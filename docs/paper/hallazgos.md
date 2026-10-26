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
