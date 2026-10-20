package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Campana
import io.github.ruddy2003.vestigium.data.Enfoque
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo

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
                Repo.salud[f]?.sueno_inicio?.let { s -> runCatching { java.time.OffsetDateTime.parse(s).atZoneSameInstant(Fechas.zona()).hour in 18..23 }.getOrNull() }
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
        val habitos = Repo.config.habitos.filter { it.activo }.map { h -> Reto("habito:${h.id}", h.nombre, "Cumplir «${h.nombre}»", 5) { f -> Repo.dias[f]?.habitos?.get(h.id) == true } }
        return base + habitos
    }

    fun reto(id: String): Reto? = retos().firstOrNull { it.id == id }

    class Estado(val c: Campana, val reto: Reto?, val dias: List<Pair<String, Boolean?>>, val hechos: Int, val ganada: Boolean, val perdida: Boolean, val terminada: Boolean, val ganadaEl: String?)

    fun estado(c: Campana, hoy: String = Fechas.diaLogico()): Estado {
        val r = reto(c.reto)
        val dias = (0 until 7).map { i -> val f = Fechas.sumar(c.lunes, i.toLong()); f to (if (f > hoy || r == null) null else r.cumple(f)) }
        var n = 0; var ganadaEl: String? = null
        dias.forEach { (f, ok) -> if (ok == true) { n++; if (n == c.meta && ganadaEl == null) ganadaEl = f } }
        val quedan = dias.count { it.first > hoy } + if (dias.any { it.first == hoy && it.second != true }) 1 else 0
        val terminada = Fechas.sumar(c.lunes, 6) < hoy
        val ganada = n >= c.meta
        return Estado(c, r, dias, n, ganada, !ganada && n + quedan < c.meta, terminada, ganadaEl)
    }

    fun deLaSemana(hoy: String = Fechas.diaLogico()): List<Campana> { val l = Fechas.lunesDe(hoy); return Repo.config.campanas.filter { it.lunes == l } }

    /** Días en que ganaste cada campaña, ordenados (para los logros). */
    fun victorias(hoy: String = Fechas.diaLogico()): List<String> = Repo.config.campanas.mapNotNull { estado(it, hoy).ganadaEl }.sorted()

    // ---------------------------------------------------------------- narrativa por tema

    /** Nombres de campaña de cada tema (se elige uno por semana) y las siete etapas del mapa. Todo inventado o histórico general. */
    fun titulo(tema: String, lunes: String, reto: String): String {
        val l = TITULOS[tema] ?: TITULOS.getValue("romano")
        val i = ((lunes + reto).hashCode() and 0x7fffffff) % l.size
        return l[i]
    }

    fun etapas(tema: String): List<String> = ETAPAS[tema] ?: ETAPAS.getValue("romano")

    private val TITULOS = mapOf(
        "romano" to listOf("Campaña de las Galias", "Marcha a Britania", "La vía Apia", "Expedición a Dacia"),
        "heleno" to listOf("La marcha de los diez mil", "Hacia el Indo", "La travesía del Egeo", "Los juegos de Olimpia"),
        "medieval" to listOf("Peregrinación a Compostela", "La copia del códice", "El camino de los monasterios", "La feria de Champaña"),
        "distopia" to listOf("PLAN TRIMESTRAL Nº 7", "CUOTA DEL MINISTERIO", "OPERACIÓN DISCIPLINA", "DIRECTIVA 451"),
        "refugio" to listOf("Expedición a la superficie", "Reparar el purificador", "Turno de mantenimiento", "Ruta de suministros"),
        "ciber" to listOf("OP: NEON RUN", "RUTINA_7DIAS.EXE", "HACK DEL HÁBITO", "PROTOCOLO NOCTURNO"),
        "orden" to listOf("Vigilia del archivo", "Patrulla del páramo", "Recuperar la reliquia", "La forja"),
        "laboratorio" to listOf("Ensayo semanal", "Protocolo de siete días", "Serie experimental B", "Cultivo controlado"),
        "colonos" to listOf("Levantar el granero", "La cosecha", "El camino al río", "Reconstruir el puente"),
        "napoleon" to listOf("Campaña de Italia", "La marcha sobre Austerlitz", "Expedición de Egipto", "Los cien días"),
        "botanico" to listOf("Expedición al Amazonas", "El viaje del Beagle", "Herbario de la semana", "Las islas del Pacífico"),
        "navidad" to listOf("El taller de diciembre", "La ruta del trineo", "Las doce noches", "El árbol del pueblo"),
        "seda" to listOf("De Chang'an a Samarcanda", "La caravana de Kashgar", "Hacia Bujará", "El oasis de Dunhuang"),
        "gotico" to listOf("La construcción de la catedral", "El vitral del rosetón", "Las horas canónicas", "La peregrinación"),
        "espacio" to listOf("Misión a Marte", "Órbita lunar", "Cinturón de asteroides", "Estación profunda"),
        "andalus" to listOf("El jardín del Generalife", "La ruta de Córdoba a Granada", "La biblioteca de los califas", "El patio de los leones"),
        "mongol" to listOf("Cruzar la estepa", "El gran kurultái", "La ruta del correo a caballo", "Las tierras del oeste"),
        "renacimiento" to listOf("El taller de Florencia", "El cuaderno de las máquinas", "La cúpula", "El retrato"),
        "urss" to listOf("PLAN QUINQUENAL", "LA COSECHA DEL KOLJÓS", "EL TRANSIBERIANO", "LA PRESA"),
    )

    private val ETAPAS = mapOf(
        "romano" to listOf("Roma", "Ostia", "Massilia", "Lugdunum", "Durocortorum", "Lutetia", "Alesia"),
        "heleno" to listOf("Pella", "Anfípolis", "Troya", "Sardes", "Gordio", "Tarso", "Babilonia"),
        "medieval" to listOf("Roncesvalles", "Pamplona", "Logroño", "Burgos", "León", "Astorga", "Santiago"),
        "distopia" to listOf("SECTOR 1", "SECTOR 2", "SECTOR 3", "SECTOR 4", "SECTOR 5", "SECTOR 6", "MINISTERIO"),
        "refugio" to listOf("Esclusa", "Túnel", "Ruinas", "Gasolinera", "Puente", "Torre", "Superficie"),
        "ciber" to listOf("NODO 01", "NODO 02", "NODO 03", "NODO 04", "NODO 05", "NODO 06", "NÚCLEO"),
        "orden" to listOf("Ciudadela", "Páramo", "Puesto", "Ruinas", "Búnker", "Archivo", "Reliquia"),
        "laboratorio" to listOf("Día 1", "Día 2", "Día 3", "Día 4", "Día 5", "Día 6", "Resultado"),
        "colonos" to listOf("Plaza", "Molino", "Granja", "Bosque", "Río", "Colina", "Granero"),
        "napoleon" to listOf("París", "Lyon", "Turín", "Milán", "Mantua", "Rívoli", "Viena"),
        "botanico" to listOf("Plymouth", "Cabo Verde", "Bahía", "Montevideo", "Tierra del Fuego", "Valparaíso", "Galápagos"),
        "navidad" to listOf("Taller", "Bosque", "Pueblo", "Montaña", "Lago helado", "Chimeneas", "Casa"),
        "seda" to listOf("Chang'an", "Dunhuang", "Turfán", "Kashgar", "Samarcanda", "Bujará", "Merv"),
        "gotico" to listOf("Cimientos", "Muros", "Columnas", "Arbotantes", "Bóveda", "Vitrales", "Campanas"),
        "espacio" to listOf("Plataforma", "Órbita baja", "Luna", "Lagrange", "Fobos", "Órbita marciana", "Marte"),
        "andalus" to listOf("Córdoba", "Écija", "Antequera", "Loja", "Santa Fe", "Albaicín", "Alhambra"),
        "mongol" to listOf("Karakórum", "Orjón", "Altái", "Dzungaria", "Almalik", "Otrar", "Samarcanda"),
        "renacimiento" to listOf("Boceto", "Estudio", "Cartón", "Imprimación", "Color", "Veladura", "Firma"),
        "urss" to listOf("FÁBRICA", "KOLJÓS", "MINA", "PRESA", "VÍA", "PUERTO", "META"),
    )
}
