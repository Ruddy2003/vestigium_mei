package io.github.ruddy2003.vestigium.data

/**
 * Diccionario de datos que acompaña a la exportación. El tipo de cada columna se deduce de los valores reales
 * (así nunca se desincroniza del código) y la descripción sale de esta tabla.
 */
object Diccionario {
    private val RX_FECHA = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    private val RX_FECHA_HORA = Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})$")
    private val RX_HORA = Regex("^\\d{2}:\\d{2}(:\\d{2})?$")

    private val SUFIJOS_NUM = listOf("_min", "_h", "_kg", "_pct", "_ms", "_mm", "_db", "_lux", "_km", "_rpm", "_ml", "_num", "_c", "_delta", "_hpa", "_media", "_max", "_medio")

    fun tipo(valores: List<Any?>, col: String = ""): String {
        val v = valores.filterNotNull().filter { it !is String || it.isNotBlank() }
        // Columna vacía por ahora: se adivina por el nombre para que en R no quede como texto una cifra.
        if (v.isEmpty()) return if (SUFIJOS_NUM.any { col.endsWith(it) } || col.startsWith("n_") || col.startsWith("m_") || col.startsWith("luz_h") || col.startsWith("wifi_") || col.startsWith("app_")) "numero" else "texto"
        return when {
            v.all { it is Boolean } -> "logico"
            v.all { it is Int || it is Long || it is Short } -> "entero"
            v.all { it is Number } -> "numero"
            v.all { it is List<*> } -> "lista"
            v.all { it is String && RX_FECHA.matches(it) } -> "fecha"
            v.all { it is String && RX_FECHA_HORA.matches(it) } -> "fecha_hora"
            v.all { it is String && RX_HORA.matches(it) } -> "hora"
            else -> "texto"
        }
    }

    private val D: Map<String, String> = mapOf(
        // Generales
        "fecha" to "Día de la app (AAAA-MM-DD). Empieza a las 5:00: lo de la madrugada cuenta para el día anterior",
        "ts" to "Momento exacto, ISO 8601 con desfase de tu zona horaria", "hora" to "Hora local HH:MM",
        "id" to "Identificador único del registro", "nota" to "Texto libre", "texto" to "Texto libre",
        "lat" to "Latitud (grados)", "lon" to "Longitud (grados)", "precision_m" to "Precisión de la ubicación (metros)",
        "personas" to "@personas mencionadas (lista)", "temas" to "#temas mencionados (lista)", "palabras" to "Número de palabras",
        // Panel y días
        "dia_semana" to "1 = lunes … 7 = domingo", "con_diario" to "Hay registro del diario ese día (0/1)",
        "valencia" to "Qué tan agradable fue (1 muy desagradable a 7 muy agradable; Russell)", "activacion" to "Energía (1 muy baja a 7 muy alta)",
        "dolor" to "Dolor o molestia física (0 nada a 5 muy fuerte)", "sueno_calidad" to "Calidad del sueño percibida (1 a 5)",
        "valencia_muestreo" to "Promedio de la valencia en los avisos del día", "n_muestreos" to "Avisos de ánimo respondidos", "n_momentos" to "Momentos registrados (sin contar avisos)",
        "gasto_total" to "Gasto del día (tu moneda)", "schema_v" to "Versión del esquema de datos", "origen" to "De dónde vino el registro (app, web, obsidian, widget…)",
        "retroactivo" to "Se llenó después de ese día (0/1)", "dia_dificil" to "Marcado como día difícil (0/1)",
        "ts_creado" to "Cuándo se creó el registro", "ts_editado" to "Última edición",
        "hitos" to "Hitos del día (lista)", "flow" to "Experiencia de flujo (Csikszentmihalyi)", "sdt_necesidad" to "Necesidad psicológica más presente (Deci y Ryan)",
        "apreciacion" to "Evaluación del estrés (Lazarus)", "sueno_recordado" to "Recordó un sueño (0/1)", "sueno_valencia" to "Qué tan agradable fue el sueño (1 a 7)",
        "sueno_lucidez" to "Lucidez del sueño", "sueno_vividez" to "Vividez del recuerdo", "sueno_tipo" to "Tipo de sueño", "sueno_temas" to "Personas y temas del sueño (lista)",
        "sueno_texto" to "Narración del sueño", "mejor_momento" to "Lo mejor del día", "peor_momento" to "Lo peor del día",
        "impulso" to "Intensidad del impulso (0 a 5), seguimiento privado", "recaida" to "Hubo recaída (0/1), seguimiento privado", "detonantes" to "Detonantes (lista)",
        "medicamentos" to "Medicamentos tomados (lista)", "prediccion_manana" to "Predicción del ánimo de mañana (1 a 7)",
        // Momentos
        "tipo" to "Tipo de registro", "intensidad" to "Intensidad (1 a 5)", "lugar" to "Nombre del lugar marcado más cercano",
        "aviso_ts" to "Cuándo llegó el aviso que se respondió", "latencia_min" to "Minutos entre el aviso y la respuesta",
        "ruido_db" to "Ruido ambiente aproximado al registrar (dB relativos, sin calibrar)", "luz_lux" to "Luz ambiente al registrar (lux)",
        // Gastos
        "monto" to "Importe (tu moneda)", "categoria" to "Categoría de gasto", "imprevisto" to "Gasto no planeado (0/1)",
        // Escalas y reacción
        "instrumento" to "Cuestionario (who5, pss4, ucla3, tipi)", "media_ms" to "Tiempo de reacción medio (ms)", "mediana_ms" to "Tiempo de reacción mediano (ms)",
        "lapsos" to "Respuestas de más de 500 ms", "anticipaciones" to "Toques antes del estímulo", "ensayos_ms" to "Tiempos de cada ensayo (lista, ms)",
        // Salud
        "sueno_min" to "Minutos dormidos según el reloj", "sueno_profundo_min" to "Minutos de sueño profundo", "sueno_rem_min" to "Minutos de sueño REM",
        "sueno_ligero_min" to "Minutos de sueño ligero", "sueno_despierto_min" to "Minutos despierto durante la noche",
        "sueno_inicio" to "Cuándo empezó el sueño", "sueno_fin" to "Cuándo terminó el sueño (asignado al día en que despertaste)",
        "fc_media" to "Frecuencia cardiaca media (lpm)", "fc_min" to "Frecuencia cardiaca mínima (lpm)", "fc_max" to "Frecuencia cardiaca máxima (lpm)",
        "fc_reposo" to "Frecuencia cardiaca en reposo (lpm)", "hrv_rmssd" to "Variabilidad cardiaca nocturna, RMSSD (ms)", "pasos" to "Pasos",
        "spo2" to "Saturación de oxígeno (%)", "peso_kg" to "Peso (kg)", "grasa_pct" to "Grasa corporal (%)", "resp_rpm" to "Frecuencia respiratoria (respiraciones por minuto)",
        "temp_c" to "Temperatura corporal (°C)", "temp_piel_delta" to "Variación de la temperatura de la piel respecto a tu línea base (°C)",
        "pa_sistolica" to "Presión arterial sistólica (mmHg)", "pa_diastolica" to "Presión arterial diastólica (mmHg)", "distancia_km" to "Distancia recorrida (km)",
        "cal_activas" to "Calorías activas (kcal)", "cal_totales" to "Calorías totales (kcal)", "pisos" to "Pisos subidos", "vo2max" to "VO2 máx. (ml/kg/min)", "agua_ml" to "Agua registrada (ml)",
        // Pantalla
        "total_min" to "Minutos de pantalla", "desbloqueos" to "Veces que desbloqueaste el teléfono", "primer_uso" to "Primer uso del día", "ultimo_uso" to "Último uso del día",
        "top_apps" to "Apps más usadas, app:minutos (lista)", "pantalla_min" to "Minutos de pantalla",
        "primer_uso_h" to "Hora del primer uso en decimal (7.5 = 7:30)", "ultimo_uso_h" to "Hora del último uso en decimal; la madrugada suma 24 (1:00 = 25)",
        "sueno_inicio_h" to "Hora de dormir en decimal; la madrugada suma 24", "sueno_fin_h" to "Hora de despertar en decimal",
        // Visitas
        "entrada" to "Llegada al lugar", "salida" to "Salida del lugar", "minutos" to "Minutos en el lugar",
        // Entorno
        "t_max" to "Temperatura máxima (°C)", "t_min" to "Temperatura mínima (°C)", "t_media" to "Temperatura media (°C)", "sensacion_max" to "Sensación térmica máxima (°C)",
        "precip_mm" to "Lluvia (mm)", "horas_lluvia" to "Horas con lluvia", "nubes_pct" to "Nubosidad media (%)", "humedad_pct" to "Humedad media (%)",
        "presion_hpa" to "Presión atmosférica media (hPa)", "viento_max" to "Viento máximo (km/h)", "uv_max" to "Índice UV máximo", "codigo_wmo" to "Código de tiempo de la OMM",
        "cielo" to "Descripción del cielo", "amanecer" to "Hora de salida del sol", "atardecer" to "Hora de puesta del sol", "luz_dia_min" to "Minutos entre amanecer y atardecer",
        "sol_min" to "Minutos de sol directo", "pm25" to "Partículas PM2.5 (µg/m³)", "pm10" to "Partículas PM10 (µg/m³)", "aqi_us" to "Índice de calidad del aire de EE. UU. (máximo del día)",
        "luna_fase" to "Fase lunar (0 nueva, 0.5 llena)", "luna_ilum" to "Fracción iluminada de la Luna (0 a 1)", "lat_aprox" to "Latitud usada para el clima (redondeada)", "lon_aprox" to "Longitud usada para el clima (redondeada)",
        // Fotos y agenda
        "fotos" to "Fotos de cámara", "capturas" to "Capturas de pantalla", "otras" to "Otras imágenes (de otras apps)", "videos" to "Videos",
        "primera" to "Primera del día", "ultima" to "Última del día", "eventos" to "Eventos con hora", "todo_el_dia" to "Eventos de todo el día",
        "min_ocupado" to "Minutos con eventos (sin contar encimados dos veces)", "primero" to "Primer evento o mensaje", "ultimo" to "Último evento o mensaje", "titulos" to "Títulos de los eventos (lista)",
        // Notificaciones
        "total" to "Notificaciones recibidas", "por_app" to "Notificaciones por app, app:n (lista)", "notificaciones" to "Notificaciones recibidas",
        // Música
        "titulo" to "Título", "artista" to "Artista o programa", "album" to "Álbum («podcast» si es un episodio)", "app" to "App que lo reprodujo",
        "duracion_s" to "Duración de la pista (s)", "ms_escuchado" to "Milisegundos realmente escuchados (historial de Spotify)", "saltada" to "Se saltó (0/1)",
        "fuente" to "notificacion = lo que sonó en el teléfono; spotify = historial importado", "canciones" to "Canciones escuchadas (30 s o más)",
        // Actividad y ejercicio
        "a_pie_min" to "Minutos a pie", "corriendo_min" to "Minutos corriendo", "bici_min" to "Minutos en bici", "vehiculo_min" to "Minutos en vehículo", "quieto_min" to "Minutos quieto",
        "inicio" to "Inicio", "fin" to "Fin", "tipo_hc" to "Código de tipo de ejercicio de Health Connect", "min" to "Duración (minutos)", "ejercicio_min" to "Minutos de ejercicio del reloj",
        "km" to "Kilómetros aproximados según tus ubicaciones", "escalada" to "Escalaste ese día (0/1)",
        // Escalada
        "modalidad" to "boulder, cuerda, boulder_roca o cuerda_roca", "bloques" to "Bloques o vías intentados", "encadenados" to "Bloques encadenados",
        "flashes" to "Bloques a vista o flash", "intentos" to "Intentos", "grado_max" to "Grado máximo encadenado", "grado_max_num" to "Grado máximo como número (V0 = 0; YDS 5.10a = 10)",
        "grado_medio_num" to "Grado medio de lo encadenado", "energia" to "Energía al empezar (1 a 5)", "piel" to "Estado de la piel (1 a 5)", "esfuerzo" to "Esfuerzo percibido (1 a 10)",
        "satisfaccion" to "Satisfacción (1 a 5)", "sesion_reloj" to "Id de la sesión del reloj enlazada", "sesion" to "Id de la sesión", "orden" to "Orden dentro de la sesión",
        "grado" to "Grado como lo anotaste", "grado_num" to "Grado como número (V0 = 0; Font convertido a V; YDS 5.10a = 10)", "escala" to "Escala del grado (V, Font, YDS)",
        "estilo" to "a_vista, flash, encadenado, repetido o proyecto", "encadenado" to "Se encadenó (0/1)", "angulo" to "placa, vertical, desplome o techo", "rasgos" to "Rasgos del bloque (lista)",
        // Biblioteca
        "autor" to "Autor, dirección o estudio", "anio" to "Año de publicación o estreno", "estado" to "quiero, en_curso, terminado o abandonado",
        "calificacion" to "Calificación (1 a 10)", "progreso" to "Avance (páginas, minutos, capítulos u horas)", "veces" to "Veces leído o visto",
        "etiquetas" to "Etiquetas (lista)", "agregado" to "Cuándo lo agregaste", "dias" to "Días entre inicio y fin",
        // Comunicación
        "entrantes" to "Llamadas entrantes contestadas", "salientes" to "Llamadas salientes", "perdidas" to "Llamadas perdidas", "rechazadas" to "Llamadas rechazadas o bloqueadas",
        "min_hablado" to "Minutos hablando por teléfono", "llamada_max_min" to "Llamada más larga (min)", "sms_recibidos" to "SMS recibidos", "sms_enviados" to "SMS enviados",
        "contactos" to "Personas distintas con las que hubo llamada o SMS", "llamadas" to "Llamadas entrantes y salientes", "sms" to "SMS enviados y recibidos",
        // Conexiones
        "muestras" to "Muestras tomadas (una cada ~15 min)", "sin_wifi_min" to "Minutos sin wifi (fuera de casa, normalmente)", "wifi_total_min" to "Minutos conectado a cualquier wifi",
        "audifonos_min" to "Minutos con audífonos conectados", "coche_min" to "Minutos conectado al coche por Bluetooth", "bocina_min" to "Minutos con bocina Bluetooth",
        "bluetooth_otro_min" to "Minutos con otro audio Bluetooth", "musica_min" to "Minutos con audio sonando", "cargando_min" to "Minutos cargando",
        "carga_noche_inicio" to "Cuándo conectaste el teléfono por la noche", "carga_noche_fin" to "Cuándo lo desconectaste en la mañana",
        "carga_noche_inicio_h" to "Hora de conectar el teléfono por la noche en decimal (la madrugada suma 24)",
        "bateria_media" to "Batería media (%)", "bateria_min" to "Batería mínima del día (%)", "luz_media" to "Luz ambiente media en las muestras (lux; en el bolsillo marca 0)",
        "luz_max" to "Luz ambiente máxima (lux)", "luz_alta_min" to "Minutos con más de 1000 lux (luz de exterior)",
        "dispositivos" to "Dispositivos de audio, nombre:minutos (lista)", "redes" to "Redes wifi, nombre:minutos (lista)",
        // Kindle
        "libro" to "Libro", "ubicacion" to "Posición en el Kindle", "pagina" to "Página",
        // Google
        "yt_vistos" to "Videos de YouTube vistos", "yt_busquedas" to "Búsquedas en YouTube", "busquedas" to "Búsquedas en Google", "visitas" to "Páginas visitadas desde la búsqueda",
        "canales" to "Canales más vistos, canal:n (lista)", "producto" to "youtube, busqueda u otro", "accion" to "visto, buscado, visitado u otro", "canal" to "Canal de YouTube",
        "subrayados" to "Subrayados y notas de Kindle hechos ese día",
        // WhatsApp
        "chat" to "Nombre del chat", "grupo" to "Es un grupo (0/1)", "mios" to "Mensajes que enviaste", "otros" to "Mensajes que recibiste",
        "palabras_mias" to "Palabras que escribiste", "palabras_otros" to "Palabras que recibiste", "multimedia" to "Fotos, audios, stickers y archivos",
        "resp_mia_min" to "Mediana de minutos que tardaste en responder", "resp_otros_min" to "Mediana de minutos que tardaron en responderte",
        "inicie" to "Conversaciones que empezaste tú (tras 4 h de silencio)", "conversaciones" to "Conversaciones del día (separadas por 4 h de silencio)",
        "wa_mios" to "Mensajes de WhatsApp enviados", "wa_otros" to "Mensajes de WhatsApp recibidos", "wa_chats" to "Chats de WhatsApp con actividad",
        "ruido_db_medio" to "Ruido ambiente medio de tus momentos del día (dB relativos)",
        "fuente_ubicacion" to "Origen de la ubicación",
    )

    private fun describir(archivo: String, col: String): String {
        D["$archivo:$col"]?.let { return it }
        D[col]?.let { return it }
        return when {
            col.startsWith("ctx_") -> "Contexto del día: ${col.removePrefix("ctx_").replace('_', ' ')} (0/1)"
            col.startsWith("hab_") -> "Hábito cumplido: ${Repo.config.habitos.firstOrNull { it.id == col.removePrefix("hab_") }?.nombre ?: col.removePrefix("hab_")} (0/1)"
            col.startsWith("m_") -> "Momentos de tipo ${col.removePrefix("m_")}"
            col.startsWith("item_") -> "Respuesta al ítem ${col.removePrefix("item_")}"
            col.startsWith("puntaje_") -> "Puntaje ${col.removePrefix("puntaje_")}"
            col.startsWith("app_") -> "Notificaciones de ${col.removePrefix("app_").replace('_', ' ')}"
            col.startsWith("wifi_") -> "Minutos en la red ${col.removePrefix("wifi_").replace('_', ' ')}"
            col.startsWith("luz_h") -> "Luz ambiente media a las ${col.removePrefix("luz_h")}:00 (lux)"
            Regex("^h\\d{2}$").matches(col) -> "Notificaciones a las ${col.drop(1)}:00"
            col.startsWith("flow_") || col.startsWith("sdt_necesidad_") || col.startsWith("apreciacion_") -> "Opción elegida (0/1)"
            else -> ""
        }
    }

    fun csv(tablas: List<Exportar.Tabla>): String {
        val filas = ArrayList<Map<String, Any?>>()
        for (t in tablas) {
            val cols = LinkedHashSet<String>(); t.filas.forEach { cols.addAll(it.keys) }
            for (c in cols) filas.add(linkedMapOf("archivo" to t.archivo, "columna" to c, "tipo" to tipo(t.filas.map { it[c] }, c),
                "descripcion" to describir(t.archivo, c), "tabla" to t.descripcion))
        }
        listOf("ts" to "fecha_hora", "lat" to "numero", "lon" to "numero", "precision_m" to "numero", "fuente" to "texto").forEach { (c, ti) ->
            filas.add(linkedMapOf("archivo" to "vm_ubicaciones.csv", "columna" to c, "tipo" to ti,
                "descripcion" to if (c == "fuente") "De dónde salió la muestra (fondo, momento, cronología…)" else describir("vm_ubicaciones.csv", c), "tabla" to "Muestras de ubicación"))
        }
        return Exportar.csv(filas)
    }
}
