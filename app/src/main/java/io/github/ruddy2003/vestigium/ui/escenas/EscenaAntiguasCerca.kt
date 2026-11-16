package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.p
import io.github.ruddy2003.vestigium.ui.escenas.MulasAntiguas.mulas
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAntiguas.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.PosadaAntiguas.posada
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.SueloAntiguas.suelo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada

/**
 * El primer plano: el delante de la puerta. La posada, y las cosas que deja la gente donde las dejaría, a distintas
 * hondas y tapándose: el montón de leña arrimado al pino, la montura de carga en su caballete (cortada por el borde de
 * abajo), el banco arrimado al muro del zaguán, el bastón de Brenna junto a la puerta, la pala en la esquina, el cubo, las
 * mulas en la argolla; y, según el día, la cuna, el trineo de duelas de barril, los haces de leña, el fardo del arpista.
 * Cada cosa con su sombra arrojada (del sol, o de la luna) y, de noche, su lado de la puerta encendido.
 */
internal object CercaAntiguas {
    private val C = ColorAntiguas

    /** Lo alto del montón de leña (el mirador del gato) y dónde va la cuna (delante del poyo). */
    val LENA = Offset(78f, 226f)
    val CUNA = Offset(318f, 272f)

    /** El trineo de duelas de los niños, en invierno, olvidado en la plaza (detrás del suelo del gato). */
    val TRINEO = Offset(198f, 262f)

    /** Cuánta lumbre de la puerta le llega de noche a [q], por su distancia al umbral (cero con la puerta cerrada). */
    fun lumbreEn(v: VistaAntiguas, q: Offset): Float = lumbreEn(v.m, v.encendido.sala, v.noche, q)

    fun lumbreEn(m: Momento, sala: Float, noche: Float, q: Offset): Float {
        if (EscenaAntiguas.puerta(m) == EscenaAntiguas.Puerta.CERRADA) return 0f
        return (sala * noche * (1f - (q - PuertaAntiguas.UMBRAL).getDistance() / 180f)).coerceIn(0f, 0.85f)
    }

    fun DrawScope.primerPlano(v: VistaAntiguas) {
        suelo(v)
        with(LuzAntiguas) { charcoDeLaPuerta(v) }
        posada(v)
        val grande = grande(v)
        val cosas = cosas(v) + listOfNotNull(grande)
        capaSombra(v.r) { c ->
            for (k in cosas) { val f = 1f - v.sombraEn(k.pie); if (f > 0.02f) sombraDe(v, k, c.copy(alpha = f), soloSuelo = k === grande) }
            with(PinoAntiguas) { sombraDelPino(v, c) }
        }
        for (k in cosas.sortedBy { it.pie.y }) k.pintar(this)
        // El objeto chico del día, sobre el poyo.
        val sobre = PuertaAntiguas.SOBRE_EL_POYO
        with(ObjetosAntiguas) { sobreElSoporte(v.m, v.pincelEn(sobre, lumbre = v.encendido.sala * v.noche * 0.6f), sobre, FachadaPosada.escala(-0.62f, 0.25f) / 48f) }
    }

    /** Hasta dónde llega hacia el fondo la sombra de lo que está en la plaza (lo que no tiene la posada detrás). */
    private const val FONDO_DE_LA_PLAZA = 230f

    /**
     * La sombra arrojada de una cosa: en el suelo hasta el pie del muro del zaguán (o, en la plaza, hasta su fondo) y, lo
     * que pasa de ahí cuando la luz viene de delante, subiendo por el muro, entre la esquina y la puerta. La del objeto
     * grande, solo en el suelo: la del muro la pinta el objeto, que sabe a qué distancia del muro está cada parte suya.
     */
    private fun DrawScope.sombraDe(v: VistaAntiguas, k: Cosa, c: Color, soloSuelo: Boolean) {
        val base = FachadaPosada.baseEn(k.pie.x)?.takeIf { it < k.pie.y - 0.5f }
        if (base == null) {
            val suelo = minOf(maxOf(k.pie.y - 44f, FONDO_DE_LA_PLAZA), ALTO - 2f)
            sombraArrojada(v.r, k.forma, k.pie, c, suelo, suelo)
            return
        }
        val alMuro = !soloSuelo && v.r.ay < -0.02f
        sombraArrojada(v.r, k.forma, k.pie, c, base, if (alMuro) base - 140f else base, p(FachadaPosada.S_FIN, 0f).x, p(PuertaAntiguas.PUERTA_S1, 0f).x)
    }

    // ---------------------------------------------------------------- Las cosas

    /** Las cosas del primer plano, cada una con su silueta (para la sombra), dónde pisa y cómo se pinta. */
    private fun cosas(v: VistaAntiguas): List<Cosa> = buildList {
        val m = v.m; val grande = ObjetosAntiguas.esGrande(m)
        add(lena(v))
        if (ObjetosAntiguas.elCuadroPinta(m, "montura")) add(caballete(v))
        add(banco(v))
        if (EscenaAntiguas.bastonEnLaPuerta(m) && ObjetosAntiguas.elCuadroPinta(m, "baston")) add(baston(v, 1.22f, 0.2f, C.FRESNO))
        if (Senal.BASTON_NUEVO in v.senales) add(baston(v, 1.05f, 0.26f, lerp(C.FRESNO, C.NIEVE, 0.35f)))
        // El día de un objeto grande, la pala y el cubo se apartan de su sitio (entre la esquina y la jamba): la pala, a la
        // leña; el cubo, delante de las mulas.
        add(if (grande) pala(v, Offset(127f, 289f), Offset(117f, 214f), 0.45f) else pala(v, p(2.12f, 0f, 0.4f), p(2.16f, 1.45f, 0.02f), 0f))
        add(if (grande) cubo(v, 1.8f, 1.5f) else cubo(v, 1.3f, 0.42f))
        if (EscenaAntiguas.cuna(m)) add(cuna(v))
        if (v.invierno) add(trineo(v))
        if (Senal.LENA in v.senales) add(haces(v))
        if (Senal.FARDO_DEL_ARPISTA in v.senales) add(fardo(v))
        val n = if (Senal.MULAS_DE_VUELTA in v.senales) 2 else EscenaAntiguas.mulas(m)
        if (n > 0) {
            val (pie, alto) = MulasAntiguas.pieYAlto(MulasAntiguas.mulasDe(grande)[0])
            add(Cosa(MulasAntiguas.silueta(pie, alto), pie) { mulas(v, n, EscenaAntiguas.mulasCargadas(m), grande) })
        }
    }

    /**
     * El objeto grande del día, recostado en el muro del zaguán junto al banco. De noche le llega la lumbre de la puerta
     * (casi tanta como al poyo: está a tres pasos del umbral y la luz de la sala le da de lado).
     */
    private fun grande(v: VistaAntiguas): Cosa? {
        val pie = p(1.82f, 0f, 0.46f); val apoyo = p(1.82f, 1.0f, 0.02f)
        return ObjetosAntiguas.grande(v.m, v.pincelEn(pie, lumbre = v.encendido.sala * v.noche * 0.8f), pie, apoyo, FachadaPosada.escala(1.82f, 0.46f) / 48f)
    }

    /** El montón de leña arrimado al pie del pino, bajo su tejadillo de losas: troncos partidos, apilados, con sus testas claras; en invierno, más alto y con su nieve. */
    private fun lena(v: VistaAntiguas): Cosa {
        val pie = Offset(80f, 286f); val alto = if (v.invierno) 58f else 52f; val ancho = 74f
        val forma = poligono(pie + Offset(-ancho / 2f, 0f), pie + Offset(ancho / 2f, 0f), pie + Offset(ancho / 2f - 2f, -alto), pie + Offset(-ancho / 2f + 4f, -alto - 4f))
        return Cosa(forma, pie, bajoElFollaje = true) {
            val pe = v.pincelEn(pie + Offset(0f, -26f)); val az = fijo("leña")
            drawPath(forma, pe.sombra(C.MADERA_OSC))
            // (Los anillos de dentro y las grietas de las testas, en un solo trazo cada uno.)
            val anillos = Path(); val grietas = Path()
            var y = pie.y - 5f; var fila = 0
            while (y > pie.y - alto + 3f) {
                var x = pie.x - ancho / 2f + 1f + (fila % 2) * 3.4f
                while (x < pie.x + ancho / 2f - 4f) {
                    val r = az.entre(3.2f, 4.6f)
                    val c = Offset(x + r, y + az.entre(-0.6f, 0.6f))
                    drawCircle(pe.medio(C.MADERA_OSC), r + 0.6f, c)
                    drawCircle(pe.por(lerp(C.FRESNO, C.MADERA, az.entre(0.05f, 0.45f)), 0.45f), r, c)
                    anillos.addOval(androidx.compose.ui.geometry.Rect(c.x - r * 0.55f, c.y - r * 0.55f, c.x + r * 0.55f, c.y + r * 0.55f))
                    grietas.moveTo(c); grietas.lineTo(c + Offset(r * 0.8f, -r * 0.3f))
                    x += r * 2f + 0.5f
                }
                y -= 8f; fila++
            }
            drawPath(anillos, pe.sombra(C.MADERA).copy(alpha = 0.4f), style = Stroke(0.4f))
            drawPath(grietas, pe.sombra(C.MADERA).copy(alpha = 0.5f), style = Stroke(0.4f))
            // El tejadillo de losas sobre dos palos.
            val t0 = pie + Offset(-ancho / 2f - 3f, -alto - 3f); val t1 = pie + Offset(ancho / 2f + 4f, -alto + 2f)
            drawPath(poligono(t0, t1, t1 + Offset(-2f, -6f), t0 + Offset(3f, -8f)), pe.por(C.LOSA, 0.35f))
            drawLine(pe.luz(lerp(C.LOSA, C.NIEVE, 0.3f)).copy(alpha = 0.6f), t0 + Offset(3f, -8f), t1 + Offset(-2f, -6f), 0.8f)
            drawLine(pe.sombra(C.ESCORIA), t0, t1, 1.2f)
            if (v.nieveAbajo > 0.2f) drawPath(poligono(t0 + Offset(2f, -7f), t1 + Offset(-2f, -5f), t1 + Offset(-3f, -5f - 5f * v.nieveAbajo), t0 + Offset(4f, -9f - 5f * v.nieveAbajo)), pe.por(C.NIEVE, 0.5f).copy(alpha = v.nieveAbajo))
        }
    }

    /** La montura de carga en su caballete, en primer plano y cortada por el borde de abajo: el caballete de dos tijeras y el madero, y encima la montura de haya forrada de fieltro. */
    private fun caballete(v: VistaAntiguas): Cosa {
        val pie = Offset(176f, 294f); val k = 1.62f
        val forma = poligono(pie + Offset(-22f * k, 0f), pie + Offset(22f * k, 0f), pie + Offset(20f * k, -40f * k), pie + Offset(-20f * k, -40f * k))
        return Cosa(forma, pie) { scale(k, k, pie) { dibujoCaballete(v, pie) } }
    }

    private fun DrawScope.dibujoCaballete(v: VistaAntiguas, pie: Offset) {
        val pe = v.pincelEn(pie + Offset(0f, -40f), lumbre = lumbreEn(v, pie + Offset(0f, -50f))); val madera = lerp(C.MADERA, C.FRESNO, 0.2f)
        for (dx in listOf(-17f, 15f)) { drawLine(pe.por(madera, -0.2f), pie + Offset(dx - 5f, 0f), pie + Offset(dx + 1f, -26f), 2.4f); drawLine(pe.por(madera, 0.2f), pie + Offset(dx + 6f, -1f), pie + Offset(dx, -26f), 2.4f) }
        drawLine(pe.por(madera, 0.3f), pie + Offset(-21f, -26f), pie + Offset(21f, -27f), 3.4f, StrokeCap.Round)
        // La montura: la manta de fieltro que cuelga a los lados y los dos arcos de haya con sus ganchos.
        drawPath(poligono(pie + Offset(-16f, -27f), pie + Offset(16f, -28f), pie + Offset(14f, -14f), pie + Offset(-15f, -13f)), pe.por(lerp(C.RUBI, C.CUERO, 0.4f), 0.15f))
        drawLine(pe.luz(C.ORO).copy(alpha = 0.6f), pie + Offset(-15f, -15f), pie + Offset(14f, -16f), 0.8f)
        val haya = lerp(C.FRESNO, C.MADERA, 0.2f)
        for (dx in listOf(-9f, 8f)) drawPath(Path().apply { moveTo(pie + Offset(dx - 6f, -28f)); cubicTo(pie + Offset(dx - 6f, -40f), pie + Offset(dx + 6f, -40f), pie + Offset(dx + 6f, -28f)) }, pe.por(haya, 0.35f), style = Stroke(2.6f, cap = StrokeCap.Round))
        drawLine(pe.por(haya, 0.25f), pie + Offset(-16f, -35f), pie + Offset(15f, -35f), 2.2f, StrokeCap.Round)
        for (dx in listOf(-14f, 13f)) drawPath(Path().apply { moveTo(pie + Offset(dx, -35f)); cubicTo(pie + Offset(dx, -30f), pie + Offset(dx + 3f, -30f), pie + Offset(dx + 3f, -33f)) }, pe.medio(C.ESCORIA), style = Stroke(0.9f))
        // Las borlas de lana de la manta, y la cincha que cuelga.
        for (k in 0..4) { val q = pie + Offset(-14f + k * 7f, -13.4f); drawLine(pe.por(C.LANA, 0.2f), q, q + Offset(0.4f, 2.6f), 1.1f, StrokeCap.Round) }
        drawPath(Path().apply { moveTo(pie + Offset(6f, -14f)); cubicTo(pie + Offset(8f, -6f), pie + Offset(4f, -4f), pie + Offset(6f, 2f)) }, pe.por(C.CUERO, 0f), style = Stroke(1.4f, cap = StrokeCap.Round))
        if (v.nieveAbajo > 0.3f) {
            // La nieve sobre el madero y en lo alto de los dos arcos.
            drawLine(pe.por(C.NIEVE, 0.5f), pie + Offset(-15f, -36.6f), pie + Offset(14f, -36.6f), 1.3f, StrokeCap.Round)
            for (dx in listOf(-9f, 8f)) drawPath(Path().apply { moveTo(pie + Offset(dx - 4.6f, -36.6f)); cubicTo(pie + Offset(dx - 4f, -40f), pie + Offset(dx + 4f, -40f), pie + Offset(dx + 4.6f, -36.6f)) }, pe.por(C.NIEVE, 0.5f), style = Stroke(1.2f, cap = StrokeCap.Round))
        }
    }

    /** El banco de tablas arrimado al muro del zaguán, gastado donde se sientan. */
    private fun banco(v: VistaAntiguas): Cosa {
        val s0 = 1.42f; val s1 = 2.16f; val fondo = 0.36f; val alto = 0.46f
        val pie = p((s0 + s1) / 2f, 0f, fondo)
        val forma = poligono(p(s0, 0f, fondo), p(s1, 0f, fondo), p(s1, alto, fondo), p(s0, alto, fondo))
        return Cosa(forma, pie) {
            val pe = PosadaAntiguas.pincel(v, (s0 + s1) / 2f, 0.4f, fondo)
            val madera = lerp(C.MADERA, C.LANA, 0.18f); val t = PosadaAntiguas.luzDeCara(v)
            for (s in listOf(s0 + 0.06f, s1 - 0.06f)) for (f in listOf(0.06f, fondo - 0.04f)) drawLine(pe.por(madera, -0.3f), p(s, 0f, f), p(s, alto - 0.04f, f), 1.6f)
            drawPath(FachadaPosada.tapa(s0, s1, 0f, fondo, alto), pe.por(madera, 0.4f))
            drawPath(FachadaPosada.cara(s0, s1, alto - 0.06f, alto, fondo), pe.por(madera, t))
            drawLine(pe.luz(lerp(madera, C.NIEVE, 0.3f)).copy(alpha = 0.6f), p(s0, alto, fondo), p(s1, alto, fondo), 0.8f)
            if (v.nieveAbajo > 0.3f) drawPath(poligono(p(s0, alto, 0.02f), p(s1, alto, 0.02f), p(s1, alto + 0.07f * v.nieveAbajo, fondo * 0.5f), p(s0, alto + 0.07f * v.nieveAbajo, fondo * 0.5f)), pe.por(C.NIEVE, 0.5f).copy(alpha = v.nieveAbajo))
        }
    }

    /** Un bastón de guía apoyado en el muro: de fresno, de su altura y un palmo más, con su punta de hierro y su correa. */
    private fun baston(v: VistaAntiguas, s: Float, fuera: Float, madera: Color): Cosa {
        val pie = p(s, 0f, fuera); val arriba = p(s - 0.06f, 1.82f, 0.03f)
        val forma = poligono(pie + Offset(-1.2f, 0f), pie + Offset(1.2f, 0f), arriba + Offset(1f, 0f), arriba + Offset(-1f, 0f))
        return Cosa(forma, pie) {
            val pe = PosadaAntiguas.pincel(v, s, 0.9f, fuera)
            drawLine(pe.sombra(madera), pie + Offset(0.6f, 0f), arriba + Offset(0.6f, 0f), 2.4f, StrokeCap.Round)
            drawLine(pe.por(madera, 0.3f), pie, arriba, 1.7f, StrokeCap.Round)
            drawLine(pe.medio(C.ESCORIA), pie, pie + (arriba - pie) * 0.05f, 2.1f, StrokeCap.Round)
            val c = pie + (arriba - pie) * 0.8f
            drawOval(pe.medio(C.CUERO), Offset(c.x - 1.6f, c.y - 1f), Size(3.2f, 6f), style = Stroke(0.7f))
        }
    }

    /**
     * La pala, de [pie] a [arriba] (donde se apoya): en la esquina del zaguán o, el día de un objeto grande, arrimada a la
     * leña, [mas] veces más grande por estar más cerca. En invierno, de madera ancha, para la nieve.
     */
    private fun pala(v: VistaAntiguas, pie: Offset, arriba: Offset, mas: Float): Cosa {
        val g = 1f + mas
        val forma = poligono(pie + Offset(-4f * g, 0f), pie + Offset(4f * g, 0f), arriba + Offset(1f, 0f), arriba + Offset(-1f, 0f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie + (arriba - pie) * 0.4f, lumbre = lumbreEn(v, pie))
            drawLine(pe.por(C.FRESNO, 0.2f), pie + (arriba - pie) * 0.18f, arriba, 1.8f * g, StrokeCap.Round)
            val hoja = if (v.invierno) lerp(C.MADERA, C.FRESNO, 0.4f) else C.PIEDRA_OSC
            val cuello = pie + (arriba - pie) * 0.2f
            drawPath(poligono(pie + Offset(-4.4f * g, 0f), pie + Offset(4.6f * g, -1f), cuello + Offset(3f * g, 0f), cuello + Offset(-3f * g, 0f)), pe.por(hoja, 0.25f))
            drawLine(pe.luz(hoja).copy(alpha = 0.5f), pie + Offset(-4.4f * g, 0f), cuello + Offset(-3f * g, 0f), 0.6f)
        }
    }

    /** El cubo de madera con sus aros de hierro, el agua de las mulas: junto a la argolla o, el día de un objeto grande, delante de ellas. */
    private fun cubo(v: VistaAntiguas, s: Float, fuera: Float): Cosa {
        val pie = p(s, 0f, fuera); val k = FachadaPosada.escala(s, fuera) / 55f; val w = 8f * k; val h = 11f * k
        val forma = poligono(pie + Offset(-w / 2f, 0f), pie + Offset(w / 2f, 0f), pie + Offset(w / 2f + 1f, -h), pie + Offset(-w / 2f - 1f, -h))
        return Cosa(forma, pie) {
            val pe = PosadaAntiguas.pincel(v, s, 0.3f, fuera)
            drawPath(forma, Brush.horizontalGradient(listOf(pe.luz(C.MADERA), pe.sombra(C.MADERA)), pie.x - w / 2f, pie.x + w / 2f))
            for (y in listOf(-2.4f * k, -8.6f * k)) drawLine(pe.medio(C.ESCORIA), pie + Offset(-w / 2f - 0.4f, y), pie + Offset(w / 2f + 0.4f, y), 0.9f)
            drawOval(pe.sombra(if (v.invierno) C.NIEVE else C.AGUA_HONDA), Offset(pie.x - w / 2f - 1f, pie.y - h - 1.4f), Size(w + 2f, 2.8f))
        }
    }

    /** La cuna «que Tobin hizo con una caja de manzanas del valle», junto a la puerta, con su manta que lleva una runa en cada esquina. */
    private fun cuna(v: VistaAntiguas): Cosa {
        val pie = CUNA; val w = 26f; val h = 12f
        val forma = poligono(pie + Offset(-w / 2f, 0f), pie + Offset(w / 2f, 0f), pie + Offset(w / 2f, -h), pie + Offset(-w / 2f, -h))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie + Offset(0f, -6f), lumbre = v.encendido.sala * v.noche * 0.7f)
            val caja = lerp(C.FRESNO, C.MADERA, 0.3f)
            drawPath(forma, pe.por(caja, 0.15f))
            for (k in 1..2) drawLine(pe.sombra(caja), pie + Offset(-w / 2f, -h * k / 3f), pie + Offset(w / 2f, -h * k / 3f), 0.6f)
            drawPath(poligono(pie + Offset(-w / 2f + 1f, -h), pie + Offset(w / 2f - 1f, -h), pie + Offset(w / 2f - 3f, -h - 4f), pie + Offset(-w / 2f + 3f, -h - 4.6f)), pe.por(C.RUBI, 0.3f))
            for (dx in listOf(-w / 2f + 4f, w / 2f - 5f)) with(PiezasAntiguas) { runa(pe, Runa.REFUGIO, pie + Offset(dx, -h - 2.2f), 2.4f, C.RUBI) }
            drawLine(pe.medio(C.ESCORIA), pie + Offset(-w / 2f, -h), pie + Offset(w / 2f, -h), 0.8f)
        }
    }

    /** El trineo de los niños, hecho con duelas de barril, olvidado en la nieve. */
    private fun trineo(v: VistaAntiguas): Cosa {
        val pie = TRINEO
        val forma = poligono(pie + Offset(-14f, 0f), pie + Offset(14f, 0f), pie + Offset(12f, -6f), pie + Offset(-12f, -6f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie, lumbre = lumbreEn(v, pie))
            for (k in 0..3) drawPath(Path().apply { moveTo(pie + Offset(-14f, -1f - k * 1.4f)); cubicTo(pie + Offset(-6f, 1f - k * 1.4f), pie + Offset(6f, 1f - k * 1.4f), pie + Offset(14f, -3f - k * 1.4f)) }, pe.por(lerp(C.MADERA, C.FRESNO, 0.3f), 0.3f - k * 0.1f), style = Stroke(1.4f, cap = StrokeCap.Round))
            drawLine(pe.medio(C.ESCORIA), pie + Offset(-12f, -2f), pie + Offset(-12f, -6f), 0.7f); drawLine(pe.medio(C.ESCORIA), pie + Offset(12f, -3f), pie + Offset(12f, -7f), 0.7f)
            drawPath(Path().apply { moveTo(pie + Offset(-14f, -2f)); cubicTo(pie + Offset(-20f, -4f), pie + Offset(-22f, 2f), pie + Offset(-18f, 4f)) }, pe.medio(C.CUERO), style = Stroke(0.6f))
        }
    }

    /** Los haces de leña para los refugios, atados, esperando en las losas delante del poyo. */
    private fun haces(v: VistaAntiguas): Cosa {
        val pie = p(-0.3f, 0f, 1.5f) + Offset(-6f, 0f)
        val forma = poligono(pie + Offset(-18f, 0f), pie + Offset(18f, 0f), pie + Offset(16f, -10f), pie + Offset(-16f, -10f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie, lumbre = lumbreEn(v, pie))
            for (h in 0..2) for (j in 0..5) { val y = pie.y - 2f - h * 3.2f - j * 0.3f; drawLine(pe.por(lerp(C.MADERA, C.FRESNO, 0.3f), 0.3f - j * 0.08f), Offset(pie.x - 16f + h * 2f, y), Offset(pie.x + 16f - h * 2f, y - 0.6f), 1.6f, StrokeCap.Round) }
            for (dx in listOf(-8f, 8f)) drawLine(pe.medio(C.CUERO), pie + Offset(dx, -0.4f), pie + Offset(dx, -9.4f), 0.9f)
        }
    }

    /** El fardo del arpista que llega: un bulto de lana y la funda de cuero del arpa, larga y triangular, apoyados en el banco. */
    private fun fardo(v: VistaAntiguas): Cosa {
        val pie = p(1.55f, 0f, 0.62f)
        val forma = poligono(pie + Offset(-8f, 0f), pie + Offset(10f, 0f), pie + Offset(6f, -30f), pie + Offset(-2f, -32f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie, lumbre = lumbreEn(v, pie))
            drawOval(pe.por(C.LANA, 0.2f), Offset(pie.x - 9f, pie.y - 9f), Size(14f, 9f))
            drawPath(poligono(pie + Offset(-1f, 0f), pie + Offset(9f, 0f), pie + Offset(5f, -30f), pie + Offset(0f, -32f)), pe.por(C.CUERO, 0.3f))
            drawLine(pe.luz(C.ORO).copy(alpha = 0.6f), pie + Offset(1f, -4f), pie + Offset(2f, -28f), 0.6f)
        }
    }
}
