package io.github.ruddy2003.vestigium.vidas.piezas

import io.github.ruddy2003.vestigium.vidas.Piezas

/**
 * El registro de las piezas de los cuentos: unas por personaje que ya tiene cuento. Cada tanda de textos suma aquí
 * las suyas, sin tocar el motor (`vidas/Cuento.kt`). Un personaje con calendario pero sin piezas tiene ficha del día
 * y todavía no tiene cuento.
 */
object TodasLasPiezas {
    val TODAS: List<Piezas> by lazy { listOf(
        PiezasDemeas.PIEZAS, PiezasPierre.PIEZAS, PiezasKei.PIEZAS, PiezasAmaru.PIEZAS,
        PiezasNaa.PIEZAS, PiezasAta.PIEZAS, PiezasIpy.PIEZAS, PiezasLucio.PIEZAS, PiezasAlice.PIEZAS, PiezasCitlali.PIEZAS,
        PiezasKichizo.PIEZAS, PiezasMary.PIEZAS, PiezasJose.PIEZAS, PiezasRosa.PIEZAS, PiezasMundo.PIEZAS, PiezasMexico.PIEZAS,
        PiezasCayo.PIEZAS, PiezasPay.PIEZAS, PiezasAma.PIEZAS, PiezasKan.PIEZAS, PiezasNanai.PIEZAS, PiezasOdo.PIEZAS, PiezasBernat.PIEZAS, PiezasZaynab.PIEZAS, PiezasBat.PIEZAS, PiezasIacopo.PIEZAS,
        PiezasTomas.PIEZAS, PiezasOchiyo.PIEZAS, PiezasMarieJeanne.PIEZAS, PiezasJeanBaptiste.PIEZAS, PiezasNaturalista.PIEZAS,
        PiezasAlbert.PIEZAS, PiezasNikos.PIEZAS, PiezasLou.PIEZAS,
        PiezasLiudmila.PIEZAS, PiezasGiulia.PIEZAS,
        PiezasAstronauta.PIEZAS, PiezasSemillera.PIEZAS, PiezasFuncionario.PIEZAS,
        PiezasResidente.PIEZAS, PiezasArchivista.PIEZAS,
        PiezasEscriba.PIEZAS, PiezasCientifica.PIEZAS,
        PiezasRuth.PIEZAS, PiezasBrenna.PIEZAS,
    ) }

    private val porVida: Map<String, Piezas> by lazy { TODAS.associateBy { it.vida } }

    fun de(vidaId: String): Piezas? = porVida[vidaId]
}
