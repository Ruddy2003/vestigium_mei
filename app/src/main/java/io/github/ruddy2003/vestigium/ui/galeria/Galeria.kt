package io.github.ruddy2003.vestigium.ui.galeria

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.CuadroGuardado
import io.github.ruddy2003.vestigium.data.CuadrosGuardados
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.escenas.CuadroHoy
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.pintarQuieta

// =====================================================================
//  La galería de cuadros guardados (parte 8, encargo C; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// Puntos de enganche: las firmas son definitivas y ya están llamadas desde su sitio (la ruta `galeria_cuadros` en
// `MainActivity`, el recuadro en `PantallaVida`); los cuerpos los escribe el encargo C. Los datos: `data/Historias.kt`
// (`CuadrosGuardados`) y `data/CarpetaCuadros.kt` (la imagen en `files/cuadros/`, que viaja en el respaldo).

/**
 * Pinta el cuadro [cuadro] a la [hora] (0 a 24) fuera de la pantalla, a [ancho] × [alto] pixeles. No toca la pantalla: se
 * puede llamar desde cualquier hilo (`Dispatchers.Default`). Hoy pinta solo la parte quieta, con la densidad de un teléfono
 * de 412 dp de ancho; el encargo C le agrega la parte viva quieta (`RELOJ_QUIETO`) y el gato, para que sea el cuadro tal cual.
 */
fun pintarCuadro(cuadro: CuadroHoy, hora: Double, ancho: Int = CuadrosGuardados.ANCHO, alto: Int = CuadrosGuardados.ALTO): ImageBitmap =
    pintarQuieta(cuadro.escena, Momento(cuadro.ficha, hora), ancho, alto, ancho / 412f)

/**
 * Guarda [cuadro] a la [hora] en la galería: lo pinta ([pintarCuadro]), escribe la imagen (`CarpetaCuadros.escribir`, JPEG
 * al `CuadrosGuardados.CALIDAD` %) y, solo si salió, sus datos (`CuadrosGuardados.guardar`, por `Repo.cambiarConfig`).
 * Devuelve lo guardado, o null si no se pudo. Fuera del hilo de la pantalla.
 */
suspend fun guardarCuadro(ctx: Context, cuadro: CuadroHoy, hora: Double): CuadroGuardado? = null

/**
 * El «me gusta» del cuadro: guarda [cuadro] a la [hora] ([guardarCuadro]) o, si ya está guardado
 * (`CuadrosGuardados.yaGuardado`), lo quita. Lo ponen el cuadro a pantalla completa y el de la pantalla del cuento (encargo B).
 */
@Composable
fun BotonMeGusta(cuadro: CuadroHoy, hora: Double, modifier: Modifier = Modifier) {
}

/** En la pestaña Vida: el mueble con los últimos cuadros guardados; tocarlo abre la galería. Sin cuadros, no ocupa sitio (o invita). */
@Composable
fun RecuadroGaleria(nav: Nav) {
}

/** La galería (ruta `galeria_cuadros`): los cuadros guardados con sus datos (tema, vida, día y hora), del más nuevo al más viejo. */
@Composable
fun PantallaGaleriaCuadros(nav: Nav) {
    Hoja("Galería de cuadros", nav.atras) {
        Ayuda("Todavía en preparación.", Modifier.padding(top = 12.dp))
    }
}
