package io.github.ruddy2003.vestigium.ui.escenas

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
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Escala
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Manguera
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.grande
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.aduja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.bomba
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.bota
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cabo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.cuboDeMadera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.escala
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.esponja
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.hornacina
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.hornillo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.manguera
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.mazo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.monton
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.radiosEnMarcha
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.redDeEsponjas
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.tina
import io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.trajeEnLaBorda
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.haloDeBruma
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * La cubierta del barco: las tablas que se alejan hacia la proa, la borda de estribor (la diagonal grande, con su
 * tablazón, sus barraganetes, la regala gastada y el cabillero), lo de la proa (el hornillo), la capa de sombras y lo
 * que hay encima, a distintas profundidades y tapándose. Es un sitio de trabajo, y cambia con el horario:
 * - **a las 7:00 se bucea:** la bomba gira, la manguera y la cuerda de vida pasan por la regala, la escala está echada,
 *   la cubierta mojada junto a ella; suben las primeras esponjas negras; un solo traje a secar;
 * - **a las 13:00 se limpian esponjas:** el montón negro por limpiar, el de las limpias, la tina, el mazo, el cubo, las
 *   ristras a medio ensartar sobre la escotilla, la red colgada de la borda, dos trajes a secar;
 * - **a las 21:00 se duerme:** las mantas tendidas, la cubierta despejada, el farol bajo y la lamparilla del icono.
 * Abajo a la derecha, en su candelero, la hornacina del icono con su lamparilla «que no se apaga nunca».
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

    /** Un punto de la cara de dentro de la borda: a [vv] metros hacia proa y a la fracción [f] de su alto (0, la cubierta; 1, la regala). */
    private fun enLaBorda(vv: Float, f: Float, dentro: Float = 0.1f): Offset = B.pb(B.estribor(vv) - dentro, f * B.regala(vv), vv)
    private fun kEn(vv: Float): Float = B.k(B.cam(B.estribor(vv), vv).y)

    // ---------------------------------------------------------------- El suelo, la borda, la proa

    fun DrawScope.suelo(v: BarcoAbismo.Vista) {
        val p = v.p; val m = v.m
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
                val tono = lerp(madera, if (i % 3 == 0) C.ARENA else C.MADERA_OSC, az.entre(0.02f, 0.16f))
                drawPath(poligono(a, b, b2, a2), p.arriba(tono).copy(alpha = 0.6f))
                drawLine(p.sombra(C.BREA).copy(alpha = 0.55f), a, b, 0.7f)
                drawLine(p.luz(madera).copy(alpha = 0.3f), a + Offset(0.6f, 0f), b + Offset(0.2f, 0f), 0.35f)
                // Las testas: juntas de través, escalonadas, con sus dos clavos.
                var vv = 1f + az.entre(0f, 2.5f)
                while (vv < B.RODA) {
                    val t0 = B.pb(u, 0f, vv); val t1 = B.pb(u + 0.14f, 0f, vv)
                    drawLine(p.sombra(C.BREA).copy(alpha = 0.4f), t0, t1, 0.5f)
                    if (vv < 5f) for (s in listOf(0.3f, 0.7f)) drawCircle(p.sombra(C.BREA).copy(alpha = 0.45f), 0.012f * B.k(B.cam(u, vv).y), t0 + (t1 - t0) * s + Offset(0f, -0.6f))
                    vv += az.entre(2.4f, 3.6f)
                }
                u += 0.14f; i++
            }
            // La veta y los nudos de las tablas de cerca.
            repeat(40) {
                val uu = az.entre(-1.5f, 1.2f); val vv = az.entre(1.6f, 4.5f); val q = B.pb(uu, 0f, vv); val kk = B.k(B.cam(uu, vv).y)
                val q2 = B.pb(uu + az.entre(-0.01f, 0.01f), 0f, vv + az.entre(0.25f, 0.7f))
                drawLine(p.sombra(madera).copy(alpha = 0.22f), q, q2, (0.008f * kk).coerceAtLeast(0.3f), StrokeCap.Round)
                if (it % 8 == 0) drawOval(p.sombra(lerp(madera, C.BREA, 0.3f)).copy(alpha = 0.35f), Offset(q.x - 0.03f * kk, q.y - 0.008f * kk), Size(0.06f * kk, 0.016f * kk))
            }
            // El trancanil: la canal junto a la borda, siempre húmeda, con la sal y el verdín.
            val canal = (0..20).map { i -> val vv = 1.0f + (B.RODA - 1.0f) * i / 20f; B.pb(B.estribor(vv) - 0.1f, 0f, vv) }
            val canalDentro = (20 downTo 0).map { i -> val vv = 1.0f + (B.RODA - 1.0f) * i / 20f; B.pb(B.estribor(vv) - 0.32f, 0f, vv) }
            drawPath(poligono(canal + canalDentro), p.sombra(lerp(madera, C.MAR_HONDO, 0.3f)).copy(alpha = 0.45f))
            drawPath(curva(canalDentro.reversed()), p.luz(lerp(madera, C.BLANCO, 0.4f)).copy(alpha = 0.3f), style = Stroke(0.6f))
            // El desgaste: el paso alrededor de la bomba, las manchas de agua y de esponja, la sal.
            mancha(B.SITIO_BOMBA.pie + Offset(-10f, 16f), 50f, 13f, p.luz(lerp(madera, C.ARENA, 0.5f)), 0.32f)
            mancha(B.SITIO_TINA.pie + Offset(30f, -18f), 40f, 10f, p.sombra(lerp(madera, C.BREA, 0.4f)), 0.4f)
            repeat(16) { val q = Offset(az.entre(0f, 210f), az.entre(170f, 285f)); mancha(q, az.entre(4f, 12f), az.entre(1.2f, 3f), p.sombra(lerp(madera, C.MAR_HONDO, 0.3f)), 0.22f) }
            repeat(10) { val q = Offset(az.entre(0f, 210f), az.entre(170f, 285f)); mancha(q, az.entre(5f, 14f), az.entre(1f, 2.5f), p.luz(lerp(madera, C.BLANCO, 0.5f)), 0.2f) }
            // El rastro de las esponjas negras: el agua sucia que chorrea y los trozos de piel negra pisados.
            val neg = EscenaAbismo.esponjasNegras(m)
            if (neg > 0.02f || EscenaAbismo.buzoAbajo(m)) {
                mancha(B.pb(1.0f, 0f, 4.15f), 46f * (0.6f + 0.4f * neg), 11f, p.sombra(lerp(madera, C.BREA, 0.55f)), 0.45f)
                val az2 = Azar(Semilla.de("abismo", "pellejos"))
                repeat(18) { val q = B.pb(az2.entre(0.3f, 1.25f), 0f, az2.entre(3.0f, 4.8f)); drawOval(p.medio(C.ESPONJA_VIVA).copy(alpha = 0.7f * (0.4f + neg)), Offset(q.x - 1f, q.y - 0.4f), Size(az2.entre(1.2f, 2.6f), az2.entre(0.6f, 1.1f))) }
            }
            // La mañana de inmersiones, el charco junto a la escala: el agua que traen la red y el buzo, que devuelve el cielo.
            if (EscenaAbismo.buzoAbajo(m) || EscenaAbismo.fase(m) == Fase.ESPONJAS) {
                val c = B.pb(1.05f, 0f, 4.6f); val kk = kEn(4.6f)
                drawOval(lerp(p.arriba(madera), p.sombra(C.MAR_HONDO), 0.45f).copy(alpha = 0.55f), Offset(c.x - 0.55f * kk, c.y - 0.08f * kk), Size(1.1f * kk, 0.18f * kk))
                drawOval(lerp(v.l.medio, Color.White, 0.25f).copy(alpha = 0.35f * (1f - v.l.noche)), Offset(c.x - 0.38f * kk, c.y - 0.06f * kk), Size(0.55f * kk, 0.08f * kk))
            }
            // De noche, el relente: la cubierta mojada brilla hacia la luna en franjas que parten las juntas.
            if (v.luna > 0.05f) {
                val lx = v.l.lunaX * ANCHO
                mancha(Offset(90f + (lx - 90f) * 0.35f, 196f), 110f, 13f, EscenaAbismo.LUZ_DE_LUNA, 0.3f * v.luna)
                mancha(Offset(70f + (lx - 70f) * 0.2f, 250f), 130f, 22f, EscenaAbismo.LUZ_DE_LUNA, 0.18f * v.luna)
                // Y las juntas de las tablas, mojadas, con un filo de plata hacia la luna.
                var uu = -1.6f
                while (uu < 1.2f) { drawLine(B.plata(v).copy(alpha = 0.16f * v.luna), B.pb(uu + 0.02f, 0f, 2.6f), B.pb(uu + 0.02f, 0f, 7f), 0.5f); uu += 0.14f }
            }
        }
        borda(v)
        // La proa: el hornillo, con la olla común.
        val ph = B.pb(B.EJE + 0.3f, 0f, 10.2f)
        hornillo(p, ph, B.k(B.cam(B.EJE + 0.3f, 10.2f).y), EscenaAbismo.hornillo(v.m))
    }

    /**
     * La borda de estribor por dentro: pintada de blanco con su traca alta azul, la tablazón con sus costuras, los
     * barraganetes que la sostienen con su sombra, los imbornales al pie, los pernos con su chorrera de óxido, la pintura
     * saltada que enseña la madera, la sal arriba y el verdín abajo; el cabillero con sus cabillas y sus adujas colgadas; y
     * encima la regala, de madera lavada, con el canto de fuera azul y desconchado.
     */
    private fun DrawScope.borda(v: BarcoAbismo.Vista) {
        val p = v.p
        val cara = bordaDentro()
        val t = v.sol.t(V3(-cos(B.GIRO), 0f, -sin(B.GIRO)))
        val pintura = lerp(C.BLANCO, C.AZUL, 0.08f)
        drawPath(cara, Brush.verticalGradient(listOf(p.por(pintura, t + 0.1f), p.por(pintura, t), p.por(lerp(pintura, C.MADERA_GRIS, 0.25f), t - 0.15f)), B.H + 30f, ALTO))
        val az = Azar(Semilla.de("abismo", "borda"))
        clipPath(cara) {
            // La traca alta, azul, bajo la regala.
            val azulArriba = (0..24).map { i -> val vv = 0.9f + (B.RODA - 0.9f) * i / 24f; enLaBorda(vv, 1f) }
            val azulAbajo = (24 downTo 0).map { i -> val vv = 0.9f + (B.RODA - 0.9f) * i / 24f; enLaBorda(vv, 0.8f) }
            drawPath(poligono(azulArriba + azulAbajo), p.por(C.AZUL, t))
            // Las costuras de la tablazón: una raya de sombra y, debajo, el canto de luz de la tabla.
            for (f in listOf(0.27f, 0.54f, 0.8f)) {
                val linea = (0..24).map { i -> val vv = 0.9f + (B.RODA - 0.9f) * i / 24f; enLaBorda(vv, f) }
                drawPath(curva(linea), p.sombra(pintura).copy(alpha = 0.65f), style = Stroke(0.7f))
                drawPath(curva(linea.map { it + Offset(0f, 0.9f) }), p.luz(pintura).copy(alpha = 0.35f), style = Stroke(0.5f))
            }
            // Los barraganetes: costillas que sobresalen, con su cara de luz, su canto y la sombra que echan en la tabla.
            var vv = 1.15f
            while (vv < B.RODA - 0.6f) {
                val kk = kEn(vv); val a = enLaBorda(vv, 0f, 0.14f); val b = enLaBorda(vv, 0.8f, 0.14f)
                val ancho = 0.07f * kk
                val lado = if (v.sol.sz > 0f && v.sol.sx > 0f) 1f else -1f
                drawLine(p.sombra(pintura).copy(alpha = 0.4f), a + Offset(ancho * 0.9f * lado, 0f), b + Offset(ancho * 0.9f * lado, 0f), ancho * 0.9f)
                drawLine(p.por(pintura, t + 0.2f), a, b, ancho)
                drawLine(p.luz(pintura).copy(alpha = 0.5f), a + Offset(-ancho * 0.35f, 0f), b + Offset(-ancho * 0.35f, 0f), ancho * 0.25f)
                // El perno de arriba, con su chorrera de óxido.
                val perno = enLaBorda(vv, 0.7f, 0.14f)
                drawCircle(p.medio(C.COBRE_OSC), 0.012f * kk + 0.2f, perno)
                if (az.real() > 0.3f) drawLine(p.medio(lerp(C.COBRE_OSC, C.COBRE, 0.3f)).copy(alpha = 0.35f), perno, perno + Offset(az.entre(-0.3f, 0.3f), az.entre(0.12f, 0.3f) * kk), 0.02f * kk, StrokeCap.Round)
                // El imbornal al pie, con su labio de verdín.
                val im = enLaBorda(vv + 0.5f, 0.06f, 0.1f)
                drawOval(p.sombra(C.BREA), Offset(im.x - 0.07f * kk, im.y - 0.035f * kk), Size(0.14f * kk, 0.07f * kk))
                drawArc(p.medio(C.VERDIN).copy(alpha = 0.6f), 0f, 180f, false, Offset(im.x - 0.08f * kk, im.y - 0.04f * kk), Size(0.16f * kk, 0.09f * kk), style = Stroke(0.012f * kk + 0.2f))
                vv += 0.62f
            }
            // La pintura saltada: islas de madera gris con el borde de la pintura más claro.
            repeat(22) {
                val vv2 = az.entre(1.2f, 9f); val f = az.entre(0.15f, 0.75f); val q = enLaBorda(vv2, f); val kk = kEn(vv2)
                val w = az.entre(0.04f, 0.14f) * kk; val h = w * az.entre(0.3f, 0.6f)
                val forma = lazo((0 until 7).map { i -> val a = i * 0.897f; q + Offset(cos(a) * w * az.entre(0.6f, 1.1f), sin(a) * h * az.entre(0.6f, 1.1f)) })
                drawPath(forma, p.luz(lerp(pintura, C.BLANCO, 0.6f)).copy(alpha = 0.7f), style = Stroke(0.6f))
                drawPath(forma, p.por(C.MADERA_GRIS, t - 0.1f))
            }
            // La sal de arriba y el verdín y la mugre de abajo.
            repeat(30) { val vv2 = az.entre(1.1f, 9.5f); val q = enLaBorda(vv2, az.entre(0.82f, 0.98f)); drawCircle(p.luz(C.BLANCO).copy(alpha = 0.35f), az.entre(0.2f, 0.6f), q) }
            val pie0 = (0..24).map { i -> val vv2 = 0.9f + (B.RODA - 0.9f) * i / 24f; enLaBorda(vv2, 0f) }
            val pie1 = (24 downTo 0).map { i -> val vv2 = 0.9f + (B.RODA - 0.9f) * i / 24f; enLaBorda(vv2, 0.13f) }
            drawPath(poligono(pie0 + pie1), Brush.verticalGradient(listOf(Color.Transparent, p.sombra(lerp(C.VERDIN, C.BREA, 0.3f)).copy(alpha = 0.5f)), 150f, ALTO))
            // De noche, la lamparilla tiñe la borda de su lado.
            if (v.l.noche > 0.2f) charcoDeLuz(Foco(B.LAMPARILLA, C.LUMBRE, v.lu.lamparilla, 60f), enLaBorda(2.0f, 0.55f), 40f, 30f, pintura, 1.1f * v.l.noche)
        }
        // El cabillero de los obenques: un listón con sus cabillas, y de cada una cuelga la aduja de su cabo.
        cabillero(v)
        // La regala: la tabla de arriba, vista desde arriba, de madera lavada, con su canto exterior azul y desconchado.
        val dentro = (0..24).map { i -> val vv = 0.85f + (B.RODA - 0.85f) * i / 24f; B.pb(B.estribor(vv) - 0.12f, B.regala(vv), vv) }
        val fuera = (24 downTo 0).map { i -> val vv = 0.85f + (B.RODA - 0.85f) * i / 24f; B.pb(B.estribor(vv) + 0.06f, B.regala(vv) + 0.02f, vv) }
        drawPath(poligono(dentro + fuera), p.arriba(C.MADERA_GRIS))
        clipPath(poligono(dentro + fuera)) {
            repeat(26) { val vv = az.entre(0.9f, 10f); val q = B.pb(B.estribor(vv) - 0.03f, B.regala(vv), vv); val kk = kEn(vv); drawLine(p.sombra(C.MADERA_GRIS).copy(alpha = 0.4f), q, q + Offset(0.12f * kk, -0.03f * kk), 0.01f * kk + 0.2f) }
            repeat(14) { val vv = az.entre(0.9f, 9f); val q = B.pb(B.estribor(vv) + 0.03f, B.regala(vv), vv); val kk = kEn(vv); mancha(q, 0.12f * kk, 0.03f * kk, p.arriba(C.AZUL), 0.6f) }
        }
        drawPath(curva(fuera.reversed()), p.por(C.AZUL, 0.4f), style = Stroke(1.6f))
        drawPath(curva(dentro), p.luz(C.MADERA_GRIS).copy(alpha = 0.7f), style = Stroke(0.6f))
        if (v.luna > 0.05f) drawPath(curva(dentro), B.plata(v).copy(alpha = 0.55f * v.luna), style = Stroke(1.2f))
    }

    /** El cabillero junto a los obenques: el listón por dentro de la borda, las cabillas de madera y los cabos adujados colgando de ellas. */
    private fun DrawScope.cabillero(v: BarcoAbismo.Vista) {
        val p = v.p
        val v0 = 2.3f; val v1 = 3.7f
        val a = enLaBorda(v0, 0.62f, 0.2f); val b = enLaBorda(v1, 0.62f, 0.2f); val kk = kEn((v0 + v1) / 2f)
        drawLine(p.sombra(C.MADERA_OSC), a + Offset(0f, 0.03f * kk), b + Offset(0f, 0.03f * kk), 0.07f * kk)
        drawLine(p.medio(C.MADERA), a, b, 0.05f * kk)
        drawLine(p.luz(C.MADERA).copy(alpha = 0.6f), a + Offset(0f, -0.018f * kk), b + Offset(0f, -0.018f * kk), 0.012f * kk)
        val az = Azar(Semilla.de("abismo", "cabillas"))
        for (i in 0 until 4) {
            val vv = v0 + 0.2f + (v1 - v0 - 0.4f) * i / 3f; val q = enLaBorda(vv, 0.62f, 0.2f); val k2 = kEn(vv)
            drawLine(p.medio(C.MADERA_OSC), q + Offset(0f, -0.1f * k2), q + Offset(0f, 0.08f * k2), 0.03f * k2, StrokeCap.Round)
            drawLine(p.luz(C.MADERA), q + Offset(-0.006f * k2, -0.1f * k2), q + Offset(-0.006f * k2, -0.02f * k2), 0.01f * k2, StrokeCap.Round)
            if (i == 2 && EscenaAbismo.buzoAbajo(v.m)) continue
            val largo = az.entre(0.24f, 0.36f) * k2
            for (j in 0 until 4) {
                val w = (0.06f + 0.015f * j) * k2
                drawOval(p.sombra(C.CANAMO), Offset(q.x - w * 0.5f + j * 0.3f, q.y + 0.02f * k2), Size(w, largo * (0.85f + 0.05f * j)), style = Stroke(0.016f * k2))
                drawArc(p.luz(C.CANAMO).copy(alpha = 0.6f), 120f, 100f, false, Offset(q.x - w * 0.5f + j * 0.3f, q.y + 0.02f * k2), Size(w, largo * (0.85f + 0.05f * j)), style = Stroke(0.007f * k2))
            }
        }
    }

    // ---------------------------------------------------------------- Las cosas de la cubierta

    /** Una cosa que da sombra también a la lumbre del farol: dónde está y sus esquinas (en sus ejes). */
    private class Sombreable(val s: Sitio, val esquinas: List<V3>)

    fun DrawScope.cosas(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p; val sol = v.sol
        val cosas = ArrayList<Cosa>(); val aLaLumbre = ArrayList<Sombreable>()
        val neg = EscenaAbismo.esponjasNegras(m); val limpias = B.esponjasLimpias(m)
        val carga = EscenaAbismo.carga(m)
        val fase = EscenaAbismo.fase(m)
        // La tapa de la escotilla, detrás de la bomba: las ristras a medio ensartar, las cartas del corro.
        val se = B.SITIO_ESCOTILLA
        val esqEscotilla = esquinas(-B.ESCOTILLA_ANCHO / 2f, B.ESCOTILLA_ANCHO / 2f, 0f, B.ESCOTILLA_ALTO, -B.ESCOTILLA_FONDO / 2f, B.ESCOTILLA_FONDO / 2f)
        aLaLumbre += Sombreable(se, esqEscotilla)
        cosas += Cosa(sombraDe(se, sol, esqEscotilla), se.pie) {
            escotilla(v)
            if (EscenaAbismo.ensartando(m)) ensartando(v)
            if (fase == Fase.CORRO) corro(v)
        }
        // La bomba del aire, con la manguera (y encima, el objeto chico del día).
        val sb = B.SITIO_BOMBA
        aLaLumbre += Sombreable(sb, PiezasAbismo.bombaEsquinas())
        cosas += Cosa(sombraDe(sb, sol, PiezasAbismo.bombaEsquinas()), sb.pie) {
            if (EscenaAbismo.manguera(m) == Manguera.ADUJADA) aduja(v.pincel(sb.pie), B.pb(0.95f, 0f, 3.05f), 0.3f * sb.k, 0.1f * sb.k, 5, 0.05f * sb.k, C.BREA, manguera = true)
            bomba(v.pincel(sb.p(0f, 0.6f, 0f)), sb, sol, EscenaAbismo.buzoAbajo(m), 0.75f, Semilla.de("abismo", "bomba"))
            if (EscenaAbismo.manguera(m) == Manguera.AL_AGUA) mangueraEnCubierta(v)
            if (v.luna > 0.05f) lunaEnLaBomba(v)
            sobreElSoporte(m, v.pincel(B.soporte()), B.soporte(), B.escalaEn(sb.z0))
        }
        // Las botas de plomo: una de pie y otra caída de lado (el día que son el objeto, las pinta el objeto).
        if (ObjetosAbismo.elCuadroPinta(m, "botas")) {
            val b1 = B.SITIO_BOTA; val b2 = B.SITIO_BOTA2
            cosas += Cosa(sombraDe(b1, sol, esquinas(-0.08f, 0.08f, 0f, 0.46f, -0.2f, 0.16f)), b1.pie) { PiezasAbismo.run { bota(v.pincel(b1.pie), b1.pie, b1.k, -1f, 3) } }
            cosas += Cosa(sombraDe(b2, sol, esquinas(-0.08f, 0.08f, 0f, 0.46f, -0.2f, 0.16f)), b2.pie) { PiezasAbismo.run { bota(v.pincel(b2.pie), b2.pie, b2.k, -1f, 4) } }
        }
        // La tina de lavar, con las esponjas dentro y el mazo recostado; cortada por el borde de abajo.
        val st = B.SITIO_TINA
        aLaLumbre += Sombreable(st, PiezasAbismo.tinaEsquinas(0.42f, 0.5f))
        cosas += Cosa(sombraDe(st, sol, PiezasAbismo.tinaEsquinas(0.42f, 0.5f)), st.pie) {
            tina(v.pincel(st.pie), st, sol, 0.42f, 0.5f, if (neg > 0f || limpias > 0f) 6 else 2, neg > 0.3f, Semilla.de("abismo", "tina"))
            mazo(v.pincel(st.pie), B.pb(0.12f, 0f, 2.75f), st.p(0.36f, 0.5f, -0.1f), 0.11f * st.k)
        }
        // Las esponjas negras recién sacadas, por limpiar: un montón que crece con la mañana y mengua con la tarde.
        if (neg > 0.02f) {
            val s = B.sitio(1.0f, 4.15f)
            val ancho = 0.75f * s.k * (0.55f + 0.45f * neg)
            aLaLumbre += Sombreable(s, esquinas(-0.35f, 0.35f, 0f, 0.3f * neg + 0.1f, -0.25f, 0.25f))
            cosas += Cosa(sombraDe(s, sol, esquinas(-0.35f, 0.35f, 0f, 0.3f * neg + 0.1f, -0.25f, 0.25f)), s.pie) { monton(v.pincel(s.pie), s.pie, ancho, (5 + 11 * neg).toInt(), Semilla.de("abismo", "negras"), false) }
        }
        // Las limpias, «como pan blanco», a la tarde: el montón dorado que crece hasta que se ensartan.
        if (limpias > 0.02f) {
            val s = B.sitio(B.LIMPIAS_U, B.LIMPIAS_V)
            cosas += Cosa(sombraDe(s, sol, esquinas(-0.3f, 0.3f, 0f, 0.25f * limpias + 0.1f, -0.2f, 0.2f)), s.pie) { monton(v.pincel(s.pie), s.pie, 0.62f * s.k * (0.6f + 0.4f * limpias), (4 + 9 * limpias).toInt(), Semilla.de("abismo", "limpias"), true) }
        }
        // El cubo de agua de mar (para lavarse la cara, para mojar las esponjas), con su asa de cuerda.
        if (fase != Fase.SUENO) {
            val s = B.sitio(1.18f, 3.55f)
            cosas += Cosa(sombraDe(s, sol, esquinas(-0.13f, 0.13f, 0f, 0.3f, -0.13f, 0.13f)), s.pie) { cuboDeMadera(v.pincel(s.pie), s, sol, Semilla.de("abismo", "cubo")) }
        }
        // Una aduja de cabo junto a la borda, de proa, y otra suelta (siempre).
        run {
            val q = B.pb(0.85f, 0f, 5.8f); val kk = kEn(5.8f)
            cosas += Cosa(Path().apply { addOval(Rect(q.x - 0.3f * kk, q.y - 0.1f * kk, q.x + 0.3f * kk, q.y + 0.08f * kk)) }, q) { aduja(v.pincel(q), q, 0.28f * kk, 0.09f * kk, 5, 0.035f * kk, C.CANAMO) }
        }
        // Los sacos de esponjas secas, contra la borda (la bodega se va llenando).
        val sacos = when (carga) { EscenaAbismo.Carga.POCAS -> 0; EscenaAbismo.Carga.RACIMOS -> 2; EscenaAbismo.Carga.LLENO -> 4 }
        for (i in 0 until sacos) {
            val vv = 5.1f + 0.62f * i; val u = B.estribor(vv) - 0.42f - 0.08f * (i % 2)
            val pie = B.pb(u, 0f, vv); val kk = B.k(B.cam(u, vv).y)
            cosas += Cosa(Path().apply { addOval(Rect(pie.x - 0.3f * kk, pie.y - 0.7f * kk, pie.x + 0.3f * kk, pie.y)) }, pie) { saco(v.pincel(pie), pie, kk, Semilla.de("abismo", "saco", i)) }
        }
        // Las mantas tendidas para dormir, con el rollo de cuerda de almohada.
        if (EscenaAbismo.mantas(m)) for ((i, mt) in MANTAS.withIndex()) {
            val pie = B.pb(mt[0], 0f, mt[1])
            cosas += Cosa(Path(), pie) { manta(v, mt, i) }
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
        // De noche, el farol tumba las suyas lejos de él (la otra luz, la de la luna, ya las tumbó en la capa de arriba).
        if (v.lu.farol > 0.02f && v.l.noche > 0.3f) {
            val lum = B.LUMBRE_FAROL
            capaSombra(rayoFijo(0f, 45f, (0.5f * v.lu.farol * v.l.noche).coerceIn(0f, 0.6f), C.SOMBRA)) { c ->
                clipPath(suelo()) { for (s in aLaLumbre) drawPath(sombraALaLumbre(s.s, s.esquinas, lum), c) }
            }
        }
        // La luz de noche en la cubierta: el charco del farol en las tablas, bajo él, y su halo en el aire.
        if (v.l.noche > 0.2f && v.lu.farol > 0.02f) {
            val f = v.lu.farol * v.l.noche
            clipPath(suelo()) {
                charcoDeLuz(Foco(B.FAROL, C.LUMBRE, f, 90f), B.pb(0.35f, 0f, 3.0f), 120f, 40f, C.MADERA, 1.5f)
                charcoDeLuz(Foco(B.FAROL, C.LUMBRE, f, 90f), B.pb(0.2f, 0f, 4.4f), 70f, 18f, C.MADERA, 0.8f)
            }
        }
        for (cosa in orden) cosa.pintar(this)
        // Lo que pasa por la regala: la escala (echada o recogida), la cuerda de vida y la red colgada.
        escalaYCuerda(v)
        if ((fase == Fase.ESPONJAS || (fase == Fase.COMIDA && m.hora > 11.0)) && m.ficha.objeto != "red") redColgada(v)
        // Los trajes de lona, doblados a secar sobre la regala (uno por la mañana, dos a la tarde).
        val trajes = B.trajesASecar(m)
        for ((i, tramo) in TRAJES.take(trajes).withIndex()) {
            val va = tramo.first; val vb = tramo.second
            val a = B.pb(B.estribor(va) - 0.04f, B.regala(va) + 0.04f, va); val b = B.pb(B.estribor(vb) - 0.04f, B.regala(vb) + 0.04f, vb)
            trajeEnLaBorda(v.pincel(a), a, b, kEn((va + vb) / 2f), Semilla.de("abismo", "traje", i), mojado = if (fase == Fase.INMERSION || i == 1) 0.9f else 0.35f)
        }
        if (v.l.noche > 0.2f && v.lu.farol > 0.02f) haloDeBruma(B.FAROL, 26f, 18f, C.LUMBRE, 0.55f * v.lu.farol * v.l.noche)
        icono(v)
    }

    /** Dónde se doblan los trajes sobre la regala (de [first] a [second] metros hacia proa). */
    private val TRAJES = listOf(2.05f to 2.8f, 5.0f to 5.75f)

    /** Las mantas de dormir: (u0, v0, u1, v1) de cada una en la cubierta, en metros. */
    private val MANTAS = listOf(floatArrayOf(0.35f, 2.75f, 1.0f, 3.55f), floatArrayOf(0.55f, 4.7f, 1.15f, 5.9f), floatArrayOf(-0.55f, 4.4f, -0.05f, 5.5f))

    /** La sombra que tumba la lumbre [lum] (en metros de la cámara: x, alto, z) de una cosa con estas esquinas, en la cubierta. */
    private fun sombraALaLumbre(s: Sitio, esquinas: List<V3>, lum: V3): Path {
        val pts = esquinas.map { a ->
            val x = s.mx(a.x, a.z); val z = s.mz(a.x, a.z); val h = a.y.coerceAtMost(lum.y - 0.15f)
            val f = (lum.y / (lum.y - h)).coerceAtMost(4f)
            B.pc(lum.x + (x - lum.x) * f, 0f, lum.z + (z - lum.z) * f)
        }
        return poligono(envolvente(pts))
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

    /** Las ristras a medio ensartar sobre la tapa: una cuerda con unas pocas esponjas limpias, la aguja de madera y el montoncito que espera. */
    private fun DrawScope.ensartando(v: BarcoAbismo.Vista) {
        val s = B.SITIO_ESCOTILLA; val p = v.pincel(s.pie); val e = s.escala(0f, 0f)
        val a = s.p(-0.42f, B.ESCOTILLA_ALTO + 0.03f, 0.12f); val b = s.p(0.05f, B.ESCOTILLA_ALTO + 0.03f, -0.22f)
        cabo(p, listOf(a, (a + b) * 0.5f + Offset(0f, 0.02f * e), b, b + Offset(0.1f * e, 0.09f * e), b + Offset(0.12f * e, 0.32f * e)), 0.012f * e)
        for (i in 0 until 6) esponja(p, a + (b - a) * (0.1f + 0.15f * i) + Offset(0f, -0.04f * e), 0.06f * e, Semilla.de("abismo", "ensarta", i), true)
        drawLine(p.luz(C.MADERA), b + Offset(0.02f * e, -0.02f * e), b + Offset(0.18f * e, -0.07f * e), 0.014f * e, StrokeCap.Round)
        monton(p, s.p(0.3f, B.ESCOTILLA_ALTO, 0.12f), 0.3f * e, 4, Semilla.de("abismo", "porEnsartar"), true)
    }

    /** El corro del farol: la tabla de las cartas y el comboloi dejado encima. */
    private fun DrawScope.corro(v: BarcoAbismo.Vista) {
        val s = B.SITIO_ESCOTILLA; val p = v.pincel(s.pie); val e = s.escala(0f, 0f)
        val c0 = s.p(0.05f, B.ESCOTILLA_ALTO + 0.01f, 0.1f)
        for (i in 0 until 4) {
            val q = c0 + Offset((i - 1.5f) * 0.07f * e, (i % 2) * 0.012f * e)
            drawRect(p.luz(C.BLANCO), Offset(q.x - 0.03f * e, q.y - 0.02f * e), Size(0.06f * e, 0.04f * e))
            drawRect(p.medio(if (i % 2 == 0) C.ROJO else C.AZUL), Offset(q.x - 0.012f * e, q.y - 0.007f * e), Size(0.024f * e, 0.014f * e))
        }
    }

    /** La manguera del aire: de la boca de la bomba, por la cubierta, hasta la regala y por encima de ella (lo de fuera lo pinta el mar). */
    private fun DrawScope.mangueraEnCubierta(v: BarcoAbismo.Vista) {
        val p = v.p; val sb = B.SITIO_BOMBA
        val e = sb.escala(0f, 0f)
        val pts = listOf(sb.p(0.16f, 0.12f, -0.16f), sb.p(0.25f, 0.04f, -0.35f), B.pb(1.0f, 0.04f, 3.3f), B.pb(B.estribor(B.BAJADA_V) - 0.14f, 0.3f, B.BAJADA_V), B.pb(B.estribor(B.BAJADA_V) - 0.02f, B.regala(B.BAJADA_V) + 0.05f, B.BAJADA_V), B.pb(B.estribor(B.BAJADA_V) + 0.12f, B.regala(B.BAJADA_V) - 0.08f, B.BAJADA_V))
        manguera(p, pts, 0.055f * e)
    }

    /** La escala echada por la borda (sus ganchos en la regala) o recogida en su aduja, y la cuerda de vida, tensa, amarrada a una cabilla. */
    private fun DrawScope.escalaYCuerda(v: BarcoAbismo.Vista) {
        val m = v.m; val p = v.p
        val ve = 4.5f; val kk = kEn(ve)
        if (EscenaAbismo.escala(m) == Escala.ECHADA) {
            val a0 = B.pb(B.estribor(ve) - 0.08f, B.regala(ve) + 0.03f, ve - 0.2f); val a1 = B.pb(B.estribor(ve) - 0.08f, B.regala(ve) + 0.03f, ve + 0.25f)
            val b0 = B.pb(B.estribor(ve) + 0.22f, B.regala(ve) - 0.55f, ve - 0.2f); val b1 = B.pb(B.estribor(ve) + 0.22f, B.regala(ve) - 0.55f, ve + 0.25f)
            escala(p, a0, a1, b0, b1, 2, 0.04f * kk)
            for (q in listOf(a0, a1)) drawLine(p.sombra(C.PLOMO), q, q + Offset(-0.06f * kk, 0.1f * kk), 0.03f * kk, StrokeCap.Round)
        } else {
            // Recogida: los peldaños apilados junto a la borda y los dos cabos en su aduja.
            val pie = B.pb(B.estribor(ve) - 0.35f, 0f, ve)
            for (i in 0 until 5) drawLine(p.medio(C.MADERA), pie + Offset(-0.2f * kk, -0.035f * kk * i), pie + Offset(0.18f * kk, -0.035f * kk * i - 0.02f * kk), 0.03f * kk, StrokeCap.Round)
            drawOval(p.sombra(C.CANAMO), Offset(pie.x - 0.22f * kk, pie.y - 0.22f * kk), Size(0.44f * kk, 0.22f * kk), style = Stroke(0.02f * kk))
        }
        if (EscenaAbismo.buzoAbajo(m)) {
            // La cuerda de vida: de la cabilla, por encima de la regala, al agua; con su marca roja.
            val vc = B.BAJADA_V + 0.45f; val k2 = kEn(vc)
            val a = B.pb(B.estribor(vc) - 0.1f, B.regala(vc) + 0.02f, vc)
            cabo(p, listOf(enLaBorda(vc - 0.1f, 0.62f, 0.2f), a, B.pb(B.estribor(vc) + 0.12f, B.regala(vc) - 0.08f, vc)), 0.022f * k2)
            drawLine(p.medio(C.ROJO), a + Offset(-0.02f * k2, 0.02f * k2), a + Offset(0.03f * k2, -0.02f * k2), 0.04f * k2, StrokeCap.Round)
        }
    }

    /** La red de las esponjas, colgada a escurrir de una cabilla, por dentro de la borda: el saco de malla y su aro de hierro. */
    private fun DrawScope.redColgada(v: BarcoAbismo.Vista) {
        val vv = 3.95f; val kk = kEn(vv)
        val arriba = B.pb(B.estribor(vv) - 0.12f, B.regala(vv) - 0.02f, vv)
        redDeEsponjas(v.pincel(arriba), arriba, kk, Semilla.de("abismo", "red"))
    }

    /** La hornacina del icono en su candelero, sobre la regala de la aleta, abajo a la derecha: la luz cálida de la noche. */
    private fun DrawScope.icono(v: BarcoAbismo.Vista) {
        val m = v.m; val c = B.ICONO; val k = B.K_ICONO; val p = v.pincel(c)
        // El candelero: un listón que sube de dentro de la borda hasta la hornacina.
        val pie = B.pb(B.estribor(B.ICONO_V) - B.ICONO_U, 0.2f, B.ICONO_V); val tope = c + Offset(0f, 0.2f * k)
        val g = 0.07f * k
        drawLine(p.sombra(C.MADERA_OSC), pie + Offset(g * 0.25f, 0f), tope + Offset(g * 0.25f, 0f), g)
        drawLine(p.por(C.MADERA_GRIS, if (p.solALaDerecha) 0.5f else -0.3f), pie, tope, g * 0.7f)
        drawLine(p.medio(C.PLOMO), B.pb(B.estribor(B.ICONO_V) - B.ICONO_U, B.regala(B.ICONO_V) + 0.02f, B.ICONO_V) + Offset(-g * 0.6f, 0f), B.pb(B.estribor(B.ICONO_V) - B.ICONO_U, B.regala(B.ICONO_V) + 0.02f, B.ICONO_V) + Offset(g * 0.6f, 0f), g * 0.25f)
        hornacina(p, c, k, v.lu.lamparilla, mira = -0.6f, luto = EscenaAbismo.luto(m), velas = EscenaAbismo.dormicion(m) || EscenaAbismo.pascua(m), huevos = EscenaAbismo.pascua(m))
    }

    /** De noche, la luna platea lo alto de la bomba: la tapa y el canto de las ruedas del lado de la luna. */
    private fun DrawScope.lunaEnLaBomba(v: BarcoAbismo.Vista) {
        val s = B.SITIO_BOMBA; val e = s.escala(0f, 0f)
        val a = s.p(-PiezasAbismo.BOMBA_ANCHO / 2f, PiezasAbismo.BOMBA_ALTO, -PiezasAbismo.BOMBA_FONDO / 2f); val b = s.p(PiezasAbismo.BOMBA_ANCHO / 2f, PiezasAbismo.BOMBA_ALTO, -PiezasAbismo.BOMBA_FONDO / 2f)
        drawLine(B.plata(v).copy(alpha = 0.5f * v.luna), a, b, 0.02f * e + 0.3f)
    }

    /** Un saco de esponjas secas: la arpillera abombada por los bultos, atada por arriba. */
    private fun DrawScope.saco(p: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel, pie: Offset, k: Float, semilla: Long) {
        val az = Azar(semilla); val w = 0.32f * k * az.entre(0.9f, 1.1f); val h = 0.62f * k * az.entre(0.85f, 1.05f)
        val forma = lazo(listOf(Offset(pie.x - w, pie.y), Offset(pie.x - w * 1.08f, pie.y - h * 0.45f), Offset(pie.x - w * 0.7f, pie.y - h * 0.85f), Offset(pie.x - w * 0.15f, pie.y - h), Offset(pie.x + w * 0.2f, pie.y - h * 0.95f), Offset(pie.x + w * 0.8f, pie.y - h * 0.8f), Offset(pie.x + w * 1.06f, pie.y - h * 0.4f), Offset(pie.x + w * 0.95f, pie.y)))
        val ld = if (p.solALaDerecha) 1f else -1f
        drawPath(forma, Brush.horizontalGradient(listOf(if (ld > 0) p.sombra(C.LONA) else p.luz(C.LONA), p.medio(C.LONA), if (ld > 0) p.luz(C.LONA) else p.sombra(C.LONA)), pie.x - w, pie.x + w))
        clipPath(forma) {
            repeat(5) { val q = Offset(pie.x + az.entre(-0.7f, 0.7f) * w, pie.y - az.entre(0.2f, 0.8f) * h); drawArc(p.sombra(C.LONA).copy(alpha = 0.5f), 20f, 140f, false, Offset(q.x - w * 0.3f, q.y - w * 0.2f), Size(w * 0.6f, w * 0.4f), style = Stroke(0.5f)) }
            // La trama de la arpillera.
            for (i in 0 until 6) drawLine(p.sombra(C.LONA).copy(alpha = 0.18f), Offset(pie.x - w, pie.y - h * i / 6f), Offset(pie.x + w, pie.y - h * i / 6f - w * 0.1f), 0.4f)
        }
        // Lo que asoma por la boca: un par de esponjas secas.
        esponja(p, Offset(pie.x - w * 0.15f, pie.y - h * 0.98f), w * 0.32f, semilla * 3, true)
        drawLine(p.sombra(C.CANAMO), Offset(pie.x - w * 0.3f, pie.y - h * 0.88f), Offset(pie.x + w * 0.3f, pie.y - h * 0.86f), 0.03f * k)
    }

    /**
     * Una manta de lana tendida en las tablas ([mt]: u0, v0, u1, v1, en metros), como la deja quien se levanta a mear: un
     * rectángulo blando de bordes que ondean, con las listas tejidas de los extremos, las arrugas con su luz y su sombra, una
     * esquina vuelta que enseña el revés, y en la cabecera el rollo de cuerda que hace de almohada.
     */
    private fun DrawScope.manta(v: BarcoAbismo.Vista, mt: FloatArray, i: Int) {
        val az = Azar(Semilla.de("abismo", "mantas", i))
        fun q(fu: Float, fv: Float, h: Float = 0.03f) = B.pb(mt[0] + (mt[2] - mt[0]) * fu, h, mt[1] + (mt[3] - mt[1]) * fv)
        val p = v.pincel(q(0.5f, 0.5f))
        val lana = listOf(lerp(C.ROJO, C.MADERA_OSC, 0.4f), lerp(C.AZUL, C.LONA, 0.35f), lerp(C.LONA, C.MADERA_OSC, 0.35f))[i % 3]
        val lista = listOf(lerp(C.LONA, C.BLANCO, 0.3f), lerp(C.ROJO, C.MADERA_OSC, 0.2f), lerp(C.AZUL, C.BREA, 0.2f))[i % 3]
        // El borde: muchos puntos, con ondas, para que no sea una tabla.
        val borde = ArrayList<Offset>()
        for (j in 0..6) borde += q(j / 6f, az.entre(-0.02f, 0.03f), 0.02f + az.entre(0f, 0.04f))
        for (j in 1..6) borde += q(1f + az.entre(-0.03f, 0.02f), j / 6f, 0.02f + az.entre(0f, 0.05f))
        for (j in 5 downTo 1) borde += q(j / 6f, 1f + az.entre(-0.02f, 0.03f), 0.02f + az.entre(0f, 0.04f))
        for (j in 5 downTo 1) borde += q(az.entre(-0.02f, 0.03f), j / 6f, 0.02f + az.entre(0f, 0.05f))
        val f = poligono(borde)
        drawPath(f, Brush.linearGradient(listOf(p.por(lana, 0.35f), p.por(lana, -0.25f)), q(0f, 1f), q(1f, 0f)))
        clipPath(f) {
            // Las listas tejidas de los extremos.
            for (fv in listOf(0.08f, 0.14f, 0.86f, 0.92f)) drawPath(poligono(q(-0.1f, fv), q(1.1f, fv), q(1.1f, fv + 0.035f), q(-0.1f, fv + 0.035f)), p.medio(lista).copy(alpha = 0.85f))
            // Las arrugas: crestas de luz con su sombra debajo, de través.
            repeat(6) {
                val fv = az.entre(0.2f, 0.8f); val f0 = az.entre(-0.1f, 0.4f); val f1 = f0 + az.entre(0.35f, 0.7f)
                val a = q(f0, fv); val b = q(f1, fv + az.entre(-0.08f, 0.08f)); val mm = (a + b) * 0.5f + Offset(0f, -2.2f)
                drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(mm.x, mm.y, mm.x, mm.y, b.x, b.y) }, p.sombra(lana).copy(alpha = 0.55f), style = Stroke(1.6f, cap = StrokeCap.Round))
                drawPath(Path().apply { moveTo(a.x, a.y - 1f); cubicTo(mm.x, mm.y - 1f, mm.x, mm.y - 1f, b.x, b.y - 1f) }, p.luz(lana).copy(alpha = 0.45f), style = Stroke(0.8f, cap = StrokeCap.Round))
            }
            // El pelo de la lana: motas.
            repeat(30) { val c = q(az.real(), az.real()); drawCircle((if (az.real() > 0.5f) p.luz(lana) else p.sombra(lana)).copy(alpha = 0.25f), 0.5f, c) }
        }
        // La esquina vuelta, que enseña el revés más claro.
        val e0 = q(1f, 1f); val e1 = q(0.72f, 1f); val e2 = q(1f, 0.72f)
        drawPath(poligono(e0, e1, e2), p.sombra(lana).copy(alpha = 0.6f))
        drawPath(poligono(e1, e2, q(0.82f, 0.82f, 0.1f)), p.luz(lerp(lana, C.LONA, 0.4f)))
        // El rollo de cuerda de almohada, en la cabecera.
        val cab = q(0.5f, -0.02f, 0.05f); val kk = B.k(B.cam(mt[0], mt[1]).y)
        aduja(p, cab, 0.17f * kk, 0.06f * kk, 4, 0.03f * kk, C.CANAMO)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(v: BarcoAbismo.Vista, reloj: Float) {
        val m = v.m
        if (EscenaAbismo.buzoAbajo(m)) radiosEnMarcha(v.pincel(B.SITIO_BOMBA.pie), B.SITIO_BOMBA, reloj)
        // La llama de la lamparilla, que tiembla (y la vela, en su día).
        val k = B.K_ICONO
        llama(B.LAMPARILLA, 0.05f * k * (0.9f + 0.1f * sin(reloj * 7f)), reloj, 3)
        if (v.lu.farol > 0.02f) llama(B.FAROL + Offset(0f, 0.04f * B.K_FAROL), 0.07f * B.K_FAROL * (0.6f + 0.4f * v.lu.farol), reloj, 5)
        if (EscenaAbismo.hornillo(m)) { val ph = B.pb(B.EJE + 0.3f, 0f, 10.2f); val kk = B.k(B.cam(B.EJE + 0.3f, 10.2f).y); humo(v.l, ph + Offset(0f, -0.6f * kk), 60f, 14f, reloj, viento = -0.4f - 0.6f * m.viento, oscuro = 0.15f, bocanadas = 6) }
        // El reflejo de la lamparilla en el agua, de noche, temblando bajo la hornacina.
        if (v.l.noche > 0.3f) {
            val q = MarAbismo.bordeDelAgua(B.ICONO.y + 0.9f * k)
            val pie = Offset(maxOf(q + 4f, B.ICONO.x + 6f), B.ICONO.y + 0.95f * k)
            reflejoDeLumbre(pie, reloj, v.lu.lamparilla * v.l.noche)
        }
    }

    private fun DrawScope.reflejoDeLumbre(pie: Offset, reloj: Float, fuerza: Float) {
        for (i in 0 until 6) {
            val k = i / 6f
            val y = pie.y + 1f + 22f * k
            val w = 1.4f + 2.6f * k + 0.8f * sin(reloj * 3.1f + i * 1.7f)
            drawLine(C.LUMBRE.copy(alpha = (fuerza * (0.6f - 0.5f * k)).coerceIn(0f, 1f)), Offset(pie.x - w, y), Offset(pie.x + w, y), 0.8f, StrokeCap.Round, blendMode = BlendMode.Screen)
        }
    }
}
