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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay en los huecos de las repisas y al pie del estante de Pirata ([MuebleEstantePirata]). En los huecos, según la
 * repisa: el cubilete de cuero con dos dados, la pipa de barro sobre el andullo de tabaco, el legajo de cartas atado con su
 * lacre, la palmatoria con su cabo de vela y la bolsa con monedas lisas. Al pie, sobre la cubierta: la botija de barro tumbada
 * en su rodete de cabo (cortada por la izquierda), el fanal de vidrio con su vela (de noche, la segunda luz), una argolla de
 * la cubierta y el cesto de mimbre con cocos y limas (cortado por la derecha).
 */
internal object PiezasEstantePirata {
    private val P = PaletaPirata

    // ---------------------------------------------------------------- los huecos

    /** Lo que se deja en el hueco libre de la repisa [k], de [oc] (hasta donde llegan los volúmenes) a [xDer] (el montante). */
    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, xDer: Float, a: AirePirata, kz: Float) {
        val w = size.width; val u = density
        val x0 = oc + w * 0.02f; val x1 = xDer - w * 0.035f
        val libre = x1 - x0
        if (libre < w * 0.1f) return
        val h = r.height
        val piso = r.bottom
        val cual = Math.floorMod(k * 3 + 1, 5)
        val ha = if (a.oscuro) 1f else -1f
        clipRect(oc, r.top, xDer, r.bottom) {
            val c = x0 + libre * (if (libre > w * 0.3f) 0.32f else 0.5f)
            pieza(cual, Offset(c, piso), h, a, kz, ha)
            if (libre > w * 0.34f) pieza(Math.floorMod(cual + 2, 5), Offset(x0 + libre * 0.76f, piso), h, a, kz, ha)
        }
    }

    private fun DrawScope.pieza(cual: Int, pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) = when (cual) {
        0 -> cubilete(pie, h, a, k, ha)
        1 -> pipa(pie, h, a, k, ha)
        2 -> legajo(pie, h, a, k, ha)
        3 -> palmatoria(pie, h, a, k, ha)
        else -> bolsa(pie, h, a, k, ha)
    }

    /** El cubilete de cuero de pie, con su costura y su borde, y dos dados delante (caras de uno, dos y tres puntos). */
    private fun DrawScope.cubilete(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val an = h * 0.32f; val al = h * 0.42f
        val x = pie.x - an * 0.6f
        contactoPirata(Offset(x, pie.y - 1f * u), an * 1.3f, 5f * u, a, hacia = -ha)
        val cuerpo = poligonoPirata(x - an * 0.5f, pie.y - al, x + an * 0.5f, pie.y - al, x + an * 0.44f, pie.y, x - an * 0.44f, pie.y)
        val cuero = Color(0xFF7A4426)
        drawPath(cuerpo, a.luz(cuero, k))
        clipPath(cuerpo) {
            cilindroPirata(Rect(x - an * 0.5f, pie.y - al, x + an * 0.5f, pie.y), a, k, ha, 1.2f)
            drawLine(a.luz(P.CABO, k).copy(alpha = 0.7f), Offset(x + an * 0.1f, pie.y - al), Offset(x + an * 0.08f, pie.y), 0.9f * u)
        }
        drawRect(a.luz(mezcla(cuero, P.TINTA, 0.4f), k), Offset(x - an * 0.52f, pie.y - al), Size(an * 1.04f, al * 0.1f))
        drawOval(a.luz(mezcla(cuero, P.TINTA, 0.6f), k), Offset(x - an * 0.48f, pie.y - al - an * 0.08f), Size(an * 0.96f, an * 0.18f))
        // Los dados.
        listOf(Triple(x + an * 0.95f, 0, 0.36f), Triple(x + an * 1.6f, 1, 0.3f)).forEach { (dx, i, s) ->
            dado(Offset(dx, pie.y), h * s * 0.45f, i, a, k)
        }
    }

    /** Un dado de hueso en tres cuartos: la cara de arriba y la de delante con sus puntos (uno, dos o tres, nunca cinco). */
    private fun DrawScope.dado(pie: Offset, l: Float, i: Int, a: AirePirata, k: Float) {
        val u = density
        val hueso = Color(0xFFEDE3C8)
        contactoPirata(Offset(pie.x, pie.y - 0.5f * u), l * 1.6f, 3f * u, a)
        drawRect(a.dia(hueso, -0.08f, k), Offset(pie.x - l / 2, pie.y - l), Size(l, l))
        drawPath(poligonoPirata(pie.x - l / 2, pie.y - l, pie.x + l / 2, pie.y - l, pie.x + l * 0.3f, pie.y - l * 1.35f, pie.x - l * 0.7f, pie.y - l * 1.35f), a.dia(hueso, 0.2f, k))
        drawRect(a.sombra(0.3f), Offset(pie.x - l / 2, pie.y - l), Size(l, l), style = Stroke(0.6f * u))
        val punto = a.luz(P.TINTA, k)
        val rp = l * 0.1f
        if (i == 0) {
            drawCircle(punto, rp, Offset(pie.x - l * 0.22f, pie.y - l * 0.75f)); drawCircle(punto, rp, Offset(pie.x, pie.y - l * 0.5f)); drawCircle(punto, rp, Offset(pie.x + l * 0.22f, pie.y - l * 0.25f))
            drawCircle(punto, rp, Offset(pie.x - l * 0.1f, pie.y - l * 1.18f))
        } else {
            drawCircle(punto, rp, Offset(pie.x - l * 0.2f, pie.y - l * 0.72f)); drawCircle(punto, rp, Offset(pie.x + l * 0.2f, pie.y - l * 0.28f))
            drawCircle(punto, rp, Offset(pie.x - l * 0.3f, pie.y - l * 1.12f)); drawCircle(punto, rp, Offset(pie.x + l * 0.05f, pie.y - l * 1.24f)); drawCircle(punto, rp, Offset(pie.x - l * 0.12f, pie.y - l * 1.18f))
        }
    }

    /** La pipa de barro blanco, larga, apoyada en el andullo (el rollo de tabaco torcido y atado). */
    private fun DrawScope.pipa(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val l = h * 0.9f
        val rr = h * 0.11f
        val c = Offset(pie.x, pie.y - rr)
        contactoPirata(Offset(pie.x, pie.y - 1f * u), l * 0.8f, 5f * u, a, hacia = -ha)
        // El andullo: el rollo torcido con sus vueltas de cordel.
        val andullo = Rect(c.x - l * 0.38f, c.y - rr, c.x + l * 0.38f, c.y + rr)
        drawRoundRect(a.luz(Color(0xFF6A4020), k), andullo.topLeft, andullo.size, CornerRadius(rr))
        val torcido = Path()
        var x = andullo.left + rr * 0.5f
        while (x < andullo.right - rr * 0.6f) { torcido.moveTo(x, andullo.bottom - 1f * u); torcido.lineTo(x + rr * 0.7f, andullo.top + 1f * u); x += rr * 0.55f }
        drawPath(torcido, a.luz(Color(0xFF3E2410), k), style = Stroke(1f * u))
        drawRoundRect(a.brillo(0.2f, k), Offset(andullo.left + rr * 0.4f, andullo.top + rr * 0.3f), Size(andullo.width - rr * 0.8f, rr * 0.35f), CornerRadius(rr * 0.2f))
        listOf(0.25f, 0.75f).forEach { f -> drawLine(a.luz(P.CABO, k), Offset(andullo.left + andullo.width * f, andullo.top), Offset(andullo.left + andullo.width * f, andullo.bottom), 1.6f * u) }
        // La pipa: la caña y la cazoleta.
        val barro = Color(0xFFE8E0D0)
        val cz = Offset(c.x + l * 0.32f, c.y - rr * 1.1f)
        drawLine(a.sombra(0.3f), Offset(c.x - l * 0.48f, c.y - rr * 0.6f), cz + Offset(1.5f * u, 2f * u), h * 0.022f, StrokeCap.Round)
        drawLine(a.dia(barro, 0f, k), Offset(c.x - l * 0.5f, c.y - rr * 0.9f), cz, h * 0.02f, StrokeCap.Round)
        val cazoleta = poligonoPirata(cz.x - h * 0.035f, cz.y - h * 0.11f, cz.x + h * 0.035f, cz.y - h * 0.11f, cz.x + h * 0.022f, cz.y + h * 0.01f, cz.x - h * 0.02f, cz.y + h * 0.01f)
        drawPath(cazoleta, a.dia(barro, 0f, k))
        drawRect(a.sombra(0.25f), Offset(cz.x + h * 0.005f, cz.y - h * 0.11f), Size(h * 0.03f, h * 0.12f))
        drawOval(a.luz(Color(0xFF3A2A20), k), Offset(cz.x - h * 0.032f, cz.y - h * 0.12f), Size(h * 0.064f, h * 0.022f))
    }

    /** El legajo de cartas dobladas atado con cinta, de canto contra el fondo, con el lacre roto de la de delante. */
    private fun DrawScope.legajo(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val an = h * 0.5f; val al = h * 0.48f
        contactoPirata(Offset(pie.x, pie.y - 1f * u), an * 1.1f, 5f * u, a, hacia = -ha)
        for (j in 0 until 4) {
            val dx = (j - 1.5f) * an * 0.05f; val dy = j * al * 0.05f
            val hoja = Rect(pie.x - an / 2 + dx, pie.y - al + dy, pie.x + an / 2 + dx, pie.y)
            drawRect(a.dia(mezcla(P.PAPEL, P.PAPEL_OSC, 0.15f + 0.2f * azar(551, j)), 0f, k), hoja.topLeft, hoja.size)
            drawRect(a.sombra(0.25f), hoja.topLeft, hoja.size, style = Stroke(0.6f * u))
        }
        val f = Rect(pie.x - an / 2 + an * 0.075f, pie.y - al + al * 0.15f, pie.x + an / 2 + an * 0.075f, pie.y)
        // El doblez de la carta y los renglones que se transparentan (trazos, sin letras).
        drawLine(a.sombra(0.25f), Offset(f.left, f.top + f.height * 0.4f), Offset(f.right, f.top + f.height * 0.4f), 0.8f * u)
        for (j in 0 until 3) drawLine(a.luz(P.TINTA, k).copy(alpha = 0.2f), Offset(f.left + f.width * 0.15f, f.top + f.height * (0.58f + 0.12f * j)), Offset(f.left + f.width * (0.5f + 0.3f * azar(552, j)), f.top + f.height * (0.58f + 0.12f * j)), 0.7f * u)
        // La cinta cruzada y el lacre.
        drawLine(a.luz(Color(0xFF2E4A6A), k), Offset(f.left - an * 0.1f, f.center.y), Offset(f.right, f.center.y), 2.2f * u)
        drawLine(a.luz(Color(0xFF2E4A6A), k), Offset(f.center.x, f.top - al * 0.2f), Offset(f.center.x, f.bottom), 2.2f * u)
        drawCircle(a.luz(P.LACRE, k), h * 0.05f, Offset(f.center.x, f.center.y))
        drawCircle(a.luz(mezcla(P.LACRE, P.TINTA, 0.4f), k), h * 0.03f, Offset(f.center.x, f.center.y), style = Stroke(0.8f * u))
        drawCircle(a.brillo(0.4f, k), h * 0.012f, Offset(f.center.x - h * 0.016f, f.center.y - h * 0.016f))
    }

    /** La palmatoria de latón con su asa de anillo y un cabo de vela de sebo, apagado. */
    private fun DrawScope.palmatoria(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val r = h * 0.2f
        contactoPirata(Offset(pie.x, pie.y - 1f * u), r * 2.4f, 5f * u, a, hacia = -ha)
        drawOval(a.luz(P.LATON_OSC, k), Offset(pie.x - r, pie.y - r * 0.32f), Size(r * 2, r * 0.36f))
        drawOval(a.luz(P.LATON, k), Offset(pie.x - r, pie.y - r * 0.42f), Size(r * 2, r * 0.34f))
        drawArc(a.luz(P.LATON, k), 200f, 300f, false, Offset(pie.x + r * 0.8f, pie.y - r * 0.6f), Size(r * 0.55f, r * 0.5f), style = Stroke(1.6f * u))
        val vela = Rect(pie.x - r * 0.18f, pie.y - h * 0.4f, pie.x + r * 0.18f, pie.y - r * 0.32f)
        drawRect(a.luz(P.LATON, k), Offset(vela.left - r * 0.08f, vela.bottom - r * 0.25f), Size(vela.width + r * 0.16f, r * 0.25f))
        drawRect(a.dia(Color(0xFFEDE2C2), 0f, k), vela.topLeft, Size(vela.width, vela.height - r * 0.25f))
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height - r * 0.25f))
        drawLine(a.luz(P.TINTA, k), Offset(vela.center.x, vela.top), Offset(vela.center.x + 1f * u, vela.top - r * 0.2f), 1f * u)
        // Un goterón de sebo.
        drawLine(a.dia(Color(0xFFEDE2C2), 0.1f, k), Offset(vela.left + 0.5f * u, vela.top + 2f * u), Offset(vela.left + 0.5f * u, vela.top + vela.height * 0.45f), 1.6f * u, StrokeCap.Round)
        destelloPirata(Offset(pie.x - r * 0.5f, pie.y - r * 0.3f), 1.2f * u, a, k)
    }

    /** La bolsa de cuero con su jareta y unas monedas lisas que se salieron (sin cuño). */
    private fun DrawScope.bolsa(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val an = h * 0.36f; val al = h * 0.4f
        contactoPirata(Offset(pie.x, pie.y - 1f * u), an * 1.4f, 5f * u, a, hacia = -ha)
        val cuero = Color(0xFF8A5A30)
        val cuello = pie.y - al * 0.7f
        // La panza redonda, apoyada y algo aplastada abajo.
        val panza = Path().apply {
            moveTo(pie.x - an * 0.16f, cuello)
            cubicTo(pie.x - an * 0.62f, cuello + al * 0.1f, pie.x - an * 0.62f, pie.y, pie.x - an * 0.2f, pie.y)
            lineTo(pie.x + an * 0.2f, pie.y)
            cubicTo(pie.x + an * 0.62f, pie.y, pie.x + an * 0.62f, cuello + al * 0.1f, pie.x + an * 0.16f, cuello)
            close()
        }
        // El frunce de arriba, por encima de la jareta, abierto en abanico blando.
        val frunce = Path().apply {
            moveTo(pie.x - an * 0.16f, cuello)
            quadraticTo(pie.x - an * 0.36f, pie.y - al * 0.92f, pie.x - an * 0.24f, pie.y - al)
            quadraticTo(pie.x - an * 0.05f, pie.y - al * 0.9f, pie.x + an * 0.06f, pie.y - al * 1.02f)
            quadraticTo(pie.x + an * 0.24f, pie.y - al * 0.92f, pie.x + an * 0.3f, pie.y - al * 0.97f)
            quadraticTo(pie.x + an * 0.3f, pie.y - al * 0.84f, pie.x + an * 0.16f, cuello)
            close()
        }
        drawPath(frunce, a.luz(mezcla(cuero, P.TINTA, 0.15f), k))
        drawLine(a.sombra(0.4f), Offset(pie.x - an * 0.08f, cuello), Offset(pie.x - an * 0.14f, pie.y - al * 0.95f), 1f * u)
        drawLine(a.sombra(0.4f), Offset(pie.x + an * 0.08f, cuello), Offset(pie.x + an * 0.16f, pie.y - al * 0.95f), 1f * u)
        drawPath(panza, a.luz(cuero, k))
        clipPath(panza) {
            drawOval(a.brillo(0.28f, k), Offset(pie.x - an * 0.44f, pie.y - al * 0.6f), Size(an * 0.36f, al * 0.34f))
            drawOval(a.sombra(0.32f), Offset(pie.x + an * 0.05f, pie.y - al * 0.7f), Size(an * 0.6f, al * 0.8f))
            // Los pliegues que bajan del frunce.
            drawLine(a.sombra(0.3f), Offset(pie.x - an * 0.1f, cuello), Offset(pie.x - an * 0.26f, pie.y - al * 0.25f), 1.2f * u)
            drawLine(a.sombra(0.3f), Offset(pie.x + an * 0.06f, cuello), Offset(pie.x + an * 0.18f, pie.y - al * 0.3f), 1.2f * u)
        }
        // La jareta: el cordel apretado en el cuello, su nudo y los dos cabos.
        drawLine(a.luz(P.CABO_OSC, k), Offset(pie.x - an * 0.22f, cuello), Offset(pie.x + an * 0.22f, cuello), 2.2f * u, StrokeCap.Round)
        drawLine(a.luz(P.CABO, k), Offset(pie.x - an * 0.2f, cuello - 0.5f * u), Offset(pie.x + an * 0.2f, cuello - 0.5f * u), 1.1f * u, StrokeCap.Round)
        val nudo = Offset(pie.x + an * 0.14f, cuello)
        drawLine(a.luz(P.CABO, k), nudo, nudo + Offset(an * 0.22f, al * 0.32f), 1.2f * u, StrokeCap.Round)
        drawLine(a.luz(P.CABO, k), nudo, nudo + Offset(an * 0.1f, al * 0.38f), 1.2f * u, StrokeCap.Round)
        drawCircle(a.luz(P.CABO_OSC, k), 1.8f * u, nudo)
        // Las monedas, lisas.
        listOf(Offset(an * 0.75f, 0f) to 0.11f, Offset(an * 1.05f, 0f) to 0.09f, Offset(an * 0.9f, -h * 0.03f) to 0.1f).forEach { (d, s) ->
            val c = pie + d - Offset(0f, h * 0.02f)
            val r = h * s * 0.5f
            drawOval(a.sombra(0.3f), Offset(c.x - r + 1f * u, c.y - r * 0.3f + 1f * u), Size(r * 2, r * 0.7f))
            drawOval(a.luz(Color(0xFF9A7A2A), k), Offset(c.x - r, c.y - r * 0.35f), Size(r * 2, r * 0.7f))
            drawOval(a.luz(Color(0xFFE0BC50), k), Offset(c.x - r, c.y - r * 0.45f), Size(r * 2, r * 0.66f))
            destelloPirata(Offset(c.x - r * 0.4f, c.y - r * 0.2f), 1f * u, a, k)
        }
    }

    // ---------------------------------------------------------------- el pie (en la imagen de abajo)

    fun DrawScope.alPie(a: AirePirata, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width
        val fondo = h - suelo
        if (fondo < 4f) return
        argolla(a, Offset(w * 0.55f, suelo + fondo * 0.62f), w, cerca)
        botija(a, Offset(w * 0.075f, suelo + fondo * 0.56f), w, cerca)
        fanalPie(a, Offset(w * MuebleEstantePirata.FANAL_X, suelo + fondo * 0.92f), w, b, cerca)
        cesto(a, Offset(w * 0.9f, suelo + fondo * 0.9f), w, cerca)
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
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2.4f * u))))
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

    /** La argolla de hierro de la cubierta, tumbada sobre su perno. */
    private fun DrawScope.argolla(a: AirePirata, c: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(c)
        val r = w * 0.022f
        drawCircle(a.sombra(0.35f), r * 0.32f, c + Offset(1f * u, 1f * u))
        drawCircle(a.luz(P.HIERRO, k), r * 0.3f, c)
        drawOval(a.sombra(0.3f), Offset(c.x - r + 1.5f * u, c.y - r * 0.15f + 2f * u), Size(r * 2, r * 0.7f), style = Stroke(2.6f * u))
        drawOval(a.luz(P.HIERRO, k), Offset(c.x - r, c.y - r * 0.15f), Size(r * 2, r * 0.7f), style = Stroke(2.4f * u))
        drawArc(a.brillo(0.4f, k), 190f, 70f, false, Offset(c.x - r, c.y - r * 0.15f), Size(r * 2, r * 0.7f), style = Stroke(0.8f * u))
    }

    /**
     * El cesto de mimbre con cocos y limas: el tejido de mimbre con su trenza en el borde, los cocos peludos con sus tres ojos
     * y unas limas encima. Lo corta el borde de la derecha.
     */
    private fun DrawScope.cesto(a: AirePirata, pie: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val an = w * 0.3f; val al = w * 0.1f
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
            listOf(Offset(-0.18f, -0.1f), Offset(0.18f, -0.1f), Offset(0f, 0.16f)).forEach { e -> drawCircle(a.luz(Color(0xFF2A1A10), k), r * 0.08f, c + e * r) }
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
