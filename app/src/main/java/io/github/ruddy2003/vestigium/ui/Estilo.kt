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
        TemaId.NEON -> "⌁ "
        TemaId.HELENO -> "∴ "
        TemaId.EGIPTO -> "☥ "
        TemaId.MEDIEVAL -> "¶ "
        TemaId.TEMPLARIO -> "☩ "
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
        TemaId.ABISMO -> "≈ "
        TemaId.ANDALUS -> "❋ "
        TemaId.MONGOL -> "◆ "
        TemaId.RENACIMIENTO -> "❧ "
        TemaId.PIRATA -> "✷ "
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
        TemaId.NEON -> true           // letreros
        TemaId.HELENO -> true          // capitales epigráficas
        TemaId.EGIPTO -> false         // hierático: la letra de corrido de los escribas
        TemaId.MEDIEVAL -> false       // gótica de libro
        TemaId.TEMPLARIO -> false       // letra de estandarte, en minúsculas
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
        TemaId.ABISMO -> true          // rótulos estrechos de instrumento
        TemaId.ANDALUS -> true         // cúfica de friso
        TemaId.MONGOL -> true          // cirílico de rótulo
        TemaId.RENACIMIENTO -> false   // cancilleresca
        TemaId.PIRATA -> false         // letra negra de carta de marear
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
            TemaId.NEON -> if (igual) null else "⌁ $t"
            TemaId.HELENO -> null
            TemaId.EGIPTO -> if (igual) null else "☥ $t"
            TemaId.DISTOPIA -> if (pantalla) "EXPEDIENTE Nº %04d · %s".format(folio(titulo, 1000, 9000), t) else if (igual) null else "▪ $t"
            TemaId.LABORATORIO -> if (pantalla) "protocolo VM-%03d · %s".format(folio(titulo, 100, 900), t) else if (igual) null else "· $t"
            TemaId.REFUGIO -> if (igual) null else "[ $t ]"
            TemaId.ORDEN -> if (igual) null else "— $t —"
            TemaId.COLONOS -> if (igual) null else "~ $t ~"
            TemaId.MEDIEVAL -> if (igual) null else "¶ $t"
            // Cada pantalla es un artículo de la regla (la de la orden llegó a 686 artículos): «☩ art. ·CCXII· de la regla».
            TemaId.TEMPLARIO -> if (pantalla) "☩ art. %s de la regla · %s".format(Temas.puntos(folio(titulo, 1, 686)), t) else if (igual) null else "☩ $t"
            TemaId.NAPOLEON -> if (igual) null else "« $t »"
            TemaId.BOTANICO -> if (igual) null else "⁂ $t"
            TemaId.NAVIDAD -> if (igual) null else "✦ $t ✦"
            TemaId.SEDA -> if (igual) null else "◇ $t ◇"
            TemaId.URSS -> if (igual) null else "★ $t"
            TemaId.GOTICO -> if (igual) null else "✠ $t"
            TemaId.ESPACIO -> if (igual) null else "⋆ $t ⋆"
            // Cada pantalla tiene su cota fija, como si estuviera a esa profundidad: «cota 3,412 m · hoy».
            TemaId.ABISMO -> if (pantalla) String.format(java.util.Locale.US, "cota %,d m · %s", folio(titulo, 10, 10900), t) else if (igual) null else "≈ $t"
            TemaId.ANDALUS -> if (igual) null else "❋ $t ❋"
            TemaId.MONGOL -> if (igual) null else "◆ $t"
            TemaId.RENACIMIENTO -> if (igual) null else "❧ $t"
            TemaId.PIRATA -> if (igual) null else "✷ $t"
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
            TemaId.NEON -> propio(titulo, true)
            TemaId.HELENO -> Griego.nombre(titulo)?.let { it to comentario + titulo.lowercase() } ?: (titulo.uppercase() to null)
            TemaId.EGIPTO -> propio(titulo, true)
            TemaId.MEDIEVAL -> propio(titulo, true)
            TemaId.TEMPLARIO -> propio(titulo, true)
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
            TemaId.ABISMO -> propio(titulo, true)
            TemaId.ANDALUS -> propio(titulo, true)
            TemaId.MONGOL -> propio(titulo, true)
            TemaId.RENACIMIENTO -> propio(titulo, true)
            TemaId.PIRATA -> propio(titulo, true)
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
        TemaId.NEON -> false
        TemaId.HELENO -> false
        TemaId.EGIPTO -> false
        TemaId.MEDIEVAL -> false
        TemaId.TEMPLARIO -> true
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
        TemaId.ABISMO -> true
        TemaId.ANDALUS -> false
        TemaId.MONGOL -> false
        TemaId.RENACIMIENTO -> false
        TemaId.PIRATA -> false
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

    /** Título y comentario de una sección. */
    fun seccion(titulo: String, latin: String?): Pair<String, String?> {
        val la = latin ?: Romano.latin(titulo)
        return when (tema) {
            TemaId.ROMANO -> (la ?: titulo.uppercase()) to (if (la != null) comentario + titulo.lowercase() else null)
            TemaId.CIBER -> nombreNuevo(titulo)?.let { "> $it" to comentario + titulo.lowercase() } ?: ("> ${titulo.lowercase()}" to null)
            TemaId.NEON -> propio(titulo, false)
            TemaId.HELENO -> Griego.nombre(titulo)?.let { it to comentario + titulo.lowercase() } ?: (titulo.uppercase() to null)
            TemaId.EGIPTO -> propio(titulo, false)
            TemaId.MEDIEVAL -> propio(titulo, false)
            TemaId.TEMPLARIO -> propio(titulo, false)
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
            TemaId.ABISMO -> propio(titulo, false)
            TemaId.ANDALUS -> propio(titulo, false)
            TemaId.MONGOL -> propio(titulo, false)
            TemaId.RENACIMIENTO -> propio(titulo, false)
            TemaId.PIRATA -> propio(titulo, false)
            TemaId.REVOLUCION -> propio(titulo, false)
            TemaId.ANTIGUAS -> propio(titulo, false)
        }
    }

    fun numeral(n: Int): String = when (tema) {
        TemaId.ROMANO -> Romano.de(n)
        TemaId.CIBER -> if (n < 100) "[%02d]".format(n) else "0x%X".format(n)
        TemaId.NEON -> n.toString().map { if (it.isDigit()) '０' + (it - '0') else it }.joinToString("")   // cifras de ancho completo, como en los letreros
        TemaId.HELENO -> Griego.numeral(n)
        // Numerales jeroglíficos: trazos, asas, rollos de cuerda, flores de loto y dedos (ver [Egipcio.numeral]).
        TemaId.EGIPTO -> Egipcio.numeral(n)
        TemaId.MEDIEVAL -> Temas.copista(n)
        // Romanos en mayúscula entre puntos, como en las cartas del siglo XII: «·XVII·».
        TemaId.TEMPLARIO -> Temas.puntos(n)
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
        TemaId.MONGOL -> Temas.mongol(n)
        // Los numerales entre puntos de los manuscritos italianos: «.xvii.».
        TemaId.RENACIMIENTO -> if (n in 1..3999) "." + Romano.de(n).lowercase() + "." else "$n"
        // El folio de la bitácora: «f.º 17». Los años, tal cual.
        TemaId.PIRATA -> if (n in 1..999) "f.º $n" else "$n"
        TemaId.SEDA -> Temas.chino(n)
        TemaId.URSS -> "№ $n"
        TemaId.GOTICO -> if (n in 1..3999) Romano.de(n).lowercase() else "$n"
        TemaId.ESPACIO -> "%03d".format(n)
        // La flecha que baja: cada número es un tramo más hondo, «▾17».
        TemaId.ABISMO -> if (n in 1..9999) "▾$n" else "$n"
        TemaId.ANDALUS -> Temas.arabe(n)
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
        TemaId.NEON -> runCatching { val d = Fechas.parse(iso); "%s %02d·%02d·%d · 区 %d".format(SEMANA_ES[d.dayOfWeek.value - 1], d.dayOfMonth, d.monthValue, d.year, d.dayOfYear % 9 + 1) }.getOrDefault(iso)   // el día y el distrito (区) donde cae
        TemaId.HELENO -> Griego.fecha(iso)
        TemaId.EGIPTO -> Egipcio.fecha(iso)
        TemaId.MEDIEVAL -> runCatching { val d = Fechas.parse(iso); "${Temas.copista(d.dayOfMonth)} de ${Temas.MESES_MEDIEVALES[d.monthValue - 1]} de ${Temas.copista(d.year)}" }.getOrDefault(iso)
        // En el calendario juliano (13 días por detrás) y, de noche, con su vigilia: «4 de octubre juliano · A. D. ·MMXXVI· · tercera vigilia».
        TemaId.TEMPLARIO -> runCatching {
            val (a, m, d) = Temas.juliano(Fechas.parse(iso))
            "$d de ${MESES_LARGOS[m - 1]} juliano · A. D. ${Temas.puntos(a)}" +
                (if (iso == Fechas.diaLogico()) Temas.vigilia(java.time.LocalTime.now().hour)?.let { " · $it" } ?: "" else "")
        }.getOrDefault(iso)
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
        // «INMERSIÓN 290 · 17 OCT 2026»: cada día desde el primer registro es una inmersión más.
        TemaId.ABISMO -> runCatching {
            val d = Fechas.parse(iso)
            val base = "%d %s %d".format(d.dayOfMonth, MESES_ES[d.monthValue - 1], d.year)
            diasDesdeInicio(iso)?.takeIf { it > 0 }?.let { "INMERSIÓN $it · $base" } ?: base
        }.getOrDefault(iso)
        TemaId.ANDALUS -> runCatching { val d = Fechas.parse(iso); Temas.fechaHegira(d) + " · ${d.dayOfMonth} ${MESES_ANDALUSIES[d.monthValue - 1].lowercase()}" }.getOrDefault(iso)
        TemaId.MONGOL -> runCatching { val d = Fechas.parse(iso); Temas.anioMongol(d) + " · " + Temas.fechaMongol(d) }.getOrDefault(iso)
        TemaId.RENACIMIENTO -> runCatching { Temas.fechaItaliana(Fechas.parse(iso)) }.getOrDefault(iso)
        // «Singladura 290 · 17 de 8bre de 2026», contada desde el primer registro (o, sin registros, desde el 1 de enero);
        // hoy, además, la hora en campanadas de guardia.
        TemaId.PIRATA -> runCatching {
            val d = Fechas.parse(iso)
            Temas.fechaPirata(d, diasDesdeInicio(iso)?.takeIf { it > 0 } ?: d.dayOfYear.toLong()) +
                if (iso == Fechas.diaLogico()) java.time.LocalTime.now().let { " · " + Temas.horaPirata(it.hour, it.minute) } else ""
        }.getOrDefault(iso)
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
            TemaId.CIBER -> MESES_ES[i].lowercase() + (anio?.let { ".$it" } ?: "")
            TemaId.NEON -> "${i + 1}月 " + MESES_ES[i].uppercase() + (anio?.let { " $it" } ?: "")
            TemaId.HELENO -> Griego.MESES[i]
            // El mes del año civil que ocupa la mayor parte del mes gregoriano, con su estación: «Paofi (II Ajet)».
            TemaId.EGIPTO -> Egipcio.mes(n, anio)
            TemaId.MEDIEVAL -> Temas.MESES_MEDIEVALES[i]
            TemaId.TEMPLARIO -> MESES_LARGOS[i] + " juliano" + (anio?.let { " · " + Temas.puntos(it) } ?: "")
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
            TemaId.SEDA -> (anio?.let { Temas.anioChino(it) + " " } ?: "") + Temas.chino(n) + "月"
            TemaId.URSS -> Temas.MESES_RUSOS[i] + (anio?.let { " $it" } ?: "")
            // En genitivo, como en las rúbricas del calendario litúrgico: «Octobris».
            TemaId.GOTICO -> Temas.MESES_LATINOS_GEN[i].replaceFirstChar { it.uppercase() } + (anio?.let { " " + Romano.de(it).lowercase() } ?: "")
            TemaId.ESPACIO -> MESES_ES[i] + (anio?.let { " · $it" } ?: "")
            // El mes con la flecha de bajada: «▾ OCTUBRE · 2026».
            TemaId.ABISMO -> "▾ " + MESES_LARGOS[i].uppercase() + (anio?.let { " · $it" } ?: "")
            // Los andalusíes nombraban los meses del sol con palabras salidas del latín: yannayr, fibrayr, mars…
            TemaId.ANDALUS -> MESES_ANDALUSIES[i] + (anio?.let { " $it" } ?: "")
            // Los meses de la estepa se nombran por la estación: primero, medio y último mes del otoño…
            TemaId.MONGOL -> Temas.MESES_MONGOLES[i] + (anio?.let { " · $it" } ?: "")
            TemaId.RENACIMIENTO -> Temas.MESES_ITALIANOS[i].replaceFirstChar { it.uppercase() } + (anio?.let { " $it" } ?: "")
            // Como en los cuadernos de bitácora del XVIII: de septiembre a diciembre, «7bre», «8bre», «9bre», «Xbre».
            TemaId.PIRATA -> Temas.MESES_PIRATAS[i] + (anio?.let { " del año $it" } ?: "")
            // El mes republicano que ocupa la mayor parte del mes gregoriano (octubre ≈ vendémiaire).
            TemaId.REVOLUCION -> Temas.mesRepublicano(n).uppercase() + (anio?.let { " " + Temas.anRepublicano(it, n) } ?: "")
            TemaId.ANTIGUAS -> Temas.LUNAS[i] + (anio?.let { " · $it" } ?: "")
        }
    }

    /** Los días de la semana, de lunes a domingo (cortos: van en columnas de calendario). */
    val semana: List<String> get() = when (tema) {
        TemaId.ROMANO -> Romano.SEMANA
        TemaId.CIBER -> listOf("lun", "mar", "mié", "jue", "vie", "sáb", "dom")
        TemaId.NEON -> listOf("LUN·月", "MAR·火", "MIÉ·水", "JUE·木", "VIE·金", "SÁB·土", "DOM·日")   // letreros en dos alfabetos
        TemaId.HELENO -> Griego.SEMANA
        TemaId.EGIPTO -> Egipcio.SEMANA
        // Los monjes contaban «ferias»: el lunes es la segunda, el martes la tercera…
        TemaId.MEDIEVAL -> listOf("ij", "iij", "iiij", "v", "vj", "Sab", "Dom")
        // Los días en el francés antiguo de la Regla: lunsdi, marsdi, mecresdi, juesdi, venresdi, samedi, diemenche.
        TemaId.TEMPLARIO -> listOf("Luns", "Mars", "Mecr", "Jues", "Venr", "Same", "Diem")
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
        TemaId.SEDA -> listOf("一", "二", "三", "四", "五", "六", "日")
        TemaId.URSS -> listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")
        // Las ferias de la liturgia, en romanos: feria II es el lunes.
        TemaId.GOTICO -> listOf("F.II", "F.III", "F.IV", "F.V", "F.VI", "Sab.", "Dom.")
        TemaId.ESPACIO -> listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
        // Abreviados con punto, como en las casillas de la bitácora de inmersiones.
        TemaId.ABISMO -> listOf("LUN.", "MAR.", "MIÉ.", "JUE.", "VIE.", "SÁB.", "DOM.")
        TemaId.ANDALUS -> listOf("اثن", "ثلا", "أرب", "خمي", "جمع", "سبت", "أحد")
        TemaId.MONGOL -> listOf("ДА", "МЯ", "ЛХ", "ПҮ", "БА", "БЯ", "НЯ")
        TemaId.RENACIMIENTO -> listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
        // Abreviados como en las bitácoras de entonces.
        TemaId.PIRATA -> listOf("Lun.", "Mart.", "Miérc.", "Juev.", "Vier.", "Sáb.", "Dom.")
        TemaId.REVOLUCION -> listOf("lun.", "mar.", "mer.", "jeu.", "ven.", "sam.", "dim.")
        // Los días del camino, de invención propia: de la Luna, de la Forja, del Viento, del Roble, del Hogar, de la Piedra y del Sol.
        TemaId.ANTIGUAS -> listOf("Luna", "Forja", "Viento", "Roble", "Hogar", "Piedra", "Sol")
    }

    /** Dos o tres letras por día, para el widget del mes. */
    val semanaCorta: List<String> get() = when (tema) {
        TemaId.ROMANO -> semana.map { it.take(2) }
        TemaId.CIBER -> listOf("lu", "ma", "mi", "ju", "vi", "sá", "do")
        TemaId.NEON -> listOf("월", "화", "수", "목", "금", "토", "일")   // los letreros coreanos del barrio
        TemaId.HELENO -> semana.map { it.take(2) }
        TemaId.EGIPTO -> listOf("Ia", "De", "Se", "We", "Du", "Ka", "Ra")
        TemaId.MEDIEVAL -> listOf("ij", "iij", "iv", "v", "vj", "Sb", "Dm")
        TemaId.TEMPLARIO -> listOf("Lu", "Ma", "Me", "Ju", "Ve", "Sa", "Di")
        TemaId.DISTOPIA -> semana
        TemaId.REFUGIO -> semana
        TemaId.ORDEN -> semana.map { it.take(2) }
        TemaId.LABORATORIO -> semana
        TemaId.COLONOS -> semana
        TemaId.NAPOLEON -> semana.map { it.take(2) }
        TemaId.BOTANICO -> semana.map { it.take(1) }
        TemaId.NAVIDAD -> listOf("L", "M", "M", "J", "V", "S", "D")
        TemaId.SEDA -> semana
        TemaId.URSS -> semana
        TemaId.GOTICO -> listOf("II", "III", "IV", "V", "VI", "SB", "DM")
        TemaId.ESPACIO -> semana
        TemaId.ABISMO -> listOf("L.", "M.", "X.", "J.", "V.", "S.", "D.")
        TemaId.ANDALUS -> semana.map { it.take(2) }
        TemaId.MONGOL -> semana
        TemaId.RENACIMIENTO -> semana.map { it.take(2) }
        TemaId.PIRATA -> listOf("Lu", "Mt", "Mc", "Ju", "Vi", "Sb", "Do")
        TemaId.REVOLUCION -> semana.map { it.take(2) }
        TemaId.ANTIGUAS -> semana.map { it.take(2) }
    }

    /** Marcas del calendario: calendas, nonas e idus (romano); las fiestas que dividían el año (medieval). */
    fun marca(fecha: String): String? = Temas.marca(tema, fecha)

    /** Los días seguidos sellados, bajo el sello del día (n ≥ 2). */
    fun racha(n: Int): String = when (tema) {
        TemaId.ROMANO -> "DIES CONTINVI · ${Romano.de(n)}"
        TemaId.CIBER -> "> streak --días $n"
        TemaId.NEON -> "$n NOCHES SEGUIDAS EN LA CALLE"
        TemaId.HELENO -> "ΣΥΝΕΧΕΙΣ ΗΜΕΡΑΙ · $n"
        TemaId.EGIPTO -> "$n días seguidos ante el cálamo"
        TemaId.MEDIEVAL -> "$n días seguidos en el libro"
        TemaId.TEMPLARIO -> "$n jornadas de guardia en la muralla"
        TemaId.DISTOPIA -> "CONTINUIDAD ACREDITADA: $n DÍAS"
        TemaId.REFUGIO -> "DÍAS SIN INCIDENTES: $n"
        TemaId.ORDEN -> "$n VIGILIAS SIN FALTA"
        TemaId.LABORATORIO -> "serie de $n sesiones"
        TemaId.COLONOS -> "$n jornadas seguidas"
        TemaId.NAPOLEON -> "$n JOURS DE CAMPAGNE"
        TemaId.BOTANICO -> "$n días de herbario"
        TemaId.NAVIDAD -> "$n noches junto al fuego"
        TemaId.SEDA -> "$n jornadas de caravana"
        TemaId.GOTICO -> "$n días de oficio sin falta"
        TemaId.ESPACIO -> "T+$n DÍAS EN ÓRBITA"
        // Cada día seguido baja diez metros: la presión sube un bar por cada diez metros, más el de la superficie.
        TemaId.ABISMO -> String.format(java.util.Locale.US, "COTA −%,d m · %d bar", n * 10, n + 1)
        TemaId.ANDALUS -> "$n días seguidos · متتالية"
        TemaId.MONGOL -> "$n ӨДӨР ДАРААЛАН"
        TemaId.RENACIMIENTO -> "$n giorni di fila"
        TemaId.PIRATA -> "$n singladuras sin tocar puerto"
        TemaId.URSS -> "ПЛАН: $n ДНЕЙ ПОДРЯД"
        TemaId.REVOLUCION -> "$n JOURS DE VERTU"
        TemaId.ANTIGUAS -> "$n JORNADAS DE CAMINO"
    }

    val atras: String get() = when (tema) {
        TemaId.ROMANO -> "← cd .."
        TemaId.CIBER -> "<< cd .."
        TemaId.NEON -> "◂ al callejón"
        TemaId.HELENO -> "← ὀπίσω"
        TemaId.EGIPTO -> "← río abajo"
        TemaId.MEDIEVAL -> "← tornar"
        TemaId.TEMPLARIO -> "← volver grupas"
        TemaId.DISTOPIA -> "◄ RETROCEDER"
        TemaId.REFUGIO -> "< SALIR"
        TemaId.ORDEN -> "« REPLEGARSE"
        TemaId.LABORATORIO -> "← anterior"
        TemaId.COLONOS -> "← pa' atrás"
        TemaId.NAPOLEON -> "← retirada"
        TemaId.BOTANICO -> "← regresar"
        TemaId.NAVIDAD -> "← volver a casa"
        TemaId.SEDA -> "← desandar"
        TemaId.URSS -> "◄ НАЗАД"
        TemaId.GOTICO -> "← redire"
        TemaId.ESPACIO -> "◄ RETORNO"
        TemaId.ABISMO -> "▲ EMERGER"
        TemaId.ANDALUS -> "← رجوع"
        TemaId.MONGOL -> "◄ БУЦАХ"
        TemaId.RENACIMIENTO -> "← indietro"
        TemaId.PIRATA -> "← virar de bordo"
        TemaId.REVOLUCION -> "← retour"
        TemaId.ANTIGUAS -> "← volver al camino"
    }
    val prefijo: String get() = when (tema) {
        TemaId.ROMANO -> "› "
        TemaId.CIBER -> "$ "
        TemaId.NEON -> "⏵ "
        TemaId.HELENO -> "· "
        TemaId.EGIPTO -> "△ "
        TemaId.MEDIEVAL -> "¶ "
        TemaId.TEMPLARIO -> "✚ "
        TemaId.DISTOPIA -> "▪ "
        TemaId.REFUGIO -> "> "
        TemaId.ORDEN -> "▸ "
        TemaId.LABORATORIO -> "○ "
        TemaId.COLONOS -> "☞ "
        TemaId.NAPOLEON -> "» "
        TemaId.BOTANICO -> "⁂ "
        TemaId.NAVIDAD -> "✦ "
        TemaId.SEDA -> "◇ "
        TemaId.URSS -> "▶ "
        TemaId.GOTICO -> "✠ "
        TemaId.ESPACIO -> "◦ "
        TemaId.ABISMO -> "↓ "
        TemaId.ANDALUS -> "◈ "
        TemaId.MONGOL -> "▹ "
        TemaId.RENACIMIENTO -> "✎ "
        TemaId.PIRATA -> "➳ "
        TemaId.REVOLUCION -> "• "
        TemaId.ANTIGUAS -> "◆ "
    }

    /**
     * Lo que remata el título de Día y de Hoy, pegado al nombre de la pantalla. Casi todos los temas no llevan nada;
     * la terminal deja su cursor.
     */
    val sufijoTitulo: String get() = when (tema) {
        TemaId.ROMANO -> ""
        TemaId.CIBER -> "_"
        TemaId.NEON -> ""
        TemaId.HELENO -> ""
        TemaId.EGIPTO -> ""
        TemaId.MEDIEVAL -> ""
        TemaId.TEMPLARIO -> ""
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
        TemaId.ABISMO -> " ▾"                 // la flecha de bajada
        TemaId.ANDALUS -> ""
        TemaId.MONGOL -> ""
        TemaId.RENACIMIENTO -> ""
        TemaId.PIRATA -> ""
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
        TemaId.NEON -> EstiloFecha(1.12f, 2.2f, peso = 600, abre = "▼")
        TemaId.HELENO -> EstiloFecha(1.12f, 3.0f, peso = 500, abre = "▿")
        TemaId.EGIPTO -> EstiloFecha(1.1f, 0.8f, peso = 700, abre = "▽")
        TemaId.MEDIEVAL -> EstiloFecha(1.1f, 0.3f)
        TemaId.TEMPLARIO -> EstiloFecha(1.08f, 1.0f, abre = "☩")
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
        TemaId.ABISMO -> EstiloFecha(1.12f, 1.2f, peso = 600, abre = "⤓")
        TemaId.ANDALUS -> EstiloFecha(1.06f, 1.0f, peso = 500)
        TemaId.MONGOL -> EstiloFecha(1.1f, 1.8f, peso = 700, abre = "▼")
        TemaId.RENACIMIENTO -> EstiloFecha(1.14f, 0f)
        TemaId.PIRATA -> EstiloFecha(1.06f, 0.9f, abre = "⇣")
        TemaId.URSS -> EstiloFecha(1.18f, 1.2f, abre = "▼")
        TemaId.REVOLUCION -> EstiloFecha(1.08f, 2.0f, cursiva = true)
        TemaId.ANTIGUAS -> EstiloFecha(1.1f, 2.6f, peso = 600)
    }

    /** Un texto corto del tema (ver [Textos]); si faltara, la clave misma. */
    fun texto(clave: String): String = Textos.de(tema, clave) ?: clave

    /** Rótulos de los widgets. */
    fun widget(clave: String): String = Temas.widget(tema, clave)

    /**
     * Claves en latín de las líneas «clave = valor» (Día, widget «hoy»), en la lengua o la jerga del tema: latín en el
     * romano, latín litúrgico en el gótico, castellano antiguo en el scriptorium, francés en el Imperio y la Revolución,
     * chino, árabe, ruso y mongol en sus temas, y el español de cada oficio en los demás. Ver [Claves].
     */
    fun clave(c: String): String {
        val sangria = c.takeWhile { it == ' ' }
        val k = c.trim()
        fun es(jerga: Map<String, String>) = jerga[k] ?: CLAVES_ES[k]
        val v = when (tema) {
            TemaId.ROMANO -> k
            TemaId.CIBER -> CLAVES_CIBER[k]
            TemaId.NEON -> es(CLAVES_NEON)
            TemaId.HELENO -> CLAVES_GRIEGO[k]
            TemaId.EGIPTO -> es(CLAVES_EGIPTO)
            TemaId.MEDIEVAL -> Claves.CASTELLANO_ANTIGUO[k]
            TemaId.TEMPLARIO -> Claves.ANCIEN_FRANCES[k]
            TemaId.DISTOPIA -> es(Claves.DISTOPIA)?.uppercase()
            TemaId.REFUGIO -> es(Claves.REFUGIO)
            TemaId.ORDEN -> es(Claves.ORDEN)?.uppercase()
            TemaId.LABORATORIO -> es(Claves.LABORATORIO)
            TemaId.COLONOS -> es(Claves.COLONOS)
            TemaId.NAPOLEON -> Claves.NAPOLEON[k] ?: Claves.FRANCES[k]
            TemaId.BOTANICO -> es(Claves.BOTANICO)
            TemaId.NAVIDAD -> es(Claves.NAVIDAD)
            TemaId.SEDA -> Claves.CHINO[k]
            TemaId.URSS -> Claves.RUSO[k]
            TemaId.GOTICO -> Claves.GOTICO[k] ?: k
            TemaId.ESPACIO -> es(Claves.ESPACIO)?.uppercase()
            TemaId.ABISMO -> es(Claves.ABISMO)?.uppercase()
            TemaId.ANDALUS -> Claves.ARABE[k]
            TemaId.MONGOL -> Claves.MONGOL[k]
            TemaId.RENACIMIENTO -> CLAVES_IT[k]
            TemaId.PIRATA -> es(Claves.PIRATA)
            TemaId.REVOLUCION -> (Claves.REVOLUCION[k] ?: Claves.FRANCES[k])?.uppercase()
            TemaId.ANTIGUAS -> es(Claves.ANTIGUAS)?.uppercase()
        }
        return sangria + (v ?: k)
    }

    /** Letras que no se leen sin saber la lengua: griego, cirílico, árabe, mongol, chino, japonés y coreano. */
    private val AJENAS = Regex("[\\u0370-\\u03FF\\u1F00-\\u1FFF\\u0400-\\u052F\\u0600-\\u06FF\\u1800-\\u18AF\\u3000-\\u303F\\u3040-\\u30FF\\u4E00-\\u9FFF\\uAC00-\\uD7AF]")

    /** Si [t] lleva letras de un alfabeto que no se lee sin saber la lengua (griego, cirílico, árabe, mongol, chino). */
    fun ajeno(t: String): Boolean = AJENAS.containsMatchIn(t)

    /**
     * Para el reporte del día: el español de la clave cuando la del tema va en otro alfabeto («ὕπνος» → «sueño»). Si la
     * del tema ya se lee (latín, italiano, francés, jerga en español), nada: la palabra del tema se queda y solo se
     * complementa donde haría falta saber la lengua.
     */
    fun claveEspanol(c: String): String? {
        val k = c.trim()
        return if (ajeno(clave(k))) (CLAVES_ES[k] ?: k).replace('_', ' ') else null
    }

    /** [t] y, si va en otro alfabeto, su español al lado: «Ἡμέρα · Llenar el día». */
    fun conEspanol(t: String, espanol: String): String = if (ajeno(t)) "$t · $espanol" else t

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

    /** La jerga de la calle de Ciudad neón: lo que no renombra queda en español. */
    private val CLAVES_NEON = mapOf(
        "somnus" to "catre", "animus" to "vibra", "svmptvs" to "créditos", "personae" to "banda", "palmae" to "fichas",
        "epistulae" to "recados", "consuetudo" to "rutina", "caelum" to "smog", "lux" to "luz_de_arriba", "loca" to "esquinas",
        "cibus" to "puestos", "momenta" to "chismes", "imagines" to "capturas", "musica" to "beats", "orbis" to "otras_ciudades",
        "passus" to "cuadras", "cor" to "pulso", "iter" to "bajada", "telephonum" to "pantallita", "telephon" to "pantallita",
        "aer" to "vapor", "strepitus" to "barullo", "lumen" to "neón", "series" to "noches_seguidas", "reditus" to "lana",
        "dies" to "noches", "dias" to "noches", "horologium" to "reloj_de_pulso", "robur" to "dojo", "voces" to "llamadas",
        "nutritio" to "lo_que_comí", "optimus" to "la_mejor", "memorabilia" to "hitos", "ascensvs" to "escaleras",
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
    /** Las claves del día en la jerga de la oficina del escriba: el corazón, la estera, la paleta, el pan y la cerveza. */
    private val CLAVES_EGIPTO = mapOf(
        "horologium" to "clepsidra", "somnus" to "sueño_en_la_estera", "stadia" to "horas_de_la_noche", "cor" to "latido", "passus" to "pasos", "motus" to "andar",
        "iter" to "camino", "exercitatio" to "trabajo_del_cuerpo", "loca" to "orillas", "telephonum" to "paleta", "telephon" to "paleta", "usus" to "uso_de_la_paleta",
        "monita" to "heraldos", "musica" to "arpa", "imagines" to "relieves", "fasti" to "rollo_de_los_días", "caelum" to "cielo_de_Nut", "lux" to "luz_de_Ra",
        "aer" to "aire", "corpus" to "cuerpo", "nutritio" to "pan_y_cerveza", "cibus" to "comidas", "robur" to "fuerza", "musculi" to "miembros",
        "voces" to "mensajeros", "soni" to "sonidos", "retia" to "canales", "lumen" to "lámpara", "vis" to "carga", "lectio" to "lectura",
        "strepitus" to "ruido", "luna" to "luna_de_Jonsu", "dias" to "días", "animus" to "corazón", "optimus" to "lo_mejor", "momenta" to "sucesos",
        "svmptvs" to "raciones", "memorabilia" to "estelas", "ascensvs" to "subida",
        "dies" to "días", "quaestiones" to "preguntas", "celeritas" to "reflejo", "bibliotheca" to "casa_de_los_libros", "synchronia" to "copias", "opera" to "obras", "iudicium" to "juicio", "citatio" to "enseñanza", "sententia" to "máxima", "orbis" to "mundo", "nova" to "nuevo", "mirabilia" to "maravillas", "somnium" to "lo_soñado", "epistulae" to "papiros_sellados", "aperta" to "abierto", "initium" to "comienzo", "finis" to "fin", "pessimus" to "lo_peor", "personae" to "rostros", "themata" to "asuntos", "praeclarum" to "lo_más_justo", "dies felix" to "día_feliz",
        "consuetudo" to "costumbres", "series" to "racha", "palmae" to "ofrendas", "reditus" to "ingresos",
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
    fun nombre(titulo: String): String? = io.github.ruddy2003.vestigium.ui.nombres.NombresCompletos.de(TemaId.HELENO, titulo) ?: NOMBRES[titulo] ?: NombresNuevos.de(TemaId.HELENO, titulo)
}

/**
 * Egipto: numerales jeroglíficos y el año civil de tres estaciones (Ajet, la inundación; Peret, la siembra; Shemu, la
 * cosecha), cada una de cuatro meses de 30 días, más 5 días añadidos (epagómenos). Es una aproximación: el año faraónico
 * no tenía bisiesto y se corría un día cada cuatro años respecto de las estaciones; aquí se usa la forma fija que le dio
 * Augusto en Alejandría, la misma que sigue hoy el calendario copto (1 de Tot = 11 de septiembre, o 12 antes de un año
 * bisiesto gregoriano). Los nombres de los meses son los de época tardía, que son los que conserva el copto.
 */
object Egipcio {
    /** Día juliano del 1 de Tot del año 1 de la era alejandrina-copta (29 de agosto del 284, juliano). */
    private const val EPOCA = 1825030L

    val MESES = listOf("Tot", "Paofi", "Hatir", "Koiak", "Tobi", "Meshir", "Paremhat", "Parmuti", "Pashons", "Paoni", "Epep", "Mesori")
    val ESTACIONES = listOf("Ajet", "Peret", "Shemu")
    private val ROMANOS = listOf("I", "II", "III", "IV")
    /** Los cinco días añadidos son los nacimientos de Osiris, Horus, Seth, Isis y Neftis; el sexto solo existe en los años bisiestos. */
    private val DIOSES = listOf("Osiris", "Horus", "Seth", "Isis", "Neftis", "el sexto")

    /**
     * Los días de la semana, de lunes a domingo. Egipto no tenía semana de siete días (contaba décadas de diez), así que
     * se toman los nombres egipcios de los astros que dan nombre a los días: Iah (la Luna), «Horus rojo» (Desher, Marte),
     * Sebeg (Mercurio), «Horus que abre las Dos Tierras» (Wepsha, Júpiter), «el de la mañana» (Duai, Venus), «Horus toro
     * del cielo» (Kapet, Saturno) y Ra (el Sol). Las formas cortas son propias.
     */
    val SEMANA = listOf("Iah", "Desher", "Sebeg", "Wepsha", "Duai", "Kapet", "Ra")

    /** Año, mes (1 a 12, o 13 para los días añadidos) y día del año civil fijo. */
    fun civil(d: java.time.LocalDate): Triple<Int, Int, Int> {
        val j = d.toEpochDay() + 2440588L
        val y = Math.floorDiv(4 * (j - EPOCA) + 1463, 1461L)
        val ini = EPOCA + 365 * (y - 1) + Math.floorDiv(y, 4L)
        val m = (j - ini) / 30 + 1
        return Triple(y.toInt(), m.toInt(), (j - ini - 30 * (m - 1) + 1).toInt())
    }

    /** «II Ajet 7» (el segundo mes de la inundación, día 7), o «día añadido 3 (Seth)». */
    fun dia(d: java.time.LocalDate): String {
        val (_, m, dd) = civil(d)
        return if (m == 13) "día añadido $dd (${DIOSES[(dd - 1).coerceIn(0, 5)]})" else "${ROMANOS[(m - 1) % 4]} ${ESTACIONES[(m - 1) / 4]} $dd"
    }

    fun fecha(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        val m = civil(d).second
        dia(d) + (if (m <= 12) " · " + MESES[m - 1] else "") + " · ${d.year}"
    }.getOrDefault(iso)

    /** El mes civil que cubre la mayor parte del mes gregoriano [n]: Tot ≈ septiembre, Paofi ≈ octubre… */
    fun mes(n: Int, anio: Int? = null): String {
        val i = (n.coerceIn(1, 12) + 3) % 12
        return "${MESES[i]} (${ROMANOS[i % 4]} ${ESTACIONES[i / 4]})" + (anio?.let { " · $it" } ?: "")
    }

    /**
     * Numerales jeroglíficos, de mayor a menor: dedos (10 000), flores de loto (1000), rollos de cuerda (100), asas (10) y
     * trazos (1). Cada cifra usa el signo agrupado de Unicode («7 trazos» es un solo signo). La letra del tema no los trae:
     * los dibuja la letra de jeroglíficos del sistema (Noto Sans Egyptian Hieroglyphs). De 100 000 en adelante, cifras.
     */
    fun numeral(n: Int): String {
        if (n <= 0 || n >= 100000) return n.toString()
        val bases = intArrayOf(0x130AD, 0x131BC, 0x13362, 0x13386, 0x133FA)
        val sb = StringBuilder(); var x = n; var div = 10000
        for (b in bases) {
            val c = x / div
            if (c > 0) sb.appendCodePoint(b + c - 1)
            x %= div; div /= 10
        }
        return sb.toString()
    }
}
