package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs

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
    val PIE_GRANDE = V3(1.47f, 0f, 2.88f)

    fun seMueve(m: Momento): Boolean = true                                    // el polvo en los haces, las nubes de la pantalla y las gotas de la campana

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = VistaLab(m)
        with(SalaLab) { techo(v); paredDelFondo(v); puerta(v); paredDerecha(v); marcoDeLaPantalla(v) }
        with(PantallaLab) { pantalla(v) }
        with(SalaLab) { suelo(v) }
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
     * La luz del gato: la de la sala donde pisa (la luz de fondo, los charcos, la pantalla, la puerta, la lámpara de su mesa),
     * y su sombra la tumba la luz que más lo alumbra, lejos de ella.
     */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaLab(m)
        val w = enElSuelo(en) + V3(0f, 0.25f, 0f)
        val manda = v.manda(w)
        val rayo = if (manda != null && v.cuanto(manda, w, V3(0f, 1f, -0.3f)) > 0.05f) {
            val dx = w.x - manda.w.x; val lat = if (dx >= 0f) -1f else 1f
            rayoFijo(lat * (0.4f + 0.5f * (abs(dx) / 2.5f).coerceIn(0f, 1f)), 50f, (0.25f + 0.4f * v.cuanto(manda, w, V3(0f, 1f, -0.3f))).coerceIn(0f, 0.7f), ColorLaboratorio.SOMBRA)
        } else rayoFijo(0.5f, 62f, 0.22f, ColorLaboratorio.SOMBRA)
        val brillo = manda?.color?.let { lerp(it, Color.White, 0.5f) } ?: Color(0xFFE8ECF0)
        return LuzGato(rayo, (1f - v.general).coerceIn(0f, 1f), brillo) { c -> v.c(w, c, V3(0f, 0.4f, -1f)) }
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
