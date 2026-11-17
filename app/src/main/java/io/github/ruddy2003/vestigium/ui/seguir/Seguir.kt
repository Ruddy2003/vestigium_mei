package io.github.ruddy2003.vestigium.ui.seguir

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav

// =====================================================================
//  Seguir a un personaje (parte 8, encargo A; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// Puntos de enganche: las firmas son definitivas y ya están llamadas desde su sitio; los cuerpos los escribe el encargo A.
// Los datos: `data/Historias.kt` (`Seguir`, `UsoDias`) y `vidas/DiasEspeciales.kt`. Lo que se guarda, siempre por
// `Repo.cambiarConfig { c -> Seguir.seguir(c, vida, Fechas.diaLogico()) }`.

/**
 * El botón para seguir o dejar de seguir a [vida]. Va debajo del retrato, en la pantalla del cuento (`PantallaCuento`).
 * Con el tope lleno ([io.github.ruddy2003.vestigium.data.Seguir.TOPE]) dice por qué no se puede.
 */
@Composable
fun BotonSeguir(vida: OtrasVidas.Vida, modifier: Modifier = Modifier) {
}

/**
 * En Hoy, debajo del cuadro (o del gato de pixel): la banderita de cada personaje que sigues y hoy, día lógico [hoy], tiene
 * un día especial (`DiasEspeciales.conEspecialHoy`). Tocarla abre su cuento (`cuento?vida=<id>`). Sin ninguno, no ocupa sitio.
 */
@Composable
fun AvisosSeguidos(hoy: String, nav: Nav, modifier: Modifier = Modifier) {
}

/** La pantalla de los personajes que sigues (ruta `seguidos`): a quién, y sus días especiales pasados para leerlos después. */
@Composable
fun PantallaSeguidos(nav: Nav) {
    Hoja("Personajes que sigues", nav.atras) {
        Ayuda("Todavía en preparación.", Modifier.padding(top = 12.dp))
    }
}
