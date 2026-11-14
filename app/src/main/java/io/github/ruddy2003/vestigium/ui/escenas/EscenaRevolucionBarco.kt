package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.banderines
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.guirnalda
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * El primer plano: la popa del barco-lavadero. A la izquierda, la pared del cuarto del maestro Gaspard y, encima, el
 * secadero; la escalera; la cuba de la colada; el hornillo con su caldero; los cajones de lavar, con sus tablas hacia
 * el agua; el banco con el objeto del día; los cestos; el tonel de la ceniza; el palo de popa con su farol, y la cuerda
 * con la ropa tendida, que enmarca el cuadro.
 */
internal object BarcoRevolucion {
    private val C = ColorRevolucion
    private val E = EscenaRevolucion
    private const val BORDA = EscenaRevolucion.BORDA
    private const val PISO = EscenaRevolucion.PISO_GATO

    /** La pared del cuarto de popa llega hasta aquí, y aquí pisa. */
    private const val PARED = 52f
    private const val PIE_PARED = 236f

    /** El suelo del secadero (el piso de arriba). */
    private const val ALTILLO = 94f

    /** La boca del hornillo (su lumbre) y el sitio del objeto del día, sobre el banco. */
    internal val BOCA = Offset(186f, 263f)
    internal val SOBRE = Offset(268f, 250.6f)

    /** Los dos cabos de la cuerda de tender: lo alto del pie derecho del secadero y lo alto del palo de popa. Va alta: pasa por encima de las torres de Notre-Dame. */
    private val CABO_A = Offset(58f, 17f)
    private val CABO_B = Offset(361f, 7f)

    /** El palo de popa. */
    private const val PALO = 362f

    /** Cómo se alumbra cada cosa: de día, con el sol de un lado; de noche, con el farol o la ventana que tenga más cerca. */
    private class Luz(val l: Luces, val r: Rayo, val a: Alumbrado) {
        val k = r.contraste * r.dia
        fun c(p: Offset, col: Color): Color = a.en(p, col)
        /** De qué lado le viene la luz a lo que está en [p]: 1, de la derecha; −1, de la izquierda. */
        fun lado(p: Offset): Float {
            if (a.focos.isEmpty() || l.noche < 0.5f) return if (r.lat > 0f) 1f else -1f
            val f = a.focos.maxByOrNull { it.fuerza / (1f + (it.p - p).getDistanceSquared() / (it.alcance * it.alcance)) }!!
            return if (f.p.x >= p.x) 1f else -1f
        }
        fun claro(p: Offset, col: Color): Color = a.en(p, lerp(col, Color(0xFFFFF2D2), 0.32f * k + 0.1f * l.noche))
        fun oscuro(p: Offset, col: Color): Color = lerp(a.en(p, mult(col, r.color, 0.75f * k)), l.tono(mult(col, ColorRevolucion.SOMBRA, 0.75f)), 0.6f * l.noche)
        /** El degradado de un cuerpo redondo entre [x0] y [x1]. */
        fun redondo(p: Offset, col: Color, x0: Float, x1: Float): Brush {
            val s = lado(p); val cl = claro(p, col); val md = c(p, col); val os = oscuro(p, col)
            return Brush.horizontalGradient(0f to md, 0.2f to cl, 0.5f to md, 0.82f to os, 1f to lerp(os, md, 0.4f), startX = if (s > 0f) x1 else x0, endX = if (s > 0f) x0 else x1)
        }
    }

    // ---------------------------------------------------------------- El gato

    /** Lo plantado en el suelo por donde anda el gato: los dos cestos de junto a la puerta, la cuba, el hornillo y el cesto grande. */
    fun estorbos(m: Momento): List<Float> = listOf(66f, 90f, 126f, 186f, 326f)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 56f, PISO, hasta = 372f))
        add(Ancla(TipoAncla.MIRADOR, 346f, 222.6f))                                 // sobre el tonel de la ceniza, mirando el río
        if (m.ficha.objeto != null && E.lugar(m) != EscenaRevolucion.Lugar.HELADA) add(Ancla(TipoAncla.OBJETO, SOBRE.x, PISO))
        add(Ancla(TipoAncla.ABRIGO, 86f, PISO))                                     // debajo de la escalera del secadero
        if (E.lumbre(m)) add(Ancla(TipoAncla.FUEGO, 156f, PISO, hasta = BOCA.x))                // se echa entre la cuba y el hornillo
        if (E.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 300f, PISO))
    }

    // ---------------------------------------------------------------- La cubierta, la pared y el secadero

    /** Las tablas de la cubierta, de proa a popa: más anchas cuanto más cerca, con sus juntas, sus clavos y lo mojado. */
    private fun DrawScope.cubierta(m: Momento, z: Luz) {
        val l = z.l
        drawRect(Brush.verticalGradient(0f to z.c(Offset(190f, BORDA), lerp(C.MADERA, C.PIEDRA_OSC, 0.3f)), 1f to z.c(Offset(190f, ALTO), lerp(C.MADERA, C.OCRE, 0.2f)), startY = BORDA, endY = ALTO), Offset(0f, BORDA), Size(ANCHO, ALTO - BORDA))
        val az = Azar(Semilla.de("revolucion", "cubierta"))
        var y = BORDA; var i = 0
        while (y < ALTO) {
            val alto = 4.6f + i * 1.9f
            // Cada tabla, de un tono; alguna, más nueva.
            var x = -az.entre(0f, 40f)
            while (x < ANCHO) { val largo = az.entre(60f, 130f); val p = Offset(x + largo / 2f, y + alto / 2f); drawRect(z.c(p, lerp(C.MADERA, if (az.entero(5) == 0) C.OCRE else C.MADERA_OSC, az.entre(0.05f, 0.3f))).copy(alpha = 0.55f), Offset(x, y), Size(largo - 0.6f, alto - 0.5f)); drawLine(z.c(p, C.MADERA_OSC).copy(alpha = 0.7f), Offset(x + largo - 0.3f, y), Offset(x + largo - 0.3f, y + alto), 0.5f); x += largo }
            drawLine(z.c(Offset(190f, y), C.BREA).copy(alpha = 0.6f), Offset(0f, y), Offset(ANCHO, y), 0.5f + i * 0.08f)
            drawLine(z.c(Offset(190f, y), lerp(C.MADERA, C.CAL, 0.4f)).copy(alpha = 0.3f), Offset(0f, y + 0.7f), Offset(ANCHO, y + 0.7f), 0.35f)
            // Las vetas y los clavos.
            repeat(9) { val x0 = az.entre(0f, ANCHO); drawLine(z.c(Offset(x0, y), C.MADERA_OSC).copy(alpha = 0.22f), Offset(x0, y + az.entre(1f, alto - 1f)), Offset(x0 + az.entre(14f, 40f), y + az.entre(1f, alto - 1f)), 0.35f) }
            val clavos = ArrayList<Offset>(); var xc = az.entre(0f, 30f); while (xc < ANCHO) { clavos += Offset(xc, y + 1.2f); clavos += Offset(xc, y + alto - 1.2f); xc += az.entre(60f, 110f) }
            drawPoints(clavos, PointMode.Points, z.c(Offset(190f, y), C.BREA).copy(alpha = 0.6f), 0.9f, StrokeCap.Round)
            y += alto; i++
        }
        // El desgaste del paso y lo mojado de alrededor de la cuba y de los cajones (o todo, si llueve).
        repeat(7) { mancha(Offset(az.entre(80f, 360f), az.entre(BORDA + 14f, ALTO - 6f)), az.entre(16f, 34f), az.entre(3f, 7f), z.c(Offset(190f, 260f), lerp(C.MADERA, C.CAL, 0.35f)), 0.2f) }
        val agua = z.c(Offset(190f, 250f), mult(C.MADERA_OSC, C.SOMBRA, 0.5f))
        if (!E.helada(m) && (E.lavando(m) || E.mojado(m))) for ((p, rx) in listOf(Offset(132f, 272f) to 40f, Offset(264f, 242f) to 62f, Offset(196f, 278f) to 26f)) { mancha(p, rx, 6f, agua, 0.42f); mancha(p + Offset(4f, -1f), rx * 0.5f, 2f, l.tono(lerp(l.bajo, Color.White, 0.3f)), 0.22f * (1f - l.noche)) }
        if (E.mojado(m)) { drawRect(agua.copy(alpha = 0.3f), Offset(0f, BORDA), Size(ANCHO, ALTO - BORDA)); val h = m.azar("charcos"); repeat(5) { val p = Offset(h.entre(70f, 360f), h.entre(BORDA + 12f, ALTO - 5f)); mancha(p, h.entre(10f, 22f), h.entre(1.6f, 3.4f), lerp(l.bajo, l.medio, 0.5f), 0.4f) } }
        // Lo que el trabajo deja en las tablas: la paja que se sale de los cajones, la espuma y alguna pinza caída.
        if (!E.nevado(m)) { val h = Azar(Semilla.de("revolucion", "paja")); repeat(46) { val p = Offset(h.entre(206f, 324f), h.entre(240f, 250f) + h.real() * h.real() * 16f); drawLine(z.c(p, lerp(C.PAJA, C.MADERA, h.real() * 0.5f)).copy(alpha = 0.75f), p, p + Offset(h.entre(-4f, 4f), h.entre(-1.2f, 1.2f)), 0.45f, StrokeCap.Round) }
            for (p in listOf(Offset(304f, 277f), Offset(232f, 281f), Offset(118f, 281f))) { drawLine(z.c(p, lerp(C.MADERA, C.OCRE, 0.45f)), p, p + Offset(4.6f, -1.2f), 1.4f, StrokeCap.Round) } }
        if (E.lavando(m)) { val h = m.azar("espumas"); for (o in listOf(Offset(160f, 276f), Offset(240f, 244f), Offset(284f, 246f), Offset(100f, 268f))) { mancha(o, 9f, 2.2f, z.c(o, C.CAL), 0.55f); val pts = ArrayList<Offset>(); repeat(9) { pts += o + Offset(h.entre(-8f, 8f), h.entre(-1.8f, 1.8f)) }; drawPoints(pts, PointMode.Points, z.c(o, Color.White).copy(alpha = 0.8f), 1.2f, StrokeCap.Round) } }
        // La nieve posada: en la helada, pisada por el medio; con nevada, a corros.
        if (E.nevado(m)) {
            val h = Azar(Semilla.de("revolucion", "nieve")); val blanca = z.c(Offset(190f, 250f), lerp(C.CAL, Color.White, 0.4f)); val sombra = z.c(Offset(190f, 250f), lerp(C.HIELO, C.SOMBRA, 0.4f))
            if (E.helada(m)) {
                // Semanas de nieve: lo cubre casi todo, menos la vereda que han pisado de la puerta a la borda.
                drawRect(blanca.copy(alpha = 0.86f), Offset(0f, BORDA + 0.6f), Size(ANCHO, ALTO - BORDA))
                repeat(14) { mancha(Offset(h.entre(0f, ANCHO), h.entre(BORDA + 6f, ALTO)), h.entre(20f, 50f), h.entre(2f, 4.6f), sombra, 0.3f) }
                val vereda = z.c(Offset(190f, 262f), lerp(C.MADERA, C.MADERA_OSC, 0.3f))
                for (i in 0..13) { val k = i / 13f; mancha(Offset(50f + 170f * k, 258f - 10f * kotlin.math.sin(k * 3.1f) + h.entre(-2f, 2f)), h.entre(12f, 20f), h.entre(2.4f, 4f), vereda, 0.6f) }
                val pisadas = ArrayList<Offset>(); repeat(40) { val k = h.real(); pisadas += Offset(60f + 280f * k + h.entre(-4f, 4f), 268f + h.entre(-9f, 12f)) }
                drawPoints(pisadas, PointMode.Points, sombra.copy(alpha = 0.7f), 2.4f, StrokeCap.Round)
            } else repeat(16) { mancha(Offset(h.entre(54f, ANCHO), h.entre(BORDA + 3f, ALTO)), h.entre(16f, 44f), h.entre(2.4f, 6f), blanca, 0.8f) }
            drawRect(blanca, Offset(PARED, BORDA + 0.6f), Size(ANCHO - PARED, 2.8f))
            if (E.helada(m) && l.noche > 0.3f) drawRect(Brush.verticalGradient(0f to Color(0xFFC6D4F0).copy(alpha = 0.22f * l.noche), 1f to Color(0xFFC6D4F0).copy(alpha = 0.1f * l.noche), startY = BORDA, endY = ALTO), Offset(0f, BORDA), Size(ANCHO, ALTO - BORDA), blendMode = BlendMode.Plus)
        }
    }

    /** La borda: el tablón que remata la popa por el lado del río. */
    private fun DrawScope.borda(m: Momento, z: Luz) {
        drawRect(z.c(Offset(200f, BORDA), C.MADERA_OSC), Offset(PARED, BORDA - 3.4f), Size(ANCHO - PARED, 4.4f))
        drawRect(z.claro(Offset(200f, BORDA), C.MADERA), Offset(PARED, BORDA - 4.2f), Size(ANCHO - PARED, 1.5f))
        for (x in listOf(96f, 160f, 204f, 330f, 376f)) drawRect(z.c(Offset(x, BORDA), C.BREA).copy(alpha = 0.6f), Offset(x, BORDA - 3.4f), Size(0.6f, 4.4f))
        if (E.nevado(m)) drawRect(z.c(Offset(200f, BORDA), C.CAL), Offset(PARED, BORDA - 5.4f), Size(ANCHO - PARED, 1.6f))
    }

    /** La pared del cuarto de popa (la puerta, la ventana, los carteles, la escarapela) y, encima, el secadero con su tejadillo. */
    private fun DrawScope.casa(m: Momento, z: Luz) {
        val l = z.l; val az = Azar(Semilla.de("revolucion", "pared"))
        // El secadero: el fondo en penumbra, la ropa colgada dentro, los pies derechos, la baranda y el tejadillo.
        drawRect(z.oscuro(Offset(30f, 50f), lerp(C.MADERA_OSC, C.BREA, 0.4f)), Offset(0f, 0f), Size(58f, ALTILLO))
        if (!E.barcoCerrado(m) || E.lugar(m) == EscenaRevolucion.Lugar.MARCHA) { val h = m.azar("secadero"); var x = 2f; while (x < 52f) { val w = h.entre(7f, 13f); val alto = h.entre(26f, 44f); val p = Offset(x + w / 2f, 40f); val col = if (h.entero(6) == 0) C.ANIL else C.CAL
            drawRect(z.c(p, lerp(col, C.PIZARRA, 0.35f)), Offset(x, 22f), Size(w, alto)); drawRect(z.c(p, lerp(col, C.PIZARRA_OSC, 0.5f)).copy(alpha = 0.5f), Offset(x + w * 0.6f, 22f), Size(w * 0.18f, alto)); x += w + h.entre(1f, 4f) } }
        for (x in listOf(3f, 29f, 55f)) { drawRect(z.c(Offset(x, 50f), C.MADERA_OSC), Offset(x - 2f, 12f), Size(4f, ALTILLO - 12f)); drawRect(z.claro(Offset(x, 50f), C.MADERA), Offset(if (z.lado(Offset(x, 50f)) > 0f) x + 0.6f else x - 2f, 12f), Size(1.4f, ALTILLO - 12f)) }
        drawRect(z.c(Offset(30f, 74f), C.MADERA), Offset(0f, 72f), Size(58f, 2.6f)); drawRect(z.claro(Offset(30f, 74f), C.MADERA), Offset(0f, 72f), Size(58f, 0.8f))
        var xb = 6f; while (xb < 56f) { drawRect(z.c(Offset(xb, 82f), C.MADERA_OSC), Offset(xb, 74.6f), Size(1.3f, ALTILLO - 74.6f)); xb += 4.6f }
        if (E.banderas(m) >= 3) {
            val o = Offset(30f, 80f); val x0 = 9f; val x1 = 51f
            val forma = Path().apply { moveTo(x0, 71.4f); lineTo(x1, 71.4f); lineTo(x1 + 0.6f, 88f); cubicTo(x1 - 8f, 91f, x1 - 14f, 87f, x1 - 21f, 89.6f); cubicTo(x0 + 12f, 92f, x0 + 6f, 88f, x0 - 0.6f, 90f); close() }
            clipPath(forma) {
                for ((i, col) in listOf(C.ANIL, C.CAL, C.GRANZA).withIndex()) drawRect(z.c(o, col), Offset(x0 - 1f + i * 14.4f, 70f), Size(14.6f, 24f))
                for (i in 1..5) drawLine(z.oscuro(o, C.PIZARRA).copy(alpha = 0.35f), Offset(x0 + i * 7f, 71f), Offset(x0 + i * 7f - 1f, 92f), 0.7f)
                drawRect(z.oscuro(o, C.PIZARRA_OSC).copy(alpha = 0.3f), Offset(x0 - 1f, 86f), Size(44f, 6f))
            }
            drawRect(z.claro(o, C.MADERA), Offset(0f, 71.6f), Size(58f, 0.9f))
        }
        // El tejadillo, visto por debajo: los cabios y el alero.
        drawPath(poligono(Offset(0f, 0f), Offset(70f, 0f), Offset(66f, 12f), Offset(0f, 15f)), z.c(Offset(30f, 8f), lerp(C.PIZARRA_OSC, C.BREA, 0.4f)))
        for (i in 0..6) drawLine(z.c(Offset(30f, 8f), C.MADERA_OSC), Offset(4f + i * 10f, 0f), Offset(2f + i * 10f, 14f), 1.2f)
        drawLine(z.claro(Offset(30f, 12f), C.MADERA), Offset(0f, 15f), Offset(66f, 12f), 1.6f)
        if (E.nevado(m)) { drawLine(z.c(Offset(30f, 12f), C.CAL), Offset(0f, 13.6f), Offset(67f, 10.6f), 2.2f); for (i in 0..8) drawPath(poligono(Offset(4f + i * 7f, 15f), Offset(5.4f + i * 7f, 15f), Offset(4.6f + i * 7f, 15f + 3f + (i % 3) * 2f)), z.c(Offset(30f, 16f), lerp(C.HIELO, Color.White, 0.5f))) }
        // La viga del piso, que vuela un poco, con su sombra sobre la pared.
        drawRect(z.c(Offset(30f, ALTILLO), C.MADERA_OSC), Offset(0f, ALTILLO - 2f), Size(60f, 6.4f)); drawRect(z.claro(Offset(30f, ALTILLO), C.MADERA), Offset(0f, ALTILLO - 2f), Size(60f, 1.4f))
        for (x in listOf(10f, 30f, 50f)) drawRect(z.c(Offset(x, ALTILLO), C.BREA), Offset(x, ALTILLO + 4.4f), Size(3.4f, 3f))
        // La pared de tabla embreada: cada tabla de su tono, con sus juntas, sus nudos y el zócalo comido por el agua.
        val tabla = lerp(C.BREA, C.MADERA_OSC, 0.55f)
        drawRect(z.c(Offset(26f, 170f), tabla), Offset(0f, ALTILLO + 4.4f), Size(PARED, PIE_PARED - ALTILLO - 4.4f))
        var x = 0f; while (x < PARED) { val w = az.entre(5f, 8f); val p = Offset(x + w / 2f, 170f); drawRect(z.c(p, lerp(tabla, if (az.entero(3) == 0) C.MADERA else C.BREA, az.entre(0.1f, 0.4f))).copy(alpha = 0.7f), Offset(x, ALTILLO + 4.4f), Size(w - 0.5f, PIE_PARED - ALTILLO - 4.4f)); drawLine(z.c(p, C.BREA), Offset(x + w - 0.3f, ALTILLO + 4.4f), Offset(x + w - 0.3f, PIE_PARED), 0.5f); if (az.entero(2) == 0) drawOval(z.c(p, C.BREA).copy(alpha = 0.6f), Offset(x + 1.6f, az.entre(110f, 220f)), Size(1.8f, 2.8f)); x += w }
        with(E) { sombraDe(z.r, Offset(0f, ALTILLO + 4.4f), Size(PARED, 5f), 0.6f) }
        drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.c(Offset(26f, 232f), lerp(C.HOJA, C.BREA, 0.6f)).copy(alpha = 0.55f), startY = PIE_PARED - 16f, endY = PIE_PARED), Offset(0f, PIE_PARED - 16f), Size(PARED, 16f))
        drawRect(z.c(Offset(26f, PIE_PARED), C.BREA), Offset(0f, PIE_PARED - 1f), Size(PARED + 1f, 2f))
        drawRect(z.oscuro(Offset(PARED, 170f), tabla), Offset(PARED - 1.4f, ALTILLO + 4.4f), Size(1.4f, PIE_PARED - ALTILLO - 4.4f))
        // La puerta del cuarto, de tablas, con sus herrajes.
        val puerta = Rect(22f, 112f, 48f, PIE_PARED - 1f)
        drawRect(z.c(puerta.center, C.BREA), Offset(puerta.left - 1.4f, puerta.top - 1.6f), Size(puerta.width + 2.8f, puerta.height + 1.6f))
        drawRect(z.c(puerta.center, lerp(C.MADERA, C.MADERA_OSC, 0.45f)), puerta.topLeft, puerta.size)
        for (i in 1..3) drawLine(z.c(puerta.center, C.BREA).copy(alpha = 0.7f), Offset(puerta.left + i * 6.5f, puerta.top), Offset(puerta.left + i * 6.5f, puerta.bottom), 0.4f)
        for (yh in listOf(126f, 176f, 224f)) { drawRect(z.c(Offset(34f, yh), C.HIERRO), Offset(puerta.left, yh), Size(17f, 1.5f)); drawCircle(z.c(Offset(34f, yh), C.HIERRO), 1.1f, Offset(puerta.left + 1.6f, yh + 0.7f)) }
        drawCircle(z.c(Offset(44f, 180f), C.HIERRO), 1.5f, Offset(44.6f, 182f), style = Stroke(0.6f))
        // La ventana del cuarto de Gaspard, de cuatro vidrios: de noche, con luz.
        val v = Rect(4f, 124f, 18f, 142f); val luz = E.ventanaDeGaspard(m)
        drawRect(z.c(v.center, C.BREA), Offset(v.left - 1.4f, v.top - 1.4f), Size(v.width + 2.8f, v.height + 3.4f))
        drawRect(if (luz) lerp(C.ACEITE, Color.White, 0.25f) else z.c(v.center, lerp(C.PIZARRA_OSC, C.SENA, 0.3f)), v.topLeft, v.size)
        if (!luz) drawPath(poligono(Offset(v.left, v.bottom), Offset(v.left, v.top + 6f), Offset(v.right - 3f, v.top), Offset(v.right, v.top), Offset(v.right, v.top + 2f)), l.tono(lerp(l.medio, Color.White, 0.2f)).copy(alpha = 0.22f * (1f - l.noche)))
        drawLine(z.c(v.center, C.MADERA_OSC), Offset(v.center.x, v.top), Offset(v.center.x, v.bottom), 1f); drawLine(z.c(v.center, C.MADERA_OSC), Offset(v.left, v.center.y), Offset(v.right, v.center.y), 1f)
        drawRect(z.claro(v.center, C.MADERA), Offset(v.left - 2f, v.bottom + 0.4f), Size(v.width + 4f, 1.4f))
        if (luz) { resplandor(v.center, 26f, 0.55f * l.noche, C.ACEITE); if (E.niebla(m)) haloDeBruma(v.center, 30f, 22f, C.ACEITE, 0.5f) }
        // Lo que los años van clavando: la escarapela de Jacques y los carteles, sin letras.
        if (E.escarapela(m)) { val o = Offset(35f, 104.6f); for ((rr, col) in listOf(3.6f to C.ANIL, 2.4f to C.CAL, 1.2f to C.GRANZA)) drawCircle(z.c(o, col), rr, o); drawLine(z.c(o, C.ANIL), o + Offset(-1f, 3f), o + Offset(-2.6f, 8f), 1.1f); drawLine(z.c(o, C.GRANZA), o + Offset(1f, 3f), o + Offset(2.4f, 8.4f), 1.1f) }
        val sitios = listOf(Rect(25f, 132f, 34f, 145f), Rect(36f, 136f, 46f, 150f), Rect(26f, 150f, 36f, 161f))
        for (i in 0 until E.carteles(m)) { val p = sitios[i]; drawRect(z.c(p.center, lerp(C.CAL, C.OCRE, 0.2f + 0.12f * i)), p.topLeft, p.size); for (j in 0..3) drawLine(z.c(p.center, C.PIZARRA_OSC).copy(alpha = 0.55f), Offset(p.left + 1.2f, p.top + 2.4f + j * 2.4f), Offset(p.right - 1.2f - (j % 2) * 2f, p.top + 2.4f + j * 2.4f), 0.5f); drawPath(poligono(Offset(p.right, p.bottom), Offset(p.right - 2.4f, p.bottom), Offset(p.right, p.bottom - 2.4f)), z.c(p.center, tabla)) }
        // La muleta de Jacques, apoyada junto a la puerta.
        if (E.muleta(m)) { val col = z.c(Offset(14f, 190f), lerp(C.MADERA, C.OCRE, 0.3f)); drawLine(col, Offset(8f, PIE_PARED + 4f), Offset(15f, 158f), 1.3f, StrokeCap.Round); drawLine(col, Offset(13f, PIE_PARED + 3f), Offset(17.4f, 158f), 1.3f, StrokeCap.Round); drawLine(col, Offset(10.4f, 196f), Offset(15.6f, 196f), 1.2f); drawRoundRect(z.c(Offset(14f, 156f), C.MADERA_OSC), Offset(11.4f, 154f), Size(9.6f, 3.4f), CornerRadius(1.6f)) }
        // La luz de la ventana tendida en la cubierta.
        if (luz) for (f in z.a.focos) if (f.p.x < 60f) charcoDeLuz(f, Offset(70f, 256f), 88f, 18f, C.MADERA, 0.85f)
    }

    /** La escalera que sube al secadero: la zanca, los peldaños y el pasamanos. */
    private fun escalera(m: Momento, z: Luz): Cosa {
        val pie = Offset(106f, 246f); val cabeza = Offset(62f, ALTILLO)
        val forma = poligono(pie + Offset(-5f, 0f), pie + Offset(7f, 0f), cabeza + Offset(7f, 0f), cabeza + Offset(-5f, 0f))
        return Cosa(forma, pie) {
            val dir = cabeza - pie
            drawPath(forma, z.c(pie + dir * 0.5f, C.MADERA_OSC))
            drawLine(z.claro(pie + dir * 0.5f, C.MADERA), pie + Offset(6.4f, 0f), cabeza + Offset(6.4f, 0f), 1.4f)
            for (i in 1..10) { val p = pie + dir * (i / 11f); drawPath(poligono(p + Offset(-8f, 0f), p + Offset(9f, 0f), p + Offset(11.4f, 2.2f), p + Offset(-5.6f, 2.2f)), z.c(p, lerp(C.MADERA, C.OCRE, 0.15f))); drawLine(z.oscuro(p, C.MADERA_OSC), p + Offset(-5.6f, 2.4f), p + Offset(11.4f, 2.4f), 0.8f); drawLine(z.claro(p, C.MADERA), p + Offset(-8f, 0f), p + Offset(9f, 0f), 0.6f); if (E.nevado(m)) drawLine(z.c(p, C.CAL), p + Offset(-7.4f, -0.5f), p + Offset(8.6f, -0.5f), 1.5f) }
            // El pasamanos, con sus tres balaustres.
            val alza = Offset(13f, -22f)
            drawLine(z.c(pie + alza, C.MADERA), pie + alza + Offset(2f, 6f), cabeza + alza, 1.8f, StrokeCap.Round); drawLine(z.claro(pie + alza, C.MADERA), pie + alza + Offset(2.5f, 5.4f), cabeza + alza + Offset(0.5f, -0.6f), 0.6f)
            for (k in listOf(0.08f, 0.5f, 0.94f)) { val p = pie + dir * k; drawLine(z.c(p, C.MADERA_OSC), p + Offset(8f, 0f), p + alza + Offset(0.6f, 2f), 1.2f) }
        }
    }

    // ---------------------------------------------------------------- Las cosas de la popa

    /** La cuba de la colada: duelas, tres aros de hierro y, encima, el saco de la ceniza sobre la ropa blanca. */
    private fun cuba(m: Momento, z: Luz): Cosa {
        val pie = Offset(126f, 268f); val arriba = 217f
        val forma = Path().apply { moveTo(pie.x - 25f, pie.y - 3f); lineTo(pie.x - 30f, arriba); cubicTo(pie.x - 30f, arriba - 9f, pie.x + 30f, arriba - 9f, pie.x + 30f, arriba); lineTo(pie.x + 25f, pie.y - 3f); cubicTo(pie.x + 25f, pie.y + 5f, pie.x - 25f, pie.y + 5f, pie.x - 25f, pie.y - 3f); close() }
        return Cosa(forma, pie) {
            drawPath(forma, z.redondo(pie, C.MADERA, pie.x - 30f, pie.x + 30f))
            clipPath(forma) {
                for (i in -6..6) { val x = pie.x + i * 4.4f; drawLine(z.c(pie, C.MADERA_OSC).copy(alpha = 0.6f), Offset(x * 1f + i * 0.7f, arriba), Offset(x, pie.y + 4f), 0.5f) }
                for (y in listOf(arriba + 9f, arriba + 27f, pie.y - 7f)) { val w = 30f - (y - arriba) / (pie.y - arriba) * 5f; drawPath(Path().apply { moveTo(pie.x - w - 1f, y); cubicTo(pie.x - w, y + 6.4f, pie.x + w, y + 6.4f, pie.x + w + 1f, y) }, z.c(pie, C.HIERRO), style = Stroke(2.2f)); drawPath(Path().apply { moveTo(pie.x - w - 1f, y - 0.8f); cubicTo(pie.x - w, y + 5.6f, pie.x + w, y + 5.6f, pie.x + w + 1f, y - 0.8f) }, z.claro(pie, lerp(C.HIERRO, C.PIZARRA, 0.5f)).copy(alpha = 0.6f), style = Stroke(0.5f)) }
                // El agua que rebosa y chorrea, y el verdín de abajo.
                if (E.lavando(m)) for (x in listOf(pie.x - 14f, pie.x + 6f, pie.x + 19f)) drawLine(z.c(pie, mult(C.MADERA_OSC, C.SOMBRA, 0.6f)).copy(alpha = 0.6f), Offset(x, arriba + 4f), Offset(x + 0.6f, pie.y), 1.6f)
                drawRect(z.c(pie, lerp(C.HOJA, C.BREA, 0.6f)).copy(alpha = 0.3f), Offset(pie.x - 30f, pie.y - 6f), Size(60f, 12f))
            }
            // La boca: el borde de las duelas y lo que hay dentro.
            val boca = Rect(pie.x - 30f, arriba - 7f, pie.x + 30f, arriba + 7f)
            drawOval(z.oscuro(pie, C.MADERA_OSC), boca.topLeft, boca.size)
            val dentro = Rect(pie.x - 27f, arriba - 5.4f, pie.x + 27f, arriba + 5.6f)
            when {
                E.helada(m) -> { drawOval(z.c(pie, C.CAL), dentro.topLeft, dentro.size); drawPath(Path().apply { moveTo(pie.x - 26f, arriba); cubicTo(pie.x - 16f, arriba - 13f, pie.x + 14f, arriba - 14f, pie.x + 26f, arriba); close() }, z.claro(pie, C.CAL)) }
                E.barcoCerrado(m) -> { drawOval(z.c(pie, C.BREA), dentro.topLeft, dentro.size); drawPath(poligono(Offset(pie.x - 29f, arriba - 2f), Offset(pie.x + 29f, arriba - 3f), Offset(pie.x + 29f, arriba + 1f), Offset(pie.x - 29f, arriba + 2f)), z.c(pie, C.MADERA)) }   // tapada con una tabla
                else -> {
                    // La ropa blanca en capas y, encima, el saco de la ceniza, gris y mojado.
                    drawOval(z.c(pie, C.CAL), dentro.topLeft, dentro.size)
                    val saco = Path().apply { moveTo(pie.x - 24f, arriba + 1f); cubicTo(pie.x - 22f, arriba - 12f, pie.x - 4f, arriba - 15f, pie.x + 6f, arriba - 13f); cubicTo(pie.x + 18f, arriba - 12f, pie.x + 25f, arriba - 6f, pie.x + 24f, arriba + 1f); cubicTo(pie.x + 10f, arriba + 6f, pie.x - 10f, arriba + 6f, pie.x - 24f, arriba + 1f); close() }
                    drawPath(saco, z.redondo(pie, lerp(C.PIEDRA_OSC, C.PIZARRA, 0.35f), pie.x - 24f, pie.x + 24f))
                    clipPath(saco) { for (i in 0..5) drawPath(Path().apply { moveTo(pie.x - 20f + i * 8f, arriba - 14f); cubicTo(pie.x - 22f + i * 8f, arriba - 6f, pie.x - 18f + i * 8f, arriba, pie.x - 20f + i * 8f, arriba + 6f) }, z.oscuro(pie, C.PIEDRA_OSC).copy(alpha = 0.5f), style = Stroke(0.5f)) }
                    drawPath(Path().apply { moveTo(pie.x + 12f, arriba + 3f); cubicTo(pie.x + 20f, arriba + 4f, pie.x + 31f, arriba + 2f, pie.x + 33f, arriba + 12f); lineTo(pie.x + 27f, arriba + 14f); cubicTo(pie.x + 26f, arriba + 8f, pie.x + 18f, arriba + 8f, pie.x + 12f, arriba + 7f); close() }, z.claro(pie, C.CAL))   // una sábana que cuelga del borde
                }
            }
            drawOval(z.claro(pie, C.MADERA), boca.topLeft, boca.size, style = Stroke(1.2f))
        }
    }

    /** El hornillo de ladrillo, con la lumbre en su boca, y encima el caldero de cobre donde hierve el agua de la colada. */
    private fun hornillo(m: Momento, z: Luz): Cosa {
        val pie = Offset(186f, 272f)
        val forma = Path().apply { addRect(Rect(pie.x - 15f, pie.y - 20f, pie.x + 15f, pie.y)); addOval(Rect(pie.x - 14f, pie.y - 37f, pie.x + 14f, pie.y - 14f)) }
        return Cosa(forma, pie) {
            val cuerpo = Rect(pie.x - 15f, pie.y - 20f, pie.x + 15f, pie.y)
            drawRect(z.c(pie, C.LADRILLO), cuerpo.topLeft, cuerpo.size)
            drawRect(z.claro(pie, C.LADRILLO), Offset(if (z.lado(pie) > 0f) cuerpo.right - 7f else cuerpo.left, cuerpo.top), Size(7f, cuerpo.height)); drawRect(z.oscuro(pie, C.LADRILLO), Offset(if (z.lado(pie) > 0f) cuerpo.left else cuerpo.right - 5f, cuerpo.top), Size(5f, cuerpo.height))
            for (i in 0..4) { drawLine(z.c(pie, C.PIEDRA).copy(alpha = 0.5f), Offset(cuerpo.left, cuerpo.top + i * 4f), Offset(cuerpo.right, cuerpo.top + i * 4f), 0.4f); var x = cuerpo.left + (if (i % 2 == 0) 3f else 6f); while (x < cuerpo.right) { drawLine(z.c(pie, C.PIEDRA).copy(alpha = 0.5f), Offset(x, cuerpo.top + i * 4f), Offset(x, cuerpo.top + i * 4f + 4f), 0.4f); x += 6f } }
            // La boca, tiznada; dentro, las brasas o la ceniza.
            val boca = Path().apply { moveTo(BOCA.x - 7f, pie.y); lineTo(BOCA.x - 7f, BOCA.y - 3f); cubicTo(BOCA.x - 7f, BOCA.y - 9f, BOCA.x + 7f, BOCA.y - 9f, BOCA.x + 7f, BOCA.y - 3f); lineTo(BOCA.x + 7f, pie.y); close() }
            mancha(BOCA + Offset(0f, -9f), 9f, 5f, z.c(pie, C.BREA), 0.5f)
            drawPath(boca, z.c(pie, C.BREA))
            if (E.lumbre(m)) { clipPath(boca) { drawRect(Brush.verticalGradient(0f to Color(0xFFFFD27A), 1f to Color(0xFFE2582A), startY = BOCA.y - 7f, endY = pie.y), Offset(BOCA.x - 7f, BOCA.y - 8f), Size(14f, 18f)) }; resplandor(BOCA, 20f, 0.35f + 0.5f * z.l.noche, Color(0xFFFF9A4A)) }
            else clipPath(boca) { drawOval(z.c(pie, C.PIEDRA_OSC), Offset(BOCA.x - 6f, pie.y - 3f), Size(12f, 5f)) }
            // El caldero, panzudo, con su borde, sus asas y el tizne de abajo.
            val panza = Path().apply { moveTo(pie.x - 13f, pie.y - 33f); cubicTo(pie.x - 16f, pie.y - 24f, pie.x - 12f, pie.y - 18f, pie.x - 8f, pie.y - 18f); lineTo(pie.x + 8f, pie.y - 18f); cubicTo(pie.x + 12f, pie.y - 18f, pie.x + 16f, pie.y - 24f, pie.x + 13f, pie.y - 33f); close() }
            drawPath(panza, z.redondo(pie, C.COBRE, pie.x - 15f, pie.x + 15f))
            clipPath(panza) { drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.c(pie, C.BREA).copy(alpha = 0.8f), startY = pie.y - 28f, endY = pie.y - 17f), Offset(pie.x - 16f, pie.y - 28f), Size(32f, 12f)) }
            drawOval(z.oscuro(pie, C.COBRE), Offset(pie.x - 13f, pie.y - 36f), Size(26f, 6.4f)); drawOval(if (E.lumbre(m)) z.c(pie, lerp(C.SENA, C.CAL, 0.55f)) else z.c(pie, C.BREA), Offset(pie.x - 11f, pie.y - 35f), Size(22f, 4.6f)); drawOval(z.claro(pie, C.COBRE), Offset(pie.x - 13f, pie.y - 36f), Size(26f, 6.4f), style = Stroke(0.9f))
            for (s in listOf(-1f, 1f)) drawArc(z.c(pie, C.HIERRO), -90f + (if (s > 0f) 0f else 90f) - 0f, 90f, false, Offset(pie.x + s * 15f - 3f, pie.y - 32f), Size(6f, 7f), style = Stroke(1f))
            // El cazo de mango largo, apoyado en el borde.
            drawLine(z.c(pie, C.MADERA), Offset(pie.x + 9f, pie.y - 34f), Offset(pie.x + 26f, pie.y - 62f), 1.5f, StrokeCap.Round); drawOval(z.c(pie, C.HIERRO), Offset(pie.x + 4f, pie.y - 37f), Size(8f, 4.6f))
        }
    }

    /** Un cajón de lavar: la caja con su paja, donde se arrodilla la lavandera, y la tabla, inclinada hacia el agua. */
    private fun cajon(m: Momento, z: Luz, x: Float, cual: Int): Cosa {
        val pie = Offset(x + 15f, 241f)
        val forma = Path().apply { addRect(Rect(x, 225f, x + 30f, pie.y)) }
        return Cosa(forma, pie) {
            // La tabla: sale de la caja por encima de la borda y baja al agua; gastada, con el jabón en la punta.
            val tabla = poligono(Offset(x + 6f, 227f), Offset(x + 24f, 227f), Offset(x + 20.6f, 202f), Offset(x + 10f, 202f))
            if (!E.helada(m)) drawPath(Path().apply { addPath(tabla, Offset(if (z.lado(pie) > 0f) -3f else 3f, 1.2f)) }, z.l.tono(mult(C.SENA, C.SOMBRA, 0.5f)).copy(alpha = 0.35f))
            drawPath(tabla, Brush.verticalGradient(0f to z.c(pie, mult(C.MADERA, C.SOMBRA, 0.45f)), 0.5f to z.c(pie, lerp(C.MADERA, C.OCRE, 0.2f)), 1f to z.c(pie, C.MADERA), startY = 202f, endY = 227f))
            clipPath(tabla) { for (i in 0..9) { drawLine(z.oscuro(pie, C.MADERA_OSC).copy(alpha = 0.55f), Offset(x + 6f, 205f + i * 2.3f), Offset(x + 24f, 205f + i * 2.3f), 0.6f); drawLine(z.claro(pie, C.OCRE).copy(alpha = 0.4f), Offset(x + 6f, 204.3f + i * 2.3f), Offset(x + 24f, 204.3f + i * 2.3f), 0.35f) }; drawRect(z.claro(pie, C.MADERA).copy(alpha = 0.5f), Offset(if (z.lado(pie) > 0f) x + 20f else x + 6f, 202f), Size(2.6f, 25f)) }
            if (E.lavando(m)) { mancha(Offset(x + 15f, 204f), 8f, 2.6f, z.c(pie, C.CAL), 0.8f); val pts = ArrayList<Offset>(); val az = m.azar("espuma", cual); repeat(16) { pts += Offset(x + az.entre(7f, 24f), az.entre(200f, 210f)) }; drawPoints(pts, PointMode.Points, z.c(pie, Color.White).copy(alpha = 0.8f), 1.3f, StrokeCap.Round) }
            if (E.helada(m)) drawPath(tabla, z.c(pie, C.CAL).copy(alpha = 0.8f))
            // La caja: tres tablas, con el canto de arriba claro y la paja asomando.
            val caja = Rect(x, 227f, x + 30f, pie.y)
            drawRect(z.c(pie, C.MADERA), caja.topLeft, caja.size); drawRect(z.oscuro(pie, C.MADERA), Offset(if (z.lado(pie) > 0f) caja.left else caja.right - 6f, caja.top), Size(6f, caja.height))
            for (i in 1..2) drawLine(z.c(pie, C.MADERA_OSC), Offset(caja.left, caja.top + i * 4.6f), Offset(caja.right, caja.top + i * 4.6f), 0.5f)
            for (xx in listOf(caja.left + 2f, caja.right - 2f)) for (yy in listOf(caja.top + 2.4f, caja.top + 7f, caja.top + 11.6f)) drawCircle(z.c(pie, C.BREA), 0.6f, Offset(xx, yy))
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to z.c(pie, mult(C.MADERA_OSC, C.SOMBRA, 0.6f)).copy(alpha = 0.6f), startY = caja.bottom - 5f, endY = caja.bottom), Offset(caja.left, caja.bottom - 5f), Size(caja.width, 5f))
            val paja = Path().apply { moveTo(caja.left + 1f, caja.top + 0.6f); cubicTo(caja.left + 6f, caja.top - 4.6f, caja.right - 6f, caja.top - 4.6f, caja.right - 1f, caja.top + 0.6f); close() }
            drawPath(paja, z.c(pie, if (E.nevado(m)) C.CAL else C.PAJA))
            if (!E.nevado(m)) { val az = Azar(Semilla.de("revolucion", "paja", cual)); repeat(14) { val p = Offset(az.entre(caja.left + 2f, caja.right - 2f), caja.top + az.entre(-3f, 0.4f)); drawLine(z.c(pie, lerp(C.OCRE, C.MADERA, az.real())).copy(alpha = 0.8f), p, p + Offset(az.entre(-4f, 4f), az.entre(-2.6f, 1f)), 0.4f) } }
            drawRect(z.claro(pie, C.MADERA), Offset(caja.left, caja.top), Size(caja.width, 1f))
            // Lo que hay en cada puesto: la paleta, el jabón con su cepillo, un trapo retorcido; el día de la marcha, el delantal dejado.
            if (E.lugar(m) == EscenaRevolucion.Lugar.MARCHA) drawPath(Path().apply { moveTo(caja.left + 5f, caja.top - 2f); cubicTo(caja.left + 10f, caja.top - 5f, caja.right - 8f, caja.top - 4f, caja.right - 4f, caja.top); lineTo(caja.right - 6f, caja.bottom - 1f); cubicTo(caja.right - 12f, caja.bottom - 3f, caja.left + 12f, caja.bottom - 2f, caja.left + 7f, caja.bottom - 4f); close() }, z.redondo(pie, C.ANIL, caja.left + 5f, caja.right - 4f))
            if (E.helada(m)) return@Cosa
            when (cual) {
                0 -> { val o = Offset(caja.left + 3f, caja.top - 3.6f); drawPath(poligono(o, o + Offset(13f, -1.6f), o + Offset(13.6f, 3.4f), o + Offset(0.6f, 5f)), z.c(pie, lerp(C.MADERA, C.OCRE, 0.35f))); drawLine(z.c(pie, lerp(C.MADERA, C.OCRE, 0.35f)), o + Offset(13f, 1f), o + Offset(22f, -0.4f), 2.2f, StrokeCap.Round); drawLine(z.claro(pie, C.OCRE), o + Offset(0.4f, 0.3f), o + Offset(13f, -1.3f), 0.6f) }   // la paleta
                1 -> { val o = Offset(caja.right - 10f, caja.top - 3f); drawOval(z.c(pie, lerp(C.MADERA_OSC, C.OCRE, 0.25f)), o, Size(8f, 5f)); drawOval(z.claro(pie, C.OCRE).copy(alpha = 0.6f), o + Offset(1.4f, 0.6f), Size(3f, 1.6f)); drawRect(z.c(pie, C.MADERA), Offset(caja.left + 4f, caja.top - 2.6f), Size(9f, 2.4f)); for (i in 0..5) drawLine(z.c(pie, C.PAJA), Offset(caja.left + 4.6f + i * 1.5f, caja.top - 0.2f), Offset(caja.left + 4.6f + i * 1.5f, caja.top + 1.8f), 0.5f) }
                else -> drawPath(Path().apply { moveTo(caja.left + 6f, caja.top - 3f); cubicTo(caja.left + 12f, caja.top - 6f, caja.left + 20f, caja.top - 1f, caja.right - 3f, caja.top - 4f) }, z.c(pie, C.CAL), style = Stroke(3.4f, cap = StrokeCap.Round))
            }
        }
    }

    /** El banco de la lavandería: el asiento de una tabla gruesa, gastado, y cuatro patas abiertas. */
    private fun banco(m: Momento, z: Luz): Cosa {
        val pie = Offset(SOBRE.x, 267f)
        val forma = Path().apply { addRect(Rect(234f, SOBRE.y, 302f, SOBRE.y + 4.4f)); addRect(Rect(240f, SOBRE.y, 245f, pie.y)); addRect(Rect(291f, SOBRE.y, 296f, pie.y)) }
        return Cosa(forma, pie) {
            for ((x, dx) in listOf(248f to -4f, 288f to 4f)) drawLine(z.oscuro(pie, C.MADERA_OSC), Offset(x, SOBRE.y + 3f), Offset(x + dx, pie.y - 6f), 3f, StrokeCap.Round)                // las patas de detrás
            for ((x, dx) in listOf(242f to -3f, 294f to 3f)) { drawLine(z.c(pie, C.MADERA_OSC), Offset(x, SOBRE.y + 3f), Offset(x + dx, pie.y), 3.6f, StrokeCap.Round); drawLine(z.claro(pie, C.MADERA).copy(alpha = 0.7f), Offset(x + z.lado(pie) * 1f, SOBRE.y + 3f), Offset(x + dx + z.lado(pie) * 1f, pie.y), 0.8f) }
            val tabla = Rect(234f, SOBRE.y, 302f, SOBRE.y + 4.6f)
            drawRect(z.c(pie, C.MADERA), tabla.topLeft, tabla.size); drawRect(z.oscuro(pie, C.MADERA_OSC), Offset(tabla.left, tabla.top + 3f), Size(tabla.width, 1.6f))
            drawPath(poligono(Offset(tabla.left, tabla.top), Offset(tabla.right, tabla.top), Offset(tabla.right - 3f, tabla.top - 3.4f), Offset(tabla.left + 3f, tabla.top - 3.4f)), z.claro(pie, lerp(C.MADERA, C.OCRE, 0.25f)))
            for (i in 0..5) drawLine(z.c(pie, C.MADERA_OSC).copy(alpha = 0.35f), Offset(tabla.left + 6f + i * 11f, tabla.top - 2.8f), Offset(tabla.left + 14f + i * 11f, tabla.top - 1f), 0.4f)
            if (E.nevado(m)) drawPath(poligono(Offset(tabla.left + 1f, tabla.top - 1f), Offset(tabla.right - 1f, tabla.top - 1f), Offset(tabla.right - 3f, tabla.top - 4.4f), Offset(tabla.left + 3f, tabla.top - 4.4f)), z.c(pie, C.CAL))
            // A mediodía, el pan, el queso y la jarra del vino aguado.
            if (E.comiendo(m)) { val o = Offset(292f, tabla.top - 3f); drawPath(Path().apply { moveTo(o.x - 4f, o.y); lineTo(o.x - 4.6f, o.y - 8f); cubicTo(o.x - 3f, o.y - 10f, o.x + 3f, o.y - 10f, o.x + 4.6f, o.y - 8f); lineTo(o.x + 4f, o.y); close() }, z.redondo(o, lerp(C.TEJA, C.OCRE, 0.3f), o.x - 4.6f, o.x + 4.6f)); drawArc(z.c(o, lerp(C.TEJA, C.OCRE, 0.3f)), -90f, 180f, false, Offset(o.x + 2.6f, o.y - 7.6f), Size(5f, 5.4f), style = Stroke(1.1f)); drawOval(z.c(o, lerp(C.OCRE, C.MADERA, 0.3f)), Offset(o.x - 16f, o.y - 3.4f), Size(9f, 3.8f)); drawOval(z.claro(o, C.OCRE), Offset(o.x - 15f, o.y - 3.2f), Size(5f, 1.6f)) }
        }
    }

    /** Un cesto de mimbre con su ropa: las costillas, el trenzado, el asa y el montón blanco encima. */
    private fun cesto(m: Momento, z: Luz, pie: Offset, rx: Float, alto: Float, lleno: Boolean, semilla: Int): Cosa {
        val forma = Path().apply { moveTo(pie.x - rx * 0.8f, pie.y); lineTo(pie.x - rx, pie.y - alto); lineTo(pie.x + rx, pie.y - alto); lineTo(pie.x + rx * 0.8f, pie.y); cubicTo(pie.x + rx * 0.6f, pie.y + 3f, pie.x - rx * 0.6f, pie.y + 3f, pie.x - rx * 0.8f, pie.y); close() }
        return Cosa(forma, pie) {
            drawPath(forma, z.redondo(pie, C.MIMBRE, pie.x - rx, pie.x + rx))
            clipPath(forma) {
                var y = pie.y - alto + 2f; var i = 0
                while (y < pie.y + 3f) { drawPath(Path().apply { moveTo(pie.x - rx, y); cubicTo(pie.x - rx * 0.5f, y + 2.2f, pie.x + rx * 0.5f, y + 2.2f, pie.x + rx, y) }, z.oscuro(pie, C.MADERA).copy(alpha = 0.5f), style = Stroke(0.5f)); y += 2.6f; i++ }
                for (k in -3..3) drawLine(z.oscuro(pie, C.MADERA_OSC).copy(alpha = 0.45f), Offset(pie.x + k * rx * 0.3f, pie.y - alto), Offset(pie.x + k * rx * 0.25f, pie.y + 3f), 0.6f)
            }
            drawOval(z.oscuro(pie, C.MADERA_OSC), Offset(pie.x - rx, pie.y - alto - rx * 0.2f), Size(rx * 2f, rx * 0.4f))
            if (lleno) {
                val az = Azar(Semilla.de("revolucion", "cesto", semilla)); val monte = Path().apply { moveTo(pie.x - rx * 0.92f, pie.y - alto); cubicTo(pie.x - rx * 0.8f, pie.y - alto - rx * 0.7f, pie.x - rx * 0.1f, pie.y - alto - rx * 0.9f, pie.x + rx * 0.2f, pie.y - alto - rx * 0.6f); cubicTo(pie.x + rx * 0.6f, pie.y - alto - rx * 0.8f, pie.x + rx * 0.95f, pie.y - alto - rx * 0.4f, pie.x + rx * 0.92f, pie.y - alto); close() }
                drawPath(monte, z.redondo(pie, if (E.nevado(m)) C.CAL else lerp(C.CAL, C.PIEDRA, 0.15f), pie.x - rx, pie.x + rx))
                clipPath(monte) { repeat(5) { val x0 = pie.x + az.entre(-rx, rx) * 0.8f; drawPath(Path().apply { moveTo(x0, pie.y - alto - rx); cubicTo(x0 + 3f, pie.y - alto - rx * 0.6f, x0 - 3f, pie.y - alto - rx * 0.3f, x0 + 1f, pie.y - alto) }, z.oscuro(pie, lerp(C.CAL, C.PIZARRA, 0.5f)).copy(alpha = 0.5f), style = Stroke(0.7f)) } }
            }
            drawOval(z.claro(pie, C.MIMBRE), Offset(pie.x - rx, pie.y - alto - rx * 0.2f), Size(rx * 2f, rx * 0.4f), style = Stroke(1f))
            for (s in listOf(-1f, 1f)) drawArc(z.c(pie, C.MADERA_OSC), 180f, 180f, false, Offset(pie.x + s * rx * 0.92f - 2.4f, pie.y - alto - 3f), Size(4.8f, 6f), style = Stroke(1f))
        }
    }

    /** El cesto grande volcado, con la ropa blanca derramada por las tablas: así quedó el día de la marcha. */
    private fun cestoVolcado(z: Luz, pie: Offset): Cosa {
        val cuerpo = Path().apply { moveTo(pie.x - 2f, pie.y - 25f); lineTo(pie.x + 17f, pie.y - 21f); cubicTo(pie.x + 20.6f, pie.y - 14f, pie.x + 20.6f, pie.y - 7f, pie.x + 17f, pie.y); lineTo(pie.x - 2f, pie.y + 2f); close() }
        return Cosa(cuerpo, pie) {
            // La ropa que se ha salido: una sábana derramada hacia la izquierda.
            val ropa = Path().apply { moveTo(pie.x - 3f, pie.y - 11f); cubicTo(pie.x - 14f, pie.y - 13f, pie.x - 25f, pie.y - 6f, pie.x - 36f, pie.y - 1f); cubicTo(pie.x - 31f, pie.y + 4f, pie.x - 16f, pie.y + 6f, pie.x - 1f, pie.y + 2f); close() }
            drawPath(ropa, z.redondo(pie, C.CAL, pie.x - 36f, pie.x))
            clipPath(ropa) { for (i in 0..3) drawPath(Path().apply { moveTo(pie.x - 4f - i * 7f, pie.y - 12f + i * 2f); cubicTo(pie.x - 8f - i * 7f, pie.y - 6f, pie.x - 5f - i * 7f, pie.y - 1f, pie.x - 10f - i * 7f, pie.y + 5f) }, z.oscuro(pie, lerp(C.CAL, C.PIZARRA, 0.5f)).copy(alpha = 0.5f), style = Stroke(0.7f)) }
            // El cesto, tumbado: el cuerpo de mimbre y la boca, que mira a la izquierda.
            drawPath(cuerpo, Brush.verticalGradient(0f to z.claro(pie, C.MIMBRE), 0.45f to z.c(pie, C.MIMBRE), 1f to z.oscuro(pie, C.MIMBRE), startY = pie.y - 25f, endY = pie.y + 2f))
            clipPath(cuerpo) {
                for (i in 0..6) drawLine(z.oscuro(pie, C.MADERA_OSC).copy(alpha = 0.45f), Offset(pie.x + 1f + i * 3.2f, pie.y - 26f), Offset(pie.x + 1f + i * 3.2f, pie.y + 3f), 0.5f)
                for (k in 0..6) drawLine(z.oscuro(pie, C.MADERA).copy(alpha = 0.5f), Offset(pie.x - 2f, pie.y - 22f + k * 4f), Offset(pie.x + 21f, pie.y - 19f + k * 3.4f), 0.5f)
            }
            drawOval(z.oscuro(pie, lerp(C.MADERA_OSC, C.BREA, 0.5f)), Offset(pie.x - 8f, pie.y - 25.4f), Size(12f, 27.6f))
            drawOval(z.claro(pie, C.MIMBRE), Offset(pie.x - 8f, pie.y - 25.4f), Size(12f, 27.6f), style = Stroke(1.2f))
            clipPath(Path().apply { addOval(Rect(pie.x - 8f, pie.y - 25.4f, pie.x + 4f, pie.y + 2.2f)) }) { drawPath(ropa, z.c(pie, lerp(C.CAL, C.PIZARRA, 0.2f))) }
        }
    }

    /** El tonel de la ceniza: panzudo, con cuatro aros, la tapa y el cucharón encima. Es el mirador del gato. */
    private fun tonel(m: Momento, z: Luz): Cosa {
        val pie = Offset(346f, 253f); val arriba = 223f
        val forma = Path().apply { moveTo(pie.x - 11f, pie.y); cubicTo(pie.x - 15f, pie.y - 10f, pie.x - 15f, arriba + 10f, pie.x - 11f, arriba); lineTo(pie.x + 11f, arriba); cubicTo(pie.x + 15f, arriba + 10f, pie.x + 15f, pie.y - 10f, pie.x + 11f, pie.y); cubicTo(pie.x + 6f, pie.y + 3f, pie.x - 6f, pie.y + 3f, pie.x - 11f, pie.y); close() }
        return Cosa(forma, pie) {
            drawPath(forma, z.redondo(pie, lerp(C.MADERA, C.PIEDRA_OSC, 0.25f), pie.x - 14f, pie.x + 14f))
            clipPath(forma) {
                for (i in -3..3) drawPath(Path().apply { moveTo(pie.x + i * 3.6f, arriba); cubicTo(pie.x + i * 4.8f, arriba + 10f, pie.x + i * 4.8f, pie.y - 10f, pie.x + i * 3.6f, pie.y + 3f) }, z.c(pie, C.MADERA_OSC).copy(alpha = 0.5f), style = Stroke(0.4f))
                for (y in listOf(arriba + 3f, arriba + 9f, pie.y - 9f, pie.y - 3f)) drawPath(Path().apply { moveTo(pie.x - 16f, y); cubicTo(pie.x - 8f, y + 2.6f, pie.x + 8f, y + 2.6f, pie.x + 16f, y) }, z.c(pie, C.HIERRO), style = Stroke(1.5f))
                mancha(Offset(pie.x + 2f, arriba + 14f), 5f, 9f, z.c(pie, lerp(C.PIEDRA_OSC, C.CAL, 0.4f)), 0.4f)                                    // la ceniza que ha chorreado
            }
            drawOval(z.c(pie, lerp(C.MADERA, C.OCRE, 0.2f)), Offset(pie.x - 11f, arriba - 2.4f), Size(22f, 4.8f)); drawOval(z.claro(pie, C.MADERA), Offset(pie.x - 11f, arriba - 2.4f), Size(22f, 4.8f), style = Stroke(0.7f))
            if (E.nevado(m)) drawOval(z.c(pie, C.CAL), Offset(pie.x - 10f, arriba - 3.4f), Size(20f, 4.4f))
        }
    }

    /** El palo de popa: va del suelo a lo alto del cuadro; lleva el farol en su palomilla, una cornamusa, un rollo de cabo y el saquito de las pinzas. */
    private fun palo(m: Momento, z: Luz): Cosa {
        val pie = Offset(PALO, 264f)
        val forma = poligono(Offset(pie.x - 3.6f, pie.y), Offset(pie.x + 3.6f, pie.y), Offset(CABO_B.x + 2.6f, 0f), Offset(CABO_B.x - 2.2f, 0f))
        return Cosa(forma, pie, bajoElFollaje = false) {
            val l = z.l
            drawPath(forma, z.redondo(Offset(pie.x, 150f), C.MADERA, pie.x - 3.6f, pie.x + 3.6f))
            clipPath(forma) { val az = Azar(Semilla.de("revolucion", "palo")); repeat(16) { val y = az.entre(10f, 260f); drawLine(z.c(Offset(pie.x, y), C.MADERA_OSC).copy(alpha = 0.4f), Offset(pie.x + az.entre(-3f, 3f), y), Offset(pie.x + az.entre(-3f, 3f), y + az.entre(8f, 26f)), 0.4f) }
                for (y in listOf(60f, 140f, 232f)) drawRect(z.c(Offset(pie.x, y), C.HIERRO), Offset(pie.x - 5f, y), Size(10f, 2.2f)) }
            drawRect(z.c(pie, C.BREA), Offset(pie.x - 5f, pie.y - 2.4f), Size(10f, 3.6f))
            // La cornamusa y el rollo de cabo que cuelga de ella.
            drawRoundRect(z.c(Offset(pie.x, 196f), C.MADERA_OSC), Offset(pie.x - 6.4f, 194f), Size(12.8f, 2.4f), CornerRadius(1.2f))
            val cabo = z.c(Offset(pie.x, 206f), lerp(C.PAJA, C.MADERA, 0.35f))
            for (i in 0..3) drawOval(cabo, Offset(pie.x - 5.4f + i * 0.4f, 196f + i * 0.5f), Size(8.4f, 17f - i), style = Stroke(1f))
            // El saquito de las pinzas.
            drawPath(Path().apply { moveTo(pie.x + 2.6f, 172f); cubicTo(pie.x + 1f, 180f, pie.x + 9f, 182f, pie.x + 9f, 172f); close() }, z.redondo(Offset(pie.x + 6f, 176f), lerp(C.ANIL, C.PIZARRA, 0.4f), pie.x + 1f, pie.x + 9f)); drawLine(z.c(Offset(pie.x, 170f), C.MADERA_OSC), Offset(pie.x + 1f, 166f), Offset(pie.x + 5.6f, 172f), 0.5f)
            // El farol de popa, colgado de su palomilla de hierro.
            val f = E.FAROL
            drawLine(z.c(f, C.HIERRO), Offset(pie.x - 2f, 138f), Offset(f.x, 138f), 1.2f); drawLine(z.c(f, C.HIERRO), Offset(pie.x - 2f, 148f), Offset(f.x + 4f, 138.4f), 0.8f); drawLine(z.c(f, C.HIERRO), Offset(f.x, 138f), Offset(f.x, 142.4f), 0.7f)
            val caja = Path().apply { moveTo(f.x - 4.4f, 144.6f); lineTo(f.x + 4.4f, 144.6f); lineTo(f.x + 3.4f, 156.4f); lineTo(f.x - 3.4f, 156.4f); close() }
            val encendido = E.farolDePopa(m)
            if (encendido) { resplandor(f, 34f, 0.5f + 0.4f * l.noche, C.ACEITE); if (E.niebla(m)) haloDeBruma(f, 44f, 30f, C.ACEITE, 0.7f) }
            drawPath(caja, if (encendido) lerp(C.ACEITE, Color.White, 0.35f) else z.c(f, lerp(C.PIZARRA, C.CAL, 0.45f)))
            if (encendido) drawOval(Color(0xFFFFF6D8), Offset(f.x - 1.3f, 148f), Size(2.6f, 5.4f))
            drawPath(caja, z.c(f, C.HIERRO), style = Stroke(0.8f)); drawLine(z.c(f, C.HIERRO), Offset(f.x, 144.6f), Offset(f.x, 156.4f), 0.4f)
            drawPath(poligono(Offset(f.x - 5.4f, 144.8f), Offset(f.x + 5.4f, 144.8f), Offset(f.x, 141.4f)), z.c(f, C.HIERRO)); drawRect(z.c(f, C.HIERRO), Offset(f.x - 4f, 156.2f), Size(8f, 1.4f))
            if (E.nevado(m)) drawLine(z.c(f, C.CAL), Offset(f.x - 4.4f, 143.6f), Offset(f.x + 4.4f, 143.6f), 1.2f)
            // La cinta de tres colores atada al palo, desde la Bastilla: un lazo y dos puntas que cuelgan.
            if (E.banderas(m) >= 1) {
                val y0 = 74f; val v = m.viento * 5f; val o = Offset(pie.x, y0 + 8f)
                for ((i, col) in listOf(C.ANIL, C.GRANZA).withIndex()) drawPath(Path().apply { moveTo(pie.x - 4f + i * 1.6f, y0 + 6.6f); cubicTo(pie.x - 8f - v + i * 2f, y0 + 12f, pie.x - 6f - v, y0 + 19f, pie.x - 11f - v + i * 4f, y0 + 27f - i * 5f); lineTo(pie.x - 7f - v + i * 4f, y0 + 28f - i * 5f); cubicTo(pie.x - 2.6f - v, y0 + 19f, pie.x - 4f + i * 2f, y0 + 12f, pie.x - 0.6f + i * 1.6f, y0 + 6.6f); close() }, z.c(o, col))
                for ((i, col) in listOf(C.ANIL, C.CAL, C.GRANZA).withIndex()) drawRect(z.c(o, col), Offset(pie.x - 5.2f, y0 + i * 2.3f), Size(10f, 2.3f))
                drawRect(z.oscuro(o, C.PIZARRA).copy(alpha = 0.3f), Offset(pie.x + (if (z.lado(o) > 0f) -5.2f else 1.6f), y0), Size(3.6f, 6.9f))
            }
            // El crespón, los días de luto del pueblo (Mirabeau, Marat).
            if (E.hoy(m, "mirabeau") || E.hoy(m, "marat")) { val o = Offset(pie.x - 1f, 112f); drawRect(z.c(o, C.BREA), Offset(pie.x - 5f, 108f), Size(10f, 5f)); drawPath(Path().apply { moveTo(o.x - 3f, 113f); cubicTo(o.x - 8f, 122f, o.x - 4f, 130f, o.x - 9f, 138f); lineTo(o.x - 5f, 138f); cubicTo(o.x - 1f, 130f, o.x - 4f, 122f, o.x + 1f, 113f); close() }, z.c(o, C.BREA)) }
        }
    }

    /** El cesto de Marie-Jeanne con su hogaza, junto a la puerta; y, a su lado, el de Paul. */
    private fun DrawScope.elPan(m: Momento, z: Luz) {
        if (E.helada(m)) return
        val pie = Offset(66f, 251f)
        with(cesto(m, z, pie, 12.4f, 10f, false, 3)) { pintar() }
        // La hogaza: entera, media o un cuarto, según lo que cueste el pan.
        val pan = z.redondo(pie, lerp(C.OCRE, C.TEJA, 0.35f), pie.x - 11f, pie.x + 11f); val miga = z.c(pie, lerp(C.CAL, C.OCRE, 0.3f))
        if (E.lugar(m) != EscenaRevolucion.Lugar.MARCHA && m.hora >= 5.5 && m.hora < 19.5) when (E.hogaza(m)) {
            3 -> { drawOval(pan, Offset(pie.x - 11f, pie.y - 21f), Size(22f, 13f)); for (i in -1..1) drawLine(miga.copy(alpha = 0.75f), Offset(pie.x + i * 5.4f - 1.8f, pie.y - 18.6f), Offset(pie.x + i * 5.4f + 1.8f, pie.y - 13.6f), 0.8f) }
            2 -> { clipRect(pie.x - 12f, pie.y - 22f, pie.x + 1f, pie.y - 7f) { drawOval(pan, Offset(pie.x - 11f, pie.y - 21f), Size(22f, 13f)) }; drawRect(miga, Offset(pie.x - 0.4f, pie.y - 20.4f), Size(1.6f, 11.4f)) }
            else -> { drawPath(Path().apply { moveTo(pie.x - 9f, pie.y - 10f); cubicTo(pie.x - 9f, pie.y - 16f, pie.x - 4f, pie.y - 20f, pie.x, pie.y - 20f); lineTo(pie.x, pie.y - 10f); close() }, pan); drawRect(miga, Offset(pie.x - 0.2f, pie.y - 19.6f), Size(1.4f, 9.6f)) }
        }
        if (E.paulAyuda(m)) with(cesto(m, z, Offset(90f, 256f), 7.4f, 7f, E.lavando(m), 4)) { pintar() }
    }

    // ---------------------------------------------------------------- La ropa tendida

    /** Una prenda de la cuerda. [tipo]: 0, sábana; 1, camisa; 2, enagua; 3, medias; 4, pañuelo; 5, delantal; 6, mantel de rayas. */
    private class Prenda(val t: Float, val tipo: Int, val color: Color, val turno: Float)

    /**
     * Cada puesto de la cuerda admite prendas largas o solo chicas (delante de donde salen el sol y la luna). Delante de
     * las torres de Notre-Dame no hay puestos: la cuerda deja ahí un hueco, para que se vean enteras.
     */
    private val PUESTOS = listOf(0.06f to true, 0.125f to true, 0.335f to true, 0.415f to true, 0.5f to true, 0.585f to true, 0.665f to true, 0.735f to false, 0.79f to false, 0.845f to false, 0.9f to false)

    /** Las prendas de hoy, con su turno (cuáles se cuelgan primero): la misma cuerda todo el día, cada vez más llena. */
    private fun prendas(m: Momento): List<Prenda> {
        val az = m.azar("ropa"); val out = ArrayList<Prenda>(); var salta = false
        for ((i, puesto) in PUESTOS.withIndex()) {
            if (salta) { salta = false; continue }
            val (t, larga) = puesto
            val tipo = if (!larga) listOf(3, 4, 4, 3, 5)[az.entero(5)] else if (PUESTOS.getOrNull(i + 1)?.let { it.second && it.first - t < 0.1f } == true && az.entero(3) == 0) 0 else listOf(1, 2, 1, 6, 5, 2)[az.entero(6)]
            if (tipo == 0) salta = true
            val color = when (tipo) { 5 -> C.ANIL; 3 -> listOf(C.ANIL, C.CAL, C.GRANZA, lerp(C.CAL, C.OCRE, 0.4f))[az.entero(4)]; 6 -> C.CAL; else -> if (az.entero(6) == 0) lerp(C.CAL, C.OCRE, 0.35f) else C.CAL }
            out += Prenda(if (tipo == 0) t + 0.035f else t, tipo, color, az.real())
        }
        return out
    }

    /** La cuerda de tender, de la casa al palo, y la ropa que cuelga hoy: cada prenda con sus pliegues, su lado de luz y sus pinzas. */
    private fun DrawScope.tendido(m: Momento, z: Luz) {
        val l = z.l
        val pts = cuerda(CABO_A, CABO_B, 9f, 30)
        fun enLaCuerda(t: Float): Offset { val f = t.coerceIn(0f, 1f) * (pts.size - 1); val i = f.toInt().coerceAtMost(pts.size - 2); return pts[i] + (pts[i + 1] - pts[i]) * (f - i) }
        drawPath(curva(pts), z.c(Offset(200f, 30f), lerp(C.PAJA, C.MADERA, 0.4f)), style = Stroke(0.8f, cap = StrokeCap.Round))
        if (E.nevado(m)) drawPath(curva(pts.map { it + Offset(0f, -0.6f) }), z.c(Offset(200f, 30f), C.CAL), style = Stroke(0.7f))
        val cuanta = E.ropaTendida(m); val inclina = m.viento * 7f
        val lado = if (z.r.lat > 0f) 1f else -1f
        for (p in prendas(m)) {
            if (p.turno >= cuanta) continue
            val o = enLaCuerda(p.t)
            val (w, h) = when (p.tipo) { 0 -> 34f to 31f; 1 -> 16f to 23f; 2 -> 18f to 24f; 3 -> 8f to 15f; 4 -> 9.6f to 9f; 5 -> 13f to 19f; else -> 22f to 17f }
            val a = enLaCuerda(p.t - w / 2f / 303f); val b = enLaCuerda(p.t + w / 2f / 303f)
            val claro = z.claro(o, p.color); val base = z.c(o, p.color); val osc = z.oscuro(o, lerp(p.color, C.PIZARRA, 0.25f))
            val relleno = Brush.horizontalGradient(if (lado > 0f) listOf(osc, base, claro) else listOf(claro, base, osc), a.x, b.x)
            val forma = when (p.tipo) {
                1 -> Path().apply { // la camisa, colgada de los faldones: el cuerpo y las dos mangas abiertas
                    moveTo(a.x + 2f, a.y); lineTo(b.x - 2f, b.y); lineTo(b.x - 1f + inclina * 0.5f, o.y + h * 0.55f); lineTo(b.x + 5f + inclina, o.y + h * 0.95f); lineTo(b.x + 1f + inclina, o.y + h)
                    lineTo(o.x + 2f + inclina * 0.7f, o.y + h * 0.72f); lineTo(o.x + 1.6f + inclina, o.y + h * 0.98f); lineTo(o.x - 1.6f + inclina, o.y + h * 0.98f); lineTo(o.x - 2f + inclina * 0.7f, o.y + h * 0.72f)
                    lineTo(a.x - 1f + inclina, o.y + h); lineTo(a.x - 5f + inclina, o.y + h * 0.95f); lineTo(a.x + 1f + inclina * 0.5f, o.y + h * 0.55f); close() }
                2 -> Path().apply { moveTo(a.x + 3f, a.y); lineTo(b.x - 3f, b.y); lineTo(b.x + 2f + inclina, o.y + h); cubicTo(o.x + 4f + inclina, o.y + h + 2.4f, o.x - 4f + inclina, o.y + h - 1.6f, a.x - 2f + inclina, o.y + h); close() }
                3 -> Path().apply { for (s in listOf(-1f, 1f)) { val x = o.x + s * 2.2f; moveTo(x - 1.5f, o.y); lineTo(x + 1.5f, o.y); lineTo(x + 1.3f + inclina * 0.5f, o.y + h - 3f); lineTo(x + 3.6f + inclina * 0.5f, o.y + h - 1f); lineTo(x + 3f + inclina * 0.5f, o.y + h); lineTo(x - 1.3f + inclina * 0.5f, o.y + h); close() } }
                else -> Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(b.x + inclina, o.y + h - 1f); cubicTo(o.x + w * 0.2f + inclina, o.y + h + 1.6f, o.x - w * 0.2f + inclina, o.y + h - 2f, a.x + inclina, o.y + h); close() }
            }
            drawPath(forma, relleno)
            clipPath(forma) {
                // Los pliegues, y lo que cada prenda tiene de suyo.
                val n = (w / 6f).toInt().coerceAtLeast(1)
                for (i in 1..n) { val x = a.x + (b.x - a.x) * i / (n + 1f); drawPath(Path().apply { moveTo(x, a.y); cubicTo(x + 1.4f, o.y + h * 0.4f, x - 1f + inclina * 0.5f, o.y + h * 0.7f, x + inclina, o.y + h + 2f) }, osc.copy(alpha = 0.5f), style = Stroke(0.6f)); drawLine(claro.copy(alpha = 0.6f), Offset(x + 1.2f * lado, a.y + 1f), Offset(x + 1.2f * lado + inclina * 0.6f, o.y + h * 0.8f), 0.5f) }
                if (p.tipo == 6) for (i in 0..2) drawRect(z.c(o, C.GRANZA).copy(alpha = 0.8f), Offset(a.x - 2f, o.y + 3f + i * 5f), Size(w + inclina + 6f, 1.3f))
                if (p.tipo == 2) drawRect(z.c(o, C.ANIL).copy(alpha = 0.7f), Offset(a.x - 4f, o.y + h - 4.4f), Size(w + inclina + 8f, 1.2f))
                if (p.tipo == 5) drawRect(osc.copy(alpha = 0.6f), Offset(o.x - 2.4f + inclina * 0.4f, o.y + h * 0.45f), Size(4.8f, 4.4f))
                drawRect(osc.copy(alpha = 0.3f), Offset(a.x - 6f, o.y + h - 2.4f), Size(w + inclina + 12f, 4f))
            }
            // Las pinzas.
            for (q in listOf(a + (b - a) * 0.14f, a + (b - a) * 0.86f)) drawRect(z.c(q, lerp(C.MADERA, C.OCRE, 0.4f)), q + Offset(-0.6f, -1.6f), Size(1.2f, 3.4f))
        }
        // Los carámbanos de la helada.
        if (E.helada(m)) for (i in 1..12) { val q = enLaCuerda(i / 13f); drawPath(poligono(q + Offset(-0.7f, 0f), q + Offset(0.7f, 0f), q + Offset(0f, 2.6f + (i % 4) * 1.6f)), z.c(q, lerp(C.HIELO, Color.White, 0.5f))) }
        // En fiesta, los banderines de la casa al palo: de colores hasta la Bastilla; después, de los tres.
        if (E.fiesta(m)) banderines(l, CABO_A + Offset(0f, 8f), CABO_B + Offset(0f, 12f), 9f, if (E.banderas(m) > 0) listOf(C.ANIL, C.CAL, C.GRANZA) else listOf(C.CAL, C.GRANZA, C.OCRE, C.HOJA), 6f)
    }

    // ---------------------------------------------------------------- El cuadro

    /** Todo el primer plano, en su orden: la cubierta, la casa, las sombras, las cosas, la cuerda con la ropa y la segunda sombra. */
    fun DrawScope.barco(m: Momento, l: Luces, r: Rayo, a: Alumbrado) {
        val z = Luz(l, r, a)
        cubierta(m, z)
        // De noche, el charco de luz que cada lámpara deja en las tablas.
        for (f in a.focos) if (f.p.x > 60f && f.p.x < ANCHO) { val rx = (f.alcance * 0.8f).coerceAtMost(118f); charcoDeLuz(f, Offset(f.p.x - (if (f.p.x > 300f) 26f else 0f), minOf(f.suelo, ALTO - 16f)), rx, 12f + rx * 0.09f, C.MADERA, 0.9f) }
        borda(m, z)
        casa(m, z)
        // En la gran colada, las sábanas echadas sobre la borda.
        if (E.colada(m) && E.ropaTendida(m) > 0.3f) for ((i, x) in listOf(60f, 84f).withIndex()) { val o = Offset(x + 10f, BORDA); val forma = Path().apply { moveTo(x, BORDA - 4.6f); lineTo(x + 21f, BORDA - 4.6f); lineTo(x + 22f, BORDA + 13f - i * 2f); cubicTo(x + 14f, BORDA + 15f - i * 2f, x + 8f, BORDA + 11f, x - 1f, BORDA + 13f); close() }; drawPath(forma, z.redondo(o, C.CAL, x, x + 22f)); clipPath(forma) { for (k in 1..3) drawLine(z.oscuro(o, lerp(C.CAL, C.PIZARRA, 0.4f)).copy(alpha = 0.5f), Offset(x + k * 5.4f, BORDA - 4f), Offset(x + k * 5.4f + 1f, BORDA + 15f), 0.6f) } }

        val marcha = E.lugar(m) == EscenaRevolucion.Lugar.MARCHA
        // El día de la marcha, los delantales colgados de la borda, como los dejaron.
        if (marcha) for ((i, x) in listOf(157f, 174.6f, 192f).withIndex()) {
            val o = Offset(x + 7f, BORDA + 4f)
            val forma = Path().apply { moveTo(x, BORDA - 4.8f); lineTo(x + 14f, BORDA - 4.8f); lineTo(x + 15f, BORDA + 9f + i % 2 * 2f); cubicTo(x + 10f, BORDA + 11f + i % 2 * 2f, x + 5f, BORDA + 8f, x - 1f, BORDA + 10f); close() }
            drawPath(forma, z.redondo(o, if (i == 1) lerp(C.ANIL, C.PIZARRA, 0.35f) else C.ANIL, x, x + 14f))
            clipPath(forma) { for (k in 1..2) drawLine(z.oscuro(o, C.PIZARRA_OSC).copy(alpha = 0.45f), Offset(x + k * 4.6f, BORDA - 4f), Offset(x + k * 4.6f + 0.8f, BORDA + 12f), 0.6f); drawRect(z.claro(o, C.CAL).copy(alpha = 0.8f), Offset(x - 1f, BORDA - 4.8f), Size(16f, 1.3f)) }
            drawLine(z.c(o, C.CAL), Offset(x + 14f, BORDA - 4f), Offset(x + 18f, BORDA + 3f + i), 0.6f, StrokeCap.Round)                       // la cinta suelta
        }

        val cosas = ArrayList<Cosa>()
        cosas += escalera(m, z)
        cosas += cuba(m, z); cosas += hornillo(m, z)
        for ((i, x) in listOf(212f, 250f, 288f).withIndex()) cosas += cajon(m, z, x, i)
        cosas += banco(m, z)
        cosas += tonel(m, z)
        cosas += if (marcha) cestoVolcado(z, Offset(326f, 271f)) else cesto(m, z, Offset(326f, 271f), 17f, 19f, !E.barcoCerrado(m) && m.hora >= 6.0 && m.hora < 19.5, 1)
        cosas += palo(m, z)
        // En la helada, las barras de hierro con que rompen el hielo alrededor del casco, apoyadas en la borda.
        if (E.helada(m)) { val pie = Offset(202f, 244f); val forma = poligono(pie + Offset(-1f, 0f), pie + Offset(1f, 0f), Offset(197f, 192f), Offset(195.6f, 192f)); cosas += Cosa(forma, pie) { drawPath(forma, z.c(pie, C.HIERRO)); drawLine(z.c(pie, C.HIERRO), pie + Offset(6f, 2f), Offset(210f, 196f), 1.6f, StrokeCap.Round); drawLine(z.claro(pie, C.PIZARRA).copy(alpha = 0.6f), pie + Offset(0.4f, 0f), Offset(196.6f, 192f), 0.4f) } }
        // El día de la marcha, las paletas tiradas en la cubierta: la pala ancha y el mango, cada una como cayó.
        if (marcha) for ((o, d) in listOf(Offset(206f, 266f) to Offset(21f, -6f), Offset(112f, 279f) to Offset(24f, -2.4f), Offset(286f, 281f) to Offset(-23f, -2f))) {
            val n = Offset(-d.y, d.x) / d.getDistance() * 3.6f                                   // medio ancho de la pala
            val forma = poligono(o - n, o + d - n, o + d + n, o + n)
            cosas += Cosa(forma, o + Offset(0f, 2f)) {
                val madera = lerp(C.MADERA, C.OCRE, 0.4f)
                drawPath(forma, z.c(o, madera)); drawLine(z.claro(o, C.OCRE), o - n, o + d - n, 0.7f); drawLine(z.oscuro(o, C.MADERA_OSC), o + n, o + d + n, 0.9f)
                for (i in 1..3) drawLine(z.oscuro(o, C.MADERA_OSC).copy(alpha = 0.35f), o + d * (i / 4f) - n, o + d * (i / 4f) + n, 0.4f)
                drawLine(z.c(o, madera), o + d, o + d * 1.62f, 2.6f, StrokeCap.Round); drawLine(z.claro(o, C.OCRE).copy(alpha = 0.7f), o + d + Offset(0f, -0.6f), o + d * 1.62f + Offset(0f, -0.6f), 0.6f)
            }
        }
        cosas.sortBy { it.pie.y }

        // La primera capa de sombras: la de cada cosa, tendida en las tablas, y la de la ropa de la cuerda.
        if (r.fuerza > 0.05f) capaSombra(r) { c ->
            for (k in cosas) sombraArrojada(r, k.forma, k.pie, c, BORDA + 1f, BORDA + 1f)
            // Con el sol del lado de la casa, su sombra se tiende por la cubierta.
            if (r.lat < 0f) { val largo = (64f * r.cot).coerceIn(26f, 170f); sombra(poligono(Offset(0f, BORDA + 1f), Offset(PARED + largo, BORDA + 1f), Offset(PARED + largo * 1.25f, ALTO), Offset(0f, ALTO)), c) }
        } else if (a.focos.isNotEmpty()) capaSombra(rayoFijo(0.5f, 40f, 0.5f * l.noche, C.SOMBRA)) { c ->
            for (k in cosas) { val f = a.focos.maxByOrNull { it.fuerza / (1f + (it.p - k.pie).getDistanceSquared() / (it.alcance * it.alcance)) }!!; sombraArrojada(rayoDeFoco(f, k.pie, 0.5f, C.SOMBRA), k.forma, k.pie, c, BORDA + 1f, BORDA + 1f) }
        }
        for (k in cosas) with(k) { pintar() }
        elPan(m, z)
        // El objeto del día, sobre el banco.
        if (!E.helada(m)) with(ObjetosRevolucion) { objeto(m, l, r, a, SOBRE + Offset(0f, -3.2f)) }
        // La guirnalda de la fiesta, en la borda, y la cuerda con su ropa.
        if (E.fiesta(m)) guirnalda(l, Offset(208f, BORDA - 4f), Offset(322f, BORDA - 4f), 7f, m.azar("guirnalda"), flores = listOf(C.GRANZA, C.CAL, C.OCRE))
        tendido(m, z)
        // Con el suelo mojado, las luces se tienden en las tablas.
        if (E.mojado(m)) for (f in a.focos) if (f.p.x in 60f..ANCHO) reflejoMojado(Offset(f.p.x, 236f), 44f, 10f, f.color, 0.5f * f.fuerza, m.azar("mojado", f.p.x.toInt()))
    }

    // ---------------------------------------------------------------- Lo que se mueve en la popa

    /** El vapor de la cuba y del caldero, la lumbre del hornillo y la llama del farol. */
    fun DrawScope.barcoVivo(m: Momento, l: Luces, reloj: Float) {
        val viento = m.viento * 0.7f + 0.1f
        if (E.lumbre(m)) {
            for (i in 0..2) llama(BOCA + Offset(-3.4f + i * 3.4f, 8f), 7f + (i % 2) * 2f, reloj, i)
            humo(l, Offset(186f, 236f), 34f, 16f, reloj, viento, 0f, 0f, 3, 8, 0.6f)
        }
        if (E.lavando(m)) humo(l, Offset(126f, 206f), if (E.colada(m)) 62f else 44f, if (E.colada(m)) 34f else 24f, reloj, viento, 0f, 0f, 5, 10, if (E.colada(m)) 0.8f else 0.6f)
        if (E.farolDePopa(m)) drawOval(Color(0xFFFFFBE6).copy(alpha = 0.7f + 0.25f * sin(reloj * 8f)), Offset(E.FAROL.x - 1f, 148.6f + 0.4f * sin(reloj * 5f)), Size(2f, 4.4f))
    }
}
