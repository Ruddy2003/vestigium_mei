package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ---------------------------------------------------------------- Trazos

/** Una línea suave que pasa por todos los [puntos] (curvas de Catmull-Rom). Con [cerrar], baja hasta [fondo] y cierra. */
fun curva(puntos: List<Offset>, fondo: Float? = null): Path {
    val p = Path()
    if (puntos.isEmpty()) return p
    p.moveTo(puntos[0].x, puntos[0].y)
    for (i in 0 until puntos.size - 1) {
        val a = puntos[maxOf(i - 1, 0)]; val b = puntos[i]; val c = puntos[i + 1]; val d = puntos[minOf(i + 2, puntos.size - 1)]
        p.cubicTo(b.x + (c.x - a.x) / 6f, b.y + (c.y - a.y) / 6f, c.x - (d.x - b.x) / 6f, c.y - (d.y - b.y) / 6f, c.x, c.y)
    }
    if (fondo != null) { p.lineTo(puntos.last().x, fondo); p.lineTo(puntos.first().x, fondo); p.close() }
    return p
}

/** Un polígono cerrado con los [puntos] dados. */
fun poligono(puntos: List<Offset>): Path = Path().apply {
    moveTo(puntos[0].x, puntos[0].y)
    for (i in 1 until puntos.size) lineTo(puntos[i].x, puntos[i].y)
    close()
}

// `Offset` es una clase de valor y no admite `vararg`: por eso van las tres formas más usadas.
fun poligono(a: Offset, b: Offset, c: Offset): Path = poligono(listOf(a, b, c))
fun poligono(a: Offset, b: Offset, c: Offset, d: Offset): Path = poligono(listOf(a, b, c, d))
fun poligono(a: Offset, b: Offset, c: Offset, d: Offset, e: Offset): Path = poligono(listOf(a, b, c, d, e))

// ---------------------------------------------------------------- Montes

/**
 * El perfil de una sierra de [x0] a [x1]: la cresta ondula entre [base] (abajo) y [base] − [alto] (más arriba = menos
 * y). [quebrado], de 0 (lomas) a 1 (picos). Con [isla], los dos extremos bajan hasta [base]: una isla, un cabo o un
 * cerro suelto. Sale siempre igual con la misma semilla.
 */
fun perfil(azar: Azar, x0: Float, x1: Float, base: Float, alto: Float, quebrado: Float = 0.4f, pasos: Int = 22, isla: Boolean = false): List<Offset> {
    val f1 = azar.entre(0f, 6.28f); val f2 = azar.entre(0f, 6.28f); val f3 = azar.entre(0f, 6.28f)
    val o1 = azar.entre(1.2f, 2.2f); val o2 = azar.entre(3.1f, 4.8f); val o3 = azar.entre(7f, 11f)
    val pi = PI.toFloat()
    return (0..pasos).map { i ->
        val k = i / pasos.toFloat()
        val onda = 0.56f + 0.26f * sin(k * pi * o1 + f1) + 0.14f * sin(k * pi * o2 + f2) + quebrado * 0.12f * sin(k * pi * o3 + f3) + (azar.real() - 0.5f) * quebrado * 0.14f
        val orilla = if (isla) sin(pi * k).coerceAtLeast(0f).let { kotlin.math.sqrt(it) } else 1f
        Offset(x0 + (x1 - x0) * k, base - alto * onda.coerceIn(0.1f, 1f) * orilla)
    }
}

/**
 * Una sierra o una loma con el [perfil] dado, rellena hasta [fondo]: más clara y azulada cuanto más [lejos] (0 a 1).
 * Las laderas que miran al sol toman luz y las otras quedan en sombra; al pie se junta la bruma.
 */
fun DrawScope.monte(l: Luces, cresta: List<Offset>, fondo: Float, propio: Color, lejos: Float) {
    val arriba = cresta.minOf { it.y }
    val c = l.tono(propio, lejos)
    val forma = curva(cresta, fondo)
    drawPath(forma, Brush.verticalGradient(listOf(lerp(c, l.tono(Color.White, lejos), 0.10f), c, lerp(c, l.bruma, 0.25f * lejos)), arriba, fondo))
    val r = Rayo(l)
    val cae = if (r.lat >= 0f) -1f else 1f                                      // hacia dónde cae la sombra
    val sombra = lerp(c, l.tono(Color(0xFF4A4C7A), lejos), 0.55f).copy(alpha = 0.10f + 0.30f * r.dia)
    val luz = l.tono(lerp(propio, Color(0xFFFFE8C0), 0.6f), lejos).copy(alpha = 0.34f * r.dia)
    clipPath(forma) {
        for (i in 1 until cresta.size - 1) {
            val p = cresta[i]
            if (p.y <= cresta[i - 1].y && p.y < cresta[i + 1].y) {
                val h = fondo - p.y
                drawPath(poligono(p, Offset(p.x + cae * (h * 1.5f + 5f), fondo), Offset(p.x + cae * h * 0.12f, fondo)), sombra)
                drawPath(poligono(p, Offset(p.x - cae * (h * 1.1f + 4f), fondo), Offset(p.x - cae * h * 0.1f, fondo)), luz)
            }
        }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.bruma.copy(alpha = (0.6f * lejos).coerceIn(0f, 1f))), arriba + (fondo - arriba) * 0.4f, fondo),
            Offset(cresta.first().x, arriba), Size(cresta.last().x - cresta.first().x, fondo - arriba))
    }
}

// ---------------------------------------------------------------- El color del campo

/**
 * El color de la hierba y del monte bajo de un lugar en cada estación. Con [mediterraneo], el verano es pajizo y el
 * invierno, verde (llueve en invierno); si no, el invierno es pardo y el verano, verde.
 */
fun hierbaDe(estacion: Estacion, mediterraneo: Boolean = false): Color = when (estacion) {
    Estacion.PRIMAVERA -> Color(0xFF86A84C)
    Estacion.VERANO -> if (mediterraneo) Color(0xFFC9B26A) else Color(0xFF6F9A45)
    Estacion.OTONO -> if (mediterraneo) Color(0xFFB8A262) else Color(0xFFA89A52)
    Estacion.INVIERNO -> if (mediterraneo) Color(0xFF8A9C58) else Color(0xFF9A9274)
    Estacion.SECAS -> Color(0xFFC6AE6C)
    Estacion.LLUVIAS -> Color(0xFF5E9A48)
    Estacion.INUNDACION -> Color(0xFF7FA35A)
    Estacion.SIEMBRA -> Color(0xFF7FA84E)
    Estacion.COSECHA -> Color(0xFFC8B060)
    Estacion.NINGUNA -> Color(0xFF86A050)
}

/** El color de las hojas de un árbol de hoja caduca en cada estación; null cuando está pelado. */
fun hojaDe(estacion: Estacion): Color? = when (estacion) {
    Estacion.PRIMAVERA -> Color(0xFF8DB84E)
    Estacion.VERANO -> Color(0xFF4F8A3C)
    Estacion.OTONO -> Color(0xFFC9842E)
    Estacion.INVIERNO -> null
    Estacion.SECAS -> Color(0xFF8A9A4A)
    Estacion.LLUVIAS -> Color(0xFF3F8A3E)
    Estacion.INUNDACION -> Color(0xFF5E9444)
    Estacion.SIEMBRA -> Color(0xFF6FA548)
    Estacion.COSECHA -> Color(0xFF8E9C46)
    Estacion.NINGUNA -> Color(0xFF5A9040)
}

// ---------------------------------------------------------------- Cultivos

/**
 * El color de un sembrado de grano según su [etapa] (el arco «cultivo» del calendario): 0 es el rastrojo o la tierra
 * arada; hacia 0.3 brota en verde; hacia 0.6 está alto y verde; en 1 está dorado, listo para segar.
 */
fun colorSembrado(etapa: Float): Color {
    val tierra = Color(0xFFA98A5E); val brote = Color(0xFF8EAA4C); val verde = Color(0xFF5E9A3E); val oro = Color(0xFFE0BC55)
    val e = etapa.coerceIn(0f, 1f)
    return when {
        e < 0.3f -> lerp(tierra, brote, e / 0.3f)
        e < 0.6f -> lerp(brote, verde, (e - 0.3f) / 0.3f)
        else -> lerp(verde, oro, (e - 0.6f) / 0.4f)
    }
}

/**
 * Un sembrado dentro del [contorno]: el color de su [etapa] y, si está cerca ([lejos] menor que 0.5), los surcos o
 * las espigas. [quemado], de 0 a 1: cuánto del campo está arrasado (negro, con rodales). [gavillas]: si está segado,
 * con sus haces en pie.
 */
fun DrawScope.sembrado(l: Luces, contorno: Path, limites: Rect, etapa: Float, lejos: Float, azar: Azar, quemado: Float = 0f, gavillas: Boolean = false) {
    // Un campo arrasado toma el color de la ceniza; cuanto más quemado, más.
    val propio = lerp(colorSembrado(etapa), Color(0xFF5E5248), 0.8f * quemado.coerceIn(0f, 1f))
    val base = l.tono(propio, lejos)
    clipPath(contorno) {
        drawRect(Brush.verticalGradient(listOf(lerp(base, l.tono(Color.White, lejos), 0.08f), base), limites.top, limites.bottom), limites.topLeft, limites.size)
        // Surcos: líneas que siguen el campo, más juntas cuanto más lejos.
        val paso = 2.2f + 5f * (1f - lejos)
        val raya = l.tono(lerp(colorSembrado(etapa), Color(0xFF4A3A22), 0.35f), lejos).copy(alpha = 0.30f - 0.18f * lejos)
        val inclinacion = azar.entre(-0.5f, 0.5f)
        var x = limites.left - limites.height
        while (x < limites.right + limites.height) {
            drawLine(raya, Offset(x, limites.top), Offset(x + inclinacion * limites.height, limites.bottom), 0.5f + 0.5f * (1f - lejos))
            x += paso
        }
        if (etapa > 0.75f && lejos < 0.6f && quemado < 0.3f) repeat((limites.width * limites.height / 26f).toInt().coerceAtMost(160)) {
            // Espigas: toques claros.
            val p = Offset(azar.entre(limites.left, limites.right), azar.entre(limites.top, limites.bottom))
            drawLine(l.tono(Color(0xFFF4DE8A), lejos).copy(alpha = 0.6f), p, p + Offset(0.6f, -2.2f * (1f - lejos * 0.6f)), 0.6f)
        }
        if (quemado > 0.02f) {
            // Rodales negros, donde ardió más.
            val negro = l.tono(Color(0xFF2B2420), lejos * 0.8f)
            repeat((1 + quemado * 4).toInt()) {
                val c = Offset(azar.entre(limites.left, limites.right), azar.entre(limites.top, limites.bottom))
                val r = limites.width * azar.entre(0.06f, 0.14f)
                drawOval(negro.copy(alpha = 0.3f + 0.25f * quemado), Offset(c.x - r, c.y - r * 0.3f), Size(r * 2f, r * 0.6f))
            }
        }
        if (gavillas) repeat((limites.width / (5f + 10f * (1f - lejos))).toInt().coerceIn(2, 14)) {
            val p = Offset(azar.entre(limites.left + 2f, limites.right - 2f), azar.entre(limites.top + 1f, limites.bottom))
            val h = 2.2f + 4f * (1f - lejos)
            drawPath(poligono(p + Offset(-h * 0.35f, 0f), p + Offset(h * 0.35f, 0f), p + Offset(h * 0.12f, -h), p + Offset(-h * 0.12f, -h)), l.tono(Color(0xFFE6C766), lejos * 0.8f))
            drawLine(l.tono(Color(0xFF8A6A2E), lejos), p + Offset(-h * 0.2f, -h * 0.55f), p + Offset(h * 0.2f, -h * 0.55f), 0.5f)
        }
    }
}

// ---------------------------------------------------------------- Árboles

private fun DrawScope.copa(l: Luces, centro: Offset, rx: Float, ry: Float, hoja: Color, lejos: Float, azar: Azar, bolas: Int) {
    val luz = l.tono(lerp(hoja, Color(0xFFF2F0C0), 0.30f), lejos); val medio = l.tono(hoja, lejos); val sombra = l.tono(lerp(hoja, Color(0xFF1E2A26), 0.42f), lejos)
    // De atrás hacia delante: sombra, cuerpo y toques de luz arriba, del lado del sol.
    val lado = if (l.solX > centro.x / ANCHO) 1f else -1f
    repeat(bolas) {
        val a = azar.entre(0f, 6.28f); val d = azar.real()
        val p = centro + Offset(cos(a) * rx * 0.62f * d, sin(a) * ry * 0.55f * d + ry * 0.12f)
        drawCircle(sombra, minOf(rx, ry) * azar.entre(0.42f, 0.62f), p)
    }
    repeat(bolas) {
        val a = azar.entre(0f, 6.28f); val d = azar.real()
        val p = centro + Offset(cos(a) * rx * 0.6f * d + lado * rx * 0.05f, sin(a) * ry * 0.5f * d - ry * 0.05f)
        drawCircle(medio, minOf(rx, ry) * azar.entre(0.36f, 0.55f), p)
    }
    repeat(bolas * 2 / 3 + 1) {
        val a = azar.entre(3.4f, 6.0f); val d = azar.real()
        val p = centro + Offset(cos(a) * rx * 0.55f * d + lado * rx * 0.2f, sin(a) * ry * 0.55f * d - ry * 0.12f)
        drawCircle(luz, minOf(rx, ry) * azar.entre(0.2f, 0.36f), p)
    }
}

private fun DrawScope.rama(l: Luces, desde: Offset, angulo: Float, largo: Float, grueso: Float, color: Color, azar: Azar, nivel: Int) {
    val fin = desde + Offset(cos(angulo) * largo, sin(angulo) * largo)
    val medio = Offset((desde.x + fin.x) / 2f + azar.entre(-1f, 1f) * largo * 0.08f, (desde.y + fin.y) / 2f)
    drawPath(Path().apply { moveTo(desde.x, desde.y); cubicTo(medio.x, medio.y, medio.x, medio.y, fin.x, fin.y) }, color, style = Stroke(grueso, cap = StrokeCap.Round))
    if (nivel > 0) {
        rama(l, fin, angulo - azar.entre(0.35f, 0.7f), largo * azar.entre(0.55f, 0.68f), grueso * 0.62f, color, azar, nivel - 1)
        rama(l, fin, angulo + azar.entre(0.3f, 0.65f), largo * azar.entre(0.55f, 0.68f), grueso * 0.62f, color, azar, nivel - 1)
        if (azar.real() > 0.5f) rama(l, medio, angulo + azar.entre(-0.9f, 0.9f), largo * 0.5f, grueso * 0.5f, color, azar, nivel - 1)
    }
}

/**
 * Un árbol de hoja caduca con el [pie] en el suelo y [alto] de altura: verde tierno en primavera, verde hondo en
 * verano, ocre en otoño y pelado en invierno ([hojaDe]). Con [hoja] se le da otro color de hoja (un frutal en flor).
 */
fun DrawScope.arbol(l: Luces, pie: Offset, alto: Float, estacion: Estacion, lejos: Float, azar: Azar, hoja: Color? = hojaDe(estacion), tronco: Color = Color(0xFF5E4630)) {
    val corteza = l.tono(tronco, lejos)
    val cruz = pie + Offset(azar.entre(-0.03f, 0.03f) * alto, -alto * 0.42f)
    drawPath(poligono(pie + Offset(-alto * 0.045f, 0f), pie + Offset(alto * 0.045f, 0f), cruz + Offset(alto * 0.028f, 0f), cruz + Offset(-alto * 0.028f, 0f)), corteza)
    val niveles = if (lejos > 0.5f) 2 else 3
    rama(l, cruz, -1.57f - 0.55f, alto * 0.19f, alto * 0.04f, corteza, azar, niveles)
    rama(l, cruz, -1.57f + 0.5f, alto * 0.20f, alto * 0.04f, corteza, azar, niveles)
    rama(l, cruz, -1.57f, alto * 0.22f, alto * 0.045f, corteza, azar, niveles)
    if (hoja != null) copa(l, pie + Offset(0f, -alto * 0.68f), alto * 0.36f, alto * 0.32f, hoja, lejos, azar, if (lejos > 0.5f) 6 else 12)
}

/** Un olivo: tronco retorcido y copa ancha de un verde plateado, igual todo el año. */
fun DrawScope.olivo(l: Luces, pie: Offset, alto: Float, lejos: Float, azar: Azar) {
    val corteza = l.tono(Color(0xFF6A5A48), lejos)
    val cruz = pie + Offset(alto * 0.06f, -alto * 0.40f)
    drawPath(Path().apply {
        moveTo(pie.x - alto * 0.08f, pie.y)
        cubicTo(pie.x - alto * 0.02f, pie.y - alto * 0.18f, pie.x - alto * 0.10f, pie.y - alto * 0.28f, cruz.x - alto * 0.04f, cruz.y)
        lineTo(cruz.x + alto * 0.04f, cruz.y)
        cubicTo(pie.x + alto * 0.02f, pie.y - alto * 0.26f, pie.x + alto * 0.10f, pie.y - alto * 0.14f, pie.x + alto * 0.09f, pie.y)
        close()
    }, corteza)
    rama(l, cruz, -1.57f - 0.7f, alto * 0.24f, alto * 0.045f, corteza, azar, 1)
    rama(l, cruz, -1.57f + 0.6f, alto * 0.26f, alto * 0.045f, corteza, azar, 1)
    copa(l, pie + Offset(alto * 0.04f, -alto * 0.66f), alto * 0.50f, alto * 0.30f, Color(0xFF7E9470), lejos, azar, if (lejos > 0.5f) 7 else 14)
}

/** Un ciprés: una llama oscura y estrecha. */
fun DrawScope.cipres(l: Luces, pie: Offset, alto: Float, lejos: Float) {
    val ancho = alto * 0.13f
    val cuerpo = Path().apply {
        moveTo(pie.x, pie.y - alto)
        cubicTo(pie.x + ancho * 0.9f, pie.y - alto * 0.6f, pie.x + ancho * 1.25f, pie.y - alto * 0.2f, pie.x + ancho * 0.5f, pie.y - alto * 0.05f)
        lineTo(pie.x - ancho * 0.5f, pie.y - alto * 0.05f)
        cubicTo(pie.x - ancho * 1.25f, pie.y - alto * 0.2f, pie.x - ancho * 0.9f, pie.y - alto * 0.6f, pie.x, pie.y - alto)
        close()
    }
    drawRect(l.tono(Color(0xFF4E3C2A), lejos), Offset(pie.x - ancho * 0.12f, pie.y - alto * 0.08f), Size(ancho * 0.24f, alto * 0.08f))
    val oscuro = l.tono(Color(0xFF24402E), lejos); val claro = l.tono(Color(0xFF3F6442), lejos)
    val solALaDerecha = l.solX > pie.x / ANCHO
    drawPath(cuerpo, Brush.horizontalGradient(if (solALaDerecha) listOf(oscuro, claro) else listOf(claro, oscuro), pie.x - ancho, pie.x + ancho))
}

/** Un pino de copa en pisos (abeto): para el norte y la montaña. */
fun DrawScope.pino(l: Luces, pie: Offset, alto: Float, lejos: Float, nevado: Boolean = false) {
    drawRect(l.tono(Color(0xFF4E3A28), lejos), Offset(pie.x - alto * 0.025f, pie.y - alto * 0.16f), Size(alto * 0.05f, alto * 0.16f))
    val oscuro = l.tono(Color(0xFF2A4A36), lejos); val claro = l.tono(Color(0xFF41694A), lejos)
    for (i in 0 until 4) {
        val y0 = pie.y - alto * (0.12f + 0.2f * i); val w = alto * (0.26f - 0.05f * i)
        drawPath(poligono(Offset(pie.x - w, y0), Offset(pie.x + w, y0), Offset(pie.x, y0 - alto * 0.34f)), if (i % 2 == 0) oscuro else claro)
        if (nevado) drawPath(poligono(Offset(pie.x - w * 0.55f, y0 - alto * 0.14f), Offset(pie.x + w * 0.55f, y0 - alto * 0.14f), Offset(pie.x, y0 - alto * 0.34f)), l.tono(Color(0xFFF4F7FA), lejos))
    }
}

/** Una mata o un arbusto bajo. */
fun DrawScope.mata(l: Luces, pie: Offset, ancho: Float, propio: Color, lejos: Float, azar: Azar) {
    copa(l, pie + Offset(0f, -ancho * 0.28f), ancho * 0.55f, ancho * 0.34f, propio, lejos, azar, 5)
}

/** Briznas de hierba a lo largo de una línea del suelo, de [x0] a [x1] a la altura [y]. */
fun DrawScope.briznas(l: Luces, x0: Float, x1: Float, y: Float, propio: Color, azar: Azar, alto: Float = 5f, cada: Float = 3f) {
    val c = l.tono(propio); val c2 = l.tono(lerp(propio, Color(0xFF26301E), 0.3f))
    var x = x0
    while (x < x1) {
        val h = alto * azar.entre(0.5f, 1.2f); val inclina = azar.entre(-0.4f, 0.4f) * h
        drawLine(if (azar.real() > 0.5f) c else c2, Offset(x, y + 0.5f), Offset(x + inclina, y - h), 0.8f, StrokeCap.Round)
        x += cada * azar.entre(0.5f, 1.5f)
    }
}
