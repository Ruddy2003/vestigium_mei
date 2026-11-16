package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.grande
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.aduja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.hornacina
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.palo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.ristra
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismoTierra.bita
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.reflejoRoto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * Lo de cerca en el muelle: el barco de Nikos, amarrado de costado al muelle de la cabeza, con la popa a la izquierda de
 * quien mira (su espejo de popa blanco y azul con la hornacina del icono y su lamparilla, el costado, la cubierta que se
 * ve por encima de la regala, el palo mayor cortado por el borde y la botavara izada que cruza por arriba con las ristras
 * tendidas a secar), y las tablas del muelle de la cabeza, donde se pisa: la bita con la amarra, la aduja, el cajón con
 * el objeto del día y, el día de la Epifanía, los cirios y los ramos que la gente dejó en el borde.
 */
internal object CercaMuelle {
    private val C = ColorAbismo
    private val M = MuelleAbismo

    private const val POPA = MuelleAbismo.NIKOS_POPA
    private const val EJE = MuelleAbismo.NIKOS_EJE
    private const val CUBIERTA = MuelleAbismo.NIKOS_CUBIERTA
    private const val MEDIA = 2.15f
    private const val REGALA = 0.32f
    /** El palo mayor del barco de Nikos (cortado por el borde de la izquierda). */
    private const val PALO_X = -3.9f

    /** Dónde acaba la popa en el lienzo (para recortar el agua abierta). */
    val POPA_X: Float get() = M.pm(POPA, 0f, EJE - MEDIA).x

    /** La hornacina del icono, en el coronamiento de popa, a la banda de quien mira. */
    val ICONO: Offset get() = M.pm(POPA + 0.05f, REGALA + 0.3f, EJE - MEDIA * 0.55f)
    val K_ICONO: Float get() = M.k(EJE - MEDIA * 0.55f)

    /** La bita del muelle donde va la amarra de popa: en el paso del gato. */
    val BITA: Sitio get() = M.sitio(0.05f, 3.25f, 0.2f)

    /** La botavara: del palo a su extremo, izada sobre el río. */
    private val BOTAVARA_0 = V3(PALO_X, 1.4f, EJE)
    private val BOTAVARA_1 = V3(POPA + 2.2f, 3.4f, EJE + 0.2f)
    private fun pv(a: V3): Offset = M.pm(a.x, a.y, a.z)

    fun DrawScope.cerca(v: MuelleAbismo.Vista) {
        val m = v.m
        if (v.flota != EscenaAbismo.Flota.VACIO && v.flota != EscenaAbismo.Flota.SALIENDO || EscenaAbismo.hoyEs(m, "zarpa")) barcoDeNikos(v)
        muelle(v)
    }

    // ---------------------------------------------------------------- El barco de Nikos

    private fun DrawScope.barcoDeNikos(v: MuelleAbismo.Vista) {
        val p = v.p; val l = v.l; val m = v.m
        val zs = listOf(EJE - MEDIA, EJE - MEDIA * 0.6f, EJE, EJE + MEDIA * 0.6f, EJE + MEDIA)
        val popaCurva = zs.map { z -> val t = (z - EJE) / MEDIA; POPA + 0.35f * t * t }                // el espejo, redondeado
        // El reflejo del casco en el agua, al pie del espejo.
        val r0 = M.pm(POPA, M.AGUA, EJE + MEDIA)
        reflejoRoto(-10f, r0.x + 4f, r0.y, 18f, l.tono(C.BLANCO), 0.45f, Azar(61), 0.9f)
        // La regala de la otra banda y la cara de dentro de su borda.
        val lejosB = poligono(listOf(M.pm(-20f, CUBIERTA, EJE + MEDIA), M.pm(popaCurva.last(), CUBIERTA, EJE + MEDIA), M.pm(popaCurva.last(), REGALA, EJE + MEDIA), M.pm(-20f, REGALA, EJE + MEDIA)))
        drawPath(lejosB, p.frente(lerp(C.BLANCO, C.AZUL, 0.3f)))
        drawLine(p.arriba(C.MADERA_GRIS), M.pm(-20f, REGALA, EJE + MEDIA), M.pm(popaCurva.last(), REGALA, EJE + MEDIA), 0.1f * M.k(EJE + MEDIA))
        // La cubierta, entre las dos bordas.
        val cub = poligono(listOf(M.pm(-20f, CUBIERTA, EJE + MEDIA), M.pm(popaCurva.last(), CUBIERTA, EJE + MEDIA), M.pm(popaCurva.first(), CUBIERTA, EJE - MEDIA), M.pm(-20f, CUBIERTA, EJE - MEDIA)))
        drawPath(cub, p.arriba(lerp(C.MADERA, C.MADERA_GRIS, 0.4f)))
        clipPath(cub) { for (i in 1..9) { val z = EJE - MEDIA + 2f * MEDIA * i / 10f; drawLine(p.sombra(C.BREA).copy(alpha = 0.5f), M.pm(-20f, CUBIERTA, z), M.pm(POPA, CUBIERTA, z), 0.5f) } }
        // Lo de cubierta: la caseta de popa, un barril, la bomba tapada con su lona.
        val caseta = M.sitio(POPA - 2.4f, EJE + 0.4f, 0f, CUBIERTA)
        for (cara in visibles(caseta, carasDeCaja(-0.9f, 0.9f, 0f, 1.1f, -0.8f, 0.8f))) drawPath(forma(caseta, cara), p.por(lerp(C.BLANCO, C.MADERA_GRIS, 0.25f), luzDe(caseta, v.sol, cara)))
        val barril = M.sitio(POPA - 1.0f, EJE + 0.9f, 0f, CUBIERTA); val kb = barril.escala(0f, 0f)
        drawRect(Brush.horizontalGradient(listOf(p.luz(C.MADERA), p.sombra(C.MADERA_OSC)), barril.pie.x - 0.3f * kb, barril.pie.x + 0.3f * kb), Offset(barril.pie.x - 0.3f * kb, barril.pie.y - 0.85f * kb), Size(0.6f * kb, 0.85f * kb))
        for (h in listOf(0.15f, 0.7f)) drawLine(p.sombra(C.PLOMO), Offset(barril.pie.x - 0.3f * kb, barril.pie.y - h * kb), Offset(barril.pie.x + 0.3f * kb, barril.pie.y - h * kb), 0.04f * kb)
        // El palo mayor, cortado por el borde de la izquierda y por arriba.
        val kp = M.k(EJE)
        palo(p, M.pm(PALO_X, CUBIERTA, EJE), M.pm(PALO_X, 9f, EJE), 0.3f * kp, 0.24f * kp, zunchos = listOf(0.15f, 0.3f))
        // El estay de popa y el amantillo de la botavara.
        drawLine(p.sombra(C.CANAMO).copy(alpha = 0.8f), M.pm(PALO_X, 9f, EJE), pv(BOTAVARA_1), 0.5f)
        // El espejo de popa, blanco con la franja azul, redondeado, con su timón y la hornacina del icono.
        val espejo = poligono(zs.indices.map { M.pm(popaCurva[it], REGALA, zs[it]) } + zs.indices.reversed().map { M.pm(popaCurva[it] + 0.25f, M.AGUA + 0.1f, zs[it]) })
        val te = v.sol.t(V3(1f, 0f, -0.15f))
        drawPath(espejo, p.por(C.BLANCO, te))
        clipPath(espejo) {
            drawPath(poligono(zs.indices.map { M.pm(popaCurva[it], REGALA, zs[it]) } + zs.indices.reversed().map { M.pm(popaCurva[it], REGALA - 0.22f, zs[it]) }), p.por(C.AZUL, te))
            drawPath(poligono(zs.indices.map { M.pm(popaCurva[it] + 0.2f, M.AGUA + 0.32f, zs[it]) } + zs.indices.reversed().map { M.pm(popaCurva[it] + 0.25f, M.AGUA + 0.1f, zs[it]) }), p.medio(lerp(C.VERDIN, C.BREA, 0.25f)))
            // La pintura saltada y la chorrera de óxido de los herrajes.
            val az = Azar(Semilla.de("abismo", "espejo"))
            repeat(8) { val q = M.pm(POPA, az.entre(-0.7f, 0.1f), az.entre(EJE - MEDIA, EJE + MEDIA)); mancha(q, az.entre(1.5f, 4f), az.entre(0.8f, 2f), p.sombra(C.BLANCO), 0.4f) }
            // El ojo pintado no está aquí (está en la proa); el nombre, sin letras: una raya azul.
            drawLine(p.medio(C.AZUL), M.pm(POPA + 0.02f, -0.25f, EJE - 0.8f), M.pm(POPA + 0.02f, -0.25f, EJE + 0.8f), 0.06f * M.k(EJE))
        }
        // El costado de esta banda: lo que asoma sobre el borde del muelle.
        val costado = poligono(listOf(M.pm(-20f, REGALA, EJE - MEDIA), M.pm(popaCurva.first(), REGALA, EJE - MEDIA), M.pm(popaCurva.first() + 0.25f, M.AGUA + 0.1f, EJE - MEDIA), M.pm(-20f, M.AGUA, EJE - MEDIA)))
        val xs0 = M.pm(-6f, 0f, EJE - MEDIA).x; val xs1 = M.pm(popaCurva.first(), 0f, EJE - MEDIA).x
        drawPath(costado, Brush.horizontalGradient(listOf(p.sombra(C.BLANCO), p.frente(C.BLANCO), p.luz(C.BLANCO)), xs0, xs1))
        clipPath(costado) {
            drawPath(poligono(listOf(M.pm(-20f, REGALA, EJE - MEDIA), M.pm(popaCurva.first(), REGALA, EJE - MEDIA), M.pm(popaCurva.first(), REGALA - 0.22f, EJE - MEDIA), M.pm(-20f, REGALA - 0.22f, EJE - MEDIA))), p.frente(C.AZUL))
            // La tablazón, con sus juntas, la cinta de roce y las chorreras de la sal y del óxido.
            for (h in listOf(-0.08f, -0.3f, -0.52f, -0.74f)) drawLine(p.sombra(C.BLANCO).copy(alpha = 0.55f), M.pm(-20f, h, EJE - MEDIA), M.pm(popaCurva.first(), h, EJE - MEDIA), 0.6f)
            drawLine(p.por(C.MADERA_GRIS, 0.3f), M.pm(-20f, REGALA - 0.3f, EJE - MEDIA), M.pm(popaCurva.first(), REGALA - 0.3f, EJE - MEDIA), 0.07f * M.k(EJE - MEDIA))
            val azc = Azar(Semilla.de("abismo", "costado"))
            repeat(7) { val x = azc.entre(-6f, POPA); val q = M.pm(x, REGALA - 0.35f, EJE - MEDIA); drawLine(p.sombra(lerp(C.BLANCO, C.COBRE_OSC, 0.3f)).copy(alpha = 0.35f), q, M.pm(x, REGALA - azc.entre(0.6f, 1.1f), EJE - MEDIA), 0.5f) }
        }
        drawLine(p.arriba(C.MADERA_GRIS), M.pm(-20f, REGALA, EJE - MEDIA), M.pm(popaCurva.first(), REGALA, EJE - MEDIA), 0.1f * M.k(EJE - MEDIA))
        // El timón, por fuera del espejo, con su caña.
        val ti0 = M.pm(POPA + 0.42f, REGALA + 0.15f, EJE - 0.1f); val ti1 = M.pm(POPA + 0.5f, M.AGUA, EJE - 0.1f)
        drawLine(p.sombra(C.MADERA_OSC), ti0, ti1, 0.18f * M.k(EJE)); drawLine(p.luz(C.MADERA), ti0 + Offset(-1f, 0f), ti1 + Offset(-1f, 0f), 0.05f * M.k(EJE))
        drawLine(p.medio(C.MADERA), ti0, M.pm(POPA - 1.2f, REGALA + 0.45f, EJE - 0.05f), 0.07f * M.k(EJE), StrokeCap.Round)
        // La hornacina del icono, con su lamparilla.
        val c = ICONO; val k = K_ICONO
        drawLine(p.sombra(C.MADERA_OSC), M.pm(POPA + 0.05f, REGALA, EJE - MEDIA * 0.55f), c + Offset(0f, 0.12f * k), 0.06f * k)
        hornacina(v.pincel(c), c, k, v.lamparilla, mira = 0.5f, luto = EscenaAbismo.luto(m))
        if (l.noche > 0.2f) resplandor(c + Offset(6f, 10f), 60f, 0.45f * l.noche, C.LUMBRE)
        // La botavara izada, con la vela aferrada y las ristras tendidas a secar.
        botavara(v)
    }

    private fun DrawScope.botavara(v: MuelleAbismo.Vista) {
        val p = v.p; val m = v.m
        val a = pv(BOTAVARA_0); val b = pv(BOTAVARA_1); val k = M.k(EJE)
        drawLine(p.sombra(C.MADERA_OSC), a, b, 0.09f * k, StrokeCap.Round)
        drawLine(p.luz(C.MADERA), a + Offset(0f, -0.025f * k), b + Offset(0f, -0.025f * k), 0.03f * k, StrokeCap.Round)
        // La vela aferrada: un bulto de lona sobre la botavara, con sus tomadores.
        val n = 12
        val pts = (0..n).map { val t = it / n.toFloat(); a + (b - a) * t + Offset(0f, -(0.1f + 0.05f * sin(t * 9f)) * k * (1f - 0.6f * t)) }
        cabo(p, pts, 0.17f * k, C.LONA)
        for (i in 1 until 6) { val t = i / 6f; val q = a + (b - a) * t; drawLine(p.sombra(C.CANAMO), q + Offset(0f, -0.2f * k * (1f - 0.6f * t)), q + Offset(0f, 0.04f * k), 0.03f * k) }
        // Las ristras que cuelgan de la botavara: pocas en invierno (las esponjas están en la lonja); cargada, el día de la vuelta.
        val cuantas = if (EscenaAbismo.cargadoEnElMuelle(m)) 7 else if (v.flota == EscenaAbismo.Flota.POCOS) 4 else 2
        for (i in 0 until cuantas) {
            val t = 0.42f + 0.07f * i
            val zz = EJE + 0.2f * t; val q = a + (b - a) * t + Offset(0f, 0.05f * k); val kk = M.k(zz)
            ristra(p, q, Azar(Semilla.de("abismo", "botavara", i)).entre(0.7f, 1.1f) * kk, 0.055f * kk, 12, Semilla.de("abismo", "ristraMuelle", i), racimo = i % 2 == 0)
        }
    }

    // ---------------------------------------------------------------- El muelle de la cabeza

    private fun DrawScope.muelle(v: MuelleAbismo.Vista) {
        val p = v.p; val m = v.m
        val y0 = M.pm(0f, 0f, M.BORDE).y
        val tablas = poligono(listOf(Offset(-10f, y0), Offset(ANCHO + 10f, y0), Offset(ANCHO + 10f, ALTO + 10f), Offset(-10f, ALTO + 10f)))
        val madera = lerp(C.MADERA_GRIS, C.MADERA, 0.35f)
        drawPath(tablas, Brush.verticalGradient(listOf(p.arriba(madera, 0.1f), p.arriba(madera)), y0, ALTO))
        val az = Azar(Semilla.de("abismo", "tablasMuelle"))
        clipPath(tablas) {
            var x = -4f
            while (x < 4.5f) {
                val a = M.pm(x, 0f, M.BORDE); val b = M.pm(x, 0f, 1.2f); val a2 = M.pm(x + 0.22f, 0f, M.BORDE); val b2 = M.pm(x + 0.22f, 0f, 1.2f)
                drawPath(poligono(a, a2, b2, b), p.arriba(lerp(madera, if (az.real() > 0.5f) C.ARENA else C.MADERA_OSC, az.entre(0.03f, 0.15f))).copy(alpha = 0.7f))
                drawLine(p.sombra(C.BREA).copy(alpha = 0.55f), a, b, 0.8f)
                x += 0.22f
            }
            // El desgaste, las manchas de agua y de brea, la sal.
            repeat(14) { val q = Offset(az.entre(0f, ANCHO), az.entre(y0 + 4f, ALTO)); mancha(q, az.entre(5f, 16f), az.entre(1.2f, 3f), p.sombra(lerp(madera, C.BREA, 0.4f)), 0.25f) }
            repeat(8) { val q = Offset(az.entre(0f, ANCHO), az.entre(y0 + 4f, ALTO)); mancha(q, az.entre(5f, 14f), az.entre(1f, 2.4f), p.luz(lerp(madera, C.BLANCO, 0.5f)), 0.2f) }
        }
        // El canto del muelle: la tabla de borde, gastada, con su filo de luz.
        drawLine(p.arriba(lerp(madera, C.BLANCO, 0.2f)), Offset(-10f, y0), Offset(ANCHO + 10f, y0), 3.2f)
        drawLine(p.sombra(C.BREA).copy(alpha = 0.6f), Offset(-10f, y0 + 1.8f), Offset(ANCHO + 10f, y0 + 1.8f), 0.7f)
        // Las sombras de lo que hay en el muelle.
        val bita = BITA; val cajon = M.SITIO_CAJON
        capaSombra(v.r) { c ->
            clipPath(tablas) {
                sombra(sombraDe(bita, v.sol, esquinas(-0.15f, 0.15f, 0f, 0.38f, -0.15f, 0.15f)), c)
                sombra(sombraDe(cajon, v.sol, esquinas(-0.32f, 0.32f, 0f, M.CAJON_ALTO, -0.24f, 0.24f)), c)
            }
        }
        // La amarra de popa: de la bita al barco, combada; y la aduja del resto.
        if (v.flota != EscenaAbismo.Flota.VACIO && v.flota != EscenaAbismo.Flota.SALIENDO) {
            val de = bita.p(0f, 0.3f, 0f); val a = M.pm(POPA - 0.3f, REGALA, EJE - MEDIA + 0.05f)
            cabo(p, combada(de, a, 3f, 10), 0.05f * bita.k)
        }
        aduja(p, M.pm(-0.9f, 0f, 2.6f), 0.36f * M.k(2.6f), 0.13f * M.k(2.6f), 4, 0.05f * M.k(2.6f), C.CANAMO)
        bita(p, bita, v.sol, true)
        // La Epifanía: los cirios y los ramos que dejaron en el borde, y el agua bendita.
        if (EscenaAbismo.cruz(m)) {
            for (i in 0 until 6) {
                val x = -1.5f + i * 0.55f; val q = M.pm(x, 0f, M.BORDE - 0.2f); val kk = M.k(M.BORDE)
                if (i % 2 == 0) {
                    drawLine(p.luz(lerp(C.BLANCO, C.ESPONJA, 0.3f)), q, q + Offset(0f, -0.3f * kk), 0.04f * kk)
                    llamaQuieta(q + Offset(0f, -0.3f * kk), 0.05f * kk, v.l.noche)
                } else {
                    for (j in 0 until 5) drawCircle(if (j % 2 == 0) p.luz(C.BLANCO) else p.medio(C.ROJO), 0.04f * kk, q + Offset((j - 2) * 0.03f * kk, -0.12f * kk - (j % 2) * 0.03f * kk))
                    drawLine(p.medio(C.HIERBA), q, q + Offset(0f, -0.1f * kk), 0.02f * kk)
                }
            }
        }
        // El cajón del primer plano, cortado por el borde de abajo, con el objeto del día.
        for (cara in visibles(cajon, carasDeCaja(-0.32f, 0.32f, 0f, M.CAJON_ALTO, -0.24f, 0.24f))) {
            val t = luzDe(cajon, v.sol, cara); val f = forma(cajon, cara)
            drawPath(f, p.por(lerp(C.MADERA_GRIS, C.MADERA, 0.5f), t))
            clipPath(f) { for (kk in 1 until 4) linea(cajon, cara, 0f, kk / 4f, 1f, kk / 4f, p.sombra(C.BREA).copy(alpha = 0.6f), 0.8f) }
            drawPath(f, p.sombra(C.BREA).copy(alpha = 0.5f), style = Stroke(0.6f))
        }
        sobreElSoporte(m, v.pincel(M.soporte()), M.soporte(), M.escalaEn(2.75f))
        grande(m, v.pincel(M.PIE_GRANDE), M.PIE_GRANDE, M.APOYO_GRANDE, M.escalaEn(2.6f))?.pintar?.invoke(this)
    }

    /** Una llamita quieta (la parte quieta del cirio; la viva la mueve). */
    private fun DrawScope.llamaQuieta(c: Offset, r: Float, noche: Float) {
        resplandor(c, r * 6f, 0.3f + 0.6f * noche, C.LUMBRE)
        drawOval(Color(0xFFFFE6A0), Offset(c.x - r * 0.4f, c.y - r * 1.2f), Size(r * 0.8f, r * 1.4f))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: MuelleAbismo.Vista, reloj: Float) {
        val m = v.m
        if (v.flota == EscenaAbismo.Flota.VACIO || v.flota == EscenaAbismo.Flota.SALIENDO && !EscenaAbismo.hoyEs(m, "zarpa")) return
        // La llama de la lamparilla.
        val c = ICONO; val k = K_ICONO
        llama(Offset(c.x, c.y + 0.28f * k * 0.34f - 0.22f * k * 0.06f), 0.05f * k, reloj, 3)
        // De día, los reflejos del agua que bailan en el espejo de popa, cerca de la flotación.
        if (v.l.noche < 0.5f && v.r.fuerza > 0.1f) {
            val az = Azar(17)
            repeat(10) {
                val z = az.entre(EJE - 0.5f, EJE + MEDIA - 0.2f); val h = M.AGUA + az.entre(0.15f, 0.6f)
                val q = M.pm(POPA + 0.1f, h, z) + Offset(0f, 1.5f * sin(reloj * az.entre(1.5f, 2.6f) + it))
                val a = (0.35f * (0.5f + 0.5f * sin(reloj * 2.2f + it * 1.7f))).coerceIn(0f, 1f)
                drawLine(Color.White.copy(alpha = a), q + Offset(-1.5f, 0f), q + Offset(1.5f, -0.4f), 0.5f, StrokeCap.Round)
            }
        }
    }
}
