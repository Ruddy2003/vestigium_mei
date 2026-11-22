package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.AI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.COBRE
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.HINOKI_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KAMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KINARI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.KURI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.MATSU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SHU
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.SUMI
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WARA
import io.github.ruddy2003.vestigium.ui.biblioteca.EdoTinta.WASHI
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE

// Lo de arriba del estante de la casa de té (MuebleEstanteEdo), lo del pie y lo de los huecos libres.

/** De qué lado viene la luz para un objeto en [x]: de día, del ranma (izquierda); de noche, del farol. -1 izquierda, 1 derecha. */
private fun AireEdo.ladoLuz(x: Float, w: Float): Float = if (oscuro) (if (w * MuebleEstanteEdo.LAMP_X >= x) 1f else -1f) else -1f

// ---------------------------------------------------------------- colgado en la pared

/** Las sandalias de paja (waraji) que se venden a los viajeros: un par atado, colgado de un clavo, con sus cordones sueltos. */
internal fun DrawScope.sandaliasEdo(clavo: Offset, w: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val paja = a.luz(WARA, k, f)
    val pajaOsc = a.luz(mezcla(WARA, HINOKI_OSC, 0.55f), k, f)
    val largo = w * 0.17f; val ancho = w * 0.06f
    // El cordel del clavo y el clavo.
    drawLine(pajaOsc, clavo, clavo + Offset(-2f * u, w * 0.02f), 1f * u)
    drawLine(pajaOsc, clavo, clavo + Offset(3f * u, w * 0.022f), 1f * u)
    listOf(Offset(-w * 0.012f, w * 0.02f) to -6f, Offset(w * 0.016f, w * 0.03f) to 7f).forEachIndexed { i, (d, giro) ->
        val top = clavo + d
        rotate(giro, top) {
            val suela = Rect(top.x - ancho / 2, top.y, top.x + ancho / 2, top.y + largo)
            val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(suela, CornerRadius(ancho * 0.48f))) }
            drawPath(corridoEdo(forma, 3f * u, 2.4f * u), a.sombra(if (a.oscuro) 0.3f else 0.26f))
            drawPath(forma, paja)
            clipPath(forma) {
                // El tejido de la paja: pasadas a lo ancho, unas más claras, y la sombra del lado contrario a la luz.
                var y = suela.top + 1.6f * u; var j = 0
                while (y < suela.bottom) {
                    drawLine(pajaOsc.copy(alpha = 0.6f), Offset(suela.left, y), Offset(suela.right, y + (azar(500 + i, j) - 0.5f) * 1.4f * u), 0.7f * u)
                    y += 2.2f * u; j++
                }
                drawLine(pajaOsc, Offset(suela.center.x, suela.top), Offset(suela.center.x, suela.bottom), 0.8f * u)
                drawRect(a.claro(0.3f), Offset(suela.left, suela.top), Size(ancho * 0.25f, largo))
                drawRect(a.sombra(0.24f), Offset(suela.right - ancho * 0.3f, suela.top), Size(ancho * 0.3f, largo))
            }
            // Las presillas de los lados y el cordón que cuelga suelto.
            for (s in listOf(-1f, 1f)) {
                drawArc(pajaOsc, if (s < 0) 90f else -90f, 180f, false, Offset(suela.center.x + s * ancho * 0.5f - 2f * u, suela.top + largo * 0.4f), Size(4f * u, 6f * u), style = Stroke(0.9f * u))
            }
            val cordon = Path().apply {
                moveTo(suela.center.x, suela.top + 2f * u)
                cubicTo(suela.left - 4f * u, suela.top + largo * 0.4f, suela.right + 3f * u, suela.top + largo * 0.8f, suela.center.x - 2f * u, suela.bottom + w * 0.03f)
            }
            drawPath(cordon, pajaOsc, style = Stroke(1f * u, cap = StrokeCap.Round))
        }
    }
    drawCircle(a.luz(SUMI, 0.3f), 1.4f * u, clavo)
}

// ---------------------------------------------------------------- encima del estante

/**
 * El shamisen que dejó un huésped, apoyado en la pared: la caja casi cuadrada con su piel clara, la funda de tela sobre el
 * canto de arriba, el puente, las tres cuerdas, el mástil oscuro y la cabeza con sus tres clavijas. Echa su sombra en las tablas.
 */
internal fun DrawScope.shamisenEdo(pie: Offset, w: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val giro = 9f
    val bw = w * 0.118f; val bh = w * 0.122f
    val nw = w * 0.021f; val largo = w * 0.3f; val hl = w * 0.052f
    val caja = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
    val mastil = Rect(pie.x - nw / 2, caja.top - largo, pie.x + nw / 2, caja.top + 2f * u)
    val cabeza = Rect(pie.x - nw * 0.72f, mastil.top - hl, pie.x + nw * 0.72f, mastil.top)
    val silueta = Path().apply {
        addRoundRect(androidx.compose.ui.geometry.RoundRect(caja, CornerRadius(bw * 0.12f)))
        addRect(mastil); addRoundRect(androidx.compose.ui.geometry.RoundRect(cabeza, CornerRadius(nw * 0.4f)))
    }
    val haciaLuz = a.ladoLuz(pie.x, w)
    // La sombra en la pared: de día hacia la derecha; de noche, lejos del farol.
    rotate(giro, pie) {
        drawPath(corridoEdo(silueta, -haciaLuz * 16f * u, 10f * u), a.sombra(if (a.oscuro) 0.16f else 0.12f))
        drawPath(corridoEdo(silueta, -haciaLuz * 7f * u, 4f * u), a.sombra(if (a.oscuro) 0.3f else 0.2f))
    }
    drawOval(a.sombra(0.4f), Offset(caja.left - 2f * u, pie.y - 2.5f * u), Size(bw + 8f * u, 5f * u))
    val madera = a.luz(KURI, k, f)
    val maderaLuz = a.luz(mezcla(KURI, COBRE, 0.45f), k, f)
    val piel = a.luz(mezcla(WASHI, KAMI, 0.25f), k + 0.05f, f)
    rotate(giro, pie) {
        // El mástil y la cabeza, de madera oscura pulida, con su brillo largo.
        drawRect(madera, mastil.topLeft, mastil.size)
        drawRect(maderaLuz, Offset(mastil.left + nw * 0.15f, mastil.top), Size(nw * 0.2f, mastil.height))
        drawRoundRect(madera, cabeza.topLeft, cabeza.size, CornerRadius(nw * 0.4f))
        drawRoundRect(maderaLuz, Offset(cabeza.left + nw * 0.15f, cabeza.top + 1f * u), Size(nw * 0.22f, hl - 2f * u), CornerRadius(nw * 0.1f))
        // Las clavijas de hueso: dos a un lado, una al otro.
        listOf(0.25f to -1f, 0.55f to 1f, 0.8f to -1f).forEach { (t, s) ->
            val y = cabeza.top + hl * t
            val x0 = if (s < 0) cabeza.left - w * 0.03f else cabeza.right
            drawRoundRect(a.luz(mezcla(WASHI, KAMI, 0.45f), k, f), Offset(x0, y - 1.3f * u), Size(w * 0.03f, 2.6f * u), CornerRadius(1.3f * u))
            drawRect(a.sombra(0.3f), Offset(x0, y + 0.3f * u), Size(w * 0.03f, 1f * u))
            drawCircle(madera, 1.6f * u, Offset(if (s < 0) x0 else x0 + w * 0.03f, y))
        }
        // La caja: el aro de madera y la piel tensada dentro.
        drawRoundRect(madera, caja.topLeft, caja.size, CornerRadius(bw * 0.12f))
        val pielR = caja.deflate(2.4f * u)
        drawRoundRect(piel, pielR.topLeft, pielR.size, CornerRadius(bw * 0.1f))
        drawRoundRect(a.sombra(0.2f), Offset(pielR.right - bw * 0.3f, pielR.top), Size(bw * 0.3f, pielR.height), CornerRadius(bw * 0.1f))
        drawRoundRect(a.claro(0.4f), Offset(pielR.left + 1f * u, pielR.top + 1f * u), Size(bw * 0.18f, pielR.height - 2f * u), CornerRadius(bw * 0.08f))
        // El parche de la piel donde pega la púa y la funda de tela verde pino sobre el canto de arriba, con su orla.
        drawRect(a.luz(mezcla(WASHI, KAMI, 0.45f), k, f), Offset(pielR.right - bw * 0.34f, pielR.top + bh * 0.24f), Size(bw * 0.22f, bh * 0.24f))
        val funda = Rect(caja.left - 1.2f * u, caja.top - 1.5f * u, caja.right + 1.2f * u, caja.top + bh * 0.2f)
        drawRoundRect(a.luz(MATSU, k, f), funda.topLeft, funda.size, CornerRadius(2f * u))
        for (j in 0 until 6) drawCircle(a.luz(mezcla(WARA, WASHI, 0.3f), k, f).copy(alpha = 0.7f), 0.7f * u, Offset(funda.left + funda.width * (0.1f + 0.16f * j), funda.center.y))
        drawRect(a.sombra(0.35f), Offset(funda.left, funda.bottom - 1f * u), Size(funda.width, 1.2f * u))
        // El puente y las tres cuerdas, que suben por el mástil hasta las clavijas.
        val puente = Offset(pie.x, caja.bottom - bh * 0.3f)
        drawRect(a.sombra(0.3f), Offset(puente.x - nw * 0.7f, puente.y + 0.6f * u), Size(nw * 1.6f, 1.2f * u))
        drawRect(a.luz(mezcla(WASHI, KAMI, 0.5f), k, f), Offset(puente.x - nw * 0.8f, puente.y - 1.2f * u), Size(nw * 1.6f, 2.2f * u))
        for (j in -1..1) {
            val x = pie.x + j * nw * 0.26f
            drawLine(a.luz(WASHI, k + 0.1f, f).copy(alpha = 0.85f), Offset(x, caja.bottom - 2f * u), Offset(pie.x + j * nw * 0.2f, cabeza.top + hl * 0.2f), 0.5f * u)
        }
        // El cabo del mástil que asoma por abajo y su cordón.
        drawRect(madera, Offset(pie.x - nw * 0.3f, caja.bottom - 1f * u), Size(nw * 0.6f, 2f * u))
    }
    filoFarolEdo(Offset(caja.right + 1f * u, caja.top + 4f * u), Offset(caja.right + 1f * u, caja.bottom - 3f * u), k, a)
}

/**
 * El ábaco de las cuentas de la casa, de pie y apoyado en la pared: el marco oscuro, la barra con sus puntos, las varillas y
 * las cuentas de boj, unas subidas y otras bajadas, como quedaron en la última cuenta. Todas las cuentas van en un solo camino.
 */
internal fun DrawScope.sorobanEdo(pie: Offset, w: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val sw = w * 0.25f; val sh = w * 0.088f
    val r = Rect(pie.x - sw / 2, pie.y - sh, pie.x + sw / 2, pie.y)
    val haciaLuz = a.ladoLuz(pie.x, w)
    drawRect(a.sombra(if (a.oscuro) 0.34f else 0.22f), Offset(r.left - haciaLuz * 5f * u, r.top + 3f * u), r.size)
    drawOval(a.sombra(0.4f), Offset(r.left - 2f * u, pie.y - 2.4f * u), Size(sw + 6f * u, 4.8f * u))
    rotate(-2.5f, pie) {
        val marco = 2.8f * u
        drawRect(a.luz(KURI, k, f), r.topLeft, r.size)
        val dentro = r.deflate(marco)
        drawRect(a.luz(mezcla(KURI, SUMI, 0.4f), k * 0.7f, f), dentro.topLeft, dentro.size)
        val barra = dentro.top + dentro.height * 0.3f
        val varillas = 13
        val paso = dentro.width / varillas
        val rw = paso * 0.9f; val rh = minOf(rw * 0.58f, dentro.height * 0.13f)
        val cuentas = Path(); val brillo = Path()
        for (j in 0 until varillas) {
            val x = dentro.left + paso * (j + 0.5f)
            drawLine(a.luz(WARA, k * 0.8f, f), Offset(x, dentro.top), Offset(x, dentro.bottom), 0.6f * u)
            fun cuenta(cy: Float) {
                cuentas.addPath(poligonoEdo(x - rw / 2, cy, x, cy - rh / 2, x + rw / 2, cy, x, cy + rh / 2))
                brillo.addPath(poligonoEdo(x - rw * 0.4f, cy - rh * 0.05f, x, cy - rh * 0.42f, x + rw * 0.15f, cy - rh * 0.3f, x - rw * 0.1f, cy - rh * 0.05f))
            }
            // Arriba, la cuenta del cinco: pegada al marco o bajada a la barra.
            val baja = azar(510, j) > 0.7f
            cuenta(if (baja) barra - rh * 0.6f else dentro.top + rh * 0.55f)
            // Abajo, las cuatro de la unidad: unas subidas a la barra y las demás abajo.
            val suben = (azar(511, j) * 5).toInt().coerceAtMost(4)
            for (i in 0 until 4) {
                val cy = if (i < suben) barra + rh * (0.6f + i) else dentro.bottom - rh * (0.55f + (3 - i))
                cuenta(cy)
            }
        }
        drawPath(cuentas, a.luz(mezcla(WARA, COBRE, 0.3f), k, f))
        drawPath(brillo, a.claro(0.35f).copy(alpha = if (a.oscuro) 0.1f + 0.3f * k else 0.4f))
        drawRect(a.luz(KURI, k, f), Offset(dentro.left, barra - 1.2f * u), Size(dentro.width, 2.4f * u))
        for (j in 0 until varillas step 3) drawCircle(a.luz(WASHI, k, f), 0.6f * u, Offset(dentro.left + paso * (j + 0.5f), barra))
        drawRect(a.claro(0.3f), r.topLeft, Size(sw, 1f * u))
        drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(sw, 1.2f * u))
    }
    filoFarolEdo(Offset(r.right, r.top + 2f * u), Offset(r.right, r.bottom - 2f * u), k, a)
}

/**
 * Los cojines de los huéspedes, apilados: tres, blandos, que se vencen por las orillas, de algodón a rayas (añil, verde pino y
 * crudo), cada uno con su borla de hilo en el centro.
 */
internal fun DrawScope.cojinesEdo(pie: Offset, w: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val cw = w * 0.175f; val gr = w * 0.03f
    val haciaLuz = a.ladoLuz(pie.x, w)
    drawOval(a.sombra(0.4f), Offset(pie.x - cw * 0.55f, pie.y - 2.4f * u), Size(cw * 1.15f, 4.8f * u))
    val telas = listOf(AI, MATSU, KINARI)
    for (i in 0 until 3) {
        val dx = (azar(520, i) - 0.5f) * w * 0.02f
        val y1 = pie.y - gr * i * 0.92f; val y0 = y1 - gr
        val l = pie.x - cw / 2 + dx; val r = pie.x + cw / 2 + dx
        val cuerpo = Path().apply {
            moveTo(l + 2f * u, y1)
            cubicTo(l - 3f * u, y1 - 1f * u, l - 3f * u, y0 + 2f * u, l + 3f * u, y0 + 1f * u)
            quadraticTo((l + r) / 2, y0 - gr * 0.35f, r - 3f * u, y0 + 1f * u)
            cubicTo(r + 3f * u, y0 + 2f * u, r + 3f * u, y1 - 1f * u, r - 2f * u, y1)
            quadraticTo((l + r) / 2, y1 + 1.6f * u, l + 2f * u, y1)
            close()
        }
        val tela = a.luz(telas[i], k, f)
        drawPath(corridoEdo(cuerpo, -haciaLuz * 3f * u, 1.6f * u), a.sombra(0.3f))
        drawPath(cuerpo, tela)
        clipPath(cuerpo) {
            // Las rayas del algodón: claras sobre el añil y el verde; añil sobre el crudo.
            val raya = if (i == 2) a.luz(AI, k, f).copy(alpha = 0.6f) else a.luz(WASHI, k, f).copy(alpha = 0.32f)
            var x = l + 2f * u
            while (x < r) { drawLine(raya, Offset(x, y0 - gr), Offset(x + 1f * u, y1 + 2f * u), 1f * u); x += if (i == 2) 7f * u else 3.4f * u }
            drawRect(a.claro(0.3f), Offset(l, y0 - gr), Size(cw, gr * 0.5f))
            drawRect(a.sombra(0.22f), Offset(l, y1 - gr * 0.3f), Size(cw, gr * 0.4f))
            drawRect(a.sombra(0.16f), Offset(if (haciaLuz < 0) r - cw * 0.25f else l, y0 - gr), Size(cw * 0.25f, gr * 2f))
        }
        // La borla del centro, del hilo de otro color.
        val bc = Offset((l + r) / 2 + 2f * u, y0 + gr * 0.25f)
        drawCircle(a.luz(if (i == 0) SHU else AI, k, f), 1.4f * u, bc)
        drawLine(a.luz(if (i == 0) SHU else AI, k, f), bc, bc + Offset(1f * u, 2.6f * u), 0.8f * u)
    }
}

/**
 * El farol de viaje plegable (odawara chōchin), colgado del nageshi por su gancho: el cilindro de papel con sus aros, las
 * tapas de laca negra arriba y abajo. De día está apagado (el papel crudo, en sombra del lado derecho); de noche arde y es la
 * luz del cuarto: el papel encendido desde dentro, los aros en contraluz y el filo cálido en las tapas.
 */
internal fun DrawScope.farolEdo(c: Offset, w: Float, a: AireEdo, g: MarcoEdo) {
    val u = density
    val fw = w * 0.092f; val fh = w * 0.165f; val tapa = w * 0.02f
    val cuerpo = Rect(c.x - fw / 2, c.y - fh / 2, c.x + fw / 2, c.y + fh / 2)
    val gancho = Offset(c.x, g.nageshiAbajo)
    // El gancho de hierro y el cordel.
    drawLine(a.luz(SUMI, 0.4f), gancho, Offset(c.x, cuerpo.top - tapa - 3f * u), 1f * u)
    drawArc(a.luz(SUMI, 0.4f), 0f, 180f, false, Offset(c.x - 2.4f * u, cuerpo.top - tapa - 5f * u), Size(4.8f * u, 4.8f * u), style = Stroke(1f * u))
    if (!a.lumbre) {
        // De día, su sombra alargada en las tablas, hacia la derecha y abajo, con la del cordel.
        drawLine(a.sombra(0.16f), Offset(c.x + 4f * u, gancho.y + 3f * u), Offset(c.x + 14f * u, cuerpo.top + 8f * u), 1f * u)
        drawRoundRect(a.sombra(0.12f), Offset(cuerpo.left + 14f * u, cuerpo.top + 10f * u), Size(fw, fh * 1.1f), CornerRadius(fw * 0.3f))
        drawRoundRect(a.sombra(0.16f), Offset(cuerpo.left + 7f * u, cuerpo.top + 5f * u), cuerpo.size, CornerRadius(fw * 0.3f))
    }
    val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(cuerpo, CornerRadius(fw * 0.3f))) }
    if (a.lumbre) {
        // Encendido: el papel brilla más en el centro, y los aros quedan en contraluz.
        val brasa = if (a.oscuro) 1f else 0.7f
        drawPath(forma, mezcla(EdoTinta.LLAMA, Color(0xFFE88A44), 0.35f))
        clipPath(forma) {
            mancha(Offset(c.x - fw * 0.1f, c.y + fh * 0.05f), fh * 0.55f, Color(0xFFFFF4D8).copy(alpha = 0.9f * brasa))
            var y = cuerpo.top + fh / 11f
            while (y < cuerpo.bottom - 1f) { drawLine(Color(0xFF8A4A22).copy(alpha = 0.55f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 0.8f * u); y += fh / 11f }
            drawRect(Color(0xFF8A4A22).copy(alpha = 0.3f), Offset(cuerpo.right - fw * 0.2f, cuerpo.top), Size(fw * 0.2f, fh))
            // La vela, que se adivina detrás del papel.
            drawRect(Color(0xFFFFFFFF).copy(alpha = 0.5f), Offset(c.x - 1.4f * u, c.y), Size(2.8f * u, fh * 0.32f))
        }
    } else {
        val papel = a.luz(mezcla(WASHI, KAMI, 0.3f), 0.6f, 0.1f)
        drawPath(forma, papel)
        clipPath(forma) {
            var y = cuerpo.top + fh / 11f
            while (y < cuerpo.bottom - 1f) {
                drawLine(a.sombra(0.32f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 0.8f * u)
                drawLine(a.claro(0.4f), Offset(cuerpo.left, y + 0.9f * u), Offset(cuerpo.right, y + 0.9f * u), 0.5f * u)
                y += fh / 11f
            }
            drawRect(a.claro(0.35f), Offset(cuerpo.left + fw * 0.08f, cuerpo.top), Size(fw * 0.2f, fh))
            drawRect(a.sombra(0.26f), Offset(cuerpo.right - fw * 0.32f, cuerpo.top), Size(fw * 0.32f, fh))
        }
    }
    // Las tapas de laca negra, un poco más anchas que el papel.
    listOf(cuerpo.top - tapa to true, cuerpo.bottom to false).forEach { (y, arriba) ->
        val t = Rect(cuerpo.left - 1.6f * u, y, cuerpo.right + 1.6f * u, y + tapa)
        drawRoundRect(a.luz(SUMI, if (a.lumbre) 0.5f else 0.3f), t.topLeft, t.size, CornerRadius(1.6f * u))
        if (a.lumbre) drawRect(FILO_MUEBLE.copy(alpha = 0.75f), Offset(t.left + 1f * u, if (arriba) t.bottom - 1.2f * u else t.top), Size(t.width - 2f * u, 1.2f * u))
        else drawRect(a.claro(0.4f), Offset(t.left + 1f * u, t.top + 1f * u), Size(t.width * 0.4f, 1f * u))
    }
}

// ---------------------------------------------------------------- al pie

/** La cubeta del arroz (ohitsu) de sawara, con sus dos aros de cobre, su tapa y la paleta de madera encima. */
internal fun DrawScope.cubetaArrozEdo(pie: Offset, w: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val ow = w * 0.15f; val oh = w * 0.085f
    val haciaLuz = a.ladoLuz(pie.x, w)
    drawOval(a.sombra(if (a.oscuro) 0.5f else 0.36f), Offset(pie.x - ow * 0.5f - haciaLuz * 4f * u, pie.y - 3f * u), Size(ow * 1.15f, 7f * u))
    val cuerpo = poligonoEdo(pie.x - ow * 0.46f, pie.y, pie.x - ow * 0.5f, pie.y - oh, pie.x + ow * 0.5f, pie.y - oh, pie.x + ow * 0.46f, pie.y)
    val madera = a.luz(mezcla(HINOKI, WASHI, 0.25f), k, f)
    drawPath(cuerpo, madera)
    clipPath(cuerpo) {
        // Las duelas, de pie, y el bulto: la luz de un lado y la sombra del otro.
        var x = pie.x - ow * 0.5f; var j = 0
        while (x < pie.x + ow * 0.5f) { drawLine(a.sombra(0.2f + 0.1f * azar(530, j)), Offset(x, pie.y - oh), Offset(x, pie.y), 0.7f * u); x += ow / 9f; j++ }
        drawRect(a.claro(0.35f), Offset(pie.x + haciaLuz * ow * 0.3f - ow * 0.08f, pie.y - oh), Size(ow * 0.16f, oh))
        drawRect(a.sombra(0.28f), Offset(if (haciaLuz < 0) pie.x + ow * 0.25f else pie.x - ow * 0.5f, pie.y - oh), Size(ow * 0.25f, oh))
        // Los dos aros de cobre, con su brillo.
        listOf(0.22f, 0.78f).forEach { t ->
            val y = pie.y - oh * t
            drawRect(a.luz(COBRE, k, f), Offset(pie.x - ow * 0.5f, y - 1.8f * u), Size(ow, 3.6f * u))
            drawRect(a.claro(0.45f), Offset(pie.x - ow * 0.5f, y - 1.8f * u), Size(ow, 0.9f * u))
            drawRect(a.sombra(0.3f), Offset(pie.x - ow * 0.5f, y + 1f * u), Size(ow, 0.8f * u))
        }
    }
    // La tapa, un poco más ancha, y la paleta del arroz atravesada encima.
    val tapa = Rect(pie.x - ow * 0.54f, pie.y - oh - w * 0.016f, pie.x + ow * 0.54f, pie.y - oh)
    drawRect(a.luz(mezcla(HINOKI, WASHI, 0.35f), k + 0.05f, f), tapa.topLeft, tapa.size)
    drawRect(a.sombra(0.3f), Offset(tapa.left, tapa.bottom - 1f * u), Size(tapa.width, 1f * u))
    drawRect(a.claro(0.4f), tapa.topLeft, Size(tapa.width, 1f * u))
    val pal = Path().apply {
        moveTo(pie.x - ow * 0.4f, tapa.top + 0.6f * u)
        lineTo(pie.x + ow * 0.1f, tapa.top - 2.2f * u)
        quadraticTo(pie.x + ow * 0.42f, tapa.top - 5.5f * u, pie.x + ow * 0.44f, tapa.top - 0.6f * u)
        quadraticTo(pie.x + ow * 0.3f, tapa.top + 1.4f * u, pie.x + ow * 0.1f, tapa.top + 0.4f * u)
        lineTo(pie.x - ow * 0.4f, tapa.top + 2f * u); close()
    }
    drawPath(corridoEdo(pal, 1.2f * u, 1.2f * u), a.sombra(0.3f))
    drawPath(pal, a.luz(mezcla(HINOKI, WASHI, 0.15f), k + 0.1f, f))
}

/**
 * La escoba de la mañana, apoyada en el poste de la derecha: el palo de bambú y la cabeza de mijo atada con hilo rojo, cortada
 * por el borde. Cruza el pie del estante.
 */
internal fun DrawScope.escobaEdo(g: MarcoEdo, a: AireEdo, k: Float) {
    val u = density; val w = g.w
    val arriba = Offset(w * 0.935f, (g.fondo - g.fila * 1.15f).coerceAtLeast(g.ch + 4f * u))
    val cuello = Offset(w * 0.975f, g.suelo - w * 0.075f)
    val haciaLuz = a.ladoLuz(cuello.x, w)
    drawLine(a.sombra(0.35f), arriba + Offset(-haciaLuz * 4f * u, 2f * u), cuello + Offset(-haciaLuz * 4f * u, 2f * u), 3f * u, StrokeCap.Round)
    drawLine(a.luz(mezcla(WARA, MATSU, 0.25f), k), arriba, cuello, 3f * u, StrokeCap.Round)
    drawLine(a.claro(0.45f), arriba + Offset(-0.8f * u, 0f), cuello + Offset(-0.8f * u, 0f), 0.8f * u)
    // Los nudos del bambú.
    for (j in 1..3) {
        val t = j / 4f
        val p = Offset(arriba.x + (cuello.x - arriba.x) * t, arriba.y + (cuello.y - arriba.y) * t)
        drawLine(a.luz(HINOKI_OSC, k), p + Offset(-2f * u, 0f), p + Offset(2f * u, 0f), 1f * u)
    }
    // La cabeza: las espigas de mijo abiertas en abanico hasta el suelo, atadas con tres vueltas de hilo rojo.
    val pie = g.suelo + w * 0.07f
    val cabeza = poligonoEdo(cuello.x - w * 0.012f, cuello.y, cuello.x + w * 0.014f, cuello.y, cuello.x + w * 0.06f, pie, cuello.x - w * 0.055f, pie)
    drawPath(corridoEdo(cabeza, -haciaLuz * 4f * u, 2f * u), a.sombra(0.3f))
    drawPath(cabeza, a.luz(WARA, k))
    clipPath(cabeza) {
        for (j in 0 until 16) {
            val t = j / 15f
            drawLine(a.luz(mezcla(WARA, HINOKI_OSC, 0.5f), k).copy(alpha = 0.7f), Offset(cuello.x - w * 0.01f + w * 0.022f * t, cuello.y), Offset(cuello.x - w * 0.058f + w * 0.12f * t, pie), 0.6f * u)
        }
        drawRect(a.sombra(0.2f), Offset(cuello.x + w * 0.01f, cuello.y), Size(w * 0.06f, pie - cuello.y))
    }
    for (j in 0 until 3) {
        val y = cuello.y + w * (0.008f + 0.009f * j)
        drawLine(a.luz(SHU, k), Offset(cuello.x - w * (0.014f + 0.006f * j), y), Offset(cuello.x + w * (0.016f + 0.006f * j), y), 1.2f * u)
    }
}

// ---------------------------------------------------------------- en los huecos libres

/**
 * Lo que se deja en el hueco libre de una repisa, de tres clases según la repisa: la botella de sake prestada con su tacita;
 * el abanico redondo apoyado en el fondo con la toalla doblada; los cuencos de laca negra con sus palillos.
 */
internal fun DrawScope.huecoLibreEdo(k: Int, r: Rect, oc: Float, a: AireEdo, kc: Float, kf: Float) {
    val u = density
    val libre = r.right - oc
    if (libre < 34f * u) return
    val s = minOf(r.height * 0.95f, libre * 1.5f)
    val x = oc + libre * (if (libre > s * 1.6f) 0.62f else 0.5f)
    val base = r.bottom
    when (k % 3) {
        0 -> botellaSakeEdo(Offset(x, base), s, a, kc, kf)
        1 -> abanicoEdo(Offset(x, base), s, a, kc, kf, k)
        else -> cuencosEdo(Offset(x, base), s, a, kc, kf)
    }
}

/** La botella de sake de barro que presta la tienda (sin letrero), vidriada de caramelo arriba, y una tacita al lado. */
private fun DrawScope.botellaSakeEdo(pie: Offset, s: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val bh = s * 0.6f; val bw = bh * 0.46f
    val c = Offset(pie.x - bw * 0.25f, pie.y)
    drawOval(a.sombra(0.4f), Offset(c.x - bw * 0.5f, pie.y - 2.4f * u), Size(bw * 1.2f, 4.8f * u))
    val cuerpo = Path().apply {
        moveTo(c.x - bw * 0.12f, pie.y - bh)
        lineTo(c.x - bw * 0.1f, pie.y - bh * 0.78f)
        cubicTo(c.x - bw * 0.62f, pie.y - bh * 0.6f, c.x - bw * 0.56f, pie.y, c.x - bw * 0.3f, pie.y)
        lineTo(c.x + bw * 0.3f, pie.y)
        cubicTo(c.x + bw * 0.56f, pie.y, c.x + bw * 0.62f, pie.y - bh * 0.6f, c.x + bw * 0.1f, pie.y - bh * 0.78f)
        lineTo(c.x + bw * 0.12f, pie.y - bh); close()
    }
    drawPath(cuerpo, a.luz(mezcla(KAMI, HINOKI_OSC, 0.3f), k, f))
    clipPath(cuerpo) {
        // El vidriado de caramelo que baja chorreado hasta media panza.
        val vidrio = Path().apply {
            moveTo(c.x - bw, pie.y - bh * 1.1f); lineTo(c.x + bw, pie.y - bh * 1.1f); lineTo(c.x + bw, pie.y - bh * 0.5f)
            quadraticTo(c.x + bw * 0.2f, pie.y - bh * 0.42f, c.x, pie.y - bh * 0.5f)
            quadraticTo(c.x - bw * 0.3f, pie.y - bh * 0.36f, c.x - bw, pie.y - bh * 0.48f); close()
        }
        drawPath(vidrio, a.luz(Color(0xFF6E3A1E), k, f))
        drawRect(a.claro(0.4f), Offset(c.x - bw * 0.36f, pie.y - bh * 0.7f), Size(bw * 0.1f, bh * 0.45f))
        drawRect(a.sombra(0.3f), Offset(c.x + bw * 0.15f, pie.y - bh), Size(bw * 0.4f, bh))
    }
    filoFarolEdo(Offset(c.x + bw * 0.42f, pie.y - bh * 0.45f), Offset(c.x + bw * 0.36f, pie.y - bh * 0.12f), k, a)
    // La tacita.
    val tc = Offset(c.x + bw * 0.75f, pie.y)
    val t = poligonoEdo(tc.x - bw * 0.22f, pie.y - bh * 0.16f, tc.x + bw * 0.22f, pie.y - bh * 0.16f, tc.x + bw * 0.13f, pie.y, tc.x - bw * 0.13f, pie.y)
    drawPath(corridoEdo(t, 1.6f * u, 0.8f * u), a.sombra(0.3f))
    drawPath(t, a.luz(mezcla(WASHI, KAMI, 0.2f), k, f))
    drawLine(a.luz(AI, k, f), Offset(tc.x - bw * 0.2f, pie.y - bh * 0.13f), Offset(tc.x + bw * 0.2f, pie.y - bh * 0.13f), 0.8f * u)
}

/** El abanico redondo de papel y varillas de bambú, recostado contra el fondo, y la toalla de algodón doblada delante. */
private fun DrawScope.abanicoEdo(pie: Offset, s: Float, a: AireEdo, k: Float, f: Float, semilla: Int) {
    val u = density
    val rr = s * 0.23f
    val tw = s * 0.42f; val th = s * 0.12f
    val tr = Rect(pie.x - tw * 0.55f, pie.y - th, pie.x + tw * 0.45f, pie.y)
    // El abanico: el mango se apoya en la toalla y la hoja se recuesta contra el papel del fondo, ladeada a la derecha.
    val base = Offset(tr.left + tw * 0.3f, tr.top + 1f * u)
    val largo = s * 0.22f
    rotate(24f, base) {
        val c = Offset(base.x, base.y - largo - rr * 0.85f)
        drawCircle(a.sombra(0.28f), rr, c + Offset(5f * u, 3f * u))
        drawLine(a.sombra(0.25f), base + Offset(4f * u, 2f * u), c + Offset(4f * u, rr * 0.8f + 2f * u), 2.2f * u)
        drawLine(a.luz(mezcla(WARA, HINOKI_OSC, 0.3f), k, f), base, Offset(c.x, c.y + rr * 0.8f), 2.2f * u, StrokeCap.Round)
        drawCircle(a.luz(if (semilla % 2 == 0) AI else mezcla(WASHI, KAMI, 0.2f), k, f), rr, c)
        // Las varillas que se abren desde el mango y el brillo del papel del lado de la luz.
        for (j in 0 until 11) {
            val ang = Math.toRadians((200.0 + 140.0 * j / 10.0)).toFloat()
            drawLine(a.sombra(0.22f), Offset(c.x, c.y + rr * 0.7f), Offset(c.x + rr * 0.96f * kotlin.math.cos(ang), c.y + rr * 0.96f * kotlin.math.sin(ang)), 0.5f * u)
        }
        drawArc(a.claro(0.35f), 190f, 80f, false, Offset(c.x - rr * 0.9f, c.y - rr * 0.9f), Size(rr * 1.8f, rr * 1.8f), style = Stroke(1f * u))
        drawCircle(a.luz(KURI, k, f), rr, c, style = Stroke(1.2f * u))
    }
    // La toalla doblada: tres dobleces de algodón añil con rayas claras, delante del mango.
    drawRect(a.sombra(0.35f), tr.topLeft + Offset(2f * u, 1.4f * u), tr.size)
    drawRoundRect(a.luz(mezcla(AI, KINARI, 0.15f), k, f), tr.topLeft, tr.size, CornerRadius(2f * u))
    for (j in 1..2) drawLine(a.sombra(0.35f), Offset(tr.left + 1f * u, tr.top + th * j / 3f), Offset(tr.right - 1f * u, tr.top + th * j / 3f), 0.7f * u)
    var x = tr.left + 3f * u
    while (x < tr.right) { drawLine(a.luz(WASHI, k, f).copy(alpha = 0.4f), Offset(x, tr.top), Offset(x, tr.bottom), 0.8f * u); x += 5f * u }
    drawRect(a.claro(0.3f), tr.topLeft, Size(tw, 1f * u))
}

/** Dos cuencos de laca negra con su tapa, uno sobre otro, y los palillos atravesados encima. */
private fun DrawScope.cuencosEdo(pie: Offset, s: Float, a: AireEdo, k: Float, f: Float) {
    val u = density
    val cw = s * 0.34f; val ch = s * 0.16f
    drawOval(a.sombra(0.4f), Offset(pie.x - cw * 0.6f, pie.y - 2.4f * u), Size(cw * 1.3f, 4.8f * u))
    for (i in 0 until 2) {
        val y1 = pie.y - ch * i * 0.95f
        val cuenco = Path().apply {
            moveTo(pie.x - cw / 2, y1 - ch)
            lineTo(pie.x + cw / 2, y1 - ch)
            cubicTo(pie.x + cw / 2, y1 - ch * 0.3f, pie.x + cw * 0.3f, y1, pie.x + cw * 0.18f, y1)
            lineTo(pie.x - cw * 0.18f, y1)
            cubicTo(pie.x - cw * 0.3f, y1, pie.x - cw / 2, y1 - ch * 0.3f, pie.x - cw / 2, y1 - ch)
            close()
        }
        drawPath(cuenco, a.luz(mezcla(SUMI, KURI, 0.3f), k, f))
        drawArc(a.claro(0.55f), 180f, 70f, false, Offset(pie.x - cw * 0.44f, y1 - ch * 1.6f), Size(cw * 0.88f, ch * 1.4f), style = Stroke(1f * u))
        drawRect(a.luz(mezcla(KURI, COBRE, 0.3f), k, f), Offset(pie.x - cw / 2, y1 - ch - 1f * u), Size(cw, 1.6f * u))
        filoFarolEdo(Offset(pie.x + cw * 0.45f, y1 - ch * 0.8f), Offset(pie.x + cw * 0.2f, y1 - ch * 0.1f), k, a)
    }
    // Los palillos de madera, atravesados.
    val yP = pie.y - ch * 1.95f - 2f * u
    drawLine(a.luz(HINOKI, k + 0.1f, f), Offset(pie.x - cw * 0.7f, yP + 1.6f * u), Offset(pie.x + cw * 0.65f, yP - 1.2f * u), 1.3f * u, StrokeCap.Round)
    drawLine(a.luz(HINOKI_OSC, k + 0.1f, f), Offset(pie.x - cw * 0.66f, yP + 3.2f * u), Offset(pie.x + cw * 0.7f, yP + 0.6f * u), 1.3f * u, StrokeCap.Round)
}
