package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Las cosas de la cocina, cada una en su sitio y de atrás adelante: lo de las paredes (el calendario, la radio de cable,
 * la repisa, el abrigo y el chal en su gancho), la puerta del cuarto con la tele, el visillo, el radiador con las botas
 * de fieltro, el fogón, la mesa con su hule y lo de encima (el samovar, el vaso de té, la azucarera, el pan, el objeto
 * del día y lo de las fiestas), la hoja abierta de la ventana en verano, el taburete y las cajas, la nevera (o la
 * alacena) y, colgada arriba, la lámpara.
 *
 * Las sombras: de día, las del sol que entra, solo dentro de la mancha (en la mesa y en el suelo); de noche, las de la
 * lámpara, abiertas en abanico desde ella. Lo vigila `EscenaUrssPrueba`.
 */
internal object CosasUrss {
    private val C = ColorUrss
    private val R get() = EscenaUrss
    private val Co get() = CocinaUrss
    private val K get() = CocinaUrss.K
    private fun p(x: Float, y: Float, t: Float = 0f) = CocinaUrss.K.p(x, y, t)

    // ---- Dónde está cada cosa (en la caja de la cocina).
    private const val SAM_X = 244f
    private const val SAM_T = 20f
    private const val VASO_X = 276f
    private const val VASO_T = 40f
    private const val AZU_X = 302f
    private const val AZU_T = 22f
    private const val PAN_X = 306f
    private const val PAN_T = 58f
    private const val TAB_X = 198f
    private const val TAB_T = 60f

    /** Dónde pisa el taburete en el cuadro (para que el gato no se siente encima). */
    val TABURETE: Offset = CocinaUrss.K.p(TAB_X, CocinaUrss.SUELO, TAB_T)

    private const val CUBO_X = 104f
    private const val CUBO_T = 74f

    /** Dónde están el cubo de las patatas y los chanclos (en el suelo del gato, cortados por el borde). */
    val CUBO: Offset = CocinaUrss.K.p(CUBO_X, CocinaUrss.SUELO, CUBO_T)
    val CHANCLOS: Offset = CocinaUrss.K.p(306f, CocinaUrss.SUELO, 75f)

    /** Dónde están las cajas del suelo, los días de la mudanza. */
    val CAJAS_SUELO: Offset = CocinaUrss.K.p(124f, CocinaUrss.SUELO, 56f)

    /** De dónde sale el vaho: la corona del samovar y el pico del hervidor. */
    val VAHO_SAMOVAR: Offset = CocinaUrss.K.p(SAM_X, CocinaUrss.MY, SAM_T) + Offset(-1f, -44f * CocinaUrss.K.s(SAM_T))
    val VAHO_HERVIDOR: Offset = CocinaUrss.K.p(CocinaUrss.FX1 - 16f, CocinaUrss.FY, 22f) + Offset(-14f * CocinaUrss.K.s(22f), -11f * CocinaUrss.K.s(22f))

    /** El hueco de la puerta del cuarto, en la pared de la derecha. */
    fun huecoDeLaPuerta(): Path = poligono(p(K.x1, Co.PY, Co.PT0), p(K.x1, Co.PY, Co.PT1), p(K.x1, Co.SUELO, Co.PT1), p(K.x1, Co.SUELO, Co.PT0))

    // ---------------------------------------------------------------- El orden

    fun DrawScope.cosas(m: Momento, lz: LuzUrss) {
        val az = Azar(Semilla.de("urss", "cosas"))
        enLasParedes(m, lz, az)
        with(PiezasUrss) {
            puertaDelCuarto(lz, R.televisor(m) && R.laCocinaPinta(m, "televisor"), R.laCocinaPinta(m, "televisor"))
            if (R.bolsaColgada(m) && R.laCocinaPinta(m, "bolsa")) bolsaDeRed(lz, p(K.x1, 104f, Co.PT0 - 3f), K.s(Co.PT0), m.azar("bolsa"))
            visillo(lz, R.visilloCorrido(m))
            radiador(lz, Co.RX0, Co.RX1, R.calefaccion(m), Azar(Semilla.de("urss", "radiador")))
            if (R.botas(m) && R.laCocinaPinta(m, "botas")) botas(lz, p(190f, Co.SUELO, 20f), K.s(20f) * 1.3f)
            fogon(lz, Co.FX0, Co.FX1, Co.FT1, Co.FY, R.fogon(m), Azar(Semilla.de("urss", "fogon")))
        }
        laMesa(m, lz)
        if (R.ventanaAbierta(m)) with(PiezasUrss) { hojaAbierta(lz, R.casaNueva(m)) }
        delSuelo(m, lz)
        with(PiezasUrss) {
            if (R.nevera(m)) nevera(lz, Co.NX0, Co.NX1, Co.NT0, Co.NT1, Co.NY, Azar(Semilla.de("urss", "nevera")))
            else alacena(lz, Co.NX0, Co.NX1, Co.NT0, Co.NT1, Co.NY - 30f)
            lampara(lz, p(Co.LX, 26f, Co.LT), p(Co.LX, Co.LY, Co.LT), 58f * K.s(Co.LT), R.lampara(m), R.bombillaDesnuda(m))
            // Lo más cercano, cortado por el borde de abajo: el cubo de las patatas junto a la nevera y los chanclos de la entrada.
            cuboDePatatas(lz, CUBO_X, CUBO_T, Azar(Semilla.de("urss", "patatas")))
            // El día de las botas, sus chanclos van con ellas (`ObjetosUrss`): los de la entrada no se pintan.
            if (R.laCocinaPinta(m, "botas")) chanclos(lz, 302f, 74f, R.nieve(m) > 0.3f && R.fase(m) != EscenaUrss.Fase.SOLA)
        }
    }

    // ---------------------------------------------------------------- Lo de las paredes

    private fun DrawScope.enLasParedes(m: Momento, lz: LuzUrss, az: Azar) {
        with(PiezasUrss) {
            val avance = (java.time.LocalDate.parse(m.ficha.dia).dayOfYear / 366f).coerceIn(0f, 1f)
            calendario(lz, 50f, 40f, avance, R.fiesta(m) || R.desfile(m))
            espejo(lz, 80f, 28f, R.luto(m))
            rejilla(lz, 142f, 8f)
            escurridor(lz, 100f, 24f, !R.te(m))
            cacharros(lz, 104f, 106f)
            if (R.laCocinaPinta(m, "radio")) radioDeCable(lz, 70f, 66f)
            repisa(lz, Co.FX0 - 4f, Co.FX1 + 2f, 70f, az)
            gancho(lz, R.abrigoColgado(m) && R.laCocinaPinta(m, "abrigo"), R.abrigoColgado(m) && R.laCocinaPinta(m, "chal"), R.luto(m))
        }
    }

    // ---------------------------------------------------------------- La mesa y lo de encima

    private fun DrawScope.laMesa(m: Momento, lz: LuzUrss) {
        with(PiezasUrss) { mesa(lz, Co.MX0, Co.MX1, Co.MT0, Co.MT1, Co.MY) }
        with(LuzCocinaUrss) { enLaMesa(m, lz); conoDeLaLampara(m, lz) }
        // Lo de encima, de atrás adelante: [sobre] cada cosa, su silueta (para la sombra) y cómo se pinta.
        val cosas = encimaDeLaMesa(m, lz)
        // Las sombras en la mesa: de día, las del sol, solo dentro de la mancha; de noche, las de la lámpara.
        val s = lz.sol; val lampara = lz.a.focos.firstOrNull { it.p == Co.BOMBILLA }
        val tapa = PiezasUrss.tapa(Co.MX0, Co.MX1, Co.MT0, Co.MT1, Co.MY)
        val bordeDeAtras = p(Co.MX0, Co.MY, Co.MT0).y
        if (lampara != null && lz.noche > 0.3f) {
            capaSombra(rayoDeFoco(lampara, Co.BAJO_LA_LAMPARA, 0.75f, C.SOMBRA)) { c ->
                clipPath(tapa) { for (q in cosas) sombraArrojada(rayoDeFoco(lampara, q.pie, 1f, c), q.forma, q.pie, c, bordeDeAtras, bordeDeAtras) }
            }
        } else if (s != null && s.fuerza > 0.1f) {
            capaSombra(LuzCocinaUrss.rayoDelSol(s, 1f)) { c ->
                clipPath(tapa) { clipPath(LuzCocinaUrss.manchaEnLaMesa(m, s)) { for (q in cosas) sombraArrojada(LuzCocinaUrss.rayoDelSol(s, 1f, q.pie), q.forma, q.pie, c, bordeDeAtras, bordeDeAtras) } }
            }
        }
        for (q in cosas.sortedBy { it.pie.y }) q.pintar(this)
    }

    /** Una cosa de encima de la mesa: su silueta, dónde pisa y cómo se pinta. */
    private class Encima(val forma: Path, val pie: Offset, val pintar: DrawScope.() -> Unit)

    private fun silueta(pie: Offset, ancho: Float, alto: Float, cintura: Float = 1f): Path = Path().apply {
        moveTo(pie.x - ancho / 2f, pie.y); lineTo(pie.x - ancho / 2f * cintura, pie.y - alto * 0.5f); lineTo(pie.x - ancho * 0.3f, pie.y - alto)
        lineTo(pie.x + ancho * 0.3f, pie.y - alto); lineTo(pie.x + ancho / 2f * cintura, pie.y - alto * 0.5f); lineTo(pie.x + ancho / 2f, pie.y); close()
    }

    private fun encimaDeLaMesa(m: Momento, lz: LuzUrss): List<Encima> = buildList {
        val ps = p(SAM_X, Co.MY, SAM_T); val ks = K.s(SAM_T)
        if (R.laCocinaPinta(m, "samovar")) add(Encima(silueta(ps, 26f * ks, 46f * ks, 1.4f), ps) { with(PiezasUrss) { samovar(lz, ps, ks) } })
        val pv = p(VASO_X, Co.MY, VASO_T); val kv = K.s(VASO_T)
        add(Encima(silueta(pv, 9f * kv, 12f * kv), pv) { with(PiezasUrss) { vasoDeTe(lz, pv, kv, R.te(m)) } })
        val pa = p(AZU_X, Co.MY, AZU_T); val ka = K.s(AZU_T)
        add(Encima(silueta(pa, 9f * ka, 9f * ka), pa) { with(PiezasUrss) { azucarera(lz, pa, ka) } })
        val pp = p(PAN_X, Co.MY, PAN_T); val kp = K.s(PAN_T)
        add(Encima(silueta(pp, 24f * kp, 8f * kp), pp) { with(PiezasUrss) { panConPano(lz, pp, kp) } })
        // Lo de los días señalados: las mandarinas y la botella del año nuevo, las mimosas, los claveles, la Pascua, los melocotones.
        val pf = p(262f, Co.MY, 66f); val kf = K.s(66f)
        if (R.mesaDeFiesta(m)) add(Encima(silueta(pf, 20f * kf, 26f * kf), pf) { with(PiezasUrss) { mesaDeFiesta(lz, pf, kf, R.anioNuevo(m)) } })
        val pr = p(232f, Co.MY, 40f); val kr = K.s(40f)
        if (R.mimosas(m) || R.claveles(m)) add(Encima(silueta(pr, 8f * kr, 26f * kr), pr) { with(PiezasUrss) { ramo(lz, pr, kr, R.mimosas(m)) } })
        if (R.pascua(m)) add(Encima(silueta(pf, 20f * kf, 16f * kf), pf) { with(PiezasUrss) { pascua(lz, pf, kf) } })
        if (R.melocotones(m)) add(Encima(silueta(pf, 24f * kf, 10f * kf), pf) { with(PiezasUrss) { melocotones(lz, pf, kf) } })
        if (R.luto(m)) { val pl = p(290f, Co.MY, 30f); val kl = K.s(30f); add(Encima(silueta(pl, 4f * kl, 18f * kl), pl) { with(PiezasUrss) { vela(lz, pl, kl) } }) }
        // Las cajas de la mudanza también encima de la mesa, cuando ya son muchas.
        if (R.cajas(m) >= 5) { val pc = p(296f, Co.MY, 48f); val kc = K.s(48f); add(Encima(silueta(pc, 30f * kc, 22f * kc), pc) { with(PiezasUrss) { caja(lz, 280f, 314f, Co.MY - 22f, Co.MY, 34f, 58f, lerp(C.MADERA, C.PAPEL, 0.4f), lerp(C.MADERA, C.PAPEL, 0.55f)); cuerda(lz, 280f, 314f, Co.MY - 22f, 34f, 58f) } }) }
        // El objeto del día: los chicos, sobre el hule, en la esquina de delante.
        val so = Co.SOBRE_LA_MESA
        add(Encima(ObjetosUrss.silueta(m, so, K.s(60f)), so) { with(ObjetosUrss) { sobreElSoporte(m, Pincel(lz.l, LuzCocinaUrss.rayoDelSol(lz.sol ?: EscenaUrss.SolDeVentana(1f, 0f, 0f, Color.White), 1f, so)), so, K.s(60f)) } })
    }

    // ---------------------------------------------------------------- Lo del suelo

    private fun DrawScope.delSuelo(m: Momento, lz: LuzUrss) {
        val s = lz.sol
        // La sombra del taburete y de las patas de la mesa, dentro de la mancha del suelo (de día) o desde la lámpara.
        val tab = TABURETE; val kt = K.s(TAB_T)
        val forma = Path().apply { addRect(Rect(tab.x - 17f * kt, tab.y - 38f * kt, tab.x + 17f * kt, tab.y - 34f * kt)); addRect(Rect(tab.x - 15f * kt, tab.y - 36f * kt, tab.x - 12f * kt, tab.y)); addRect(Rect(tab.x + 12f * kt, tab.y - 36f * kt, tab.x + 15f * kt, tab.y)) }
        val lampara = lz.a.focos.firstOrNull { it.p == Co.BOMBILLA }
        if (lampara != null && lz.noche > 0.3f) capaSombra(rayoDeFoco(lampara, tab, 0.6f, C.SOMBRA)) { c -> clipPath(K.formaDelSuelo()) { sombraArrojada(rayoDeFoco(lampara, tab, 1f, c), forma, tab, c, Co.SUELO, Co.SUELO) } }
        else if (s != null && s.fuerza > 0.1f) capaSombra(LuzCocinaUrss.rayoDelSol(s, 1f)) { c -> clipPath(K.formaDelSuelo()) { clipPath(LuzCocinaUrss.manchaEnElSuelo(m, s)) { sombraArrojada(LuzCocinaUrss.rayoDelSol(s, 1f, tab), forma, tab, c, Co.SUELO, Co.SUELO) } } }
        with(PiezasUrss) {
            val n = R.cajas(m)
            if (n > 0) cajasDeLaMudanza(lz, n, Azar(Semilla.de("urss", "cajas")))
            platilloDelGato(lz, 214f, 30f)
            cestaDeCebollas(lz, 124f, 46f, Azar(Semilla.de("urss", "cebollas")))
            escoba(lz, p(150f, Co.SUELO, 40f), p(167f, 98f, 4f), K.s(30f))
            taburete(lz, TAB_X, TAB_T, 34f, 36f)
            panoEnElTaburete(lz, TAB_X, TAB_T, 34f, 36f)
            if (R.fase(m) != EscenaUrss.Fase.SOLA) periodico(lz, TAB_X, CocinaUrss.SUELO - 36f, TAB_T)
            // El objeto grande del día: de pie en el linóleo, recostado en el canto de la mesa.
            with(ObjetosUrss) { grande(m, Pincel(lz.l, LuzCocinaUrss.rayoDelSol(lz.sol ?: EscenaUrss.SolDeVentana(1f, 0f, 0f, Color.White), 1f, Co.PIE_GRANDE)), Co.PIE_GRANDE, Co.APOYO_GRANDE, K.s(70f))?.pintar?.invoke(this@delSuelo) }
        }
    }
}
