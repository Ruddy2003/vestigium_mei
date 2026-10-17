package io.github.ruddy2003.vestigium.ui

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

    fun nombre(tema: TemaId, titulo: String): String? = when (tema) {
        TemaId.MEDIEVAL -> MEDIEVAL[titulo]
        TemaId.DISTOPIA -> DISTOPIA[titulo]
        TemaId.REFUGIO -> REFUGIO[titulo]
        TemaId.ORDEN -> ORDEN[titulo]
        TemaId.LABORATORIO -> LABORATORIO[titulo]
        TemaId.COLONOS -> COLONOS[titulo]
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
            else -> emptyMap()
        }
        return m[clave] ?: clave.uppercase()
    }

    private val MEDIEVAL = mapOf(
        "Día" to "Del día", "Momentos" to "Crónica", "Gastos" to "Libro de cuentas", "Explorar" to "Glosas", "Más" to "Otros folios",
        "Hábitos" to "Regla", "Cartas al futuro" to "Epístolas", "Experimentos personales" to "Pruebas", "Guía científica" to "Tratado",
        "Cuestionarios" to "Cuestiones", "Prueba de reacción" to "Presteza", "Datos" to "Archivo", "Permisos y sensores" to "Sentidos",
        "Ajustes" to "Ordenanzas", "Reportes" to "Anales", "Bienvenido" to "Bien venido", "Escalada" to "Ascensión", "Biblioteca" to "Librería",
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
        "Día" to "PARTE DIARIO", "Momentos" to "INCIDENCIAS", "Gastos" to "RACIONAMIENTO", "Explorar" to "VIGILANCIA", "Más" to "MINISTERIOS",
        "Hábitos" to "CUOTAS", "Cartas al futuro" to "CORRESPONDENCIA SELLADA", "Experimentos personales" to "PRUEBAS AUTORIZADAS",
        "Guía científica" to "DOCTRINA", "Cuestionarios" to "INTERROGATORIOS", "Prueba de reacción" to "PRUEBA DE REFLEJOS", "Lugares" to "ZONAS",
        "Datos" to "ARCHIVO CENTRAL", "Permisos y sensores" to "SENSORES", "Ajustes" to "DIRECTIVAS", "Reportes" to "INFORMES",
        "Bienvenido" to "ALTA DE CIUDADANO", "Calendario" to "PLAN", "Biblioteca" to "MATERIAL APROBADO", "Tu año" to "BALANCE ANUAL",
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
        "Día" to "PARTE DE CAMPO", "Momentos" to "BITÁCORA", "Gastos" to "INTENDENCIA", "Explorar" to "RECONOCIMIENTO", "Más" to "ARSENAL",
        "Hábitos" to "DISCIPLINA", "Cartas al futuro" to "ÓRDENES SELLADAS", "Experimentos personales" to "ENSAYOS", "Guía científica" to "CÓDICE",
        "Cuestionarios" to "EVALUACIÓN", "Prueba de reacción" to "REFLEJOS", "Lugares" to "TERRITORIO", "Datos" to "ARCHIVO",
        "Permisos y sensores" to "SENSORES", "Ajustes" to "CONFIGURACIÓN", "Reportes" to "INFORMES", "Bienvenido" to "INICIACIÓN",
        "Calendario" to "CALENDARIO DE CAMPAÑA", "Biblioteca" to "ARCHIVO DE SABERES", "Tu año" to "CAMPAÑA ANUAL", "Diario" to "CRÓNICA",
        "Predicción" to "PREVISIÓN", "Lo que registró el teléfono" to "TELEMETRÍA", "Mapa" to "MAPA TÁCTICO", "Mapa emocional" to "MAPA TÁCTICO",
        "Tablero" to "PUESTO DE MANDO", "Tu día en 24 horas" to "CRONOLOGÍA 24 H", "Te falta hoy" to "OBJETIVOS PENDIENTES",
        "Datos que faltan" to "TELEMETRÍA PERDIDA", "Tema" to "BLASÓN", "Resumen semanal" to "INFORME SEMANAL", "Totales" to "BALANCE",
        "Gimnasio" to "ENTRENAMIENTO", "Buscar" to "RASTREO", "Medicamentos" to "BOTIQUÍN", "Clima" to "CONDICIONES", "Ánimo y cuerpo" to "MORAL Y CUERPO",
        "Lo mejor y lo peor" to "VICTORIAS Y DERROTAS", "Momentos de este día" to "BITÁCORA DEL DÍA", "Momentos de la semana" to "BITÁCORA DE LA SEMANA",
        "Respaldo automático" to "COPIA DE RESGUARDO", "Registrar" to "REPORTAR",
    )

    private val LABORATORIO = mapOf(
        "Día" to "SESIÓN", "Momentos" to "OBSERVACIONES", "Gastos" to "RECURSOS", "Explorar" to "HALLAZGOS", "Más" to "DIVISIONES",
        "Hábitos" to "VARIABLES", "Cartas al futuro" to "MUESTRAS SELLADAS", "Experimentos personales" to "EXPERIMENTOS", "Guía científica" to "MÉTODO",
        "Cuestionarios" to "INSTRUMENTOS", "Prueba de reacción" to "TIEMPO DE REACCIÓN", "Lugares" to "COORDENADAS", "Datos" to "REPOSITORIO",
        "Ajustes" to "CALIBRACIÓN", "Reportes" to "INFORMES", "Bienvenido" to "INGRESO", "Biblioteca" to "REFERENCIAS", "Tu año" to "SERIE ANUAL",
        "Sueño" to "CICLO DE SUEÑO", "Ánimo y cuerpo" to "ESTADO BASAL", "Diario" to "CUADERNO", "Predicción" to "HIPÓTESIS",
        "Lo que registró el teléfono" to "SENSORES PASIVOS", "Las relaciones más marcadas" to "CORRELACIONES", "Mapa" to "DISTRIBUCIÓN ESPACIAL",
        "Mapa emocional" to "DISTRIBUCIÓN ESPACIAL", "Tablero" to "MONITOR", "Tu día en 24 horas" to "SERIE DE 24 H", "Te falta hoy" to "MEDICIONES PENDIENTES",
        "Datos que faltan" to "DATOS FALTANTES", "Tema" to "INTERFAZ", "Resumen semanal" to "SÍNTESIS SEMANAL", "Totales" to "ACUMULADOS",
        "Gimnasio" to "CARGA FÍSICA", "Buscar" to "CONSULTA", "Cifras" to "MEDIDAS", "Medicamentos" to "FÁRMACOS", "Momentos de este día" to "OBSERVACIONES DEL DÍA",
        "Momentos de la semana" to "OBSERVACIONES DE LA SEMANA", "Este día hace un año" to "CONTROL: HACE UN AÑO", "Registrar" to "REGISTRAR",
    )

    private val COLONOS = mapOf(
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
}
