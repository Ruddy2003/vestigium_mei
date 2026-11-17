package io.github.ruddy2003.vestigium.ui.seguir

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.CuentosLeidos
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Seguir
import io.github.ruddy2003.vestigium.vidas.DiasEspeciales
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios

/**
 * Lo que muestran las pantallas de seguir personajes (`Seguir.kt`), sin Compose ni Android: qué dice el botón, qué
 * banderitas salen en Hoy y qué días especiales pasados se pueden leer. Lo prueba `SeguirDatosPrueba`.
 */
internal object SeguirDatos {
    /** Lo que puede hacer el botón de seguir con un personaje. */
    enum class Estado {
        /** No lo sigues y cabe: «Seguir a …». */
        SEGUIR,
        /** Ya lo sigues: «Lo sigues · dejar de seguir». */
        SIGUES,
        /** No lo sigues y ya sigues a [Seguir.TOPE]: el botón lleva a la lista para dejar a uno. */
        LLENO,
    }

    fun estado(c: Config, vida: String): Estado = when {
        Seguir.sigue(c, vida) -> Estado.SIGUES
        Seguir.activos(c).size >= Seguir.TOPE -> Estado.LLENO
        else -> Estado.SEGUIR
    }

    /**
     * Una banderita de Hoy: el personaje [vida] (con su [nombre] y el id de su tema, [tema], null para los doce comunes),
     * el [titulo] de su día especial (el que manda, el primero de su ficha) y si ya leíste su cuento de hoy ([leido]).
     */
    data class Marca(val vida: String, val nombre: String, val tema: String?, val titulo: String, val leido: Boolean)

    /** Las banderitas del día lógico [hoy]: una por cada personaje que sigues y hoy tiene un día especial, en el orden en que los seguiste. */
    fun marcasDeHoy(c: Config, hoy: String): List<Marca> =
        DiasEspeciales.conEspecialHoy(Seguir.activos(c), hoy).map { (v, l) ->
            val vida = OtrasVidas.porId(v)
            Marca(v, vida?.nombre ?: v, vida?.tema, l.first().dia.titulo, CuentosLeidos.leido(c, v, hoy) != null)
        }

    /**
     * Todas las vidas que se pueden seguir: las que tienen calendario (`Calendarios.TODOS`), de todos los temas y no solo
     * del puesto (seguir es por personaje y sobrevive al cambio de tema). En orden de su época, de la más antigua a la más nueva.
     */
    fun seguibles(): List<OtrasVidas.Vida> =
        Calendarios.TODOS.mapNotNull { OtrasVidas.porId(it.vida) }.distinctBy { it.id }.sortedWith(compareBy({ it.anio }, { it.nombre }))

    /** Un día especial pasado y si ya leíste su cuento (el de su primer día, [DiasEspeciales.Especial.diaDelCuento]). */
    data class Pasado(val especial: DiasEspeciales.Especial, val leido: Boolean)

    /**
     * Los días especiales pasados de [vida] hasta el día lógico [hoy] (`DiasEspeciales.pasados`; nunca los futuros),
     * agrupados por el año en que empezaron: el año más reciente primero y, dentro, del más reciente al más viejo.
     */
    fun pasadosPorAnio(c: Config, vida: String, hoy: String): List<Pair<Int, List<Pasado>>> =
        DiasEspeciales.pasados(vida, hoy)
            .map { Pasado(it, CuentosLeidos.leido(c, vida, it.diaDelCuento) != null) }
            .groupBy { it.especial.inicio.year }
            .toList()
            .sortedByDescending { it.first }

    /** La ruta del cuento de [vida] del día [dia] (el de un día especial pasado). */
    fun rutaCuento(vida: String, dia: String): String = "cuento?vida=$vida&dia=$dia"
}
