package io.github.ruddy2003.vestigium.ui

/**
 * Las claves de las líneas «clave = valor» (Día, widget «hoy», reportes) en la jerga de cada tema, siempre
 * legibles: latín en el romano y el gótico, castellano antiguo en el scriptorium y español en los demás. Las claves
 * originales están en latín (`somnus`, `passus`…); [Estilo.clave] decide qué diccionario usa cada tema. Son cortas: van en
 * una columna de ancho fijo.
 */
internal object Claves {
    /** Castellano de los siglos XIII a XV, como escribía el copista. */
    val CASTELLANO_ANTIGUO: Map<String, String> = mapOf(
        "horologium" to "relox", "somnus" to "suenno", "stadia" to "tiempos", "cor" to "coraçon", "passus" to "passos",
        "motus" to "mouimiento", "iter" to "camino", "exercitatio" to "exerçiçio", "loca" to "logares", "telephonum" to "tablilla",
        "telephon" to "tablilla", "usus" to "usança", "monita" to "pregones", "musica" to "cantigas", "imagines" to "pinturas",
        "fasti" to "almanaque", "caelum" to "çielo", "lux" to "claridad", "aer" to "ayre", "corpus" to "carne",
        "nutritio" to "mantenimiento", "cibus" to "yantares", "robur" to "fuerça", "musculi" to "nervios", "voces" to "vozes",
        "soni" to "sones", "retia" to "redes", "lumen" to "lumbre", "vis" to "vigor", "lectio" to "leyenda", "strepitus" to "roído",
        "luna" to "luna", "dias" to "días", "animus" to "ánima", "optimus" to "mejor", "momenta" to "sucesos", "svmptvs" to "despensa",
        "memorabilia" to "memorias", "ascensvs" to "subida", "dies" to "jornadas", "quaestiones" to "questiones",
        "celeritas" to "presteza", "bibliotheca" to "librería", "synchronia" to "concordia", "opera" to "obras", "iudicium" to "juyzio",
        "citatio" to "dicho", "sententia" to "sentençia", "orbis" to "orbe", "nova" to "nueuo", "mirabilia" to "marauillas",
        "somnium" to "ensuenno", "epistulae" to "epístolas", "aperta" to "abierta", "initium" to "comienço", "finis" to "cabo",
        "pessimus" to "peor", "personae" to "gentes", "themata" to "materias", "praeclarum" to "la_mejor", "dies felix" to "buen_día",
        "consuetudo" to "costumbres", "series" to "continuo", "palmae" to "fechos", "reditus" to "rentas", "apps" to "ingenios",
    )

    /** Latín litúrgico del gótico (el resto queda en el latín del romano). */
    val GOTICO: Map<String, String> = mapOf(
        "somnus" to "dormitio", "animus" to "anima", "personae" to "fratres", "palmae" to "merita", "epistulae" to "litterae",
        "momenta" to "horae", "svmptvs" to "expensae", "loca" to "loca_sancta", "musica" to "cantus", "consuetudo" to "regula",
        "iter" to "peregrinatio", "opera" to "codices", "mirabilia" to "miracula", "themata" to "materiae",
    )

    /** Jerga del ministerio. Lo que no está aquí queda en el español común. */
    val DISTOPIA: Map<String, String> = mapOf(
        "somnus" to "descanso", "animus" to "estado", "svmptvs" to "ración", "cor" to "pulso", "loca" to "zonas",
        "personae" to "ciudadanos", "consuetudo" to "cuotas", "palmae" to "méritos", "telephonum" to "monitor", "telephon" to "monitor",
        "iudicium" to "dictamen", "epistulae" to "misivas", "reditus" to "asignación", "momenta" to "incidencias",
        "imagines" to "evidencia", "iter" to "trayecto", "orbis" to "territorio", "themata" to "asuntos", "memorabilia" to "hechos",
    )

    /** Jerga del refugio. Lo que no está aquí queda en el español común. */
    val REFUGIO: Map<String, String> = mapOf(
        "somnus" to "ciclo_sueño", "animus" to "estado", "svmptvs" to "suministros", "aer" to "calidad_aire", "caelum" to "exterior",
        "lux" to "luz_exterior", "loca" to "sectores", "personae" to "residentes", "palmae" to "insignias", "corpus" to "biometría",
        "consuetudo" to "protocolos", "reditus" to "créditos", "strepitus" to "ruido_sector", "momenta" to "sucesos",
        "orbis" to "superficie", "vis" to "energía", "retia" to "red_interna", "epistulae" to "mensajes",
    )

    /** Jerga de la orden. Lo que no está aquí queda en el español común. */
    val ORDEN: Map<String, String> = mapOf(
        "somnus" to "reposo", "animus" to "moral", "svmptvs" to "intendencia", "passus" to "marcha", "iter" to "avance",
        "robur" to "entreno", "loca" to "posiciones", "personae" to "hermanos", "palmae" to "medallas", "consuetudo" to "disciplina",
        "epistulae" to "despachos", "reditus" to "soldada", "momenta" to "bitácora", "orbis" to "frentes", "telephonum" to "terminal",
        "telephon" to "terminal", "themata" to "materias", "opera" to "archivo",
    )

    /** Jerga del laboratorio. Lo que no está aquí queda en el español común. */
    val LABORATORIO: Map<String, String> = mapOf(
        "animus" to "afecto", "cor" to "fc", "svmptvs" to "recursos", "personae" to "sujetos", "loca" to "coordenadas",
        "consuetudo" to "variables", "iudicium" to "valoración", "palmae" to "resultados", "somnium" to "ensoñación",
        "momenta" to "observaciones", "imagines" to "muestras", "dies" to "sesiones", "quaestiones" to "instrumentos",
        "celeritas" to "latencia_ms", "lux" to "lux_diurna",
    )

    /** Habla del pueblo. Lo que no está aquí queda en el español común. */
    val COLONOS: Map<String, String> = mapOf(
        "somnus" to "dormida", "animus" to "humor", "svmptvs" to "cuentas", "personae" to "vecinos", "loca" to "parajes",
        "caelum" to "el_tiempo", "robur" to "fuerza", "palmae" to "medallas", "reditus" to "ganancias", "iter" to "camino",
        "momenta" to "sucedidos", "imagines" to "estampas", "telephonum" to "aparato", "telephon" to "aparato", "epistulae" to "correo",
        "orbis" to "lejanías",
    )

    /** Jerga del naturalista. Lo que no está aquí queda en el español común. */
    val BOTANICO: Map<String, String> = mapOf(
        "somnus" to "latencia", "animus" to "temple", "passus" to "andadura", "loca" to "localidades", "personae" to "compañía",
        "imagines" to "láminas", "lectio" to "lecturas", "caelum" to "meteoro", "lux" to "fotoperiodo", "aer" to "atmósfera",
        "consuetudo" to "cuidados", "palmae" to "hallazgos", "svmptvs" to "semillas", "strepitus" to "rumor", "cibus" to "sustento",
        "iter" to "itinerario", "orbis" to "territorio", "momenta" to "observaciones", "dies" to "jornadas",
    )

    /** Voz de la casa en Navidad. Lo que no está aquí queda en el español común. */
    val NAVIDAD: Map<String, String> = mapOf(
        "somnus" to "noche_de_paz", "animus" to "sentir", "svmptvs" to "regalos", "personae" to "familia", "palmae" to "estrellas",
        "epistulae" to "cartitas", "consuetudo" to "propósitos", "caelum" to "afuera", "lux" to "sol", "loca" to "rincones",
        "cibus" to "cena", "momenta" to "momentitos", "imagines" to "postales", "musica" to "villancicos", "orbis" to "lo_lejano",
    )

    /** Jerga de la sala de control. Lo que no está aquí queda en el español común. */
    val ESPACIO: Map<String, String> = mapOf(
        "animus" to "estado", "cor" to "ritmo", "iter" to "trayectoria", "loca" to "posición", "lux" to "iluminación", "caelum" to "meteo",
        "aer" to "atmósfera", "telephonum" to "consola", "telephon" to "consola", "svmptvs" to "combustible", "personae" to "tripulación",
        "palmae" to "insignias", "epistulae" to "transmisiones", "consuetudo" to "rutinas", "momenta" to "eventos",
        "dies" to "soles_de_misión", "orbis" to "planeta", "vis" to "energía",
    )

    /** Habla del camino. Lo que no está aquí queda en el español común. */
    val ANTIGUAS: Map<String, String> = mapOf(
        "somnus" to "lecho", "animus" to "temple", "svmptvs" to "monedas", "passus" to "andanza", "iter" to "camino", "loca" to "tierras",
        "personae" to "compañía", "palmae" to "hazañas", "consuetudo" to "juramentos", "epistulae" to "pergaminos", "caelum" to "cielo",
        "telephonum" to "espejo", "telephon" to "espejo", "imagines" to "grabados", "musica" to "cantares", "reditus" to "tributos",
        "momenta" to "sucesos", "orbis" to "el_ancho_mundo",
    )

    /** Voz clásica (Morfeo, dracmas, laureles). Lo que no está aquí queda en el español común. */
    val HELENO: Map<String, String> = mapOf(
        "somnus" to "morfeo", "animus" to "ánimo", "svmptvs" to "dracmas", "personae" to "amigos", "palmae" to "laureles",
        "loca" to "lugares", "momenta" to "instantes", "caelum" to "cielo", "orbis" to "ecúmene", "consuetudo" to "costumbres",
        "epistulae" to "epístolas", "reditus" to "óbolos",
    )

    /** Habla de la caravana. Lo que no está aquí queda en el español común. */
    val SEDA: Map<String, String> = mapOf(
        "somnus" to "acampada", "svmptvs" to "mercancías", "loca" to "postas", "personae" to "caravana", "palmae" to "tesoros",
        "iter" to "ruta", "momenta" to "relatos", "orbis" to "rutas", "consuetudo" to "ritos", "caelum" to "cielo",
        "reditus" to "ganancias",
    )

    /** Jerga del plan. Lo que no está aquí queda en el español común. */
    val URSS: Map<String, String> = mapOf(
        "somnus" to "noche", "animus" to "moral", "svmptvs" to "gasto", "personae" to "camaradas", "palmae" to "honores",
        "loca" to "lugares", "momenta" to "informes", "consuetudo" to "normas", "reditus" to "salario", "epistulae" to "correo",
        "orbis" to "mundo", "robur" to "cultura_física",
    )

    /** Palabras que el español tomó del árabe (almohada, maravedís, aldea, alhaja…). Lo que no está aquí queda en el español común. */
    val ANDALUS: Map<String, String> = mapOf(
        "somnus" to "almohada", "svmptvs" to "maravedís", "loca" to "aldeas", "personae" to "allegados", "palmae" to "alhajas",
        "momenta" to "instantes", "consuetudo" to "costumbres", "caelum" to "cielo", "iter" to "camino", "reditus" to "rentas",
        "musica" to "laúd",
    )

    /** Habla de la estepa. Lo que no está aquí queda en el español común. */
    val MONGOL: Map<String, String> = mapOf(
        "somnus" to "yurta", "passus" to "marcha", "svmptvs" to "gastos", "loca" to "campamentos", "personae" to "clan",
        "palmae" to "hazañas", "momenta" to "sucesos", "iter" to "galope", "orbis" to "estepa", "consuetudo" to "costumbres",
        "caelum" to "cielo_azul",
    )

    /** Jerga del cuartel general. Lo que no está aquí queda en el español común. */
    val NAPOLEON: Map<String, String> = mapOf(
        "somnus" to "vivac", "palmae" to "victorias", "svmptvs" to "tesorería", "personae" to "estado_mayor", "momenta" to "boletines",
        "iter" to "marcha", "epistulae" to "despachos", "robur" to "manejo_armas", "consuetudo" to "código", "reditus" to "paga",
        "orbis" to "imperio", "animus" to "moral",
    )

    /** Jerga de la Convención. Lo que no está aquí queda en el español común. */
    val REVOLUCION: Map<String, String> = mapOf(
        "somnus" to "noche_cívica", "animus" to "espíritu", "personae" to "ciudadanos", "palmae" to "honores", "consuetudo" to "virtudes",
        "svmptvs" to "asignados", "momenta" to "jornadas", "epistulae" to "proclamas", "reditus" to "contribución",
        "themata" to "mociones", "iudicium" to "opinión", "orbis" to "naciones",
    )
}
