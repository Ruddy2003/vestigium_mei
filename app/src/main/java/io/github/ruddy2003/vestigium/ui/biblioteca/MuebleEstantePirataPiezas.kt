package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay al pie del estante de Pirata ([MuebleEstantePirata]), sobre la cubierta y agrupado, unas cosas delante de otras
 * con su sombra en las tablas: a la izquierda, la botija de barro tumbada en su rodete (cortada por el borde) con el cabo
 * grueso adujado delante y el fanal de vidrio con su vela (de noche, la segunda luz); en medio, el balde de lona embreada con
 * su asa de cabo; a la derecha, la esquina del arcón de marinero con sus cantoneras de hierro y su gaza (cortado por el borde)
 * y, delante, el cesto de mimbre con cocos y limas.
 */
internal object PiezasEstantePirata {
    private val P = PaletaPirata

    fun DrawScope.alPie(a: AirePirata, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width
        val fondo = h - suelo
        if (fondo < 4f) return
        arcon(a, Offset(w * 1.0f, suelo + fondo * 0.5f), w, cerca)
        botija(a, Offset(w * 0.075f, suelo + fondo * 0.5f), w, cerca)
        adujada(a, Offset(w * 0.2f, suelo + fondo * 0.8f), w, cerca)
        balde(a, Offset(w * 0.53f, suelo + fondo * 0.86f), w, cerca)
        fanalPie(a, Offset(w * MuebleEstantePirata.FANAL_X, suelo + fondo * 0.92f), w, b, cerca)
        cesto(a, Offset(w * 0.88f, suelo + fondo * 0.98f), w, cerca)
    }

    /**
     * El cabo grueso adujado en plano sobre la cubierta: las vueltas concéntricas en escorzo, de fuera adentro, cada una con
     * su luz arriba, su sombra abajo y su torcido; el chicote que sale hacia la derecha.
     */
    private fun DrawScope.adujada(a: AirePirata, c: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(c)
        val rx = w * 0.095f; val ry = w * 0.036f
        val g = w * 0.013f
        contactoPirata(Offset(c.x + w * 0.01f, c.y + ry * 0.35f), rx * 2.25f, ry * 2.3f, a, 0.38f)
        // El chicote, que sale por debajo hacia la derecha.
        val chicote = Path().apply { moveTo(c.x + rx * 0.7f, c.y + ry * 0.6f); cubicTo(c.x + rx * 1.2f, c.y + ry * 1.2f, c.x + rx * 1.5f, c.y + ry * 0.4f, c.x + rx * 1.9f, c.y + ry * 1.1f) }
        drawPath(chicote, a.luz(P.CABO_OSC, k), style = Stroke(g, cap = StrokeCap.Round))
        drawPath(chicote, a.luz(P.CABO, k), style = Stroke(g * 0.55f, cap = StrokeCap.Round))
        val vueltas = 5
        for (j in 0 until vueltas) {
            val f = 1f - j * 0.17f
            val r = Rect(c.x - rx * f, c.y - ry * f - j * g * 0.12f, c.x + rx * f, c.y + ry * f - j * g * 0.12f)
            drawOval(a.luz(mezcla(P.CABO_OSC, P.BREA, 0.3f), k), Offset(r.left, r.top + g * 0.25f), r.size, style = Stroke(g))
            drawOval(a.luz(P.CABO, k), r.topLeft, r.size, style = Stroke(g * 0.62f))
            drawOval(a.luz(P.CABO_OSC, k).copy(alpha = 0.75f), r.topLeft, r.size, style = Stroke(g * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.35f, g * 0.7f))))
            drawArc(a.brillo(0.35f, k), 200f, 120f, false, Offset(r.left, r.top - g * 0.2f), r.size, style = Stroke(g * 0.22f))
        }
        drawOval(a.luz(P.BREA, k), Offset(c.x - rx * 0.12f, c.y - ry * 0.12f - g * 0.6f), Size(rx * 0.24f, ry * 0.24f))
    }

    /**
     * El balde de lona embreada (el de baldear y el de los incendios), más ancho arriba: el cuerpo por franjas redondas, la
     * costura a lo alto, una faja pintada de ocre, el borde con su vuelta de cabo, la boca honda y el asa de cabo con su gaza.
     */
    private fun DrawScope.balde(a: AirePirata, pie: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val al = w * 0.12f; val arriba = w * 0.05f; val abajo = w * 0.04f
        val k = cerca(pie - Offset(0f, al * 0.6f))
        val ha = if (a.oscuro) (if (pie.x < size.width * MuebleEstantePirata.FANAL_X) 1f else -1f) else -1f
        contactoPirata(Offset(pie.x + w * 0.012f, pie.y - 1f * u), abajo * 3.1f, w * 0.022f, a, 0.4f, hacia = -ha)
        val top = pie.y - al
        // El asa de cabo, detrás, arqueada por encima.
        val asa = Path().apply { moveTo(pie.x - arriba * 0.92f, top + al * 0.06f); cubicTo(pie.x - arriba * 0.8f, top - al * 0.62f, pie.x + arriba * 0.8f, top - al * 0.62f, pie.x + arriba * 0.92f, top + al * 0.06f) }
        drawPath(asa, a.luz(P.CABO_OSC, k), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(asa, a.luz(P.CABO, k), style = Stroke(w * 0.004f, cap = StrokeCap.Round))
        val cuerpo = poligonoPirata(pie.x - arriba, top, pie.x + arriba, top, pie.x + abajo, pie.y, pie.x - abajo, pie.y)
        val lona = Color(0xFF3A3A30)
        drawPath(cuerpo, a.luz(lona, k))
        clipPath(cuerpo) {
            val r = Rect(pie.x - arriba, top, pie.x + arriba, pie.y)
            cilindroPirata(r, a, k, ha, 1.3f)
            // La faja pintada de ocre y la costura a lo alto.
            drawRect(a.luz(Color(0xFFB8862E), k), Offset(r.left, top + al * 0.42f), Size(r.width, al * 0.16f))
            drawRect(a.sombra(0.25f), Offset(r.left, top + al * 0.55f), Size(r.width, al * 0.03f))
            drawLine(a.luz(P.CABO, k).copy(alpha = 0.6f), Offset(pie.x + arriba * 0.35f, top), Offset(pie.x + abajo * 0.35f, pie.y), 0.9f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 2f * u)))
            drawRect(a.sombra(0.3f), Offset(r.left, pie.y - al * 0.1f), Size(r.width, al * 0.1f))
        }
        // La boca y la vuelta de cabo del borde.
        drawOval(a.luz(Color(0xFF1A1612), k), Offset(pie.x - arriba, top - al * 0.07f), Size(arriba * 2, al * 0.16f))
        drawOval(a.luz(P.CABO_OSC, k), Offset(pie.x - arriba * 1.04f, top - al * 0.08f), Size(arriba * 2.08f, al * 0.18f), style = Stroke(w * 0.009f))
        drawOval(a.luz(P.CABO, k), Offset(pie.x - arriba * 1.04f, top - al * 0.09f), Size(arriba * 2.08f, al * 0.18f), style = Stroke(w * 0.0045f))
        listOf(-1f, 1f).forEach { s -> drawCircle(a.luz(P.CABO_OSC, k), w * 0.006f, Offset(pie.x + s * arriba * 0.92f, top + al * 0.06f)) }
    }

    /**
     * La esquina del arcón de marinero, de roble sin pintar y gastado, cortada por el borde de la derecha: la tapa con su canto
     * y su moldura, el frente de tablas, el costado en escorzo con su gaza de cabo en su tojino, y las cantoneras de hierro con
     * sus clavos.
     */
    private fun DrawScope.arcon(a: AirePirata, pieDer: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val al = w * 0.19f; val x0 = w * 0.7f
        val k = cerca(Offset(x0 + w * 0.1f, pieDer.y - al * 0.5f))
        val top = pieDer.y - al
        val lado = w * 0.035f
        contactoPirata(Offset(x0 + w * 0.12f, pieDer.y), w * 0.36f, w * 0.03f, a, 0.42f)
        val roble = Color(0xFF7E5A38)
        // El costado en escorzo, a la izquierda, y su gaza de cabo.
        drawPath(poligonoPirata(x0 - lado, top - al * 0.08f, x0, top, x0, pieDer.y, x0 - lado, pieDer.y - al * 0.05f), a.luz(mezcla(roble, P.BREA, 0.35f), k))
        drawRect(a.luz(P.ROBLE_OSC, k), Offset(x0 - lado * 0.7f, top + al * 0.35f), Size(lado * 0.4f, al * 0.12f))
        drawArc(a.luz(P.CABO, k), 70f, 220f, false, Offset(x0 - lado * 1.1f, top + al * 0.38f), Size(lado * 0.9f, al * 0.4f), style = Stroke(w * 0.006f))
        // El frente de tablas.
        drawRect(a.luz(roble, k), Offset(x0, top), Size(w - x0 + 2f, al))
        val juntas = Path()
        for (j in 1..2) { val y = top + al * j / 3f; juntas.moveTo(x0, y); juntas.lineTo(w, y) }
        drawPath(juntas, a.sombra(0.4f), style = Stroke(1.4f * u))
        val veta = Path()
        for (j in 0 until 6) { val y = top + al * (0.08f + 0.15f * j); veta.moveTo(x0, y); veta.cubicTo(x0 + w * 0.08f, y + 2f * u, x0 + w * 0.16f, y - 2f * u, w, y + 1f * u) }
        drawPath(veta, a.sombra(0.16f), style = Stroke(0.8f * u))
        drawRect(a.brillo(0.22f, k), Offset(x0, top), Size(w * 0.02f, al))
        // La tapa: su cara de arriba en escorzo y su canto con moldura.
        drawPath(poligonoPirata(x0 - lado * 0.6f, top - al * 0.2f, w + 2f, top - al * 0.2f, w + 2f, top - al * 0.06f, x0 - lado * 0.6f, top - al * 0.06f), a.luz(mezcla(roble, P.ROBLE_LUZ, 0.35f), k))
        drawRect(a.luz(mezcla(roble, P.BREA, 0.2f), k), Offset(x0 - lado * 0.6f, top - al * 0.06f), Size(w - x0 + lado, al * 0.09f))
        drawRect(a.brillo(0.3f, k), Offset(x0 - lado * 0.6f, top - al * 0.06f), Size(w - x0 + lado, 1.2f * u))
        drawRect(a.sombra(0.4f), Offset(x0, top + al * 0.03f), Size(w - x0, al * 0.04f))
        // Las cantoneras de hierro de la esquina, con sus clavos.
        val hierro = a.luz(P.HIERRO, k)
        drawRect(hierro, Offset(x0, top + al * 0.03f), Size(w * 0.035f, al * 0.97f))
        drawRect(hierro, Offset(x0, top + al * 0.03f), Size(w * 0.1f, al * 0.1f))
        drawRect(hierro, Offset(x0, pieDer.y - al * 0.1f), Size(w * 0.1f, al * 0.1f))
        drawRect(a.brillo(0.3f, k), Offset(x0, top + al * 0.03f), Size(1.2f * u, al * 0.97f))
        listOf(0.12f, 0.5f, 0.86f).forEach { f -> remachePirata(Offset(x0 + w * 0.017f, top + al * f), 1.6f * u, a, k) }
        listOf(0.06f, 0.085f).forEach { dx -> remachePirata(Offset(x0 + w * dx, top + al * 0.08f), 1.4f * u, a, k); remachePirata(Offset(x0 + w * dx, pieDer.y - al * 0.05f), 1.4f * u, a, k) }
    }

    /**
     * La botija de barro (la tinaja de aceite de los barcos de las Antillas), tumbada en su rodete de cabo: la panza de huevo
     * con sus anillos de torno, la luz a lo largo de su lomo, el cuello corto con su labio grueso, el vidriado verde que asoma
     * por la boca y chorrea un poco, y la sombra en la cubierta. La corta el borde de la izquierda.
     */
    private fun DrawScope.botija(a: AirePirata, c: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(c)
        val largo = w * 0.31f; val r = w * 0.078f
        contactoPirata(Offset(c.x + w * 0.03f, c.y + r * 0.95f), largo * 1.15f, r * 0.42f, a, 0.42f)
        // El rodete de cabo bajo la panza: dos vueltas con su torcido.
        val ro = Rect(c.x - largo * 0.28f, c.y + r * 0.55f, c.x + largo * 0.22f, c.y + r * 1.05f)
        drawOval(a.luz(P.CABO_OSC, k), ro.topLeft, ro.size)
        drawOval(a.luz(P.CABO, k), ro.deflate(1.6f * u).topLeft, ro.deflate(1.6f * u).size, style = Stroke(3f * u))
        drawOval(a.luz(P.CABO_OSC, k), ro.deflate(1.6f * u).topLeft, ro.deflate(1.6f * u).size, style = Stroke(3f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2.4f * u))))
        rotate(-10f, c) {
            // La panza de huevo: ancha en el hombro (hacia la boca, a la derecha) y redonda abajo.
            val x0 = c.x - largo * 0.55f; val x1 = c.x + largo * 0.38f
            val panza = Path().apply {
                moveTo(x1, c.y - r * 0.38f)
                cubicTo(x1 - largo * 0.05f, c.y - r * 1.05f, c.x - largo * 0.1f, c.y - r * 1.02f, x0 + largo * 0.12f, c.y - r * 0.78f)
                cubicTo(x0 - largo * 0.02f, c.y - r * 0.5f, x0 - largo * 0.02f, c.y + r * 0.5f, x0 + largo * 0.12f, c.y + r * 0.78f)
                cubicTo(c.x - largo * 0.1f, c.y + r * 1.02f, x1 - largo * 0.05f, c.y + r * 1.05f, x1, c.y + r * 0.38f)
                close()
            }
            drawPath(panza, a.luz(P.BARRO, k))
            clipPath(panza) {
                // Los anillos del torno, de lado a lado.
                val anillos = Path()
                for (j in 0 until 8) {
                    val x = x0 + (x1 - x0) * (0.12f + 0.11f * j)
                    anillos.moveTo(x, c.y - r * 1.1f); anillos.quadraticTo(x + r * 0.18f, c.y, x, c.y + r * 1.1f)
                }
                drawPath(anillos, a.luz(Color(0xFF8A4A26), k).copy(alpha = 0.35f), style = Stroke(1.3f * u))
                // La luz a lo largo del lomo y la sombra de abajo.
                drawOval(a.brillo(0.3f, k), Offset(x0 + largo * 0.1f, c.y - r * 0.85f), Size(largo * 0.7f, r * 0.5f))
                drawOval(a.brillo(0.4f, k), Offset(x0 + largo * 0.25f, c.y - r * 0.75f), Size(largo * 0.35f, r * 0.18f))
                drawRect(a.sombra(0.34f), Offset(x0, c.y + r * 0.3f), Size(largo, r))
                drawRect(a.sombra(0.18f), Offset(x0, c.y), Size(largo, r * 0.3f))
                // Una chorrera del vidriado y una mancha de aceite.
                drawLine(a.luz(P.VIDRIADO, k).copy(alpha = 0.85f), Offset(x1 - largo * 0.04f, c.y - r * 0.4f), Offset(x1 - largo * 0.16f, c.y - r * 0.15f), 2.6f * u, StrokeCap.Round)
                drawOval(a.luz(Color(0xFF7A3E1E), k).copy(alpha = 0.4f), Offset(c.x - largo * 0.2f, c.y + r * 0.05f), Size(largo * 0.2f, r * 0.35f))
            }
            // El cuello corto y el labio grueso, con el vidriado verde que asoma por la boca.
            val cu = Rect(x1 - 1f * u, c.y - r * 0.32f, x1 + largo * 0.07f, c.y + r * 0.32f)
            drawRect(a.luz(P.BARRO, k), cu.topLeft, cu.size)
            drawRect(a.sombra(0.3f), Offset(cu.left, c.y + r * 0.1f), Size(cu.width, r * 0.22f))
            val labio = Rect(cu.right - largo * 0.012f, c.y - r * 0.46f, cu.right + largo * 0.05f, c.y + r * 0.46f)
            drawOval(a.luz(mezcla(P.BARRO, Color(0xFF6A3418), 0.3f), k), labio.topLeft, labio.size)
            drawOval(a.luz(P.VIDRIADO, k), labio.deflate(labio.width * 0.18f).topLeft, labio.deflate(labio.width * 0.18f).size)
            drawOval(a.luz(Color(0xFF1E140C), k), Offset(labio.center.x - labio.width * 0.12f, c.y - r * 0.2f), Size(labio.width * 0.3f, r * 0.4f))
            drawArc(a.brillo(0.45f, k), 200f, 120f, false, labio.topLeft, labio.size, style = Stroke(1.2f * u))
            destelloPirata(Offset(c.x - largo * 0.05f, c.y - r * 0.62f), 2.2f * u, a, k)
        }
    }

    /** El fanal: la vela de sebo bajo su campana de vidrio, sobre un pie de latón; de noche, su llama y su charco en la cubierta. */
    private fun DrawScope.fanalPie(a: AirePirata, pie: Offset, w: Float, b: Rect, cerca: (Offset) -> Float) {
        val u = density
        val k = if (a.lumbre) 1f else cerca(pie)
        val an = w * 0.064f; val al = w * 0.13f
        val llama = Offset(pie.x, b.top + b.height * MuebleEstantePirata.FANAL_Y)
        if (a.lumbre) {
            val f = if (a.oscuro) 1f else 0.6f
            mancha(Offset(pie.x, pie.y), w * 0.17f, P.LLAMA.copy(alpha = 0.24f * f))
            mancha(llama, w * 0.12f, P.LLAMA.copy(alpha = 0.3f * f))
        }
        contactoPirata(Offset(pie.x, pie.y - 1f * u), an * 2f, 6f * u, a)
        // El pie de latón.
        drawOval(a.luz(P.LATON_OSC, k), Offset(pie.x - an * 0.7f, pie.y - an * 0.22f), Size(an * 1.4f, an * 0.34f))
        drawRect(a.luz(P.LATON, k), Offset(pie.x - an * 0.6f, pie.y - an * 0.34f), Size(an * 1.2f, an * 0.2f))
        drawOval(a.luz(P.LATON, k), Offset(pie.x - an * 0.6f, pie.y - an * 0.42f), Size(an * 1.2f, an * 0.18f))
        // La vela.
        val vela = Rect(pie.x - an * 0.13f, llama.y + w * 0.008f, pie.x + an * 0.13f, pie.y - an * 0.34f)
        drawRect(a.dia(Color(0xFFF1E8CE), 0f, k), vela.topLeft, vela.size)
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.4f, vela.top), Size(vela.width * 0.4f, vela.height))
        drawLine(a.luz(P.TINTA, k), Offset(pie.x, vela.top), Offset(pie.x, vela.top - w * 0.004f), 1f * u)
        // La campana de vidrio.
        val top = pie.y - an * 0.36f - al
        val campana = Path().apply {
            moveTo(pie.x - an * 0.46f, pie.y - an * 0.38f)
            cubicTo(pie.x - an * 0.7f, pie.y - al * 0.45f, pie.x - an * 0.58f, top + al * 0.3f, pie.x - an * 0.32f, top)
            lineTo(pie.x + an * 0.32f, top)
            cubicTo(pie.x + an * 0.58f, top + al * 0.3f, pie.x + an * 0.7f, pie.y - al * 0.45f, pie.x + an * 0.46f, pie.y - an * 0.38f)
            close()
        }
        if (a.lumbre) {
            val fl = Path().apply {
                moveTo(llama.x, llama.y - w * 0.02f)
                quadraticTo(llama.x + w * 0.007f, llama.y - w * 0.004f, llama.x, llama.y + w * 0.006f)
                quadraticTo(llama.x - w * 0.007f, llama.y - w * 0.004f, llama.x, llama.y - w * 0.02f); close()
            }
            drawPath(campana, P.FUEGO.copy(alpha = if (a.oscuro) 0.2f else 0.12f))
            drawPath(fl, P.LLAMA)
            drawOval(P.FUEGO, Offset(llama.x - w * 0.0025f, llama.y - w * 0.009f), Size(w * 0.005f, w * 0.011f))
        } else drawPath(campana, a.claro(0.16f))
        drawPath(campana, a.luz(P.VIDRIO, k).copy(alpha = 0.7f), style = Stroke(1.2f * u))
        drawLine(Color.White.copy(alpha = 0.6f), Offset(pie.x - an * 0.4f, pie.y - al * 0.5f), Offset(pie.x - an * 0.3f, top + al * 0.15f), 1.6f * u, StrokeCap.Round)
        drawOval(a.luz(P.LATON, k), Offset(pie.x - an * 0.34f, top - an * 0.05f), Size(an * 0.68f, an * 0.12f), style = Stroke(1.4f * u))
    }

    /**
     * El cesto de mimbre con cocos y limas: el tejido de mimbre con su trenza en el borde, los cocos con su fibra
     * y unas limas encima. Lo corta el borde de la derecha.
     */
    private fun DrawScope.cesto(a: AirePirata, pie: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val an = w * 0.26f; val al = w * 0.085f
        val k = cerca(pie - Offset(0f, al))
        contactoPirata(Offset(pie.x - an * 0.05f, pie.y - 1f * u), an * 1.1f, al * 0.18f, a, 0.4f)
        // Lo de dentro, que asoma: tres cocos y dos limas.
        val coco = P.COCO
        listOf(Offset(-0.26f, -1.05f) to 0.09f, Offset(0.0f, -1.18f) to 0.1f, Offset(0.26f, -1.08f) to 0.085f).forEach { (o, s) ->
            val c = pie + Offset(o.x * an, o.y * al)
            val r = w * s * 0.55f
            drawCircle(a.luz(coco, k), r, c)
            val pelo = Path()
            for (j in 0 until 10) { val ang = j * 0.63f; pelo.moveTo(c.x + kotlin.math.cos(ang) * r * 0.5f, c.y + kotlin.math.sin(ang) * r * 0.5f); pelo.lineTo(c.x + kotlin.math.cos(ang + 0.3f) * r * 0.95f, c.y + kotlin.math.sin(ang + 0.3f) * r * 0.95f) }
            drawPath(pelo, a.luz(Color(0xFF8E6A44), k).copy(alpha = 0.6f), style = Stroke(0.8f * u))
            drawCircle(a.brillo(0.2f, k), r * 0.35f, c + Offset(-r * 0.35f, -r * 0.35f))
            drawOval(a.luz(Color(0xFF4A2E18), k).copy(alpha = 0.6f), Offset(c.x + r * 0.15f, c.y + r * 0.1f), Size(r * 0.5f, r * 0.3f))
        }
        listOf(Offset(-0.12f, -0.98f), Offset(0.13f, -1.0f)).forEach { o ->
            val c = pie + Offset(o.x * an, o.y * al)
            val r = w * 0.026f
            drawCircle(a.luz(P.LIMA, k), r, c)
            drawCircle(a.luz(P.LIMA_OSC, k).copy(alpha = 0.6f), r * 0.7f, c + Offset(r * 0.2f, r * 0.3f))
            drawCircle(a.brillo(0.6f, k), r * 0.22f, c + Offset(-r * 0.35f, -r * 0.4f))
        }
        // El cesto.
        val cuerpo = poligonoPirata(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y - al, pie.x + an * 0.44f, pie.y, pie.x - an * 0.44f, pie.y)
        drawPath(cuerpo, a.luz(P.MIMBRE, k))
        clipPath(cuerpo) {
            val trama = Path(); val luces = Path()
            var y = pie.y - al
            var fila = 0
            while (y < pie.y) {
                var x = pie.x - an / 2 + (if (fila % 2 == 0) 0f else an * 0.03f)
                while (x < pie.x + an / 2) { trama.moveTo(x, y); trama.quadraticTo(x + an * 0.03f, y + al * 0.12f, x + an * 0.06f, y); luces.moveTo(x + an * 0.01f, y + al * 0.03f); luces.lineTo(x + an * 0.04f, y + al * 0.03f); x += an * 0.06f }
                y += al * 0.14f; fila++
            }
            drawPath(trama, a.luz(Color(0xFF8A6430), k), style = Stroke(1.1f * u))
            drawPath(luces, a.brillo(0.35f, k), style = Stroke(0.9f * u))
            for (j in 0 until 6) drawLine(a.luz(Color(0xFF7A5428), k), Offset(pie.x - an / 2 + an * j / 5f, pie.y - al), Offset(pie.x - an * 0.44f + an * 0.88f * j / 5f, pie.y), 1.4f * u)
            drawRect(a.sombra(0.25f), Offset(pie.x - an / 2, pie.y - al * 0.4f), Size(an, al * 0.4f))
            drawRect(a.sombra(0.2f), Offset(pie.x + an * 0.1f, pie.y - al), Size(an * 0.4f, al))
        }
        // La trenza del borde.
        val borde = Rect(pie.x - an * 0.52f, pie.y - al - al * 0.1f, pie.x + an * 0.52f, pie.y - al + al * 0.08f)
        drawRoundRect(a.luz(Color(0xFF9A7038), k), borde.topLeft, borde.size, CornerRadius(al * 0.08f))
        val trenza = Path()
        var x = borde.left
        while (x < borde.right) { trenza.moveTo(x, borde.bottom); trenza.lineTo(x + al * 0.1f, borde.top); x += al * 0.08f }
        drawPath(trenza, a.luz(P.MIMBRE, k), style = Stroke(1.6f * u))
    }
}
