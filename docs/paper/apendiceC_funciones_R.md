# Apéndice C. Funciones de R (`vestigium.R`)

_El script `app/src/main/assets/vestigium.R` viaja con cada exportación. Se pone en la misma carpeta que los `vm_*.csv` y se ejecuta con `source("vestigium.R")`. Requiere tidyverse; algunas funciones piden además `nlme`, `igraph`, `stringi`, `DBI` y `RSQLite`. Versión descrita: v81 (commit `fa527df`), con los cambios de la entrega 10c (commit `b943ec7`) en `vm_ritmo` y `vm_red`. La entrega 10c corrigió varias herramientas **de la app** (t de Student, autocorrelación, tendencia en «Todo lo que se mueve», ceros); las funciones de R equivalentes se alinean en la entrega 10d (P-30 a P-33), así que mientras tanto hay diferencias nuevas, señaladas en cada función. Para cada función: qué hace, cómo y en qué se diferencia de la app. Las fórmulas están en el Apéndice B._

---

## C.1 Carga

| Función u objeto | Qué hace |
|---|---|
| `vm_carpeta`, `vm_zona` | Carpeta de los CSV (la del script) y zona horaria (la del sistema; si es UTC, `America/Mexico_City`). Se pueden definir antes de `source()`. |
| `vm_dic` | Lee `vm_diccionario.csv` (archivo, columna, tipo, descripción). |
| `vm_leer(archivo)` | Lee un CSV con el tipo de cada columna según el diccionario: fechas, horas, números, lógicos (1 → `TRUE`), fecha-hora ISO 8601 convertida a `vm_zona`, enteros. Avisa si hubo celdas ilegibles. |
| `vm_fecha_hora(x)` | Convierte texto ISO con desfase a fecha-hora en `vm_zona`; agrega «:00» cuando Java omitió los segundos. |
| `vm_lista(x)`, `vm_pares(datos, columna)` | Separan columnas «a\|b\|c» y «clave:n\|clave:n». |
| `vm` y objetos sueltos | `vm` es la lista de todas las tablas; cada una queda también como objeto (`panel`, `dias`, `momentos`…). Desde el esquema 13 incluye `personas` (`vm_personas.csv`) y `fechas_propias` (`vm_fechas_propias.csv`). |
| `panel` (enriquecido) | `vm_panel.csv` ordenado, **completado a todos los días del calendario** (`complete()`), con `dia_semana`, `fin_de_semana`, `semana`, `mes`, `valencia_ayer`, `valencia_manana` y `sueno_h = sueno_min/60`. |
| `vm_describir(archivo)`, `vm_resumen()` | Diccionario de una tabla; resumen de filas, columnas y rangos de fechas. |
| `vm_leer_sqlite(ruta)` | Lee `vm_datos.sqlite` con los mismos tipos. |

**Diferencia clave con la app (H-5-1).** `vm_panel.csv` lo arma `Exportar.filasPanel()`, no `Estadistica.panel()`: otros nombres (`sueno_min`, `km`, `hrv_rmssd`, `valencia_muestreo`, `luz_dia_min`…), otras reglas de ceros (desde 10c la app usa `ceroDesde`; el CSV todavía no) y menos variables (sección 6.3.4). Desde 10c trae `festivo` y `dia_libre` junto a `dia_semana`. La regla de +24 de `ultimo_uso_h` (antes de las 5 en la app, antes de las 12 en R) también sigue distinta. Todo esto se alinea en la entrega 10d (P-30). Las variables dinámicas están en `vm_panel_extendido.csv`, que el script **no carga** en `vm_archivos`.

---

## C.2 Funciones de análisis

### `vm_tendencia(var = "valencia", datos = panel)`
Puntos diarios con medias móviles de 7 y 30 días (`vm_movil`, B.17). **Igual que la app** (`Analisis.movil`), salvo que la app solo dibuja el último año.

### `vm_movil(x, k)`
Media hacia atrás de k renglones; como el panel está completado a todos los días, k renglones = k días. Exige max(2, k %/% 3) valores. **Igual que la app.**

### `vm_semana(var, datos)`
Media, n, EE = DE/√n e intervalo ±1.96 EE por día de la semana; gráfica de barras. **Diferencia desde 10c:** la app pide 5 valores por día de la semana y usa t con n − 1 grados (con 5 valores, ±2.776 EE); R no tiene mínimo (con 1 valor, DE = NA) y usa 1.96. Pendiente para 10d.

### `vm_cruce(x, y, desfase = 0, datos, metodo = "spearman")`
`cor.test` entre x de hoy e y de `desfase` días después (con `lead` sobre el panel completado: días de calendario); exige 5 días; dispersión con recta y banda de `lm`. **Diferencia:** por omisión es **Spearman**; la app muestra Pearson y Spearman pero razona con Pearson (H-5-7). Desde 10c la app da el intervalo con el n efectivo y muestra la r sin tendencia; R usa el n bruto y los valores tal cual. Pendiente para 10d.

### `vm_familias`, `vm_obvia(a, b)`
20 conjuntos de columnas «obvias» (desde el esquema 13, también los avisos de ánimo: `avisos_*`, `tasa_respuesta`, `valencia_muestreo` y `n_muestreos`) y la regla de que todas las `m_*` y `n_momentos` lo son entre sí. Desde el esquema 13 hay una regla más, la de las campañas: cada `campana_<reto>` es obvia con lo que ese reto busca mover (`vm_campana_mueve`, con los nombres de `vm_panel.csv`) y con `campanas_n`, y `campana_hab_<id>` con su `hab_<id>` (capítulo 9). **Diferencia (H-5-2):** la app tiene 57 conjuntos en `Estadistica.FAMILIAS` más reglas por grupo, por prefijo dinámico, por suplemento, por persona y por cuestionario (sección 6.9). Faltan en R, entre otras, las familias de valencia (`valencia`, `animo_valencia`, `animo_cierre_valencia`), de activación, de alcohol, de cafeína, de personas (`cercanos`, `vi_*`, `personas_*`, `penso_en_*`), de horas por tipo de lugar, de cultura, de mundo, de presión y de sentimientos con `registros_animo`.

### `vm_relaciones(sin_tendencia = TRUE, obvias = FALSE, min_n = 14, ventana = 10, datos = panel)`
Todas las parejas de columnas numéricas o lógicas del panel (excepto `fecha`, `semana`, `mes`, `dia_semana`, `valencia_ayer`, `valencia_manana`, `con_diario`, `n_muestreos`).
- `quitar()`: resto de ±`ventana` días con al menos 3 valores (B.3).
- `ac1()`: autocorrelación de días consecutivos con ≥ 10 pares, recortada a [−0.5, 0.97] (B.4).
- lenta: var > 0, ≥ `min_n` restos, var(resto)/var < 0.12.
- Por pareja: n ≥ `min_n`; 0/1 con ≥ 4 de cada valor; r de Pearson; `bruta`; n_ef acotado a [4, n] (B.5); intervalo de Fisher con n_ef; p = 2·pnorm(−|z/EE|).
- `q = p.adjust(p, "BH")`, `firme = q < .1`, orden por «seguro» y |r|.

**Coincidencias con la app:** el algoritmo es el mismo, cota por cota (`Estadistica.relaciones`). **Diferencias:** (1) la tabla (`vm_panel.csv` frente al panel de la app); (2) la lista de obvias; (3) con `obvias = FALSE` las obvias **no se calculan** y BH se hace solo entre las demás (como `q` en la app); con `obvias = TRUE` BH incluye todas (como `qConObvias`); (4) R incluye columnas que la app no compara (`fin_de_semana`, `sueno_h` y `sueno_min` a la vez, `gimnasio_min`, `sms`, `n_momentos`, `m_*`…); (5) no hay modo por semana; (6) el p es exacto (`pnorm`), en la app es la aproximación de Abramowitz y Stegun (diferencia < 10⁻⁶).

### `vm_factores(objetivo = "valencia", desfase = 0, min_n = 14, datos = panel)`
Cada columna contra el objetivo (con desfase de `desfase` días):
- 0/1 (con ≥ 5 de cada valor): **prueba t de Welch** (`t.test`), efecto, intervalo y p de la t.
- Continua: **Spearman** con `cor.test(exact = FALSE)` (p por aproximación t); intervalo de Fisher con 1.96/√(n − 3).
- Exige ≥ `min_n` pares y al menos 2 valores distintos; excluye las columnas de la familia del objetivo en `vm_familias`; BH, `firme = q < .1`; une la descripción del diccionario.

**Diferencias con `Analisis.factores`:** desde 10c la app quita la tendencia (con su interruptor, encendido por omisión), usa **r de Pearson** con el n efectivo para las continuas y, para 0/1, t de Welch con el error estándar corregido por autocorrelación (apéndice B, B.11 y B.11a); pide 10 días en total (5 y 5) y excluye con `FAMILIAS` y `obvia()`. R sigue con los valores tal cual, Spearman con n bruto y Welch sin corrección: con variables persistentes, sus p son más pequeños que los de la app (H-5-3, resuelto en la app; R pendiente para 10d, P-32).

### `vm_modelo(objetivo, predictores = c("sueno_h", "pasos", "pantalla_min"), ayer = TRUE, datos)`
Agrega `.ayer` = el objetivo del día anterior (`lag` sobre el panel completado: día de calendario anterior), deja los días completos, estandariza todo y ajusta `lm` (con intercepto, que vale 0 al estar centrado). Exige n ≥ número de predictores + 10. Imprime `summary`. **Mismos β e intervalos que `Analisis.modelo`** desde 10c: los dos usan la distribución t con n − k − 1 grados de libertad (antes la app usaba 1.96). `summary` muestra el R² y el R² ajustado; la app muestra el ajustado.

### `vm_modelo_ar1(objetivo, predictores, datos)`
Mismo modelo sin el día anterior, ajustado con `nlme::gls(…, correlation = corAR1(form = ~ t), method = "ML")`, con t = días desde el primero. Los errores siguen un proceso AR(1): el parecido entre dos días separados por h días es φ^h. Comprobado en el código de nlme (v3.1-171): si la covariable entera tiene saltos, `corAR1` se convierte internamente en `corARMA(p = 1, q = 0)` y usa la distancia real en días, así que **los días faltantes se respetan**. No existe en la app. Es la opción más defendible para inferencia sobre efectos de un día (Hamaker y Wichers, 2017), aunque no quita tendencias ni ciclos semanales.

### `vm_dinamica(var = "valencia", datos)`
n, media, DE, MSSD e inercia (con `lead` sobre el panel completado: solo pares de días consecutivos), en total y por mes. **Diferencias con `Analisis.dinamica`:** sirve para cualquier variable; no exige mínimos (la app: 7 días, 3 pares para MSSD, 10 para inercia); por mes calcula también la inercia, y el último día de un mes se empareja con el primero del siguiente (la app solo empareja dentro del mes); no calcula días difíciles, rachas ni la DE intradía.

### `vm_ritmo(dias = 56)`
Desde `vm_salud`: hora de dormir (madrugada + 24), despertar (+ 24), duración, punto medio; filtra 2 < duración < 16 h; desde 10c usa solo las noches de los últimos `dias` días hasta la última fecha de `vm_salud` (`dias = Inf` para toda la historia); agrupa en noches antes de día libre y laborales: una noche es libre si el día en que despiertas tiene `dia_libre` en el panel (sábado, domingo o, si vives en México, festivo; `Fechas.esDiaLibre`), y si el panel no trae esa columna (exportaciones viejas), si cae en sábado o domingo; imprime el jet lag social (diferencia absoluta de los puntos medios) y la DE de la hora de dormir. **Como `Analisis.ritmo` con reloj** (misma ventana y misma regla de día libre desde 10c); R no aproxima con el teléfono ni exige mínimos.

### `vm_palabras(min_dias = 5)`
Tokens de 3+ letras de las notas, sin una lista de ~110 palabras vacías (comparación con acentos), un conteo por día; para cada palabra con ≥ `min_dias` días con valencia: media y diferencia con la media de la valencia de **todos** los días de `vm_dias`. **Diferencias (H-5-10):** la app usa ~170 vacías comparadas sin acentos y su referencia es la media de los días **con nota**; la app muestra solo los 15 extremos.

### `vm_atipicos(vars = c("valencia", "sueno_h", "pasos", "pantalla_min", "gasto_total", "notificaciones", "desbloqueos", "km"), z = 3, datos)`
z robusto con `median` y `mad` (constante 1.4826) por variable; devuelve los días con |z| ≥ 3, del más reciente al más viejo. **Diferencias:** otra lista de variables; sin mínimo de 20 días ni límite de un año; mediana promediada para n par. `km` existe en `vm_panel.csv`; en la app la variable se llama `distancia_km`, y desde 10c la app la revisa (antes pedía `km` y nunca la encontraba; H-5-8, resuelto).

---

## C.3 Funciones descriptivas y temáticas

| Función | Qué hace | Relación con la app |
|---|---|---|
| `vm_totales(desde, hasta, datos)` | Para ~40 columnas sumables (desde el esquema 13, también `avisos_planeados`, `avisos_mostrados` y `avisos_respondidos`): total, días con dato, primera y última fecha con valor > 0, promedio por día con dato. | Como la pantalla Totales, sobre `vm_panel.csv`. |
| `vm_buscar(texto, desde, hasta)` | Busca sin acentos en alimentos, personas («@»), temas («#»), artistas, canciones (≥ 30 s), lugares, ejercicios (sin calentamiento), canales, búsquedas, gastos, libros y chats. | Como el buscador de Totales (sin los sinónimos español-inglés de `Totales.SINONIMOS`). |
| `vm_fuerza(n = 6)` | 1RM estimado máximo por sesión de los n ejercicios más repetidos. | Capítulo 5. |
| `vm_mapa_animo()` | Registros de ánimo (`es_animo`) con coordenadas y su propia valencia, coloreados por ella. Desde el módulo 10a excluye impulso, recaída y libido y ya no rellena con la valencia del diario. | H-2-15 (parte de R) y H-9-12, resueltos. |
| `vm_por_hora(var)` | Promedio por hora (0–23) de una columna de `vm_horas.csv`, entre semana y fin de semana. | No hay equivalente en pantalla. |
| `vm_cruce_horas(x, y, desfase)` | Spearman entre dos columnas de `vm_horas.csv` en la misma hora o `desfase` horas después; exige 10 horas. | Ignora que horas consecutivas se parecen mucho más que días consecutivos: su p es muy optimista. |
| `vm_sustancias()` | Registros, días, nivel máximo medio, horas y cruda por sustancia; cruda contra bebidas. | Descriptiva. |
| `vm_lugares()` | Horas por tipo de lugar por semana; valencia media de los registros de ánimo por tipo de lugar. | Descriptiva. |
| `vm_presion()` | Spearman entre `cambio_presion_3h` y valencia. | Una sola prueba, sin tendencia ni nEf. |
| `vm_retraso(tabla, umbral_min = 60)` | Desde el esquema 14: minutos entre cuándo pasó y cuándo se anotó cada registro (`retraso_min`), si se anotó en otro día lógico (`otro_dia`) y si fue `tarde` (más del umbral u otro día). Sirve para momentos, sustancias, tramos, suplementos, gastos e ingresos. | Descriptiva. El umbral de 60 min es **[Diseño propio]**; los registros de antes del esquema 14 quedan con NA. |
| `vm_red(declaradas, min_juntos)` | Red personal con igraph: densidad, componentes, restricción de Burt, grado, composición. Desde 10c, la composición y el color usan la columna `grupo` de `vm_red_nodos.csv` (familia, pareja, amistad, trabajo…), como la Red de la app; si la exportación no la trae, la relación tal cual. | Capítulo 7. |
| `vm_laptop()` | Horas por categoría de Cold Turkey por semana. | Descriptiva. |
| `vm_guardar_graficas(carpeta)` | Guarda en PNG tendencia y semana de la valencia, ritmo y mapa de ánimo. | — |

---

## C.4 Qué usar en R para conclusiones más firmes

Con lo que ya trae el script, lo más sólido para una pregunta concreta («¿el sueño se asocia con mi ánimo?») es `vm_modelo_ar1()`, que respeta que los días se parecen y los días faltantes. Para explorar «todo con todo», `vm_relaciones()` reproduce la lógica de la app, con las diferencias de tabla y de familias señaladas en H-5-1 y H-5-2. Ninguna función quita el ciclo semanal (H-5-6); en un modelo puede controlarse agregando `fin_de_semana` o `factor(dia_semana)` como predictor.
