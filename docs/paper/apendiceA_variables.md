# Apéndice A. Variables y tablas del sistema

_Generado a partir del código de la versión v81 (commit `fa527df`): `ciencia/Estadistica.kt` (`variables()`), `ciencia/Dinamicas.kt` y `data/Exportar.kt` (`tablas()`). Lo que dice «Si hay datos…» describe cuándo la variable existe en una instalación concreta: el catálogo se arma con lo que el usuario ha registrado._

**Cómo leer las columnas.** *Tipo* y *Unidad* se leen del nombre que ve el usuario en la app (la app no guarda un tipo formal por variable). *Fuente* es la de todo el grupo; la procedencia exacta de cada variable está en el panel (`Estadistica.panel`). *Un valor vacío significa «sin dato»; un 0 significa «se observó y fue cero»* salvo en las reglas de relleno con ceros del capítulo 2, sección 2.6.

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
| `sueno_vividez` | Vividez del sueño (0 vago, 1 moderado, 2 muy vívido) | numérica | — | Si hay datos de la fuente |
| `siesta` | Tomé siesta (0/1) | binaria (0/1) | — | Si hay tramos del cronómetro |
| `siesta_min` | Minutos de siesta | numérica | — | Si hay tramos del cronómetro |
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
| `canciones` | Canciones escuchadas | numérica | — | Si hay datos de la fuente |
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
| `personas` | Personas distintas mencionadas | numérica | — | Si hay datos de la fuente |
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
| `sin_wifi_min` | Minutos sin wifi (fuera) | numérica | — | Si hay datos de la fuente |
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
| `cafeina_ultima_h` | Hora de la última cafeína | numérica | — | Si hay datos de la fuente |

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
| `alcohol_estimado_max` | Alcohol en sangre máximo estimado (g/L) | numérica | g/L | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_al_dormir` | Alcohol en sangre estimado al dormirme (g/L) | numérica | g/L | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_nivel_max` | Qué tan pedo llegué (0 a 10) | numérica | — | Si hay consumos de alcohol |
| `alcohol_horas` | Horas con efecto del alcohol | numérica | — | Si hay consumos de alcohol |
| `cannabis` | Cannabis (0/1) | binaria (0/1) | — | Si hay consumos de cannabis |
| `cannabis_nivel_max` | Nivel máximo con cannabis (0 a 10) | numérica | — | Si hay consumos de cannabis |
| `cannabis_horas` | Horas con efecto del cannabis | numérica | — | Si hay consumos de cannabis |
| `tabaco_n` | Cigarros o sesiones de vape | numérica | — | Si hay consumos de tabaco |
| `otra_sustancia` | Otra sustancia (0/1) | binaria (0/1) | — | Si hay consumos de «otra» |
| `cruda` | Cruda o bajón (0 a 5, del día anterior) | numérica | — | Si algún consumo tiene cruda |
| `cafeina_total_mg` | Cafeína del día (MacroFactor o anotada, mg) | numérica | — | Si hay cafeína registrada o en MacroFactor |
| `cafeina_al_dormir_mg` | Cafeína en el cuerpo al dormirme (mg, estimada) | numérica | — | Si hay cafeína registrada o en MacroFactor |

### Privado

*Fuente:* Pantalla Privado. Desde el módulo 10a estas variables **no aparecen en ninguna lista ni análisis de la app**; el panel las calcula siempre (con o sin el interruptor) y van en la exportación para R. Tampoco aparece `hab_` del hábito ligado a las recaídas.

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `impulso` | Intensidad del impulso | numérica | — | Si se anotó |
| `recaida` | Recaída (0/1) | binaria (0/1) | — | Si se anotó |
| `n_recaida` | Recaídas registradas con hora | numérica | — | Desde la primera recaída con hora |
| `recaida_min` | Minutos de recaída | numérica | — | Si hay recaídas con duración |
| `libido_max` | Libido máxima del día (0 a 5) | numérica | — | Si hay registros de libido |
| `n_libido` | Registros de libido | numérica | — | Si hay registros de libido |
| `n_impulso` | Momentos de impulso | numérica | — | Días con diario (hasta v84 estaba en el grupo «Momentos» y entraba siempre en los análisis) |
| `medicamentos_n` | Medicamentos tomados | numérica | — | Desde el primer día con medicamentos (hasta v84, grupo «Salud») |

### Enfoque

*Fuente:* Cronómetro de enfoque y siestas

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `enfoque_min` | Minutos de enfoque (cronómetro) | numérica | — | Si hay tramos del cronómetro |

### Suplementos

*Fuente:* Tomas de suplementos y modelos de carga

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `creatina_saturacion_pct` | Saturación de creatina estimada (%) | numérica | % | Si el suplemento correspondiente tiene datos |
| `probioticos_porciones` | Porciones de probióticos (anotadas y estimadas) | numérica | — | Si el suplemento correspondiente tiene datos |
| `probioticos_estimado` | Porciones de probióticos leídas en tu bitácora de comidas | numérica | — | Si el suplemento correspondiente tiene datos |

### Laptop

*Fuente:* Cold Turkey (importación)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `laptop_min` | Minutos en la laptop | numérica | — | Si se importó Cold Turkey |
| `laptop_web_min` | Minutos en sitios web (laptop) | numérica | — | Si se importó Cold Turkey |

_Total de variables fijas listadas en el código: 177 en 22 grupos._

### Variables con nombre generado

Estas se crean una por cada elemento que el usuario ha usado; el identificador lleva el id del elemento.

| Patrón | Descripción | Grupo |
|---|---|---|
| `n_$id` | Registros con ${Catalogo.nombreSentimiento(id).lowercase()} | Sentimientos |
| `n_$id` | Registros pensando: ${(Catalogo.PENSAMIENTOS.firstOrNull { it.id == id }?.nombre ?: id).lowercase()} | Momentos |
| `sueno_$id` | Sueño: ${Catalogo.opcionSueno(id)?.nombre?.lowercase() ?: id} (0/1) | Sueños |
| `enfoque_${t}_min` | Enfoque: ${Catalogo.nombreTramo(t).lowercase()} (min) | Enfoque |
| `horas_tipo_$t` | Horas en lugares de tipo ${Catalogo.nombreTipoLugar(t).lowercase()} | Lugares |
| `laptop_${o.id}_min` | Minutos en la laptop: ${o.nombre.lowercase()} | Laptop |
| `con_${o.id}` | Avisos con ${o.nombre.lowercase()} (0 a 1) | Social |
| `ctx_${it.id}` | Contexto: ${it.nombre} (0/1) | Contexto |
| `${p.clave}_${o.id}` | ${o.nombre} (0/1) | Lectura del día |
| `n_${it.id}` | Momentos de ${it.nombre.lowercase()} | Momentos |
| `hab_${it.id}` | Hábito: ${it.nombre} (0/1) | Hábitos |
| `ctx_<id>` | Contexto: <nombre> (0/1); 10 contextos: trabajo, gym, viaje, enfermo, académico, ocio inmersivo, naturaleza, amigos, romántico, familia | Contexto |
| `flow_<id>`, `sdt_necesidad_<id>`, `apreciacion_<id>` | Opción elegida en las tres preguntas científicas del día (0/1) | Lectura del día |
| `hab_<id>` | Hábito: <nombre> (0/1) | Hábitos |
| `sueno_<categoria>` | Categoría del sueño marcada (0/1), solo en días con categorías | Sueños |
| `supl_<id>_dosis/_tomado/_nivel/_porciones` | Dosis, toma, reserva o cantidad estimada y porciones del suplemento | Suplementos |
| `horas_tipo_<tipo>` | Horas en lugares de ese tipo (12 tipos de lugar posibles) | Lugares |
| `enfoque_<tipo>_min` | Minutos de enfoque de ese tipo (estudio, lectura, idiomas, tesis, trabajo, creativo) | Enfoque |
| `laptop_<categoria>_min` | Minutos en la laptop por categoría (productivo, ocio, social, otro) | Laptop |
| `campana_<reto>`, `campana_hab_<id>` | En campaña ese día (0/1; la tenías aceptada). 0 desde tu primera campaña, vacío antes. Desde el esquema 13 | Campañas |
| `campanas_n` | Campañas que tenías ese día. Desde el esquema 13 | Campañas |
| `dia_especial` | Ese día cae un cumpleaños, un aniversario o una fecha propia (0/1). Existe si hay alguna fecha guardada. Desde el esquema 13 | Calendario |

**Pantalla y pasos, desde el esquema 13.** `pantalla_min`, `desbloqueos`, `primer_uso_h`, `ultimo_uso_h` y `pasos` se calculan por **día lógico** (de 05:00 a 05:00) en los días nuevos; los de antes quedaron por día de calendario. La ventana de cada día está en `vm_pantalla.csv:ventana`, `vm_salud.csv:ventana_pasos` y, en el panel exportado, `ventana_pantalla` y `ventana_pasos` (capítulo 2, sección 2.3.2).

## A.2 Familias de variables dinámicas

Cada familia crea una variable por cada elemento (persona, tema, lugar, app…) que aparece en al menos *Mín. días* días (en el análisis); en la exportación extendida basta con 2 días y se permiten hasta 400 por familia. *Máx.* es el tope en el análisis. Fuera de los días con dato de la fuente, el valor es 0 en las fechas de la *cobertura* de cada familia.

| Prefijo | Grupo | Máx. | Mín. días | Plantilla del nombre |
|---|---|---|---|---|
| `per_` | Personas | 15 | 5 | Aparece @‹elemento› (diario, momentos, sueños o cartas) (0/1) |
| `pen_` | Personas | 10 | 3 | Pensé en @‹elemento› sin verla (0/1) |
| `tema_` | Temas | 12 | 5 | Aparece #‹elemento› (0/1) |
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
| `suenot_` | Sueño | 6 | 3 | Sueño de tipo ‹elemento› (0/1) |
| `ciu_` | Mundo | 8 | 3 | Horas en ‹elemento› |
| `pais_` | Mundo | 6 | 3 | Horas en ‹elemento› |
| `musc_` | Cuerpo | 12 | 3 | Series de ‹elemento› |
| `act_` | Cuerpo | 6 | 3 | Minutos ‹elemento› |
| `nut_` | Nutrición | 60 | 14 | nombre del nutriente de MacroFactor traducido (p. ej. «Vitamina C (mg)»); excluye los ya fijos, los objetivos y los pasos/peso de MacroFactor |
| `ali_` | Nutrición | 12 | 5 | Comí ‹elemento› (0/1) |
| `esc_` | Cuestionarios | todos | — | «‹cuestionario›: ‹puntaje›»; el puntaje vale para los 14 (WHO-5) o 30 días (PSS-4, UCLA-3) previos, o hasta el siguiente (personalidad) |
| `pol_` | Política | ejes | — | «Brújula: ‹eje›»; la posición de cada test vale hasta el test siguiente (máx. 400 días) |
| `vida_` | Etapas de vida | 14 + ciudades | — | «Etapa: ‹campo de Conocerte›»; se reconstruye con el historial de cambios |

## A.3 Tablas exportadas

La exportación genera 50 tablas CSV con datos (48 hasta el esquema 12; el 13 agregó `vm_personas.csv` y `vm_fechas_propias.csv`), más `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv`, `vm_diccionario.csv`, `vm_respaldo.json`, `vm_datos.sqlite` (una tabla por CSV, con las ubicaciones y el diccionario) y un `LEEME.txt`. Todos los CSV usan UTF-8 con BOM, fechas AAAA-MM-DD, booleanos 0/1, listas separadas por «|» y vacío para el dato faltante.

| Archivo | Contenido |
|---|---|
| `vm_panel.csv` | Una fila por día con lo principal de todas las fuentes |
| `vm_panel_extendido.csv` | Una fila por día con todas las variables: cada persona, lugar, app, categoría, artista, obra, ciudad, nutriente y más |
| `vm_dias.csv` | Diario: lo que llenaste cada día |
| `vm_momentos.csv` | Momentos y avisos de ánimo, uno por fila |
| `vm_tramos.csv` | Cronómetro: bloques de enfoque (estudio, lectura, idiomas, tesis…) y siestas, uno por fila |
| `vm_sustancias.csv` | Sustancias: cada consumo (alcohol, cannabis, tabaco, cafeína, otra) con su nivel, duración y cruda |
| `vm_horas.csv` | Una fila por hora: dormido, lugar, pantalla, notificaciones, luz, música, ánimo, cafeína y alcohol en el cuerpo, sustancias, enfoque, gastos y comidas |
| `vm_gastos.csv` | Gastos, uno por fila |
| `vm_notas.csv` | Notas sueltas: ideas, pensamientos, propósitos y recordatorios |
| `vm_suplementos.csv` | Cada toma de suplemento (y las porciones de probióticos estimadas de tu bitácora de comidas) |
| `vm_laptop.csv` | Minutos por app y por sitio en la laptop, por día (Cold Turkey) |
| `vm_red_nodos.csv` | Personas de tu red: relación, cercanía, días mencionada y grado |
| `vm_red_vinculos.csv` | Pares de personas: cómo se conocen según tú y cuántos días aparecen juntas |
| `vm_personas.csv` | Tu gente: relación, cercanía, desde cuándo, nacimiento, fallecimiento y edad (esquema 13) |
| `vm_fechas_propias.csv` | Fechas propias que se repiten cada año (esquema 13) |
| `vm_experimentos.csv` | Experimentos personales: pregunta, hipótesis sellada, diseño, lavado y plan por semana (1 = con la acción); desde el esquema 13, también `fin` y `hecho` («¿Lo hiciste hoy?») |
| `vm_metas.csv` | Metas: horizonte, fecha objetivo, estado, medida, progreso e historial de cambios |
| `vm_ingresos.csv` | Ingresos, uno por fila |
| `vm_escalas.csv` | Cuestionarios validados (WHO-5, PSS-4, UCLA-3, TIPI) |
| `vm_reaccion.csv` | Pruebas de reacción |
| `vm_salud.csv` | Health Connect por día (reloj y báscula) |
| `vm_pantalla.csv` | Tiempo en pantalla por día |
| `vm_visitas.csv` | Llegadas y salidas de tus lugares |
| `vm_entorno.csv` | Clima, luz, aire y luna por día |
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
| `vm_conexiones.csv` | Wifi, audio, carga, batería y luz por día |
| `vm_subrayados.csv` | Subrayados y notas de Kindle |
| `vm_google_dia.csv` | YouTube y búsquedas de Google por día |
| `vm_google.csv` | YouTube y búsquedas de Google, una por fila |
| `vm_youtube.csv` | YouTube (CSV de Takeout): suscripciones, «me gusta», videos en listas, comentarios y chats (sin texto) |
| `vm_whatsapp.csv` | WhatsApp: un chat por día por fila (solo conteos) |
| `vm_nutricion.csv` | MacroFactor: nutrición, gasto energético y peso por día |
| `vm_comidas.csv` | MacroFactor: cada alimento registrado |
| `vm_entrenos.csv` | MacroFactor Workouts: resumen por día y por grupo muscular |
| `vm_series.csv` | MacroFactor Workouts: cada serie |
| `vm_panel.csv` | Una fila por día con lo principal de todas las fuentes |

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
| `vm_dias.csv`, `vm_panel.csv` | `version_noche` | Con qué orden de preguntas se llenó la hoja de la noche: 1 = el de hasta el esquema 14; 2 = el orden nuevo (N-18, desde la entrega N4: el ánimo del momento primero; capítulo 3, sección 3.7.1). Vacío = no pasó por la hoja o es de antes |
| `vm_salud.csv` | `sueno_descartadas` | Lo que llegó del reloj esa noche y marcaste que no cuenta (lista) |

**Cómo leer `ts_creado`.** La hora de un registro (`ts`, `inicio` o `fecha` + `hora`) dice **cuándo pasó**; `ts_creado`, **cuándo lo anotaste**. La diferencia dice si se anotó en el momento o después. En R, `vm_retraso(tabla, umbral_min = 60)` la calcula (`retraso_min`) y marca `tarde` si pasó más del umbral o cayó en otro día lógico (Apéndice C). El umbral es **[Diseño propio]**.

