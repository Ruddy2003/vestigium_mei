package io.github.ruddy2003.vestigium.ui

/**
 * Textos cortos que antes eran iguales en todos los temas, ahora en la voz de cada uno: los nombres de las pestañas, la
 * escala sin marcar, el «+ añadir» de las listas, el contador de palabras del escritor y las preguntas de los widgets (y
 * las del modo discreto, que no dicen qué se registra). Se leen con [Estilo.texto]; la prueba de cobertura exige todas
 * las claves de [CLAVES] en todos los temas.
 */
object Textos {
    /** Todas las claves; `palabras` lleva un `%d` para el número. */
    val CLAVES = listOf("pestana_dia", "pestana_diario", "pestana_registrar", "pestana_vida", "pestana_entender", "escala_vacia", "lista_anadir", "palabras", "widget_animo", "widget_momento", "widget_discreto_animo", "widget_discreto_momento")

    private val ROMANO: Map<String, String> = mapOf(
        "pestana_dia" to "HODIE",
        "pestana_diario" to "DIARIVM",
        "pestana_registrar" to "SCRIBE",
        "pestana_vida" to "VITA",
        "pestana_entender" to "RATIO",
        "escala_vacia" to "nihil · toca o desliza",
        "lista_anadir" to "+ adde",
        "palabras" to "%d verba",
        "widget_animo" to "quid sentis nunc?",
        "widget_momento" to "nunc sentio…",
        "widget_discreto_animo" to "nunc",
        "widget_discreto_momento" to "scribere",
    )

    private val CIBER: Map<String, String> = mapOf(
        "pestana_dia" to "hoy",
        "pestana_diario" to "diario",
        "pestana_registrar" to "+reg",
        "pestana_vida" to "vida",
        "pestana_entender" to "stats",
        "escala_vacia" to "null · toca o desliza",
        "lista_anadir" to "+ push",
        "palabras" to "wc: %d palabras",
        "widget_animo" to "> ánimo --ahora ?",
        "widget_momento" to "> siento ...",
        "widget_discreto_animo" to "> ahora",
        "widget_discreto_momento" to "> log",
    )

    private val HELENO: Map<String, String> = mapOf(
        "pestana_dia" to "ΣΗΜΕΡΟΝ",
        "pestana_diario" to "ΗΜΕΡΑΙ",
        "pestana_registrar" to "ΓΡΑΦΕ",
        "pestana_vida" to "ΒΙΟΣ",
        "pestana_entender" to "ΛΟΓΟΣ",
        "escala_vacia" to "οὐδέν · toca o desliza",
        "lista_anadir" to "+ πρόσθες",
        "palabras" to "%d λέξεις",
        "widget_animo" to "πῶς ἔχεις νῦν;",
        "widget_momento" to "νῦν αἰσθάνομαι…",
        "widget_discreto_animo" to "νῦν",
        "widget_discreto_momento" to "γράφε",
    )

    private val MEDIEVAL: Map<String, String> = mapOf(
        "pestana_dia" to "Hogaño",
        "pestana_diario" to "Libro",
        "pestana_registrar" to "Asentar",
        "pestana_vida" to "Vida",
        "pestana_entender" to "Glosas",
        "escala_vacia" to "sin nota · toca o desliza",
        "lista_anadir" to "+ añadir al folio",
        "palabras" to "%d vocablos",
        "widget_animo" to "¿cómo está el ánima agora?",
        "widget_momento" to "agora siento…",
        "widget_discreto_animo" to "agora",
        "widget_discreto_momento" to "asentar",
    )

    private val DISTOPIA: Map<String, String> = mapOf(
        "pestana_dia" to "PARTE",
        "pestana_diario" to "DECLARAR",
        "pestana_registrar" to "ANOTAR",
        "pestana_vida" to "EXPEDIENTE",
        "pestana_entender" to "INFORMES",
        "escala_vacia" to "SIN DECLARAR · TOQUE O DESLICE",
        "lista_anadir" to "+ ANEXAR",
        "palabras" to "%d PALABRAS DECLARADAS",
        "widget_animo" to "DECLARE SU ESTADO ACTUAL",
        "widget_momento" to "EL CIUDADANO SIENTE…",
        "widget_discreto_animo" to "ESTADO",
        "widget_discreto_momento" to "DECLARAR",
    )

    private val REFUGIO: Map<String, String> = mapOf(
        "pestana_dia" to "BITÁCORA",
        "pestana_diario" to "CUADERNO",
        "pestana_registrar" to "ANOTAR",
        "pestana_vida" to "REFUGIO",
        "pestana_entender" to "ANÁLISIS",
        "escala_vacia" to "SIN LECTURA · TOQUE O DESLICE",
        "lista_anadir" to "+ AGREGAR",
        "palabras" to "%d PALABRAS EN CINTA",
        "widget_animo" to "ESTADO DEL RESIDENTE: ¿AGRADABLE?",
        "widget_momento" to "EL RESIDENTE SIENTE…",
        "widget_discreto_animo" to "LECTURA",
        "widget_discreto_momento" to "ANOTAR",
    )

    private val ORDEN: Map<String, String> = mapOf(
        "pestana_dia" to "PARTE",
        "pestana_diario" to "CRÓNICA",
        "pestana_registrar" to "REPORTAR",
        "pestana_vida" to "SERVICIO",
        "pestana_entender" to "TÁCTICA",
        "escala_vacia" to "SIN PARTE · TOCA O DESLIZA",
        "lista_anadir" to "+ ALISTAR",
        "palabras" to "%d PALABRAS EN EL PARTE",
        "widget_animo" to "¿MORAL DE LA TROPA AHORA?",
        "widget_momento" to "LA TROPA SIENTE…",
        "widget_discreto_animo" to "MORAL",
        "widget_discreto_momento" to "REPORTAR",
    )

    private val LABORATORIO: Map<String, String> = mapOf(
        "pestana_dia" to "SESIÓN",
        "pestana_diario" to "CUADERNO",
        "pestana_registrar" to "MEDIR",
        "pestana_vida" to "SUJETO",
        "pestana_entender" to "RESULTADOS",
        "escala_vacia" to "sin medición · toca o desliza",
        "lista_anadir" to "+ variable",
        "palabras" to "n = %d palabras",
        "widget_animo" to "¿valencia afectiva ahora?",
        "widget_momento" to "estado observado…",
        "widget_discreto_animo" to "medición",
        "widget_discreto_momento" to "registrar",
    )

    private val COLONOS: Map<String, String> = mapOf(
        "pestana_dia" to "Jornada",
        "pestana_diario" to "Cuaderno",
        "pestana_registrar" to "Apuntar",
        "pestana_vida" to "Pueblo",
        "pestana_entender" to "Almanaque",
        "escala_vacia" to "sin apuntar · toca o desliza",
        "lista_anadir" to "+ apuntar otro",
        "palabras" to "%d palabras en el cuaderno",
        "widget_animo" to "¿cómo amaneció el ánimo?",
        "widget_momento" to "ahorita siento…",
        "widget_discreto_animo" to "ahorita",
        "widget_discreto_momento" to "apuntar",
    )

    private val NAPOLEON: Map<String, String> = mapOf(
        "pestana_dia" to "BULLETIN",
        "pestana_diario" to "MÉMOIRES",
        "pestana_registrar" to "DÉPÊCHE",
        "pestana_vida" to "ÉPOPÉE",
        "pestana_entender" to "ÉTAT-MAJOR",
        "escala_vacia" to "SANS RAPPORT · toca o desliza",
        "lista_anadir" to "+ AJOUTER",
        "palabras" to "%d MOTS",
        "widget_animo" to "¿MORAL DE LA GARDE?",
        "widget_momento" to "LE CŒUR DIT…",
        "widget_discreto_animo" to "MORAL",
        "widget_discreto_momento" to "DÉPÊCHE",
    )

    private val BOTANICO: Map<String, String> = mapOf(
        "pestana_dia" to "Lámina",
        "pestana_diario" to "Herbario",
        "pestana_registrar" to "Observar",
        "pestana_vida" to "Ciclo",
        "pestana_entender" to "Taxonomía",
        "escala_vacia" to "sin ejemplar · toca o desliza",
        "lista_anadir" to "+ ejemplar",
        "palabras" to "%d palabras al margen",
        "widget_animo" to "¿cómo florece el ánimo?",
        "widget_momento" to "hoy brota…",
        "widget_discreto_animo" to "brote",
        "widget_discreto_momento" to "anotar",
    )

    private val RUINAS: Map<String, String> = mapOf(
        "pestana_dia" to "Hoy",
        "pestana_diario" to "Cuaderno",
        "pestana_registrar" to "Apuntar",
        "pestana_vida" to "Rastro",
        "pestana_entender" to "Grietas",
        "escala_vacia" to "nada apuntado · toca o desliza",
        "lista_anadir" to "+ a la mochila",
        "palabras" to "%d palabras a lápiz",
        "widget_animo" to "¿cuánto aguante queda hoy?",
        "widget_momento" to "entre la hiedra encontré…",
        "widget_discreto_animo" to "aguante",
        "widget_discreto_momento" to "apuntar",
    )

    private val NAVIDAD: Map<String, String> = mapOf(
        "pestana_dia" to "El día",
        "pestana_diario" to "Cartas",
        "pestana_registrar" to "Contar",
        "pestana_vida" to "Mi año",
        "pestana_entender" to "Balance",
        "escala_vacia" to "aún sin escribir · toca o desliza",
        "lista_anadir" to "+ añadir al árbol",
        "palabras" to "%d palabras en la carta",
        "widget_animo" to "¿cómo está el corazón hoy?",
        "widget_momento" to "en este momento siento…",
        "widget_discreto_animo" to "junto al fuego",
        "widget_discreto_momento" to "contar",
    )

    private val SEDA: Map<String, String> = mapOf(
        "pestana_dia" to "Posta",
        "pestana_diario" to "Rollos",
        "pestana_registrar" to "Relatar",
        "pestana_vida" to "Ruta",
        "pestana_entender" to "Astrolabio",
        "escala_vacia" to "sin sello · toca o desliza",
        "lista_anadir" to "+ añadir al fardo",
        "palabras" to "%d palabras en el rollo",
        "widget_animo" to "¿cómo va el espíritu del viaje?",
        "widget_momento" to "el viajero siente…",
        "widget_discreto_animo" to "espíritu",
        "widget_discreto_momento" to "relatar",
    )

    private val URSS: Map<String, String> = mapOf(
        "pestana_dia" to "PLAN",
        "pestana_diario" to "INFORMES",
        "pestana_registrar" to "INFORMAR",
        "pestana_vida" to "BRIGADA",
        "pestana_entender" to "ESTADÍSTICA",
        "escala_vacia" to "SIN NORMA · TOCA O DESLIZA",
        "lista_anadir" to "+ AL PLAN",
        "palabras" to "%d PALABRAS AL PLAN",
        "widget_animo" to "¿MORAL DEL CAMARADA HOY?",
        "widget_momento" to "EL CAMARADA SIENTE…",
        "widget_discreto_animo" to "MORAL",
        "widget_discreto_momento" to "INFORMAR",
    )

    private val GOTICO: Map<String, String> = mapOf(
        "pestana_dia" to "Laudes",
        "pestana_diario" to "Breviario",
        "pestana_registrar" to "Inscribir",
        "pestana_vida" to "Vita",
        "pestana_entender" to "Exégesis",
        "escala_vacia" to "en blanco · toca o desliza",
        "lista_anadir" to "+ añadir al códice",
        "palabras" to "%d palabras en el folio",
        "widget_animo" to "¿en paz el alma ahora?",
        "widget_momento" to "el alma siente…",
        "widget_discreto_animo" to "alma",
        "widget_discreto_momento" to "inscribir",
    )

    private val ESPACIO: Map<String, String> = mapOf(
        "pestana_dia" to "ÓRBITA",
        "pestana_diario" to "BITÁCORA",
        "pestana_registrar" to "REGISTRO",
        "pestana_vida" to "MISIÓN",
        "pestana_entender" to "TELEMETRÍA",
        "escala_vacia" to "SIN SEÑAL · TOCA O DESLIZA",
        "lista_anadir" to "+ AÑADIR CARGA",
        "palabras" to "%d PALABRAS TRANSMITIDAS",
        "widget_animo" to "ESTADO DE LA TRIPULACIÓN: ¿NOMINAL?",
        "widget_momento" to "LA TRIPULACIÓN REPORTA…",
        "widget_discreto_animo" to "ESTADO",
        "widget_discreto_momento" to "TRANSMITIR",
    )

    private val ANDALUS: Map<String, String> = mapOf(
        "pestana_dia" to "اليوم",
        "pestana_diario" to "اليوميات",
        "pestana_registrar" to "دوّن",
        "pestana_vida" to "الحياة",
        "pestana_entender" to "الحكمة",
        "escala_vacia" to "فارغ · toca o desliza",
        "lista_anadir" to "+ أضف",
        "palabras" to "%d كلمة",
        "widget_animo" to "كيف حالك الآن؟",
        "widget_momento" to "أشعر الآن…",
        "widget_discreto_animo" to "الآن",
        "widget_discreto_momento" to "دوّن",
    )

    private val MONGOL: Map<String, String> = mapOf(
        "pestana_dia" to "ӨНӨӨДӨР",
        "pestana_diario" to "ДЭВТЭР",
        "pestana_registrar" to "БИЧИХ",
        "pestana_vida" to "АМЬДРАЛ",
        "pestana_entender" to "ШИНЖИЛГЭЭ",
        "escala_vacia" to "ХООСОН · toca o desliza",
        "lista_anadir" to "+ НЭМЭХ",
        "palabras" to "%d ҮГ",
        "widget_animo" to "СЭТГЭЛ ЯМАР БАЙНА?",
        "widget_momento" to "ОДОО МЭДРЭХ НЬ…",
        "widget_discreto_animo" to "ОДОО",
        "widget_discreto_momento" to "БИЧИХ",
    )

    private val RENACIMIENTO: Map<String, String> = mapOf(
        "pestana_dia" to "Oggi",
        "pestana_diario" to "Taccuino",
        "pestana_registrar" to "Annota",
        "pestana_vida" to "Vita",
        "pestana_entender" to "Studio",
        "escala_vacia" to "senza segno · toca o desliza",
        "lista_anadir" to "+ aggiungi",
        "palabras" to "%d parole",
        "widget_animo" to "come sta l'animo adesso?",
        "widget_momento" to "ora sento…",
        "widget_discreto_animo" to "ora",
        "widget_discreto_momento" to "annota",
    )

    private val REVOLUCION: Map<String, String> = mapOf(
        "pestana_dia" to "CAHIER",
        "pestana_diario" to "CUADERNOS",
        "pestana_registrar" to "ACTA",
        "pestana_vida" to "REPÚBLICA",
        "pestana_entender" to "LUCES",
        "escala_vacia" to "SIN VOTO · TOCA O DESLIZA",
        "lista_anadir" to "+ ADHERIR",
        "palabras" to "%d PALABRAS AL ACTA",
        "widget_animo" to "¿ESPÍRITU CÍVICO DEL DÍA?",
        "widget_momento" to "HOY SIENTO, CIUDADANO…",
        "widget_discreto_animo" to "ESPÍRITU",
        "widget_discreto_momento" to "ASENTAR",
    )

    private val ANTIGUAS: Map<String, String> = mapOf(
        "pestana_dia" to "JORNADA",
        "pestana_diario" to "CRÓNICAS",
        "pestana_registrar" to "ANOTAR",
        "pestana_vida" to "CAMINO",
        "pestana_entender" to "SABER",
        "escala_vacia" to "SIN MARCA · TOCA O DESLIZA",
        "lista_anadir" to "+ A LA ALFORJA",
        "palabras" to "%d PALABRAS EN LA CRÓNICA",
        "widget_animo" to "¿CÓMO ANDA EL TEMPLE?",
        "widget_momento" to "EL CAMINANTE SIENTE…",
        "widget_discreto_animo" to "TEMPLE",
        "widget_discreto_momento" to "CONTAR",
    )

    fun de(tema: TemaId, clave: String): String? = when (tema) {
        TemaId.ROMANO -> ROMANO
        TemaId.CIBER -> CIBER
        TemaId.NEON -> NEON
        TemaId.HELENO -> HELENO
        TemaId.MEDIEVAL -> MEDIEVAL
        TemaId.DISTOPIA -> DISTOPIA
        TemaId.REFUGIO -> REFUGIO
        TemaId.ORDEN -> ORDEN
        TemaId.LABORATORIO -> LABORATORIO
        TemaId.COLONOS -> COLONOS
        TemaId.NAPOLEON -> NAPOLEON
        TemaId.BOTANICO -> BOTANICO
        TemaId.RUINAS -> RUINAS
        TemaId.NAVIDAD -> NAVIDAD
        TemaId.SEDA -> SEDA
        TemaId.URSS -> URSS
        TemaId.GOTICO -> GOTICO
        TemaId.ESPACIO -> ESPACIO
        TemaId.ANDALUS -> ANDALUS
        TemaId.MONGOL -> MONGOL
        TemaId.RENACIMIENTO -> RENACIMIENTO
        TemaId.REVOLUCION -> REVOLUCION
        TemaId.ANTIGUAS -> ANTIGUAS
    }[clave]
}
