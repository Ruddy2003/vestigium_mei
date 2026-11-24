package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El rincón de la libreta de Revolución ([MuebleLibretaRevolucion]), lo fijo: el yeso ocre rosado con la luz de la ventana y el
 * zócalo pintado de sangre de toro, la viga que cruza arriba, el pie derecho de la prensa con sus pernos y el ovillo de
 * bramante, el suelo de liais, y el pupitre de roble del regente: el tablero inclinado (con la luz de la ventana y la prueba
 * que se está corrigiendo), el reborde que sujeta el registro, el faldón con su cajón, las patas, el travesaño con los
 * registros viejos y la repisa de arriba con el cuerno de tinta, la salvadera y la palmatoria. La llama se pinta en vivo.
 */
internal object RinconLibretaRevolucion {
    private val P = PaletaRevolucion
    /** El boj de la salvadera. */
    private val BOJ = Color(0xFFD9B678)

    private fun SitioLibretaRevolucion.k(a: AireRevolucion, p: Offset) = MuebleLibretaRevolucion.cerca(p, this, a)
    private fun SitioLibretaRevolucion.c(a: AireRevolucion, col: Color, f: Float, p: Offset) = a.dia(col, f, k(a, p))
    private fun SitioLibretaRevolucion.pincel(a: AireRevolucion, col: Color, f: Float, pano: Float = 0f) = MuebleLibretaRevolucion.pincel(col, f, this, a, pano)

    fun DrawScope.rincon(s: SitioLibretaRevolucion, a: AireRevolucion, ac: AireRevolucion) {
        pared(s, a)
        viga(s, ac)
        zocalo(s, ac)
        suelo(s, ac)
        prensa(s, ac)
        pupitre(s, ac)
        galeria(s, ac)
        if (a.oscuro) {
            // El aura de la vela en el aire, chica: el charco ya está en cada cosa.
            val r = 46f * s.U
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.16f), P.LLAMA.copy(alpha = 0f)), s.llama, r), r, s.llama)
        }
    }

    // ---------------------------------------------------------------- la pared

    /** El yeso de la pared con su luz (la ventana de día, la vela y la luna de noche), el moteado de la cal y la luz de la ventana. */
    private fun DrawScope.pared(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val w = s.w; val U = s.U
        val base = mezcla(P.YESO, P.YESO_SOMBRA, 0.25f)
        drawRect(s.pincel(a, base, 0f), Offset.Zero, Size(w, s.suelo))
        // La luz de la ventana: la penumbra y los cuatro paños, del mismo yeso un paso más claro.
        val fz = s.pano.fuerza(a)
        drawPath(s.pano.forma(0f, 0f, 0.022f), s.pincel(a, base, 0f, fz * 0.45f))
        drawPath(s.pano.forma(0f, 0f), s.pincel(a, base, 0f, fz))
        // El moteado de la cal: manchas grandes y suaves.
        val motas = Path()
        for (j in 0 until 12) {
            val mx = w * azar(9600, j); val my = s.suelo * azar(9601, j)
            val mw = (40f + 54f * azar(9602, j)) * U; val mh = mw * 0.35f
            motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
        }
        drawPath(motas, Color(0xFFF2E2D4).copy(alpha = 0.3f), blendMode = BlendMode.Multiply)
    }

    /** La viga de roble que cruza arriba, con su cara de abajo (la que mira la vela) y su sombra en el yeso. */
    private fun DrawScope.viga(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val w = s.w; val U = s.U
        drawRect(s.pincel(a, P.ROBLE_OSC, -0.1f), Offset.Zero, Size(w, s.viga))
        drawRect(s.pincel(a, P.ROBLE, 0f), Offset(0f, s.viga - 2.6f * U), Size(w, 2.6f * U))
        val vetas = Path()
        for (j in 0 until 2) { val y = (2f + 2.6f * j) * U; vetas.moveTo(0f, y); var x = 0f; while (x < w) { x += 40f * U; vetas.lineTo(x, y + (azar(9620 + j, (x / U).toInt()) - 0.5f) * 1.4f * U) } }
        drawPath(vetas, a.sombra(0.3f), style = Stroke(0.8f * U))
        drawRect(a.sombra(0.4f), Offset(0f, s.viga), Size(w, 2f * U))
        drawRect(a.sombra(0.14f), Offset(0f, s.viga + 2f * U), Size(w, 5f * U))
    }

    /** El zócalo de la pared, pintado de sangre de toro: la moldura de arriba y los cuarterones con su junquillo. */
    private fun DrawScope.zocalo(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val w = s.w; val U = s.U
        drawRect(s.pincel(a, P.SANGRE, -0.05f), Offset(0f, s.zocalo), Size(w, s.suelo - s.zocalo))
        drawRect(s.pincel(a, P.SANGRE_LUZ, 0f), Offset(0f, s.zocalo), Size(w, 4f * U))
        drawRect(s.pincel(a, P.SANGRE_OSC, 0f), Offset(0f, s.zocalo + 4f * U), Size(w, 3f * U))
        drawRect(a.claro(0.35f), Offset(0f, s.zocalo), Size(w, 1f * U))
        // Los cuarterones, con su luz arriba a la izquierda y su sombra abajo a la derecha.
        val luz = Path(); val sombra = Path()
        var x = 6f * U
        while (x < w) {
            val r = Rect(x, s.zocalo + 13f * U, x + 58f * U, s.suelo - 8f * U)
            sombra.addRect(Rect(r.left, r.bottom - 1.4f * U, r.right, r.bottom)); sombra.addRect(Rect(r.right - 1.4f * U, r.top, r.right, r.bottom))
            luz.addRect(Rect(r.left, r.top, r.right, r.top + 1f * U)); luz.addRect(Rect(r.left, r.top, r.left + 1f * U, r.bottom))
            x += 66f * U
        }
        drawPath(sombra, a.sombra(0.35f)); drawPath(luz, a.claro(0.25f))
        drawRect(a.sombra(0.5f), Offset(0f, s.suelo - 2f * U), Size(w, 2f * U))
    }

    /** Las losas de liais en rombo con sus tacos de pizarra, en perspectiva, y la sombra del pupitre. */
    private fun DrawScope.suelo(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val w = s.w; val h = s.h; val U = s.U
        drawRect(s.pincel(a, P.PIEDRA, -0.05f), Offset(0f, s.suelo), Size(w, h - s.suelo))
        val fuga = Offset(s.izq + s.pw / 2, s.suelo - 300f * U)
        val juntas = Path(); val tacos = Path()
        val ys = listOf(s.suelo, s.suelo + 9f * U, s.suelo + 22f * U, h)
        ys.forEach { y -> juntas.moveTo(0f, y); juntas.lineTo(w, y) }
        var xb = -200f * U
        while (xb < w + 200f * U) {
            fun en(y: Float) = fuga.x + (xb - fuga.x) * (y - fuga.y) / (h - fuga.y)
            juntas.moveTo(en(s.suelo), s.suelo); juntas.lineTo(en(h), h)
            ys.forEach { y -> val tx = en(y); val ta = 4f * U * (y - fuga.y) / (h - fuga.y); tacos.addPath(poligonoRevolucion(tx, y - ta * 0.5f, tx + ta, y, tx, y + ta * 0.5f, tx - ta, y)) }
            xb += 48f * U
        }
        drawPath(juntas, s.pincel(a, P.PIEDRA_OSC, -0.2f), style = Stroke(1f * U))
        drawPath(tacos, s.pincel(a, P.PIZARRA, 0f))
        drawRect(a.sombra(0.4f), Offset(0f, s.suelo), Size(w, 4f * U))
        // La sombra del pupitre en el suelo: hacia la derecha de día; de noche, hacia la izquierda (la vela está a la derecha).
        val dx = if (a.oscuro) -16f * U else 14f * U
        drawOval(a.sombra(if (a.oscuro) 0.5f else 0.36f), Offset(s.izq + dx, s.pata - 9f * U), Size(s.pw, 15f * U))
    }

    // ---------------------------------------------------------------- la prensa

    /** El pie derecho de la prensa de madera: roble con su chaflán, los pernos de hierro y el ovillo de bramante colgado. */
    private fun DrawScope.prensa(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val U = s.U
        val r = Rect(s.prensaIzq, 0f, s.prensaIzq + 38f * U, s.pata + 2f * U)
        if (r.left > s.w) return
        // Su sombra en la pared, a la derecha (de día la ventana y de noche la vela están a su izquierda).
        drawRect(a.sombra(0.3f), Offset(r.right, s.viga), Size(if (a.oscuro) 20f * U else 12f * U, s.suelo - s.viga))
        drawRect(s.pincel(a, P.ROBLE_OSC, 0f), r.topLeft, r.size)
        drawRect(s.pincel(a, P.ROBLE, 0.04f), r.topLeft, Size(6f * U, r.height))
        if (a.oscuro) {
            // El filo de la vela en la cara que la mira: fuerte a la altura de la llama, se apaga hacia abajo.
            val pico = (s.llama.y / r.height).coerceIn(0.02f, 0.9f)
            drawRect(Brush.verticalGradient(0f to a.filo(0.35f), pico to a.filo(0.75f), 0.6f to a.filo(0.18f), 1f to a.filo(0.04f), startY = 0f, endY = r.bottom),
                r.topLeft, Size(1.6f * U, r.height))
        }
        drawRect(a.claro(0.3f), Offset(r.left, 0f), Size(1.2f * U, r.height))
        drawRect(a.sombra(0.3f), Offset(r.left + 6f * U, 0f), Size(1.2f * U, r.height))
        // Las vetas, la mancha de tinta donde la agarran y el pie ensamblado abajo.
        val vetas = Path()
        for (j in 0 until 4) { val x = r.left + (10f + 7f * j) * U; vetas.moveTo(x, 0f); vetas.quadraticTo(x + 3f * U, r.height * 0.5f, x - 1f * U, r.height) }
        drawPath(vetas, a.sombra(0.25f), style = Stroke(0.8f * U))
        val pie = Rect(r.left - 8f * U, s.pata - 18f * U, r.right + 8f * U, s.pata + 2f * U)
        drawRect(s.c(a, P.ROBLE_OSC, -0.1f, pie.center), pie.topLeft, pie.size)
        drawRect(a.claro(0.25f), pie.topLeft, Size(pie.width, 1.2f * U))
        // Los pernos de hierro con su arandela cuadrada.
        listOf(30f, 84f, 196f).forEach { y ->
            val c = Offset(r.center.x + 2f * U, y * U)
            val k = s.k(a, c)
            drawRect(a.sombra(0.45f), Offset(c.x - 4.4f * U, c.y - 3.6f * U), Size(11f * U, 10f * U))
            drawRect(a.dia(P.HIERRO, 0f, k), Offset(c.x - 5.5f * U, c.y - 5f * U), Size(11f * U, 10f * U))
            drawCircle(a.dia(P.HIERRO_LUZ, -0.2f, k), 3f * U, c)
            drawCircle(a.brillo(0.5f, k), 1f * U, c - Offset(1f * U, 1f * U))
        }
        // El ovillo de bramante, colgado de un clavo, con la punta suelta.
        val cl = Offset(r.left + 12f * U, 140f * U)
        val o = Offset(cl.x - 2f * U, cl.y + 18f * U)
        val ko = s.k(a, o)
        drawLine(a.dia(P.BRAMANTE, 0f, ko), cl, o, 1.2f * U)
        drawCircle(a.sombra(0.35f), 9.5f * U, o + Offset(3f * U, 2f * U))
        drawCircle(a.dia(P.BRAMANTE, 0f, ko), 9.5f * U, o)
        for (j in 0 until 4) drawArc(a.dia(P.BRAMANTE_OSC, 0f, ko), 200f + 20f * j, 120f, false, Offset(o.x - 9.5f * U + j * U, o.y - 8f * U + j * 1.4f * U), Size(19f * U - 2f * j * U, 14f * U), style = Stroke(0.8f * U))
        drawCircle(a.brillo(0.4f, ko), 2.4f * U, o - Offset(3f * U, 3f * U))
        val punta = Path().apply { moveTo(o.x + 4f * U, o.y + 8f * U); quadraticTo(o.x + 2f * U, o.y + 20f * U, o.x + 7f * U, o.y + 30f * U) }
        drawPath(punta, a.dia(P.BRAMANTE, 0f, ko), style = Stroke(1.1f * U, cap = StrokeCap.Round))
        clavoRevolucion(cl, 1.6f * U, a, s.k(a, cl))
    }

    // ---------------------------------------------------------------- el pupitre

    /** El tablero inclinado del pupitre, de la repisa al reborde, algo más angosto arriba (está más lejos). */
    fun tablero(s: SitioLibretaRevolucion) = poligonoRevolucion(s.izq + 4f * s.U, s.galeriaAbajo, s.der - 4f * s.U, s.galeriaAbajo, s.der, s.reborde, s.izq, s.reborde)

    /** El pupitre de roble: el tablero con la luz y la prueba, sus cantos, el reborde, el faldón con su cajón, las patas y el travesaño. */
    private fun DrawScope.pupitre(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val U = s.U
        val k0 = s.k(a, Offset(s.izq + s.pw / 2, 120f * U))
        // La sombra del pupitre en la pared: de día a la derecha y abajo; de noche, a la izquierda (la vela está a la derecha).
        val sx = if (a.oscuro) -16f * U else 12f * U
        val sombra = poligonoRevolucion(s.izq + 6f * U + sx, s.galeria - 4f * U, s.der - 6f * U + sx, s.galeria - 4f * U, s.der + sx, s.suelo, s.izq + sx, s.suelo)
        clipRect(0f, 0f, s.prensaIzq, s.suelo) { drawPath(sombra, a.sombra(if (a.oscuro) 0.35f else 0.22f)) }
        // Las patas de atrás (asoman bajo el faldón, más oscuras) y el travesaño con los registros viejos.
        listOf(s.izq + 22f * U, s.der - 32f * U).forEach { x -> drawRect(a.dia(P.ROBLE_OSC, -0.3f, k0), Offset(x, s.faldonAbajo), Size(9f * U, s.suelo + 6f * U - s.faldonAbajo)) }
        val trav = Rect(s.izq + 12f * U, 240f * U, s.der - 12f * U, 246f * U)
        drawRect(a.sombra(0.35f), Offset(s.izq, s.faldonAbajo), Size(s.pw, trav.top - s.faldonAbajo))
        viejos(s, a, trav)
        drawRect(a.dia(P.ROBLE_OSC, -0.1f, k0), trav.topLeft, trav.size)
        drawRect(a.claro(0.25f), trav.topLeft, Size(trav.width, 1f * U))
        // El tablero: el roble con su luz, los paños de la ventana corridos hacia delante, las vetas y la junta de las tablas.
        val tab = tablero(s)
        val fz = s.pano.fuerza(a)
        clipPath(tab) {
            drawRect(s.pincel(a, P.ROBLE, -0.05f), Offset(s.izq, s.galeriaAbajo), Size(s.pw, s.reborde - s.galeriaAbajo))
            if (!a.oscuro) {
                val d = s.pano.tablero
                drawPath(s.pano.forma(d.x, d.y, 0.022f), s.pincel(a, P.ROBLE, -0.05f, fz * 0.45f))
                drawPath(s.pano.forma(d.x, d.y), s.pincel(a, P.ROBLE, -0.05f, fz))
            }
            val vetas = Path()
            for (j in 0 until 8) { val y = s.galeriaAbajo + (9f + 19f * j) * U; vetas.moveTo(s.izq, y); vetas.quadraticTo(s.izq + s.pw * 0.5f, y + (azar(9640, j) - 0.5f) * 8f * U, s.der, y + 2f * U) }
            drawPath(vetas, a.sombra(0.16f), style = Stroke(1f * U))
            drawLine(a.sombra(0.35f), Offset(s.izq, s.reborde - 64f * U), Offset(s.der, s.reborde - 64f * U), 1f * U)
            drawLine(a.claro(0.2f), Offset(s.izq, s.reborde - 63f * U), Offset(s.der, s.reborde - 63f * U), 0.8f * U)
            // El roce oscuro donde se apoyan los antebrazos, abajo.
            drawRect(a.sombra(0.1f), Offset(s.izq, s.reborde - 16f * U), Size(s.pw, 16f * U))
            prueba(s, a)
        }
        // Los cantos del tablero: el de la izquierda mira a la ventana; el de la derecha, a la vela.
        drawPath(poligonoRevolucion(s.izq - 2f * U, s.galeriaAbajo + 2f * U, s.izq + 4f * U, s.galeriaAbajo, s.izq, s.reborde, s.izq - 5f * U, s.reborde + 1f * U), s.c(a, P.ROBLE, 0.12f, Offset(s.izq, 110f * U)))
        drawPath(poligonoRevolucion(s.der - 4f * U, s.galeriaAbajo, s.der + 2f * U, s.galeriaAbajo + 2f * U, s.der + 5f * U, s.reborde + 1f * U, s.der, s.reborde), s.c(a, P.ROBLE_OSC, -0.1f, Offset(s.der, 110f * U)))
        if (a.oscuro) drawLine(a.filo(0.6f * s.k(a, Offset(s.der, 80f * U))), Offset(s.der - 4f * U, s.galeriaAbajo), Offset(s.der, s.reborde), 1.4f * U)
        // El reborde que sujeta el registro: su cara de arriba (con luz) y la de delante.
        val reb = Rect(s.izq - 6f * U, s.reborde, s.der + 6f * U, s.faldon)
        drawRect(s.pincel(a, P.ROBLE, 0.04f), reb.topLeft, reb.size)
        drawRect(s.pincel(a, P.ROBLE, 0.2f), reb.topLeft, Size(reb.width, 2.4f * U))
        if (a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.filo(0f), a.filo(0.5f), a.filo(0.1f)), reb.left, reb.right), reb.topLeft, Size(reb.width, 1.2f * U))
        drawRect(a.sombra(0.4f), Offset(reb.left, reb.bottom - 1.6f * U), Size(reb.width, 1.6f * U))
        // El faldón con su moldura y el cajón con su tirador de latón.
        val fal = Rect(s.izq - 4f * U, s.faldon, s.der + 4f * U, s.faldonAbajo)
        drawRect(s.pincel(a, P.ROBLE, -0.02f), fal.topLeft, fal.size)
        drawRect(a.sombra(0.3f), fal.topLeft, Size(fal.width, 1.6f * U))
        drawRect(a.sombra(0.45f), Offset(fal.left, fal.bottom - 2f * U), Size(fal.width, 2f * U))
        val cx = s.izq + s.pw / 2
        val caj = Rect(cx - 50f * U, fal.top + 3f * U, cx + 50f * U, fal.bottom - 4f * U)
        drawRect(a.sombra(0.5f), caj.topLeft, caj.size, style = Stroke(1.2f * U))
        drawRect(a.claro(0.25f), Offset(caj.left, caj.bottom), Size(caj.width, 0.8f * U))
        val tir = caj.center
        drawCircle(a.sombra(0.4f), 3.4f * U, tir + Offset(0.8f * U, 1f * U))
        drawCircle(s.c(a, P.LATON, 0f, tir), 3f * U, tir)
        destelloRevolucion(tir - Offset(1f * U, 1f * U), 1f * U, a, s.k(a, tir))
        // Las patas de delante, afiladas, con su anillo.
        listOf(s.izq + 2f * U, s.der - 14f * U).forEach { x ->
            val k = s.k(a, Offset(x, 236f * U))
            val pata = poligonoRevolucion(x, s.faldonAbajo, x + 12f * U, s.faldonAbajo, x + 9f * U, s.pata, x + 3f * U, s.pata)
            drawPath(pata, a.dia(P.ROBLE, 0f, k))
            clipPath(pata) {
                drawRect(a.claro(0.3f), Offset(x, s.faldonAbajo), Size(3f * U, s.pata - s.faldonAbajo))
                drawRect(a.sombra(0.35f), Offset(x + 8f * U, s.faldonAbajo), Size(4f * U, s.pata - s.faldonAbajo))
                drawRect(a.dia(P.ROBLE_OSC, 0f, k), Offset(x, s.faldonAbajo + 6f * U), Size(12f * U, 2.4f * U))
            }
        }
    }

    /** La prueba que se está corrigiendo, suelta en el tablero a la derecha del registro (el registro la tapa en parte). */
    private fun DrawScope.prueba(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val U = s.U
        val c = Offset(s.registro.right + 32f * U, 138f * U)
        if (c.x - 20f * U > s.der) return
        rotate(7f, c) {
            val pr = Rect(c.x - 25f * U, c.y - 34f * U, c.x + 25f * U, c.y + 34f * U)
            val k = s.k(a, pr.center)
            drawRect(a.sombra(0.28f), Offset(pr.left + (if (a.oscuro) -3f else 3f) * U, pr.top + 3f * U), pr.size)
            drawRect(a.dia(mezcla(P.PAPEL, P.TOSTADO, 0.2f), 0f, k), pr.topLeft, pr.size)
            drawRect(a.dia(P.TINTA, 0f, k).copy(alpha = 0.7f), Offset(pr.left + 7f * U, pr.top + 7f * U), Size(26f * U, 3.4f * U))
            val renglones = Path()
            var y = pr.top + 15f * U; var j = 0
            while (y < pr.bottom - 6f * U) {
                var x = pr.left + 6f * U; var q = 0
                while (x < pr.right - 12f * U) { val l = (3f + 6f * azar(9630 + j, q)) * U; renglones.moveTo(x, y); renglones.lineTo(minOf(x + l, pr.right - 12f * U), y); x += l + 1.6f * U; q++ }
                y += 4.2f * U; j++
            }
            drawPath(renglones, a.dia(P.TINTA, 0f, k).copy(alpha = 0.5f), style = Stroke(1.2f * U))
            // Las correcciones a pluma en el margen: rayas que salen del renglón, en tinta roja.
            val rojo = a.dia(P.ROJO, 0f, k)
            listOf(0.3f, 0.52f, 0.76f).forEach { f ->
                val yy = pr.top + pr.height * f
                drawLine(rojo, Offset(pr.right - 16f * U, yy), Offset(pr.right - 4f * U, yy - 2f * U), 1f * U, StrokeCap.Round)
            }
            drawLine(rojo, Offset(pr.left + 14f * U, pr.top + pr.height * 0.52f + 1.5f * U), Offset(pr.left + 24f * U, pr.top + pr.height * 0.52f + 1.5f * U), 0.9f * U)
            drawRect(a.claro(0.3f), pr.topLeft, Size(pr.width, 1f * U))
            drawRect(a.sombra(0.12f), Offset(pr.left, pr.bottom - 10f * U), Size(pr.width, 10f * U))
        }
    }

    /** Los registros viejos en el travesaño, tumbados y atados con bramante: el lomo de becerro gastado con sus nervios. */
    private fun DrawScope.viejos(s: SitioLibretaRevolucion, a: AireRevolucion, trav: Rect) {
        val U = s.U
        var y = trav.top
        val x0 = s.izq + s.pw / 2 - 64f * U
        for (j in 0 until 3) {
            val g = (8f + 3f * azar(9650, j)) * U
            val r = Rect(x0 + (azar(9651, j) - 0.5f) * 8f * U, y - g, x0 + 110f * U + (azar(9652, j) - 0.5f) * 10f * U, y)
            val k = s.k(a, r.center) * 0.6f
            drawRect(a.dia(mezcla(Color(0xFF6E4630), P.TOSTADO, 0.25f * j), -0.2f, k), r.topLeft, r.size)
            drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - g * 0.3f), Size(r.width, g * 0.3f))
            listOf(0.2f, 0.5f, 0.8f).forEach { f -> drawRect(a.sombra(0.3f), Offset(r.left + r.width * f, r.top), Size(2.4f * U, r.height)) }
            y -= g
        }
    }

    // ---------------------------------------------------------------- la repisa

    /** La repisa de arriba del tablero: el respaldo y la repisa, el cuerno de tinta con su pluma, la salvadera y la palmatoria. */
    private fun DrawScope.galeria(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val U = s.U
        val resp = Rect(s.izq + 4f * U, s.galeria - 8f * U, s.der - 4f * U, s.galeria)
        drawRect(s.pincel(a, P.ROBLE_OSC, 0f), resp.topLeft, resp.size)
        drawRect(a.claro(0.3f), resp.topLeft, Size(resp.width, 1f * U))
        drawRect(s.pincel(a, P.ROBLE, 0.06f), Offset(resp.left, s.galeria), Size(resp.width, s.galeriaAbajo - s.galeria))
        drawRect(a.claro(0.4f), Offset(resp.left, s.galeria), Size(resp.width, 1f * U))
        if (a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.filo(0f), a.filo(0.75f), a.filo(0f)), s.vela.x - 150f * U, s.vela.x + 60f * U), Offset(resp.left, s.galeria), Size(resp.width, 1.2f * U))
        drawRect(a.sombra(0.45f), Offset(resp.left, s.galeriaAbajo - 1.6f * U), Size(resp.width, 1.6f * U))
        // La sombra de la repisa en el tablero.
        drawRect(a.sombra(0.25f), Offset(resp.left, s.galeriaAbajo), Size(resp.width, 3f * U))
        cuerno(s, a)
        salvadera(s, a)
        palmatoria(s, a)
    }

    /** El cuerno de tinta (un cuerno de buey cortado, con su anilla de cuero), con la pluma de ganso metida. */
    private fun DrawScope.cuerno(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val U = s.U
        val p = s.cuerno
        val k = s.k(a, p)
        contactoRevolucion(p, 22f * U, 3f * U, a, 0.4f, if (a.oscuro) -1f else 1f)
        // La pluma: el cañón y las barbas, inclinada hacia la izquierda.
        val pluma = Path().apply {
            moveTo(p.x + 1f * U, p.y - 16f * U)
            quadraticTo(p.x - 9f * U, p.y - 28f * U, p.x - 20f * U, p.y - 34f * U)
            quadraticTo(p.x - 8f * U, p.y - 32f * U, p.x + 3f * U, p.y - 17f * U); close()
        }
        drawPath(pluma, a.dia(P.PAPEL_LUZ, -0.05f, k))
        drawLine(a.dia(P.PAPEL_OSC, 0f, k), Offset(p.x + 2f * U, p.y - 16f * U), Offset(p.x - 19f * U, p.y - 33.5f * U), 0.8f * U)
        // El cuerno: de pie, ancho abajo, con la boca negra de tinta.
        val c = poligonoRevolucion(p.x - 7f * U, p.y, p.x + 7f * U, p.y, p.x + 5f * U, p.y - 17f * U, p.x - 5f * U, p.y - 17f * U)
        drawPath(c, a.dia(P.SEBO_OSC, -0.05f, k))
        clipPath(c) {
            cilindroRevolucion(Rect(p.x - 7f * U, p.y - 18f * U, p.x + 7f * U, p.y), a, k, if (a.oscuro) 1f else -1f, 1.3f)
            drawRect(a.dia(P.CUERO, 0f, k), Offset(p.x - 8f * U, p.y - 10f * U), Size(16f * U, 2.4f * U))
        }
        drawOval(a.dia(P.TINTA, 0f, k), Offset(p.x - 5f * U, p.y - 18.5f * U), Size(10f * U, 3f * U))
        destelloRevolucion(Offset(p.x - 3f * U, p.y - 12f * U), 1.2f * U, a, k)
    }

    /** La salvadera de boj: el cuerpo torneado y el platillo de arriba, cóncavo, con sus agujeros para la arenilla. */
    private fun DrawScope.salvadera(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val U = s.U
        val p = s.salvadera
        val k = s.k(a, p)
        contactoRevolucion(p, 18f * U, 2.6f * U, a, 0.38f, if (a.oscuro) -1f else 1f)
        val cuerpo = Path().apply {
            moveTo(p.x - 6f * U, p.y); lineTo(p.x + 6f * U, p.y)
            quadraticTo(p.x + 7f * U, p.y - 5f * U, p.x + 4f * U, p.y - 8f * U)
            quadraticTo(p.x + 2.6f * U, p.y - 10f * U, p.x + 5f * U, p.y - 12f * U)
            lineTo(p.x - 5f * U, p.y - 12f * U)
            quadraticTo(p.x - 2.6f * U, p.y - 10f * U, p.x - 4f * U, p.y - 8f * U)
            quadraticTo(p.x - 7f * U, p.y - 5f * U, p.x - 6f * U, p.y); close()
        }
        drawPath(cuerpo, a.dia(BOJ, 0f, k))
        clipPath(cuerpo) { cilindroRevolucion(Rect(p.x - 7f * U, p.y - 13f * U, p.x + 7f * U, p.y), a, k, if (a.oscuro) 1f else -1f, 1.2f) }
        drawRect(a.sombra(0.3f), Offset(p.x - 6f * U, p.y - 2.4f * U), Size(12f * U, 1f * U))
        // El platillo con su hueco y los agujeros (una corona de puntos, sin dibujo).
        drawOval(a.dia(BOJ, 0.1f, k), Offset(p.x - 6.4f * U, p.y - 14.6f * U), Size(12.8f * U, 4f * U))
        drawOval(a.dia(BOJ, -0.35f, k), Offset(p.x - 4.6f * U, p.y - 13.9f * U), Size(9.2f * U, 2.6f * U))
        for (j in 0 until 5) drawCircle(a.sombra(0.55f), 0.45f * U, Offset(p.x + (j - 2f) * 1.6f * U, p.y - 12.6f * U))
    }

    /** La palmatoria de hojalata con su asa de anillo y la vela de sebo, ya gastada, con su chorrera. */
    private fun DrawScope.palmatoria(s: SitioLibretaRevolucion, a: AireRevolucion) {
        val U = s.U
        val p = s.vela
        val k = if (a.oscuro) 1f else s.k(a, p)
        contactoRevolucion(p, 30f * U, 3.4f * U, a, if (a.oscuro) 0.25f else 0.4f, -1f)
        val vela = Rect(p.x - 3.4f * U, s.llama.y + 1.6f * U, p.x + 3.4f * U, p.y - 5f * U)
        drawRect(a.dia(P.SEBO, 0f, k), vela.topLeft, vela.size)
        drawRect(a.sombra(0.22f), Offset(vela.right - 2.4f * U, vela.top), Size(2.4f * U, vela.height))
        drawRect(a.claro(0.35f), Offset(vela.left + 0.8f * U, vela.top), Size(1f * U, vela.height))
        if (a.oscuro) drawRect(P.FUEGO.copy(alpha = 0.35f), Offset(vela.left, vela.top), Size(vela.width, 5f * U))
        drawLine(a.dia(P.SEBO, 0.05f, k), Offset(vela.left + 0.8f * U, vela.top + 1.5f * U), Offset(vela.left + 0.6f * U, vela.top + 7f * U), 1.4f * U, StrokeCap.Round)
        // El platillo, el cubo y el asa de anillo.
        drawOval(a.dia(P.HIERRO_LUZ, 0.1f, k), Offset(p.x - 14f * U, p.y - 4f * U), Size(28f * U, 5f * U))
        drawOval(a.dia(P.HIERRO_LUZ, -0.15f, k), Offset(p.x - 14f * U, p.y - 4f * U), Size(28f * U, 5f * U), style = Stroke(1f * U))
        drawRect(a.dia(P.HIERRO_LUZ, 0f, k), Offset(p.x - 5f * U, p.y - 8f * U), Size(10f * U, 5f * U))
        drawOval(a.dia(P.HIERRO_LUZ, -0.2f, k), Offset(p.x + 12f * U, p.y - 6f * U), Size(7f * U, 5f * U), style = Stroke(1.3f * U))
        destelloRevolucion(Offset(p.x - 8f * U, p.y - 2f * U), 1.2f * U, a, k)
        if (!a.lumbre) drawLine(P.TINTA, Offset(p.x, vela.top), Offset(p.x + 0.6f * U, vela.top - 3f * U), 0.9f * U, StrokeCap.Round)
    }

    /** La llama de la vela (de noche), en vivo, encima de todo. */
    fun DrawScope.llama(s: SitioLibretaRevolucion, a: AireRevolucion) {
        if (!a.lumbre) return
        llamaRevolucion(Offset(s.llama.x, s.llama.y + 2f * s.U), 4.4f * s.U, 1f)
    }
}
