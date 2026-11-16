package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * El laboratorio de la grama: el cuadro de Laboratorio (`composicion_laboratorio.md` de la tanda S17), de pie en un rincón
 * mirando en diagonal. Los planos, cada uno en su archivo, para que otro lo continúe:
 * - **La sala** ([SalaLab], `EscenaLaboratorioSala.kt`): el techo, la pared del fondo con el estante de los cuadernos, la
 *   percha y el reloj, la puerta de vidrio con el pasillo y los jardines, la pared de la pantalla y el suelo pulido.
 * - **La pantalla de la Vigía** ([PantallaLab], `EscenaLaboratorioPantalla.kt`): el cielo de arriba a su hora y las ruinas.
 * - **La campana de cultivo** ([CampanaLab], `EscenaLaboratorioCampana.kt`).
 * - **La mesa larga** ([MesaLab], `EscenaLaboratorioMesa.kt`) y las lámparas de cultivo.
 * - **Lo del suelo y lo de los días** ([CosasLab], `EscenaLaboratorioCosas.kt`).
 * - **La luz** ([LucesLab], [LuzLab], `EscenaLaboratorioLuz.kt`): las fuentes en metros, los charcos, los haces, las sombras.
 */
internal object CuadroLab {
    /** Por dónde camina el gato: el suelo de delante de la campana, del pie de la mesa al cubo. */
    const val PISO_GATO = 276f
    const val GATO_DESDE = 212f
    const val GATO_HASTA = 372f

    /** Dónde va el objeto grande: de pie en el suelo, recostado en el taburete de Lauro. */
    val PIE_GRANDE = V3(1.3f, 0f, 2.95f)

    fun seMueve(m: Momento): Boolean = true                                    // el polvo en los haces, las nubes de la pantalla y las gotas de la campana

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = VistaLab(m)
        with(SalaLab) { techo(v); paredDelFondo(v); puerta(v); paredDerecha(v); marcoDeLaPantalla(v) }
        with(PantallaLab) { pantalla(v) }
        with(SalaLab) { cables(v); suelo(v) }
        with(LuzLab) { enLasSuperficies(v) }
        val cosas = ArrayList<CosaLab>()
        cosas += CosasLab.cosas(v)
        cosas += CampanaLab.cosa(v)
        cosas += MesaLab.cosa(v)
        // El objeto grande del día, recostado en el taburete.
        val t = CosasLab.taburete(m)
        ObjetosLaboratorio.grande(m, v.pincel(PIE_GRANDE), CamLab.p(PIE_GRANDE), CamLab.p(t + V3(0f, 0.62f, 0f)), CamLab.k(PIE_GRANDE) / 150f)?.let { g ->
            cosas += CosaLab(CamLab.hondo(PIE_GRANDE.x, PIE_GRANDE.z) - 0.01f, emptyList()) { g.pintar(this) }
        }
        val orden = cosas.sortedByDescending { it.hondo }
        with(LuzLab) { sombras(v, orden) }
        for (k in orden) k.pintar(this)
        // El objeto chico del día, en el claro de la mesa más cercano.
        if (!ObjetosLaboratorio.esGrande(m)) with(ObjetosLaboratorio) { sobreElSoporte(m, v.pincel(MesaLab.CLARO), CamLab.p(MesaLab.CLARO), CamLab.k(MesaLab.CLARO) / 150f) }
        with(SalaLab) { conducto(v) }
        with(MesaLab) { lamparasDeCultivo(v) }
        with(LuzLab) { aire(v) }
        with(CosasLab) { adornos(v) }
        with(LuzLab) { velo(v) }
        grano(Azar(Semilla.de("laboratorio", "grano")), 0.55f + 0.3f * v.general)
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = VistaLab(m)
        with(PantallaLab) { vivo(v, reloj) }
        with(LuzLab) { vivo(v, reloj) }
    }

    // ---------------------------------------------------------------- El gato

    /** El punto del suelo de la sala que se ve en [p] del lienzo. */
    fun enElSuelo(p: Offset): V3 {
        val cz = CamLab.F * CamLab.OJO / (p.y - CamLab.CY).coerceAtLeast(1f); val cx = (p.x - CamLab.CX) / CamLab.F * cz
        return V3(cx * CamLab.CA + cz * CamLab.SA, 0f, -cx * CamLab.SA + cz * CamLab.CA)
    }

    /**
     * La luz del gato donde pisa, pareja ([luzPareja]): el matiz de la sala en su sitio, no su fuerza (la cuenta de una cara
     * de la sala, en la penumbra de color, lo dejaba gris oscuro con la cabeza quemada por el brillo del lomo). De día, su
     * color propio con el lado de la luz que más le llega (la pantalla, casi siempre; las lámparas de cultivo alumbran la
     * mesa, no el suelo) y la sombra azul verdosa suave; de noche, el azul del Claustro con el filo violeta de la puerta y,
     * cerca de la mesa, lo cálido de su lámpara (por el charco que deja en el suelo). Su sombra, corta y blanda, del lado
     * contrario a esa luz.
     */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaLab(m)
        val w = enElSuelo(en) + V3(0f, 0.25f, 0f)
        // Cuánto le llega de cada luz, mirándola de frente.
        fun cuanta(f: Luz3): Float = when (f.de) {
            Fuente.PANEL, Fuente.REBOTE, Fuente.CAMPANA -> 0f
            Fuente.CULTIVO -> v.cuanto(f, w, f.w - w)
            Fuente.MESA -> { val d = (V3(f.w.x + 0.5f, 0.25f, f.w.z + 0.6f) - w).largo(); 0.6f * f.fuerza / (1f + (d / 1.1f) * (d / 1.1f)) }
            Fuente.PANTALLA, Fuente.PUERTA, Fuente.VELA, Fuente.LINTERNA, Fuente.AVISO -> { val d = (f.w - w).largo(); f.fuerza / (1f + (d / f.alcance) * (d / f.alcance)) }
        }
        val manda = v.luces.maxByOrNull { cuanta(it) }
        val k = manda?.let { cuanta(it) } ?: 0f
        val lat = manda?.let { if (it.w.x >= w.x) 1f else -1f } ?: 0.5f
        val noche = (1f - v.general).coerceIn(0f, 1f)
        // El filo: el de la luz que manda; de noche, el violeta de la puerta, cálido cerca de la mesa.
        val mesa = v.luces.firstOrNull { it.de == Fuente.MESA }?.let { (cuanta(it) / 0.25f).coerceIn(0f, 1f) } ?: 0f
        val puerta = v.luces.firstOrNull { it.de == Fuente.PUERTA }?.color
        val filo = if (v.noche) lerp(puerta ?: manda?.color ?: ColorLaboratorio.VIOLETA, ColorLaboratorio.LAMPARA, mesa) else manda?.color ?: Color(0xFFE8ECF0)
        val sombra = if (v.noche) ColorLaboratorio.SOMBRA else lerp(ColorLaboratorio.SOMBRA, Color(0xFF2E6A6A), 0.5f)
        return luzPareja(v.c(w, Color.White, V3(0f, 0.5f, -1f)), noche, filo, lat, k, sombra)
    }

    /**
     * Una luz de gato pareja y creíble, para el laboratorio y el centro del Claustro. [aqui] es el color que toma algo blanco
     * en su sitio (la luz de fondo y la de cada luz): se queda su matiz y no su fuerza, así el gato conserva su color propio
     * (de día casi entero, apenas teñido; de noche, más bajo y azul) y la luz y la sombra del dibujo no lo queman. [filo] es
     * el color de la luz que le da de lado, [lat] de qué lado viene (−1 izquierda, 1 derecha) y [k] cuánto; [sombra], el
     * color de la sombra que deja en el suelo. **[Diseño propio]**: cuánto se tiñe, cuánto baja de noche y en un sitio de
     * poca luz.
     */
    fun luzPareja(aqui: Color, noche: Float, filo: Color, lat: Float, k: Float, sombra: Color): LuzGato {
        val n = noche.coerceIn(0f, 1f); val kk = k.coerceIn(0f, 1f)
        val m0 = maxOf(aqui.red, aqui.green, aqui.blue).coerceAtLeast(0.05f)
        val matiz = Color(aqui.red / m0, aqui.green / m0, aqui.blue / m0)
        val deDia = lerp(Color.White, matiz, 0.22f)
        val deNoche = lerp(lerp(Color.White, matiz, 0.6f), Color(0xFF8C96C8), 0.35f).let { Color(it.red * 0.72f, it.green * 0.72f, it.blue * 0.72f) }
        // Donde la luz es poca, algo más bajo (sin llegar a la penumbra del sitio: el gato tiene que leerse).
        val cuanta = 0.72f + 0.28f * (m0 / 0.55f).coerceIn(0f, 1f)
        val base = lerp(deDia, deNoche, n).let { Color(it.red * cuanta, it.green * cuanta, it.blue * cuanta) }
        val rayo = rayoFijo(lat.coerceIn(-1f, 1f) * (0.45f + 0.35f * kk), 60f - 10f * n, 0.26f + 0.24f * kk + 0.08f * n, sombra, 0.45f + 0.15f * n)
        return LuzGato(rayo, n, lerp(filo, Color.White, 0.45f - 0.15f * n)) { c -> Color(c.red * base.red, c.green * base.green, c.blue * base.blue, c.alpha) }
    }

    /** Lo plantado en el suelo del gato: el taburete de Lauro, la caja de tierra y el cubo (los que caen en su tramo). */
    fun estorbos(m: Momento): List<Float> = listOf(CamLab.p(CosasLab.taburete(m)).x, CamLab.p(CosasLab.CAJA).x, CamLab.p(CosasLab.CUBO).x).filter { it in GATO_DESDE..GATO_HASTA }

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        // En la esquina de la mesa, junto al claro: un sitio alto con nada delante.
        val mirador = CamLab.p(MesaLab.MK.w(0.14f, 0.1f, MesaLab.ALTO))
        add(Ancla(TipoAncla.MIRADOR, mirador.x, mirador.y))
        // El objeto del día: el grande, en el suelo junto al taburete; el chico, en la mesa (el gato se pone al pie de la esquina).
        add(Ancla(TipoAncla.OBJETO, if (ObjetosLaboratorio.esGrande(m)) CamLab.p(PIE_GRANDE).x else GATO_DESDE + 8f, PISO_GATO))
        // Se guarece bajo la esquina de la mesa.
        add(Ancla(TipoAncla.ABRIGO, GATO_DESDE + 4f, PISO_GATO))
        if (EscenaLaboratorio.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 300f, PISO_GATO))
    }
}
