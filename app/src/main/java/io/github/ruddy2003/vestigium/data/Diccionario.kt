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
        "con_quien" to "Con quién estabas en el aviso de ánimo (lista: solo, pareja, amigos, familia, companeros, desconocidos)",
        "detonantes" to "Detonantes (lista)", "dispositivo" to "Recaída: en qué aparato (seguimiento privado)", "vm_momentos.csv:antes" to "Recaída: qué hacías justo antes (seguimiento privado)",
        "duracion_min" to "Recaída: cuánto duró (minutos)", "recaida_min" to "Minutos de recaída en el día (seguimiento privado)",
        "es_siesta" to "El tramo es una siesta (0/1)", "vm_tramos.csv:inicio" to "Inicio, ISO 8601", "vm_tramos.csv:fin" to "Fin, ISO 8601", "vm_tramos.csv:min" to "Minutos del tramo",
        "inicio" to "Inicio", "fin" to "Fin", "min" to "Minutos",
        "cama_min" to "Minutos de pantalla en la última hora antes de dormirte (la noche que terminó ese día, según el reloj)",
        "dormir_tras_tel_min" to "Minutos entre soltar el teléfono y quedarte dormido", "tel_tras_despertar_min" to "Minutos entre despertar y usar el teléfono",
        "por_hora_min" to "Minutos de pantalla en cada hora, de 0 a 23 (lista)",
        "enfoque_min" to "Minutos de enfoque medidos con el cronómetro (sin siestas)", "siesta_min" to "Minutos de siesta", "siesta" to "Tomó siesta (0/1)",
        "aire_libre_min" to "Minutos al aire libre, estimados con la luz de exterior o el tiempo caminando, corriendo o en bici (el mayor)",
        "en_casa_min" to "Minutos dentro de tus lugares de tipo casa", "acompanado_pct" to "Proporción de avisos de ánimo en que estabas con alguien",
        "valencia_acompanado" to "Valencia promedio en avisos con alguien", "valencia_solo" to "Valencia promedio en avisos estando solo", "n_recaida" to "Recaídas registradas con hora (seguimiento privado)",
        // Ánimo unificado, sustancias y la tabla por hora
        "es_animo" to "Es un registro de ánimo (aviso o hecho por ti), no un impulso ni una recaída (0/1)",
        "emociones" to "Lo que sentías (lista de Catalogo: alegria, calma, estres, frustracion…; no_se = no lo sabías nombrar; otro = ver emocion_otra)",
        "emocion_otra" to "El sentimiento que escribiste cuando elegiste «otro»", "pensamiento" to "En qué pensabas (pensando_en, preocupacion, idea, recuerdo, plan, vueltas, autocritica, deseo)",
        "alcohol_nivel" to "Qué tan pedo estabas en ese momento (0 sobrio a 10 vómito y mucho mareo; interpolado de tus lecturas)",
        "cannabis_nivel" to "Nivel con cannabis en ese momento (0 a 10; interpolado)", "otra_nivel" to "Nivel con otra sustancia en ese momento (0 a 10; interpolado)",
        "sueno_categorias" to "De qué fue el sueño (lista de ejes: per_ personajes, esc_ escenario, int_ interacción, tema_ temas típicos, est_ estructura, emo_ emoción)",
        "vm_sustancias.csv:sustancia" to "alcohol, cannabis, tabaco, cafeina u otra", "otra_nombre" to "Cuál, si fue «otra»",
        "hora_inicio" to "Hora en que empezó", "hora_fin" to "Hora en que se te pasó el efecto", "duracion_h" to "Horas con efecto (de inicio a fin)",
        "nivel_max" to "Nivel máximo (0 a 10): el que dijiste en la mañana o el mayor que leíste", "lecturas" to "Lecturas de nivel con su hora (lista HH:MM=nivel)",
        "vm_sustancias.csv:cantidad" to "Alcohol: bebidas estándar (14 g); tabaco: cigarros o sesiones; cannabis: veces; cafeína: mg", "unidad" to "Unidad de la cantidad",
        "tabaco_n" to "Cigarros o sesiones de vape del día", "spo2_min" to "Saturación de oxígeno mínima durante la noche (%)",
        "fc_sueno" to "Frecuencia cardiaca media mientras dormías (lpm)", "siesta_reloj_min" to "Siestas que registró el reloj ese día (minutos)",
        "sueno_manual" to "El sueño de esa noche se corrigió a mano (0/1)", "sueno_fuentes" to "Lo que llegó de Health Connect esa noche: fuente, horas, etapas y si se usó (lista)", "via" to "Cannabis: fumado, vape o comestible; tabaco: cigarro o vape", "cruda" to "Cruda o bajón al día siguiente (0 nada a 5 no puedo funcionar)",
        "sintomas" to "Síntomas al día siguiente (lista)", "alcohol_estimado_max_gl" to "Alcohol en sangre máximo estimado con Widmark (g/L ≈ por mil)",
        "completo" to "Ya se llenó lo de la mañana siguiente (0/1)", "oculta_en_pantallas" to "Esa sustancia está oculta de las pantallas (sigue en los análisis) (0/1)",
        "vm_horas.csv:fecha" to "Día de calendario (no el día de la app)", "vm_horas.csv:hora" to "Hora del día, de 0 a 23 (la fila cubre de HH:00 a HH:59)",
        "vm_horas.csv:dia_logico" to "El día de la app al que pertenece la hora (de 0 a 4 cuenta para el día anterior)",
        "dormido_min" to "Minutos dormido en esa hora (reloj)", "vm_horas.csv:lugar" to "Lugar donde pasaste más tiempo en esa hora (al menos 15 min)",
        "vm_horas.csv:en_casa_min" to "Minutos en casa en esa hora", "vm_horas.csv:pantalla_min" to "Minutos de pantalla en esa hora",
        "vm_horas.csv:notificaciones" to "Notificaciones en esa hora", "vm_horas.csv:luz_lux" to "Luz ambiente media en esa hora (lux)",
        "vm_horas.csv:canciones" to "Canciones que empezaron en esa hora", "n_registros" to "Registros de ánimo en esa hora",
        "vm_horas.csv:valencia" to "Valencia media de los registros de esa hora (1 a 7)", "vm_horas.csv:activacion" to "Energía media de los registros de esa hora (1 a 7)",
        "vm_horas.csv:cafeina_mg" to "Cafeína en el cuerpo a la mitad de la hora (mg, estimada con tu vida media)",
        "alcohol_gl" to "Alcohol en sangre a la mitad de la hora (g/L, Widmark, estimado)",
        "vm_horas.csv:tabaco_n" to "Cigarros o sesiones de vape en esa hora", "vm_horas.csv:gasto" to "Gasto en esa hora", "comidas_n" to "Alimentos registrados en esa hora (MacroFactor)",
        "vm_horas.csv:kcal" to "Calorías registradas en esa hora (MacroFactor)", "vm_horas.csv:enfoque_min" to "Minutos de enfoque (cronómetro) en esa hora", "vm_horas.csv:siesta_min" to "Minutos de siesta en esa hora",
        "alcohol" to "Tomó alcohol ese día (0/1)", "alcohol_bebidas" to "Bebidas estándar del día (MacroFactor o anotadas)", "alcohol_nivel_max" to "Qué tan pedo llegó (0 a 10)",
        "alcohol_horas" to "Horas con efecto del alcohol", "alcohol_estimado_max" to "Alcohol en sangre máximo estimado del día (g/L)", "alcohol_al_dormir" to "Alcohol en sangre estimado al dormirte (g/L; en el día del sueño)",
        "cannabis" to "Cannabis ese día (0/1)", "cannabis_nivel_max" to "Nivel máximo con cannabis (0 a 10)", "cannabis_horas" to "Horas con efecto del cannabis",
        "otra_sustancia" to "Otra sustancia ese día (0/1)", "cafeina_total_mg" to "Cafeína del día (mg; MacroFactor o anotada, sin contar dos veces)",
        "cafeina_al_dormir_mg" to "Cafeína en el cuerpo al dormirte (mg, estimada; en el día del sueño)",
        "animo_valencia" to "Valencia promedio de todos los registros de ánimo del día (avisos y propios)", "animo_activacion" to "Energía promedio de todos los registros de ánimo del día",
        "registros_animo" to "Registros de ánimo del día", "vi_expareja" to "Estuvo con una expareja (con la relación de ese día; no cuenta si solo pensó en ella) (0/1)",
        "vi_trabajo" to "Estuvo con un colega, compañero o mentor (0/1)", "cercania_media" to "Cercanía media (1 a 7, círculos de Aron) de quien mencionó ese día",
        "personas" to "@personas mencionadas (lista)", "temas" to "#temas mencionados (lista)", "palabras" to "Número de palabras",
        // Panel y días
        "dia_semana" to "1 = lunes … 7 = domingo",
        "festivo" to "Día de descanso obligatorio de la Ley Federal del Trabajo de México, art. 74 (0/1; vacío si en «Conocerte» no vives en México)",
        "dia_libre" to "Sábado, domingo o festivo (0/1): la regla de día libre del jet lag social; una noche es libre si despiertas en un día libre", "con_diario" to "Hay registro del diario ese día (0/1)",
        "valencia" to "Qué tan agradable fue (1 muy desagradable a 7 muy agradable; Russell)", "activacion" to "Energía (1 muy baja a 7 muy alta)",
        "dolor" to "Dolor o molestia física (0 nada a 5 muy fuerte)", "sueno_calidad" to "Calidad del sueño percibida (1 a 5)",
        "valencia_muestreo" to "Promedio de la valencia en los avisos del día", "n_muestreos" to "Avisos de ánimo respondidos (registros «muestreo», con o sin valencia)", "n_momentos" to "Momentos registrados (sin contar avisos; incluye los privados: impulsos, recaídas y libido)",
        "gasto_total" to "Gasto del día (tu moneda); 0 desde tu primer gasto, en días con diario; antes, vacío", "ingreso_total" to "Ingresos del día (tu moneda); 0 desde tu primer ingreso, en días con diario; antes, vacío", "fijo" to "Ingreso que se repite, como sueldo o beca (0/1)", "schema_v" to "Versión del esquema de datos (0 = importado de Obsidian o de la versión web sin versión propia)", "origen" to "De dónde vino el registro (app, web, obsidian, widget…)",
        "retroactivo" to "La valencia se llenó otro día, después (0/1). Desde la v13 se decide con ts_valencia; los días de antes, por si el registro del día se creó después",
        "ts_valencia" to "Cuándo se llenó o se cambió por última vez la valencia del día (desde la v13)", "dia_dificil" to "Marcado como día difícil (0/1)",
        "ts_creado" to "Cuándo se anotó el registro. En momentos, gastos, ingresos, tomas, consumos y tramos, desde la v14 (vacío en los de antes); comparado con su hora dice si se anotó tarde",
        "ts_editado" to "Cuándo se corrigió por última vez después de anotarlo (en momentos, gastos e ingresos, desde la v14)",
        "version_noche" to "Con qué orden de preguntas se llenó la hoja de la noche (desde la v14): 1 = el orden de hasta la v14; 2 = el orden nuevo, con el ánimo del momento primero. Vacío = no pasó por la hoja o es de antes",
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
        "monto" to "Importe (tu moneda)", "vm_gastos.csv:categoria" to "Categoría de gasto", "vm_ingresos.csv:categoria" to "Categoría de ingreso", "subcategoria" to "Subcategoría dentro de la categoría (desde la v14; vacío = sin subcategoría)", "imprevisto" to "Gasto no planeado (0/1)",
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
        "pa_sistolica" to "Presión arterial sistólica (mmHg)", "pa_diastolica" to "Presión arterial diastólica (mmHg)", "vm_salud.csv:distancia_km" to "Distancia recorrida según Health Connect (km)",
        "vm_panel.csv:distancia_km" to "Kilómetros recorridos según tus ubicaciones (GPS, aproximado; 0 en días con ubicaciones y sin movimiento)",
        "cal_activas" to "Calorías activas (kcal)", "cal_totales" to "Calorías totales (kcal)", "pisos" to "Pisos subidos", "vo2max" to "VO2 máx. (ml/kg/min)", "agua_ml" to "Agua registrada (ml)",
        // Pantalla
        "total_min" to "Minutos de pantalla", "desbloqueos" to "Veces que desbloqueaste el teléfono", "primer_uso" to "Primer uso del día", "ultimo_uso" to "Último uso del día",
        "top_apps" to "Apps más usadas, app:minutos (lista)", "pantalla_min" to "Minutos de pantalla",
        "primer_uso_h" to "Hora del primer uso en decimal (7.5 = 7:30)", "ultimo_uso_h" to "Hora del último uso en decimal; antes de las 5:00 suma 24 (1:00 = 25), como el día lógico",
        "sueno_inicio_h" to "Hora de dormir en decimal; la madrugada suma 24", "sueno_fin_h" to "Hora de despertar en decimal",
        // Visitas
        "entrada" to "Llegada al lugar", "salida" to "Salida del lugar", "vm_visitas.csv:minutos" to "Minutos en el lugar",
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
        "vm_musica.csv:duracion_s" to "Duración de la pista (s)", "ms_escuchado" to "Milisegundos realmente escuchados (historial de Spotify)", "saltada" to "Se saltó (0/1)",
        "fuente" to "notificacion = lo que sonó en el teléfono; spotify = historial importado; spotify_api = conexión directa con tu cuenta",
        "vm_gastos.csv:dia_logico" to "Día del diario al que cuenta el gasto: antes de las 5:00 es el día anterior (fecha y hora son las reales)",
        "vm_ingresos.csv:dia_logico" to "Día del diario al que cuenta el ingreso: antes de las 5:00 es el día anterior (fecha y hora son las reales)",
        "gramos" to "Gramos del alimento (cantidad × peso de la porción); vacío si se registró en ml",
        "mililitros" to "Mililitros, cuando el alimento se registró en ml",
        "contexto" to "Desde dónde sonó (conexión directa): playlist, album, artist o collection = tus «me gusta»", "es_musica" to "1 si es música; 0 si sonó en una app de video o navegador (YouTube, Chrome…) y no cuenta como canción", "canciones" to "Canciones escuchadas (30 s o más)",
        // Actividad y ejercicio
        "a_pie_min" to "Minutos a pie", "corriendo_min" to "Minutos corriendo", "bici_min" to "Minutos en bici", "vehiculo_min" to "Minutos en vehículo", "quieto_min" to "Minutos quieto",
        "vm_ejercicio.csv:inicio" to "Inicio de la sesión", "vm_ejercicio.csv:fin" to "Fin de la sesión", "tipo_hc" to "Código de tipo de ejercicio de Health Connect", "vm_ejercicio.csv:min" to "Duración (minutos)", "ejercicio_min" to "Minutos de ejercicio del reloj; 0 en días con datos del reloj y sin sesión, desde la primera sesión",
        "km" to "Kilómetros aproximados según tus ubicaciones (0 en días con ubicaciones y sin movimiento)", "escalada" to "Escalaste ese día (0/1); 0 desde tu primera sesión, en días con diario; antes, vacío",
        // Escalada
        "modalidad" to "boulder, cuerda, boulder_roca o cuerda_roca", "bloques" to "Bloques o vías intentados", "encadenados" to "Bloques encadenados",
        "flashes" to "Bloques a vista o flash", "intentos" to "Intentos", "grado_max" to "Grado máximo encadenado", "grado_max_num" to "Grado máximo como número (V0 = 0; YDS 5.10a = 10)",
        "grado_medio_num" to "Grado medio de lo encadenado", "energia" to "Energía al empezar (1 a 5)", "piel" to "Estado de la piel (1 a 5)", "esfuerzo" to "Esfuerzo percibido (1 a 10)",
        "satisfaccion" to "Satisfacción (1 a 5)", "sesion_reloj" to "Id de la sesión del reloj enlazada", "sesion" to "Id de la sesión", "orden" to "Orden dentro de la sesión",
        "vm_escalada_bloques.csv:grado" to "Grado como lo anotaste", "grado_num" to "Grado como número (V0 = 0; Font convertido a V; YDS 5.10a = 10)", "escala" to "Escala del grado (V, Font, YDS)",
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
        // Obras y citas
        "revista" to "Revista donde salió el artículo", "doi" to "DOI del artículo", "dias_marcados" to "Días que marcaste que leíste, viste o jugaste la obra",
        "fechas_marcadas" to "Esos días (lista)", "pais" to "País (código ISO de dos letras)", "desde" to "Desde (fecha)", "hasta" to "Hasta (fecha)", "citas" to "Citas guardadas de la obra", "obra_id" to "Obra de la que es la cita", "favorita" to "Cita con ♥ (0/1)",
        // Google
        "vm_youtube.csv:tipo" to "suscripcion, megusta (le diste «me gusta»), lista (lo agregaste a una lista), comentario o chat (mensaje en un chat en vivo)",
        "vm_youtube.csv:video_id" to "Identificador del video de YouTube (youtube.com/watch?v=…)", "vm_youtube.csv:canal" to "Canal (en suscripciones)",
        "vm_youtube.csv:lista" to "Nombre de tu lista (en videos agregados a listas)", "vm_youtube.csv:ts" to "Cuándo (vacío en suscripciones: Takeout no trae la fecha)",
        "yt_megusta" to "Videos de YouTube a los que les diste «me gusta»", "yt_comentarios" to "Comentarios y mensajes en chats en vivo de YouTube (solo conteo)",
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
        // MacroFactor
        "kcal" to "Calorías consumidas (kcal)", "proteina_g" to "Proteína (g)", "carbohidratos_g" to "Carbohidratos (g)", "grasa_g" to "Grasa (g)",
        "fibra_g" to "Fibra (g)", "alcohol_g" to "Alcohol (g)", "cafeina_mg" to "Cafeína (mg)", "azucares_g" to "Azúcares (g)", "azucares_anadidos_g" to "Azúcares añadidos (g)",
        "sodio_mg" to "Sodio (mg)", "agua_g" to "Agua de los alimentos (g)", "gasto_kcal" to "Gasto energético estimado por MacroFactor (kcal)",
        "peso_mf_kg" to "Peso registrado en MacroFactor (kg)", "peso_tendencia_kg" to "Peso de tendencia de MacroFactor (kg)", "pasos_mf" to "Pasos según MacroFactor",
        "objetivo_kcal" to "Objetivo de calorías", "objetivo_proteina_g" to "Objetivo de proteína (g)", "objetivo_carbohidratos_g" to "Objetivo de carbohidratos (g)",
        "objetivo_grasa_g" to "Objetivo de grasa (g)", "comidas" to "Alimentos registrados", "primera_comida" to "Hora del primer alimento", "ultima_comida" to "Hora del último alimento",
        "cafeina_ultima" to "Hora de la última cafeína", "primera_comida_h" to "Hora del primer alimento en decimal", "ultima_comida_h" to "Hora del último alimento en decimal",
        "cafeina_ultima_h" to "Hora de la última cafeína en decimal", "alimento" to "Alimento", "porcion" to "Unidad de la porción", "vm_comidas.csv:cantidad" to "Cantidad de porciones",
        "peso_porcion_g" to "Gramos por porción", "entrenos" to "Entrenamientos del día (lista)", "vm_entrenos.csv:minutos" to "Minutos de entrenamiento",
        "series" to "Series totales (con calentamiento)", "series_efectivas" to "Series sin contar calentamiento", "reps" to "Repeticiones", "volumen_kg" to "Volumen: peso × repeticiones (kg)",
        "entreno" to "Nombre del entrenamiento", "entreno_min" to "Duración del entrenamiento (min)", "ejercicio" to "Ejercicio", "tipo_serie" to "Tipo de serie",
        "calentamiento" to "Serie de calentamiento (0/1)", "peso_kg" to "Peso (kg)", "rir" to "Repeticiones en reserva", "e1rm_kg" to "1RM estimado con Epley (kg), series de hasta 12 reps",
        "vm_series.csv:duracion_s" to "Duración de la serie (s)", "distancia_m" to "Distancia (m)", "peso_base_kg" to "Peso base del ejercicio (kg)",
        "gimnasio" to "Entrenaste en el gimnasio (0/1); 0 desde tu primer entreno, en días con diario; antes, vacío", "gimnasio_min" to "Minutos de gimnasio", "series_gimnasio" to "Series efectivas en el gimnasio",
        // Cultura, mundo, cartas y personas cercanas (panel)
        "obras_dia" to "Obras a las que les dedicaste tiempo ese día", "lectura" to "Leíste un libro o artículo (0/1)", "vi_pelicula_serie" to "Viste una película o serie (0/1)",
        "jugue" to "Jugaste un videojuego (0/1)", "obra_terminada" to "Terminaste una obra ese día (0/1)", "calificacion_obra" to "Calificación media de lo que terminaste (1 a 10)",
        "citas_dia" to "Citas copiadas y frases guardadas ese día", "ciudades_dia" to "Ciudades distintas con al menos media hora", "fuera_de_casa" to "Fuera de tu ciudad (a más de 40 km) (0/1)",
        "extranjero" to "En un país distinto al tuyo (0/1)", "ciudad_nueva" to "Primera vez en una ciudad (0/1)", "unesco" to "Pasaste por un sitio del Patrimonio Mundial (0/1)",
        "carta_escrita" to "Escribiste una carta ese día (0/1)", "sueno_palabras" to "Palabras del sueño escrito", "cercanos" to "Personas cercanas (familia, pareja, amistades, convivientes) con las que estuviste",
        "vi_familia" to "Estuviste con tu familia (0/1)", "vi_pareja" to "Estuviste con tu pareja (0/1)", "vi_amistades" to "Estuviste con una amistad (0/1)",
        "vi_convivientes" to "Estuviste con quien vive contigo (0/1)",
        "personas_vistas" to "Personas con las que estuviste ese día (diario y momentos)", "personas_pensadas" to "Personas en las que pensaste sin verlas",
        "personas_mensajes" to "Personas con las que hablaste por mensaje o llamada, sin verlas", "penso_en_pareja" to "Pensaste en tu pareja sin verla (0/1)",
        "penso_en_expareja" to "Pensaste en una expareja sin verla (0/1)", "penso_en_familia" to "Pensaste en tu familia sin verla (0/1)",
        "penso_en_amistades" to "Pensaste en una amistad sin verla (0/1)",
        // Lote 2: lugares, cierre, barómetro, libido, notas y metas.
        "animo_cierre_valencia" to "Valencia del «Cómo estás ahora» al cerrar el día (1 a 7)", "animo_cierre_activacion" to "Activación al cerrar el día (1 a 7)",
        "presion_local_hpa" to "Presión del barómetro del teléfono, media del día y calibrada con Open-Meteo (hPa)",
        "cambio_presion_3h" to "Mayor cambio de presión en unas tres horas, con su signo (hPa; negativo = bajó)",
        "presion_media" to "Presión media del barómetro sin calibrar (hPa)", "presion_min" to "Presión mínima del barómetro (hPa)", "presion_max" to "Presión máxima del barómetro (hPa)",
        "libido_max" to "Libido máxima registrada en el día (0 nada a 5 dominante), seguimiento privado", "n_libido" to "Registros de libido del día, seguimiento privado",
        "foco" to "Libido: en qué estaba el foco (pensando, viendo, conversando, nada, otro)", "cierre" to "Es el «Cómo estás ahora» al cerrar el día (0/1)",
        "tipo_lugar" to "Tipo del lugar donde se registró (casa, trabajo, bar…)",
        "vm_notas.csv:tipo" to "idea, pensamiento, proposito o recordatorio", "fijada" to "Fijada arriba (0/1)", "recordar_el" to "Día del recordatorio", "hecha" to "Ya hecha (0/1)",
        "creada" to "Cuándo se creó", "editada" to "Última edición", "horizonte" to "corto (hasta 3 meses), mediano (hasta un año) o largo",
        "fecha_objetivo" to "Fecha objetivo", "vm_metas.csv:estado" to "activa, pausada, lograda o abandonada", "medida_tipo" to "habito, variable o vacío (progreso a mano)",
        "medida_clave" to "Hábito o variable del panel ligada a la meta", "medida_modo" to "dias (días que cumplen), suma o media", "umbral" to "Desde cuánto cuenta un día",
        "objetivo" to "Meta numérica de la medida", "valor_actual" to "Lo que lleva la medida", "vm_metas.csv:progreso" to "Progreso de 0 a 1",
        "revision" to "Cada cuánto se revisa: semanal, mensual, fecha o ninguna", "ultima_revision" to "Última revisión", "anio_proposito" to "Año del propósito (si es de año nuevo)",
        "historial" to "Cambios: fecha:campo:antes>después (lista)",
        // Lote 3: suplementos, laptop, red de personas y experimentos.
        "creatina_saturacion_pct" to "Saturación de creatina estimada en el músculo (0 a 100 %; modelo simplificado, no es medición)",
        "probioticos_porciones" to "Porciones de probióticos del día (anotadas más las leídas en tu bitácora de comidas)",
        "probioticos_estimado" to "Porciones de probióticos leídas en tu bitácora de MacroFactor (estimado)",
        "laptop_min" to "Minutos en la laptop (Cold Turkey, suma de apps)", "laptop_web_min" to "Minutos en sitios web de la laptop (Cold Turkey)",
        "laptop_productivo_min" to "Minutos en apps y sitios productivos (categoría editable; sin contar el navegador mismo)",
        "laptop_ocio_min" to "Minutos en apps y sitios de ocio", "laptop_social_min" to "Minutos en apps y sitios sociales", "laptop_otro_min" to "Minutos en el resto de apps y sitios",
        "suplemento" to "Identificador del suplemento", "vm_suplementos.csv:cantidad" to "Cantidad tomada (en la unidad del suplemento)", "estimado" to "Inferido de tu bitácora de comidas (0/1)",
        "dias_mencion" to "Días en que la mencionaste en el diario o los momentos", "vm_red_nodos.csv:grado" to "Con cuántas personas está unida: líneas que dijiste (salvo «no se conocen») o con 1 día o más juntas. La Red de la app pide 2 días y muestra 45 personas; aquí van hasta 400", "vm_red_nodos.csv:grupo" to "Grupo de la relación: familia, pareja, amistad, trabajo u otro (el color de la Red)",
        "tipo_declarado" to "Cómo se conocen según tú: se_conocen, familia, pareja, colegas, no_se_conocen (vacío = no lo dijiste)",
        "dias_juntos" to "Días en que aparecen las dos el mismo día", "vm_laptop.csv:minutos" to "Minutos usados ese día", "vm_laptop.csv:categoria" to "Categoría (productivo, ocio, social, otro)",
        "presencias" to "Cómo estuviste con cada @persona del diario: persona:estuve, persona:pense o persona:mensajes (lista)",
        "presencias_revisado" to "Ya revisaste ese día quién estuvo y en quién solo pensaste (0/1); antes de la versión 10 todo cuenta como «estuve»",
        // Entrega 10b (v13): avisos de ánimo, campañas, experimentos, ventana del día, días especiales y Tu gente.
        "avisos_planeados" to "Avisos de ánimo planeados ese día (desde la v13; vacío = sin registro o con los avisos apagados)",
        "avisos_mostrados" to "Avisos de ánimo que se mostraron: menos de 50 minutos tarde y con permiso de notificaciones",
        "avisos_respondidos" to "Avisos mostrados que tienen respuesta (un registro «muestreo» con esa hora de aviso, con o sin valencia)",
        "tasa_respuesta" to "avisos_respondidos entre avisos_mostrados (vacío si no se mostró ninguno)",
        "campanas_n" to "Campañas que tenías aceptadas ese día (vacío antes de tu primera campaña)",
        "vm_experimentos.csv:id" to "Identificador del experimento", "vm_experimentos.csv:nombre" to "Nombre del experimento",
        "vm_experimentos.csv:pregunta" to "La pregunta que querías responder", "vm_experimentos.csv:hipotesis" to "Lo que creías que pasaría, escrito antes de empezar",
        "vm_experimentos.csv:sellada" to "Cuándo se selló la hipótesis (fecha y hora)", "vm_experimentos.csv:accion" to "Lo que cambiabas en las semanas «SÍ»",
        "vm_experimentos.csv:resultado" to "La variable del panel que se mide", "vm_experimentos.csv:diseno" to "alterno (semanas sorteadas por pares) o antes_despues",
        "vm_experimentos.csv:inicio" to "Primer día (un lunes)", "vm_experimentos.csv:semanas" to "Semanas del plan",
        "vm_experimentos.csv:lavado_dias" to "Días que no cuentan tras cada cambio de condición", "vm_experimentos.csv:plan" to "Plan por semana: 1 con la acción, 0 sin ella (lista)",
        "vm_experimentos.csv:activo" to "Sigue activo (0/1)", "vm_experimentos.csv:plantilla" to "Plantilla de la que salió (vacío = propio)",
        "vm_experimentos.csv:fin" to "Día lógico en que lo terminaste (desde la v13; los días después no cuentan)",
        "vm_experimentos.csv:hecho" to "«¿Lo hiciste hoy?»: fecha:1 sí, fecha:0 no (lista; una fecha que no aparece = sin respuesta, no «no»)",
        "vm_pantalla.csv:ventana" to "logica = de las 05:00 a las 05:00 del día siguiente (desde la v13); calendario = de 0:00 a 24:00 (antes)",
        "sueno_descartadas" to "Lo que llegó del reloj esa noche y marcaste que no cuenta (desde la v14)",
        "ventana_pantalla" to "Ventana del uso del teléfono ese día: logica (05:00 a 05:00, desde la v13) o calendario (0:00 a 24:00)",
        "ventana_pasos" to "Ventana de los pasos ese día: logica (05:00 a 05:00, desde la v13) o calendario (0:00 a 24:00)",
        "dia_especial" to "Ese día cae un cumpleaños, un aniversario o una fecha propia (0/1)",
        "dia_especial_tipo" to "Qué cae: mi_cumpleanos, cumpleanos, fallecimiento, aniversario u otra (lista)",
        "vm_personas.csv:persona" to "Nombre con que la mencionas (sin @)", "vm_personas.csv:relacion" to "Qué es para ti ahora",
        "vm_personas.csv:detalle" to "Parentesco o detalle libre", "vm_personas.csv:cercania" to "Qué tan cerca (1 a 7, círculos de Aron)",
        "vm_personas.csv:desde" to "Desde cuándo la tienes clasificada", "nacimiento" to "AAAA-MM-DD, o --MM-DD si no se sabe el año",
        // Entrega 10d: el panel de la app para R (P-30) y sus parejas obvias (P-31).
        "vm_variables.csv:variable" to "Nombre de la columna en vm_panel_extendido.csv", "vm_variables.csv:nombre" to "Qué es, como lo dice la app",
        "vm_variables.csv:grupo" to "Grupo de la variable en la app", "vm_variables.csv:privada" to "Seguimiento privado (0/1): la app no lo cruza; en R, solo con privadas = TRUE",
        "vm_familias.csv:familia" to "Nombre de la familia (la regla de la app que la produce)", "vm_familias.csv:variable" to "Columna de vm_panel_extendido.csv (o su nombre en vm_panel.csv)",
        "vm_familias.csv:lado" to "0 = obvia con todas las de la familia; 1 o 2 = obvia solo con las del otro lado",
        "fallecimiento" to "AAAA-MM-DD, o --MM-DD si no se sabe el año", "edad" to "Años cumplidos hoy, o los que tenía al morir (vacío sin el año)",
        "vm_personas.csv:historial" to "Relaciones de antes: relacion:hasta (lista)",
        "vm_fechas_propias.csv:nombre" to "Qué es", "vm_fechas_propias.csv:fecha" to "AAAA-MM-DD, o --MM-DD si no se sabe el año",
        "mes_dia" to "Mes y día en que se repite (MM-DD)", "vm_fechas_propias.csv:anio" to "Año, si se sabe", "vm_fechas_propias.csv:tipo" to "cumpleanos, aniversario u otra",
        // Cartas
        "vm_cartas.csv:para" to "A quién va (sin @); vacío = a ti en el futuro", "escrita" to "Día en que se escribió", "abrir_en" to "Día desde el que se puede abrir",
        "abierta" to "Ya se abrió (0/1)", "abierta_el" to "Día en que se abrió", "vm_cartas.csv:lugar" to "Ciudad donde se escribió", "vm_cartas.csv:tema" to "Tema de la app al escribirla",
        "vm_cartas.csv:texto" to "Texto de la carta (vacío mientras siga sellada)",
        // Conocerte
        "campo" to "Qué dato de «Conocerte»", "valor" to "Cómo está ahora", "vm_perfil.csv:antes" to "Cómo estaba", "despues" to "Cómo quedó",
        // Brújula política
        "vm_politica.csv:economia" to "Eje economía: −100 igualdad y Estado, +100 mercado y empresa", "vm_politica.csv:autoridad" to "Eje autoridad: −100 libertad individual, +100 orden y autoridad",
        "vm_politica.csv:valores" to "Eje valores: −100 cambio, +100 tradición", "vm_politica.csv:mundo" to "Eje mundo: −100 apertura, +100 soberanía",
        "vm_politica.csv:ambiente" to "Eje ambiente: −100 proteger el ambiente, +100 crecimiento primero",
    )

    /** De qué tabla salió cada prefijo del panel extendido. */
    private val PREFIJOS_EXT = mapOf("salud_" to "vm_salud.csv", "pant_" to "vm_pantalla.csv", "clima_" to "vm_entorno.csv", "fotos_" to "vm_fotos.csv",
        "agenda_" to "vm_agenda.csv", "notif_" to "vm_notificaciones.csv", "mov_" to "vm_actividad.csv", "com_" to "vm_comunicacion.csv",
        "cx_" to "vm_conexiones.csv", "google_" to "vm_google_dia.csv", "nutr_" to "vm_nutricion.csv", "gym_" to "vm_entrenos.csv")

    private fun describir(archivo: String, col: String): String {
        D["$archivo:$col"]?.let { return it }
        if (archivo == "vm_panel_extendido.csv") {
            D["vm_panel.csv:$col"]?.let { return it }
            io.github.ruddy2003.vestigium.ciencia.Dinamicas.nombres[col]?.let { return it }
            PREFIJOS_EXT.entries.firstOrNull { col.startsWith(it.key) }?.let { (p, a) -> return describir(a, col.removePrefix(p)) + " (de ${a.removePrefix("vm_").removeSuffix(".csv")})" }
            D[col]?.let { return it }
            io.github.ruddy2003.vestigium.ciencia.Estadistica.variables(dinamicas = false, privadas = true).firstOrNull { it.id == col }?.let { return it.nombre }
        }
        D[col]?.let { return it }
        return when {
            col.startsWith("supl_") -> {
                val resto = col.removePrefix("supl_"); val id = resto.substringBeforeLast('_'); val s = Repo.config.suplementos.firstOrNull { it.id == id }
                val n = s?.nombre ?: id
                when (resto.substringAfterLast('_')) { "dosis" -> "$n: dosis del día (${s?.unidad ?: ""})"; "tomado" -> "$n: lo tomaste (0/1)"; "porciones" -> "$n: porciones del día"
                    else -> if (s?.modelo == "carga") "$n: reserva estimada (0 a 100 %)" else "$n: cantidad estimada en el cuerpo al final del día" }
            }
            col.startsWith("horas_tipo_") -> "Horas en lugares de tipo ${Catalogo.nombreTipoLugar(col.removePrefix("horas_tipo_")).lowercase()}"
            col.startsWith("ctx_") -> "Contexto del día: ${col.removePrefix("ctx_").replace('_', ' ')} (0/1)"
            col.startsWith("campana_") -> "En campaña ese día: ${io.github.ruddy2003.vestigium.ciencia.Campanas.reto(col.removePrefix("campana_").let { if (it.startsWith("hab_")) "habito:" + it.removePrefix("hab_") else it })?.nombre ?: col.removePrefix("campana_")} (0/1; la tenías aceptada, la cumplieras o no)"
            col.startsWith("hab_") -> "Hábito cumplido: ${Repo.config.habitos.firstOrNull { it.id == col.removePrefix("hab_") }?.nombre ?: col.removePrefix("hab_")} (0/1)"
            col.startsWith("m_") -> "Momentos de tipo ${col.removePrefix("m_")} (registros de antes de unir ánimo y momentos)"
            col.startsWith("n_") && Catalogo.sentimiento(col.removePrefix("n_")) != null -> "Registros con ${Catalogo.nombreSentimiento(col.removePrefix("n_")).lowercase()} en el día"
            col.startsWith("n_") && Catalogo.PENSAMIENTOS.any { it.id == col.removePrefix("n_") } -> "Registros pensando: ${Catalogo.PENSAMIENTOS.first { it.id == col.removePrefix("n_") }.nombre.lowercase()}"
            col == "n_no_se" -> "Registros en que no supiste nombrar lo que sentías"
            col.startsWith("sueno_") && Catalogo.opcionSueno(col.removePrefix("sueno_")) != null -> "Sueño: ${Catalogo.opcionSueno(col.removePrefix("sueno_"))!!.nombre.lowercase()} (0/1, solo en días con categorías marcadas)"
            col.startsWith("item_") -> "Respuesta al ítem ${col.removePrefix("item_")}"
            archivo == "vm_politica.csv" && col.startsWith("r_") -> Politica.AFIRMACIONES.firstOrNull { it.id == col.removePrefix("r_") }?.let { "«${it.texto}» (1 muy en desacuerdo a 5 muy de acuerdo)" } ?: col
            col.startsWith("puntaje_") -> "Puntaje ${col.removePrefix("puntaje_")}"
            col.startsWith("app_") -> "Notificaciones de ${col.removePrefix("app_").replace('_', ' ')}"
            col.startsWith("wifi_") -> "Minutos en la red ${col.removePrefix("wifi_").replace('_', ' ')}"
            col.startsWith("luz_h") -> "Luz ambiente media a las ${col.removePrefix("luz_h")}:00 (lux)"
            col.startsWith("series_") -> "Series para ${col.removePrefix("series_").replace('_', ' ')}"
            col.startsWith("volumen_") -> "Volumen para ${col.removePrefix("volumen_").removeSuffix("_kg").replace('_', ' ')} (kg)"
            archivo == "vm_nutricion.csv" || archivo == "vm_comidas.csv" -> "Nutriente de MacroFactor: ${col.replace('_', ' ')}"
            Regex("^h\\d{2}$").matches(col) -> "Notificaciones a las ${col.drop(1)}:00"
            col.startsWith("flow_") || col.startsWith("sdt_necesidad_") || col.startsWith("apreciacion_") -> "Opción elegida (0/1)"
            else -> ""
        }
    }

    /**
     * Las variables del panel que van por día de calendario (0:00 a 24:00) y no por día lógico (de 5:00 a 5:00): lo que el reloj
     * y Health Connect dan por día (salvo pasos y sueño), la agenda, el clima, la luna y las ciudades (P-16).
     */
    val CALENDARIO: Set<String> = setOf("fc_media", "fc_reposo", "spo2", "spo2_min", "cal_activas", "temp_piel", "resp_rpm", "peso_kg",
        "eventos", "min_ocupado", "t_max", "t_min", "precip_mm", "nubes_pct", "luz_horas", "sol_horas", "presion_hpa", "aqi_us", "pm25",
        "luna_ilum", "ciudades_dia", "fuera_de_casa", "extranjero", "ciudad_nueva", "unesco")

    private fun conVentana(archivo: String, col: String, texto: String): String =
        if ((archivo == "vm_panel.csv" || archivo == "vm_panel_extendido.csv") && (col in CALENDARIO || col.startsWith("ciu_") || col.startsWith("pais_")))
            "$texto · por día de calendario (0:00 a 24:00)" else texto

    /** Las 11 columnas de vm_panel.csv que en el panel de la app se llaman distinto (P-30): se dice en las dos. */
    private fun conEquivalente(archivo: String, col: String, texto: String): String {
        if (archivo != "vm_panel.csv" && archivo != "vm_panel_extendido.csv") return texto
        Exportar.EQUIVALENTES[col]?.let { (app, f) -> return "$texto · en el panel de la app: ${app}" + if (f != 1.0) " (en horas)" else "" }
        val de = Exportar.EQUIVALENTES.filterValues { it.first == col }.keys
        return if (de.isEmpty()) texto else "$texto · en vm_panel.csv: ${de.joinToString()}"
    }

    fun csv(tablas: List<Exportar.Tabla>): String = Exportar.csv(filas(tablas))

    /** Una fila por columna de cada tabla: archivo, columna, tipo, descripción y de qué tabla es. */
    fun filas(tablas: List<Exportar.Tabla>): List<Map<String, Any?>> {
        val filas = ArrayList<Map<String, Any?>>()
        for (t in tablas) {
            val cols = LinkedHashSet<String>(); t.filas.forEach { cols.addAll(it.keys) }
            for (c in cols) filas.add(linkedMapOf("archivo" to t.archivo, "columna" to c, "tipo" to tipo(t.filas.map { it[c] }, c),
                "descripcion" to conEquivalente(t.archivo, c, conVentana(t.archivo, c, describir(t.archivo, c))), "tabla" to t.descripcion))
        }
        listOf("ts" to "fecha_hora", "lat" to "numero", "lon" to "numero", "precision_m" to "numero", "fuente" to "texto", "ms" to "entero").forEach { (c, ti) ->
            filas.add(linkedMapOf("archivo" to "vm_ubicaciones.csv", "columna" to c, "tipo" to ti,
                "descripcion" to when (c) { "fuente" -> "De dónde salió la muestra (fondo, momento, cronología, foto…)"; "ms" -> "Milisegundos desde 1970 (la misma hora exacta, para restaurar)"; else -> describir("vm_ubicaciones.csv", c) },
                "tabla" to "Muestras de ubicación"))
        }
        listOf("clave" to ("texto" to "Identificador de la foto (no es la foto)"), "ms" to ("entero" to "Hora de la foto en milisegundos"), "ts" to ("fecha_hora" to "Hora de la foto"),
            "lat" to ("numero" to "Latitud"), "lon" to ("numero" to "Longitud"), "aparato" to ("texto" to "Con qué cámara o teléfono se tomó"),
            "origen" to ("texto" to "Carpeta o fuente (cámara, WhatsApp, Takeout…)"), "ms_archivo" to ("entero" to "Fecha del archivo, para detectar relojes equivocados"),
            "fuente" to ("texto" to "foto (del teléfono) o foto_takeout (de Google Fotos)")).forEach { (c, td) ->
            filas.add(linkedMapOf("archivo" to "vm_fotos_catalogo.csv", "columna" to c, "tipo" to td.first, "descripcion" to td.second, "tabla" to "Catálogo de fotos con lugar (solo hora y coordenadas)"))
        }
        return filas
    }
}
