# Apéndice A. Variables y tablas del sistema

_Se generó a partir del código de la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper: `ciencia/Estadistica.kt` (`variables()`), `ciencia/Dinamicas.kt`, `data/Exportar.kt` (`tablas()`) y `data/Diccionario.kt`. Lo que cambió en el módulo 10 se marca «desde 10a…10d», lo de la entrega 10e, «desde 10e» (sección A.8), lo de la 10f, «desde 10f» (sección A.9), y todo está en el Apéndice D. Lo que dice «Si hay datos…» describe cuándo la variable existe en una instalación concreta: el catálogo se arma con lo que el usuario ha registrado._

**Cómo leer las columnas.** *Tipo* y *Unidad* se leen del nombre que ve el usuario en la app (la app no guarda un tipo formal por variable). *Fuente* es la de todo el grupo; la procedencia exacta de cada variable está en el panel (`Estadistica.panel`). *Un valor vacío significa «sin dato»; un 0 significa «se observó y fue cero»* salvo en las reglas de relleno con ceros del capítulo 2, sección 2.6. Las definiciones que cambiaron en la entrega 10c (cuándo vale 0 y cuándo queda vacía cada variable) están en la sección A.6.

## A.1 Variables fijas del panel analítico

### Ánimo

*Fuente:* Diario (Dia) y momentos/avisos (Momento)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `valencia` | Valencia del día | numérica | — | Si hay datos de la fuente |
| `activacion` | Activación del día | numérica | — | Si hay datos de la fuente |
| `muestreo_valencia` | Valencia promedio en avisos | numérica | — | Si hay datos de la fuente |
| `muestreo_activacion` | Activación promedio en avisos | numérica | — | Si hay datos de la fuente |
| `dia_dificil` | Día difícil (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `prediccion` | Predicción para mañana (1 a 7) | numérica | — | Si hay datos de la fuente |
| `error_prediccion` | Cuánto falló la predicción de anoche | numérica | — | Si hay datos de la fuente |
| `error_prediccion_signo` | El día contra la predicción de anoche (+ mejor, − peor): valencia de hoy − predicción de anoche, de −6 a +6 | numérica | — | Si hay datos de la fuente: los mismos días que `error_prediccion` (con valencia hoy y predicción la noche anterior); si falta una, vacía. Desde 10e (D.94) |
| `animo_valencia` | Valencia promedio al momento (todos los registros) | numérica | — | Si hay registros de ánimo |
| `animo_activacion` | Activación promedio al momento (todos los registros) | numérica | — | Si hay registros de ánimo |
| `animo_cierre_valencia` | Valencia al cerrar el día | numérica | — | Si hay registros de cierre del día |
| `animo_cierre_activacion` | Activación al cerrar el día | numérica | — | Si hay registros de cierre del día |
| `valencia_acompanado` | Valencia en avisos con alguien | numérica | — | Si hay avisos con «con quién» |
| `valencia_solo` | Valencia en avisos estando solo | numérica | — | Si hay avisos con «con quién» |
| `avisos_planeados` | Avisos de ánimo planeados | numérica | avisos | Desde el esquema 13, los días con avisos encendidos |
| `avisos_mostrados` | Avisos de ánimo mostrados (menos de 50 min tarde y con permiso de notificaciones) | numérica | avisos | Desde el esquema 13 |
| `avisos_respondidos` | Avisos mostrados con respuesta (con o sin valencia) | numérica | avisos | Desde el esquema 13 |
| `tasa_respuesta` | Proporción de avisos respondidos (respondidos ÷ mostrados) | numérica | 0 a 1 | Desde el esquema 13, si se mostró alguno |

### Cuerpo

*Fuente:* Reloj y báscula (Health Connect), MacroFactor, sesiones, escalada

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `dolor` | Dolor físico | numérica | — | Si hay datos de la fuente |
| `hrv` | Variabilidad cardiaca (HRV) | numérica | — | Si hay datos de la fuente |
| `fc_reposo` | Frecuencia cardiaca en reposo | numérica | — | Si hay datos de la fuente |
| `fc_media` | Frecuencia cardiaca media del día | numérica | — | Si hay datos de la fuente |
| `fc_sueno` | Frecuencia cardiaca dormido | numérica | — | Si hay datos de la fuente |
| `spo2` | Saturación de oxígeno media (%) | numérica | % | Si hay datos de la fuente |
| `spo2_min` | Oxígeno mínimo de noche (%) | numérica | % | Si hay datos de la fuente |
| `pasos` | Pasos | numérica | — | Si hay datos de la fuente |
| `ejercicio_min` | Minutos de ejercicio (reloj) | numérica | — | Si hay datos de la fuente |
| `escalada` | Escalé (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `escalada_grado` | Grado máximo encadenado | numérica | — | Si hay datos de la fuente |
| `peso_kg` | Peso (kg) | numérica | kg | Si hay datos de la fuente |
| `temp_piel` | Variación de temperatura de la piel | numérica | — | Si hay datos de la fuente |
| `resp_rpm` | Frecuencia respiratoria | numérica | — | Si hay datos de la fuente |
| `cal_activas` | Calorías activas | numérica | — | Si hay datos de la fuente |
| `peso_tendencia_kg` | Peso de tendencia (MacroFactor) | numérica | — | Si hay datos de la fuente |
| `gimnasio` | Entrené en el gimnasio (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `volumen_kg` | Volumen del entrenamiento (kg) | numérica | kg | Si hay datos de la fuente |
| `series_gimnasio` | Series efectivas de gimnasio | numérica | — | Si hay datos de la fuente |
| `fuerza_ref_kg` | Fuerza de referencia: 1RM estimada en press inclinado con mancuernas (kg). La mejor del día con Epley, en series de 1 a 12 repeticiones sin calentamiento (`Macrofactor.fuerzaReferencia`, A.9) | numérica | kg | Si hay datos de la fuente: solo los días en que hiciste ese ejercicio; vacía los demás. Desde 10f (D.114) |

### Sueño

*Fuente:* Reloj (Health Connect), diario, teléfono (pantalla, carga)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `sueno_calidad` | Calidad de sueño percibida | numérica | — | Si hay datos de la fuente |
| `sueno_horas` | Horas de sueño (reloj) | numérica | — | Si hay datos de la fuente |
| `sueno_profundo` | Minutos de sueño profundo | numérica | — | Si hay datos de la fuente |
| `sueno_rem` | Minutos de sueño REM | numérica | — | Si hay datos de la fuente |
| `sueno_recordado` | Recordé un sueño (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `sueno_valencia` | Valencia del sueño | numérica | — | Si hay datos de la fuente |
| `carga_noche_h` | Hora de conectar el teléfono de noche | numérica | — | Si hay datos de la fuente |
| `sueno_inicio_h` | Hora de dormir (reloj) | numérica | — | Si hay datos de la fuente |
| `sueno_fin_h` | Hora de despertar (reloj) | numérica | — | Si hay datos de la fuente |
| `sueno_palabras` | Palabras del sueño escrito | numérica | — | Si hay datos de la fuente |
| `sueno_lucidez` | Lucidez del sueño (0 no, 1 parcial, 2 total) | numérica | — | Si hay datos de la fuente |
| `sueno_vividez` | Qué tan claro recuerdas el sueño (0 solo fragmentos, 1 partes claras, 2 casi completo y nítido) | numérica | — | Si hay datos de la fuente |
| `siesta` | Tomé siesta (0/1) | binaria (0/1) | — | Si hay siestas en el cronómetro |
| `siesta_min` | Minutos de siesta | numérica | — | Si hay siestas en el cronómetro |
| `cama_min` | Pantalla en la última hora antes de dormir (min) | numérica | min | Si hay datos de pantalla nocturna |
| `dormir_tras_tel_min` | Minutos entre soltar el teléfono y dormirte | numérica | — | Si hay datos de pantalla nocturna |
| `tel_tras_despertar_min` | Minutos entre despertar y tomar el teléfono | numérica | — | Si hay datos de uso al despertar |

### Teléfono

*Fuente:* Sensores del teléfono; importaciones de Google/YouTube

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `pantalla_min` | Minutos de pantalla | numérica | — | Si hay datos de la fuente |
| `desbloqueos` | Desbloqueos del teléfono | numérica | — | Si hay datos de la fuente |
| `fotos` | Fotos tomadas | numérica | — | Si hay datos de la fuente |
| `notificaciones` | Notificaciones recibidas | numérica | — | Si hay datos de la fuente |
| `canciones` | Canciones escuchadas (30 s o más, desde 10d) | numérica | — | Si hay datos de la fuente |
| `primer_uso_h` | Hora del primer uso del teléfono | numérica | — | Si hay datos de la fuente |
| `ultimo_uso_h` | Hora del último uso del teléfono | numérica | — | Si hay datos de la fuente |
| `audifonos_min` | Minutos con audífonos | numérica | — | Si hay datos de la fuente |
| `musica_min` | Minutos con audio sonando | numérica | — | Si hay datos de la fuente |
| `yt_vistos` | Videos de YouTube vistos | numérica | — | Si hay datos de la fuente |
| `yt_megusta` | «Me gusta» en YouTube | numérica | — | Si hay datos de la fuente |
| `yt_comentarios` | Comentarios en YouTube | numérica | — | Si hay datos de la fuente |
| `busquedas` | Búsquedas en Google | numérica | — | Si hay datos de la fuente |

### Dinero

*Fuente:* Gastos e ingresos registrados

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `gasto_total` | Gasto del día | numérica | — | Si hay datos de la fuente |
| `gasto_imprevisto` | Hubo gasto imprevisto (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `gasto_imprevisto_monto` | Gasto imprevisto (monto): suma de los gastos imprevistos del día | numérica | tu moneda | Si hay datos de la fuente: 0 desde el primer gasto en los días con diario (`ceroDesde`, como `gasto_total`) y en los días con gastos sin ninguno imprevisto; vacía antes del primer gasto. Desde 10e (D.94) |
| `ingreso_total` | Ingresos del día | numérica | — | Si hay datos de la fuente |
| `balance_dinero` | Ingresos menos gastos del día | numérica | — | Si hay datos de la fuente |

### Texto

*Fuente:* Diario, cartas

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `palabras` | Palabras en el diario | numérica | — | Si hay datos de la fuente |
| `hitos_n` | Número de hitos | numérica | — | Si hay datos de la fuente |
| `carta_escrita` | Escribí una carta (0/1) | binaria (0/1) | — | Si hay datos de la fuente |

### Social

*Fuente:* Diario (@personas), Tu gente, llamadas/SMS, WhatsApp (conteos)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `personas` | Personas distintas mencionadas: las nombradas con @ en el diario y, desde 10e, en los momentos no privados de ese día (D.93; antes, solo el diario) | numérica | — | Si hay datos de la fuente; vacía los días sin diario escrito ni momento con @ (10c, A.6) |
| `presencias_revisado` | Revisaste quién estuvo y en quién solo pensaste (0/1) | binaria (0/1) | — | Días con personas en el diario o ya revisados (desde 10c) |
| `llamadas` | Llamadas (entrantes y salientes) | numérica | — | Si hay datos de la fuente |
| `min_hablado` | Minutos al teléfono | numérica | — | Si hay datos de la fuente |
| `contactos` | Personas distintas por llamada o SMS | numérica | — | Si hay datos de la fuente |
| `wa_mios` | Mensajes de WhatsApp enviados | numérica | — | Si hay datos de la fuente |
| `wa_otros` | Mensajes de WhatsApp recibidos | numérica | — | Si hay datos de la fuente |
| `wa_chats` | Chats de WhatsApp activos | numérica | — | Si hay datos de la fuente |
| `cartas_otros` | Cartas escritas a otras personas | numérica | — | Si hay datos de la fuente |
| `cercanos` | Personas cercanas con las que estuve | numérica | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_familia` | Estuve con mi familia (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_pareja` | Estuve con mi pareja (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_amistades` | Estuve con una amistad (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_convivientes` | Estuve con quien vive conmigo (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_expareja` | Estuve con una expareja (0/1) | binaria (0/1) | — | Si alguna persona es o fue expareja |
| `vi_trabajo` | Estuve con un colega, compañero o mentor (0/1) | binaria (0/1) | — | Si hay personas de trabajo, compañeros o mentores |
| `personas_vistas` | Personas con las que estuve | numérica | — | Si hay presencias en el diario o momentos «pensando en» con personas |
| `personas_pensadas` | Personas en las que pensé sin verlas | numérica | — | Si hay presencias en el diario o momentos «pensando en» con personas |
| `personas_mensajes` | Personas con las que hablé por mensaje o llamada | numérica | — | Si hay presencias en el diario o momentos «pensando en» con personas |
| `penso_en_pareja` | Pensé en mi pareja sin verla (0/1) | binaria (0/1) | — | Si hay presencias y una pareja registrada |
| `penso_en_expareja` | Pensé en una expareja sin verla (0/1) | binaria (0/1) | — | Si hay presencias y una expareja |
| `penso_en_familia` | Pensé en mi familia sin verla (0/1) | binaria (0/1) | — | Si hay presencias |
| `penso_en_amistades` | Pensé en una amistad sin verla (0/1) | binaria (0/1) | — | Si hay presencias |
| `cercania_media` | Cercanía media de quien mencioné (1 a 7) | numérica | — | Si alguna persona tiene cercanía |
| `acompanado_pct` | Avisos de ánimo con alguien (0 a 1) | numérica | — | Si hay avisos con «con quién» |

### Mente

*Fuente:* Prueba de reacción

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `rt_media` | Tiempo de reacción (ms) | numérica | ms | Si hay datos de la fuente |

### Movilidad

*Fuente:* Ubicaciones, visitas a lugares, actividad del teléfono

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `lugares_distintos` | Lugares distintos visitados | numérica | — | Si hay datos de la fuente |
| `distancia_km` | Kilómetros recorridos (aprox.) | numérica | — | Si hay datos de la fuente |
| `a_pie_min` | Minutos a pie | numérica | — | Si hay datos de la fuente |
| `vehiculo_min` | Minutos en vehículo | numérica | — | Si hay datos de la fuente |
| `coche_min` | Minutos en el coche (Bluetooth) | numérica | — | Si hay datos de la fuente |
| `sin_wifi_min` | Minutos sin wifi (fuera). Desde 10f, solo cuando el wifi estaba apagado o el sistema dijo que no había red wifi; los minutos sin dato van a `wifi_sin_dato_min` de `vm_conexiones.csv` (D.112, A.9) | numérica | min | Si hay datos de la fuente |
| `en_casa_min` | Minutos en casa | numérica | — | Si hay un lugar de tipo casa |

### Entorno

*Fuente:* Open-Meteo, luna calculada, luz y barómetro del teléfono

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `t_max` | Temperatura máxima (°C) | numérica | °C | Si hay datos de la fuente |
| `t_min` | Temperatura mínima (°C) | numérica | °C | Si hay datos de la fuente |
| `precip_mm` | Lluvia (mm) | numérica | mm | Si hay datos de la fuente |
| `nubes_pct` | Nubosidad (%) | numérica | % | Si hay datos de la fuente |
| `luz_horas` | Horas de luz del día | numérica | — | Si hay datos de la fuente |
| `sol_horas` | Horas de sol directo | numérica | — | Si hay datos de la fuente |
| `presion_hpa` | Presión atmosférica (hPa) | numérica | hPa | Si hay datos de la fuente |
| `aqi_us` | Índice de calidad del aire | numérica | — | Si hay datos de la fuente |
| `pm25` | Partículas PM2.5 | numérica | — | Si hay datos de la fuente |
| `luna_ilum` | Luna iluminada (0 a 1) | numérica | — | Si hay datos de la fuente |
| `luz_alta_min` | Minutos con luz de exterior | numérica | — | Si hay datos de la fuente |
| `ruido_db` | Ruido medio al registrar (dB) | numérica | dB | Si hay datos de la fuente |
| `aire_libre_min` | Minutos al aire libre (estimado) | numérica | — | Si hay datos de la fuente |
| `presion_local_hpa` | Presión del barómetro del teléfono (hPa) | numérica | hPa | Si el teléfono tiene barómetro con lecturas |
| `cambio_presion_3h` | Mayor cambio de presión en 3 horas (hPa) | numérica | hPa | Si el teléfono tiene barómetro con lecturas |

### Agenda

*Fuente:* Calendario del teléfono

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `eventos` | Eventos en la agenda | numérica | — | Si hay datos de la fuente |
| `min_ocupado` | Minutos ocupados en agenda | numérica | — | Si hay datos de la fuente |

### Lectura

*Fuente:* Kindle (subrayados)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `subrayados` | Subrayados de Kindle | numérica | — | Si hay datos de la fuente |

### Nutrición

*Fuente:* MacroFactor

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `kcal` | Calorías consumidas | numérica | — | Si hay datos de la fuente |
| `proteina_g` | Proteína (g) | numérica | g | Si hay datos de la fuente |
| `carbohidratos_g` | Carbohidratos (g) | numérica | g | Si hay datos de la fuente |
| `grasa_g` | Grasa (g) | numérica | g | Si hay datos de la fuente |
| `fibra_g` | Fibra (g) | numérica | g | Si hay datos de la fuente |
| `alcohol_g` | Alcohol (g) | numérica | g | Si hay datos de la fuente |
| `cafeina_mg` | Cafeína (mg) | numérica | mg | Si hay datos de la fuente |
| `azucares_g` | Azúcares (g) | numérica | g | Si hay datos de la fuente |
| `balance_kcal` | Calorías menos gasto estimado | numérica | — | Si hay datos de la fuente |
| `primera_comida_h` | Hora del primer alimento | numérica | — | Si hay datos de la fuente |
| `ultima_comida_h` | Hora del último alimento | numérica | — | Si hay datos de la fuente |
| `cafeina_ultima_h` | Hora de la última cafeína. Desde 10e (D.92), del día lógico, con las tomas de MacroFactor y de la app (antes de las 5:00 suma 24); antes, la de MacroFactor por día de calendario (A.8) | numérica | — | Si hay datos de la fuente |

### Cultura

*Fuente:* Biblioteca (obras), citas

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `obras_dia` | Obras a las que les dediqué tiempo | numérica | — | Si hay datos de la fuente |
| `lectura` | Leí un libro o artículo (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `vi_pelicula_serie` | Vi una película o serie (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `jugue` | Jugué un videojuego (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `obra_terminada` | Terminé un libro, película, serie o juego (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `calificacion_obra` | Calificación de lo que terminé (1 a 10) | numérica | — | Si hay datos de la fuente |
| `citas_dia` | Citas y frases guardadas | numérica | — | Si hay datos de la fuente |

### Mundo

*Fuente:* Ubicaciones (ciudades, países, Patrimonio Mundial)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `ciudades_dia` | Ciudades distintas en el día | numérica | — | Si hay datos de la fuente |
| `fuera_de_casa` | Fuera de mi ciudad (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `extranjero` | En otro país (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `ciudad_nueva` | Primera vez en una ciudad (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `unesco` | Pasé por un sitio del Patrimonio Mundial (0/1) | binaria (0/1) | — | Si hay datos de la fuente |

### Momentos

*Fuente:* Momentos con hora

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `n_madrugada` | Momentos de madrugada (antes de las 5) | numérica | — | Si hay datos de la fuente |
| `n_manana` | Momentos en la mañana (5 a 12) | numérica | — | Si hay datos de la fuente |
| `n_tarde` | Momentos en la tarde (12 a 19) | numérica | — | Si hay datos de la fuente |
| `n_noche` | Momentos en la noche (19 a 24) | numérica | — | Si hay datos de la fuente |
| `momentos_intensidad` | Intensidad media de los momentos no privados (1 a 5; desde el módulo 10a sin impulsos) | numérica | — | Si hay datos de la fuente |
| `momentos_valencia` | Valencia media de los momentos | numérica | — | Si hay datos de la fuente |
| `registros_animo` | Registros de ánimo en el día | numérica | — | Si hay registros de ánimo |

### Sustancias

*Fuente:* Consumos, MacroFactor, modelos de estimación

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `alcohol` | Tomé alcohol (0/1) | binaria (0/1) | — | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_bebidas` | Bebidas estándar | numérica | — | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_estimado_max` | Alcohol en sangre máximo estimado (g/L); desde 10e cuenta lo que seguía de la noche anterior (A.8) | numérica | g/L | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_al_dormir` | Alcohol en sangre estimado al dormirme (g/L) | numérica | g/L | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_nivel_max` | Qué tan pedo llegué (0 a 10) | numérica | — | Si hay consumos de alcohol |
| `alcohol_horas` | Horas con efecto del alcohol | numérica | — | Si hay consumos de alcohol |
| `cannabis` | Cannabis (0/1) | binaria (0/1) | — | Si hay consumos de cannabis |
| `cannabis_nivel_max` | Nivel máximo con cannabis (0 a 10) | numérica | — | Si hay consumos de cannabis |
| `cannabis_horas` | Horas con efecto del cannabis | numérica | — | Si hay consumos de cannabis |
| `tabaco_n` | Cigarros o sesiones de vape | numérica | — | Si hay consumos de tabaco |
| `otra_sustancia` | Otra sustancia (0/1) | binaria (0/1) | — | Si hay consumos de «otra» |
| `cruda` | Cruda o bajón (0 a 5, del día anterior) | numérica | — | Si algún consumo tiene cruda |
| `cafeina_total_mg` | Cafeína del día (MacroFactor o anotada, mg) | numérica | mg | Si hay cafeína registrada o en MacroFactor |
| `cafeina_al_dormir_mg` | Cafeína en el cuerpo al dormirme (mg, estimada) | numérica | mg | Si hay cafeína registrada o en MacroFactor |

### Privado

*Fuente:* Pantalla Privado. Desde el módulo 10a estas variables **no aparecen en ninguna lista ni análisis de la app**; el panel las calcula siempre (con o sin el interruptor) y van en la exportación para R (en `vm_variables.csv`, con `privada` = 1). La regla es `Privado.esVariable`: las de `Privado.VARIABLES`, las familias `med_` y `det_`, y el `hab_` y el `campana_hab_` del hábito ligado a las recaídas.

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `impulso` | Intensidad del impulso | numérica | — | Si se anotó |
| `recaida` | Recaída (0/1) | binaria (0/1) | — | Si se anotó |
| `n_recaida` | Recaídas registradas con hora | numérica | — | Desde la primera recaída con hora |
| `recaida_min` | Minutos de recaída | numérica | — | Si hay recaídas con duración |
| `libido_max` | Libido máxima del día (0 a 5) | numérica | — | Si hay registros de libido |
| `n_libido` | Registros de libido | numérica | — | Si hay registros de libido |
| `n_impulso` | Momentos de impulso | numérica | — | Días con diario desde el primer impulso (10c; antes, todos los días con diario). Hasta v84 estaba en el grupo «Momentos» y entraba en los análisis |
| `medicamentos_n` | Medicamentos tomados | numérica | — | Desde el primer día con medicamentos. En el código conserva el grupo «Salud» (así sale en `vm_variables.csv`), pero desde 10a es privada; hasta v84 entraba en los análisis |

### Enfoque

*Fuente:* Cronómetro de enfoque y siestas

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `enfoque_min` | Minutos de enfoque (cronómetro) | numérica | — | Si hay tramos del cronómetro |

### Suplementos

*Fuente:* Tomas de suplementos y modelos de carga

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `creatina_saturacion_pct` | Saturación de creatina estimada (%); desde 10e, con 3 g al día como dosis de referencia (A.8) | numérica | % | Si el suplemento correspondiente tiene datos |
| `probioticos_porciones` | Porciones de probióticos (anotadas y estimadas) | numérica | — | Si el suplemento correspondiente tiene datos |
| `probioticos_estimado` | Porciones de probióticos leídas en tu bitácora de comidas | numérica | — | Si el suplemento correspondiente tiene datos |

### Laptop

*Fuente:* Cold Turkey (importación)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `laptop_min` | Minutos en la laptop | numérica | — | Si se importó Cold Turkey |
| `laptop_web_min` | Minutos en sitios web (laptop) | numérica | — | Si se importó Cold Turkey con sitios web |

_Cuántas son. Regla de conteo: una variable fija es un `Variable("id", …)` con el identificador escrito tal cual en `Estadistica.variables()`. Desde 10f son **187, con 24 nombres de grupo** (186 en la v91 y 184 en la v90). Las tablas de arriba traen **186 en 21 grupos**, porque: `dia_especial` («Calendario») y `campanas_n` («Campañas») van en la tabla siguiente, junto a las de su grupo; `n_impulso` se agrega aquí aunque el código la arma con una plantilla (`n_${it.id}` sobre `Catalogo.MOMENTOS`, que solo da esa), y `medicamentos_n`, la única del grupo «Salud», se lista en Privado. En la v81 eran 177 en 22 grupos; el esquema 13 (10b) agregó `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos`, `tasa_respuesta`, `campanas_n` y `dia_especial`, 10c, `presencias_revisado`, 10e, `error_prediccion_signo` y `gasto_imprevisto_monto` (D.94), y 10f, `fuerza_ref_kg` (D.114). No todas existen en cada instalación: 130 se agregan siempre (entre ellas, las dos de 10e y 4 privadas) y las otras 57 (entre ellas, la de 10f), según la columna «Existe»._

### Variables con nombre generado

Estas se crean una por cada elemento que el usuario ha usado (o, en las de catálogo fijo, una por opción); el identificador lleva el id del elemento. Todas salen de `Estadistica.variables()`; las dos últimas filas tienen nombre fijo y van aquí con su grupo.

| Patrón | Descripción | Grupo |
|---|---|---|
| `n_<sentimiento>` | Registros con ‹sentimiento› (los usados, sin «otro») | Sentimientos |
| `n_<pensamiento>` | Registros pensando: ‹pensamiento› (los usados) | Momentos |
| `sueno_<categoria>` | Sueño: ‹categoría› (0/1); las categorías usadas, con valor solo en días con categorías | Sueños |
| `enfoque_<tipo>_min` | Enfoque: ‹tipo› (min); los tipos usados de estudio, lectura, idiomas, tesis, trabajo y creativo | Enfoque |
| `horas_tipo_<tipo>` | Horas en lugares de tipo ‹tipo›; los tipos de tus lugares, de 12 posibles | Lugares |
| `laptop_<categoria>_min` | Minutos en la laptop: ‹categoría› (productivo, ocio, social, otro); con Cold Turkey | Laptop |
| `con_<id>` | Avisos con ‹pareja, amigos, familia, compañeros o desconocidos› (0 a 1); si hay avisos con «con quién» | Social |
| `ctx_<id>` | Contexto: ‹nombre› (0/1); siempre, los 10 contextos: trabajo, gym, viaje, enfermo, académico, ocio inmersivo, naturaleza, amigos, romántico, familia | Contexto |
| `flow_<id>`, `sdt_necesidad_<id>`, `apreciacion_<id>` | Opción elegida en las tres preguntas científicas del día (0/1); siempre, 13 opciones (4, 5 y 4; «No aplica» no cuenta) | Lectura del día |
| `hab_<id>` | Hábito: ‹nombre› (0/1) | Hábitos |
| `supl_<id>_dosis/_tomado/_nivel/_porciones` | Dosis, toma, reserva o cantidad estimada y porciones del suplemento. Desde 10e, `_nivel` no existe en los suplementos de vida media menor de 6 horas (`Suplementos.VIDA_MEDIA_MIN_NIVEL`, A.8) | Suplementos |
| `campana_<reto>`, `campana_hab_<id>` | En campaña ese día (0/1; la tenías aceptada). 0 desde tu primera campaña, vacío antes. Desde el esquema 13 | Campañas |
| `campanas_n` | Campañas que tenías ese día. Desde el esquema 13 | Campañas |
| `dia_especial` | Ese día cae un cumpleaños, un aniversario o una fecha propia (0/1). Existe si hay alguna fecha guardada. Desde el esquema 13 | Calendario |

**Pantalla y pasos, desde el esquema 13.** `pantalla_min`, `desbloqueos`, `primer_uso_h`, `ultimo_uso_h` y `pasos` se calculan por **día lógico** (de 05:00 a 05:00) en los días nuevos; los de antes quedaron por día de calendario. La ventana de cada día está en `vm_pantalla.csv:ventana`, `vm_salud.csv:ventana_pasos` y, en el panel exportado, `ventana_pantalla` y `ventana_pasos` (capítulo 2, sección 2.3.2).

## A.2 Familias de variables dinámicas

Cada familia crea una variable por cada elemento (persona, tema, lugar, app…) que aparece en al menos *Mín. días* días (en el análisis); en la exportación extendida basta con 2 días y se permiten hasta 400 por familia. *Máx.* es el tope en el análisis. Fuera de los días con dato de la fuente, el valor es 0 en las fechas de la *cobertura* de cada familia.

| Prefijo | Grupo | Máx. | Mín. días | Plantilla del nombre |
|---|---|---|---|---|
| `per_` | Personas | 15 | 5 | Aparece @‹elemento› (diario, mejor y peor momento, momentos, sueños o cartas) (0/1); 0 solo en días con diario escrito o momento con @ (10c) |
| `pen_` | Personas | 10 | 3 | Pensé en @‹elemento› sin verla (0/1) |
| `tema_` | Temas | 12 | 5 | Aparece #‹elemento› (0/1); 0 solo en días con diario escrito o momento con #tema (10c) |
| `hito_` | Hitos | 10 | 3 | Hito: ‹elemento› (0/1) |
| `lug_` | Lugares | 15 | 3 | Horas en «‹elemento›» |
| `app_` | Apps | 12 | 5 | Minutos en ‹elemento› |
| `ntf_` | Notificaciones | 8 | 5 | Notificaciones de ‹elemento› |
| `lap_` | Laptop | 10 | 4 | Minutos en ‹elemento› (laptop) |
| `web_` | Laptop | 10 | 4 | Minutos en ‹elemento› (navegador de la laptop) |
| `ytc_` | YouTube | 8 | 5 | Videos de ‹elemento› en YouTube |
| `gcat_` | Dinero | 15 | 5 | Gasto en ‹elemento› |
| `icat_` | Dinero | 8 | 3 | Ingreso de ‹elemento› |
| `art_` | Música | 10 | 5 | Canciones de ‹elemento› |
| `obr_` | Cultura | 10 | 5 | Le dediqué tiempo a «‹elemento›» (0/1) |
| `gen_` | Cultura | 10 | 5 | Leí, vi o jugué algo de ‹elemento› (0/1) |
| `med_` | Privado | 15 | 3 | Tomé ‹elemento› (0/1) |
| `det_` | Privado | 10 | 3 | Detonante: ‹elemento› (0/1) |
| `suenot_` | Sueño | 6 | 3 | Sueño realista / raro pero posible / imposible o surreal, o pesadilla (0/1); desde el esquema 17 (antes, el tipo) |
| `ciu_` | Mundo | 8 | 3 | Horas en ‹elemento› |
| `pais_` | Mundo | 6 | 3 | Horas en ‹elemento› |
| `musc_` | Cuerpo | 12 | 3 | Series de ‹elemento› |
| `act_` | Cuerpo | 6 | 3 | Minutos ‹elemento› |
| `nut_` | Nutrición | 60 | 14 | nombre del nutriente de MacroFactor traducido (p. ej., «Vitamina C (mg)»); excluye los ya fijos, los objetivos y los pasos/peso de MacroFactor |
| `ali_` | Nutrición | 12 | 5 | Comí ‹elemento› (0/1) |
| `esc_` | Cuestionarios | todos | — | «‹cuestionario›: ‹puntaje›»; desde 10c el puntaje vale para su día y los 6 previos (WHO-5, PSS-4) o los 29 previos (UCLA-3), y un día cubierto por dos respuestas se queda con la primera; la personalidad, hasta el test siguiente (máx. 365 días, `porDiaCuestionario`). Hasta 10c: 14 o 30 días previos, ganando la última |
| `pol_` | Política | ejes | — | «Brújula: ‹eje› (−100 ‹polo›, +100 ‹polo›)»; la posición de cada test vale hasta el test siguiente (máx. 400 días) |
| `vida_` | Etapas de vida | 14 + ciudades | — | «Etapa: ‹campo de Conocerte›»; se reconstruye con el historial de cambios. Desde 10c, vacía antes del primer guardado de «Conocerte» para los campos llenados ese día (`Perfiles.primerGuardado`) |

## A.3 Tablas exportadas

La exportación genera 53 tablas CSV con datos (contadas en `Exportar.tablas()`: 51 antes del esquema 16, que agregó `vm_ideas.csv` y `vm_ideas_revisiones.csv`, A.10; 49 antes de la entrega 10d, que agregó `vm_variables.csv` y `vm_familias.csv`; el esquema 13 había agregado `vm_personas.csv` y `vm_fechas_propias.csv`), más `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv`, `vm_diccionario.csv` (columnas `archivo`, `columna`, `tipo`, `descripcion` y `tabla`; desde 10f, también `desde_version` y `cambio`, A.9), `vm_respaldo.json`, `vm_datos.sqlite` (una tabla por cada CSV con datos, sin el «vm_», más `ubicaciones` y `diccionario`; no lleva el catálogo de fotos), un `LEEME.txt`, el propio `vestigium.R` y la carpeta `portadas/` (`Exportar.archivos` y `Exportar.zip`). La exportación resumida (`Exportar.zipResumido`) trae solo `vm_panel.csv`, su diccionario, el `LEEME.txt` y `vestigium.R`. Todos los CSV usan UTF-8 con BOM, fechas AAAA-MM-DD, booleanos 0/1, listas separadas por «|» y vacío para el dato faltante.

| Archivo | Contenido |
|---|---|
| `vm_panel.csv` | Una fila por día con lo principal de todas las fuentes |
| `vm_panel_extendido.csv` | Una fila por día con todas las variables: cada persona, lugar, app, categoría, artista, obra, ciudad, nutriente y más; desde 10d, el panel que analiza R |
| `vm_variables.csv` | Las variables que la app cruza entre sí, con su nombre y su grupo (las privadas, marcadas) (10d) |
| `vm_familias.csv` | Parejas de variables que la app esconde por obvias (la misma medida, una parte y su total…), en familias (10d) |
| `vm_dias.csv` | Diario: lo que llenaste cada día |
| `vm_momentos.csv` | Momentos y avisos de ánimo, uno por fila |
| `vm_tramos.csv` | Cronómetro: bloques de enfoque (estudio, lectura, idiomas, tesis…) y siestas, uno por fila |
| `vm_sustancias.csv` | Sustancias: cada consumo (alcohol, cannabis, tabaco, cafeína, otra) con su nivel, duración y cruda |
| `vm_horas.csv` | Una fila por hora: dormido, lugar, pantalla, notificaciones, luz, música, ánimo, cafeína y alcohol en el cuerpo, sustancias, enfoque, gastos y comidas |
| `vm_gastos.csv` | Gastos, uno por fila |
| `vm_notas.csv` | Notas sueltas: ideas, pensamientos, propósitos y recordatorios |
| `vm_ideas.csv` | Ideario: lo que aprendiste o pensaste, con quién lo dice, su fuente y cómo la tienes (esquema 16) |
| `vm_ideas_revisiones.csv` | Ideario: cada vez que volviste a ver una idea y qué respondiste (esquema 16) |
| `vm_suplementos.csv` | Cada toma de suplemento (y las porciones de probióticos estimadas de tu bitácora de comidas) |
| `vm_laptop.csv` | Minutos por app y por sitio en la laptop, por día (Cold Turkey) |
| `vm_red_nodos.csv` | Personas de tu red: relación, grupo de relación (columna `grupo`, desde 10c), cercanía, días mencionada y grado |
| `vm_red_vinculos.csv` | Pares de personas: cómo se conocen según tú y cuántos días aparecen juntas |
| `vm_personas.csv` | Tu gente: relación, cercanía, desde cuándo, nacimiento, fallecimiento y edad (esquema 13) |
| `vm_fechas_propias.csv` | Fechas propias que se repiten cada año (esquema 13) |
| `vm_experimentos.csv` | Experimentos personales: pregunta, hipótesis sellada, diseño, lavado y plan por semana (1 = con la acción); desde el esquema 13, también `fin` y `hecho` («¿Lo hiciste hoy?»); desde el 15, el criterio sellado (`sentido`, `umbral`) y la huella (`huella`, `intacto`) |
| `vm_metas.csv` | Metas: horizonte, fecha objetivo, estado, medida, progreso e historial de cambios |
| `vm_ingresos.csv` | Ingresos, uno por fila |
| `vm_escalas.csv` | Cuestionarios validados (WHO-5, PSS-4, UCLA-3, TIPI) |
| `vm_reaccion.csv` | Pruebas de reacción |
| `vm_salud.csv` | Health Connect por día (reloj y báscula) |
| `vm_pantalla.csv` | Tiempo en pantalla por día |
| `vm_visitas.csv` | Llegadas y salidas de tus lugares |
| `vm_entorno.csv` | Clima, luz, aire y luna por día; desde el esquema 15, de dónde salió el clima (`fuente`) |
| `vm_fotos.csv` | Fotos tomadas por día |
| `vm_agenda.csv` | Agenda por día |
| `vm_notificaciones.csv` | Notificaciones por día (solo conteos) |
| `vm_musica.csv` | Canciones y podcasts, uno por fila |
| `vm_actividad.csv` | Minutos por tipo de movimiento |
| `vm_ejercicio.csv` | Sesiones de ejercicio del reloj |
| `vm_escalada_sesiones.csv` | Sesiones de escalada |
| `vm_escalada_bloques.csv` | Bloques o vías de cada sesión |
| `vm_biblioteca.csv` | Libros, películas, series, juegos y artículos |
| `vm_citas.csv` | Citas de tus obras y frases guardadas con ♥ |
| `vm_mundo.csv` | Ciudades añadidas a mano, países rascados y sitios del Patrimonio Mundial marcados |
| `vm_cartas.csv` | Cartas: a tu yo del futuro y a otras personas (las selladas, sin su texto) |
| `vm_perfil.csv` | Conocerte: cómo está ahora y cada cambio con su fecha |
| `vm_politica.csv` | Brújula política: un test por año, ejes y respuestas |
| `vm_comunicacion.csv` | Llamadas y SMS por día (solo conteos) |
| `vm_conexiones.csv` | Wifi, audio, carga, batería y luz por día; desde el esquema 15, los minutos sin dato de wifi aparte (`wifi_sin_dato_min`) |
| `vm_subrayados.csv` | Subrayados y notas de Kindle |
| `vm_google_dia.csv` | YouTube y búsquedas de Google por día |
| `vm_google.csv` | YouTube y búsquedas de Google, una por fila |
| `vm_youtube.csv` | YouTube (CSV de Takeout): suscripciones, «me gusta», videos en listas, comentarios y chats (sin texto) |
| `vm_whatsapp.csv` | WhatsApp: un chat por día por fila (solo conteos) |
| `vm_nutricion.csv` | MacroFactor: nutrición, gasto energético y peso por día |
| `vm_comidas.csv` | MacroFactor: cada alimento registrado |
| `vm_entrenos.csv` | MacroFactor Workouts: resumen por día y por grupo muscular; desde el esquema 15, `fuerza_ref_kg` |
| `vm_series.csv` | MacroFactor Workouts: cada serie; desde el esquema 15, si es del ejercicio de referencia (`referencia`) |

## A.4 Columnas nuevas del esquema 13 (entrega 10b)

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_panel.csv` | `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos`, `tasa_respuesta` | Los avisos de ánimo del día (capítulo 3, sección 3.5.3) |
| `vm_panel.csv` | `n_muestreos` | Ahora cuenta **todos** los avisos respondidos, con o sin valencia (antes, solo con valencia) |
| `vm_panel.csv` | `campana_<reto>`, `campanas_n` | Campañas como variable (capítulo 9, sección 9.5) |
| `vm_panel.csv` | `ventana_pantalla`, `ventana_pasos` | `logica` o `calendario` |
| `vm_panel.csv` | `dia_especial`, `dia_especial_tipo` | Día especial y de qué tipo: `mi_cumpleanos`, `cumpleanos`, `fallecimiento`, `aniversario` u `otra` (lista) |
| `vm_dias.csv` | `ts_valencia` | Cuándo se llenó o cambió la valencia; decide `retroactivo` |
| `vm_experimentos.csv` | `fin`, `hecho` | Fecha de fin; respuestas de «¿Lo hiciste hoy?» como `fecha:1` o `fecha:0` (lista; sin fecha = sin respuesta) |
| `vm_pantalla.csv` | `ventana` | `logica` (05:00 a 05:00) o `calendario` (0:00 a 24:00) |
| `vm_salud.csv` | `ventana_pasos` | Igual, para los pasos |
| `vm_personas.csv` | `persona`, `relacion`, `detalle`, `cercania`, `desde`, `nacimiento`, `fallecimiento`, `edad`, `historial` | Fechas en AAAA-MM-DD, o --MM-DD sin año. `edad` = años cumplidos hoy, o al morir |
| `vm_fechas_propias.csv` | `nombre`, `fecha`, `mes_dia`, `anio`, `tipo` | Fechas propias que se repiten cada año |
| `vm_respaldo.json` | `avisos` | Los avisos de ánimo de cada día, para restaurarlos |

## A.5 Columnas nuevas del esquema 14 (revisión de navegación, entrega NE)

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_momentos.csv`, `vm_gastos.csv`, `vm_ingresos.csv` | `ts_creado`, `ts_editado` | Cuándo se anotó y cuándo se corrigió por última vez. Vacíos en los registros de antes del esquema 14 |
| `vm_sustancias.csv`, `vm_tramos.csv`, `vm_suplementos.csv` | `ts_creado` | Cuándo se anotó. En `vm_suplementos.csv` va también `ts` (cuándo se tomó); las porciones estimadas de probióticos quedan vacías |
| `vm_gastos.csv` | `subcategoria` | Subcategoría dentro de la categoría (por ejemplo, Comida → Café). Vacía = sin subcategoría |
| `vm_dias.csv`, `vm_panel.csv` | `version_noche` | Con qué orden de preguntas se llenó la hoja de la noche (`VERSION_NOCHE`): 1 = el orden con que se creó el campo en la entrega NE; 2 = el orden nuevo (N-18, desde la entrega N4: el ánimo del momento primero; capítulo 3, sección 3.7.1). Vacío = no pasó por la hoja o es de antes del esquema 14 |
| `vm_salud.csv` | `sueno_descartadas` | Las sesiones del reloj de esa noche que marcaste «no cuenta» (fuente y horas, por ejemplo, «Samsung Health 15:10–16:05»); no entran ni en `sueno_min` ni en `siesta_reloj_min` |

**Cómo leer `ts_creado`.** La hora de un registro (`ts`, `inicio` o `fecha` + `hora`) dice **cuándo pasó**; `ts_creado`, **cuándo lo anotaste**. La diferencia dice si se anotó en el momento o después. En R, `vm_retraso(tabla, umbral_min = 60)` la calcula (`retraso_min`) y marca `tarde` si pasó más del umbral o cayó en otro día lógico (Apéndice C). El umbral es **[Diseño propio]**.

## A.6 Definiciones que cambiaron en la entrega 10c (cálculos)

No cambia lo que se guarda (`SCHEMA_V` = 14); cambia cómo se calcula el panel con lo ya guardado, **también en los días viejos**. Detalle y motivo en el Apéndice D (D.62 a D.78) y en el capítulo 2, sección 2.6.1. «Base» son los días en que la variable puede valer 0; fuera de ellos queda vacía.

| Variable | Definición desde 10c | Antes |
|---|---|---|
| `gasto_total`, `gasto_imprevisto` | 0 en los días con diario desde el **primer gasto** (`ceroDesde`) | 0 en todos los días con diario |
| `n_impulso` (privada) | 0 en los días con diario desde el **primer impulso** | 0 en todos los días con diario |
| `escalada` | 0 en los días con diario desde la **primera sesión de escalada** | 0 en todos los días con diario |
| `gimnasio` | 0 en los días con diario desde el **primer entrenamiento**, más los días con entrenamiento | 0 en cualquier fecha del panel desde el primer entrenamiento |
| `hab_<id>` | 1 si lo marcaste; 0 desde `Habito.desde`; vacío antes (los hábitos sin esa fecha, 0 siempre) | 0 en todos los días con diario, también antes de existir (igual en `vm_dias.csv`) |
| `sueno_recordado` | 1 si recordaste; 0 solo si ese día dijiste algo del sueño (calidad o texto); si no, vacío | 0 en cualquier día con diario sin sueño recordado |
| `sdt_necesidad_autonomia`, `_competencia`, `_relacion` | También 1 si elegiste «las tres» | 0 con «las tres» |
| `personas`, `vi_*`, `cercanos`, `cercania_media`, `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `penso_en_*` | Solo en los días con diario escrito o con un momento no privado que nombra a alguien con @ (`Dinamicas.diasSociales`); vacías en los demás | En cualquier día con documento de diario o algún momento |
| `per_`, `pen_` | 0 en los días de `diasSociales` y en los días donde aparece otra persona; los temas del sueño cuentan solo si llevan @ | 0 en todos los días con diario o momentos no privados; cualquier etiqueta del sueño sin # contaba como persona |
| `tema_` | 0 en los días con diario escrito o con un momento que lleva algún #tema | 0 en todos los días con diario o momentos no privados |
| `vi_convivientes` | Compara con los convivientes pasados por `canon` (equivalencias en cadena) | Comparaba el texto tal cual |
| `cercania_media` | La cercanía de cada día respeta «sin cercanía» en el historial | Un tramo sin cercanía usaba la cercanía actual |
| `presencias_revisado` (nueva en el panel) | 1 si revisaste las presencias ese día; 0 si hay personas en el diario sin revisar; vacío si no | Solo en `vm_dias.csv` |
| `ejercicio_min` | 0 los días con datos del reloj (pasos o ritmo cardiaco) y sin sesión, desde la primera sesión | Vacío esos días |
| `distancia_km` | GPS (`Movilidad.kmPorDia`): 0 los días con ubicaciones y sin movimiento. En `vm_salud.csv`, `distancia_km` es la de Health Connect (otra fuente; el diccionario las distingue) | Vacío esos días |
| `lugares_distintos` | Por **día lógico** de la llegada | Por fecha de calendario de la llegada |
| `probioticos_estimado` | Solo los días con bitácora de MacroFactor (o con algo leído en ella) | 0 todos los días desde la primera toma |
| `esc_*` | Ventana del periodo preguntado (7 o 30 días) y la primera respuesta que cubre el día | Ventana de 14 o 30 días, ganando la última respuesta |
| `animo_cierre_valencia`, `animo_cierre_activacion` (y `animo_valencia`, `animo_activacion`, `registros_animo`) | Sin los cierres guardados otro día que el que cierran (`Estadistica.cierreFuera`) | Con todos los cierres de esa fecha |
| `vida_*` | Vacías antes del primer guardado de «Conocerte» para los campos llenados ese día | El valor se proyectaba hacia atrás |
| `alcohol` | 1 el día lógico de cada dosis (y el de los episodios sin cantidad); 0 los demás desde el primer registro | 1 el día del episodio, y también el día de las dosis: dos reglas a la vez |
| `alcohol_estimado_max`, `alcohol_al_dormir` | Peso más reciente entre báscula y tendencia de MacroFactor (si no hay anterior, el primero posterior; 70 kg solo sin ningún peso) y, con peso real, sexo, estatura y (en hombres) edad, la *r* de Watson; si no, *r* fija por sexo | La báscula ganaba siempre a MacroFactor, 70 kg antes de la primera pesada, *r* fija por sexo |
| `cafeina_total_mg`, `alcohol_bebidas` | Incluyen las tomas de MacroFactor sin hora legible (al mediodía de su día) | Esas tomas se perdían |
| `n_momentos` (`vm_panel.csv`) | Sin cambio de cálculo; el diccionario aclara que incluye los momentos privados | Igual |

**Columnas nuevas en la exportación (10c).**

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_panel.csv` | `festivo` | Día de descanso obligatorio de la Ley Federal del Trabajo de México, art. 74 (0/1); vacío si en «Conocerte» no vives en México (`Fechas.esFestivoMx`) |
| `vm_panel.csv` | `dia_libre` | Sábado, domingo o festivo (0/1): la regla de día libre del jet lag social (`Fechas.esDiaLibre`) |
| `vm_red_nodos.csv` | `grupo` | Grupo de la relación: familia, pareja, amistad, trabajo u otro; el que da el color en la Red |

**Por día de calendario.** Las descripciones del diccionario de `vm_panel.csv` y `vm_panel_extendido.csv` terminan con «· por día de calendario (0:00 a 24:00)» en las variables de `Diccionario.CALENDARIO` y en las familias `ciu_` y `pais_` (capítulo 2, sección 2.3.2).

## A.7 La exportación en la entrega 10d (R y exportación)

Tampoco cambia lo que se guarda (`SCHEMA_V` = 14). Cambian los valores de algunas columnas de `vm_panel.csv` y hay dos tablas nuevas. Detalle en el capítulo 2 (sección 2.8) y en el Apéndice D.

**`vm_panel.csv` con los valores de la app (P-30).** En cada columna que comparte nombre con el panel de la app, el valor es el de la app (`Exportar.comoLaApp`), con el mismo tipo que tenía la columna (0/1, entero o número). Las que más cambian:

| Columna | Desde 10d (= la app) | Antes en `vm_panel.csv` |
|---|---|---|
| `gasto_total` | 0 en los días con diario desde el primer gasto; vacío antes | 0 en cualquier fecha exportada |
| `ingreso_total` | 0 en los días con diario desde el primer ingreso; vacío antes | 0 en cualquier fecha exportada |
| `escalada`, `gimnasio` | 0 en los días con diario desde la primera sesión o el primer entreno; vacío antes | Falso en cualquier fecha exportada |
| `ejercicio_min` | 0 los días con datos del reloj y sin sesión, desde la primera sesión | Vacío esos días |
| `canciones` | Escuchas de 30 s o más (o sin duración), en la app y en el CSV | El CSV ya filtraba 30 s; la app contaba todas |
| `ultimo_uso_h` | +24 solo antes de las 5:00 (1:00 = 25; 11:00 = 11) | +24 antes de las 12:00 (11:00 = 35) |
| `audifonos_min`, `coche_min` | 0 en días con datos de conexiones y sin ese audio | Vacío esos días |
| `luna_ilum` | La iluminación calculada de la fase lunar, en todos los días del panel | La de `vm_entorno.csv` si existía |
| `ruido_db_medio` | `ruido_db` de la app: la media de todos los registros del día con ruido | La media solo de los momentos que no son avisos |

**Once columnas con otro nombre en la app** (`Exportar.EQUIVALENTES`; no se renombran, y el diccionario lo dice en las dos):

| `vm_panel.csv` | Panel de la app | ¿Mismo valor? |
|---|---|---|
| `sueno_min` | `sueno_horas` | La misma fuente; en minutos frente a horas |
| `sueno_profundo_min` | `sueno_profundo` | Sí (toma el de la app) |
| `sueno_rem_min` | `sueno_rem` | Sí |
| `hrv_rmssd` | `hrv` | Sí |
| `km` | `distancia_km` | Sí |
| `valencia_muestreo` | `muestreo_valencia` | Sí |
| `luz_dia_min` | `luz_horas` | La misma fuente; en minutos frente a horas |
| `sol_min` | `sol_horas` | La misma fuente; en minutos frente a horas |
| `carga_noche_inicio_h` | `carga_noche_h` | Sí |
| `temp_piel_delta` | `temp_piel` | Sí |
| `ruido_db_medio` | `ruido_db` | Sí |

**Tablas nuevas.**

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_variables.csv` | `variable` | Nombre de la columna en `vm_panel_extendido.csv` |
| `vm_variables.csv` | `nombre`, `grupo` | Qué es y su grupo, como lo dice la app |
| `vm_variables.csv` | `privada` | Seguimiento privado (0/1): la app no lo cruza; en R, solo con `privadas = TRUE` |
| `vm_familias.csv` | `familia` | La regla de la app que la produce (por ejemplo, `familia_3`, `total:per_`, `persona:ana`, `escala:who5`, `igual:distancia_km`) |
| `vm_familias.csv` | `variable` | Columna de `vm_panel_extendido.csv`, o su nombre en `vm_panel.csv` |
| `vm_familias.csv` | `lado` | 0 = obvia con todas las de la familia; 1 o 2 = obvia solo con las del otro lado |

**Otras columnas que cambiaron (P-33).** `vm_dias.csv:schema_v` vale 0 en los días importados de Obsidian o de la versión web que no traían versión; `ts_creado` y `ts_editado` de los días de Obsidian quedan vacíos si la nota no los traía. `vm_ubicaciones.csv:precision_m` guarda la precisión de los puntos del formato viejo de Takeout (antes vacía). La descripción de `vm_red_nodos.csv:grado` aclara que usa 1 día juntas y hasta 400 personas, frente a 2 días y 45 personas en la app.

## A.8 Definiciones que cambiaron en la entrega 10e

Tampoco cambia lo que se guarda (`SCHEMA_V` = 14): cambia cómo se calculan algunas variables con lo ya guardado, **también en los días viejos**, y hay dos variables nuevas. Detalle y motivo en el Apéndice D (D.88 a D.107); los modelos de sustancias y suplementos se explican en el capítulo 5.

| Variable | Definición desde 10e | Antes |
|---|---|---|
| `error_prediccion_signo` (nueva) | Valencia de hoy − predicción de la noche anterior (−6 a +6; positiva, el día salió mejor de lo predicho). Los mismos días que `error_prediccion` (D.94) | No existía; solo el valor absoluto (H-2-10) |
| `gasto_imprevisto_monto` (nueva) | Suma de los gastos imprevistos del día; 0 desde el primer gasto en los días con diario (`ceroDesde`) y en los días con gastos sin ninguno imprevisto; vacía antes (D.94) | No existía; `gasto_imprevisto` solo dice si hubo alguno (0/1) (H-1-5) |
| `personas` | Personas distintas nombradas con @ en el diario **y** en los momentos no privados de ese día, en los mismos días en que puede valer 0 (`Dinamicas.diasSociales`) (D.93) | Solo el diario: un día con solo momentos valía 0 aunque nombraras a alguien (H-14-2) |
| `alcohol_estimado_max` | Cada día con alcohol mira de su primera dosis a 14 horas después, o hasta justo antes de la primera dosis del siguiente día con alcohol; la simulación incluye las dosis de las 24 horas previas, y se lee cada 5 minutos, como `Sustancias.alcoholMaximo` (D.88, D.89) | Cada día empezaba en 0 y se leía cada 10 minutos (H-4-5, H-14-5) |
| `alcohol` | En el hilo principal, cuando aún no están calculadas las dosis de MacroFactor: 1 los días con un episodio anotado y vacío los demás (D.90). Fuera de ese caso, como en A.6 | Esos días sin episodio valían 0 (H-14-4) |
| `cafeina_total_mg`, `alcohol_bebidas` (y lo que sale de las mismas dosis: `cafeina_al_dormir_mg`, `alcohol_estimado_max`, `alcohol_al_dormir`) | MacroFactor y la app se juntan toma por toma (`Sustancias.dosis`, `MISMA_TOMA` = 90 min). Cafeína: una toma de la app y una de MacroFactor a menos de 90 minutos y de cantidad parecida (de la mitad al doble) cuentan una vez, con la hora de la app. Alcohol: un episodio de la app y lo de MacroFactor entre su inicio y su fin (±90 minutos) son el mismo, y cuenta la fuente con más gramos (D.91) | Si MacroFactor tenía algo de esa sustancia ese día, se ignoraba todo lo de la app (H-4-2) |
| `cafeina_ultima_h` | Hora de la última de esas mismas dosis, por día lógico; antes de las 5:00 suma 24. En `vm_panel.csv`, la misma; si no se pudo calcular, la de MacroFactor. `cafeina_mg` sigue siendo el nutriente de MacroFactor tal cual (D.92) | La de MacroFactor, por su fecha de calendario (H-4-12, en parte) |
| `creatina_saturacion_pct` | Llega al 95 % en los días de carga (28 por omisión) con **3 g** al día, la dosis de referencia (`Suplementos.dosisReferencia`); con 5 g al día se llena antes, en unos 17 días (D.98) | Con la dosis que pusiste (5 g en el catálogo) llegaba al 95 % en 28 días (H-4-6) |
| `supl_<id>_nivel` | Solo en los suplementos con vida media de 6 horas o más (`Suplementos.VIDA_MEDIA_MIN_NIVEL`); la melatonina del catálogo (1 hora) ya no lo tiene (D.98) | También en los de vida media corta, que a las 5:00 casi siempre valían 0 (H-4-7, en parte) |

**El diccionario y el LEEME (10e).** La descripción de `cafeina_al_dormir_mg`, `creatina_saturacion_pct` y cada `supl_<id>_nivel` termina con los parámetros vigentes al exportar (vida media, o días de carga y de lavado con la dosis de referencia) y con el aviso de que cambiarlos recalcula toda la historia (`Diccionario.conParametros`, D.99; H-4-11, en parte). El LEEME y el diccionario dicen «esquema 13» y «esquema 14» donde decían «v13» y «v14», y el LEEME dice que el esquema de cada día está en `schema_v` de `vm_dias.csv` (D.107, H-14-1). Las descripciones nuevas del diccionario son las de `error_prediccion_signo`, `gasto_imprevisto_monto`, `cafeina_ultima_h` y `vm_panel_extendido.csv:personas`.

## A.9 Columnas nuevas del esquema 15 (entrega 10f)

Cambia lo que se guarda: `SCHEMA_V` = 15. Una variable nueva en el panel de la app (`fuerza_ref_kg`) y columnas nuevas en la exportación, que no son variables del panel. Detalle y motivo en el Apéndice D (D.109 a D.118).

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_panel.csv` | `desfase_utc_h` | Horas de diferencia con UTC a mediodía de ese día, con la zona que tenía el teléfono entonces (`Fechas.desfaseH`; −6 en la Ciudad de México). Desde 10f, cada hora y cada fecha de las tablas es la hora local de entonces (D.109, D.110; capítulo 2, sección 2.3.1) |
| `vm_panel.csv` | `cambio_horario` | 0/1: el desfase con UTC al empezar el día lógico no es el de su último instante (`Fechas.cambioDeHora`): horario de verano o un viaje. Ese día no duró 24 horas (fórmula B.24) |
| `vm_panel.csv` | `clima_reanalisis` | 0/1: el clima del día viene del archivo histórico de Open-Meteo (reanálisis, tipo ERA5) (1) o de los modelos de pronóstico (0); vacío si no hay clima o no se sabe de dónde vino (D.111; capítulo 4) |
| `vm_entorno.csv` | `fuente` | `archivo` o `pronostico`. Lo guardado antes del esquema 15 se deduce (`Entorno.fuente`): `archivo` si se pidió más de 80 días después de la fecha (`Entorno.DIAS_ARCHIVO`). Los días del último año que vinieron del pronóstico se vuelven a pedir al archivo cuando pasan de 80 días; el índice UV (`uv_max`), que el archivo no trae, se conserva del pronóstico (D.111) |
| `vm_conexiones.csv` | `wifi_sin_dato_min` | Minutos en que no se pudo saber si había wifi (sin permiso o sin respuesta del sistema). Desde 10f no cuentan en `sin_wifi_min`, que puede bajar desde esta versión; antes, sí contaban (D.112; capítulo 4) |
| `vm_experimentos.csv` | `sentido`, `umbral` | El criterio sellado: hacia dónde esperabas el cambio (1 sube, −1 baja; vacío = no lo dijiste) y la diferencia mínima que contaría como efecto, en las unidades de la medida (vacío = cualquiera con el intervalo lejos del cero). Fórmula B.22 (D.113; capítulo 8) |
| `vm_experimentos.csv` | `huella`, `intacto` | SHA-256 de lo sellado (`Experimentos.huella`) y si sigue coincidiendo con lo que hay ahora (1), si cambió después del sello (0) o si no tiene huella (vacío, experimentos de antes del esquema 15) |
| `vm_entrenos.csv`, `vm_panel.csv` | `fuerza_ref_kg` | La mejor 1RM estimada del día en el press inclinado con mancuernas (fórmula B.23); vacía los días sin ese ejercicio (D.114) |
| `vm_series.csv` | `referencia` | 0/1: la serie es del ejercicio de referencia (`Macrofactor.esReferencia`) |
| `vm_diccionario.csv` | `desde_version`, `cambio` | Desde qué versión de la app rige la definición actual de la columna y qué cambió entonces, con su fila del Apéndice D; vacío = sin cambios desde la v84 (`Diccionario.desde`; D.115, H-1-8). La tabla que las llena (`Diccionario.DESDE`, 165 entradas, con comodines como `per_*`) se armó con las filas del Apéndice D (capítulo 2, sección 2.8) |

**Lo que cambia sin columna nueva.** `sin_wifi_min` (arriba) y todas las horas que la app saca de un instante: `primer_uso_h`, `ultimo_uso_h`, `cafeina_ultima_h`, las horas de `vm_horas.csv` y las demás `*_h` se calculan con la zona que tenía el teléfono entonces, o con el desfase de la propia nota (`Fechas.local`, `Fechas.localDe`). Mientras no viajes, nada cambia. Lo que el teléfono ya guardó sumado por día (sueño, pasos, pantalla, ubicaciones) no se recalcula. La plantilla «Creatina» mide desde 10f `fuerza_ref_kg` en lugar de `volumen_kg`; un experimento creado con la plantilla de antes sigue midiendo `volumen_kg`.

## A.10 Tablas nuevas del esquema 16 (Ideario)

Cambia lo que se guarda: `SCHEMA_V` = 16, con una colección nueva (`ideas`) y un ajuste (`Config.ideas_resurgir`, si sale «Una idea de antes» en el Día). No hay variables nuevas en el panel ni cambia ninguna columna de las tablas anteriores. Detalle en el capítulo 2 (sección 2.5) y en el Apéndice D (D.123 a D.126).

`vm_ideas.csv` tiene una fila por idea (`Exportar.filasIdeas`):

| Columna | Qué es |
|---|---|
| `id`, `creada`, `fecha`, `editada` | Identificador; cuándo se escribió (fecha y hora con su desfase); el día al que pertenece (por omisión, el día lógico en que se anotó; se puede cambiar); la última edición |
| `titulo` | La idea en una línea (opcional) |
| `texto` | Lo que entendiste, con tus palabras, con sus @personas y #temas |
| `origen` | `leido`, `visto` (o escuchado), `contado` o `propia`; vacío = sin decir |
| `quien` | Lista de autores o invitados que lo dicen. No son personas de Tu gente: no cuentan en la red social |
| `obra_id`, `obra_titulo`, `cita_id` | La obra y la cita de las que salió; `obra_titulo` queda vacío si la obra se borró |
| `fuente`, `enlace`, `donde` | De dónde salió cuando no hay obra (escrita a mano o al compartir), su enlace y el minuto o la página |
| `postura` | Cómo la tienes: `convence`, `piensa` (te hace pensar), `duda` o `desacuerdo`; vacío = sin decir |
| `fijada`, `n_revisiones` | 0/1 si está fijada arriba; cuántas veces la respondiste en «Una idea de antes» |
| `personas`, `temas` | Las @personas y los #temas escritos en el texto (listas) |

`vm_ideas_revisiones.csv` tiene una fila por respuesta (`Exportar.filasIdeasRevisiones`): `idea_id`, `fecha` (día lógico en que respondiste) y `respuesta` (`sigue`, «sigo pensando igual»; `cambio`, «ya no»; `ignorar`, «ahora no»). En R se leen como `vm$ideas` y `vm$ideas_revisiones`.

**Al leer los datos.** Las ideas no son una muestra de lo que aprendiste: solo está lo que decidiste anotar. `postura` y `revisiones` son autorreporte y empiezan vacías en casi todas las ideas. Una idea pasada desde una nota conserva la `creada` y la `fecha` de la nota; una pasada desde el diario es una copia con la fecha de ese día. `Diccionario.DESDE` no tiene entrada para estas columnas porque son nuevas: no hay una definición anterior con la cual compararlas.

## A.11 Cambios del esquema 17 (pantallas de captura)

Cambia lo que se guarda: `SCHEMA_V` = 17. El detalle está en el capítulo 3 y en el Apéndice D (desde D.127).

**Sueños** (`vm_dias.csv`, `vm_panel.csv`):

| Columna | Qué es |
|---|---|
| `sueno_rareza` | `realista`, `raro` (pero posible) o `imposible` (o surreal). En días de antes del esquema 17 se deduce del tipo: mundano = realista, bizarro = imposible |
| `sueno_pesadilla` | 1 si fue pesadilla (angustia fuerte o te despertó), 0 si dijiste que no y vacío si no se dijo. En días de antes, 1 si el tipo era «ansioso o pesadilla» |
| `sueno_tipo` | Ya no se pregunta; solo trae valor en los días de antes del esquema 17 que no se han corregido |
| `sueno_categorias`, `sueno_<categoría>` | Nueve ejes (prefijos `per_`, `esc_`, `int_`, `tema_`, `res_`, `sen_`, `est_`, `emo_`, `desp_`). El tipo «recurrente» de antes cuenta como `est_recurrente` |
| `suenot_realista`, `suenot_raro`, `suenot_imposible`, `suenot_pesadilla` (panel extendido) | Reemplazan a `suenot_<tipo>` |

**Momentos y hoja de la noche** (`vm_momentos.csv`, `vm_dias.csv`, `vm_panel.csv`):

| Columna | Qué es |
|---|---|
| `vm_momentos.csv:intensidades` | Lista `emoción:n` (1 un poco, 2 bastante, 3 mucho); solo las emociones a las que les pusiste intensidad |
| `vm_momentos.csv:pensamientos` | Lista de pensamientos. `pensamiento` queda con el primero, por compatibilidad |
| `vm_momentos.csv:detonante_otro`, `vm_dias.csv:detonante_otro` | El detonante escrito con «otro» (seguimiento privado) |
| `n_<emoción>` nuevas | `n_amor`, `n_decepcion`, `n_anhelo`…; solo desde que se usan. Antes del esquema 17 no se podían marcar |
| `n_<pensamiento>` nuevos | `n_trabajo`, `n_cuerpo`, `n_imaginando`, `n_nada`. Un registro con varios pensamientos cuenta en cada uno |
| `con_hijos`, `con_mascota`, `con_mensaje` | Igual que los demás `con_*`. «Mensaje» puede ir con «solo» y no cuenta como acompañado |
| `ctx_casa`, `ctx_fiesta`, `ctx_tramites` | Contextos nuevos; 0 en los días de antes (no existían) |
| `vm_dias.csv:ctx_otro` | Otro contexto escrito a mano |
| `vm_dias.csv:habitos_tri`, `hab_*` | Con `habitos_tri` = 1, un hábito sin respuesta queda vacío en `hab_*` en vez de 0 |
