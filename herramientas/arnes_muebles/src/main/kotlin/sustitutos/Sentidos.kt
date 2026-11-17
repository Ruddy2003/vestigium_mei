// Sustituto de ui/Sentidos.kt (vibración y sonidos de Android): en el arnés no se toca nada, así que no hace nada.
package io.github.ruddy2003.vestigium.ui

import androidx.compose.runtime.Composable

object Sentidos {
    enum class Evento { TOQUE, ELEGIR, GUARDAR, LOGRO, PASAR, CAMBIO, ERROR, ABRIR, TEMA }
}

@Composable
fun rememberSentir(): (Sentidos.Evento) -> Unit = { }
