package io.github.ruddy2003.vestigium.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * El modo «hora» de `Config.modo`: claro de día y oscuro de noche, según la hora del teléfono (16 nov 2026).
 *
 * Las horas son fijas, iguales todo el año y en todas partes (sin ubicación ni cálculo del sol), y son las mismas del
 * cuadro de «Otras vidas»: en `vidas/Ficha.kt` (`luzDe`, seis franjas) la franja «noche» va de las 21:00 a las 5:00 y a
 * las 5:00 empieza el «amanecer», que es también cuando empieza el día lógico (`Fechas.diaLogico`). Así, cuando el cielo
 * del cuadro ya es de noche (en `ui/escenas/paisaje/Luces.kt`, desde las 21:00 salen las estrellas y se encienden las
 * luces), la app también está en oscuro, y vuelve a claro con la primera claridad del cuadro.
 *
 * No es el filtro nocturno (`Config.nocturno`, de 22 a 5 h, en `MainActivity`): ese tiñe la pantalla y es otro ajuste.
 */
object ModoHora {
    /** A esta hora en punto empieza el oscuro (21:00). */
    const val OSCURECE = 21
    /** A esta hora en punto vuelve el claro (5:00). */
    const val AMANECE = 5

    /** ¿Es de noche a la [hora]? De 21:00:00 (incluida) a 5:00:00 (sin incluir). */
    fun esDeNoche(hora: LocalTime): Boolean = hora.hour >= OSCURECE || hora.hour < AMANECE

    /**
     * Milisegundos desde [ahora] hasta el próximo cambio (a las 21:00 o a las 5:00), con la zona de [ahora]; si un cambio
     * de horario cae entre medio, se cuenta bien. Siempre mayor que 0.
     */
    fun msHastaElCambio(ahora: ZonedDateTime): Long {
        val dia = ahora.truncatedTo(ChronoUnit.DAYS)
        val proximo = when {
            ahora.hour < AMANECE -> dia.withHour(AMANECE)
            ahora.hour < OSCURECE -> dia.withHour(OSCURECE)
            else -> dia.plusDays(1).withHour(AMANECE)
        }
        return ChronoUnit.MILLIS.between(ahora, proximo).coerceAtLeast(1L)
    }
}

/**
 * ¿Va en oscuro? La regla de los cuatro modos, sin Android ni Compose: «claro» y «oscuro» lo fijan, «hora» sigue a
 * [noche] ([ModoHora.esDeNoche]) y «sistema» (o un valor desconocido) sigue a [telefonoOscuro]. Un tema que solo existe
 * en oscuro siempre va en oscuro.
 */
fun oscuroSegun(modo: String, soloOscuro: Boolean, telefonoOscuro: Boolean, noche: Boolean): Boolean = soloOscuro || when (modo) {
    "claro" -> false
    "oscuro" -> true
    "hora" -> noche
    else -> telefonoOscuro
}

/** De noche o de día para el modo «hora», como estado: quien lo lee en Compose se vuelve a dibujar cuando cambia. */
object RelojModo {
    var noche by mutableStateOf(ModoHora.esDeNoche(LocalTime.now())); private set
    fun actualizar() { val n = ModoHora.esDeNoche(LocalTime.now()); if (n != noche) noche = n }
}

/**
 * Con el modo «hora», pone al día [RelojModo] justo al cruzar las 21:00 o las 5:00 (una espera hasta el próximo cambio,
 * no un sondeo) y cada vez que se vuelve a la app: con el teléfono dormido la espera puede llegar tarde.
 */
@Composable
internal fun RelojDelModo() {
    LaunchedEffect(Unit) {
        while (true) {
            RelojModo.actualizar()
            // Un segundo de margen para despertar ya del otro lado del cambio.
            kotlinx.coroutines.delay(ModoHora.msHastaElCambio(ZonedDateTime.now()) + 1000L)
        }
    }
    val ciclo = LocalLifecycleOwner.current
    DisposableEffect(ciclo) {
        val obs = LifecycleEventObserver { _, e -> if (e == Lifecycle.Event.ON_RESUME) RelojModo.actualizar() }
        ciclo.lifecycle.addObserver(obs)
        onDispose { ciclo.lifecycle.removeObserver(obs) }
    }
}
