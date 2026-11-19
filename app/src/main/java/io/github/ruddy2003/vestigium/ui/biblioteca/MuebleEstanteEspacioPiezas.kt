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
     * El casco por dentro: el acolchado azul en paños entre las cuadernas, cosido en rombos (cada costura con su sombra del
     * lado de arriba a la izquierda y su luz del otro, para que se vea el bulto), con su ribete crudo; arriba, la viga del
     * techo. Va en la corona y al pie, que es lo que el módulo deja ver.
     */
    fun DrawScope.pared(a: AireEstanteEspacio, ch: Float, b: Rect, lado: Float, luz: (Offset) -> LuzEstanteEspacio) {
        val u = density; val w = size.width; val h = size.height
        val lejos = luz(Offset(w * 0.5f, ch * 0.5f))
        val fondo = a.luz(P.TELA_AZUL, LuzEstanteEspacio(0.12f, lejos.f * 0.4f))
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
        drawRect(a.luz(mezcla(P.CASCO_OSC, P.CASCO, 0.35f), luz(Offset(w * 0.2f, 0f))), viga.topLeft, viga.size)
        drawRect(a.claro(0.3f), Offset(0f, viga.bottom - viga.height * 0.4f), Size(w, 1f * u))
        drawRect(a.sombra(0.4f), Offset(0f, viga.bottom), Size(w, 3f * u))
        drawRect(a.sombra(0.15f), Offset(0f, viga.bottom), Size(w, 8f * u))
        var x = w * 0.04f
        while (x < w) { remacheEspacio(Offset(x, viga.center.y), 1.2f * u, a.luz(P.ACERO, luz(Offset(x, 0f))), a); x += maxOf(1f, w * 0.06f) }
        // Las manos gastan el acolchado junto al ojo de buey y bajo el pasamanos; dos parches de velcro sin nada.
        pinceladas(Rect(w * 0.02f, w * 0.36f, w * 0.32f, w * 0.5f), a.sombra(1f), 731, 10, u, 0.08f)
        pinceladas(Rect(w * 0.76f, w * 0.36f, w * 0.98f, w * 0.48f), a.sombra(1f), 732, 10, u, 0.08f)
        listOf(Rect(w * 0.06f, w * 0.43f, w * 0.12f, w * 0.448f), Rect(w * 0.64f, w * 0.15f, w * 0.69f, w * 0.166f)).forEachIndexed { i, r -> velcro(r, a, luz(r.center), 740 + i) }
    }

    /** Un paño de acolchado cosido en rombos de 15 dp, con su ribete en los bordes. */
    private fun DrawScope.acolchado(r: Rect, a: AireEstanteEspacio, semilla: Int) {
        val u = density
        if (r.width < 2f || r.height < 2f) return
        val s = maxOf(1f, 18f * u)
        clipRect(r.left, r.top, r.right, r.bottom) {
            pinceladas(r, mezcla(P.TELA_AZUL, LUZ_MUEBLE, 0.3f).copy(alpha = 1f), semilla, (r.width * r.height / (900f * u * u)).toInt().coerceIn(4, 60), u, if (a.oscuro) 0.03f else 0.07f)
            val costura = mezcla(P.TELA_AZUL, P.TINTA, 0.55f)
            // «/»: x + y = c. Del lado de arriba a la izquierda el bulto baja hacia la costura (sombra ancha y suave); del
            // otro lado sube hacia la luz (claro ancho). Así cada rombo se ve inflado, no como una reja.
            var c = r.left + r.top + s * 0.5f
            while (c < r.right + r.bottom) {
                val p0 = Offset(c - r.top, r.top); val p1 = Offset(c - r.bottom, r.bottom)
                drawLine(a.sombra(0.12f), p0 - Offset(2.6f * u, 0f), p1 - Offset(2.6f * u, 0f), 4f * u)
                drawLine(a.claro(0.13f), p0 + Offset(2.8f * u, 0f), p1 + Offset(2.8f * u, 0f), 4f * u)
                drawLine(costura.copy(alpha = 0.5f), p0, p1, 0.8f * u)
                c += s
            }
            // «\»: x − y = c. La luz corre a lo largo: sombra pareja y tenue a los dos lados.
            c = r.left - r.bottom + s * 0.5f
            while (c < r.right - r.top) {
                val p0 = Offset(c + r.top, r.top); val p1 = Offset(c + r.bottom, r.bottom)
                drawLine(a.sombra(0.08f), p0, p1, 3f * u)
                drawLine(costura.copy(alpha = 0.42f), p0, p1, 0.7f * u)
                c += s
            }
        }
        // El ribete crudo de los lados, abultado.
        listOf(r.left + 1.4f * u, r.right - 1.4f * u).forEach { x ->
            drawLine(a.luz(mezcla(P.TELA, P.TELA_AZUL, 0.35f), LuzEstanteEspacio(0.15f, 0f)), Offset(x, r.top), Offset(x, r.bottom), 2.6f * u)
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
            mancha(vent, w * 0.85f, P.LUZ_FRIA.copy(alpha = 0.34f))
            mancha(vent + Offset(w * 0.1f, w * 0.12f), w * 0.4f, luzMueble(0.28f))
            mancha(Offset(w * 1.05f, ch), w * 0.9f, sombraMueble(0.2f))
            return
        }
        val f = if (a.oscuro) 1f else 0.8f
        mancha(vent, w * 0.6f, P.ESTRELLAS.copy(alpha = (if (a.noche) 0.24f else 0.15f) * f))
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
        drawCircle(a.luz(P.CASCO, l), ro, c)
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
        val cara = if (!a.lumbre) mezcla(P.CASCO, P.LUZ_FRIA, 0.35f) else mezcla(P.GOMA, P.FUERA, 0.35f)
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

    /** Una tira de cinta gris pegada al acolchado, con los cuatro tornillos del ventilador pegados en ella para que no se vayan flotando. */
    fun DrawScope.cintaTornillos(o: Offset, largo: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val alto = largo * 0.17f
        rotate(-5f, o + Offset(largo / 2, alto / 2)) {
            val r = Rect(o.x, o.y, o.x + largo, o.y + alto)
            val n = 7
            val tira = Path().apply {
                moveTo(r.left, r.top)
                for (j in 0..n) lineTo(r.left + r.width * j / n, r.top + (if (j % 2 == 0) 0f else 1.2f * u))
                lineTo(r.right + 1.4f * u, r.top + r.height * 0.3f); lineTo(r.right, r.top + r.height * 0.6f); lineTo(r.right + 1.2f * u, r.bottom)
                for (j in n downTo 0) lineTo(r.left + r.width * j / n, r.bottom - (if (j % 2 == 0) 0f else 1f * u))
                lineTo(r.left - 1.2f * u, r.top + r.height * 0.7f); lineTo(r.left + 0.6f * u, r.top + r.height * 0.4f); close()
            }
            translate(caida.x * 0.4f, caida.y * 0.4f) { drawPath(tira, a.sombra(0.25f)) }
            drawPath(tira, a.luz(mezcla(P.ACERO, P.TELA, 0.35f), l))
            drawRect(a.claro(0.35f), Offset(r.left, r.top + 0.6f * u), Size(r.width, 0.8f * u))
            for (j in 0 until 6) drawLine(a.sombra(0.12f), Offset(r.left + r.width * (0.1f + 0.15f * j), r.top), Offset(r.left + r.width * (0.12f + 0.15f * j), r.bottom), 0.5f * u)
            for (j in 0 until 4) {
                val p = Offset(r.left + r.width * (0.16f + 0.22f * j), r.center.y)
                val gt = 12f + 20f * azar(77, j)
                rotate(gt, p) {
                    drawLine(a.sombra(0.35f), p + Offset(1f * u, 1.2f * u), p + Offset(alto * 0.95f + 1f * u, 1.2f * u), 1.3f * u)
                    drawLine(a.luz(P.ACERO, l), p, p + Offset(alto * 0.95f, 0f), 1.3f * u)
                    for (m in 1 until 4) drawLine(a.sombra(0.35f), p + Offset(alto * 0.22f * m, -0.7f * u), p + Offset(alto * 0.22f * m + 0.6f * u, 0.7f * u), 0.4f * u)
                    drawCircle(a.luz(mezcla(P.ACERO, P.PAPEL, 0.3f), l), alto * 0.24f, p)
                    drawLine(a.luz(P.TINTA, l), p - Offset(0f, alto * 0.14f), p + Offset(0f, alto * 0.14f), 0.6f * u)
                }
            }
        }
    }

    /** El tubo transparente con una espiga de trigo enano de la granja, sujeto al acolchado con velcro y una goma. */
    fun DrawScope.trigo(pie: Offset, alto: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val an = alto * 0.2f
        rotate(-7f, pie) {
            val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
            velcro(Rect(r.left - 2f * u, r.top + alto * 0.4f, r.right + 2f * u, r.top + alto * 0.55f), a, l, 751)
            translate(caida.x * 0.5f, caida.y * 0.5f) { drawRoundRect(a.sombra(0.22f), r.topLeft, r.size, CornerRadius(an / 2)) }
            // Lo de dentro: el tallo, la hoja y la espiga con sus granos y sus barbas.
            val tallo = a.luz(mezcla(P.TRIGO, P.VERDE, 0.45f), l)
            drawLine(tallo, Offset(pie.x, r.bottom - an * 0.4f), Offset(pie.x - an * 0.05f, r.top + alto * 0.42f), 1.2f * u)
            drawPath(Path().apply { moveTo(pie.x, r.top + alto * 0.7f); quadraticTo(pie.x + an * 0.6f, r.top + alto * 0.5f, pie.x + an * 0.2f, r.top + alto * 0.36f); quadraticTo(pie.x + an * 0.2f, r.top + alto * 0.55f, pie.x, r.top + alto * 0.72f); close() }, a.luz(P.VERDE, l))
            val grano = a.luz(P.TRIGO, l)
            for (j in 0 until 7) {
                val y = r.top + alto * (0.16f + 0.035f * j)
                val s = if (j % 2 == 0) -1f else 1f
                drawOval(grano, Offset(pie.x - an * 0.05f + s * an * 0.1f - an * 0.12f, y), Size(an * 0.24f, alto * 0.05f))
                drawLine(a.luz(mezcla(P.TRIGO, P.PAPEL, 0.3f), l), Offset(pie.x + s * an * 0.12f, y), Offset(pie.x + s * an * 0.32f, y - alto * 0.07f), 0.5f * u)
            }
            drawOval(a.claro(0.35f), Offset(pie.x - an * 0.2f, r.top + alto * 0.17f), Size(an * 0.12f, alto * 0.1f))
            // El vidrio: tenue, con su luz a la izquierda y su canto.
            drawRoundRect(a.luz(mezcla(P.LUZ_FRIA, P.TELA_AZUL, 0.3f), l).copy(alpha = 0.22f), r.topLeft, r.size, CornerRadius(an / 2))
            drawRoundRect(a.luz(mezcla(P.LUZ_FRIA, P.ACERO, 0.4f), l).copy(alpha = 0.6f), r.topLeft, r.size, CornerRadius(an / 2), style = Stroke(0.8f * u))
            drawLine(Color.White.copy(alpha = if (a.oscuro) 0.2f else 0.55f), Offset(r.left + an * 0.22f, r.top + an * 0.6f), Offset(r.left + an * 0.22f, r.bottom - an * 0.6f), 1.2f * u)
            // El tapón de goma naranja y la goma que lo abraza.
            drawRoundRect(a.luz(P.NARANJA, l), Offset(r.left - 0.8f * u, r.top - alto * 0.04f), Size(an + 1.6f * u, alto * 0.09f), CornerRadius(1.5f * u))
            drawRect(a.claro(0.35f), Offset(r.left, r.top - alto * 0.03f), Size(an * 0.4f, 0.8f * u))
            drawRect(a.luz(P.GOMA, l), Offset(r.left - 1f * u, r.top + alto * 0.62f), Size(an + 2f * u, 2.2f * u))
        }
    }

    /** El pasamanos naranja con sus dos patas atornilladas al acolchado, gastado en el medio. */
    fun DrawScope.pasamanos(r: Rect, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val radio = CornerRadius(r.height / 2)
        drawRoundRect(a.sombra(if (a.lumbre) 0.4f else 0.26f), r.topLeft + caida, r.size, radio)
        listOf(r.left + r.width * 0.08f, r.right - r.width * 0.12f).forEach { x ->
            drawRect(a.sombra(0.3f), Offset(x + 1.5f * u, r.top + 1f * u), Size(r.width * 0.05f, r.height * 1.5f))
            drawRect(a.luz(P.ACERO, l), Offset(x, r.top - r.height * 0.25f), Size(r.width * 0.05f, r.height * 1.5f))
        }
        drawRoundRect(a.luz(P.NARANJA, l), r.topLeft, r.size, radio)
        drawRoundRect(a.luz(mezcla(P.NARANJA, P.TELA, 0.45f), l), Offset(r.left + r.width * 0.35f, r.top), Size(r.width * 0.3f, r.height), CornerRadius(1f * u))
        drawRect(a.claro(0.45f), Offset(r.left + r.height * 0.5f, r.top + r.height * 0.2f), Size(r.width - r.height, 0.9f * u))
        drawRect(a.sombra(0.25f), Offset(r.left + r.height * 0.5f, r.bottom - 1.6f * u), Size(r.width - r.height, 1.2f * u))
        if (a.lumbre) drawRect(a.filo(0.5f * l.k), Offset(r.left + r.height * 0.5f, r.top), Size(r.width - r.height, 0.8f * u))
    }

    /** Los auriculares enganchados del pasamanos por la diadema, con las copas al aire y el cable que baja flotando hasta la tabla alta. */
    fun DrawScope.auriculares(gancho: Offset, an: Float, apoyo: Float, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density
        val izq = Offset(gancho.x - an * 0.55f, gancho.y + an * 0.55f)
        val der = Offset(gancho.x + an * 0.22f, gancho.y + an * 0.72f)
        // El cable, detrás: de la copa izquierda baja en una curva floja hasta la tabla alta.
        val cable = Path().apply { moveTo(izq.x, izq.y + an * 0.2f); cubicTo(izq.x - an * 0.1f, izq.y + an * 0.6f, izq.x + an * 0.5f, apoyo - an * 0.5f, izq.x + an * 0.25f, apoyo) }
        drawPath(corridoEspacio(cable, caida.x, caida.y), a.sombra(0.22f), style = Stroke(1.4f * u))
        drawPath(cable, a.luz(P.GOMA, l), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        // La diadema: un arco que pasa por encima del pasamanos.
        val diadema = Path().apply { moveTo(izq.x, izq.y); cubicTo(izq.x - an * 0.05f, gancho.y - an * 0.35f, der.x + an * 0.25f, gancho.y - an * 0.4f, der.x, der.y) }
        drawPath(corridoEspacio(diadema, caida.x, caida.y), a.sombra(0.3f), style = Stroke(3.2f * u))
        drawPath(diadema, a.luz(mezcla(P.GOMA, P.ACERO, 0.3f), l), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawPath(corridoEspacio(diadema, -0.8f * u, -0.8f * u), a.claro(0.3f), style = Stroke(0.7f * u))
        // Las copas, cada una con su almohadilla de tela azul.
        listOf(izq to -20f, der to 28f).forEach { (c, g) ->
            rotate(g, c) {
                val cr = Rect(c.x - an * 0.17f, c.y - an * 0.06f, c.x + an * 0.17f, c.y + an * 0.32f)
                drawRoundRect(a.sombra(0.3f), cr.topLeft + caida, cr.size, CornerRadius(an * 0.12f))
                drawRoundRect(a.luz(P.GOMA, l), cr.topLeft, cr.size, CornerRadius(an * 0.12f))
                drawRoundRect(a.luz(mezcla(P.TELA_AZUL, P.TELA, 0.15f), l), Offset(cr.left + an * 0.04f, cr.top + an * 0.05f), Size(cr.width - an * 0.08f, cr.height - an * 0.1f), CornerRadius(an * 0.1f))
                drawOval(a.claro(0.25f), Offset(cr.left + an * 0.07f, cr.top + an * 0.08f), Size(an * 0.1f, an * 0.14f))
                drawRect(a.luz(P.NARANJA, l), Offset(cr.left + cr.width * 0.3f, cr.top - an * 0.03f), Size(cr.width * 0.4f, an * 0.05f))
            }
        }
    }

    // ---------------------------------------------------------------- lo de la tabla alta

    /**
     * El ventilador de recambio a medio arreglar, sujeto a la tabla alta con su correa: la caja de esmalte gris con su cara de
     * arriba y su costado, la boca con las aspas, los cuatro agujeros sin tornillos (están en la cinta, en la pared), la franja
     * de aviso, el panel con su conector y el mazo de cables al aire; flotando arriba, la rejilla atada con un cordón.
     */
    fun DrawScope.ventilador(pie: Offset, an: Float, a: AireEstanteEspacio, luz: (Offset) -> LuzEstanteEspacio, caida: Offset) {
        val u = density
        val alto = an * 0.56f
        val prof = an * 0.07f
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        val l = luz(r.center)
        val lt = luz(Offset(r.center.x, r.top - an * 0.3f))
        val esmalte = mezcla(P.ACERO, P.PAPEL, 0.32f)
        val arriba = poligonoEspacio(r.left, r.top, r.left + prof, r.top - prof * 0.7f, r.right + prof, r.top - prof * 0.7f, r.right, r.top)
        val lado = poligonoEspacio(r.right, r.top, r.right + prof, r.top - prof * 0.7f, r.right + prof, r.bottom - prof * 0.7f, r.right, r.bottom)
        // La rejilla, flotando arriba, atada con su cordón (detrás de la caja).
        val rc = Offset(r.left + an * 0.3f, r.top - an * 0.5f)
        val lado2 = an * 0.42f
        val cordon = Path().apply { moveTo(r.left + an * 0.08f, r.top); cubicTo(r.left - an * 0.08f, r.top - an * 0.2f, rc.x - lado2 * 0.7f, rc.y + lado2 * 0.3f, rc.x - lado2 * 0.42f, rc.y + lado2 * 0.38f) }
        drawPath(cordon, a.luz(P.NARANJA, lt), style = Stroke(1f * u, cap = StrokeCap.Round))
        rotate(-16f, rc) {
            val rr = Rect(rc.x - lado2 / 2, rc.y - lado2 / 2, rc.x + lado2 / 2, rc.y + lado2 / 2)
            drawRoundRect(a.sombra(if (a.lumbre) 0.4f else 0.24f), rr.topLeft + caida * 1.6f, rr.size, CornerRadius(2.4f * u))
            drawRoundRect(a.luz(esmalte, lt), rr.topLeft, rr.size, CornerRadius(2.4f * u))
            val g = rr.deflate(lado2 * 0.08f)
            drawCircle(a.luz(mezcla(P.GOMA, P.TELA_AZUL, 0.3f), lt), g.width * 0.47f, g.center)
            for (j in 1..4) drawCircle(a.luz(esmalte, lt), g.width * 0.47f * j / 4.2f, g.center, style = Stroke(1.1f * u))
            drawLine(a.luz(esmalte, lt), Offset(g.left, g.center.y), Offset(g.right, g.center.y), 1.1f * u)
            drawLine(a.luz(esmalte, lt), Offset(g.center.x, g.top), Offset(g.center.x, g.bottom), 1.1f * u)
            listOf(rr.topLeft, Offset(rr.right, rr.top), Offset(rr.left, rr.bottom), rr.bottomRight).forEach { q ->
                val qq = q + Offset(if (q.x < rc.x) lado2 * 0.09f else -lado2 * 0.09f, if (q.y < rc.y) lado2 * 0.09f else -lado2 * 0.09f)
                drawCircle(a.luz(P.GOMA, lt), 1.2f * u, qq)
            }
            drawRect(a.claro(0.4f), rr.topLeft + Offset(2f * u, 0.6f * u), Size(rr.width - 4f * u, 0.8f * u))
            drawRect(a.sombra(0.2f), Offset(rr.right - lado2 * 0.1f, rr.top), Size(lado2 * 0.1f, rr.height))
        }
        // La sombra de la caja en la pared y en la tabla.
        translate(caida.x, caida.y) { drawRect(a.sombra(if (a.lumbre) 0.42f else 0.22f), Offset(r.left, r.top - prof * 0.7f), Size(an + prof, alto)) }
        contactoEspacio(Offset(pie.x + an * 0.06f, pie.y), an * 1.12f, an * 0.07f, a, 0.4f)
        // La caja.
        drawPath(arriba, a.luz(mezcla(esmalte, LUZ_MUEBLE, 0.4f), l))
        drawPath(lado, a.luz(mezcla(esmalte, P.CASCO_OSC, 0.4f), l))
        drawRect(a.luz(esmalte, l), r.topLeft, r.size)
        drawRect(a.claro(0.3f), Offset(r.left, r.top), Size(an * 0.04f, alto))
        drawRect(a.sombra(0.1f), Offset(r.left + an * 0.7f, r.top), Size(an * 0.3f, alto))
        // La boca con las aspas.
        val bo = Rect(r.left + an * 0.06f, r.top + alto * 0.1f, r.left + an * 0.06f + alto * 0.8f, r.top + alto * 0.9f)
        drawRoundRect(a.luz(mezcla(P.GOMA, P.TELA_AZUL, 0.25f), LuzEstanteEspacio(l.k * 0.6f, l.f * 0.5f)), bo.topLeft, bo.size, CornerRadius(3f * u))
        drawArc(a.sombra(0.5f), 180f, 90f, false, bo.topLeft, bo.size, style = Stroke(3f * u))
        val cc = bo.center; val ra = bo.width * 0.4f
        drawCircle(a.luz(mezcla(P.GOMA, P.ACERO, 0.25f), l), ra * 1.05f, cc, style = Stroke(1.6f * u))
        for (j in 0 until 5) {
            val ang = Math.toRadians(72.0 * j + 18).toFloat()
            val p0 = cc + Offset(cos(ang), sin(ang)) * (ra * 0.25f)
            val p1 = cc + Offset(cos(ang + 0.5f), sin(ang + 0.5f)) * ra
            val p2 = cc + Offset(cos(ang + 0.95f), sin(ang + 0.95f)) * (ra * 0.9f)
            val p3 = cc + Offset(cos(ang + 0.55f), sin(ang + 0.55f)) * (ra * 0.25f)
            val aspa = poligonoEspacio(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
            drawPath(aspa, a.luz(P.ACERO, l))
            drawPath(bordeEspacio(aspa, ra * 0.08f, ra * 0.06f), a.claro(0.35f))
        }
        drawCircle(a.luz(P.GOMA, l), ra * 0.28f, cc)
        drawCircle(a.claro(0.35f), ra * 0.1f, cc - Offset(ra * 0.08f, ra * 0.08f))
        // Los cuatro agujeros vacíos de los tornillos.
        listOf(bo.topLeft, Offset(bo.right, bo.top), Offset(bo.left, bo.bottom), bo.bottomRight).forEach { q ->
            drawCircle(a.luz(P.TINTA, l), 1.4f * u, q + Offset(if (q.x < cc.x) -2.6f * u else 2.6f * u, if (q.y < cc.y) -1f * u else 1f * u))
        }
        // El panel de la derecha con su conector, la franja de aviso abajo y la correa que lo sujeta.
        val pn = Rect(bo.right + an * 0.06f, r.top + alto * 0.14f, r.right - an * 0.05f, r.top + alto * 0.5f)
        drawRoundRect(a.luz(mezcla(esmalte, P.TINTA, 0.2f), l), pn.topLeft, pn.size, CornerRadius(1.5f * u))
        drawCircle(a.luz(P.GOMA, l), pn.height * 0.28f, Offset(pn.center.x, pn.center.y))
        for (j in 0 until 4) drawCircle(a.luz(P.ACERO, l), 0.6f * u, Offset(pn.center.x + pn.height * 0.13f * cos(j * 1.57f), pn.center.y + pn.height * 0.13f * sin(j * 1.57f)))
        clipRect(r.left, r.bottom - alto * 0.12f, r.right, r.bottom) {
            drawRect(a.luz(P.GOMA, l), Offset(r.left, r.bottom - alto * 0.12f), Size(an, alto * 0.12f))
            var x = r.left - alto * 0.1f
            while (x < r.right) {
                drawPath(poligonoEspacio(x, r.bottom, x + alto * 0.06f, r.bottom, x + alto * 0.18f, r.bottom - alto * 0.12f, x + alto * 0.12f, r.bottom - alto * 0.12f), a.luz(mezcla(P.NARANJA, P.TRIGO, 0.3f), l))
                x += maxOf(1f, alto * 0.13f)
            }
        }
        val cx = r.right - an * 0.2f
        drawRect(a.sombra(0.3f), Offset(cx + 1.6f * u, r.top - prof * 0.7f), Size(an * 0.08f, alto + prof * 0.7f))
        drawRect(a.luz(P.NARANJA, l), Offset(cx, r.top - prof * 0.7f), Size(an * 0.08f, alto + prof * 0.7f))
        drawRect(a.claro(0.3f), Offset(cx, r.top - prof * 0.7f), Size(1f * u, alto + prof * 0.7f))
        drawRoundRect(a.luz(P.ACERO, l), Offset(cx - 1f * u, r.top + alto * 0.55f), Size(an * 0.08f + 2f * u, alto * 0.14f), CornerRadius(1f * u))
        // El mazo de cables, saliendo del costado, al aire, con su conector suelto.
        val m0 = Offset(r.right + prof * 0.6f, r.top + alto * 0.3f)
        val mazo = Path().apply { moveTo(m0.x, m0.y); cubicTo(m0.x + an * 0.18f, m0.y - an * 0.05f, m0.x + an * 0.12f, m0.y - an * 0.35f, m0.x + an * 0.26f, m0.y - an * 0.42f) }
        drawPath(corridoEspacio(mazo, caida.x, caida.y), a.sombra(0.2f), style = Stroke(2.6f * u))
        listOf(P.ROJO to -0.9f, P.GOMA to 0f, P.TELA to 0.9f).forEach { (col, d) ->
            drawPath(corridoEspacio(mazo, d * u, d * 0.5f * u), a.luz(col, lt), style = Stroke(1f * u, cap = StrokeCap.Round))
        }
        val fin = Offset(m0.x + an * 0.26f, m0.y - an * 0.42f)
        drawRoundRect(a.luz(mezcla(P.PAPEL, P.ACERO, 0.4f), lt), Offset(fin.x - 2.5f * u, fin.y - 4f * u), Size(5f * u, 6f * u), CornerRadius(1f * u))
        if (a.lumbre) drawRect(a.filo(0.5f * l.k), Offset(r.right - 1f * u, r.top), Size(1f * u, alto))
    }

    /** El rotulador al aire, atado a la tabla alta con su cordón en espiral. */
    fun DrawScope.rotulador(base: Offset, c: Offset, a: AireEstanteEspacio, l: LuzEstanteEspacio, caida: Offset) {
        val u = density; val w = size.width
        val largo = w * 0.09f
        val giro = -38f
        val dir = Offset(cos(Math.toRadians(giro.toDouble())).toFloat(), sin(Math.toRadians(giro.toDouble())).toFloat())
        val cola = c - dir * (largo / 2)
        // El cordón en espiral, de la tabla al rotulador: una cadena de lazos chicos a lo largo de una curva.
        val n = 16
        val q0 = base; val q3 = cola
        val q1 = Offset(base.x + w * 0.05f, base.y - w * 0.05f); val q2 = Offset(cola.x + w * 0.03f, cola.y + w * 0.07f)
        drawCircle(a.luz(P.ACERO, l), 2f * u, base)
        for (j in 0..n) {
            val t = j / n.toFloat(); val s = 1f - t
            val p = q0 * (s * s * s) + q1 * (3 * s * s * t) + q2 * (3 * s * t * t) + q3 * (t * t * t)
            drawOval(a.luz(mezcla(P.NARANJA, P.GOMA, 0.2f), l), Offset(p.x - 2.6f * u, p.y - 1.4f * u), Size(5.2f * u, 2.8f * u), style = Stroke(0.8f * u))
        }
        rotate(giro, c) {
            val cuerpo = Rect(c.x - largo / 2, c.y - largo * 0.075f, c.x + largo * 0.25f, c.y + largo * 0.075f)
            drawRoundRect(a.sombra(0.3f), cuerpo.topLeft + caida, Size(largo, cuerpo.height), CornerRadius(cuerpo.height / 2))
            drawRoundRect(a.luz(P.PAPEL, l), cuerpo.topLeft, cuerpo.size, CornerRadius(cuerpo.height / 2))
            drawRect(a.luz(P.TELA_AZUL, l), Offset(cuerpo.left + largo * 0.15f, cuerpo.top), Size(largo * 0.3f, cuerpo.height))
            val tapa = Rect(cuerpo.right - 1f * u, cuerpo.top - 0.4f * u, c.x + largo / 2, cuerpo.bottom + 0.4f * u)
            drawRoundRect(a.luz(P.ROJO, l), tapa.topLeft, tapa.size, CornerRadius(tapa.height / 2))
            drawRect(a.luz(P.ROJO, l), Offset(tapa.left + 2f * u, cuerpo.top - 1.6f * u), Size(tapa.width * 0.6f, 1.4f * u))
            drawRect(a.claro(0.4f), Offset(cuerpo.left + 2f * u, cuerpo.top + 0.6f * u), Size(largo * 0.7f, 0.8f * u))
            drawRect(a.sombra(0.2f), Offset(cuerpo.left + 2f * u, cuerpo.bottom - 1.2f * u), Size(largo * 0.95f, 1f * u))
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
