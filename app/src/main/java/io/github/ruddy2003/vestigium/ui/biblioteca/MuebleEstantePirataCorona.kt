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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** Hacia dónde cae la sombra en la pared de algo que está en [p]: de día, a la derecha y abajo; de noche, lejos del candelero. */
internal fun DrawScope.caidaPirata(p: Offset, largo: Float, a: AirePirata): Offset {
    if (!a.oscuro) return Offset(largo * 1.1f, largo * 0.7f)
    val llama = Offset(size.width * MuebleEstantePirata.LAMP_X, size.width * MuebleEstantePirata.LAMP_Y)
    val v = p - llama
    return v / v.getDistance().coerceAtLeast(1f) * (largo * 1.8f)
}

/** La sombra de [forma] en la pared, corrida según la luz y más honda cuanto más cerca está de ella. */
internal fun DrawScope.sombraEnParedPirata(forma: Path, desde: Offset, largo: Float, a: AirePirata, k: Float, fuerza: Float = 0.26f) {
    val c = caidaPirata(desde, largo, a)
    val f = if (a.oscuro) (fuerza * 2.4f * k).coerceAtMost(0.6f) else fuerza
    if (f < 0.02f) return
    translate(c.x, c.y) { drawPath(forma, a.sombra(f)) }
}

/** De qué lado le llega la luz a algo en [x]: de día, de la ventana (izquierda: -1); de noche, del candelero. */
internal fun DrawScope.haciaPirata(x: Float, a: AirePirata): Float =
    if (!a.oscuro) -1f else if (x < size.width * MuebleEstantePirata.LAMP_X) 1f else -1f

/**
 * El bodegón de encima del estante de Pirata ([MuebleEstantePirata]). En el mamparo: la carta de marear clavada (costas
 * inventadas y la red de rumbos, sin letras ni cifras), el tricornio colgado de su cabilla junto a la ventana y el cuerno de
 * pólvora de su clavo. Sobre la cornisa, detrás de la galería: la ampolleta de media hora en su armazón de columnillas (la que
 * voltea el grumete en cada guardia), la jícara con naranjas y limas y la piña de las Antillas, los dos libros de cuentas
 * tumbados con la pistola de chispa encima, el tintero de barro con su pluma y el carretel de la corredera con su barquilla; y
 * a la derecha, el candelero de cardán en su brazo de hierro, con la vela dentro del guardabrisa, que de noche arde.
 */
internal object CoronaEstantePirata {
    private val P = PaletaPirata

    // ---------------------------------------------------------------- en el mamparo

    fun DrawScope.pared(a: AirePirata, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width
        carta(a, Rect(w * 0.3f, w * 0.082f, w * 0.635f, w * 0.345f), cerca)
        tricornio(a, Offset(w * 0.255f, w * 0.095f), w * 0.165f, cerca)
        cuerno(a, Offset(w * 0.71f, w * 0.095f), w, cerca)
    }

    /**
     * La carta de marear clavada con tachuelas: el papel con su borde gastado y una esquina que se levanta, la costa inventada
     * con su aguada y su orla de puntos de los bajos, dos islas, la red de rumbos que sale de dos centros (en tinta, en rojo y
     * en verde, como en las cartas de entonces) y la escala de tramos.
     */
    private fun DrawScope.carta(a: AirePirata, r: Rect, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val k = cerca(r.center)
        val hoja = contornoIrregular(r, 501, 40, 0.008f)
        sombraEnParedPirata(hoja, r.center, w * 0.012f, a, k, 0.3f)
        drawPath(hoja, a.dia(P.PAPEL, 0f, k))
        clipPath(hoja) {
            // El papel amarillea hacia los bordes.
            drawRect(a.luz(P.PAPEL_OSC, k).copy(alpha = 0.4f), r.topLeft, Size(r.width, r.height * 0.06f))
            drawRect(a.luz(P.PAPEL_OSC, k).copy(alpha = 0.35f), Offset(r.left, r.bottom - r.height * 0.08f), Size(r.width, r.height * 0.08f))
            drawRect(a.luz(P.PAPEL_OSC, k).copy(alpha = 0.3f), r.topLeft, Size(r.width * 0.05f, r.height))
            drawRect(a.luz(P.PAPEL_OSC, k).copy(alpha = 0.3f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
            // La red de rumbos, desde dos centros.
            val tinta = a.luz(P.TINTA, k)
            listOf(Offset(r.left + r.width * 0.64f, r.top + r.height * 0.42f) to 0, Offset(r.left + r.width * 0.22f, r.top + r.height * 0.72f) to 1).forEach { (o, i) ->
                val rumbos = Path(); val rojos = Path(); val verdes = Path()
                for (j in 0 until 16) {
                    val ang = Math.toRadians(j * 22.5 + 11.25 * i)
                    val fin = o + Offset(kotlin.math.cos(ang).toFloat(), kotlin.math.sin(ang).toFloat()) * (r.width * 1.2f)
                    val p = when (j % 4) { 0 -> rumbos; 2 -> rojos; else -> verdes }
                    p.moveTo(o.x, o.y); p.lineTo(fin.x, fin.y)
                }
                drawPath(rumbos, tinta.copy(alpha = 0.5f), style = Stroke(0.6f * u))
                drawPath(rojos, a.luz(P.LACRE, k).copy(alpha = 0.5f), style = Stroke(0.6f * u))
                drawPath(verdes, a.luz(Color(0xFF3E6A3A), k).copy(alpha = 0.45f), style = Stroke(0.55f * u))
                drawCircle(tinta.copy(alpha = 0.55f), w * 0.007f, o, style = Stroke(0.7f * u))
            }
            // La costa inventada que entra por la izquierda, con sus ensenadas, y dos islas.
            val costa = Path().apply {
                moveTo(r.left - 2f, r.top + r.height * 0.05f)
                cubicTo(r.left + r.width * 0.2f, r.top + r.height * 0.08f, r.left + r.width * 0.12f, r.top + r.height * 0.24f, r.left + r.width * 0.26f, r.top + r.height * 0.3f)
                cubicTo(r.left + r.width * 0.34f, r.top + r.height * 0.34f, r.left + r.width * 0.3f, r.top + r.height * 0.42f, r.left + r.width * 0.2f, r.top + r.height * 0.44f)
                cubicTo(r.left + r.width * 0.12f, r.top + r.height * 0.47f, r.left + r.width * 0.2f, r.top + r.height * 0.6f, r.left + r.width * 0.1f, r.top + r.height * 0.66f)
                cubicTo(r.left + r.width * 0.04f, r.top + r.height * 0.72f, r.left + r.width * 0.06f, r.top + r.height * 0.86f, r.left - 2f, r.top + r.height * 0.95f)
                close()
            }
            drawPath(costa, a.luz(Color(0xFFB8A46A), k).copy(alpha = 0.6f))
            // El sombreado de la costa (rayitas hacia el agua) y su contorno doble.
            translate(r.width * 0.014f, 0f) { drawPath(costa, a.luz(Color(0xFF6A8AA0), k).copy(alpha = 0.35f), style = Stroke(2.4f * u)) }
            drawPath(costa, tinta.copy(alpha = 0.8f), style = Stroke(1f * u))
            val isla1 = contornoIrregular(Rect(r.left + r.width * 0.48f, r.top + r.height * 0.62f, r.left + r.width * 0.66f, r.top + r.height * 0.76f), 502, 14, 0.22f)
            val isla2 = contornoIrregular(Rect(r.left + r.width * 0.74f, r.top + r.height * 0.14f, r.left + r.width * 0.86f, r.top + r.height * 0.25f), 503, 16, 0.12f)
            for (isla in listOf(isla1, isla2)) {
                drawPath(isla, a.luz(Color(0xFFB8A46A), k).copy(alpha = 0.65f))
                drawPath(isla, tinta.copy(alpha = 0.75f), style = Stroke(0.9f * u))
            }
            // Los puntos de los bajos a lo largo de la costa.
            for (j in 0 until 20) {
                val p = Offset(r.left + r.width * (0.16f + 0.18f * azar(504, j)), r.top + r.height * (0.1f + 0.8f * azar(505, j)))
                drawCircle(tinta.copy(alpha = 0.5f), 0.6f * u, p)
            }
            // La escala de tramos, abajo a la derecha (sin cifras).
            val e0 = Offset(r.left + r.width * 0.58f, r.bottom - r.height * 0.1f)
            for (j in 0 until 5) drawRect(if (j % 2 == 0) tinta.copy(alpha = 0.6f) else a.dia(P.PAPEL, 0f, k), Offset(e0.x + j * r.width * 0.06f, e0.y), Size(r.width * 0.06f, 2.4f * u))
            drawRect(tinta.copy(alpha = 0.6f), Offset(e0.x, e0.y), Size(r.width * 0.3f, 2.4f * u), style = Stroke(0.5f * u))
            // El doblez de la carta, a lo alto, y la arruga de otro.
            drawLine(a.sombra(0.2f), Offset(r.center.x, r.top), Offset(r.center.x + 2f * u, r.bottom), 1.6f * u)
            drawLine(a.claro(0.35f), Offset(r.center.x + 1.6f * u, r.top), Offset(r.center.x + 3.6f * u, r.bottom), 0.9f * u)
            drawLine(a.sombra(0.12f), Offset(r.left, r.center.y + 3f * u), Offset(r.right, r.center.y), 1.2f * u)
        }
        // La esquina de abajo a la derecha, que se levanta.
        val e = Offset(r.right, r.bottom)
        drawPath(poligonoPirata(e.x - r.width * 0.1f, e.y, e.x, e.y - r.height * 0.13f, e.x + 2f, e.y + 2f), a.luz(P.VERDE_OSC, k))
        val doblez = poligonoPirata(e.x - r.width * 0.1f, e.y, e.x, e.y - r.height * 0.13f, e.x - r.width * 0.05f, e.y - r.height * 0.025f)
        drawPath(doblez, a.luz(P.PAPEL_OSC, k))
        drawPath(doblez, a.sombra(0.3f), style = Stroke(0.6f * u))
        // Las tachuelas.
        listOf(Offset(r.left + 5f * u, r.top + 5f * u), Offset(r.right - 5f * u, r.top + 5f * u), Offset(r.left + 5f * u, r.bottom - 5f * u)).forEach {
            remachePirata(it, 2f * u, a, cerca(it), P.LATON)
        }
    }

    /**
     * El tricornio de fieltro colgado de una cabilla clavada en el mamparo, visto de frente: el pico de delante abajo, las dos
     * alas levantadas que suben hacia las puntas de los lados (cada una entre su doblez de abajo y su borde de arriba, ribeteado
     * de galón dorado), la copa redonda que asoma entre ellas y, detrás, el borde del ala de atrás; el botón donde se recoge el
     * ala, la luz en el ala que mira a la luz y la sombra en la pared.
     */
    private fun DrawScope.tricornio(a: AirePirata, cabilla: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val c = cabilla + Offset(0f, ancho * 0.42f)
        val k = cerca(c)
        val ha = haciaPirata(c.x, a)
        fun p(x: Float, y: Float) = Offset(c.x + x * ancho, c.y + y * ancho)
        val izq = p(-0.5f, -0.2f); val der = p(0.5f, -0.2f); val pico = p(0f, 0.2f)
        // El ala de atrás: su borde sube en arco por encima de la copa.
        val atras = Path().apply {
            moveTo(izq.x, izq.y)
            cubicTo(c.x - ancho * 0.4f, c.y - ancho * 0.44f, c.x + ancho * 0.4f, c.y - ancho * 0.44f, der.x, der.y)
            quadraticTo(c.x + ancho * 0.12f, c.y - ancho * 0.12f, pico.x, pico.y)
            quadraticTo(c.x - ancho * 0.12f, c.y - ancho * 0.12f, izq.x, izq.y)
            close()
        }
        val copa = Path().apply {
            moveTo(c.x - ancho * 0.27f, c.y - ancho * 0.08f)
            cubicTo(c.x - ancho * 0.28f, c.y - ancho * 0.33f, c.x - ancho * 0.1f, c.y - ancho * 0.39f, c.x, c.y - ancho * 0.39f)
            cubicTo(c.x + ancho * 0.1f, c.y - ancho * 0.39f, c.x + ancho * 0.28f, c.y - ancho * 0.33f, c.x + ancho * 0.27f, c.y - ancho * 0.08f)
            close()
        }
        // Cada ala de delante: del doblez de abajo (abombado) al borde de arriba (hundido), de la punta del lado al pico.
        fun ala(lado: Float) = Path().apply {
            val punta = if (lado < 0f) izq else der
            moveTo(punta.x, punta.y)
            quadraticTo(c.x + lado * ancho * 0.4f, c.y + ancho * 0.26f, pico.x, pico.y)
            quadraticTo(c.x + lado * ancho * 0.12f, c.y - ancho * 0.12f, punta.x, punta.y)
            close()
        }
        val alaI = ala(-1f); val alaD = ala(1f)
        val silueta = Path().apply { addPath(atras); addPath(alaI); addPath(alaD); addPath(copa) }
        sombraEnParedPirata(silueta, c, ancho * 0.08f, a, k, 0.34f)
        // La cabilla: el mango torneado que asoma del mamparo, tras la copa.
        drawRoundRect(a.luz(P.ROBLE_OSC, k), Offset(cabilla.x - ancho * 0.035f, cabilla.y - ancho * 0.04f), Size(ancho * 0.07f, ancho * 0.12f), CornerRadius(ancho * 0.035f))
        drawPath(atras, a.luz(mezcla(P.FIELTRO, Color(0xFF0E0B09), 0.4f), k))
        drawPath(copa, a.luz(mezcla(P.FIELTRO, Color(0xFF5C5248), 0.3f), k))
        clipPath(copa) {
            val lx = if (ha < 0f) c.x - ancho * 0.24f else c.x + ancho * 0.05f
            drawOval(a.brillo(0.24f, k), Offset(lx, c.y - ancho * 0.37f), Size(ancho * 0.18f, ancho * 0.24f))
            drawRect(a.sombra(0.3f), Offset(if (ha < 0f) c.x + ancho * 0.08f else c.x - ancho * 0.3f, c.y - ancho * 0.4f), Size(ancho * 0.22f, ancho * 0.4f))
        }
        // Las alas, la que mira a la luz más clara; el doblez de abajo en sombra.
        val luzAla = a.luz(mezcla(P.FIELTRO, Color(0xFF8A7C6E), 0.62f), k)
        val sombraAla = a.luz(mezcla(P.FIELTRO, Color(0xFF4A4038), 0.4f), k)
        drawPath(alaI, if (ha < 0f) luzAla else sombraAla)
        drawPath(alaD, if (ha < 0f) sombraAla else luzAla)
        listOf(-1f, 1f).forEach { lado ->
            val punta = if (lado < 0f) izq else der
            val doblez = Path().apply { moveTo(punta.x, punta.y); quadraticTo(c.x + lado * ancho * 0.4f, c.y + ancho * 0.26f, pico.x, pico.y) }
            drawPath(doblez, a.sombra(0.5f), style = Stroke(ancho * 0.03f))
        }
        // El galón dorado: el borde de arriba de las dos alas (en uve hasta el pico) y el del ala de atrás.
        val galon = Path().apply {
            moveTo(izq.x, izq.y); quadraticTo(c.x - ancho * 0.12f, c.y - ancho * 0.12f, pico.x, pico.y)
            quadraticTo(c.x + ancho * 0.12f, c.y - ancho * 0.12f, der.x, der.y)
            moveTo(izq.x, izq.y); cubicTo(c.x - ancho * 0.4f, c.y - ancho * 0.44f, c.x + ancho * 0.4f, c.y - ancho * 0.44f, der.x, der.y)
        }
        drawPath(galon, a.luz(mezcla(P.GALON, P.LATON_OSC, 0.45f), k), style = Stroke(ancho * 0.032f, cap = StrokeCap.Round))
        drawPath(galon, a.luz(P.GALON, k), style = Stroke(ancho * 0.018f, cap = StrokeCap.Round))
        drawPath(galon, a.brillo(0.5f, k), style = Stroke(ancho * 0.008f, cap = StrokeCap.Round))
        // El botón y la presilla que recogen el ala de la izquierda.
        drawLine(a.luz(P.GALON, k), p(-0.3f, -0.12f), p(-0.27f, 0.06f), ancho * 0.014f)
        remachePirata(p(-0.285f, -0.02f), ancho * 0.03f, a, k, P.LATON)
    }

    /**
     * El cuerno de pólvora colgado de su clavo por un cordón: el cuerno curvo con sus vetas, la tapa de madera de la boca ancha,
     * la boquilla de latón con su tapón y su luz.
     */
    private fun DrawScope.cuerno(a: AirePirata, clavo: Offset, w: Float, cerca: (Offset) -> Float) {
        val u = density
        val boca = clavo + Offset(-w * 0.05f, w * 0.13f)
        val punta = clavo + Offset(w * 0.085f, w * 0.17f)
        val c = (boca + punta) / 2f
        val k = cerca(c)
        val cuerpo = Path().apply {
            moveTo(boca.x - w * 0.005f, boca.y - w * 0.028f)
            cubicTo(boca.x + w * 0.05f, boca.y - w * 0.05f, punta.x - w * 0.035f, punta.y - w * 0.04f, punta.x, punta.y - w * 0.006f)
            lineTo(punta.x, punta.y + w * 0.005f)
            cubicTo(punta.x - w * 0.035f, punta.y - w * 0.012f, boca.x + w * 0.05f, boca.y + w * 0.016f, boca.x + w * 0.003f, boca.y + w * 0.03f)
            close()
        }
        sombraEnParedPirata(cuerpo, c, w * 0.011f, a, k, 0.3f)
        // El cordón, de la boca y la punta al clavo.
        val cordon = Path().apply { moveTo(boca.x, boca.y - w * 0.025f); quadraticTo(clavo.x - w * 0.03f, clavo.y + w * 0.05f, clavo.x, clavo.y); quadraticTo(clavo.x + w * 0.04f, clavo.y + w * 0.06f, punta.x - w * 0.015f, punta.y - w * 0.014f) }
        drawPath(cordon, a.luz(P.CABO_OSC, k), style = Stroke(1.6f * u))
        drawPath(cordon, a.luz(P.CABO, k), style = Stroke(0.7f * u))
        remachePirata(clavo, 2f * u, a, k)
        drawPath(cuerpo, a.luz(P.CUERNO, k))
        clipPath(cuerpo) {
            // Las vetas del cuerno, su luz arriba y su sombra abajo.
            for (j in 0 until 7) {
                val x = boca.x + (punta.x - boca.x) * (0.08f + 0.13f * j)
                drawLine(a.luz(Color(0xFF9A7A4A), k).copy(alpha = 0.45f), Offset(x, boca.y - w * 0.06f), Offset(x + w * 0.008f, boca.y + w * 0.06f), (1f + azar(511, j)) * u)
            }
            drawRect(a.sombra(0.32f), Offset(boca.x, c.y + w * 0.004f), Size(punta.x - boca.x, w * 0.05f))
            drawRect(a.brillo(0.4f, k), Offset(boca.x, c.y - w * 0.03f), Size(punta.x - boca.x, w * 0.007f))
        }
        // La tapa de madera de la boca ancha y la boquilla de latón.
        drawOval(a.luz(P.ROBLE_OSC, k), Offset(boca.x - w * 0.012f, boca.y - w * 0.032f), Size(w * 0.02f, w * 0.064f))
        drawOval(a.dia(P.ROBLE, 0.1f, k), Offset(boca.x - w * 0.01f, boca.y - w * 0.027f), Size(w * 0.011f, w * 0.054f))
        remachePirata(Offset(boca.x - w * 0.003f, boca.y), 1.5f * u, a, k, P.LATON)
        drawRoundRect(a.luz(P.LATON, k), Offset(punta.x - w * 0.014f, punta.y - w * 0.008f), Size(w * 0.024f, w * 0.014f), CornerRadius(2f * u))
        drawRect(a.brillo(0.6f, k), Offset(punta.x - w * 0.012f, punta.y - w * 0.007f), Size(w * 0.02f, w * 0.003f))
    }

    // ---------------------------------------------------------------- encima de la cornisa

    fun DrawScope.encima(a: AirePirata, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width
        pina(a, Offset(w * 0.285f, apoyo - w * 0.012f), w * 0.1f, cerca)
        jicara(a, Offset(w * 0.215f, apoyo), w * 0.17f, cerca)
        ampolleta(a, Offset(w * 0.47f, apoyo), w * 0.175f, w * 0.335f, cerca)
        with(MesaEstantePirata) {
            libros(a, Offset(w * 0.665f, apoyo), w, cerca)
            tintero(a, Offset(w * 0.815f, apoyo), w, cerca)
            carretel(a, Offset(w * 0.925f, apoyo), w, cerca)
        }
        with(MuroEstantePirata) { solEncima(a) }
        with(ObraEstantePirata) { galeria(a, apoyo, cerca) }
        with(MesaEstantePirata) { candelero(a, w, cerca) }
    }

    /**
     * La ampolleta de media hora: las dos tapas torneadas de roble claro, cuatro columnillas (las de atrás, tras el vidrio), las
     * dos ampollas soldadas por la cintura con su arena (la de arriba, mediada y hundida en el centro; el hilo que cae; el
     * montón de abajo) y los brillos del vidrio, que de día cogen el sol de la ventana.
     */
    private fun DrawScope.ampolleta(a: AirePirata, pie: Offset, ancho: Float, alto: Float, cerca: (Offset) -> Float) {
        val u = density
        val c = Offset(pie.x, pie.y - alto / 2)
        val k = cerca(c)
        val ha = haciaPirata(pie.x, a)
        val tapa = alto * 0.085f
        val x0 = pie.x - ancho / 2; val x1 = pie.x + ancho / 2
        val arriba = pie.y - alto
        val silueta = poligonoPirata(x0, arriba, x1, arriba, x1, pie.y, x0, pie.y)
        sombraEnParedPirata(silueta, c, ancho * 0.1f, a, k, 0.22f)
        contactoPirata(Offset(pie.x, pie.y - 1f * u), ancho * 1.2f, 6f * u, a, hacia = -ha)
        // Las columnillas de atrás.
        val col = ancho * 0.085f
        listOf(x0 + ancho * 0.26f, x1 - ancho * 0.26f).forEach { x ->
            drawRect(a.luz(P.ROBLE_OSC, k), Offset(x - col * 0.4f, arriba + tapa), Size(col * 0.8f, alto - 2 * tapa))
        }
        // Las ampollas.
        val cx = pie.x; val yA = arriba + tapa; val yB = pie.y - tapa
        val ym = (yA + yB) / 2
        val rb = ancho * 0.31f
        val vidrio = Path().apply {
            moveTo(cx - ancho * 0.06f, yA)
            cubicTo(cx - rb * 1.3f, yA + (ym - yA) * 0.18f, cx - rb * 1.15f, ym - (ym - yA) * 0.22f, cx - ancho * 0.022f, ym)
            cubicTo(cx - rb * 1.15f, ym + (yB - ym) * 0.22f, cx - rb * 1.3f, yB - (yB - ym) * 0.18f, cx - ancho * 0.06f, yB)
            lineTo(cx + ancho * 0.06f, yB)
            cubicTo(cx + rb * 1.3f, yB - (yB - ym) * 0.18f, cx + rb * 1.15f, ym + (yB - ym) * 0.22f, cx + ancho * 0.022f, ym)
            cubicTo(cx + rb * 1.15f, ym - (ym - yA) * 0.22f, cx + rb * 1.3f, yA + (ym - yA) * 0.18f, cx + ancho * 0.06f, yA)
            close()
        }
        drawPath(vidrio, a.luz(mezcla(P.VERDE, P.VIDRIO, 0.45f), k).copy(alpha = 0.62f))
        clipPath(vidrio) {
            // La arena: la de arriba, hundida en el centro; el hilo; el montón de abajo.
            val arenaA = Path().apply {
                moveTo(cx - rb * 1.4f, ym - (ym - yA) * 0.5f)
                quadraticTo(cx, ym - (ym - yA) * 0.18f, cx + rb * 1.4f, ym - (ym - yA) * 0.5f)
                lineTo(cx + rb * 1.4f, ym); lineTo(cx - rb * 1.4f, ym); close()
            }
            drawPath(arenaA, a.luz(P.ARENA, k))
            drawRect(a.sombra(0.2f), Offset(cx - rb * 1.4f, ym - (ym - yA) * 0.18f), Size(rb * 2.8f, (ym - yA) * 0.18f))
            drawLine(a.luz(P.ARENA, k), Offset(cx, ym - 2f * u), Offset(cx, yB - (yB - ym) * 0.46f), 1.5f * u)
            val monton = Path().apply {
                moveTo(cx - rb * 1.4f, yB); lineTo(cx - rb * 1.4f, yB - (yB - ym) * 0.2f)
                quadraticTo(cx - rb * 0.3f, yB - (yB - ym) * 0.32f, cx, yB - (yB - ym) * 0.48f)
                quadraticTo(cx + rb * 0.3f, yB - (yB - ym) * 0.32f, cx + rb * 1.4f, yB - (yB - ym) * 0.2f)
                lineTo(cx + rb * 1.4f, yB); close()
            }
            drawPath(monton, a.luz(P.ARENA, k))
            drawPath(poligonoPirata(cx, yB - (yB - ym) * 0.48f, cx + rb * 1.4f, yB - (yB - ym) * 0.2f, cx + rb * 1.4f, yB, cx + rb * 0.2f, yB), a.sombra(0.22f))
            // La luz y la sombra redonda de las ampollas.
            val ladoLuz = if (ha < 0f) cx - rb * 1.0f else cx + rb * 0.4f
            drawRect(a.brillo(0.3f, k), Offset(ladoLuz, yA), Size(rb * 0.55f, yB - yA))
            drawRect(a.sombra(0.22f), Offset(if (ha < 0f) cx + rb * 0.45f else cx - rb * 1.3f, yA), Size(rb * 0.85f, yB - yA))
        }
        drawPath(vidrio, a.luz(P.VIDRIO, k).copy(alpha = 0.8f), style = Stroke(1.4f * u))
        // Los brillos del vidrio.
        val bx = if (ha < 0f) cx - rb * 0.66f else cx + rb * 0.55f
        val blanco = Color.White.copy(alpha = if (a.oscuro) (0.8f * k).coerceAtMost(0.7f) else 0.9f)
        drawLine(blanco, Offset(bx, yA + (ym - yA) * 0.2f), Offset(bx + ha * rb * 0.1f, ym - (ym - yA) * 0.32f), 2.6f * u, StrokeCap.Round)
        drawLine(blanco.copy(alpha = blanco.alpha * 0.8f), Offset(bx, ym + (yB - ym) * 0.3f), Offset(bx + ha * rb * 0.08f, yB - (yB - ym) * 0.18f), 2.2f * u, StrokeCap.Round)
        destelloPirata(Offset(bx + ha * rb * 0.12f, yA + (ym - yA) * 0.18f), 2f * u, a, k)
        // Las tapas torneadas, arriba y abajo: el plato, la moldura y el canto.
        listOf(arriba to true, pie.y - tapa to false).forEach { (y, sup) ->
            val t = Rect(x0, y, x1, y + tapa)
            drawRoundRect(a.luz(P.ROBLE, k), t.topLeft, t.size, CornerRadius(tapa * 0.35f))
            val m = Rect(x0 + ancho * 0.06f, if (sup) t.bottom - tapa * 0.34f else t.top, x1 - ancho * 0.06f, if (sup) t.bottom else t.top + tapa * 0.34f)
            drawRect(a.luz(P.ROBLE_OSC, k), m.topLeft, m.size)
            drawRect(a.dia(P.ROBLE_LUZ, 0.25f, k), Offset(t.left + tapa * 0.2f, t.top + tapa * (if (sup) 0.1f else 0.42f)), Size(t.width - tapa * 0.4f, tapa * 0.18f))
            drawRect(a.sombra(0.32f), Offset(t.left, t.bottom - tapa * 0.16f), Size(t.width, tapa * 0.16f))
            val fl = if (ha < 0f) t.left else t.right - tapa * 0.3f
            drawRect(a.brillo(0.25f, k), Offset(fl, t.top), Size(tapa * 0.3f, t.height))
        }
        // Las columnillas de delante, torneadas: cuerpo, anillos y su luz.
        listOf(x0 + ancho * 0.08f, x1 - ancho * 0.08f).forEach { x ->
            val cr = Rect(x - col / 2, arriba + tapa, x + col / 2, pie.y - tapa)
            drawRect(a.luz(P.ROBLE, k), cr.topLeft, cr.size)
            cilindroPirata(cr, a, k, ha, 1.3f)
            for (j in 0 until 5) {
                val y = cr.top + cr.height * (0.04f + 0.23f * j)
                drawOval(a.luz(P.ROBLE_OSC, k), Offset(x - col * 0.8f, y), Size(col * 1.6f, col * 0.7f))
                drawOval(a.dia(P.ROBLE_LUZ, 0.25f, k), Offset(x - col * 0.65f, y), Size(col * 1.3f, col * 0.28f))
            }
        }
    }

    /** La piña de las Antillas, de pie detrás de la jícara: el cuerpo dorado con su retícula de escamas y su corona de hojas. */
    private fun DrawScope.pina(a: AirePirata, pie: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val alto = ancho * 1.35f
        val cuerpo = Rect(pie.x - ancho / 2, pie.y - alto, pie.x + ancho / 2, pie.y)
        val k = cerca(cuerpo.center)
        val ha = haciaPirata(pie.x, a)
        // La corona de hojas, en abanico.
        val hojas = Path()
        for (j in 0 until 11) {
            val ang = Math.toRadians(-90.0 + (j - 5) * 13.0)
            val largo = ancho * (0.85f + 0.5f * azar(521, j)) * (if (j == 5) 1.25f else 1f)
            val base = Offset(pie.x + (j - 5) * ancho * 0.035f, cuerpo.top + ancho * 0.08f)
            val punta = base + Offset(kotlin.math.cos(ang).toFloat(), kotlin.math.sin(ang).toFloat()) * largo
            val n = Offset(kotlin.math.sin(ang).toFloat(), -kotlin.math.cos(ang).toFloat()) * (ancho * 0.07f)
            hojas.moveTo(base.x - n.x, base.y - n.y)
            hojas.quadraticTo((base.x + punta.x) / 2 + n.x * 0.3f, (base.y + punta.y) / 2, punta.x, punta.y)
            hojas.quadraticTo((base.x + punta.x) / 2 - n.x * 0.3f, (base.y + punta.y) / 2, base.x + n.x, base.y + n.y)
            hojas.close()
        }
        sombraEnParedPirata(hojas, cuerpo.center, ancho * 0.12f, a, k, 0.26f)
        val forma = Path().apply { addOval(cuerpo) }
        sombraEnParedPirata(forma, cuerpo.center, ancho * 0.12f, a, k, 0.26f)
        drawPath(hojas, a.luz(P.HOJA, k))
        drawPath(hojas, a.luz(mezcla(P.HOJA, P.VERDE_HONDO, 0.5f), k), style = Stroke(0.9f * u))
        drawPath(forma, a.luz(P.PINA, k))
        clipPath(forma) {
            // La retícula de escamas en diagonal, con su punto en cada una.
            val red = Path()
            var x = cuerpo.left - cuerpo.height
            val paso = ancho * 0.2f
            while (x < cuerpo.right + cuerpo.height) {
                red.moveTo(x, cuerpo.bottom); red.lineTo(x + cuerpo.height * 0.8f, cuerpo.top)
                red.moveTo(x, cuerpo.top); red.lineTo(x + cuerpo.height * 0.8f, cuerpo.bottom)
                x += paso
            }
            drawPath(red, a.luz(P.PINA_OSC, k), style = Stroke(1.2f * u))
            for (i in 0 until 5) for (j in 0 until 7) {
                val q = Offset(cuerpo.left + cuerpo.width * (0.1f + 0.2f * i + (if (j % 2 == 0) 0.1f else 0f)), cuerpo.top + cuerpo.height * (0.07f + 0.14f * j))
                drawCircle(a.luz(Color(0xFF6E8A2E), k), 1.2f * u, q)
            }
            drawRect(a.brillo(0.3f, k), Offset(if (ha < 0f) cuerpo.left else cuerpo.right - cuerpo.width * 0.3f, cuerpo.top), Size(cuerpo.width * 0.3f, cuerpo.height))
            drawRect(a.sombra(0.34f), Offset(if (ha < 0f) cuerpo.right - cuerpo.width * 0.34f else cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.34f, cuerpo.height))
            drawRect(a.sombra(0.25f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.2f), Size(cuerpo.width, cuerpo.height * 0.2f))
        }
    }

    /**
     * La jícara (media calabaza) con naranjas agrias y limas: el cuenco con su labio claro y su veta, la fruta amontonada (una
     * lima partida, con sus gajos) y su sombra en la cornisa.
     */
    private fun DrawScope.jicara(a: AirePirata, pie: Offset, ancho: Float, cerca: (Offset) -> Float) {
        val u = density
        val alto = ancho * 0.38f
        val k = cerca(pie - Offset(0f, alto))
        val ha = haciaPirata(pie.x, a)
        contactoPirata(Offset(pie.x, pie.y - 1f * u), ancho * 1.1f, 6f * u, a, hacia = -ha)
        // La fruta de atrás, que asoma por encima del labio: naranjas y limas.
        val r = ancho * 0.15f
        val frutas = listOf(Triple(-0.25f, -0.95f, true), Triple(0.04f, -1.15f, false), Triple(0.3f, -0.92f, true), Triple(-0.04f, -0.8f, false))
        frutas.forEachIndexed { j, (x, y, naranja) -> fruta(pie + Offset(x * ancho, y * alto), r * (0.95f + 0.12f * azar(531, j)), a, k, ha, naranja) }
        // El cuenco.
        val cuenco = Path().apply {
            moveTo(pie.x - ancho / 2, pie.y - alto * 0.78f)
            cubicTo(pie.x - ancho * 0.48f, pie.y - alto * 0.1f, pie.x - ancho * 0.25f, pie.y, pie.x, pie.y)
            cubicTo(pie.x + ancho * 0.25f, pie.y, pie.x + ancho * 0.48f, pie.y - alto * 0.1f, pie.x + ancho / 2, pie.y - alto * 0.78f)
            close()
        }
        drawPath(cuenco, a.luz(P.JICARA, k))
        clipPath(cuenco) {
            for (j in 0 until 6) drawLine(a.luz(Color(0xFF6A4420), k).copy(alpha = 0.45f), Offset(pie.x - ancho * 0.45f + ancho * 0.18f * j, pie.y - alto), Offset(pie.x - ancho * 0.32f + ancho * 0.13f * j, pie.y), 0.9f * u)
            drawRect(a.brillo(0.32f, k), Offset(if (ha < 0f) pie.x - ancho * 0.45f else pie.x + ancho * 0.2f, pie.y - alto), Size(ancho * 0.22f, alto))
            drawRect(a.sombra(0.34f), Offset(if (ha < 0f) pie.x + ancho * 0.16f else pie.x - ancho * 0.5f, pie.y - alto), Size(ancho * 0.34f, alto))
        }
        drawOval(a.dia(Color(0xFFD8B884), 0f, k), Offset(pie.x - ancho / 2, pie.y - alto * 0.86f), Size(ancho, alto * 0.16f), style = Stroke(2f * u))
        // La lima partida, delante, apoyada en el labio.
        val o = pie + Offset(ancho * 0.32f, -alto * 0.72f)
        drawCircle(a.luz(P.LIMA_OSC, k), r * 0.95f, o)
        drawCircle(a.dia(Color(0xFFDDE6A0), 0f, k), r * 0.8f, o)
        val gajos = Path()
        for (j in 0 until 7) {
            val ang = Math.toRadians(j * 360.0 / 7 + 10)
            gajos.moveTo(o.x, o.y); gajos.lineTo(o.x + kotlin.math.cos(ang).toFloat() * r * 0.7f, o.y + kotlin.math.sin(ang).toFloat() * r * 0.7f)
        }
        drawPath(gajos, a.luz(Color(0xFFB8C870), k), style = Stroke(0.9f * u))
        drawCircle(a.brillo(0.45f, k), r * 0.28f, o + Offset(-r * 0.3f, -r * 0.3f))
    }

    /** Una naranja agria o una lima: la esfera con su luz, su sombra, los poros y el brillo de la cáscara. */
    private fun DrawScope.fruta(o: Offset, r: Float, a: AirePirata, k: Float, ha: Float, naranja: Boolean) {
        val base = if (naranja) Color(0xFFE0862A) else P.LIMA
        val honda = if (naranja) Color(0xFF9A4A16) else P.LIMA_OSC
        drawCircle(a.luz(base, k), r, o)
        drawCircle(a.luz(honda, k).copy(alpha = 0.7f), r * 0.78f, o + Offset(-ha * r * 0.28f, r * 0.26f))
        drawCircle(a.luz(base, k), r * 0.6f, o + Offset(ha * r * 0.15f, -r * 0.15f))
        drawCircle(a.brillo(0.65f, k), r * 0.2f, o + Offset(ha * r * 0.4f, -r * 0.42f))
        drawCircle(a.luz(Color(0xFF4A3A1A), k), r * 0.07f, o + Offset(0f, -r * 0.82f))
    }

}
