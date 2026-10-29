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
        TemaId.ROMANO -> "// "
        TemaId.CIBER -> "# "
        TemaId.HELENO -> "∴ "
        TemaId.MEDIEVAL -> "¶ "
        TemaId.DISTOPIA -> "▪ "
        TemaId.REFUGIO -> "> "
        TemaId.ORDEN -> "‡ "
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

    /**
     * ¿Los títulos van en mayúsculas? Las capitales de inscripción, los carteles y las pantallas sí; la gótica, la letra de
     * imprenta antigua, la caligrafía y la letra de mano se leen mejor en minúsculas.
     */
    val mayusculas: Boolean get() = when (tema) {
        TemaId.ROMANO -> true          // capitales de inscripción
        TemaId.CIBER -> true           // consola
        TemaId.HELENO -> true          // capitales epigráficas
        TemaId.MEDIEVAL -> false       // gótica de libro
        TemaId.DISTOPIA -> true        // cartel
        TemaId.REFUGIO -> true         // pantalla de fósforo
        TemaId.ORDEN -> true           // estarcido
        TemaId.LABORATORIO -> true     // rótulo de protocolo
        TemaId.COLONOS -> false        // imprenta de pueblo
        TemaId.NAPOLEON -> true        // capitales de Bodoni
        TemaId.BOTANICO -> false       // lámina de herbario
        TemaId.NAVIDAD -> false        // tarjeta escrita a mano
        TemaId.SEDA -> false           // manuscrito de caravana
        TemaId.URSS -> true            // cartel constructivista
        TemaId.GOTICO -> false         // textura
        TemaId.ESPACIO -> true         // tablero de misión
        TemaId.ANDALUS -> true         // cúfica de friso
        TemaId.MONGOL -> true          // cirílico de rótulo
        TemaId.RENACIMIENTO -> false   // cancilleresca
        TemaId.REVOLUCION -> true      // cartel de la Convención
        TemaId.ANTIGUAS -> true        // capitales grabadas
    }

    /** Nombre del tema para un título en español, o null si ese tema no lo renombra. */
    private fun nombreNuevo(titulo: String): String? = Temas.nombre(tema, titulo)

    private fun comentarioNuevo(titulo: String, nombre: String, pantalla: Boolean): String? {
        val t = titulo.lowercase()
        val igual = nombre.equals(titulo, true)
        return when (tema) {
            // Romano, terminal y heleno arman su comentario en [pantalla] y [seccion].
            TemaId.ROMANO -> null
            TemaId.CIBER -> null
            TemaId.HELENO -> null
            TemaId.DISTOPIA -> if (pantalla) "EXPEDIENTE Nº %04d · %s".format(folio(titulo, 1000, 9000), t) else if (igual) null else "▪ $t"
            TemaId.LABORATORIO -> if (pantalla) "protocolo VM-%03d · %s".format(folio(titulo, 100, 900), t) else if (igual) null else "· $t"
            TemaId.REFUGIO -> if (igual) null else "[ $t ]"
            TemaId.ORDEN -> if (igual) null else "— $t —"
            TemaId.COLONOS -> if (igual) null else "~ $t ~"
            TemaId.MEDIEVAL -> if (igual) null else "¶ $t"
            TemaId.NAPOLEON -> if (igual) null else "« $t »"
            TemaId.BOTANICO -> if (igual) null else "⁂ $t"
            TemaId.NAVIDAD -> if (igual) null else "✦ $t ✦"
            TemaId.SEDA -> if (igual) null else "◇ $t ◇"
            TemaId.URSS -> if (igual) null else "★ $t"
            TemaId.GOTICO -> if (igual) null else "✠ $t"
            TemaId.ESPACIO -> if (igual) null else "⋆ $t ⋆"
            TemaId.ANDALUS -> if (igual) null else "❋ $t ❋"
            TemaId.MONGOL -> if (igual) null else "◆ $t"
            TemaId.RENACIMIENTO -> if (igual) null else "❧ $t"
            TemaId.REVOLUCION -> if (igual) null else "✶ $t ✶"
            TemaId.ANTIGUAS -> if (igual) null else "⟡ $t"
        }
    }

    /** Título grande y comentario de una pantalla. Sin [latin], el romano busca el suyo en [Romano.latin]. */
    fun pantalla(titulo: String, latin: String?): Pair<String, String?> {
        val la = latin ?: Romano.latin(titulo)
        return when (tema) {
            TemaId.ROMANO -> (la ?: titulo.uppercase()) to (if (la != null) comentario + titulo.lowercase() else null)
            TemaId.CIBER -> (nombreNuevo(titulo) ?: titulo.uppercase()) to "${comentario}sys.${slug(la ?: titulo)}"
            TemaId.HELENO -> Griego.nombre(titulo)?.let { it to comentario + titulo.lowercase() } ?: (titulo.uppercase() to null)
            TemaId.MEDIEVAL -> propio(titulo, true)
            TemaId.DISTOPIA -> propio(titulo, true)
            TemaId.REFUGIO -> propio(titulo, true)
            TemaId.ORDEN -> propio(titulo, true)
            TemaId.LABORATORIO -> propio(titulo, true)
            TemaId.COLONOS -> propio(titulo, true)
            TemaId.NAPOLEON -> propio(titulo, true)
            TemaId.BOTANICO -> propio(titulo, true)
            TemaId.NAVIDAD -> propio(titulo, true)
            TemaId.SEDA -> propio(titulo, true)
            TemaId.URSS -> propio(titulo, true)
            TemaId.GOTICO -> propio(titulo, true)
            TemaId.ESPACIO -> propio(titulo, true)
            TemaId.ANDALUS -> propio(titulo, true)
            TemaId.MONGOL -> propio(titulo, true)
            TemaId.RENACIMIENTO -> propio(titulo, true)
            TemaId.REVOLUCION -> propio(titulo, true)
            TemaId.ANTIGUAS -> propio(titulo, true)
        }
    }

    /** El nombre del diccionario del tema (o el español, en mayúsculas si el tema las usa) y su comentario. */
    private fun propio(titulo: String, pantalla: Boolean): Pair<String, String?> {
        val n = nombreNuevo(titulo) ?: if (mayusculas) titulo.uppercase() else titulo
        return n to comentarioNuevo(titulo, n, pantalla)
    }

    /** ¿El comentario de la pantalla dice algo más que el nombre en español? (el número de expediente, el protocolo). */
    private val comentarioConFolio: Boolean get() = when (tema) {
        TemaId.DISTOPIA -> true
        TemaId.LABORATORIO -> true
        TemaId.ROMANO -> false
        TemaId.CIBER -> false
        TemaId.HELENO -> false
        TemaId.MEDIEVAL -> false
        TemaId.REFUGIO -> false
        TemaId.ORDEN -> false
        TemaId.COLONOS -> false
        TemaId.NAPOLEON -> false
        TemaId.BOTANICO -> false
        TemaId.NAVIDAD -> false
        TemaId.SEDA -> false
        TemaId.URSS -> false
        TemaId.GOTICO -> false
        TemaId.ESPACIO -> false
        TemaId.ANDALUS -> false
        TemaId.MONGOL -> false
        TemaId.RENACIMIENTO -> false
        TemaId.REVOLUCION -> false
        TemaId.ANTIGUAS -> false
    }

    /**
     * El título de una pantalla en una línea (P-1): el nombre del tema y, si el tema lo renombra, el nombre en español
     * aparte, para ponerlos juntos y del mismo tamaño («Quaestiones · Análisis»). El comentario solo queda si dice algo
     * más que el nombre en español (el número de expediente, el protocolo, el «sys.» de la terminal).
     */
    fun pantallaEnLinea(titulo: String, latin: String?): Triple<String, String?, String?> {
        val (n, c) = pantalla(titulo, latin)
        val renombra = slug(n) != slug(titulo)
        val extra = if (renombra && !comentarioConFolio) null else c
        return Triple(n, titulo.takeIf { renombra }, extra)
    }

    /**
     * ¿Se leen las letras de los nombres del tema? El griego, el árabe y el cirílico mongol no se leen sin saberlos: en esos
     * temas, donde el nombre va solo (listas, menús, títulos pequeños), lleva al lado su nombre en español. Ver
     * [pantallaLegible] y [seccionLegible].
     */
    val alfabetoLegible: Boolean get() = when (tema) {
        TemaId.HELENO -> false
        TemaId.ANDALUS -> false
        TemaId.MONGOL -> false
        TemaId.ROMANO -> true
        TemaId.CIBER -> true
        TemaId.MEDIEVAL -> true
        TemaId.DISTOPIA -> true
        TemaId.REFUGIO -> true
        TemaId.ORDEN -> true
        TemaId.LABORATORIO -> true
        TemaId.COLONOS -> true
        TemaId.NAPOLEON -> true
        TemaId.BOTANICO -> true
        TemaId.NAVIDAD -> true
        TemaId.SEDA -> true
        TemaId.URSS -> true
        TemaId.GOTICO -> true
        TemaId.ESPACIO -> true
        TemaId.RENACIMIENTO -> true
        TemaId.REVOLUCION -> true
        TemaId.ANTIGUAS -> true
    }

    private fun legible(nombre: String, titulo: String) = if (alfabetoLegible || slug(nombre) == slug(titulo)) nombre else "$nombre · $titulo"

    /** El nombre de una pantalla para mostrarlo solo, sin su comentario: con el español al lado si no se lee (ver [alfabetoLegible]). */
    fun pantallaLegible(titulo: String, latin: String?): String = legible(pantalla(titulo, latin).first, titulo)

    /** Lo mismo para una sección. */
    fun seccionLegible(titulo: String, latin: String?): String = legible(seccion(titulo, latin).first, titulo)

    /** Título y comentario de una sección. */
    fun seccion(titulo: String, latin: String?): Pair<String, String?> {
        val la = latin ?: Romano.latin(titulo)
        return when (tema) {
            TemaId.ROMANO -> (la ?: titulo.uppercase()) to (if (la != null) comentario + titulo.lowercase() else null)
            TemaId.CIBER -> nombreNuevo(titulo)?.let { "> $it" to comentario + titulo.lowercase() } ?: ("> ${titulo.lowercase()}" to null)
            TemaId.HELENO -> Griego.nombre(titulo)?.let { it to comentario + titulo.lowercase() } ?: (titulo.uppercase() to null)
            TemaId.MEDIEVAL -> propio(titulo, false)
            TemaId.DISTOPIA -> propio(titulo, false)
            TemaId.REFUGIO -> propio(titulo, false)
            TemaId.ORDEN -> propio(titulo, false)
            TemaId.LABORATORIO -> propio(titulo, false)
            TemaId.COLONOS -> propio(titulo, false)
            TemaId.NAPOLEON -> propio(titulo, false)
            TemaId.BOTANICO -> propio(titulo, false)
            TemaId.NAVIDAD -> propio(titulo, false)
            TemaId.SEDA -> propio(titulo, false)
            TemaId.URSS -> propio(titulo, false)
            TemaId.GOTICO -> propio(titulo, false)
            TemaId.ESPACIO -> propio(titulo, false)
            TemaId.ANDALUS -> propio(titulo, false)
            TemaId.MONGOL -> propio(titulo, false)
            TemaId.RENACIMIENTO -> propio(titulo, false)
            TemaId.REVOLUCION -> propio(titulo, false)
            TemaId.ANTIGUAS -> propio(titulo, false)
        }
    }

    fun numeral(n: Int): String = when (tema) {
        TemaId.ROMANO -> Romano.de(n)
        TemaId.CIBER -> if (n < 100) "[%02d]".format(n) else "0x%X".format(n)
        // La cifra con la keraia, la tilde con que los griegos marcaban un número: «17ʹ».
        TemaId.HELENO -> if (n in 1..999) "${n}ʹ" else "$n"
        TemaId.MEDIEVAL -> Temas.copista(n)
        TemaId.DISTOPIA -> if (n < 100) "Nº%02d".format(n) else "Nº$n"
        TemaId.REFUGIO -> if (n < 100) "#%02d".format(n) else "#$n"
        // Ordinal de regimiento y de batallón: «17º».
        TemaId.ORDEN -> if (n in 1..999) "${n}º" else "$n"
        // Muestra numerada del cuaderno: «M-17».
        TemaId.LABORATORIO -> if (n in 1..999) "M-%02d".format(n) else "$n"
        TemaId.COLONOS -> if (n < 100) "§ $n" else "$n"
        // El ordinal francés de las unidades («17ᵉ régiment», «1ᵉʳ corps»); los años, en romanos como en las medallas del Imperio.
        TemaId.NAPOLEON -> when { n == 1 -> "1ᵉʳ"; n in 2..999 -> "${n}ᵉ"; n in 1000..3999 -> Romano.de(n); else -> "$n" }
        // Número de pliego del herbario: «n.º 17».
        TemaId.BOTANICO -> if (n in 1..999) "n.º $n" else "$n"
        // La puertita del calendario de Adviento.
        TemaId.NAVIDAD -> if (n in 1..999) "✦$n" else "$n"
        // Cifras mongolas tradicionales: ᠑᠗.
        // El rombo de los bordados de la yurta delante de la cifra: «◆17».
        TemaId.MONGOL -> if (n in 1..999) "◆$n" else "$n"
        // Los numerales entre puntos de los manuscritos italianos: «.xvii.».
        TemaId.RENACIMIENTO -> if (n in 1..3999) "." + Romano.de(n).lowercase() + "." else "$n"
        // El 第 de los ordinales chinos delante de la cifra: «第17».
        TemaId.SEDA -> if (n in 1..999) "第$n" else "$n"
        TemaId.URSS -> "№ $n"
        TemaId.GOTICO -> if (n in 1..3999) Romano.de(n).lowercase() else "$n"
        TemaId.ESPACIO -> "%03d".format(n)
        TemaId.ANDALUS -> if (n in 1..999) "❋ $n" else "$n"
        TemaId.REVOLUCION -> if (n < 100) "N° $n" else "$n"
        // La cuenta de jornadas y lunas del camino: «17ª».
        TemaId.ANTIGUAS -> if (n in 1..999) "${n}ª" else "$n"
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
        TemaId.HELENO -> runCatching { val d = Fechas.parse(iso); "${numeral(d.dayOfMonth)} ${Griego.MESES_LEGIBLES[d.monthValue - 1]} ${d.year}" }.getOrDefault(iso)
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
        TemaId.ANDALUS -> runCatching { val d = Fechas.parse(iso); "${d.dayOfMonth} de ${MESES_LARGOS[d.monthValue - 1]} de ${d.year} · " + Temas.fechaHegira(d) }.getOrDefault(iso)
        TemaId.MONGOL -> runCatching { val d = Fechas.parse(iso); Temas.anioMongolEs(d) + " · día ${d.dayOfMonth} del ${d.monthValue}.º mes de ${d.year}" }.getOrDefault(iso)
        TemaId.RENACIMIENTO -> runCatching { Temas.fechaItaliana(Fechas.parse(iso)) }.getOrDefault(iso)
        TemaId.NAVIDAD -> runCatching { val d = Fechas.parse(iso); "${d.dayOfMonth} de ${MESES_LARGOS[d.monthValue - 1]} · " + Temas.cuentaAtras(d, 12, 25, "Navidad", "Navidad") }.getOrDefault(iso)
        TemaId.SEDA -> runCatching { val d = Fechas.parse(iso); "año ${d.year} · ${d.monthValue}.ª luna · día ${d.dayOfMonth}" }.getOrDefault(iso)
        TemaId.URSS -> runCatching {
            val d = Fechas.parse(iso)
            "%d.%s.%d".format(d.dayOfMonth, Romano.de(d.monthValue), d.year) + (diasDesdeInicio(iso)?.takeIf { it > 0 }?.let { " · PLAN QUINQUENAL, AÑO ${((it - 1) / 365) % 5 + 1}" } ?: "")
        }.getOrDefault(iso)
    }

    fun mes(n: Int, anio: Int? = null): String {
        val i = (n - 1).coerceIn(0, 11)
        return when (tema) {
            TemaId.ROMANO -> Romano.mes(n)
            TemaId.CIBER -> MESES_ES[i].lowercase() + (anio?.let { ".$it" } ?: "")
            // Los nombres griegos de hoy, que vienen del latín: se leen casi como en español.
            TemaId.HELENO -> Griego.MESES_LEGIBLES[i] + (anio?.let { " $it" } ?: "")
            TemaId.MEDIEVAL -> Temas.MESES_MEDIEVALES[i]
            TemaId.DISTOPIA -> "MES %02d".format(n) + (anio?.let { " · $it" } ?: "")
            TemaId.REFUGIO -> MESES_LARGOS[i].uppercase() + (anio?.let { " $it" } ?: "")
            // Mes y número, como en los partes: «OCT-10».
            TemaId.ORDEN -> MESES_ES[i] + "-%02d".format(n) + (anio?.let { " · $it" } ?: "")
            // Número y abreviatura del cuaderno de laboratorio: «10 · oct», «2026-10 · oct».
            TemaId.LABORATORIO -> (anio?.let { "$it-" } ?: "") + "%02d".format(n) + " · " + MESES_ES[i].lowercase()
            TemaId.COLONOS -> MESES_LARGOS[i].replaceFirstChar { it.uppercase() } + (anio?.let { " de $it" } ?: "")
            TemaId.NAPOLEON -> Temas.MESES_FRANCESES[i].uppercase() + (anio?.let { " $it" } ?: "")
            // Las etiquetas de herbario escriben el mes en romanos: «X-2026».
            TemaId.BOTANICO -> Romano.de(n) + (anio?.let { "-$it" } ?: "")
            TemaId.NAVIDAD -> (if (n == 12) "Mes de la Navidad" else "Mes de " + MESES_LARGOS[i]) + (anio?.let { " de $it" } ?: "")
            // Como en el calendario chino, el mes es la luna con su número.
            TemaId.SEDA -> "${n}.ª luna" + (anio?.let { " · $it" } ?: "")
            // El mes en romanos, como en las fechas soviéticas («17.X.1957»), y su nombre.
            TemaId.URSS -> Romano.de(n) + " · " + MESES_LARGOS[i].uppercase() + (anio?.let { " $it" } ?: "")
            // En genitivo, como en las rúbricas del calendario litúrgico: «Octobris».
            TemaId.GOTICO -> Temas.MESES_LATINOS_GEN[i].replaceFirstChar { it.uppercase() } + (anio?.let { " " + Romano.de(it).lowercase() } ?: "")
            TemaId.ESPACIO -> MESES_ES[i] + (anio?.let { " · $it" } ?: "")
            // Los andalusíes nombraban los meses del sol con palabras salidas del latín (yannayr, fibrayr, mars…): van detrás del nuestro.
            TemaId.ANDALUS -> MESES_LARGOS[i].replaceFirstChar { it.uppercase() } + " · " + MESES_ANDALUSIES[i].lowercase() + (anio?.let { " $it" } ?: "")
            // En mongol los meses se cuentan: «10-р сар», el décimo mes.
            TemaId.MONGOL -> "$n.º mes" + (anio?.let { " · $it" } ?: "")
            TemaId.RENACIMIENTO -> Temas.MESES_ITALIANOS[i].replaceFirstChar { it.uppercase() } + (anio?.let { " $it" } ?: "")
            // El mes republicano que ocupa la mayor parte del mes gregoriano (octubre ≈ vendémiaire).
            TemaId.REVOLUCION -> Temas.mesRepublicano(n).uppercase() + (anio?.let { " " + Temas.anRepublicano(it, n) } ?: "")
            TemaId.ANTIGUAS -> Temas.LUNAS[i] + (anio?.let { " · $it" } ?: "")
        }
    }

    /** Los días de la semana, de lunes a domingo (cortos: van en columnas de calendario). */
    val semana: List<String> get() = when (tema) {
        TemaId.ROMANO -> Romano.SEMANA
        TemaId.CIBER -> listOf("lun", "mar", "mié", "jue", "vie", "sáb", "dom")
        // La letra griega del día (α el primero) y su nombre corto.
        TemaId.HELENO -> listOf("α lu", "β ma", "γ mi", "δ ju", "ε vi", "ζ sá", "η do")
        // Con punto de abreviatura y sin tilde, como en los códices.
        TemaId.MEDIEVAL -> listOf("lun.", "mart.", "mierc.", "iuev.", "vier.", "sab.", "dom.")
        // Las letras de los formularios oficiales (con la X del miércoles).
        TemaId.DISTOPIA -> listOf("L", "M", "X", "J", "V", "S", "D")
        TemaId.REFUGIO -> listOf("LU", "MA", "MI", "JU", "VI", "SA", "DO")
        // Abreviatura de parte militar, solo consonantes.
        TemaId.ORDEN -> listOf("LNS", "MRT", "MCL", "JVS", "VRN", "SBD", "DMG")
        TemaId.LABORATORIO -> listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do")
        // Con punto, como en la gaceta del pueblo.
        TemaId.COLONOS -> listOf("Lu.", "Ma.", "Mi.", "Ju.", "Vi.", "Sá.", "Do.")
        TemaId.NAPOLEON -> listOf("LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM")
        // Los signos de los planetas que Linneo usaba en sus cuadernos: ☽ lunes, ♂ martes, ☿ miércoles…
        TemaId.BOTANICO -> listOf("☽ lu", "♂ ma", "☿ mi", "♃ ju", "♀ vi", "♄ sá", "☉ do")
        TemaId.NAVIDAD -> listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        // Los astros que dan nombre a los días, como en los almanaques de las caravanas.
        TemaId.SEDA -> listOf("Luna", "Marte", "Merc.", "Júp.", "Venus", "Sat.", "Sol")
        TemaId.URSS -> listOf("LUN.", "MAR.", "MIÉ.", "JUE.", "VIE.", "SÁB.", "DOM.")
        // Los días en latín, de donde salen los nuestros: Lunae, Martis, Mercurii…
        TemaId.GOTICO -> listOf("Lun.", "Mart.", "Merc.", "Iov.", "Ven.", "Sabb.", "Dom.")
        TemaId.ESPACIO -> listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
        TemaId.ANDALUS -> listOf("Lun.", "Mar.", "Mié.", "Jue.", "Vie.", "Sáb.", "Dom.")
        // En mongol los días también se cuentan; aquí van con su número y su nombre corto.
        TemaId.MONGOL -> listOf("1·lu", "2·ma", "3·mi", "4·ju", "5·vi", "6·sá", "7·do")
        TemaId.RENACIMIENTO -> listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
        TemaId.REVOLUCION -> listOf("lun.", "mar.", "mer.", "jeu.", "ven.", "sam.", "dim.")
        // Los días del camino, de invención propia: de la Luna, de la Forja, del Viento, del Roble, del Hogar, de la Piedra y del Sol.
        TemaId.ANTIGUAS -> listOf("Luna", "Forja", "Viento", "Roble", "Hogar", "Piedra", "Sol")
    }

    /** Dos o tres letras por día, para el widget del mes. */
    val semanaCorta: List<String> get() = when (tema) {
        TemaId.ROMANO -> semana.map { it.take(2) }
        TemaId.CIBER -> listOf("lu", "ma", "mi", "ju", "vi", "sá", "do")
        TemaId.HELENO -> semana
        TemaId.MEDIEVAL -> listOf("lu.", "ma.", "mi.", "iu.", "vi.", "sa.", "do.")
        TemaId.DISTOPIA -> semana
        TemaId.REFUGIO -> semana
        TemaId.ORDEN -> semana.map { it.take(2) }
        TemaId.LABORATORIO -> semana
        TemaId.COLONOS -> semana
        TemaId.NAPOLEON -> semana.map { it.take(2) }
        TemaId.BOTANICO -> semana.map { it.take(1) }
        TemaId.NAVIDAD -> listOf("L", "M", "M", "J", "V", "S", "D")
        TemaId.SEDA -> semana.map { it.take(3) }
        TemaId.URSS -> semana.map { it.take(2) + "." }
        TemaId.GOTICO -> semana.map { it.take(3) }
        TemaId.ESPACIO -> semana
        TemaId.ANDALUS -> semana
        TemaId.MONGOL -> semana
        TemaId.RENACIMIENTO -> semana.map { it.take(2) }
        TemaId.REVOLUCION -> semana.map { it.take(2) }
        TemaId.ANTIGUAS -> semana.map { it.take(2) }
    }

    /** Marcas del calendario: calendas, nonas e idus (romano); las fiestas que dividían el año (medieval). */
    fun marca(fecha: String): String? = Temas.marca(tema, fecha)

    /** Los días seguidos sellados, bajo el sello del día (n ≥ 2). */
    fun racha(n: Int): String = when (tema) {
        TemaId.ROMANO -> "DIES CONTINVI · ${Romano.de(n)}"; TemaId.CIBER -> "> streak --días $n"; TemaId.HELENO -> "${numeral(n)} días seguidos, sin tregua"
        TemaId.MEDIEVAL -> "$n días seguidos en el libro"; TemaId.DISTOPIA -> "CONTINUIDAD ACREDITADA: $n DÍAS"; TemaId.REFUGIO -> "DÍAS SIN INCIDENTES: $n"
        TemaId.ORDEN -> "$n VIGILIAS SIN FALTA"; TemaId.LABORATORIO -> "serie de $n sesiones"; TemaId.COLONOS -> "$n jornadas seguidas"
        TemaId.NAPOLEON -> "$n DÍAS DE CAMPAÑA"; TemaId.BOTANICO -> "$n días de herbario"; TemaId.NAVIDAD -> "$n noches junto al fuego"
        TemaId.SEDA -> "$n jornadas de caravana"; TemaId.GOTICO -> "$n días de oficio sin falta"; TemaId.ESPACIO -> "T+$n DÍAS EN ÓRBITA"
        TemaId.ANDALUS -> "$n días seguidos · متتالية"; TemaId.MONGOL -> "$n DÍAS SEGUIDOS EN LA ESTEPA"; TemaId.RENACIMIENTO -> "$n giorni di fila"
        TemaId.URSS -> "PLAN: $n DÍAS SEGUIDOS"; TemaId.REVOLUCION -> "$n DÍAS DE VIRTUD"; TemaId.ANTIGUAS -> "$n JORNADAS DE CAMINO"
    }

    val atras: String get() = when (tema) {
        TemaId.ROMANO -> "← cd .."; TemaId.CIBER -> "<< cd .."; TemaId.HELENO -> "← de vuelta"; TemaId.MEDIEVAL -> "← tornar"
        TemaId.DISTOPIA -> "◄ RETROCEDER"; TemaId.REFUGIO -> "< SALIR"; TemaId.ORDEN -> "« REPLEGARSE"; TemaId.LABORATORIO -> "← anterior"; TemaId.COLONOS -> "← pa' atrás"
        TemaId.NAPOLEON -> "← retirada"; TemaId.BOTANICO -> "← regresar"; TemaId.NAVIDAD -> "← volver a casa"; TemaId.SEDA -> "← desandar"; TemaId.URSS -> "◄ ATRÁS"
        TemaId.GOTICO -> "← redire"; TemaId.ESPACIO -> "◄ RETORNO"; TemaId.ANDALUS -> "← regreso"; TemaId.MONGOL -> "◄ VOLVER"; TemaId.RENACIMIENTO -> "← indietro"; TemaId.REVOLUCION -> "← retour"; TemaId.ANTIGUAS -> "← volver al camino"
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

    /** Un texto corto del tema (ver [Textos]); si faltara, la clave misma. */
    fun texto(clave: String): String = Textos.de(tema, clave) ?: clave

    /** Rótulos de los widgets. */
    fun widget(clave: String): String = Temas.widget(tema, clave)

    /**
     * Claves en latín de las líneas «clave = valor» (Día, widget «hoy»), en la jerga del tema pero siempre legibles: latín
     * en el romano, latín litúrgico en el gótico, castellano antiguo en el scriptorium, inglés en la terminal, italiano en el
     * Renacimiento y el español de cada oficio en los demás. Ver [Claves].
     */
    fun clave(c: String): String {
        val sangria = c.takeWhile { it == ' ' }
        val k = c.trim()
        fun es(jerga: Map<String, String>) = jerga[k] ?: CLAVES_ES[k]
        val v = when (tema) {
            TemaId.ROMANO -> k
            TemaId.CIBER -> CLAVES_CIBER[k]
            TemaId.HELENO -> es(Claves.HELENO)
            TemaId.MEDIEVAL -> Claves.CASTELLANO_ANTIGUO[k]
            TemaId.DISTOPIA -> es(Claves.DISTOPIA)?.uppercase()
            TemaId.REFUGIO -> es(Claves.REFUGIO)
            TemaId.ORDEN -> es(Claves.ORDEN)?.uppercase()
            TemaId.LABORATORIO -> es(Claves.LABORATORIO)
            TemaId.COLONOS -> es(Claves.COLONOS)
            TemaId.NAPOLEON -> es(Claves.NAPOLEON)
            TemaId.BOTANICO -> es(Claves.BOTANICO)
            TemaId.NAVIDAD -> es(Claves.NAVIDAD)
            TemaId.SEDA -> es(Claves.SEDA)
            TemaId.URSS -> es(Claves.URSS)?.uppercase()
            TemaId.GOTICO -> Claves.GOTICO[k] ?: k
            TemaId.ESPACIO -> es(Claves.ESPACIO)?.uppercase()
            TemaId.ANDALUS -> es(Claves.ANDALUS)
            TemaId.MONGOL -> es(Claves.MONGOL)?.uppercase()
            TemaId.RENACIMIENTO -> CLAVES_IT[k]
            TemaId.REVOLUCION -> es(Claves.REVOLUCION)?.uppercase()
            TemaId.ANTIGUAS -> es(Claves.ANTIGUAS)?.uppercase()
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

/** Griego: nombres de secciones (con su español al lado, ver [Estilo.alfabetoLegible]) y los meses. */
object Griego {
    /**
     * Los meses en el griego de hoy, escritos con nuestras letras: vienen del latín y se leen casi como en español.
     */
    val MESES_LEGIBLES = listOf("Ianouários", "Fevrouários", "Mártios", "Aprílios", "Máios", "Ioúnios", "Ioúlios", "Ávgoustos", "Septémvrios", "Októvrios", "Noémvrios", "Dekémvrios")

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
    fun nombre(titulo: String): String? = io.github.ruddy2003.vestigium.ui.nombres.NombresCompletos.de(TemaId.HELENO, titulo) ?: NOMBRES[titulo] ?: NombresNuevos.de(TemaId.HELENO, titulo)
}
