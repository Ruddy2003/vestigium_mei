package io.github.ruddy2003.vestigium.data

/**
 * El barómetro del teléfono calibrado con la presión de Open-Meteo: la diferencia típica (mediana de los últimos 60 días
 * con ambos datos) se suma a la media del sensor. Así la cifra se parece a la del clima del lugar, y los cambios del día
 * conservan la precisión del sensor.
 */
object Barometro {
    fun ajuste(): Double? {
        val dif = Repo.conexiones.values.filter { it.presion_n >= 8 }.sortedByDescending { it.fecha }.take(60)
            .mapNotNull { c -> Repo.entorno[c.fecha]?.presion_hpa?.let { it - c.presion_suma / c.presion_n } }
            .filter { kotlin.math.abs(it) < 150 }
        if (dif.size < 3) return null
        val s = dif.sorted(); return s[s.size / 2]
    }

    /** Presión local media del día (calibrada si se puede). */
    fun local(c: ConexionDia, ajuste: Double? = ajuste()): Double? =
        if (c.presion_n <= 0) null else Math.round((c.presion_suma / c.presion_n + (ajuste ?: 0.0)) * 10) / 10.0
}
