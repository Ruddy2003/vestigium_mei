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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que rodea al módulo de estiba de Espacio ([MuebleEstanteEspacio]): la pared acolchada con el ojo de buey, la lámpara de
 * trabajo, lo que está sujeto a la pared y a la tabla alta (el ventilador a medio arreglar, el rotulador al aire, los
 * auriculares), lo de los huecos libres de las repisas y el pie.
 */
internal object PiezasEstanteEspacio {
    private val P = PaletaEstanteEspacio
    private val M = MuebleEstanteEspacio

    // ---------------------------------------------------------------- la pared

    /** Dónde van las dos cuadernas, en anchos del mueble, y su ancho. */
    private val CUADERNAS = listOf(0.335f, 0.735f)
    private const val CUADERNA = 0.026f

    /**
     * El casco por dentro: el acolchado de tela gris clara en paños entre las cuadernas, capitoneado (botones hundidos en los
     * cruces, costuras que ceden un poco, cada cojín con su bulto), con su ribete crudo; un parche cosido, la tela gastada
     * donde se agarran las manos y, arriba, la viga del techo. Va en la corona y al pie, que es lo que el módulo deja ver.
     */
    fun DrawScope.pared(a: AireEstanteEspacio, ch: Float, b: Rect, lado: Float, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density; val w = size.width; val h = size.height
        val lejos = luz(Offset(w * 0.5f, ch * 0.5f))
        val fondo = a.luz(P.ACOLCHADO, LuzEstanteEspacio(0.12f, lejos.f * 0.4f))
        listOf(0f to ch, b.top to h).forEachIndexed { zona, (y0, y1) ->
            if (y1 <= y0) return@forEachIndexed
            drawRect(fondo, Offset(0f, y0), Size(w, y1 - y0))
            val xs = listOf(0f) + CUADERNAS.flatMap { listOf(w * it, w * (it + CUADERNA)) } + listOf(w)
            for (pz in 0 until xs.size / 2) {
                val pano = Rect(xs[2 * pz], y0, xs[2 * pz + 1], y1)
                acolchado(pano, a, 700 + pz * 7 + zona)
            }
            // Las cuadernas: pletinas pintadas con sus agujeros de aligerar y sus remaches.
            CUADERNAS.forEachIndexed { i, fx -> cuaderna(Rect(w * fx, y0, w * (fx + CUADERNA), y1), a, i, luz(Offset(w * fx, (y0 + y1) / 2))) }
        }
        // La viga del techo, arriba de todo.
        val viga = Rect(0f, 0f, w, w * 0.03f)
        drawRect(a.luz(mezcla(P.CASCO_OSC, P.CREMA, 0.4f), luz(Offset(w * 0.2f, 0f))), viga.topLeft, viga.size)
        drawRect(a.claro(0.3f), Offset(0f, viga.bottom - viga.height * 0.4f), Size(w, 1f * u))
        drawRect(a.sombra(0.4f), Offset(0f, viga.bottom), Size(w, 3f * u))
        drawRect(a.sombra(0.15f), Offset(0f, viga.bottom), Size(w, 8f * u))
        var x = w * 0.04f
        while (x < w) { remacheEspacio(Offset(x, viga.center.y), 1.2f * u, a.luz(P.ACERO, luz(Offset(x, 0f))), a); x += maxOf(1f, w * 0.06f) }
        // Las manos gastan la tela junto al ojo de buey y bajo el pasamanos: más oscura y con pelusa.
        listOf(Rect(w * 0.03f, w * 0.38f, w * 0.3f, w * 0.5f) to 731, Rect(w * 0.77f, w * 0.37f, w * 0.99f, w * 0.47f) to 732).forEach { (r, sm) ->
            pinceladas(r, a.sombra(1f), sm, 12, u, 0.1f)
            pinceladas(r, a.claro(1f), sm + 1, 8, u, 0.08f)
        }
        // El parche cosido bajo el ojo de buey, un poco torcido, con su puntada.
        val pc = Rect(w * 0.05f, w * 0.425f, w * 0.15f, w * 0.495f)
        rotate(-5f, pc.center) {
            drawRect(a.sombra(0.3f), pc.topLeft + Offset(1.2f * u, 1.5f * u), pc.size)
            drawRect(a.luz(mezcla(P.ACOLCHADO, P.TELA_AZUL, 0.45f), luz(pc.center)), pc.topLeft, pc.size)
            drawRect(a.claro(0.2f), pc.topLeft, Size(pc.width, 1f * u))
            val d = pc.deflate(2f * u)
            var px = d.left
            while (px < d.right) { drawLine(a.luz(P.TELA, luz(pc.center)), Offset(px, d.top), Offset(px + 1.6f * u, d.top), 0.7f * u); drawLine(a.luz(P.TELA, luz(pc.center)), Offset(px, d.bottom), Offset(px + 1.6f * u, d.bottom), 0.7f * u); px += maxOf(1f, 3f * u) }
            var py = d.top
            while (py < d.bottom) { drawLine(a.luz(P.TELA, luz(pc.center)), Offset(d.left, py), Offset(d.left, py + 1.6f * u), 0.7f * u); drawLine(a.luz(P.TELA, luz(pc.center)), Offset(d.right, py), Offset(d.right, py + 1.6f * u), 0.7f * u); py += maxOf(1f, 3f * u) }
        }
        velcro(Rect(w * 0.64f, w * 0.06f, w * 0.69f, w * 0.076f), a, luz(Offset(w * 0.66f, w * 0.07f)), 741)
    }

    /**
     * Un paño de acolchado capitoneado: cojines en rombo de 24 dp, cada costura corrida un poco (la tela cede), con la sombra
     * del bulto del lado de arriba a la izquierda y su luz del otro; un botón hundido en cada cruce; el ribete en los bordes.
     */
    private fun DrawScope.acolchado(r: Rect, a: AireEstanteEspacio, semilla: Int) {
        val u = density
        if (r.width < 2f || r.height < 2f) return
        val s = maxOf(1f, 24f * u)
        clipRect(r.left, r.top, r.right, r.bottom) {
            pinceladas(r, mezcla(P.ACOLCHADO, LUZ_MUEBLE, 0.4f), semilla, (r.width * r.height / (900f * u * u)).toInt().coerceIn(4, 60), u, if (a.oscuro) 0.03f else 0.08f)
            val costura = mezcla(P.ACOLCHADO, P.TINTA, 0.45f)
            // «/»: x + y = c, cada una corrida un poco.
            var c = r.left + r.top + s * 0.5f
            var i = 0
            while (c < r.right + r.bottom) {
                val d = (azar(semilla, i) - 0.5f) * 3f * u
                val p0 = Offset(c - r.top + d, r.top); val p1 = Offset(c - r.bottom - d, r.bottom)
                drawLine(a.sombra(0.12f), p0 - Offset(3f * u, 0f), p1 - Offset(3f * u, 0f), 5f * u)
                drawLine(a.claro(0.16f), p0 + Offset(3.2f * u, 0f), p1 + Offset(3.2f * u, 0f), 5f * u)
                drawLine(costura.copy(alpha = 0.35f), p0, p1, 0.8f * u)
                c += s; i++
            }
            // «\»: x − y = c. La luz corre a lo largo: sombra pareja y tenue.
            c = r.left - r.bottom + s * 0.5f
            i = 0
            while (c < r.right - r.top) {
                val d = (azar(semilla + 1, i) - 0.5f) * 3f * u
                val p0 = Offset(c + r.top + d, r.top); val p1 = Offset(c + r.bottom - d, r.bottom)
                drawLine(a.sombra(0.07f), p0, p1, 3.5f * u)
                drawLine(costura.copy(alpha = 0.3f), p0, p1, 0.7f * u)
                c += s; i++
            }
            // Los botones hundidos en los cruces de la retícula (sin el corrimiento: así no quedan en fila perfecta).
            var c1 = r.left + r.top + s * 0.5f
            var j = 0
            while (c1 < r.right + r.bottom) {
                var c2 = r.left - r.bottom + s * 0.5f
                while (c2 < r.right - r.top) {
                    val px = (c1 + c2) / 2; val py = (c1 - c2) / 2
                    if (px > r.left - 2f * u && px < r.right + 2f * u && py > r.top - 2f * u && py < r.bottom + 2f * u) {
                        val q = Offset(px + (azar(semilla + 5, j) - 0.5f) * 2f * u, py)
                        drawCircle(a.sombra(0.32f), 2f * u, q)
                        drawCircle(a.luz(mezcla(P.ACOLCHADO, P.TINTA, 0.25f), LuzEstanteEspacio(0.2f, 0f)), 1.3f * u, q + Offset(0.3f * u, 0.3f * u))
                    }
                    c2 += s; j++
                }
                c1 += s
            }
        }
        // El ribete crudo de los lados, abultado.
        listOf(r.left + 1.4f * u, r.right - 1.4f * u).forEach { x ->
            drawLine(a.luz(mezcla(P.TELA, P.ACOLCHADO, 0.3f), LuzEstanteEspacio(0.15f, 0f)), Offset(x, r.top), Offset(x, r.bottom), 2.6f * u)
            drawLine(a.claro(0.3f), Offset(x - 0.6f * u, r.top), Offset(x - 0.6f * u, r.bottom), 0.6f * u)
        }
    }

    /** Una cuaderna: la pletina pintada de gris azulado, con su canto de luz, sus agujeros de aligerar y sus remaches. */
    private fun DrawScope.cuaderna(r: Rect, a: AireEstanteEspacio, i: Int, l: LuzEstanteEspacio) {
        val u = density
        drawRect(a.sombra(if (a.lumbre) 0.4f else 0.26f), Offset(r.right, r.top), Size(3.5f * u, r.height))
        drawRect(a.luz(P.CASCO_OSC, l), r.topLeft, r.size)
        drawRect(a.claro(0.35f), r.topLeft, Size(1.4f * u, r.height))
        drawRect(a.sombra(0.25f), Offset(r.right - 2f * u, r.top), Size(2f * u, r.height))
        val paso = maxOf(1f, r.width * 3.2f)
        var y = r.top + paso * 0.6f
        var j = 0
        while (y < r.bottom - r.width * 0.5f) {
            if (j % 2 == 0) {
                drawCircle(a.luz(mezcla(P.TELA_AZUL, P.TINTA, 0.55f), l), r.width * 0.26f, Offset(r.center.x, y))
                drawArc(a.claro(0.4f), 20f, 150f, false, Offset(r.center.x - r.width * 0.26f, y - r.width * 0.26f), Size(r.width * 0.52f, r.width * 0.52f), style = Stroke(0.7f * u))
            } else {
                remacheEspacio(Offset(r.center.x, y - r.width * 0.4f), 1.1f * u, a.luz(P.ACERO, l), a)
                remacheEspacio(Offset(r.center.x, y + r.width * 0.4f), 1.1f * u, a.luz(P.ACERO, l), a)
            }
            y += paso; j++
        }
    }

    /** Una tira de velcro: el gris peludo, unos puntitos y su canto. */
    fun DrawScope.velcro(r: Rect, a: AireEstanteEspacio, l: LuzEstanteEspacio, semilla: Int) {
        val u = density
        drawRect(a.sombra(0.3f), r.topLeft + Offset(0.8f * u, 1f * u), r.size)
        drawRect(a.luz(mezcla(P.ACERO, P.GOMA, 0.45f), l), r.topLeft, r.size)
        for (k in 0 until 10) drawCircle(a.luz(mezcla(P.ACERO, P.GOMA, if (k % 2 == 0) 0.2f else 0.7f), l), 0.5f * u, Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k)))
        drawRect(a.claro(0.25f), r.topLeft, Size(r.width, 0.7f * u))
    }

    // ---------------------------------------------------------------- las luces

    /** El cono de la lámpara, desde [o] y de [largo]: unos 40° a cada lado del eje (abajo a la izquierda). */
    private fun cono(o: Offset, largo: Float): Path {
        fun dir(g: Double) = Offset(cos(Math.toRadians(g)).toFloat(), sin(Math.toRadians(g)).toFloat()) * largo
        return Path().apply {
            moveTo(o.x, o.y)
            for (g in listOf(80.0, 105.0, 135.0, 162.0)) { val p = o + dir(g); lineTo(p.x, p.y) }
            close()
        }
    }

    /**
     * La luz en la pared: de día, la mancha fría del ojo de buey arriba a la izquierda y la penumbra abajo a la derecha; de
     * noche, el resplandor de las estrellas junto al vidrio y el halo y el cono de la lámpara a la derecha; en los Sueños, la
     * Vía Láctea en el vidrio y la luz de guardia al pie.
     */
    fun DrawScope.luces(a: AireEstanteEspacio, vent: Offset, lamp: Offset, ch: Float) {
        val w = size.width
        if (!a.lumbre) {
            // La luz fría que sale del ojo de buey: un halo marcado junto al aro, que se abre hacia abajo a la derecha y se pierde.
            mancha(vent + Offset(w * 0.06f, w * 0.08f), w * 0.62f, P.LUZ_FRIA.copy(alpha = 0.5f))
            mancha(vent, w * 0.3f, Color.White.copy(alpha = 0.35f))
            mancha(Offset(w * 1.05f, ch), w * 0.95f, sombraMueble(0.26f))
            return
        }
        val f = if (a.oscuro) 1f else 0.8f
        mancha(vent, w * 0.6f, (if (a.noche && !a.oscuro) P.CREPUSCULO else P.ESTRELLAS).copy(alpha = (if (a.noche) 0.3f else 0.15f) * f))
        mancha(vent, w * 0.24f, P.LUZ_FRIA.copy(alpha = (if (a.noche) 0.16f else 0.1f) * f))
        if (!a.noche) {
            mancha(lamp, w * 0.75f, P.AMBAR.copy(alpha = 0.14f))
            mancha(lamp + Offset(-w * 0.02f, w * 0.03f), w * 0.2f, LUZ_MUEBLE.copy(alpha = 0.3f))
            clipPath(cono(lamp, w * 1.4f)) {
                mancha(lamp, w * 1.0f, P.AMBAR.copy(alpha = 0.3f))
                mancha(lamp + Offset(-w * 0.16f, w * 0.24f), w * 0.5f, LUZ_MUEBLE.copy(alpha = 0.22f))
            }
        } else mancha(Offset(w * 0.5f, ch * 0.2f), w * 0.9f, P.GUARDIA.copy(alpha = 0.08f * f))
    }

    // ---------------------------------------------------------------- el ojo de buey

    /**
     * El ojo de buey a un costado: el aro de chapa crema con su bisel y sus doce pernos, el escalón de aluminio, la junta de
     * goma, el vidrio con lo de afuera y su reflejo, y la tapa de protección abierta hacia arriba sobre su bisagra.
     */
    fun DrawScope.ventana(c: Offset, rv: Float, a: AireEstanteEspacio, caida: Offset) {
        val u = density
        val ro = rv * 1.45f
        val l = LuzEstanteEspacio(if (a.lumbre) 0.25f else 1f, 1f)
        // La tapa abierta, arriba: se le ve la cara de dentro, de canto, con su junta y su asa.
        val tapa = Rect(c.x - ro * 0.95f, c.y - ro - rv * 0.55f, c.x + ro * 0.95f, c.y - ro + rv * 0.14f)
        translate(caida.x, caida.y * 0.6f) { drawOval(a.sombra(if (a.lumbre) 0.4f else 0.22f), tapa.topLeft, tapa.size) }
        drawOval(a.luz(mezcla(P.CASCO, P.CASCO_OSC, 0.15f), l), tapa.topLeft, tapa.size)
        // El grosor de la tapa: su canto, más hondo, arriba; la cara de dentro, un poco más baja.
        val cara = Rect(tapa.left + rv * 0.06f, tapa.top + rv * 0.1f, tapa.right - rv * 0.06f, tapa.bottom)
        drawOval(a.luz(mezcla(P.CASCO, P.CASCO_OSC, 0.4f), l), cara.topLeft, cara.size)
        drawArc(a.sombra(0.3f), 180f, 180f, false, cara.topLeft, cara.size, style = Stroke(1.2f * u))
        drawOval(a.luz(P.GOMA, l), cara.deflate(rv * 0.1f).topLeft, cara.deflate(rv * 0.1f).size, style = Stroke(1.8f * u))
        drawArc(a.claro(0.45f), 190f, 120f, false, tapa.topLeft, tapa.size, style = Stroke(1.2f * u))
        drawArc(a.sombra(0.3f), 10f, 160f, false, tapa.topLeft, tapa.size, style = Stroke(1.6f * u))
        drawRoundRect(a.luz(P.NARANJA, l), Offset(c.x - rv * 0.3f, tapa.top + tapa.height * 0.32f), Size(rv * 0.6f, tapa.height * 0.26f), CornerRadius(2f * u))
        drawRect(a.claro(0.35f), Offset(c.x - rv * 0.26f, tapa.top + tapa.height * 0.34f), Size(rv * 0.5f, 0.8f * u))
        // La sombra del aro en el acolchado y el aro.
        drawCircle(a.sombra(if (a.lumbre) 0.45f else 0.28f), ro, c + caida)
        drawCircle(a.luz(P.CREMA, l), ro, c)
        drawArc(a.claro(0.5f), 160f, 110f, false, Offset(c.x - ro * 0.93f, c.y - ro * 0.93f), Size(ro * 1.86f, ro * 1.86f), style = Stroke(2.2f * u))
        drawArc(a.sombra(0.3f), -20f, 130f, false, Offset(c.x - ro * 0.9f, c.y - ro * 0.9f), Size(ro * 1.8f, ro * 1.8f), style = Stroke(3f * u))
        // La pintura saltada del aro, abajo, donde se apoyan las manos.
        listOf(70f, 98f, 115f).forEachIndexed { i, g ->
            val ang = Math.toRadians(g.toDouble()).toFloat()
            drawOval(a.luz(P.ACERO, l), Offset(c.x + ro * 0.88f * cos(ang) - 2f * u, c.y + ro * 0.88f * sin(ang) - 1f * u), Size((3f + 2f * i) * u, 2f * u))
        }
        // Los doce pernos.
        for (k in 0 until 12) {
            val ang = (k * 30f + 15f) * (Math.PI.toFloat() / 180f)
            val p = Offset(c.x + ro * 0.82f * cos(ang), c.y + ro * 0.82f * sin(ang))
            drawCircle(a.sombra(0.4f), 2.2f * u, p + Offset(0.7f * u, 0.8f * u))
            drawCircle(a.luz(P.ACERO, l), 2f * u, p)
            drawLine(a.luz(mezcla(P.ACERO, P.TINTA, 0.5f), l), p - Offset(1.1f * u, 0f), p + Offset(1.1f * u, 0f), 0.6f * u)
            drawCircle(a.claro(0.5f), 0.7f * u, p - Offset(0.7f * u, 0.7f * u))
        }
        // El escalón de aluminio, la junta de goma.
        drawCircle(a.luz(mezcla(P.ACERO, P.CASCO_OSC, 0.3f), l), rv * 1.17f, c)
        drawArc(a.sombra(0.4f), 180f, 90f, false, Offset(c.x - rv * 1.15f, c.y - rv * 1.15f), Size(rv * 2.3f, rv * 2.3f), style = Stroke(2f * u))
        drawArc(a.claro(0.4f), 0f, 90f, false, Offset(c.x - rv * 1.15f, c.y - rv * 1.15f), Size(rv * 2.3f, rv * 2.3f), style = Stroke(1.2f * u))
        drawCircle(a.luz(P.GOMA, l), rv * 1.06f, c)
        // El vidrio: lo de afuera, el hondo del casco arriba a la izquierda y el reflejo.
        val vidrio = Path().apply { addOval(Rect(c, rv)) }
        clipPath(vidrio) {
            fuera(c, rv, a)
            drawPath(bordeEspacio(vidrio, rv * 0.14f, rv * 0.16f), Color(0xFF000000).copy(alpha = 0.4f))
            if (!a.lumbre) {
                drawPath(poligonoEspacio(c.x - rv, c.y - rv * 0.1f, c.x - rv * 0.1f, c.y - rv, c.x + rv * 0.18f, c.y - rv, c.x - rv, c.y + rv * 0.2f), Color.White.copy(alpha = 0.1f))
                drawPath(poligonoEspacio(c.x - rv * 0.2f, c.y + rv, c.x + rv, c.y - rv * 0.2f, c.x + rv, c.y, c.x, c.y + rv), Color.White.copy(alpha = 0.06f))
            } else if (!a.noche) {
                // El reflejo de la lámpara de trabajo en el vidrio: un arco cálido en el borde que la mira.
                drawArc(P.AMBAR.copy(alpha = 0.35f), -40f, 50f, false, Offset(c.x - rv * 0.86f, c.y - rv * 0.86f), Size(rv * 1.72f, rv * 1.72f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                drawCircle(Color(0xFFFFE6C0).copy(alpha = 0.7f), 1.2f * u, Offset(c.x + rv * 0.8f, c.y - rv * 0.32f))
            }
        }
        drawArc(Color.White.copy(alpha = if (a.oscuro) 0.14f else 0.35f), 200f, 55f, false, Offset(c.x - rv * 0.82f, c.y - rv * 0.82f), Size(rv * 1.64f, rv * 1.64f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawArc(Color.White.copy(alpha = if (a.oscuro) 0.08f else 0.2f), 262f, 14f, false, Offset(c.x - rv * 0.82f, c.y - rv * 0.82f), Size(rv * 1.64f, rv * 1.64f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // Las dos bisagras de la tapa.
        listOf(-0.5f, 0.5f).forEach { s ->
            drawRoundRect(a.sombra(0.35f), Offset(c.x + rv * s - 2.2f * u, c.y - ro - 4f * u), Size(6f * u, 10f * u), CornerRadius(1.5f * u))
            drawRoundRect(a.luz(P.ACERO, l), Offset(c.x + rv * s - 3f * u, c.y - ro - 5f * u), Size(6f * u, 10f * u), CornerRadius(1.5f * u))
            drawLine(a.claro(0.5f), Offset(c.x + rv * s - 2f * u, c.y - ro - 4.4f * u), Offset(c.x + rv * s - 2f * u, c.y - ro + 4f * u), 0.7f * u)
            drawLine(a.sombra(0.4f), Offset(c.x + rv * s - 3f * u, c.y - ro), Offset(c.x + rv * s + 3f * u, c.y - ro), 0.7f * u)
        }
    }

    /**
     * Lo que se ve por el vidrio: el anillo de la nave, que gira alrededor del eje, cruzando en curva por abajo. De día, los
     * focos lo alumbran (la cara crema con sus juntas, el canto en sombra, sus ventanillas oscuras y el destello de un foco),
     * con pocas estrellas. De noche, con los focos apagados, es una silueta con las ventanillas encendidas (las luces lejanas de
     * la gente que vive allá), y arriba la Vía Láctea y la estrella de delante, la más brillante. En los Sueños, la Vía Láctea viva.
     */
    private fun DrawScope.fuera(c: Offset, rv: Float, a: AireEstanteEspacio) {
        val u = density
        drawCircle(P.FUERA, rv, c)
        // La Vía Láctea, en diagonal: trazos anchos y tenues, y su polvo de estrellas.
        if (a.lumbre) {
            val f = if (a.noche) 1f else 0.6f
            val p0 = Offset(c.x - rv * 1.1f, c.y + rv * 0.35f); val p1 = Offset(c.x + rv * 1.1f, c.y - rv * 0.85f)
            listOf(0.9f to 0.05f, 0.6f to 0.07f, 0.34f to 0.09f, 0.16f to 0.1f).forEach { (g, al) ->
                drawLine((if (a.noche) mezcla(P.ESTRELLAS, Color(0xFFC9A8FF), 0.35f) else P.ESTRELLAS).copy(alpha = al * f), p0, p1, rv * g, StrokeCap.Round)
            }
            for (k in 0 until 46) {
                val t = azar(911, k); val n = (azar(912, k) - 0.5f) * (0.5f + 0.5f * azar(913, k))
                val p = p0 + (p1 - p0) * t + Offset(0.48f, 0.88f) * (n * rv * 0.6f)
                drawCircle(P.LUZ_FRIA.copy(alpha = (0.35f + 0.5f * azar(914, k)) * f), (0.35f + 0.5f * azar(915, k)) * u, p)
            }
        }
        val n = if (a.lumbre) 34 else 14
        for (k in 0 until n) {
            val p = Offset(c.x + rv * (azar(921, k) * 2f - 1f), c.y + rv * (azar(922, k) * 2f - 1f))
            drawCircle(P.LUZ_FRIA.copy(alpha = 0.4f + 0.55f * azar(923, k)), (0.4f + 0.8f * azar(924, k) * azar(925, k)) * u, p)
        }
        // El anillo: una banda ancha en curva, centrada lejos abajo a la derecha.
        val o = Offset(c.x + rv * 0.9f, c.y + rv * 3.05f)
        val r1 = rv * 3.0f; val r2 = rv * 2.4f
        val rm = (r1 + r2) / 2
        // En los Sueños en claro, el anillo al anochecer: alumbrado de lila, ya sin los focos.
        val ocaso = a.noche && !a.oscuro
        val cara = if (!a.lumbre) mezcla(P.CASCO, P.LUZ_FRIA, 0.35f) else if (ocaso) mezcla(mezcla(P.CASCO, P.CREPUSCULO, 0.6f), P.FUERA, 0.35f) else mezcla(P.GOMA, P.FUERA, 0.35f)
        val canto = if (!a.lumbre) mezcla(P.CASCO_OSC, P.FUERA, 0.3f) else mezcla(P.GOMA, P.FUERA, 0.65f)
        drawCircle(cara, rm, o, style = Stroke(r1 - r2))
        drawCircle(canto, r2 + (r1 - r2) * 0.14f, o, style = Stroke((r1 - r2) * 0.28f))
        drawCircle(if (!a.lumbre) Color.White.copy(alpha = 0.7f) else P.ESTRELLAS.copy(alpha = 0.35f), r1, o, style = Stroke(1f * u))
        // Las juntas de los módulos del anillo y sus ventanillas.
        for (j in 0 until 9) {
            val g = Math.toRadians(-128.0 + 6.0 * j).toFloat()
            val d = Offset(cos(g), sin(g))
            drawLine((if (!a.lumbre) mezcla(P.CASCO_OSC, P.CASCO, 0.3f) else P.FUERA).copy(alpha = 0.8f), o + d * r2, o + d * r1, 0.8f * u)
        }
        for (j in 0 until 16) {
            val g = Math.toRadians(-127.0 + 2.9 * j).toFloat()
            val p = o + Offset(cos(g), sin(g)) * (rm + (r1 - r2) * 0.12f)
            val encendida = a.lumbre && azar(931, j) < (if (a.noche) 0.35f else 0.7f)
            if (encendida) {
                drawCircle(P.AMBAR.copy(alpha = 0.3f), 2f * u, p)
                drawCircle(mezcla(P.AMBAR, Color.White, 0.4f), 0.9f * u, p)
            } else drawCircle(if (!a.lumbre) mezcla(P.CASCO_OSC, P.TINTA, 0.5f) else P.FUERA, 0.9f * u, p)
        }
        if (!a.lumbre) {
            // El foco que alumbra el anillo, con su destello.
            val g = Math.toRadians(-117.0).toFloat()
            val foco = o + Offset(cos(g), sin(g)) * (r1 + 1.5f * u)
            drawCircle(P.LUZ_FRIA.copy(alpha = 0.35f), rv * 0.2f, foco)
            drawLine(Color.White.copy(alpha = 0.7f), foco - Offset(rv * 0.3f, 0f), foco + Offset(rv * 0.3f, 0f), 0.6f * u)
            drawCircle(Color.White, rv * 0.06f, foco)
        } else {
            // La estrella de delante, con sus cuatro rayos.
            val e = Offset(c.x - rv * 0.32f, c.y - rv * 0.42f)
            drawCircle(P.LUZ_FRIA.copy(alpha = 0.25f), rv * 0.12f, e)
            drawLine(P.LUZ_FRIA.copy(alpha = 0.7f), e - Offset(rv * 0.22f, 0f), e + Offset(rv * 0.22f, 0f), 0.6f * u)
            drawLine(P.LUZ_FRIA.copy(alpha = 0.7f), e - Offset(0f, rv * 0.22f), e + Offset(0f, rv * 0.22f), 0.6f * u)
            drawCircle(Color.White, 1.5f * u, e)
        }
    }

    // ---------------------------------------------------------------- lo de la pared

    /** La lámpara de trabajo, sujeta a la pared con su brazo: el cuerpo de goma con su agarre, la visera y el vidrio; de noche, encendida. */
    fun DrawScope.lampara(boca: Offset, a: AireEstanteEspacio, caida: Offset) {
        val u = density; val w = size.width
        val encendida = a.oscuro && !a.noche
        val l = LuzEstanteEspacio(if (encendida) 0.9f else 0.5f, 0.1f)
        val largo = w * 0.088f; val radio = w * 0.021f
        val giro = 122f
        val atras = Offset(cos(Math.toRadians(giro.toDouble())).toFloat(), sin(Math.toRadians(giro.toDouble())).toFloat()) * -1f
        val medio = boca + atras * (largo * 0.55f)
        // El brazo: de la placa en la pared al cuerpo, con su rótula.
        val placa = Offset(w * 0.935f, w * 0.115f)
        drawLine(a.sombra(0.3f), placa + caida, medio + caida, 3.2f * u, StrokeCap.Round)
        drawLine(a.luz(P.ACERO, l), placa, medio, 2.6f * u, StrokeCap.Round)
        drawLine(a.claro(0.4f), placa - Offset(0.8f * u, 0.8f * u), medio - Offset(0.8f * u, 0.8f * u), 0.6f * u)
        drawRoundRect(a.sombra(0.35f), Offset(placa.x - 5f * u + 1.5f * u, placa.y - 6f * u + 1.5f * u), Size(10f * u, 12f * u), CornerRadius(2f * u))
        drawRoundRect(a.luz(P.CASCO_OSC, l), Offset(placa.x - 5f * u, placa.y - 6f * u), Size(10f * u, 12f * u), CornerRadius(2f * u))
        remacheEspacio(placa - Offset(0f, 3.4f * u), 1f * u, a.luz(P.ACERO, l), a)
        remacheEspacio(placa + Offset(0f, 3.4f * u), 1f * u, a.luz(P.ACERO, l), a)
        // El cable enrollado que sale de atrás y sube a la toma.
        val cola = boca + atras * largo
        val cable = Path().apply { moveTo(cola.x, cola.y); cubicTo(cola.x + w * 0.04f, cola.y - w * 0.02f, w * 0.97f, w * 0.12f, w * 1.01f, w * 0.06f) }
        drawPath(corridoEspacio(cable, caida.x * 0.6f, caida.y * 0.6f), a.sombra(0.25f), style = Stroke(1.8f * u))
        drawPath(cable, a.luz(P.GOMA, l), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        // El cuerpo, dibujado de lado y girado hacia abajo a la izquierda (en su marco, la luz viene de +y).
        rotate(giro, boca) {
            val cuerpo = Rect(boca.x - largo, boca.y - radio, boca.x - largo * 0.22f, boca.y + radio)
            drawRoundRect(a.sombra(0.35f), cuerpo.topLeft + Offset(0f, -2.2f * u), cuerpo.size, CornerRadius(radio * 0.5f))
            drawRoundRect(a.luz(P.GOMA, l), cuerpo.topLeft, cuerpo.size, CornerRadius(radio * 0.5f))
            for (j in 0 until 5) {
                val x = cuerpo.left + cuerpo.width * (0.18f + 0.13f * j)
                drawLine(a.luz(mezcla(P.GOMA, P.ACERO, 0.35f), l), Offset(x, cuerpo.top + 1.5f * u), Offset(x, cuerpo.bottom - 1.5f * u), 1f * u)
            }
            drawRect(a.claro(0.25f), Offset(cuerpo.left + 2f * u, cuerpo.bottom - radio * 0.5f), Size(cuerpo.width - 4f * u, 1f * u))
            drawRect(a.luz(P.NARANJA, l), Offset(cuerpo.left + cuerpo.width * 0.04f, cuerpo.top), Size(cuerpo.width * 0.08f, cuerpo.height))
            // La visera de aluminio, que se abre hacia la boca.
            val visera = poligonoEspacio(boca.x - largo * 0.26f, boca.y - radio * 1.02f, boca.x, boca.y - radio * 1.38f, boca.x, boca.y + radio * 1.38f, boca.x - largo * 0.26f, boca.y + radio * 1.02f)
            drawPath(visera, a.luz(P.ACERO, l))
            drawPath(poligonoEspacio(boca.x - largo * 0.26f, boca.y + radio * 0.4f, boca.x, boca.y + radio * 0.6f, boca.x, boca.y + radio * 1.38f, boca.x - largo * 0.26f, boca.y + radio * 1.02f), a.claro(0.35f))
            drawPath(poligonoEspacio(boca.x - largo * 0.26f, boca.y - radio * 1.02f, boca.x, boca.y - radio * 1.38f, boca.x, boca.y - radio * 0.5f, boca.x - largo * 0.26f, boca.y - radio * 0.4f), a.sombra(0.3f))
            // El vidrio.
            drawOval(if (encendida) Color(0xFFFFEBC8) else a.luz(mezcla(P.LUZ_FRIA, P.ACERO, 0.4f), l), Offset(boca.x - radio * 0.3f, boca.y - radio * 1.3f), Size(radio * 0.6f, radio * 2.6f))
            if (a.lumbre && !encendida) drawOval(a.sombra(0.3f), Offset(boca.x - radio * 0.3f, boca.y - radio * 1.3f), Size(radio * 0.6f, radio * 2.6f))
        }
    }

    /** Lo que alumbra por sí mismo de noche: la boca de la lámpara de trabajo; en los Sueños, la tira de guardia. */
    fun DrawScope.brillos(lamp: Offset, a: AireEstanteEspacio) {
        val w = size.width
        if (!a.noche) {
            drawCircle(P.AMBAR.copy(alpha = 0.3f), w * 0.03f, lamp)
            drawCircle(Color(0xFFFFF0D6), w * 0.011f, lamp)
        }
    }
}
