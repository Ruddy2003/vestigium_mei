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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que rodea la carpeta en la mesa del comedor de Espacio ([MuebleLibretaEspacio]), pocas cosas y grandes, todas sujetas como
 * se sujeta lo que no pesa: la bolsa de café pinzada al pasamanos (el café flota en un globo dentro de la bolsa), los cubiertos
 * y las tijeras pegados a la barra imantada, la placa con sus signos, la lámpara de pinza, la bolsa de tortillas bajo la goma de
 * la mesa, la gota que se escapó de la pajita y el piloto de guardia. Cada pieza recibe la luz en un punto ([LuzLibretaEspacio]).
 */
internal object PiezasLibretaEspacio {
    private val P = PaletaEstanteEspacio
    private val trazo = Path()

    /** El café, la lámina transparente de las bolsas, la tortilla y su tostado. */
    private val CAFE = Color(0xFF6B4226)
    private val LAMINA = Color(0xFFD4DAE2)
    private val TORTILLA = Color(0xFFE2C48C)
    private val TOSTADO = Color(0xFF9C6A2E)

    // ---------------------------------------------------------------- en la pared

    /**
     * La bolsa de café, pinzada por abajo al pasamanos con una pinza de papelería (sin peso, la bolsa flota hacia arriba):
     * la lámina transparente con sus soldaduras, el café dentro hecho un globo con sus burbujas, la etiqueta con su raya de
     * color y sus signos, la pajita con su pinza roja y el brillo de la lámina.
     */
    fun DrawScope.bebida(agarre: Offset, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(agarre)
        val d = luz.caida(agarre, 4f * u)
        rotate(-7f, agarre) {
            val b = Rect(agarre.x - 16f * u, agarre.y - 58f * u, agarre.x + 16f * u, agarre.y - 2f * u)
            // La sombra en la pared y el cuerpo de lámina.
            drawRoundRect(a.sombra(0.25f), b.topLeft + d, b.size, CornerRadius(5f * u))
            drawRoundRect(a.luz(LAMINA, l).copy(alpha = 0.85f), b.topLeft, b.size, CornerRadius(5f * u))
            // La bolsa llena de café y, sin peso, una burbuja de aire que flota en medio (no sube a la boca) y otras chicas.
            val cafe = Rect(b.left + 2.5f * u, b.top + 6f * u, b.right - 2.5f * u, b.bottom - 6f * u)
            drawRoundRect(a.luz(CAFE, l).copy(alpha = 0.94f), cafe.topLeft, cafe.size, CornerRadius(3.5f * u))
            val burbuja = Offset(b.left + b.width * 0.62f, b.top + b.height * 0.36f)
            drawCircle(a.luz(mezcla(LAMINA, CAFE, 0.25f), l), 5.5f * u, burbuja)
            drawCircle(a.luz(mezcla(CAFE, P.PAPEL, 0.25f), l), 5.5f * u, burbuja, style = Stroke(1f * u))
            drawCircle(Color.White.copy(alpha = if (a.oscuro) 0.3f else 0.8f), 1.5f * u, burbuja - Offset(2f * u, 2f * u))
            listOf(Offset(0.3f, 0.24f) to 1.6f, Offset(0.4f, 0.47f) to 1.1f, Offset(0.8f, 0.52f) to 1.3f).forEach { (fr, rr) ->
                drawCircle(a.luz(mezcla(CAFE, P.PAPEL, 0.55f), l), rr * u, Offset(b.left + b.width * fr.x, b.top + b.height * fr.y))
            }
            // La etiqueta, con su raya de color y sus signos.
            val et = Rect(b.left + 4f * u, b.top + b.height * 0.56f, b.right - 4f * u, b.top + b.height * 0.8f)
            drawRect(a.luz(P.PAPEL, l), et.topLeft, et.size)
            drawRect(a.luz(P.NARANJA, l), et.topLeft, Size(et.width, 2.4f * u))
            signos(Rect(et.left + 2f * u, et.top + 4f * u, et.right - 2f * u, et.bottom - 2f * u), a.luz(P.TINTA, l).copy(alpha = 0.7f), 1451)
            // Las soldaduras de la lámina, arriba y abajo, y el brillo a lo largo.
            drawRect(a.luz(mezcla(LAMINA, P.ACERO, 0.5f), l), Offset(b.left + 1f * u, b.top + 2f * u), Size(b.width - 2f * u, 3f * u))
            drawRect(a.luz(mezcla(LAMINA, P.ACERO, 0.5f), l), Offset(b.left + 1f * u, b.bottom - 6f * u), Size(b.width - 2f * u, 3f * u))
            drawRect(Color.White.copy(alpha = if (a.oscuro) 0.15f else 0.5f), Offset(b.left + 4f * u, b.top + 6f * u), Size(2f * u, b.height - 14f * u))
            drawRoundRect(a.sombra(0.25f), b.topLeft, b.size, CornerRadius(5f * u), style = Stroke(0.8f * u))
            // La pajita con su pinza roja.
            val pj = Offset(b.center.x + 5f * u, b.top + 2f * u)
            drawLine(a.luz(P.PAPEL, l), pj, pj + Offset(2f * u, -15f * u), 2.6f * u, StrokeCap.Round)
            drawRoundRect(a.luz(P.ROJO, l), Offset(pj.x - 2.6f * u, pj.y - 11f * u), Size(8f * u, 5f * u), CornerRadius(1.5f * u))
            // La pinza de papelería que la aprieta por abajo, con sus dos asas de alambre sobre el pasamanos.
            drawRect(a.luz(P.GOMA, l), Offset(agarre.x - 9f * u, agarre.y - 7f * u), Size(18f * u, 8f * u))
            drawRect(a.claro(0.35f), Offset(agarre.x - 9f * u, agarre.y - 7f * u), Size(18f * u, 1.2f * u))
            drawLine(a.luz(P.ACERO, l), Offset(agarre.x - 6f * u, agarre.y), Offset(agarre.x - 4f * u, agarre.y + 9f * u), 1.3f * u)
            drawLine(a.luz(P.ACERO, l), Offset(agarre.x + 6f * u, agarre.y), Offset(agarre.x + 4f * u, agarre.y + 9f * u), 1.3f * u)
        }
    }

    /**
     * La barra imantada atornillada a la pared con lo que se pega en ella: una cuchara, un tenedor y las tijeras de abrir las
     * bolsas, cada uno donde lo dejaron, con su sombra en la tela.
     */
    fun DrawScope.iman(r: Rect, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(r.center)
        drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size, CornerRadius(2f * u))
        drawRoundRect(a.luz(P.GOMA, l), r.topLeft, r.size, CornerRadius(2f * u))
        drawRect(a.claro(0.3f), Offset(r.left + 2f * u, r.top + 1f * u), Size(r.width - 4f * u, 1f * u))
        listOf(r.left + 4f * u, r.right - 4f * u).forEach { x -> remacheEspacio(Offset(x, r.center.y), 1.4f * u, a.luz(P.ACERO, l), a) }
        cuchara(Offset(r.left + r.width * 0.2f, r.center.y + 2f * u), 64f, a, luz)
        tenedor(Offset(r.left + r.width * 0.43f, r.center.y + 1f * u), 112f, a, luz)
        tijeras(Offset(r.left + r.width * 0.74f, r.center.y + 3f * u), 48f, a, luz)
    }

    private fun metal(a: AireEstanteEspacio, l: LuzEstanteEspacio) = a.luz(mezcla(P.ACERO, P.GOMA, 0.38f), l)

    private fun DrawScope.cuchara(c: Offset, giro: Float, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(c)
        val d = luz.caida(c, 3f * u)
        rotate(giro, c) {
            translate(d.y, -d.x) {
                drawLine(a.sombra(0.3f), Offset(c.x - 22f * u, c.y), Offset(c.x + 8f * u, c.y), 3f * u, StrokeCap.Round)
                drawOval(a.sombra(0.3f), Offset(c.x + 6f * u, c.y - 4.5f * u), Size(13f * u, 9f * u))
            }
            drawLine(metal(a, l), Offset(c.x - 22f * u, c.y), Offset(c.x + 8f * u, c.y), 3f * u, StrokeCap.Round)
            drawOval(metal(a, l), Offset(c.x + 6f * u, c.y - 4.5f * u), Size(13f * u, 9f * u))
            drawOval(a.sombra(0.25f), Offset(c.x + 8f * u, c.y - 2.8f * u), Size(9f * u, 5.6f * u))
            drawLine(a.claro(0.6f), Offset(c.x - 20f * u, c.y - 0.8f * u), Offset(c.x + 6f * u, c.y - 0.8f * u), 0.7f * u)
        }
    }

    private fun DrawScope.tenedor(c: Offset, giro: Float, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(c)
        val d = luz.caida(c, 3f * u)
        rotate(giro, c) {
            trazo.reset()
            trazo.moveTo(c.x - 22f * u, c.y); trazo.lineTo(c.x + 7f * u, c.y)
            for (k in 0 until 4) { val y = c.y + (k - 1.5f) * 2f * u; trazo.moveTo(c.x + 7f * u, y); trazo.lineTo(c.x + 18f * u, y) }
            translate(d.y, -d.x) { drawPath(trazo, a.sombra(0.3f), style = Stroke(2.4f * u, cap = StrokeCap.Round)) }
            drawPath(trazo, metal(a, l), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawRect(metal(a, l), Offset(c.x + 5f * u, c.y - 4f * u), Size(4f * u, 8f * u))
            drawLine(a.claro(0.6f), Offset(c.x - 20f * u, c.y - 0.7f * u), Offset(c.x + 5f * u, c.y - 0.7f * u), 0.7f * u)
        }
    }

    private fun DrawScope.tijeras(c: Offset, giro: Float, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(c)
        val d = luz.caida(c, 3f * u)
        rotate(giro, c) {
            for (sombra in listOf(true, false)) {
                val o = if (sombra) Offset(d.y, -d.x) else Offset.Zero
                val hoja = if (sombra) a.sombra(0.3f) else metal(a, l)
                val ojo = if (sombra) a.sombra(0.3f) else a.luz(P.NARANJA, l)
                poligonoEnEspacio(trazo, c.x + o.x, c.y - 1.6f * u + o.y, c.x + 24f * u + o.x, c.y - 3f * u + o.y, c.x + 2f * u + o.x, c.y + 1.4f * u + o.y)
                drawPath(trazo, hoja)
                poligonoEnEspacio(trazo, c.x + o.x, c.y + 1.6f * u + o.y, c.x + 23f * u + o.x, c.y + 2.5f * u + o.y, c.x + 2f * u + o.x, c.y - 1.4f * u + o.y)
                drawPath(trazo, hoja)
                drawOval(ojo, Offset(c.x - 12f * u + o.x, c.y - 7.5f * u + o.y), Size(11f * u, 6.5f * u), style = Stroke(2.4f * u))
                drawOval(ojo, Offset(c.x - 12f * u + o.x, c.y + 1f * u + o.y), Size(11f * u, 6.5f * u), style = Stroke(2.4f * u))
            }
            drawLine(a.claro(0.6f), Offset(c.x + 3f * u, c.y - 2f * u), Offset(c.x + 20f * u, c.y - 3f * u), 0.6f * u)
            drawCircle(a.luz(P.GOMA, l), 1.4f * u, c)
        }
    }

    /** La placa remachada de la pared: la franja amarilla y negra de aviso y, debajo, renglones de signos y un pictograma. */
    fun DrawScope.placa(r: Rect, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(r.center)
        drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(1f * u, 1.4f * u), r.size, CornerRadius(2f * u))
        drawRoundRect(a.luz(mezcla(P.ACERO, P.PAPEL, 0.4f), l), r.topLeft, r.size, CornerRadius(2f * u))
        val banda = Rect(r.left + 3f * u, r.top + 3f * u, r.right - 3f * u, r.top + 8f * u)
        drawRect(a.luz(P.TRIGO, l), banda.topLeft, banda.size)
        clipRect(banda.left, banda.top, banda.right, banda.bottom) {
            trazo.reset()
            var x = banda.left - banda.height
            while (x < banda.right) {
                trazo.moveTo(x, banda.bottom); trazo.lineTo(x + banda.height, banda.top); trazo.lineTo(x + banda.height + 2.5f * u, banda.top); trazo.lineTo(x + 2.5f * u, banda.bottom); trazo.close()
                x += maxOf(1f, 5f * u)
            }
            drawPath(trazo, a.luz(P.TINTA, l))
        }
        val tinta = a.luz(P.TINTA, l).copy(alpha = 0.75f)
        signos(Rect(r.left + 3f * u, banda.bottom + 3f * u, r.right - 14f * u, r.bottom - 3f * u), tinta, 1461)
        val pc = Offset(r.right - 8f * u, banda.bottom + (r.bottom - banda.bottom) / 2f)
        drawCircle(tinta, 4f * u, pc, style = Stroke(1f * u))
        drawLine(tinta, pc + Offset(-2f * u, 0f), pc + Offset(2.4f * u, 0f), 1f * u)
        drawLine(tinta, pc + Offset(2.4f * u, 0f), pc + Offset(0.6f * u, -1.6f * u), 1f * u)
        listOf(Offset(r.left + 1.8f * u, r.top + 1.8f * u), Offset(r.right - 1.8f * u, r.top + 1.8f * u), Offset(r.left + 1.8f * u, r.bottom - 1.8f * u), Offset(r.right - 1.8f * u, r.bottom - 1.8f * u))
            .forEach { remacheEspacio(it, 0.9f * u, a.luz(P.ACERO, l), a) }
    }

    /** Una etiqueta chica pegada en el canto de la mesa, con su raya de color y sus signos. */
    fun DrawScope.etiqueta(r: Rect, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(r.center)
        drawRect(a.luz(P.PAPEL, l), r.topLeft, r.size)
        drawRect(a.luz(P.TELA_AZUL, l), r.topLeft, Size(4f * u, r.height))
        signos(Rect(r.left + 6f * u, r.top + 1.2f * u, r.right - 2f * u, r.bottom - 1.2f * u), a.luz(P.TINTA, l).copy(alpha = 0.7f), 1471)
    }

    /**
     * Renglones de signos inventados, impresos en [r]: rayas cortas y largas y puntos, como un código de inventario. No son
     * letras: no se leen, solo dicen «aquí hay algo escrito». Un solo camino.
     */
    private fun DrawScope.signos(r: Rect, color: Color, semilla: Int) {
        val u = density
        if (r.width < 4f * u || r.height < 2f * u) return
        val renglon = 3.2f * u
        trazo.reset()
        var y = r.top + 1f * u; var k = 0
        while (y < r.bottom - 0.5f * u) {
            var x = r.left
            while (x < r.right - 1f * u) {
                val l = if (azar(semilla, k) < 0.2f) 0.8f * u else (1.5f + 4f * azar(semilla + 1, k)) * u
                trazo.addRect(Rect(x, y, minOf(r.right, x + l), y + 1.1f * u))
                x += l + maxOf(0.5f, (1f + 1.2f * azar(semilla + 2, k)) * u); k++
            }
            y += maxOf(1f, renglon)
        }
        drawPath(trazo, color)
    }

    /**
     * La lámpara de pinza sujeta al pasamanos: la pinza naranja, el brazo corto y la cabeza que mira abajo a la izquierda, hacia
     * la carpeta. De día, apagada; en oscuro, encendida (su boca la pinta [MuebleLibretaEspacio] al final, encima de todo).
     */
    fun DrawScope.lampara(boca: Offset, pasa: Float, a: AireEstanteEspacio) {
        val u = density
        val encendida = a.oscuro
        val l = LuzEstanteEspacio(1f, 0f)
        val pinza = Offset(boca.x + 15f * u, pasa)
        drawRoundRect(a.sombra(0.3f), Offset(pinza.x - 6f * u + 2f * u, pinza.y - 8f * u + 2f * u), Size(12f * u, 16f * u), CornerRadius(2.5f * u))
        drawRoundRect(a.luz(P.NARANJA, l), Offset(pinza.x - 6f * u, pinza.y - 8f * u), Size(12f * u, 16f * u), CornerRadius(2.5f * u))
        drawRect(a.claro(0.4f), Offset(pinza.x - 5f * u, pinza.y - 7f * u), Size(1.2f * u, 14f * u))
        drawLine(a.luz(P.ACERO, l), pinza, boca + Offset(8f * u, -7f * u), 2.8f * u, StrokeCap.Round)
        rotate(140f, boca) {
            val r = Rect(boca.x - 18f * u, boca.y - 6f * u, boca.x, boca.y + 6f * u)
            drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(0f, -2f * u), r.size, CornerRadius(3f * u))
            drawRoundRect(a.luz(P.GOMA, l), r.topLeft, r.size, CornerRadius(3f * u))
            drawRect(a.claro(0.3f), Offset(r.left + 2f * u, r.bottom - 2f * u), Size(r.width - 4f * u, 1f * u))
            drawRect(a.luz(mezcla(P.ACERO, P.GOMA, 0.4f), l), Offset(r.left + 3f * u, r.top), Size(2f * u, r.height))
            drawOval(if (encendida) Color(0xFFFFEBC8) else a.luz(mezcla(P.LUZ_FRIA, P.ACERO, 0.45f), l), Offset(boca.x - 2.4f * u, boca.y - 6.4f * u), Size(4.8f * u, 12.8f * u))
        }
    }

    /** El piloto azul de guardia en la pared: apagado de día, encendido en oscuro. */
    fun DrawScope.piloto(c: Offset, a: AireEstanteEspacio) {
        val u = density
        drawRoundRect(a.sombra(0.35f), Offset(c.x - 3.5f * u, c.y - 5f * u), Size(9f * u, 12f * u), CornerRadius(2.5f * u))
        drawRoundRect(a.luz(P.GOMA, LuzEstanteEspacio(0.45f, 0f)), Offset(c.x - 4.5f * u, c.y - 6f * u), Size(9f * u, 12f * u), CornerRadius(2.5f * u))
        if (a.lumbre) drawCircle(mezcla(P.GUARDIA, Color.White, 0.5f), 2.6f * u, c)
        else {
            drawCircle(a.luz(mezcla(P.GUARDIA, P.TINTA, 0.45f), LuzEstanteEspacio(1f, 0f)), 2.6f * u, c)
            drawCircle(a.claro(0.5f), 0.9f * u, c - Offset(0.9f * u, 0.9f * u))
        }
    }

    // ---------------------------------------------------------------- en la mesa

    /**
     * La bolsa de tortillas (el pan de a bordo: no suelta migas), tumbada en la mesa bajo la goma: la lámina transparente con
     * su cierre de presión rojo y azul, el montón de tortillas con su canto y sus manchas de tostado, y su sombra.
     */
    fun DrawScope.tortillas(c: Offset, k: Float, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.en(c)
        val d = luz.caida(c, 4f * u)
        rotate(-8f, c) {
            val b = Rect(c.x - 38f * u * k, c.y - 24f * u * k, c.x + 38f * u * k, c.y + 24f * u * k)
            drawRoundRect(a.sombra(0.32f), b.topLeft + d, b.size, CornerRadius(6f * u))
            drawRoundRect(a.luz(LAMINA, l).copy(alpha = 0.7f), b.topLeft, b.size, CornerRadius(6f * u))
            // El montón: el canto (varias tortillas) y la de arriba con su tostado.
            val t = Rect(c.x - 31f * u * k, c.y - 17f * u * k, c.x + 29f * u * k, c.y + 15f * u * k)
            drawOval(a.luz(mezcla(TORTILLA, TOSTADO, 0.35f), l), t.topLeft + Offset(0f, 4f * u), t.size)
            drawOval(a.luz(TORTILLA, l), t.topLeft, t.size)
            for (k in 0 until 6) {
                val p = Offset(t.left + t.width * (0.2f + 0.6f * azar(1481, k)), t.top + t.height * (0.25f + 0.5f * azar(1482, k)))
                drawOval(a.luz(TOSTADO, l).copy(alpha = 0.6f), Offset(p.x, p.y), Size((2.5f + 3f * azar(1483, k)) * u, (1.6f + 1.4f * azar(1484, k)) * u))
            }
            drawArc(a.claro(0.35f), 200f, 90f, false, t.topLeft + Offset(3f * u, 2f * u), Size(t.width - 6f * u, t.height - 4f * u), style = Stroke(1.2f * u))
            // El cierre de presión, arriba, y los brillos de la lámina.
            drawLine(a.luz(P.ROJO, l), Offset(b.left + 4f * u, b.top + 4f * u), Offset(b.right - 4f * u, b.top + 4f * u), 1.4f * u)
            drawLine(a.luz(P.TELA_AZUL, l), Offset(b.left + 4f * u, b.top + 6f * u), Offset(b.right - 4f * u, b.top + 6f * u), 1.2f * u)
            drawLine(Color.White.copy(alpha = if (a.oscuro) 0.15f else 0.5f), Offset(b.left + 6f * u, b.bottom - 6f * u), Offset(b.left + b.width * 0.45f, b.top + 10f * u), 1.6f * u)
            drawRoundRect(a.sombra(0.2f), b.topLeft, b.size, CornerRadius(6f * u), style = Stroke(0.8f * u))
        }
    }

    /** Una gota de agua que se escapó de una pajita y flota: la esfera transparente con su brillo y su reflejo de abajo. */
    fun DrawScope.gota(c: Offset, r: Float, a: AireEstanteEspacio, luz: LuzLibretaEspacio) {
        val u = density
        val l = luz.real(c)
        drawCircle(a.luz(mezcla(P.LUZ_FRIA, P.TELA_AZUL, 0.3f), l).copy(alpha = 0.45f), r, c)
        drawCircle(a.claro(0.7f), r, c, style = Stroke(0.7f * u))
        drawCircle(Color.White.copy(alpha = if (a.oscuro) 0.4f else 0.85f), r * 0.3f, c - Offset(r * 0.35f, r * 0.35f))
        drawArc(a.sombra(0.3f), 20f, 120f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(0.8f * u))
    }
}
