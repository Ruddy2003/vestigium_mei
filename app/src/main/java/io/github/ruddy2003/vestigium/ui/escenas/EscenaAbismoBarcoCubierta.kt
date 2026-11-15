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
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Escala
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Manguera
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.grande
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.aduja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.bomba
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.bota
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.escala
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.esponja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.hornacina
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.hornillo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.manguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.mazo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.monton
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.radiosEnMarcha
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.tina
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.trajeEnLaBorda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.sin

/**
 * La cubierta del barco: las tablas que se alejan hacia la proa, la borda de estribor (la diagonal grande, con su cara de
 * dentro y la regala azul), lo de la proa (el hornillo), la capa de sombras y lo que hay encima, a distintas profundidades
 * y tapándose: la bomba del aire (el emblema), la tapa de la escotilla con el objeto del día, la tina de lavar con el mazo,
 * las botas de plomo, la manguera y la cuerda de vida que pasan por la regala, la escala echada, las esponjas negras por
 * limpiar, las ristras a medio ensartar, los sacos, las mantas de la noche y, abajo a la derecha, la hornacina del icono
 * con su lamparilla «que no se apaga nunca».
 */
internal object CubiertaAbismo {
    private val C = ColorAbismo
    private val B = BarcoAbismo

    /** El contorno de la cubierta (el suelo) en el lienzo: de la borda de estribor a la de babor, por la proa. */
    fun suelo(): Path {
        val est = (0..20).map { i -> val vv = 1.0f + (B.RODA - 1.0f) * i / 20f; B.pb(B.estribor(vv) - 0.1f, 0f, vv) }
        val bab = (0..8).map { i -> val vv = B.RODA - (B.RODA - 6.5f) * i / 8f; B.pb(B.babor(vv) + 0.1f, 0f, vv) }
        return poligono(listOf(Offset(-20f, ALTO + 40f)) + est + bab + listOf(Offset(-20f, bab.last().y)))
    }

    /** La borda por dentro (de la cubierta a la regala), en el lienzo. */
    private fun bordaDentro(): Path {
        val pie = (0..24).map { i -> val vv = 0.9f + (B.RODA - 0.9f) * i / 24f; B.pb(B.estribor(vv) - 0.1f, 0f, vv) }
        val alto = (24 downTo 0).map { i -> val vv = 0.9f + (B.RODA - 0.9f) * i / 24f; B.pb(B.estribor(vv) - 0.1f, B.regala(vv), vv) }
        return poligono(pie + alto)
    }

    // ---------------------------------------------------------------- El suelo, la borda, la proa

    fun DrawScope.suelo(v: BarcoAbismo.Vista) {
        val p = v.p
        val cubierta = suelo()
        val madera = lerp(C.MADERA, C.MADERA_GRIS, 0.35f)
        drawPath(cubierta, Brush.verticalGradient(listOf(p.arriba(madera, 0.25f), p.arriba(madera), p.arriba(lerp(madera, C.MADERA, 0.4f))), B.H + 20f, ALTO))
        val az = Azar(Semilla.de("abismo", "tablas"))
        clipPath(cubierta) {
            // Las tablas: cada una con su tono, las juntas de brea y el canto claro, en fuga hacia la proa.
            var u = -3.2f; var i = 0
            while (u < 1.5f) {
                val a = B.pb(u, 0f, 1.0f); val b = B.pb(u, 0f, B.RODA)
                val a2 = B.pb(u + 0.14f, 0f, 1.0f); val b2 = B.pb(u + 0.14f, 0f, B.RODA)
                val tono = lerp(madera, if (i % 3 == 0) C.ARENA else C.MADERA_OSC, az.entre(0.02f, 0.14f))
                drawPath(poligono(a, b, b2, a2), p.arriba(tono).copy(alpha = 0.6f))
                drawLine(p.sombra(C.BREA).copy(alpha = 0.55f), a, b, 0.7f)
                drawLine(p.luz(madera).copy(alpha = 0.3f), a + Offset(0.6f, 0f), b + Offset(0.2f, 0f), 0.35f)
                // Las testas: juntas de través, escalonadas.
                var vv = 1f + az.entre(0f, 2.5f)
                while (vv < B.RODA) { drawLine(p.sombra(C.BREA).copy(alpha = 0.4f), B.pb(u, 0f, vv), B.pb(u + 0.14f, 0f, vv), 0.5f); vv += az.entre(2.4f, 3.6f) }
                u += 0.14f; i++
            }
            // El desgaste: el paso alrededor de la bomba, las manchas de agua y de esponja, la sal.
            mancha(B.SITIO_BOMBA.pie + Offset(-10f, 14f), 46f, 12f, p.luz(lerp(madera, C.ARENA, 0.5f)), 0.35f)
            mancha(B.SITIO_TINA.pie + Offset(30f, -18f), 40f, 10f, p.sombra(lerp(madera, C.BREA, 0.4f)), 0.4f)
            repeat(16) { val q = Offset(az.entre(0f, 210f), az.entre(170f, 285f)); mancha(q, az.entre(4f, 12f), az.entre(1.2f, 3f), p.sombra(lerp(madera, C.MAR_HONDO, 0.3f)), 0.25f) }
            repeat(10) { val q = Offset(az.entre(0f, 210f), az.entre(170f, 285f)); mancha(q, az.entre(5f, 14f), az.entre(1f, 2.5f), p.luz(lerp(madera, C.BLANCO, 0.5f)), 0.22f) }
            // De noche, el relente: la cubierta mojada devuelve un poco de la luna.
            if (v.luna > 0.05f) drawRect(Brush.verticalGradient(listOf(Color.Transparent, B.plata(v).copy(alpha = 0.12f * v.luna)), B.H + 30f, ALTO), Offset(0f, B.H + 30f), Size(ANCHO, ALTO))
        }
        borda(v)
        // La proa: el hornillo, con la olla común.
        val ph = B.pb(B.EJE + 0.3f, 0f, 10.2f)
        hornillo(p, ph, B.k(B.cam(B.EJE + 0.3f, 10.2f).y), EscenaAbismo.hornillo(v.m))
    }

    /** La borda de estribor por dentro, con sus barraganetes, los imbornales y la regala azul por encima. */
    private fun DrawScope.borda(v: BarcoAbismo.Vista) {
        val p = v.p
        val cara = bordaDentro()
        val t = v.sol.t(V3(-kotlin.math.cos(B.GIRO), 0f, -kotlin.math.sin(B.GIRO)))
        val pintura = lerp(C.BLANCO, C.AZUL, 0.3f)
        drawPath(cara, p.por(pintura, t))
        val az = Azar(Semilla.de("abismo", "borda"))
        clipPath(cara) {
            // Las tablas de la borda, a lo largo, y los barraganetes que la sostienen.
            for (h in listOf(0.24f, 0.48f)) drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva((0..16).map { i -> val vv = 0.9f + (B.RODA - 0.9f) * i / 16f; B.pb(B.estribor(vv) - 0.1f, h * B.regala(vv) / B.REGALA, vv) }), p.sombra(pintura).copy(alpha = 0.6f), style = Stroke(0.6f))
            var vv = 1.2f
            while (vv < B.RODA - 0.6f) {
                val a = B.pb(B.estribor(vv) - 0.12f, 0f, vv); val b = B.pb(B.estribor(vv) - 0.12f, B.regala(vv), vv); val kk = B.k(B.cam(B.estribor(vv), vv).y)
                drawLine(p.por(lerp(pintura, C.MADERA_GRIS, 0.3f), t + 0.25f), a, b, 0.09f * kk)
                drawLine(p.sombra(pintura), a + Offset(0.05f * kk, 0f), b + Offset(0.05f * kk, 0f), 0.025f * kk)
                // El imbornal al pie, con su chorrera de óxido.
                val im = B.pb(B.estribor(vv + 0.45f) - 0.1f, 0.06f, vv + 0.45f)
                drawOval(p.sombra(C.BREA), Offset(im.x - 0.06f * kk, im.y - 0.04f * kk), Size(0.12f * kk, 0.06f * kk))
                drawLine(p.medio(lerp(C.COBRE_OSC, pintura, 0.4f)).copy(alpha = 0.35f), im, im + Offset(0f, -0.01f * kk), 0.04f * kk)
                vv += 1.1f
            }
            // La pintura saltada y la sal.
            repeat(18) { val q = Offset(az.entre(100f, 330f), az.entre(120f, 285f)); mancha(q, az.entre(2f, 7f), az.entre(1f, 2.5f), p.luz(lerp(pintura, C.BLANCO, 0.6f)), 0.35f) }
        }
        // La regala: la tabla de arriba, vista desde arriba, de madera lavada, con su canto exterior azul.
        val dentro = (0..24).map { i -> val vv = 0.85f + (B.RODA - 0.85f) * i / 24f; B.pb(B.estribor(vv) - 0.12f, B.regala(vv), vv) }
        val fuera = (24 downTo 0).map { i -> val vv = 0.85f + (B.RODA - 0.85f) * i / 24f; B.pb(B.estribor(vv) + 0.06f, B.regala(vv) + 0.02f, vv) }
        drawPath(poligono(dentro + fuera), p.arriba(C.MADERA_GRIS))
        drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(fuera.reversed()), p.por(C.AZUL, 0.4f), style = Stroke(1.6f))
        drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(dentro), p.luz(C.MADERA_GRIS).copy(alpha = 0.7f), style = Stroke(0.6f))
        if (v.luna > 0.05f) drawPath(io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva(dentro), B.plata(v).copy(alpha = 0.5f * v.luna), style = Stroke(1.2f))
    }

    // ---------------------------------------------------------------- Las cosas de la cubierta

    fun DrawScope.cosas(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p; val sol = v.sol
        val cosas = ArrayList<Cosa>()
        val neg = EscenaAbismo.esponjasNegras(m)
        val carga = EscenaAbismo.carga(m)
        // La tapa de la escotilla, con el objeto chico del día, las ristras a medio ensartar y, a su hora, el farol.
        val se = B.SITIO_ESCOTILLA
        val esqEscotilla = esquinas(-B.ESCOTILLA_ANCHO / 2f, B.ESCOTILLA_ANCHO / 2f, 0f, B.ESCOTILLA_ALTO, -B.ESCOTILLA_FONDO / 2f, B.ESCOTILLA_FONDO / 2f)
        cosas += Cosa(sombraDe(se, sol, esqEscotilla), se.pie) {
            escotilla(v)
            if (EscenaAbismo.ensartando(m)) ensartando(v)
            if (EscenaAbismo.fase(m) == EscenaAbismo.Fase.CORRO) corro(v)
        }
        // La bomba del aire, con la manguera.
        val sb = B.SITIO_BOMBA
        cosas += Cosa(sombraDe(sb, sol, PiezasAbismo.bombaEsquinas()), sb.pie) {
            if (EscenaAbismo.manguera(m) == Manguera.ADUJADA) aduja(v.pincel(sb.pie), B.pb(1.0f, 0f, 2.95f), 0.32f * sb.k, 0.11f * sb.k, 4, 0.05f * sb.k, C.BREA, manguera = true)
            bomba(v.pincel(sb.p(0f, 0.6f, 0f)), sb, sol, EscenaAbismo.buzoAbajo(m), 0.75f, Semilla.de("abismo", "bomba"))
            if (EscenaAbismo.manguera(m) == Manguera.AL_AGUA) mangueraEnCubierta(v)
            sobreElSoporte(m, v.pincel(B.soporte()), B.soporte(), B.escalaEn(sb.z0))
        }
        // Las botas de plomo, una de pie y otra caída (el día que son el objeto, las pinta el objeto).
        if (ObjetosAbismo.elCuadroPinta(m, "botas")) cosas += Cosa(sombraDe(B.SITIO_BOTA, sol, esquinas(-0.08f, 0.08f, 0f, 0.42f, -0.17f, 0.13f)), B.SITIO_BOTA.pie) { bota(v.pincel(B.SITIO_BOTA.pie), B.SITIO_BOTA.pie, B.SITIO_BOTA.k, -1f, 3) }
        if (ObjetosAbismo.elCuadroPinta(m, "botas")) cosas += Cosa(sombraDe(B.SITIO_BOTA2, sol, esquinas(-0.08f, 0.08f, 0f, 0.42f, -0.17f, 0.13f)), B.SITIO_BOTA2.pie) { bota(v.pincel(B.SITIO_BOTA2.pie), B.SITIO_BOTA2.pie, B.SITIO_BOTA2.k, 1f, 4) }
        // La tina de lavar, con las esponjas dentro y el mazo recostado; cortada por el borde de abajo.
        val st = B.SITIO_TINA
        cosas += Cosa(sombraDe(st, sol, PiezasAbismo.tinaEsquinas(0.42f, 0.5f)), st.pie) {
            tina(v.pincel(st.pie), st, sol, 0.42f, 0.5f, if (neg > 0f) 5 else 2, neg > 0.3f, Semilla.de("abismo", "tina"))
            mazo(v.pincel(st.pie), B.pb(-0.05f, 0f, 2.55f), st.p(0.36f, 0.5f, -0.1f), 0.11f * st.k)
        }
        // Las esponjas negras recién sacadas, por limpiar.
        if (neg > 0.02f) {
            val pie = B.pb(-0.05f, 0f, 3.55f); val kk = B.k(B.cam(-0.05f, 3.55f).y)
            cosas += Cosa(Path().apply { addOval(androidx.compose.ui.geometry.Rect(pie, 0.3f * kk)) }, pie) { monton(v.pincel(pie), pie, 0.62f * kk * (0.6f + 0.4f * neg), (4 + 9 * neg).toInt(), Semilla.de("abismo", "negras"), false) }
        }
        // Los sacos de esponjas secas, contra la borda (la bodega se va llenando).
        val sacos = when (carga) { EscenaAbismo.Carga.POCAS -> 0; EscenaAbismo.Carga.RACIMOS -> 2; EscenaAbismo.Carga.LLENO -> 4 }
        for (i in 0 until sacos) {
            val vv = 5.0f + 0.62f * i; val u = B.estribor(vv) - 0.42f - 0.08f * (i % 2)
            val pie = B.pb(u, 0f, vv); val kk = B.k(B.cam(u, vv).y)
            cosas += Cosa(Path().apply { addOval(androidx.compose.ui.geometry.Rect(pie.x - 0.3f * kk, pie.y - 0.7f * kk, pie.x + 0.3f * kk, pie.y)) }, pie) { saco(v.pincel(pie), pie, kk, Semilla.de("abismo", "saco", i)) }
        }
        // Las mantas tendidas para dormir, con el rollo de cuerda de almohada.
        if (EscenaAbismo.mantas(m)) {
            val pie = B.pb(0.35f, 0f, 2.7f)
            cosas += Cosa(Path(), pie) { mantas(v) }
        }
        // El objeto grande del día, recostado en la bomba.
        grande(m, v.pincel(B.PIE_GRANDE), B.PIE_GRANDE, B.APOYO_GRANDE, B.escalaEn(B.cam(0.1f, 3.05f).y))?.let { cosas += it }

        // La capa de sombras: la de cada cosa, la de la borda (con el sol de estribor) y la del aparejo.
        val orden = cosas.sortedBy { it.pie.y }
        capaSombra(v.r) { c ->
            clipPath(suelo()) {
                for (cosa in orden) sombra(cosa.forma, c)
                sombraDeLaBorda(v, c)
                with(AparejoAbismo) { sombras(v, c) }
            }
        }
        // La luz de noche en la cubierta: el charco de la lamparilla y el del farol.
        if (v.l.noche > 0.2f) {
            resplandor(B.ICONO + Offset(-10f, 30f), 70f, 0.5f * v.l.noche, C.LUMBRE)
            if (v.lu.farol > 0.02f) { resplandor(B.FAROL + Offset(0f, 70f), 120f, 0.7f * v.l.noche * v.lu.farol, C.LUMBRE); resplandor(B.FAROL, 45f, 0.5f * v.l.noche * v.lu.farol, C.LUMBRE) }
        }
        for (cosa in orden) cosa.pintar(this)
        // Lo que pasa por la regala: la escala echada y la cuerda de vida; y la hornacina del icono en su candelero.
        escalaYCuerda(v)
        // El traje de lona, doblado a secar sobre la regala (el día que es el objeto, lo pinta el objeto).
        if (ObjetosAbismo.elCuadroPinta(m, "escafandra")) {
            val va = 2.25f; val vb = 3.05f
            val a = B.pb(B.estribor(va) - 0.04f, B.regala(va) + 0.04f, va); val b = B.pb(B.estribor(vb) - 0.04f, B.regala(vb) + 0.04f, vb)
            trajeEnLaBorda(v.pincel(a), a, b, B.k(B.cam(B.estribor(2.6f), 2.6f).y), Semilla.de("abismo", "traje"), mojado = if (EscenaAbismo.fase(m) == EscenaAbismo.Fase.INMERSION) 0.9f else 0.4f)
        }
        icono(v)
    }

    /** La sombra que la borda de estribor echa en la cubierta cuando el sol viene de estribor (por la mañana): una franja a lo largo. */
    private fun DrawScope.sombraDeLaBorda(v: BarcoAbismo.Vista, c: Color) {
        val sol = v.sol
        if (sol.sx > -0.05f) return
        val pts = (0..16).map { i -> val vv = 1.0f + (B.RODA - 1.0f) * i / 16f; B.cam(B.estribor(vv) - 0.1f, vv) }
        val pie = pts.map { B.pc(it.x, 0f, it.y) }
        val cae = (16 downTo 0).map { i -> val vv = 1.0f + (B.RODA - 1.0f) * i / 16f; val q = B.cam(B.estribor(vv) - 0.1f, vv); val s = B.alSuelo(sol, q.x, B.regala(vv), q.y); B.pc(s.x, 0f, s.y) }
        drawPath(poligono(pie + cae), c)
    }

    /** La tapa de la escotilla: una caja baja de tablas, con su brazola, las argollas de hierro y el canto gastado. */
    private fun DrawScope.escotilla(v: BarcoAbismo.Vista) {
        val s = B.SITIO_ESCOTILLA; val p = v.pincel(s.pie); val e = s.escala(0f, 0f)
        val az = Azar(Semilla.de("abismo", "escotilla"))
        for (cara in visibles(s, carasDeCaja(-B.ESCOTILLA_ANCHO / 2f, B.ESCOTILLA_ANCHO / 2f, 0f, B.ESCOTILLA_ALTO, -B.ESCOTILLA_FONDO / 2f, B.ESCOTILLA_FONDO / 2f))) {
            val t = luzDe(s, v.sol, cara); val f = forma(s, cara)
            val madera = lerp(C.MADERA_GRIS, C.MADERA, if (cara.arriba) 0.2f else 0.5f)
            drawPath(f, p.por(madera, t))
            clipPath(f) {
                val tablas = if (cara.arriba) 5 else 2
                for (k in 1 until tablas) linea(s, cara, 0f, k / tablas.toFloat(), 1f, k / tablas.toFloat(), p.sombra(C.BREA).copy(alpha = 0.6f), (0.012f * e).coerceAtLeast(0.4f))
                repeat(5) { val u = az.entre(0.05f, 0.9f); val vv = az.entre(0.1f, 0.9f); linea(s, cara, u, vv, u + az.entre(0.05f, 0.15f), vv, p.luz(lerp(madera, C.BLANCO, 0.4f)).copy(alpha = 0.4f), (0.006f * e).coerceAtLeast(0.3f), StrokeCap.Round) }
            }
            drawPath(f, p.sombra(C.BREA).copy(alpha = 0.5f), style = Stroke((0.008f * e).coerceAtLeast(0.4f)))
            if (cara.arriba) for (u in listOf(0.2f, 0.8f)) { val q = s.p(cara.en(u, 0.15f)); drawOval(p.sombra(C.PLOMO), Offset(q.x - 0.035f * e, q.y - 0.015f * e), Size(0.07f * e, 0.03f * e), style = Stroke((0.012f * e).coerceAtLeast(0.4f))) }
        }
    }

    /** Las ristras a medio ensartar sobre la tapa: una cuerda con unas pocas esponjas limpias y la aguja de madera. */
    private fun DrawScope.ensartando(v: BarcoAbismo.Vista) {
        val s = B.SITIO_ESCOTILLA; val p = v.pincel(s.pie); val e = s.escala(0f, 0f)
        val a = s.p(-0.42f, B.ESCOTILLA_ALTO + 0.03f, 0.12f); val b = s.p(-0.02f, B.ESCOTILLA_ALTO + 0.03f, -0.2f)
        cabo(p, listOf(a, (a + b) * 0.5f + Offset(0f, 0.02f * e), b, b + Offset(0.1f * e, 0.09f * e), b + Offset(0.12f * e, 0.28f * e)), 0.012f * e)
        for (i in 0 until 5) esponja(p, a + (b - a) * (0.12f + 0.18f * i) + Offset(0f, -0.04f * e), 0.055f * e, Semilla.de("abismo", "ensarta", i), true)
        drawLine(p.luz(C.MADERA), b + Offset(0.02f * e, -0.02f * e), b + Offset(0.16f * e, -0.06f * e), 0.012f * e, StrokeCap.Round)
    }

    /** El corro del farol: la tabla de las cartas y el comboloi dejado encima. */
    private fun DrawScope.corro(v: BarcoAbismo.Vista) {
        val s = B.SITIO_ESCOTILLA; val p = v.pincel(s.pie); val e = s.escala(0f, 0f)
        val c0 = s.p(0.05f, B.ESCOTILLA_ALTO + 0.01f, 0.1f)
        for (i in 0 until 4) {
            val q = c0 + Offset((i - 1.5f) * 0.06f * e, (i % 2) * 0.01f * e)
            drawRect(p.luz(C.BLANCO), Offset(q.x - 0.025f * e, q.y - 0.018f * e), Size(0.05f * e, 0.036f * e))
            drawRect(p.medio(if (i % 2 == 0) C.ROJO else C.AZUL), Offset(q.x - 0.01f * e, q.y - 0.006f * e), Size(0.02f * e, 0.012f * e))
        }
    }

    /** La manguera del aire: de la boca de la bomba, por la cubierta, a la regala, tensa, y por encima de ella al agua. */
    private fun DrawScope.mangueraEnCubierta(v: BarcoAbismo.Vista) {
        val p = v.p; val sb = B.SITIO_BOMBA
        val e = sb.escala(0f, 0f)
        val pts = listOf(sb.p(0.16f, 0.12f, -0.16f), sb.p(0.25f, 0.04f, -0.35f), B.pb(1.05f, 0.04f, 3.25f), B.pb(B.estribor(B.BAJADA_V) - 0.12f, 0.35f, B.BAJADA_V), B.pb(B.estribor(B.BAJADA_V) - 0.02f, B.regala(B.BAJADA_V) + 0.05f, B.BAJADA_V), B.pb(B.estribor(B.BAJADA_V) + 0.12f, B.regala(B.BAJADA_V) - 0.15f, B.BAJADA_V + 0.05f))
        manguera(p, pts, 0.055f * e)
    }

    /** La escala echada por la borda (sus ganchos en la regala) y la cuerda de vida, tensa, amarrada a una cabilla. */
    private fun DrawScope.escalaYCuerda(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p
        val ve = 4.5f; val kk = B.k(B.cam(B.estribor(ve), ve).y)
        if (EscenaAbismo.escala(m) == Escala.ECHADA) {
            val a0 = B.pb(B.estribor(ve) - 0.08f, B.regala(ve) + 0.03f, ve - 0.2f); val a1 = B.pb(B.estribor(ve) - 0.08f, B.regala(ve) + 0.03f, ve + 0.25f)
            val b0 = B.pb(B.estribor(ve) + 0.2f, B.regala(ve) - 0.5f, ve - 0.2f); val b1 = B.pb(B.estribor(ve) + 0.2f, B.regala(ve) - 0.5f, ve + 0.25f)
            escala(p, a0, a1, b0, b1, 2, 0.04f * kk)
            for (q in listOf(a0, a1)) drawLine(p.sombra(C.PLOMO), q, q + Offset(-0.06f * kk, 0.1f * kk), 0.03f * kk, StrokeCap.Round)
        } else {
            // Recogida: enrollada junto a la borda.
            val pie = B.pb(B.estribor(ve) - 0.35f, 0f, ve)
            for (i in 0 until 4) drawLine(p.medio(C.MADERA), pie + Offset(-0.18f * kk, -0.04f * kk * i), pie + Offset(0.18f * kk, -0.04f * kk * i - 0.02f * kk), 0.035f * kk, StrokeCap.Round)
            drawOval(p.sombra(C.CANAMO), Offset(pie.x - 0.2f * kk, pie.y - 0.2f * kk), Size(0.4f * kk, 0.2f * kk), style = Stroke(0.02f * kk))
        }
        if (EscenaAbismo.buzoAbajo(m)) {
            // La cuerda de vida: de la cabilla de la regala, por encima, al agua; con su marca roja.
            val vc = B.BAJADA_V + 0.45f; val k2 = B.k(B.cam(B.estribor(vc), vc).y)
            val a = B.pb(B.estribor(vc) - 0.1f, B.regala(vc) + 0.02f, vc)
            cabo(p, listOf(B.pb(B.estribor(vc) - 0.3f, 0.5f, vc), a, B.pb(B.estribor(vc) + 0.15f, B.regala(vc) - 0.2f, vc + 0.05f)), 0.025f * k2)
            drawLine(p.medio(C.ROJO), a + Offset(-0.02f * k2, 0.02f * k2), a + Offset(0.03f * k2, -0.02f * k2), 0.04f * k2, StrokeCap.Round)
        }
    }

    /** La hornacina del icono en su candelero, sobre la regala de la aleta, abajo a la derecha: la luz cálida de la noche. */
    private fun DrawScope.icono(v: BarcoAbismo.Vista) {
        val m = v.m; val c = B.ICONO; val k = B.K_ICONO; val p = v.pincel(c)
        // La sombra que la hornacina echa en la borda, del lado contrario a la luz.
        drawRect(p.sombra(C.AZUL).copy(alpha = 0.35f), Offset(c.x - 0.1f * k + (if (p.solALaDerecha) -0.05f else 0.05f) * k, c.y - 0.2f * k), Size(0.26f * k, 0.42f * k))
        hornacina(p, c, k, v.lu.lamparilla, mira = -0.6f, luto = EscenaAbismo.luto(m), velas = EscenaAbismo.dormicion(m) || EscenaAbismo.pascua(m), huevos = EscenaAbismo.pascua(m))
    }

    /** Un saco de esponjas secas: la arpillera abombada por los bultos, atada por arriba. */
    private fun DrawScope.saco(p: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel, pie: Offset, k: Float, semilla: Long) {
        val az = Azar(semilla); val w = 0.32f * k * az.entre(0.9f, 1.1f); val h = 0.62f * k * az.entre(0.85f, 1.05f)
        val forma = lazo(listOf(Offset(pie.x - w, pie.y), Offset(pie.x - w * 1.08f, pie.y - h * 0.45f), Offset(pie.x - w * 0.7f, pie.y - h * 0.85f), Offset(pie.x - w * 0.15f, pie.y - h), Offset(pie.x + w * 0.2f, pie.y - h * 0.95f), Offset(pie.x + w * 0.8f, pie.y - h * 0.8f), Offset(pie.x + w * 1.06f, pie.y - h * 0.4f), Offset(pie.x + w * 0.95f, pie.y)))
        val ld = if (p.solALaDerecha) 1f else -1f
        drawPath(forma, Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(C.LONA) else p.luz(C.LONA), p.medio(C.LONA), if (ld > 0) p.luz(C.LONA) else p.sombra(C.LONA)), pie.x - w, pie.x + w))
        clipPath(forma) { repeat(5) { val q = Offset(pie.x + az.entre(-0.7f, 0.7f) * w, pie.y - az.entre(0.2f, 0.8f) * h); drawArc(p.sombra(C.LONA).copy(alpha = 0.5f), 20f, 140f, false, Offset(q.x - w * 0.3f, q.y - w * 0.2f), Size(w * 0.6f, w * 0.4f), style = Stroke(0.5f)) } }
        drawLine(p.sombra(C.CANAMO), Offset(pie.x - w * 0.2f, pie.y - h * 0.92f), Offset(pie.x + w * 0.25f, pie.y - h * 0.9f), 0.03f * k)
    }

    /** Las mantas de dormir en la popa: una tendida y arrugada, con el rollo de cuerda de almohada, y otra enrollada. */
    private fun DrawScope.mantas(v: BarcoAbismo.Vista) {
        val az = Azar(Semilla.de("abismo", "mantas"))
        val q = listOf(B.pb(0.35f, 0.02f, 2.75f), B.pb(0.95f, 0.02f, 2.7f), B.pb(1.0f, 0.04f, 3.45f), B.pb(0.3f, 0.04f, 3.55f))
        val p = v.pincel(q[0])
        val lana = lerp(C.ROJO, C.MADERA_OSC, 0.45f)
        val f = lazo(q)
        drawPath(f, Brush.linearGradient(listOf(p.por(lana, 0.4f), p.por(lana, -0.3f)), q[3], q[1]))
        clipPath(f) {
            repeat(6) { val a = q[0] + (q[1] - q[0]) * az.real() + (q[3] - q[0]) * az.real(); drawArc(p.sombra(lana).copy(alpha = 0.6f), 0f, 160f, false, Offset(a.x - 7f, a.y - 2.5f), Size(14f, 5f), style = Stroke(1.1f)) }
            for (t in listOf(0.18f, 0.82f)) drawLine(p.luz(lerp(lana, C.BLANCO, 0.5f)).copy(alpha = 0.55f), q[0] + (q[3] - q[0]) * t, q[1] + (q[2] - q[1]) * t, 1.2f)
        }
        aduja(p, q[3] + (q[2] - q[3]) * 0.5f + Offset(0f, 2f), 8f, 2.8f, 3, 1.3f, C.CANAMO)
        // La otra, enrollada y atada, junto a la bomba.
        val r0 = B.pb(-0.1f, 0.1f, 3.25f); val r1 = B.pb(0.45f, 0.1f, 3.15f); val g = 0.2f * B.k(B.cam(0.2f, 3.2f).y)
        val lana2 = lerp(C.AZUL, C.LONA, 0.45f)
        drawLine(p.sombra(lana2), r0, r1, g, StrokeCap.Round)
        drawLine(p.medio(lana2), r0 + Offset(0f, -g * 0.15f), r1 + Offset(0f, -g * 0.15f), g * 0.6f, StrokeCap.Round)
        drawLine(p.luz(lana2).copy(alpha = 0.6f), r0 + Offset(0f, -g * 0.3f), r1 + Offset(0f, -g * 0.3f), g * 0.15f, StrokeCap.Round)
        for (t in listOf(0.25f, 0.75f)) { val c = r0 + (r1 - r0) * t; drawLine(p.sombra(C.CANAMO), c + Offset(0f, -g * 0.5f), c + Offset(0f, g * 0.5f), g * 0.12f) }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: BarcoAbismo.Vista, reloj: Float) {
        val m = v.m
        if (EscenaAbismo.buzoAbajo(m)) radiosEnMarcha(v.pincel(B.SITIO_BOMBA.pie), B.SITIO_BOMBA, reloj)
        // La llama de la lamparilla, que tiembla (y la vela, en su día).
        val c = B.ICONO; val k = B.K_ICONO
        val vaso = Offset(c.x, c.y + 0.28f * k * 0.34f)
        llama(vaso + Offset(0f, -0.22f * k * 0.06f), 0.05f * k * (0.9f + 0.1f * sin(reloj * 7f)), reloj, 3)
        if (v.lu.farol > 0.02f) llama(B.FAROL + Offset(0f, 0.04f * B.K_FAROL), 0.07f * B.K_FAROL, reloj, 5)
        if (EscenaAbismo.hornillo(m)) { val ph = B.pb(B.EJE + 0.3f, 0f, 10.2f); val kk = B.k(B.cam(B.EJE + 0.3f, 10.2f).y); humo(v.l, ph + Offset(0f, -0.6f * kk), 60f, 14f, reloj, viento = -0.4f - 0.6f * m.viento, oscuro = 0.15f, bocanadas = 6) }
    }
}
