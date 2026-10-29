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
    val tema: TemaId get() = Repo.config.tema.let { id -> TemaId.entries.firstOrNull { it.id == id } ?: if (id == "muertos") TemaId.BOTANICO else TemaId.ROMANO }

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
        TemaId.NAPOLEON -> "— "
        TemaId.BOTANICO -> "⁂ "
        TemaId.NAVIDAD -> "✦ "
        TemaId.SEDA -> "◇ "
        TemaId.URSS -> "★ "
        TemaId.GOTICO -> "✠ "
        TemaId.ESPACIO -> "⋆ "
        TemaId.ANDALUS -> "❋ "
        TemaId.MONGOL -> "◆ "
        TemaId.RENACIMIENTO -> "❧ "
        TemaId.REVOLUCION -> "✶ "
        TemaId.ANTIGUAS -> "⟡ "
    }

    /** ¿Los títulos van en mayúsculas? La gótica y la letra de imprenta antigua se leen mejor en minúsculas. */
    val mayusculas: Boolean get() = tema !in setOf(TemaId.MEDIEVAL, TemaId.COLONOS, TemaId.NAVIDAD, TemaId.SEDA, TemaId.GOTICO, TemaId.RENACIMIENTO, TemaId.BOTANICO)

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
            TemaId.ROMANO, TemaId.CIBER, TemaId.HELENO -> null
            else -> if (nombre.equals(titulo, true)) null else comentario + t
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

    /**
     * El título de una pantalla en una línea (P-1): el nombre del tema y, si el tema lo renombra, el nombre en español
     * aparte, para ponerlos juntos y del mismo tamaño («Quaestiones · Análisis»). El comentario solo queda si dice algo
     * más que el nombre en español (el número de expediente, el protocolo, el «sys.» de la terminal).
     */
    fun pantallaEnLinea(titulo: String, latin: String?): Triple<String, String?, String?> {
        val (n, c) = pantalla(titulo, latin)
        val renombra = slug(n) != slug(titulo)
        val extra = if (renombra && tema !in setOf(TemaId.DISTOPIA, TemaId.LABORATORIO)) null else c
        return Triple(n, titulo.takeIf { renombra }, extra)
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
        TemaId.NAPOLEON -> Romano.de(n)
        TemaId.BOTANICO, TemaId.NAVIDAD, TemaId.MONGOL, TemaId.RENACIMIENTO -> "$n"
        TemaId.SEDA -> Temas.chino(n)
        TemaId.URSS -> "№ $n"
        TemaId.GOTICO -> if (n in 1..3999) Romano.de(n).lowercase() else "$n"
        TemaId.ESPACIO -> "%03d".format(n)
        TemaId.ANDALUS -> Temas.arabe(n)
        TemaId.REVOLUCION -> if (n < 100) "N° $n" else "$n"
        TemaId.ANTIGUAS -> if (n in 1..3999) Romano.de(n) else "$n"
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
        TemaId.NAPOLEON -> Temas.fechaImperial(iso)
        TemaId.REVOLUCION -> Temas.republicano(iso)
        TemaId.ANTIGUAS -> Temas.fechaAntigua(iso)
        TemaId.BOTANICO -> runCatching { val d = Fechas.parse(iso); "${d.dayOfMonth} de ${MESES_LARGOS[d.monthValue - 1]} · ${Temas.estacion(d)}" }.getOrDefault(iso)
        TemaId.GOTICO -> runCatching {
            val d = Fechas.parse(iso)
            Temas.fechaLatina(d) + if (iso == Fechas.diaLogico()) " · ad ${Temas.horaCanonica(java.time.LocalTime.now().hour)}" else ""
        }.getOrDefault(iso)
        TemaId.ESPACIO -> runCatching { val d = Fechas.parse(iso); String.format(java.util.Locale.US, "JD %,d · %04d-%02d-%02d", Temas.diaJuliano(d), d.year, d.monthValue, d.dayOfMonth).replace(',', ' ') }.getOrDefault(iso)
        TemaId.ANDALUS -> runCatching { val d = Fechas.parse(iso); Temas.fechaHegira(d) + " · ${d.dayOfMonth} ${MESES_ANDALUSIES[d.monthValue - 1].lowercase()}" }.getOrDefault(iso)
        TemaId.MONGOL -> runCatching { val d = Fechas.parse(iso); Temas.anioMongol(d) + " · " + Temas.fechaMongol(d) }.getOrDefault(iso)
        TemaId.RENACIMIENTO -> runCatching { Temas.fechaItaliana(Fechas.parse(iso)) }.getOrDefault(iso)
        TemaId.NAVIDAD -> runCatching { val d = Fechas.parse(iso); "${d.dayOfMonth} de ${MESES_LARGOS[d.monthValue - 1]} · " + Temas.cuentaAtras(d, 12, 25, "Navidad", "Navidad") }.getOrDefault(iso)
        TemaId.SEDA -> runCatching { val d = Fechas.parse(iso); Temas.fechaChina(d.year, d.monthValue, d.dayOfMonth) }.getOrDefault(iso)
        TemaId.URSS -> runCatching {
            val d = Fechas.parse(iso)
            "%d.%s.%d".format(d.dayOfMonth, Romano.de(d.monthValue), d.year) + (diasDesdeInicio(iso)?.takeIf { it > 0 }?.let { " · PLAN QUINQUENAL, AÑO ${((it - 1) / 365) % 5 + 1}" } ?: "")
        }.getOrDefault(iso)
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
            TemaId.NAPOLEON -> Temas.MESES_FRANCESES[i].uppercase() + (anio?.let { " $it" } ?: "")
            TemaId.BOTANICO, TemaId.NAVIDAD -> MESES_LARGOS[i].replaceFirstChar { it.uppercase() } + (anio?.let { " $it" } ?: "")
            TemaId.SEDA -> (anio?.let { Temas.anioChino(it) + " " } ?: "") + Temas.chino(n) + "月"
            TemaId.URSS -> Temas.MESES_RUSOS[i] + (anio?.let { " $it" } ?: "")
            TemaId.GOTICO -> Temas.MESES_LATINOS[i] + (anio?.let { " " + Romano.de(it) } ?: "")
            TemaId.ESPACIO -> MESES_ES[i] + (anio?.let { " · $it" } ?: "")
            // Los andalusíes nombraban los meses del sol con palabras salidas del latín: yannayr, fibrayr, mars…
            TemaId.ANDALUS -> MESES_ANDALUSIES[i] + (anio?.let { " $it" } ?: "")
            TemaId.MONGOL -> "$n-р сар" + (anio?.let { " · $it" } ?: "")
            TemaId.RENACIMIENTO -> Temas.MESES_ITALIANOS[i].replaceFirstChar { it.uppercase() } + (anio?.let { " $it" } ?: "")
            TemaId.REVOLUCION -> Temas.MESES_FRANCESES[i].uppercase() + (anio?.let { " " + Temas.anRepublicano(it, n) } ?: "")
            TemaId.ANTIGUAS -> Temas.LUNAS[i] + (anio?.let { " · $it" } ?: "")
        }
    }

    val semana: List<String> get() = when (tema) {
        TemaId.ROMANO -> Romano.SEMANA; TemaId.CIBER, TemaId.DISTOPIA, TemaId.REFUGIO, TemaId.ORDEN -> SEMANA_ES; TemaId.HELENO -> Griego.SEMANA
        // Los monjes contaban «ferias»: el lunes es la segunda, el martes la tercera…
        TemaId.MEDIEVAL -> listOf("ij", "iij", "iiij", "v", "vj", "Sab", "Dom")
        TemaId.LABORATORIO -> listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do")
        TemaId.COLONOS, TemaId.BOTANICO, TemaId.NAVIDAD -> listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        TemaId.NAPOLEON, TemaId.REVOLUCION -> listOf("LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM")
        TemaId.ANTIGUAS -> listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
        TemaId.SEDA -> listOf("一", "二", "三", "四", "五", "六", "日")
        TemaId.URSS -> listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")
        TemaId.GOTICO -> listOf("ij", "iij", "iv", "v", "vj", "Sab", "Dom")
        TemaId.ESPACIO -> SEMANA_ES
        TemaId.ANDALUS -> listOf("اثن", "ثلا", "أرب", "خمي", "جمع", "سبت", "أحد")
        TemaId.MONGOL -> listOf("ДА", "МЯ", "ЛХ", "ПҮ", "БА", "БЯ", "НЯ")
        TemaId.RENACIMIENTO -> listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
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
        else -> Temas.marca(tema, fecha)
    }

    /** Los días seguidos sellados, bajo el sello del día (n ≥ 2). */
    fun racha(n: Int): String = when (tema) {
        TemaId.ROMANO -> "DIES CONTINVI · ${Romano.de(n)}"; TemaId.CIBER -> "> streak --días $n"; TemaId.HELENO -> "ΣΥΝΕΧΕΙΣ ΗΜΕΡΑΙ · $n"
        TemaId.MEDIEVAL -> "$n días seguidos en el libro"; TemaId.DISTOPIA -> "CONTINUIDAD ACREDITADA: $n DÍAS"; TemaId.REFUGIO -> "DÍAS SIN INCIDENTES: $n"
        TemaId.ORDEN -> "$n VIGILIAS SIN FALTA"; TemaId.LABORATORIO -> "serie de $n sesiones"; TemaId.COLONOS -> "$n jornadas seguidas"
        TemaId.NAPOLEON -> "$n JOURS DE CAMPAGNE"; TemaId.BOTANICO -> "$n días de herbario"; TemaId.NAVIDAD -> "$n noches junto al fuego"
        TemaId.SEDA -> "$n jornadas de caravana"; TemaId.GOTICO -> "$n días de oficio sin falta"; TemaId.ESPACIO -> "T+$n DÍAS EN ÓRBITA"
        TemaId.ANDALUS -> "$n días seguidos · متتالية"; TemaId.MONGOL -> "$n ӨДӨР ДАРААЛАН"; TemaId.RENACIMIENTO -> "$n giorni di fila"
        TemaId.URSS -> "ПЛАН: $n ДНЕЙ ПОДРЯД"; TemaId.REVOLUCION -> "$n JOURS DE VERTU"; TemaId.ANTIGUAS -> "$n JORNADAS DE CAMINO"
    }

    val atras: String get() = when (tema) {
        TemaId.ROMANO -> "← cd .."; TemaId.CIBER -> "<< cd .."; TemaId.HELENO -> "← ὀπίσω"; TemaId.MEDIEVAL -> "← tornar"
        TemaId.DISTOPIA -> "◄ RETROCEDER"; TemaId.REFUGIO -> "< SALIR"; TemaId.ORDEN -> "« REPLEGARSE"; TemaId.LABORATORIO -> "← volver"; TemaId.COLONOS -> "← volver"
        TemaId.NAPOLEON -> "← retirada"; TemaId.BOTANICO -> "← regresar"; TemaId.NAVIDAD -> "← volver"; TemaId.SEDA -> "← regresar"; TemaId.URSS -> "◄ НАЗАД"
        TemaId.GOTICO -> "← redire"; TemaId.ESPACIO -> "◄ RETORNO"; TemaId.ANDALUS -> "← رجوع"; TemaId.MONGOL -> "◄ БУЦАХ"; TemaId.RENACIMIENTO -> "← indietro"; TemaId.REVOLUCION -> "← retour"; TemaId.ANTIGUAS -> "← volver al camino"
    }
    val prefijo: String get() = when (tema) {
        TemaId.ROMANO -> "› "; TemaId.CIBER -> "$ "; TemaId.HELENO -> "· "; TemaId.MEDIEVAL -> "¶ "; TemaId.DISTOPIA -> "▪ "
        TemaId.REFUGIO -> "> "; TemaId.ORDEN -> "▸ "; TemaId.LABORATORIO -> "○ "; TemaId.COLONOS -> "☞ "
        TemaId.NAPOLEON -> "» "; TemaId.BOTANICO -> "⁂ "; TemaId.NAVIDAD -> "✦ "; TemaId.SEDA -> "◇ "; TemaId.URSS -> "▶ "
        TemaId.GOTICO -> "✠ "; TemaId.ESPACIO -> "◦ "; TemaId.ANDALUS -> "◈ "; TemaId.MONGOL -> "▹ "; TemaId.RENACIMIENTO -> "✎ "; TemaId.REVOLUCION -> "• "; TemaId.ANTIGUAS -> "◆ "
    }

    /**
     * Lo que remata el título de Día y de Hoy, pegado al nombre de la pantalla. Casi todos los temas no llevan nada;
     * la terminal deja su cursor.
     */
    val sufijoTitulo: String get() = when (tema) {
        TemaId.ROMANO -> ""
        TemaId.CIBER -> "_"
        TemaId.HELENO -> ""
        TemaId.MEDIEVAL -> ""
        TemaId.DISTOPIA -> ""
        TemaId.REFUGIO -> ""
        TemaId.ORDEN -> ""
        TemaId.LABORATORIO -> ""
        TemaId.COLONOS -> ""
        TemaId.NAPOLEON -> ""
        TemaId.BOTANICO -> ""
        TemaId.NAVIDAD -> ""
        TemaId.SEDA -> ""
        TemaId.GOTICO -> ""
        TemaId.ESPACIO -> ""
        TemaId.ANDALUS -> ""
        TemaId.MONGOL -> ""
        TemaId.RENACIMIENTO -> ""
        TemaId.URSS -> ""
        TemaId.REVOLUCION -> ""
        TemaId.ANTIGUAS -> ""
    }

    /**
     * Cómo se ve la fecha grande de Día (la que abre el calendario): la inscripción romana grande y espaciada, la terminal
     * sin adornos, la cursiva de la gaceta, el cartel en negrita… Ver [EstiloFecha].
     */
    val fechaGrande: EstiloFecha get() = when (tema) {
        TemaId.ROMANO -> EstiloFecha(1.27f, 1.8f, peso = 400)
        TemaId.CIBER -> EstiloFecha(1.0f, 0.4f, abre = "↓")
        TemaId.HELENO -> EstiloFecha(1.12f, 3.0f, peso = 500, abre = "▿")
        TemaId.MEDIEVAL -> EstiloFecha(1.1f, 0.3f)
        TemaId.DISTOPIA -> EstiloFecha(1.12f, 1.6f, peso = 700, abre = "▼")
        TemaId.REFUGIO -> EstiloFecha(1.15f, 0.8f, abre = "▼")
        TemaId.ORDEN -> EstiloFecha(1.08f, 2.4f)
        TemaId.LABORATORIO -> EstiloFecha(0.96f, 2.6f, peso = 400, abre = "▿")
        TemaId.COLONOS -> EstiloFecha(1.06f, 0.5f, cursiva = true)
        TemaId.NAPOLEON -> EstiloFecha(1.04f, 2.2f, peso = 600)
        TemaId.BOTANICO -> EstiloFecha(1.0f, 0.2f, cursiva = true, abre = "▿")
        TemaId.NAVIDAD -> EstiloFecha(1.1f, 1.4f)
        TemaId.SEDA -> EstiloFecha(1.08f, 0.6f, abre = "▿")
        TemaId.GOTICO -> EstiloFecha(1.16f, 0.6f)
        TemaId.ESPACIO -> EstiloFecha(0.92f, 2.8f, peso = 500, abre = "▼")
        TemaId.ANDALUS -> EstiloFecha(1.06f, 1.0f, peso = 500)
        TemaId.MONGOL -> EstiloFecha(1.1f, 1.8f, peso = 700, abre = "▼")
        TemaId.RENACIMIENTO -> EstiloFecha(1.14f, 0f)
        TemaId.URSS -> EstiloFecha(1.18f, 1.2f, abre = "▼")
        TemaId.REVOLUCION -> EstiloFecha(1.08f, 2.0f, cursiva = true)
        TemaId.ANTIGUAS -> EstiloFecha(1.1f, 2.6f, peso = 600)
    }

    /** Rótulos de los widgets. */
    fun widget(clave: String): String = when (tema) {
        TemaId.ROMANO -> mapOf("animo" to "ANIMVS", "momento" to "MOMENTA", "gasto" to "SVMPTVS", "sueno" to "SOMNVS", "habitos" to "CONSVETVDINES", "hoy" to "DIES", "cifra" to "NVMERVS", "foto" to "IMAGO", "enfoque" to "STVDIVM")[clave] ?: clave.uppercase()
        TemaId.CIBER -> "> " + (mapOf("sueno" to "sueño", "habitos" to "hábitos", "animo" to "ánimo")[clave] ?: clave)
        TemaId.HELENO -> mapOf("animo" to "ΘΥΜΟΣ", "momento" to "ΚΑΙΡΟΙ", "gasto" to "ΔΑΠΑΝΑΙ", "sueno" to "ΥΠΝΟΣ", "habitos" to "ΕΘΗ", "hoy" to "ΗΜΕΡΑ", "cifra" to "ΑΡΙΘΜΟΣ", "foto" to "ΕΙΚΩΝ", "enfoque" to "ΣΧΟΛΗ")[clave] ?: clave.uppercase()
        else -> Temas.widget(tema, clave)
    }

    /** Claves en latín de las líneas «clave = valor» (Día, widget «hoy»), traducidas al tema. */
    fun clave(c: String): String {
        if (tema == TemaId.ROMANO || tema == TemaId.MEDIEVAL || tema == TemaId.GOTICO) return c  // en el scriptorium también se escribía en latín
        val sangria = c.takeWhile { it == ' ' }
        val k = c.trim()
        val v = when (tema) {
            TemaId.CIBER -> CLAVES_CIBER[k]
            TemaId.HELENO -> CLAVES_GRIEGO[k]
            TemaId.DISTOPIA, TemaId.ORDEN, TemaId.URSS, TemaId.ESPACIO, TemaId.MONGOL, TemaId.REVOLUCION, TemaId.ANTIGUAS -> CLAVES_ES[k]?.uppercase()
            TemaId.RENACIMIENTO -> CLAVES_IT[k]
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
        "dies" to "days", "quaestiones" to "tests", "celeritas" to "reaction", "bibliotheca" to "library", "synchronia" to "sync", "opera" to "works", "iudicium" to "review", "citatio" to "quote", "sententia" to "phrase", "orbis" to "world", "nova" to "new", "mirabilia" to "sites", "somnium" to "dream", "epistulae" to "letters", "aperta" to "opened", "initium" to "start", "finis" to "end", "pessimus" to "worst", "personae" to "people", "themata" to "topics", "praeclarum" to "top", "dies felix" to "best_day", "apps" to "apps",
        "consuetudo" to "habits", "series" to "streak", "palmae" to "achievements", "reditus" to "income",
    )
    private val CLAVES_GRIEGO = mapOf(
        "horologium" to "ὡρολόγιον", "somnus" to "ὕπνος", "stadia" to "στάδια", "cor" to "καρδία", "passus" to "βήματα", "motus" to "κίνησις",
        "iter" to "ὁδός", "exercitatio" to "ἄσκησις", "loca" to "τόποι", "telephonum" to "ὀθόνη", "telephon" to "ὀθόνη", "usus" to "χρῆσις",
        "monita" to "σήματα", "musica" to "μουσική", "imagines" to "εἰκόνες", "fasti" to "ἔργα", "caelum" to "οὐρανός", "lux" to "φῶς",
        "aer" to "ἀήρ", "corpus" to "σῶμα", "nutritio" to "τροφή", "cibus" to "σιτία", "robur" to "ῥώμη", "musculi" to "μύες",
        "voces" to "φωναί", "soni" to "ἦχοι", "retia" to "δίκτυα", "lumen" to "φέγγος", "vis" to "δύναμις", "lectio" to "ἀνάγνωσις",
        "strepitus" to "θόρυβος", "luna" to "σελήνη", "dias" to "ἡμέραι", "animus" to "θυμός", "optimus" to "ἄριστος", "momenta" to "καιροί",
        "svmptvs" to "δαπάνη", "memorabilia" to "μνημεῖα", "ascensvs" to "ἀνάβασις",
        "dies" to "ἡμέραι", "quaestiones" to "ἐρωτήσεις", "celeritas" to "ταχύτης", "bibliotheca" to "βιβλιοθήκη", "synchronia" to "συγχρονία", "opera" to "ἔργα", "iudicium" to "κρίσις", "citatio" to "χωρίον", "sententia" to "γνώμη", "orbis" to "οἰκουμένη", "nova" to "νέα", "mirabilia" to "θαύματα", "somnium" to "ὄνειρος", "epistulae" to "ἐπιστολαί", "aperta" to "ἀνοιχθεῖσα", "initium" to "ἀρχή", "finis" to "τέλος", "pessimus" to "κάκιστος", "personae" to "πρόσωπα", "themata" to "θέματα", "praeclarum" to "ἄριστον", "dies felix" to "εὐτυχὴς ἡμέρα",
        "consuetudo" to "ἔθη", "series" to "σειρά", "palmae" to "νῖκαι", "reditus" to "πρόσοδος",
    )
    private val CLAVES_ES = mapOf(
        "horologium" to "reloj", "somnus" to "sueño", "stadia" to "fases", "cor" to "corazón", "passus" to "pasos", "motus" to "movimiento",
        "iter" to "ruta", "exercitatio" to "ejercicio", "loca" to "lugares", "telephonum" to "pantalla", "telephon" to "pantalla", "usus" to "uso",
        "monita" to "avisos", "musica" to "música", "imagines" to "fotos", "fasti" to "agenda", "caelum" to "clima", "lux" to "luz_día",
        "aer" to "aire", "corpus" to "cuerpo", "nutritio" to "nutrición", "cibus" to "comidas", "robur" to "gimnasio", "musculi" to "músculos",
        "voces" to "llamadas", "soni" to "audio", "retia" to "wifi", "lumen" to "luz", "vis" to "carga", "lectio" to "lectura",
        "strepitus" to "ruido", "luna" to "luna", "dias" to "días", "animus" to "ánimo", "optimus" to "mejor", "momenta" to "momentos",
        "svmptvs" to "gasto", "memorabilia" to "hitos", "ascensvs" to "escalada",
        "dies" to "días", "quaestiones" to "cuestionarios", "celeritas" to "reacción", "bibliotheca" to "biblioteca", "synchronia" to "sincronía", "opera" to "obras", "iudicium" to "opinión", "citatio" to "cita", "sententia" to "frase", "orbis" to "mundo", "nova" to "nuevo", "mirabilia" to "maravillas", "somnium" to "soñé", "epistulae" to "cartas", "aperta" to "abierta", "initium" to "inicio", "finis" to "fin", "pessimus" to "peor", "personae" to "personas", "themata" to "temas", "praeclarum" to "la_mejor", "dies felix" to "día_feliz",
        "consuetudo" to "hábitos", "series" to "racha", "palmae" to "logros", "reditus" to "ingresos",
    )

    private val CLAVES_IT = mapOf(
        "horologium" to "orologio", "somnus" to "sonno", "stadia" to "fasi", "cor" to "cuore", "passus" to "passi", "motus" to "moto",
        "iter" to "cammino", "exercitatio" to "esercizio", "loca" to "luoghi", "telephonum" to "schermo", "telephon" to "schermo", "usus" to "uso",
        "monita" to "avvisi", "musica" to "musica", "imagines" to "immagini", "fasti" to "agenda", "caelum" to "cielo", "lux" to "luce_del_dì",
        "aer" to "aria", "corpus" to "corpo", "nutritio" to "nutrimento", "cibus" to "pasti", "robur" to "palestra", "musculi" to "muscoli",
        "voces" to "chiamate", "soni" to "suoni", "retia" to "reti", "lumen" to "lume", "vis" to "carica", "lectio" to "lettura",
        "strepitus" to "rumore", "luna" to "luna", "dias" to "giorni", "animus" to "animo", "optimus" to "migliore", "momenta" to "momenti",
        "svmptvs" to "spesa", "memorabilia" to "memorie", "ascensvs" to "scalata",
        "dies" to "giorni", "quaestiones" to "questionari", "celeritas" to "riflessi", "bibliotheca" to "biblioteca", "synchronia" to "sincronia", "opera" to "opere", "iudicium" to "giudizio", "citatio" to "citazione", "sententia" to "sentenza", "orbis" to "mondo", "nova" to "nuovo", "mirabilia" to "meraviglie", "somnium" to "sogno", "epistulae" to "lettere", "aperta" to "aperta", "initium" to "inizio", "finis" to "fine", "pessimus" to "peggiore", "personae" to "persone", "themata" to "temi", "praeclarum" to "il_migliore", "dies felix" to "giorno_felice",
        "consuetudo" to "abitudini", "series" to "serie", "palmae" to "traguardi", "reditus" to "entrate",
    )
    private val MESES_ANDALUSIES = listOf("Yanáyir", "Fibráyir", "Maris", "Abril", "Mayu", "Yunyu", "Yulyu", "Gusht", "Shutanbir", "Uktubar", "Nunbir", "Duyanbir")

    private val SEMANA_ES = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
    private val SEMANA_LARGA = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo")
    private val MESES_ES = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC")
    private val MESES_LARGOS = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
}

/**
 * La fecha grande de Día. [escala] multiplica el tamaño de `titleLarge` del tema; [espaciado] (en sp) separa las letras;
 * [peso] (100–900) cambia el grueso o, si es null, deja el del tema; [cursiva] la inclina; [abre] es el signo que invita a
 * abrir el calendario.
 */
data class EstiloFecha(val escala: Float, val espaciado: Float, val peso: Int? = null, val cursiva: Boolean = false, val abre: String = "▾")

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
        "Mundo" to "ΟΙΚΟΥΜΕΝΗ", "Patrimonio Mundial" to "ΘΑΥΜΑΤΑ", "Mapa para rascar" to "ΠΙΝΑΞ ΤΗΣ ΓΗΣ", "Ciudades visitadas" to "ΠΟΛΕΙΣ",
        "Biblioteca de sueños" to "ΟΝΕΙΡΟΙ", "Cartas" to "ΕΠΙΣΤΟΛΑΙ", "Escribir una carta" to "ΓΡΑΦΕ ΕΠΙΣΤΟΛΗΝ", "Carta" to "ΕΠΙΣΤΟΛΗ", "Conocerte" to "ΓΝΩΘΙ ΣΑΥΤΟΝ", "Brújula política" to "ΠΟΛΙΤΕΙΑ", "Mapas" to "ΧΑΡΤΑΙ", "Salud" to "ΥΓΕΙΑ", "Tus apps" to "ΟΡΓΑΝΑ", "Otras vidas" to "ΑΛΛΟΙ ΒΙΟΙ", "Logros" to "ΝΙΚΑΙ", "Tu año en historias" to "ΤΟ ΕΤΟΣ ΣΟΥ", "Anoche" to "ΧΘΕΣ ΝΥΚΤΟΣ", "Selladas" to "ΣΕΣΗΜΑΣΜΕΝΑΙ", "Listas para abrir" to "ΠΡΟΣ ΑΝΟΙΞΙΝ", "A otras personas" to "ΠΡΟΣ ΑΛΛΟΥΣ", "Del pasado" to "ΕΚ ΤΟΥ ΠΑΡΕΛΘΟΝΤΟΣ", "Tus mapas" to "ΧΑΡΤΑΙ", "Tus calles" to "ΟΔΟΙ", "Tú" to "ΣΥ", "Con quién vives" to "ΟΙΚΟΣ", "A quién ves más" to "ΦΙΛΟΙ", "Pareja" to "ΣΥΖΥΓΟΣ", "A qué te dedicas" to "ΕΡΓΟΝ", "Tus costumbres" to "ΤΡΟΠΟΙ", "Lo que está pasando en tu vida" to "ΝΥΝ", "Cómo ha cambiado" to "ΜΕΤΑΒΟΛΑΙ", "Tu brújula" to "ΠΥΞΙΣ", "Los cinco ejes" to "ΠΕΝΤΕ ΑΞΟΝΕΣ", "Qué cambió" to "ΤΙ ΜΕΤΕΒΛΗΘΗ", "Lo que viviste cada año" to "ΧΡΟΝΙΚΑ", "¿Para quién?" to "ΤΙΝΙ;", "La carta" to "Η ΕΠΙΣΤΟΛΗ", "Donde más has estado" to "ΟΠΟΥ ΜΑΛΙΣΤΑ",
        "Día" to "ΗΜΕΡΑ", "Momentos" to "ΚΑΙΡΟΙ", "Gastos" to "ΔΑΠΑΝΑΙ", "Explorar" to "ΙΣΤΟΡΙΑ", "Más" to "ΤΑ ΛΟΙΠΑ",
        "Hábitos" to "ΕΘΗ", "Cartas al futuro" to "ΕΠΙΣΤΟΛΑΙ", "Experimentos personales" to "ΠΕΙΡΑΙ", "Cómo leer tus datos" to "ΕΠΙΣΤΗΜΗ", "Guía científica" to "ΕΠΙΣΤΗΜΗ",
        "Cuestionarios" to "ΕΡΩΤΗΣΕΙΣ", "Prueba de reacción" to "ΤΑΧΟΣ", "Lugares" to "ΤΟΠΟΙ", "Datos" to "ΑΡΧΕΙΟΝ",
        "Permisos y sensores" to "ΑΙΣΘΗΣΕΙΣ", "Ajustes" to "ΤΑΞΙΣ", "Reportes" to "ΥΠΟΜΝΗΜΑΤΑ", "Bienvenido" to "ΧΑΙΡΕ",
        "Calendario" to "ΗΜΕΡΟΛΟΓΙΟΝ", "Escalada" to "ΑΝΑΒΑΣΙΣ", "Obras" to "ΕΡΓΑ", "Tu año" to "ΕΝΙΑΥΤΟΣ",
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
        "Llenar el día" to "ΕΣΠΕΡΑ", "Entender" to "ΓΙΓΝΩΣΚΕΙΝ", "Registros especiales" to "ΑΝΑΓΡΑΦΑΙ", "Apariencia" to "ΚΟΣΜΟΣ", "Avisos y recordatorios" to "ΣΗΜΕΙΑ", "Biblioteca del diario" to "ΒΙΒΛΟΣ ΗΜΕΡΩΝ", "Entrada" to "ΣΕΛΙΣ", "Hoy" to "ΣΗΜΕΡΟΝ", "Línea de tiempo" to "ΧΡΟΝΟΣ", "Ahora mismo estoy…" to "ΝΥΝ",
    )
    fun nombre(titulo: String): String? = NOMBRES[titulo] ?: NombresNuevos.de(TemaId.HELENO, titulo)
}
