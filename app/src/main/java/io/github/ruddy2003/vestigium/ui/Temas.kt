package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.ui.nombres.NombresCompletos

/**
 * Lo propio de cada tema que no es dibujo: nombres de pantallas y secciones, calendarios, marcas, rótulos de widget y
 * numerales. Todo es invención propia:
 * ningún nombre, lema ni símbolo viene de novelas o videojuegos con derechos de autor.
 */
object Temas {
    /** Numerales de copista: romanos en minúscula, 4 como «iiij» y la última i alargada en j (xvij, mmxxvj). */
    fun copista(n: Int): String {
        if (n <= 0) return "nulla"
        var s = Romano.de(n).lowercase().replace("iv", "iiij")
        if (s.endsWith("i")) s = s.dropLast(1) + "j"
        return s
    }

    /** Meses con la grafía castellana de los siglos XIII a XV. */
    val MESES_MEDIEVALES = listOf("enero", "febrero", "março", "abril", "mayo", "junio", "julio", "agosto", "setiembre", "otubre", "noviembre", "deziembre")

    /** Fiestas que ordenaban el año medieval; varias eran los «días de cuarto» en que se pagaban rentas. */
    val FIESTAS = mapOf(
        "01-06" to "Reyes", "02-02" to "Cand.", "03-25" to "Anunc.", "06-24" to "S. Juan", "08-15" to "Asunc.",
        "09-29" to "S. Miguel", "11-01" to "Santos", "11-11" to "S. Martín", "12-25" to "Navidad",
    )

    // ------------------------------------------------------------ calendarios y números de los temas nuevos

    val MESES_FRANCESES = listOf("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre")
    val MESES_RUSOS = listOf("ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ", "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ")
    private val REPUBLICANOS = listOf("vendémiaire", "brumaire", "frimaire", "nivôse", "pluviôse", "ventôse", "germinal", "floréal", "prairial", "messidor", "thermidor", "fructidor")

    /**
     * Calendario republicano francés (1793–1805): doce meses de 30 días que empiezan cerca del equinoccio de otoño,
     * más los días complementarios, contados desde la proclamación de la República. (Aproximado: el año empieza el 22 de septiembre.)
     */
    fun republicano(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        val inicioEsteAnio = java.time.LocalDate.of(d.year, 9, 22)
        val inicio = if (d.isBefore(inicioEsteAnio)) inicioEsteAnio.minusYears(1) else inicioEsteAnio
        val an = inicio.year - 1791
        val i = java.time.temporal.ChronoUnit.DAYS.between(inicio, d).toInt()
        val dia = if (i < 360) "${i % 30 + 1} ${REPUBLICANOS[i / 30]}" else "jour complémentaire ${i - 359}"
        "$dia an ${Romano.de(an)}"
    }.getOrDefault(iso)

    /**
     * El mes republicano que ocupa la mayor parte de un mes gregoriano: nivôse en enero, pluviôse en febrero… vendémiaire en
     * octubre (del 22 de septiembre al 21 de octubre), brumaire en noviembre y frimaire en diciembre.
     */
    fun mesRepublicano(mes: Int): String = REPUBLICANOS[Math.floorMod(mes + 2, 12)]

    /** «an CCXXXV»: el año de la República al que pertenece un mes gregoriano (el que tiene la mayor parte del mes). */
    fun anRepublicano(anio: Int, mes: Int): String = "an " + Romano.de(if (mes >= 10) anio - 1791 else anio - 1792)

    private val SEMANA_FRANCESA = listOf("lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche")

    /** El Imperio volvió al calendario gregoriano en 1806: «samedi 17 octobre 2026». */
    fun fechaImperial(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        "${SEMANA_FRANCESA[d.dayOfWeek.value - 1]} ${if (d.dayOfMonth == 1) "1er" else d.dayOfMonth} ${MESES_FRANCESES[d.monthValue - 1]} ${d.year}"
    }.getOrDefault(iso)

    /** Tierras Antiguas: el año se cuenta por lunas con nombres de las labores y del clima (invención propia). */
    val LUNAS = listOf("LUNA DEL HIELO", "LUNA DE LA NIEVE", "LUNA DEL DESHIELO", "LUNA DE LA SIEMBRA", "LUNA DE LAS FLORES", "LUNA LARGA",
        "LUNA DEL HENO", "LUNA DE LA COSECHA", "LUNA DE LAS HOJAS", "LUNA DE LA NIEBLA", "LUNA DE LA ESCARCHA", "LUNA DEL HOGAR")

    /** «17 de la Luna de las Hojas» (el 17 de octubre, contado a la antigua). */
    fun fechaAntigua(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        "${d.dayOfMonth} de la " + LUNAS[d.monthValue - 1].lowercase().replaceFirstChar { it.uppercase() }
    }.getOrDefault(iso)

    private val CIFRAS_CHINAS = listOf("〇", "一", "二", "三", "四", "五", "六", "七", "八", "九")

    /** Números chinos tradicionales: 十七, 二十三, 一百二十… */
    fun chino(n: Int): String {
        if (n <= 0) return "〇"
        if (n >= 10000) return n.toString()
        val partes = StringBuilder()
        val mil = n / 1000; val cien = (n / 100) % 10; val diez = (n / 10) % 10; val uno = n % 10
        if (mil > 0) partes.append(CIFRAS_CHINAS[mil] + "千")
        if (cien > 0) partes.append(CIFRAS_CHINAS[cien] + "百") else if (mil > 0 && (diez > 0 || uno > 0)) partes.append("〇")
        if (diez > 0) partes.append((if (diez == 1 && n < 20) "" else CIFRAS_CHINAS[diez]) + "十") else if (cien > 0 && uno > 0) partes.append("〇")
        if (uno > 0) partes.append(CIFRAS_CHINAS[uno])
        return partes.toString()
    }

    /** Año escrito cifra por cifra, como en las fechas chinas: 二〇二六年. */
    fun anioChino(a: Int): String = a.toString().map { CIFRAS_CHINAS[it - '0'] }.joinToString("") + "年"

    fun fechaChina(a: Int, m: Int, d: Int) = anioChino(a) + " " + chino(m) + "月" + chino(d) + "日"

    /**
     * Japón Edo: los nombres tradicionales de los meses (wafū getsumei: «mes de la armonía», «mes sin dioses»…). En la época
     * Edo nombraban los meses del calendario lunisolar; hoy se usan, por costumbre, para los meses del calendario gregoriano,
     * y así se usan aquí (aproximación).
     */
    val MESES_JAPONESES = listOf("睦月", "如月", "弥生", "卯月", "皐月", "水無月", "文月", "葉月", "長月", "神無月", "霜月", "師走")

    /** Los días de la semana en japonés, de lunes a domingo: luna, fuego, agua, madera, metal, tierra y sol. */
    val SEMANA_JAPONESA = listOf("月", "火", "水", "木", "金", "土", "日")

    /**
     * Las 72 microestaciones (kō) del almanaque japonés (la versión de 1685 de Shibukawa Shunkai, con los nombres que se
     * usan hoy): cada uno de los 24 términos solares se parte en tres, de unos cinco días. Las fechas de inicio son las
     * habituales y se mueven un día de un año a otro (aproximación). (mes, día de inicio, nombre en español).
     */
    private val KO = listOf(
        Triple(2, 4, "el viento del este deshiela"), Triple(2, 9, "canta el ruiseñor del monte"), Triple(2, 14, "los peces asoman bajo el hielo"),
        Triple(2, 19, "la lluvia humedece la tierra"), Triple(2, 24, "se tiende la neblina"), Triple(3, 1, "despiertan hierbas y árboles"),
        Triple(3, 6, "salen los insectos dormidos"), Triple(3, 11, "sonríen los primeros melocotoneros"), Triple(3, 16, "las orugas se vuelven mariposas"),
        Triple(3, 21, "los gorriones hacen nido"), Triple(3, 26, "se abren los primeros cerezos"), Triple(3, 31, "el trueno alza la voz a lo lejos"),
        Triple(4, 5, "llegan las golondrinas"), Triple(4, 10, "los gansos vuelan al norte"), Triple(4, 15, "se ven los primeros arcoíris"),
        Triple(4, 20, "brotan los primeros juncos"), Triple(4, 25, "cesa la escarcha, crece el arroz"), Triple(4, 30, "florecen las peonías"),
        Triple(5, 5, "empiezan a cantar las ranas"), Triple(5, 10, "salen las lombrices"), Triple(5, 15, "brotan los tallos de bambú"),
        Triple(5, 21, "los gusanos de seda comen morera"), Triple(5, 26, "florece el cártamo"), Triple(5, 31, "madura la cebada"),
        Triple(6, 6, "nacen las mantis"), Triple(6, 11, "de la hierba húmeda salen luciérnagas"), Triple(6, 16, "amarillean las ciruelas"),
        Triple(6, 21, "se seca la prunela"), Triple(6, 27, "florecen los lirios"), Triple(7, 2, "brota el hange"),
        Triple(7, 7, "llegan los vientos cálidos"), Triple(7, 12, "se abren los primeros lotos"), Triple(7, 17, "los halcones aprenden a volar"),
        Triple(7, 23, "las paulonias dan semilla"), Triple(7, 29, "tierra húmeda, aire de bochorno"), Triple(8, 3, "caen a ratos grandes lluvias"),
        Triple(8, 8, "llega el viento fresco"), Triple(8, 13, "cantan las cigarras del ocaso"), Triple(8, 18, "sube y baja una niebla espesa"),
        Triple(8, 23, "se abren las cápsulas del algodón"), Triple(8, 28, "por fin amaina el calor"), Triple(9, 2, "madura el arroz"),
        Triple(9, 8, "blanquea el rocío en la hierba"), Triple(9, 13, "cantan las lavanderas"), Triple(9, 18, "se van las golondrinas"),
        Triple(9, 23, "el trueno calla"), Triple(9, 28, "los insectos tapan sus madrigueras"), Triple(10, 3, "se secan los arrozales"),
        Triple(10, 8, "vuelven los gansos salvajes"), Triple(10, 13, "florecen los crisantemos"), Triple(10, 18, "los grillos cantan junto a la puerta"),
        Triple(10, 23, "cae la primera escarcha"), Triple(10, 28, "lloviznas ligeras"), Triple(11, 2, "arces y hiedras amarillean"),
        Triple(11, 7, "se abren las camelias"), Triple(11, 12, "la tierra empieza a helarse"), Triple(11, 17, "huelen los narcisos"),
        Triple(11, 22, "se esconden los arcoíris"), Triple(11, 27, "el viento del norte barre las hojas"), Triple(12, 2, "amarillean los tachibana"),
        Triple(12, 7, "se cierra el cielo, llega el invierno"), Triple(12, 12, "los osos se meten en su cueva"), Triple(12, 16, "los salmones remontan juntos"),
        Triple(12, 21, "brota la prunela"), Triple(12, 26, "los ciervos mudan las astas"), Triple(12, 31, "bajo la nieve brota el trigo"),
        Triple(1, 5, "crece el perejil japonés"), Triple(1, 10, "se mueven los manantiales"), Triple(1, 15, "llaman los faisanes"),
        Triple(1, 20, "florece la petasita"), Triple(1, 25, "se hielan los arroyos"), Triple(1, 30, "las gallinas empiezan a poner"),
    )
    private val ORDEN_KO = KO.indices.sortedBy { KO[it].first * 100 + KO[it].second }

    /** El kō de un día: su número (1 a 72, contados desde el comienzo de la primavera, hacia el 4 de febrero) y su nombre. */
    fun ko(d: java.time.LocalDate): Pair<Int, String> {
        val md = d.monthValue * 100 + d.dayOfMonth
        val i = ORDEN_KO.lastOrNull { KO[it].first * 100 + KO[it].second <= md } ?: ORDEN_KO.last()
        return (i + 1) to KO[i].third
    }

    /**
     * El año en la era japonesa (nengō), con la fecha exacta del cambio de era: 令和八年 (Reiwa 8) es 2026. El primer año
     * de una era se escribe 元年. Antes de Meiji, el año gregoriano en cifras chinas.
     */
    fun nengo(d: java.time.LocalDate): String {
        val eras = listOf(java.time.LocalDate.of(2019, 5, 1) to "令和", java.time.LocalDate.of(1989, 1, 8) to "平成",
            java.time.LocalDate.of(1926, 12, 25) to "昭和", java.time.LocalDate.of(1912, 7, 30) to "大正", java.time.LocalDate.of(1868, 10, 23) to "明治")
        val (ini, era) = eras.firstOrNull { !d.isBefore(it.first) } ?: return anioChino(d.year)
        val n = d.year - ini.year + 1
        return era + (if (n == 1) "元" else chino(n)) + "年"
    }

    /** «神無月十七日 · los grillos cantan junto a la puerta»: el mes tradicional, el día en kanji y el kō en que cae. */
    fun fechaEdo(d: java.time.LocalDate): String = MESES_JAPONESES[d.monthValue - 1] + chino(d.dayOfMonth) + "日 · " + ko(d).second

    /** «faltan N días para …», o el nombre del día cuando llega. */
    fun cuentaAtras(d: java.time.LocalDate, mes: Int, dia: Int, nombre: String, corto: String): String {
        var meta = java.time.LocalDate.of(d.year, mes, dia)
        if (d.isAfter(meta)) meta = meta.plusYears(1)
        val n = java.time.temporal.ChronoUnit.DAYS.between(d, meta)
        return when (n) { 0L -> "¡$nombre!"; 1L -> "mañana es $corto"; else -> "faltan $n días para $corto" }
    }

    // Marcas del calendario: días que cada tema celebra o recuerda (cortas: van dentro de la celda del día).
    private val MARCAS_CIBER = mapOf("01-01" to "epoch", "03-14" to "π", "06-23" to "Turing", "08-06" to "www", "10-29" to "ARPANET",
        "11-30" to "seguridad", "12-09" to "Hopper", "12-10" to "Lovelace")

    private val MARCAS_NEON = mapOf("01-01" to "epoch", "03-14" to "π", "06-23" to "Turing", "08-06" to "www", "10-29" to "ARPANET",
        "11-30" to "seguridad", "12-09" to "Hopper", "12-10" to "Lovelace")
    private val MARCAS_HELENO = mapOf("03-20" to "Equinoccio", "06-21" to "Año ático", "08-15" to "Panateneas", "08-20" to "Termópilas",
        "09-12" to "Maratón", "09-25" to "Misterios", "09-28" to "Salamina", "12-21" to "Solsticio")
    private val MARCAS_DISTOPIA = mapOf("01-01" to "CENSO", "04-15" to "DECLARACIÓN", "05-01" to "CUOTA", "07-01" to "INVENTARIO",
        "10-01" to "PLAN", "11-15" to "REVISIÓN", "12-31" to "CIERRE")
    private val MARCAS_REFUGIO = mapOf("01-15" to "FILTROS", "04-01" to "SIMULACRO", "06-01" to "INVENTARIO", "07-16" to "TRINITY",
        "10-23" to "SIMULACRO", "12-02" to "REACTOR")
    private val MARCAS_ORDEN = mapOf("01-15" to "JURAMENTO", "03-01" to "LEVA", "04-23" to "S. JORGE", "06-15" to "TORNEO",
        "09-01" to "CAMPAÑA", "11-11" to "ARMISTICIO", "12-21" to "VIGILIA")
    private val MARCAS_LABORATORIO = mapOf("02-11" to "Científicas", "03-14" to "Einstein", "04-25" to "ADN", "05-20" to "Metrología",
        "10-21" to "Nobel", "10-23" to "Mol", "11-07" to "Curie")
    private val MARCAS_COLONOS = mapOf("03-19" to "Siembra", "05-15" to "S. Isidro", "05-20" to "Tierras", "06-24" to "S. Juan",
        "09-29" to "S. Miguel", "10-15" to "Cosecha", "12-24" to "Nochebuena")
    private val MARCAS_NAPOLEON = mapOf("12-02" to "Austerlitz", "08-15" to "Natalicio", "06-18" to "Waterloo", "03-21" to "Código", "05-18" to "Imperio",
        "10-14" to "Jena", "06-14" to "Friedland", "03-20" to "Cien Días", "07-06" to "Wagram")
    private val MARCAS_ANTIGUAS = mapOf("12-21" to "Noche larga", "06-21" to "Día largo", "03-20" to "Deshielo", "09-22" to "Cosecha", "10-31" to "Víspera", "01-01" to "Año nuevo")
    private val MARCAS_REVOLUCION = mapOf("07-14" to "Bastilla", "06-20" to "Juego de pelota", "08-04" to "Privilegios", "08-26" to "Derechos", "10-05" to "Versalles",
        "09-22" to "República", "07-27" to "Termidor", "09-20" to "Valmy")
    private val MARCAS_BOTANICO = mapOf("05-23" to "Linneo", "02-12" to "Darwin", "09-14" to "Humboldt", "04-22" to "Tierra", "03-20" to "Equinoccio",
        "06-21" to "Solsticio", "09-22" to "Equinoccio", "12-21" to "Solsticio", "07-20" to "Mendel")
    private val MARCAS_GOTICO = mapOf("04-26" to "Consagr.", "08-25" to "S. Luis", "08-15" to "Asunción", "12-25" to "Navidad", "01-06" to "Epifanía", "11-01" to "Todos Santos", "09-29" to "S. Miguel")
    private val MARCAS_ESPACIO = mapOf("04-12" to "Gagarin", "07-20" to "Apolo 11", "10-04" to "Sputnik", "02-18" to "Plutón", "12-24" to "Earthrise",
        "03-20" to "Equinoccio", "06-21" to "Solsticio", "09-22" to "Equinoccio", "12-21" to "Solsticio", "04-24" to "Hubble")
    private val MARCAS_ANDALUS = mapOf("01-02" to "Granada", "04-30" to "Tariq", "03-30" to "Alhambra")
    private val MARCAS_MONGOL = mapOf("07-11" to "Naadam", "07-12" to "Naadam", "07-13" to "Naadam", "12-29" to "Independ.", "11-26" to "Repúbl.")
    private val MARCAS_RENACIMIENTO = mapOf("04-15" to "Leonardo", "05-02" to "Leonardo †", "03-06" to "Michelangelo", "03-25" to "Capodanno", "06-24" to "S. Giovanni", "04-06" to "Rafael")
    private val MARCAS_NAVIDAD = mapOf("12-12" to "Guadalupe", "12-16" to "Posadas", "12-24" to "Nochebuena", "12-25" to "Navidad", "12-28" to "Inocentes", "12-31" to "Año nuevo", "01-06" to "Reyes")
    // Nowruz, el equinoccio de otoño y tres de los veinticuatro términos solares chinos; y el día en que la ruta entró al Patrimonio Mundial.
    private val MARCAS_SEDA = mapOf("03-21" to "Nowruz", "09-21" to "Luna", "04-05" to "Qingming", "06-21" to "Xiazhi", "06-22" to "Ruta",
        "12-22" to "Dongzhi")
    // Los cinco sekku (hoy en sus fechas gregorianas), el año nuevo, Setsubun, Obon, Shichi-go-san y el último día del año.
    private val MARCAS_EDO = mapOf("01-01" to "Shōgatsu", "01-07" to "Nanakusa", "02-03" to "Setsubun", "03-03" to "Hinamatsuri", "04-08" to "Hanamatsuri",
        "05-05" to "Tango", "07-07" to "Tanabata", "08-15" to "Obon", "09-09" to "Chōyō", "11-15" to "Shichigosan", "12-31" to "Ōmisoka")
    private val MARCAS_URSS = mapOf("05-01" to "1º mayo", "04-12" to "Cosmos", "05-09" to "Victoria", "11-07" to "Octubre", "03-08" to "Mujer")

    /** Marca de [fecha] en el calendario del tema, o null. */
    fun marca(tema: TemaId, fecha: String): String? {
        val md = fecha.takeLast(5)
        return when (tema) {
            TemaId.ROMANO -> Romano.marca(fecha)
            TemaId.CIBER -> MARCAS_CIBER[md]
            TemaId.NEON -> MARCAS_NEON[md]
            TemaId.HELENO -> MARCAS_HELENO[md]
            TemaId.MEDIEVAL -> FIESTAS[md]
            TemaId.DISTOPIA -> MARCAS_DISTOPIA[md]
            TemaId.REFUGIO -> MARCAS_REFUGIO[md]
            TemaId.ORDEN -> MARCAS_ORDEN[md]
            TemaId.LABORATORIO -> MARCAS_LABORATORIO[md]
            TemaId.COLONOS -> MARCAS_COLONOS[md]
            TemaId.NAPOLEON -> MARCAS_NAPOLEON[md]
            TemaId.BOTANICO -> MARCAS_BOTANICO[md]
            TemaId.NAVIDAD -> MARCAS_NAVIDAD[md]
            TemaId.SEDA -> MARCAS_SEDA[md]
            TemaId.EDO -> MARCAS_EDO[md]
            TemaId.URSS -> MARCAS_URSS[md]
            TemaId.GOTICO -> MARCAS_GOTICO[md]
            TemaId.ESPACIO -> MARCAS_ESPACIO[md]
            TemaId.ANDALUS -> MARCAS_ANDALUS[md] ?: fiestaHegira(fecha)
            TemaId.MONGOL -> MARCAS_MONGOL[md]
            TemaId.RENACIMIENTO -> MARCAS_RENACIMIENTO[md]
            TemaId.REVOLUCION -> MARCAS_REVOLUCION[md]
            TemaId.ANTIGUAS -> MARCAS_ANTIGUAS[md]
        }
    }

    // ------------------------------------------------------------ calendarios de los temas gótico, espacio, Al-Ándalus, mongol, Renacimiento y botánico

    val MESES_LATINOS_GEN = listOf("ianuarii", "februarii", "martii", "aprilis", "maii", "iunii", "iulii", "augusti", "septembris", "octobris", "novembris", "decembris")
    val MESES_LATINOS = listOf("Ianuarius", "Februarius", "Martius", "Aprilis", "Maius", "Iunius", "Iulius", "Augustus", "September", "October", "November", "December")

    /** «die XVII octobris · A.D. MMXXVI», como en los cartularios. */
    fun fechaLatina(d: java.time.LocalDate) = "die ${Romano.de(d.dayOfMonth)} ${MESES_LATINOS_GEN[d.monthValue - 1]} · A.D. ${Romano.de(d.year)}"

    /** Hora canónica según la hora del reloj: maitines, laudes, prima, tercia, sexta, nona, vísperas, completas. */
    fun horaCanonica(h: Int): String = when (h) {
        in 0..2 -> "maitines"; in 3..5 -> "laudes"; in 6..8 -> "prima"; in 9..11 -> "tercia"; in 12..14 -> "sexta"; in 15..16 -> "nona"; in 17..19 -> "vísperas"; else -> "completas"
    }

    /** Día juliano al mediodía, el número con que los astrónomos cuentan los días sin calendario. */
    fun diaJuliano(d: java.time.LocalDate): Long = d.toEpochDay() + 2440588L

    private val MESES_HEGIRA = listOf("muharram", "safar", "rabi al-awwal", "rabi al-ajir", "yumada al-ula", "yumada al-ajira",
        "rayab", "shaabán", "ramadán", "shawwal", "du l-qada", "du l-hiyya")

    /** Fecha del calendario hégira (lunar, el de al-Ándalus) según el cálculo de Umm al-Qura. */
    fun hegira(d: java.time.LocalDate): Triple<Int, Int, Int>? = runCatching {
        val h = java.time.chrono.HijrahDate.from(d)
        Triple(h.get(java.time.temporal.ChronoField.DAY_OF_MONTH), h.get(java.time.temporal.ChronoField.MONTH_OF_YEAR), h.get(java.time.temporal.ChronoField.YEAR))
    }.getOrNull()

    fun fechaHegira(d: java.time.LocalDate): String = hegira(d)?.let { (dd, m, a) -> "$dd de ${MESES_HEGIRA[m - 1]} de $a H" } ?: Fechas.titulo(d.toString())

    /** Fiestas del calendario hégira: año nuevo, inicio de ramadán y las dos fiestas grandes. */
    private fun fiestaHegira(fecha: String): String? = runCatching { hegira(Fechas.parse(fecha)) }.getOrNull()?.let { (dd, m, _) ->
        when { m == 1 && dd == 1 -> "Año nuevo"; m == 9 && dd == 1 -> "Ramadán"; m == 10 && dd == 1 -> "Fitr"; m == 12 && dd == 10 -> "Adha"; else -> null }
    }

    private val CIFRAS_ARABES = "٠١٢٣٤٥٦٧٨٩"
    fun arabe(n: Int): String = n.toString().map { if (it.isDigit()) CIFRAS_ARABES[it - '0'] else it }.joinToString("")

    private val CIFRAS_MONGOLAS = "᠐᠑᠒᠓᠔᠕᠖᠗᠘᠙"
    fun mongol(n: Int): String = n.toString().map { if (it.isDigit()) CIFRAS_MONGOLAS[it - '0'] else it }.joinToString("")

    private val ANIMALES = listOf("хулгана", "үхэр", "бар", "туулай", "луу", "могой", "морин", "хонин", "бичин", "тахиа", "нохой", "гахай")
    private val COLOR_MACHO = listOf("цагаан", "хар", "хөх", "улаан", "шар")
    private val COLOR_HEMBRA = listOf("цагаагчин", "харагчин", "хөхөгчин", "улаагчин", "шарагчин")

    /** Año del calendario mongol: color del elemento y animal («улаан морин жил», año del caballo rojo). Cambia, aproximadamente, a inicios de febrero. */
    fun anioMongol(d: java.time.LocalDate): String {
        val a = if (d.monthValue == 1 || (d.monthValue == 2 && d.dayOfMonth < 4)) d.year - 1 else d.year
        val animal = ANIMALES[Math.floorMod(a - 2020, 12)]
        val elemento = (Math.floorMod(a, 10)) / 2
        val color = if (a % 2 == 0) COLOR_MACHO[elemento] else COLOR_HEMBRA[elemento]
        return "$color $animal жил".replaceFirstChar { it.uppercase() }
    }

    /**
     * Meses mongoles nombrados por la estación: el primero (тэргүүн), el de en medio (дунд) y el último (сүүл) de cada una.
     * El año empieza con la primavera, en el Цагаан сар (hacia febrero); aquí cada mes gregoriano toma el nombre del mes
     * lunar que más se le parece, así que es aproximado: octubre es el último del otoño.
     */
    val MESES_MONGOLES = listOf("Өвлийн сүүл сар", "Хаврын тэргүүн сар", "Хаврын дунд сар", "Хаврын сүүл сар", "Зуны тэргүүн сар", "Зуны дунд сар",
        "Зуны сүүл сар", "Намрын тэргүүн сар", "Намрын дунд сар", "Намрын сүүл сар", "Өвлийн тэргүүн сар", "Өвлийн дунд сар")

    fun fechaMongol(d: java.time.LocalDate) = "${d.year} оны ${d.monthValue}-р сарын ${d.dayOfMonth}"

    val MESES_ITALIANOS = listOf("gennaio", "febbraio", "marzo", "aprile", "maggio", "giugno", "luglio", "agosto", "settembre", "ottobre", "novembre", "dicembre")
    /** «a dì 17 d'ottobre 2026», como fechaba Leonardo sus apuntes. */
    fun fechaItaliana(d: java.time.LocalDate): String {
        val m = MESES_ITALIANOS[d.monthValue - 1]
        return "a dì ${d.dayOfMonth} " + (if (m.first() in "aeiou") "d'$m" else "di $m") + " ${d.year}"
    }

    /** Estación astronómica del hemisferio norte y cuántos días lleva: «otoño, día 26». */
    fun estacion(d: java.time.LocalDate): String {
        val y = d.year
        val inicios = listOf(java.time.LocalDate.of(y - 1, 12, 21) to "invierno", java.time.LocalDate.of(y, 3, 20) to "primavera",
            java.time.LocalDate.of(y, 6, 21) to "verano", java.time.LocalDate.of(y, 9, 22) to "otoño", java.time.LocalDate.of(y, 12, 21) to "invierno")
        val (ini, nombre) = inicios.last { !d.isBefore(it.first) }
        return "$nombre, día ${java.time.temporal.ChronoUnit.DAYS.between(ini, d) + 1}"
    }

    fun nombre(tema: TemaId, titulo: String): String? = NombresCompletos.de(tema, titulo) ?: nombreBase(tema, titulo) ?: NombresNuevos.de(tema, titulo)

    private fun nombreBase(tema: TemaId, titulo: String): String? = when (tema) {
        // El romano y el heleno tienen su diccionario en `Romano` y `Griego`; la terminal solo usa el de [NombresNuevos].
        TemaId.ROMANO -> null
        TemaId.CIBER -> null
        TemaId.NEON -> null
        TemaId.HELENO -> null
        TemaId.MEDIEVAL -> MEDIEVAL[titulo]
        TemaId.DISTOPIA -> DISTOPIA[titulo]
        TemaId.REFUGIO -> REFUGIO[titulo]
        TemaId.ORDEN -> ORDEN[titulo]
        TemaId.LABORATORIO -> LABORATORIO[titulo]
        TemaId.COLONOS -> COLONOS[titulo]
        TemaId.NAPOLEON -> NAPOLEON[titulo]
        TemaId.BOTANICO -> BOTANICO[titulo]
        TemaId.GOTICO -> GOTICO[titulo]
        TemaId.ESPACIO -> ESPACIO[titulo]
        TemaId.ANDALUS -> ANDALUS[titulo]
        TemaId.MONGOL -> MONGOL[titulo]
        TemaId.RENACIMIENTO -> RENACIMIENTO[titulo]
        TemaId.NAVIDAD -> NAVIDAD[titulo]
        TemaId.SEDA -> SEDA[titulo]
        TemaId.EDO -> EDO[titulo]
        TemaId.URSS -> URSS[titulo]
        TemaId.REVOLUCION -> REVOLUCION[titulo]
        TemaId.ANTIGUAS -> ANTIGUAS[titulo]
    }

    fun widget(tema: TemaId, clave: String): String {
        val m = when (tema) {
            TemaId.ROMANO -> mapOf("animo" to "ANIMVS", "momento" to "MOMENTA", "gasto" to "SVMPTVS", "sueno" to "SOMNVS", "habitos" to "CONSVETVDINES", "hoy" to "DIES", "cifra" to "NVMERVS", "foto" to "IMAGO", "enfoque" to "STVDIVM")
            TemaId.CIBER -> mapOf("animo" to "> ánimo", "momento" to "> momento", "gasto" to "> gasto", "sueno" to "> sueño", "habitos" to "> hábitos", "hoy" to "> hoy", "cifra" to "> cifra", "foto" to "> foto", "enfoque" to "> enfoque")
            TemaId.NEON -> mapOf("animo" to "> ánimo", "momento" to "> momento", "gasto" to "> gasto", "sueno" to "> sueño", "habitos" to "> hábitos", "hoy" to "> hoy", "cifra" to "> cifra", "foto" to "> foto", "enfoque" to "> enfoque")
            TemaId.HELENO -> mapOf("animo" to "ΘΥΜΟΣ", "momento" to "ΚΑΙΡΟΙ", "gasto" to "ΔΑΠΑΝΑΙ", "sueno" to "ΥΠΝΟΣ", "habitos" to "ΕΘΗ", "hoy" to "ΗΜΕΡΑ", "cifra" to "ΑΡΙΘΜΟΣ", "foto" to "ΕΙΚΩΝ", "enfoque" to "ΣΧΟΛΗ")
            TemaId.MEDIEVAL -> mapOf("animo" to "Del ánima", "momento" to "Crónica", "gasto" to "Cuentas", "sueno" to "Del sueño", "habitos" to "Regla", "hoy" to "Del día", "cifra" to "Cuenta", "foto" to "Miniatura", "enfoque" to "Estudio")
            TemaId.DISTOPIA -> mapOf("animo" to "ESTADO", "momento" to "INCIDENCIA", "gasto" to "RACIÓN", "sueno" to "DESCANSO", "habitos" to "CUOTAS", "hoy" to "PARTE", "cifra" to "CIFRA OFICIAL", "foto" to "EVIDENCIA", "enfoque" to "PRODUCTIVIDAD")
            TemaId.REFUGIO -> mapOf("animo" to "ESTADO", "momento" to "SUCESO", "gasto" to "SUMINISTROS", "sueno" to "DESCANSO", "habitos" to "PROTOCOLOS", "hoy" to "BITÁCORA", "cifra" to "LECTURA", "foto" to "FOTO", "enfoque" to "TURNO")
            TemaId.ORDEN -> mapOf("animo" to "MORAL", "momento" to "BITÁCORA", "gasto" to "INTENDENCIA", "sueno" to "DESCANSO", "habitos" to "DISCIPLINA", "hoy" to "PARTE", "cifra" to "CIFRA", "foto" to "IMAGEN", "enfoque" to "VIGILIA")
            TemaId.LABORATORIO -> mapOf("animo" to "ESTADO", "momento" to "OBSERVACIÓN", "gasto" to "RECURSOS", "sueno" to "SUEÑO", "habitos" to "VARIABLES", "hoy" to "SESIÓN", "cifra" to "MEDIDA", "foto" to "MUESTRA", "enfoque" to "ENSAYO")
            TemaId.COLONOS -> mapOf("animo" to "Ánimo", "momento" to "Sucesos", "gasto" to "Cuentas", "sueno" to "Descanso", "habitos" to "Faenas", "hoy" to "Jornada", "cifra" to "Número", "foto" to "Estampa", "enfoque" to "Faena")
            TemaId.NAPOLEON -> mapOf("animo" to "MORAL", "momento" to "DESPACHO", "gasto" to "TESORERÍA", "sueno" to "VIVAC", "habitos" to "CÓDIGO", "hoy" to "BOLETÍN", "cifra" to "CIFRA", "foto" to "RETRATO", "enfoque" to "ÉTUDE")
            TemaId.BOTANICO -> mapOf("animo" to "Ánimo", "momento" to "Observación", "gasto" to "Semillas", "sueno" to "Latencia", "habitos" to "Cuidados", "hoy" to "Herbario", "cifra" to "Medida", "foto" to "Lámina", "enfoque" to "Estudio")
            TemaId.GOTICO -> mapOf("animo" to "Ánima", "momento" to "Horas", "gasto" to "Tesoro", "sueno" to "Maitines", "habitos" to "Regla", "hoy" to "Laudes", "cifra" to "Cuenta", "foto" to "Vidriera", "enfoque" to "Lectio")
            TemaId.ESPACIO -> mapOf("animo" to "ESTADO", "momento" to "EVENTO", "gasto" to "COMBUSTIBLE", "sueno" to "SUEÑO", "habitos" to "RUTINAS", "hoy" to "ÓRBITA", "cifra" to "TELEMETRÍA", "foto" to "IMAGEN", "enfoque" to "CONCENTRACIÓN")
            TemaId.ANDALUS -> mapOf("animo" to "الروح", "momento" to "لحظة", "gasto" to "النفقات", "sueno" to "النوم", "habitos" to "العادات", "hoy" to "النهار", "cifra" to "الحساب", "foto" to "صورة", "enfoque" to "دراسة")
            TemaId.MONGOL -> mapOf("animo" to "СЭТГЭЛ", "momento" to "АГШИН", "gasto" to "ЗАРДАЛ", "sueno" to "НОЙР", "habitos" to "ЗАНШИЛ", "hoy" to "ӨДӨР", "cifra" to "ТОО", "foto" to "ЗУРАГ", "enfoque" to "СУРАЛЦАХ")
            TemaId.RENACIMIENTO -> mapOf("animo" to "Animo", "momento" to "Nota", "gasto" to "Conti", "sueno" to "Sonno", "habitos" to "Esercizi", "hoy" to "Giornata", "cifra" to "Misura", "foto" to "Ritratto", "enfoque" to "Studio")
            TemaId.NAVIDAD -> mapOf("animo" to "Ánimo", "momento" to "Momento", "gasto" to "Regalos", "sueno" to "Noche de paz", "habitos" to "Propósitos", "hoy" to "El día", "cifra" to "Número", "foto" to "Postal", "enfoque" to "Taller")
            TemaId.SEDA -> mapOf("animo" to "Espíritu", "momento" to "Relato", "gasto" to "Mercancías", "sueno" to "Reposo", "habitos" to "Ritos", "hoy" to "Jornada", "cifra" to "Cuenta", "foto" to "Estampa", "enfoque" to "Estudio")
            TemaId.EDO -> mapOf("animo" to "Corazón", "momento" to "Estampa", "gasto" to "Monedero", "sueno" to "Futón", "habitos" to "Costumbres", "hoy" to "Posta", "cifra" to "Ábaco", "foto" to "Ukiyo-e", "enfoque" to "Pincel")
            TemaId.URSS -> mapOf("animo" to "MORAL", "momento" to "INFORME", "gasto" to "ECONOMÍA", "sueno" to "DESCANSO", "habitos" to "NORMAS", "hoy" to "PLAN", "cifra" to "PRODUCCIÓN", "foto" to "FOTO", "enfoque" to "TRABAJO")
            TemaId.REVOLUCION -> mapOf("animo" to "ESPÍRITU", "momento" to "SUCESO", "gasto" to "ASIGNADOS", "sueno" to "REPOSO", "habitos" to "VIRTUDES", "hoy" to "CAHIER", "cifra" to "CIFRA", "foto" to "ESTAMPA", "enfoque" to "ESTUDIO")
            TemaId.ANTIGUAS -> mapOf("animo" to "TEMPLE", "momento" to "SUCESO", "gasto" to "MONEDAS", "sueno" to "VIGILIA", "habitos" to "JURAMENTOS", "hoy" to "CRÓNICA", "cifra" to "CUENTA", "foto" to "GRABADO", "enfoque" to "ESTUDIO")
        }
        return m[clave] ?: clave.uppercase()
    }

    private val MEDIEVAL = mapOf(
        "Mundo" to "Orbe", "Patrimonio Mundial" to "Maravillas del orbe", "Mapa para rascar" to "Mapamundi de oro", "Ciudades visitadas" to "Ciudades",
        "Biblioteca de sueños" to "Libro de las visiones", "Cartas" to "Epístolas", "Escribir una carta" to "Escribir epístola", "Carta" to "Epístola", "Conocerte" to "De ti mismo", "Brújula política" to "Del buen gobierno", "Mapas" to "Mapamundis", "Salud" to "Regimiento de sanidad", "Tus apps" to "Los ingenios", "Otras vidas" to "Otras vidas", "Logros" to "Gestas", "Tu año en historias" to "Historias del año", "Anoche" to "Esta noche pasada", "Selladas" to "Selladas con lacre", "Listas para abrir" to "Prestas a abrirse", "A otras personas" to "A otras gentes", "Del pasado" to "De antaño",
        "Hoy" to "El folio de hoy", "Línea de tiempo" to "Crónica", "Ahora mismo estoy…" to "En este instante…",
        "Entender" to "Glosas", "Registros especiales" to "Otros libros", "Apariencia" to "Estilo", "Avisos y recordatorios" to "Campanas",
        "Llenar el día" to "Completas", "Biblioteca del diario" to "Libro de los días", "Entrada" to "Folio",
        "Día" to "Del día", "Momentos" to "Crónica", "Gastos" to "Libro de cuentas", "Explorar" to "Glosas", "Más" to "Otros folios",
        "Hábitos" to "Regla", "Cartas al futuro" to "Epístolas", "Experimentos personales" to "Pruebas", "Cómo leer tus datos" to "Tratado", "Guía científica" to "Tratado",
        "Cuestionarios" to "Cuestiones", "Prueba de reacción" to "Presteza", "Datos" to "Archivo", "Permisos y sensores" to "Sentidos",
        "Ajustes" to "Ordenanzas", "Reportes" to "Anales", "Bienvenido" to "Bien venido", "Escalada" to "Ascensión", "Obras" to "Librería",
        "Tu año" to "Anuario", "Privacidad" to "Secreto", "Sueño" to "Del sueño", "Contexto" to "Circunstancias", "Hitos" to "Memorables",
        "Ánimo y cuerpo" to "Del ánima y del cuerpo", "Lectura del día" to "Lección del día", "Lo mejor y lo peor" to "Lo mejor e lo peor",
        "Medicamentos" to "Remedios", "Seguimiento privado" to "Lo secreto", "Predicción" to "Augurio", "Momentos de este día" to "Crónica del día",
        "Este día hace un año" to "Hogaño y antaño", "Lo que registró el teléfono" to "Lo que asentó el ingenio", "Registrar" to "Asentar",
        "Últimos 30 días" to "Treinta días", "Cobertura" to "Constancia", "Cruzar dos variables" to "Concordancia",
        "Las relaciones más marcadas" to "Concordancias", "Personas, temas e hitos" to "Personas e materias", "Mapa" to "Mapamundi",
        "Mapa emocional" to "Mapamundi del ánima", "Tablero" to "Tabla", "Tu día" to "Las horas del día", "Tu día en 24 horas" to "Las horas del día", "Te falta hoy" to "Lo que resta",
        "Datos que faltan" to "Lagunas", "Tema" to "Estilo", "Recordatorios" to "Avisos", "Resumen semanal" to "La semana", "Totales" to "Suma de todo",
        "Gimnasio" to "Ejercicio de armas", "Análisis" to "Quaestiones", "Cifras" to "Cuenta", "Momentos de la semana" to "Crónica de la semana",
        "Fotos del día" to "Miniaturas", "Exportar" to "Trasladar", "Importar" to "Traer", "Respaldo automático" to "Copia guardada", "Clima" to "Del tiempo",
        "Widgets" to "Tablillas", "Lugares" to "Lugares", "Diario" to "Diario", "Calendario" to "Calendario", "Buscar" to "Buscar",
    )

    private val DISTOPIA = mapOf(
        "Mundo" to "DESPLAZAMIENTOS", "Patrimonio Mundial" to "SITIOS AUTORIZADOS", "Mapa para rascar" to "TERRITORIOS VISITADOS", "Ciudades visitadas" to "JURISDICCIONES",
        "Biblioteca de sueños" to "ARCHIVO ONÍRICO", "Cartas" to "CORRESPONDENCIA", "Escribir una carta" to "REDACTAR CORRESPONDENCIA", "Carta" to "MISIVA", "Conocerte" to "FICHA DEL CIUDADANO", "Brújula política" to "EVALUACIÓN IDEOLÓGICA", "Mapas" to "CARTOGRAFÍA OFICIAL", "Salud" to "INFORME SANITARIO", "Tus apps" to "MONITOREO DE ACTIVIDAD", "Otras vidas" to "EXPEDIENTES HISTÓRICOS", "Logros" to "CONDECORACIONES", "Tu año en historias" to "INFORME ANUAL DEL CIUDADANO", "Anoche" to "ÚLTIMO TURNO NOCTURNO", "Selladas" to "CLASIFICADAS", "Listas para abrir" to "DESCLASIFICADAS", "A otras personas" to "A OTROS CIUDADANOS", "Del pasado" to "ARCHIVO HISTÓRICO",
        "Hoy" to "DECLARACIÓN DE HOY", "Línea de tiempo" to "REGISTRO CRONOLÓGICO", "Ahora mismo estoy…" to "ESTADO ACTUAL DEL CIUDADANO",
        "Entender" to "INTELIGENCIA", "Registros especiales" to "REGISTROS ESPECIALES", "Apariencia" to "APARIENCIA", "Avisos y recordatorios" to "CITACIONES",
        "Llenar el día" to "DECLARACIÓN NOCTURNA", "Biblioteca del diario" to "ARCHIVO DE DECLARACIONES", "Entrada" to "DECLARACIÓN",
        "Día" to "PARTE DIARIO", "Momentos" to "INCIDENCIAS", "Gastos" to "RACIONAMIENTO", "Explorar" to "VIGILANCIA", "Más" to "MINISTERIOS",
        "Hábitos" to "CUOTAS", "Cartas al futuro" to "CORRESPONDENCIA SELLADA", "Experimentos personales" to "PRUEBAS AUTORIZADAS",
        "Cómo leer tus datos" to "DOCTRINA", "Guía científica" to "DOCTRINA", "Cuestionarios" to "INTERROGATORIOS", "Prueba de reacción" to "PRUEBA DE REFLEJOS", "Lugares" to "ZONAS",
        "Datos" to "ARCHIVO CENTRAL", "Permisos y sensores" to "SENSORES", "Ajustes" to "DIRECTIVAS", "Reportes" to "INFORMES",
        "Bienvenido" to "ALTA DE CIUDADANO", "Calendario" to "PLAN", "Obras" to "MATERIAL APROBADO", "Tu año" to "BALANCE ANUAL",
        "Privacidad" to "CLASIFICADO", "Sueño" to "DESCANSO", "Contexto" to "CIRCUNSTANCIAS", "Hitos" to "HECHOS NOTABLES",
        "Ánimo y cuerpo" to "ESTADO DEL CIUDADANO", "Diario" to "DECLARACIÓN", "Predicción" to "PRONÓSTICO", "Este día hace un año" to "ANTECEDENTES",
        "Lo que registró el teléfono" to "VIGILANCIA AUTOMÁTICA", "Registrar" to "DECLARAR", "Las relaciones más marcadas" to "CORRELACIONES",
        "Mapa" to "ZONIFICACIÓN", "Mapa emocional" to "ZONIFICACIÓN", "Tablero" to "INDICADORES", "Tu día" to "SEGUIMIENTO 24 H", "Tu día en 24 horas" to "SEGUIMIENTO 24 H",
        "Te falta hoy" to "PENDIENTE DE DECLARAR", "Datos que faltan" to "OMISIONES", "Tema" to "APARIENCIA", "Recordatorios" to "CITACIONES",
        "Resumen semanal" to "INFORME SEMANAL", "Totales" to "ESTADÍSTICA OFICIAL", "Gimnasio" to "ACONDICIONAMIENTO", "Buscar" to "BÚSQUEDA",
        "Cifras" to "CIFRAS OFICIALES", "Fotos del día" to "EVIDENCIA FOTOGRÁFICA", "Medicamentos" to "SUMINISTRO MÉDICO",
        "Seguimiento privado" to "EXPEDIENTE RESERVADO", "Momentos de la semana" to "INCIDENCIAS DE LA SEMANA", "Momentos de este día" to "INCIDENCIAS DEL DÍA",
        "Lo mejor y lo peor" to "MÉRITOS Y FALTAS", "Personas, temas e hitos" to "PERSONAS DE INTERÉS", "Respaldo automático" to "COPIA DE SEGURIDAD",
    )

    private val REFUGIO = mapOf(
        "Mundo" to "SUPERFICIE", "Patrimonio Mundial" to "MONUMENTOS EN PIE", "Mapa para rascar" to "MAPA DE EXPLORACIÓN", "Ciudades visitadas" to "ASENTAMIENTOS",
        "Biblioteca de sueños" to "REGISTRO DE SUEÑOS", "Cartas" to "CORREO DEL REFUGIO", "Escribir una carta" to "NUEVO MENSAJE", "Carta" to "MENSAJE", "Conocerte" to "FICHA DEL RESIDENTE", "Brújula política" to "ENCUESTA CÍVICA", "Mapas" to "MAPAS DE LA SUPERFICIE", "Salud" to "ENFERMERÍA", "Tus apps" to "TERMINALES", "Otras vidas" to "REGISTROS DE OTRAS ÉPOCAS", "Logros" to "INSIGNIAS", "Tu año en historias" to "REGISTRO ANUAL", "Anoche" to "ÚLTIMA NOCHE", "Selladas" to "CÁPSULAS SELLADAS", "Listas para abrir" to "CÁPSULAS LISTAS", "A otras personas" to "A OTROS RESIDENTES", "Del pasado" to "CÁPSULAS ABIERTAS",
        "Hoy" to "ENTRADA DE HOY", "Línea de tiempo" to "REGISTRO", "Ahora mismo estoy…" to "ESTADO ACTUAL",
        "Entender" to "ANÁLISIS", "Registros especiales" to "REGISTROS ESPECIALES", "Apariencia" to "APARIENCIA", "Avisos y recordatorios" to "ALARMAS",
        "Llenar el día" to "INFORME DE CIERRE", "Biblioteca del diario" to "DIARIOS DEL RESIDENTE", "Entrada" to "REGISTRO",
        "Día" to "BITÁCORA", "Momentos" to "SUCESOS", "Gastos" to "SUMINISTROS", "Explorar" to "SUPERFICIE", "Más" to "TERMINAL",
        "Hábitos" to "PROTOCOLOS", "Cartas al futuro" to "CÁPSULA DEL TIEMPO", "Experimentos personales" to "PRUEBAS", "Cómo leer tus datos" to "MANUAL", "Guía científica" to "MANUAL",
        "Cuestionarios" to "EVALUACIONES", "Prueba de reacción" to "REFLEJOS", "Lugares" to "SECTORES", "Datos" to "ARCHIVO",
        "Permisos y sensores" to "SENSORES", "Ajustes" to "MANTENIMIENTO", "Reportes" to "INFORMES", "Bienvenido" to "BIENVENIDO, RESIDENTE",
        "Tu año" to "EL AÑO", "Sueño" to "TURNO DE DESCANSO", "Ánimo y cuerpo" to "ESTADO DEL RESIDENTE", "Predicción" to "PRONÓSTICO",
        "Lo que registró el teléfono" to "LECTURAS AUTOMÁTICAS", "Mapa" to "MAPA DE SUPERFICIE", "Mapa emocional" to "MAPA DE SUPERFICIE",
        "Tablero" to "PANEL", "Tu día" to "CICLO DE 24 H", "Tu día en 24 horas" to "CICLO DE 24 H", "Te falta hoy" to "TAREAS PENDIENTES", "Datos que faltan" to "LECTURAS PERDIDAS",
        "Tema" to "APARIENCIA", "Resumen semanal" to "INFORME SEMANAL", "Totales" to "INVENTARIO", "Gimnasio" to "ACONDICIONAMIENTO",
        "Cifras" to "LECTURAS", "Medicamentos" to "ENFERMERÍA", "Clima" to "CONDICIONES EXTERIORES", "Recordatorios" to "ALARMAS",
        "Respaldo automático" to "COPIA DE SEGURIDAD", "Momentos de este día" to "SUCESOS DEL DÍA", "Este día hace un año" to "HACE UN AÑO",
        "Momentos de la semana" to "SUCESOS DE LA SEMANA", "Registrar" to "ANOTAR",
    )

    private val ORDEN = mapOf(
        "Mundo" to "CAMPAÑAS", "Patrimonio Mundial" to "RELIQUIAS DEL VIEJO MUNDO", "Mapa para rascar" to "TERRITORIO RECONOCIDO", "Ciudades visitadas" to "PLAZAS",
        "Biblioteca de sueños" to "VIGILIAS", "Cartas" to "DESPACHOS", "Escribir una carta" to "REDACTAR DESPACHO", "Carta" to "DESPACHO", "Conocerte" to "HOJA DE SERVICIO", "Brújula política" to "DOCTRINA", "Mapas" to "CARTOGRAFÍA", "Salud" to "INFORME MÉDICO", "Tus apps" to "ARCHIVO DE DISPOSITIVOS", "Otras vidas" to "CRÓNICAS DE OTRAS ERAS", "Logros" to "CONDECORACIONES", "Tu año en historias" to "PARTE ANUAL", "Anoche" to "VIGILIA DE ANOCHE", "Selladas" to "SELLADOS", "Listas para abrir" to "LISTOS PARA ABRIR", "A otras personas" to "A OTROS HERMANOS", "Del pasado" to "ARCHIVO",
        "Hoy" to "PARTE DE HOY", "Línea de tiempo" to "CRONOLOGÍA", "Ahora mismo estoy…" to "ESTADO DEL OPERATIVO",
        "Entender" to "INTELIGENCIA", "Registros especiales" to "OPERACIONES ESPECIALES", "Apariencia" to "BLASÓN", "Avisos y recordatorios" to "TOQUES DE CORNETA",
        "Llenar el día" to "PARTE DE CIERRE", "Biblioteca del diario" to "CRÓNICAS", "Entrada" to "CRÓNICA",
        "Día" to "PARTE DE CAMPO", "Momentos" to "BITÁCORA", "Gastos" to "INTENDENCIA", "Explorar" to "RECONOCIMIENTO", "Más" to "ARSENAL",
        "Hábitos" to "DISCIPLINA", "Cartas al futuro" to "ÓRDENES SELLADAS", "Experimentos personales" to "ENSAYOS", "Cómo leer tus datos" to "CÓDICE", "Guía científica" to "CÓDICE",
        "Cuestionarios" to "EVALUACIÓN", "Prueba de reacción" to "REFLEJOS", "Lugares" to "TERRITORIO", "Datos" to "ARCHIVO",
        "Permisos y sensores" to "SENSORES", "Ajustes" to "CONFIGURACIÓN", "Reportes" to "INFORMES", "Bienvenido" to "INICIACIÓN",
        "Calendario" to "CALENDARIO DE CAMPAÑA", "Obras" to "ARCHIVO DE SABERES", "Tu año" to "CAMPAÑA ANUAL", "Diario" to "CRÓNICA",
        "Predicción" to "PREVISIÓN", "Lo que registró el teléfono" to "TELEMETRÍA", "Mapa" to "MAPA TÁCTICO", "Mapa emocional" to "MAPA TÁCTICO",
        "Tablero" to "PUESTO DE MANDO", "Tu día" to "CRONOLOGÍA 24 H", "Tu día en 24 horas" to "CRONOLOGÍA 24 H", "Te falta hoy" to "OBJETIVOS PENDIENTES",
        "Datos que faltan" to "TELEMETRÍA PERDIDA", "Tema" to "BLASÓN", "Resumen semanal" to "INFORME SEMANAL", "Totales" to "BALANCE",
        "Gimnasio" to "ENTRENAMIENTO", "Buscar" to "RASTREO", "Medicamentos" to "BOTIQUÍN", "Clima" to "CONDICIONES", "Ánimo y cuerpo" to "MORAL Y CUERPO",
        "Lo mejor y lo peor" to "VICTORIAS Y DERROTAS", "Momentos de este día" to "BITÁCORA DEL DÍA", "Momentos de la semana" to "BITÁCORA DE LA SEMANA",
        "Respaldo automático" to "COPIA DE RESGUARDO", "Registrar" to "REPORTAR",
    )

    private val LABORATORIO = mapOf(
        "Mundo" to "DISPERSIÓN GEOGRÁFICA", "Patrimonio Mundial" to "MUESTRAS DE REFERENCIA", "Mapa para rascar" to "COBERTURA", "Ciudades visitadas" to "LOCALIDADES",
        "Biblioteca de sueños" to "REGISTRO DE SUEÑOS", "Cartas" to "CORRESPONDENCIA", "Escribir una carta" to "NUEVA CARTA", "Carta" to "CARTA", "Conocerte" to "DATOS DEL SUJETO", "Brújula política" to "ESCALA POLÍTICA", "Mapas" to "MAPAS", "Salud" to "PANEL CLÍNICO", "Tus apps" to "TELEMETRÍA", "Otras vidas" to "SUJETOS DE COMPARACIÓN", "Logros" to "HITOS DEL SUJETO", "Tu año en historias" to "INFORME ANUAL", "Anoche" to "ÚLTIMA NOCHE", "Selladas" to "MUESTRAS SELLADAS", "Listas para abrir" to "LISTAS PARA ABRIR", "A otras personas" to "A OTRAS PERSONAS", "Del pasado" to "ABIERTAS",
        "Hoy" to "REGISTRO DE HOY", "Línea de tiempo" to "SERIE TEMPORAL", "Ahora mismo estoy…" to "OBSERVACIÓN INMEDIATA",
        "Entender" to "RESULTADOS", "Registros especiales" to "PROTOCOLOS ESPECIALES", "Apariencia" to "INTERFAZ", "Avisos y recordatorios" to "ALERTAS",
        "Llenar el día" to "CIERRE DE SESIÓN", "Biblioteca del diario" to "CUADERNOS", "Entrada" to "REGISTRO",
        "Día" to "SESIÓN", "Momentos" to "OBSERVACIONES", "Gastos" to "RECURSOS", "Explorar" to "HALLAZGOS", "Más" to "DIVISIONES",
        "Hábitos" to "VARIABLES", "Cartas al futuro" to "MUESTRAS SELLADAS", "Experimentos personales" to "EXPERIMENTOS", "Cómo leer tus datos" to "MÉTODO", "Guía científica" to "MÉTODO",
        "Cuestionarios" to "INSTRUMENTOS", "Prueba de reacción" to "TIEMPO DE REACCIÓN", "Lugares" to "COORDENADAS", "Datos" to "REPOSITORIO",
        "Ajustes" to "CALIBRACIÓN", "Reportes" to "INFORMES", "Bienvenido" to "INGRESO", "Obras" to "REFERENCIAS", "Tu año" to "SERIE ANUAL",
        "Sueño" to "CICLO DE SUEÑO", "Ánimo y cuerpo" to "ESTADO BASAL", "Diario" to "CUADERNO", "Predicción" to "HIPÓTESIS",
        "Lo que registró el teléfono" to "SENSORES PASIVOS", "Las relaciones más marcadas" to "CORRELACIONES", "Mapa" to "DISTRIBUCIÓN ESPACIAL",
        "Mapa emocional" to "DISTRIBUCIÓN ESPACIAL", "Tablero" to "MONITOR", "Tu día" to "SERIE DE 24 H", "Tu día en 24 horas" to "SERIE DE 24 H", "Te falta hoy" to "MEDICIONES PENDIENTES",
        "Datos que faltan" to "DATOS FALTANTES", "Tema" to "INTERFAZ", "Resumen semanal" to "SÍNTESIS SEMANAL", "Totales" to "ACUMULADOS",
        "Gimnasio" to "CARGA FÍSICA", "Buscar" to "CONSULTA", "Cifras" to "MEDIDAS", "Medicamentos" to "FÁRMACOS", "Momentos de este día" to "OBSERVACIONES DEL DÍA",
        "Momentos de la semana" to "OBSERVACIONES DE LA SEMANA", "Este día hace un año" to "CONTROL: HACE UN AÑO", "Registrar" to "REGISTRAR",
    )

    private val COLONOS = mapOf(
        "Mundo" to "Tierras recorridas", "Patrimonio Mundial" to "Maravillas del camino", "Mapa para rascar" to "Mapa del explorador", "Ciudades visitadas" to "Poblados",
        "Biblioteca de sueños" to "Lo que soñé", "Cartas" to "Correo del pueblo", "Escribir una carta" to "Escribir una carta", "Carta" to "Carta", "Conocerte" to "Del vecino", "Brújula política" to "Asamblea del pueblo", "Mapas" to "Mapas de la comarca", "Salud" to "La enfermería del pueblo", "Tus apps" to "Lo que dicen tus aparatos", "Otras vidas" to "Otras vidas", "Logros" to "Medallas del pueblo", "Tu año en historias" to "El almanaque del año", "Anoche" to "Anoche", "Selladas" to "Selladas", "Listas para abrir" to "Listas para abrir", "A otras personas" to "A los vecinos", "Del pasado" to "Del pasado",
        "Hoy" to "La jornada de hoy", "Línea de tiempo" to "Crónica del pueblo", "Ahora mismo estoy…" to "En este momento…",
        "Entender" to "El almanaque", "Registros especiales" to "Otras faenas", "Apariencia" to "Estilo", "Avisos y recordatorios" to "Campanadas",
        "Llenar el día" to "Al caer la noche", "Biblioteca del diario" to "Gacetas del vecino", "Entrada" to "Gaceta",
        "Día" to "Jornada", "Momentos" to "Sucesos", "Gastos" to "Cuentas", "Explorar" to "Patrulla", "Más" to "Cabildo", "Hábitos" to "Faenas",
        "Cartas al futuro" to "Cartas al porvenir", "Experimentos personales" to "Ensayos", "Cómo leer tus datos" to "Almanaque", "Guía científica" to "Almanaque", "Cuestionarios" to "Encuestas",
        "Prueba de reacción" to "Reflejos", "Lugares" to "Asentamientos", "Datos" to "Archivo del pueblo", "Permisos y sensores" to "Vigías",
        "Ajustes" to "Ordenanzas", "Reportes" to "Gacetas", "Bienvenido" to "Bienvenido, vecino", "Tu año" to "El año", "Sueño" to "Descanso",
        "Ánimo y cuerpo" to "Ánimo y salud", "Predicción" to "Pronóstico", "Lo que registró el teléfono" to "Lo que anotaron los vigías",
        "Mapa" to "Mapa de la región", "Mapa emocional" to "Mapa de la región", "Tablero" to "Tablón de anuncios", "Tu día" to "La jornada entera", "Tu día en 24 horas" to "La jornada entera",
        "Te falta hoy" to "Pendientes", "Datos que faltan" to "Huecos", "Tema" to "Estilo", "Resumen semanal" to "La semana",
        "Totales" to "Cuentas del pueblo", "Gimnasio" to "Faena física", "Cifras" to "Números", "Medicamentos" to "Botica", "Clima" to "El tiempo",
        "Momentos de este día" to "Sucesos del día", "Momentos de la semana" to "Sucesos de la semana", "Registrar" to "Anotar",
    )

    private val NAPOLEON = mapOf(
        "Mundo" to "CAMPAÑAS", "Patrimonio Mundial" to "MONUMENTOS", "Mapa para rascar" to "CONQUISTAS", "Ciudades visitadas" to "GUARNICIONES",
        "Biblioteca de sueños" to "SONGES", "Cartas" to "CORRESPONDANCE", "Escribir una carta" to "DICTAR UN DESPACHO", "Carta" to "DESPACHO", "Conocerte" to "HOJA DE SERVICIOS", "Brújula política" to "CONSEJO DE ESTADO", "Mapas" to "ATLAS DE CAMPAÑA", "Salud" to "PARTE MÉDICO", "Tus apps" to "ESTADÍSTICA", "Otras vidas" to "OTRAS VIDAS", "Logros" to "CONDECORACIONES", "Tu año en historias" to "BOLETÍN DEL AÑO", "Anoche" to "LA NOCHE PASADA", "Selladas" to "SELLADAS", "Listas para abrir" to "LISTAS PARA ABRIR", "A otras personas" to "A OTROS", "Del pasado" to "ARCHIVO",
        "Hoy" to "DESPACHO DE HOY", "Línea de tiempo" to "DIARIO DE CAMPAÑA", "Ahora mismo estoy…" to "PARTE INMEDIATO",
        "Día" to "BOLETÍN DEL DÍA", "Diario" to "MEMORIAS", "Gastos" to "TESORERÍA", "Entender" to "ESTADO MAYOR", "Más" to "GABINETE",
        "Llenar el día" to "DESPACHO NOCTURNO", "Biblioteca del diario" to "MEMORIAL", "Entrada" to "MEMORIA", "Tablero" to "PARTE DE CAMPAÑA",
        "Te falta hoy" to "ÓRDENES PENDIENTES", "Hábitos" to "CÓDIGO", "Sueño" to "VIVAC", "Ánimo y cuerpo" to "MORAL DE LA TROPA", "Predicción" to "PRONÓSTICO",
        "Calendario" to "CALENDARIO DE CAMPAÑA", "Apariencia" to "ESTANDARTE", "Tu año" to "CAMPAÑA DEL AÑO", "Totales" to "BALANCE DEL IMPERIO",
        "Obras" to "BIBLIOTECA DE CAMPAÑA", "Lugares" to "PLAZAS", "Mapa" to "TEATRO DE OPERACIONES", "Datos que faltan" to "BAJAS", "Hitos" to "VICTORIAS",
        "Momentos de este día" to "DESPACHOS DEL DÍA", "Resumen semanal" to "BOLETÍN SEMANAL", "Tu día" to "LAS VEINTICUATRO HORAS", "Tu día en 24 horas" to "LAS VEINTICUATRO HORAS",
        "Registros especiales" to "CAMPAÑAS ESPECIALES", "Avisos y recordatorios" to "TOQUES DE TAMBOR", "Contexto" to "CIRCUNSTANCIAS",
    )
    /** La Revolución: el vocabulario de 1789–1794 (asignados, espíritu público, salud pública, las jornadas, la Asamblea…). */
    private val REVOLUCION = mapOf(
        "Mundo" to "DEPARTAMENTOS", "Patrimonio Mundial" to "PATRIMONIO NACIONAL", "Mapa para rascar" to "MAPA DE LOS DEPARTAMENTOS", "Ciudades visitadas" to "COMUNAS",
        "Biblioteca de sueños" to "CUADERNO DE SUEÑOS", "Cartas" to "CORRESPONDENCIA", "Escribir una carta" to "ESCRIBIR A LA POSTERIDAD", "Carta" to "CARTA",
        "Conocerte" to "EXAMEN DEL CIUDADANO", "Brújula política" to "LA ASAMBLEA", "Mapas" to "ATLAS NACIONAL", "Salud" to "SALUD PÚBLICA", "Tus apps" to "INVENTARIO",
        "Otras vidas" to "OTROS CIUDADANOS", "Logros" to "HONORES CÍVICOS", "Tu año en historias" to "ANALES DEL AÑO", "Anoche" to "LA NOCHE PASADA",
        "Selladas" to "LACRADAS", "Listas para abrir" to "LISTAS PARA ABRIR", "A otras personas" to "A LOS CIUDADANOS", "Del pasado" to "ARCHIVOS",
        "Hoy" to "CAHIER DE HOY", "Línea de tiempo" to "CRÓNICA", "Ahora mismo estoy…" to "EN ESTE INSTANTE…",
        "Día" to "CAHIER DEL DÍA", "Diario" to "CUADERNOS", "Gastos" to "ASIGNADOS", "Entender" to "LAS LUCES", "Más" to "EL CLUB",
        "Llenar el día" to "VELADA", "Biblioteca del diario" to "ARCHIVO NACIONAL", "Entrada" to "CAHIER", "Tablero" to "TABLERO CÍVICO",
        "Te falta hoy" to "ORDEN DEL DÍA", "Hábitos" to "VIRTUDES CÍVICAS", "Sueño" to "REPOSO", "Ánimo y cuerpo" to "ESPÍRITU PÚBLICO", "Predicción" to "PRONÓSTICO",
        "Calendario" to "CALENDARIO REPUBLICANO", "Apariencia" to "ESCARAPELA", "Tu año" to "EL AÑO REPUBLICANO", "Totales" to "BALANCE DE LA NACIÓN",
        "Obras" to "BIBLIOTECA DEL PUEBLO", "Lugares" to "SECCIONES", "Mapa" to "MAPA DE LA NACIÓN", "Datos que faltan" to "CASILLAS VACÍAS", "Hitos" to "JORNADAS",
        "Momentos de este día" to "SUCESOS DEL DÍA", "Resumen semanal" to "BOLETÍN SEMANAL", "Tu día" to "LAS VEINTICUATRO HORAS", "Tu día en 24 horas" to "LAS VEINTICUATRO HORAS",
        "Registros especiales" to "REGISTROS CÍVICOS", "Avisos y recordatorios" to "TOQUE DE REBATO", "Contexto" to "CIRCUNSTANCIAS", "Medicamentos" to "BOTICA",
        "Ajustes" to "DECRETOS", "Datos" to "ARCHIVOS", "Privacidad" to "SECRETO", "Reportes" to "INFORMES", "Experimentos personales" to "ENSAYOS",
        "Cómo leer tus datos" to "ENCICLOPEDIA", "Guía científica" to "ENCICLOPEDIA", "Cartas al futuro" to "CARTAS A LA POSTERIDAD",
    )

    /** Tierras Antiguas: el vocabulario de una compañía de viaje (crónica, travesía, salones, runas, lunas). Todo inventado. */
    private val ANTIGUAS = mapOf(
        "Mundo" to "LAS TIERRAS", "Patrimonio Mundial" to "MARAVILLAS ANTIGUAS", "Mapa para rascar" to "MAPA DE LAS TIERRAS", "Ciudades visitadas" to "VILLAS Y ALDEAS",
        "Biblioteca de sueños" to "LIBRO DE VISIONES", "Cartas" to "MISIVAS", "Escribir una carta" to "ESCRIBIR UNA MISIVA", "Carta" to "MISIVA",
        "Conocerte" to "EL VIAJERO", "Brújula política" to "EL CONSEJO", "Mapas" to "CARTOGRAFÍA", "Salud" to "EL CUERPO DEL VIAJERO", "Tus apps" to "INVENTARIO",
        "Otras vidas" to "OTRAS VIDAS", "Logros" to "HAZAÑAS", "Tu año en historias" to "CRÓNICA DEL AÑO", "Anoche" to "LA NOCHE PASADA",
        "Selladas" to "LACRADAS", "Listas para abrir" to "LISTAS PARA ABRIR", "A otras personas" to "A LA COMPAÑÍA", "Del pasado" to "DEL PASADO",
        "Hoy" to "LA JORNADA", "Línea de tiempo" to "EL CAMINO DEL DÍA", "Ahora mismo estoy…" to "EN ESTE MOMENTO…",
        "Día" to "CRÓNICA DEL DÍA", "Diario" to "CRÓNICAS", "Gastos" to "MONEDERO", "Entender" to "EL SABER", "Más" to "EL SALÓN",
        "Llenar el día" to "JUNTO AL FUEGO", "Biblioteca del diario" to "ARCHIVO DE CRÓNICAS", "Entrada" to "CRÓNICA", "Tablero" to "MESA DE MAPAS",
        "Te falta hoy" to "TAREAS DEL DÍA", "Hábitos" to "JURAMENTOS", "Sueño" to "VIGILIA", "Ánimo y cuerpo" to "TEMPLE Y FUERZAS", "Predicción" to "PRESAGIO",
        "Calendario" to "LAS LUNAS", "Apariencia" to "BLASÓN", "Tu año" to "EL AÑO DE LA TRAVESÍA", "Totales" to "EL TESORO",
        "Obras" to "BIBLIOTECA DEL SALÓN", "Lugares" to "LUGARES", "Mapa" to "MAPA DE LA TRAVESÍA", "Datos que faltan" to "LO QUE FALTA", "Hitos" to "HITOS DEL CAMINO",
        "Momentos de este día" to "SUCESOS DEL DÍA", "Resumen semanal" to "CRÓNICA DE LA SEMANA", "Tu día" to "LAS HORAS DEL DÍA", "Tu día en 24 horas" to "LAS HORAS DEL DÍA",
        "Registros especiales" to "REGISTROS", "Avisos y recordatorios" to "CUERNOS Y CAMPANAS", "Contexto" to "CIRCUNSTANCIAS", "Medicamentos" to "POCIONES",
        "Ajustes" to "EL TALLER", "Datos" to "ARCHIVOS", "Privacidad" to "LO SECRETO", "Reportes" to "PERGAMINOS", "Experimentos personales" to "PRUEBAS",
        "Cómo leer tus datos" to "EL SABER ANTIGUO", "Guía científica" to "EL SABER ANTIGUO", "Cartas al futuro" to "MISIVAS AL PORVENIR", "Notas" to "APUNTES DE VIAJE", "Metas" to "LAS GESTAS POR VENIR",
    )

    private val BOTANICO = mapOf(
        "Mundo" to "Expediciones", "Patrimonio Mundial" to "Maravillas del mundo", "Mapa para rascar" to "Mapa de expediciones", "Ciudades visitadas" to "Estaciones",
        "Biblioteca de sueños" to "Herbario de sueños", "Cartas" to "Correspondencia del naturalista", "Escribir una carta" to "Escribir una carta", "Carta" to "Carta", "Conocerte" to "Ficha del naturalista", "Brújula política" to "Brújula del naturalista", "Mapas" to "Atlas", "Salud" to "Fisiología", "Tus apps" to "Instrumentos", "Otras vidas" to "Otros ejemplares", "Logros" to "Vitrina de hallazgos", "Tu año en historias" to "Catálogo del año", "Anoche" to "Flora de anoche", "Selladas" to "Semillas guardadas", "Listas para abrir" to "Listas para brotar", "A otras personas" to "Cartas a otros", "Del pasado" to "Prensadas",
        "Hoy" to "La lámina de hoy", "Línea de tiempo" to "Diario de campo", "Ahora mismo estoy…" to "Observación inmediata",
        "Día" to "Herbario del día", "Diario" to "Cuaderno de campo", "Gastos" to "Semillas y cosecha", "Entender" to "Taxonomía", "Más" to "Invernadero",
        "Llenar el día" to "Al anochecer", "Biblioteca del diario" to "El herbario", "Entrada" to "Lámina", "Tablero" to "Mediciones del jardín",
        "Te falta hoy" to "Por observar", "Hábitos" to "Cuidados", "Sueño" to "Latencia", "Ánimo y cuerpo" to "Ánimo y organismo", "Hitos" to "Floraciones",
        "Apariencia" to "Láminas", "Obras" to "Biblioteca natural", "Mapa" to "Hábitats", "Lugares" to "Hábitats", "Totales" to "Inventario de especies",
        "Momentos de este día" to "Observaciones del día", "Datos que faltan" to "Ejemplares perdidos", "Resumen semanal" to "La semana en el jardín",
        "Avisos y recordatorios" to "Cantos de pájaro", "Contexto" to "Condiciones", "Calendario" to "Fenología", "Tu año" to "Las estaciones",
    )
    private val GOTICO = mapOf(
        "Mundo" to "El orbe", "Patrimonio Mundial" to "Catedrales y maravillas", "Mapa para rascar" to "Mapa dorado", "Ciudades visitadas" to "Ciudades santas",
        "Biblioteca de sueños" to "Visiones", "Cartas" to "Epístolas", "Escribir una carta" to "Escribir una epístola", "Carta" to "Epístola", "Conocerte" to "Examen de vida", "Brújula política" to "Del gobierno", "Mapas" to "Mapamundi", "Salud" to "Regimiento de salud", "Tus apps" to "Los artificios", "Otras vidas" to "Vidas de otros", "Logros" to "Reliquias", "Tu año en historias" to "Annales", "Anoche" to "Maitines", "Selladas" to "Selladas", "Listas para abrir" to "Listas para abrir", "A otras personas" to "A otras almas", "Del pasado" to "Reliquias",
        "Hoy" to "La vidriera de hoy", "Línea de tiempo" to "Crónica de la capilla", "Ahora mismo estoy…" to "En esta hora…",
        "Día" to "Laudes", "Diario" to "Breviario", "Gastos" to "Tesoro", "Entender" to "Vitrales", "Más" to "Sacristía",
        "Llenar el día" to "Vísperas", "Biblioteca del diario" to "Cartulario", "Entrada" to "Folio", "Tablero" to "Rosetón",
        "Te falta hoy" to "Oficio pendiente", "Hábitos" to "Regla", "Sueño" to "Maitines", "Ánimo y cuerpo" to "Alma y cuerpo", "Hitos" to "Milagros",
        "Apariencia" to "Vidrieras", "Obras" to "Librería", "Mapa" to "Peregrinaciones", "Lugares" to "Santuarios", "Totales" to "Tesoro de la capilla",
        "Momentos de este día" to "Horas del día", "Datos que faltan" to "Vidrios rotos", "Resumen semanal" to "La semana",
        "Avisos y recordatorios" to "Campanas", "Contexto" to "Circunstancias", "Calendario" to "Santoral", "Tu año" to "Año litúrgico",
    )
    private val ESPACIO = mapOf(
        "Mundo" to "ÓRBITA TERRESTRE", "Patrimonio Mundial" to "PUNTOS DE INTERÉS", "Mapa para rascar" to "PLANETA EXPLORADO", "Ciudades visitadas" to "BASES",
        "Biblioteca de sueños" to "SUEÑOS EN ÓRBITA", "Cartas" to "TRANSMISIONES", "Escribir una carta" to "NUEVA TRANSMISIÓN", "Carta" to "TRANSMISIÓN", "Conocerte" to "PERFIL DE LA TRIPULACIÓN", "Brújula política" to "COORDENADAS POLÍTICAS", "Mapas" to "CARTAS ESTELARES", "Salud" to "SOPORTE VITAL", "Tus apps" to "TELEMETRÍA", "Otras vidas" to "OTRAS TRIPULACIONES", "Logros" to "INSIGNIAS DE MISIÓN", "Tu año en historias" to "BITÁCORA DEL CICLO", "Anoche" to "ÚLTIMA NOCHE", "Selladas" to "CÁPSULAS SELLADAS", "Listas para abrir" to "LISTAS PARA ABRIR", "A otras personas" to "A OTRAS ESTACIONES", "Del pasado" to "RECIBIDAS",
        "Hoy" to "BITÁCORA DE HOY", "Línea de tiempo" to "REGISTRO DE LA MISIÓN", "Ahora mismo estoy…" to "TELEMETRÍA INMEDIATA",
        "Día" to "ÓRBITA DEL DÍA", "Diario" to "BITÁCORA", "Gastos" to "COMBUSTIBLE", "Entender" to "OBSERVATORIO", "Más" to "BASE",
        "Llenar el día" to "REPORTE DE MISIÓN", "Biblioteca del diario" to "ARCHIVO DE VUELO", "Entrada" to "REGISTRO", "Tablero" to "TELEMETRÍA",
        "Te falta hoy" to "SECUENCIA PENDIENTE", "Hábitos" to "RUTINAS", "Sueño" to "PERIODO DE SUEÑO", "Ánimo y cuerpo" to "SIGNOS VITALES", "Hitos" to "HITOS DE MISIÓN",
        "Apariencia" to "CONSOLA", "Obras" to "BIBLIOTECA DE A BORDO", "Mapa" to "CARTA ESTELAR", "Lugares" to "ESTACIONES", "Totales" to "ODÓMETRO",
        "Momentos de este día" to "EVENTOS DEL DÍA", "Datos que faltan" to "SEÑAL PERDIDA", "Resumen semanal" to "INFORME SEMANAL",
        "Avisos y recordatorios" to "ALERTAS", "Contexto" to "CONDICIONES", "Calendario" to "EFEMÉRIDES", "Tu año" to "LA ÓRBITA",
    )
    private val ANDALUS = mapOf(
        "Mundo" to "الرحلة", "Patrimonio Mundial" to "العجائب", "Mapa para rascar" to "خريطة الرحلة", "Ciudades visitadas" to "المدن",
        "Biblioteca de sueños" to "الأحلام", "Cartas" to "الرسائل", "Escribir una carta" to "كتابة رسالة", "Carta" to "رسالة", "Conocerte" to "اعرف نفسك", "Brújula política" to "السياسة", "Mapas" to "الخرائط", "Salud" to "الصحة", "Tus apps" to "الأدوات", "Otras vidas" to "حيوات أخرى", "Logros" to "Preseas", "Tu año en historias" to "Crónica del año", "Anoche" to "البارحة", "Selladas" to "المختومة", "Listas para abrir" to "جاهزة للفتح", "A otras personas" to "إلى الآخرين", "Del pasado" to "من الماضي",
        "Día" to "النهار", "Diario" to "اليوميات", "Gastos" to "بيت المال", "Entender" to "الحكمة", "Más" to "الديوان",
        "Llenar el día" to "المساء", "Biblioteca del diario" to "خزانة الكتب", "Entrada" to "صحيفة", "Tablero" to "الميزان",
        "Te falta hoy" to "ما بقي", "Hábitos" to "العادات", "Sueño" to "النوم", "Ánimo y cuerpo" to "الروح والجسد", "Hitos" to "الأيام",
        "Hoy" to "اليوم", "Línea de tiempo" to "التاريخ", "Ahora mismo estoy…" to "الآن", "Apariencia" to "الزخرفة", "Obras" to "الكتب",
        "Mapa" to "الأطلس", "Totales" to "الحساب", "Calendario" to "التقويم", "Resumen semanal" to "الأسبوع", "Avisos y recordatorios" to "النداء",
    )
    private val MONGOL = mapOf(
        "Mundo" to "ДЭЛХИЙ", "Patrimonio Mundial" to "ДЭЛХИЙН ӨВ", "Mapa para rascar" to "АЯЛЛЫН ЗУРАГ", "Ciudades visitadas" to "ХОТУУД",
        "Biblioteca de sueños" to "ЗҮҮД", "Cartas" to "ЗАХИА", "Escribir una carta" to "ЗАХИА БИЧИХ", "Carta" to "ЗАХИА", "Conocerte" to "ӨӨРИЙГӨӨ ТАНИХ", "Brújula política" to "УЛС ТӨР", "Mapas" to "ГАЗРЫН ЗУРАГ", "Salud" to "ЭРҮҮЛ МЭНД", "Tus apps" to "ХЭРЭГСЭЛ", "Otras vidas" to "БУСАД АМЬДРАЛ", "Logros" to "МЕДАЛЬ", "Tu año en historias" to "ЖИЛИЙН ТОЙМ", "Anoche" to "ӨЧИГДӨР ШӨНӨ", "Selladas" to "ЛАЦТАЙ", "Listas para abrir" to "НЭЭХЭД БЭЛЭН", "A otras personas" to "БУСАДАД", "Del pasado" to "ӨНГӨРСӨНӨӨС",
        "Día" to "ӨДӨР", "Diario" to "ТЭМДЭГЛЭЛ", "Gastos" to "ЗАРДАЛ", "Entender" to "МЭДЛЭГ", "Más" to "ӨРГӨӨ",
        "Llenar el día" to "ОРОЙ", "Biblioteca del diario" to "СУДАР", "Entrada" to "ХУУДАС", "Tablero" to "ХЭМЖҮҮР",
        "Te falta hoy" to "ҮЛДСЭН АЖИЛ", "Hábitos" to "ЗАН ЗАНШИЛ", "Sueño" to "НОЙР", "Ánimo y cuerpo" to "СЭТГЭЛ, БИЕ", "Hitos" to "ГАВЬЯА",
        "Hoy" to "ӨНӨӨДӨР", "Línea de tiempo" to "ШАСТИР", "Ahora mismo estoy…" to "ОДОО", "Apariencia" to "ХЭЭ", "Obras" to "НОМ",
        "Mapa" to "ГАЗРЫН ЗУРАГ", "Totales" to "НИЙТ", "Calendario" to "ЛИТ", "Resumen semanal" to "ДОЛОО ХОНОГ", "Avisos y recordatorios" to "БҮРЭЭ",
        "Lugares" to "НУТАГ", "Contexto" to "НӨХЦӨЛ",
    )
    private val RENACIMIENTO = mapOf(
        "Mundo" to "Il mondo", "Patrimonio Mundial" to "Meraviglie", "Mapa para rascar" to "Mappamondo", "Ciudades visitadas" to "Città",
        "Biblioteca de sueños" to "Sogni", "Cartas" to "Lettere", "Escribir una carta" to "Scrivere una lettera", "Carta" to "Lettera", "Conocerte" to "Conosci te stesso", "Brújula política" to "Bussola politica", "Mapas" to "Mappe", "Salud" to "Salute", "Tus apps" to "Strumenti", "Otras vidas" to "Altre vite", "Logros" to "Trionfi", "Tu año en historias" to "Annali", "Anoche" to "Stanotte", "Selladas" to "Sigillate", "Listas para abrir" to "Pronte da aprire", "A otras personas" to "Ad altri", "Del pasado" to "Dal passato",
        "Hoy" to "Il foglio d'oggi", "Línea de tiempo" to "Ricordanze", "Ahora mismo estoy…" to "In questo momento…",
        "Día" to "Giornata", "Diario" to "Taccuino", "Gastos" to "Libro dei conti", "Entender" to "Studio", "Más" to "Bottega",
        "Llenar el día" to "A sera", "Biblioteca del diario" to "I codici", "Entrada" to "Foglio", "Tablero" to "Misure",
        "Te falta hoy" to "Da fare", "Hábitos" to "Esercizi", "Sueño" to "Il sonno", "Ánimo y cuerpo" to "Animo e corpo", "Hitos" to "Invenzioni",
        "Apariencia" to "Disegno", "Obras" to "Libreria", "Mapa" to "Mappa", "Lugares" to "Luoghi", "Totales" to "Inventario",
        "Momentos de este día" to "Note del giorno", "Datos que faltan" to "Lacune", "Resumen semanal" to "La settimana",
        "Avisos y recordatorios" to "Campane", "Contexto" to "Circostanze", "Calendario" to "Calendario", "Tu año" to "L'anno",
    )
    private val NAVIDAD = mapOf(
        "Mundo" to "Viajes", "Patrimonio Mundial" to "Maravillas", "Mapa para rascar" to "Mapa de los viajes", "Ciudades visitadas" to "Ciudades",
        "Biblioteca de sueños" to "Sueños de Nochebuena", "Cartas" to "Cartas", "Escribir una carta" to "Escribir una carta", "Carta" to "Carta", "Conocerte" to "Quién eres", "Brújula política" to "Brújula política", "Mapas" to "Mapas del viaje", "Salud" to "Salud", "Tus apps" to "Tus apps", "Otras vidas" to "Otras Navidades", "Logros" to "Regalos ganados", "Tu año en historias" to "Lo que trajo el año", "Anoche" to "Anoche", "Selladas" to "Envueltas para después", "Listas para abrir" to "Listas para abrir", "A otras personas" to "Para los seres queridos", "Del pasado" to "Navidades pasadas",
        "Hoy" to "La carta de hoy", "Línea de tiempo" to "Los días del año", "Ahora mismo estoy…" to "Ahora mismo estoy…",
        "Día" to "El día", "Diario" to "Cartas", "Gastos" to "Regalos y gastos", "Entender" to "Balance del año", "Más" to "El taller",
        "Llenar el día" to "Junto al fuego", "Biblioteca del diario" to "Cartas guardadas", "Entrada" to "Carta", "Tablero" to "El árbol",
        "Te falta hoy" to "Lista de pendientes", "Hábitos" to "Propósitos", "Sueño" to "Noche de paz", "Ánimo y cuerpo" to "Ánimo y salud",
        "Hitos" to "Regalos del día", "Apariencia" to "Adornos", "Obras" to "Cuentos y películas", "Calendario" to "Calendario de Adviento",
        "Datos que faltan" to "Lo que falta", "Momentos de este día" to "Momentos del día", "Avisos y recordatorios" to "Campanas",
    )
    private val SEDA = mapOf(
        "Mundo" to "Caravanas", "Patrimonio Mundial" to "Maravillas de la ruta", "Mapa para rascar" to "Mapa del mercader", "Ciudades visitadas" to "Ciudades de paso",
        "Biblioteca de sueños" to "Sueños de la caravana", "Cartas" to "Cartas de la ruta", "Escribir una carta" to "Escribir una carta", "Carta" to "Carta", "Conocerte" to "El viajero", "Brújula política" to "Consejo del caravasar", "Mapas" to "Mapas de la ruta", "Salud" to "Salud del viajero", "Tus apps" to "Instrumentos", "Otras vidas" to "Otros viajeros", "Logros" to "Tesoros", "Tu año en historias" to "Bitácora del año", "Anoche" to "Anoche", "Selladas" to "Selladas", "Listas para abrir" to "Listas para abrir", "A otras personas" to "A otros viajeros", "Del pasado" to "Del pasado",
        "Hoy" to "El rollo de hoy", "Línea de tiempo" to "El camino recorrido", "Ahora mismo estoy…" to "En esta etapa…",
        "Día" to "Jornada de caravana", "Diario" to "Bitácora del viajero", "Gastos" to "Mercaderías", "Entender" to "El astrolabio", "Más" to "El caravasar",
        "Llenar el día" to "Bajo las estrellas", "Biblioteca del diario" to "Rollos de seda", "Entrada" to "Rollo", "Tablero" to "Carga de la caravana",
        "Te falta hoy" to "Pendientes del camino", "Hábitos" to "Ritos", "Sueño" to "Reposo", "Ánimo y cuerpo" to "Espíritu y cuerpo", "Hitos" to "Maravillas",
        "Apariencia" to "Telas", "Obras" to "Manuscritos", "Mapa" to "La ruta", "Lugares" to "Oasis", "Totales" to "Inventario del mercader",
        "Momentos de este día" to "Relatos del día", "Avisos y recordatorios" to "Gongs",
    )
    private val EDO = mapOf(
        "Mundo" to "Los caminos del mundo", "Patrimonio Mundial" to "Lugares famosos del mundo", "Mapa para rascar" to "Mapa de las provincias", "Ciudades visitadas" to "Postas visitadas",
        "Biblioteca de sueños" to "Sueños de la posada", "Cartas" to "Correo a pie", "Escribir una carta" to "Escribir con el pincel", "Carta" to "Carta de correo", "Conocerte" to "El caminante",
        "Brújula política" to "Consejo de la aldea", "Mapas" to "Mapas del camino", "Salud" to "Libro del herbolario", "Tus apps" to "Útiles de viaje", "Otras vidas" to "Otros caminantes",
        "Logros" to "Sellos del templo", "Tu año en historias" to "Estampas del año", "Anoche" to "La noche en la posada", "Selladas" to "Con el sello puesto",
        "Listas para abrir" to "Cartas por abrir", "A otras personas" to "A otros caminantes", "Del pasado" to "De estaciones pasadas",
        "Hoy" to "La posta de hoy", "Línea de tiempo" to "El camino recorrido de posta en posta", "Ahora mismo estoy…" to "En esta posta…",
        "Día" to "Jornada del camino", "Diario" to "Cuaderno del caminante", "Gastos" to "Cuentas del monedero", "Entender" to "El ábaco", "Más" to "La posada",
        "Llenar el día" to "Junto al farolillo", "Biblioteca del diario" to "Cuadernos cosidos", "Entrada" to "Hoja de washi", "Tablero" to "Tablón de la posta",
        "Te falta hoy" to "Lo que falta para la siguiente posta", "Hábitos" to "Costumbres", "Sueño" to "Noche en el futón", "Ánimo y cuerpo" to "Corazón y cuerpo", "Hitos" to "Lugares famosos",
        "Apariencia" to "Telas y tintes", "Obras" to "Libros y estampas", "Mapa" to "El camino real", "Lugares" to "Postas", "Totales" to "Cuenta del ábaco",
        "Momentos de este día" to "Estampas del día", "Avisos y recordatorios" to "Campanas del templo",
    )
    private val URSS = mapOf(
        "Mundo" to "INTERNACIONAL", "Patrimonio Mundial" to "PATRIMONIO DE LOS PUEBLOS", "Mapa para rascar" to "MAPA DE LA AMISTAD", "Ciudades visitadas" to "CIUDADES",
        "Biblioteca de sueños" to "СНЫ · SUEÑOS", "Cartas" to "CORRESPONDENCIA", "Escribir una carta" to "ESCRIBIR UNA CARTA", "Carta" to "CARTA", "Conocerte" to "EXPEDIENTE PERSONAL", "Brújula política" to "CONCIENCIA POLÍTICA", "Mapas" to "MAPAS", "Salud" to "PARTE MÉDICO", "Tus apps" to "ESTADÍSTICA", "Otras vidas" to "OTRAS VIDAS", "Logros" to "ORDEN DEL MÉRITO", "Tu año en historias" to "BALANCE DEL PLAN", "Anoche" to "ANOCHE", "Selladas" to "SELLADAS", "Listas para abrir" to "LISTAS PARA ABRIR", "A otras personas" to "A LOS CAMARADAS", "Del pasado" to "DEL PASADO",
        "Hoy" to "INFORME DE HOY", "Línea de tiempo" to "CRÓNICA DEL PLAN", "Ahora mismo estoy…" to "ESTADO DEL CAMARADA",
        "Día" to "PLAN DEL DÍA", "Diario" to "DIARIO DEL CAMARADA", "Gastos" to "ECONOMÍA PLANIFICADA", "Entender" to "ESTADÍSTICA", "Más" to "COMISARIADO",
        "Llenar el día" to "INFORME DE JORNADA", "Biblioteca del diario" to "ARCHIVO DEL PUEBLO", "Entrada" to "INFORME", "Tablero" to "CUMPLIMIENTO DEL PLAN",
        "Te falta hoy" to "NORMAS PENDIENTES", "Hábitos" to "NORMAS", "Sueño" to "DESCANSO", "Ánimo y cuerpo" to "MORAL Y SALUD", "Hitos" to "HAZAÑAS",
        "Apariencia" to "AGITPROP", "Obras" to "CULTURA", "Mapa" to "MAPA DE LA UNIÓN", "Totales" to "PRODUCCIÓN TOTAL", "Datos que faltan" to "FALTAS AL PLAN",
        "Resumen semanal" to "PLAN SEMANAL", "Momentos de este día" to "INFORMES DEL DÍA", "Avisos y recordatorios" to "SIRENAS",
    )
}
