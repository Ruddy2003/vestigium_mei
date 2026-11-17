// Sustituto vacío de Emblema (ui/Decoracion.kt, que arrastra media app): Tema.kt lo usa en las tarjetas del selector de
// temas, que el arnés no dibuja.
package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Emblema(tema: TemaId = Estilo.tema, tam: Dp = 30.dp, esquema: ColorScheme = MaterialTheme.colorScheme, animado: Boolean = false,
            modifier: Modifier = Modifier) {}
