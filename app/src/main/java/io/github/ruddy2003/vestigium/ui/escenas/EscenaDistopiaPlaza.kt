package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDistopia.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.brasas
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cable
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoMojado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * La plaza, en primer plano: las losas y lo que hay plantado en ellas. Es la misma en la capital y en el pueblo de la
 * hermana (allí, sin cámaras ni bidón, y con el cartel descolorido).
 */
internal object PlazaDistopia {
    private val C = ColorDistopia
    private const val H = EscenaDistopia.H
    private const val VX = EscenaDistopia.VX
    private const val BORDE = CiudadDistopia.BORDE

    /** Hasta dónde llegan, plaza adentro, las sombras de las cosas. */
    private const val FONDO = 196f

    private val POSTE = Offset(62f, 264f)
    private const val POSTE_CIMA = 26f
    private val COLUMNA = Offset(108f, 271f)
    private val BANCO = Rect(124f, 227f, 222f, 281f)
    private val SOBRE = Offset(172f, 251f)
    private const val KO = 1.5f
    private val JARDINERA = Rect(306f, 238f, 374f, 275f)
    private val CARTEL = Rect(300f, 102f, 392f, 160f)
    private val TAPA = Offset(286f, 264f)

    // ---------------------------------------------------------------- Ayudas de pintura

    /** Lo que pinta una cosa de la plaza: el sol le da su lado claro y su lado en sombra, y de noche la tiñen las luces que tenga cerca. */
    private class Pincel(val l: Luces, val r: Rayo, val a: Alumbrado) {
        /** El color [propio] de una cara que está en [p] y mira a la izquierda (−1), de frente (0) o a la derecha (1). */
        fun cara(p: Offset, propio: Color, mira: Float = 0f): Color {
            val hacia = if (mira == 0f) CiudadDistopia.frente(r) else CiudadDistopia.lado(r, mira)
            val c = if (hacia >= 0f) lerp(propio, C.LUZ, 0.3f * hacia * r.contraste) else mult(propio, r.color, 0.9f * -hacia * r.contraste)
            return a.en(p, c, mira)
        }

        /** Una cara de arriba: recibe toda la luz. */
        fun arriba(p: Offset, propio: Color): Color = a.en(p, lerp(propio, C.LUZ, 0.34f * r.dia * r.contraste))

        val deNoche: Boolean get() = a.focos.isNotEmpty() && l.noche > 0.45f
    }

    /** Un cuerpo redondo: de día, con el volumen del sol; de noche, con un filo de cada luz. */
    private fun DrawScope.redondo(p: Pincel, forma: Path, x0: Float, x1: Float, y: Float, propio: Color) {
        if (p.deNoche) cuerpo(p.a, forma, x0, x1, y, propio) else volumen(p.l, p.r, forma, x0, x1, propio)
    }

    // ---------------------------------------------------------------- La parte quieta

    fun DrawScope.plaza(m: Momento, l: Luces, r: Rayo, a: Alumbrado) {
        val p = Pincel(l, r, a)
        val capital = EscenaDistopia.lugar(m) == Lugar.CAPITAL
        suelo(m, p)
        val cosas = cosasDeLaPlaza(m, p).sortedBy { it.pie.y }
        // Las sombras del sol sobre las losas: la de cada cosa.
        capaSombra(r) { c -> if (capital) sombraDelTestero(r, c); cosas.forEach { sombraArrojada(r, it.forma, it.pie, c, FONDO, FONDO) } }
        // De noche, cada cosa arroja la sombra de la luz que tiene más cerca.
        if (p.deNoche) capaSombra(rayoFijo(0.5f, 50f, 0.5f * l.noche, C.SOMBRA)) { c ->
            cosas.forEach { k -> val f = a.focos.maxByOrNull { it.fuerza / (1f + ((it.p.x - k.pie.x) * (it.p.x - k.pie.x)) / (it.alcance * it.alcance)) }!!; sombraArrojada(rayoDeFoco(f, k.pie, 0.5f, c), k.forma, k.pie, c, FONDO, FONDO) }
        }
        if (p.deNoche) lucesEnElSuelo(m, p)
        cables(m, p)
        cosas.forEach { it.pintar(this) }
        // Cada cosa da sombra sobre las que tiene detrás.
        capaSombra(r, 0.7f) { c -> cosas.forEachIndexed { i, k -> clipPath(k.forma) { if (r.ay < -0.02f) for (j in i + 1 until cosas.size) { val sep = cosas[j].pie.y - k.pie.y; if (sep > 0.5f) translate(r.ax * sep / -r.ay, sep / -r.ay) { drawPath(cosas[j].forma, c) } } } } }
        if (capital) camaras(m, p)
        if (EscenaDistopia.fiesta(m)) bombillas(m, p)
    }

    /**
     * La sombra del bloque de la izquierda sobre la plaza, cuando el sol está de ese lado (por la tarde): una cuña que
     * sale del pie del testero y se alarga hacia la derecha a medida que el sol baja. **[Aproximación]**: las demás
     * sombras del cuadro se tumban hacia el fondo (la luz entra un poco de frente); esta cae además hacia quien mira, que
     * es lo que haría un bloque tan alto con el sol detrás de él. Va en la misma capa que las sombras de las cosas.
     */
    private fun DrawScope.sombraDelTestero(r: Rayo, c: Color) {
        if (r.ax <= 0.05f) return
        val pie = gy(0.92f); val esquina = VX - 110f * 0.92f
        val alcance = (r.ax * 160f).coerceAtMost(260f); val cae = (r.ax * 26f).coerceAtMost(34f)
        clipRect(0f, pie, ANCHO, ALTO) { sombra(poligono(Offset(-4f, pie - 1f), Offset(esquina, pie - 1f), Offset(esquina + alcance, pie + cae), Offset(-4f, pie + cae)), c) }
    }

    /** La x en pantalla de un punto del suelo que está a [X] del eje de la avenida, a la profundidad [u]. */
    private fun px(X: Float, u: Float) = VX + X * u
    private fun gy(u: Float) = CiudadDistopia.gy(u)

    /**
     * El suelo de la plaza: losas de hormigón que huyen hacia la avenida, cada una de su tono; juntas con hierba, grietas,
     * manchas de aceite, las marcas pintadas donde se forma en los actos, papeles del día y, si llueve, los charcos.
     */
    private fun DrawScope.suelo(m: Momento, p: Pincel) {
        val l = p.l
        val az = Azar(Semilla.de("distopia", "losas"))
        val mojado = EscenaDistopia.mojado(m); val nieve = EscenaDistopia.nevado(m)
        val base = if (nieve) lerp(C.LOSA, C.NIEVE, 0.66f) else if (mojado) lerp(C.LOSA, C.ASFALTO, 0.45f) else C.LOSA
        drawRect(l.tono(lerp(base, C.HORMIGON_OSC, 0.3f)), Offset(0f, BORDE), Size(ANCHO, ALTO - BORDE))
        val filas = listOf(1f, 1.1f, 1.22f, 1.37f, 1.55f, 1.77f, 2.04f)
        for (f in 0 until filas.size - 1) {
            val u0 = filas[f]; val u1 = filas[f + 1]
            var X = -150f + (f % 2) * 14f
            while (X < 170f) {
                val w = az.entre(24f, 34f)
                val q = listOf(Offset(px(X, u0), gy(u0)), Offset(px(X + w, u0), gy(u0)), Offset(px(X + w, u1), gy(u1)), Offset(px(X, u1), gy(u1)))
                val tono = lerp(base, listOf(C.HORMIGON, C.HORMIGON_OSC, Color(0xFFC4BEA8), C.LOSA)[az.entero(4)], az.real() * 0.55f)
                val centro = Offset((q[0].x + q[2].x) / 2f, (q[0].y + q[2].y) / 2f)
                drawPath(poligono(q), Brush.verticalGradient(listOf(l.tono(lerp(tono, C.HORMIGON_OSC, 0.16f)), l.tono(tono)), gy(u0), gy(u1)))
                // El filo claro de arriba y la junta oscura de abajo y del costado.
                drawLine(l.tono(C.LUZ).copy(alpha = 0.22f), q[0] + Offset(0f, 0.5f), q[1] + Offset(0f, 0.5f), 0.4f)
                drawLine(l.tono(C.HOLLIN).copy(alpha = 0.5f), q[3], q[2], 0.5f + 0.25f * f); drawLine(l.tono(C.HOLLIN).copy(alpha = 0.42f), q[1], q[2], 0.4f + 0.2f * f)
                // Lo que el uso le ha dejado a cada losa: una grieta, una esquina rota, un parche.
                val suerte = az.real()
                if (suerte < 0.24f) { var g = q[0] + Offset(az.entre(2f, w * u0 * 0.7f), 0.6f); val grieta = Path().apply { moveTo(g.x, g.y); repeat(4) { g += Offset(az.entre(-3f, 4f) * u0, (gy(u1) - gy(u0)) / 4f); lineTo(g.x, g.y) } }; drawPath(grieta, l.tono(C.HOLLIN).copy(alpha = 0.55f), style = Stroke(0.45f)) }
                else if (suerte < 0.34f) drawPath(poligono(q[2], q[2] + Offset(-az.entre(3f, 7f) * u1, 0f), q[2] + Offset(0f, -az.entre(2f, 4f))), l.tono(lerp(C.HOLLIN, C.OXIDO, 0.3f)).copy(alpha = 0.7f))
                else if (suerte < 0.42f) drawPath(poligono(q[0] + Offset(2f, 1f), q[1] + Offset(-w * u0 * 0.4f, 1f), q[2] + Offset(-w * u1 * 0.45f, -1f), q[3] + Offset(3f, -1f)), l.tono(C.ASFALTO).copy(alpha = 0.5f))
                X += w
            }
        }
        // Las manchas largas de lo pisado, de aceite y de humedad.
        repeat(22) { val u = az.entre(1f, 2f); mancha(Offset(az.entre(0f, ANCHO), gy(u)), az.entre(10f, 34f) * u * 0.6f, az.entre(1.2f, 3f) * u * 0.6f, l.tono(listOf(C.HOLLIN, C.HORMIGON, C.OXIDO)[it % 3]), 0.16f) }
        // Las marcas de formar: cuadros pintados en filas, gastados por las botas.
        if (!nieve) for ((f, u) in listOf(1.16f, 1.46f, 1.9f).withIndex()) {
            var X = -132f + f * 9f
            while (X < 150f) { val c = Offset(px(X, u), gy(u)); if (az.real() > 0.2f) drawPath(poligono(c + Offset(-3f * u, -1f * u), c + Offset(3f * u, -1f * u), c + Offset(3.2f * u, 1f * u), c + Offset(-3.2f * u, 1f * u)), l.tono(C.HUESO).copy(alpha = az.entre(0.14f, 0.42f))); X += 34f }
        }
        // La hierba de las juntas y, en otoño, las hojas de los árboles de la avenida.
        if (!nieve && m.ficha.estacion != Estacion.INVIERNO) repeat(40) { val u = filas[1 + az.entero(5)]; val c = Offset(az.entre(0f, ANCHO), gy(u)); val verde = l.tono(lerp(Color(0xFF7C8A56), C.HORMIGON_OSC, 0.3f)); repeat(3) { drawLine(verde, c + Offset(it * 0.9f, 0f), c + Offset(it * 0.9f + az.entre(-1f, 1f), -az.entre(1.4f, 3.6f)), 0.5f) } }
        if (m.ficha.estacion == Estacion.OTONO) { val h = m.azar("hojas"); repeat(26) { val c = Offset(h.entre(150f, ANCHO), h.entre(BORDE + 2f, ALTO - 2f)); val ang = h.entre(0f, 3.1f); drawLine(l.tono(listOf(Color(0xFFB08A48), Color(0xFF9A6A3A), Color(0xFFC0A860))[it % 3]), c, c + Offset(cos(ang) * 2.6f, sin(ang) * 1.1f), 1.1f, StrokeCap.Round) } }
        // La tapa de la alcantarilla y el sumidero.
        drawOval(l.tono(C.HOLLIN), Offset(TAPA.x - 9.6f, TAPA.y - 3.6f), Size(19.2f, 7.2f)); drawOval(l.tono(C.HIERRO), Offset(TAPA.x - 8.4f, TAPA.y - 3.1f), Size(16.8f, 6.2f))
        clipPath(Path().apply { addOval(Rect(TAPA.x - 8.4f, TAPA.y - 3.1f, TAPA.x + 8.4f, TAPA.y + 3.1f)) }) { for (i in -4..4) drawLine(l.tono(C.HOLLIN).copy(alpha = 0.7f), TAPA + Offset(i * 2.2f - 3f, -3.4f), TAPA + Offset(i * 2.2f + 3f, 3.4f), 0.5f) }
        drawLine(l.tono(C.LUZ).copy(alpha = 0.3f), TAPA + Offset(-7f, -2.2f), TAPA + Offset(3f, -3f), 0.4f)
        val sumidero = Rect(160f, 213f, 178f, 217.6f)
        drawPath(poligono(Offset(sumidero.left + 1.6f, sumidero.top), Offset(sumidero.right - 1f, sumidero.top), Offset(sumidero.right, sumidero.bottom), Offset(sumidero.left, sumidero.bottom)), l.tono(C.HOLLIN))
        for (i in 0..5) drawLine(l.tono(C.HIERRO), Offset(sumidero.left + 2.4f + i * 2.8f, sumidero.top + 0.4f), Offset(sumidero.left + 1.6f + i * 3f, sumidero.bottom - 0.4f), 0.8f)
        // Los papeles de hoy: octavillas y hojas del periódico que el viento arrima a las cosas. Cada día caen en otro sitio.
        if (!nieve) { val hoy = m.azar("papeles"); repeat(3 + hoy.entero(5)) {
            val c = Offset(hoy.entre(10f, ANCHO - 10f), hoy.entre(BORDE + 6f, ALTO - 4f)); val ang = hoy.entre(-0.5f, 0.5f); val w = hoy.entre(4f, 7f)
            val hoja = poligono(c, c + Offset(cos(ang) * w, sin(ang) * w * 0.4f), c + Offset(cos(ang) * w - 1f, sin(ang) * w * 0.4f + 2.2f), c + Offset(-1.2f, 2f))
            drawPath(hoja, l.tono(if (it % 3 == 0) C.ROJO else C.HUESO).copy(alpha = if (mojado) 0.6f else 0.92f)); drawLine(l.tono(C.HOLLIN).copy(alpha = 0.3f), c + Offset(0.6f, 1f), c + Offset(cos(ang) * w * 0.8f, sin(ang) * w * 0.3f + 1f), 0.3f) } }
        // La lluvia: el suelo devuelve el cielo, y en los hoyos se hacen charcos.
        if (mojado) {
            drawRect(Brush.verticalGradient(listOf(l.bajo.copy(alpha = 0.26f), l.medio.copy(alpha = 0.1f)), BORDE, ALTO), Offset(0f, BORDE), Size(ANCHO, ALTO - BORDE))
            for ((c, w) in listOf(Offset(168f, 226f) to 54f, Offset(296f, 280f) to 44f, Offset(34f, 276f) to 40f, Offset(256f, 236f) to 26f)) {
                val charco = Path().apply { moveTo(c.x - w / 2f, c.y); cubicTo(c.x - w * 0.4f, c.y - 3.6f, c.x + w * 0.1f, c.y - 2.4f, c.x + w * 0.3f, c.y - 3f); cubicTo(c.x + w * 0.56f, c.y - 2f, c.x + w * 0.5f, c.y + 2.4f, c.x + w * 0.2f, c.y + 2.6f); cubicTo(c.x - w * 0.1f, c.y + 3.4f, c.x - w * 0.46f, c.y + 2.6f, c.x - w / 2f, c.y); close() }
                drawPath(charco, l.tono(C.HOLLIN).copy(alpha = 0.5f), style = Stroke(1.2f)); drawPath(charco, Brush.verticalGradient(listOf(lerp(l.bajo, l.medio, 0.3f), lerp(l.medio, l.alto, 0.5f)), c.y - 3.6f, c.y + 3.4f))
                drawLine(Color.White.copy(alpha = 0.3f * (1f - l.noche)), c + Offset(-w * 0.3f, -0.8f), c + Offset(w * 0.12f, -1f), 0.5f)
            }
            // El poste y el cartel, en el agua.
            reflejoRoto(POSTE.x - 3f, POSTE.x + 3f, POSTE.y + 2f, 18f, l.tono(C.HIERRO), 0.4f, az, 1.2f)
            reflejoRoto(316f, 372f, 274f, 9f, l.tono(C.HORMIGON_OSC), 0.3f, az, 1.2f)
        }
        if (nieve) {
            // La nieve cuaja en las losas, menos donde se pisa: quedan las veredas oscuras de ir a la caseta y a la avenida.
            repeat(12) { val u = az.entre(1f, 2f); mancha(Offset(az.entre(0f, ANCHO), gy(u)), az.entre(20f, 46f), az.entre(2f, 5f), l.tono(C.NIEVE), 0.6f) }
            for ((a0, b0) in listOf(Offset(40f, 285f) to Offset(150f, BORDE), Offset(330f, 285f) to Offset(270f, BORDE), Offset(120f, 240f) to Offset(300f, 232f)))
                drawPath(Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x + (b0.x - a0.x) * 0.3f, a0.y - 20f, b0.x - (b0.x - a0.x) * 0.2f, b0.y + 20f, b0.x, b0.y) }, l.tono(lerp(C.LOSA, C.ASFALTO, 0.5f)).copy(alpha = 0.5f), style = Stroke(5f, cap = StrokeCap.Round))
        }
        // La raya donde la plaza se junta con la avenida: el bordillo rebajado, con su canto gastado.
        drawRect(l.tono(C.HORMIGON), Offset(0f, BORDE - 0.6f), Size(ANCHO, 1.8f)); drawRect(l.tono(C.HOLLIN).copy(alpha = 0.3f), Offset(0f, BORDE + 1.2f), Size(ANCHO, 0.7f))
    }

    /** De noche, el charco de luz de cada farola sobre las losas y, si llueve, su reflejo. */
    private fun DrawScope.lucesEnElSuelo(m: Momento, p: Pincel) {
        val az = Azar(Semilla.de("distopia", "luces en el suelo"))
        for (f in p.a.focos) {
            val centro = Offset(f.p.x, f.suelo + 6f)
            haloDeBruma(centro, f.alcance * 1.25f, f.alcance * 0.3f, f.color, 0.75f * f.fuerza); haloDeBruma(centro, f.alcance * 0.6f, f.alcance * 0.14f, lerp(f.color, Color.White, 0.3f), 0.5f * f.fuerza)
            if (EscenaDistopia.mojado(m)) reflejoMojado(Offset(f.p.x, f.suelo), 34f, 12f, f.color, 0.5f * f.fuerza, az, 1.6f)
        }
    }

    /** Los cables que salen de lo alto del poste: a la farola de la plaza, al cartel y al bloque. Cruzan el cielo. */
    private fun DrawScope.cables(m: Momento, p: Pincel) {
        val c = p.l.tono(C.HOLLIN, 0.1f)
        val alto = Offset(POSTE.x, POSTE_CIMA + 12f)
        if (EscenaDistopia.lugar(m) == Lugar.CAPITAL) cable(alto, EscenaDistopia.FAROLA + Offset(0f, 2f), 14f, c, 0.5f)
        // Los grajos del cable del cartel: de día, y cada día en otro número y en otro sitio.
        if (p.l.noche < 0.4f && !EscenaDistopia.mojado(m)) { val az = m.azar("grajos"); val hilo = io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda(alto + Offset(0f, 4f), Offset(CARTEL.left + 2f, CARTEL.top + 1f), 20f, 40)
            val tinta = p.l.tono(Color(0xFF2E2C34), 0.06f); var i = 6 + az.entero(8)
            repeat(az.entero(5)) { val q = hilo[i.coerceAtMost(38)]; val s = if (az.real() > 0.5f) 1f else -1f
                drawOval(tinta, q + Offset(-2.6f, -4.4f), Size(5.2f, 4f)); drawCircle(tinta, 1.4f, q + Offset(s * 2.2f, -4.6f)); drawLine(tinta, q + Offset(s * 3f, -4.6f), q + Offset(s * 4.6f, -4.2f), 0.7f); drawLine(tinta, q + Offset(-s * 2f, -1.6f), q + Offset(-s * 4.6f, 0.4f), 1.1f, StrokeCap.Round)
                i += 2 + az.entero(7) } }
        cable(alto + Offset(0f, 4f), Offset(CARTEL.left + 2f, CARTEL.top + 1f), 20f, c, 0.6f)
        cable(alto + Offset(0f, 2f), Offset(-4f, 40f), 5f, c, 0.6f)
    }

    /** Todo lo que hay plantado en la plaza, cada cosa con su silueta. */
    private fun cosasDeLaPlaza(m: Momento, p: Pincel): List<Cosa> = buildList {
        val capital = EscenaDistopia.lugar(m) == Lugar.CAPITAL
        add(cartelCosa(m, p))
        add(posteCosa(m, p))
        add(columnaCosa(m, p))
        add(bancoCosa(m, p))
        add(jardineraCosa(m, p))
        if (capital) add(bidonCosa(m, p))
        if (capital && EscenaDistopia.conGente(m)) { add(tabureteCosa(p, Offset(222f, 281f), 1f)); add(tabureteCosa(p, Offset(279f, 280f), -1f)) }
        if (capital && EscenaDistopia.camaras(m) >= 6) add(postecilloCosa(p))
        if (capital) add(sacosCosa(m, p))
        add(fardosCosa(m, p))
        if (EscenaDistopia.pegandoCarteles(m)) add(escaleraCosa(p))
    }

    /** Los sacos terreros, apilados contra el bloque desde que empezó la guerra: nadie los ha quitado, y unos ya se han reventado. */
    private fun sacosCosa(m: Momento, p: Pincel): Cosa {
        val pie = Offset(32f, 273f); val g = 1.36f
        val sitios = listOf(Offset(-22f, 0f), Offset(-8f, 0.6f), Offset(6f, 0f), Offset(20f, 0.8f), Offset(-15f, -6.4f), Offset(-1f, -6f), Offset(13f, -6.6f), Offset(-8f, -12.6f), Offset(6f, -12.4f)).map { it * g }
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(pie.x - 40f, pie.y - 26f, pie.x + 38f, pie.y + 1f, CornerRadius(7f))) }
        return Cosa(forma, pie) {
            val l = p.l; val az = Azar(Semilla.de("distopia", "sacos"))
            drawOval(Color.Black.copy(alpha = 0.24f), pie + Offset(-42f, -3f), Size(82f, 7f))
            for ((i, s0) in sitios.withIndex()) {
                val c = pie + s0; val w = 14.6f * g; val h = 7f * g
                val saco = Path().apply { moveTo(c.x - w / 2f, c.y); cubicTo(c.x - w / 2f - 1.6f, c.y - h * 0.5f, c.x - w / 2f + 1f, c.y - h, c.x - w * 0.2f, c.y - h); cubicTo(c.x, c.y - h - 0.8f, c.x + w * 0.3f, c.y - h - 0.4f, c.x + w / 2f - 1f, c.y - h * 0.86f); cubicTo(c.x + w / 2f + 1.8f, c.y - h * 0.5f, c.x + w / 2f + 0.6f, c.y - 0.6f, c.x + w / 2f, c.y); close() }
                val propio = lerp(C.SACO, if (i % 3 == 0) C.HORMIGON_OSC else C.OXIDO, 0.18f + 0.1f * (i % 2))
                drawPath(saco, Brush.verticalGradient(listOf(p.arriba(c, propio), p.cara(c, propio), p.cara(c, lerp(propio, C.HOLLIN, 0.35f))), c.y - h, c.y))
                // La costura, la trama de la arpillera y la sombra del saco de encima.
                drawLine(p.cara(c, C.OXIDO).copy(alpha = 0.6f), c + Offset(w / 2f - 2.4f, -h * 0.84f), c + Offset(w / 2f - 1.6f, -0.6f), 0.4f)
                for (k in 1..3) drawLine(p.cara(c, C.HOLLIN).copy(alpha = 0.14f), c + Offset(-w / 2f + 1f, -h * k / 4f), c + Offset(w / 2f - 1f, -h * k / 4f - 0.3f), 0.3f)
                drawPath(saco, p.cara(c, C.HOLLIN).copy(alpha = 0.3f), style = Stroke(0.4f))
                if (i == 2) { drawPath(poligono(c + Offset(1f, -3.4f), c + Offset(4.6f, -4f), c + Offset(3f, -1.6f)), p.cara(c, C.HOLLIN).copy(alpha = 0.7f)); repeat(7) { drawCircle(p.cara(c, Color(0xFFD8C8A0)), az.entre(0.4f, 0.9f), c + Offset(az.entre(0f, 9f), az.entre(-1.4f, 1.4f))) } }   // el reventado, con su arena
            }
            if (EscenaDistopia.nevado(m)) for (s0 in sitios.takeLast(3)) drawOval(l.tono(C.NIEVE), pie + s0 + Offset(-8f, -11.4f), Size(16f, 3.4f))
            // La hierba que les ha crecido al pie.
            if (!EscenaDistopia.nevado(m)) repeat(12) { val x = az.entre(pie.x - 40f, pie.x + 38f); drawLine(l.tono(Color(0xFF7C8A56)), Offset(x, pie.y + 0.6f), Offset(x + az.entre(-1.2f, 1.2f), pie.y - az.entre(2f, 5f)), 0.6f) }
        }
    }

    /** Los fardos de periódicos de hoy, atados con cuerda, al pie de la columna: unos días hay dos y otros tres. */
    private fun fardosCosa(m: Momento, p: Pincel): Cosa {
        val pie = Offset(234f, 262f); val cuantos = 2 + m.azar("fardos").entero(2); val g = 1.3f
        val forma = Path().apply { addRect(Rect(pie.x - 11f, pie.y - 7.8f * cuantos - 1f, pie.x + 11f, pie.y)) }
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), pie + Offset(-11f, -1.6f), Size(22f, 4f))
            for (i in 0 until cuantos) {
                val c = pie + Offset(if (i == 1) 1.6f else if (i == 2) -1f else 0f, -7.6f * i); val w = 16f * g
                val h = 5.6f * g
                drawRect(Brush.verticalGradient(listOf(p.cara(c, C.HUESO), p.cara(c, lerp(C.HUESO, C.HORMIGON_OSC, 0.5f))), c.y - h, c.y), c + Offset(-w / 2f, -h), Size(w, h))
                for (k in 1..6) drawLine(p.cara(c, C.HORMIGON_OSC).copy(alpha = 0.6f), c + Offset(-w / 2f, -h + k * 1f), c + Offset(w / 2f, -h + k * 1f), 0.25f)
                drawPath(poligono(c + Offset(-w / 2f, -h), c + Offset(w / 2f, -h), c + Offset(w / 2f - 2f, -h - 2.4f), c + Offset(-w / 2f + 2f, -h - 2.4f)), p.arriba(c, lerp(C.HUESO, C.HORMIGON, 0.2f)))
                drawRect(p.cara(c, C.ROJO), c + Offset(-w / 2f + 3.4f, -h - 2f), Size(6.4f, 1.5f))
                drawLine(p.cara(c, C.OXIDO), c + Offset(-3f, -h - 2.4f), c + Offset(-3f, 0f), 0.6f); drawLine(p.cara(c, C.OXIDO), c + Offset(4.4f, -h - 2.4f), c + Offset(4.4f, 0f), 0.6f)
            }
        }
    }

    /** La escalera de quien pega el cartel del mes, apoyada en el tablero, con el cubo del engrudo y la brocha. */
    private fun escaleraCosa(p: Pincel): Cosa {
        val pie = Offset(306f, 262f); val cima = Offset(322f, CARTEL.bottom - 6f); val ancho = 9f
        val forma = poligono(pie + Offset(-ancho / 2f, 0f), cima + Offset(-ancho * 0.36f, 0f), cima + Offset(ancho * 0.36f, 0f), pie + Offset(ancho / 2f, 0f))
        return Cosa(forma, pie) {
            val palo = p.cara(pie, C.MADERA, if (p.r.lat >= 0f) 1f else -1f); val paloOsc = p.cara(pie, lerp(C.MADERA, C.HOLLIN, 0.4f), if (p.r.lat >= 0f) -1f else 1f)
            fun en(t: Float, s: Float) = Offset(pie.x + (cima.x - pie.x) * t + s * ancho * (0.5f - 0.14f * t), pie.y + (cima.y - pie.y) * t)
            var t = 0.08f
            while (t < 0.98f) { drawLine(paloOsc, en(t, -1f), en(t, 1f), 1.1f); t += 0.085f }
            drawLine(paloOsc, en(0f, -1f), en(1f, -1f), 1.6f, StrokeCap.Round); drawLine(palo, en(0f, 1f), en(1f, 1f), 1.6f, StrokeCap.Round)
            // El cubo, con el engrudo que ha chorreado, y la brocha atravesada.
            val c = Offset(292f, 266f)
            drawOval(Color.Black.copy(alpha = 0.2f), c + Offset(-7f, -1.4f), Size(14f, 3.4f))
            val cubo = poligono(c + Offset(-5f, -9f), c + Offset(5f, -9f), c + Offset(4f, 0f), c + Offset(-4f, 0f))
            volumen(p.l, p.r, cubo, c.x - 5f, c.x + 5f, lerp(C.HIERRO, C.HUESO, 0.4f))
            drawOval(p.cara(c, C.HUESO), c + Offset(-5f, -10.2f), Size(10f, 2.6f)); drawRect(p.cara(c, C.HUESO).copy(alpha = 0.8f), c + Offset(1f, -9f), Size(1.6f, 5f))
            drawLine(p.cara(c, C.MADERA), c + Offset(-7f, -11.6f), c + Offset(5f, -9.4f), 1.2f, StrokeCap.Round); drawRect(p.cara(c, C.HOLLIN), c + Offset(3.6f, -10.6f), Size(3f, 2.4f))
            mancha(c + Offset(6f, 1f), 7f, 1.6f, p.l.tono(C.HUESO), 0.5f)
        }
    }

    // ---------------------------------------------------------------- El poste de los altavoces

    /** Una bocina: la campana, vista de lado ([hacia]: −1 o 1) o de frente (0), con su motor detrás. */
    private fun DrawScope.bocina(p: Pincel, boca: Offset, k: Float, hacia: Float) {
        val metal = Color(0xFFB4B8B0)
        if (hacia == 0f) {
            drawOval(p.cara(boca, lerp(metal, C.HOLLIN, 0.2f)), Offset(boca.x - 7f * k, boca.y - 6f * k), Size(14f * k, 12f * k))
            drawOval(p.cara(boca, C.HOLLIN), Offset(boca.x - 5.4f * k, boca.y - 4.6f * k), Size(10.8f * k, 9.2f * k))
            drawOval(p.cara(boca, C.HIERRO), Offset(boca.x - 1.8f * k, boca.y - 1.6f * k), Size(3.6f * k, 3.2f * k))
            drawArc(p.cara(boca, C.HUESO, 1f).copy(alpha = 0.7f), 200f, 100f, false, Offset(boca.x - 6.4f * k, boca.y - 5.4f * k), Size(12.8f * k, 10.8f * k), style = Stroke(0.6f))
            return
        }
        val cuello = boca + Offset(-hacia * 13f * k, -2f * k)
        val campana = poligono(cuello + Offset(0f, -1.6f * k), boca + Offset(0f, -6.4f * k), boca + Offset(0f, 6.4f * k), cuello + Offset(0f, 1.6f * k))
        drawPath(campana, Brush.verticalGradient(listOf(p.arriba(boca, metal), p.cara(boca, metal, hacia), p.cara(boca, lerp(metal, C.HOLLIN, 0.45f), -hacia)), boca.y - 6.4f * k, boca.y + 6.4f * k))
        drawOval(p.cara(boca, C.HOLLIN), Offset(boca.x - 1.7f * k, boca.y - 6.4f * k), Size(3.4f * k, 12.8f * k))
        drawRoundRect(p.cara(cuello, C.HIERRO, hacia), Offset(minOf(cuello.x, cuello.x - hacia * 6f * k), cuello.y - 2.6f * k), Size(6f * k, 5.2f * k), CornerRadius(1.2f * k))
    }

    /**
     * El poste de los altavoces: una torre de celosía que cruza el cuadro de abajo arriba, con tres bocinas en lo alto,
     * su lámpara, una caja de empalmes y el dado de hormigón del pie. Los días de fiesta y de desfile lleva una bandera.
     */
    private fun posteCosa(m: Momento, p: Pincel): Cosa {
        val pie = POSTE; val cima = Offset(pie.x, POSTE_CIMA)
        val forma = Path().apply {
            addPath(poligono(pie + Offset(-6.4f, 0f), cima + Offset(-2.8f, 0f), cima + Offset(2.8f, 0f), pie + Offset(6.4f, 0f)))
            addRect(Rect(pie.x - 10f, pie.y - 7f, pie.x + 10f, pie.y))
            addOval(Rect(cima.x - 18f, cima.y + 2f, cima.x + 20f, cima.y + 26f))
            addRect(Rect(pie.x, EscenaDistopia.LAMPARA.y - 2f, EscenaDistopia.LAMPARA.x + 6f, EscenaDistopia.LAMPARA.y + 2f))
        }
        return Cosa(forma, pie) {
            val l = p.l; val pintura = C.VERDE_GRIS
            // El dado de hormigón, con el óxido que le chorrea.
            val dado = Rect(pie.x - 10f, pie.y - 7f, pie.x + 10f, pie.y)
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(dado.left - 3f, dado.bottom - 2.4f), Size(dado.width + 6f, 5f))
            drawRect(Brush.verticalGradient(listOf(p.cara(pie, C.HORMIGON), p.cara(pie, C.HORMIGON_OSC)), dado.top, dado.bottom), dado.topLeft, dado.size)
            drawPath(poligono(Offset(dado.left, dado.top), Offset(dado.right, dado.top), Offset(dado.right - 2.4f, dado.top - 2.6f), Offset(dado.left + 2.4f, dado.top - 2.6f)), p.arriba(pie, C.HORMIGON))
            drawRect(if (p.r.lat >= 0f) p.cara(pie, C.HORMIGON, 1f) else p.cara(pie, C.HORMIGON, -1f), Offset(if (p.r.lat >= 0f) dado.right - 3f else dado.left, dado.top), Size(3f, dado.height))
            for (x in listOf(-3f, 2.4f)) drawRect(Brush.verticalGradient(listOf(p.cara(pie, C.OXIDO).copy(alpha = 0.7f), Color.Transparent), dado.top, dado.bottom), Offset(pie.x + x, dado.top), Size(1.6f, dado.height))
            // La celosía: dos montantes y las diagonales.
            fun ancho(y: Float) = 2.6f + 3.6f * (y - cima.y) / (pie.y - cima.y)
            val claro = p.cara(Offset(pie.x, 150f), pintura, if (p.r.lat >= 0f) 1f else -1f); val oscuro = p.cara(Offset(pie.x, 150f), lerp(pintura, C.HOLLIN, 0.3f), if (p.r.lat >= 0f) -1f else 1f)
            var y = pie.y - 7f; var i = 0
            while (y > cima.y + 4f) {
                val y1 = (y - (7f + 5f * (y - cima.y) / (pie.y - cima.y))).coerceAtLeast(cima.y + 2f)
                drawLine(if (i % 2 == 0) oscuro else claro, Offset(pie.x - ancho(y), y), Offset(pie.x + ancho(y1), y1), 0.7f)
                drawLine(if (i % 2 == 0) claro else oscuro, Offset(pie.x + ancho(y), y), Offset(pie.x - ancho(y1), y1), 0.7f)
                drawLine(oscuro, Offset(pie.x - ancho(y1), y1), Offset(pie.x + ancho(y1), y1), 0.6f)
                y = y1; i++
            }
            drawLine(if (p.r.lat >= 0f) oscuro else claro, Offset(pie.x - ancho(pie.y - 7f), pie.y - 7f), Offset(pie.x - ancho(cima.y), cima.y), 1.5f)
            drawLine(if (p.r.lat >= 0f) claro else oscuro, Offset(pie.x + ancho(pie.y - 7f), pie.y - 7f), Offset(pie.x + ancho(cima.y), cima.y), 1.5f)
            val az = Azar(Semilla.de("distopia", "poste"))
            repeat(9) { val yy = az.entre(cima.y + 30f, pie.y - 10f); drawLine(l.tono(C.OXIDO).copy(alpha = 0.6f), Offset(pie.x - ancho(yy), yy), Offset(pie.x - ancho(yy) + (if (it % 2 == 0) 0f else 2f * ancho(yy)), yy + az.entre(2f, 6f)), 1.3f) }
            // La caja de empalmes y su tubo.
            val caja = Rect(pie.x - 5f, 196f, pie.x + 5f, 212f)
            drawRect(p.cara(caja.center, lerp(pintura, C.HOLLIN, 0.2f)), caja.topLeft, caja.size); drawRect(p.cara(caja.center, pintura, if (p.r.lat >= 0f) 1f else -1f), Offset(if (p.r.lat >= 0f) caja.right - 2f else caja.left, caja.top), Size(2f, caja.height))
            drawCircle(p.cara(caja.center, C.HOLLIN), 1f, caja.center + Offset(0f, 2f)); drawLine(p.cara(caja.center, C.HOLLIN), Offset(pie.x, caja.bottom), Offset(pie.x, pie.y - 7f), 1f)
            drawRect(l.tono(C.OXIDO).copy(alpha = 0.5f), Offset(caja.left, caja.bottom - 2f), Size(caja.width, 2f))
            // El brazo de la lámpara (en el pueblo no hay: allí no hay farolas).
            val lamp = EscenaDistopia.LAMPARA
            if (EscenaDistopia.lugar(m) == Lugar.CAPITAL) {
                drawLine(oscuro, Offset(pie.x, lamp.y - 1f), lamp + Offset(-3f, -1f), 1.4f); drawLine(oscuro, Offset(pie.x, lamp.y + 9f), lamp + Offset(-8f, -1f), 0.7f)
                drawPath(poligono(lamp + Offset(-6f, -2f), lamp + Offset(6f, -2f), lamp + Offset(8f, 1.6f), lamp + Offset(-8f, 1.6f)), p.cara(lamp, C.HIERRO))
                if (EscenaDistopia.farolas(m)) { drawOval(lerp(C.SODIO, Color.White, 0.6f), lamp + Offset(-6f, 1f), Size(12f, 2.6f)); resplandor(lamp + Offset(0f, 4f), 26f, 0.7f, C.SODIO) }
                else drawOval(l.tono(C.HUESO).copy(alpha = 0.85f), lamp + Offset(-6f, 1f), Size(12f, 2.2f))
            }
            // Las tres bocinas, en su cruceta.
            drawLine(oscuro, cima + Offset(-12f, 12f), cima + Offset(13f, 12f), 1.6f)
            bocina(p, cima + Offset(-17f, 15f), 1f, -1f); bocina(p, cima + Offset(4.6f, 21f), 0.86f, 0f); bocina(p, cima + Offset(20f, 12f), 1.05f, 1f)
            drawLine(oscuro, cima, cima + Offset(0f, -5f), 0.8f)
            // La bandera de los días señalados; el crespón del luto.
            if (EscenaDistopia.fiesta(m) || EscenaDistopia.desfile(m)) {
                val a0 = Offset(pie.x + 4f, 134f); val w = 24f; val h = 36f
                drawLine(p.cara(a0, C.MADERA), a0 + Offset(-4f, 0f), a0 + Offset(w + 2f, -2f), 1.4f, StrokeCap.Round)
                val pano = poligono(listOf(a0 + Offset(1f, 0f), a0 + Offset(w, -1.6f), a0 + Offset(w - 1f, h), a0 + Offset(w / 2f, h - 6f), a0 + Offset(1.6f, h + 1f)))
                drawPath(pano, Brush.horizontalGradient(listOf(p.cara(a0, C.ROJO_OSC), p.cara(a0, C.ROJO), p.cara(a0, lerp(C.ROJO, C.LUZ, 0.22f)), p.cara(a0, C.ROJO)), a0.x, a0.x + w))
                for (k in listOf(0.34f, 0.7f)) drawLine(p.cara(a0, C.ROJO_OSC).copy(alpha = 0.6f), a0 + Offset(w * k, 0f), a0 + Offset(w * k - 0.6f, h - 3f), 0.7f)
                with(CiudadDistopia) { ojo(a0 + Offset(w / 2f, 15f), 17f, p.cara(a0, C.HUESO), p.cara(a0, C.ROJO_OSC)) }
            }
            if (EscenaDistopia.luto(m)) { drawLine(p.cara(pie, C.HOLLIN), Offset(pie.x - 4f, 150f), Offset(pie.x + 4f, 150f), 2.4f); for (s in listOf(-1f, 1f)) drawLine(p.cara(pie, C.HOLLIN), Offset(pie.x, 150f), Offset(pie.x + s * 5f, 166f), 1.6f, StrokeCap.Round) }
            if (EscenaDistopia.nevado(m)) { drawRect(l.tono(C.NIEVE), Offset(dado.left + 1f, dado.top - 3.6f), Size(dado.width - 2f, 1.6f)); if (EscenaDistopia.lugar(m) == Lugar.CAPITAL) drawRect(l.tono(C.NIEVE), lamp + Offset(-6f, -3.2f), Size(12f, 1.4f)) }
        }
    }

    // ---------------------------------------------------------------- La columna de los carteles

    /** Uno de los seis carteles sin texto, dentro de [z]. [viejo] lo descolora. */
    private fun DrawScope.cartelSinTexto(p: Pincel, z: Rect, cual: Int, viejo: Float = 0f) {
        fun c(propio: Color) = p.cara(z.center, lerp(propio, Color(0xFFB8B4A4), viejo))
        val w = z.width; val h = z.height; val o = z.topLeft
        fun en(kx: Float, ky: Float) = Offset(o.x + w * kx, o.y + h * ky)
        when (cual) {
            0 -> { drawRect(c(C.ROJO), o, z.size); with(CiudadDistopia) { ojo(z.center + Offset(0f, -h * 0.06f), w * 0.72f, c(C.HUESO), c(C.ROJO_OSC)) }; drawRect(c(C.HUESO), en(0.14f, 0.8f), Size(w * 0.72f, h * 0.06f)) }
            1 -> { drawRect(c(C.HUESO), o, z.size); drawRect(c(C.ROJO), en(0f, 0.78f), Size(w, h * 0.22f))
                drawPath(poligono(listOf(en(0.38f, 0.78f), en(0.38f, 0.34f), en(0.44f, 0.34f), en(0.44f, 0.2f), en(0.48f, 0.2f), en(0.5f, 0.08f), en(0.52f, 0.2f), en(0.56f, 0.2f), en(0.56f, 0.34f), en(0.62f, 0.34f), en(0.62f, 0.78f))), c(C.ROJO_OSC))
                for (k in listOf(0.14f, 0.24f, 0.76f, 0.86f)) drawRect(c(C.HORMIGON_OSC), en(k - 0.04f, 0.52f + 0.3f * kotlin.math.abs(k - 0.5f)), Size(w * 0.08f, h * (0.26f - 0.3f * kotlin.math.abs(k - 0.5f)))) }
            2 -> { drawRect(c(C.VERDE_GRIS), o, z.size); val r0 = minOf(w, h) * 0.3f
                for (i in 0 until 10) { val ang = i * 0.628f; drawLine(c(C.HUESO), z.center + Offset(cos(ang), sin(ang)) * (r0 * 0.8f), z.center + Offset(cos(ang), sin(ang)) * (r0 * 1.24f), r0 * 0.3f) }
                drawCircle(c(C.HUESO), r0, z.center); drawCircle(c(C.VERDE_GRIS), r0 * 0.62f, z.center); drawCircle(c(C.ROJO), r0 * 0.3f, z.center) }
            3 -> { drawRect(c(C.SACO), o, z.size); drawLine(c(C.ROJO_OSC), en(0.5f, 0.9f), en(0.5f, 0.22f), w * 0.03f)
                for (i in 0..5) for (s in listOf(-1f, 1f)) { val q = en(0.5f, 0.2f + 0.085f * i); drawOval(c(C.ROJO), Offset(q.x + (if (s < 0f) -w * 0.15f else w * 0.01f), q.y), Size(w * 0.14f, h * 0.075f)) }
                drawRect(c(C.ROJO_OSC), en(0f, 0.9f), Size(w, h * 0.1f)) }
            4 -> { drawRect(c(C.HUESO), o, z.size); for (i in 0..4) drawRect(c(if (i == 4) C.ROJO_OSC else C.ROJO), en(0.12f + 0.16f * i, 0.76f - 0.12f * i), Size(w * 0.11f, h * (0.12f + 0.12f * i)))
                drawLine(c(C.HOLLIN), en(0.1f, 0.62f), en(0.86f, 0.12f), w * 0.022f); drawPath(poligono(en(0.9f, 0.08f), en(0.8f, 0.1f), en(0.86f, 0.2f)), c(C.HOLLIN)); drawRect(c(C.HOLLIN), en(0.08f, 0.88f), Size(w * 0.84f, h * 0.025f)) }
            else -> { drawRect(c(C.ROJO), o, z.size); for (i in 0..2) drawPath(Path().apply { val y = 0.3f + 0.22f * i; moveTo(en(0.2f, y + 0.16f).x, en(0.2f, y + 0.16f).y); lineTo(en(0.5f, y).x, en(0.5f, y).y); lineTo(en(0.8f, y + 0.16f).x, en(0.8f, y + 0.16f).y) }, c(if (i == 0) C.HUESO else lerp(C.HUESO, C.ROJO, 0.3f * i)), style = Stroke(h * 0.07f)) }
        }
    }

    /** La columna de los carteles: un cilindro de hormigón con su sombrerete, forrado de carteles pegados unos sobre otros y arrancados a medias. */
    private fun columnaCosa(m: Momento, p: Pincel): Cosa {
        val pie = COLUMNA; val rx = 15f; val arriba = 196f
        val cuerpo = Path().apply { addRect(Rect(pie.x - rx, arriba, pie.x + rx, pie.y)) ; addOval(Rect(pie.x - rx, pie.y - 3f, pie.x + rx, pie.y + 3f)) }
        val forma = Path().apply { addPath(cuerpo); addPath(poligono(Offset(pie.x - rx - 2.4f, arriba), Offset(pie.x - 5f, arriba - 9f), Offset(pie.x + 5f, arriba - 9f), Offset(pie.x + rx + 2.4f, arriba))) }
        return Cosa(forma, pie) {
            val l = p.l
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(pie.x - rx - 3f, pie.y - 2f), Size(rx * 2f + 6f, 6.4f))
            redondo(p, cuerpo, pie.x - rx, pie.x + rx, 236f, C.HORMIGON)
            clipPath(cuerpo) {
                val az = Azar(Semilla.de("distopia", "columna"))
                // Los carteles viejos, por capas: cada mes se pega uno encima.
                val mes = EscenaDistopia.cartelDelMes(m)
                for (i in 0 until 9) {
                    val z = Rect(pie.x - rx + az.entre(-2f, 20f), arriba + az.entre(2f, 46f), 0f, 0f).let { Rect(it.left, it.top, it.left + az.entre(8f, 14f), it.top + az.entre(11f, 18f)) }
                    if (i % 3 == 0) cartelSinTexto(p, z, (mes + 2 + i) % 6, 0.5f) else drawRect(p.cara(z.center, listOf(C.HUESO, C.SACO, lerp(C.ROJO, C.HORMIGON, 0.4f), C.VERDE_GRIS)[i % 4]).copy(alpha = 0.9f), z.topLeft, z.size)
                    // El desgarrón: un pico de hormigón o del cartel de debajo.
                    drawPath(poligono(z.bottomRight, z.bottomRight + Offset(-az.entre(3f, 7f), 0f), z.bottomRight + Offset(0f, -az.entre(3f, 8f))), p.cara(z.center, C.HORMIGON))
                }
                // El del mes, entero, en el centro; y, en campaña, la franja roja que lo cruza todo.
                val z = Rect(pie.x - 9f, arriba + 14f, pie.x + 9f, arriba + 40f)
                drawRect(p.cara(z.center, C.HUESO), Offset(z.left - 0.8f, z.top - 0.8f), Size(z.width + 1.6f, z.height + 1.6f)); cartelSinTexto(p, z, mes)
                if (EscenaDistopia.enCampana(m)) for (y in listOf(arriba + 5f, arriba + 50f)) drawRect(p.cara(Offset(pie.x, y), C.ROJO), Offset(pie.x - rx, y), Size(rx * 2f, 6f))
                // La curva del cilindro oscurece los carteles hacia el lado de la sombra, y el pie tiene el cerco de la humedad.
                drawRect(Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.r.color.copy(alpha = 0.7f * p.r.contraste), Color.Transparent, Color.Transparent) else listOf(Color.Transparent, Color.Transparent, p.r.color.copy(alpha = 0.7f * p.r.contraste)), pie.x - rx, pie.x + rx), Offset(pie.x - rx, arriba), Size(rx * 2f, pie.y - arriba), blendMode = BlendMode.Multiply)
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(C.HOLLIN).copy(alpha = 0.4f)), pie.y - 12f, pie.y + 3f), Offset(pie.x - rx, pie.y - 12f), Size(rx * 2f, 15f))
                repeat(5) { val x = az.entre(pie.x - rx, pie.x + rx); drawRect(Brush.verticalGradient(listOf(l.tono(C.HOLLIN).copy(alpha = 0.3f), Color.Transparent), arriba, arriba + az.entre(8f, 26f)), Offset(x, arriba), Size(az.entre(0.8f, 2f), 26f)) }
            }
            // El sombrerete y su remate.
            val gorro = poligono(Offset(pie.x - rx - 2.4f, arriba), Offset(pie.x - 5f, arriba - 9f), Offset(pie.x + 5f, arriba - 9f), Offset(pie.x + rx + 2.4f, arriba))
            redondo(p, gorro, pie.x - rx - 2.4f, pie.x + rx + 2.4f, arriba - 4f, C.VERDE_GRIS)
            drawRect(p.cara(Offset(pie.x, arriba), lerp(C.VERDE_GRIS, C.HOLLIN, 0.4f)), Offset(pie.x - rx - 2.4f, arriba - 0.6f), Size(rx * 2f + 4.8f, 2f))
            drawCircle(p.cara(Offset(pie.x, arriba - 11f), C.VERDE_GRIS), 2.2f, Offset(pie.x, arriba - 10.6f))
            if (EscenaDistopia.nevado(m)) drawPath(poligono(Offset(pie.x - rx, arriba - 1.4f), Offset(pie.x - 5f, arriba - 9.6f), Offset(pie.x + 5f, arriba - 9.6f), Offset(pie.x + rx, arriba - 1.4f)), l.tono(C.NIEVE).copy(alpha = 0.9f))
        }
    }

    // ---------------------------------------------------------------- El banco y el objeto del día

    /** El banco: dos pies de hormigón, el asiento de tres listones y el respaldo de dos, con la pintura verde saltada. Encima, el objeto del día. */
    private fun bancoCosa(m: Momento, p: Pincel): Cosa {
        val b = BANCO; val yt = 246f; val yf = 257f; val canto = 4.4f
        val forma = Path().apply {
            addRect(Rect(b.left, yt, b.right, yf + canto)); addRect(Rect(b.left + 6f, yf + canto, b.left + 18f, b.bottom)); addRect(Rect(b.right - 18f, yf + canto, b.right - 6f, b.bottom))
            addRect(Rect(b.left + 2f, b.top, b.right - 2f, b.top + 12f)); addRect(Rect(b.left + 9f, b.top, b.left + 13f, yt)); addRect(Rect(b.right - 13f, b.top, b.right - 9f, yt))
        }
        return Cosa(forma, Offset(b.center.x, b.bottom)) {
            val l = p.l; val az = Azar(Semilla.de("distopia", "banco"))
            drawOval(Color.Black.copy(alpha = 0.22f), Offset(b.left + 2f, b.bottom - 2.6f), Size(b.width - 4f, 5.6f))
            // Lo de debajo del asiento, en sombra.
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.34f), Color.Transparent), yf + canto, yf + canto + 11f), Offset(b.left + 8f, yf + canto), Size(b.width - 16f, 11f))
            // Los pies de hormigón, con su canto.
            for (x in listOf(b.left + 6f, b.right - 18f)) {
                val c = Offset(x + 6f, 270f)
                drawPath(poligono(Offset(x, yf + canto), Offset(x + 12f, yf + canto), Offset(x + 13.4f, b.bottom), Offset(x - 1.4f, b.bottom)), Brush.verticalGradient(listOf(p.cara(c, C.HORMIGON_OSC), p.cara(c, C.HORMIGON)), yf + canto, b.bottom))
                drawRect(p.cara(c, C.HORMIGON, if (p.r.lat >= 0f) 1f else -1f), Offset(if (p.r.lat >= 0f) x + 9.6f else x - 0.6f, yf + canto), Size(3f, b.bottom - yf - canto))
                drawRect(l.tono(C.HOLLIN).copy(alpha = 0.3f), Offset(x - 1.4f, b.bottom - 3f), Size(14.8f, 3f))
            }
            // El respaldo: dos listones en sus dos pletinas.
            for (x in listOf(b.left + 9f, b.right - 13f)) drawRect(p.cara(Offset(x, 242f), C.HIERRO), Offset(x, b.top), Size(3.4f, yt - b.top + 2f))
            fun liston(y0: Float, y1: Float, x0: Float = b.left + 2f, x1: Float = b.right - 2f, deArriba: Boolean = false) {
                val c = Offset((x0 + x1) / 2f, (y0 + y1) / 2f); val verde = lerp(C.VERDE_GRIS, Color(0xFF5E7A62), 0.4f)
                drawRect(Brush.verticalGradient(listOf(if (deArriba) p.arriba(c, verde) else p.cara(c, verde), p.cara(c, lerp(verde, C.HOLLIN, 0.2f))), y0, y1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
                // La pintura saltada deja ver la madera.
                clipRect(x0, y0, x1, y1) { repeat(9) { val x = az.entre(x0, x1); drawOval(p.cara(c, C.MADERA).copy(alpha = 0.85f), Offset(x, az.entre(y0 - 1f, y1 - 1f)), Size(az.entre(2f, 9f), az.entre(0.8f, 1.8f))) } }
                drawLine(l.tono(C.LUZ).copy(alpha = 0.3f), Offset(x0, y0 + 0.3f), Offset(x1, y0 + 0.3f), 0.4f); drawLine(l.tono(C.HOLLIN).copy(alpha = 0.4f), Offset(x0, y1), Offset(x1, y1), 0.5f)
            }
            liston(b.top, b.top + 5.4f); liston(b.top + 6.8f, b.top + 12.2f, x1 = b.right - 18f)       // al de abajo le falta un trozo
            // El asiento, visto un poco desde arriba: tres listones y su canto.
            val asiento = poligono(Offset(b.left + 3f, yt), Offset(b.right - 3f, yt), Offset(b.right, yf), Offset(b.left, yf))
            for (i in 0..2) { val y0 = yt + i * 3.66f; val k0 = i / 3f; val k1 = (i + 1) / 3f; liston(y0 + 0.3f, y0 + 3.4f, b.left + 3f - 3f * k0, b.right - 3f + 3f * k1, deArriba = true) }
            liston(yf, yf + canto)
            for (x in listOf(b.left + 12f, b.right - 12f)) for (y in listOf(yt + 1.8f, yt + 5.5f, yt + 9.2f)) drawCircle(p.cara(Offset(x, y), C.HIERRO), 0.6f, Offset(x, y))
            if (EscenaDistopia.nevado(m)) { drawPath(asiento, l.tono(C.NIEVE).copy(alpha = 0.85f)); drawRect(l.tono(C.NIEVE), Offset(b.left + 2f, b.top - 1.4f), Size(b.width - 4f, 1.6f)) }
            // El objeto del día, encima (el abrigo, colgado del respaldo).
            val objeto = m.ficha.objeto
            if (objeto != "abrigo") capaSombra(p.r) { c -> clipPath(asiento) { drawOval(c, Offset(SOBRE.x - 17f + p.r.ax * 4f, SOBRE.y - 1.6f), Size(34f, 5f)) } }
            with(ObjetosDistopia) { objetoDelDia(m, p.l, p.r, p.a, SOBRE, KO, Rect(b.left + 2f, b.top, b.right - 2f, b.top + 12.2f)) }
            if (EscenaDistopia.luto(m)) { val a0 = Offset(b.right - 30f, b.top); drawPath(poligono(a0, a0 + Offset(18f, 0f), a0 + Offset(17f, 20f), a0 + Offset(9f, 16f), a0 + Offset(1f, 21f)), p.cara(a0, C.HOLLIN)) }
        }
    }

    // ---------------------------------------------------------------- El bidón, los taburetes y la jardinera

    /** El bidón donde se calienta la cola: un barril de chapa oxidado y negro de hollín, con agujeros por los que se ve la lumbre. */
    private fun bidonCosa(m: Momento, p: Pincel): Cosa {
        val pie = Offset(EscenaDistopia.BIDON.x, 279f); val rx = 13f; val arriba = 243f
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(pie.x - rx, arriba, pie.x + rx, pie.y + 1.6f, CornerRadius(2f))) }
        return Cosa(forma, pie) {
            val l = p.l; val encendido = EscenaDistopia.bidon(m)
            val az = Azar(Semilla.de("distopia", "bidon"))
            // La ceniza alrededor.
            repeat(5) { mancha(pie + Offset(az.entre(-16f, 18f), az.entre(-1f, 3f)), az.entre(5f, 11f), az.entre(1.2f, 2.4f), l.tono(Color(0xFF7E7C76)), 0.45f) }
            drawOval(Color.Black.copy(alpha = 0.26f), Offset(pie.x - rx - 2f, pie.y - 2f), Size(rx * 2f + 4f, 5.6f))
            redondo(p, forma, pie.x - rx, pie.x + rx, 264f, lerp(C.OXIDO, C.HOLLIN, 0.35f))
            clipPath(forma) {
                // Los aros, el hollín de arriba, las abolladuras y los agujeros.
                for (y in listOf(arriba + 11f, arriba + 24f)) { drawLine(l.tono(C.HOLLIN).copy(alpha = 0.7f), Offset(pie.x - rx, y), Offset(pie.x + rx, y), 1.2f); drawLine(l.tono(C.OXIDO).copy(alpha = 0.7f), Offset(pie.x - rx, y - 0.9f), Offset(pie.x + rx, y - 0.9f), 0.4f) }
                drawRect(Brush.verticalGradient(listOf(l.tono(C.HOLLIN).copy(alpha = 0.85f), Color.Transparent), arriba, arriba + 12f), Offset(pie.x - rx, arriba), Size(rx * 2f, 12f))
                repeat(6) { mancha(Offset(az.entre(pie.x - rx, pie.x + rx), az.entre(arriba + 6f, pie.y)), az.entre(2f, 5f), az.entre(2f, 5f), l.tono(if (it % 2 == 0) C.OXIDO else C.HOLLIN), 0.4f) }
                for (i in 0..4) { val q = Offset(pie.x - 9.2f + i * 4.6f, pie.y - 7.6f + (i % 2) * 1.8f); drawCircle(if (encendido) Color(0xFFFF9A40) else l.tono(Color(0xFF1C1A18)), 1.4f, q); if (encendido) drawCircle(Color(0xFFFFE08A), 0.6f, q) }
            }
            // La boca, con los palos y las tablas que asoman.
            drawOval(p.cara(Offset(pie.x, arriba), C.HOLLIN), Offset(pie.x - rx, arriba - 2.6f), Size(rx * 2f, 5.2f))
            drawOval(if (encendido) Color(0xFFE0621E) else l.tono(Color(0xFF1C1A18)), Offset(pie.x - rx + 1.6f, arriba - 1.8f), Size(rx * 2f - 3.2f, 3.6f))
            for ((dx, dy, g) in listOf(Triple(-8f, -11f, 1.8f), Triple(4f, -14f, 1.5f), Triple(9f, -8f, 2f))) drawLine(p.cara(Offset(pie.x, arriba), lerp(C.MADERA, C.HOLLIN, 0.5f)), Offset(pie.x + dx * 0.4f, arriba), Offset(pie.x + dx, arriba + dy), g, StrokeCap.Round)
            if (encendido) resplandor(Offset(pie.x, arriba - 2f), 34f, 0.25f + 0.5f * l.noche)
            if (EscenaDistopia.nevado(m) && !encendido) drawOval(l.tono(C.NIEVE), Offset(pie.x - rx + 1f, arriba - 2.4f), Size(rx * 2f - 2f, 3.2f))
        }
    }

    /** Un taburete de tijera, de los que se sacan para las actividades obligatorias. */
    private fun tabureteCosa(p: Pincel, pie: Offset, lado: Float): Cosa {
        val forma = poligono(pie + Offset(-8f, 0f), pie + Offset(-7f, -14f), pie + Offset(7f, -14f), pie + Offset(8f, 0f))
        return Cosa(forma, pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), pie + Offset(-9f, -1.4f), Size(18f, 3.6f))
            val palo = p.cara(pie, C.MADERA, lado); val paloOsc = p.cara(pie, lerp(C.MADERA, C.HOLLIN, 0.4f), -lado)
            drawLine(paloOsc, pie + Offset(-7f, 0f), pie + Offset(6.4f, -13f), 1.4f, StrokeCap.Round); drawLine(palo, pie + Offset(7f, 0f), pie + Offset(-6.4f, -13f), 1.4f, StrokeCap.Round)
            drawPath(poligono(pie + Offset(-7.6f, -13.6f), pie + Offset(7.6f, -13.6f), pie + Offset(6.6f, -11f), pie + Offset(-6.6f, -11f)), p.arriba(pie, C.SACO))
            drawLine(p.cara(pie, C.HOLLIN).copy(alpha = 0.5f), pie + Offset(-6.6f, -11f), pie + Offset(6.6f, -11f), 0.5f)
        }
    }

    /** El poste de la cámara del banco: desde que hay cámaras «en los bancos de los parques». */
    private fun postecilloCosa(p: Pincel): Cosa {
        val pie = Offset(BANCO.right + 9f, 268f)
        return Cosa(poligono(pie + Offset(-1.2f, 0f), pie + Offset(-1f, -46f), pie + Offset(1f, -46f), pie + Offset(1.2f, 0f)), pie) {
            drawOval(Color.Black.copy(alpha = 0.2f), pie + Offset(-4f, -1f), Size(8f, 2.4f))
            drawRect(Brush.horizontalGradient(if (p.r.lat >= 0f) listOf(p.cara(pie, C.HIERRO, -1f), p.cara(pie, C.VERDE_GRIS, 1f)) else listOf(p.cara(pie, C.VERDE_GRIS, -1f), p.cara(pie, C.HIERRO, 1f)), pie.x - 1.2f, pie.x + 1.2f), pie + Offset(-1.2f, -46f), Size(2.4f, 46f))
            drawRect(p.cara(pie, C.HORMIGON_OSC), pie + Offset(-3f, -2.4f), Size(6f, 2.4f))
        }
    }

    /** La jardinera de hormigón, con su tierra seca, un aligustre a medio pelar y las colillas. Su borde es el mirador del gato. */
    private fun jardineraCosa(m: Momento, p: Pincel): Cosa {
        val j = JARDINERA; val yt = j.top; val yf = j.top + 6f
        val forma = Path().apply { addPath(poligono(listOf(Offset(j.left + 4f, yt), Offset(j.right - 4f, yt), Offset(j.right, yf), Offset(j.right - 3f, j.bottom), Offset(j.left + 3f, j.bottom), Offset(j.left, yf)))); addOval(Rect(j.left + 26f, yt - 32f, j.right - 2f, yt + 2f)) }
        return Cosa(forma, Offset(j.center.x, j.bottom)) {
            val l = p.l; val az = Azar(Semilla.de("distopia", "jardinera")); val c = j.center
            drawOval(Color.Black.copy(alpha = 0.24f), Offset(j.left, j.bottom - 2.6f), Size(j.width, 5.6f))
            // La cara de delante: hormigón de árido visto, con sus chorreras y un desportillado.
            val cara = poligono(Offset(j.left, yf), Offset(j.right, yf), Offset(j.right - 3f, j.bottom), Offset(j.left + 3f, j.bottom))
            drawPath(cara, Brush.verticalGradient(listOf(p.cara(c, C.HORMIGON), p.cara(c, C.HORMIGON_OSC)), yf, j.bottom))
            clipPath(cara) {
                repeat(90) { drawCircle(p.cara(c, if (it % 3 == 0) C.HOLLIN else C.HUESO).copy(alpha = 0.3f), az.entre(0.3f, 0.8f), Offset(az.entre(j.left, j.right), az.entre(yf, j.bottom))) }
                repeat(6) { val x = az.entre(j.left, j.right); drawRect(Brush.verticalGradient(listOf(l.tono(C.HOLLIN).copy(alpha = 0.34f), Color.Transparent), yf, yf + az.entre(6f, 18f)), Offset(x, yf), Size(az.entre(1f, 2.6f), 18f)) }
                drawRect(p.cara(c, C.ROJO).copy(alpha = 0.5f), Offset(j.left + 8f, yf + 5f), Size(22f, 3f))        // la franja pintada, casi borrada
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, l.tono(Color(0xFF5E6A4A)).copy(alpha = 0.4f)), j.bottom - 6f, j.bottom), Offset(j.left, j.bottom - 6f), Size(j.width, 6f))
                drawRect(if (p.r.lat >= 0f) p.cara(c, C.HORMIGON, 1f) else p.cara(c, C.HORMIGON_OSC, 1f), Offset(j.right - 6f, yf), Size(6f, j.bottom - yf))
            }
            drawPath(poligono(Offset(j.right - 12f, yf), Offset(j.right - 5f, yf), Offset(j.right - 8f, yf + 3.6f)), p.cara(c, C.HOLLIN).copy(alpha = 0.6f))
            // El borde, visto desde arriba, y la tierra.
            drawPath(poligono(Offset(j.left + 4f, yt), Offset(j.right - 4f, yt), Offset(j.right, yf), Offset(j.left, yf)), p.arriba(c, C.HORMIGON))
            drawPath(poligono(Offset(j.left + 7.4f, yt + 1.4f), Offset(j.right - 7.4f, yt + 1.4f), Offset(j.right - 5f, yf - 1.4f), Offset(j.left + 5f, yf - 1.4f)), p.cara(c, if (EscenaDistopia.nevado(m)) C.NIEVE else Color(0xFF6A5C4A)))
            drawLine(l.tono(C.LUZ).copy(alpha = 0.4f), Offset(j.left, yf), Offset(j.right, yf), 0.5f)
            if (!EscenaDistopia.nevado(m)) repeat(5) { drawLine(p.cara(c, C.HUESO), Offset(j.left + 10f + it * 3.4f, yt + 2.6f + (it % 2)), Offset(j.left + 11.6f + it * 3.4f, yt + 3f + (it % 2)), 0.8f) }
            // El aligustre: unas varas y sus hojas, pocas y con polvo; en invierno, casi pelado.
            val raiz = Offset(j.right - 22f, yt + 3.6f)
            val hojas = when (m.ficha.estacion) { Estacion.INVIERNO -> 18; Estacion.OTONO -> 44; else -> 70 }
            val verde = lerp(Color(0xFF5E7A4E), Color(0xFF8A9078), 0.3f)
            val varas = (0..6).map { val ang = -1.57f + (it - 3) * 0.3f + az.entre(-0.1f, 0.1f); val largo = az.entre(20f, 34f); raiz + Offset(cos(ang) * largo, sin(ang) * largo) }
            for (fin in varas) drawPath(Path().apply { moveTo(raiz.x, raiz.y); cubicTo(raiz.x, raiz.y - 6f, fin.x, fin.y + 8f, fin.x, fin.y) }, p.cara(raiz, Color(0xFF5A4A3A)), style = Stroke(0.9f, cap = StrokeCap.Round))
            repeat(hojas) { val v = varas[it % varas.size]; val k = az.entre(0.3f, 1f); val q = raiz + (v - raiz) * k + Offset(az.entre(-3f, 3f), az.entre(-2f, 2f)); val ang = az.entre(0f, 3.14f)
                drawLine(p.cara(q, if ((q.x - raiz.x) * p.r.lat > 0f) lerp(verde, C.LUZ, 0.25f) else lerp(verde, Color(0xFF2E3A2A), 0.3f)), q, q + Offset(cos(ang) * 3.2f, -sin(ang) * 2f), 1.6f, StrokeCap.Round) }
            if (EscenaDistopia.nevado(m)) for (fin in varas) drawCircle(l.tono(C.NIEVE), 1.4f, fin)
        }
    }

    // ---------------------------------------------------------------- El cartel del mes

    /**
     * El cartel grande de la plaza, sin texto: un tablero sobre dos pies de hierro, con el cartel de este mes; por una
     * esquina despegada asoma el del mes pasado. De noche lo alumbran tres focos. En el pueblo es uno solo, viejo y
     * descolorido por el sol.
     */
    private fun cartelCosa(m: Momento, p: Pincel): Cosa {
        val z = CARTEL; val suelo = 240f
        val forma = Path().apply { addRect(Rect(z.left, z.top, z.right, z.bottom)); addRect(Rect(z.left + 12f, z.bottom, z.left + 16f, suelo)); addRect(Rect(z.right - 16f, z.bottom, z.right - 12f, suelo + 2f)) }
        return Cosa(forma, Offset(z.center.x, suelo)) {
            val l = p.l; val capital = EscenaDistopia.lugar(m) == Lugar.CAPITAL; val az = Azar(Semilla.de("distopia", "cartel"))
            val hierro = p.cara(Offset(z.left + 14f, 200f), C.HIERRO, if (p.r.lat >= 0f) 1f else -1f); val hierroOsc = p.cara(Offset(z.left + 14f, 200f), lerp(C.HIERRO, C.HOLLIN, 0.5f), if (p.r.lat >= 0f) -1f else 1f)
            // Los dos pies, con sus riostras y sus dados.
            for (x in listOf(z.left + 12f, z.right - 16f)) {
                drawRect(hierroOsc, Offset(x, z.bottom - 2f), Size(4f, suelo - z.bottom + 2f)); drawRect(hierro, Offset(if (p.r.lat >= 0f) x + 2.6f else x, z.bottom - 2f), Size(1.4f, suelo - z.bottom + 2f))
                drawRect(p.cara(Offset(x, suelo), C.HORMIGON_OSC), Offset(x - 3f, suelo - 4f), Size(10f, 5f)); drawRect(l.tono(C.OXIDO).copy(alpha = 0.6f), Offset(x, suelo - 14f), Size(4f, 10f))
            }
            drawLine(hierroOsc, Offset(z.left + 14f, z.bottom + 4f), Offset(z.right - 14f, 214f), 1f); drawLine(hierroOsc, Offset(z.right - 14f, z.bottom + 4f), Offset(z.left + 14f, 214f), 1f)
            drawLine(hierroOsc, Offset(z.left + 14f, 214f), Offset(z.right - 14f, 214f), 1.2f)
            // El tablero: el marco, el papel en cuatro paños, y el cartel.
            drawRect(hierroOsc, Offset(z.left - 2f, z.top - 2f), Size(z.width + 4f, z.height + 4f))
            val papel = Rect(z.left + 1.4f, z.top + 1.4f, z.right - 1.4f, z.bottom - 1.4f)
            // De noche el papel no recibe la luz de las farolas de la plaza: solo la de sus tres focos, que cae de arriba abajo.
            val pp = if (p.deNoche) Pincel(l, p.r, Alumbrado(l, emptyList())) else p
            val viejo = if (capital) 0f else 0.6f
            clipRect(papel.left, papel.top, papel.right, papel.bottom) {
                cartelSinTexto(pp, papel, if (capital) EscenaDistopia.cartelDelMes(m) else 0, viejo)
                // Las juntas de los paños, las arrugas del engrudo y el cerco de la lluvia.
                for (k in 1..3) drawLine(l.tono(C.HOLLIN).copy(alpha = 0.22f), Offset(papel.left + papel.width * k / 4f, papel.top), Offset(papel.left + papel.width * k / 4f + 0.6f, papel.bottom), 0.4f)
                repeat(14) { val q = Offset(az.entre(papel.left, papel.right), az.entre(papel.top, papel.bottom)); drawLine(l.tono(if (it % 2 == 0) C.LUZ else C.HOLLIN).copy(alpha = 0.14f), q, q + Offset(az.entre(4f, 12f), az.entre(-1.4f, 1.4f)), 0.6f) }
                repeat(5) { val x = az.entre(papel.left, papel.right); drawRect(Brush.verticalGradient(listOf(l.tono(C.HOLLIN).copy(alpha = 0.26f), Color.Transparent), papel.top, papel.top + az.entre(8f, 24f)), Offset(x, papel.top), Size(az.entre(1f, 3f), 24f)) }
                // La esquina despegada: debajo, el cartel del mes pasado; encima, el revés del papel, que cuelga.
                val e = Offset(papel.left, papel.bottom); val pico = poligono(e, e + Offset(22f, 0f), e + Offset(0f, -18f))
                clipPath(pico) { cartelSinTexto(pp, Rect(papel.left, papel.bottom - 40f, papel.left + 50f, papel.bottom), Math.floorMod(EscenaDistopia.cartelDelMes(m) - 1, 6), 0.35f + viejo * 0.5f) }
                drawPath(poligono(e + Offset(22f, 0f), e + Offset(0f, -18f), e + Offset(15f, -13f)), p.cara(e, lerp(C.HUESO, C.HORMIGON, 0.3f)))
                drawLine(l.tono(C.HOLLIN).copy(alpha = 0.3f), e + Offset(22f, 0f), e + Offset(0f, -18f), 0.5f)
                // La sombra del marco, arriba y del lado del sol.
                drawRect(p.r.color, Offset(papel.left, papel.top), Size(papel.width, 1.6f), alpha = 0.5f * p.r.contraste, blendMode = BlendMode.Multiply)
                // De noche, la luz de los tres focos cae de arriba abajo.
                if (l.noche > 0.3f) drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF2A2440).copy(alpha = 0.5f * l.noche)), papel.top + 6f, papel.bottom), papel.topLeft, papel.size, blendMode = BlendMode.Multiply)
                if (capital && EscenaDistopia.farolas(m)) for (k in 0..2) { val fx = papel.left + 14f + k * 28f; haloDeBruma(Offset(fx, papel.top + 4f), 14f, 26f, lerp(C.SODIO, Color.White, 0.4f), 0.5f * l.noche) }
            }
            if (capital) for (k in 0..2) {
                val fx = z.left + 15f + k * 28f; val cabeza = Offset(fx, z.top - 6f)
                drawLine(hierroOsc, Offset(fx, z.top - 2f), cabeza + Offset(-3f, -1f), 0.7f); drawPath(poligono(cabeza + Offset(-5f, -2.4f), cabeza + Offset(1f, -2.4f), cabeza + Offset(2.4f, 1f), cabeza + Offset(-4f, 1f)), hierro)
                if (EscenaDistopia.farolas(m)) drawOval(lerp(C.SODIO, Color.White, 0.6f), cabeza + Offset(-3.6f, 0.4f), Size(5.6f, 1.4f))
            }
            if (EscenaDistopia.nevado(m)) drawRect(l.tono(C.NIEVE), Offset(z.left - 2f, z.top - 3.4f), Size(z.width + 4f, 1.6f))
        }
    }

    // ---------------------------------------------------------------- Las cámaras

    /** Dónde va cada cámara, por orden de llegada: (sitio, hacia dónde mira, tamaño). Las cinco primeras están desde la temporada 1. */
    private val CAMARAS = listOf(
        Triple(Offset(POSTE.x + 3f, 122f), 1f, 1f), Triple(Offset(50f, 146f), 1f, 0.8f), Triple(Offset(EscenaDistopia.FAROLA.x, 122f), -1f, 0.74f),
        Triple(Offset(CiudadDistopia.CASETA.left + 1f, CiudadDistopia.CASETA.top + 1f), -1f, 0.6f), Triple(Offset(CARTEL.left - 2f, CARTEL.top + 8f), -1f, 0.95f),
        Triple(Offset(BANCO.right + 9f, 223f), -1f, 0.9f), Triple(Offset(COLUMNA.x + 1f, 184f), 1f, 0.8f), Triple(Offset(228f, 126f), 1f, 0.56f), Triple(Offset(POSTE.x - 3f, 164f), -1f, 0.95f),
    )

    /** Una cámara: la caja gris, del color de las paredes, con su visera y su ojo de cristal. Las nuevas llevan un piloto rojo. */
    private fun DrawScope.camara(p: Pincel, sitio: Offset, hacia: Float, k: Float, piloto: Boolean) {
        val c = sitio + Offset(hacia * 6f * k, 3f * k)
        drawLine(p.cara(sitio, C.HIERRO), sitio, c + Offset(-hacia * 1f * k, -1f * k), 1.1f * k)
        val caja = poligono(c + Offset(-hacia * 4.6f * k, -2.6f * k), c + Offset(hacia * 5f * k, -0.6f * k), c + Offset(hacia * 4.4f * k, 2.8f * k), c + Offset(-hacia * 5.2f * k, 0.8f * k))
        drawPath(caja, Brush.verticalGradient(listOf(p.arriba(c, C.HORMIGON), p.cara(c, C.HORMIGON_OSC, hacia)), c.y - 2.6f * k, c.y + 2.8f * k))
        drawPath(poligono(c + Offset(-hacia * 5f * k, -3.2f * k), c + Offset(hacia * 6.6f * k, -0.9f * k), c + Offset(hacia * 6.4f * k, -0.1f * k), c + Offset(-hacia * 5.2f * k, -2.4f * k)), p.cara(c, C.HIERRO))
        val ojo = c + Offset(hacia * 4.7f * k, 1.1f * k)
        drawCircle(p.cara(c, C.HOLLIN), 1.7f * k, ojo); drawCircle(lerp(C.CRISTAL, Color.White, 0.3f).copy(alpha = 0.8f), 0.6f * k, ojo + Offset(-0.4f * k, -0.5f * k))
        if (piloto) { val q = c + Offset(-hacia * 2.4f * k, 0.2f * k); drawCircle(C.PILOTO, 0.9f * k, q); resplandor(q, 5f * k, 0.5f + 0.4f * p.l.noche, C.PILOTO) }
    }

    private fun DrawScope.camaras(m: Momento, p: Pincel) {
        val n = EscenaDistopia.camaras(m); val pilotos = EscenaDistopia.pilotos(m)
        for (i in 0 until n) { val (sitio, hacia, k) = CAMARAS[i]; camara(p, sitio, hacia, k, pilotos) }
    }

    /** La ristra de bombillas de las fiestas, del poste a la farola: el brindis es obligatorio, y el adorno también. */
    private fun DrawScope.bombillas(m: Momento, p: Pincel) {
        val a0 = Offset(POSTE.x + 2f, 110f); val b0 = if (EscenaDistopia.lugar(m) == Lugar.CAPITAL) Offset(EscenaDistopia.FAROLA.x, 112f) else Offset(CARTEL.left, CARTEL.top + 6f)
        val on = p.l.noche > 0.3f && EscenaDistopia.corriente(m)
        val pts = (0..16).map { i -> val t = i / 16f; Offset(a0.x + (b0.x - a0.x) * t, a0.y + (b0.y - a0.y) * t + 22f * 4f * t * (1f - t)) }
        drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) lineTo(q.x, q.y) }, p.l.tono(C.HOLLIN), style = Stroke(0.6f))
        for ((i, q) in pts.withIndex()) { if (i == 0 || i == 16) continue
            val color = listOf(C.SODIO, C.ROJO, C.HUESO)[i % 3]
            drawLine(p.l.tono(C.HOLLIN), q, q + Offset(0f, 1.6f), 0.6f)
            drawOval(if (on) lerp(if (i % 3 == 1) C.PILOTO else color, Color.White, 0.35f) else p.l.tono(color), q + Offset(-1.5f, 1.4f), Size(3f, 3.8f))
            if (on) resplandor(q + Offset(0f, 3f), 8f, 0.55f, if (i % 3 == 1) C.PILOTO else C.SODIO) }
    }

    // ---------------------------------------------------------------- La parte viva

    fun DrawScope.plazaViva(m: Momento, l: Luces, reloj: Float) {
        val viento = -(0.25f + 0.75f * m.viento)
        val capital = EscenaDistopia.lugar(m) == Lugar.CAPITAL
        // El vaho de la alcantarilla: con frío se ve más.
        if (capital) humo(l, TAPA + Offset(0f, -1f), 30f, 9f, reloj, viento * 0.6f, 0.02f, 0f, 5, 7, if (m.ficha.estacion == Estacion.INVIERNO || l.noche > 0.5f) 0.7f else 0.35f)
        // El bidón: la llama, las chispas y el humo; apagado, las brasas hasta la madrugada.
        val boca = Offset(EscenaDistopia.BIDON.x, 243f)
        if (capital && EscenaDistopia.bidon(m)) {
            llama(boca + Offset(-3f, 0f), 13f, reloj, 1); llama(boca + Offset(3.4f, 0f), 10f, reloj, 2)
            resplandor(boca + Offset(0f, -6f), 40f, 0.25f + 0.45f * l.noche)
            humo(l, boca + Offset(0f, -12f), 48f, 9f, reloj, viento, 0.6f, 0f, 6, 8, 0.7f)
            for (i in 0..3) { val t = ((reloj * 0.6f + i * 0.27f) % 1f); drawCircle(Color(0xFFFFC060).copy(alpha = (1f - t) * 0.9f), 0.6f, boca + Offset(sin(i * 2.1f + reloj) * 5f + viento * 10f * t, -8f - 24f * t)) }
        } else if (capital && l.noche > 0.4f && !EscenaDistopia.apagon(m) && m.ficha.estacion == Estacion.INVIERNO) brasas(l, boca + Offset(0f, 0.4f), 13f, reloj)
        // El cigarrillo del banco humea.
        if (m.ficha.objeto == "cigarrillo") humo(l, SOBRE + Offset(13f * KO, -3f * KO), 20f, 4f, reloj, viento * 0.4f, 0.05f, 0f, 9, 6, 0.6f)
        // Los pilotos de las cámaras: no se apagan, pero laten.
        if (EscenaDistopia.pilotos(m)) for (i in 0 until EscenaDistopia.camaras(m)) { val (sitio, hacia, k) = CAMARAS[i]; val q = sitio + Offset(hacia * 3.6f * k, 3.2f * k); drawCircle(C.PILOTO.copy(alpha = (0.35f + 0.3f * sin(reloj * 3f + i)).coerceIn(0f, 1f)), 2f * k, q) }
    }

    // ---------------------------------------------------------------- El gato

    fun estorbos(m: Momento): List<Float> = if (EscenaDistopia.lugar(m) == Lugar.PROVINCIA) emptyList() else buildList {
        add(EscenaDistopia.BIDON.x)
        if (EscenaDistopia.conGente(m)) { add(222f); add(279f) }
    }

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 14f, 283f, hasta = 336f))
        add(Ancla(TipoAncla.MIRADOR, 324f, JARDINERA.top + 4f))                // en el borde de la jardinera
        add(Ancla(TipoAncla.OBJETO, SOBRE.x, 283f))
        add(Ancla(TipoAncla.ABRIGO, 322f, 282f))                               // al pie del cartel, que le hace de techo
        if (EscenaDistopia.lugar(m) == Lugar.CAPITAL && EscenaDistopia.bidon(m)) add(Ancla(TipoAncla.FUEGO, EscenaDistopia.BIDON.x - 36f, 283f, hasta = EscenaDistopia.BIDON.x))
        if (EscenaDistopia.fiesta(m) || EscenaDistopia.desfile(m)) add(Ancla(TipoAncla.ADORNO, POSTE.x + 20f, 170f))
    }
}
