package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.Gato
import io.github.ruddy2003.vestigium.ui.TemaId
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Qué banda del gato carga la app (`Gato.clave`): Psicodelia la elige por su cara y, en la cara intensa, por el escalón
 * de intensidad más cercano; los demás temas, como siempre, solo por el claro y el oscuro. Que cada clave exista en
 * assets/gato/gato.json lo vigila `CoberturaTemas` (con `Gato.bandas`).
 */
class GatoClavePrueba {
    private val p = TemaId.PSICODELIA

    @Test fun laCiudadEsLaBandaDeSiempre() {
        assertEquals("psicodelia_c", Gato.clave(p, false))
        assertEquals("psicodelia_o", Gato.clave(p, true))
        // En la cara de la ciudad la intensidad no cuenta (con un consumo en curso la cara ya es la intensa).
        assertEquals("psicodelia_c", Gato.clave(p, false, Cara.BASE, 1f))
    }

    @Test fun laPsicodeliaEligeElEscalonMasCercano() {
        assertEquals("psicodelia_tinta_c", Gato.clave(p, false, Cara.INTENSA, 0f))
        assertEquals("psicodelia_tinta_o", Gato.clave(p, true, Cara.INTENSA, 0.24f))
        assertEquals("psicodelia_tinta_media_c", Gato.clave(p, false, Cara.INTENSA, 0.25f))
        assertEquals("psicodelia_tinta_media_o", Gato.clave(p, true, Cara.INTENSA, 0.5f))
        assertEquals("psicodelia_tinta_media_c", Gato.clave(p, false, Cara.INTENSA, 0.74f))
        assertEquals("psicodelia_tinta_plena_c", Gato.clave(p, false, Cara.INTENSA, 0.75f))
        assertEquals("psicodelia_tinta_plena_o", Gato.clave(p, true, Cara.INTENSA, 1f))
        assertEquals("psicodelia_tinta_c", Gato.clave(p, false, Cara.INTENSA, Float.NaN))
    }

    @Test fun lasBandasDePsicodelia() {
        assertEquals(listOf("psicodelia", "psicodelia_tinta", "psicodelia_tinta_media", "psicodelia_tinta_plena"), Gato.bandas(p))
    }

    @Test fun losDemasTemasNoMiranLaCaraNiLaIntensidad() {
        for (t in TemaId.entries.filterNot { it.conCaras }) {
            val sufijo = if (t.soloOscuro) "_o" else "_c"
            assertEquals(t.id + sufijo, Gato.clave(t, false))
            assertEquals(t.id + sufijo, Gato.clave(t, false, Cara.INTENSA, 1f))
            assertEquals(t.id + "_o", Gato.clave(t, true, Cara.INTENSA, 0.5f))
            assertEquals(listOf(t.id), Gato.bandas(t))
        }
    }
}
