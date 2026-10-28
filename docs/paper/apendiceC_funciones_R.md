# Apéndice C. Funciones de R (`vestigium.R`)

_El script `app/src/main/assets/vestigium.R` viaja con cada exportación. Se pone en la misma carpeta que los `vm_*.csv` y se ejecuta con `source("vestigium.R")`. Requiere tidyverse; algunas funciones piden además `nlme`, `igraph`, `stringi`, `DBI` y `RSQLite`. Versión descrita: la de la entrega 10d (commit `035982c`), que alineó R con la app (P-30 a P-32): R analiza el mismo panel que la app, esconde las mismas parejas obvias y repite las correcciones que la entrega 10c (commit `b943ec7`) hizo en la app (t de Student, autocorrelación, tendencia en «Todo lo que se mueve»). **Cómo se comprobó:** en esta sesión se corrió `vestigium.R` con R 4.3.3 sobre CSV sintéticos (todas las funciones de C.2 que cambiaron, más `vm_red`), y la prueba `ExportacionPrueba.kt` fija que `vm_factores` y `vm_relaciones` dan los mismos números que `Analisis.factores` y `Estadistica.relaciones` con los mismos datos inventados (diferencia < 10⁻⁹). R no corre en la compilación automática. Después se corrió con una exportación real de la v89: cargó y analizó sin errores, y así se vio que al panel extendido le faltaban 57 variables de la app (D.87, commit `d4e8e62`). Después, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Para cada función: qué hace, cómo y en qué se diferencia de la app. Las fórmulas están en el Apéndice B._

---

## C.1 Carga

| Función u objeto | Qué hace |
|---|---|
| `vm_carpeta`, `vm_zona` | Carpeta de los CSV (la del script) y zona horaria (la del sistema; si es UTC, `America/Mexico_City`). Se pueden definir antes de `source()`. |
| `vm_dic` | Lee `vm_diccionario.csv` (archivo, columna, tipo, descripción). |
| `vm_leer(archivo)` | Lee un CSV con el tipo de cada columna según el diccionario: fechas, horas, números, lógicos (1 → `TRUE`), fecha-hora ISO 8601 convertida a `vm_zona`, enteros. Avisa si hubo celdas ilegibles. |
| `vm_fecha_hora(x)` | Convierte texto ISO con desfase a fecha-hora en `vm_zona`; agrega «:00» cuando Java omitió los segundos. |
| `vm_lista(x)`, `vm_pares(datos, columna)` | Separan columnas «a\|b\|c» y «clave:n\|clave:n». |
| `vm` y objetos sueltos | `vm` es la lista de todas las tablas; cada una queda también como objeto (`panel`, `dias`, `momentos`…). Desde el esquema 13 incluye `personas` (`vm_personas.csv`) y `fechas_propias` (`vm_fechas_propias.csv`); desde 10d, `panel_extendido` (`vm_panel_extendido.csv`), `variables` (`vm_variables.csv`) y `familias` (`vm_familias.csv`). |
| `panel` (enriquecido) | Desde 10d, **`vm_panel_extendido.csv`** (si la exportación lo trae; si no, `vm_panel.csv`), pasado por `.vm_enriquecer()`: quita filas sin fecha, ordena, **completa todos los días del calendario** (`complete()`) y agrega `dia_semana`, `fin_de_semana`, `semana`, `mes`, `valencia_ayer` y `valencia_manana` (si hay `valencia`) y `sueno_h = sueno_min/60` (si hay `sueno_min`). Todas las funciones de análisis lo usan por omisión. `vm$panel` (de `vm_panel.csv`, enriquecido igual) queda por compatibilidad y lo siguen usando `vm_ritmo` (columna `dia_libre`), `vm_lugares` y `vm_presion`. |
| `.vm_variables(privadas = FALSE)` | Las variables de `vm_variables.csv`: las que la app cruza (`Estadistica.variables(dinamicas = true)`, con las familias dinámicas recortadas como en la app). Con `privadas = TRUE` suma el seguimiento privado, marcado en la columna `privada`, que la app nunca cruza. Devuelve NULL si la exportación no trae ese archivo. |
| `.vm_no_cruzar` | Columnas que describen el calendario o cuánto se registró, no una conducta, y que `vm_relaciones` y `vm_factores` no cruzan nunca (P-32): `fecha`, `semana`, `mes`, `dia_semana`, `fin_de_semana`, `dia_libre`, `festivo`, `valencia_ayer`, `valencia_manana`, `con_diario` y `n_muestreos`. **[Diseño propio]**. |
| `vm_describir(archivo)`, `vm_resumen()` | Diccionario de una tabla; resumen de filas, columnas y rangos de fechas. |
| `vm_leer_sqlite(ruta)` | Lee `vm_datos.sqlite` con los mismos tipos. |

**El panel de R es el de la app (H-5-1, resuelto en 10d; P-30).** Hasta la entrega 10d el script analizaba `vm_panel.csv`, que arma `Exportar.filasPanel()` con otras reglas que `Estadistica.panel()`. Desde 10d:
- `vm_panel_extendido.csv` empieza con las columnas de `vm_panel.csv` y suma todas las del panel de la app que no estén ya (con los nombres de la app: `sueno_horas`, `distancia_km`, `hrv`…), las variables dinámicas y las columnas de las tablas diarias.
- En `vm_panel.csv`, cada columna que **comparte nombre** con el panel de la app lleva **el valor de la app** (`Exportar.comoLaApp`), con sus reglas de 0 y vacío (`ceroDesde`, P-15): por ejemplo, `gasto_total`, `ingreso_total`, `escalada` y `gimnasio` valen 0 desde el primer registro y en días con diario, y antes quedan vacíos; `ejercicio_min` vale 0 en días con datos del reloj y sin sesión. Se conserva el tipo de la columna (0/1, entero o número) para que R la lea igual que antes.
- `ultimo_uso_h` suma 24 **solo antes de las 5:00**, como el día lógico (antes, en el CSV, antes de las 12; H-9-4, P-16). Las horas de dormir (`sueno_inicio_h`, `carga_noche_inicio_h`) siguen sumando 24 antes de las 12.
- `canciones` cuenta, también en la app, solo escuchas de 30 s o más (sin duración registrada, cuenta), que es lo que dice el diccionario (H-9-3).
- **Once columnas** de `vm_panel.csv` tienen otro nombre en la app (`Exportar.EQUIVALENTES`) y no se renombraron, para no romper scripts viejos: `sueno_min` = `sueno_horas` (en minutos), `sueno_profundo_min` = `sueno_profundo`, `sueno_rem_min` = `sueno_rem`, `hrv_rmssd` = `hrv`, `km` = `distancia_km`, `valencia_muestreo` = `muestreo_valencia`, `luz_dia_min` = `luz_horas` (en minutos), `sol_min` = `sol_horas` (en minutos), `carga_noche_inicio_h` = `carga_noche_h`, `temp_piel_delta` = `temp_piel` y `ruido_db_medio` = `ruido_db`. Las ocho de la misma unidad toman el valor de la app; las tres en minutos salen de la misma fuente. El diccionario lo dice en las dos columnas.

Lo que sigue distinto: R completa el calendario con renglones vacíos (no cambia ningún cálculo, porque los vacíos no entran) y el panel extendido trae columnas que la app no cruza (las de las tablas diarias con prefijo, `app_min_*`…); por eso `vm_relaciones` y `vm_factores` se limitan por omisión a las variables de `vm_variables.csv`.

---

## C.2 Funciones de análisis

### `vm_tendencia(var = "valencia", datos = panel)`
Puntos diarios con medias móviles de 7 y 30 días (`vm_movil`, B.17). **Igual que la app** (`Analisis.movil`), salvo que la app solo dibuja el último año.

### `vm_movil(x, k)`
Media hacia atrás de k renglones; como el panel está completado a todos los días, k renglones = k días. Exige max(2, k %/% 3) valores. **Igual que la app.**

### `vm_semana(var, datos)`
Media, n y EE = DE/√n por día de la semana; desde 10d, solo los días de la semana con **5 valores o más** y el intervalo con **t de Student con n − 1 grados** (`qt(.975, n - 1)`; con 5 valores, ±2.776 EE); gráfica de barras. **Igual que `Analisis.porDiaSemana`** (antes R usaba ±1.96 EE sin mínimo).

### `vm_cruce(x, y, desfase = 0, datos, metodo = "spearman")`
`cor.test` entre x de hoy e y de `desfase` días después (con `lead` sobre el panel completado: días de calendario); exige 5 días; dispersión con recta y banda de `lm`. **Diferencia:** por omisión es **Spearman**; la app muestra Pearson y Spearman pero razona con Pearson (H-5-7). Desde 10c la app da el intervalo con el n efectivo y muestra la r sin tendencia; R usa el n bruto y los valores tal cual. La entrega 10d no la cambió (P-32 no la incluía): para una pareja con el n efectivo, usa `vm_relaciones(variables = c(x, y))` o `vm_factores()`.

### `vm_familias.csv`, `vm_obvia(a, b)`
Desde 10d, las parejas obvias vienen de la app (P-31; H-5-2, resuelto). La exportación escribe `vm_familias.csv` con tres columnas (`familia`, `variable`, `lado`), generado por `Estadistica.familias()` a partir de **las mismas reglas de `obvia()`** (sección 6.9.2): las familias de `FAMILIAS`, los cuatro grupos («Lectura del día», «Momentos», «Sentimientos», «Sueños»), lugares, suplementos, sentimientos con `registros_animo`, categorías de sueño con `sueno_*`, cada familia dinámica con sus totales (`AGREGADOS`), las familias obvias entre sí (`ENTRE_SI`), la misma persona (`per_X` y `pen_X`), el mismo cuestionario y las campañas. Se calcula sobre las variables fijas y **todas** las dinámicas del panel extendido.

- **Lado 0:** todas las variables de la familia son obvias entre sí (por ejemplo, los nutrientes fijos).
- **Lados 1 y 2:** cada variable del lado 1 es obvia con cada una del lado 2, pero no con las de su mismo lado. Así se escriben las reglas «en estrella»: `per_ana` es obvia con `personas` y con `palabras`, pero `personas` y `palabras` no lo son por esa regla; una campaña es obvia con lo que busca mover, pero eso que mueve no lo es entre sí.
- Los nombres de `vm_panel.csv` (las once equivalencias de C.1) entran en las mismas familias que su variable de la app, con el mismo lado, y cada pareja forma además una familia propia («igual:…»): `km` con `distancia_km` es obvia, y `km` con `pasos` también.

`.vm_pares_obvios(vars)` arma, una sola vez, las claves «a|b» de las parejas obvias entre `vars`; `vm_obvia(a, b)` dice si una pareja lo es. **Coincide con la app:** `ExportacionPrueba.familiasIgualQueObvia` comprueba, para 45 variables inventadas que tocan todas las reglas, que «comparten familia» da lo mismo que `obvia()` en todos los pares. Si la exportación no trae `vm_familias.csv` (exportaciones viejas o la resumida), `vm_obvia` usa la lista vieja escrita en el script: 20 conjuntos (`vm_familias`), las `m_*` y `n_momentos` entre sí y las campañas con `vm_campana_mueve`, mucho más corta que la de la app.

### `vm_relaciones(sin_tendencia = TRUE, obvias = FALSE, min_n = 14, ventana = 10, privadas = FALSE, variables = NULL, datos = panel)`
Todas las parejas de las variables que la app cruza: las de `vm_variables.csv` (`.vm_variables(privadas)`), en su orden, que existan en el panel y sean numéricas o lógicas, sin las de `.vm_no_cruzar`. Con `privadas = TRUE` entra también el seguimiento privado (la app nunca lo cruza; su pantalla Privado deja las correlaciones para R). Con `variables = c(...)` se eligen otras columnas. Sin `vm_variables.csv`, todas las columnas numéricas o lógicas menos `.vm_no_cruzar`.
- `quitar()`: resto de ±`ventana` días con al menos 3 valores (B.3).
- `ac1()`: autocorrelación de días consecutivos con ≥ 10 pares, recortada a [−0.5, 0.97] (B.4).
- lenta: var > 0, ≥ `min_n` restos, var(resto)/var < 0.12.
- Por pareja: n ≥ `min_n`; 0/1 con ≥ 4 de cada valor; r de Pearson; `bruta`; n_ef acotado a [4, n] (B.5); intervalo de Fisher con n_ef; p = 2·pnorm(−|z/EE|).
- Obvias según `vm_familias.csv`; `q = p.adjust(p, "BH")`, `firme = q < .1`, orden por «seguro» y |r|.

**Coincide con `Estadistica.relaciones`:** el mismo algoritmo, cota por cota, sobre el mismo panel, con las mismas variables y la misma lista de obvias; con `obvias = FALSE` las obvias no se calculan y BH se hace solo entre las demás (como `q` en la app); con `obvias = TRUE`, BH incluye todas (como `qConObvias`). `ExportacionPrueba.relacionesComoR` fija que, con 80 días inventados, r, intervalo, p, q y el orden coinciden (< 10⁻⁹). **Diferencias que quedan:** no hay modo por semana; el p usa `pnorm` exacto y la app la aproximación de Abramowitz y Stegun (diferencia < 10⁻⁶). Que con una exportación real den las mismas primeras parejas es lo que proponía comprobar P-30, y **se comprobó**: el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026).

### `vm_factores(objetivo = "valencia", desfase = 0, sin_tendencia = TRUE, min_n = 14, ventana = 10, privadas = FALSE, variables = NULL, datos = panel)`
Desde 10d es **«Todo lo que se mueve con algo» de la app** (`Analisis.factores`; P-17 y P-32; H-5-3, resuelto también en R). Las piezas comunes, escritas igual que en `ciencia/Estadistica.kt`:
- `.vm_sin_tendencia(f, x, ventana)`: cada día menos el promedio de sus ±`ventana` días de calendario con dato, si hay al menos 3 (`sinTendencia`, B.3).
- `.vm_ar1(f, x)`: autocorrelación de un día al siguiente, solo con días seguidos; NULL con menos de 10 pares (`ar1`, B.4).
- `.vm_dif_dias(fa, ya, fb, yb)`: diferencia de medias entre dos grupos de días con t de Welch y el error estándar de cada grupo multiplicado por √((1 + ρ)/(1 − ρ)), con ρ corregido por sesgo, ρ + (1 + 4ρ)/n (Marriott y Pope, 1954); si un grupo no tiene 10 pares de días seguidos, usa la ρ de los dos juntos (cada uno centrado en su media), y si tampoco, 0; ρ acotada a [0, 0.9]; n efectivo por grupo n(1 − ρ)/(1 + ρ) (mínimo 2); grados de libertad de Welch con esos n, acotados a [1, n_a + n_b − 2]; intervalo con `qt(.975, gl)` y p con `pt` (`diferenciaEnDias`, B.11 y B.11a).

Con eso, contra el objetivo (con `sin_tendencia`, sus restos) del día `f + desfase` (por fecha de calendario):
- La variable necesita al menos 10 días con dato.
- **0/1** (todos sus valores 0 o 1, con ≥ 5 días de cada uno con objetivo): `.vm_dif_dias` entre los días con 1 y con 0. Columnas `efecto`, `lo`, `hi`, `n`, `n_con`, `n_sin`, `p`.
- **Continua** (con `sin_tendencia`, sus restos; ≥ `min_n` pares): r de Pearson; n efectivo n(1 − r_x r_y)/(1 + r_x r_y), con las autocorrelaciones de la variable y del objetivo recortadas a [−0.5, 0.97] y n_ef acotado a [mín(4, n), n]; intervalo de Fisher con n_ef; p normal. Columna `n_ef`.
- Omite el objetivo, las columnas de `.vm_no_cruzar` y las **parejas obvias con el objetivo** según `vm_familias.csv` (sin ese archivo, la familia del objetivo en `vm_familias` y `vm_obvia`). Por omisión, solo las variables de `vm_variables.csv`.
- BH sobre todas las revisadas, `firme = q < .1`, orden por p; une la descripción del diccionario (del panel extendido o de `vm_panel.csv`).

**Coincide con la app:** `ExportacionPrueba.factoresComoR` fija, con 80 días inventados y en las cuatro combinaciones (con y sin tendencia, con y sin desfase), que efecto, intervalo, n, p y q son los de `Analisis.factores` (< 10⁻⁹). Hasta 10d usaba los valores tal cual, Spearman con n bruto y `t.test` sin corrección por autocorrelación.

### `vm_modelo(objetivo, predictores = c("sueno_h", "pasos", "pantalla_min"), ayer = TRUE, datos)`
Agrega `.ayer` = el objetivo del día anterior (`lag` sobre el panel completado: día de calendario anterior), deja los días completos, estandariza todo y ajusta `lm` (con intercepto, que vale 0 al estar centrado). Exige n ≥ número de predictores + 10. Imprime `summary`. **Mismos β e intervalos que `Analisis.modelo`** desde 10c: los dos usan la distribución t con n − k − 1 grados de libertad (antes la app usaba 1.96). `summary` muestra el R² y el R² ajustado; la app muestra el ajustado.

### `vm_modelo_ar1(objetivo, predictores, datos)`
Mismo modelo sin el día anterior, ajustado con `nlme::gls(…, correlation = corAR1(form = ~ t), method = "ML")`, con t = días desde el primero. Los errores siguen un proceso AR(1): el parecido entre dos días separados por h días es φ^h. Comprobado en el código de nlme (v3.1-171): si la covariable entera tiene saltos, `corAR1` se convierte internamente en `corARMA(p = 1, q = 0)` y usa la distancia real en días, así que **los días faltantes se respetan**. No existe en la app. Es la opción más defendible para inferencia sobre efectos de un día (Hamaker y Wichers, 2017), aunque no quita tendencias ni ciclos semanales.

### `vm_dinamica(var = "valencia", datos)`
n, media, DE, MSSD e inercia (con `lead` sobre el panel completado: solo pares de días consecutivos), en total y por mes. **Diferencias con `Analisis.dinamica`:** sirve para cualquier variable; no exige mínimos (la app: 7 días, 3 pares para MSSD, 10 para inercia); por mes calcula también la inercia, y el último día de un mes se empareja con el primero del siguiente (la app solo empareja dentro del mes); no calcula días difíciles, rachas ni la DE intradía.

### `vm_ritmo(dias = 56)`
Desde `vm_salud`: hora de dormir (madrugada + 24), despertar (+ 24), duración, punto medio; filtra 2 < duración < 16 h; desde 10c usa solo las noches de los últimos `dias` días hasta la última fecha de `vm_salud` (`dias = Inf` para toda la historia); agrupa en noches antes de día libre y laborales: una noche es libre si el día en que despiertas tiene `dia_libre` en el panel (sábado, domingo o, si vives en México, festivo; `Fechas.esDiaLibre`), y si el panel no trae esa columna (exportaciones viejas), si cae en sábado o domingo; imprime el jet lag social (diferencia absoluta de los puntos medios) y la DE de la hora de dormir. **Como `Analisis.ritmo` con reloj** (misma ventana y misma regla de día libre desde 10c); R no aproxima con el teléfono ni exige mínimos.

### `vm_palabras(min_dias = 5)`
Tokens de 3+ letras de las notas (en minúsculas), sin las **mismas 167 palabras vacías que la app** (`Analisis.VACIAS`), comparadas **sin acentos** (se quitan con `stringi`, igual que `Analisis.normal`); un conteo por día; para cada palabra con ≥ `min_dias` días con valencia: media y diferencia con la valencia media de los **días con nota**. **Igual que `Analisis.palabras`** desde 10d (P-32; la parte de R de H-5-10); `ExportacionPrueba.palabrasVaciasComoR` comprueba que las dos listas son la misma. Diferencias que quedan: R devuelve todas las palabras ordenadas y la app solo los 15 extremos de cada lado; ninguna de las dos da intervalo ni encoge las diferencias con pocos días (la otra mitad de H-5-10 sigue abierta).

### `vm_atipicos(vars = c("valencia", "sueno_horas", "pasos", "pantalla_min", "desbloqueos", "gasto_total", "notificaciones", "fotos", "distancia_km", "min_hablado", "wa_mios", "musica_min", "fc_reposo", "hrv", "palabras"), z = 3, datos)`
z robusto con `median` y `mad` (constante 1.4826) por variable; devuelve los días con |z| ≥ 3, del más reciente al más viejo. Desde 10d revisa **las mismas 15 variables que `Analisis.atipicos`**, con los nombres de la app (antes eran 8, con `sueno_h` y `km`). **Diferencias que quedan:** sin mínimo de 20 días ni límite de un año; mediana promediada para n par (la app toma la superior); R da una fila por variable y día, la app un día con sus razones.

---

## C.3 Funciones descriptivas y temáticas

| Función | Qué hace | Relación con la app |
|---|---|---|
| `vm_totales(desde, hasta, datos)` | Para ~40 columnas sumables (desde el esquema 13, también `avisos_planeados`, `avisos_mostrados` y `avisos_respondidos`): total, días con dato, primera y última fecha con valor > 0, promedio por día con dato. | Como la pantalla Totales. Desde 10d corre sobre el panel extendido, que trae las mismas columnas de `vm_panel.csv` (ya con los valores de la app). |
| `vm_buscar(texto, desde, hasta)` | Busca sin acentos en alimentos, personas («@»), temas («#»), artistas, canciones (≥ 30 s), lugares, ejercicios (sin calentamiento), canales, búsquedas, gastos, libros y chats. | Como el buscador de Totales (sin los sinónimos español-inglés de `Totales.SINONIMOS`). |
| `vm_fuerza(n = 6)` | 1RM estimado máximo por sesión de los n ejercicios más repetidos. | Capítulo 5. |
| `vm_mapa_animo()` | Registros de ánimo (`es_animo`) con coordenadas y su propia valencia, coloreados por ella. Desde el módulo 10a excluye impulso, recaída y libido y ya no rellena con la valencia del diario. | H-2-15 (parte de R) y H-9-12, resueltos. |
| `vm_por_hora(var)` | Promedio por hora (0–23) de una columna de `vm_horas.csv`, en días laborables y en días libres. Desde 10d, un día es libre si tiene `dia_libre` en `vm$panel` (sábado, domingo o, si vives en México, festivo: la regla de la app, `Fechas.esDiaLibre`); si el panel no trae esa columna (exportaciones viejas), si cae en sábado o domingo. | No hay equivalente en pantalla. |
| `vm_cruce_horas(x, y, desfase)` | Spearman entre dos columnas de `vm_horas.csv` en la misma hora o `desfase` horas después; exige 10 horas. | Ignora que horas consecutivas se parecen mucho más que días consecutivos: su p es muy optimista. |
| `vm_sustancias()` | Registros, días, nivel máximo medio, horas y cruda por sustancia; cruda contra bebidas. | Descriptiva. |
| `vm_lugares()` | Horas por tipo de lugar por semana; valencia media de los registros de ánimo por tipo de lugar. | Descriptiva. |
| `vm_presion()` | Spearman entre `cambio_presion_3h` y valencia. | Una sola prueba, sin tendencia ni nEf. |
| `vm_retraso(tabla, umbral_min = 60)` | Desde el esquema 14: minutos entre cuándo pasó y cuándo se anotó cada registro (`retraso_min`), si se anotó en otro día lógico (`otro_dia`) y si fue `tarde` (más del umbral u otro día). Sirve para momentos, sustancias, tramos, suplementos, gastos e ingresos. | Descriptiva. El umbral de 60 min es **[Diseño propio]**; los registros de antes del esquema 14 quedan con NA. |
| `vm_red(declaradas, min_juntos = 2)` | Red personal con igraph: densidad, grupos, aisladas, restricción de Burt, grado, composición. Desde 10c, la composición y el color usan la columna `grupo` de `vm_red_nodos.csv` (familia, pareja, amistad, trabajo…), como la Red de la app; si la exportación no la trae, la relación tal cual. Desde 10d (P-32; H-6-7 a y b, H-9-23): un par declarado «no se conocen» **nunca** es línea, aunque aparezcan juntas `min_juntos` días; `componentes` cuenta solo los grupos con al menos una línea (`sum(csize > 1)`) y las personas sin ninguna van aparte en `aisladas`; sin ningún par (o sin `vm_red_vinculos.csv`) ya no falla: da una red sin líneas. | Capítulo 7. Igual que `RedPersonas.componentes`. Lo que sigue distinto (H-6-7 c y d, documentado en el diccionario): la exportación trae hasta 400 personas y el `grado` del CSV cuenta pares con 1 día o más juntas; la Red de la app dibuja 45 y pide 2 días. |
| `vm_laptop()` | Horas por categoría de Cold Turkey por semana. | Descriptiva. |
| `vm_guardar_graficas(carpeta)` | Guarda en PNG tendencia y semana de la valencia, ritmo y mapa de ánimo. | — |

---

## C.4 Qué usar en R para conclusiones más firmes

Con lo que ya trae el script, lo más sólido para una pregunta concreta («¿el sueño se asocia con mi ánimo?») es `vm_modelo_ar1()`, que respeta que los días se parecen y los días faltantes. Para explorar «todo con todo», `vm_relaciones()` y `vm_factores()` reproducen desde 10d lo que muestra la app, sobre el mismo panel y con las mismas parejas obvias, así que sirven para verificarla; con `privadas = TRUE` se cruza también el seguimiento privado, que la app no cruza en ninguna pantalla. Ninguna función quita el ciclo semanal (H-5-6); en un modelo puede controlarse agregando `fin_de_semana` o `factor(dia_semana)` como predictor.
