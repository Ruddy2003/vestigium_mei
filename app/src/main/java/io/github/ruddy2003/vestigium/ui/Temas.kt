package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas

/**
 * Nombres de pantallas y secciones de los seis temas nuevos. Todo es invención propia:
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
     * Calendario republicano francés, el de los primeros años de Napoleón: doce meses de 30 días que empiezan
     * cerca del equinoccio de otoño, más los días complementarios. (Aproximado: el año empieza el 22 de septiembre.)
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

    /** «faltan N días para …», o el nombre del día cuando llega. */
    fun cuentaAtras(d: java.time.LocalDate, mes: Int, dia: Int, nombre: String, corto: String): String {
        var meta = java.time.LocalDate.of(d.year, mes, dia)
        if (d.isAfter(meta)) meta = meta.plusYears(1)
        val n = java.time.temporal.ChronoUnit.DAYS.between(d, meta)
        return when (n) { 0L -> "¡$nombre!"; 1L -> "mañana es $corto"; else -> "faltan $n días para $corto" }
    }

    private val MARCAS = mapOf(
        TemaId.NAPOLEON to mapOf("12-02" to "Austerlitz", "08-15" to "Natalicio", "06-18" to "Waterloo", "03-21" to "Código", "07-14" to "Bastilla"),
        TemaId.MUERTOS to mapOf("10-31" to "Altar", "11-01" to "Angelitos", "11-02" to "Muertos"),
        TemaId.NAVIDAD to mapOf("12-12" to "Guadalupe", "12-16" to "Posadas", "12-24" to "Nochebuena", "12-25" to "Navidad", "12-28" to "Inocentes", "12-31" to "Año nuevo", "01-06" to "Reyes"),
        TemaId.SEDA to mapOf("03-21" to "Nowruz", "09-21" to "Luna"),
        TemaId.URSS to mapOf("05-01" to "1º mayo", "04-12" to "Cosmos", "05-09" to "Victoria", "11-07" to "Octubre", "03-08" to "Mujer"),
    )
    fun marca(tema: TemaId, fecha: String): String? = MARCAS[tema]?.get(fecha.takeLast(5))

    fun nombre(tema: TemaId, titulo: String): String? = when (tema) {
        TemaId.MEDIEVAL -> MEDIEVAL[titulo]
        TemaId.DISTOPIA -> DISTOPIA[titulo]
        TemaId.REFUGIO -> REFUGIO[titulo]
        TemaId.ORDEN -> ORDEN[titulo]
        TemaId.LABORATORIO -> LABORATORIO[titulo]
        TemaId.COLONOS -> COLONOS[titulo]
        TemaId.NAPOLEON -> NAPOLEON[titulo]
        TemaId.MUERTOS -> MUERTOS[titulo]
        TemaId.NAVIDAD -> NAVIDAD[titulo]
        TemaId.SEDA -> SEDA[titulo]
        TemaId.URSS -> URSS[titulo]
        else -> null
    }

    fun widget(tema: TemaId, clave: String): String {
        val m = when (tema) {
            TemaId.MEDIEVAL -> mapOf("animo" to "Del ánima", "momento" to "Crónica", "gasto" to "Cuentas", "sueno" to "Del sueño", "habitos" to "Regla", "hoy" to "Del día", "cifra" to "Cuenta", "foto" to "Miniatura")
            TemaId.DISTOPIA -> mapOf("animo" to "ESTADO", "momento" to "INCIDENCIA", "gasto" to "RACIÓN", "sueno" to "DESCANSO", "habitos" to "CUOTAS", "hoy" to "PARTE", "cifra" to "CIFRA OFICIAL", "foto" to "EVIDENCIA")
            TemaId.REFUGIO -> mapOf("animo" to "ESTADO", "momento" to "SUCESO", "gasto" to "SUMINISTROS", "sueno" to "DESCANSO", "habitos" to "PROTOCOLOS", "hoy" to "BITÁCORA", "cifra" to "LECTURA", "foto" to "FOTO")
            TemaId.ORDEN -> mapOf("animo" to "MORAL", "momento" to "BITÁCORA", "gasto" to "INTENDENCIA", "sueno" to "DESCANSO", "habitos" to "DISCIPLINA", "hoy" to "PARTE", "cifra" to "CIFRA", "foto" to "IMAGEN")
            TemaId.LABORATORIO -> mapOf("animo" to "ESTADO", "momento" to "OBSERVACIÓN", "gasto" to "RECURSOS", "sueno" to "SUEÑO", "habitos" to "VARIABLES", "hoy" to "SESIÓN", "cifra" to "MEDIDA", "foto" to "MUESTRA")
            TemaId.COLONOS -> mapOf("animo" to "Ánimo", "momento" to "Sucesos", "gasto" to "Cuentas", "sueno" to "Descanso", "habitos" to "Faenas", "hoy" to "Jornada", "cifra" to "Número", "foto" to "Estampa")
            TemaId.NAPOLEON -> mapOf("animo" to "MORAL", "momento" to "DESPACHO", "gasto" to "TESORERÍA", "sueno" to "VIVAC", "habitos" to "CÓDIGO", "hoy" to "BOLETÍN", "cifra" to "CIFRA", "foto" to "RETRATO")
            TemaId.MUERTOS -> mapOf("animo" to "ÁNIMO", "momento" to "RECUERDO", "gasto" to "MERCADO", "sueno" to "DESCANSO", "habitos" to "COSTUMBRES", "hoy" to "OFRENDA", "cifra" to "CUENTA", "foto" to "RETRATO")
            TemaId.NAVIDAD -> mapOf("animo" to "Ánimo", "momento" to "Momento", "gasto" to "Regalos", "sueno" to "Noche de paz", "habitos" to "Propósitos", "hoy" to "El día", "cifra" to "Número", "foto" to "Postal")
            TemaId.SEDA -> mapOf("animo" to "Espíritu", "momento" to "Relato", "gasto" to "Mercancías", "sueno" to "Reposo", "habitos" to "Ritos", "hoy" to "Jornada", "cifra" to "Cuenta", "foto" to "Estampa")
            TemaId.URSS -> mapOf("animo" to "MORAL", "momento" to "INFORME", "gasto" to "ECONOMÍA", "sueno" to "DESCANSO", "habitos" to "NORMAS", "hoy" to "PLAN", "cifra" to "PRODUCCIÓN", "foto" to "FOTO")
            else -> emptyMap()
        }
        return m[clave] ?: clave.uppercase()
    }

    private val MEDIEVAL = mapOf(
        "Hoy" to "El folio de hoy", "Línea de tiempo" to "Crónica", "Ahora mismo estoy…" to "En este instante…",
        "Entender" to "Glosas", "Registros especiales" to "Otros libros", "Apariencia" to "Estilo", "Avisos y recordatorios" to "Campanas",
        "Llenar el día" to "Completas", "Biblioteca del diario" to "Libro de los días", "Entrada" to "Folio",
        "Día" to "Del día", "Momentos" to "Crónica", "Gastos" to "Libro de cuentas", "Explorar" to "Glosas", "Más" to "Otros folios",
        "Hábitos" to "Regla", "Cartas al futuro" to "Epístolas", "Experimentos personales" to "Pruebas", "Guía científica" to "Tratado",
        "Cuestionarios" to "Cuestiones", "Prueba de reacción" to "Presteza", "Datos" to "Archivo", "Permisos y sensores" to "Sentidos",
        "Ajustes" to "Ordenanzas", "Reportes" to "Anales", "Bienvenido" to "Bien venido", "Escalada" to "Ascensión", "Obras" to "Librería",
        "Tu año" to "Anuario", "Privacidad" to "Secreto", "Sueño" to "Del sueño", "Contexto" to "Circunstancias", "Hitos" to "Memorables",
        "Ánimo y cuerpo" to "Del ánima y del cuerpo", "Lectura del día" to "Lección del día", "Lo mejor y lo peor" to "Lo mejor e lo peor",
        "Medicamentos" to "Remedios", "Seguimiento privado" to "Lo secreto", "Predicción" to "Augurio", "Momentos de este día" to "Crónica del día",
        "Este día hace un año" to "Hogaño y antaño", "Lo que registró el teléfono" to "Lo que asentó el ingenio", "Registrar" to "Asentar",
        "Últimos 30 días" to "Treinta días", "Cobertura" to "Constancia", "Cruzar dos variables" to "Concordancia",
        "Las relaciones más marcadas" to "Concordancias", "Personas, temas e hitos" to "Personas e materias", "Mapa" to "Mapamundi",
        "Mapa emocional" to "Mapamundi del ánima", "Tablero" to "Tabla", "Tu día en 24 horas" to "Las horas del día", "Te falta hoy" to "Lo que resta",
        "Datos que faltan" to "Lagunas", "Tema" to "Estilo", "Recordatorios" to "Avisos", "Resumen semanal" to "La semana", "Totales" to "Suma de todo",
        "Gimnasio" to "Ejercicio de armas", "Análisis" to "Quaestiones", "Cifras" to "Cuenta", "Momentos de la semana" to "Crónica de la semana",
        "Fotos del día" to "Miniaturas", "Exportar" to "Trasladar", "Importar" to "Traer", "Respaldo automático" to "Copia guardada", "Clima" to "Del tiempo",
        "Widgets" to "Tablillas", "Lugares" to "Lugares", "Diario" to "Diario", "Calendario" to "Calendario", "Buscar" to "Buscar",
    )

    private val DISTOPIA = mapOf(
        "Hoy" to "DECLARACIÓN DE HOY", "Línea de tiempo" to "REGISTRO CRONOLÓGICO", "Ahora mismo estoy…" to "ESTADO ACTUAL DEL CIUDADANO",
        "Entender" to "INTELIGENCIA", "Registros especiales" to "REGISTROS ESPECIALES", "Apariencia" to "APARIENCIA", "Avisos y recordatorios" to "CITACIONES",
        "Llenar el día" to "DECLARACIÓN NOCTURNA", "Biblioteca del diario" to "ARCHIVO DE DECLARACIONES", "Entrada" to "DECLARACIÓN",
        "Día" to "PARTE DIARIO", "Momentos" to "INCIDENCIAS", "Gastos" to "RACIONAMIENTO", "Explorar" to "VIGILANCIA", "Más" to "MINISTERIOS",
        "Hábitos" to "CUOTAS", "Cartas al futuro" to "CORRESPONDENCIA SELLADA", "Experimentos personales" to "PRUEBAS AUTORIZADAS",
        "Guía científica" to "DOCTRINA", "Cuestionarios" to "INTERROGATORIOS", "Prueba de reacción" to "PRUEBA DE REFLEJOS", "Lugares" to "ZONAS",
        "Datos" to "ARCHIVO CENTRAL", "Permisos y sensores" to "SENSORES", "Ajustes" to "DIRECTIVAS", "Reportes" to "INFORMES",
        "Bienvenido" to "ALTA DE CIUDADANO", "Calendario" to "PLAN", "Obras" to "MATERIAL APROBADO", "Tu año" to "BALANCE ANUAL",
        "Privacidad" to "CLASIFICADO", "Sueño" to "DESCANSO", "Contexto" to "CIRCUNSTANCIAS", "Hitos" to "HECHOS NOTABLES",
        "Ánimo y cuerpo" to "ESTADO DEL CIUDADANO", "Diario" to "DECLARACIÓN", "Predicción" to "PRONÓSTICO", "Este día hace un año" to "ANTECEDENTES",
        "Lo que registró el teléfono" to "VIGILANCIA AUTOMÁTICA", "Registrar" to "DECLARAR", "Las relaciones más marcadas" to "CORRELACIONES",
        "Mapa" to "ZONIFICACIÓN", "Mapa emocional" to "ZONIFICACIÓN", "Tablero" to "INDICADORES", "Tu día en 24 horas" to "SEGUIMIENTO 24 H",
        "Te falta hoy" to "PENDIENTE DE DECLARAR", "Datos que faltan" to "OMISIONES", "Tema" to "APARIENCIA", "Recordatorios" to "CITACIONES",
        "Resumen semanal" to "INFORME SEMANAL", "Totales" to "ESTADÍSTICA OFICIAL", "Gimnasio" to "ACONDICIONAMIENTO", "Buscar" to "BÚSQUEDA",
        "Cifras" to "CIFRAS OFICIALES", "Fotos del día" to "EVIDENCIA FOTOGRÁFICA", "Medicamentos" to "SUMINISTRO MÉDICO",
        "Seguimiento privado" to "EXPEDIENTE RESERVADO", "Momentos de la semana" to "INCIDENCIAS DE LA SEMANA", "Momentos de este día" to "INCIDENCIAS DEL DÍA",
        "Lo mejor y lo peor" to "MÉRITOS Y FALTAS", "Personas, temas e hitos" to "PERSONAS DE INTERÉS", "Respaldo automático" to "COPIA DE SEGURIDAD",
    )

    private val REFUGIO = mapOf(
        "Hoy" to "ENTRADA DE HOY", "Línea de tiempo" to "REGISTRO", "Ahora mismo estoy…" to "ESTADO ACTUAL",
        "Entender" to "ANÁLISIS", "Registros especiales" to "REGISTROS ESPECIALES", "Apariencia" to "APARIENCIA", "Avisos y recordatorios" to "ALARMAS",
        "Llenar el día" to "INFORME DE CIERRE", "Biblioteca del diario" to "DIARIOS DEL RESIDENTE", "Entrada" to "REGISTRO",
        "Día" to "BITÁCORA", "Momentos" to "SUCESOS", "Gastos" to "SUMINISTROS", "Explorar" to "SUPERFICIE", "Más" to "TERMINAL",
        "Hábitos" to "PROTOCOLOS", "Cartas al futuro" to "CÁPSULA DEL TIEMPO", "Experimentos personales" to "PRUEBAS", "Guía científica" to "MANUAL",
        "Cuestionarios" to "EVALUACIONES", "Prueba de reacción" to "REFLEJOS", "Lugares" to "SECTORES", "Datos" to "ARCHIVO",
        "Permisos y sensores" to "SENSORES", "Ajustes" to "MANTENIMIENTO", "Reportes" to "INFORMES", "Bienvenido" to "BIENVENIDO, RESIDENTE",
        "Tu año" to "EL AÑO", "Sueño" to "TURNO DE DESCANSO", "Ánimo y cuerpo" to "ESTADO DEL RESIDENTE", "Predicción" to "PRONÓSTICO",
        "Lo que registró el teléfono" to "LECTURAS AUTOMÁTICAS", "Mapa" to "MAPA DE SUPERFICIE", "Mapa emocional" to "MAPA DE SUPERFICIE",
        "Tablero" to "PANEL", "Tu día en 24 horas" to "CICLO DE 24 H", "Te falta hoy" to "TAREAS PENDIENTES", "Datos que faltan" to "LECTURAS PERDIDAS",
        "Tema" to "APARIENCIA", "Resumen semanal" to "INFORME SEMANAL", "Totales" to "INVENTARIO", "Gimnasio" to "ACONDICIONAMIENTO",
        "Cifras" to "LECTURAS", "Medicamentos" to "ENFERMERÍA", "Clima" to "CONDICIONES EXTERIORES", "Recordatorios" to "ALARMAS",
        "Respaldo automático" to "COPIA DE SEGURIDAD", "Momentos de este día" to "SUCESOS DEL DÍA", "Este día hace un año" to "HACE UN AÑO",
        "Momentos de la semana" to "SUCESOS DE LA SEMANA", "Registrar" to "ANOTAR",
    )

    private val ORDEN = mapOf(
        "Hoy" to "PARTE DE HOY", "Línea de tiempo" to "CRONOLOGÍA", "Ahora mismo estoy…" to "ESTADO DEL OPERATIVO",
        "Entender" to "INTELIGENCIA", "Registros especiales" to "OPERACIONES ESPECIALES", "Apariencia" to "BLASÓN", "Avisos y recordatorios" to "TOQUES DE CORNETA",
        "Llenar el día" to "PARTE DE CIERRE", "Biblioteca del diario" to "CRÓNICAS", "Entrada" to "CRÓNICA",
        "Día" to "PARTE DE CAMPO", "Momentos" to "BITÁCORA", "Gastos" to "INTENDENCIA", "Explorar" to "RECONOCIMIENTO", "Más" to "ARSENAL",
        "Hábitos" to "DISCIPLINA", "Cartas al futuro" to "ÓRDENES SELLADAS", "Experimentos personales" to "ENSAYOS", "Guía científica" to "CÓDICE",
        "Cuestionarios" to "EVALUACIÓN", "Prueba de reacción" to "REFLEJOS", "Lugares" to "TERRITORIO", "Datos" to "ARCHIVO",
        "Permisos y sensores" to "SENSORES", "Ajustes" to "CONFIGURACIÓN", "Reportes" to "INFORMES", "Bienvenido" to "INICIACIÓN",
        "Calendario" to "CALENDARIO DE CAMPAÑA", "Obras" to "ARCHIVO DE SABERES", "Tu año" to "CAMPAÑA ANUAL", "Diario" to "CRÓNICA",
        "Predicción" to "PREVISIÓN", "Lo que registró el teléfono" to "TELEMETRÍA", "Mapa" to "MAPA TÁCTICO", "Mapa emocional" to "MAPA TÁCTICO",
        "Tablero" to "PUESTO DE MANDO", "Tu día en 24 horas" to "CRONOLOGÍA 24 H", "Te falta hoy" to "OBJETIVOS PENDIENTES",
        "Datos que faltan" to "TELEMETRÍA PERDIDA", "Tema" to "BLASÓN", "Resumen semanal" to "INFORME SEMANAL", "Totales" to "BALANCE",
        "Gimnasio" to "ENTRENAMIENTO", "Buscar" to "RASTREO", "Medicamentos" to "BOTIQUÍN", "Clima" to "CONDICIONES", "Ánimo y cuerpo" to "MORAL Y CUERPO",
        "Lo mejor y lo peor" to "VICTORIAS Y DERROTAS", "Momentos de este día" to "BITÁCORA DEL DÍA", "Momentos de la semana" to "BITÁCORA DE LA SEMANA",
        "Respaldo automático" to "COPIA DE RESGUARDO", "Registrar" to "REPORTAR",
    )

    private val LABORATORIO = mapOf(
        "Hoy" to "Registro de hoy", "Línea de tiempo" to "Serie temporal", "Ahora mismo estoy…" to "Observación inmediata",
        "Entender" to "RESULTADOS", "Registros especiales" to "PROTOCOLOS ESPECIALES", "Apariencia" to "INTERFAZ", "Avisos y recordatorios" to "ALERTAS",
        "Llenar el día" to "CIERRE DE SESIÓN", "Biblioteca del diario" to "CUADERNOS", "Entrada" to "REGISTRO",
        "Día" to "SESIÓN", "Momentos" to "OBSERVACIONES", "Gastos" to "RECURSOS", "Explorar" to "HALLAZGOS", "Más" to "DIVISIONES",
        "Hábitos" to "VARIABLES", "Cartas al futuro" to "MUESTRAS SELLADAS", "Experimentos personales" to "EXPERIMENTOS", "Guía científica" to "MÉTODO",
        "Cuestionarios" to "INSTRUMENTOS", "Prueba de reacción" to "TIEMPO DE REACCIÓN", "Lugares" to "COORDENADAS", "Datos" to "REPOSITORIO",
        "Ajustes" to "CALIBRACIÓN", "Reportes" to "INFORMES", "Bienvenido" to "INGRESO", "Obras" to "REFERENCIAS", "Tu año" to "SERIE ANUAL",
        "Sueño" to "CICLO DE SUEÑO", "Ánimo y cuerpo" to "ESTADO BASAL", "Diario" to "CUADERNO", "Predicción" to "HIPÓTESIS",
        "Lo que registró el teléfono" to "SENSORES PASIVOS", "Las relaciones más marcadas" to "CORRELACIONES", "Mapa" to "DISTRIBUCIÓN ESPACIAL",
        "Mapa emocional" to "DISTRIBUCIÓN ESPACIAL", "Tablero" to "MONITOR", "Tu día en 24 horas" to "SERIE DE 24 H", "Te falta hoy" to "MEDICIONES PENDIENTES",
        "Datos que faltan" to "DATOS FALTANTES", "Tema" to "INTERFAZ", "Resumen semanal" to "SÍNTESIS SEMANAL", "Totales" to "ACUMULADOS",
        "Gimnasio" to "CARGA FÍSICA", "Buscar" to "CONSULTA", "Cifras" to "MEDIDAS", "Medicamentos" to "FÁRMACOS", "Momentos de este día" to "OBSERVACIONES DEL DÍA",
        "Momentos de la semana" to "OBSERVACIONES DE LA SEMANA", "Este día hace un año" to "CONTROL: HACE UN AÑO", "Registrar" to "REGISTRAR",
    )

    private val COLONOS = mapOf(
        "Hoy" to "La jornada de hoy", "Línea de tiempo" to "Crónica del pueblo", "Ahora mismo estoy…" to "En este momento…",
        "Entender" to "El almanaque", "Registros especiales" to "Otras faenas", "Apariencia" to "Estilo", "Avisos y recordatorios" to "Campanadas",
        "Llenar el día" to "Al caer la noche", "Biblioteca del diario" to "Gacetas del vecino", "Entrada" to "Gaceta",
        "Día" to "Jornada", "Momentos" to "Sucesos", "Gastos" to "Cuentas", "Explorar" to "Patrulla", "Más" to "Cabildo", "Hábitos" to "Faenas",
        "Cartas al futuro" to "Cartas al porvenir", "Experimentos personales" to "Ensayos", "Guía científica" to "Almanaque", "Cuestionarios" to "Encuestas",
        "Prueba de reacción" to "Reflejos", "Lugares" to "Asentamientos", "Datos" to "Archivo del pueblo", "Permisos y sensores" to "Vigías",
        "Ajustes" to "Ordenanzas", "Reportes" to "Gacetas", "Bienvenido" to "Bienvenido, vecino", "Tu año" to "El año", "Sueño" to "Descanso",
        "Ánimo y cuerpo" to "Ánimo y salud", "Predicción" to "Pronóstico", "Lo que registró el teléfono" to "Lo que anotaron los vigías",
        "Mapa" to "Mapa de la región", "Mapa emocional" to "Mapa de la región", "Tablero" to "Tablón de anuncios", "Tu día en 24 horas" to "La jornada entera",
        "Te falta hoy" to "Pendientes", "Datos que faltan" to "Huecos", "Tema" to "Estilo", "Resumen semanal" to "La semana",
        "Totales" to "Cuentas del pueblo", "Gimnasio" to "Faena física", "Cifras" to "Números", "Medicamentos" to "Botica", "Clima" to "El tiempo",
        "Momentos de este día" to "Sucesos del día", "Momentos de la semana" to "Sucesos de la semana", "Registrar" to "Anotar",
    )

    private val NAPOLEON = mapOf(
        "Hoy" to "DESPACHO DE HOY", "Línea de tiempo" to "DIARIO DE CAMPAÑA", "Ahora mismo estoy…" to "PARTE INMEDIATO",
        "Día" to "BOLETÍN DEL DÍA", "Diario" to "MEMORIAS", "Gastos" to "TESORERÍA", "Entender" to "ESTADO MAYOR", "Más" to "GABINETE",
        "Llenar el día" to "DESPACHO NOCTURNO", "Biblioteca del diario" to "MEMORIAL", "Entrada" to "MEMORIA", "Tablero" to "PARTE DE CAMPAÑA",
        "Te falta hoy" to "ÓRDENES PENDIENTES", "Hábitos" to "CÓDIGO", "Sueño" to "VIVAC", "Ánimo y cuerpo" to "MORAL DE LA TROPA", "Predicción" to "PRONÓSTICO",
        "Calendario" to "CALENDARIO REPUBLICANO", "Apariencia" to "ESTANDARTE", "Tu año" to "CAMPAÑA DEL AÑO", "Totales" to "BALANCE DEL IMPERIO",
        "Obras" to "BIBLIOTECA DE CAMPAÑA", "Lugares" to "PLAZAS", "Mapa" to "TEATRO DE OPERACIONES", "Datos que faltan" to "BAJAS", "Hitos" to "VICTORIAS",
        "Momentos de este día" to "DESPACHOS DEL DÍA", "Resumen semanal" to "BOLETÍN SEMANAL", "Tu día en 24 horas" to "LAS VEINTICUATRO HORAS",
        "Registros especiales" to "CAMPAÑAS ESPECIALES", "Avisos y recordatorios" to "TOQUES DE TAMBOR", "Contexto" to "CIRCUNSTANCIAS",
    )
    private val MUERTOS = mapOf(
        "Hoy" to "LA OFRENDA DE HOY", "Línea de tiempo" to "LO VIVIDO", "Ahora mismo estoy…" to "AHORITA ESTOY…",
        "Día" to "OFRENDA", "Diario" to "CALAVERITAS", "Gastos" to "MERCADO", "Entender" to "MEMORIA", "Más" to "ALTAR",
        "Llenar el día" to "VELADA", "Biblioteca del diario" to "PANTEÓN DE RECUERDOS", "Entrada" to "CALAVERITA", "Tablero" to "VELADORAS",
        "Te falta hoy" to "FALTA EN EL ALTAR", "Hábitos" to "COSTUMBRES", "Sueño" to "DESCANSO", "Ánimo y cuerpo" to "ALMA Y HUESOS", "Hitos" to "RECUERDOS",
        "Apariencia" to "PAPEL PICADO", "Obras" to "LECTURAS", "Mapa" to "CAMINOS", "Totales" to "TODO JUNTO", "Momentos de este día" to "RECUERDOS DEL DÍA",
        "Datos que faltan" to "LO QUE FALTA", "Resumen semanal" to "LA SEMANA", "Avisos y recordatorios" to "CAMPANADAS",
    )
    private val NAVIDAD = mapOf(
        "Hoy" to "La carta de hoy", "Línea de tiempo" to "Los días del año", "Ahora mismo estoy…" to "Ahora mismo estoy…",
        "Día" to "El día", "Diario" to "Cartas", "Gastos" to "Regalos y gastos", "Entender" to "Balance del año", "Más" to "El taller",
        "Llenar el día" to "Junto al fuego", "Biblioteca del diario" to "Cartas guardadas", "Entrada" to "Carta", "Tablero" to "El árbol",
        "Te falta hoy" to "Lista de pendientes", "Hábitos" to "Propósitos", "Sueño" to "Noche de paz", "Ánimo y cuerpo" to "Ánimo y salud",
        "Hitos" to "Regalos del día", "Apariencia" to "Adornos", "Obras" to "Cuentos y películas", "Calendario" to "Calendario de Adviento",
        "Datos que faltan" to "Lo que falta", "Momentos de este día" to "Momentos del día", "Avisos y recordatorios" to "Campanas",
    )
    private val SEDA = mapOf(
        "Hoy" to "El rollo de hoy", "Línea de tiempo" to "El camino recorrido", "Ahora mismo estoy…" to "En esta etapa…",
        "Día" to "Jornada de caravana", "Diario" to "Bitácora del viajero", "Gastos" to "Mercaderías", "Entender" to "El astrolabio", "Más" to "El caravasar",
        "Llenar el día" to "Bajo las estrellas", "Biblioteca del diario" to "Rollos de seda", "Entrada" to "Rollo", "Tablero" to "Carga de la caravana",
        "Te falta hoy" to "Pendientes del camino", "Hábitos" to "Ritos", "Sueño" to "Reposo", "Ánimo y cuerpo" to "Espíritu y cuerpo", "Hitos" to "Maravillas",
        "Apariencia" to "Telas", "Obras" to "Manuscritos", "Mapa" to "La ruta", "Lugares" to "Oasis", "Totales" to "Inventario del mercader",
        "Momentos de este día" to "Relatos del día", "Avisos y recordatorios" to "Gongs",
    )
    private val URSS = mapOf(
        "Hoy" to "INFORME DE HOY", "Línea de tiempo" to "CRÓNICA DEL PLAN", "Ahora mismo estoy…" to "ESTADO DEL CAMARADA",
        "Día" to "PLAN DEL DÍA", "Diario" to "DIARIO DEL CAMARADA", "Gastos" to "ECONOMÍA PLANIFICADA", "Entender" to "ESTADÍSTICA", "Más" to "COMISARIADO",
        "Llenar el día" to "INFORME DE JORNADA", "Biblioteca del diario" to "ARCHIVO DEL PUEBLO", "Entrada" to "INFORME", "Tablero" to "CUMPLIMIENTO DEL PLAN",
        "Te falta hoy" to "NORMAS PENDIENTES", "Hábitos" to "NORMAS", "Sueño" to "DESCANSO", "Ánimo y cuerpo" to "MORAL Y SALUD", "Hitos" to "HAZAÑAS",
        "Apariencia" to "AGITPROP", "Obras" to "CULTURA", "Mapa" to "MAPA DE LA UNIÓN", "Totales" to "PRODUCCIÓN TOTAL", "Datos que faltan" to "FALTAS AL PLAN",
        "Resumen semanal" to "PLAN SEMANAL", "Momentos de este día" to "INFORMES DEL DÍA", "Avisos y recordatorios" to "SIRENAS",
    )
}
