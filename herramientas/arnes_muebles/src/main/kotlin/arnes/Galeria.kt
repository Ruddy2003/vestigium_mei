// El modo `galeria` del arnés (parte 8, encargo C): el mueble de la galería de cuadros de la pestaña Vida, con el dibujo del
// tema (`DisenoVida.muebleGaleria`, por omisión `MuebleGaleriaComun`) y las miniaturas puestas con el composable real de la
// app (`ParedDeCuadros`, ui/vida/MuebleGaleria.kt), uno bajo otro con 0, 1 y 5 cuadros guardados.
//
//   dibujar.sh galeria <tema> <claro|oscuro> <salida.png>
//   dibujar.sh hoja galeria <claro|oscuro> <salida.png> <tema1,tema2,...>
//
// ARNES_CUADROS=0,1,5 elige cuántos cuadros (por omisión, esos tres). Las miniaturas son paisajes de relleno pintados aquí
// (el arnés no compila las escenas): sirven para ver cómo caen en sus marcos y con la luz del mueble.
package arnes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.vida.EstadoMueble
import io.github.ruddy2003.vestigium.ui.vida.ParedDeCuadros
import io.github.ruddy2003.vestigium.ui.vida.disenoVida

/** El recuadro de la galería, como `RecuadroGaleria` (ui/galeria/Galeria.kt): 190 dp, sin la lámpara de la escena, semilla 11. */
@Composable
internal fun GaleriaArnes() {
    val cs = MaterialTheme.colorScheme
    val d = disenoVida(Estilo.tema)
    val oscuro = esOscuro()
    val cuantos = System.getenv("ARNES_CUADROS")?.split(',')?.mapNotNull { it.trim().toIntOrNull() }?.takeIf { it.isNotEmpty() } ?: listOf(0, 1, 5)
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        cuantos.forEach { n ->
            val detalle = if (n == 0) "Dale ♥ a un cuadro para guardarlo aquí" else if (n == 1) "1 cuadro guardado · el último, el 21 de marzo" else "$n cuadros guardados · el último, el 21 de marzo"
            RecuadroArnes("Galería de cuadros", detalle, 190.dp, luz = false) {
                ParedDeCuadros(d, EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 11), oscuro, MINIATURAS.take(n))
            }
        }
    }
}

/** Cinco paisajes de relleno de 300 × 225 (lo que decodifica la app con `inSampleSize` 4): mar de día, ocaso, noche, niebla y trigal. */
private val MINIATURAS: List<ImageBitmap> by lazy {
    listOf(
        listOf(0xFF8FC1E3, 0xFFE9F1F4, 0xFF2F6E8E, 0xFF6E8B5A, 0xFFFFF2C0),
        listOf(0xFFE9895B, 0xFFF6C987, 0xFF6B4A6E, 0xFF3C2F45, 0xFFFFD27A),
        listOf(0xFF141B33, 0xFF2C3A5E, 0xFF0E1222, 0xFF1C2236, 0xFFF4EBC8),
        listOf(0xFFB9C2C4, 0xFFDCE0DE, 0xFF7D8A86, 0xFF59665F, 0xFFF0EFE6),
        listOf(0xFF9CC7E8, 0xFFF4EBD2, 0xFFC9A44A, 0xFF8A6A2C, 0xFFFFF4C8),
    ).mapIndexed { k, c -> paisaje(k, c.map { Color(it) }) }
}

private fun paisaje(k: Int, c: List<Color>): ImageBitmap {
    val w = 300; val h = 225
    val img = ImageBitmap(w, h)
    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(w.toFloat(), h.toFloat())) {
        drawRect(Brush.verticalGradient(listOf(c[0], c[1]), 0f, h * 0.62f))
        drawCircle(c[4], h * (if (k == 2) 0.06f else 0.09f), Offset(w * (0.25f + 0.13f * k), h * (0.22f + 0.05f * k)))
        val lejos = Path().apply {
            moveTo(0f, h * 0.55f); quadraticTo(w * 0.3f, h * (0.38f + 0.03f * k), w * 0.55f, h * 0.52f); quadraticTo(w * 0.8f, h * 0.44f, w.toFloat(), h * 0.56f)
            lineTo(w.toFloat(), h.toFloat()); lineTo(0f, h.toFloat()); close()
        }
        drawPath(lejos, c[2])
        val cerca = Path().apply {
            moveTo(0f, h * 0.78f); quadraticTo(w * 0.45f, h * 0.62f, w.toFloat(), h * 0.80f); lineTo(w.toFloat(), h.toFloat()); lineTo(0f, h.toFloat()); close()
        }
        drawPath(cerca, c[3])
        // Un árbol o un poste, para que haya algo que cruce.
        drawRect(c[3], Offset(w * 0.78f, h * 0.30f), Size(w * 0.025f, h * 0.45f))
        drawCircle(c[3], h * 0.10f, Offset(w * 0.79f, h * 0.30f))
    }
    return img
}
