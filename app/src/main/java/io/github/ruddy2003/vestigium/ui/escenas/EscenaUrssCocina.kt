package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Estancia
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.nieve
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lluvia
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * La cocina de Liudmila: «una cocina de seis metros», mirada de pie desde la puerta, con la ventana al patio en la
 * pared del fondo. Es la base que no cambia en los cinco años (la casa vieja y la nueva tienen los mismos muebles y los
 * mismos cacharros); lo de fuera cambia ([PatioUrss]).
 *
 * Está ordenada por planos, de atrás adelante, para que otro la continúe:
 * 1. **Lo de fuera** ([PatioUrss], `EscenaUrssPatio.kt`): el cielo, la ciudad lejana, el bloque de enfrente y el patio.
 * 2. **La caja** ([CajaUrss], `EscenaUrssCocinaCaja.kt`): las paredes, el suelo de linóleo y la ventana con su marco.
 * 3. **La luz** ([LuzCocinaUrss], `EscenaUrssCocinaLuz.kt`): la mancha de sol con la cruz del marco, el haz, las
 *    sombras, el cono de la lámpara, el halo, el reflejo en el cristal y la luz de la tele.
 * 4. **Las cosas** ([CosasUrss], `EscenaUrssCocinaCosas.kt`): el radiador, el fogón, la nevera, la mesa con su hule,
 *    el samovar, el taburete, la puerta del cuarto con la tele, la lámpara, el visillo; cada una dibujada con las
 *    piezas de [PiezasUrss].
 * 5. El grano, al final de la parte quieta.
 *
 * **[Diseño propio]**: cómo es la cocina y dónde va cada cosa. El cuento nombra la cocina de seis metros, el gas, la
 * radio de cable, la tetera, la mesa con la lámpara y el vaso de té, la nevera ZIL, el termómetro clavado en el marco,
 * las tiras de papel de las rendijas, el ventanillo y la leche entre las dos ventanas.
 */
internal object CocinaUrss {
    private val C = ColorUrss
    private val R get() = EscenaUrss

    /**
     * La caja de la cocina: los ojos de quien mira, de pie en la puerta, quedan al 32 % del alto del cuadro (ve el
     * suelo y la mesa desde arriba) y un poco a la izquierda de la ventana, que mira en diagonal.
     */
    val K = Estancia(fuga = Offset(176f, 92f), ojo = 210f, x0 = 40f, x1 = 336f, techo = 4f, suelo = 214f, cerca = 130f)
    const val SUELO = 214f

    // ---- La ventana, en la pared del fondo: el hueco y lo hondo del muro.
    const val VX0 = 172f
    const val VX1 = 330f
    const val VY0 = 22f
    const val VY1 = 146f
    const val GRUESO = 20f

    /** El montante y el travesaño del marco de dentro (en unidades de la pared). */
    const val MONTANTE = 251f
    const val TRAVESANO = 60f

    /** El hueco de la ventana por fuera del muro: por ahí se ve el patio. */
    val FUERA: Rect = K.s(-GRUESO).let { k -> Rect(176f + (VX0 - 176f) * k, 92f + (VY0 - 92f) * k, 176f + (VX1 - 176f) * k, 92f + (VY1 - 92f) * k) }

    /** Los cuatro cristales de la ventana (las hojas de arriba y las de abajo): cada uno, sus esquinas en la pared. */
    internal fun cristales(abierta: Boolean): List<List<Pair<Float, Float>>> {
        // Las sombras de los palos son las de los dos marcos (la ventana es doble): algo más anchas que cada palo.
        val m = MONTANTE; val tr = TRAVESANO; val b = 5f; val g = 4f
        // La hoja de la izquierda, abierta en verano: su hueco entero, sin palos (va la primera).
        val izq = if (abierta) listOf(listOf((VX0 + b) to (VY0 + b), (m - g) to (VY0 + b), (m - g) to (VY1 - b), (VX0 + b) to (VY1 - b)))
        else listOf(listOf((VX0 + b) to (VY0 + b), (m - g) to (VY0 + b), (m - g) to (tr - g), (VX0 + b) to (tr - g)), listOf((VX0 + b) to (tr + g), (m - g) to (tr + g), (m - g) to (VY1 - b), (VX0 + b) to (VY1 - b)))
        val der = listOf(listOf((m + g) to (VY0 + b), (VX1 - b) to (VY0 + b), (VX1 - b) to (tr - g), (m + g) to (tr - g)), listOf((m + g) to (tr + g), (VX1 - b) to (tr + g), (VX1 - b) to (VY1 - b), (m + g) to (VY1 - b)))
        return izq + der
    }

    // ---- La mesa: de MX0 a MX1 de lado, de MT0 a MT1 de hondo, con la tapa a la altura MY.
    const val MX0 = 222f
    const val MX1 = 328f
    const val MT0 = 6f
    const val MT1 = 74f
    const val MY = 150f

    // ---- La nevera (o la alacena), contra la pared de la izquierda, junto a quien mira.
    const val NX0 = 40f
    const val NX1 = 96f
    const val NT0 = 30f
    const val NT1 = 70f
    const val NY = 106f

    // ---- El fogón, contra la pared del fondo, a la izquierda de la ventana.
    const val FX0 = 100f
    const val FX1 = 162f
    const val FT1 = 30f
    const val FY = 148f

    // ---- El radiador, bajo la ventana.
    const val RX0 = 180f
    const val RX1 = 322f

    // ---- La lámpara, colgada sobre la mesa.
    const val LX = 275f
    const val LT = 62f
    const val LY = 48f

    /** Dónde está la bombilla de la lámpara en el cuadro, y dónde cae su luz en la mesa. */
    val BOMBILLA: Offset = K.p(LX, LY - 3f, LT)
    val BAJO_LA_LAMPARA: Offset = K.p(LX, MY, LT - 10f)

    /** Desde dónde alumbra el cielo de la ventana. */
    val LUZ_VENTANA = Offset(250f, 92f)

    /** La puerta del cuarto, en la pared de la derecha: de PT0 a PT1 de hondo. La tele está detrás. */
    const val PT0 = 16f
    const val PT1 = 58f
    const val PY = 54f
    val LUZ_TELE: Offset = K.p(K.x1 + 6f, 150f, 30f)

    /** El gas: la llama del fuego de delante. */
    val LUZ_GAS: Offset = K.p(146f, FY - 2f, 22f)

    /** Por dónde camina el gato: por delante de todo. */
    const val PISO_GATO = 278f

    /** Dónde se pone el objeto del día: sobre el hule, en la esquina de delante de la mesa. */
    val SOBRE_LA_MESA: Offset = K.p(246f, MY, 60f)

    /** Para los objetos grandes: pisan el linóleo junto a la mesa y se recuestan en su canto. */
    val PIE_GRANDE: Offset = K.p(214f, SUELO, 70f)
    val APOYO_GRANDE: Offset = K.p(MX0, MY + 4f, 66f)

    // ---------------------------------------------------------------- Las luces de la cocina

    /**
     * Las luces de la cocina en este momento. La primera es siempre el cielo de la ventana; luego la claridad de la
     * mancha de sol, la lámpara, el gas y la tele, si están encendidos.
     */
    internal fun focos(m: Momento): List<Foco> = buildList {
        val l = R.luces(m); val dia = (1f - l.noche).coerceIn(0f, 1f); val nv = R.nieve(m)
        // En verano, en la casa vieja, la luz de la ventana pasa por los árboles: verdea.
        val verde = if (R.casaNueva(m)) 0.08f * R.hojas(m) else 0.3f * R.hojas(m)
        val cielo = lerp(lerp(lerp(l.medio, Color.White, 0.5f), lerp(C.VERDE, C.PAPEL, 0.35f), verde), C.LUNA, l.noche)
        val abre = if (R.visilloCorrido(m)) 0.75f else 1f
        add(Foco(LUZ_VENTANA, cielo, (0.36f * dia * (1f - 0.3f * l.nublado) * (1f + 0.3f * nv) + (0.14f + 0.08f * nv) * l.noche) * abre, 100f, suelo = SUELO))
        R.solQueEntra(m)?.let { s -> add(Foco(LuzCocinaUrss.centroDeLaMancha(s), s.color, (if (s.deLuna) 0.2f else 0.3f) * s.fuerza, 80f, suelo = SUELO)) }
        if (R.lampara(m)) add(Foco(BOMBILLA, C.LAMPARA, 0.45f + 0.75f * l.noche, 78f, suelo = BAJO_LA_LAMPARA.y))
        if (R.fogon(m)) add(Foco(LUZ_GAS, lerp(C.AZUL, C.LAMPARA, 0.5f), 0.18f + 0.25f * l.noche, 34f, suelo = LUZ_GAS.y + 2f))
        if (R.televisor(m)) add(Foco(LUZ_TELE, C.TELE, 0.22f + 0.3f * l.noche, 70f, suelo = K.p(K.x1, SUELO, 30f).y))
    }

    /** El color de cada cosa de la cocina con sus luces. */
    internal fun luz(m: Momento): LuzUrss {
        val ld = R.lucesDentro(m); val f = focos(m); val l = R.luces(m)
        // Las paredes reciben menos la lámpara (la pantalla la echa hacia abajo): su halo se pinta aparte, con la sombra de los flecos.
        val enParedes = f.map { if (it.p == BOMBILLA) Foco(it.p, it.color, it.fuerza * 0.4f, it.alcance, it.suelo) else it }
        return LuzUrss(ld, Alumbrado(ld, f), (1f - l.noche).coerceIn(0f, 1f), l.noche, R.solQueEntra(m), R.lampara(m), Alumbrado(ld, enParedes))
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val lz = luz(m); val ext = R.luces(m)
        drawRect(lz.l.tono(C.PARED), Offset.Zero, Size(ANCHO, ALTO))
        with(CajaUrss) {
            paredes(m, lz)
            suelo(m, lz)
            ventana(m, lz, ext)
        }
        with(LuzCocinaUrss) { enElSuelo(m, lz) }
        with(CosasUrss) { cosas(m, lz) }
        with(LuzCocinaUrss) { enElAire(m, lz) }
        with(CajaUrss) { penumbra(m, lz) }
        grano(Azar(Semilla.de("urss", "grano")), 0.55f + 0.35f * lz.dia)
    }

    fun seMueve(m: Momento): Boolean {
        val c = m.ficha.clima
        return c == Clima.NIEVE || c == Clima.LLUVIA || c == Clima.TORMENTA || R.televisor(m) || R.fogon(m) || R.samovar(m) || R.hervidorCaliente(m) || R.pelusa(m) > 0.1f
    }

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val ext = R.luces(m); val c = m.ficha.clima
        // Lo que cae fuera, solo por el hueco de la ventana.
        clipRect(FUERA.left, FUERA.top, FUERA.right, FUERA.bottom) {
            when (c) {
                Clima.NIEVE -> nieve(ext, reloj, m.semilla("copos"), if (m.con("nieve")) 0.4f else 0.75f)
                Clima.LLUVIA -> lluvia(ext, reloj, m.semilla("gotas"), 0.5f, 0.2f)
                Clima.TORMENTA -> lluvia(ext, reloj, m.semilla("gotas"), 0.9f, 0.4f)
                Clima.DESPEJADO, Clima.NUBES, Clima.NIEBLA, Clima.VIENTO, Clima.POLVO, Clima.SIN_CLIMA -> Unit
            }
            if (R.pelusa(m) > 0.1f) with(LuzCocinaUrss) { pelusaQueFlota(m, reloj, FUERA) }
        }
        with(LuzCocinaUrss) { vivo(m, reloj) }
    }

    // ---------------------------------------------------------------- El gato

    /** Al gato lo alumbran la ventana, el sol que entra, la lámpara y la tele. */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val lz = luz(m); val f = lz.a.focos
        val lampara = f.firstOrNull { it.p == BOMBILLA }
        val rayo = if (lampara != null && lz.noche > 0.4f) rayoDeFoco(lampara, en, 0.5f, C.SOMBRA)
        else lz.sol?.let { LuzCocinaUrss.rayoDelSol(it, 0.6f) } ?: rayoFijo(-0.4f, 55f, 0.2f, C.SOMBRA, 0.3f)
        val centro = en + Offset(0f, -12f)
        return LuzGato(rayo, lz.noche, lerp(Color(0xFFFFF0D2), Color(0xFFC8D0F0), lz.noche * 0.5f)) { lerp(lz.l.tono(it), lz.a.en(centro, it), 0.85f) }
    }

    fun estorbos(m: Momento): List<Float> = buildList {
        add(CosasUrss.TABURETE.x); add(CosasUrss.CUBO.x); add(CosasUrss.CHANCLOS.x)
        if (R.cajas(m) > 0) add(CosasUrss.CAJAS_SUELO.x)
    }

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 20f, PISO_GATO, hasta = 362f))
        // En el alféizar ancho, a la izquierda de los tarros: se sienta a mirar el patio.
        add(Ancla(TipoAncla.MIRADOR, 196f, K.p(196f, VY1 + 2f, 9f).y))
        add(Ancla(TipoAncla.OBJETO, SOBRE_LA_MESA.x - 16f, PISO_GATO))
        // Debajo de la mesa, junto al radiador: lo más caliente de la cocina.
        add(Ancla(TipoAncla.ABRIGO, 300f, PISO_GATO))
        if (R.anioNuevo(m) || R.ramaDeAbeto(m) && R.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 214f, 262f))
    }
}
