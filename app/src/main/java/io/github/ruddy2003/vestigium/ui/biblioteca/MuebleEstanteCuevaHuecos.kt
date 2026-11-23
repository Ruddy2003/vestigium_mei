package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sin

/**
 * Lo que acompaña en los huecos libres de las repisas: cosas grandes de la cueva (del 70 al 90 % del alto del hueco), en grupos
 * solapados que cambian de una repisa a otra. El cesto de corteza en artesa con el núcleo de sílex, sus láminas y el percutor;
 * el asta de reno de pie con dos pieles dobladas delante; el cuerno de bisonte con la grasa, la lamparilla (que de noche arde y
 * alumbra su hueco) y los terrones de ocre.
 */
internal object HuecosEstanteCueva {
    private const val DELANTE = 0.1f

    /**
     * El hueco libre de una repisa, de [ocupado] al costado: un grupo; si el hueco es muy ancho (una repisa con pocas entradas),
     * dos grupos distintos, uno cerca de las entradas y otro contra el poste. Si no cabe nada, nada.
     */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, g: MarcoCueva) {
        val w = g.w
        val x0 = ocupado + w * 0.02f
        val x1 = g.x1 - w * 0.012f
        val ancho = x1 - x0
        val H = r.height
        val pie = r.bottom - w * 0.004f
        if (ancho < H * 0.3f) return
        var grupo = Math.floorMod(k * 5 + 1, 3)
        // Si el grupo que le toca no cabe, el que quepa; si no cabe ninguno, el cesto solo o, en lo más angosto, el cuerno.
        if (ancho < H * ANCHOS[grupo]) grupo = (0 until 3).filter { ancho >= H * ANCHOS[it] }.maxByOrNull { ANCHOS[it] } ?: -1
        if (grupo < 0) {
            if (ancho >= H * 0.84f) cestoGrande(g, Offset(x0 + ancho * 0.5f, pie), H * 0.8f, H * 0.74f, k)
            else if (ancho >= H * 0.32f) cuerno(g, Offset(x0 + ancho * 0.5f, pie), H * 0.78f, k)
            return
        }
        val largo = H * ANCHOS[grupo]
        val otro = (grupo + 1 + Math.floorMod(k, 2)) % 3
        val largo2 = H * ANCHOS[otro]
        if (ancho >= largo + largo2 + H * 0.25f) {
            val libre = ancho - largo - largo2
            grupoEn(grupo, x0 + libre * 0.18f, pie, H, g, k)
            grupoEn(otro, x1 - largo2 - libre * 0.1f, pie, H, g, k + 1)
            return
        }
        grupoEn(grupo, x0 + (ancho - largo) * 0.3f, pie, H, g, k)
    }

    /** Lo que mide de ancho cada grupo, en altos de hueco. */
    private val ANCHOS = floatArrayOf(1.0f, 0.9f, 0.95f)

    private fun DrawScope.grupoEn(grupo: Int, x: Float, pie: Float, H: Float, g: MarcoCueva, k: Int) {
        when (grupo) {
            0 -> {
                cestoGrande(g, Offset(x + H * 0.4f, pie), H * 0.8f, H * 0.74f, k)
                nucleo(g, Offset(x + H * 0.86f, pie), H * 0.17f, k)
                percutor(g, Offset(x + H * 0.7f, pie), H * 0.11f, k)
            }
            1 -> {
                astaDePie(g, Offset(x + H * 0.32f, pie), H * 0.88f, k)
                pielesBajas(g, Offset(x + H * 0.5f, pie), H * 0.74f, H * 0.36f, k)
            }
            else -> {
                cuerno(g, Offset(x + H * 0.24f, pie), H * 0.78f, k)
                lamparilla(g, Offset(x + H * 0.6f, pie), H * 0.36f, k)
                terrones(g, Offset(x + H * 0.84f, pie), H * 0.1f, k)
            }
        }
    }

    /** La sombra de lo que pisa, en la cara del zarzo, corrida hacia donde cae la luz. */
    private fun DrawScope.contacto(c: Offset, ancho: Float, alto: Float, l: LuzCueva, g: MarcoCueva) {
        val hacia = if (g.a.oscuro && !g.a.noche) -0.2f else 0.25f
        drawOval(l.s(0.45f), Offset(c.x - ancho * (0.5f - hacia), c.y - alto / 2), Size(ancho, alto))
    }

    /** Hacia dónde le llega la luz a algo del hueco: de día y en los Sueños, de la boca; de noche, del fuego más cercano. */
    private fun hacia(g: MarcoCueva, x: Float): Float = if (g.a.oscuro && !g.a.noche) (if (x > g.w * 0.7f) 1f else -1f) else -1f

    // ---------------------------------------------------------------- las pieles

    /**
     * El cesto de corteza en artesa, grande: una hoja de abedul plegada como una barca (las dos puntas cosidas suben, el borde baja
     * hondo en medio), con su aro de vara cosido con raíz, una franja de ocre pintada y el musgo seco que acolcha lo guardado.
     */
    private fun DrawScope.cestoGrande(g: MarcoCueva, pie: Offset, ancho: Float, alto: Float, k: Int) {
        val u = density
        val l = g.luz(pie.x, pie.y - alto * 0.5f, DELANTE)
        val hc = hacia(g, pie.x)
        val cx = pie.x
        val arriba = ancho * 0.5f; val abajo = arriba * 0.66f
        val top = pie.y - alto
        val yPunta = top; val yMedio = top + alto * 0.36f
        fun borde(x: Float): Float { val t = ((x - cx) / arriba).coerceIn(-1f, 1f); val t2 = t * t; return yMedio - (yMedio - yPunta) * t2 * t2 }
        val pasos = 16
        contacto(pie, ancho * 0.9f, alto * 0.1f, l, g)
        val f = Path().apply {
            moveTo(cx - arriba, yPunta)
            for (i in 1..pasos) { val x = cx - arriba + 2 * arriba * i / pasos; lineTo(x, borde(x)) }
            quadraticTo(cx + arriba * 0.92f, top + alto * 0.62f, cx + abajo, pie.y - 1.5f * u)
            quadraticTo(cx, pie.y + 3f * u, cx - abajo, pie.y - 1.5f * u)
            quadraticTo(cx - arriba * 0.92f, top + alto * 0.62f, cx - arriba, yPunta)
            close()
        }
        val musgo = cerradaCueva(listOf(Offset(cx - arriba * 0.76f, borde(cx - arriba * 0.76f) + 2f * u), Offset(cx - arriba * 0.5f, yMedio - (yMedio - yPunta) * 0.55f),
            Offset(cx - arriba * 0.1f, yMedio - (yMedio - yPunta) * 0.7f), Offset(cx + arriba * 0.34f, yMedio - (yMedio - yPunta) * 0.62f),
            Offset(cx + arriba * 0.72f, borde(cx + arriba * 0.72f) + 2f * u), Offset(cx, yMedio + 2f * u)))
        drawPath(corridoCueva(Path().apply { addPath(f); addPath(musgo) }, -hc * alto * 0.08f, alto * 0.02f), l.s(0.36f))
        bultoRapidoCueva(musgo, l.c(Color(0xFF8E8A4E)), l, hc, 1f, 0.36f)
        val hebras = Path()
        for (j in 0 until 14) {
            val x = cx - arriba * 0.6f + arriba * 1.2f * azar(7500 + k, j)
            val y = yMedio - (yMedio - yPunta) * (0.2f + 0.45f * azar(7510 + k, j))
            hebras.moveTo(x, y); hebras.lineTo(x + 3f * u * (azar(7520 + k, j) - 0.5f), y - 3f * u)
        }
        drawPath(hebras, l.c(Color(0xFFC9C07A)), style = Stroke(1f * u, cap = StrokeCap.Round))
        bultoRapidoCueva(f, l.c(mezcla(P.ABEDUL, P.CUERO, 0.2f)), l, hc, 1.1f, 0.3f)
        // Lo de la corteza, dentro del cesto sin recortar: el medio ancho del cesto a cada altura (los costados casi rectos).
        fun mitad(y: Float) = abajo + (arriba - abajo) * ((pie.y - y) / (pie.y - yMedio)).coerceIn(0f, 1f)
        val lent = Path()
        for (j in 0 until 22) {
            val y = yMedio + (pie.y - yMedio) * (0.08f + 0.86f * azar(7530 + k, j))
            val m = mitad(y) * 0.86f
            val largo = arriba * (0.08f + 0.12f * azar(7550 + k, j))
            val x = cx - m + (2 * m - largo).coerceAtLeast(0f) * azar(7540 + k, j)
            lent.moveTo(x, y); lent.lineTo(x + largo, y + 0.3f * u)
        }
        drawPath(lent, l.c(P.LENTICELA).copy(alpha = 0.66f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // Una franja pintada con ocre rojo y su hilera de puntos de carbón.
        val ya = yMedio + (pie.y - yMedio) * 0.3f; val yb = ya + (pie.y - yMedio) * 0.22f
        val franja = Path().apply {
            moveTo(cx - mitad(ya) * 0.97f, ya); lineTo(cx + mitad(ya) * 0.97f, ya); lineTo(cx + mitad(yb) * 0.97f, yb); lineTo(cx - mitad(yb) * 0.97f, yb); close()
        }
        drawPath(franja, l.c(P.OCRE_ROJO).copy(alpha = 0.85f))
        val puntos = Path()
        val yp = ya + (pie.y - yMedio) * 0.36f
        var xp = cx - mitad(yp) * 0.7f
        while (xp < cx + mitad(yp) * 0.7f) { puntos.addOval(Rect(xp - 1.3f * u, yp - 1.3f * u, xp + 1.3f * u, yp + 1.3f * u)); xp += 5.5f * u }
        drawPath(puntos, l.c(P.CARBON).copy(alpha = 0.8f))
        sombreadoCueva(f, l, hc, 0.8f, 0.2f)
        listOf(-1f, 1f).forEach { lado ->
            val x0 = cx + lado * arriba * 0.84f
            val costura = Path().apply { moveTo(x0, borde(x0) + 1f * u); quadraticTo(cx + lado * arriba * 0.86f, top + alto * 0.55f, cx + lado * abajo * 0.88f, pie.y - 3f * u) }
            drawPath(costura, l.s(0.5f), style = Stroke(1.3f * u))
            val punt = Path()
            for (j in 0 until 6) {
                val t = (j + 0.5f) / 6f
                val y = borde(x0) + (pie.y - borde(x0)) * t * 0.9f
                val xx = x0 + (cx + lado * abajo * 0.88f - x0) * t
                punt.moveTo(xx - 2.2f * u, y - 0.8f * u); punt.lineTo(xx + 2.2f * u, y + 0.8f * u)
            }
            drawPath(punt, l.c(P.CORTEZA_OSC), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        val aro = Path().apply {
            moveTo(cx - arriba, yPunta)
            for (i in 1..pasos) { val x = cx - arriba + 2 * arriba * i / pasos; lineTo(x, borde(x)) }
        }
        val ga = 3.6f * u
        drawPath(corridoCueva(aro, 0f, ga * 0.5f), l.s(0.45f), style = Stroke(ga, cap = StrokeCap.Round))
        drawPath(aro, l.c(mezcla(P.PINO, P.CORTEZA, 0.4f)), style = Stroke(ga, cap = StrokeCap.Round))
        drawPath(corridoCueva(aro, 0f, -ga * 0.25f), l.b(0.5f), style = Stroke(ga * 0.3f, cap = StrokeCap.Round))
        val espiral = Path()
        var x = cx - arriba * 0.9f
        while (x < cx + arriba * 0.9f) {
            val y = borde(x)
            espiral.moveTo(x - ga * 0.4f, y - ga * 0.55f); espiral.lineTo(x + ga * 0.4f, y + ga * 0.55f)
            x += ga * 1.4f
        }
        drawPath(espiral, l.c(P.CORTEZA_OSC), style = Stroke(1.1f * u, cap = StrokeCap.Round))
    }

    /**
     * Dos pieles dobladas, tendidas una sobre otra: la de abajo, de reno pardo; la de arriba, el blanco de invierno. Blandas: la
     * cara de arriba abombada, los costados que se vencen, una pata que cuelga, y el pelo largo que asoma en mechones por el canto.
     */
    private fun DrawScope.pielesBajas(g: MarcoCueva, pie: Offset, ancho: Float, alto: Float, k: Int) {
        val u = density
        val l = g.luz(pie.x, pie.y - alto * 0.5f, DELANTE)
        val hc = hacia(g, pie.x)
        contacto(pie, ancho * 1.05f, alto * 0.16f, l, g)
        val capa = alto * 0.52f
        listOf(Color(0xFF8A6244), Color(0xFFE6DCC6)).forEachIndexed { i, col ->
            val y1 = pie.y - capa * i * 0.92f; val y0 = y1 - capa
            val a = ancho * (0.5f - 0.06f * i); val cx = pie.x + ancho * 0.05f * (if (i == 0) -1f else 1f)
            val f = Path().apply {
                moveTo(cx - a * 0.92f, y1)
                cubicTo(cx - a * 1.08f, y1 - capa * 0.3f, cx - a * 1.02f, y0 + capa * 0.2f, cx - a * 0.8f, y0 + capa * 0.12f)
                quadraticTo(cx, y0 - capa * 0.28f, cx + a * 0.84f, y0 + capa * 0.16f)
                cubicTo(cx + a * 1.04f, y0 + capa * 0.3f, cx + a * 1.06f, y1 - capa * 0.3f, cx + a * 0.9f, y1)
                close()
            }
            bultoRapidoCueva(f, l.c(col), l, hc, 1.1f, 0.42f)
            // El canto del doblez, más oscuro, y los mechones del pelo que asoman por él (óvalos sueltos, no rayas en fila).
            val pelo = l.c(mezcla(col, P.PELO, 0.45f))
            val claro = l.c(mezcla(col, Color.White, 0.25f))
            var x = cx - a * 0.86f; var j = 0
            while (x < cx + a * 0.86f) {
                val rr = capa * (0.07f + 0.06f * azar(7600 + k * 3 + i, j))
                val y = y1 - capa * (0.06f + 0.08f * azar(7610 + k * 3 + i, j))
                drawOval(if (j % 2 == 0) pelo else claro, Offset(x - rr, y - rr * 0.6f), Size(rr * 2f, rr * 1.4f))
                x += capa * (0.1f + 0.08f * azar(7620 + k * 3 + i, j)); j++
            }
            drawLine(l.s(0.45f), Offset(cx - a * 0.8f, y1 - 0.5f * u), Offset(cx + a * 0.8f, y1 - 0.5f * u), 1.4f * u)
        }
        // Una pata de la piel de arriba, que cuelga por el costado.
        val pata = cerradaCueva(listOf(Offset(pie.x + ancho * 0.38f, pie.y - capa * 1.2f), Offset(pie.x + ancho * 0.5f, pie.y - capa * 1.1f),
            Offset(pie.x + ancho * 0.54f, pie.y - capa * 0.4f), Offset(pie.x + ancho * 0.47f, pie.y - capa * 0.3f), Offset(pie.x + ancho * 0.42f, pie.y - capa * 0.8f)))
        bultoRapidoCueva(pata, l.c(Color(0xFFD8CCB4)), l, hc, 1f, 0.3f)
    }

    // ---------------------------------------------------------------- el taller del sílex

    /** Un asta de reno de muda, de pie, apoyada en la pared: la vara curva que sube y sus candiles, la roseta al pie. */
    private fun DrawScope.astaDePie(g: MarcoCueva, pie: Offset, alto: Float, k: Int) {
        val u = density
        val l = g.luz(pie.x, pie.y - alto * 0.5f, DELANTE)
        fun q(x: Float, y: Float) = Offset(pie.x + alto * x, pie.y - alto * y)
        val eje = listOf(q(0f, 0.02f), q(0.04f, 0.3f), q(0.0f, 0.6f), q(-0.06f, 0.82f), q(-0.02f, 0.99f))
        val candiles = listOf(listOf(q(0.02f, 0.14f), q(0.14f, 0.22f), q(0.22f, 0.2f)), listOf(q(0.03f, 0.46f), q(0.15f, 0.56f), q(0.2f, 0.66f)),
            listOf(q(-0.04f, 0.74f), q(-0.16f, 0.82f), q(-0.2f, 0.92f)), listOf(q(-0.05f, 0.88f), q(0.06f, 0.96f)))
        val todo = Path().apply { addPath(abiertaCueva(eje)); candiles.forEach { addPath(abiertaCueva(it)) } }
        val g0 = alto * 0.068f
        drawPath(corridoCueva(todo, alto * 0.06f, alto * 0.02f), l.s(0.32f), style = Stroke(g0, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(todo, l.c(mezcla(P.HUESO_OSC, P.PIEL_OSC, 0.3f)), style = Stroke(g0, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(corridoCueva(todo, -g0 * 0.2f, -g0 * 0.1f), l.c(P.HUESO), style = Stroke(g0 * 0.45f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(corridoCueva(todo, -g0 * 0.28f, -g0 * 0.15f), l.b(0.5f), style = Stroke(g0 * 0.16f, cap = StrokeCap.Round))
        for (j in 0 until 8) drawCircle(l.c(P.HUESO_OSC).copy(alpha = 0.6f), (0.6f + 0.6f * azar(7100 + k, j)) * u, q(0.03f * sin(j * 0.9f), 0.05f + 0.04f * j))
        val roseta = piedraCueva(q(0f, 0.02f), alto * 0.06f, alto * 0.035f, 7110 + k, 9, 0.25f)
        bultoRapidoCueva(roseta, l.c(mezcla(P.HUESO_OSC, P.PIEL_OSC, 0.4f)), l, hacia(g, pie.x), 1f, 0.35f)
        if (l.k > 0.05f) drawPath(corridoCueva(todo, -g0 * 0.3f, 0f), l.filo(0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
    }

    /** El núcleo de sílex, con las cicatrices de las láminas sacadas y la corteza blanca arriba, y dos láminas a su pie. */
    private fun DrawScope.nucleo(g: MarcoCueva, pie: Offset, r: Float, k: Int) {
        val u = density
        val l = g.luz(pie.x, pie.y - r, DELANTE)
        val c = Offset(pie.x, pie.y - r * 0.75f)
        contacto(Offset(pie.x, pie.y), r * 2.4f, r * 0.3f, l, g)
        val f = poligonoCueva(c, r * 0.95f, r * 0.8f, 7200 + k, 7)
        bultoRapidoCueva(f, l.c(P.SILEX), l, hacia(g, pie.x), 1.3f, 0.45f)
        // Las cicatrices de las láminas sacadas y la corteza blanca arriba, dentro del núcleo.
        for (j in 0 until 4) drawLine(l.c(mezcla(P.SILEX, P.CAOLIN, 0.4f)), Offset(c.x - r * 0.4f + j * r * 0.26f, c.y - r * 0.3f), Offset(c.x - r * 0.36f + j * r * 0.27f, c.y + r * 0.45f), 0.9f * u)
        drawOval(l.c(P.CAOLIN), Offset(c.x - r * 0.42f, c.y - r * 0.6f), Size(r * 0.8f, r * 0.3f))
        listOf(-1f, 1f).forEachIndexed { i, s ->
            val a = Offset(pie.x + s * r * 1.1f, pie.y - r * 0.05f)
            val hoja = Path().apply { moveTo(a.x - r * 0.5f, a.y); lineTo(a.x + r * 0.55f, a.y - r * 0.12f * (1 + i)); lineTo(a.x + r * 0.6f, a.y - r * 0.02f); close() }
            drawPath(corridoCueva(hoja, 0.8f * u, 1f * u), l.s(0.35f))
            drawPath(hoja, l.c(mezcla(P.SILEX, P.CAOLIN, 0.3f)))
            drawLine(l.b(0.6f), Offset(a.x - r * 0.4f, a.y - r * 0.03f), Offset(a.x + r * 0.45f, a.y - r * 0.1f), 0.6f * u)
        }
    }

    /** El percutor: un canto rodado, con la cara gastada de golpear. */
    private fun DrawScope.percutor(g: MarcoCueva, pie: Offset, r: Float, k: Int) {
        val l = g.luz(pie.x, pie.y - r, DELANTE)
        val c = Offset(pie.x, pie.y - r * 0.7f)
        contacto(Offset(pie.x, pie.y), r * 2.2f, r * 0.35f, l, g)
        val f = piedraCueva(c, r * 1.1f, r * 0.75f, 7300 + k, 9, 0.08f)
        bultoRapidoCueva(f, l.c(mezcla(P.CALIZA_FRIA, P.CALIZA, 0.4f)), l, hacia(g, pie.x), 1.1f, 0.5f)
        drawOval(l.c(mezcla(P.CALIZA, P.CAOLIN, 0.4f)).copy(alpha = 0.8f), Offset(c.x + r * 0.5f, c.y - r * 0.2f), Size(r * 0.5f, r * 0.5f))
    }

    // ---------------------------------------------------------------- la grasa y la luz

    /** El cuerno de bisonte de pie sobre su boca, usado de recipiente: curvo, oscuro en la punta, con su tapón de cuero. */
    private fun DrawScope.cuerno(g: MarcoCueva, pie: Offset, alto: Float, k: Int) {
        val u = density
        val l = g.luz(pie.x, pie.y - alto * 0.5f, DELANTE)
        val hc = hacia(g, pie.x)
        fun q(x: Float, y: Float) = Offset(pie.x + alto * x, pie.y - alto * y)
        contacto(pie, alto * 0.42f, alto * 0.06f, l, g)
        val f = cerradaCueva(listOf(q(-0.17f, 0f), q(-0.18f, 0.2f), q(-0.14f, 0.45f), q(-0.05f, 0.7f), q(0.08f, 0.9f), q(0.16f, 1f), q(0.13f, 0.88f),
            q(0.07f, 0.66f), q(0.05f, 0.42f), q(0.08f, 0.2f), q(0.17f, 0f)))
        drawPath(corridoCueva(f, -hc * alto * 0.06f, alto * 0.01f), l.s(0.32f))
        bultoRapidoCueva(f, l.c(Color(0xFF8E7558)), l, hc, 1.2f, 0.42f)
        // La punta, negra (la misma silueta, desde donde se oscurece); los anillos de crecimiento, de canto a canto.
        drawPath(cerradaCueva(listOf(q(-0.08f, 0.66f), q(-0.02f, 0.78f), q(0.08f, 0.9f), q(0.16f, 1f), q(0.13f, 0.88f), q(0.08f, 0.72f), q(0.065f, 0.64f))), l.c(Color(0xFF2B221C)))
        val anillos = Path()
        val izq = floatArrayOf(-0.17f, -0.18f, -0.14f, -0.05f); val der = floatArrayOf(0.17f, 0.08f, 0.05f, 0.07f); val alt = floatArrayOf(0f, 0.2f, 0.44f, 0.68f)
        fun borde(t: Float, xs: FloatArray): Float {
            var i = 0
            while (i < 2 && t > alt[i + 1]) i++
            val f0 = ((t - alt[i]) / (alt[i + 1] - alt[i])).coerceIn(0f, 1f)
            return xs[i] + (xs[i + 1] - xs[i]) * f0
        }
        for (j in 0 until 6) {
            val t = 0.1f + 0.09f * j
            anillos.moveTo(pie.x + alto * (borde(t, izq) + 0.02f), pie.y - alto * t); anillos.lineTo(pie.x + alto * (borde(t, der) - 0.02f), pie.y - alto * (t + 0.03f))
        }
        drawPath(anillos, l.c(Color(0xFF5E4A38)).copy(alpha = 0.55f), style = Stroke(0.9f * u))
        // El tapón de cuero atado en la boca, abajo.
        drawRect(l.c(P.CUERO_OSC), Offset(pie.x - alto * 0.18f, pie.y - alto * 0.07f), Size(alto * 0.36f, alto * 0.05f))
        drawLine(l.c(P.CUERO), Offset(pie.x - alto * 0.19f, pie.y - alto * 0.045f), Offset(pie.x + alto * 0.19f, pie.y - alto * 0.04f), 1.6f * u)
    }

    /**
     * La lamparilla del hueco: un cuenco chico de piedra con su sebo y su mecha. De noche arde (es la luz de este hueco: su charco
     * en la roca y en lo que tiene al lado); de día está apagada, tiznada.
     */
    private fun DrawScope.lamparilla(g: MarcoCueva, pie: Offset, ancho: Float, k: Int) {
        val u = density
        val l = g.luz(pie.x, pie.y - ancho * 0.3f, DELANTE)
        val arde = g.a.oscuro && !g.a.noche
        if (arde) {
            mancha(Offset(pie.x, pie.y - ancho * 0.6f), ancho * 2.2f, P.LLAMA.copy(alpha = 0.34f))
            mancha(Offset(pie.x, pie.y - ancho * 0.55f), ancho * 0.9f, P.LLAMA.copy(alpha = 0.3f))
        }
        contacto(pie, ancho * 1.1f, ancho * 0.12f, l, g)
        val a = ancho * 0.5f
        val top = pie.y - ancho * 0.36f
        val f = cerradaCueva(listOf(Offset(pie.x - a, top), Offset(pie.x - a * 0.85f, pie.y - ancho * 0.12f), Offset(pie.x - a * 0.4f, pie.y), Offset(pie.x + a * 0.45f, pie.y),
            Offset(pie.x + a * 0.9f, pie.y - ancho * 0.12f), Offset(pie.x + a, top)))
        val ll = if (arde) g.a.en(maxOf(l.k, 0.85f), P.LLAMA, 0f, 0f, l.amb) else l
        bultoRapidoCueva(f, ll.c(mezcla(P.CALIZA, P.HUESO_OSC, 0.35f)), ll, hacia(g, pie.x), 1f, 0.4f)
        drawOval(ll.c(mezcla(P.CALIZA, P.CALIZA_SOL, 0.4f)), Offset(pie.x - a, top - ancho * 0.08f), Size(ancho, ancho * 0.16f))
        drawOval(ll.c(Color(0xFFE6D6A6)), Offset(pie.x - a * 0.75f, top - ancho * 0.05f), Size(ancho * 0.75f, ancho * 0.1f))
        drawOval(P.HOLLIN.copy(alpha = 0.5f), Offset(pie.x + a * 0.05f, top - ancho * 0.07f), Size(ancho * 0.4f, ancho * 0.1f))
        drawLine(ll.c(P.CARBON), Offset(pie.x + a * 0.25f, top - ancho * 0.02f), Offset(pie.x + a * 0.35f, top - ancho * 0.1f), 1.4f * u, StrokeCap.Round)
        if (arde) llamaCueva(Offset(pie.x + a * 0.32f, top - ancho * 0.08f), ancho * 0.7f, ancho * 0.13f, 20 + k)
    }

    /** Unos terrones de ocre amontonados: rojos y amarillos, de aristas vivas, y el polvo rojo en la vara. */
    private fun DrawScope.terrones(g: MarcoCueva, pie: Offset, r: Float, k: Int) {
        val l = g.luz(pie.x, pie.y - r, DELANTE)
        drawOval(l.c(P.OCRE_ROJO).copy(alpha = 0.5f), Offset(pie.x - r * 2f, pie.y - r * 0.25f), Size(r * 4f, r * 0.5f))
        listOf(Triple(-0.9f, 0.6f, P.OCRE_ROJO), Triple(0.7f, 0.65f, P.OCRE_AMAR), Triple(-0.1f, 1.5f, P.OCRE_NAR), Triple(1.6f, 0.45f, P.OCRE_ROJO)).forEachIndexed { i, (dx, dy, col) ->
            val c = Offset(pie.x + r * dx, pie.y - r * dy)
            val f = poligonoCueva(c, r * 0.95f, r * 0.7f, 7400 + k * 7 + i, 6)
            drawOval(l.s(0.36f), Offset(c.x - r * 0.8f, c.y + r * 0.35f), Size(r * 1.9f, r * 0.45f))
            bultoRapidoCueva(f, l.c(col), l, hacia(g, pie.x), 1.3f, 0.42f)
        }
    }
}
