package io.github.ruddy2003.vestigium.ui.nombres

import io.github.ruddy2003.vestigium.ui.TemaId

/**
 * Lo que completa los diccionarios de nombres de cada tema: todos los títulos de pantalla y de sección tienen nombre en
 * todos los temas, y ninguno es igual al español. Va antes que los diccionarios de siempre (`Romano.LATIN`,
 * `Griego.NOMBRES`, los de `Temas` y `NombresNuevos`), así que también corrige las entradas que eran iguales al español.
 */
object NombresCompletos {
    fun de(tema: TemaId, titulo: String): String? = when (tema) {
        TemaId.ROMANO -> NombresRomano.M[titulo]
        TemaId.URBE -> NombresUrbe.M[titulo]
        TemaId.CIBER -> NombresCiber.M[titulo]
        TemaId.NEON -> NombresNeon.M[titulo]
        TemaId.HELENO -> NombresHeleno.M[titulo]
        TemaId.EGIPTO -> NombresEgipto.M[titulo]
        TemaId.MEDIEVAL -> NombresMedieval.M[titulo]
        TemaId.TEMPLARIO -> NombresTemplario.M[titulo]
        TemaId.DISTOPIA -> NombresDistopia.M[titulo]
        TemaId.REFUGIO -> NombresRefugio.M[titulo]
        TemaId.ORDEN -> NombresOrden.M[titulo]
        TemaId.LABORATORIO -> NombresLaboratorio.M[titulo]
        TemaId.COLONOS -> NombresColonos.M[titulo]
        TemaId.NAPOLEON -> NombresNapoleon.M[titulo]
        TemaId.DECO -> NombresDeco.M[titulo]
        TemaId.BOTANICO -> NombresBotanico.M[titulo]
        TemaId.RUINAS -> NombresRuinas.M[titulo]
        TemaId.NAVIDAD -> NombresNavidad.M[titulo]
        TemaId.SEDA -> NombresSeda.M[titulo]
        TemaId.EDO -> NombresEdo.M[titulo]
        TemaId.URSS -> NombresUrss.M[titulo]
        TemaId.GOTICO -> NombresGotico.M[titulo]
        TemaId.ARCANO -> NombresArcano.M[titulo]
        TemaId.ESPACIO -> NombresEspacio.M[titulo]
        TemaId.ABISMO -> NombresAbismo.M[titulo]
        TemaId.ANDALUS -> NombresAndalus.M[titulo]
        TemaId.MESO -> NombresMeso.M[titulo]
        TemaId.MONGOL -> NombresMongol.M[titulo]
        TemaId.RENACIMIENTO -> NombresRenacimiento.M[titulo]
        TemaId.PIRATA -> NombresPirata.M[titulo]
        TemaId.REVOLUCION -> NombresRevolucion.M[titulo]
        TemaId.ANTIGUAS -> NombresAntiguas.M[titulo]
        TemaId.CUEVA -> NombresCueva.M[titulo]
    }
}
