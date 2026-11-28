package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo de arriba del estante de Arcontes ([MuebleEstanteArcontes]): en la pared, dos clavijas de olivo con el manojo de hisopo
 * puesto a secar boca abajo y la bolsa de cuero del escriba colgada de su correa; encima del armario, un bodegón solapado. Manda
 * la capsa: la caja redonda de madera forrada de cuero, con sus aros de bronce y su correa, llena de rollos de pie con sus
 * etiquetas teñidas, y su tapa apoyada de canto al lado. Contra ella, las tablillas de cera abiertas con el estilo; delante, el
 * tintero de bronce con los cálamos y el cuenco de sal; a la derecha, la lámpara sobre un montón de tablillas atadas (arde de
 * noche) y la alcuza del Ideario. Cada cosa deja su sombra en la pared y en la cornisa.
 */
internal object CoronaEstanteArcontes {
    private val P = PaletaArcontes

    /** Dónde pisan las cosas: un poco detrás del canto de la cornisa. */
    private fun piso(w: Float, apoyo: Float) = apoyo - w * 0.006f

    // ---------------------------------------------------------------- lo colgado

    /** Las clavijas de la pared y lo que cuelga de ellas: el hisopo (en la mancha del sol) y la bolsa del escriba. */
    fun DrawScope.colgado(s: SitioArcontes, apoyo: Float) {
        val w = size.width
        val c1 = Offset(w * 0.11f, w * 0.105f)
        val c2 = Offset(w * 0.905f, w * 0.13f)
        // Las sombras en la roca, una sola vez.
        sombraHisopo(c1, w * 0.16f, s)
        sombraBolsa(c2, s)
        conManchaArcontes(s) {
            clavija(c1, s)
            hisopo(c1, w * 0.16f, s)
            clavija(c2, s)
            bolsa(c2, s)
        }
    }

    /** Una clavija de olivo metida en la roca: el hoyo, la cabeza con su luz y su sombra en la piedra. */
    private fun DrawScope.clavija(c: Offset, s: SitioArcontes) {
        val u = density
        val t = s.t(c)
        drawCircle(t.s(0.45f), 3.6f * u, c + Offset(1.6f * u, 2f * u))
        drawCircle(t.c(P.OLIVO_HONDO), 3.2f * u, c)
        drawCircle(t.c(P.OLIVO_OSC), 2.6f * u, c - Offset(0.4f * u, 0.4f * u))
        drawCircle(t.b(0.5f), 1f * u, c - Offset(1.1f * u, 1.1f * u))
    }

    /** La sombra del hisopo en la roca, corrida a la derecha y abajo. */
    private fun DrawScope.sombraHisopo(c: Offset, largo: Float, s: SitioArcontes) {
        val u = density
        val t = s.t(c + Offset(0f, largo * 0.5f))
        val p = Path().apply {
            moveTo(c.x + 3f * u, c.y + 6f * u)
            lineTo(c.x + largo * 0.32f + 5f * u, c.y + largo * 1.02f)
            lineTo(c.x - largo * 0.18f + 5f * u, c.y + largo * 1.04f)
            close()
        }
        drawPath(p, t.s(0.18f))
    }

    /**
     * El manojo de hisopo puesto a secar boca abajo: el atado de tallos arriba, con sus vueltas de cordel, y debajo la mata de
     * hojitas grises y verdes, más ancha abajo, con las puntas secas y pálidas de las flores.
     */
    private fun DrawScope.hisopo(c: Offset, largo: Float, s: SitioArcontes) {
        val u = density
        val nudo = c + Offset(0f, 7f * u)
        val t = s.t(c + Offset(0f, largo * 0.5f))
        drawLine(t.c(P.CORDEL), c, nudo, 1.2f * u, StrokeCap.Round)
        // Los tallos, que se abren desde el atado.
        val tallos = Path()
        for (k in 0 until 9) {
            val dx = (k - 4) * largo * 0.045f
            tallos.moveTo(nudo.x + (k - 4) * 0.5f * u, nudo.y)
            tallos.quadraticTo(nudo.x + dx * 0.4f, nudo.y + largo * 0.3f, nudo.x + dx, nudo.y + largo * (0.62f + 0.12f * azar(7905, k)))
        }
        drawPath(tallos, t.c(mezcla(P.HISOPO, P.OLIVO_OSC, 0.5f)), style = Stroke(0.8f * u))
        // La mata: hojitas sueltas, de dos verdes, cada una a su modo, dentro de una forma de campana.
        val hondas = Path(); val claras = Path(); val secas = Path()
        for (k in 0 until 70) {
            val f = 0.15f + 0.85f * azar(7906, k)
            val ancho = largo * (0.08f + 0.26f * f)
            val p = Offset(nudo.x + (azar(7907, k) - 0.5f) * 2f * ancho, nudo.y + largo * (0.12f + 0.8f * f))
            val r = (1.3f + 1.2f * azar(7908, k)) * u
            val e = 0.45f + 0.3f * azar(7909, k)
            val o = Rect(p.x - r, p.y - r * e, p.x + r, p.y + r * e)
            when {
                f > 0.85f && k % 2 == 0 -> secas.addOval(o)
                k % 3 == 0 -> claras.addOval(o)
                else -> hondas.addOval(o)
            }
        }
        translate(2.5f * u, 2.5f * u) { drawPath(hondas, t.s(0.16f)) }
        drawPath(hondas, t.c(P.HISOPO, -0.05f))
        drawPath(claras, t.c(P.HISOPO_CLARO))
        drawPath(secas, t.c(mezcla(P.HISOPO_CLARO, P.CALIZA_LUZ, 0.55f)))
        translate(-0.6f * u, -0.6f * u) { drawPath(claras, t.b(0.25f)) }
        // Las vueltas del cordel en el atado.
        for (v in 0 until 3) drawLine(t.c(P.CORDEL), nudo + Offset(-3f * u, 1f * u + v * 1.6f * u), nudo + Offset(3f * u, 0.4f * u + v * 1.6f * u), 1.3f * u, StrokeCap.Round)
        drawLine(t.s(0.4f), nudo + Offset(-3f * u, 5.6f * u), nudo + Offset(3f * u, 5f * u), 0.8f * u)
    }

    /** La forma de la bolsa del escriba colgada en [c]: el cuerpo, más ancho abajo, con la solapa. */
    private fun cuerpoBolsa(c: Offset, w: Float): Path {
        val x0 = c.x - w * 0.055f; val x1 = c.x + w * 0.06f
        val y0 = c.y + w * 0.11f; val y1 = c.y + w * 0.235f
        return Path().apply {
            moveTo(x0 + w * 0.006f, y0)
            lineTo(x1 - w * 0.006f, y0)
            quadraticTo(x1 + w * 0.006f, (y0 + y1) / 2, x1, y1 - w * 0.012f)
            quadraticTo(x1, y1, x1 - w * 0.016f, y1)
            lineTo(x0 + w * 0.014f, y1)
            quadraticTo(x0, y1, x0 - w * 0.002f, y1 - w * 0.014f)
            quadraticTo(x0 - w * 0.006f, (y0 + y1) / 2, x0 + w * 0.006f, y0)
            close()
        }
    }

    private fun DrawScope.sombraBolsa(c: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val t = s.t(c + Offset(0f, w * 0.17f))
        translate(5f * u, 5f * u) { drawPath(cuerpoBolsa(c, w), t.s(0.24f)) }
    }

    /** La bolsa de cuero del escriba, colgada de su correa: la solapa con su pasador de hueso y los cálamos que asoman. */
    private fun DrawScope.bolsa(c: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val t = s.t(c + Offset(0f, w * 0.17f))
        val y0 = c.y + w * 0.11f
        // La correa, de la clavija a los dos lados de la bolsa.
        val correa = t.c(P.CORREA)
        listOf(c.x - w * 0.048f, c.x + w * 0.053f).forEach { x ->
            drawLine(t.s(0.4f), c + Offset(0.6f * u, 1f * u), Offset(x + 0.6f * u, y0 + 1f * u), 2.6f * u, StrokeCap.Round)
            drawLine(correa, c, Offset(x, y0), 2.2f * u, StrokeCap.Round)
        }
        // Los cálamos que asoman por la boca, detrás de la solapa.
        for (k in 0 until 3) {
            val x = c.x - w * 0.02f + k * w * 0.017f
            drawLine(t.c(mezcla(P.JUNCO, P.OLIVO_LUZ, 0.4f)), Offset(x, y0 + 3f * u), Offset(x + (k - 1) * 1.5f * u, y0 - w * (0.03f + 0.012f * k)), 1.8f * u, StrokeCap.Round)
        }
        val cuerpo = cuerpoBolsa(c, w)
        val b = Rect(c.x - w * 0.06f, y0, c.x + w * 0.066f, c.y + w * 0.235f)
        drawPath(cuerpo, Brush.horizontalGradient(listOf(t.c(P.CUERO, 0.12f), t.c(P.CUERO), t.c(P.CUERO, -0.35f)), b.left, b.right))
        clipPath(cuerpo) {
            // Las costuras del canto y los rozones del cuero.
            drawRect(t.s(0.22f), Offset(b.left, b.bottom - w * 0.03f), Size(b.width, w * 0.03f))
            for (k in 0 until 5) drawOval(t.b(0.1f), Offset(b.left + b.width * azar(7911, k), b.top + b.height * azar(7912, k)), Size(w * 0.02f, w * 0.006f))
        }
        val puntadas = Path()
        var y = y0 + w * 0.06f
        while (y < b.bottom - w * 0.01f) { puntadas.moveTo(b.left + w * 0.006f, y); puntadas.lineTo(b.left + w * 0.006f, y + 1.6f * u); y += 3.4f * u }
        drawPath(puntadas, t.c(P.CORDEL).copy(alpha = 0.7f), style = Stroke(0.8f * u))
        // La solapa, con su borde redondeado y su sombra.
        val sol = Path().apply {
            moveTo(b.left + w * 0.004f, y0 - 1f * u); lineTo(b.right - w * 0.004f, y0 - 1f * u)
            lineTo(b.right - w * 0.006f, y0 + w * 0.042f)
            quadraticTo(c.x + w * 0.003f, y0 + w * 0.068f, b.left + w * 0.006f, y0 + w * 0.042f)
            close()
        }
        translate(0.8f * u, 2f * u) { drawPath(sol, t.s(0.4f)) }
        drawPath(sol, t.c(mezcla(P.CUERO, P.CORREA, 0.35f), 0.04f))
        drawPath(sol, t.b(0.3f), style = Stroke(0.7f * u))
        // El pasador de hueso que la cierra.
        val pas = Offset(c.x + w * 0.003f, y0 + w * 0.06f)
        drawLine(t.c(P.CORREA), pas - Offset(0f, w * 0.01f), pas + Offset(0f, w * 0.012f), 1.6f * u)
        drawRoundPill(pas, 1.8f * u, 4.6f * u, t)
    }

    /** Un pasador de hueso tendido en [c]. */
    private fun DrawScope.drawRoundPill(c: Offset, r: Float, largo: Float, t: TonoArcontes) {
        drawLine(t.s(0.4f), c + Offset(-largo / 2 + 0.6f * density, 0.9f * density), c + Offset(largo / 2 + 0.6f * density, 0.9f * density), r * 2f, StrokeCap.Round)
        drawLine(t.c(P.HUESO), c - Offset(largo / 2, 0f), c + Offset(largo / 2, 0f), r * 2f, StrokeCap.Round)
        drawLine(t.b(0.6f), c - Offset(largo / 2, r * 0.4f), c + Offset(largo / 3, -r * 0.4f), r * 0.6f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- el bodegón de encima

    /** El bodegón: primero las sombras en la pared y en la cornisa (una sola vez) y, después, las cosas, con la mancha del sol. */
    fun DrawScope.encima(s: SitioArcontes, apoyo: Float) {
        val w = size.width
        val y = piso(w, apoyo)
        sombras(s, y)
        conManchaArcontes(s) {
            tapaCapsa(Offset(w * 0.465f, y), s)
            tablillasAbiertas(Offset(w * 0.115f, y), s)
            capsa(Offset(w * 0.3f, y), s)
            estilo(Offset(w * 0.06f, y), s)
            cuenco(Offset(w * 0.575f, y), s)
            tintero(Offset(w * 0.49f, y), s)
            montonTablillas(Offset(w * 0.78f, y), s)
            lamparaArriba(s, y)
            alcuza(Offset(w * 0.925f, y), w * 0.075f, s)
        }
    }

    /**
     * Las sombras de todo lo de encima, con la silueta de cada cosa: de día, en la pared, corridas a la derecha (la boca está a la
     * izquierda); de noche, lejos de la lámpara. Y el contacto de cada una en la cornisa.
     */
    private fun DrawScope.sombras(s: SitioArcontes, y: Float) {
        val w = size.width
        val r = CornerRadius(w * 0.02f)
        fun forma(x0: Float, y0: Float, x1: Float, y1: Float) = androidx.compose.ui.geometry.RoundRect(w * x0, y - w * y0, w * x1, y - w * y1, r)
        val izq = Path().apply {
            addRoundRect(forma(0.175f, 0.13f, 0.425f, 0f))
            addRoundRect(forma(0.07f, 0.155f, 0.17f, 0f))
            addOval(Rect(w * 0.43f, y - w * 0.13f, w * 0.5f, y))
            addRoundRect(forma(0.47f, 0.05f, 0.51f, 0f))
            addRoundRect(forma(0.545f, 0.04f, 0.61f, 0f))
        }
        val der = Path().apply {
            addRoundRect(forma(0.695f, 0.04f, 0.865f, 0f))
            addRoundRect(forma(0.72f, 0.085f, 0.83f, 0.03f))
            addOval(Rect(w * 0.895f, y - w * 0.075f, w * 0.955f, y))
        }
        // Los rollos de la capsa, cada uno con su sombra.
        val an = w * 0.25f; val al = w * 0.13f
        val cuerpo = Rect(w * 0.3f - an / 2, y - al, w * 0.3f + an / 2, y)
        for (k in 0 until 6) {
            val q = rolloCapsa(cuerpo, al, an, an * 0.2f, k)
            izq.addRoundRect(androidx.compose.ui.geometry.RoundRect(q.left, q.top - q.width * 0.22f, q.right, q.bottom, CornerRadius(q.width * 0.4f)))
        }
        val t = s.t(Offset(w * 0.4f, y - w * 0.1f))
        if (!s.a.oscuro) {
            translate(w * 0.018f, -w * 0.004f) { drawPath(izq, t.s(0.17f)); drawPath(der, t.s(0.17f)) }
        } else {
            // De noche: lo de la izquierda de la lámpara echa su sombra a la izquierda, larga; lo de la derecha, a la derecha.
            translate(-w * 0.04f, -w * 0.012f) { drawPath(izq, t.s(0.28f)) }
            translate(w * 0.03f, -w * 0.01f) { drawPath(der, t.s(0.24f)) }
        }
        listOf(0.3f to 0.29f, 0.115f to 0.1f, 0.49f to 0.06f, 0.575f to 0.08f, 0.78f to 0.18f, 0.925f to 0.06f).forEach { (x, an) ->
            contactoArcontes(Offset(w * x + w * 0.01f, y + w * 0.002f), w * an, w * 0.012f, s.t(Offset(w * x, y)), 0.42f)
        }
    }

    /** El rollo [k] de los de dentro de la capsa de cuerpo [r]: su rectángulo de pie (para dibujarlo y para su sombra). */
    private fun rolloCapsa(r: Rect, al: Float, an: Float, elipse: Float, k: Int): Rect {
        val x = r.left + an * (0.16f + 0.135f * k)
        val alto = al * (0.55f + 0.45f * azar(7951, k)) + al * 0.6f
        val ancho = an * (0.1f + 0.025f * azar(7952, k))
        val pie = r.top + elipse * (0.1f + 0.2f * azar(7953, k)) + al * 0.6f
        return Rect(x - ancho / 2, pie - alto, x + ancho / 2, pie)
    }

    /** Un rollo de pie (para la capsa): el cilindro de papiro, la boca con la espiral y la etiqueta teñida colgada. */
    private fun DrawScope.rolloDePie(base: Offset, alto: Float, ancho: Float, giro: Float, s: SitioArcontes) {
        val u = density
        val t = s.t(base - Offset(0f, alto * 0.5f))
        rotate(giro, base) {
            val r = Rect(base.x - ancho / 2, base.y - alto, base.x + ancho / 2, base.y)
            drawRect(t.c(mezcla(P.PAPIRO, P.PAPIRO_OSC, 0.45f), -0.02f), r.topLeft, r.size)
            recortado(r) { cilindroArcontes(r, t, -1f, 0.9f) }
            // La boca: el óvalo con las vueltas de la espiral.
            val boca = Rect(r.left, r.top - ancho * 0.22f, r.right, r.top + ancho * 0.22f)
            drawOval(t.c(mezcla(P.PAPIRO, P.PAPIRO_OSC, 0.3f), 0.06f), boca.topLeft, boca.size)
            for (k in 1..3) {
                val f = k / 4f
                drawOval(t.c(P.FIBRA).copy(alpha = 0.6f), Offset(boca.center.x - boca.width * f / 2, boca.center.y - boca.height * f / 2), Size(boca.width * f, boca.height * f), style = Stroke(0.6f * u))
            }
            drawCircle(t.c(P.FIBRA, -0.2f), 0.9f * u, boca.center)
        }
    }

    private fun DrawScope.recortado(r: Rect, bloque: DrawScope.() -> Unit) = clipRect(r.left, r.top, r.right, r.bottom) { bloque() }

    /**
     * La capsa, de pie en [base] (el centro de su suelo): la caja redonda de madera forrada de cuero, vista un poco desde arriba,
     * con su boca, los rollos de dentro, los dos aros de bronce y la correa de llevarla.
     */
    private fun DrawScope.capsa(base: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * 0.25f; val al = w * 0.13f
        val elipse = an * 0.2f
        val r = Rect(base.x - an / 2, base.y - al, base.x + an / 2, base.y)
        val t = s.t(Offset(base.x, base.y - al * 0.5f))
        // La boca por dentro, honda.
        val boca = Rect(r.left, r.top - elipse / 2, r.right, r.top + elipse / 2)
        drawOval(t.c(P.CUERO, -0.6f), boca.topLeft, boca.size)
        // Los rollos de dentro, de alturas distintas, algunos ladeados.
        val tintes = listOf(Color(0xFF9A3A2A), P.ORO, Color(0xFF2F5E6E), P.PERGAMINO, Color(0xFF9A3A2A))
        for (k in 0 until 6) {
            val q = rolloCapsa(r, al, an, elipse, k)
            rolloDePie(Offset(q.center.x, q.bottom), q.height, q.width, (azar(7954, k) - 0.5f) * 14f, s)
        }
        // El cuerpo: el cuero sobre la madera, redondo (claro a la izquierda, hacia la boca de la cueva).
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.left, r.bottom - elipse / 2)
            arcTo(Rect(r.left, r.bottom - elipse, r.right, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top)
            arcTo(Rect(r.left, r.top - elipse / 2, r.right, r.top + elipse / 2), 0f, 180f, false)
            close()
        }
        drawPath(cuerpo, t.c(mezcla(P.CUERO, P.CUERO_CRUDO, 0.4f)))
        clipPath(cuerpo) {
            cilindroArcontes(Rect(r.left, r.top - elipse, r.right, r.bottom), t, -1f, 1.2f)
            // Los rozones del cuero y la costura de atrás que asoma.
            for (k in 0 until 7) drawOval(t.b(0.08f + 0.06f * azar(7961, k)), Offset(r.left + an * azar(7962, k), r.top + al * azar(7963, k)), Size(an * 0.09f, al * 0.05f))
            drawLine(t.s(0.3f), Offset(r.left + an * 0.82f, r.top), Offset(r.left + an * 0.82f, r.bottom), 0.8f * u)
        }
        // El canto de la boca: el labio de madera, con su luz.
        drawOval(t.c(P.OLIVO_LUZ, 0.04f), boca.topLeft, boca.size, style = Stroke(2.4f * u))
        drawArc(t.b(0.5f), 160f, 120f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
        // Los dos aros de bronce, que siguen la curva del cuerpo.
        listOf(0.18f, 0.8f).forEach { f ->
            val y = r.top + al * f
            val aro = Rect(r.left, y - elipse / 2, r.right, y + elipse / 2)
            drawArc(t.s(0.4f), 0f, 180f, false, aro.topLeft + Offset(0f, 1.6f * u), aro.size, style = Stroke(3.6f * u))
            drawArc(Brush.horizontalGradient(listOf(t.c(P.BRONCE_LUZ), t.c(P.BRONCE), t.c(P.BRONCE_OSC)), r.left, r.right), 0f, 180f, false, aro.topLeft, aro.size, style = Stroke(3.2f * u))
            drawArc(t.b(0.5f), 110f, 50f, false, aro.topLeft + Offset(0f, -0.8f * u), aro.size, style = Stroke(0.8f * u))
            drawArc(t.c(P.CARDENILLO).copy(alpha = 0.5f), 20f, 30f, false, aro.topLeft, aro.size, style = Stroke(1.6f * u))
        }
        // Las etiquetas de los rollos, de pergamino teñido, colgadas por el borde de la boca con su hilo.
        listOf(0.2f, 0.42f, 0.58f, 0.8f).forEachIndexed { j, f ->
            val x = r.left + an * f
            val col = tintes[(j * 2 + 1) % tintes.size]
            val et = Rect(x - an * 0.032f, r.top + al * 0.12f, x + an * 0.032f, r.top + al * 0.12f + an * 0.16f)
            rotate((azar(7955, j) - 0.5f) * 16f, Offset(x, r.top)) {
                drawLine(t.c(P.CORDEL), Offset(x, r.top - elipse * 0.3f), Offset(x, et.top + 1f * u), 0.7f * u)
                drawRect(t.s(0.35f), et.topLeft + Offset(1f * u, 1.2f * u), et.size)
                drawRect(t.c(col), et.topLeft, et.size)
                drawRect(t.b(0.35f), et.topLeft, Size(et.width, 0.8f * u))
                drawRect(t.s(0.2f), Offset(et.left, et.bottom - et.height * 0.25f), Size(et.width, et.height * 0.25f))
            }
        }
        // La correa de llevarla: sale de un remache de cada lado y se arquea por detrás de los rollos.
        val i0 = Offset(r.left + 2f * u, r.top + al * 0.32f); val i1 = Offset(r.right - 2f * u, r.top + al * 0.32f)
        val correa = Path().apply { moveTo(i0.x, i0.y); cubicTo(i0.x - an * 0.08f, r.top - al * 0.5f, i1.x + an * 0.12f, r.top - al * 0.65f, i1.x, i1.y) }
        drawPath(correa, t.s(0.35f), style = Stroke(3.6f * u, cap = StrokeCap.Round))
        drawPath(correa, t.c(P.CORREA), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawPath(correa, t.b(0.25f), style = Stroke(0.7f * u))
        clavoArcontes(i0, 1.9f * u, t); clavoArcontes(i1, 1.9f * u, t)
        // El filo del lado de la luz: de día, el sol de la boca; de noche, la lámpara (a la derecha).
        if (s.a.oscuro) drawLine(t.filo(0.6f), Offset(r.right - 1f * u, r.top), Offset(r.right - 1f * u, r.bottom - elipse / 2), 1.6f * u)
        else drawLine(t.b(0.5f), Offset(r.left + 1f * u, r.top), Offset(r.left + 1f * u, r.bottom - elipse / 2), 1.4f * u)
    }

    /** La tapa de la capsa, apoyada de canto contra su costado: el disco de madera forrado, con su botón de bronce. */
    private fun DrawScope.tapaCapsa(base: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val rx = w * 0.04f; val ry = w * 0.125f / 2
        val c = Offset(base.x, base.y - ry)
        val t = s.t(c)
        rotate(10f, base) {
            drawOval(t.c(P.CUERO, -0.25f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
            drawOval(t.c(mezcla(P.CUERO, P.CUERO_CRUDO, 0.4f), 0.02f), Offset(c.x - rx * 0.78f, c.y - ry * 0.95f), Size(rx * 1.5f, ry * 1.9f))
            drawOval(t.c(P.OLIVO_LUZ, -0.05f), Offset(c.x - rx * 0.78f, c.y - ry * 0.95f), Size(rx * 1.5f, ry * 1.9f), style = Stroke(1.8f * u))
            drawCircle(t.c(P.BRONCE), 2.6f * u, c + Offset(-rx * 0.05f, 0f))
            drawCircle(t.b(0.6f), 1f * u, c + Offset(-rx * 0.05f - 0.8f * u, -0.8f * u))
        }
    }

    /** Las tablillas de cera abiertas, apoyadas contra la capsa: dos hojas de madera con su cera oscura rayada y el cordel de la bisagra. */
    private fun DrawScope.tablillasAbiertas(base: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * 0.075f; val al = w * 0.15f
        val t = s.t(base - Offset(0f, al / 2))
        rotate(9f, base) {
            listOf(-1f, 1f).forEach { lado ->
                val r = if (lado < 0f) Rect(base.x - an, base.y - al, base.x, base.y) else Rect(base.x, base.y - al, base.x + an * 0.82f, base.y)
                val c = if (lado < 0f) t.c(P.OLIVO, 0.04f) else t.c(P.OLIVO, -0.14f)
                drawRect(c, r.topLeft, r.size)
                val cera = r.deflate(an * 0.12f)
                drawRect(t.c(P.CERA, if (lado < 0f) 0.04f else -0.1f), cera.topLeft, cera.size)
                drawRect(t.s(0.4f), cera.topLeft, Size(cera.width, 1.4f * u))
                drawRect(t.s(0.3f), cera.topLeft, Size(1.2f * u, cera.height))
                // Rayas a punta de estilo: renglones sin letra.
                var y = cera.top + 4f * u; var j = 0
                while (y < cera.bottom - 2f * u && j < 20) {
                    val l = cera.width * (0.45f + 0.45f * azar(7971 + lado.toInt(), j))
                    drawLine(t.c(mezcla(P.CERA, P.PAPIRO_OSC, 0.55f)).copy(alpha = 0.75f), Offset(cera.left + 2f * u, y), Offset(cera.left + 2f * u + l - 3f * u, y), 0.6f * u)
                    y += 3.6f * u; j++
                }
                drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.9f * u))
            }
            // El cordel de la bisagra, en dos puntos.
            listOf(0.25f, 0.75f).forEach { f ->
                val p = Offset(base.x, base.y - al * f)
                drawLine(t.c(P.CORDEL), p - Offset(2.4f * u, 0f), p + Offset(2.4f * u, 0f), 1.4f * u, StrokeCap.Round)
            }
        }
    }

    /** El estilo de bronce tendido delante de las tablillas, con su espátula. */
    private fun DrawScope.estilo(base: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val t = s.t(base)
        val a = base + Offset(-w * 0.01f, -1.2f * u); val b = base + Offset(w * 0.075f, -2.6f * u)
        drawLine(t.s(0.4f), a + Offset(1f * u, 1.6f * u), b + Offset(1f * u, 1.6f * u), 1.8f * u, StrokeCap.Round)
        drawLine(t.c(P.BRONCE), a, b, 1.5f * u, StrokeCap.Round)
        drawLine(t.b(0.5f), a + Offset(0f, -0.5f * u), b + Offset(0f, -0.5f * u), 0.5f * u)
        drawPath(poligonoArcontes(a.x, a.y - 1.8f * u, a.x - 3.4f * u, a.y - 1.6f * u, a.x - 3.4f * u, a.y + 1.6f * u, a.x, a.y + 1.6f * u), t.c(P.BRONCE, 0.06f))
    }

    /** El cuenco de sal del salador: el barro con su labio, el montoncito de sal con sus granos y la luz. */
    private fun DrawScope.cuenco(base: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val r = w * 0.036f
        val t = s.t(base)
        val boca = Rect(base.x - r, base.y - r * 0.9f, base.x + r, base.y - r * 0.5f)
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y)
            quadraticTo(base.x - r * 0.95f, base.y, base.x, base.y)
            quadraticTo(base.x + r * 0.95f, base.y, boca.right, boca.center.y)
            close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(t.c(P.BARRO_CLARO, 0.06f), t.c(P.BARRO_CLARO), t.c(P.BARRO, -0.2f)), boca.left, boca.right))
        val sal = Path().apply {
            moveTo(boca.left + r * 0.1f, boca.center.y)
            quadraticTo(base.x - r * 0.3f, boca.top - r * 0.55f, base.x + r * 0.15f, boca.top - r * 0.4f)
            quadraticTo(base.x + r * 0.6f, boca.top - r * 0.2f, boca.right - r * 0.1f, boca.center.y)
            close()
        }
        drawOval(t.c(P.BARRO, -0.4f), boca.topLeft, boca.size)
        drawPath(sal, t.c(P.SAL))
        drawPath(sal, t.s(0.12f), style = Stroke(0.6f * u))
        for (k in 0 until 10) drawCircle(t.c(mezcla(P.SAL, P.CALIZA_SOMBRA, 0.5f)), 0.45f * u, Offset(base.x + r * (azar(7981, k) * 1.3f - 0.65f), boca.top - r * 0.3f * azar(7982, k)))
        drawArc(t.b(0.5f), 180f, 90f, false, boca.topLeft, boca.size, style = Stroke(0.8f * u))
    }

    /** El tintero de bronce, con su tapa en cúpula, y los dos cálamos apoyados. */
    private fun DrawScope.tintero(base: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * 0.042f; val al = w * 0.045f
        val r = Rect(base.x - an / 2, base.y - al, base.x + an / 2, base.y)
        val t = s.t(r.center)
        // Los cálamos, por detrás, apoyados contra el tintero.
        listOf(-0.08f to 0.13f, 0.2f to 0.11f).forEachIndexed { k, (dx, l) ->
            val a = Offset(r.center.x + an * dx, r.top + 2f * u); val b = Offset(r.center.x + w * (0.05f + 0.02f * k), r.top - w * l)
            drawLine(t.s(0.35f), a + Offset(1f * u, 1f * u), b + Offset(1f * u, 1f * u), 2.2f * u, StrokeCap.Round)
            drawLine(t.c(mezcla(P.JUNCO, P.OLIVO_LUZ, 0.3f)), a, b, 1.9f * u, StrokeCap.Round)
            drawLine(t.c(P.JUNCO_OSC), a + (b - a) * 0.4f + Offset(-1.2f * u, 0f), a + (b - a) * 0.4f + Offset(1.2f * u, 0f), 0.8f * u)
            drawLine(t.c(P.TINTA), b, b + (b - a) * 0.04f, 1.4f * u, StrokeCap.Round)
        }
        drawRect(t.c(P.BRONCE), r.topLeft, r.size)
        recortado(r) { cilindroArcontes(r, t, -1f, 1.3f) }
        drawRect(t.c(P.CARDENILLO).copy(alpha = 0.35f), Offset(r.left, r.bottom - al * 0.2f), Size(an, al * 0.2f))
        drawLine(t.c(P.BRONCE_OSC), Offset(r.left, r.top + al * 0.3f), Offset(r.right, r.top + al * 0.3f), 0.8f * u)
        val tapa = Rect(r.left - an * 0.06f, r.top - al * 0.28f, r.right + an * 0.06f, r.top + al * 0.08f)
        drawArc(Brush.horizontalGradient(listOf(t.c(P.BRONCE_LUZ), t.c(P.BRONCE_OSC)), tapa.left, tapa.right), 180f, 180f, true, tapa.topLeft, Size(tapa.width, tapa.height * 2f))
        drawCircle(t.c(P.BRONCE_LUZ), 1.6f * u, Offset(r.center.x, tapa.top))
        destelloArcontes(Offset(r.left + an * 0.28f, r.top + al * 0.45f), 0.9f * u, t)
    }

    /** El montón de tablillas atadas donde está la lámpara: tres tablas de madera con su canto y el cordel que las ciñe. */
    private fun DrawScope.montonTablillas(base: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * 0.17f; val gr = w * 0.012f
        val t = s.t(base - Offset(0f, gr * 1.5f))
        for (k in 0 until 3) {
            val dx = (azar(7991, k) - 0.5f) * w * 0.012f
            val r = Rect(base.x - an / 2 + dx, base.y - gr * (k + 1), base.x + an / 2 + dx, base.y - gr * k)
            drawRect(t.c(if (k % 2 == 0) P.OLIVO else mezcla(P.OLIVO, P.OLIVO_OSC, 0.3f), 0.02f), r.topLeft, r.size)
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 0.8f * u))
            drawRect(t.s(0.4f), Offset(r.left, r.bottom - 0.9f * u), Size(r.width, 0.9f * u))
            // El canto de la cera, oscuro, entre las dos caras.
            drawRect(t.c(P.CERA).copy(alpha = 0.5f), Offset(r.left + an * 0.06f, r.top + gr * 0.4f), Size(an * 0.88f, gr * 0.22f))
        }
        val x = base.x - an * 0.3f
        drawLine(t.c(P.CORDEL), Offset(x, base.y - gr * 3.2f), Offset(x + 1.2f * u, base.y), 1.4f * u, StrokeCap.Round)
        drawLine(t.s(0.3f), Offset(x + 1.2f * u, base.y - gr * 3.2f), Offset(x + 2.2f * u, base.y), 0.6f * u)
    }

    /** La lámpara de aceite sobre el montón de tablillas: apagada de día (con el pico tiznado), encendida de noche. */
    private fun DrawScope.lamparaArriba(s: SitioArcontes, y: Float) {
        val w = size.width; val u = density
        val lw = w * 0.11f
        val pie = y - w * 0.036f
        val c = Offset(MuebleEstanteArcontes.LAMP_X * w - lw * 0.39f, pie - lw * 0.36f * 0.45f)
        val t = s.t(c)
        contactoArcontes(Offset(c.x + lw * 0.04f, pie), lw * 0.8f, lw * 0.1f, t, 0.4f)
        val enc = s.a.lumbre
        val barro = if (enc && s.a.oscuro) mezcla(t.c(P.BARRO_CLARO), P.LLAMA, 0.2f) else t.c(P.BARRO_CLARO)
        with(TrazosArcontes) { lampara(c, lw, barro, if (enc) (if (s.a.oscuro && !s.a.noche) 1f else 0.75f) else 0f) }
        if (!enc) drawCircle(P.TINTA.copy(alpha = 0.45f), lw * 0.05f, Offset(c.x + lw * 0.4f, c.y - lw * 0.36f * 0.36f))
        // El filo de la luz en el hombro: el sol de día; de noche, la propia llama.
        if (!s.a.oscuro) drawArc(t.b(0.35f), 200f, 60f, false, Offset(c.x - lw * 0.36f, c.y - lw * 0.3f), Size(lw * 0.5f, lw * 0.45f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
    }

    /** La alcuza (el jarrito del aceite de la lámpara), como la del Ideario: el cuerpo redondo, el cuello, la boca y el asa. */
    fun DrawScope.alcuza(base: Offset, a: Float, s: SitioArcontes) {
        val u = density
        val t = s.t(base - Offset(0f, a * 0.4f))
        val barro = t.c(P.BARRO)
        val cuerpo = Path().apply {
            moveTo(base.x - a * 0.12f, base.y)
            cubicTo(base.x - a * 0.36f, base.y - a * 0.05f, base.x - a * 0.34f, base.y - a * 0.5f, base.x - a * 0.08f, base.y - a * 0.62f)
            lineTo(base.x - a * 0.07f, base.y - a * 0.86f); lineTo(base.x + a * 0.07f, base.y - a * 0.86f); lineTo(base.x + a * 0.08f, base.y - a * 0.62f)
            cubicTo(base.x + a * 0.34f, base.y - a * 0.5f, base.x + a * 0.36f, base.y - a * 0.05f, base.x + a * 0.12f, base.y)
            close()
        }
        drawPath(Path().apply { moveTo(base.x + a * 0.07f, base.y - a * 0.78f); cubicTo(base.x + a * 0.34f, base.y - a * 0.82f, base.x + a * 0.34f, base.y - a * 0.5f, base.x + a * 0.2f, base.y - a * 0.44f) },
            t.c(P.BARRO, -0.25f), style = Stroke(a * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(cuerpo, Brush.horizontalGradient(listOf(t.c(P.BARRO_CLARO, 0.05f), barro, t.c(P.BARRO, -0.4f)), base.x - a * 0.3f, base.x + a * 0.3f))
        drawOval(t.c(P.BARRO, -0.6f), Offset(base.x - a * 0.09f, base.y - a * 0.9f), Size(a * 0.18f, a * 0.07f))
        for (k in 0 until 3) drawLine(t.s(0.22f), Offset(base.x - a * 0.26f, base.y - a * (0.2f + 0.08f * k)), Offset(base.x + a * 0.26f, base.y - a * (0.2f + 0.08f * k)), 0.5f * u)
        drawArc(t.b(0.4f), 190f, 60f, false, Offset(base.x - a * 0.3f, base.y - a * 0.56f), Size(a * 0.36f, a * 0.5f), style = Stroke(1f * u))
    }
}
