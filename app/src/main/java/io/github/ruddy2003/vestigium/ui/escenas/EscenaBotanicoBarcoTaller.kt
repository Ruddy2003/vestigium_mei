package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.ALTO_CAJON
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.FAROL
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.HUECOS
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.MESA_ALTO
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.MESA_ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.MESA_FONDO
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.PIE_GRANDE
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.REGALA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_CUBA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.SITIO_MESA
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.Vista
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.estribor
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.k
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico.sitio
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El taller del naturalista en la cubierta del bergantín ([BarcoBotanico]), el primer plano: la mesa de trabajo
 * **[Diseño propio]** (el cuento la pone en la cabina; el cuadro la saca a cubierta, a la vista) grande y cerca, con lo
 * suyo según la hora ([EscenaBotanico.mesaDeCubierta]): la carta marina sujeta con piedras y el compás, la prensa con sus
 * correas, una hoja de herbario recostada en ella, la caja de insectos abierta y levantada, los frascos, el pájaro que
 * se está disecando con su estopa y su etiqueta, el tintero; y el objeto del día delante, en medio. Detrás, la bitácora
 * y la rueda, que la mesa tapa a medias. A la derecha, los cajones trincados contra la amurada (el arco `cajones`), la
 * caja de frascos con serrín, la cuba del agua, el balde y el lampazo del baldeo, la red tendida en la regala, el farol
 * de popa. En el suelo, las piedras de la última costa y unas adujas.
 *
 * Cada cosa pide su pincel a [Vista.pincel]: al sol se enciende; dentro de la sombra de las velas o de la amurada,
 * penumbra cálida; de noche, el calor de la lumbre que tenga más cerca.
 */
internal object TallerBarco {
    private val C = ColorBotanico

    private val SITIO_FRASCOS = sitio(0.3f, 3.0f, -0.25f)
    private val SITIO_BALDE = sitio(0.62f, 2.65f, 0f)

    private const val RADIO_CUBA = 0.22f
    private const val ALTO_CUBA = 0.8f

    /**
     * La cuba del agua dulce, abierta: las duelas, cada una de su tono, con su veta, sus golpes y el verdín de abajo; los
     * aros de hierro con su óxido y sus chorreras; el borde mojado; el agua dentro, que copia el cielo; el cazo de latón
     * colgado del canto, y el cerco de agua en la cubierta.
     */
    private fun DrawScope.cubaDeAgua(v: Vista, p: Pincel, sol: Sol3) {
        val s = SITIO_CUBA; val e = s.escala(0f, 0f); val r = RADIO_CUBA; val h = ALTO_CUBA; val az = Azar(Semilla.de("botanico", "cuba propia"))
        fun panza(y: Float) = r * (1f + 0.09f * sin(PI_F * y / h))
        // El cerco de agua en la cubierta.
        mancha(s.p(0f, 0f, -0.05f), r * e * 1.5f, r * e * 0.35f, lerp(v.l.medio, C.MADERA_OSC, 0.5f), 0.35f)
        val n = 22; val ys = (0..6).map { it / 6f }
        val vistas = (0 until n).filter { i -> val a = (2 * PI_F * (i + 0.5f) / n); s.seVe(V3(r * cos(a), h * 0.5f, r * sin(a)), V3(cos(a), 0f, sin(a))) }
        for (i in vistas.sortedByDescending { val a = (2 * PI_F * (it + 0.5f) / n); s.hondo(V3(r * cos(a), 0f, r * sin(a))) }) {
            val a0 = 2 * PI_F * i / n; val a1 = 2 * PI_F * (i + 1) / n; val am = (a0 + a1) / 2f
            val pts = ys.map { y -> s.p(panza(y * h) * cos(a0), y * h, panza(y * h) * sin(a0)) } + ys.reversed().map { y -> s.p(panza(y * h) * cos(a1), y * h, panza(y * h) * sin(a1)) }
            val duela = lerp(C.MADERA, listOf(C.ARENA, C.MADERA_OSC, C.CUERO, C.TECA)[az.entero(4)], az.entre(0.05f, 0.28f))
            val t = sol.t(s.normal(V3(cos(am), 0f, sin(am))))
            val f = poligono(pts)
            drawPath(f, p.por(duela, t))
            clipPath(f) {
                // La veta: rayas largas a lo alto de la duela; los golpes, claros; el verdín y lo mojado de abajo.
                repeat(3) { val u = az.entre(0.2f, 0.8f); val aa = a0 + (a1 - a0) * u; drawLine(p.sombra(duela).copy(alpha = 0.35f), s.p(panza(0f) * cos(aa), 0.04f, panza(0f) * sin(aa)), s.p(panza(h) * cos(aa + az.entre(-0.02f, 0.02f)), h * 0.96f, panza(h) * sin(aa)), (e * 0.004f).coerceIn(0.3f, 0.8f)) }
                if (az.real() > 0.5f) { val y = az.entre(0.25f, 0.8f) * h; drawLine(p.luz(lerp(duela, C.LONA, 0.4f)).copy(alpha = 0.55f), s.p(panza(y) * cos(am - 0.04f), y, panza(y) * sin(am - 0.04f)), s.p(panza(y) * cos(am + 0.05f), y - 0.02f, panza(y) * sin(am + 0.05f)), (e * 0.006f).coerceIn(0.3f, 1f), StrokeCap.Round) }
                drawPath(poligono(listOf(s.p(panza(0f) * cos(a0), 0f, panza(0f) * sin(a0)), s.p(panza(0f) * cos(a1), 0f, panza(0f) * sin(a1)), s.p(panza(0.14f * h) * cos(a1), 0.14f * h, panza(0.14f * h) * sin(a1)), s.p(panza(0.12f * h) * cos(a0), 0.12f * h, panza(0.12f * h) * sin(a0)))), p.sombra(lerp(C.MADERA, C.SELVA, 0.45f)).copy(alpha = 0.55f))
            }
            drawPath(Path().apply { moveTo(pts[0]); for (q in pts.subList(1, ys.size)) lineTo(q) }, p.sombra(C.BREA).copy(alpha = 0.4f), style = Stroke((e * 0.005f).coerceIn(0.35f, 0.9f)))
        }
        // Los aros de hierro, con su filo de luz y el óxido que chorrea por las duelas.
        val orden = vistas.sorted()
        for (y in listOf(0.08f, 0.28f, 0.72f, 0.93f)) {
            if (orden.isEmpty()) continue
            val pts = (orden + listOf(orden.last() + 1)).map { j -> val a = 2 * PI_F * j / n; s.p(panza(y * h) * 1.02f * cos(a), y * h, panza(y * h) * 1.02f * sin(a)) }
            drawPath(Path().apply { moveTo(pts[0]); for (q in pts.drop(1)) lineTo(q) }, p.sombra(C.HIERRO), style = Stroke((e * 0.028f).coerceIn(0.6f, 3.4f)))
            drawPath(Path().apply { moveTo(pts[0] + Offset(0f, -0.5f)); for (q in pts.drop(1)) lineTo(q + Offset(0f, -0.5f)) }, p.luz(C.HIERRO).copy(alpha = 0.45f), style = Stroke((e * 0.008f).coerceIn(0.3f, 1f)))
            repeat(4) { val q = pts[az.entero(pts.size)]; drawLine(p.medio(C.CUERO).copy(alpha = 0.5f), q, q + Offset(az.entre(-0.3f, 0.3f), e * az.entre(0.03f, 0.1f)), (e * 0.009f).coerceIn(0.4f, 1.4f), StrokeCap.Round) }
        }
        // La boca: el canto de las duelas, mojado, y el agua dentro, con el cielo y un brillo.
        val boca = (0..24).map { j -> val a = 2 * PI_F * j / 24; s.p(r * 1.06f * cos(a), h, r * 1.06f * sin(a)) }
        drawPath(poligono(boca), p.arriba(lerp(C.MADERA, C.MADERA_OSC, 0.3f)))
        // Por dentro, las duelas del fondo en sombra; el agua, un palmo más abajo, deja ver esa pared.
        val dentro = (0..24).map { j -> val a = 2 * PI_F * j / 24; s.p(r * 0.95f * cos(a), h - 0.005f, r * 0.95f * sin(a)) }
        drawPath(poligono(dentro), p.sombra(lerp(C.MADERA_OSC, C.BREA, 0.3f)))
        val agua = (0..24).map { j -> val a = 2 * PI_F * j / 24; s.p(r * 0.93f * cos(a), h - 0.14f, r * 0.93f * sin(a)) }
        clipPath(poligono(dentro)) {
            drawPath(poligono(agua), Brush.verticalGradient(listOf(lerp(v.l.tono(lerp(C.MAR_HONDO, C.BREA, 0.3f)), v.l.medio, 0.3f), v.l.tono(lerp(C.MAR_HONDO, C.BREA, 0.5f))), agua.minOf { it.y }, agua.maxOf { it.y }))
            for (k in 0 until 8) { val a = PI_F + PI_F * (k + 0.5f) / 8f; drawLine(p.sombra(C.BREA).copy(alpha = 0.4f), s.p(r * 0.95f * cos(a), h, r * 0.95f * sin(a)), s.p(r * 0.93f * cos(a), h - 0.14f, r * 0.93f * sin(a)), 0.5f) }
        }
        val c = s.p(0.03f, h - 0.14f, 0.04f)
        mancha(c, r * e * 0.5f, r * e * 0.08f, lerp(v.l.bajo, Color.White, 0.4f), 0.5f * (1f - 0.6f * v.l.noche))
        if (v.lu.farol > 0.05f) mancha(c, r * e * 0.3f, r * e * 0.06f, C.LUMBRE, 0.6f * v.lu.farol)
        if (v.nieve > 0.02f) drawPath(poligono(agua), p.luz(C.NIEVE).copy(alpha = 0.7f * v.nieve))
        // El cazo de latón, colgado del canto por su mango.
        val m0 = s.p(-r * 0.9f, h + 0.02f, -r * 0.4f); val m1 = s.p(-r * 1.5f, h - 0.1f, -r * 0.75f)
        drawLine(p.medio(C.MADERA), m0, s.p(r * 0.3f, h + 0.06f, r * 0.1f), (e * 0.02f).coerceAtLeast(0.6f), StrokeCap.Round)
        val cz = m1; val rc = e * 0.06f
        drawOval(Brush.horizontalGradient(listOf(p.sombra(C.LATON), p.luz(C.LATON), p.medio(C.LATON)), cz.x - rc, cz.x + rc), Offset(cz.x - rc, cz.y - rc * 0.7f), Size(rc * 2f, rc * 1.4f))
        drawLine(p.medio(C.LATON), m0, cz + Offset(rc * 0.6f, -rc * 0.5f), (e * 0.012f).coerceAtLeast(0.5f))
    }

    private const val PI_F = 3.1415927f

    private fun frascos() = PiezasBotanico.CajaDeFrascos(SITIO_FRASCOS, Semilla.de("botanico", "frascos"))
    private fun cuba(v: Vista) = PiezasBotanico.Cuba(SITIO_CUBA, Semilla.de("botanico", "cuba"), radio = RADIO_CUBA, alto = ALTO_CUBA, nieve = v.nieve)

    /** Los cajones de hoy, trincados contra la amurada: los de abajo primero, de popa a proa. */
    private fun cajones(v: Vista): List<PiezasBotanico.Cajon> = (0 until BarcoBotanico.cajonesEnCubierta(v.m)).map { i -> val h = HUECOS[i]
        PiezasBotanico.Cajon(sitio(h[0], h[1], h[3]), Semilla.de("botanico", "cajon", i), ancho = 0.82f - 0.06f * (i % 3), alto = ALTO_CAJON, fondo = 0.56f, base = h[2] * ALTO_CAJON,
            desgaste = 0.3f + 0.2f * (i % 4), forrado = EscenaBotanico.diaDeEnvio(v.m) && i == 0, nieve = v.nieve) }

    /** Las sombras del taller en la cubierta (van en la capa de sombras de [CubiertaBarco]). */
    fun sombras(v: Vista): List<Path> = buildList {
        val sol = v.sol
        add(PiezasBotanico.sombraDeCuba(cuba(v), sol))
        if (EscenaBotanico.mesaDeCubierta(v.m) != null) add(PiezasBotanico.sombraDeMesa(BarcoBotanico.mesa(v.m), sol))
        add(PiezasBotanico.sombraDeCajaDeFrascos(frascos(), sol))
        for (cj in cajones(v)) add(PiezasBotanico.sombraDeCajon(cj, sol))
        add(sombraDe(SITIO_BALDE, sol, (0 until 10).flatMap { i -> val a = i * 0.628f; listOf(V3(0.15f * cos(a), 0f, 0.15f * sin(a)), V3(0.17f * cos(a), 0.3f, 0.17f * sin(a))) }))
    }

    /** Las esquinas (en metros del barco) de lo que está arrimado a la amurada de estribor: su sombra sube por ella con el sol de babor. */
    fun cuerpos(v: Vista): List<List<V3>> = buildList {
        fun caja(s: Sitio, x0: Float, x1: Float, y0: Float, y1: Float, z0: Float, z1: Float) = esquinas(x0, x1, y0, y1, z0, z1).map { V3(s.mx(it.x, it.z), it.y, s.mz(it.x, it.z)) }
        for (c in cajones(v)) add(caja(c.s, c.x0, c.x1, c.y0, c.y1, c.z0, c.z1))
        add(caja(SITIO_CUBA, -RADIO_CUBA, RADIO_CUBA, 0f, ALTO_CUBA, -RADIO_CUBA, RADIO_CUBA))
        add(caja(SITIO_BALDE, -0.16f, 0.16f, 0f, 0.3f, -0.16f, 0.16f))
    }

    fun DrawScope.primerPlano(v: Vista) {
        val m = v.m; val estado = EscenaBotanico.mesaDeCubierta(m)
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        // La bitácora y la rueda: detrás de la mesa, que las tapa a medias.
        cosas += parte(BarcoBotanico.SITIO_BITACORA.pie.y) { with(CubiertaBarco) { bitacora(v) } }
        cosas += parte(BarcoBotanico.SITIO_TIMON.pie.y) { with(CubiertaBarco) { rueda(v) } }
        cosas += parte(pc(-1.5f, 0f, 4.6f).y) { with(CubiertaBarco) { adujas(v, listOf(V3(-1.55f, 0f, 4.7f))) } }
        val me = BarcoBotanico.mesa(m)
        val (pm, sm) = v.pincel(-0.55f, 3.62f, MESA_ALTO)
        if (estado != null) {
            cosas += parte(SITIO_MESA.pie.y) {
                with(PiezasBotanico) { mesa(me, pm, sm) }
                if (estado == EscenaBotanico.Mesa.TRABAJO || estado == EscenaBotanico.Mesa.QUIETA) sobreLaMesa(v, me, estado == EscenaBotanico.Mesa.TRABAJO, pm, sm)
                if (estado == EscenaBotanico.Mesa.RECOGIDA) trincas(v, me)
                if (estado == EscenaBotanico.Mesa.TAPADA) encerado(v, me, pm, sm)
                with(ObjetosBotanico) { sobreElSoporte(m, pm, BarcoBotanico.soporte(m), BarcoBotanico.escalaEn(SITIO_MESA.z0 - MESA_FONDO * 0.17f)) }
            }
        } else {
            // Sin mesa (fondeado frente a un lugar sin cuadro): un cajón hace de soporte.
            val c = PiezasBotanico.Cajon(SITIO_MESA, Semilla.de("botanico", "cajon soporte"), ancho = 0.82f, alto = ALTO_CAJON, fondo = 0.56f, nieve = v.nieve)
            cosas += parte(SITIO_MESA.pie.y) { with(PiezasBotanico) { cajon(c, pm, sm) }; with(ObjetosBotanico) { sobreElSoporte(m, pm, c.tapa, BarcoBotanico.escalaEn(SITIO_MESA.z0)) } }
        }
        // La caja de frascos con serrín y unos frascos sueltos junto a ella.
        val fr = frascos()
        cosas += parte(SITIO_FRASCOS.pie.y) {
            val (pf, sf) = v.pincel(0.3f, 3f, 0.3f)
            with(PiezasBotanico) { cajaDeFrascos(fr, pf, sf); frasco(pf, pc(0.66f, 0f, 3.05f), 20f, 0); frasco(pf, pc(0.74f, 0f, 3.3f), 16f, 1) }
        }
        cosas += parte(pc(-1.4f, 0f, 3.45f).y) { piedras(v) }
        val (pg, _) = v.pincel(0.5f, 3.2f, 0.6f)
        ObjetosBotanico.grande(m, pg, PIE_GRANDE, BarcoBotanico.apoyoGrande(m), BarcoBotanico.escalaEn(3.2f))?.let { g -> cosas += parte(g.pie.y) { g.pintar(this) } }
        // Los cajones, trincados contra la amurada.
        for (c in cajones(v).sortedWith(compareBy({ -it.s.hondo(V3(0f, 0f, 0f)) }, { it.base }))) cosas += parte(c.s.pie.y + c.base * 0.01f) {
            val (pcj, scj) = v.pincel(c.s.mx(0f, 0f), c.s.mz(0f, 0f), c.base + 0.3f); with(PiezasBotanico) { cajon(c, pcj, scj) } }
        // El balde y el lampazo del baldeo; la cuba del agua, en la esquina.
        cosas += parte(SITIO_BALDE.pie.y) { balde(v) }
        cosas += parte(pc(0.9f, 0f, 6.6f).y) { lampazo(v) }
        val cu = cuba(v)
        cosas += parte(ALTO + 60f) {
            val (pq, sq) = v.pincel(1.15f, 2.05f, 0.8f); cubaDeAgua(v, pq, sq)
            // La trinca: dos vueltas de cabo que la abrazan por delante y van a las argollas de la amurada.
            val s = SITIO_CUBA; val g = (s.escala(0f, 0f) * 0.02f).coerceIn(0.8f, 3f)
            for (h in listOf(0.3f, 0.62f)) {
                val vuelta = (0..12).map { i -> val a = (Math.PI + Math.PI * i / 12.0).toFloat(); s.p((RADIO_CUBA + 0.015f) * cos(a), h + 0.012f * sin(a * 3f), (RADIO_CUBA + 0.015f) * sin(a)) }
                drawPath(Path().apply { moveTo(vuelta[0]); for (q in vuelta.drop(1)) lineTo(q) }, pq.sombra(C.CANAMO), style = Stroke(g, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(vuelta[0] + Offset(0f, -g * 0.3f)); for (q in vuelta.drop(1)) lineTo(q + Offset(0f, -g * 0.3f)) }, pq.luz(C.CANAMO).copy(alpha = 0.6f), style = Stroke(g * 0.4f, cap = StrokeCap.Round))
            }
        }
        when (EscenaBotanico.red(m)) {
            EscenaBotanico.Red.SECANDO -> cosas += parte(pc(estribor(10.5f), 0f, 10.5f).y) { val (pr, _) = v.pincel(1.4f, 10.5f, 1f); with(PiezasBotanico) { redASecar(pr, pc(estribor(9.7f) - 0.02f, REGALA + 0.03f, 9.7f), pc(estribor(11.6f) - 0.02f, REGALA + 0.03f, 11.6f), k(10.6f) * 0.55f, Azar(Semilla.de("botanico", "red"))) } }
            EscenaBotanico.Red.ARRASTRANDO -> cosas += parte(ALTO + 50f) { with(PiezasBotanico) { cabo(v.p, pc(estribor(2.9f) + 0.06f, REGALA + 0.06f, 2.9f), Offset(ANCHO + 30f, ALTO - 30f), 3f, 1.8f) } }
            EscenaBotanico.Red.GUARDADA -> Unit
        }
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()
        farol(v)
    }

    // ---------------------------------------------------------------- Lo de encima de la mesa

    /**
     * Lo que hay en la mesa. Trabajando: la carta marina con sus piedras y el compás, la prensa y una hoja de herbario
     * recostada en ella, la caja de insectos abierta y levantada sobre un libro, tres frascos, el pájaro con su estopa y
     * su etiqueta, el tintero con la pluma. Quieta: la prensa, la carta enrollada, la caja cerrada, el tintero.
     */
    private fun DrawScope.sobreLaMesa(v: Vista, me: PiezasBotanico.Mesa, trabajando: Boolean, p: Pincel, sol: Sol3) {
        val s = me.s; val w = MESA_ANCHO / 2f; val d = MESA_FONDO / 2f; val h = MESA_ALTO
        val alSol = if (p === v.p && v.r.fuerza > 0.1f) 1f else 0f
        val t = sol.t(s.normal(V3(0f, 1f, 0f))) + 0.2f
        with(PiezasBotanico) {
            if (trabajando) {
                // La carta marina, extendida a la izquierda, con dos piedras en las esquinas y el compás de puntas.
                val carta = listOf(me.sobre(-0.98f, -0.55f), me.sobre(-0.18f, -0.62f), me.sobre(-0.12f, 0.75f), me.sobre(-0.92f, 0.82f))
                cartaMarina(p, carta, t, Azar(Semilla.de("botanico", "carta")), sol = alSol)
                piedra(p, me.sobre(-0.9f, -0.5f), k(s.z0) * 0.06f, 0); piedra(p, me.sobre(-0.22f, 0.7f), k(s.z0) * 0.05f, 1)
                val c0 = me.sobre(-0.55f, 0.1f); drawLine(p.luz(C.LATON), c0, me.sobre(-0.38f, -0.25f), 0.9f, StrokeCap.Round); drawLine(p.medio(C.LATON), c0, me.sobre(-0.68f, -0.22f), 0.9f, StrokeCap.Round); drawCircle(p.luz(C.LATON), 1.1f, c0)
            } else {
                // La carta enrollada.
                val a = me.sobre(-0.85f, 0.35f); val b = me.sobre(-0.2f, 0.5f); val r = k(s.z0) * 0.035f
                drawLine(p.sombra(C.PAPEL), a + Offset(0f, r * 0.5f), b + Offset(0f, r * 0.5f), r * 2f, StrokeCap.Round); drawLine(p.luz(C.PAPEL), a, b, r * 1.4f, StrokeCap.Round)
                drawOval(p.medio(lerp(C.PAPEL, C.CUERO, 0.2f)), Offset(a.x - r * 0.6f, a.y - r), Size(r * 1.2f, r * 2f))
            }
            // La prensa, en su esquina del fondo, y una hoja de herbario recostada en ella, de cara a quien mira.
            val prensaS = Sitio(s.v, me.sobre(-0.52f, 0.55f), s.k, s.giro + 0.18f)
            prensa(PiezasBotanico.Prensa(prensaS, Semilla.de("botanico", "prensa"), ancho = 0.5f, fondo = 0.34f, grueso = 0.14f, base = 0f), p, sol)
            // Detrás, una hoja de herbario de lado, con su planta de flor (siempre a la vista); delante, trabajando, otra recostada en la prensa.
            val h2 = listOf(s.p(-0.3f * MESA_ANCHO, h + 0.01f, 0.28f), s.p(-0.06f * MESA_ANCHO, h + 0.01f, 0.3f), s.p(-0.06f * MESA_ANCHO, h + 0.3f, 0.36f), s.p(-0.3f * MESA_ANCHO, h + 0.3f, 0.34f))
            hojaDeHerbario(p, h2, 0, 0.1f, Azar(Semilla.de("botanico", "herbario mesa 2")), sol = alSol * 0.6f)
            if (trabajando) {
                val pie0 = s.p(-0.05f * MESA_ANCHO, h + 0.01f, 0.08f); val pie1 = s.p(0.28f * MESA_ANCHO, h + 0.01f, 0.06f)
                val alto = k(s.z0 + 0.1f) * 0.42f
                val hoja = listOf(pie0, pie1, pie1 + Offset(-alto * 0.16f, -alto), pie0 + Offset(-alto * 0.18f, -alto * 0.98f))
                drawPath(poligono(listOf(pie0, pie1, pie1 + Offset(alto * 0.25f, alto * 0.06f), pie0 + Offset(alto * 0.25f, alto * 0.06f))), p.sombra(C.MADERA).copy(alpha = 0.35f))
                hojaDeHerbario(p, hoja, 1, 0.4f, Azar(Semilla.de("botanico", "herbario mesa")), sol = alSol)
            }
            // La caja de insectos, abierta y levantada sobre un libro, a la derecha del fondo (sus mariposas, siempre a la vista).
            run {
                val libro = Sitio(s.v, me.sobre(0.62f, 0.45f), s.k, s.giro - 0.15f)
                for (cara in visibles(libro, carasDeCaja(-0.17f, 0.17f, 0f, 0.07f, -0.12f, 0.12f))) { drawPath(forma(libro, cara), p.por(if (cara.arriba) C.CUERO else C.PAPEL, luzDe(libro, sol, cara))) }
                val b0 = libro.p(-0.2f, 0.07f, -0.04f); val b1 = libro.p(0.26f, 0.07f, -0.06f); val altoC = k(s.z0 + 0.4f) * 0.38f
                val caja = listOf(b0, b1, b1 + Offset(altoC * 0.12f, -altoC), b0 + Offset(altoC * 0.1f, -altoC * 0.96f))
                cajaDeInsectos(p, caja, Offset(-altoC * 0.08f, altoC * 0.05f), t, Azar(Semilla.de("botanico", "insectos mesa")), sol = alSol)
            }
            // Los frascos de esta tanda, de pie, y el pájaro a medio disecar con su estopa y su etiqueta.
            val ef = k(s.z0 - 0.1f)
            frasco(p, me.sobre(0.5f, -0.1f), ef * 0.24f, 1); frasco(p, me.sobre(0.72f, 0.05f), ef * 0.19f, 3)
            if (trabajando) {
                frasco(p, me.sobre(0.86f, -0.28f), ef * 0.17f, 2)
                pajaro(p, me.sobre(-0.45f, -0.3f), ef * 0.2f)
            }
            // El tintero de cuerno con la pluma.
            val ti = me.sobre(0.3f, 0.48f); val r = ef * 0.03f
            drawOval(p.sombra(C.BREA), Offset(ti.x - r, ti.y - r * 0.5f), Size(r * 2f, r)); drawRect(p.medio(lerp(C.CUERO, C.BREA, 0.4f)), Offset(ti.x - r, ti.y - r * 1.4f), Size(r * 2f, r * 1.2f)); drawOval(p.luz(lerp(C.CUERO, C.ARENA, 0.3f)), Offset(ti.x - r, ti.y - r * 1.7f), Size(r * 2f, r * 0.8f))
            drawPath(Path().apply { moveTo(ti + Offset(0f, -r * 1.4f)); cubicTo(ti + Offset(r * 2f, -r * 5f), ti + Offset(r * 4f, -r * 7f), ti + Offset(r * 6f, -r * 9f)) }, p.luz(C.LONA), style = Stroke((r * 0.6f).coerceAtLeast(0.6f), cap = StrokeCap.Round))
        }
    }

    /** Una piedra suelta, con su lado de luz. */
    private fun DrawScope.piedra(p: Pincel, q: Offset, r: Float, i: Int) {
        val roca = if (i % 3 == 0) lerp(C.ROCA, C.CUERO, 0.3f) else if (i % 3 == 1) lerp(C.ROCA, C.BREA, 0.3f) else lerp(C.ROCA, C.LONA, 0.3f)
        val forma = poligono(listOf(q + Offset(-r, 0f), q + Offset(-r * 0.8f, -r * 0.7f), q + Offset(r * 0.1f, -r * 0.95f), q + Offset(r * 0.9f, -r * 0.5f), q + Offset(r, 0f)))
        drawPath(forma, Brush.horizontalGradient(listOf(p.sombra(roca), p.medio(roca), p.luz(roca)), q.x + (if (p.solALaDerecha) -r else r), q.x + (if (p.solALaDerecha) r else -r)))
        drawPath(forma, p.sombra(lerp(roca, C.BREA, 0.4f)).copy(alpha = 0.4f), style = Stroke(0.4f))
    }

    /**
     * El pájaro que está disecando, tendido de espaldas en la mesa: el cuerpo relleno de estopa (que asoma), las alas
     * plegadas con sus plumas, la cola, el pico, las patas y la etiqueta atada a una pata. Un pinzón pardo.
     */
    private fun DrawScope.pajaro(p: Pincel, c: Offset, tam: Float) {
        val plumas = lerp(C.CUERO, C.ARENA, 0.25f)
        drawOval(p.sombra(C.MADERA).copy(alpha = 0.4f), Offset(c.x - tam * 0.5f, c.y - tam * 0.05f), Size(tam * 1.1f, tam * 0.2f))
        val cuerpo = Path().apply { moveTo(c + Offset(-tam * 0.45f, 0f)); cubicTo(c + Offset(-tam * 0.4f, -tam * 0.3f), c + Offset(tam * 0.25f, -tam * 0.32f), c + Offset(tam * 0.38f, -tam * 0.1f)); cubicTo(c + Offset(tam * 0.3f, tam * 0.05f), c + Offset(-tam * 0.3f, tam * 0.06f), c + Offset(-tam * 0.45f, 0f)); close() }
        drawPath(cuerpo, Brush.verticalGradient(listOf(p.luz(plumas), p.medio(plumas), p.sombra(plumas)), c.y - tam * 0.3f, c.y + tam * 0.05f))
        // El ala, con las plumas en raya; la cola; la cabeza y el pico grueso.
        for (k in 0..4) drawLine(p.sombra(lerp(plumas, C.BREA, 0.3f)).copy(alpha = 0.6f), c + Offset(-tam * 0.1f + k * tam * 0.08f, -tam * 0.22f), c + Offset(-tam * 0.3f + k * tam * 0.07f, -tam * 0.05f), 0.5f)
        drawPath(poligono(c + Offset(-tam * 0.42f, -tam * 0.06f), c + Offset(-tam * 0.78f, -tam * 0.12f), c + Offset(-tam * 0.76f, tam * 0.02f)), p.medio(lerp(plumas, C.BREA, 0.25f)))
        drawCircle(p.medio(lerp(plumas, C.BREA, 0.15f)), tam * 0.13f, c + Offset(tam * 0.4f, -tam * 0.12f))
        drawPath(poligono(c + Offset(tam * 0.5f, -tam * 0.17f), c + Offset(tam * 0.66f, -tam * 0.1f), c + Offset(tam * 0.5f, -tam * 0.05f)), p.medio(lerp(C.ARENA, C.BREA, 0.35f)))
        // La estopa que asoma por el corte del vientre.
        mancha(c + Offset(0f, -tam * 0.24f), tam * 0.12f, tam * 0.04f, p.luz(lerp(C.LONA, C.ARENA, 0.4f)), 0.9f)
        // Las patas y la etiqueta atada con su hilo.
        drawLine(p.sombra(C.BREA), c + Offset(tam * 0.05f, -tam * 0.26f), c + Offset(tam * 0.12f, -tam * 0.4f), 0.5f)
        val et = c + Offset(tam * 0.3f, -tam * 0.45f)
        drawLine(p.sombra(C.CANAMO), c + Offset(tam * 0.12f, -tam * 0.4f), et, 0.4f)
        drawRect(p.luz(C.PAPEL), Offset(et.x, et.y - tam * 0.06f), Size(tam * 0.22f, tam * 0.12f)); drawLine(p.sombra(C.BREA).copy(alpha = 0.6f), Offset(et.x + tam * 0.03f, et.y), Offset(et.x + tam * 0.18f, et.y), 0.4f)
    }

    /**
     * El encerado sobre la mesa, con lluvia o nieve: una lona embreada, oscura y con brillo, que cae por los lados en
     * pliegues, se abomba sobre lo que tapa (la prensa, la caja, los frascos) y se ata a las patas. Mojada, refleja el
     * cielo; con nieve, se le queda encima.
     */
    private fun DrawScope.encerado(v: Vista, me: PiezasBotanico.Mesa, p: Pincel, sol: Sol3) {
        val s = me.s; val w = MESA_ANCHO / 2f + 0.05f; val d = MESA_FONDO / 2f + 0.05f; val h = MESA_ALTO + 0.02f; val e = k(s.z0)
        val lona = lerp(C.BREA, lerp(C.ROCA, C.LONA, 0.3f), 0.72f)
        val az = Azar(Semilla.de("botanico", "encerado"))
        val caida = 0.36f
        val tArriba = sol.t(s.normal(V3(0f, 1f, 0f))) + 0.25f
        // Los faldones: el de delante y el del lado que se ve, con el borde de abajo ondulado y los pliegues.
        for ((cara, n) in listOf(Triple(V3(-w, h, -d), V3(w, h, -d), V3(0f, 0f, -1f)), Triple(V3(w, h, -d), V3(w, h, d), V3(1f, 0f, 0f)), Triple(V3(-w, h, d), V3(-w, h, -d), V3(-1f, 0f, 0f))).map { Pair(it.first to it.second, it.third) }) {
            if (!s.seVe(cara.first + (cara.second - cara.first) * 0.5f + V3(0f, -0.1f, 0f), n)) continue
            val (a, b) = cara; val t = sol.t(s.normal(n))
            val arriba = (0..10).map { s.p(a + (b - a) * (it / 10f)) }
            val abajo = (10 downTo 0).map { val q = a + (b - a) * (it / 10f); s.p(q + V3(0f, -caida * (0.86f + 0.1f * sin(it * 0.9f + 1.3f) + 0.05f * az.real()), 0f)) }
            val falda = poligono(arriba + abajo)
            drawPath(falda, p.por(lona, t))
            clipPath(falda) { for (i in 1..9) { val q = arriba[i]; drawLine((if (i % 2 == 0) p.luz(lona) else p.sombra(lona)).copy(alpha = 0.45f), q, q + Offset(az.entre(-1f, 1f), e * caida * 0.95f), (e * 0.02f).coerceIn(0.6f, 3f)) } }
        }
        // El tablero tapado, con los bultos de lo que hay debajo y el brillo del agua.
        val tapa = poligono(listOf(V3(-w, h, -d), V3(w, h, -d), V3(w, h, d), V3(-w, h, d)).map { s.p(it) })
        drawPath(tapa, p.por(lona, tArriba))
        clipPath(tapa) {
            for ((u, vv, r) in listOf(Triple(-0.52f, 0.5f, 0.22f), Triple(0.6f, 0.4f, 0.2f), Triple(0.5f, -0.1f, 0.12f), Triple(-0.1f, 0.2f, 0.16f))) {
                val c = me.sobre(u, vv) + Offset(0f, -e * r * 0.25f); val rr = e * r
                drawOval(p.sombra(lona).copy(alpha = 0.5f), Offset(c.x - rr, c.y - rr * 0.1f), Size(rr * 2f, rr * 0.45f))
                drawOval(p.luz(lerp(lona, C.LONA, 0.25f)), Offset(c.x - rr * 0.8f, c.y - rr * 0.35f), Size(rr * 1.6f, rr * 0.5f))
            }
            if (EscenaBotanico.cubiertaMojada(v.m)) repeat(6) { val c = me.sobre(az.entre(-0.9f, 0.9f), az.entre(-0.8f, 0.8f)); mancha(c, e * az.entre(0.1f, 0.25f), e * 0.03f, lerp(v.l.medio, Color.White, 0.2f), 0.35f) }
            if (v.nieve > 0.02f) drawPath(tapa, p.luz(C.NIEVE).copy(alpha = 0.85f * v.nieve))
        }
        drawPath(tapa, p.sombra(lona).copy(alpha = 0.6f), style = Stroke(0.6f))
        // Las ataduras a las patas.
        with(PiezasBotanico) { for (u in listOf(-0.8f, 0.8f)) cabo(p, s.p(u * w, h - caida * 0.8f, -d), s.p(u * w * 0.9f, 0.05f, -d * 0.7f), 0f, (e * 0.014f).coerceIn(0.5f, 2f)) }
    }

    /** Con tormenta, la mesa trincada a las argollas con dos vueltas por encima del tablero. */
    private fun DrawScope.trincas(v: Vista, me: PiezasBotanico.Mesa) {
        with(PiezasBotanico) { for (u in listOf(-0.6f, 0.6f)) { cabo(v.p, me.sobre(u, -1.05f), me.sobre(u, 1.05f), 0f, 1.6f); cabo(v.p, me.s.p(u * MESA_ANCHO / 2f, 0f, -MESA_FONDO * 0.7f), me.sobre(u, -1.05f), 0f, 1.4f) } }
    }

    // ---------------------------------------------------------------- Lo del suelo

    /** Las piedras que trajo de la última costa («les llena la cubierta de basura»), en un montón, y un saco abierto. */
    private fun DrawScope.piedras(v: Vista) {
        val az = Azar(Semilla.de("botanico", "piedras"))
        val (p, _) = v.pincel(-1.4f, 3.45f, 0.1f)
        repeat(8) {
            val q = pc(az.entre(-1.55f, -0.95f), 0f, az.entre(3.25f, 3.6f)); val r = az.entre(2.5f, 6f)
            drawOval(v.r.color.copy(alpha = 0.45f * v.r.fuerza), Offset(q.x - r + v.r.ax.coerceIn(-2f, 2f) * r * 0.4f, q.y - r * 0.15f), Size(r * 2.3f, r * 0.5f), blendMode = androidx.compose.ui.graphics.BlendMode.Multiply)
            piedra(p, q, r, it)
        }
    }

    /** El lampazo del baldeo, recostado en la amurada detrás de los cajones, con sus filásticas mojadas en el suelo. */
    private fun DrawScope.lampazo(v: Vista) {
        val (p, _) = v.pincel(0.9f, 6.6f, 0.5f); val e = k(6.6f)
        val cab = pc(0.9f, 0f, 6.6f); val tope = pc(estribor(6.15f) - 0.04f, REGALA + 0.16f, 6.15f)
        drawLine(p.sombra(C.MADERA), cab + Offset(0f, -e * 0.08f), tope, e * 0.035f, StrokeCap.Round); drawLine(p.luz(lerp(C.MADERA, C.ARENA, 0.4f)), cab + Offset(-e * 0.006f, -e * 0.08f), tope + Offset(-e * 0.006f, 0f), e * 0.012f, StrokeCap.Round)
        repeat(16) { val a = -2.6f + it * 0.14f; drawLine((if (it % 2 == 0) p.medio(lerp(C.CANAMO, C.LONA, 0.3f)) else p.sombra(C.CANAMO)), cab + Offset(0f, -e * 0.08f), cab + Offset(cos(a) * e * 0.2f, -e * 0.02f + sin(a).coerceAtLeast(-0.3f) * e * 0.05f + e * 0.04f), (e * 0.012f).coerceAtLeast(0.5f), StrokeCap.Round) }
        mancha(cab + Offset(0f, e * 0.02f), e * 0.28f, e * 0.06f, lerp(v.l.medio, Color.White, 0.1f), 0.25f * (1f - 0.6f * v.l.noche))
    }

    /** El balde del baldeo, de duelas, con su asa de cabo y el agua dentro, que refleja el cielo. */
    private fun DrawScope.balde(v: Vista) {
        val (p, _) = v.pincel(0.62f, 2.65f, 0.3f); val s = SITIO_BALDE; val e = s.escala(0f, 0f)
        // El balde: de duelas, con dos aros, el agua con el cielo reflejado.
        val r0 = 0.15f; val r1 = 0.17f; val hb = 0.3f
        val cuerpo = poligono(s.p(-r0, 0f, 0f), s.p(r0, 0f, 0f), s.p(r1, hb, 0f), s.p(-r1, hb, 0f))
        val lado = if (p.solALaDerecha) 1f else -1f
        drawPath(cuerpo, Brush.horizontalGradient(listOf(p.sombra(C.MADERA), p.medio(C.MADERA), p.luz(lerp(C.MADERA, C.ARENA, 0.25f)), p.medio(C.MADERA)), s.p(-lado * r1, 0f, 0f).x, s.p(lado * r1, 0f, 0f).x))
        clipPath(cuerpo) { for (k in -3..3) drawLine(p.sombra(C.MADERA_OSC).copy(alpha = 0.4f), s.p(k * r0 / 3.5f, 0f, -r0), s.p(k * r1 / 3.5f, hb, -r1), 0.5f) }
        for (h in listOf(0.06f, 0.24f)) drawLine(p.sombra(C.HIERRO), s.p(-r0 - 0.01f, h, 0f), s.p(r0 + 0.01f, h, 0f), (e * 0.016f).coerceAtLeast(0.5f))
        val boca = s.p(0f, hb, 0f); val rb = r1 * e
        drawOval(p.sombra(C.MADERA_OSC), Offset(boca.x - rb, boca.y - rb * 0.3f), Size(rb * 2f, rb * 0.6f))
        drawOval(lerp(v.l.tono(lerp(C.MAR, C.ORILLA, 0.5f)), v.l.medio, 0.4f), Offset(boca.x - rb * 0.85f, boca.y - rb * 0.18f), Size(rb * 1.7f, rb * 0.42f))
        drawPath(Path().apply { moveTo(boca + Offset(-rb, 0f)); cubicTo(boca + Offset(-rb * 0.9f, -rb * 1.6f), boca + Offset(rb * 0.9f, -rb * 1.6f), boca + Offset(rb, 0f)) }, p.sombra(C.CANAMO), style = Stroke((e * 0.014f).coerceAtLeast(0.5f)))
    }

    /** El farol de popa, sobre la regala de la aleta: de latón, con sus vidrios; de noche alumbra la amurada, los cajones y el agua. */
    private fun DrawScope.farol(v: Vista) {
        val (p, _) = v.pincel(1.3f, BarcoBotanico.Z_FAROL, 1f); val e = k(BarcoBotanico.Z_FAROL); val f = v.lu.farol
        // El pescante: una escuadra de hierro clavada en la regala, que sale hacia dentro, con su tornapunta; el farol cuelga de su gancho.
        val z = BarcoBotanico.Z_FAROL
        val pie = pc(estribor(z) - 0.03f, REGALA + 0.02f, z); val punta = pc(1.2f, REGALA + 0.12f, z); val tirante = pc(estribor(z) - 0.03f, REGALA - 0.22f, z)
        drawLine(p.sombra(C.HIERRO), pie, punta, (e * 0.03f).coerceAtLeast(0.7f), StrokeCap.Round); drawLine(p.sombra(C.HIERRO), tirante, pie + (punta - pie) * 0.55f, (e * 0.022f).coerceAtLeast(0.5f))
        drawLine(p.luz(C.HIERRO).copy(alpha = 0.55f), pie + Offset(0f, -0.4f), punta + Offset(0f, -0.4f), (e * 0.01f).coerceAtLeast(0.3f))
        val q = FAROL; val alto = e * 0.32f
        drawLine(p.sombra(C.HIERRO), punta, q + Offset(0f, -alto * 0.62f), (e * 0.012f).coerceAtLeast(0.4f))
        with(PiezasBotanico) { farol(p, q, alto, f) }
        if (f > 0.05f) resplandor(q, e * 1.6f, 0.3f * f, C.LUMBRE)
    }
}
