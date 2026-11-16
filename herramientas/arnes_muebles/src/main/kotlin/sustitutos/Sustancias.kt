// Sustituto de data/Sustancias.kt (que arrastra la base de datos y media app): solo las dos funciones que nombra
// ui/CaraTema.kt (Intensidad), y no hacen nada. En el arnés no hay consumos: la intensidad se fija con ARNES_INTENSIDAD.
@file:Suppress("UNUSED_PARAMETER")
package io.github.ruddy2003.vestigium.data

object Sustancias {
    fun finEfectivo(c: Consumo, ahora: Long = System.currentTimeMillis()): Long = 0L

    fun nivelEn(c: Consumo, ms: Long, ahora: Long = System.currentTimeMillis()): Double? = null
}
