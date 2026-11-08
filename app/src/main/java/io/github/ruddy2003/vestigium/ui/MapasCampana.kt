package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los mapas de campaña de todos los temas. El fondo, el camino y las paradas de cada uno se dibujan en [MapasMas] (el motor
 * de rutas, con sus siete puntos por tema); aquí está la escena de la victoria: la corona triunfal en Alesia, el sol de
 * Vergina en Babilonia, la cruz de Santiago, las campanas de la catedral, el cuadro enmarcado y firmado, el águila de 1804,
 * la bandera de la Federación, la casa del pueblo nuevo, la flor de la expedición, el árbol de la plaza, los farolillos de
 * Antioquía, el arco de herradura, el ovoo, la almenara, el sello de «APROBADO» y las escenas de los temas del lote 2.
 */
object MapasCampana {
    /** El fondo del mapa. [avance] va de 0 a 1 (días cumplidos de los siete). */
    fun DrawScope.fondo(tema: TemaId, cs: ColorScheme, avance: Float) {
        with(MapasMas) { fondoMas(tema, cs, avance) }
    }

    /** El camino entre etapas. */
    fun DrawScope.ruta(tema: TemaId, cs: ColorScheme, p: Path) {
        with(MapasMas) { rutaMas(tema, cs, p) }
    }

    /** La marca de cada etapa (la de la meta un poco más grande). */
    fun DrawScope.etapa(tema: TemaId, cs: ColorScheme, c: Offset, r: Float, col: Color, meta: Boolean) {
        with(MapasMas) { etapaMas(tema, cs, c, r, col, meta) }
    }



}
