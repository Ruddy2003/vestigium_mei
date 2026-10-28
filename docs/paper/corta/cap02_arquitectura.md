# Capítulo 2. Inventario y arquitectura de datos

> **Para qué sirve este capítulo.** Explica *cómo se obtiene, se guarda, se asigna a un día y se organiza* cada dato de Vestigium Mei, y por qué cada decisión es razonable (o no). Describe el código de la v92 (la v91 más la entrega 10f, commit `2b262c5`; `SCHEMA_V = 15`); lo que cambió en las entregas 10e y 10f se marca «desde 10e» y «desde 10f». Los ejemplos numéricos son **sintéticos**. Cada decisión se marca como **[Validado]** (respaldada por literatura o por un estándar), **[Aproximación]** (razonable, con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app sin referente directo). Los hallazgos de este capítulo son H-1-n (módulo 1); los H-9-n son de la revisión general.

---

## 2.1 Propósito y principios de diseño

Vestigium Mei es un instrumento de **autoseguimiento cuantificado (quantified self, QS) de un solo sujeto** (diseño N-de-1, es decir, un estudio cuya muestra es una sola persona): una persona produce, durante años, un registro denso de su ánimo, cuerpo, conducta, entorno y relaciones. La pregunta metodológica central es: *¿qué estructura de datos permite hacer inferencia razonable sobre un solo individuo, sin falsear lo que realmente se midió?*

La arquitectura responde con cinco principios:

1. **Un día es la unidad de análisis.** Casi todo se resume en una fila por día (el *panel diario*). Las mediciones intradía (avisos de ánimo, ubicación por hora) se conservan en tablas aparte. **[Aproximación]**: pierde la variación dentro del día.
2. **No inventar datos.** Un dato ausente no se convierte en cero salvo que haya una razón estructural (sección 2.6). Confundir «no medido» con «cero» sesga medias, correlaciones y modelos (Rubin, 1976; Little y Rubin, 2019).
3. **Conservar el dato crudo y derivarlo después.** Cada evento se guarda con su marca de tiempo completa, y el panel y los resúmenes se calculan al analizar o exportar, así que se pueden re-derivar con otra regla (por ejemplo, otro corte de día). *Límite:* pantalla, pasos y lo que Health Connect entrega por día se guardan ya sumados (`PantallaDia`, `SaludDia`) y no se pueden re-derivar (sección 2.3).
4. **Esquema flexible que crece con los años.** Cada colección guarda documentos JSON; añadir un campo no obliga a migrar la base.
5. **Todo es exportable**: tablas en formato ordenado (*tidy*, Wickham, 2014), diccionario y un script en R, por reproducibilidad y principios FAIR (Wilkinson et al., 2016).

---

## 2.2 Las tres capas de captura: activa, pasiva e importada

El origen de un dato determina su tipo de error, por lo que la app conserva esa distinción.

| Capa | Ejemplos | Error típico |
|---|---|---|
| **Activa** (la persona lo teclea o marca) | Diario del día, avisos de ánimo, momentos, gastos, notas | Sesgo de recuerdo, de deseabilidad social, fatiga de registro, días sin llenar |
| **Pasiva** (sensores y APIs del teléfono) | Pantalla, desbloqueos, pasos, sueño del reloj, ubicación, clima, luz | Error de instrumento, cambios de permisos u hora, huecos por apagado |
| **Importada** (archivos que la persona carga) | Takeout de YouTube/Google, WhatsApp, Cold Turkey, nutrición, respaldos | Cobertura parcial, formato cambiante, zonas horarias distintas |

**Razonamiento científico.** Lo pasivo reduce la carga y el sesgo de recuerdo; lo activo captura lo que ningún sensor ve. El **muestreo de experiencias** (*experience sampling*, ESM: preguntar varias veces al día «¿cómo estás ahora?»; Csikszentmihalyi y Larson, 1987) justifica los avisos de ánimo: preguntar *en el momento* reduce el sesgo retrospectivo frente al diario nocturno. **[Validado]** el principio; **[Diseño propio]** la frecuencia y el contenido exacto de los avisos.

**Cuándo falla.** Dos fuentes que miden lo mismo no son intercambiables. Por eso la app guarda la valencia del diario y la de los avisos como variables distintas (`valencia`, `muestreo_valencia`, `animo_valencia`). *Ejemplo sintético:* un día el diario marca `valencia = 3` y los avisos promedian `muestreo_valencia = 5.3`. Eso sugeriría que el recuerdo nocturno está dominado por el peor momento o por el final del día (la «regla del pico y el final», Kahneman et al., 1993). Esa diferencia **es un resultado de investigación**, no un error (capítulo 3).

---

## 2.3 Tiempo: marcas ISO y día lógico

### 2.3.1 Marcas de tiempo

`Fechas.ahoraIso()` genera, por ejemplo, `2026-10-16T08:42:10-06:00`: fecha y hora **locales con el desfase respecto a UTC** (el horario universal de referencia). Las fechas «de día» son `YYYY-MM-DD`. Este formato ISO-8601 identifica un instante físico sin ambigüedad y conserva la hora «de reloj de pared», necesaria para ritmos circadianos (a qué hora se duerme, cuándo se usa el teléfono). **[Validado]** (estándar ISO 8601).

**La hora local de entonces (desde 10f, D.109 y D.110; H-1-7, resuelto).** Hasta 10f, los eventos pasados se interpretaban con la zona **actual** del teléfono (`Fechas.zona()`): tras un viaje, un evento antiguo con otro desfase se reconvertía a la hora de hoy, y la hora de dormir o el día de una nota podían moverse. Ahora la configuración guarda cada cambio de zona horaria (`Config.zonas`, «desde este instante, esta zona»), y cada instante se lee con la zona que tenía el teléfono entonces (`Fechas.zonaEn`); una nota con su desfase se lee con ese desfase (`Fechas.localDe`). El cambio se anota al abrir la app, cada 15 minutos y cuando Android avisa (`TIMEZONE_CHANGED`). Para lo guardado antes, el historial se arma con las notas: un periodo con otro desfase empieza si **dos notas seguidas** lo traen. *Ejemplo sintético:* una toma de cafeína anotada en Madrid como `…T08:00:00+02:00` antes se leía a las 00:00 en la Ciudad de México y caía en el día lógico anterior; ahora se lee a las 08:00 y cuenta para su día. `vm_panel.csv` marca los días con cambio de horario o de zona (`cambio_horario`, con `desfase_utc_h`); un día así no dura 24 horas sino 24 − (desfase final − desfase inicial): 23 o 25 con el horario de verano, 16 en un viaje de UTC−6 a UTC+2. **[Validado]** el principio; **[Diseño propio]** cómo se arma el historial. *Cuándo falla:* lo que el teléfono ya guardó sumado por día (sueño, pasos, pantalla) no se recalcula, y el historial reconstruido solo ve los periodos en que anotaste algo.

### 2.3.2 Día lógico (corte a las 05:00)

`Fechas.diaLogico` asigna al **día anterior** cualquier evento entre las 00:00 y las 04:59 (si la hora es menor que 5, resta un día).

*Razonamiento científico.* La medianoche no es una frontera natural de la conducta: quien se acuesta a las 01:30 «todavía está viviendo» el día anterior. La *American Time Use Survey* usa un día-diario que va de las 4:00 a.m. a las 3:59 a.m. por la misma razón (U.S. Bureau of Labor Statistics, s. f.). Cortar en un periodo de mínima actividad reduce los episodios partidos entre dos días. **[Validado]** el principio; **[Diseño propio]** el valor exacto 05:00.

*Ejemplo sintético.* Una nota del martes a las 00:40 y la última pantalla a las 02:10 pertenecen al **lunes** lógico; el primer uso a las 05:05 ya es del martes. `ultimo_uso_h` suma 24 si la hora es menor que 5 (02:10 → 26.17), para que «más tarde» siga siendo un número mayor. Antes de las 05:00, las pantallas donde se anota dicen el corte en palabras (`AvisoMadrugada`). La calidad del sueño va, en cambio, al día del despertar (capítulo 3).

**Pantalla y pasos, desde el esquema 13.** Antes se sumaban de 0:00 a 24:00; ahora `Pantalla.calcular` y `Salud.sincronizar` usan la ventana de 05:00 a 05:00. Cada día guarda qué ventana usó (`"logica"` o `"calendario"`), y **la serie se parte en dos tramos**: una madrugada de teléfono de 01:00 a 01:30 antes contaba para el día nuevo y ahora para el anterior. Las correlaciones que crucen la fecha del cambio deben leerse por tramo; en R se separan con `ventana_pantalla` y `ventana_pasos` de `vm_panel.csv`. **[Diseño propio]**.

**Lo que sigue por día de calendario** (0:00 a 24:00): las variables del reloj y la báscula, la agenda, el clima y el aire, la luna y las de ciudades y países (lista exacta en `Diccionario.CALENDARIO`; versión completa, §2.3.2). El diccionario y el `LEEME.txt` lo dicen; antes el LEEME afirmaba que **todo** empezaba a las 5:00 (H-9-4).

**Cuándo falla.** (a) Quien trabaja de noche o duerme de madrugada con regularidad rompe el supuesto de que a las 05:00 «hay poca actividad»; la regla no se adapta a la persona (limitación L-1). (b) En una misma fila del panel, la madrugada cuenta para unas variables y no para otras, aunque ahora se declara. Entre las visitas, `lugares_distintos` y `en_casa_min` ya usan el mismo día lógico de la llegada (H-1-2, resuelto en 10c).

---

## 2.4 El almacén: SQLite, documentos JSON y `Repo`

### 2.4.1 Base de datos y lectura tolerante

`Db` (`SQLiteOpenHelper`, archivo `vestigium.db`) es un **almacén de documentos sobre SQLite**: cada colección (`dias`, `momentos`, `gastos`…) es un conjunto de filas de la tabla `docs` (clave `(col, id)`, con `fecha` y el `json` del documento). Las ubicaciones, muy numerosas y de tamaño fijo, van en su propia tabla con columnas numéricas, por eficiencia. Añadir campos no obliga a migrar, y SQLite da transacciones atómicas y robustez ante apagados. **[Diseño propio]**, práctica común en apps móviles.

La lectura es **tolerante**. `ignoreUnknownKeys` ignora los campos que el código no conoce: permite leer respaldos de versiones futuras, pero descarta en silencio lo que no entiende. `coerceInputValues` sustituye `null` o valores inválidos por el valor por omisión, y así puede convertir un «sin dato» en un valor (H-1-4, abierto). Los documentos que no se pueden decodificar quedan fuera de la app, del panel y de la exportación; hasta el esquema 12 desaparecían **sin aviso** (H-1-3), y desde el 13 se cuentan por colección (`Repo.ilegibles`) y se avisan en «Estado de los datos» y en Hoy. Si la configuración no se puede leer, su texto se copia a `config_ilegible` **antes** de seguir con una vacía, para que el siguiente cambio de ajustes no la sobrescriba (H-9-2).

### 2.4.2 `Repo`: memoria con escritura inmediata

`Repo` mantiene en memoria **35 colecciones** que precarga al iniciar (entre ellas, desde el esquema 13, `avisos`: un documento por día con los avisos de ánimo planeados, vencidos y mostrados; capítulo 3). Cada `guardarX` hace **escritura inmediata** (*write-through*): primero a SQLite (`db.poner`), luego a la memoria. Así se pierde lo mínimo si la app se cierra, y los análisis sobre miles de días son instantáneos; el costo es la memoria, aceptable para un solo usuario. **[Validado]** (patrón estándar de persistencia).

**Metadatos de calidad en `Dia`.** `guardarDia` añade `ts_creado`, `ts_editado`, `schema_v` y **`retroactivo`**: si el día se llenó después de que su día lógico terminó. Desde el esquema 13, `Dia` guarda **`ts_valencia`**, la hora en que la valencia pasó de vacía a llena o cambió (`Reglas.tsValencia`), y `retroactivo` = el día lógico de `ts_valencia` es posterior a la fecha del día (`Reglas.retroactivo`). Antes se decidía al crear el documento, y fallaba si otra cosa lo creaba ese mismo día y la valencia se llenaba días después (H-2-2; capítulo 3). `retroactivo` es un **moderador** del sesgo de recuerdo: un día llenado a posteriori se recuerda, no se vive. En la literatura de diarios se recomienda registrar *cuándo* se llenó cada entrada (Stone et al., 2002). **[Diseño propio]**.

---

## 2.5 Catálogo de colecciones

Las colecciones se agrupan por familia: ánimo y diario (`dias`, `momentos`, `respuestas`…), cuerpo y salud, teléfono y pantalla, dinero (`gastos`, `ingresos`), lugar y movimiento (`lugares`, `visitas`, la tabla `ubicaciones`…), entorno, cultura y mente, sociales, cognición (`reacciones`) y ciencia personal (`experimentos`). El detalle campo a campo está en el Apéndice A.

**Unidad de observación.** `momentos` (varios por día, con hora), `gastos` (por transacción), `visitas` (con entrada y salida) y `ubicaciones` (puntos) son **eventos**, no días. El paso de evento a día (sección 2.6) es una fuente de decisiones que afectan los resultados: un día con un solo aviso de ánimo tiene una media tan ruidosa como una observación única.

---

## 2.6 El panel diario y las reglas de relleno

`Estadistica.panel()` devuelve un mapa `fecha → (id de variable → Double?)`. Cada celda tiene **tres estados** posibles (Figura 2.1):

| Estado | Significado | Representación |
|---|---|---|
| **Valor** | Se observó y vale *x* | número |
| **Cero estructural** | Se sabe que *podría haberse registrado* y no ocurrió | `0.0` |
| **Sin dato** | No hay información | `null` (vacío en CSV, `NA` en R) |

![Figura 2.1](figuras/fig02_estructura_panel.png)

*Figura 2.1. Panel diario sintético de 30 días con los tres estados posibles de cada celda: sin dato (null), cero estructural y valor observado. El gasto vale 0 solo desde el primer gasto y en días con diario (`Estadistica.ceroDesde`). Script: `figuras/fig02_estructura_panel.py`.*

### 2.6.1 La regla general

Una fila se crea para cada fecha que aparece en *alguna* fuente. Una variable recibe `null` si su fuente no tiene registro ese día, salvo en los casos de **relleno con cero**. Desde la entrega 10c la regla es una sola, `Estadistica.ceroDesde(fuente, base)`: devuelve los días de `base` (los que pudieron responderse) iguales o posteriores al primer registro de la fuente. En esos días «no hubo» vale 0; antes del primer registro, o fuera de la base, la celda queda vacía. Las variantes principales:

| Variables | Pueden valer 0… |
|---|---|
| `gasto_total`, `gasto_imprevisto`, `n_impulso`, `escalada`, `gimnasio`; emociones `n_*`, `registros_animo`, `ingreso_total` y otras | desde el primer registro de su fuente, en días con diario (en algunas, o con datos de esa fuente) |
| Hábitos `hab_<id>` | desde `Habito.desde` (el día en que se creó el hábito) |
| `sueno_recordado` | solo si ese día se dijo algo del sueño |
| Variables sociales (`personas`, `vi_*`, `per_`, `pen_`…) | solo en días en que se pudo nombrar a alguien (`Dinamicas.diasSociales`); desde 10e, `personas` cuenta a quien nombras en el diario y en los momentos no privados (D.93) |
| `ctx_*`, `dia_dificil`, `palabras`, `hitos_n` y otras | en todos los días con diario |
| Pasos, sueño del reloj, pantalla, clima, HRV… | nunca: sin lectura no hay dato |

La lista completa está en la versión completa, §2.6.1.

**Razonamiento científico.** Es una aplicación de la teoría de datos faltantes (Rubin, 1976): un dato puede faltar *completamente al azar* (MCAR), *al azar según lo observado* (MAR) o *no al azar* (MNAR, cuando la razón por la que falta depende del propio dato). Rellenar con cero es válido solo si el cero es el valor *verdadero*. Como los días sin anotar no son al azar (suelen ser los peores o los más ocupados), un cero falso no solo baja la media: sesga las correlaciones. **[Validado]** la distinción cero/nulo; **[Aproximación]** la heurística «desde el primer registro», que supone que el uso de la función es continuo desde que se empieza.

*Ejemplo sintético.* Con diario desde el día 1 y el primer gasto el día 40, `gasto_total` queda vacío los días 1 a 39; un día 41 con diario y sin gasto vale 0, y un día 42 sin documento de diario, vacío. Rellenar con 0 desde el día 1 bajaría la media y podría crear una correlación falsa con cualquier variable que *empieza* a registrarse más tarde, producto solo del calendario de adopción.

**Cuándo falla.** (a) «Desde el primer registro» no distingue *abandono de la función* de *ausencia real*: si se deja de registrar emociones tres meses, esos meses quedan en 0 (H-1-1, vigente para el abandono; la inconsistencia entre fuentes que también señalaba quedó resuelta en 10c). (b) La base «días con diario» supone que, si abriste el diario, pudiste anotar un gasto; un día en que solo marcaste la valencia cuenta como «sin gasto». (c) Los hábitos creados antes de que existiera `Habito.desde` siguen en 0 desde el principio. Desde la entrega 10d, `vm_panel.csv` usa estas mismas reglas (sección 2.8).

### 2.6.2 Agregación de eventos a día y otras variables

La operación sigue la naturaleza física de la variable: las cantidades extensivas (minutos, gastos, pasos) se **suman**; los estados intensivos (valencia) se **promedian**; los eventos raros se **cuentan** o se marcan 0/1. Así, `muestreo_valencia` es la media de los avisos del día (`null` si no hay avisos); `escalada_grado` toma el máximo, porque el desempeño en escalada se evalúa por su pico; `aire_libre_min` toma el máximo entre la luz alta del teléfono y el tiempo a pie, corriendo o en bici (dos aproximaciones a lo mismo, **[Aproximación]**). `gasto_imprevisto` es **binaria** (¿hubo algún gasto imprevisto?); desde 10e, el monto va en `gasto_imprevisto_monto`, con la misma regla de 0 que `gasto_total` (H-1-5, resuelto, D.94). `error_prediccion_signo` (desde 10e) es la valencia de hoy menos la predicción de ayer (capítulo 3). **[Diseño propio]** cada operador.

Las escalas del diario (valencia y activación, 1–7) son **ordinales**: la distancia entre 3 y 4 no es necesariamente la de 6 a 7; los análisis usan métodos de rangos (Spearman, capítulo 6) cuando la variable es ordinal. Algunos códigos se vuelven números que solo preservan el orden: `sueno_lucidez` (no = 0, parcial = 1, total = 2). `luna_ilum` se calcula con una fórmula astronómica para cada fecha: es **exógena y completa**, útil como control negativo (si aparece una asociación con la luna, probablemente es ruido o multiplicidad).

---

## 2.7 Familias de variables dinámicas

Hay **187** variables fijas desde 10f (cada identificador que `Estadistica.variables()` escribe con su nombre; 186 en la v91 y 184 en la v90): 130 existen siempre y 57 solo si hay datos de su fuente. Las de 10e son `error_prediccion_signo` y `gasto_imprevisto_monto` (D.94); la de 10f, `fuerza_ref_kg`, la mejor 1RM estimada del día en press inclinado con mancuernas (D.114; capítulo 8). Siete son privadas: la app no las cruza y van a R. Además, el sistema crea **variables dinámicas** por cada elemento que la persona usa: una por persona (`per_*`, `pen_*`), tema, lugar, app, categoría de gasto, artista, ciudad, nutriente… (27 prefijos, Apéndice A), y también hábitos, emociones usadas y campañas.

- **Análisis:** entran solo las más frecuentes de cada familia, con un tope y un mínimo de días (por ejemplo, `per_`: máx. 15, mín. 5 días; `Dinamicas.familia`).
- **Exportación extendida (`vm_panel_extendido.csv`):** todas las que aparecen en al menos 2 días, hasta 400 por familia.

*Razonamiento científico.* Con *p* variables hay *p(p−1)/2* pares posibles; crecer sin límite dispararía la **multiplicidad** (con muchas pruebas, algunas salen «significativas» por azar). El filtro por frecuencia la limita, pero elige mirando los mismos datos, así que la corrección de falsos descubrimientos del capítulo 6 es imprescindible. **[Aproximación]**.

El mapa `AGREGADOS` (en `ciencia/Dinamicas.kt`, usado por `Estadistica.obvia`) evita comparar variables **obviamente relacionadas** (`app_instagram` contra `pantalla_min`: una es parte de la otra), y `ENTRE_SI` evita pares dentro de familias que salen de lo mismo (un nutriente y un alimento, una ciudad y su país). Una correlación entre parte y todo es una tautología, no un hallazgo. **[Validado]** el principio; **[Diseño propio]** los pares concretos.

*Ejemplo.* Si la persona menciona `@ana` en 12 de 60 días, existe `per_ana` (0/1 por día); `Texto.canon` unifica «@Ana», «@ana_m» y los apodos declarados. Una mención en el diario cuenta como **«estuve con»** por omisión, pero una mención no es presencia (H-1-6): desde la entrega 10c, la marca de día revisado entra al panel como `presencias_revisado` (0/1), para comparar días revisados y sin revisar (capítulo 7).

---

## 2.8 Exportación, diccionario y R

La app exporta **51 tablas CSV** de datos (`Exportar.tablas()`, Apéndice A), más el diccionario `vm_diccionario.csv`, `LEEME.txt`, respaldos y el script `vestigium.R`. Las principales son `vm_panel.csv` (una fila por día; tabla de compatibilidad para scripts viejos), `vm_panel_extendido.csv` (el panel de la app más todas las dinámicas con al menos 2 días; el que analiza R) y las tablas por evento (`vm_momentos`, `vm_gastos`…).

**Principio tidy** (ordenado). Cada variable es una columna, cada observación una fila y cada tipo de unidad observacional una tabla (Wickham, 2014): `vm_panel` (unidad = día) y `vm_gastos` (unidad = transacción) conviven sin mezclar granularidades. El diccionario sirve a la interoperabilidad y reutilización (FAIR: *Findable, Accessible, Interoperable, Reusable*; Wilkinson et al., 2016). **[Validado]**. Desde 10c describe cada columna por archivo, para que columnas homónimas de tablas distintas no compartan descripción (H-9-20).

**R analiza el mismo panel que la app (entrega 10d).** Antes, `vm_panel.csv` tenía reglas propias (por ejemplo, `gasto_total` valía 0 en cualquier fecha) y R no servía para comprobar la app (H-5-1). Ahora:

1. `Exportar.comoLaApp` pone en cada columna de `vm_panel.csv` que también existe en `Estadistica.panel()` el valor de la app, con sus reglas de 0 y vacío, conservando el tipo de la columna. **[Diseño propio]**.
2. Once columnas tienen otro nombre en `vm_panel.csv` (por ejemplo, `km` es `distancia_km`); no se renombraron para no romper scripts viejos, y el diccionario dice la equivalencia (`Exportar.EQUIVALENTES`).
3. `vestigium.R` carga `vm_panel_extendido.csv` como `panel`, y `vm_relaciones()` y `vm_factores()` cruzan por omisión las variables que cruza la app (`vm_variables.csv`) y esconden las mismas parejas obvias (`vm_familias.csv`).

Una prueba JVM (`ExportacionPrueba`) comprueba con datos inventados que la app da los mismos números que R, y con una exportación real el autor comprobó que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026).

**Desde qué versión rige cada columna (desde 10f, D.115; H-1-8).** Hasta 10f, el diccionario describía el *significado* pero no versionaba cada variable: si una definición cambiaba, solo reflejaba la actual, y una exportación antigua podía no ser comparable. Ahora `vm_diccionario.csv` trae `desde_version` y `cambio` (con su fila del Apéndice D; vacío = sin cambios desde la v84). *Cuándo falla:* en el código revisado (commit `2b262c5`) la tabla que las llena, `Diccionario.DESDE`, está vacía, así que salen vacías en todas las filas. Desde 10e, el diccionario dice los parámetros con que se calcularon las variables de modelos (vida media, carga y lavado; D.99), y el LEEME dice «esquema 13» y «esquema 14», no «v13» y «v14», y dónde está el esquema de cada día (`schema_v` de `vm_dias.csv`; D.107).

---

## 2.9 Integridad, respaldo e importación

- **Transacciones.** Las importaciones masivas (`importarDias`) usan `db.transaccion { }`: se aplican todas o ninguna, y por omisión no pisan días existentes.
- **Importar sin inventar.** Desde la entrega 10d, los días importados guardan los sellos de tiempo que traiga el archivo, o los dejan vacíos, y `origen` dice de dónde vinieron (`web`, `obsidian`). Sigue sin haber una marca de «importado el día X» (H-1-9, resuelto en parte).
- **Restaurar.** Un respaldo con `schema_v` mayor que el de la app no se restaura, porque los campos desconocidos se descartarían en silencio; los gastos e ingresos que ya existen (mismo id) no se tocan (capítulo 10).
- **Identificadores.** `nuevoId()` = UUID recortado a 16 caracteres hexadecimales (64 bits): con 10⁵ registros, la probabilidad de al menos una colisión es ≈ 3×10⁻¹⁰.

---

## 2.10 Amenazas a la validez

| # | Amenaza | Qué hace hoy el sistema |
|---|---|---|
| L-1 | Día lógico fijo (05:00) | Corte único; no se adapta a la persona |
| L-2 | Cero frente a nulo | Una sola regla, `ceroDesde`; el abandono sigue contando como 0 |
| L-3 | Autoselección del registro | `retroactivo`; `con_diario` en `vm_panel.csv` |
| L-4 | Medición reactiva (medirse cambia lo medido) | Inevitable; documentar periodos de adaptación |
| L-5 | Cambios de instrumento (también viajes y cambios de horario) | `schema_v`; `ventana_pantalla` y `ventana_pasos`; desde el esquema 15, `cambio_horario` |
| L-6 | Multiplicidad por variables dinámicas | Filtros por frecuencia; corrección en el capítulo 6 |
| L-7 | Autocorrelación (días seguidos no son independientes) | Tratada en el capítulo 6 |
| L-8 | Generalización (un solo sujeto) | Los resultados describen a esa persona |

---

## 2.11 En resumen

1. La app conserva el origen de cada dato (activo, pasivo o importado), y así puede comparar autorreporte con medición objetiva.
2. El **día lógico** (corte 05:00) asigna los eventos nocturnos al día vivido; desde 10f, cada instante se lee con la zona que tenía el teléfono entonces. Pantalla y pasos lo usan desde el esquema 13 (la serie se parte en dos tramos); otras variables siguen por día de calendario, y la exportación lo declara.
3. El almacén (SQLite + documentos JSON + `Repo` con escritura inmediata) privilegia flexibilidad y pérdida mínima de datos; su lectura tolerante puede convertir un «sin dato» en un valor por omisión (H-1-4).
4. El panel distingue **valor, cero estructural y sin dato**; desde la entrega 10c hay una sola regla, `ceroDesde`: 0 desde el primer registro de la fuente y solo en los días que se pudieron responder.
5. Las variables dinámicas (una por persona, lugar, tema…) exigen control de multiplicidad.
6. La exportación sigue principios tidy y FAIR, y desde la entrega 10d R analiza el mismo panel que la app.
7. El módulo 1 anotó 9 hallazgos (H-1-1 a H-1-9). El módulo 10 resolvió H-1-1 (salvo el abandono de una función), H-1-2, H-1-3 y H-1-6, y en parte H-1-9; la entrega 10e, H-1-5, y la 10f, H-1-7 y H-1-8. Sigue abierto solo H-1-4.
