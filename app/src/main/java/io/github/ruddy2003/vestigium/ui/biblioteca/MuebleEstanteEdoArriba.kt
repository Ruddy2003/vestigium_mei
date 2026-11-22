package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.AI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.AI_CLARO
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.BAMBU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.COBRE
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KINARI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.MATCHA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.ORO
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SHU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WARA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import kotlin.math.cos
import kotlin.math.sin

// El bodegón de encima del estante de la casa de té (MuebleEstanteEdo) y lo que hay a su pie.

/** El bodegón, de atrás adelante: las sandalias colgadas, el paraguas, el shamisen, el farol, los cojines, el ábaco y la botella. */
internal fun DrawScope.bodegonEdo(a: AireEdo, g: MarcoEdo) {
    val w = g.w
    sandaliasEdo(a, g, Offset(w * 0.6f, w * 0.165f))
    paraguasEdo(a, g, Offset(w * 0.895f, g.apoyo), Offset(w * 0.972f, w * 0.13f))
    shamisenEdo(a, g, Offset(w * 0.315f, g.apoyo))
    farolEdo(a, g)
    cojinesEdo(a, g, Offset(w * 0.15f, g.apoyo))
    sorobanEdo(a, g, Offset(w * 0.585f, g.apoyo))
    botellaEdo(a, g, Offset(w * 0.745f, g.apoyo + w * 0.004f), w * 0.16f)
}

/** Lo del pie, de atrás adelante: la escoba apoyada en el poste, la cubeta del arroz y la bandeja del tabaco con su brasa. */
internal fun DrawScope.alPieEdo(a: AireEdo, g: MarcoEdo) {
    val w = g.w
    escobaEdo(a, g, g.fondo, g.h)
    cubetaEdo(a, g, Offset(w * 0.07f, g.fondo + w * 0.385f))
    bandejaTabacoEdo(a, g, Offset(w * 0.59f, g.fondo + w * 0.4f))
}

// ---------------------------------------------------------------- en la pared

/**
 * Las sandalias de paja (waraji) que se venden a los viajeros: un par colgado de un clavo por los cordones del talón, con la
 * punta hacia abajo; la suela tejida en hileras de paja, más ancha delante, el cordón en V de la punta que se ata a las
 * presillas de los lados, y los cordones largos que suben al clavo.
 */
private fun DrawScope.sandaliasEdo(a: AireEdo, g: MarcoEdo, clavo: Offset) {
    val u = density; val w = g.w
    val k = g.cerca(clavo + Offset(0f, w * 0.1f)); val f = g.frio(clavo)
    val largo = w * 0.17f; val ancho = w * 0.068f
    val pares = listOf(Offset(-w * 0.03f, w * 0.045f) to 7f, Offset(w * 0.03f, w * 0.055f) to -6f)
    fun suela(top: Offset): Path = Path().apply {
        // Más estrecha en el talón (arriba) y más ancha en la punta (abajo), con las esquinas redondas.
        moveTo(top.x - ancho * 0.38f, top.y + ancho * 0.2f)
        quadraticTo(top.x, top.y - ancho * 0.12f, top.x + ancho * 0.38f, top.y + ancho * 0.2f)
        lineTo(top.x + ancho * 0.5f, top.y + largo - ancho * 0.35f)
        quadraticTo(top.x + ancho * 0.48f, top.y + largo, top.x, top.y + largo)
        quadraticTo(top.x - ancho * 0.48f, top.y + largo, top.x - ancho * 0.5f, top.y + largo - ancho * 0.35f)
        close()
    }
    sombraParedEdo(a, g, clavo + Offset(0f, w * 0.1f), 0.24f) { col -> pares.forEach { (d, giro) -> rotate(giro, clavo + d) { drawPath(suela(clavo + d), col) } } }
    val paja = a.luz(WARA, k, f)
    val pajaOsc = a.luz(mezcla(WARA, KURI_OSC, 0.55f), k, f)
    val pajaLuz = a.luz(mezcla(WARA, LUZ_MUEBLE, 0.4f), k, f)
    // Los cordones del talón, que suben al clavo, de las dos.
    pares.forEach { (d, _) ->
        drawLine(pajaOsc, clavo, clavo + d + Offset(-ancho * 0.25f, ancho * 0.1f), 1.4f * u)
        drawLine(pajaOsc, clavo, clavo + d + Offset(ancho * 0.25f, ancho * 0.1f), 1.4f * u)
    }
    pares.forEachIndexed { i, (d, giro) ->
        val top = clavo + d
        rotate(giro, top) {
            val forma = suela(top)
            drawPath(forma, paja)
            clipPath(forma) {
                // Las hileras del tejido, a lo ancho: una de luz y una de sombra, parejas, sobre la urdimbre de cuatro cordones.
                var y = top.y; var j = 0
                while (y < top.y + largo) {
                    drawRect(if (j % 2 == 0) pajaOsc.copy(alpha = 0.32f) else pajaLuz.copy(alpha = 0.35f), Offset(top.x - ancho, y), Size(ancho * 2, 2f * u))
                    y += 2f * u; j++
                }
                listOf(-0.22f, 0.22f).forEach { fx -> drawLine(pajaOsc.copy(alpha = 0.6f), Offset(top.x + ancho * fx, top.y), Offset(top.x + ancho * fx * 1.2f, top.y + largo), 1f * u) }
                drawRect(a.brillo(0.3f, k), Offset(top.x - ancho * 0.5f, top.y), Size(ancho * 0.25f, largo))
                drawRect(a.sombra(0.28f), Offset(top.x + ancho * 0.22f, top.y), Size(ancho * 0.3f, largo))
            }
            drawPath(forma, pajaOsc, style = Stroke(1.1f * u))
            // El cordón en V de la punta: de la presilla de la punta a las dos presillas de los lados, torcido.
            val punta = Offset(top.x, top.y + largo - ancho * 0.25f)
            listOf(-1f, 1f).forEach { sx ->
                val lado = Offset(top.x + sx * ancho * 0.52f, top.y + largo * 0.5f)
                drawLine(a.sombra(0.35f), punta + Offset(1f * u, 1.4f * u), lado + Offset(1f * u, 1.4f * u), 2.6f * u, StrokeCap.Round)
                drawLine(pajaLuz, punta, lado, 2.4f * u, StrokeCap.Round)
                for (t in 1..5) {
                    val q = Offset(punta.x + (lado.x - punta.x) * t / 6f, punta.y + (lado.y - punta.y) * t / 6f)
                    drawLine(pajaOsc, q + Offset(-1f * u, -0.6f * u), q + Offset(1f * u, 0.6f * u), 0.6f * u)
                }
                // La presilla del lado, un aro de paja que sobresale.
                drawArc(pajaOsc, if (sx < 0) 90f else -90f, 180f, false, Offset(lado.x - 2.6f * u, lado.y - 3.4f * u), Size(5.2f * u, 6.8f * u), style = Stroke(1.5f * u))
            }
            drawCircle(pajaOsc, 2.2f * u, punta)
            drawCircle(pajaLuz, 1f * u, punta - Offset(0.6f * u, 0.6f * u))
            // El cordón largo que cuelga suelto de una de ellas.
            if (i == 1) {
                val cordon = Path().apply { moveTo(top.x + ancho * 0.45f, top.y + largo * 0.5f); cubicTo(top.x + ancho * 1.1f, top.y + largo * 0.7f, top.x + ancho * 0.4f, top.y + largo * 0.95f, top.x + ancho * 0.9f, top.y + largo * 1.15f) }
                drawPath(corridoEdo(cordon, 2f * u, 1.4f * u), a.sombra(0.25f), style = Stroke(1.4f * u))
                drawPath(cordon, pajaOsc, style = Stroke(1.5f * u, cap = StrokeCap.Round))
                drawPath(corridoEdo(cordon, -0.5f * u, -0.4f * u), pajaLuz.copy(alpha = 0.7f), style = Stroke(0.6f * u))
            }
        }
    }
    remacheEdo(clavo, 2f * u, a, k)
}

/**
 * El paraguas de papel aceitado de la casa (los que se prestan a los viajeros), cerrado y apoyado en el pilar: el mango de
 * bambú en la tabla, el papel pardo plegado en husos con sus aristas, los anillos de laca negra y la caperuza de arriba.
 */
private fun DrawScope.paraguasEdo(a: AireEdo, g: MarcoEdo, pie: Offset, punta: Offset) {
    val u = density; val w = g.w
    val dx = punta.x - pie.x; val dy = punta.y - pie.y
    val largo = kotlin.math.sqrt(dx * dx + dy * dy)
    val ang = Math.toDegrees(kotlin.math.atan2(dx.toDouble(), -dy.toDouble())).toFloat()
    val k = g.cerca(Offset((pie.x + punta.x) / 2, (pie.y + punta.y) / 2)); val f = g.frio(punta)
    val mango = largo * 0.18f
    val ancho = w * 0.072f
    // El huso del papel plegado: más ancho a un tercio, fino en la punta y en el mango.
    val huso = Path().apply {
        moveTo(pie.x - ancho * 0.18f, pie.y - mango)
        cubicTo(pie.x - ancho * 0.6f, pie.y - mango - largo * 0.25f, pie.x - ancho * 0.45f, pie.y - largo * 0.8f, pie.x - ancho * 0.12f, pie.y - largo * 0.95f)
        lineTo(pie.x + ancho * 0.12f, pie.y - largo * 0.95f)
        cubicTo(pie.x + ancho * 0.45f, pie.y - largo * 0.8f, pie.x + ancho * 0.6f, pie.y - mango - largo * 0.25f, pie.x + ancho * 0.18f, pie.y - mango)
        close()
    }
    sombraParedEdo(a, g, Offset(pie.x, pie.y - largo * 0.5f), 0.22f) { col -> rotate(ang, pie) { drawPath(huso, col) } }
    rotate(ang, pie) {
        // El mango de bambú, con sus nudos.
        drawLine(a.luz(BAMBU, k, f), pie, Offset(pie.x, pie.y - mango - 2f * u), w * 0.012f, StrokeCap.Round)
        drawLine(a.brillo(0.4f, k), Offset(pie.x - w * 0.003f, pie.y), Offset(pie.x - w * 0.003f, pie.y - mango), 0.8f * u)
        drawLine(a.luz(mezcla(BAMBU, KURI_OSC, 0.4f), k), Offset(pie.x - w * 0.006f, pie.y - mango * 0.5f), Offset(pie.x + w * 0.006f, pie.y - mango * 0.5f), 1f * u)
        val papel = a.luz(mezcla(KAKI, KURI_OSC, 0.45f), k, f)
        drawPath(huso, papel)
        clipPath(huso) {
            // Las aristas de los pliegues, con su luz del lado del ranma.
            for (j in -3..3) {
                val x = pie.x + j * ancho * 0.13f
                drawLine(a.sombra(0.35f), Offset(x, pie.y - mango), Offset(pie.x + j * ancho * 0.03f, pie.y - largo * 0.95f), 0.8f * u)
                drawLine(a.brillo(0.25f, k), Offset(x - 1.2f * u, pie.y - mango), Offset(pie.x + j * ancho * 0.03f - 0.8f * u, pie.y - largo * 0.95f), 0.6f * u)
            }
            drawRect(a.brillo(0.22f, k), Offset(pie.x - ancho * 0.5f, pie.y - largo), Size(ancho * 0.3f, largo))
            drawRect(a.sombra(0.25f), Offset(pie.x + ancho * 0.15f, pie.y - largo), Size(ancho * 0.4f, largo))
            // Los anillos de laca negra, arriba y abajo.
            listOf(0.26f, 0.3f, 0.84f).forEach { t ->
                val y = pie.y - largo * t
                drawRect(a.luz(SUMI, k), Offset(pie.x - ancho, y - 1.6f * u), Size(ancho * 2, 3.2f * u))
                drawRect(a.brillo(0.4f, k), Offset(pie.x - ancho, y - 1.6f * u), Size(ancho * 2, 0.8f * u))
            }
        }
        // La caperuza de papel de la punta, y el cordón que lo ata.
        drawCircle(a.luz(mezcla(KAMI, KURI_OSC, 0.2f), k, f), ancho * 0.18f, Offset(pie.x, pie.y - largo * 0.96f))
        drawLine(a.luz(SHU, k, f), Offset(pie.x - ancho * 0.3f, pie.y - largo * 0.55f), Offset(pie.x + ancho * 0.3f, pie.y - largo * 0.57f), 1.4f * u)
        drawLine(a.luz(SHU, k, f), Offset(pie.x + ancho * 0.3f, pie.y - largo * 0.57f), Offset(pie.x + ancho * 0.36f, pie.y - largo * 0.5f), 1f * u)
    }
    contactoEdo(a, pie, w * 0.03f, 3.6f * u, 0.4f)
}

// ---------------------------------------------------------------- el shamisen

/**
 * El shamisen que dejó un huésped, apoyado en la pared: la caja casi cuadrada con su aro de madera oscura y su piel clara,
 * el parche donde pega la púa, la funda de brocado verde sobre el canto de arriba, el puente de hueso, las tres cuerdas, el
 * mástil pulido y la cabeza con sus tres clavijas de hueso; abajo, el cordón de la cola. Echa su sombra larga en la pared.
 */
private fun DrawScope.shamisenEdo(a: AireEdo, g: MarcoEdo, pie: Offset) {
    val u = density; val w = g.w
    val giro = 9f
    val bw = w * 0.15f; val bh = w * 0.158f
    val nw = w * 0.026f; val largo = w * 0.31f; val hl = w * 0.072f
    val caja = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
    val mastil = Rect(pie.x - nw / 2, caja.top - largo, pie.x + nw / 2, caja.top + 2f * u)
    val cabeza = Rect(pie.x - nw * 0.72f, mastil.top - hl, pie.x + nw * 0.72f, mastil.top)
    val silueta = Path().apply {
        addRoundRect(RoundRect(caja, CornerRadius(bw * 0.12f)))
        addRect(mastil); addRoundRect(RoundRect(cabeza, CornerRadius(nw * 0.4f)))
    }
    val centro = Offset(pie.x + w * 0.02f, caja.top - largo * 0.3f)
    val k = g.cerca(centro); val f = g.frio(centro)
    val kc = g.cerca(caja.center)
    sombraParedEdo(a, g, centro, 0.24f) { col -> rotate(giro, pie) { drawPath(silueta, col) } }
    contactoEdo(a, pie, bw * 1.1f, 5f * u, 0.4f)
    val madera = mezcla(KURI_OSC, SHU, 0.12f)
    rotate(giro, pie) {
        // El mástil, de madera oscura pulida: su lado de luz, su sombra y un brillo largo.
        drawRect(a.luz(madera, k, f), mastil.topLeft, mastil.size)
        drawRect(a.luz(mezcla(madera, KURI_LUZ, 0.45f), k, f), mastil.topLeft, Size(nw * 0.3f, mastil.height))
        drawRect(a.sombra(0.3f), Offset(mastil.right - nw * 0.3f, mastil.top), Size(nw * 0.3f, mastil.height))
        drawLine(a.brillo(0.6f, k), Offset(mastil.left + nw * 0.22f, mastil.top + 2f * u), Offset(mastil.left + nw * 0.22f, mastil.bottom - 4f * u), 0.8f * u)
        // La cabeza, doblada hacia atrás (su parte de abajo más honda), y las clavijas: dos a un lado, una al otro.
        drawRoundRect(a.luz(madera, k, f), cabeza.topLeft, cabeza.size, CornerRadius(nw * 0.4f))
        drawRect(a.sombra(0.3f), Offset(cabeza.left, cabeza.bottom - hl * 0.3f), Size(cabeza.width, hl * 0.3f))
        drawRoundRect(a.brillo(0.4f, k), Offset(cabeza.left + nw * 0.15f, cabeza.top + 1f * u), Size(nw * 0.22f, hl * 0.6f), CornerRadius(nw * 0.1f))
        listOf(0.22f to -1f, 0.5f to 1f, 0.78f to -1f).forEach { (t, s) ->
            val y = cabeza.top + hl * t
            val lc = w * 0.045f
            val x0 = if (s < 0) cabeza.left - lc else cabeza.right
            val clavija = poligonoEdo(x0, y - (if (s < 0) 1.3f else 2.2f) * u, x0 + lc, y - (if (s < 0) 2.2f else 1.3f) * u, x0 + lc, y + (if (s < 0) 2.2f else 1.3f) * u, x0, y + (if (s < 0) 1.3f else 2.2f) * u)
            drawPath(corridoEdo(clavija, 1.4f * u, 1.6f * u), a.sombra(0.35f))
            drawPath(clavija, a.luz(mezcla(WASHI, KAMI, 0.4f), k, f))
            drawLine(a.brillo(0.6f, k), Offset(x0 + 1f * u, y - 0.8f * u), Offset(x0 + lc - 1f * u, y - 0.8f * u), 0.6f * u)
            drawLine(a.luz(madera, k), Offset(x0 + (if (s < 0) lc * 0.18f else lc * 0.82f), y - 2f * u), Offset(x0 + (if (s < 0) lc * 0.18f else lc * 0.82f), y + 2f * u), 1.2f * u)
        }
        // La caja: el aro de madera y la piel tensada, más clara en el centro y más honda en la orilla.
        drawRoundRect(a.luz(madera, kc, f), caja.topLeft, caja.size, CornerRadius(bw * 0.12f))
        drawRoundRect(a.brillo(0.4f, kc), caja.topLeft, Size(bw * 0.25f, bh), CornerRadius(bw * 0.12f))
        val piel = caja.deflate(3f * u)
        val pielC = a.luz(mezcla(WASHI, KAMI, 0.25f), kc + 0.05f, f)
        drawRoundRect(pielC, piel.topLeft, piel.size, CornerRadius(bw * 0.1f))
        clipRect(piel.left, piel.top, piel.right, piel.bottom) {
            drawRect(Brush.radialGradient(listOf(Color.Transparent, a.sombra(0.26f)), Offset(piel.center.x - piel.width * 0.12f, piel.center.y), piel.width * 0.7f), piel.topLeft, piel.size)
            drawRect(a.brillo(0.3f, kc), Offset(piel.left + 2f * u, piel.top), Size(piel.width * 0.18f, piel.height))
            // El parche de la púa, abajo a la derecha, apenas más amarillo, con su orilla.
            val parche = Rect(piel.right - piel.width * 0.42f, piel.bottom - piel.height * 0.46f, piel.right - piel.width * 0.08f, piel.bottom - piel.height * 0.16f)
            drawRect(a.luz(mezcla(KAMI, WARA, 0.3f), kc, f).copy(alpha = 0.7f), parche.topLeft, parche.size)
            drawRect(a.sombra(0.2f), parche.topLeft, parche.size, style = Stroke(0.6f * u))
        }
        // La funda de brocado verde sobre el canto de arriba, con sus puntos de oro y su borla.
        val funda = Rect(caja.left - 1.6f * u, caja.top - 2f * u, caja.right + 1.6f * u, caja.top + bh * 0.2f)
        drawRoundRect(a.luz(MATCHA, kc, f), funda.topLeft, funda.size, CornerRadius(2.4f * u))
        for (j in 0 until 8) for (fila in 0 until 2) drawCircle(a.luz(ORO, kc, f).copy(alpha = 0.8f), 0.8f * u, Offset(funda.left + funda.width * (0.07f + 0.12f * j + 0.06f * fila), funda.top + funda.height * (0.32f + 0.36f * fila)))
        drawRect(a.brillo(0.35f, kc), Offset(funda.left + 2f * u, funda.top), Size(funda.width - 4f * u, 1f * u))
        drawRect(a.sombra(0.4f), Offset(funda.left, funda.bottom - 1.2f * u), Size(funda.width, 1.6f * u))
        // El puente de hueso, las tres cuerdas que suben a las clavijas, y el cordón rojo de la cola.
        val puente = Offset(pie.x, caja.bottom - bh * 0.28f)
        drawRect(a.sombra(0.35f), Offset(puente.x - nw * 0.6f, puente.y + 1f * u), Size(nw * 1.4f, 1.6f * u))
        drawRect(a.luz(mezcla(WASHI, KAMI, 0.3f), kc + 0.1f, f), Offset(puente.x - nw * 0.7f, puente.y - 1.6f * u), Size(nw * 1.4f, 2.8f * u))
        for (j in -1..1) {
            drawLine(a.luz(WASHI, k + 0.1f, f).copy(alpha = 0.9f), Offset(pie.x + j * nw * 0.24f, caja.bottom - 2f * u), Offset(pie.x + j * nw * 0.18f, cabeza.top + hl * 0.2f), 0.6f * u)
        }
        drawRect(a.luz(madera, kc), Offset(pie.x - nw * 0.3f, caja.bottom - 1f * u), Size(nw * 0.6f, 2.4f * u))
        drawLine(a.luz(SHU, kc, f), Offset(pie.x, caja.bottom - 2f * u), Offset(pie.x + nw * 0.4f, caja.bottom + 1f * u), 1.6f * u, StrokeCap.Round)
    }
    filoFarolEdo(Offset(caja.right + 1f * u, caja.top + 4f * u), Offset(caja.right + 1f * u, caja.bottom - 3f * u), kc, a)
}

// ---------------------------------------------------------------- el farol

/**
 * El farol de viaje plegable (el de Odawara), colgado del nageshi por su gancho de hierro: el cilindro de papel con sus aros, las
 * tapas de laca negra arriba y abajo. De día está apagado (el papel crudo, con su luz y su sombra y la sombra larga en la
 * pared); de noche arde: el papel encendido desde dentro, más claro en el centro, los aros en contraluz, la vela que se
 * adivina y el filo de llama en las tapas.
 */
private fun DrawScope.farolEdo(a: AireEdo, g: MarcoEdo) {
    val u = density; val w = g.w
    val c = Offset(w * MuebleEstanteEdo.LAMP_X, w * MuebleEstanteEdo.LAMP_Y)
    val fw = w * 0.135f; val fh = w * 0.225f; val tapa = w * 0.026f
    val cuerpo = Rect(c.x - fw / 2, c.y - fh / 2, c.x + fw / 2, c.y + fh / 2)
    val gancho = Offset(c.x, g.nageshiAbajo)
    val forma = Path().apply { addRoundRect(RoundRect(cuerpo, CornerRadius(fw * 0.32f))) }
    if (!a.lumbre) sombraParedEdo(a, g, c, 0.2f) { col -> drawPath(forma, col); drawRect(col, Offset(cuerpo.left - 2f * u, cuerpo.top - tapa), Size(fw + 4f * u, tapa)) }
    // El gancho de hierro y el cordel trenzado.
    drawLine(a.luz(HIERRO, 0.5f), gancho, Offset(c.x, gancho.y + w * 0.012f), 1.6f * u)
    drawArc(a.luz(HIERRO, 0.5f), 0f, 180f, false, Offset(c.x - 3f * u, gancho.y + w * 0.008f), Size(6f * u, 6f * u), style = Stroke(1.4f * u))
    drawLine(a.luz(mezcla(SHU, KURI_OSC, 0.4f), 0.6f), Offset(c.x, gancho.y + w * 0.016f), Offset(c.x, cuerpo.top - tapa - 2f * u), 1.4f * u)
    drawCircle(a.luz(HIERRO, 0.6f), 3f * u, Offset(c.x, cuerpo.top - tapa - 3f * u), style = Stroke(1.2f * u))
    val aros = 12
    if (a.lumbre) {
        val viva = if (a.oscuro) 1f else 0.75f
        drawPath(forma, mezcla(LLAMA, Color(0xFFE8833E), 0.42f))
        clipPath(forma) {
            drawRect(Brush.radialGradient(listOf(Color(0xFFFFF6DC).copy(alpha = viva), LLAMA.copy(alpha = 0.6f * viva), Color.Transparent), Offset(c.x - fw * 0.08f, c.y + fh * 0.06f), fh * 0.62f), cuerpo.topLeft, cuerpo.size)
            for (j in 1 until aros) {
                val y = cuerpo.top + fh * j / aros
                drawLine(Color(0xFF8A4A22).copy(alpha = 0.5f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 1f * u)
            }
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF7A3A18).copy(alpha = 0.4f)), c.x, cuerpo.right), cuerpo.topLeft, cuerpo.size)
            // La vela, que se adivina detrás del papel, con su llama.
            drawRect(Color(0xFF8A4A22).copy(alpha = 0.35f), Offset(c.x - 2.2f * u, c.y + fh * 0.05f), Size(4.4f * u, fh * 0.3f))
            drawOval(Color.White.copy(alpha = 0.7f * viva), Offset(c.x - 2.4f * u, c.y - fh * 0.04f), Size(4.8f * u, 8f * u))
        }
    } else {
        val k = 1f; val f = g.frio(c)
        val papel = a.luz(mezcla(WASHI, KAMI, 0.35f), k, f)
        drawPath(forma, papel)
        clipPath(forma) {
            for (j in 1 until aros) {
                val y = cuerpo.top + fh * j / aros
                drawLine(a.sombra(0.3f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 0.9f * u)
                drawLine(a.claro(0.5f), Offset(cuerpo.left, y + 1f * u), Offset(cuerpo.right, y + 1f * u), 0.6f * u)
            }
            drawRect(Brush.horizontalGradient(listOf(LUZ_MUEBLE.copy(alpha = 0.4f), Color.Transparent, a.sombra(0.34f)), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
            drawLine(a.sombra(0.2f), Offset(c.x + fw * 0.2f, cuerpo.top), Offset(c.x + fw * 0.2f, cuerpo.bottom), 0.7f * u)
        }
    }
    // Las tapas de laca negra, un poco más anchas que el papel, con su brillo.
    listOf(cuerpo.top - tapa to true, cuerpo.bottom to false).forEach { (y, arriba) ->
        val t = Rect(cuerpo.left - 2.4f * u, y, cuerpo.right + 2.4f * u, y + tapa)
        drawRoundRect(a.luz(SUMI, if (a.lumbre) 0.5f else 1f), t.topLeft, t.size, CornerRadius(2.4f * u))
        if (a.lumbre) drawRect(LLAMA.copy(alpha = 0.85f), Offset(t.left + 1.5f * u, if (arriba) t.bottom - 1.4f * u else t.top), Size(t.width - 3f * u, 1.4f * u))
        drawRect(a.brillo(0.45f, if (a.lumbre) 0.8f else 1f), Offset(t.left + 2f * u, t.top + 1.2f * u), Size(t.width * 0.35f, 1f * u))
    }
    if (a.lumbre) {
        mancha(c, fw * 1.5f, LLAMA.copy(alpha = if (a.oscuro) 0.34f else 0.18f))
        mancha(c, fw * 0.8f, Color(0xFFFFF0C8).copy(alpha = if (a.oscuro) 0.3f else 0.14f))
    }
}

// ---------------------------------------------------------------- encima de la tabla

/**
 * Los cojines de los huéspedes, apilados: tres, blandos, que se vencen por las orillas, cada uno de su algodón (añil salpicado
 * de blanco, rayas de bermellón apagado, verde de té con hilos crudos), con su ribete y la borla de hilo en el centro.
 */
private fun DrawScope.cojinesEdo(a: AireEdo, g: MarcoEdo, pie: Offset) {
    val u = density; val w = g.w
    val cw = w * 0.22f; val gr = w * 0.038f
    val k = g.cerca(pie - Offset(0f, gr)); val f = g.frio(pie)
    contactoEdo(a, pie, cw * 1.15f, 6f * u, 0.45f)
    sombraParedEdo(a, g, pie - Offset(0f, gr * 1.5f), 0.2f) { col -> drawRoundRect(col, Offset(pie.x - cw / 2, pie.y - gr * 2.8f), Size(cw, gr * 2.8f), CornerRadius(gr * 0.5f)) }
    val telas = listOf(AI, mezcla(SHU, KURI_OSC, 0.25f), MATCHA)
    for (i in 0 until 3) {
        val dx = (azar(5100, i) - 0.5f) * w * 0.03f
        val y1 = pie.y - gr * i * 0.9f; val y0 = y1 - gr
        val l = pie.x - cw / 2 + dx; val r = pie.x + cw / 2 + dx
        val cuerpo = Path().apply {
            moveTo(l + 3f * u, y1)
            cubicTo(l - 4f * u, y1 - 1f * u, l - 4f * u, y0 + 2f * u, l + 4f * u, y0 + 1f * u)
            quadraticTo((l + r) / 2, y0 - gr * 0.4f, r - 4f * u, y0 + 1f * u)
            cubicTo(r + 4f * u, y0 + 2f * u, r + 4f * u, y1 - 1f * u, r - 3f * u, y1)
            quadraticTo((l + r) / 2, y1 + 2f * u, l + 3f * u, y1)
            close()
        }
        val tela = a.luz(telas[i], k, f)
        drawPath(corridoEdo(cuerpo, 2f * u, 2f * u), a.sombra(0.35f))
        drawPath(cuerpo, tela)
        clipPath(cuerpo) {
            when (i) {
                0 -> for (j in 0 until 26) {
                    // El añil salpicado de blanco (kasuri): rayitas cortas que no llegan a dibujar nada.
                    val p = Offset(l + (r - l) * azar(5101, j), y0 - gr * 0.3f + gr * 1.3f * azar(5102, j))
                    drawLine(a.luz(WASHI, k, f).copy(alpha = 0.55f), p, p + Offset((2f + 2f * azar(5103, j)) * u, 0f), 1.1f * u)
                }
                1 -> { var x = l; while (x < r) { drawLine(a.luz(KINARI, k, f).copy(alpha = 0.45f), Offset(x, y0 - gr), Offset(x + 1f * u, y1 + 2f * u), 1.4f * u); x += 6f * u } }
                else -> { var x = l + 2f * u; while (x < r) { drawLine(a.luz(KINARI, k, f).copy(alpha = 0.35f), Offset(x, y0 - gr), Offset(x + 1f * u, y1 + 2f * u), 0.7f * u); x += 3.2f * u } }
            }
            drawRect(a.brillo(0.32f, k), Offset(l, y0 - gr), Size(cw, gr * 0.55f))
            drawRect(a.sombra(0.26f), Offset(l, y1 - gr * 0.3f), Size(cw, gr * 0.45f))
            drawRect(a.sombra(0.18f), Offset(r - cw * 0.22f, y0 - gr), Size(cw * 0.22f, gr * 2f))
        }
        // El ribete de la costura, a media altura, y la borla del centro.
        drawLine(mezcla(tela, a.hondo, 0.35f), Offset(l + 2f * u, y0 + gr * 0.45f), Offset(r - 2f * u, y0 + gr * 0.45f), 0.8f * u)
        val bc = Offset((l + r) / 2 + 3f * u, y0 + gr * 0.12f)
        val hilo = a.luz(if (i == 0) SHU else WASHI, k, f)
        drawCircle(hilo, 1.8f * u, bc)
        drawLine(hilo, bc, bc + Offset(-2f * u, 3f * u), 0.9f * u)
        drawLine(hilo, bc, bc + Offset(2f * u, 3f * u), 0.9f * u)
    }
    filoFarolEdo(Offset(pie.x + cw / 2 + 2f * u, pie.y - gr * 2.6f), Offset(pie.x + cw / 2 + 2f * u, pie.y - 2f * u), k, a)
}

/**
 * El ábaco de las cuentas de la casa, de pie y apoyado en la pared: el marco de madera oscura con su veta, la barra con sus
 * puntos, las varillas de bambú y las cuentas de boj (cada una con su cara de luz y su cara de sombra), unas subidas y otras
 * bajadas, como quedaron en la última cuenta. Todas las cuentas van en dos caminos.
 */
private fun DrawScope.sorobanEdo(a: AireEdo, g: MarcoEdo, pie: Offset) {
    val u = density; val w = g.w
    val sw = w * 0.3f; val sh = w * 0.105f
    val r = Rect(pie.x - sw / 2, pie.y - sh, pie.x + sw / 2, pie.y)
    val k = g.cerca(r.center); val f = g.frio(r.center)
    sombraParedEdo(a, g, r.center, 0.24f) { col -> rotate(-3f, pie) { drawRect(col, r.topLeft, r.size) } }
    contactoEdo(a, pie, sw * 1.05f, 5f * u, 0.4f)
    rotate(-3f, pie) {
        val marco = 3.4f * u
        val madera = a.luz(mezcla(KURI_OSC, SUMI, 0.25f), k, f)
        drawRect(madera, r.topLeft, r.size)
        vetas(Rect(r.left, r.top, r.right, r.top + marco), a.brillo(0.25f, k), 5200, densidad = 1.5f, grosor = 0.5f, ondulacion = 0.3f)
        val dentro = r.deflate(marco)
        drawRect(a.luz(mezcla(KURI_OSC, SUMI, 0.6f), k * 0.6f, f), dentro.topLeft, dentro.size)
        val barra = dentro.top + dentro.height * 0.3f
        val varillas = 15
        val paso = dentro.width / varillas
        val rw = paso * 0.92f; val rh = minOf(rw * 0.6f, dentro.height * 0.13f)
        val luzC = Path(); val sombraC = Path()
        for (j in 0 until varillas) {
            val x = dentro.left + paso * (j + 0.5f)
            drawLine(a.luz(BAMBU, k * 0.8f, f), Offset(x, dentro.top), Offset(x, dentro.bottom), 0.8f * u)
            fun cuenta(cy: Float) {
                luzC.addPath(poligonoEdo(x - rw / 2, cy, x, cy - rh / 2, x + rw / 2, cy))
                sombraC.addPath(poligonoEdo(x - rw / 2, cy, x + rw / 2, cy, x, cy + rh / 2))
            }
            cuenta(if (azar(5201, j) > 0.7f) barra - rh * 0.6f else dentro.top + rh * 0.55f)
            val suben = (azar(5202, j) * 5).toInt().coerceAtMost(4)
            for (i in 0 until 4) cuenta(if (i < suben) barra + rh * (0.6f + i) else dentro.bottom - rh * (0.55f + (3 - i)))
        }
        drawPath(luzC, a.luz(mezcla(WARA, KAKI, 0.25f), k, f))
        drawPath(sombraC, a.luz(mezcla(KAKI, KURI_OSC, 0.45f), k, f))
        drawRect(a.luz(mezcla(KURI_OSC, SUMI, 0.25f), k, f), Offset(dentro.left, barra - 1.6f * u), Size(dentro.width, 3.2f * u))
        drawRect(a.brillo(0.35f, k), Offset(dentro.left, barra - 1.6f * u), Size(dentro.width, 0.8f * u))
        for (j in 0 until varillas step 3) drawCircle(a.luz(WASHI, k, f), 0.8f * u, Offset(dentro.left + paso * (j + 0.5f), barra))
        drawRect(a.brillo(0.45f, k), r.topLeft, Size(sw, 1f * u))
        drawRect(a.brillo(0.3f, k), r.topLeft, Size(1f * u, sh))
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - 1.4f * u), Size(sw, 1.4f * u))
    }
    filoFarolEdo(Offset(r.right, r.top + 2f * u), Offset(r.right, r.bottom - 2f * u), k, a)
}

/**
 * La botella de sake de barro de la casa (sin letrero): la panza de barro crudo, el vidriado de caramelo que baja chorreado,
 * el cuello y el cordel de paja que la ata; su luz, su sombra, un brillo en el vidriado y la sombra en la pared y en la tabla.
 */
internal fun DrawScope.botellaEdo(a: AireEdo, g: MarcoEdo, pie: Offset, alto: Float) {
    val u = density
    val bw = alto * 0.5f
    val k = g.cerca(pie - Offset(0f, alto * 0.5f)); val f = g.frio(pie)
    val cuerpo = Path().apply {
        moveTo(pie.x - bw * 0.13f, pie.y - alto)
        lineTo(pie.x - bw * 0.11f, pie.y - alto * 0.78f)
        cubicTo(pie.x - bw * 0.64f, pie.y - alto * 0.6f, pie.x - bw * 0.58f, pie.y, pie.x - bw * 0.3f, pie.y)
        lineTo(pie.x + bw * 0.3f, pie.y)
        cubicTo(pie.x + bw * 0.58f, pie.y, pie.x + bw * 0.64f, pie.y - alto * 0.6f, pie.x + bw * 0.11f, pie.y - alto * 0.78f)
        lineTo(pie.x + bw * 0.13f, pie.y - alto)
        close()
    }
    sombraParedEdo(a, g, pie - Offset(0f, alto * 0.5f), 0.22f) { col -> drawPath(cuerpo, col) }
    contactoEdo(a, pie, bw * 1.2f, 4.6f * u, 0.45f)
    drawPath(cuerpo, a.luz(mezcla(KAMI, KURI_LUZ, 0.25f), k, f))
    clipPath(cuerpo) {
        val vidrio = Path().apply {
            moveTo(pie.x - bw, pie.y - alto * 1.1f); lineTo(pie.x + bw, pie.y - alto * 1.1f); lineTo(pie.x + bw, pie.y - alto * 0.48f)
            quadraticTo(pie.x + bw * 0.2f, pie.y - alto * 0.4f, pie.x, pie.y - alto * 0.5f)
            quadraticTo(pie.x - bw * 0.3f, pie.y - alto * 0.32f, pie.x - bw * 0.4f, pie.y - alto * 0.46f)
            quadraticTo(pie.x - bw * 0.6f, pie.y - alto * 0.5f, pie.x - bw, pie.y - alto * 0.47f); close()
        }
        drawPath(vidrio, a.luz(Color(0xFF6E3A1E), k, f))
        drawRect(Brush.horizontalGradient(listOf(a.brillo(0.3f, k), Color.Transparent, a.sombra(0.4f)), pie.x - bw * 0.55f, pie.x + bw * 0.55f), Offset(pie.x - bw, pie.y - alto), Size(bw * 2, alto))
        drawRect(a.brillo(0.6f, k), Offset(pie.x - bw * 0.34f, pie.y - alto * 0.68f), Size(bw * 0.07f, alto * 0.22f))
    }
    // El cordel de paja atado al cuello.
    val yc = pie.y - alto * 0.8f
    drawLine(a.luz(WARA, k, f), Offset(pie.x - bw * 0.16f, yc), Offset(pie.x + bw * 0.16f, yc + 1f * u), 1.6f * u)
    drawLine(a.luz(WARA, k, f), Offset(pie.x + bw * 0.14f, yc + 1f * u), Offset(pie.x + bw * 0.3f, yc + alto * 0.12f), 1.1f * u)
    drawOval(a.luz(mezcla(KAMI, KURI_OSC, 0.4f), k), Offset(pie.x - bw * 0.13f, pie.y - alto - 1.2f * u), Size(bw * 0.26f, 2.4f * u))
    filoFarolEdo(Offset(pie.x + bw * 0.46f, pie.y - alto * 0.45f), Offset(pie.x + bw * 0.4f, pie.y - alto * 0.1f), k, a)
}

// ---------------------------------------------------------------- al pie

/** La cubeta del arroz (ohitsu) de sawara, cortada por el borde: sus duelas, sus dos aros de cobre, la tapa y la paleta encima. */
private fun DrawScope.cubetaEdo(a: AireEdo, g: MarcoEdo, pie: Offset) {
    val u = density; val w = g.w
    val ow = w * 0.27f; val oh = w * 0.14f; val elip = w * 0.036f
    val k = g.cerca(pie - Offset(0f, oh)); val br = g.brasaEn(pie) / (k + 0.001f); val f = 0f
    contactoEdo(a, pie, ow * 1.2f, w * 0.03f, 0.45f)
    val cuerpo = Path().apply {
        moveTo(pie.x - ow * 0.5f, pie.y - oh)
        lineTo(pie.x - ow * 0.46f, pie.y - elip * 0.3f)
        quadraticTo(pie.x, pie.y + elip * 0.5f, pie.x + ow * 0.46f, pie.y - elip * 0.3f)
        lineTo(pie.x + ow * 0.5f, pie.y - oh)
        close()
    }
    val madera = a.luz(HINOKI, k, f, br)
    drawPath(cuerpo, madera)
    clipPath(cuerpo) {
        var x = pie.x - ow * 0.5f; var j = 0
        while (x < pie.x + ow * 0.5f) { drawLine(a.sombra(0.16f + 0.1f * azar(5300, j)), Offset(x, pie.y - oh), Offset(x, pie.y + elip), 0.8f * u); x += ow / 11f; j++ }
        drawRect(Brush.horizontalGradient(listOf(a.brillo(0.3f, k), Color.Transparent, a.sombra(0.34f)), pie.x - ow * 0.4f, pie.x + ow * 0.5f), Offset(pie.x - ow * 0.5f, pie.y - oh), Size(ow, oh + elip))
        listOf(0.22f, 0.74f).forEach { t ->
            val y = pie.y - oh * t
            val aro = Path().apply { moveTo(pie.x - ow * 0.5f, y - 2.4f * u); quadraticTo(pie.x, y + elip * 0.6f - 2.4f * u, pie.x + ow * 0.5f, y - 2.4f * u); lineTo(pie.x + ow * 0.5f, y + 2.4f * u); quadraticTo(pie.x, y + elip * 0.6f + 2.4f * u, pie.x - ow * 0.5f, y + 2.4f * u); close() }
            drawPath(aro, a.luz(COBRE, k, f, br))
            drawPath(aro, a.brillo(0.5f, k, br), style = Stroke(0.7f * u))
        }
    }
    // La tapa: un disco visto un poco desde arriba, y la paleta del arroz atravesada encima.
    val tapa = Rect(pie.x - ow * 0.54f, pie.y - oh - elip, pie.x + ow * 0.54f, pie.y - oh + elip)
    drawOval(a.sombra(0.3f), tapa.topLeft + Offset(0f, 2f * u), tapa.size)
    drawOval(a.luz(mezcla(HINOKI, WASHI, 0.3f), k + 0.05f, f, br), tapa.topLeft, tapa.size)
    vetas(tapa.deflate(2f * u), a.sombra(0.2f), 5301, densidad = 0.8f, grosor = 0.5f)
    drawArc(a.brillo(0.45f, k), 180f, 120f, false, tapa.topLeft + Offset(2f * u, 1f * u), Size(tapa.width - 4f * u, tapa.height - 2f * u), style = Stroke(1f * u))
    val pal = Path().apply {
        moveTo(pie.x - ow * 0.45f, tapa.center.y + 2f * u)
        lineTo(pie.x + ow * 0.05f, tapa.center.y - 1.5f * u)
        quadraticTo(pie.x + ow * 0.36f, tapa.center.y - 6f * u, pie.x + ow * 0.42f, tapa.center.y)
        quadraticTo(pie.x + ow * 0.3f, tapa.center.y + 3f * u, pie.x + ow * 0.05f, tapa.center.y + 1.5f * u)
        lineTo(pie.x - ow * 0.45f, tapa.center.y + 4f * u); close()
    }
    drawPath(corridoEdo(pal, 1.4f * u, 1.6f * u), a.sombra(0.35f))
    drawPath(pal, a.luz(mezcla(HINOKI, WASHI, 0.15f), k + 0.1f, f, br))
    drawLine(a.brillo(0.5f, k), Offset(pie.x - ow * 0.4f, tapa.center.y + 2.2f * u), Offset(pie.x + ow * 0.3f, tapa.center.y - 3.6f * u), 0.6f * u)
}

/**
 * La bandeja del tabaco que la casa pone a los huéspedes: la caja de madera con su asa, el braserito de loza añil con la ceniza
 * y una brasa encendida, el tubo de bambú para la ceniza y la pipa larga de boquillas de latón atravesada encima.
 */
private fun DrawScope.bandejaTabacoEdo(a: AireEdo, g: MarcoEdo, pie: Offset) {
    val u = density; val w = g.w
    val bw = w * 0.3f; val bh = w * 0.085f
    val caja = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
    val k = g.cerca(caja.center); val br = g.brasaEn(caja.center) / (k + 0.001f)
    contactoEdo(a, pie, bw * 1.15f, w * 0.022f, 0.45f)
    // El asa de madera curvada, detrás.
    val asa = Path().apply {
        moveTo(caja.left + bw * 0.12f, caja.top)
        cubicTo(caja.left + bw * 0.12f, caja.top - bh * 1.6f, caja.right - bw * 0.12f, caja.top - bh * 1.6f, caja.right - bw * 0.12f, caja.top)
    }
    drawPath(asa, a.luz(KURI_OSC, k, 0f, br), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
    drawPath(corridoEdo(asa, -1f * u, -1f * u), a.brillo(0.35f, k, br), style = Stroke(0.8f * u))
    // El fondo de la caja por dentro: la tabla de atrás, que asoma sobre el canto, en sombra.
    drawRect(a.luz(KURI_OSC, k * 0.7f, 0f, br), Offset(caja.left + 2f * u, caja.top - bh * 0.3f), Size(bw - 4f * u, bh * 0.3f))
    drawRect(a.brillo(0.4f, k, br), Offset(caja.left + 2f * u, caja.top - bh * 0.3f), Size(bw - 4f * u, 1f * u))
    // El braserito de loza añil y blanca: la panza redonda con sus franjas, la boca con la ceniza y la brasa encima.
    val hc = Offset(caja.left + bw * 0.33f, caja.top + bh * 0.1f)
    val hr = bw * 0.17f
    val olla = Path().apply {
        moveTo(hc.x - hr, hc.y - bh * 0.62f)
        cubicTo(hc.x - hr * 1.12f, hc.y - bh * 0.2f, hc.x - hr * 0.9f, hc.y, hc.x - hr * 0.6f, hc.y)
        lineTo(hc.x + hr * 0.6f, hc.y)
        cubicTo(hc.x + hr * 0.9f, hc.y, hc.x + hr * 1.12f, hc.y - bh * 0.2f, hc.x + hr, hc.y - bh * 0.62f)
        close()
    }
    drawPath(olla, a.luz(mezcla(WASHI, KAMI, 0.2f), k, 0f, br))
    clipPath(olla) {
        drawRect(a.luz(AI, k, 0f, br), Offset(hc.x - hr * 1.2f, hc.y - bh * 0.5f), Size(hr * 2.4f, bh * 0.16f))
        drawRect(a.luz(AI, k, 0f, br), Offset(hc.x - hr * 1.2f, hc.y - bh * 0.24f), Size(hr * 2.4f, bh * 0.07f))
        for (j in 0 until 5) drawCircle(a.luz(AI, k, 0f, br), hr * 0.08f, Offset(hc.x - hr * 0.7f + hr * 0.35f * j, hc.y - bh * 0.33f))
        drawRect(Brush.horizontalGradient(listOf(a.brillo(0.35f, k, br), Color.Transparent, a.sombra(0.35f)), hc.x - hr, hc.x + hr), Offset(hc.x - hr * 1.2f, hc.y - bh), Size(hr * 2.4f, bh))
    }
    val boca = Rect(hc.x - hr, hc.y - bh * 0.62f - hr * 0.22f, hc.x + hr, hc.y - bh * 0.62f + hr * 0.22f)
    drawOval(a.luz(mezcla(WASHI, KAMI, 0.3f), k, 0f, br), boca.topLeft, boca.size)
    drawOval(a.luz(mezcla(KAMI, SUMI, 0.3f), k, 0f, br), boca.deflate(hr * 0.1f).topLeft, boca.deflate(hr * 0.1f).size)
    val brasa = Offset(hc.x + hr * 0.08f, boca.center.y - hr * 0.04f)
    drawOval(Color(0xFF2A1A14), Offset(brasa.x - hr * 0.45f, brasa.y - hr * 0.2f), Size(hr * 0.9f, hr * 0.36f))
    val vivo = if (a.lumbre) mezcla(EdoTinta.BRASA, LLAMA, 0.45f) else mezcla(EdoTinta.BRASA, KURI_OSC, 0.2f)
    drawOval(vivo, Offset(brasa.x - hr * 0.3f, brasa.y - hr * 0.12f), Size(hr * 0.6f, hr * 0.22f))
    drawOval(if (a.lumbre) Color(0xFFFFE2A0) else mezcla(EdoTinta.BRASA, WASHI, 0.3f), Offset(brasa.x - hr * 0.12f, brasa.y - hr * 0.06f), Size(hr * 0.24f, hr * 0.1f))
    if (a.lumbre) {
        mancha(brasa, hr * 2f, EdoTinta.BRASA.copy(alpha = if (a.oscuro) 0.5f else 0.25f))
        mancha(brasa, hr * 0.7f, LLAMA.copy(alpha = if (a.oscuro) 0.5f else 0.25f))
    }
    // El tubo de bambú de la ceniza, de pie en la otra punta.
    val tubo = Rect(caja.right - bw * 0.26f, caja.top - bh * 1.1f, caja.right - bw * 0.13f, caja.top + 1f * u)
    drawRect(a.luz(BAMBU, k, 0f, br), tubo.topLeft, tubo.size)
    drawRect(a.brillo(0.4f, k, br), tubo.topLeft, Size(tubo.width * 0.3f, tubo.height))
    drawRect(a.sombra(0.3f), Offset(tubo.right - tubo.width * 0.3f, tubo.top), Size(tubo.width * 0.3f, tubo.height))
    drawLine(a.luz(mezcla(BAMBU, KURI_OSC, 0.4f), k), Offset(tubo.left, tubo.top + tubo.height * 0.45f), Offset(tubo.right, tubo.top + tubo.height * 0.45f), 1.2f * u)
    drawOval(a.luz(mezcla(BAMBU, SUMI, 0.55f), k), Offset(tubo.left, tubo.top - 1.6f * u), Size(tubo.width, 3.2f * u))
    // La caja, con su veta, su canto de luz y sus esquinas.
    drawRect(a.luz(KURI, k, 0f, br), caja.topLeft, caja.size)
    clipRect(caja.left, caja.top, caja.right, caja.bottom) { vetas(caja, a.luz(EdoTinta.VETA, k).copy(alpha = 0.4f), 5400, densidad = 0.8f, grosor = 0.5f, ondulacion = 0.4f) }
    drawRect(Brush.horizontalGradient(listOf(a.brillo(0.25f, k, br), Color.Transparent, a.sombra(0.3f)), caja.left, caja.right), caja.topLeft, caja.size)
    drawRect(a.brillo(0.55f, k, br), caja.topLeft, Size(bw, 1.2f * u))
    drawRect(a.sombra(0.4f), Offset(caja.left, caja.bottom - 1.4f * u), Size(bw, 1.4f * u))
    // La pipa larga, atravesada delante: la caña de bambú y las dos boquillas de latón.
    val p0 = Offset(caja.left - bw * 0.12f, caja.top + bh * 0.25f); val p1 = Offset(caja.right + bw * 0.08f, caja.top - bh * 0.2f)
    drawLine(a.sombra(0.35f), p0 + Offset(1.4f * u, 2f * u), p1 + Offset(1.4f * u, 2f * u), 2.4f * u, StrokeCap.Round)
    drawLine(a.luz(mezcla(BAMBU, KURI_OSC, 0.3f), k, 0f, br), p0, p1, 2.2f * u, StrokeCap.Round)
    val dx = (p1.x - p0.x); val dy = (p1.y - p0.y)
    drawLine(a.luz(ORO, k, 0f, br), p0, Offset(p0.x + dx * 0.16f, p0.y + dy * 0.16f), 2.6f * u, StrokeCap.Round)
    drawLine(a.luz(ORO, k, 0f, br), Offset(p1.x - dx * 0.14f, p1.y - dy * 0.14f), p1, 2.4f * u, StrokeCap.Round)
    drawCircle(a.luz(ORO, k, 0f, br), 2.6f * u, p1 + Offset(0f, -1.6f * u))
    drawLine(a.brillo(0.6f, k, br), Offset(p0.x + 1f * u, p0.y - 0.8f * u), Offset(p0.x + dx * 0.14f, p0.y + dy * 0.14f - 0.8f * u), 0.6f * u)
}

/**
 * La escoba de mijo de la mañana, apoyada en el poste de la derecha y cortada por el borde: el palo de bambú con sus nudos, la
 * cabeza de espigas abierta en abanico hasta el suelo, atada con vueltas de hilo bermellón y añil. Se pinta solo de [y0] a
 * [y1] (la parte de arriba va con el cuerpo del estante, la de abajo con el pie).
 */
internal fun DrawScope.escobaEdo(a: AireEdo, g: MarcoEdo, y0: Float, y1: Float) {
    val u = density; val w = g.w
    if (y1 - y0 < 1f) return
    val arriba = Offset(w * 0.93f, g.fondo - g.fila * 1.2f)
    val cuello = Offset(w * 0.975f, g.suelo - w * 0.03f)
    val suelo = g.h - w * 0.02f
    val k = g.cerca(Offset(w * 0.95f, (arriba.y + cuello.y) / 2))
    clipRect(0f, y0, w, y1) {
        val haz = Offset(if (a.oscuro) -6f * u else 8f * u, 4f * u)
        drawLine(a.sombra(0.3f), arriba + haz, cuello + haz, 4f * u, StrokeCap.Round)
        drawLine(a.luz(mezcla(BAMBU, WARA, 0.3f), k), arriba, cuello, 4f * u, StrokeCap.Round)
        drawLine(a.brillo(0.45f, k), arriba + Offset(-1.2f * u, 0f), cuello + Offset(-1.2f * u, 0f), 0.9f * u)
        for (j in 1..5) {
            val t = j / 6f
            val p = Offset(arriba.x + (cuello.x - arriba.x) * t, arriba.y + (cuello.y - arriba.y) * t)
            drawLine(a.luz(mezcla(BAMBU, KURI_OSC, 0.5f), k), p + Offset(-2.4f * u, 0f), p + Offset(2.4f * u, 0f), 1.2f * u)
        }
        val cabeza = poligonoEdo(cuello.x - w * 0.016f, cuello.y, cuello.x + w * 0.018f, cuello.y, cuello.x + w * 0.08f, suelo, cuello.x - w * 0.07f, suelo)
        drawPath(corridoEdo(cabeza, haz.x * 0.8f, 2f * u), a.sombra(0.3f))
        drawPath(cabeza, a.luz(WARA, k))
        clipPath(cabeza) {
            for (j in 0 until 22) {
                val t = j / 21f
                drawLine(a.luz(mezcla(WARA, KURI_OSC, 0.45f), k).copy(alpha = 0.75f), Offset(cuello.x - w * 0.014f + w * 0.03f * t, cuello.y), Offset(cuello.x - w * 0.07f + w * 0.15f * t, suelo), 0.7f * u)
            }
            drawRect(a.brillo(0.2f, k), Offset(cuello.x - w * 0.07f, cuello.y), Size(w * 0.04f, suelo - cuello.y))
            drawRect(a.sombra(0.22f), Offset(cuello.x + w * 0.02f, cuello.y), Size(w * 0.07f, suelo - cuello.y))
        }
        for (j in 0 until 4) {
            val y = cuello.y + w * (0.008f + 0.011f * j)
            drawLine(a.luz(if (j % 2 == 0) SHU else AI, k), Offset(cuello.x - w * (0.017f + 0.008f * j), y), Offset(cuello.x + w * (0.019f + 0.008f * j), y), 1.5f * u)
        }
    }
}

// ---------------------------------------------------------------- un ayudante

/** Un punto sobre una circunferencia de radio [r] alrededor de [c], al ángulo [ang] (en radianes). */
internal fun enCirculoEdo(c: Offset, r: Float, ang: Float) = Offset(c.x + r * cos(ang), c.y + r * sin(ang))
