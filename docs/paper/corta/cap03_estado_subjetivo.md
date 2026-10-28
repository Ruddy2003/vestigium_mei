# Capítulo 3. Medición del estado subjetivo

> **Para qué sirve este capítulo.** Explica **cómo mide la app lo que sientes y piensas**: el ánimo en el momento y el del día, las emociones con nombre, los cuestionarios, los sueños, con quién estuviste y algunas mediciones raras. Para cada cosa dice qué se pregunta, cuándo, en qué escala, cómo se convierte en número y qué tan sólido es eso. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo que cambió en las entregas 10e y 10f se marca «desde 10e» y «desde 10f». **Los ejemplos numéricos son inventados.** Los hallazgos se numeran H-2-n (módulo 2). El seguimiento privado se describe solo en su mecánica.

---

## 3.1 Qué se mide y cuándo

Lo subjetivo no lo mide ningún sensor: hay que preguntarlo, con un **autorreporte** (una respuesta que das tú sobre ti mismo), que tiene sesgos conocidos. Por eso la app **mide lo mismo de varias formas**, cada una con sesgos distintos:

| Medición | Cuándo se pide | Variables principales |
|---|---|---|
| Ánimo ahora (aviso) | 3 veces al día por omisión, al azar entre 10:00 y 21:00 | `muestreo_valencia`, `muestreo_activacion` |
| Ánimo ahora (por iniciativa propia) | Cuando quieras | entra en `animo_valencia`, `animo_activacion` |
| Ánimo al cerrar el día | Al principio de la hoja de la noche, solo la de hoy | `animo_cierre_valencia`, `animo_cierre_activacion` |
| Ánimo del día (diario) | Aviso a las 22:00 por omisión | `valencia`, `activacion`, `dolor` |
| Cuestionarios (WHO-5, PSS-4, UCLA-3, TIPI) | Semanal, mensual o anual | `esc_<instrumento>_<puntaje>` |
| Sueño y sueños | Aviso a las 8:00 por omisión | `sueno_calidad`, `sueno_recordado`… |
| Brújula política, perfil, prueba de reacción | Anual / al cambiar / diaria | `pol_<eje>`, `vida_*`, `rt_media` |

---

## 3.2 Los dos ejes del ánimo: valencia y activación

Russell (1980) propuso que casi todas las emociones se pueden ubicar en un plano con **dos ejes independientes**: qué tan agradable es la experiencia (**valencia**) y cuánta energía o agitación implica (**activación**, *arousal*). La *calma* es agradable y poco activada; la *ansiedad*, desagradable y muy activada. Como las emociones quedan alrededor de un círculo, se llama **circumplejo**. **[Validado]**: es uno de los modelos de afecto más usados, aunque no el único. Separar la activación permite ver, por ejemplo, si después de dormir poco no estás «peor», sino «más apagado».

**La cuadrícula del momento.** Para el ánimo en el momento, una **cuadrícula de 7 × 7** (`CuadriculaAfecto`) da dos números con **un solo toque**. Si el toque cae en la posición horizontal `x` de un cuadro de ancho `W`, la valencia es `⌊7·x/W⌋ + 1`; si cae en la altura `y` (medida desde arriba) de un cuadro de alto `H`, la activación es `7 − ⌊7·y/H⌋` (ambas limitadas a 1–7). Es una versión de la **Affect Grid** (Russell, Weiss y Mendelsohn, 1989), una escala de un solo reactivo para agrado y activación. **[Aproximación]**: la idea está validada, pero el formato 7 × 7 y las palabras de las esquinas son de la app.

**La escala del día.** Para el ánimo del día hay **dos filas de números del 1 al 7** con un texto por número, llamado **ancla** (`Catalogo.ANCLAS`): en valencia, de «1 = Muy desagradable: angustia, desesperanza» a «7 = Muy agradable: entusiasmo pleno», con «4 = Neutral: plano»; en activación, de «1 = Muy baja: agotado, apagado» a «7 = Muy alta: acelerado, inquieto». También pide **dolor o molestia física de 0 a 5**. Las anclas existen para que un «5» signifique lo mismo hoy que dentro de dos años. **[Aproximación]**: práctica común; las anclas concretas no están validadas. Las de activación mezclan un poco de valencia («3 = relajado, en calma» suena agradable), un efecto pequeño de tamaño desconocido (parte de H-2-11).

Las dos escalas **comparten nombre y rango pero no son el mismo instrumento**: una tiene anclas y pregunta por todo el día; la otra, por *ahora*. Un 5 en una no equivale necesariamente a un 5 en la otra.

---

## 3.3 Tres formas de responder «¿cómo estuve?»

Conner y Barrett (2012) distinguen el **yo que experimenta** (cómo te sientes *ahora*), el **yo que recuerda** (cómo *recuerdas* que te sentiste) y el **yo que cree** (cómo *crees* que eres en general). Cuanto más lejos está el momento que se reporta, más se apoya la respuesta en creencias generales (Robinson y Clore, 2002). **[Validado]**. La app mide las tres: los avisos y registros propios, el diario de la noche (una medida **retrospectiva**: se responde después, recordando) y los cuestionarios. El cierre del día es momentáneo, pero siempre al final del día.

**El recuerdo no es un promedio.** Al evaluar un periodo, la gente pesa de más el **momento más intenso** y **cómo terminó** (regla «pico-final»; Kahneman et al., 1993). *Ejemplo (inventado):* tres avisos con 6, 5 y 6 (promedio 5.67); a las 21:30, una discusión fuerte; en el diario pones **3** y el cierre lo marcas en 2. Las tres cifras son «verdad», pero de cosas distintas, y por eso se guardan las tres.

**Cómo se calcula cada una** (`Estadistica.panel`): `muestreo_valencia` es el promedio de la valencia de los registros de tipo `muestreo` del día lógico; `animo_valencia`, el de **todos** los registros de ánimo (incluido el cierre); `animo_cierre_valencia`, la del **último** cierre; `valencia`, el número del diario. Las relaciones entre ellas se marcan como «obvias» (capítulo 6).

**Anclaje (H-2-11).** Ver los valores de tus avisos mientras calificas el día puede **anclar** (arrastrar) la respuesta hacia ellos. Desde la entrega N4 esa línea solo aparece **después** de elegir la valencia (`PantallaNoche`). **[Diseño propio]**.

---

## 3.4 El muestreo de experiencia

### 3.4.1 Cómo elige las horas

El **muestreo de experiencia** (ESM, *experience sampling method*) pregunta varias veces al día, en momentos que no eliges tú, «¿cómo estás ahora?» (Csikszentmihalyi y Larson, 1987; Shiffman et al., 2008). **[Validado]**. Las horas deben ser **al azar** y no elegidas: si solo registras cuando te pasa algo, solo conoces esos momentos.

`Programador.planDelDia` (`avisos/Trabajo.kt`) arma el plan así:

1. Toma la ventana (por omisión **10:00 a 21:00**) y el número de avisos (por omisión **3**, de 0 a 6).
2. Divide la ventana en **n tramos iguales**: tramo = (fin − inicio) / n.
3. Deja un **margen** a cada lado: margen = mín(45 minutos, tramo / 4).
4. Elige una hora **al azar** (uniforme) entre «inicio del tramo + margen» y «fin del tramo − margen», con la fecha como semilla (`Random(fecha.hashCode())`).

Es **muestreo aleatorio estratificado** (al azar, pero dentro de bloques; Figura 3.1): asegura avisos en la mañana, la tarde y la noche, sin que sean predecibles. **[Validado]** como diseño. Con los valores por omisión, cada tramo dura 220 minutos y el margen es mín(45, 55) = 45:

| Tramo | Va de | Hora posible del aviso |
|---|---|---|
| 1 | 10:00 a 13:40 | 10:45 a 12:55 |
| 2 | 13:40 a 17:20 | 14:25 a 16:35 |
| 3 | 17:20 a 21:00 | 18:05 a 20:15 |

Dos avisos seguidos quedan separados al menos **2 × mín(45 min, tramo/4)**: 90 minutos solo si cada tramo dura 3 horas o más; con 4 avisos, 82.5 minutos, y con 6, 55 (H-2-7).

![Figura 3.1](figuras/fig08_dia_muestreo.png)

*Figura 3.1. Un día sintético con los valores por omisión: tres avisos dentro de sus franjas posibles, una respuesta rápida, un aviso que caduca y los demás registros del día. Script: `figuras/fig08_dia_muestreo.py` (replica las reglas de `planDelDia`, pero no el generador al azar de Kotlin, así que las horas exactas difieren).*

### 3.4.2 Cómo llega el aviso y cómo se cuenta

El plan se revisa en el **pulso** de cada ~15 minutos (capítulo 4). Un aviso vencido se muestra **solo si han pasado menos de 50 minutos** desde su hora, y se borra solo a los 60 minutos. Si respondes tocándolo, el registro es de tipo **`muestreo`**, con la hora planeada en `aviso_ts` (y la exportación calcula `latencia_min`); si abres la app por tu cuenta, es **`animo`** (iniciativa propia).

Desde el esquema 13, la colección `avisos` anota por día los avisos **planeados**, **vencidos** y **mostrados**, y el panel tiene `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos` y **`tasa_respuesta`** = respondidos ÷ mostrados (`AvisosAnimo.cifras`). *Ejemplo (inventado):* 4 planeados; 3 vencen y 2 se muestran (el tercero llegó 70 minutos tarde porque el teléfono dormía); respondes uno: tasa 0.5. **[Validado]** el propósito; **[Diseño propio]** la forma de contarla. Hasta el esquema 12 no se guardaba cuántos avisos se mostraron (H-2-3), así que esos días no tienen tasa de respuesta, la primera cifra de un estudio de este tipo: combinando varios, Rintala et al. (2019) encontraron una respuesta promedio de 78 %, más baja temprano en la mañana y que bajaba con los días de estudio.

**Por qué separar avisos y registros propios.** Cuando decides registrar, sueles hacerlo **porque pasó algo**: un muestreo por evento, sesgado si se usa como «cómo estoy en general». La medida más limpia del ánimo momentáneo es `muestreo_valencia`; `animo_valencia` mezcla los dos tipos. **[Validado]** el principio. El mapa emocional de lugares usa todos los registros (H-2-15; versión completa, §3.5.4).

---

## 3.5 Emociones con nombre, pensamientos y compañía

En cualquier registro de ánimo se puede marcar (todo opcional) **qué sientes**, de 25 emociones (`Catalogo.SENTIMIENTOS`: 9 agradables, 13 desagradables y 3 mixtas), más «otro…» y «no sé»; **en qué piensas**; una nota con @personas y #temas, y **con quién estás**.

«Enojo» y «miedo» caen en la misma zona de los dos ejes y son experiencias distintas. Nombrar con precisión lo que sientes (*granularidad emocional*) se asocia con manejar mejor las emociones desagradables (Kashdan, Barrett y McKnight, 2015). **[Validado]** el concepto; la app solo guarda los nombres, no calcula un índice. «No sé» también es información y tiene su variable, `n_no_se`. **[Diseño propio]**. Como hay más emociones desagradables que agradables, **los conteos no son comparables entre sí**.

**En el panel**, `n_<emoción>` cuenta los registros del día con esa emoción, y `registros_animo`, los registros del día; desde el primer registro de ánimo, un día con diario sin registros vale **0** (H-1-1, capítulo 2). Con los avisos que tienen compañía marcada se calculan `acompanado_pct`, `con_*`, `valencia_acompanado` y `valencia_solo`. En `vm_dias.csv`, las columnas `m_<tipo>` cuentan solo los tipos de momento viejos y valen 0 desde que se unieron ánimo y momentos (H-2-13, resuelto en parte: el diccionario lo advierte).

Ponerle palabras a una emoción (*affect labeling*) puede reducir su intensidad: nombrar la emoción de una cara redujo la respuesta de la amígdala (Lieberman et al., 2007). **Registrar puede cambiar un poco lo registrado** (sección 3.13).

---

## 3.6 El diario de la noche

### 3.6.1 Qué contiene y en qué orden

La hoja de la noche («Llenar el día», `PantallaNoche`) sigue desde la entrega N4 este **orden fijo** (`VERSION_NOCHE = 2`): el cierre «Cómo estás ahora» (solo en la hoja de hoy); valencia, activación y dolor; predicción de mañana; contexto; hábitos y suplementos; diario; lo opcional, plegado; sellar (un cierre simbólico que no cambia los datos).

**Por qué este orden.** **El ánimo del momento va primero** porque recordar a propósito lo que te pasó cambia el ánimo (es una forma habitual de inducirlo en el laboratorio; Westermann et al., 1996), y nombrar al escribir baja la intensidad de la emoción (Lieberman et al., 2007). **El juicio general del día va antes de lo específico**: una pregunta concreta previa («¿cumpliste tus hábitos?») se mete en el juicio general o se excluye de él (Schwarz, Strack y Mai, 1991), y el panel cruza justo la valencia con los hábitos. **El costo**: el ánimo del momento puede teñir el juicio del día (Schwarz y Clore, 1983); se aceptó para proteger el cierre. **[Aproximación]**: viene de estudios de encuestas y de laboratorio, no de una prueba con esta app. `version_noche` (1 = orden anterior, 2 = nuevo) permite comparar los dos tramos en R.

### 3.6.2 La «lectura del día»

Tres preguntas de opción única, plegadas por omisión (`Catalogo.CIENTIFICAS`): **flujo** («¿El reto igualó tu habilidad?»: flujo, ansiedad, aburrimiento, apatía o «no aplica»; Csikszentmihalyi y LeFevre, 1989), **necesidades psicológicas** (autonomía, competencia, relación, ninguna o las tres; Deci y Ryan, 2000) y **evaluación del estrés** (reto, amenaza, pérdida o rutina; Lazarus y Folkman, 1984). **[Aproximación]**: los estudios miden cada componente por separado, con varios reactivos; aquí se elige un estado para el día completo. Cada opción es una columna 0/1 (`flow_flujo`, `sdt_necesidad_autonomia`…), vacía si no respondiste; «las tres» pone en 1 también cada necesidad (desde 10c, parte de H-2-4). Al estar plegadas, se responden menos y probablemente no al azar.

### 3.6.3 Contextos, hábitos y «retroactivo»

Los contextos (`ctx_*`) valen **0 en todos los días con diario**, también si no abriste esa sección: es la convención de la pantalla («marca lo que aplique»). Los hábitos quedan vacíos antes de `Habito.desde`, salvo que los hayas marcado (desde 10c). En ambos, «no lo marqué» y «no pasó» siguen siendo lo mismo (H-2-4, H-1-1).

`retroactivo` separa lo escrito en el momento de lo escrito de memoria. **[Validado]** el propósito: Stone et al. (2002) mostraron que en diarios en papel mucha gente llena días atrasados de golpe. Desde el esquema 13, `retroactivo` = el día lógico de `ts_valencia` (cuándo se llenó o cambió la valencia) es posterior a la fecha del día (`Reglas.retroactivo`); antes se decidía al crear el documento, y un día creado por el widget el martes y llenado el viernes quedaba «no retroactivo» (H-2-2). Llenarla a la 1:30 del miércoles no cuenta, porque sigue siendo el día lógico del martes. Desde 10f (D.109), ese día lógico se calcula con la zona horaria que tenía el teléfono al llenarla, no con la de hoy, así que un viaje no cambia la marca al volver (capítulo 4). **[Diseño propio]**.

### 3.6.4 El cierre del día

La cuadrícula «Cómo estás ahora» (`BloqueCierre`) guarda un registro `animo` de origen `cierre`, con la **hora real**: la misma medición en el mismo punto del día, comparable entre días. **[Diseño propio]**. Ese punto es la hora en que cierres, que varía y queda guardada (H-2-8). Solo se ofrece en la hoja de hoy (`cierrePermitido(fecha)`), y `Estadistica.cierreFuera(m)` deja fuera del panel los cierres viejos cuyo día lógico de la hora no es su `fecha`.

---

## 3.7 La predicción de mañana

«¿Cómo crees que estará tu ánimo mañana?», de 1 a 7, con anclas de agrado, así que se compara con la **valencia** del día siguiente (solo si existen las dos):

$$ \text{error\_prediccion} = |\,\text{predicción de ayer} - \text{valencia de hoy}\,| $$

Solemos equivocarnos al predecir cómo nos sentiremos (*pronóstico afectivo*; Wilson y Gilbert, 2005). **[Validado]** la idea; **[Diseño propio]** la variable. El valor absoluto quita la dirección: predecir 6 y obtener 4 (optimista) y predecir 3 y obtener 5 (pesimista) dan el mismo error, 2. Por eso, desde 10e, el panel tiene también el error con signo (D.94; H-2-10, resuelto):

$$ \text{error\_prediccion\_signo} = \text{valencia de hoy} - \text{predicción de ayer} $$

De −6 a +6; positivo si el día salió mejor de lo predicho. En el ejemplo, −2 y +2. **[Diseño propio]**. Se mueve con `valencia` por construcción; por eso la app esconde esa pareja como obvia, igual que con `prediccion` y `error_prediccion`.

---

## 3.8 Cuestionarios validados

### 3.8.1 Los cuatro instrumentos

Un **instrumento validado** es un cuestionario publicado cuyas preguntas, escala y puntuación se han probado en estudios. Están en `ciencia/Escalas.kt` y solo se guardan con **todos** los reactivos contestados.

**WHO-5 (bienestar), cada 7 días.** Cinco frases de 0 a 5; bruto = suma (0–25), porcentaje = bruto × 4 (0–100) (Topp et al., 2015). La app interpreta **≤ 28** como «valor bajo» y **≤ 50** como «por debajo de lo habitual». En la literatura, ≤ 50 suele usarse para tamizaje de depresión (conviene evaluar más); que ≤ 28 indique probable depresión no se comprobó en la fuente primaria **[sin verificar]**. El original pregunta por **2 semanas**; la app, por **7 días**, así que los cortes son orientativos.

**PSS-4 (estrés percibido), cada 7 días.** Cuatro preguntas de 0 a 4, dos **inversas** (se puntúan al revés):

$$ \text{total} = p_1 + p_4 + (4 - p_2) + (4 - p_3) \quad (0\text{–}16) $$

(Cohen, Kamarck y Mermelstein, 1983). La versión española citada (Remor, 2006) validó las de 14 y 10 preguntas, no la de 4. El original pregunta por el último mes; la app, por 7 días. **[Validado]** el instrumento; **[Aproximación]** el periodo. *Ejemplo:* p1 = 2, p2 = 3, p3 = 1, p4 = 2 → 2 + 2 + 1 + 3 = **8**.

**UCLA-3 (soledad), cada 30 días.** Tres preguntas de 1 a 3; total 3–9 (Hughes et al., 2004). El corte «6 o más» que muestra la app aparece en estudios que usan la escala, pero no se encontró la fuente que lo establece **[sin verificar]**. **[Validado]** el instrumento.

**TIPI (personalidad), cada 365 días.** Diez frases de 1 a 7; cada rasgo de los cinco grandes promedia una frase directa y una inversa (8 − respuesta), por ejemplo extraversión = (t1 + (8 − t6)) / 2 (Gosling, Rentfrow y Swann, 2003; Romero et al., 2012; los cinco rasgos en la versión completa, §3.9.1). Sirve para ver **cambios grandes** con los años. **[Validado]**.

`Escalas.proxima` fija la siguiente fecha (última respuesta + 7, 30 o 365 días), y el recordatorio insiste más con el atraso (`Escalas.horasAviso`). La regularidad importa porque cada instrumento pregunta por un periodo.

### 3.8.2 Cómo pasan al panel (H-2-1)

Un cuestionario se responde **un día**, pero habla de **un periodo**, así que el puntaje se copia a varios días. Hasta la entrega 10c, WHO-5 se copiaba a 14 días y PSS-4 y UCLA-3 a 30, hacia atrás, y **cada respuesta sobrescribía** a las anteriores: un día podía recibir el «estrés» de una respuesta dada hasta 29 días después. Gravedad **alta**.

Ahora `Dinamicas.porDiaCuestionario` usa una ventana igual al periodo que pregunta cada instrumento: WHO-5 y PSS-4, el día de la respuesta y los **6 anteriores**; UCLA-3, el día y los **29 anteriores**; TIPI, del día de la respuesta hacia adelante hasta la siguiente (como mucho un año). Si dos respuestas cubren el mismo día, se queda **la primera** que lo cubre (`putIfAbsent`): la más cercana **después** de ese día, la que pregunta por él. Un día sin respuesta que lo cubra queda vacío.

*Ejemplo (inventado, Figura 3.2).* PSS-4 los días 1, 8, 15, 22 y 29 de marzo, con 4, 6, 9, 5 y 7. Antes, del 1 al 29 **todos** los días quedaban con 7 y la semana de 9 desaparecía. Ahora el 3 de marzo solo lo cubre la respuesta del 8: **6**, y cada semana conserva su puntaje (`CalculosPrueba.cuestionarioPorSemana`). **[Diseño propio]** la regla; **[Aproximación]** suponer que el puntaje vale igual para cada día del periodo.

![Figura 3.2](figuras/fig10_cuestionarios_ventana.png)

*Figura 3.2. PSS-4 sintético respondido cada semana. Línea discontinua: lo que guardaba el panel hasta la entrega 10c (ventana de 30 días, gana la última respuesta). Línea continua: lo que guarda ahora (los 7 días que pregunta cada respuesta, gana la primera que cubre el día). Script: `figuras/fig10_cuestionarios_ventana.py` (replica las dos versiones de `Dinamicas.kt`).*

---

## 3.9 La brújula política

Una vez al año, 25 afirmaciones **escritas para la app** (`data/Politica.kt`), 5 por eje (economía, autoridad, valores, mundo y ambiente), de 1 a 5. Cada afirmación tiene un **signo** (+1 o −1) según el polo al que acerca estar de acuerdo:

$$ \text{posición} = \frac{\sum (\text{respuesta} - 3) \times \text{signo} \times 100}{2 \times \text{afirmaciones respondidas}} \quad (-100 \text{ a } +100) $$

`Politica.lado` dice «muy hacia…» desde ±60, «hacia…» desde ±20 y «al centro» entre −20 y 20 (sin incluirlos). Mezclar signos busca neutralizar la **aquiescencia** (tender a estar de acuerdo con todo), pero el reparto es 3 contra 2: «muy de acuerdo» en las 25 deja cada eje en **±20**, «hacia…» en los cinco (H-2-6). Gravedad baja, porque el sesgo es constante y sirve para compararte contigo mismo. **[Diseño propio]**.

---

## 3.10 «Conocerte»: el perfil y las etapas de vida

«Conocerte» (`data/Perfiles.kt`) pregunta lo grande de tu vida (con quién vives, pareja, ocupación, ciudad…) y guarda cada cambio con su fecha; de ahí salen las variables de **etapa de vida** (`vida_en_pareja`, `vida_vive_solo`…). `valorEn(campo, fecha)` da el último cambio antes o en esa fecha; si no hay, el valor «antes» del primer cambio posterior; si nunca hubo cambios, el actual. Por eso, llenar el perfil por primera vez proyectaba el valor sobre **todos los días anteriores** (H-2-5). Desde 10c el primer guardado deja historial (`Perfiles.primerGuardado`): si llenas «en pareja» el 10 de octubre, `vida_en_pareja` vale 1 desde ese día y queda vacía antes. Los perfiles llenados antes de esa versión siguen proyectándose hacia atrás. **[Diseño propio]**.

---

## 3.11 Sueño y sueños

A las **8:00** por omisión, si el día no tiene relato ni la marca «recordé un sueño», llega un aviso. La pantalla pone **primero la narración**, porque el contenido de los sueños se olvida muy rápido; que sea cuestión de minutos es la afirmación de la app **[sin verificar]**. Se registra la **calidad del sueño como la sentiste** (1–5, en el día del despertar) y, si recordaste un sueño, su valencia (1–7), lucidez, vividez, tipo y categorías en seis ejes (`Catalogo.SUENO_EJES`).

Las categorías se basan en el sistema de **Hall y Van de Castle (1966)**. **[Aproximación]**: el original se aplica leyendo el relato con reglas detalladas; aquí quien soñó elige categorías simplificadas, útiles para patrones propios, no para compararse con las normas. La idea de que los sueños reflejan preocupaciones de la vida despierta (hipótesis de la **continuidad**) es de Domhoff (2003).

En el panel, `sueno_lucidez` y `sueno_vividez` valen 0, 1 o 2 (supone saltos iguales, **[Aproximación]**). `sueno_recordado` vale 1 si recordaste un sueño, 0 solo si ese día dijiste algo del sueño sin recordarlo, y vacío en los demás (H-2-4, resuelto en 10c).

---

## 3.12 Presencias: estuve, pensé, mensajes

Al nombrar a una @persona en el diario, un toque sobre el nombre dice cómo fue: **estuve con**, **pensé en** o **mensajes** (`data/Presencias.kt`); por omisión, «estuve con» (H-1-6). Si alguien aparece solo en momentos, cuenta como «pensé en» si el momento era de tipo «pensando en» o «recuerdo» **y** estabas solo o sin compañía marcada; si no, «estuve con». Lo marcado en el diario manda, y los momentos privados no se usan. Nombrar a alguien no es haberlo visto. **[Diseño propio]**. Su uso en la red de personas está en el capítulo 7.

---

## 3.13 Reactividad: medir cambia lo medido

**Reactividad** es que registrar cambie lo registrado: porque te hace prestar atención, porque nombrar una emoción la modifica o porque quieres verte bien en tus datos (capítulo 9). En pacientes con dolor crónico que lo registraban cada hora durante una semana, el registro no cambió en promedio el dolor reportado (Cruise et al., 1996). **[Validado]** que puede no haber reactividad apreciable en el ánimo o el dolor; no se puede descartar para hábitos y conductas. Conviene no sacar conclusiones de las primeras semanas de cada medición nueva. «Tus últimos días» en Hoy pone tu historial de valencia en primer plano; el hilo de hoy no se colorea hasta tener valencia y diario, y se puede apagar. **[Diseño propio]**.

---

## 3.14 Datos que faltan: por qué no da igual cuándo faltan

Lo típico es no llenar el diario precisamente los días malos. Cuando la probabilidad de que falte un dato depende del propio dato se habla de faltante **no al azar** (MNAR; Rubin, 1976), y el sesgo no se puede corregir solo con los datos que sí hay. *Ejemplo (inventado):* diez días con valencias 5, 6, 5, 2, 6, 5, 3, 6, 5, 2 (promedio **4.50**); sin los tres días malos, el promedio es 38 / 7 = **5.43**.

**El registro de un toque.** La notificación del diario trae «**Día difícil: solo un número**»: una sola escala de valencia que guarda la del día con `dia_dificil = 1` (`QuickActivity.Minimo`). En el ejemplo, esos tres números devuelven el promedio a 4.50. **[Diseño propio]** con buen fundamento. Además, Hoy muestra como pendientes los días de los 7 anteriores sin valencia ni diario (`Pendientes.faltantes`).

**Editar después.** Hasta el esquema 13 un registro de ánimo corregido no guardaba que fue editado, y un dato momentáneo podía volverse retrospectivo sin que se supiera (H-2-9). Desde el 14, cada corrección guarda `ts_editado` y cada registro nuevo `ts_creado`; en R, `vm_retraso()` dice qué se anotó tarde (Apéndice C).

---

## 3.15 Seguimiento privado (solo la mecánica)

Tres registros: **impulso** (0 a 5 en el día; momentos de 1 a 5), **recaída** (sí o no; momentos con hora, duración, detonantes, aparato y qué hacías antes) y **libido** (0 a 5 y foco), con anclas propias. Desde el módulo 10a, junto con los medicamentos, se anotan y se ven solo en la pantalla Privado y no entran en los análisis de la app, pero **sí** en el panel que se exporta a R (`n_recaida`, `recaida_min`, `n_impulso`…), para entender qué días y contextos lo hacen más difícil (capítulo 10). **[Diseño propio]**. Efectos posibles, sin comprobar: menos reactividad y más datos faltantes.

---

## 3.16 Una medida que no es autorreporte: la prueba de reacción

`PantallaReaccion` hace **10 intentos**: tras una espera al azar de **1.5 a 4.0 s** aparece un círculo que hay que tocar lo antes posible. Guarda la media, la mediana, los **lapsos** (respuestas de **500 ms o más**) y las **anticipaciones** (toques antes del círculo o, desde 10c, a menos de 100 ms de él; el intento se repite).

Es una versión muy corta de la **prueba de vigilancia psicomotora (PVT)**, la medida estándar de cómo la falta de sueño afecta la atención: 10 minutos, esperas de 2 a 10 s y lapsos de 500 ms o más (Basner y Dinges, 2011). La versión breve validada (PVT-B) dura 3 minutos, con esperas de 1 a 4 s y un umbral de lapso de 355 ms (Basner, Mollicone y Dinges, 2011). La de la app dura unos 36 s en promedio, no los «unos 30 segundos» que dice (H-2-12, cálculo propio), y mide en una pantalla táctil con retrasos que varían entre teléfonos. **[Aproximación]**: útil para tendencias grandes, comparándote contigo mismo. `rt_media` es el promedio de las medias de las pruebas del día.

---

## 3.17 En resumen

1. El ánimo se mide en **dos ejes** (valencia y activación) y de **tres formas**: en el momento, al recordar el día y como creencia general. Tienen sesgos distintos y **no son intercambiables**.
2. Los avisos usan **muestreo aleatorio estratificado**: 3 al día por omisión, uno por tramo de 10:00–21:00, con margen de mín(45 min, tramo/4). Desde el esquema 13 hay `tasa_respuesta` (H-2-3).
3. `muestreo_valencia` es la medida más limpia del ánimo momentáneo; los registros propios se sesgan hacia los eventos.
4. El hallazgo más grave era que **los cuestionarios se asignaban a días de otras semanas** (H-2-1); desde 10c cada puntaje cubre solo los días que pregunta.
5. En 10c dejaron de codificarse como 0 varios «no respondí» (H-2-4), y el perfil nuevo ya no se proyecta hacia atrás (H-2-5); `retroactivo` usa `ts_valencia` desde el esquema 13 (H-2-2).
6. El **registro de un toque** es una buena defensa contra los días malos sin registrar (MNAR).
7. Los cuestionarios están bien puntuados, pero WHO-5 y PSS-4 usan 7 días en vez de su periodo original, y algunos cortes no se pudieron verificar.
8. De los 15 hallazgos del módulo 2 ya no queda ninguno pendiente: H-2-10 se resolvió en 10e (D.94), y H-2-5, H-2-6, H-2-13 y H-2-15, en parte.
