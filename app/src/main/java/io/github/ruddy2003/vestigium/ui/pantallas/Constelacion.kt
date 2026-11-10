package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Perfiles
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.figuraTema
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Una persona en el cielo: cuántos días la mencionaste, la última vez y qué es para ti. */
class Estrella(val nombre: String, val dias: Int, val ultima: String, val relacion: String?, var x: Float = 0f, var y: Float = 0f, var r: Float = 0f)

object Constelacion {
    /**
     * Las personas del periodo y con quién aparece cada una el mismo día. Los días por persona salen de la cuenta única
     * ([io.github.ruddy2003.vestigium.data.Personas.diasCon]: diario, momentos y su «con quién», sueños, cronómetro y cartas),
     * la misma de Tu gente y de los reportes.
     */
    fun calcular(desde: String, hoy: String = Fechas.diaLogico()): Pair<List<Estrella>, Map<Pair<String, String>, Int>> {
        val porDia = HashMap<String, MutableSet<String>>()
        io.github.ruddy2003.vestigium.data.Personas.diasCon(ini = desde).forEach { (p, fs) -> fs.forEach { f -> porDia.getOrPut(f) { HashSet() }.add(p) } }
        val dias = HashMap<String, Int>(); val ultima = HashMap<String, String>()
        val juntos = HashMap<Pair<String, String>, Int>()
        porDia.forEach { (f, gente) ->
            gente.forEach { p -> dias[p] = (dias[p] ?: 0) + 1; if ((ultima[p] ?: "") < f) ultima[p] = f }
            val l = gente.sorted()
            for (i in l.indices) for (j in i + 1 until l.size) { val k = l[i] to l[j]; juntos[k] = (juntos[k] ?: 0) + 1 }
        }
        val estrellas = dias.entries.sortedByDescending { it.value }.take(80).map { (p, n) -> Estrella(p, n, ultima[p] ?: hoy, Perfiles.relacion(p)) }
        return estrellas to juntos.filterValues { it >= 2 }
    }
}

// La pantalla de la Constelación pasó a Social, vista «cielo» (9 nov 2026): ver Social.kt.
