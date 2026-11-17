package io.github.ruddy2003.vestigium.avisos

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Seguir
import io.github.ruddy2003.vestigium.vidas.DiasEspeciales
import io.github.ruddy2003.vestigium.vidas.EspecialDelDia

// =====================================================================
//  El aviso del día especial de quien sigues (parte 8, encargo A; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// Kotlin puro, sin Android: decide **qué** avisar; el aviso lo publica `Avisos.historia` y lo llama el pulso
// (`Trabajo.avisos`). Lo prueba `HistoriaPendientePrueba`.

/**
 * Qué avisar de los días especiales de los personajes que sigues.
 *
 * - **Cuándo:** con los avisos encendidos (`Config.avisos`) y el suyo también (`Config.avisos_historia`), desde tu hora de
 *   despertar (`Config.hora_sueno`) hasta antes de las [HORA_FIN] (como los días especiales de tu gente).
 * - **De qué:** de cada día especial de hoy (`DiasEspeciales.conEspecialHoy`) que todavía no se avisó. Se recuerda por su
 *   clave con el día en que empezó (`Seguir.claveAviso`), no por el día de hoy: un día especial de 30 días avisa una sola
 *   vez, y si empiezas a seguir a alguien a mitad de uno, te avisa ese mismo día.
 * - **Cómo:** un solo aviso que junta a todos («Hoy Demeas: Las Panateneas. Kei: …»). El texto lleva solo el nombre del
 *   personaje y el título de su día: nada tuyo.
 */
object HistoriaPendiente {
    /** Hasta qué hora (sin incluirla) puede salir el aviso. */
    const val HORA_FIN = 21

    /** Un personaje con días especiales de hoy sin avisar: su id, su nombre, esos días y sus claves de aviso (en el mismo orden). */
    data class Pendiente(val vida: String, val nombre: String, val especiales: List<EspecialDelDia>, val claves: List<String>)

    /** El aviso que toca: a quiénes junta, su texto y la pantalla que abre al tocarlo. */
    data class Aviso(val pendientes: List<Pendiente>, val texto: String, val destino: String) {
        val claves: List<String> get() = pendientes.flatMap { it.claves }
    }

    /** Si la hora [hora] (0 a 23) está dentro del horario del aviso, con la hora de despertar [horaSueno]. */
    fun enHorario(hora: Int, horaSueno: Int): Boolean = hora >= horaSueno && hora < HORA_FIN

    /** La clave con que se recuerda el aviso del día especial [e] de [vida]. */
    fun clave(vida: String, e: EspecialDelDia): String = Seguir.claveAviso(vida, e.dia.id, e.inicio.toString())

    /** El nombre del personaje [vida] para el aviso (su id, si no se encuentra). */
    fun nombre(vida: String): String = OtrasVidas.porId(vida)?.nombre ?: vida

    /** De los que sigues en [c], los que el día lógico [hoy] tienen un día especial que no se ha avisado, en el orden en que los seguiste. */
    fun pendientes(c: Config, hoy: String): List<Pendiente> =
        DiasEspeciales.conEspecialHoy(Seguir.activos(c), hoy).mapNotNull { (v, l) ->
            val nuevos = l.filter { !Seguir.avisado(c, clave(v, it)) }
            if (nuevos.isEmpty()) null else Pendiente(v, nombre(v), nuevos, nuevos.map { clave(v, it) })
        }

    /** El texto del aviso: «Hoy Demeas: Las Panateneas. Kei: …» (con dos días especiales el mismo día, «… y …»). */
    fun texto(p: List<Pendiente>): String =
        "Hoy " + p.joinToString(". ") { "${it.nombre}: ${it.especiales.joinToString(" y ") { e -> e.dia.titulo }}" }

    /** Qué abre el aviso: el cuento del personaje si es uno solo; si son varios, Hoy (donde están sus banderitas). */
    fun destino(p: List<Pendiente>): String = p.singleOrNull()?.let { "cuento?vida=${it.vida}" } ?: "dia"

    /**
     * El aviso que toca ahora, a la hora [hora] del día lógico [hoy], o null si no toca ninguno (apagado, fuera de horario o
     * nada nuevo). Se llama en cada pulso: lo que ya se avisó no vuelve a salir.
     */
    fun decidir(c: Config, hoy: String, hora: Int): Aviso? {
        if (!c.avisos || !c.avisos_historia || !enHorario(hora, c.hora_sueno)) return null
        val p = pendientes(c, hoy)
        if (p.isEmpty()) return null
        return Aviso(p, texto(p), destino(p))
    }

    /** Anota en [c] que ya se avisó de todo lo de [aviso], en el instante [ts] (y olvida los avisos viejos, `Seguir.podarAvisos`). */
    fun anotar(c: Config, aviso: Aviso, ts: String, hoy: String): Config =
        aviso.claves.fold(c) { acc, k -> Seguir.anotarAviso(acc, k, ts, hoy) }
}
