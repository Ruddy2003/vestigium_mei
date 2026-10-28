# Capítulo 8. Experimentos personales (N-de-1)

> **Para qué sirve este capítulo.** Explica cómo la app te ayuda a **probar algo en ti mismo** (dormirte antes, tomar creatina) y a decidir si funcionó: la lógica de estos diseños, qué hace exactamente el código (sorteo de semanas, días de lavado, diferencia de medias, tamaño del efecto, prueba de tendencia) y qué tan confiable es el resultado. También trata «Antes y después» de Explorar, que **parece** un experimento pero no lo es. Describe el código de la v90 (commit `8818d5a`, `SCHEMA_V = 14`). Los hallazgos se numeran H-7-n y están en `hallazgos.md`. Los ejemplos y las **simulaciones** (miles de experimentos inventados en los que *sabemos* la verdad) usan datos inventados.

---

## 8.1 Por qué experimentar en uno mismo

El resto de la app encuentra **relaciones**: «los días que duermes más, estás de mejor ánimo». Pero una relación no dice qué causa qué: quizá dormir más mejora el ánimo, quizá el buen ánimo te deja dormir, quizá los fines de semana causan ambas cosas.

Un **experimento** rompe ese nudo: **tú decides** cuándo hacer la acción, y si además lo decide **el azar** (si se **aleatoriza** o sortea), lo demás (el día de la semana, tu humor previo, el clima) queda repartido entre las dos **condiciones**: con la acción («SÍ») y sin ella («no»). Si después hay diferencia, lo más probable es que la cause la acción.

Los **ensayos N-de-1** son ensayos aleatorizados en **una sola persona** («N» es el número de participantes; aquí, 1), que se compara consigo misma en periodos con y sin la acción. Se propusieron en medicina para elegir el tratamiento de un paciente concreto (Guyatt et al., 1986); la guía de la AHRQ explica cómo diseñarlos (Kravitz et al., 2014) y la extensión CONSORT para N-de-1 (CENT 2015) dice qué hay que reportar (Vohra et al., 2015). En psicología existen como **diseños de caso único**, a los que se ha insistido en añadir aleatorización (Kratochwill y Levin, 2010). **[Validado]**: la lógica de alternar condiciones al azar en una misma persona está bien establecida.

**Cuándo sirven**: la acción tiene un efecto **rápido** que también **se va rápido**; la medida se toma **muchas veces** (diaria, idealmente); el problema es **estable** durante el experimento; y la alternancia se puede **repetir**. La creatina no las cumple (sección 8.8).

---

## 8.2 Cómo se crea y se sella un experimento

**Lo que escribes** (`PantallaExperimentos`, modelo `Experimento`). Nombre, pregunta (opcional), **acción** e **hipótesis**; **una sola medida** del panel, de una lista corta (valencia, sueño, pantalla, tiempo de reacción, volumen en el gimnasio, enfoque…); el **diseño**, «Semanas alternas sorteadas» (`alterno`) o «Antes y después» (`antes_despues`: un periodo sin la acción y luego uno con ella); la **duración**, 4, 6, 8 o 12 semanas; y los **días de lavado** tras cada cambio: ninguno, 1, 2, 7 o 28 (sección 8.4).

Si con esa combinación **podría** quedar alguna condición con menos de **7 días que cuentan** (`Experimentos.MIN_DIAS`), la pantalla lo avisa y **no deja sellar**. `Experimentos.minimoPosible` prueba **todos** los planes que el sorteo podría dar y toma el peor, así que no se puede «sortear otra vez hasta que salga bien». El mínimo de 7 es **[Diseño propio]**: una semana completa, para que cada condición tenga al menos un día de cada día de la semana.

**El sellado.** Al tocar «Sellar y empezar el lunes» se guarda la hora exacta (`sellada = Fechas.ahoraIso()`), se **sortea el plan** (sección 8.3) y el experimento empieza **el lunes siguiente**: `inicio = lunesDe(hoy) + 7 días`. Después no se pueden editar la pregunta, la hipótesis, la variable ni el plan, ni borrar el experimento. Es un **prerregistro** casero: escribir la pregunta, la hipótesis y qué vas a medir **antes** de ver los datos, para no acomodar la conclusión a lo que salga (Simmons et al., 2011; Nosek et al., 2018). **[Diseño propio]** inspirado en una práctica **[Validado]**. Límites (H-7-10): no hay huella del texto sellado (el sello es de **honor**), y se fija la variable, pero **no el análisis**: ni cuándo vas a mirar ni qué vas a considerar «funcionó».

**El aviso diario** (`AvisoExperimento`). En los días que cubre el experimento (`Experimentos.cubre`), nunca en días futuros, el día dice qué toca esa semana, si es día de lavado, y pregunta **«¿Lo hiciste hoy?»** («sí» / «no») **en las dos condiciones**. La respuesta se guarda en `Experimento.hecho`. Preguntar cada día puede cambiar la conducta (reactividad); no se pudo estimar cuánto.

---

## 8.3 El sorteo del plan (`Experimentos.plan`)

- **Alterno**: se sortea **por pares de semanas**. Las semanas se agrupan de dos en dos (1–2, 3–4…) y en cada par se sortea, con la misma probabilidad, si va «SÍ, no» o «no, SÍ»; con un número impar de semanas, la última se sortea sola. Hay $2^{N/2}$ planes posibles: 4 con 4 semanas, 8 con 6, 16 con 8 y 64 con 12.
- **Antes y después**: primero ⌊*N*/2⌋ semanas «no» y luego el resto «SÍ». **No se sortea nada.**

**[Validado]** como aleatorización. **Un acierto del diseño**: como todo empieza en lunes y cada condición dura semanas completas, cada condición tiene la misma cantidad de lunes, martes…, y el efecto del día de la semana no se cuela en la comparación.

**Por qué por pares** (H-7-5, resuelto). Hasta la entrega 10c del módulo 10, las semanas se barajaban libremente, y podía salir «SÍ SÍ SÍ no no no», que en la práctica es un antes y después: con 4 semanas, el **33 %** de las veces. Por pares, nunca hay más de dos semanas seguidas iguales, y una tendencia lenta afecta casi por igual a ambas condiciones. Con una tendencia de 0.02 desviaciones por día y sin efecto real, el sorteo libre de 8 semanas daba una falsa señal el **14 %** de las veces y el sorteo por pares, alrededor del **5 %** (con el método de cálculo anterior a la 10c; versión completa, §8.4.2). Si un plan sellado antes con sorteo libre quedó en bloque (`Experimentos.enBloque`), la tarjeta avisa que se lee como un antes y después (H-9-30).

---

## 8.4 Qué días cuentan: el lavado

El **lavado** (*washout*) son días que **no cuentan** después de cada cambio de condición, para evitar el **arrastre** (*carryover*): que el efecto de una condición siga actuando cuando ya empezó la otra.

**Las reglas** (`Experimentos.toca` y `Experimentos.enLavado`, `data/Lote3.kt`). Con *d* = días desde `inicio`, la semana es ⌊*d*/7⌋ y la condición es `plan[semana]`. Los **días de cambio** *c* son el día 0 **si** el plan empieza con «SÍ», y el primer día de cada semana cuyo valor difiere del de la anterior. Un día es de lavado si $0 \le d - c < L$ para algún cambio, con *L* = `lavado_dias`. El lavado va **en los dos sentidos**: de «SÍ» a «no» (para que se vaya el efecto) y de «no» a «SÍ» (para que tenga tiempo de aparecer).

*Ejemplo.* Plan SÍ no no SÍ SÍ no, 2 días de lavado. Cambios: días 0, 7, 21 y 35; días de lavado: 0–1, 7–8, 21–22 y 35–36. Cuentan **17 días con la acción** (21 − 4) y **17 sin ella**; la semana 3 («no» tras «no») no pierde ningún día.

**Razonamiento.** Excluir los primeros días de cada periodo es una forma estándar de lidiar con el arrastre. **[Aproximación]** razonable: la duración correcta depende de cada acción, y la app la deja a tu criterio (o a la plantilla).

**El problema grave que tuvo** (H-7-1, resuelto en la 10c). Antes del mínimo de 7 días, el lavado podía **comerse una condición entera**: la plantilla «Creatina» (entonces de 8 semanas, antes y después, 28 días de lavado) contaba **28 días sin la acción y 0 con ella**, y no podía dar resultado nunca. Ahora dura 12 semanas: cambio el día 42, lavado de los días 42 a 69, y cuentan **42 días sin la acción y 14 con ella** (calendarios en la versión completa, §8.5).

---

## 8.5 El resultado: diferencia, intervalo y tamaño del efecto

### 8.5.1 Qué calcula (`Experimentos.resultado` y `Estadistica.diferenciaEnDias`)

Recorre los días desde `inicio` hasta el último día del plan, hasta hoy o hasta el día en que lo terminaste (`Experimento.fin`), lo que llegue antes; **hoy no entra**, porque las variables que se acumulan (pantalla, pasos, enfoque) estarían a medias. Cada día que está en el experimento, no es de lavado y **tiene dato** va a la lista «con» o «sin».

**1. Diferencia de medias con su intervalo**, si hay **al menos 3 días en cada condición**. Un **intervalo de confianza del 95 %** es un rango de valores plausibles para el efecto: si se repitiera el experimento muchas veces con el mismo método, el 95 % de esos rangos contendría el efecto real… **si los supuestos se cumplen**. El supuesto delicado es la **autocorrelación**: que un día se parezca al anterior. La de orden 1, *ρ*, va de −1 a 1; 0 significa días independientes. El cálculo:

- *ρ* de cada condición (`Estadistica.ar1Grupos`): la correlación entre pares de días **seguidos** de esa condición, si hay al menos 10 pares; si no, la de las dos condiciones juntas (cada una centrada en su media); si tampoco, 0. Como en series cortas sale más baja que la real, se corrige: *ρ* + (1 + 4*ρ*)/*n* (la aproximación de primer orden atribuida a Marriott y Pope, 1954; la forma exacta no se leyó en el artículo **[sin verificar]**), y se acota entre 0 y 0.9: solo se corrige hacia un intervalo más ancho.
- El error estándar de cada condición se multiplica por √((1 + *ρ*)/(1 − *ρ*)), y su número efectivo de días es *n*(1 − *ρ*)/(1 + *ρ*), nunca menos de 2 (`Estadistica.diferenciaCompleta`):

$$EE = \sqrt{\frac{s_c^2}{n_c}\cdot\frac{1+\rho_c}{1-\rho_c} + \frac{s_s^2}{n_s}\cdot\frac{1+\rho_s}{1-\rho_s}}$$

- Los grados de libertad son los de Welch con esos *n* efectivos, y el intervalo usa el cuantil de la *t* de Student (`Estadistica.cuantilT(0.975, gl)`):

$$\bar{x}_c - \bar{x}_s \;\pm\; t_{0.975,\,gl}\cdot EE$$

donde $\bar{x}$ son las medias, $s^2$ las varianzas muestrales y *n* los días de cada condición.

**2. Mensaje.** Mientras dura el plan (`Experimentos.enCurso`), la tarjeta dice «Provisional: el plan sigue en curso. Mirar el resultado a medio camino y parar en la primera «señal» engaña; espera a que termine», sin veredicto. Al terminar: si el intervalo no toca el cero, «es una señal para ti»; si lo toca, «todavía no se distingue del azar».

**3. d de Cohen**: la diferencia medida en «desviaciones estándar», cuántas veces el vaivén normal de un día a otro (Cohen, 1988), con la desviación agrupada $s_p$:

$$d = \frac{\bar{x}_c - \bar{x}_s}{s_p}, \qquad s_p = \sqrt{\frac{(n_c-1)s_c^2 + (n_s-1)s_s^2}{n_c+n_s-2}}$$

La palabra (`Experimentos.tamano`): |*d*| < 0.2 «muy pequeño», < 0.5 «pequeño», < 0.8 «mediano» y desde 0.8 «grande», los puntos de referencia de Cohen (1988).

**4. Prueba de tendencia** (sección 8.7).

### 8.5.2 Ejemplo trabajado (Figura 8.1, izquierda)

Experimento inventado: 6 semanas alternas (SÍ no no SÍ SÍ no), 1 día de lavado, medida = valencia del día, con un efecto real de +0.5 y autocorrelación real de 0.3. Cuentan 19 días por condición. Medias 4.84 y 4.26: diferencia **+0.58**. Varianzas 0.474 y 0.871; $s_p$ = 0.820 y *d* = **0.71**, «mediano».

- Autocorrelación de los pares de días seguidos: 0.098 («con») y 0.408 («sin»). Corregidas: 0.098 + (1 + 4 × 0.098)/19 = **0.172** y 0.408 + (1 + 4 × 0.408)/19 = **0.547**.
- Varianza de cada media, inflada: 0.474/19 × 1.172/0.828 = 0.0353 y 0.871/19 × 1.547/0.453 = 0.1564; error estándar √(0.0353 + 0.1564) = **0.438**.
- Días efectivos: 13.4 y 5.6; grados de libertad ≈ **6.7**; *t* = **2.38**.
- Intervalo: 0.58 ± 2.38 × 0.438 → **−0.46 a +1.62**. **Toca el cero**: no se distingue del azar, aunque sabemos que el efecto real era +0.5.

Con el método anterior a la 10c (días independientes y 1.96 fijo, H-7-3), daba +0.06 a +1.10: «señal». La *ρ* estimada con 19 días es muy ruidosa: el intervalo es honesto sobre lo poco que dicen 6 semanas, a costa de ser ancho. **[Aproximación]**: el factor es exacto para la media de una serie con autocorrelación de orden 1 y aproximado para una diferencia entre bloques de semanas.

### 8.5.3 Por qué hacía falta corregirlo

El ánimo, el sueño y casi todo lo que mide la app **se parecen de un día al siguiente**, y lo que se sortea son **semanas**: en 6 semanas hay 6 unidades sorteadas, no 38. Tratar cada día como independiente estrecha el intervalo de más. Y si miras cada día y te detienes en la primera «señal», el intervalo ya no significa lo que dice: mirar repetidamente datos que se acumulan aumenta mucho la probabilidad de una **falsa señal** (decir «hay un efecto» cuando no lo hay, que un intervalo del 95 % bien hecho hace el 5 % de las veces) (Armitage et al., 1969; H-7-4).

**Cuánto importa (Figura 8.1, derecha).** En 3000 experimentos simulados de 6 semanas alternas con 1 día de lavado **en los que la acción no hace nada**:

| Situación | Falsas señales antes de la 10c | Desde la 10c | Lo prometido |
|---|---|---|---|
| Días independientes, mirar solo al final | 6 % | 3 % | 5 % |
| Autocorrelación 0.3, mirar al final | 14 % | 4 % | 5 % |
| Autocorrelación 0.5, mirar al final | 24 % | 3 % | 5 % |
| Autocorrelación 0.3, detenerse en la primera «señal» | 50 % | — (provisional) | 5 % |

La autocorrelación de 0.3 a 0.5 es ilustrativa **[sin verificar]**. **El método actual es conservador**: da menos del 5 % de falsas señales. **El costo es el poder** (la probabilidad de ver un efecto que sí existe): con autocorrelación 0.3 y un efecto real de 0.5 desviaciones, 6 semanas lo detectan el **13 %** de las veces; con un efecto de 0.8, el **29 %**; con 12 semanas, el **30 %** y el **62 %**.

![Figura 8.1](figuras/fig21_resultado_experimento.png)

*Figura 8.1. Izquierda: un experimento inventado con su resultado calculado como antes de la 10c, con cada corrección por separado y como desde la 10c. Derecha: con qué frecuencia el método de la app da una «señal» cuando la acción no hace nada, antes y desde la 10c. Script: `figuras/fig21_resultado_experimento.py`.*

### 8.5.4 Qué sería más adecuado: la prueba de aleatorización

**[Validado]** en la literatura de caso único (Kratochwill y Levin, 2010; Tanious y Onghena, 2019): calcular la diferencia con el plan que de verdad se sorteó y con **cada uno de los otros planes que pudieron salir**; el valor *p* es la fracción de planes que dan una diferencia igual o mayor que la real. **No supone que los días sean independientes**, porque se apoya solo en que el sorteo fue real. Su límite: el *p* más pequeño posible es 1 / número de planes, que con el sorteo por pares es 0.25 con 4 semanas, 0.125 con 6, 0.0625 con 8 y 0.016 con 12: **con 4, 6 u 8 semanas no se puede llegar a *p* < 0.05**, por grande que sea el efecto. Es una **propuesta no aplicada**: no está en la app ni en R (H-7-11).

### 8.5.5 El d de Cohen en una sola persona

No lleva la corrección de Hedges (1981) para el *d* inflado con pocos datos, que importa solo con muy pocos días (H-7-7, documentado). Más importante: Cohen propuso 0.2, 0.5 y 0.8 para diferencias **entre personas**; aquí la desviación es la de **tus días**, que suele ser menor, así que el mismo cambio da un *d* más grande. **[Aproximación]**: útil como escala dentro de la app, no para comparar con la literatura.

---

## 8.6 La adherencia: lo asignado y lo hecho

El resultado compara **lo asignado** por el plan: si en una semana «SÍ» te dormiste tarde tres noches, esos días cuentan igual como «con la acción». Es el análisis por **intención de tratar** de los ensayos clínicos, legítimo (estima el efecto de *intentar* la acción) **[Validado]**, pero diluye el efecto si no la cumples.

«Terminar» guarda el día lógico en `Experimento.fin`, y el resultado deja de contar los días posteriores (H-7-2). Con las respuestas de «¿Lo hiciste hoy?», `Experimentos.resultado` da el **cumplimiento** sin contar el lavado: la tarjeta muestra el porcentaje de días en que lo hiciste en las semanas «SÍ» y en las «no» (H-7-8). Si el cumplimiento difiere al menos 20 puntos entre condiciones, da como dato secundario el **cociente** (`Resultado.cociente`): la diferencia según el plan entre la diferencia de cumplimiento. *Ejemplo:* diferencia +0.30; lo hiciste el 70 % de los días «SÍ» y el 10 % de los «no»; cociente = 0.30 ÷ 0.60 = **+0.50**. Estima el efecto de **hacerlo de verdad**, suponiendo que saber qué semana tocaba no cambia nada por sí solo, lo que es dudoso sin cegamiento (sección 8.9). **[Aproximación]**.

No se calcula el análisis «por lo hecho» (días hechos contra no hechos): si cumples más en los días buenos, exagera el efecto (en una simulación del módulo 9 lo **duplicaba**).

---

## 8.7 La prueba de tendencia (`Experimentos.tendencia`)

Una **tendencia** es que la medida suba o baje sola con el tiempo, por algo ajeno al experimento (la estación, avanzar en el gimnasio). En un **antes/después**, el periodo «después» se verá mejor si la medida sube sola, aunque la acción no haga nada: es la principal debilidad de esos diseños, y la razón de alternar y sortear (Kratochwill y Levin, 2010).

Con al menos 8 días que cuentan y tienen dato (sin hoy), la app ajusta por mínimos cuadrados, **a la vez**, la tendencia y la condición:

$$y = a + b\,i + c\,\text{con} + \varepsilon$$

con *i* = número de día y «con» = 1 los días con la acción. *b* es la **pendiente dentro de cada condición**: cuánto cambia la medida por día una vez descontado el efecto de la condición. El aviso («Ojo: dentro de cada condición, tu medida venía cambiando sola…») salta si |*b*/EE(*b*)| supera el cuantil 0.975 de la *t* con *n* − 3 grados de libertad.

**Por qué así** (H-7-6). Hasta la 10c, la recta se ajustaba con las dos condiciones juntas y confundía un **efecto real** con una tendencia: con *d* = 0.8 y sin tendencia, el aviso aparecía en el **71 %** de los antes/después. Ahora, en esa situación, sale el **5 %** de las veces en los antes/después y el **6 %** en los alternos: lo esperado por azar. **[Validado]** como método (regresión con tendencia y condición).

**Lo que no resuelve.** Con una tendencia de 0.02 desviaciones por día y **sin efecto real**, el antes/después da una falsa señal el **39 %** de las veces y las semanas alternas por pares, el **2 %**. En ese antes/después la prueba avisa solo el **22 %** de las veces, porque dentro de cada mitad hay poca información para estimar la pendiente; y el efecto que se muestra no descuenta la tendencia (detalle y figura en la versión completa, §8.8).

---

## 8.8 Las plantillas (`Experimentos.PLANTILLAS`)

| Plantilla | Acción | Medida | Diseño | Semanas | Lavado |
|---|---|---|---|---|---|
| Creatina | 5 g al día | `volumen_kg` | antes/después | 12 | 28 |
| Hora de dormir | en la cama antes de las 00:00 | `valencia` | alterno | 6 | 1 |
| Pantalla antes de dormir | sin pantalla la última hora | `sueno_calidad` | alterno | 6 | 1 |
| Cafeína en la tarde | nada después de las 14:00 | `sueno_horas` | alterno | 6 | 2 |

**Por qué la creatina va en antes/después.** La creatina se acumula en el músculo: Hultman et al. (1996) midieron que la creatina total del músculo sube alrededor de un 20 % tras 6 días a 20 g diarios, se mantiene con 2 g diarios durante 30 días más y, 30 días después de dejarla, ya no difería del valor inicial. Si la reserva tarda semanas en llenarse y en vaciarse, alternar semanas solo compararía «medio lleno» con «medio lleno». El razonamiento es correcto. Aun así, **antes/después + volumen de entrenamiento** es la peor combinación para la tendencia: si estás progresando en el gimnasio, el volumen sube solo. Y `volumen_kg` depende de **qué sesión** hiciste, no solo de tu fuerza (H-7-14).

Las otras tres son razonables: efectos que aparecen y se van en uno o dos días, medidas diarias y lavado corto. **[Aproximación]**: los lavados no vienen de un estudio específico.

---

## 8.9 Cegamiento y efecto expectativa

**Cegamiento** es que quien recibe la acción no sepa si la está recibiendo (como con una pastilla de azúcar). Aquí es imposible: **sabes** si te acostaste antes o si tomaste café. Importa sobre todo con medidas **subjetivas**: si crees que dormir antes te hará sentir mejor, es probable que califiques tus días un poco mejor en las semanas «SÍ», aunque el sueño no haya cambiado. Es el **efecto expectativa** o placebo; la extensión CENT pide reportar si hubo cegamiento justamente por esto (Vohra et al., 2015).

Ayuda preferir **medidas objetivas** (`sueno_horas` del reloj en vez de `sueno_calidad`), mirar el resultado **solo al final** y, para la valencia, preferir `muestreo_valencia` (avisos al azar en el momento, capítulo 3); no pude comprobar cuánto reduce el sesgo de expectativa **[sin verificar]**. **[Aproximación]**: sin cegamiento, el resultado responde a «¿hacer esto **sabiendo que lo hago** me cambia?», una pregunta legítima para uso personal que no aísla el efecto fisiológico.

---

## 8.10 «Antes y después» en Explorar: un estudio observacional

`EtapasDeVida` (`ui/pantallas/Explorar.kt`) toma los **8 días más recientes** en que guardaste cambios en «Conocerte» (mudanza, pareja, trabajo…) y compara siete variables (valencia, horas de sueño, minutos de pantalla, pasos, gasto, personas mencionadas y palabras escritas) entre los días −1 a −60 y 0 a 59 respecto al cambio, con la misma `Estadistica.diferenciaEnDias`.

Nadie sorteó cuándo mudarte, y esos cambios vienen con muchos otros. Es un **estudio observacional** del tipo de *serie de tiempo interrumpida* (comparar una serie antes y después de un evento). **[Aproximación]**: útil para generar ideas, no para concluir causas. Fallos (H-7-12): hasta **56 comparaciones** (8 × 7) sin corregir, así que aunque nada cambiara cabe esperar unas **3** «claras» por azar; tendencias; la fecha es la del registro, no la del cambio real; y `personas` y `palabras` miden **cuánto escribes**, no tu vida social.

---

## 8.11 Lo que el resultado puede y no puede decir

**Puede decir**, si el experimento fue alterno, sorteado, con lavado razonable, una medida objetiva, suficientes semanas y miraste solo al final: «en mi caso, en este periodo, hacer esto se asoció con este cambio, y es poco probable que sea solo por cómo cayeron las semanas». **No puede decir** que el efecto sea igual en otra época de tu vida ni que sea fisiológico y no expectativa, y no dice nada si el diseño fue antes/después con una medida que tiende a subir o bajar sola.

---

## 8.12 En resumen

1. Los experimentos siguen la lógica de los **ensayos N-de-1**: semanas con y sin la acción, **sorteadas por pares**, que empiezan en lunes, con la hipótesis **sellada**.
2. No se deja sellar si alguna condición podría quedar con menos de 7 días; antes, la plantilla «Creatina» nunca podía dar resultado (H-7-1).
3. El **intervalo** usa la *t* con grados de Welch y el error estándar corregido por autocorrelación: 3–4 % de falsas señales en la simulación, frente al 14–24 % del método anterior, a costa de poder. Mientras dura el plan, es **provisional**.
4. La **prueba de aleatorización** sería lo más adecuado, pero no existe (H-7-11).
5. La **prueba de tendencia** separa tendencia y condición, pero un antes/después con tendencia real sigue dando falsas señales el 39 % de las veces.
6. El análisis es **según el plan**; «¿Lo hiciste hoy?» da el cumplimiento. Sin cegamiento, conviene usar medidas objetivas y mirar solo al final.
7. «Antes y después» en Explorar es **observacional**, con hasta 56 comparaciones sin corregir (H-7-12).
8. Siguen abiertos H-7-7 (Hedges, documentado), H-7-10 (el sello no fija el análisis), H-7-11 y H-7-14; H-7-6 y H-7-12 se resolvieron solo en parte.
