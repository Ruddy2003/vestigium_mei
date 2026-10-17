package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import java.text.Normalizer

/**
 * Lo que cambia con el tema además de los colores: cómo se nombran las pantallas y secciones,
 * cómo se escriben números y fechas, y los pequeños rótulos. Lee el tema de la configuración,
 * así que cualquier pantalla se redibuja sola al cambiarlo.
 */
object Estilo {
    val tema: TemaId get() = TemaId.entries.firstOrNull { it.id == Repo.config.tema } ?: TemaId.ROMANO

    private fun slug(s: String) = Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
        .replace(Regex("[^a-z0-9]+"), "_").trim('_')

    /** Título grande y comentario de una pantalla. */
    fun pantalla(titulo: String, latin: String?): Pair<String, String?> = when (tema) {
        TemaId.ROMANO -> (latin ?: titulo.uppercase()) to (if (latin != null) "// ${titulo.lowercase()}" else null)
        TemaId.CIBER -> titulo.uppercase() to "// sys.${slug(latin ?: titulo)}"
        TemaId.HELENO -> Griego.nombre(titulo)?.let { it to "— ${titulo.lowercase()}" } ?: (titulo.uppercase() to null)
    }

    /** Título y comentario de una sección. */
    fun seccion(titulo: String, latin: String?): Pair<String, String?> = when (tema) {
        TemaId.ROMANO -> (latin ?: titulo.uppercase()) to (if (latin != null) "// ${titulo.lowercase()}" else null)
        TemaId.CIBER -> "> ${slug(titulo)}" to null
        TemaId.HELENO -> Griego.nombre(titulo)?.let { it to "— ${titulo.lowercase()}" } ?: (titulo.uppercase() to null)
    }

    fun numeral(n: Int): String = when (tema) {
        TemaId.ROMANO -> Romano.de(n)
        TemaId.CIBER -> if (n < 100) "[%02d]".format(n) else "0x%X".format(n)
        TemaId.HELENO -> Griego.numeral(n)
    }

    /** Fecha solemne del encabezado. */
    fun fecha(iso: String): String = when (tema) {
        TemaId.ROMANO -> Romano.fecha(iso)
        TemaId.CIBER -> runCatching { val d = Fechas.parse(iso); "%04d.%02d.%02d // %s".format(d.year, d.monthValue, d.dayOfMonth, SEMANA_ES[d.dayOfWeek.value - 1]) }.getOrDefault(iso)
        TemaId.HELENO -> Griego.fecha(iso)
    }

    fun mes(n: Int, anio: Int? = null): String = when (tema) {
        TemaId.ROMANO -> Romano.mes(n)
        TemaId.CIBER -> MESES_ES[(n - 1).coerceIn(0, 11)] + (anio?.let { ".$it" } ?: "")
        TemaId.HELENO -> Griego.MESES[(n - 1).coerceIn(0, 11)]
    }

    val semana: List<String> get() = when (tema) { TemaId.ROMANO -> Romano.SEMANA; TemaId.CIBER -> SEMANA_ES; TemaId.HELENO -> Griego.SEMANA }

    /** Marcas del calendario romano (calendas, nonas, idus); en los otros temas no hay. */
    fun marca(fecha: String): String? = if (tema == TemaId.ROMANO) Romano.marca(fecha) else null

    val atras: String get() = when (tema) { TemaId.ROMANO -> "← cd .."; TemaId.CIBER -> "<< cd .."; TemaId.HELENO -> "← ὀπίσω" }
    val prefijo: String get() = when (tema) { TemaId.ROMANO -> "› "; TemaId.CIBER -> "$ "; TemaId.HELENO -> "· " }

    /** Rótulos de los widgets. */
    fun widget(clave: String): String = when (tema) {
        TemaId.ROMANO -> mapOf("animo" to "ANIMVS", "momento" to "MOMENTA", "gasto" to "SVMPTVS", "sueno" to "SOMNVS", "habitos" to "CONSVETVDINES", "hoy" to "DIES", "cifra" to "NVMERVS", "foto" to "IMAGO")[clave] ?: clave.uppercase()
        TemaId.CIBER -> "> " + (mapOf("sueno" to "sueño", "habitos" to "hábitos", "animo" to "ánimo")[clave] ?: clave)
        TemaId.HELENO -> mapOf("animo" to "ΘΥΜΟΣ", "momento" to "ΚΑΙΡΟΙ", "gasto" to "ΔΑΠΑΝΑΙ", "sueno" to "ΥΠΝΟΣ", "habitos" to "ΕΘΗ", "hoy" to "ΗΜΕΡΑ", "cifra" to "ΑΡΙΘΜΟΣ", "foto" to "ΕΙΚΩΝ")[clave] ?: clave.uppercase()
    }

    /** Claves de las líneas de datos del widget «hoy» y del tablero. */
    fun clave(c: String): String = when (tema) {
        TemaId.ROMANO -> c
        TemaId.CIBER -> mapOf("animus" to "mood", "somnus" to "sleep", "passus" to "steps", "momenta" to "events", "telephon" to "screen", "caelum" to "sky")[c] ?: c
        TemaId.HELENO -> mapOf("animus" to "θυμός", "somnus" to "ὕπνος", "passus" to "βήματα", "momenta" to "καιροί", "telephon" to "ὀθόνη", "caelum" to "οὐρανός")[c] ?: c
    }

    private val SEMANA_ES = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    private val MESES_ES = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")
}

/** Griego antiguo: nombres de secciones, numerales jónicos y el calendario macedonio de Alejandro. */
object Griego {
    /** Numerales jónicos (alfabéticos). El 6 se escribe ΣΤ, como en la tradición griega posterior. */
    fun numeral(n: Int): String {
        if (n <= 0 || n >= 10000) return n.toString()
        val u = listOf("", "Α", "Β", "Γ", "Δ", "Ε", "ΣΤ", "Ζ", "Η", "Θ")
        val d = listOf("", "Ι", "Κ", "Λ", "Μ", "Ν", "Ξ", "Ο", "Π", "Ϟ")
        val c = listOf("", "Ρ", "Σ", "Τ", "Υ", "Φ", "Χ", "Ψ", "Ω", "Ϡ")
        val m = n / 1000; val r = n % 1000
        return (if (m > 0) "͵" + u[m] else "") + c[r / 100] + d[(r / 10) % 10] + u[r % 10] + "ʹ"
    }

    /**
     * Meses del calendario macedonio, el que usaba la corte de Alejandro, en la correspondencia
     * que fijaron después las ciudades helenísticas (Dios ≈ noviembre … Hiperbereteo ≈ octubre).
     */
    val MESES = listOf("ΑΥΔΝΑΙΟΣ", "ΠΕΡΙΤΙΟΣ", "ΔΥΣΤΡΟΣ", "ΞΑΝΔΙΚΟΣ", "ΑΡΤΕΜΙΣΙΟΣ", "ΔΑΙΣΙΟΣ", "ΠΑΝΗΜΟΣ", "ΛΩΟΣ", "ΓΟΡΠΙΑΙΟΣ", "ΥΠΕΡΒΕΡΕΤΑΙΟΣ", "ΔΙΟΣ", "ΑΠΕΛΛΑΙΟΣ")
    private val MESES_GEN = listOf("ΑΥΔΝΑΙΟΥ", "ΠΕΡΙΤΙΟΥ", "ΔΥΣΤΡΟΥ", "ΞΑΝΔΙΚΟΥ", "ΑΡΤΕΜΙΣΙΟΥ", "ΔΑΙΣΙΟΥ", "ΠΑΝΗΜΟΥ", "ΛΩΟΥ", "ΓΟΡΠΙΑΙΟΥ", "ΥΠΕΡΒΕΡΕΤΑΙΟΥ", "ΔΙΟΥ", "ΑΠΕΛΛΑΙΟΥ")
    /** Días con sus dioses, como los romanos: Selene, Ares, Hermes, Zeus, Afrodita, Cronos, Helios. */
    val SEMANA = listOf("ΣΕΛ", "ΑΡΕ", "ΕΡΜ", "ΔΙΟ", "ΑΦΡ", "ΚΡΟ", "ΗΛΙ")

    fun fecha(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        "${numeral(d.dayOfMonth)} ${MESES_GEN[d.monthValue - 1]} · ${numeral(d.year)}"
    }.getOrDefault(iso)

    private val NOMBRES = mapOf(
        "Día" to "ΗΜΕΡΑ", "Momentos" to "ΚΑΙΡΟΙ", "Gastos" to "ΔΑΠΑΝΑΙ", "Explorar" to "ΙΣΤΟΡΙΑ", "Más" to "ΤΑ ΛΟΙΠΑ",
        "Hábitos" to "ΕΘΗ", "Cartas al futuro" to "ΕΠΙΣΤΟΛΑΙ", "Experimentos personales" to "ΠΕΙΡΑΙ", "Guía científica" to "ΕΠΙΣΤΗΜΗ",
        "Cuestionarios" to "ΕΡΩΤΗΣΕΙΣ", "Prueba de reacción" to "ΤΑΧΟΣ", "Lugares" to "ΤΟΠΟΙ", "Datos" to "ΑΡΧΕΙΟΝ",
        "Permisos y sensores" to "ΑΙΣΘΗΣΕΙΣ", "Ajustes" to "ΤΑΞΙΣ", "Reportes" to "ΥΠΟΜΝΗΜΑΤΑ", "Bienvenido" to "ΧΑΙΡΕ",
        "Calendario" to "ΗΜΕΡΟΛΟΓΙΟΝ", "Escalada" to "ΑΝΑΒΑΣΙΣ", "Biblioteca" to "ΒΙΒΛΙΟΘΗΚΗ", "Tu año" to "ΕΝΙΑΥΤΟΣ",
        "Privacidad" to "ΑΠΟΡΡΗΤΑ", "Gimnasio" to "ΓΥΜΝΑΣΙΟΝ", "Análisis" to "ΑΝΑΛΥΣΙΣ", "Totales" to "ΣΥΝΟΛΑ", "Mapa" to "ΧΑΡΤΗΣ",
        "Sueño" to "ΥΠΝΟΣ", "Contexto" to "ΠΕΡΙΣΤΑΣΙΣ", "Hitos" to "ΜΝΗΜΕΙΑ", "Ánimo y cuerpo" to "ΨΥΧΗ ΚΑΙ ΣΩΜΑ",
        "Lectura del día" to "ΘΕΩΡΙΑ", "Lo mejor y lo peor" to "ΑΡΙΣΤΟΝ ΚΑΙ ΚΑΚΙΣΤΟΝ", "Medicamentos" to "ΦΑΡΜΑΚΑ",
        "Seguimiento privado" to "ΑΠΟΡΡΗΤΟΝ", "Diario" to "ΕΦΗΜΕΡΙΔΕΣ", "Predicción" to "ΜΑΝΤΕΙΑ", "Momentos de este día" to "ΚΑΙΡΟΙ ΤΗΣ ΗΜΕΡΑΣ",
        "Este día hace un año" to "ΠΕΡΥΣΙ", "Lo que registró el teléfono" to "ΙΧΝΗ", "Registrar" to "ΓΡΑΦΕΙΝ", "Ahora mismo estoy…" to "ΝΥΝ",
        "Últimos 30 días" to "ΤΡΙΑΚΟΝΤΑ ΗΜΕΡΑΙ", "Cobertura" to "ΣΥΝΕΧΕΙΑ", "Cruzar dos variables" to "ΣΥΓΚΡΙΣΙΣ",
        "Las relaciones más marcadas" to "ΣΧΕΣΕΙΣ", "¿Qué tan bien te conoces?" to "ΓΝΩΘΙ ΣΕΑΥΤΟΝ", "A qué hora aparecen tus emociones" to "ΩΡΑΙ",
        "Personas, temas e hitos" to "ΠΡΟΣΩΠΑ", "Lugares y ánimo" to "ΤΟΠΟΙ ΚΑΙ ΘΥΜΟΣ", "Medir" to "ΜΕΤΡΕΙΝ", "Entenderte" to "ΓΙΓΝΩΣΚΕΙΝ",
        "Sistema" to "ΜΗΧΑΝΗ", "Mapa emocional" to "ΧΑΡΤΗΣ", "Tus lugares" to "ΟΙ ΣΟΙ ΤΟΠΟΙ", "Resultado" to "ΑΠΟΤΕΛΕΣΜΑ",
        "Historial" to "ΧΡΟΝΙΚΑ", "Exportar" to "ΕΞΑΓΩΓΗ", "Importar" to "ΕΙΣΑΓΩΓΗ", "Respaldo automático" to "ΦΥΛΑΚΗ",
        "Google Drive" to "ΘΗΣΑΥΡΟΣ", "Resumen" to "ΚΕΦΑΛΑΙΑ", "Avisos" to "ΣΗΜΕΙΑ", "Ubicación" to "ΤΟΠΟΣ", "Cuerpo" to "ΣΩΜΑ",
        "Teléfono" to "ΤΗΛΕΦΩΝΟΝ", "Entorno" to "ΠΕΡΙΒΑΛΛΟΝ", "Clima" to "ΟΥΡΑΝΟΣ", "Movimiento" to "ΚΙΝΗΣΙΣ", "Música" to "ΜΟΥΣΙΚΗ",
        "Agenda" to "ΗΜΕΡΟΛΟΓΙΟΝ", "Comida" to "ΤΡΟΦΗ", "Dinero" to "ΧΡΗΜΑΤΑ", "Social" to "ΦΙΛΙΑ", "Cultura" to "ΠΑΙΔΕΙΑ",
        "Diario y mente" to "ΨΥΧΗ", "Índices" to "ΚΑΤΑΛΟΓΟΙ", "Herramientas" to "ΟΡΓΑΝΑ", "Spotify en vivo" to "ΜΟΥΣΙΚΗ",
        "Otros servicios" to "ΑΛΛΟΤΡΙΑ", "Etiquetas equivalentes" to "ΣΥΝΩΝΥΜΑ", "Categorías de gasto" to "ΓΕΝΗ",
        "Sesiones" to "ΑΓΩΝΕΣ", "Progreso" to "ΠΡΟΚΟΠΗ", "Bloques" to "ΛΙΘΟΙ", "Libros" to "ΒΙΒΛΙΑ", "Mes por mes" to "ΜΗΝΕΣ",
        "El año en píxeles" to "Ο ΕΝΙΑΥΤΟΣ", "Lo más frecuente" to "ΤΑ ΣΥΝΗΘΗ", "Este mes" to "ΟΔΕ Ο ΜΗΝ", "Ir a una fecha" to "ΑΛΜΑ",
        "Sincronizar" to "ΣΥΓΧΡΟΝΙΣΜΟΣ", "Notificaciones y música" to "ΗΧΟΙ", "Galería y agenda" to "ΕΙΚΟΝΕΣ", "Escuchado hoy" to "ΑΚΟΥΣΜΑΤΑ",
        "Capas" to "ΣΤΡΩΜΑΤΑ", "Rutas del día" to "ΟΔΟΙ", "Widgets" to "ΠΙΝΑΚΙΔΙΑ", "Tema" to "ΚΟΣΜΟΣ", "Fotos del día" to "ΕΙΚΟΝΕΣ",
        "Corregir gasto" to "ΕΠΑΝΟΡΘΩΣΙΣ", "Recordatorios" to "ΥΠΟΜΝΗΣΕΙΣ", "Te falta hoy" to "ΤΑ ΛΕΙΠΟΝΤΑ", "Datos que faltan" to "ΚΕΝΑ",
        "Tablero" to "ΠΙΝΑΞ", "Tu día en 24 horas" to "ΤΟ ΗΜΕΡΟΝΥΚΤΙΟΝ", "Resumen semanal" to "ΕΒΔΟΜΑΣ",
    )
    fun nombre(titulo: String): String? = NOMBRES[titulo]
}

/**
 * Una frase griega por día para el tema helénico. Original en griego y traducción propia;
 * casi todas son de textos clásicos que se conservan (se indica la fuente).
 */
object Frases {
    data class Frase(val griego: String?, val texto: String, val autor: String)

    val TODAS = listOf(
        Frase("ὁ δὲ ἀνεξέταστος βίος οὐ βιωτὸς ἀνθρώπῳ", "Una vida sin examen no merece ser vivida.", "Sócrates, en la Apología de Platón"),
        Frase("γνῶθι σεαυτόν", "Conócete a ti mismo.", "Inscripción del templo de Delfos"),
        Frase("μηδὲν ἄγαν", "Nada en exceso.", "Inscripción del templo de Delfos"),
        Frase("ἦθος ἀνθρώπῳ δαίμων", "El carácter de cada quien es su destino.", "Heráclito, fragmento 119"),
        Frase("ποταμῷ γὰρ οὐκ ἔστιν ἐμβῆναι δὶς τῷ αὐτῷ", "No es posible entrar dos veces en el mismo río.", "Heráclito, fragmento 91"),
        Frase("φύσις κρύπτεσθαι φιλεῖ", "A la naturaleza le gusta ocultarse.", "Heráclito, fragmento 123"),
        Frase("ἐδιζησάμην ἐμεωυτόν", "Me he buscado a mí mismo.", "Heráclito, fragmento 101"),
        Frase("μία γὰρ χελιδὼν ἔαρ οὐ ποιεῖ, οὐδὲ μία ἡμέρα", "Una golondrina no hace la primavera, ni tampoco un solo día.", "Aristóteles, Ética a Nicómaco I"),
        Frase("τὰ μὲν δίκαια πράττοντες δίκαιοι γινόμεθα", "Nos volvemos justos haciendo cosas justas: lo que repetimos nos forma.", "Aristóteles, Ética a Nicómaco II"),
        Frase("πάντες ἄνθρωποι τοῦ εἰδέναι ὀρέγονται φύσει", "Todos los seres humanos desean por naturaleza saber.", "Aristóteles, Metafísica I"),
        Frase("ἄνθρωπος φύσει πολιτικὸν ζῷον", "El ser humano es por naturaleza un animal político.", "Aristóteles, Política I"),
        Frase("ὁ βίος βραχύς, ἡ δὲ τέχνη μακρή", "La vida es breve; el arte, largo.", "Hipócrates, Aforismos"),
        Frase("γένοι᾽ οἷος ἐσσὶ μαθών", "Llega a ser el que eres, una vez que lo hayas aprendido.", "Píndaro, Pítica II"),
        Frase("ταράσσει τοὺς ἀνθρώπους οὐ τὰ πράγματα, ἀλλὰ τὰ περὶ τῶν πραγμάτων δόγματα", "No nos perturban las cosas, sino lo que opinamos de ellas.", "Epicteto, Enquiridión 5"),
        Frase("τῶν ὄντων τὰ μέν ἐστιν ἐφ᾽ ἡμῖν, τὰ δὲ οὐκ ἐφ᾽ ἡμῖν", "De las cosas, unas dependen de nosotros y otras no.", "Epicteto, Enquiridión 1"),
        Frase("ὁ κόσμος ἀλλοίωσις, ὁ βίος ὑπόληψις", "El mundo es cambio; la vida, opinión.", "Marco Aurelio, Meditaciones IV"),
        Frase("μὴ ὡς μύρια μέλλων ἔτη ζῆν", "No vivas como si te quedaran diez mil años.", "Marco Aurelio, Meditaciones IV"),
        Frase("κτῆμά τε ἐς αἰεὶ", "Lo escribí como una adquisición para siempre.", "Tucídides, Historia I"),
        Frase("πάντων χρημάτων μέτρον ἐστὶν ἄνθρωπος", "El ser humano es la medida de todas las cosas.", "Protágoras"),
        Frase("χαλεπὸν τὸ ἑαυτὸν γνῶναι", "Lo más difícil es conocerse a uno mismo.", "Tales de Mileto, según Diógenes Laercio"),
        Frase("εἰ μὴ Ἀλέξανδρος ἤμην, Διογένης ἂν ἤμην", "Si no fuera Alejandro, sería Diógenes.", "Alejandro Magno, según Plutarco"),
        Frase("μικρὸν ἀπὸ τοῦ ἡλίου μετάστηθι", "Apártate un poco, que me tapas el sol.", "Diógenes a Alejandro, según Plutarco"),
        Frase("τὰς ἐλπίδας", "«La esperanza»: lo que Alejandro guardó para sí al repartir sus bienes antes de partir a Asia.", "Plutarco, Vida de Alejandro 15"),
        Frase("τῷ κρατίστῳ", "«Al más fuerte»: la respuesta de Alejandro, ya moribundo, cuando le preguntaron a quién dejaba su reino.", "Arriano, Anábasis VII"),
        Frase("οὔτε γὰρ ἱστορίας γράφομεν, ἀλλὰ βίους", "No escribimos historias, sino vidas.", "Plutarco, Vida de Alejandro 1"),
        Frase(null, "Un hecho pequeño, una frase o una broma revelan mejor el carácter que batallas con miles de muertos.", "Plutarco, Vida de Alejandro 1"),
        Frase("ἄνθρωπος μικρὸς κόσμος", "El ser humano es un pequeño cosmos.", "Demócrito"),
        Frase("πολλὰ τὰ δεινὰ κοὐδὲν ἀνθρώπου δεινότερον πέλει", "Muchas cosas son asombrosas, pero ninguna más que el ser humano.", "Sófocles, Antígona"),
        Frase("οἵη περ φύλλων γενεὴ τοίη δὲ καὶ ἀνδρῶν", "Como la generación de las hojas, así la de los seres humanos.", "Homero, Ilíada VI"),
        Frase("ἄνδρα μοι ἔννεπε, Μοῦσα, πολύτροπον", "Háblame, Musa, del hombre de muchos caminos.", "Homero, Odisea I"),
        Frase("κλέος ἄφθιτον", "Gloria imperecedera: lo que eligió Aquiles, el héroe que Alejandro quiso imitar.", "Homero, Ilíada IX"),
        Frase("τῆς δ᾽ ἀρετῆς ἱδρῶτα θεοὶ προπάροιθεν ἔθηκαν", "Delante de la excelencia, los dioses pusieron el sudor.", "Hesíodo, Trabajos y días"),
        Frase("καιρὸν γνῶθι", "Conoce el momento oportuno.", "Pítaco de Mitilene"),
        Frase("ἀρχὴ παντὸς ἔργου μέγιστον", "El comienzo es lo más importante de toda obra.", "Platón, República II"),
        Frase("πόλλ᾽ οἶδ᾽ ἀλώπηξ, ἀλλ᾽ ἐχῖνος ἓν μέγα", "La zorra sabe muchas cosas; el erizo, una sola y grande.", "Arquíloco"),
        Frase("μέγα βιβλίον μέγα κακόν", "Libro grande, gran mal.", "Calímaco"),
        Frase("ὁ θάνατος οὐδὲν πρὸς ἡμᾶς", "La muerte no es nada para nosotros.", "Epicuro, Máximas capitales II"),
        Frase("χαλεπὸν ἐσθλὸν ἔμμεναι", "Es difícil ser bueno.", "Pítaco, citado por Simónides"),
    )

    /** La del día: cambia cada día y recorre todas antes de repetir. */
    fun del(fecha: String): Frase = TODAS[Math.floorMod(Fechas.parse(fecha).toEpochDay(), TODAS.size.toLong()).toInt()]
}
