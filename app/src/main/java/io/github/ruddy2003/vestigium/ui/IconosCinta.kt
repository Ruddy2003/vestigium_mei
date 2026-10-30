package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los íconos de las pistas de la cinta, dibujados todos en el mismo cuadro y con el mismo trazo,
 * para que se vean del mismo tamaño (los caracteres de texto de antes no medían lo mismo en cada letra).
 * Con [tema], el ícono va dentro del marco de ese tema (ver [marcoPista]); sin él, solo, como en la libreta.
 */
@Composable
fun IconoPista(id: String, color: Color, modifier: Modifier = Modifier, tam: Dp = 18.dp, tema: TemaId? = null) {
    Canvas(modifier.size(tam)) {
        if (tema == null) iconoPista(id, color)
        else {
            marcoPista(tema, color.copy(alpha = color.alpha * 0.6f))
            inset(size.minDimension * 0.22f) { iconoPista(id, color) }
        }
    }
}

/** El marco de los íconos de la cinta en cada tema: clípeo, corchetes de consola, frontón, recuadro iluminado, sello de goma… */
private fun DrawScope.marcoPista(tema: TemaId, c: Color) {
    val w = size.width; val h = size.height; val g = w * 0.06f; val o = center
    when (tema) {
        // Clípeo: el escudo redondo con su doble canto.
        TemaId.ROMANO -> { drawCircle(c, w * 0.47f, o, style = Stroke(g)); drawCircle(c, w * 0.4f, o, style = Stroke(g * 0.5f)) }
        // La placa de la parada del autobús: rectángulo de esquinas romas, con el poste que baja.
        TemaId.URBE -> {
            drawRoundRect(c, Offset(w * 0.06f, h * 0.1f), Size(w * 0.88f, h * 0.72f), CornerRadius(w * 0.1f), style = Stroke(g))
            drawLine(c, Offset(w * 0.5f, h * 0.82f), Offset(w * 0.5f, h * 0.98f), g * 1.4f)
        }
        // Corchetes de consola: [ ícono ].
        TemaId.CIBER -> drawPath(Path().apply {
            moveTo(w * 0.2f, h * 0.06f); lineTo(w * 0.04f, h * 0.06f); lineTo(w * 0.04f, h * 0.94f); lineTo(w * 0.2f, h * 0.94f)
            moveTo(w * 0.8f, h * 0.06f); lineTo(w * 0.96f, h * 0.06f); lineTo(w * 0.96f, h * 0.94f); lineTo(w * 0.8f, h * 0.94f)
        }, c, style = Stroke(g))
        // Placa redonda de letrero, con el tubo y su halo.
        TemaId.NEON -> {
            drawRoundRect(c.copy(alpha = c.alpha * 0.3f), Offset(w * 0.05f, h * 0.05f), Size(w * 0.9f, h * 0.9f), androidx.compose.ui.geometry.CornerRadius(w * 0.3f), style = Stroke(g * 2.2f))
            drawRoundRect(c, Offset(w * 0.05f, h * 0.05f), Size(w * 0.9f, h * 0.9f), androidx.compose.ui.geometry.CornerRadius(w * 0.3f), style = Stroke(g * 0.8f))
        }
        // Templo: frontón arriba y gradas abajo.
        TemaId.HELENO -> {
            drawPath(Path().apply { moveTo(w * 0.04f, h * 0.19f); lineTo(w * 0.5f, h * 0.02f); lineTo(w * 0.96f, h * 0.19f); close() }, c, style = Stroke(g * 0.8f))
            drawLine(c, Offset(w * 0.14f, h * 0.86f), Offset(w * 0.86f, h * 0.86f), g * 0.6f)
            drawLine(c, Offset(w * 0.04f, h * 0.95f), Offset(w * 0.96f, h * 0.95f), g)
        }
        // Pilono: los muros en talud y la gola que se abre arriba.
        TemaId.EGIPTO -> {
            drawPath(Path().apply {
                moveTo(w * 0.1f, h * 0.96f); lineTo(w * 0.18f, h * 0.2f); quadraticTo(w * 0.15f, h * 0.07f, w * 0.04f, h * 0.04f)
                lineTo(w * 0.96f, h * 0.04f); quadraticTo(w * 0.85f, h * 0.07f, w * 0.82f, h * 0.2f); lineTo(w * 0.9f, h * 0.96f); close()
            }, c, style = Stroke(g * 0.8f))
            drawLine(c, Offset(w * 0.17f, h * 0.2f), Offset(w * 0.83f, h * 0.2f), g * 0.5f)
        }
        // Recuadro de miniatura con las cuatro esquinas doradas.
        TemaId.MEDIEVAL -> {
            drawRect(c, Offset(w * 0.06f, h * 0.06f), Size(w * 0.88f, h * 0.88f), style = Stroke(g * 0.6f))
            listOf(Offset(w * 0.06f, h * 0.06f), Offset(w * 0.94f, h * 0.06f), Offset(w * 0.06f, h * 0.94f), Offset(w * 0.94f, h * 0.94f))
                .forEach { p -> drawRect(c, Offset(p.x - w * 0.07f, p.y - h * 0.07f), Size(w * 0.14f, h * 0.14f)) }
        }
        // Escudo de lágrima con el ribete doble de hierro.
        TemaId.TEMPLARIO -> {
            val escudo = Path().apply { moveTo(w * 0.08f, h * 0.04f); lineTo(w * 0.92f, h * 0.04f); quadraticTo(w * 0.96f, h * 0.62f, w * 0.5f, h * 0.98f); quadraticTo(w * 0.04f, h * 0.62f, w * 0.08f, h * 0.04f); close() }
            drawPath(escudo, c, style = Stroke(g))
            drawLine(c, Offset(w * 0.14f, h * 0.12f), Offset(w * 0.86f, h * 0.12f), g * 0.5f)
        }
        // Ficha de expediente: borde grueso y pestaña.
        TemaId.DISTOPIA -> {
            drawRect(c, Offset(w * 0.05f, h * 0.08f), Size(w * 0.9f, h * 0.87f), style = Stroke(g * 1.6f))
            drawRect(c, Offset(w * 0.05f, 0f), Size(w * 0.42f, h * 0.1f))
        }
        // Esfera de medidor con sus rayas.
        TemaId.REFUGIO -> {
            drawArc(c, 135f, 270f, false, Offset(w * 0.04f, h * 0.04f), Size(w * 0.92f, h * 0.92f), style = Stroke(g))
            for (k in 0..6) { val a = 135f + k * 45f; drawLine(c, polarFig(o, w * 0.46f, a), polarFig(o, w * 0.37f, a), g * 0.7f) }
        }
        // Engrane.
        TemaId.ORDEN -> drawPath(figEngrane(o, w * 0.49f, 10, 0.86f), c, style = Stroke(g * 0.8f))
        // La esfera de un manómetro: el bisel y la zona roja del final de la escala.
        TemaId.VAPOR -> {
            drawCircle(c, w * 0.46f, o, style = Stroke(g * 1.2f))
            drawArc(c, 351f, 54f, false, Offset(o.x - w * 0.34f, o.y - h * 0.34f), Size(w * 0.68f, h * 0.68f), style = Stroke(g * 1.4f))
            for (k in 0..4) { val a = 135f + k * 54f; drawLine(c, polarFig(o, w * 0.4f, a), polarFig(o, w * 0.33f, a), g * 0.6f) }
        }
        // Anillo de benceno.
        TemaId.LABORATORIO -> drawPath(figPoligono(o, w * 0.48f, 6, 0f), c, style = Stroke(g))
        // Letrero de madera clavado.
        TemaId.COLONOS -> {
            drawRoundRect(c, Offset(w * 0.05f, h * 0.1f), Size(w * 0.9f, h * 0.8f), CornerRadius(w * 0.08f), style = Stroke(g))
            drawCircle(c, w * 0.045f, Offset(w * 0.15f, h * 0.2f)); drawCircle(c, w * 0.045f, Offset(w * 0.85f, h * 0.2f))
        }
        // Corona de laurel abierta arriba.
        TemaId.NAPOLEON -> {
            drawArc(c, 100f, 140f, false, Offset(w * 0.05f, h * 0.05f), Size(w * 0.9f, h * 0.9f), style = Stroke(g * 0.6f))
            drawArc(c, 80f, -140f, false, Offset(w * 0.05f, h * 0.05f), Size(w * 0.9f, h * 0.9f), style = Stroke(g * 0.6f))
            for (k in 0..4) { drawCircle(c, w * 0.05f, polarFig(o, w * 0.45f, 110f + k * 30f)); drawCircle(c, w * 0.05f, polarFig(o, w * 0.45f, 70f - k * 30f)) }
        }
        // El octógono escalonado de un ascensor: marco cuadrado con las cuatro esquinas metidas en un escalón y un filete dentro.
        TemaId.DECO -> {
            val a = w * 0.04f; val k = w * 0.14f; val b = w - a; val ab = h - a
            val marco = Path().apply {
                moveTo(a + k, a); lineTo(b - k, a); lineTo(b - k, a + k * 0.5f); lineTo(b - k * 0.5f, a + k * 0.5f); lineTo(b - k * 0.5f, a + k); lineTo(b, a + k)
                lineTo(b, ab - k); lineTo(b - k * 0.5f, ab - k); lineTo(b - k * 0.5f, ab - k * 0.5f); lineTo(b - k, ab - k * 0.5f); lineTo(b - k, ab)
                lineTo(a + k, ab); lineTo(a + k, ab - k * 0.5f); lineTo(a + k * 0.5f, ab - k * 0.5f); lineTo(a + k * 0.5f, ab - k); lineTo(a, ab - k)
                lineTo(a, a + k); lineTo(a + k * 0.5f, a + k); lineTo(a + k * 0.5f, a + k * 0.5f); lineTo(a + k, a + k * 0.5f); close()
            }
            drawPath(marco, c, style = Stroke(g * 0.7f))
            drawRect(c.copy(alpha = 0.5f), Offset(w * 0.2f, h * 0.2f), Size(w * 0.6f, h * 0.6f), style = Stroke(g * 0.3f))
        }
        // Etiqueta de colección clavada con alfiler.
        TemaId.BOTANICO -> {
            drawRect(c, Offset(w * 0.08f, h * 0.14f), Size(w * 0.84f, h * 0.8f), style = Stroke(g * 0.6f))
            drawLine(c, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h * 0.24f), g * 0.6f)
            drawCircle(c, w * 0.07f, Offset(w * 0.5f, h * 0.06f))
        }
        // Una placa de calle atornillada, colgada de su poste.
        TemaId.RUINAS -> {
            drawRoundRect(c, Offset(w * 0.06f, h * 0.16f), Size(w * 0.88f, h * 0.7f), CornerRadius(w * 0.1f), style = Stroke(g * 0.7f))
            drawCircle(c, w * 0.04f, Offset(w * 0.18f, h * 0.27f)); drawCircle(c, w * 0.04f, Offset(w * 0.82f, h * 0.27f))
            drawLine(c, Offset(w * 0.5f, h * 0.86f), Offset(w * 0.5f, h), g * 0.8f)
        }
        // Esfera de árbol con su tapa.
        TemaId.NAVIDAD -> {
            drawCircle(c, w * 0.42f, Offset(o.x, h * 0.56f), style = Stroke(g))
            drawRect(c, Offset(w * 0.39f, h * 0.04f), Size(w * 0.22f, h * 0.11f))
        }
        // Ventana octogonal.
        TemaId.SEDA -> drawPath(figPoligono(o, w * 0.49f, 8, -67.5f), c, style = Stroke(g))
        // Marumado: la ventana redonda de las casas de té, con su celosía en la parte baja.
        TemaId.EDO -> {
            val r = w * 0.47f
            drawCircle(c, r, o, style = Stroke(g))
            val y = o.y + r * 0.25f; val media = r * kotlin.math.sqrt(1f - 0.25f * 0.25f)
            drawLine(c, Offset(o.x - media, y), Offset(o.x + media, y), g * 0.6f)
            listOf(-0.4f, 0f, 0.4f).forEach { f -> val x = o.x + f * r; val fondo = o.y + r * kotlin.math.sqrt(1f - f * f); drawLine(c, Offset(x, y), Offset(x, fondo), g * 0.6f) }
        }
        // Lanceta: el arco apuntado de la ventana.
        TemaId.GOTICO -> drawPath(Path().apply {
            moveTo(w * 0.1f, h * 0.96f); lineTo(w * 0.1f, h * 0.45f); quadraticTo(w * 0.1f, h * 0.12f, w * 0.5f, h * 0.02f)
            quadraticTo(w * 0.9f, h * 0.12f, w * 0.9f, h * 0.45f); lineTo(w * 0.9f, h * 0.96f); close()
        }, c, style = Stroke(g))
        // Un heptágono que no cierra, como el del signo.
        TemaId.ARCANO -> drawPath(Path().apply {
            for (k in 0..6) {
                val a = Math.toRadians(-90.0 + k * 360.0 / 7)
                val x = w * (0.5f + 0.46f * kotlin.math.cos(a).toFloat()); val y = h * (0.52f + 0.46f * kotlin.math.sin(a).toFloat())
                if (k == 0) moveTo(x, y) else lineTo(x, y)
            }
        }, c, style = Stroke(g))
        // Esquinas de mira de la pantalla de control.
        TemaId.ESPACIO -> {
            val l = w * 0.24f
            drawPath(Path().apply {
                moveTo(0f, l); lineTo(0f, 0f); lineTo(l, 0f); moveTo(w - l, 0f); lineTo(w, 0f); lineTo(w, l)
                moveTo(w, h - l); lineTo(w, h); lineTo(w - l, h); moveTo(l, h); lineTo(0f, h); lineTo(0f, h - l)
            }, c, style = Stroke(g * 1.4f))
        }
        // Ojo de buey: el aro grueso de la brida con sus ocho pernos.
        TemaId.ABISMO -> {
            drawCircle(c, w * 0.465f, o, style = Stroke(g * 0.6f)); drawCircle(c, w * 0.37f, o, style = Stroke(g * 0.6f))
            for (k in 0 until 8) drawCircle(c, g * 0.7f, polarFig(o, w * 0.418f, k * 45f + 22.5f))
        }
        // Estrella de ocho puntas: dos cuadrados.
        TemaId.ANDALUS -> {
            drawPath(figPoligono(o, w * 0.48f, 4, -90f), c, style = Stroke(g * 0.7f))
            drawPath(figPoligono(o, w * 0.48f, 4, -45f), c, style = Stroke(g * 0.7f))
        }
        // El cartucho de un glifo de día, con su pie de tres lóbulos.
        TemaId.MESO -> {
            drawRoundRect(c, Offset(w * 0.06f, h * 0.04f), Size(w * 0.88f, h * 0.78f), CornerRadius(w * 0.2f), style = Stroke(g * 0.8f))
            for (k in -1..1) drawCircle(c, w * 0.07f, Offset(o.x + k * w * 0.2f, h * 0.9f), style = Stroke(g * 0.6f))
        }
        // Fieltro cosido: círculo con su puntada.
        TemaId.MONGOL -> {
            drawCircle(c, w * 0.47f, o, style = Stroke(g))
            drawCircle(c, w * 0.39f, o, style = Stroke(g * 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.05f, w * 0.05f))))
        }
        // Trazo de construcción: el círculo y el cuadrado.
        TemaId.RENACIMIENTO -> {
            drawCircle(c, w * 0.48f, o, style = Stroke(g * 0.5f))
            drawRect(c, Offset(w * 0.1f, h * 0.1f), Size(w * 0.8f, h * 0.8f), style = Stroke(g * 0.5f))
        }
        // Un salvavidas de cabo: el aro grueso con sus cuatro amarres.
        TemaId.PIRATA -> {
            drawCircle(c, w * 0.44f, o, style = Stroke(g * 1.2f))
            for (k in 0 until 4) { val a = Math.PI / 4 + k * Math.PI / 2; val p = Offset(o.x + (w * 0.44f * kotlin.math.cos(a)).toFloat(), o.y + (h * 0.44f * kotlin.math.sin(a)).toFloat())
                drawCircle(c, g * 0.9f, p) }
        }
        // Cuadro constructivista: cuña en la esquina y barra abajo.
        TemaId.URSS -> {
            drawRect(c, Offset(w * 0.05f, h * 0.05f), Size(w * 0.9f, h * 0.9f), style = Stroke(g * 0.6f))
            drawPath(Path().apply { moveTo(w * 0.55f, h * 0.05f); lineTo(w * 0.95f, h * 0.05f); lineTo(w * 0.95f, h * 0.45f); close() }, c)
            drawRect(c, Offset(w * 0.05f, h * 0.86f), Size(w * 0.9f, h * 0.09f))
        }
        // Escarapela: tres anillos.
        TemaId.REVOLUCION -> {
            drawCircle(c, w * 0.47f, o, style = Stroke(g))
            drawCircle(c.copy(alpha = c.alpha * 0.45f), w * 0.41f, o, style = Stroke(g))
            drawCircle(c, w * 0.35f, o, style = Stroke(g * 0.5f))
        }
        // Piedra rúnica de cabeza redonda.
        TemaId.ANTIGUAS -> {
            drawPath(Path().apply {
                moveTo(w * 0.12f, h * 0.96f); lineTo(w * 0.12f, h * 0.42f); arcTo(Rect(w * 0.12f, h * 0.04f, w * 0.88f, h * 0.8f), 180f, 180f, false)
                lineTo(w * 0.88f, h * 0.96f); close()
            }, c, style = Stroke(g))
            drawLine(c, Offset(w * 0.3f, h * 0.9f), Offset(w * 0.7f, h * 0.9f), g * 0.5f)
        }
        // Un canto rodado con tres puntos de ocre abajo.
        TemaId.CUEVA -> {
            drawOval(c, Offset(w * 0.08f, h * 0.06f), Size(w * 0.84f, h * 0.86f), style = Stroke(g))
            for (k in -1..1) drawCircle(c, g * 0.7f, Offset(o.x + k * w * 0.14f, h * 0.8f))
        }
    }
}

// =====================================================================
//  Figuras por tema que comparten la cinta, la constelación, el calendario y Otras vidas
// =====================================================================

/** El punto a [r] de [c] en el ángulo [grados] (0° a la derecha, en el sentido del reloj). */
internal fun polarFig(c: Offset, r: Float, grados: Float): Offset {
    val a = Math.toRadians(grados.toDouble())
    return Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
}

/** Estrella de [puntas] puntas, con la primera hacia [giro] grados. */
internal fun figEstrella(c: Offset, rExt: Float, rInt: Float, puntas: Int, giro: Float = -90f): Path = Path().apply {
    for (k in 0 until puntas * 2) {
        val p = polarFig(c, if (k % 2 == 0) rExt else rInt, giro + k * 180f / puntas)
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Polígono regular de [lados] lados, con el primer vértice hacia [giro] grados. */
internal fun figPoligono(c: Offset, r: Float, lados: Int, giro: Float = -90f): Path = Path().apply {
    for (k in 0 until lados) {
        val p = polarFig(c, r, giro + k * 360f / lados)
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Círculo de borde ondulado (lacre, escarapela plisada, roseta): [lobulos] ondas que entran [hondo] veces el radio. */
internal fun figOndulado(c: Offset, r: Float, lobulos: Int, hondo: Float): Path = Path().apply {
    val n = 120
    for (k in 0..n) {
        val t = k * 2 * PI / n
        val rr = r * (1 - hondo / 2 + hondo / 2 * cos(lobulos * t)).toFloat()
        val x = c.x + rr * cos(t).toFloat(); val y = c.y + rr * sin(t).toFloat()
        if (k == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Engrane de [dientes] dientes cuadrados; el fondo de los dientes queda a [hondo] veces el radio. */
internal fun figEngrane(c: Offset, r: Float, dientes: Int, hondo: Float = 0.78f): Path = Path().apply {
    val paso = 360f / dientes
    for (k in 0 until dientes) {
        val a = k * paso - 90f
        val p = listOf(polarFig(c, r * hondo, a - paso * 0.3f), polarFig(c, r, a - paso * 0.17f), polarFig(c, r, a + paso * 0.17f), polarFig(c, r * hondo, a + paso * 0.3f))
        p.forEachIndexed { j, q -> if (k == 0 && j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
    }
    close()
}

/** La estrella de la Legión de Honor: cinco brazos, cada uno con dos puntas. */
internal fun figLegion(c: Offset, r: Float): Path = Path().apply {
    for (i in 0 until 5) {
        val a = i * 72f - 90f
        val p = listOf(polarFig(c, r * 0.34f, a - 36f), polarFig(c, r, a - 11f), polarFig(c, r * 0.74f, a), polarFig(c, r, a + 11f))
        p.forEachIndexed { j, q -> if (i == 0 && j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
    }
    close()
}

/**
 * La figura propia de cada tema, llena y de radio [r]: la estrella de la constelación, el nodo de la cinta, la marca del
 * reloj de Otras vidas y lo que lleva impreso el sello del día en el calendario. [hueco] es el color de lo calado
 * (el agujero de la moneda, el iris del ojo, las perlas del canto).
 */
internal fun DrawScope.figuraTema(tema: TemaId, color: Color, c: Offset, r: Float, hueco: Color) {
    when (tema) {
        // Un áureo: la moneda con su canto de perlas.
        TemaId.ROMANO -> {
            drawCircle(color, r, c)
            for (k in 0 until 12) drawCircle(hueco.copy(alpha = 0.6f), r * 0.08f, polarFig(c, r * 0.78f, k * 30f))
            drawCircle(hueco.copy(alpha = 0.45f), r * 0.3f, c)
        }
        // Un sampietrino visto de arriba: el adoquín de basalto puesto en rombo, gastado en las aristas, con su brillo.
        TemaId.URBE -> {
            val k = r * 0.98f; val q = r * 0.22f
            drawPath(Path().apply {
                moveTo(c.x, c.y - k + q); quadraticTo(c.x, c.y - k, c.x + q, c.y - k + q); lineTo(c.x + k - q, c.y - q); quadraticTo(c.x + k, c.y, c.x + k - q, c.y + q)
                lineTo(c.x + q, c.y + k - q); quadraticTo(c.x, c.y + k, c.x - q, c.y + k - q); lineTo(c.x - k + q, c.y + q); quadraticTo(c.x - k, c.y, c.x - k + q, c.y - q); close()
            }, color)
            drawLine(hueco.copy(alpha = 0.45f), Offset(c.x - r * 0.38f, c.y - r * 0.12f), Offset(c.x - r * 0.08f, c.y - r * 0.42f), r * 0.14f, cap = StrokeCap.Round)
        }
        // Un píxel de fósforo encendido, con su halo y la raya del barrido.
        TemaId.CIBER -> {
            drawRect(color.copy(alpha = color.alpha * 0.3f), Offset(c.x - r, c.y - r), Size(r * 2, r * 2))
            drawRect(color, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f))
            drawLine(hueco.copy(alpha = 0.5f), Offset(c.x - r * 0.72f, c.y), Offset(c.x + r * 0.72f, c.y), r * 0.14f)
        }
        // El tazón de fideos del letrero, con sus palillos.
        TemaId.NEON -> {
            drawCircle(color.copy(alpha = color.alpha * 0.3f), r, c)
            drawPath(Path().apply { moveTo(c.x - r * 0.75f, c.y - r * 0.05f); lineTo(c.x + r * 0.75f, c.y - r * 0.05f); quadraticTo(c.x + r * 0.7f, c.y + r * 0.7f, c.x, c.y + r * 0.72f); quadraticTo(c.x - r * 0.7f, c.y + r * 0.7f, c.x - r * 0.75f, c.y - r * 0.05f); close() }, color)
            drawLine(color, Offset(c.x + r * 0.1f, c.y - r * 0.15f), Offset(c.x + r * 0.7f, c.y - r * 0.75f), r * 0.12f)
            drawLine(color, Offset(c.x + r * 0.28f, c.y - r * 0.15f), Offset(c.x + r * 0.85f, c.y - r * 0.6f), r * 0.12f)
        }
        // El sol de Vergina: dieciséis rayos.
        TemaId.HELENO -> { drawPath(figEstrella(c, r, r * 0.48f, 16), color); drawCircle(hueco.copy(alpha = 0.35f), r * 0.22f, c) }
        // Un escarabajo de fayenza, con su cabeza, la raya de los élitros y las patas.
        TemaId.EGIPTO -> {
            drawOval(color, Offset(c.x - r * 0.62f, c.y - r * 0.5f), Size(r * 1.24f, r * 1.4f))
            drawOval(color, Offset(c.x - r * 0.36f, c.y - r * 0.98f), Size(r * 0.72f, r * 0.5f))
            drawLine(hueco.copy(alpha = 0.45f), Offset(c.x, c.y - r * 0.42f), Offset(c.x, c.y + r * 0.84f), r * 0.1f)
            for (s in listOf(-1f, 1f)) {
                drawLine(color, Offset(c.x + s * r * 0.5f, c.y - r * 0.1f), Offset(c.x + s * r, c.y - r * 0.45f), r * 0.14f)
                drawLine(color, Offset(c.x + s * r * 0.5f, c.y + r * 0.45f), Offset(c.x + s * r, c.y + r * 0.85f), r * 0.14f)
            }
        }
        // Sello de lacre con la cruz impresa.
        TemaId.MEDIEVAL -> {
            drawPath(figOndulado(c, r, 9, 0.16f), color)
            drawCircle(hueco.copy(alpha = 0.4f), r * 0.6f, c, style = Stroke(r * 0.1f))
            drawLine(hueco.copy(alpha = 0.5f), Offset(c.x - r * 0.34f, c.y), Offset(c.x + r * 0.34f, c.y), r * 0.11f)
            drawLine(hueco.copy(alpha = 0.5f), Offset(c.x, c.y - r * 0.34f), Offset(c.x, c.y + r * 0.34f), r * 0.11f)
        }
        // El escudo blanco y rojo: el escudo lleno y la cruz patada calada.
        TemaId.TEMPLARIO -> {
            val escudo = Path().apply { moveTo(c.x - r * 0.82f, c.y - r * 0.9f); lineTo(c.x + r * 0.82f, c.y - r * 0.9f); quadraticTo(c.x + r * 0.88f, c.y + r * 0.3f, c.x, c.y + r); quadraticTo(c.x - r * 0.88f, c.y + r * 0.3f, c.x - r * 0.82f, c.y - r * 0.9f); close() }
            drawPath(escudo, color)
            drawPath(cruzPatada(Offset(c.x, c.y - r * 0.12f), r * 0.56f), hueco.copy(alpha = 0.6f))
        }
        // El ojo que todo lo mira.
        TemaId.DISTOPIA -> {
            drawPath(Path().apply { moveTo(c.x - r, c.y); quadraticTo(c.x, c.y - r * 1.15f, c.x + r, c.y); quadraticTo(c.x, c.y + r * 1.15f, c.x - r, c.y); close() }, color)
            drawCircle(hueco, r * 0.4f, c); drawCircle(color, r * 0.2f, c)
        }
        // El átomo: núcleo y tres órbitas.
        TemaId.REFUGIO -> {
            for (a in listOf(0f, 60f, 120f)) rotate(a, c) { drawOval(color, Offset(c.x - r, c.y - r * 0.34f), Size(r * 2, r * 0.68f), style = Stroke(r * 0.13f)) }
            drawCircle(color, r * 0.3f, c)
        }
        // Un engrane.
        TemaId.ORDEN -> { drawPath(figEngrane(c, r, 8, 0.72f), color); drawCircle(hueco, r * 0.3f, c) }
        // El manómetro: esfera llena, la escala vaciada y la aguja.
        TemaId.VAPOR -> {
            drawCircle(color, r, c); drawCircle(hueco.copy(alpha = 0.7f), r * 0.72f, c, style = Stroke(r * 0.12f))
            drawLine(hueco, c, Offset(c.x + r * 0.5f, c.y - r * 0.45f), r * 0.16f, StrokeCap.Round); drawCircle(hueco, r * 0.16f, c)
        }
        // El anillo de benceno.
        TemaId.LABORATORIO -> { drawPath(figPoligono(c, r, 6, 0f), color); drawCircle(hueco.copy(alpha = 0.55f), r * 0.5f, c, style = Stroke(r * 0.12f)) }
        // Un botón de costura con sus cuatro agujeros.
        TemaId.COLONOS -> {
            drawCircle(color, r, c); drawCircle(hueco.copy(alpha = 0.35f), r * 0.8f, c, style = Stroke(r * 0.08f))
            for (dx in listOf(-1f, 1f)) for (dy in listOf(-1f, 1f)) drawCircle(hueco, r * 0.13f, Offset(c.x + dx * r * 0.26f, c.y + dy * r * 0.26f))
        }
        // La estrella de la Legión de Honor.
        TemaId.NAPOLEON -> { drawPath(figLegion(c, r), color); drawCircle(hueco.copy(alpha = 0.6f), r * 0.26f, c) }
        // El abanico solar: rayos alternos sobre su base y el medio sol vaciado.
        TemaId.DECO -> {
            val b = Offset(c.x, c.y + r * 0.45f)
            for (i in 0 until 9) if (i % 2 == 0) drawArc(color, 180f + i * 20f, 20f, true, Offset(b.x - r * 1.15f, b.y - r * 1.15f), Size(r * 2.3f, r * 2.3f))
            drawArc(color, 180f, 180f, false, Offset(b.x - r * 1.15f, b.y - r * 1.15f), Size(r * 2.3f, r * 2.3f), style = Stroke(r * 0.1f))
            drawArc(hueco.copy(alpha = 0.6f), 180f, 180f, true, Offset(b.x - r * 0.38f, b.y - r * 0.38f), Size(r * 0.76f, r * 0.76f))
            drawRect(color, Offset(b.x - r * 1.15f, b.y), Size(r * 2.3f, r * 0.2f))
        }
        // Un escarabajo clavado con alfiler.
        TemaId.BOTANICO -> {
            for (lado in listOf(-1f, 1f)) for (k in 0..2)
                drawLine(color, Offset(c.x + lado * r * 0.35f, c.y - r * 0.15f + k * r * 0.3f), Offset(c.x + lado * r * 0.88f, c.y - r * 0.4f + k * r * 0.48f), r * 0.09f)
            drawOval(color, Offset(c.x - r * 0.45f, c.y - r * 0.4f), Size(r * 0.9f, r * 1.32f))
            drawCircle(color, r * 0.26f, Offset(c.x, c.y - r * 0.6f))
            drawLine(hueco.copy(alpha = 0.5f), Offset(c.x, c.y - r * 0.32f), Offset(c.x, c.y + r * 0.88f), r * 0.06f)
            drawCircle(hueco.copy(alpha = 0.85f), r * 0.11f, Offset(c.x, c.y - r * 0.12f))
        }
        // Un semáforo con sus tres luces (la verde encendida) y una hoja de hiedra que se le enreda.
        TemaId.RUINAS -> {
            drawRoundRect(color, Offset(c.x - r * 0.42f, c.y - r * 0.95f), Size(r * 0.84f, r * 1.7f), CornerRadius(r * 0.18f))
            for (k in 0..2) drawCircle(hueco.copy(alpha = if (k == 2) 0.95f else 0.45f), r * 0.2f, Offset(c.x, c.y - r * 0.55f + k * r * 0.5f))
            drawLine(color, Offset(c.x, c.y + r * 0.75f), Offset(c.x, c.y + r), r * 0.14f)
            val q = Offset(c.x + r * 0.5f, c.y + r * 0.55f); val s = r * 0.5f
            drawPath(Path().apply {
                moveTo(q.x, q.y); lineTo(q.x - s * 0.55f, q.y - s * 0.15f); lineTo(q.x - s * 0.8f, q.y - s * 0.55f); lineTo(q.x - s * 0.32f, q.y - s * 0.55f)
                lineTo(q.x, q.y - s); lineTo(q.x + s * 0.32f, q.y - s * 0.55f); lineTo(q.x + s * 0.8f, q.y - s * 0.55f); lineTo(q.x + s * 0.55f, q.y - s * 0.15f); close()
            }, color)
        }
        // Un copo de nieve de seis brazos.
        TemaId.NAVIDAD -> {
            for (k in 0 until 6) {
                val a = k * 60f - 90f
                drawLine(color, c, polarFig(c, r, a), r * 0.16f, StrokeCap.Round)
                val q = polarFig(c, r * 0.56f, a)
                drawLine(color, q, polarFig(q, r * 0.34f, a - 50f), r * 0.11f, StrokeCap.Round)
                drawLine(color, q, polarFig(q, r * 0.34f, a + 50f), r * 0.11f, StrokeCap.Round)
            }
            drawCircle(color, r * 0.22f, c)
        }
        // La moneda de agujero cuadrado.
        TemaId.SEDA -> {
            drawCircle(color, r, c); drawCircle(hueco.copy(alpha = 0.35f), r * 0.82f, c, style = Stroke(r * 0.07f))
            drawRect(hueco, Offset(c.x - r * 0.26f, c.y - r * 0.26f), Size(r * 0.52f, r * 0.52f))
        }
        // Un mon (blasón de familia) de flor de cerezo: cinco pétalos con su muesca y el centro en reserva.
        TemaId.EDO -> {
            for (i in 0 until 5) {
                val a = -90f + 72f * i
                drawCircle(color, r * 0.46f, polarFig(c, r * 0.5f, a))
                drawCircle(hueco, r * 0.11f, polarFig(c, r * 0.97f, a))
            }
            drawCircle(hueco, r * 0.18f, c)
            for (i in 0 until 5) drawCircle(color, r * 0.05f, polarFig(c, r * 0.28f, -90f + 72f * i + 36f))
        }
        // Un rosetón de ocho lóbulos.
        TemaId.GOTICO -> {
            for (k in 0 until 8) drawCircle(color, r * 0.3f, polarFig(c, r * 0.68f, k * 45f))
            drawCircle(color, r * 0.56f, c); drawCircle(hueco.copy(alpha = 0.5f), r * 0.28f, c, style = Stroke(r * 0.08f))
        }
        // El ojo del signo, con su pupila vertical.
        TemaId.ARCANO -> {
            drawPath(Path().apply { moveTo(c.x - r, c.y); quadraticTo(c.x, c.y - r * 1.2f, c.x + r, c.y); quadraticTo(c.x, c.y + r * 1.2f, c.x - r, c.y); close() }, color)
            drawCircle(hueco.copy(alpha = 0.55f), r * 0.42f, c, style = Stroke(r * 0.1f))
            drawOval(hueco, Offset(c.x - r * 0.1f, c.y - r * 0.36f), Size(r * 0.2f, r * 0.72f))
        }
        // Una estrella vista por el telescopio: núcleo y rayos de difracción.
        TemaId.ESPACIO -> {
            fun rayo(a: Float, l: Float, ancho: Float) = Path().apply {
                val p1 = polarFig(c, l, a); val p2 = polarFig(c, ancho, a + 90f); val p3 = polarFig(c, l, a + 180f); val p4 = polarFig(c, ancho, a - 90f)
                moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close()
            }
            drawPath(rayo(-90f, r, r * 0.13f), color); drawPath(rayo(0f, r, r * 0.13f), color)
            drawPath(rayo(-45f, r * 0.55f, r * 0.08f), color); drawPath(rayo(45f, r * 0.55f, r * 0.08f), color)
            drawCircle(color, r * 0.36f, c)
        }
        // Una medusa: la campana llena, las gónadas caladas y tres tentáculos.
        TemaId.ABISMO -> {
            val y0 = c.y - r * 0.05f
            drawPath(Path().apply {
                moveTo(c.x - r * 0.95f, y0); cubicTo(c.x - r * 0.95f, y0 - r * 1.25f, c.x + r * 0.95f, y0 - r * 1.25f, c.x + r * 0.95f, y0)
                quadraticTo(c.x, y0 - r * 0.25f, c.x - r * 0.95f, y0); close()
            }, color)
            listOf(-0.35f, 0f, 0.35f).forEach { f -> drawCircle(hueco.copy(alpha = 0.5f), r * 0.12f, Offset(c.x + f * r, y0 - r * 0.42f)) }
            listOf(-0.55f, 0f, 0.55f).forEach { f ->
                val x = c.x + f * r
                drawPath(Path().apply { moveTo(x, y0 - r * 0.1f); quadraticTo(x - r * 0.25f, y0 + r * 0.5f, x, y0 + r) }, color, style = Stroke(r * 0.14f, cap = StrokeCap.Round))
            }
        }
        // La estrella de ocho puntas de los alicatados: dos cuadrados.
        TemaId.ANDALUS -> {
            drawPath(figPoligono(c, r, 4, -90f), color); drawPath(figPoligono(c, r, 4, -45f), color)
            drawCircle(hueco.copy(alpha = 0.5f), r * 0.24f, c)
        }
        // Una cuenta de jade con un numeral de punto y barra grabado.
        TemaId.MESO -> {
            drawCircle(color, r, c)
            drawRoundRect(hueco.copy(alpha = 0.5f), Offset(c.x - r * 0.5f, c.y + r * 0.08f), Size(r, r * 0.22f), CornerRadius(r * 0.08f))
            drawCircle(hueco.copy(alpha = 0.5f), r * 0.13f, Offset(c.x, c.y - r * 0.26f))
        }
        // El tono de la yurta: la rueda del techo con sus varas.
        TemaId.MONGOL -> {
            drawCircle(color, r * 0.85f, c, style = Stroke(r * 0.22f))
            for (k in 0 until 8) drawLine(color, c, polarFig(c, r * 0.85f, k * 45f + 22.5f), r * 0.1f)
            drawCircle(color, r * 0.3f, c)
        }
        // Trazo de construcción: círculo, cuadrado, ejes y la punta del compás.
        TemaId.RENACIMIENTO -> {
            drawCircle(color, r * 0.92f, c, style = Stroke(r * 0.12f))
            drawRect(color, Offset(c.x - r * 0.65f, c.y - r * 0.65f), Size(r * 1.3f, r * 1.3f), style = Stroke(r * 0.08f))
            drawLine(color, Offset(c.x - r, c.y), Offset(c.x + r, c.y), r * 0.05f); drawLine(color, Offset(c.x, c.y - r), Offset(c.x, c.y + r), r * 0.05f)
            drawCircle(color, r * 0.34f, c)
        }
        // Un doblón con su cruz de cuarteles.
        TemaId.PIRATA -> {
            drawCircle(color, r * 0.95f, c); drawCircle(hueco.copy(alpha = 0.45f), r * 0.75f, c, style = Stroke(r * 0.08f))
            drawLine(hueco, Offset(c.x, c.y - r * 0.6f), Offset(c.x, c.y + r * 0.6f), r * 0.16f); drawLine(hueco, Offset(c.x - r * 0.6f, c.y), Offset(c.x + r * 0.6f, c.y), r * 0.16f)
        }
        // La estrella roja de cinco puntas.
        TemaId.URSS -> drawPath(figEstrella(c, r, r * 0.4f, 5), color)
        // La escarapela plisada.
        TemaId.REVOLUCION -> {
            drawPath(figOndulado(c, r, 14, 0.14f), color)
            drawCircle(hueco.copy(alpha = 0.85f), r * 0.62f, c, style = Stroke(r * 0.2f)); drawCircle(color, r * 0.3f, c)
        }
        // Una gema tallada, vista de lado.
        TemaId.ANTIGUAS -> {
            drawPath(Path().apply {
                moveTo(c.x - r * 0.45f, c.y - r * 0.72f); lineTo(c.x + r * 0.45f, c.y - r * 0.72f); lineTo(c.x + r * 0.9f, c.y - r * 0.2f)
                lineTo(c.x, c.y + r); lineTo(c.x - r * 0.9f, c.y - r * 0.2f); close()
            }, color)
            val f = hueco.copy(alpha = 0.45f); val g = r * 0.06f
            drawLine(f, Offset(c.x - r * 0.9f, c.y - r * 0.2f), Offset(c.x + r * 0.9f, c.y - r * 0.2f), g)
            drawLine(f, Offset(c.x - r * 0.22f, c.y - r * 0.72f), Offset(c.x - r * 0.4f, c.y - r * 0.2f), g)
            drawLine(f, Offset(c.x + r * 0.22f, c.y - r * 0.72f), Offset(c.x + r * 0.4f, c.y - r * 0.2f), g)
            drawLine(f, Offset(c.x - r * 0.4f, c.y - r * 0.2f), Offset(c.x, c.y + r), g)
            drawLine(f, Offset(c.x + r * 0.4f, c.y - r * 0.2f), Offset(c.x, c.y + r), g)
        }
        // La mano en negativo: la nube de ocre y la mano, del color del hueco.
        TemaId.CUEVA -> {
            drawCircle(color, r, c)
            drawPath(manoUnida(Offset(c.x, c.y + r * 0.04f), r * 0.66f), hueco.copy(alpha = 0.85f))
        }
    }
}

fun DrawScope.iconoPista(id: String, c: Color) {
    val w = size.width; val h = size.height
    val t = Stroke(w * 0.09f, cap = StrokeCap.Round)
    val o = Offset(w / 2, h / 2)
    when (id) {
        "sueno" -> mediaLuna(Offset(w * 0.46f, h * 0.5f), h * 0.4f, c)
        "enfoque" -> {
            drawCircle(c, w * 0.4f, o, style = t)
            drawLine(c, o, Offset(o.x, h * 0.24f), t.width, StrokeCap.Round)
            drawLine(c, o, Offset(w * 0.7f, o.y), t.width, StrokeCap.Round)
        }
        "lugares" -> {
            drawPath(Path().apply { moveTo(w * 0.1f, h * 0.48f); lineTo(w * 0.5f, h * 0.12f); lineTo(w * 0.9f, h * 0.48f) }, c, style = t)
            drawRect(c, Offset(w * 0.22f, h * 0.46f), Size(w * 0.56f, h * 0.42f), style = t)
        }
        "ejercicio" -> drawPath(Path().apply { moveTo(w * 0.06f, h * 0.86f); lineTo(w * 0.38f, h * 0.24f); lineTo(w * 0.56f, h * 0.56f); lineTo(w * 0.7f, h * 0.38f); lineTo(w * 0.94f, h * 0.86f); close() }, c, style = t)
        "pantalla" -> {
            drawRoundRect(c, Offset(w * 0.28f, h * 0.06f), Size(w * 0.44f, h * 0.88f), CornerRadius(w * 0.08f), style = t)
            drawLine(c, Offset(w * 0.44f, h * 0.8f), Offset(w * 0.56f, h * 0.8f), t.width, StrokeCap.Round)
        }
        "avisos" -> {
            drawPath(Path().apply {
                moveTo(w * 0.2f, h * 0.74f); quadraticTo(w * 0.28f, h * 0.62f, w * 0.28f, h * 0.44f); quadraticTo(w * 0.3f, h * 0.14f, w * 0.5f, h * 0.14f)
                quadraticTo(w * 0.7f, h * 0.14f, w * 0.72f, h * 0.44f); quadraticTo(w * 0.72f, h * 0.62f, w * 0.8f, h * 0.74f); close()
            }, c, style = t)
            drawCircle(c, w * 0.07f, Offset(w * 0.5f, h * 0.86f))
        }
        "luz" -> {
            drawCircle(c, w * 0.2f, o, style = t)
            for (k in 0 until 8) {
                val a = k * PI / 4
                drawLine(c, Offset(o.x + (w * 0.32f) * cos(a).toFloat(), o.y + (h * 0.32f) * sin(a).toFloat()),
                    Offset(o.x + (w * 0.45f) * cos(a).toFloat(), o.y + (h * 0.45f) * sin(a).toFloat()), t.width, StrokeCap.Round)
            }
        }
        "musica" -> {
            drawCircle(c, w * 0.13f, Offset(w * 0.32f, h * 0.76f))
            drawCircle(c, w * 0.13f, Offset(w * 0.74f, h * 0.66f))
            drawLine(c, Offset(w * 0.44f, h * 0.76f), Offset(w * 0.44f, h * 0.18f), t.width, StrokeCap.Round)
            drawLine(c, Offset(w * 0.86f, h * 0.66f), Offset(w * 0.86f, h * 0.1f), t.width, StrokeCap.Round)
            drawLine(c, Offset(w * 0.44f, h * 0.18f), Offset(w * 0.86f, h * 0.1f), t.width, StrokeCap.Round)
        }
        "momentos" -> {
            val r = w * 0.44f
            drawPath(Path().apply {
                moveTo(o.x, o.y - r); quadraticTo(o.x, o.y, o.x + r, o.y); quadraticTo(o.x, o.y, o.x, o.y + r)
                quadraticTo(o.x, o.y, o.x - r, o.y); quadraticTo(o.x, o.y, o.x, o.y - r); close()
            }, c)
        }
        "comidas" -> {
            drawPath(Path().apply { moveTo(w * 0.08f, h * 0.46f); lineTo(w * 0.92f, h * 0.46f); quadraticTo(w * 0.88f, h * 0.88f, w * 0.5f, h * 0.88f); quadraticTo(w * 0.12f, h * 0.88f, w * 0.08f, h * 0.46f); close() }, c, style = t)
            drawLine(c, Offset(w * 0.4f, h * 0.36f), Offset(w * 0.62f, h * 0.1f), t.width, StrokeCap.Round)
        }
        "gastos" -> {
            drawCircle(c, w * 0.4f, o, style = t)
            drawLine(c, Offset(o.x, h * 0.22f), Offset(o.x, h * 0.78f), t.width * 0.8f, StrokeCap.Round)
            drawPath(Path().apply { moveTo(w * 0.64f, h * 0.36f); quadraticTo(w * 0.5f, h * 0.28f, w * 0.38f, h * 0.38f); quadraticTo(w * 0.36f, h * 0.5f, w * 0.5f, h * 0.5f)
                quadraticTo(w * 0.64f, h * 0.52f, w * 0.62f, h * 0.62f); quadraticTo(w * 0.5f, h * 0.72f, w * 0.36f, h * 0.64f) }, c, style = Stroke(t.width * 0.8f, cap = StrokeCap.Round))
        }
        "fotos" -> {
            drawRoundRect(c, Offset(w * 0.08f, h * 0.28f), Size(w * 0.84f, h * 0.6f), CornerRadius(w * 0.08f), style = t)
            drawRect(c, Offset(w * 0.34f, h * 0.16f), Size(w * 0.32f, h * 0.12f))
            drawCircle(c, w * 0.16f, Offset(o.x, h * 0.58f), style = t)
        }
        "sustancias" -> {
            // Una copa.
            drawPath(Path().apply { moveTo(w * 0.22f, h * 0.1f); lineTo(w * 0.78f, h * 0.1f); quadraticTo(w * 0.78f, h * 0.5f, w * 0.5f, h * 0.52f); quadraticTo(w * 0.22f, h * 0.5f, w * 0.22f, h * 0.1f); close() }, c, style = t)
            drawLine(c, Offset(w * 0.5f, h * 0.52f), Offset(w * 0.5f, h * 0.86f), t.width, StrokeCap.Round)
            drawLine(c, Offset(w * 0.3f, h * 0.88f), Offset(w * 0.7f, h * 0.88f), t.width, StrokeCap.Round)
        }
        "cafeina" -> {
            drawPath(Path().apply { moveTo(w * 0.14f, h * 0.34f); lineTo(w * 0.7f, h * 0.34f); lineTo(w * 0.64f, h * 0.88f); lineTo(w * 0.2f, h * 0.88f); close() }, c, style = t)
            drawArc(c, -90f, 180f, false, Offset(w * 0.6f, h * 0.44f), Size(w * 0.26f, h * 0.24f), style = t)
            drawLine(c, Offset(w * 0.34f, h * 0.22f), Offset(w * 0.38f, h * 0.06f), t.width * 0.7f, StrokeCap.Round)
            drawLine(c, Offset(w * 0.5f, h * 0.22f), Offset(w * 0.54f, h * 0.06f), t.width * 0.7f, StrokeCap.Round)
        }
        // Los del reporte del día (Día → «Lo que registró el teléfono»).
        "pasos" -> {
            drawOval(c, Offset(w * 0.14f, h * 0.08f), Size(w * 0.26f, h * 0.4f), style = t)
            drawCircle(c, w * 0.09f, Offset(w * 0.27f, h * 0.6f))
            drawOval(c, Offset(w * 0.58f, h * 0.3f), Size(w * 0.26f, h * 0.4f), style = t)
            drawCircle(c, w * 0.09f, Offset(w * 0.71f, h * 0.82f))
        }
        "corazon" -> drawPath(Path().apply {
            moveTo(w * 0.5f, h * 0.86f)
            cubicTo(w * 0.04f, h * 0.56f, w * 0.1f, h * 0.1f, w * 0.5f, h * 0.32f)
            cubicTo(w * 0.9f, h * 0.1f, w * 0.96f, h * 0.56f, w * 0.5f, h * 0.86f); close()
        }, c, style = t)
        "clima" -> {
            drawCircle(c, w * 0.2f, o, style = t)
            for (k in 0 until 8) {
                val a = Math.toRadians(k * 45.0)
                val x = kotlin.math.cos(a).toFloat(); val y = kotlin.math.sin(a).toFloat()
                drawLine(c, Offset(o.x + x * w * 0.3f, o.y + y * h * 0.3f), Offset(o.x + x * w * 0.44f, o.y + y * h * 0.44f), t.width * 0.8f, StrokeCap.Round)
            }
        }
        "luna" -> {
            drawCircle(c, w * 0.38f, o, style = t)
            drawArc(c, 90f, 180f, true, Offset(w * 0.12f, h * 0.12f), Size(w * 0.76f, h * 0.76f))
        }
        "voces" -> {
            drawRoundRect(c, Offset(w * 0.1f, h * 0.14f), Size(w * 0.8f, h * 0.54f), androidx.compose.ui.geometry.CornerRadius(w * 0.16f), style = t)
            drawPath(Path().apply { moveTo(w * 0.3f, h * 0.66f); lineTo(w * 0.24f, h * 0.9f); lineTo(w * 0.5f, h * 0.68f) }, c, style = t)
        }
        "gimnasio" -> {
            drawLine(c, Offset(w * 0.2f, o.y), Offset(w * 0.8f, o.y), t.width, StrokeCap.Round)
            drawRect(c, Offset(w * 0.12f, h * 0.28f), Size(w * 0.14f, h * 0.44f))
            drawRect(c, Offset(w * 0.74f, h * 0.28f), Size(w * 0.14f, h * 0.44f))
        }
        else -> drawCircle(c, w * 0.3f, o, style = t)
    }
}
