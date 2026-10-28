# Capítulo 4. Sensores, datos pasivos e importaciones

> **Para qué sirve este capítulo.** Explica, fuente por fuente, **qué mide la app sin que tú escribas nada** (datos *pasivos*: lo que el teléfono o el reloj registran solos) y **qué entra cuando cargas un archivo** (datos *importados*): cómo se obtiene, qué tan confiable es, qué pasa cuando falta y a qué día se asigna. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo que cambió en las entregas 10e y 10f se marca «desde 10e» y «desde 10f». Los ejemplos son **inventados**. Los problemas se numeran H-3-n (por el módulo que los encontró) y se detallan en `hallazgos.md`.

---

## 4.1 Cómo y cuándo llegan los datos

**El «pulso» de 15 minutos.** La app programa una tarea periódica (`Pulso`, con `PeriodicWorkRequestBuilder` de WorkManager) **cada 15 minutos**. En cada pulso guarda un punto de ubicación, toma una «muestra de percepción» (wifi, audio, carga, luz, presión), revisa si llevas rato en un lugar nuevo, consulta Spotify (si pasaron al menos 25 minutos desde la última vez), decide si toca algún aviso y, una vez al día, sincroniza lo demás. 15 minutos es el **mínimo** que Android permite, y el sistema puede **retrasar** la tarea para ahorrar batería: el pulso es una *intención*, no un reloj exacto. Por eso los cálculos que dependen de él usan el tiempo *real* entre lecturas (sección 4.5).

**La sincronización diaria.** Después de las 06:00 (`Trabajo.kt`, función `diario`) la app trae reloj (últimos **3** días), pantalla (**2**), fotos (3), agenda, llamadas/SMS (3) y clima, y hace un respaldo; a partir de las 21:00 hay una segunda pasada más corta, y una vez al mes rellena historia atrasada (versión completa, §4.2.2). Si la app no corrió durante más días que esa ventana, esos días **no se recuperan solos**; hay botones que leen hasta 30 días del reloj y 7 de pantalla.

**Permisos.** Sin permiso no hay dato, y la app **no inventa nada**: la variable queda vacía (`null`, capítulo 2). La pantalla **Estado de los datos** (`EstadoDatos.kt`) pone cada fuente en *Al día*, *Atrasada*, *Faltan permisos* o *Apagada*. Saber *por qué* falta un dato importa: un hueco por «sin permiso» no es uno porque «no pasó nada». Pero **la app no guarda el motivo en el panel**: el análisis ve solo una celda vacía (limitación L-4.1, sección 4.10).

---

## 4.2 Dos relojes: día lógico y día de calendario

El **día lógico** de la app va de 05:00 a 04:59 (capítulo 2): un evento a la 01:00 cuenta para el día *anterior*. El **día de calendario** va de 00:00 a 24:00. **No todas las fuentes usan el mismo:**

| Usan **día lógico** | Usan **día de calendario** |
|---|---|
| Ubicación (km), movimiento, wifi/audio/carga/luz/presión, notificaciones, música, fotos, llamadas/SMS, Spotify, Google Takeout, WhatsApp, Kindle, ejercicio del reloj y, **desde el esquema 13, pantalla y pasos** | Corazón, oxígeno, calorías y demás lecturas del reloj, agenda, clima/aire, ciudades y países (`Mundo`), Cold Turkey y MacroFactor; pantalla y pasos **hasta el esquema 12** |

El **sueño** del reloj va al **día en que despiertas**.

**Ejemplo, como era hasta el esquema 12.** Un lunes usas el teléfono de 00:30 a 01:30. Esa hora de **pantalla** se sumaba al **martes** (calendario), pero las **notificaciones** y el **wifi** de esa hora iban al **lunes** (lógico): dentro de una misma fila, las variables no hablaban del mismo día. Es **H-3-1** (gravedad alta, porque afecta el uso nocturno, el más interesante para el sueño). Para quien se duerme antes de la medianoche el efecto es pequeño; para quien usa el teléfono de madrugada, es real. **[Aproximación]** mientras queden variables por calendario.

**Lo que cambió en el esquema 13.** La pantalla y los pasos pasan al día lógico de aquí en adelante. Cada día guarda qué ventana usó (`PantallaDia.ventana`, `SaludDia.ventana_pasos`: `"logica"` o `"calendario"`), y el panel exportado lleva `ventana_pantalla` y `ventana_pasos` para analizar por separado los dos tramos de la serie. La app no recalcula sola los días de antes (Android solo guarda unos días de eventos de uso). Desde la 10c, el diccionario marca «por día de calendario» las variables que siguen así (`Diccionario.CALENDARIO`). H-3-1 queda **resuelto en parte**. En los días nuevos también se corrige **H-3-2**: `primer_uso_h` ya no toma un uso de la 01:00 como «primer uso», y la corrección «+24 si la hora es menor a 5» de `ultimo_uso_h` sí actúa (`Estadistica.panel`).

**La hora local de entonces (desde 10f; H-1-7, D.109 y D.110).** Hasta la v91, cada instante se convertía con la zona horaria que el teléfono tiene **hoy**: tras un viaje a Madrid (UTC+2), una hora de dormir de las 23:30 de allá se leía, ya de vuelta en UTC−6, como las 15:30. Desde 10f la app guarda cada cambio de zona del teléfono (`Config.zonas`; se anota al abrir la app, en cada pulso y con el aviso `TIMEZONE_CHANGED`) y convierte cada instante con la zona de entonces (`Fechas.zonaEn`); una nota con desfase se lee con el suyo. Así lo hacen Health Connect, la pantalla, las fotos, WhatsApp, Kindle y las ubicaciones. Un día con cambio de horario dura 23 o 25 horas (el de un viaje, otras), y `vm_panel.csv` lo marca con `desfase_utc_h` y `cambio_horario`. Para lo de antes, el historial se arma con las notas: un periodo cuenta con otro desfase si **dos notas seguidas** lo traen; lo demás queda con la zona de hoy. **[Diseño propio]**.

---

## 4.3 Pantalla (`sensores/Pantalla.kt`)

Android guarda una bitácora de eventos de uso, y la app arma con ella «tramos»: cuando una app pasa a primer plano (`ACTIVITY_RESUMED`) se cierra el tramo anterior y se abre uno nuevo (**no cuentan** el lanzador, es decir, la pantalla de inicio, ni la propia Vestigium Mei); el tramo termina cuando la app sale (`ACTIVITY_PAUSED`), cuando la pantalla se apaga (`SCREEN_NON_INTERACTIVE`) o al final de la ventana. Los **desbloqueos** son los eventos `KEYGUARD_HIDDEN`. `total_min` suma todos los tramos, **sin descartar nada**; `por_app_min` guarda solo apps con **≥ 1 minuto** (máximo **40**), y cada tramo se reparte entre las horas que cruza (`por_hora_min`).

Es **tiempo de apps en primer plano**, no «pantalla encendida»: un video que ves sin tocar cuenta; la pantalla de inicio encendida no. Por eso **no coincidirá exactamente** con «Bienestar digital». **[Diseño propio]** la definición. Se mide de forma pasiva porque lo que la gente *dice* que usa los medios digitales **solo se correlaciona moderadamente** con lo que registran los dispositivos (Parry et al., 2021). **[Validado]**.

Si el reloj registró la noche, se calculan `cama_min` (pantalla en la **última hora antes de dormirte**), `dormir_tras_tel_min` (de soltar el teléfono, último uso en las 3 horas previas, a dormirte) y `tel_tras_despertar_min` (de despertar al primer uso en las 4 horas siguientes). Sirven para *cruzar* con tu sueño, no para afirmar causalidad. **[Aproximación]**: heredan el error de la hora de dormir del reloj.

---

## 4.4 Ubicación, distancia y lugares

### 4.4.1 Puntos y distancia (`sensores/Ubicacion.kt`)

En cada pulso, con permiso de ubicación *en segundo plano*, la app pide una lectura de **potencia equilibrada** (`PRIORITY_BALANCED_POWER_ACCURACY`: en general usa wifi y antenas antes que el GPS; gasta poca batería, pero es menos precisa). Además registra **geocercas**: un círculo de radio `max(radio del lugar, 80 m)` por cada lugar guardado (hasta **95**), y Android avisa al **entrar** y al **salir**.

Según la documentación de Android (`Location.getAccuracy`), el campo `precision` es el **radio horizontal con 68 % de confianza**: hay 68 % de probabilidad de que la posición verdadera esté dentro de ese círculo. Es normal ver errores de decenas o cientos de metros. Desde la 10d, un punto importado **sin** precisión cuenta como de **100 m** (`Db.PRECISION_SIN_DATO`), no de 0 m. **[Diseño propio]**.

`Movilidad.kmPorDia` suma la distancia en línea recta entre cada par de puntos consecutivos (fórmula del *haversine*, radio terrestre 6 371 km) **si** pasaron entre **1 segundo y 2 horas**, la velocidad implícita es menor a **70 m/s** (descarta saltos imposibles) y la distancia es mayor a **30 m** (descarta el «temblor» del GPS quieto). Va al día lógico del segundo punto; un día con puntos pero sin tramos que cuenten vale **0 km**. Es una **cota inferior**: la línea recta siempre es ≤ el camino real, y se queda más corta los días con menos puntos. **[Aproximación]** (**H-3-5**, resuelto por documentación: no se pondera por fuente).

### 4.4.2 Lugares y visitas (`data/Lugares.kt`)

Cuando llevas un rato en un sitio que no tienes guardado, la app pregunta si es un «lugar especial». La regla (`estancia`; por omisión radio **75 m** y **20 min**): toma los puntos recientes, **sin fotos** y con **precisión ≤ 120 m**; hacen falta **≥ 2 puntos** y el último debe tener **≤ 25 min**; desde el último punto va hacia atrás **mientras cada punto esté a ≤ 75 m del último** (no del centro del grupo), y si ese tramo dura **≥ 20 min**, es candidato. Para no molestar (`revisar`): solo entre **08:00 y 22:00**, **máximo 2 preguntas al día** y **3 veces** por sitio.

Como los puntos llegan cada ~15 minutos, con 2 puntos no se llega a 20 min: en la práctica hacen falta **3 lecturas dentro del radio**, unos **30 minutos** (Figura 4.1).

![Figura 4.1](figuras/fig05_deteccion_lugar.png)

*Figura 4.1. Detección de un lugar nuevo con datos sintéticos: un punto cada 15 minutos y un radio de 75 m; la app pregunta en el minuto 60, 30 minutos después de llegar. Script: `figuras/fig05_deteccion_lugar.py`.*

Al guardar un lugar, `reconstruir` rehace las visitas de los **30 días** previos (huecos de hasta **2 horas** no rompen una visita; **solo cuentan visitas de ≥ 10 minutos**). Detectar *estancias* (*stay points*) con un radio y un tiempo mínimo es la técnica estándar de la minería de trayectorias GPS (Zheng, 2015); la app usa una versión simple, sin agrupamiento por densidad. **[Aproximación]**. Falla en interiores (el GPS «salta»), con estancias de menos de 30 minutos y con dos lugares a menos de ~75 m (se funden).

---

## 4.5 Movimiento y «percepción»

**Movimiento (`sensores/Actividad.kt`).** El **reconocimiento de actividad** de Android avisa cuando *empieza* o *termina* «a pie», «corriendo», «en bici», «en vehículo» o «quieto», y la app suma los minutos; parte los tramos en el corte de las **05:00** e **ignora los de más de 20 horas** (probable aviso perdido). Es un clasificador de Android, no validado en esta app. **[Aproximación]**. «Quieto» es ambiguo (el teléfono en la mesa mientras caminas).

**La regla de los minutos (`sensores/Percepcion.kt`).** Cada pulso toma una **muestra** del estado del teléfono y le atribuye **el tiempo transcurrido desde la muestra anterior**:

- Sin muestra previa → **15 min**.
- Si pasaron **más de 2 horas** → solo **15 min** («para no inventar»).
- En otro caso → los minutos reales, **con tope de 60**.

Con muestras a las 10:00, 10:16, 10:31 y 12:45 se cuentan 15 + 16 + 15 + 15 = 61 min, aunque entre 10:31 y 12:45 pasaron 134. La app prefiere **subestimar** a inventar: `wifi_min + sin_wifi_min` (más `wifi_sin_dato_min`, desde 10f) **no suma 1 440**; son **minutos observados**. **[Diseño propio]**.

**Qué se mide en cada muestra:**

- **Wifi (desde 10f, D.112; H-3-7).** Si alguna red activa es wifi según `ConnectivityManager`, cuenta como wifi aunque Android oculte el nombre («wifi (sin nombre)»). Va a `sin_wifi_min` solo si el wifi está apagado o el sistema dice que no hay red wifi; si no se pudo saber, a `wifi_sin_dato_min`. Hasta la v91, «no se pudo saber» contaba como «sin wifi», así que `sin_wifi_min` puede bajar desde 10f. No se probó en un teléfono. **[Diseño propio]**.
- **Audio, música, carga y batería.** La **carga nocturna** (enchufado entre las **20:00 y las 04:00**, **> 3 h y < 16 h**) se guarda como marca aproximada de cuándo dormiste.
- **Luz.** `luz_alta_min` = minutos con **≥ 1 000 lux**. El sensor mide en el bolsillo, no donde están tus ojos, y subestima el aire libre; `aire_libre_min` toma el **máximo** entre la luz alta y el tiempo a pie, corriendo o en bici: un *proxy*, es decir, una medida indirecta (no mide el aire libre, sino la luz). Los 1 000 lux son una regla propia, sin estándar detrás. **[Aproximación]**.
- **Presión.** Se **descartan lecturas fuera de 300–1 100 hPa** (`Percepcion.RANGO_HPA`; hPa = hectopascales, ~1013 a nivel del mar).

**Presión y altitud (H-3-3, resuelto en 10d).** El filtro anterior, 850–1 090 hPa, descartaba **todas** las lecturas por encima de unos **1 450 m**. El rango nuevo es el de medida del sensor BMP280 (Bosch Sensortec, 2021); el LPS22HB mide de 260 a 1 260 hPa (STMicroelectronics, 2016). Qué sensor trae tu teléfono **no se comprobó**. **[Aproximación]**. Del día se guarda también el **cambio de presión en ~3 horas** de mayor valor absoluto, para *poder cruzarlo* con el ánimo o el dolor (la evidencia es mixta). Como el barómetro tiene un **sesgo constante**, `Barometro.ajuste` le suma la **mediana** (el valor del medio al ordenar, que aguanta mejor los valores raros que el promedio) de sus diferencias con Open-Meteo en los **60 días más recientes con ≥ 8 lecturas** (necesita **≥ 3 días**); los *cambios* no se ven afectados. **[Diseño propio]**.

---

## 4.6 Ruido, notificaciones, fotos, agenda y llamadas

**Ruido (`sensores/Ambiente.kt`).** Al registrar algo, puede escuchar el micrófono **1.5 segundos** y **descartar el audio**. Calcula la raíz del promedio de los cuadrados de las muestras (RMS, la intensidad media de la señal) y convierte:

$$\text{dB} = 20 \cdot \log_{10}\left(\frac{\text{RMS}}{32768}\right) + 90$$

Es un **nivel relativo**, no decibeles de presión sonora: la precisión de las apps de sonómetro **varía mucho entre dispositivos y apps** (Kardous y Shaw, 2014). Sirve para comparar tus lugares entre sí. **[Aproximación]**.

**Notificaciones y música (`sensores/Oyente.kt`).** Un servicio del sistema **solo anota** de qué app y a qué hora llega cada notificación; **no lee ni guarda el texto**, y no cuenta actualizaciones de la misma notificación en **menos de 60 segundos**. El mismo servicio anota lo que se reproduce. Contar notificaciones mide **interrupciones externas** (Mohr et al., 2017, sobre el «sensado personal»). **[Aproximación]**. Si el servicio no está activo, esas notificaciones **nunca se cuentan**.

**Fotos (`sensores/Galeria.kt`, `FotosLugar.kt`).** Solo **metadatos**: cuántas fotos y videos hubo ese día lógico; **las imágenes nunca salen del teléfono**. Para el mapa, se lee el lugar que la cámara guarda en el archivo (**EXIF**). Son posiciones aisladas y de fiabilidad variable. **[Diseño propio]**.

**Agenda (`Agenda.kt`)**: eventos y **minutos ocupados** (uniendo los que se enciman), por día de calendario. **Llamadas y SMS (`Comunicacion.kt`)**: solo **conteos** y **personas distintas** (cada número se reduce a sus **últimos 8 dígitos** y se descarta si tiene menos de 6); **los números se usan en memoria y se descartan**. **[Aproximación]**: un número no es una persona.

---

## 4.7 Clima, aire y luna (`sensores/Entorno.kt`, `data/Calculos.kt`)

El clima viene de **Open-Meteo**; solo se envían **coordenadas redondeadas a 0.01°** (~1 km) y fechas, **nunca tu ruta**. `lugarDe` toma la **mediana** de latitud y de longitud de los puntos del día. Se piden temperatura, lluvia, sol, viento, humedad, nubosidad, presión, **PM2.5** y **PM10** (partículas de menos de 2.5 y 10 micrómetros) y calidad del aire (versión completa, §4.4.10).

**Dos fuentes (H-3-4).** Si el día tiene **más de 80 días**, la app pide el **archivo histórico** (reanálisis: el clima pasado reconstruido con modelos y observaciones, con conjuntos como ERA5; Hersbach et al., 2020); si es más reciente, el **pronóstico**. Hasta la v91, un día con **al menos 2 días** quedaba completo y no se volvía a pedir, así que los días sincronizados con entre 2 y 80 días **quedaban para siempre con valores del pronóstico**, sin que constara la fuente. Desde 10f (D.111), cada día guarda su fuente (`EntornoDia.fuente`; en lo de antes se deduce con la misma regla de 80 días), y los días del último año que vinieron del pronóstico se vuelven a pedir al archivo cuando pasan de 80 días. El archivo no trae el **índice UV**: `uv_max` sigue siendo solo del pronóstico (si el día lo tenía, se conserva). Columnas: `fuente` en `vm_entorno.csv` y `clima_reanalisis` en `vm_panel.csv`; los últimos 80 días siguen siendo de pronóstico. **[Aproximación]**. No se probó con la red. Y es el clima de la zona, no el de donde estuviste.

**La luna** se **calcula** (`Luna.fase`): edad = días desde una luna nueva de referencia (6-ene-2000 18:14 UTC) **módulo 29.530588853** (el mes sinódico medio); **iluminación = (1 − cos(2π·fase)) / 2**, al mediodía local. Como no depende de ti, sirve de **control negativo**: si algo «se asocia con la luna», sospecha de azar.

---

## 4.8 El reloj (Health Connect)

La app lee el reloj a través de **Health Connect**, el sistema de salud de Android donde otras apps escriben sus datos; solo pide **permisos de lectura**.

**Sueño (`Salud.noche`).**

1. Lee las sesiones que **terminan** ese día de calendario, desde el mediodía anterior, y toma **una sola fuente por noche** (prefiere la que trae **etapas**), para no sumar la misma noche dos veces.
2. **Une sesiones separadas por menos de 1 hora**. **La noche** es el bloque más largo que **empezó antes del mediodía**; los demás son **siestas** (`siesta_reloj_min`). Lo que marcaste «no cuenta» no entra.
3. **Minutos dormido:** si las etapas cubren **al menos 60 %** de la sesión, se suman las etapas de sueño; si no, la sesión completa menos lo despierto.

*Ejemplo:* una sesión de 23:40 a 07:10 dura 450 min; las etapas suman 400 (≥ 60 % de 450 = 270), así que se usan: profundo 70 + REM 95 + ligero 210 = **375 min**, `sueno_horas = 6.25`. Una corrección **manual** (`sueno_manual`) no la sobrescriben ni la sincronización ni, desde el esquema 14, un respaldo que no la tenía (`Fusion.salud`).

**Confiabilidad.** Contra la polisomnografía (el estándar de laboratorio), siete dispositivos de consumo tuvieron sensibilidad para el sueño ≥ 0.93 pero **especificidad para la vigilia de solo 0.18–0.54**, y tres **sobrestimaron** el sueño total (Chinoy et al., 2021; dispositivos distintos al de la app). **[Validado]** el patrón; **no hay validación del reloj de esta app**. Las **etapas** son lo menos fiable.

| Variable | Cómo se calcula |
|---|---|
| `fc_media`, mínimo, máximo | Agregación de Health Connect sobre el día de calendario |
| `fc_reposo` | Promedio de los registros de reposo del día |
| `fc_sueno`, `spo2_min` | Frecuencia cardiaca media y oxígeno mínimo durante la noche |
| `hrv` (RMSSD) | Promedio de los registros de la noche; sin noche, de 18:00 del día anterior a 14:00 |

El RMSSD es la **raíz cuadrada del promedio de los cuadrados de las diferencias entre intervalos de latidos consecutivos** (en ms): el corazón no late a intervalos iguales, y un valor mayor suele reflejar más actividad parasimpática («descanso y recuperación») (Task Force of the ESC and NASPE, 1996; Shaffer y Ginsberg, 2017). **[Validado]** el significado; **[Aproximación]** el uso: varía mucho entre personas y días, así que se compara con **tu propia línea base**.

Una revisión de dispositivos de consumo encontró **pasos y frecuencia cardiaca razonablemente exactos en laboratorio** (frecuencia cardiaca con error mediano ≈ −1 %; pasos con error medio ≈ −9 % y mediano ≈ −2 %), pero **ninguna marca midió bien el gasto de energía** (Fuller et al., 2020). **[Validado]** para pasos y corazón; de `cal_activas` y `cal_totales` conviene desconfiar. Las demás lecturas se guardan tal como el reloj o la báscula las escriben.

| Dato del reloj | Calidad esperada |
|---|---|
| Pasos, frecuencia cardiaca en reposo | Buena |
| Sueño total | Razonable; tiende a sobrestimar |
| HRV nocturno | Razonable, ruidoso entre noches |
| Etapas del sueño, calorías | Baja |

---

## 4.9 Importaciones: lo que cargas tú

Traen datos que el teléfono no ve, con tres riesgos: llegan **a destiempo**, **parciales** y en **formatos que cambian**.

**Spotify (`data/Spotify.kt`).** (a) La **conexión directa** lee cada **~25–30 min** las **últimas 50** reproducciones; si no se consulta a tiempo, las de más **se pierden**. Como Spotify da la hora en que **terminó** la canción y la app resta la duración completa, una canción parada a la mitad queda con un inicio **demasiado temprano** (**H-3-9**; error de minutos; resuelto por documentación: la app solo recibe la duración de la canción). (b) El **historial descargable** trae cuánto escuchaste de verdad (`ms_played`) y **reemplaza** lo anterior en esos días, lo que corrige ese error. `canciones` cuenta solo reproducciones de **≥ 30 s**.

**Google Takeout y Kindle.** Del historial de YouTube y búsquedas se quitan los anuncios; un «video visto» es un **evento** de abrirlo, **no una duración**. De comentarios **solo se guarda la fecha**. En Kindle, la fecha es **cuándo subrayaste**, no cuándo leíste.

**WhatsApp.** **Solo conteos y tiempos, ningún texto**. Una **conversación nueva** es un mensaje que llega **más de 4 horas** después del anterior; un **tiempo de respuesta** cuenta **si fue en menos de 12 horas**; un chat es **de grupo** con **más de 2 autores**. **Fechas ambiguas (H-3-10, resuelto en 10e, D.105):** si en todo el chat ni el primer ni el segundo número de la fecha pasan de 12, la app prueba día/mes y mes/día y se queda con la lectura en que los mensajes van en orden, como vienen en el archivo (menos retrocesos de fecha); con empate, día/mes (hasta la v90, siempre día/mes). Un chat de un solo día sigue sin poder decidirse. Los avisos de multimedia se reconocen en español, inglés, francés, portugués, italiano y alemán (`Importaciones.MULTIMEDIA`). **[Aproximación]** las medidas; **[Diseño propio]** la regla de las fechas.

**Cold Turkey (laptop).** Minutos por app y sitio, repartidos en productivo, ocio, social y otro con listas de palabras. Desde la 10d (`ColdTurkey.coincide`), en los **sitios** la palabra debe ocupar **partes completas del dominio** («x.com» coincide con `m.x.com`, ya no con `dropbox.com`), lo que corrige **H-3-6**. **[Diseño propio]**.

**MacroFactor (`data/Macrofactor.kt`).** Nutrición, alimentos y series de entrenamiento del `.xlsx`; la hora de la **última cafeína** (desde 10e, `cafeina_ultima_h` del panel sale de las tomas de MacroFactor y de la app, por día lógico; capítulo 5); series efectivas **sin calentamientos**. Lo que registra **depende de lo que anotas**, y su gasto energético es un **algoritmo propio de MacroFactor**, no una medición. El 1RM estimado se explica en el capítulo 5. Otras importaciones, en la versión completa, §4.6.7.

---

## 4.10 Mundo sin conexión, huecos y amenazas a la validez

**El mundo sin conexión (`data/Mundo.kt`).** Para convertir coordenadas en países y ciudades la app no manda tus ubicaciones a internet: trae fronteras de Natural Earth y unas **135 mil localidades de GeoNames**. El país se decide con la regla **par-impar** (*ray casting*, que dice si un punto cae dentro de un contorno); la ciudad es la localidad más cercana a **menos de 3 km** o la principal (≥ 5 mil habitantes) a menos de **30 km**. **En la frontera** el país puede ser el equivocado. **[Aproximación]**.

**Cómo se ve un dato que falta.** Una celda sin dato queda **vacía**, no en 0: los datos de sensores **nunca se rellenan con ceros**. Si **el hueco no es azar** (el reloj se agota justo las noches de poco sueño), el resultado se **sesga** aunque se ignoren las celdas vacías (datos faltantes «no al azar», Rubin, 1976; capítulo 2).

| # | Amenaza | Estado |
|---|---|---|
| L-4.1 | No se guarda el motivo de un hueco | Solo se ve en vivo |
| L-4.2 | Dos criterios de día | Resuelto en parte (H-3-1) |
| L-4.3 | Minutos «observados» por muestreo | Subestiman a propósito |
| L-4.4 | Proxies (luz → aire libre; apps ≠ pantalla encendida) | Sin validar |
| L-4.5 | Instrumentos de consumo | Sin calibración propia |
| L-4.6 | Importaciones tardías y parciales | Recordatorios |
| L-4.7 | Procedencia mixta del clima | Resuelto en 10f (H-3-4): fuente por día y reanálisis a los 80 días; el UV, solo del pronóstico |
| L-4.8 | Presión descartada en altura | Resuelto en 10d (H-3-3) |
| L-4.9 | Medir cambia la conducta | Inevitable en N-de-1 |

---

## 4.11 En resumen

1. Los datos pasivos llegan por un **pulso de ~15 min** y una **sincronización diaria**; los importados, **cuando tú los cargas**. Sin permiso no hay dato, y el motivo del hueco no queda en el histórico.
2. **No todas las fuentes usan el mismo «día»** (H-3-1): desde el esquema 13, pantalla y pasos van por día lógico; el corazón, el oxígeno, la agenda, el clima y las ciudades siguen por calendario, y el diccionario lo marca. Desde 10f, cada instante se lee con la zona horaria que tenía el teléfono entonces (H-1-7).
3. Los minutos de wifi, audio, carga y luz son **estimaciones por muestreo** que **subestiman** a propósito.
4. El **sueño** del reloj está bien cuidado en el cálculo, pero es un dato de consumo: bueno para la duración, débil para la vigilia y las etapas.
5. **Detectar un lugar** necesita unos **30 min** reales, no 20; la distancia es una cota inferior.
6. La 10d corrigió el barómetro (H-3-3), Cold Turkey (H-3-6) y la precisión de los puntos importados; la 10e, las fechas ambiguas de WhatsApp (H-3-10); la 10f, la fuente del clima (H-3-4) y el wifi sin dato (H-3-7), y H-3-5 y H-3-9 quedaron resueltos por documentación. Quedan en parte H-3-1 y H-3-8 (inciso a); ninguno pendiente.
