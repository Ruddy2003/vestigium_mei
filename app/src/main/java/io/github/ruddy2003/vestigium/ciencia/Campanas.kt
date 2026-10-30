package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Campana
import io.github.ruddy2003.vestigium.data.Enfoque
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.TemaId

/**
 * Campañas semanales: retos opcionales de lunes a domingo («5 noches de 7 horas»). Cada día que cumples avanzas una
 * etapa en el mapa de la campaña; al llegar a la meta, la ganas. Cada tema le pone su narrativa.
 */
object Campanas {
    /** Un reto: qué cuenta un día como cumplido (null = ese día no hay dato). */
    class Reto(val id: String, val nombre: String, val descripcion: String, val meta: Int, val cumple: (String) -> Boolean?)

    fun retos(): List<Reto> {
        val base = listOf(
            Reto("sueno7", "Noches de 7 horas", "Dormir 7 horas o más (según el reloj)", 5) { f -> Repo.salud[f]?.sueno_min?.let { it >= 420 } },
            Reto("temprano", "Dormir antes de medianoche", "Quedarte dormido antes de las 00:00", 4) { f ->
                Repo.salud[f]?.sueno_inicio?.let { s -> runCatching { Fechas.localDe(s).hour in 18..23 }.getOrNull() }
            },
            Reto("pasos", "8,000 pasos", "Caminar 8,000 pasos o más", 5) { f -> Repo.salud[f]?.pasos?.let { it >= 8000 } },
            Reto("diario", "Escribir el diario", "Escribir la nota del día", 6) { f -> Repo.dias[f]?.nota?.isNotBlank() ?: false },
            Reto("enfoque", "Una hora de enfoque", "60 minutos o más con el cronómetro de enfoque", 5) { f -> Enfoque.delDia(f).filterKeys { it != "siesta" }.values.sum() >= 60 },
            Reto("pantalla", "Menos de 3 horas de pantalla", "Usar el teléfono menos de 3 horas", 4) { f -> Repo.pantalla[f]?.total_min?.let { it < 180 } },
            Reto("movimiento", "Entrenar o escalar", "Un entrenamiento, una sesión de escalada o ejercicio del reloj", 3) { f ->
                (Repo.entrenos[f]?.series ?: 0) > 0 || Repo.ascensos.any { it.fecha == f } || Repo.sesiones.any { it.fecha == f }
            },
            Reto("leer", "Leer", "Leer un libro o artículo (marcado en la biblioteca o con el cronómetro)", 5) { f ->
                Repo.obras.any { o -> (o.tipo == "libro" || o.tipo == "articulo") && f in o.dias } || (Enfoque.delDia(f)["lectura"] ?: 0) > 0
            },
            Reto("reaccion", "Prueba de reacción", "Hacer la prueba de reacción", 5) { f -> Repo.reacciones.any { it.fecha == f } },
            Reto("animo", "Contestar los avisos de ánimo", "Contestar al menos dos avisos de ánimo", 5) { f -> Repo.momentos.count { it.fecha == f && it.tipo == "muestreo" } >= 2 },
        )
        val habitos = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().filter { it.activo }.map { h -> Reto("habito:${h.id}", h.nombre, "Cumplir «${h.nombre}»", 5) { f -> Repo.dias[f]?.habitos?.get(h.id) == true } }
        return base + habitos
    }

    /**
     * El reto de una campaña. Un hábito que ya desactivaste se sigue encontrando: si no, al desactivarlo se perderían
     * las campañas que ganaste con él y sus logros.
     */
    fun reto(id: String): Reto? = retos().firstOrNull { it.id == id } ?: id.takeIf { it.startsWith("habito:") }?.let { hid ->
        io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().firstOrNull { "habito:${it.id}" == hid }
            ?.let { h -> Reto(hid, h.nombre, "Cumplir «${h.nombre}»", 5) { f -> Repo.dias[f]?.habitos?.get(h.id) == true } }
    }

    /** Primer y último día que cuenta de una campaña: desde que la aceptaste y hasta que la abandonaste (los dos incluidos). */
    fun tramo(c: Campana): Pair<String, String> {
        val domingo = Fechas.sumar(c.lunes, 6)
        val ini = c.aceptada?.takeIf { it > c.lunes }?.let { minOf(it, domingo) } ?: c.lunes
        val fin = c.abandonada?.let { minOf(it, domingo) } ?: domingo
        return ini to fin
    }

    /** Nombre de la variable del panel de un reto: campana_sueno7, campana_hab_leer… */
    fun variable(reto: String): String = "campana_" + if (reto.startsWith("habito:")) "hab_" + reto.removePrefix("habito:") else reto

    /**
     * Por fecha, los retos que tenías en campaña ese día (P-11). Codifica la asignación (la tenías aceptada), no el cumplimiento.
     * Días hasta [hoy].
     */
    fun diasActivos(campanas: List<Campana>, hoy: String): Map<String, Set<String>> {
        val m = HashMap<String, MutableSet<String>>()
        for (c in campanas) {
            val (ini, fin) = tramo(c)
            var f = ini
            while (f <= fin && f <= hoy) { m.getOrPut(f) { mutableSetOf() } += c.reto; f = Fechas.sumar(f, 1) }
        }
        return m
    }

    /**
     * Las variables del panel que cada reto busca mover: su correlación con la campaña es esperable (la elegiste para eso)
     * y no dice nada nuevo. Las de un hábito, con su variable hab_.
     */
    val MUEVE: Map<String, Set<String>> = mapOf(
        "sueno7" to setOf("sueno_horas", "sueno_profundo", "sueno_rem", "sueno_inicio_h", "sueno_fin_h", "sueno_calidad"),
        "temprano" to setOf("sueno_inicio_h", "sueno_horas", "ultimo_uso_h", "carga_noche_h", "cama_min"),
        "pasos" to setOf("pasos", "a_pie_min", "distancia_km", "cal_activas", "aire_libre_min"),
        "diario" to setOf("palabras", "personas", "hitos_n"),
        "enfoque" to setOf("enfoque_min", "enfoque_estudio_min", "enfoque_lectura_min", "enfoque_idiomas_min", "enfoque_tesis_min", "enfoque_trabajo_min", "enfoque_creativo_min", "laptop_productivo_min"),
        "pantalla" to setOf("pantalla_min", "desbloqueos", "primer_uso_h", "ultimo_uso_h", "notificaciones"),
        "movimiento" to setOf("ejercicio_min", "gimnasio", "volumen_kg", "series_gimnasio", "fuerza_ref_kg", "escalada", "escalada_grado", "cal_activas"),
        "leer" to setOf("lectura", "enfoque_lectura_min", "obras_dia"),
        "reaccion" to setOf("rt_media"),
        "animo" to setOf("muestreo_valencia", "muestreo_activacion", "avisos_respondidos", "tasa_respuesta", "n_muestreos", "registros_animo"),
    )

    /** Si la variable [id] es de una campaña y [otra] es lo que ese reto busca mover (o el conteo de campañas). */
    fun obvia(id: String, otra: String): Boolean {
        if (id == "campanas_n") return otra.startsWith("campana_")
        if (!id.startsWith("campana_")) return false
        if (otra == "campanas_n") return true
        val reto = id.removePrefix("campana_")
        if (reto.startsWith("hab_")) return otra == reto
        return MUEVE[reto]?.contains(otra) == true
    }

    class Estado(val c: Campana, val reto: Reto?, val dias: List<Pair<String, Boolean?>>, val hechos: Int, val ganada: Boolean, val perdida: Boolean, val terminada: Boolean, val ganadaEl: String?)

    fun estado(c: Campana, hoy: String = Fechas.diaLogico()): Estado {
        val r = reto(c.reto)
        // Solo cuentan los días desde que la aceptaste y hasta que la abandonaste: no se gana en el acto de aceptarla.
        val (ini, fin) = tramo(c)
        val dias = (0 until 7).map { i -> val f = Fechas.sumar(c.lunes, i.toLong()); f to (if (f > hoy || f < ini || f > fin || r == null) null else r.cumple(f)) }
        var n = 0; var ganadaEl: String? = null
        dias.forEach { (f, ok) -> if (ok == true) { n++; if (n == c.meta && ganadaEl == null) ganadaEl = f } }
        val quedan = if (c.abandonada != null) 0 else dias.count { it.first > hoy } + if (dias.any { it.first == hoy && it.second != true }) 1 else 0
        val terminada = Fechas.sumar(c.lunes, 6) < hoy || c.abandonada != null
        val ganada = n >= c.meta
        return Estado(c, r, dias, n, ganada, !ganada && n + quedan < c.meta, terminada, ganadaEl)
    }

    /** Las campañas vigentes de esta semana (las abandonadas no). */
    fun deLaSemana(hoy: String = Fechas.diaLogico()): List<Campana> { val l = Fechas.lunesDe(hoy); return Repo.config.campanas.filter { it.lunes == l && it.abandonada == null } }

    /** Días en que ganaste cada campaña, ordenados (para los logros). */
    fun victorias(hoy: String = Fechas.diaLogico()): List<String> = Repo.config.campanas.mapNotNull { estado(it, hoy).ganadaEl }.sorted()

    // ---------------------------------------------------------------- narrativa por tema

    /**
     * Nombres de campaña de cada tema (se elige uno por semana) y las siete etapas del mapa. Todo inventado o histórico
     * general. Ningún nombre se repite entre temas. Un id de tema que ya no exista cae en el romano.
     */
    fun titulo(tema: String, lunes: String, reto: String): String {
        val l = titulos(temaDe(tema))
        val i = ((lunes + reto).hashCode() and 0x7fffffff) % l.size
        return l[i]
    }

    fun etapas(tema: String): List<String> = etapas(temaDe(tema))

    private fun temaDe(id: String): TemaId = TemaId.entries.firstOrNull { it.id == id } ?: TemaId.ROMANO

    /** Los títulos de campaña por id de tema (para quien los busca por texto). */
    internal val TITULOS: Map<String, List<String>> by lazy { TemaId.entries.associate { it.id to titulos(it) } }

    /** Las siete etapas por id de tema. */
    internal val ETAPAS: Map<String, List<String>> by lazy { TemaId.entries.associate { it.id to etapas(it) } }

    fun titulos(t: TemaId): List<String> = when (t) {
        TemaId.ROMANO -> listOf("Campaña de las Galias", "Marcha a Britania", "La vía Apia", "Expedición a Dacia")
        TemaId.URBE -> listOf("Il giro dei rioni", "Dal Tevere al Gianicolo", "La passeggiata delle fontane", "Di cupola in cupola")
        TemaId.CIBER -> listOf("RUTINA_7DIAS.EXE", "HACK DEL HÁBITO", "PROTOCOLO NOCTURNO", "BUCLE DE LA SEMANA")
        TemaId.NEON -> listOf("OP: NEON RUN", "BAJADA AL NIVEL −7", "LA RUTA DE LOS PUESTOS", "SIETE NOCHES EN LA CALLE")
        TemaId.HELENO -> listOf("La marcha de los diez mil", "Hacia el Indo", "La travesía del Egeo", "Los juegos de Olimpia")
        TemaId.EGIPTO -> listOf("Río arriba hasta la primera catarata", "La barca de Amón", "Las canteras de granito", "La crecida del Nilo")
        TemaId.MEDIEVAL -> listOf("Peregrinación a Compostela", "La copia del códice", "El camino de los monasterios", "La feria de Champaña")
        TemaId.TEMPLARIO -> listOf("La marcha de las atalayas", "Relevo en Montalbar", "La escolta de los peregrinos", "Cabalgada al Pozo del Halcón")
        TemaId.DISTOPIA -> listOf("PLAN TRIMESTRAL Nº 7", "CUOTA DEL MINISTERIO", "OPERACIÓN DISCIPLINA", "DIRECTIVA 451")
        TemaId.REFUGIO -> listOf("Expedición a la superficie", "Reparar el purificador", "Turno de mantenimiento", "Ruta de suministros")
        TemaId.ORDEN -> listOf("Vigilia del archivo", "Patrulla del páramo", "Recuperar la reliquia", "La forja")
        TemaId.VAPOR -> listOf("El expreso del Norte", "La línea de Valcarbón", "El correo nocturno", "Siete estaciones en el horario")
        TemaId.LABORATORIO -> listOf("Ensayo semanal", "Protocolo de siete días", "Serie experimental B", "Cultivo controlado")
        TemaId.COLONOS -> listOf("La caravana de carretas", "Fundar el pueblo", "Cruzar el río", "El camino del oeste")
        TemaId.NAPOLEON -> listOf("La campaña de 1805", "De Boulogne a Austerlitz", "La marcha sobre Ulm", "El sol de Austerlitz")
        TemaId.BOTANICO -> listOf("Expedición por los biomas", "Del jardín a la selva", "Herbario de la semana", "La ruta de las semillas")
        TemaId.RUINAS -> listOf("Travesía por la ciudad verde", "De la autopista a la torre", "Calle por calle", "La ronda de las azoteas")
        TemaId.NAVIDAD -> listOf("La ruta de los mercados", "De Estrasburgo a Salzburgo", "Pueblo por pueblo", "Los mercados de Adviento")
        TemaId.SEDA -> listOf("De Chang'an a Antioquía", "La caravana de Kashgar", "Hacia Samarcanda", "El oasis de Dunhuang")
        TemaId.GOTICO -> listOf("La construcción de la catedral", "El vitral del rosetón", "Las horas canónicas", "La peregrinación")
        TemaId.ARCANO -> listOf("Por la costa hasta el faro", "El pueblo en la niebla", "Los legajos que faltan", "La señal del faro apagado")
        TemaId.ESPACIO -> listOf("Misión a Marte", "Órbita lunar", "Cinturón de asteroides", "Estación profunda")
        TemaId.ABISMO -> listOf("Bajada a la fosa", "Por las capas del océano", "Rumbo a la llanura abisal", "La inmersión de la semana")
        TemaId.ANDALUS -> listOf("De Toledo a Granada", "La ruta de las ciudades", "La biblioteca de los califas", "Del Guadalquivir a la Alhambra")
        TemaId.MONGOL -> listOf("La ruta del yam", "Las postas de la estepa", "Rumbo a Karakórum", "El correo del imperio")
        TemaId.RENACIMIENTO -> listOf("El taller de Florencia", "El cuaderno de las máquinas", "La cúpula", "El retrato")
        TemaId.PIRATA -> listOf("La carta de las siete islas", "Los doblones de la X", "Rumbo a barlovento", "El cofre enterrado")
        TemaId.URSS -> listOf("PLAN QUINQUENAL", "LA COSECHA DEL KOLJÓS", "EL TRANSIBERIANO", "LA PRESA")
        TemaId.REVOLUCION -> listOf("Las jornadas de 1789", "La toma de la Bastilla", "La fiesta de la Federación", "El cuaderno de quejas")
        TemaId.ANTIGUAS -> listOf("La travesía de la compañía", "El paso de las montañas", "La mina olvidada", "Rumbo a la fortaleza")
    }

    /** Las siete etapas del mapa, en el orden de la ruta (cuadran con `MapasMas.puntos`). */
    fun etapas(t: TemaId): List<String> = when (t) {
        TemaId.ROMANO -> listOf("Roma", "Ostia", "Massilia", "Lugdunum", "Durocortorum", "Lutetia", "Alesia")
        TemaId.URBE -> listOf("Testaccio", "Trastevere", "Gianicolo", "Borgo", "Piazza Navona", "Pantheon", "Fontana di Trevi")
        TemaId.CIBER -> listOf("localhost", "pasarela-01", "proxy-02", "muro-03", "dns-04", "espejo-05", "raíz")
        TemaId.NEON -> listOf("Azotea", "Nivel −1", "Nivel −2 · mercado", "Nivel −3 · callejón", "Nivel −4 · baños", "Nivel −5 · andén", "El fondo")
        TemaId.HELENO -> listOf("Pella", "Anfípolis", "Troya", "Sardes", "Gordio", "Tarso", "Babilonia")
        TemaId.EGIPTO -> listOf("El Delta", "Menfis", "Hermópolis", "Abidos", "Tebas", "Edfu", "Elefantina")
        TemaId.MEDIEVAL -> listOf("Roncesvalles", "Pamplona", "Logroño", "Burgos", "León", "Astorga", "Santiago")
        // De fortaleza en fortaleza por una frontera inventada, de la costa al castillo de las montañas.
        TemaId.TEMPLARIO -> listOf("Encomienda de la costa", "Atalaya del Alba", "Torre de la Sed", "Pozo del Halcón", "Vado de las Piedras Rojas", "Peña del Cuervo", "Castillo de Montalbar")
        TemaId.DISTOPIA -> listOf("VENTANILLA 1", "REGISTRO", "SELLADO", "REVISIÓN", "ARCHIVO", "COMISIÓN", "MINISTERIO")
        TemaId.REFUGIO -> listOf("Dormitorios", "Comedor", "Hidroponía", "Taller", "Purificador", "Esclusa", "Superficie")
        TemaId.ORDEN -> listOf("Campamento", "Puente de hierro", "Cota 214", "Desfiladero", "Trincheras", "Bastión", "Fortaleza")
        // Una línea de ferrocarril inventada, del depósito de locomotoras a la estación terminal.
        TemaId.VAPOR -> listOf("Depósito de locomotoras", "Apeadero del Molino", "Viaducto de los Siete Arcos", "Túnel de la Sierra Negra", "Aguada de Valcarbón", "Empalme de Ferrería", "Estación Terminal del Norte")
        TemaId.LABORATORIO -> listOf("Hipótesis", "Reactivos", "Siembra", "Incubación", "Medición", "Análisis", "Cultivo completo")
        TemaId.COLONOS -> listOf("Puerto", "Vado del río", "Pradera", "Cruce", "Colina", "Valle", "Pueblo nuevo")
        TemaId.NAPOLEON -> listOf("Boulogne", "Kehl", "Ulm", "Múnich", "Schönbrunn", "Brünn", "Austerlitz")
        TemaId.BOTANICO -> listOf("Jardín botánico", "Bosque templado", "Sabana", "Desierto", "Selva", "Manglar", "Invernadero")
        TemaId.RUINAS -> listOf("Autopista rota", "Gasolinera de los helechos", "Avenida de la hiedra", "Mercado inundado", "Biblioteca municipal", "Paso a desnivel", "Azotea de la torre")
        TemaId.NAVIDAD -> listOf("Estrasburgo", "Colmar", "Núremberg", "Dresde", "Praga", "Viena", "Salzburgo")
        TemaId.SEDA -> listOf("Chang'an", "Dunhuang", "Turfán", "Kashgar", "Samarcanda", "Bujará", "Antioquía")
        TemaId.GOTICO -> listOf("Cimientos", "Muros", "Columnas", "Arbotantes", "Bóveda", "Vitrales", "Campanas")
        TemaId.ARCANO -> listOf("La estación del tren", "La posada del puerto", "El archivo municipal", "El muelle viejo", "La marisma", "Los acantilados", "El faro")
        TemaId.ESPACIO -> listOf("Plataforma", "Órbita baja", "Luna", "Lagrange", "Fobos", "Órbita marciana", "Marte")
        TemaId.ABISMO -> listOf("Muelle", "Zona iluminada", "Zona crepuscular", "Zona de medianoche", "Llanura abisal", "Zona hadal", "Fondo de la fosa")
        TemaId.ANDALUS -> listOf("Toledo", "Calatrava", "Córdoba", "Sevilla", "Málaga", "Almería", "Granada")
        TemaId.MONGOL -> listOf("Janbalic", "Shangdu", "Posta del Gobi", "Posta del pozo", "Kerulen", "Valle del Orjón", "Karakórum")
        TemaId.RENACIMIENTO -> listOf("Boceto", "Estudio", "Cartón", "Imprimación", "Color", "Veladura", "Firma")
        TemaId.PIRATA -> listOf("Puerto de la Brea", "Cayo de las Gaviotas", "Arrecife del Ahorcado", "Bahía del Ron", "Isla de los Cocos", "Cala de los Huesos", "Isla de la X")
        TemaId.URSS -> listOf("MOSCÚ", "EKATERIMBURGO", "NOVOSIBIRSK", "KRASNOYARSK", "IRKUTSK", "CHITÁ", "VLADIVOSTOK")
        TemaId.REVOLUCION -> listOf("Estados Generales", "Juego de pelota", "Bastilla", "4 de agosto", "Declaración", "Versalles", "Federación")
        TemaId.ANTIGUAS -> listOf("La aldea", "El bosque viejo", "El vado", "Las montañas", "La mina", "El puente de piedra", "La fortaleza")
    }
}
