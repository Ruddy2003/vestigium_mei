// Sustituto de Font(R.font.x) de Android: en escritorio las letras se cargan de su archivo en app/src/main/res/font
// (la tabla número -> archivo la genera el arnés junto con R).
package androidx.compose.ui.text.font

import io.github.ruddy2003.vestigium.ARCHIVOS_FUENTE
import java.io.File

fun Font(resId: Int, weight: FontWeight = FontWeight.Normal, style: FontStyle = FontStyle.Normal): Font =
    androidx.compose.ui.text.platform.Font(File(ARCHIVOS_FUENTE[resId] ?: error("Letra desconocida: $resId")), weight, style)
