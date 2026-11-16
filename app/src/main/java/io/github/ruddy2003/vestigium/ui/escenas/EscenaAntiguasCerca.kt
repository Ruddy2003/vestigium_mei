package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Senal
import io.github.ruddy2003.vestigium.ui.escenas.FachadaPosada.p
import io.github.ruddy2003.vestigium.ui.escenas.MulasAntiguas.mulas
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAntiguas.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.PosadaAntiguas.posada
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas.fijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima

/**
 * El primer plano: el suelo de delante de la puerta (las losas en fuga con el muro, la tierra pisada hacia el pino, la
 * hierba de las juntas), la posada y las cosas que deja la gente donde las dejaría: el montón de leña arrimado al pino, la
 * montura de carga en su caballete, el banco sacado al sol, el bastón de Brenna y la pala apoyados en el muro, el cubo,
 * las mulas en la argolla; y, según el día, la cuna, el trineo de duelas de barril, los haces de leña, el fardo del
 * arpista. Cada cosa con su sombra arrojada (del sol, o de la luna) y, de noche, su lado de la puerta encendido.
 */
internal object CercaAntiguas {
    private val C = ColorAntiguas

    /** Lo alto del montón de leña (el mirador del gato) y dónde va la cuna. */
    val LENA = Offset(78f, 226f)
    val CUNA = Offset(318f, 268f)

    /** El trineo de duelas de los niños, en invierno (plantado en el suelo del gato). */
    val TRINEO = Offset(222f, 266f)

    /** Los extremos del banco (a ras de suelo) y el alto de su asiento, en metros. */
    private const val BANCO_Z = 10.9f

    fun DrawScope.primerPlano(v: VistaAntiguas) {
        val az = fijo("cerca")
        suelo(v, az)
        posada(v)
        val cosas = cosas(v)
        capaSombra(v.r) { c ->
            for (k in cosas) { val f = 1f - v.sombraEn(k.pie); if (f > 0.02f) { val suelo = minOf(k.pie.y - 1f, ALTO - 2f); sombraArrojada(v.r, k.forma, k.pie, c.copy(alpha = f), suelo, suelo) } }
            with(PinoAntiguas) { sombraDelPino(v, c) }
        }
        for (k in cosas.sortedBy { it.pie.y }) k.pintar(this)
        // El objeto chico del día, sobre el poyo.
        with(ObjetosAntiguas) { sobreElSoporte(v.m, v.pincelEn(PosadaAntiguas.SOBRE_EL_POYO, lumbre = v.encendido.sala * v.noche * 0.6f), PosadaAntiguas.SOBRE_EL_POYO, 1f) }
    }

    // ---------------------------------------------------------------- El suelo

    /**
     * El suelo: las losas delante de la puerta (en fuga con el muro, cada una con su tono, sus juntas y su hierba), la tierra
     * pisada y la hierba hacia el pino; con nieve, el manto y la zanja abierta a pala hasta la puerta; con lluvia, mojado y
     * con charcos que devuelven la luz.
     */
    private fun DrawScope.suelo(v: VistaAntiguas, az: Azar) {
        val hierba = EscenaAntiguas.hierba(v.m)
        val borde = listOf(Offset(-10f, 258f), Offset(60f, 257f), Offset(130f, 254f), Offset(190f, 250f), Offset(240f, 246f), Offset(300f, 238f), Offset(344f, 232f))
        val forma = poligono(borde + listOf(Offset(392f, 232f), Offset(392f, ALTO + 6f), Offset(-10f, ALTO + 6f)))
        val pe = v.pincelEn(Offset(160f, 270f))
        drawPath(forma, Brush.verticalGradient(0f to pe.medio(lerp(hierba, C.TIERRA, 0.45f)), 1f to pe.medio(lerp(hierba, C.TIERRA, 0.65f)), startY = 240f, endY = ALTO))
        clipPath(forma) {
            val puntos = (0 until 260).map { Offset(az.entre(-6f, 230f), az.entre(248f, ALTO)) }
            drawPoints(puntos.filterIndexed { i, _ -> i % 2 == 0 }, PointMode.Points, pe.luz(lerp(hierba, C.ORO, 0.3f)).copy(alpha = 0.5f), 1.6f, StrokeCap.Round)
            drawPoints(puntos.filterIndexed { i, _ -> i % 2 == 1 }, PointMode.Points, pe.sombra(lerp(hierba, C.PINO, 0.4f)).copy(alpha = 0.5f), 1.6f, StrokeCap.Round)
            // El camino pisado que va de la puerta a la plaza.
            drawPath(Path().apply { moveTo(Offset(392f, 300f)); cubicTo(Offset(320f, 272f), Offset(270f, 258f), Offset(236f, 240f)) }, pe.por(lerp(C.TIERRA, C.LANA, 0.3f), 0.2f).copy(alpha = 0.6f), style = Stroke(16f, cap = StrokeCap.Round))
            losas(v, az)
            if (v.nieveAbajo > 0.02f) nieve(v, az)
            else if (v.clima == Clima.LLUVIA || v.clima == Clima.TORMENTA) mojado(v, az)
        }
    }

    /** Las losas de delante de la puerta, en fuga con el muro. */
    private fun DrawScope.losas(v: VistaAntiguas, az: Azar) {
        var z = 7.8f
        while (z < 13.6f) {
            val dz = az.entre(0.45f, 0.7f); var f = 0f
            while (f < 3.4f) {
                val df = az.entre(0.45f, 0.85f)
                fun j() = az.entre(-0.06f, 0.06f)
                val forma = poligono(p(z + 0.04f + j(), 0f, f + 0.04f + j()), p(z + dz - 0.04f + j(), 0f, f + 0.04f + j()), p(z + dz - 0.04f + j(), 0f, f + df - 0.04f + j()), p(z + 0.04f + j(), 0f, f + df - 0.04f + j()))
                val c = p(z + dz / 2f, 0f, f + df / 2f); val pe = v.pincelEn(c)
                val tono = lerp(lerp(C.PIEDRA, C.TIERRA, 0.3f), if (az.real() < 0.5f) C.TIERRA else C.ROCA, az.entre(0f, 0.4f))
                drawPath(forma, pe.por(tono, az.entre(-0.25f, 0.15f)).copy(alpha = (1f - f / 4.2f).coerceIn(0.2f, 1f)))
                // El canto gastado, más claro, del lado de la luz.
                drawLine(pe.luz(lerp(tono, C.NIEVE, 0.3f)).copy(alpha = 0.4f * (1f - f / 4f)), p(z + 0.05f, 0f, f + 0.05f), p(z + dz - 0.05f, 0f, f + 0.05f), 0.6f)
                if (az.real() < 0.3f) drawLine(pe.por(lerp(EscenaAntiguas.hierba(v.m), C.PINO, 0.3f), 0f).copy(alpha = 0.6f), p(z + dz, 0f, f), p(z + dz, 0f, f + df * 0.6f), 1f)
                f += df
            }
            z += dz
        }
    }

    /** La nieve del primer plano: el manto con su sombra azul y, con el paso cerrado, la zanja abierta a pala de la puerta a la plaza, con las losas mojadas al fondo. */
    private fun DrawScope.nieve(v: VistaAntiguas, az: Azar) {
        val n = v.nieveAbajo; val pe = v.pincelEn(Offset(200f, 270f))
        if (!v.invierno) {
            // Nieve recién caída o vieja en manchas: se queda en las juntas, al pie del muro y donde no se pisa.
            if (v.clima == Clima.NIEVE) drawRect(Brush.verticalGradient(0f to pe.por(C.NIEVE, 0.3f).copy(alpha = 0f), 0.15f to pe.por(C.NIEVE, 0.3f).copy(alpha = 0.6f), 1f to pe.por(C.NIEVE, 0.1f).copy(alpha = 0.7f), startY = 228f, endY = ALTO), Offset(-10f, 228f), Size(410f, 70f))
            repeat((60 + 160 * n).toInt()) { val q = Offset(az.entre(-6f, 386f), az.entre(236f, ALTO + 4f)); val w = az.entre(2f, 6f) * (0.5f + n) * (0.6f + (q.y - 230f) / 80f)
                drawPath(TrazosValle.blando(q, w, w * 0.25f, az, 8, 0.25f), pe.por(C.NIEVE, 0.3f)); drawLine(pe.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.5f), q + Offset(-w * 0.7f, w * 0.25f), q + Offset(w * 0.7f, w * 0.25f), 0.8f) }
            return
        }
        drawRect(Brush.verticalGradient(0f to pe.por(C.NIEVE, 0.3f).copy(alpha = 0f), 0.08f to pe.por(C.NIEVE, 0.3f), 1f to pe.por(C.NIEVE, 0.05f), startY = 228f, endY = ALTO), Offset(-10f, 228f), Size(410f, 70f))
        // La zanja abierta a pala, a lo largo del muro hasta la puerta y de ahí a la plaza: las losas mojadas al fondo y
        // las dos paredes de nieve, la de dentro en sombra azul y la de fuera con su filo de luz.
        val zanja = poligono(listOf(p(7.8f, 0f, 0.15f), p(13.4f, 0f, 0.15f), p(13.6f, 0f, 1.0f), p(7.8f, 0f, 1.25f)))
        drawPath(zanja, pe.por(lerp(C.PIEDRA, C.ESCORIA, 0.3f), -0.1f))
        clipPath(zanja) {
            var z = 7.8f
            while (z < 13.6f) { var f = 0.15f; while (f < 1.3f) { val c = p(z + 0.25f, 0f, f + 0.25f); drawPath(TrazosValle.blando(c, FachadaPosada.escala(z) * 0.2f, FachadaPosada.escala(z) * 0.05f, az, 7, 0.2f), pe.por(lerp(C.PIEDRA, C.TIERRA, 0.3f), az.entre(-0.2f, 0.2f))); f += 0.5f }; z += 0.5f }
            drawRect(lerp(v.l.bajo, C.LUNA, 0.3f).copy(alpha = 0.12f), Offset(150f, 220f), Size(240f, 70f))
        }
        val borde = (0..12).map { i -> val z = 7.8f + i * 0.48f; p(z, 0f, 1.25f - 0.02f * i) }
        drawPath(poligono(borde + borde.reversed().map { it + Offset(0f, -FachadaPosada.escala(10f) * 0.08f) }), pe.sombra(C.NIEVE_SOMBRA))
        drawPath(curva(borde.map { it + Offset(0f, -FachadaPosada.escala(10f) * 0.09f) }), pe.luz(C.NIEVE).copy(alpha = 0.8f), style = Stroke(1.4f))
        // Las huellas que bajan a la plaza.
        repeat(18) { val q = Offset(az.entre(220f, 300f), az.entre(244f, 262f)); drawOval(pe.sombra(C.NIEVE_SOMBRA).copy(alpha = 0.5f), Offset(q.x - 1.4f, q.y - 0.6f), Size(2.8f, 1.2f)) }
    }

    /** Con lluvia: el suelo más oscuro y charcos en las losas que devuelven el cielo y, de noche, la luz de la puerta. */
    private fun DrawScope.mojado(v: VistaAntiguas, az: Azar) {
        drawRect(C.ESCORIA.copy(alpha = 0.18f), Offset(-10f, 228f), Size(410f, 70f))
        for (k in 0..4) {
            val c = p(8.6f + k * 0.9f, 0f, az.entre(0.6f, 2.6f)); val w = az.entre(8f, 18f)
            val forma = TrazosValle.blando(c, w, w * 0.22f, az, 9, 0.2f)
            drawPath(forma, lerp(lerp(v.l.bajo, v.l.medio, 0.4f), C.ESCORIA, 0.35f).copy(alpha = 0.55f))
            drawPath(forma, lerp(v.l.alto, Color.White, 0.2f).copy(alpha = 0.25f), style = Stroke(0.6f))
            clipPath(forma) { if (v.encendido.sala * v.noche > 0.1f) reflejoRoto(c.x - w * 0.4f, c.x + w * 0.2f, c.y - 2f, 6f, C.LUMBRE, 0.6f * v.noche, az) }
        }
    }

    // ---------------------------------------------------------------- Las cosas

    /** Las cosas del primer plano, cada una con su silueta (para la sombra), dónde pisa y cómo se pinta. */
    private fun cosas(v: VistaAntiguas): List<Cosa> = buildList {
        val m = v.m
        add(lena(v))
        if (ObjetosAntiguas.elCuadroPinta(m, "montura") && m.ficha.objeto != "montura") add(caballete(v))
        add(banco(v))
        if (EscenaAntiguas.bastonEnLaPuerta(m) && m.ficha.objeto != "baston" && ObjetosAntiguas.elCuadroPinta(m, "baston")) add(baston(v, 10.7f, 0.25f, C.FRESNO))
        if (Senal.BASTON_NUEVO in v.senales) add(baston(v, 10.5f, 0.32f, lerp(C.FRESNO, C.NIEVE, 0.35f)))
        add(pala(v))
        add(cubo(v))
        if (EscenaAntiguas.cuna(m)) add(cuna(v))
        if (v.invierno) add(trineo(v))
        if (Senal.LENA in v.senales) add(haces(v))
        if (Senal.FARDO_DEL_ARPISTA in v.senales) add(fardo(v))
        val pieGrande = p(10.95f, 0f, 0.3f); val apoyo = p(10.85f, 1.1f, 0.02f)
        ObjetosAntiguas.grande(m, v.pincelEn(pieGrande, lumbre = v.encendido.sala * v.noche * 0.6f), pieGrande, apoyo, 1f)?.let { add(it) }
        val n = if (Senal.MULAS_DE_VUELTA in v.senales) 2 else EscenaAntiguas.mulas(m)
        if (n > 0) {
            val (pie, alto) = MulasAntiguas.pieYAlto(MulasAntiguas.MULAS[0])
            add(Cosa(MulasAntiguas.silueta(pie, alto), pie) { mulas(v, n, EscenaAntiguas.mulasCargadas(m)) })
        }
    }

    /** El montón de leña arrimado al pie del pino, bajo su tejadillo de losas: troncos partidos, apilados, con sus testas claras; en invierno, más alto y con su nieve. */
    private fun lena(v: VistaAntiguas): Cosa {
        val pie = Offset(80f, 286f); val alto = if (v.invierno) 58f else 52f; val ancho = 74f
        val forma = poligono(pie + Offset(-ancho / 2f, 0f), pie + Offset(ancho / 2f, 0f), pie + Offset(ancho / 2f - 2f, -alto), pie + Offset(-ancho / 2f + 4f, -alto - 4f))
        return Cosa(forma, pie, bajoElFollaje = true) {
            val pe = v.pincelEn(pie + Offset(0f, -26f)); val az = fijo("leña")
            drawPath(forma, pe.sombra(C.MADERA_OSC))
            var y = pie.y - 5f; var fila = 0
            while (y > pie.y - alto + 3f) {
                var x = pie.x - ancho / 2f + 1f + (fila % 2) * 3.4f
                while (x < pie.x + ancho / 2f - 4f) {
                    val r = az.entre(3.2f, 4.6f)
                    val c = Offset(x + r, y + az.entre(-0.6f, 0.6f))
                    drawCircle(pe.medio(C.MADERA_OSC), r + 0.6f, c)
                    drawCircle(pe.por(lerp(C.FRESNO, C.MADERA, az.entre(0.05f, 0.45f)), 0.45f), r, c)
                    drawCircle(pe.sombra(C.MADERA).copy(alpha = 0.4f), r * 0.55f, c, style = Stroke(0.4f))
                    drawLine(pe.sombra(C.MADERA).copy(alpha = 0.5f), c, c + Offset(r * 0.8f, -r * 0.3f), 0.4f)
                    x += r * 2f + 0.5f
                }
                y -= 8f; fila++
            }
            // El tejadillo de losas sobre dos palos.
            val t0 = pie + Offset(-ancho / 2f - 3f, -alto - 3f); val t1 = pie + Offset(ancho / 2f + 4f, -alto + 2f)
            drawPath(poligono(t0, t1, t1 + Offset(-2f, -6f), t0 + Offset(3f, -8f)), pe.por(C.LOSA, 0.35f))
            drawLine(pe.luz(lerp(C.LOSA, C.NIEVE, 0.3f)).copy(alpha = 0.6f), t0 + Offset(3f, -8f), t1 + Offset(-2f, -6f), 0.8f)
            drawLine(pe.sombra(C.ESCORIA), t0, t1, 1.2f)
            if (v.nieveAbajo > 0.2f) drawPath(poligono(t0 + Offset(2f, -7f), t1 + Offset(-2f, -5f), t1 + Offset(-3f, -5f - 5f * v.nieveAbajo), t0 + Offset(4f, -9f - 5f * v.nieveAbajo)), pe.por(C.NIEVE, 0.5f).copy(alpha = v.nieveAbajo))
        }
    }

    /** La montura de carga en su caballete: el caballete de dos tijeras y el madero, y encima la montura de haya forrada de fieltro, con su cuerda adujada. */
    private fun caballete(v: VistaAntiguas): Cosa {
        val pie = Offset(152f, 292f); val k = 1.55f
        val forma = poligono(pie + Offset(-22f * k, 0f), pie + Offset(22f * k, 0f), pie + Offset(20f * k, -40f * k), pie + Offset(-20f * k, -40f * k))
        return Cosa(forma, pie) { scale(k, k, pie) { dibujoCaballete(v, pie) } }
    }

    private fun DrawScope.dibujoCaballete(v: VistaAntiguas, pie: Offset) {
        run {
            val pe = v.pincelEn(pie + Offset(0f, -40f)); val madera = lerp(C.MADERA, C.FRESNO, 0.2f)
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
            if (v.nieveAbajo > 0.3f) drawLine(pe.por(C.NIEVE, 0.5f), pie + Offset(-15f, -40f), pie + Offset(14f, -40f), 2f, StrokeCap.Round)
        }
    }

    /** El banco de tablas, sacado al sol de la puerta: un tablón sobre cuatro patas abiertas, gastado donde se sientan. */
    private fun banco(v: VistaAntiguas): Cosa {
        val a0 = p(BANCO_Z, 0f, 0.65f); val a1 = p(BANCO_Z, 0f, 2.25f)
        val s0 = p(BANCO_Z, 0.46f, 0.65f); val s1 = p(BANCO_Z, 0.46f, 2.25f)
        val pie = (a0 + a1) / 2f
        val forma = poligono(a0, a1, s1 + Offset(0f, -2f), s0 + Offset(0f, -2f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie + Offset(0f, -10f), lumbre = v.encendido.sala * v.noche * 0.45f)
            val madera = lerp(C.MADERA, C.LANA, 0.18f)
            for (q in listOf(a0 + Offset(-3f, 0f), a1 + Offset(4f, 0f))) for (d in listOf(-2.4f, 2.4f)) drawLine(pe.por(madera, -0.3f), q + Offset(d, 0f), q + Offset(d * 0.4f, s0.y - a0.y + 2f), 1.8f)
            val tablon = poligono(s0 + Offset(-4f, 0f), s1 + Offset(5f, 0f), s1 + Offset(5f, -3.4f), s0 + Offset(-4f, -3.4f))
            drawPath(tablon, pe.por(madera, 0.35f))
            drawLine(pe.luz(lerp(madera, C.NIEVE, 0.3f)).copy(alpha = 0.6f), s0 + Offset(-4f, -3.4f), s1 + Offset(5f, -3.4f), 0.8f)
            drawLine(pe.sombra(madera), s0 + Offset(-4f, 0f), s1 + Offset(5f, 0f), 1f)
            if (v.nieveAbajo > 0.3f) drawPath(poligono(s0 + Offset(-4f, -3.4f), s1 + Offset(5f, -3.4f), s1 + Offset(4f, -3.4f - 3f * v.nieveAbajo), s0 + Offset(-3f, -3.4f - 3f * v.nieveAbajo)), pe.por(C.NIEVE, 0.5f))
        }
    }

    /** Un bastón de guía apoyado en el muro: de fresno, de su altura y un palmo más, con su punta de hierro y su correa. */
    private fun baston(v: VistaAntiguas, z: Float, fuera: Float, madera: Color): Cosa {
        val pie = p(z, 0f, fuera); val arriba = p(z - 0.1f, 1.78f, 0.03f)
        val forma = poligono(pie + Offset(-1.2f, 0f), pie + Offset(1.2f, 0f), arriba + Offset(1f, 0f), arriba + Offset(-1f, 0f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie + Offset(0f, -30f), lumbre = v.encendido.sala * v.noche * 0.6f)
            drawLine(pe.sombra(madera), pie + Offset(0.6f, 0f), arriba + Offset(0.6f, 0f), 2.2f, StrokeCap.Round)
            drawLine(pe.por(madera, 0.3f), pie, arriba, 1.6f, StrokeCap.Round)
            drawLine(pe.medio(C.ESCORIA), pie, pie + (arriba - pie) * 0.05f, 2f, StrokeCap.Round)
            val c = pie + (arriba - pie) * 0.8f
            drawOval(pe.medio(C.CUERO), Offset(c.x - 1.6f, c.y - 1f), Size(3.2f, 6f), style = Stroke(0.7f))
        }
    }

    /** La pala, apoyada en el muro junto a la puerta (en invierno, de madera ancha, clavada en la nieve). */
    private fun pala(v: VistaAntiguas): Cosa {
        val pie = p(9.25f, 0f, 0.42f); val arriba = p(9.2f, 1.45f, 0.02f)
        val forma = poligono(pie + Offset(-4f, 0f), pie + Offset(4f, 0f), arriba + Offset(1f, 0f), arriba + Offset(-1f, 0f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie + Offset(0f, -30f))
            drawLine(pe.por(C.FRESNO, 0.2f), pie + (arriba - pie) * 0.18f, arriba, 1.8f, StrokeCap.Round)
            val hoja = if (v.invierno) lerp(C.MADERA, C.FRESNO, 0.4f) else C.PIEDRA_OSC
            drawPath(poligono(pie + Offset(-4.4f, 0f), pie + Offset(4.6f, -1f), pie + (arriba - pie) * 0.2f + Offset(3f, 0f), pie + (arriba - pie) * 0.2f + Offset(-3f, 0f)), pe.por(hoja, 0.25f))
        }
    }

    /** El cubo de madera con sus aros de hierro, junto a la puerta. */
    private fun cubo(v: VistaAntiguas): Cosa {
        val pie = p(9.45f, 0f, 0.7f); val w = 8f; val h = 12f
        val forma = poligono(pie + Offset(-w / 2f, 0f), pie + Offset(w / 2f, 0f), pie + Offset(w / 2f + 1f, -h), pie + Offset(-w / 2f - 1f, -h))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie + Offset(0f, -6f), lumbre = v.encendido.sala * v.noche * 0.7f)
            drawPath(forma, Brush.horizontalGradient(listOf(pe.luz(C.MADERA), pe.sombra(C.MADERA)), pie.x - w / 2f, pie.x + w / 2f))
            for (y in listOf(-2.4f, -9.6f)) drawLine(pe.medio(C.ESCORIA), pie + Offset(-w / 2f - 0.4f, y), pie + Offset(w / 2f + 0.4f, y), 0.9f)
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
            drawPath(poligono(pie + Offset(-w / 2f, 0f), pie + Offset(w / 2f, 0f), pie + Offset(w / 2f, -h), pie + Offset(-w / 2f, -h)), pe.por(caja, 0.15f))
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
            val pe = v.pincelEn(pie)
            for (k in 0..3) drawPath(Path().apply { moveTo(pie + Offset(-14f, -1f - k * 1.4f)); cubicTo(pie + Offset(-6f, 1f - k * 1.4f), pie + Offset(6f, 1f - k * 1.4f), pie + Offset(14f, -3f - k * 1.4f)) }, pe.por(lerp(C.MADERA, C.FRESNO, 0.3f), 0.3f - k * 0.1f), style = Stroke(1.4f, cap = StrokeCap.Round))
            drawLine(pe.medio(C.ESCORIA), pie + Offset(-12f, -2f), pie + Offset(-12f, -6f), 0.7f); drawLine(pe.medio(C.ESCORIA), pie + Offset(12f, -3f), pie + Offset(12f, -7f), 0.7f)
            drawPath(Path().apply { moveTo(pie + Offset(-14f, -2f)); cubicTo(pie + Offset(-20f, -4f), pie + Offset(-22f, 2f), pie + Offset(-18f, 4f)) }, pe.medio(C.CUERO), style = Stroke(0.6f))
        }
    }

    /** Los haces de leña para los refugios, atados, esperando en las losas. */
    private fun haces(v: VistaAntiguas): Cosa {
        val pie = Offset(232f, 262f)
        val forma = poligono(pie + Offset(-18f, 0f), pie + Offset(18f, 0f), pie + Offset(16f, -10f), pie + Offset(-16f, -10f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie)
            for (h in 0..2) for (j in 0..5) { val y = pie.y - 2f - h * 3.2f - j * 0.3f; drawLine(pe.por(lerp(C.MADERA, C.FRESNO, 0.3f), 0.3f - j * 0.08f), Offset(pie.x - 16f + h * 2f, y), Offset(pie.x + 16f - h * 2f, y - 0.6f), 1.6f, StrokeCap.Round) }
            for (dx in listOf(-8f, 8f)) drawLine(pe.medio(C.CUERO), pie + Offset(dx, -0.4f), pie + Offset(dx, -9.4f), 0.9f)
        }
    }

    /** El fardo del arpista que llega: un bulto de lana y la funda de cuero del arpa, larga y triangular, apoyados en el banco. */
    private fun fardo(v: VistaAntiguas): Cosa {
        val pie = p(BANCO_Z - 0.4f, 0f, 2.2f)
        val forma = poligono(pie + Offset(-8f, 0f), pie + Offset(10f, 0f), pie + Offset(6f, -30f), pie + Offset(-2f, -32f))
        return Cosa(forma, pie) {
            val pe = v.pincelEn(pie)
            drawOval(pe.por(C.LANA, 0.2f), Offset(pie.x - 9f, pie.y - 9f), Size(14f, 9f))
            drawPath(poligono(pie + Offset(-1f, 0f), pie + Offset(9f, 0f), pie + Offset(5f, -30f), pie + Offset(0f, -32f)), pe.por(C.CUERO, 0.3f))
            drawLine(pe.luz(C.ORO).copy(alpha = 0.6f), pie + Offset(1f, -4f), pie + Offset(2f, -28f), 0.6f)
        }
    }
}
