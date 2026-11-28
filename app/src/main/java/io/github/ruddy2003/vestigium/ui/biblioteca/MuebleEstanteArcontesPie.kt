package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.sin

/**
 * El pie del estante de Arcontes ([MuebleEstanteArcontes]): el suelo de tierra apisonada de la cueva (el del Ideario), con sus
 * piedrecitas y una estera de juncos tejida en espiga; encima, cortados por los bordes: el ánfora del salador apoyada contra el
 * zócalo, con su tapón de barro; el brasero de barro con sus brasas y la ollita (la tercera luz de la noche, más roja); y el
 * montón de red de pesca con sus plomos, sus flotadores de madera y la aguja de hacer red, como las de la excavación de Magdala.
 */
internal object PieEstanteArcontes {
    private val P = PaletaArcontes

    /** El suelo, de [suelo] (debajo del zócalo) al borde de abajo [h]: la tierra con su luz, sus piedras y la estera. */
    fun DrawScope.suelo(s: SitioArcontes, b: Rect, suelo: Float, h: Float) {
        val w = size.width; val u = density
        val r = Rect(0f, suelo - 1f, w, h + 1f)
        if (s.a.oscuro) {
            val f = Offset(MuebleEstanteArcontes.BRASERO_X * w, b.top + b.height * MuebleEstanteArcontes.BRASERO_Y)
            fun k(d: Float) = s.t(f + Offset(d * w, d * w * 0.2f)).c(P.TIERRA)
            drawRect(Brush.radialGradient(0f to k(0.02f), 0.15f to k(0.1f), 0.35f to k(0.22f), 0.6f to k(0.38f), 1f to k(0.6f), center = f + Offset(0f, w * 0.05f), radius = w * 0.62f),
                r.topLeft, r.size)
        } else {
            val cs = List(5) { i -> s.t(Offset(w * i / 4f, (suelo + h) / 2)).c(P.TIERRA) }
            drawRect(Brush.horizontalGradient(cs, 0f, w), r.topLeft, r.size)
        }
        // La sombra del zócalo en el suelo y la tierra más hundida junto a él.
        val t0 = s.t(Offset(w / 2, suelo))
        drawRect(Brush.verticalGradient(listOf(t0.s(0.55f), t0.s(0f)), suelo, suelo + w * 0.035f), Offset(0f, suelo), Size(w, w * 0.035f))
        pinceladas(r, if (s.a.oscuro) P.NOCHE else mezclaRapida(P.TIERRA, P.CALIZA_LUZ, 0.4f), 8101, 12, u, 0.14f)
        pinceladas(r, if (s.a.oscuro) P.NOCHE else P.TIERRA_OSC, 8102, 9, u, 0.16f)
        // Las piedrecitas: un óvalo con su luz y su sombra.
        for (k in 0 until 9) {
            val p = Offset(w * azar(8103, k), suelo + (h - suelo) * (0.15f + 0.8f * azar(8104, k)))
            val rr = (0.9f + 2.2f * azar(8105, k)) * u
            val t = s.t(p)
            drawOval(t.s(0.35f), Offset(p.x - rr + 0.8f * u, p.y - rr * 0.5f + 0.8f * u), Size(2 * rr, 1.1f * rr))
            drawOval(t.c(if (k % 2 == 0) P.CALIZA_SOMBRA else P.HUECO), Offset(p.x - rr, p.y - rr * 0.6f), Size(2 * rr, 1.2f * rr))
            drawOval(t.b(0.3f), Offset(p.x - rr * 0.6f, p.y - rr * 0.55f), Size(rr, rr * 0.4f))
        }
        estera(s, suelo, h)
    }

    /**
     * La estera de juncos, en el suelo delante del armario: vista un poco desde arriba (más angosta al fondo), tejida en espiga,
     * con su orilla atada y algún junco suelto.
     */
    private fun DrawScope.estera(s: SitioArcontes, suelo: Float, h: Float) {
        val w = size.width; val u = density
        val y0 = suelo + (h - suelo) * 0.22f
        val forma = poligonoArcontes(w * 0.2f, y0, w * 0.86f, y0, w * 0.93f, h + 2f, w * 0.12f, h + 2f)
        val t = s.t(Offset(w * 0.5f, (y0 + h) / 2))
        translate(0f, 2f * u) { drawPath(forma, t.s(0.35f)) }
        drawPath(forma, s.aLoAncho(w * 0.12f, w * 0.93f, (y0 + h) / 2) { it.c(P.JUNCO, -0.02f) })
        clipPath(forma) {
            // La espiga: hileras de juncos en ángulo, una hacia un lado y la siguiente hacia el otro (en una baldosa).
            val b = baldosaEspiga(u)
            val al = if (s.a.oscuro) 0.75f else 1f
            var y = y0; var k = 0
            while (y < h + 1f && k < 80) {
                var x = w * 0.1f; var j = 0
                while (x < w * 0.95f && j < 40) { drawImage(b, Offset(x, y), alpha = al); x += b.width; j++ }
                y += b.height; k++
            }
        }
        // La orilla atada, del fondo.
        drawLine(s.aLoAncho(w * 0.2f, w * 0.86f, y0) { it.c(P.JUNCO_OSC, -0.1f) }, Offset(w * 0.2f, y0), Offset(w * 0.86f, y0), 2.4f * u)
        if (!s.a.oscuro) drawLine(t.b(0.35f), Offset(w * 0.2f, y0 - 0.8f * u), Offset(w * 0.86f, y0 - 0.8f * u), 0.7f * u)
        for (k in 0 until 9) {
            val x = w * (0.22f + 0.07f * k)
            drawLine(s.t(Offset(x, y0)).c(P.CORDEL), Offset(x, y0 - 1.6f * u), Offset(x + 0.8f * u, y0 + 1.6f * u), 1.2f * u, StrokeCap.Round)
        }
    }

    private var densidadEspiga = -1f
    private var espiga: ImageBitmap? = null

    /** El tejido en espiga de la estera, neutro (sombra violeta y luz crema), pintado una vez por densidad y pegado en mosaico. */
    private fun baldosaEspiga(u: Float): ImageBitmap {
        val hecha = espiga
        if (hecha != null && densidadEspiga == u) return hecha
        val fila = 5f * u; val paso = fila * 0.75f
        val ancho = paso * 16; val alto = fila * 4
        val img = ImageBitmap(kotlin.math.ceil(ancho).toInt().coerceAtLeast(1), kotlin.math.ceil(alto).toInt().coerceAtLeast(1))
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ancho, alto)) {
            val hondos = Path(); val claros = Path()
            for (k in 0 until 4) {
                val sx = if (k % 2 == 0) 1f else -1f
                val y = fila * k
                for (j in -2 until 18) {
                    val x = paso * j
                    hondos.moveTo(x, y); hondos.lineTo(x + sx * fila * 0.9f, y + fila)
                    claros.moveTo(x + 1.2f * u, y); claros.lineTo(x + 1.2f * u + sx * fila * 0.9f, y + fila)
                }
            }
            drawPath(hondos, SOMBRA_MUEBLE.copy(alpha = 0.32f), style = Stroke(0.9f * u))
            drawPath(claros, LUZ_MUEBLE.copy(alpha = 0.3f), style = Stroke(0.6f * u))
        }
        espiga = img; densidadEspiga = u
        return img
    }

    /** Lo que hay en el suelo: el ánfora, el brasero y la red. */
    fun DrawScope.objetos(s: SitioArcontes, b: Rect, suelo: Float, h: Float) {
        val w = size.width
        anfora(Offset(w * 0.115f, h - (h - suelo) * 0.12f), b, s)
        red(Offset(w * 0.83f, h - (h - suelo) * 0.1f), b, s)
        brasero(Offset(MuebleEstanteArcontes.BRASERO_X * w, h - (h - suelo) * 0.16f), b, s)
    }

    // ---------------------------------------------------------------- el ánfora

    /**
     * El ánfora del salador, apoyada contra el zócalo y cortada por el borde: el cuerpo largo con su pie de punta en un aro de
     * cuerda, el hombro, el cuello con sus dos asas y el tapón de barro sellado; el engobe claro chorreado y las marcas del torno.
     */
    private fun DrawScope.anfora(pie: Offset, b: Rect, s: SitioArcontes) {
        val w = size.width; val u = density
        val al = minOf(pie.y - b.top - w * 0.005f, w * 0.31f)
        val an = al * 0.34f
        val t = s.t(Offset(pie.x, pie.y - al * 0.5f))
        val hacia = if (s.a.oscuro) 1f else -1f
        contactoArcontes(Offset(pie.x + w * 0.02f, pie.y), an * 1.4f, w * 0.018f, t, 0.48f)
        rotate(8f, pie) {
            val x = pie.x; val y = pie.y
            // El aro de cuerda donde se calza la punta.
            drawOval(t.c(P.CORDEL, -0.2f), Offset(x - an * 0.3f, y - al * 0.05f), Size(an * 0.6f, al * 0.07f), style = Stroke(2.6f * u))
            val cuerpo = Path().apply {
                moveTo(x, y)
                cubicTo(x - an * 0.18f, y - al * 0.12f, x - an * 0.52f, y - al * 0.42f, x - an * 0.5f, y - al * 0.62f)
                cubicTo(x - an * 0.48f, y - al * 0.75f, x - an * 0.3f, y - al * 0.8f, x - an * 0.16f, y - al * 0.82f)
                lineTo(x - an * 0.12f, y - al * 0.97f); lineTo(x + an * 0.12f, y - al * 0.97f); lineTo(x + an * 0.16f, y - al * 0.82f)
                cubicTo(x + an * 0.3f, y - al * 0.8f, x + an * 0.48f, y - al * 0.75f, x + an * 0.5f, y - al * 0.62f)
                cubicTo(x + an * 0.52f, y - al * 0.42f, x + an * 0.18f, y - al * 0.12f, x, y)
                close()
            }
            translate(hacia * -w * 0.012f, -w * 0.004f) { drawPath(cuerpo, t.s(0.24f)) }
            // Las asas, del cuello al hombro.
            listOf(-1f, 1f).forEach { lado ->
                drawPath(Path().apply {
                    moveTo(x + lado * an * 0.12f, y - al * 0.93f)
                    cubicTo(x + lado * an * 0.4f, y - al * 0.95f, x + lado * an * 0.42f, y - al * 0.84f, x + lado * an * 0.32f, y - al * 0.78f)
                }, t.c(P.BARRO_CLARO, if (lado * hacia > 0f) 0.05f else -0.25f), style = Stroke(an * 0.08f, cap = StrokeCap.Round))
            }
            drawPath(cuerpo, Brush.horizontalGradient(listOf(t.c(P.BARRO_CLARO, if (hacia < 0f) 0.14f else -0.35f), t.c(P.BARRO_CLARO), t.c(P.BARRO, if (hacia < 0f) -0.35f else 0.1f)),
                x - an * 0.5f, x + an * 0.5f))
            clipPath(cuerpo) {
                // Las marcas del torno y el engobe claro que chorreó del hombro.
                for (k in 1..9) drawLine(t.s(0.1f), Offset(x - an * 0.6f, y - al * 0.09f * k), Offset(x + an * 0.6f, y - al * 0.09f * k), 0.6f * u)
                listOf(Triple(-0.26f, 0.3f, 0.09f), Triple(-0.02f, 0.14f, 0.05f), Triple(0.14f, 0.22f, 0.07f)).forEachIndexed { k, (dx, l, g) ->
                    val c0 = Offset(x + an * dx, y - al * 0.8f)
                    val chorro = Path().apply {
                        moveTo(c0.x, c0.y); lineTo(c0.x + an * g, c0.y)
                        quadraticTo(c0.x + an * g * 1.2f, c0.y + al * l * 0.6f, c0.x + an * g * 0.8f, c0.y + al * l)
                        quadraticTo(c0.x + an * g * 0.4f, c0.y + al * l * 1.08f, c0.x + an * g * 0.1f, c0.y + al * l * 0.92f)
                        quadraticTo(c0.x - an * g * 0.2f, c0.y + al * l * 0.5f, c0.x, c0.y); close()
                    }
                    drawPath(chorro, t.c(P.CALIZA_LUZ).copy(alpha = 0.28f + 0.1f * k))
                }
                drawLine(t.b(0.4f), Offset(x - an * 0.36f, y - al * 0.7f), Offset(x - an * 0.4f, y - al * 0.35f), an * 0.06f, StrokeCap.Round)
            }
            // El tapón de barro sellado en la boca.
            val tp = Rect(x - an * 0.15f, y - al * 1.02f, x + an * 0.15f, y - al * 0.95f)
            drawRoundRect(t.c(P.BARRO, -0.1f), tp.topLeft, tp.size, androidx.compose.ui.geometry.CornerRadius(tp.height * 0.5f))
            drawRect(t.b(0.4f), Offset(tp.left + 1f * u, tp.top), Size(tp.width * 0.6f, 0.9f * u))
        }
    }

    // ---------------------------------------------------------------- el brasero

    /**
     * El brasero: un hornillo de barro, redondo y más ancho abajo, con su cordón de dedos apretados y la boca del tiro en arco abajo
     * (de día, oscura, con ceniza gris y un rojo apagado; de noche, las brasas encendidas, la tercera luz) y, arriba, la boca con
     * su resplandor y la ollita.
     */
    private fun DrawScope.brasero(pie: Offset, b: Rect, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * 0.19f
        val fuego = Offset(pie.x, b.top + b.height * MuebleEstanteArcontes.BRASERO_Y)
        val al = ((pie.y - fuego.y) / 0.3f).coerceIn(w * 0.06f, w * 0.19f)
        val top = pie.y - al
        fun tb(p: Offset) = s.a.en(p.x / w, p.y / w, s.k(p) * 0.4f, 0.8f)
        val t = tb(Offset(pie.x, pie.y - al * 0.5f))
        val viva = s.a.lumbre
        val fuerza = if (s.a.oscuro && !s.a.noche) 1f else 0.6f
        contactoArcontes(Offset(pie.x, pie.y), an * 1.3f, w * 0.022f, t, 0.5f)
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.36f, top)
            cubicTo(pie.x - an * 0.46f, top + al * 0.2f, pie.x - an * 0.56f, pie.y - al * 0.3f, pie.x - an * 0.5f, pie.y)
            lineTo(pie.x + an * 0.5f, pie.y)
            cubicTo(pie.x + an * 0.56f, pie.y - al * 0.3f, pie.x + an * 0.46f, top + al * 0.2f, pie.x + an * 0.36f, top)
            close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(tb(Offset(pie.x - an * 0.45f, pie.y - al * 0.5f)).c(P.BARRO_CLARO, -0.04f), t.c(P.BARRO_CLARO, -0.04f),
            tb(Offset(pie.x + an * 0.45f, pie.y - al * 0.5f)).c(P.BARRO_CLARO, -0.04f)), pie.x - an * 0.5f, pie.x + an * 0.5f))
        clipPath(cuerpo) {
            // Redondo: el lado que mira a la boca de la cueva, claro; el otro, en sombra; y el tizne de la boca de arriba.
            if (!s.a.oscuro) {
                drawRect(t.b(0.22f), Offset(pie.x - an * 0.5f, top), Size(an * 0.2f, al))
                drawRect(t.s(0.26f), Offset(pie.x + an * 0.18f, top), Size(an * 0.4f, al))
            } else drawRect(t.s(0.2f), Offset(pie.x - an * 0.5f, top), Size(an * 0.22f, al))
            drawRect(Brush.verticalGradient(listOf(P.TINTA.copy(alpha = 0.45f), Color.Transparent), top, top + al * 0.3f), Offset(pie.x - an * 0.5f, top), Size(an, al * 0.3f))
            for (k in 1..4) drawLine(t.s(0.1f), Offset(pie.x - an * 0.6f, top + al * 0.17f * k), Offset(pie.x + an * 0.6f, top + al * 0.17f * k), 0.6f * u)
        }
        // El cordón de dedos apretados, a media altura.
        val cordon = top + al * 0.38f
        val puntos = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(0.01f, 3.6f * u))
        drawLine(t.s(0.35f), Offset(pie.x - an * 0.42f + 0.6f * u, cordon + 0.6f * u), Offset(pie.x + an * 0.42f + 0.6f * u, cordon + 0.6f * u), 1.8f * u, StrokeCap.Round, puntos)
        drawLine(t.c(P.BARRO_CLARO, if (s.a.oscuro) 0f else 0.06f), Offset(pie.x - an * 0.42f, cordon), Offset(pie.x + an * 0.42f, cordon), 3.4f * u, StrokeCap.Round, puntos)
        // La boca del tiro, en arco, abajo: lo que arde de noche.
        val ab = Rect(fuego.x - an * 0.17f, fuego.y - al * 0.16f, fuego.x + an * 0.17f, pie.y - 1f * u)
        val arco = Path().apply {
            moveTo(ab.left, ab.bottom); lineTo(ab.left, ab.top + ab.width * 0.4f)
            arcTo(Rect(ab.left, ab.top, ab.right, ab.top + ab.width * 0.8f), 180f, 180f, false)
            lineTo(ab.right, ab.bottom); close()
        }
        drawPath(arco, P.TINTA.copy(alpha = 0.85f))
        val ascuas = Path(); val ceniza = Path()
        for (k in 0 until 9) {
            val c = Offset(ab.left + ab.width * (0.12f + 0.76f * azar(8121, k)), ab.bottom - ab.height * (0.08f + 0.3f * azar(8122, k)))
            val rr = (1.6f + 1.8f * azar(8123, k)) * u
            (if (k % 3 == 0) ceniza else ascuas).addOval(Rect(c.x - rr, c.y - rr * 0.7f, c.x + rr, c.y + rr * 0.7f))
        }
        clipPath(arco) {
            if (viva) {
                drawRect(Brush.verticalGradient(listOf(P.BRASA.copy(alpha = 0.2f * fuerza), P.BRASA.copy(alpha = 0.75f * fuerza)), ab.top, ab.bottom), ab.topLeft, ab.size)
                drawPath(ascuas, P.BRASA)
                translate(-0.5f * u, -0.6f * u) { drawPath(ascuas, P.LLAMA.copy(alpha = 0.85f * fuerza)) }
                drawPath(ceniza, Color(0xFF4A3A34))
            } else {
                drawPath(ascuas, t.c(Color(0xFF8E8580)))
                drawPath(ceniza, t.c(Color(0xFFB9B0A6)))
                drawPath(ascuas, P.BRASA.copy(alpha = 0.3f))
            }
        }
        drawPath(arco, t.s(0.5f), style = Stroke(1.2f * u))
        translate(1f * u, 0f) {
            drawPath(Path().apply { moveTo(ab.left, ab.bottom); lineTo(ab.left, ab.top + ab.width * 0.4f); arcTo(Rect(ab.left, ab.top, ab.right, ab.top + ab.width * 0.8f), 180f, 90f, false) },
                t.b(0.4f), style = Stroke(0.8f * u))
        }
        // La boca de arriba, con el resplandor de las brasas, y la ollita encima, de lado.
        val boca = Rect(pie.x - an * 0.36f, top - al * 0.07f, pie.x + an * 0.36f, top + al * 0.07f)
        drawOval(t.c(P.BARRO, -0.55f), boca.topLeft, boca.size)
        if (viva) drawOval(P.BRASA.copy(alpha = 0.6f * fuerza), boca.deflate(boca.height * 0.2f).topLeft, boca.deflate(boca.height * 0.2f).size)
        drawArc(t.b(0.4f), 180f, 120f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
        val oc = Offset(pie.x + an * 0.06f, top - al * 0.04f)
        val orr = minOf(al * 0.4f, an * 0.3f)
        val to = tb(oc)
        rotate(-5f, oc) {
            val olla = Path().apply {
                moveTo(oc.x - orr * 0.7f, oc.y - orr * 0.9f); lineTo(oc.x + orr * 0.7f, oc.y - orr * 0.9f)
                cubicTo(oc.x + orr * 1.15f, oc.y - orr * 0.6f, oc.x + orr * 1.1f, oc.y + orr * 0.2f, oc.x, oc.y + orr * 0.3f)
                cubicTo(oc.x - orr * 1.1f, oc.y + orr * 0.2f, oc.x - orr * 1.15f, oc.y - orr * 0.6f, oc.x - orr * 0.7f, oc.y - orr * 0.9f)
                close()
            }
            drawPath(olla, Brush.horizontalGradient(listOf(to.c(P.BARRO, 0.06f), to.c(P.BARRO_OSC, -0.1f)), oc.x - orr, oc.x + orr))
            if (viva) drawPath(Path().apply { addOval(Rect(oc.x - orr, oc.y - orr * 0.05f, oc.x + orr, oc.y + orr * 0.35f)) }, P.BRASA.copy(alpha = 0.35f * fuerza))
            drawOval(to.c(P.BARRO_OSC, -0.4f), Offset(oc.x - orr * 0.7f, oc.y - orr * 1f), Size(orr * 1.4f, orr * 0.22f))
            drawArc(to.b(0.4f), 190f, 50f, false, Offset(oc.x - orr * 0.95f, oc.y - orr * 0.8f), Size(orr * 1.2f, orr), style = Stroke(1f * u))
            drawLine(to.c(P.BARRO, -0.2f), Offset(oc.x + orr * 0.7f, oc.y - orr * 0.85f), Offset(oc.x + orr * 1.15f, oc.y - orr * 0.95f), 2f * u, StrokeCap.Round)
        }
        // El resplandor de las brasas en el aire, de noche.
        if (viva) {
            charcoArcontes(Offset(fuego.x, fuego.y + al * 0.05f), w * 0.22f, P.BRASA, 0.5f * fuerza)
            charcoArcontes(Offset(fuego.x, fuego.y + al * 0.05f), w * 0.09f, P.LLAMA_HALO, 0.8f * fuerza)
        }
    }

    // ---------------------------------------------------------------- la red

    /**
     * El montón de red de pesca, cortado por el borde de la derecha: la malla en rombos sobre su bulto blando, la relinga de arriba
     * con los flotadores de madera, la de abajo con los plomos y, delante, la aguja de hacer red.
     */
    private fun DrawScope.red(pie: Offset, b: Rect, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * 0.32f
        val al = minOf(pie.y - b.top - w * 0.02f, w * 0.2f)
        val t = s.t(Offset(pie.x, pie.y - al * 0.5f))
        contactoArcontes(Offset(pie.x, pie.y), an * 1.05f, w * 0.02f, t, 0.48f)
        val bulto = Path().apply {
            moveTo(pie.x - an * 0.5f, pie.y)
            cubicTo(pie.x - an * 0.52f, pie.y - al * 0.5f, pie.x - an * 0.3f, pie.y - al * 1.05f, pie.x - an * 0.02f, pie.y - al * 0.95f)
            cubicTo(pie.x + an * 0.2f, pie.y - al * 1.1f, pie.x + an * 0.5f, pie.y - al * 0.8f, pie.x + an * 0.6f, pie.y - al * 0.5f)
            lineTo(pie.x + an * 0.6f, pie.y); close()
        }
        val cuerda = mezclaRapida(P.CORDEL, P.JUNCO_OSC, 0.35f)
        // Lo de dentro del montón, en sombra, y la malla encima.
        drawPath(bulto, s.aLoAncho(pie.x - an * 0.5f, pie.x + an * 0.6f, pie.y - al * 0.5f) { it.c(mezclaRapida(P.TIERRA_OSC, cuerda, 0.3f), -0.2f) })
        clipPath(bulto) {
            // Los pliegues del montón: bandas de luz y sombra que siguen la curva.
            for (k in 0 until 5) {
                val y = pie.y - al * (0.15f + 0.19f * k)
                drawOval(t.b(0.12f), Offset(pie.x - an * (0.45f - 0.04f * k), y - al * 0.08f), Size(an * (0.9f - 0.1f * k), al * 0.12f))
                drawOval(t.s(0.2f), Offset(pie.x - an * (0.42f - 0.04f * k), y), Size(an * (0.9f - 0.1f * k), al * 0.08f))
            }
            val malla = Path()
            val paso = 5.5f * u
            var x = pie.x - an * 0.6f; var j = 0
            while (x < pie.x + an * 0.7f && j < 200) {
                malla.moveTo(x, pie.y + 2f * u); malla.lineTo(x + al * 1.2f, pie.y - al * 1.2f)
                malla.moveTo(x, pie.y - al * 1.2f); malla.lineTo(x + al * 1.2f, pie.y + 2f * u)
                x += paso; j++
            }
            drawPath(malla, t.c(cuerda, 0.05f).copy(alpha = 0.75f), style = Stroke(0.7f * u))
            drawPath(malla, t.s(0.25f), style = Stroke(0.5f * u))
        }
        // La relinga de arriba con los flotadores de madera, que cuelga por delante.
        val arriba = Path().apply { moveTo(pie.x - an * 0.4f, pie.y - al * 0.7f); quadraticTo(pie.x, pie.y - al * 0.35f, pie.x + an * 0.6f, pie.y - al * 0.62f) }
        drawPath(arriba, t.c(P.CORDEL), style = Stroke(1.6f * u))
        for (k in 0 until 4) {
            val f = 0.12f + 0.24f * k
            val p = Offset(pie.x - an * 0.4f + an * f, pie.y - al * (0.7f - 0.33f * sin(f * 3.1416f)) + al * 0.03f * (1 - k % 2))
            val tf = s.t(p)
            drawOval(tf.s(0.35f), Offset(p.x - 5f * u, p.y - 2.2f * u + 1.2f * u), Size(11f * u, 5f * u))
            drawOval(tf.c(P.OLIVO_LUZ, -0.04f), Offset(p.x - 5.5f * u, p.y - 2.6f * u), Size(11f * u, 5.2f * u))
            drawOval(tf.b(0.4f), Offset(p.x - 4f * u, p.y - 2.2f * u), Size(5f * u, 1.6f * u))
        }
        // La relinga de abajo, con sus plomos, tendida en la tierra.
        val pl0 = Offset(pie.x - an * 0.55f, pie.y - 2f * u)
        drawLine(t.c(P.CORDEL, -0.1f), pl0, Offset(pie.x + an * 0.6f, pie.y - 4f * u), 1.4f * u)
        for (k in 0 until 5) {
            val p = Offset(pl0.x + an * (0.06f + 0.24f * k), pie.y - 2.5f * u - k * 0.4f * u)
            val tp = s.t(p)
            drawRoundRect(tp.s(0.4f), Offset(p.x - 3.4f * u + 0.8f * u, p.y - 2f * u + 1f * u), Size(6.8f * u, 4f * u), androidx.compose.ui.geometry.CornerRadius(1.6f * u))
            drawRoundRect(tp.c(P.PLOMO), Offset(p.x - 3.4f * u, p.y - 2f * u), Size(6.8f * u, 4f * u), androidx.compose.ui.geometry.CornerRadius(1.6f * u))
            drawRect(tp.b(0.35f), Offset(p.x - 2.6f * u, p.y - 1.6f * u), Size(4f * u, 0.8f * u))
        }
        // La aguja de hacer red, de madera, tendida delante.
        val ag = Offset(pie.x - an * 0.42f, pie.y + w * 0.012f)
        val ta = s.t(ag)
        rotate(-8f, ag) {
            val r = Rect(ag.x, ag.y - 2.4f * u, ag.x + w * 0.11f, ag.y + 2.4f * u)
            drawRect(ta.s(0.4f), r.topLeft + Offset(1f * u, 1.4f * u), r.size)
            drawPath(poligonoArcontes(r.left, r.center.y, r.left + r.height * 1.6f, r.top, r.right, r.top, r.right, r.bottom, r.left + r.height * 1.6f, r.bottom), ta.c(P.OLIVO_LUZ, 0.02f))
            drawRect(ta.c(P.OLIVO_HONDO), Offset(r.left + r.width * 0.3f, r.center.y - 0.7f * u), Size(r.width * 0.25f, 1.4f * u))
            drawRect(ta.b(0.45f), Offset(r.left + r.height, r.top), Size(r.width - r.height, 0.8f * u))
            drawLine(ta.c(cuerda), Offset(r.right - r.width * 0.2f, r.center.y), Offset(r.right + w * 0.02f, r.center.y + 3f * u), 0.9f * u)
        }
    }

}
