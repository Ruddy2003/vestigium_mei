# Capítulo 3. Medición del estado subjetivo

> **Para qué sirve este capítulo.** Explica **cómo mide la app lo que sientes y piensas**: el ánimo en el momento, el ánimo del día, las emociones con nombre, los cuestionarios, los sueños, con quién estuviste y algunas mediciones raras (la brújula política, la prueba de reacción). Para cada cosa dice qué se pregunta, cuándo, en qué escala, cómo se convierte en número para los análisis y qué tan sólido es eso científicamente. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10a…10d» (lo de la revisión de navegación, «desde N1…N7»; lo de la entrega 10e, posterior al módulo 14, «desde 10e», y lo de la entrega 10f, «desde 10f») y está en el Apéndice D. **Los ejemplos numéricos son inventados.**
>
> **Cómo leerlo.** Está escrito para entenderse sin ser experto, pero sin recortar la precisión. Las cifras y reglas que vienen del código se dan tal cual. Cuando algo no lo pude comprobar, lo digo. Cada decisión se marca como **[Validado]** (respaldada por literatura o un estándar), **[Aproximación]** (razonable, pero con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app, sin estándar detrás). Los problemas encontrados se numeran H-2-n (el número es el del módulo 2, en el que se escribió este capítulo, no el del capítulo) y se detallan en `hallazgos.md`.
>
> **Privacidad.** El seguimiento privado (impulsos, recaídas, libido) se describe solo en su mecánica, sin datos ni ejemplos personales.

---

## 3.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Estado subjetivo** | Lo que solo tú puedes observar: cómo te sientes, qué piensas, qué tan estresado o solo te sientes. No hay sensor que lo mida; hay que preguntarlo. |
| **Autorreporte** | Una respuesta que das tú sobre ti mismo. Es la única forma de medir lo subjetivo, y tiene sesgos conocidos (sección 3.4). |
| **Valencia** | Qué tan **agradable o desagradable** se siente algo. En la app, de 1 (muy desagradable) a 7 (muy agradable). |
| **Activación** | Cuánta **energía o agitación** hay en el cuerpo y la mente, sin importar si es agradable. De 1 (muy baja: agotado) a 7 (muy alta: acelerado). En inglés, *arousal*. |
| **Muestreo de experiencia (ESM)** | Preguntar varias veces al día, en momentos al azar, «¿cómo estás ahora?». En inglés, *experience sampling method*; casi sinónimo de *ecological momentary assessment* (EMA). |
| **Retrospectivo** | Que se responde **después**, recordando («¿cómo estuvo tu día?»). Lo opuesto a «momentáneo» («¿cómo estás ahora?»). |
| **Instrumento validado** | Un cuestionario publicado cuyas preguntas, escala y forma de puntuar se han probado en estudios (miden lo que dicen medir y dan resultados estables). |
| **Ancla** | El texto que acompaña a un número de una escala para que signifique lo mismo cada vez (p. ej., «4 = Neutral: plano»). |
| **Panel** | La tabla de análisis: una fila por día, una columna por variable (capítulo 2). |
| **Día lógico** | El día de la app: empieza a las 05:00 (capítulo 2). Un registro a la 01:00 cuenta para el día anterior. |
| **MNAR** | «Faltante no al azar» (*missing not at random*): cuando la razón por la que falta un dato depende del propio dato. Ejemplo: no llenar el diario precisamente los días malos (Rubin, 1976; sección 3.15). |

---

## 3.2 Mapa: qué se pregunta, cuándo y dónde queda

| Medición | Cuándo se pide | Escala | Dónde se guarda | Variables principales del panel |
|---|---|---|---|---|
| Ánimo ahora (aviso) | 3 veces al día por omisión, al azar entre 10:00 y 21:00 | Cuadrícula 7 × 7 (valencia y activación) + opcionales | `Momento` con tipo `muestreo` | `muestreo_valencia`, `muestreo_activacion` |
| Ánimo ahora (por iniciativa propia) | Cuando tú quieras (botón, widget, mosaico) | Igual | `Momento` con tipo `animo` | entra en `animo_valencia`, `animo_activacion` |
| Ánimo al cerrar el día | Al principio de la hoja de la noche, solo la de hoy (hasta la entrega N4, al final) | Cuadrícula 7 × 7 | `Momento` tipo `animo`, origen `cierre` | `animo_cierre_valencia`, `animo_cierre_activacion` |
| Ánimo del día (diario) | Aviso a las 22:00 por omisión | Valencia 1–7, activación 1–7, dolor 0–5, con anclas | `Dia` | `valencia`, `activacion`, `dolor` |
| Emociones con nombre, pensamiento, con quién | Dentro de cualquier registro de ánimo (opcional) | Listas | `Momento` | `n_<emoción>`, `n_<pensamiento>`, `con_*`, `acompanado_pct` |
| Predicción de mañana | En la hoja de la noche | 1–7 | `Dia` | `prediccion`, `error_prediccion`, `error_prediccion_signo` (desde 10e) |
| «Lectura del día» (flujo, necesidades, evaluación) | En la hoja de la noche, plegada | Opción única | `Dia` | `flow_*`, `sdt_necesidad_*`, `apreciacion_*` |
| Cuestionarios (WHO-5, PSS-4, UCLA-3, TIPI) | Semanal, mensual o anual | Las de cada instrumento | `Respuesta` | `esc_<instrumento>_<puntaje>` |
| Sueño y sueños | Aviso a las 8:00 por omisión | Calidad 1–5, valencia 1–7, ejes de contenido | `Dia` | `sueno_calidad`, `sueno_recordado`, `sueno_<categoría>`… |
| Presencias (estuve / pensé / mensajes) | Al revisar el diario | 3 estados | `Dia.presencias` | `personas_vistas`, `vi_*`, `penso_en_*` (capítulo 7) |
| Brújula política | Una vez al año | 25 afirmaciones de 1 a 5 | `Config.politica` | `pol_<eje>` |
| Perfil («Conocerte») | Cuando cambie algo de tu vida | Opciones | `Config.perfil` + historial | `vida_*` |
| Prueba de reacción | Una vez al día (sugerida desde las 8:00) | Milisegundos | `Reaccion` | `rt_media` y relacionadas |

La idea general es **medir lo mismo de varias formas** (en el momento, al final del día, cada semana), porque cada forma tiene sesgos distintos. Las secciones siguientes explican cada una.

---

## 3.3 Los dos ejes del ánimo: valencia y activación

### 3.3.1 Qué es el modelo circumplejo

Russell (1980) propuso que casi todas las emociones se pueden ubicar en un plano con **dos ejes independientes**: qué tan agradable es la experiencia (**valencia**) y cuánta activación implica (**activación**). Las emociones con nombre quedan como puntos alrededor de un círculo: la *alegría* es agradable y algo activada; la *calma*, agradable y poco activada; la *ansiedad*, desagradable y muy activada; la *tristeza*, desagradable y poco activada (Figura 3.1, derecha). Por eso, se llama **circumplejo** (dispuesto en círculo). **[Validado]**: es uno de los modelos de afecto más usados en psicología, aunque no el único (hay modelos con más dimensiones y modelos de emociones discretas; la app usa ambos, sección 3.6).

**Por qué importan dos ejes y no uno.** Con una sola pregunta («¿cómo te sientes, de mal a bien?») se confunden estados muy distintos: «tranquilo» y «emocionado» son ambos agradables, pero el primero tiene poca energía y el segundo mucha. Separar la activación permite ver, por ejemplo, si después de dormir poco no estás «peor», sino «más apagado».

### 3.3.2 La cuadrícula de la app

Para el ánimo **en el momento**, la app usa una **cuadrícula de 7 × 7** (`CuadriculaAfecto`, en `ui/Selectores.kt`): **un solo toque** da dos números. La columna tocada es la valencia (1 a la izquierda, 7 a la derecha) y la fila es la activación (1 abajo, 7 arriba). Los ejes se cruzan en el centro (4, 4 = neutral), y las esquinas llevan cuatro palabras guía: «tenso, estresado» (arriba a la izquierda), «emocionado, alegre» (arriba a la derecha), «triste, apagado» (abajo a la izquierda) y «tranquilo, relajado» (abajo a la derecha). El color de cada celda va de un extremo a otro según la valencia y es más intenso cuanto mayor es la activación.

*Cómo convierte el toque en números (exacto):* si el toque cae en la posición horizontal `x` de un cuadro de ancho `W`, la valencia es `⌊7·x/W⌋ + 1` (limitada a 1–7); si cae en la altura `y` (medida desde arriba) de un cuadro de alto `H`, la activación es `7 − ⌊7·y/H⌋` (limitada a 1–7).

**Base científica.** Es una versión de la **Affect Grid** (Russell, Weiss y Mendelsohn, 1989), una escala de un solo reactivo para agrado y activación. La publicación original usa una cuadrícula más fina (hasta donde sé, de 9 × 9; no pude comprobar el tamaño en la fuente durante esta sesión). **[Aproximación]**: la idea está validada, pero el formato 7 × 7, las palabras de las esquinas y la colocación en un teléfono son de la app, y no hay estudio que valide exactamente esta versión.

![Figura 3.1](figuras/fig07_circumplejo.png)

*Figura 3.1. Izquierda: la cuadrícula de la app con 40 registros sintéticos. Derecha: la idea del circumplejo, con posiciones aproximadas. La app **no** coloca sus emociones en el círculo; la figura solo ilustra el modelo. Script: `figuras/fig07_circumplejo.py`.*

### 3.3.3 La escala del día (1 a 7 con anclas)

Para el ánimo **del día**, la app no usa la cuadrícula, sino **dos filas de números del 1 al 7** con un texto por número (`Catalogo.ANCLAS`):

| | Valencia («qué tan agradable fue el día») | Activación («tu nivel de energía») |
|---|---|---|
| 1 | Muy desagradable: angustia, desesperanza | Muy baja: agotado, apagado |
| 2 | Desagradable: tristeza o frustración marcada | Baja: cansado, lento |
| 3 | Algo desagradable: incomodidad leve | Algo baja: relajado, en calma |
| 4 | Neutral: plano | Neutral |
| 5 | Algo agradable: contento | Algo alta: alerta, despierto |
| 6 | Agradable: disfrute claro | Alta: activo, con impulso |
| 7 | Muy agradable: entusiasmo pleno | Muy alta: acelerado, inquieto |

Además, pide **dolor o molestia física de 0 a 5**, con anclas por **interferencia** («1 = lo noto, no interfiere» … «5 = no puedo funcionar normal»).

**Por qué 1 a 7.** Una escala con un punto medio (4) permite decir «neutral», y siete opciones dan más detalle que cinco sin volverse difíciles de distinguir. Las **anclas** existen para que un «5» signifique lo mismo hoy que dentro de dos años: sin texto, la gente tiende a «reajustar» lo que significa cada número con el tiempo. **[Aproximación]**: es una práctica común y razonable; las anclas concretas son de la app y no están validadas como instrumento.

**Detalle a saber.** Las anclas de activación mezclan un poco de valencia: «3 = relajado, en calma» suena agradable y «7 = acelerado, inquieto» suena algo desagradable. En el circumplejo puro, la activación no debería sugerir agrado ni desagrado. Además, hasta la entrega N4 la fila de activación usaba **la misma paleta de color** que la de valencia (`Tonos.divergente`, del color «bajo» al «alto»), lo que podía insinuar que poca energía es «peor»; desde N4 usa una paleta de una sola dirección (sección 3.7.1). Las anclas no cambiaron. Es un efecto pequeño y no hay forma de saber cuánto influye (parte de H-2-11).

### 3.3.4 Las dos escalas no son la misma medida

La «valencia» del día y la del momento **comparten nombre y rango (1 a 7) pero no son el mismo instrumento**: una es una fila de números con anclas y pregunta por todo el día; la otra es una cuadrícula sin anclas por número y pregunta por *ahora*. Un 5 en una no equivale necesariamente a un 5 en la otra. Compararlas directamente («mi promedio de avisos es 5.1 y mi día fue 4») mezcla dos cosas. La sección siguiente explica por qué esa diferencia, además, es esperable.

---

## 3.4 Tres formas de responder «¿cómo estuve?»

Conner y Barrett (2012) distinguen tres «yo» que responden cuestionarios: el **yo que experimenta** (cómo te sientes *ahora*), el **yo que recuerda** (cómo *recuerdas* que te sentiste) y el **yo que cree** (cómo *crees* que eres en general). Robinson y Clore (2002) mostraron que, cuanto más lejos está el momento que se reporta, más se apoya la respuesta en **creencias generales** («los lunes siempre son malos») y menos en la experiencia concreta. **[Validado]**.

La app mide las tres:

| «Yo» | Qué lo mide en la app | Variable |
|---|---|---|
| El que experimenta | Avisos al azar y registros propios (cuadrícula) | `muestreo_valencia` (solo avisos), `animo_valencia` (todos) |
| El que recuerda | Diario de la noche («qué tan agradable fue el día») | `valencia` |
| El que cree | Cuestionarios semanales/mensuales/anuales | `esc_*` |

Y una cuarta, intermedia: el **cierre del día** («cómo estás ahora», al abrir la hoja de la noche; hasta la entrega N4, al terminarla) es momentáneo, pero siempre al final del día.

**Por qué el recuerdo no es un promedio.** Al evaluar un periodo, la gente pesa de más el **momento más intenso** y **cómo terminó**, y casi ignora la duración (regla «pico-final»; Kahneman et al., 1993). Así, un día bueno con un final malo puede recordarse como peor que su promedio.

**Ejemplo (inventado).** Un martes respondes tres avisos: 6, 5 y 6 (promedio 5.67). A las 21:30 tienes una discusión fuerte. En el diario de las 23:00 pones **3**. El cierre del día lo marcas en 2. Las tres cifras son «verdad», pero de cosas distintas: el promedio de avisos refleja **la mayor parte del día**; el diario, **el día tal como quedó en tu memoria**; el cierre, **cómo estabas al sentarte a cerrar el día**. Ninguna es la «correcta»; por eso se guardan las tres.

**Cómo se calcula cada una en el panel** (`Estadistica.panel`):

- `muestreo_valencia` = promedio de la valencia de los registros de tipo `muestreo` de ese día lógico; `muestreo_activacion`, igual con la activación.
- `animo_valencia` = promedio de **todos** los registros de ánimo del día (`muestreo` + `animo`, incluido el cierre).
- `animo_cierre_valencia` = la valencia del **último** cierre del día (si lo cambiaste varias veces, el último).
- Desde la entrega 10c, un cierre guardado **otro día** que el que cierra (el día lógico de su hora no es su fecha) no cuenta en ninguna de estas variables (`Estadistica.cierreFuera`; sección 3.7.5).
- `valencia` = el número del diario.

La app marca como «obvias» las relaciones entre estas variables (por ejemplo, `valencia` con `muestreo_valencia`), para no presentarlas como descubrimientos (capítulo 6).

**Un detalle de presentación que podía influir (H-2-11).** Hasta la entrega N4, mientras llenabas la valencia del día en la hoja de la noche, debajo aparecían los valores de los avisos de ese día («avisos: 10:56→6 14:55→5…»). Ver esos números antes de calificar el día puede **anclar** (arrastrar) tu respuesta hacia ellos, y así las dos medidas se parecen más de lo que se parecerían por sí solas. Desde N4 la línea solo aparece **después** de elegir la valencia (`PantallaNoche`); todavía puede influir si la corriges después de verla. **[Diseño propio]**.

---

## 3.5 El muestreo de experiencia: cómo y cuándo pregunta la app

### 3.5.1 La idea

Si solo preguntas al final del día, obtienes el «yo que recuerda», con todos sus sesgos. Si preguntas **varias veces al día, en momentos que no eliges tú**, obtienes muestras del «yo que experimenta». Esa es la idea del muestreo de experiencia (Csikszentmihalyi y Larson, 1987; Shiffman et al., 2008). **[Validado]**.

Dos detalles importan: que las horas sean **al azar** (si siempre preguntas a la misma hora, solo conoces esa hora) y que la persona **no las elija** (si solo registras cuando te pasa algo, solo conoces los momentos en que te pasa algo).

### 3.5.2 Cómo elige las horas (exacto)

La función `Programador.planDelDia` (`avisos/Trabajo.kt`) arma el plan de cada día así:

1. Toma la **ventana** configurada: por omisión de **10:00 a 21:00** (`ventana_inicio = 10`, `ventana_fin = 21`) y el número de avisos: por omisión **3** (`muestreos_por_dia`, ajustable de 0 a 6).
2. Divide la ventana en **n tramos iguales**: tramo = (fin − inicio) / n.
3. En cada tramo deja un **margen** a cada lado: margen = mín(45 minutos, tramo / 4).
4. Elige una hora **al azar** (distribución uniforme) entre «inicio del tramo + margen» y «fin del tramo − margen».

El generador al azar usa como semilla la fecha (`Random(fecha.hashCode())`), así que el plan de un mismo día es siempre el mismo aunque se recalcule. Esto se llama **muestreo aleatorio estratificado** (al azar, pero dentro de bloques): garantiza que haya un aviso en la mañana, otro en la tarde y otro en la noche, y a la vez que no sean predecibles. **[Validado]** como diseño; es el esquema habitual en estudios de muestreo de experiencia.

**Ejemplo con los valores por omisión.** La ventana dura 660 minutos; con 3 avisos, cada tramo dura 220 minutos y el margen es mín(45, 55) = **45 minutos**. Entonces:

| Tramo | Va de | Hora posible del aviso |
|---|---|---|
| 1 | 10:00 a 13:40 | 10:45 a 12:55 |
| 2 | 13:40 a 17:20 | 14:25 a 16:35 |
| 3 | 17:20 a 21:00 | 18:05 a 20:15 |

Dos avisos seguidos quedan separados al menos **2 × mín(45 min, tramo/4)**: el margen del final de un tramo más el del principio del siguiente. Con los valores por omisión, **90 minutos**.

**La separación mínima no siempre es de 90 minutos (H-2-7).** Solo se cumple si cada tramo dura **3 horas o más** (porque el margen se reduce a tramo/4 cuando el tramo es corto). Con la misma ventana y **4** avisos, el tramo es de 165 minutos, el margen de 41.25 y la separación mínima de **82.5 minutos**; con **6** avisos, el tramo es de 110 minutos y la separación mínima de **55 minutos**. No es un error de cálculo. Hasta la entrega 10c el comentario del código decía «separadas al menos 90 minutos»; ahora dice la regla exacta, con ejemplos (`Programador.planDelDia`). Gravedad baja.

### 3.5.3 Cómo llega el aviso (y cuándo se pierde)

La app no programa una alarma exacta para cada aviso: lo revisa en el **pulso** de cada ~15 minutos (capítulo 4). En cada pulso, para cada hora del plan que ya pasó y no se ha procesado:

- la marca como «ya procesada»;
- **solo si han pasado menos de 50 minutos** desde la hora planeada, muestra la notificación «¿Cómo te sientes ahora?»;
- la notificación **se borra sola a los 60 minutos** de mostrarse (`setTimeoutAfter`).

Si respondes tocando la notificación, el registro se guarda como tipo **`muestreo`**, con la hora planeada en `aviso_ts`. La exportación calcula `latencia_min` = minutos entre la hora planeada y tu respuesta. Si en vez de eso abres la app por tu cuenta, el registro se guarda como **`animo`** (iniciativa propia), aunque fuera para contestar el aviso.

**Lo que queda registrado desde el esquema 13 (P-10).** Cada pulso anota en la colección `avisos`, un documento por día del plan (`AvisosDia`):
- **`planeados`**: cuántos avisos podían sonar ese día. Si cambias los ajustes a mitad del día, el plan se rehace. Entonces se suman los que ya vencieron y los del plan nuevo que todavía pueden sonar; los de un turno ya usado no vuelven a sonar (H-9-11).
- **`vencidos`**: los avisos cuya hora ya pasó y que el pulso ya procesó.
- **`mostrados`**: los que además salieron en pantalla, es decir, con menos de 50 minutos de retraso y con permiso de notificaciones.

Las horas se guardan en el mismo formato que `aviso_ts`, para unir cada aviso con su respuesta. Con los avisos apagados no se anota nada, y el día queda **sin dato**, no en 0.

En el panel salen cuatro variables (`AvisosAnimo.cifras`):
- `avisos_planeados`;
- `avisos_mostrados`;
- `avisos_respondidos`: los mostrados que tienen un registro `muestreo` con esa hora, tenga o no valencia;
- **`tasa_respuesta`** = respondidos ÷ mostrados.

Se marcan como familia «obvia» entre sí y con `muestreo_valencia`, `muestreo_activacion` y `n_muestreos` (capítulo 6). En la exportación, `n_muestreos` cuenta ahora **todos** los avisos respondidos, con o sin valencia: la misma regla que el diccionario, Explorar y los widgets (H-9-10).

*Ejemplo (inventado):* 4 avisos planeados; 3 vencen y 2 se muestran (el tercero llegó 70 minutos tarde porque el teléfono dormía); respondes uno de los mostrados. Quedan planeados 4, mostrados 2, respondidos 1, tasa 0.5. Los días anteriores al cambio quedan vacíos: reconstruirlos no es fiable. **[Validado]** el propósito (la tasa de respuesta es la primera cifra de un estudio de muestreo de experiencia); **[Diseño propio]** la forma de contarla.

También cambió la tarjeta «N avisos de ánimo sin contestar» de Hoy (H-9-9). Antes restaba registros a avisos planeados, y al tocarla la respuesta se guardaba como registro propio (`animo`), así que la tarjeta no se apagaba. Ahora cuenta los avisos sin respuesta por su hora (`AvisosAnimo.sinContestar`) y abre la captura con la hora del más antiguo. La respuesta queda como `muestreo` de ese aviso, con su `latencia_min`.

**Qué no quedaba registrado hasta el esquema 12 (H-2-3).** Si el teléfono estuvo dormido y el pulso llegó más de 50 minutos tarde, el aviso **se marcaba como hecho pero nunca se mostraba**, sin dejar rastro (hoy sigue sin mostrarse, pero queda en `vencidos`). Tampoco se guardaba, como dato del día, **cuántos avisos se mostraron**: el plan se borra a los 3 días y las marcas internas a los 40. La pantalla de Hoy sí contaba «avisos sin contestar» del día en curso, pero ese número no llegaba al panel. Consecuencia, para los días anteriores al esquema 13: no se puede calcular la **tasa de respuesta** histórica (avisos respondidos / avisos mostrados), que es la primera cifra que se reporta en un estudio de muestreo de experiencia, ni saber si los avisos sin respuesta se concentraban en ciertos días u horas (Rintala et al., 2019, combinando datos de varios estudios de este tipo, encontraron una respuesta promedio de 78 %, más baja temprano en la mañana y que bajaba con los días de estudio). Gravedad media.

![Figura 3.2](figuras/fig08_dia_muestreo.png)

*Figura 3.2. Un día sintético con los valores por omisión: tres avisos dentro de sus franjas posibles, una respuesta rápida, un aviso que caduca y los demás registros del día. Script: `figuras/fig08_dia_muestreo.py` (replica las reglas de `planDelDia`, pero no el generador al azar de Kotlin, así que las horas exactas difieren).*

### 3.5.4 Por qué se separan avisos y registros propios

Cuando tú decides registrar, sueles hacerlo **porque pasó algo** (una alegría, una preocupación). Eso es un muestreo **por evento**, válido para estudiar esos eventos, pero sesgado si se usa como «cómo estoy en general». Por eso, la variable más limpia del ánimo momentáneo es `muestreo_valencia` (solo avisos), y `animo_valencia` (todo junto) mezcla los dos tipos. **[Validado]** el principio.

Ese mismo sesgo afecta al **mapa emocional de lugares** (pantalla Lugares): colorea cada lugar según la valencia de **todos** los registros de ánimo hechos ahí, avisos y propios, contra tu promedio general, con toda tu historia de ánimo (los puntos grises del mapa sí son solo de los últimos 60 días). Hasta la entrega 10c, el mapa y la lista de la misma pantalla usaban criterios distintos para decidir si un registro «es de ese lugar» (el mapa, solo si quedó asignado al lugar; la lista, también si estaba dentro del radio), y el texto decía «últimos 60 días» aunque las medias usaban toda la historia. Ahora los dos usan el mismo criterio (`enLugar`: asignado al lugar **o** a una distancia menor o igual que su radio) y el texto dice qué es de 60 días y qué no. En R, `vm_mapa_animo` les ponía a los registros sin valencia la del diario; desde 10a usa solo registros con su propia valencia. (H-2-15, resuelto en 10a y 10c, D.10 y D.74.)

---

## 3.6 Emociones con nombre, pensamientos y compañía

### 3.6.1 Qué se puede marcar

En cualquier registro de ánimo, después del toque en la cuadrícula, puedes marcar (todo opcional):

- **Qué sientes** (varias a la vez), de una lista de 47 emociones (`Catalogo.SENTIMIENTOS`; 25 hasta el esquema 16) agrupadas por tono y con encabezado en la pantalla:
  - **18 agradables:** alegría, calma, gratitud, entusiasmo, orgullo, cariño, alivio, diversión, esperanza, amor, satisfacción, curiosidad, inspiración, seguridad en mí, admiración, motivación, conectado, asombro.
  - **23 desagradables:** estrés, ansiedad, frustración, tristeza, enojo, miedo, soledad, aburrimiento, culpa, vergüenza, celos, abrumado, cansancio, decepción, impotencia, dolido o rechazado, inseguridad, envidia, irritación, vacío o apatía, desesperanza, arrepentimiento, inquietud.
  - **6 mixtas:** nostalgia, confusión, sorpresa, anhelo, sentimientos encontrados, vulnerable.

  Dentro de cada tono salen primero las que más usas. Hay dos opciones especiales: **«otro…»** (escribes cuál, con un toque para las que ya escribiste antes) y **«no sé»**. Elegir «no sé» quita las demás; elegir otra quita «no sé». Desde el esquema 17 el selector es el mismo en el registro rápido y al corregir un momento (`SelectorEmociones`); antes, al corregir no estaba «otro» y «no sé» no quitaba las demás.
- **Cuánto de cada emoción** (opcional, esquema 17): un poco (1), bastante (2) o mucho (3), en `Momento.intensidades`. Sin respuesta, no hay intensidad (no se asume ninguna).
- **En qué piensas** (varias desde el esquema 17; antes, una): en una persona, preocupación, idea, recuerdo, plan o pendiente, dándole vueltas, autocrítica, antojo o ganas de algo, en el trabajo o la escuela, en mi cuerpo o salud, imaginando o fantaseando, en nada en particular. Las tres que se parecen llevan una línea que las distingue: preocupación es miedo a lo que viene; dándole vueltas, repetir lo que ya pasó; autocrítica, juzgarte a ti. Los registros de antes se leen con `Catalogo.pensamientosDe` (el único pensamiento, o el tipo de momento viejo).
- **Una nota** con @personas y #temas.
- **Con quién estás** (varias): solo, pareja, amigos, familia, hijos, compañeros de trabajo o escuela, desconocidos, mascota, por mensaje o llamada. «Solo» excluye a quien está contigo en persona, pero puede ir con «por mensaje o llamada». Desde el esquema 17, marcar solo «con quién» ya no crea un registro de ánimo: se guarda junto con lo primero que digas del ánimo (la cuadrícula, una emoción, un pensamiento o el texto).

### 3.6.2 Por qué emociones con nombre además de los dos ejes

Los dos ejes resumen bien, pero pierden información: «enojo» y «miedo» caen en la misma zona (desagradable, activado) y son experiencias distintas. Poder **nombrar con precisión** lo que sientes, llamado *diferenciación* o *granularidad emocional*, se asocia con manejar mejor las emociones desagradables (Kashdan, Barrett y McKnight, 2015). **[Validado]** el concepto. La app no calcula un índice de granularidad (eso requiere métodos estadísticos específicos); solo guarda los nombres.

**Por qué «no sé» es un dato.** No poder nombrar lo que sientes también es información (puede indicar baja diferenciación en ese momento, o un estado mezclado). Por eso, se guarda como emoción propia (`no_se`) y produce su propia variable `n_no_se`. **[Diseño propio]**, razonable.

**Asimetría de la lista.** Hay más emociones desagradables (23) que agradables (18); hasta el esquema 16 eran 13 y 9. Es habitual (el vocabulario emocional suele tener más palabras negativas), pero significa que **los conteos no son comparables entre sí**: que marques más emociones negativas que positivas puede deberse en parte a que hay más para elegir. **[Diseño propio]**.

### 3.6.3 Cómo entran al panel

- `n_<emoción>`: cuántos registros de ese día incluyen esa emoción (`n_alegria`, `n_estres`, `n_no_se`…). Solo se crean columnas para emociones que hayas usado al menos una vez (la de «otro» no genera columna).
- `n_<pensamiento>`: igual con los pensamientos; desde el esquema 17, un registro con varios pensamientos cuenta en cada uno.
- **Las emociones y pensamientos nuevos del esquema 17** solo tienen columna desde que se usan por primera vez. Antes de existir no se podían marcar, así que su 0 en días viejos no significa «no lo sentí». **[Diseño propio]**. La intensidad (1–3) no entra al panel: se exporta (`vm_momentos.csv:intensidades`) y, desde el esquema 18 (D.146), se muestra junto a cada emoción en las pantallas («Tristeza (mucho)», `Catalogo.tituloDetallado`) y en los reportes («2 veces «mucho»», `Periodo.animoDe`); en R se resume con `vm_emociones()` (Apéndice C). Ninguno de esos usos la convierte en número del día ni la promedia con la valencia.
- `registros_animo`: cuántos registros de ánimo hiciste ese día.
- **Relleno con cero:** desde la fecha de tu **primer** registro de ánimo, los días que tienen documento de diario y no tienen registros cuentan como **0** en estas variables (no como «sin dato»). **[Diseño propio]**: es razonable si se asume que siempre registras cuando sientes algo, cosa que no es cierta en un día sin registros por olvido (ver H-1-1 sobre el criterio de rellenar con cero).
- Con quién (solo con los registros **de aviso** que tienen compañía marcada): `acompanado_pct` (fracción de avisos en compañía), `con_pareja`, `con_amigos`… (fracción de avisos con cada tipo) y `valencia_acompanado` / `valencia_solo` (promedio de valencia en cada caso).

**Compatibilidad con los registros viejos.** Antes existían «tipos de momento» (tristeza, alegría, pensando en…). La función `Catalogo.emocionesDe` traduce los viejos al formato nuevo (un momento viejo de tipo «tristeza» cuenta como la emoción tristeza). En el panel la serie es continua. **En la exportación `vm_dias.csv` no**: sus columnas `m_<tipo>` siguen contando solo los tipos viejos, así que desde el cambio valen 0 aunque registres esas emociones (H-2-13, resuelto en parte; el diccionario de la v90 lo advierte: «registros de antes de unir ánimo y momentos»).

### 3.6.4 Nombrar puede cambiar lo que sientes

Ponerle palabras a una emoción (*affect labeling*) puede reducir su intensidad: en un estudio de laboratorio, nombrar la emoción de una cara redujo la respuesta de la amígdala, una zona del cerebro relacionada con la reacción emocional (Lieberman et al., 2007). Es decir, **el acto de registrar puede cambiar un poco lo registrado**. Esto se retoma en la sección 3.14 (reactividad).

---

## 3.7 El diario de la noche

### 3.7.1 Qué contiene

La «hoja de la noche» (`PantallaNoche`, en `ui/pantallas/Dia.kt`; en la app se llama **«Llenar el día»** en todas partes desde la entrega N6 de la revisión de navegación: en Hoy, en ⊕, en el acceso directo, en el widget y en el aviso de la noche) reúne en una página, **en este orden fijo** desde la entrega N4 de la revisión de navegación (`VERSION_NOCHE = 2`):

1. **«Cómo estás ahora»** (el cierre, sección 3.7.5), solo en la hoja del día de hoy.
2. **Ánimo y cuerpo** del día: valencia, activación y dolor.
3. **Predicción** de mañana.
4. **Cómo fue el día** (el contexto del día completo: trabajo, gym, viaje, enfermo, académico, ocio inmersivo, naturaleza, amigos, cita o tiempo en pareja, familia y, desde el esquema 17, casa o descanso, fiesta, trámites o pendientes; varios o ninguno), más un «otro» escrito (`ctx_otro`, solo se exporta).
5. **Hábitos** (desde el esquema 17, «sí» o «no» por hábito, y tocar otra vez la misma respuesta la quita; antes, casillas; sin el hábito ligado a las recaídas) y **suplementos de este día**.

La barra fija de abajo cuenta lo mismo que pide «Sellar el día»: el ánimo y el diario. La predicción y los hábitos aparecen como «extra». Los hábitos cuentan como hechos cuando todos tienen respuesta, sea «sí» o «no». Antes contaba cuatro pasos y los hábitos solo estaban hechos si todos eran «sí».
6. **Diario** (texto libre con @personas y #temas) y con quién estuviste.
7. Plegado, como opcional: la «lectura del día», lo mejor y lo peor, hitos, lo que leíste o viste, la foto del día y «privado de este día →».
8. Sellar.

«Solo lo que falta» oculta lo ya contestado, pero no cambia el orden.

**Por qué este orden.** No hay un orden sin efectos: cada pregunta puede cambiar las que vienen después. Se eligió así:
- **El ánimo del momento va primero.** Recordar a propósito lo que te pasó cambia el ánimo; de hecho, es una forma habitual de inducir un ánimo en el laboratorio (Westermann et al., 1996). Y ponerle nombre a lo que sientes al escribir baja la intensidad de la emoción (Lieberman et al., 2007). Con el cierre al final, medía cómo quedaste después de repasar y escribir el día, no cómo estabas.
- **El juicio general del día va antes de las preguntas específicas.** Si primero se pregunta algo concreto («¿cumpliste tus hábitos?») y luego algo general («¿qué tan agradable fue el día?»), lo concreto se mete en el juicio general o se excluye de él, y cambia la relación entre las dos respuestas (Schwarz, Strack y Mai, 1991). El panel cruza justo la valencia con los hábitos.
- **El costo.** Con el ánimo del momento primero, ese ánimo puede teñir el juicio del día: es el efecto de usar el ánimo como información (Schwarz y Clore, 1983). Se aceptó porque el cierre es el dato que se quiere proteger (momentáneo, comparable entre días), mientras que el juicio del día es retrospectivo y el ánimo del momento lo tiñe en cualquier orden. En ese mismo trabajo, cuando la persona se fija en su ánimo actual, este pesa menos en el juicio.

Es una **[Aproximación]**: viene de estudios de encuestas y de laboratorio, no de una prueba con esta app. Para verlo en tus datos, cada día guarda `version_noche` (1 = el orden de antes: hábitos, ánimo, contexto, diario, obras, foto, predicción, lo opcional y el cierre al final; 2 = el nuevo). Así se pueden comparar los dos tramos en R.

Además, desde `VERSION_NOCHE = 2`, la línea con los valores de tus avisos de ánimo de ese día solo aparece **después** de elegir la valencia, y la activación usa una paleta de una sola dirección (`Escala(secuencial = true)`), para que «menos energía» no se lea como «peor» (H-2-11).

Desde la entrega N2 de la revisión de navegación hay también una sección **«Suplementos de este día»**: marca o quita la toma de ese día (a la hora habitual o a la de ahora). Y si faltan días, «Ponerte al día» abre la hoja en el primero que falta y, al sellar, pasa al siguiente (hasta 7 días hacia atrás). Lo que se llena así queda como `retroactivo` (sección 3.7.4), y la diferencia entre `ts` y `ts_creado` dice cuánto después se anotó.

Se guarda sola mientras escribes (cada 0.7 s sin cambios). Si tiene **valencia y texto del diario**, se puede «sellar» el día; sellar no cambia los datos, es solo un cierre simbólico.

### 3.7.2 La «lectura del día»: tres preguntas de marcos científicos

Son tres preguntas de opción única (`Catalogo.CIENTIFICAS`), cada una inspirada en una teoría:

**Flujo (Csikszentmihalyi).** «¿El reto igualó tu habilidad?» Opciones: **flujo** (concentración total), **ansiedad** (el reto te rebasó), **aburrimiento** (muy por debajo de tu capacidad), **apatía** (ni reto ni interés) y «no aplica». Desde el esquema 17 la pantalla dice «Me rebasó (ansiedad)» y «Me quedó chico (aburrimiento)», porque en este modelo son estados de reto contra habilidad, no las emociones que llevan el mismo nombre en los registros de ánimo. Los ids (`ansiedad`, `aburrimiento`) y las columnas no cambian. Corresponde al **modelo de cuadrantes**: cruzar «reto alto/bajo» con «habilidad alta/baja» da cuatro estados, y el flujo aparece cuando ambos son altos (Csikszentmihalyi y LeFevre, 1989). **[Aproximación]**: en los estudios se mide reto y habilidad por separado y se calcula el cuadrante; aquí se pide elegir directamente el estado, y referido al día completo (a «la actividad más exigente del día»), no a un momento.

**Necesidades psicológicas (Deci y Ryan).** «¿Qué necesidad se satisfizo más?»: **autonomía** (las decisiones fueron tuyas), **competencia** (algo se te dio bien), **relación** (te sentiste conectado), **ninguna** o **las tres**. Se basa en la teoría de la autodeterminación, que propone esas tres necesidades básicas (Deci y Ryan, 2000). **[Aproximación]**: las escalas validadas miden cada necesidad por separado con varios reactivos; aquí es una sola elección.

**Evaluación del estrés (Lazarus).** «¿Cómo evaluaste la exigencia del día?»: **reto** (exigente pero motivante), **amenaza** (exigente y temiste fallar), **pérdida** (algo ya salió mal) o **rutina** (baja exigencia); desde el esquema 17 también «no aplica», que deja las columnas en 0 igual que en flujo y no cuenta en el reporte. Corresponde a la «evaluación primaria» de Lazarus y Folkman (1984), que distingue daño/pérdida, amenaza y reto. **[Aproximación]**: mismo comentario, es una sola elección del día.

**Cómo entran al panel.** Cada opción se convierte en una columna de 0/1 (`flow_flujo`, `flow_ansiedad`, `sdt_necesidad_autonomia`…), con valor **vacío** si no respondiste. Dos consecuencias del código:

- Elegir «**no aplica**» deja todas las columnas de flujo en 0 (no en vacío), igual que si hubieras elegido algo que no existe. Se puede distinguir de «no respondí», pero no hay columna propia para «no aplica».
- Elegir «**las tres**» pone `sdt_necesidad_las_tres = 1` y, desde la entrega 10c, también `sdt_necesidad_autonomia`, `sdt_necesidad_competencia` y `sdt_necesidad_relacion` en **1** (antes quedaban en 0 aunque las tres se hubieran satisfecho). Así, cada necesidad por separado cuenta bien sus días. (Parte de H-2-4, resuelta en 10c, D.63.)

Estas preguntas están **plegadas por omisión**, así que se responden menos y probablemente no al azar (más en días con ganas de escribir). **[Diseño propio]**.

### 3.7.3 Lo mejor, lo peor, hitos, medicamentos, contextos y hábitos

- **Lo mejor y lo peor** del día: una línea cada uno, texto libre. No entran al panel como números.
- **Hitos**: etiquetas cortas de cosas que no pasan todos los días; `hitos_n` cuenta cuántas.
- **Medicamentos**: desde el módulo 10a son privados y se anotan en la pantalla Privado, no en el diario de la noche (al final hay un enlace «privado de este día»). Son etiquetas; `medicamentos_n` cuenta cuántas desde el primer día en que anotaste alguno y solo viaja a R.
- **Contextos** (`ctx_*`) y **hábitos** (`hab_*`): 1 si lo marcaste. **Ojo (H-2-4):** los contextos valen **0 en todos los días con diario**, también si no abriste esa sección: es la convención declarada en la pantalla («marca lo que aplique»), y se mantiene. Los hábitos valían 0 incluso **antes de crear el hábito**; desde la entrega 10c, antes de `Habito.desde` (el día en que lo creaste) quedan vacíos, salvo que los hayas marcado (D.63). Los hábitos creados antes de que existiera esa fecha siguen valiendo 0 desde el principio.
- **Hábitos con «sí / no / sin contestar» (esquema 17).** Al contestar un hábito en la hoja de la noche, el día queda marcado (`habitos_tri`). En esos días, «no» vale 0 y un hábito **sin respuesta queda vacío**, no 0. En los días de antes (sin la marca), sin marca sigue valiendo 0. *Ejemplo con datos inventados:* un día con «Leer: sí» y «Meditar» sin tocar da `hab_leer` = 1 y `hab_meditar` vacío; el mismo día sin la marca daba `hab_meditar` = 0. Una recaída deja el hábito ligado en «no» (0), como antes. Con esto se resuelve en parte H-2-4 para los hábitos; en los contextos, «no lo marqué» y «no pasó» siguen siendo lo mismo (H-1-1).

### 3.7.4 «Retroactivo»: cuándo se llenó el día

Cada documento de día guarda `retroactivo`: verdadero si el día se llenó **después de que terminó** (la regla exacta cambió en el esquema 13; abajo). Sirve para separar lo escrito en el momento de lo escrito de memoria días después, que tiene más sesgo de recuerdo. **[Validado]** el propósito (Stone et al., 2002, mostraron que en diarios en papel mucha gente llena días atrasados de golpe).

**Hasta el esquema 12 se decidía en el primer guardado, no cuando se llenó el ánimo (H-2-2).** `Repo.guardarDia` marcaba el día como retroactivo si, al crear el documento, su fecha era anterior al día lógico de hoy, y la marca ya no cambiaba. Ejemplo (inventado): el martes a las 8:10 tocas la calidad del sueño en el widget. Eso crea el documento del martes con `retroactivo = falso`. El viernes llenas la valencia y el diario del martes de memoria: el día **seguía marcado como no retroactivo**, porque la marca ya existía. Al revés, un día creado retroactivo lo era para siempre. Además, `ts_editado` se sobrescribe en cada cambio, así que tampoco quedaba cuándo se escribió cada parte. Gravedad media: es justo la variable pensada para controlar el sesgo de recuerdo.

**Desde el esquema 13 (P-13)** cada día guarda `ts_valencia`: la hora en que la valencia pasó de vacía a llena, o en que cambió. Con ella, `retroactivo` = el día lógico de `ts_valencia` es posterior a la fecha del día (`Reglas.retroactivo`). En el ejemplo, la valencia del martes se llenó el viernes, así que el martes queda retroactivo aunque el widget lo haya creado el mismo martes. Llenarla el martes a la 1:30 de la madrugada del miércoles no cuenta como retroactivo, porque sigue siendo el día lógico del martes. Corregir la valencia días después también vuelve el día retroactivo: el número nuevo es de memoria. Los días de antes del esquema 13 no tienen `ts_valencia` y conservan su marca vieja, salvo que cambies su valencia. **[Diseño propio]**. Desde 10f (D.109), el día lógico de `ts_valencia` se calcula con la zona horaria que tenía el teléfono en ese instante (`Fechas.zonaEn`; capítulo 4, sección 4.3), no con la de hoy. Importa después de un viaje, porque la marca se vuelve a calcular cada vez que se guarda el día (`Repo.guardarDia`). Ejemplo (inventado): la valencia del martes, llenada a las 09:00 del miércoles en Madrid (UTC+2), es retroactiva. Hasta la v91, si ya de vuelta en UTC−6 guardabas otra vez ese día, la hora se leía como la 01:00 del miércoles, todavía del día lógico del martes, y la marca se perdía. Lo mismo vale para `Estadistica.cierreFuera` (sección 3.7.5), que se calcula en cada panel.

**Un aviso que prometía una marca inexistente (H-2-14).** Si abrías un día futuro, la app decía «se marcará como escrito por adelantado», pero no existía ningún campo que lo registrara. Desde el esquema 14 el día guarda `ts_creado`, así que «escrito por adelantado» se deriva en R: el día lógico de `ts_creado` es anterior a la fecha del día. Desde la entrega 10c el aviso dice lo que pasa de verdad: «Puedes dejar notas; queda guardado cuándo las escribiste».

### 3.7.5 El cierre del día

Al **principio** de la hoja (hasta `VERSION_NOCHE = 1` iba al final; sección 3.7.1) hay una cuadrícula «Cómo estás ahora» (`BloqueCierre`, en `ui/pantallas/Lote2.kt`). Guarda un registro de ánimo con la **hora real** (tipo `animo`, origen `cierre`). La idea es tener **la misma medición, en el mismo punto del día, todos los días**. **[Diseño propio]**, buena idea: es un dato momentáneo comparable entre días, menos afectado por el recuerdo que la valencia del diario.

Dos precisiones (H-2-8):

- El comentario dice «a la misma hora todos los días», pero en realidad es **a la hora en que cierres** el diario, que varía. La hora queda guardada, así que se puede controlar en el análisis.
- Hasta la entrega N4, si llenabas la hoja de **un día pasado**, el cierre se guardaba **con la fecha de ese día pero con la hora de ahora**: `animo_cierre_valencia` del martes podía ser cómo te sentías el viernes. Desde la N4, el cierre solo se ofrece en la hoja del día lógico de hoy (`cierrePermitido(fecha)`, en `data/Modelo.kt`; lo comprueba `EsquemaCatorcePrueba.cierreSoloHoy`). Los cierres tardíos de antes siguen guardados, pero desde la entrega 10c **el panel no los usa**: `Estadistica.cierreFuera(m)` es verdadero cuando el momento es un cierre y el día lógico de su hora (`Fechas.diaLogicoDeMillis(Fechas.millis(ts))`) no es su `fecha`, y esos momentos no entran en `animo_cierre_*`, `animo_valencia`, `animo_activacion` ni `registros_animo`. Así se arreglan también los datos viejos (D.74). Ejemplo: un cierre con fecha martes y hora viernes 09:00 queda fuera; uno con fecha martes y hora martes 21:00 cuenta.

---

## 3.8 La predicción de mañana

En la hoja de la noche: «¿Cómo crees que estará tu ánimo mañana?», de 1 a 7. Aunque la pregunta dice «ánimo», la escala es de agrado: sus anclas (`Catalogo.ANCLAS["prediccion_manana"]`) van de «1 = Muy desagradable» a «7 = Muy agradable», las mismas palabras de la valencia del día pero sin sus descripciones («angustia, desesperanza»…). Por eso se compara con la **valencia** del diario del día siguiente, no con la activación. Al día siguiente el panel calcula:

  `error_prediccion` = | predicción de ayer − valencia de hoy |

y, desde 10e (D.94),

  `error_prediccion_signo` = valencia de hoy − predicción de ayer

(`Estadistica.panel`; las dos solo si existen la predicción de la noche anterior y la valencia de hoy). La segunda va de −6 a +6: **positiva** si el día salió **mejor** de lo que predijiste, **negativa** si salió peor. `error_prediccion` es su valor absoluto.

**Para qué.** Las personas solemos equivocarnos al predecir cómo nos sentiremos: sobrestimamos cuánto y cuánto tiempo nos afectarán los eventos (*pronóstico afectivo*; Wilson y Gilbert, 2005). Medir el error permite ver si con el tiempo te conoces mejor y qué días predices peor. **[Validado]** la idea; **[Diseño propio]** la variable.

**Qué se pierde al usar el valor absoluto (H-2-10).** El valor absoluto quita la dirección del error. Ejemplo (inventado): el lunes predices 6 y el martes resulta 4 → error 2 (fuiste **optimista**). El miércoles predices 3 y el jueves resulta 5 → error 2 (fuiste **pesimista**). Para la variable son iguales, aunque son sesgos opuestos. Si quieres saber si tiendes al optimismo, hace falta también el **error con signo**. En la v90 no existía: había que calcularlo en R con `prediccion` del día anterior y `valencia`. Desde 10e es `error_prediccion_signo` (H-2-10, resuelto). Con el ejemplo: el martes vale 4 − 6 = **−2** (optimista) y el jueves 5 − 3 = **+2** (pesimista). Si su promedio en un mes es −0.8, ese mes tendiste a esperar días mejores de los que tuviste. **[Diseño propio]** la variable, como la otra.

**Cuándo falla.** (a) Hereda la regla de la otra: sin predicción la noche anterior o sin valencia hoy, queda vacía, y los días que faltan no son al azar (sección 3.15). (b) Como se calcula con la valencia de hoy, **se mueve con `valencia` por construcción**: un día bueno tiende a dar un error positivo aunque la predicción no cambie. Por eso `Estadistica.FAMILIAS` la marca como obvia con `prediccion`, `error_prediccion` y `valencia` (desde 10e, también `error_prediccion` con `valencia`): esas parejas no salen en «Las relaciones más marcadas» salvo con «Mostrar también las obvias» (capítulo 2, sección 2.7).

---

## 3.9 Cuestionarios validados

### 3.9.1 Los cuatro instrumentos

Están en `ciencia/Escalas.kt`. Para guardar una respuesta hay que contestar **todos** los reactivos (el botón «Guardar» no se habilita antes), así que no hay puntajes con preguntas en blanco.

**WHO-5 (bienestar), cada 7 días.** Cinco frases («Me he sentido alegre y de buen humor», «…tranquilo y relajado», «…activo y enérgico», «Me he despertado fresco y descansado», «Mi vida diaria ha estado llena de cosas que me interesan»), cada una de 0 (nunca) a 5 (todo el tiempo).

  bruto = suma de las 5 (0–25)  porcentaje = bruto × 4 (0–100)

Referencia: Topp et al. (2015). La app interpreta: **≤ 28** «valor bajo, vale la pena hablarlo con un profesional si se repite»; **≤ 50** «por debajo de lo habitual»; más de 50, «rango habitual o alto». En la literatura, **≤ 50** suele usarse como punto de corte para **tamizaje de depresión** (indica que conviene evaluar más, no que haya depresión); el valor **≤ 28** se cita con frecuencia como indicativo de probable depresión, pero no pude comprobarlo en la fuente primaria durante esta sesión **[sin verificar]**. **Adaptación:** el original pregunta por **las últimas 2 semanas**; la app usa **7 días**. Los puntos de corte se establecieron con el periodo de 2 semanas, así que aplicados a una semana son orientativos.

**PSS-4 (estrés percibido), cada 7 días.** Cuatro preguntas de 0 (nunca) a 4 (muy a menudo): sentirte incapaz de controlar lo importante, seguro de manejar tus problemas (**inversa**), que las cosas te van bien (**inversa**), que las dificultades se acumulan.

  total = p1 + p4 + (4 − p2) + (4 − p3)  (0–16; más alto = más estrés)

Referencia: Cohen, Kamarck y Mermelstein (1983). La app cita además la versión española de Remor (2006), pero **esa validación estudió las versiones de 14 y 10 preguntas, no la de 4**: la versión corta en español que usa la app no está validada en ese artículo (y no comparé sus frases palabra por palabra con la publicada). **Adaptación:** el original pregunta por **el último mes**; la app, por **7 días**. No hay puntos de corte; la app solo propone compararte contigo mismo. **[Validado]** el instrumento; **[Aproximación]** el periodo semanal.

**UCLA-3 (soledad), cada 30 días.** Tres preguntas: falta de compañía, sentirte excluido, sentirte aislado; de 1 (casi nunca) a 3 (a menudo). Total 3–9 (Hughes et al., 2004). La app dice que «6 o más suele usarse como indicador de soledad»; ese punto de corte aparece en estudios que usan esta escala, pero no encontré la fuente que lo establece **[sin verificar]**. La app pregunta «pensando en el último mes». **[Validado]** el instrumento.

**TIPI (personalidad), cada 365 días.** Diez frases («Me veo a mí mismo como una persona… extravertida, entusiasta», «… reservada, callada», etc.), de 1 (muy en desacuerdo) a 7 (muy de acuerdo). Cada rasgo de los cinco grandes es el promedio de una frase directa y una **inversa** (inversa = 8 − respuesta):

| Rasgo | Fórmula |
|---|---|
| Extraversión | (t1 + (8 − t6)) / 2 |
| Amabilidad | ((8 − t2) + t7) / 2 |
| Responsabilidad | (t3 + (8 − t8)) / 2 |
| Estabilidad emocional | ((8 − t4) + t9) / 2 |
| Apertura | (t5 + (8 − t10)) / 2 |

Referencias: Gosling, Rentfrow y Swann (2003); versión española, Romero et al. (2012). Con solo dos frases por rasgo, el TIPI sirve para ver **cambios grandes** con los años, no matices. **[Validado]**. No comparé palabra por palabra las frases de la app con la versión española publicada.

**Ejemplo de puntaje (inventado), PSS-4.** Respuestas: p1 = 2, p2 = 3, p3 = 1, p4 = 2. Total = 2 + 2 + (4 − 3) + (4 − 1) = 2 + 2 + 1 + 3 = **8** de 16.

### 3.9.2 Cuándo se piden

`Escalas.proxima`: la fecha de la última respuesta más la frecuencia (7, 30 o 365 días); si nunca se respondió, hoy. El recordatorio (a las 19:00 por omisión, `escalas_hora`) insiste más con el atraso (`Escalas.horasAviso`, si está encendido `escalas_insistir`, como viene por omisión; si no, avisa una vez al día): el día que toca, **una vez**; con 1 a 3 días de atraso, **dos** (12:00 y la hora configurada); con 4 o más, **tres** (10:00, 15:00 y la configurada o las 20:00, la que sea más tarde).

Que se pidan de forma regular es importante porque estos instrumentos preguntan por un periodo («los últimos 7 días»): si se responden irregularmente, los periodos se enciman o dejan huecos.

### 3.9.3 Cómo se pasan al panel: el problema principal de este capítulo (H-2-1)

Un cuestionario se responde **un día**, pero habla de **un periodo**. Para poder cruzarlo con variables diarias, la app copia el puntaje a varios días (`ciencia/Dinamicas.kt`, sección «Cuestionarios»).

**Cómo era hasta la entrega 10c (H-2-1).**

- **WHO-5:** al día de la respuesta y a los **13 días anteriores** (ventana de 14).
- **PSS-4 y UCLA-3:** al día de la respuesta y a los **29 días anteriores** (ventana de 30).
- Las respuestas se procesaban de la más antigua a la más nueva, y **cada una sobrescribía** lo que había.

Eso tenía dos problemas. Primero, los periodos no coincidían con lo que la app pregunta: WHO-5 y PSS-4 preguntan por **7 días** (adaptación de la propia app) y se copiaban a 14 y 30. Segundo, la sobrescritura hacía que cada día recibiera la respuesta más nueva que lo alcanzaba, no la que habla de él: con un PSS-4 cada semana, un día podía recibir el puntaje de una respuesta dada **hasta 29 días después**. Las correlaciones con variables diarias comparaban semanas distintas, con el «estrés» de un día viniendo del futuro, y las semanas extremas desaparecían. Gravedad **alta**.

**Cómo es ahora (entrega 10c, D.62).** La función pura `Dinamicas.porDiaCuestionario(respuestas, ventana, hoy)` recibe las respuestas (fecha, puntaje) de la más vieja a la más nueva y una ventana igual al periodo que pregunta cada instrumento (`ventanaCuestionario`, igual a `frecuenciaDias` de `Escalas.kt`):

- **WHO-5 y PSS-4:** el día de la respuesta y los **6 anteriores** (ventana de 7).
- **UCLA-3:** el día de la respuesta y los **29 anteriores** (ventana de 30).
- **TIPI:** igual que antes, del día de la respuesta **hacia adelante**, hasta la siguiente respuesta (como mucho un año, y no más allá de hoy).

Si dos respuestas cubren el mismo día, se queda **la primera** que lo cubre (`putIfAbsent`): como se recorren de la más vieja a la más nueva, es la respuesta más cercana **después** de ese día, la que pregunta por él. Un día que ninguna respuesta cubre queda vacío.

**Ejemplo (inventado, Figura 3.3).** Respondes el PSS-4 los días 1, 8, 15, 22 y 29 de marzo, con 4, 6, 9, 5 y 7. ¿Qué guarda el panel el **3 de marzo**?

- *Antes:* las respuestas cuya ventana de 30 días lo cubría eran las del 8, 15, 22 y 29; la última en escribir era la del **29 (7)**. Del 1 al 29 de marzo **todos** los días quedaban con 7, y la semana de estrés alto (9, del 15) desaparecía.
- *Ahora:* el 3 de marzo solo lo cubre la respuesta del 8 (periodo del 2 al 8): **6**. Cada semana queda con su puntaje: 4 (del 23 de febrero al 1 de marzo), 6, 9, 5 y 7; el 30 de marzo, sin respuesta que lo cubra, queda vacío.

Con UCLA-3 (ventana de 30) respondido igual cada semana, el 3 de marzo también recibiría **6**: lo cubren las respuestas del 8, 15, 22 y 29, y gana la primera. Lo comprueba la prueba JVM `CalculosPrueba.cuestionarioPorSemana`. **[Diseño propio]** la regla de asignación; **[Aproximación]** suponer que el puntaje vale igual para cada día del periodo.

![Figura 3.3](figuras/fig10_cuestionarios_ventana.png)

*Figura 3.3. PSS-4 sintético respondido cada semana. Línea discontinua: lo que guardaba el panel hasta la entrega 10c (ventana de 30 días, gana la última respuesta). Línea continua: lo que guarda ahora (los 7 días que pregunta cada respuesta, gana la primera que cubre el día). Script: `figuras/fig10_cuestionarios_ventana.py` (replica las dos versiones de `Dinamicas.kt`).*

La app sí evita un error relacionado: dos puntajes del **mismo** cuestionario (por ejemplo, extraversión y apertura del TIPI) se marcan como relación «obvia», porque salen de las mismas respuestas.

---

## 3.10 La brújula política

Una vez al año (la primera vez, cuando ya hay al menos 14 días con documento de diario), 25 afirmaciones **escritas para la app** (`data/Politica.kt`), 5 por eje, respondidas de 1 (muy en desacuerdo) a 5 (muy de acuerdo). Ejes: economía (igualdad y Estado ↔ mercado y empresa), autoridad (libertad individual ↔ orden y autoridad), valores (cambio ↔ tradición), mundo (apertura ↔ soberanía) y ambiente (proteger el ambiente ↔ crecimiento primero). El orden de las afirmaciones se mezcla, con el mismo orden durante todo el año.

**Cálculo (exacto).** Cada afirmación tiene un **signo**: +1 si estar de acuerdo acerca al polo «positivo» del eje y −1 si al «negativo». Para cada eje:

  posición = Σ (respuesta − 3) × signo × 100 / (2 × número de afirmaciones respondidas)  (−100 a +100)

y se describe (`Politica.lado`) como «muy hacia…» (≤ −60 o ≥ 60), «hacia…» (más de −60 y hasta −20, o desde 20 y menos de 60) o «al centro» (más de −20 y menos de 20). Los bordes son simétricos: ±20 ya es «hacia…» y ±60 ya es «muy hacia…».

**Para qué sirven los signos mezclados.** Hay personas que tienden a estar de acuerdo con todo (*aquiescencia*). Si todas las afirmaciones apuntaran al mismo lado, esa tendencia se confundiría con una postura. Mezclar afirmaciones en ambos sentidos la neutraliza… **si están equilibradas**.

**No lo están del todo (H-2-6).** Con 5 afirmaciones por eje el reparto es 3 contra 2. Si alguien responde «muy de acuerdo» (5) a las 25, cada eje queda en ±2 × 100 / 10 = **±20**, que la app describe como «hacia…» en los **cinco** ejes (hacia igualdad y Estado, hacia orden y autoridad, hacia cambio, hacia soberanía, hacia proteger el ambiente). El comentario del código decía que estar de acuerdo con todo «no empuja hacia ningún lado»; desde la entrega 10c dice que sí empuja un poco (±20) y que, al ser el mismo cada año, afecta poco a compararte contigo mismo. El cálculo no cambió. Con «de acuerdo» (4) en todo, el efecto es ±10 y queda «al centro». Gravedad baja: es un instrumento para compararte contigo mismo año con año, y ese sesgo es constante. **[Diseño propio]**: las afirmaciones no están validadas como escala.

En el panel, cada eje (`pol_<eje>`) vale lo mismo desde el día del test hasta el siguiente (o 400 días).

---

## 3.11 «Conocerte»: el perfil y las etapas de vida

«Conocerte» (`data/Perfiles.kt`, pantalla `Conocer.kt`) pregunta lo grande de tu vida: con quién vives, a quién ves más, pareja, ocupación, horario, cronotipo (de mañana o de noche), hábitos generales, ciudad, transporte, dieta, etc. Cada vez que cambias una respuesta se guarda en un **historial** con la fecha (`perfil_historial`).

Con eso, el panel crea variables de **etapa de vida** (`vida_en_pareja`, `vida_vive_solo`, `vida_trabaja`, `vida_desde_casa`, ciudad…): cada día sabe en qué etapa estabas, para comparar, por ejemplo, tu ánimo cuando vivías solo y cuando no. La función `valorEn(campo, fecha)` decide el valor de un día así: el último cambio **antes o en** esa fecha; si no hay, el valor **«antes»** del primer cambio posterior; si nunca hubo cambios en ese campo, **el valor actual**.

**El valor actual se proyectaba hacia atrás (H-2-5).** Si llenabas el perfil por primera vez (por ejemplo, «en pareja»), **todos los días anteriores**, incluidos años de historia importada, quedaban como «en pareja», porque el primer guardado no dejaba historial. Gravedad media.

**Desde la entrega 10c (D.74)** el primer guardado también deja historial: `Perfiles.cambios(antes, despues)` se aplica aunque el perfil estuviera vacío, y registra cada campo llenado como un cambio de «» a su valor, con la fecha de ese día. `Perfiles.primerGuardado(historial)` reconoce esa fecha: es el primer día del historial si **todos** los cambios de ese día pasaron de vacío a algo. Para los campos llenados ese día, `valorEn` devuelve «sin dato» en las fechas anteriores, y las variables `vida_*` quedan vacías (también `vida_mascota` y `vida_idiomas`, que antes daban 0, y `vida_ciudad_*`). Ejemplo: llenas «Conocerte» por primera vez el 10 de octubre con «en pareja»; `vida_en_pareja` vale 1 desde el 10 de octubre y queda vacía antes. Ese primer llenado tampoco sale en «Antes y después» (capítulo 6), porque no es un cambio de vida.

Lo que no se arregla: los perfiles llenados antes de esta versión no tienen ese primer registro, y sus valores se siguen proyectando hacia atrás. Y si la primera edición de un perfil viejo consistió solo en llenar campos vacíos, `primerGuardado` la toma por un primer guardado y deja sin dato esos campos en los días anteriores. **[Diseño propio]**.

---

## 3.12 Sueño y sueños

### 3.12.1 Cómo se captura

A la hora configurada (**8:00** por omisión, `hora_sueno`), si el día lógico aún no tiene relato del sueño ni la marca «recordé un sueño» (la calidad sola no lo evita), llega el aviso «¿Recuerdas lo que soñaste? Anótalo antes de levantarte; el recuerdo se borra en minutos», que se borra solo a las **3 horas**. El aviso solo se intenta durante las 3 horas siguientes a la hora configurada. La pantalla rápida pone **primero la narración** y después lo demás, precisamente porque el contenido de los sueños se olvida muy rápido al despertar. Desde el esquema 17 la narración ya no se escribe en un cuadro de texto: se toca el «libro de noche» del tema y se escribe a pantalla completa, igual que el diario; el formulario es el mismo en la pantalla rápida y en el Día (`FormularioSueno`, en `ui/CategoriasSueno.kt`). Que el olvido sea cuestión de minutos es la afirmación de la app; es coherente con lo que sé de la literatura sobre el recuerdo de sueños, pero no la verifiqué con una fuente en esta sesión **[sin verificar]**.

Se registra:

- **Calidad del sueño como la sentiste**, de 1 (muy mala) a 5 (muy buena). Es una percepción, distinta de las horas que mide el reloj (capítulo 4). Se guarda en el **día del despertar** (la fecha del calendario, `Fechas.hoyCalendario()`), igual que las horas del reloj. Así, quien despierta a las 04:30 y la anota no la deja en el día anterior. Esto es así desde la entrega N2 de la revisión de navegación; antes usaba el día lógico (H-NAV-7).
- **¿Recordaste un sueño?** (sí/no). Si sí: **valencia del sueño** (1–7), **¿fue pesadilla?** (sí/no: angustia fuerte o te despertó), **qué tan raro fue** (realista / raro pero posible / imposible o surreal), **qué tan claro lo recuerdas** (solo fragmentos / partes claras / casi completo y nítido), **lucidez** (no / parcial / total), **categorías por ejes** y nombres y temas propios (@ y #).

**El «tipo» de antes (hasta el esquema 16).** Antes había una sola pregunta de tipo con cuatro opciones excluyentes: mundano, ansioso o pesadilla, bizarro o surreal, recurrente. Mezclaba tres cosas distintas (qué tan raro, qué tan angustiante y si se repite), obligaba a elegir una aunque el sueño fuera raro *y* angustiante, y «recurrente» repetía la categoría «se repite» de la estructura. Desde el esquema 17 se pregunta por separado: **rareza** (`sueno_rareza`) y **pesadilla** (`sueno_pesadilla`), y «recurrente» queda solo en la estructura. Los días viejos se leen con funciones de compatibilidad (`Catalogo.rarezaDe`, `pesadillaDe`, `categoriasSuenoDe`): mundano → realista; bizarro o surreal → imposible; ansioso o pesadilla → pesadilla = sí (sin rareza); recurrente → categoría `est_recurrente`. *Ejemplo con datos inventados:* un día de 2025 con tipo «recurrente» aparece en el panel con `sueno_est_recurrente` = 1 y sin rareza. Al corregir un día viejo, se pasa a los campos nuevos y el tipo se borra. **[Aproximación]**: la rareza corresponde a la idea de *bizarreness* de la investigación sobre sueños (lo que no podría pasar despierto), aquí en tres niveles juzgados por quien soñó, sin las escalas de codificación de esa literatura. La definición de pesadilla (un sueño muy desagradable que despierta o casi) es la habitual en clínica; no verifiqué en esta sesión una fuente con la redacción exacta **[sin verificar]**. *Cuándo falla:* un día viejo marcado «ansioso o pesadilla» puede haber sido solo ansioso; desde el esquema 17 cuenta como pesadilla.

### 3.12.2 Las categorías por ejes

Nueve ejes independientes (`Catalogo.SUENO_EJES`, desde el esquema 17; antes eran seis), con varias opciones cada uno:
- **quiénes aparecen** (familia, pareja, expareja, amistades, gente de antes, gente del trabajo o la escuela, desconocidos, figuras públicas, niños, animales, criaturas, alguien que ya murió, yo con otra edad o cuerpo);
- **dónde pasa** (mi casa, un lugar de mi pasado, escuela o trabajo, un lugar conocido pero cambiado, lugar desconocido, otro país, transporte, lugar imposible, naturaleza);
- **qué pasa entre personas** (romántico, sexual, amistoso, conflicto, ayuda, cuidado, rechazo, traición, reconciliación, despedida);
- **temas típicos** (persecución, caer, volar, examen o llegar tarde, perder algo, buscar, atrapado, no poder moverme o gritar, muerte, desnudez en público, dientes, viaje, agua, desastre, aparato que no funciona, embarazo, fiesta, volver a otra época, poderes);
- **cómo me fue** (logré algo, fallé, tuve suerte, me pasó algo malo, estaba en peligro);
- **sensaciones** (colores vivos, sonidos o voces, tacto o dolor, olores o sabores, caer o flotar en el cuerpo);
- **estructura** (una persona en el centro, restos del día anterior, se repite, continúa uno anterior, me veía desde fuera, era otra persona, cambios bruscos de escena, desperté a la mitad);
- **emociones del sueño** (19, varias a la vez);
- **al despertar** (aliviado de que no fuera real, con ganas de volver, con la emoción encima, pensando en alguien del sueño).

Arriba de los ejes aparecen las **8 categorías más marcadas en los últimos 180 días** (`Catalogo.suenoRapidas`); si no llegan a 8, se completan con una lista fija. Antes la lista era siempre la misma. La categoría «solo fragmentos» de la estructura se retiró en el esquema 17 porque repetía la claridad del recuerdo («vago»); en los días viejos se conserva.

Se basan en el sistema de **Hall y Van de Castle (1966)**, que codifica los sueños en categorías como personajes, interacciones sociales, escenarios, emociones, éxitos y fracasos, entre otras («cómo me fue» toma de ahí éxito, fracaso, buena y mala fortuna). Las sensaciones, la perspectiva y «al despertar» son **[Diseño propio]**. **[Aproximación]**: el sistema original se aplica **leyendo el relato** con reglas detalladas de codificación; aquí la persona que soñó elige categorías simplificadas. Sirve para ver patrones propios, no para compararse con las normas publicadas. La idea de que los sueños reflejan preocupaciones de la vida despierta (hipótesis de la **continuidad**) es la de Domhoff (2003).

### 3.12.3 Cómo entra al panel

- `sueno_calidad` (1–5), `sueno_valencia` (solo si recordaste), `sueno_palabras` (palabras del relato).
- `sueno_lucidez` y `sueno_vividez` se convierten en **0, 1, 2** (por ejemplo, vago = 0, moderado = 1, muy vívido = 2), solo en días con sueño recordado. Tratarlas como números supone que los saltos entre niveles son iguales. **[Aproximación]**.
- `sueno_<categoría>` = 1/0, solo en días en que marcaste **alguna** categoría (si no marcaste ninguna, quedan vacías: bien resuelto). En los días de antes del esquema 17, el tipo «recurrente» cuenta como `sueno_est_recurrente`.
- En el panel extendido, la familia `suenot_` ya no es el tipo: son `suenot_realista`, `suenot_raro`, `suenot_imposible` y `suenot_pesadilla` (1 si ese día el sueño fue así), en los días con sueño recordado. Las columnas viejas (`suenot_mundano`, `suenot_ansioso_pesadilla`…) dejan de existir.
- La exportación (`vm_dias.csv`) agrega `sueno_rareza` y `sueno_pesadilla` (con los días viejos ya traducidos) y conserva `sueno_tipo` tal como se guardó.

**`sueno_recordado` valía 0 cuando no respondiste (H-2-4).** Era 1 si marcaste que recordaste un sueño y **0 en cualquier otro caso**, incluidos los días en que no abriste la pantalla de sueño, así que la frecuencia de recuerdo se subestimaba. Desde la entrega 10c (D.63) vale 1 si recordaste un sueño, **0 solo si ese día dijiste algo del sueño** (la calidad o un texto) sin marcar que lo recordaste, y queda vacía en los demás días. Ejemplo: un día con calidad 4 y sin sueño recordado → 0; un día en que no tocaste la sección del sueño → vacío.

---

## 3.13 Presencias: estuve, pensé, mensajes

Cuando nombras a una @persona en el diario, la app pregunta (con un toque sobre el nombre) cómo fue: **estuve con** (la viste en persona), **pensé en** (no la viste) o **mensajes** (hablaron sin verse). Está en `data/Presencias.kt`.

Reglas exactas:

- **En el diario, el estado por omisión es «estuve con»**: si no tocas el nombre, cuenta como que la viste (este punto ya está anotado como H-1-6 en el capítulo 2). El día queda «revisado» (`presencias_ok`) al tocar un nombre debajo del diario o al confirmarlo en la pantalla de revisión de presencias.
- **Si alguien aparece solo en momentos** (no en el diario), cuenta como «pensé en» si ese momento era de tipo «pensando en» o «recuerdo» **y** estabas solo o no marcaste compañía; en otro caso, como «estuve con». Si aparece en varios momentos, basta uno de «estuve» para que cuente como «estuve».
- Lo que marcas en el diario **manda** sobre lo que dicen los momentos.
- Los momentos privados no se usan para esto.

**Razonamiento.** Nombrar a alguien no es haberlo visto, y para estudiar el efecto del contacto social sobre el ánimo importa la diferencia entre ver, pensar y escribir. **[Diseño propio]**, razonable. Cómo se usan estas presencias en la red de personas y en las variables `vi_*` y `penso_en_*` se explica en el capítulo 7.

---

## 3.14 Reactividad: medir cambia lo medido

**Qué es.** Que el acto de registrar cambie la conducta o el estado que se registra. Puede pasar porque registrar te hace **prestar atención** (te das cuenta de que duermes poco y duermes más), porque **nombrar** una emoción la modifica (sección 3.6.4) o porque quieres **verte bien** en tus propios datos (más aún si hay logros y rachas; capítulo 9).

**Qué dice la evidencia.** En un estudio con pacientes con dolor crónico que registraban su dolor cada hora durante una semana, el registro intensivo no cambió en promedio el dolor reportado (Cruise et al., 1996). Hasta donde sé, la literatura en general encuentra efectos de reactividad pequeños en estos diseños, más probables en conductas que la persona quiere cambiar; no verifiqué esa conclusión general con una revisión durante esta sesión **[sin verificar]**. **[Validado]** que puede no haber reactividad apreciable en el ánimo o el dolor; no se puede descartar para hábitos y conductas.

**Qué implica para la app.**

- Los cambios **al inicio** del uso (o al empezar a medir algo nuevo) pueden deberse en parte a empezar a medir. Conviene no sacar conclusiones de las primeras semanas.
- Las conductas que quieres cambiar (hábitos, sustancias, el seguimiento privado) son las más expuestas.
- Ver tus propios números antes de responder (sección 3.4) es una forma de reactividad dentro del mismo día.
- Desde la entrega N4 de la revisión de navegación, Hoy muestra **«Tus últimos días»**: 14 hilos con el color de la valencia de cada día, el alto del sueño frente a tu normal y si el día quedó sellado (`FranjaDias`, en `ui/pantallas/Hoy.kt`). Pone tu historial de ánimo en primer plano. Para no influir en cómo calificas el día, el hilo de hoy no se colorea hasta que tiene valencia y diario, y la franja no aparece en la hoja de la noche. Aun así, puede influir; se puede apagar en Apariencia. **[Diseño propio]**.

---

## 3.15 Datos que faltan: por qué no da igual cuándo faltan

### 3.15.1 El problema

En un registro personal, los datos **no faltan al azar**. Lo típico es no llenar el diario precisamente los días malos (por cansancio, tristeza o porque no hay ganas de pensar en ellos), o no contestar avisos cuando estás ocupado o con gente. Cuando la probabilidad de que falte un dato depende del propio dato, se habla de faltante **no al azar** (MNAR; Rubin, 1976), y los promedios quedan sesgados de una forma que **no se puede corregir solo con los datos que sí hay**.

**Ejemplo (inventado, Figura 3.4).** Diez días con valencias reales 5, 6, 5, 2, 6, 5, 3, 6, 5, 2 (promedio **4.50**). Si los tres días malos (2, 3, 2) no se registran, el promedio de lo registrado es 38 / 7 = **5.43**: casi un punto más alto. Todas las correlaciones con la valencia quedan calculadas sobre «los días que tuviste ganas de registrar».

### 3.15.2 Lo que hace la app

**El registro de un toque.** La notificación del diario trae un botón «**Día difícil: solo un número**» que abre una pantalla con **una sola escala de valencia**. Al tocarla se guarda la valencia del día con la marca `dia_dificil = 1` (`QuickActivity.Minimo`). La idea: que llenar el día cueste un segundo justo cuando menos ganas hay. «Un día registrado así vale mucho más que un día vacío», dice la propia pantalla, y es correcto: en el ejemplo, con esos tres números el promedio vuelve a 4.50. **[Diseño propio]** con buen fundamento. La variable `dia_dificil` permite además ver cuántos días se registraron así.

**Otros apoyos:** desde la entrega N2 (D.37), Hoy muestra como pendientes los días de los 7 anteriores que no tienen ni valencia ni diario («Diario de ayer» o «Te faltan…», `Pendientes.faltantes`), a cualquier hora, hasta que los llenes; descartarlo los oculta solo ese día (antes, el diario de ayer dejaba de pedirse a las 14:00). Además, los cuestionarios insisten más con el atraso; la «semana corta» reduce la app a lo mínimo; y `retroactivo` separa lo escrito tarde (desde el esquema 13, con `ts_valencia`; sección 3.7.4).

**Lo que faltaba hasta el esquema 13:** saber **cuántos avisos se mostraron** (H-2-3). Desde entonces `tasa_respuesta` está en el panel (sección 3.5.3). Con ella se puede ver si los avisos sin respuesta se concentran en ciertos días u horas, y usarla como covariable: por ejemplo, ver si los días con tasa baja son también los de valencia baja del diario.

![Figura 3.4](figuras/fig09_dia_faltante.png)

*Figura 3.4. Cómo los días malos sin registro inflan el promedio, y cómo el registro de un toque lo corrige (datos sintéticos). Script: `figuras/fig09_dia_faltante.py`.*

### 3.15.3 Editar después

Un registro de ánimo se puede editar más tarde con `DialogoMomento`: valencia, activación, emociones, pensamiento, texto y, desde el esquema 14, también **la fecha y la hora**. Se abre tocándolo en el Día de su fecha (sección «Tu día»), en Diario → «Línea de tiempo» o en Privado.

Desde la entrega N3 de la revisión de navegación, «Tu día» lista todo lo que tiene hora ese día (momentos, consumos, tramos, gastos, ingresos, tomas y notas; lo privado no) y cada fila abre su corrección. «＋ agregar a este día…» abre el registro de ánimo, el gasto, la nota o las tomas **con la fecha de ese día ya puesta**. El registro de ánimo empieza a mediodía de ese día y la hora se elige ahí mismo. Todo lo que se agrega así guarda `ts_creado` con la hora real en que se anotó, así que `vm_retraso()` lo marca como anotado tarde.

Hasta el esquema 13, el registro **no guardaba que fue editado ni cuándo**, así que un dato «momentáneo» podía convertirse en retrospectivo sin que se supiera (H-2-9). Desde el 14:
- cada corrección guarda `ts_editado`, y cada registro nuevo, `ts_creado` (cuándo se anotó);
- al cambiar la hora, el día se recalcula con el día lógico y el lugar se toma de las ubicaciones de esa hora (`Recaidas.aOtraHora`); la luz y el ruido medidos al registrar se descartan;
- un momento de antes, con tipo «idea», «alegría», etc., pasa a tipo `animo` al corregirlo: lo que sentías y pensabas ya va explícito en `emociones` y `pensamiento`. Por eso, los conteos `m_<tipo>` de esos tipos viejos pueden bajar un poco en los días con correcciones.

En R, `vm_retraso()` dice qué registros se anotaron tarde (Apéndice C). Los registros con `ts_editado` se pueden excluir si se quiere solo lo momentáneo sin tocar.

---

## 3.16 Seguimiento privado (solo la mecánica)

Hay tres registros:
- **impulso**: intensidad de 0 a 5 en el día, y momentos con intensidad de 1 a 5;
- **recaída**: sí o no en el día; momentos con hora, duración, detonantes, aparato y qué hacías antes;
- **libido**: de 0 a 5 y en qué está el foco; es distinta del impulso.

Usan anclas propias (`Catalogo.ANCLAS["impulso"]`, `["libido"]`). Los momentos de impulso se pueden registrar siempre. La intensidad del día, las recaídas, la libido y los detonantes solo con el seguimiento privado activado.

Desde el módulo 10a todo esto, junto con los medicamentos, **se anota y se ve solo en la pantalla Privado**. No aparece en el Día, los momentos, la cinta, los reportes, los logros, «Tu año», los mapas, los widgets ni los análisis de la app, y la red de personas no lo usa. **Sí** entra en el panel que se exporta a R (por ejemplo, `n_recaida`, `recaida_min`, `n_impulso`), porque su propósito es entender qué días y contextos lo hacen más difícil. El capítulo 10 detalla cómo se protege. **[Diseño propio]**.

**Para la medición, el cambio tiene dos efectos posibles, sin comprobar:**
- Ver menos estos datos en el día a día puede reducir la reactividad (que registrar cambie lo registrado).
- Que la intensidad del impulso ya no esté en el diario de la noche puede hacer que se anote menos días, y eso aumenta los datos faltantes.

---

## 3.17 Una medida que no es autorreporte: la prueba de reacción

En «Medir» hay una **prueba de reacción** breve (`PantallaReaccion`, en `ui/pantallas/Medir.kt`): **10 intentos**; en cada uno se espera un tiempo al azar entre **1.5 y 4.0 segundos** y aparece un círculo; hay que tocar lo antes posible. Se guarda el tiempo de cada intento, la **media**, la **mediana**, los **lapsos** (respuestas de **500 ms o más**) y las **anticipaciones** (toques antes de que aparezca el círculo). Entre intentos hay una pausa de 0.6 s.

**Base científica.** Es una versión muy corta de la **prueba de vigilancia psicomotora (PVT)**, la medida estándar de cómo la falta de sueño afecta la atención (Basner y Dinges, 2011). La PVT estándar dura **10 minutos**, con esperas de **2 a 10 s**, cuenta como lapso un tiempo de **500 ms o más** y como **falsa salida** tanto tocar sin estímulo como responder en **menos de 100 ms** (demasiado rápido para ser una reacción real). La versión breve validada (PVT-B) dura **3 minutos**, con esperas de **1 a 4 s** (Basner, Mollicone y Dinges, 2011), y sus autores encontraron que para ella funcionaba mejor un umbral de lapso de **355 ms**.

**Comparación (H-2-12).** La pantalla dice «unos 30 segundos»; con 10 esperas de 1.5 a 4.0 s (2.75 s en promedio), 9 pausas de 0.6 s y los tiempos de reacción, sale más cerca de **unos 36 s** en promedio (de ~22 a ~50 s con reacciones de 0.15 a 0.5 s; cada anticipación suma 0.9 s y una espera nueva; cálculo propio a partir de `PantallaReaccion`). Es mucho menos que cualquier versión validada; usa el umbral de 500 ms (pensado para la prueba de 10 minutos); hasta la entrega 10c **no marcaba como falsas salidas las respuestas de menos de 100 ms** (entraban en la media). Desde 10c, un toque a menos de 100 ms del estímulo cuenta como anticipación (suma a `anticipaciones`), no entra en los tiempos y el intento se repite, como en la PVT (D.74). Sigue midiendo en una pantalla táctil, con retrasos de dibujo y de detección del toque que varían entre teléfonos. La propia app advierte que solo sirve para compararte contigo mismo. **[Aproximación]**: útil para ver tendencias grandes, con mucho ruido de un día a otro. En el panel, `rt_media` es el promedio de las medias de las pruebas hechas ese día.

---

## 3.18 Amenazas a la validez (resumen)

| Código | Amenaza | Dónde | Qué hacer al analizar | Hallazgo |
|---|---|---|---|---|
| L-3.1 | Cuestionarios asignados a semanas que no les corresponden | 3.9.3 | Resuelto en 10c (D.62): cada puntaje cubre los días que pregunta | H-2-1 |
| L-3.2 | Días malos sin registrar (MNAR) | 3.15 | Revisar `dia_dificil` y días sin diario; no confiar en promedios de periodos con muchos huecos | — |
| L-3.3 | Tasa de respuesta a avisos desconocida | 3.5.3 | Desde el esquema 13 (10b), usar `avisos_mostrados` y `tasa_respuesta`; en los días anteriores no hay denominador y `n_muestreos` va con cautela | H-2-3 |
| L-3.4 | Recuerdo frente a experiencia | 3.4 | No tratar `valencia` y `muestreo_valencia` como intercambiables | — |
| L-3.5 | Registros por iniciativa propia sesgados hacia eventos | 3.5.4 | Preferir `muestreo_valencia` para «cómo estoy en general» | — |
| L-3.6 | «No respondí» codificado como 0 | 3.7.3, 3.12.3 | `sueno_recordado`, hábitos antes de existir y «las tres» resueltos en 10c (D.63); `ctx_*` y hábitos no marcados siguen como cotas inferiores | H-2-4 |
| L-3.7 | `retroactivo` poco fiable | 3.7.4 | Corregido en el esquema 13 (10b, `ts_valencia`); en los días anteriores, que conservan la marca vieja, usarlo con cautela | H-2-2 |
| L-3.8 | Perfil proyectado hacia atrás | 3.11 | Resuelto en 10c para los perfiles nuevos (D.74); en los ya llenados, desconfiar de `vida_*` antes de la primera respuesta | H-2-5 |
| L-3.9 | Reactividad | 3.14 | Descartar o separar las primeras semanas de cada medición nueva | — |
| L-3.10 | Instrumentos adaptados (periodo de 7 días) | 3.9.1 | Usar los puntajes para compararte contigo, no con normas | — |
| L-3.11 | Anclaje al calificar el día | 3.4 | Desde N4 los avisos se ven después de elegir la valencia; antes (sin `version_noche` = 2), tener presente que `valencia` y `muestreo_valencia` no son independientes | H-2-11 |

---

## 3.19 Qué es estándar y qué es propio

| Elemento | Clasificación | Respaldo |
|---|---|---|
| Valencia y activación como ejes del ánimo | Validado | Russell (1980) |
| Cuadrícula de un toque | Aproximación | Russell et al. (1989); formato 7 × 7 propio |
| Escala diaria 1–7 con anclas | Aproximación | Práctica común; anclas propias |
| Avisos al azar dentro de tramos | Validado | Shiffman et al. (2008); Csikszentmihalyi y Larson (1987) |
| Separar avisos de registros propios | Validado (principio) | Shiffman et al. (2008) |
| Tres «yo»: momento, recuerdo, creencia | Validado | Conner y Barrett (2012); Robinson y Clore (2002) |
| Emociones con nombre y «no sé» | Validado (concepto) / Diseño propio (lista) | Kashdan et al. (2015) |
| Flujo, necesidades, evaluación del estrés | Aproximación | Csikszentmihalyi y LeFevre (1989); Deci y Ryan (2000); Lazarus y Folkman (1984) |
| Predicción de mañana y su error | Validado (idea) / Diseño propio (variable) | Wilson y Gilbert (2005) |
| WHO-5, PSS-4, UCLA-3, TIPI | Validado (instrumentos) / Aproximación (periodo adaptado) | Topp et al. (2015); Cohen et al. (1983); Hughes et al. (2004); Gosling et al. (2003) |
| Asignación de cuestionarios a días | Diseño propio | Ventana del periodo preguntado, primera respuesta que cubre (10c) |
| Brújula política | Diseño propio | — |
| Etapas de vida desde el perfil | Diseño propio | — |
| Categorías de sueños por ejes | Aproximación | Hall y Van de Castle (1966); Domhoff (2003) |
| Presencias (estuve / pensé / mensajes) | Diseño propio | — |
| Registro de un toque para días difíciles | Diseño propio (con fundamento) | Rubin (1976) sobre datos faltantes |
| Prueba de reacción | Aproximación | Basner y Dinges (2011); Basner et al. (2011) |

---

## 3.20 Resumen del capítulo

1. La app mide el ánimo en **dos ejes** (valencia y activación) y de **tres formas**: en el momento (avisos al azar y registros propios), al recordar el día (diario) y como creencia general (cuestionarios). Cada forma tiene sesgos distintos y **no son intercambiables**.
2. Los avisos usan **muestreo aleatorio estratificado**: 3 al día por omisión, uno por tramo de la ventana 10:00–21:00, con margen de 45 minutos. Es un diseño estándar.
3. El hallazgo más grave del módulo era que **los puntajes de los cuestionarios se asignaban en el panel a días de otras semanas** (ventanas de 14 y 30 días hacia atrás, con sobrescritura) (H-2-1). Desde la entrega 10c, cada puntaje cubre solo los días que pregunta y cada día se queda con la primera respuesta que lo cubre (D.62).
4. Varias variables codificaban «no respondí» como 0 (`sueno_recordado`, contextos, hábitos, «las tres» necesidades) (H-2-4); en 10c se corrigieron `sueno_recordado`, los hábitos antes de existir y «las tres» (D.63); los contextos siguen la convención de la pantalla. El perfil se proyectaba hacia atrás (H-2-5; corregido para los perfiles nuevos en 10c, D.74); la marca `retroactivo` no reflejaba cuándo se llenó el ánimo (H-2-2; corregido en 10b). Los cierres guardados otro día ya no cuentan en el panel (D.74).
5. Hasta el esquema 12 no se guardaba cuántos avisos se mostraron, así que no hay tasa de respuesta de esos días (H-2-3); desde el esquema 13 (10b) el panel tiene `avisos_*` y `tasa_respuesta`.
6. El **registro de un toque** para días difíciles es una buena defensa contra el sesgo de los días malos sin registrar.
7. Los cuestionarios están bien puntuados (incluidas las preguntas inversas), pero WHO-5 y PSS-4 usan un periodo de 7 días distinto al original, y algunos puntos de corte que muestra la app no los pude verificar.
8. El módulo 2 identificó **15 hallazgos** (H-2-1 a H-2-15) y no cambió código. Después, el módulo 10 resolvió H-2-1, H-2-2, H-2-3, H-2-4, H-2-7, H-2-12 y H-2-14, y en parte H-2-5, H-2-6 y H-2-15; la revisión de navegación (esquema 14) resolvió H-2-8 (completado en 10c), H-2-9 y H-2-11; el módulo 10d, en parte, H-2-13, y la entrega 10e, H-2-10 (`error_prediccion_signo`, D.94). Ya no queda ninguno pendiente.

---

### Referencias citadas en este capítulo

Ver `referencias.md`. Citas: Basner y Dinges (2011); Basner, Mollicone y Dinges (2011); Cohen, Kamarck y Mermelstein (1983); Conner y Barrett (2012); Cruise et al. (1996); Csikszentmihalyi y Larson (1987); Csikszentmihalyi y LeFevre (1989); Deci y Ryan (2000); Domhoff (2003); Gosling, Rentfrow y Swann (2003); Hall y Van de Castle (1966); Hughes et al. (2004); Kahneman et al. (1993); Kashdan, Barrett y McKnight (2015); Lazarus y Folkman (1984); Lieberman et al. (2007); Remor (2006); Rintala et al. (2019); Robinson y Clore (2002); Romero et al. (2012); Rubin (1976); Russell (1980); Russell, Weiss y Mendelsohn (1989); Schwarz y Clore (1983); Schwarz, Strack y Mai (1991); Shiffman, Stone y Hufford (2008); Stone et al. (2002); Topp et al. (2015); Westermann et al. (1996); Wilson y Gilbert (2005).
