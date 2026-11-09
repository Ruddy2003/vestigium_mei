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
        "con_quien" to "Con quién estabas en el registro de ánimo (lista: solo, pareja, amigos, familia, hijos, companeros, desconocidos, mascota, mensaje = por mensaje o llamada; «mensaje» puede ir con «solo»)",
        "intensidades" to "Cuánto de cada emoción (lista emocion:n; 1 un poco, 2 bastante, 3 mucho); solo las que lo dijiste (esquema 17)",
        "pensamientos" to "En qué pensabas, varios (lista; esquema 17). En registros de antes, el único pensamiento",
        "detonante_otro" to "El detonante escrito cuando elegiste «otro» (seguimiento privado, esquema 17)",
        "habitos_tri" to "Los hábitos de ese día se contestaron con sí / no / sin contestar (0/1): con 1, un hábito sin respuesta queda vacío en hab_*",
        "ctx_otro" to "Otro contexto del día, escrito a mano (esquema 17)",
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
        "emocion_otra" to "El sentimiento que escribiste cuando elegiste «otro»", "pensamiento" to "En qué pensabas: el primero (pensando_en, preocupacion, idea, recuerdo, plan, vueltas, autocritica, deseo, trabajo, cuerpo, imaginando, nada); todos, en pensamientos",
        "alcohol_nivel" to "Qué tan pedo estabas en ese momento (0 sobrio a 10 vómito y mucho mareo; interpolado de tus lecturas)",
        "cannabis_nivel" to "Nivel con cannabis en ese momento (0 a 10; interpolado)", "otra_nivel" to "Nivel con otra sustancia en ese momento (0 a 10; interpolado)",
        "sueno_categorias" to "De qué fue el sueño (lista de ejes: per_ personajes, esc_ escenario, int_ interacción, tema_ temas típicos, res_ cómo me fue, sen_ sensaciones, est_ estructura, emo_ emociones, desp_ al despertar; en los días de antes del esquema 17, el tipo «recurrente» aparece como est_recurrente)",
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
        "desfase_utc_h" to "Horas de diferencia con UTC a mediodía de ese día, en la zona donde estaba el teléfono (−6 en la Ciudad de México). Cada hora de las tablas es la hora local de entonces",
        "cambio_horario" to "Ese día lógico cambió la hora del teléfono: horario de verano o un viaje a otra zona (0/1). El día duró más o menos de 24 horas",
        "clima_reanalisis" to "El clima de ese día viene del archivo histórico (reanálisis tipo ERA5) (1) o de modelos de pronóstico (0). Los días de pronóstico se cambian por el reanálisis cuando pasan 80 días",
        "vm_entorno.csv:fuente" to "De dónde salió el clima: archivo (reanálisis histórico de Open-Meteo, tipo ERA5) o pronostico (modelos de pronóstico; se cambia por el archivo a los 80 días). El índice UV solo viene del pronóstico",
        "festivo" to "Día de descanso obligatorio de la Ley Federal del Trabajo de México, art. 74 (0/1; vacío si en «Conocerte» no vives en México)",
        "dia_libre" to "Sábado, domingo o festivo (0/1): la regla de día libre del jet lag social; una noche es libre si despiertas en un día libre", "con_diario" to "Hay registro del diario ese día (0/1)",
        "valencia" to "Qué tan agradable fue (1 muy desagradable a 7 muy agradable; Russell)", "activacion" to "Energía (1 muy baja a 7 muy alta)",
        "dolor" to "Dolor o molestia física (0 nada a 5 muy fuerte)", "sueno_calidad" to "Calidad del sueño percibida (1 a 5)",
        "valencia_muestreo" to "Promedio de la valencia en los avisos del día", "n_muestreos" to "Avisos de ánimo respondidos (registros «muestreo», con o sin valencia)", "n_momentos" to "Momentos registrados (sin contar avisos; incluye los privados: impulsos, recaídas y libido)",
        "gasto_total" to "Gasto del día (tu moneda); 0 desde tu primer gasto, en días con diario; antes, vacío", "vm_panel_extendido.csv:personas" to "Personas distintas nombradas con @ ese día, en el diario y en los momentos no privados; vale 0 los días con diario escrito o con un momento que nombra a alguien, y si no, vacío", "gasto_imprevisto_monto" to "Suma de los gastos imprevistos del día (tu moneda); gasto_imprevisto solo dice si hubo alguno (0/1)", "error_prediccion_signo" to "Valencia del día menos la predicción de la noche anterior (−6 a +6): positivo, el día salió mejor de lo predicho; error_prediccion es su valor absoluto", "ingreso_total" to "Ingresos del día (tu moneda); 0 desde tu primer ingreso, en días con diario; antes, vacío", "fijo" to "Ingreso que se repite, como sueldo o beca (0/1)", "schema_v" to "Versión del esquema de datos (0 = importado de Obsidian o de la versión web sin versión propia)", "origen" to "De dónde vino el registro (app, web, obsidian, widget…)",
        "retroactivo" to "La valencia se llenó otro día, después (0/1). Desde el esquema 13 se decide con ts_valencia; los días de antes, por si el registro del día se creó después",
        "ts_valencia" to "Cuándo se llenó o se cambió por última vez la valencia del día (desde el esquema 13)", "dia_dificil" to "Marcado como día difícil (0/1)",
        "ts_creado" to "Cuándo se anotó el registro. En momentos, gastos, ingresos, tomas, consumos y tramos, desde el esquema 14 (vacío en los de antes); comparado con su hora dice si se anotó tarde",
        "ts_editado" to "Cuándo se corrigió por última vez después de anotarlo (en momentos, gastos e ingresos, desde el esquema 14)",
        "version_noche" to "Con qué orden de preguntas se llenó la hoja de la noche (desde el esquema 14): 1 = el orden anterior; 2 = el orden nuevo, con el ánimo del momento primero. Vacío = no pasó por la hoja o es de antes",
        "hitos" to "Fechas señaladas del día: la frase de cada una (lista; desde el esquema 21, con los hitos de antes; el tipo, en vm_senaladas.csv)", "flow" to "Experiencia de flujo (Csikszentmihalyi)", "sdt_necesidad" to "Necesidad psicológica más presente (Deci y Ryan)",
        "apreciacion" to "Evaluación del estrés (Lazarus)", "sueno_recordado" to "Recordó un sueño (0/1)", "sueno_valencia" to "Qué tan agradable fue el sueño (1 a 7)",
        "sueno_lucidez" to "Lucidez del sueño", "sueno_vividez" to "Qué tan claro lo recuerdas: vago (solo fragmentos), moderado (partes claras) o muy_vivido (casi completo y nítido)", "sueno_tipo" to "Tipo de sueño (solo hasta el esquema 17: mundano, ansioso_pesadilla, bizarro_surreal, recurrente; después, ver sueno_rareza y sueno_pesadilla)",
        "sueno_rareza" to "Qué tan raro fue el sueño: realista, raro (pero posible) o imposible (o surreal); en días de antes del esquema 17, deducido del tipo (mundano = realista, bizarro = imposible)",
        "sueno_pesadilla" to "Si fue pesadilla: angustia fuerte o te despertó (TRUE/FALSE; vacío = no se dijo); en días de antes del esquema 17, TRUE si el tipo era ansioso o pesadilla", "sueno_temas" to "Personas y temas del sueño (lista)",
        "sueno_texto" to "Narración del sueño", "mejor_momento" to "Lo mejor del día", "peor_momento" to "Lo peor del día", "foto_archivo" to "Nombre de la copia de la foto del día, en la carpeta fotos_dia/ del respaldo o de la cápsula del año (vacío = sin foto o sin copia)",
        "impulso" to "Intensidad del impulso (0 a 5), seguimiento privado", "recaida" to "Hubo recaída (0/1), seguimiento privado", "detonantes" to "Detonantes (lista)",
        "medicamentos" to "Medicamentos tomados (lista)", "prediccion_manana" to "Predicción del ánimo de mañana (1 a 7)",
        // Momentos
        "tipo" to "Tipo de registro", "intensidad" to "Intensidad: 1 a 5 en impulsos y recaídas, 0 a 5 en libido", "lugar" to "Nombre del lugar marcado más cercano",
        "aviso_ts" to "Hora planeada del aviso que se respondió", "mostrado_ts" to "Hora en que el aviso salió de verdad (desde el esquema 20; vacío antes o si no se sabe)",
        "latencia_min" to "Minutos entre que el aviso salió de verdad y la respuesta (desde el esquema 20; vacío antes)", "latencia_plan_min" to "Minutos entre la hora planeada del aviso y la respuesta (incluye el retraso del propio aviso)",
        "ruido_db" to "Ruido ambiente aproximado al registrar (dB relativos, sin calibrar)", "luz_lux" to "Luz ambiente al registrar (lux)",
        // Gastos
        "monto" to "Importe (tu moneda)", "vm_gastos.csv:categoria" to "Categoría de gasto", "vm_ingresos.csv:categoria" to "Categoría de ingreso", "subcategoria" to "Subcategoría dentro de la categoría (desde el esquema 14; vacío = sin subcategoría)", "imprevisto" to "Gasto no planeado (0/1)", "tipo_otro" to "Bloque de enfoque de tipo «otro»: en qué fue, escrito a mano (desde el esquema 17)", "se_repite" to "Gasto que se repite, como renta, suscripción o servicios (0/1; desde el esquema 17)",
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
        "vm_visitas.csv:cierre" to "Cómo se supo la salida: geocerca (Android avisó), otro_lugar (llegaste a un lugar lejano sin aviso de salida), ubicaciones (tus puntos ya te ponían fuera), reconstruida (la visita salió de tus ubicaciones al guardar el lugar), manual (la corregiste tú); vacío = abierta o anterior al esquema 19 sin corregir",
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
        "autor" to "Autor, dirección o estudio", "anio" to "Año de publicación o estreno", "estado" to "quiero (pendiente), en_curso, pausado, terminado o abandonado",
        "calificacion" to "Calificación (1 a 10)", "progreso" to "Avance (páginas, minutos, capítulos u horas)", "veces" to "Veces leído o visto",
        "etiquetas" to "Etiquetas (lista)", "agregado" to "Cuándo lo agregaste", "dias" to "Días entre inicio y fin",
        "vm_biblioteca.csv:prioridad" to "Si está pendiente, qué tan pronto la quieres: pronto, cuando_pueda o algun_dia; vacío = sin decir (desde el esquema 18)",
        "vm_biblioteca.csv:recomendo" to "Quién te la recomendó: personas de tu gente (lista; desde el esquema 18)",
        "vm_biblioteca.csv:donde" to "Dónde verla o conseguirla: plataforma, biblioteca, quien te la presta… (desde el esquema 18)",
        // Listas libres (esquema 18)
        "vm_listas.csv:lista_id" to "Identificador de la lista", "vm_listas.csv:lista" to "Nombre de la lista (lugares por visitar, cosas por comprar…)",
        "vm_listas.csv:texto" to "El elemento de la lista", "vm_listas.csv:enlace" to "Enlace del elemento",
        "vm_listas.csv:prioridad" to "Qué tan pronto: pronto, cuando_pueda o algun_dia; vacío = sin decir", "vm_listas.csv:agregado" to "Cuándo lo agregaste",
        "vm_listas.csv:hecho" to "Cuándo lo hiciste (lo tachaste); vacío = pendiente", "vm_listas.csv:dias_hasta_hecho" to "Días entre agregarlo y hacerlo",
        // Comunicación
        "entrantes" to "Llamadas entrantes contestadas", "salientes" to "Llamadas salientes", "perdidas" to "Llamadas perdidas", "rechazadas" to "Llamadas rechazadas o bloqueadas",
        "min_hablado" to "Minutos hablando por teléfono", "llamada_max_min" to "Llamada más larga (min)", "sms_recibidos" to "SMS recibidos", "sms_enviados" to "SMS enviados",
        "contactos" to "Personas distintas con las que hubo llamada o SMS", "llamadas" to "Llamadas entrantes y salientes", "sms" to "SMS enviados y recibidos",
        // Conexiones
        "muestras" to "Muestras tomadas (una cada ~15 min)", "sin_wifi_min" to "Minutos sin wifi: el wifi estaba apagado o el sistema dijo que no había red wifi (fuera de casa, normalmente). Desde el esquema 15 no incluye los minutos sin dato; antes, sí",
        "wifi_sin_dato_min" to "Minutos en que no se pudo saber si había wifi (sin permiso o sin respuesta del sistema); no cuentan como sin wifi (esquema 15)", "wifi_total_min" to "Minutos conectado a cualquier wifi",
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
        "cafeina_ultima_h" to "Hora de la última cafeína del día lógico en decimal, de MacroFactor y de lo anotado en la app (antes de las 5:00 suma 24)", "alimento" to "Alimento", "porcion" to "Unidad de la porción", "vm_comidas.csv:cantidad" to "Cantidad de porciones",
        "peso_porcion_g" to "Gramos por porción", "entrenos" to "Entrenamientos del día (lista)", "vm_entrenos.csv:minutos" to "Minutos de entrenamiento",
        "fuerza_ref_kg" to "Fuerza de referencia: la mejor 1RM estimada (Epley, series de hasta 12 reps, sin calentamiento) del día en press inclinado con mancuernas; vacío si ese día no lo hiciste (esquema 15)",
        "vm_series.csv:referencia" to "La serie es del ejercicio de referencia, press inclinado con mancuernas (0/1)",
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
        // Esquema 21: masturbación e intimidad (seguimiento privado), episodios, fechas señaladas, varias tomas y la extra del enfoque.
        "estimulo" to "Masturbación: qué la acompañó (pornografia, fantasia, recuerdo, mensajes, ninguno, otro); con pornografía cuenta como recaída (seguimiento privado)",
        "estimulo_otro" to "Masturbación: el estímulo escrito cuando elegiste «otro»",
        "sitio" to "Masturbación o intimidad: dónde (cama, bano, sofa, otro)", "sitio_otro" to "El sitio escrito cuando elegiste «otro»",
        "practicas" to "Intimidad: qué pasó (lista: mensajes_eroticos, llamada_erotica, beso_breve, beso_profundo, caricias_ropa, caricias_intimas, estimulacion_manual, oral_dar, oral_recibir, coito_vaginal, coito_anal, no_convencional, juguetes, otra), seguimiento privado",
        "practica_otra" to "Intimidad: la práctica escrita cuando elegiste «otra»",
        "nivel" to "Intimidad: el nivel más alto de sus prácticas (0 a distancia, 1 besos, 2 caricias, 3 oral, 4 coito; vacío si solo hubo prácticas sin nivel)",
        "vm_momentos.csv:satisfaccion" to "Intimidad o masturbación: satisfacción (1 me dejó mal a 5 me dejó muy bien)", "placer" to "Intimidad: placer (1 nada a 5 muchísimo)",
        "conexion" to "Intimidad: cercanía con la otra persona (1 distante a 5 en sintonía)", "comodidad" to "Intimidad: qué tan a gusto y con control (1 muy incómodo, no lo quería, a 5 muy a gusto)",
        "orgasmo" to "Intimidad: orgasmo propio (si, no, no_se)", "orgasmo_otra" to "Intimidad: orgasmo de la otra persona (si, no, no_se)",
        "proteccion" to "Intimidad: protección (barrera, otro_metodo, ninguno, no_aplica)", "iniciativa" to "Intimidad: quién empezó (yo, otra, ambos)",
        "vm_momentos.csv:personas" to "Personas del registro: el «con quién» (en la intimidad y en un episodio de pelea) y las @personas del texto (lista)",
        "episodio" to "Episodio: qué fue (crisis, llanto, pelea, migrana, enfermedad, accidente, otro)", "episodio_otro" to "El episodio escrito cuando elegiste «otro»",
        "vm_momentos.csv:sintomas" to "Episodio de crisis: los síntomas (lista: corazon, aire, temblor, mareo, sudor, miedo, irrealidad)",
        "vinculo" to "El registro gemelo: la recaída que creó una masturbación con pornografía, y al revés",
        "bajo_sustancia" to "Intimidad o masturbación con alcohol o cannabis encima: un consumo que empezó en las 3 horas de antes o cuyo efecto seguía (0/1; vacío antes del primer consumo de esas dos)",
        "n_masturbacion" to "Masturbaciones registradas en el día (0 desde la primera, en días con diario), seguimiento privado",
        "masturbacion_min" to "Minutos de masturbación en el día, seguimiento privado", "masturbacion_satisfaccion" to "Satisfacción con la masturbación (1 a 5, promedio del día), seguimiento privado",
        "n_intimidad" to "Encuentros íntimos registrados en el día (0 desde el primero, en días con diario), seguimiento privado",
        "intimidad_nivel" to "Nivel más alto de intimidad del día (0 a distancia a 4 coito), seguimiento privado",
        "intimidad_satisfaccion" to "Satisfacción con la intimidad (1 a 5, promedio del día), seguimiento privado",
        "intimidad_comodidad" to "Comodidad en la intimidad (1 a 5, promedio del día), seguimiento privado",
        "n_coito" to "Encuentros con coito en el día, seguimiento privado", "intimidad_distancia" to "Toda la intimidad del día fue a distancia: mensajes o llamadas (0/1), seguimiento privado",
        "n_episodio" to "Episodios del día: crisis, llanto, pelea, migraña, enfermedad, accidente u otro (0 desde el primero, en días con diario)",
        "episodio_intensidad_max" to "Intensidad del episodio más fuerte del día (1 leve a 5 lo peor)",
        "epi_crisis" to "Hubo una crisis de ansiedad o pánico (0/1)", "epi_llanto" to "Hubo llanto o un bajón fuerte (0/1)", "epi_pelea" to "Hubo una pelea o discusión (0/1)",
        "epi_salud" to "Hubo migraña, enfermedad o accidente (0/1)", "hitos_n" to "Fechas señaladas del día (con los hitos de antes)",
        "vm_senaladas.csv:tipo" to "Tipo de fecha señalada: primera_vez, viaje, logro, cambio, perdida, encuentro, celebracion, otro (los hitos de antes son «otro»)",
        "vm_senaladas.csv:tipo_nombre" to "El nombre del tipo", "vm_senaladas.csv:texto" to "La frase de la fecha señalada",
        "plan_min" to "Minutos planeados del bloque (vacío en el cronómetro libre o lo anotado a mano); min es lo que duró de verdad", "extra_min" to "Minutos de más sobre lo planeado (min − plan_min; negativo si terminaste antes)",
        // Ideario (esquema 16).
        "vm_ideas.csv:titulo" to "La idea en una línea (opcional)", "vm_ideas.csv:texto" to "Lo que entendiste, con tus palabras (con @personas y #temas)",
        "vm_ideas.csv:origen" to "leido, visto (o escuchado), contado (te lo contaron) o propia; vacío = sin decir",
        "vm_ideas.csv:quien" to "Autores o invitados que lo dicen (lista; no son personas de Tu gente)", "vm_ideas.csv:obra_id" to "Obra de la que salió la idea",
        "vm_ideas.csv:obra_titulo" to "Título de esa obra (vacío si se borró)", "vm_ideas.csv:cita_id" to "Cita de la obra de la que salió la idea",
        "vm_ideas.csv:fuente" to "De dónde salió, escrito a mano o al compartir (video, conversación, sitio…)", "vm_ideas.csv:enlace" to "Enlace de la fuente",
        "vm_ideas.csv:donde" to "Minuto o página", "vm_ideas.csv:postura" to "Cómo la tienes: convence, piensa (te hace pensar), duda o desacuerdo; vacío = sin decir",
        "vm_ideas.csv:n_revisiones" to "Veces que la respondiste en «Una idea de antes»", "vm_ideas.csv:personas" to "@personas escritas en el texto",
        "vm_ideas.csv:temas" to "Temas de la idea: los del campo «Temas» (desde el esquema 17) y los #temas escritos en el texto", "vm_ideas_revisiones.csv:idea_id" to "Idea que volviste a ver",
        "vm_ideas_revisiones.csv:fecha" to "Día lógico en que respondiste", "vm_ideas_revisiones.csv:respuesta" to "sigue (sigo pensando igual), cambio (ya no) o ignorar (ahora no)",
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
        // Entrega 10b (esquema 13): avisos de ánimo, campañas, experimentos, ventana del día, días especiales y Tu gente.
        "avisos_planeados" to "Avisos de ánimo planeados ese día (desde el esquema 13; vacío = sin registro o con los avisos apagados)",
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
        "vm_experimentos.csv:fin" to "Día lógico en que lo terminaste (desde el esquema 13; los días después no cuentan)",
        "vm_experimentos.csv:sentido" to "Criterio sellado: hacia dónde esperabas el cambio (1 sube, -1 baja; vacío = no lo dijiste; esquema 15)",
        "vm_experimentos.csv:umbral" to "Criterio sellado: diferencia mínima que contaría como efecto, en las unidades de la medida (vacío = cualquiera con el intervalo lejos del cero)",
        "vm_experimentos.csv:huella" to "SHA-256 de lo sellado (pregunta, acción, medida, hipótesis, criterio, diseño, inicio, semanas, plan, lavado y hora del sello)",
        "vm_experimentos.csv:intacto" to "La huella coincide con lo que hay ahora (1), cambió después del sello (0) o no tiene huella (vacío, de antes del esquema 15)",
        "vm_experimentos.csv:hecho" to "«¿Lo hiciste hoy?»: fecha:1 sí, fecha:0 no (lista; una fecha que no aparece = sin respuesta, no «no»)",
        "vm_pantalla.csv:ventana" to "logica = de las 05:00 a las 05:00 del día siguiente (desde el esquema 13); calendario = de 0:00 a 24:00 (antes)",
        "sueno_descartadas" to "Lo que llegó del reloj esa noche y marcaste que no cuenta (desde el esquema 14)",
        "ventana_pantalla" to "Ventana del uso del teléfono ese día: logica (05:00 a 05:00, desde el esquema 13) o calendario (0:00 a 24:00)",
        "ventana_pasos" to "Ventana de los pasos ese día: logica (05:00 a 05:00, desde el esquema 13) o calendario (0:00 a 24:00)",
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
                when (resto.substringAfterLast('_')) { "dosis" -> "$n: dosis del día (${s?.unidad ?: ""})"; "tomado" -> "$n: lo tomaste (0/1)"; "porciones" -> "$n: porciones del día"; "n" -> "$n: tomas del día"
                    else -> if (s?.modelo == "carga") "$n: reserva estimada (0 a 100 %)" else "$n: cantidad estimada en el cuerpo al final del día" }
            }
            col.startsWith("horas_tipo_") -> "Horas en lugares de tipo ${Catalogo.nombreTipoLugar(col.removePrefix("horas_tipo_")).lowercase()}"
            col.startsWith("ctx_") -> "Contexto del día: ${col.removePrefix("ctx_").replace('_', ' ')} (0/1)"
            col.startsWith("campana_") -> "En campaña ese día: ${io.github.ruddy2003.vestigium.ciencia.Campanas.reto(col.removePrefix("campana_").let { if (it.startsWith("hab_")) "habito:" + it.removePrefix("hab_") else it })?.nombre ?: col.removePrefix("campana_")} (0/1; la tenías aceptada, la cumplieras o no)"
            col.startsWith("hab_") -> "Hábito cumplido: ${Repo.config.habitos.firstOrNull { it.id == col.removePrefix("hab_") }?.nombre ?: col.removePrefix("hab_")} (0/1; vacío si ese día no lo contestaste, desde el esquema 17)"
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

    /**
     * Los parámetros con que se calculó la columna en esta exportación (H-4-11): si los cambias, toda la historia se recalcula
     * con los nuevos, así que exportaciones de fechas distintas pueden no compararse. Vacío si la columna no tiene.
     */
    private fun conParametros(col: String, texto: String): String {
        fun n(x: Double) = if (x % 1.0 == 0.0) x.toLong().toString() else x.toString()
        val c = Repo.config
        fun deSuplemento(s: Suplemento) = if (s.modelo == "carga")
            "llega al 95 % en ${porNumero(s.carga_dias, "1 día", "${s.carga_dias} días")} con ${n(Suplementos.dosisReferencia(s))} ${s.unidad} al día y se vacía en ${porNumero(s.lavado_dias, "1 día", "${s.lavado_dias} días")}"
            else "vida media de ${n(s.vida_media_h)} h"
        val p = when {
            col == "cafeina_al_dormir_mg" -> "vida media de la cafeína de ${n(c.cafeina_vida_media_h)} h"
            col == "creatina_saturacion_pct" -> c.suplementos.firstOrNull { it.id == "creatina" }?.let { deSuplemento(it) }
            col.startsWith("supl_") && col.endsWith("_nivel") -> c.suplementos.firstOrNull { it.id == col.removePrefix("supl_").removeSuffix("_nivel") }?.let { deSuplemento(it) }
            else -> null
        } ?: return texto
        return "$texto · al exportar: $p (si lo cambias, se recalcula toda la historia)"
    }

    /**
     * Desde qué versión de la app rige la definición actual de cada columna (H-1-8), con qué cambió y su fila del Apéndice D del paper.
     * Clave «archivo:columna» o solo «columna» (vale en todas las tablas que la tengan). Sin entrada = sin cambios desde la v84.
     * Una clave con «*» vale para toda una familia de columnas (por ejemplo «per_*» o «supl_*_nivel»); si dos coinciden, la más larga.
     */
    /** La versión de la app con la que llega el esquema 18 (deseados y listas). */
    private const val VERSION_ESQUEMA_18 = 118
    /** La versión de la app con la que llegan el esquema 19 (cierre de las visitas) y el 20: la v129. */
    private const val VERSION_ESQUEMA_19 = 129
    /** La versión de la app con la que llega el esquema 20 (avisos de ánimo a su hora): la misma v129. */
    private const val VERSION_ESQUEMA_20 = 129
    /** La versión de la app con la que llega el esquema 21 (masturbación e intimidad, episodios, fechas señaladas, varias tomas, la extra del enfoque). */
    private const val VERSION_ESQUEMA_21 = 175
    /** La versión que trae el esquema 22 (respaldo blindado y foto del día copiada, 9 nov 2026). */
    private const val VERSION_ESQUEMA_22 = 177

    private val DESDE: Map<String, Pair<Int, String>> = mapOf(
        "momentos_intensidad" to (85 to "sin momentos privados: ya no incluye impulsos (D.4)"),
        "n_madrugada" to (85 to "sin momentos privados: ya no cuenta impulsos (D.4)"),
        "n_manana" to (85 to "sin momentos privados: ya no cuenta impulsos (D.4)"),
        "n_noche" to (85 to "sin momentos privados: ya no cuenta impulsos (D.4)"),
        "n_tarde" to (85 to "sin momentos privados: ya no cuenta impulsos (D.4)"),
        "avisos_mostrados" to (86 to "nueva: avisos de ánimo mostrados en el día (D.15)"),
        "avisos_planeados" to (86 to "nueva: avisos de ánimo planeados en el día (D.15)"),
        "avisos_respondidos" to (86 to "nueva: avisos mostrados que se respondieron (D.15)"),
        "campana_*" to (86 to "nueva: 1 los días con el reto aceptado, desde la primera campaña (D.16)"),
        "campanas_n" to (86 to "nueva: campañas aceptadas ese día (D.16)"),
        "desbloqueos" to (86 to "por día lógico, de 05:00 a 05:00 (D.19)"),
        "dia_especial" to (86 to "nueva: cumpleaños, aniversario o fecha propia (D.20)"),
        "dia_especial_tipo" to (86 to "nueva: tipos de día especial que caen ese día (D.20)"),
        "n_muestreos" to (86 to "cuenta todos los avisos respondidos, también sin valencia (D.15)"),
        "pantalla_min" to (86 to "por día lógico, de 05:00 a 05:00 (ver ventana_pantalla) (D.19)"),
        "pasos" to (86 to "por día lógico, de 05:00 a 05:00 (ver ventana_pasos) (D.19)"),
        "por_hora_min" to (86 to "por día lógico: las horas 0 a 4 son de la madrugada siguiente (D.19)"),
        "primer_uso" to (86 to "primer uso del día lógico, desde las 05:00 (D.19)"),
        "primer_uso_h" to (86 to "primer uso del día lógico, desde las 05:00 (D.19)"),
        "tasa_respuesta" to (86 to "nueva: respondidos entre mostrados (D.15)"),
        "top_apps" to (86 to "por día lógico, de 05:00 a 05:00 (D.19)"),
        "total_min" to (86 to "por día lógico, de 05:00 a 05:00 (D.19)"),
        "ultimo_uso" to (86 to "último uso del día lógico, hasta las 05:00 siguientes (D.19)"),
        "ventana" to (86 to "nueva: logica o calendario, el día que cubre la pantalla (D.19)"),
        "ventana_pantalla" to (86 to "nueva: logica o calendario, el día que cubre la pantalla (D.19)"),
        "ventana_pasos" to (86 to "nueva: logica o calendario, el día que cubren los pasos (D.19)"),
        "vm_dias.csv:retroactivo" to (86 to "según el día lógico de la hora en que se guardó la valencia (D.18)"),
        "vm_dias.csv:ts_valencia" to (86 to "nueva: cuándo se guardó la valencia (D.18)"),
        "vm_experimentos.csv:fin" to (86 to "nueva: día en que terminó el experimento (D.17)"),
        "vm_experimentos.csv:hecho" to (86 to "nueva: respuestas a «¿Lo hiciste hoy?» (D.17)"),
        "vm_fechas_propias.csv:anio" to (86 to "nueva: tabla de fechas propias (D.20)"),
        "vm_fechas_propias.csv:fecha" to (86 to "nueva: tabla de fechas propias (D.20)"),
        "vm_fechas_propias.csv:mes_dia" to (86 to "nueva: tabla de fechas propias (D.20)"),
        "vm_fechas_propias.csv:nombre" to (86 to "nueva: tabla de fechas propias (D.20)"),
        "vm_fechas_propias.csv:tipo" to (86 to "nueva: tabla de fechas propias (D.20)"),
        "vm_panel_extendido.csv:app_min_*" to (86 to "por día lógico, de 05:00 a 05:00 (D.19)"),
        "vm_personas.csv:cercania" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:desde" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:detalle" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:edad" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:fallecimiento" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:historial" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:nacimiento" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:persona" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "vm_personas.csv:relacion" to (86 to "nueva: tabla de Tu gente (D.20)"),
        "siesta_reloj_min" to (87 to "respeta «no cuenta» y ya no se congela en noches corregidas (D.57)"),
        "subcategoria" to (87 to "nueva: subcategoría del gasto (D.27)"),
        "sueno_calidad" to (87 to "la del widget y la hoja rápida va al día del despertar (D.36)"),
        "sueno_descartadas" to (87 to "nueva: sesiones del reloj que marcaste que no cuentan (D.57)"),
        "ts_creado" to (87 to "nueva: cuándo se anotó el registro (D.25)"),
        "ts_editado" to (87 to "nueva: cuándo se corrigió el registro (D.25)"),
        "version_noche" to (87 to "nueva: orden de la hoja de la noche; vale 2 desde N4 (D.46)"),
        "animo_activacion" to (88 to "sin los «Cómo estás ahora» guardados otro día que el que cierran (D.74)"),
        "animo_cierre_activacion" to (88 to "sin los «Cómo estás ahora» guardados otro día que el que cierran (D.74)"),
        "animo_cierre_valencia" to (88 to "sin los «Cómo estás ahora» guardados otro día que el que cierran (D.74)"),
        "animo_valencia" to (88 to "sin los «Cómo estás ahora» guardados otro día que el que cierran (D.74)"),
        "cercania_media" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "cercanos" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "dia_libre" to (88 to "nueva: sábado, domingo o festivo (D.69)"),
        "esc_*" to (88 to "cada cuestionario cubre solo los días que pregunta (7 o 30) (D.62)"),
        "festivo" to (88 to "nueva: festivo de la Ley Federal del Trabajo, solo si vives en México (D.69)"),
        "gasto_imprevisto" to (88 to "0 desde el primer gasto, en días con diario; antes, vacío (D.63)"),
        "hab_*" to (110 to "vacío antes de que existiera el hábito, salvo marcado (D.63); desde el esquema 17, también vacío si ese día no lo contestaste (D.133)"),
        "lugares_distintos" to (88 to "por día lógico: una visita antes de las 05:00 cuenta para el anterior (D.64)"),
        "n_impulso" to (88 to "0 desde el primer impulso, en días con diario; antes, vacío (D.63)"),
        "pen_*" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "penso_en_amistades" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "penso_en_expareja" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "penso_en_familia" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "penso_en_pareja" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "per_*" to (88 to "con el nombre unido y sin momentos privados ni días sin nombrar (D.73)"),
        "personas_mensajes" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "personas_pensadas" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "personas_vistas" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "probioticos_estimado" to (88 to "vacío sin bitácora de MacroFactor (D.63)"),
        "registros_animo" to (88 to "sin los «Cómo estás ahora» guardados otro día que el que cierran (D.74)"),
        "rt_media" to (88 to "un toque a menos de 100 ms es anticipación y se repite (D.74)"),
        "sdt_necesidad_*" to (88 to "«las tres» cuenta en autonomía, competencia y relación (D.63)"),
        "tema_*" to (88 to "vacío los días sin diario ni momento con un #tema (D.63)"),
        "vi_amistades" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "vi_convivientes" to (88 to "con el nombre unido y vacío en días sin nombrar a nadie (D.73)"),
        "vi_expareja" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "vi_familia" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "vi_pareja" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "vi_trabajo" to (88 to "vacío los días sin diario ni momento que nombre a alguien (D.63)"),
        "vida_*" to (88 to "sin etapa antes del primer guardado de «Conocerte» (D.74)"),
        "vm_dias.csv:personas" to (88 to "nombres unidos siguiendo uniones encadenadas (D.73)"),
        "vm_dias.csv:presencias" to (88 to "nombres unidos siguiendo uniones encadenadas (D.73)"),
        "vm_dias.csv:temas" to (88 to "nombres unidos siguiendo uniones encadenadas (D.73)"),
        "vm_panel.csv:km" to (88 to "0 en días con ubicaciones y sin movimiento (D.63)"),
        "vm_panel_extendido.csv:distancia_km" to (88 to "0 en días con ubicaciones y sin movimiento (D.63)"),
        "vm_panel_extendido.csv:presencias_revisado" to (88 to "nueva en el panel: solo días con diario que nombra a alguien (D.63)"),
        "vm_panel_extendido.csv:sueno_recordado" to (88 to "vacío si ese día no dijiste nada del sueño (D.63)"),
        "vm_reaccion.csv:anticipaciones" to (88 to "incluye los toques a menos de 100 ms del estímulo (D.74)"),
        "vm_reaccion.csv:ensayos_ms" to (88 to "sin toques a menos de 100 ms: se repiten (D.74)"),
        "vm_reaccion.csv:media_ms" to (88 to "sin toques a menos de 100 ms: cuentan como anticipación (D.74)"),
        "vm_reaccion.csv:mediana_ms" to (88 to "sin toques a menos de 100 ms: cuentan como anticipación (D.74)"),
        "vm_red_nodos.csv:dias_mencion" to (88 to "cuenta con el nombre unido (D.73)"),
        "vm_red_nodos.csv:grado" to (88 to "cuenta con el nombre unido (D.73)"),
        "vm_red_nodos.csv:grupo" to (88 to "nueva: grupo de la relación (D.73)"),
        "vm_red_nodos.csv:persona" to (88 to "nombres unidos siguiendo uniones encadenadas (D.73)"),
        "vm_red_nodos.csv:relacion" to (88 to "relación del nombre unido (D.73)"),
        "vm_red_vinculos.csv:a" to (88 to "nombres unidos siguiendo uniones encadenadas (D.73)"),
        "vm_red_vinculos.csv:b" to (88 to "nombres unidos siguiendo uniones encadenadas (D.73)"),
        "vm_red_vinculos.csv:dias_juntos" to (88 to "cuenta con el nombre unido (D.73)"),
        "cambio_presion_3h" to (89 to "el barómetro acepta de 300 a 1100 hPa; antes, 850 a 1090 (D.85)"),
        "ejercicio_min" to (89 to "valor de la app: 0 en días con datos del reloj y sin sesión (D.80)"),
        "escalada" to (89 to "valor de la app: 0 desde la primera sesión, en días con diario (D.80)"),
        "gasto_total" to (89 to "valor de la app: 0 desde el primer gasto, en días con diario (D.80)"),
        "gimnasio" to (89 to "valor de la app: 0 desde el primer entreno, en días con diario (D.80)"),
        "ingreso_total" to (89 to "valor de la app: 0 desde el primer ingreso, en días con diario (D.80)"),
        "laptop_ocio_min" to (89 to "los sitios se comparan por partes completas del dominio (D.85)"),
        "laptop_otro_min" to (89 to "los sitios se comparan por partes completas del dominio (D.85)"),
        "laptop_productivo_min" to (89 to "los sitios se comparan por partes completas del dominio (D.85)"),
        "laptop_social_min" to (89 to "los sitios se comparan por partes completas del dominio (D.85)"),
        "presion_local_hpa" to (89 to "el barómetro acepta de 300 a 1100 hPa; antes, 850 a 1090 (D.85)"),
        "presion_max" to (89 to "el barómetro acepta de 300 a 1100 hPa; antes, 850 a 1090 (D.85)"),
        "presion_media" to (89 to "el barómetro acepta de 300 a 1100 hPa; antes, 850 a 1090 (D.85)"),
        "presion_min" to (89 to "el barómetro acepta de 300 a 1100 hPa; antes, 850 a 1090 (D.85)"),
        "ultimo_uso_h" to (89 to "suma 24 solo antes de las 5:00; antes, antes de las 12 (D.80)"),
        "vm_dias.csv:schema_v" to (89 to "0 en lo importado de la web u Obsidian sin versión propia (D.85)"),
        "vm_familias.csv:familia" to (89 to "nueva: tabla de parejas obvias (D.83)"),
        "vm_familias.csv:lado" to (89 to "nueva: tabla de parejas obvias (D.83)"),
        "vm_familias.csv:variable" to (89 to "nueva: tabla de parejas obvias (D.83)"),
        "vm_laptop.csv:categoria" to (89 to "los sitios se comparan por partes completas del dominio (D.85)"),
        "vm_panel.csv:a_pie_min" to (89 to "valor de la app: 0 si hay actividad sin ese movimiento (D.80)"),
        "vm_panel.csv:ruido_db_medio" to (89 to "valor de la app: media de todos los registros, también los avisos (D.80)"),
        "vm_panel.csv:vehiculo_min" to (89 to "valor de la app: 0 si hay actividad sin ese movimiento (D.80)"),
        "vm_variables.csv:grupo" to (89 to "nueva: tabla de variables que la app cruza (D.82)"),
        "vm_variables.csv:nombre" to (89 to "nueva: tabla de variables que la app cruza (D.82)"),
        "vm_variables.csv:privada" to (89 to "nueva: tabla de variables que la app cruza (D.82)"),
        "vm_variables.csv:variable" to (89 to "nueva: tabla de variables que la app cruza (D.82)"),
        "vm_panel_extendido.csv:act_*" to (90 to "nueva con el nombre de la app (D.87)"),
        "vm_panel_extendido.csv:app_*" to (90 to "nueva con el nombre de la app (D.87)"),
        "vm_panel_extendido.csv:nut_*" to (90 to "nueva con el nombre de la app (D.87)"),
        "alcohol" to (91 to "vacío sin dosis calculadas ni episodio anotado; antes, 0 (D.90)"),
        "alcohol_al_dormir" to (91 to "MacroFactor y la app se juntan toma por toma (D.91)"),
        "alcohol_bebidas" to (91 to "MacroFactor y la app se juntan toma por toma (D.91)"),
        "alcohol_estimado_max" to (91 to "de la primera dosis a 14 h, con dosis previas, cada 5 min (D.88)"),
        "cafeina_al_dormir_mg" to (91 to "MacroFactor y la app se juntan toma por toma (D.91)"),
        "cafeina_total_mg" to (91 to "MacroFactor y la app se juntan toma por toma (D.91)"),
        "creatina_saturacion_pct" to (91 to "la reserva se llena con 3 g al día, no con tu dosis (D.98)"),
        "error_prediccion_signo" to (91 to "nueva: valencia menos la predicción de la noche anterior (D.94)"),
        "gasto_imprevisto_monto" to (91 to "nueva: suma de los gastos imprevistos del día (D.94)"),
        "supl_*_nivel" to (91 to "creatina con 3 g de referencia; sin columna si la vida media < 6 h (D.98)"),
        "vm_horas.csv:alcohol_gl" to (91 to "MacroFactor y la app se juntan toma por toma (D.91)"),
        "vm_horas.csv:cafeina_mg" to (91 to "MacroFactor y la app se juntan toma por toma (D.91)"),
        "vm_panel.csv:cafeina_ultima_h" to (91 to "sale de las mismas dosis que los totales, por día lógico (D.92)"),
        "vm_panel_extendido.csv:personas" to (91 to "cuenta también a quien nombras en los momentos no privados (D.93)"),
        "vm_sustancias.csv:alcohol_estimado_max_gl" to (91 to "MacroFactor y la app se juntan toma por toma (D.91)"),
        "vm_whatsapp.csv:multimedia" to (91 to "reconoce también avisos en francés, portugués, italiano y alemán (D.105)"),
        "fuerza_ref_kg" to (92 to "nueva: 1RM estimada en press inclinado con mancuernas (D.114)"),
        "sin_wifi_min" to (92 to "ya no incluye los minutos sin dato (D.112)"),
        "vm_entorno.csv:fuente" to (92 to "nueva: archivo (reanálisis) o pronostico (D.111)"),
        "vm_experimentos.csv:huella" to (92 to "nueva: SHA-256 de lo sellado (D.113)"),
        "vm_experimentos.csv:intacto" to (92 to "nueva: la huella coincide con lo que hay ahora (D.113)"),
        "vm_experimentos.csv:sentido" to (92 to "nueva: sentido del cambio esperado, sellado (D.113)"),
        "vm_experimentos.csv:umbral" to (92 to "nueva: diferencia mínima que contaría, sellada (D.113)"),
        "vm_panel.csv:cambio_horario" to (92 to "nueva: la hora del teléfono cambió ese día lógico (D.110)"),
        "vm_panel.csv:clima_reanalisis" to (92 to "nueva: el clima viene del reanálisis (1) o del pronóstico (0) (D.111)"),
        "vm_panel.csv:desfase_utc_h" to (92 to "nueva: horas de diferencia con UTC a mediodía (D.110)"),
        "vm_panel_extendido.csv:fuente" to (92 to "nueva: de vm_entorno, archivo (reanálisis) o pronostico (D.111)"),
        "vm_series.csv:referencia" to (92 to "nueva: la serie es del ejercicio de referencia (D.114)"),
        "wifi_sin_dato_min" to (92 to "nueva: minutos en que no se supo si había wifi (D.112)"),
        "wifi_total_min" to (92 to "cuenta también el wifi con el nombre oculto (D.112)"),
        // Esquema 17: pantallas de captura.
        "vm_momentos.csv:pensamientos" to (110 to "nueva: varios pensamientos por registro (D.131)"),
        "vm_momentos.csv:intensidades" to (110 to "nueva: cuánto de cada emoción (D.130)"),
        "vm_momentos.csv:con_quien" to (110 to "más opciones: hijos, mascota, por mensaje o llamada (D.132)"),
        "con_*" to (110 to "más opciones: hijos, mascota, mensaje (D.132)"),
        "vm_ideas.csv:temas" to (110 to "suma los temas del campo «Temas» a los #temas del texto (D.134)"),
        "vm_dias.csv:sueno_rareza" to (110 to "nueva: qué tan raro fue el sueño; los días de antes, desde su tipo (D.127)"),
        "vm_dias.csv:sueno_pesadilla" to (110 to "nueva: si fue pesadilla; los días de antes, desde su tipo (D.127)"),
        "vm_dias.csv:sueno_tipo" to (110 to "ya no se pregunta: queda solo en los días de antes (D.127)"),
        "vm_dias.csv:sueno_categorias" to (110 to "nueve ejes; el tipo «recurrente» de antes aparece como est_recurrente (D.127, D.128)"),
        "suenot_*" to (110 to "rareza del sueño y pesadilla en lugar del tipo (D.127)"),
        "sueno_est_recurrente" to (110 to "incluye los días de antes con tipo «recurrente» (D.127)"),
        // Esquema 18: deseados y listas libres.
        "vm_visitas.csv:cierre" to (VERSION_ESQUEMA_19 to "nueva: cómo se supo la salida (D.147)"),
        "mostrado_ts" to (VERSION_ESQUEMA_20 to "nueva: hora real en que salió el aviso (D.153)"),
        "latencia_min" to (VERSION_ESQUEMA_20 to "desde que el aviso salió de verdad; antes, desde la hora planeada (ahora latencia_plan_min) (D.153)"),
        "latencia_plan_min" to (VERSION_ESQUEMA_20 to "nueva: la latencia de antes, desde la hora planeada (D.153)"),
        "en_casa_min" to (VERSION_ESQUEMA_19 to "con las visitas corregidas: sin las que quedaban abiertas estando en otro lado (D.147)"),
        "horas_tipo_*" to (VERSION_ESQUEMA_19 to "con las visitas corregidas: sin las que quedaban abiertas estando en otro lado (D.147)"),
        "lug_*" to (VERSION_ESQUEMA_19 to "con las visitas corregidas: sin las que quedaban abiertas estando en otro lado (D.147)"),
        "vm_visitas.csv:salida" to (VERSION_ESQUEMA_19 to "si Android no avisó, se deduce de la llegada a otro lugar o de tus ubicaciones; también hacia atrás (D.147)"),
        "vm_visitas.csv:minutos" to (VERSION_ESQUEMA_19 to "visitas de menos de 8 min ya no se guardan; antes 10 en las reconstruidas y ninguno en las de geocerca (D.147)"),
        "vm_biblioteca.csv:prioridad" to (VERSION_ESQUEMA_18 to "nueva: qué tan pronto quieres lo pendiente (D.145)"),
        "vm_biblioteca.csv:recomendo" to (VERSION_ESQUEMA_18 to "nueva: quién te lo recomendó (D.145)"),
        "vm_biblioteca.csv:donde" to (VERSION_ESQUEMA_18 to "nueva: dónde verlo o conseguirlo (D.145)"),
        // Esquema 21.
        "vm_dias.csv:hitos" to (VERSION_ESQUEMA_21 to "las frases de las fechas señaladas, con los hitos de antes (D.161)"),
        "hitos_n" to (VERSION_ESQUEMA_21 to "cuenta las fechas señaladas (D.161)"),
        "hito_*" to (VERSION_ESQUEMA_21 to "ya no se calcula: los hitos son fechas señaladas (D.161)"),
        "vm_senaladas.csv:tipo" to (VERSION_ESQUEMA_21 to "nueva: tabla de fechas señaladas (D.161)"),
        "vm_senaladas.csv:texto" to (VERSION_ESQUEMA_21 to "nueva: tabla de fechas señaladas (D.161)"),
        "n_episodio" to (VERSION_ESQUEMA_21 to "nueva: episodios del día (D.162)"),
        "episodio_intensidad_max" to (VERSION_ESQUEMA_21 to "nueva (D.162)"), "epi_*" to (VERSION_ESQUEMA_21 to "nueva: tipos de episodio (D.162)"),
        "vm_momentos.csv:episodio" to (VERSION_ESQUEMA_21 to "nueva: tipo de episodio (D.162)"),
        "vm_momentos.csv:personas" to (VERSION_ESQUEMA_21 to "suma el «con quién» de la intimidad y de una pelea (D.162, D.163)"),
        "vm_panel.csv:personas" to (VERSION_ESQUEMA_21 to "suma el «con quién» de un episodio de pelea (D.162)"),
        "n_masturbacion" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"), "masturbacion_min" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"),
        "masturbacion_satisfaccion" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"),
        "n_intimidad" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"), "intimidad_nivel" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"),
        "intimidad_satisfaccion" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"), "intimidad_comodidad" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"),
        "n_coito" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"), "intimidad_distancia" to (VERSION_ESQUEMA_21 to "nueva, seguimiento privado (D.163)"),
        "vm_dias.csv:recaida" to (VERSION_ESQUEMA_21 to "también la marca una masturbación con pornografía (D.163)"),
        "n_recaida" to (VERSION_ESQUEMA_21 to "cuenta también las recaídas gemelas de una masturbación con pornografía (D.163)"),
        "supl_*_n" to (VERSION_ESQUEMA_21 to "nueva: tomas del día, ahora pueden ser varias (D.164)"),
        "plan_min" to (VERSION_ESQUEMA_21 to "nueva: minutos planeados del bloque (D.165)"), "extra_min" to (VERSION_ESQUEMA_21 to "nueva: minutos de más (D.165)"),
        "vm_tramos.csv:min" to (VERSION_ESQUEMA_21 to "lo que duró de verdad: el bloque ya no se corta en la meta, sigue contando hasta que pasas al descanso o terminas (D.165)"),
        "enfoque_min" to (VERSION_ESQUEMA_21 to "con los minutos de más sobre la meta (D.165)"),
        // Esquema 22.
        "vm_dias.csv:foto_archivo" to (VERSION_ESQUEMA_22 to "nueva: la copia de la foto del día dentro del respaldo (D.168)"),
    )

    // Las claves con «*», como patrones; la más larga primero, para que «app_min_*» gane a «app_*».
    private val DESDE_PATRONES: List<Pair<Regex, String>> = DESDE.keys.filter { '*' in it }.sortedByDescending { it.length }
        .map { k -> Regex(k.split('*').joinToString(".*") { Regex.escape(it) }) to k }

    /** Desde qué versión rige la definición de la columna y qué cambió (H-1-8); null = sin cambios desde la v84. */
    fun desde(archivo: String, col: String): Pair<Int, String>? {
        DESDE["$archivo:$col"]?.let { return it }
        // En el panel extendido, lo que viene del panel de la app o de vm_panel.csv con el mismo nombre.
        if (archivo == "vm_panel_extendido.csv") DESDE["vm_panel.csv:$col"]?.let { return it }
        DESDE[col]?.let { return it }
        return DESDE_PATRONES.firstOrNull { (r, k) -> r.matches(if (':' in k) "$archivo:$col" else col) }?.let { DESDE[it.second] }
    }

    fun csv(tablas: List<Exportar.Tabla>): String = Exportar.csv(filas(tablas))

    /** Una fila por columna de cada tabla: archivo, columna, tipo, descripción, de qué tabla es y desde qué versión rige (H-1-8). */
    fun filas(tablas: List<Exportar.Tabla>): List<Map<String, Any?>> {
        val filas = ArrayList<Map<String, Any?>>()
        for (t in tablas) {
            val cols = LinkedHashSet<String>(); t.filas.forEach { cols.addAll(it.keys) }
            for (c in cols) filas.add(linkedMapOf("archivo" to t.archivo, "columna" to c, "tipo" to tipo(t.filas.map { it[c] }, c),
                "descripcion" to conParametros(c, conEquivalente(t.archivo, c, conVentana(t.archivo, c, describir(t.archivo, c)))), "tabla" to t.descripcion,
                "desde_version" to desde(t.archivo, c)?.first, "cambio" to desde(t.archivo, c)?.second))
        }
        listOf("ts" to "fecha_hora", "lat" to "numero", "lon" to "numero", "precision_m" to "numero", "fuente" to "texto", "ms" to "entero").forEach { (c, ti) ->
            filas.add(linkedMapOf("archivo" to "vm_ubicaciones.csv", "columna" to c, "tipo" to ti,
                "descripcion" to when (c) { "fuente" -> "De dónde salió la muestra (fondo, momento, cronología, foto…)"; "ms" -> "Milisegundos desde 1970 (la misma hora exacta, para restaurar)"; else -> describir("vm_ubicaciones.csv", c) },
                "tabla" to "Muestras de ubicación", "desde_version" to null, "cambio" to null))
        }
        listOf("clave" to ("texto" to "Identificador de la foto (no es la foto)"), "ms" to ("entero" to "Hora de la foto en milisegundos"), "ts" to ("fecha_hora" to "Hora de la foto"),
            "lat" to ("numero" to "Latitud"), "lon" to ("numero" to "Longitud"), "aparato" to ("texto" to "Con qué cámara o teléfono se tomó"),
            "origen" to ("texto" to "Carpeta o fuente (cámara, WhatsApp, Takeout…)"), "ms_archivo" to ("entero" to "Fecha del archivo, para detectar relojes equivocados"),
            "fuente" to ("texto" to "foto (del teléfono) o foto_takeout (de Google Fotos)")).forEach { (c, td) ->
            filas.add(linkedMapOf("archivo" to "vm_fotos_catalogo.csv", "columna" to c, "tipo" to td.first, "descripcion" to td.second, "tabla" to "Catálogo de fotos con lugar (solo hora y coordenadas)", "desde_version" to null, "cambio" to null))
        }
        return filas
    }
}
