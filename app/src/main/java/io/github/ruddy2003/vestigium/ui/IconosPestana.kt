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
    fun rayos(cx: Float, cy: Float, n: Int, r1: Float, r2: Float, desde: Double = 0.0, hasta: Double = 360.0, w: Float = 1.7f) {
        for (i in 0 until n) {
            val a = Math.toRadians(desde + (hasta - desde) * i / (if (hasta - desde >= 360.0) n else n - 1)).toFloat()
            l(cx + r1 * cos(a), cy + r1 * sin(a), cx + r2 * cos(a), cy + r2 * sin(a), w)
        }
    }

    fun estrella(cx: Float, cy: Float, r1: Float, r2: Float, relleno: Boolean = false) = p(relleno) {
        for (i in 0 until 10) { val rr = if (i % 2 == 0) r1 else r2; val a = Math.toRadians(-90.0 + 36.0 * i).toFloat(); if (i == 0) m(cx + rr * cos(a), cy + rr * sin(a)) else t(cx + rr * cos(a), cy + rr * sin(a)) }
        close()
    }
    fun engrane(cx: Float, cy: Float, ro: Float, ri: Float, n: Int) = p {
        for (i in 0 until n * 4) { val rr = if ((i / 2) % 2 == 0) ro else ri; val a = Math.toRadians(360.0 * i / (n * 4) + 360.0 / (n * 8)).toFloat()
            if (i == 0) m(cx + rr * cos(a), cy + rr * sin(a)) else t(cx + rr * cos(a), cy + rr * sin(a)) }
        close()
    }

    /** Una hoja (de laurel, de olivo, un grano de trigo): un huso de [largo] × [ancho] girado [grados] (0 = hacia la derecha). */
    fun hoja(cx: Float, cy: Float, largo: Float, ancho: Float, grados: Float, relleno: Boolean = true) {
        val a = Math.toRadians(grados.toDouble())
        val dx = (cos(a) * largo / 2).toFloat(); val dy = (sin(a) * largo / 2).toFloat()
        val nx = (-sin(a) * ancho).toFloat(); val ny = (cos(a) * ancho).toFloat()
        p(relleno, fino) { m(cx - dx, cy - dy); q(cx + nx, cy + ny, cx + dx, cy + dy); q(cx - nx, cy - ny, cx - dx, cy - dy); close() }
    }
    fun ov(x: Float, y: Float, w: Float, h: Float, relleno: Boolean = false, st: Stroke = grueso) =
        s.drawOval(c, o(x, y), Size(w * u, h * u), style = if (relleno) Fill else st)
    fun arco(x: Float, y: Float, w: Float, h: Float, desde: Float, barrido: Float, st: Stroke = grueso) =
        s.drawArc(c, desde, barrido, false, o(x, y), Size(w * u, h * u), style = st)
    /** Un punto a [radio] y [grados] del centro ([cx], [cy]). */
    fun px(cx: Float, radio: Float, grados: Double) = cx + radio * cos(Math.toRadians(grados)).toFloat()
    fun py(cy: Float, radio: Float, grados: Double) = cy + radio * sin(Math.toRadians(grados)).toFloat()
    /** La cruz patada del Temple, rellena, de radio [rad]. */
    fun patada(cx: Float, cy: Float, rad: Float) = s.drawPath(cruzPatada(o(cx, cy), rad * u), c)

    /**
     * Día es «el día» (sol, terminal, ojo…); Diario es «escribir» (tablilla, códice, expediente…); Registrar es el botón del
     * centro (un «+» con la mano del tema); Vida es tu vida vista de lejos; Más es todo lo demás; Ajustes, el botón de arriba.
     */
    fun dibujar(id: String, tm: TemaId) = when (id) {
        "dia" -> dia(tm)
        "diario" -> diario(tm)
        "momentos" -> momentos(tm)
        "entender", "explorar", "analisis" -> explorar(tm)
        "registrar" -> registrar(tm)
        "ajustes" -> ajustes(tm)
        "vida" -> vida(tm)
        "mas" -> mas(tm)
        else -> mas(tm)
    }

    // ------------------------------------------------------------ Día
    private fun dia(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { ci(12f, 12f, 4f); rayos(12f, 12f, 8, 6.6f, 9.6f) }                                                              // sol
        TemaId.CIBER -> { r(2.5f, 4f, 19f, 16f, 1f); p { m(6.5f, 9f); t(9.5f, 12f); t(6.5f, 15f) }; l(11.5f, 15f, 16.5f, 15f) }              // terminal
        TemaId.NEON -> { p { m(2f, 21f); t(2f, 14f); t(6f, 14f); t(6f, 10f); t(10f, 10f); t(10f, 16f); t(14f, 16f); t(14f, 7f); t(18f, 7f); t(18f, 12f); t(22f, 12f); t(22f, 21f) }
            arco(3f, 2f, 6f, 6f, 110f, 240f, fino) }                                                                                            // la noche sobre los edificios
        TemaId.HELENO -> { p { m(2.5f, 8.5f); t(21.5f, 8.5f); q(21.5f, 19.5f, 12f, 19.5f); q(2.5f, 19.5f, 2.5f, 8.5f); close() }
            rayos(12f, 8.5f, 5, 2.6f, 8f, 30.0, 150.0, 1.1f); p(relleno = true) { m(12f, 8.5f); t(12f, 3.2f); t(16.5f, 8.5f); close() }
            l(12f, 19.5f, 12f, 21.5f); l(9f, 21.5f, 15f, 21.5f) }                                                                         // reloj de sol: cuenco, horas y gnomon
        TemaId.EGIPTO -> { ci(12f, 11f, 3.8f, relleno = true); p { m(2f, 19f); q(5.5f, 10f, 9f, 19f) }; p { m(15f, 19f); q(18.5f, 10f, 22f, 19f) }
            l(2f, 19.5f, 22f, 19.5f) }                                                                                                       // el horizonte (ajet): el sol entre dos montes
        TemaId.MEDIEVAL -> { r(9f, 10f, 6f, 10.5f, 0.8f); l(12f, 10f, 12f, 8.6f, 1.1f); l(6.5f, 21f, 17.5f, 21f)
            p(relleno = true) { m(12f, 2.8f); q(14.6f, 5.8f, 12f, 8f); q(9.4f, 5.8f, 12f, 2.8f); close() } }                               // vela
        TemaId.TEMPLARIO -> { r(7f, 9f, 10f, 12.5f, 0.4f); r(6.5f, 6f, 2.6f, 3.2f, 0.3f, true); r(10.7f, 6f, 2.6f, 3.2f, 0.3f, true); r(14.9f, 6f, 2.6f, 3.2f, 0.3f, true)
            p(st = fino) { m(10.5f, 21.5f); t(10.5f, 17.5f); q(12f, 15.5f, 13.5f, 17.5f); t(13.5f, 21.5f) }
            l(12f, 6f, 12f, 1.6f, 1.1f); p(relleno = true) { m(12f, 1.6f); t(16.5f, 2.8f); t(12f, 4.1f); close() } }                           // torre del homenaje con su pendón
        TemaId.DISTOPIA -> { p { m(2.5f, 12f); q(12f, 3f, 21.5f, 12f); q(12f, 21f, 2.5f, 12f); close() }; ci(12f, 12f, 3.3f, relleno = true) } // ojo
        TemaId.REFUGIO -> { r(3f, 3.5f, 18f, 13.5f, 3f); l(12f, 17f, 12f, 20f); l(8f, 20.5f, 16f, 20.5f); p(st = fino) { m(6f, 10.5f); t(9f, 10.5f); t(10.5f, 7.5f); t(12.5f, 13.5f); t(14f, 10.5f); t(18f, 10.5f) } } // monitor
        TemaId.ORDEN -> { p { m(12f, 3f); t(19f, 5.5f); t(19f, 11f); q(19f, 17.5f, 12f, 21f); q(5f, 17.5f, 5f, 11f); t(5f, 5.5f); close() }; l(12f, 7f, 12f, 17f); l(8.5f, 10.5f, 15.5f, 10.5f) } // escudo
        TemaId.LABORATORIO -> { p { m(9.5f, 3f); t(9.5f, 9f); t(4.5f, 19f); q(4f, 21f, 6f, 21f); t(18f, 21f); q(20f, 21f, 19.5f, 19f); t(14.5f, 9f); t(14.5f, 3f) }; l(8f, 3f, 16f, 3f); l(7.2f, 15f, 16.8f, 15f, 1.1f) } // matraz
        TemaId.COLONOS -> { p { m(6.5f, 17f); q(6.5f, 11.5f, 12f, 11.5f); q(17.5f, 11.5f, 17.5f, 17f) }; l(3f, 17f, 21f, 17f); rayos(12f, 17f, 5, 7.5f, 10f, -160.0, -20.0); l(7f, 20.5f, 17f, 20.5f) } // amanecer
        TemaId.NAPOLEON -> { p { m(2.5f, 15.5f); q(12f, 2.5f, 21.5f, 15.5f); q(12f, 11.5f, 2.5f, 15.5f); close() }; ci(12f, 9.8f, 1.6f, st = fino); l(12f, 8.2f, 12f, 4.5f, 1.1f) } // bicornio
        TemaId.BOTANICO -> { p { m(5f, 19f); q(5f, 5f, 19f, 5f); q(19f, 19f, 5f, 19f); close() }; l(4f, 20f, 15.5f, 8.5f, 1.2f)
            l(8.5f, 15.5f, 8.5f, 11.5f, 1.1f); l(11.5f, 12.5f, 11.5f, 8.5f, 1.1f); l(8.5f, 15.5f, 12.5f, 15.5f, 1.1f); l(11.5f, 12.5f, 15.5f, 12.5f, 1.1f) } // hoja con nervios
        TemaId.GOTICO -> { p { m(6f, 21f); t(6f, 10f); q(6f, 3f, 12f, 2.5f); q(18f, 3f, 18f, 10f); t(18f, 21f); close() }; l(12f, 9.5f, 12f, 21f, 1.1f)
            ci(12f, 7.3f, 1.8f, st = fino); l(6f, 14.5f, 18f, 14.5f, 1.1f) }                                                                  // ventana de lanceta
        TemaId.ESPACIO -> { ci(12f, 12f, 5.4f); p { m(2.5f, 15f); q(4f, 10f, 12f, 9.6f); q(21f, 9.3f, 21.5f, 9f) }; p(st = fino) { m(2.5f, 15f); q(12f, 16.5f, 21.5f, 9f) }
            ci(19f, 4f, 0.9f, relleno = true); ci(4.5f, 5f, 0.6f, relleno = true) }                                                           // planeta con anillo
        TemaId.ABISMO -> { p { m(5f, 12f); q(5f, 3.5f, 12f, 3.5f); q(19f, 3.5f, 19f, 12f); q(15.5f, 10.6f, 12f, 12f); q(8.5f, 10.6f, 5f, 12f); close() }
            p(st = fino) { m(8f, 12f); q(6.5f, 16f, 8.5f, 20.5f) }; p(st = fino) { m(12f, 12.2f); q(13.6f, 16.5f, 12f, 21f) }; p(st = fino) { m(16f, 12f); q(17.5f, 16f, 15.5f, 20.5f) } } // medusa
        TemaId.ANDALUS -> { p { m(7f, 21f); t(7f, 15f); q(4f, 12f, 5.5f, 8f); q(8f, 3f, 12f, 3f); q(16f, 3f, 18.5f, 8f); q(20f, 12f, 17f, 15f); t(17f, 21f) }
            l(4f, 21f, 20f, 21f); l(12f, 3f, 12f, 5.5f, 1.1f); l(7.2f, 6f, 8.9f, 7.6f, 1.1f); l(16.8f, 6f, 15.1f, 7.6f, 1.1f) }                // arco de herradura
        TemaId.MONGOL -> { p { m(3f, 20.5f); t(3f, 13f); q(12f, 5f, 21f, 13f); t(21f, 20.5f); close() }; r(10f, 15f, 4f, 5.5f, 0.3f); ci(12f, 8.6f, 1.3f, st = fino)
            l(3f, 13f, 21f, 13f, 1.1f) }                                                                                                      // yurta
        TemaId.RENACIMIENTO -> { l(12f, 4f, 6.5f, 21f); l(12f, 4f, 17.5f, 21f); ci(12f, 3.2f, 1.4f); p(st = fino) { m(5f, 16f); q(12f, 21f, 19f, 16f) } } // compás
        TemaId.NAVIDAD -> { estrella(12f, 12.5f, 9.5f, 4f); ci(12f, 12.5f, 1.2f, relleno = true) }                                        // estrella
        TemaId.SEDA -> { p { m(2f, 18f); q(7f, 12.5f, 12f, 17f); q(17f, 12f, 22f, 16f) }; ci(15.5f, 7.5f, 3f); l(2f, 21f, 22f, 21f, 1.1f) } // sol sobre las dunas
        TemaId.URSS -> { l(2.5f, 21f, 21.5f, 21f); p { m(4f, 21f); t(4f, 13f); t(8.5f, 16f); t(8.5f, 13f); t(13f, 16f); t(13f, 21f) }; r(15.5f, 6f, 3.4f, 15f, 0.3f)
            ci(17.2f, 3.2f, 1.3f, st = fino); ci(20f, 2.4f, 0.9f, st = fino) }                                                               // fábrica
        TemaId.REVOLUCION -> { p { m(5f, 20f); q(4f, 10f, 10f, 5f); q(15f, 2f, 19f, 6f); q(20f, 9f, 17.5f, 10f); q(19f, 14f, 19f, 20f); close() }; l(4.5f, 20.5f, 19.5f, 20.5f); ci(9f, 15.5f, 1.8f, st = fino) } // gorro frigio
        TemaId.ANTIGUAS -> { p { m(5f, 21f); t(5f, 10f); q(5f, 3f, 12f, 3f); q(19f, 3f, 19f, 10f); t(19f, 21f) }; l(3f, 21f, 21f, 21f); l(9f, 9f, 9f, 21f, 1.1f); l(15f, 9f, 15f, 21f, 1.1f)
            ci(13.6f, 15f, 0.9f, relleno = true) }                                                                                           // puerta del salón
    } }

    // ------------------------------------------------------------ Diario
    private fun diario(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { r(4f, 3f, 16f, 18f, 1.5f); l(7.5f, 8f, 16.5f, 8f); l(7.5f, 12f, 16.5f, 12f); l(7.5f, 16f, 13f, 16f) }          // tablilla de cera
        TemaId.CIBER -> { r(4.5f, 2.5f, 15f, 19f, 0.4f); for (y in listOf(5f, 9f, 13f, 17f)) { ci(6.6f, y, 0.7f, relleno = true); ci(17.4f, y, 0.7f, relleno = true) }
            l(9f, 6.5f, 15f, 6.5f, 1.1f); l(9f, 10f, 14f, 10f, 1.1f); l(9f, 13.5f, 15f, 13.5f, 1.1f)
            p(st = fino) { m(9f, 16f); t(10.6f, 17.2f); t(9f, 18.4f) }; l(11.8f, 18.4f, 14f, 18.4f, 1.1f) }                                // papel continuo de teletipo con su «>_»
        TemaId.NEON -> { p { m(6f, 2.5f); t(18f, 2.5f); t(18f, 20f); t(16f, 21.5f); t(14f, 20f); t(12f, 21.5f); t(10f, 20f); t(8f, 21.5f); t(6f, 20f); close() }   // el ticket
            l(9f, 7f, 15f, 7f, 1.1f); l(9f, 10.5f, 13f, 10.5f, 1.1f); l(9f, 14f, 15f, 14f, 1.1f) }
        TemaId.HELENO -> momentos(tm)
        TemaId.EGIPTO -> { ov(3f, 4f, 4f, 16f); l(5f, 4f, 20.5f, 4f); l(5f, 20f, 20.5f, 20f); l(20.5f, 4f, 20.5f, 20f)
            l(10f, 7f, 10f, 17f, 1.1f); l(13.5f, 7f, 13.5f, 17f, 1.1f); l(17f, 7f, 17f, 13.5f, 1.1f) }                                      // papiro a medio desenrollar, escrito en columnas
        TemaId.MEDIEVAL -> { p { m(12f, 6.5f); q(8f, 4f, 3f, 5f); t(3f, 18.5f); q(8f, 17.5f, 12f, 19.5f); q(16f, 17.5f, 21f, 18.5f); t(21f, 5f); q(16f, 4f, 12f, 6.5f) }; l(12f, 6.5f, 12f, 19.5f, 1.1f) } // códice abierto
        TemaId.TEMPLARIO -> { r(5f, 3f, 13.5f, 18f, 1f); patada(11.8f, 11.5f, 4.6f); l(18.5f, 8f, 20.5f, 8f, 1.4f); l(18.5f, 15f, 20.5f, 15f, 1.4f); l(7f, 3f, 7f, 21f, 1.1f) } // la regla con su cruz y sus correas
        TemaId.DISTOPIA -> { r(5f, 4f, 14f, 17.5f, 0.6f); r(8.5f, 2.5f, 7f, 3.2f, 0.4f, relleno = true); l(8f, 10f, 16f, 10f); l(8f, 13.5f, 16f, 13.5f); l(8f, 17f, 12.5f, 17f) } // expediente
        TemaId.REFUGIO -> momentos(tm)
        TemaId.ORDEN -> momentos(tm)
        TemaId.LABORATORIO -> momentos(tm)
        TemaId.COLONOS -> momentos(tm)
        TemaId.NAPOLEON -> momentos(tm)
        TemaId.BOTANICO -> momentos(tm)
        TemaId.NAVIDAD -> momentos(tm)
        TemaId.SEDA -> momentos(tm)
        TemaId.GOTICO -> momentos(tm)
        TemaId.ESPACIO -> momentos(tm)
        TemaId.ABISMO -> { r(3.5f, 3f, 13f, 18f, 2.2f); l(6.5f, 8f, 13.5f, 8f, 1.1f); l(6.5f, 12f, 13.5f, 12f, 1.1f); l(6.5f, 16f, 11f, 16f, 1.1f)
            l(19.5f, 3.5f, 19.5f, 16f, 2f); p(relleno = true) { m(18.4f, 16f); t(20.6f, 16f); t(19.5f, 19.5f); close() } }                   // pizarra de buceo y lápiz
        TemaId.ANDALUS -> momentos(tm)
        TemaId.MONGOL -> momentos(tm)
        TemaId.RENACIMIENTO -> momentos(tm)
        TemaId.URSS -> momentos(tm)
        TemaId.REVOLUCION -> momentos(tm)
        TemaId.ANTIGUAS -> momentos(tm)
    } }

    // ------------------------------------------------------------ Momentos
    private fun momentos(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { p { m(3f, 15f); q(3f, 11f, 9f, 11f); t(16f, 11f); q(20f, 11f, 21f, 14f); t(17f, 15f); q(14f, 18f, 9f, 18f); q(3f, 18f, 3f, 15f); close() }
            p(relleno = true) { m(19.8f, 10f); q(22f, 7f, 20.3f, 4.5f); q(18.4f, 7.5f, 19.8f, 10f); close() } }                              // lucerna
        TemaId.CIBER -> p { m(13f, 2.5f); t(5f, 13.5f); t(11f, 13.5f); t(10f, 21.5f); t(19f, 10f); t(13f, 10f); close() }                        // rayo
        TemaId.NEON -> { r(2.5f, 4f, 19f, 12.5f, 6f); p { m(7f, 16.5f); t(6f, 21f); t(11f, 16.5f) }; ci(8f, 10.2f, 1f, relleno = true); ci(12f, 10.2f, 1f, relleno = true); ci(16f, 10.2f, 1f, relleno = true) } // el chisme
        TemaId.HELENO -> { p { m(7.5f, 4f); q(4f, 12f, 8f, 19f) }; p { m(16.5f, 4f); q(20f, 12f, 16f, 19f) }; l(6f, 6.5f, 18f, 6.5f); l(8f, 19f, 16f, 19f)
            l(10f, 6.5f, 10f, 19f, 1.1f); l(12f, 6.5f, 12f, 19f, 1.1f); l(14f, 6.5f, 14f, 19f, 1.1f) }                                         // lira
        TemaId.EGIPTO -> { p { m(8f, 14f); t(8f, 7f); q(8f, 2.5f, 12f, 2.5f); q(16f, 2.5f, 16f, 7f); t(16f, 14f) }; l(8f, 14f, 16f, 14f)
            l(6.5f, 7f, 17.5f, 7f, 1.1f); l(6.5f, 10.5f, 17.5f, 10.5f, 1.1f); l(12f, 14f, 12f, 21.5f) }                                    // sistro, la sonaja de Hathor
        TemaId.MEDIEVAL -> { p { m(20.5f, 3f); q(10f, 5f, 6.5f, 16f); t(8.5f, 17f); q(17.5f, 13f, 20.5f, 3f); close() }; l(6.5f, 16f, 4f, 21f); l(18f, 5.5f, 9.5f, 13.5f, 1.1f) } // pluma
        TemaId.TEMPLARIO -> { p(relleno = true) { m(12f, 1.8f); t(13.4f, 4f); t(13.1f, 15.5f); t(10.9f, 15.5f); t(10.6f, 4f); close() }; l(7f, 16.2f, 17f, 16.2f, 2f)
            l(12f, 16.2f, 12f, 20f, 1.8f); ci(12f, 21.2f, 1.3f, relleno = true) }                                                               // la espada en alto
        TemaId.DISTOPIA -> { p { m(2.5f, 12f); q(12f, 3f, 21.5f, 12f); q(12f, 21f, 2.5f, 12f); close() }; ci(12f, 12f, 3.3f, relleno = true) }  // ojo
        TemaId.REFUGIO -> { ci(12f, 16f, 1.8f, relleno = true); p { m(8f, 13f); q(12f, 9f, 16f, 13f) }; p { m(5f, 10f); q(12f, 3.5f, 19f, 10f) }; l(12f, 17.5f, 12f, 21f) } // radio
        TemaId.ORDEN -> { l(6f, 3f, 6f, 21f); p { m(6f, 4f); t(19f, 7.5f); t(6f, 11f) }; l(6f, 7.5f, 13f, 7.5f, 1.1f) }                        // banderín
        TemaId.LABORATORIO -> { p { m(12f, 3f); q(18f, 11f, 18f, 14.5f); q(18f, 20.5f, 12f, 20.5f); q(6f, 20.5f, 6f, 14.5f); q(6f, 11f, 12f, 3f); close() }; p(st = fino) { m(9f, 14.5f); q(9f, 17.5f, 12f, 17.5f) } } // gota
        TemaId.COLONOS -> { ci(12f, 3.6f, 1.4f, st = fino); p { m(8f, 7f); t(16f, 7f); t(14f, 5f); t(10f, 5f); close() }; r(7.5f, 7f, 9f, 11.5f, 0.8f)
            l(6.5f, 19.8f, 17.5f, 19.8f); p(relleno = true) { m(12f, 9.5f); q(14.3f, 12.5f, 12f, 15f); q(9.7f, 12.5f, 12f, 9.5f); close() } }   // farol
        TemaId.NAPOLEON -> { p { m(5f, 15f); t(5f, 20.5f); t(13f, 20.5f); t(13f, 15f); q(13f, 13.5f, 11.5f, 13.5f); t(6.5f, 13.5f); q(5f, 13.5f, 5f, 15f); close() }
            p { m(9f, 14f); q(13f, 6f, 21f, 2.5f); q(18f, 9f, 9f, 14f); close() } }                                                       // tintero y pluma
        TemaId.BOTANICO -> { r(5f, 3f, 14f, 18f, 0.8f); l(8f, 3f, 8f, 21f, 1.1f); p(relleno = true) { m(10.5f, 16.5f); q(10.5f, 8f, 16.5f, 7f); q(17f, 15f, 10.5f, 16.5f); close() } } // cuaderno con hoja prensada
        TemaId.GOTICO -> { ci(12f, 12f, 8.6f); ci(12f, 12f, 2.6f); rayos(12f, 12f, 8, 2.6f, 8.6f); for (i in 0 until 8) { val a = Math.toRadians(22.5 + 45.0 * i).toFloat(); ci(12f + 6.2f * cos(a), 12f + 6.2f * sin(a), 0.7f, relleno = true) } } // rosetón
        TemaId.ESPACIO -> { p { m(12f, 2.5f); q(17f, 7f, 15.5f, 15.5f); t(8.5f, 15.5f); q(7f, 7f, 12f, 2.5f); close() }; ci(12f, 9f, 1.8f, st = fino)
            p { m(8.5f, 12.5f); t(5.5f, 17f); t(8.8f, 15.5f) }; p { m(15.5f, 12.5f); t(18.5f, 17f); t(15.2f, 15.5f) }
            p(relleno = true) { m(10f, 16.5f); q(12f, 22.5f, 14f, 16.5f); close() } }                                                      // cohete
        TemaId.ABISMO -> { r(2.5f, 5f, 19f, 6f, 3f); r(10f, 2.5f, 4f, 2.5f, 0.6f); l(12f, 11f, 12f, 12.8f, 1.1f); ci(12f, 16.6f, 3.7f)
            ci(11.1f, 16.1f, 1.1f, relleno = true); l(5f, 11f, 4f, 13.5f, 1.1f); l(19f, 11f, 20f, 13.5f, 1.1f) }                             // batiscafo: flotador y esfera
        TemaId.ANDALUS -> { l(5f, 21f, 19f, 13f); l(5f, 13f, 19f, 21f); p { m(4f, 12.5f); q(8f, 9.5f, 12f, 12.5f); q(16f, 9.5f, 20f, 12.5f); t(20f, 5.5f); q(16f, 2.5f, 12f, 5.5f); q(8f, 2.5f, 4f, 5.5f); close() }
            l(12f, 5.5f, 12f, 12.5f, 1.1f) }                                                                                                  // libro sobre su atril
        TemaId.MONGOL -> { p { m(7f, 3f); q(17f, 12f, 7f, 21f) }; l(7f, 3f, 7f, 21f, 1f); l(3f, 12f, 20f, 12f, 1.3f); p(relleno = true) { m(21.5f, 12f); t(18.5f, 10.3f); t(18.5f, 13.7f); close() }
            l(3f, 12f, 4.5f, 10.5f, 1f); l(3f, 12f, 4.5f, 13.5f, 1f) }                                                                       // arco y flecha
        TemaId.RENACIMIENTO -> { r(4f, 4f, 12f, 16f, 0.6f); l(6.5f, 9f, 13f, 9f, 1.1f); l(6.5f, 12f, 13f, 12f, 1.1f); l(6.5f, 15f, 11f, 15f, 1.1f)
            p(relleno = true) { m(21f, 2.5f); q(15f, 6f, 13.5f, 15f); t(15f, 15.5f); q(19f, 9f, 21f, 2.5f); close() } }                     // cuaderno y pluma
        TemaId.NAVIDAD -> { r(3f, 6f, 18f, 13f, 1f); p(st = fino) { m(3f, 6.5f); t(12f, 13f); t(21f, 6.5f) }; r(15.5f, 8.5f, 3.5f, 4f, 0.3f, relleno = true) } // carta con sello
        TemaId.SEDA -> { l(4.5f, 3.5f, 19.5f, 3.5f, 2.4f); r(6f, 3.5f, 12f, 16f, 0.3f); l(4.5f, 20f, 19.5f, 20f, 2.4f); l(10f, 7f, 10f, 16.5f, 1.1f); l(14f, 7f, 14f, 13f, 1.1f) } // rollo colgante
        TemaId.URSS -> { r(5f, 3f, 14f, 18f, 0.5f); estrella(12f, 8.5f, 3.4f, 1.4f, relleno = true); l(8f, 14f, 16f, 14f, 1.1f); l(8f, 17f, 14f, 17f, 1.1f) } // informe
        TemaId.REVOLUCION -> { r(5f, 3f, 14f, 18f, 0.6f); l(12f, 3f, 12f, 1.2f, 1.1f); l(8f, 8f, 16f, 8f, 1.1f); l(8f, 11.5f, 16f, 11.5f, 1.1f); l(8f, 15f, 13f, 15f, 1.1f) } // cuaderno de quejas
        TemaId.ANTIGUAS -> { r(5f, 3f, 14f, 18f, 0.6f); l(8.5f, 3f, 8.5f, 21f, 1.1f); r(17.5f, 10f, 3.5f, 4f, 0.3f, relleno = true); p(st = fino) { m(13.5f, 7.5f); t(15.5f, 10.5f); t(13.5f, 14.5f); t(11.5f, 10.5f); close() } } // libro con broche
    } }

    // ------------------------------------------------------------ Explorar
    private fun explorar(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { p { m(8f, 21f); t(8f, 7f); q(8f, 3f, 12f, 3f); q(16f, 3f, 16f, 7f); t(16f, 21f) }; l(5.5f, 21f, 18.5f, 21f); l(10f, 9f, 14f, 9f, 1.1f); l(10f, 12f, 14f, 12f, 1.1f); l(10f, 15f, 14f, 15f, 1.1f) } // miliario
        TemaId.CIBER -> { ci(12f, 12f, 9f); ci(12f, 12f, 4.8f, st = fino); l(12f, 12f, 18.4f, 5.6f); ci(15.5f, 9f, 1.2f, relleno = true) }           // radar
        TemaId.NEON -> { r(8f, 2.5f, 8f, 19f, 1f); r(10f, 5f, 4f, 3f, 0.3f, true); r(10f, 10.5f, 4f, 3f, 0.3f, true); r(10f, 16f, 4f, 3f, 0.3f, true); l(5f, 2.5f, 8f, 4f, 1.1f) } // el letrero vertical
        TemaId.HELENO -> { ci(12f, 12f, 8.6f); p(relleno = true) { m(12f, 5f); t(13.8f, 12f); t(10.2f, 12f); close() }; p(st = fino) { m(10.2f, 12f); t(12f, 19f); t(13.8f, 12f) } } // brújula
        TemaId.EGIPTO -> { p { m(3f, 11f); q(12f, 4f, 21f, 11f); q(12f, 15.5f, 3f, 11f); close() }; ci(12f, 10.5f, 2.2f, relleno = true)
            p { m(3f, 6.5f); q(12f, 2f, 21.5f, 6f) }; l(10.5f, 13.5f, 9f, 20f); p { m(13f, 13.5f); q(17f, 20f, 19.5f, 16.5f) } }        // el ojo de Horus, el que todo lo ve
        TemaId.MEDIEVAL -> { ci(12f, 12f, 8.6f); l(3.6f, 12f, 20.4f, 12f); l(12f, 12f, 12f, 20.4f) }                                              // mapamundi en T-O
        TemaId.TEMPLARIO -> { p { m(3.5f, 10f); q(12f, 10.5f, 19.5f, 4f); t(21f, 9.5f); q(13f, 16.5f, 4.5f, 14f); close() }
            l(8f, 10.4f, 8.4f, 14.6f, 1.1f); l(15.4f, 7.6f, 16.6f, 12.6f, 1.1f); p(st = fino) { m(8.2f, 14.6f); q(12f, 20.5f, 16f, 12.6f) } }      // el olifante de la descubierta
        TemaId.DISTOPIA -> { ci(7f, 15f, 4f); ci(17f, 15f, 4f); l(4.5f, 11.8f, 6.5f, 5.5f); l(19.5f, 11.8f, 17.5f, 5.5f); l(6.5f, 5.5f, 10f, 5.5f); l(14f, 5.5f, 17.5f, 5.5f); l(10.5f, 9f, 13.5f, 9f) } // binoculares
        TemaId.REFUGIO -> { p { m(12f, 21f); q(5f, 13f, 5f, 9.5f); q(5f, 3f, 12f, 3f); q(19f, 3f, 19f, 9.5f); q(19f, 13f, 12f, 21f); close() }; ci(12f, 9.5f, 2.6f) } // marcador de mapa
        TemaId.ORDEN -> { ci(12f, 12f, 7.5f); l(12f, 2f, 12f, 7f); l(12f, 17f, 12f, 22f); l(2f, 12f, 7f, 12f); l(17f, 12f, 22f, 12f); ci(12f, 12f, 1.2f, relleno = true) } // retícula
        TemaId.LABORATORIO -> { p { m(4f, 4f); t(4f, 20f); t(20f, 20f) }; p(st = fino) { m(6.5f, 16f); t(10f, 11f); t(13f, 13.5f); t(19f, 6f) }
            ci(10f, 11f, 1.3f, relleno = true); ci(13f, 13.5f, 1.3f, relleno = true); ci(19f, 6f, 1.3f, relleno = true) }                       // gráfica
        TemaId.COLONOS -> { l(4f, 19f, 9.5f, 13.5f, 3.4f); l(9.5f, 13.5f, 14.5f, 8.5f, 2.6f); l(14.5f, 8.5f, 19f, 4f, 1.9f); l(7f, 21f, 9.5f, 18.5f, 1.1f) } // catalejo
        TemaId.NAPOLEON -> { p { m(3f, 6f); t(9f, 4f); t(15f, 6f); t(21f, 4f); t(21f, 18f); t(15f, 20f); t(9f, 18f); t(3f, 20f); close() }; l(9f, 4f, 9f, 18f, 1.1f); l(15f, 6f, 15f, 20f, 1.1f)
            ci(17.8f, 10.5f, 1.4f, relleno = true) }                                                                                          // mapa de campaña
        TemaId.BOTANICO -> { ci(10f, 10f, 6.5f); l(14.8f, 14.8f, 20.5f, 20.5f, 2.4f); s.drawOval(c, o(7.4f, 8.2f), Size(5.2f * u, 3.6f * u))
            l(10f, 8.2f, 10f, 11.8f, 0.9f); l(7.4f, 10f, 12.6f, 10f, 0.9f) }                                                                // lupa sobre un escarabajo
        TemaId.GOTICO -> { p { m(12f, 3f); q(15f, 7f, 12.8f, 12f); t(11.2f, 12f); q(9f, 7f, 12f, 3f); close() }; p { m(11f, 12f); q(4.5f, 12.5f, 4.5f, 7.5f); q(8f, 7.5f, 10f, 11f) }
            p { m(13f, 12f); q(19.5f, 12.5f, 19.5f, 7.5f); q(16f, 7.5f, 14f, 11f) }; l(7f, 13.5f, 17f, 13.5f); p { m(12f, 13.5f); q(12f, 19f, 8.5f, 20.5f) }; p { m(12f, 13.5f); q(12f, 19f, 15.5f, 20.5f) } } // flor de lis
        TemaId.ESPACIO -> { l(4f, 14f, 17f, 6.5f, 3.2f); l(17.5f, 6.2f, 20f, 4.8f, 2f); l(10.5f, 10.5f, 7f, 21f); l(10.5f, 10.5f, 14f, 21f); l(10.5f, 10.5f, 10.5f, 21f, 1.1f) } // telescopio
        TemaId.ABISMO -> { ci(12f, 12f, 8.5f); ci(12f, 12f, 4.5f, st = fino); l(12f, 12f, 18f, 6f, 1.4f); ci(7.8f, 15f, 1f, relleno = true); ci(15.6f, 14.6f, 0.9f, relleno = true)
            ci(12f, 12f, 0.9f, relleno = true) }                                                                                              // pantalla del sonar
        TemaId.ANDALUS -> { l(12f, 2f, 12f, 4.5f, 1.1f); p { m(8f, 8f); q(12f, 3.5f, 16f, 8f); close() }; p { m(8f, 8f); t(16f, 8f); t(17.5f, 15f); t(12f, 21f); t(6.5f, 15f); close() }
            l(8f, 8f, 12f, 21f, 0.9f); l(16f, 8f, 12f, 21f, 0.9f); l(6.5f, 15f, 17.5f, 15f, 0.9f) }                                          // lámpara nazarí
        TemaId.MONGOL -> { ci(12f, 14.5f, 3.6f); p { m(7f, 8f); q(12f, 12.5f, 17f, 8f); q(12f, 10f, 7f, 8f); close() }; p(relleno = true) { m(12f, 2f); q(14.5f, 4.5f, 12f, 6.5f); q(9.5f, 4.5f, 12f, 2f); close() }
            l(6f, 20.5f, 18f, 20.5f) }                                                                                                        // sol, luna y fuego
        TemaId.RENACIMIENTO -> { p { m(2.5f, 13f); q(7f, 4f, 12f, 10f); q(17f, 4f, 21.5f, 13f); q(18f, 11f, 16.5f, 13.5f); q(14f, 11f, 12f, 13.5f); q(10f, 11f, 7.5f, 13.5f); q(6f, 11f, 2.5f, 13f); close() }
            l(12f, 10f, 12f, 19f, 1.1f); l(7f, 7.5f, 7.5f, 13.3f, 0.9f); l(17f, 7.5f, 16.5f, 13.3f, 0.9f) }                                 // ala de la máquina voladora
        TemaId.NAVIDAD -> { p { m(12f, 2.5f); t(18f, 9.5f); t(15f, 9.5f); t(19.5f, 16f); t(4.5f, 16f); t(9f, 9.5f); t(6f, 9.5f); close() }; r(10.5f, 16f, 3f, 4.5f, 0.3f) } // árbol
        TemaId.SEDA -> { ci(12f, 13f, 7.8f); ci(12f, 3.8f, 1.3f, st = fino); l(12f, 5.1f, 12f, 5.2f); ci(12f, 13f, 4.2f, st = fino); l(5.5f, 17.5f, 18.5f, 8.5f); ci(12f, 13f, 1f, relleno = true) } // astrolabio
        TemaId.URSS -> { l(3f, 21f, 21f, 21f); r(5f, 14f, 3.4f, 7f, 0.2f, true); r(10.3f, 10f, 3.4f, 11f, 0.2f, true); r(15.6f, 5f, 3.4f, 16f, 0.2f, true) } // estadística
        TemaId.REVOLUCION -> { l(12f, 4f, 12f, 20f); l(4f, 7f, 20f, 7f); l(8f, 20.5f, 16f, 20.5f); p(st = fino) { m(4f, 7f); t(2f, 13f); q(4f, 15.5f, 6f, 13f); t(4f, 7f) }; p(st = fino) { m(20f, 7f); t(18f, 13f); q(20f, 15.5f, 22f, 13f); t(20f, 7f) } } // balanza
        TemaId.ANTIGUAS -> { r(3f, 5f, 18f, 14f, 0.4f); p(st = fino) { m(5.5f, 16f); q(9f, 9f, 12.5f, 13f); q(16f, 16.5f, 18f, 8.5f) }; l(7f, 9f, 9f, 7f, 1.1f); l(9f, 9f, 11f, 7f, 1.1f); ci(18f, 8.5f, 1.3f, relleno = true) } // mapa con el camino
    } }

    // ------------------------------------------------------------ Más
    private fun mas(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { r(6f, 5f, 12f, 14f, 0.5f); l(4.5f, 4f, 19.5f, 4f, 2.4f); l(4.5f, 20f, 19.5f, 20f, 2.4f); l(8.5f, 9f, 15.5f, 9f, 1.1f); l(8.5f, 12f, 15.5f, 12f, 1.1f); l(8.5f, 15f, 13f, 15f, 1.1f) } // rollo
        TemaId.CIBER -> { r(4f, 4f, 6.5f, 6.5f, 0.6f); r(13.5f, 4f, 6.5f, 6.5f, 0.6f); r(4f, 13.5f, 6.5f, 6.5f, 0.6f); r(13.5f, 13.5f, 6.5f, 6.5f, 0.6f, true) } // módulos
        TemaId.NEON -> { p { m(3.5f, 11f); t(20.5f, 11f); q(19.5f, 19.5f, 12f, 19.5f); q(4.5f, 19.5f, 3.5f, 11f); close() }; l(12f, 9f, 19f, 3f, 1.3f); l(14.5f, 9f, 21f, 4.5f, 1.3f) } // el tazón de fideos
        TemaId.HELENO -> { l(4f, 4f, 20f, 4f); ci(6.5f, 6.3f, 1.8f, st = fino); ci(17.5f, 6.3f, 1.8f, st = fino); l(6.5f, 8f, 17.5f, 8f, 1.1f)
            l(8f, 8f, 8f, 19f, 1.1f); l(12f, 8f, 12f, 19f, 1.1f); l(16f, 8f, 16f, 19f, 1.1f); l(6f, 19.5f, 18f, 19.5f); l(5f, 21.5f, 19f, 21.5f) } // columna jónica
        TemaId.EGIPTO -> { p { m(5.5f, 3.5f); t(18.5f, 3.5f); t(14f, 8.5f); t(10f, 8.5f); close() }; l(10f, 8.5f, 10f, 19f); l(14f, 8.5f, 14f, 19f)
            l(10f, 10.5f, 14f, 10.5f, 1.1f); l(10f, 12.5f, 14f, 12.5f, 1.1f); l(7.5f, 19.5f, 16.5f, 19.5f); l(6.5f, 21.5f, 17.5f, 21.5f) } // columna papiriforme con el capitel abierto
        TemaId.MEDIEVAL -> { ci(7.5f, 8f, 4f); l(10.3f, 10.8f, 20f, 20.5f); l(15.5f, 16f, 17.7f, 13.8f); l(18f, 18.5f, 20.2f, 16.3f) }           // llave
        TemaId.TEMPLARIO -> { p { m(6f, 20.5f); t(6f, 8f); q(6f, 3.5f, 12f, 3.5f); q(18f, 3.5f, 18f, 8f); t(18f, 20.5f); close() }
            l(7.5f, 10.5f, 16.5f, 10.5f, 1.5f); l(12f, 12.8f, 12f, 18.5f, 1.1f); l(9.5f, 15.5f, 14.5f, 15.5f, 1.1f) }                              // el yelmo de cubo
        TemaId.DISTOPIA -> { r(5f, 3f, 14f, 18f, 0.5f); l(5f, 9f, 19f, 9f, 1.1f); l(5f, 15f, 19f, 15f, 1.1f); l(10.5f, 6f, 13.5f, 6f); l(10.5f, 12f, 13.5f, 12f); l(10.5f, 18f, 13.5f, 18f) } // archivero
        TemaId.REFUGIO -> { l(6f, 18f, 14.5f, 9.5f, 2.6f); p { m(14.5f, 9.5f); q(12.5f, 5f, 16.5f, 3.5f); t(15f, 6.5f); t(17.5f, 9f); t(20.5f, 7.5f); q(19f, 11.5f, 14.5f, 9.5f) } } // llave inglesa
        TemaId.ORDEN -> { l(5.5f, 20.5f, 13.5f, 12.5f, 2.2f); p(relleno = true) { m(10.5f, 6f); t(17f, 12.5f); t(20f, 9.5f); t(13.5f, 3f); close() } } // martillo
        TemaId.LABORATORIO -> { l(3f, 6f, 21f, 6f); for (x in listOf(5f, 10.25f, 15.5f)) p { m(x, 4f); t(x, 18f); q(x, 20.5f, x + 1.75f, 20.5f); q(x + 3.5f, 20.5f, x + 3.5f, 18f); t(x + 3.5f, 4f) }
            l(5.8f, 14f, 7.7f, 14f, 1.1f); l(11f, 11f, 13f, 11f, 1.1f); l(16.3f, 16f, 18.2f, 16f, 1.1f) }                                  // tubos de ensayo
        TemaId.COLONOS -> { p { m(3.5f, 11f); t(12f, 4f); t(20.5f, 11f) }; r(6f, 10f, 12f, 10.5f, 0.4f); r(10.5f, 15f, 3f, 5.5f, 0.3f) }          // casa del cabildo
        TemaId.NAPOLEON -> { p { m(5f, 7f); q(12f, 3.5f, 19f, 7f); q(12f, 10.5f, 5f, 7f); close() }; l(5f, 7f, 5f, 17f); l(19f, 7f, 19f, 17f); p { m(5f, 17f); q(12f, 20.5f, 19f, 17f) }
            p(st = fino) { m(5f, 10.5f); t(8.5f, 16.5f); t(12f, 10.5f); t(15.5f, 16.5f); t(19f, 10.5f) } }                                // tambor
        TemaId.BOTANICO -> { p { m(6f, 13f); t(18f, 13f); t(16f, 21f); t(8f, 21f); close() }; l(5f, 13f, 19f, 13f); l(12f, 13f, 12f, 7f)
            p(relleno = true) { m(12f, 9f); q(6f, 9f, 5.5f, 3.5f); q(11f, 3.5f, 12f, 9f); close() }; p(relleno = true) { m(12f, 7.5f); q(18f, 7.5f, 18.5f, 2.5f); q(13f, 2.5f, 12f, 7.5f); close() } } // maceta
        TemaId.GOTICO -> { p { m(8f, 21f); t(8f, 9f); t(12f, 2.5f); t(16f, 9f); t(16f, 21f) }; l(5f, 21f, 19f, 21f); p(st = fino) { m(10.5f, 16f); t(10.5f, 13f); q(10.5f, 11f, 12f, 10.5f); q(13.5f, 11f, 13.5f, 13f); t(13.5f, 16f) }
            l(8f, 9f, 16f, 9f, 1.1f) }                                                                                                        // campanario
        TemaId.ESPACIO -> { r(9.5f, 9.5f, 5f, 5f, 0.6f); r(2f, 10.5f, 6f, 3f, 0.3f, true); r(16f, 10.5f, 6f, 3f, 0.3f, true); l(8f, 12f, 9.5f, 12f, 1.1f); l(14.5f, 12f, 16f, 12f, 1.1f)
            l(12f, 9.5f, 12f, 5.5f, 1.1f); ci(12f, 4.5f, 1.2f, st = fino) }                                                                 // satélite
        TemaId.ABISMO -> { ci(12f, 10.5f, 7.5f); ci(12f, 10.5f, 3f, st = fino); ci(6.4f, 11f, 1.3f, st = fino); ci(17.6f, 11f, 1.3f, st = fino)
            r(5.5f, 17.5f, 13f, 3.5f, 1.2f); l(9f, 19.2f, 9f, 19.2f, 1.6f); l(15f, 19.2f, 15f, 19.2f, 1.6f) }                                 // casco de buzo de latón
        TemaId.ANDALUS -> { p { m(5f, 5f); t(19f, 5f); t(19f, 19f); t(5f, 19f); close() }; p { m(12f, 2f); t(22f, 12f); t(12f, 22f); t(2f, 12f); close() }; ci(12f, 12f, 2.4f, st = fino) } // estrella de ocho puntas
        TemaId.MONGOL -> { p { m(8f, 21f); t(16f, 21f); t(15f, 14f); t(9f, 14f); close() }; l(12f, 14f, 12f, 4.5f); p { m(12f, 4.5f); q(12f, 2f, 14.5f, 2.5f); q(16.5f, 3.5f, 15f, 6f); t(12f, 6f) }
            l(11f, 15f, 11f, 20f, 0.8f); l(13f, 15f, 13f, 20f, 0.8f) }                                                                       // morin juur
        TemaId.RENACIMIENTO -> { l(7.5f, 21.5f, 12f, 2.5f); l(16.5f, 21.5f, 12f, 2.5f); l(12f, 15f, 12f, 21.5f, 1.1f); r(6.5f, 6.5f, 11f, 8f, 0.3f); l(5.5f, 14.5f, 18.5f, 14.5f) } // caballete
        TemaId.NAVIDAD -> { p { m(6f, 16f); q(6f, 5f, 12f, 5f); q(18f, 5f, 18f, 16f); t(19.5f, 18f); t(4.5f, 18f); close() }; ci(12f, 19.8f, 1.3f, relleno = true); l(12f, 3f, 12f, 5f, 1.1f) } // campana
        TemaId.SEDA -> { p { m(4f, 21f); t(4f, 11f); q(4f, 5f, 12f, 3.5f); q(20f, 5f, 20f, 11f); t(20f, 21f) }; p { m(9f, 21f); t(9f, 15f); q(12f, 11f, 15f, 15f); t(15f, 21f) }; l(2.5f, 21f, 21.5f, 21f) } // caravasar
        TemaId.URSS -> { p { m(4f, 10f); t(9f, 10f); t(18.5f, 5f); t(18.5f, 19f); t(9f, 14f); t(4f, 14f); close() }; l(7f, 14f, 8.2f, 19.5f); p(st = fino) { m(20.5f, 9f); q(22.5f, 12f, 20.5f, 15f) } } // megáfono
        TemaId.REVOLUCION -> { ci(12f, 12f, 9f); ci(12f, 12f, 5.6f, st = fino); ci(12f, 12f, 2.4f, relleno = true) } // escarapela
        TemaId.ANTIGUAS -> { p { m(5.5f, 9f); t(9f, 4f); t(15f, 4f); t(18.5f, 9f); t(12f, 21f); close() }; l(5.5f, 9f, 18.5f, 9f, 1.1f); l(9f, 4f, 10.5f, 9f, 1.1f); l(15f, 4f, 13.5f, 9f, 1.1f)
            l(10.5f, 9f, 12f, 21f, 0.9f); l(13.5f, 9f, 12f, 21f, 0.9f) }                                                                   // gema tallada
    } }

    // ------------------------------------------------------------ Vida (tu vida vista de lejos)
    private fun vida(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { p(st = fino) { m(12f, 20.5f); q(3.5f, 18f, 4.5f, 5.5f) }; p(st = fino) { m(12f, 20.5f); q(20.5f, 18f, 19.5f, 5.5f) }
            hoja(5.6f, 16.4f, 4.6f, 2.4f, -135f); hoja(4.1f, 11.8f, 4.6f, 2.4f, -105f); hoja(4.6f, 7.2f, 4.6f, 2.4f, -80f)
            hoja(18.4f, 16.4f, 4.6f, 2.4f, -45f); hoja(19.9f, 11.8f, 4.6f, 2.4f, -75f); hoja(19.4f, 7.2f, 4.6f, 2.4f, -100f)
            l(9.8f, 22f, 12f, 20.5f, 1.1f); l(14.2f, 22f, 12f, 20.5f, 1.1f) }                                                              // corona de laurel
        TemaId.CIBER -> { r(2.5f, 8f, 17f, 8f, 0.6f); l(21.2f, 10.5f, 21.2f, 13.5f, 2f)
            r(4.6f, 10f, 3.2f, 4f, 0.2f, true); r(8.8f, 10f, 3.2f, 4f, 0.2f, true); r(13f, 10f, 3.2f, 4f, 0.2f, true) }                // barra de energía a tres cuartos
        TemaId.NEON -> { ci(12f, 9f, 5.5f); p { m(7.2f, 11.8f); t(12f, 21.5f); t(16.8f, 11.8f) }; ci(12f, 9f, 2f, relleno = true) }         // el distrito, con su alfiler
        TemaId.HELENO -> { p(st = fino) { m(4.5f, 20.5f); q(9.5f, 11f, 19.5f, 3.5f) }
            hoja(6.6f, 13.2f, 5.2f, 2.2f, -110f); hoja(11.6f, 13.6f, 5.2f, 2.2f, 5f); hoja(10.8f, 7.4f, 5.2f, 2.2f, -110f); hoja(16.6f, 8.2f, 4.6f, 2f, 10f)
            ci(8.6f, 18.4f, 1.4f, relleno = true); ci(14.2f, 11.6f, 1.2f, relleno = true) }                                             // rama de olivo con aceitunas
        TemaId.EGIPTO -> { ov(9f, 2.5f, 6f, 8f); l(5.5f, 11.5f, 18.5f, 11.5f); l(12f, 10.5f, 12f, 21.5f) }                                // el ankh, la vida
        TemaId.MEDIEVAL -> { ci(12f, 13.5f, 7.5f); ci(12f, 13.5f, 1.4f, relleno = true); rayos(12f, 13.5f, 6, 1.4f, 7.5f, -90.0, 270.0, 1.2f)
            p(relleno = true) { m(9f, 5.6f); t(9f, 2f); t(10.5f, 3.6f); t(12f, 1.5f); t(13.5f, 3.6f); t(15f, 2f); t(15f, 5.6f); close() } } // rueda de la fortuna coronada
        TemaId.TEMPLARIO -> { p { m(2.5f, 21f); t(2.5f, 8f); t(5f, 8f); t(5f, 10f); t(7f, 10f); t(7f, 8f); t(9.5f, 8f); t(9.5f, 12f); t(14.5f, 12f); t(14.5f, 8f)
                t(17f, 8f); t(17f, 10f); t(19f, 10f); t(19f, 8f); t(21.5f, 8f); t(21.5f, 21f); close() }
            p(st = fino) { m(10f, 21f); t(10f, 17f); q(12f, 14.5f, 14f, 17f); t(14f, 21f) } }                                                     // el castillo de la casa
        TemaId.DISTOPIA -> { r(2.5f, 5f, 19f, 14f, 1f); r(5f, 8f, 5.5f, 7.5f, 0.3f); ci(7.75f, 10.4f, 1.3f, st = fino); p(st = fino) { m(5.6f, 15.5f); q(7.75f, 11.8f, 9.9f, 15.5f) }
            l(12.5f, 9f, 18.5f, 9f, 1.1f); l(12.5f, 12f, 18.5f, 12f, 1.1f); l(12.5f, 15f, 16f, 15f, 1.1f) }                               // cédula con foto
        TemaId.REFUGIO -> { arco(3f, 7f, 18f, 18f, 180f, 180f); l(3f, 16f, 21f, 16f, 1.1f); rayos(12f, 16f, 5, 6.4f, 9f, -180.0, 0.0, 1.2f)
            l(12f, 16f, 16.2f, 10f); ci(12f, 16f, 1.5f, relleno = true); r(9f, 18.5f, 6f, 2.5f, 0.4f) }                                   // manómetro
        TemaId.ORDEN -> { p { m(5f, 21f); t(5f, 4f); t(8f, 4f); t(8f, 6.5f); t(10.5f, 6.5f); t(10.5f, 4f); t(13.5f, 4f); t(13.5f, 6.5f); t(16f, 6.5f); t(16f, 4f); t(19f, 4f); t(19f, 21f); close() }
            p { m(10f, 21f); t(10f, 16.5f); q(12f, 14f, 14f, 16.5f); t(14f, 21f) }; l(12f, 9.5f, 12f, 12f, 1.4f) }                        // torre de la ciudadela
        TemaId.LABORATORIO -> { val fx = { y: Float -> 4.5f * sin((y - 3f) / 18f * 2f * Math.PI.toFloat()) }
            p { for (i in 0..36) { val y = 3f + i * 0.5f; if (i == 0) m(12f + fx(y), y) else t(12f + fx(y), y) } }
            p { for (i in 0..36) { val y = 3f + i * 0.5f; if (i == 0) m(12f - fx(y), y) else t(12f - fx(y), y) } }
            for (y in listOf(5.5f, 7.5f, 9.5f, 14.5f, 16.5f, 18.5f)) l(12f - fx(y), y, 12f + fx(y), y, 1.1f) }                            // doble hélice
        TemaId.COLONOS -> { p { m(4f, 8.5f); t(12f, 3f); t(20f, 8.5f) }; l(6f, 8f, 6f, 15f); l(18f, 8f, 18f, 15f); l(12f, 6f, 12f, 11.5f, 1.1f)
            r(10.5f, 11.5f, 3f, 2.6f, 0.3f, true); r(4f, 15f, 16f, 6f, 0.6f); l(4f, 18f, 20f, 18f, 1.1f) }                                // pozo del pueblo
        TemaId.NAPOLEON -> { ci(12f, 13.5f, 7.5f); l(12f, 6f, 12f, 4.6f); ci(12f, 3f, 1.5f, st = fino); rayos(12f, 13.5f, 4, 5.6f, 7.5f, -90.0, 270.0, 1.2f)
            l(12f, 13.5f, 12f, 9f, 1.4f); l(12f, 13.5f, 15f, 15.2f, 1.4f) }                                                               // reloj de bolsillo
        TemaId.BOTANICO -> { l(12f, 7f, 12f, 18.5f, 1.6f); p(st = fino) { m(12f, 7f); q(10.5f, 3.5f, 8.5f, 3f) }; p(st = fino) { m(12f, 7f); q(13.5f, 3.5f, 15.5f, 3f) }
            p { m(12f, 9.5f); q(4f, 3f, 3.5f, 9.5f); q(4f, 13.5f, 12f, 12.5f) }; p { m(12f, 9.5f); q(20f, 3f, 20.5f, 9.5f); q(20f, 13.5f, 12f, 12.5f) }
            p { m(12f, 12.5f); q(5f, 13.5f, 6.5f, 18.5f); q(9f, 21f, 12f, 15.5f) }; p { m(12f, 12.5f); q(19f, 13.5f, 17.5f, 18.5f); q(15f, 21f, 12f, 15.5f) } } // mariposa
        TemaId.NAVIDAD -> { ci(12f, 16.5f, 5f); ci(12f, 8.5f, 3.4f); r(10f, 2f, 4f, 3f, 0.3f, true); l(8.6f, 5f, 15.4f, 5f, 1.5f)
            ci(12f, 15f, 0.8f, relleno = true); ci(12f, 18f, 0.8f, relleno = true); l(7.2f, 13.5f, 3.5f, 10.5f, 1.1f); l(16.8f, 13.5f, 20.5f, 10.5f, 1.1f) } // muñeco de nieve
        TemaId.SEDA -> { l(12f, 1.5f, 12f, 4.5f, 1.1f)
            val tejados = listOf(7.5f to 5.5f, 12.5f to 7f, 17.5f to 8.5f)
            tejados.forEach { (y, hw) -> p { m(12f - hw, y); q(12f - hw * 0.35f, y - 0.6f, 12f, y - 2.6f); q(12f + hw * 0.35f, y - 0.6f, 12f + hw, y) } }
            l(8.5f, 7.6f, 8.5f, 10.5f, 1.1f); l(15.5f, 7.6f, 15.5f, 10.5f, 1.1f); l(7.5f, 12.6f, 7.5f, 15.5f, 1.1f); l(16.5f, 12.6f, 16.5f, 15.5f, 1.1f)
            l(6.5f, 17.6f, 6.5f, 21f, 1.1f); l(17.5f, 17.6f, 17.5f, 21f, 1.1f); l(4f, 21.2f, 20f, 21.2f); r(10.6f, 18.4f, 2.8f, 2.8f, 0.3f) } // pagoda
        TemaId.GOTICO -> { l(6f, 3f, 18f, 3f, 2f); l(6f, 21f, 18f, 21f, 2f); p { m(7.5f, 3f); q(7.5f, 9f, 12f, 12f); q(16.5f, 9f, 16.5f, 3f) }; p { m(7.5f, 21f); q(7.5f, 15f, 12f, 12f); q(16.5f, 15f, 16.5f, 21f) }
            p(relleno = true) { m(9.3f, 6.6f); t(14.7f, 6.6f); q(13.5f, 9.4f, 12f, 10.4f); q(10.5f, 9.4f, 9.3f, 6.6f); close() }; p(relleno = true) { m(9.6f, 21f); q(12f, 16.6f, 14.4f, 21f); close() } } // reloj de arena
        TemaId.ESPACIO -> { ci(12f, 12f, 8.5f); ov(8f, 3.5f, 8f, 17f, st = fino); l(3.5f, 12f, 20.5f, 12f, 1.1f)
            p(st = fino) { m(5.2f, 7.4f); q(12f, 9.6f, 18.8f, 7.4f) }; p(st = fino) { m(5.2f, 16.6f); q(12f, 14.4f, 18.8f, 16.6f) } }      // la Tierra vista desde lejos
        TemaId.ABISMO -> { p(st = fino) { m(2.5f, 5.5f); q(7f, 3.5f, 12f, 5.5f); q(17f, 7.5f, 21.5f, 5.5f) }; p(st = fino) { m(2.5f, 10.5f); q(7f, 8.5f, 12f, 10.5f); q(17f, 12.5f, 21.5f, 10.5f) }
            p(st = fino) { m(2.5f, 15.5f); q(7f, 13.5f, 12f, 15.5f); q(17f, 17.5f, 21.5f, 15.5f) }; l(12f, 2.5f, 12f, 17.5f, 1.1f)
            p(relleno = true) { m(12f, 17f); q(14.6f, 20f, 12f, 22f); q(9.4f, 20f, 12f, 17f); close() } }                                  // las capas del mar y la plomada
        TemaId.ANDALUS -> { ci(12f, 14f, 7f); p { m(9.5f, 7.6f); t(9f, 4.4f); t(10.8f, 5.9f); t(12f, 3.6f); t(13.2f, 5.9f); t(15f, 4.4f); t(14.5f, 7.6f) }
            hoja(18.2f, 5.6f, 4.4f, 1.9f, -30f); p(st = fino) { m(7.8f, 15f); q(8.6f, 19f, 12.8f, 19.6f) } }                              // granada
        TemaId.MONGOL -> { l(12f, 7f, 12f, 22f); p { m(9f, 2.5f); t(9f, 5f); q(9f, 7f, 12f, 7f); q(15f, 7f, 15f, 5f); t(15f, 2.5f) }; l(12f, 7f, 12f, 1.8f)
            p(relleno = true) { m(9f, 8.6f); q(7.8f, 14f, 10f, 17.5f); q(12f, 15.2f, 14f, 17.5f); q(16.2f, 14f, 15f, 8.6f); close() } }   // estandarte de crin
        TemaId.RENACIMIENTO -> { ci(12f, 11.5f, 9f, st = fino); p(st = fino) { m(4.5f, 5.5f); t(19.5f, 5.5f); t(19.5f, 20.5f); t(4.5f, 20.5f); close() }
            ci(12f, 6.9f, 1.5f, relleno = true); l(12f, 8.6f, 12f, 14.5f, 1.4f); l(4.8f, 10f, 19.2f, 10f, 1.2f); l(12f, 14.5f, 9f, 20.5f, 1.2f); l(12f, 14.5f, 15f, 20.5f, 1.2f) } // figura en el círculo y el cuadrado
        TemaId.URSS -> { p(st = fino) { m(12f, 22f); q(11.4f, 15f, 12f, 3.5f) }; hoja(12f, 3.6f, 3.6f, 2f, -90f)
            for (y in listOf(6.2f, 9.2f, 12.2f)) { hoja(10.2f, y, 3.8f, 2f, -125f); hoja(13.8f, y, 3.8f, 2f, -55f) }
            hoja(15f, 18f, 6f, 2f, -40f, relleno = false) }                                                                                // espiga de trigo
        TemaId.REVOLUCION -> { p { m(7f, 12.5f); q(2.8f, 11.5f, 4.8f, 7.6f); q(5.4f, 3f, 10f, 3.6f); q(12f, 1.5f, 14.4f, 3.6f); q(19.2f, 3f, 19.2f, 7.6f); q(21.2f, 11.5f, 17f, 12.5f); close() }
            l(12f, 12.5f, 12f, 21f); l(8.5f, 21.5f, 15.5f, 21.5f); l(9.4f, 15.4f, 14.6f, 17f, 1.4f) }                                     // árbol de la libertad con su cinta
        TemaId.ANTIGUAS -> { ci(12f, 12f, 3.8f, relleno = true)
            p(relleno = true) { m(6f, 7.6f); q(1.4f, 12f, 6f, 16.4f); q(3.6f, 12f, 6f, 7.6f); close() }
            p(relleno = true) { m(18f, 7.6f); q(22.6f, 12f, 18f, 16.4f); q(20.4f, 12f, 18f, 7.6f); close() }
            l(5f, 20f, 19f, 20f, 1.1f); ci(12f, 20f, 0.9f, relleno = true) }                                                               // las lunas: creciente, llena y menguante
    } }

    // ------------------------------------------------------------ Registrar (el botón del centro)
    private fun registrar(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { l(12f, 4.5f, 12f, 19.5f, 2.4f); l(4.5f, 12f, 19.5f, 12f, 2.4f)
            l(9.8f, 4.2f, 14.2f, 4.2f, 1.5f); l(9.8f, 19.8f, 14.2f, 19.8f, 1.5f); l(4.2f, 9.8f, 4.2f, 14.2f, 1.5f); l(19.8f, 9.8f, 19.8f, 14.2f, 1.5f) } // «+» de inscripción, con remates
        TemaId.CIBER -> { p { m(7f, 4f); t(4f, 4f); t(4f, 20f); t(7f, 20f) }; p { m(17f, 4f); t(20f, 4f); t(20f, 20f); t(17f, 20f) }
            l(12f, 8f, 12f, 16f, 2.2f); l(8f, 12f, 16f, 12f, 2.2f) }                                                                         // [+]
        TemaId.NEON -> { r(2.5f, 6f, 19f, 12f, 6f); l(12f, 8.8f, 12f, 15.2f, 2.2f); l(8.8f, 12f, 15.2f, 12f, 2.2f) }                        // «+» en la placa del puesto
        TemaId.HELENO -> { p { m(4f, 7f); t(9f, 3.5f); t(17f, 4f); t(20.5f, 9.5f); t(19f, 17f); t(13f, 20.5f); t(5.5f, 18.5f); t(3f, 12.5f); close() }
            l(12f, 8.5f, 12f, 15.5f, 2f); l(8.5f, 12f, 15.5f, 12f, 2f) }                                                                     // óstrakon con el «+» grabado
        TemaId.EGIPTO -> { r(7.5f, 2.5f, 9f, 19f, 1.5f); ci(10.4f, 5.2f, 1.1f, relleno = true); ci(13.6f, 5.2f, 1.1f)
            l(12f, 10f, 12f, 17.5f, 2f); l(9.5f, 13.75f, 14.5f, 13.75f, 2f) }                                                              // la paleta del escriba con sus dos pocillos y el «+»
        TemaId.MEDIEVAL -> p(relleno = true) { m(10.6f, 10.6f); t(9f, 3f); t(15f, 3f); t(13.4f, 10.6f); t(21f, 9f); t(21f, 15f); t(13.4f, 13.4f); t(15f, 21f); t(9f, 21f); t(10.6f, 13.4f); t(3f, 15f); t(3f, 9f); close() } // cruz patada
        TemaId.TEMPLARIO -> { p { m(4.5f, 3.5f); t(19.5f, 3.5f); q(20f, 14f, 12f, 21f); q(4f, 14f, 4.5f, 3.5f); close() }; l(12f, 7.5f, 12f, 15.5f, 2f); l(8f, 11.2f, 16f, 11.2f, 2f) } // escudo con el «+»
        TemaId.DISTOPIA -> { ci(12f, 4.6f, 2.3f); r(10.5f, 6.9f, 3f, 4.6f, 0.3f); r(5f, 11.6f, 14f, 4f, 0.6f, relleno = true); r(6.5f, 18.5f, 11f, 3f, 0.3f) } // sello de goma y su marca
        TemaId.REFUGIO -> { ci(12f, 12f, 8.8f); ci(12f, 12f, 6.4f, st = fino); l(12f, 8.5f, 12f, 15.5f, 2f); l(8.5f, 12f, 15.5f, 12f, 2f) } // botón de panel
        TemaId.ORDEN -> { l(12f, 3.5f, 12f, 9.5f, 2.8f); l(12f, 14.5f, 12f, 20.5f, 2.8f); l(3.5f, 12f, 9.5f, 12f, 2.8f); l(14.5f, 12f, 20.5f, 12f, 2.8f)
            ci(12f, 12f, 1f, relleno = true) }                                                                                               // «+» de estarcido, con puentes
        TemaId.LABORATORIO -> { l(15.5f, 8.5f, 8f, 16f, 2.2f); l(8f, 16f, 6.4f, 17.6f, 1.2f); ci(17.6f, 6.4f, 3f, relleno = true)
            p(relleno = true) { m(5.5f, 18.6f); q(7.3f, 21f, 5.5f, 22f); q(3.7f, 21f, 5.5f, 18.6f); close() }; l(6f, 3f, 6f, 9f, 1.6f); l(3f, 6f, 9f, 6f, 1.6f) } // cuentagotas
        TemaId.COLONOS -> { r(10f, 3f, 4f, 18f, 0.4f); r(3f, 10f, 18f, 4f, 0.4f); for ((x, y) in listOf(12f to 5f, 12f to 19f, 5f to 12f, 19f to 12f)) ci(x, y, 0.7f, relleno = true) } // tablas cruzadas y clavadas
        TemaId.NAPOLEON -> { p { m(5f, 19.5f); q(9f, 11f, 19.5f, 4f) }; p { m(19f, 19.5f); q(15f, 11f, 4.5f, 4f) }
            l(4f, 16.6f, 7.8f, 19.8f, 1.6f); l(20f, 16.6f, 16.2f, 19.8f, 1.6f); ci(3.8f, 21f, 1f, relleno = true); ci(20.2f, 21f, 1f, relleno = true) } // sables cruzados
        TemaId.BOTANICO -> { ci(15f, 6f, 2.6f, relleno = true); l(13.6f, 7.4f, 5f, 18f, 1.4f); l(18f, 13.5f, 18f, 20.5f, 1.8f); l(14.5f, 17f, 21.5f, 17f, 1.8f) } // alfiler de entomólogo y un ejemplar más
        TemaId.NAVIDAD -> { for (k in 0 until 6) { val g = -90.0 + 60.0 * k
                l(12f, 12f, px(12f, 9.5f, g), py(12f, 9.5f, g))
                for (d in listOf(-45.0, 45.0)) l(px(12f, 5.6f, g), py(12f, 5.6f, g), px(px(12f, 5.6f, g), 2.8f, g + d), py(py(12f, 5.6f, g), 2.8f, g + d), 1.2f) } } // copo de nieve
        TemaId.SEDA -> { r(3.5f, 3.5f, 17f, 17f, 0.8f); p(st = fino) { m(5.6f, 5.6f); t(18.4f, 5.6f); t(18.4f, 18.4f); t(5.6f, 18.4f); close() }
            l(12f, 7.5f, 12f, 16.5f, 2.2f); l(7.5f, 10.8f, 16.5f, 10.8f, 2.2f) }                                                             // sello de cinabrio con 十
        TemaId.GOTICO -> { l(12f, 6f, 12f, 18f, 2f); l(6f, 12f, 18f, 12f, 2f)
            for ((dx, dy) in listOf(0f to -1f, 0f to 1f, -1f to 0f, 1f to 0f)) { val ex = 12f + dx * 7.6f; val ey = 12f + dy * 7.6f
                ci(ex + dx * 0.8f, ey + dy * 0.8f, 1.4f, relleno = true)
                ci(ex - dy * 1.5f - dx * 0.5f, ey + dx * 1.5f - dy * 0.5f, 1.4f, relleno = true); ci(ex + dy * 1.5f - dx * 0.5f, ey - dx * 1.5f - dy * 0.5f, 1.4f, relleno = true) } } // cruz trebolada
        TemaId.ESPACIO -> { p(relleno = true) { m(12f, 2.5f); q(13f, 11f, 21.5f, 12f); q(13f, 13f, 12f, 21.5f); q(11f, 13f, 2.5f, 12f); q(11f, 11f, 12f, 2.5f); close() }
            l(19f, 2.5f, 19f, 7f, 1.1f); l(16.8f, 4.8f, 21.2f, 4.8f, 1.1f) }                                                                 // destello de cuatro puntas
        TemaId.ABISMO -> { l(12f, 5f, 12f, 19f, 2.4f); l(5f, 12f, 19f, 12f, 2.4f); ci(18.5f, 5.5f, 1.6f, st = fino); ci(20.5f, 2.5f, 0.9f, st = fino) } // «+» con sus burbujas
        TemaId.ANDALUS -> { p { m(4f, 15f); t(11f, 15f); t(11.5f, 20.5f); t(3.5f, 20.5f); close() }; l(6f, 15f, 6.5f, 13.5f, 1.1f); l(9f, 15f, 8.5f, 13.5f, 1.1f)
            l(8.4f, 13f, 18.5f, 3f, 1.8f); p(relleno = true) { m(7f, 14.6f); t(8.6f, 11.6f); t(10f, 13f); close() }
            l(17.5f, 13.5f, 17.5f, 20.5f, 1.8f); l(14f, 17f, 21f, 17f, 1.8f) }                                                               // cálamo y tintero
        TemaId.MONGOL -> { l(12f, 5.4f, 12f, 18.6f, 1.8f); l(5.4f, 12f, 18.6f, 12f, 1.8f); ci(12f, 12f, 2.4f, st = fino)
            p(relleno = true) { m(12f, 2f); t(14.2f, 5.6f); t(9.8f, 5.6f); close() }; p(relleno = true) { m(12f, 22f); t(14.2f, 18.4f); t(9.8f, 18.4f); close() }
            p(relleno = true) { m(2f, 12f); t(5.6f, 9.8f); t(5.6f, 14.2f); close() }; p(relleno = true) { m(22f, 12f); t(18.4f, 9.8f); t(18.4f, 14.2f); close() } } // las cuatro direcciones de la estepa
        TemaId.RENACIMIENTO -> { ci(8f, 16f, 4.5f, st = fino); l(8f, 10f, 8f, 22f, 1.1f); l(2f, 16f, 14f, 16f, 1.1f)
            l(10.6f, 13.4f, 18.6f, 5.4f, 2.2f); l(15.2f, 7.4f, 17.2f, 9.4f, 1.1f); p(relleno = true) { m(18f, 4f); t(21.8f, 2.2f); t(20f, 6f); close() } } // portalápiz de sanguina y su cruz de construcción
        TemaId.URSS -> { ci(15f, 9.5f, 6.5f); p(relleno = true) { m(2.5f, 20f); t(13.5f, 10.5f); t(7.5f, 21.5f); close() }
            l(16.5f, 18.5f, 21.5f, 18.5f, 1.8f); l(19f, 16f, 19f, 21f, 1.8f) }                                                               // la cuña que entra en el círculo
        TemaId.REVOLUCION -> { l(12f, 3f, 4.5f, 20.5f); l(12f, 3f, 19.5f, 20.5f); l(7.2f, 14.5f, 16.8f, 14.5f); l(12f, 3f, 12f, 16f, 1.1f); ci(12f, 17.2f, 1.3f, relleno = true) } // nivel de la igualdad
        TemaId.ANTIGUAS -> { l(12f, 3f, 12f, 21f, 2f); l(12f, 7.5f, 17.5f, 3.5f, 1.8f); l(12f, 12f, 6.5f, 16f, 1.8f); l(12f, 16.5f, 16.5f, 20.5f, 1.8f); ci(7f, 6f, 1.3f, relleno = true) } // runa inventada
    } }

    // ------------------------------------------------------------ Ajustes (el botón «Más» de arriba)
    private fun ajustes(tm: TemaId) { when (tm) {
        TemaId.ROMANO -> { l(12f, 8f, 12f, 21.5f, 1.8f); l(4f, 8f, 20f, 8f, 1.8f); l(8.5f, 5.5f, 15.5f, 10.5f, 1.2f); l(4f, 8f, 4f, 14.5f, 1f); l(20f, 8f, 20f, 14.5f, 1f)
            ci(4f, 15.6f, 1.2f, relleno = true); ci(20f, 15.6f, 1.2f, relleno = true); ci(12f, 8f, 1.3f, relleno = true); l(9f, 21.5f, 15f, 21.5f) } // groma del agrimensor
        TemaId.CIBER -> { l(6f, 3f, 6f, 21f, 1.1f); l(12f, 3f, 12f, 21f, 1.1f); l(18f, 3f, 18f, 21f, 1.1f)
            r(4f, 13f, 4f, 3f, 0.4f, true); r(10f, 6f, 4f, 3f, 0.4f, true); r(16f, 15f, 4f, 3f, 0.4f, true) }                             // deslizadores
        TemaId.NEON -> { r(4f, 3f, 16f, 18f, 1.5f); r(7f, 6f, 3.5f, 7f, 0.6f, true); r(13.5f, 10f, 3.5f, 7f, 0.6f, true); l(7f, 18f, 17f, 18f, 1.1f) } // la caja de fusibles
        TemaId.HELENO -> { engrane(9.5f, 10f, 6.6f, 5f, 8); ci(9.5f, 10f, 1.8f); engrane(17f, 17f, 4.6f, 3.4f, 6); ci(17f, 17f, 1.1f, relleno = true) } // dos engranes que encajan
        TemaId.EGIPTO -> { r(10f, 9f, 4f, 12f, 0.6f); l(6.5f, 3.5f, 17.5f, 3.5f, 1.5f); l(6.5f, 5.5f, 17.5f, 5.5f, 1.5f); l(6.5f, 7.5f, 17.5f, 7.5f, 1.5f)
            l(7.5f, 21.5f, 16.5f, 21.5f) }                                                                                                   // el pilar djed, lo que se mantiene firme
        TemaId.MEDIEVAL -> { ci(7f, 18.6f, 2.6f); ci(17f, 18.6f, 2.6f); l(8.6f, 16.5f, 17.6f, 3.5f); l(15.4f, 16.5f, 6.4f, 3.5f); ci(12f, 10f, 0.9f, relleno = true) } // tijeras del copista
        TemaId.TEMPLARIO -> { p { m(3.5f, 10f); t(19.5f, 10f); q(19.5f, 13f, 15.5f, 13.5f); t(15f, 16.5f); t(18f, 20f); t(6f, 20f); t(9f, 16.5f); t(8.5f, 13.5f); q(4.5f, 13f, 3.5f, 10f); close() }
            l(12f, 7.6f, 17.5f, 2.5f, 1.4f); r(15.6f, 1f, 4.6f, 2.6f, 0.4f, true) }                                                               // yunque y martillo de la herrería
        TemaId.DISTOPIA -> { r(4f, 15f, 16f, 6f, 0.6f); l(8f, 18f, 16f, 18f, 1.1f); l(12f, 17f, 16.8f, 5.6f, 1.8f); ci(17.4f, 4.2f, 2.2f, relleno = true) } // palanca de mando
        TemaId.REFUGIO -> { ci(12f, 10f, 7f); rayos(12f, 10f, 4, 1.6f, 7f, 45.0, 405.0); ci(12f, 10f, 1.6f, relleno = true); l(12f, 17f, 12f, 19f); r(4f, 19f, 16f, 3f, 0.5f) } // válvula de rueda
        TemaId.ORDEN -> { p { for (k in 0 until 6) { val g = 60.0 * k; if (k == 0) m(px(12f, 9f, g), py(12f, 9f, g)) else t(px(12f, 9f, g), py(12f, 9f, g)) }; close() }; ci(12f, 12f, 3.8f) } // tuerca
        TemaId.LABORATORIO -> { r(5f, 19.5f, 14f, 2f, 0.5f); p { m(15.5f, 19.5f); q(19f, 13f, 13.5f, 8.5f) }; l(9f, 3f, 13f, 12f, 2.6f); l(7.6f, 3.4f, 10.6f, 2f, 1.4f)
            l(13f, 12f, 13.6f, 13.6f, 1.4f); l(9.5f, 14.8f, 17f, 14.8f, 1.7f) }                                                               // microscopio
        TemaId.COLONOS -> { p { m(9.5f, 21f); t(10.6f, 11f); t(13.4f, 11f); t(14.5f, 21f); close() }; l(7.5f, 21.2f, 16.5f, 21.2f)
            rayos(12f, 9f, 4, 1.6f, 8f, 45.0, 405.0); ci(12f, 9f, 1.3f, relleno = true) }                                                   // molino de viento
        TemaId.NAPOLEON -> { p { m(4f, 12.4f); t(17.2f, 6.4f); t(18.4f, 9.2f); t(5.2f, 15.2f); close() }; ci(9f, 16f, 4.5f); rayos(9f, 16f, 6, 0.8f, 4.5f, 0.0, 360.0, 1.1f)
            l(11.5f, 17.5f, 20.5f, 21f) }                                                                                                     // cañón
        TemaId.BOTANICO -> { r(5f, 10f, 10f, 10.5f, 1.5f); l(15f, 13f, 20.5f, 7f, 1.5f); l(19.4f, 5.8f, 21.8f, 8.2f, 1.9f); p(st = fino) { m(6.5f, 10f); q(10f, 3.6f, 13.5f, 10f) } } // regadera
        TemaId.NAVIDAD -> { ov(3.5f, 3.5f, 7.5f, 9f); ov(13f, 3.5f, 7.5f, 9f); r(10.6f, 6f, 2.8f, 4f, 0.5f, true); l(12f, 10f, 12f, 20.5f); l(9.5f, 20.5f, 14.5f, 20.5f); l(12f, 16.5f, 14.5f, 16.5f, 1.2f) } // llave de cuerda
        TemaId.SEDA -> { r(3f, 3.5f, 18f, 17f, 0.8f); l(3f, 9f, 21f, 9f, 1.4f); for (x in listOf(7f, 12f, 17f)) l(x, 3.5f, x, 20.5f, 0.8f)
            for ((x, y) in listOf(7f to 5.2f, 12f to 6.6f, 17f to 5.2f, 7f to 11f, 7f to 13.4f, 12f to 15.8f, 12f to 18.2f, 17f to 11f)) ov(x - 1.8f, y - 1f, 3.6f, 2f, relleno = true) } // ábaco
        TemaId.GOTICO -> { ci(5.5f, 18.5f, 2.6f); l(7.4f, 16.6f, 19f, 5f); l(17f, 7f, 18.8f, 8.8f, 1.6f); l(19f, 5f, 20.8f, 6.8f, 1.6f)
            ci(18.5f, 18.5f, 2.6f); l(16.6f, 16.6f, 5f, 5f); l(7f, 7f, 5.2f, 8.8f, 1.6f); l(5f, 5f, 3.2f, 6.8f, 1.6f) }                     // llaves cruzadas
        TemaId.ESPACIO -> { p { m(3.5f, 8f); q(5f, 17.5f, 15f, 18f) }; p(st = fino) { m(3.5f, 8f); t(15f, 18f) }; l(9.2f, 13f, 15f, 7f, 1.1f); ci(15.5f, 6.5f, 1.3f, relleno = true)
            p(st = fino) { m(18f, 3f); q(21f, 5.5f, 19.5f, 9f) }; l(10f, 16f, 12f, 21f); l(8f, 21.5f, 16f, 21.5f) }                         // antena parabólica
        TemaId.ABISMO -> { ci(12f, 12f, 7f); ci(12f, 12f, 2f, st = fino); rayos(12f, 12f, 6, 2f, 9.8f, 0.0, 360.0, 1.4f) }               // volante de una válvula
        TemaId.ANDALUS -> { ci(12f, 11f, 8f); ci(12f, 11f, 2f, st = fino); rayos(12f, 11f, 8, 2f, 8f, 0.0, 360.0, 1.1f)
            for (k in 0 until 8) { val g = 22.5 + 45.0 * k; ci(px(12f, 8f, g), py(11f, 8f, g), 1.1f, relleno = true) }
            p(st = fino) { m(3f, 21f); q(6f, 19.5f, 9f, 21f); q(12f, 22.5f, 15f, 21f); q(18f, 19.5f, 21f, 21f) } }                          // noria
        TemaId.MONGOL -> { ci(12f, 12f, 8.5f); ci(12f, 12f, 4f); for (k in 0 until 12) { val g = 15.0 + 30.0 * k; l(px(12f, 4.8f, g), py(12f, 4.8f, g), px(12f, 8.5f, g), py(12f, 8.5f, g), 1f) }
            l(10.8f, 8f, 10.8f, 16f, 1.1f); l(13.2f, 8f, 13.2f, 16f, 1.1f); l(8f, 10.8f, 16f, 10.8f, 1.1f); l(8f, 13.2f, 16f, 13.2f, 1.1f) } // corona de la yurta
        TemaId.RENACIMIENTO -> { l(4f, 2.5f, 20f, 2.5f, 1.8f); l(12f, 2.5f, 12f, 5.8f); ci(12f, 9f, 3.2f); ci(12f, 9f, 0.9f, relleno = true)
            l(8.8f, 9f, 8.8f, 20f, 1.1f); l(15.2f, 9f, 15.2f, 14f, 1.1f); r(13.2f, 14f, 4f, 5f, 0.4f, true); ci(8.8f, 20.8f, 1f) }      // polea
        TemaId.URSS -> { p { m(6f, 21f); q(10f, 12f, 7.5f, 3f) }; p { m(18f, 21f); q(14f, 12f, 16.5f, 3f) }; l(7.5f, 3f, 16.5f, 3f); l(4.5f, 21f, 19.5f, 21f)
            l(7f, 19f, 15.8f, 6f, 1f); l(17f, 19f, 8.2f, 6f, 1f); l(8.4f, 12f, 15.6f, 12f, 1.1f); l(12f, 3f, 12f, 0.8f, 1.1f) }            // torre de radio de celosía
        TemaId.REVOLUCION -> { ci(12f, 12f, 8.8f); for (k in 0 until 10) { val g = -90.0 + 36.0 * k; l(px(12f, 5.6f, g), py(12f, 5.6f, g), px(12f, 7.2f, g), py(12f, 7.2f, g), 1.1f) }
            l(12f, 12f, 12f, 6.5f, 1.5f); l(12f, 12f, 15.5f, 14f, 1.5f); ci(12f, 12f, 1f, relleno = true) }                                // reloj decimal
        TemaId.ANTIGUAS -> { p { m(3f, 7f); t(19f, 7f); t(19f, 10f); q(15f, 11f, 14.5f, 14f); t(17f, 18f); t(7f, 18f); t(9.5f, 14f); q(9f, 11f, 6f, 10f); q(3.5f, 9.5f, 3f, 7f); close() }
            l(5f, 20.5f, 19f, 20.5f, 1.8f) }                                                                                                  // yunque
    } }
}
