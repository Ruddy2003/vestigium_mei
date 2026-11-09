package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo

/**
 * El tema que cambia solo cada día (a las 5:00, cuando empieza el día de la app), entre los que elegiste. Básico
 * ([TemaId.sinDiseno]) nunca entra: es para probar la app, no para que toque un día.
 */
object RotacionTema {
    /** Los temas que pueden tocar en la rotación (todos menos Básico). */
    val rotables: List<TemaId> get() = TemaId.entries.filterNot { it.sinDiseno }

    fun candidatos(c: Config = Repo.config): List<TemaId> =
        c.temas_rotacion.mapNotNull { id -> rotables.firstOrNull { it.id == id } }.ifEmpty { rotables }

    /** El tema que le toca a [dia]: al azar, pero siempre el mismo para ese día y nunca igual al de ayer. */
    fun deDia(dia: String, actual: String, c: Config = Repo.config): TemaId {
        val l = candidatos(c)
        val opciones = l.filter { it.id != actual }.ifEmpty { l }
        return opciones[Math.floorMod(java.util.Random(dia.hashCode().toLong() * 31 + 7).nextInt(), opciones.size)]
    }

    /** Si toca, cambia el tema de hoy. Devuelve true si cambió. */
    fun revisar(): Boolean {
        val c = Repo.config
        if (!c.tema_rotar) return false
        val hoy = Fechas.diaLogico()
        if (c.tema_rotado == hoy) return false
        val t = deDia(hoy, c.tema, c)
        Repo.cambiarConfig { it.copy(tema = t.id, tema_rotado = hoy) }
        return true
    }
}
