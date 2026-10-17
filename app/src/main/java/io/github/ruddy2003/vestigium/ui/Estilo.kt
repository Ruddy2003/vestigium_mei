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

    /** Número de expediente o de protocolo estable para cada pantalla (no cambia entre aperturas). */
    private fun folio(s: String, desde: Int, rango: Int) = (s.hashCode() and 0x7fffffff) % rango + desde

    /** Prefijo de los comentarios (la línea pequeña bajo los títulos). */
    val comentario: String get() = when (tema) {
        TemaId.ROMANO, TemaId.CIBER, TemaId.ORDEN -> "// "
        TemaId.HELENO -> "— "
        TemaId.MEDIEVAL -> "¶ "
        TemaId.DISTOPIA -> "▪ "
        TemaId.REFUGIO -> "> "
        TemaId.LABORATORIO -> "· "
        TemaId.COLONOS -> "~ "
    }

    /** ¿Los títulos van en mayúsculas? La gótica y la letra de imprenta antigua se leen mejor en minúsculas. */
    val mayusculas: Boolean get() = tema != TemaId.MEDIEVAL && tema != TemaId.COLONOS

    /** Nombre del tema para un título en español, o null si ese tema no lo renombra. */
    private fun nombreNuevo(titulo: String): String? = Temas.nombre(tema, titulo)

    private fun comentarioNuevo(titulo: String, nombre: String, pantalla: Boolean): String? {
        val t = titulo.lowercase()
        return when (tema) {
            TemaId.DISTOPIA -> if (pantalla) "EXPEDIENTE Nº %04d · %s".format(folio(titulo, 1000, 9000), t) else if (nombre.equals(titulo, true)) null else "▪ $t"
            TemaId.LABORATORIO -> if (pantalla) "protocolo VM-%03d · %s".format(folio(titulo, 100, 900), t) else if (nombre.equals(titulo, true)) null else "· $t"
            TemaId.REFUGIO -> if (nombre.equals(titulo, true)) null else "[ $t ]"
            TemaId.ORDEN -> if (nombre.equals(titulo, true)) null else "— $t —"
            TemaId.COLONOS -> if (nombre.equals(titulo, true)) null else "~ $t ~"
            TemaId.MEDIEVAL -> if (nombre.equals(titulo, true)) null else "¶ $t"
            else -> null
        }
    }

    /** Título grande y comentario de una pantalla. */
    fun pantalla(titulo: String, latin: String?): Pair<String, String?> = when (tema) {
        TemaId.ROMANO -> (latin ?: titulo.uppercase()) to (if (latin != null) "// ${titulo.lowercase()}" else null)
        TemaId.CIBER -> titulo.uppercase() to "// sys.${slug(latin ?: titulo)}"
        TemaId.HELENO -> Griego.nombre(titulo)?.let { it to "— ${titulo.lowercase()}" } ?: (titulo.uppercase() to null)
        else -> {
            val n = nombreNuevo(titulo) ?: if (mayusculas) titulo.uppercase() else titulo
            n to comentarioNuevo(titulo, n, true)
        }
    }

    /** Título y comentario de una sección. */
    fun seccion(titulo: String, latin: String?): Pair<String, String?> = when (tema) {
        TemaId.ROMANO -> (latin ?: titulo.uppercase()) to (if (latin != null) "// ${titulo.lowercase()}" else null)
        TemaId.CIBER -> "> ${titulo.lowercase()}" to null
        TemaId.HELENO -> Griego.nombre(titulo)?.let { it to "— ${titulo.lowercase()}" } ?: (titulo.uppercase() to null)
        else -> {
            val n = nombreNuevo(titulo) ?: if (mayusculas) titulo.uppercase() else titulo
            n to comentarioNuevo(titulo, n, false)
        }
    }

    fun numeral(n: Int): String = when (tema) {
        TemaId.ROMANO -> Romano.de(n)
        TemaId.CIBER -> if (n < 100) "[%02d]".format(n) else "0x%X".format(n)
        TemaId.HELENO -> Griego.numeral(n)
        TemaId.MEDIEVAL -> Temas.copista(n)
        TemaId.DISTOPIA -> if (n < 100) "Nº%02d".format(n) else "Nº$n"
        TemaId.REFUGIO -> if (n < 100) "#%02d".format(n) else "#$n"
        TemaId.ORDEN -> if (n < 100) "%02d".format(n) else "$n"
        TemaId.LABORATORIO -> if (n < 100) "%02d".format(n) else "$n"
        TemaId.COLONOS -> if (n < 100) "§ $n" else "$n"
    }

    /** Días desde el primer registro: «día N en el refugio», «T+N» en el laboratorio. */
    private fun diasDesdeInicio(iso: String): Long? = runCatching {
        val primero = Repo.dias.keys.minOrNull() ?: return null
        Fechas.parse(iso).toEpochDay() - Fechas.parse(primero).toEpochDay() + 1
    }.getOrNull()

    /** Fecha solemne del encabezado. */
    fun fecha(iso: String): String = when (tema) {
        TemaId.ROMANO -> Romano.fecha(iso)
        TemaId.CIBER -> runCatching { val d = Fechas.parse(iso); "%04d.%02d.%02d // %s".format(d.year, d.monthValue, d.dayOfMonth, SEMANA_ES[d.dayOfWeek.value - 1]) }.getOrDefault(iso)
        TemaId.HELENO -> Griego.fecha(iso)
        TemaId.MEDIEVAL -> runCatching { val d = Fechas.parse(iso); "${Temas.copista(d.dayOfMonth)} de ${Temas.MESES_MEDIEVALES[d.monthValue - 1]} de ${Temas.copista(d.year)}" }.getOrDefault(iso)
        TemaId.DISTOPIA -> runCatching { val d = Fechas.parse(iso); "JORNADA %03d · AÑO %d".format(d.dayOfYear, d.year) }.getOrDefault(iso)
        TemaId.REFUGIO -> runCatching {
            val d = Fechas.parse(iso)
            "%02d.%s.%d".format(d.dayOfMonth, MESES_ES[d.monthValue - 1], d.year) + (diasDesdeInicio(iso)?.takeIf { it > 0 }?.let { " · DÍA $it" } ?: "")
        }.getOrDefault(iso)
        TemaId.ORDEN -> runCatching { val d = Fechas.parse(iso); "%02d %s %02d // %s".format(d.dayOfMonth, MESES_ES[d.monthValue - 1], d.year % 100, SEMANA_LARGA[d.dayOfWeek.value - 1].uppercase()) }.getOrDefault(iso)
        TemaId.LABORATORIO -> iso + (diasDesdeInicio(iso)?.takeIf { it > 0 }?.let { " · T+$it" } ?: "")
        TemaId.COLONOS -> runCatching { val d = Fechas.parse(iso); "${SEMANA_LARGA[d.dayOfWeek.value - 1]}, ${d.dayOfMonth} de ${MESES_LARGOS[d.monthValue - 1]} de ${d.year}" }.getOrDefault(iso)
    }

    fun mes(n: Int, anio: Int? = null): String {
        val i = (n - 1).coerceIn(0, 11)
        return when (tema) {
            TemaId.ROMANO -> Romano.mes(n)
            TemaId.CIBER -> MESES_ES[i] + (anio?.let { ".$it" } ?: "")
            TemaId.HELENO -> Griego.MESES[i]
            TemaId.MEDIEVAL -> Temas.MESES_MEDIEVALES[i]
            TemaId.DISTOPIA -> "MES %02d".format(n) + (anio?.let { " · $it" } ?: "")
            TemaId.REFUGIO, TemaId.ORDEN -> MESES_ES[i] + (anio?.let { " $it" } ?: "")
            TemaId.LABORATORIO -> MESES_LARGOS[i].uppercase() + (anio?.let { " $it" } ?: "")
            TemaId.COLONOS -> MESES_LARGOS[i].replaceFirstChar { it.uppercase() } + (anio?.let { " de $it" } ?: "")
        }
    }

    val semana: List<String> get() = when (tema) {
        TemaId.ROMANO -> Romano.SEMANA; TemaId.CIBER, TemaId.DISTOPIA, TemaId.REFUGIO, TemaId.ORDEN -> SEMANA_ES; TemaId.HELENO -> Griego.SEMANA
        // Los monjes contaban «ferias»: el lunes es la segunda, el martes la tercera…
        TemaId.MEDIEVAL -> listOf("ij", "iij", "iiij", "v", "vj", "Sab", "Dom")
        TemaId.LABORATORIO -> listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do")
        TemaId.COLONOS -> listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    }

    /** Dos letras por día, para el widget del mes. */
    val semanaCorta: List<String> get() = when (tema) {
        TemaId.ROMANO, TemaId.CIBER, TemaId.HELENO -> semana.map { it.take(2) }
        else -> listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do").let { l -> if (mayusculas) l.map { it.uppercase() } else l }
    }

    /** Marcas del calendario: calendas, nonas e idus (romano); las fiestas que dividían el año (medieval). */
    fun marca(fecha: String): String? = when (tema) {
        TemaId.ROMANO -> Romano.marca(fecha)
        TemaId.MEDIEVAL -> Temas.FIESTAS[fecha.takeLast(5)]
        else -> null
    }

    val atras: String get() = when (tema) {
        TemaId.ROMANO -> "← cd .."; TemaId.CIBER -> "<< cd .."; TemaId.HELENO -> "← ὀπίσω"; TemaId.MEDIEVAL -> "← tornar"
        TemaId.DISTOPIA -> "◄ RETROCEDER"; TemaId.REFUGIO -> "< SALIR"; TemaId.ORDEN -> "« REPLEGARSE"; TemaId.LABORATORIO -> "← volver"; TemaId.COLONOS -> "← volver"
    }
    val prefijo: String get() = when (tema) {
        TemaId.ROMANO -> "› "; TemaId.CIBER -> "$ "; TemaId.HELENO -> "· "; TemaId.MEDIEVAL -> "¶ "; TemaId.DISTOPIA -> "▪ "
        TemaId.REFUGIO -> "> "; TemaId.ORDEN -> "▸ "; TemaId.LABORATORIO -> "○ "; TemaId.COLONOS -> "☞ "
    }

    /** Rótulos de los widgets. */
    fun widget(clave: String): String = when (tema) {
        TemaId.ROMANO -> mapOf("animo" to "ANIMVS", "momento" to "MOMENTA", "gasto" to "SVMPTVS", "sueno" to "SOMNVS", "habitos" to "CONSVETVDINES", "hoy" to "DIES", "cifra" to "NVMERVS", "foto" to "IMAGO")[clave] ?: clave.uppercase()
        TemaId.CIBER -> "> " + (mapOf("sueno" to "sueño", "habitos" to "hábitos", "animo" to "ánimo")[clave] ?: clave)
        TemaId.HELENO -> mapOf("animo" to "ΘΥΜΟΣ", "momento" to "ΚΑΙΡΟΙ", "gasto" to "ΔΑΠΑΝΑΙ", "sueno" to "ΥΠΝΟΣ", "habitos" to "ΕΘΗ", "hoy" to "ΗΜΕΡΑ", "cifra" to "ΑΡΙΘΜΟΣ", "foto" to "ΕΙΚΩΝ")[clave] ?: clave.uppercase()
        else -> Temas.widget(tema, clave)
    }

    /** Claves en latín de las líneas «clave = valor» (Día, widget «hoy»), traducidas al tema. */
    fun clave(c: String): String {
        if (tema == TemaId.ROMANO || tema == TemaId.MEDIEVAL) return c  // en el scriptorium también se escribía en latín
        val sangria = c.takeWhile { it == ' ' }
        val k = c.trim()
        val v = when (tema) {
            TemaId.CIBER -> CLAVES_CIBER[k]
            TemaId.HELENO -> CLAVES_GRIEGO[k]
            TemaId.DISTOPIA, TemaId.ORDEN -> CLAVES_ES[k]?.uppercase()
            else -> CLAVES_ES[k]
        }
        return sangria + (v ?: k)
    }

    private val CLAVES_CIBER = mapOf(
        "horologium" to "watch", "somnus" to "sleep", "stadia" to "stages", "cor" to "heart", "passus" to "steps", "motus" to "motion",
        "iter" to "route", "exercitatio" to "workout", "loca" to "places", "telephonum" to "screen", "telephon" to "screen", "usus" to "usage",
        "monita" to "notifs", "musica" to "music", "imagines" to "photos", "fasti" to "agenda", "caelum" to "sky", "lux" to "daylight",
        "aer" to "air", "corpus" to "body", "nutritio" to "food", "cibus" to "meals", "robur" to "gym", "musculi" to "muscles",
        "voces" to "calls", "soni" to "audio", "retia" to "wifi", "lumen" to "light", "vis" to "charge", "lectio" to "kindle",
        "strepitus" to "noise", "luna" to "moon", "dias" to "days", "animus" to "mood", "optimus" to "best", "momenta" to "events",
        "svmptvs" to "spent", "memorabilia" to "milestones", "ascensvs" to "climbing",
    )
    private val CLAVES_GRIEGO = mapOf(
        "horologium" to "ὡρολόγιον", "somnus" to "ὕπνος", "stadia" to "στάδια", "cor" to "καρδία", "passus" to "βήματα", "motus" to "κίνησις",
        "iter" to "ὁδός", "exercitatio" to "ἄσκησις", "loca" to "τόποι", "telephonum" to "ὀθόνη", "telephon" to "ὀθόνη", "usus" to "χρῆσις",
        "monita" to "σήματα", "musica" to "μουσική", "imagines" to "εἰκόνες", "fasti" to "ἔργα", "caelum" to "οὐρανός", "lux" to "φῶς",
        "aer" to "ἀήρ", "corpus" to "σῶμα", "nutritio" to "τροφή", "cibus" to "σιτία", "robur" to "ῥώμη", "musculi" to "μύες",
        "voces" to "φωναί", "soni" to "ἦχοι", "retia" to "δίκτυα", "lumen" to "φέγγος", "vis" to "δύναμις", "lectio" to "ἀνάγνωσις",
        "strepitus" to "θόρυβος", "luna" to "σελήνη", "dias" to "ἡμέραι", "animus" to "θυμός", "optimus" to "ἄριστος", "momenta" to "καιροί",
        "svmptvs" to "δαπάνη", "memorabilia" to "μνημεῖα", "ascensvs" to "ἀνάβασις",
    )
    private val CLAVES_ES = mapOf(
        "horologium" to "reloj", "somnus" to "sueño", "stadia" to "fases", "cor" to "corazón", "passus" to "pasos", "motus" to "movimiento",
        "iter" to "ruta", "exercitatio" to "ejercicio", "loca" to "lugares", "telephonum" to "pantalla", "telephon" to "pantalla", "usus" to "uso",
        "monita" to "avisos", "musica" to "música", "imagines" to "fotos", "fasti" to "agenda", "caelum" to "clima", "lux" to "luz_día",
        "aer" to "aire", "corpus" to "cuerpo", "nutritio" to "nutrición", "cibus" to "comidas", "robur" to "gimnasio", "musculi" to "músculos",
        "voces" to "llamadas", "soni" to "audio", "retia" to "wifi", "lumen" to "luz", "vis" to "carga", "lectio" to "lectura",
        "strepitus" to "ruido", "luna" to "luna", "dias" to "días", "animus" to "ánimo", "optimus" to "mejor", "momenta" to "momentos",
        "svmptvs" to "gasto", "memorabilia" to "hitos", "ascensvs" to "escalada",
    )

    private val SEMANA_ES = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    private val SEMANA_LARGA = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo")
    private val MESES_ES = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")
    private val MESES_LARGOS = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
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
        "Tablero" to "ΠΙΝΑΞ", "Buscar" to "ΖΗΤΗΣΙΣ", "Cifras" to "ΑΡΙΘΜΟΙ", "Momentos de la semana" to "ΚΑΙΡΟΙ ΤΗΣ ΕΒΔΟΜΑΔΟΣ", "Tu día en 24 horas" to "ΤΟ ΗΜΕΡΟΝΥΚΤΙΟΝ", "Resumen semanal" to "ΕΒΔΟΜΑΣ",
    )
    fun nombre(titulo: String): String? = NOMBRES[titulo]
}
