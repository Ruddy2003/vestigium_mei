# Capítulo 6. Análisis estadístico

> **Para qué sirve este capítulo.** Explica **cómo la app pasa de miles de registros a frases como «cuando duermes más, tu ánimo tiende a subir»**: cómo arma la tabla de un renglón por día (el *panel*), cómo compara cada variable con cada otra, qué hace para no dejarse engañar por las tendencias, por los días que se parecen a sus vecinos y por el azar de revisar miles de parejas, y qué hacen las demás herramientas de análisis (modelo múltiple, dinámica emocional, palabras, días atípicos…). Al final compara **función por función** lo que hace la app con lo que hace el script de R que viaja en cada exportación. Todo corresponde a la versión v81 del código (commit `fa527df`). **Los ejemplos numéricos son inventados.**
>
> **Cómo leerlo.** Está escrito para entenderse sin saber estadística, pero sin recortar la precisión. Las cifras, umbrales y fórmulas se dan tal como están en el código, con el nombre de la función de origen; las fórmulas completas están reunidas en el **Apéndice B** y las funciones de R en el **Apéndice C**. Cada decisión se marca como **[Validado]** (respaldada por literatura o un estándar), **[Aproximación]** (razonable, con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app, sin estándar detrás). Los problemas encontrados se numeran H-5-n y se detallan en `hallazgos.md`.
>
> **Archivos leídos.** `ciencia/Estadistica.kt` (completo), `ciencia/Analisis.kt`, `ciencia/Dinamicas.kt`, `ciencia/Periodo.kt`, `ciencia/TusNumeros.kt`, `ciencia/Totales.kt`, `ui/pantallas/Explorar.kt`, `ui/pantallas/Analisis.kt`, `ui/Relacion.kt` (la tarjeta de relación), la parte de `data/Exportar.kt` que arma `vm_panel.csv` y todas las funciones de análisis de `app/src/main/assets/vestigium.R`.

---

## 6.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Variable** | Algo que tiene un número cada día: horas de sueño, valencia, minutos en una app, si mencionaste a una persona (1) o no (0). |
| **Panel** | La tabla con **un renglón por día** y **una columna por variable**. Casi todos los análisis salen de ahí. |
| **Dato faltante (`null`)** | «No sé» para ese día. No es lo mismo que 0: 0 minutos de ejercicio es un dato; no tener reloj ese día es un faltante. |
| **Correlación (r)** | Un número de −1 a 1 que dice qué tanto dos variables suben y bajan juntas. +1: suben juntas siempre en línea recta; 0: ninguna relación lineal; −1: cuando una sube, la otra baja. |
| **Pearson / Spearman** | Dos formas de calcular la correlación. Pearson usa los valores; Spearman usa su **orden** (el día con más sueño es el 1.º, el siguiente el 2.º…), así que no le afectan los valores extremos y capta relaciones que suben sin ser rectas. |
| **Tendencia** | Un cambio lento, de semanas o meses (bajas de peso poco a poco; los días se acortan en otoño). |
| **Quitar la tendencia** | Comparar cada día con sus vecinos en el calendario en vez de con todo tu historial. |
| **Autocorrelación** | Qué tanto el valor de hoy se parece al de ayer. Si duermes poco una semana entera, los días de esa semana se parecen entre sí. |
| **n / n efectivo (nEf)** | n: cuántos días tienen ambos datos. nEf: a cuántos días **independientes** equivalen, una vez descontado que se parecen a sus vecinos. |
| **Intervalo de confianza de 95 %** | El rango de valores compatibles con tus datos. Si va de 0.10 a 0.47, la relación real probablemente está en ese rango; si incluye el 0, podría no haber relación. |
| **p** | La probabilidad de ver una relación así de fuerte **por puro azar** si en realidad no hubiera ninguna. |
| **Comparaciones múltiples** | Revisar muchas parejas a la vez. Con 1000 parejas sin relación, unas 50 tendrán p < 0.05 por azar. |
| **Benjamini-Hochberg (BH) y q** | Una corrección para comparaciones múltiples. q < 0.10 quiere decir, aproximadamente: «de todas las relaciones que marco como firmes, espero que a lo más un 10 % sean azar». |
| **Relación obvia** | Dos variables que salen de la misma fuente o se calculan una de otra (calorías y proteína). Su correlación no dice nada nuevo de ti. |
| **Desfase** | Comparar lo de hoy con lo de **mañana** (¿dormir hoy se asocia con el ánimo de mañana?). |
| **β estandarizado** | En un modelo con varias causas posibles: cuántas desviaciones estándar cambia el objetivo cuando un predictor sube una desviación estándar, con los demás fijos. |
| **Desviación estándar (DE)** | Qué tan lejos del promedio suelen caer los valores. |
| **Mediana / MAD** | La mediana es el valor de en medio. La MAD (desviación absoluta mediana) es la mediana de las distancias a la mediana: una DE que no se deja arrastrar por los valores extremos. |

---

## 6.2 Mapa: qué análisis hay y con qué método

La pantalla **Análisis** (`ui/pantallas/Explorar.kt`) tiene, de arriba abajo (desde la entrega N4 de la revisión de navegación): los experimentos, «Las relaciones más marcadas» (las 5 primeras, con «ver las siguientes»), las herramientas en fichas y, plegadas, «Cruzar dos variables», la cobertura, «¿Qué tan bien te conoces?», «A qué hora aparecen tus emociones», «Ánimo según con quién estás», listas de personas, temas y lugares, y «Antes y después». Desde ahí se abren ocho **herramientas** (`ui/pantallas/Analisis.kt`). La tabla resume qué hace cada una y qué protecciones estadísticas aplica.

| Dónde | Función | Coeficiente o prueba | ¿Quita tendencia? | ¿Corrige por días parecidos? | ¿Corrige por muchas pruebas? | En R |
|---|---|---|---|---|---|---|
| Las relaciones más marcadas | `Estadistica.relaciones` | Pearson | Sí (interruptor, encendido por omisión) | Sí (nEf) | Sí (BH, q < 0.10) | `vm_relaciones` |
| … por semana | `Estadistica.semanal` + `relaciones` | Pearson sobre promedios semanales | Sí (±35 días) | Sí (nEf semanal) | Sí | — |
| Cruzar dos variables | `Estadistica.pares`, `pearson`, `spearman`, `ic95` | Pearson y Spearman; intervalo sobre Pearson | No | No | No aplica (una sola pareja) | `vm_cruce` (Spearman) |
| Todo lo que se mueve con algo | `Analisis.factores` | Spearman (continuas); diferencia de medias (0/1) | No | No | Sí (BH, q < 0.1) | `vm_factores` |
| Modelo múltiple | `Analisis.modelo` | Mínimos cuadrados estandarizados | No | En parte (el día anterior como predictor) | No | `vm_modelo`, `vm_modelo_ar1` |
| Tendencia | `Analisis.movil`, `Analisis.cambio` | Medias móviles; diferencia de medias | — | No | No | `vm_tendencia` |
| Día de la semana | `Analisis.porDiaSemana` | Media ± 1.96 EE | No | No | No | `vm_semana` |
| Dinámica emocional | `Analisis.dinamica` | DE, MSSD, autocorrelación | No | — | — | `vm_dinamica` |
| Ritmo de sueño | `Analisis.ritmo` | Promedios de horas | — | — | — | `vm_ritmo` |
| Palabras del diario | `Analisis.palabras` | Diferencia de medias, sin prueba | No | No | No | `vm_palabras` |
| Días atípicos | `Analisis.atipicos` | z robusto (mediana y MAD) | No | — | — | `vm_atipicos` |
| Antes y después | `Estadistica.diferencia` (en `EtapasDeVida`) | Diferencia de medias, z | No | No | No | — |

Lo importante de la tabla: **solo «Las relaciones más marcadas» aplica las tres protecciones a la vez** (tendencia, días parecidos y muchas pruebas). Las demás herramientas son descriptivas o aplican una sola. Esto no es un error en sí —la app lo dice: «Pistas para entenderte. Ninguna prueba causas»—, pero sí produce contradicciones entre pantallas que se discuten en las secciones 6.12 y 6.13 (H-5-3, H-5-4).

---

## 6.3 El panel diario: la tabla de la que sale todo

### 6.3.1 Qué es

`Estadistica.panel()` devuelve un mapa `fecha → (variable → valor)`. Cada **fecha** es un día lógico (de 5:00 a 4:59 del día siguiente; capítulo 2). Cada **valor** es un número o `null`. Las variables 0/1 se guardan como 0.0 y 1.0.

Qué variables entran en los análisis lo decide otra función, `Estadistica.variables()`: una lista de unas 150 variables fijas (valencia, sueño, pasos, clima…) más las que **solo aparecen si hay datos** (por ejemplo, las de alcohol solo si alguna vez registraste alcohol; las privadas nunca: desde el módulo 10a se excluyen siempre y solo viajan a R) y, con `dinamicas = true`, las **variables dinámicas** de `Dinamicas.calcular()`: una por persona, tema, lugar, app, artista, nutriente, cuestionario, etapa de vida… (sección 6.3.3). El panel puede tener columnas que no están en esa lista; esas no se analizan. **[Diseño propio]**.

### 6.3.2 Cero o faltante: la decisión más delicada del panel

Para cada variable la app decide qué pasa en los días en que no hay registro. Hay tres reglas en el código:

1. **Faltante**: si la fuente no dio nada, `null` (sueño del reloj, clima, pantalla…).
2. **Cero en todos los días con diario**: `gasto_total`, `gasto_imprevisto`, `n_impulso`, `escalada`, `n_madrugada`… valen 0 en cualquier día que tenga diario y no tenga registros.
3. **Cero desde el primer registro**: muchas familias (`registros_animo` y `n_<emoción>` desde el primer registro de ánimo; `enfoque_*` desde el primer tramo de cronómetro; `ingreso_total` desde el primer ingreso; `gimnasio` desde el primer entrenamiento; `obras_dia` desde la primera obra; las de sustancias desde el primer consumo de cada una…). Antes de esa fecha, `null`; después, 0 si ese día no hubo nada.

La regla 3 es la más defendible: antes de empezar a usar una función no se sabe si «no pasó» o «no lo anotaste». La mezcla de las reglas 2 y 3 en funciones parecidas (gastos frente a ingresos) ya se registró en H-1-1. **[Diseño propio]**.

**Ejemplo.** Empiezas a registrar ingresos el 1 de marzo. El 15 de febrero, `ingreso_total` es `null` (no se sabe), pero `gasto_total` es 0 si ese día escribiste el diario. Si comparas «gasto» con «ánimo» usando todo el año, los ceros de enero y febrero cuentan como «no gasté nada», aunque solo significan «todavía no anotaba gastos».

### 6.3.3 Las variables dinámicas: «todo se puede cruzar con todo»

`Dinamicas.construir()` crea **familias** de variables con la función interna `familia(prefijo, …, max, minDias)`:

- Recorre los días, cuenta en cuántos días aparece cada cosa (una persona, una app) y suma su «peso» (minutos, pesos, horas).
- Se queda con las que aparecen al menos **`minDias`** días y, de ellas, con las **`max`** más frecuentes (o con más peso, si `porPeso`).
- En los días «base» de la familia, lo que no aparece vale **0**; fuera de la base, no hay valor.
- En la **exportación extendida** (`extendido = true`) el mínimo baja a **2 días** y el máximo sube a **400** por familia.

| Prefijo | Qué es | Valor | Máx. / mín. días (app) |
|---|---|---|---|
| `per_` | aparece @persona (diario, momentos, sueños, cartas) | 0/1 | 15 / 5 |
| `pen_` | pensé en @persona sin verla | 0/1 | 10 / 3 |
| `tema_`, `hito_` | aparece #tema; hito del día | 0/1 | 12 / 5; 10 / 3 |
| `lug_` | horas en cada lugar marcado | horas | 15 / 3 (por peso) |
| `app_`, `ntf_` | minutos y notificaciones por app | min, n | 12 / 5; 8 / 5 (por peso) |
| `lap_`, `web_` | minutos por app y sitio en la laptop | min | 10 / 4 (por peso) |
| `ytc_`, `art_` | videos por canal; canciones por artista | n | 8 / 5; 10 / 5 |
| `gcat_`, `icat_` | gasto e ingreso por categoría | $ | 15 / 5; 8 / 3 (por peso) |
| `obr_`, `gen_` | cada obra y cada género | 0/1 | 10 / 5 |
| `med_`, `det_` | cada medicamento; cada detonante (privadas: solo en la exportación para R, nunca en las listas de la app) | 0/1 | 15 / 3; 10 / 3 |
| `suenot_` | tipo de sueño | 0/1 | 6 / 3 |
| `ciu_`, `pais_` | horas en cada ciudad y país | horas | 8 / 3; 6 / 3 |
| `musc_`, `act_` | series por músculo; minutos por tipo de movimiento | n, min | 12 / 3; 6 / 3 |
| `nut_`, `ali_` | cada nutriente de MacroFactor; cada alimento | cantidad; 0/1 | 60 / 14; 12 / 5 |
| `esc_` | puntaje de cada cuestionario (copiado a los días de su ventana) | puntaje | todos |
| `vida_` | etapas de vida («Conocerte») | 0/1 o número | todas |
| `pol_` | brújula política, vigente hasta el test siguiente | −100 a 100 | todos |

**Por qué.** El principio de la app es que cualquier cosa registrada pueda compararse con cualquier otra. El tope por familia evita que las relaciones se ahoguen en casos raros (una persona mencionada dos veces). **[Diseño propio]**. El costo es enorme en comparaciones múltiples (sección 6.8).

**Un efecto que conviene conocer.** Las variables de tipo «escalón» (`vida_*`, `pol_*`, y en buena parte `esc_*`) cambian pocas veces al año. Al quitar la tendencia casi no les queda variación de un día a otro, así que la app las clasifica como «lentas» y **no entran** en «Las relaciones más marcadas» con el interruptor encendido (sección 6.5.4). Con el interruptor apagado sí entran, pero entonces quedan expuestas a cualquier otra tendencia que coincida en el tiempo. Dicho de otro modo: **una etapa de vida nunca puede aparecer como relación «sin tendencia»**; para ella están «Antes y después» y el análisis en R (H-5-14).

### 6.3.4 El panel de R no es el mismo panel (H-5-1)

El script de R lee `vm_panel.csv`, que **no** es `Estadistica.panel()`. Lo arma `Exportar.filasPanel()` con otras reglas:

- **Otros nombres** para lo mismo: `sueno_min` (en minutos) frente a `sueno_horas`; `km` frente a `distancia_km`; `hrv_rmssd` frente a `hrv`; `valencia_muestreo` frente a `muestreo_valencia`; `luz_dia_min` frente a `luz_horas`; `carga_noche_inicio_h` frente a `carga_noche_h`; `temp_piel_delta` frente a `temp_piel`; `ruido_db_medio` frente a `ruido_db`.
- **Otros ceros**: `gasto_total` e `ingreso_total` valen 0 en **cualquier** fecha exportada sin registros (no solo con diario ni desde el primer ingreso); `escalada` y `gimnasio` son falso en cualquier fecha exportada; `n_momentos` y `m_<tipo>` cuentan también momentos privados y las `m_<tipo>` están congeladas desde el registro de ánimo nuevo (H-2-13).
- **Otras filas**: solo fechas con diario, salud, pantalla, entorno, momentos, gastos, ingresos, nutrición o entrenos; R además completa el calendario con renglones vacíos (`complete()`).
- **Menos variables**: no trae las dinámicas (están en `vm_panel_extendido.csv`), ni contextos, hábitos, lectura del día, sueños por categoría, enfoque, lugares por nombre, etc. Sí trae, copiadas del panel de la app, unas 50 (`nuevas` en `filasPanel`): cultura, mundo, cercanos, presencias, ánimo de todos los registros, sustancias, cierre del día, barómetro, horas por tipo de lugar y `n_<emoción>`.

Consecuencia: **`vm_relaciones()` en R y «Las relaciones más marcadas» en la app aplican el mismo algoritmo a tablas distintas**, así que no dan la misma lista. Ver la comparación completa en la sección 6.22.

---

## 6.4 La correlación: Pearson y Spearman

### 6.4.1 Qué hace

Ambas responden «¿cuando una sube, la otra tiende a subir (o a bajar)?» con un número de −1 a 1.

### 6.4.2 Cómo lo hace exactamente

**Pearson** (`Estadistica.pearson`, ecuación B.1). Con n ≥ 3 pares:

$$ r = \frac{\sum_i (x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_i (x_i-\bar x)^2\,\sum_i (y_i-\bar y)^2}} $$

Si una de las dos variables no cambia (suma de cuadrados igual a 0), devuelve `null`: no hay relación que calcular. La función interna `r()` de `relaciones` es la misma fórmula, escrita sobre arreglos para ir más rápido.

**Spearman** (`Estadistica.spearman`, B.2): Pearson aplicado a los **rangos**. `Estadistica.rangos` ordena los valores y, si hay empates, a todos les da el **promedio** de los lugares que ocupan (dos valores empatados en los lugares 3.º y 4.º reciben 3.5). Es el tratamiento estándar de empates. **[Validado]**.

**Cómo se nombra la fuerza** (`Estadistica.fuerza`): |r| < 0.1 «prácticamente nula»; < 0.3 «débil»; < 0.5 «moderada»; de 0.5 en adelante «fuerte». Son los cortes convencionales de Cohen (1988) para r (0.1, 0.3 y 0.5). **[Validado]** como convención; Cohen mismo advertía que dependen del campo.

### 6.4.3 Cuál se usa dónde (pista 5 del plan: confirmada, H-5-7)

| Lugar | Coeficiente |
|---|---|
| Las relaciones más marcadas (`relaciones`) | **Pearson** sobre los restos sin tendencia (o los valores, si se apaga el interruptor) |
| Cruzar dos variables | Muestra **Pearson y Spearman**; el texto, la fuerza y el intervalo usan **Pearson** |
| Todo lo que se mueve con algo (`factores`) | **Spearman** para variables continuas; diferencia de medias para 0/1 |
| Dinámica (inercia) | Pearson |
| R: `vm_relaciones` | Pearson (como la app) |
| R: `vm_cruce`, `vm_factores`, `vm_cruce_horas`, `vm_presion` | **Spearman** por omisión |

### 6.4.4 Razonamiento y cuándo falla

Pearson mide relaciones **rectas** y se deja arrastrar por un día extremo (un día con 30 000 pasos pesa mucho). Spearman mide relaciones **que solo suben o solo bajan** (monótonas) y es robusto a extremos, pero con muchas variables 0/1 o con muchos empates pierde información. Usar Pearson sobre los restos sin tendencia es razonable: al restar el promedio de los vecinos, los valores se vuelven más simétricos. El problema no es elegir uno u otro, sino que **la misma pareja puede mostrar números distintos en dos pantallas** (r en la lista, ρ en la ficha de la variable) sin que la app lo explique. **[Aproximación]**.

---

## 6.5 Quitar la tendencia

### 6.5.1 El problema: correlaciones de mentira por tendencias

Si dos cosas cambian lentamente con el tiempo, **parecen relacionadas aunque no tengan nada que ver**. Este problema se conoce desde hace un siglo como «correlaciones sin sentido» entre series de tiempo (Yule, 1926) y, en regresión, como «regresiones espurias» (Granger y Newbold, 1974). **[Validado]**.

**Ejemplo.** En otoño los días se acortan; al mismo tiempo, por otras razones, estás bajando de peso. Si correlacionas «horas de luz» con «peso», sale una correlación positiva fuerte. Pero no es que la luz te haga subir de peso: ambas bajan con los meses y nada más.

### 6.5.2 Cómo lo hace exactamente

`Estadistica.series()` (privada) transforma cada variable antes de compararla:

1. Alinea la variable a las fechas del panel; donde no hay dato, `NaN`.
2. Para cada día *i* con dato, toma **todos los días con dato que estén a 10 días de calendario o menos** de él (de *i* − 10 a *i* + 10, **incluido el propio día**). Esa es la constante `VENTANA = 10`.
3. Si hay al menos **3** de esos días, el **resto** del día es su valor menos el promedio de esa ventana (ecuación B.3):

$$ \text{resto}_i = x_i - \frac{1}{|W_i|}\sum_{j\in W_i} x_j,\qquad W_i=\{j:\ |d_j-d_i|\le 10,\ x_j\ \text{existe}\},\ |W_i|\ge 3 $$

4. Si hay menos de 3, el resto queda vacío (ese día no entra en la comparación sin tendencia).

La ventana es de **días de calendario**, no de registros: si faltaron días, la ventana tiene menos valores, pero no se estira hacia días más lejanos. Cerca del principio y del final de tus datos la ventana queda **cargada hacia un solo lado** (el primer día solo tiene vecinos posteriores). El interruptor dice «Compara cada día con sus ~3 semanas alrededor»: 21 días como máximo. **[Diseño propio]** en la elección de ±10 días; la técnica (restar una media móvil centrada) es un filtro pasa-altas estándar. **[Aproximación]**.

R hace exactamente lo mismo en `vm_relaciones(…, ventana = 10)` con la función interna `quitar()`.

### 6.5.3 Ejemplo trabajado

Valencia de los días 1 a 15 (faltan los días 7 y 13): 5, 4, 6, **3**, 5, 5, —, 4, 6, 5, 4, 5, —, 3, 6.

Para el **día 4** (valor 3): los días con dato a 10 o menos de distancia son del 1 al 14, menos el 7 y el 13: 12 días (el 15 está a 11 días, fuera). Su promedio es 55/12 = **4.583**. Resto del día 4: 3 − 4.583 = **−1.583**. Es decir: el día 4 fue 1.6 puntos peor que sus semanas vecinas, y eso es lo que se compara con las otras variables, no el 3.

### 6.5.4 Variables «lentas»

Si a una variable casi no le queda variación después de quitar la tendencia, compararla «sin tendencia» no tiene sentido. `series()` la marca como **lenta** si se cumplen las tres condiciones:

- su varianza original es mayor que 0 (no es constante);
- tiene al menos **14** restos (al menos **8** en el análisis por semana);
- la varianza de los restos es **menor que el 12 %** de la varianza original: var(resto) / var(x) < 0.12.

Con el interruptor encendido, las lentas **se omiten** y la pantalla lo dice: «Sin tendencia no entran …: casi solo cambian con las semanas». Ejemplos típicos: horas de luz, peso de tendencia de MacroFactor, etapas de vida, brújula política. **[Diseño propio]** (el corte de 0.12 no viene de la literatura).

### 6.5.5 Figura

![Figura 6.2](figuras/fig12_tendencia_espuria.png)

*Figura 6.2. Dos variables sintéticas sin relación día a día que solo comparten un cambio lento: con los valores tal cual, r = −0.36; con los restos de ±10 días, r = +0.01. Script: `figuras/fig12_tendencia_espuria.py` (replica `Estadistica.series`).*

### 6.5.6 Cuándo falla

- **El ciclo de la semana no se quita (H-5-6).** Una ventana de 21 días contiene exactamente tres semanas, así que el promedio de la ventana **borra** cualquier patrón semanal del promedio y lo **deja entero** en los restos. Si dos variables cambian ambas con el fin de semana (duermes más y te sientes mejor el sábado), su correlación sin tendencia puede deberse solo al calendario. A veces eso es justo lo que quieres saber («el fin de semana me hace bien»); otras veces confunde (no es el sueño, es el sábado). La app no ofrece quitar el efecto del día de la semana. **[Aproximación]**.
- **Cambios bruscos.** Si algo cambia de golpe (te mudas), los días cercanos al cambio quedan a medio camino entre «antes» y «después» y sus restos son raros durante ~10 días.
- **Efectos lentos reales.** Si dormir mal **durante semanas** te deprime poco a poco, ese efecto es justo lo que quitar la tendencia borra. Con el interruptor encendido solo se ven efectos **de corto plazo** (de días). Por eso el interruptor existe y por eso la tarjeta avisa cuando la correlación «con tendencia» difiere en 0.2 o más.

---

## 6.6 Los días no son independientes: el número efectivo de días

### 6.6.1 El problema

Las fórmulas clásicas de intervalos y valores p suponen que cada día es una observación **independiente**. Pero tus días se parecen a sus vecinos: una semana de exámenes tiene varios días seguidos con poco sueño **y** mal ánimo. Si cuentas esos 7 días como 7 pruebas independientes, la relación parece más segura de lo que es. En la simulación de la Figura 6.3, con dos series sin relación real y autocorrelación de 0.8, el intervalo ingenuo «encuentra» una relación en el **35 %** de los casos en vez del 5 % esperado.

### 6.6.2 Cómo lo hace exactamente

1. **Autocorrelación de cada variable** (`Estadistica.autocorr`, B.4): Pearson entre el valor de cada día y el del día siguiente, usando **solo pares de días consecutivos** con dato en ambos (en el análisis por semana, semanas consecutivas: `paso = 7`). Si hay **menos de 10 pares**, devuelve **0** (no corrige). Se calcula sobre los restos (con el interruptor encendido) o sobre los valores (apagado), sobre **toda la serie** de la variable.
2. Cada autocorrelación se **recorta** a [−0.5, 0.97].
3. **Días efectivos** (B.5):

$$ n_{Ef} = n\,\frac{1-r_a r_b}{1+r_a r_b},\qquad \text{acotado a } [\min(4,n),\ n] $$

donde *n* son los días con ambos datos y *r_a*, *r_b* las autocorrelaciones de las dos variables. Si nEf ≤ 3, la pareja no se evalúa.

### 6.6.3 Razonamiento científico

Es la aproximación clásica para la correlación entre dos series con autocorrelación de orden 1 (AR(1)), conocida en climatología y ecología (Bretherton et al., 1999; Pyper y Peterman, 1998). La idea: lo que infla la incertidumbre es que **ambas** series sean persistentes; si solo una lo es (*r_b* = 0), no hay inflación. **[Validado]** como aproximación de primer orden. Supone que la dependencia es de tipo AR(1) (que el parecido decae geométricamente con los días) y que las autocorrelaciones se estiman bien.

Las cotas son decisiones propias y prudentes: la cota superior *n* impide que una autocorrelación negativa en una variable y positiva en la otra **aumente** los días efectivos por encima de los reales; el tope de 0.97 evita dividir casi entre cero. **[Diseño propio]**.

### 6.6.4 Ejemplo trabajado

Sueño y valencia, sin tendencia: n = 120 días con ambos; autocorrelación de los restos de sueño *r_a* = 0.4 y de valencia *r_b* = 0.3. Producto 0.12.

nEf = 120 × (1 − 0.12) / (1 + 0.12) = 120 × 0.88 / 1.12 = **94.3** días efectivos.

Como 94.3 es menor que 0.8 × 120 = 96, la tarjeta escribe «120 días (≈ 94 independientes)».

### 6.6.5 Figura

![Figura 6.3](figuras/fig13_n_efectivo.png)

*Figura 6.3. Izquierda: días efectivos según la autocorrelación (la misma en ambas variables) para 30, 90 y 365 días. Derecha: 4000 parejas simuladas de series AR(1) independientes de 90 días; con la autocorrelación real de 0.8, el intervalo con n da falsas alarmas el 35 % de las veces y el de la app, el 4.6 %. Script: `figuras/fig13_n_efectivo.py`.*

### 6.6.6 Cuándo falla (H-5-13)

- **Variables esporádicas**: si una variable tiene menos de 10 pares de días consecutivos, su autocorrelación se toma como 0 y no se corrige nada. En el análisis por semana pasa durante las primeras ~11 semanas.
- **Autocorrelación de toda la serie, no de los días compartidos**: si una variable tiene dos años y la otra dos meses, la autocorrelación de la primera se calcula con los dos años, aunque la comparación use solo los dos meses comunes.
- **Persistencia más larga que un día**: si el parecido entre días dura semanas (no decae geométricamente), la fórmula de primer orden se queda corta. Quitar la tendencia reduce mucho este problema.
- **Solo en esta lista.** La corrección existe solo en «Las relaciones más marcadas». «Cruzar dos variables», «Todo lo que se mueve con algo», «Tendencia» y «Antes y después» tratan los días como independientes (H-5-3, H-5-4, H-5-5).

---

## 6.7 Intervalo de confianza y valor p

### 6.7.1 Cómo lo hace exactamente

Para una correlación *r* con *n* días (o nEf), la app usa la **transformación z de Fisher** (Fisher, 1915) (B.6–B.8):

$$ z=\operatorname{atanh}(r),\qquad EE=\frac{1}{\sqrt{n-3}},\qquad IC_{95}=\big[\tanh(z-1.96\,EE),\ \tanh(z+1.96\,EE)\big] $$

- `Estadistica.ic95(r, n)`: con *n* entero; exige n ≥ 5; recorta *r* a ±0.9999 para que atanh no sea infinito.
- En `relaciones` se usa **nEf** en lugar de *n*, y el valor p bilateral es *p* = 2 · (1 − Φ(|z| / EE)).
- Φ (la distribución normal acumulada) se calcula con `Analisis.phi`, la aproximación 7.1.26 de Abramowitz y Stegun (1964) para la función de error, con error máximo del orden de 10⁻⁷. Para estos usos es exacta en la práctica. **[Validado]**.

### 6.7.2 Por qué

La distribución de *r* es asimétrica cerca de ±1; atanh la vuelve aproximadamente normal con varianza 1/(n − 3). Es el método estándar. **[Validado]**. Usarlo con nEf es la combinación habitual con la corrección de la sección 6.6. **[Aproximación]**.

### 6.7.3 Ejemplo trabajado (continúa el de 6.6.4)

r = 0.30, nEf = 94.29.
z = atanh(0.30) = 0.3095; EE = 1/√(94.29 − 3) = 0.1047.
IC: tanh(0.3095 ± 1.96 × 0.1047) = tanh(0.1044) a tanh(0.5146) = **0.10 a 0.47**.
z/EE = 2.957 → **p = 0.0031**.
Con *n* = 120 sin corregir habría salido IC 0.13 a 0.45 y p = 0.0008: casi cuatro veces «más significativo» de lo que es.

### 6.7.4 Cuándo falla

- Para **Spearman**, la varianza de atanh(ρ) es algo mayor que 1/(n − 3) (Fieller et al., 1957, proponen 1.06/(n − 3)). `factores` y `vm_factores` usan 1/(n − 3) también para ρ: intervalos ligeramente estrechos. **[Aproximación]**.
- Para variables 0/1 con pocos «1», Pearson es una correlación punto-biserial; la aproximación de Fisher es menos precisa. La app exige al menos **4 días de cada valor** para evaluarlas (sección 6.10).

---

## 6.8 Muchas comparaciones a la vez: Benjamini-Hochberg

### 6.8.1 El problema

Con 300 variables hay 300 × 299 / 2 = **44 850** parejas. Si ninguna tuviera relación real, con el umbral clásico p < 0.05 saldrían **unas 2 240 «relaciones» por puro azar**. Revisar «todo contra todo» obliga a corregir.

### 6.8.2 Cómo lo hace exactamente

`Analisis.bh(p)` (B.9): ordena los *m* valores p de menor a mayor, p₍₁₎ ≤ … ≤ p₍ₘ₎, y calcula para cada uno

$$ q_{(k)} = \min_{j\ge k}\ \min\!\Big(1,\ p_{(j)}\,\frac{m}{j}\Big) $$

(recorre de abajo hacia arriba guardando el mínimo, así q nunca baja cuando p sube). Una relación «se sostiene» si **q < 0.10**. En `relaciones` se calculan dos q:

- `q`: corrigiendo **solo entre las parejas no obvias** (es el que se usa por omisión);
- `qConObvias`: corrigiendo entre todas (se usa si activas «Mostrar también las obvias»).

«Tu año» (`TuAnio.kt`) usa un criterio más estricto: q < **0.05**, n ≥ 30 y al menos 40 días en el año.

### 6.8.3 Razonamiento científico

El procedimiento es el de Benjamini y Hochberg (1995). No controla «la probabilidad de equivocarme alguna vez» (eso sería Bonferroni, mucho más estricto) sino la **proporción esperada de falsos descubrimientos** entre lo que se marca como firme (FDR). El valor *q* así calculado es el «valor p ajustado» de BH, cercano al q de Storey y Tibshirani (2003). Es apropiado para exploración: prefiere encontrar varias pistas aceptando que unas pocas sean falsas. **[Validado]**.

**Supuesto.** BH garantiza su control si las pruebas son independientes o tienen cierta dependencia positiva (Benjamini y Yekutieli, 2001). Aquí las pruebas **comparten variables** (la valencia aparece en cientos de parejas), así que están correlacionadas. En la práctica BH suele comportarse bien en esa situación, pero la garantía formal no está demostrada para este caso. **[Aproximación]**.

### 6.8.4 Ejemplo trabajado

Seis parejas con p = 0.0004, 0.003, 0.011, 0.02, 0.04 y 0.30 (m = 6):

| k | p | p · 6 / k | q |
|---|---|---|---|
| 1 | 0.0004 | 0.0024 | 0.0024 |
| 2 | 0.003 | 0.009 | 0.009 |
| 3 | 0.011 | 0.022 | 0.022 |
| 4 | 0.02 | 0.030 | 0.030 |
| 5 | 0.04 | 0.048 | 0.048 |
| 6 | 0.30 | 0.300 | 0.300 |

Las cinco primeras tienen q < 0.10: se sostienen. Con m = 6 la corrección es suave. Con m = 44 850, la mejor pareja necesita p < 0.10/44 850 = 2.2 × 10⁻⁶ para tener q < 0.10 si es la única real; con nEf = 100, eso equivale a |r| ≳ **0.45**. Si hay unas 50 relaciones reales, el umbral de la 50.ª baja a |r| ≳ 0.37. Por eso la app avisa: «Con pocos días es normal que nada se sostenga».

### 6.8.5 Figura

![Figura 6.4](figuras/fig14_benjamini_hochberg.png)

*Figura 6.4. 200 pruebas simuladas (20 relaciones reales con r = 0.35, 180 sin relación, 90 días). Sin corregir: 19 aciertos y 9 falsas alarmas (32 % de lo marcado es falso). Con BH a q < 0.10: 15 aciertos y 1 falsa alarma (6 %). Script: `figuras/fig14_benjamini_hochberg.py` (replica `Analisis.bh`).*

### 6.8.6 Cuándo falla

- **El número de pruebas depende del modo.** Por día o por semana, con o sin tendencia, se prueban parejas distintas (las lentas salen, el mínimo de días cambia), así que la misma pareja puede tener q distinto en cada modo.
- **Excluir las obvias es razonable pero no neutral**: baja *m* y hace la corrección más permisiva. Está bien porque esas parejas no son descubrimientos, pero la lista de obvias debe estar completa y ser la misma en app y R (H-5-2).
- **«Todo lo que se mueve con algo» corrige con p que no descuentan la autocorrelación** (H-5-3): ahí BH trabaja con valores p demasiado pequeños.

---

## 6.9 Las relaciones «obvias» y cómo se excluyen

### 6.9.1 Qué hace

Marca las parejas cuya correlación es casi segura por construcción, para no presentarlas como descubrimientos: calorías con proteína, eventos de agenda con minutos ocupados, pasos con minutos a pie, «minutos en Instagram» con «minutos de pantalla».

### 6.9.2 Cómo lo hace exactamente

`Estadistica.obvia(a, b)` devuelve verdadero si se cumple **cualquiera** de estas reglas:

1. Ambas son del mismo grupo y el grupo es «Lectura del día», «Momentos», «Sentimientos» o «Sueños».
2. Ambas están en una misma familia de `Estadistica.FAMILIAS` (57 conjuntos desde el esquema 13; una variable puede estar en varios). Ejemplos: todos los nutrientes fijos; `valencia` con `muestreo_valencia`; `valencia`, `animo_valencia`, `animo_cierre_valencia`, `momentos_valencia`, `valencia_acompanado` y `valencia_solo`; las de alcohol entre sí; impulsos, recaídas y libido entre sí.
3. Ambas son de lugares (`horas_tipo_*` o `lug_*`).
4. Son del mismo suplemento (`supl_<id>_dosis`, `_tomado`, `_nivel`…).
5. Una es un sentimiento y la otra `registros_animo`; o una es una categoría de sueño y la otra empieza con `sueno_`.
6. Una es dinámica y la otra es uno de sus **totales** (`Dinamicas.AGREGADOS`: `app_*` con `pantalla_min` y `desbloqueos`; `per_*` con `personas`, `cercanos`, `palabras`…).
7. Ambas son dinámicas de familias que se consideran obvias **entre sí** (`Dinamicas.ENTRE_SI`: dos nutrientes, nutriente y alimento, ciudad y país, obra y género, dos músculos, dos artistas, dos etapas de vida, dos ejes políticos…).
8. `per_X` y `pen_X` de la **misma** persona.
9. Dos puntajes del **mismo** cuestionario (`esc_<instrumento>_…`).
10. Desde el esquema 13: una campaña (`campana_<reto>`) y lo que ese reto busca mover (`Campanas.MUEVE`; por ejemplo, la de pasos con `pasos`, `a_pie_min` y `distancia_km`; la de un hábito con su `hab_`), y cualquier campaña con `campanas_n` (capítulo 9).

La familia de los avisos de ánimo, también del esquema 13, entra por la regla 2: `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos` y `tasa_respuesta`, entre sí y con `muestreo_valencia`, `muestreo_activacion` y `n_muestreos` (capítulo 3).

Las obvias se calculan igual pero se esconden (el interruptor «Mostrar también las obvias» las muestra) y no cuentan en la corrección BH por omisión.

### 6.9.3 Razonamiento y cuándo falla

No hay una regla estadística para decidir qué es «obvio»; es una lista curada a mano. **[Diseño propio]**. Dos riesgos: (a) olvidar una familia hace que aparezca como «hallazgo» algo trivial; (b) marcar como obvio algo que no lo es esconde una relación interesante. Un caso discutible: la regla 1 declara obvias **todas** las parejas entre sentimientos (por ejemplo, «registros con tristeza» con «registros con alegría»), aunque su relación sí podría ser informativa (¿las emociones opuestas se excluyen o conviven el mismo día?).

En R, `vm_familias` tiene solo **19** conjuntos y `vm_obvia` no tiene las reglas 1, 3–9 (salvo los `m_*` entre sí). Así, en `vm_relaciones()` aparecen como relaciones fuertes y «firmes» parejas que la app esconde, como `valencia` con `animo_valencia` o `vi_familia` con `cercanos` (H-5-2).

---

## 6.10 «Las relaciones más marcadas», paso a paso

### 6.10.1 Qué hace

Revisa **todas las parejas** de variables y muestra las más sólidas como tarjetas.

### 6.10.2 Cómo lo hace exactamente (`Estadistica.relaciones`)

Con el panel, la lista de variables, el modo (`sinTendencia`), `minN = 14`, `paso = 1` y `ventana = 10`:

1. `series()` prepara cada variable con al menos 3 días: valores, restos (6.5), autocorrelaciones (6.6), si es lenta y si es 0/1.
2. Con el interruptor encendido, aparta las lentas.
3. Para cada pareja (a, b):
   - toma los días en que **ambas** tienen valor (resto o valor, según el modo): *n*;
   - si *n* < **14**, la salta;
   - si una variable es 0/1 y, en esos días, tiene **menos de 4** días de alguno de los dos valores, la salta («una variable 0/1 necesita algunos días de cada tipo para decir algo»);
   - calcula *r* (Pearson sobre restos o valores) y `bruta` (Pearson sobre los valores, en los mismos días);
   - calcula nEf, el intervalo y *p* (6.6 y 6.7);
   - marca si es obvia (6.9).
4. Calcula `q` (entre no obvias) y `qConObvias` (entre todas) con BH.
5. **Ordena** primero por lo «seguro»: qué tan lejos del cero queda el extremo **más cercano** del intervalo (si el intervalo va de 0.10 a 0.47, 0.10; si cruza el cero, 0); a igualdad, por |r|.

### 6.10.3 Qué se muestra (Explorar.kt)

- Un resumen: «Revisé N combinaciones (dejé fuera M obvias)… ya corregido eso, K se sostienen», donde K es el número con q < 0.10. Si hay lentas, las nombra.
- Filtros por tema (`FOCOS`: con tu ánimo, con tu sueño, personas y vida, lugares y viajes, cuerpo y comida, teléfono y social, cultura, dinero). Una pareja entra si **alguna** de sus dos variables es del tema.
- `agrupar(lista, 15)`: junta las parejas **casi repetidas**. Dos parejas son «parecidas» si comparten una variable y la otra de cada una está en la misma familia de `FAMILIAS` (ánimo con horas de sueño y ánimo con sueño REM). Recorre la lista ya ordenada; la primera de cada grupo es la representante y las demás se anotan como «También con «…» (+0.31)» (máximo 3 nombradas y «y N más»). Muestra hasta **15** grupos; deja de buscar parecidas después del elemento 400.
- La **tarjeta** (`ui/Relacion.kt`, `TarjetaRelacion`): dos recuadros con los nombres y un **lazo** entre ellos.
  - Color: verde si r ≥ 0 («suben juntas»), rojo si r < 0.
  - **Grosor**: (1.2 + 4.5 · |r|) dp; un r de 0.42 da 3.09 dp.
  - **Punteado** y con 65 % de opacidad si q ≥ 0.10 («podría ser azar»).
  - Texto de detalle: «N días»; «(≈ nEf independientes)» si nEf < 0.8 · n; la fuerza; «con tendencia: ±0.xx» si el interruptor está encendido y |bruta − r| ≥ 0.2; «obvia»; «podría ser azar».
- Tocar una tarjeta abre «Todo lo que se mueve con algo» con una de las dos variables como objetivo (la que no es la valencia).

![Figura 6.5](figuras/fig15_tarjeta_relacion.png)

*Figura 6.5. Tres tarjetas sintéticas: una relación positiva firme (grosor 3.09 dp), una negativa firme cuya correlación con tendencia era bastante más fuerte, y una que podría ser azar (punteada). Script: `figuras/fig15_tarjeta_relacion.py` (imita `TarjetaRelacion` y `lazo` del tema por omisión; los temas cambian la forma del lazo, no su grosor ni el punteado).*

### 6.10.4 Ejemplo trabajado completo

Sueño (h) y valencia, interruptor encendido. Tras quitar la tendencia hay 120 días con ambos restos. r = 0.30, autocorrelaciones 0.4 y 0.3 → nEf = 94.3; IC 0.10 a 0.47; p = 0.0031 (6.6.4 y 6.7.3). Supón que se probaron m = 2 000 parejas no obvias y que esta queda en el lugar 25 por p: su q es como mucho 0.0031 × 2000 / 25 = 0.25 (puede bajar si alguna pareja de más abajo da un mínimo menor). Con q ≥ 0.10, la tarjeta sale **punteada** y dice «120 días (≈ 94 independientes) · moderada · podría ser azar», aunque su intervalo no incluya el cero. **No es una contradicción**: el intervalo es el de esa pareja sola; q tiene en cuenta que se miraron 2 000.

### 6.10.5 Cuándo falla

- Las tres protecciones solo cubren el azar y las tendencias; **no** cubren **confusores** (una tercera cosa que mueve a las dos, como el día de la semana o estar enfermo) ni la **dirección** (¿el sueño mejora el ánimo o el buen ánimo te deja dormir?). La app lo dice en «Cruzar dos variables».
- El mínimo de 14 días con 14 restos es bajo; con 14 días solo se ven correlaciones muy grandes (sección 6.21).
- Las variables privadas **no** entran en esta lista ni en ningún análisis de la app desde el módulo 10a (`Estadistica.variables()` las excluye; ver capítulo 10, sección 10.3). Son `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `n_impulso`, `medicamentos_n`, `med_*`, `det_*` y `hab_` del hábito ligado a las recaídas. El panel las sigue calculando para la exportación, así que solo se cruzan en R. Hasta v84 entraban con el interruptor encendido, y `n_impulso` y `med_*` siempre.

---

## 6.11 Análisis por semana

**Qué hace.** «Por semana» repite la lista con **semanas** en lugar de días. Sirve para lo que pasa pocas veces (escalar, viajar, escribir cartas).

**Cómo exactamente** (`Estadistica.semanal`, B.10): cada semana va de **lunes a domingo**; cada variable es el **promedio** de los días de esa semana con dato (en las 0/1, la proporción de días con 1); una semana entra para una variable si tiene **al menos 3** días con dato. Luego `relaciones(…, minN = 8, paso = 7, ventana = 35)`: al menos 8 semanas en común; la tendencia se quita con ±35 días (±5 semanas, ventana de hasta 11 semanas); la autocorrelación es entre semanas consecutivas; una variable es lenta con al menos 8 restos. «Cruzar dos variables» por semana usa `pares(…, paso = 7)`: Y de la semana siguiente.

**Por qué.** Promediar reduce el ruido diario y vuelve frecuentes los eventos raros. **[Diseño propio]**.

**Cuándo falla.** Con 8 semanas solo se ve una correlación enorme; durante las primeras ~11 semanas la autocorrelación se toma como 0 (6.6.6); el promedio de una semana con 3 días no es comparable al de una con 7.

---

## 6.12 «Cruzar dos variables»

**Qué hace.** Eliges X e Y y ves su dispersión, r de Pearson, ρ de Spearman, el número de días y un texto.

**Cómo exactamente.** `Estadistica.pares(panel, x, y, desfase)` toma los días con ambos datos; con desfase, Y es la del **día siguiente de calendario** (fecha + 1). Con menos de 3 días no dibuja; el intervalo (`ic95`, sobre Pearson, con *n* sin corregir) requiere 5. El texto dice la fuerza, la dirección, si el intervalo incluye el cero («podría ser azar») y, con menos de 30 días, «Solo son N días: tómalo como pista»; siempre recuerda que correlación no es causa. La recta punteada de la dispersión es la de mínimos cuadrados de Y sobre X.

**Cuándo falla (H-5-4).** Esta pantalla usa **los valores tal cual** (sin quitar tendencia) y el **n bruto** (sin descontar la autocorrelación). Puede decir «no incluye el cero» para una pareja que en la lista aparece punteada como «podría ser azar», o con un signo distinto si la relación era de tendencia. Para una sola pareja elegida por ti no hace falta BH, pero sí los otros dos ajustes, o al menos un aviso.

---

## 6.13 «Todo lo que se mueve con algo»

### 6.13.1 Qué hace

Eliges un objetivo (tu ánimo, una persona, un lugar, una app, una etapa de vida) y la app revisa **todas** las demás variables contra él.

### 6.13.2 Cómo exactamente (`Analisis.factores`)

Para cada variable *v* distinta del objetivo:

1. Se salta si comparte familia con el objetivo (`FAMILIAS`) o si `obvia(v, objetivo)`.
2. Toma los pares (con o sin desfase de un día). Si hay **menos de 10**, se salta.
3. Si *v* es 0/1:
   - exige al menos **5** días con 1 y 5 con 0;
   - calcula la **diferencia de medias** del objetivo entre días con y sin (`Estadistica.diferencia`, 6.15.2), su intervalo con ±1.96 EE y *p* = 2(1 − Φ(|d|/EE));
   - muestra «+0.45 puntos los días con (12) frente a sin (80)».
4. Si es continua: exige al menos **14** pares; calcula **ρ de Spearman**, su intervalo de Fisher con *n* bruto y *p* con z = atanh(ρ)·√(n − 3).
5. Corrige todos los *p* con BH; marca con ✓ los de q < 0.1; muestra las 20 primeras ordenadas por *p*.

### 6.13.3 Razonamiento

Responde una pregunta natural («¿qué acompaña mis mejores días?»). El desfase de un día ayuda con la dirección: si lo de hoy se asocia con el ánimo de **mañana**, es menos probable que el ánimo haya causado lo otro (aunque no imposible: una variable persistente puede seguir siendo efecto del ánimo de ayer). **[Aproximación]**.

### 6.13.4 Ejemplo trabajado

Objetivo: valencia. Variable: «Aparece @persona_b» (0/1). Días con: 5, 6, 5, 4, 6, 5, 6 (media 5.29). Días sin (diez): 4, 4, 5, 3, 4, 5, 4, 3, 4, 5 (media 4.10). Diferencia 1.19; EE = √(0.571/7 + 0.544/10) = 0.369; IC 0.46 a 1.91; p = 0.0013. Hay 17 pares (mínimo 10) y 7 y 10 días de cada tipo (mínimo 5), así que la app sí la evalúa. La prueba t de Welch de R daría p = 0.0069 e IC 0.39 a 1.98: más ancho, porque con 7 y 10 días la distribución t tiene colas más gruesas que la normal.

### 6.13.5 Cuándo falla (H-5-3, alta)

- **No quita la tendencia ni descuenta la autocorrelación.** Sus *p* son, para variables persistentes, mucho más pequeños de lo que deberían (Figura 6.3), y BH no puede compensar eso. Como tocar una tarjeta de la lista abre esta pantalla, el usuario puede ver la misma pareja **punteada** («podría ser azar») en la lista y con **✓** aquí.
- Usa ρ de Spearman donde la lista usa r de Pearson sobre restos: el número también cambia.
- Para 0/1 usa z (1.96) en vez de t, con apenas 5 días por grupo (H-5-5).

---

## 6.14 Modelo múltiple

### 6.14.1 Qué hace

Pregunta por **varias causas posibles a la vez**: ¿el sueño se asocia con el ánimo **manteniendo fijos** los pasos y la pantalla? Opcionalmente incluye **el valor del objetivo el día anterior**.

### 6.14.2 Cómo exactamente (`Analisis.modelo`)

1. Toma los días con objetivo y **todos** los predictores (y, si `conAyer`, el objetivo del día de calendario anterior); descarta los demás.
2. Exige **n ≥ k + 10**, con *k* el número de predictores (incluido «ayer»). Con 3 predictores y ayer, 14 días.
3. **Estandariza** cada columna y el objetivo: resta su media y divide entre su DE (si alguna DE es 0, no hay modelo).
4. Resuelve mínimos cuadrados **sin intercepto** (todo está centrado): β = (X′X)⁻¹ X′y, invirtiendo con Gauss-Jordan con pivoteo parcial; si un pivote es menor que 10⁻¹⁰ (predictores redundantes), no hay modelo.
5. Error: s² = SSE / (n − k − 1); EE(β_j) = √(s² · [(X′X)⁻¹]_jj); intervalo β ± **1.96** EE.
6. R² = 1 − SSE / SST, con SST = Σ y² (y estandarizada).

La pantalla permite hasta **6** predictores; marca ✓ si el intervalo no incluye el cero; interpreta |β| de 0.1 como pequeño, 0.3 moderado y 0.5 grande.

### 6.14.3 Razonamiento

Los coeficientes estandarizados permiten comparar predictores en unidades distintas (horas, pasos, minutos). Controlar por el valor de ayer reconoce que el ánimo tiene inercia: sin eso, cualquier cosa que coincida con «rachas» parecería un efecto. Es la idea de los modelos autorregresivos con predictores, el estándar en estudios diarios intensivos (Hamaker y Wichers, 2017). **[Aproximación]**: es un modelo autorregresivo simple, no uno con errores autocorrelados ni multinivel.

### 6.14.4 Ejemplo trabajado (sintético)

59 días completos; objetivo valencia; predictores horas de sueño, pasos y valencia de ayer. Resultado: β sueño = +0.38 [0.15, 0.62] ✓; β pasos = +0.09 [−0.14, 0.32]; β ayer = +0.41 [0.18, 0.65] ✓; R² = 27 %. Lectura: un día con una DE más de sueño (≈ 1 h en estos datos) se asocia con 0.38 DE más de valencia, a igual ánimo de ayer y pasos; los pasos no añaden nada claro.

### 6.14.5 Cuándo falla (H-5-11)

- Intervalo con 1.96 (normal) en vez de t con n − k − 1 grados de libertad: con 14 días queda ~7 % estrecho.
- R² **no ajustado**: con 6 predictores y pocos días sube aunque los predictores sean ruido.
- Solo días **completos**: si un predictor falta mucho, el modelo usa pocos días y quizá no representativos.
- No quita tendencias.
- El interruptor dice «Controlar por el **ánimo** del día anterior», pero lo que agrega es el **objetivo** de ayer (si el objetivo es el sueño, es el sueño de ayer).
- En R, `vm_modelo_ar1` agrega errores AR(1) pero no tiene la opción del valor de ayer (Apéndice C).

---

## 6.15 Tendencia, cambio reciente y día de la semana

### 6.15.1 Medias móviles (`Analisis.movil`)

Para cada día del calendario entre el primero y el último con dato, promedia los valores de **ese día y los `ventana − 1` anteriores** que existan; el punto se dibuja solo si hay **al menos max(2, ventana/3)** días con dato (división entera: 2 para la de 7 días, 10 para la de 30). Es una media **hacia atrás**: se «retrasa» frente a los cambios, a diferencia de la media centrada que se usa para quitar la tendencia. La gráfica muestra el último año, con líneas solo entre días consecutivos. `vm_movil` en R es idéntica. **[Diseño propio]**, técnica estándar.

![Figura 6.1](figuras/fig11_medias_moviles.png)

*Figura 6.1. Valencia sintética de 200 días con 18 % de días sin registro y un hueco de 17 días; medias móviles de 7 y 30 días con sus mínimos de días. Script: `figuras/fig11_medias_moviles.py` (replica `Analisis.movil`).*

### 6.15.2 Diferencia de medias (`Estadistica.diferencia`)

Con al menos 3 valores por grupo (B.11):

$$ d=\bar a-\bar b,\qquad EE=\sqrt{\frac{s_a^2}{n_a}+\frac{s_b^2}{n_b}},\qquad IC_{95}=d\pm 1.96\,EE $$

Las varianzas son muestrales (con n − 1). Es el error estándar de Welch (1947), pero con el multiplicador **1.96 de la normal** en vez del cuantil t con los grados de libertad de Welch. La usan «Tendencia» (`cambio`), «Todo lo que se mueve con algo» (variables 0/1), «Antes y después» y los experimentos (capítulo 8).

### 6.15.3 Cambio reciente (`Analisis.cambio`)

Compara los **últimos 30 días** (fechas posteriores a hoy − 30) con los **30 anteriores** (de hoy − 60, exclusive, a hoy − 30, inclusive) con `diferencia`. Si el intervalo no incluye el cero: «El cambio es claro».

### 6.15.4 Día de la semana (`Analisis.porDiaSemana`)

Para cada día de la semana con **al menos 2** valores: media ± 1.96 · DE/√n. Dice «Los intervalos no se enciman: la diferencia parece real» si el límite inferior del mejor día supera el superior del peor.

### 6.15.5 Antes y después (`EtapasDeVida` en Explorar.kt)

Para cada uno de los últimos 8 cambios guardados en «Conocerte», compara los **60 días anteriores** (fecha − 1 a fecha − 60) con los **60 siguientes** (fecha a fecha + 59) en valencia, sueño, pantalla, pasos, gasto, personas mencionadas y palabras, con `diferencia`. El diseño (estudio observacional, no experimento) se discute en el capítulo 8.

### 6.15.6 Cuándo fallan (H-5-5)

Las tres comparaciones tratan cada día como **independiente**: 30 días seguidos de un mismo periodo se parecen, así que el intervalo real es más ancho (la misma lógica de la Figura 6.3). Además usan 1.96 fijo: con «Día de la semana» basta **n = 2** por día, y con 2 valores el multiplicador correcto de t sería 12.7, no 1.96. «Antes y después» compara periodos que también difieren en estación del año y en todo lo demás que cambió a la vez (la app lo advierte: «Muchas cosas cambian a la vez; tómalo como una pista»).

---

## 6.16 Dinámica emocional

### 6.16.1 Qué hace

Resume **cómo se mueve** tu ánimo, no solo su nivel.

### 6.16.2 Cómo exactamente (`Analisis.dinamica`)

Usa la `valencia` del diario (1 a 7); exige al menos **7** días.

- **Media** y **DE** (variabilidad).
- **MSSD** (inestabilidad; B.12): promedio de los cuadrados de las diferencias entre **días consecutivos** de calendario con ambos datos; exige al menos 3 pares. Si faltó un día, ese salto no cuenta.
- **Inercia** (B.13): Pearson entre el valor de cada día y el del día siguiente; exige al menos **10** pares.
- **Intradía**: el promedio, entre días con al menos 2 registros de ánimo con valencia (avisos y propios), de la DE de esos registros.
- **% de días difíciles**: días con valencia **≤ 2**.
- **Racha buena**: la más larga de días seguidos con valencia ≥ 5. **Racha dura**: la más larga con valencia **≤ 3**.
- **Por mes**: media, DE y MSSD (pares dentro del mismo mes).

### 6.16.3 Razonamiento científico

La DE, el MSSD y la autocorrelación son los índices clásicos de la dinámica afectiva. El MSSD (von Neumann et al., 1941) mide la inestabilidad **de un momento al siguiente**, que la DE no distingue: dos personas con la misma DE pueden cambiar suave o bruscamente (Jahng et al., 2008). La inercia, la tendencia del ánimo a quedarse donde estaba, se ha asociado con malestar psicológico (Kuppens et al., 2010). **[Validado]** como índices. Dos advertencias de la literatura: el MSSD mezcla variabilidad e inercia (aproximadamente MSSD ≈ 2 · DE² · (1 − inercia)), y en muestras grandes estos índices añadieron poca información sobre el bienestar más allá de la media y la DE (Dejonckheere et al., 2019). Además, la literatura los define sobre mediciones **momentáneas** varias veces al día; aquí se calculan sobre **un valor por día** recordado por la noche. **[Aproximación]**.

### 6.16.4 Ejemplo trabajado

Siete días seguidos: 4, 5, 3, 3, 6, 5, 4.
Media = 4.29; DE = 1.11.
Diferencias: +1, −2, 0, +3, −1, −1 → cuadrados 1, 4, 0, 9, 1, 1 → **MSSD = 16/6 = 2.67**.
Inercia: no se calcula (6 pares < 10).
Días difíciles (≤ 2): 0 %. Racha buena (≥ 5): 2 días (6, 5). Racha dura (≤ 3): 2 días (3, 3).

### 6.16.5 Cuándo falla (H-5-12)

- «Días difíciles» usa ≤ 2 y «racha dura» ≤ 3: dos definiciones de «difícil» en la misma tarjeta.
- Con días faltantes no aleatorios (dejar de escribir los días malos, capítulo 3), el MSSD y la inercia se calculan con los días que quedan, que son los más estables.
- R (`vm_dinamica`) calcula también la inercia por mes y empareja el último día de un mes con el primero del siguiente; la app no.

---

## 6.17 Palabras del diario

**Qué hace.** Muestra las palabras de tus días mejores y más difíciles, y las 25 más frecuentes.

**Cómo exactamente (`Analisis.palabras`).** Solo días con nota. Toma las secuencias de **3 o más letras** (en minúsculas), quita una lista de ~170 palabras vacías (comparando sin acentos) y, para cada palabra, los **días distintos** en que aparece. Si aparece en al menos **5** días con valencia, calcula la media de la valencia de esos días y su diferencia con la media **de los días con nota y valencia**. Muestra las 15 de mayor y menor diferencia (la pantalla, 10).

**Ejemplo.** La media de tus días con nota es 4.4. «Playa» aparece en 6 días con valencias 6, 5, 7, 6, 5, 6 (media 5.83): +1.43.

**Razonamiento y límites (H-5-10).** Es un análisis de **contenido por conteo** sin diccionario de emociones (a diferencia de LIWC, Tausczik y Pennebaker, 2010) ni prueba estadística. **[Diseño propio]**. Con cientos de palabras y 5 días como mínimo, las de los extremos de la lista son, en buena parte, las que tuvieron suerte (regresión a la media): con 5 días, la media de valencia varía ±0.9 puntos solo por azar si tu DE es 1. No hay intervalo ni q. Las palabras de días malos pueden reflejar **lo que escribes** cuando estás mal más que **lo que te pone mal**. En R, `vm_palabras` usa otra lista de vacías y otra referencia (la media de **todos** los días, con o sin nota), así que sus diferencias no coinciden con las de la app.

---

## 6.18 Días atípicos

**Qué hace.** Lista los días del **último año** que se salieron de lo normal en alguna variable, con la razón.

**Cómo exactamente (`Analisis.atipicos`).** Revisa 15 variables: `valencia`, `sueno_horas`, `pasos`, `pantalla_min`, `desbloqueos`, `gasto_total`, `notificaciones`, `fotos`, `km`, `min_hablado`, `wa_mios`, `musica_min`, `fc_reposo`, `hrv`, `palabras`. Para cada una con al menos **20** días: mediana *M* = el valor en la posición n/2 de los datos ordenados (con n par, el **de arriba** de los dos centrales), MAD = la mediana (misma regla) de |x − M| multiplicada por **1.4826**, y z robusto (B.14):

$$ z_i=\frac{x_i-M}{1.4826\cdot \mathrm{mediana}(|x-M|)} $$

Marca el día si |z| ≥ **3** («muy alto» o «muy bajo»). Si MAD = 0 (la mitad o más de los días tienen el mismo valor), salta la variable. «Tu año» usa umbral 2.8.

**Razonamiento.** La media y la DE se dejan arrastrar por los propios atípicos; la mediana y la MAD no. El factor 1.4826 hace que la MAD equivalga a la DE si los datos fueran normales, y |z| ≥ 3 es un umbral común y conservador (Leys et al., 2013). **[Validado]**.

**Ejemplo.** 20 días de pasos: mediana (superior) 8 200; MAD × 1.4826 = 1 038; un día con 2 100 pasos da z = (2100 − 8200)/1038 = **−5.9**: «Pasos muy bajo». (R, con la mediana promediada, daría 8 150 y 964; z = −6.3.)

**Cuándo falla (H-5-8).** El identificador `km` **no existe** en el panel de la app (la variable se llama `distancia_km`), así que la distancia **nunca** se revisa. Las variables muy concentradas (gasto, muchos días en 0) tienen MAD = 0 y se saltan siempre.

---

## 6.19 Ritmo de sueño (lo estadístico)

Los modelos de sueño (jet lag social, noches aproximadas por el teléfono) se explican en el capítulo 5. Desde lo estadístico basta saber que `Analisis.ritmo` usa **promedios simples** de horas decimales (con la madrugada sumada 24 h para no promediar 23:30 con 0:30 como 12:00), una DE de la hora de dormir como «regularidad» y la diferencia absoluta de los puntos medios entre noches antes de días libres y laborales; exige 5 noches en total y 2 de cada tipo. `vm_ritmo` en R hace lo mismo pero **solo con el reloj** (no aproxima con el teléfono) y filtra con 2 < duración < 16 h (la app incluye los extremos 2 y 16).

---

## 6.20 Cifras descriptivas: «Tus números», «Totales» y los reportes

Estas funciones no prueban nada; resumen. Importan porque sus cifras se comparan con recomendaciones de salud y entre periodos.

### 6.20.1 «Tus números» (`TusNumeros`)

Para cada medida: el **promedio de los últimos 30 días que tengan dato** dentro del último año (función `ultimos`), con las fechas desde y hasta; se considera «reciente» si el último dato tiene 45 días o menos. Algunas conversiones: km al año = km por día × 365; ejercicio a la semana = media diaria × 7; días de fuerza por semana = entrenamientos en los 28 días que terminan en el último entrenamiento / 4; personas al día = el máximo entre `personas`, `contactos` y `wa_chats`; tazas de café (en reportes) = mg de cafeína / 95.

**Cuándo falla (H-5-9).** `ejercicio_min` solo tiene valor los días **con** una sesión del reloj (los demás son `null`), así que «los últimos 30 días con dato» son los últimos 30 días **en que hiciste ejercicio**, y multiplicar su media por 7 supone que haces ejercicio **todos** los días: sobreestima el ejercicio semanal. Ejemplo: 3 sesiones de 45 min por semana → la app calcula 45 × 7 = **315 min/semana** en lugar de 135. Además, «aire libre» usa `luz_alta_min` (solo la luz del teléfono) y no `aire_libre_min` (el máximo entre luz y movimiento, capítulo 4).

### 6.20.2 «Totales» (`Totales.calcular`)

Suma cada medida en un periodo y dice **desde qué fecha** hay datos de cada fuente, el récord y su fecha; arma índices buscables (alimentos, personas, artistas, lugares, ejercicios…). Excluye los momentos privados. Es pura aritmética. Usa la fecha de calendario (`fecha`) para gastos e ingresos, mientras el panel usa el día lógico (`dia`).

### 6.20.3 Reportes y «Tu año» (`Periodo.calcular`)

Calcula todo lo de una semana, mes o año y lo compara con el **periodo anterior de la misma duración** (exige 3 días con dato en el anterior). La valencia de un día es la del diario o, si no hay, el promedio de los registros de ánimo (`valDe`): mezcla el «yo que recuerda» con el «yo que experimenta» (capítulo 3). La hora media de dormir suma 24 a las horas **antes de las 15:00** (en el resto de la app, antes de las 12:00) y lee la hora tal como quedó escrita en el texto, no en la zona actual. Los gastos van por día lógico y los ingresos por fecha de calendario (H-5-15). Nunca usa lo privado ni el texto de las cartas.

---

## 6.21 Potencia: por qué 14, 30 y 90 días

### 6.21.1 Qué dice la app

- La lista de relaciones exige **14** días (8 semanas por semana).
- «Cruzar dos variables» dice que hacen falta «unos 30 para empezar a creerle», y la pantalla «Cobertura de variables» cuenta cuánto falta para **30** días por variable.
- La pantalla de Análisis dice: «Con 90 días o más ya se pueden detectar relaciones moderadas» y, antes, «Faltan N días para tener potencia suficiente».

### 6.21.2 Qué significa «potencia»

La **potencia** es la probabilidad de detectar una relación que **sí existe**. Con pocos días, aunque haya relación, la prueba casi nunca la encuentra.

### 6.21.3 Cálculo (B.15)

Con la aproximación de Fisher, la correlación más pequeña que se detecta con potencia de 80 % y α = 0.05 bilateral es

$$ |r_{\min}|=\tanh\!\Big(\frac{1.96+0.84}{\sqrt{n_{Ef}-3}}\Big) $$

| Días | Días independientes | Autocorrelación 0.5 en ambas (nEf = 0.6 n) | Igual, con α = 0.001 |
|---|---|---|---|
| 14 | 0.69 | 0.84 | 0.95 |
| 30 | 0.49 | 0.62 | 0.79 |
| 90 | **0.29** | 0.37 | 0.52 |
| 180 | 0.21 | 0.27 | 0.38 |
| 365 | 0.15 | 0.19 | 0.27 |

Así, la frase de los 90 días es **correcta para una sola relación con días independientes**: con 90 días se detecta |r| ≈ 0.29, el límite entre «débil» y «moderada» de Cohen. **[Validado]** en ese sentido estricto. Pero en «Las relaciones más marcadas» los días no son independientes y se revisan miles de parejas, así que el umbral efectivo se parece más a la última columna: con 90 días solo se sostienen relaciones de 0.5 o más. Los 14 días son un **mínimo para calcular**, no para creer. Los 30 días son una convención razonable para «pista». **[Diseño propio]**.

![Figura 6.6](figuras/fig16_potencia.png)

*Figura 6.6. La correlación más pequeña detectable con potencia de 80 % según los días, con días independientes, con autocorrelación 0.5 y con un umbral estricto como el que imponen miles de comparaciones. Script: `figuras/fig16_potencia.py` (cálculo, sin datos).*

### 6.21.4 Cuándo falla (H-5-16)

El aviso de los 90 días cuenta **días con diario** (`Repo.dias.size`), no días con ambos datos de cada pareja, y no menciona que la corrección por muchas pruebas y la autocorrelación suben bastante el umbral.

---

## 6.22 App y R, función por función

El script `vestigium.R` reproduce casi todas las herramientas. La tabla dice dónde coinciden y dónde no. El detalle de cada función está en el Apéndice C.

| Análisis | App | R | ¿Mismo resultado? |
|---|---|---|---|
| Datos | `Estadistica.panel()` | `vm_panel.csv` (`Exportar.filasPanel`) | **No**: otros nombres, otras filas, otros ceros, menos variables (6.3.4; H-5-1) |
| Relaciones | `relaciones`: Pearson, ±10 d, lentas < 0.12, nEf, BH, orden por «seguro» | `vm_relaciones`: el **mismo algoritmo** (mismas cotas, mínimos y orden; p con `pnorm` exacto) | Mismo método, **distinta tabla** y **distinta lista de obvias** (19 conjuntos frente a 56 + reglas; H-5-2). R además compara `fin_de_semana` (columna lógica que agrega el script). No hay modo semanal en R. |
| Cruzar | Pearson + Spearman; IC de Pearson con n | `vm_cruce`: **Spearman** por omisión, `cor.test` | Distinto coeficiente por omisión (H-5-7) |
| Factores | ρ con p por Fisher; 0/1 con z; mínimos 10 pares (5 y 5) / 14 | `vm_factores`: ρ con p de `cor.test` (aproximación t); 0/1 con **t de Welch**; mínimo 14 pares para todo; excluye solo la familia del objetivo en `vm_familias` | Parecido, no igual (H-5-3) |
| Modelo | MCO estandarizado sin intercepto; IC 1.96 | `vm_modelo`: `lm` sobre datos estandarizados; inferencia con t | Mismos β; intervalos/p algo distintos |
| Modelo con AR(1) | — | `vm_modelo_ar1`: `nlme::gls` con `corAR1(~ t)` | Solo en R |
| Tendencia | `movil` | `vm_movil` | Igual |
| Día de la semana | n ≥ 2 por día; 1.96 EE | `vm_semana`: sin mínimo; 1.96 EE | Igual salvo el mínimo |
| Dinámica | Valencia; mínimos 7 días, 3 y 10 pares; meses sin cruzar | `vm_dinamica`: cualquier variable; sin mínimos; inercia por mes; pares que cruzan meses | Casi igual en el total |
| Ritmo | Reloj o teléfono | Solo reloj | Igual con reloj |
| Palabras | ~170 vacías sin acentos; referencia: días con nota | ~110 vacías con acentos; referencia: todos los días | **No** (H-5-10) |
| Atípicos | 15 variables; mediana superior; ≥ 20 días; último año; `km` inexistente | 8 variables; `median`/`mad` de R; todos los días; `km` sí existe | **No** (H-5-8) |

---

## 6.23 Qué es estándar y qué es propio

| Decisión | Marca |
|---|---|
| Pearson, Spearman con rangos promedio, cortes 0.1/0.3/0.5 | [Validado] |
| Transformación z de Fisher para intervalos y p | [Validado] |
| Φ por Abramowitz y Stegun 7.1.26 | [Validado] |
| Benjamini-Hochberg con q < 0.10 | [Validado] (procedimiento); [Aproximación] (pruebas dependientes) |
| nEf = n(1 − r_a r_b)/(1 + r_a r_b) | [Validado] como aproximación AR(1); cotas [−0.5, 0.97] y [4, n]: [Diseño propio] |
| Quitar tendencia restando la media de ±10 días | [Aproximación]; tamaño de ventana [Diseño propio] |
| Variables lentas con var(resto)/var < 0.12 | [Diseño propio] |
| Lista de relaciones obvias | [Diseño propio] |
| Familias dinámicas, topes y mínimos de días | [Diseño propio] |
| Mínimos de 14 días, 4 de cada valor en 0/1, 8 semanas | [Diseño propio] |
| Diferencia de medias con EE de Welch y 1.96 | [Aproximación] (debería ser t) |
| Modelo estandarizado con el día anterior | [Aproximación] |
| DE, MSSD, inercia | [Validado] como índices; [Aproximación] con un valor por día |
| z robusto con MAD y umbral 3 | [Validado] |
| Palabras por diferencia de medias | [Diseño propio] |
| 90 días para relaciones moderadas | [Validado] para una prueba con días independientes; optimista para la lista |

---

## 6.24 Resumen del capítulo

1. Todo sale de un **panel** de un renglón por día. Cada variable tiene su regla para distinguir «0» de «no sé», y esas reglas no son uniformes (H-1-1).
2. «Las relaciones más marcadas» es el análisis más cuidadoso de la app: **quita tendencias** (±10 días), **descuenta los días parecidos** (nEf) y **corrige por miles de comparaciones** (BH, q < 0.10). Una relación firme ahí es una pista seria, no una prueba de causa.
3. Las demás herramientas aplican menos protecciones. La más importante de corregir es «Todo lo que se mueve con algo», que puede marcar como firme lo que la lista llama azar (H-5-3).
4. **El ciclo semanal no se quita** al quitar la tendencia (H-5-6).
5. **R aplica el mismo algoritmo a otra tabla y con otra lista de obvias**: hoy no se puede esperar que dé la misma lista (H-5-1, H-5-2).
6. Con 90 días independientes se ve una relación de 0.3; con miles de parejas y días parecidos, hace falta más de 0.5. Paciencia: el sistema está pensado para años.

### Referencias citadas en este capítulo

Abramowitz y Stegun (1964); Benjamini y Hochberg (1995); Benjamini y Yekutieli (2001); Bretherton et al. (1999); Cohen (1988); Dejonckheere et al. (2019); Fieller et al. (1957); Fisher (1915); Granger y Newbold (1974); Hamaker y Wichers (2017); Jahng et al. (2008); Kuppens et al. (2010); Leys et al. (2013); Pyper y Peterman (1998); Storey y Tibshirani (2003); Tausczik y Pennebaker (2010); von Neumann et al. (1941); Welch (1947); Yule (1926). Datos completos y verificación en `referencias.md`.
