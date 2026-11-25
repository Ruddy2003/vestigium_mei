package io.github.ruddy2003.vestigium.data

import java.time.LocalDate
import kotlin.math.floor

// =====================================================================
//  Lo que se guarda de los cuadros y las historias de «Otras vidas» (esquema 27, 17 nov 2026)
// =====================================================================
//
// La parte 8 del plan de mejoras (`docs/diseno/PLAN_MEJORAS.md`; el diseño técnico, en `docs/diseno/CUADROS_PLAN.md`): seguir
// personajes, leer sus cuentos, guardar cuadros en la galería. Y lo que necesitarán los logros nuevos de la parte 9
// (`docs/diseno/LOGROS_NUEVOS.md`). Todo vive en `Config` y viaja en el respaldo; nada se exporta a CSV: es estado de la app,
// no una medida (Apéndice D, D.199).
//
// Aquí solo hay funciones puras: reciben la configuración y devuelven la nueva, sin tocar el disco. Quien las usa las pasa
// por `Repo.cambiarConfig { c -> Seguir.seguir(c, vida, hoy) }`, que guarda de una sola vez y con candado. Las fechas son
// días lógicos AAAA-MM-DD (`Fechas.diaLogico()`); los instantes, ISO (`Fechas.ahoraIso()`). Las imágenes de los cuadros, en
// `CarpetaCuadros` (ese sí con Android).

/** Una fecha AAAA-MM-DD, o null si no lo es. */
private fun diaDe(s: String): LocalDate? = runCatching { LocalDate.parse(s) }.getOrNull()

/** Los personajes que sigues (`Config.seguidos`). */
object Seguir {
    /** Cuántos personajes se pueden seguir a la vez (decisión del autor). */
    const val TOPE = 6

    /** Cuántos días se recuerda que ya se avisó de un día especial, contados desde el día en que empezó (más que la duración más larga, 365). */
    const val DIAS_AVISO = 400L

    /** Los ids de los personajes que sigues ahora, en el orden en que empezaste a seguirlos. */
    fun activos(c: Config): List<String> = c.seguidos.filter { it.activo }.map { it.vida }.distinct()

    fun sigue(c: Config, vida: String): Boolean = c.seguidos.any { it.activo && it.vida == vida }

    /** Si se puede empezar a seguir a [vida]: existe, no lo sigues ya y no llegaste al [TOPE]. */
    fun puedeSeguir(c: Config, vida: String): Boolean = OtrasVidas.porId(vida) != null && !sigue(c, vida) && activos(c).size < TOPE

    /** Empieza a seguir a [vida] desde el día lógico [hoy]. Si no se puede ([puedeSeguir]), devuelve [c] tal cual. */
    fun seguir(c: Config, vida: String, hoy: String): Config =
        if (!puedeSeguir(c, vida) || diaDe(hoy) == null) c else c.copy(seguidos = c.seguidos + Seguido(vida, hoy))

    /** Deja de seguir a [vida] el día lógico [hoy]: su entrada se queda, con la fecha en que lo dejaste (no se borra). */
    fun dejar(c: Config, vida: String, hoy: String): Config =
        if (!sigue(c, vida)) c else c.copy(seguidos = c.seguidos.map { if (it.activo && it.vida == vida) it.copy(hasta = hoy) else it })

    /** La clave con que se recuerda el aviso de un día especial: el personaje, el id del día especial y el día real en que empezó. */
    fun claveAviso(vida: String, especial: String, inicio: String): String = "$vida|$especial|$inicio"

    fun avisado(c: Config, clave: String): Boolean = clave in c.historia_avisada

    /** Anota que ya se avisó de [clave] en el instante [ts], y olvida los avisos viejos ([podarAvisos]). */
    fun anotarAviso(c: Config, clave: String, ts: String, hoy: String): Config =
        podarAvisos(c.copy(historia_avisada = c.historia_avisada + (clave to ts)), hoy)

    /** Olvida los avisos de días especiales que empezaron hace más de [DIAS_AVISO] días. Una clave que no se entiende se queda. */
    fun podarAvisos(c: Config, hoy: String): Config {
        val h = diaDe(hoy) ?: return c
        val viejos = c.historia_avisada.keys.filter { k -> diaDe(k.substringAfterLast('|'))?.let { it.plusDays(DIAS_AVISO).isBefore(h) } == true }
        return if (viejos.isEmpty()) c else c.copy(historia_avisada = c.historia_avisada - viejos.toSet())
    }
}

/** Los cuentos leídos hasta el final (`Config.cuentos_leidos`). */
object CuentosLeidos {
    /** Un cuento se puede leer el día que es o después, nunca antes: el día [dia] no puede ser posterior al día lógico [hoy]. */
    fun sePuedeLeer(dia: String, hoy: String): Boolean {
        val d = diaDe(dia) ?: return false
        val h = diaDe(hoy) ?: return false
        return !d.isAfter(h)
    }

    /** La lectura guardada del cuento de [vida] del día [dia], o null si no lo has leído. */
    fun leido(c: Config, vida: String, dia: String): CuentoLeido? = c.cuentos_leidos.firstOrNull { it.vida == vida && it.dia == dia }

    /**
     * Marca leído el cuento de [vida] del día [dia], leído el día lógico [hoy] en el instante [ts], con los días especiales que
     * tenía ([especiales]). Solo vale para hoy o un día pasado ([sePuedeLeer]). Si ya estaba leído no cambia nada: se queda la
     * primera lectura (la que dice si lo leíste el mismo día).
     */
    fun marcar(c: Config, vida: String, dia: String, hoy: String, ts: String? = null, especiales: List<EspecialLeido> = emptyList()): Config =
        if (vida.isBlank() || !sePuedeLeer(dia, hoy) || leido(c, vida, dia) != null) c
        else c.copy(cuentos_leidos = c.cuentos_leidos + CuentoLeido(vida, dia, hoy, ts, especiales.distinct()))

    /** Si lo leíste el mismo día del cuento. */
    fun mismoDia(l: CuentoLeido): Boolean = l.leido == l.dia
}

/**
 * Los días de uso de lo que no deja otro rastro (`Config.uso_dias`), solo para los logros: se guarda **en qué días** se usó,
 * no cuántas veces (un toque al día cuenta lo mismo que cien). Solo se aceptan las claves de aquí.
 *
 * - Del gato se guardan solo **los días** en que lo acariciaste ([GATO], decisión del autor del 25 nov 2026, D.202), no
 *   cuántas caricias ni a qué hora. Hasta el esquema 28 no se guardaba nada (D.190).
 * - Nunca una clave de Privado ni de Sustancias (regla 1 y 2 de `LOGROS_NUEVOS.md`, sección 6).
 */
object UsoDias {
    /** Abriste «Hoy en la historia». */
    const val HISTORIA_HOY = "historia_hoy"
    /** Abriste un cuento desde «en la época de…» de «Hoy en la historia». */
    const val HISTORIA_EPOCA = "historia_epoca"
    /** Un respaldo a la carpeta escrito y verificado (lo anota `Respaldos`, desde el esquema 29). */
    const val RESPALDO_OK = "respaldo_ok"
    /** Un ensayo de restauración que salió bien (lo anota `Respaldos.ensayo`, desde el esquema 29). */
    const val ENSAYO_OK = "ensayo_ok"
    /** Una exportación a mano, resumida o extendida, que se armó bien (lo anota Ajustes, desde el esquema 29). */
    const val EXPORTAR = "exportar"
    /** El pulso vio al menos un widget de la app puesto en el teléfono (desde el esquema 29). */
    const val WIDGET = "widget"
    /**
     * Una escena del cuadro como fondo fijo del teléfono (desde el esquema 29): el día en que la app la pone y cada día en que
     * el pulso ve que sigue puesta (`FondosTelefono.escenaPuesta`).
     */
    const val FONDO_ESCENA = "fondo_escena"
    /** Acariciaste al gato del cuadro (desde el esquema 29, D.202): solo el día. */
    const val GATO = "gato"
    /** «tema:<id>»: ese tema estuvo puesto ese día, visto por el pulso de 15 minutos (desde el esquema 29). */
    const val PREFIJO_TEMA = "tema:"

    val FIJAS: Set<String> = setOf(HISTORIA_HOY, HISTORIA_EPOCA, RESPALDO_OK, ENSAYO_OK, EXPORTAR, WIDGET, FONDO_ESCENA, GATO)

    private val ID_TEMA = Regex("""[a-z][a-z0-9_]*""")

    /** La clave del tema [id]. */
    fun tema(id: String): String = PREFIJO_TEMA + id

    fun valida(clave: String): Boolean = clave in FIJAS || (clave.startsWith(PREFIJO_TEMA) && ID_TEMA.matches(clave.removePrefix(PREFIJO_TEMA)))

    /** Anota que [clave] se usó el día lógico [dia]. Una clave que no es de las de aquí, o un día que no es fecha, no cambia nada. */
    fun anotar(c: Config, clave: String, dia: String): Config {
        if (!valida(clave) || diaDe(dia) == null) return c
        val l = c.uso_dias[clave].orEmpty()
        return if (dia in l) c else c.copy(uso_dias = c.uso_dias + (clave to (l + dia).distinct().sorted()))
    }

    /** Anota varias [claves] el mismo [dia]; devuelve la misma [c] si ya estaban todas (así no se escribe la configuración). */
    fun anotarVarios(c: Config, claves: Collection<String>, dia: String): Config = claves.fold(c) { a, k -> anotar(a, k, dia) }

    /** Ya está anotada [clave] el [dia]: para no escribir la configuración cada vez. */
    fun anotada(c: Config, clave: String, dia: String): Boolean = dia in c.uso_dias[clave].orEmpty()

    fun dias(c: Config, clave: String): List<String> = c.uso_dias[clave].orEmpty()
}

/**
 * Los cuadros guardados con «me gusta» (`Config.cuadros_guardados`). La imagen se pinta fuera de la pantalla a [ANCHO] ×
 * [ALTO] pixeles (4:3, como el cuadro), se guarda como JPEG al [CALIDAD] % en `files/[CARPETA]/<id>.jpg` y viaja en el
 * respaldo (`Respaldos`). Unos 200 a 350 KB cada una.
 */
object CuadrosGuardados {
    const val CARPETA = "cuadros"
    const val ANCHO = 1200
    const val ALTO = 900
    const val CALIDAD = 90

    private val ID = Regex("""[A-Za-z0-9_-]{1,64}""")

    fun idValido(id: String): Boolean = ID.matches(id)

    /** El nombre de la imagen del cuadro [id]. */
    fun nombre(id: String): String = "$id.jpg"

    /**
     * Arma los datos de un cuadro nuevo. [id]: único (por ejemplo `Repo.nuevoId()`); [hora], de 0 a 24; [guardado], el
     * instante ISO. Null si algo no vale (id, día u hora).
     */
    fun nuevo(id: String, tema: String, vida: String, dia: String, hora: Double, guardado: String, claves: Collection<String> = emptyList()): CuadroGuardado? {
        if (!idValido(id) || diaDe(dia) == null || hora.isNaN() || hora < 0.0 || hora >= 24.0 || tema.isBlank() || vida.isBlank()) return null
        return CuadroGuardado(id, tema, vida, dia, hora, guardado, nombre(id), claves.distinct().sorted())
    }

    /** Agrega [g]. Si ya hay uno con su id, o el id no vale, no cambia nada. Guarda primero la imagen y después esto. */
    fun guardar(c: Config, g: CuadroGuardado): Config =
        if (!idValido(g.id) || c.cuadros_guardados.any { it.id == g.id }) c else c.copy(cuadros_guardados = c.cuadros_guardados + g)

    /** Quita el cuadro [id] (la imagen la borra quien llama, con `CarpetaCuadros.borrar`). */
    fun quitar(c: Config, id: String): Config =
        if (c.cuadros_guardados.none { it.id == id }) c else c.copy(cuadros_guardados = c.cuadros_guardados.filter { it.id != id })

    /** Los guardados, del más nuevo al más viejo. */
    fun todos(c: Config): List<CuadroGuardado> = c.cuadros_guardados.sortedByDescending { it.guardado }

    /** El tramo de veinte minutos de una hora: el mismo con que el cuadro decide cuándo se vuelve a pintar (`Momento.hora`). */
    fun tramo(hora: Double): Int = floor((((hora % 24) + 24) % 24) * 3).toInt()

    /** El cuadro ya guardado de ese [tema], [vida] y [dia] en el mismo tramo de veinte minutos que [hora], si lo hay (para que el «me gusta» se vea puesto). */
    fun yaGuardado(c: Config, tema: String, vida: String, dia: String, hora: Double): CuadroGuardado? =
        c.cuadros_guardados.firstOrNull { it.tema == tema && it.vida == vida && it.dia == dia && tramo(it.hora) == tramo(hora) }

    /** De los archivos [nombres] de la carpeta, los que ningún cuadro guardado usa (restos de un guardado a medias), para borrarlos. */
    fun sobrantes(c: Config, nombres: Collection<String>): List<String> {
        val usados = c.cuadros_guardados.map { it.archivo }.toSet()
        return nombres.filter { it !in usados }
    }
}

/**
 * Al importar un respaldo con datos aquí (`Importar.combinarConfig`): lo del esquema 27 de [c] (este teléfono) y de [r] (el
 * respaldo), sin perder nada de ninguno.
 *
 * - Seguidos: lo de aquí, más las entradas del respaldo que no estén ya (mismo personaje y mismo día de inicio). Una del
 *   respaldo que sigue activa no entra si aquí ya lo sigues o si ya no cabe en el [Seguir.TOPE].
 * - Cuentos leídos: uno por personaje y día; si los dos lo tienen, el de la primera lectura.
 * - Días de uso: la unión de los días de cada clave (solo claves válidas).
 * - Cuadros guardados: la unión, por id. Las imágenes llegan aparte, en `cuadros/` del respaldo.
 * - Avisos de historia: los dos; manda lo de aquí. El interruptor `avisos_historia` es el de aquí.
 */
fun combinarHistorias(c: Config, r: Config): Config {
    val seguidos = c.seguidos.toMutableList()
    for (s in r.seguidos) {
        if (seguidos.any { it.vida == s.vida && it.desde == s.desde }) continue
        if (s.activo && (seguidos.any { it.activo && it.vida == s.vida } || seguidos.count { it.activo } >= Seguir.TOPE)) continue
        seguidos += s
    }
    val leidos = (c.cuentos_leidos + r.cuentos_leidos).groupBy { it.vida to it.dia }.values
        .map { l -> l.minWith(compareBy<CuentoLeido>({ it.leido }, { it.ts ?: "￿" })) }
    val claves = (c.uso_dias.keys + r.uso_dias.keys).filter { UsoDias.valida(it) }
    val uso = claves.associateWith { k -> (c.uso_dias[k].orEmpty() + r.uso_dias[k].orEmpty()).distinct().sorted() }
    return c.copy(
        seguidos = seguidos, cuentos_leidos = leidos, uso_dias = uso,
        cuadros_guardados = (c.cuadros_guardados + r.cuadros_guardados).distinctBy { it.id },
        historia_avisada = r.historia_avisada + c.historia_avisada,
    )
}
