# Hallazgos metodológicos (registro acumulado)

_Solo se documentan; ningún módulo de contenido modifica el código. Gravedad: Alta / Media / Baja. Los hallazgos se resolverán (o descartarán) en el módulo de revisión general de metodología._

## Módulo 1 · Inventario y arquitectura de datos (commit `fa527df`, v81)

### H-1-1 · Relleno con cero inconsistente entre fuentes similares
- **Dónde:** `ciencia/Estadistica.kt`, `panel()`: `gasto_total`, `n_impulso`, `escalada` se ponen en 0 para *todos* los días con diario; `ingreso_total`, `gimnasio`, `carta_escrita`, `enfoque_*` empiezan en 0 solo desde el primer registro.
- **Qué pasa:** dos funciones análogas (registrar gastos / registrar ingresos) tratan los días anteriores a su adopción de forma distinta: 0 vs. `null`. Además, «desde el primer registro» no detecta abandono.
- **Por qué importa:** los ceros previos a la adopción reducen medias y pueden generar correlaciones espurias con variables que sí empiezan más tarde (sesgo por calendario de adopción).
- **Gravedad:** Alta.
- **Propuesta:** criterio único «cero desde la fecha de primer registro» (o fecha de adopción declarada); opcionalmente marcar periodos de abandono como `null`.

### H-1-2 · Dos criterios de día en el mismo panel
- **Dónde:** `panel()`: `lugares_distintos` agrupa por `v.entrada.take(10)` (fecha calendario); `en_casa_min` usa `Fechas.diaLogicoDeMillis`.
- **Qué pasa:** una visita que empieza entre 00:00 y 04:59 se asigna a días distintos según la variable.
- **Por qué importa:** inconsistencia que desalinea variables de movilidad entre sí y con el sueño/diario.
- **Gravedad:** Media.
- **Propuesta:** usar `diaLogicoDeMillis(Fechas.millis(entrada))` en todas las agregaciones por día (revisar también `reacciones`, `gastos.dia`, `Movilidad.kmPorDia`).

### H-1-3 · Documentos no decodificables se descartan en silencio
- **Dónde:** `data/Repo.kt`, `cargar(col, s)`: `mapNotNull { runCatching { decode }.getOrNull() }`.
- **Qué pasa:** un JSON corrupto o incompatible con el modelo desaparece de la app, del panel y de la exportación sin aviso (sigue en SQLite).
- **Por qué importa:** pérdida silenciosa de observaciones; sesga el análisis si el fallo no es aleatorio.
- **Gravedad:** Media.
- **Propuesta:** contar y mostrar documentos descartados por colección (diagnóstico de integridad) y conservarlos en una lista de «no leídos».

### H-1-4 · `coerceInputValues` convierte nulos o inválidos en valores por omisión
- **Dónde:** `data/Modelo.kt`, `val JSON = Json { coerceInputValues = true … }`.
- **Qué pasa:** un campo `null` o inválido en un campo no nulo se sustituye por su valor por omisión sin registrarlo.
- **Por qué importa:** puede transformar un «sin dato» en un valor aparente (p. ej. 0 o `false`).
- **Gravedad:** Media.
- **Propuesta:** registrar cuántas coerciones ocurren al cargar; para campos analíticos usar tipos anulables.

### H-1-5 · `gasto_imprevisto` es binaria pero su nombre sugiere un monto
- **Dónde:** `panel()`: `r["gasto_imprevisto"] = b(lista.any { it.imprevisto })`.
- **Qué pasa:** la variable es 0/1 (¿hubo algún gasto imprevisto?); está agrupada con `gasto_total` (monto) en `AGREGADOS["gcat_"]`.
- **Por qué importa:** riesgo de interpretar como dinero; mezcla de escalas en comparaciones.
- **Gravedad:** Baja.
- **Propuesta:** renombrar (`hubo_gasto_imprevisto`) y/o añadir `gasto_imprevisto_monto`.

### H-1-6 · Menciones cuentan como «estuve con» por omisión
- **Dónde:** `data/Presencias.kt`, `enDiario()` devuelve `ESTUVE` si no hay estado explícito; `panel()` calcula `personas_vistas`, `vi_*` desde ese estado.
- **Qué pasa:** nombrar a alguien en el diario equivale a haberla visto salvo que se reclasifique; `presencias_ok` solo indica revisión.
- **Por qué importa:** infla las variables de contacto social y la red de personas con menciones que no son presencia.
- **Gravedad:** Media.
- **Propuesta:** excluir (o marcar como incierto) los días con `presencias_ok = false` en las variables `vi_*`/`personas_vistas`, o exponer la variable de revisión como covariable.

### H-1-7 · Reinterpretación de horas con la zona actual del teléfono
- **Dónde:** `data/Fechas.kt`: `zona()` = `ZoneId.systemDefault()`; `horaDe`, `diaLogicoDeMillis`, `diaDeMillis`.
- **Qué pasa:** los instantes antiguos se convierten a la zona vigente, no a la que tenía la persona cuando ocurrieron.
- **Por qué importa:** tras viajes o cambios de horario, la hora de dormir/uso y la asignación al día lógico pueden desplazarse.
- **Gravedad:** Baja (sujeto con poca movilidad entre zonas) / Media (viajes largos).
- **Propuesta:** respetar el desfase guardado en el ISO (`OffsetDateTime`) al calcular hora y día lógico.

### H-1-8 · El diccionario no versiona definiciones por variable
- **Dónde:** `data/Diccionario.kt` / `Exportar.kt` (`vm_diccionario.csv`).
- **Qué pasa:** describe la definición vigente; los cambios históricos de fórmula no quedan registrados (solo `schema_v` global).
- **Por qué importa:** exportaciones de distintas fechas pueden no ser comparables sin que lo advierta el archivo.
- **Gravedad:** Baja.
- **Propuesta:** añadir columna `desde_version` y registro de cambios por variable.

### H-1-9 · Los días importados no reciben sellos de tiempo ni versión
- **Dónde:** `data/Repo.kt`, `importarDias()` (guarda `d` sin pasar por `guardarDia`).
- **Qué pasa:** `ts_creado`, `ts_editado`, `schema_v` y `retroactivo` quedan como vengan en el archivo (o vacíos).
- **Por qué importa:** se pierde la trazabilidad del momento de captura y el indicador de sesgo de recuerdo.
- **Gravedad:** Baja.
- **Propuesta:** conservar los sellos originales si existen y marcar `importado = true` con fecha de importación.

## Módulo 3 · Sensores, datos pasivos e importaciones (v81)

### H-3-1 · Fuentes pasivas con distinto criterio de «día»
- **Dónde:** `sensores/Pantalla.kt`, `Salud.kt` (pasos, sueño aparte), `Agenda.kt`, `Entorno.kt` (clima) usan la ventana de calendario (00:00–24:00); notificaciones, wifi, percepción, ejercicio y la mayoría de lo demás usan el día lógico (corte 05:00).
- **Qué pasa:** dentro de una misma fila del panel, la pantalla incluye la madrugada 00:00–05:00 que las notificaciones y el wifi del mismo día no incluyen (y viceversa).
- **Por qué importa:** desalinea justo las variables de uso nocturno, las más relevantes para el sueño; correlaciones entre fuentes comparan ventanas distintas.
- **Gravedad:** Alta.
- **Propuesta:** unificar a día lógico (o declarar explícitamente el criterio de cada variable en el diccionario). Relacionado con H-1-2.

### H-3-2 · `primer_uso_h` / `ultimo_uso_h` con ventana de calendario
- **Dónde:** `sensores/Pantalla.kt`.
- **Qué pasa:** el «primer uso» de una madrugada de 01:00 aparece como 01:00 y no como la hora real de levantarse; la corrección «+24 si hora < 5» de `ultimo_uso_h` casi nunca actúa porque la ventana termina a las 24:00.
- **Por qué importa:** variable de ritmo circadiano sesgada hacia horas tempranas.
- **Gravedad:** Media.
- **Propuesta:** calcular sobre día lógico.

### H-3-3 · Filtro barométrico 850–1090 hPa descarta lecturas en altura
- **Dónde:** `Barometro.kt` / `sensores/Percepcion.kt`.
- **Qué pasa:** 850 hPa ≈ 1 450 m sobre el nivel del mar (atmósfera estándar); por encima de esa altitud la presión normal es menor y todas las lecturas se descartan.
- **Por qué importa:** variables de presión quedarían vacías sin aviso.
- **Gravedad:** Alta si se vive o viaja a >1 450 m; ninguna a menor altitud (condicional; no conozco la altitud de la persona usuaria).
- **Propuesta:** filtro relativo a la mediana local o límite inferior ~500 hPa.

### H-3-4 · Procedencia mixta del clima (reanálisis vs. pronóstico)
- **Dónde:** `sensores/Entorno.kt`.
- **Qué pasa:** días con >80 días de antigüedad vienen del archivo histórico (reanálisis tipo ERA5); los recientes, de modelos de pronóstico; un día es «completo» con ≥2 días de antigüedad.
- **Por qué importa:** la serie mezcla dos tipos de fuente con sesgos distintos; puede introducir un salto artificial entre periodos.
- **Gravedad:** Media.
- **Propuesta:** registrar la fuente por día y, cuando sea posible, reemplazar pronóstico por reanálisis al estar disponible.

### H-3-5 · Distancia (km) es cota inferior y mezcla fuentes de precisión distinta
- **Dónde:** `ciencia/Estadistica.kt`, `kmPorDia`.
- **Qué pasa:** línea recta entre puntos consecutivos (≤ camino real), se pierden tramos con huecos >2 h, y se mezclan puntos de fotos e importaciones de Maps con GPS.
- **Por qué importa:** subestimación sistemática, variable según densidad de muestreo.
- **Gravedad:** Baja.
- **Propuesta:** documentar como cota inferior; opcionalmente ponderar o filtrar por fuente.

### H-3-6 · Categorías de Cold Turkey por coincidencia de texto («contiene»)
- **Dónde:** `Lote3.kt` (ColdTurkey).
- **Qué pasa:** «x.com» coincide con «dropbox.com», «box.com», «wix.com»; «code» con cualquier sitio que lo contenga.
- **Por qué importa:** `laptop_social_min` y similares quedan inflados por falsos positivos.
- **Gravedad:** Baja.
- **Propuesta:** comparar por dominio exacto o sufijo de dominio.

### H-3-7 · Wifi nulo se cuenta como «sin wifi» (posible, no comprobado)
- **Dónde:** `sensores/Percepcion.kt` (lectura de SSID).
- **Qué pasa:** en versiones recientes de Android el sistema puede devolver datos censurados o nulos aun conectado; ese tiempo se sumaría a `sin_wifi_min`.
- **Por qué importa:** sobreestimaría el tiempo sin wifi.
- **Gravedad:** Baja–Media (no pude verificarlo sin el teléfono).
- **Propuesta:** distinguir «sin permiso/dato» de «sin red»; revisar en el dispositivo.

### H-3-8 · Detalles de almacenamiento de ubicación
- **Dónde:** `sensores/Percepcion.kt`/`Ubicacion.kt` (inserta siempre una muestra por pulso, aunque el comentario dice que solo si cambió algo) y `Importaciones.kt` (puntos de Maps sin precisión).
- **Qué pasa:** (a) la tabla crece un punto por pulso; (b) los puntos importados de Maps no traen precisión y la detección de lugares los trata como precisos.
- **Por qué importa:** (a) solo tamaño; (b) puede crear estancias falsas o inexactas.
- **Gravedad:** Baja.
- **Propuesta:** alinear comentario y código; asignar una precisión por defecto conservadora a puntos sin precisión.

### H-3-9 · Inicio de canción de Spotify subestimado si se interrumpe
- **Dónde:** `Spotify.kt`.
- **Qué pasa:** inicio = fin − duración completa; si se paró a la mitad, el inicio calculado es demasiado temprano.
- **Por qué importa:** error de minutos; afecta solo cerca del corte de las 05:00.
- **Gravedad:** Baja.
- **Propuesta:** documentar; usar `ms_played` del historial extendido cuando exista.

### H-3-10 · Fechas ambiguas (día/mes vs. mes/día) y detección de multimedia solo es/en (WhatsApp)
- **Dónde:** `Importar.kt` (WhatsApp).
- **Qué pasa:** si todos los números son ≤12 asume día/mes; «omitido/omitted/adjunto» solo en español e inglés.
- **Por qué importa:** un export mes/día en chat corto se invierte; en otros idiomas, avisos de multimedia cuentan como palabras.
- **Gravedad:** Baja.
- **Propuesta:** pedir confirmación del formato cuando es ambiguo.

## Módulo 2 · Medición del estado subjetivo (v81)

### H-2-1 · Puntajes de cuestionarios asignados a días de otras semanas
- **Dónde:** `ciencia/Dinamicas.kt`, sección «Cuestionarios» (`ventana = 14` para WHO-5, `30` para PSS-4 y UCLA-3; bucle `for (k in 0 until ventana) pon(Fechas.sumar(r.fecha, -k), …)`), con `pon()` que sobrescribe.
- **Qué pasa:** (a) la ventana no coincide con lo que la app pregunta: WHO-5 y PSS-4 se adaptaron a «los últimos 7 días» (`Escalas.kt`), pero se copian a 14 y 30 días hacia atrás; (b) las respuestas se recorren de la más vieja a la más nueva y cada una sobrescribe, así que cada día recibe la respuesta **más nueva** cuya ventana lo alcanza. Con PSS-4 semanal, un día recibe el puntaje respondido hasta 29 días después; con WHO-5 semanal, cada día (salvo los últimos) recibe el de la semana siguiente a la suya. Ejemplo sintético: respuestas 1, 8, 15, 22 y 29 de marzo con 4, 6, 9, 5, 7 → del 1 al 29 de marzo todos los días quedan con 7 y la semana con 9 desaparece.
- **Por qué importa:** toda correlación entre `esc_*` y variables diarias compara semanas distintas (con el valor «viniendo del futuro»), y la serie se aplana.
- **Gravedad:** Alta.
- **Propuesta:** ventana = periodo realmente preguntado (7 días WHO-5 y PSS-4; 30 UCLA-3) y, si hay solapamiento, que cada día conserve la respuesta más cercana posterior (la primera que lo cubre), no la última.

### H-2-2 · `retroactivo` se decide en el primer guardado del documento del día
- **Dónde:** `data/Repo.kt`, `guardarDia()`: `retro = previo?.retroactivo ?: (fecha < diaLogico())`; `ts_editado` se sobrescribe en cada guardado.
- **Qué pasa:** si el documento del día se crea ese mismo día por cualquier motivo (p. ej., la calidad del sueño desde el widget, un hábito, una recaída), queda `retroactivo = false` para siempre, aunque la valencia y el diario se llenen días después. No queda cuándo se llenó cada campo.
- **Por qué importa:** es la variable pensada para controlar el sesgo de recuerdo; tal como está, lo subestima.
- **Gravedad:** Media.
- **Propuesta:** guardar la fecha/hora en que se llenó la valencia (p. ej., `ts_valencia`) y derivar `retroactivo` de ella.

### H-2-3 · No se guarda cuántos avisos de ánimo se mostraron
- **Dónde:** `avisos/Trabajo.kt`, `avisos()`: el aviso se marca como disparado (`marcar(clave)`) y solo se muestra si `ms - t < 50 min`; `plan_muestreo` se poda a 3 días y `disparos` a 40.
- **Qué pasa:** los avisos que no se mostraron por llegar tarde desaparecen sin rastro, y ningún dato diario guarda el número de avisos mostrados. `n_muestreos` es el numerador sin denominador.
- **Por qué importa:** no se puede calcular la tasa de respuesta ni ver si la falta de respuesta se concentra en ciertos días u horas (dato que puede ser no aleatorio).
- **Gravedad:** Media.
- **Propuesta:** registrar por día los avisos planeados, mostrados y respondidos (p. ej., en una colección pequeña o en el documento del día) y exportarlos.

### H-2-4 · «No respondí» codificado como 0 en varias variables subjetivas
- **Dónde:** `ciencia/Estadistica.kt`, `panel()`: `sueno_recordado = b(d.sueno_recordado)`, `ctx_* = b(...)`, `hab_* = b(...)` en todos los días con documento; codificación de `sdt_necesidad` (`las_tres` deja `autonomia`, `competencia`, `relacion` en 0) y de flujo (`no_aplica` deja todas en 0).
- **Qué pasa:** «no marqué», «no pasó» y (para hábitos) «aún no existía el hábito» quedan iguales; el modelo `Habito` tiene `desde` y no se usa. Elegir «las tres» necesidades pone 0 en cada una.
- **Por qué importa:** subestima frecuencias (recuerdo de sueños, contextos, hábitos) en proporción a los días sin respuesta y sesga análisis por necesidad. Mismo problema de fondo que H-1-1.
- **Gravedad:** Media.
- **Propuesta:** vacío (`null`) cuando la sección no se respondió (`sueno_recordado` solo si se abrió la pantalla de sueño o hay calidad; `hab_*` desde `Habito.desde`); con «las tres», poner 1 en las tres columnas.

### H-2-5 · El perfil actual se proyecta hacia atrás en las etapas de vida
- **Dónde:** `ciencia/Dinamicas.kt`, «Etapas de vida», `valorEn()`: si un campo nunca cambió, se usa `actual[campo]` para cualquier fecha.
- **Qué pasa:** al llenar «Conocerte» por primera vez, todos los días anteriores (incluida la historia importada) quedan con la situación actual (p. ej., «en pareja»).
- **Por qué importa:** contamina las comparaciones «antes y después» de etapas de vida sin que se note.
- **Gravedad:** Media.
- **Propuesta:** no asignar etapa antes de la primera fecha en que se respondió ese campo, o pedir «desde cuándo».

### H-2-6 · Brújula política: 5 afirmaciones por eje no neutralizan la aquiescencia
- **Dónde:** `data/Politica.kt` (`AFIRMACIONES`, `ejes()`, comentario de la cabecera).
- **Qué pasa:** cada eje tiene reparto 3/2 de signos; responder 5 a todo da ±20 en los cinco ejes, que `lado()` describe como «hacia…». El comentario afirma que estar de acuerdo con todo no empuja hacia ningún lado.
- **Por qué importa:** un estilo de respuesta se lee como postura; el sesgo es constante entre años, así que afecta poco a la comparación contigo mismo.
- **Gravedad:** Baja.
- **Propuesta:** ajustar por la media de acuerdo de la persona (restar su aquiescencia) o usar un número par de afirmaciones por eje; corregir el comentario.

### H-2-7 · La separación mínima entre avisos no siempre es de 90 minutos
- **Dónde:** `avisos/Trabajo.kt`, `Programador.planDelDia` (margen = `minOf(45 min, tramo/4)`; comentario «separadas al menos 90 minutos»).
- **Qué pasa:** la separación mínima es 2 × margen; solo es ≥ 90 min si cada tramo dura ≥ 3 h. Con ventana 10–21: 4 avisos → 82.5 min; 6 avisos → 55 min.
- **Por qué importa:** solo documentación; avisos muy juntos aumentan la carga y la dependencia entre respuestas.
- **Gravedad:** Baja.
- **Propuesta:** corregir el comentario, o fijar una separación mínima real.

### H-2-8 · El cierre de un día pasado se guarda con la hora actual
- **Dónde:** `ui/pantallas/Lote2.kt`, `BloqueCierre(fecha)`: `Momento(…, ts = ahoraIso(), fecha = fecha, origen = "cierre")`.
- **Qué pasa:** llenar la hoja de un día anterior crea un «cómo estás ahora» de hoy asignado a ese día; `animo_cierre_valencia` de ese día deja de ser del cierre de ese día. El comentario habla de «la misma hora todos los días», pero es la hora a la que se cierra.
- **Por qué importa:** contamina una variable pensada como comparable entre días.
- **Gravedad:** Baja–Media.
- **Propuesta:** mostrar el cierre solo si `fecha` es el día lógico actual (o marcar los cierres tardíos).

### H-2-9 · Editar un registro de ánimo no deja rastro
- **Dónde:** `ui/pantallas/Momentos.kt`, `DialogoMomento`; modelo `Momento` sin sello de edición.
- **Qué pasa:** la valencia, la activación o las emociones de un registro momentáneo se pueden cambiar después sin que quede cuándo.
- **Por qué importa:** un dato momentáneo puede volverse retrospectivo sin saberlo.
- **Gravedad:** Baja.
- **Propuesta:** añadir `ts_editado` a `Momento`.

### H-2-10 · `error_prediccion` es absoluto y pierde la dirección
- **Dónde:** `ciencia/Estadistica.kt`, `panel()`: `abs(p - valencia)`.
- **Qué pasa:** optimismo y pesimismo del mismo tamaño dan el mismo valor.
- **Por qué importa:** no permite estudiar el sesgo de pronóstico afectivo (en qué dirección te equivocas).
- **Gravedad:** Baja.
- **Propuesta:** añadir `error_prediccion_signo = valencia − predicción`.

### H-2-11 · Elementos de la interfaz que pueden influir en la respuesta del día
- **Dónde:** `ui/pantallas/Dia.kt`, `PantallaNoche` (línea «avisos: hh:mm→v» junto a la escala de valencia del día); `ui/Componentes.kt`, `Escala` (activación con `Tonos.divergente`, la misma paleta que la valencia).
- **Qué pasa:** se ven los valores de los avisos al calificar el día (posible anclaje), y la activación se colorea como si bajo fuera «malo».
- **Por qué importa:** reduce la independencia entre `valencia` y `muestreo_valencia`; posible sesgo leve en la activación.
- **Gravedad:** Baja.
- **Propuesta:** solo documentar, o mostrar los avisos después de elegir la valencia y usar una paleta secuencial para la activación.

### H-2-12 · Prueba de reacción: diferencias con la PVT
- **Dónde:** `ui/pantallas/Medir.kt`, `PantallaReaccion`.
- **Qué pasa:** 10 intentos (~30 s), esperas de 1.5–4.0 s, lapso ≥ 500 ms (umbral de la PVT de 10 min; para la PVT-B de 3 min se propuso 355 ms); respuestas de menos de 100 ms no se marcan como falsas salidas y entran en la media; tiempos medidos en pantalla táctil.
- **Por qué importa:** mucho ruido entre días; un toque reflejo muy rápido baja la media.
- **Gravedad:** Baja.
- **Propuesta:** contar las respuestas < 100 ms como falsas salidas (excluirlas de la media); documentar que no es una PVT validada.

### H-2-13 · Exportación `vm_dias.csv`: columnas de momentos congeladas y nombres distintos al panel
- **Dónde:** `data/Exportar.kt` (`m_<tipo>` con `Catalogo.MOMENTOS`; `valencia_muestreo`, `n_muestreos`) frente a `Estadistica.panel()` (`muestreo_valencia`, `n_<emoción>`).
- **Qué pasa:** desde el registro de ánimo nuevo (tipo `animo` con lista de emociones), las columnas `m_tristeza`, `m_alegria`… solo cuentan registros viejos y valen 0 en adelante; la misma variable se llama `valencia_muestreo` en `vm_dias` y `muestreo_valencia` en el panel.
- **Por qué importa:** quiebre de serie en R si se usan las `m_*`; confusión de nombres.
- **Gravedad:** Baja.
- **Propuesta:** calcular `m_*` con `Catalogo.emocionesDe`/`pensamientoDe` (o retirarlas) y unificar nombres (revisar en el módulo 5).

### H-2-14 · El aviso de «escrito por adelantado» no se registra
- **Dónde:** `ui/pantallas/Dia.kt` («se marcará como escrito por adelantado»); `Dia` no tiene campo para ello.
- **Qué pasa:** la app promete una marca que no existe.
- **Por qué importa:** notas de días futuros no se distinguen de las escritas ese día.
- **Gravedad:** Baja.
- **Propuesta:** añadir la marca o cambiar el texto.

### H-2-15 · Ánimo por lugar: criterios distintos y mezcla de tipos de registro
- **Dónde:** `ui/pantallas/Medir.kt` (`PantallaLugares`: la lista usa `m.lugar == id` **o** distancia ≤ radio; `Mapa` solo `m.lugar == id`; ambos usan todos los registros de ánimo); `vestigium.R`, `vm_mapa_animo` (`coalesce(valencia, v_dia)`).
- **Qué pasa:** el mapa y la lista de la misma pantalla pueden dar valencias distintas para un lugar; se mezclan avisos al azar con registros por iniciativa propia; en R, los registros sin valencia reciben la valencia del diario del día.
- **Por qué importa:** mezcla ánimo momentáneo con recordado y muestreo al azar con muestreo por evento.
- **Gravedad:** Baja.
- **Propuesta:** un solo criterio de pertenencia al lugar; preferir solo avisos (`muestreo`) o mostrarlos separados; en R, no rellenar con la valencia del día.

## Módulo 5 · Núcleo estadístico (v81, commit `fa527df`)

_Pistas preliminares del plan: la 5 (Pearson y Spearman mezclados) se **confirma** (H-5-7). Las pistas 1 y 2 (experimentos) dependen de `Estadistica.diferencia`; aquí se registra el problema general de esa función (H-5-5) y el módulo 7 lo desarrolla para los experimentos._

### H-5-1 · R analiza otra tabla: `vm_panel.csv` no es el panel de la app
- Dónde: `data/Exportar.kt`, `filasPanel()` frente a `ciencia/Estadistica.kt`, `panel()`; `vestigium.R`, `vm_relaciones`, `vm_factores`, `vm_atipicos`, `vm_modelo` (todas usan `panel` = `vm_panel.csv`).
- Qué pasa: la misma variable tiene otro nombre (`sueno_min`/`sueno_horas`, `km`/`distancia_km`, `hrv_rmssd`/`hrv`, `valencia_muestreo`/`muestreo_valencia`, `luz_dia_min`/`luz_horas`, `carga_noche_inicio_h`/`carga_noche_h`, `temp_piel_delta`/`temp_piel`, `ruido_db_medio`/`ruido_db`); otras reglas de ceros (`gasto_total` e `ingreso_total` = 0 en cualquier fecha exportada; `escalada` y `gimnasio` = falso en cualquier fecha; `n_momentos`/`m_*` incluyen momentos privados); otras filas; y faltan muchas variables (dinámicas, contextos, hábitos, lectura del día, enfoque…), que están en `vm_panel_extendido.csv` pero el script no lo carga.
- Por qué importa: `vm_relaciones()` aplica el mismo algoritmo que «Las relaciones más marcadas» pero a otra tabla; no se puede usar R para verificar lo que muestra la app, y las diferencias de ceros cambian medias y correlaciones.
- Gravedad: alta.
- Propuesta: exportar el panel de la app (`Estadistica.panel()` con los nombres de `variables()`) como tabla de análisis (por ejemplo, que `vm_panel_extendido.csv` sea exactamente ese panel) y que el script de R lo lea por omisión; o, como mínimo, unificar nombres y reglas de ceros entre `filasPanel()` y `panel()`.

### H-5-2 · La lista de relaciones «obvias» de R es mucho más corta que la de la app
- Dónde: `vestigium.R`, `vm_familias` (19 conjuntos) y `vm_obvia`; `ciencia/Estadistica.kt`, `FAMILIAS` (56 conjuntos) y `obvia()` (reglas por grupo, lugares, suplementos, prefijos dinámicos `AGREGADOS`/`ENTRE_SI`, misma persona, mismo cuestionario).
- Qué pasa: en R se prueban y pueden salir como «firmes» parejas que la app esconde como triviales (p. ej., `valencia` con `animo_valencia` o `animo_cierre_valencia`; `cercanos` con `vi_familia`; `alcohol` con `alcohol_bebidas`; `cafeina_mg` con `cafeina_total_mg`; `n_<emoción>` con `registros_animo`), y la corrección BH se hace sobre otro número de pruebas.
- Por qué importa: la lista de R se llena de trivialidades y no coincide con la de la app.
- Gravedad: media.
- Propuesta: exportar las familias de la app (por ejemplo, `vm_familias.csv` generado desde `FAMILIAS` y las reglas de `obvia`) y que `vm_obvia` lo lea; o una columna `obvia` precalculada para cada pareja.

### H-5-3 · «Todo lo que se mueve con algo» no quita tendencia ni descuenta la autocorrelación
- Dónde: `ciencia/Analisis.kt`, `factores()`; `ui/pantallas/Analisis.kt`, `Factores`; mismo patrón en `vestigium.R`, `vm_factores`.
- Qué pasa: compara valores tal cual con n bruto (Spearman con EE = 1/√(n − 3); diferencia de medias con días independientes) y luego aplica BH. Como tocar una tarjeta de «Las relaciones más marcadas» abre esta pantalla, la misma pareja puede verse punteada («podría ser azar») en la lista y con ✓ (q < 0.1) aquí, o con otro signo si la relación era de tendencia.
- Por qué importa: con variables persistentes los p son mucho más pequeños de lo que deberían (en la simulación de la Figura 6.3, con autocorrelación 0.8 la tasa de falsas alarmas sube de 5 % a 35 %), y BH no lo compensa; el usuario recibe ✓ en relaciones espurias.
- Gravedad: alta.
- Propuesta: reutilizar en `factores` la maquinaria de `relaciones` (restos sin tendencia según el interruptor, nEf y p con nEf); para 0/1, una diferencia de medias con EE inflado por (1 + r₁)/(1 − r₁) o calculada sobre los restos. Alternativa mínima: mostrar el mismo r, IC y q que la lista para la pareja de origen.

### H-5-4 · «Cruzar dos variables» usa n bruto y valores con tendencia
- Dónde: `ui/pantallas/Explorar.kt`, sección «Cruzar dos variables» (`Estadistica.pares`, `pearson`, `ic95`).
- Qué pasa: el intervalo y la frase «no incluye el cero» se calculan con n sin descontar la autocorrelación y sin quitar tendencia.
- Por qué importa: puede afirmar que una relación «no incluye el cero» cuando la lista la marca como posible azar; contradicción visible entre secciones de la misma pantalla.
- Gravedad: media.
- Propuesta: calcular el intervalo con nEf (autocorrelación de cada variable) y ofrecer el mismo interruptor «Quitar tendencias»; o al menos mostrar «≈ nEf independientes» y el r sin tendencia junto al bruto.

### H-5-5 · `Estadistica.diferencia`: 1.96 fijo y días independientes
- Dónde: `ciencia/Estadistica.kt`, `diferencia()`; la usan `Analisis.cambio` (Tendencia: 30 contra 30 días), `Analisis.factores` (variables 0/1), `EtapasDeVida` en `ui/pantallas/Explorar.kt` (60 contra 60 días) y `Experimentos` (`data/Lote3.kt`). Lo mismo en `Analisis.porDiaSemana` (media ± 1.96 DE/√n con n ≥ 2).
- Qué pasa: el intervalo usa el cuantil de la normal (1.96) en vez del de t (con 3 a 5 valores por grupo, t ≈ 2.8–4.3; con 2 valores en «Día de la semana», 12.7) y trata cada día como independiente aunque los días de un mismo periodo se parecen.
- Por qué importa: intervalos demasiado estrechos; «El cambio es claro» o «la diferencia parece real» con más frecuencia de la debida.
- Gravedad: media.
- Propuesta: usar t con los grados de libertad de Welch; inflar el EE por la autocorrelación (o usar nEf por grupo); subir el mínimo de `porDiaSemana` (p. ej., 5 por día). Para experimentos, ver módulo 7.

### H-5-6 · Quitar la tendencia no quita el ciclo semanal
- Dónde: `ciencia/Estadistica.kt`, `series()` (ventana ±10 días = 21 días = 3 semanas exactas); igual en `vm_relaciones`.
- Qué pasa: el promedio de 21 días elimina el patrón semanal del promedio y lo deja entero en los restos. Dos variables que cambian con el fin de semana quedan correlacionadas sin tendencia.
- Por qué importa: el día de la semana es un confusor común (sueño, ánimo, pantalla, gasto, alcohol); relaciones «firmes» pueden deberse solo al calendario.
- Gravedad: media.
- Propuesta: opción de restar también la media de cada día de la semana (calculada sobre los restos) antes de correlacionar, o documentarlo en la pantalla; en R, sugerir `factor(dia_semana)` en los modelos.

### H-5-7 · Pearson y Spearman mezclados entre pantallas y con R (pista 5 confirmada)
- Dónde: `Estadistica.relaciones` (Pearson sobre restos), `Analisis.factores` (Spearman), Explorar «Cruzar dos variables» (muestra ambos, razona con Pearson), `vestigium.R`: `vm_cruce`, `vm_factores`, `vm_cruce_horas`, `vm_presion` (Spearman por omisión), `vm_relaciones` (Pearson).
- Qué pasa: la misma pareja muestra números distintos según dónde se mire, sin explicación.
- Por qué importa: confusión; parece que los análisis se contradicen.
- Gravedad: baja.
- Propuesta: un solo coeficiente principal por tipo de análisis, nombrado siempre en pantalla; `vm_cruce` con `metodo = "pearson"` por omisión o mostrar ambos como la app.

### H-5-8 · Días atípicos: la variable `km` no existe en el panel de la app
- Dónde: `ciencia/Analisis.kt`, `atipicos()`, lista `ids`.
- Qué pasa: busca `km`, pero la variable del panel se llama `distancia_km`; la distancia nunca se revisa. Además, la mediana y la MAD toman el central superior cuando n es par (R promedia).
- Por qué importa: se pierden días atípicos de movilidad (viajes); pequeñas diferencias de z con R.
- Gravedad: baja.
- Propuesta: cambiar `km` por `distancia_km`; opcionalmente promediar los dos centrales.

### H-5-9 · «Tus números»: ejercicio semanal sobreestimado y aire libre con otra variable
- Dónde: `ciencia/TusNumeros.kt`, `construir()`: `ejercicio_sem` = media de `ejercicio_min` en los últimos 30 días con dato × 7; `aire_libre` usa `luz_alta_min`.
- Qué pasa: `ejercicio_min` solo tiene valor los días con sesión del reloj, así que se promedian solo días con ejercicio y se multiplica por 7 (tres sesiones de 45 min por semana dan 315 min/semana en lugar de 135). «Aire libre» usa solo la luz del teléfono y no `aire_libre_min` (máximo de luz y movimiento).
- Por qué importa: la comparación con las recomendaciones de actividad física (150 min/semana) sale engañosamente favorable.
- Gravedad: media.
- Propuesta: calcular el ejercicio semanal como suma de minutos en los últimos 28 días con cobertura de reloj / 4 (o rellenar con 0 los días con datos del reloj y sin sesión); usar `aire_libre_min`.

### H-5-10 · Palabras del diario sin incertidumbre y distintas en R
- Dónde: `ciencia/Analisis.kt`, `palabras()`; `vestigium.R`, `vm_palabras`.
- Qué pasa: se ordenan cientos de palabras por la diferencia de medias con mínimo 5 días, sin intervalo ni corrección; las de los extremos son en parte azar (regresión a la media). R usa otra lista de vacías (107 frente a 167, con y sin acentos) y otra media de referencia (todos los días frente a días con nota).
- Por qué importa: listas llamativas pero poco estables; app y R no coinciden.
- Gravedad: baja.
- Propuesta: mostrar n y un intervalo, o encoger la diferencia hacia 0 según n (p. ej., dif · n/(n + k)); unificar lista de vacías y referencia en R.

### H-5-11 · Modelo múltiple: inferencia y etiqueta
- Dónde: `ciencia/Analisis.kt`, `modelo()`; `ui/pantallas/Analisis.kt`, `ModeloUI`.
- Qué pasa: intervalos con 1.96 en vez de t(n − k − 1); R² sin ajustar; mínimo n ≥ k + 10; el interruptor dice «Controlar por el ánimo del día anterior» pero agrega el objetivo de ayer, sea cual sea.
- Por qué importa: con pocos días los intervalos son estrechos y el R² optimista; texto inexacto cuando el objetivo no es el ánimo.
- Gravedad: baja.
- Propuesta: usar t y mostrar R² ajustado; texto «Controlar por el valor del día anterior».

### H-5-12 · Dinámica emocional: dos definiciones de «difícil»
- Dónde: `ciencia/Analisis.kt`, `dinamica()`.
- Qué pasa: «% días difíciles» usa valencia ≤ 2 y «racha dura» usa ≤ 3. En R, `vm_dinamica` por mes empareja el último día de un mes con el primero del siguiente y calcula inercia mensual; la app no.
- Por qué importa: inconsistencia de definiciones en la misma tarjeta; pequeñas diferencias con R.
- Gravedad: baja.
- Propuesta: un solo umbral (o nombrarlos distinto: «muy difíciles» ≤ 2); documentar la diferencia mensual con R.

### H-5-13 · Límites de la corrección por autocorrelación
- Dónde: `ciencia/Estadistica.kt`, `autocorr()` y `relaciones()`.
- Qué pasa: con menos de 10 pares consecutivos la autocorrelación vale 0 (sin corrección: variables esporádicas y las primeras ~11 semanas del modo semanal); la autocorrelación se estima sobre toda la serie de cada variable, no sobre los días compartidos; la fórmula es de primer orden (AR(1)).
- Por qué importa: en esos casos nEf = n y la relación parece más segura de lo que es.
- Gravedad: baja.
- Propuesta: solo documentar; opcionalmente usar un valor prudente (p. ej., 0.3) cuando no se puede estimar.

### H-5-14 · Las variables de etapa, cuestionario y política casi nunca entran «sin tendencia»
- Dónde: `ciencia/Estadistica.kt`, `series()` (criterio de lenta var(resto)/var < 0.12) aplicado a `vida_*`, `pol_*`, `esc_*` de `Dinamicas.kt`.
- Qué pasa: al ser escalones que cambian pocas veces, se clasifican como lentas y se omiten con el interruptor encendido; con el interruptor apagado entran, pero expuestas a tendencias.
- Por qué importa: la promesa «cruzar todo con todo» no se cumple para estas variables en el modo recomendado; el usuario puede no notarlo (la pantalla las nombra solo en el texto de lentas).
- Gravedad: baja.
- Propuesta: solo documentar, o remitir desde la lista a «Antes y después» y al análisis en R para estas variables.

### H-5-15 · Reportes y Totales: fechas y horas con criterios distintos al panel
- Dónde: `ciencia/Periodo.kt` (`horaDec` suma 24 a las horas < 15; lee la hora del texto ISO; ingresos por `fecha`, gastos por `dia`; `valDe` cae a los registros de ánimo si no hay diario); `ciencia/Totales.kt` (gastos e ingresos por `fecha` de calendario).
- Qué pasa: la hora media de dormir del reporte usa otro corte (15 h) que el resto de la app (12 h); un ingreso de madrugada cae en otro día que su gasto; la valencia del reporte mezcla diario y registros momentáneos.
- Por qué importa: pequeñas incoherencias entre reportes, panel y R (relacionado con H-1-2).
- Gravedad: baja.
- Propuesta: usar `Exportar.horaDecimal` y `dia` en todas partes; indicar cuando la valencia de un día viene de registros momentáneos.

### H-5-16 · El aviso de potencia cuenta días con diario, no días por pareja
- Dónde: `ui/pantallas/Explorar.kt` («Con 90 días o más ya se pueden detectar relaciones moderadas» / «Faltan N días…», con `Repo.dias.size`).
- Qué pasa: 90 días bastan para |r| ≈ 0.29 en **una** prueba con días independientes; con autocorrelación y miles de comparaciones hace falta |r| ≳ 0.5. Y el conteo es de días con diario, no de días con ambos datos.
- Por qué importa: expectativa optimista sobre lo que la lista puede encontrar.
- Gravedad: baja.
- Propuesta: matizar el texto («relaciones moderadas en una pareja concreta; en la lista completa, solo las fuertes») o basarlo en la cobertura de cada variable.

## Módulo 6 · Personas y red social (v81, commit `fa527df`)

### H-6-1 · Momentos privados e impulsos en funciones de personas
- **Dónde:** `ciencia/Dinamicas.kt`, `construir()` (familia `per_`: `Repo.momentos.forEach { texto(...) }`, sin filtro); `data/Personas.kt`, `menciones()` (todos los momentos); `data/Modelo.kt`, `Momento.privado` = solo `recaida` o `libido`.
- **Qué pasa:** las @menciones de momentos de recaída y libido entran en `per_*` (panel, análisis y exportación) y en «Tu gente» (días mencionada, «¿Quién es @x?», duplicados), sin importar si el seguimiento privado está activo. Los momentos de tipo `impulso` no cuentan como privados para el código y entran además en presencias, Constelación, Red y `vm_red_*`. La Constelación y la Red sí excluyen recaída y libido con el mismo criterio (la pista 4 del plan no se confirma en su forma original).
- **Por qué importa:** contenido del seguimiento privado aparece en pantallas y exportaciones donde el resto de la app lo oculta; criterio (c) del módulo 9.
- **Gravedad:** Media.
- **Propuesta:** filtrar `!it.privado` en `Dinamicas.construir` y `Personas.menciones`; decidir explícitamente si `impulso` debe formar parte de `Momento.privado`.

### H-6-2 · No hay variable por persona de «estuve con @x»; `per_` mezcla cuatro cosas
- **Dónde:** `ciencia/Dinamicas.kt`, familias `per_` y `pen_`.
- **Qué pasa:** `per_<x>` vale 1 si la persona aparece en el diario, un sueño, el mejor/peor momento, cualquier momento o una carta. Existe `pen_<x>` (pensé sin verla), pero no `vi_<x>`/`estuve_<x>`.
- **Por qué importa:** una correlación con `per_ana` no distingue haberla visto de haberla soñado o pensado; el constructo es «presencia mental o física», no contacto.
- **Gravedad:** Media.
- **Propuesta:** agregar una familia por persona con estado «estuve» (desde `Presencias.delDia`) o documentar `per_` como «mención en cualquier texto» en el diccionario.

### H-6-3 · La fecha de un cambio de relación es la fecha de edición, y lo anterior a la primera clasificación se proyecta
- **Dónde:** `data/Personas.kt`, `guardar()` (`CambioRelacion(hoy, …)`) y `relacionEn()`/`cercaniaEn()`.
- **Qué pasa:** no se puede indicar cuándo cambió de verdad la relación; los días entre el cambio real y la anotación cuentan con la relación anterior. Para días previos a la primera clasificación se usa la relación más antigua del historial (o la actual).
- **Por qué importa:** `vi_pareja`/`vi_expareja` y `penso_en_*` quedan mal asignados alrededor de los cambios, que suelen ser justo los periodos más interesantes.
- **Gravedad:** Media.
- **Propuesta:** permitir elegir la fecha del cambio (por omisión, hoy) y, opcionalmente, una fecha «desde» real de la relación.

### H-6-4 · Conocerte sin historial y sin equivalencias
- **Dónde:** `data/Perfiles.kt`, `relacion()` (`p.cercanas.firstOrNull { it.nombre == persona }`, `persona in p.convivientes`); `ciencia/Estadistica.kt`, `panel()` (`vi_convivientes` con `perfil.convivientes` actual).
- **Qué pasa:** «a quién ves más» y «quiénes viven contigo» se aplican con su valor actual a todas las fechas, y se comparan con el nombre canónico sin aplicar equivalencias ni quitar @/mayúsculas.
- **Por qué importa:** `vi_convivientes` y los `vi_*` de quien solo está en Conocerte se proyectan hacia atrás (como H-2-5) y pueden fallar si el nombre de Conocerte es un alias.
- **Gravedad:** Baja.
- **Propuesta:** comparar con `Texto.canon`; usar el historial de cambios del perfil (`CambioPerfil`) para fechar convivientes, o mover a esas personas a «Tu gente».

### H-6-5 · Colores de la Red con la relación sin agrupar
- **Dónde:** `data/Lote3.kt`, `RedPersonas.calcular()` (`p?.relacion ?: Perfiles.relacion(n)`); `ui/pantallas/Lote3.kt`, `PantallaRed` (`tonos`).
- **Qué pasa:** para quien está en Tu gente se usa la relación tal cual (`amistad_cercana`, `colega`, `companero`, `mentor`), que no está en la tabla de colores; esas personas salen sin color de relación. La Constelación sí agrupa. Lo mismo llega a `vm_red_nodos.csv` y a `composicion` en R.
- **Por qué importa:** el dibujo engaña (una amistad cercana parece «sin clasificar») y la composición en R separa categorías que los análisis juntan.
- **Gravedad:** Baja.
- **Propuesta:** usar `Personas.grupo(...)` para el color (y exportar ambas columnas: relación y grupo).

### H-6-6 · «Solo lo que dije» cuenta grupos de más en la Red
- **Dónde:** `ui/pantallas/Lote3.kt`, `PantallaRed` (filtra aristas pero no recalcula `grado`); `data/Lote3.kt`, `componentes()` (`nodos.filter { it.grado > 0 }`).
- **Qué pasa:** con el filtro activo, quien solo tenía líneas derivadas conserva `grado > 0` y cuenta como un grupo de una persona; la ficha «unida a N» muestra el grado con todas las líneas.
- **Por qué importa:** la cifra «grupos» y el grado mostrados no corresponden al dibujo.
- **Gravedad:** Baja.
- **Propuesta:** recalcular el grado con las aristas filtradas antes de `componentes()`.

### H-6-7 · La app y `vm_red()` calculan la red distinto
- **Dónde:** `data/Exportar.kt`, `filasRed()` (`minJuntos = 1, max = 400`); `assets/vestigium.R`, `vm_red()`; `PantallaRed`.
- **Qué pasa:** (a) R incluye como línea un par declarado «no se conocen» si aparece junto ≥ `min_juntos` días; la app lo excluye. (b) `components(g)$no` cuenta a cada persona sin líneas como componente; la app no. (c) La app usa 45 personas; la exportación hasta 400, sin mínimo de días. (d) El `grado` del CSV usa co-mención ≥ 1 día; la app, ≥ 2.
- **Por qué importa:** densidad, grupos y grado difieren entre app y R para los mismos datos; criterio (b) del módulo 9.
- **Gravedad:** Media.
- **Propuesta:** en R, excluir `tipo_declarado == "no_se_conocen"` también en la rama derivada y contar componentes sin aislados (o mostrar ambos); exportar el grado con `minJuntos = 2` o documentarlo.

### H-6-8 · Densidad y restricción de Burt dominadas por personas mencionadas pocas veces
- **Dónde:** `assets/vestigium.R`, `vm_red()` (vértices = todos los nodos exportados; ego unido a todos); `PantallaRed` (densidad con todos los nodos).
- **Qué pasa:** con el ego unido a todas, la restricción mínima es 1/*n* y cada persona sin líneas la baja; la densidad cae con cada nodo aislado. Con hasta 400 nodos sin mínimo de días, una persona mencionada una vez pesa igual que tu familia.
- **Por qué importa:** las métricas cambian con cuánto escribes, no con la estructura de tu red; comparar periodos o versiones puede llevar a conclusiones falsas.
- **Gravedad:** Baja.
- **Propuesta:** argumento `min_dias` en `vm_red()` (p. ej., 3) y documentarlo; solo documentar en la app.

### H-6-9 · Cada función usa fuentes distintas para «quién aparece»
- **Dónde:** `Personas.menciones`, `Estadistica.panel` (`personas`), `Presencias.delDia`, `Dinamicas` (`per_`), `Constelacion.calcular`, `RedPersonas.calcular`, tarjetas de `Explorar.kt` y `Vida.kt`.
- **Qué pasa:** unas leen solo el diario, otras diario y momentos, otras también sueños, mejor/peor momento, notas del cronómetro o cartas; la tarjeta «personas en tu cielo este año» ni siquiera aplica equivalencias (tabla 7.2 del capítulo 7).
- **Por qué importa:** los conteos de una misma persona no cuadran entre pantallas y variables; `personas` (solo diario) y `per_` (todo) miden cosas distintas con nombres parecidos.
- **Gravedad:** Baja.
- **Propuesta:** documentar en el diccionario la fuente de cada variable; aplicar `Texto.canon` en las tarjetas.

### H-6-10 · Pares por construcción no marcados como obvios
- **Dónde:** `ciencia/Dinamicas.kt`, `AGREGADOS["per_"]`.
- **Qué pasa:** no incluye `personas_vistas`, `personas_mensajes`, `vi_expareja`, `vi_trabajo` ni `cercania_media`, que dependen de las mismas menciones.
- **Por qué importa:** pueden aparecer «relaciones» triviales (p. ej., `per_ana` con `personas_vistas`) que ocupan lugar en la lista y en la corrección por comparaciones múltiples.
- **Gravedad:** Baja.
- **Propuesta:** agregar esas variables a `AGREGADOS["per_"]` (y `personas_pensadas` ya está en `pen_`).

### H-6-11 · Equivalencias de un solo paso y compartidas entre @ y #
- **Dónde:** `data/Texto.kt`, `canon()` (`eq[t] ?: t`); `Config.equivalencias` usada para personas y temas.
- **Qué pasa:** `a → b` y `b → c` dejan `a` como `b`; una equivalencia creada para `@x` también afecta `#x`.
- **Por qué importa:** una persona puede quedar partida en dos tras unir nombres en cadena; colisión improbable entre personas y temas.
- **Gravedad:** Baja.
- **Propuesta:** resolver la cadena al aplicar (o al guardar) y separar las tablas por símbolo.

### H-6-12 · La cercanía no se puede borrar y la primera se proyecta hacia atrás
- **Dónde:** `data/Personas.kt`, `guardar()` (`cercania ?: previo.cercania`; cambio solo si ambas cercanías existen).
- **Qué pasa:** una cercanía puesta no se puede quitar; poner cercanía a alguien que no tenía no crea historial y vale para todos los días pasados.
- **Por qué importa:** `cercania_media` de días antiguos usa un juicio de hoy.
- **Gravedad:** Baja.
- **Propuesta:** registrar en el historial también el paso de «sin cercanía» a un valor; permitir borrar.

### H-6-13 · Variables sociales en 0 en días sin diario
- **Dónde:** `ciencia/Estadistica.kt`, `panel()` (bucle sobre `Repo.dias.keys + momentosPorDia.keys`).
- **Qué pasa:** `vi_*`, `cercanos`, `personas_vistas`, `penso_en_*` valen 0 en cualquier día con documento de diario o algún momento, aunque no se haya escrito el texto del diario (p. ej., solo un aviso de ánimo o solo el sueño). Lo mismo con la base de `per_`/`pen_` (`diasDiario`).
- **Por qué importa:** «no escribí» se confunde con «no vi a nadie»; si los días sin diario no son al azar (días malos o muy ocupados), sesga las correlaciones sociales. Relacionado con H-1-6 y H-2-4.
- **Gravedad:** Media.
- **Propuesta:** calcular estas variables solo en días con texto de diario (o dejarlas `null` cuando no lo hay y no hay momentos con @).

### H-6-14 · La Constelación usa la relación actual
- **Dónde:** `ui/pantallas/Constelacion.kt`, `calcular()` (`Perfiles.relacion(p)` sin fecha).
- **Qué pasa:** el color refleja la relación de hoy, no la del periodo mostrado (pista 6 del plan, confirmada). El ángulo de cada estrella depende de su posición en el orden por días y no tiene significado.
- **Por qué importa:** solo visual; no entra en análisis.
- **Gravedad:** Baja.
- **Propuesta:** solo documentar (opcional: colorear con la relación más frecuente en el periodo).

### H-6-15 · La escala IOS de la app difiere del original
- **Dónde:** `ui/pantallas/Gente.kt`, `CirculosCercania` y `CERCANIA`.
- **Qué pasa:** se agregan etiquetas verbales a los siete pares de círculos; se responde una vez por persona y no se pide actualizarla; la superposición crece de forma no lineal (0 %, 4 %, 15 %, 30 %, 47 %, 65 %, 85 %).
- **Por qué importa:** la validez de la IOS es para la versión pictórica en un momento dado; `cercania_media` mide a quién nombras más que la cercanía de ese día.
- **Gravedad:** Baja.
- **Propuesta:** solo documentar; opcionalmente, recordar revisar la cercanía una vez al año.

## Módulo 7 · Experimentos personales N-de-1 (v81, commit `fa527df`)

### H-7-1 · El lavado puede eliminar una condición entera; la plantilla «Creatina» nunca da resultado
- **Dónde:** `data/Lote3.kt`, `Experimentos.enLavado()` y `PLANTILLAS` («creatina»: antes/después, 8 semanas, 28 días de lavado); `ui/pantallas/Lote3.kt`, `PantallaExperimentos` (opciones de lavado 0, 1, 2, 7 y 28 con cualquier diseño y duración).
- **Qué pasa:** en la plantilla, el único cambio es el día 28 y los días 28–55 son de lavado: cuentan 28 días sin la acción y 0 con ella, así que `diferencia()` siempre devuelve `null`. Con semanas alternas, un lavado de 7 días elimina toda semana que empieza con un cambio (un plan SÍ no SÍ no SÍ no queda sin ningún día); con 28, todo.
- **Por qué importa:** el experimento se hace completo y no produce resultado; la plantilla recomendada para el caso más largo es inutilizable.
- **Gravedad:** Alta.
- **Propuesta:** impedir lavados ≥ duración de un periodo (o alargar el plan en esos casos); en la plantilla, por ejemplo, 12 semanas con lavado de 28 o contar el lavado solo al inicio de la fase «SÍ» con más semanas.

### H-7-2 · «Terminar experimento» no guarda la fecha
- **Dónde:** `ui/pantallas/Lote3.kt`, `TarjetaExperimento` (`e.copy(activo = false)`); `data/Lote3.kt`, `Experimentos.resultado()` (no mira `activo`).
- **Qué pasa:** tras terminar antes de tiempo, los días hasta el final del plan siguen contando según el plan, aunque ya no hagas la acción; el aviso diario desaparece.
- **Por qué importa:** semanas «SÍ» sin acción se cuentan como «con la acción» y diluyen o distorsionan el resultado.
- **Gravedad:** Media.
- **Propuesta:** guardar `fin` al terminar y cortar el análisis en esa fecha.

### H-7-3 · Intervalo demasiado estrecho: días independientes y 1.96 fijo
- **Dónde:** `ciencia/Estadistica.kt`, `diferencia()`; `Experimentos.resultado()`; también `EtapasDeVida` en `Explorar.kt`.
- **Qué pasa:** el error estándar de Welch trata cada día como independiente y usa 1.96 aun con 3 días por condición; la unidad sorteada es la semana, no el día. Pistas 1 y 2 del plan, confirmadas.
- **Por qué importa:** en simulación (6 semanas alternas, sin efecto real), falsas señales del 14 % con autocorrelación 0.3 y del 24 % con 0.5, en lugar del 5 %. Con 3 + 3 días, el intervalo debería ser ~42 % más ancho solo por usar *t*.
- **Gravedad:** Alta.
- **Relación:** es el mismo problema de H-5-5, aquí cuantificado para los experimentos.
- **Propuesta:** prueba de aleatorización sobre los planes posibles (por semanas); como mínimo, *t* de Welch y un error estándar ajustado por la autocorrelación (como el *n* efectivo de las correlaciones), y exigir más días antes de mostrar el resultado.

### H-7-4 · El resultado se ve y se actualiza durante el experimento
- **Dónde:** `ui/pantallas/Lote3.kt`, `TarjetaExperimento` (muestra la diferencia y el mensaje de «señal» desde 3 días por condición).
- **Qué pasa:** se puede mirar cada día y detenerse en la primera «señal».
- **Por qué importa:** en simulación, detenerse en la primera señal da falsas señales el 30 % (días independientes) a 50 % (autocorrelación 0.3) de las veces; además, ver resultados parciales alimenta la expectativa (sin cegamiento).
- **Gravedad:** Media.
- **Propuesta:** ocultar la diferencia hasta terminar el plan (mostrar solo días contados), o marcar el resultado como «provisional».

### H-7-5 · El sorteo libre puede producir un antes/después
- **Dónde:** `ui/pantallas/Lote3.kt`, `PantallaExperimentos` (`shuffled(...)` de la mitad «SÍ» y la mitad «no»).
- **Qué pasa:** probabilidad de un bloque puro (todo «SÍ» y luego todo «no», o al revés): 33 % con 4 semanas, 10 % con 6, 2.9 % con 8, 0.2 % con 12. Pista 3 del plan, confirmada.
- **Por qué importa:** con una tendencia lenta (0.02 desviaciones/día, 8 semanas, sin efecto real), falsas señales del 14 % con sorteo libre frente al 6 % por pares.
- **Gravedad:** Media.
- **Propuesta:** sortear por pares de semanas y, si se usa una prueba de aleatorización, recomendar 10–12 semanas (con pares, el *p* mínimo es 1/2^(N/2): 0.125 con 6 semanas).

### H-7-6 · La prueba de tendencia confunde un efecto real con una tendencia
- **Dónde:** `data/Lote3.kt`, `Experimentos.resultado()` (pendiente del valor contra el día, ambas condiciones juntas, |b/EE| > 2).
- **Qué pasa:** en un antes/después, un efecto real hace la segunda mitad más alta y la recta lo toma como tendencia.
- **Por qué importa:** en simulación, con efecto real *d* = 0.8 y sin tendencia, el aviso aparece en el 72 % de los antes/después y el 16 % de los alternos (≈ 5 % esperado); el mensaje sugiere lo contrario de lo que pasa.
- **Gravedad:** Media.
- **Propuesta:** ajustar valor ~ día + condición y reportar el efecto de la condición descontada la tendencia; avisar de la tendencia solo con el término del día en ese modelo.

### H-7-7 · d de Cohen sin corrección de Hedges y con umbrales de diseños entre personas
- **Dónde:** `data/Lote3.kt`, `Experimentos.resultado()` y `tamano()`.
- **Qué pasa:** *d* con desviación agrupada sin el factor de Hedges (0.80 con 3 + 3 días); etiquetas 0.2/0.5/0.8 pensadas para diferencias entre personas, aplicadas a la variación de un día a otro de una persona.
- **Por qué importa:** con pocos días, *d* inflado; las etiquetas («mediano», «grande») sugieren comparaciones con la literatura que no aplican.
- **Gravedad:** Baja.
- **Propuesta:** aplicar la corrección de Hedges y aclarar en la tarjeta que la escala es relativa a tus días.

### H-7-8 · No se registra la adherencia
- **Dónde:** modelo `Experimento` (`data/Modelo.kt`); aviso en `ui/pantallas/Dia.kt`.
- **Qué pasa:** se guarda lo asignado por semana, no si se hizo la acción cada día.
- **Por qué importa:** el análisis es por intención de tratar; el efecto se diluye con incumplimientos y no se puede medir cuánto se cumplió.
- **Gravedad:** Media.
- **Propuesta:** casilla diaria «¿lo hiciste?» en el aviso del Día, guardada por fecha, y mostrar el % de cumplimiento (y un análisis por lo hecho como secundario).

### H-7-9 · El aviso del Día muestra la semana 1 durante la semana previa
- **Dónde:** `ui/pantallas/Dia.kt` (`ChronoUnit.WEEKS.between(inicio, fecha)`).
- **Qué pasa:** la función trunca hacia cero: para los 6 días anteriores a `inicio` da 0 y se muestra «semana 1 de N» con su instrucción.
- **Por qué importa:** si la semana 1 es «SÍ», se puede empezar antes y alterar la línea de base inmediata (no cambia el cálculo, que empieza en `inicio`).
- **Gravedad:** Baja.
- **Propuesta:** usar `Experimentos.toca()` (que devuelve `null` antes del inicio) o comprobar `fecha >= inicio`; mostrar «empieza el lunes».

### H-7-10 · El sellado no fija el análisis y no está protegido
- **Dónde:** `PantallaExperimentos` y `Repo.guardarExperimento()` (sobrescribe el documento).
- **Qué pasa:** se sella la hora de la hipótesis, pero no el criterio de decisión ni cuándo mirar; el documento se puede reemplazar al restaurar un respaldo.
- **Por qué importa:** el prerregistro queda incompleto (ver H-7-4); su valor depende de la disciplina del usuario.
- **Gravedad:** Baja.
- **Propuesta:** solo documentar; opcionalmente, sellar también el umbral de decisión y guardar una huella (hash) del texto sellado.

### H-7-11 · No hay función de R para analizar los experimentos
- **Dónde:** `assets/vestigium.R` (lee `vm_experimentos.csv` pero no lo usa); `data/Exportar.kt`, `filasExperimentos()`.
- **Qué pasa:** el plan, el lavado y la variable se exportan, pero no hay forma reproducible de recalcular el resultado ni de hacer una prueba de aleatorización.
- **Por qué importa:** el resultado de la app no se puede comprobar fuera de ella; criterio (b) del módulo 9 por ausencia.
- **Gravedad:** Baja.
- **Propuesta:** `vm_experimento(id)` que replique `Experimentos.resultado` y añada la prueba de aleatorización sobre todos los planes posibles.

### H-7-12 · «Antes y después» de los cambios de vida: muchas comparaciones y tarjetas repetidas
- **Dónde:** `ui/pantallas/Explorar.kt`, `EtapasDeVida()`.
- **Qué pasa:** hasta 8 cambios × 7 variables = 56 comparaciones con el intervalo de H-7-3 (el mismo de H-5-5), sin corrección; un mismo guardado con varios campos produce tarjetas con la misma fecha y los mismos números; ventanas de 60 días que se enciman; fecha de edición, no del cambio real.
- **Por qué importa:** cabe esperar ~3 diferencias «claras» por azar incluso sin cambios reales (más con autocorrelación); la repetición da la impresión de hallazgos independientes.
- **Gravedad:** Media.
- **Propuesta:** agrupar los cambios por fecha, aplicar Benjamini-Hochberg (como en las correlaciones) y un intervalo ajustado por autocorrelación; mantener el aviso de «pista».

### H-7-13 · El panel de los experimentos se recalcula solo cuando cambia el número de días
- **Dónde:** `ui/pantallas/Lote3.kt`, `PantallaExperimentos` (`remember(Repo.dias.size, Repo.experimentos)`).
- **Qué pasa:** datos que llegan sin crear un día nuevo (sueño del reloj, pantalla, entrenos) no actualizan el resultado mientras la pantalla siga en memoria.
- **Por qué importa:** el resultado mostrado puede no incluir los últimos datos.
- **Gravedad:** Baja.
- **Propuesta:** usar las mismas claves de caché que el panel general.

### H-7-14 · La medida de la plantilla «Creatina» depende de la sesión y de la progresión
- **Dónde:** `Experimentos.PLANTILLAS` («creatina» → `volumen_kg`).
- **Qué pasa:** `volumen_kg` solo existe los días con entreno y depende de qué grupo muscular se entrenó; con entrenamiento progresivo, sube solo.
- **Por qué importa:** en un antes/después, la progresión se confunde con el efecto (sección 8.8 del capítulo 8).
- **Gravedad:** Baja.
- **Propuesta:** medir con un ejercicio fijo de referencia (o el máximo estimado en ese ejercicio) y documentar el riesgo de tendencia.

## Módulo 8 · Reportes, motivación, privacidad e integridad (v81, commit `fa527df`)

### H-8-1 · Los momentos de impulso no cuentan como privados
- **Dónde:** `data/Modelo.kt`, `Momento.privado` (solo `recaida` y `libido`); consumidores que filtran con `!privado`: `ciencia/Periodo.kt` (reportes), `ciencia/TuAnio.kt` («lo que pasó una vez»), `ciencia/Logros.kt` (logros de momentos), pantallas Semana, Calendario, Año y Mapa.
- **Qué pasa:** los momentos de tipo `impulso` aparecen en el conteo por tipo del reporte («momenta»), sus @menciones entran en «con quién», pueden salir en «Tu año» y cuentan para los logros «Coleccionista», «Cien momentos» y «Mil momentos». El comentario de `Periodo` dice «Nunca toca lo privado (impulsos, detonantes, medicamentos)».
- **Por qué importa:** contenido del seguimiento privado llega a salidas que se guardan en carpetas públicas o se comparten, y a un sistema de recompensas. Contradice la regla declarada en la sección 7 del documento de contexto («Seguimiento privado (impulsos, …) nunca en reportes, Tu año, logros, mapas ni widgets»); también llegan al mapa (`Mapa.kt`) y al conteo de momentos de un widget (`Widgets.kt`). Criterio (c) del módulo 9.
- **Gravedad:** Alta.
- **Propuesta:** incluir `impulso` en `Momento.privado` (revisando que la pantalla de impulsos siga funcionando) o filtrarlo explícitamente en `Periodo`, `TuAnio` y `Logros`.

### H-8-2 · «La relación del año» puede usar variables privadas y de medicamentos
- **Dónde:** `ciencia/TuAnio.kt`, `calcular()` (`Estadistica.relaciones(panelAnio, vars, true)` con `vars = Estadistica.variables(dinamicas = false)`).
- **Qué pasa:** con el seguimiento privado activo, la lista incluye `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`; `medicamentos_n` siempre. La relación más fuerte se muestra en una tarjeta que se puede compartir como imagen.
- **Por qué importa:** contradice «Nunca toca el seguimiento privado» del propio comentario y la regla declarada del proyecto, que incluye los medicamentos en el seguimiento privado; criterio (c).
- **Gravedad:** Media.
- **Propuesta:** excluir el grupo «Privado» y los medicamentos de `vars` en `TuAnio` (y en cualquier salida compartible).

### H-8-3 · La recaída afecta logros y campañas a través del hábito ligado
- **Dónde:** `data/Recaidas.kt`, `registrar()` (desmarca el hábito ligado); `ciencia/Logros.kt`, constancia por hábito (`c_hab_*`) y logros propios (`serie`, cualquier variable del panel); `ciencia/Campanas.kt`, retos `habito:*`.
- **Qué pasa:** registrar una recaída baja el progreso de los logros de constancia y de las campañas de ese hábito; un logro propio puede definirse sobre variables privadas (las de sustancias no son privadas según la regla declarada, así que ahí no hay conflicto).
- **Por qué importa:** el principio de no premiar ni castigar con logros el seguimiento privado se cumple solo en los logros predefinidos; la vía indirecta añade presión para no registrar recaídas (dato faltante no al azar).
- **Gravedad:** Media.
- **Propuesta:** excluir el hábito ligado a recaídas de logros y campañas, y excluir el grupo «Privado» de las fuentes de logros propios.

### H-8-4 · Reportes automáticos en carpetas públicas
- **Dónde:** `reportes/Reportes.kt`, `guardarPng` / `guardarPdf` (`Pictures/VestigiumMei`, `Download/VestigiumMei`); `avisos/Trabajo.kt` (lunes desde las 8:00, día 1 desde las 9:00).
- **Qué pasa:** los reportes se generan y guardan solos en almacenamiento compartido; incluyen nombres de personas (modo discreto apagado por omisión), la frase del mejor momento, ciudades, peso, dinero y ánimo.
- **Por qué importa:** quedan al alcance de la galería, de otras apps con permiso de medios y de la copia automática de fotos a la nube, sin una decisión explícita.
- **Gravedad:** Media.
- **Propuesta:** guardarlos en el almacenamiento privado y copiarlos a la carpeta pública solo al compartir; o preguntar la primera vez; considerar el modo discreto por omisión en lo que se guarda solo.

### H-8-5 · Sin contraseña, todo sale en claro; el JSON lleva el texto de las cartas selladas
- **Dónde:** `data/Respaldos.kt`, `aCarpeta()`; `data/Drive.kt`, `subir()`; `data/Exportar.kt`, `archivos()` (`vm_respaldo.json` junto a `vm_cartas.csv`).
- **Qué pasa:** sin contraseña, la carpeta elegida y Drive reciben todos los CSV y el JSON completo (diario, seguimiento privado aunque el interruptor esté apagado). Que el seguimiento privado vaya en la exportación a R es intencional según la regla declarada; lo que no lo es: que viaje en claro fuera del teléfono. `vm_cartas.csv` omite el texto de las cartas selladas (como pide la regla), pero `vm_respaldo.json` lo trae.
- **Por qué importa:** el diario y lo privado quedan legibles fuera del teléfono; el sello de las cartas se puede saltar.
- **Gravedad:** Media.
- **Propuesta:** proponer (o exigir) contraseña al activar la carpeta o Drive; en `respaldoJson()` para exportar (no para el respaldo cifrado), omitir el texto de las cartas aún selladas o advertirlo.

### H-8-6 · Logros que premian valores de la valencia
- **Dónde:** `ciencia/Logros.kt` («Día perfecto»: valencia 7; «Remontada»: de ≤ 2 a ≥ 6 en menos de 3 días).
- **Qué pasa:** son medallas por escribir ciertos valores en la escala subjetiva más analizada; son secretas y de una sola vez.
- **Por qué importa:** incentivo (pequeño) a sesgar la valencia; ley de Campbell.
- **Gravedad:** Baja.
- **Propuesta:** reemplazarlas por logros de registro (p. ej., «registraste un día difícil con un toque») o solo documentar.

### H-8-7 · Las campañas no quedan registradas como variable
- **Dónde:** `ciencia/Campanas.kt`; `Config.campanas`; ausentes de `Estadistica.panel` y de la exportación tabular.
- **Qué pasa:** una semana con campaña de sueño o de pasos es una intervención, pero el panel no la marca.
- **Por qué importa:** las correlaciones entre la conducta objetivo y otras variables pueden deberse a la campaña (o a por qué se eligió esa semana); no se puede controlar en R; criterio (e).
- **Gravedad:** Media.
- **Propuesta:** variable diaria `campana_<reto>` (0/1) en el panel y en la exportación.

### H-8-8 · Las rachas de recaídas cuentan como limpios los días sin respuesta
- **Dónde:** `data/Recaidas.kt`, `resumen()` (bucle desde el primer día con respuesta; `diasConRespuesta()` no se usa en la racha).
- **Qué pasa:** todo día sin recaída registrada cuenta como limpio, aunque no haya respuesta ese día.
- **Por qué importa:** si los días sin registro son los difíciles, la racha se infla (dato faltante no al azar).
- **Gravedad:** Media.
- **Propuesta:** contar como limpios solo los días con respuesta «no»; mostrar aparte los días sin respuesta.

### H-8-9 · PBKDF2 con menos iteraciones que la recomendación actual
- **Dónde:** `data/Respaldos.kt`, `ITERACIONES = 210_000` con `PBKDF2WithHmacSHA256`; contraseña mínima de 8 caracteres (`ui/pantallas/Respaldo.kt`).
- **Qué pasa:** OWASP recomienda 600 000 iteraciones para PBKDF2-HMAC-SHA256 (210 000 parece corresponder a una recomendación anterior para SHA-512).
- **Por qué importa:** ~2.9 veces menos costo por intento al adivinar la contraseña de un .vmc robado.
- **Gravedad:** Baja.
- **Propuesta:** subir a 600 000 guardando el número de iteraciones en el encabezado (nueva versión del formato, p. ej. `…-CIFRADO-2`) para seguir abriendo los archivos viejos; sugerir contraseñas largas.

### H-8-10 · El «ánimo del día» de reportes y «Tu año» mezcla instrumentos
- **Dónde:** `ciencia/Periodo.kt`, `valDe()`.
- **Qué pasa:** usa la valencia del diario y, si falta, el promedio de los registros momentáneos.
- **Por qué importa:** el ánimo medio de periodos con distinta cobertura del diario no es comparable (capítulo 3, sección 3.3.4).
- **Gravedad:** Baja.
- **Relación:** ya señalado en H-5-15 (módulo 5); se repite aquí porque afecta a «Tu año» y a la comparación entre periodos.
- **Propuesta:** usar solo la valencia del diario (y mostrar aparte la de los avisos) o indicar cuántos días vienen de cada fuente.

### H-8-11 · Comparaciones con el periodo anterior sin incertidumbre
- **Dónde:** `reportes/Reportes.kt`, `cambio()` (umbral fijo 0.05 / 0.5); `Periodo` (mínimo 3 días en el periodo anterior).
- **Qué pasa:** cualquier diferencia ≥ 0.05 se presenta como cambio.
- **Por qué importa:** con pocas observaciones se reportan como cambios variaciones de azar.
- **Gravedad:** Baja.
- **Propuesta:** solo documentar, o marcar «parecido» cuando la diferencia sea menor que el error típico de la semana.

### H-8-12 · «Verificar» promete más de lo que comprueba y no revisa el esquema
- **Dónde:** `data/Respaldos.kt`, `verificar()` y `contenido()`; `data/Modelo.kt`, `JSON { ignoreUnknownKeys = true }`.
- **Qué pasa:** el mensaje dice «se lee completo y no le falta nada» sin comparar con el teléfono; no compara `schema_v` del respaldo con el de la app; al restaurar un respaldo de una versión más nueva, los campos desconocidos se descartan en silencio.
- **Por qué importa:** falsa sensación de seguridad; pérdida silenciosa de datos al restaurar entre versiones; criterio (d).
- **Gravedad:** Media.
- **Propuesta:** decir «se lee completo» y comparar conteos con el teléfono; avisar (o impedir restaurar) si `schema_v` del respaldo es mayor que `SCHEMA_V`.

### H-8-13 · Restaurar combina con reglas distintas por colección
- **Dónde:** `data/Importar.kt`, `respaldoTexto()`; `data/Repo.kt`, `importarDias(sobrescribir = false)`.
- **Qué pasa:** en días gana el teléfono; en salud, pantalla, lugares, cartas, experimentos y otras gana el respaldo; momentos y obras se agregan por id; MacroFactor solo si no hay nada.
- **Por qué importa:** restaurar sobre un teléfono con un día incompleto no recupera el diario de ese día; un respaldo viejo puede pisar datos nuevos de salud; no se avisa; criterio (d).
- **Gravedad:** Media.
- **Propuesta:** regla única (gana el documento con `ts_editado` más reciente, o fusión campo por campo de los días) e informe de conflictos.

### H-8-14 · Un respaldo malo puede reemplazar al bueno; Drive restaura siempre el más reciente
- **Dónde:** `data/Drive.kt`, `subir()` (reemplaza el respaldo del día y crea la semanal sin comparar) y `ultimaCopia()` / `restaurar()`; `data/Respaldos.kt`, `aCarpeta()`.
- **Qué pasa:** tras una pérdida o daño de datos en el teléfono, el respaldo automático sube la versión empobrecida; la restauración desde Drive elige la copia con fecha de modificación más reciente.
- **Por qué importa:** la protección contra pérdida depende de copias más viejas que la app no ofrece restaurar; criterio (d).
- **Gravedad:** Media.
- **Propuesta:** no sobrescribir si el respaldo nuevo trae muchos menos días o momentos que el anterior (y avisar); dejar elegir la copia a restaurar.

### H-8-15 · Cambiar la contraseña deja copias viejas con la contraseña anterior
- **Dónde:** `data/Respaldos.kt`, `ponerContrasena()`; copias semanales y mensuales de `data/Drive.kt`.
- **Qué pasa:** las copias ya subidas siguen cifradas con la contraseña anterior.
- **Por qué importa:** para restaurar una copia vieja hace falta recordar la contraseña de entonces.
- **Gravedad:** Baja.
- **Propuesta:** avisarlo al cambiarla (o recifrar las copias existentes).

### H-8-16 · Los medicamentos no están detrás del interruptor privado
- **Dónde:** `ui/pantallas/Dia.kt` (sección «Medicamentos» siempre visible); `ciencia/Estadistica.kt` (`medicamentos_n` siempre); `ciencia/Dinamicas.kt` (`med_`); comentario de `ciencia/Periodo.kt`.
- **Qué pasa:** el código los trata como datos de salud normales, pero su propio comentario, la guía del proyecto y la regla declarada del documento de contexto (sección 7) los cuentan como seguimiento privado.
- **Por qué importa:** la regla no prohíbe mostrarlos en pantallas ni analizarlos, pero sí que lleguen a «Tu año», y llegan (H-8-2). El criterio del código y el declarado no coinciden.
- **Gravedad:** Baja.
- **Propuesta:** decidir explícitamente; si son privados, ponerlos detrás del interruptor y excluirlos de salidas compartibles.

### H-8-17 · La lista declarada de servicios externos está incompleta
- **Dónde:** documento de contexto (sección 1: «nada pasa por servidores ajenos salvo Drive del usuario, Open-Meteo (clima) y Spotify API»); código: `data/Portadas.kt` (TMDB, RAWG, TVmaze, Open Library, Crossref), `ui/pantallas/Mapa.kt` (mosaicos de OpenStreetMap con osmdroid, `TileSourceFactory.MAPNIK`).
- **Qué pasa:** la app también consulta catálogos de obras (envía el título o DOI buscado) y descarga mosaicos del mapa (revela la zona que se mira).
- **Por qué importa:** la descripción de qué sale del teléfono es la base para decidir qué es privado; hoy subestima las salidas, aunque las omitidas no envían datos del diario.
- **Gravedad:** Baja.
- **Propuesta:** solo documentar (actualizar la sección 1 del documento de contexto); opcionalmente, mostrar en la app una lista de servicios externos con lo que recibe cada uno.
