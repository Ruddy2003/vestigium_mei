# Capítulo 8. Experimentos personales (N-de-1)

> **Para qué sirve este capítulo.** Explica cómo la app te ayuda a **probar algo en ti mismo** (dormirte antes, dejar la cafeína en la tarde, tomar creatina) y a decidir si funcionó. Cubre la lógica de estos diseños y su respaldo en la literatura, qué hace exactamente el código (sorteo de semanas, días de lavado, diferencia de medias, tamaño del efecto, prueba de tendencia) y qué tan confiable es el resultado. También trata la sección «Antes y después» de Explorar, que **parece** un experimento pero no lo es. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f, commit `2b262c5`; `SCHEMA_V = 15`), la versión que describe todo el paper. Lo que cambió en el módulo 10 se marca «desde el esquema 13» (entrega 10b: fecha de fin y «¿Lo hiciste hoy?») o «desde la 10c» (commit `b943ec7`: intervalo con *t* y autocorrelación, sorteo por pares, lavado que no se come una condición, tendencia dentro de cada condición y resultado provisional mientras dura el plan), y está en el apéndice D (D.13, D.17, D.65, D.67 y D.68). La entrega 10e (v91; `SCHEMA_V` sigue en 14) cambió la d de Cohen por la g de Hedges, completa el cumplimiento de «Creatina» con el registro de suplementos (D.96) y agregó `vm_experimento()` en R, con la prueba de aleatorización (D.97); se marca «desde 10e». La entrega 10f (v92, esquema 15) sella también el criterio de decisión, con una huella de lo sellado (D.113), y hace que «Creatina» mida la fuerza en un ejercicio fijo (D.114); se marca «desde 10f». Cada sección dice qué era así antes y qué es así ahora. **Los ejemplos y las simulaciones usan datos inventados.**
>
> **Cómo leerlo.** Igual que los capítulos anteriores: lenguaje sencillo, cifras y condiciones del código tal cual, con el nombre de la función. Cada decisión se marca como **[Validado]**, **[Aproximación]** o **[Diseño propio]**. Los problemas se numeran H-7-n (el número es el del módulo 7, que escribió este capítulo) y se detallan en `hallazgos.md`. Este capítulo usa **simulaciones**: generar miles de experimentos inventados en los que *sabemos* la verdad (por ejemplo, que la acción no hace nada) y contar cuántas veces el método de la app se equivoca. Los scripts están en `figuras/`.

---

## 8.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Experimento N-de-1** | Un experimento con **una sola persona**, que se compara consigo misma en distintos periodos: unos con la acción y otros sin ella. «N» es el número de participantes; aquí, 1. |
| **Condición** | Cada una de las dos situaciones que se comparan: **con** la acción («SÍ») o **sin** ella («no»). |
| **Diseño ABAB (alterno)** | Alternar periodos sin la acción (A) y con ella (B) varias veces. En la app: semanas alternas, en un orden sorteado. |
| **Diseño antes/después (AB)** | Un solo periodo sin la acción y luego uno con ella. |
| **Aleatorizar (sortear)** | Decidir al azar qué periodos llevan la acción. Evita que tú elijas, sin querer, hacerla justo cuando ya ibas a estar mejor. |
| **Lavado** (*washout*) | Días que **no cuentan** después de cada cambio de condición, para que el efecto de la condición anterior se vaya (y el de la nueva aparezca). |
| **Arrastre** (*carryover*) | Que el efecto de una condición siga actuando cuando ya empezó la otra. El lavado existe para evitarlo. |
| **Prerregistro** | Escribir la pregunta, la hipótesis y cómo vas a analizar **antes** de ver los datos, para no acomodar la conclusión a lo que salga (Nosek et al., 2018). |
| **Huella** (*hash*) | Un código corto que se calcula a partir de un texto: si cambia una sola letra del texto, la huella sale completamente distinta. La app usa SHA-256, que da 64 caracteres (la tarjeta muestra los primeros 8). Sirve para notar que algo cambió, no para saber qué cambió. |
| **Intervalo de confianza del 95 %** | Un rango de valores plausibles para el efecto. Si se repitiera el experimento muchas veces con el mismo método, el 95 % de esos rangos contendría el efecto real… **si los supuestos del método se cumplen** (sección 8.6). |
| **Falsa señal** (falso positivo) | Que el método diga «hay un efecto» cuando en realidad no lo hay. Un intervalo del 95 % bien hecho lo hace el **5 %** de las veces. |
| **Autocorrelación** | Que un día se parezca al anterior (si hoy estás bien, mañana probablemente también). La autocorrelación de orden 1, *ρ*, va de −1 a 1; 0 significa días independientes. |
| **d de Cohen** y **g de Hedges** | La diferencia entre las dos condiciones medida en «desviaciones estándar»: cuántas veces el vaivén normal de un día a otro (Cohen, 1988). La *g* de Hedges es la misma cifra con una pequeña corrección que quita lo que se infla con pocos días (Hedges, 1981); la app la usa desde 10e. |
| **Tendencia** | Que la medida suba o baje sola con el tiempo, por algo ajeno al experimento (la estación del año, avanzar en el gimnasio, una racha). |
| **Cegamiento** | Que quien recibe la acción no sepa si la está recibiendo (como con una pastilla de azúcar). En estos experimentos casi nunca es posible. |

---

## 8.2 Por qué experimentar en uno mismo

### 8.2.1 El problema que resuelven

El resto de la app encuentra **relaciones**: «los días que duermes más, estás de mejor ánimo». Pero una relación no dice qué causa qué: quizá dormir más mejora el ánimo, quizá el buen ánimo te deja dormir, quizá los fines de semana causan ambas cosas. La propia app lo recuerda en «Cómo leer tus datos»: «Para causas, usa los experimentos personales» (`PantallaGuia`, `ui/pantallas/Mas.kt`), y con otras palabras en la guía de uso (`data/GuiaUso.kt`).

Un **experimento** rompe ese nudo: **tú decides** cuándo hacer la acción, y si además lo decide **el azar**, lo demás (el día de la semana, tu humor previo, el clima) queda repartido entre las dos condiciones. Si después hay diferencia, lo más probable es que la cause la acción.

### 8.2.2 La literatura

Los **ensayos N-de-1** son ensayos aleatorizados en una sola persona. Se propusieron en medicina para elegir el tratamiento de un paciente concreto: Guyatt et al. (1986) describieron cómo alternar al azar periodos con y sin un medicamento en un paciente y decidir según su propia respuesta. La guía de la AHRQ (Kravitz et al., 2014) explica cómo diseñarlos y la extensión CONSORT para N-de-1 (CENT 2015; Vohra et al., 2015) dice qué hay que reportar. En psicología y educación, la misma idea existe como **diseños de caso único** (*single-case designs*), y se ha insistido en **añadir aleatorización** para que sus conclusiones sean más creíbles (Kratochwill y Levin, 2010; Tanious y Onghena, 2019). **[Validado]**: la lógica de alternar condiciones al azar en una misma persona está bien establecida.

**Cuándo sirven** (condiciones habituales en la literatura, resumidas):

1. La acción tiene un efecto **rápido** que también **se va rápido** (si tarda meses en aparecer o no se va, alternar no sirve).
2. La medida se puede tomar **muchas veces** (diaria, idealmente).
3. El problema es **estable**: no está empeorando o mejorando solo durante el experimento.
4. Se puede **repetir** la alternancia varias veces.

Dormirte antes, quitar la pantalla de noche o la cafeína de la tarde cumplen bastante bien esas condiciones. La creatina no (sección 8.9).

---

## 8.3 Cómo se crea un experimento

Está en `PantallaExperimentos` (`ui/pantallas/Lote3.kt`) y el modelo `Experimento` (`data/Modelo.kt`).

### 8.3.1 Lo que escribes

- **Nombre**, **pregunta** (opcional), **acción** («qué vas a cambiar») e **hipótesis** («qué crees que pasará»). Sin nombre, acción e hipótesis, el botón no se activa.
- **Qué vas a medir**: una sola variable del panel, elegida de una lista corta: valencia del día, activación, calidad de sueño, horas de sueño, valencia de los avisos, minutos de pantalla, tiempo de reacción, volumen en el gimnasio, desde 10f la fuerza de referencia (`fuerza_ref_kg`, sección 8.9) o minutos de enfoque. Las cuatro plantillas usan medidas de esa lista. Desde la entrega 10a, el impulso ya no es una medida posible (es privado), y un experimento viejo que lo medía no se muestra en esta pantalla.
- **Qué contaría como efecto (se sella)**, desde 10f (D.113; H-7-10). Opcional: dos fichas, «debería subir» o «debería bajar» (`Experimento.sentido`: 1 o −1; tocar otra vez la elegida la quita), y, si elegiste una, un campo para la **diferencia mínima que te importaría**, en las unidades de la medida (`Experimento.umbral`; se guarda su valor absoluto, y vacío si no pones nada). La pantalla lo explica: «Al terminar, contará como efecto si la diferencia va hacia arriba, mide al menos 0.3 y su intervalo de 95 % no toca el cero». Sin sentido elegido no se guarda umbral.
- **Diseño**: «Semanas alternas sorteadas» (`alterno`) o «Antes y después» (`antes_despues`).
- **Duración**: 4, 6, 8 o 12 semanas.
- **Días de lavado tras cada cambio**: ninguno, 1, 2, 7 o 28.
- **Desde la entrega 10c**, si con ese diseño, esas semanas y ese lavado **podría** quedar alguna condición con menos de **7 días que cuentan** (`Experimentos.MIN_DIAS`), la pantalla lo avisa («Con *L* días de lavado tras cada cambio, en *N* semanas podrían quedar solo *k* días que cuentan en una condición…») y **no deja sellar**. `Experimentos.minimoPosible` lo calcula probando **todos** los planes que el sorteo podría dar (sección 8.4) y tomando el peor; así no se puede «sortear otra vez hasta que salga bien». Ejemplos: alterno de 6 semanas con 1 día de lavado: lo peor son 18 días por condición, se deja sellar; con 7 días de lavado, lo peor son 0, no se deja.

### 8.3.2 El sellado

Al tocar **«Sellar y empezar el lunes»**:

1. Se guarda la hora exacta (`sellada = Fechas.ahoraIso()`, con su desfase horario).
2. Se **sortea el plan** (sección 8.4).
3. El experimento **empieza el lunes siguiente**: `inicio = lunesDe(hoy) + 7 días`. Si hoy es lunes, empieza dentro de 7 días.
4. **Desde 10f (D.113), se le pone la huella** (`Experimentos.sellar`). `Experimentos.textoSellado` junta, en este orden fijo y separados por un carácter que no se escribe a mano (U+001F), la pregunta, la acción, la medida, la hipótesis, el umbral, el sentido, el diseño, el inicio, las semanas, el plan (como «100110»), los días de lavado y la hora del sello; `Experimentos.huella` es el SHA-256 de ese texto (en UTF-8), escrito en 64 caracteres hexadecimales, y se guarda en `Experimento.huella`. No entran el nombre, la plantilla, la fecha de fin ni las respuestas de «¿Lo hiciste hoy?», que cambian legítimamente después.

Después, la tarjeta muestra la hipótesis con «sellada el … a las …» (desde 10f, la hora es la local del lugar donde sellaste, con el desfase guardado en `sellada`: `Fechas.horaDe`, D.109; antes se pasaba a la zona del teléfono de hoy) y, desde 10f, el criterio («Criterio sellado: un cambio hacia arriba de al menos 0.3, con un intervalo de 95 % que no toque el cero») y la huella. Cada vez que se dibuja, `Experimentos.intacto` vuelve a calcular la huella con lo que hay guardado y la compara: si coincide, «Huella del sello: 3f9a21c0 (coincide: nada de lo sellado cambió)»; si no, un aviso: «Lo sellado cambió después del sello (la huella … ya no coincide): la pregunta, la hipótesis, el criterio o el plan no son los del principio». Un experimento sellado antes del esquema 15 no tiene huella (`intacto` = null) y no muestra ninguna de las dos líneas. La pantalla no permite editar la pregunta, la hipótesis, la variable, el criterio ni el plan; solo «Terminar experimento». Tampoco hay botón para borrar un experimento.

**Razonamiento.** Es un **prerregistro** casero: escribir qué esperas antes de ver los datos. Sin él, es fácil reinterpretar después («en realidad lo que quería ver era la energía, no el ánimo») y encontrar algo por casualidad (Simmons et al., 2011; Nosek et al., 2018). Que no se puedan borrar los experimentos también ayuda: los que no salieron quedan a la vista, en lugar de olvidarse. **[Diseño propio]** inspirado en una práctica **[Validado]**.

**Cómo era hasta 10f** (H-7-10). La hora quedaba guardada, pero no había huella del texto sellado, y se fijaba la variable pero **no el análisis**: no se sellaba qué ibas a considerar «funcionó», así que, viendo los datos, podías decidir después que un cambio pequeño «ya contaba» o que lo importante era que bajara y no que subiera. Hasta el esquema 12, además, restaurar un respaldo reemplazaba el experimento entero; desde el 13 (`Fusion.experimento`), un experimento que ya está en el teléfono conserva su texto y su plan, y del respaldo solo se toman la fecha de fin y las respuestas.

**Desde 10f (D.113): el criterio también se sella, con una huella.**

- **El criterio** (`Experimentos.criterio(e, r)`). Cuenta como efecto si se cumplen dos cosas: la diferencia, contada en el sentido que sellaste, mide al menos el umbral (*d* × sentido ≥ umbral; sin umbral, 0, y basta con que vaya en ese sentido), y el intervalo de 95 % no toca el cero **del lado esperado** (si esperabas que subiera, su límite inferior es mayor que 0; si que bajara, el superior es menor que 0). Da «se cumple» o «no se cumple» solo cuando ya hay resultado (3 días por condición) y el plan **terminó** (no es provisional: venció el plan o tocaste «Terminar»). A medio camino no decide, igual que la frase de «señal» (sección 8.6.1). Si no sellaste un sentido, no hay criterio y la tarjeta queda como antes. **[Diseño propio]**: es la regla de decisión habitual de una prueba de una cola (el sentido) con una diferencia mínima importante (el umbral), pero la combinación concreta es de la app.
- **Lo que no mira el criterio.** El umbral se compara con la **diferencia estimada**, no con el límite del intervalo: con umbral 0.3, una diferencia de +0.5 con intervalo de +0.1 a +0.9 «se cumple», aunque el intervalo admite efectos menores que 0.3. Una regla más exigente pediría que todo el intervalo quedara por encima del umbral; con las semanas que permite la app casi nunca se cumpliría (sección 8.6.3).
- **La huella** detecta cualquier cambio en lo sellado después del sello: una edición o un respaldo restaurado que lo reescribió. SHA-256 es una función hash criptográfica estándar: no se conoce manera práctica de cambiar el texto y conservar la huella **[Validado]** (como propiedad de SHA-256, no de la app).

*Ejemplo (inventado), con el experimento de la sección 8.6.2.* Al sellar elegiste «debería subir» y pusiste 0.3. Al terminar: diferencia **+0.58**, intervalo **−0.46 a +1.62**. +0.58 ≥ 0.3, pero el intervalo toca el cero: **no se cumple**. Si con 12 semanas la diferencia fuera la misma y el intervalo de +0.12 a +1.04: **se cumple**. Si la diferencia fuera +0.20 con intervalo de +0.02 a +0.38: el intervalo no toca el cero, pero 0.20 < 0.3: **no se cumple** (es «una señal» en la frase de la tarjeta, pero no el efecto que dijiste que te importaría).

**Límites que quedan:**

- **El criterio es opcional.** Si no eliges un sentido al sellar, todo sigue como antes de 10f: nada fija qué contará como «funcionó» (el estado de H-7-10 lo dice).
- **La huella avisa, no impide.** Está guardada junto a lo que protege y no lleva una llave secreta: quien edite un respaldo a propósito puede volver a calcularla con la misma receta, y la tarjeta dirá «coincide». Detecta un cambio accidental o hecho sin recalcularla, no uno deliberado. La hora del sello sale del reloj del teléfono; no hay un sello de tiempo externo. Sigue siendo un sello de **honor**, ahora con una alarma; para un solo usuario es razonable.
- **Un respaldo que trae un experimento que no está en el teléfono** lo trae tal cual, con su huella (si la trae). `intacto` dice si coincide; un experimento de antes del esquema 15 no tiene huella y no se puede comprobar.
- **El resultado se ve desde el principio** (como provisional, sección 8.6.3): sellar el criterio protege la decisión, pero no impide que lo que ves a medio camino influya en cómo vives las semanas que faltan.

### 8.3.3 El aviso diario

En la pantalla del día (`ui/pantallas/Dia.kt`), cada experimento muestra: «Experimento «…», semana *k* de *N*: esta semana SÍ: … / esta semana NO hagas: …» (`AvisoExperimento`). Hasta el esquema 12 solo salía en los experimentos activos, y la semana se calculaba con `ChronoUnit.WEEKS.between(inicio, fecha)`.

**Detalle (hasta el esquema 12).** Esa función **trunca hacia cero**: para los 6 días anteriores al inicio da 0, no −1. Por eso, desde el martes de la semana previa, el día ya mostraba «semana 1 de *N*» con la instrucción de la primera semana. Si la primera semana era «SÍ», podías empezar antes de tiempo. No afectaba al cálculo del resultado, que solo cuenta desde `inicio`, pero sí podía contaminar la línea de base (H-7-9). Resuelto: el esquema 13 dejó de mostrar el aviso fuera de los días del experimento, y desde la entrega 10c `AvisoExperimento` toma la condición de `Experimentos.toca`, que no devuelve nada antes del inicio.

**Desde el esquema 13 (entrega 10b):**
- **Cuándo aparece el aviso.** Sale en los días que cubre el experimento (`Experimentos.cubre`: del inicio a su último día) y nunca en días futuros. Ya no depende de que esté activo: así puedes contestar los días que quedaron sin respuesta aunque ya lo hayas terminado. La semana se calcula como días desde el inicio entre 7, y ya no aparece antes de empezar.
- **Días de lavado.** El aviso dice cuándo un día es de lavado y no cuenta.
- **«¿Lo hiciste hoy?»** El aviso lleva la pregunta (I-3; en un día pasado, «¿Lo hiciste ese día?») con dos fichas, «sí» y «no». Se pregunta **en las dos condiciones**: en una semana «no», responder «sí» quiere decir que lo hiciste aunque no tocaba. La respuesta se guarda en `Experimento.hecho` (fecha → sí/no). Tocar otra vez la ficha elegida la borra, y el día queda **sin respuesta**, que no es lo mismo que «no».
- **Por qué en el experimento.** Se guarda en el experimento y no en el día para no crear días vacíos ni tocar `retroactivo`.
- **Lo que no se pudo medir.** Preguntar cada día puede cambiar la conducta (reactividad); no se pudo estimar cuánto.

---

## 8.4 El sorteo del plan

### 8.4.1 Qué hace exactamente (desde la entrega 10c: `Experimentos.plan`)

Al sellar:

- **Alterno**: se sortea **por pares de semanas**. Las semanas se agrupan de dos en dos (1–2, 3–4…) y en cada par se sortea, con la misma probabilidad, si va «SÍ, no» o «no, SÍ». Con un número impar de semanas, la última se sortea sola. El generador se inicia con la hora en milisegundos.
- **Antes y después**: primero ⌊*N*/2⌋ semanas «no» y luego el resto «SÍ». **No se sortea nada.**

El plan se guarda en el experimento (`plan: List<Boolean>`) y se muestra completo en la tarjeta («plan: SÍ no no SÍ SÍ no»). Los experimentos sellados antes de la 10c **conservan su plan**.

**Hasta la 10c el sorteo era libre**: una lista con ⌊*N*/2⌋ semanas «SÍ» y el resto «no», **barajada** con `shuffled(Random(System.currentTimeMillis()))`. Cada plan distinto era igual de probable: $\binom{N}{N/2}$ planes posibles, 6 con 4 semanas, 20 con 6, 70 con 8 y 924 con 12. Ahora hay $2^{N/2}$: 4 con 4 semanas, 8 con 6, 16 con 8 y 64 con 12. **[Validado]** como aleatorización en los dos casos.

**Un acierto del diseño**: como todo empieza en lunes y cada condición dura semanas completas, cada condición tiene la misma cantidad de lunes, martes… Así, el efecto del día de la semana (los fines de semana suelen ser distintos) no se cuela en la comparación.

### 8.4.2 Por qué por pares (pista 3 del plan; H-7-5, resuelto en 10c)

Barajar sin restricciones podía dar «SÍ SÍ SÍ no no no», que en la práctica es un diseño de antes y después y queda expuesto a las tendencias (sección 8.8). La probabilidad de un plan así (todo «SÍ» seguido de todo «no», o al revés) era 2 entre el número de planes:

| Semanas | Planes posibles (sorteo libre) | Probabilidad de un bloque puro |
|---|---|---|
| 4 | 6 | 33 % |
| 6 | 20 | 10 % |
| 8 | 70 | 2.9 % |
| 12 | 924 | 0.2 % |

Con 4 semanas, uno de cada tres experimentos «alternos» era en realidad un antes/después.

**Sortear por pares** lo evita: nunca hay más de dos semanas seguidas iguales, y una tendencia lenta afecta casi por igual a ambas condiciones. En las simulaciones de la Figura 8.3 (con el método de antes de la 10c), con una tendencia de 0.02 desviaciones por día y sin efecto real, el sorteo libre de 8 semanas daba una falsa señal el **14 %** de las veces y el sorteo por pares, alrededor del **5 %** (el script de la figura no trae este último caso; se repitió con sus mismas funciones en el módulo 14).

**El costo**: hay menos planes posibles. Esto importa para la **prueba de aleatorización** (sección 8.6.4; en R desde 10e), cuyo valor *p* más pequeño posible es 1 entre el número de planes: con pares, 1/8 = 0.125 con 6 semanas y 1/16 = 0.0625 con 8, las dos por encima de 0.05. De las duraciones que ofrece la app (4, 6, 8 o 12 semanas), solo con 12 (1/64 ≈ 0.016) podría bajar de 0.05 (en la práctica el mínimo es el doble, sección 8.6.4).

**Con pares ya no puede salir un plan en bloque** (siempre hay al menos dos cambios de condición con 4 semanas o más), pero los experimentos sellados antes con sorteo libre conservan su plan. Desde la 10c, la tarjeta decide si el plan **real** quedó en bloque (`Experimentos.enBloque`: diseño antes/después, o un solo cambio de condición en todo el plan) y, si es un alterno que quedó así, lo dice: «El sorteo quedó en bloque… Se lee como un antes y después» (si además salta el aviso de tendencia, lo dice dentro de ese aviso, sección 8.8.1). Antes, el aviso fuerte dependía solo de que el diseño elegido fuera «antes y después» (H-9-30).

---

## 8.5 Qué días cuentan: el lavado

### 8.5.1 Las reglas (`Experimentos.toca` y `Experimentos.enLavado`, `data/Lote3.kt`)

- **Qué condición tiene un día**: con *d* = días desde `inicio`, si *d* < 0 el día no está en el experimento; si no, la semana es ⌊*d*/7⌋ y la condición es `plan[semana]`. Pasado el plan, el día tampoco está.
- **Qué días son de lavado**: se buscan los **días de cambio**: el día 0 **si** el plan empieza con «SÍ», y el primer día de cada semana cuyo valor es distinto del de la semana anterior (7·*k*). Un día es de lavado si cae en los primeros `lavado_dias` días tras cualquiera de esos cambios: $0 \le d - c < L$.

Observa que el lavado se aplica **en los dos sentidos**: al pasar de «SÍ» a «no» (para que se vaya el efecto) y de «no» a «SÍ» (para que el efecto tenga tiempo de aparecer). Si el plan empieza con «no», el día 0 no es de lavado.

### 8.5.2 Ejemplo (Figura 8.1, izquierda)

Plan sorteado: SÍ no no SÍ SÍ no, 2 días de lavado. Cambios: día 0 (empieza con SÍ), día 7 (SÍ→no), día 21 (no→SÍ), día 35 (SÍ→no). Días de lavado: 0–1, 7–8, 21–22, 35–36 = 8 días. Cuentan **17 días con la acción** (21 − 4) y **17 sin ella** (21 − 4). La semana 3 («no» tras «no») no pierde ningún día.

![Figura 8.1](figuras/fig20_calendario_experimento.png)

*Figura 8.1. Izquierda: qué días cuentan con un plan alterno y 2 días de lavado. Derecha: la plantilla «Creatina» desde la entrega 10c (12 semanas, antes y después, 28 días de lavado): quedan 14 días con la acción y 42 sin ella. Con las 8 semanas que tenía la plantilla hasta la 10c (4 «no» y 4 «SÍ»), todo el periodo con la acción quedaba en lavado (sección 8.5.3). Script: `figuras/fig20_calendario_experimento.py`.*

### 8.5.3 Razonamiento y cuándo falla

Excluir los primeros días de cada periodo es una forma estándar de lidiar con el arrastre cuando no se puede (o no se quiere) dejar un periodo sin nada entre condiciones; hasta donde sé, las guías de N-de-1 lo llaman *lavado analítico*, pero no lo verifiqué en el texto de la guía **[sin verificar]**. **[Aproximación]** razonable: la duración correcta depende de cada acción, y la app la deja a tu criterio (o a la plantilla).

**El problema grave (hasta la entrega 10c): el lavado podía comerse una condición entera** (H-7-1, resuelto en 10c).

- Con semanas alternas y **7 días de lavado**, toda semana que empieza con un cambio se pierde completa. Con un plan que alterna cada semana (SÍ no SÍ no SÍ no), **no queda ningún día**; con «no SÍ no SÍ no SÍ», solo quedan los 7 días «no» de la primera semana. El experimento nunca daba resultado.
- Con **28 días** y semanas alternas, todo queda en lavado.
- **La plantilla «Creatina»** (8 semanas, antes y después, 28 días de lavado) sufría exactamente esto: el plan era 4 semanas «no» + 4 «SÍ»; el único cambio, el día 28, y los días 28 a 55 de lavado. Contaban **28 días sin la acción y 0 con ella**. **La plantilla no podía dar resultado nunca.**

**Desde la 10c:** no se deja sellar si en el peor sorteo posible alguna condición queda con menos de 7 días que cuentan (sección 8.3.1), y la plantilla «Creatina» dura **12 semanas**: 6 «no» y 6 «SÍ», cambio el día 42, lavado de los días 42 a 69; cuentan **42 días sin la acción y 14 con ella** (Figura 8.1, derecha). El mínimo de 7 días es **[Diseño propio]**: una semana completa, para que cada condición tenga al menos un día de cada día de la semana.

---

## 8.6 El resultado: diferencia, intervalo y tamaño del efecto

### 8.6.1 Qué calcula (`Experimentos.resultado` y `Estadistica.diferenciaEnDias`)

Recorre los días desde `inicio` hasta el último día del plan, hasta hoy o, desde el esquema 13, hasta el día en que lo terminaste (`Experimento.fin`), lo que llegue antes. Para cada día que está en el experimento, no es de lavado y **tiene dato** en la variable elegida, guarda el valor (con su fecha) en la lista «con» o «sin». **Desde la entrega 10c, hoy no entra** (H-9-31): el día aún no termina y las variables que se acumulan (pantalla, pasos, enfoque) estarían a medias; las respuestas de «¿Lo hiciste hoy?» sí cuentan para el cumplimiento. El panel es el mismo de los análisis (`Estadistica.panel(dinamicas = false)`) y, desde la 10c, se arma en segundo plano, como en Análisis, para que las variables que dependen de MacroFactor valgan lo mismo aquí que en las demás pantallas (H-7-13, capítulo 5).

1. **Diferencia de medias con su intervalo** (`Estadistica.diferenciaEnDias(con, sin)`), solo si hay **al menos 3 días en cada condición**. Desde la 10c (D.65):

   - **La autocorrelación de cada condición**, *ρ* (`Estadistica.ar1Grupos`): la correlación entre pares de días **seguidos** de esa condición, si hay al menos 10 pares; si no, la de las dos condiciones juntas (cada una centrada en su media); si tampoco, 0 (aquí no se usa el 0.3 que, desde 10e, suponen las relaciones cuando no se puede estimar: D.95 no cambia los experimentos). Como en series cortas la autocorrelación estimada sale más baja que la real, se corrige: *ρ* + (1 + 4*ρ*)/*n*, con *n* los días de esa condición (o de las dos, si se usó la de las dos juntas) (la aproximación de primer orden atribuida a Marriott y Pope, 1954; la forma exacta no se leyó en el artículo **[sin verificar]**). Luego se acota entre 0 y 0.9: solo se corrige hacia un intervalo más ancho.
   - **El error estándar de cada condición** se multiplica por √((1 + *ρ*)/(1 − *ρ*)), y su número efectivo de días es *n*(1 − *ρ*)/(1 + *ρ*), nunca menos de 2 (`Estadistica.diferenciaCompleta`):

$$EE = \sqrt{\frac{s_c^2}{n_c}\cdot\frac{1+\rho_c}{1-\rho_c} + \frac{s_s^2}{n_s}\cdot\frac{1+\rho_s}{1-\rho_s}}$$

   - **Los grados de libertad** son los de Welch con esos *n* efectivos (acotados entre 1 y la suma de los dos *n* efectivos menos 2), y el intervalo usa el **cuantil de la *t* de Student** (`Estadistica.cuantilT(0.975, gl)`) en lugar de 1.96:

$$\bar{x}_c - \bar{x}_s \;\pm\; t_{0.975,\,gl}\cdot EE$$

   donde $\bar{x}$ son las medias, $s^2$ las varianzas muestrales (dividiendo entre *n* − 1) y *n* el número de días de cada condición. **Hasta la 10c** era el error estándar de Welch sin corregir y con el valor **1.96 fijo** de la distribución normal.

2. **Mensaje**:
   - **Mientras dura el plan** (`Experimentos.enCurso`), la tarjeta dice «Provisional: el plan sigue en curso. Mirar el resultado a medio camino y parar en la primera «señal» engaña; espera a que termine», y **no** da la frase de «señal» (H-7-4).
   - **Al terminar**: si el intervalo no toca el cero, «es una señal para ti»; si lo toca, «todavía no se distingue del azar».
   - **Desde 10f, el criterio sellado** (si lo hay; sección 8.3.2): «Según el criterio que sellaste: se cumple» o «no se cumple» (`Experimentos.criterio`). Mientras el plan sigue en curso no sale.
   - **El rótulo** dice «Semanas con la acción según el plan: … · sin ella: …», para que no parezca que se midió lo que hiciste (H-7-8).
   - **Si no hay 3 días de cada condición**, desde la 10c distingue tres casos (H-9-32): «Este plan nunca dará resultado» (con el lavado, en todo el plan quedan menos de 3 días de alguna condición: `Resultado.posibleSi`, `posibleNo`); «El experimento terminó sin suficientes días medidos»; y, si sigue en curso, «Aún no hay suficientes días medidos en ambas condiciones».

3. **Tamaño del efecto: g de Hedges desde 10e** (D.96; antes, la *d* de Cohen sin corregir), también con al menos 3 días por condición:

$$d = \frac{\bar{x}_c - \bar{x}_s}{s_p}, \qquad s_p = \sqrt{\frac{(n_c-1)s_c^2 + (n_s-1)s_s^2}{n_c+n_s-2}}, \qquad g = d \cdot \Big(1 - \frac{3}{4(n_c+n_s) - 9}\Big)$$

   con la **desviación estándar agrupada** $s_p$. `Experimentos.resultado` guarda *g* en el campo `d` del `Resultado` (el nombre del campo no cambió). La palabra (`Experimentos.tamano`, sin cambios): |*g*| < 0.2 «muy pequeño», < 0.5 «pequeño», < 0.8 «mediano» y desde 0.8 «grande». Son los puntos de referencia de Cohen (1988). Desde 10e la tarjeta dice «Tamaño del efecto (g de Hedges): 0.69, mediano para cómo varían tus días; no se compara con los estudios entre personas» (sección 8.6.5).

4. **Prueba de tendencia** (sección 8.8).

### 8.6.2 Ejemplo trabajado (Figura 8.2, izquierda)

Un experimento inventado: 6 semanas alternas (SÍ no no SÍ SÍ no), 1 día de lavado, medida = valencia del día. Los datos se generaron con un efecto real de +0.5 y días que se parecen al anterior (autocorrelación 0.3).

- Cuentan 19 días con la acción (suma 92) y 19 sin ella (suma 81).
- Medias: 92/19 = **4.84** y 81/19 = **4.26**. Diferencia = **+0.58**.
- Varianzas: $s_c^2$ = 0.474 y $s_s^2$ = 0.871.
- $s_p$ = √((18 × 0.474 + 18 × 0.871)/36) = √0.672 = 0.820; *d* = 0.58/0.820 = **0.71**. Corrección de Hedges: 1 − 3/(4 × 38 − 9) = 1 − 3/143 = 0.979; *g* = 0.71 × 0.979 = **0.69**, «mediano». Es lo que da la app desde 10e; antes decía 0.71.

**Como lo calculaba la app hasta la 10c:** error estándar √(0.474/19 + 0.871/19) = √(0.0249 + 0.0459) = √0.0708 = **0.266**; intervalo 0.58 ± 1.96 × 0.266 = 0.58 ± 0.52 → **+0.06 a +1.10**. No toca el cero: «es una señal para ti».

**Con cada corrección por separado:**

- **Solo *t* en lugar de 1.96.** Los grados de libertad de Welch son ≈ 33, y el valor de *t* para el 95 % es 2.03: intervalo **+0.04 a +1.12**. Cambia poco con 19 días por lado. Con 3 días por lado (lo mínimo que acepta la app), *t* sería ≈ 2.78 en vez de 1.96: el intervalo tiene que ser **42 % más ancho**.
- **Solo la autocorrelación, con la *ρ* = 0.3 que sabemos que tienen los datos.** El factor √(1.3/0.7) = 1.36 en las dos condiciones da **−0.13 a +1.29**.

**Como lo calcula la app desde la 10c** (las dos correcciones, con la *ρ* estimada de los propios datos):

- Autocorrelación de los pares de días seguidos: 0.098 en la condición «con» y 0.408 en «sin». Corregidas: 0.098 + (1 + 4 × 0.098)/19 = **0.172** y 0.408 + (1 + 4 × 0.408)/19 = **0.547**.
- Varianza de cada media, inflada: 0.474/19 × 1.172/0.828 = 0.0353 y 0.871/19 × 1.547/0.453 = 0.1564; error estándar √(0.0353 + 0.1564) = **0.438**.
- Días efectivos: 19 × 0.828/1.172 = 13.4 y 19 × 0.453/1.547 = 5.6; grados de libertad de Welch ≈ **6.7**; *t* = **2.38**.
- Intervalo: 0.58 ± 2.38 × 0.438 → **−0.46 a +1.62**. **Toca el cero**: con estos datos no se distingue del azar, aunque aquí sabemos que el efecto real era +0.5.

La estimación de *ρ* con 19 días es muy ruidosa (salió 0.17 y 0.55 para un valor real de 0.3 en las dos): el intervalo de la app es honesto sobre lo poco que dicen 6 semanas, pero a costa de ser ancho. **[Aproximación]**: el factor AR(1) es exacto para la media de una serie con autocorrelación de orden 1 y aproximado para una diferencia entre bloques de semanas.

### 8.6.3 Por qué el intervalo de antes de la 10c era demasiado estrecho (pistas 1 y 2; H-7-3 y H-7-4, resueltos en 10c)

**1. Trataba cada día como independiente** (H-7-3). El cálculo suponía que cada día era un dato nuevo, como si fueran 38 personas distintas. Pero el ánimo, el sueño y casi todo lo que mide la app **se parecen de un día al siguiente**. La propia app lo sabe: para las correlaciones calcula la autocorrelación de cada variable y un *n efectivo* (`Estadistica.autocorr`; capítulo 6, sección 6.6, y H-5-5, el mismo problema en `Estadistica.diferencia` para todas las pantallas que la usaban, también resuelto en 10c). En los experimentos no lo usaba. Además, lo que se sortea son **semanas**, no días: en un experimento de 6 semanas hay 6 unidades sorteadas, no 38.

**2. Usaba 1.96 fijo** (H-7-3). Con pocos días, el valor correcto sale de la distribución *t* y es mayor.

**3. Se podía mirar todos los días** (H-7-4). La tarjeta muestra el resultado en cuanto hay 3 días de cada condición, y lo actualiza cada día. Desde la 10c sigue mostrándolo, pero como **provisional** y sin la frase de «señal» mientras dura el plan. Si miras cada día y te detienes la primera vez que dice «señal», el intervalo ya no significa lo que dice: mirar repetidamente datos que se acumulan aumenta mucho la probabilidad de una falsa señal (Armitage et al., 1969).

**Cuánto importa (Figura 8.2, derecha).** Simulé 3000 experimentos de 6 semanas alternas con 1 día de lavado **en los que la acción no hace nada**, con el método exacto de la app antes y después de la 10c (después: sorteo por pares y la diferencia de 8.6.1):

| Situación | Falsas señales antes de la 10c | Desde la 10c | Lo prometido |
|---|---|---|---|
| Días independientes, mirar solo al final | 6 % | 3 % | 5 % |
| Autocorrelación 0.3, mirar al final | 14 % | 4 % | 5 % |
| Autocorrelación 0.5, mirar al final | 24 % | 3 % | 5 % |
| Días independientes, detenerse en la primera «señal» | 30 % | — (provisional) | 5 % |
| Autocorrelación 0.3, detenerse en la primera «señal» | 50 % | — (provisional) | 5 % |

Antes de la 10c, con autocorrelaciones como las que suelen tener las medidas diarias de ánimo, **entre una de cada siete y una de cada cuatro** «señales» podían ser azar, y la mitad si te detenías al ver la primera. La cifra de 0.3 a 0.5 es ilustrativa: no verifiqué un valor típico en la literatura **[sin verificar]**; lo que importa es que cualquier autocorrelación positiva estrecha el intervalo de más.

**Desde la 10c el método es conservador**: da menos del 5 % de falsas señales, porque *ρ* se estima con pocos días y se acota hacia arriba. **El costo es el poder** (la probabilidad de ver un efecto que sí existe). En la misma simulación, con autocorrelación 0.3 y un efecto real de 0.5 desviaciones, un experimento de 6 semanas lo detecta el **13 %** de las veces (antes, el 40 %, pero con 14 % de falsas señales); con un efecto de 0.8, el **29 %**; con 12 semanas, el **30 %** y el **62 %**. Para efectos moderados, conviene ir a 12 semanas y no esperar certezas de 4 o 6.

![Figura 8.2](figuras/fig21_resultado_experimento.png)

*Figura 8.2. Izquierda: un experimento inventado con su resultado calculado como antes de la 10c, con cada corrección por separado y como desde la 10c. Derecha: con qué frecuencia el método de la app da una «señal» cuando la acción no hace nada, antes y desde la 10c. Script: `figuras/fig21_resultado_experimento.py`.*

### 8.6.4 Qué sería más adecuado

**[Validado]** en la literatura de caso único: la **prueba de aleatorización** (Kratochwill y Levin, 2010; Tanious y Onghena, 2019). La idea es sencilla:

1. Calcula la diferencia con el plan que de verdad se sorteó.
2. Calcula la misma diferencia con **cada uno de los otros planes que pudieron salir** (con los mismos datos y el lavado que correspondería a cada plan).
3. El valor *p* es la fracción de planes que dan una diferencia igual o mayor que la real (en valor absoluto, si la prueba es de dos colas).

Su ventaja: **no supone que los días sean independientes**, porque se apoya solo en que el sorteo fue real. Su límite: el *p* más pequeño posible es 1 / número de planes (en una prueba de una cola; en la de dos colas que hace R, el doble, como se ve abajo). Con el sorteo por pares de la 10c hay $2^{N/2}$ planes: con 4 semanas, 0.25; con 6, 0.125; con 8 (16 planes), 0.0625; con 12 (64 planes), 0.016. Es decir, **con 4, 6 u 8 semanas no se puede llegar a una conclusión con el criterio habitual** (*p* < 0.05), por grande que sea el efecto; de las duraciones de la app, solo 12 semanas lo permite. Hasta 10e era una **propuesta no aplicada**: no había función de R para los experimentos, aunque `vm_experimentos.csv` exportaba el plan, el lavado, la variable, la fecha de fin y las respuestas (H-7-11). **Desde 10e (D.97) la hace `vm_experimento(id)` en R**; la app sigue sin ofrecerla y se queda con la corrección por autocorrelación de la 10c.

**Qué hace `vm_experimento(id, datos = panel, hoy)`** (`vestigium.R`):

- **Repite `Experimentos.resultado`**: el mismo plan y lavado, los mismos días que cuentan (sin hoy, hasta la fecha de fin), la diferencia con Welch y el error corregido por autocorrelación (`.vm_dif_dias`), la *g* de Hedges, la tendencia dentro de cada condición, el cumplimiento (con «Creatina» completada con `supl_creatina_tomado`, sección 8.7.1), el cociente y si es provisional.
- **Prueba de aleatorización exacta**: arma **todos** los planes que el sorteo por pares pudo dar (2^k con *k* pares de semanas; el doble si sobra una semana), aplica a cada uno su propio lavado y calcula la diferencia de medias con los mismos datos. Los planes que dejan menos de 3 días medidos en una condición no tienen diferencia y no entran. `p_aleatorizacion` = fracción de planes con |diferencia| ≥ |diferencia observada|: es de **dos colas**. Devuelve también `planes` (cuántos entraron) y `distribucion` (la diferencia de cada uno).
- **Cuándo no hay prueba** (`p_aleatorizacion = NA`): en «antes y después», porque no hubo sorteo; y si el plan guardado no es uno de los del sorteo por pares (un experimento sellado antes de la 10c, con sorteo libre).
- **El criterio sellado (desde 10f, D.113).** Lee `sentido` y `umbral` de `vm_experimentos.csv` y los devuelve, con `criterio`: la misma regla que `Experimentos.criterio` (`obs × sentido ≥ umbral`, con umbral 0 si falta, y `lo > 0` si se esperaba que subiera o `hi < 0` si que bajara); `NA` si no hay sentido, si no hay diferencia o si es provisional. Imprime «Criterio sellado: hacia arriba al menos 0.3, con el intervalo lejos del cero · se cumple» (o «no se cumple», o «se decide al terminar»). Devuelve también `intacto`, tal como lo exporta la app (columna `intacto` de `vm_experimentos.csv`: 1 si la huella coincide, 0 si no, vacío sin huella), y si es 0 avisa «Ojo: lo sellado cambió después del sello». R **no** recalcula la huella: confía en lo que dijo la app al exportar. Lo corrí con CSV inventados (6 semanas alternas, 1 día de lavado, sin `vm_diccionario.csv`): con sentido 1 y umbral 0.3 dio `criterio = TRUE`; con sentido −1 y los mismos datos, `FALSE`, y con `intacto` en 0, el aviso; sin sentido, `NA`.

*Ejemplo (inventado).* 4 semanas sin lavado, 7 días medidos por semana, con promedios semanales 5.0, 4.2, 4.8 y 4.4. Salió «SÍ no SÍ no». Los 4 planes posibles (dos pares, cada uno «SÍ no» o «no SÍ») dan: «SÍ no SÍ no», (5.0 + 4.8)/2 − (4.2 + 4.4)/2 = **+0.6** (el observado); «SÍ no no SÍ», 4.7 − 4.5 = +0.2; «no SÍ SÍ no», −0.2; «no SÍ no SÍ», −0.6. Tienen |diferencia| ≥ 0.6 dos de los cuatro: *p* = 2/4 = **0.5**.

**En la prueba de dos colas, el mínimo es el doble.** El ejemplo muestra por qué: en el sorteo por pares, el plan **espejo** (cada «SÍ» cambiado por «no») siempre es posible y, sin lavado, da exactamente la diferencia contraria. Como la prueba es de dos colas, cuenta igual que la observada, y el *p* más pequeño posible es **2 / planes**: 0.125 con 8 semanas y 2/64 ≈ **0.031** con 12. Con lavado, el espejo pierde o gana los primeros días de lavado y su diferencia puede quedar un poco por debajo de la observada, así que el mínimo puede bajar hasta 1 / planes. La conclusión no cambia: de las duraciones de la app, solo 12 semanas pueden dar *p* < 0.05. Lo comprobé con una simulación en Python que copia la lógica de la función, con un efecto enorme para que el plan observado sea el más extremo: con 12 semanas sin lavado, el *p* salió siempre 0.031; con 1 o 2 días de lavado, entre 0.016 y 0.031. En R, la función se corrió con datos inventados de 4, 5 y 6 semanas, con y sin lavado: sin lavado, la distribución sale siempre simétrica, y la nota que imprime da el mínimo calculado con los datos (Apéndice C).

**[Validado]** como método (Kratochwill y Levin, 2010; Tanious y Onghena, 2019); su implementación en R se probó con CSV sintéticos y un caso hecho a mano (Apéndice D, D.97), no con datos reales.

### 8.6.5 El tamaño del efecto en una sola persona

- **Corrección de Hedges (desde 10e, D.96; H-7-7).** Hasta 10e la tarjeta daba la *d* de Cohen sin corregir, que con pocos datos sale un poco inflada; Hedges (1981) derivó la corrección. La app usa la aproximación habitual, el factor 1 − 3/(4(*n*<sub>c</sub> + *n*<sub>s</sub>) − 9): 0.98 con 19 + 19 días (0.71 → 0.69) y 0.80 con 3 + 3 (con el mínimo de días, el tamaño baja un 20 %). Importa solo con muy pocos días. **[Validado]** como corrección.
- **Los umbrales de Cohen no significan lo mismo aquí.** Cohen propuso 0.2, 0.5 y 0.8 para diferencias **entre personas**, con la desviación estándar de un grupo de personas. En un experimento N-de-1, la desviación es la de **tus días**, que suele ser menor que la que hay entre personas, así que el mismo cambio da un *d* más grande. Un «mediano» aquí no equivale a un «mediano» en un estudio de grupos. **[Aproximación]**: es útil como escala dentro de la app, no como comparación con la literatura. Desde 10e la tarjeta lo dice: «… mediano para cómo varían tus días; no se compara con los estudios entre personas» (D.96, la otra mitad de H-7-7).

---

## 8.7 Lo que no se registra: la adherencia

Hasta el esquema 12, la app guardaba qué **debías** hacer cada semana, pero **no si lo hiciste** (H-7-8; desde el esquema 13 se pregunta, sección 8.7.1). El resultado sigue comparando lo asignado: si en una semana «SÍ» te dormiste tarde tres noches, esos días cuentan igual como «con la acción». Es lo que en ensayos clínicos se llama análisis por **intención de tratar**: compara lo asignado, no lo hecho. Es una forma legítima de análisis (estima el efecto de *intentar* la acción) **[Validado]**, pero diluye el efecto si no la cumples; sin la pregunta diaria, además, no había forma de saber cuánto la cumpliste.

Relacionado: **hasta el esquema 12, «Terminar experimento» no guardaba la fecha** (H-7-2). Solo marcaba `activo = false`. `Experimentos.resultado` seguía contando todos los días hasta el final del plan original, así que si terminabas en la semana 3 de 6 y dejabas de hacer la acción, las semanas «SÍ» posteriores se contaban como «con la acción» aunque no la hiciste. Además, el aviso del día desaparecía y no sabías qué semanas tocaban.

### 8.7.1 Lo que cambió en el esquema 13 (P-12 e I-3)

- **Fecha de fin.** «Terminar» guarda el día lógico en `Experimento.fin`, y el resultado deja de contar los días posteriores. La tarjeta dice «Terminado el …» y, si fue antes de acabar el plan, que lo que seguía no cuenta. «En curso» (Análisis) cuenta solo los que no tienen fin y cuyo plan no ha vencido (`Experimentos.enCurso`).
- **Restaurar.** Un respaldo viejo ya no reactiva un experimento terminado, y se juntan las respuestas de los dos (`Fusion.experimento`, capítulo 10).
- **Cumplimiento.** Con las respuestas de «¿Lo hiciste hoy?», `Experimentos.resultado` da `Cumplimiento`, sin contar los días de lavado: cuántos días de cada condición hubo, cuántos respondiste y en cuántos lo hiciste. La tarjeta muestra el porcentaje de días en que lo hiciste en las semanas «SÍ» y en las «no», y cuántos días quedaron sin respuesta.
- **El análisis principal no cambia.** Sigue siendo **según el plan** (intención de tratar), y la tarjeta lo dice. Como dato secundario, cuando el cumplimiento difiere al menos 20 puntos entre condiciones, la tarjeta da el **cociente**: la diferencia según el plan entre la diferencia de cumplimiento (`Resultado.cociente`). Es una estimación del efecto de **hacerlo de verdad**, y supone que saber qué semana tocaba no cambia nada por sí solo. Eso es dudoso sin cegamiento (sección 8.10). **[Aproximación]**. *Ejemplo (inventado):* diferencia según el plan +0.30; lo hiciste el 70 % de los días «SÍ» y el 10 % de los «no». Cociente = 0.30 ÷ 0.60 = +0.50.
- **Por qué no se usa «por lo hecho».** No se calcula el análisis «por lo hecho», que compara días hechos contra no hechos. Si tiendes a cumplir más en los días buenos, ese análisis exagera el efecto: en una simulación del módulo 9 lo **duplicaba**.
- **Límites de las respuestas.** «No respondí» no es «no lo hice», y probablemente se responde menos en los días malos. Responder días después mete sesgo de memoria.
- **Creatina (desde 10e, D.96; H-9-26).** En la plantilla «Creatina», la adherencia ya está en el registro de suplementos. Hasta 10e el experimento no la usaba; desde 10e, `Experimentos.resultado` toma, en los días que cuentan **sin respuesta** a «¿Lo hiciste hoy?», lo que dice `supl_creatina_tomado` (hecho si es mayor que 0). Una respuesta tuya manda sobre el registro. La tarjeta lo avisa («Los días sin respuesta se completan con tu registro de suplementos») y el cumplimiento aparece aunque nunca hayas respondido. `supl_creatina_tomado` solo existe desde la primera toma anotada: los días anteriores, típicamente las semanas «no» del principio, siguen sin respuesta. La pregunta sale igual, por uniformidad. **[Diseño propio]**.

---

## 8.8 La prueba de tendencia

### 8.8.1 Qué hace (desde la entrega 10c: `Experimentos.tendencia`)

Si hay al menos 8 días que cuentan y tienen dato (sin hoy), ajusta por mínimos cuadrados **a la vez** la tendencia y la condición:

$$y = a + b\,i + c\,\text{con} + \varepsilon$$

con *i* = número de día (0, 1, 2…) y «con» = 1 los días con la acción y 0 los demás. *b* es la **pendiente dentro de cada condición** (la misma para las dos): cuánto cambia la medida por día una vez descontado el efecto de la condición. Su error estándar sale de la matriz del ajuste con *n* − 3 grados de libertad, y el aviso salta si |*b*/EE(*b*)| supera el **cuantil 0.975 de la *t*** con *n* − 3 grados. Si todos los días son de la misma condición (o el día y la condición no se pueden separar), queda la pendiente simple con *n* − 2. El aviso dice: «Ojo: dentro de cada condición, tu medida venía cambiando sola (… por día). Parte de la diferencia puede ser esa tendencia», y añade «como el plan quedó en bloque (primero una condición y luego la otra), pesa más» si `Experimentos.enBloque`.

**Hasta la 10c** la recta se ajustaba **con las dos condiciones juntas** (valor contra día) y avisaba si |*b*/EE(*b*)| > 2:

$$b = \frac{\sum (i-\bar{i})(y-\bar{y})}{\sum (i-\bar{i})^2}, \qquad EE(b) = \sqrt{\frac{\sum (y - \hat{y})^2/(n-2)}{\sum (i-\bar{i})^2}}$$

### 8.8.2 Por qué importa la tendencia

Si la medida sube sola (por la estación, por avanzar en el gimnasio, por salir de una mala racha), en un diseño **antes/después** el periodo «después» siempre se verá mejor, aunque la acción no haga nada. Es la principal debilidad de los diseños AB, y la razón de alternar y sortear (Kratochwill y Levin, 2010).

**Simulación (Figura 8.3).** Con una tendencia de 0.02 desviaciones por día (en 8 semanas, ≈ 1.1 desviaciones) y **sin efecto real**:

| Diseño | Falsas señales antes de la 10c | Desde la 10c |
|---|---|---|
| Antes/después | 55 % | 39 % |
| Semanas alternas (antes: sorteo libre; desde la 10c: por pares) | 14 % | 2 % |

**El antes/después sigue siendo muy vulnerable**: la corrección por autocorrelación ensancha el intervalo, pero la tendencia sigue cayendo entera en una condición. Y la prueba de tendencia nueva lo detecta mal en ese diseño: en esa misma simulación avisa solo el **22 %** de las veces, porque dentro de cada mitad hay poca información para estimar la pendiente. Por eso la plantilla «Creatina», que no puede alternar, debe leerse con cuidado (sección 8.9).

### 8.8.3 El problema de la prueba de antes (H-7-6, resuelto en 10c)

La recta se ajustaba **sin separar la condición**. En un antes/después, un **efecto real** también hace que la segunda mitad sea más alta, y la recta lo confundía con una tendencia. En la simulación, con un efecto real *d* = 0.8 y **sin ninguna tendencia**, el aviso «tu medida venía cambiando sola» aparecía en el **71 %** de los antes/después y en el 16 % de los alternos (lo esperado por azar sería ≈ 5 %). Es decir: justo cuando la acción funcionaba, la app sugería que quizá fue la tendencia.

Con el modelo de la 10c, en la misma situación el aviso sale el **5 %** de las veces en los antes/después y el **6 %** en los alternos por pares: lo esperado por azar. **[Validado]** como método (regresión con tendencia y condición); en un antes/después puro, tendencia y efecto se siguen separando mal, porque la condición cambia una sola vez.

![Figura 8.3](figuras/fig22_tendencia_diseno.png)

*Figura 8.3. Izquierda: una tendencia sin efecto real produce una diferencia falsa en un antes/después (con el método de antes y con el de la 10c). Centro: con semanas alternas por pares, la misma tendencia se reparte entre las dos condiciones. Derecha: frecuencia de falsas señales con tendencia, y de avisos de tendencia cuando hay un efecto real, antes de la 10c y desde la 10c. Script: `figuras/fig22_tendencia_diseno.py`.*

---

## 8.9 Las plantillas

`Experimentos.PLANTILLAS` (`data/Lote3.kt`):

| Plantilla | Acción | Medida | Diseño | Semanas | Lavado | Nota de la app |
|---|---|---|---|---|---|---|
| Creatina | 5 g al día | `fuerza_ref_kg` desde 10f (antes, `volumen_kg`) | antes/después | 12 (8 hasta la 10c) | 28 | «la reserva del músculo tarda semanas en llenarse y en vaciarse, así que alternar semanas no sirve»; desde 10f sigue: «Se mide con un ejercicio fijo, el press inclinado con mancuernas (la 1RM estimada de tu mejor serie), para que no dependa de qué entrenaste ese día; solo cuentan los días en que lo hiciste. Si vas subiendo de peso, la cifra sube sola: una mejora no prueba que fue la creatina.» |
| Hora de dormir | en la cama antes de las 00:00 | `valencia` | alterno | 6 | 1 | «el primer día de cada cambio no cuenta» |
| Pantalla antes de dormir | sin pantalla la última hora | `sueno_calidad` | alterno | 6 | 1 | «Semanas alternas sorteadas» |
| Cafeína en la tarde | nada después de las 14:00 | `sueno_horas` | alterno | 6 | 2 | «dos días de lavado por la cafeína que queda en el cuerpo» |

**Por qué la creatina va en antes/después.** La creatina no actúa como una pastilla para dormir: se acumula en el músculo. Hultman et al. (1996) midieron que la creatina total del músculo sube alrededor de un 20 % tras 6 días a 20 g diarios y se mantiene con 2 g diarios durante 30 días más. Si la reserva tarda semanas en llenarse y en vaciarse, alternar semanas solo compararía «medio lleno» con «medio lleno». El razonamiento es correcto. El modelo de saturación de la app (28 días para llenarse y 35 para vaciarse) se trata en el capítulo 5 (sección 5.6.1); desde 10e los 28 días son con la dosis del estudio, 3 g al día, y con los 5 g de la plantilla la reserva llega al 95 % en unos 17 días (D.98). El mismo estudio midió el vaciado: 30 días después de dejar la creatina, su concentración en el músculo ya no difería del valor inicial.

**Hasta la entrega 10c la plantilla no funcionaba** (H-7-1): con 28 días de lavado sobre 4 semanas «SÍ», todos los días con la acción quedaban excluidos. Desde la 10c dura 12 semanas y quedan 14 días con la acción y 42 sin ella (sección 8.5.3).

**La medida, hasta 10f: `volumen_kg`** (H-7-14). El volumen (peso × repeticiones de todas las series efectivas del día) depende de **qué sesión** hiciste (pierna o brazo), no solo de tu fuerza: una semana con más días de pierna tiene más volumen. Y en antes/después, si estás progresando en el gimnasio, el volumen sube solo.

**Desde 10f (D.114): la fuerza de referencia, `fuerza_ref_kg`.** Es la mejor 1RM estimada del día en **un solo ejercicio fijo**, el press inclinado con mancuernas (`Macrofactor.fuerzaReferencia`):

- **Qué ejercicio cuenta** (`Macrofactor.esReferencia`). El nombre del ejercicio se pasa a minúsculas sin acentos ni signos (`Texto.slug`: «Press inclinado con mancuernas» → `press_inclinado_con_mancuernas`), y cuenta si contiene «press» y «inclin», además de «mancuern», «dumbbell» o la palabra suelta «db». Así se reconocen «Incline Dumbbell Press», «Incline DB Bench Press», «Press inclinado con mancuernas» y «Press de banca inclinado con mancuernas», y quedan fuera las aperturas y los curls en banco inclinado (que no dicen «press»). **[Diseño propio]**: no se comprobó en la sesión con qué nombre exacto exporta MacroFactor este ejercicio (Apéndice D). Cuándo falla: un nombre sin esas palabras (una abreviatura propia, o «banco inclinado con mancuernas» sin «press») no cuenta, y otro press inclinado con mancuernas que no sea el de banca (uno de pie, por ejemplo) sí contaría.
- **Cómo se calcula.** Para cada serie de ese ejercicio, la 1RM de Epley de `Macrofactor.e1rm`: peso × (1 + repeticiones/30), solo con 1 a 12 repeticiones, peso mayor que 0 y sin las series de calentamiento; redondeada a 0.1 kg (capítulo 4, sección 4.6.6, y capítulo 5, sección 5.9). `fuerza_ref_kg` es la mayor del día; vacía (`null`) los días sin ese ejercicio. **[Aproximación]**: Epley estima el máximo a partir de una serie submáxima, con error que crece con las repeticiones.
- **Dónde está.** Se calcula al importar MacroFactor y, una vez, para lo ya importado (`Repo.completarFuerzaReferencia`). Es una variable del panel (grupo «Cuerpo»; capítulo 6, sección 6.3.1), una medida posible en cualquier experimento (sección 8.3.1), y sale en `vm_entrenos.csv` y `vm_panel.csv`; `vm_series.csv` marca con `referencia` las series de ese ejercicio.

*Ejemplo (inventado).* Un día hiciste press inclinado con mancuernas: una serie de calentamiento de 12 kg × 15, y luego 24 kg × 8 y 26 kg × 5. El calentamiento no cuenta (y además pasa de 12 repeticiones). 24 × (1 + 8/30) = 30.4 kg; 26 × (1 + 5/30) = 30.3 kg. `fuerza_ref_kg` = **30.4**. Ese día también hiciste sentadilla, que sube `volumen_kg` pero no cambia `fuerza_ref_kg`.

**Lo que mejora y lo que no.** Con un solo ejercicio, la cifra ya no depende de qué sesión tocó: los días de pierna no cuentan, porque no tienen el ejercicio (`fuerza_ref_kg` vacía, y en el experimento solo cuentan los días con dato). Pero **la tendencia sigue**: si vas subiendo de peso, la 1RM sube semana a semana con o sin creatina, y en un antes/después toda esa subida cae en la mitad «con la acción» (el caso de la Figura 8.3). La nota de la plantilla lo dice. Además, solo cuentan los días en que hiciste ese ejercicio: con uno o dos por semana, 14 días con la acción dan pocos datos. Un experimento creado antes de 10f con esta plantilla **sigue midiendo `volumen_kg`**: la medida queda sellada.

Las otras tres plantillas son diseños razonables: efectos que aparecen y se van en uno o dos días, medidas diarias y lavado corto. **[Aproximación]**: las duraciones de lavado son razonables pero no vienen de un estudio específico.

---

## 8.10 Cegamiento y efecto expectativa

En los ensayos con medicamentos, ni el paciente ni el médico saben quién toma el fármaco y quién la pastilla de azúcar. Aquí eso es imposible: **sabes** si te acostaste antes o si tomaste café. Y además escribiste qué esperabas (la hipótesis) y ves el plan completo desde el primer día.

Esto importa sobre todo con medidas **subjetivas** (valencia, calidad de sueño percibida): si crees que dormir antes te hará sentir mejor, es probable que califiques tus días un poco mejor en las semanas «SÍ», aunque el sueño no haya cambiado nada. Es el **efecto expectativa** o placebo. La extensión CENT pide reportar si hubo cegamiento justamente por esto (Vohra et al., 2015).

Lo que ayuda (sin cambiar la app):

- Preferir **medidas objetivas** cuando existan: `sueno_horas` del reloj en vez de `sueno_calidad`, `rt_media` (tiempo de reacción) en vez de «me sentí despierto».
- Mirar el resultado **solo al final**.
- Para la valencia, preferir `muestreo_valencia` (avisos al azar en el momento, capítulo 3), que es menos propensa a reinterpretaciones que la valencia del día recordada en la noche. No pude comprobar cuánto reduce el sesgo de expectativa **[sin verificar]**.

**[Aproximación]**: sin cegamiento, el resultado responde a «¿hacer esto **sabiendo que lo hago** me cambia?», que es una pregunta legítima para uso personal, pero no aísla el efecto «químico» o fisiológico de la acción.

---

## 8.11 «Antes y después» en Explorar: un estudio observacional

### 8.11.1 Qué hace (`EtapasDeVida`, `ui/pantallas/Explorar.kt`)

Para los **8 días más recientes** en que guardaste cambios en «Conocerte» (mudanza, pareja, trabajo…; `perfil_historial`), compara siete variables: valencia (la tarjeta la rotula «ánimo»), horas de sueño, minutos de pantalla, pasos, gasto, personas mencionadas y palabras escritas.

- **Antes**: los días −1 a −60 respecto a la fecha del cambio.
- **Después**: los días 0 a 59.
- Usa la misma diferencia de los experimentos: desde la entrega 10c, `Estadistica.diferenciaEnDias(despues, antes)`, con *t* de Welch y el error estándar corregido por la autocorrelación de cada periodo (antes, con 1.96 y días independientes). Si el intervalo no toca el cero, muestra el cambio sin advertencia; si lo toca, añade «podría ser azar».
- Desde la 10c, los cambios de **un mismo día** (un mismo guardado) van en **una sola tarjeta**, y el **primer llenado** de «Conocerte» (de vacío a lo que dijiste, `Perfiles.primerGuardado`) no sale: no es un cambio de vida (D.68).
- La sección avisa: «Muchas cosas cambian a la vez; tómalo como una pista».

### 8.11.2 Por qué no es un experimento

Nadie sorteó cuándo mudarte o cambiar de trabajo, y esos cambios suelen venir con muchos otros (estación, rutina, estrés). Es un **estudio observacional** del tipo de *serie de tiempo interrumpida* (comparar una serie antes y después de un evento). **[Aproximación]**: útil para generar ideas, no para concluir causas. La propia app lo dice bien.

### 8.11.3 Cuándo falla (H-7-12)

- **Muchas comparaciones.** 8 tarjetas × 7 variables = hasta **56 comparaciones**. Aunque nada cambiara de verdad, con un intervalo que da el 5 % de falsas señales cabe esperar unas **3** comparaciones «claras» por azar; con el intervalo de antes de la 10c, que ignoraba la autocorrelación, muchas más (sección 8.6.3). No se corrige por comparaciones múltiples.
- **Hasta la 10c, días tratados como independientes**, con 1.96 (igual que en H-7-3); desde la 10c, con *t* y autocorrelación.
- **Tendencias**: el mismo problema del antes/después (sección 8.8).
- **La fecha es la del registro en la app**, no la del cambio real (como H-6-3).
- **Un mismo cambio podía salir varias veces** (resuelto en 10c): si en una sola edición cambiabas ciudad, vivienda y «vive con», salían tres tarjetas con la misma fecha y **los mismos números**, lo que daba la impresión de tres hallazgos. Ahora es una tarjeta con los tres cambios.
- **Ventanas que se enciman**: dos cambios separados por menos de 60 días comparten días.
- `personas` y `palabras` miden **cuánto escribes**, no tu vida social. (Hasta la 10c, `gasto_total` ponía 0 en todo día con diario, aun antes del primer gasto anotado; desde la 10c, solo desde el primer gasto, H-1-1.)

---

## 8.12 Lo que el resultado puede y no puede decir

**Puede decir**, si el experimento fue alterno, sorteado, con lavado razonable, una medida objetiva, suficientes semanas, sellaste el criterio (desde 10f) y miraste solo al final: «en mi caso, en este periodo, hacer esto se asoció con este cambio, y es poco probable que sea solo por cómo cayeron las semanas».

**No puede decir:**

- Que el efecto sea **igual** en otra época de tu vida (un N-de-1 vale para la persona y el periodo en que se hizo).
- Que el efecto sea **fisiológico** y no expectativa (sin cegamiento).
- Mientras el plan siga en curso: el resultado es **provisional** (sección 8.6.1).
- Mucho con pocas semanas: desde la 10c el intervalo es honesto y, con 4 o 6 semanas y un efecto moderado, casi siempre incluirá el cero (sección 8.6.3).
- Nada, si el diseño fue antes/después con una medida que tiende a subir o bajar sola.

---

## 8.13 Resumen de decisiones

| Decisión | Clasificación | Respaldo |
|---|---|---|
| Comparar periodos con y sin la acción en una misma persona | Validado | Guyatt et al. (1986); Kravitz et al. (2014) |
| Sorteo real del orden de las semanas, por pares desde la 10c (antes, libre) | Validado como aleatorización | Kratochwill y Levin (2010) |
| Empezar en lunes, semanas completas | Diseño propio (acierto: equilibra el día de la semana) | — |
| Sellar pregunta e hipótesis con la hora | Diseño propio inspirado en el prerregistro | Nosek et al. (2018); Simmons et al. (2011) |
| Desde 10f: sellar también el criterio (sentido + umbral + intervalo de 95 % que no toca el cero), decidido solo al terminar | Diseño propio | — |
| Desde 10f: huella SHA-256 de lo sellado, comprobada en cada tarjeta | Validado (que SHA-256 detecta cambios, como función hash criptográfica estándar); no protege de una edición deliberada que la recalcule | — |
| Lavado en ambos sentidos tras cada cambio | Aproximación | — |
| No sellar si alguna condición podría quedar con menos de 7 días (10c) | Diseño propio | — |
| Hasta la 10c: intervalo de Welch con 1.96, días independientes | Aproximación (demasiado estrecho) | Armitage et al. (1969) sobre mirar repetidamente |
| Desde la 10c: *t* de Welch con el error estándar corregido por AR(1) (*ρ* corregida por sesgo, acotada 0–0.9) | Aproximación (conservadora) | Welch (1947); Marriott y Pope (1954) [la forma de la corrección, sin verificar] |
| Resultado provisional mientras dura el plan; hoy fuera | Diseño propio inspirado en el problema de mirar repetidamente | Armitage et al. (1969) |
| Tamaño del efecto con desviación agrupada: *d* de Cohen hasta 10e, *g* de Hedges desde 10e; umbrales 0.2/0.5/0.8 dichos como relativos a tus días | Validado (la corrección); Aproximación (los umbrales) | Cohen (1988); Hedges (1981) |
| «Creatina»: días sin respuesta completados con `supl_creatina_tomado` (10e) | Diseño propio | — |
| Prueba de aleatorización exacta en R, `vm_experimento` (10e) | Validado como método | Kratochwill y Levin (2010); Tanious y Onghena (2019) |
| Hasta la 10c: prueba de tendencia con la recta sin separar la condición | Diseño propio (confundía efecto con tendencia) | — |
| Desde la 10c: tendencia con valor ~ día + condición y *t* con *n* − 3 grados | Validado como método | — |
| Plantilla «Creatina» en antes/después, 12 semanas desde la 10c | Razonamiento correcto; expuesta a tendencias | Hultman et al. (1996) |
| Desde 10f: «Creatina» mide `fuerza_ref_kg`, la mejor 1RM de Epley del día en press inclinado con mancuernas | Aproximación (la 1RM de Epley); sigue expuesta a la progresión | capítulo 5 |
| Reconocer el ejercicio de referencia por su nombre (`Macrofactor.esReferencia`) | Diseño propio | — |
| «Antes y después» de los cambios de vida, 60 contra 60 días, una tarjeta por guardado | Aproximación (observacional) | — |
| Fecha de fin y «¿Lo hiciste hoy?» en las dos condiciones (esquema 13) | Diseño propio | — |
| Cociente efecto según el plan ÷ diferencia de cumplimiento, como dato secundario | Aproximación (supone que saber la condición no cambia nada) | — |

---

## 8.14 Resumen del capítulo

1. Los experimentos de la app siguen la lógica de los **ensayos N-de-1**: semanas con y sin la acción, **sorteadas de verdad** (por pares desde la entrega 10c), que empiezan en lunes, con la pregunta y la hipótesis **selladas** antes de empezar. Desde 10f se puede sellar también el **criterio** (hacia dónde y cuánto), que la tarjeta aplica al terminar, y lo sellado lleva una **huella** SHA-256 que avisa si algo cambió después (H-7-10).
2. El hallazgo más grave del módulo 7: **la plantilla «Creatina» nunca podía dar resultado**, porque sus 28 días de lavado cubrían todo el periodo con la acción; y con 7 o 28 días de lavado, un plan alterno podía quedarse sin días (H-7-1). Desde la 10c no se deja sellar si alguna condición podría quedar con menos de 7 días, y «Creatina» dura 12 semanas (14 + 42 días). Desde 10f mide la fuerza en un ejercicio fijo (`fuerza_ref_kg`) en lugar del volumen, que dependía de la sesión; la progresión en el gimnasio la sigue subiendo sola (H-7-14).
3. El **intervalo** trataba los días como independientes y usaba 1.96: con autocorrelaciones de 0.3 a 0.5, daba falsas señales el 14 % a 24 % de las veces en lugar del 5 %; si se miraba cada día y uno se detenía en la primera señal, hasta el 50 % (H-7-3, H-7-4). Desde la 10c usa *t* de Welch y corrige por autocorrelación (3–4 % de falsas señales en la simulación, a costa de poder), excluye hoy y llama «provisional» al resultado mientras dura el plan. La **prueba de aleatorización** por semanas no está en la app; desde 10e la hace `vm_experimento()` en R (H-7-11), y con el sorteo por pares solo 12 semanas pueden dar *p* < 0.05.
4. El **sorteo libre** podía producir un antes/después disfrazado (33 % de las veces con 4 semanas); desde la 10c se sortea por pares, y la tarjeta avisa si el plan real quedó en bloque (H-7-5, H-9-30).
5. La **prueba de tendencia** confundía un efecto real con una tendencia en los antes/después: avisaba en el 71 % de los casos con un efecto grande y sin tendencia (H-7-6). Desde la 10c separa tendencia y condición y avisa en el 5 %; pero en un antes/después con tendencia real solo la detecta el 22 % de las veces.
6. Hasta el esquema 12, **«Terminar» no guardaba la fecha** ni se registraba si cumpliste la acción (H-7-2, H-7-8). Desde el 13 se guardan la fecha de fin y «¿Lo hiciste hoy?», con el cumplimiento por condición y un cociente secundario. No hay cegamiento: prefiere medidas objetivas y mirar solo al final.
7. «Antes y después» en Explorar es **observacional**, con hasta 56 comparaciones sin corregir (H-7-12); desde la 10c, una tarjeta por guardado y con el mismo intervalo corregido.
8. El módulo 7 identificó **14 hallazgos** (H-7-1 a H-7-14) y no cambió código. Después, el esquema 13 (entrega 10b) resolvió H-7-2 y H-7-8 (el rótulo «según el plan» llegó en la 10c), y la entrega 10c resolvió H-7-1, H-7-3, H-7-4, H-7-5, H-7-9 y H-7-13, y en parte H-7-6 (el efecto que se muestra no descuenta la tendencia) y H-7-12 (las comparaciones múltiples siguen sin corregirse). La entrega 10e resolvió H-7-7 (*g* de Hedges y la escala dicha como relativa a tus días) y H-7-11 (`vm_experimento` con la prueba de aleatorización). La entrega 10f resolvió los dos que quedaban pendientes: H-7-10 (el criterio se sella, con una huella; sigue siendo opcional) y H-7-14 (`fuerza_ref_kg` en un ejercicio fijo). En total: 12 resueltos, 2 en parte (H-7-6 y H-7-12) y ninguno pendiente.

---

### Referencias citadas en este capítulo

Ver `referencias.md`. Citas: Armitage, McPherson y Rowe (1969); Cohen (1988); Guyatt et al. (1986); Hedges (1981); Hultman et al. (1996); Kratochwill y Levin (2010); Kravitz et al. (2014); Marriott y Pope (1954); Nosek et al. (2018); Simmons, Nelson y Simonsohn (2011); Tanious y Onghena (2019); Vohra et al. (2015); Welch (1947).
