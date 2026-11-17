package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav

// =====================================================================
//  El cuadro a pantalla completa (parte 8, encargo B; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================

/**
 * El cuadro del tema [tema] (su id, `TemaId.id`) para el día lógico [dia], a pantalla completa (ruta
 * `cuadro?tema=<id>&dia=<AAAA-MM-DD>`), con su gato y el «me gusta» de la galería (`ui.galeria.BotonMeGusta`). Un día
 * futuro no se abre. Punto de enganche: la firma es definitiva; el cuerpo lo escribe el encargo B.
 */
@Composable
fun PantallaCuadroCompleto(nav: Nav, tema: String, dia: String) {
    Hoja("El cuadro", nav.atras) {
        Ayuda("Todavía en preparación.", Modifier.padding(top = 12.dp))
    }
}
