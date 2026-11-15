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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.BATAYOLA_ALTO
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.BATAYOLA_DESDE
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.BATAYOLA_HASTA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.EJE
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.H
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.PALO_MAYOR
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.POPA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.REGALA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.RODA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_BITACORA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_BOTE
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_CANON
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_CLARABOYA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_PALO
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_TIMON
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.Vista
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.Z_CANON
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.babor
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.enLienzo
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.estribor
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.k
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * La cubierta del bergantín ([BarcoBotanico]): las tablas de pino lavadas con arena, con sus costuras de brea, sus topes
 * y sus tapines; las dos amuradas por dentro (la de estribor es la diagonal grande del cuadro: tablazón pintada, con sus
 * barraganetes, el trancanil y sus imbornales, la porta abierta del cañón, el cabillero con sus adujas, los baldes de
 * incendio, el botalón de respeto trincado, la batayola con los coyes, las chorreras de sal y de óxido); la capa de
 * sombras de la cubierta (la de las velas, la de la amurada, la reja de la jarcia y la de cada cosa); y lo que va en
 * crujía, de proa a popa: los botes, el palo mayor con su cabillero y sus obenques, la claraboya, el cañón en su porta,
 * la bitácora y la rueda.
 */
internal object CubiertaBarco {
    private val C = ColorBotanico

    /** La pintura de dentro de las amuradas: un ocre gastado, que deja ver la madera donde roza **[Diseño propio]**. */
    private val PINTURA = lerp(C.ARENA, C.CUERO, 0.24f)

    /** La cubierta, de la popa visible a la roda (en el lienzo). */
    fun formaDeLaCubierta(): Path {
        val zs = (0..50).map { POPA + (RODA - POPA) * it / 50f }
        return poligono(zs.map { pc(estribor(it), 0f, it) } + zs.reversed().map { pc(babor(it), 0f, it) })
    }

    /** La cara de dentro de la amurada de estribor (en el lienzo). */
    fun caraDeEstribor(): Path {
        val zs = (0..44).map { POPA + (RODA - POPA) * it / 44f }
        return poligono(zs.map { pc(estribor(it), 0f, it) } + zs.reversed().map { pc(estribor(it), REGALA, it) })
    }

    fun DrawScope.cubierta(v: Vista) {
        tablas(v)
        amuradaDeBabor(v)
        amuradaDeEstribor(v)
        sombras(v)
        crujia(v)
    }

    // ================================================================ Las tablas

    private fun DrawScope.tablas(v: Vista) {
        val p = v.p; val m = v.m
        val forma = formaDeLaCubierta()
        val teca = C.TECA; val mojada = EscenaBotanico.cubiertaMojada(m); val nieve = v.nieve
        drawPath(forma, Brush.verticalGradient(0f to p.arriba(lerp(teca, C.ROCA, 0.15f), 0.4f), 0.22f to p.arriba(teca, 0.12f), 1f to p.arriba(lerp(teca, C.ARENA, 0.18f)), startY = H + 4f, endY = ALTO))
        val az = Azar(Semilla.de("botanico", "cubierta"))
        clipPath(forma) {
            // Cada tabla con su tono, de popa a proa, y la costura de brea entre ellas, cada vez más juntas hacia el fondo.
            var x = babor(3f) + 0.02f; var i = 0
            while (x < estribor(3f)) {
                val tono = lerp(teca, if (az.real() > 0.5f) C.ARENA else C.MADERA, az.entre(0.02f, 0.16f))
                drawPath(poligono(pc(x, 0f, POPA), pc(x + 0.15f, 0f, POPA), pc(x + 0.15f, 0f, RODA), pc(x, 0f, RODA)), Brush.verticalGradient(listOf(p.arriba(tono, 0.3f).copy(alpha = 0.55f), p.arriba(tono).copy(alpha = 0.55f)), H + 6f, ALTO))
                drawLine(p.sombra(lerp(teca, C.BREA, 0.65f)).copy(alpha = 0.7f), pc(x, 0f, POPA), pc(x, 0f, RODA), 0.55f)
                drawLine(p.luz(lerp(teca, C.LONA, 0.3f)).copy(alpha = 0.35f), pc(x + 0.012f, 0f, POPA), pc(x + 0.012f, 0f, 12f), 0.4f)
                // Los topes (las juntas de cabeza) y, en cada bao, los tapines que tapan los pernos.
                var z = POPA + az.entre(0f, 5f)
                while (z < RODA) { drawLine(p.sombra(lerp(teca, C.BREA, 0.55f)).copy(alpha = 0.6f), pc(x, 0f, z), pc(x + 0.15f, 0f, z), (k(z) * 0.012f).coerceIn(0.3f, 1.4f)); z += az.entre(4.5f, 6.5f) }
                var zb = POPA + 0.25f
                while (zb < 9f) { val q = pc(x + 0.075f, 0f, zb); val r = (k(zb) * 0.013f).coerceIn(0.25f, 1.5f); drawOval(p.arriba(lerp(teca, C.MADERA, 0.35f)), Offset(q.x - r, q.y - r * 0.35f), Size(r * 2f, r * 0.7f)); zb += 0.62f }
                x += 0.15f; i++
            }
            // El desgaste: donde más se pisa (junto a la rueda, al pie del palo, el paso de la mesa), más claro.
            mancha(pc(-1.3f, 0f, 3.9f), 70f, 14f, p.arriba(lerp(teca, C.LONA, 0.4f)), 0.28f)
            mancha(pc(EJE + 0.8f, 0f, PALO_MAYOR - 0.6f), 30f, 4f, p.arriba(lerp(teca, C.LONA, 0.35f)), 0.3f)
            // Las manchas de brea, de agua y de lo que cae del trabajo; las vetas.
            repeat(20) { val q = pc(az.entre(-5f, 1f), 0f, az.entre(2.5f, 16f)); mancha(q, az.entre(3f, 12f) * (k(4f) / 75f), az.entre(1f, 2.6f), p.sombra(lerp(teca, C.BREA, 0.35f)), 0.16f) }
            repeat(140) { val xx = az.entre(-6f, 1.4f); val zz = az.entre(POPA, 9f); val q = pc(xx, 0f, zz); val w = k(zz) * az.entre(0.05f, 0.22f); drawLine(p.sombra(teca).copy(alpha = 0.18f), q, q + Offset(0f, -w * 0.02f) + (pc(xx, 0f, zz + 0.4f) - q) * 0.6f, (k(zz) * 0.004f).coerceIn(0.25f, 0.7f)) }
            // La brea derretida que chorrea de las costuras al sol, en gotas.
            repeat(30) { val q = pc(az.entre(-4f, 1f), 0f, az.entre(2.5f, 8f)); drawCircle(p.sombra(C.BREA).copy(alpha = 0.35f), (k(4f) * 0.01f).coerceIn(0.4f, 1f), q) }
            // Mojada (el baldeo del alba, la lluvia): más oscura, con el cielo reflejado en las tablas y unos charcos.
            if (mojada) {
                drawPath(forma, p.sombra(lerp(teca, C.MADERA_OSC, 0.45f)).copy(alpha = 0.42f))
                val cielo = lerp(v.l.medio, Color.White, 0.15f)
                repeat(9) { val q = pc(az.entre(-4.5f, 1f), 0f, az.entre(2.6f, 12f)); mancha(q, az.entre(14f, 36f), az.entre(1.5f, 4.5f), cielo, 0.3f * (1f - 0.6f * v.l.noche)) }
                for (j in 0 until 5) { val z = az.entre(2.6f, 7f); val xx = az.entre(-4f, 0.8f); drawPath(Path().apply { moveTo(pc(xx, 0f, z)); lineTo(pc(xx + az.entre(0.3f, 0.9f), 0f, z + az.entre(-0.1f, 0.1f))) }, cielo.copy(alpha = 0.35f), style = Stroke((k(z) * 0.02f).coerceIn(0.5f, 2f), cap = StrokeCap.Round)) }
            }
            if (nieve > 0.02f) {
                drawPath(forma, p.arriba(C.NIEVE).copy(alpha = 0.78f * nieve))
                repeat(26) { val q = pc(az.entre(-5f, 1f), 0f, az.entre(2.5f, 14f)); mancha(q, az.entre(6f, 22f), az.entre(1f, 3f), p.sombra(C.NIEVE), 0.3f * nieve) }
                // Las pisadas por donde se pasa, más oscuras.
                repeat(14) { val z = az.entre(2.8f, 9f); val q = pc(-1.2f + 0.5f * sin(z * 2f), 0f, z); drawOval(p.sombra(lerp(C.NIEVE, C.TECA, 0.4f)).copy(alpha = 0.4f), Offset(q.x - k(z) * 0.06f, q.y - k(z) * 0.02f), Size(k(z) * 0.12f, k(z) * 0.04f)) }
            }
            if (EscenaBotanico.escarcha(m) && nieve < 0.02f) drawPath(forma, Brush.verticalGradient(listOf(p.luz(C.NIEVE).copy(alpha = 0.12f), p.luz(C.NIEVE).copy(alpha = 0.32f)), H, ALTO))
        }
    }

    // ================================================================ Las amuradas

    /** La amurada de babor, lejos: se ve por dentro desde el palo mayor hasta la proa, con la regala y la batayola. */
    private fun DrawScope.amuradaDeBabor(v: Vista) {
        val p = v.p; val sol = v.sol
        val zs = (0..30).map { 6f + (RODA - 6f) * it / 30f }
        val cara = poligono(zs.map { pc(babor(it), 0f, it) } + zs.reversed().map { pc(babor(it), REGALA, it) })
        val t = sol.t(V3(1f, 0f, 0f))
        drawPath(cara, p.por(PINTURA, t, 0.15f))
        clipPath(cara) {
            var z = 6.5f; while (z < RODA) { drawLine(p.sombra(PINTURA, 0.15f).copy(alpha = 0.5f), pc(babor(z), 0f, z), pc(babor(z), REGALA, z), (k(z) * 0.1f).coerceIn(0.4f, 3f)); z += 1.1f }
            for (h in listOf(0.3f, 0.6f)) drawPath(Path().apply { moveTo(pc(babor(zs[0]), h, zs[0])); for (z2 in zs.drop(1)) lineTo(pc(babor(z2), h, z2)) }, p.sombra(PINTURA, 0.15f).copy(alpha = 0.3f), style = Stroke(0.4f))
        }
        drawPath(Path().apply { moveTo(pc(babor(zs[0]), REGALA, zs[0])); for (z in zs.drop(1)) lineTo(pc(babor(z), REGALA, z)) }, p.medio(C.MADERA_OSC, 0.15f), style = Stroke(1.6f))
        // La batayola de babor, con los coyes: una fila de bultos claros sobre la regala.
        val az = Azar(Semilla.de("botanico", "coyes babor"))
        var z = 7f
        while (z < 16f) { val a = pc(babor(z), REGALA, z); val b = pc(babor(z + 0.7f), REGALA + 0.32f, z + 0.7f); drawOval(p.por(lerp(C.LONA, C.ARENA, az.entre(0f, 0.3f)), 0.2f, 0.15f), Offset(minOf(a.x, b.x), b.y), Size(kotlin.math.abs(b.x - a.x) + 1f, a.y - b.y)); z += 0.75f }
    }

    /**
     * La amurada de estribor por dentro: la diagonal grande del cuadro. Las tracas pintadas de color piedra con su
     * desgaste, los barraganetes con su cara de luz, el trancanil y sus imbornales con el verdín, la porta abierta del
     * cañón (por ella se ve el mar), el cabillero con sus adujas, los baldes de incendio, el botalón de respeto, las
     * chorreras de sal y de óxido, la regala y la batayola con los coyes.
     */
    private fun DrawScope.amuradaDeEstribor(v: Vista) {
        val p = v.p; val sol = v.sol; val m = v.m
        val zs = (0..44).map { POPA + (RODA - POPA) * it / 44f }
        val pie = zs.map { pc(estribor(it), 0f, it) }; val arriba = zs.map { pc(estribor(it), REGALA, it) }
        val cara = caraDeEstribor()
        // La cara de dentro: de espaldas al sol de la mañana (penumbra violeta), al sol de la tarde (cálida); abajo, el rebote de la cubierta.
        val t = sol.t(V3(-1f, 0f, 0f)).coerceAtLeast(-0.45f)
        drawPath(cara, Brush.verticalGradient(listOf(p.por(PINTURA, t + 0.08f), p.por(lerp(PINTURA, C.TECA, 0.3f), t + 0.25f), lerp(p.por(lerp(PINTURA, C.TECA, 0.5f), t + 0.45f), p.luz(lerp(C.TECA, C.ARENA, 0.4f)), 0.35f * v.r.dia)), H, ALTO))
        val az = Azar(Semilla.de("botanico", "amurada"))
        clipPath(cara) {
            // Las tracas: cada una con su tono, la junta oscura y el canto de luz debajo.
            val alturas = listOf(0f, 0.22f, 0.43f, 0.64f, 0.85f, REGALA)
            for (j in 0 until alturas.size - 1) {
                val h0 = alturas[j]; val h1 = alturas[j + 1]
                val tono = lerp(PINTURA, if (j % 2 == 0) C.LONA else C.MADERA, az.entre(0.04f, 0.12f))
                drawPath(poligono(zs.map { pc(estribor(it), h0, it) } + zs.reversed().map { pc(estribor(it), h1, it) }), p.por(tono, t + 0.05f + 0.06f * j).copy(alpha = 0.6f))
                if (j > 0) {
                    drawPath(Path().apply { moveTo(pc(estribor(zs[0]), h0, zs[0])); for (z in zs.drop(1)) lineTo(pc(estribor(z), h0, z)) }, p.sombra(lerp(PINTURA, C.BREA, 0.3f)).copy(alpha = 0.55f), style = Stroke(0.7f))
                    drawPath(Path().apply { moveTo(pc(estribor(zs[0]), h0 - 0.02f, zs[0])); for (z in zs.drop(1)) lineTo(pc(estribor(z), h0 - 0.02f, z)) }, p.luz(PINTURA).copy(alpha = 0.3f), style = Stroke(0.45f))
                }
            }
            // El desgaste de la pintura: donde rozan los cabos y las cajas, asoma la madera.
            repeat(26) { val zz = az.entre(1.8f, 13f); val q = pc(estribor(zz), az.entre(0.15f, 0.95f), zz); val e = k(zz); val w = e * az.entre(0.05f, 0.2f); val h = e * az.entre(0.02f, 0.06f)
                drawPath(poligono(listOf(q + Offset(-w, 0f), q + Offset(-w * 0.4f, -h), q + Offset(w * 0.5f, -h * 0.8f), q + Offset(w, h * 0.2f), q + Offset(w * 0.2f, h), q + Offset(-w * 0.6f, h * 0.7f))), p.por(lerp(C.MADERA, C.TECA, 0.35f), t).copy(alpha = 0.55f)) }
            // Las chorreras de sal (blancas, de la regala abajo) y de óxido (de cada perno).
            repeat(18) { val zz = az.entre(2f, 15f); val q = pc(estribor(zz), REGALA - 0.04f, zz); drawLine(p.luz(lerp(C.LONA, Color.White, 0.4f)).copy(alpha = 0.28f), q, q + Offset(az.entre(-0.3f, 0.3f), k(zz) * az.entre(0.15f, 0.55f)), (k(zz) * az.entre(0.01f, 0.03f)).coerceIn(0.4f, 2.4f), StrokeCap.Round) }
            repeat(12) { val zz = az.entre(2.2f, 14f); val h = az.entre(0.3f, 0.9f); val q = pc(estribor(zz), h, zz); val e = k(zz)
                drawCircle(p.sombra(C.HIERRO), (e * 0.012f).coerceIn(0.4f, 1.5f), q); drawLine(p.medio(C.CUERO).copy(alpha = 0.45f), q, q + Offset(0f, e * az.entre(0.08f, 0.3f)), (e * 0.012f).coerceIn(0.4f, 1.6f), StrokeCap.Round) }
            // Los barraganetes, cada metro: la cara que mira a popa clara, la de proa en sombra, y la sombra que echan en las tablas.
            var z = 2f
            while (z < RODA - 1f) {
                val w = 0.13f; val fondo = 0.13f
                val a = pc(estribor(z), 0f, z); val b = pc(estribor(z), REGALA, z)
                val a1 = pc(estribor(z) - fondo, 0f, z); val b1 = pc(estribor(z) - fondo, REGALA, z)
                val a2 = pc(estribor(z + w) - fondo, 0f, z + w); val b2 = pc(estribor(z + w) - fondo, REGALA, z + w)
                val sh = (0.25f + 0.35f * v.r.dia) * (if (sol.sx > 0f) 1f else 0.5f)
                drawPath(poligono(a2, a2 + (pc(estribor(z + w + 0.3f), 0f, z + w + 0.3f) - a2), b2 + (pc(estribor(z + w + 0.3f), REGALA, z + w + 0.3f) - b2), b2), p.sombra(PINTURA).copy(alpha = sh))
                drawPath(poligono(a, a1, b1, b), p.por(lerp(PINTURA, C.LONA, 0.3f), t + 0.55f))
                drawPath(poligono(a1, a2, b2, b1), p.por(PINTURA, t + 0.12f))
                drawLine(p.sombra(lerp(PINTURA, C.BREA, 0.3f)).copy(alpha = 0.6f), a2, b2, (k(z) * 0.012f).coerceIn(0.3f, 1.6f))
                z += 1.05f
            }
            // El trancanil, oscuro, con los imbornales y el verdín que deja el agua al escurrir.
            drawPath(poligono(pie + zs.reversed().map { pc(estribor(it), 0.1f, it) }), p.sombra(lerp(PINTURA, C.BREA, 0.5f)))
            for (zz in listOf(3.2f, 6.1f, 9.9f, 13.2f)) {
                val q = pc(estribor(zz), 0.05f, zz); val e = k(zz)
                drawOval(p.sombra(C.BREA), Offset(q.x - e * 0.08f, q.y - e * 0.05f), Size(e * 0.16f, e * 0.06f))
                mancha(q + Offset(0f, -e * 0.08f), e * 0.12f, e * 0.08f, p.medio(lerp(C.SELVA, PINTURA, 0.4f)), 0.4f)
            }
            // La porta del cañón, abierta: por ella se ve el mar; el marco, de madera clara.
            val z0 = Z_CANON - 0.45f; val z1 = Z_CANON + 0.45f
            val q0 = pc(estribor(z0), 0.3f, z0); val q1 = pc(estribor(z1), 0.3f, z1); val q2 = pc(estribor(z1), 0.88f, z1); val q3 = pc(estribor(z0), 0.88f, z0)
            val hueco = poligono(q0, q1, q2, q3)
            drawPath(hueco, Brush.verticalGradient(listOf(lerp(v.l.tono(MundoBarco.aguaDe(EscenaBotanico.costa(m)).first), v.l.bajo, 0.25f), v.l.tono(MundoBarco.aguaDe(EscenaBotanico.costa(m)).second)), q3.y, q0.y))
            drawPath(hueco, p.sombra(C.BREA).copy(alpha = 0.6f), style = Stroke(1.2f))
            drawLine(p.luz(lerp(PINTURA, C.LONA, 0.4f)), q0, q1, 1f); drawLine(p.por(C.TEJA, t), q3, q2, 1.2f)
        }
        // Con tormenta, los rociones que saltan por encima de la regala.
        if (EscenaBotanico.tormenta(m)) { val rz = m.azar("rociones"); repeat(9) { val zz = rz.entre(5f, 16f); val q = pc(estribor(zz) + 0.3f, REGALA + rz.entre(0.3f, 1.4f), zz); mancha(q, k(zz) * rz.entre(0.4f, 0.9f), k(zz) * rz.entre(0.2f, 0.45f), v.l.tono(Color.White), 0.45f); drawPoints((0 until 12).map { q + Offset(rz.entre(-1f, 1f) * k(zz) * 0.6f, rz.entre(-1f, 0.6f) * k(zz) * 0.3f) }, PointMode.Points, v.l.tono(Color.White).copy(alpha = 0.7f), 1.1f, StrokeCap.Round) } }
        // La regala: el canto de arriba, de madera, con su cara al cielo y su filo de luz.
        val fuera = zs.map { pc(estribor(it) + 0.28f, REGALA + 0.03f, it) }; val dentro = zs.map { pc(estribor(it) - 0.05f, REGALA + 0.03f, it) }
        drawPath(poligono(dentro + fuera.reversed()), p.arriba(lerp(C.MADERA, C.TECA, 0.35f)))
        drawPath(poligono(arriba + dentro.reversed()), p.por(C.MADERA_OSC, t))
        drawPath(Path().apply { moveTo(fuera[0]); for (q in fuera.drop(1)) lineTo(q) }, p.luz(lerp(C.MADERA, C.LONA, 0.35f)).copy(alpha = 0.65f), style = Stroke(0.9f))
        if (v.nieve > 0.02f || EscenaBotanico.escarcha(m)) drawPath(poligono(dentro + fuera.reversed()), p.luz(C.NIEVE).copy(alpha = if (v.nieve > 0.02f) 0.85f else 0.4f))
        with(PiezasBotanico) {
            // El cabillero de la banda, junto al palo mayor: cabillas con sus adujas colgadas, y los cabos que suben.
            for (zz in listOf(5.0f, 5.4f, 5.8f, 6.2f, 10.1f, 10.7f, 11.3f, 12.6f, 13.2f)) {
                val q = pc(estribor(zz) - 0.06f, 0.74f, zz); val e = k(zz)
                drawLine(p.sombra(C.MADERA_OSC), q + Offset(0f, -e * 0.09f), q + Offset(0f, e * 0.1f), (e * 0.03f).coerceAtLeast(0.4f))
                drawCircle(p.luz(C.MADERA), (e * 0.018f).coerceAtLeast(0.4f), q + Offset(0f, -e * 0.09f))
                for (j in 0..2) drawOval(p.por(C.CANAMO, if (j == 1) 0.3f else -0.2f), Offset(q.x - e * (0.07f + 0.015f * j), q.y), Size(e * (0.14f + 0.03f * j), e * (0.36f + 0.04f * j)), style = Stroke((e * 0.016f).coerceIn(0.3f, 1.6f)))
            }
            // Los baldes de incendio, de cuero, colgados de la batayola en fila (cada uno un poco distinto).
            for ((i, zz) in listOf(6.75f, 7.2f, 7.65f).withIndex()) {
                val q = pc(estribor(zz) - 0.08f, 0.78f, zz); val e = k(zz); val w = e * 0.12f; val h = e * 0.2f; val lado = if (p.solALaDerecha) 1f else -1f
                val forma = poligono(Offset(q.x - w, q.y), Offset(q.x + w, q.y), Offset(q.x + w * 0.82f, q.y + h), Offset(q.x - w * 0.82f, q.y + h))
                drawPath(forma, Brush.horizontalGradient(listOf(p.sombra(lerp(C.CUERO, C.BREA, 0.2f)), p.medio(C.CUERO), p.luz(lerp(C.CUERO, C.ARENA, 0.2f))), q.x - lado * w, q.x + lado * w))
                drawLine(p.luz(C.LATON).copy(alpha = 0.7f), Offset(q.x - w, q.y + h * 0.2f), Offset(q.x + w, q.y + h * 0.2f), (e * 0.01f).coerceAtLeast(0.4f))
                drawRect(p.medio(lerp(C.TEJA, C.CUERO, 0.3f)), Offset(q.x - w * 0.4f, q.y + h * 0.4f), Size(w * 0.8f, h * 0.25f))
                drawPath(Path().apply { moveTo(Offset(q.x - w * 0.9f, q.y)); cubicTo(Offset(q.x - w, q.y - h * 0.6f), Offset(q.x + w, q.y - h * 0.6f), Offset(q.x + w * 0.9f, q.y)) }, p.sombra(C.CANAMO), style = Stroke((e * 0.012f).coerceAtLeast(0.4f)))
                if (i == 1) drawLine(p.sombra(C.BREA).copy(alpha = 0.4f), Offset(q.x - w * 0.82f, q.y + h), Offset(q.x + w * 0.82f, q.y + h), 0.6f)
            }
            // El botalón de respeto, trincado a lo largo de la amurada, bajo.
            val b0 = pc(estribor(6.6f) - 0.12f, 0.2f, 6.6f); val b1 = pc(estribor(14.5f) - 0.12f, 0.24f, 14.5f)
            palito(p, b0, b1, k(6.6f) * 0.13f, k(14.5f) * 0.11f, lerp(C.MADERA, C.ARENA, 0.3f), 0f, listOf(0.15f, 0.6f))
            for (zz in listOf(7.5f, 12f)) { val q = pc(estribor(zz) - 0.12f, 0.22f, zz); drawLine(p.sombra(C.CANAMO), q + Offset(0f, -k(zz) * 0.08f), q + Offset(0f, k(zz) * 0.08f), (k(zz) * 0.02f).coerceAtLeast(0.5f)) }
        }
        batayola(v)
    }

    /** La batayola de estribor: los candeleros de hierro sobre la regala, la red y, dentro, los coyes enrollados, tapados a medias por su encerado. */
    private fun DrawScope.batayola(v: Vista) {
        val p = v.p; val az = Azar(Semilla.de("botanico", "batayola"))
        val zs = (0..24).map { BATAYOLA_DESDE + (BATAYOLA_HASTA - BATAYOLA_DESDE) * it / 24f }
        val h0 = REGALA + 0.03f; val h1 = REGALA + BATAYOLA_ALTO
        // Los coyes: rollos de lona apretados, de distinto blanco, en fila, con su lado de luz arriba.
        var z = BATAYOLA_DESDE + 0.05f
        while (z < BATAYOLA_HASTA - 0.2f) {
            val lz = az.entre(0.42f, 0.6f); val a = pc(estribor(z) + 0.1f, h0, z); val b = pc(estribor(z + lz) + 0.1f, h1 - az.entre(0f, 0.05f), z + lz)
            val tono = lerp(C.LONA, if (az.real() > 0.5f) C.ARENA else C.ROCA, az.entre(0.05f, 0.3f))
            val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(minOf(a.x, b.x), b.y, maxOf(a.x, b.x) + 1f, a.y, (a.y - b.y) * 0.45f, (a.y - b.y) * 0.45f)) }
            drawPath(forma, Brush.verticalGradient(listOf(p.luz(tono), p.medio(tono), p.sombra(tono)), b.y, a.y))
            drawLine(p.sombra(C.CANAMO), Offset((a.x + b.x) / 2f, b.y), Offset((a.x + b.x) / 2f, a.y), (k(z) * 0.012f).coerceAtLeast(0.35f))
            z += lz + az.entre(0.02f, 0.08f)
        }
        // El encerado que los tapa a medias: una lona más oscura, atada.
        val tapa = poligono(zs.filter { it < 10.5f }.map { pc(estribor(it) + 0.1f, h1 + 0.02f, it) } + zs.filter { it < 10.5f }.reversed().map { pc(estribor(it) + 0.1f, h1 - 0.16f, it) })
        drawPath(tapa, p.por(lerp(C.LONA, C.ROCA, 0.45f), 0.25f))
        // La red y los candeleros.
        for (zz in zs.filterIndexed { i, _ -> i % 3 == 0 }) { val a = pc(estribor(zz) + 0.14f, REGALA, zz); val b = pc(estribor(zz) + 0.14f, h1 + 0.04f, zz); drawLine(p.sombra(C.HIERRO), a, b, (k(zz) * 0.02f).coerceIn(0.4f, 2f)); drawLine(p.luz(C.HIERRO).copy(alpha = 0.5f), a + Offset(-0.3f, 0f), b + Offset(-0.3f, 0f), 0.3f) }
        drawPath(Path().apply { moveTo(pc(estribor(zs[0]) + 0.14f, h1 + 0.04f, zs[0])); for (zz in zs.drop(1)) lineTo(pc(estribor(zz) + 0.14f, h1 + 0.04f, zz)) }, p.sombra(C.HIERRO), style = Stroke(0.8f))
    }

    // ================================================================ La capa de sombras de la cubierta

    /**
     * Una sola capa con todas las sombras de la cubierta: la de las velas (de color, más clara y tibia, porque la lona deja
     * pasar luz), la de la amurada que da la espalda al sol, la reja de la jarcia y la de cada cosa de crujía y del
     * taller; y, con el sol de babor, la de las velas y la jarcia que sube por la amurada de estribor.
     */
    private fun DrawScope.sombras(v: Vista) {
        if (v.r.fuerza < 0.03f) return
        val sol = v.sol
        val forma = formaDeLaCubierta(); val cara = caraDeEstribor()
        capaSombra(v.r) { c ->
            clipPath(forma) {
                val tibia = lerp(c, Color(0xFFF2D9B8), 0.2f)
                for (q in v.manchas) { val ps = enLienzo(q) ?: continue; drawPath(ps, tibia); drawPath(ps, tibia.copy(alpha = 0.5f), style = Stroke(2.4f)) }
                // La amurada de estribor echa la suya (más oscura que la de la lona).
                for (lado in listOf(true, false)) BarcoBotanico.sombraDeLaAmurada(sol, lado)?.let { q -> enLienzo(q)?.let { sombra(it, c) } }
                // La reja de la jarcia: cada obenque, brandal y estay deja su raya.
                for ((a0, b0) in VelasBarco.sombraDeLaJarcia(sol)) {
                    var a = a0; var b = b0
                    if (a.y < POPA && b.y < POPA) continue
                    if (a.y < POPA) a = a + (b - a) * ((POPA - a.y) / (b.y - a.y))
                    if (b.y < POPA) b = b + (a - b) * ((POPA - b.y) / (a.y - b.y))
                    val z = ((a.y + b.y) / 2f).coerceIn(2f, 30f)
                    drawLine(c, pc(a.x, 0f, a.y), pc(b.x, 0f, b.y), (k(z) * 0.035f).coerceIn(0.5f, 3f), StrokeCap.Round)
                }
                // Lo de crujía y el taller.
                for (s in sombrasDeCrujia(v)) sombra(s, c)
                for (s in TallerBarco.sombras(v)) sombra(s, c)
            }
            // Con el sol de babor, la sombra de las velas sube por la amurada de estribor.
            if (sol.sx > 0.1f) clipPath(cara) {
                val tibia = lerp(c, Color(0xFFF2D9B8), 0.2f)
                for (vela in VelasBarco.velasQueDanSombra(v.m)) {
                    val q = vela.map { a -> val xb = estribor(a.z.coerceIn(2f, 15f)); val s = (xb - a.x) / sol.sx; V3(xb, a.y - s, a.z + sol.sz * s) }
                    if (q.any { it.y > 6f || it.y < -4f }) continue
                    drawPath(poligono(q.map { pc(it) }), tibia)
                }
            }
        }
    }

    // ================================================================ Lo que va en crujía

    private fun bote(v: Vista) = PiezasBotanico.Bote(SITIO_BOTE, Semilla.de("botanico", "bote"), pintura = lerp(C.LONA, C.ARENA, 0.2f), nieve = v.nieve)
    private fun claraboya(v: Vista) = PiezasBotanico.Claraboya(SITIO_CLARABOYA, Semilla.de("botanico", "claraboya"), luz = EscenaBotanico.luzDeLaCabina(v.m), nieve = v.nieve)
    private fun canon(v: Vista) = PiezasBotanico.Canon(SITIO_CANON, Semilla.de("botanico", "canon"), nieve = v.nieve)
    private fun laBitacora(v: Vista) = PiezasBotanico.Bitacora(SITIO_BITACORA, Semilla.de("botanico", "bitacora"), lampara = v.lu.bitacora, nieve = v.nieve)
    private fun elTimon(v: Vista) = PiezasBotanico.Timon(SITIO_TIMON, Semilla.de("botanico", "timon"), nieve = v.nieve)
    private fun palo(v: Vista) = PiezasBotanico.Palo(SITIO_PALO, Semilla.de("botanico", "palo mayor"), 0.28f, 16f, nieve = v.nieve)

    private fun sombrasDeCrujia(v: Vista): List<Path> {
        val sol = v.sol
        return listOf(
            PiezasBotanico.sombraDePalo(PiezasBotanico.Palo(BarcoBotanico.SITIO_TRINQUETE, 0L), sol, 4f),
            PiezasBotanico.sombraDeBote(bote(v), sol),
            PiezasBotanico.sombraDePalo(palo(v), sol, 6f),
            PiezasBotanico.sombraDeClaraboya(claraboya(v), sol),
            PiezasBotanico.sombraDeCanon(canon(v), sol),
            PiezasBotanico.sombraDeBitacora(laBitacora(v), sol),
            PiezasBotanico.sombraDeTimon(elTimon(v), sol),
        )
    }

    /** Lo de crujía, de proa a popa; la bitácora y la rueda van con el taller (se tapan con la mesa: [TallerBarco]). */
    private fun DrawScope.crujia(v: Vista) {
        val p = v.p
        with(PiezasBotanico) {
            val (pb, sb) = v.pincel(EJE, 15.6f, 0.8f); bote(bote(v), pb, sb)
            // Unas adujas a la holandesa al pie del palo y un barril de agua trincado.
            adujas(v, listOf(V3(EJE + 0.9f, 0f, 12.6f), V3(EJE - 1.2f, 0f, 10.6f), V3(EJE + 1.4f, 0f, 14.2f)))
            val mayor = palo(v)
            obenques(mayor, p, -3.65f, 1.25f, 15.6f, -1.4f, 1.4f, 5, v.nieve, 0.15f)
            val (pm, sm) = v.pincel(EJE, PALO_MAYOR, 1.2f)
            palo(mayor, pm, sm)
            val (pk, sk) = v.pincel(estribor(Z_CANON) - 1.12f, Z_CANON, 0.5f)
            canon(canon(v), pk, sk)
            val (pc0, sc0) = v.pincel(EJE, 8.6f, 0.5f)
            claraboya(claraboya(v), pc0, sc0)
            // Los obenques de estribor del palo mayor, por delante de todo lo de crujía: suben de la mesa de guarnición.
            obenques(mayor, p, 3.7f, 1.25f, 15.6f, -1.4f, 1.4f, 5, v.nieve, 0f)
        }
    }

    /** La bitácora (la pinta el taller en su turno, para que la mesa la tape). */
    internal fun DrawScope.bitacora(v: Vista) {
        val (pb, sb) = v.pincel(-0.8f, 5.5f, 1f)
        with(PiezasBotanico) { bitacora(laBitacora(v), pb, sb) }
    }

    /** La rueda del timón (la pinta el taller en su turno). */
    internal fun DrawScope.rueda(v: Vista) {
        val (pt, st) = v.pincel(EJE + 0.4f, 4.4f, 1.1f)
        with(PiezasBotanico) { timon(elTimon(v), pt, st) }
    }

    /** Unas adujas de cabo en la cubierta, a la holandesa (en espiral), cada una con su luz. */
    internal fun DrawScope.adujas(v: Vista, donde: List<V3>) {
        val p = v.p
        for ((i, a) in donde.withIndex()) {
            val c = pc(a); val e = k(a.z)
            for (j in 0..6) { val r = e * (0.32f - 0.038f * j) * (1f - 0.05f * i); drawOval(p.por(C.CANAMO, if (j % 2 == 0) 0.4f else -0.15f), Offset(c.x - r, c.y - r * 0.26f), Size(r * 2f, r * 0.52f), style = Stroke((e * 0.032f).coerceIn(0.4f, 3f))) }
            drawCircle(p.sombra(C.CANAMO), (e * 0.03f).coerceAtLeast(0.4f), c)
        }
    }
}
