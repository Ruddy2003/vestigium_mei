package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono

/**
 * Las mulas de la posada, «la Terca, la más vieja», atadas a la argolla cuando no están en el camino: de lado, con la
 * cabeza hacia el muro. Una mula no es un caballo chico: cabeza grande y larga, orejas largas, cuello recto con la crin
 * corta, cascos pequeños, la cola de cerdas solo abajo; el lomo con su montura de carga (de haya forrada de fieltro, con
 * sus ganchos) y, el día de llenar los refugios, la leña. Con su sombra, su lado de luz y, de noche, el filo de la luna
 * arriba y el de la puerta del lado del muro (la noche a dos luces).
 */
internal object MulasAntiguas {
    private val C = ColorAntiguas

    /** Una mula de la argolla: dónde tiene el hocico a lo largo del muro del zaguán, cuánto se aparta del muro, su pelo y si descansa una pata. */
    class Mula(val s: Float, val fuera: Float, val pelo: Color, val descansa: Boolean)

    val MULAS = listOf(Mula(1.12f, 0.95f, C.MULA, false), Mula(1.28f, 0.42f, C.MULA_PARDA, true))

    /** Dónde pisa una mula (bajo la cruz) y su alto hasta la cruz, en el lienzo. */
    fun pieYAlto(m: Mula): Pair<Offset, Float> {
        val k = FachadaPosada.escala(m.s + 1f, m.fuera)
        val hocico = FachadaPosada.p(m.s, 0f, m.fuera)
        val alto = 1.28f * k
        return Offset(hocico.x - 0.88f * alto, FachadaPosada.p(m.s + 1f, 0f, m.fuera).y) to alto
    }

    fun DrawScope.mulas(v: VistaAntiguas, n: Int, cargadas: Boolean) {
        if (n <= 0) return
        for (mu in MULAS.take(n).reversed()) {
            val (pie, alto) = pieYAlto(mu)
            val pe = v.pincelEn(pie + Offset(0f, -alto * 0.6f), lumbre = (v.encendido.sala * v.noche * 0.35f).coerceIn(0f, 1f))
            mula(pe, v, pie, alto, mu.pelo, mu.descansa, cargadas)
            // El ronzal, de la cabeza a la argolla.
            val cab = pie + Offset(0.7f * alto, -alto * 0.96f)
            drawPath(Path().apply { moveTo(cab); cubicTo(cab + Offset(6f, 8f), PuertaAntiguas.ARGOLLA + Offset(-6f, 8f), PuertaAntiguas.ARGOLLA) }, pe.medio(lerp(C.CUERO, C.LANA, 0.3f)), style = Stroke(0.7f, cap = StrokeCap.Round))
        }
    }

    /** La silueta de una mula (para su sombra arrojada): el cuerpo, el cuello, la cabeza y las patas. */
    fun silueta(pie: Offset, alto: Float): Path {
        val u = alto / 100f
        fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        return poligono(listOf(q(-62f, -96f), q(-30f, -104f), q(20f, -100f), q(46f, -112f), q(70f, -118f), q(76f, -96f), q(56f, -94f), q(44f, -66f), q(40f, 0f), q(28f, 0f), q(26f, -56f), q(-36f, -56f), q(-40f, 0f), q(-54f, 0f), q(-64f, -60f)))
    }

    /**
     * Una mula de lado, mirando a la derecha, con [alto] unidades hasta la cruz (u = alto / 100). [descansa]: una pata de
     * atrás doblada. [cargada]: con dos haces de leña sobre la montura.
     */
    fun DrawScope.mula(p: Pincel, v: VistaAntiguas, pie: Offset, alto: Float, pelo: Color, descansa: Boolean, cargada: Boolean) {
        val u = alto / 100f
        fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        val luz = p.luz(lerp(pelo, C.LANA, 0.2f)); val medio = p.por(pelo, 0.05f); val sombra = p.sombra(lerp(pelo, C.ESCORIA, 0.2f))
        val oscura = p.sombra(lerp(pelo, C.ESCORIA, 0.45f))
        fun vestir(forma: Path, y0: Float, y1: Float) {
            drawPath(forma, medio)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(0f to luz, 0.4f to luz.copy(alpha = 0f), 0.62f to sombra.copy(alpha = 0f), 1f to sombra, startY = y0, endY = y1), Offset(pie.x - 90f * u, y0 - 2f), Size(190f * u, y1 - y0 + 4f))
            }
        }
        // La sombra de contacto.
        drawOval(p.sombra(C.TIERRA).copy(alpha = 0.45f), Offset(pie.x - 66f * u, pie.y - 3f * u), Size(118f * u, 6f * u))
        // Las patas de allá, en sombra.
        pata(q(28f, -58f), q(30f, -28f), q(31f, 0f), 6f * u, oscura, u)
        pata(q(-44f, -62f), q(-50f, -30f), q(-46f, 0f), 7f * u, oscura, u)
        // La cola: cerdas solo de media cola para abajo (la de una mula).
        val cola = Path().apply { moveTo(q(-62f, -92f)); cubicTo(q(-70f, -80f), q(-70f, -60f), q(-68f, -44f)); cubicTo(q(-66f, -36f), q(-62f, -34f), q(-60f, -40f)); cubicTo(q(-62f, -58f), q(-62f, -78f), q(-58f, -90f)); close() }
        drawPath(cola, p.medio(lerp(pelo, C.ESCORIA, 0.5f)))
        // El cuerpo: el lomo algo hundido, la barriga redonda, la grupa caída.
        val cuerpo = Path().apply {
            moveTo(q(-64f, -88f)); cubicTo(q(-60f, -100f), q(-44f, -104f), q(-28f, -100f)); cubicTo(q(-10f, -96f), q(10f, -97f), q(24f, -102f))
            cubicTo(q(38f, -104f), q(48f, -96f), q(50f, -84f)); cubicTo(q(52f, -72f), q(46f, -60f), q(34f, -56f)); cubicTo(q(14f, -50f), q(-20f, -50f), q(-38f, -56f))
            cubicTo(q(-52f, -60f), q(-64f, -70f), q(-64f, -88f)); close()
        }
        vestir(cuerpo, q(0f, -104f).y, q(0f, -50f).y)
        clipPath(cuerpo) { mancha(q(-40f, -84f), 16f * u, 14f * u, luz, 0.4f); mancha(q(34f, -80f), 12f * u, 16f * u, luz, 0.3f); mancha(q(0f, -56f), 34f * u, 6f * u, sombra, 0.5f) }
        // Las patas de acá: finas, rectas, con los cascos pequeños; una de atrás en descanso.
        pata(q(36f, -60f), q(38f, -28f), q(40f, 0f), 6.6f * u, medio, u)
        if (descansa) pata(q(-38f, -64f), q(-42f, -26f), q(-36f, -3f), 7.4f * u, medio, u) else pata(q(-38f, -64f), q(-44f, -30f), q(-40f, 0f), 7.6f * u, medio, u)
        // El cuello recto y la crin corta, de cepillo.
        val cuello = poligono(listOf(q(22f, -100f), q(40f, -112f), q(60f, -120f), q(70f, -112f), q(56f, -96f), q(46f, -78f), q(34f, -70f)))
        vestir(cuello, q(0f, -120f).y, q(0f, -70f).y)
        for (k in 0..8) { val a = q(22f + k * 4.6f, -102f - k * 2f); drawLine(p.medio(lerp(pelo, C.ESCORIA, 0.55f)), a, a + Offset(-1.4f * u, -4.4f * u), 1.4f * u, StrokeCap.Round) }
        // La cabeza grande y larga, con su morro claro y las orejas largas.
        val cabeza = Path().apply { moveTo(q(58f, -122f)); cubicTo(q(68f, -126f), q(76f, -120f), q(84f, -106f)); cubicTo(q(90f, -96f), q(92f, -86f), q(86f, -82f)); cubicTo(q(80f, -80f), q(74f, -86f), q(68f, -94f)); cubicTo(q(62f, -100f), q(56f, -106f), q(58f, -122f)); close() }
        vestir(cabeza, q(0f, -126f).y, q(0f, -80f).y)
        mancha(q(86f, -86f), 5f * u, 4f * u, p.por(lerp(pelo, C.LANA, 0.5f), 0.2f), 0.8f)
        for ((dx, cerca) in listOf(-3f to false, 3f to true)) {
            val b = q(60f + dx, -122f)
            drawPath(Path().apply { moveTo(b); cubicTo(b + Offset(-6f * u, -10f * u), b + Offset(-8f * u, -22f * u), b + Offset(-4f * u, -30f * u)); cubicTo(b + Offset(0f, -22f * u), b + Offset(2f * u, -10f * u), b + Offset(5f * u, 0f)); close() }, if (cerca) medio else oscura)
            if (cerca) drawLine(sombra, b + Offset(-1f * u, -4f * u), b + Offset(-3f * u, -24f * u), 1.2f * u, StrokeCap.Round)
        }
        drawCircle(p.medio(C.ESCORIA), 1.8f * u, q(70f, -110f))
        drawCircle(Color.White.copy(alpha = 0.45f), 0.6f * u, q(69.4f, -110.6f))
        // El cabezal.
        val cab = p.medio(lerp(C.CUERO, C.ESCORIA, 0.3f))
        drawLine(cab, q(64f, -118f), q(70f, -96f), 1.4f * u); drawLine(cab, q(70f, -96f), q(86f, -94f), 1.4f * u)
        montura(p, u, ::q, cargada)
        // De noche: el lomo y las orejas con la luz de la luna (la otra luz es la de la puerta, que llega por la derecha).
        if (v.luna > 0.05f) clipPath(cuerpo) { drawPath(Path().apply { moveTo(q(-60f, -96f)); cubicTo(q(-40f, -104f), q(0f, -96f), q(24f, -102f)) }, C.LUNA.copy(alpha = 0.45f * v.luna), style = Stroke(5f * u, cap = StrokeCap.Round)) }
        if (p.calida > 0.03f) {
            val formas = union(listOf(cuerpo, cuello, cabeza))
            clipPath(formas) { drawRect(Brush.horizontalGradient(listOf(Color.Transparent, p.lumbre.copy(alpha = (0.5f * p.calida).coerceIn(0f, 1f))), pie.x, pie.x + 92f * u), Offset(pie.x - 70f * u, pie.y - 130f * u), Size(165f * u, 132f * u), blendMode = BlendMode.Plus) }
        }
    }

    /** Una pata: de [a] (arriba) por [b] (la rodilla o el corvejón) a [c] (el casco), de [g] de ancho arriba; el casco, chico y oscuro. */
    private fun DrawScope.pata(a: Offset, b: Offset, c: Offset, g: Float, col: Color, u: Float) {
        drawPath(poligono(listOf(a + Offset(-g, 0f), a + Offset(g, 0f), b + Offset(g * 0.45f, 0f), c + Offset(g * 0.32f, -3f * u), c + Offset(-g * 0.32f, -3f * u), b + Offset(-g * 0.45f, 0f))), col)
        drawPath(poligono(c + Offset(-g * 0.4f, -3.2f * u), c + Offset(g * 0.5f, -3.2f * u), c + Offset(g * 0.65f, 0.4f), c + Offset(-g * 0.55f, 0.4f)), lerp(col, C.ESCORIA, 0.5f))
    }

    /** La montura de carga: la manta de fieltro, el armazón de haya con sus dos arcos y sus ganchos; con leña, los dos haces atados. */
    private fun DrawScope.montura(p: Pincel, u: Float, q: (Float, Float) -> Offset, cargada: Boolean) {
        val manta = poligono(listOf(q(-26f, -103f), q(14f, -100f), q(18f, -74f), q(-28f, -74f)))
        drawPath(manta, p.por(lerp(C.RUBI, C.CUERO, 0.35f), 0.15f))
        drawLine(p.luz(C.ORO).copy(alpha = 0.5f), q(-28f, -76f), q(18f, -76f), 1f * u)
        val haya = lerp(C.FRESNO, C.MADERA, 0.25f)
        for (x in listOf(-18f, 6f)) drawPath(Path().apply { moveTo(q(x - 6f, -98f)); cubicTo(q(x - 6f, -112f), q(x + 6f, -112f), q(x + 6f, -98f)) }, p.por(haya, 0.3f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawLine(p.por(haya, 0.2f), q(-24f, -104f), q(12f, -104f), 2f * u, StrokeCap.Round)
        for (x in listOf(-22f, 10f)) drawLine(p.medio(C.ESCORIA), q(x, -104f), q(x + 2f, -96f), 0.9f * u, StrokeCap.Round)
        drawLine(p.medio(lerp(C.CUERO, C.ESCORIA, 0.3f)), q(-4f, -100f), q(-6f, -52f), 1.6f * u)
        if (cargada) for (k in 0..1) {
            val y = -112f - k * 8f
            for (j in 0..5) { val a = q(-34f + j * 1.6f, y + j * 0.6f); val b = q(26f + j * 1.2f, y - 2f + j * 0.6f); drawLine(p.por(lerp(C.MADERA, C.FRESNO, 0.3f), 0.2f - j * 0.08f), a, b, 2.2f * u, StrokeCap.Round) }
            drawCircle(p.luz(C.FRESNO), 1.4f * u, q(-34f, y + 1f)); drawLine(p.medio(C.CUERO), q(-12f, y - 3f), q(-12f, y + 4f), 1f * u)
        }
    }
}
