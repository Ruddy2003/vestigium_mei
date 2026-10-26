package io.github.ruddy2003.vestigium.ui

/**
 * Los seis temas base. Desde v81, lo nuevo de diseño se dibuja solo para ellos; cada tema complementario toma prestado
 * lo de su base (con sus propios colores). Lo que cada tema ya tenía (trofeos, salón, libreta, campaña) no cambia.
 * El `when` es exhaustivo a propósito: un tema nuevo tiene que elegir su base.
 */
object Bases {
    val TODAS = listOf(TemaId.GOTICO, TemaId.NAPOLEON, TemaId.DISTOPIA, TemaId.ROMANO, TemaId.SEDA, TemaId.BOTANICO)

    fun de(t: TemaId): TemaId = when (t) {
        TemaId.GOTICO, TemaId.MEDIEVAL, TemaId.RENACIMIENTO, TemaId.ANTIGUAS, TemaId.ORDEN -> TemaId.GOTICO
        TemaId.NAPOLEON, TemaId.REVOLUCION -> TemaId.NAPOLEON
        TemaId.DISTOPIA, TemaId.CIBER, TemaId.ESPACIO, TemaId.LABORATORIO, TemaId.REFUGIO, TemaId.URSS -> TemaId.DISTOPIA
        TemaId.ROMANO, TemaId.HELENO -> TemaId.ROMANO
        TemaId.SEDA, TemaId.MONGOL, TemaId.ANDALUS -> TemaId.SEDA
        TemaId.BOTANICO, TemaId.COLONOS, TemaId.NAVIDAD -> TemaId.BOTANICO
    }
}
