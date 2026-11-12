package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import kotlin.math.abs
import kotlin.math.sqrt

// =====================================================================
//  El dibujo del gato del cuadro (tanda P5a)
// =====================================================================
//
// Pinta el cuerpo sobre una postura del esqueleto (`GatoEsqueleto.kt`), al gouache, como las escenas: formas de color
// opaco, un lado con luz y otro en sombra, y su sombra en el suelo. La luz no es suya: se la da la escena en la que
// está ([LuzGato]), así que el mismo gato se ve dorado al atardecer, azulado de noche y magenta junto al letrero.
//
// Cómo se pinta: el cuerpo entero va en una capa aparte, con todas sus partes del mismo color (así las juntas no se
// notan); encima, y solo donde ya hay gato, van el dibujo del pelaje y la luz; al final, la cara.

/**
 * La luz que le llega al gato en el sitio donde pisa. La da cada escena (`EscenaVida.luzDelGato`).
 *
 * - [rayo]: de qué lado viene la luz y cómo es la sombra que arroja.
 * - [noche]: de 0 a 1; de noche le brillan los ojos.
 * - [brillo]: el color de la luz que le da en el lomo (crema con el sol; el del letrero, junto a un letrero).
 * - [tono]: el color de una cosa con la luz de ese sitio; es `Luces.tono`, o `Alumbrado.en` en una escena de focos.
 */
class LuzGato(val rayo: Rayo, val noche: Float, val brillo: Color = Color(0xFFFFF0D0), val tono: (Color) -> Color)

/** El dibujo del pelaje. */
enum class DibujoPelaje {
    /** De un solo color. */
    LISO,
    /** Rayas en el lomo, anillos en la cola y en las patas. */
    ATIGRADO,
    /** Manchas grandes de dos colores. */
    MANCHAS,
    /** El pecho, el hocico y las cuatro patas claros. */
    BICOLOR,
    /** La cara, las orejas, las patas y la cola oscuras. */
    PUNTAS,
}

/** Los sitios del gato en una postura, para colgarle el accesorio o ponerle algo en las manos. */
class PuntosGato(val cadera: Offset, val hombro: Offset, val cuello: Offset, val cabeza: Offset, val lomo: Offset, val manoC: Offset, val puntaCola: Offset, val frente: Boolean)

/**
 * Una de las dos acciones propias de un tema (tocar la lira, sostener una vela). [sentado]: si parte de estar sentado
 * (si no, de estar de pie). [cuadros] y [pose]: sus posturas. [utileria]: lo que lleva en las manos o tiene delante.
 */
class EspecialGato(val nombre: String, val sentado: Boolean, val cuadros: Int, val pose: (Int) -> Pose, val utileria: (DrawScope.(PuntosGato, LuzGato, Int) -> Unit)? = null)

/**
 * El pelaje del gato de un tema: sus colores, su dibujo, su accesorio y sus dos acciones propias. Lo demás (el
 * esqueleto, las 13 animaciones, cómo se porta) es igual para todos.
 *
 * - [manto]: el color del cuerpo. [claro]: el del vientre y el hocico. [marca]: el de las rayas, las manchas o las puntas.
 * - [collar]: un collar de ese color, o ninguno. [accesorio]: lo que lleve puesto, dibujado encima.
 */
class Pelaje(
    val manto: Color, val claro: Color, val marca: Color, val dibujo: DibujoPelaje, val ojos: Color,
    val nariz: Color = Color(0xFFD08888), val collar: Color? = null, val especiales: List<EspecialGato> = emptyList(),
    val accesorio: (DrawScope.(PuntosGato, LuzGato) -> Unit)? = null,
) {
    companion object {
        /** El gato de los temas que todavía no tienen el suyo: gris atigrado, de ojos verdes. */
        val COMUN = Pelaje(Color(0xFF6E6A70), Color(0xFFECE6DC), Color(0xFF34313A), DibujoPelaje.ATIGRADO, Color(0xFFB6D060))
    }
}

private val SOMBRA = Color(0xFF261E44)
private val TINTA = Color(0xFF1E1826)

private fun largo(v: Offset) = sqrt(v.x * v.x + v.y * v.y)
private fun unidad(v: Offset) = largo(v).let { if (it < 0.001f) Offset(0f, 1f) else v / it }

/** Dos círculos y lo que los une: el cuerpo entre la grupa y el pecho, o el cuello. */
private fun DrawScope.tramo(c: Color, a: Offset, ra: Float, b: Offset, rb: Float, modo: BlendMode = BlendMode.SrcOver) {
    val n = unidad(b - a).let { Offset(-it.y, it.x) }
    drawPath(Path().apply { moveTo(a.x + n.x * ra, a.y + n.y * ra); lineTo(b.x + n.x * rb, b.y + n.y * rb); lineTo(b.x - n.x * rb, b.y - n.y * rb); lineTo(a.x - n.x * ra, a.y - n.y * ra); close() }, c, blendMode = modo)
    drawCircle(c, ra, a, blendMode = modo); drawCircle(c, rb, b, blendMode = modo)
}

private fun DrawScope.triangulo(c: Color, a: Offset, b: Offset, d: Offset) = drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(d.x, d.y); close() }, c)

/**
 * El gato en la postura [pose], con su [pelaje] y la [luz] del sitio, pisando en [pie] (en las unidades del lienzo).
 * [dir]: 1 si mira a la derecha, −1 a la izquierda. [talla]: su tamaño. [dy]: cuánto está en el aire (negativo: sube).
 * [especial] y [cuadro]: si está en una de sus acciones propias, para dibujar lo que lleva.
 */
fun DrawScope.gato(pose: Pose, pelaje: Pelaje, luz: LuzGato, pie: Offset, dir: Int, talla: Float = 1f, dy: Float = 0f, especial: EspecialGato? = null, cuadro: Int = 0) {
    val r = luz.rayo
    val g = Gatuno
    // La sombra en el suelo: una mancha debajo, siempre, y la que arroja la luz, hacia el otro lado.
    val aire = (1f + dy / (16f * talla)).coerceIn(0.35f, 1f)
    val ancho = (g.MEDIO_LARGO * 1.5f * (0.45f + 0.55f * pose.ancho)) * talla * aire
    drawOval(Color.Black, Offset(pie.x - ancho * 0.42f, pie.y - 1.4f * talla), Size(ancho * 0.84f, 3f * talla), alpha = 0.16f * aire)
    if (r.fuerza > 0.03f) {
        val corre = (r.ax * 15f * talla).coerceIn(-30f * talla, 30f * talla)
        val x0 = minOf(pie.x, pie.x + corre) - ancho * 0.48f
        drawOval(r.color, Offset(x0, pie.y - 2.2f * talla + (r.ay * 6f * talla).coerceIn(-5f, 0f)), Size(abs(corre) + ancho * 0.96f, 4.6f * talla), alpha = (r.fuerza * 0.8f * aire).coerceIn(0f, 1f), blendMode = BlendMode.Multiply)
    }
    withTransform({ translate(pie.x, pie.y + dy); scale(dir * talla, talla, Offset.Zero) }) { cuerpo(pose, pelaje, luz, if (dir >= 0) r.lat else -r.lat, especial, cuadro) }
}

/** El cuerpo, en sus propias medidas: mira a la derecha y pisa en (0, 0). [lat]: de qué lado le da la luz, ya visto desde él. */
private fun DrawScope.cuerpo(p: Pose, pelaje: Pelaje, luz: LuzGato, lat: Float, especial: EspecialGato?, cuadro: Int) {
    val g = Gatuno
    val mantoPropio = luz.tono(pelaje.manto); val claro = luz.tono(pelaje.claro); val marca = luz.tono(pelaje.marca)
    val lejosPropio = luz.tono(lerp(Color(pelaje.manto.red * 0.7f, pelaje.manto.green * 0.68f, pelaje.manto.blue * 0.76f), SOMBRA, 0.1f))
    val orejaPropia = if (pelaje.dibujo == DibujoPelaje.PUNTAS) marca else mantoPropio
    val rosaPropio = luz.tono(Color(0xFFD9A6A0))
    // El borde: la misma silueta, oscura, corrida un poco hacia abajo y hacia el lado de la sombra. Lo despega del fondo.
    val borde = luz.tono(lerp(Color(pelaje.manto.red * 0.42f, pelaje.manto.green * 0.4f, pelaje.manto.blue * 0.48f), SOMBRA, 0.45f))
    var manto = mantoPropio; var lejos = lejosPropio; var oreja = orejaPropia; var rosa = rosaPropio
    val contraste = luz.rayo.contraste
    val medio = (p.cadera + p.hombro) / 2f + Offset(0f, -p.lomo - 0.3f)
    val rMedio = 4.3f + 0.3f * p.erizo
    val raizT = p.cadera + Offset(-0.4f, 1f); val raizD = p.hombro + Offset(0.5f, 0f)
    val baseCola = p.cadera + Offset(-4.2f, -1.6f)
    val cuelloA = p.hombro + Offset(0.8f, -1.2f); val cuelloB = p.cabeza + Offset(-1.6f, 1.4f)
    val c = p.cabeza
    // Con el giro, el cuerpo se ve de escorzo, pero la cabeza no se estrecha tanto.
    val cabezaAncho = (0.25f + 0.75f * p.ancho) / p.ancho

    fun fin(j: Offset, pie: Offset, b: Float): Offset { val v = pie - j; val d = largo(v); return if (d <= b) pie else j + v / d * b }
    val codoC = g.articulacion(raizD, p.manoC, g.BRAZO, g.ANTEBRAZO, false); val manoC = fin(codoC, p.manoC, g.ANTEBRAZO)
    val codoL = g.articulacion(raizD + Offset(1f, 0f), p.manoL, g.BRAZO, g.ANTEBRAZO, false); val manoL = fin(codoL, p.manoL, g.ANTEBRAZO)
    val rodC = g.articulacion(raizT, p.pieC, g.MUSLO, g.PIERNA, true); val pieC = fin(rodC, p.pieC, g.PIERNA)
    val rodL = g.articulacion(raizT + Offset(1f, 0f), p.pieL, g.MUSLO, g.PIERNA, true); val pieL = fin(rodL, p.pieL, g.PIERNA)

    fun pata(col: Color, raiz: Offset, j: Offset, f: Offset, arriba: Float, abajo: Float) {
        drawLine(col, raiz, j, arriba, StrokeCap.Round); drawLine(col, j, f, abajo, StrokeCap.Round)
        drawOval(col, Offset(f.x - 1.6f, f.y - 2.5f), Size(4.8f, 2.6f))
    }
    fun radioCola(t: Float) = (1.7f - 0.5f * t) * (1f + 0.55f * p.erizo)
    fun puntoCola(t: Float) = g.cola(p, baseCola, g.COLA, t).let { Offset(it.x, minOf(it.y, -radioCola(t))) }
    fun anillo(col: Color, t: Float, alfa: Float) { val a = puntoCola(t); val v = unidad(puntoCola((t + 0.04f).coerceAtMost(1f)) - puntoCola((t - 0.04f).coerceAtLeast(0f))); val n = Offset(-v.y, v.x) * (radioCola(t) + 0.3f); drawLine(col, a + n, a - n, 1.1f, alpha = alfa, blendMode = BlendMode.SrcAtop) }
    fun cola(col: Color, desde: Float = 0f) { for (i in 0..18) { val t = i / 18f; if (t >= desde) drawCircle(col, radioCola(t), puntoCola(t)) } }
    fun espinazo(t: Float): Offset = if (t < 0.5f) p.cadera + (medio - p.cadera) * (t * 2f) else medio + (p.hombro - medio) * ((t - 0.5f) * 2f)
    fun radioEn(t: Float) = if (t < 0.5f) g.R_GRUPA + (rMedio - g.R_GRUPA) * t * 2f else rMedio + (g.R_PECHO - rMedio) * (t - 0.5f) * 2f
    fun girarOreja(base: Offset, bloque: DrawScope.() -> Unit) = rotate(-38f * (1f - p.orejas), base, bloque)

    val kc = g.R_CABEZA / 4.9f
    fun cabeza(soloDetalles: Boolean) = scale(cabezaAncho * kc, kc, c) {
        rotate(p.morro * 57.3f, c) {
            if (!p.frente) {
                if (!soloDetalles) {
                    girarOreja(c + Offset(1.4f, -3.6f)) { triangulo(lejos, c + Offset(0.2f, -3.2f), c + Offset(3.1f, -3.8f), c + Offset(1.6f, -8.2f)) }
                    drawCircle(manto, 4.9f, c)
                    drawOval(manto, Offset(c.x + 1.3f, c.y - 0.5f), Size(4.9f, 3.9f))
                    girarOreja(c + Offset(-1.4f, -3.8f)) {
                        triangulo(oreja, c + Offset(-3f, -3f), c + Offset(0.3f, -4.3f), c + Offset(-1.7f, -8.8f))
                        triangulo(rosa, c + Offset(-2.1f, -4f), c + Offset(-0.5f, -4.6f), c + Offset(-1.6f, -7.3f))
                    }
                } else {
                    val ojo = c + Offset(2.1f, -1f)
                    ojoDe(ojo, p.ojos, pelaje, luz, 1.25f)
                    triangulo(luz.tono(pelaje.nariz), c + Offset(5.3f, 0.2f), c + Offset(6.3f, 0.5f), c + Offset(5.7f, 1.3f))
                    if (p.boca > 0.3f) {
                        drawOval(luz.tono(Color(0xFF5A1E2A)), Offset(c.x + 3.2f, c.y + 1.9f), Size(2.3f, 0.8f + 1.6f * p.boca))
                        triangulo(Color.White.copy(alpha = 0.9f), c + Offset(4.6f, 2f), c + Offset(5.1f, 2f), c + Offset(4.9f, 3f))
                    } else drawLine(TINTA.copy(alpha = 0.6f), c + Offset(5.7f, 1.3f), c + Offset(4.3f, 2.5f), 0.4f, StrokeCap.Round)
                    if (p.lengua) drawOval(luz.tono(Color(0xFFE08A96)), Offset(c.x + 4.4f, c.y + 2.2f), Size(1.6f, 2.5f))
                    val bigote = luz.tono(Color(0xFFF4EEE4)).copy(alpha = 0.75f)
                    for ((dx, dy2) in listOf(5.2f to -0.6f, 5.6f to 0.8f, 4.8f to 2.2f)) drawLine(bigote, c + Offset(4.5f, 1.5f), c + Offset(4.5f + dx, 1.5f + dy2), 0.28f)
                }
            } else {
                // De frente: las dos orejas, los dos ojos.
                if (!soloDetalles) {
                    for (s in listOf(-1f, 1f)) girarOreja(c + Offset(2.6f * s, -3.6f)) {
                        triangulo(oreja, c + Offset(4.4f * s, -2.4f), c + Offset(0.9f * s, -4.2f), c + Offset(3.2f * s, -8.6f))
                        triangulo(rosa, c + Offset(3.6f * s, -3.4f), c + Offset(1.7f * s, -4.3f), c + Offset(3f * s, -7.1f))
                    }
                    drawCircle(manto, 5.1f, c)
                    drawOval(manto, Offset(c.x - 5.6f, c.y - 1.2f), Size(11.2f, 5.6f))
                } else {
                    for (s in listOf(-1f, 1f)) ojoDe(c + Offset(2.1f * s, -0.8f), p.ojos, pelaje, luz, 1.2f)
                    triangulo(luz.tono(pelaje.nariz), c + Offset(-0.6f, 1.1f), c + Offset(0.6f, 1.1f), c + Offset(0f, 2f))
                    for (s in listOf(-1f, 1f)) {
                        drawLine(TINTA.copy(alpha = 0.55f), c + Offset(0f, 2f), c + Offset(1f * s, 2.9f), 0.35f, StrokeCap.Round)
                        val bigote = luz.tono(Color(0xFFF4EEE4)).copy(alpha = 0.75f)
                        for (dy2 in listOf(-0.5f, 0.6f, 1.7f)) drawLine(bigote, c + Offset(1.8f * s, 2f), c + Offset(7.4f * s, 2f + dy2), 0.28f)
                    }
                }
            }
        }
    }

    scale(p.ancho, 1f, Offset.Zero) {
        drawIntoCanvas { tela ->
            tela.saveLayer(Rect(-40f, -48f, 40f, 4f), Paint())
            // 1. Todas las partes, del color del manto: lo de atrás, un poco más oscuro. Antes, la misma silueta en oscuro.
            fun silueta() {
                if (!p.colaDelante) cola(manto)
                pata(lejos, raizT + Offset(1f, 0f), rodL, pieL, 5.2f, 2.9f)
                pata(lejos, raizD + Offset(1f, 0f), codoL, manoL, 4f, 2.9f)
                tramo(manto, p.cadera, g.R_GRUPA, medio, rMedio); tramo(manto, medio, rMedio, p.hombro, g.R_PECHO)
                tramo(manto, cuelloA, 3.8f, cuelloB, 3.7f)
                if (p.erizo > 0.05f) for (i in 0..11) {
                    val t = i / 11f; val s = espinazo(t) + Offset(0f, -radioEn(t) + 0.4f)
                    triangulo(manto, s + Offset(-1f, 0.6f), s + Offset(1f, 0.6f), s + Offset(-0.4f, -2.6f * p.erizo))
                }
            }
            fun deEsteLado() {
                pata(manto, raizT, rodC, pieC, 5.8f, 3.1f)
                pata(manto, raizD, codoC, manoC, 4.2f, 3.1f)
                cabeza(false)
                if (p.colaDelante) cola(manto)
            }
            manto = borde; lejos = borde; oreja = borde; rosa = borde
            withTransform({ translate(if (lat >= 0f) -0.5f else 0.5f, 0.55f) }) { silueta(); deEsteLado() }
            manto = mantoPropio; lejos = lejosPropio; oreja = orejaPropia; rosa = rosaPropio
            silueta()
            val largoCuerpo = largo(p.hombro - p.cadera)
            drawOval(claro, Offset(p.cadera.x - 1f, maxOf(p.cadera.y, p.hombro.y) + 2.4f), Size(largoCuerpo + 2f, 3.6f), alpha = 0.6f, blendMode = BlendMode.SrcAtop)
            deEsteLado()

            // 2. El dibujo del pelaje, solo donde ya hay gato.
            val encima = BlendMode.SrcAtop
            // El hocico claro lo llevan todos (el vientre ya se pintó, debajo de las patas).
            if (pelaje.dibujo == DibujoPelaje.PUNTAS) Unit
            else if (!p.frente) scale(cabezaAncho * kc, kc, c) { rotate(p.morro * 57.3f, c) { drawOval(claro, Offset(c.x + 2.1f, c.y + 0.4f), Size(4.4f, 3.4f), alpha = 0.9f, blendMode = encima) } }
            else scale(cabezaAncho * kc, kc, c) { drawOval(claro, Offset(c.x - 2.6f, c.y + 0.6f), Size(5.2f, 3.6f), alpha = 0.9f, blendMode = encima) }
            when (pelaje.dibujo) {
                DibujoPelaje.LISO -> Unit
                DibujoPelaje.ATIGRADO -> {
                    for (i in 0..4) {
                        val t = 0.1f + i * 0.2f; val s = espinazo(t); val rr = radioEn(t); val baja = if (i % 2 == 0) 0.25f else -0.1f
                        drawPath(Path().apply { moveTo(s.x + 0.3f, s.y - rr); cubicTo(s.x - 0.9f, s.y - rr * 0.6f, s.x - 1f, s.y - rr * 0.2f, s.x - 0.2f, s.y - rr * baja) }, marca, alpha = 0.5f, style = Stroke(0.75f, cap = StrokeCap.Round), blendMode = encima)
                    }
                    for (t in listOf(0.2f, 0.38f, 0.56f, 0.74f, 0.9f)) anillo(marca, t, 0.65f)
                    for ((j, f) in listOf(codoC to manoC, rodC to pieC)) for (k in listOf(0.4f, 0.68f)) { val q = j + (f - j) * k; drawLine(marca, q + Offset(-1.6f, 0f), q + Offset(1.6f, 0f), 0.7f, alpha = 0.5f, blendMode = encima) }
                    scale(cabezaAncho * kc, kc, c) { for (dx in listOf(-1.4f, 0f, 1.4f)) drawLine(marca, c + Offset(dx, -4.7f), c + Offset(dx + 0.5f, -2.6f), 0.7f, StrokeCap.Round, alpha = 0.8f, blendMode = encima) }
                }
                DibujoPelaje.MANCHAS -> {
                    drawOval(marca, Offset(p.cadera.x - 4f, p.cadera.y - 6f), Size(8.5f, 7f), blendMode = encima)
                    drawOval(claro, Offset(medio.x - 3f, medio.y - 5.5f), Size(7f, 12f), blendMode = encima)
                    drawOval(marca, Offset(p.hombro.x - 1f, p.hombro.y - 2f), Size(6f, 6.5f), blendMode = encima)
                    drawOval(marca, Offset(c.x - 4.5f, c.y - 5.5f), Size(5.5f, 6f), blendMode = encima)
                    for (i in 9..18) { val t = i / 18f; drawCircle(marca, radioCola(t) + 0.2f, puntoCola(t), blendMode = encima) }
                    drawLine(claro, rodC + (pieC - rodC) * 0.5f, pieC + Offset(1.4f, -1f), 3.6f, StrokeCap.Round, blendMode = encima)
                }
                DibujoPelaje.BICOLOR -> {
                    drawCircle(claro, 4.6f, p.hombro + Offset(3.2f, 3.2f), blendMode = encima)
                    tramo(claro, cuelloA + Offset(1.8f, 1.6f), 2.4f, cuelloB + Offset(2f, 2f), 2.2f, encima)
                    for (f in listOf(manoC, manoL, pieC, pieL)) drawCircle(claro, 3f, f + Offset(0.8f, -1f), blendMode = encima)
                    drawCircle(claro, 1.6f, puntoCola(1f), blendMode = encima)
                }
                DibujoPelaje.PUNTAS -> {
                    for ((j, f) in listOf(codoC to manoC, rodC to pieC, codoL to manoL, rodL to pieL)) drawLine(marca, j + (f - j) * 0.45f, f + Offset(1.2f, -1f), 3.4f, StrokeCap.Round, alpha = 0.9f, blendMode = encima)
                    for (i in 8..18) { val t = i / 18f; drawCircle(marca, radioCola(t) + 0.2f, puntoCola(t), alpha = 0.9f, blendMode = encima) }
                    scale(cabezaAncho * kc, kc, c) { rotate(p.morro * 57.3f, c) { drawCircle(marca, 3f, c + if (p.frente) Offset(0f, 1f) else Offset(3.2f, 0.9f), alpha = 0.75f, blendMode = encima) } }
                }
            }
            pelaje.collar?.let { col ->
                val a = cuelloA + (cuelloB - cuelloA) * 0.62f; val n = unidad(cuelloB - cuelloA).let { Offset(-it.y, it.x) }
                drawLine(luz.tono(col), a + n * 4.2f, a - n * 4.2f, 1.5f, blendMode = encima)
            }
            // El pelo: trazos cortos que siguen al cuerpo.
            for (i in 0..11) {
                val t = (i * 0.618f) % 1f; val s = espinazo(t); val rr = radioEn(t); val alto = ((i * 0.37f) % 1f - 0.5f) * 1.5f * rr
                drawLine(lejos, s + Offset(0f, alto), s + Offset(-1.5f, alto + 0.4f), 0.35f, StrokeCap.Round, alpha = 0.22f, blendMode = encima)
            }

            // 3. La luz: el lomo claro, el vientre en sombra, y un lado más claro que el otro.
            val zona = Rect(-40f, -48f, 40f, 4f)
            val arriba = minOf(p.cadera.y, p.hombro.y, c.y) - 6f
            drawRect(Brush.verticalGradient(0f to luz.brillo.copy(alpha = 0.42f * contraste + 0.08f), 0.4f to Color.Transparent, 0.62f to SOMBRA.copy(alpha = 0.34f * contraste + 0.12f), 1f to SOMBRA.copy(alpha = 0.16f * contraste + 0.08f), startY = arriba, endY = 0f), zona.topLeft, zona.size, blendMode = encima)
            val claroX = if (lat >= 0f) 22f else -22f
            drawRect(Brush.horizontalGradient(0f to luz.brillo.copy(alpha = 0.22f * contraste), 0.55f to Color.Transparent, 1f to SOMBRA.copy(alpha = 0.26f * contraste), startX = claroX, endX = -claroX), zona.topLeft, zona.size, blendMode = encima)
            tela.restore()
        }
        // 4. La cara, lo que lleva puesto y lo que tiene en las manos.
        cabeza(true)
        val puntos = PuntosGato(p.cadera, p.hombro, cuelloA + (cuelloB - cuelloA) * 0.62f, c, medio + Offset(0f, -rMedio), manoC, puntoCola(1f), p.frente)
        pelaje.accesorio?.invoke(this, puntos, luz)
        especial?.utileria?.invoke(this, puntos, luz, cuadro)
    }
}

/** Un ojo: abierto, con la pupila en raja (más ancha de noche), o cerrado, una raya. De noche le brilla. */
private fun DrawScope.ojoDe(centro: Offset, abierto: Float, pelaje: Pelaje, luz: LuzGato, radio: Float) {
    if (abierto < 0.25f) {
        drawPath(Path().apply { moveTo(centro.x - radio, centro.y); cubicTo(centro.x - radio * 0.4f, centro.y + radio * 0.7f, centro.x + radio * 0.4f, centro.y + radio * 0.7f, centro.x + radio, centro.y) }, TINTA.copy(alpha = 0.8f), style = Stroke(0.45f, cap = StrokeCap.Round))
        return
    }
    val alto = radio * 2f * (0.35f + 0.65f * abierto)
    val iris = lerp(luz.tono(pelaje.ojos), pelaje.ojos, 0.45f + 0.55f * luz.noche)
    drawOval(TINTA, Offset(centro.x - radio - 0.25f, centro.y - alto / 2f - 0.25f), Size(radio * 2f + 0.5f, alto + 0.5f))
    drawOval(iris, Offset(centro.x - radio, centro.y - alto / 2f), Size(radio * 2f, alto))
    val pupila = radio * (0.36f + 0.5f * luz.noche)
    drawOval(TINTA, Offset(centro.x - pupila / 2f + 0.1f, centro.y - alto * 0.42f), Size(pupila, alto * 0.84f))
    drawCircle(Color.White.copy(alpha = 0.85f), 0.3f, centro + Offset(-0.35f, -alto * 0.22f))
}
