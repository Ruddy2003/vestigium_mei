package io.github.ruddy2003.vestigium.data

/**
 * «Conocerte»: las opciones de cada pregunta, cómo se comparan dos versiones del perfil (para guardar la historia
 * de los cambios) y lo que los análisis leen de él (quiénes son tus cercanos, dónde está tu casa).
 */
object Perfiles {
    val VIVE_CON = listOf(
        Opcion("solo", "Solo o sola"), Opcion("pareja", "Pareja"), Opcion("hijos", "Hijos"), Opcion("padres", "Mamá o papá"),
        Opcion("hermanos", "Hermanos"), Opcion("abuelos", "Abuelos"), Opcion("familia", "Otros familiares"),
        Opcion("companeros", "Compañeros de piso"), Opcion("amistades", "Amistades"),
    )
    val ESPECIES = listOf(Opcion("perro", "Perro"), Opcion("gato", "Gato"), Opcion("ave", "Ave"), Opcion("pez", "Pez"), Opcion("roedor", "Roedor"),
        Opcion("reptil", "Reptil"), Opcion("otro", "Otro"))
    val RELACIONES = listOf(Opcion("pareja", "Pareja"), Opcion("familia", "Familia"), Opcion("amistad", "Amistad"), Opcion("trabajo", "Trabajo o escuela"), Opcion("otra", "Otra"))
    val PAREJA = listOf(Opcion("soltero", "Sin pareja"), Opcion("saliendo", "Saliendo con alguien"), Opcion("pareja", "En pareja"),
        Opcion("casado", "Casado o en unión"), Opcion("separado", "Separado o divorciado"), Opcion("viudo", "Viudo o viuda"), Opcion("prefiero_no", "Prefiero no decir"))
    val OCUPACION = listOf(Opcion("estudio", "Estudio"), Opcion("trabajo", "Trabajo"), Opcion("propio", "Por mi cuenta"), Opcion("busco", "Busco trabajo"),
        Opcion("hogar", "Hogar o cuidados"), Opcion("jubilado", "Jubilado o jubilada"))
    val HORARIO = listOf(Opcion("manana", "De mañana"), Opcion("tarde", "De tarde"), Opcion("noche", "De noche"), Opcion("turnos", "Turnos que cambian"),
        Opcion("flexible", "Flexible o desde casa"))
    val CRONOTIPO = listOf(Opcion("muy_manana", "Muy de mañana"), Opcion("manana", "Algo de mañana"), Opcion("intermedio", "Intermedio"),
        Opcion("noche", "Algo de noche"), Opcion("muy_noche", "Muy de noche"))
    val EJERCICIO = listOf(Opcion("nunca", "Casi nunca"), Opcion("1_2", "1 o 2 días por semana"), Opcion("3_4", "3 o 4 días"), Opcion("5", "5 o más"))
    val ALCOHOL = listOf(Opcion("nunca", "Nunca"), Opcion("ocasional", "En ocasiones"), Opcion("semanal", "Cada semana"), Opcion("diario", "Casi a diario"))
    val TABACO = listOf(Opcion("no", "No"), Opcion("ocasional", "A veces"), Opcion("diario", "A diario"))
    val CAFE = listOf(Opcion("no", "No tomo"), Opcion("1", "Uno al día"), Opcion("2_3", "2 o 3"), Opcion("4", "4 o más"))

    val SEXO = listOf(Opcion("hombre", "Hombre"), Opcion("mujer", "Mujer"))
    val ESTUDIOS = listOf(Opcion("secundaria", "Secundaria"), Opcion("prepa", "Preparatoria"), Opcion("tecnico", "Técnico"), Opcion("licenciatura", "Licenciatura"),
        Opcion("maestria", "Maestría"), Opcion("doctorado", "Doctorado"))
    val MODALIDAD = listOf(Opcion("presencial", "Presencial"), Opcion("hibrido", "Híbrido"), Opcion("remoto", "Desde casa"))
    val TRANSPORTE = listOf(Opcion("pie", "A pie"), Opcion("bici", "Bici"), Opcion("publico", "Transporte público"), Opcion("auto", "Auto"), Opcion("moto", "Moto"))
    val VIVIENDA = listOf(Opcion("casa", "Casa"), Opcion("departamento", "Departamento"), Opcion("cuarto", "Cuarto"), Opcion("residencia", "Residencia"))
    val DIETA = listOf(Opcion("omnivora", "Como de todo"), Opcion("flexitariana", "Poca carne"), Opcion("pescetariana", "Pescetariana"),
        Opcion("vegetariana", "Vegetariana"), Opcion("vegana", "Vegana"))

    private fun nombre(l: List<Opcion>, id: String) = l.firstOrNull { it.id == id }?.nombre ?: id
    private fun op(l: List<Opcion>, id: String) = id.takeIf { it.isNotBlank() }?.let { nombre(l, it) }.orEmpty()

    /** Sexo para las referencias de salud: el de Conocerte, o el que elegiste en Salud. */
    fun sexo(): String = Repo.config.perfil.sexo.ifBlank { Repo.config.salud_sexo }

    /** No dijiste nada todavía. */
    fun vacio(p: Perfil): Boolean = p == Perfil()

    /** Cada respuesta como texto legible, para comparar versiones y para exportar. */
    fun campos(p: Perfil): LinkedHashMap<String, String> = linkedMapOf(
        "nombre" to p.nombre,
        "nacimiento" to p.nacimiento,
        "ciudad" to listOf(p.ciudad, p.pais).filter { it.isNotBlank() }.joinToString(", "),
        "vive con" to p.vive_con.joinToString(", ") { nombre(VIVE_CON, it).lowercase() },
        "quiénes viven contigo" to p.convivientes.joinToString(", ") { "@$it" },
        "mascotas" to p.mascotas.joinToString(", ") { m -> m.nombre + if (m.especie.isNotBlank()) " (${nombre(ESPECIES, m.especie).lowercase()})" else "" },
        "a quién ves más" to p.cercanas.joinToString(", ") { c -> "@${c.nombre}" + if (c.relacion.isNotBlank()) " (${nombre(RELACIONES, c.relacion).lowercase()})" else "" },
        "pareja" to p.pareja.takeIf { it.isNotBlank() }?.let { nombre(PAREJA, it) }.orEmpty(),
        "a qué te dedicas" to (p.ocupacion.map { nombre(OCUPACION, it).lowercase() } + listOfNotNull(p.ocupacion_detalle.ifBlank { null })).joinToString(", "),
        "horario" to p.horario.takeIf { it.isNotBlank() }?.let { nombre(HORARIO, it) }.orEmpty(),
        "de mañana o de noche" to p.cronotipo.takeIf { it.isNotBlank() }?.let { nombre(CRONOTIPO, it) }.orEmpty(),
        "ejercicio" to p.ejercicio.takeIf { it.isNotBlank() }?.let { nombre(EJERCICIO, it) }.orEmpty(),
        "alcohol" to p.alcohol.takeIf { it.isNotBlank() }?.let { nombre(ALCOHOL, it) }.orEmpty(),
        "tabaco" to p.tabaco.takeIf { it.isNotBlank() }?.let { nombre(TABACO, it) }.orEmpty(),
        "café" to p.cafe.takeIf { it.isNotBlank() }?.let { nombre(CAFE, it) }.orEmpty(),
        "lo que está pasando" to p.momento,
        "sexo" to op(SEXO, p.sexo),
        "estatura" to (p.estatura_cm?.let { "$it cm" } ?: ""),
        "ciudad natal" to p.ciudad_natal,
        "estudios" to op(ESTUDIOS, p.estudios),
        "modalidad" to op(MODALIDAD, p.modalidad),
        "horas a la semana" to (p.horas_semana?.toString() ?: ""),
        "transporte" to op(TRANSPORTE, p.transporte),
        "vivienda" to op(VIVIENDA, p.vivienda),
        "hijos" to (p.hijos?.toString() ?: ""),
        "dieta" to op(DIETA, p.dieta),
        "deporte" to p.deporte,
        "horario de sueño ideal" to if (p.dormir_ideal.isBlank() && p.despertar_ideal.isBlank()) "" else "${p.dormir_ideal.ifBlank { "?" }} a ${p.despertar_ideal.ifBlank { "?" }}",
        "idiomas que aprendes" to p.idiomas.joinToString(", "),
        "metas del año" to p.metas.joinToString(" | "),
    )

    /** Lo que cambió de [antes] a [despues], con la fecha de hoy. */
    fun cambios(antes: Perfil, despues: Perfil, fecha: String = Fechas.diaLogico()): List<CambioPerfil> {
        val a = campos(antes); val b = campos(despues)
        return b.keys.filter { a[it] != b[it] }.map { CambioPerfil(fecha, it, a[it].orEmpty(), b[it].orEmpty()) }
    }

    /** Qué es para ti una persona del diario (por su @), según lo que dijiste: pareja, familia, amistad, trabajo, casa. */
    fun relacion(persona: String, p: Perfil = Repo.config.perfil, fecha: String? = null): String? {
        // Primero lo que dijiste en «Tu gente» (con la relación que había ese día); luego «Conocerte».
        Personas.relacionEn(persona, fecha)?.let { return Personas.grupo(it) }
        p.cercanas.firstOrNull { it.nombre == persona }?.let { return it.relacion.ifBlank { "otra" } }
        if (persona in p.convivientes) return "casa"
        return null
    }

    /** Tu casa en el mapa, si dijiste en qué ciudad vives. */
    fun casa(p: Perfil = Repo.config.perfil): Pair<Double, Double>? =
        if (p.ciudad_lat != null && p.ciudad_lon != null) p.ciudad_lat to p.ciudad_lon else null

    /** Edad en años cumplidos en [fecha], si dijiste cuándo naciste. */
    fun edad(fecha: String = Fechas.diaLogico(), p: Perfil = Repo.config.perfil): Int? = runCatching {
        if (p.nacimiento.isBlank()) return null
        java.time.Period.between(Fechas.parse(p.nacimiento), Fechas.parse(fecha)).years
    }.getOrNull()
}
