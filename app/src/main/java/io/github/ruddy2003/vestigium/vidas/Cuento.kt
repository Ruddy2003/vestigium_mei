package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import io.github.ruddy2003.vestigium.vidas.piezas.TodasLasPiezas
import java.time.LocalDate

/*
 * El cuento del día: «hoy, en su vida». Ocho párrafos, uno por cada lugar de [Parrafo], armados con las piezas del
 * personaje y con lo que dice su ficha (la estación, el tiempo, el objeto del día, lo que pasa en su mundo y lo que
 * hace a esta hora). En un día especial, tres o cuatro párrafos se cambian por texto escrito para ese día.
 *
 * Es Kotlin puro y no lee ningún dato tuyo. Los textos de cada personaje están en `vidas/piezas/`; este archivo es
 * solo el motor y las tandas de textos no lo tocan.
 */

/** El género gramatical del personaje, para la concordancia: en un texto, `{cansado|cansada}` elige según este. */
enum class Genero { MASCULINO, FEMENINO }

/**
 * Una variante de un párrafo.
 *
 * - [estado]: solo sale cuando el personaje está en ese estado de su calendario («mar»: embarcado; ver [Estado]); sin
 *   estado, es la de todos los días.
 *   Si para el estado de hoy un lugar no tiene variantes propias, se usan las de todos los días.
 * - [hacer]: solo sale cuando a esta hora el personaje hace eso, según «Un día suyo» (para el párrafo del trabajo).
 * - [desde] y [hasta]: el id de un día especial del calendario. La variante vale desde el día en que pasa [desde] y
 *   hasta la víspera de [hasta] («su padre», hasta el día en que muere; «la niña», desde que nace).
 * - [meses]: solo sale en esos meses (de 1 a 12), todos los años: lo que solo se hace en verano o solo en invierno.
 */
class Variante(val texto: String, val estado: String? = null, val hacer: OtrasVidas.Hacer? = null, val desde: String? = null, val hasta: String? = null, val meses: Set<Int>? = null)

/**
 * Un objeto del día. [el] y [un]: su nombre con artículo, en minúsculas («el remo», «un remo»). [titulo]: el título
 * del cuento en un día normal. [dato]: una oración completa sobre él, que entra en el hueco `{dato_objeto}`.
 */
class ObjetoCuento(val clave: String, val el: String, val un: String, val titulo: String, val dato: String)

/** Una frase que entra en `{entorno}` cuando el arco [arco] vale entre [desde] y [hasta]. Manda la primera regla que se cumple. */
class Entorno(val arco: String, val desde: Double, val hasta: Double = 1.0, val frases: List<String>)

/**
 * El texto de un día especial. [dias]: para cada día suyo (el primero, el segundo…), los párrafos que se cambian; el
 * primer día cambia tres o cuatro. [sigue]: para los días que no tienen texto propio (el día 12 de una cosecha), uno
 * o dos lugares con varias versiones, que se van turnando.
 */
class TextoEspecial(val dias: List<Map<Parrafo, String>>, val sigue: Map<Parrafo, List<String>> = emptyMap())

/**
 * Todas las piezas del cuento de un personaje.
 *
 * - [variantes]: de 6 a 8 por lugar (más las de cada estado).
 * - [cielos]: oraciones para `{cielo}`, por tiempo. [estaciones]: oraciones para `{de_estacion}`, por estación.
 * - [objetos]: uno por cada objeto de su calendario.
 * - [mundos]: oraciones para `{mundo}`, por el estado del mundo de la temporada («guerra», «peste»).
 * - [especiales]: el texto de cada día especial, por su id. Los de las [temporadasEscritas] tienen que estar todos.
 */
class Piezas(
    val vida: String, val genero: Genero, val temporadasEscritas: Set<Int>,
    val variantes: Map<Parrafo, List<Variante>>, val cielos: Map<Clima, List<String>>, val estaciones: Map<Estacion, List<String>>,
    val objetos: List<ObjetoCuento>, val mundos: Map<String, List<String>> = emptyMap(), val entornos: List<Entorno> = emptyList(),
    val especiales: Map<String, TextoEspecial> = emptyMap(),
) {
    /** Todos los textos, para revisarlos de una vez. */
    fun textos(): List<String> = variantes.values.flatten().map { it.texto } + cielos.values.flatten() + estaciones.values.flatten() + objetos.flatMap { listOf(it.titulo, it.dato, it.el, it.un) } +
        mundos.values.flatten() + entornos.flatMap { it.frases } + especiales.values.flatMap { t -> t.dias.flatMap { it.values } + t.sigue.values.flatten() }

    /** Todo lo que está mal en estas piezas frente al calendario [cal]; vacío si están bien. Lo usan las pruebas de cada tanda de textos. */
    fun problemas(cal: Calendario): List<String> {
        val p = mutableListOf<String>()
        val quien = OtrasVidas.porId(vida)
        if (quien == null || cal.vida != vida) return listOf("Las piezas de «$vida» no corresponden al calendario de «${cal.vida}»")
        val ids = cal.temporadas.flatMap { it.dias }.map { it.id }.toSet()

        Parrafo.entries.forEach { lugar ->
            val l = variantes[lugar].orEmpty()
            val deSiempre = l.filter { it.estado == null && it.desde == null && it.hasta == null && it.meses == null }
            if (lugar == Parrafo.TRABAJO) quien.dia.map { it.que }.toSet().forEach { h ->
                if (deSiempre.count { it.hacer == h } < 3) p += "${lugar.nombre}: hacen falta 3 variantes de todos los días para «${h.nombre}»"
            } else if (deSiempre.size < 5) p += "${lugar.nombre}: hacen falta al menos 5 variantes que valgan todos los días y todo el año (hay ${deSiempre.size})"
            if (lugar != Parrafo.TRABAJO && l.count { it.estado == null } < 6) p += "${lugar.nombre}: hacen falta 6 variantes (hay ${l.count { it.estado == null }})"
        }
        variantes.values.flatten().forEach { v ->
            listOfNotNull(v.desde, v.hasta).forEach { if (it !in ids) p += "Una variante depende del día «$it», que no está en el calendario" }
            if (v.estado != null && cal.estados.none { it.clave == v.estado }) p += "Una variante es del estado «${v.estado}», que no está en el calendario"
            if (v.meses != null && (v.meses.isEmpty() || v.meses.any { it !in 1..12 })) p += "Una variante tiene meses que no van de 1 a 12"
        }
        entornos.forEach { e -> if (cal.arcos.none { it.id == e.arco }) p += "Un entorno depende del arco «${e.arco}», que no existe"; if (e.frases.isEmpty()) p += "El entorno del arco «${e.arco}» no tiene frases" }

        val climas = cal.clima.flatMap { m -> m.filterValues { it > 0 }.keys }.toSet() + cal.temporadas.flatMap { it.dias }.mapNotNull { it.clima }
        climas.forEach { if (cielos[it].isNullOrEmpty()) p += "Falta el cielo de «${it.nombre}»" }
        cal.estaciones.map { it.second }.toSet().forEach { if (estaciones[it].isNullOrEmpty()) p += "Falta la estación «${it.nombre}»" }
        if (objetos.map { it.clave }.toSet() != cal.objetos.toSet()) p += "Los objetos no son los del calendario: sobran ${objetos.map { it.clave } - cal.objetos.toSet()} y faltan ${cal.objetos - objetos.map { it.clave }.toSet()}"
        mundos.keys.forEach { m -> if (cal.temporadas.none { it.mundo == m }) p += "El mundo «$m» no es de ninguna temporada" }
        temporadasEscritas.forEach { t -> if (mundos[cal.temporadas[t - 1].mundo].isNullOrEmpty()) p += "Falta el mundo «${cal.temporadas[t - 1].mundo}» de la temporada $t" }

        especiales.forEach { (id, t) ->
            if (id !in ids) p += "Hay texto para el día «$id», que no está en el calendario"
            if (t.dias.isEmpty()) p += "Día «$id»: no tiene texto para su primer día"
            else if (t.dias.first().size !in 3..4) p += "Día «$id»: el primer día cambia ${t.dias.first().size} párrafos; deben ser 3 o 4"
            if (t.dias.drop(1).any { it.isEmpty() } || t.sigue.values.any { it.isEmpty() }) p += "Día «$id»: tiene un día sin párrafos"
        }
        temporadasEscritas.forEach { n -> cal.temporadas[n - 1].dias.forEach { d ->
            val t = especiales[d.id]
            if (t == null) p += "Temporada $n: falta el texto del día «${d.id}»"
            else if (d.duracion > t.dias.size && t.sigue.isEmpty()) p += "Día «${d.id}»: dura ${d.duracion} días y solo tiene texto para ${t.dias.size}"
        } }

        val anio = Regex("""\b(1[0-9]{3}|20[0-9]{2})\b""")
        textos().forEach { s ->
            if (anio.containsMatchIn(s)) p += "Lleva un año escrito: «${s.take(60)}…»"
            HUECO.findAll(s).forEach { h -> if (h.groupValues[1] !in HUECOS && !h.groupValues[1].contains('|')) p += "Hueco desconocido «${h.value}» en «${s.take(60)}…»" }
            if (s.count { it == '{' } != s.count { it == '}' }) p += "Llaves sin cerrar en «${s.take(60)}…»"
        }
        return p
    }

    companion object {
        internal val HUECO = Regex("""\{([^{}]*)\}""")

        /** Los huecos que llena la ficha. Además, `{él|ella}` elige según el género del personaje. */
        val HUECOS = setOf("nombre", "edad", "estacion", "cielo", "de_estacion", "el_objeto", "El_objeto", "un_objeto", "dato_objeto", "entorno", "mundo")
    }
}

/** Lo que el pie del cuento dice de un día especial de hoy: qué es, qué tan cierto es y de dónde sale. */
data class NotaDia(val titulo: String, val tipo: TipoDia, val certeza: CertezaDia, val fuente: Pair<String, String>?)

/**
 * El cuento de un día. [titulo]: el del día especial que manda, o el del objeto del día. [parrafos]: los ocho, en el
 * orden de [Parrafo]. [notas]: una por cada día especial de hoy, con su certeza. [nota]: la advertencia que siempre
 * acompaña al cuento. [ficha]: la ficha de la que salió (la edad que avanza, el año, la estación).
 */
data class Cuento(val vida: String, val dia: String, val titulo: String, val parrafos: List<String>, val especial: Boolean, val notas: List<NotaDia>, val nota: String, val ficha: FichaDelDia) {
    companion object {
        const val NOTA_HISTORICA = "Relato de ficción con base histórica"
        const val NOTA_FICCION = "Relato de ficción"

        /**
         * El cuento del personaje [vidaId] en el día lógico [diaLogico], leído a la [hora] (0 a 24, con decimales).
         * Null si el personaje todavía no tiene calendario o no tiene piezas. La hora solo cambia el párrafo del trabajo.
         */
        fun de(diaLogico: String, vidaId: String, hora: Double): Cuento? {
            val quien = OtrasVidas.porId(vidaId) ?: return null
            val cal = Calendarios.de(vidaId) ?: return null
            val piezas = TodasLasPiezas.de(vidaId) ?: return null
            val ficha = FichaDelDia.de(diaLogico, vidaId) ?: return null
            return de(ficha, quien, cal, piezas, hora)
        }

        /** Lo mismo, con todo en la mano (para las pruebas). */
        fun de(ficha: FichaDelDia, quien: OtrasVidas.Vida, cal: Calendario, piezas: Piezas, hora: Double): Cuento {
            val d = LocalDate.parse(ficha.dia)
            val hoy = (ficha.temporada - 1) * 365 + MesDia.indice(d)
            val estado = ficha.estado
            val hacer = ficha.tramo(hora)?.que

            // Los días especiales de hoy, el que manda primero: sus párrafos sustituyen a los de todos los días.
            val cambios = LinkedHashMap<Parrafo, String>()
            ficha.especiales.forEachIndexed { i, e ->
                val t = piezas.especiales[e.dia.id] ?: return@forEachIndexed
                val suyos = if (e.numero <= t.dias.size) t.dias[e.numero - 1] else t.sigue.mapValues { (_, l) -> l[(e.numero - t.dias.size - 1) % l.size] }
                suyos.forEach { (lugar, texto) -> if (lugar !in cambios && (i == 0 || cambios.size < 4)) cambios[lugar] = texto }
            }

            val candidatas = Parrafo.entries.map { lugar -> candidatas(piezas.variantes[lugar].orEmpty(), cal, hoy, estado, hacer, d.monthValue) }
            val cuales = ficha.piezas(candidatas.map { maxOf(1, it.size) })
            val objeto = piezas.objetos.firstOrNull { it.clave == ficha.objeto }
            fun elegir(l: List<String>?, que: String): String = if (l.isNullOrEmpty()) "" else l[Azar(Semilla.de(ficha.vida, ficha.dia, que)).entero(l.size)]
            val huecos = mapOf(
                "nombre" to quien.nombre, "edad" to enLetras(ficha.edad), "estacion" to ficha.estacion.nombre,
                "cielo" to elegir(piezas.cielos[ficha.clima], "cielo"), "de_estacion" to elegir(piezas.estaciones[ficha.estacion], "estacion"),
                "el_objeto" to (objeto?.el ?: ""), "El_objeto" to (objeto?.el ?: "").replaceFirstChar { it.uppercase() }, "un_objeto" to (objeto?.un ?: ""), "dato_objeto" to (objeto?.dato ?: ""),
                "entorno" to elegir(piezas.entornos.firstOrNull { e -> (ficha.arcos[e.arco] ?: -1.0) in e.desde..e.hasta }?.frases, "entorno"),
                "mundo" to elegir(piezas.mundos[ficha.mundo], "mundo"),
            )
            val parrafos = Parrafo.entries.mapIndexed { i, lugar ->
                llenar(cambios[lugar] ?: candidatas[i].getOrNull(cuales[i])?.texto ?: "", huecos, piezas.genero)
            }
            val principal = ficha.principal
            return Cuento(
                vida = ficha.vida, dia = ficha.dia,
                titulo = llenar(principal?.dia?.titulo ?: objeto?.titulo ?: quien.nombre, huecos, piezas.genero),
                parrafos = parrafos, especial = ficha.especial,
                notas = ficha.especiales.map { NotaDia(it.dia.titulo, it.dia.tipo, it.dia.certeza, it.dia.fuente) },
                nota = if (quien.ficticia) NOTA_FICCION else NOTA_HISTORICA, ficha = ficha,
            )
        }

        /** El lugar, en la línea de los cinco años, del primer día especial con ese id; null si no existe. */
        private fun lugarDe(cal: Calendario, id: String): Int? {
            cal.temporadas.forEachIndexed { t, temp -> temp.dias.firstOrNull { it.id == id }?.let { return t * 365 + MesDia.indice(it.fecha) } }
            return null
        }

        /**
         * Las variantes de un lugar que pueden salir hoy. Primero se quitan las que aún no valen o ya no valen. De las
         * que quedan se prefieren, en este orden: las del estado y la actividad de ahora; las de todos los días con esa
         * actividad; las del estado sin actividad; las de todos los días sin actividad.
         */
        internal fun candidatas(todas: List<Variante>, cal: Calendario, hoy: Int, estado: String?, hacer: OtrasVidas.Hacer?, mes: Int): List<Variante> {
            val vigentes = todas.filter { v ->
                (v.meses == null || mes in v.meses) && (v.desde == null || lugarDe(cal, v.desde)?.let { hoy >= it } == true) && (v.hasta == null || lugarDe(cal, v.hasta)?.let { hoy < it } == true)
            }
            for ((e, h) in listOf(estado to hacer, null to hacer, estado to null, null to null).distinct()) {
                val l = vigentes.filter { it.estado == e && it.hacer == h }
                if (l.isNotEmpty()) return l
            }
            return vigentes.filter { it.estado == null }.ifEmpty { vigentes }
        }

        /** Llena los huecos de [texto]: los de [huecos] y los de género (`{cansado|cansada}`). Quita los espacios que sobren. */
        internal fun llenar(texto: String, huecos: Map<String, String>, genero: Genero): String =
            Piezas.HUECO.replace(texto) { m ->
                val dentro = m.groupValues[1]
                if ('|' in dentro) dentro.split('|').let { if (genero == Genero.MASCULINO) it.first() else it.last() } else huecos[dentro] ?: ""
            }.replace(Regex("""\s+"""), " ").replace(" .", ".").replace(" ,", ",").trim()

        private val UNIDADES = listOf("cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "once", "doce", "trece", "catorce", "quince",
            "dieciséis", "diecisiete", "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés", "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve")
        private val DECENAS = listOf("", "", "", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa")

        /** Un número de 0 a 99 con letras, como va delante de «años»: «treinta y un», «veintiún», «cuarenta y siete». */
        fun enLetras(n: Int): String = when {
            n !in 0..99 -> n.toString()
            n == 1 -> "un"
            n == 21 -> "veintiún"
            n < 30 -> UNIDADES[n]
            n % 10 == 0 -> DECENAS[n / 10]
            n % 10 == 1 -> "${DECENAS[n / 10]} y un"
            else -> "${DECENAS[n / 10]} y ${UNIDADES[n % 10]}"
        }
    }
}
