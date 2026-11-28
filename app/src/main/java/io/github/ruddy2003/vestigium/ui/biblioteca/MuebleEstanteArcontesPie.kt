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
 * zócalo, con su tapón de barro; el brasero de bronce, un cuenco bajo sobre tres patas con sus ascuas (la luz más roja de la
 * noche); y el montón de red de pesca con sus plomos, sus flotadores de madera y la aguja de hacer red, como las de la excavación
 * de Magdala. Lo que hay en el suelo se pinta a la medida de un teléfono grande (en un estante ancho, achicado en medio).
 */
internal object PieEstanteArcontes {
    private val P = PaletaArcontes

    /** Las medidas del brasero, en anchos: el ancho del cuenco, el alto de las patas y la hondura del cuenco. */
    private const val BR_ANCHO = 0.23f
    private const val BR_PATAS = 0.07f
    private const val BR_CUENCO = 0.054f

    /** Dónde se para el brasero en un suelo de [suelo] a [h]. */
    private fun pieBrasero(w: Float, suelo: Float, h: Float) = Offset(MuebleEstanteArcontes.BRASERO_X * w, h - (h - suelo) * 0.16f)

    /** Dónde arden las ascuas del brasero (en las medidas en que se pinta el suelo): un poco por encima de la boca del cuenco. */
    fun fuego(w: Float, suelo: Float, h: Float): Offset = pieBrasero(w, suelo, h).let { Offset(it.x, it.y - w * (BR_PATAS + BR_CUENCO + 0.006f)) }

    /** El suelo, de [suelo] (debajo del zócalo) al borde de abajo [h]: la tierra con su luz, sus piedras y la estera. */
    fun DrawScope.suelo(s: SitioArcontes, b: Rect, suelo: Float, h: Float, fuego: Offset) {
        val w = size.width; val u = density
        val r = Rect(0f, suelo - 1f, w, h + 1f)
        if (s.a.oscuro) {
            val e = s.a.escala
            fun k(d: Float) = s.t(fuego + Offset(d * w * e, d * w * e * 0.2f)).c(P.TIERRA)
            drawRect(Brush.radialGradient(0f to k(0.02f), 0.15f to k(0.1f), 0.35f to k(0.22f), 0.6f to k(0.38f), 1f to k(0.6f), center = fuego + Offset(0f, w * e * 0.1f), radius = w * e * 0.62f),
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

    /** Lo que hay en el suelo, de [suelo] a [h] (en sus medidas): el ánfora, el brasero y la red. */
    fun DrawScope.objetos(s: SitioArcontes, suelo: Float, h: Float) {
        val w = size.width
        // El borde de arriba del zócalo, en estas medidas (hasta ahí puede subir el ánfora).
        val tope = suelo - (h - suelo) * MuebleEstanteArcontes.SUELO / (1f - MuebleEstanteArcontes.SUELO)
        anfora(Offset(w * 0.115f, h - (h - suelo) * 0.12f), tope, s)
        red(Offset(w * 0.83f, h - (h - suelo) * 0.1f), tope, s)
        brasero(pieBrasero(w, suelo, h), s)
    }

    // ---------------------------------------------------------------- el ánfora

    /**
     * El ánfora del salador, apoyada contra el zócalo y cortada por el borde: el cuerpo largo con su pie de punta en un aro de
     * cuerda, el hombro, el cuello con sus dos asas y el tapón de barro sellado; el engobe claro chorreado y las marcas del torno.
     */
    private fun DrawScope.anfora(pie: Offset, tope: Float, s: SitioArcontes) {
        val w = size.width; val u = density
        val al = minOf(pie.y - tope - w * 0.005f, w * 0.31f)
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
                // De noche, el brasero (a la derecha) le pinta el costado que lo mira: el charco rojo en el barro y su filo.
                if (s.a.lumbre) {
                    val f = if (s.a.oscuro && !s.a.noche) 1f else 0.55f
                    drawRect(Brush.horizontalGradient(listOf(Color.Transparent, P.BRASA.copy(alpha = 0.3f * f), P.LLAMA.copy(alpha = 0.42f * f)), x, x + an * 0.52f),
                        Offset(x, y - al * 0.85f), Size(an * 0.55f, al * 0.85f))
                    drawLine(P.LLAMA_HALO.copy(alpha = 0.55f * f), Offset(x + an * 0.44f, y - al * 0.66f), Offset(x + an * 0.4f, y - al * 0.36f), an * 0.05f, StrokeCap.Round)
                }
            }
            // El tapón de barro sellado en la boca.
            val tp = Rect(x - an * 0.15f, y - al * 1.02f, x + an * 0.15f, y - al * 0.95f)
            drawRoundRect(t.c(P.BARRO, -0.1f), tp.topLeft, tp.size, androidx.compose.ui.geometry.CornerRadius(tp.height * 0.5f))
            drawRect(t.b(0.4f), Offset(tp.left + 1f * u, tp.top), Size(tp.width * 0.6f, 0.9f * u))
        }
    }

    // ---------------------------------------------------------------- el brasero

    /**
     * El brasero de bronce: un cuenco bajo y ancho sobre tres patas finas que se abren (la de atrás, más corta y en sombra), con
     * sus pies de disco y dos anillas a los lados; dentro, el montón de carbón con las ascuas (de día, ceniza gris con algún rojo
     * apagado; de noche, encendidas: la luz más roja de la cueva, que deja su charco en el suelo y en el ánfora).
     */
    private fun DrawScope.brasero(pie: Offset, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * BR_ANCHO; val lg = w * BR_PATAS; val bd = w * BR_CUENCO
        val cx = pie.x
        val boca = pie.y - lg - bd
        val viva = s.a.lumbre
        val fuerza = if (s.a.oscuro && !s.a.noche) 1f else 0.6f
        // El bronce de fuera mira lejos de las ascuas: de noche le llega poco de ellas.
        val t0 = s.t(Offset(cx, boca + bd * 0.6f))
        val t = if (s.a.oscuro) TonoArcontes(t0.a, t0.k * 0.35f, t0.sol, t0.amb, t0.rojo, false) else t0
        val bronce = t.c(P.BRONCE); val luz = t.c(P.BRONCE_LUZ, 0.04f); val hondo = t.c(P.BRONCE_OSC, -0.15f)
        // El charco de las ascuas en la tierra, debajo y alrededor (de noche), y las sombras de los pies.
        if (viva) drawOval(Brush.radialGradient(listOf(P.BRASA.copy(alpha = 0.42f * fuerza), P.LLAMA.copy(alpha = 0.16f * fuerza), Color.Transparent),
            Offset(cx, pie.y), an * 1.1f), Offset(cx - an * 1.1f, pie.y - an * 0.24f), Size(an * 2.2f, an * 0.5f))
        contactoArcontes(Offset(cx, pie.y), an * 0.95f, w * 0.014f, t0, 0.42f)
        // Las tres patas: la de atrás (más corta y en sombra), y las dos de delante que se abren, con su luz.
        val atras = Path().apply { moveTo(cx + an * 0.05f, boca + bd * 0.95f); quadraticTo(cx + an * 0.08f, pie.y - lg * 0.5f, cx + an * 0.04f, pie.y - lg * 0.16f) }
        drawPath(atras, hondo, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawOval(hondo, Offset(cx + an * 0.04f - 3f * u, pie.y - lg * 0.16f - 1.2f * u), Size(6f * u, 2.4f * u))
        listOf(-1f, 1f).forEach { lado ->
            val a0 = Offset(cx + lado * an * 0.28f, boca + bd * 0.7f)
            val a1 = Offset(cx + lado * an * 0.44f, pie.y - 1.2f * u)
            val pata = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x + lado * an * 0.02f, a0.y + lg * 0.45f, a1.x - lado * an * 0.1f, a1.y - lg * 0.5f, a1.x, a1.y) }
            translate(1.4f * u, 0.8f * u) { drawPath(pata, t0.s(0.3f), style = Stroke(2.6f * u, cap = StrokeCap.Round)) }
            drawPath(pata, if (lado < 0f) bronce else hondo, style = Stroke(2.8f * u, cap = StrokeCap.Round))
            drawPath(pata, (if (lado < 0f) t.b(0.55f) else t.b(0.2f)), style = Stroke(0.8f * u, cap = StrokeCap.Round))
            // El pie: un disco chato.
            drawOval(t0.s(0.4f), Offset(a1.x - 4f * u, a1.y - 0.4f * u), Size(9f * u, 2.6f * u))
            drawOval(if (lado < 0f) bronce else hondo, Offset(a1.x - 4.2f * u, a1.y - 1.6f * u), Size(8.4f * u, 3f * u))
            drawLine(t.b(0.4f), Offset(a1.x - 3f * u, a1.y - 1.2f * u), Offset(a1.x + 1.5f * u, a1.y - 1.2f * u), 0.6f * u)
        }
        // El cuenco: bajo y ancho, redondo por debajo; la luz del lado de la boca de la cueva (o de las ascuas, de noche, en el labio).
        val cuenco = Path().apply {
            moveTo(cx - an / 2, boca)
            cubicTo(cx - an * 0.49f, boca + bd * 0.8f, cx - an * 0.22f, boca + bd * 1.06f, cx, boca + bd * 1.06f)
            cubicTo(cx + an * 0.22f, boca + bd * 1.06f, cx + an * 0.49f, boca + bd * 0.8f, cx + an / 2, boca)
            close()
        }
        drawPath(cuenco, Brush.horizontalGradient(listOf(luz, bronce, bronce, hondo), cx - an / 2, cx + an / 2))
        // El reflejo largo del bronce bruñido, la banda en sombra de abajo y los dos filetes torneados.
        drawPath(Path().apply { moveTo(cx - an * 0.4f, boca + bd * 0.25f); quadraticTo(cx - an * 0.3f, boca + bd * 0.75f, cx - an * 0.08f, boca + bd * 0.9f) },
            t.b(0.5f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(cx - an * 0.36f, boca + bd * 0.86f); quadraticTo(cx, boca + bd * 1.12f, cx + an * 0.36f, boca + bd * 0.86f) },
            t.s(0.35f), style = Stroke(2.2f * u))
        listOf(0.22f, 0.32f).forEach { f ->
            drawPath(Path().apply { moveTo(cx - an * 0.49f, boca + bd * f); quadraticTo(cx, boca + bd * (f + 0.16f), cx + an * 0.49f, boca + bd * f) },
                hondo.copy(alpha = 0.6f), style = Stroke(0.7f * u))
        }
        // Las dos anillas de los lados, para moverlo.
        listOf(-1f, 1f).forEach { lado ->
            val c = Offset(cx + lado * an * 0.5f, boca + bd * 0.32f)
            drawCircle(t0.s(0.35f), 3.6f * u, c + Offset(0.6f * u, 0.8f * u), style = Stroke(1.4f * u))
            drawCircle(if (lado < 0f) luz else hondo, 3.4f * u, c, style = Stroke(1.3f * u))
        }
        // La boca: el labio vuelto, con su filo, y dentro el montón de carbón con las ascuas.
        val labio = Rect(cx - an * 0.52f, boca - an * 0.07f, cx + an * 0.52f, boca + an * 0.07f)
        drawOval(hondo, labio.topLeft, labio.size)
        val dentro = labio.deflate(an * 0.035f)
        drawOval(Color(0xFF231C1A), dentro.topLeft, dentro.size)
        val monton = Path().apply {
            moveTo(dentro.left + dentro.width * 0.08f, dentro.center.y)
            quadraticTo(dentro.left + dentro.width * 0.2f, boca - w * 0.02f, cx, boca - w * 0.022f)
            quadraticTo(dentro.right - dentro.width * 0.2f, boca - w * 0.02f, dentro.right - dentro.width * 0.08f, dentro.center.y)
            quadraticTo(cx, dentro.bottom, dentro.left + dentro.width * 0.08f, dentro.center.y)
            close()
        }
        drawPath(monton, if (viva) Color(0xFF2E1E18) else t.c(Color(0xFF5A524E)))
        val carbones = Path(); val ascuas = Path(); val ceniza = Path()
        for (k in 0 until 14) {
            val f = azar(8131, k)
            val c = Offset(dentro.left + dentro.width * (0.14f + 0.72f * f), boca - w * 0.016f * (1f - kotlin.math.abs(f - 0.5f) * 1.7f) * azar(8132, k) + an * 0.02f)
            val rr = (2.2f + 2.2f * azar(8133, k)) * u
            val o = Rect(c.x - rr, c.y - rr * 0.65f, c.x + rr, c.y + rr * 0.65f)
            carbones.addOval(o)
            when (k % 3) {
                0 -> ascuas.addOval(o.deflate(rr * 0.35f))
                1 -> ceniza.addOval(Rect(o.left + rr * 0.3f, o.top, o.right - rr * 0.2f, o.center.y))
                else -> ascuas.addOval(Rect(o.left + rr * 0.5f, o.center.y - rr * 0.15f, o.right - rr * 0.2f, o.bottom - rr * 0.1f))
            }
        }
        drawPath(carbones, if (viva) Color(0xFF1C1412) else t.c(Color(0xFF3E3632)))
        if (viva) {
            drawPath(ascuas, P.BRASA.copy(alpha = 0.95f))
            translate(-0.4f * u, -0.5f * u) { drawPath(ascuas, P.LLAMA.copy(alpha = 0.8f * fuerza)) }
            drawPath(ceniza, Color(0xFF6A5650).copy(alpha = 0.7f))
        } else {
            drawPath(ceniza, t.c(Color(0xFFB9B0A6)))
            drawPath(ascuas, P.BRASA.copy(alpha = 0.35f))
        }
        drawArc(t.b(if (s.a.oscuro) 0.2f else 0.55f), 190f, 120f, false, labio.topLeft, labio.size, style = Stroke(1.1f * u))
        if (viva) {
            // El resplandor de las ascuas en el labio de dentro, en el aire de encima y unas chispas.
            drawArc(P.LLAMA.copy(alpha = 0.6f * fuerza), 20f, 140f, false, dentro.topLeft, dentro.size, style = Stroke(1.2f * u))
            val f0 = Offset(cx, boca - w * 0.01f)
            charcoArcontes(f0, w * 0.2f, P.BRASA, 0.5f * fuerza)
            charcoArcontes(f0, w * 0.08f, P.LLAMA_HALO, 0.75f * fuerza)
            for (k in 0 until 4) {
                val p = Offset(cx + an * (azar(8141, k) - 0.5f) * 0.4f, boca - w * (0.03f + 0.05f * azar(8142, k)))
                drawCircle(P.LLAMA_HALO.copy(alpha = 0.8f * fuerza), (0.6f + 0.6f * azar(8143, k)) * u, p)
            }
        }
    }

    // ---------------------------------------------------------------- la red

    /**
     * El montón de red de pesca, cortado por el borde de la derecha: la malla en rombos sobre su bulto blando, la relinga de arriba
     * con los flotadores de madera, la de abajo con los plomos y, delante, la aguja de hacer red.
     */
    private fun DrawScope.red(pie: Offset, tope: Float, s: SitioArcontes) {
        val w = size.width; val u = density
        val an = w * 0.32f
        val al = minOf(pie.y - tope - w * 0.02f, w * 0.2f)
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
