# Capítulo 6. Análisis estadístico

> **Para qué sirve este capítulo.** Explica cómo la app pasa de miles de registros a frases como «cuando duermes más, tu ánimo tiende a subir»: cómo arma la tabla de un renglón por día (el *panel*), cómo compara cada variable con cada otra y qué hace para no dejarse engañar por las tendencias, por los días que se parecen a sus vecinos y por el azar de revisar miles de parejas. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), puesto al día con las entregas 10e (D.88 a D.108; lo que cambió se marca «desde 10e») y 10f (D.109 a D.118; «desde 10f»). Las fórmulas están en el Apéndice B. **Los ejemplos numéricos son inventados.**

---

## 6.1 Mapa: qué análisis hay y con qué protecciones

La pantalla **Análisis** (`ui/pantallas/Explorar.kt`) muestra «Las relaciones más marcadas» y abre ocho **herramientas** (`ui/pantallas/Analisis.kt`). Tres peligros acechan a cualquier comparación hecha con los datos de una persona a lo largo del tiempo: las **tendencias**, los **días parecidos** y las **muchas pruebas**. La tabla dice qué herramienta se protege de qué.

| Dónde | Función | Prueba | ¿Quita tendencia? | ¿Corrige por días parecidos? | ¿Corrige por muchas pruebas? |
|---|---|---|---|---|---|
| Las relaciones más marcadas | `Estadistica.relaciones` | Pearson | Sí (interruptor, encendido por omisión) | Sí (nEf) | Sí (BH, q < 0.10) |
| Cruzar dos variables | `Estadistica.pares`, `correlacionEf`, `sinTendencia` | Pearson y Spearman | Se muestra al lado | Sí (nEf) | No aplica (una pareja) |
| Todo lo que se mueve con algo | `Analisis.factores` | Pearson con nEf; diferencia de medias (0/1) | Sí (interruptor) | Sí | Sí (BH, q < 0.1) |
| Modelo múltiple | `Analisis.modelo` | Mínimos cuadrados estandarizados | No | En parte (el día anterior) | No |
| Tendencia, Antes y después | `Analisis.cambio`, `Estadistica.diferenciaEnDias` | t de Welch con factor AR(1) | No | Sí | No |
| Día de la semana | `Analisis.porDiaSemana` | Media ± t(n − 1) · EE | No | No | No |
| Dinámica, palabras, atípicos | `Analisis.dinamica`, `palabras`, `atipicos` | Descriptivas | No | — | No |

Lo importante: **«Las relaciones más marcadas» y, desde la entrega 10c, «Todo lo que se mueve con algo» aplican las tres protecciones a la vez**. Las demás son descriptivas o aplican una o dos; la app lo dice: «Pistas para entenderte. Ninguna prueba causas».

---

## 6.2 El panel diario: la tabla de la que sale todo

### 6.2.1 Qué es

`Estadistica.panel()` devuelve un mapa `fecha → (variable → valor)`. Cada **fecha** es un día lógico (de 5:00 a 4:59 del día siguiente; capítulo 2), y desde 10f cada momento se asigna a su día y a su hora con la zona que tenía el teléfono entonces (D.109). Cada **valor** es un número o `null` (un **dato faltante**: «no sé» para ese día, que no es lo mismo que 0). Las variables 0/1 se guardan como 0.0 y 1.0. Qué variables entran en los análisis lo decide `Estadistica.variables()`: 187 con nombre fijo en 24 grupos (valencia, sueño, pasos, clima…; 130 existen siempre y 57 **solo aparecen si hay datos**; en 10e se agregaron `error_prediccion_signo` y `gasto_imprevisto_monto`, D.94, y en 10f `fuerza_ref_kg`, la mejor 1RM estimada del día en press inclinado con mancuernas, D.114) y las **dinámicas** (6.2.3). Las privadas nunca: desde el módulo 10a se excluyen siempre y solo viajan a R. **[Diseño propio]**.

### 6.2.2 Cero o faltante: la decisión más delicada

Para cada variable la app decide qué pasa en los días sin registro:

1. **Faltante**: si la fuente no dio nada, `null` (sueño del reloj, clima, pantalla…).
2. **Cero desde el primer registro** (`Estadistica.ceroDesde(fuente, base)`, B.20): en los días de `base` iguales o posteriores al primer día de `fuente`, «no hubo» vale 0; antes, `null`. La usan, entre otras, `gasto_total` (desde el primer gasto, en días con diario) y `ejercicio_min` (desde la primera sesión del reloj, en días con datos del reloj).
3. **Cero solo donde se pudo responder**: por ejemplo, las variables sociales valen 0 solo en los días «sociales» (`Dinamicas.diasSociales`: con diario escrito o con un momento no privado que nombra a alguien con @).
4. **Excepción declarada**: los contextos `ctx_*` valen 0 cuando no se marcan, porque la pantalla del Día dice que lo no marcado es «no».

La regla 2 es la más defendible: antes de empezar a usar una función no se sabe si «no pasó» o «no lo anotaste» (H-1-1, H-9-16). **[Diseño propio]**. Si empiezas a registrar gastos el 1 de marzo, el 15 de febrero `gasto_total` es `null`, no 0. Lista completa de casos en la versión completa, §6.3.2.

### 6.2.3 Las variables dinámicas: «todo se puede cruzar con todo»

`Dinamicas.construir()` crea **familias** de variables con `familia(prefijo, …, max, minDias)`: cuenta en cuántos días aparece cada cosa (una persona, una app, un lugar), se queda con las que aparecen al menos `minDias` días y, de ellas, con las `max` más frecuentes (o con más peso). Por ejemplo, `per_` (aparece @persona) tiene máximo 15 y mínimo 5 días. El tope evita que las relaciones se ahoguen en casos raros. **[Diseño propio]**. El costo es enorme en comparaciones múltiples (6.7).

Las variables de tipo «escalón» (`vida_*`, `pol_*` y en buena parte `esc_*`) cambian pocas veces al año; al quitar la tendencia casi no les queda variación, la app las clasifica como «lentas» (6.4.3) y no entran en la lista con el interruptor encendido: **una etapa de vida nunca puede aparecer como relación «sin tendencia»**; para ella están «Antes y después» y R. Desde 10e (D.103) la lista lo dice y remite a «Antes y después» (H-5-14, resuelto).

---

## 6.3 La correlación: Pearson y Spearman

La **correlación** responde «¿cuando una sube, la otra tiende a subir (o a bajar)?» con un número de −1 a 1: +1, suben juntas siempre en línea recta; 0, ninguna relación lineal; −1, cuando una sube la otra baja.

**Pearson** (`Estadistica.pearson`, B.1), con n ≥ 3 pares:

$$ r = \frac{\sum_i (x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_i (x_i-\bar x)^2\,\sum_i (y_i-\bar y)^2}} $$

Si una variable no cambia, devuelve `null`. **Spearman** (`Estadistica.spearman`, B.2) es Pearson aplicado a los **rangos** (el orden de los valores); a los empates se les da el promedio de los lugares que ocupan (dos valores en los lugares 3.º y 4.º reciben 3.5). **[Validado]**. La **fuerza** (`Estadistica.fuerza`): |r| < 0.1 «prácticamente nula»; < 0.3 «débil»; < 0.5 «moderada»; de 0.5 en adelante «fuerte», los cortes de Cohen (1988). **[Validado]** como convención; Cohen mismo advertía que dependen del campo.

Pearson mide relaciones **rectas** y se deja arrastrar por un día extremo; Spearman mide relaciones **que solo suben o solo bajan** y es robusto a extremos, pero con muchos empates pierde información. La app usa Pearson en la lista, en «Todo lo que se mueve con algo» y en «Cruzar dos variables» (que muestra también Spearman); sobre los restos sin tendencia es razonable, porque al restar el promedio de los vecinos los valores se vuelven más simétricos. **[Aproximación]**. En R, desde 10e (D.106), `vm_cruce` y `vm_presion` dan lo mismo que «Cruzar dos variables» (Pearson con nEf como principal, la r sin tendencia y Spearman) y `vm_cruce_horas` da los dos coeficientes; hasta 10e daban solo Spearman (H-5-7, resuelto en parte).

---

## 6.4 Quitar la tendencia

### 6.4.1 El problema

Si dos cosas cambian lentamente con el tiempo, **parecen relacionadas aunque no tengan nada que ver**: son las «correlaciones sin sentido» entre series de tiempo (Yule, 1926) y las «regresiones espurias» (Granger y Newbold, 1974). **[Validado]**. En otoño los días se acortan y, por otras razones, estás bajando de peso: «horas de luz» y «peso» salen correlacionadas solo porque ambas bajan con los meses.

### 6.4.2 Cómo lo hace exactamente

`Estadistica.series()` (privada) toma, para cada día *i* con dato, **todos los días con dato a 10 días de calendario o menos** (de *i* − 10 a *i* + 10, incluido el propio día; `VENTANA = 10`). Si hay al menos **3**, el **resto** del día es su valor menos el promedio de esa ventana (B.3); si no, el resto queda vacío:

$$ \text{resto}_i = x_i - \frac{1}{|W_i|}\sum_{j\in W_i} x_j,\qquad W_i=\{j:\ |d_j-d_i|\le 10,\ x_j\ \text{existe}\},\ |W_i|\ge 3 $$

La ventana es de días de calendario: si faltaron días tiene menos valores, pero no se estira. **[Diseño propio]** la elección de ±10 días; la técnica (restar una media móvil centrada) es un filtro pasa-altas estándar. **[Aproximación]**. R hace lo mismo en `vm_relaciones(…, ventana = 10)`.

**Ejemplo.** Valencia de los días 1 a 15 (faltan el 7 y el 13): 5, 4, 6, **3**, 5, 5, —, 4, 6, 5, 4, 5, —, 3, 6. Para el día 4, los días con dato a 10 o menos de distancia son del 1 al 14, menos el 7 y el 13: 12 días. Su promedio es 55/12 = **4.583**; el resto, 3 − 4.583 = **−1.583**. El día 4 fue 1.6 puntos peor que sus semanas vecinas, y eso es lo que se compara, no el 3.

![Figura 6.1](figuras/fig12_tendencia_espuria.png)

*Figura 6.1. Dos variables sintéticas sin relación día a día que solo comparten un cambio lento: con los valores tal cual, r = −0.36; con los restos de ±10 días, r = +0.01. Script: `figuras/fig12_tendencia_espuria.py` (replica `Estadistica.series`).*

### 6.4.3 Variables «lentas»

`series()` marca una variable como **lenta** si su varianza original es mayor que 0, tiene al menos **14** restos (8 por semana) y var(resto) / var(x) < **0.12**. Con el interruptor encendido las lentas se omiten y la pantalla lo dice. Ejemplos: horas de luz, etapas de vida, brújula política. **[Diseño propio]** (el corte de 0.12 no viene de la literatura).

### 6.4.4 Cuándo falla

- **El ciclo de la semana no se quita (H-5-6).** Una ventana de 21 días contiene exactamente tres semanas: el promedio **borra** el patrón semanal y lo **deja entero** en los restos. Si dos variables cambian ambas con el fin de semana, su correlación sin tendencia puede deberse solo al calendario. **[Aproximación]**. Desde 10e (D.103, D.106; resuelto en parte) la ayuda del interruptor lo dice, y en R `vm_relaciones` y `vm_factores` tienen `sin_semana = TRUE`, que resta a los restos de cada variable el promedio de sus restos en su día de la semana: si tus sábados promedian +0.8 h de sueño sobre sus semanas vecinas, un sábado con +1.0 h queda en +0.2 h. La app no tiene esa opción.
- **Cambios bruscos.** Si te mudas, los restos de los ~10 días cercanos al cambio son raros.
- **Efectos lentos reales.** Si dormir mal durante semanas te deprime poco a poco, quitar la tendencia borra justo ese efecto. Por eso el interruptor existe y la tarjeta avisa cuando la correlación «con tendencia» difiere en 0.2 o más.

---

## 6.5 Los días no son independientes: el número efectivo de días

### 6.5.1 El problema

Las fórmulas clásicas suponen que cada día es una observación **independiente**. Pero tus días se parecen a sus vecinos (eso es la **autocorrelación**): una semana de exámenes tiene varios días seguidos con poco sueño y mal ánimo, y contarlos como 7 pruebas independientes hace que la relación parezca más segura de lo que es. En la simulación de la Figura 6.2, con dos series sin relación y autocorrelación de 0.8, el intervalo ingenuo «encuentra» una relación en el **35 %** de los casos en vez del 5 %.

### 6.5.2 Cómo lo hace exactamente

1. `Estadistica.autocorr` (B.4): Pearson entre cada día y el siguiente, **solo con pares de días consecutivos** con dato; con **menos de 10 pares** (o sin variación), desde 10e se supone **0.3** (`Estadistica.AR1_SIN_DATO`; antes, 0, que no corregía). Se calcula sobre restos o valores, según el modo, sobre **toda la serie** de la variable, y se recorta a [−0.5, 0.97].
2. **Días efectivos** (nEf, B.5), con *n* los días con ambos datos y *r_a*, *r_b* las autocorrelaciones:

$$ n_{Ef} = n\,\frac{1-r_a r_b}{1+r_a r_b},\qquad \text{acotado a } [\min(4,n),\ n] $$

Si nEf ≤ 3, la pareja no se evalúa.

**Por qué.** Es la aproximación clásica para la correlación entre dos series con autocorrelación de orden 1, AR(1) (el supuesto de que el parecido entre días decae geométricamente) (Bretherton et al., 1999; Pyper y Peterman, 1998). Lo que infla la incertidumbre es que **ambas** series sean persistentes; si solo una lo es, no hay inflación. **[Validado]** como aproximación de primer orden. Las cotas son prudentes: la superior impide que los días efectivos superen a los reales; el tope de 0.97 evita dividir casi entre cero. **[Diseño propio]**.

**Ejemplo.** Sueño y valencia, sin tendencia: n = 120; autocorrelaciones 0.4 y 0.3, producto 0.12. nEf = 120 × 0.88 / 1.12 = **94.3**. Como es menor que 0.8 × 120 = 96, la tarjeta escribe «120 días (≈ 94 independientes)».

![Figura 6.2](figuras/fig13_n_efectivo.png)

*Figura 6.2. Izquierda: días efectivos según la autocorrelación (la misma en ambas variables) para 30, 90 y 365 días. Derecha: 4000 parejas simuladas de series AR(1) independientes de 90 días; con la autocorrelación real de 0.8, el intervalo con n da falsas alarmas el 35 % de las veces y el de la app, el 4.6 %. Script: `figuras/fig13_n_efectivo.py`.*

### 6.5.3 Cuándo falla (H-5-13)

- **Variables esporádicas (resuelto en 10e, D.95)**: con menos de 10 pares de días seguidos, la autocorrelación se tomaba como 0 y no se corregía nada; ahora se supone 0.3, en la app y en R (`.vm_ar1_sin_dato`), salvo en las diferencias de medias, donde sigue en 0. **[Diseño propio]**: es un valor prudente, no estimado. Por ejemplo, 40 días en común, una variable sin pares seguidos y la valencia con 0.5: antes nEf = 40; ahora 40 × 0.85 / 1.15 = **29.6**. Con r = 0.35, el intervalo pasa de 0.04 a 0.60 a −0.01 a 0.63: ahora incluye el cero.
- **Toda la serie, no los días compartidos**: si una variable tiene dos años y la otra dos meses, la autocorrelación de la primera usa los dos años.
- **Persistencia más larga que un día**: la fórmula de primer orden se queda corta; quitar la tendencia reduce mucho este problema.

Desde la entrega 10c, «Cruzar dos variables» y «Todo lo que se mueve con algo» usan la misma fórmula (`Estadistica.correlacionEf`, con `Estadistica.ar1`, que también exige 10 pares; sin ellos, 0.3 desde 10e), y las diferencias de medias, el factor AR(1) de 6.10.

---

## 6.6 Intervalo de confianza y valor p: la transformación de Fisher

El **intervalo de confianza de 95 %** es el rango de valores compatibles con tus datos: si incluye el 0, podría no haber relación. El **valor p** es la probabilidad de ver una relación así de fuerte por puro azar si no hubiera ninguna. Para *r* con *n* días (o nEf), la app usa la **transformación z de Fisher** (Fisher, 1915) (B.6–B.8):

$$ z=\operatorname{atanh}(r),\qquad EE=\frac{1}{\sqrt{n-3}},\qquad IC_{95}=\big[\tanh(z-1.96\,EE),\ \tanh(z+1.96\,EE)\big] $$

`Estadistica.ic95(r, n)` exige n ≥ 5 y recorta *r* a ±0.9999. En `relaciones` se usa **nEf** en lugar de *n*, y *p* = 2 · (1 − Φ(|z| / EE)), con Φ (la normal acumulada) calculada por `Analisis.phi` con la aproximación 7.1.26 de Abramowitz y Stegun (1964), de error del orden de 10⁻⁷. **[Validado]**. La distribución de *r* es asimétrica cerca de ±1; atanh la vuelve aproximadamente normal con varianza 1/(n − 3): es el método estándar. **[Validado]**. Usarlo con nEf es la combinación habitual. **[Aproximación]**.

**Ejemplo (continúa el de 6.5.2).** r = 0.30, nEf = 94.29: z = 0.3095; EE = 0.1047; IC = **0.10 a 0.47**; z/EE = 2.957 → **p = 0.0031**. Con *n* = 120 sin corregir habría salido IC 0.13 a 0.45 y p = 0.0008: casi cuatro veces «más significativo» de lo que es.

**La t de Student.** Para las diferencias de medias, el modelo múltiple y el día de la semana, la app usa la **t de Student**, la curva que reemplaza a la normal cuando la variabilidad se estima con pocos datos: tiene colas más gruesas cuanto menos grados de libertad (con 5, el 95 % central llega a ±2.57; con 30, a ±2.04; con muchos, a ±1.96). `Estadistica.pT` y `Estadistica.cuantilT` la calculan de forma exacta (B.14a), comprobada contra las tablas en `CalculosPrueba.cuantilesDeT`. **[Validado]**.

**Cuándo falla.** El intervalo de la correlación usa 1.96 aun con nEf pequeño; con Fisher es lo estándar y el error es pequeño frente al de no corregir por días parecidos. **[Aproximación]**. Para variables 0/1 con pocos «1» la aproximación es menos precisa; la app exige al menos **4 días de cada valor** para evaluarlas.

---

## 6.7 Muchas comparaciones a la vez: Benjamini-Hochberg

### 6.7.1 El problema

Con 300 variables hay 300 × 299 / 2 = **44 850** parejas. Si ninguna tuviera relación real, con el umbral clásico p < 0.05 saldrían **unas 2 240 «relaciones» por puro azar**.

### 6.7.2 Cómo lo hace exactamente

`Analisis.bh(p)` (B.9) ordena los *m* valores p de menor a mayor y calcula

$$ q_{(k)} = \min_{j\ge k}\ \min\!\Big(1,\ p_{(j)}\,\frac{m}{j}\Big) $$

(recorre de abajo hacia arriba guardando el mínimo, así q nunca baja cuando p sube). Una relación «se sostiene» si **q < 0.10**, que quiere decir, aproximadamente: «de todas las relaciones que marco como firmes, espero que a lo más un 10 % sean azar». En `relaciones` hay dos q: `q`, solo entre las parejas no obvias (por omisión), y `qConObvias`, entre todas. «Tu año» exige q < 0.05, n ≥ 30 y al menos 40 días en el año.

### 6.7.3 Por qué

Es el procedimiento de Benjamini y Hochberg (1995). No controla «la probabilidad de equivocarme alguna vez» (eso sería Bonferroni, mucho más estricto), sino la **proporción esperada de falsos descubrimientos** (FDR) entre lo que se marca como firme; el *q* es cercano al de Storey y Tibshirani (2003). Es apropiado para explorar. **[Validado]**. **Supuesto:** BH garantiza su control si las pruebas son independientes o tienen cierta dependencia positiva (Benjamini y Yekutieli, 2001); aquí **comparten variables** (la valencia aparece en cientos de parejas). En la práctica BH suele comportarse bien así, pero la garantía formal no está demostrada para este caso. **[Aproximación]**.

### 6.7.4 Ejemplo

Seis parejas (m = 6):

| k | p | p · 6 / k | q |
|---|---|---|---|
| 1 | 0.0004 | 0.0024 | 0.0024 |
| 2 | 0.003 | 0.009 | 0.009 |
| 3 | 0.011 | 0.022 | 0.022 |
| 4 | 0.02 | 0.030 | 0.030 |
| 5 | 0.04 | 0.048 | 0.048 |
| 6 | 0.30 | 0.300 | 0.300 |

Las cinco primeras tienen q < 0.10. Con m = 44 850, la mejor pareja necesita p < 0.10/44 850 = 2.2 × 10⁻⁶ si es la única real; con nEf = 100 eso equivale a |r| ≳ **0.45**. Por eso la app avisa: «Con pocos días es normal que nada se sostenga».

![Figura 6.3](figuras/fig14_benjamini_hochberg.png)

*Figura 6.3. 200 pruebas simuladas (20 relaciones reales con r = 0.35, 180 sin relación, 90 días). Sin corregir: 19 aciertos y 9 falsas alarmas (32 % de lo marcado es falso). Con BH a q < 0.10: 15 aciertos y 1 falsa alarma (6 %). Script: `figuras/fig14_benjamini_hochberg.py` (replica `Analisis.bh`).*

### 6.7.5 Cuándo falla

- **El número de pruebas depende del modo** (por día o por semana, con o sin tendencia), así que la misma pareja puede tener q distinto en cada modo.
- **Excluir las obvias no es neutral**: baja *m* y hace la corrección más permisiva. Está bien porque no son descubrimientos, pero la lista debe estar completa y ser la misma en app y R (H-5-2; desde 10d lo es, 6.8).

---

## 6.8 Las relaciones «obvias» y sus familias

**Qué hace.** Marca las parejas cuya correlación es casi segura por construcción, porque salen de la misma fuente o se calculan una de otra: calorías con proteína, pasos con minutos a pie, «minutos en Instagram» con «minutos de pantalla».

**Cómo.** `Estadistica.obvia(a, b)` devuelve verdadero si se cumple **cualquiera** de diez reglas; las principales: ambas están en una misma familia de `Estadistica.FAMILIAS` (59 conjuntos desde 10e, 58 en la v90, como todos los nutrientes fijos, `valencia` con `animo_valencia` o, desde 10f, `fuerza_ref_kg` con las demás del gimnasio); una es dinámica y la otra uno de sus **totales** (`Dinamicas.AGREGADOS`: `app_*` con `pantalla_min`); `per_X` y `pen_X` de la misma persona; dos puntajes del mismo cuestionario (las diez, en la versión completa, §6.9.2). Las obvias se calculan igual pero se esconden («Mostrar también las obvias») y no cuentan en BH por omisión.

**Por qué y cuándo falla.** No hay regla estadística para decidir qué es «obvio»: es una lista curada a mano. **[Diseño propio]**. Olvidar una familia hace que aparezca como «hallazgo» algo trivial; marcar como obvio algo que no lo es esconde una relación interesante. Caso discutible: se declaran obvias **todas** las parejas entre sentimientos, aunque «registros con tristeza» con «registros con alegría» podría ser informativa.

**En R (H-5-2, resuelto en 10d).** La exportación escribe `vm_familias.csv`, generado por `Estadistica.familias()` con las **mismas** diez reglas, y `vm_obvia` lo lee. Cada familia tiene un **lado**: con lado 0 todas sus variables son obvias entre sí; con lados 1 y 2, cada una de un lado lo es con cada una del otro, pero no con las de su lado. Así `per_ana` es obvia con `personas` y con `palabras` sin que `personas` y `palabras` lo sean entre sí. `ExportacionPrueba.familiasIgualQueObvia` comprueba, con 45 variables inventadas, que da lo mismo que `obvia()` en cada par. **[Diseño propio]**.

---

## 6.9 Las herramientas de relaciones

### 6.9.1 «Las relaciones más marcadas» (`Estadistica.relaciones`)

Con `minN = 14`, `paso = 1` y `ventana = 10`:

1. `series()` prepara cada variable con al menos 3 días: restos (6.4), autocorrelación (6.5), si es lenta y si es 0/1. Con el interruptor encendido, aparta las lentas.
2. Para cada pareja toma los días en que **ambas** tienen valor (*n*); la salta si *n* < **14**, o si una variable 0/1 tiene **menos de 4** días de alguno de los dos valores. Calcula *r*, `bruta` (Pearson sobre los valores), nEf, intervalo y *p*, y marca si es obvia.
3. Calcula `q` y `qConObvias` con BH.
4. **Ordena** por lo «seguro»: qué tan lejos del cero queda el extremo **más cercano** del intervalo (0 si lo cruza); a igualdad, por |r|.

La **tarjeta** (`ui/Relacion.kt`) une los dos nombres con un lazo verde (r ≥ 0) o rojo, de grosor (1.2 + 4.5 · |r|) dp, **punteado** y con 65 % de opacidad si q ≥ 0.10 («podría ser azar»); dice «(≈ nEf independientes)» si nEf < 0.8 · n y «con tendencia: ±0.xx» si |bruta − r| ≥ 0.2.

**Ejemplo.** Sueño y valencia: 120 días, r = 0.30, nEf = 94.3, IC 0.10 a 0.47, p = 0.0031. Si se probaron m = 2 000 parejas no obvias y esta queda en el lugar 25 por p, su q es como mucho 0.0031 × 2000 / 25 = 0.25: la tarjeta sale **punteada**, «podría ser azar», aunque su intervalo no incluya el cero. **No es una contradicción**: el intervalo es el de esa pareja sola; q tiene en cuenta que se miraron 2 000.

**Por semana** (`Estadistica.semanal`, B.10): semanas de lunes a domingo, cada variable el promedio de sus días con dato (al menos 3), y `relaciones(…, minN = 8, paso = 7, ventana = 35)`. Sirve para lo que pasa pocas veces. **[Diseño propio]**. Con 8 semanas solo se ve una correlación enorme. Desde 10e (D.103; H-9-40), tocar una relación de este modo la abre en «Cruzar dos variables» por semanas, no en la ficha por día.

### 6.9.2 «Cruzar dos variables»

Eliges X e Y y ves su dispersión, r de Pearson, la r sin tendencia, ρ de Spearman y el número de días (`Estadistica.pares`; con desfase, Y es la del día siguiente de calendario). El intervalo sale de `Estadistica.correlacionEf` (Fisher sobre **nEf**; requiere 5 días). La r sin tendencia (`Estadistica.sinTendencia`, ±10 días) se muestra aparte, sin intervalo; si difiere de la bruta en 0.2 o más, el texto lo explica. Con menos de 30 días dice «Solo son N días: tómalo como pista». Para una sola pareja elegida por ti no hace falta BH. **[Aproximación]**. Desde 10e, `vm_cruce` en R da lo mismo.

### 6.9.3 «Todo lo que se mueve con algo» (`Analisis.factores`)

Eliges un objetivo (tu ánimo, una persona, un lugar) y la app revisa **todas** las demás variables contra él, con un interruptor «Quitar tendencias»:

1. Prepara el objetivo: sus restos sin tendencia (±10 días) si el interruptor está encendido, y su autocorrelación (`Estadistica.ar1`).
2. Salta cada variable de la misma familia, obvia con el objetivo o con menos de 10 días.
3. Si es 0/1: compara el objetivo en los días con 1 y con 0 (al menos **5** de cada uno) con `Estadistica.diferenciaEnDias` (6.10).
4. Si es continua: con al menos **14** pares, r de Pearson con intervalo y *p* sobre **nEf**, igual que la lista.
5. Corrige los *p* con BH; marca con ✓ los de q < 0.1; muestra las 20 primeras por *p*.

El desfase de un día ayuda con la dirección: si lo de hoy se asocia con el ánimo de mañana, es menos probable que el ánimo haya causado lo otro (aunque no imposible). **[Aproximación]**. **Cuándo falla (H-5-3, resuelto en 10c).** El *q* se calcula entre las variables revisadas contra **este** objetivo, no entre todas las parejas, así que una pareja puede tener ✓ aquí y salir punteada en la lista sin contradicción. La variable 0/1 no se «desestaciona»: si casi todos sus 1 caen en una temporada, la comparación la hereda en parte. `vm_factores` en R hace lo mismo desde 10d (`ExportacionPrueba.factoresComoR`).

---

## 6.10 La diferencia de medias: t de Welch y factor AR(1)

### 6.10.1 Qué hace y cómo

`Estadistica.diferenciaCompleta` y `diferenciaEnDias` comparan el promedio de dos grupos de días y dicen qué tan seguro es que la diferencia no sea azar, teniendo en cuenta que con pocos días la incertidumbre es mayor de lo que dice la normal y que los días seguidos se parecen. Con al menos 3 valores por grupo (B.11 y B.11a):

1. Medias y varianzas muestrales (con n − 1) de cada grupo.
2. **Autocorrelación de cada grupo** (`Estadistica.ar1Grupos`): la del grupo si tiene al menos 10 pares de días seguidos; si no, la de los dos grupos juntos, cada uno centrado en su media; si tampoco, 0. Como en series cortas sale **más baja** que la real, se corrige: ρ̂ + (1 + 4ρ̂)/n, con n los días del grupo (o de los dos). Se acota a **[0, 0.9]**: solo corrige hacia arriba.
3. **Error estándar con el factor AR(1)** √((1 + ρ)/(1 − ρ)) (`Estadistica.factorAR1`):

$$ EE=\sqrt{\frac{s_a^2}{n_a}\,\frac{1+\rho_a}{1-\rho_a}+\frac{s_b^2}{n_b}\,\frac{1+\rho_b}{1-\rho_b}} $$

4. **Grados de libertad de Welch** con los días efectivos de cada grupo, n·(1 − ρ)/(1 + ρ) (mínimo 2), acotados entre 1 y su suma menos 2.
5. **Intervalo** d ± t(0.975; gl) · EE y **p** de dos colas con la t exacta.

Con ρ = 0 es la prueba t de Welch (1947) clásica. **[Validado]** la t de Welch; **[Aproximación]** el factor AR(1) (supone que el parecido decae de forma geométrica) y la corrección de sesgo, que se suele atribuir a Marriott y Pope (1954): **no se pudo leer el original** [sin verificar], y se justificó por simulación.

### 6.10.2 Ejemplo: días que se parecen

Los últimos 30 días tienen media 4.6 y los 30 anteriores 4.0, ambos con DE 1.0; autocorrelación estimada de cada grupo, 0.45.

- Sin corregir: EE = 0.258; con 1.96, IC **0.09 a 1.11**: «El cambio es claro».
- Corrección de sesgo: 0.45 + (1 + 4 × 0.45)/30 = **0.543**. Factor √(1.543/0.457) = 1.838; días efectivos por grupo, 8.9.
- EE = 0.475; gl = 15.8; t = 2.123. IC **−0.41 a 1.61**: «podría ser variación normal».

En la simulación de `CalculosPrueba.coberturaConDiasParecidos` (2 000 réplicas sin cambio real, 30 + 30 días, ρ = 0.5), el intervalo «de 95 %» cubre el cero el **74 %** de las veces sin corregir, el **92 %** con el factor AR(1) sin corrección de sesgo, y alrededor del **95 %** con las dos.

### 6.10.3 Dónde se usa y cuándo falla

La usan **Cambio reciente** (`Analisis.cambio`: los últimos 30 días contra los 30 anteriores), **Antes y después** (los 60 días anteriores contra los 60 siguientes a un cambio en «Conocerte»; estudio observacional, capítulo 8), «Todo lo que se mueve con algo» (0/1) y los experimentos (capítulo 8). **Día de la semana** no la usa: con al menos 5 valores por día, media ± t(0.975; n − 1) · DE/√n.

Hasta la entrega 10c, «Tendencia», «Antes y después» y «Día de la semana» trataban cada día como independiente y usaban 1.96 fijo (H-5-5, resuelto). Lo que sigue: falla si un periodo entero es distinto (un mes de vacaciones dentro de los 30 días), que la autocorrelación de orden 1 no captura del todo; con menos de 10 pares de días seguidos, ρ = 0 y solo queda la t (el 0.3 de 10e es solo para las correlaciones); y «Antes y después» compara periodos que también difieren en estación y en todo lo demás que cambió a la vez. **[Aproximación]**.

---

## 6.11 Las demás herramientas

**Modelo múltiple** (`Analisis.modelo`, B.16). Pregunta si el sueño se asocia con el ánimo **manteniendo fijos** otros predictores. Usa solo días completos, exige n ≥ k + 10, estandariza cada columna y resuelve mínimos cuadrados sin intercepto; el intervalo es β ± t(0.975; n − k − 1) · EE, y muestra el R² ajustado. Opcionalmente incluye el valor del objetivo el día anterior: es la idea de los modelos autorregresivos con predictores (Hamaker y Wichers, 2017). **[Aproximación]**: no tiene errores autocorrelados ni es multinivel, no quita tendencias y supone residuos independientes (H-5-11).

**Dinámica emocional** (`Analisis.dinamica`). Con la `valencia` del diario y al menos 7 días: media, DE, **MSSD** (promedio de los cuadrados de las diferencias entre días consecutivos, B.12) e **inercia** (Pearson entre cada día y el siguiente, con al menos 10 pares, B.13). Son índices clásicos (von Neumann et al., 1941; Jahng et al., 2008; Kuppens et al., 2010). **[Validado]** como índices, pero en muestras grandes añadieron poca información sobre el bienestar más allá de la media y la DE (Dejonckheere et al., 2019) y aquí se calculan con un valor por día. **[Aproximación]**. Desde 10e (H-5-12, resuelto) la tarjeta llama «muy difíciles» a los días de 1 o 2, distintos de la «racha dura» (3 o menos), y `vm_dinamica` en R calcula cada mes solo con pares del mismo mes, como la app.

**Palabras del diario** (`Analisis.palabras`, B.18): la diferencia de valencia de los días con cada palabra (en al menos 5 días), sin diccionario de emociones (a diferencia de LIWC; Tausczik y Pennebaker, 2010) ni prueba. **[Diseño propio]**. Con 5 días, la media varía ±0.9 puntos solo por azar si tu DE es 1 (H-5-10).

**Días atípicos** (`Analisis.atipicos`, B.14): z robusto con la mediana y la MAD × 1.4826 (una DE que no se deja arrastrar por los extremos) en 15 variables con al menos 20 días; atípico si |z| ≥ 3 (Leys et al., 2013). **[Validado]**. Revisa el último año; «Tu año», desde 10e (D.100; H-14-3), todos los días del año elegido.

El ritmo de sueño está en el capítulo 5; las cifras descriptivas, en la versión completa, §6.20.

---

## 6.12 Potencia: por qué 14, 30 y 90 días

La **potencia** es la probabilidad de detectar una relación que **sí existe**. La pantalla dice: «Con 90 días o más ya se puede detectar una relación moderada en una pareja concreta; en esta lista, que revisa todas a la vez, solo las fuertes». La correlación más pequeña detectable con potencia de 80 % y α = 0.05 es (B.15)

$$ |r_{\min}|=\tanh\!\Big(\frac{1.96+0.84}{\sqrt{n_{Ef}-3}}\Big) $$

| Días | Días independientes | Autocorrelación 0.5 en ambas (nEf = 0.6 n) | Igual, con α = 0.001 |
|---|---|---|---|
| 14 | 0.69 | 0.84 | 0.94 |
| 30 | 0.49 | 0.62 | 0.79 |
| 90 | **0.29** | 0.37 | 0.52 |
| 365 | 0.15 | 0.19 | 0.27 |

La frase es **correcta para una sola relación con días independientes**: con 90 días se detecta |r| ≈ 0.29. **[Validado]** en ese sentido estricto. En la lista los días no son independientes y se revisan miles de parejas, así que el umbral efectivo se parece más a la última columna: con 90 días solo se sostienen relaciones de 0.5 o más. Los 14 días son un **mínimo para calcular**, no para creer; los 30, una convención razonable para «pista». **[Diseño propio]**. El aviso cuenta días con diario, no días con ambos datos de cada pareja (H-5-16).

---

## 6.13 App y R

Desde la entrega 10d, `vestigium.R` analiza el mismo panel que la app (`vm_panel_extendido.csv`, con once nombres dobles documentados en `Exportar.EQUIVALENTES`; H-5-1, resuelto), con las mismas variables (`vm_variables.csv`) y las mismas obvias. `vm_relaciones` y `vm_factores` aplican el mismo algoritmo que la app; `ExportacionPrueba.kt` lo fija con datos inventados, y el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de la pantalla (28 oct 2026). Desde 10e, `vm_cruce` también coincide con «Cruzar dos variables» (H-5-7), `vm_experimento` recalcula los experimentos y agrega una prueba de aleatorización (desde 10f, también el criterio sellado), y `vm_relaciones(sin_semana = TRUE)` quita el ritmo de la semana, que la app no puede quitar; `vm_modelo_ar1` solo existe en R (Pinheiro et al., 2026). Tabla función por función en la versión completa, §6.22.

---

## 6.14 Qué protege y qué no

En «Las relaciones más marcadas» y «Todo lo que se mueve con algo», las protecciones cubren **las tendencias** (±10 días, 6.4), **los días parecidos** (nEf, 6.5, o el factor AR(1), 6.10) y **el azar de las muchas pruebas** (BH a q < 0.10, 6.7, sin las obvias, 6.8).

**No** cubren:

- **confusores**: una tercera cosa que mueve a las dos, como el día de la semana o estar enfermo;
- **la dirección**: ¿el sueño mejora el ánimo o el buen ánimo te deja dormir? El desfase de un día solo ayuda;
- **el ciclo semanal**, que quitar la tendencia deja entero en la app (H-5-6; en R lo quita `sin_semana = TRUE`), ni **los efectos lentos reales**, que borra;
- **los faltantes no aleatorios**: si dejas de escribir los días malos, todo se calcula con los días que quedan.

Además, la garantía formal de BH no está demostrada con pruebas que comparten variables. Una relación firme en la lista es una **pista seria, no una prueba de causa**.

---

## 6.15 En resumen

1. Todo sale de un **panel** de un renglón por día, con una regla general para distinguir «0» de «no sé»: 0 desde el primer registro y donde se pudo responder (`ceroDesde`).
2. «Las relaciones más marcadas» **quita tendencias** (±10 días), **descuenta los días parecidos** (nEf) y **corrige por miles de comparaciones** (BH, q < 0.10), sin contar las obvias.
3. «Todo lo que se mueve con algo» usa la misma maquinaria, y las diferencias de medias usan **t de Welch** con el factor AR(1).
4. **El ciclo semanal no se quita** en la app (H-5-6; en R, `sin_semana`), y las etapas de vida nunca aparecen como relación sin tendencia: desde 10e la lista remite a «Antes y después» (H-5-14).
5. Desde 10d, **R analiza el mismo panel**, con las mismas variables y obvias: sirve para verificar la app.
6. Con 90 días independientes se ve una relación de 0.3; con miles de parejas y días parecidos, hace falta más de 0.5.
7. Ninguna protección cubre confusores ni dirección: lo firme es una pista, no una causa.
