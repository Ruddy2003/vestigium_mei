package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.ADORMIDERA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.ARCILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.Aire
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.BARNIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.CANA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.CANA_VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.HIGO
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.HIGO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.HOJA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleTapaHeleno.VETA
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las piezas chicas del libro del día de Helénico ([MuebleTapaHeleno]): la lucerna de barniz negro con su llama, el cuenco
 * con higos (o con adormideras, en el sueño) y el cálamo. Cada una con su luz y su sombra según el [Aire] del momento.
 */
internal object PiezasTapaHeleno {
    /**
     * La lucerna ática de barniz negro, vista un poco desde arriba: el cuerpo bajo y redondo con su agujero de llenar, el pico
     * largo hacia la izquierda (de día, con el hollín de la mecha) y el asa de cinta a la derecha; su sombra en la mesa.
     */
    fun DrawScope.lucerna(pie: Offset, s: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        val kk = maxOf(k, if (a.lumbre) 0.6f else 0f)
        val barniz = a.luz(BARNIZ, kk)
        val arriba = pie.y - s * 0.05f
        // La sombra en la mesa: de día y con la luna, larga a la derecha; con la llama encendida, debajo y hacia atrás.
        drawOval(a.sombra(if (a.lumbre) 0.45f else 0.3f), Offset(pie.x - s * 0.15f + caida.x, pie.y - s * 0.03f + caida.y * 0.3f), Size(s * 0.3f, s * 0.05f))
        drawOval(a.sombra(0.2f), Offset(pie.x - s * 0.12f + caida.x * 2f, pie.y - s * 0.025f + caida.y * 0.5f), Size(s * 0.28f, s * 0.04f))
        // El asa de cinta.
        drawArc(barniz, -70f, 200f, false, Offset(pie.x + s * 0.045f, arriba - s * 0.03f), Size(s * 0.06f, s * 0.05f), style = Stroke(s * 0.014f))
        // El pico.
        val pico = Path().apply {
            moveTo(pie.x - s * 0.04f, arriba - s * 0.014f)
            lineTo(pie.x - s * 0.135f, arriba - s * 0.012f)
            cubicTo(pie.x - s * 0.162f, arriba - s * 0.01f, pie.x - s * 0.162f, arriba + s * 0.016f, pie.x - s * 0.135f, arriba + s * 0.018f)
            lineTo(pie.x - s * 0.04f, arriba + s * 0.026f)
            close()
        }
        drawPath(pico, barniz)
        // El cuerpo: el costado y la cara de arriba, con el anillo del borde y el agujero.
        drawOval(barniz, Offset(pie.x - s * 0.062f, pie.y - s * 0.036f), Size(s * 0.124f, s * 0.042f))
        drawRect(barniz, Offset(pie.x - s * 0.068f, arriba), Size(s * 0.136f, pie.y - s * 0.015f - arriba))
        drawOval(a.luz(mezcla(BARNIZ, ARCILLA, 0.12f), kk), Offset(pie.x - s * 0.07f, arriba - s * 0.026f), Size(s * 0.14f, s * 0.052f))
        drawOval(a.luz(BARNIZ, kk), Offset(pie.x - s * 0.048f, arriba - s * 0.017f), Size(s * 0.096f, s * 0.034f))
        drawOval(mezcla(BARNIZ, NOCHE, 0.5f), Offset(pie.x - s * 0.014f, arriba - s * 0.006f), Size(s * 0.028f, s * 0.012f))
        // El brillo del barniz: de día, arriba a la izquierda; con la lucerna encendida, el filo del lado del pico.
        if (!a.lumbre) {
            drawArc(a.claro(0.55f), 190f, 70f, false, Offset(pie.x - s * 0.066f, arriba - s * 0.024f), Size(s * 0.132f, s * 0.048f), style = Stroke(1f * u))
            drawLine(a.claro(0.45f), Offset(pie.x - s * 0.13f, arriba - s * 0.008f), Offset(pie.x - s * 0.05f, arriba - s * 0.01f), 0.8f * u)
            drawOval(sombraMueble(0.6f), Offset(pie.x - s * 0.165f, arriba - s * 0.008f), Size(s * 0.03f, s * 0.022f))
        } else {
            drawArc(FILO_MUEBLE.copy(alpha = 0.85f), 150f, 90f, false, Offset(pie.x - s * 0.066f, arriba - s * 0.024f), Size(s * 0.132f, s * 0.048f), style = Stroke(1.1f * u))
            drawLine(FILO_MUEBLE.copy(alpha = 0.9f), Offset(pie.x - s * 0.135f, arriba - s * 0.011f), Offset(pie.x - s * 0.05f, arriba - s * 0.013f), 0.9f * u)
            if (a.sueno) drawArc(LUNA.copy(alpha = 0.5f), 200f, 60f, false, Offset(pie.x - s * 0.07f, arriba - s * 0.026f), Size(s * 0.14f, s * 0.052f), style = Stroke(0.8f * u))
        }
        // La línea de arcilla reservada del costado.
        drawLine(a.luz(ARCILLA, kk).copy(alpha = 0.7f), Offset(pie.x - s * 0.066f, pie.y - s * 0.022f), Offset(pie.x + s * 0.066f, pie.y - s * 0.022f), 0.6f * u)
    }

    /** La llama de la lucerna: la lengua cálida y su corazón claro. No pasa por la noche: da luz. */
    fun DrawScope.llamita(base: Offset, alto: Float) {
        val a = alto * 0.36f
        val lengua = Path().apply {
            moveTo(base.x, base.y)
            cubicTo(base.x - a * 0.7f, base.y - alto * 0.15f, base.x - a * 0.5f, base.y - alto * 0.6f, base.x + a * 0.05f, base.y - alto)
            cubicTo(base.x + a * 0.25f, base.y - alto * 0.6f, base.x + a * 0.7f, base.y - alto * 0.2f, base.x, base.y)
            close()
        }
        drawPath(lengua, Color(0xFFF2A24A))
        val corazon = Path().apply {
            moveTo(base.x, base.y - alto * 0.04f)
            cubicTo(base.x - a * 0.35f, base.y - alto * 0.2f, base.x - a * 0.2f, base.y - alto * 0.5f, base.x + a * 0.04f, base.y - alto * 0.68f)
            cubicTo(base.x + a * 0.15f, base.y - alto * 0.45f, base.x + a * 0.35f, base.y - alto * 0.2f, base.x, base.y - alto * 0.04f)
            close()
        }
        drawPath(corazon, Color(0xFFFFF0C4))
        drawOval(Color(0xFF6F7FB8).copy(alpha = 0.7f), Offset(base.x - a * 0.22f, base.y - alto * 0.08f), Size(a * 0.44f, alto * 0.12f))
    }

    /**
     * El cuenco de barniz negro con lo que se come al escribir: de día, higos y una hoja de higuera; en el sueño, tres cabezas
     * de adormidera (la planta de Hipnos), grandes y con su corona.
     */
    fun DrawScope.cuencoFrutas(c: Offset, s: Float, a: Aire, k: Float, l: Float, sueno: Boolean, caida: Offset) {
        val u = density
        val rx = s * 0.1f; val ry = s * 0.036f
        val barniz = a.luz(BARNIZ, k, l)
        drawOval(a.sombra(if (a.oscuro) 0.42f else 0.3f), Offset(c.x - rx + caida.x, c.y - ry * 0.2f + caida.y * 0.5f), Size(rx * 2.1f, ry * 2.4f))
        // El cuerpo bajo y la boca, con su brillo de barniz y la línea de arcilla del labio.
        val cuerpo = Path().apply {
            moveTo(c.x - rx, c.y)
            cubicTo(c.x - rx, c.y + ry * 1.6f, c.x + rx, c.y + ry * 1.6f, c.x + rx, c.y)
            close()
        }
        drawPath(cuerpo, barniz)
        drawOval(barniz, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(a.luz(mezcla(BARNIZ, ARCILLA, 0.1f), k, l), Offset(c.x - rx * 0.86f, c.y - ry * 0.8f), Size(rx * 1.72f, ry * 1.6f))
        drawArc(a.luz(ARCILLA, k, l).copy(alpha = 0.7f), 20f, 140f, false, Offset(c.x - rx * 0.96f, c.y - ry * 0.9f), Size(rx * 1.92f, ry * 1.8f), style = Stroke(0.6f * u))
        drawArc(if (a.oscuro && !sueno) a.filo(0.6f) else a.claro(0.6f), 110f, 60f, false, Offset(c.x - rx * 0.98f, c.y - ry * 0.4f), Size(rx * 1.96f, ry * 2.2f), style = Stroke(1f * u))
        if (!sueno) {
            // La hoja de higuera, de tres lóbulos, saliendo por el borde de atrás.
            val hoja = a.luz(HOJA, k, l)
            for ((ang, lado) in listOf(-30f to 0f, -60f to -1f, 0f to 1f)) {
                val p = Offset(c.x - rx * 0.2f + lado * rx * 0.25f, c.y - ry * 0.9f)
                rotate(ang, p) { drawOval(hoja, Offset(p.x - rx * 0.16f, p.y - ry * 1.6f), Size(rx * 0.32f, ry * 1.8f)) }
            }
            // Los higos: gotas moradas con su rabito; uno abierto, con la pulpa roja.
            val higos = listOf(Offset(c.x - rx * 0.35f, c.y - ry * 0.1f), Offset(c.x + rx * 0.25f, c.y - ry * 0.2f), Offset(c.x - rx * 0.02f, c.y + ry * 0.35f))
            higos.forEachIndexed { i, p ->
                val r = s * 0.026f
                drawCircle(a.sombra(0.35f), r, p + Offset(r * 0.25f, r * 0.3f))
                drawCircle(a.luz(HIGO, k, l), r, p)
                drawLine(a.luz(HIGO, k, l), p, p + Offset(r * 0.5f, -r * 1.2f), r * 0.45f, StrokeCap.Round)
                if (i == 2) {
                    drawCircle(a.luz(Color(0xFFC0485A), k, l), r * 0.62f, p)
                    drawCircle(a.luz(Color(0xFFE9C9A0), k, l), r * 0.3f, p)
                } else drawCircle(if (a.oscuro) a.filo(0.5f * k) else a.claro(0.4f), r * 0.32f, p - Offset(r * 0.35f, r * 0.35f))
                if (i < 2) drawCircle(a.luz(HIGO_LUZ, k, l).copy(alpha = 0.5f), r * 0.55f, p - Offset(r * 0.2f, r * 0.15f))
            }
        } else {
            // Las adormideras: la cápsula verde gris, con su cuello y su corona de rayos encima, y el tallo largo y seco.
            val cabezas = listOf(Offset(c.x - rx * 0.42f, c.y - ry * 0.5f), Offset(c.x + rx * 0.22f, c.y - ry * 0.75f), Offset(c.x + rx * 0.0f, c.y + ry * 0.25f))
            cabezas.forEachIndexed { i, p ->
                val r = s * (0.026f + 0.004f * i)
                val verde = a.luz(ADORMIDERA, k, l)
                val oscura = a.luz(mezcla(ADORMIDERA, BARNIZ, 0.5f), k, l)
                drawLine(a.luz(mezcla(ADORMIDERA, CANA, 0.5f), k, l), p + Offset(0f, r * 0.8f), p + Offset(rx * (0.7f - 0.45f * i), ry * 1.6f), 1.3f * u, StrokeCap.Round)
                drawOval(a.sombra(0.4f), Offset(p.x - r + r * 0.3f, p.y - r * 0.8f + r * 0.35f), Size(r * 2f, r * 1.75f))
                drawOval(verde, Offset(p.x - r, p.y - r * 0.8f), Size(r * 2f, r * 1.75f))
                // Las costillas de la cápsula.
                for (j in -1..1) drawLine(oscura.copy(alpha = 0.5f), Offset(p.x + j * r * 0.45f, p.y - r * 0.7f), Offset(p.x + j * r * 0.55f, p.y + r * 0.85f), 0.5f * u)
                // El cuello y la corona, como un sombrerito plano de rayos.
                drawRect(oscura, Offset(p.x - r * 0.22f, p.y - r * 1.05f), Size(r * 0.44f, r * 0.3f))
                drawOval(oscura, Offset(p.x - r * 0.66f, p.y - r * 1.3f), Size(r * 1.32f, r * 0.42f))
                for (j in 0 until 7) {
                    val x = p.x - r * 0.55f + r * 0.183f * j
                    drawLine(verde, Offset(p.x, p.y - r * 1.12f), Offset(x, p.y - r * 1.22f), 0.5f * u)
                }
                // La luna en el lado de la cápsula que la mira.
                drawOval(LUNA.copy(alpha = 0.55f), Offset(p.x - r * 0.8f, p.y - r * 0.6f), Size(r * 0.55f, r * 0.9f))
            }
        }
    }

    /**
     * El cálamo de caña, dejado en el margen: la caña con su sombra arrojada, el lado de la luz y el de la sombra, los nudos un
     * poco más gruesos, la punta cortada en bisel (se ve el hueco de la caña) con su hendidura y, si ya se escribió, la tinta.
     */
    fun DrawScope.calamo(desde: Offset, hasta: Offset, s: Float, a: Aire, k: Float, l: Float, entintado: Boolean, caida: Offset) {
        val u = density
        val g = s * 0.024f
        val v = hasta - desde; val largo = v.getDistance().coerceAtLeast(1f); val dir = v / largo
        val normal = Offset(-dir.y, dir.x)
        val corte = hasta - dir * (s * 0.09f)
        // La sombra arrojada en la hoja y en la mesa.
        drawLine(a.sombra(if (a.oscuro) 0.5f else 0.32f), desde + caida, corte + caida, g * 1.1f, StrokeCap.Round)
        drawPath(Path().apply { val p = corte + caida; moveTo(p.x + normal.x * g / 2, p.y + normal.y * g / 2); lineTo(hasta.x + caida.x, hasta.y + caida.y); lineTo(p.x - normal.x * g / 2, p.y - normal.y * g / 2); close() },
            a.sombra(if (a.oscuro) 0.5f else 0.32f))
        // La caña: el color, el lado de la sombra (abajo) y el de la luz (arriba).
        val cana = a.luz(CANA, k, l)
        drawLine(cana, desde, corte, g, StrokeCap.Butt)
        drawLine(a.luz(mezcla(CANA, CANA_VERDE, 0.6f), k, l), desde + normal * (g * 0.28f), corte + normal * (g * 0.28f), g * 0.42f, StrokeCap.Butt)
        drawLine(if (a.oscuro && !a.sueno) a.filo(0.5f * k) else a.claro(0.55f), desde - normal * (g * 0.3f), corte - normal * (g * 0.3f), g * 0.18f, StrokeCap.Butt)
        // Los nudos: un anillo apenas más grueso y más oscuro, con su luz.
        for (f in listOf(0.22f, 0.55f)) {
            val p = desde + v * f
            drawLine(a.luz(mezcla(CANA, BARNIZ, 0.35f), k, l), p + normal * (g * 0.62f), p - normal * (g * 0.62f), 1.6f * u)
            drawLine(a.claro(0.4f), p + normal * (g * 0.5f) - dir * (1.2f * u), p - normal * (g * 0.5f) - dir * (1.2f * u), 0.6f * u)
        }
        // La punta en bisel: el corte largo deja ver el hueco de la caña y se afina hasta la hendidura.
        val bisel = Path().apply {
            moveTo(corte.x + normal.x * g / 2, corte.y + normal.y * g / 2)
            lineTo(hasta.x, hasta.y)
            lineTo(corte.x - normal.x * g / 2, corte.y - normal.y * g / 2)
            close()
        }
        drawPath(bisel, a.luz(mezcla(CANA, LUZ_MUEBLE, 0.25f), k, l))
        val hueco = Path().apply {
            val p = corte + dir * (s * 0.012f)
            moveTo(p.x + normal.x * g * 0.3f, p.y + normal.y * g * 0.3f)
            lineTo(hasta.x - dir.x * s * 0.025f, hasta.y - dir.y * s * 0.025f)
            lineTo(p.x - normal.x * g * 0.3f, p.y - normal.y * g * 0.3f)
            close()
        }
        drawPath(hueco, a.luz(mezcla(CANA, VETA, 0.55f), k, l))
        if (entintado) {
            val t0 = hasta - dir * (s * 0.04f)
            drawPath(Path().apply {
                moveTo(t0.x + normal.x * g * 0.32f, t0.y + normal.y * g * 0.32f); lineTo(hasta.x, hasta.y); lineTo(t0.x - normal.x * g * 0.32f, t0.y - normal.y * g * 0.32f); close()
            }, TINTA.copy(alpha = 0.9f))
        }
        drawLine(a.sombra(0.6f), hasta - dir * (s * 0.035f), hasta, 0.6f * u)
        // El otro cabo de la caña, cortado en recto: se ve su anillo.
        drawCircle(a.luz(mezcla(CANA, VETA, 0.3f), k, l), g * 0.5f, desde)
        drawCircle(a.luz(VETA, k, l), g * 0.25f, desde)
    }
}
