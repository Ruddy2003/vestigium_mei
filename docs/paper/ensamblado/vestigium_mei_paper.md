---
title: "Vestigium Mei"
subtitle: "Qué hace la app, cómo y por qué: el paper metodológico"
date: "Versión de la app: v92 (esquema 15), más el Ideario (esquema 16) y las pantallas de captura (esquema 17) en los capítulos que las describen · 3 de noviembre de 2026"
lang: es-MX
toc-title: "Índice"
---



# Resumen {.unnumbered}

Vestigium Mei es una app de Android para registrar, durante toda la vida, el rastro de cada día de una sola persona. Combina datos que se escriben (valencia y activación con avisos al azar y un diario nocturno; cuestionarios validados; personas nombradas con @), datos que el teléfono y el reloj miden solos (pantalla, ubicación, sueño, pasos) y archivos importados. Todo se resume en un panel de un renglón por día, de 05:00 a 04:59 (salvo algunas fuentes, por día de calendario), que distingue «cero» de «sin dato» con una sola regla. La app cruza cualquier variable con cualquier otra: quita tendencias restando el promedio de ±10 días, descuenta que los días se parecen con un número efectivo de días y corrige por miles de comparaciones con Benjamini-Hochberg. Ofrece experimentos personales con semanas sorteadas por pares, hipótesis sellada e intervalos corregidos por autocorrelación, y exporta todo a R, que repite los análisis. Este documento describe cada función según el código de la versión 92 (esquema de datos 15) con su base científica y sus límites, y separa lo validado de lo aproximado y lo propio. Las revisiones anotaron 166 problemas metodológicos: 134 se resolvieron, 31 en parte y 1 sigue pendiente. Las conclusiones posibles son descriptivas y de asociación dentro de la persona, salvo las de los experimentos, causales sin cegamiento. Las limitaciones principales son los datos faltantes no al azar, variables que miden algo distinto de su nombre, la reactividad y suponer que las relaciones no cambian con los años. Se propone confirmar cada hallazgo con datos nuevos.


# Capítulo 1. Introducción y marco teórico

> **Para qué sirve este capítulo.** Explica **qué es Vestigium Mei, qué tipo de conocimiento puede dar y por qué está construida como está**. Sitúa la app en tres tradiciones de investigación: el autoseguimiento cuantificado (*Quantified Self*), la ciencia de una sola persona (diseños idiográficos y de caso único) y la medición en la vida diaria. Después discute dos decisiones de fondo que atraviesan todos los capítulos: que sea un sistema **para toda la vida** y no para una etapa, y que **todo pueda cruzarse con todo**, con su costo en comparaciones múltiples. Al final explica cómo está organizado el resto del documento.
>
> **Qué versión describe.** Todo el paper describe el código de la **v92**, esquema de datos `SCHEMA_V = 15`: la v90 (commit `8818d5a`), después de las entregas 10a a 10d del módulo 10 y de la revisión de navegación (entregas N0 a N7), más la entrega **10e**, que después de la lectura final arregló los hallazgos que tenían solución en el código (la v91), y la **10f**, que aplicó las decisiones del autor sobre los hallazgos que quedaban pendientes (la v92). Lo que cambió se marca «desde 10e» o «desde 10f». Los capítulos 2 a 10 se escribieron sobre la v81 y se actualizaron en cada entrega; cada cambio está en el Apéndice D con su motivo. Los ejemplos numéricos son **inventados**; ningún dato real aparece aquí.
>
> **Cómo leerlo.** Igual que el resto del paper: lenguaje sencillo, pero las cifras y reglas del código van tal cual, con el nombre de la función de donde salen. Cada decisión se marca como **[Validado]**, **[Aproximación]** o **[Diseño propio]** (sección 1.8). Lo que no se pudo comprobar se marca **[sin verificar]**.

## 1.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Autoseguimiento** (*self-tracking*) | Registrar de forma sistemática datos sobre uno mismo: ánimo, sueño, pasos, gastos. Puede ser a mano (un diario) o con sensores (un reloj). |
| **Quantified Self** | El nombre con que se conoce a la comunidad y la práctica de medirse a uno mismo con números, sobre todo con tecnología (Swan, 2013). |
| **Nomotético** | Que busca leyes que valgan **para la gente en general**: «en promedio, quien duerme más está de mejor ánimo». Es el enfoque habitual de la psicología y la medicina. |
| **Idiográfico** | Que busca entender **a una persona concreta**: «en mis días, ¿dormir más va con mejor ánimo?». Es el enfoque de esta app. |
| **Entre personas / dentro de la persona** | Una relación **entre personas** compara a unas personas con otras (Ana duerme más que Beto). Una relación **dentro de la persona** compara los días de alguien consigo mismo (Ana duerme más los sábados que los martes). Pueden ir en direcciones distintas (sección 1.4.2). |
| **Diseño de caso único / N-de-1** | Un estudio con **una sola persona** que se observa o se compara consigo misma muchas veces. «N» es el número de participantes; aquí, 1 (capítulo 8). |
| **Medición en la vida diaria** | Preguntar o medir **mientras la persona vive su vida**, no en un laboratorio ni de memoria: avisos en el teléfono, sensores, diarios. En la literatura se llama *ambulatory assessment* o *ecological momentary assessment* (Trull y Ebner-Priemer, 2013; Shiffman et al., 2008). |
| **Comparaciones múltiples** | Revisar muchas preguntas a la vez. Cuantas más se revisan, más «hallazgos» salen por puro azar (sección 1.6). |
| **Estacionario** | Un proceso cuyas reglas no cambian con el tiempo: su promedio, su variabilidad y sus relaciones son las mismas este año que hace cinco. Una vida casi nunca lo es del todo. |

: Tabla 1.1. Mini-glosario

## 1.2 Qué es Vestigium Mei, en una página

Vestigium Mei («mi rastro», en latín) es una app de Android, escrita en Kotlin con Jetpack Compose, **con un solo usuario: su autor**. Su propósito es registrar, a lo largo de los años, el rastro de cada día y poder preguntarle a ese registro cosas sobre uno mismo. Todo vive en el teléfono (capítulo 2) y sale solo por los respaldos que el usuario activa y por consultas puntuales a servicios externos (capítulo 10).

**Tres capas de datos** (capítulo 2, sección 2.2):

- **Activa**: lo que escribes o marcas. La valencia y la activación del día (escalas de 1 a 7), los avisos de ánimo al azar (3 al día por omisión entre las 10:00 y las 21:00), los registros de ánimo por iniciativa propia, el diario con @personas y #temas, cuestionarios validados, sueños, gastos, hábitos (capítulo 3).
- **Pasiva**: lo que el teléfono y el reloj miden solos. Tiempo de pantalla, ubicación y lugares, pasos y sueño del reloj por Health Connect, clima, luz, ruido, notificaciones (capítulo 4).
- **Importada**: archivos que cargas. Spotify, Google Takeout, WhatsApp (solo conteos), MacroFactor, Kindle, Cold Turkey (capítulo 4).

**Una unidad de análisis: el día.** Todo se resume en el **panel diario**, una tabla con un renglón por día y una columna por variable (`Estadistica.panel`). El día no va de medianoche a medianoche, sino de **las 05:00 a las 04:59** del día siguiente (el *día lógico*, `Fechas.diaLogico`), para que la madrugada cuente con el día que se estaba viviendo. Algunas variables de fuentes externas siguen yendo por día de calendario, y la exportación lo dice en cada una (capítulo 2, sección 2.3.2). Además de 193 variables fijas desde el esquema 17 (las que `Estadistica.variables()` escribe con su nombre; 60 de ellas solo existen si hay datos de su fuente; capítulo 2, sección 2.7, y Apéndice A.1), la app crea **variables dinámicas**: una por cada persona, tema, lugar, app, artista, nutriente o etapa de vida que aparece en tus datos (27 prefijos; capítulo 2, sección 2.7).

**Lo que la app devuelve:**

- **Análisis**: relaciones entre cualquier par de variables, quitando tendencias y corrigiendo por días parecidos y por muchas comparaciones; herramientas para un objetivo concreto, un modelo con varias causas posibles, dinámica emocional, ritmo de sueño, días atípicos (capítulo 6).
- **Experimentos personales**: semanas con y sin una acción, sorteadas, con la hipótesis sellada antes de empezar y, desde 10f, si se quiere, también el criterio para decidir si funcionó (capítulo 8).
- **Reportes, «Tu año», logros y campañas**: resúmenes y un sistema de motivación (capítulo 9).
- **Exportación**: 51 tablas CSV, un diccionario de variables y un script de R (`vestigium.R`) que repite los análisis de la app (Apéndices A y C).

**Lo privado.** Los impulsos, las recaídas, la libido, los detonantes, los medicamentos y el hábito ligado a las recaídas se anotan y se ven **solo en la pantalla Privado** y viajan completos a R; no aparecen en ninguna otra pantalla ni análisis de la app (capítulo 10, sección 10.3). En este paper se describen solo en su mecánica.

## 1.3 El autoseguimiento y el autoconocimiento a largo plazo

### 1.3.1 De dónde viene

Medirse a uno mismo no es nuevo: los diarios personales, el registro de la temperatura para conocer el ciclo menstrual o la libreta de un deportista lo son. Lo nuevo de las últimas dos décadas es la **escala**: un teléfono y un reloj registran sin esfuerzo miles de datos al día, y esa facilidad dio lugar a la comunidad conocida como **Quantified Self**. Swan (2013) la describe como el movimiento de personas que registran por su cuenta información biológica, física, conductual o ambiental sobre sí mismas, y señala que sus datos, aunque recogidos por individuos, tocan problemas de la ciencia de grandes datos y del descubrimiento biológico. No releí en esta sesión los detalles históricos que da sobre el origen del nombre **[sin verificar]**, así que no los repito.

Para entender qué hace una persona con sus datos, la investigación en interacción humano-computadora propuso modelos por etapas. El de Li, Dey y Forlizzi (2010) distingue cinco: **prepararse** (decidir qué medir), **recolectar**, **integrar** (juntar fuentes), **reflexionar** y **actuar**; cada etapa tiene sus propias barreras, y un problema en una arrastra a las siguientes. Choe et al. (2014), entrevistando a personas que se miden de forma intensiva, encontraron que suelen registrar demasiadas cosas a la vez y luego no saben cómo sacar conclusiones confiables de ellas. Epstein et al. (2015) añadieron que el autoseguimiento real no es lineal: la gente **deja de registrar, retoma, cambia de herramienta** y lo hace con distintos fines a lo largo del tiempo («informática vivida», *lived informatics*). **[Validado]**: estos son modelos descriptivos bien establecidos en ese campo.

### 1.3.2 Dónde se sitúa la app

Leída con esos modelos, Vestigium Mei toma decisiones claras en cada etapa:

| Etapa (Li et al., 2010) | Barrera típica | Qué hace la app | Capítulo |
|---|---|---|---|
| Prepararse | No saber qué medir | Mide mucho por omisión y deja que las preguntas surjan de los datos; el principio de «todo con todo» (sección 1.6) | 2, 6 |
| Recolectar | Cansancio, olvido, días malos sin registro | Captura pasiva; avisos al azar; registro de un toque para los días difíciles; «Ponerte al día» | 3, 4 |
| Integrar | Fuentes con formatos y días distintos | Un solo panel por día lógico; importadores propios | 2, 4 |
| Reflexionar | No saber interpretar | Análisis con protecciones estadísticas, reportes, «Tu año» | 6, 9 |
| Actuar | No saber si un cambio funcionó | Experimentos personales sorteados | 8 |
| (Abandono, Epstein et al., 2015) | Dejar de registrar | Constancia en lugar de rachas, campañas, recordatorios | 9 |

: Tabla 1.2. Dónde se sitúa la app

La respuesta a la barrera de Choe et al. (2014) es la parte más ambiciosa de la app: en lugar de dejar que la persona saque conclusiones a ojo, el capítulo 6 describe un análisis que quita tendencias, descuenta que los días se parecen entre sí y corrige por las miles de comparaciones. **[Diseño propio]** la combinación; cada pieza está **[Validado]** en la literatura estadística (capítulo 6).

### 1.3.3 Para qué sirve conocerse con datos

La promesa del autoseguimiento es el **autoconocimiento**: saber cosas de uno mismo que la memoria y la intuición no dan. Hay razones sólidas para esperar que los datos ayuden:

- **La memoria resume mal.** Al recordar un periodo pesamos de más el peor momento y el final, y casi ignoramos la duración (Kahneman et al., 1993); cuanto más lejos está lo que se recuerda, más se apoya el recuerdo en creencias generales y menos en lo vivido (Robinson y Clore, 2002). Un registro hecho en el momento no tiene ese sesgo (capítulo 3, sección 3.4).
- **Lo que creemos que hacemos difiere de lo que hacemos.** Lo que la gente dice sobre su uso del teléfono se parece solo moderadamente a lo que registran los aparatos (Parry et al., 2021; capítulo 4).
- **Los patrones lentos no se ven a simple vista.** Un cambio de ánimo de medio punto a lo largo de un otoño, o una relación entre el sueño y el ánimo que solo aparece con desfase de un día, no se perciben sin datos.

Pero hay que ser honestos sobre la evidencia: **no conozco un estudio que muestre que llevar un registro tan amplio como este mejore el bienestar** de quien lo hace; lo que sí está documentado es que el autorregistro **cambia** la conducta registrada (Korotitsch y Nelson-Gray, 1999; capítulo 9). El valor del sistema depende de lo bien que distinga lo que los datos muestran de lo que parecen mostrar, y ese es el tema de todo el paper.

## 1.4 Ciencia de una sola persona

### 1.4.1 Dos maneras de hacer preguntas sobre la gente

La mayor parte de la psicología y la medicina es **nomotética**: estudia a muchas personas para encontrar lo que vale en promedio. Su conclusión típica es «en la población, más sueño se asocia con mejor ánimo». Una persona concreta quiere saber otra cosa: «**en mí**, ¿dormir más va con mejor ánimo?». Esa pregunta es **idiográfica**.

Se suele suponer que la respuesta de grupo sirve para el individuo. Molenaar (2004) mostró que eso solo es cierto bajo una condición matemática fuerte, la **ergodicidad**: que todas las personas sigan el mismo proceso (homogeneidad) y que ese proceso no cambie con el tiempo (estacionariedad). Si no se cumple, lo que se aprende comparando personas **no dice nada seguro** sobre cómo funciona cada una a lo largo de sus días. Fisher, Medaglia y Jeronimus (2018) lo comprobaron con datos: en varios conjuntos de mediciones intensivas, las estimaciones obtenidas del grupo aproximaron mal la variabilidad y las relaciones de cada participante. **[Validado]**: es el argumento central de la investigación idiográfica moderna.

### 1.4.2 Un ejemplo de por qué importa (Figura 1.1)

Seis personas inventadas, 60 días cada una. Entre personas, quien duerme más en promedio está de mejor ánimo en promedio: la recta que une sus promedios sube **+0.55 puntos de valencia por hora de sueño**. Pero dentro de una de ellas («tú»), las noches en que duerme más que su propio promedio van con días **algo peores**: **−0.34 puntos por hora**. Para esta persona, quizá dormir más es señal de que estaba enferma o de que algo la tumbó; lo que sea, la regla del grupo no la describe.

![Figura 1.1. Seis personas sintéticas: relación entre personas (azul, discontinua) y relación dentro de una persona (naranja). Script: `figuras/fig30_dentro_entre.py`.](figuras/fig30_dentro_entre.png){width=100%}

Por eso, el panel de la app compara **tus días entre sí**, y no te compara con normas de población. Donde la app usa normas (los puntos de corte del WHO-5, los umbrales de Cohen, la «recomendación» de 7 h de sueño), los capítulos lo señalan como un préstamo que hay que leer con cuidado (capítulos 3, 6, 8 y 9).

### 1.4.3 Medir en la vida diaria

La fuente de datos de la ciencia idiográfica son las **mediciones intensivas**: muchas observaciones de la misma persona, tomadas mientras vive. Trull y Ebner-Priemer (2013) reúnen bajo el nombre de *ambulatory assessment* los autorreportes en el momento, las mediciones fisiológicas y las conductuales hechas con aparatos en la vida diaria. El **muestreo de experiencia**, que pregunta varias veces al día en momentos al azar, es la parte de autorreporte (Csikszentmihalyi y Larson, 1987; Shiffman et al., 2008). Hamaker y Wichers (2017) explican que estos datos permiten estudiar **dinámicas dentro de la persona**: cómo un estado de hoy se relaciona con el de mañana, qué tan persistente es el ánimo, qué lo mueve. La app combina las tres fuentes (avisos al azar, reloj y teléfono) en un solo panel. **[Validado]** el enfoque.

### 1.4.4 Experimentar en uno mismo

Observar no basta para saber qué **causa** qué. Los **ensayos N-de-1** resuelven eso para una persona: se alternan al azar periodos con y sin una acción y se compara (Guyatt et al., 1986; Kravitz et al., 2014; capítulo 8). En medicina se han propuesto como la forma de elegir el tratamiento de cada paciente, porque muchos tratamientos solo funcionan en una parte de quienes los reciben (Schork, 2015). En psicología existe la misma idea como **diseños de caso único**, con un llamado a sortear las condiciones para que sean más creíbles (Kratochwill y Levin, 2010).

La autoexperimentación también tiene una tradición más informal: Roberts (2004) describe diez ideas sobre sueño, ánimo, salud y peso que le surgieron de experimentar en sí mismo durante años. Su trabajo ilustra el valor del autoexperimento para **generar** hipótesis, y también su límite: con una sola persona, que además sabe lo que espera, el resultado vale para esa persona y es vulnerable al efecto expectativa (capítulo 8, sección 8.10). Daza (2018) propuso un marco formal para hacer inferencia causal con series de autoseguimiento, que deja explícitos los supuestos que hacen falta para pasar de una asociación a un efecto. **[Validado]** la lógica de los N-de-1; **[Aproximación]** su versión en la app, por la falta de cegamiento y las demás limitaciones del capítulo 8.

### 1.4.5 Tres tipos de pregunta y la herramienta de cada una

| Pregunta | Ejemplo | Herramienta de la app | Qué permite decir |
|---|---|---|---|
| **Descriptiva**: ¿cómo soy? | ¿Cuánto duermo? ¿Cómo varía mi ánimo? | Tus números, Totales, Dinámica emocional, Ritmo, reportes | Cómo fueron tus días, si las mediciones son válidas (capítulo 11, sección 11.3) |
| **De asociación**: ¿qué va con qué? | ¿Los días que camino más estoy mejor? | Las relaciones más marcadas, Cruzar dos variables, Todo lo que se mueve con algo, Modelo múltiple | Qué se mueve junto **en tus días**, descontando el azar y las tendencias; no qué causa qué |
| **Causal**: ¿qué me cambia? | ¿Dejar la cafeína de la tarde mejora mi sueño? | Experimentos personales | Si hacer esto, en este periodo y sabiendo que lo haces, cambió tu medida |

: Tabla 1.3. Tres tipos de pregunta y la herramienta de cada una

El capítulo 11 (sección 11.2) vuelve sobre esta tabla para decir con precisión qué puede y qué no puede concluirse en cada fila.

## 1.5 Por qué un sistema para toda la vida y no para una etapa

### 1.5.1 La decisión

La mayoría de los estudios de muestreo de experiencia duran una o dos semanas, y un ensayo N-de-1, unas semanas o meses. Vestigium Mei está pensada para **no terminar**. Eso se ve en el diseño: las marcas de tiempo completas, el esquema de documentos que crece sin migraciones, la versión de esquema en cada día y en cada respaldo, el respaldo cifrado y verificable (capítulos 2 y 10). **[Diseño propio]**.

### 1.5.2 Razones a favor

1. **Potencia.** Con pocos días solo se ven relaciones enormes. Para una sola pregunta y días independientes, con potencia de 80 % y α = 0.05, con 90 días se detecta una correlación de **0.29**; con 365, de **0.15** (capítulo 6, sección 6.21; fórmula B.15). Si los días se parecen a sus vecinos (autocorrelación 0.5 en las dos variables), hacen falta más: **0.37** con 90 días y **0.19** con 365. Y en la lista de relaciones, que revisa miles de parejas, el umbral efectivo se parece al de α = 0.001: con esa misma autocorrelación, **0.52** con 90 días y **0.27** con 365. Solo un registro de años permite ver relaciones moderadas en la lista completa.
2. **Lo que pasa poco.** Viajes, enfermedades, mudanzas, rupturas, una persona que ves una vez al mes: para tener 5 días con una persona (el mínimo de `per_`) o 60 días antes y 60 después de un cambio de vida («Antes y después», capítulo 8, sección 8.11), hace falta tiempo.
3. **Los ciclos lentos.** Las estaciones dan un solo ciclo al año. Para separar «el invierno» de «lo que me pasó ese invierno» hacen falta varios inviernos.
4. **Tu propia normalidad.** Los días atípicos se juzgan contra tu mediana (`Analisis.atipicos`, al menos 20 días por variable), la variabilidad del corazón contra tu línea base (capítulo 4, sección 4.5.2) y las cifras de los modelos fisiológicos sirven para compararte contigo mismo (capítulo 5). Una línea base larga es más estable.
5. **La memoria se pierde.** Lo que no se registró en su momento no se recupera después con la misma calidad (sección 1.3.3).

### 1.5.3 Costos

Un registro de por vida trae problemas que un estudio de dos semanas no tiene:

- **Cambia el instrumento.** En una década cambian el teléfono, el reloj, los permisos de Android y la propia app. Para comparar mediciones de distintas épocas hace falta que midan lo mismo de la misma manera, lo que en psicometría se llama **invarianza de la medición** (Meredith, 1993, para grupos; la idea se aplica igual entre periodos). La app ya tiene varias costuras así, todas marcadas: la ventana de pantalla y pasos (`ventana_pantalla`, `ventana_pasos`, esquema 13), el orden de la hoja de la noche (`version_noche`, 1 y 2), los avisos de ánimo contados solo desde el esquema 13 y las marcas `ts_creado` y `ts_editado` de momentos, gastos, ingresos, consumos, bloques del cronómetro y tomas solo desde el 14 (los días ya las tenían; Apéndice D), y, desde el 15, los días con cambio de horario (`cambio_horario`) y la fuente del clima (`clima_reanalisis`). Otras no dejan marca: cambiar la vida media de la cafeína reescribe todo el pasado (H-4-11). Para la definición de cada variable, desde 10f el diccionario tiene las columnas `desde_version` y `cambio` (H-1-8, D.115) (capítulo 2, sección 2.8).
- **Cambias tú.** Una relación entre el sueño y el ánimo puede ser distinta a los 25 y a los 35 años. Si el proceso no es estacionario, mezclar toda la historia en un solo número la promedia. La lista de relaciones usa **todos** los días del panel (`PantallaExplorar` llama a `Estadistica.relaciones` con el panel completo); quitar la tendencia corrige los **niveles** que cambian lento, pero no una **relación** que cambia con los años (capítulo 11, secciones 11.2.3 y 11.4; H-11-1).
- **El registro mismo cambia lo registrado,** y ese efecto se acumula y se mezcla con lo que se quiere medir (capítulo 3, sección 3.14, y capítulo 11, sección 11.5).
- **Se abandona.** Epstein et al. (2015) muestran que dejar de registrar y retomar es lo normal. Un hueco largo no es «nada pasó» (capítulo 2, sección 2.6; H-1-1 en su parte de abandono).
- **Los datos se pueden perder** en un cambio de teléfono o una restauración mal hecha (capítulo 10).

**Cómo lo enfrenta la app.** Guardar el dato crudo con su hora y calcular después, salvo en las fuentes que se guardan ya sumadas por día (capítulo 2, sección 2.1), marcar la versión de cada día y de cada respaldo, guardar la ventana de cada día cuando una regla cambia y documentar cada cambio con su versión en el Apéndice D. Es lo que en el capítulo 2 se llama principio 3, «conservar el dato crudo y derivarlo después». **[Diseño propio]**, coherente con los principios FAIR de reutilización de datos (Wilkinson et al., 2016) aplicados a una sola persona.

## 1.6 Todo con todo, y su costo

### 1.6.1 El principio

La app no decide de antemano qué preguntas son interesantes: **cualquier variable registrada puede compararse con cualquier otra** (capítulo 6, sección 6.3.3). Cada persona que nombras, cada app que usas, cada nutriente, cada lugar se vuelve una columna y entra en la lista de relaciones. La razón es la del modelo de Li et al. (2010): al empezar no se sabe qué conviene medir, y las relaciones inesperadas son justo las que la intuición no da. **[Diseño propio]**.

### 1.6.2 El costo

Revisar todo contra todo tiene un precio que crece muy rápido (Figura 1.2, izquierda). Con *p* variables hay *p*(*p* − 1)/2 parejas: con 50 variables, 1 225; con 150, 11 175; con 300, **44 850**. Si ninguna tuviera relación real y se usara el umbral habitual p < 0.05, saldrían por puro azar unas **61**, **559** y **2 242** «relaciones».

La app se defiende con varias capas (capítulo 6):

1. **Benjamini-Hochberg** (`Analisis.bh`): solo marca como firmes las relaciones con q < 0.10, lo que mantiene la **proporción esperada** de falsos descubrimientos entre las marcadas alrededor del 10 % (Benjamini y Hochberg, 1995).
2. **Las parejas obvias** (`Estadistica.obvia`, diez reglas) no se cuentan: calorías con proteína, una app con el total de pantalla. Así no ocupan lugares ni gastan corrección.
3. **Topes por familia**: entran las 15 personas más mencionadas (con al menos 5 días), las 12 apps con más minutos, etc. (capítulo 6, sección 6.3.3).
4. **Quitar la tendencia** y el **número efectivo de días**, para que el p de cada pareja no sea más pequeño de lo que debe.

El precio de esas defensas es la **potencia**: lo que se sostiene depende de cuántas cosas se miraron (Figura 1.2, derecha). Una pareja con 100 días independientes necesita |*r*| ≥ **0.20** si fue la única pregunta hecha de antemano, pero ≥ **0.45** si es la única relación real entre 44 850 parejas revisadas (le hace falta p < 0.10 / 44 850 ≈ 2.2 × 10⁻⁶). Con 365 días independientes, **0.10** frente a **0.24**.

![Figura 1.2. Izquierda: parejas posibles y «relaciones» esperadas por azar según el número de variables. Derecha: la |r| que necesita la mejor pareja para sostenerse, según los días independientes y cuántas parejas se revisaron. Script: `figuras/fig29_todo_con_todo.py` (cálculo con la aproximación de Fisher de `Estadistica.relaciones`; sin datos).](figuras/fig29_todo_con_todo.png){width=100%}

### 1.6.3 Lo que la corrección no cubre

Benjamini-Hochberg cuenta las parejas **de una lista**. No cuenta las otras maneras de mirar los mismos datos:

- La lista se puede ver **con o sin tendencia**, **por día o por semana**, **con o sin las obvias** y con **ocho filtros por tema**; «Cruzar dos variables» tiene **desfase** de un día; «Todo lo que se mueve con algo» se puede abrir con cualquier variable como objetivo, y su corrección se hace solo entre las variables revisadas contra ese objetivo (capítulo 6, sección 6.13.5). En R, `privadas = TRUE` suma el seguimiento privado.
- Cada una de esas elecciones es una bifurcación. Gelman y Loken (2014) llaman a esto el **jardín de senderos que se bifurcan**: aunque nadie busque a propósito un resultado, cuando el análisis se elige después de ver los datos, la probabilidad de encontrar algo «firme» por azar es mayor que la que dice cada prueba. Simmons et al. (2011) mostraron cuánto puede crecer. Nada en la app registra cuántas de esas maneras se miraron (H-11-2).

Hay además un segundo efecto, más sutil: **la relación más fuerte de una lista larga casi siempre exagera su tamaño**, aunque sea real. Si entre muchas relaciones reales se escoge la que dio la *r* más grande, se escoge también la que tuvo más suerte; en la siguiente muestra, su *r* tenderá a bajar. Ioannidis (2008) documentó este efecto («la maldición del ganador») en la investigación biomédica, y es la misma regresión a la media que describen Barnett, van der Pols y Dobson (2005). Le pasa a «la relación del año» de «Tu año» (capítulo 9, sección 9.3.5) y a los extremos de «Palabras del diario» (capítulo 6, sección 6.17).

**[Validado]** que la corrección por comparaciones múltiples es necesaria y que BH es un método adecuado para explorar; **[Aproximación]** su garantía aquí, porque las pruebas comparten variables (capítulo 6, sección 6.8.3) y porque las bifurcaciones del análisis no entran en la cuenta.

### 1.6.4 Cómo leer la lista, en consecuencia

Ioannidis (2005) mostró que la probabilidad de que un hallazgo sea cierto depende de cuántas relaciones se prueban, de la potencia y de qué tan probable era de antemano. En una lista de miles de parejas con potencia modesta, una relación firme es **una buena pista, no una conclusión**. La app ya lo dice («Pistas para entenderte. Ninguna prueba causas»). El camino para pasar de pista a conclusión es el que se discute en el capítulo 11 (sección 11.7.4): confirmar la pista con datos que no se usaron para encontrarla, o con un experimento.

## 1.7 Qué no es este sistema

Para evitar malentendidos:

- **No es un instrumento clínico.** Los cuestionarios validados están adaptados (periodos de 7 días en WHO-5 y PSS-4; capítulo 3, sección 3.9) y los puntos de corte que muestra la app son orientativos.
- **No mide el cuerpo directamente en todo.** Cafeína, alcohol en sangre, reserva de creatina y niveles de suplementos son **estimaciones con parámetros de población**, no mediciones (capítulo 5).
- **No generaliza.** Lo que se encuentra vale para el autor, en el periodo analizado. No dice nada de otras personas (sección 1.4).
- **No es un estudio diseñado de antemano.** La mayoría de sus análisis son **exploratorios**: buscan pistas. Solo los experimentos personales tienen una hipótesis fijada antes de ver los datos y, desde 10f, pueden sellar con ella el criterio de decisión (capítulo 8).

## 1.8 Cómo se califica cada decisión

Cada decisión de diseño del paper lleva una de tres etiquetas:

- **[Validado]**: respaldada por literatura publicada o por un estándar. Ejemplo: el modelo circumplejo del afecto (Russell, 1980), la corrección de Benjamini-Hochberg, AES-256-GCM.
- **[Aproximación]**: razonable, pero con supuestos que conviene conocer. Ejemplo: tomar la luz alta del teléfono como señal de estar al aire libre; corregir por autocorrelación suponiendo que el parecido entre días decae como en un proceso AR(1).
- **[Diseño propio]**: una decisión de la app sin un estándar detrás; puede ser buena, pero no está probada. Ejemplo: el corte del día a las 05:00 (con un principio validado detrás, el día de 4:00 a 3:59 de la encuesta estadounidense de uso del tiempo; capítulo 2), el umbral de 0.12 para declarar «lenta» una variable.

Y una marca más: **[sin verificar]** señala lo que no se pudo comprobar en una fuente durante la sesión en que se escribió. No se presenta como hecho. La lista de lo que quedó sin verificar en cada capítulo está en `referencias.md`.

Esta clasificación sigue la regla del plan: **no presentar heurísticas como modelos validados**. Una buena parte de la app es diseño propio; eso no es un defecto, pero obliga a leer sus resultados con la confianza que corresponde a cada pieza.

## 1.9 Cómo está organizado el paper

| Capítulo | Tema | Pregunta que responde |
|---|---|---|
| 1 | Introducción y marco teórico | Qué es esto y qué tipo de conocimiento puede dar |
| 2 | Arquitectura y flujo de datos | Cómo se guarda cada dato, a qué día pertenece y cómo se distingue «cero» de «no sé» |
| 3 | Medición del estado subjetivo | Cómo se pregunta el ánimo, cuándo y con qué sesgos |
| 4 | Sensores, datos pasivos e importaciones | Qué mide el teléfono solo y qué tan confiable es |
| 5 | Modelos fisiológicos y variables derivadas | Qué cifras son cuentas con un modelo y no mediciones |
| 6 | Análisis estadístico | Cómo se pasa de miles de registros a «esto va con aquello», y cómo coinciden la app y R |
| 7 | Personas y red social | Cómo se cuentan las personas y qué miden la Constelación y la Red |
| 8 | Experimentos personales | Cómo probar algo en ti y qué tan confiable es el resultado |
| 9 | Reportes, visualización y motivación | Qué devuelve la app y cómo la motivación puede cambiar los datos |
| 10 | Privacidad, seguridad e integridad | Dónde viven los datos, cómo se protegen y cómo no se pierden |
| 11 | Discusión, limitaciones y trabajo futuro | Qué puede y qué no puede concluirse, y qué falta |
| Ap. A | Diccionario de variables | Cada variable del panel con su fuente y unidad |
| Ap. B | Fórmulas | Las fórmulas del capítulo 6, numeradas |
| Ap. C | Funciones de R | Qué hace cada función de `vestigium.R` y en qué difiere de la app |
| Ap. D | Cambios metodológicos aplicados | Cada cambio del módulo 10 y de la revisión de navegación, con su motivo y su versión |
| Ap. E | Glosario | Los términos de los mini-glosarios, en orden alfabético |

: Tabla 1.4. Cómo está organizado el paper

**Cómo se hizo.** El paper se escribió por módulos, cada uno en una sesión de trabajo con un modelo de Claude, leyendo el código de la app (el plan está en `PLAN_PAPER_METODOLOGIA.md`). Los módulos 1 a 8 describieron el código y anotaron problemas en `hallazgos.md` sin cambiarlo; el módulo 9 revisó todos los hallazgos y propuso cambios (`revision_propuestas.md`); el autor los aprobó, y el módulo 10 los aplicó en cuatro entregas, actualizando los capítulos; después de la lectura final (módulo 14), una quinta entrega, la 10e, arregló los que quedaban abiertos y tenían solución en el código, y una sexta, la 10f, aplicó lo que el autor decidió sobre los ocho que seguían pendientes (en siete cambió el código, por ejemplo para leer cada hora con la zona de entonces o para mostrar en la app la lista de servicios externos; en uno, H-8-6, decidió dejarlo como estaba). El módulo 11 escribió este capítulo y el 11; los módulos 12 a 14 ensamblaron, corrigieron y revisaron el documento. Por eso, muchos capítulos dicen «hasta la entrega 10c era así; desde entonces es así»: el paper documenta también **la historia de las decisiones**, que importa para leer datos viejos.

**Cómo se numeran los hallazgos.** Cada hallazgo lleva el número del **módulo** que lo encontró, no del capítulo: H-1-n son del capítulo 2; H-2-n, del 3; H-3-n, del 4, y así hasta H-7-n, del 8; H-8-n, de los capítulos 9 y 10; H-9-n, de la revisión general; H-11-n, de los capítulos 1 y 11, y H-NAV-n, de la revisión de navegación.

**Lo que no es.** No es un artículo para publicar ni una revisión sistemática de la literatura: las referencias se eligieron para respaldar o discutir decisiones concretas, y se buscaron en la sesión en que se citaron; las pocas que no se pudieron comprobar llevan «[sin verificar]» en `referencias.md`.

## 1.10 Resumen del capítulo

1. Vestigium Mei es un sistema de **autoseguimiento de por vida para una sola persona**: combina datos activos, pasivos e importados en un panel de un renglón por día lógico (de 05:00 a 04:59), y ofrece análisis, experimentos, reportes y una exportación completa a R.
2. Se sitúa en tres tradiciones: el **Quantified Self** (Swan, 2013; Li et al., 2010; Choe et al., 2014; Epstein et al., 2015), la **ciencia idiográfica** (Molenaar, 2004; Fisher et al., 2018) y la **medición en la vida diaria** (Trull y Ebner-Priemer, 2013; Shiffman et al., 2008), con los **ensayos N-de-1** para las preguntas causales (Guyatt et al., 1986; Kravitz et al., 2014).
3. Lo que vale para un grupo no tiene por qué valer para una persona (Figura 1.1); por eso la app compara tus días entre sí.
4. Un sistema **de por vida** da potencia, captura lo raro y lo lento y conserva una línea base propia; a cambio, el instrumento y la persona cambian con los años, y hay que marcar cada costura.
5. **Todo con todo** multiplica las comparaciones (44 850 parejas con 300 variables). La app corrige con Benjamini-Hochberg, las parejas obvias, los topes por familia, quitar la tendencia y el n efectivo; aun así, las bifurcaciones del análisis y la maldición del ganador quedan fuera de la cuenta. Una relación firme es una pista seria, no una conclusión.
6. Cada decisión se califica como **[Validado]**, **[Aproximación]** o **[Diseño propio]**, y lo no comprobado se marca **[sin verificar]**.
7. El módulo 11 anotó dos hallazgos nuevos: las relaciones se calculan con toda la historia, como si fueran las mismas en todas las etapas de la vida (H-11-1), y las maneras de mirar los datos no entran en ninguna corrección (H-11-2).


# Capítulo 2. Inventario y arquitectura de datos

> **Alcance.** Este capítulo describe *cómo se obtiene, se guarda, se asigna a un día y se organiza* cada dato de Vestigium Mei, y por qué cada decisión es razonable (o no) desde la metodología de investigación. No evalúa todavía las pruebas estadísticas (capítulos 6 en adelante). Se escribió sobre la v81 (commit `fa527df`, `SCHEMA_V = 12`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10a…10d» (o «desde el esquema 13»), lo de las entregas 10e y 10f, «desde 10e» y «desde 10f», y todo está en el Apéndice D. Los ejemplos numéricos son **sintéticos**: ningún dato real del usuario aparece aquí.
>
> **Convención de etiquetas.** Cada decisión de diseño se marca como **[Validado]** (respaldada por literatura o por un estándar establecido), **[Aproximación]** (razonable pero con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app sin referente directo; defendible, pero no es un estándar). Los hallazgos llevan el número del módulo que los encontró: los de este capítulo (módulo 1) son H-1-n; los H-9-n son de la revisión general. Están en `hallazgos.md`.

## 2.1 Propósito y principios de diseño

Vestigium Mei es un instrumento de **autoseguimiento cuantificado (quantified self, QS) de un solo sujeto** (diseño N-de-1): una persona produce, durante años, un registro denso de su ánimo, cuerpo, conducta, entorno y relaciones. La pregunta metodológica central es: *¿qué estructura de datos permite hacer inferencia razonable sobre un solo individuo, sin falsear lo que realmente se midió?*

La arquitectura responde con cinco principios:

1. **Un día es la unidad de análisis.** Casi todo se resume en una fila por día (el *panel diario*). Es la unidad natural del autoseguimiento: el sueño, el ánimo del día y el diario se organizan por día. *Razonamiento:* la resolución diaria equilibra ruido y señal; las mediciones intradía (avisos de ánimo, ubicación por hora) se conservan en tablas aparte para análisis más finos.
2. **No inventar datos.** Un dato ausente no se convierte en cero salvo que haya una razón estructural (sección 2.6). *Razonamiento:* confundir «no medido» con «cero» es uno de los errores más comunes en datos observacionales y sesga medias, correlaciones y modelos (Rubin, 1976; Little y Rubin, 2019).
3. **Conservar el dato crudo y derivarlo después.** Cada evento (momento, gasto, visita, punto de ubicación) se guarda con su marca de tiempo completa (ISO-8601 con desfase horario), y el panel, la semana y los resúmenes se calculan después, al analizar o exportar. *Razonamiento:* permite re-derivar con otra regla (por ejemplo, otro corte de día) sin perder información. *Límite:* varias fuentes pasivas se guardan ya sumadas por día (`PantallaDia`, `SaludDia`: pantalla, pasos y lo que Health Connect entrega por día); para ellas no se puede re-derivar, y por eso el cambio de ventana del esquema 13 parte la serie (sección 2.3.2).
4. **Esquema flexible que crece con los años.** Cada colección guarda documentos JSON; añadir un campo no obliga a migrar la base. *Razonamiento:* un instrumento personal evoluciona; la rigidez de un esquema relacional estricto lo volvería inusable.
5. **Todo es exportable y legible por otros programas.** Existen exportaciones en formato ordenado (*tidy*, Wickham, 2014), diccionario de variables y un script en R. *Razonamiento:* reproducibilidad y principios FAIR (Wilkinson et al., 2016) a escala individual.

## 2.2 Las tres capas de captura: activa, pasiva e importada

El origen de un dato determina su tipo de error, por lo que la app conserva esa distinción (Figura 2.1).

![Figura 2.1. Flujo de datos: de las tres capas de captura al almacén, al día lógico, al panel diario y a la exportación y los análisis. Esquema, sin datos. Script: `figuras/fig01_flujo_datos.py`.](figuras/fig01_flujo_datos.png){width=100%}

| Capa | Ejemplos | Cómo se genera | Error típico |
|---|---|---|---|
| **Activa** | Diario del día (valencia, activación, dolor, sueño, hábitos), avisos de ánimo («muestreo»), momentos, gastos, notas, cartas | La persona lo teclea o marca | Sesgo de recuerdo, sesgo de deseabilidad social, fatiga de registro, días sin llenar |
| **Pasiva** | Tiempo de pantalla, desbloqueos, pasos, sueño del reloj, ubicación, clima y calidad del aire, notificaciones, luz ambiental, conectividad | Sensores y APIs del teléfono, sin esfuerzo | Error de instrumento, cambios de permisos u hora, huecos por apagado o falta de permiso |
| **Importada** | Historial de YouTube/Google (Takeout), chats de WhatsApp, minutos de laptop (Cold Turkey), nutrición, respaldos | Archivos que la persona carga | Cobertura parcial, formato cambiante, diferencias de zona horaria entre fuentes |

: Tabla 2.1. Las tres capas de captura: activa, pasiva e importada

**Razonamiento científico.** Combinar medición subjetiva (activa) y objetiva (pasiva) es una práctica recomendada en psicología del autoseguimiento y en el muestreo de experiencias: lo pasivo reduce la carga y el sesgo de recuerdo, lo activo captura lo que ningún sensor ve (cómo se siente alguien). El método de **muestreo de experiencias** (*experience sampling*, ESM; Csikszentmihalyi y Larson, 1987) justifica los avisos de ánimo: preguntar *en el momento* reduce el sesgo retrospectivo frente al diario nocturno. **[Validado]** el principio; **[Diseño propio]** la frecuencia y el contenido exacto de los avisos.

**Cuándo falla.** Si dos fuentes miden lo mismo (valencia del diario vs. valencia media de los avisos), no son intercambiables: la app las guarda como variables distintas (`valencia`, `muestreo_valencia`, `animo_valencia`) justamente para poder comparar su concordancia.

### Ejemplo sintético

Un martes (día lógico 12): el diario nocturno marca `valencia = 5` (escala 1–7). Durante el día hubo tres avisos con valencias 4, 6 y 5. La app guarda `valencia = 5`, `muestreo_valencia = media(4, 6, 5) = 5.0`. Coinciden aquí; en otro día `valencia = 3` con `muestreo_valencia = 5.3` sugeriría que el recuerdo nocturno está dominado por el peor momento o por el final del día (la «regla del pico y el final», Kahneman et al., 1993). Esa diferencia **es un resultado de investigación**, no un error, y solo es visible porque ambas medidas se conservan.

## 2.3 Tiempo: marcas ISO y día lógico

### 2.3.1 Marcas de tiempo

`Fechas.ahoraIso()` genera `2026-10-16T08:42:10-06:00`: fecha y hora **locales con el desfase respecto a UTC**. Las fechas «de día» (sin hora) son `YYYY-MM-DD`. Desde el 6 nov 2026 (D.159) la hora lleva **siempre** segundos: antes, cuando eran :00, salía `2026-10-16T18:32-06:00` (así lo escribe `OffsetDateTime.toString()`), que también es ISO 8601 pero que Excel y algunos lectores de Python no leían. Lo guardado antes se queda como estaba; la app y `vestigium.R` leen los dos.

- *Qué hace:* guarda el instante exacto y a la vez la hora local con que la persona lo vivió.
- *Razonamiento:* el ISO-8601 con desfase es no ambiguo (identifica un instante físico) y conserva la hora «de reloj de pared», necesaria para ritmos circadianos (a qué hora se duerme, cuándo se usa el teléfono). Usar solo UTC borraría esa información; usar solo hora local sin desfase se volvería ambiguo en viajes y cambios de horario. **[Validado]** (práctica estándar de ISO 8601).
- *Cuándo falla:* hasta 10f, los eventos pasados se interpretaban con la zona **actual** del teléfono (`Fechas.zona()` = `ZoneId.systemDefault()`): después de un viaje, la hora de un evento antiguo tomado con otro desfase se reconvertía a la zona de hoy, y la hora de dormir o el día de una nota podían moverse artificialmente (H-1-7). Desde 10f se lee con la zona de entonces (lo que sigue).

**La hora local de entonces (desde 10f, D.109; H-1-7, resuelto).** Una *zona horaria* es la regla que convierte un instante en la hora del reloj de un lugar: «America/Mexico_City» da UTC−6 todo el año; «Europe/Madrid» da UTC+1 en invierno y UTC+2 en verano (el *horario de verano*). Desde 10f, la app sigue una regla: **cada instante se lee con la zona que tenía el teléfono en ese momento**, no con la de hoy.

- *Cómo.* La configuración guarda un historial, `Config.zonas`: una lista de `CambioZona(desde, zona)` («desde este instante, esta zona»). `Fechas.zonaEn(ms)` busca en esa lista (búsqueda binaria) la última zona que empezó antes del instante; antes del primer cambio guardado usa la primera zona de la lista, y sin historial, la de ahora. Con ella se calculan la hora (`Fechas.local`), el día de calendario (`diaDeMillis`), el día lógico (`diaLogicoDeMillis`) y la marca ISO (`isoDeMillis`). Al revés, para pasar una hora local a un instante (`Fechas.millisDe`) se usa la zona que regía ese día, y con ella los bordes de cada día: `inicioDiaLogico`, `finDiaLogico`, `inicioDia` y `finDia`. Una nota que ya trae su desfase se lee **con su propio desfase** (`Fechas.localDe`, que usan `horaDe` y las horas decimales de la exportación, `Exportar.horaDecimal`); solo las marcas en UTC («Z», que vienen de importaciones) se pasan a la zona de entonces. Lo leen así Health Connect, la pantalla, las fotos (con la hora de la cámara), WhatsApp, Kindle, la tabla por hora (`vm_horas.csv`), la cinta y la estadística. **[Validado]** el principio (es lo que el desfase de ISO 8601 permite conservar); **[Diseño propio]** cómo se arma el historial.
- *Cuándo se anota un cambio.* `Repo.registrarZona` compara la zona del teléfono con la última del historial y, si cambió, agrega una entrada con la hora de ahora. Corre al abrir la app, en la tarea de cada 15 minutos (`Pulso`, en `avisos/Trabajo.kt`) y cuando Android avisa que cambió la zona (`TIMEZONE_CHANGED`). El horario de verano no necesita entrada: la zona «Europe/Madrid» ya sabe cuándo cambia.
- *La primera vez (lo guardado antes de 10f).* No había historial, así que se arma con las marcas de momentos, respuestas, pruebas de reacción y tomas de suplementos, ordenadas en el tiempo (sin las que están en UTC). Una marca «delata» otra zona si su desfase no es el que la zona de hoy daría en ese instante. Un periodo con otro desfase empieza solo si **dos marcas seguidas** traen ese mismo desfase; una suelta (por ejemplo, una nota corregida estando de viaje) no basta. Ese periodo queda con el desfase fijo de las notas (por ejemplo, «−05:00»), y todo lo demás, con la zona de hoy. **[Diseño propio]**.
- *Ejemplo sintético.* Vives en la Ciudad de México (UTC−6) y pasas una semana en Madrid (UTC+2 en verano). Una toma de cafeína anotada allá se guardó como `2026-07-04T08:00:00+02:00`. Antes de 10f, al volver, la app la leía en UTC−6: las 00:00 del 4 de julio, y las cuentas que sacan el día del instante (`diaLogicoDeMillis`, como la última cafeína del día, `cafeina_ultima_h`) la ponían en el día lógico del **3**, con hora 24.0. Desde 10f se lee a las 08:00, la hora que viviste, y cuenta para el día lógico del **4**, con hora 8.0.
- *La marca del cambio de horario (D.110).* `vm_panel.csv` trae `desfase_utc_h`, las horas de diferencia con UTC a mediodía de ese día (`Fechas.desfaseH` de las 12:00, con la zona de entonces: −6 en la Ciudad de México), y `cambio_horario` (0/1): 1 si el desfase al empezar el día lógico (`inicioDiaLogico`) no es el de su último instante (`finDiaLogico` − 1 ms) (`Fechas.cambioDeHora`). Un día así no dura 24 horas: dura 24 − (desfase final − desfase inicial) (fórmula B.24). La pantalla del Día lo dice debajo de la fecha: «cambio de horario: de UTC−6 a UTC+2 · el día duró 16 h». El día del cambio al horario de verano dura 23 horas, y el de regreso, 25. *Por qué importa:* un día de 16 o de 25 horas tiene menos o más minutos posibles de pantalla, pasos o sueño; esas sumas no son comparables con las de un día normal, y en R se pueden separar con `cambio_horario`. **[Diseño propio]**.
- *Cuándo falla.* (a) Lo que el teléfono ya guardó **sumado por día** (sueño, pasos, pantalla, ubicaciones) no se recalcula: si viajaste antes de 10f, se guardó con la zona que tenía entonces el teléfono, y así se queda; lo que cambia es lo que la app saca de un instante al leer (las horas del panel, la tabla por hora). (b) El historial reconstruido solo ve los periodos en que anotaste algo: entre que llegaste y la primera de esas dos notas se usa la zona de hoy, y un periodo con desfase fijo no sigue un horario de verano que haya ocurrido dentro de él. (c) `cambio_horario` solo compara el principio y el fin del día lógico: un viaje de ida y vuelta el mismo día da 0. (d) No se probó en un teléfono que Android entregue el aviso `TIMEZONE_CHANGED`; si no llegara, el cambio se anota en la siguiente tarea de 15 minutos o al abrir la app, y lo de ese rato queda con la zona anterior.

### 2.3.2 Día lógico (corte a las 05:00)

```kotlin
fun diaLogico(ahora: LocalDateTime = LocalDateTime.now()): String {
    val d = if (ahora.hour < 5) ahora.toLocalDate().minusDays(1) else ahora.toLocalDate()
    return d.format(ISO_DIA)
}
```

*Qué hace:* un evento entre las 00:00 y las 04:59 se asigna al **día anterior**.

*Razonamiento científico.* La medianoche no es una frontera natural de la conducta: quien se acuesta a las 01:30 «todavía está viviendo» el día anterior. La *American Time Use Survey* usa un **día-diario que va de las 4:00 a.m. a las 3:59 a.m.** por la misma razón (U.S. Bureau of Labor Statistics, s. f.). Elegir el corte en un periodo de mínima actividad reduce el número de episodios (sueño, cena tardía, última revisión del teléfono) partidos entre dos días. **[Validado]** el principio (cortes entre 3 y 5 h en encuestas de uso del tiempo); **[Diseño propio]** el valor exacto 05:00.

**Desde la versión con esquema 13 (entrega 10b del módulo 10, Apéndice D), la pantalla y los pasos también van por día lógico.** Antes se sumaban de 0:00 a 24:00 (día de calendario), aunque todo lo demás usaba el corte de las 05:00. Ahora `Pantalla.calcular` lee los eventos de uso de las 05:00 de ese día a las 05:00 del siguiente (`Fechas.inicioDiaLogico` y `finDiaLogico`), y `Salud.sincronizar` agrega los pasos en la misma ventana. Cada día guarda qué ventana usó: `PantallaDia.ventana` y `SaludDia.ventana_pasos` valen `"logica"` desde ese cambio y `"calendario"` en los días de antes. *Por qué importa:* la serie **se parte en dos tramos**. Por ejemplo, una madrugada con el teléfono de 01:00 a 01:30 antes contaba para el día nuevo, y su `primer_uso_h` era 1.0; ahora cuenta para el día anterior, y el primer uso del día nuevo es el de la mañana. Las correlaciones que crucen la fecha del cambio deben leerse por tramo; en R se separan con las columnas `ventana_pantalla` y `ventana_pasos` de `vm_panel.csv`. El resto del reloj (ritmo cardiaco, oxígeno) sigue por día de calendario. **[Diseño propio]**.

**Qué va todavía por día de calendario, dicho en la exportación (entrega 10c, D.64).** No todo pudo pasar al día lógico: lo que Health Connect entrega ya sumado por día y lo que depende del calendario se queda de 0:00 a 24:00. La lista está en `Diccionario.CALENDARIO`: `fc_media`, `fc_reposo`, `spo2`, `spo2_min`, `cal_activas`, `temp_piel`, `resp_rpm`, `peso_kg` (reloj y báscula), `eventos` y `min_ocupado` (agenda), `t_max`, `t_min`, `precip_mm`, `nubes_pct`, `luz_horas`, `sol_horas`, `presion_hpa`, `aqi_us`, `pm25` (clima), `luna_ilum`, y `ciudades_dia`, `fuera_de_casa`, `extranjero`, `ciudad_nueva`, `unesco` y las familias `ciu_` y `pais_` (Mundo). En `vm_panel.csv` y `vm_panel_extendido.csv`, la descripción de cada una de esas columnas termina con «· por día de calendario (0:00 a 24:00)» (`Diccionario.conVentana`), y el `LEEME.txt` lo resume: los días empiezan a las 5:00 salvo esas variables, «la pantalla y los pasos de antes de la v13» (así lo dice el LEEME; se refiere al **esquema** 13, la entrega 10b, no a la versión 13 de la app) y el sueño, que va al día en que despiertas. Antes, el LEEME decía que **todo** empezaba a las 5:00, y era falso (H-9-4). **[Diseño propio]**.

**Los bordes del día con cambio de hora (desde 10f, D.109).** `inicioDiaLogico` y `finDiaLogico` son las 05:00 locales de cada extremo, y cada una se convierte con la zona que regía a esa hora (`Fechas.millisDe`); lo mismo `inicioDia` y `finDia` con las 0:00. Por eso un día lógico con cambio de horario dura 23 o 25 horas, y en un viaje, las que den los desfases (sección 2.3.1). Antes los dos bordes se convertían con la zona de hoy. El LEEME dice ahora que las fechas van «en hora local, la del lugar donde estabas en ese momento» (`Exportar.leeme`).

![Figura 2.2. Un evento a las 00:40 del martes pertenece al lunes lógico: el día lógico va de las 05:00 a las 04:59 del día siguiente (`Fechas.diaLogico`). Ejemplo sintético. Script: `figuras/fig03_dia_logico.py`.](figuras/fig03_dia_logico.png){width=100%}

### Ejemplo sintético (Figura 2.2)

| Evento | Hora real | Día calendario | **Día lógico** |
|---|---|---|---|
| Cena | lun 21:30 | lun 12 | lun 12 |
| Nota en el diario | mar 00:40 | mar 13 | **lun 12** |
| Última pantalla | mar 02:10 | mar 13 | **lun 12** |
| Se duerme | mar 04:55 | mar 13 | **lun 12** |
| Primer uso al despertar | mar 05:05 | mar 13 | mar 13 |

: Tabla 2.2. Ejemplo sintético (Figura 2.2)

Con corte a medianoche, el «lunes» perdería su cierre y el «martes» empezaría con la pantalla de la madrugada (de 00:40 a 04:55), que en realidad pertenece al lunes. La variable `ultimo_uso_h` se corrige además sumando 24 si la hora es < 5 (p. ej., 02:10 → 26.17), de modo que «más tarde» siga significando un número mayor.

En las pantallas donde se anota, el corte se dice en palabras antes de las 05:00: «Son las 01:30: lo que anotes va al lunes… (los días empiezan a las 5:00)» (`AvisoMadrugada`, en `ui/Componentes.kt`). La excepción es la calidad del sueño, que va al día del despertar (capítulo 3, sección 3.12.1).

**Cuándo falla.** (a) Quien trabaja de noche o duerme de madrugada con regularidad rompe el supuesto de que a las 05:00 «hay poca actividad»; la regla es fija y no se adapta a la persona (limitación L-1). (b) **No todo usa el mismo criterio de día**, aunque ahora se declara: las variables de la lista anterior siguen por día de calendario, y en una misma fila del panel la madrugada cuenta para unas y no para otras. Hasta la entrega 10c, además, `lugares_distintos` agrupaba las visitas con `entrada.take(10)` (fecha de calendario) mientras `en_casa_min` usaba `diaLogicoDeMillis`; ahora las dos usan el día lógico de la llegada (`Fechas.diaLogicoDeMillis(Fechas.millis(entrada))`), así que una visita que empieza a la 1:00 cuenta para el día anterior en ambas (H-1-2, resuelto en 10c, D.64).

## 2.4 El almacén: SQLite, documentos JSON y `Repo`

### 2.4.1 Base de datos

`Db` (`SQLiteOpenHelper`, archivo `vestigium.db`, versión 2) crea tres tablas:

| Tabla | Clave | Contenido |
|---|---|---|
| `docs` | `(col, id)` | `fecha` (texto, indexado por `(col, fecha)`), `json` (documento), `editado` (ms) |
| `ubicaciones` | — | `ms`, `lat`, `lon`, `precision`, `fuente` (índice por `ms`) |
| `fotos` | `clave` | catálogo de fotos con lugar: aparato, origen, fechas |

: Tabla 2.3. Base de datos

*Qué hace.* Un enfoque de **almacén de documentos sobre SQLite**: cada colección (`dias`, `momentos`, `gastos`…) es un conjunto de filas `docs` con el JSON del objeto. Las ubicaciones (muy numerosas, de tamaño fijo) van en una tabla con columnas numéricas.

*Razonamiento.* Para un instrumento que cambia continuamente, el costo de migrar un esquema relacional supera el beneficio; el JSON permite añadir campos con valor por omisión. SQLite aporta transacciones atómicas y robustez ante apagados. La separación de `ubicaciones` es una decisión de eficiencia: millones de puntos como documentos JSON serían lentos. **[Diseño propio]**, práctica común en apps móviles.

### 2.4.2 Serialización tolerante

La configuración global `JSON` activa cuatro opciones:

| Opción | Efecto | Consecuencia metodológica |
|---|---|---|
| `ignoreUnknownKeys` | Ignora campos que el código no conoce | Permite leer respaldos de versiones futuras, pero descarta en silencio lo que no entiende |
| `encodeDefaults` | Escribe también los valores por omisión | El JSON es autodescriptivo (se ve qué valor tenía cada campo) |
| `coerceInputValues` | Sustituye `null` o valores inválidos por el valor por omisión | Puede convertir un «sin dato» en un valor por omisión (H-1-4) |
| `isLenient` | Acepta JSON con sintaxis relajada | Tolera archivos importados imperfectos |

: Tabla 2.4. Serialización tolerante

La carga inicial usa `cargar(col, serializer)`. Hasta el esquema 12 descartaba **sin aviso** los documentos que no se podían decodificar (`mapNotNull { runCatching … getOrNull() }`): seguían en la base de datos, pero eran invisibles para la app, el panel y la exportación (H-1-3). Desde el esquema 13 (P-07) los sigue dejando fuera, pero **los cuenta**: `Decodificar.todos` devuelve los leídos y el número de fallidos, y `Repo.ilegibles` guarda ese conteo por colección. Las consultas por fechas (`escuchas`, `huellas`, `comidas`, `series`) anotan el mayor conteo visto. El conteo aparece en «Estado de los datos» y como aviso en Hoy. La configuración, que es un solo documento, tiene un trato aparte: si no se puede leer, su texto original se copia a la colección `config_ilegible` **antes** de seguir con una configuración vacía. Sin esa copia, el siguiente cambio de ajustes la sobrescribiría, y se perderían personas, hábitos, perfil y campañas (H-9-2).

### 2.4.3 `Repo`: memoria con escritura inmediata

`Repo` mantiene en memoria el estado observable (Compose `mutableStateOf`) de las **36 colecciones** que precarga al iniciar (`dias`, `momentos`, `gastos`, `ingresos`, `respuestas`, `reacciones`, `lugares`, `visitas`, `youtube`, `salud`, `pantalla`, `cartas`, `experimentos`, `entorno`, `fotos`, `agenda`, `notifs`, `actividad`, `sesiones`, `ascensos`, `obras`, `comunicacion`, `conexiones`, `subrayados`, `huellas_dia`, `whatsapp`, `nutricion`, `entrenos`, `tramos`, `consumos`, `notas`, `metas`, `tomas`, `laptop` y, desde el esquema 13, `avisos`, un documento por día con los avisos de ánimo planeados, vencidos y mostrados, capítulo 3; desde el esquema 16, `ideas`, el Ideario, sección 2.5; desde el esquema 18, `listas`, las listas libres). Cada `guardarX` hace **escritura inmediata (write-through)**: primero a SQLite (`db.poner`), luego actualiza la memoria en el hilo principal. Los más usados (`guardarDia`, `guardarMomento`, `guardarConsumo` y los que anotan visitas, avisos, actividad, notificaciones y conexiones) están sincronizados (`@Synchronized`); los demás no.

Otras colecciones se guardan en la misma tabla pero no están en la carga de 36: `comidas`, `escuchas`, `huellas` y `series` se leen bajo demanda, por fechas; `papelera` (desde el esquema 14) y `config_ilegible`, cuando hacen falta. La configuración (`config`, un solo documento) se lee al iniciar, aparte de las 36; desde el esquema 15 guarda también el historial de zonas horarias (`Config.zonas`, sección 2.3.1), que `Repo` le pasa a `Fechas` (`Fechas.usarHistorial`) al iniciar y cada vez que cambia la configuración.

*Razonamiento.* La escritura inmediata minimiza la pérdida de datos si la app se cierra; mantener todo en memoria hace instantáneos los análisis sobre cientos o miles de días. El costo es el uso de memoria, aceptable para un solo usuario.

**Metadatos de calidad en `Dia`.** `guardarDia` añade a cada día:

- `ts_creado` (primera vez que se guardó), `ts_editado` (última edición), `schema_v`;
- **`retroactivo`**: hasta el esquema 12, `true` si el documento del día se guardaba por primera vez *después* de que su día lógico terminó. Esa regla fallaba cuando otra cosa creaba el documento ese mismo día (el sueño de la captura rápida, un hábito desde el widget): el día quedaba `false` para siempre, aunque la valencia se llenara días después (H-2-2). Desde el esquema 13 (P-13), `Dia` guarda **`ts_valencia`**, la hora en que la valencia pasó de vacía a llena o cambió (`Reglas.tsValencia`). Así, `retroactivo` = el día lógico de `ts_valencia` es posterior a la fecha del día (`Reglas.retroactivo`). Sin `ts_valencia` (días de antes del esquema 13, o sin valencia), el día conserva el valor que tenía; si el documento es nuevo, vale `true` si se creó después de su fecha.

*Razonamiento.* `retroactivo` permite estimar sesgo de recuerdo: un día llenado a posteriori se recuerda, no se vive. Es un **moderador** que el análisis puede incluir (¿cambian las asociaciones al excluir los días retroactivos?). **[Diseño propio]**, muy buena práctica: en la literatura de diarios se recomienda registrar *cuándo* se llenó cada entrada (Stone et al., 2002 propusieron verificar el cumplimiento temporal en diarios).

## 2.5 Catálogo de colecciones

Resumen por familia temática (el detalle campo a campo está en el Apéndice A):

| Familia | Colecciones | Granularidad | Capa |
|---|---|---|---|
| Ánimo y diario | `dias`, `momentos`, `respuestas`, `cartas`, `notas`, `metas`, `avisos` (desde el esquema 13) | día / evento | activa (`avisos`: automática) |
| Cuerpo y salud | `salud`, `entrenos`, `sesiones`, `ascensos`, `consumos`, `tomas`, `nutricion` | día / sesión | pasiva e importada (`consumos` y `tomas`: activa) |
| Teléfono y pantalla | `pantalla`, `notifs`, `conexiones`, `laptop`, `huellas_dia`, `youtube` | día | pasiva e importada |
| Dinero | `gastos`, `ingresos` | transacción | activa |
| Lugar y movimiento | `lugares`, `visitas`, `actividad`, `ubicaciones` (tabla), `fotos` | visita / punto | pasiva |
| Entorno | `entorno` (clima, aire, luz solar), fases de la luna (calculadas) | día | pasiva / calculada |
| Cultura y mente | `obras`, `subrayados`, `agenda`, `tramos` (cronómetro de enfoque y siestas), `ideas` (desde el esquema 16), `listas` (desde el esquema 18) | día / obra / bloque / idea / lista | activa e importada |
| Sociales | `comunicacion`, `whatsapp`, menciones `@persona` en texto | día | pasiva e importada |
| Cognición | `reacciones` (tiempo de reacción) | sesión | activa |
| Ciencia personal | `experimentos` | semanas | activa (diseño en capítulo de experimentos) |

: Tabla 2.5. Catálogo de colecciones

**El Ideario (desde el esquema 16).** La colección `ideas` guarda, una por documento (`Idea`), lo que entendiste de un libro, un video o una plática, o algo que se te ocurrió, con tus palabras. Solo el texto es obligatorio; además puede llevar la idea en una línea (`titulo`), sus temas (`temas`, desde el esquema 17; se suman a los `#temas` escritos en el texto, `Ideario.temasDe`), de dónde viene (`origen`: leído, visto o escuchado, contado o propio), quién lo dice (`quien`, una lista de nombres que **no** son personas de Tu gente), la obra y la cita de las que salió (`obra_id`, `cita_id`), una fuente escrita y su enlace, el minuto o la página, cómo la tienes (`postura`: me convence, me hace pensar, la dudo, no estoy de acuerdo) y cada vez que la volviste a ver en «Una idea de antes» (`revisiones`). Se asigna al día que tú eliges (por omisión, el día lógico en que se anota) y guarda la hora en que se escribió (`creada`). Es una colección de **eventos** (varias ideas por día) que **no entra al panel**: ninguna variable diaria sale de ella, sus temas no se suman a «Tus temas» ni sus `@personas` a las menciones de la red social (capítulo 7). Solo se exporta tal cual (`vm_ideas.csv` y `vm_ideas_revisiones.csv`, Apéndice A.10). **[Diseño propio]**.

**Deseados y listas libres (desde el esquema 18, D.145).** Lo que quieres leer, ver o jugar es una `Obra` con `estado = "quiero"` (la pantalla «Quiero leer y ver»); desde el esquema 18 lleva además qué tan pronto la quieres (`prioridad`: pronto, cuando pueda, algún día), quién te la recomendó (`recomendo`, personas de tu gente sin @) y dónde verla o conseguirla (`donde`). Lo que no es una obra (lugares por visitar, cosas por comprar…) va en la colección `listas`: un documento por lista (`Lista`) con sus elementos (`ElementoLista`: texto, nota, enlace, prioridad, cuándo se agregó y cuándo se hizo). **Ninguna de las dos entra al panel.** Agregar algo a una lista o tacharlo no dice cómo fue un día; serían columnas casi siempre en cero que solo sumarían comparaciones a `vm_relaciones` (capítulo 6). Lo que sí describe el día ya se mide (`obras_dia`, `obra_terminada`), y el tiempo entre desear una obra y empezarla se calcula en R con `agregado` e `inicio` de `vm_biblioteca.csv`. Se exportan tal cual: tres columnas nuevas en `vm_biblioteca.csv` y `vm_listas.csv` (Apéndice A.12). **[Diseño propio]**.

«Una idea de antes» (`Ideario.resurgir`) elige, en el Día de hoy, una idea con al menos 30 días que no hayas respondido en los últimos 30, y prefiere las que cumplen un aniversario: 30, 180 o 365·n días (n = 1…10), con ± 3 días de holgura. Si ninguna cumple, toma cualquiera de las elegibles. La elección depende solo de la fecha y del identificador (el menor `hashCode` de «fecha:id»), así que es la misma toda la jornada; sale una por día. *Ejemplo con datos inventados:* el 2 de noviembre, con ideas del 20 de octubre (13 días: aún no), del 2 de octubre (31 días: aniversario de un mes) y del 15 de agosto (79 días), sale la del 2 de octubre. Releer a intervalos que se alargan se parece a la **repetición espaciada** (repasar algo justo cuando empieza a olvidarse ayuda a recordarlo), pero aquí los intervalos son fijos y no se adaptan a lo que recuerdas: es una aproximación a esa idea, no el método validado. **[Aproximación]**. *Cuándo falla:* con pocas ideas, casi siempre sale «cualquiera»; y la respuesta («sigo pensando igual», «ya no», «ahora no») mide si cambió tu opinión, no si la recordabas.

**Unidad de observación.** No todas las colecciones son «una fila por día»: `momentos` (varios por día, con hora), `gastos` (por transacción), `visitas` (con entrada/salida) y `ubicaciones` (puntos) son **eventos**. El paso evento → día (agregar por media, suma, conteo o máximo según la variable) es el corazón de la sección 2.6 y una fuente de decisiones que afectan los resultados (por ejemplo, un día con un solo aviso de ánimo tiene una media tan ruidosa como una observación única). Desde el esquema 13, `avisos_respondidos` dice cuántos de los avisos mostrados ese día tuvieron respuesta, que es casi siempre el número de valores detrás de `muestreo_valencia` (no siempre: la media usa todos los registros de tipo «muestreo» del día, también los que no están unidos a un aviso); antes solo estaba `registros_animo`, que cuenta también los registros por iniciativa propia.

## 2.6 El panel diario y las reglas de relleno

`Estadistica.panel()` devuelve un mapa `fecha → (id de variable → Double?)`. Cada celda puede tener **tres estados** (Figura 2.3):

| Estado | Significado | Representación |
|---|---|---|
| **Valor** | Se observó y vale *x* | número |
| **Cero estructural** | Se sabe que *podría haberse registrado* y no ocurrió | `0.0` |
| **Sin dato** | No hay información | `null` (celda vacía en CSV, `NA` en R) |

: Tabla 2.6. El panel diario y las reglas de relleno

![Figura 2.3. Panel diario sintético de 30 días con los tres estados posibles de cada celda: sin dato (null), cero estructural y valor observado. El gasto vale 0 solo desde el primer gasto y en días con diario (`Estadistica.ceroDesde`). Script: `figuras/fig02_estructura_panel.py`.](figuras/fig02_estructura_panel.png){width=100%}

### 2.6.1 La regla general

Una fila de la tabla se crea para cada fecha que aparece en *alguna* fuente. Una variable recibe `null` si su fuente no tiene registro ese día, salvo en los casos de **relleno con cero**. Desde la entrega 10c (D.63, P-15) la regla es **una sola**, escrita en una función común, `Estadistica.ceroDesde(fuente, base)`: devuelve los días de `base` (los días que pudieron responderse) que son iguales o posteriores al primer registro de la fuente. En esos días «no hubo» vale 0; antes del primer registro, o fuera de la base, la celda queda vacía.

| Regla | Variables | Base (días que pueden valer 0) | Razonamiento |
|---|---|---|---|
| **Cero desde el primer registro, en días con diario** (`ceroDesde`) | `gasto_total`, `gasto_imprevisto` y, desde 10e, `gasto_imprevisto_monto` (primer gasto), `n_impulso` (primer impulso), `escalada` (primera sesión), `gimnasio` (primer entrenamiento; también cuenta los días con entrenamiento) | días con documento de diario | Cuando la persona *ya usa* esa función, un día con diario sin registro significa «no ocurrió». Antes del primer uso no se sabe. |
| **Cero desde el primer registro** (reglas que ya existían, con la misma lógica) | emociones `n_*`, `registros_animo`, `enfoque_*_min`, `siesta_min`, `horas_tipo_*`, `obras_dia`, `carta_escrita`, `cartas_otros`, `n_recaida`, `yt_megusta`, `yt_comentarios`, `ingreso_total`, `medicamentos_n`, `campana_*` | días con diario (o con datos de esa fuente) | Igual |
| **Hábitos** `hab_<id>` | 1 si lo marcaste; 0 desde `Habito.desde` (el día en que lo creaste); antes, vacío. Los hábitos sin esa fecha (importados o de versiones viejas) siguen en 0 todos los días. Desde el esquema 17, en los días contestados con «sí / no / sin contestar» (`habitos_tri`), «no» es 0 y un hábito sin respuesta queda vacío (D.133) | días con diario | Un hábito que aún no existía no se pudo cumplir ni dejar de cumplir |
| **Sueño recordado** `sueno_recordado` | 1 si recordaste un sueño; 0 solo si ese día dijiste algo del sueño (calidad o texto); si no, vacío | días con diario | Un día en que no abriste la sección del sueño no dice «no recordé nada» |
| **Variables sociales** `personas`, `vi_*`, `cercanos`, `cercania_media`, `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `penso_en_*`, y las familias `per_` y `pen_` | 0 solo en los días en que se pudo nombrar a alguien (`Dinamicas.diasSociales`): con diario escrito o con un momento no privado que nombra a alguien con @. En `tema_`, los días con diario escrito o con un momento que lleva un #tema. Desde 10e (D.93), `personas` cuenta a quien nombras con @ en el diario **y** en los momentos no privados de ese día; antes contaba solo el diario, y un día con solo momentos valía 0 aunque nombraras a alguien (H-14-2) | esos días | Antes, un toque en el widget de hábitos o una recaída creaban el documento del día y con él un día de «no vi a nadie» |
| **Ejercicio y distancia** | `ejercicio_min` en 0 los días con datos del reloj (pasos o ritmo cardiaco) y sin sesión, desde la primera sesión; `distancia_km` en 0 los días con ubicaciones y sin movimiento (`Movilidad.kmPorDia`) | días con reloj / con ubicaciones | Antes quedaban vacíos aunque sí se sabía que fueron 0 |
| **Probióticos leídos de la bitácora** `probioticos_estimado` | solo los días con bitácora de MacroFactor | días con nutrición | Sin bitácora no se puede leer nada |
| **Cero en todo el diario** (lo que queda) | `n_madrugada…n_noche`, `ctx_*`, `dia_dificil`, `palabras`, `hitos_n` | todos los días con diario | Los contextos sin marcar valen 0 por convención de la pantalla («marca lo que aplique»); se documenta |
| **Ausencia = null** | pasos, sueño del reloj, pantalla, clima, HRV, etc. | — | Son mediciones instrumentales: no hay lectura = no hay dato (el dispositivo no midió). |

: Tabla 2.7. La regla general

**Razonamiento científico.** Es una aplicación práctica de la teoría de datos faltantes (Rubin, 1976): un dato faltante puede ser *completamente aleatorio* (MCAR), *aleatorio condicionado a lo observado* (MAR) o *no aleatorio* (MNAR). Rellenar con cero es válido únicamente si el cero es el valor *verdadero* (se sabe que no hubo evento). Poner `null` evita crear ceros falsos que reducirían la media y crearían correlaciones espurias por el solo hecho de que la persona dejó de registrar. Como los días sin anotar no son al azar (suelen ser los peores o los más ocupados), un cero falso no solo baja la media: sesga las correlaciones. **[Validado]** la distinción cero/nulo; **[Aproximación]** la heurística «desde el primer registro» (supone que el uso de la función es continuo desde que se empieza).

### Ejemplo sintético

Un usuario registra su primera emoción el día 6 y no vuelve a hacerlo hasta el 8. La variable `n_ansiedad`: días 1–5 → `null`; día 6 → 2; día 7 → `0` (ya usa la función y ese día hay diario, pero no registró ansiedad); día 8 → 1. Si en cambio `n_ansiedad` se rellenara con 0 desde el día 1, la media de la ansiedad bajaría artificialmente y su correlación con cualquier variable que *empieza* a registrarse más tarde (por ejemplo, el sueño del reloj desde el día 4) mostraría una asociación falsa, producto solo del calendario de adopción.

Lo mismo con el gasto, que hasta la entrega 10c era la excepción. Con diario desde el día 1 y el primer gasto el día 40: antes, `gasto_total` valía 0 los días 1 a 39; ahora esos días quedan vacíos y el 0 empieza el día 40 (`ceroDesde(["día 40", …], días con diario)`). Un día 41 con diario y sin gasto vale 0; un día 42 sin documento de diario, vacío.

**Cuándo falla.** (a) «Desde el primer registro» no distingue entre *abandono de la función* y *ausencia real*: si la persona deja de usar el registro de emociones tres meses, esos meses quedan en 0, no `null` (H-1-1, sigue vigente para el abandono). (b) La base «días con diario» supone que, si abriste el diario, pudiste anotar un gasto o una escalada; un día en que solo marcaste la valencia cuenta como «sin gasto». (c) Los hábitos creados antes de que existiera `Habito.desde` no tienen fecha de inicio y siguen en 0 desde el principio. La inconsistencia entre fuentes parecidas que señalaba H-1-1 (gasto desde el diario, ingreso desde su primer registro) quedó resuelta en 10c (D.63). Desde la entrega 10d (P-30), `vm_panel.csv` toma estas mismas reglas del panel de la app en lugar de aplicar las suyas (§ 2.8).

### 2.6.2 Agregación de eventos a día

| Variable | Operación | Notas |
|---|---|---|
| `muestreo_valencia` | media de los avisos del día | `media()` devuelve null si no hay avisos |
| `registros_animo` | conteo | 0 estructural desde el primer registro |
| `gasto_total` | suma | `gasto_imprevisto` es **binaria** (¿hubo algún gasto imprevisto?), no un monto (H-1-5) |
| `gasto_imprevisto_monto` | suma de los gastos marcados como imprevistos | Desde 10e (D.94): el monto que `gasto_imprevisto` no da. Misma base que `gasto_total` (0 desde el primer gasto, en días con diario); un día con gastos y ninguno imprevisto vale 0. `gasto_imprevisto` conserva su nombre para no romper la serie (H-1-5, resuelto) |
| `acompanado_pct` | proporción de avisos con alguien | solo si hay avisos con «con quién» |
| `en_casa_min` | minutos dentro de lugares tipo «casa», recortados por día lógico, tope 1440 | visitas > 40 días se descartan como probable error |
| `aire_libre_min` | máximo entre luz alta del teléfono y tiempo a pie/corriendo/bici | **[Aproximación]**: dos proxies de la misma cosa, se toma el mayor |
| `ruido_db` | media de las lecturas del día | |
| `error_prediccion` | |predicción de ayer − valencia de hoy| | solo con días consecutivos |
| `error_prediccion_signo` | valencia de hoy − predicción de ayer (−6 a +6) | Desde 10e (D.94); los mismos días que `error_prediccion`. Positivo: el día salió mejor de lo predicho (capítulo 3, sección 3.8) |
| `balance_kcal` | `kcal − gasto_kcal` | null si falta cualquiera |

: Tabla 2.8. Agregación de eventos a día

**Razonamiento.** La elección media/suma/máximo debe corresponder a la naturaleza física de la variable: cantidades extensivas (minutos, gastos, pasos) se **suman**; estados intensivos (valencia) se **promedian**; eventos raros (ansiedad, impulso) se **cuentan** o se marcan 0/1. Usar `max` para `escalada_grado` captura «el mejor del día», no la media, porque el desempeño en escalada se evalúa por su pico. **[Diseño propio]** cada operador, coherente con la naturaleza de la variable.

### 2.6.3 Variables binarias, escalas y códigos ordinales

Los 0/1 (`ctx_*`, `hab_*`, `vi_pareja`…) tratan una condición como «ocurrió o no ese día». Las escalas del diario (valencia, activación 1–7) se guardan como números pero son **ordinales** (la distancia entre 3 y 4 no es necesariamente la de 6 a 7). Algunos códigos ordinales se convierten a números: `sueno_lucidez` (no=0, parcial=1, total=2), `sueno_vividez` (vago=0, moderado=1, muy vivido=2). *Razonamiento:* los modelos de los capítulos siguientes usan métodos robustos (rangos, Spearman) cuando la variable es ordinal; el panel solo preserva el orden.

### 2.6.4 Variables derivadas deterministas

`luna_ilum` (iluminación lunar) se calcula con una fórmula astronómica para *cada* fecha del panel; no depende de lo que la persona registre. Es una variable **exógena y completa**, útil como control negativo (control de «placebo»): si una asociación con la luna aparece, probablemente es ruido o multiplicidad. Aun así, al estar presente en todos los días del panel, nunca produce celdas `null`.

## 2.7 Familias de variables dinámicas

**Cuántas variables fijas hay.** Contando como «fija» cada identificador que `Estadistica.variables()` escribe con su nombre, son **193** desde el esquema 17 (187 desde 10f, 186 en la v91 y 184 en la v90), en 24 grupos: 133 existen siempre y 60 solo si hay datos de su fuente (por ejemplo, `alcohol` si alguna vez anotaste alcohol, o `laptop_min` si importaste Cold Turkey). De ellas, 7 son privadas (`impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `medicamentos_n`): la app no las cruza y van a R. Otras 39 salen de listas fijas del catálogo: los 13 `ctx_*` (10 hasta el esquema 16), las 13 opciones de las tres preguntas científicas, los 8 `con_*` (5 hasta el esquema 16), las 4 `laptop_<categoría>_min` y `n_impulso`. Las dos que agregó 10e (D.94), `error_prediccion_signo` (grupo «Ánimo») y `gasto_imprevisto_monto` («Dinero»), están en la lista fija y existen siempre. La que agregó 10f (D.114), `fuerza_ref_kg` («Cuerpo»), solo existe si hay datos: es la mejor 1RM estimada del día en un ejercicio fijo, el press inclinado con mancuernas (capítulo 8). El Apéndice A.1 lista 192 (186 hasta el esquema 16), porque pone `dia_especial` y `campanas_n` entre las de nombre generado y suma `n_impulso`.

Además, el sistema crea **variables dinámicas** por cada elemento que la persona usa: una por cada persona (`per_*`, `pen_*`), tema, hito, lugar, app, categoría de gasto, artista, canal, género, obra, medicamento, ciudad, país, músculo, nutriente, alimento… (27 prefijos, Apéndice A.2). Lo mismo los hábitos (`hab_*`), las emociones y pensamientos usados (`n_*`), las categorías de sueño, los tipos de lugar, los suplementos y las campañas, que `variables()` arma con lo que hay en tus datos.

- **Análisis:** entran solo las más frecuentes de cada familia, con un tope máximo y un mínimo de días (por ejemplo, `per_`: máx. 15, mín. 5 días; `Dinamicas.familia`).
- **Exportación extendida (`vm_panel_extendido.csv`):** entran todas las que aparecen en al menos 2 días, hasta 400 por familia.

*Razonamiento científico.* Con *p* variables hay *p(p−1)/2* pares posibles; crecer sin límite dispararía la **multiplicidad** (falsos positivos) y la inestabilidad de las estimaciones con pocos días. El filtro por frecuencia es una forma de control de la dimensionalidad: solo se analizan elementos con suficiente información para estimar algo. Aun así, el filtro depende de los datos (se eligen las más frecuentes *mirando los mismos datos*), por lo que el tratamiento estadístico posterior (corrección de falsos descubrimientos, capítulo 6) es imprescindible. **[Aproximación]**.

Además, el mapa `AGREGADOS` (en `ciencia/Dinamicas.kt`, usado por `Estadistica.obvia`) evita comparar variables **obviamente relacionadas** (p. ej., `app_instagram` contra `pantalla_min`: una es parte de la otra) y `ENTRE_SI` evita pares dentro de ciertas familias o entre familias que salen de lo mismo (dos nutrientes del mismo plato, un nutriente y un alimento, una ciudad y su país). *Razonamiento:* una correlación que es consecuencia de la definición (parte vs. todo) no es un hallazgo, es una tautología; excluirla reduce falsos positivos de «descubrimientos» triviales. **[Validado]** el principio (evitar colinealidad por construcción); **[Diseño propio]** los pares concretos.

**Ejemplo.** Si la persona menciona `@ana` en 12 de 60 días, existen `per_ana` (0/1 por día) y, si hubo días en que pensó en ella sin verla, `pen_ana`. La normalización de nombres (`Texto.canon` con el diccionario de equivalencias) unifica «@Ana», «@ana_m» y apodos declarados. Una mención escrita en el diario cuenta como **«estuve con»** por omisión (estado `estuve`), a menos que la persona cambie el estado a «pensé en» o «mensajes»; la marca `presencias_ok` indica si el día fue *revisado* (H-1-6). Esto es relevante porque una mención no es presencia. Desde la entrega 10c esa marca entra al panel como `presencias_revisado` (0/1, solo en días con personas en el diario o ya revisados), para usarla como covariable: así se puede comparar «estuve con» en días revisados y sin revisar. Es obvia con las variables sociales (se mueve con ellas por construcción), así que no sale como «relación» en la app. La normalización `Texto.canon` sigue además las uniones encadenadas (capítulo 7, D.73).

## 2.8 Exportación, diccionario y R

La app puede exportar **51 tablas CSV** con datos (las que arma `Exportar.tablas()`) (`vm_*.csv`, Apéndice A.3), más `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv`, el diccionario `vm_diccionario.csv`, `vm_respaldo.json`, `LEEME.txt`, una copia SQLite (`vm_datos.sqlite`, en la exportación completa) y un script `vestigium.R` que carga todo en *tidy* (`Exportar.archivos`).

| Elemento | Función | Razonamiento |
|---|---|---|
| `vm_panel.csv` | Una fila por día con las variables principales | Entrada para el análisis rápido; tabla de compatibilidad para scripts viejos |
| `vm_panel_extendido.csv` | Panel de la app + todas las dinámicas (≥ 2 días) + las columnas de las tablas diarias | El panel que analiza `vestigium.R` desde la entrega 10d |
| `vm_variables.csv` | Las variables que la app cruza, con su nombre, su grupo y si son privadas | Que R cruce las mismas variables que la app (10d) |
| `vm_familias.csv` | Las parejas de variables que la app esconde por obvias, escritas como familias | Que R esconda las mismas parejas (10d) |
| Tablas por evento (`vm_momentos`, `vm_gastos`, `vm_horas`…) | Una fila por evento (o por hora) | Datos en forma **larga**: cada fila es una observación (Wickham, 2014) |
| `vm_diccionario.csv` | Qué significa cada columna, unidad, grupo | Interoperabilidad y reutilización (FAIR: *Findable, Accessible, Interoperable, Reusable*; Wilkinson et al., 2016) |
| `vestigium.R` | Carga, tipos de datos y funciones auxiliares | Reproducibilidad: otro analista (o la misma persona en 5 años) reproduce el análisis |
| Respaldo (zip / `.vmc` cifrado) | Copia completa de la información | Preservación y portabilidad |

: Tabla 2.9. Exportación, diccionario y R

**Principio tidy.** Cada variable es una columna, cada observación una fila, cada tipo de unidad observacional una tabla (Wickham, 2014). Así `vm_panel` (unidad = día) y `vm_gastos` (unidad = transacción) conviven sin mezclar granularidades. **[Validado]**.

**El diccionario, columna por columna (entrega 10c, D.76).** `Diccionario.describir` busca primero la clave «archivo:columna» (por ejemplo, `vm_red_nodos.csv:grado`) y, si no existe, la columna sola. Hasta 10c, varias columnas con el mismo nombre en tablas distintas compartían una sola clave, y en un `mapOf` de Kotlin gana la última: `grado` de los bloques de escalada se describía como «Con cuántas personas está unida en tu red», `antes` de los momentos como «Cómo estaba» y `dia_logico` de todas las tablas como un gasto (H-9-20). Ahora las que chocaban (`antes`, `inicio`, `fin`, `min`, `dia_logico`, `categoria`, `minutos`, `duracion_s`, `grado`, `cantidad`) tienen su clave por archivo, las columnas de `vm_experimentos.csv` tienen descripción propia, `distancia_km` se describe distinto en el panel (GPS) y en `vm_salud.csv` (Health Connect), y una prueba JVM (`CalculosPrueba.diccionarioSinClavesRepetidas`) falla si alguna clave vuelve a aparecer dos veces con descripciones distintas.

**El calendario como variable (entrega 10c, D.69).** `vm_panel.csv` trae, junto a `dia_semana`, dos columnas nuevas: `festivo` (0/1; día de descanso obligatorio de la Ley Federal del Trabajo de México, art. 74, solo si en «Conocerte» el país es México; vacío si no) y `dia_libre` (0/1; sábado, domingo o festivo). `dia_libre` es la misma regla que usa el jet lag social de la app (`Fechas.esDiaLibre`, capítulo 5), y R la lee de esa columna en vez de recalcularla. Desde 10f (D.110) van también `desfase_utc_h` y `cambio_horario` (sección 2.3.1), y `clima_reanalisis` (0/1: el clima de ese día viene del reanálisis histórico de Open-Meteo y no de los modelos de pronóstico; D.111, capítulo 4).

**R analiza el mismo panel que la app (entrega 10d, P-30 y P-31).** Hasta 10c, `vestigium.R` analizaba `vm_panel.csv`, que se armaba con reglas propias: `gasto_total` e `ingreso_total` valían 0 en cualquier fecha exportada, `escalada` y `gimnasio` eran «falso» en cualquier fecha, `canciones` contaba distinto y `ultimo_uso_h` sumaba 24 a cualquier hora antes de las 12:00. Así, R no servía para comprobar lo que muestra la app (H-5-1). Ahora hay cuatro piezas:

1. **Los valores de las columnas compartidas.** `Exportar.comoLaApp` recorre, después de armar `vm_panel.csv`, cada columna cuyo nombre también existe en el panel de la app (`Estadistica.panel()`) y le pone el valor de la app, con sus reglas de 0 y vacío (§ 2.6.1). Por ejemplo, `gasto_total`, `escalada` y `gimnasio` valen 0 desde el primer registro de su fuente y solo en días con diario, y antes quedan vacíos; `ejercicio_min` vale 0 en días con datos del reloj y sin sesión; `ultimo_uso_h` suma 24 solo antes de las 5:00, como el día lógico (1:00 → 25; 11:00 → 11). Se conserva el tipo que tenía la columna: si era 0/1, sigue siendo 0/1, y si era entero, sigue entero cuando el valor no tiene decimales. Así un script viejo la sigue leyendo igual. **[Diseño propio]**.
2. **Once nombres distintos, documentados y no renombrados.** La misma medida tiene otro nombre en `vm_panel.csv` y en la app. Por ejemplo, `km` es `distancia_km`, `sueno_min` es `sueno_horas` en minutos y `hrv_rmssd` es `hrv`. La lista completa está en `Exportar.EQUIVALENTES`: `sueno_min`, `sueno_profundo_min`, `sueno_rem_min`, `hrv_rmssd`, `km`, `valencia_muestreo`, `luz_dia_min`, `sol_min`, `carga_noche_inicio_h`, `temp_piel_delta` y `ruido_db_medio`. No se renombraron para no romper scripts viejos. El diccionario lo dice en las dos (`Diccionario.conEquivalente`: «· en el panel de la app: distancia_km» y «· en vm_panel.csv: km»). Las ocho de igual unidad (factor 1 en `EQUIVALENTES`) toman el valor de la app (`comoLaApp`); por ejemplo, `ruido_db_medio` ahora trae el `ruido_db` de la app, y `km`, la `distancia_km`. Las tres que cambian de unidad (minutos frente a horas, factor 60: `sueno_min`, `luz_dia_min`, `sol_min`) se dejan como estaban, porque salen de la misma fuente y solo difieren en la unidad.
3. **`vm_variables.csv` y `vm_familias.csv`.** La primera lista las variables que la app cruza en «Las relaciones más marcadas» (`Estadistica.variables()`), con la columna `privada` (0/1): la app nunca cruza las privadas, y R solo las cruza si se pide. La segunda escribe como familias las parejas que la app esconde por obvias (`Estadistica.familias`, P-31; detalle en el capítulo 6).
4. **R lee el panel extendido por omisión.** `vestigium.R` carga `vm_panel_extendido.csv` como `panel`, y `vm_relaciones()` y `vm_factores()` cruzan por omisión las variables de `vm_variables.csv`. `vm_panel.csv` queda en `vm$panel` como tabla de compatibilidad. El `LEEME.txt` lo explica.

Una prueba JVM (`ExportacionPrueba`) fija los números que da R con unos datos inventados y comprueba que la app da los mismos (capítulo 6). R no corre en la compilación automática. Con una exportación real de la v89, `vestigium.R` cargó y analizó sin errores, y así se vio que al panel extendido le faltaban 57 variables de la app, ya corregido (D.87). Después, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026).

**Desde qué versión rige cada columna (desde 10f, D.115; H-1-8).** Hasta 10f, el diccionario describía el *significado* pero no versionaba cada variable: si el código cambiaba una definición entre versiones (p. ej., el cálculo de `aire_libre_min`), el diccionario solo reflejaba la versión actual, y una exportación antigua podía no ser comparable (`schema_v` ayuda, pero no a nivel de variable). Desde 10f, `vm_diccionario.csv` trae dos columnas más: `desde_version`, la versión de la app desde la que rige la definición actual de la columna, y `cambio`, qué cambió entonces, con su fila del Apéndice D. Vacío quiere decir «sin cambios desde la v84». `Diccionario.desde(archivo, columna)` busca primero la clave «archivo:columna» en la tabla `Diccionario.DESDE`, después (en `vm_panel_extendido.csv`) la de `vm_panel.csv` con el mismo nombre, después la columna sola, como `describir`, y al final las claves con comodín (`per_*`, `supl_*_nivel`…; si dos coinciden, gana la más larga, así `app_min_*` le gana a `app_*`). La tabla tiene 165 entradas, armadas con las filas D.1 a D.118 del Apéndice D (v85: 5; v86: 40; v87: 7; v88: 53; v89: 27; v90: 3; v91: 16; v92: 14). *Ejemplo:* `sin_wifi_min` dice 92 y «ya no incluye los minutos sin dato (D.112)». El LEEME dice que se miren al comparar exportaciones de fechas distintas. **[Diseño propio]**. *Cuándo falla:* la tabla se escribe a mano: un cambio futuro que no se anote ahí deja «vacío» donde debía haber versión. El registro empieza en la v85: lo que cambió antes no se puede reconstruir. Y las columnas de hora que cambian solo cuando hubo un viaje (la hora local de entonces, D.109) no se marcan con 92, porque sin viajes nada cambió.

Desde 10e había ya dos ayudas parciales. El diccionario dice con qué parámetros se calcularon `cafeina_al_dormir_mg`, `creatina_saturacion_pct` y cada `supl_x_nivel` (vida media, días de carga y de lavado, dosis de referencia) y advierte que cambiarlos recalcula toda la historia (`Diccionario.conParametros`, D.99; capítulo 5). Y el `LEEME.txt` y el diccionario dicen «esquema 13» y «esquema 14» donde decían «v13» y «v14», que se confundían con las versiones de la app, y el LEEME dice que el esquema de cada día está en `schema_v` de `vm_dias.csv` (`Exportar.leeme`, D.107, H-14-1).

## 2.9 Integridad, respaldo e importación

- **Transacciones.** Las importaciones masivas (`importarDias`) usan `db.transaccion { }`: se aplican todas o ninguna.
- **Importar con o sin sobrescribir.** `importarDias(lista, sobrescribir)` evita pisar días existentes por omisión. Los días importados guardan lo que traiga el archivo, sin inventar nada (entrega 10d, P-33). Los de la versión web conservan su `ts_creado` y `ts_editado`, y `schema_v` es el del archivo, o 0 si no lo trae; antes recibían la versión de hoy. Los de Obsidian toman `ts_creado` y `ts_editado` del encabezado de la nota, o los dejan vacíos; antes recibían la hora de la importación. `origen` dice de dónde vinieron (`web`, `obsidian`). Sigue sin haber una marca de «importado el día X» (H-1-9): si el archivo no traía sellos, no se sabe cuándo se capturaron esos días, y la app no lo inventa.
- **Caché de variables dinámicas.** `Dinamicas.calcular` guarda en caché el resultado y lo invalida comparando referencias de las colecciones (`clave()`); si cambia cualquiera de ellas se recalcula. *Razonamiento:* cálculo costoso, pero la invalidación por referencia es correcta solo mientras las colecciones se reemplacen (inmutables), como hace `Repo`.
- **Restaurar sin pisar ni dejar a medias (esquema 13, P-08).** Un respaldo con `schema_v` mayor que el de la app no se restaura: los campos que esta versión no conoce se descartarían en silencio (`ignoreUnknownKeys`). Los gastos e ingresos del respaldo que ya existen (mismo id) no se tocan, ni en la base ni en memoria. Antes se sobrescribían en la base pero no en memoria, y al reiniciar aparecía la versión del respaldo. Un experimento del respaldo no reactiva uno terminado aquí, y se juntan las respuestas de «¿Lo hiciste hoy?» de los dos (`Fusion.experimento`). El detalle está en el capítulo 10.
- **Identificadores.** `nuevoId()` = UUID recortado a 16 caracteres hexadecimales (64 bits): colisión prácticamente imposible a esta escala (probabilidad de al menos una colisión ≈ 3×10⁻¹⁰ con 10⁵ registros).

## 2.10 Amenazas a la validez y limitaciones

| # | Amenaza | Dónde nace | Qué hace el sistema | Qué falta |
|---|---|---|---|---|
| L-1 | **Día lógico fijo** (05:00) | 2.3.2 | Corte único | No se adapta al ritmo de la persona |
| L-2 | **Cero vs. nulo** | 2.6.1 | Una sola regla, `ceroDesde` (10c) | El abandono de una función sigue contando como 0; desde 10d, `vm_panel.csv` usa las mismas reglas |
| L-3 | **Autoselección del registro** (se registra más cuando hay algo que contar) | Captura activa | Marca `retroactivo`; `con_diario` (0/1) en `vm_panel.csv` | Indicador de «días con diario» como covariable en el panel de la app |
| L-4 | **Medición reactiva** (medirse cambia lo medido; efecto Hawthorne personal) | N-de-1 | — | Inevitable; documentar periodos de adaptación |
| L-5 | **Cambios de instrumento** (nuevo reloj, nuevo permiso, nueva versión de la app, viaje o cambio de horario) | Captura pasiva | `schema_v`; desde el esquema 13, la ventana de cada día (`ventana_pantalla`, `ventana_pasos`); desde el 15, `cambio_horario` y `desde_version` en el diccionario | Registrar eventos de cambio como variables de intervención; llenar la tabla `Diccionario.DESDE` |
| L-6 | **Multiplicidad por variables dinámicas** | 2.7 | Filtros por frecuencia | Corrección formal en el análisis (cap. 6) |
| L-7 | **Autocorrelación** (días consecutivos no son independientes) | Estructura temporal | — | Tratada en capítulo 6 (tamaño efectivo de muestra) |
| L-8 | **Generalización** (un solo sujeto) | Diseño N-de-1 | — | Los resultados describen a *esa persona*; no se extrapolan a poblaciones |

: Tabla 2.10. Amenazas a la validez y limitaciones

## 2.11 Qué es estándar y qué es diseño propio

| Decisión | Clasificación | Fuente / justificación |
|---|---|---|
| Unidad de análisis = día | Aproximación | Convención en autoseguimiento; pierde variación intradía |
| ISO-8601 con desfase | Validado | Estándar internacional |
| Leer cada instante con la zona de entonces; historial de zonas armado con «dos notas seguidas» (10f) | Diseño propio (principio validado) | El desfase guardado es lo que permite conservar la hora de reloj de pared |
| Corte de día a las 05:00 | Diseño propio (principio validado) | ATUS usa 04:00 (BLS, s. f.) |
| Almacén de documentos JSON en SQLite | Diseño propio | Flexibilidad vs. rigidez relacional |
| Escritura inmediata (write-through) | Validado | Patrón estándar de persistencia |
| Distinguir cero y nulo | Validado | Teoría de datos faltantes (Rubin, 1976) |
| Rellenar con cero «desde el primer registro», en días que se pudieron responder (`ceroDesde`) | Aproximación | Heurística razonable con supuesto de uso continuo |
| `retroactivo` como moderador de sesgo de recuerdo | Diseño propio | Coherente con recomendaciones de diarios |
| Formato tidy y diccionario | Validado | Wickham (2014); Wilkinson et al. (2016) |
| Filtro de variables dinámicas por frecuencia | Aproximación | Control de dimensionalidad; requiere corrección posterior |

: Tabla 2.11. Qué es estándar y qué es diseño propio

## 2.12 Resumen del capítulo

1. La app recoge datos activos, pasivos e importados y conserva su origen; eso permite comparar autorreporte con medición objetiva.
2. Cada evento guarda su marca de tiempo completa; el **día lógico** (corte 05:00) asigna los eventos nocturnos al día vivido, siguiendo la lógica de las encuestas de uso del tiempo. Desde 10f, cada instante se lee con la zona que tenía el teléfono entonces, y los días con cambio de horario quedan marcados (`cambio_horario`).
3. El almacén (SQLite + documentos JSON + `Repo` en memoria) privilegia flexibilidad y pérdida mínima de datos. Sus opciones tolerantes de lectura dejan fuera los documentos que no se pueden decodificar (desde el esquema 13 se cuentan y se avisan) y pueden convertir un «sin dato» en un valor por omisión (H-1-4).
4. El panel diario distingue **valor, cero estructural y sin dato**. Las reglas de relleno con cero no eran uniformes entre fuentes (hallazgo principal del módulo); desde la entrega 10c hay una sola regla, `ceroDesde`: 0 desde el primer registro de la fuente y solo en los días que se pudieron responder.
5. Las variables dinámicas amplían el panel con elementos de la vida de la persona; su volumen exige control de multiplicidad.
6. La exportación sigue principios tidy y FAIR, con diccionario y script de R.
7. El módulo 1 anotó **9 hallazgos** (H-1-1 a H-1-9) sin cambiar código. Después, el módulo 10 resolvió H-1-1 (salvo el abandono de una función, que sigue contando como 0), H-1-2, H-1-3 y H-1-6, y en parte H-1-9; la entrega 10e resolvió H-1-5 (`gasto_imprevisto_monto`, D.94), y la 10f, H-1-7 (la hora local de entonces, D.109 y D.110) y H-1-8 (`desde_version` en el diccionario, D.115). Sigue abierto solo H-1-4.

**Siguiente capítulo.** Capítulo 3: instrumentos de medición del estado de ánimo y el muestreo (valencia-activación, avisos, validez de escala).


# Capítulo 3. Medición del estado subjetivo

> **Para qué sirve este capítulo.** Explica **cómo mide la app lo que sientes y piensas**: el ánimo en el momento, el ánimo del día, las emociones con nombre, los cuestionarios, los sueños, con quién estuviste y algunas mediciones raras (la brújula política, la prueba de reacción). Para cada cosa dice qué se pregunta, cuándo, en qué escala, cómo se convierte en número para los análisis y qué tan sólido es eso científicamente. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10a…10d» (lo de la revisión de navegación, «desde N1…N7»; lo de la entrega 10e, posterior al módulo 14, «desde 10e», y lo de la entrega 10f, «desde 10f») y está en el Apéndice D. **Los ejemplos numéricos son inventados.**
>
> **Cómo leerlo.** Está escrito para entenderse sin ser experto, pero sin recortar la precisión. Las cifras y reglas que vienen del código se dan tal cual. Cuando algo no lo pude comprobar, lo digo. Cada decisión se marca como **[Validado]** (respaldada por literatura o un estándar), **[Aproximación]** (razonable, pero con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app, sin estándar detrás). Los problemas encontrados se numeran H-2-n (el número es el del módulo 2, en el que se escribió este capítulo, no el del capítulo) y se detallan en `hallazgos.md`.
>
> **Privacidad.** El seguimiento privado (impulsos, recaídas, libido) se describe solo en su mecánica, sin datos ni ejemplos personales.

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

: Tabla 3.1. Mini-glosario

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

: Tabla 3.2. Mapa: qué se pregunta, cuándo y dónde queda

La idea general es **medir lo mismo de varias formas** (en el momento, al final del día, cada semana), porque cada forma tiene sesgos distintos. Las secciones siguientes explican cada una.

## 3.3 Los dos ejes del ánimo: valencia y activación

### 3.3.1 Qué es el modelo circumplejo

Russell (1980) propuso que casi todas las emociones se pueden ubicar en un plano con **dos ejes independientes**: qué tan agradable es la experiencia (**valencia**) y cuánta activación implica (**activación**). Las emociones con nombre quedan como puntos alrededor de un círculo: la *alegría* es agradable y algo activada; la *calma*, agradable y poco activada; la *ansiedad*, desagradable y muy activada; la *tristeza*, desagradable y poco activada (Figura 3.1, derecha). Por eso, se llama **circumplejo** (dispuesto en círculo). **[Validado]**: es uno de los modelos de afecto más usados en psicología, aunque no el único (hay modelos con más dimensiones y modelos de emociones discretas; la app usa ambos, sección 3.6).

**Por qué importan dos ejes y no uno.** Con una sola pregunta («¿cómo te sientes, de mal a bien?») se confunden estados muy distintos: «tranquilo» y «emocionado» son ambos agradables, pero el primero tiene poca energía y el segundo mucha. Separar la activación permite ver, por ejemplo, si después de dormir poco no estás «peor», sino «más apagado».

### 3.3.2 La cuadrícula de la app

Para el ánimo **en el momento**, la app usa una **cuadrícula de 7 × 7** (`CuadriculaAfecto`, en `ui/Selectores.kt`): **un solo toque** da dos números. La columna tocada es la valencia (1 a la izquierda, 7 a la derecha) y la fila es la activación (1 abajo, 7 arriba). Los ejes se cruzan en el centro (4, 4 = neutral), y las esquinas llevan cuatro palabras guía: «tenso, estresado» (arriba a la izquierda), «emocionado, alegre» (arriba a la derecha), «triste, apagado» (abajo a la izquierda) y «tranquilo, relajado» (abajo a la derecha). El color de cada celda va de un extremo a otro según la valencia y es más intenso cuanto mayor es la activación.

*Cómo convierte el toque en números (exacto):* si el toque cae en la posición horizontal `x` de un cuadro de ancho `W`, la valencia es `⌊7·x/W⌋ + 1` (limitada a 1–7); si cae en la altura `y` (medida desde arriba) de un cuadro de alto `H`, la activación es `7 − ⌊7·y/H⌋` (limitada a 1–7).

**Base científica.** Es una versión de la **Affect Grid** (Russell, Weiss y Mendelsohn, 1989), una escala de un solo reactivo para agrado y activación. La publicación original usa una cuadrícula más fina (hasta donde sé, de 9 × 9; no pude comprobar el tamaño en la fuente durante esta sesión). **[Aproximación]**: la idea está validada, pero el formato 7 × 7, las palabras de las esquinas y la colocación en un teléfono son de la app, y no hay estudio que valide exactamente esta versión.

![Figura 3.1. Izquierda: la cuadrícula de la app con 40 registros sintéticos. Derecha: la idea del circumplejo, con posiciones aproximadas. La app **no** coloca sus emociones en el círculo; la figura solo ilustra el modelo. Script: `figuras/fig07_circumplejo.py`.](figuras/fig07_circumplejo.png){width=100%}

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

: Tabla 3.3. La escala del día (1 a 7 con anclas)

Además, pide **dolor o molestia física de 0 a 5**, con anclas por **interferencia** («1 = lo noto, no interfiere» … «5 = no puedo funcionar normal»).

**Por qué 1 a 7.** Una escala con un punto medio (4) permite decir «neutral», y siete opciones dan más detalle que cinco sin volverse difíciles de distinguir. Las **anclas** existen para que un «5» signifique lo mismo hoy que dentro de dos años: sin texto, la gente tiende a «reajustar» lo que significa cada número con el tiempo. **[Aproximación]**: es una práctica común y razonable; las anclas concretas son de la app y no están validadas como instrumento.

**Detalle a saber.** Las anclas de activación mezclan un poco de valencia: «3 = relajado, en calma» suena agradable y «7 = acelerado, inquieto» suena algo desagradable. En el circumplejo puro, la activación no debería sugerir agrado ni desagrado. Además, hasta la entrega N4 la fila de activación usaba **la misma paleta de color** que la de valencia (`Tonos.divergente`, del color «bajo» al «alto»), lo que podía insinuar que poca energía es «peor»; desde N4 usa una paleta de una sola dirección (sección 3.7.1). Las anclas no cambiaron. Es un efecto pequeño y no hay forma de saber cuánto influye (parte de H-2-11).

### 3.3.4 Las dos escalas no son la misma medida

La «valencia» del día y la del momento **comparten nombre y rango (1 a 7) pero no son el mismo instrumento**: una es una fila de números con anclas y pregunta por todo el día; la otra es una cuadrícula sin anclas por número y pregunta por *ahora*. Un 5 en una no equivale necesariamente a un 5 en la otra. Compararlas directamente («mi promedio de avisos es 5.1 y mi día fue 4») mezcla dos cosas. La sección siguiente explica por qué esa diferencia, además, es esperable.

## 3.4 Tres formas de responder «¿cómo estuve?»

Conner y Barrett (2012) distinguen tres «yo» que responden cuestionarios: el **yo que experimenta** (cómo te sientes *ahora*), el **yo que recuerda** (cómo *recuerdas* que te sentiste) y el **yo que cree** (cómo *crees* que eres en general). Robinson y Clore (2002) mostraron que, cuanto más lejos está el momento que se reporta, más se apoya la respuesta en **creencias generales** («los lunes siempre son malos») y menos en la experiencia concreta. **[Validado]**.

La app mide las tres:

| «Yo» | Qué lo mide en la app | Variable |
|---|---|---|
| El que experimenta | Avisos al azar y registros propios (cuadrícula) | `muestreo_valencia` (solo avisos), `animo_valencia` (todos) |
| El que recuerda | Diario de la noche («qué tan agradable fue el día») | `valencia` |
| El que cree | Cuestionarios semanales/mensuales/anuales | `esc_*` |

: Tabla 3.4. Tres formas de responder «¿cómo estuve?»

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

: Tabla 3.5. Cómo elige las horas (exacto)

Dos avisos seguidos quedan separados al menos **2 × mín(45 min, tramo/4)**: el margen del final de un tramo más el del principio del siguiente. Con los valores por omisión, **90 minutos**.

**La separación mínima no siempre es de 90 minutos (H-2-7).** Solo se cumple si cada tramo dura **3 horas o más** (porque el margen se reduce a tramo/4 cuando el tramo es corto). Con la misma ventana y **4** avisos, el tramo es de 165 minutos, el margen de 41.25 y la separación mínima de **82.5 minutos**; con **6** avisos, el tramo es de 110 minutos y la separación mínima de **55 minutos**. No es un error de cálculo. Hasta la entrega 10c el comentario del código decía «separadas al menos 90 minutos»; ahora dice la regla exacta, con ejemplos (`Programador.planDelDia`). Gravedad baja.

### 3.5.3 Cómo llega el aviso (y cuándo se pierde)

**Desde el esquema 20 (decisión del autor del 5 nov 2026), cada aviso tiene su alarma exacta** (`avisos/Muestreo.kt`). La alarma es `setExactAndAllowWhileIdle`, la misma del temporizador de enfoque; si el sistema no deja programarla exacta, se usa una aproximada. Suena a la hora planeada aunque el teléfono esté en reposo. El **pulso** de cada ~15 minutos (capítulo 4) queda como respaldo, por si una alarma se perdió (por ejemplo, al reiniciar el teléfono), y vuelve a programar las que faltan.

**Hasta el esquema 19 no había alarmas.** Cada aviso salía cuando le tocaba correr al pulso: entre 0 y ~15 minutos tarde, y más con el teléfono en reposo, porque Android aplaza esas tareas. Los que más se atrasaban, o se perdían por pasar de 50 minutos, eran justo los de los ratos en que no usabas el teléfono, y eso sesga la muestra hacia los momentos de uso.

Lo dispare la alarma o el pulso, cada aviso se procesa una sola vez (`Muestreo.disparar`):

- lo marca como «ya procesado»;
- **solo si han pasado menos de 50 minutos** desde la hora planeada, muestra la notificación «¿Cómo te sientes ahora?»; con alarma, el retraso es de segundos;
- la notificación **se borra sola a los 60 minutos** de mostrarse (`setTimeoutAfter`).

Si respondes tocando la notificación, el registro se guarda como tipo **`muestreo`**, con la hora planeada en `aviso_ts`. Si en vez de eso abres la app por tu cuenta, el registro se guarda como **`animo`** (iniciativa propia), aunque fuera para contestar el aviso.

**Dos latencias desde el esquema 20** (D.153). Cada aviso mostrado guarda la hora en que salió de verdad (`AvisosDia.reales`; en la exportación, `mostrado_ts`):
- **`latencia_min`**: minutos entre que el aviso salió de verdad y tu respuesta. Queda vacía en los avisos de antes del esquema 20, cuya hora real no se guardó.
- **`latencia_plan_min`**: la medida de siempre, desde la hora planeada, en todas las filas. Incluye el retraso del propio aviso.

Hasta el esquema 19, `latencia_min` era esta segunda medida.

*Ejemplo (inventado).* El aviso estaba planeado para las 11:23, el pulso lo mostró a las 11:31 y contestaste a las 11:40. Antes, `latencia_min` = 17; ahora `latencia_min` = 9 y `latencia_plan_min` = 17. Con alarma, el aviso sale a las 11:23 y las dos dan 17.

**Razonamiento.** En el muestreo de experiencias, el aviso a una hora al azar sirve para captar el estado del momento y no un recuerdo; por eso estos estudios programan el aviso a su hora, registran cuándo sonó y cuándo se contestó, y con esa diferencia separan las respuestas «del momento» de las tardías (Csikszentmihalyi y Larson, 1987; Shiffman, Stone y Hufford, 2008). **[Validado]** como práctica del método. El tope de 50 minutos para mostrar un aviso y la forma de usar la latencia en el análisis son **[Diseño propio]**. Para el autor los avisos son también un recordatorio de anotar el ánimo durante el día. Eso sigue valiendo: la latencia permite decidir en R qué respuestas cuentan como «del momento».

**Lo que queda registrado desde el esquema 13 (P-10).** Cada pulso anota en la colección `avisos`, un documento por día del plan (`AvisosDia`):
- **`planeados`**: cuántos avisos podían sonar ese día. Si cambias los ajustes a mitad del día, el plan se rehace. Entonces se suman los que ya vencieron y los del plan nuevo que todavía pueden sonar; los de un turno ya usado no vuelven a sonar (H-9-11).
- **`vencidos`**: los avisos cuya hora ya pasó y que el pulso ya procesó.
- **`mostrados`**: los que además salieron en pantalla, es decir, con menos de 50 minutos de retraso y con permiso de notificaciones. Desde el esquema 20 también hace falta que las notificaciones de la app estén encendidas y el canal de ánimo no esté silenciado del todo (`Avisos.animoVisible`). Antes, un aviso con el canal apagado contaba como mostrado y bajaba la tasa de respuesta.
- **`reales`** (desde el esquema 20): de cada aviso mostrado, la hora en que salió de verdad.

Al cambiar de zona horaria (un viaje), el plan del día se rehace con la zona nueva, porque guarda instantes fijos que de otro modo caerían de madrugada; los turnos ya usados no vuelven a sonar (desde el esquema 20).

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

![Figura 3.2. Un día sintético con los valores por omisión: tres avisos dentro de sus franjas posibles, una respuesta rápida, un aviso que caduca y los demás registros del día. Script: `figuras/fig08_dia_muestreo.py` (replica las reglas de `planDelDia`, pero no el generador al azar de Kotlin, así que las horas exactas difieren).](figuras/fig08_dia_muestreo.png){width=100%}

### 3.5.4 Por qué se separan avisos y registros propios

Cuando tú decides registrar, sueles hacerlo **porque pasó algo** (una alegría, una preocupación). Eso es un muestreo **por evento**, válido para estudiar esos eventos, pero sesgado si se usa como «cómo estoy en general». Por eso, la variable más limpia del ánimo momentáneo es `muestreo_valencia` (solo avisos), y `animo_valencia` (todo junto) mezcla los dos tipos. **[Validado]** el principio.

Ese mismo sesgo afecta al **mapa emocional de lugares** (pantalla Lugares): colorea cada lugar según la valencia de **todos** los registros de ánimo hechos ahí, avisos y propios, contra tu promedio general, con toda tu historia de ánimo (los puntos grises del mapa sí son solo de los últimos 60 días). Hasta la entrega 10c, el mapa y la lista de la misma pantalla usaban criterios distintos para decidir si un registro «es de ese lugar» (el mapa, solo si quedó asignado al lugar; la lista, también si estaba dentro del radio), y el texto decía «últimos 60 días» aunque las medias usaban toda la historia. Ahora los dos usan el mismo criterio (`enLugar`: asignado al lugar **o** a una distancia menor o igual que su radio) y el texto dice qué es de 60 días y qué no. En R, `vm_mapa_animo` les ponía a los registros sin valencia la del diario; desde 10a usa solo registros con su propia valencia. (H-2-15, resuelto en 10a y 10c, D.10 y D.74.)

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

## 3.8 La predicción de mañana

En la hoja de la noche: «¿Cómo crees que estará tu ánimo mañana?», de 1 a 7. Aunque la pregunta dice «ánimo», la escala es de agrado: sus anclas (`Catalogo.ANCLAS["prediccion_manana"]`) van de «1 = Muy desagradable» a «7 = Muy agradable», las mismas palabras de la valencia del día pero sin sus descripciones («angustia, desesperanza»…). Por eso se compara con la **valencia** del diario del día siguiente, no con la activación. Al día siguiente el panel calcula:

  `error_prediccion` = | predicción de ayer − valencia de hoy |

y, desde 10e (D.94),

  `error_prediccion_signo` = valencia de hoy − predicción de ayer

(`Estadistica.panel`; las dos solo si existen la predicción de la noche anterior y la valencia de hoy). La segunda va de −6 a +6: **positiva** si el día salió **mejor** de lo que predijiste, **negativa** si salió peor. `error_prediccion` es su valor absoluto.

**Para qué.** Las personas solemos equivocarnos al predecir cómo nos sentiremos: sobrestimamos cuánto y cuánto tiempo nos afectarán los eventos (*pronóstico afectivo*; Wilson y Gilbert, 2005). Medir el error permite ver si con el tiempo te conoces mejor y qué días predices peor. **[Validado]** la idea; **[Diseño propio]** la variable.

**Qué se pierde al usar el valor absoluto (H-2-10).** El valor absoluto quita la dirección del error. Ejemplo (inventado): el lunes predices 6 y el martes resulta 4 → error 2 (fuiste **optimista**). El miércoles predices 3 y el jueves resulta 5 → error 2 (fuiste **pesimista**). Para la variable son iguales, aunque son sesgos opuestos. Si quieres saber si tiendes al optimismo, hace falta también el **error con signo**. En la v90 no existía: había que calcularlo en R con `prediccion` del día anterior y `valencia`. Desde 10e es `error_prediccion_signo` (H-2-10, resuelto). Con el ejemplo: el martes vale 4 − 6 = **−2** (optimista) y el jueves 5 − 3 = **+2** (pesimista). Si su promedio en un mes es −0.8, ese mes tendiste a esperar días mejores de los que tuviste. **[Diseño propio]** la variable, como la otra.

**Cuándo falla.** (a) Hereda la regla de la otra: sin predicción la noche anterior o sin valencia hoy, queda vacía, y los días que faltan no son al azar (sección 3.15). (b) Como se calcula con la valencia de hoy, **se mueve con `valencia` por construcción**: un día bueno tiende a dar un error positivo aunque la predicción no cambie. Por eso `Estadistica.FAMILIAS` la marca como obvia con `prediccion`, `error_prediccion` y `valencia` (desde 10e, también `error_prediccion` con `valencia`): esas parejas no salen en «Las relaciones más marcadas» salvo con «Mostrar también las obvias» (capítulo 2, sección 2.7).

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

: Tabla 3.6. Los cuatro instrumentos

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

![Figura 3.3. PSS-4 sintético respondido cada semana. Línea discontinua: lo que guardaba el panel hasta la entrega 10c (ventana de 30 días, gana la última respuesta). Línea continua: lo que guarda ahora (los 7 días que pregunta cada respuesta, gana la primera que cubre el día). Script: `figuras/fig10_cuestionarios_ventana.py` (replica las dos versiones de `Dinamicas.kt`).](figuras/fig10_cuestionarios_ventana.png){width=100%}

La app sí evita un error relacionado: dos puntajes del **mismo** cuestionario (por ejemplo, extraversión y apertura del TIPI) se marcan como relación «obvia», porque salen de las mismas respuestas.

## 3.10 La brújula política

Una vez al año (la primera vez, cuando ya hay al menos 14 días con documento de diario), 25 afirmaciones **escritas para la app** (`data/Politica.kt`), 5 por eje, respondidas de 1 (muy en desacuerdo) a 5 (muy de acuerdo). Ejes: economía (igualdad y Estado ↔ mercado y empresa), autoridad (libertad individual ↔ orden y autoridad), valores (cambio ↔ tradición), mundo (apertura ↔ soberanía) y ambiente (proteger el ambiente ↔ crecimiento primero). El orden de las afirmaciones se mezcla, con el mismo orden durante todo el año.

**Cálculo (exacto).** Cada afirmación tiene un **signo**: +1 si estar de acuerdo acerca al polo «positivo» del eje y −1 si al «negativo». Para cada eje:

  posición = Σ (respuesta − 3) × signo × 100 / (2 × número de afirmaciones respondidas)  (−100 a +100)

y se describe (`Politica.lado`) como «muy hacia…» (≤ −60 o ≥ 60), «hacia…» (más de −60 y hasta −20, o desde 20 y menos de 60) o «al centro» (más de −20 y menos de 20). Los bordes son simétricos: ±20 ya es «hacia…» y ±60 ya es «muy hacia…».

**Para qué sirven los signos mezclados.** Hay personas que tienden a estar de acuerdo con todo (*aquiescencia*). Si todas las afirmaciones apuntaran al mismo lado, esa tendencia se confundiría con una postura. Mezclar afirmaciones en ambos sentidos la neutraliza… **si están equilibradas**.

**No lo están del todo (H-2-6).** Con 5 afirmaciones por eje el reparto es 3 contra 2. Si alguien responde «muy de acuerdo» (5) a las 25, cada eje queda en ±2 × 100 / 10 = **±20**, que la app describe como «hacia…» en los **cinco** ejes (hacia igualdad y Estado, hacia orden y autoridad, hacia cambio, hacia soberanía, hacia proteger el ambiente). El comentario del código decía que estar de acuerdo con todo «no empuja hacia ningún lado»; desde la entrega 10c dice que sí empuja un poco (±20) y que, al ser el mismo cada año, afecta poco a compararte contigo mismo. El cálculo no cambió. Con «de acuerdo» (4) en todo, el efecto es ±10 y queda «al centro». Gravedad baja: es un instrumento para compararte contigo mismo año con año, y ese sesgo es constante. **[Diseño propio]**: las afirmaciones no están validadas como escala.

En el panel, cada eje (`pol_<eje>`) vale lo mismo desde el día del test hasta el siguiente (o 400 días).

## 3.11 «Conocerte»: el perfil y las etapas de vida

«Conocerte» (`data/Perfiles.kt`, pantalla `Conocer.kt`) pregunta lo grande de tu vida: con quién vives, a quién ves más, pareja, ocupación, horario, cronotipo (de mañana o de noche), hábitos generales, ciudad, transporte, dieta, etc. Cada vez que cambias una respuesta se guarda en un **historial** con la fecha (`perfil_historial`).

Con eso, el panel crea variables de **etapa de vida** (`vida_en_pareja`, `vida_vive_solo`, `vida_trabaja`, `vida_desde_casa`, ciudad…): cada día sabe en qué etapa estabas, para comparar, por ejemplo, tu ánimo cuando vivías solo y cuando no. La función `valorEn(campo, fecha)` decide el valor de un día así: el último cambio **antes o en** esa fecha; si no hay, el valor **«antes»** del primer cambio posterior; si nunca hubo cambios en ese campo, **el valor actual**.

**El valor actual se proyectaba hacia atrás (H-2-5).** Si llenabas el perfil por primera vez (por ejemplo, «en pareja»), **todos los días anteriores**, incluidos años de historia importada, quedaban como «en pareja», porque el primer guardado no dejaba historial. Gravedad media.

**Desde la entrega 10c (D.74)** el primer guardado también deja historial: `Perfiles.cambios(antes, despues)` se aplica aunque el perfil estuviera vacío, y registra cada campo llenado como un cambio de «» a su valor, con la fecha de ese día. `Perfiles.primerGuardado(historial)` reconoce esa fecha: es el primer día del historial si **todos** los cambios de ese día pasaron de vacío a algo. Para los campos llenados ese día, `valorEn` devuelve «sin dato» en las fechas anteriores, y las variables `vida_*` quedan vacías (también `vida_mascota` y `vida_idiomas`, que antes daban 0, y `vida_ciudad_*`). Ejemplo: llenas «Conocerte» por primera vez el 10 de octubre con «en pareja»; `vida_en_pareja` vale 1 desde el 10 de octubre y queda vacía antes. Ese primer llenado tampoco sale en «Antes y después» (capítulo 6), porque no es un cambio de vida.

Lo que no se arregla: los perfiles llenados antes de esta versión no tienen ese primer registro, y sus valores se siguen proyectando hacia atrás. Y si la primera edición de un perfil viejo consistió solo en llenar campos vacíos, `primerGuardado` la toma por un primer guardado y deja sin dato esos campos en los días anteriores. **[Diseño propio]**.

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

## 3.13 Presencias: estuve, pensé, mensajes

Cuando nombras a una @persona en el diario, la app pregunta (con un toque sobre el nombre) cómo fue: **estuve con** (la viste en persona), **pensé en** (no la viste) o **mensajes** (hablaron sin verse). Está en `data/Presencias.kt`.

Reglas exactas:

- **En el diario, el estado por omisión es «estuve con»**: si no tocas el nombre, cuenta como que la viste (este punto ya está anotado como H-1-6 en el capítulo 2). El día queda «revisado» (`presencias_ok`) al tocar un nombre debajo del diario o al confirmarlo en la pantalla de revisión de presencias.
- **Si alguien aparece solo en momentos** (no en el diario), cuenta como «pensé en» si ese momento era de tipo «pensando en» o «recuerdo» **y** estabas solo o no marcaste compañía; en otro caso, como «estuve con». Si aparece en varios momentos, basta uno de «estuve» para que cuente como «estuve».
- Lo que marcas en el diario **manda** sobre lo que dicen los momentos.
- Los momentos privados no se usan para esto.

**Razonamiento.** Nombrar a alguien no es haberlo visto, y para estudiar el efecto del contacto social sobre el ánimo importa la diferencia entre ver, pensar y escribir. **[Diseño propio]**, razonable. Cómo se usan estas presencias en la red de personas y en las variables `vi_*` y `penso_en_*` se explica en el capítulo 7.

## 3.14 Reactividad: medir cambia lo medido

**Qué es.** Que el acto de registrar cambie la conducta o el estado que se registra. Puede pasar porque registrar te hace **prestar atención** (te das cuenta de que duermes poco y duermes más), porque **nombrar** una emoción la modifica (sección 3.6.4) o porque quieres **verte bien** en tus propios datos (más aún si hay logros y rachas; capítulo 9).

**Qué dice la evidencia.** En un estudio con pacientes con dolor crónico que registraban su dolor cada hora durante una semana, el registro intensivo no cambió en promedio el dolor reportado (Cruise et al., 1996). Hasta donde sé, la literatura en general encuentra efectos de reactividad pequeños en estos diseños, más probables en conductas que la persona quiere cambiar; no verifiqué esa conclusión general con una revisión durante esta sesión **[sin verificar]**. **[Validado]** que puede no haber reactividad apreciable en el ánimo o el dolor; no se puede descartar para hábitos y conductas.

**Qué implica para la app.**

- Los cambios **al inicio** del uso (o al empezar a medir algo nuevo) pueden deberse en parte a empezar a medir. Conviene no sacar conclusiones de las primeras semanas.
- Las conductas que quieres cambiar (hábitos, sustancias, el seguimiento privado) son las más expuestas.
- Ver tus propios números antes de responder (sección 3.4) es una forma de reactividad dentro del mismo día.
- Desde la entrega N4 de la revisión de navegación, Hoy muestra **«Tus últimos días»**: 14 hilos con el color de la valencia de cada día, el alto del sueño frente a tu normal y si el día quedó sellado (`FranjaDias`, en `ui/pantallas/Hoy.kt`). Pone tu historial de ánimo en primer plano. Para no influir en cómo calificas el día, el hilo de hoy no se colorea hasta que tiene valencia y diario, y la franja no aparece en la hoja de la noche. Aun así, puede influir; se puede apagar en Apariencia. **[Diseño propio]**.

## 3.15 Datos que faltan: por qué no da igual cuándo faltan

### 3.15.1 El problema

En un registro personal, los datos **no faltan al azar**. Lo típico es no llenar el diario precisamente los días malos (por cansancio, tristeza o porque no hay ganas de pensar en ellos), o no contestar avisos cuando estás ocupado o con gente. Cuando la probabilidad de que falte un dato depende del propio dato, se habla de faltante **no al azar** (MNAR; Rubin, 1976), y los promedios quedan sesgados de una forma que **no se puede corregir solo con los datos que sí hay**.

**Ejemplo (inventado, Figura 3.4).** Diez días con valencias reales 5, 6, 5, 2, 6, 5, 3, 6, 5, 2 (promedio **4.50**). Si los tres días malos (2, 3, 2) no se registran, el promedio de lo registrado es 38 / 7 = **5.43**: casi un punto más alto. Todas las correlaciones con la valencia quedan calculadas sobre «los días que tuviste ganas de registrar».

### 3.15.2 Lo que hace la app

**El registro de un toque.** La notificación del diario trae un botón «**Día difícil: solo un número**» que abre una pantalla con **una sola escala de valencia**. Al tocarla se guarda la valencia del día con la marca `dia_dificil = 1` (`QuickActivity.Minimo`). La idea: que llenar el día cueste un segundo justo cuando menos ganas hay. «Un día registrado así vale mucho más que un día vacío», dice la propia pantalla, y es correcto: en el ejemplo, con esos tres números el promedio vuelve a 4.50. **[Diseño propio]** con buen fundamento. La variable `dia_dificil` permite además ver cuántos días se registraron así.

**Otros apoyos:** desde la entrega N2 (D.37), Hoy muestra como pendientes los días de los 7 anteriores que no tienen ni valencia ni diario («Diario de ayer» o «Te faltan…», `Pendientes.faltantes`), a cualquier hora, hasta que los llenes; descartarlo los oculta solo ese día (antes, el diario de ayer dejaba de pedirse a las 14:00). Además, los cuestionarios insisten más con el atraso; la «semana corta» reduce la app a lo mínimo; y `retroactivo` separa lo escrito tarde (desde el esquema 13, con `ts_valencia`; sección 3.7.4).

**Lo que faltaba hasta el esquema 13:** saber **cuántos avisos se mostraron** (H-2-3). Desde entonces `tasa_respuesta` está en el panel (sección 3.5.3). Con ella se puede ver si los avisos sin respuesta se concentran en ciertos días u horas, y usarla como covariable: por ejemplo, ver si los días con tasa baja son también los de valencia baja del diario.

![Figura 3.4. Cómo los días malos sin registro inflan el promedio, y cómo el registro de un toque lo corrige (datos sintéticos). Script: `figuras/fig09_dia_faltante.py`.](figuras/fig09_dia_faltante.png){width=100%}

### 3.15.3 Editar después

Un registro de ánimo se puede editar más tarde con `DialogoMomento`: valencia, activación, emociones, pensamiento, texto y, desde el esquema 14, también **la fecha y la hora**. Se abre tocándolo en el Día de su fecha (sección «Tu día»), en Diario → «Línea de tiempo» o en Privado.

Desde la entrega N3 de la revisión de navegación, «Tu día» lista todo lo que tiene hora ese día (momentos, consumos, tramos, gastos, ingresos, tomas y notas; lo privado no) y cada fila abre su corrección. «＋ agregar a este día…» abre el registro de ánimo, el gasto, la nota o las tomas **con la fecha de ese día ya puesta**. El registro de ánimo empieza a mediodía de ese día y la hora se elige ahí mismo. Todo lo que se agrega así guarda `ts_creado` con la hora real en que se anotó, así que `vm_retraso()` lo marca como anotado tarde.

Hasta el esquema 13, el registro **no guardaba que fue editado ni cuándo**, así que un dato «momentáneo» podía convertirse en retrospectivo sin que se supiera (H-2-9). Desde el 14:
- cada corrección guarda `ts_editado`, y cada registro nuevo, `ts_creado` (cuándo se anotó);
- al cambiar la hora, el día se recalcula con el día lógico y el lugar se toma de las ubicaciones de esa hora (`Recaidas.aOtraHora`); la luz y el ruido medidos al registrar se descartan;
- un momento de antes, con tipo «idea», «alegría», etc., pasa a tipo `animo` al corregirlo: lo que sentías y pensabas ya va explícito en `emociones` y `pensamiento`. Por eso, los conteos `m_<tipo>` de esos tipos viejos pueden bajar un poco en los días con correcciones.

En R, `vm_retraso()` dice qué registros se anotaron tarde (Apéndice C). Los registros con `ts_editado` se pueden excluir si se quiere solo lo momentáneo sin tocar.

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

## 3.17 Una medida que no es autorreporte: la prueba de reacción

En «Medir» hay una **prueba de reacción** breve (`PantallaReaccion`, en `ui/pantallas/Medir.kt`): **10 intentos**; en cada uno se espera un tiempo al azar entre **1.5 y 4.0 segundos** y aparece un círculo; hay que tocar lo antes posible. Se guarda el tiempo de cada intento, la **media**, la **mediana**, los **lapsos** (respuestas de **500 ms o más**) y las **anticipaciones** (toques antes de que aparezca el círculo). Entre intentos hay una pausa de 0.6 s.

**Base científica.** Es una versión muy corta de la **prueba de vigilancia psicomotora (PVT)**, la medida estándar de cómo la falta de sueño afecta la atención (Basner y Dinges, 2011). La PVT estándar dura **10 minutos**, con esperas de **2 a 10 s**, cuenta como lapso un tiempo de **500 ms o más** y como **falsa salida** tanto tocar sin estímulo como responder en **menos de 100 ms** (demasiado rápido para ser una reacción real). La versión breve validada (PVT-B) dura **3 minutos**, con esperas de **1 a 4 s** (Basner, Mollicone y Dinges, 2011), y sus autores encontraron que para ella funcionaba mejor un umbral de lapso de **355 ms**.

**Comparación (H-2-12).** La pantalla dice «unos 30 segundos»; con 10 esperas de 1.5 a 4.0 s (2.75 s en promedio), 9 pausas de 0.6 s y los tiempos de reacción, sale más cerca de **unos 36 s** en promedio (de ~22 a ~50 s con reacciones de 0.15 a 0.5 s; cada anticipación suma 0.9 s y una espera nueva; cálculo propio a partir de `PantallaReaccion`). Es mucho menos que cualquier versión validada; usa el umbral de 500 ms (pensado para la prueba de 10 minutos); hasta la entrega 10c **no marcaba como falsas salidas las respuestas de menos de 100 ms** (entraban en la media). Desde 10c, un toque a menos de 100 ms del estímulo cuenta como anticipación (suma a `anticipaciones`), no entra en los tiempos y el intento se repite, como en la PVT (D.74). Sigue midiendo en una pantalla táctil, con retrasos de dibujo y de detección del toque que varían entre teléfonos. La propia app advierte que solo sirve para compararte contigo mismo. **[Aproximación]**: útil para ver tendencias grandes, con mucho ruido de un día a otro. En el panel, `rt_media` es el promedio de las medias de las pruebas hechas ese día.

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

: Tabla 3.7. Amenazas a la validez (resumen)

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

: Tabla 3.8. Qué es estándar y qué es propio

## 3.20 Resumen del capítulo

1. La app mide el ánimo en **dos ejes** (valencia y activación) y de **tres formas**: en el momento (avisos al azar y registros propios), al recordar el día (diario) y como creencia general (cuestionarios). Cada forma tiene sesgos distintos y **no son intercambiables**.
2. Los avisos usan **muestreo aleatorio estratificado**: 3 al día por omisión, uno por tramo de la ventana 10:00–21:00, con margen de 45 minutos. Es un diseño estándar.
3. El hallazgo más grave del módulo era que **los puntajes de los cuestionarios se asignaban en el panel a días de otras semanas** (ventanas de 14 y 30 días hacia atrás, con sobrescritura) (H-2-1). Desde la entrega 10c, cada puntaje cubre solo los días que pregunta y cada día se queda con la primera respuesta que lo cubre (D.62).
4. Varias variables codificaban «no respondí» como 0 (`sueno_recordado`, contextos, hábitos, «las tres» necesidades) (H-2-4); en 10c se corrigieron `sueno_recordado`, los hábitos antes de existir y «las tres» (D.63); los contextos siguen la convención de la pantalla. El perfil se proyectaba hacia atrás (H-2-5; corregido para los perfiles nuevos en 10c, D.74); la marca `retroactivo` no reflejaba cuándo se llenó el ánimo (H-2-2; corregido en 10b). Los cierres guardados otro día ya no cuentan en el panel (D.74).
5. Hasta el esquema 12 no se guardaba cuántos avisos se mostraron, así que no hay tasa de respuesta de esos días (H-2-3); desde el esquema 13 (10b) el panel tiene `avisos_*` y `tasa_respuesta`.
6. El **registro de un toque** para días difíciles es una buena defensa contra el sesgo de los días malos sin registrar.
7. Los cuestionarios están bien puntuados (incluidas las preguntas inversas), pero WHO-5 y PSS-4 usan un periodo de 7 días distinto al original, y algunos puntos de corte que muestra la app no los pude verificar.
8. El módulo 2 identificó **15 hallazgos** (H-2-1 a H-2-15) y no cambió código. Después, el módulo 10 resolvió H-2-1, H-2-2, H-2-3, H-2-4, H-2-7, H-2-12 y H-2-14, y en parte H-2-5, H-2-6 y H-2-15; la revisión de navegación (esquema 14) resolvió H-2-8 (completado en 10c), H-2-9 y H-2-11; el módulo 10d, en parte, H-2-13, y la entrega 10e, H-2-10 (`error_prediccion_signo`, D.94). Ya no queda ninguno pendiente.


# Capítulo 4. Sensores, datos pasivos e importaciones

> **Para qué sirve este capítulo.** Explica, fuente por fuente, **qué mide la app sin que tú escribas nada** (datos *pasivos*) y **qué datos entran cuando tú cargas un archivo** (datos *importados*): qué dato es, cómo se obtiene exactamente, qué tan confiable es, qué hace la app cuando falta y a qué día se asigna. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10a…10d» (o «desde el esquema 13»), lo de la entrega 10e, posterior al módulo 14, «desde 10e», lo de la entrega 10f (las decisiones del autor sobre los hallazgos pendientes) «desde 10f», y todo está en el Apéndice D. Los ejemplos numéricos son **inventados**; no hay datos reales aquí.
>
> **Cómo leerlo.** Está escrito para entenderse sin ser experto, pero sin recortar la precisión: cuando una cifra o regla viene del código, se da tal cual (umbrales, ventanas de tiempo, mínimos). Cuando algo es una suposición mía o no pude comprobarlo, lo digo. Cada decisión se marca como **[Validado]** (respaldada por literatura o un estándar), **[Aproximación]** (razonable, pero con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app, sin estándar detrás). Los problemas encontrados se numeran H-3-n (el 3 es el número del módulo que los encontró, no del capítulo) y se detallan en `hallazgos.md`.

## 4.1 Mini-glosario (palabras que aparecen mucho)

| Término | Qué significa aquí |
|---|---|
| **Dato pasivo** | Lo que el teléfono o el reloj registran solos (pasos, pantalla, ubicación…). Tú no haces nada. |
| **Dato importado** | Lo que llega de un archivo que tú exportas de otro servicio y cargas en la app (Spotify, Google, WhatsApp…). |
| **Proxy** | Una medida *indirecta* de lo que de verdad nos interesa. Ejemplo: «luz fuerte del teléfono» es un proxy de «estuve al aire libre», porque no mide el aire libre, sino la luz. |
| **Mediana** | El valor del medio al ordenar una lista. Aguanta mejor los valores raros que el promedio. |
| **hPa** | Hectopascales, la unidad de presión del aire (a nivel del mar, ~1013 hPa). |
| **RMSSD** | Una forma de medir la variabilidad del corazón (sección 4.5.2). |
| **Día lógico** | El día de la app: empieza a las 05:00 (capítulo 2, sección 2.3). Un evento a las 01:00 cuenta para el día *anterior*. |
| **Día de calendario** | El día normal, de 00:00 a 24:00. |
| **Zona horaria y desfase con UTC** | La zona es la regla de hora de un lugar («America/Mexico_City»); el desfase, cuántas horas va ese lugar respecto a la hora universal (UTC): −6 en la Ciudad de México, +2 en Madrid en verano. Un mismo instante es las 15:30 en un sitio y las 23:30 en el otro (sección 4.3). |
| **Pulso** | La tarea que la app ejecuta cada ~15 minutos en segundo plano (sección 4.2). |

: Tabla 4.1. Mini-glosario (palabras que aparecen mucho)

## 4.2 Cómo y cuándo llegan los datos

### 4.2.1 El «pulso» de 15 minutos

La app programa una tarea periódica (`Pulso`, con `PeriodicWorkRequestBuilder` de WorkManager) **cada 15 minutos**. En cada pulso hace, en este orden: guarda un punto de ubicación, toma una «muestra de percepción» (wifi, audio, carga, luz, presión), revisa si llevas rato en un lugar nuevo, consulta Spotify (si pasó al menos 25 minutos desde la última vez), decide si toca algún aviso y, una vez al día, sincroniza lo demás.

**Qué significa «cada 15 minutos» en la práctica.** 15 minutos es el **mínimo** que Android permite para tareas periódicas, y el sistema puede **retrasarlas** para ahorrar batería (por ejemplo, si el teléfono está en reposo profundo). Es decir, el pulso es una *intención*, no un reloj exacto. Por eso todos los cálculos de tiempo que dependen del pulso usan el tiempo *real* transcurrido entre una lectura y la anterior, no «15 minutos fijos» (sección 4.4.5).

### 4.2.2 La sincronización diaria

Una vez al día, después de las 06:00 (`Trabajo.kt`, función `diario`), la app trae del teléfono (desde el 5 nov 2026, cada paso se anota como hecho al terminar y no antes: si Android corta la tarea a los 10 minutos, el siguiente pulso sigue donde se quedó, con un máximo de dos intentos por paso; D.152), en este orden: reloj (últimos **3** días), pantalla (últimos **2** días), registra de nuevo las geocercas y el reconocimiento de actividad, fotos (3 días), agenda, llamadas/SMS (3 días), clima y un respaldo automático. A partir de las 21:00 hay una **segunda** pasada más corta (1 día de pantalla, reloj, fotos y llamadas, más la agenda). Además, una vez al mes (después de las 13:00) rellena la historia que guardan la galería (hasta 400 días con registro y sin conteo de fotos), el calendario y el registro de llamadas (hasta un año atrás), y pide clima atrasado (hasta 20 peticiones).

Hay un botón «Sincronizar todo ahora» (`Sincronizar.todo`): en Permisos y sensores lee 7 días; el de Estado de los datos, y los de Hoy y el Día, 3 días. Hay otros para rangos mayores.

**Por qué importa la ventana de 2–3 días.** Si la app no corrió durante más días (teléfono apagado, ahorro de batería agresivo), esos días **no se recuperan solos**. Para el reloj hay botones que leen hasta 30 días; para la pantalla, el botón manual calcula 7 días. Android conserva los eventos de uso por un tiempo limitado, así que el historial de pantalla de días lejanos puede ya no existir en el sistema (esto último es comportamiento de Android que conozco pero no pude comprobar en la documentación oficial durante esta sesión).

### 4.2.3 Permisos y el panel «Estado de los datos»

Cada fuente necesita un permiso del sistema. Sin permiso no hay dato, y la app **no inventa nada**: la variable queda vacía (`null`, capítulo 2, sección 2.6). La pantalla **Estado de los datos** (`EstadoDatos.kt`) reúne el estado de cada fuente en cuatro categorías: *Al día*, *Atrasada*, *Faltan permisos* y *Apagada*. Cada fuente tiene su tolerancia en días (por omisión 1: pantalla, clima, reloj; fotos y llamadas, 3; agenda, 7; notificaciones y wifi/luz, 0, es decir, deben haber llegado hoy).

**Razonamiento.** Saber *por qué* falta un dato es tan importante como el dato. Un hueco por «sin permiso» o «teléfono apagado» no es lo mismo que un hueco porque «no pasó nada», y confundirlos sesga el análisis (teoría de datos faltantes, capítulo 2). La pantalla de estado deja ver los huecos, pero **la app no guarda una marca de «hueco por permiso» en el panel**: el análisis ve solo una celda vacía (limitación L-4.1, sección 4.9).

![Figura 4.1. Fuentes de datos pasivos e importados: cómo llega cada una, cuándo se sincroniza y qué «día» usa. Esquema, sin datos. Script: `figuras/fig04_fuentes_datos.py`.](figuras/fig04_fuentes_datos.png){width=100%}

## 4.3 Dos relojes: día lógico y día de calendario (¡lee esta sección!)

En el capítulo 2 vimos que la app define el «día» de 05:00 a 04:59. **Pero no todas las fuentes pasivas usan esa definición.** Según el código:

| Usan **día lógico** (05:00 → 04:59) | Usan **día de calendario** (00:00 → 24:00) |
|---|---|
| Ubicación (km del día), Movimiento, Wifi/audio/carga/luz/presión, Notificaciones, Música (Oyente), Fotos, Llamadas/SMS, Spotify, Google Takeout, WhatsApp, Kindle, ejercicio del reloj (día del inicio) y, **desde el esquema 13, Pantalla y pasos** | **Pantalla** (hasta el esquema 12), **Reloj** (pasos hasta el esquema 12; corazón, oxígeno, calorías…), **Agenda**, **Clima/aire** (por naturaleza son datos por día de calendario), **ciudades y países** (`Mundo`), Cold Turkey y MacroFactor (el archivo trae la fecha) |
| El **sueño** del reloj se asigna al **día en que despiertas** (fecha de calendario del final de la sesión). | |

: Tabla 4.2. Dos relojes: día lógico y día de calendario (¡lee esta sección!)

**Ejemplo (inventado), como era hasta el esquema 12.** Un lunes te quedas usando el teléfono de 00:30 a 01:30 de la madrugada del martes.
- Esa hora de **pantalla** se sumaba al **martes** (calendario).
- Las **notificaciones** que llegaron a las 01:00 se cuentan para el **lunes** (lógico).
- El **wifi** de esa hora también va al **lunes**.

Resultado: dentro de una misma fila del panel (digamos «martes»), la pantalla incluía una hora que las notificaciones y el wifi del mismo martes *no* incluyen. Ese es el hallazgo **H-3-1** (gravedad alta, porque afecta justo las variables de uso nocturno, que son las más interesantes para el sueño).

**Por qué importa científicamente.** Cuando se cruzan dos variables medidas con ventanas distintas, parte de la relación (o de la falta de relación) viene de que «no hablan del mismo día». Para quien duerme tarde, el desfase es de varias horas.

**Qué ocurre en la práctica.** Para alguien que se duerme antes de la medianoche, ambos criterios casi coinciden y el efecto es pequeño. Para quien usa el teléfono de madrugada, el efecto es real. La propuesta del módulo 3 fue usar un solo criterio o, al menos, declarar el de cada variable; el módulo 10 hizo las dos cosas en parte (abajo). **[Aproximación]** mientras queden variables por calendario.

**Lo que cambió en el esquema 13 (entrega 10b; P-16 ii, decisión D-3 del autor).** La pantalla y los pasos pasan al día lógico de aquí en adelante. En el ejemplo, la hora de 00:30 a 01:30 de pantalla cuenta ahora para el **lunes**, igual que las notificaciones y el wifi. Cada día guarda qué ventana usó (`PantallaDia.ventana`, `SaludDia.ventana_pasos`: `"logica"` o `"calendario"`), y el panel exportado lleva `ventana_pantalla` y `ventana_pasos`, para analizar por separado los dos tramos de la serie. La app no recalcula por su cuenta los días de antes: Android solo guarda unos días de eventos de uso, y mezclar días recalculados con otros sin recalcular sería peor. Los botones manuales sí vuelven a calcular, con la ventana nueva, los días que cubren (7 de pantalla, hasta 30 del reloj), y esos días quedan marcados `"logica"`. Lo que **no** cambió: el corazón, el oxígeno y las demás lecturas del reloj (salvo pasos, sueño y ejercicio) siguen por día de calendario, y la agenda, el clima y las ciudades también. Desde la entrega 10c (P-16 i, D.64) eso queda declarado: el LEEME de la exportación lo dice y el diccionario marca «por día de calendario» en cada una de esas variables del panel (`Diccionario.CALENDARIO`); además, `lugares_distintos` pasó al día lógico. Por eso H-3-1 queda **resuelto en parte**.

Una consecuencia concreta (**H-3-2**), hasta el esquema 12: `primer_uso_h` (hora del primer uso del día) se calculaba con la ventana de calendario, así que el uso de las 01:00 de la madrugada aparecía como «primer uso» a la 01:00, no a la hora real en que te levantaste. Y la corrección «+24 si la hora es menor a 5» de `ultimo_uso_h` casi nunca llegaba a actuar, porque la ventana de calendario termina a las 24:00. Desde el esquema 13, con la ventana del día lógico, el primer uso es el de después de las 05:00 y la corrección sí actúa (`Estadistica.panel`); los días anteriores quedan como estaban y se reconocen por `ventana_pantalla`.

**La hora local de entonces (desde 10f; H-1-7, D.109 y D.110).** Además del corte del día, importa **en qué zona horaria** se lee cada hora. Casi todo lo que guarda el teléfono es un **instante** (milisegundos desde 1970, igual en todo el mundo); la hora y el día salen de convertir ese instante con una zona. Hasta la v91, `Fechas.zona()` era la zona que el teléfono tiene **hoy** (`ZoneId.systemDefault()`), y con ella se convertía todo, también lo de hace meses (H-1-7).

*Ejemplo (inventado).* Vives en UTC−6 y pasas una semana en Madrid (UTC+2). Allá te duermes a las 23:30, hora de Madrid. El reloj guarda la hora de dormir con su desfase (`…T23:30:00+02:00`). Al volver, la app convertía esa marca a la zona de hoy: 15:30, y `sueno_inicio_h` salía 15.5 en vez de 23.5. Del mismo modo, un punto de ubicación de las 09:00 del miércoles en Madrid (01:00 del miércoles en UTC−6) pasaba a contar para el día lógico del **martes**.

**Cómo es desde 10f.**
- **El historial de zonas.** La app guarda cada cambio de zona del teléfono en `Config.zonas`: una lista de «desde este instante, esta zona» (`CambioZona`). `Repo.registrarZona` anota la zona si cambió: al abrir la app, en cada pulso de 15 minutos y cuando Android avisa de un cambio de zona (`TIMEZONE_CHANGED`, en `ArranqueReceiver`).
- **Cada instante con su zona.** `Fechas.zonaEn(ms)` busca en el historial la zona que regía en ese instante; antes del primer cambio guardado usa la primera zona de la lista, y con el historial vacío, la de hoy. Con ella se calculan la hora local (`local`), el día de calendario (`diaDeMillis`), el día lógico (`diaLogicoDeMillis`) y la marca ISO (`isoDeMillis`).
- **Al revés: de una hora local a un instante.** `Fechas.millisDe` usa la zona que regía ese día. Así se calculan los bordes de cada día (`inicioDiaLogico`, `finDiaLogico`, `inicioDia`, `finDia`), cada uno con la zona de esa hora. Por eso un día con cambio de horario dura 23 o 25 horas, y el día de un viaje, las horas que toque: de UTC−6 a UTC+2, un día lógico dura 16 horas.
- **Las marcas con desfase y las marcas en UTC.** Una nota guardada con su desfase se lee con **ese** desfase (`localDe`, `horaDe`, y en el panel `Exportar.horaDecimal`). Una marca en UTC (las que traen las importaciones, como el historial de Spotify) se pasa a la zona de entonces.

**Lo guardado antes del historial.** La primera vez, `registrarZona` arma el historial con lo que ya hay: revisa las marcas de los momentos, las respuestas, las reacciones y las tomas de suplementos, en orden. Si **dos o más notas seguidas** traen un desfase que la zona de hoy no daría en ese instante, ese periodo queda con ese desfase (un desfase fijo, como «+02:00»); todo lo demás, con la zona de hoy. Una nota suelta con un desfase raro (por ejemplo, editada en otra zona) no abre un periodo. **[Diseño propio]**: reconstruye bien los viajes en que escribiste al menos dos notas; uno sin notas, o con una sola, se queda con la zona de hoy.

**Las marcas en la exportación.** `vm_panel.csv` trae `desfase_utc_h` (el desfase con UTC a mediodía de ese día, `Fechas.desfaseH`: −6 en la Ciudad de México) y `cambio_horario` (1 si el desfase al empezar el día lógico no es el mismo que al terminar, `Fechas.cambioDeHora`: horario de verano o viaje). El Día lo dice debajo de la fecha, por ejemplo «cambio de horario: de UTC−6 a UTC+2 · el día duró 16 h». En R, `vm_hora_local(ts)` da la hora de una marca con su propio desfase (7.5 = 7:30); `vm_fecha_hora` sigue pasando todo a `vm_zona` (es el mismo instante, mostrado con otra hora).

**Quiénes lo usan.** Health Connect, la pantalla, las fotos, WhatsApp, Kindle, las ubicaciones, Spotify y Google, además de la tabla por hora, la cinta y la estadística (cada ficha lo dice abajo).

**Cuándo falla.**
- Es la zona **del teléfono**, no la tuya: si el teléfono no ajusta la hora solo al viajar, la app no se entera del viaje.
- Si Android no avisa del cambio, se anota en el siguiente pulso o al abrir la app. Lo de ese rato queda con la zona anterior.
- Lo que el teléfono ya había guardado **por día** antes de 10f (sueño, pasos, pantalla, notificaciones, wifi) se calculó con la zona que tenía al sincronizar o al anotarlo, y no se recalcula. Lo que sí cambia es lo que la app saca de un instante al leer: las horas del panel, la tabla por hora, los kilómetros por día y los días del Mundo.
- Los días de 23 o 25 horas (y los de un viaje) acumulan más o menos minutos que un día normal en las variables que suman tiempo. `cambio_horario` permite separarlos.
- No se probó en un teléfono que llegue el aviso `TIMEZONE_CHANGED` (Apéndice D, D.118: solo hay pruebas JVM del historial y de los bordes del día).

Mientras el teléfono no cambie de zona, nada de esto mueve una sola cifra.

## 4.4 Fuente por fuente: teléfono

Cada ficha responde: **qué mide · cómo lo hace exactamente · qué tan confiable es · qué pasa si falta**.

### 4.4.1 Pantalla (`sensores/Pantalla.kt`)

**Qué mide.** Cuánto tiempo estuvo en primer plano cada app, cuántas veces desbloqueaste el teléfono, cuándo fue el primer y último uso y cómo se reparte el uso por hora.

**Cómo lo hace.** Android guarda una bitácora de eventos de uso («esta app pasó a primer plano», «esta app salió», «se apagó la pantalla», «se quitó el bloqueo»). La app recorre los eventos del día y arma «tramos»:

1. Cuando una app pasa a primer plano (`ACTIVITY_RESUMED`), cierra el tramo anterior y abre uno nuevo. **No cuenta** el lanzador (la pantalla de inicio) ni la propia Vestigium Mei.
2. El tramo termina cuando esa app sale (`ACTIVITY_PAUSED`), cuando la pantalla se apaga (`SCREEN_NON_INTERACTIVE`) o al final de la ventana.
3. Los **desbloqueos** son los eventos `KEYGUARD_HIDDEN` (se quitó el bloqueo de pantalla).
4. `total_min` = suma de todos los tramos, entera, **sin descartar nada**. La lista `por_app_min` guarda solo apps con **≥ 1 minuto** y como máximo **40** apps.
5. Cada tramo se reparte entre las horas que cruza (`por_hora_min`, 24 valores).

**Ejemplo (inventado).**

| Evento | Hora |
|---|---|
| App A a primer plano | 22:10 |
| App B a primer plano (A sale) | 22:40 |
| Pantalla se apaga | 23:10 |

: Tabla 4.3. Pantalla (sensores/Pantalla.kt)

Resultado: A = 30 min, B = 30 min, total 60 min. Por hora: 22:00–23:00 → 50 min (30 de A + 20 de B), 23:00–24:00 → 10 min (de B).

**Qué es y qué no es.** Es **tiempo de apps en primer plano**, no «pantalla encendida». Un video que se ve sin tocar la pantalla cuenta mientras la app esté al frente; un teléfono con pantalla encendida en la pantalla de inicio **no cuenta** (porque el lanzador se excluye). Por eso **no coincidirá exactamente** con «Bienestar digital» u otras apps, que pueden definir el tiempo de otra manera. **[Diseño propio]** la definición.

**Relación con el autorreporte.** Una revisión sistemática de 106 tamaños de efecto encontró que lo que la gente *dice* que usa los medios digitales **solo se correlaciona moderadamente** con lo que registran los dispositivos, y que los autorreportes rara vez reflejan con exactitud el uso real (Parry et al., 2021). Esa es la razón principal para medirla de forma pasiva. **[Validado]**.

**Día y cobertura.** Hasta el esquema 12, ventana de **calendario** (H-3-1, H-3-2). Desde el esquema 13, ventana del **día lógico**: de las 05:00 de ese día a las 05:00 del siguiente, y en `por_hora_min` las horas 0 a 4 son de la madrugada siguiente. La sincronización recalcula el día lógico de hoy y el de ayer (2 días). Desde 10f (D.109), los bordes de la ventana y el reparto por hora usan la zona que tenía el teléfono en cada instante (`Fechas.zonaEn`, sección 4.3): en un día de 25 horas, la hora que se repite suma sus dos vueltas en el mismo casillero de `por_hora_min`, y en uno de 23, la hora que se salta queda en 0. `primer_uso_h` y `ultimo_uso_h` se leen con el desfase guardado en cada marca.

**Cuando falta.** Sin permiso de «acceso a uso», la función devuelve nulo y esa fecha no se guarda.

#### Variables derivadas del sueño y el teléfono (`cama_min`, `dormir_tras_tel_min`, `tel_tras_despertar_min`)

Solo si el reloj registró el sueño de esa noche (`sueno_inicio`, `sueno_fin`):

- `cama_min` = minutos de pantalla en la **última hora antes de dormirte**.
- `dormir_tras_tel_min` = minutos entre que **soltaste el teléfono** (fin del último uso en las 3 horas antes de dormir) y te **dormiste**.
- `tel_tras_despertar_min` = minutos entre que **despertaste** y tomaste el teléfono (primer uso en las 4 horas siguientes).

*Razonamiento.* El uso de pantalla antes de dormir es una hipótesis frecuente en la investigación del sueño (luz, activación mental). Aquí se calcula el dato para *cruzarlo* con tu sueño, no para afirmar causalidad. **[Aproximación]**: la hora de dormir del reloj es una estimación (sección 4.5.1), así que estas variables heredan su error.

### 4.4.2 Ubicación (`sensores/Ubicacion.kt`)

**Qué guarda.** Puntos con hora, latitud, longitud, precisión (en metros) y la fuente («app», «geocerca», «foto», «foto_takeout», «maps», «takeout»…), en la tabla `ubicaciones`.

**Cómo lo obtiene.**
- **Muestra de fondo:** en cada pulso (cada ~15 min), si tienes el permiso de ubicación *en segundo plano* y el muestreo está activo, pide una lectura de **potencia equilibrada** (`PRIORITY_BALANCED_POWER_ACCURACY`: en general usa wifi y antenas antes que encender el GPS; gasta poca batería, pero es menos precisa). Espera hasta 10 s; si no responde, usa la última ubicación conocida y cancela la petición (antes seguía buscando señal). Desde el esquema 19, un punto que ya está guardado (misma hora, latitud y longitud) no se vuelve a guardar (`Db.ponerMuestra`): la «última ubicación conocida» suele ser la misma varios pulsos seguidos, y antes quedaba repetida una vez por pulso. Lo mismo vale para el punto que trae cada aviso de geocerca.
- **Geocercas:** para cada lugar guardado (hasta **95**; Android permite 100 por app) registra un círculo con radio `max(radio del lugar, 80 m)`. Android avisa al **entrar** y al **salir**. Eso crea o cierra «visitas» sin gastar mucha batería. (El código también fija una «espera» de 2 minutos, pero esa espera solo aplica a un tipo de aviso, «permanencia», que la app no pide; por eso no tiene efecto.) Las geocercas se registran una vez al día, al encender el teléfono, al cambiar los lugares y cuando la app deduce una salida (4.4.3). Desde el esquema 19 ya **no** se registran cada vez que arranca el proceso de la app (lo arrancan también cada pulso y cada widget), salvo que los lugares hayan cambiado o haya pasado un día: al quitarlas y ponerlas de nuevo, una salida que pasara justo en ese momento se perdía. Al registrarlas, Android avisa también de los lugares donde **ya no** estás (`INITIAL_TRIGGER_EXIT`), lo que cierra una visita que quedó abierta.

**Precisión.** El campo `precision` es la estimación que da Android del error. Según la documentación de Android para `Location.getAccuracy` (Android Developers, s. f.), es el **radio horizontal con 68 % de confianza**: hay 68 % de probabilidad de que la posición verdadera esté dentro de un círculo de ese radio alrededor del punto. Con potencia equilibrada es normal ver errores de decenas o cientos de metros; por eso la detección de lugares ignora puntos con precisión peor a **120 m** (4.4.3). Un punto **sin** precisión (los importados de Google Maps, 4.6.7) cuenta desde la entrega 10d como de **100 m** (`Db.PRECISION_SIN_DATO`), no de 0 m. Así lo leen la detección de lugares (`Lugares.estancia`), `Db.recorrerUbicaciones` (el mapa y el Mundo) y la ruta del mapa. Antes, `COALESCE(precision, 0)` lo trataba como un punto perfecto. Los 100 m son una cifra prudente propia, sin estándar detrás. **[Diseño propio]**. Con los umbrales actuales (120 m para lugares, 150 m para la ruta, 500 m y 1 000 m para el mapa y el Mundo), un punto así **sigue entrando** en todos: el cambio corrige lo que el valor significa y deja de presentarlo como exacto, pero no cambia por sí solo ningún resultado.

**Nota.** Hasta el esquema 18, el código **insertaba siempre** un punto por pulso, aunque fuera el mismo de antes (**H-3-8**, inciso a). Además de hacer crecer la tabla, el mismo punto viejo repetido varias horas pesaba de más en todo lo que cuenta puntos. Desde el esquema 19 no se repite, y una corrección única quitó los repetidos ya guardados de las fuentes del propio teléfono («app», «geocerca», «momento»; se queda el primero de cada uno). No toca los puntos importados ni los de fotos.

#### Distancia recorrida (`distancia_km`)

`Movilidad.kmPorDia` (`ciencia/Estadistica.kt`) recorre **todos** los puntos en orden (de cualquier fuente) y, para cada par consecutivo, suma la distancia en línea recta (fórmula del *haversine*, radio terrestre 6 371 km) **si**:
1. el tiempo entre los puntos está entre **1 segundo y 2 horas**,
2. la velocidad implícita es menor a **70 m/s** (252 km/h: descarta saltos imposibles), y
3. la distancia es mayor a **30 m** (descarta el «temblor» del GPS quieto).

La distancia se asigna al **día lógico** del segundo punto (desde 10f, con la zona que tenía el teléfono en ese instante, sección 4.3; también la hora `ts` de `vm_ubicaciones.csv`). Desde la entrega 10c (P-15, D.63), un día con puntos de ubicación pero sin ningún tramo que cuente vale **0 km**, no «sin dato».

*Cómo interpretarlo.* Es una **cota inferior**: la línea recta entre dos puntos siempre es ≤ el camino real, y los tramos con huecos de más de 2 horas se pierden. Además, mezcla puntos de varias fuentes con precisiones distintas (fotos, importaciones de Maps), y cuánto se queda corta depende de cuántos puntos haya ese día: un día con pulsos retrasados o con huecos pierde más camino que uno bien muestreado. Úsala para comparar días entre sí con esa cautela, no como los kilómetros reales. **[Aproximación]** (**H-3-5**, resuelto por documentación: la propuesta era describirla como cota inferior, que es lo que hace este párrafo; la app no pondera ni filtra los puntos por fuente).

### 4.4.3 Lugares y visitas (`data/Lugares.kt`)

**Qué hace.** Cuando llevas un rato en un sitio que no tienes guardado, la app te pregunta si es un «lugar especial». Al guardarlo, reconstruye las visitas pasadas y luego las geocercas lo vigilan.

**Regla exacta de detección** (función `estancia`; parámetros de la configuración: radio **75 m** y **20 min** por omisión, con topes de 30–300 m y 10–120 min):

1. Toma los puntos de ubicación de la **ventana** (tiempo configurado + 45 min), **sin fotos**, con **precisión ≤ 120 m**.
2. Hacen falta **≥ 2 puntos** y el último debe tener **≤ 25 min** de antigüedad.
3. Desde el último punto va hacia atrás **mientras cada punto esté a ≤ 75 m del último** (no del centro del grupo).
4. Si ese tramo tiene ≥ 2 puntos y dura **≥ 20 min** → candidato. El centro es el promedio de lat/lon del tramo.
5. Se descarta si ya está cerca de un lugar guardado (distancia ≤ radio del lugar + radio/2) o de uno ignorado.

**Reglas para no molestar** (`revisar`): solo entre **08:00 y 22:00**; **máximo 2 preguntas al día**; un mismo sitio (a ≤ 150 m de un candidato anterior) se pregunta **máximo 3 veces**, y después de cada pregunta (la respondas con «ahora no» o no) espera **3 días**.

**Un detalle importante (con ejemplo).** Como los puntos llegan *aproximadamente cada 15 minutos*, con solo 2 puntos (15 min de diferencia) no se llega a 20 min. En la práctica, se necesitan **3 lecturas dentro del radio**, es decir, **~30 minutos** de permanencia. La Figura 4.2 lo muestra con datos inventados: llegas en el minuto 30 del recorrido y la app pregunta en el minuto 60.

![Figura 4.2. Detección de un lugar nuevo con datos sintéticos: un punto cada 15 minutos y un radio de 75 m; la app pregunta en el minuto 60, 30 minutos después de llegar. Script: `figuras/fig05_deteccion_lugar.py`.](figuras/fig05_deteccion_lugar.png){width=100%}

**Reconstrucción de visitas** (`reconstruir`, 30 días hacia atrás; el cálculo está en `reconstruirEn`): recorre los puntos; cada racha de puntos dentro del radio del lugar (**mínimo 60 m**) es una visita; huecos de hasta **2 horas** dentro del radio no la rompen; **solo cuentan visitas de ≥ 8 minutos** (10 hasta el esquema 18) y no se duplican con las existentes. Su salida queda marcada como `reconstruida`.

**Cuándo termina una visita** (esquema 19, decisiones del autor del 5 nov 2026). Android no siempre avisa la salida de una geocerca: con el teléfono en reposo o con mala señal el aviso puede llegar tarde o no llegar. Hasta el esquema 18, una visita sin aviso de salida quedaba **abierta** hasta la siguiente salida de ese mismo lugar, aunque mientras tanto hubieras ido a otros lugares; y como Android seguía creyendo que estabas dentro, tampoco avisaba cuando volvías. Desde el esquema 19 la salida se deduce con tres reglas:

1. **Llegar a un lugar lejano** (`Repo.entrada`) cierra la visita abierta de otro lugar si sus círculos vigilados no se tocan (`Lugares.lejos`: distancia entre centros > suma de los radios, cada uno de al menos 80 m). Si un lugar está dentro de otro (un gimnasio dentro del campus), estar en uno no te saca del otro.
2. **Tus ubicaciones** (`Lugares.salidaPorUbicaciones`): la salida es el primero de **dos puntos seguidos** fuera del círculo vigilado aun restando su imprecisión (distancia − precisión > radio). Solo cuentan puntos con precisión de **150 m** o mejor; a los que no la traen se les supone 100 m. Un punto suelto fuera no basta, porque puede ser un brinco del GPS. Esta regla se aplica en cada pulso a las visitas abiertas (`cerrarAbiertas`) y también al llegar a otro lugar, si da una hora anterior.
3. **Un aviso de salida tardío** (`Lugares.salidaAvisada`): si tus ubicaciones dicen que saliste al menos **15 minutos** antes del aviso, vale esa hora.

Cuando la app deduce una salida, vuelve a registrar las geocercas para que Android avise la próxima entrada. Si mientras tanto ya habías vuelto, esa vuelta sale de tus ubicaciones con `reconstruirEn`.

**Mínimo de 8 minutos.** Una visita cerrada de menos de **8 minutos** (`VISITA_MINIMA_MS`) se quita: pasar cerca en coche o un brinco del GPS. Si esa visita falsa había cerrado al empezar la de otro lugar donde en realidad seguías (regla 1), esa visita se vuelve a abrir. El autor eligió 8 minutos (5 nov 2026), y el mismo mínimo vale para las visitas reconstruidas.

**Cómo se supo la salida** (`Visita.cierre`, columna `cierre` de `vm_visitas.csv`): `geocerca` (avisó Android), `otro_lugar` (regla 1), `ubicaciones` (reglas 2 y 3), `reconstruida`, `manual` (la corregiste tú) o vacío (abierta, o anterior al esquema 19 y sin corregir). Sirve para separar en R las salidas medidas de las deducidas.

**Corrección de lo ya guardado** (una sola vez, `limpiarVisitasPasadas`). El autor decidió corregir también la historia. Primero se quitan las visitas de menos de 8 minutos. Luego, a cada visita se le aplican las reglas 1 y 2, y solo se cambia si la salida deducida es anterior: con la regla 2, al menos 15 minutos antes de la que tenía. Las que quedan de menos de 8 minutos también se quitan. Por último, el tramo liberado (entre la salida nueva y la vieja) se reconstruye con tus ubicaciones, por si volviste al mismo lugar.

*Ejemplo (datos inventados).* Llegas a casa el lunes a las 19:00. El martes sales a las 08:30, pero Android no avisa. A las 09:10 la geocerca del trabajo avisa la entrada; trabajas y regresas a casa a las 20:00, sin aviso, porque Android te creía en casa. El miércoles a las 08:00, al salir, sí avisa. Hasta el esquema 18 había una sola visita a casa de 37 h, encimada con la del trabajo. Con estas reglas pasa esto:
- Si los puntos de las 08:40 y las 08:55 ya te ponen fuera, la visita termina a las 08:40 (`ubicaciones`). Si no, termina a las 09:10 (`otro_lugar`).
- De 20:00 a 08:00 se reconstruye otra visita a partir de tus puntos (de 20:05 a 07:50, por ejemplo).
- En total suman unas 25 horas en casa en lugar de 37.

**[Diseño propio]**. Las reglas usan el mismo radio que la geocerca y la misma idea de estancia que la detección de lugares; los 15 minutos y los dos puntos seguidos son umbrales propios, sin validación externa.

*Cuándo falla.* (a) Si en casa los puntos son muy imprecisos, dos puntos seguidos «fuera» pueden cerrar la visita aunque sigas ahí. Se pierde el tiempo hasta el siguiente aviso o hasta que `reconstruirEn` encuentre puntos dentro. (b) Sin puntos (muestreo apagado, sin permiso en segundo plano), solo queda la regla 1. (c) La salida deducida por puntos cae, como mucho, un intervalo de muestreo (~15 min) después de la real: es una cota tardía.

**Corregir lugares y visitas** (desde la entrega N7 de la revisión de navegación, H-NAV-4). Un lugar guardado se puede renombrar, cambiar de radio o quitar. Agrandar el radio vuelve a llamar a `reconstruir` (30 días), que solo agrega visitas que no se enciman con las existentes; achicarlo no quita las que ya hay. Al quitar un lugar se pregunta qué pasa con sus visitas: pasarlas a otro lugar o quitarlas (`Repo.borrarLugar(id, pasarA)`, `Lugares.reasignar`); ya no quedan visitas que apuntan a un lugar que no existe. Se puede, además, ignorar el sitio para siempre. En el Día, cada visita aparece en «Tu día» y se corrige con «fue en…» (otro de tus lugares) o «no estuve aquí». *Límite:* «no estuve aquí» no se recuerda; si después agrandas el radio de ese lugar, `reconstruir` podría volver a crear esa visita. **[Diseño propio]**.

**Tipos de lugar y tiempo por tipo.** Cada lugar tiene un tipo (casa, trabajo, bar…). `minutosPorTipo` reparte cada visita entre los **días lógicos** que cruza (desde el esquema 19, cortando en `Fechas.finDiaLogico`, con la zona horaria: antes cortaba a medianoche + 5 h de reloj, y la noche en que se atrasa el reloj eso caía a las 04:00), descarta visitas de más de **40 días** (error probable) y limita cada día a **1 440 min** (24 h). La app también estima la **valencia media por tipo de lugar** (sin momentos privados).

**Razonamiento.** Detectar *estancias* (*stay points*) con un radio y un tiempo mínimo es la técnica estándar de la minería de trayectorias GPS (Zheng, 2015). La app usa una versión simple: sin agrupamiento por densidad y con el último punto como referencia. **[Aproximación]**.

**Cuándo falla.** (a) Interior de edificios: el GPS «salta» y la precisión es mala (se filtra a ≤ 120 m, pero un salto de 100 m dentro de un edificio grande sigue pasando). (b) Lugares de menos de 30 minutos (una tienda rápida) **no se detectan**. (c) Dos lugares a menos de ~75 m se funden. (d) El nombre sugerido viene del servicio de direcciones del teléfono (*geocoder*), si responde.

### 4.4.4 Movimiento (`sensores/Actividad.kt`)

**Qué mide.** Cuántos minutos del día pasaste «a pie», «corriendo», «en bici», «en vehículo» o «quieto».

**Cómo.** La app se suscribe al **reconocimiento de actividad** de Android (Google Play Services): el sistema avisa cada vez que *empieza* o *termina* una actividad. La app guarda el tipo y la hora de inicio y, al llegar el siguiente aviso, **suma los minutos** al día. Reglas:

- Un tramo se parte en el corte de las **05:00** (día lógico).
- **Se ignora cualquier tramo de más de 20 horas** (probable aviso perdido).
- Los avisos se ordenan por su marca de tiempo del sistema.
- **Al encender el teléfono, el tramo abierto se descarta** (desde el 6 nov 2026, D.159; `ArranqueReceiver`). Antes seguía abierto mientras el teléfono estuvo apagado, y al llegar el siguiente aviso se sumaba todo ese rato (por ejemplo, 8 horas «en vehículo» que no pasaron), siempre que fueran menos de 20 horas. Como no se sabe a qué hora se apagó, ese último tramo se pierde. **[Diseño propio]**.

**Razonamiento y confiabilidad.** Es un clasificador del propio Android (no controlado por la app) que combina acelerómetro y otros sensores. Funciona bien para distinguir «quieto / a pie / vehículo» y peor para matices. No hay una validación publicada en esta app. **[Aproximación]**. «Quieto» es especialmente ambiguo (teléfono sobre la mesa mientras tú caminas por otro lado). Las variables `a_pie_min` y `vehiculo_min` entran al panel.

### 4.4.5 «Percepción»: wifi, audio, música, carga, luz, presión (`sensores/Percepcion.kt`)

Cada pulso toma una **muestra** del estado del teléfono. Las variables resultantes no son *mediciones continuas*, sino **estimaciones por muestreo**.

**La regla de los minutos («mantener el último estado»).** Cada muestra atribuye al día **el tiempo transcurrido desde la muestra anterior**, como si el estado actual hubiera durado todo ese tiempo:

- Si no había muestra previa → **15 min**.
- Si pasaron **más de 2 horas** → solo **15 min** («para no inventar»).
- En otro caso → los minutos reales, **con tope de 60**.

**Ejemplo (inventado).** Muestras a las 10:00, 10:16, 10:31 y 12:45.

| Muestra | Tiempo desde la anterior | Minutos que se cuentan |
|---|---|---|
| 10:00 | más de 2 h (la anterior fue de la noche) | 15 |
| 10:16 | 16 min | 16 |
| 10:31 | 15 min | 15 |
| 12:45 | 134 min (> 2 h) | **15** (no 134) |

: Tabla 4.4. «Percepción»: wifi, audio, música, carga, luz, presión (sensores/Percepcion.kt)

Total: 61 min contados. Entre 10:31 y 12:45 pasaron 134 min, pero solo 15 se contaron. La app prefiere **subestimar** a inventar. Consecuencia clave: `wifi_min + sin_wifi_min` (más `wifi_sin_dato_min`, desde 10f) **no suma 1 440**; son **«minutos observados»**, no minutos del día. Un día con el teléfono apagado seis horas simplemente tiene menos minutos. Esto es un **sesgo de subestimación conocido** y deliberado. **[Diseño propio]** (similar a la lógica de «último valor conocido» de la minería de series de tiempo; no tiene un referente estándar específico).

**Qué se mide en cada muestra** (todas con *try/catch*: si algo falla, esa pieza queda vacía):

| Variable | Qué hace exactamente |
|---|---|
| **Wifi** | Desde 10f (D.112; `Percepcion.wifi`), cada muestra cae en uno de tres casos. **Con wifi**: alguna red activa es wifi según `ConnectivityManager` (`TRANSPORT_WIFI`), o la conexión tiene red o dirección IP; se suma a esa red por su nombre (SSID) o, si Android lo oculta, a «wifi (sin nombre)». **Sin wifi** (`sin_wifi_min`): el wifi está apagado o el sistema dice que ninguna red activa es wifi. **Sin dato** (`wifi_sin_dato_min`, en `vm_conexiones.csv`): no se pudo saber (el sistema no respondió o la lectura falló). **[Diseño propio]** los tres casos. *(H-3-7: hasta la v91, «no hay información» y «no hay red» iban juntos a `sin_wifi_min`, que podía inflarse; desde 10f puede bajar, así que los días de antes y de después no son del todo comparables. Que `ConnectivityManager` responda así cuando Android esconde el nombre no se probó en un teléfono.)* |
| **Audio** | Dispositivos de salida activos: audífonos (cable, USB o Bluetooth clase «headphones»), coche (Bluetooth clase «car audio/handsfree»), bocina, USB, pantalla HDMI, «bluetooth» genérico. Se guarda la categoría y el **nombre** del dispositivo. |
| **Música** | `isMusicActive` del sistema de audio: ¿suena algo ahora? |
| **Carga** | ¿Está enchufado? Más **carga nocturna**: si se enchufó entre las **20:00 y las 04:00** y estuvo enchufado **> 3 h y < 16 h**, se guarda inicio y fin como marca aproximada de cuándo dormiste. |
| **Batería** | Suma de niveles y mínimo del día. |
| **Luz** | Una lectura del sensor de luz (hasta 1.5 s de espera), **si** la opción está activa. Guarda suma por hora, máximo, y **`luz_alta_min` = minutos con ≥ 1 000 lux**. |
| **Presión** | Una lectura del barómetro **si la opción está activa y el teléfono tiene sensor**; se **descartan lecturas fuera de 300–1 100 hPa** (`Percepcion.RANGO_HPA`, desde la entrega 10d; antes, 850–1 090 hPa). Guarda suma, cantidad, mínimo, máximo y el **cambio de presión en ~3 horas** (ver abajo). |

: Tabla 4.5. «Percepción»: wifi, audio, música, carga, luz, presión (sensores/Percepcion.kt)

**Sobre la luz.** Un sensor de luz de teléfono mide dentro del bolsillo o sobre la mesa, no donde están tus ojos. Por eso `luz_alta_min` **subestima** el tiempo al aire libre. La app lo compensa en `aire_libre_min` (capítulo 2) tomando el **máximo** entre la luz alta y el tiempo a pie/corriendo/en bici, un **proxy** razonable pero no medición. Los 1 000 lux son un umbral práctico (por referencia, el exterior de día suele ser de miles a decenas de miles de lux y una oficina bien iluminada ronda cientos), **regla propia, sin estándar detrás**. **[Aproximación]**.

**Sobre la presión y la altitud (hallazgo H-3-3, resuelto en 10d).** Hasta la entrega 10d, el filtro era 850–1 090 hPa, para descartar «lecturas absurdas». Pero 850 hPa corresponde a unos **1 450 m** sobre el nivel del mar en una atmósfera estándar. En una ciudad más alta, la presión *normal* es menor que 850 hPa, y **todas** las lecturas se descartaban, dejando sin datos todas las variables de presión. La gravedad era **alta si se vive por encima de ~1 450 m y ninguna más abajo**; no sé a qué altitud vive la persona usuaria, así que quedó como condicional.

**Corregido en la entrega 10d (P-33, D.85).** El filtro pasó a **300–1 100 hPa** (`Percepcion.RANGO_HPA`, que aplica `Percepcion.presion`). Es el rango de medida de dos sensores de presión comunes en teléfonos, comprobado en sus hojas de datos en la sesión de la entrega 10d. El BMP280 de Bosch mide de 300 a 1 100 hPa (revisión 1.26, «*handsets such as mobile phones*»; Bosch Sensortec, 2021). El LPS22HB de ST mide de 260 a 1 260 hPa (DocID027083, revisión 5; STMicroelectronics, 2016). 300 hPa equivale, en atmósfera estándar, a unos 9 000 m, así que ya no se pierde ninguna ciudad habitada. Lo que se descarta sigue siendo lo imposible: un cero o un valor basura del sensor. Qué sensor trae tu teléfono **no lo comprobé**: si fuera otro modelo con un rango más estrecho, el filtro dejaría pasar lecturas que ese sensor no garantiza. **[Aproximación]**. Efecto: si vives o viajas por encima de ~1 450 m, aparecen lecturas de presión que antes se descartaban todas; por debajo, nada cambia.

**Cambio de presión en 3 horas.** Se guardan las lecturas de las últimas 4 horas; la comparación usa la lectura más cercana a **hace 3 horas** entre **2.5 y 3.5 h**. Del día se queda el cambio con **mayor valor absoluto, con signo** («las bajadas bruscas son las que se notan»). Es una hipótesis popular (los cambios de presión y el dolor o el ánimo), pero la evidencia es mixta; aquí se mide para *poder cruzarlo*, no para afirmarlo.

**Calibración del barómetro** (`data/Barometro.kt`). La presión de un sensor de teléfono tiene un **sesgo constante** (cada sensor «lee» unos hPa de más o de menos). La app (`Barometro.ajuste`) toma los **60 días más recientes con ≥ 8 lecturas** del sensor; de ellos, los que tienen presión de Open-Meteo (clima) y una diferencia < 150 hPa. Calcula la **mediana de las diferencias** (Open-Meteo menos la media del sensor) y se la suma a la media del sensor. Necesita **≥ 3 días**. El resultado se parece a la presión meteorológica del lugar, y los *cambios* del día conservan la precisión del sensor.

*Razonamiento.* Si el sesgo es constante, la **diferencia** entre dos lecturas (el cambio de presión) no se ve afectada; solo se corrige el nivel absoluto para que se pueda comparar con datos de clima. **[Diseño propio]**, lógica razonable.

**Qué NO hace.** No «escucha» (no hay micrófono aquí), no guarda contraseñas de wifi. Sí guarda **nombres de redes wifi y de dispositivos Bluetooth** (revisar en el capítulo de privacidad).

### 4.4.6 Ruido del ambiente (`sensores/Ambiente.kt`)

**Qué hace.** Cuando registras algo (un momento, un aviso), puede escuchar el micrófono **1.5 segundos**, calcular un nivel y **descartar el audio**.

**Cómo.** Graba a 16 000 muestras por segundo, **ignora el primer cuarto de segundo** (el micrófono se estabiliza), calcula la **raíz del promedio de los cuadrados (RMS)** de las muestras restantes y convierte:

> `dB = 20 · log10( RMS / 32768 ) + 90`

(32768 es el valor máximo de una muestra de 16 bits; el «+90» desplaza la escala para que quede positiva).

**Ejemplo (inventado).** Si el RMS es 328 (≈ 1 % del máximo): 20·log10(0.01) = −40 → −40 + 90 = **50**. Con RMS 33 (0.1 %): −60 + 90 = **30**.

**Qué es y qué NO es.** Es un **nivel relativo**, no decibeles de presión sonora (dB SPL). Los micrófonos de teléfono no están calibrados, tienen control automático de ganancia y filtran frecuencias. Las pruebas con apps de sonómetro para teléfono muestran que algunas se acercan a un sonómetro profesional y otras no, y que **la precisión varía mucho entre dispositivos y apps** (Kardous y Shaw, 2014). El propio código lo reconoce: es «útil para comparar tus lugares entre sí», no para medir ruido real. **[Aproximación]**. La variable `ruido_db` del panel es la media de las lecturas del día.

### 4.4.7 Notificaciones y música (`sensores/Oyente.kt`)

**Notificaciones.** Un servicio del sistema (`NotificationListenerService`) recibe un aviso cada vez que llega una notificación. La app **solo anota** de qué app y a qué hora (suma al día lógico por app y por hora). **No lee ni guarda el texto.**

Qué **no** cuenta: notificaciones de la propia app, notificaciones «en curso» (`isOngoing`, como el reproductor de música fijo), resúmenes de grupo, y **actualizaciones de la misma notificación** (misma clave) si ocurren en **menos de 60 segundos** o si está marcada «alertar solo una vez».

**Música.** El mismo servicio puede ver las **sesiones de reproducción** del teléfono. Cuando pasa a *reproduciendo*, anota título, artista, álbum, app y duración. **No cuenta dos veces** la misma canción de la misma app si vuelve a sonar dentro de `max(duración, 3 min) + 1 min`. En el análisis, una escucha cuenta como **música** (`esMusica`) salvo que venga de una app de video, un navegador, una red social o de mensajería (lista `NO_MUSICA` en `data/Modelo.kt`).

*Razonamiento.* Contar notificaciones es una medida de **interrupciones externas**. Las notificaciones y el tiempo de pantalla son indicadores de la relación con el teléfono que se usan en investigación del uso del teléfono inteligente (Mohr et al., 2017 hablan del «sensado personal» en general). **[Aproximación]**.

**Cuando falla.** Si el servicio no está activo (permiso retirado, o la app «dormida» por ahorro de batería), esas notificaciones **nunca se cuentan** y no hay forma de recuperarlas después.

### 4.4.8 Fotos (`sensores/Galeria.kt` y `FotosLugar.kt`)

**Conteo diario.** Lee solo los **metadatos** de las fotos en la galería: cuántas hubo ese día lógico, de qué tipo (cámara: carpeta que empieza con `DCIM/`; captura de pantalla; otras) y cuántos videos. **Las imágenes nunca se copian ni salen del teléfono.** Con el acceso parcial de Android 14 («solo algunas fotos») la app **no cuenta** (lo considera poco confiable, porque solo vería las fotos que elegiste).

**Fotos con lugar (para el mapa).** La app puede leer, de cada foto, el lugar y la hora que la cámara guarda dentro del archivo (**EXIF**), en el teléfono o en un Takeout de Google Fotos. La hora EXIF es local y no dice de qué zona: desde 10f (D.109) se lee con la zona que regía ese día (`Fechas.millisDe`, en `FotosLugar.fechaExif`); antes se leía con la de hoy, y las fotos de un viaje se corrían de hora. Guarda una fila por foto: hora, lat, lon, aparato u origen. Reglas:
- La **clave** de cada foto es *(segundo, lat redondeada a 4 decimales, lon redondeada)*, así la misma foto en el teléfono y en Takeout no se duplica (el redondeo equivale a ~10 m).
- **Solo cuentan fotos de tus aparatos** (si eliges aparatos) y, opcionalmente, no las «compartidas».
- Se descartan fechas imposibles (antes de 2004 o más de 1 día en el futuro) y coordenadas (0, 0).
- **Fotos «raras»:** si una foto te ubica en un país distinto al tuyo y ese día el teléfono estaba en otro lado (**contradicha**) o es un país con muy pocos días (**aislada**), la app te pregunta antes de contarla en el mapa.

*Razonamiento.* Las fotos con GPS dan **posiciones del pasado** (años), muy útiles para reconstruir historia. Pero son **puntos aislados** (cuando tomaste una foto), con precisión y fiabilidad de fecha variables, por eso se marcan con su propia fuente y se pueden activar o apagar. **[Diseño propio]**.

### 4.4.9 Agenda y llamadas/SMS

**Agenda** (`Agenda.kt`): lee los eventos del calendario del teléfono: cuántos, **minutos ocupados** (uniendo eventos que se enciman para no contar dos veces), hora del primero y del último, y títulos (hasta 30). Ignora eventos cancelados y calendarios ocultos; los eventos de «todo el día» se cuentan aparte (se guardan en UTC). Ventana de **calendario**. La sincronización cubre de mañana a hace 2 días.

**Llamadas y SMS** (`Comunicacion.kt`): solo **cuenta**: llamadas entrantes, salientes, perdidas, rechazadas, minutos hablados (segundos / 60, solo entrantes y salientes), SMS enviados y recibidos, y **personas distintas**. Para contar personas, cada número se reduce a sus **últimos 8 dígitos** y se descarta si tiene menos de 6 dígitos (así los SMS de bancos y servicios, que llegan de números cortos, no cuentan como «personas»). **Los números se usan en memoria y se descartan.** Ventana de **día lógico**.

*Razonamiento.* La comunicación (frecuencia y duración) es un indicador clásico de actividad social en estudios de «sensado móvil». **[Aproximación]**: un número de teléfono no es una persona (compartidos, varios números por persona), y WhatsApp/Telegram no aparecen aquí (ver WhatsApp, sección 4.6.4).

### 4.4.10 Clima, aire y luna (`sensores/Entorno.kt`, `data/Calculos.kt`)

**De dónde viene.** De **Open-Meteo**, un servicio gratuito sin cuenta. Solo se envían **coordenadas redondeadas a 0.01°** (~1 km) y fechas; **nunca tu ruta**. Desde 10f (D.116; H-8-17), Ajustes › Privacidad › «Qué sale del teléfono» lo dice así, junto con los demás servicios externos (`Red.SERVICIOS`).

**Dónde estabas.** `lugarDe` toma la **mediana** de latitud y la **mediana** de longitud de los puntos de ese día de calendario (si no hay, de tus momentos no privados con coordenadas; si no, la última ubicación conocida). Se piden varios días seguidos de golpe mientras estén a < 0.1° (≈ 10 km), a < 60 días de distancia y usen la misma fuente (archivo o pronóstico).

**Qué se pide.** Diarios: código de clima, temperatura máxima y mínima, sensación térmica máxima, precipitación, horas de lluvia, amanecer, atardecer, duración de luz y de sol, viento máximo (y **índice UV máximo**, que solo da el pronóstico: el archivo no lo trae). Por hora, de las que se saca el **promedio diario**: temperatura, humedad, nubosidad y **presión de superficie**. De la API de calidad del aire: **PM2.5** y **PM10** (promedio diario, partículas de menos de 2.5 y 10 micrómetros) y el **índice de calidad del aire de EE. UU.** (máximo del día).

**Dos fuentes (hallazgo H-3-4).** Si el día tiene **más de 80 días** de antigüedad (`Entorno.DIAS_ARCHIVO`), la app pide el **archivo histórico** (reanálisis: el clima pasado reconstruido con modelos y observaciones; Open-Meteo indica que usa conjuntos como ERA5, un reanálisis global del centro europeo ECMWF, Hersbach et al., 2020, con resolución del orden de 10–25 km y hasta ~5 días de retraso). Si es **más reciente**, pide el **pronóstico** (modelos de pronóstico). Un día se marca **completo** (y deja de pedirse en cada sincronización) cuando tiene **al menos 2 días** de antigüedad.

*Lo que pasaba hasta la v91.* Un día completo ya no se volvía a pedir. Los días que la app sincronizó con entre 2 y 80 días **se quedaban para siempre con los valores del pronóstico**, y los antiguos, rellenados con el archivo, venían del reanálisis. La fuente de cada día dependía de cuándo sincronizó la app, y no quedaba anotada: la serie mezclaba dos fuentes con modelos y resolución distintos sin poder separarlas.

*Desde 10f (D.111).*
1. **Cada día guarda su fuente** (`EntornoDia.fuente`: «archivo» o «pronostico»). En lo guardado antes, `Entorno.fuente` la deduce de cuántos días habían pasado entre el día y su última petición (`EntornoDia.ts`), con la misma regla de 80 días. Sin `ts`, queda sin fuente.
2. **El pronóstico se cambia por el reanálisis.** En cada sincronización, los días del último año (hasta 365 días atrás, con algún registro) que vinieron del pronóstico y ya tienen más de 80 días se vuelven a pedir al archivo. Se piden primero los más recientes, hasta 8 peticiones en la sincronización diaria (20 en la mensual y en el botón del clima); cada petición cubre hasta 60 días seguidos en un mismo lugar, pero la primera vez el cambio puede tardar más de una sincronización. Los días de hace más de un año que vinieron del pronóstico se quedan así.
3. **Lo que el archivo no trae se conserva.** El archivo no da el índice UV: si el día ya lo tenía del pronóstico, `uv_max` se queda. También se conservan PM2.5, PM10 y el índice de calidad del aire si ese servicio no responde.
4. **Columnas:** `fuente` en `vm_entorno.csv` y `clima_reanalisis` (1 = archivo, 0 = pronóstico, vacío si no se sabe) en `vm_panel.csv`.

*Ejemplo (inventado).* El 1 de marzo la app pidió el clima del 27 de febrero (2 días: pronóstico) y lo marcó completo. Hasta la v91 se quedaba así. Desde 10f, en la primera sincronización desde el 19 de mayo (81 días después, más de 80), se pide de nuevo al archivo: `t_max` puede pasar, digamos, de 24.1 a 23.6 °C, `clima_reanalisis` pasa de 0 a 1 y `uv_max` sigue siendo el del pronóstico.

*Cuándo falla.* (a) Un día del reanálisis puede traer `uv_max` del pronóstico, y los días que siempre vinieron del archivo no tienen `uv_max`: el índice UV **sigue siendo solo del pronóstico** y no se puede usar en una serie larga sin cuidado. (b) Los días de los últimos 80 siguen siendo de pronóstico: `clima_reanalisis` sirve para separarlos o para controlar la fuente en un modelo. (c) Que el archivo ya tenga los días de más de 80 días es lo que la app supone desde antes; no se consultó en la sesión, y el cambio no se probó con la red (Apéndice D, D.111). **[Aproximación]**: las diferencias típicas entre las dos fuentes deberían ser pequeñas, pero no las medí.

**Datos de clima = «clima de la zona», no de tu cuerpo.** No miden temperatura *donde estuviste* (adentro, afuera), sino una estimación del lugar en una rejilla. Una persona que pasa el día en interiores climatizados tiene una exposición distinta a la reportada.

**La luna.** No viene de internet; se **calcula** (`Luna.fase`): edad de la luna = (días desde una luna nueva de referencia, 6-ene-2000 18:14 UTC) **módulo 29.530588853** (el mes sinódico medio); fase = edad / 29.53; **iluminación = (1 − cos(2π·fase)) / 2**, evaluada al mediodía local (desde 10f, con la zona que regía ese día). Usa el **promedio** del mes lunar (la luna real adelanta o atrasa unas horas), así que es una aproximación. Es una variable **determinista**: no depende de ti, por eso sirve de **control negativo** (si algo «se asocia con la luna», sospecha de azar).

## 4.5 Fuente por fuente: reloj (Health Connect)

La app lee los datos de tu reloj a través de **Health Connect**, el sistema de salud de Android, donde otras apps (por ejemplo, la del reloj Samsung) escriben sus datos. Solo se piden **permisos de lectura**.

### 4.5.1 Sueño (`Salud.noche`, `Salud.sincronizar`)

**Qué mide.** Duración de la noche, tiempo en etapas (profundo, REM, ligero, despierto), hora de dormir y de despertar, y siestas detectadas por el reloj.

**Cómo lo calcula exactamente** (esta es una de las partes más cuidadas del código):

1. Lee las sesiones de sueño que **terminan** ese día de calendario, desde el mediodía del día anterior.
2. **Una sola fuente por noche:** elige la app con más puntos (prefiere la que trae **etapas**; después, Samsung Health; después, la de más duración total). Así no se suma la misma noche dos veces desde dos apps.
3. **Une sesiones separadas por menos de 1 hora** en un mismo «bloque» (la noche).
4. **La noche** es el bloque más largo que **empezó antes del mediodía**; los demás bloques son **siestas** (`siesta_reloj_min`).
5. **Lo que marcaste «no cuenta»** no entra en ninguno de los pasos anteriores (desde la entrega N7 de la revisión de navegación, H-NAV-3). En la corrección del sueño, cada sesión que llegó del reloj tiene «no cuenta»: sirve para la siesta del sofá o para una noche que no fue tuya. Se guarda la clave de la sesión (fuente y horas, `claveFuenteSueno`) en `SaludDia.sueno_descartadas`; cada sincronización la respeta, y un respaldo no la borra (`Fusion.salud` junta las de los dos lados). Con la noche corregida a mano, las siestas se vuelven a contar en cada sincronización; antes se quedaban con el último valor leído.
6. Dentro del bloque, si dos sesiones se **enciman más de 10 minutos**, se descarta la más corta.
7. **Minutos dormido:** si las etapas cubren **al menos 60 %** de la sesión, se suman las etapas de sueño (profundo, REM, ligero y «sin clasificar»); si no, se toma **la sesión completa menos lo despierto**. Se calcula en **segundos** y se redondea al final (para no perder minutos por truncar etapas cortas, que antes hacía perder hasta una hora).

**Ejemplo (inventado).** Una sesión de 23:40 a 07:10 dura **450 min**. Las etapas registradas suman 400 min (≥ 60 % de 450 = 270, así que se usan las etapas): profundo 70 + REM 95 + ligero 210 = **375 min dormidos**, más 25 min despierto dentro de las etapas. El panel guarda `sueno_horas = 375 / 60 = 6.25` h y `sueno_profundo = 70`, `sueno_rem = 95`. Si las etapas solo hubieran cubierto 200 min (< 270), se habría usado 450 − (despierto registrado).

**Día asignado.** El **día de calendario en que termina** la sesión (el día en que despiertas). Desde 10f (D.109), ese día, los bordes del día de calendario, el mediodía que separa la noche de las siestas y la ventana de 18:00 a 14:00 de la variabilidad se calculan con la zona que tenía el teléfono entonces (sección 4.3), y lo mismo el resto de Health Connect. Si hay una corrección **manual** del sueño (`sueno_manual`), la sincronización no la sobrescribe, y desde el esquema 14 tampoco un respaldo que no la tenía (`Fusion.salud`). Quitar la corrección («volver a lo del reloj») pide confirmación, avisa si el reloj no tiene esa noche (quedaría vacía) y se puede deshacer; la relectura del reloj espera a que pase el «deshacer» (H-NAV-5).

**Confiabilidad.** Los relojes y anillos de consumo **detectan bien el sueño** pero **detectan mal la vigilia**: en una comparación contra la polisomnografía (el estándar de laboratorio) de siete dispositivos, la sensibilidad para detectar sueño fue ≥ 0.93, mientras que la **especificidad para detectar vigilia fue de solo 0.18–0.54**; tres de ellos **sobrestimaron** significativamente el tiempo total de sueño (dos relojes Garmin, en ~44 y ~47 minutos por noche, y un sensor de cama, ~14 minutos) (Chinoy et al., 2021; dispositivos distintos al reloj de la app). **[Validado]** el patrón general; **no hay una validación del reloj específico de esta app**. Las **etapas** (profundo/REM) son las menos fiables de todas las salidas del reloj.

**Cuando falta.** Sin reloj puesto esa noche, no hay sesión: las variables quedan vacías (la app puede estimar «noches aproximadas» por uso del teléfono; eso es del capítulo 5).

### 4.5.2 Corazón, oxígeno y variabilidad

| Variable | Cómo se calcula |
|---|---|
| `fc_media`, mínimo, máximo | Agregación de Health Connect sobre el día de calendario (promedio de lecturas; si no hay agregación, calcula con las muestras sueltas). |
| `fc_reposo` | Promedio de los registros de «frecuencia en reposo» del día. |
| `fc_sueno` | Promedio de la frecuencia cardiaca **durante la noche** (el bloque de sueño). |
| `spo2` | Promedio de los registros de saturación de oxígeno del día. |
| `spo2_min` | Mínimo durante la noche. |
| `hrv` (RMSSD) | Promedio de los registros de variabilidad **durante la noche**; si no hay noche, de 18:00 del día anterior a 14:00. |

: Tabla 4.6. Corazón, oxígeno y variabilidad

**Qué es el RMSSD.** El corazón no late a intervalos exactamente iguales. El RMSSD es la **raíz cuadrada del promedio de los cuadrados de las diferencias entre intervalos de latidos consecutivos** (en milisegundos). Un valor mayor suele reflejar más **actividad parasimpática** («descanso y recuperación»); es la medida de variabilidad de **corto plazo** más usada en dispositivos de consumo (Task Force of the ESC and NASPE, 1996; Shaffer y Ginsberg, 2017). **[Validado]** el significado; **[Aproximación]** el uso: el RMSSD depende de cuándo y cómo se mide (en reposo, de noche) y **varía mucho entre personas y días**, así que se interpreta sobre todo como cambio **respecto a tu propia línea base**, no contra normas.

**Un matiz.** Un «promedio de lecturas del día» pesa más los momentos con más lecturas (por ejemplo, durante el ejercicio, si el reloj mide más seguido). Es un promedio **de muestras**, no de **tiempo** (no lo pude comprobar en la documentación de Health Connect; lo asumo por el nombre de la función `BPM_AVG`).

**Confiabilidad.** Una revisión sistemática de dispositivos de consumo encontró que **los pasos y la frecuencia cardiaca se miden con razonable exactitud en laboratorio** (la frecuencia cardiaca con error mediano ≈ −1 %; los pasos con error medio ≈ −9 % y mediano ≈ −2 %), pero que **ninguna marca midió bien el gasto de energía** (Fuller et al., 2020). **[Validado]** para pasos y corazón; **la app no usa el gasto del reloj como dato principal** (`cal_activas`, `cal_totales` se guardan, pero conviene desconfiar de ellos).

### 4.5.3 Otras lecturas del reloj

Peso, grasa corporal, frecuencia respiratoria (`resp_rpm`), temperatura corporal, **temperatura de la piel** (`temp_piel_delta`: diferencia respecto a la línea base del propio dispositivo), presión arterial, distancia, calorías activas y totales, pisos, VO2 máx. y agua: se guarda **lo que el reloj o la báscula escriban**, si tienes el permiso. **Ejercicio:** se leen las sesiones (tipo, duración, frecuencia cardiaca media y máxima) y se asignan al **día lógico** del inicio (igual que los pasos desde el esquema 13; hasta el 12 los pasos usaban el calendario, H-3-1).

### 4.5.4 Resumen de confianza (reloj)

| Dato | Calidad esperada | Nota |
|---|---|---|
| Pasos | Buena | Subestima un poco; ignora movimiento sin brazo |
| Frecuencia cardiaca | Buena en reposo; peor en movimiento intenso | |
| Sueño total | Razonable; tiende a sobrestimar si despiertas en silencio | |
| Etapas del sueño | Baja | Útiles solo como tendencia |
| HRV nocturno | Razonable, ruidoso entre noches | Mirar promedios de varios días |
| Calorías | Baja | No usar para decisiones |

: Tabla 4.7. Resumen de confianza (reloj)

## 4.6 Importaciones: lo que cargas tú

Las importaciones tienen una ventaja (traen datos que el teléfono no ve) y tres riesgos: llegan **a destiempo** (cuando tú los cargas), **parciales** (solo lo que cada servicio exporta) y en **formatos que cambian**. Cada fuente tiene un «toca cada N días» en el panel de estado (por ejemplo, mensual).

### 4.6.1 Spotify (`data/Spotify.kt`, `Importaciones.spotify`)

**Dos caminos que se complementan:**

**(a) Conexión directa (API «recently-played»).** Autorización **PKCE** (un método estándar sin guardar un «secreto» en la app: la app crea un código de un solo uso y su huella SHA-256). La app abre durante un rato un receptor en `127.0.0.1` (solo accesible desde el propio teléfono) para recibir la respuesta. Los **tokens** se guardan en preferencias privadas y **no entran al respaldo ni a los CSV**. Cada **~25–30 min** (desde el pulso) lee las **últimas 50** reproducciones. *Limitación de Spotify:* esa API solo devuelve 50, así que si no se consulta a tiempo (muchas canciones cortas, teléfono sin pulso), **se pierden**.

Spotify entrega la hora en que **terminó** cada canción (`played_at`); la app calcula el **inicio = fin − duración** de la canción. **Ojo (H-3-9):** si paraste la canción a la mitad, el inicio calculado queda **demasiado temprano** (resta la duración completa y no lo que realmente escuchaste). El error es de minutos y solo importa cerca del corte de las 05:00. No hay con qué corregirlo: de cada reproducción, `Spotify.sincronizar` lee la hora de fin (`played_at`) y la duración de la canción (`duration_ms`), no cuánto se escuchó (no comprobé en esta sesión, en la documentación de Spotify, que la API no lo dé). El historial descargable (b) no tiene este problema: resta lo que de verdad escuchaste (`ms_played`, `Importaciones.spotify`), y al importarlo **reemplaza** lo que había llegado por la API en esos días. Por eso H-3-9 queda **resuelto por documentación**: el límite es de la conexión directa y se corrige importando el historial.

**(b) Historial descargable.** El «historial de reproducción ampliado» (`Streaming_History_Audio_*.json`) trae **cuántos milisegundos escuchaste** y si **saltaste** la canción. Las horas vienen en **UTC** y se convierten a la hora local de entonces (desde 10f, con la zona que tenía el teléfono en ese instante; sección 4.3). La app **reemplaza** lo de Spotify anotado antes por notificaciones o por la API en esos días (el historial es más preciso) y no duplica (identificador por huella de *hora de fin + título + artista*). Distingue las reproducciones de **≥ 30 s** («reales»; 30 s es el umbral habitual con que Spotify cuenta una reproducción, dato que no comprobé en esta sesión). La variable `canciones` del panel cuenta solo esas, en la app y en la exportación (desde la entrega 10d, P-30; antes la app contaba todas las escuchas y la exportación solo las de 30 s o más, H-9-3). Una escucha sin la duración escuchada (por notificaciones o por la API) cuenta.

**Música vs. «lo que sonó».** `Escucha.esMusica`: todo lo de Spotify cuenta; lo que viene de notificaciones cuenta salvo apps de video o navegadores.

### 4.6.2 Google Takeout: YouTube, búsquedas y fotos

**Historial** (`Importaciones.google`): lee `watch-history.json`, `search-history.json`, `MyActivity.json` (JSON, no HTML). Para cada registro toma título, hora, canal, producto. **Quita los anuncios** (marcados «From Google Ads»). Reconoce los prefijos «Watched/Viste/Has visto…», «Searched for/Buscaste…», «Visited/Visitaste…» (inglés y español). Genera un **resumen por día** (videos vistos, búsquedas, canales más vistos) recalculado con todo lo guardado en esas fechas para no contar dos veces.

**Qué es un «video visto».** Es un **evento** de «abriste/viste este video» en el historial de Google, **no una duración** de visualización. Un video abierto 5 segundos y uno visto completo cuentan igual.

**CSV de YouTube:** suscripciones, «me gusta», listas de reproducción, comentarios y chats en vivo. De los comentarios y chats **solo se guarda la fecha** (nunca el texto).

**Fotos con lugar (Takeout de Google Fotos):** cada foto trae un `.json` con la fecha, el lugar (`geoData`) y **de dónde llegó** (subida desde un teléfono, álbum compartido, fotos de tu pareja…). Esa «procedencia» permite **separar las fotos propias de las compartidas**.

**Riesgos.** Si el historial de actividad estaba **pausado** en tu cuenta de Google, hay huecos; solo cubre hasta la fecha del Takeout; y el formato cambia entre versiones (la app lo intenta tolerar y avisa qué archivos no pudo usar).

### 4.6.3 Kindle (`Importaciones.kindle`)

Lee `My Clippings.txt` (subrayados, notas y marcadores). Reconoce los formatos de fecha en inglés y español. La fecha del subrayado es **cuándo subrayaste**, no cuándo leíste. La hora del archivo no dice de qué zona es: desde 10f se lee con la zona que regía ese día (`Fechas.millisDe`). Cada fila se identifica por una **huella** (libro + metadatos + texto) para no duplicar al reimportar. El **texto** del subrayado **sí se guarda** (a diferencia de WhatsApp y comentarios).

### 4.6.4 WhatsApp (`Importaciones.whatsappLeer / whatsappGuardar`)

**Qué hace.** Lee chats exportados («Exportar chat → Sin archivos») y **solo guarda conteos y tiempos**; **ningún texto**. Para cada chat y día guarda: mensajes míos y de otros, palabras mías y de otros, multimedia, hora del primero y del último, **tiempo de respuesta** (mediana en minutos), **cuántas veces iniciaste tú la conversación** y cuántas conversaciones hubo.

**Reglas exactas.**
- Una **conversación nueva** es un mensaje que llega **más de 4 horas** después del anterior.
- Un **tiempo de respuesta** es el tiempo entre el último mensaje de la otra persona y tu primera respuesta, **si fue en menos de 12 horas**.
- Un chat es **de grupo** si tiene **más de 2 autores**.
- Hay que **decir cuál autor eres tú** para separar «míos» y «de otros».
- El **día** es el **día lógico**. Las horas del export no dicen de qué zona son: desde 10f se leen con la zona que regía ese día (`Fechas.millisDe`, sección 4.3).

**La ambigüedad de las fechas (H-3-10).** Un export puede venir como día/mes o mes/día («3/5/26»). La app lo decide así, chat por chat (`Importaciones.whatsappLeer`): si algún primer número pasa de 12, es día/mes; si algún segundo número pasa de 12 y ningún primero, es mes/día. Hasta la v90, **si todo era ≤ 12 asumía día/mes**, y un chat corto en mes/día se leía al revés.

**Desde 10e (D.105)**, cuando ningún número pasa de 12, la app prueba las dos lecturas y cuenta, en cada una, cuántas veces un mensaje queda con fecha **anterior** a la del mensaje de arriba (los mensajes vienen en el archivo en el orden en que se mandaron). Se queda con la lectura que tiene menos retrocesos; **con empate, día/mes**. Ejemplo (inventado): un chat con mensajes del «5/10/26», «5/11/26» y «6/1/26». Leído como mes/día, son el 10 y el 11 de mayo y el 1 de junio: van en orden, 0 retrocesos. Leído como día/mes, son el 5 de octubre, el 5 de noviembre y el 6 de enero: un retroceso (de noviembre a enero). Gana mes/día. **[Diseño propio]**.

*Cuándo falla.* Si las dos lecturas dejan los mensajes en orden, decide el empate. Por ejemplo, un chat con mensajes de un solo día («3/5/26») o con el primer número fijo («3/5», «3/6», «3/7») queda en día/mes, aunque sea mes/día. La app no pregunta, como proponía el hallazgo: elige sola. En un chat con fechas mayores a 12, la regla vieja sigue decidiendo y no falla.

**Multimedia.** Un mensaje cuenta como multimedia (y sus palabras no se cuentan) si contiene, sin importar mayúsculas, alguna de estas frases (`Importaciones.MULTIMEDIA`): «omitido», «omitida», «omitted», «<adjunto:», «<attached:» y, desde 10e (D.105), «<médias omis>», «<mídia oculta>», «<media omessi>» y «<medien ausgeschlossen>» (francés, portugués, italiano y alemán). Hasta la v90 solo reconocía español e inglés, y en otro idioma esos avisos contaban como palabras. En un idioma que no está en la lista sigue pasando. Que esas frases sean las que escribe WhatsApp en cada idioma no lo comprobé con un export real en esta sesión.

**Razonamiento.** Las medidas de frecuencia, equilibrio (quién escribe más) y rapidez de respuesta son indicadores usados en el estudio de relaciones y comunicación mediada; aquí sirven para cruzar con el ánimo y con la red de personas (capítulo 7). **[Aproximación]**: dependen de que **exportes el chat completo y a tiempo**; no es una medición continua.

### 4.6.5 Cold Turkey (laptop) (`data/Lote3.kt`, `ColdTurkey`)

Cold Turkey es un programa de bloqueo para la computadora que registra **minutos por app y por sitio web**. La app importa su CSV («Statistics → Export», uno de apps y otro de sitios).

**Cómo se lee.** Para cada fila, la fecha (`AAAA-MM-DD`) y los minutos; nombres normalizados (sin `www.`, sin `.exe`). Solo se guardan días desde el **primer día con algún minuto**.

**Variables.** `laptop_min` (total de apps, o de sitios si no hay apps), `laptop_web_min` (total de sitios) y minutos por **categoría**: productivo, ocio, social, otro. Los **navegadores** no cuentan en categorías (sus minutos ya están repartidos entre los sitios): son 8, `chrome`, `msedge`, `firefox`, `opera`, `brave`, `vivaldi`, `safari` y `arc` (`ColdTurkey.NAVEGADORES`). Desde 10e, `vm_laptop()` de R excluye los mismos 8; antes solo excluía los 5 primeros, y en R los minutos de Vivaldi, Safari o Arc se contaban dos veces (D.106, H-14-6).

**La categorización es automática** con listas de palabras: «productivo» (winword, excel, rstudio, code, notion, github, zoom…), «ocio» (steam, netflix, youtube, spotify, leagueoflegends…), «social» (whatsapp, instagram, reddit, **x.com**…). Lo demás es «otro». Puedes **corregir a mano** la categoría de cada nombre, y tu corrección siempre manda.

**Cómo se compara, desde la entrega 10d (P-33, `ColdTurkey.coincide`).** Hay dos casos:
- **Sitios** (nombres con punto): la palabra de la lista tiene que ocupar **partes completas del dominio**. La regla es que «.sitio.» contenga «.palabra.». Así, «x.com» coincide con `x.com` y `m.x.com`, pero ya no con `dropbox.com` ni `wix.com`. «youtube» coincide con `youtube.com` y `m.youtube.com`, y «gemini.google» con `gemini.google.com`. «max.com» ya no atrapa a `hbomax.com`.
- **Apps** (sin punto): basta con que el nombre contenga la palabra, como antes (`rstudio`, `leagueclient`).

Se agregó «leagueoflegends» a «ocio», porque con la regla nueva «league» ya no coincide con `leagueoflegends.com`. **[Diseño propio]**.

**El defecto que corrigió (H-3-6).** Antes la coincidencia era por *contiene* también en los sitios: «**x.com**» coincidía con **«dropbox.com»**, **«box.com»** o **«wix.com»**, que se clasificaban como «social», y con «code» pasaba algo parecido. Eso inflaba `laptop_social_min`. Lo que queda: un sitio que no está en ninguna lista va a «otro», y en las apps, «code» sigue coincidiendo con cualquier nombre que lo contenga.

**Día.** El de la fecha del CSV (calendario).

### 4.6.6 MacroFactor (nutrición y entrenamiento) (`data/Macrofactor.kt`)

Lee el `.xlsx` que exporta MacroFactor (la app incluye su propio lector mínimo de Excel, sin librerías externas): resumen diario de nutrición, alimentos, series de entrenamiento y series/volumen por grupo muscular. **Reimportar un periodo reemplaza lo de esas fechas.**

- Los nombres de columna se traducen a claves de la app (`calories_kcal → kcal`, `protein_g → proteina_g`, `caffeine_mg → cafeina_mg`…). Las **libras se convierten a kilos** (× 0.45359237).
- **Horario de comidas:** se saca la hora de la **primera** y la **última** comida y la de la **última cafeína** (importa para el sueño), por fecha de MacroFactor. Según el Apéndice D (D.91), es la hora en que **anotaste** el alimento, no necesariamente la de la toma (no lo comprobé en la documentación de MacroFactor). Desde 10e (D.92), la variable `cafeina_ultima_h` del panel ya no es esa hora tal cual: sale de las mismas tomas que `cafeina_total_mg` (MacroFactor y lo anotado en la app, juntadas toma por toma desde 10e, D.91), por día lógico y sumando 24 antes de las 5:00. El detalle está en el capítulo 5.
- **Entrenamiento:** de las series se calculan series efectivas (**sin calentamientos**), repeticiones, volumen = Σ (peso × repeticiones) y la duración del entrenamiento.
- En el panel, `balance_kcal` = kcal comidas − gasto estimado (si ambos existen).
- **1RM estimado** (`e1rm`, solo series efectivas de **≤ 12 repeticiones**): `peso × (1 + repeticiones / 30)` (con **1** repetición, el peso mismo desde el 5 nov 2026, D.156; antes daba 3.3 % de más) (**fórmula de Epley**, una estimación de la fuerza máxima en una repetición, útil para *comparar* tus propias series entre días). Es una estimación, no una prueba de fuerza; el tope de 12 repeticiones es una decisión del código (no revisé literatura específica sobre su exactitud). **[Aproximación]**.

**Limitación que conviene saber.** Lo que MacroFactor registra **depende de lo que tú anotas** y de la base de datos de alimentos: los **micronutrientes** (vitaminas y minerales) suelen estar **incompletos** en las entradas de alimentos. No lo pude comprobar con una fuente citable en esta sesión, así que **trátalo como supuesto**, no como hecho. El gasto energético («expenditure») es un **algoritmo propio de MacroFactor**, no una medición.

### 4.6.7 Otras importaciones

- **Cronología de Google Maps** (JSON del teléfono o el formato viejo de Takeout): se leen los puntos de ubicación y las visitas y se guardan en la tabla `ubicaciones`, con fuentes «maps», «maps_visita» y «takeout». No se duplica un punto con la misma marca de tiempo. Los puntos del JSON del teléfono **no traen precisión** (queda vacía) y, desde la entrega 10d, se leen como de 100 m (4.4.2). El formato viejo de Takeout sí trae `accuracy` en cada punto, y desde 10d se guarda como su precisión (P-33; antes se descartaba) (**H-3-8**, H-9-7).
- **Notas de Obsidian (`.md`)**: migran los **diarios antiguos** (YAML al principio de cada nota) a días de la app, sin sobrescribir los existentes. Desde la entrega 10d (P-33), `ts_creado` y `ts_editado` se toman del encabezado si los trae y, si no, quedan vacíos; antes recibían la hora de la importación, y el día parecía escrito ese día (H-9-5). `schema_v` es el del encabezado, o 0.
- **Respaldo propio** (JSON, `.zip` o `.vmc` cifrado) y el JSON de la versión web: restauran datos y configuración (capítulos 2 y 10). Los días de la versión web conservan sus sellos y, desde la entrega 10d, `schema_v` es el del día en el archivo, o 0; antes recibían la versión actual (H-9-5).

## 4.7 El mundo sin conexión (`data/Mundo.kt`)

Para convertir coordenadas en **países y ciudades** la app no manda tus ubicaciones a internet: trae dentro de sí **fronteras de Natural Earth**, unas **135 mil localidades de GeoNames** y los sitios del **Patrimonio Mundial** de la UNESCO.

- **País:** una regla clásica de geometría (**par-impar**, *ray casting*) decide si el punto cae dentro del contorno de cada país; las islas pequeñas pueden perderse por la simplificación de los contornos, así que, si el punto cae «fuera de todo», se asigna el país más cercano a **menos de ~0.6°** (unos 65 km) del borde.
- **Ciudad:** la localidad de la lista **más cercana a menos de 3 km** o, si no hay, la **ciudad principal** (≥ 5 mil habitantes) a menos de **30 km**.

**Razonamiento.** Procesar localmente es una decisión de **privacidad** (capítulo 10) y también de **disponibilidad** (funciona sin internet). El precio es resolución: los contornos están simplificados, así que **en la frontera** el país puede ser el equivocado. **[Aproximación]**.

## 4.8 Cobertura y huecos: ¿cómo se ve un dato que falta?

Cada fuente empieza cuando le das el permiso (o cuando importas), tiene huecos (teléfono apagado, reloj sin batería) y algunas se llenan por periodos (importaciones). La Figura 4.3 muestra una situación **inventada** de 90 días: cada fila es una variable, cada columna un día, **oscuro = hay dato** y **claro = no hay (null)**.

![Figura 4.3. Cobertura sintética de 90 días por fuente (oscuro = con dato; claro = sin dato) y porcentaje de días con dato. Script: `figuras/fig06_cobertura.py`.](figuras/fig06_cobertura.png){width=100%}

**Cómo lo trata el panel (capítulo 2, sección 2.6).** Una celda sin dato queda **vacía**, no en 0, salvo las reglas de relleno con cero (que son por función, no por sensor). Los datos **de sensores** (pantalla, reloj, ubicación, notificaciones) **nunca se rellenan con ceros**: sin lectura, sin dato. Un 0 aparece solo cuando la fuente sí registró algo ese día; por ejemplo, desde 10c, `distancia_km` con puntos pero sin movimiento y `ejercicio_min` con datos del reloj y sin sesión (D.63).

**Por qué importa para el análisis.** Dos variables pueden estar disponibles en periodos distintos. Una correlación entre «Spotify» y «ánimo» solo usa los días con ambas. Si **el hueco no es azar** (por ejemplo, el reloj se agota justo las noches de poco sueño), el resultado se **sesga** aunque se ignoren las celdas vacías (datos faltantes «no al azar», Rubin, 1976; capítulo 2).

**Lo que la app NO guarda (limitación L-4.1).** El *motivo* del hueco (permiso, apagado, nada que reportar). El panel de estado lo ve «en vivo», pero **no queda en el histórico**.

## 4.9 Resumen de amenazas a la validez

| # | Amenaza | Dónde | Qué hace la app | Qué falta |
|---|---|---|---|---|
| L-4.1 | **No se guarda el motivo de un hueco** | Todas las fuentes pasivas | Estado en vivo | Marcar «hueco por permiso/apagado» en el histórico |
| L-4.2 | **Dos criterios de día** (calendario vs. lógico) | 4.3 | Desde el esquema 13, pantalla y pasos por día lógico, con `ventana_pantalla` y `ventana_pasos`; desde 10c, el diccionario marca las variables por calendario (`Diccionario.CALENDARIO`) | Corazón, oxígeno, agenda, clima y ciudades siguen por calendario, y los días de antes del esquema 13 no se recalculan solos (H-3-1, resuelto en parte; H-3-2, resuelto en los días nuevos) |
| L-4.3 | **Muestreo cada ~15 min con mínimos** (minutos «observados») | 4.4.5 | Tope de 15 min tras huecos; desde 10f, lo que no se pudo saber del wifi va a `wifi_sin_dato_min` (H-3-7, resuelto) | Guardar también las horas cubiertas por muestras |
| L-4.4 | **Proxies** (luz → aire libre; notificaciones → interrupciones; pantalla de apps ≠ pantalla encendida) | 4.4 | Variables separadas | Validar contra autorreportes |
| L-4.5 | **Instrumentos de consumo** (reloj, micrófono, clasificador de actividad) | 4.5, 4.4.6 | — | Sin calibración propia |
| L-4.6 | **Importaciones tardías y parciales** | 4.6 | Recordatorios | Fecha de «cubierto hasta» por fuente |
| L-4.7 | **Procedencia mixta de clima** | 4.4.10 | Desde 10f, fuente por día (`clima_reanalisis`) y el pronóstico se cambia por el reanálisis a los 80 días (H-3-4, resuelto) | Los últimos 80 días siguen siendo de pronóstico, y el índice UV solo existe en el pronóstico |
| L-4.8 | **Presión descartada en altura** (hasta 10d) | 4.4.5 | Desde 10d acepta 300–1 100 hPa (`Percepcion.RANGO_HPA`) | — (H-3-3, resuelto) |
| L-4.9 | **Medir cambia la conducta** (saber que te miden) | Todas | — | Inevitable en N-de-1 |
| L-4.10 | **Hora local tras viajes o cambios de horario** | 4.3 | Desde 10f, cada instante con la zona que tenía el teléfono entonces; `desfase_utc_h` y `cambio_horario` (H-1-7, resuelto) | Antes de 10f, el historial se reconstruye con las notas; un viaje sin dos notas seguidas queda con la zona de hoy |

: Tabla 4.8. Resumen de amenazas a la validez

## 4.10 Qué es estándar y qué es diseño propio

| Decisión | Clasificación | Fuente o justificación |
|---|---|---|
| Medir uso de pantalla de forma pasiva | Validado | Autorreporte y registro difieren (Parry et al., 2021) |
| Definir pantalla = apps en primer plano, sin lanzador | Diseño propio | No equivale a «pantalla encendida» |
| Detección de estancias por radio y tiempo | Aproximación | Técnica estándar (Zheng, 2015); parámetros propios |
| Radio 75 m, 20 min, precisión ≤ 120 m | Diseño propio | Configurable |
| Minutos por muestreo con tope de 15 min tras huecos | Diseño propio | Subestima a propósito |
| Umbral 1 000 lux = «luz alta» | Diseño propio | Regla práctica |
| Ruido en dB relativos | Aproximación | Los micrófonos de teléfono no están calibrados (Kardous y Shaw, 2014) |
| Sueño del reloj (cálculo, bloques, siestas) | Aproximación | Dispositivos de consumo sobrestiman sueño (Chinoy et al., 2021) |
| RMSSD nocturno | Validado (concepto) | Task Force (1996); Shaffer y Ginsberg (2017) |
| Pasos y corazón del reloj | Validado (laboratorio) | Fuller et al. (2020) |
| Clima de Open-Meteo (reanálisis/pronóstico); desde 10f, fuente por día y reanálisis a los 80 días | Aproximación | Hersbach et al. (2020) para el reanálisis |
| Fase lunar con mes sinódico medio | Aproximación | Fórmula astronómica simple |
| Calibración del barómetro por mediana de 60 días | Diseño propio | Corrige sesgo constante |
| Filtro del barómetro 300–1 100 hPa (10d) | Aproximación | Rango de medida del BMP280 (Bosch Sensortec, 2021); el LPS22HB mide 260–1 260 hPa (STMicroelectronics, 2016) |
| Precisión de 100 m para puntos sin ella (10d) | Diseño propio | Cifra prudente |
| Cold Turkey por partes completas del dominio (10d) | Diseño propio | Corrige H-3-6 |
| Importar solo conteos de WhatsApp | Diseño propio | Privacidad |
| Fechas ambiguas de WhatsApp por el orden de los mensajes (10e) | Diseño propio | Corrige H-3-10 sin preguntar |
| Wifi en tres casos: con red, sin red, sin dato (10f) | Diseño propio | Corrige H-3-7 |
| Historial de zonas de lo de antes armado con «dos notas seguidas» (10f) | Diseño propio | Corrige H-1-7 en lo ya guardado, hasta donde las notas lo permiten |

: Tabla 4.9. Qué es estándar y qué es diseño propio

## 4.11 Resumen del capítulo

1. Los datos pasivos llegan por un **pulso de ~15 min** (ubicación y estados del teléfono) y una **sincronización diaria** (reloj, pantalla, fotos, agenda, llamadas, clima). Los importados llegan **cuando tú los cargas**.
2. **No todas las fuentes usan el mismo «día»:** hasta el esquema 12, pantalla, reloj, agenda y clima usaban el día de calendario, y casi todo lo demás, el día lógico (H-3-1, H-3-2); fue el hallazgo más importante del módulo 3. Desde el esquema 13 (10b), la pantalla y los pasos van por día lógico; siguen por calendario el corazón, el oxígeno y las demás lecturas del reloj (salvo pasos, sueño y ejercicio), la agenda, el clima y las ciudades, y desde 10c el diccionario lo marca en cada variable. Desde 10f, además, cada instante se lee con la zona horaria que tenía el teléfono entonces, no con la de hoy (H-1-7): un viaje ya no mueve lo que pasó a otra hora ni a otro día, y `cambio_horario` marca los días de 23, 25 u otras horas.
3. Los minutos de wifi, audio, carga y luz son **estimaciones por muestreo** con una regla que **subestima** a propósito; no son minutos del día. Desde 10f, lo que no se pudo saber del wifi va aparte (`wifi_sin_dato_min`) y no a `sin_wifi_min` (H-3-7).
4. El **sueño** del reloj está bien cuidado en el cálculo, pero el dato en sí es una estimación de consumo (buena para duración, débil para etapas).
5. **Detectar un lugar** necesita ~**30 min** reales, no 20.
6. El módulo 3 encontró además defectos concretos: barómetro descartado a gran altura (H-3-3), procedencia mixta del clima (H-3-4), categorías de Cold Turkey por coincidencia de texto (H-3-6). La entrega 10d corrigió el barómetro (300–1 100 hPa), Cold Turkey (partes completas del dominio) y la precisión de los puntos importados (H-3-8, inciso b). La entrega 10e corrigió las fechas ambiguas y el idioma de los avisos de multimedia de WhatsApp (H-3-10). La entrega 10f anotó la fuente del clima de cada día y cambia el pronóstico por el reanálisis a los 80 días (H-3-4; el índice UV sigue siendo solo del pronóstico), y separó el wifi sin dato (H-3-7).
7. El módulo 3 identificó **10 hallazgos** (H-3-1 a H-3-10) y, como todo módulo de contenido, no cambió código. Después, el módulo 10 resolvió H-3-3 y H-3-6, H-3-2 en los días nuevos, el inciso b de H-3-8 y H-3-1 en parte; la entrega 10e resolvió H-3-10 (D.105); la entrega 10f, H-3-4 y H-3-7 (D.111 y D.112), y H-3-5 y H-3-9 quedaron resueltos por documentación (este capítulo los describe; el código no cambió). Quedan **8 resueltos** y **2 resueltos en parte** (H-3-1 y H-3-8, cuyo inciso a no se cambió); ninguno pendiente.

**Siguiente capítulo.** Capítulo 5: modelos fisiológicos (cafeína, alcohol, creatina…) y variables derivadas.


# Capítulo 5. Modelos fisiológicos y variables derivadas

> **Para qué sirve este capítulo.** Algunas variables de la app no se miden: se **calculan con un modelo** a partir de otros datos. La cafeína que te queda en el cuerpo al dormir, el alcohol en sangre, la reserva de creatina del músculo, el «jet lag social» o los minutos al aire libre son **estimaciones**, no mediciones. Este capítulo explica cada modelo: qué calcula, con qué fórmula exacta y qué parámetros, de dónde salen esos parámetros, qué tan preciso puede ser y cuándo falla. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper. Lo que cambió en el módulo 10 se marca «desde la 10c» y está en el apéndice D (D.63 y D.69 a D.72): el peso y la *r* del alcohol, las tomas sin hora, el ritmo de sueño con las noches recientes y los festivos, y una sola repetición máxima. Está puesto al día también con la **entrega 10e** (v91; `SCHEMA_V` sigue en 14), que se marca «desde 10e» (D.88 a D.92, D.98 y D.99): el alcohol de la noche anterior en el máximo del día, la cafeína y el alcohol de MacroFactor y de la app juntados toma por toma, la hora de la última cafeína, la referencia de 3 g de la creatina, los suplementos de vida media corta y los parámetros en el diccionario. De la **entrega 10f** (v92; `SCHEMA_V = 15`), marcada «desde 10f», toca a este capítulo: las horas en la hora local de donde estabas (D.109) y la fuerza de referencia que mide la plantilla de la creatina (D.114). **Los ejemplos usan datos inventados.**
>
> **Cómo leerlo.** Igual que los capítulos anteriores: lenguaje sencillo, fórmulas y cifras del código tal cual, con el nombre de la función. Cada decisión se marca como **[Validado]**, **[Aproximación]** o **[Diseño propio]**. Los problemas se numeran H-4-n (por el módulo 4, en que se escribió este capítulo) y se detallan en `hallazgos.md`. **Ningún número de este capítulo es una medición de tu cuerpo**: son cuentas con parámetros promedio de la literatura.

## 5.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Farmacocinética** | Cómo entra, se reparte y sale una sustancia del cuerpo con el tiempo. |
| **Absorción** | El paso de la sustancia del estómago a la sangre. Tarda un rato: por eso un café no hace efecto en el segundo en que lo tomas. |
| **Eliminación** | Cómo el cuerpo la va quitando (sobre todo el hígado). |
| **Vida media** (T½) | El tiempo que tarda en irse **la mitad** de lo que hay. Si la vida media es 5 h, de 100 mg quedan 50 a las 5 h, 25 a las 10 h, 12.5 a las 15 h. Esto se llama eliminación **de primer orden** (se va una proporción fija por hora). |
| **Eliminación de orden cero** | Se va una **cantidad fija** por hora, sin importar cuánto haya. El alcohol se elimina así casi siempre, porque la enzima que lo procesa trabaja a su máxima velocidad desde los primeros tragos (Jones, 2010). |
| **Volumen de distribución** | El «tamaño del recipiente» en el que se diluye una sustancia. Con la misma cantidad, un recipiente más chico da una concentración más alta. |
| **Factor de Widmark** (*r*) | La fracción del peso corporal en la que se reparte el alcohol (casi toda el agua del cuerpo). |
| **Saturación** | Qué tan llena está una reserva (aquí, la de creatina del músculo), de 0 a 100 %. |
| **Punto medio del sueño** | La hora a la mitad entre dormirte y despertar. |
| **Jet lag social** | La diferencia entre el punto medio del sueño en los días libres y en los laborales: como si cada fin de semana viajaras a otro huso horario y volvieras el lunes (Wittmann et al., 2006). |
| **Proxy** | Una medida indirecta de lo que interesa (capítulo 4). |

: Tabla 5.1. Mini-glosario

## 5.2 Mapa: qué se estima y con qué

| Qué | Función (archivo) | Parámetros | Variables del panel | Tipo |
|---|---|---|---|---|
| Cafeína en el cuerpo | `Sustancias.cafeinaEn` (`data/Sustancias.kt`) | absorción 45 min; vida media 5 h (ajustable) | `cafeina_total_mg`, `cafeina_al_dormir_mg`, `cafeina_ultima_h` (desde 10e; y `cafeina_mg`, de MacroFactor) | Aproximación |
| Alcohol en sangre | `Sustancias.alcoholEn` | absorción 30 min; β = 0.15 g/L/h; *r* de Watson (con peso, sexo, estatura y edad) o fija de 0.68, 0.55 o 0.62; peso de cada día | `alcohol_bebidas`, `alcohol_estimado_max`, `alcohol_al_dormir` | Aproximación |
| Nivel subjetivo de un episodio | `Sustancias.nivelEn` | tus lecturas de 0 a 10 | `alcohol_nivel_max`, `cannabis_nivel_max`, `*_horas` | Diseño propio |
| Reserva de creatina | `Suplementos.carga` (`data/Lote3.kt`) | 28 días para llenar con 3 g al día (desde 10e), 35 para vaciar | `creatina_saturacion_pct`, `supl_creatina_nivel` | Aproximación |
| Suplementos con vida media | `Suplementos.cantidadEn` | vitamina D 360 h, magnesio 12 h, melatonina 1 h, omega-3 60 h | `supl_<id>_nivel` (desde 10e, solo con vida media de 6 h o más: la melatonina no lo tiene) | Aproximación |
| Probióticos | `Suplementos.probioticosEstimados` | palabras clave en la bitácora de comidas | `probioticos_porciones`, `probioticos_estimado` | Diseño propio |
| Ritmo de sueño y jet lag social | `Analisis.ritmo` (`ciencia/Analisis.kt`) | noches de los últimos 56 días; días libres = sábado, domingo y, en México, festivos (`Fechas.esDiaLibre`) | solo en la pantalla Análisis (el panel exportado lleva `festivo` y `dia_libre`) | Validado (definición) / Aproximación |
| Noches sin reloj | `Analisis.ritmo` | último y primer uso del teléfono | solo en Análisis | Aproximación |
| Aire libre, km | `Estadistica.panel`, `Movilidad.kmPorDia` | luz ≥ 1000 lux, actividad; trayectos en línea recta | `aire_libre_min`, `distancia_km` | Aproximación (capítulo 4) |
| Otras | `Luna`, `Grados` (`data/Calculos.kt`), fórmula de Epley (`Macrofactor.e1rm`) | — | `luna_ilum`, grados de escalada, `fuerza_ref_kg` (desde 10f) | Validado / Aproximación |

: Tabla 5.2. Mapa: qué se estima y con qué

## 5.3 Cafeína en el cuerpo

### 5.3.1 Qué hace

Estima cuántos miligramos de cafeína tienes en el cuerpo en cada momento. Se usa en la pantalla de sustancias («ahora», «baja de 50 mg a las…»), en la cinta del día y en el panel (`cafeina_al_dormir_mg`).

### 5.3.2 Cómo lo hace exactamente (`Sustancias.cafeinaEn`)

Para cada dosis *D* (mg) tomada hace Δ*t* horas:

$$C(t) = \sum_{\text{dosis}} D \cdot \min\!\left(1, \frac{\Delta t}{0.75}\right) \cdot 0.5^{\Delta t / T_{1/2}}\qquad\qquad (5.1)$$

- El primer factor sube en línea recta de 0 a 1 en **45 minutos** (0.75 h): la absorción.
- El segundo es la eliminación de primer orden con la vida media *T*½ de `Config.cafeina_vida_media_h`, **5 h por omisión** (se cambia en Privacidad, de 2 a 10 h en pasos de media hora).
- Las dosis se suman: cada café se va por su cuenta.

**De dónde salen las dosis** (`Sustancias.dosis`): de **MacroFactor** (cada alimento con su hora y su `cafeina_mg`) y de lo que anotes en la app. Desde la entrega 10e (D.91), las dos fuentes se juntan **toma por toma**, para no contar dos veces la misma taza:

- Cada toma de la app (en orden de hora) busca entre las de MacroFactor que aún no tienen pareja una que esté a **90 minutos o menos** (`MISMA_TOMA`) y cuya cantidad vaya **de la mitad al doble** de la suya; si hay varias, la más cercana en hora.
- Si la encuentra, las dos son **la misma toma** y cuenta **una vez, con la hora y los miligramos de la app**: la hora de MacroFactor es la de cuando registraste el alimento, no necesariamente la de cuando lo tomaste.
- Las tomas de MacroFactor sin pareja y las de la app sin pareja cuentan todas.

*Ejemplo inventado.* En la app anotas un café de 150 mg a las 14:00; en MacroFactor hay un café de 95 mg a las 8:30 y uno de 120 mg registrado a las 15:10. El de 15:10 está a 70 minutos y 120 cae entre 75 y 300 mg: es la misma toma. Cuentan 95 mg a las 8:30 y 150 mg a las 14:00: `cafeina_total_mg` = **245 mg**. Hasta 10e, como MacroFactor tenía cafeína ese día, mandaba solo MacroFactor (H-4-2): **215 mg**, con el segundo café a las 15:10 y el de la app perdido. Los 90 minutos y el rango de la mitad al doble son **[Diseño propio]**: un margen razonable para la diferencia entre la hora de la toma y la del registro, no un valor estimado.

Desde la entrega 10c, un alimento de MacroFactor cuya hora no se puede leer se pone **al mediodía de su día** (`msComida`) en lugar de perderse; antes desaparecía de `cafeina_total_mg` aunque sí contaba en `cafeina_mg` (H-9-13).

**Variables del panel:**

- `cafeina_total_mg`: suma de las dosis del día lógico (de ambas fuentes, juntadas toma por toma), desde el primer día con cafeína.
- `cafeina_al_dormir_mg`: la cafeína estimada **en el instante en que te dormiste** según el reloj, sumando las dosis de las **36 horas** previas. Va en el día del sueño (el día en que despiertas).
- `cafeina_ultima_h`: desde 10e (D.92), la hora de la **última dosis del día lógico** en la misma lista de dosis que `cafeina_total_mg` (MacroFactor y la app), en hora decimal con dos decimales y, desde 10f (D.109), en la hora local de donde estabas al tomarla (`Fechas.local`); **antes de las 5:00 suma 24** (un café a las 0:30 cuenta como 24.5 del día anterior), como `ultimo_uso_h`. Queda vacía los días con bitácora de MacroFactor sin ninguna dosis. Antes salía solo de MacroFactor y por día de calendario, así que un café anotado solo en la app no contaba y uno de madrugada caía en el día siguiente. La exportación (`Exportar.filasPanel`) usa la misma cifra y solo recurre a la de MacroFactor si el panel no la calculó.
- `cafeina_mg` sigue viniendo **directo de MacroFactor** (el nutriente del día, tal cual, como los demás nutrientes), sin el modelo.

### 5.3.3 Razonamiento científico

El informe del Institute of Medicine (2001) resume: la cafeína se absorbe **rápida y casi completamente** (alrededor del **99 % en 45 minutos**, con el pico en sangre entre 15 y 120 minutos), y su vida media en adultos sanos es en promedio de **unas 5 horas**, con un rango individual de **1.5 a 9.5 horas**. El embarazo, la obesidad, los anticonceptivos orales, el tabaco y la altitud la cambian. Nehlig (2018) explica que esa variabilidad depende en gran parte de la enzima hepática CYP1A2, que metaboliza el 95 % de la cafeína y tiene variantes genéticas.

Los parámetros de la app (45 min y 5 h) coinciden con esos promedios. **[Aproximación]**: el modelo es razonable para el promedio, pero tu vida media real puede estar en cualquier punto de ese rango.

### 5.3.4 Ejemplo trabajado (Figura 5.1)

Un café de **95 mg a las 8:00** y uno grande de **150 mg a las 14:00**; te duermes a las **23:30**. Con *T*½ = 5 h:

- Primer café: han pasado 15.5 h → 95 × 0.5^(15.5/5) = 95 × 0.117 = **11.1 mg**.
- Segundo café: han pasado 9.5 h → 150 × 0.5^(9.5/5) = 150 × 0.268 = **40.2 mg**.
- `cafeina_al_dormir_mg` = **51.3 mg**.

Con una vida media corta (2.5 h; el rango va de 1.5 a 9.5 h) serían **12 mg**; con la más larga (9.5 h), **106 mg**. **La misma tarde da una cifra que varía casi nueve veces según la persona.** Por eso la cifra solo sirve **para compararte contigo mismo** (si fijas tu vida media), no como dato absoluto.

![Figura 5.1. Cafeína estimada tras dos dosis con tres vidas medias. Script: `figuras/fig25_cafeina.py`.](figuras/fig25_cafeina.png){width=100%}

### 5.3.5 Cuándo falla

- **El pico queda un poco bajo y temprano** (H-4-1, resuelto por documentación: este punto y el siguiente son esa documentación; el código no cambió). Como la rampa de absorción se multiplica por la eliminación desde el primer minuto, a los 45 min queda 0.5^(0.75/5) = **90 %** de la dosis. Un modelo farmacocinético estándar (absorción y eliminación de primer orden) da otra forma de curva. Para dosis separadas por horas, la diferencia es pequeña. **[Aproximación]**.
- **Una sola vida media para toda la historia**: si la cambias, **todas** las `cafeina_al_dormir_mg` pasadas se recalculan con la nueva, sin dejar registro.
- **Juntar dos fuentes** (H-4-2, resuelto en 10e, D.91). Hasta 10e mandaba una sola fuente por día: si en MacroFactor anotaste un café y en la app otro distinto el mismo día, el de la app se perdía. Ahora se juntan toma por toma (sección 5.3.2), pero la regla puede equivocarse en los dos sentidos: dos cafés distintos de cantidad parecida, uno en cada app y a menos de 90 minutos, cuentan como uno; y la misma taza registrada en MacroFactor más de 90 minutos después, o con una cantidad muy distinta, cuenta dos veces. Una toma de MacroFactor sin pareja conserva la hora del registro, que puede no ser la de la toma.
- **Las cifras cambiaban según dónde se calcularan** (H-4-3, resuelto en 10c, D.71). Para no trabar la pantalla, cuando el panel se calculaba en el hilo principal **sin un cálculo previo guardado**, `Estadistica.sustancias` usaba solo lo anotado en la app y **omitía MacroFactor**, y la pantalla de experimentos calculaba ahí. Desde la entrega 10c: la pantalla de experimentos arma el panel en segundo plano, como Explorar y Análisis; si aun así toca calcular en el hilo principal sin caché y hay datos de MacroFactor, las variables que dependen de las dosis (`cafeina_total_mg`, `cafeina_al_dormir_mg`, `alcohol_bebidas`, `alcohol_estimado_max`, `alcohol_al_dormir`) quedan **vacías** en lugar de dar otra cifra (`alcohol` 0/1 valía 1 solo en los días con episodios sin cantidad y 0 en los demás, aunque hayas bebido; desde 10e, D.90, en ese caso vale **1 los días con un episodio anotado** y queda **vacío** los demás, porque sin MacroFactor no se sabe si un día sin episodios fue 0, H-14-4); y la clave de la caché incluye la identidad del mapa de MacroFactor, que cambia con cada importación, no solo el número de días (H-9-14).
- **Cuatro variables de cafeína** (H-4-12, resuelto en parte en 10e, D.92): `cafeina_total_mg`, `cafeina_al_dormir_mg` y, desde 10e, `cafeina_ultima_h` salen de la misma lista de dosis; `cafeina_mg` sigue siendo el nutriente de MacroFactor, así que puede no coincidir con `cafeina_total_mg` los días con cafeína anotada en la app. En el hilo principal sin caché (punto anterior), `cafeina_ultima_h` queda vacía, como las demás variables que dependen de MacroFactor.

## 5.4 Alcohol en sangre: el modelo de Widmark

### 5.4.1 Qué hace

Estima la concentración de alcohol en sangre (g/L, aproximadamente lo mismo que «por mil», ‰) a lo largo de la noche. Se usa en la cinta del día, en la lista de episodios («≈ 0.45 g/L estimado») y en el panel.

### 5.4.2 Cómo lo hace exactamente (`Sustancias.alcoholEn`)

La fórmula clásica de Widmark (de los años treinta) dice que la concentración es el alcohol tomado repartido en el agua del cuerpo, menos lo que el hígado ya quitó:

$$C(t) = \frac{A}{r \cdot W} - \beta \cdot t\qquad\qquad (5.2)$$

con *A* = gramos de alcohol, *W* = peso (kg), *r* = factor de Widmark y β = velocidad de eliminación. La app la aplica como una **simulación en pasos de 5 minutos**:

1. Cada dosis entra **repartida en 30 minutos** (en cada paso de 5 min entra 1/6 de la dosis).
2. En cada paso, el cuerpo quita β · *r* · *W* · (5/60) gramos, con **β = 0.15 g/L por hora**, sin bajar de 0.
3. La concentración es gramos en el cuerpo ÷ (*r* · *W*).

**Los parámetros** (desde la entrega 10c, D.70; los dos se calculan **para cada día** de la simulación, con `rYPeso`):

- *W* (`pesoDe`): de todos los pesos con fecha (la báscula de Health Connect y la **tendencia de peso de MacroFactor**, `pesos()`), el **más reciente hasta ese día**, sea de la fuente que sea; el mismo día, gana la báscula. Si no hay ninguno antes de esa fecha, el **primero posterior**. Solo si no hay ningún peso registrado se usan **70 kg** (`PESO_OMISION`). Antes de la 10c, cualquier pesada de la báscula, aunque fuera de hace años, le ganaba a la tendencia de MacroFactor de ayer, y antes de la primera pesada se usaban 70 kg aunque hubiera pesos posteriores (H-9-24).
- *r*: con un **peso real** ese día, la *r* sale del **agua corporal total** de Watson et al. (1980) (`watsonR`):
  - hombres: agua (L) = 2.447 − 0.09516 · edad + 0.1074 · estatura (cm) + 0.3362 · peso (kg);
  - mujeres: agua (L) = −2.097 + 0.1069 · estatura (cm) + 0.2466 · peso (kg);
  - *r* = agua / (0.80 · peso), acotada entre **0.45 y 0.95**. El 0.80 (`AGUA_SANGRE`) es la proporción de agua en la sangre que usan Watson et al. (1981) para pasar del agua corporal a *r*.

  Hace falta el sexo (de Conocerte o de Salud), la estatura de Conocerte entre **100 y 250 cm** y, en los hombres, la edad de ese día (`Perfiles.edad`, a partir de la fecha de nacimiento de Conocerte). Si falta algo, o si el peso es el de omisión, se usa la *r* fija (`widmarkR`): **0.68** si dijiste «hombre», **0.55** si «mujer», **0.62** si no dijiste nada. Con 70 kg inventados, Watson no corregiría nada: por eso no se usa.
- **Bebida estándar** = **14 g** de alcohol puro (`GRAMOS_BEBIDA`), la definición de Estados Unidos (NIAAA, s. f.). Otros países usan otras cifras.

**De dónde salen las dosis** (`Sustancias.dosis`): de MacroFactor (`alcohol_g` de cada alimento, con su hora; sin hora legible, al mediodía de su día desde la 10c) y de los episodios que anotas. Las bebidas de un episodio se **reparten** en el primer 70 % del episodio (si dijiste cuándo terminó) o una cada 40 minutos (si no).

Desde la entrega 10e (D.91), las dos fuentes se juntan **por episodio**, no por día. Para cada episodio de la app con cantidad (en orden de inicio), se toman los registros de MacroFactor que aún no se usaron y caen entre su inicio y su fin, con **90 minutos** de margen a cada lado (sin fin dicho, el fin es la hora de la última bebida repartida). Son lo mismo y **cuenta la fuente con más gramos**: si MacroFactor suma más, se quedan sus registros y se quitan las bebidas del episodio; si no (también con empate), se quedan las del episodio y se quitan esos registros. Lo de MacroFactor fuera de todo episodio cuenta siempre. *Ejemplo inventado:* un episodio de 20:00 a 23:00 con 3 bebidas (42 g) y, en MacroFactor, una cerveza de 13 g a las 21:30 y un vino de 15 g a las 23:40 (dentro del margen, que llega a las 00:30): 28 g < 42 g, así que cuentan las 3 bebidas del episodio. Hasta 10e, como MacroFactor tenía alcohol ese día, contaban solo los 28 g (H-4-2). El margen de 90 minutos y «la fuente con más gramos» son **[Diseño propio]**.

**Variables del panel** (desde el primer día con alcohol):

- `alcohol_bebidas`: gramos del día ÷ 14.
- `alcohol_estimado_max` (desde 10e, D.88 y D.89): para cada día lógico con alcohol, el máximo de la simulación **desde su primera dosis hasta 14 horas después, o hasta justo antes de la primera dosis del siguiente día con alcohol** si llega antes (para no contar el mismo pico en dos días). La simulación incluye las dosis de las **24 horas previas** a esa primera dosis: lo que seguía en la sangre de la noche anterior cuenta. Las muestras son **cada 5 minutos**, como en la lista de episodios (`Sustancias.alcoholMaximo`). Los días desde el primero con alcohol sin ninguna dosis valen 0. Las 14 horas y las 24 horas son **[Diseño propio]**; con β = 0.15 g/L/h, en 24 horas se eliminan 3.6 g/L, así que lo tomado antes ya no queda en la sangre, salvo tras más de un día bebiendo casi sin parar.
- `alcohol_al_dormir`: la concentración en el instante en que te dormiste, con las dosis de las **18 horas** previas.
- `alcohol` (0/1, desde la 10c con **una sola regla de día**, H-9-17): 1 el día lógico de **cada dosis**; los episodios anotados **sin cantidad** (que no tienen dosis) cuentan el día lógico en que empezaron; 0 los demás días desde el primero con alcohol. Antes, la variable mezclaba el día del episodio y el de las dosis, y un episodio que cruzaba las 5:00 podía quedar en un día y sus bebidas en otro.
- Aparte, `alcohol_nivel_max` y `alcohol_horas` vienen de **tu nivel subjetivo** (sección 5.5), no del modelo.

### 5.4.3 Razonamiento científico

- **β = 0.15 g/L/h** es el valor que Jones (2010) recomienda como **promedio para bebedores moderados**, en una revisión de la evidencia forense: en ayunas, de 0.10 a 0.15; después de comer, de 0.15 a 0.20; en personas con consumo intenso, hasta 0.25–0.35. La eliminación es casi de orden cero porque la enzima se satura desde los primeros tragos. **[Validado]** como promedio.
- **Los factores *r*** de 0.68 y 0.55 son los valores clásicos de Widmark para hombres y mujeres; el 0.62 «sin sexo» es un punto intermedio **[Diseño propio]**. En realidad, *r* depende de la composición corporal: el alcohol se reparte en el agua del cuerpo, y una persona alta y delgada tiene más agua por kilo que una de más peso y edad. Watson et al. (1980) dieron ecuaciones del agua corporal total a partir de peso, estatura, edad y sexo, y Watson et al. (1981) propusieron usarlas para actualizar la ecuación de Widmark. Searle (2015) lo resume así: la *r* de Widmark es el agua corporal total dividida entre el peso por la proporción de agua en la sangre. Desde la 10c la app usa esa *r* cuando tiene los datos. **[Aproximación]** (mejor que la *r* fija): reduce el error, pero no lo elimina; según Searle (2015), la *r* conserva una incertidumbre de alrededor de un 9 %. **El valor 0.80 de la proporción de agua en la sangre [sin verificar]**: se comprobó la definición en Searle (2015), pero no se leyó el valor en el texto original de Watson et al. (1981).
- **La incertidumbre es grande.** Searle (2015) revisa cómo se propaga el error en estos cálculos: Gullberg había propuesto un coeficiente de variación fijo de ±21 % para la concentración calculada, y Searle muestra que el error **no es fijo** y hay que calcularlo en cada caso. **[Aproximación]**: el modelo da un orden de magnitud, no una cifra confiable al centésimo.

### 5.4.4 Ejemplo trabajado (Figura 5.2)

Tres bebidas estándar (42 g) en un episodio de 20:00 a 23:00. La app las coloca a las **20:00, 21:03 y 22:06** (el primer 70 % de 3 h son 2.1 h, repartidas en 2 intervalos).

- **Hombre de 75 kg** (*r* = 0.68): *r* · *W* = 51. Si todo entrara de golpe: 42/51 = 0.82 g/L. Con la absorción y la eliminación simultáneas, la simulación da un **máximo de 0.42 g/L a las 22:35** y llega a **0 a la 01:25**. (Con la fórmula simple: 0.82 − 0.15 × 2.58 h = 0.44 g/L en el pico, y 0.82/0.15 = 5.5 h después de empezar, 01:29: muy parecido).
- **Mujer de 60 kg** (*r* = 0.55): *r* · *W* = 33 → máximo **0.87 g/L**, en 0 a las **04:25**.
- **Sin sexo ni peso** (*r* = 0.62, 70 kg): máximo **0.57 g/L**.

Si en el hombre β fuera 0.10 o 0.20 en lugar de 0.15, el final de la curva se movería más de una hora en cada dirección (banda sombreada).

**Con la *r* de Watson (desde la 10c).** El mismo hombre de 75 kg, si en Conocerte su fecha de nacimiento le da 30 años y dijo que mide 180 cm: agua = 2.447 − 0.09516 × 30 + 0.1074 × 180 + 0.3362 × 75 = 44.14 L; *r* = 44.14 / (0.80 × 75) = **0.736**. Con un recipiente más grande, el máximo baja de 0.42 a **0.36 g/L** y llega a 0 hacia la **01:00** (línea punteada).

**Cuánto cambia con Watson** (tres bebidas de 14 g a los 0, 40 y 80 minutos, como reparte la app un episodio de tres bebidas sin hora de fin; simulación de `alcoholEn`, en pasos de 5 minutos, para personas inventadas; desde 10e es también lo que da `alcohol_estimado_max`; tres decimales para que los porcentajes se puedan rehacer):

| Persona | *r* fija | *r* de Watson | Máximo con *r* fija | Máximo con Watson |
|---|---|---|---|---|
| Hombre, 30 años, 175 cm, 70 kg | 0.68 | 0.749 | 0.607 g/L | 0.527 g/L (−13 %) |
| Hombre, 30 años, 190 cm, 70 kg | 0.68 | 0.777 | 0.607 g/L | 0.497 g/L (−18 %) |
| Hombre, 55 años, 170 cm, 95 kg | 0.68 | 0.624 | 0.375 g/L | 0.434 g/L (+16 %) |
| Mujer, 30 años, 160 cm, 60 kg | 0.55 | 0.621 | 0.998 g/L | 0.852 g/L (−15 %) |

: Tabla 5.3. Ejemplo trabajado (Figura 5.2)

El error que más pesaba no era *r*, sino el **peso**: con las mismas tres bebidas y *r* = 0.68, un hombre de 90 kg reales calculado con 70 kg sale con un máximo de **0.61 g/L** en lugar de **0.41 g/L** (casi un 50 % más alto: el peso divide la dosis, pero la eliminación por hora no cambia). Por eso `pesoDe` busca el peso más reciente entre las dos fuentes antes de cualquier ajuste de *r*.

![Figura 5.2. Tres bebidas estándar con la simulación de la app para tres perfiles y, punteado, el mismo hombre con la *r* de Watson; la banda muestra β de 0.10 a 0.20 g/L/h. Script: `figuras/fig26_alcohol_widmark.py`.](figuras/fig26_alcohol_widmark.png){width=100%}

### 5.4.5 Cuándo falla

- **Parámetros fijos** (H-4-4, resuelto en parte en 10c): β sigue sin ajustarse a si comiste o no (que según Jones cambia de 0.10–0.15 a 0.15–0.20). Desde la 10c, *r* sale de Watson cuando hay peso, sexo, estatura y edad, y el peso es el más reciente entre las dos fuentes; pero sin ningún peso registrado se siguen usando **70 kg sin avisar** en la pantalla (el diccionario sí lo dice), y la estatura es la de hoy (en un adulto casi no importa).
- **No muestra incertidumbre**: dice «≈ 0.45 g/L estimado», pero el error razonable es de varias décimas.
- **Unidades**: la fórmula de Widmark da gramos por kilo de peso corporal; la app lo presenta como g/L de sangre. La diferencia (por la densidad y el contenido de agua de la sangre) es pequeña frente a las demás incertidumbres, pero existe.
- **Absorción de 30 minutos fija**: con el estómago lleno la absorción es más lenta y el pico más bajo y tardío.
- **Cada día se simulaba por separado** (H-4-5, resuelto en 10e, D.88). Hasta 10e, `alcohol_estimado_max` empezaba en 0 con la primera dosis del día lógico y no arrastraba lo del día anterior: si un episodio cruzaba las 5:00, las bebidas posteriores caían en el día siguiente, que arrancaba de cero. *Ejemplo inventado* (hombre de 75 kg, *r* = 0.68): un episodio de 1:00 a 7:00 con 5 bebidas, repartidas a las 1:00, 2:03, 3:06, 4:09 y 5:12. Las cuatro primeras son del día lógico anterior y la última del siguiente. Antes, el primer día daba 0.536 g/L y el segundo, con una sola bebida desde cero, **0.187 g/L**. Desde 10e, el primer día mira hasta justo antes de las 5:12 y da 0.548 g/L; el segundo incluye las cuatro bebidas anteriores y da **0.660 g/L** (el pico de la noche, a las 5:40). Lo que queda: si el pico llega después de la primera bebida del día siguiente, cuenta en ese día, aunque ahí `alcohol_bebidas` valga 1 (en el ejemplo).
- **Dos máximos que no coincidían** (H-14-5, resuelto en 10e, D.89). Hasta 10e, la lista de episodios (`alcoholMaximo`) miraba la simulación cada 5 minutos y `alcohol_estimado_max` cada 10: si el pico caía entre dos muestras de 10 minutos, el panel daba hasta 0.15/12 ≈ **0.0125 g/L** menos (en la tabla de arriba, 0.595 en lugar de 0.607 g/L). Ahora los dos miran cada 5 minutos desde la primera bebida y, con un episodio por día y sin alcohol en las horas previas, dan la misma cifra. Pueden seguir difiriendo cuando no se cumple eso: el episodio solo cuenta las dosis desde 1 hora antes de su inicio y hasta 3 horas después de su fin, el panel da un solo máximo por día (y, como en el ejemplo anterior, lo corta en la primera dosis del día siguiente), y si mandan los registros de MacroFactor las muestras del panel empiezan en la primera de ellas.
- **Juntar dos fuentes** (H-4-2, resuelto en 10e): la regla por episodio de la sección 5.4.2 puede equivocarse si un registro de MacroFactor de otra ocasión cae dentro del margen de 90 minutos, o si la misma bebida se registró en MacroFactor fuera de él. La dependencia del hilo (H-4-3) se resolvió en la 10c (sección 5.3.5).

## 5.5 Episodios con nivel subjetivo (`Sustancias.nivelEn`)

Para alcohol y cannabis (las sustancias «con nivel»), además de las dosis, puedes decir **qué tan fuerte lo sientes** de 0 a 10, una o varias veces. La curva del episodio se arma así:

- Con lecturas: se unen con líneas rectas, empezando en el nivel de la primera lectura; si el máximo que dijiste después (la mañana siguiente) es mayor que lo leído, se agrega ese máximo **a la mitad del episodio**; si el episodio terminó (o quedó abierto de otro día), baja a 0 al final.
- Sin lecturas, solo con el máximo: un trapecio que sube de 0 al máximo en el primer 35 % del episodio, se queda hasta el 65 % y baja a 0 al final.
- **Fin efectivo** (`finEfectivo`): el que dijiste; si el episodio sigue abierto y empezó hace menos de 18 horas, «ahora»; si quedó abierto de otro día, **4 horas** después del inicio.

Variables: `alcohol_nivel_max`, `cannabis_nivel_max` (el mayor nivel del día) y `alcohol_horas`, `cannabis_horas` (duración). **[Diseño propio]**: son **autorreportes**, no estimaciones fisiológicas; la forma de trapecio y el «4 horas por omisión» son decisiones de la app. Es útil tener ambas medidas: la estimada (Widmark) y la sentida.

## 5.6 Suplementos: tres modelos

`Suplementos` (`data/Lote3.kt`) ofrece tres modelos, elegibles por suplemento y con parámetros editables en la app. Catálogo inicial:

| Suplemento | Dosis de referencia | Modelo | Parámetros |
|---|---|---|---|
| Creatina | 5 g (la reserva se calcula con 3 g, desde 10e) | carga | 28 días para llenar, 35 para vaciar |
| Vitamina D | 2000 UI | vida media | 360 h (15 días) |
| Magnesio | 300 mg | vida media | 12 h |
| Melatonina | 1 mg | vida media | 1 h (sin `nivel`, desde 10e) |
| Omega-3 | 1000 mg | vida media | 60 h |
| Probióticos | 1 porción | exposición | — |

: Tabla 5.4. Suplementos: tres modelos

Para todos se guardan, por día, `supl_<id>_dosis` (lo anotado) y `supl_<id>_tomado` (0/1), desde la primera toma.

Una toma olvidada se puede anotar después, con su hora real («¿Cuándo la tomaste?», o «Suplementos de este día» en la hoja de la noche), y una toma de más se puede quitar. Los niveles se recalculan con lo corregido. Cada toma guarda `ts` (cuándo se tomó) y `ts_creado` (cuándo se anotó), así que en R se ve cuáles se anotaron tarde (`vm_retraso`). Esto es desde la entrega N2 de la revisión de navegación (H-NAV-1).

### 5.6.1 Carga: la creatina (`Suplementos.carga`)

La creatina no actúa como una pastilla que sube y baja en horas: se **acumula** en el músculo durante semanas. El modelo representa esa reserva de 0 a 100 %:

$$s = 1 - 0.05^{1/\text{carga\_días}}, \qquad b = 1 - 0.05^{1/\text{lavado\_días}}\qquad\qquad (5.3)$$

- Un día con dosis *d*: $\text{nivel} \mathrel{+}= \left(1 - (1-s)^{d/\text{ref}}\right)\cdot(100 - \text{nivel})$: se llena una fracción de **lo que falta**. *ref* es la **dosis de referencia** (`Suplementos.dosisReferencia`): desde la entrega 10e (D.98), **3 g en la creatina**, la dosis del estudio del que salen los 28 días, aunque en el catálogo tenga 5 g; en los demás suplementos con este modelo, la dosis que pusiste. Antes de 10e, *ref* era siempre la dosis del suplemento (5 g en la creatina).
- Un día sin dosis: $\text{nivel} \mathrel{\times}= (1 - b)$: se vacía una fracción de **lo que hay**.

Con 28 y 35 días, *s* = 0.1015 y *b* = 0.0820. El diseño garantiza dos cosas: con la dosis de referencia todos los días, a los **28 días** queda lleno al **95 %** (falta el 5 %); y sin tomarla, a los **35 días** queda el **5 %** de lo que había. Con una dosis diaria *d* distinta, el 95 % llega a los 28 · ref/*d* días. Las variables son `supl_creatina_nivel` y `creatina_saturacion_pct`, redondeadas a una décima.

**Ejemplo (Figura 5.3).**

- 3 g diarios (la referencia): día 28 → **95.0 %**.
- 5 g diarios (la dosis del catálogo, 5/3 de la referencia): llega al 95 % a los 28 × 3/5 = 16.8 días, es decir, el **día 17** (95.2 %); día 28 → **99.3 %**.
- Carga de 20 g × 6 días (20/3 ≈ 6.7 veces la referencia): cada día se llena 1 − (1 − 0.1015)^(20/3) = 51.0 % de lo que falta; al día 6 → **98.6 %**.
- Tras 8 semanas a 5 g (100.0 %) se deja: 30 días después → **7.7 %**; 35 días → **5.0 %**.

Hasta 10e, con la referencia de 5 g, las mismas cuentas daban 83.4 % con 3 g al día 28, 95.0 % con 5 g al día 28 y 92.3 % con la carga al día 6.

**De dónde salen los parámetros.** Hultman et al. (1996) midieron la creatina del músculo en 31 hombres: aumentó alrededor de un **20 %** tras 6 días a 20 g diarios; se mantuvo con 2 g diarios; **30 días después de dejarla ya no difería** del valor inicial; y con **3 g diarios** se alcanzó el mismo 20 % de forma gradual en **28 días**. **[Aproximación]**: los 28 días del modelo vienen de ese protocolo lento, y desde 10e con su misma dosis (3 g); el vaciado de 35 días es compatible con los 30 días medidos (el modelo dice 7.7 % a los 30 días). Que con 5 g llegue al 95 % en 17 días, o con 20 g en 5, sale de la forma del modelo (más dosis llena proporcionalmente más rápido), no de una medición: el estudio midió el +20 % tras 6 días a 20 g, pero no a qué día llega con 5 g **[Diseño propio]**.

![Figura 5.3. La reserva de creatina del modelo con tres formas de tomarla y al dejarla, con la dosis de referencia de 3 g (desde 10e). Script: `figuras/fig27_creatina.py`.](figuras/fig27_creatina.png){width=100%}

**Cuándo falla** (H-4-6):

- **La dosis de referencia define la velocidad** (resuelto en 10e, D.98). Hultman et al. llenaron la reserva con **3 g** en 28 días; con la referencia de 5 g que usaba la app, 3 g diarios daban solo 83 % al día 28: el modelo se llenaba más lento que el estudio en que se basa. Desde 10e la referencia es 3 g. Queda fija en el código: cambiar la dosis de la creatina en la app ya no cambia la velocidad del llenado (cambiar los días de carga o de lavado, sí).
- **El 100 % es del modelo**, no una concentración: no considera la creatina de la dieta (carne, pescado), el punto de partida de cada persona ni que hay personas que casi no responden a la suplementación.
- **Interacción con los experimentos**: la plantilla de experimento de creatina usa 28 días de lavado, que con 8 semanas eliminaba todos los días con creatina (capítulo 8, H-7-1). Desde la entrega 10c dura 12 semanas (6 sin la acción y luego 6 con ella; el lavado se come los primeros 28 días con ella): quedan 14 días con la acción y 42 sin ella (`Experimentos.PLANTILLAS`, `data/Lote3.kt`). Desde 10e (D.96), en esa plantilla los días sin respuesta a «¿Lo hiciste hoy?» se completan con `supl_creatina_tomado` (capítulo 8). Desde 10f (D.114; H-7-14), la plantilla mide `fuerza_ref_kg`, la fuerza estimada en un ejercicio fijo (sección 5.9), en vez de `volumen_kg`: el volumen del día sube o baja según qué entrenaste, y eso no tiene que ver con la creatina. Su nota advierte que, si vas subiendo de peso, la cifra sube sola, así que una mejora no prueba que fue la creatina. Un experimento creado antes con la plantilla sigue midiendo `volumen_kg`.

### 5.6.2 Vida media (`Suplementos.cantidadEn`)

La cantidad «en el cuerpo» en un instante es la suma de cada toma multiplicada por 0.5^(horas transcurridas / vida media), **sin fase de absorción**. La variable `supl_<id>_nivel` se evalúa **al final del día lógico** (las 5:00 del día siguiente). Desde la entrega 10e (D.98), solo existe si la vida media es de **6 horas o más** (`Suplementos.VIDA_MEDIA_MIN_NIVEL`); con menos, a las 5:00 casi no queda nada. La regla mira la vida media que tenga el suplemento: con la de omisión, la melatonina (1 h) se queda sin `nivel`; si se la subieras a 6 h, volvería. El umbral de 6 h es **[Diseño propio]**: con esa vida media, una toma a las 23:00 conserva la mitad a las 5:00.

**Los parámetros:**

- **Vitamina D, 360 h (15 días)**: coincide con la vida media de **25(OH)D**, la forma que circula en sangre, de unos 15 días; la vitamina D almacenada en la grasa tiene una vida media de unos 2 meses (Jones, 2008). **[Aproximación]**: el modelo suma «UI en el cuerpo», que no es lo que mide un análisis de sangre. Con 2000 UI diarias, el modelo se estabiliza en 2000 / (1 − 0.5^(24/360)) ≈ **44 000 UI**: una cifra sin interpretación fisiológica directa, útil solo para ver la tendencia.
- **Melatonina, 1 h**: la vida media de la melatonina oral va de unos 28 a 126 minutos según la revisión de Harpsøe et al. (2015). **[Validado]** como orden de magnitud. Pero si la tomas a las 23:00, a las 5:00 han pasado 6 vidas medias y queda el 1.6 %: **`supl_melatonina_nivel` era casi siempre ≈ 0** y no servía para el análisis (H-4-7, resuelto en parte en 10e): desde 10e ya no existe, y la variable útil es `supl_melatonina_tomado`. Lo demás de H-4-7 (las fuentes del magnesio y el omega-3, y las unidades de la vitamina D) queda solo documentado aquí.
- **Magnesio, 12 h; omega-3, 60 h**: no encontré en esta sesión una fuente que respalde esos valores **[sin verificar]**. El magnesio no se comporta como un fármaco con una vida media única (el cuerpo regula sus reservas), y los omega-3 se incorporan a las membranas durante semanas.

### 5.6.3 Exposición: los probióticos (`Suplementos.probioticosEstimados`)

No hay farmacocinética: solo se cuentan **porciones**. A lo que anotes se suma **una porción por cada alimento de MacroFactor cuyo nombre contenga** alguna de las palabras clave que configures (por ejemplo, «yogur» o «kéfir»; comparación sin acentos ni mayúsculas). Variables: `probioticos_porciones` (anotadas + estimadas) y `probioticos_estimado` (solo las estimadas; desde la entrega 10c, vacía los días sin bitácora de MacroFactor, porque esos días no se sabe, D.63). **[Diseño propio]**: es una cuenta de exposición, sin relación con las unidades formadoras de colonias ni con si el producto tiene cultivos vivos (H-4-8; lo que proponía, dejar vacío `probioticos_estimado` sin bitácora, se hizo en la 10c).

## 5.7 Ritmo de sueño y jet lag social (`Analisis.ritmo`)

### 5.7.1 Qué hace

La herramienta «Ritmo» de Análisis muestra a qué hora te duermes y despiertas en promedio, cuánto duermes antes de un día laboral y antes de un día libre, la **regularidad** y el **jet lag social**, con una barra por noche (Figura 5.4). No crea variables del panel; el panel tiene aparte `sueno_inicio_h` y `sueno_fin_h` del reloj y, desde la entrega 10c, el panel exportado lleva `festivo` y `dia_libre` (sección 5.7.2).

### 5.7.2 Cómo lo hace exactamente

0. **Solo las noches recientes** (desde la entrega 10c, D.69): las que terminan en los **últimos 56 días** hasta hoy (`Analisis.recientes`; la noche cuenta por el día en que despiertas). Todos los umbrales que siguen se aplican dentro de esa ventana. En R, `vm_ritmo(dias = 56)` aplica la misma ventana con el último día de `vm_salud` como referencia (`dias = Inf` para toda la historia), pero solo con las noches del reloj: no usa las del teléfono ni pide un mínimo de noches.
1. **Noches del reloj**: inicio y fin del sueño en hora decimal (`Exportar.horaDecimal`; desde 10f, con el desfase guardado en cada marca, es decir, en la hora local de donde dormiste); si el inicio es antes de las 12:00, se le suman 24 (dormirte a la 1:00 cuenta como 25.0, para promediarlo bien con las 23:00); al fin se le suman 24 siempre. La noche se guarda con **el día en que despiertas**.
2. **Noches del teléfono** (si faltan del reloj): inicio = **último uso del teléfono** del día lógico (debe ser **a las 18:00 o después**; uno de madrugada, antes de las 12:00, cuenta como 24 + la hora y también vale); fin = **primer uso** del día siguiente (a las **4:00 o después**).
3. Si hay **7 o más** noches del reloj, se usan solo esas; si no, se completan con las del teléfono en las fechas sin reloj. La pantalla lo dice: sin ninguna noche del reloj, «Sin datos del reloj en las últimas 8 semanas…»; con 1 a 6, «Solo *N* noches del reloj en las últimas 8 semanas: las demás se aproximan con el último uso del teléfono y el primero del día siguiente» (fuente «mezcla»). Las noches del reloj se cuentan antes del filtro del paso 4.
4. Se descartan las noches de menos de **2 h** o más de **16 h**. Hacen falta al menos **5** noches.
5. **Días libres** (`Fechas.esDiaLibre`, la única regla de la app desde la 10c): una noche es libre si el día en que despiertas es **sábado, domingo** o, si en Conocerte dijiste que vives en México (`pais = "MX"`), un **festivo** de la Ley Federal del Trabajo, art. 74 (`Fechas.esFestivoMx`): 1 de enero; primer lunes de febrero; tercer lunes de marzo; 1 de mayo; 16 de septiembre; tercer lunes de noviembre; 25 de diciembre; y el día de la transmisión del Poder Ejecutivo Federal, cada seis años (años 2024, 2030… el **1 de octubre**; hasta 2018, el **1 de diciembre**). **Antes de 2006**, los de febrero, marzo y noviembre iban en su fecha fija (5 de febrero, 21 de marzo y 20 de noviembre). Se omite la jornada electoral (cae en domingo) y no se cuentan Jueves ni Viernes Santo, que no están en la ley. Las demás noches son laborales. La misma regla da las columnas `festivo` (vacía si no vives en México) y `dia_libre` del panel exportado, y R la lee de ahí.
6. **Punto medio** de cada noche = (inicio + fin)/2. **MSW** = promedio de los puntos medios laborales; **MSF** = de los libres (cada uno con al menos 2 noches).
7. **Jet lag social** = |MSF − MSW|.
8. **Regularidad** = desviación estándar de la hora de dormirte (en la pantalla, «la hora de dormir varía ±*N* min»).
9. Duraciones medias en laborales y libres.

### 5.7.3 Razonamiento científico

Wittmann et al. (2006) definieron el jet lag social como la discrepancia entre el horario biológico y el social: las personas de cronotipo tardío duermen muy distinto en días laborales y libres, acumulan deuda de sueño entre semana y la compensan en el fin de semana. Lo midieron con el cuestionario de cronotipo de Múnich (MCTQ) y lo asociaron con peor bienestar y más consumo de estimulantes, sobre todo en jóvenes. Roenneberg et al. (2012), en un estudio poblacional grande, encontraron que el jet lag social se asocia con un índice de masa corporal más alto, más allá de la duración del sueño. **[Validado]** como definición y como variable asociada a la salud.

La app dice en la pantalla que «más de 1 hora se ha asociado con peor ánimo y salud». La asociación está respaldada por esos estudios; **el umbral de 1 hora no lo pude verificar** en esta sesión **[sin verificar]**.

Sobre la **regularidad**: existen medidas específicas, como el **Índice de Regularidad del Sueño** (SRI) de Phillips et al. (2017), que compara, minuto a minuto, si estabas dormido o despierto a la misma hora en días consecutivos; en estudiantes, la irregularidad se asoció con un ritmo circadiano retrasado y peor rendimiento académico. La app usa algo más simple: la desviación estándar de la hora de dormirte. **[Aproximación]**.

**Las noches del teléfono** son un proxy: usar el teléfono por última vez no es dormirte. Cuttone et al. (2017) mostraron que, con un modelo estadístico de los eventos de encender y apagar la pantalla, se puede estimar el sueño con buena exactitud (0.89 frente a pulseras de sueño); la app usa una regla mucho más simple. **[Aproximación]**.

### 5.7.4 Ejemplo trabajado (Figura 5.4)

Tres semanas inventadas. Entre semana te duermes hacia las **00:15** y despiertas a las **06:45**; las noches del viernes y el sábado, hacia las **01:45** y las **10:15** (con variación al azar).

- **MSW** = **03:26**; **MSF** = **06:03** → jet lag social = **2.62 h**.
- Duración media: **6.7 h** entre semana y **8.4 h** en fin de semana: la deuda que se paga el sábado.
- Regularidad: la hora de dormir «varía **±53 min**». Ojo: casi toda esa variación es la diferencia **sistemática** entre semana y fin de semana; la desviación estándar no distingue «me duermo cada día a una hora distinta» de «me duermo siempre igual entre semana y siempre igual el fin de semana, pero distinto».

![Figura 5.4. 21 noches sintéticas dibujadas como en la app, con los puntos medios y el jet lag social. Script: `figuras/fig28_jetlag_social.py`.](figuras/fig28_jetlag_social.png){width=100%}

### 5.7.5 Cuándo falla (H-4-9 y H-4-10)

- **Días libres por calendario.** Desde la 10c cuentan los festivos de México, pero no el horario de Conocerte, las vacaciones ni los turnos. Si trabajas un sábado o tienes vacaciones, el cálculo los clasifica mal. Los festivos mueven poco el jet lag social (unas 7 noches laborales al año, el 2.8 %); las vacaciones, que no arregla ningún calendario fijo, pesan mucho más. Las fechas anteriores a 2006 y a 2024 siguen las reglas de entonces: que los «lunes» empezaran con la reforma de 2006 y que antes de 2024 la transmisión fuera el 1 de diciembre se dedujo de las notas de reforma del artículo, no del texto de los decretos **[sin verificar]**.
- **Toda la historia junta** (esta parte de H-4-9 se resolvió en la 10c; el hallazgo queda resuelto en parte): hasta la 10c, los promedios usaban **todas** las noches registradas aunque la gráfica mostrara solo las últimas 30. Ahora usan las de los últimos 56 días. *Ejemplo inventado:* 60 noches de hace meses con 3 h de jet lag social y 60 recientes con 1 h: con toda la historia salía cerca de **2 h**; con la ventana, **1 h**, que es tu presente.
- **El valor absoluto** pierde la dirección (si en días libres duermes antes o después), aunque casi siempre es después.
- **Reloj y teléfono mezclados** cuando hay menos de 7 noches del reloj: dos medidas distintas en un mismo promedio. Desde la 10c la pantalla lo avisa, pero sigue mezclándolas (H-4-10, resuelto en parte).
- **Hora de dormir con dos cortes distintos** (H-5-15, resuelto en 10c): ahora los reportes (`Periodo`) usan la misma regla que aquí y el panel (`Exportar.horaDecimal`: antes de las 12:00 suma 24).
- **Noches de un viaje** (H-1-7, resuelto en 10f, D.109). Hasta la v91, `horaDecimal` pasaba cada hora a la zona que el teléfono tiene hoy. *Ejemplo inventado:* en un viaje a Madrid (UTC+2) duermes de 23:30 a 07:30, hora de allá. Ya de vuelta en UTC−6, esa noche se leía de 15:30 a 23:30: el inicio quedaba en 15.5 (no suma 24, porque es después de las 12:00) y el fin en 23.5 + 24 = 47.5, así que la noche «duraba» 32 h y el filtro de 2 a 16 h la descartaba. Desde 10f se lee con su propio desfase: 23.5 a 31.5, 8 h. Lo mismo vale para `sueno_inicio_h` y `sueno_fin_h` del panel. Las noches de un viaje cuentan con la hora local de allá, que es la del horario social; los primeros días el cuerpo puede seguir en la hora de casa, y eso el cálculo no lo ve. `cambio_horario` (capítulo 4, sección 4.3) marca los días en que cambió la hora.

## 5.8 Aire libre y kilómetros

Ambas están descritas con detalle en el capítulo 4. En resumen:

- **`aire_libre_min`** = el mayor entre los minutos con luz de **1000 lux o más** en el sensor del teléfono (`luz_alta_min`) y los minutos caminando, corriendo o en bici. Es un proxy que **subestima** (el teléfono en el bolsillo no ve la luz) y que no distingue un parque de una calle. **[Aproximación]**.
- **`distancia_km`** suma la distancia **en línea recta** entre puntos de ubicación consecutivos, solo si pasaron entre 1 s y 2 h, la velocidad implícita es menor a 70 m/s y la distancia es mayor a 30 m. Es una **cota inferior** del recorrido real (H-3-5). Desde la entrega 10c, un día con ubicaciones y sin ningún tramo que cumpla esas condiciones vale **0 km** en lugar de quedar vacío (D.63). **[Aproximación]**.

## 5.9 Otras variables derivadas

- **Fase lunar** (`Luna`, `data/Calculos.kt`): edad de la luna a partir de una luna nueva de referencia (6 ene 2000, 18:14 UTC) y del **mes sinódico medio** de 29.530588853 días, evaluada al mediodía local; iluminación = (1 − cos 2π*f*)/2 (variable `luna_ilum`). **[Validado]** como aproximación astronómica: el mes real varía alrededor de ese promedio, así que la fase puede desviarse algunas horas; no lo cuantifiqué **[sin verificar]**.
- **Enfoque y siestas** (`Enfoque`, `data/Enfoque.kt`): el cronómetro guarda cada bloque como un tramo **sin contar las pausas**, redondeado a minutos; los bloques de **menos de 1 minuto** no se guardan. Con meta y descansos, la meta se reparte en bloques iguales (2 h con 3 descansos = 4 bloques de 30 min). Los tramos anotados después con hora antes de las 5:00 se asignan a la madrugada del día lógico. Variables: `enfoque_min`, `enfoque_<tipo>_min`, `siesta`, `siesta_min`. Son **mediciones** (de tu cronómetro), no modelos.
- **Repetición máxima estimada** (1RM): peso × (1 + repeticiones/30), la **fórmula de Epley** (con 1 repetición, el peso mismo desde el 5 nov 2026, D.156), una de las habituales para estimar el máximo; no verifiqué su fuente original **[sin verificar]**. Desde la entrega 10c (D.72) hay **un solo criterio** en toda la app, `Macrofactor.e1rm`: series de 1 a **12** repeticiones, sin calentamientos, redondeada a una décima. Lo usan la exportación, `vm_fuerza()` de R, Fuerza, Totales, los reportes (`Periodo`) y Tus apps. Antes, los reportes aceptaban hasta 15 repeticiones con calentamientos y Tus apps no tenía límite (H-4-13). *Ejemplo:* series de 100 kg × 15, 120 kg × 5 y un calentamiento de 130 kg × 8: antes el reporte daba 130 × (1 + 8/30) = **164.7 kg** (el calentamiento); ahora, 120 × (1 + 5/30) = **140 kg**, como en R.
- **Fuerza de referencia** (`fuerza_ref_kg`, desde 10f; D.114, H-7-14): la mayor 1RM estimada del día (`Macrofactor.e1rm`, con sus mismas reglas) entre las series de **un solo ejercicio fijo**, el press inclinado con mancuernas (`Macrofactor.fuerzaReferencia`). Vacía los días sin ese ejercicio. `Macrofactor.esReferencia` lo reconoce por el nombre, en inglés o en español: el nombre simplificado (`Texto.slug`: minúsculas, sin acentos) debe contener «inclin»; «mancuern», «dumbbell» o la palabra «db», y «press», «banc» o «bench». Así entran «Incline Dumbbell Press», «Incline Dumbbell Bench Press» y «Press inclinado con mancuernas», y no «Incline Barbell Bench Press» ni «Dumbbell Bench Press». Se calcula al importar MacroFactor y, una sola vez, para lo ya importado (`Repo.completarFuerzaReferencia`). Va en el panel (grupo «Cuerpo»), en `vm_entrenos.csv` y en `vm_panel.csv`, y `vm_series.csv` marca las series del ejercicio (`referencia`). *Ejemplo* (inventado): 30 kg × 8, 32 kg × 5 y un calentamiento de 40 kg × 3 dan 30 × (1 + 8/30) = 38.0 y 32 × (1 + 5/30) = 37.3; el calentamiento no cuenta, y `fuerza_ref_kg` = **38.0 kg**. **[Aproximación]** la 1RM de Epley; **[Diseño propio]** reconocer el ejercicio por su nombre. *Cuándo falla.* (a) Con un nombre sin esas palabras, el día queda vacío; el nombre exacto con que MacroFactor exporta este ejercicio no se comprobó. (b) Al revés, un ejercicio que no es press pero se llama con «banco inclinado» y «mancuernas» (por ejemplo, «Aperturas en banco inclinado con mancuernas») también cuenta, porque «banc» basta. (c) Solo hay dato los días en que hiciste el ejercicio: si lo haces una vez por semana, un experimento de creatina tiene pocos días medidos en cada condición (capítulo 8).
- **Grados de escalada** (`Grados`): los grados de Fontainebleau se convierten a la escala V con una tabla fija (6A = V3, 7A = V6…) y los de YDS a número (5.10a = 10, 5.10b = 10.25…). **[Aproximación]**: las tablas de equivalencia publicadas no coinciden entre sí.

## 5.10 Estimaciones, no mediciones

Todo este capítulo comparte tres advertencias:

1. **Parámetros promedio.** Los modelos usan valores promedio de la literatura; tu cuerpo puede estar lejos de ese promedio (la vida media de la cafeína varía entre 1.5 y 9.5 h). Los resultados sirven sobre todo para **compararte contigo mismo** con los mismos parámetros.
2. **Sin incertidumbre visible.** Ninguna pantalla ni variable dice cuánto podría equivocarse la cifra.
3. **Parámetros que reescriben el pasado** (H-4-11, resuelto en parte en 10e, D.99). La vida media de la cafeína y los parámetros de los suplementos se pueden cambiar, y al hacerlo **todas** las variables derivadas pasadas se recalculan, sin guardar qué parámetro se usó ni cuándo cambió. Desde 10e, el diccionario de cada exportación (`Diccionario.conParametros`) agrega a la descripción de `cafeina_al_dormir_mg`, `creatina_saturacion_pct` y cada `supl_<id>_nivel` los parámetros vigentes al exportar: por ejemplo, «· al exportar: vida media de la cafeína de 5 h (si lo cambias, se recalcula toda la historia)», o, en la creatina, «llega al 95 % en 28 días con 3 g al día y se vacía en 35 días». Así se sabe con qué se calculó cada exportación, pero no qué valía antes: dos exportaciones con parámetros distintos siguen sin ser comparables entre sí. Desde 10f (D.115; H-1-8, resuelto), `vm_diccionario.csv` dice además, en `desde_version` y `cambio`, desde qué versión rige la definición de cada columna y qué cambió entonces; eso cubre los cambios de definición, no los de parámetros.

Cuando una variable estimada aparece relacionada con otra en Explorar, la relación es tan buena como el modelo: por ejemplo, `cafeina_al_dormir_mg` es, en la práctica, una **combinación de la cantidad y la hora** de tus cafés. Si se relaciona con dormir peor, eso dice que «tomar más o más tarde» se asocia con dormir peor; no que tu sangre tuviera exactamente esos miligramos.

## 5.11 Resumen de decisiones

| Decisión | Clasificación | Respaldo |
|---|---|---|
| Cafeína: absorción en 45 min, vida media 5 h por omisión | Aproximación | Institute of Medicine (2001); Nehlig (2018) |
| Cafeína: vida media ajustable | Diseño propio (bien fundado) | Nehlig (2018) sobre la variabilidad |
| Alcohol: Widmark con β = 0.15 g/L/h | Validado como promedio | Jones (2010) |
| *r* de Watson (agua corporal / (0.80 · peso), acotada 0.45–0.95) con peso real, sexo, estatura y edad | Aproximación (mejor que la fija) | Watson et al. (1980, 1981); Searle (2015); el 0.80 [sin verificar] |
| *r* fija 0.68 / 0.55 / 0.62 si falta algo; peso más reciente entre báscula y MacroFactor; 70 kg solo sin pesos | Aproximación / Diseño propio | Searle (2015) sobre la incertidumbre |
| Bebida estándar de 14 g | Validado (definición de EE. UU.) | NIAAA (s. f.) |
| Nivel subjetivo con interpolación y trapecio | Diseño propio | — |
| Cafeína y alcohol de MacroFactor y de la app juntados toma por toma: 90 min; en la cafeína, de la mitad al doble; en el alcohol, la fuente con más gramos (desde 10e) | Diseño propio | — |
| Máximo de alcohol del día: dosis de las 24 h previas; de la primera dosis a 14 h o a la primera del día siguiente; muestras cada 5 min (desde 10e) | Diseño propio | — |
| Creatina: 28 días para llenar con 3 g al día (desde 10e), 35 para vaciar | Aproximación | Hultman et al. (1996) |
| Sin `supl_<id>_nivel` con vida media de menos de 6 h (desde 10e) | Diseño propio | — |
| Vitamina D 15 días; melatonina 1 h | Aproximación / Validado (orden de magnitud) | Jones (2008); Harpsøe et al. (2015) |
| Magnesio 12 h; omega-3 60 h | Diseño propio | [sin verificar] |
| Probióticos por palabras clave | Diseño propio | — |
| Jet lag social | Validado (definición) | Wittmann et al. (2006); Roenneberg et al. (2012) |
| Ritmo con las noches de los últimos 56 días | Diseño propio | — |
| Días libres = fin de semana o festivo de México | Validado (calendario legal) / Aproximación (sin vacaciones ni turnos) | Ley Federal del Trabajo, art. 74 |
| 1RM con Epley, 1 a 12 repeticiones, sin calentamientos | Aproximación | [sin verificar] |
| Fuerza de referencia: un ejercicio fijo (press inclinado con mancuernas) reconocido por su nombre (desde 10f) | Diseño propio | — |
| Regularidad = desviación estándar de la hora de dormir | Aproximación | Phillips et al. (2017), el SRI como alternativa |
| Noches por uso del teléfono | Aproximación | Cuttone et al. (2017) sobre lo que se puede lograr |
| Aire libre, km | Aproximación | capítulo 4 |
| Fase lunar con el mes sinódico medio | Validado (aproximación astronómica) | — |

: Tabla 5.5. Resumen de decisiones

## 5.12 Resumen del capítulo

1. La cafeína y el alcohol se estiman con modelos farmacocinéticos sencillos y **parámetros promedio bien elegidos** (5 h de vida media y 45 min de absorción; β = 0.15 g/L/h), pero la variación entre personas es enorme: la misma tarde de café puede dejar de 12 a 106 mg al dormir según la vida media.
2. Desde la entrega 10c, el alcohol usa el peso más reciente entre la báscula y MacroFactor y, cuando tiene los datos, la *r* de Watson; sin pesos sigue usando 70 kg y no muestra su incertidumbre (H-4-4, en parte).
3. Desde la 10e, lo de MacroFactor y lo anotado en la app se juntan toma por toma, en vez de ignorar la app los días en que MacroFactor tenía algo (H-4-2); el máximo de alcohol del día arrastra lo de la noche anterior (H-4-5) y se lee cada 5 minutos, como la lista de episodios; y la hora de la última cafeína sale de las mismas dosis que los totales (H-4-12, en parte). Desde la 10c, las cifras ya no cambian según la pantalla (H-4-3) y las tomas de MacroFactor sin hora ya no se pierden.
4. El modelo de creatina se basa en Hultman et al. (1996) y, desde la 10e, usa su dosis de 3 g como referencia: 3 g al día llenan al 95 % en 28 días, y 5 g en unos 17 (H-4-6). La melatonina, cuyo nivel al final del día era casi siempre 0, ya no tiene `nivel` (H-4-7, en parte). Desde la 10f, la plantilla de experimento de la creatina mide la fuerza en un ejercicio fijo (`fuerza_ref_kg`, la 1RM de Epley en press inclinado con mancuernas) en vez del volumen del día (H-7-14).
5. El jet lag social está bien definido. Desde la 10c usa las noches de los últimos 56 días y una sola regla de día libre con los festivos de México (H-4-9 y H-4-10, resueltos en parte); las vacaciones siguen sin contarse y la regularidad es una desviación estándar simple. Desde la 10f, cada hora se lee en la hora local de donde estabas, y las noches de un viaje ya no se descartan por «durar» 32 horas (H-1-7).
6. Todas estas cifras son **estimaciones**: sirven para compararte contigo mismo con parámetros fijos, y cambiar un parámetro reescribe el pasado (H-4-11, en parte: desde la 10e el diccionario dice con qué parámetros se exportó).
7. El módulo 4 identificó **13 hallazgos** (H-4-1 a H-4-13) y no cambió código. Después, la entrega 10c resolvió H-4-3, H-4-8 y H-4-13, y en parte H-4-4, H-4-9 y H-4-10 (apéndice D, D.63 y D.69 a D.72); la entrega 10e resolvió H-4-2, H-4-5 y H-4-6, y en parte H-4-7, H-4-11 y H-4-12 (D.88 a D.92, D.98 y D.99), y H-4-1 quedó resuelto por documentación (este capítulo). Quedan **7 resueltos** (H-4-1, H-4-2, H-4-3, H-4-5, H-4-6, H-4-8 y H-4-13), **6 resueltos en parte** (H-4-4, H-4-7, H-4-9, H-4-10, H-4-11 y H-4-12) y ninguno pendiente.


# Capítulo 6. Análisis estadístico

> **Para qué sirve este capítulo.** Explica **cómo la app pasa de miles de registros a frases como «cuando duermes más, tu ánimo tiende a subir»**: cómo arma la tabla de un renglón por día (el *panel*), cómo compara cada variable con cada otra, qué hace para no dejarse engañar por las tendencias, por los días que se parecen a sus vecinos y por el azar de revisar miles de parejas, y qué hacen las demás herramientas de análisis (modelo múltiple, dinámica emocional, palabras, días atípicos…). Al final compara **función por función** lo que hace la app con lo que hace el script de R que viaja en cada exportación. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10a…10d» y está en el Apéndice D (la comparación con R de 6.3.4, 6.9.3 y 6.22 describe el script desde la entrega 10d). Está puesto al día también con la **entrega 10e** (D.88 a D.108, sin cambio de esquema: `SCHEMA_V` siguió en 14), que se marca «desde 10e», y con la **entrega 10f** (D.109 a D.118, v92, `SCHEMA_V = 15`), que se marca «desde 10f». **Los ejemplos numéricos son inventados.**
>
> **Cómo leerlo.** Está escrito para entenderse sin saber estadística, pero sin recortar la precisión. Las cifras, umbrales y fórmulas se dan tal como están en el código, con el nombre de la función de origen; las fórmulas completas están reunidas en el **Apéndice B** y las funciones de R en el **Apéndice C**. Cada decisión se marca como **[Validado]** (respaldada por literatura o un estándar), **[Aproximación]** (razonable, con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app, sin estándar detrás). Los problemas encontrados se numeran H-5-n (por el módulo 5, que escribió el capítulo) y se detallan en `hallazgos.md`, con su estado.
>
> **Archivos leídos.** `ciencia/Estadistica.kt` (completo), `ciencia/Analisis.kt`, `ciencia/Dinamicas.kt`, `ciencia/Periodo.kt`, `ciencia/TusNumeros.kt`, `ciencia/Totales.kt`, `ui/pantallas/Explorar.kt`, `ui/pantallas/Analisis.kt`, `ui/Relacion.kt` (la tarjeta de relación), la parte de `data/Exportar.kt` que arma `vm_panel.csv` y todas las funciones de análisis de `app/src/main/assets/vestigium.R`.

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

: Tabla 6.1. Mini-glosario

## 6.2 Mapa: qué análisis hay y con qué método

La pantalla **Análisis** (`ui/pantallas/Explorar.kt`) tiene, de arriba abajo (desde la entrega N4 de la revisión de navegación): los experimentos, «Las relaciones más marcadas» (las 5 primeras, con «ver las siguientes»), las herramientas en fichas y, plegadas, «Cruzar dos variables», la cobertura, «¿Qué tan bien te conoces?», «A qué hora aparecen tus emociones», «Ánimo según con quién estás», listas de personas, temas y lugares, y «Antes y después». Desde ahí se abren ocho **herramientas** (`ui/pantallas/Analisis.kt`). La tabla resume qué hace cada una y qué protecciones estadísticas aplica.

| Dónde | Función | Coeficiente o prueba | ¿Quita tendencia? | ¿Corrige por días parecidos? | ¿Corrige por muchas pruebas? | En R |
|---|---|---|---|---|---|---|
| Las relaciones más marcadas | `Estadistica.relaciones` | Pearson | Sí (interruptor, encendido por omisión) | Sí (nEf) | Sí (BH, q < 0.10) | `vm_relaciones` (en R, además, `sin_semana`, desde 10e) |
| … por semana | `Estadistica.semanal` + `relaciones` | Pearson sobre promedios semanales | Sí (±35 días) | Sí (nEf semanal) | Sí | — |
| Cruzar dos variables | `Estadistica.pares`, `pearson`, `spearman`, `correlacionEf`, `sinTendencia` | Pearson y Spearman; intervalo sobre Pearson con nEf; r sin tendencia al lado | Se muestra al lado (r sin tendencia) | Sí (nEf, desde 10c) | No aplica (una sola pareja) | `vm_cruce` (Pearson con nEf y Spearman, desde 10e) |
| Todo lo que se mueve con algo | `Analisis.factores` | Pearson con nEf (continuas); diferencia de medias con t de Welch y factor AR(1) (0/1) | Sí (interruptor, el mismo de la lista; desde 10c) | Sí (desde 10c) | Sí (BH, q < 0.1) | `vm_factores` (las mismas correcciones desde 10d) |
| Modelo múltiple | `Analisis.modelo` | Mínimos cuadrados estandarizados; t con n − k − 1; R² ajustado | No | En parte (el día anterior como predictor) | No | `vm_modelo`, `vm_modelo_ar1` |
| Tendencia | `Analisis.movil`, `Analisis.cambio` | Medias móviles; diferencia de medias con t de Welch y factor AR(1) | — | Sí (desde 10c) | No | `vm_tendencia` |
| Día de la semana | `Analisis.porDiaSemana` | Media ± t(n − 1) · EE, con 5 días o más | No | No | No | `vm_semana` |
| Dinámica emocional | `Analisis.dinamica` | DE, MSSD, autocorrelación | No | — | — | `vm_dinamica` |
| Ritmo de sueño | `Analisis.ritmo` | Promedios de horas | — | — | — | `vm_ritmo` |
| Palabras del diario | `Analisis.palabras` | Diferencia de medias, sin prueba | No | No | No | `vm_palabras` |
| Días atípicos | `Analisis.atipicos` | z robusto (mediana y MAD) | No | — | — | `vm_atipicos` |
| Antes y después | `Estadistica.diferenciaEnDias` (en `EtapasDeVida`) | Diferencia de medias con t de Welch y factor AR(1) | No | Sí (desde 10c) | No | — |

: Tabla 6.2. Mapa: qué análisis hay y con qué método

Lo importante de la tabla: **«Las relaciones más marcadas» y, desde la entrega 10c, «Todo lo que se mueve con algo» aplican las tres protecciones a la vez** (tendencia, días parecidos y muchas pruebas). Hasta 10c, «Todo lo que se mueve» no quitaba tendencia ni contaba los días parecidos, así que la misma pareja podía verse «podría ser azar» en la lista y con ✓ un toque después (H-5-3, H-5-4; secciones 6.12 y 6.13). Las demás herramientas son descriptivas o aplican una o dos; la app lo dice: «Pistas para entenderte. Ninguna prueba causas».

## 6.3 El panel diario: la tabla de la que sale todo

### 6.3.1 Qué es

`Estadistica.panel()` devuelve un mapa `fecha → (variable → valor)`. Cada **fecha** es un día lógico (de 5:00 a 4:59 del día siguiente; capítulo 2); desde 10f (D.109), cada momento se asigna a su día y a su hora con la zona horaria que tenía el teléfono entonces, así que las horas del panel (`primer_uso_h`, `ultimo_uso_h`, `cafeina_ultima_h`…) son la hora local del lugar donde estabas, también si viajaste. Cada **valor** es un número o `null`. Las variables 0/1 se guardan como 0.0 y 1.0.

Qué variables entran en los análisis lo decide otra función, `Estadistica.variables()`: una lista de 193 variables con nombre fijo en 24 grupos (valencia, sueño, pasos, clima…; capítulo 2 y Apéndice A.1; 187 hasta el esquema 16, que sumó tres contextos y tres compañías): 133 existen siempre y 60 **solo aparecen si hay datos** (por ejemplo, las de alcohol solo si alguna vez registraste alcohol; las privadas nunca: desde el módulo 10a se excluyen siempre y solo viajan a R) y, con `dinamicas = true`, las **variables dinámicas** de `Dinamicas.calcular()`: una por persona, tema, lugar, app, artista, nutriente, cuestionario, etapa de vida… (sección 6.3.3). En 10e (D.94) se sumaron dos a las de la v90: `error_prediccion_signo` (grupo «Ánimo»: valencia del día menos la predicción de la noche anterior, de −6 a +6; positivo si el día salió mejor de lo predicho) y `gasto_imprevisto_monto` (grupo «Dinero»: la suma de los gastos imprevistos del día), las dos de las que existen siempre y obvias con sus parientes: `error_prediccion_signo` con `prediccion`, `error_prediccion` y `valencia`, de la que se calcula; `gasto_imprevisto_monto` con `gasto_total`, `gasto_imprevisto`, `balance_dinero` y los `gcat_` (sección 6.9.2). Desde 10f (D.114) hay una más, `fuerza_ref_kg` (grupo «Cuerpo»: la mejor 1RM estimada del día en press inclinado con mancuernas, el ejercicio fijo de la plantilla «Creatina»; capítulo 8, sección 8.9), de las que solo aparecen si hay datos: vacía los días sin ese ejercicio, y obvia con las demás del gimnasio (sección 6.9.2). El panel puede tener columnas que no están en esa lista; esas no se analizan. **[Diseño propio]**.

### 6.3.2 Cero o faltante: la decisión más delicada del panel

Para cada variable la app decide qué pasa en los días en que no hay registro. Desde la entrega 10c (D.63) hay **una regla general** y unas pocas excepciones declaradas:

1. **Faltante**: si la fuente no dio nada, `null` (sueño del reloj, clima, pantalla…).
2. **Cero desde el primer registro** (`Estadistica.ceroDesde(fuente, base)`): devuelve los días de `base` que son iguales o posteriores al primer día de `fuente`. En esos días «no hubo» vale 0; antes, `null`. La usan `gasto_total`, `gasto_imprevisto` y, desde 10e, `gasto_imprevisto_monto` (desde el primer gasto, en días con diario), `n_impulso` (desde el primer impulso), `escalada` (desde la primera sesión), `gimnasio` (desde el primer entrenamiento, en días con diario o con entrenamiento) y `ejercicio_min` (desde la primera sesión del reloj, en días con datos del reloj: pasos o frecuencia cardiaca). Las familias que ya seguían esta idea siguen igual: `registros_animo` y `n_<emoción>`, `enfoque_*`, `ingreso_total`, `obras_dia`, las de sustancias…
3. **Cero solo donde se pudo responder.** Algunas variables necesitan algo más que «ya usabas la función»:
   - `hab_<id>`: 0 desde el día en que creaste el hábito (`Habito.desde`); antes, `null`, salvo que lo hayas marcado. Los hábitos creados antes de que se guardara esa fecha no la tienen y valen 0 en todos los días con diario.
   - `sueno_recordado`: 1 si recordaste un sueño; 0 solo si ese día dijiste algo del sueño (calidad o texto); si no, `null`.
   - Las sociales (`personas`, `cercanos`, `vi_*`, `cercania_media`, `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `penso_en_*`) y la base de `per_` y `pen_`: solo en los días «sociales» (`Dinamicas.diasSociales`), es decir, con diario escrito o con un momento no privado que nombra a alguien con @. Desde 10e (D.93), `personas` cuenta a quien nombras con @ en el diario **y** en esos momentos no privados; antes contaba solo el diario, y un día con solo momentos valía 0 aunque nombraras a alguien (H-14-2). En `tema_`, los días con diario escrito o con un momento que tiene un #tema.
   - `distancia_km`: 0 en los días con ubicaciones y sin movimiento (`Movilidad.kmPorDia` marca cada día lógico que tiene alguna muestra).
   - `probioticos_estimado`: solo en los días con bitácora de MacroFactor.
4. **Excepción declarada**: los contextos `ctx_*` valen 0 cuando no se marcan, porque la pantalla del Día dice que lo no marcado es «no».

La regla 2 es la más defendible: antes de empezar a usar una función no se sabe si «no pasó» o «no lo anotaste» (H-1-1, H-9-16). **[Diseño propio]**. Además, `presencias_revisado` (si revisaste quién estuvo y en quién solo pensaste) entra al panel como covariable en los días con personas, y es obvia con las variables sociales (H-1-6).

**Ejemplo.** Empiezas a registrar gastos el 1 de marzo. Hasta la entrega 10c, el 15 de febrero `gasto_total` valía 0 si ese día escribiste el diario: si comparabas «gasto» con «ánimo» usando todo el año, los ceros de enero y febrero contaban como «no gasté nada», aunque solo significaban «todavía no anotaba gastos». Ahora ese día es `null`, como ya pasaba con `ingreso_total`.

**Efecto:** más vacíos y menos ceros. Cambian medias, frecuencias y correlaciones, y la cobertura por variable baja; el cambio vale también para los días viejos, porque se recalcula con lo ya guardado.

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
| `lug_` | horas en cada lugar marcado, repartidas entre los días lógicos que cruza la visita (desde el 5 nov 2026, D.155; antes, toda la visita al día de llegada, con tope de 24 h) | horas | 15 / 3 (por peso) |
| `app_`, `ntf_` | minutos y notificaciones por app | min, n | 12 / 5; 8 / 5 (por peso) |
| `lap_`, `web_` | minutos por app y sitio en la laptop | min | 10 / 4 (por peso) |
| `ytc_`, `art_` | videos por canal; canciones por artista | n | 8 / 5; 10 / 5 |
| `gcat_`, `icat_` | gasto e ingreso por categoría | $ | 15 / 5; 8 / 3 (por peso) |
| `obr_`, `gen_` | cada obra y cada género | 0/1 | 10 / 5 |
| `med_`, `det_` | cada medicamento; cada detonante (privadas: solo en la exportación para R, nunca en las listas de la app) | 0/1 | 15 / 3; 10 / 3 |
| `suenot_` | rareza del sueño y pesadilla (antes del esquema 17, el tipo) | 0/1 | 6 / 3 |
| `ciu_`, `pais_` | horas en cada ciudad y país | horas | 8 / 3; 6 / 3 |
| `musc_`, `act_` | series por músculo; minutos por tipo de movimiento | n, min | 12 / 3; 6 / 3 |
| `nut_`, `ali_` | cada nutriente de MacroFactor; cada alimento | cantidad; 0/1 | 60 / 14; 12 / 5 |
| `esc_` | puntaje de cada cuestionario, copiado a los días que pregunta (7 en WHO-5 y PSS-4, 30 en UCLA-3; si dos respuestas cubren un día, la primera que lo cubre; desde 10c, `Dinamicas.porDiaCuestionario`) | puntaje | todos |
| `vida_` | etapas de vida («Conocerte») | 0/1 o número | todas |
| `pol_` | brújula política, vigente hasta el test siguiente | −100 a 100 | todos |

: Tabla 6.3. Las variables dinámicas: «todo se puede cruzar con todo»

**Por qué.** El principio de la app es que cualquier cosa registrada pueda compararse con cualquier otra. El tope por familia evita que las relaciones se ahoguen en casos raros (una persona mencionada dos veces). **[Diseño propio]**. El costo es enorme en comparaciones múltiples (sección 6.8).

**Un efecto que conviene conocer.** Las variables de tipo «escalón» (`vida_*`, `pol_*`, y en buena parte `esc_*`) cambian pocas veces al año. Al quitar la tendencia casi no les queda variación de un día a otro, así que la app las clasifica como «lentas» y **no entran** en «Las relaciones más marcadas» con el interruptor encendido (sección 6.5.4). Con el interruptor apagado sí entran, pero entonces quedan expuestas a cualquier otra tendencia que coincida en el tiempo. Dicho de otro modo: **una etapa de vida nunca puede aparecer como relación «sin tendencia»**; para ella están «Antes y después» y el análisis en R. Desde 10e (D.103) la lista lo dice y remite a «Antes y después» cuando deja fuera etapas, cuestionarios o la brújula (H-5-14, resuelto; sección 6.10.3).

### 6.3.4 El panel de R y el de la app (H-5-1, resuelto en 10d)

Hasta la entrega 10d, el script de R leía `vm_panel.csv`, que **no** es `Estadistica.panel()`: lo arma `Exportar.filasPanel()`, y tenía otros nombres para lo mismo, otros ceros (`gasto_total` e `ingreso_total` en 0 en cualquier fecha exportada; `escalada` y `gimnasio` falso en cualquier fecha), otras filas y muchas menos variables. Así, `vm_relaciones()` y «Las relaciones más marcadas» aplicaban el mismo algoritmo a tablas distintas. Desde 10d (P-30; Apéndice D, D.79 a D.84):

- **R analiza `vm_panel_extendido.csv`.** Empieza con las columnas de `vm_panel.csv` y suma todas las del panel de la app que falten, con sus nombres (`sueno_horas`, `distancia_km`, `hrv`…), las dinámicas, los contextos, hábitos, lectura del día y demás. `vm_panel.csv` se queda como tabla de compatibilidad.
- **Mismos valores en las columnas que comparten nombre.** En `vm_panel.csv` (y por tanto en el extendido) cada columna con el mismo nombre que una variable de la app lleva el valor de la app (`Exportar.comoLaApp`), con su regla de 0 y vacío (`ceroDesde`, sección 6.3.2): `gasto_total`, `ingreso_total`, `escalada` y `gimnasio` valen 0 desde el primer registro y en días con diario, y antes quedan vacíos; `ejercicio_min` vale 0 en días con datos del reloj y sin sesión; `ultimo_uso_h` suma 24 solo antes de las 5:00 (antes, en el CSV, antes de las 12; H-9-4); `canciones` cuenta solo escuchas de 30 s o más, también en la app (H-9-3). El tipo de cada columna (0/1, entero o número) se conserva.
- **Once nombres distintos, documentados.** `sueno_min`/`sueno_horas` (en minutos frente a horas), `sueno_profundo_min`/`sueno_profundo`, `sueno_rem_min`/`sueno_rem`, `km`/`distancia_km`, `hrv_rmssd`/`hrv`, `valencia_muestreo`/`muestreo_valencia`, `luz_dia_min`/`luz_horas` (minutos frente a horas), `sol_min`/`sol_horas` (minutos frente a horas), `carga_noche_inicio_h`/`carga_noche_h`, `temp_piel_delta`/`temp_piel` y `ruido_db_medio`/`ruido_db` (`Exportar.EQUIVALENTES`). No se renombraron, para no romper scripts viejos; el diccionario dice en cada una cuál es la otra, y `vm_familias.csv` las trata como la misma medida.
- **Mismas variables.** `vm_variables.csv` lista las que la app cruza (`Estadistica.variables()`, con las dinámicas recortadas igual que en la app); `vm_relaciones` y `vm_factores` se limitan a ellas por omisión. El seguimiento privado va marcado y solo se cruza con `privadas = TRUE`.

Lo que sigue distinto: `n_momentos` y `m_<tipo>` (solo en `vm_panel.csv`) cuentan también momentos privados (H-2-13); desde el 5 nov 2026 valen 0 con la misma regla P-15 que el panel (desde el primer registro y en días con diario; antes, sin dato), y antes de eso valían 0 en todas las fechas (D.154), y R completa el calendario con renglones vacíos (`complete()`), que no cambia ningún cálculo porque los vacíos no entran. **Cómo se comprobó:** `ExportacionPrueba.kt` fija que, con el mismo panel inventado, `vm_relaciones` y `vm_factores` dan los mismos números que la app; con una exportación real de la v89, el script cargó y analizó sin errores (y así se encontró lo que corrigió D.87); y después el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Ver la comparación completa en la sección 6.22.

## 6.4 La correlación: Pearson y Spearman

### 6.4.1 Qué hace

Ambas responden «¿cuando una sube, la otra tiende a subir (o a bajar)?» con un número de −1 a 1.

### 6.4.2 Cómo lo hace exactamente

**Pearson** (`Estadistica.pearson`, ecuación B.1). Con n ≥ 3 pares:

$$r = \frac{\sum_i (x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_i (x_i-\bar x)^2\,\sum_i (y_i-\bar y)^2}}\qquad\qquad (6.1)$$

Si una de las dos variables no cambia (suma de cuadrados igual a 0), devuelve `null`: no hay relación que calcular. La función interna `r()` de `relaciones` es la misma fórmula, escrita sobre arreglos para ir más rápido.

**Spearman** (`Estadistica.spearman`, B.2): Pearson aplicado a los **rangos**. `Estadistica.rangos` ordena los valores y, si hay empates, a todos les da el **promedio** de los lugares que ocupan (dos valores empatados en los lugares 3.º y 4.º reciben 3.5). Es el tratamiento estándar de empates. **[Validado]**.

**Cómo se nombra la fuerza** (`Estadistica.fuerza`): |r| < 0.1 «prácticamente nula»; < 0.3 «débil»; < 0.5 «moderada»; de 0.5 en adelante «fuerte». Son los cortes convencionales de Cohen (1988) para r (0.1, 0.3 y 0.5). **[Validado]** como convención; Cohen mismo advertía que dependen del campo.

### 6.4.3 Cuál se usa dónde (pista 5 del plan: confirmada, H-5-7)

| Lugar | Coeficiente |
|---|---|
| Las relaciones más marcadas (`relaciones`) | **Pearson** sobre los restos sin tendencia (o los valores, si se apaga el interruptor) |
| Cruzar dos variables | Muestra **Pearson y Spearman**; el texto, la fuerza y el intervalo usan **Pearson** |
| Todo lo que se mueve con algo (`factores`) | Desde 10c, **Pearson** (sobre los restos sin tendencia, como la lista) para variables continuas; diferencia de medias para 0/1. Hasta 10c era Spearman |
| Dinámica (inercia) | Pearson |
| R: `vm_relaciones`, `vm_factores` | Pearson (como la app; `vm_factores` desde 10d) |
| R: `vm_cruce`, `vm_presion` | Desde 10e, **Pearson** (el principal, con intervalo y p por el n efectivo, como la app), la r sin tendencia y **Spearman**; antes, Spearman por omisión |
| R: `vm_cruce_horas` | Desde 10e, Pearson y Spearman, con p sin corregir (la app no cruza horas) |

: Tabla 6.4. Cuál se usa dónde (pista 5 del plan: confirmada, H-5-7)

### 6.4.4 Razonamiento y cuándo falla

Pearson mide relaciones **rectas** y se deja arrastrar por un día extremo (un día con 30 000 pasos pesa mucho). Spearman mide relaciones **que solo suben o solo bajan** (monótonas) y es robusto a extremos, pero con muchas variables 0/1 o con muchos empates pierde información. Usar Pearson sobre los restos sin tendencia es razonable: al restar el promedio de los vecinos, los valores se vuelven más simétricos. El problema no es elegir uno u otro, sino que **la misma pareja puede mostrar números distintos en dos pantallas** (r en la lista, ρ en la ficha de la variable) sin que la app lo explique. Desde la entrega 10c, la ficha usa también r de Pearson con la misma tendencia quitada que la lista, así que la pareja muestra el mismo número en las dos; «Cruzar dos variables» sigue mostrando los dos coeficientes y ahora también la r sin tendencia. En R, desde la entrega 10d `vm_factores` usa Pearson como la app, y desde 10e (D.106) `vm_cruce` y `vm_presion` imprimen lo mismo que «Cruzar dos variables»: r de Pearson como coeficiente principal, con su intervalo y su p por el n efectivo, la r sin tendencia y ρ de Spearman; `vm_cruce_horas` da también los dos coeficientes. Hasta 10e los tres daban solo Spearman por omisión (H-5-7, **resuelto en parte**: «Todo lo que se mueve con algo» y `vm_factores` siguen con su propio coeficiente, r de Pearson o, en las 0/1, la diferencia de medias, sin ρ al lado; sección 6.13). **[Aproximación]**.

## 6.5 Quitar la tendencia

### 6.5.1 El problema: correlaciones de mentira por tendencias

Si dos cosas cambian lentamente con el tiempo, **parecen relacionadas aunque no tengan nada que ver**. Este problema se conoce desde hace un siglo como «correlaciones sin sentido» entre series de tiempo (Yule, 1926) y, en regresión, como «regresiones espurias» (Granger y Newbold, 1974). **[Validado]**.

**Ejemplo.** En otoño los días se acortan; al mismo tiempo, por otras razones, estás bajando de peso. Si correlacionas «horas de luz» con «peso», sale una correlación positiva fuerte. Pero no es que la luz te haga subir de peso: ambas bajan con los meses y nada más.

### 6.5.2 Cómo lo hace exactamente

`Estadistica.series()` (privada) transforma cada variable antes de compararla:

1. Alinea la variable a las fechas del panel; donde no hay dato, `NaN`.
2. Para cada día *i* con dato, toma **todos los días con dato que estén a 10 días de calendario o menos** de él (de *i* − 10 a *i* + 10, **incluido el propio día**). Esa es la constante `VENTANA = 10`.
3. Si hay al menos **3** de esos días, el **resto** del día es su valor menos el promedio de esa ventana (ecuación B.3):

$$\text{resto}_i = x_i - \frac{1}{|W_i|}\sum_{j\in W_i} x_j,\qquad W_i=\{j:\ |d_j-d_i|\le 10,\ x_j\ \text{existe}\},\ |W_i|\ge 3\qquad\qquad (6.2)$$

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

![Figura 6.1. Dos variables sintéticas sin relación día a día que solo comparten un cambio lento: con los valores tal cual, r = −0.36; con los restos de ±10 días, r = +0.01. Script: `figuras/fig12_tendencia_espuria.py` (replica `Estadistica.series`).](figuras/fig12_tendencia_espuria.png){width=100%}

### 6.5.6 Cuándo falla

- **El ciclo de la semana no se quita (H-5-6).** Una ventana de 21 días contiene exactamente tres semanas, así que el promedio de la ventana **borra** cualquier patrón semanal del promedio y lo **deja entero** en los restos. Si dos variables cambian ambas con el fin de semana (duermes más y te sientes mejor el sábado), su correlación sin tendencia puede deberse solo al calendario. A veces eso es justo lo que quieres saber («el fin de semana me hace bien»); otras veces confunde (no es el sueño, es el sábado). **[Aproximación]**. Desde 10e (D.103, D.106; H-5-6, **resuelto en parte**):
  - **En la app**, la ayuda del interruptor «Quitar tendencias» lo dice: «No quita el ritmo de la semana: dos cosas que cambian juntas el fin de semana siguen saliendo juntas (en R, vm_relaciones(sin_semana = TRUE) lo quita)». La app no tiene opción para quitarlo.
  - **En R**, `vm_relaciones` y `vm_factores` tienen `sin_semana = FALSE`. Con `sin_semana = TRUE` (solo actúa con `sin_tendencia = TRUE`; si no, avisa y no hace nada), a los restos de cada variable se les resta el promedio de sus restos en su día de la semana, de lunes a domingo, con los días con dato (`.vm_sin_semana`). La autocorrelación y r usan esos restos; si una variable es «lenta» (6.5.4) se decide con los restos de antes. En `vm_factores` se ajustan el objetivo y las variables continuas; las 0/1 se comparan contra el objetivo ya ajustado. **[Aproximación]** (restar la media de cada día de la semana es la forma más simple de quitar un ciclo semanal fijo; supone que ese ciclo no cambia con los meses).
  - **Ejemplo.** Tus restos de sueño de los sábados promedian +0.8 h (duermes más que tus semanas vecinas). Un sábado con resto +1.0 h queda, con `sin_semana = TRUE`, en +0.2 h: lo que ese sábado tuvo de más **para ser sábado**. Si el ánimo también sube los sábados, la relación entre los dos ya no puede salir solo de eso.
- **Cambios bruscos.** Si algo cambia de golpe (te mudas), los días cercanos al cambio quedan a medio camino entre «antes» y «después» y sus restos son raros durante ~10 días.
- **Efectos lentos reales.** Si dormir mal **durante semanas** te deprime poco a poco, ese efecto es justo lo que quitar la tendencia borra. Con el interruptor encendido solo se ven efectos **de corto plazo** (de días). Por eso el interruptor existe y por eso la tarjeta avisa cuando la correlación «con tendencia» difiere en 0.2 o más.

## 6.6 Los días no son independientes: el número efectivo de días

### 6.6.1 El problema

Las fórmulas clásicas de intervalos y valores p suponen que cada día es una observación **independiente**. Pero tus días se parecen a sus vecinos: una semana de exámenes tiene varios días seguidos con poco sueño **y** mal ánimo. Si cuentas esos 7 días como 7 pruebas independientes, la relación parece más segura de lo que es. En la simulación de la Figura 6.2, con dos series sin relación real y autocorrelación de 0.8, el intervalo ingenuo «encuentra» una relación en el **35 %** de los casos en vez del 5 % esperado.

### 6.6.2 Cómo lo hace exactamente

1. **Autocorrelación de cada variable** (`Estadistica.autocorr`, B.4): Pearson entre el valor de cada día y el del día siguiente, usando **solo pares de días consecutivos** con dato en ambos (en el análisis por semana, semanas consecutivas: `paso = 7`). Si hay **menos de 10 pares**, o si la serie no varía y Pearson no se puede calcular, devuelve desde 10e **0.3** (`Estadistica.AR1_SIN_DATO`; D.95); hasta 10e devolvía 0, que no corregía nada. Se calcula sobre los restos (con el interruptor encendido) o sobre los valores (apagado), sobre **toda la serie** de la variable.
2. Cada autocorrelación se **recorta** a [−0.5, 0.97].
3. **Días efectivos** (B.5):

$$n_{Ef} = n\,\frac{1-r_a r_b}{1+r_a r_b},\qquad \text{acotado a } [\min(4,n),\ n]\qquad\qquad (6.3)$$

donde *n* son los días con ambos datos y *r_a*, *r_b* las autocorrelaciones de las dos variables. Si nEf ≤ 3, la pareja no se evalúa.

### 6.6.3 Razonamiento científico

Es la aproximación clásica para la correlación entre dos series con autocorrelación de orden 1 (AR(1)), conocida en climatología y ecología (Bretherton et al., 1999; Pyper y Peterman, 1998). La idea: lo que infla la incertidumbre es que **ambas** series sean persistentes; si solo una lo es (*r_b* = 0), no hay inflación. **[Validado]** como aproximación de primer orden. Supone que la dependencia es de tipo AR(1) (que el parecido decae geométricamente con los días) y que las autocorrelaciones se estiman bien.

El 0.3 que se supone cuando la autocorrelación no se puede estimar es **[Diseño propio]**: un valor prudente, no estimado (sección 6.6.6). Las cotas son decisiones propias y prudentes: la cota superior *n* impide que una autocorrelación negativa en una variable y positiva en la otra **aumente** los días efectivos por encima de los reales; el tope de 0.97 evita dividir casi entre cero. **[Diseño propio]**.

### 6.6.4 Ejemplo trabajado

Sueño y valencia, sin tendencia: n = 120 días con ambos; autocorrelación de los restos de sueño *r_a* = 0.4 y de valencia *r_b* = 0.3. Producto 0.12.

nEf = 120 × (1 − 0.12) / (1 + 0.12) = 120 × 0.88 / 1.12 = **94.3** días efectivos.

Como 94.3 es menor que 0.8 × 120 = 96, la tarjeta escribe «120 días (≈ 94 independientes)».

### 6.6.5 Figura

![Figura 6.2. Izquierda: días efectivos según la autocorrelación (la misma en ambas variables) para 30, 90 y 365 días. Derecha: 4000 parejas simuladas de series AR(1) independientes de 90 días; con la autocorrelación real de 0.8, el intervalo con n da falsas alarmas el 35 % de las veces y el de la app, el 4.6 %. Script: `figuras/fig13_n_efectivo.py`.](figuras/fig13_n_efectivo.png){width=100%}

### 6.6.6 Cuándo falla (H-5-13)

- **Variables esporádicas (resuelto en 10e, D.95).** Si una variable tiene menos de 10 pares de días consecutivos (o no varía), su autocorrelación no se puede estimar. Hasta 10e se tomaba como 0: nEf = n y no se corregía nada, justo en las variables con menos datos. Desde 10e se supone **0.3** (`Estadistica.AR1_SIN_DATO`), en «Las relaciones más marcadas» (`autocorr`), en «Todo lo que se mueve con algo» y en «Cruzar dos variables» (allí `Estadistica.ar1` devuelve `null` y quien la usa pone 0.3); en R igual, con `.vm_ar1_sin_dato` (`vm_relaciones`, `vm_factores`, `vm_cruce` y `vm_presion`). Las diferencias de medias (6.15.2) **no** lo usan: ahí, sin 10 pares, ρ sigue siendo 0. En el análisis por semana pasa durante las primeras ~11 semanas. **[Diseño propio]**: 0.3 es un valor prudente elegido a mano, no estimado de tus datos; si la autocorrelación real es mayor, la corrección sigue quedándose corta.
  - **Ejemplo.** Una variable que registras a saltos (un día sí y otro no) tiene 40 días en común con la valencia, pero ningún par de días seguidos; la autocorrelación de la valencia es 0.5 y r = 0.35. Con 0 (antes): nEf = 40, IC de 0.04 a 0.60, p = 0.026: «no incluye el cero». Con 0.3 (desde 10e): producto 0.15, nEf = 40 × 0.85 / 1.15 = **29.6**, IC de −0.01 a 0.63, p = 0.060: incluye el cero, y la tarjeta dice «40 días (≈ 29 independientes)». Si ninguna de las dos se pudiera estimar, el producto sería 0.09 y nEf = 33.4.
- **Autocorrelación de toda la serie, no de los días compartidos**: si una variable tiene dos años y la otra dos meses, la autocorrelación de la primera se calcula con los dos años, aunque la comparación use solo los dos meses comunes.
- **Persistencia más larga que un día**: si el parecido entre días dura semanas (no decae geométricamente), la fórmula de primer orden se queda corta. Quitar la tendencia reduce mucho este problema.
- **Ya no solo en esta lista (resuelto en 10c, D.65 y D.66).** Hasta la entrega 10c, la corrección existía solo en «Las relaciones más marcadas»; «Cruzar dos variables», «Todo lo que se mueve con algo», «Tendencia» y «Antes y después» trataban los días como independientes (H-5-3, H-5-4, H-5-5). Ahora las correlaciones de «Cruzar» y de «Todo lo que se mueve» usan la misma fórmula de nEf (`Estadistica.correlacionEf`, con la autocorrelación de cada serie calculada por `Estadistica.ar1`, que también exige 10 pares de días seguidos; sin ellos, 0.3 desde 10e), y las diferencias de medias usan el factor AR(1) de la sección 6.15.2.

## 6.7 Intervalo de confianza y valor p

### 6.7.1 Cómo lo hace exactamente

Para una correlación *r* con *n* días (o nEf), la app usa la **transformación z de Fisher** (Fisher, 1915) (B.6–B.8):

$$z=\operatorname{atanh}(r),\qquad EE=\frac{1}{\sqrt{n-3}},\qquad IC_{95}=\big[\tanh(z-1.96\,EE),\ \tanh(z+1.96\,EE)\big]\qquad\qquad (6.4)$$

- `Estadistica.ic95(r, n)`: con *n* entero; exige n ≥ 5; recorta *r* a ±0.9999 para que atanh no sea infinito.
- En `relaciones` se usa **nEf** en lugar de *n*, y el valor p bilateral es *p* = 2 · (1 − Φ(|z| / EE)).
- Φ (la distribución normal acumulada) se calcula con `Analisis.phi`, la aproximación 7.1.26 de Abramowitz y Stegun (1964) para la función de error, con error máximo del orden de 10⁻⁷. Para estos usos es exacta en la práctica. **[Validado]**.

**La t de Student (desde la entrega 10c).** Para las diferencias de medias, el modelo múltiple y el día de la semana, la app ya no usa 1.96 sino el cuantil de la **t de Student**, la curva que reemplaza a la normal cuando la variabilidad se estima con pocos datos: tiene colas más gruesas, y más cuanto menos grados de libertad (con 2, el 95 % central llega a ±4.30; con 5, a ±2.57; con 30, a ±2.04; con muchos, a ±1.96).

- `Estadistica.pT(t, gl)`: el *p* de dos colas, con la fórmula exacta p = I_{gl/(gl+t²)}(gl/2, 1/2), donde I es la función beta incompleta regularizada. La app la calcula con una fracción continua (método de Lentz) y el logaritmo de la función gamma con la aproximación de Lanczos (g = 7). Admite grados no enteros, como los de Welch (B.14a).
- `Estadistica.cuantilT(p, gl)`: el valor t tal que la probabilidad acumulada es *p*, buscado por bisección (100 pasos) sobre `pT`.

Se comprobó en pruebas JVM (`CalculosPrueba.cuantilesDeT`): cuantilT(0.975, 1) = 12.706, (0.975, 2) = 4.303, (0.975, 5) = 2.571, (0.975, 30) = 2.042, y pT(2, 10) = 0.0734, los valores de las tablas. **[Validado]**.

### 6.7.2 Por qué

La distribución de *r* es asimétrica cerca de ±1; atanh la vuelve aproximadamente normal con varianza 1/(n − 3). Es el método estándar. **[Validado]**. Usarlo con nEf es la combinación habitual con la corrección de la sección 6.6. **[Aproximación]**.

### 6.7.3 Ejemplo trabajado (continúa el de 6.6.4)

r = 0.30, nEf = 94.29.
z = atanh(0.30) = 0.3095; EE = 1/√(94.29 − 3) = 0.1047.
IC: tanh(0.3095 ± 1.96 × 0.1047) = tanh(0.1044) a tanh(0.5146) = **0.10 a 0.47**.
z/EE = 2.957 → **p = 0.0031**.
Con *n* = 120 sin corregir habría salido IC 0.13 a 0.45 y p = 0.0008: casi cuatro veces «más significativo» de lo que es.

### 6.7.4 Cuándo falla

- Para **Spearman**, la varianza de atanh(ρ) es algo mayor que 1/(n − 3) (Fieller et al., 1957, proponen 1.06/(n − 3)). Hasta 10d, `vm_factores` en R usaba 1/(n − 3) también para ρ: intervalos ligeramente estrechos. Desde 10c en la app y desde 10d en R, `factores` usa Pearson, así que este punto ya no los afecta. **[Aproximación]**.
- El intervalo de la correlación sigue usando **1.96** (normal) aun con nEf pequeño. Con la transformación de Fisher eso es lo estándar: el error de usar la normal es pequeño frente al de no corregir por días parecidos. **[Aproximación]**.
- Para variables 0/1 con pocos «1», Pearson es una correlación punto-biserial; la aproximación de Fisher es menos precisa. La app exige al menos **4 días de cada valor** para evaluarlas (sección 6.10).

## 6.8 Muchas comparaciones a la vez: Benjamini-Hochberg

### 6.8.1 El problema

Con 300 variables hay 300 × 299 / 2 = **44 850** parejas. Si ninguna tuviera relación real, con el umbral clásico p < 0.05 saldrían **unas 2 240 «relaciones» por puro azar**. Revisar «todo contra todo» obliga a corregir.

### 6.8.2 Cómo lo hace exactamente

`Analisis.bh(p)` (B.9): ordena los *m* valores p de menor a mayor, p₍₁₎ ≤ … ≤ p₍ₘ₎, y calcula para cada uno

$$q_{(k)} = \min_{j\ge k}\ \min\!\Big(1,\ p_{(j)}\,\frac{m}{j}\Big)\qquad\qquad (6.5)$$

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

: Tabla 6.5. Ejemplo trabajado

Las cinco primeras tienen q < 0.10: se sostienen. Con m = 6 la corrección es suave. Con m = 44 850, la mejor pareja necesita p < 0.10/44 850 = 2.2 × 10⁻⁶ para tener q < 0.10 si es la única real; con nEf = 100, eso equivale a |r| ≳ **0.45**. Si hay unas 50 relaciones reales, el umbral de la 50.ª baja a |r| ≳ 0.37. Por eso la app avisa: «Con pocos días es normal que nada se sostenga».

### 6.8.5 Figura

![Figura 6.3. 200 pruebas simuladas (20 relaciones reales con r = 0.35, 180 sin relación, 90 días). Sin corregir: 19 aciertos y 9 falsas alarmas (32 % de lo marcado es falso). Con BH a q < 0.10: 15 aciertos y 1 falsa alarma (6 %). Script: `figuras/fig14_benjamini_hochberg.py` (replica `Analisis.bh`).](figuras/fig14_benjamini_hochberg.png){width=100%}

### 6.8.6 Cuándo falla

- **El número de pruebas depende del modo.** Por día o por semana, con o sin tendencia, se prueban parejas distintas (las lentas salen, el mínimo de días cambia), así que la misma pareja puede tener q distinto en cada modo.
- **Excluir las obvias es razonable pero no neutral**: baja *m* y hace la corrección más permisiva. Está bien porque esas parejas no son descubrimientos, pero la lista de obvias debe estar completa y ser la misma en app y R (H-5-2; desde 10d lo es, sección 6.9.3).
- **Hasta 10c, «Todo lo que se mueve con algo» corregía con p que no descontaban la autocorrelación** (H-5-3): ahí BH trabajaba con valores p demasiado pequeños. Desde 10c en la app y desde 10d en R (`vm_factores`), sus p usan nEf o el factor AR(1) (sección 6.13). Lo que queda: su BH corrige solo entre las variables revisadas contra ese objetivo.

## 6.9 Las relaciones «obvias» y cómo se excluyen

### 6.9.1 Qué hace

Marca las parejas cuya correlación es casi segura por construcción, para no presentarlas como descubrimientos: calorías con proteína, eventos de agenda con minutos ocupados, pasos con minutos a pie, «minutos en Instagram» con «minutos de pantalla».

### 6.9.2 Cómo lo hace exactamente

`Estadistica.obvia(a, b)` devuelve verdadero si se cumple **cualquiera** de estas reglas:

1. Ambas son del mismo grupo y el grupo es «Lectura del día», «Momentos», «Sentimientos» o «Sueños».
2. Ambas están en una misma familia de `Estadistica.FAMILIAS` (59 conjuntos desde 10e; 58 en la v90; 56 en la v81, más el de los avisos de ánimo y el de `presencias_revisado`; una variable puede estar en varios). Desde 10e (D.94), `error_prediccion_signo` entra en la familia de `prediccion` y `error_prediccion`, y un conjunto nuevo une `valencia` con `error_prediccion` y `error_prediccion_signo`, que se calculan de ella; `gasto_imprevisto_monto` entra en la de `gasto_total` y `gasto_imprevisto`, que ahora incluye también `balance_dinero`. Desde 10f (D.114), `fuerza_ref_kg` entra en la del gimnasio (`gimnasio`, `volumen_kg`, `series_gimnasio`, `ejercicio_min` y `cal_activas`): sale de las mismas series; siguen siendo 59 conjuntos. Ejemplos: todos los nutrientes fijos; `valencia` con `muestreo_valencia`; `valencia`, `animo_valencia`, `momentos_valencia`, `valencia_acompanado` y `valencia_solo`; `animo_cierre_valencia` con `valencia` y `animo_valencia`; las de alcohol entre sí; impulsos, recaídas y libido entre sí.
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

Hasta la entrega 10d, en R `vm_familias` tenía solo 20 conjuntos y `vm_obvia` no tenía las reglas 1 y 3–9 (salvo los `m_*` entre sí), así que en `vm_relaciones()` salían como «firmes» parejas que la app esconde, como `valencia` con `animo_valencia` o `vi_familia` con `cercanos`, y BH corregía sobre otro número de pruebas (H-5-2). Desde 10d (P-31) la exportación escribe `vm_familias.csv`, generado por `Estadistica.familias()` con las **mismas** diez reglas, sobre las variables fijas y todas las dinámicas, con los nombres del panel exportado. Cada familia tiene un **lado**: con lado 0 todas sus variables son obvias entre sí; con lados 1 y 2, cada una de un lado lo es con cada una del otro, pero no con las de su lado. Así se escriben las reglas «en estrella» (6, 10 y parte de 5): `per_ana` es obvia con `personas` y con `palabras`, pero eso no vuelve obvias a `personas` y `palabras` entre sí. `vm_obvia` lee ese archivo. `ExportacionPrueba.familiasIgualQueObvia` comprueba, con 45 variables inventadas que tocan todas las reglas, que da lo mismo que `obvia()` en cada par. La forma (familias con lados) es **[Diseño propio]**; la lista sigue siendo la misma lista curada a mano. Sin ese archivo (exportaciones viejas o la resumida), R usa la lista corta de antes.

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

- Un resumen: «Revisé N combinaciones (dejé fuera M obvias)… ya corregido eso, K se sostienen», donde K es el número con q < 0.10. Si hay lentas, las nombra. Desde 10e (D.103; H-5-14), si entre las lentas hay alguna etapa de vida, cuestionario o eje de la brújula (`vida_`, `esc_`, `pol_`), agrega: «Las etapas de vida, los cuestionarios y la brújula cambian a saltos: para ellas mira «Antes y después», abajo, o apaga «Quitar tendencias» sabiendo que así las tendencias se cuelan».
- Filtros por tema (`FOCOS`: con tu ánimo, con tu sueño, personas y vida, lugares y viajes, cuerpo y comida, teléfono y social, lo que lees, ves y escuchas, dinero). Una pareja entra si **alguna** de sus dos variables es del tema.
- `agrupar(lista, 15)`: junta las parejas **casi repetidas**. Dos parejas son «parecidas» si comparten una variable y la otra de cada una está en la misma familia de `FAMILIAS` (ánimo con horas de sueño y ánimo con sueño REM). Recorre la lista ya ordenada; la primera de cada grupo es la representante y las demás se anotan como «También con «…» (+0.31)» (máximo 3 nombradas y «y N más»). Muestra hasta **15** grupos; deja de buscar parecidas después del elemento 400.
- La **tarjeta** (`ui/Relacion.kt`, `TarjetaRelacion`): dos recuadros con los nombres y un **lazo** entre ellos.
  - Color: verde si r ≥ 0 («suben juntas»), rojo si r < 0.
  - **Grosor**: (1.2 + 4.5 · |r|) dp; un r de 0.42 da 3.09 dp.
  - **Punteado** y con 65 % de opacidad si q ≥ 0.10 («podría ser azar»).
  - Texto de detalle: «N días»; «(≈ nEf independientes)» si nEf < 0.8 · n; la fuerza; «con tendencia: ±0.xx» si el interruptor está encendido y |bruta − r| ≥ 0.2; «obvia»; «podría ser azar».
- Tocar una tarjeta abre «Todo lo que se mueve con algo» con una de las dos variables como objetivo (la que no es la valencia). En el modo «por semana», desde 10e (D.103; H-9-40), la pareja se abre en cambio abajo, en «Cruzar dos variables», por semanas y sin desfase, y la pantalla lo dice («La pareja quedó abajo, en «Cruzar dos variables», por semanas»); antes abría la ficha por día, que mostraba otro análisis que el tocado.

![Figura 6.4. Tres tarjetas sintéticas: una relación positiva firme (grosor 3.09 dp), una negativa firme cuya correlación con tendencia era bastante más fuerte, y una que podría ser azar (punteada). Script: `figuras/fig15_tarjeta_relacion.py` (imita `TarjetaRelacion` y `lazo` del tema por omisión; los temas cambian la forma del lazo, no su grosor ni el punteado).](figuras/fig15_tarjeta_relacion.png){width=100%}

### 6.10.4 Ejemplo trabajado completo

Sueño (h) y valencia, interruptor encendido. Tras quitar la tendencia hay 120 días con ambos restos. r = 0.30, autocorrelaciones 0.4 y 0.3 → nEf = 94.3; IC 0.10 a 0.47; p = 0.0031 (6.6.4 y 6.7.3). Supón que se probaron m = 2 000 parejas no obvias y que esta queda en el lugar 25 por p: su q es como mucho 0.0031 × 2000 / 25 = 0.25 (puede bajar si alguna pareja de más abajo da un mínimo menor). Con q ≥ 0.10, la tarjeta sale **punteada** y dice «120 días (≈ 94 independientes) · moderada · podría ser azar», aunque su intervalo no incluya el cero. **No es una contradicción**: el intervalo es el de esa pareja sola; q tiene en cuenta que se miraron 2 000.

### 6.10.5 Cuándo falla

- Las tres protecciones solo cubren el azar y las tendencias; **no** cubren **confusores** (una tercera cosa que mueve a las dos, como el día de la semana o estar enfermo) ni la **dirección** (¿el sueño mejora el ánimo o el buen ánimo te deja dormir?). La app lo dice en «Cruzar dos variables».
- El mínimo de 14 días con 14 restos es bajo; con 14 días solo se ven correlaciones muy grandes (sección 6.21).
- Las variables privadas **no** entran en esta lista ni en ningún análisis de la app desde el módulo 10a (`Estadistica.variables()` las excluye; ver capítulo 10, sección 10.3). Son `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `n_impulso`, `medicamentos_n`, `med_*`, `det_*`, y el `hab_` y la `campana_hab_` del hábito ligado a las recaídas (`Privado.esVariable`). El panel las sigue calculando para la exportación, así que solo se cruzan en R. Hasta v84 entraban si el seguimiento privado estaba encendido, y `n_impulso` y `med_*` siempre.

## 6.11 Análisis por semana

**Qué hace.** «Por semana» repite la lista con **semanas** en lugar de días. Sirve para lo que pasa pocas veces (escalar, viajar, escribir cartas).

**Cómo exactamente** (`Estadistica.semanal`, B.10): cada semana va de **lunes a domingo**; cada variable es el **promedio** de los días de esa semana con dato (en las 0/1, la proporción de días con 1); una semana entra para una variable si tiene **al menos 3** días con dato. Luego `relaciones(…, minN = 8, paso = 7, ventana = 35)`: al menos 8 semanas en común; la tendencia se quita con ±35 días (±5 semanas, ventana de hasta 11 semanas); la autocorrelación es entre semanas consecutivas; una variable es lenta con al menos 8 restos. «Cruzar dos variables» por semana usa `pares(…, paso = 7)`: Y de la semana siguiente.

**Por qué.** Promediar reduce el ruido diario y vuelve frecuentes los eventos raros. **[Diseño propio]**.

Desde 10e (D.103; H-9-40), tocar una relación de este modo la abre en «Cruzar dos variables» por semanas (6.10.3).

**Cuándo falla.** Con 8 semanas solo se ve una correlación enorme; durante las primeras ~11 semanas la autocorrelación no se puede estimar y se supone 0.3 (hasta 10e, 0; 6.6.6); el promedio de una semana con 3 días no es comparable al de una con 7.

## 6.12 «Cruzar dos variables»

**Qué hace.** Eliges X e Y y ves su dispersión, r de Pearson, la r sin tendencia, ρ de Spearman, el número de días y un texto.

**Cómo exactamente.** `Estadistica.pares(panel, x, y, desfase)` toma los días con ambos datos; con desfase, Y es la del **día siguiente de calendario** (fecha + 1; por semana, la semana siguiente). Con menos de 3 días no dibuja. Desde la entrega 10c (D.66):

- **El intervalo** sale de `Estadistica.correlacionEf`: Pearson con el intervalo de Fisher sobre **nEf** (6.6), usando la autocorrelación de toda la serie de X y de toda la de Y (`Estadistica.ar1`; por semana, entre semanas seguidas; si no se puede estimar, 0.3 desde 10e). Requiere 5 días. Si nEf < 0.8 · n, el texto añade «cuentan como unos N independientes».
- **La r sin tendencia**: `Estadistica.sinTendencia` resta a cada valor el promedio de sus ±10 días (±35 por semana), como la lista; con al menos 5 pares, la pantalla la muestra en una cifra aparte, y si difiere de la bruta en 0.2 o más el texto lo explica («parte de lo que se ve es que las dos cambiaron con el tiempo»).

El texto dice la fuerza, la dirección, si el intervalo incluye el cero («podría ser azar») y, con menos de 30 días, «Solo son N días: tómalo como pista»; siempre recuerda que correlación no es causa. La recta punteada de la dispersión es la de mínimos cuadrados de Y sobre X.

**Cuándo falla.** Hasta 10c esta pantalla usaba el **n bruto** y solo los valores tal cual, así que podía decir «no incluye el cero» para una pareja que la lista marcaba «podría ser azar» (H-5-4, **resuelto en 10c, D.66**). Sigue habiendo diferencias menores con la lista: el intervalo y la fuerza son los de la r **bruta** (la sin tendencia se muestra al lado, sin intervalo), y la autocorrelación se calcula con toda la serie de cada variable, no solo con los días comunes. Para una sola pareja elegida por ti no hace falta BH. **[Aproximación]**. Desde 10e (D.106), `vm_cruce` en R da lo mismo que esta pantalla (6.22 y Apéndice C).

## 6.13 «Todo lo que se mueve con algo»

### 6.13.1 Qué hace

Eliges un objetivo (tu ánimo, una persona, un lugar, una app, una etapa de vida) y la app revisa **todas** las demás variables contra él.

### 6.13.2 Cómo exactamente (`Analisis.factores`)

Desde la entrega 10c (D.66), con un interruptor «Quitar tendencias» que empieza como estaba en la lista desde la que llegaste (`Herramienta.sinTendencia`):

1. Prepara el objetivo: su serie y, con el interruptor encendido, sus **restos sin tendencia** (`Estadistica.sinTendencia`, ±10 días, como en 6.5); calcula su autocorrelación (`Estadistica.ar1`; si no se puede estimar, 0.3 desde 10e, como la de cada variable continua del paso 4).
2. Para cada variable *v* distinta del objetivo: se salta si comparte familia con el objetivo (`FAMILIAS`) o si `obvia(v, objetivo)`. Si *v* tiene menos de 10 días, se salta.
3. Si *v* es 0/1 (en sus valores tal cual):
   - toma el objetivo (resto o valor; con desfase, el del día siguiente) en los días con 1 y en los días con 0; exige al menos **5** de cada uno;
   - calcula la **diferencia de medias** con `Estadistica.diferenciaEnDias` (6.15.2): t de Welch y el error estándar corregido porque los días seguidos se parecen; el *p* es el de esa t;
   - muestra «+0.45 puntos los días con (12) frente a sin (80)».
4. Si es continua: toma sus restos (o valores), los empareja con el objetivo y exige al menos **14** pares; calcula **r de Pearson**, el intervalo de Fisher y el *p* con **nEf** (`Estadistica.correlacionEf`, igual que la lista). Muestra «r = +0.31 · moderada · 90 días (≈ 70 independientes)».
5. Corrige todos los *p* con BH; marca con ✓ los de q < 0.1; muestra las 20 primeras ordenadas por *p*.

Hasta 10c usaba los valores tal cual, ρ de Spearman con *n* bruto y, para 0/1, la normal (1.96) con días independientes.

### 6.13.3 Razonamiento

Responde una pregunta natural («¿qué acompaña mis mejores días?»). El desfase de un día ayuda con la dirección: si lo de hoy se asocia con el ánimo de **mañana**, es menos probable que el ánimo haya causado lo otro (aunque no imposible: una variable persistente puede seguir siendo efecto del ánimo de ayer). **[Aproximación]**.

### 6.13.4 Ejemplo trabajado

Objetivo: valencia, con el interruptor apagado (valores tal cual). Variable: «Aparece @persona_b» (0/1). Días con: 5, 6, 5, 4, 6, 5, 6 (media 5.29). Días sin (diez): 4, 4, 5, 3, 4, 5, 4, 3, 4, 5 (media 4.10). Hay 7 y 10 días de cada tipo (mínimo 5), así que la app sí la evalúa. Supón que son días sueltos, sin 10 pares de días seguidos: la autocorrelación se toma como 0 (en las diferencias de medias, el 0.3 de 10e no se usa; 6.6.6).

- Diferencia 1.19; EE = √(0.571/7 + 0.544/10) = 0.369.
- Grados de libertad de Welch: (0.0816 + 0.0544)² / (0.0816²/6 + 0.0544²/9) = **12.86**; t(0.975; 12.86) = **2.163**.
- IC: 1.19 ± 2.163 × 0.369 = **0.39 a 1.98**; t = 3.21, **p = 0.0069**. Es lo mismo que da `t.test` de R.

Hasta 10c, con 1.96 y la normal, salía IC 0.46 a 1.91 y p = 0.0013: unas cinco veces «más significativo». Si los días fueran seguidos y se parecieran (ρ ≈ 0.4), el intervalo se ensancharía todavía más (6.15.2).

### 6.13.5 Cuándo falla (H-5-3, alta)

- **Resuelto en 10c (D.66):** hasta esa entrega no quitaba la tendencia ni descontaba la autocorrelación, así que sus *p* eran, para variables persistentes, mucho más pequeños de lo que debían (Figura 6.2: con autocorrelación 0.8, alrededor de un tercio de falsas alarmas), y la misma pareja podía verse **punteada** en la lista y con **✓** aquí; usaba ρ donde la lista usa r, y para 0/1 usaba 1.96 con apenas 5 días por grupo (H-5-5). Ahora usa la misma maquinaria que la lista.
- **Lo que sigue distinto de la lista:** el *q* se calcula entre las variables revisadas contra **este** objetivo, no entre todas las parejas; la autocorrelación de cada variable se calcula con toda su serie. Por eso una pareja puede tener ✓ aquí y salir punteada en la lista (o al revés) sin contradicción: se corrigió por menos comparaciones. **[Aproximación]**.
- Para 0/1 se comparan los **restos del objetivo** entre días con y sin, pero la variable 0/1 no se «desestaciona»: si casi todos sus 1 caen en una sola temporada, la comparación hereda esa temporada en parte (los restos ya quitan lo lento del objetivo).
- Desde la entrega 10d, `vm_factores` en R hace lo mismo (P-17 y P-32; desde 10e, con el 0.3 cuando la autocorrelación no se puede estimar y con la opción `sin_semana`, 6.5.6): las mismas piezas (`.vm_sin_tendencia`, `.vm_ar1`, `.vm_dif_dias`, con la corrección de Marriott y Pope y los grados de Welch con n efectivo), las mismas variables y las mismas obvias; `ExportacionPrueba.factoresComoR` fija que da los mismos números que la app con datos inventados (Apéndice C).

## 6.14 Modelo múltiple

### 6.14.1 Qué hace

Pregunta por **varias causas posibles a la vez**: ¿el sueño se asocia con el ánimo **manteniendo fijos** los pasos y la pantalla? Opcionalmente incluye **el valor del objetivo el día anterior**.

### 6.14.2 Cómo exactamente (`Analisis.modelo`)

1. Toma los días con objetivo y **todos** los predictores (y, si `conAyer`, el objetivo del día de calendario anterior); descarta los demás.
2. Exige **n ≥ k + 10**, con *k* el número de predictores (incluido «ayer»). Con 3 predictores y ayer, 14 días.
3. **Estandariza** cada columna y el objetivo: resta su media y divide entre su DE (si alguna DE es 0, no hay modelo).
4. Resuelve mínimos cuadrados **sin intercepto** (todo está centrado): β = (X′X)⁻¹ X′y, invirtiendo con Gauss-Jordan con pivoteo parcial; si un pivote es menor que 10⁻¹⁰ (predictores redundantes), no hay modelo.
5. Error: s² = SSE / (n − k − 1); EE(β_j) = √(s² · [(X′X)⁻¹]_jj); intervalo β ± **t(0.975; n − k − 1)** · EE (desde 10c; antes, 1.96).
6. R² = 1 − SSE / SST, con SST = Σ y² (y estandarizada), y el **R² ajustado** = 1 − (1 − R²)·(n − 1)/(n − k − 1), que es el que muestra la pantalla («varianza explicada (ajustada)», con 0 como mínimo).

La pantalla permite hasta **6** predictores; marca ✓ si el intervalo no incluye el cero; interpreta |β| de 0.1 como pequeño, 0.3 moderado y 0.5 grande.

### 6.14.3 Razonamiento

Los coeficientes estandarizados permiten comparar predictores en unidades distintas (horas, pasos, minutos). Controlar por el valor de ayer reconoce que el ánimo tiene inercia: sin eso, cualquier cosa que coincida con «rachas» parecería un efecto. Es la idea de los modelos autorregresivos con predictores, el estándar en estudios diarios intensivos (Hamaker y Wichers, 2017). **[Aproximación]**: es un modelo autorregresivo simple, no uno con errores autocorrelados ni multinivel.

### 6.14.4 Ejemplo trabajado (sintético)

59 días completos; objetivo valencia; predictores horas de sueño, pasos y valencia de ayer (k = 3; 55 grados de libertad, t = 2.004). Resultado: β sueño = +0.38 [0.14, 0.62] ✓; β pasos = +0.09 [−0.15, 0.33]; β ayer = +0.41 [0.17, 0.65] ✓; R² = 27 % y R² ajustado = 1 − 0.73 × 58/55 = **23 %**, que es lo que se muestra. Lectura: un día con una DE más de sueño (≈ 1 h en estos datos) se asocia con 0.38 DE más de valencia, a igual valor de ayer y pasos; los pasos no añaden nada claro. Con 1.96 (antes de 10c) los intervalos eran apenas más estrechos ([0.15, 0.62], [−0.14, 0.32], [0.18, 0.65]): con 59 días la diferencia entre t y la normal es pequeña. Con pocos días no: en el mínimo que admite el modelo (n = k + 10) siempre quedan 9 grados de libertad, t = 2.262, y el intervalo con 1.96 sale un 13 % más estrecho (1 − 1.96/2.262); con 14 días y un solo predictor (12 grados, t = 2.179), un 10 %.

### 6.14.5 Cuándo falla (H-5-11)

- **Resuelto en 10c (D.66):** el intervalo usaba 1.96 en vez de t con n − k − 1 grados (en el mínimo de días quedaba un 13 % más estrecho, 6.14.4); el R² no estaba ajustado; y el interruptor decía «Controlar por el **ánimo** del día anterior» aunque agrega el **objetivo** de ayer. Ahora dice «Controlar por el valor del día anterior».
- Solo días **completos**: si un predictor falta mucho, el modelo usa pocos días y quizá no representativos.
- No quita tendencias, y el error estándar supone residuos independientes (el valor de ayer como predictor reduce, pero no elimina, la autocorrelación de los residuos).
- En R, `vm_modelo_ar1` agrega errores AR(1) pero no tiene la opción del valor de ayer (Apéndice C).

## 6.15 Tendencia, cambio reciente y día de la semana

### 6.15.1 Medias móviles (`Analisis.movil`)

Para cada día del calendario entre el primero y el último con dato, promedia los valores de **ese día y los `ventana − 1` anteriores** que existan; el punto se dibuja solo si hay **al menos max(2, ventana/3)** días con dato (división entera: 2 para la de 7 días, 10 para la de 30). Es una media **hacia atrás**: se «retrasa» frente a los cambios, a diferencia de la media centrada que se usa para quitar la tendencia. La gráfica muestra el último año, con líneas solo entre días consecutivos. `vm_movil` en R es idéntica. **[Diseño propio]**, técnica estándar.

![Figura 6.5. Valencia sintética de 200 días: cada día falta con probabilidad 0.18 y además se fuerza un hueco de 17 días (sombreado), que con dos faltas al azar pegadas queda en 19 días seguidos; en total faltan 55 de los 200 días (27.5 %). Medias móviles de 7 y 30 días con sus mínimos de días. Script: `figuras/fig11_medias_moviles.py` (replica `Analisis.movil`).](figuras/fig11_medias_moviles.png){width=100%}

### 6.15.2 Diferencia de medias (`Estadistica.diferenciaCompleta` y `diferenciaEnDias`)

**Qué hace.** Compara el promedio de dos grupos de días (los últimos 30 contra los 30 anteriores; los días con algo contra los días sin) y dice qué tan seguro es que la diferencia no sea azar. Desde la entrega 10c (D.65) tiene en cuenta dos cosas que antes ignoraba: que con pocos días la incertidumbre es mayor de lo que dice la curva normal, y que los días seguidos se parecen.

**Cómo exactamente.** Con al menos 3 valores por grupo (B.11 y B.11a):

1. Medias y varianzas muestrales (con n − 1) de cada grupo.
2. **Autocorrelación de cada grupo** (`Estadistica.ar1Grupos`, solo en `diferenciaEnDias`, que recibe los días con su fecha): la de orden 1 del grupo (`ar1`: Pearson entre cada día y el siguiente, solo con días seguidos, si hay al menos 10 pares); si el grupo no tiene 10 pares, la de los dos grupos juntos, cada uno centrado en su media; si tampoco, 0. Como en series cortas la autocorrelación estimada sale **más baja** que la real, se corrige: ρ̂ + (1 + 4ρ̂)/n, con n los días del grupo (o de los dos juntos). Al final se acota a **[0, 0.9]**: solo corrige hacia arriba (una autocorrelación negativa no estrecha el intervalo).
3. **Error estándar con el factor AR(1)**: el de cada grupo se multiplica por √((1 + ρ)/(1 − ρ)) (`Estadistica.factorAR1`):

$$EE=\sqrt{\frac{s_a^2}{n_a}\,\frac{1+\rho_a}{1-\rho_a}+\frac{s_b^2}{n_b}\,\frac{1+\rho_b}{1-\rho_b}}\qquad\qquad (6.6)$$

4. **Grados de libertad de Welch** con el número efectivo de días de cada grupo, n·(1 − ρ)/(1 + ρ) (mínimo 2), acotados entre 1 y la suma de esos números menos 2.
5. **Intervalo**: d ± t(0.975; gl) · EE, con `Estadistica.cuantilT`; **p** de dos colas con `Estadistica.pT`. Los dos salen de la distribución t exacta (sección 6.7 y B.14a).

Sin fechas (`diferencia(a, b)` con ρ = 0), es la prueba t de Welch (1947) clásica. La usan «Tendencia» (`cambio`), «Todo lo que se mueve con algo» (variables 0/1), «Antes y después» y los experimentos (capítulo 8). **[Validado]** la t de Welch; **[Aproximación]** el factor AR(1) (supone que el parecido entre días decae de un día a otro de forma geométrica) y la corrección de sesgo.

**Por qué la corrección de sesgo.** La fórmula ρ + (1 + 4ρ)/n es la aproximación de primer orden al sesgo de la autocorrelación de orden 1 cuando la media se estima, que se suele atribuir a Marriott y Pope (1954). **No se pudo leer el artículo original en la sesión** [sin verificar]; la corrección se justificó por simulación (abajo). **[Aproximación]**.

**Ejemplo trabajado 1 (solo la t).** a = 1, 2, 3 y b = 4, 5, 6. d = −3; EE = √(1/3 + 1/3) = 0.816; Welch da 4 grados de libertad y t = 2.776. IC: −3 ± 2.776 × 0.816 = **−5.27 a −0.73**; p = 0.021. Con 1.96 (antes de 10c) salía −4.60 a −1.40: el intervalo con t es un 42 % más ancho.

**Ejemplo trabajado 2 (días que se parecen).** Los últimos 30 días tienen media 4.6 y los 30 anteriores 4.0, ambos con DE 1.0; la autocorrelación estimada de cada grupo es 0.45.

- Sin corregir: EE = √(1/30 + 1/30) = 0.258; con 1.96, IC **0.09 a 1.11**: «El cambio es claro».
- Corrección de sesgo: 0.45 + (1 + 4 × 0.45)/30 = **0.543**. Factor √(1.543/0.457) = 1.838; días efectivos por grupo 30 × 0.457/1.543 = 8.9.
- EE = 0.258 × 1.838 = 0.475; gl = 15.8; t = 2.123. IC: 0.6 ± 2.123 × 0.475 = **−0.41 a 1.61**: «podría ser variación normal».

**Comprobación por simulación** (prueba `CalculosPrueba.coberturaConDiasParecidos` y simulación de la entrega 10c, con 2 000 réplicas sin cambio real): con 30 + 30 días y ρ = 0.5, el intervalo «de 95 %» cubre el cero el **74 %** de las veces sin corregir, el **92 %** con el factor AR(1) pero sin la corrección de sesgo, y alrededor del **95 %** con las dos (la prueba exige entre 93 y 97 %). Con 3 + 3 días independientes, la t de Welch cubre el **96 %** (con 1.96, el 88 %).

### 6.15.3 Cambio reciente (`Analisis.cambio`)

Compara los **últimos 30 días** (fechas posteriores a hoy − 30 y hasta hoy) con los **30 anteriores** (de hoy − 60, exclusive, a hoy − 30, inclusive) con `diferenciaEnDias` (6.15.2). Si el intervalo no incluye el cero: «El cambio es claro».

### 6.15.4 Día de la semana (`Analisis.porDiaSemana`)

Para cada día de la semana con **al menos 5** valores (desde 10c; antes, 2): media ± t(0.975; n − 1) · DE/√n. Con 5 valores el multiplicador es 2.776; con 20, 2.093. Dice «Los intervalos no se enciman: la diferencia parece real» si el límite inferior del mejor día supera el superior del peor. No corrige por autocorrelación: los lunes no son días seguidos entre sí, así que el parecido de un día al siguiente casi no los afecta (sí el de una semana a la siguiente).

### 6.15.5 Antes y después (`EtapasDeVida` en Explorar.kt)

Para cada uno de los últimos 8 días con cambios guardados en «Conocerte» (desde 10c, los cambios de un mismo día van en una sola tarjeta y el primer llenado no sale), compara los **60 días anteriores** (fecha − 1 a fecha − 60) con los **60 siguientes** (fecha a fecha + 59) en valencia, sueño, pantalla, pasos, gasto, personas mencionadas y palabras, con `diferenciaEnDias`. El diseño (estudio observacional, no experimento) se discute en el capítulo 8.

### 6.15.6 Cuándo fallan (H-5-5)

**Resuelto en 10c (D.65, D.66):** hasta esa entrega las tres comparaciones trataban cada día como **independiente** y usaban 1.96 fijo; con «Día de la semana» bastaban **n = 2** por día, y con 2 valores el multiplicador correcto de t es 12.7, no 1.96 (H-5-5). Lo que sigue:

- La corrección AR(1) supone que el parecido entre días decae geométricamente; si un periodo entero es distinto (un mes de vacaciones dentro de los 30 días), la autocorrelación de orden 1 no lo captura del todo. **[Aproximación]**.
- Con menos de 10 pares de días seguidos en los dos grupos juntos, ρ = 0 y solo queda la t. El 0.3 que desde 10e se supone en las correlaciones (6.6.6) aquí no se usa (D.95).
- «Antes y después» compara periodos que también difieren en estación del año y en todo lo demás que cambió a la vez (la app lo advierte: «Muchas cosas cambian a la vez; tómalo como una pista»).

## 6.16 Dinámica emocional

### 6.16.1 Qué hace

Resume **cómo se mueve** tu ánimo, no solo su nivel.

### 6.16.2 Cómo exactamente (`Analisis.dinamica`)

Usa la `valencia` del diario (1 a 7); exige al menos **7** días.

- **Media** y **DE** (variabilidad).
- **MSSD** (inestabilidad; B.12): promedio de los cuadrados de las diferencias entre **días consecutivos** de calendario con ambos datos; exige al menos 3 pares. Si faltó un día, ese salto no cuenta.
- **Inercia** (B.13): Pearson entre el valor de cada día y el del día siguiente; exige al menos **10** pares.
- **Intradía**: el promedio, entre días con al menos 2 registros de ánimo con valencia (avisos y propios), de la DE de esos registros.
- **% de días muy difíciles**: días con valencia **1 o 2** (≤ 2). Desde 10e (D.103) la tarjeta los llama «muy difíciles»; antes, «difíciles».
- **Racha buena**: la más larga de días seguidos con valencia ≥ 5. **Racha dura**: la más larga con valencia **≤ 3**.
- **Por mes**: media, DE y MSSD (pares dentro del mismo mes, con al menos 3); la app no da inercia por mes.

### 6.16.3 Razonamiento científico

La DE, el MSSD y la autocorrelación son los índices clásicos de la dinámica afectiva. El MSSD (von Neumann et al., 1941) mide la inestabilidad **de un momento al siguiente**, que la DE no distingue: dos personas con la misma DE pueden cambiar suave o bruscamente (Jahng et al., 2008). La inercia, la tendencia del ánimo a quedarse donde estaba, se ha asociado con malestar psicológico (Kuppens et al., 2010). **[Validado]** como índices. Dos advertencias de la literatura: el MSSD mezcla variabilidad e inercia (aproximadamente MSSD ≈ 2 · DE² · (1 − inercia)), y en muestras grandes estos índices añadieron poca información sobre el bienestar más allá de la media y la DE (Dejonckheere et al., 2019). Además, la literatura los define sobre mediciones **momentáneas** varias veces al día; aquí se calculan sobre **un valor por día** recordado por la noche. **[Aproximación]**.

### 6.16.4 Ejemplo trabajado

Siete días seguidos: 4, 5, 3, 3, 6, 5, 4.
Media = 4.29; DE = 1.11.
Diferencias: +1, −2, 0, +3, −1, −1 → cuadrados 1, 4, 0, 9, 1, 1 → **MSSD = 16/6 = 2.67**.
Inercia: no se calcula (6 pares < 10).
Días muy difíciles (1 o 2): 0 %. Racha buena (≥ 5): 2 días (6, 5). Racha dura (≤ 3): 2 días (3, 3).

### 6.16.5 Cuándo falla (H-5-12)

- **Resuelto en 10e (D.103, D.106).** Hasta 10e, «días difíciles» usaba ≤ 2 y «racha dura» ≤ 3: dos definiciones de «difícil» en la misma tarjeta. Ahora tienen nombres distintos: «muy difíciles» (1 o 2) y «racha dura» (3 o menos). Y `vm_dinamica` en R emparejaba el último día de un mes con el primero del siguiente; ahora cada mes usa solo sus propios pares, como la app (sección 6.22).
- Con días faltantes no aleatorios (dejar de escribir los días malos, capítulo 3), el MSSD y la inercia se calculan con los días que quedan, que son los más estables.

## 6.17 Palabras del diario

**Qué hace.** Muestra las palabras de tus días mejores y más difíciles, y las 25 más frecuentes.

**Cómo exactamente (`Analisis.palabras`).** Solo días con nota. Toma las secuencias de **3 o más letras** (en minúsculas), quita las palabras vacías de `Analisis.VACIAS` (167 escritas, muchas en dos formas como «aqui» y «aquí»: 146 distintas sin acentos, que es como se comparan) y, para cada palabra, los **días distintos** en que aparece. Si aparece en al menos **5** días con valencia, calcula la media de la valencia de esos días y su diferencia con la media **de los días con nota y valencia**. Muestra las 15 de mayor y menor diferencia (la pantalla, 10).

**Ejemplo.** La media de tus días con nota es 4.4. «Playa» aparece en 6 días con valencias 6, 5, 7, 6, 5, 6 (media 5.83): +1.43.

**Razonamiento y límites (H-5-10).** Es un análisis de **contenido por conteo** sin diccionario de emociones (a diferencia de LIWC, Tausczik y Pennebaker, 2010) ni prueba estadística. **[Diseño propio]**. Con cientos de palabras y 5 días como mínimo, las de los extremos de la lista son, en buena parte, las que tuvieron suerte (regresión a la media): con 5 días, la media de valencia varía ±0.9 puntos solo por azar si tu DE es 1. No hay intervalo ni q. Las palabras de días malos pueden reflejar **lo que escribes** cuando estás mal más que **lo que te pone mal**. Hasta la entrega 10d, `vm_palabras` en R usaba otra lista de vacías (con acentos) y otra referencia (la media de **todos** los días); desde 10d usa la misma lista de 167 palabras vacías, comparadas sin acentos y la misma referencia (los días con nota), así que da las mismas diferencias. Lo de la incertidumbre sigue igual en las dos.

## 6.18 Días atípicos

**Qué hace.** Lista los días del **último año** que se salieron de lo normal en alguna variable, con la razón.

**Cómo exactamente (`Analisis.atipicos`).** Revisa 15 variables: `valencia`, `sueno_horas`, `pasos`, `pantalla_min`, `desbloqueos`, `gasto_total`, `notificaciones`, `fotos`, `distancia_km`, `min_hablado`, `wa_mios`, `musica_min`, `fc_reposo`, `hrv`, `palabras`. Para cada una con al menos **20** días: mediana *M* = el valor en la posición n/2 de los datos ordenados (con n par, el **de arriba** de los dos centrales), MAD = la mediana (misma regla) de |x − M| multiplicada por **1.4826**, y z robusto (B.14):

$$z_i=\frac{x_i-M}{1.4826\cdot \mathrm{mediana}(|x-M|)}\qquad\qquad (6.7)$$

Marca el día si |z| ≥ **3** («muy alto» o «muy bajo»). Si MAD = 0 (más de la mitad de los días tienen el valor de la mediana), salta la variable. Devuelve los días desde el parámetro `desde`, que por omisión es hoy − 365. «Tu año» usa umbral 2.8 y, desde 10e (D.100), `desde = null`: le pasa solo los días del año elegido y se queda con todos.

**Razonamiento.** La media y la DE se dejan arrastrar por los propios atípicos; la mediana y la MAD no. El factor 1.4826 hace que la MAD equivalga a la DE si los datos fueran normales, y |z| ≥ 3 es un umbral común y conservador (Leys et al., 2013). **[Validado]**.

**Ejemplo.** 20 días de pasos: mediana (superior) 8 200; MAD × 1.4826 = 1 038; un día con 2 100 pasos da z = (2100 − 8200)/1038 = **−5.9**: «Pasos muy bajo». (R, con la mediana promediada, daría 8 150 y 964; z = −6.3.)

**Cuándo falla.** Hasta la entrega 10c la lista pedía `km`, que **no existe** en el panel de la app, así que la distancia nunca se revisaba y los viajes no salían como atípicos (H-5-8, **resuelto en 10c, D.72**: ahora es `distancia_km`). Las variables muy concentradas (gasto, muchos días en 0) tienen MAD = 0 y se saltan siempre. Hasta 10e, «el día más raro» de «Tu año» también pasaba por el filtro de los últimos 365 días contados desde hoy, así que un año pasado salía vacío o incompleto, y el mismo año daba otra respuesta según el día en que se mirara (H-14-3, **resuelto en 10e, D.100**).

## 6.19 Ritmo de sueño (lo estadístico)

Los modelos de sueño (jet lag social, noches aproximadas por el teléfono) se explican en el capítulo 5. Desde lo estadístico basta saber que `Analisis.ritmo` usa **promedios simples** de horas decimales (con la madrugada sumada 24 h para no promediar 23:30 con 0:30 como 12:00), una DE de la hora de dormir como «regularidad» y la diferencia absoluta de los puntos medios entre noches antes de días libres y laborales; exige 5 noches en total y 2 de cada tipo. Desde la entrega 10c (D.69) usa solo las noches de los **últimos 56 días** (`Analisis.recientes`) y una sola regla de día libre (`Fechas.esDiaLibre`: sábado, domingo o, si vives en México, festivo). `vm_ritmo(dias = 56)` en R hace lo mismo, con la columna `dia_libre` del panel, pero **solo con el reloj** (no aproxima con el teléfono) y filtra con 2 < duración < 16 h (la app incluye los extremos 2 y 16).

## 6.20 Cifras descriptivas: «Tus números», «Totales» y los reportes

Estas funciones no prueban nada; resumen. Importan porque sus cifras se comparan con recomendaciones de salud y entre periodos.

### 6.20.1 «Tus números» (`TusNumeros`)

Para cada medida: el **promedio de los últimos 30 días que tengan dato** dentro del último año (función `ultimos`), con las fechas desde y hasta; se considera «reciente» si el último dato tiene 45 días o menos. Algunas conversiones: km al año = km por día × 365; ejercicio a la semana = la suma de los minutos de las sesiones de los 28 días que terminan en la última sesión, entre 4 (desde 10c); días de fuerza por semana = entrenamientos en los 28 días que terminan en el último entrenamiento / 4; personas al día = el máximo entre `personas`, `contactos` y `wa_chats`; tazas de café (en reportes) = mg de cafeína / 95.

**Cuándo falla.** Hasta la entrega 10c el ejercicio semanal era la media de los días **con** sesión multiplicada por 7, lo que suponía que hacías ejercicio todos los días: con 3 sesiones de 45 min por semana daba 45 × 7 = **315 min/semana** en lugar de 135 (H-5-9, **resuelto en 10c, D.72**: ahora 4 semanas × 3 × 45 = 540 min, entre 4 = **135**). Queda que, si dejaste de hacer ejercicio hace meses, la cifra es la de tus últimas 4 semanas con sesiones, con sus fechas (como los días de fuerza). Además, «aire libre» usa `luz_alta_min` (solo la luz del teléfono) y no `aire_libre_min` (el máximo entre luz y movimiento, capítulo 4).

### 6.20.2 «Totales» (`Totales.calcular`)

Suma cada medida en un periodo y dice **desde qué fecha** hay datos de cada fuente, el récord y su fecha; arma índices buscables (alimentos, personas, artistas, lugares, ejercicios…). Excluye los momentos privados. Es pura aritmética. Desde la entrega 10c (D.77) cuenta gastos e ingresos por el día del diario (`dia`, de 5:00 a 5:00), como el panel; antes usaba la fecha de calendario.

### 6.20.3 Reportes y «Tu año» (`Periodo.calcular`)

Calcula todo lo de una semana, mes o año y lo compara con el **periodo anterior de la misma duración** (exige 3 días con dato en el anterior). Desde la entrega 10c (D.77): la valencia de un día es **solo la del diario** (`valDe`), como `valencia` en el panel y en R, y el reporte dice cuántos días tenían ánimo solo en los avisos («N días solo con avisos no cuentan»); hasta 10c, si no había diario usaba el promedio de los registros de ánimo, mezclando el «yo que recuerda» con el «yo que experimenta» (capítulo 3). La hora media de dormir usa `Exportar.horaDecimal` (suma 24 antes de las 12:00, en la zona actual), como el panel; antes sumaba 24 antes de las 15:00 y leía la hora escrita en el texto. Gastos e ingresos van por día lógico (H-5-15, resuelto). Nunca usa lo privado ni el texto de las cartas.

**«Parecido al periodo anterior» (desde 10e, D.101; H-8-11).** En ánimo, sueño, pasos y pantalla, el reporte calcula el error típico de la diferencia entre los dos promedios, √(s₁²/n₁ + s₂²/n₂) (`Periodo.errorDif`, con al menos 3 días en cada periodo; s², la varianza de los días con n − 1). Si la diferencia es menor que ese error, dice «parecido al periodo anterior (la diferencia cabe en lo que varían tus días)» en vez de «+x frente al periodo anterior» (`Reportes.cambio`); si es casi nula (menos de 0.05, o de 0.5 en las cifras enteras), sigue diciendo «igual que el periodo anterior». **Ejemplo:** un mes con valencia media 4.6 contra 4.3 del anterior, los dos con 30 días y DE 1.2: error = √(1.44/30 + 1.44/30) = 0.31; la diferencia, 0.3, es menor: «parecido». **[Diseño propio]** el umbral de un error típico (no es una prueba con un nivel de confianza); **[Aproximación]** el error mismo, que no descuenta que los días seguidos se parecen (6.6), así que se queda corto y todavía llama «cambio» a algunas diferencias que son variación normal.

## 6.21 Potencia: por qué 14, 30 y 90 días

### 6.21.1 Qué dice la app

- La lista de relaciones exige **14** días (8 semanas por semana).
- «Cruzar dos variables» dice que hacen falta «unos 30 para empezar a creerle», y la pantalla «Cobertura de variables» cuenta cuánto falta para **30** días por variable.
- La pantalla de Análisis dice, desde la entrega 10c (D.76): «Con 90 días o más ya se puede detectar una relación moderada en una pareja concreta; en esta lista, que revisa todas a la vez, solo las fuertes» y, antes de los 90, «Faltan N días para tener potencia suficiente». Hasta 10c decía solo «Con 90 días o más ya se pueden detectar relaciones moderadas».

### 6.21.2 Qué significa «potencia»

La **potencia** es la probabilidad de detectar una relación que **sí existe**. Con pocos días, aunque haya relación, la prueba casi nunca la encuentra.

### 6.21.3 Cálculo (B.15)

Con la aproximación de Fisher, la correlación más pequeña que se detecta con potencia de 80 % y α = 0.05 bilateral es

$$|r_{\min}|=\tanh\!\Big(\frac{1.96+0.84}{\sqrt{n_{Ef}-3}}\Big)\qquad\qquad (6.8)$$

| Días | Días independientes | Autocorrelación 0.5 en ambas (nEf = 0.6 n) | Igual, con α = 0.001 |
|---|---|---|---|
| 14 | 0.69 | 0.84 | 0.94 |
| 30 | 0.49 | 0.62 | 0.79 |
| 90 | **0.29** | 0.37 | 0.52 |
| 180 | 0.21 | 0.27 | 0.38 |
| 365 | 0.15 | 0.19 | 0.27 |

: Tabla 6.6. Cálculo (B.15)

Así, la frase de los 90 días es **correcta para una sola relación con días independientes**: con 90 días se detecta |r| ≈ 0.29, el límite entre «débil» y «moderada» de Cohen. **[Validado]** en ese sentido estricto. Pero en «Las relaciones más marcadas» los días no son independientes y se revisan miles de parejas, así que el umbral efectivo se parece más a la última columna: con 90 días solo se sostienen relaciones de 0.5 o más. Los 14 días son un **mínimo para calcular**, no para creer. Los 30 días son una convención razonable para «pista». **[Diseño propio]**.

![Figura 6.6. La correlación más pequeña detectable con potencia de 80 % según los días, con días independientes, con autocorrelación 0.5 y con un umbral estricto como el que imponen miles de comparaciones. Script: `figuras/fig16_potencia.py` (cálculo, sin datos).](figuras/fig16_potencia.png){width=100%}

### 6.21.4 Cuándo falla (H-5-16)

El aviso de los 90 días cuenta **días con diario** (`Repo.dias.size`), no días con ambos datos de cada pareja. Desde 10c sí dice que en la lista completa solo se sostienen las relaciones fuertes (H-5-16, resuelto en 10c con la primera opción de su propuesta: matizar el texto), aunque no menciona la autocorrelación y sigue contando días con diario.

## 6.22 App y R, función por función

El script `vestigium.R` reproduce casi todas las herramientas. Desde la entrega 10d (P-30 a P-32) analiza el mismo panel, con las mismas variables y las mismas obvias, y repite las correcciones de 10c en «Todo lo que se mueve», el día de la semana y las palabras. Desde 10e (D.95, D.97, D.106) también «Cruzar dos variables», la dinámica por mes, el 0.3 de autocorrelación y los experimentos. La tabla dice dónde coinciden y dónde no. El detalle de cada función está en el Apéndice C.

| Análisis | App | R | ¿Mismo resultado? |
|---|---|---|---|
| Datos | `Estadistica.panel()` | Desde 10d, `vm_panel_extendido.csv` (las columnas de la app más las de `vm_panel.csv`, que en lo compartido llevan el valor de la app); variables de `vm_variables.csv` | **Sí** en las variables que la app cruza; quedan 11 nombres dobles documentados y el calendario completado (6.3.4; H-5-1 resuelto) |
| Relaciones | `relaciones`: Pearson, ±10 d, lentas < 0.12, nEf (0.3 si una autocorrelación no se puede estimar, desde 10e), BH, orden por «seguro» | `vm_relaciones`: el **mismo algoritmo** (mismas cotas, mínimos y orden; p con `pnorm` exacto; el 0.3 desde 10e); además, `sin_semana = TRUE` quita el ritmo de la semana (desde 10e; la app no puede) | **Sí** desde 10d: mismo panel, mismas variables y mismas obvias (`vm_familias.csv`; H-5-2 resuelto); R ya no cruza calendario ni cobertura (`.vm_no_cruzar`: `fin_de_semana`, `dia_libre`, `festivo`, `con_diario`, `n_muestreos`…). Probado con datos inventados; con una exportación real, el autor comprobó que las primeras parejas coinciden con la pantalla. No hay modo semanal en R. |
| Cruzar | Pearson + Spearman; IC de Pearson con nEf; r sin tendencia al lado (desde 10c) | Desde 10e, `vm_cruce` (y `vm_presion` para su pareja): r de Pearson con IC y p por nEf, r sin tendencia (±10 días) y ρ de Spearman (`.vm_cruce_resumen`); hasta 10e, Spearman con `cor.test` y n bruto | **Mismo cálculo** desde 10e en los días (H-5-7, resuelto en parte), comprobado con CSV sintéticos, sin una prueba JVM que lo fije; R no tiene el modo por semana y da además el p |
| Factores | Desde 10c: restos sin tendencia (interruptor), r de Pearson con nEf; 0/1 con t de Welch y factor AR(1); mínimos 10 días (5 y 5) / 14 | `vm_factores` desde 10d: lo mismo (`sin_tendencia`, r de Pearson con `n_ef`; 0/1 con `.vm_dif_dias`: Welch, factor AR(1), Marriott y Pope, `qt`); mismos mínimos; obvias de `vm_familias.csv` | **Sí** (fijado por `ExportacionPrueba.factoresComoR` con datos inventados) |
| Modelo | MCO estandarizado sin intercepto; IC con t(n − k − 1); R² ajustado (desde 10c) | `vm_modelo`: `lm` sobre datos estandarizados; inferencia con t | Mismos β e intervalos desde 10c (los dos usan n − k − 1 grados; R reporta el R² sin ajustar y el ajustado) |
| Modelo con AR(1) | — | `vm_modelo_ar1`: `nlme::gls` con `corAR1(~ t)` (Pinheiro et al., 2026) | Solo en R |
| Tendencia | `movil` | `vm_movil` | Igual |
| Día de la semana | n ≥ 5 por día; t(n − 1) EE (desde 10c) | `vm_semana` desde 10d: n ≥ 5 por día; t(n − 1) EE | Igual |
| Dinámica | Valencia; mínimos 7 días, 3 y 10 pares; meses sin cruzar | `vm_dinamica` desde 10e: cualquier variable; los mismos mínimos (7 días, 3 pares para el MSSD, 10 para la inercia); cada mes solo con sus pares; da también la inercia de cada mes (con 10 pares) | Igual en el total y en el MSSD por mes; la inercia por mes solo en R |
| Ritmo | Reloj o teléfono; últimos 56 días; `Fechas.esDiaLibre` (desde 10c) | `vm_ritmo(dias = 56)`: solo reloj; lee `dia_libre` del panel (desde 10c) | Igual con reloj |
| Palabras | 167 vacías (146 sin acentos), comparadas sin acentos; referencia: días con nota | Desde 10d, las mismas vacías, sin acentos; referencia: días con nota | Igual; R devuelve todas las palabras, la app los 15 extremos |
| Atípicos | 15 variables (con `distancia_km` desde 10c); mediana superior; ≥ 20 días; último año («Tu año», desde 10e: todo el año elegido) | Desde 10d, las mismas 15 variables; `median`/`mad` de R; todos los días | Parecido; distinto número de días y mediana para n par |
| Experimentos | `Experimentos.resultado` (capítulo 8): Welch con error corregido, g de Hedges, tendencia, cumplimiento; desde 10f, el criterio sellado (`Experimentos.criterio`) y la huella (`intacto`) | Desde 10e, `vm_experimento(id)`: lo mismo, más una prueba de aleatorización sobre todos los planes del sorteo (Apéndice C); desde 10f, `criterio` con la misma regla, y `intacto` tal como lo exportó la app | Mismo cálculo (comprobado con CSV sintéticos, sin una prueba JVM que lo fije); la prueba de aleatorización solo en R |

: Tabla 6.7. App y R, función por función

## 6.23 Qué es estándar y qué es propio

| Decisión | Marca |
|---|---|
| Pearson, Spearman con rangos promedio, cortes 0.1/0.3/0.5 | [Validado] |
| Transformación z de Fisher para intervalos y p | [Validado] |
| Φ por Abramowitz y Stegun 7.1.26 | [Validado] |
| Benjamini-Hochberg con q < 0.10 | [Validado] (procedimiento); [Aproximación] (pruebas dependientes) |
| nEf = n(1 − r_a r_b)/(1 + r_a r_b) | [Validado] como aproximación AR(1); cotas [−0.5, 0.97] y [4, n]: [Diseño propio] |
| Autocorrelación de 0.3 cuando no se puede estimar (desde 10e) | [Diseño propio] (valor prudente, no estimado) |
| Quitar tendencia restando la media de ±10 días | [Aproximación]; tamaño de ventana [Diseño propio] |
| Quitar el ritmo de la semana restando la media de los restos de cada día de la semana (solo R, `sin_semana`, desde 10e) | [Aproximación] |
| Variables lentas con var(resto)/var < 0.12 | [Diseño propio] |
| Lista de relaciones obvias | [Diseño propio] |
| Escribirla como familias con lados para R (`vm_familias.csv`) y no cruzar en R calendario ni cobertura (`.vm_no_cruzar`) | [Diseño propio] |
| Familias dinámicas, topes y mínimos de días | [Diseño propio] |
| Mínimos de 14 días, 4 de cada valor en 0/1, 8 semanas | [Diseño propio] |
| Diferencia de medias con t de Welch (desde 10c) | [Validado] |
| Factor AR(1) √((1 + ρ)/(1 − ρ)) en el EE y n efectivo por grupo | [Aproximación] (supone AR(1)); acotar ρ a [0, 0.9] y el respaldo con los dos grupos juntos: [Diseño propio] |
| Corrección de sesgo ρ + (1 + 4ρ)/n | [Aproximación] (atribuida a Marriott y Pope, 1954, sin leer el original; validada por simulación) |
| t de Student exacta por la beta incompleta (Lentz, Lanczos) | [Validado] (comprobada contra tablas) |
| Modelo estandarizado con el día anterior; t con n − k − 1 y R² ajustado | [Aproximación] (el modelo); [Validado] (la inferencia, si los residuos fueran independientes) |
| DE, MSSD, inercia | [Validado] como índices; [Aproximación] con un valor por día |
| «Parecido al periodo anterior» si la diferencia es menor que su error típico (reportes, desde 10e) | [Diseño propio] el umbral; [Aproximación] el error, sin autocorrelación |
| z robusto con MAD y umbral 3 | [Validado] |
| Palabras por diferencia de medias | [Diseño propio] |
| 90 días para relaciones moderadas | [Validado] para una prueba con días independientes; optimista para la lista |

: Tabla 6.8. Qué es estándar y qué es propio

## 6.24 Resumen del capítulo

1. Todo sale de un **panel** de un renglón por día. Desde la entrega 10c, casi todas las variables siguen una sola regla para distinguir «0» de «no sé»: 0 desde el primer registro y donde se pudo responder (`ceroDesde`; H-1-1, resuelto en 10c, D.63).
2. «Las relaciones más marcadas» es el análisis más cuidadoso de la app: **quita tendencias** (±10 días), **descuenta los días parecidos** (nEf) y **corrige por miles de comparaciones** (BH, q < 0.10). Una relación firme ahí es una pista seria, no una prueba de causa.
3. Desde 10c, «Todo lo que se mueve con algo» usa la misma maquinaria, y las diferencias de medias usan **t de Welch** con el error estándar corregido porque los días seguidos se parecen (H-5-3, H-5-5, resueltos en 10c, D.65 y D.66). Antes, la ficha podía marcar como firme lo que la lista llamaba azar.
4. **El ciclo semanal no se quita** al quitar la tendencia en la app; desde 10e la pantalla lo dice, y en R `sin_semana = TRUE` lo quita (H-5-6, resuelto en parte). También desde 10e, cuando la autocorrelación de una variable no se puede estimar se supone 0.3, no 0, así que las variables esporádicas ya no cuentan cada día como independiente (H-5-13, D.95).
5. Desde la entrega 10d, **R analiza el mismo panel que la app**, con las mismas variables y las mismas parejas obvias, y `vm_factores`, `vm_semana` y `vm_palabras` tienen las correcciones de 10c (H-5-1, H-5-2, resueltos): R sirve para verificar lo que muestra la app. Se comprobó con datos inventados; con una exportación real, el autor comprobó que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Desde 10e, `vm_cruce` da lo mismo que «Cruzar dos variables» (Pearson con nEf, r sin tendencia y Spearman), `vm_dinamica` empareja solo días del mismo mes y `vm_experimento` recalcula los experimentos con una prueba de aleatorización (y, desde 10f, el criterio sellado).
6. Con 90 días independientes se ve una relación de 0.3; con miles de parejas y días parecidos, hace falta más de 0.5. Paciencia: el sistema está pensado para años.


# Capítulo 7. Personas y red social

> **Para qué sirve este capítulo.** Explica **cómo la app registra a la gente de tu vida** y qué hace con eso: cómo reconoce a una persona cuando la nombras con @, cómo guarda qué es para ti (y cómo eso cambia con los años), cómo mide qué tan cerca la sientes, cómo dibuja la **Constelación** y la **Red**, qué calcula R con `vm_red()` y cómo entran las personas en las correlaciones. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10a…10e» y está en el Apéndice D. La entrega 10f (D.109 a D.118) no cambió nada de lo que trata este capítulo. **Las personas, fechas y números de los ejemplos son inventados.**
>
> **Cómo leerlo.** Igual que los capítulos anteriores: lenguaje sencillo, pero las cifras, umbrales y condiciones del código van tal cual, con el nombre de la función de donde salen. Cada decisión se marca como **[Validado]** (respaldada por literatura o un estándar), **[Aproximación]** (razonable, con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app, sin estándar detrás). Los problemas encontrados se numeran H-6-n (el número es el del módulo 6, que escribió este capítulo) y se detallan en `hallazgos.md`.
>
> **Privacidad.** El capítulo describe la mecánica; no hay nombres reales. Los momentos del seguimiento privado se mencionan solo para decir dónde entran y dónde no (sección 7.13).

## 7.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Mención** | Escribir `@nombre` en un texto (diario, momento, sueño, nota, carta). La app solo «ve» a una persona si está escrita así. |
| **Nombre canónico** | El nombre con el que la app cuenta a una persona después de aplicar las **equivalencias** (si dijiste que `@anita` es `@ana`, el canónico de `anita` es `ana`). |
| **Relación** | Qué es esa persona para ti (familia, pareja, amistad…). Se guarda con **historial**: la relación que tenía cada día. |
| **Grupo de relación** | La relación simplificada que usan los análisis: `familia`, `pareja`, `amistad`, `trabajo`, `casa` y, aparte, `expareja` y las demás (sección 7.4). |
| **Cercanía (IOS)** | Qué tan cerca sientes a alguien, de 1 a 7, eligiendo entre siete pares de círculos cada vez más encimados (Aron et al., 1992). |
| **Presencia** | Si ese día **estuviste con** la persona, solo **pensaste en** ella o hablaron por **mensajes** (capítulo 3, sección 3.13). |
| **Co-mención** | Que dos personas aparezcan mencionadas **el mismo día lógico**, aunque sea en textos distintos. Es la base de las líneas de la Constelación y de la Red. |
| **Red (grafo)** | Un dibujo de **nodos** (personas) unidos por **aristas** o **vínculos** (líneas). En matemáticas se llama *grafo*. |
| **Red egocéntrica** | La red vista desde una persona central, el **ego** (tú), con sus contactos (los **alteri**) y las relaciones entre ellos (Perry et al., 2018). Es el tipo de red de esta app. |
| **Grado** | Cuántas líneas tiene un nodo: con cuántas personas está unida. |
| **Densidad** | Qué fracción de todas las líneas posibles existe. Con *n* personas hay *n(n−1)/2* pares posibles. |
| **Componente** | Un grupo de nodos conectados entre sí por algún camino; dos componentes no tienen ninguna línea entre ellas. |
| **Restricción de Burt** | Una medida de qué tanto tus contactos **se conocen entre sí** (son redundantes). Baja = tus contactos están en mundos separados y tú eres el puente entre ellos (Burt, 2004; sección 7.11). |
| **Día lógico** | El día de la app: empieza a las 05:00 (capítulo 2). |

: Tabla 7.1. Mini-glosario

## 7.2 Mapa: de una @mención a cada salida

La misma persona puede «aparecer» de forma distinta según la función, porque **cada función lee textos distintos**. Esta tabla resume qué lee cada una (la diferencia importa: ver H-6-9).

| Función (archivo) | Diario (`nota`) | Momentos | Momentos privados (recaída, libido) | Sueño (texto y temas) | Mejor/peor momento | Notas del cronómetro | Cartas | Equivalencias |
|---|---|---|---|---|---|---|---|---|
| `Personas.menciones` (`data/Personas.kt`): lista de «Tu gente», «¿Quién es @x?», duplicados | sí | sí | no (desde 10a) | sí | no | sí | destinatario | sí |
| Variable `personas` del panel (`Estadistica.panel`) | sí | sí (desde 10e) | no | no | no | no | no | sí |
| `Presencias.delDia` → `personas_vistas`, `vi_*`, `penso_en_*`, `cercania_media`, `pen_*` | sí | sí | no | no | no | no | no | sí |
| Familia `per_` (`Dinamicas.construir`) | sí | sí | no (desde 10a) | sí (en los temas del sueño, solo los que llevan @, desde 10c) | sí | no | destinatario y texto | sí |
| Constelación (`Constelacion.calcular`) | sí | sí | no | no | no | no | no | sí |
| Red y exportación `vm_red_*` (`RedPersonas.calcular`) | sí | sí | no | no | no | no | no | sí |
| Tarjeta «personas en tu cielo este año» (`Vida.kt`) | sí | no | no | no | no | no | no | sí (desde 10c) |

: Tabla 7.2. Mapa: de una @mención a cada salida

«Momentos privados» son los de tipo `impulso`, `recaida` o `libido` (la propiedad `Momento.privado` en `data/Modelo.kt`, que desde el módulo 10a incluye el impulso). Ninguna fila de esta tabla los usa (sección 7.13). Hasta v84 el impulso no contaba como privado y entraba en todas las filas que dicen «Momentos».

## 7.3 Menciones con @ y equivalencias

### 7.3.1 Qué hace

La app no intenta adivinar nombres en el texto libre: **solo cuenta a una persona si la escribes con @**. Así evita confundir «Rosa» (persona) con «rosa» (color) y no necesita reconocer nombres propios.

### 7.3.2 Cómo lo hace exactamente

- **Reconocer la mención** (`Texto.personas`): busca la expresión regular `@([\p{L}\p{N}_]+)`, es decir, una arroba seguida de letras (de cualquier alfabeto, con acentos), números o guion bajo. El nombre se pasa a **minúsculas**. `@Ana`, `@ana` y `@ANA` son lo mismo; `@ana.` termina en el punto; `@ana_m` es distinta de `@ana`.
- **Nombre canónico** (`Texto.canon`): si el nombre está en la tabla de equivalencias, se cambia por su equivalente, y se sigue la cadena. Hasta la entrega 10c era **un solo paso**: si dijiste `anita → ana` y luego `ana → ana_g`, `anita` se contaba como `ana`, no como `ana_g`, y un ciclo `a → b`, `b → a` nunca unía a las dos (H-6-11). Desde 10c (D.73) `canon` sigue las uniones hasta **8 pasos**: `anita → ana → ana_g` da `ana_g`. Si la cadena vuelve a un nombre ya visitado (un ciclo), todos los nombres del ciclo cuentan como **el primero en orden alfabético** (`a → b` y `b → a`: los dos cuentan como `a`). Lo comprueba `CalculosPrueba.nombresEncadenados`. **[Diseño propio]**.
- **Detectar posibles duplicados** (`Personas.duplicados`): compara cada par de nombres mencionados y propone «¿son la misma persona?» si se cumple cualquiera de estas condiciones (`Texto.parecidos` y la regla de prefijo):
  1. son iguales al quitar los guiones bajos (`luis_m` y `luism`);
  2. ambos tienen 4 letras o más y su **distancia de Levenshtein** es ≤ 2 (cuántas letras hay que cambiar, agregar o quitar para pasar de uno a otro: `daniel` → `danie` es 1);
  3. ambos tienen 3 letras o más, uno empieza con el otro y la diferencia de largo es ≤ 3 (`ana` y `anita`).

  Muestra hasta 20 pares (la pantalla enseña 6). Puedes responder «misma: @a», «misma: @b» (`Personas.unir` agrega la equivalencia) o «son distintas» (`Personas.distintas`, que guarda el par para no volver a preguntar).
- **Pedir que clasifiques** (`Personas.pendientes`): la tarjeta «¿Quién es @x?» propone, de la más mencionada a la menos, a quien aparece **2 días o más**, no está clasificada en «Tu gente» ni en «Conocerte» y no marcaste «no preguntar».

**Razonamiento.** Marcar a mano es más fiable que reconocer nombres automáticamente, y las equivalencias resuelven apodos y errores de dedo sin reescribir el diario (lo escrito se conserva; solo cambia cómo se cuenta). **[Diseño propio]**, razonable.

### 7.3.3 Ejemplo

Durante un mes escribes `@Dani` 6 días y `@daniel` 3 días. Sin equivalencias, son dos personas: 6 y 3 días. Como `dani` y `daniel` cumplen las reglas 2 (distancia 2) y 3 (prefijo, diferencia de 2 letras), la app pregunta. Si eliges «misma: @dani», se guarda `daniel → dani` y desde ese momento todas las funciones cuentan **9 días** (o menos si algún día aparecen ambas: un día cuenta una vez).

### 7.3.4 Cuándo falla

- **Personas sin @**: si escribes «comí con mi mamá» sin `@`, la app no la ve. Todas las variables sociales cuentan **menciones marcadas**, no contactos reales.
- **Homónimos**: dos personas distintas con el mismo `@nombre` se cuentan como una. La solución es escribirlas distinto (`@ana_t`, `@ana_u`).
- **Equivalencias compartidas con los temas**: la misma tabla `Config.equivalencias` se usa para `@personas` y `#temas` (el código llama a `Texto.canon` con el mismo mapa en ambos casos). Una equivalencia `ana → ana_g` creada para personas también cambiaría un tema `#ana` (H-6-11, parte que sigue vigente). Es poco probable, pero posible.

## 7.4 Quién es cada persona: relaciones con historial

### 7.4.1 Qué hace

En «Tu gente» dices qué es cada @persona para ti. Una persona nace cuando la mencionas con @, cuando la pones en «Conocerte» o, desde la entrega N6 de la revisión de navegación, cuando la agregas a mano con «+ persona» (sin días hasta que la menciones). Si eso cambia (pareja → expareja), la app **guarda lo de antes** con su fecha, y los análisis usan **la relación que había cada día**.

### 7.4.2 Las opciones

`Personas.RELACIONES` ofrece 14 desde el esquema 17: familia, pareja, expareja, amistad, colega o compañero de trabajo, compañero de escuela, profesor/jefe/mentor, roomie, vecino, profesional de salud, conocido, «solo en línea o de lejos» (antes «no la conozco en persona», mismo id), mascota y otra. Hasta el esquema 16 eran 12 e incluían «amistad cercana». Esa opción ya no se ofrece porque la cercanía la dan los círculos 1 a 7; quien la tenía conserva su id y se ve como «amistad» (`Personas.relacionVisible`). Los análisis ya la juntaban con amistad (`Personas.grupo`), así que no cambian. Las relaciones nuevas forman su propio grupo: roomie no cuenta como familia aunque vivan juntos.

Para los análisis se **agrupan** (`Personas.grupo`):

| Relación guardada | Grupo |
|---|---|
| familia, casa | `familia` |
| pareja | `pareja` |
| amistad, amistad cercana | `amistad` |
| colega, compañero de escuela, mentor, trabajo | `trabajo` |
| expareja, conocido, lejana, mascota, otra | se quedan como están (`expareja`, `conocido`…) |

: Tabla 7.3. Las opciones

Además existe la sección «Conocerte» (capítulo 3), con **«a quién ves más»** (`perfil.cercanas`, cada una con relación pareja/familia/amistad/trabajo/otra) y **«quiénes viven contigo»** (`perfil.convivientes`).

**Qué relación se usa** (`Perfiles.relacion(persona, perfil, fecha)`), en este orden:

1. Si la persona está en «Tu gente», el **grupo** de la relación que tenía en esa fecha.
2. Si no, si está en «a quién ves más» de Conocerte, esa relación (o `otra` si no la dijiste).
3. Si no, si está en «quiénes viven contigo», `casa`.
4. Si no, ninguna (`null`): la persona cuenta en los totales, pero no en `vi_familia`, `vi_pareja`, etc.

Desde la entrega 10c (D.73), los pasos 2 y 3 comparan **nombres canónicos**: si en Conocerte escribiste `anita` y en el diario `@ana`, y las uniste, se reconocen (antes se comparaba el texto tal cual).

### 7.4.3 Cómo se guarda el historial (exacto)

Cada persona (`Persona` en `data/Modelo.kt`) tiene `relacion`, `cercania`, `detalle`, `desde` (el día en que la clasificaste por primera vez) e `historial`, una lista de `CambioRelacion(hasta, relacion, cercania)`.

**Al guardar** (`Personas.guardar`), hay «cambio» si:

- la relación es distinta de la anterior, **o**
- la cercanía es distinta de la anterior, **incluido** pasar de «sin cercanía» a un valor o de un valor a «sin cercanía» (desde la entrega 10c; antes solo contaba si las dos existían).

Si hay cambio, se agrega al historial `CambioRelacion(hasta = hoy, relación anterior, cercanía anterior)`. Por omisión, «hoy» es el **día lógico en que guardas**. Desde la entrega 10e (D.102), cuando el formulario (`FormularioPersona`, en `ui/pantallas/Gente.kt`) detecta un cambio de relación o de cercanía en alguien ya clasificado, ofrece «cambió antes de hoy…»: eliges en un calendario el día en que cambió de verdad, y ese día se pasa como `hoy` a `Personas.guardar`. La fecha no puede ser posterior a hoy ni anterior o igual al `hasta` del cambio anterior (si eliges una así, se mueve al día siguiente de ese `hasta`), para que el historial siga en orden. A la primera clasificación no se le puede poner fecha: su `desde` es siempre el día en que la guardas. **[Diseño propio]**. Desde 10c, la cercanía que se guarda es la que dejas en el formulario: si desmarcas los círculos, la cercanía **se borra** (antes, una cercanía vacía conservaba la anterior y no había forma de quitarla).

**Al consultar** (`Personas.relacionEn(nombre, fecha)`): ordena el historial por `hasta` y toma la **primera** entrada con `fecha < hasta`; si ninguna cumple, usa la relación actual. `cercaniaEn` hace lo mismo con la cercanía. Desde 10c, si esa entrada no tenía cercanía, el resultado es **«sin cercanía»** en ese tramo (antes usaba la actual, y una cercanía puesta hoy se aplicaba a todos los días pasados). En palabras: **cada entrada del historial vale para los días anteriores a su `hasta`** (y posteriores al `hasta` de la entrada anterior); el propio día del cambio ya tiene la relación nueva.

### 7.4.4 Ejemplo trabajado (Figura 7.1)

Clasificas a una persona inventada así:

1. 1 feb 2025: amistad, cercanía 4.
2. 15 sep 2025: la cambias a pareja, cercanía 6 → historial: `(hasta 2025-09-15, amistad, 4)`.
3. 20 abr 2026: la cambias a expareja, cercanía 3 → historial: `(hasta 2026-04-20, pareja, 6)`.

| Día que se analiza | Primera entrada con fecha < hasta | `relacionEn` | `cercaniaEn` |
|---|---|---|---|
| 2024-12-24 | 2025-09-15 | amistad | 4 |
| 2025-09-14 | 2025-09-15 | amistad | 4 |
| 2025-09-15 | 2026-04-20 | pareja | 6 |
| 2026-04-02 | 2026-04-20 | pareja | 6 |
| 2026-04-20 | ninguna | expareja | 3 |

: Tabla 7.4. Ejemplo trabajado (Figura 7.1)

Si estuviste con ella el 2 abr 2026, ese día se enciende `vi_pareja` (no `vi_expareja`).

![Figura 7.1. Historial de una persona inventada. Rayado: días anteriores a la primera clasificación, a los que se les proyecta la relación más antigua. Cuadriculado: días entre la ruptura real y el día en que la anotaste, que cuentan con la relación vieja si al guardar no dices cuándo fue (desde 10e se puede decir). Script: `figuras/fig19_relacion_fecha.py`.](figuras/fig19_relacion_fecha.png){width=100%}

**Razonamiento.** En un registro de años, las relaciones cambian. Si los análisis usaran la relación de hoy, todos los días con quien hoy es expareja contarían como «estuve con una expareja», incluidos los días en que era tu pareja: eso mezclaría dos situaciones opuestas. Guardar la relación por fecha es la forma correcta de tratar una **covariable que cambia con el tiempo** (*time-varying covariate*: un dato de contexto que no es fijo, sino que tiene un valor por periodo). **[Diseño propio]**, bien planteado.

### 7.4.5 Cuándo falla

- **La fecha del cambio es la que tú digas (resuelto en parte en 10e, D.102).** Hasta 10e era siempre el día en que lo anotabas, sin forma de decir «terminamos hace tres semanas»: en la Figura 7.1, los 23 días entre la ruptura real (28 mar) y la anotación (20 abr) cuentan como pareja (H-6-3). Desde 10e puedes poner el 28 mar con «cambió antes de hoy…», y esos 23 días pasan a «expareja». Si no lo dices, sigue valiendo el día en que guardas, así que el error de la figura sigue ocurriendo cuando se te olvida.
- **Antes de la primera clasificación se proyecta la relación más antigua conocida.** Si clasificaste a alguien como amistad en 2025, los días de 2024 (cuando quizá era un conocido) también cuentan como amistad. Es el mismo problema que el perfil proyectado hacia atrás (H-2-5).
- **Conocerte no tiene historial por persona.** «A quién ves más» y «quiénes viven contigo» se leen con su valor **actual** para todas las fechas (H-6-4, parte que sigue vigente). Si alguien deja de vivir contigo y lo quitas de la lista, `vi_convivientes` deja de contarlo también en los días en que sí vivía contigo; si no lo quitas, lo sigue contando en los días posteriores.
- **Comparaciones sin canon (resuelto en 10c, D.73).** `Perfiles.relacion` buscaba en `perfil.cercanas` y en `perfil.convivientes` con el texto tal cual; ahora compara los nombres canónicos, igual que `vi_convivientes` (H-6-4).
- **Cercanía (resuelto en 10c, D.73).** Antes no se podía borrar, y ponerle cercanía a quien no tenía no contaba como cambio, así que se aplicaba a todos los días pasados (H-6-12). Ahora se borra al desmarcarla, y el paso de «sin cercanía» a un valor queda en el historial. Lo que sigue: una cercanía puesta antes de 10c, sin historial, sigue valiendo hacia atrás; desde 10e, la fecha de un cambio de cercanía también se puede elegir (D.102).
- **Varios cambios el mismo día**: si cambias dos veces en un día, la relación intermedia se pierde (las dos entradas tienen el mismo `hasta`). Es irrelevante en la práctica.

## 7.5 Cercanía: los círculos de Aron (escala IOS)

### 7.5.1 Qué es la escala original

La **escala de Inclusión del Otro en el Yo** (*Inclusion of Other in the Self*, IOS) de Aron, Aron y Smollan (1992) es una pregunta de **un solo reactivo** con dibujos: siete pares de círculos, uno rotulado «yo» y otro «otro», que van de separados a casi completamente encimados. Eliges el par que mejor describe tu relación. La idea es que en una relación cercana el otro pasa a «formar parte» de cómo te ves a ti mismo. Los autores la validaron en dos estudios: buena confiabilidad (da lo mismo si se repite), relación con otras medidas de cercanía y capacidad de predecir si una relación de pareja seguía meses después (Aron et al., 1992). Gächter, Starmer y Tufano (2015) la compararon con seis medidas de cercanía en 772 adultos y concluyeron que es una medida fiable de la cercanía **subjetiva**; un índice combinado de las otras escalas correlacionó ρ = .85 con la IOS. **[Validado]** como instrumento.

### 7.5.2 La versión de la app

`CirculosCercania` (en `ui/pantallas/Gente.kt`) dibuja siete pares de círculos de radio *r*; la distancia entre los centros del par *i* es

$$d_i = r\,(2.1 - 0.31\,(i-1)), \quad i = 1,\dots,7\qquad\qquad (7.1)$$

es decir, de 2.1 *r* (los círculos no se tocan) a 0.24 *r* (casi uno encima del otro). Debajo de cada par va su número; al elegir uno, una línea bajo la fila dice la palabra de ese nivel (`CERCANIA`): 1 «Casi nada en común», 2 «Poco», 3 «Algo», 4 «Bastante», 5 «Mucho», 6 «Muy cercana», 7 «Casi parte de mí». Es opcional; tocar de nuevo el par elegido lo desmarca.

Como la distancia entre centros baja en línea recta, la **fracción encimada** de cada círculo no sube en línea recta. Calculada con la fórmula del área de intersección de dos círculos iguales:

| Nivel | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|---|---|---|---|---|---|---|---|
| *d*/*r* | 2.10 | 1.79 | 1.48 | 1.17 | 0.86 | 0.55 | 0.24 |
| Fracción encimada | 0 % | 4 % | 15 % | 30 % | 47 % | 65 % | 85 % |

: Tabla 7.5. La versión de la app

No pude comprobar en esta sesión cómo están espaciados los círculos en la publicación original **[sin verificar]**, así que no puedo decir si esta progresión se parece a la suya.

**Diferencias con el original y qué implican.** **[Aproximación]**:

- **Etiquetas con palabras.** La IOS original es solo dibujo. Las palabras de la app (que aparecen al elegir un par) ayudan a que un «5» signifique lo mismo con el tiempo (como las anclas del capítulo 3), pero cambian el instrumento: alguien puede ajustar su elección por la palabra y no por el dibujo.
- **Se responde una vez por persona, no por día.** En los estudios, la IOS se aplica en un momento dado. En la app, la cercanía es un rasgo de la persona que **solo cambia cuando la editas**. Por eso `cercania_media` (sección 7.7) mide **a quién nombras** (personas que sientes más o menos cercanas), no **cómo te sentiste con ellas ese día**.
- **No se pregunta con regularidad.** Si no la actualizas, la cercanía de hace años sigue valiendo. No hay recordatorio para revisarla.

## 7.6 Presencias: estuve, pensé, mensajes (resumen)

El capítulo 3 (sección 3.13) explica las reglas de `data/Presencias.kt`. Lo que importa aquí:

- Cada persona de un día tiene uno de tres estados: `estuve`, `pense`, `mensajes`.
- **En el diario, el estado por omisión es «estuve»** si no tocas el nombre (H-1-6).
- Si alguien aparece **solo en momentos**, cuenta como «pensé» cuando el momento era «pensando en…» o «recuerdo» y no marcaste compañía (o marcaste solo «solo»); en cualquier otro caso, «estuve». Basta un momento con «estuve» para que el día cuente «estuve».
- **El diario manda**: si la persona está en el diario, se ignoran los momentos.
- `Presencias.delDia` **excluye los momentos privados** (`impulso`, `recaida`, `libido`; el impulso, desde 10a: hasta v84 sus @menciones contaban aquí).
- La pantalla «¿Estuviste o pensaste?» (`PantallaPresencias`) lista los días con personas sin revisar; «dejar todos como están» los da por revisados sin cambiar nada.

## 7.7 Las variables de personas en el panel

### 7.7.1 Variables fijas (`Estadistica.panel`)

Se calculan solo para los días en que **se pudo nombrar a alguien** (`Dinamicas.diasSociales`, desde la entrega 10c): los días con texto en el diario o con un momento no privado que nombra a alguien con @. Los demás días quedan **vacíos**, no en 0 (D.63; antes se calculaban para cada día con documento de diario o algún momento). Primero `Presencias.delDia` da el estado de cada persona; luego se busca el grupo de relación **de ese día** (sección 7.4).

| Variable | Definición exacta | Aparece en la lista de análisis si… |
|---|---|---|
| `personas` | Número de @personas **distintas** (canónicas) en el **texto del diario** y en los **momentos no privados** de ese día, sin importar su estado (vacío los días sin texto ni momento con @). Desde 10e (D.93); antes solo contaba el diario, y un día en que solo un momento nombró a alguien valía 0 (H-14-2) | siempre |
| `cercanos` | Número de personas con estado «estuve» y grupo `familia`, `pareja`, `amistad` o `casa` | hay alguien en Conocerte o en Tu gente |
| `vi_familia`, `vi_pareja`, `vi_amistades` | 1 si estuviste con al menos una persona de ese grupo; 0 si no | igual |
| `vi_convivientes` | 1 si estuviste con alguien cuyo nombre canónico está en la lista **actual** de convivientes (también pasada por `canon` desde 10c) **y** tiene algún grupo de relación | igual |
| `vi_expareja` | 1 si estuviste con alguien que **ese día** era expareja | alguien es o fue expareja |
| `vi_trabajo` | 1 si estuviste con alguien del grupo `trabajo` | alguien de Tu gente es del grupo trabajo |
| `personas_vistas`, `personas_pensadas`, `personas_mensajes` | Cuántas personas tuvieron cada estado (incluye personas sin clasificar) | algún día tiene un estado distinto de «estuve» marcado, o algún momento «pensando en» nombra a alguien |
| `penso_en_pareja`, `penso_en_expareja`, `penso_en_familia`, `penso_en_amistades` | 1 si pensaste sin ver a alguien de ese grupo | lo anterior (y, para pareja y expareja, que existan) |
| `cercania_media` | Promedio de `cercaniaEn` (cercanía de ese día) de **todas** las personas del día (cualquier estado) que tengan cercanía; vacío si ninguna | alguien tiene cercanía |
| `cartas_otros` | Cartas escritas a otra persona ese día (0 desde la primera carta) | hay cartas a otros |
| `presencias_revisado` | 1 si ese día revisaste quién estuvo y en quién solo pensaste; 0 si hay personas en el diario sin revisar; vacío los días sin personas ni revisión (desde 10c) | siempre (es obvia con las sociales, sección 7.12) |

: Tabla 7.6. Variables fijas (Estadistica.panel)

Hay además variables de **compañía en los avisos** (`acompanado_pct`, `con_pareja`, `con_amigos`…), que **no** salen de las @menciones sino de la pregunta «¿con quién estás?» de cada registro de ánimo. Se explican en el capítulo 3; son una segunda fuente, independiente, sobre lo mismo.

### 7.7.2 Variables dinámicas: una por persona (`Dinamicas.construir`)

- **`per_<nombre>`**: «Aparece @x (diario, mejor y peor momento, momentos, sueños o cartas) (0/1)» (la etiqueta menciona el mejor y el peor momento desde 10c). Vale 1 si la persona se menciona ese día en cualquiera de: texto del diario, texto del sueño, temas del sueño que empiezan con `@` (desde 10c; antes contaba cualquier etiqueta sin `#`, también las de lugares o cosas), «mejor momento» o «peor momento», el texto de un momento **no privado**, o una carta (como destinataria o dentro del texto). Vale 0 en los días en que se pudo nombrar a alguien (diario escrito o momento con @; `Dinamicas.diasSociales`) y en los días donde aparece otra persona; en los demás, vacío (D.63). `pen_` usa la misma base; `tema_`, los días con diario escrito o con un momento que lleva algún #tema.
  - En los análisis entran las **15** personas con más días, siempre que tengan **al menos 5 días**. En la exportación extendida, todas las que tengan al menos 2 días (hasta 400).
- **`pen_<nombre>`**: «Pensé en @x sin verla (0/1)». Vale 1 si `Presencias.delDia` le da estado «pensé» ese día. Entran las **10** con más días, con **al menos 3 días** (en la exportación extendida, desde 2).
- El nombre de la variable es el `slug` del nombre canónico (minúsculas sin acentos, con `_`), hasta 40 caracteres; si dos coinciden, se agrega `_2`, `_3`…

**No existe una variable por persona de «estuve con @x».** `per_` mezcla haber estado con ella, pensar en ella, soñarla o escribirle una carta (H-6-2).

### 7.7.3 Ejemplo trabajado

Un día inventado. Diario: «Desayuné con @ana y @beto. Me acordé de @caro.» Tocas `@caro` y queda en «pensé». Un momento de las 18:00, tipo «pensando en…», sin compañía marcada: «@dani me hace falta». Esa noche sueñas con `@eli`.

Relaciones de ese día: ana = familia (cercanía 6), beto = pareja (7), caro = amistad cercana (5), dani = amistad (4), eli = colega (sin cercanía).

1. `Presencias.delDia`: ana → estuve, beto → estuve, caro → pensé (lo marcaste en el diario), dani → pensé (solo en un momento «pensando en», sin compañía). eli no aparece (los sueños no cuentan aquí).
2. `personas` = 4 (ana, beto y caro del diario, dani del momento; eli no, porque los sueños no cuentan). Antes de 10e valía 3: solo contaba el diario.
3. `personas_vistas` = 2, `personas_pensadas` = 2, `personas_mensajes` = 0.
4. `cercanos` = 2 (ana familia, beto pareja). `vi_familia` = 1, `vi_pareja` = 1, `vi_amistades` = 0 (caro y dani fueron «pensé»).
5. `penso_en_amistades` = 1 (caro y dani son del grupo amistad). `penso_en_familia` = 0.
6. `cercania_media` = (6 + 7 + 5 + 4) / 4 = **5.5** (incluye a las pensadas).
7. `per_ana` = `per_beto` = `per_caro` = `per_dani` = `per_eli` = 1 (eli por el sueño, si la escribiste `@eli` en su texto o en sus temas; en los temas, una etiqueta `eli` sin @ ya no cuenta desde 10c). `pen_caro` = `pen_dani` = 1.
8. Al día siguiente solo respondes un aviso de ánimo, sin escribir el diario ni nombrar a nadie: desde 10c, todas estas variables quedan **vacías** ese día (antes, `vi_familia = 0`, `personas_vistas = 0`…).

### 7.7.4 Cuándo falla

- **Un 0 no siempre quería decir «no la vi» (resuelto en parte en 10c, D.63).** El panel ponía `vi_familia = 0`, `personas_vistas = 0`, etc. en **cualquier día con documento de diario o con algún momento**, aunque ese día no hubieras escrito el diario (H-6-13). Ahora el 0 solo aparece los días con diario escrito o con un momento que nombra a alguien. Sigue siendo «no la nombré»: un diario escrito sin @ cuenta como «no vi a nadie», y quien no escribe nombres en el diario sale sin compañía.
- **Lo que no se nombra no existe** (sección 7.3.4).
- **«Estuve» por omisión** infla las presencias (H-1-6).
- **Relaciones proyectadas** hacia atrás o con fecha de cambio tardía (sección 7.4.5).

## 7.8 Co-mención como aproximación de co-presencia

### 7.8.1 Qué se hace

La Constelación y la Red trazan una línea entre dos personas cuando **aparecen el mismo día lógico**. La idea implícita: si nombras a dos personas el mismo día, es probable que se relacionen (estaban juntas, o las viste en el mismo contexto).

### 7.8.2 Qué tan buena es esa aproximación

**[Aproximación]** débil, por varias razones:

- **El mismo día no es el mismo momento.** Si ves a tu hermana en la mañana y a un compañero en la noche, aparecen juntos aunque nunca se hayan visto. La co-mención mide **co-ocurrencia en tu día**, no que esas personas compartan algo.
- **No distingue el estado.** La Constelación y la Red cuentan la mención sin mirar si fue «estuve», «pensé» o «mensajes»: pensar en alguien mientras estás con otra persona crea una línea.
- **Depende de cuánto escribes.** Los días con diarios largos generan muchas líneas; los días con una línea, ninguna.
- **La memoria de con quién interactuamos es imperfecta.** En estudios clásicos, lo que la gente reporta de sus interacciones coincide solo en parte con lo observado (Bernard et al., 1984). Y al comparar registros del teléfono con lo que las personas reportan, los dos tipos de datos se parecen pero no son lo mismo: lo reportado se desvía según qué tan reciente y qué tan llamativa fue la interacción (Eagle et al., 2009). El diario tiene este sesgo: anotas a quien recuerdas o te importó, no a todos.

Por eso la app exige **al menos 2 días juntas** para dibujar una línea derivada (`minJuntos = 2`; sección 7.10), lo que filtra coincidencias de un solo día. Y la Red permite **declarar** quién conoce a quién (sección 7.10.2), que es más fiable que la co-mención.

## 7.9 La Constelación

### 7.9.1 Qué muestra

Tu gente como un cielo: tú en el centro, cada persona una estrella. **Más grande** si la mencionaste más días; **más cerca del centro** si la mencionaste hace poco. Las personas que aparecen juntas quedan unidas por líneas. El color indica la relación. Está en `ui/pantallas/Constelacion.kt`.

### 7.9.2 Cómo lo hace exactamente (`Constelacion.calcular` y `PantallaConstelacion`)

1. **Periodo**: «3 meses» (90 días), «un año» (365, por omisión) o «siempre». Se toman los días con fecha ≥ hoy − periodo.
2. **Quién aparece**: menciones del **diario** y de los **momentos no privados** dentro del periodo, con nombre canónico. Para cada persona: `dias` = días distintos con mención; `ultima` = el día más reciente.
3. **Quiénes salen**: las **80** con más días.
4. **Líneas**: pares que aparecen el mismo día en **2 días o más** del periodo.
5. **Distancia al centro** (fracción del radio del dibujo):

$$d = \left(0.30 + 0.64\cdot \min\!\left(1, \frac{\text{días desde la última mención}}{P}\right)^{0.6}\right)\cdot 0.94\qquad\qquad (7.2)$$

   donde *P* es el periodo en días (para «siempre», los días desde el primer diario, con mínimo 30). Así, alguien mencionado hoy queda a 0.28 del radio, y alguien no mencionado en todo el periodo, a 0.88. El exponente 0.6 abre más espacio entre los recientes.

6. **Ángulo**: el «ángulo de girasol» (2.399963 radianes ≈ 137.5° entre una estrella y la siguiente, por orden de días) más un pequeño ajuste fijo por nombre (hasta 0.35 radianes, según el `hashCode` del nombre). Sirve **solo para que no se encimen**: el ángulo **no significa nada**, y cambia si cambia el orden de las personas. El comentario del código decía «ángulo fijo por su nombre»; desde 10c dice esto (H-6-14).
7. **Tamaño**: radio = 2.5 + 7·√(días / días de la más mencionada), en dp (unidades de pantalla), con un halo de 2.2 veces ese radio.
8. **Líneas**: opacidad = min(0.12 + 0.06·*n*, 0.5) y grosor = min(0.6 + 0.15·*n*, 2) dp, con *n* = días juntas.
9. **Color**: grupo de relación **actual** (`Perfiles.relacion(p)` sin fecha): casa, familia, pareja, amistad, trabajo y expareja (tenue); las demás, del color del texto.
10. **Nombres**: solo las 14 primeras (o la que tocas). Al tocar una estrella: días, última vez, relación y «suele aparecer con» las 4 personas con más días juntas.
11. Tres anillos de guía a 1/3, 2/3 y el total del radio útil; son decorativos, no umbrales.

![Figura 7.2. Una constelación de un año con 16 personas inventadas, calculada con las fórmulas de la app. Derecha: la curva que convierte «hace cuántos días» en distancia. Script: `figuras/fig17_constelacion.py`.](figuras/fig17_constelacion.png){width=100%}

### 7.9.3 Ejemplo

Con el periodo de un año: `@fer` fue mencionada 44 días, la última hace 20. La más mencionada tiene 210 días. Distancia: (20/365)^0.6 = 0.175; 0.30 + 0.64·0.175 = 0.412; ×0.94 = **0.387** del radio. Tamaño: 2.5 + 7·√(44/210) = 2.5 + 7·0.458 = **5.7 dp**.

### 7.9.4 Razonamiento y cuándo falla

Es una **visualización**, no un análisis: no produce variables ni entra en las correlaciones. **[Diseño propio]**. Combina dos ideas útiles: *frecuencia* (tamaño) y *recencia* (distancia). Hasta donde sé, la literatura sobre el tamaño de las redes sociales describe «círculos» de cercanía cada vez más amplios (no lo verifiqué en esta sesión **[sin verificar]**), pero aquí la distancia mide **recencia de la mención**, no cercanía emocional; para esa está la escala IOS.

Límites:

- **Color con la relación de hoy** (pista 6 del plan, confirmada). Una expareja aparece en rosa tenue aunque en el periodo mostrado haya sido tu pareja casi todo el tiempo. Para un dibujo de «cómo está hoy tu cielo» es razonable; solo hay que saberlo (H-6-14).
- **La distancia premia la última mención.** Una persona que viste a diario durante 11 meses y no mencionas desde hace 30 días queda más lejos que alguien que nombraste una vez ayer.
- **Las líneas son co-menciones** (sección 7.8).
- **Momentos de impulso**: hasta v84 no eran «privados» para el código y sus @menciones contaban; desde 10a no (sección 7.13).

## 7.10 La pantalla Red

### 7.10.1 Qué muestra

Quién conoce a quién, como un grafo. Las **líneas firmes** son lo que **dijiste** en Tu gente; las **finas**, personas que aparecen juntas en tu diario o tus momentos **al menos 2 días**. Arriba, tres cifras: personas, densidad y número de grupos. Está en `PantallaRed` (`ui/pantallas/Lote3.kt`) y `RedPersonas` (`data/Lote3.kt`).

### 7.10.2 Vínculos declarados

En la ficha de una persona, «¿A quién conoce?» muestra hasta 24 personas (las clasificadas y las 20 más mencionadas). Cada toque avanza: sin decir → se conocen → familia entre sí → pareja → colegas → no se conocen → sin decir (`RedPersonas.siguiente`). Se guarda un `Vinculo(a, b, tipo)` con el par ordenado alfabéticamente, así que es **simétrico** (si A conoce a B, B conoce a A). «No se conocen» es información explícita: ese par **no** se dibuja ni cuenta como unido, aunque aparezcan juntas.

Desde la entrega 10c (D.73, H-9-22), los vínculos pasan por los nombres canónicos: `RedPersonas.dichos()` traduce cada par con `canon`, descarta los que quedan como la misma persona y, si dos vínculos dichos con nombres distintos caen en el mismo par, se queda con el último. Al guardar (`poner`), se quita también lo dicho antes con otro nombre de las mismas dos personas. Antes, si uniste `@anita` con `@ana`, lo dicho de `@anita` no aparecía en la Red.

### 7.10.3 Cómo se calcula (`RedPersonas.calcular(minJuntos = 2, max = 45)`)

1. Para **todo el historial** (sin periodo), junta por día las personas del **diario** y de los **momentos no privados**.
2. `dias[p]` = días con mención; `juntos[a, b]` = días en que aparecen las dos.
3. **Nodos**: las **45** personas con más días, **más** todas las que tengan algún vínculo declarado (aunque tengan 0 días).
4. **Aristas**: pares con `juntos ≥ 2` **o** con vínculo declarado (de cualquier tipo), siempre que ambos estén entre los nodos.
5. **Líneas que cuentan** («conectadas»): todas menos las declaradas «no se conocen».
6. **Grado** de cada nodo: número de líneas que cuentan.

### 7.10.4 Las tres cifras

- **Personas**: número de nodos *n*.
- **Densidad**: líneas que cuentan / (*n*(*n*−1)/2), en porcentaje. Incluye en *n* a las personas sin ninguna línea.
- **Grupos** (`componentes`): número de componentes conectadas **sin contar a quienes no tienen ninguna línea** de las que se muestran. Usa la técnica de *unión-búsqueda* (cada nodo apunta a un «representante» de su grupo; al encontrar una línea se juntan los dos grupos). Desde 10c, la función decide quién «tiene líneas» con las aristas que recibe, no con el grado guardado.

**Ejemplo.** 10 personas; líneas: un triángulo (A-B, B-C, A-C), una pareja de líneas (D-E, E-F), una línea G-H; I y J sin líneas. Densidad = 6 / 45 = **13 %**. Grupos = **3** ({A,B,C}, {D,E,F}, {G,H}); I y J no cuentan como grupo.

### 7.10.5 El dibujo: un modelo de fuerzas

`RedPersonas.acomodar` coloca los nodos con un **modelo de fuerzas** del tipo de Fruchterman y Reingold (1991): imagina que todos los nodos se repelen como imanes del mismo polo y que cada línea es un resorte que acerca a los unidos. Tras muchas vueltas, los grupos que se conocen quedan juntos y los grupos separados se alejan. **[Validado]** como técnica de dibujo (es estándar); no es una medida.

Parámetros exactos: posiciones iniciales al azar con semilla fija 7 (el dibujo es siempre igual con los mismos datos); distancia ideal *k* = 0.5/√*n*; repulsión *k*²/*d* entre todos los pares; atracción *d*²/*k*·peso en cada línea que cuenta, con peso **1.4** si es declarada y min(0.5 + 0.05·juntos, 1.2) si es derivada; una leve atracción al centro (0.12 × la distancia al centro) para que los sueltos no se vayan a las orillas; **300** vueltas con un paso máximo («temperatura») que empieza en 0.1 y se multiplica por 0.985 cada vuelta (mínimo 0.002); al final se estira todo al cuadro de 0.1 a 0.9.

Estilo: líneas declaradas de 2 dp y más opacas (punteadas si son «colegas»); derivadas de 1 dp y tenues. Radio del nodo = 4 + 8·√(días / máximo) dp. Nombres visibles si la persona tiene al menos 1/4 de los días de la más mencionada, si hay 15 personas o menos, o si la tocas. El botón «solo lo que dije» quita las líneas derivadas.

### 7.10.6 Constelación frente a Red

| | Constelación | Red |
|---|---|---|
| Pregunta que responde | ¿Quién está presente en mi vida y desde cuándo? | ¿Cómo se conectan entre sí las personas de mi vida? |
| Periodo | 3 meses, un año o siempre | todo el historial |
| Personas | 80 más mencionadas | 45 más mencionadas + las que tienen vínculos dichos |
| Posición | ángulo decorativo; distancia = recencia | modelo de fuerzas: cerca = conectadas |
| Tamaño | días mencionada | días mencionada |
| Líneas | solo co-mención (≥ 2 días) | co-mención (≥ 2 días) + vínculos declarados |
| Color | grupo de relación actual (agrupado) | grupo de relación actual (agrupado, desde 10c) |
| Cifras | ninguna | personas, densidad, grupos |

: Tabla 7.7. Constelación frente a Red

### 7.10.7 Cuándo falla

- **Colores incompletos (resuelto en 10c, D.73).** Hasta 10c, el color salía de la relación **tal cual** (`amistad_cercana`, `colega`, `companero`, `mentor`), que no estaba en la tabla de colores, y una amistad cercana o un colega salían sin color (H-6-5). Ahora cada nodo tiene `grupo = Personas.grupo(relacion)` y el color sale de ahí, como en la Constelación.
- **«Solo lo que dije» contaba grupos de más (resuelto en 10c, D.73).** Al activar ese filtro, el grado seguía siendo el de todas las líneas, y alguien que solo tenía líneas derivadas (ya ocultas) contaba como un grupo de una sola persona (H-6-6). Ahora `RedPersonas.grados(nodos, aristas)` recalcula el grado con las líneas que se ven, y `componentes` solo cuenta a quien tiene alguna de esas líneas. Ejemplo (prueba `CalculosPrueba.redConLineasFiltradas`): A-B dicha y C-D solo por co-mención; con todo, 2 grupos; con «solo lo que dije», 1 grupo, y C queda con grado 0.
- **La densidad depende de cuántas personas sueltas haya.** Con 45 nodos hay 990 pares posibles; muchas personas mencionadas pocas veces y sin líneas bajan la densidad sin que tu red sea «menos unida».

## 7.11 Métricas en R: `vm_red()`

### 7.11.1 Qué se exporta

`Exportar.filasRed` llama a `RedPersonas.calcular(minJuntos = 1, max = 400)`:

- `vm_red_nodos.csv`: `persona`, `relacion` (actual, sin agrupar para Tu gente; agrupada para Conocerte), `grupo` (desde 10c: el grupo de relación, el mismo que da el color en la app), `cercania` (actual), `dias_mencion`, `grado` (calculado con **cualquier** co-mención, `minJuntos = 1`).
- `vm_red_vinculos.csv`: `a`, `b`, `tipo_declarado` (vacío si no lo dijiste), `dias_juntos`.

Exportar con `minJuntos = 1` permite elegir el umbral en R. Pero ojo: el `grado` del archivo **no** es el que muestra la app (que usa 2 días). Desde la entrega 10d el diccionario lo dice en esa columna: «líneas que dijiste (salvo "no se conocen") o con 1 día o más juntas. La Red de la app pide 2 días y muestra 45 personas; aquí van hasta 400» (P-32, incisos c y d de H-6-7, que se documentan en vez de cambiarse).

### 7.11.2 Qué hace `vm_red(declaradas = FALSE, min_juntos = 2, min_dias = 3)`

1. Quita **siempre** los pares declarados «no se conocen», aunque aparezcan juntos muchos días (como la app; desde la entrega 10d). De los demás, se queda con los declarados **o**, si `declaradas = FALSE`, con los pares con `dias_juntos ≥ min_juntos`. Si no hay ningún par (`vm_red_vinculos.csv` vacío o ausente), sigue con una red sin líneas en vez de fallar (H-9-23).
2. **Elige a las personas** (desde 10e, D.106): deja fuera a quien se mencionó menos de `min_dias` días (por omisión, 3), salvo que tenga alguna línea dicha en Tu gente que no sea «no se conocen». Avisa cuántas quedan de cuántas, y se detiene si no queda nadie. Las líneas de las personas que salen también se quitan. Con `min_dias = 0` usa **todos** los nodos del archivo (hasta 400), como antes de 10e. El 3 es **[Diseño propio]**: quita a quien apareció una o dos veces sin exigir tanto que la red se vacíe.
3. Arma el grafo no dirigido con esas personas, tengan o no líneas.
4. Calcula:
   - **`densidad`** = `igraph::edge_density(g)`: líneas / (*n*(*n*−1)/2), sin el ego.
   - **`componentes`** = `sum(igraph::components(g)$csize > 1)`: número de grupos con al menos una línea, **sin contar a las personas sin líneas**, como la app (desde la entrega 10d; antes, `components(g)$no` contaba a cada persona aislada como un grupo).
   - **`aisladas`** = `sum(igraph::components(g)$csize == 1)`: las personas sin ninguna línea, aparte.
   - **`restriccion_burt`**: agrega un nodo «yo» unido a todas las personas y calcula `igraph::constraint` para «yo».
   - **`grado`**: grado de cada persona, de mayor a menor.
   - **`composicion`**: cuántas personas hay de cada relación (`table(relacion)`).
5. Dibuja el grafo con tamaño 4 + 2·√(días mencionada) y color por relación.

Requiere el paquete `igraph`.

### 7.11.3 Las métricas explicadas

**Densidad.** Si de 10 personas hay 9 pares que se conocen, la densidad es 9/45 = 0.20: uno de cada cinco pares posibles está unido. Una red personal densa es un grupo donde todos se conocen (un pueblo, una familia grande); una poco densa, varios mundos separados (trabajo, escuela, familia). Ninguna es «mejor» en sí: Burt (2004) muestra ventajas de ser el puente entre grupos que no se conocen (acceso a ideas y opciones distintas); de las redes densas se dice en general que dan más apoyo y confianza mutua, afirmación que no verifiqué con una fuente en esta sesión **[sin verificar]**.

**Componentes.** Cuántos «mundos» sin ninguna conexión entre sí tiene tu red.

**Restricción de Burt.** Burt llamó **huecos estructurales** (*structural holes*) a la ausencia de conexión entre dos de tus contactos: si conoces a dos personas que no se conocen entre sí, tú eres el puente. Su **restricción** (*constraint*) mide lo contrario: qué tanto tu tiempo y atención están «encerrados» en contactos redundantes, que se conocen entre sí. La fórmula (la que documenta `igraph::constraint`) es

$$C_i = \sum_{j \ne i} \Big(p_{ij} + \sum_{q \ne i,j} p_{iq}\,p_{qj}\Big)^2, \qquad p_{ij} = \frac{a_{ij}+a_{ji}}{\sum_{k \ne i}(a_{ik}+a_{ki})}\qquad\qquad (7.3)$$

donde *a*<sub>ij</sub> = 1 si *i* y *j* están unidos. Sin pesos, *p*<sub>ij</sub> es simplemente 1/(grado de *i*): la parte de tu atención que «invierte» *i* en *j*. El término de adentro suma la inversión **indirecta**: lo que inviertes en *q* que a su vez invierte en *j*. Si tus contactos se conocen, cada uno te «llega» también por los demás y la restricción sube.

**Ejemplo trabajado (Figura 7.3).** Tú y 6 personas; `vm_red()` te une a las 6.

- **Nadie se conoce.** Cada *p* = 1/6 y no hay caminos indirectos: *C* = 6 × (1/6)² = **0.167** (en general, 1/*n*).
- **Dos grupos de 3** (cada grupo se conoce entero, sin conexión entre grupos). Cada persona tiene grado 3 (dos de su grupo + tú). Para un contacto *j*: directo 1/6; indirecto por sus dos compañeros de grupo, 2 × (1/6)(1/3) = 1/9. Término: (1/6 + 1/9)² = (5/18)² = 0.0772; × 6 = **0.463**.
- **Todos se conocen.** Cada persona tiene grado 6. Para un contacto *j*: directo 1/6; indirecto por los otros 5, 5 × (1/6)(1/6) = 5/36. Término: (11/36)² = 0.0934; × 6 = **0.560** (en general, (2*n*−1)²/*n*³).
- **Dos grupos con un puente** (a3 conoce a b1): **0.483**. Para ti, el puente *sube* la restricción, porque tus dos mundos ya no dependen solo de ti. Para a3, en cambio, es una ventaja: en la red sin ti, su restricción es 0.611 frente a 1.007 de sus compañeros de grupo; es la persona que une los dos grupos.

Estos números los calcula el script de la figura con la fórmula escrita a mano y los compara con `networkx.constraint` (coinciden). Para el ego unido a todas, `igraph` y `networkx` suman sobre las mismas personas, así que deberían coincidir también con R; **no pude ejecutar R en esta sesión** para confirmarlo **[sin verificar]**.

![Figura 7.3. Restricción de Burt del ego y densidad en cuatro redes de 6 personas (datos sintéticos). Script: `figuras/fig18_burt_puente.py`.](figuras/fig18_burt_puente.png){width=100%}

**Composición.** Qué proporción de tu red es familia, amistad, trabajo… Es la descripción más sencilla de una red personal y una de las más usadas en el análisis de redes egocéntricas (Perry et al., 2018).

### 7.11.4 Razonamiento y validez

Las métricas son estándar **[Validado]** como definiciones (Burt, 2004; Perry et al., 2018). Lo que **no** está validado es la red sobre la que se calculan **[Aproximación]**:

- Las líneas derivadas son co-menciones (sección 7.8), no relaciones confirmadas. Con `declaradas = TRUE` solo cuentan las que dijiste, que son más fiables pero quizá incompletas.
- Los nodos son personas **mencionadas**, no tu red real.
- **La restricción y la densidad dependen mucho del número de personas.** Con el ego unido a todas, la restricción mínima es 1/*n*: con 400 personas puede bajar hasta 0.0025 solo por haber mencionado a mucha gente una vez. El archivo exportado no pone un mínimo de días para los nodos, y hasta 10e `vm_red()` tampoco: cada persona mencionada un solo día bajaba la densidad y la restricción sin cambiar nada de tu red de verdad (H-6-8). Desde 10e (D.106), `vm_red()` pide por omisión 3 días (`min_dias = 3`). La app no filtra así (toma las 45 más mencionadas); queda dicho aquí. Para comparar en el tiempo, conviene usar el mismo `min_dias` en todos los periodos.

### 7.11.5 Diferencias entre la app y R (H-6-7)

| Aspecto | App (pantalla Red) | R (`vm_red`) |
|---|---|---|
| Personas | 45 más mencionadas + declaradas | hasta 400, con al menos `min_dias` días (3 por omisión, desde 10e) + las que tienen una línea dicha |
| Pares «no se conocen» que aparecen juntos ≥ umbral | **no** cuentan como línea | **no** cuentan (igual desde 10d; antes sí contaban) |
| Grupos / componentes | sin contar a las personas sin líneas | sin contar a las personas sin líneas (desde 10d), y aparte `aisladas` |
| Grado | con co-mención ≥ 2 días | en el CSV, con ≥ 1 día; en R, recalculado con `min_juntos` |
| Relación | grupo actual (desde 10c) | `composicion` y color por la columna `grupo` desde 10c (las exportaciones viejas, sin esa columna, usan `relacion`) |

: Tabla 7.8. Diferencias entre la app y R (H-6-7)

Desde la entrega 10d (P-32) solo quedan dos diferencias, las de personas y de grado, y el diccionario las dice. Con los mismos datos, la app y R pueden dar todavía **densidades y número de grupos distintos** por esas dos razones, no por error de cálculo; con `min_juntos = 2` y filtrando a las mismas 45 personas, deberían coincidir; desde 10e, ese filtro no es el que hace `min_dias`, que corta por días y no por puesto (no lo comprobé con datos reales: R no corre en la compilación automática; sí corrí `vm_red()` con un archivo inventado de 3 personas, un par con 3 días juntas y otro declarado «no se conocen»: 1 grupo, 1 aislada).

## 7.12 Cómo entran las personas en las correlaciones

Las variables de las secciones 7.7.1 y 7.7.2 entran en la lista de relaciones de Explorar con el mismo método que todas las demás (capítulo 6, secciones 6.5 a 6.10): mínimo de días con dato, quitar la tendencia, intervalo y corrección por comparaciones múltiples. Lo específico de las personas:

- **Cuántas entran**: 15 `per_` (≥ 5 días) y 10 `pen_` (≥ 3 días). Es un filtro razonable para no llenar la lista de casos raros, pero significa que las relaciones con personas poco frecuentes **no se buscan** en la app. En R sí se pueden buscar: desde 10d el panel de R es `vm_panel_extendido.csv`, que trae a todas las personas con 2 días o más (hasta 400), aunque `vm_relaciones()` cruza por omisión solo las variables de la app (`vm_variables.csv`); las demás se piden con `variables = c(...)`.
- **Pares «obvios» que se excluyen** (`Estadistica.obvia`), porque se mueven juntos por construcción:
  - cada `per_` con `personas`, `cercanos`, `vi_familia`, `vi_pareja`, `vi_amistades`, `vi_convivientes`, `palabras`, `cartas_otros` y, desde 10c, también `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `vi_expareja`, `vi_trabajo`, `cercania_media`, `presencias_revisado` y los `penso_en_*` (`Dinamicas.AGREGADOS["per_"]`);
  - cada `pen_` con `personas`, `personas_pensadas` y los `penso_en_*`;
  - `per_x` con `pen_x` de la misma persona;
  - y las familias fijas de `Estadistica.FAMILIAS`: {`palabras`, `personas`, `hitos_n`}, {`cercanos`, `vi_familia`, `vi_pareja`, `vi_amistades`, `vi_convivientes`, `personas`, `palabras`}, {`cercanos`, `vi_familia`, `vi_pareja`, `vi_amistades`, `vi_expareja`, `vi_trabajo`, `cercania_media`, `personas`}, {`personas`, `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `cercanos`}, {`personas_pensadas`, `penso_en_*`} y, desde 10c, {`presencias_revisado`, `personas`, `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `cercanos`, `palabras`}.
- **R usa la misma lista (desde 10d).** Hasta 10d, la lista de pares obvios de R era mucho más corta que la de la app y no tenía ninguna de estas familias de personas, así que en `vm_relaciones()` varios de estos pares sí aparecían (H-5-2). Desde 10d (P-31), la exportación trae `vm_familias.csv`, generado con `Estadistica.familias` a partir de las mismas reglas de `obvia()` (incluidas las de `per_` y `pen_`, también para las personas que solo están en el panel extendido), y `vm_obvia`/`vm_relaciones()` lo leen. Solo con una exportación vieja, sin ese archivo, R vuelve a su lista corta.
- **Lo que no se excluía y debía (resuelto en 10c, D.73)**: `per_x` con `personas_vistas`, `personas_mensajes`, `vi_expareja`, `vi_trabajo` y `cercania_media` no estaban en `AGREGADOS["per_"]`, y salían como relación aunque fueran por construcción (H-6-10). Ahora sí están.
- **Dos personas entre sí** (`per_ana` con `per_beto`) **no** se excluyen: una relación positiva entre ellas es su co-mención, que es la misma información de la Red. No es un error, pero no es un hallazgo nuevo.
- **La relación de cada día sí se usa** en `vi_*`, `penso_en_*` y `cercania_media` (sección 7.4), salvo la lista de convivientes de `vi_convivientes`, que es la actual. Las variables `per_`/`pen_` son por persona, sin relación.

## 7.13 Privacidad: dónde entran los momentos privados

Desde el módulo 10a, un momento es privado si su tipo es `impulso`, `recaida` o `libido` (`Momento.privado`, regla única en `data/Privado.kt`). Sus @menciones y #temas **no entran en nada de este capítulo**:
- no entran en presencias, `vi_*`, `personas_vistas`, `pen_`, Constelación ni Red (ni en `vm_red_*`);
- no entran en `per_` ni `tema_`, porque `Dinamicas.construir` filtra los privados, y sus días ya no cuentan como «días con diario» para los ceros;
- no entran en `Personas.menciones` (Tu gente, la tarjeta «¿Quién es @x?», los duplicados, «¿A quién conoce?»), ni en las sugerencias de @ y # al escribir.

Esto vale con o sin el seguimiento privado activado. Los momentos privados solo se ven en la pantalla Privado y van completos a la exportación para R (`vm_momentos.csv`), donde sus menciones se pueden analizar si se quiere.

**Hasta v84** (H-6-1):
- las recaídas y la libido entraban en `per_`, `tema_` y `Personas.menciones`, sin importar el interruptor;
- los impulsos no eran privados para el código y entraban **en todo**.

La pista 4 del plan («la constelación toma los momentos excepto el tipo `recaida`; la red excluye todos los privados») **no se confirmó en esa forma**: ya entonces la Constelación y la Red usaban el mismo criterio.

## 7.14 Resumen de decisiones

| Decisión | Clasificación | Respaldo |
|---|---|---|
| Solo cuentan las personas escritas con @ | Diseño propio | — |
| Equivalencias y detección de duplicados (Levenshtein ≤ 2, prefijo) | Diseño propio | — |
| Relación con historial por fecha | Diseño propio (bien fundado) | Tratamiento estándar de covariables que cambian en el tiempo |
| Grupos de relación (12 → 5 + otros) | Diseño propio | — |
| Cercanía con círculos de Aron (IOS) con etiquetas | Aproximación | Aron et al. (1992); Gächter et al. (2015) |
| Presencias (estuve / pensé / mensajes) | Diseño propio | capítulo 3 |
| Co-mención el mismo día como vínculo (≥ 2 días) | Aproximación (débil) | Bernard et al. (1984); Eagle et al. (2009), sobre los límites del autorreporte |
| Constelación (tamaño = frecuencia, distancia = recencia) | Diseño propio (visualización) | — |
| Dibujo por modelo de fuerzas | Validado (técnica de dibujo) | Fruchterman y Reingold (1991) |
| Densidad, componentes, restricción de Burt, composición | Validado (definiciones) | Burt (2004); Perry et al. (2018) |
| Ego unido a todas para la restricción | Validado (en una red egocéntrica el ego está unido, por definición, a cada contacto) | Perry et al. (2018) |
| Topes de 15 `per_` y 10 `pen_` en los análisis | Diseño propio | — |

: Tabla 7.9. Resumen de decisiones

## 7.15 Resumen del capítulo

1. La app reconoce a las personas **solo por `@nombre`**, en minúsculas, con equivalencias que desde la entrega 10c se siguen en cadena (hasta 8 pasos, con los ciclos unidos). Cada función lee textos distintos (sección 7.2), así que «aparecer» no significa lo mismo en todas partes.
2. La **relación con historial** es una buena decisión: los análisis usan la relación de cada día. Sus límites: lo anterior a la primera clasificación se proyecta, y Conocerte no tiene historial (H-6-4); desde 10c, al menos, sus nombres se comparan con las equivalencias, y la cercanía se puede borrar y queda en el historial. Desde 10e se puede decir en qué día cambió de verdad la relación (H-6-3, en parte); si no se dice, vale el día de la anotación.
3. La **cercanía** usa la escala IOS, validada, con etiquetas añadidas y respondida una vez por persona; `cercania_media` describe a quién nombras, no cómo te fue con ellos ese día.
4. Las variables sociales del panel ponían **0 en días sin diario** pero con algún momento o documento (H-6-13); desde 10c quedan vacías los días sin diario escrito ni momento con @. **No hay variable por persona de «estuve con @x»**: `per_` mezcla presencia, pensamiento, sueños y cartas, y su etiqueta lo dice (H-6-2).
5. La **Constelación** es una visualización de frecuencia y recencia, coloreada con la relación actual. La **Red** combina co-menciones (≥ 2 días) y vínculos declarados (con nombres unidos desde 10c), con un dibujo de fuerzas estándar; sus dos fallos de presentación (color y «solo lo que dije») se corrigieron en 10c (H-6-5, H-6-6).
6. `vm_red()` calcula densidad, componentes, **restricción de Burt** y composición. Las definiciones son estándar, pero los resultados dependen mucho del número de personas incluidas (H-6-8; desde 10e, `vm_red()` deja fuera por omisión a quien se mencionó menos de 3 días) y **diferían de la app** en cuatro puntos concretos (H-6-7). Desde la entrega 10d, `vm_red()` ya no cuenta como línea los pares «no se conocen» ni cuenta como grupo a quien no tiene líneas, y no falla sin pares; la diferencia de personas (45 frente a 400) y de grado (2 días frente a 1) se documenta en el diccionario.
7. Desde 10a, los momentos privados (impulso, recaída, libido) no entran en nada de este capítulo; hasta v84, recaídas y libido entraban en `per_` y en «Tu gente», y los impulsos en todo (H-6-1).
8. En el módulo 6 se identificaron **15 hallazgos** (H-6-1 a H-6-15) y no se cambió código. Después, el módulo 10 resolvió H-6-1 (10a), H-6-2 (documentándolo), H-6-5, H-6-6, H-6-10, H-6-12 y H-6-14 (10c) y H-6-7 (10d), y resolvió en parte H-6-4, H-6-9, H-6-11 y H-6-13 (10c). En la entrega 10e se resolvió H-6-8 (`vm_red(min_dias = 3)`) y en parte H-6-3 (la fecha del cambio se puede elegir; lo anterior a la primera clasificación sigue proyectado), y H-6-15 (diferencias con la IOS original) se da por resuelto con su descripción en la sección 7.5. Ninguno de los 15 queda pendiente: 10 resueltos y 5 en parte (H-6-3, H-6-4, H-6-9, H-6-11 y H-6-13).


# Capítulo 8. Experimentos personales (N-de-1)

> **Para qué sirve este capítulo.** Explica cómo la app te ayuda a **probar algo en ti mismo** (dormirte antes, dejar la cafeína en la tarde, tomar creatina) y a decidir si funcionó. Cubre la lógica de estos diseños y su respaldo en la literatura, qué hace exactamente el código (sorteo de semanas, días de lavado, diferencia de medias, tamaño del efecto, prueba de tendencia) y qué tan confiable es el resultado. También trata la sección «Antes y después» de Explorar, que **parece** un experimento pero no lo es. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper. Lo que cambió en el módulo 10 se marca «desde el esquema 13» (entrega 10b: fecha de fin y «¿Lo hiciste hoy?») o «desde la 10c» (commit `b943ec7`: intervalo con *t* y autocorrelación, sorteo por pares, lavado que no se come una condición, tendencia dentro de cada condición y resultado provisional mientras dura el plan), y está en el apéndice D (D.13, D.17, D.65, D.67 y D.68). La entrega 10e (v91; `SCHEMA_V` sigue en 14) cambió la d de Cohen por la g de Hedges, completa el cumplimiento de «Creatina» con el registro de suplementos (D.96) y agregó `vm_experimento()` en R, con la prueba de aleatorización (D.97); se marca «desde 10e». La entrega 10f (v92, esquema 15) sella también el criterio de decisión, con una huella de lo sellado (D.113), y hace que «Creatina» mida la fuerza en un ejercicio fijo (D.114); se marca «desde 10f». Cada sección dice qué era así antes y qué es así ahora. **Los ejemplos y las simulaciones usan datos inventados.**
>
> **Cómo leerlo.** Igual que los capítulos anteriores: lenguaje sencillo, cifras y condiciones del código tal cual, con el nombre de la función. Cada decisión se marca como **[Validado]**, **[Aproximación]** o **[Diseño propio]**. Los problemas se numeran H-7-n (el número es el del módulo 7, que escribió este capítulo) y se detallan en `hallazgos.md`. Este capítulo usa **simulaciones**: generar miles de experimentos inventados en los que *sabemos* la verdad (por ejemplo, que la acción no hace nada) y contar cuántas veces el método de la app se equivoca. Los scripts están en `figuras/`.

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

: Tabla 8.1. Mini-glosario

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

: Tabla 8.2. Por qué por pares (pista 3 del plan; H-7-5, resuelto en 10c)

Con 4 semanas, uno de cada tres experimentos «alternos» era en realidad un antes/después.

**Sortear por pares** lo evita: nunca hay más de dos semanas seguidas iguales, y una tendencia lenta afecta casi por igual a ambas condiciones. En las simulaciones de la Figura 8.3 (con el método de antes de la 10c), con una tendencia de 0.02 desviaciones por día y sin efecto real, el sorteo libre de 8 semanas daba una falsa señal el **14 %** de las veces y el sorteo por pares, alrededor del **5 %** (el script de la figura no trae este último caso; se repitió con sus mismas funciones en el módulo 14).

**El costo**: hay menos planes posibles. Esto importa para la **prueba de aleatorización** (sección 8.6.4; en R desde 10e), cuyo valor *p* más pequeño posible es 1 entre el número de planes: con pares, 1/8 = 0.125 con 6 semanas y 1/16 = 0.0625 con 8, las dos por encima de 0.05. De las duraciones que ofrece la app (4, 6, 8 o 12 semanas), solo con 12 (1/64 ≈ 0.016) podría bajar de 0.05 (en la práctica el mínimo es el doble, sección 8.6.4).

**Con pares ya no puede salir un plan en bloque** (siempre hay al menos dos cambios de condición con 4 semanas o más), pero los experimentos sellados antes con sorteo libre conservan su plan. Desde la 10c, la tarjeta decide si el plan **real** quedó en bloque (`Experimentos.enBloque`: diseño antes/después, o un solo cambio de condición en todo el plan) y, si es un alterno que quedó así, lo dice: «El sorteo quedó en bloque… Se lee como un antes y después» (si además salta el aviso de tendencia, lo dice dentro de ese aviso, sección 8.8.1). Antes, el aviso fuerte dependía solo de que el diseño elegido fuera «antes y después» (H-9-30).

## 8.5 Qué días cuentan: el lavado

### 8.5.1 Las reglas (`Experimentos.toca` y `Experimentos.enLavado`, `data/Lote3.kt`)

- **Qué condición tiene un día**: con *d* = días desde `inicio`, si *d* < 0 el día no está en el experimento; si no, la semana es ⌊*d*/7⌋ y la condición es `plan[semana]`. Pasado el plan, el día tampoco está.
- **Qué días son de lavado**: se buscan los **días de cambio**: el día 0 **si** el plan empieza con «SÍ», y el primer día de cada semana cuyo valor es distinto del de la semana anterior (7·*k*). Un día es de lavado si cae en los primeros `lavado_dias` días tras cualquiera de esos cambios: $0 \le d - c < L$.

Observa que el lavado se aplica **en los dos sentidos**: al pasar de «SÍ» a «no» (para que se vaya el efecto) y de «no» a «SÍ» (para que el efecto tenga tiempo de aparecer). Si el plan empieza con «no», el día 0 no es de lavado.

### 8.5.2 Ejemplo (Figura 8.1, izquierda)

Plan sorteado: SÍ no no SÍ SÍ no, 2 días de lavado. Cambios: día 0 (empieza con SÍ), día 7 (SÍ→no), día 21 (no→SÍ), día 35 (SÍ→no). Días de lavado: 0–1, 7–8, 21–22, 35–36 = 8 días. Cuentan **17 días con la acción** (21 − 4) y **17 sin ella** (21 − 4). La semana 3 («no» tras «no») no pierde ningún día.

![Figura 8.1. Izquierda: qué días cuentan con un plan alterno y 2 días de lavado. Derecha: la plantilla «Creatina» desde la entrega 10c (12 semanas, antes y después, 28 días de lavado): quedan 14 días con la acción y 42 sin ella. Con las 8 semanas que tenía la plantilla hasta la 10c (4 «no» y 4 «SÍ»), todo el periodo con la acción quedaba en lavado (sección 8.5.3). Script: `figuras/fig20_calendario_experimento.py`.](figuras/fig20_calendario_experimento.png){width=100%}

### 8.5.3 Razonamiento y cuándo falla

Excluir los primeros días de cada periodo es una forma estándar de lidiar con el arrastre cuando no se puede (o no se quiere) dejar un periodo sin nada entre condiciones; hasta donde sé, las guías de N-de-1 lo llaman *lavado analítico*, pero no lo verifiqué en el texto de la guía **[sin verificar]**. **[Aproximación]** razonable: la duración correcta depende de cada acción, y la app la deja a tu criterio (o a la plantilla).

**El problema grave (hasta la entrega 10c): el lavado podía comerse una condición entera** (H-7-1, resuelto en 10c).

- Con semanas alternas y **7 días de lavado**, toda semana que empieza con un cambio se pierde completa. Con un plan que alterna cada semana (SÍ no SÍ no SÍ no), **no queda ningún día**; con «no SÍ no SÍ no SÍ», solo quedan los 7 días «no» de la primera semana. El experimento nunca daba resultado.
- Con **28 días** y semanas alternas, todo queda en lavado.
- **La plantilla «Creatina»** (8 semanas, antes y después, 28 días de lavado) sufría exactamente esto: el plan era 4 semanas «no» + 4 «SÍ»; el único cambio, el día 28, y los días 28 a 55 de lavado. Contaban **28 días sin la acción y 0 con ella**. **La plantilla no podía dar resultado nunca.**

**Desde la 10c:** no se deja sellar si en el peor sorteo posible alguna condición queda con menos de 7 días que cuentan (sección 8.3.1), y la plantilla «Creatina» dura **12 semanas**: 6 «no» y 6 «SÍ», cambio el día 42, lavado de los días 42 a 69; cuentan **42 días sin la acción y 14 con ella** (Figura 8.1, derecha). El mínimo de 7 días es **[Diseño propio]**: una semana completa, para que cada condición tenga al menos un día de cada día de la semana.

## 8.6 El resultado: diferencia, intervalo y tamaño del efecto

### 8.6.1 Qué calcula (`Experimentos.resultado` y `Estadistica.diferenciaEnDias`)

Recorre los días desde `inicio` hasta el último día del plan, hasta hoy o, desde el esquema 13, hasta el día en que lo terminaste (`Experimento.fin`), lo que llegue antes. Para cada día que está en el experimento, no es de lavado y **tiene dato** en la variable elegida, guarda el valor (con su fecha) en la lista «con» o «sin». **Desde la entrega 10c, hoy no entra** (H-9-31): el día aún no termina y las variables que se acumulan (pantalla, pasos, enfoque) estarían a medias; las respuestas de «¿Lo hiciste hoy?» sí cuentan para el cumplimiento. El panel es el mismo de los análisis (`Estadistica.panel(dinamicas = false)`) y, desde la 10c, se arma en segundo plano, como en Análisis, para que las variables que dependen de MacroFactor valgan lo mismo aquí que en las demás pantallas (H-7-13, capítulo 5).

1. **Diferencia de medias con su intervalo** (`Estadistica.diferenciaEnDias(con, sin)`), solo si hay **al menos 3 días en cada condición**. Desde la 10c (D.65):

   - **La autocorrelación de cada condición**, *ρ* (`Estadistica.ar1Grupos`): la correlación entre pares de días **seguidos** de esa condición, si hay al menos 10 pares; si no, la de las dos condiciones juntas (cada una centrada en su media); si tampoco, 0 (aquí no se usa el 0.3 que, desde 10e, suponen las relaciones cuando no se puede estimar: D.95 no cambia los experimentos). Como en series cortas la autocorrelación estimada sale más baja que la real, se corrige: *ρ* + (1 + 4*ρ*)/*n*, con *n* los días de esa condición (o de las dos, si se usó la de las dos juntas) (la aproximación de primer orden atribuida a Marriott y Pope, 1954; la forma exacta no se leyó en el artículo **[sin verificar]**). Luego se acota entre 0 y 0.9: solo se corrige hacia un intervalo más ancho.
   - **El error estándar de cada condición** se multiplica por √((1 + *ρ*)/(1 − *ρ*)), y su número efectivo de días es *n*(1 − *ρ*)/(1 + *ρ*), nunca menos de 2 (`Estadistica.diferenciaCompleta`):

$$EE = \sqrt{\frac{s_c^2}{n_c}\cdot\frac{1+\rho_c}{1-\rho_c} + \frac{s_s^2}{n_s}\cdot\frac{1+\rho_s}{1-\rho_s}}\qquad\qquad (8.1)$$

   - **Los grados de libertad** son los de Welch con esos *n* efectivos (acotados entre 1 y la suma de los dos *n* efectivos menos 2), y el intervalo usa el **cuantil de la *t* de Student** (`Estadistica.cuantilT(0.975, gl)`) en lugar de 1.96:

$$\bar{x}_c - \bar{x}_s \;\pm\; t_{0.975,\,gl}\cdot EE\qquad\qquad (8.2)$$

   donde $\bar{x}$ son las medias, $s^2$ las varianzas muestrales (dividiendo entre *n* − 1) y *n* el número de días de cada condición. **Hasta la 10c** era el error estándar de Welch sin corregir y con el valor **1.96 fijo** de la distribución normal.

2. **Mensaje**:
   - **Mientras dura el plan** (`Experimentos.enCurso`), la tarjeta dice «Provisional: el plan sigue en curso. Mirar el resultado a medio camino y parar en la primera «señal» engaña; espera a que termine», y **no** da la frase de «señal» (H-7-4).
   - **Al terminar**: si el intervalo no toca el cero, «es una señal para ti»; si lo toca, «todavía no se distingue del azar».
   - **Desde 10f, el criterio sellado** (si lo hay; sección 8.3.2): «Según el criterio que sellaste: se cumple» o «no se cumple» (`Experimentos.criterio`). Mientras el plan sigue en curso no sale.
   - **El rótulo** dice «Semanas con la acción según el plan: … · sin ella: …», para que no parezca que se midió lo que hiciste (H-7-8).
   - **Si no hay 3 días de cada condición**, desde la 10c distingue tres casos (H-9-32): «Este plan nunca dará resultado» (con el lavado, en todo el plan quedan menos de 3 días de alguna condición: `Resultado.posibleSi`, `posibleNo`); «El experimento terminó sin suficientes días medidos»; y, si sigue en curso, «Aún no hay suficientes días medidos en ambas condiciones».

3. **Tamaño del efecto: g de Hedges desde 10e** (D.96; antes, la *d* de Cohen sin corregir), también con al menos 3 días por condición:

$$d = \frac{\bar{x}_c - \bar{x}_s}{s_p}, \qquad s_p = \sqrt{\frac{(n_c-1)s_c^2 + (n_s-1)s_s^2}{n_c+n_s-2}}, \qquad g = d \cdot \Big(1 - \frac{3}{4(n_c+n_s) - 9}\Big)\qquad\qquad (8.3)$$

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

: Tabla 8.3. Por qué el intervalo de antes de la 10c era demasiado estrecho (pistas 1 y 2; H-7-3 y H-7-4, resueltos en 10c)

Antes de la 10c, con autocorrelaciones como las que suelen tener las medidas diarias de ánimo, **entre una de cada siete y una de cada cuatro** «señales» podían ser azar, y la mitad si te detenías al ver la primera. La cifra de 0.3 a 0.5 es ilustrativa: no verifiqué un valor típico en la literatura **[sin verificar]**; lo que importa es que cualquier autocorrelación positiva estrecha el intervalo de más.

**Desde la 10c el método es conservador**: da menos del 5 % de falsas señales, porque *ρ* se estima con pocos días y se acota hacia arriba. **El costo es el poder** (la probabilidad de ver un efecto que sí existe). En la misma simulación, con autocorrelación 0.3 y un efecto real de 0.5 desviaciones, un experimento de 6 semanas lo detecta el **13 %** de las veces (antes, el 40 %, pero con 14 % de falsas señales); con un efecto de 0.8, el **29 %**; con 12 semanas, el **30 %** y el **62 %**. Para efectos moderados, conviene ir a 12 semanas y no esperar certezas de 4 o 6.

![Figura 8.2. Izquierda: un experimento inventado con su resultado calculado como antes de la 10c, con cada corrección por separado y como desde la 10c. Derecha: con qué frecuencia el método de la app da una «señal» cuando la acción no hace nada, antes y desde la 10c. Script: `figuras/fig21_resultado_experimento.py`.](figuras/fig21_resultado_experimento.png){width=100%}

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

## 8.8 La prueba de tendencia

### 8.8.1 Qué hace (desde la entrega 10c: `Experimentos.tendencia`)

Si hay al menos 8 días que cuentan y tienen dato (sin hoy), ajusta por mínimos cuadrados **a la vez** la tendencia y la condición:

$$y = a + b\,i + c\,\text{con} + \varepsilon\qquad\qquad (8.4)$$

con *i* = número de día (0, 1, 2…) y «con» = 1 los días con la acción y 0 los demás. *b* es la **pendiente dentro de cada condición** (la misma para las dos): cuánto cambia la medida por día una vez descontado el efecto de la condición. Su error estándar sale de la matriz del ajuste con *n* − 3 grados de libertad, y el aviso salta si |*b*/EE(*b*)| supera el **cuantil 0.975 de la *t*** con *n* − 3 grados. Si todos los días son de la misma condición (o el día y la condición no se pueden separar), queda la pendiente simple con *n* − 2. El aviso dice: «Ojo: dentro de cada condición, tu medida venía cambiando sola (… por día). Parte de la diferencia puede ser esa tendencia», y añade «como el plan quedó en bloque (primero una condición y luego la otra), pesa más» si `Experimentos.enBloque`.

**Hasta la 10c** la recta se ajustaba **con las dos condiciones juntas** (valor contra día) y avisaba si |*b*/EE(*b*)| > 2:

$$b = \frac{\sum (i-\bar{i})(y-\bar{y})}{\sum (i-\bar{i})^2}, \qquad EE(b) = \sqrt{\frac{\sum (y - \hat{y})^2/(n-2)}{\sum (i-\bar{i})^2}}\qquad\qquad (8.5)$$

### 8.8.2 Por qué importa la tendencia

Si la medida sube sola (por la estación, por avanzar en el gimnasio, por salir de una mala racha), en un diseño **antes/después** el periodo «después» siempre se verá mejor, aunque la acción no haga nada. Es la principal debilidad de los diseños AB, y la razón de alternar y sortear (Kratochwill y Levin, 2010).

**Simulación (Figura 8.3).** Con una tendencia de 0.02 desviaciones por día (en 8 semanas, ≈ 1.1 desviaciones) y **sin efecto real**:

| Diseño | Falsas señales antes de la 10c | Desde la 10c |
|---|---|---|
| Antes/después | 55 % | 39 % |
| Semanas alternas (antes: sorteo libre; desde la 10c: por pares) | 14 % | 2 % |

: Tabla 8.4. Por qué importa la tendencia

**El antes/después sigue siendo muy vulnerable**: la corrección por autocorrelación ensancha el intervalo, pero la tendencia sigue cayendo entera en una condición. Y la prueba de tendencia nueva lo detecta mal en ese diseño: en esa misma simulación avisa solo el **22 %** de las veces, porque dentro de cada mitad hay poca información para estimar la pendiente. Por eso la plantilla «Creatina», que no puede alternar, debe leerse con cuidado (sección 8.9).

### 8.8.3 El problema de la prueba de antes (H-7-6, resuelto en 10c)

La recta se ajustaba **sin separar la condición**. En un antes/después, un **efecto real** también hace que la segunda mitad sea más alta, y la recta lo confundía con una tendencia. En la simulación, con un efecto real *d* = 0.8 y **sin ninguna tendencia**, el aviso «tu medida venía cambiando sola» aparecía en el **71 %** de los antes/después y en el 16 % de los alternos (lo esperado por azar sería ≈ 5 %). Es decir: justo cuando la acción funcionaba, la app sugería que quizá fue la tendencia.

Con el modelo de la 10c, en la misma situación el aviso sale el **5 %** de las veces en los antes/después y el **6 %** en los alternos por pares: lo esperado por azar. **[Validado]** como método (regresión con tendencia y condición); en un antes/después puro, tendencia y efecto se siguen separando mal, porque la condición cambia una sola vez.

![Figura 8.3. Izquierda: una tendencia sin efecto real produce una diferencia falsa en un antes/después (con el método de antes y con el de la 10c). Centro: con semanas alternas por pares, la misma tendencia se reparte entre las dos condiciones. Derecha: frecuencia de falsas señales con tendencia, y de avisos de tendencia cuando hay un efecto real, antes de la 10c y desde la 10c. Script: `figuras/fig22_tendencia_diseno.py`.](figuras/fig22_tendencia_diseno.png){width=100%}

## 8.9 Las plantillas

`Experimentos.PLANTILLAS` (`data/Lote3.kt`):

| Plantilla | Acción | Medida | Diseño | Semanas | Lavado | Nota de la app |
|---|---|---|---|---|---|---|
| Creatina | 5 g al día | `fuerza_ref_kg` desde 10f (antes, `volumen_kg`) | antes/después | 12 (8 hasta la 10c) | 28 | «la reserva del músculo tarda semanas en llenarse y en vaciarse, así que alternar semanas no sirve»; desde 10f sigue: «Se mide con un ejercicio fijo, el press inclinado con mancuernas (la 1RM estimada de tu mejor serie), para que no dependa de qué entrenaste ese día; solo cuentan los días en que lo hiciste. Si vas subiendo de peso, la cifra sube sola: una mejora no prueba que fue la creatina.» |
| Hora de dormir | en la cama antes de las 00:00 | `valencia` | alterno | 6 | 1 | «el primer día de cada cambio no cuenta» |
| Pantalla antes de dormir | sin pantalla la última hora | `sueno_calidad` | alterno | 6 | 1 | «Semanas alternas sorteadas» |
| Cafeína en la tarde | nada después de las 14:00 | `sueno_horas` | alterno | 6 | 2 | «dos días de lavado por la cafeína que queda en el cuerpo» |

: Tabla 8.5. Las plantillas

**Por qué la creatina va en antes/después.** La creatina no actúa como una pastilla para dormir: se acumula en el músculo. Hultman et al. (1996) midieron que la creatina total del músculo sube alrededor de un 20 % tras 6 días a 20 g diarios y se mantiene con 2 g diarios durante 30 días más. Si la reserva tarda semanas en llenarse y en vaciarse, alternar semanas solo compararía «medio lleno» con «medio lleno». El razonamiento es correcto. El modelo de saturación de la app (28 días para llenarse y 35 para vaciarse) se trata en el capítulo 5 (sección 5.6.1); desde 10e los 28 días son con la dosis del estudio, 3 g al día, y con los 5 g de la plantilla la reserva llega al 95 % en unos 17 días (D.98). El mismo estudio midió el vaciado: 30 días después de dejar la creatina, su concentración en el músculo ya no difería del valor inicial.

**Hasta la entrega 10c la plantilla no funcionaba** (H-7-1): con 28 días de lavado sobre 4 semanas «SÍ», todos los días con la acción quedaban excluidos. Desde la 10c dura 12 semanas y quedan 14 días con la acción y 42 sin ella (sección 8.5.3).

**La medida, hasta 10f: `volumen_kg`** (H-7-14). El volumen (peso × repeticiones de todas las series efectivas del día) depende de **qué sesión** hiciste (pierna o brazo), no solo de tu fuerza: una semana con más días de pierna tiene más volumen. Y en antes/después, si estás progresando en el gimnasio, el volumen sube solo.

**Desde 10f (D.114): la fuerza de referencia, `fuerza_ref_kg`.** Es la mejor 1RM estimada del día en **un solo ejercicio fijo**, el press inclinado con mancuernas (`Macrofactor.fuerzaReferencia`):

- **Qué ejercicio cuenta** (`Macrofactor.esReferencia`). El nombre del ejercicio se pasa a minúsculas sin acentos ni signos (`Texto.slug`: «Press inclinado con mancuernas» → `press_inclinado_con_mancuernas`), y cuenta si contiene «press» y «inclin», además de «mancuern», «dumbbell» o la palabra suelta «db». Así se reconocen «Incline Dumbbell Press», «Incline DB Bench Press», «Press inclinado con mancuernas» y «Press de banca inclinado con mancuernas», y quedan fuera las aperturas y los curls en banco inclinado (que no dicen «press»). **[Diseño propio]**: no se comprobó en la sesión con qué nombre exacto exporta MacroFactor este ejercicio (Apéndice D). Cuándo falla: un nombre sin esas palabras (una abreviatura propia, o «banco inclinado con mancuernas» sin «press») no cuenta, y otro press inclinado con mancuernas que no sea el de banca (uno de pie, por ejemplo) sí contaría.
- **Cómo se calcula.** Para cada serie de ese ejercicio, la 1RM de Epley de `Macrofactor.e1rm`: peso × (1 + repeticiones/30) (con 1 repetición, el peso mismo desde el 5 nov 2026, D.156), solo con 1 a 12 repeticiones, peso mayor que 0 y sin las series de calentamiento; redondeada a 0.1 kg (capítulo 4, sección 4.6.6, y capítulo 5, sección 5.9). `fuerza_ref_kg` es la mayor del día; vacía (`null`) los días sin ese ejercicio. **[Aproximación]**: Epley estima el máximo a partir de una serie submáxima, con error que crece con las repeticiones.
- **Dónde está.** Se calcula al importar MacroFactor, al restaurar un respaldo que trae entrenos y, una vez, para lo ya importado (`Repo.completarFuerzaReferencia`). Es una variable del panel (grupo «Cuerpo»; capítulo 6, sección 6.3.1), una medida posible en cualquier experimento (sección 8.3.1), y sale en `vm_entrenos.csv` y `vm_panel.csv`; `vm_series.csv` marca con `referencia` las series de ese ejercicio.

*Ejemplo (inventado).* Un día hiciste press inclinado con mancuernas: una serie de calentamiento de 12 kg × 15, y luego 24 kg × 8 y 26 kg × 5. El calentamiento no cuenta (y además pasa de 12 repeticiones). 24 × (1 + 8/30) = 30.4 kg; 26 × (1 + 5/30) = 30.3 kg. `fuerza_ref_kg` = **30.4**. Ese día también hiciste sentadilla, que sube `volumen_kg` pero no cambia `fuerza_ref_kg`.

**Lo que mejora y lo que no.** Con un solo ejercicio, la cifra ya no depende de qué sesión tocó: los días de pierna no cuentan, porque no tienen el ejercicio (`fuerza_ref_kg` vacía, y en el experimento solo cuentan los días con dato). **Cuántos días quedan:** la plantilla deja 14 días con la acción (dos semanas tras el lavado); con el press una vez por semana serían unos 2 días medidos, menos de los 3 que pide el resultado. Por eso la nota de la plantilla pide hacerlo al menos dos veces por semana (unos 4 días con la acción), que sigue siendo poco: el intervalo saldrá ancho. Pero **la tendencia sigue**: si vas subiendo de peso, la 1RM sube semana a semana con o sin creatina, y en un antes/después toda esa subida cae en la mitad «con la acción» (el caso de la Figura 8.3). La nota de la plantilla lo dice. Además, solo cuentan los días en que hiciste ese ejercicio: con uno o dos por semana, 14 días con la acción dan pocos datos. Un experimento creado antes de 10f con esta plantilla **sigue midiendo `volumen_kg`**: la medida queda sellada.

Las otras tres plantillas son diseños razonables: efectos que aparecen y se van en uno o dos días, medidas diarias y lavado corto. **[Aproximación]**: las duraciones de lavado son razonables pero no vienen de un estudio específico.

## 8.10 Cegamiento y efecto expectativa

En los ensayos con medicamentos, ni el paciente ni el médico saben quién toma el fármaco y quién la pastilla de azúcar. Aquí eso es imposible: **sabes** si te acostaste antes o si tomaste café. Y además escribiste qué esperabas (la hipótesis) y ves el plan completo desde el primer día.

Esto importa sobre todo con medidas **subjetivas** (valencia, calidad de sueño percibida): si crees que dormir antes te hará sentir mejor, es probable que califiques tus días un poco mejor en las semanas «SÍ», aunque el sueño no haya cambiado nada. Es el **efecto expectativa** o placebo. La extensión CENT pide reportar si hubo cegamiento justamente por esto (Vohra et al., 2015).

Lo que ayuda (sin cambiar la app):

- Preferir **medidas objetivas** cuando existan: `sueno_horas` del reloj en vez de `sueno_calidad`, `rt_media` (tiempo de reacción) en vez de «me sentí despierto».
- Mirar el resultado **solo al final**.
- Para la valencia, preferir `muestreo_valencia` (avisos al azar en el momento, capítulo 3), que es menos propensa a reinterpretaciones que la valencia del día recordada en la noche. No pude comprobar cuánto reduce el sesgo de expectativa **[sin verificar]**.

**[Aproximación]**: sin cegamiento, el resultado responde a «¿hacer esto **sabiendo que lo hago** me cambia?», que es una pregunta legítima para uso personal, pero no aísla el efecto «químico» o fisiológico de la acción.

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

## 8.12 Lo que el resultado puede y no puede decir

**Puede decir**, si el experimento fue alterno, sorteado, con lavado razonable, una medida objetiva, suficientes semanas, sellaste el criterio (desde 10f) y miraste solo al final: «en mi caso, en este periodo, hacer esto se asoció con este cambio, y es poco probable que sea solo por cómo cayeron las semanas».

**No puede decir:**

- Que el efecto sea **igual** en otra época de tu vida (un N-de-1 vale para la persona y el periodo en que se hizo).
- Que el efecto sea **fisiológico** y no expectativa (sin cegamiento).
- Mientras el plan siga en curso: el resultado es **provisional** (sección 8.6.1).
- Mucho con pocas semanas: desde la 10c el intervalo es honesto y, con 4 o 6 semanas y un efecto moderado, casi siempre incluirá el cero (sección 8.6.3).
- Nada, si el diseño fue antes/después con una medida que tiende a subir o bajar sola.

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

: Tabla 8.6. Resumen de decisiones

## 8.14 Resumen del capítulo

1. Los experimentos de la app siguen la lógica de los **ensayos N-de-1**: semanas con y sin la acción, **sorteadas de verdad** (por pares desde la entrega 10c), que empiezan en lunes, con la pregunta y la hipótesis **selladas** antes de empezar. Desde 10f se puede sellar también el **criterio** (hacia dónde y cuánto), que la tarjeta aplica al terminar, y lo sellado lleva una **huella** SHA-256 que avisa si algo cambió después (H-7-10).
2. El hallazgo más grave del módulo 7: **la plantilla «Creatina» nunca podía dar resultado**, porque sus 28 días de lavado cubrían todo el periodo con la acción; y con 7 o 28 días de lavado, un plan alterno podía quedarse sin días (H-7-1). Desde la 10c no se deja sellar si alguna condición podría quedar con menos de 7 días, y «Creatina» dura 12 semanas (14 + 42 días). Desde 10f mide la fuerza en un ejercicio fijo (`fuerza_ref_kg`) en lugar del volumen, que dependía de la sesión; la progresión en el gimnasio la sigue subiendo sola (H-7-14).
3. El **intervalo** trataba los días como independientes y usaba 1.96: con autocorrelaciones de 0.3 a 0.5, daba falsas señales el 14 % a 24 % de las veces en lugar del 5 %; si se miraba cada día y uno se detenía en la primera señal, hasta el 50 % (H-7-3, H-7-4). Desde la 10c usa *t* de Welch y corrige por autocorrelación (3–4 % de falsas señales en la simulación, a costa de poder), excluye hoy y llama «provisional» al resultado mientras dura el plan. La **prueba de aleatorización** por semanas no está en la app; desde 10e la hace `vm_experimento()` en R (H-7-11), y con el sorteo por pares solo 12 semanas pueden dar *p* < 0.05.
4. El **sorteo libre** podía producir un antes/después disfrazado (33 % de las veces con 4 semanas); desde la 10c se sortea por pares, y la tarjeta avisa si el plan real quedó en bloque (H-7-5, H-9-30).
5. La **prueba de tendencia** confundía un efecto real con una tendencia en los antes/después: avisaba en el 71 % de los casos con un efecto grande y sin tendencia (H-7-6). Desde la 10c separa tendencia y condición y avisa en el 5 %; pero en un antes/después con tendencia real solo la detecta el 22 % de las veces.
6. Hasta el esquema 12, **«Terminar» no guardaba la fecha** ni se registraba si cumpliste la acción (H-7-2, H-7-8). Desde el 13 se guardan la fecha de fin y «¿Lo hiciste hoy?», con el cumplimiento por condición y un cociente secundario. No hay cegamiento: prefiere medidas objetivas y mirar solo al final.
7. «Antes y después» en Explorar es **observacional**, con hasta 56 comparaciones sin corregir (H-7-12); desde la 10c, una tarjeta por guardado y con el mismo intervalo corregido.
8. El módulo 7 identificó **14 hallazgos** (H-7-1 a H-7-14) y no cambió código. Después, el esquema 13 (entrega 10b) resolvió H-7-2 y H-7-8 (el rótulo «según el plan» llegó en la 10c), y la entrega 10c resolvió H-7-1, H-7-3, H-7-4, H-7-5, H-7-9 y H-7-13, y en parte H-7-6 (el efecto que se muestra no descuenta la tendencia) y H-7-12 (las comparaciones múltiples siguen sin corregirse). La entrega 10e resolvió H-7-7 (*g* de Hedges y la escala dicha como relativa a tus días) y H-7-11 (`vm_experimento` con la prueba de aleatorización). La entrega 10f resolvió los dos que quedaban pendientes: H-7-10 (el criterio se sella, con una huella; sigue siendo opcional) y H-7-14 (`fuerza_ref_kg` en un ejercicio fijo). En total: 12 resueltos, 2 en parte (H-7-6 y H-7-12) y ninguno pendiente.


# Capítulo 9. Reportes, visualización y motivación

> **Para qué sirve este capítulo.** Explica qué te devuelve la app a cambio de registrar: los **reportes** (semanal, mensual y anual), **«Tu año»**, los **logros** y las **campañas** semanales. Para cada uno dice qué muestra, de dónde sale cada cifra y qué decisiones de visualización se tomaron. Después discute el lado científico: un sistema de motivación ayuda a que no falten datos, pero también puede **cambiar lo que se mide**. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10a…10f» o «desde el esquema 13» y está en el Apéndice D. **Los ejemplos son inventados.**
>
> **Cómo leerlo.** Igual que los capítulos anteriores: lenguaje sencillo, cifras y condiciones del código tal cual, con el nombre de la función; cada decisión se marca como **[Validado]**, **[Aproximación]** o **[Diseño propio]**. Los problemas se numeran H-8-n (este capítulo y el 10 forman el módulo 8; los H-9-n son del módulo 9) y se detallan en `hallazgos.md`. Lo relativo a qué datos privados llegan a cada salida está en el capítulo 10.
>
> **Fuentes.** Además del código, se leyó el documento de contexto del proyecto (`VESTIGIUM_MEI_CONTEXTO.md`, v81, que no está en el repositorio). Su sección 7 fija las **reglas de privacidad** de la app; el capítulo 10 (sección 10.3.4) las compara una por una con el código. Donde ese documento describe una intención («nunca en reportes…») y el código hace otra cosa, este capítulo y el 10 lo dicen.

## 9.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Reporte** | Una imagen (y, para el mes y el año, también un PDF) que resume un periodo. La app la genera sola y la guarda en el teléfono. |
| **Periodo anterior** | El periodo de la misma duración inmediatamente antes (la semana o el mes anterior). Los reportes se comparan contra él. |
| **Gamificación** | Usar elementos de juego (puntos, medallas, retos, rachas) en algo que no es un juego (Deterding et al., 2011). |
| **Logro** | Una medalla que se gana al llegar a una meta (100 días escritos, un día de 20 000 pasos). |
| **Racha** | Días **seguidos** cumpliendo algo. Un solo día sin cumplir la rompe. |
| **Constancia** | Cumplir algo **la mayoría** de los días de una ventana (por ejemplo, 25 de 30), sin necesidad de racha. |
| **Campaña** | Un reto opcional de lunes a domingo («5 noches de 7 horas»). |
| **Reactividad** | Que medir algo cambie lo que se mide (capítulo 3, sección 3.14). |
| **Ley de Campbell** | Cuanto más se usa un indicador para premiar o decidir, más presión hay para distorsionarlo y más deja de reflejar lo que medía (Campbell, 1979). |

: Tabla 9.1. Mini-glosario

## 9.2 De dónde salen las cifras: `Periodo.calcular`

Los reportes y «Tu año» no calculan nada por su cuenta: usan un mismo resumen del periodo, `Periodo.calcular(ini, fin)` (`ciencia/Periodo.kt`). Lo importante, regla por regla:

| Cifra | Cómo se calcula exactamente |
|---|---|
| **Días con diario** | Días con texto en el diario **o** con valencia del día. |
| **Ánimo del día** (`valDe`) | La valencia del diario, como `valencia` en el panel y en R (desde la entrega 10c). Hasta entonces, si no había valencia del diario, se usaba el promedio de los registros de ánimo de ese día. |
| **Ánimo medio** | Promedio de los «ánimos del día» que existen. El reporte lo rotula «ánimo medio del diario (de 7)» y, si en el periodo hubo días con valencia solo en los avisos (`diasSoloAvisos`), añade «*N* días solo con avisos no cuentan». |
| **Mejor y peor día** | El día con mayor y menor «ánimo del día»; en empate, el primero. Con el mejor día se muestra su «mejor momento» (hasta 90 caracteres). |
| **Racha de diario** | La racha más larga **dentro del periodo** de días con texto o valencia. |
| **Hábitos cumplidos** | Hábitos activos marcados ÷ (hábitos activos × días con documento de diario). No cuenta el hábito ligado a las recaídas (`Privado.habitosVisibles`). |
| **Sueño medio** | Promedio de las noches del reloj con **más de 120 minutos** (las más cortas se descartan como siestas o errores). |
| **Hora de dormir media** | Promedio de las horas de inicio, con la misma regla que el panel y R (`Exportar.horaDecimal`, desde 10c): de noche, las anteriores a las **12:00** suman 24 (la 01:00 cuenta como 25:00) para no promediar la 01:00 con las 23:00 como si fuera mediodía. Antes el corte era a las 15:00, y una siesta larga que empezara a las 13:00 contaba como 37:00. Desde 10f (D.109), cada hora es la hora local del lugar donde estabas esa noche: se lee con el desfase guardado en la propia marca (`Fechas.localDe`). Antes se pasaba a la zona que tenía el teléfono al calcular el reporte, y una noche de viaje salía corrida tantas horas como la diferencia de zona. |
| **Pasos** | Promedio de los días con pasos > 0. Km: los del reloj o, si no hay, pasos × 0.00075. |
| **Mejor marca** | La mayor repetición máxima estimada del periodo con `Macrofactor.e1rm`, la misma que usan la exportación (`e1rm_kg`), Fuerza, Totales y `vm_fuerza` en R (desde 10c, D.72): peso × (1 + repeticiones/30) (**fórmula de Epley**), solo en series de **1 a 12** repeticiones y sin calentamientos, redondeada a 0.1 kg. Antes se aceptaban hasta 15 repeticiones y los calentamientos. Ejemplo: series de 100 × 15, 120 × 5 y un calentamiento de 130 × 8 → antes 164.7 kg (el calentamiento); ahora **140 kg**. Tus apps usa la misma función. |
| **Gasto e ingresos** | Por el **día del diario** (`Gasto.dia`, `Ingreso.dia`: antes de las 5:00 cuenta para el día anterior), igual que el panel; los ingresos se contaban por su fecha de calendario hasta 10c, así que un ingreso de madrugada caía en otro día que su gasto. Totales usa la misma regla desde 10c (D.77). |
| **Tazas de café** | Cafeína total ÷ 95 mg. |
| **Personas, temas** | Las @ y # del diario (una vez por día) y de los momentos no privados. |
| **Emociones, pensamientos y con quién** (desde el esquema 18, D.146) | Salen de los registros de ánimo no privados del periodo (`Catalogo.esRegistro`, los de un aviso y los que abres tú), con `Periodo.animoDe`. Cada emoción cuenta **una vez por registro** (dos registros con «ansiedad» = 2); «no sé» no cuenta; «otro» se nombra con lo que escribiste; y se dice en cuántos de esos registros fue «mucho» (intensidad 3 de 3). Igual con los pensamientos (un registro con dos cuenta en los dos) y con quién (de cuántos registros que lo dicen). Son **conteos para leer**: no entran a ningún promedio ni al panel, y no se pesan por intensidad. **[Diseño propio]** |
| **Comparación con el periodo anterior** | Solo si el periodo anterior tiene **al menos 3 días** con dato. |

: Tabla 9.2. De dónde salen las cifras: Periodo.calcular

**Detalle a saber** (H-8-10 y H-5-15, resueltos en 10c, D.77). Hasta la entrega 10c, el «ánimo del día» mezclaba dos instrumentos distintos: la valencia recordada del diario y, cuando faltaba, el promedio de los registros momentáneos. El capítulo 3 (sección 3.3.4) explica por qué no son la misma medida: si un mes tenía pocos diarios y muchos avisos, su «ánimo medio» no era comparable con el de un mes con diario completo, ni con `mean(panel$valencia)` en R. Ahora el ánimo medio usa solo la valencia del diario, y el reporte dice cuántos días tenían solo avisos. Ejemplo (inventado): una semana con valencia del diario 5, 6 y 4 en tres días y avisos con promedio 3 en otros dos: antes, ánimo medio (5 + 6 + 4 + 3 + 3) / 5 = 4.2; ahora (5 + 6 + 4) / 3 = **5.0**, «2 días solo con avisos no cuentan». La **activación** del día sigue completándose con los avisos cuando falta la del diario.

## 9.3 Los reportes

### 9.3.1 Cuándo se generan y dónde quedan

La tarea periódica (`avisos/Trabajo.kt`) genera, sin que lo pidas:

- **El semanal**, cada **lunes desde las 8:00**, de la semana anterior: una imagen PNG. Si pides el de la semana o el mes en curso, llega solo hasta hoy (desde el 5 nov 2026, D.155). Antes contaba los días que aún no pasaban: un miércoles decía «3/7 días con diario» y los promedios por día salían a menos de la mitad.
- **El mensual**, cada **día 1 desde las 9:00**, del mes anterior: PNG y PDF.
- **El anual**: a petición (y «Tu año» avisa el 31 de diciembre desde las 9:00).

Se guardan en las carpetas públicas del teléfono: **`Pictures/VestigiumMei`** (imágenes) y **`Download/VestigiumMei`** (PDF), con una notificación. Desde el módulo 10a, los que se generan solos van **siempre en modo discreto** (iniciales en vez de nombres) y **sin la frase del mejor momento**; los que pides a mano siguen tu ajuste de modo discreto. Las consecuencias para la privacidad están en el capítulo 10 (H-8-4).

### 9.3.2 Qué muestran (`Reportes.cuerpo`)

Arriba, hasta **9 cifras** (días con diario, ánimo medio, sueño, pasos, pantalla, palabras, gasto, entrenamientos, música). Después, **solo las secciones que tienen datos**, cada una con un nombre en español y otro en latín (*Animus*, *Corpus*, *Mens*, *Homines*, *Orbis*, *Cultura*, *Pecunia*, *Telephonum*, *Memorabilia*):

- **La semana de un vistazo** (solo el semanal; Figura 9.1).
- **El mes de un vistazo** (solo el mensual): calendario del ánimo, curva del sueño, comparación con el mes anterior, nubes de personas y temas, y un **mapa abstracto**: cada lugar guardado es un punto sobre una cuadrícula, **sin calles ni nombres**.
- **Ánimo**: gráfica de valencia (con la activación detrás) o, en el anual, un cuadro por día del color del ánimo; mejor y peor día, cambio frente al periodo anterior, el mejor día de la semana (si el periodo pasa de 13 días), hábitos y racha. Desde el esquema 18 (D.146), también tres líneas de los registros de ánimo: *affectus* («lo que más sentiste»: las 5 emociones más nombradas, con cuántas veces fueron «mucho»), *cogitatio* (los 3 pensamientos más frecuentes) y *comites* (con quién, con de cuántos registros sale). La sección aparece aunque no haya valencia del diario si hay registros con estos datos. Ejemplo (inventado): «lo que más sentiste: ansiedad 6 (2 «mucho») · calma 4 · cansancio 3».
- **Cuerpo, Mente, Gente, Mundo, Cultura, Dinero, Teléfono, Lo memorable** (hitos y logros del periodo).

**El modo discreto** (`Config.discreto`, apagado por omisión), en los reportes, cambia los nombres de las personas por iniciales (`@ana_luisa` → «A.L.», `Reportes.persona`) y, desde el esquema 18, quita las tres líneas de emociones, pensamientos y compañía. No toca nada más del reporte (en otras pantallas también oculta, por ejemplo, los nombres de las emociones). Como los reportes que se generan solos van siempre en modo discreto (9.3.1), esas líneas solo salen en los que pides a mano.

Las pantallas **Semana** y **Año** usan la misma `Periodo.animoDe`: Semana suma las tres líneas debajo de «Momentos de la semana», y Año las dos primeras en «Lo más frecuente» (siempre que el modo discreto esté apagado).

### 9.3.3 Decisiones de visualización

La Figura 9.1 imita «La semana de un vistazo» con datos inventados y las reglas de `semanaVisual`:

- **Ánimo**: una **medalla** por día con el color del ánimo y el número (`%.0f`). Desde 10c es la valencia del diario, un entero de 1 a 7, así que no se redondea nada; hasta entonces podía ser un promedio de avisos y se redondeaba la mitad hacia arriba (4.5 → 5, 6.5 → 7). El color va del color de alerta (1), pasando por un gris neutro (4), al color del tema (7) (`Lienzo.colorAnimo`). Sin dato, un círculo punteado.
- **Diario**: hojas apiladas; una hoja = max(10, ⌈máximo de la semana / 10⌉) palabras, hasta 10 hojas. El día que más escribiste llega a 10 hojas.
- **Sueño**: barras de 0 a 10 h, con una línea punteada en **7 h** y la barra en tono de alerta si la noche duró **menos de 6.3 h**.
- **Pasos**: ⌈pasos / 2 000⌉ huellas, hasta 10.

![Figura 9.1. «La semana de un vistazo», imitada con datos inventados y las mismas reglas del código. El ánimo de la figura trae dos días con decimales (4.5 y 6.5), como podía pasar hasta 10c; desde entonces el número de la medalla siempre es un entero. Script: `figuras/fig23_reporte_semanal.py`.](figuras/fig23_reporte_semanal.png){width=100%}

**Qué dice la investigación sobre gráficas.** Cleveland y McGill (1984) midieron qué tan bien la gente lee distintas codificaciones: la **posición sobre una escala común** (la altura de una barra con su eje) se lee con mucha más precisión que el **área** o el **color**. Aplicado a la app **[Aproximación]**:

- Las barras de sueño son la codificación más precisa del reporte, y además llevan el número.
- La medalla del ánimo codifica con **color**, que se lee mal, pero lleva el número escrito, lo que lo compensa. Hasta 10c, cuando el ánimo podía ser un promedio, el redondeo perdía información (un 4.5 y un 5.4 se veían igual, «5»); con la valencia del diario, que es entera, ya no.
- Las hojas y huellas son **conteos de iconos**: se leen bien para comparaciones gruesas («el domingo escribí mucho más»), no para cifras exactas.
- La línea de 7 h y el corte de 6.3 h son **[Diseño propio]**: 7 h coincide con la recomendación habitual mínima de sueño para adultos (no verificada en esta sesión **[sin verificar]**); el 6.3 h no tiene una fuente que yo conozca.
- El color «de alerta» para el ánimo bajo y el sueño corto **juzga** el dato (rojo = malo). Para un resumen personal es útil, pero refuerza la idea de que hay valores «correctos», lo que se relaciona con la sección 9.7.

### 9.3.4 Comparar con el periodo anterior

`Reportes.cambio` compara cuatro cifras con el periodo anterior: el ánimo, las horas de sueño, los pasos y los minutos de pantalla. Decide así, en este orden:

1. Si la diferencia es menor que **0.05** (o **0.5** en las cifras sin decimales: pasos y pantalla), «igual que el periodo anterior».
2. **Desde 10e** (D.101, H-8-11): si es menor que su **error típico**, «parecido al periodo anterior (la diferencia cabe en lo que varían tus días)». El error típico (`Periodo.errorDif`) es

$$EE = \sqrt{\frac{s_1^2}{n_1} + \frac{s_2^2}{n_2}}\qquad\qquad (9.1)$$

   con $s^2$ la varianza de los días de cada periodo (dividiendo entre *n* − 1) y *n* sus días con dato. Mide cuánto podría moverse la diferencia de promedios solo porque los días varían. Se calcula si los dos periodos tienen al menos 3 días; si no, se salta este paso.
3. Si no, «+0.4 de ánimo frente al periodo anterior» (o «−», con la cifra y su unidad).

*Ejemplo (inventado).* Esta semana, ánimo 5, 6, 4 y 5 (media 5.0, varianza 0.667); la anterior, 4, 5, 3, 5 y 4 (media 4.2, varianza 0.70). *EE* = √(0.667/4 + 0.70/5) = √0.307 = 0.55. La diferencia, 0.8, es mayor: «+0.8 de ánimo frente al periodo anterior». Si la anterior hubiera sido 4, 6, 3, 5 y 5 (media 4.6, varianza 1.30), *EE* = √(0.167 + 0.26) = 0.65 y la diferencia, 0.4, es menor: «parecido al periodo anterior».

**Hasta 10e no había ninguna noción de ruido**: una semana con 3 días de ánimo podía variar medio punto respecto de la anterior por puro azar, y el reporte lo presentaba como un cambio. **[Aproximación]**: el umbral de un error típico es poco exigente (una diferencia así de grande sale por azar alrededor de un tercio de las veces) y, como no descuenta que los días seguidos se parecen (la autocorrelación, capítulo 6), el error real es mayor que el calculado: el filtro se queda corto, y algunas diferencias que se reportan como cambio siguen siendo ruido. Tampoco se aplica cuando el periodo actual tiene menos de 3 días con dato. En los experimentos (capítulo 8) y en las correlaciones (capítulo 6) la app calcula intervalos completos; en los reportes, solo este filtro. Conviene leer estas flechas como «qué pasó», no como «qué cambió de verdad».

### 9.3.5 «Tu año»

`TuAnio.calcular` usa el mismo `Periodo` del año y le suma: equivalencias («escribiste el equivalente a *N* páginas», a 300 palabras por página), un día típico armado con lo más frecuente, lo que pasó una sola vez, **el día más raro** (`Analisis.atipicos` con umbral 2.8, sobre 15 variables fijas; desde 10e, D.100, «Tu año» le pasa `desde = null` y usa todos los días del año elegido; antes, `atipicos` solo devolvía días de los últimos 365 contados desde hoy, así que al ver un año pasado podía faltar o quedarse en parte del año, H-14-3), récords, **la relación más fuerte del año** (la correlación no obvia con mayor |*r*|, con *q* < 0.05 y *n* ≥ 30, si el año tiene al menos 40 días), la palabra del año, un «arquetipo», tus medallas y cómo te fue con las **predicciones de enero** (8 preguntas: obras, países, ciudades, pasos, sueño, **ánimo medio**, persona más mencionada y palabras). Desde el esquema 18, el día típico suma «sentías» (la emoción más nombrada del año) y «pensabas» (el pensamiento más frecuente), con la regla de la sección 9.2 y solo sin modo discreto.

Dos observaciones:

- **«La relación del año» es la más fuerte de muchas.** Elegir el máximo |*r*| entre cientos de pares sobrestima su tamaño: es la que tuvo más suerte además de ser real (un sesgo de selección; el método y su criterio para «Tu año» están en el capítulo 6, secciones 6.8 y 6.10). Que pase la corrección por comparaciones múltiples (*q* < 0.05) ayuda, pero el valor de *r* mostrado será, en promedio, más alto que el verdadero. **[Aproximación]**.
- **Predecir tu ánimo medio en enero** crea una expectativa sobre la variable que luego mides. No hay forma de saber cuánto influye, pero es una forma de reactividad (sección 9.7).

### 9.3.6 El reporte para terapia (desde D.119)

**Qué es.** Un PDF tamaño carta, de varias páginas, que resume los **últimos 7 o 14 días** para llevarlo a una sesión de psicoterapia. A diferencia de los otros reportes, no se genera solo ni se guarda en la galería: se arma en Reportes › «Para terapia» y se abre directo el menú de compartir de Android (el archivo queda en la carpeta interna `cache/export/terapia/`, y cada reporte nuevo borra el anterior). Va en blanco y negro con un solo color de acento, sin los colores ni las letras del tema, para que se pueda imprimir. El cálculo está en `Terapia.calcular` (`ciencia/Terapia.kt`) y el dibujo en `ReporteTerapia.pdf` (`reportes/ReporteTerapia.kt`).

**El periodo.** Por omisión termina **ayer** (el día lógico de hoy menos uno, porque hoy aún no se cierra) y cubre 7 o 14 días hacia atrás (`Opciones.ini = fin − (días − 1)`). Se puede elegir otro último día. Pensado para quien va a terapia en un día fijo: el de 7 días cubre justo de una sesión a otra; el de 14, una sesión quincenal. La comparación con el «periodo anterior» usa los 7 o 14 días inmediatamente antes (la misma regla de la sección 9.3.4, con `Periodo.errorDif`).

**Qué lleva, en este orden** (todo sale de funciones que ya existían; donde hay una regla nueva, se dice). Desde D.122, cada parte es una de **8 secciones** que se encienden o apagan al generar el reporte, más un interruptor «Todo» que las enciende o apaga juntas (`Opciones.conTodo`). La primera fila, «qué tan completo está», sale siempre; la tabla día por día lleva solo las columnas de las secciones encendidas y, si no hay ninguna, no sale. Entre paréntesis, la sección (`Opciones`) y cómo empieza:

| Sección | Qué muestra | De dónde sale |
|---|---|---|
| Qué tan completo está (siempre) | Días con diario de *N*; avisos de ánimo respondidos de los mostrados y su tasa; días con ánimo solo en los avisos | `Periodo.diasDiario`, `AvisosAnimo.cifras`, `diasSoloAvisos` |
| Día por día (columnas según las secciones) | Una tabla: ánimo y energía (1–7), horas de sueño, calidad (1–5), kcal menos el objetivo, minutos de gimnasio; un asterisco en los días marcados como difíciles | `Repo.dias`, `Periodo.activacion` y `suenoPorDia`, `NutricionDia` |
| Ánimo (1, `animo`, encendida) | Gráfica de ánimo y energía por día; ánimo medio del diario y su cambio; días difíciles; las 5 emociones más nombradas en los registros (desde el esquema 18, D.146, cada una cuenta una vez por registro, «otro» sale con lo que escribiste y al lado va cuántas veces fue «mucho», por ejemplo «Ansiedad (2 «mucho»)»; «no sé» sigue contando) y qué parte de las nombradas son desagradables; en qué pensabas; qué parte de los avisos estabas con alguien y el ánimo solo frente a acompañado; cómo viviste la exigencia del día (reto, amenaza, pérdida, rutina); el error medio de la predicción del día siguiente | `Periodo`, `Terapia.emociones`, `Catalogo.pensamientoDe`, `Momento.con_quien`, `Dia.apreciacion`, `prediccion_manana` |
| Cuestionarios (2, `cuestionarios`, encendida) | WHO-5 (0–100), PSS-4 (0–16) y UCLA-3 (3–9) respondidos en el periodo, con el texto de lectura de `Escalas` y hasta 3 respuestas anteriores. El TIPI no entra: la personalidad no cambia en una semana | `Repo.respuestas`, `Instrumento.lectura` |
| Sueño (3, `sueno`, encendida) | Barras de horas por noche; media y cambio; hora media de dormir y de despertar con su desviación estándar; calidad media; noches con pesadilla (solo el número; desde el esquema 17 con «¿fue pesadilla?», y en los días de antes, el tipo «ansioso o pesadilla», D.127); minutos de pantalla en la hora antes de dormir | `Periodo`, `Exportar.horaDecimal`, `Catalogo.pesadillaDe`, `PantallaDia.cama_min` |
| Actividad y rutina (4, `rutina`, encendida) | Pasos; minutos de ejercicio del reloj; minutos al aire libre y horas en casa al día; hábitos cumplidos; dolor medio y máximo; días con los contextos «enfermo», «trabajo» y «académico»; pantalla y desbloqueos | `Periodo`, el panel (`aire_libre_min`, `en_casa_min`) |
| Contacto con otros (4, `rutina`) | Días en que estuviste en persona con familia, pareja, amistades o expareja, y días en que solo pensaste en ellas; llamadas. **Sin nombres** | El panel (`vi_*`, `penso_en_*`), `Periodo.llamadas` |
| Gimnasio y ejercicio (5, `cuerpo`, encendida) | Días de gimnasio (con series en MacroFactor o con el contexto «gym») y la duración de cada uno; las otras sesiones del reloj por tipo | `EntrenoDia.minutos`; si falta, la sesión de fuerza o pesas del reloj de ese día; `Sesion` |
| Alimentación (5, `cuerpo`) | Calorías y proteína contra el objetivo de cada día; carbohidratos y grasa contra el suyo; promedio diario de cafeína, alcohol, azúcares añadidos, fibra, omega-3 y agua; hora de la última cafeína; comidas al día y hora de la primera y la última | `NutricionDia` (MacroFactor), `Terapia.contraObjetivo` |
| Sustancias (6, `sustancias`, encendida) | Por cada sustancia que no ocultaste: días, cantidad total, nivel máximo (0–10), malestar medio del día siguiente (0–5) y los síntomas más frecuentes | `Repo.consumos`, `Sustancias.maximo` |
| Momentos del periodo (7, `momentosPeriodo`, encendida) | «Lo mejor» de tu mejor día y «lo peor» de tu peor día, tal como los escribiste | `Periodo.mejorDia` y `peorDia` |
| Lo mejor y lo peor de cada día (dentro de la 7, `momentosDia`, apagada) | Los mismos dos textos de cada día | `Dia.mejor_momento`, `peor_momento` |
| Seguimiento privado (8, `privado`, apagada) | Impulso medio del día (0–5) y por día en la tabla; impulsos registrados y cuántos no tuvieron una recaída en las 3 h siguientes; días con recaída de los días con respuesta; detonantes más frecuentes (desde el esquema 18, el detonante «otro» se cuenta con lo que escribiste en `detonante_otro`, igual que en la pantalla Privado, `Recaidas.resumen`); medicamentos anotados | `Dia.impulso`, `recaida`, `detonantes`, `medicamentos`; momentos `impulso` y `recaida`; `Terapia.superados` (la misma regla de `Recaidas.resumen`) |

: Tabla 9.3. El reporte para terapia (desde D.119)

**Lo que nunca lleva**, aunque exista: el diario completo, los sueños escritos, las cartas, las notas, los títulos de la agenda, el contenido o los nombres de los chats, los lugares y el GPS, los nombres de personas (en los textos de «lo mejor» y «lo peor», cada `@ana_luisa` sale como «A.L.», `Terapia.sinNombres`), el dinero, la música, YouTube, los libros, el volumen y los récords del gimnasio, los alimentos, el peso, la frecuencia cardiaca, la HRV, la SpO₂, el tiempo de reacción y la libido. Tampoco lleva correlaciones, valores *p* ni experimentos: son análisis de una sola persona que, fuera de contexto, se leen como hallazgos firmes. El reporte describe; no interpreta ni etiqueta. **[Diseño propio]**: la selección se hizo pensando en lo que se suele revisar en una sesión (ánimo, sueño, actividad, contacto, consumo, alimentación), no siguiendo un formato clínico validado.

**La regla nueva: calorías contra el objetivo** (`Terapia.contraObjetivo`). Para cada día con registro y con objetivo, la diferencia es *lo que comiste − tu objetivo* y su porcentaje es esa diferencia entre el objetivo. Un día cuenta como «dentro» si el porcentaje está entre −10 % y +10 % (los extremos incluidos), «por encima» si pasa de +10 % y «por debajo» si baja de −10 %. Un día con menos de 300 kcal (`MINIMO_KCAL`, la misma regla de `Periodo`) se toma como **sin registro** (se te olvidó anotar), no como un día de ayuno: no cuenta en los promedios y la nota lo dice. La proteína usa la misma cuenta sobre los días con registro. **[Diseño propio]**: el margen de 10 % es una convención para no marcar como «fallo» variaciones pequeñas; no viene de una guía.

*Ejemplo (inventado).* Tres días con objetivo de 2,000 kcal: comiste 2,300, 1,700 y 2,050. Las diferencias son +300, −300 y +50; la media, **+17 kcal** (+0.8 %). Por porcentaje: +15 % (por encima), −15 % (por debajo) y +2.5 % (dentro). Un cuarto día con 120 kcal anotadas no cuenta. El reporte diría: «2,017 kcal al día contra un objetivo de 2,000: +17 kcal (+0.8 %) · dentro de ±10 %: 1 día · por encima: 1 día · por debajo: 1 día», y la barra de cada día muestra su diferencia.

Esta media esconde los extremos: un día de +300 y otro de −300 se anulan. Por eso el reporte da también el conteo de días fuera del margen y la barra por día.

**Las otras cuentas nuevas:**
- **Emociones desagradables** (`Terapia.emociones`): de todas las emociones nombradas en los registros de ánimo del periodo, la parte con tono −1 en `Catalogo.SENTIMIENTOS`. Las mixtas (nostalgia, confusión, sorpresa) cuentan en el total; «no sé» y «otro» no, porque no tienen tono. *Ejemplo (inventado)*: ansiedad, ansiedad, calma y nostalgia → 2 de 4 = **50 %**. Es una proporción de **menciones**, no de registros: un registro con tres emociones pesa tres veces. **[Diseño propio]**.
- **Regularidad del horario**: la desviación estándar muestral (con *n* − 1) de las horas de dormir y de despertar de las noches del reloj con más de 2 h. *Ejemplo (inventado)*: 23:30, 00:30 y 01:30 → media 00:30, desviación **60 min**. No es el índice de regularidad del sueño (*Sleep Regularity Index*), que compara minuto a minuto; es una aproximación más simple. **[Aproximación]**.
- **Error de predicción**: el promedio, con signo, de *ánimo del día − predicción de la noche anterior*, el mismo `error_prediccion_signo` del panel (capítulo 3). Positivo: los días salieron mejor de lo que predijiste.
- **Gimnasio**: un día cuenta si MacroFactor tiene series ese día o si marcaste el contexto «gym». La duración es la de MacroFactor (`EntrenoDia.minutos`); si no la hay, la suma de las sesiones del reloj de tipo «fuerza» o «pesas» de ese día. Esas sesiones no se cuentan otra vez entre las «otras sesiones». **[Diseño propio]**.

**Las secciones elegidas no se guardan.** Cada vez que se abre la pantalla empiezan igual: encendido todo lo que el reporte llevaba desde D.119; lo mejor y lo peor de cada día y el seguimiento privado, apagados. «Todo» enciende también esos dos. Así, lo privado solo sale si lo eliges en ese momento, y un reporte compartido con prisa no lo lleva por error (capítulo 10, sección 10.3.4).

**Cuándo falla o engaña:**
- **Pocos datos.** Con pocos días de diario o pocos avisos respondidos, las medias son inestables. Por eso la primera sección dice cuántos hay; la comparación con el periodo anterior exige al menos 3 días en cada lado (sección 9.3.4).
- **Avisos que faltan sin azar.** Si dejas de responder los avisos justo en los peores momentos, las emociones y el ánimo «solo o acompañado» se ven mejor de lo que fueron. El reporte no puede saberlo; la tasa de respuesta es la única pista.
- **Contacto.** Solo cuenta lo que nombraste con `@` y la relación que le diste a cada persona; un día sin nombres no es un día sin contacto (por eso se dice de cuántos días hay datos).
- **Alimentación.** Depende de que MacroFactor esté importado hasta el último día del periodo; un día a medio anotar (más de 300 kcal pero incompleto) cuenta como «por debajo».
- **Cuestionarios.** El WHO-5 y el PSS-4 se aplican aquí con un periodo de 7 días en lugar de 2 semanas y un mes (capítulo 3); sus puntos de corte se validaron con el periodo original.
- **Texto libre.** «Lo mejor» y «lo peor» van tal como se escribieron: aunque los `@nombres` salen como iniciales, un nombre escrito sin `@` sí sale.

## 9.4 Logros

### 9.4.1 Qué son (`ciencia/Logros.kt`)

Medallas de cuatro niveles (bronce, plata, oro, legendario) en 9 categorías: Diario, Mundo, Cuerpo, Mente, Cultura, Gente, Constancia, Curiosos y Tuyos. **Se calculan de tus datos cada vez**; lo único que se guarda es cuáles ya viste. Tres tipos:

| Tipo | Ejemplos (metas exactas) | Función |
|---|---|---|
| **Cantidad** | Diario: 1, 7, 30, 100, 365 y 1000 días; palabras: 10 000 a 1 000 000; momentos: 10, 100 y 1000; avisos contestados: 50, 250 y 1000 | `escalones` |
| **Récord** | 20 000 pasos en un día; 42.195 km en un día; reacción con mediana < 250 ms | — |
| **Constancia** | «Escribe 20, 25 o 28 de 30 días»; 8000 pasos 15, 20 o 25 de 30 días; 7 h de sueño 15, 20 o 25 de 30 noches; entrenar 16, 20 o 24 de 56 días; cada hábito activo 15, 20 o 25 de 30 días (salvo el ligado a las recaídas, desde el módulo 10a) | `constancia` |

: Tabla 9.4. Qué son (ciencia/Logros.kt)

Además hay **rachas** de diario (7, 30 y 100 días seguidos), **secretos** (aparecen al lograrlos: «Día perfecto», «Remontada», «Noctámbulo», «Desahogo»…) y **logros propios**: eliges una fuente (un hábito, una @persona o #tema, o **cualquier variable del panel** que no sea privada), un umbral y un modo (total, racha, días o constancia en una ventana). Desde el esquema 17, con una variable el umbral tiene dirección: «pasa de» (un día cuenta si el valor es mayor que el umbral, como antes) o «queda en o menos de» (`LogroPropio.umbral_max`: cuenta si es menor o igual, por ejemplo «pantalla de 120 min o menos»), y los logros propios se pueden corregir además de borrar. «Suma total» ya no se ofrece con un hábito, porque daba lo mismo que «días». Las metas medidas por días tienen la misma opción, «al menos» o «como mucho» (`Meta.umbral_max`, en `Metas.valor`). **[Diseño propio]**. Desde el módulo 10a no se ofrecen ni se muestran los logros propios sobre algo privado: el hábito ligado a las recaídas o una variable privada.

Al abrir un logro, `comoSeConsiguio` dice en qué día de registro llegó, el ánimo de ese día y una cita de hasta **22 palabras** del diario de esa noche (no en los secretos).

### 9.4.2 Una buena decisión: constancia en lugar de rachas

La propia app lo explica: «de constancia (25 de 30 días; no hace falta racha)». Las rachas castigan un solo día perdido con volver a cero, lo que puede desanimar justo cuando más falta hace seguir; la constancia tolera días malos. **[Diseño propio]** con buen fundamento: para un registro de por vida, lo que importa es **no abandonar**, y la meta «25 de 30» premia eso sin convertir un tropiezo en fracaso. (Hay rachas de diario, pero son pocas y no son el centro del sistema.)

### 9.4.3 Lo que premian, y por qué importa para los datos

Conviene separar los logros por **qué** premian:

| Premian… | Ejemplos | Efecto sobre los datos |
|---|---|---|
| **Registrar** | días de diario, avisos contestados, momentos, cuestionarios, pruebas de reacción | Menos datos faltantes (bueno: capítulo 3, sección 3.15). Riesgo: entradas de relleno para cumplir. |
| **Una conducta** | pasos, sueño ≥ 7 h, entrenar, hábitos, enfoque | Cambian la conducta medida: es su objetivo (es una intervención). Riesgo: confundir el análisis observacional (sección 9.7). |
| **Un valor de la medida subjetiva** | «Día perfecto» (calificar un día con 7 de 7), «Remontada» (un ≥ 6 tras un ≤ 2 en los 3 días de calendario anteriores; hasta el 5 nov 2026, en los 3 días con valencia anteriores, que podían estar a semanas de distancia) | Premian **un número que tú escribes**. Riesgo directo de sesgar la valencia (H-8-6). Se quedan por decisión del autor (D.117). |
| **Un rendimiento** | «Rayo» (reacción < 250 ms; desde el 5 nov 2026 el avance es 250 / tu mejor mediana, sin unidad, y ya no «208 / 250 ms») | Incentiva practicar la prueba; los tiempos pueden mejorar por práctica y no por estar más despierto. |

: Tabla 9.5. Lo que premian, y por qué importa para los datos

Los dos logros del tercer grupo son **secretos** y se ganan una sola vez, lo que reduce mucho el incentivo: no sabes que existen hasta que ocurren. Aun así, una vez descubiertos, son medallas por escribir cierto valor en la escala que más se analiza. **[Diseño propio]**, de bajo riesgo en la práctica, pero contrario al principio de no premiar el valor de una medida.

**Se quedan, por decisión del autor (10f, D.117; H-8-6).** El módulo 8 propuso cambiarlos para que premiaran registrar y no un valor, o quitarlos; el autor decidió dejarlos como están (`Logros.kt` no cambió). El riesgo que queda, dicho con precisión: **un incentivo pequeño a sesgar la valencia**, por ejemplo a poner un 7 en lugar de un 6, o un 6 en lugar de un 5 justo después de un día de 2. Lo acotan tres cosas del código: cada uno se gana **una sola vez** (meta 1, `dia_perfecto` con el primer día de valencia 7 y `remontada` con la primera remontada), así que el empujón desaparece en cuanto se consigue; no hay niveles que perseguir; y la valencia que piden es la del diario de la noche, no la de los avisos al azar (`muestreo_valencia`), que ningún logro premia. Lo que **no** lo acota: la protección de ser «secreto» casi no vale aquí, porque el único usuario es el autor, que sabe que existen y qué piden. **Cómo leerlo en los datos**: si alguna vez sospechas el efecto, compara la proporción de 7 (y de subidas a ≥ 6 tras un ≤ 2) antes y después de la fecha en que se ganó cada logro (`Logro.conseguido`: el primer día que lo cumplió), y contrasta la valencia del diario con `muestreo_valencia` esos días. No lo comprobé con datos reales.

## 9.5 Campañas

`ciencia/Campanas.kt`. Un reto de lunes a domingo con una meta de días. Los retos y la meta que propone cada uno (al aceptar se puede elegir de 3 a 7 días):

| Reto | Cuenta el día si… | Meta |
|---|---|---|
| Noches de 7 horas | el reloj marca ≥ 420 min de sueño | 5 |
| Dormir antes de medianoche | el sueño empieza entre las 18:00 y las 23:59 (desde 10f, en la hora local de esa noche, `Fechas.localDe`) | 4 |
| 8000 pasos | pasos ≥ 8000 | 5 |
| Escribir el diario | hay texto en el diario | 6 |
| Una hora de enfoque | ≥ 60 min con el cronómetro (sin siestas) | 5 |
| Menos de 3 horas de pantalla | pantalla < 180 min | 4 |
| Entrenar o escalar | un entreno con series, una sesión de escalada o ejercicio del reloj | 3 |
| Leer | un libro o artículo marcado ese día, o lectura en el cronómetro | 5 |
| Prueba de reacción | al menos una prueba | 5 |
| Contestar los avisos de ánimo | al menos 2 avisos contestados | 5 |
| Cada hábito activo | el hábito marcado | 5 |

: Tabla 9.6. Campañas

`Campanas.estado`: cada día cumplido avanza una etapa en un «mapa» narrativo que cambia con el tema (de Roma a Alesia, de Chang'an a Antioquía…); se **gana** al llegar a la meta y se **pierde** cuando ya no da tiempo (días cumplidos + días que quedan < meta). Ganar campañas da logros (1, 5, 20 y 52 victorias).

**Qué son científicamente.** Una campaña de sueño **es una intervención**: durante esa semana intentas dormir más. Eso está bien para tu vida, pero tiene una consecuencia para el análisis. **Hasta el esquema 12, las campañas no se guardaban como variable del panel ni se exportaban como tal** (solo vivían en los ajustes, `Config.campanas`) (H-8-7). Si en las semanas con campaña de sueño también estuviste de mejor ánimo, la app veía «dormir más se asocia con mejor ánimo», sin poder distinguir si fue el sueño o el empujón motivacional de la campaña (o qué te llevó a elegir esa campaña esa semana). Un experimento personal (capítulo 8) separa esto; una campaña, no; desde el esquema 13, al menos queda registrada.

**Desde el esquema 13 (P-11) la campaña es una variable del panel.** Hay una columna `campana_<reto>` por cada reto que hayas tenido. Por ejemplo, `campana_sueno7`, o `campana_hab_<id>` para un hábito. Vale **1** los días que tenías ese reto aceptado y **0** los demás, desde el primer día de tu primera campaña; antes, sin dato. Además está `campanas_n`, cuántas tenías ese día, porque puede haber hasta tres a la vez. Codifica la **asignación** (la tenías aceptada), no el cumplimiento. `Campanas.diasActivos` la arma desde `Config.campanas`, que guarda la historia completa.

Cada `campana_<reto>` se marca como «obvia» con lo que ese reto busca mover (`Campanas.MUEVE`; capítulo 6). La de sueño va con las horas y las horas de dormir y despertar; la de pantalla, con los minutos y desbloqueos. Así no aparece como hallazgo «en las semanas de campaña de sueño dormiste más». En R, desde la entrega 10d (P-31), la regla sale de la misma fuente: `vm_familias.csv` se genera desde `Campanas.MUEVE` (`Estadistica.familias`: cada campaña va en el lado 1 y lo que busca mover en el lado 2, y solo cuentan como obvias las parejas de lados distintos; así, dos variables que mueve la misma campaña, como las horas de sueño y la hora de dormir, no se esconden entre sí por esa razón), y `vm_obvia` lo lee. La lista escrita a mano en R (`vm_campana_mueve`) queda solo para exportaciones viejas sin ese archivo; desde la entrega 10d tiene los mismos retos que `Campanas.MUEVE`, con los nombres de `vm_panel.csv`.

Lo que sí queda para el análisis es lo interesante: por ejemplo, si en las semanas de campaña de sueño cambió **otra** cosa, como el ánimo. Y la campaña sirve como control: comparar «dormir más y ánimo» con y sin campaña. **[Diseño propio]**.

**Tres cambios en cómo se cuentan, también del esquema 13:**
- **Fecha de aceptación.** `Campana.aceptada` guarda el día lógico. Una campaña aceptada el miércoles ya no cuenta el lunes ni el martes, así que no se puede ganar en el mismo acto de aceptarla (H-9-34).
- **Abandonar no borra.** Abandonar la cierra ese día (`Campana.abandonada`): queda en la historia y en el panel, los días que la tuviste (H-9-36).
- **Hábitos desactivados.** Desactivar un hábito ya no anula las campañas ganadas con él ni sus logros, porque `Campanas.reto` sigue encontrando el hábito (H-9-35).

Las campañas de antes no tienen `aceptada` y cuentan la semana completa.

**Lo que hay que tener en cuenta al leerlas:**
- Una campaña es **autoelegida**, no sorteada. Pudiste elegirla justo en una semana en que ya ibas a mejorar.
- Varias semanas seguidas de la misma campaña hacen que la variable cambie poco. Puede clasificarse como «lenta» y quedar fuera del análisis sin tendencia (capítulo 6).
- Una campaña de avisos o de diario cambia cuántos datos faltan esa semana.

## 9.6 Gamificación: lo que dice la evidencia

- **Qué es.** Deterding et al. (2011) definen la gamificación como el uso de elementos de diseño de juegos en contextos que no son juegos. La app la usa con medallas, retos, mapas narrativos, temas y «Tu año».
- **¿Funciona?** La revisión de Hamari, Koivisto y Sarsa (2014) encontró que la mayoría de los estudios reportan efectos positivos, pero que dependen mucho del contexto y de quién la usa, y que muchos estudios tenían problemas de método. **[Validado]** que *puede* funcionar; no que funcione siempre.
- **El riesgo de las recompensas.** El metaanálisis de Deci, Koestner y Ryan (1999) encontró que las recompensas **tangibles y esperadas** reducen la motivación intrínseca (hacer algo porque te interesa), mientras que la retroalimentación verbal positiva tiende a aumentarla. Las medallas de la app no son tangibles; están a medio camino entre una recompensa y una retroalimentación. **[Aproximación]**: no sé de un estudio que mida esto con medallas digitales en autorregistro, así que no puedo decir hacia qué lado cae.
- **El autorregistro ya cambia la conducta.** En la literatura clínica, el autoseguimiento es reactivo: tiende a aumentar las conductas deseables y a reducir las indeseables (Korotitsch y Nelson-Gray, 1999). Premiar ciertos valores suma un segundo empujón en la misma dirección.
- **En los rastreadores personales.** Los modelos de *personal informatics* describen etapas (preparar, recolectar, integrar, reflexionar, actuar) y las barreras de cada una; el abandono es uno de los problemas centrales (Li et al., 2010). Quienes llevan un autoseguimiento intensivo suelen registrar demasiadas cosas y luego no saber qué hacer con ellas (Choe et al., 2014). La app ataca el abandono con constancia, campañas y reportes, y la reflexión con los análisis.

## 9.7 El dilema: motivar sin distorsionar

Toda la app tiene dos objetivos que tiran en direcciones opuestas:

1. **Que no falten datos.** Los datos que faltan no faltan al azar (capítulo 3, sección 3.15). Todo lo que haga más probable registrar los días malos mejora la validez.
2. **Que los datos midan la vida tal como es.** Si registrar o ciertos valores dan premios, los datos empiezan a medir, en parte, el deseo de ganar el premio. Es la **ley de Campbell**: cuanto más se usa un indicador para premiar, más se distorsiona lo que mide (Campbell, 1979).

**Cómo lo resuelve la app** (en el código actual):

- **Bien**: la mayoría de los logros premian **registrar** o **conductas objetivas medidas por sensores** (pasos y sueño del reloj, pantalla del teléfono), no lo que escribes. Un reloj no se deja convencer por una medalla.
- **Bien**: la constancia tolera días malos, así que no hay incentivo para inventar un día con tal de no romper la racha… salvo en las rachas de diario, donde sí lo hay.
- **Bien**: el registro de un toque para días difíciles (capítulo 3) hace que registrar un día malo cuente igual que uno bueno para los logros de diario.
- **Mejorable**: «Día perfecto» y «Remontada» premian valores de la valencia (H-8-6); se quedan por decisión del autor (D.117), con el riesgo descrito en la sección 9.4.3. Las predicciones de enero incluyen el ánimo medio. Las campañas, desde el esquema 13, sí quedan como variable (H-8-7, resuelto).
- **No se puede evitar**: que ver tus reportes cambie tu conducta. Es, en parte, el propósito de la app.

**Recomendación práctica para leer los datos** (no requiere cambiar código): al analizar una variable que también fue meta de una campaña o un logro, mirar si su relación con otras cambia antes y después de empezar a perseguir esa meta.

## 9.8 Resumen de decisiones

| Decisión | Clasificación | Respaldo |
|---|---|---|
| Reportes generados solos y guardados en carpetas públicas | Diseño propio | — (ver capítulo 10) |
| Barras con eje y número para el sueño; medalla de color con número para el ánimo | Aproximación | Cleveland y McGill (1984) |
| Línea de 7 h; alerta bajo 6.3 h | Diseño propio | 7 h: recomendación habitual [sin verificar] |
| Comparación con el periodo anterior: «parecido» si la diferencia es menor que su error típico (desde 10e; antes, sin incertidumbre) | Aproximación (no descuenta la autocorrelación) | — |
| Logros de constancia (k de n días) en lugar de rachas | Diseño propio (bien fundado) | — |
| Logros que premian valores de la valencia; se quedan por decisión del autor (10f, D.117) | Diseño propio (contrario a la ley de Campbell) | Campbell (1979) |
| Campañas semanales como retos | Diseño propio | Hamari et al. (2014) sobre gamificación |
| «Relación del año» = máximo |*r*| con *q* < 0.05 | Aproximación (sobrestima) | — |
| Reporte para terapia: qué entra y qué no; 8 secciones elegibles que no se guardan, con lo privado apagado | Diseño propio | — |
| Calorías y proteína contra el objetivo con un margen de ±10 %; menos de 300 kcal = sin registro | Diseño propio | — |
| Regularidad del horario de sueño como desviación estándar de las horas | Aproximación | — |

: Tabla 9.7. Resumen de decisiones

## 9.9 Resumen del capítulo

1. Los reportes y «Tu año» usan un mismo resumen, `Periodo.calcular`. Hasta la entrega 10c su «ánimo del día» mezclaba la valencia del diario con el promedio de los avisos (H-8-10); ahora usa la valencia del diario, la hora de dormir y los ingresos siguen las reglas del panel, y la mejor marca es la misma 1RM de la exportación (D.72, D.77).
2. Los reportes se generan solos (lunes y día 1) y se guardan en las carpetas públicas de imágenes y descargas; su contenido y privacidad se tratan en el capítulo 10.
3. La visualización combina codificaciones precisas (barras con eje y números) con otras gruesas (colores, iconos); las comparaciones con el periodo anterior no tenían noción de azar; desde 10e dicen «parecido» cuando la diferencia es menor que su error típico, un filtro que se queda corto porque no descuenta la autocorrelación (H-8-11).
4. Los logros premian sobre todo **registrar** y **conductas medidas por sensores**, y prefieren la **constancia** a las rachas: una buena decisión para no abandonar. Dos logros secretos premian valores de la valencia (H-8-6). Desde 10f se quedan como están por decisión del autor (D.117); el riesgo, un incentivo pequeño a sesgar la valencia que desaparece al ganarlos, queda descrito en la sección 9.4.3.
5. Las campañas son **intervenciones**. Hasta el esquema 12 no quedaban registradas como variable y podían confundir el análisis observacional (H-8-7). Desde el 13 son variables del panel (`campana_<reto>`, `campanas_n`), obvias con lo que cada reto mueve.
6. La gamificación puede ayudar a no abandonar, pero con efectos dependientes del contexto; las recompensas pueden cambiar la motivación y lo que se mide.
7. Desde D.119 hay un reporte para terapia de 7 o 14 días, en PDF para compartir (sección 9.3.6). Describe ánimo, cuestionarios, sueño, rutina, contacto, gimnasio, alimentación contra el objetivo y, si se elige al generarlo, sustancias, textos de «lo mejor» y «lo peor» y el seguimiento privado. No lleva nombres, lugares, el diario ni análisis estadísticos.


# Capítulo 10. Privacidad, seguridad e integridad de los datos

> **Para qué sirve este capítulo.** Explica **dónde viven tus datos, quién puede verlos y qué tan seguro es que no se pierdan**: el almacenamiento en el teléfono, el seguimiento privado y por dónde se filtra, el cifrado del respaldo, la verificación sin restaurar, los respaldos automáticos (carpeta y Google Drive), la restauración y los servicios externos con los que la app habla. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper (en 10e cambió el formato de los respaldos cifrados, D.104; en 10f, la lista de servicios externos llegó a los ajustes, D.116, y los experimentos ganaron una huella de lo sellado, D.113); lo que cambió en el módulo 10 se marca «desde 10a…10f» o «desde el esquema 13» y está en el Apéndice D.
>
> **Cómo leerlo.** Igual que los capítulos anteriores. Las cifras criptográficas y las reglas vienen del código, con el nombre de la función. Los hallazgos son H-8-n (módulo 8, junto con el capítulo 9); los H-9-n son del módulo 9. Las **reglas de privacidad declaradas** vienen de la sección 7 del documento de contexto del proyecto (`VESTIGIUM_MEI_CONTEXTO.md`, v81); la sección 10.3.4 las compara con el código.
>
> **Privacidad del propio capítulo.** El seguimiento privado (impulsos, recaídas, libido, medicamentos) se describe **solo en su mecánica**: no hay ejemplos ni datos personales, y tampoco se reproducen las palabras clave que el código usa para reconocer ciertos hábitos.

## 10.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Cifrar** | Convertir un archivo en algo ilegible sin una llave. |
| **AES-256** | El algoritmo de cifrado estándar más usado; «256» es el largo de la llave en bits. |
| **GCM** | Un modo de usar AES que, además de cifrar, añade una **etiqueta de autenticación**: si alguien cambia un solo bit del archivo, el descifrado falla en vez de dar datos alterados (Dworkin, 2007). |
| **IV (vector de inicialización)** | Un número al azar que se usa una sola vez por cifrado para que el mismo contenido no produzca el mismo resultado. |
| **PBKDF2** | Una función que convierte una contraseña en una llave **a propósito lenta**: repite un cálculo muchas veces (las **iteraciones**) para que probar millones de contraseñas cueste mucho (Turan et al., 2010). |
| **Sal** | Bytes al azar que se mezclan con la contraseña, para que la misma contraseña dé llaves distintas en cada archivo. |
| **Android Keystore** | Un almacén de llaves del sistema: la app puede usar una llave para cifrar, pero no puede sacarla del teléfono. |
| **Seguimiento privado** | Impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas. Desde el módulo 10a solo se ven en la pantalla Privado y viajan a R (sección 10.3). |
| **Integridad** | Que los datos no se pierdan, no se dañen y no cambien sin que lo sepas. |
| **Huella (SHA-256)** | Un resumen de 64 caracteres que se calcula de un texto: el mismo texto da siempre la misma huella, y cambiar una sola letra da una huella completamente distinta. No esconde nada ni usa contraseña; sirve para notar si algo cambió (sección 10.9). |

: Tabla 10.1. Mini-glosario

## 10.2 Dónde viven los datos

- **En el teléfono**, en una base SQLite de documentos JSON (`vestigium.db`, capítulo 2) en el almacenamiento privado de la app, al que otras apps no tienen acceso.
- **Sin copia automática de Android**: el manifiesto declara `android:allowBackup="false"`, así que la copia de seguridad del sistema **no** sube la base a la nube. Los únicos respaldos son los que hace la app (sección 10.5). **[Validado]** como decisión: evita que el diario termine en una copia que no controlas.
- **La base no tiene un cifrado propio**: depende del cifrado del almacenamiento que hace Android cuando el teléfono tiene bloqueo de pantalla. No verifiqué en esta sesión cómo funciona ese cifrado en cada versión de Android **[sin verificar]**.
- **Bloqueo de la app** (opcional, `Config.bloqueo`, apagado por omisión): al abrirla pide huella o el bloqueo del teléfono (`BiometricPrompt`, con `BIOMETRIC_WEAK` o credencial del dispositivo) y vuelve a pedirlo si estuvo **más de 60 segundos** en segundo plano. Si el teléfono no tiene ningún bloqueo configurado, la app **no puede** pedirlo y queda abierta. Es una protección contra miradas casuales, no contra alguien con acceso técnico al teléfono.

## 10.3 El seguimiento privado

### 10.3.1 Qué es y cómo funciona

_Esta sección describe el código después del módulo 10a (decisiones D-1 y D-2 de `revision_propuestas.md`). Hasta v84 la regla era otra; el cambio está en el Apéndice D._

- **Una sola regla, en un solo archivo** (`data/Privado.kt`). Es privado:
  - todo momento de tipo `impulso`, `recaida` o `libido` (`Momento.privado`, que usa `Privado.TIPOS`);
  - en el día, la intensidad del impulso (0 a 5), si hubo recaída, los detonantes y los **medicamentos**;
  - el **hábito ligado a las recaídas**. Con el interruptor encendido es el que elegiste en Privacidad o, si no elegiste, un hábito activo cuyo nombre contenga ciertas palabras clave. Con el interruptor apagado, solo el que elegiste. Elegir «ninguno» funciona; hasta v84 caía de nuevo en las palabras clave.
- **Dónde se ve: solo en la pantalla Privado** (Más → Privado; antes «Impulsos y recaídas»). Siempre, con el interruptor encendido o apagado (`ui/pantallas/Recaidas.kt`):
  - los medicamentos del día, con un botón «lo de siempre» que copia los del último día con medicamentos;
  - «Registrar un impulso ahora» (un momento de impulso con hora);
  - los impulsos por hora, la tendencia de los últimos 12 meses (impulsos y días con medicamentos), los días que tomaste cada medicamento y el registro de los últimos 30 momentos privados, que se pueden corregir.

  Con el interruptor encendido, además:
  - en el día, la intensidad del impulso (0 a 5), si hubo recaída, los detonantes y el hábito ligado;
  - los botones para registrar una recaída o la libido;
  - el resto del resumen privado (rachas, recaídas por hora y por día de la semana, lugares, detonantes…), los días con recaída en la tendencia y la sección de libido.

  Desde Día, el enlace «privado de este día» abre la pantalla en esa fecha. Las correlaciones de estas variables se hacen **solo en R**, como decidiste.
- **Dónde no se ve: en ninguna otra parte de la app**, ni con el interruptor encendido:
  - Día, Hoy, momentos, cinta, diarios, libreta, calendario, semana, año, totales, mapa y widgets;
  - reportes, «Tu año», logros (también los propios), metas, campañas y experimentos (el impulso dejó de ser una medida posible);
  - Constelación, Red, Tu gente, las sugerencias de @ y # al escribir, «Tus temas», y las variables `per_`, `pen_` y `tema_`;
  - todas las listas de análisis: `Estadistica.variables()` excluye `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `n_impulso`, `medicamentos_n`, las familias `med_` y `det_` y `hab_` del hábito ligado.
- **El interruptor `Config.seguimiento_privado`** (apagado por omisión) solo decide qué se **ofrece** en la pantalla Privado: con él apagado, medicamentos y momentos de impulso; con él encendido, también la intensidad del impulso del día, recaídas, libido y detonantes (la lista de arriba). Ya no decide qué se muestra fuera de Privado: fuera, nunca se muestra.
- **La exportación para R lleva todo, siempre.** El panel calcula las variables privadas aunque el interruptor esté apagado, y `vm_momentos.csv`, `vm_dias.csv`, `vm_panel_extendido.csv` y `vm_respaldo.json` las llevan. Desde la entrega 10d, `vm_variables.csv` lista las variables que la app cruza y marca las privadas (`privada` = 1, con `Privado.esVariable`). Por omisión, `vm_relaciones()` y `vm_factores()` de R **no** las cruzan, para dar lo mismo que la app; con `privadas = TRUE` sí, y esa es la vía para hacer en R las correlaciones del seguimiento privado. Las coordenadas de un momento privado ya no se usan para pedir el clima (`Entorno.lugarDe`).
- **Registrar una recaída** (`Recaidas.registrar`) guarda el momento con hora y lugar, marca el día con recaída y **desmarca el hábito ligado**. Se puede deshacer tal cual (`deshacer`) y cambiar la hora (`moverHora`), que busca la ubicación de esa hora (la más cercana en ±30 min) y descarta la luz y el ruido medidos al registrar. Desde el esquema 14 (H-NAV-6): cambiar solo el día, con la misma hora, también la mueve, porque se compara el minuto completo; y borrarla desde el diálogo de Privado quita la marca del día si era la única recaída de ese día (`alBorrar`; el día queda sin respuesta, porque no se sabe qué había antes).
- **El resumen privado** (`Recaidas.resumen`):
  - impulsos y recaídas por hora; recaídas por día de la semana;
  - lugares, detonantes, qué hacías antes y aparato;
  - minutos entre un impulso y la recaída que le siguió (dentro de 24 h);
  - cuántos impulsos pasaron **sin recaída en las 3 horas siguientes**;
  - días desde la última recaída y rachas.
- **Lo que no se puede filtrar:** si escribes algo privado en el texto libre del diario o de un momento común, la app no tiene forma de saberlo.

### 10.3.2 Las rachas: solo los días respondidos (H-8-8)

**Hasta la entrega 10c**, `Recaidas.resumen` empezaba a contar desde el primer día con respuesta y, a partir de ahí, **todo día que no era de recaída contaba como limpio**, aunque ese día no se hubiera contestado nada. Es el mismo problema de los datos faltantes del capítulo 3 (sección 3.15): si los días sin registro son justamente los difíciles, la racha queda inflada.

**Desde la entrega 10c (D.75)** las rachas salen de una función pura, `Recaidas.rachas(recaidas, limpios, hoy)`:

- un día **con recaída** (marcado en el día o con un momento de recaída) reinicia la racha a 0;
- un día cuenta como **limpio** solo si ese día se respondió «no» (`Dia.recaida == false`); suma 1;
- un día **sin respuesta** no suma ni reinicia: se cuenta aparte, como «sin respuesta» desde la última recaída. El día de hoy, mientras no se responda, no cuenta como sin respuesta.

La más larga (`rachaMaxima`) y la actual (`rachaActual`) salen de ahí, y la pantalla Privado dice «días respondidos sin» y, si los hay, cuántos días quedaron sin respuesta desde la última («no se sabe»). Ejemplo con una secuencia inventada de cuatro días (sí, no, sin respuesta, no): antes, racha actual 3; ahora, **2**, con 1 día sin respuesta. Lo comprueba `CalculosPrueba.rachasSoloConRespuesta`. **[Diseño propio]**: no contar ni romper con los días sin respuesta es un punto medio; romper la racha castigaría olvidar el registro, y contarlos la inflaría.

### 10.3.3 Dónde aparecía hasta v84 y qué cambió

La Figura 10.1 resume qué tipo de dato llega a cada salida. Hasta v84, el seguimiento privado se ocultaba de casi todo, pero se filtraba por seis vías. Todas se cerraron en el módulo 10a (la 5, en lo que toca a lo privado: H-8-5 sigue abierto en lo demás):

1. **Los momentos de impulso no eran «privados» para el código** (H-8-1). Contaban en el reporte, en «Tu año», en los logros de momentos, en el mapa, en un widget, en la semana, el calendario, el año, la Constelación, la Red y las presencias. Ahora `Momento.privado` incluye el impulso.
2. **«La relación del año» podía usar variables privadas y `medicamentos_n`** (H-8-2), y `n_impulso` entraba siempre, aun con el interruptor apagado, porque estaba en el grupo «Momentos» (H-9-1). Ahora `variables()` las excluye.
3. **La recaída apagaba un hábito con logros, campañas y rachas visibles** (H-8-3, H-9-38), y los logros propios aceptaban variables privadas. Ahora el hábito ligado solo se ve y se marca en Privado (`Privado.habitosVisibles`), y los logros propios sobre algo privado no se ofrecen ni se muestran.
4. **Los medicamentos no estaban detrás de ninguna regla** (H-8-16). Ahora son privados.
5. **La exportación salía en claro a la carpeta y a Drive** (H-8-5). Ahora, con datos privados y sin contraseña, el respaldo no sale del teléfono (sección 10.5).
6. **Las @menciones y #temas de los momentos privados** entraban en `per_`, `tema_`, «Tu gente», «¿Quién es @x?» y las sugerencias al escribir (H-6-1). Ahora se filtran.

**Sobre la pregunta del plan («por qué los logros nunca se ligan a sustancias, impulsos, recaídas ni libido»).** El documento de contexto lo afirma de los 12 logros secretos y, en general, de todo el seguimiento privado. El razonamiento es correcto: un logro por «días sin recaída» o por «tomar menos alcohol» convertiría el seguimiento privado en un marcador, con la presión de la ley de Campbell (capítulo 9, sección 9.7) justo sobre los datos más delicados; y uno «por registrar impulsos» premiaría registrarlos.

Hasta v84 la afirmación era **casi** cierta, salvo por tres vías indirectas, recogidas en los puntos 1 y 3 de arriba: los impulsos contaban en los logros de momentos, el hábito ligado tenía logros de constancia y campañas, y los logros propios aceptaban variables privadas. Desde el módulo 10a se cumple. Las sustancias **no** son privadas según la regla declarada («Sustancias NO son privadas: dato normal»), así que un logro propio sobre ellas no rompe ninguna regla; solo conviene recordar el riesgo de la ley de Campbell.

### 10.3.4 Las reglas declaradas frente al código

La sección 7 del documento de contexto llama a estas reglas «no negociables». Comparación con el código de v90 (donde cambió desde v81, se dice):

| Regla declarada | ¿Se cumple? | Dónde se comprobó |
|---|---|---|
| WhatsApp y comentarios de YouTube: nunca guardar texto | **Sí** | `ChatDia` guarda solo conteos por chat y día (`mios`, `otros`); de los comentarios y chats de YouTube solo se guarda cuándo y en qué video (`data/Modelo.kt`, `Importaciones.kt`) |
| Notificaciones: no leer su texto | **Sí** | `sensores/Oyente.kt` cuenta solo la app y la hora |
| Tokens de Spotify y claves de API solo en el teléfono | **Sí** | Preferencias privadas (`Spotify.prefs`, `Portadas.prefs` «claves_busqueda»), fuera de `Config` y por tanto fuera de los respaldos |
| `allowBackup=false` | **Sí** | Manifiesto |
| Fotos nunca se copian | **Sí** | El catálogo guarda hora, coordenadas y aparato, no la imagen |
| Cartas selladas: sin contenido en búsqueda, CSV ni «Tu año» | **En parte** | El CSV, «Tu año» y la búsqueda cumplen (la búsqueda en cartas solo mira las legibles, `Carta.legible`, en `ui/pantallas/Totales.kt`); `vm_respaldo.json`, que se exporta junto al CSV, lleva el texto (H-8-5) |
| Seguimiento privado (impulsos, recaídas, libido, detonantes, medicamentos) nunca en reportes, «Tu año», logros, mapas ni widgets; sí en la exportación a R | **Sí desde el módulo 10a**, con una regla más estricta: fuera de la pantalla Privado no aparece en ninguna salida. Hasta v84 fallaba en los impulsos y los medicamentos (H-8-1, H-8-2). **Una excepción elegida, desde D.119**: el reporte para terapia lleva impulsos, recaídas, detonantes y medicamentos (nunca la libido) solo si se marca su casilla al generarlo; la casilla no se guarda y empieza apagada cada vez (capítulo 9, sección 9.3.6) | `data/Privado.kt`, `Momento.privado`, `Estadistica.variables`, `ciencia/Terapia.kt` (`Opciones.privado`) |
| Sustancias no son privadas | **Sí** | Se tratan como dato normal |
| La imagen del hall no lleva texto del diario | **Sí** | `HallImagen.kt` («Sin textos del diario ni nada privado»); la cita del diario solo aparece en el detalle del trofeo dentro de la app |
| «Nada pasa por servidores ajenos salvo Drive, Open-Meteo y Spotify» (sección 1) | **No del todo** | También hay consultas a TMDB, RAWG, TVmaze, Google Books, Open Library, Crossref y a los mosaicos de OpenStreetMap (sección 10.8; H-8-17). Desde 10e, el texto de privacidad de la app los nombra todos (D.103), y desde 10f también Ajustes › Privacidad, con la misma lista (`Red.SERVICIOS`, D.116); el documento de contexto, que no está en el repositorio, sigue con la lista corta |

: Tabla 10.2. Las reglas declaradas frente al código

![Figura 10.1. Qué tipo de dato llega a cada salida, según el código después del módulo 10a. Borde rojo: hallazgo que sigue abierto (H-8-5). Script: `figuras/fig24_flujo_privacidad.py`.](figuras/fig24_flujo_privacidad.png){width=100%}

## 10.4 El respaldo cifrado

### 10.4.1 Qué contiene

`Respaldos.archivo` arma un **.zip** con `vm_respaldo.json` (diario, momentos, cartas, ajustes, Conocerte… todo), `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv` (solo hora y coordenadas de las fotos, **nunca la foto**) y las portadas de libros y películas. Si pusiste contraseña, ese .zip se cifra y se guarda como **.vmc**; el .zip en claro se borra.

### 10.4.2 Cómo se cifra exactamente (`Respaldos.cifrar`)

1. **Sal**: 16 bytes al azar (`SecureRandom`), nueva en cada respaldo.
2. **Llave**: PBKDF2 con HMAC-SHA256, **600 000 iteraciones** desde 10e (antes, 210 000), 256 bits (`ITERACIONES`, `llave`).
3. **IV**: 12 bytes al azar, nuevo en cada respaldo.
4. **Cifrado**: AES-256 en modo GCM con etiqueta de **128 bits**. El encabezado completo entra como **dato adicional autenticado**: no se cifra, pero si alguien lo cambia, el descifrado falla.
5. **Formato del archivo** (formato 2, desde 10e, D.104): encabezado de 27 bytes, que son la marca `VESTIGIUM-MEI-CIFRADO-2` (23 bytes) y el número de iteraciones (4 bytes, entero con el byte más significativo primero) · sal (16) · IV (12) · datos cifrados con su etiqueta. Como las iteraciones viajan en el archivo, se pueden subir más adelante sin inventar otro formato, y como van dentro del dato autenticado, cambiarlas hace fallar el descifrado.

**Por partes** (desde el 5 nov 2026, D.157). El .zip se escribe entrada por entrada, directo de la memoria o de la base al archivo: `vm_respaldo.json` sale con el codificador de flujo (`Exportar.escribirRespaldo`), y las ubicaciones y el catálogo de fotos, punto por punto (`Db.recorrerPuntos`). El cifrado y el descifrado van de archivo a archivo, en trozos de 64 KB (`Respaldos.cifrarArchivo`, `descifrarArchivo`), con el mismo formato 2. Antes se armaba todo como texto: el JSON completo, las ubicaciones completas, el .zip leído entero, su versión cifrada y la copia con el encabezado, todo a la vez en memoria. Con años de ubicaciones (por ejemplo, un historial viejo de Google Maps), la app se podía quedar sin memoria, y el respaldo automático dejaba de salir sin aviso. GCM tiene un límite que no cambia: Android guarda internamente lo cifrado hasta comprobar la etiqueta, así que al descifrar el contenido sí ocupa memoria una vez **[sin verificar]** en cada versión de Android. Nunca se escribe nada sin comprobar: el contenido descifrado solo sale después de la etiqueta. Lo comprueba `RespaldoPrueba.cifradoPorArchivos`: lo cifrado por archivos se abre en memoria y al revés, con otra contraseña no abre ni deja nada escrito, y un archivo truncado no abre.

**Los dos formatos** (`Respaldos.formato`, `descifrar`). La app escribe siempre el formato 2, pero sigue abriendo el **formato 1** de las versiones anteriores: marca `VESTIGIUM-MEI-CIFRADO-1` (23 bytes, el único dato autenticado) · sal · IV · datos, siempre con 210 000 iteraciones (`ITERACIONES_1`). Al leer un formato 2, si las iteraciones del encabezado no están entre 100 000 y 10 000 000 (`ITER_MIN`, `ITER_MAX`), el archivo se da por dañado sin intentar descifrarlo: una cifra enorme colgaría la app derivando la llave. Lo comprueban las pruebas JVM `RespaldoPrueba` (los dos formatos y encabezados dañados; D.108). La consecuencia práctica: **una versión de la app anterior a 10e no abre un .vmc nuevo**; para restaurar en otro teléfono, primero hay que instalar esta versión.

**Qué protege.** Sin la contraseña, el contenido es ilegible, y cualquier cambio o daño del archivo hace que el descifrado **falle** (no devuelve datos alterados). Como la sal y el IV son nuevos en cada respaldo, nunca se repite una combinación de llave e IV. Las elecciones (AES-GCM con IV de 96 bits y etiqueta de 128; PBKDF2 con sal de 128 bits) siguen las recomendaciones del NIST (Dworkin, 2007; Turan et al., 2010). **[Validado]**.

**Qué no protege.** El tamaño del archivo y sus fechas se ven. Y el punto débil de todo cifrado con contraseña es **adivinar la contraseña**: quien tenga el .vmc puede probar contraseñas sin límite en su propia computadora.

### 10.4.3 Las iteraciones (H-8-9, resuelto en 10e)

Las iteraciones son el «peaje» por cada intento de adivinar. La guía de OWASP para guardar contraseñas recomienda, para PBKDF2 con HMAC-SHA256, **600 000 iteraciones** (y 220 000 para HMAC-SHA512; cifras comprobadas en la página el 28 de octubre de 2026) (OWASP, s. f.). **Desde 10e la app usa exactamente eso**: 600 000 con SHA-256 (`PBKDF2WithHmacSHA256`, `ITERACIONES = 600_000` en `data/Respaldos.kt`, D.104). **[Validado]**: sigue la recomendación actual. Hasta 10e usaba **210 000**, unas 2.9 veces menos peaje que lo recomendado para SHA-256. (Algunas fuentes secundarias atribuyen a una versión anterior de la guía 210 000 para SHA-512, la cifra que tenía la app; no lo pude confirmar en la fuente **[sin verificar]**. Aunque fuera así, la app usaba SHA-256.) Los .vmc hechos antes de 10e siguen con 210 000: no se vuelven a cifrar solos, y quedan protegidos como antes mientras existan. Cuánto tarda en el teléfono derivar la llave con 600 000 iteraciones no se midió; el código lo hace fuera del hilo principal, y su comentario lo estima en «un segundo o más» **[sin verificar]**.

**Ejemplo (cuentas exactas, sin velocidades).** La app exige al menos **8 caracteres** (`ui/pantallas/Respaldo.kt`). Si fueran 8 letras minúsculas al azar, hay 26⁸ ≈ 2.1 × 10¹¹ contraseñas posibles; probarlas todas cuesta, con las 600 000 iteraciones de ahora, 2.1 × 10¹¹ × 600 000 ≈ 1.3 × 10¹⁷ cálculos de HMAC; con las 210 000 de antes de 10e eran ≈ 4.4 × 10¹⁶. Pero una contraseña humana (una palabra con un número) no es aleatoria y se adivina mucho antes. **La longitud y el azar de la contraseña importan mucho más que las iteraciones.** No doy tiempos, porque dependen del equipo del atacante y no los verifiqué.

### 10.4.4 La contraseña en el teléfono

`ponerContrasena` guarda la contraseña **cifrada con una llave AES-256-GCM del Android Keystore** (en las preferencias privadas `respaldo_secreto`). Así la app puede hacer respaldos cifrados sola, cada día, sin pedírtela. La contraseña **nunca viaja** en ningún respaldo. Si la olvidas, los .vmc no se pueden abrir: la propia pantalla lo advierte.

**Detalle** (H-8-15, resuelto en 10e): si cambias la contraseña, las copias que ya existen (en Drive, las semanales y mensuales, y las que guardaste o compartiste) siguen cifradas con la **anterior**; la app no las vuelve a cifrar. Para restaurar una copia vieja hará falta recordar esa contraseña. Hasta 10e no se avisaba. Desde 10e (D.104, `ui/pantallas/Respaldo.kt`): al **cambiarla**, el diálogo de la contraseña nueva lo advierte antes de escribirla y el mensaje final lo repite; al **quitarla**, la app pide confirmar («¿Quitar la contraseña?») y explica que las copias cifradas la siguen pidiendo. **[Diseño propio]**: avisar es lo mínimo; recifrar las copias viejas no se hizo.

## 10.5 Respaldos automáticos

### 10.5.1 A una carpeta (`Respaldos.aCarpeta`)

Una vez al día (la primera vez que corre la tarea diaria después de las 6:00, `Trabajo.diario`), si elegiste una carpeta:

- **Con contraseña**: se escribe solo `vestigium_respaldo.vmc` y se **borran** los archivos en claro que hubiera de antes (`vm_*.csv`, `vm_*.json`, `LEEME.txt`, `vestigium.R`, `vestigium_*.zip`). Desde el esquema 13 (P-09) se borran **después** de que el .vmc quedó escrito: si la escritura falla, la copia en claro sigue ahí (H-9-37).
- **Sin contraseña**: se escriben **todos** los CSV de la exportación, el diccionario, `vm_respaldo.json`, `vestigium.R` y `vestigium_respaldo.zip`, en claro.
- **Sin contraseña y con datos privados** (desde el módulo 10a): **no se escribe nada** (`Respaldos.bloqueado`). Hoy muestra «Tus respaldos están en pausa…» y llega una notificación por semana hasta que pongas contraseña.
- **Sin dejarlo a medias** (desde el 5 nov 2026, D.151). El respaldo (`.zip` o `.vmc`) se escribe primero a un archivo aparte (`….nuevo`) y solo cuando está completo toma el nombre del respaldo; el anterior se borra al final (`Respaldos.escribirSinRomper`). Antes se escribía encima del único respaldo, y si la tarea se cortaba o el teléfono se apagaba a la mitad, quedaba truncado. Si la carpeta no deja renombrar archivos, se escribe encima como antes. Los CSV sueltos se siguen escribiendo encima: son una copia de lo que ya lleva el respaldo.
- **Si falla, avisa** (desde el 5 nov 2026, D.157). Si el respaldo a la carpeta no se pudo escribir (sin espacio, la carpeta ya no deja escribir, sin memoria…) y van más de 3 días desde el último, llega una notificación, una vez por semana: «Tu respaldo automático no está saliendo». Antes, el error se tragaba y solo quedaba el aviso de Hoy («Sin respaldo desde…», a las 36 horas). Los 3 días son **[Diseño propio]**.

### 10.5.2 A Google Drive (`data/Drive.kt`)

- Usa el permiso **`drive.file`**: la app solo ve los archivos que ella misma creó, **nunca el resto de tu Drive**. **[Validado]** como el permiso mínimo necesario.
- Todo va a una carpeta «Vestigium Mei». Cada día **reemplaza** el respaldo del día (y, sin contraseña, los CSV); guarda una **copia semanal** fechada con el lunes (se conservan las **8** más recientes) y una **mensual** (las **12** más recientes).
- Con contraseña, solo sube el .vmc y borra de Drive los archivos en claro anteriores; sin contraseña, sube los CSV y el .zip en claro. Desde el esquema 13 (P-09), los archivos en claro se borran solo **después** de subir el .vmc (H-9-37).
- **No se reemplaza una copia buena por una mucho más pobre** (esquema 13, P-09). Antes de subir, `Respaldos.empobrecido` compara cuántos días y momentos trae el respaldo nuevo con los del último que reemplazó la copia (`Config.drive_conteo`). Si el nuevo trae **menos del 90 %** de cualquiera de los dos (y el anterior tenía 20 o más), no toca la copia del día, la semanal ni la mensual. En su lugar lo guarda aparte como `vestigium_revisar` y avisa con una notificación. «Restaurar desde Drive» nunca elige esa copia. Si el borrado fue a propósito, «conectar y subir ahora» (a mano) reemplaza la copia y fija el nuevo punto de comparación. El umbral de 90 % y el mínimo de 20 son **[Diseño propio]**: borrar a propósito muchos días también dispara el aviso, y eso es lo que se busca (H-8-14).
- Con datos privados y sin contraseña, no sube nada (desde el módulo 10a), igual que la carpeta. Tampoco se puede **compartir** el respaldo completo desde la pantalla Respaldo: el botón se desactiva y explica por qué.
- Si el permiso de Google caduca, avisa una vez por semana.

### 10.5.3 Cuándo falla

- **Sin contraseña, todo queda en claro fuera del teléfono** (H-8-5), incluido el diario completo. Desde el módulo 10a, si hay datos privados, el respaldo no sale sin contraseña. La **exportación para R** (resumida o extendida, que pides a mano y compartes) es la excepción: R necesita leer los CSV, así que va sin cifrar, y la pantalla advierte que lleva los datos privados.
- **El costo de esa regla:** si nunca pones contraseña, los respaldos automáticos quedan en pausa mientras haya datos privados. Por eso el aviso es visible en Hoy y llega una notificación semanal.
- **Las cartas selladas** van **sin texto** en `vm_cartas.csv` («no se pueden leer antes de su fecha»), pero `vm_respaldo.json`, que se exporta junto, lleva **el texto completo** (H-8-5). El sello se puede saltar abriendo el JSON.
- **Hasta el esquema 12, un respaldo malo podía reemplazar a uno bueno** (H-8-14). Si un día los datos del teléfono se dañaban o se perdían (por ejemplo, tras reinstalar), el respaldo de ese día **sobrescribía** el anterior sin comprobar si traía muchos menos datos, y la copia semanal también salía mala si era la primera de la semana. Desde el 13, Drive aparta un respaldo mucho más pobre (sección 10.5.2). Lo que sigue igual: «Restaurar desde Drive» **toma siempre la más reciente** (`Drive.ultimaCopia`, por fecha de modificación, sin contar `vestigium_revisar`). Para traer una semanal o mensual anterior hay que bajarla a mano y usar «Restaurar desde un archivo».

### 10.5.4 Los reportes, en carpetas públicas (H-8-4)

Los reportes semanales y mensuales se generan **solos** (capítulo 9, sección 9.3.1) y se guardan con `MediaStore` en `Pictures/VestigiumMei` y `Download/VestigiumMei`, fuera del almacenamiento privado de la app. Ahí los ve la galería, cualquier app con permiso para leer imágenes o descargas y, si está activada, la copia automática de fotos del teléfono, que puede subirlos a la nube sin que lo decidas. El reporte excluye el seguimiento privado (desde el módulo 10a también los impulsos), las calles y los nombres de lugares, pero incluye ciudades, peso, dinero y ánimo. Hasta v84 incluía también los **nombres de personas** (salvo en modo discreto, apagado por omisión) y la **frase del mejor momento** del diario. Desde el módulo 10a, los reportes **que se guardan solos** (lunes y día 1) van siempre en modo discreto, con iniciales, y sin esa frase (`Reportes.reporte(automatico = true)`); los que pides a mano siguen como antes. **[Diseño propio]**: la idea de reportes «para compartir» es coherente, pero guardarlos solos en una carpeta pública convierte cada lunes en una salida de datos que no elegiste.

El **reporte para terapia** (desde D.119) va por otro camino: no se genera solo ni pasa por `MediaStore`. Se escribe en la carpeta interna de la app (`cache/export/terapia/`, la única que comparte el `FileProvider` de `res/xml/archivos.xml`), se borra al generar el siguiente y sale del teléfono solo por el menú de compartir, a donde elijas. Lleva más datos sensibles que los otros reportes (consumo, alimentación, textos de «lo mejor» y «lo peor» y, si lo marcas, lo privado), pero cada salida es una decisión tuya en ese momento. Lo que se haga con el archivo después (un correo, un chat) ya no lo controla la app.

## 10.6 Verificar sin restaurar

`Respaldos.verificar` (desde un archivo) y `Drive.verificar` (la copia más reciente de Drive):

1. Si está cifrado, lo descifra (con la contraseña guardada o la que escribas). **Si el archivo está dañado, aquí falla**, gracias a la etiqueta de GCM.
2. Lee el .zip (`contenido`): decodifica `vm_respaldo.json` completo y cuenta las líneas de ubicaciones y del catálogo de fotos y las portadas. Desde el 5 nov 2026 (D.157), el archivo se copia y se descifra a archivos temporales, y el JSON se decodifica directo del zip, sin pasarlo a texto.
3. Muestra un **informe** (`informe`): fecha y esquema del respaldo, días (y rango de fechas), días con diario, sueños, momentos, tramos, gastos, cartas, obras, días de salud y de pantalla, canciones, comidas, lugares, visitas, ubicaciones, fotos con lugar, portadas y algunos ajustes; y debajo, cuántos días, momentos y gastos hay **en este teléfono**.

**Qué garantiza.** Que el archivo se abre, que no fue alterado (si está cifrado) y que el JSON se puede leer de punta a punta. Al leer las entradas del .zip, Java comprueba la suma de verificación (CRC-32) de cada una; esto no lo comprobé en la documentación durante esta sesión **[sin verificar]**. Es una práctica muy buena: **un respaldo que nunca se probó no es un respaldo**. **[Validado]** como práctica.

**Qué no garantiza** (H-8-12):

- Hasta el esquema 12, el mensaje decía «**El respaldo se lee completo y no le falta nada**», pero la app **no comparaba** nada: mostraba las cifras del respaldo y las del teléfono una debajo de otra, y tú tenías que notar si no cuadraban. «No le falta nada» era más de lo que se comprobó. Desde el 13 (P-08) dice solo «El respaldo se lee completo», y pone lado a lado respaldo / teléfono en días, momentos, gastos y experimentos.
- **La versión del esquema.** Como el lector de JSON ignora los campos que no conoce (`ignoreUnknownKeys = true`), restaurar en una versión vieja de la app un respaldo hecho con una versión más nueva **perdía en silencio** los campos nuevos. Desde el esquema 13, `Importar.esquemaFuturo` **rechaza** restaurar un respaldo con `schema_v` mayor que el de la app, antes de tocar nada (ni días, ni ubicaciones, ni portadas), y «Verificar» lo advierte. Al revés, los campos que falten se siguen llenando con valores por omisión (H-1-4).

## 10.7 Restaurar: cómo se combinan los datos

`Respaldos.restaurar` acepta el .vmc, el .zip completo, la exportación extendida o el JSON suelto. La pantalla promete: «Lo que ya tengas aquí no se duplica ni se borra». Con la app vacía, se trae todo, incluidos los ajustes. Con datos, `Importar.respaldoTexto` **combina**, pero con reglas distintas según la colección (H-8-13):

| Colección | Si ya existe en el teléfono… |
|---|---|
| Días | **gana el teléfono**: el día del respaldo se ignora (`importarDias(sobrescribir = false)`) |
| Momentos, reacciones, obras, ascensos, chats, YouTube | se agregan los que no existan (por identificador) |
| Salud, canciones | **gana el respaldo**: se guardan encima. Desde el esquema 14, con una excepción: el sueño que corregiste a mano en el teléfono (`sueno_manual`) no lo pisa un respaldo que no lo tenía corregido (`Fusion.salud`; H-NAV-5) |
| Pantalla, lugares, entorno, fotos, agenda, sesiones (desde el 5 nov 2026, D.150) | **gana el teléfono**: del respaldo solo entran las fechas o los identificadores que no estén. Hasta entonces ganaba el respaldo: un lugar que renombraste o al que le cambiaste el radio volvía a su versión vieja |
| Cartas (desde el 5 nov 2026, D.150) | gana el teléfono, pero una carta que en el respaldo ya estaba abierta queda abierta. Hasta entonces ganaba el respaldo: una carta que abriste después del respaldo volvía a quedar sellada |
| Experimentos (desde el esquema 13) | gana el teléfono, pero se conserva lo que solo trae el respaldo: la fecha de fin (un respaldo viejo no reactiva uno terminado) y las respuestas de «¿Lo hiciste hoy?» (`Fusion.experimento`). Hasta el 12, ganaba el respaldo |
| Gastos e ingresos | se agregan los que no existan (por identificador). Hasta el esquema 12, los repetidos se sobrescribían en la base pero no en memoria, y al reiniciar aparecía en silencio la versión del respaldo (H-9-28) |
| Avisos de ánimo (desde el esquema 13) | se juntan los dos: el mayor número de planeados y la unión de vencidos y mostrados |
| Personas y fechas propias (desde el esquema 13) | las personas del teléfono mandan, pero toman la fecha de nacimiento o de fallecimiento que solo tenga el respaldo; las fechas propias se suman |
| Laptop, comunicación, conexiones | se agregan solo las fechas nuevas |
| MacroFactor (nutrición, comidas, series, entrenos) | solo si en el teléfono **no hay nada** de MacroFactor |
| Ajustes | se combinan (`combinarConfig`). El historial de zonas horarias (`Config.zonas`, desde el esquema 15; capítulo 2) se junta: los cambios de los dos, sin repetir instantes (si coinciden, manda el del teléfono) |
| Ubicaciones | se agregan las que no existan (por milisegundo); las de fotos se rehacen del catálogo |

: Tabla 10.3. Restaurar: cómo se combinan los datos

**Por qué importa.** Si restauras un respaldo sobre un teléfono que tiene una versión **incompleta** de un día (por ejemplo, solo el sueño de esa mañana), el diario completo del respaldo para ese día **no se recupera**. En cambio, para la salud y las canciones, un respaldo viejo puede **pisar** datos más nuevos del teléfono. Ninguna de las dos cosas se avisa. Un lugar que quitaste en el teléfono vuelve si el respaldo lo trae: «combinar» no distingue lo que borraste de lo que nunca tuviste.

**Un elemento ilegible ya no tumba todo** (desde el 5 nov 2026, D.157). `Importar.leer` decodifica el respaldo directo del archivo. Si así no se puede (un momento con un dato que esta versión no entiende, por ejemplo), `Importar.tolerante` revisa cada colección que falla elemento por elemento: se quedan los que se leen y el mensaje final dice cuántos se saltaron y de qué colección. Si lo que no se lee son los ajustes, no se toman como si el teléfono fuera nuevo: los de aquí se quedan como están. Antes, un solo elemento hacía fallar la restauración entera con «No pude abrir el respaldo». Lo comprueba `RespaldoPrueba.unElementoIlegibleNoTumbaElRespaldo`. **Lo que no cambia:** los documentos que la app ya no puede leer en el teléfono (los que cuenta Estado de los datos) no entran en ningún respaldo. **[Diseño propio]**: saltar y avisar en vez de rechazar todo.

**En lote** (desde el 5 nov 2026, D.157). Momentos, respuestas de cuestionarios, pruebas de reacción, días de salud y canciones se guardan en una sola transacción y la lista en memoria se reordena una vez. Antes, cada momento se buscaba en toda la lista, se guardaba suelto y la lista se reordenaba entera: con miles de momentos, la restauración podía congelar la app. Las respuestas de cuestionarios que ya están en el teléfono (mismo identificador) ya no se vuelven a escribir.

**Durante la restauración** (desde el 5 nov 2026, D.150). La restauración corre en otro hilo. Los días se mezclan ya en el hilo principal, sobre lo que haya en ese momento: antes, un día que guardaras mientras se restauraba desaparecía de la memoria, y la siguiente edición partía de la versión vieja. Los avisos de ánimo del respaldo también pasan a la copia de trabajo de la tarea de fondo, que si no los borraba con el siguiente aviso del día.

### 10.7.1 La papelera (desde el esquema 14)

Antes de borrar un registro que hiciste tú (momentos, tramos, consumos, notas, metas, tomas, gastos, ingresos, lugares, cartas, sesiones de escalada y obras), `Repo` copia el documento tal como estaba a la colección `papelera` (`Borrado`: colección, id, el documento y cuándo se borró). Lo que lleva más de 7 días se vacía en la tarea diaria (`Repo.limpiarPapelera`). Lo que volvió con «deshacer» deja de contar como borrado. La papelera **no viaja** en los respaldos ni en la exportación. Lo que vive en los ajustes (fechas propias, logros propios, ciudades) no pasa por la papelera; para eso queda «deshacer». Desde la entrega N7 de la revisión de navegación hay una pantalla para recuperar (Más → Datos → «Papelera»): lista lo borrado con cuántos días le quedan y «recuperar». **Lo privado no aparece ahí** (ni siquiera se cuenta): un impulso, una recaída o la libido borrados se recuperan en la papelera de la pantalla Privado (`Papelera.esPrivado`, vigilado por `PrivadoPrueba`). Una sustancia que ocultaste sale como «Un registro oculto». **[Diseño propio]**.

## 10.8 Servicios externos

La app no incluye herramientas de analítica, publicidad ni reporte de errores (no hay dependencias de ese tipo en `app/build.gradle.kts`). Habla con estos servicios:

| Servicio | Para qué | Qué se envía |
|---|---|---|
| **Google Drive** (`googleapis.com`) | Respaldos (sección 10.5), solo si lo activas | Los archivos de respaldo (cifrados o no) |
| **Open-Meteo** (pronóstico, archivo histórico y calidad del aire) | Clima y aire del día (capítulo 4) | La **mediana de las ubicaciones del día, redondeada a 2 decimales** (`Entorno.r2`: ≈ 1.1 km en latitud) y las fechas. Desde 10f (D.111), los días del último año que vinieron del pronóstico se vuelven a pedir al archivo cuando pasan 80 días (`DIAS_ARCHIVO`): más consultas, pero con el mismo tipo de dato |
| **Spotify** | Historial de canciones (capítulo 4) | Autorización con redirección a `127.0.0.1` (solo dentro del teléfono) y luego consultas con tu permiso |
| **Catálogos de obras**: TMDB, RAWG, TVmaze, Google Books, Open Library, Crossref | Portadas y fichas de libros, películas, series, juegos y artículos | El **título o DOI** que buscas |
| **Mapas de OpenStreetMap** (osmdroid, mosaicos `MAPNIK`) | Fondo del mapa | Qué zona del mapa estás viendo (al pedir los mosaicos) |
| **Health Connect, uso del teléfono, ubicación** | Datos pasivos (capítulo 4) | Nada: se leen **dentro** del teléfono |

: Tabla 10.4. Servicios externos

El documento de contexto dice que «nada pasa por servidores ajenos salvo Drive del usuario, Open-Meteo (clima) y Spotify API»; la tabla muestra que hay más servicios, aunque los demás solo reciben títulos de obras o la zona del mapa que miras (H-8-17). (Google Books, que `Portadas` consulta para buscar libros, faltaba en esta tabla hasta esta actualización.) **Desde 10e** (D.103), el texto de privacidad de la app (`ui/PrivacidadActivity.kt`) los nombra todos y dice qué recibe cada uno: Open-Meteo, la zona aproximada y las fechas; OpenStreetMap, qué zona miras; los catálogos, solo el título o el DOI que escribes; Spotify, la petición de lo que escuchaste; Drive, tus respaldos. El documento de contexto no está en el repositorio y hay que corregirlo a mano. El texto dice también que Google Drive recibe los respaldos completos, diario incluido (cifrados si pones contraseña, en claro si no; sección 10.5), y que los demás servicios no reciben tu diario, tu salud ni tu ruta.

**Desde 10f, la lista está dentro de la app** (D.116; H-8-17, resuelto). Ajustes › Privacidad termina con una sección plegada, «Qué sale del teléfono», cerrada por omisión para que no estorbe (`ui/pantallas/Sistema.kt`). Abierta, muestra cada servicio y qué recibe, y una nota: Health Connect, las fotos, la ubicación y el diario se quedan en el teléfono, y solo salen al exportar, al elegir una carpeta de respaldo o al activar Google Drive. La lista sale de un solo lugar, `Red.SERVICIOS` (`data/Red.kt`), que usa también el texto de privacidad (`ui/PrivacidadActivity.kt`, que ahora termina con «Fuera de Google Drive, ninguno recibe tu diario, tu salud ni tu ruta»). Tiene **5 entradas**: Open-Meteo (la zona aproximada, a ~1 km, y las fechas), OpenStreetMap (los mosaicos de la zona que miras), los seis catálogos de obras juntos (solo el título o el DOI que escribes), Spotify si lo conectas (el permiso para leer lo que escuchaste) y Google Drive si lo activas (los respaldos completos, cifrados si pones contraseña). Por eso el resumen de la sección dice «5 servicios», aunque los catálogos son seis. Coincide con la tabla de arriba. El autor revisó la lista y está de acuerdo con lo que se envía (estado de H-8-17). **[Diseño propio]**: tener una sola lista evita que la pantalla y el texto digan cosas distintas, pero nada en el código obliga a anotar ahí un servicio nuevo; lo dice solo el comentario de `Red.SERVICIOS` («si se agrega un servicio, va aquí»), y ninguna prueba lo vigila.

**[Validado]** como enfoque general: el procesamiento es local y solo salen las consultas necesarias, con la ubicación redondeada para el clima. Lo que no es mínimo es el respaldo sin contraseña. No verifiqué las políticas de privacidad de cada servicio en esta sesión **[sin verificar]**.

## 10.9 Integridad: lo que ya se vio en otros capítulos

- **Documentos que no se pueden leer.** Hasta el esquema 12 se descartaban en silencio (H-1-3). Desde el 13 (P-07) se cuentan por colección y se muestran en «Estado de los datos» y en Hoy, y una configuración ilegible se copia aparte antes de quedar en blanco (H-9-2; capítulo 2). Los **valores inválidos se convierten en valores por omisión** (H-1-4).
- **Versión del esquema**: cada día guarda `schema_v` y cada respaldo el suyo (capítulo 2); desde 10f el vigente es el **15** (`SCHEMA_V` en `data/Modelo.kt`). Como un respaldo de un esquema más nuevo no se restaura (sección 10.6), una app de la v91 o anterior (esquema 14) no restaura un respaldo de la v92. Al revés, un respaldo de antes del 15 se restaura con los campos nuevos en su valor por omisión: sin fuente del clima (al exportar se deduce de la fecha en que se pidió, `Entorno.fuente`, D.111), con 0 minutos de wifi sin dato (el campo no existía), con los experimentos sin huella (`intacto` vacío) y sin fuerza de referencia. Esta última se calcula de las series del respaldo al restaurarlo (`Repo.completarFuerzaReferencia`, D.114), y además una sola vez para lo que ya estaba en el teléfono (la marca `fuerza_ref` en `avisos/Trabajo.kt`). No lo probé en un teléfono. Hasta el 5 nov 2026 el cálculo al restaurar no hacía nada: recorría la lista de entrenos en memoria, que todavía estaba vacía, y como la marca `fuerza_ref` ya estaba puesta, nunca se repetía. Ahora recibe los entrenos del respaldo (D.150).

**Esquemas 16 y 17.** Después de 10f, el esquema 16 agregó el Ideario y el 17, los campos de las pantallas de captura (Apéndice D, D.127 a D.144). Con la misma regla, una app con esquema 16 o anterior no restaura un respaldo del 17. Al revés, un respaldo de antes del 17 se restaura con los campos nuevos en su valor por omisión:
- Los sueños quedan sin rareza ni pesadilla. Se traducen de su «tipo» al leerlos (`Catalogo.rarezaDe`, `pesadillaDe`).
- Los días quedan sin la marca de hábitos de tres estados, así que siguen con la regla de antes: sin marca, 0.
- Las ideas quedan sin el campo de temas y cuentan solo sus #temas del texto.
- Los momentos quedan sin intensidades ni lista de pensamientos.
- `Config.categorias_v17` queda apagado, así que Vivienda y Servicios se agregan una vez a las categorías.

No lo probé en un teléfono.
- **Desde qué versión vale cada columna** (desde 10f, D.115; H-1-8, resuelto). Hasta la v91, el diccionario no versionaba variable por variable: si una definición cambiaba, una exportación nueva y una vieja traían columnas con el mismo nombre y distinto significado, sin aviso. Ahora `vm_diccionario.csv` trae dos columnas más: `desde_version` (la versión de la app desde la que rige la definición actual de esa columna) y `cambio` (qué cambió entonces, con su fila del Apéndice D). Salen de una tabla fija en el código, `Diccionario.DESDE`, que acepta claves «archivo:columna», «columna» o familias con «*» (por ejemplo `per_*`); si coinciden varias, gana la más larga (`Diccionario.desde`). Vacío quiere decir «sin cambios desde la v84»: antes de la v85 no hay registro. *Ejemplo:* `sin_wifi_min` dice 92, «ya no incluye los minutos sin dato (D.112)»; si comparas una exportación de la v91 con una de la v92, esa columna no es la misma medida. El LEEME pide mirar las dos columnas al comparar exportaciones de fechas distintas. **[Diseño propio]**: la tabla es tan completa como quien la mantiene; una prueba JVM (`ZonasPrueba`) comprueba algunas entradas, no que estén todas.
- **El sello de los experimentos** (desde 10f, D.113; H-7-10, resuelto; el método está en el capítulo 8). Al crear un experimento, `Experimentos.sellar` le pone una **huella** (`huella`): el SHA-256 de lo sellado, unido en un orden fijo (`textoSellado`): pregunta, acción, medida, hipótesis, umbral, sentido, diseño, inicio, semanas, plan, lavado y hora del sello. El nombre, el «¿Lo hiciste hoy?» y la fecha de fin no entran, porque cambian durante el experimento. Cada vez que se muestra, la app vuelve a calcularla (`Experimentos.intacto`): si coincide, la tarjeta enseña sus primeros 8 caracteres («coincide: nada de lo sellado cambió»); si no, avisa que la pregunta, la hipótesis, el criterio o el plan ya no son los del principio. `vm_experimentos.csv` trae `huella` e `intacto` (1, 0, o vacío en los experimentos de antes del esquema 15, que no tienen huella). *Ejemplo:* si después de empezar cambias la hipótesis «dormiré mejor» por «dormiré mejor o igual», la huella cambia por completo y la tarjeta lo dice. **[Validado]** que SHA-256 detecta cambios: es una función hash criptográfica estándar, y encontrar otro texto con la misma huella no es factible en la práctica. **[Diseño propio]** cómo se usa, con un límite que conviene decir: la huella se guarda junto a los datos y **sin ninguna llave**, así que nota los cambios hechos sin querer o sin recalcularla (una edición, un respaldo que reescribe el experimento), pero no los hechos a propósito: quien edite el JSON de un respaldo puede calcular la huella nueva con la misma receta, que está en el código. Protege contra acomodar el criterio sin darse cuenta, no contra hacer trampa.
- **Estado de los datos** (`EstadoDatos.kt`, capítulo 4): muestra por fuente si llegó hoy o ayer (verde), si está atrasada (ámbar), si falta un permiso (rojo) o si está apagada (gris). Es el monitor de integridad de los datos pasivos.
- **Días importados sin sellos** (H-1-9).

## 10.10 Resumen de decisiones

| Decisión | Clasificación | Respaldo |
|---|---|---|
| Datos solo en el teléfono, sin copia automática de Android | Validado (buena práctica) | — |
| Bloqueo opcional con huella o credencial, 60 s | Diseño propio | — |
| AES-256-GCM, IV de 12 bytes y sal de 16 nuevos en cada respaldo | Validado | Dworkin (2007); Turan et al. (2010) |
| PBKDF2-HMAC-SHA256 con 600 000 iteraciones desde 10e, guardadas en el encabezado (formato 2); antes, 210 000 | Validado (la recomendación actual); hasta 10e, Aproximación | OWASP (s. f.): 600 000 |
| Avisar al cambiar o quitar la contraseña que las copias viejas siguen con la anterior (10e) | Diseño propio | — |
| Contraseña guardada con el Android Keystore, nunca en el respaldo | Diseño propio (razonable) | — |
| Permiso `drive.file` | Validado (mínimo necesario) | — |
| 8 copias semanales y 12 mensuales | Diseño propio | — |
| Verificar sin restaurar | Validado como práctica | — |
| Combinar al restaurar con reglas distintas por colección | Diseño propio (inconsistente) | — |
| Ubicación redondeada a 0.01° para el clima | Diseño propio (razonable) | — |
| Seguimiento privado: visible solo en la pantalla Privado, siempre en la exportación para R | Diseño propio | — |
| Sin contraseña no sale ningún respaldo si hay datos privados (salvo la exportación para R, a mano) | Diseño propio | — |
| Contar lo ilegible y apartar la configuración dañada (esquema 13) | Diseño propio | — |
| No restaurar un respaldo de un esquema más nuevo (esquema 13) | Validado como práctica | — |
| No reemplazar en Drive una copia por otra con menos del 90 % de días o momentos (esquema 13) | Diseño propio | — |
| Una sola lista de servicios externos (`Red.SERVICIOS`) para el texto de privacidad y Ajustes › Privacidad (10f) | Diseño propio | — |
| Huella SHA-256 de lo sellado en cada experimento, sin llave (10f) | Validado (SHA-256 detecta cambios); su uso, Diseño propio (no impide recalcularla a propósito) | — |
| `desde_version` y `cambio` por columna en el diccionario, desde una tabla fija en el código (10f) | Diseño propio | — |

: Tabla 10.5. Resumen de decisiones

## 10.11 Resumen del capítulo

1. Los datos viven solo en el teléfono, sin copia automática de Android; la app puede pedir huella. Salen solo por los respaldos que tú activas y por consultas puntuales (clima con la ubicación redondeada, títulos de obras, mapas).
2. El **respaldo cifrado** usa AES-256-GCM con sal e IV nuevos en cada archivo: bien diseñado. Desde 10e, PBKDF2 usa las 600 000 iteraciones que recomienda hoy OWASP (antes, 210 000; H-8-9), con un formato nuevo que guarda las iteraciones en el encabezado y sigue abriendo los archivos viejos; al cambiar o quitar la contraseña, la app avisa que las copias ya hechas siguen con la anterior (H-8-15). Lo que más importa sigue siendo una contraseña larga.
3. **Sin contraseña, todo sale en claro** a la carpeta y a Drive, incluido el texto de las cartas selladas (H-8-5). Desde el módulo 10a, si hay datos privados, el respaldo no sale sin contraseña; solo la exportación para R, que pides a mano, va en claro y lo advierte.
4. **El seguimiento privado** (impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado) se ve, desde el módulo 10a, **solo en la pantalla Privado** y va completo a R. Hasta v84 se colaba por los impulsos, los medicamentos y el hábito ligado (H-8-1, H-8-2, H-8-3, H-9-39). Desde la entrega 10c, las rachas cuentan solo los días respondidos «no» (H-8-8, D.75).
5. **Verificar** un respaldo es una gran idea. Hasta el esquema 12, el mensaje prometía más de lo que comprobaba y no revisaba la versión del esquema (H-8-12). Desde el 13 dice solo lo que comprueba, compara respaldo y teléfono, y un respaldo de una versión más nueva no se restaura.
6. **Restaurar** combina con reglas distintas según la colección (H-8-13). Desde el esquema 13, los gastos e ingresos no se pisan y un experimento terminado no se reactiva. En Drive, un respaldo mucho más pobre ya no reemplaza al bueno: se guarda aparte y se avisa (H-8-14).
7. Los reportes se guardan solos en carpetas públicas (H-8-4; capítulo 9). Hasta v84 llevaban nombres de personas y la frase del mejor momento; desde el módulo 10a, los que se guardan solos van en modo discreto y sin esa frase.
8. Las reglas de privacidad declaradas se cumplen en casi todo (texto de WhatsApp, notificaciones, claves, fotos, seguimiento privado). Hasta v84 fallaban en los impulsos y los medicamentos (H-8-1, H-8-2, resueltos en 10a); siguen fallando en el texto de las cartas selladas dentro del JSON (H-8-5). La lista de servicios externos estaba incompleta (H-8-17); desde 10e, el texto de privacidad de la app la da completa, y desde 10f también Ajustes › Privacidad, con la misma lista (`Red.SERVICIOS`); falta corregir el documento de contexto.
9. El módulo 8 identificó, entre los capítulos 9 y 10, **17 hallazgos** (H-8-1 a H-8-17), sin cambiar código. Después, el módulo 10 resolvió H-8-1, H-8-2, H-8-3, H-8-7, H-8-8, H-8-10, H-8-12 y H-8-16 (10a a 10c) y H-8-9, H-8-11, H-8-15 y H-8-17 (10e), y en parte H-8-4, H-8-5, H-8-13 y H-8-14. La entrega 10f cerró los dos que quedaban: H-8-6 (los logros que premian valores de la valencia se quedan, por decisión del autor, con el riesgo documentado en el capítulo 9; D.117) y la parte que faltaba de H-8-17 (la lista en los ajustes, D.116). En total: 13 resueltos, 4 en parte y ninguno pendiente.
10. **Integridad, desde 10f** (esquema 15): cada experimento lleva una huella SHA-256 de lo sellado y la tarjeta avisa si cambió (D.113), aunque sin llave no impide una trampa hecha a propósito; y `vm_diccionario.csv` dice desde qué versión rige cada columna (`desde_version`, `cambio`; D.115, H-1-8). Un respaldo de la v92 no se restaura en una app más vieja.


# Capítulo 11. Discusión, limitaciones y trabajo futuro

> **Para qué sirve este capítulo.** Junta lo que dicen los capítulos 2 a 10 para responder tres preguntas: **qué se puede concluir y qué no** de los datos de Vestigium Mei; **qué amenazas a la validez quedan**, vistas en conjunto y no capítulo por capítulo; y **qué falta**, tanto en la app y en R como en este propio documento. No describe funciones nuevas: cada afirmación remite al capítulo donde está el detalle, con el nombre de la función de origen.
>
> **Qué versión describe.** La v92 (`SCHEMA_V = 15`): la v90 (commit `8818d5a`), después de las entregas 10a a 10d del módulo 10 y de la revisión de navegación; más la entrega 10e, que arregló en el código todo lo que se pudo de los hallazgos abiertos (Apéndice D, D.88 a D.108), y la entrega 10f, que llevó al código las decisiones del autor sobre los 8 que seguían pendientes (D.109 a D.118). Las cuentas de hallazgos son las del campo «Estado» de `hallazgos.md` al 28 de octubre de 2026, después de 10f.
>
> **Cómo leerlo.** Igual que los demás capítulos. Las etiquetas **[Validado]**, **[Aproximación]** y **[Diseño propio]** se explican en el capítulo 1 (sección 1.8). Los ejemplos son inventados.

## 11.1 Qué está bien resuelto

Antes de las limitaciones, conviene decir qué partes del sistema son sólidas, porque son las que justifican confiar en el resto con la cautela debida.

1. **«Cero» y «no sé» se distinguen con una sola regla.** Desde la entrega 10c, una variable vale 0 solo desde el primer registro de su fuente y en los días en que se pudo responder (`Estadistica.ceroDesde`; capítulo 2, sección 2.6). Es la aplicación correcta de la teoría de datos faltantes (Rubin, 1976): un cero falso baja las medias y crea correlaciones por el solo calendario de adopción. **[Validado]** el principio.
2. **El ánimo se mide de varias formas que no se mezclan.** Avisos al azar dentro de tramos (muestreo estratificado), registros propios, el diario de la noche, el cierre del día y cuestionarios guardan variables distintas (capítulo 3). La app no las trata como intercambiables, y desde la entrega 10c los reportes tampoco (capítulo 9, sección 9.2). **[Validado]** el diseño.
3. **La lista de relaciones tiene tres protecciones a la vez**: quita la tendencia (±10 días), descuenta que los días se parecen entre sí (n efectivo) y corrige por las miles de comparaciones (Benjamini-Hochberg, q < 0.10), además de esconder las parejas obvias (capítulo 6). Desde la entrega 10c, «Todo lo que se mueve con algo», «Cruzar dos variables» y las diferencias de medias usan la misma maquinaria: t de Student y la corrección por autocorrelación. **[Diseño propio]** la combinación; cada pieza, **[Validado]**.
4. **R repite a la app.** Desde la entrega 10d, `vestigium.R` analiza el mismo panel, con las mismas variables y las mismas parejas obvias, y una prueba JVM fija que `vm_relaciones` y `vm_factores` dan los mismos números que la app con datos inventados (capítulo 6, sección 6.22; Apéndice C). Con datos reales, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Eso convierte a R en un **verificador independiente** de lo que muestra el teléfono.
5. **Los experimentos son experimentos de verdad**: el plan se sortea por pares de semanas, empieza en lunes (cada condición tiene los mismos días de la semana), la hipótesis queda sellada antes de empezar (desde la entrega 10f, también el criterio de decisión, con una huella SHA-256 que avisa si lo sellado cambia; D.113), y el intervalo usa t de Welch con el error estándar corregido por autocorrelación; desde la entrega 10e, el tamaño del efecto es la g de Hedges y R agrega una prueba de aleatorización exacta (`vm_experimento`). En simulación, sin efecto real y con días que se parecen a sus vecinos (autocorrelación de 0.3 a 0.5), da una falsa señal el 3 o 4 % de las veces, frente al 14 a 24 % del método de antes de la entrega 10c (capítulo 8, sección 8.6.3). **[Validado]** la lógica; el método es conservador.
6. **Las costuras se marcan.** Cuando una regla de medición cambió, cada día guarda con qué regla se midió (`ventana_pantalla`, `ventana_pasos`, `version_noche`) y el Apéndice D dice qué cambió y en qué versión.
7. **Lo privado tiene una sola regla** (`data/Privado.kt`) y una prueba que falla si algo se cuela (`PrivadoPrueba`); el respaldo se cifra con AES-256-GCM (con las 600 000 iteraciones de PBKDF2 que recomienda OWASP, desde 10e), se verifica sin restaurar y no se restaura si es de un esquema más nuevo (capítulo 10).

**El saldo de la revisión.** Los módulos 1 a 9 y la revisión de navegación anotaron **158 hallazgos** en `hallazgos.md` (149 de los módulos y 9 H-NAV). Según su campo «Estado», revisado en el módulo 14 y puesto al día con las entregas 10e y 10f, **126 están resueltos** (incluidos los que se resolvieron documentándolos, sin cambiar código, y uno por decisión del autor), **31 resueltos en parte** y **1 pendiente**; antes de 10f eran 119, 31 y 8, y antes de 10e, 97, 24 y 37. La entrega 10f resolvió siete de los ocho pendientes: seis en el código (H-1-7, la hora local de entonces; H-1-8, desde qué versión vale cada columna; H-3-4, la fuente del clima; H-3-7, el wifi sin dato; H-7-10, el criterio sellado de los experimentos; H-7-14, la fuerza de referencia de la creatina) y uno por decisión del autor (H-8-6: los logros que premian la valencia se quedan, con el riesgo documentado en el capítulo 9). El único pendiente es **H-1-4**, de gravedad media: los valores inválidos que se convierten en silencio en el valor por omisión al cargar; contarlos queda para una revisión con el teléfono. Ninguno de gravedad alta queda pendiente del todo. Los dos de gravedad alta marcados «en parte» son H-1-1 (una sola regla para «cero» y «no sé», pero una función que se deja de usar sigue contando 0) y H-3-1 (las lecturas del reloj, la agenda, el clima y las ciudades siguen por día de calendario, ahora declarado en cada variable). La sección 11.7.5 lista los de gravedad media que siguen abiertos. A ellos se suman los dos de este capítulo (H-11-1 y H-11-2, resueltos documentándolos aquí, sin cambiar código) y seis de gravedad baja o muy baja que encontró la lectura final contra el código (H-14-1 a H-14-6; por ejemplo, que «el día más raro» de un año pasado salía incompleto, o que R descontaba menos navegadores que la app), que la entrega 10e corrigió todos. En total, **166 hallazgos: 134 resueltos, 31 en parte y 1 pendiente** (H-1-4).

## 11.2 Qué se puede concluir y qué no

### 11.2.1 Por tipo de pregunta

El capítulo 1 (sección 1.4.5) distinguió tres tipos de pregunta. Esta tabla dice, para cada herramienta, qué afirmación permite y cuál no.

| Herramienta | Puede decir | No puede decir | Por qué |
|---|---|---|---|
| Tus números, Totales, reportes | Cuánto, cuándo y cuántos días con dato | Que una diferencia con la semana anterior sea real | Desde 10e, los reportes dicen «parecido» si la diferencia es menor que su error típico, pero ese filtro no descuenta que los días se parecen y se queda corto (H-8-11; capítulo 9, sección 9.3.4) |
| Dinámica emocional | Qué tan variable y persistente es tu valencia del diario | Lo mismo que esos índices en la literatura | Allí se calculan con mediciones momentáneas varias veces al día; aquí, con un valor recordado por día (capítulo 6, sección 6.16.3) |
| Las relaciones más marcadas | Qué se mueve junto **de un día a sus vecinos**, descontando tendencias, días parecidos y muchas comparaciones | Qué causa qué; qué se mueve junto **a lo largo de semanas o meses** | Quitar la tendencia borra los efectos lentos (capítulo 6, sección 6.5.6); no hay control de confusores |
| Lo mismo, con desfase de un día | Que lo de hoy **ayuda a predecir** lo de mañana | Que lo cause | Es causalidad en el sentido de Granger (1969): precedencia y predicción. Un tercer factor persistente produce lo mismo |
| Modelo múltiple (con el valor de ayer) | La asociación de un predictor manteniendo fijos los demás que elegiste | El efecto de lo que no pusiste en el modelo | Los confusores no medidos siguen ahí; el error estándar supone residuos independientes (capítulo 6, sección 6.14.5) |
| Antes y después | Cómo cambiaron tus días alrededor de un cambio de vida | Que el cambio de vida lo causara | Es una serie de tiempo interrumpida: muchas cosas cambian a la vez, y hasta 56 comparaciones sin corregir (capítulo 8, sección 8.11) |
| Experimentos personales | Que **hacer la acción, sabiendo que la haces**, cambió tu medida en ese periodo | Que el efecto sea fisiológico; que dure; que valga en otra etapa de tu vida | Sin cegamiento, con potencia modesta y con un análisis según lo asignado (capítulo 8, secciones 8.10 y 8.12) |

: Tabla 11.1. Por tipo de pregunta

### 11.2.2 Asociación no es causa, aquí tampoco

Las protecciones de la lista (sección 11.1) atacan el **azar** y las **tendencias**. No atacan los otros dos motivos por los que dos cosas se mueven juntas sin que una cause la otra:

- **Un tercer factor** (un *confusor*) que mueve a las dos. El más común en datos diarios es **el día de la semana**: duermes más y te sientes mejor el sábado, y la lista sin tendencia lo conserva entero, porque una ventana de 21 días contiene tres semanas exactas (H-5-6; capítulo 6, sección 6.5.6). Desde la entrega 10e la pantalla lo dice, y en R `vm_relaciones` y `vm_factores` aceptan `sin_semana = TRUE`, que resta a cada resto sin tendencia el promedio de su día de la semana (D.103, D.106); la app no tiene esa opción. Otros confusores probables: estar enfermo, viajar, una temporada de trabajo, la estación.
- **La dirección.** ¿Dormir bien mejora el ánimo, o el buen ánimo deja dormir? El desfase de un día ayuda, pero no decide: si el ánimo es persistente, el ánimo de ayer puede causar el sueño de anoche y el ánimo de hoy a la vez.

Por eso la regla práctica del paper es: **una relación firme en la lista es una hipótesis; un experimento personal es la forma de probarla** (capítulo 8).

### 11.2.3 Qué tan lejos llega una conclusión

Aun la mejor conclusión de la app vale **para el autor, en el periodo analizado**. Dos límites:

- **A otras personas, nada.** Es la consecuencia directa del argumento idiográfico (Molenaar, 2004; capítulo 1, sección 1.4): la app no puede decir nada sobre cómo funcionan otros.
- **A tu futuro, solo si nada importante cambió.** Una conclusión sobre tus días de este año vale para los del próximo si el proceso es aproximadamente **estacionario**: las mismas reglas, la misma vida. Un cambio de trabajo, de ciudad o de pareja puede cambiar la relación entre el sueño y el ánimo, no solo sus niveles. Un experimento N-de-1 vale para la persona y el periodo en que se hizo (capítulo 8, sección 8.12).

Este segundo límite tiene una consecuencia concreta, que el módulo 11 anota como hallazgo nuevo (**H-11-1**): **la lista de relaciones usa toda la historia**. `PantallaExplorar` pasa a `Estadistica.relaciones` el panel completo, desde el primer día. Quitar la tendencia corrige los **niveles** que cambian lento, pero no una **relación** que cambia con los años: si durante dos años el sueño y el ánimo iban muy juntos y en los últimos dos ya no, la lista muestra un promedio que no describe ninguno de los dos periodos. En un registro de una semana esto no importa; en uno de diez años, sí. **[Aproximación]**: es el supuesto de estacionariedad, razonable a corto plazo y cada vez menos cuanto más larga es la historia.

## 11.3 Validez de constructo, en conjunto

### 11.3.1 La pregunta

Un número puede ser preciso y aun así no medir lo que su nombre dice. La **validez de constructo** es justo eso: si una medición refleja el concepto teórico que pretende medir (Cronbach y Meehl, 1955). Messick (1995) la amplió: lo que se valida no es el instrumento en abstracto, sino **las interpretaciones** que se hacen de sus números. Para esta app, la pregunta útil es: **cuando leo el nombre de una variable, ¿qué estoy autorizado a creer que midió?**

### 11.3.2 Cuatro familias de variables

Los capítulos 3 a 7 permiten agrupar las variables según la distancia entre su nombre y lo que de verdad miden.

**a) Autorreportes con instrumento propio o adaptado.** Miden lo que dicen, con sesgos conocidos.

| Variable | Lo que el nombre sugiere | Lo que mide de verdad | Capítulo |
|---|---|---|---|
| `valencia` | Cómo fue tu día | Cómo **recuerdas** el día por la noche, en una escala 1–7 con anclas propias; desde `version_noche = 2`, los valores de tus avisos se ven **después** de elegir la valencia | 3.3.3, 3.4, 3.7.1 |
| `muestreo_valencia` | Tu ánimo durante el día | El ánimo en los avisos **que respondiste**, en una cuadrícula 7 × 7 sin anclas por número | 3.3.2, 3.5 |
| `esc_who5_*`, `esc_pss4_*` | Bienestar y estrés según instrumentos validados | Los mismos reactivos, pero preguntando por 7 días en lugar de 2 semanas (WHO-5) o un mes (PSS-4); la versión española de 4 reactivos del PSS no está validada en la fuente que cita la app | 3.9 |
| `rt_media` | Tu atención | 10 intentos en una pantalla táctil, frente a los 3 o 10 minutos de las versiones validadas de la PVT | 3.17 |

: Tabla 11.2. Cuatro familias de variables

**b) Proxies: medidas indirectas de otra cosa.** Miden algo cercano al nombre, casi siempre por debajo.

| Variable | Lo que el nombre sugiere | Lo que mide de verdad | Capítulo |
|---|---|---|---|
| `aire_libre_min` | Minutos al aire libre | El mayor entre los minutos con ≥ 1000 lux en el sensor del teléfono y los minutos a pie, corriendo o en bici; subestima | 4.4.5, 5.8 |
| `pantalla_min` | Tiempo con el teléfono | Minutos de apps en primer plano, sin el lanzador | 4.4.1 |
| `distancia_km` | Kilómetros recorridos | Suma de tramos en línea recta entre puntos de ubicación con filtros; cota inferior | 4.4.2 |
| `ruido_db` | Ruido del ambiente | Un nivel relativo del micrófono, no decibeles calibrados | 4.4.6 |
| `sueno_horas` | Horas dormidas | La estimación del reloj, que tiende a sobrestimar el sueño porque detecta mal la vigilia | 4.5.1 |
| Noches del teléfono (Ritmo) | Cuándo dormiste | Último uso del teléfono en la noche y primer uso en la mañana | 5.7.3 |

: Tabla 11.3. Cuatro familias de variables

**c) Modelos con parámetros de población.** No son mediciones de tu cuerpo, sino cuentas.

| Variable | Lo que mide de verdad | Capítulo |
|---|---|---|
| `cafeina_al_dormir_mg` | Una combinación de cuánta cafeína tomaste y a qué hora, con una vida media de 5 h (la real va de 1.5 a 9.5 h) | 5.3 |
| `alcohol_estimado_max`, `alcohol_al_dormir` | La fórmula de Widmark con β = 0.15 g/L/h y la *r* de Watson o fija; un orden de magnitud | 5.4 |
| `creatina_saturacion_pct`, `supl_*_nivel` | La reserva de un modelo, no una concentración | 5.6 |

: Tabla 11.4. Cuatro familias de variables

**d) Conteos de lo que escribes.** Miden tu escritura tanto como tu vida.

| Variable | Lo que el nombre sugiere | Lo que mide de verdad | Capítulo |
|---|---|---|---|
| `personas`, `per_<x>` | A quién viste | A quién **nombraste con @** en el diario y, desde 10e, en los momentos (en `per_`, también en sueños y cartas); `per_` mezcla haber estado, pensado, soñado y escrito | 7.7 |
| `vi_*`, `personas_vistas` | Con quién estuviste | Con quién marcaste «estuve», que es el estado por omisión de cualquier mención en el diario | 7.6, 7.7 |
| `cercania_media` | Qué tan cerca te sentiste ese día | La cercanía guardada de las personas que nombraste; cambia solo cuando la editas | 7.5.2 |
| `palabras` | — | Cuánto escribiste; entra en muchas parejas porque los días con más texto tienen más de todo lo que se cuenta en el texto | 6.15.5, 8.11.3 |
| Líneas de la Red y la Constelación | Quién se conoce | Quiénes nombraste el mismo día, al menos 2 días | 7.8 |
| `campana_<reto>` | Que hiciste el reto | Que lo tenías **aceptado** (asignación, no cumplimiento) | 9.5 |

: Tabla 11.5. Cuatro familias de variables

### 11.3.3 Qué implica

Lo importante no es que haya proxies (toda medición del mundo real lo es en algún grado), sino **leer cada relación con el nombre verdadero de sus variables**. «`per_ana` se asocia con mejor valencia» quiere decir «los días en que escribo sobre Ana con @ los recuerdo como mejores», que puede deberse a verla, a pensar en ella o a que esos días escribo más. Del mismo modo, una relación de `palabras` o de `personas` con cualquier otra cosa es, en buena parte, una relación con **cuánto escribiste ese día**.

Cronbach y Meehl (1955) proponen validar un constructo por su **red nomológica**: si una medición mide lo que dice, debe relacionarse con otras medidas como predice la teoría. Los datos de la app permiten algunas de esas comprobaciones en R, porque varias cosas se miden dos veces por caminos distintos (sección 11.7.3).

## 11.4 Sesgos, en conjunto

Cada capítulo describió sus sesgos. Vistos juntos, se agrupan en siete fuentes. Para cada una, la tabla dice qué marca existe en los datos para detectarla.

| Fuente | Qué produce | Marca en los datos | Capítulo |
|---|---|---|---|
| **Qué días faltan** | Los días malos se registran menos: las medias suben y las correlaciones se calculan sobre «los días con ganas» (faltante no al azar, MNAR) | `dia_dificil` (registro de un toque), `tasa_respuesta` (desde el esquema 13), días sin diario | 3.15 |
| **Cuándo se anotó** | Lo anotado tarde es recuerdo, no experiencia; desde la revisión de navegación hay más datos anotados tarde («Ponerte al día», tomas de ayer, «＋ agregar a este día») | `retroactivo` (por la hora de la valencia desde el esquema 13), `ts_creado` y `ts_editado` (desde el 14), `vm_retraso()` en R | 3.7.4, 3.15.3; H-NAV-9 |
| **Costuras del instrumento** | Series partidas en tramos que no son comparables del todo | `ventana_pantalla`, `ventana_pasos`, `version_noche`, `schema_v`; desde 10e el diccionario dice con qué parámetros (vida media, días de carga) se calculó cada exportación, pero no guarda su historial (H-4-11, en parte); desde 10f dice desde qué versión vale cada definición y qué cambió (`desde_version`, `cambio`; H-1-8), y `clima_reanalisis` marca si el clima del día salió del reanálisis o del pronóstico (H-3-4) | 2.3.2, 3.7.1; Ap. D |
| **Qué día es** | En una misma fila, unas variables cuentan la madrugada y otras no | El diccionario marca cada variable por día de calendario (`Diccionario.CALENDARIO`). Desde 10f, cada hora se lee con la zona que tenía el teléfono entonces (`Fechas.zonaEn`), y `desfase_utc_h` y `cambio_horario` marcan los días con cambio de hora o de zona (H-1-7); lo anterior al historial de zonas usa la zona de hoy, salvo los periodos que delatan dos o más notas seguidas con otro desfase | 2.3, 4.3 |
| **Confusores comunes** | Relaciones que se deben al día de la semana, la estación, una enfermedad | `dia_semana`, `fin_de_semana`, `festivo`, `dia_libre` en el panel exportado, y `sin_semana = TRUE` en R desde 10e; no hay marca de enfermedad salvo el contexto `ctx_enfermo` | 6.5.6; H-5-6 |
| **Cómo se eligió el análisis** | Más hallazgos por azar de los que dice cada prueba; la relación más fuerte exagera su tamaño | Ninguna: la app no registra qué modos se miraron | 1.6.3; H-11-2 |
| **La historia completa** | Una relación promedio de etapas de vida distintas | `vida_*` y las fechas de «Conocerte»; en R se puede analizar por periodo (sección 11.7.2) | 11.2.3; H-11-1 |

: Tabla 11.6. Sesgos, en conjunto

Tres observaciones sobre la tabla:

1. **Los sesgos no son independientes.** Un día difícil es a la vez un día con menos registros (fuente 1), más probablemente anotado tarde (fuente 2) y quizá un día de enfermedad (fuente 5). Los tres empujan en la misma dirección: los días que quedan en el análisis son mejores que los que faltan.
2. **Varias marcas existen solo desde hace poco** (esquemas 13 a 15). Para los años anteriores no se pueden reconstruir: `tasa_respuesta`, `ts_creado` y `ts_editado` quedan vacíos, `wifi_sin_dato_min` vale 0 antes del esquema 15 (antes esos minutos contaban en `sin_wifi_min`) y el historial de zonas solo conoce los viajes que las notas delatan (Apéndice D).
3. **La fuente 6 es la única sin ninguna marca.** No hay forma de saber, después, cuántas maneras de mirar los datos se probaron antes de encontrar algo. Es la razón del flujo confirmatorio de la sección 11.7.4.

## 11.5 Reactividad, en conjunto

**Reactividad** es que medir algo cambie lo que se mide (capítulo 3, sección 3.14). En esta app tiene cuatro fuentes, y la cuarta es buscada a propósito.

1. **Registrar.** El autoseguimiento tiende a aumentar las conductas deseables y reducir las indeseables (Korotitsch y Nelson-Gray, 1999). En el ánimo y el dolor el efecto parece pequeño (Cruise et al., 1996); poner nombre a una emoción puede bajar su intensidad (Lieberman et al., 2007).
2. **Lo que la pantalla muestra.** Los valores de tus avisos al calificar el día (desde `version_noche = 2`, solo después de elegir la valencia), la franja «Tus últimos días» en Hoy, la predicción de tu ánimo medio en enero, la hipótesis y el criterio escritos del experimento (efecto expectativa) (capítulos 3, 8 y 9).
3. **Los premios.** Logros y campañas dan premios por registrar, por conductas y, en dos logros secretos, por valores de la valencia (H-8-6; se quedan por decisión del autor, D.117). La ley de Campbell (1979) dice que un indicador que se usa para premiar se distorsiona (capítulo 9, sección 9.7).
4. **El propósito mismo.** La app quiere que te conozcas y, con eso, que cambies: dormir más tras ver tu sueño, gastar menos tras ver tus gastos. Eso es bueno para la vida y malo para la observación: los datos no describen una vida «sin intervenir», sino una vida que se observa a sí misma.

**Qué se puede hacer.** No eliminarla, sino **marcarla y separarla**:

- Las campañas son variables del panel desde el esquema 13 (`campana_<reto>`, `campanas_n`): se pueden usar como covariable o comparar periodos con y sin campaña (capítulo 9, sección 9.5).
- Los tramos de cada cambio de interfaz están fechados en el Apéndice D y, en parte, en los datos (`version_noche`).
- Las primeras semanas de cada medición nueva se pueden excluir o analizar aparte en R (capítulo 3, sección 3.14).
- Los experimentos personales dan la única estimación de un efecto con la reactividad **igual en las dos condiciones**: registras igual en las semanas «SÍ» y en las «no».

**[Aproximación]**: ninguna de estas medidas cuantifica la reactividad; solo permiten ver si una relación cambia cuando cambia la presión de medir.

## 11.6 Limitaciones de este paper

Este documento tiene sus propios límites, que conviene decir con la misma franqueza que los de la app.

1. **Se escribió leyendo el código, no usándolo.** Las descripciones salen de leer Kotlin y R. La compilación y las pruebas JVM corren en GitHub Actions, pero ninguna sesión ejecutó la app en un teléfono. Comportamientos que dependen de Android o de otras apps (cuánto conserva el sistema los eventos de uso, qué responde el sistema de una red wifi cuyo nombre esconde, si llega el aviso de cambio de zona horaria, con qué nombre exporta MacroFactor el press inclinado con mancuernas, qué sensor de presión trae el teléfono) quedaron como suposiciones marcadas.
2. **R se probó con datos reales en un solo punto.** El script se ejecutó con CSV sintéticos en las sesiones de 10d y con una exportación real de la v89, que reveló que faltaban 57 variables en el panel extendido (D.87). Después, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Las demás funciones de R (`vm_factores`, `vm_semana`, `vm_palabras`, `vm_red`…) solo se compararon con la app con datos inventados, y lo que agregó la entrega 10e (`vm_experimento` con su prueba de aleatorización, `vm_red(min_dias)`, el nuevo `vm_cruce`, `sin_semana`, `vm_dinamica` por mes) se corrió solo con CSV sintéticos. De lo que agregó 10f (`vm_hora_local`, y `sentido`, `umbral`, `intacto` y `criterio` en `vm_experimento`), el Apéndice D no dice que se haya ejecutado.
3. **Hay afirmaciones sin verificar.** `referencias.md` lista, por capítulo, las afirmaciones marcadas **[sin verificar]**: entre ellas, el tamaño original de la Affect Grid, los puntos de corte del WHO-5 (≤ 28) y del UCLA-3 (≥ 6), las vidas medias del magnesio y el omega-3, el valor 0.80 de Watson et al. (1981) y la forma exacta de la corrección de Marriott y Pope (1954). No se presentan como hechos, pero el lector debe tenerlas en cuenta.
4. **Las simulaciones son simulaciones.** Las cifras de falsas señales, cobertura y potencia (capítulos 6 y 8) salen de datos inventados con supuestos concretos (autocorrelación de orden 1, efectos constantes). Con datos reales, el comportamiento puede ser otro.
5. **Muchas manos.** Los capítulos los escribieron sesiones distintas, con modelos distintos, y se actualizaron en cada entrega del módulo 10. Puede haber frases que todavía describan el estado anterior a una entrega o cifras que no coincidan entre capítulos; los módulos 13 y 14 existen para encontrarlas.
6. **La literatura no es una revisión sistemática.** Las referencias se eligieron para respaldar o discutir decisiones concretas; no garantizan que no haya trabajos que digan otra cosa.
7. **Autor y sujeto son la misma persona.** Quien diseñó la app, quien la usa y quien aprueba los cambios es el mismo. No hubo una revisión externa del método. Las sesiones de Claude que escribieron el paper funcionaron como revisión, con los límites de 1 a 6.

## 11.7 Trabajo futuro

Lo que sigue son **propuestas**, no cambios. Según las reglas del plan, el código solo cambia con lo aprobado en una revisión como la del módulo 9. Varias de estas propuestas no requieren tocar la app: se hacen en R con lo que ya exporta.

### 11.7.1 Modelos multinivel

Los días de una vida no son una sola serie homogénea: están anidados en semanas, meses y etapas. Un **modelo multinivel** (también llamado jerárquico o de efectos mixtos) trata esa estructura de forma explícita: estima, por ejemplo, la relación entre el sueño y el ánimo **en cada año** y, a la vez, cuánto varía de un año a otro, prestando información entre años para que los años con pocos datos no den estimaciones extremas.

Dos usos concretos para esta app:

- **Relaciones que cambian con el tiempo** (H-11-1). Un modelo con la pendiente variando por año (o por etapa de `vida_*`) diría si la relación es estable o no. Los **modelos dinámicos de ecuaciones estructurales** (DSEM; Asparouhov, Hamaker y Muthén, 2018) combinan precisamente series de tiempo con autocorrelación, efectos que varían entre unidades (aquí, periodos) y datos faltantes. **[Validado]** como método; su aplicación a un solo sujeto con periodos como unidades es **[Aproximación]**.
- **Familias con muchos miembros.** Las variables dinámicas (`per_`, `lug_`, palabras del diario) comparan muchos miembros parecidos entre sí. Un modelo multinivel **encoge** las estimaciones de los miembros con pocos datos hacia el promedio de su familia, y así reduce la maldición del ganador sin necesidad de una corrección por comparaciones múltiples aparte (Gelman, Hill y Yajima, 2012). Es la respuesta de fondo a H-5-10: las palabras con 5 días que hoy salen en los extremos quedarían más cerca de 0.

### 11.7.2 Autocorrelación AR(1) en R

Qué hay hoy en `vestigium.R` (Apéndice C):

- `vm_relaciones` y `vm_factores` corrigen por autocorrelación con el n efectivo y, en las 0/1, con el factor AR(1), igual que la app (desde 10d).
- `vm_modelo_ar1` ajusta el modelo múltiple con **errores AR(1)** (`nlme::gls` con `corAR1(form = ~ t)`, con *t* = días desde el primero), que respeta que cada día se parece al anterior.
- Desde la entrega 10e (D.106): `vm_relaciones` y `vm_factores` aceptan `sin_semana = TRUE`, que quita el ciclo semanal de los restos sin tendencia (H-5-6, en parte), y `vm_cruce` da Pearson con intervalo y *p* con el n efectivo, como la app, además de Spearman (H-5-7, en parte; antes, solo Spearman con el n bruto).
- Desde la entrega 10e (D.97): **`vm_experimento(id)`** replica `Experimentos.resultado` y agrega la **prueba de aleatorización** exacta sobre todos los planes del sorteo por pares (H-7-11; capítulo 8, sección 8.6.4). Como el plan espejo da la misma diferencia con el signo contrario, el *p* mínimo real es 2/2^(N/2) sin lavado: con 12 semanas, ≈ 0.031.

Lo que falta, ordenado de lo más barato a lo más costoso:

1. **Usar lo que ya existe.** El ciclo semanal (H-5-6) se puede quitar en la lista con `vm_relaciones(sin_semana = TRUE)` y, en el modelo, controlar agregando `fin_de_semana` o `dia_libre` como predictor: por ejemplo, `vm_modelo_ar1("valencia", c("sueno_h", "pasos", "dia_libre"))`. Y la estabilidad en el tiempo (H-11-1) se puede revisar pasando un periodo a la lista: `vm_relaciones(datos = filter(panel, fecha >= as.Date("2026-01-01")))`, y comparando con otro periodo. No ejecuté estas llamadas en esta sesión: `fecha` es una fecha de R (`.vm_enriquecer` completa el calendario con `seq(…, by = "day")`) y ambas funciones aceptan `datos`, pero no las corrí.
2. **`vm_modelo` con errores AR(1) y el valor de ayer a la vez.** Hoy son dos funciones: `vm_modelo` tiene la opción del valor de ayer, pero supone residuos independientes; `vm_modelo_ar1` tiene los errores AR(1), pero no el valor de ayer.

### 11.7.3 Validar las heurísticas

Muchas reglas de la app son **[Diseño propio]** o **[Aproximación]**. Varias se pueden poner a prueba con los propios datos, porque la app mide algunas cosas dos veces o porque el usuario ya corrige algunas a mano:

| Heurística | Con qué compararla | Qué diría |
|---|---|---|
| Noches del teléfono (último y primer uso) | Las noches del reloj, en los días que tienen ambas | El error típico, en minutos, de la hora de dormir y despertar aproximadas |
| Sesiones de sueño del reloj | Las que marcaste «no cuenta» (`sueno_descartadas`, desde N7) | Cuántas sesiones falsas detecta el reloj |
| Co-mención el mismo día (líneas de la Red) | Los vínculos que declaraste en «¿A quién conoce?» | Qué fracción de las líneas derivadas son personas que de verdad se conocen |
| `per_<x>` como presencia | Las presencias revisadas (`presencias_revisado`, estados «estuve / pensé / mensajes») | Cuánto de `per_` es presencia y cuánto pensamiento o sueño |
| `aire_libre_min` | Los minutos a pie y en bici (`act_`), las horas en lugares de tipo «naturaleza» y el contexto del día «Naturaleza» (`ctx_naturaleza`) | Si la luz del teléfono y el movimiento cuentan lo mismo |
| Valencia del diario | `muestreo_valencia` y el cierre (`animo_cierre_valencia`) del mismo día | La convergencia entre el ánimo recordado y el momentáneo; en qué días se separan |
| Ventana de ±10 días y umbral de 0.12 para «lenta» | `vm_relaciones(ventana = 7)`, `14` o `28` | Si las relaciones firmes dependen de esas cifras (análisis de sensibilidad) |
| Factor AR(1) y n efectivo | Simulaciones con la autocorrelación real de tus variables | Si la cobertura del 95 % se cumple con la forma real de tus series, no solo con AR(1) |
| Detección de lugares (75 m, 20 min, 120 m) | Las visitas que corregiste («fue en…», «no estuve aquí», desde N7) | Los errores de la detección automática; hoy «no estuve aquí» no se guarda (capítulo 4, sección 4.4.3), así que primero habría que guardarlo |

: Tabla 11.7. Validar las heurísticas

La primera fila es la más fácil y la más útil: el panel ya trae `sueno_inicio_h`, `sueno_fin_h`, `primer_uso_h` y `ultimo_uso_h`, así que es una comparación de una tarde en R.

### 11.7.4 Del hallazgo a la conclusión: un flujo confirmatorio

La sección 1.6 y la tabla de 11.4 terminan en el mismo lugar: la lista de relaciones es **exploratoria**, y no hay forma de contar después las maneras en que se miraron los datos. La salida estándar en ciencia es separar **explorar** de **confirmar** (Nosek et al., 2018). Aplicado a un solo sujeto, sin cambiar la app:

1. **Explorar** en un periodo (por ejemplo, un año) con la lista de la app y con R.
2. **Escribir antes** las pocas relaciones que se quieren confirmar, con su dirección y la variable de control (por ejemplo, el día libre). Puede ser un texto con fecha en el repositorio de análisis personal. Para los experimentos, desde 10f la app ya lo hace: sella el sentido esperado y la diferencia mínima, con una huella (D.113; capítulo 8); para las relaciones de la lista no hay nada equivalente dentro de la app.
3. **Confirmar** con el periodo siguiente, que no se miró, con una sola prueba por relación (o pocas, corregidas entre sí) y el modelo AR(1) de R.
4. **Experimentar** con las que sobreviven y son accionables: el experimento personal es la única herramienta que responde a «qué me cambia».

Este flujo responde a tres problemas a la vez: las comparaciones múltiples (en el paso 3 hay pocas), la maldición del ganador (el tamaño se estima en datos nuevos) y la estacionariedad (si la relación desaparece en el segundo periodo, era de una etapa). Su costo es el tiempo: hay que esperar el segundo periodo. **[Validado]** el principio (prerregistro y confirmación con datos nuevos); **[Diseño propio]** su adaptación.

### 11.7.5 Hallazgos de gravedad media que siguen abiertos

Para una futura ronda de revisión con el mismo criterio del módulo 9. Pendiente del todo queda uno (la entrega 10e resolvió H-4-2 y dejó en parte H-5-6, H-6-3 y H-NAV-9; la 10f resolvió H-3-4, el otro de gravedad media: cada día guarda de dónde salió el clima, `clima_reanalisis`, y los días de pronóstico se cambian por el reanálisis a los 80 días, D.111):

| Hallazgo | Qué queda |
|---|---|
| H-1-4 | `coerceInputValues` puede convertir un nulo o un valor inválido en el valor por omisión sin registrarlo; contar esas conversiones necesita una revisión con el teléfono |

: Tabla 11.8. Hallazgos de gravedad media que siguen abiertos

Resueltos en parte, con una parte de gravedad media pendiente: H-5-6 (en la app no hay opción para quitar el ciclo semanal; en R sí, `sin_semana`), H-6-3 (desde 10e se elige la fecha del cambio de relación, pero lo anterior a la primera clasificación se proyecta), H-NAV-9 (las marcas existen; leer por tramo queda en manos de cada análisis), H-2-5 (perfiles llenados antes de la 10c), H-4-4 (sin pesos se usan 70 kg; no se muestra la incertidumbre), H-4-9 (vacaciones y turnos no cuentan como días libres), H-5-9 (aire libre con otra variable en «Tus números»), H-6-13 (un diario escrito sin @ cuenta como «no vi a nadie»), H-7-6 (en un antes/después puro, tendencia y efecto se separan mal), H-7-12 (las comparaciones de «Antes y después» no se corrigen), H-8-4 (reportes en carpetas públicas), H-8-5 (la exportación para R va en claro, a propósito, y el JSON lleva el texto de las cartas selladas), H-8-13 (reglas de restauración distintas por colección) y H-8-14 (no se puede elegir qué copia de Drive restaurar).

### 11.7.6 Comprobaciones pendientes

- **App frente a R con datos reales, más allá de la lista**: la lista de relaciones ya coincide (P-30; comprobado por el autor el 28 oct 2026). Faltaría lo mismo con «Todo lo que se mueve con algo» (`vm_factores`) y la Red (`vm_red`, con `min_juntos = 2`, `min_dias = 0` y las mismas 45 personas: desde 10e, `vm_red` deja fuera por omisión a quien se mencionó menos de 3 días), y probar `vm_experimento` con un experimento real, incluido el criterio sellado de 10f.
- **Lo de 10f que depende del teléfono o de la red** (Apéndice D, entrega 10f): que llegue el aviso de cambio de zona (`TIMEZONE_CHANGED`) y que, tras un viaje real, las horas y los días salgan con la zona de entonces; qué responde `ConnectivityManager` cuando Android esconde el nombre de la red; con qué nombre exporta MacroFactor el press inclinado con mancuernas (se reconocen formas en inglés y en español); y que el archivo de Open-Meteo ya tenga los días de más de 80 días.
- **R en la compilación automática**: hoy R no corre en CI; las pruebas JVM fijan los números que dio R con datos inventados, pero un cambio en `vestigium.R` que rompa algo solo se nota al usarlo.
- **Las afirmaciones [sin verificar]** de `referencias.md`, sobre todo las que sostienen umbrales que la app muestra (WHO-5 ≤ 28, UCLA-3 ≥ 6, jet lag social de 1 hora).

## 11.8 Conclusión

Vestigium Mei intenta algo que la literatura del autoseguimiento describe como difícil: no solo juntar datos de una vida, sino **sacar de ellos conclusiones que resistan el azar, las tendencias y el deseo de encontrar algo**. Después de la revisión de los módulos 1 a 10, la parte de análisis está a la altura de esa intención en lo que se puede pedir a una app personal: distingue «cero» de «no sé», mide el ánimo de varias formas sin mezclarlas, corrige por días parecidos y por muchas comparaciones, ofrece experimentos sorteados con intervalos honestos y deja que R verifique lo que muestra el teléfono.

Lo que no puede resolver ninguna app es la naturaleza de sus datos: **una sola persona, que sabe que se mide, que cambia con los años y que elige qué mirar**. Por eso sus conclusiones son **de asociación y para ti**, salvo las de los experimentos, que son causales con las condiciones del capítulo 8. Leídas así, con el nombre verdadero de cada variable y como pistas que se confirman con datos nuevos o con un experimento, las relaciones del panel pueden decir cosas que la memoria no dice. Leídas como verdades, engañan con la misma precisión con la que se calcularon.

## 11.9 Resumen del capítulo

1. Lo sólido: una sola regla para «cero» y «no sé», el ánimo medido de varias formas, la lista de relaciones con tres protecciones, R alineado con la app, experimentos sorteados por pares con intervalos corregidos, costuras marcadas y una sola regla de privacidad. De los 158 hallazgos de los módulos 1 a 9 y la navegación, después de la entrega 10f, 126 están resueltos, 31 en parte y 1 pendiente (H-1-4, de gravedad media; ninguno alto); con los dos de este capítulo y los seis del módulo 14, resueltos todos, son 166: 134 resueltos, 31 en parte y 1 pendiente.
2. Lo que se puede concluir depende de la herramienta: descripción, asociación de corto plazo dentro de tus días, predicción con desfase o efecto causal en un experimento. Ninguna generaliza a otras personas, y solo valen para tu futuro si tu vida no cambió en lo importante.
3. La lista de relaciones usa toda la historia, como si las relaciones fueran las mismas en todas las etapas (H-11-1, nuevo), y las maneras de mirar los datos no entran en ninguna corrección (H-11-2, nuevo).
4. Muchas variables miden algo distinto de su nombre: recuerdos, proxies, modelos o cuánto escribes. Hay que leer cada relación con el nombre verdadero de sus variables.
5. Los sesgos se agrupan en siete fuentes que suelen empujar en la misma dirección (los días que quedan son mejores que los que faltan). La única sin marca en los datos es cómo se eligió el análisis.
6. La reactividad viene de registrar, de lo que muestra la pantalla, de los premios y del propósito mismo de la app. No se elimina; se marca (campañas, tramos, experimentos).
7. El paper tiene límites propios: se escribió leyendo el código, R se comparó con la app con datos reales solo en la lista de relaciones y quedan afirmaciones sin verificar.
8. Trabajo futuro: modelos multinivel y DSEM, completar la corrección AR(1) en R (y usar ya `dia_libre` y los periodos), validar las heurísticas con lo que la app mide dos veces, un flujo confirmatorio que separe explorar de confirmar (los experimentos ya sellan su criterio desde 10f), resolver H-1-4 y probar en un teléfono lo que 10f dejó sin comprobar.


# Apéndice A. Variables y tablas del sistema

_Se generó a partir del código de la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper: `ciencia/Estadistica.kt` (`variables()`), `ciencia/Dinamicas.kt`, `data/Exportar.kt` (`tablas()`) y `data/Diccionario.kt`. Lo que cambió en el módulo 10 se marca «desde 10a…10d», lo de la entrega 10e, «desde 10e» (sección A.8), lo de la 10f, «desde 10f» (sección A.9), y todo está en el Apéndice D. Lo que dice «Si hay datos…» describe cuándo la variable existe en una instalación concreta: el catálogo se arma con lo que el usuario ha registrado._

**Cómo leer las columnas.** *Tipo* y *Unidad* se leen del nombre que ve el usuario en la app (la app no guarda un tipo formal por variable). *Fuente* es la de todo el grupo; la procedencia exacta de cada variable está en el panel (`Estadistica.panel`). *Un valor vacío significa «sin dato»; un 0 significa «se observó y fue cero»* salvo en las reglas de relleno con ceros del capítulo 2, sección 2.6. Las definiciones que cambiaron en la entrega 10c (cuándo vale 0 y cuándo queda vacía cada variable) están en la sección A.6.

## A.1 Variables fijas del panel analítico

### Ánimo

*Fuente:* Diario (Dia) y momentos/avisos (Momento)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `valencia` | Valencia del día | numérica | — | Si hay datos de la fuente |
| `activacion` | Activación del día | numérica | — | Si hay datos de la fuente |
| `muestreo_valencia` | Valencia promedio en avisos | numérica | — | Si hay datos de la fuente |
| `muestreo_activacion` | Activación promedio en avisos | numérica | — | Si hay datos de la fuente |
| `dia_dificil` | Día difícil (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `prediccion` | Predicción para mañana (1 a 7) | numérica | — | Si hay datos de la fuente |
| `error_prediccion` | Cuánto falló la predicción de anoche | numérica | — | Si hay datos de la fuente |
| `error_prediccion_signo` | El día contra la predicción de anoche (+ mejor, − peor): valencia de hoy − predicción de anoche, de −6 a +6 | numérica | — | Si hay datos de la fuente: los mismos días que `error_prediccion` (con valencia hoy y predicción la noche anterior); si falta una, vacía. Desde 10e (D.94) |
| `animo_valencia` | Valencia promedio al momento (todos los registros) | numérica | — | Si hay registros de ánimo |
| `animo_activacion` | Activación promedio al momento (todos los registros) | numérica | — | Si hay registros de ánimo |
| `animo_cierre_valencia` | Valencia al cerrar el día | numérica | — | Si hay registros de cierre del día |
| `animo_cierre_activacion` | Activación al cerrar el día | numérica | — | Si hay registros de cierre del día |
| `valencia_acompanado` | Valencia en avisos con alguien | numérica | — | Si hay avisos con «con quién» |
| `valencia_solo` | Valencia en avisos estando solo | numérica | — | Si hay avisos con «con quién» |
| `avisos_planeados` | Avisos de ánimo planeados | numérica | avisos | Desde el esquema 13, los días con avisos encendidos |
| `avisos_mostrados` | Avisos de ánimo mostrados (menos de 50 min tarde y con permiso de notificaciones) | numérica | avisos | Desde el esquema 13 |
| `avisos_respondidos` | Avisos mostrados con respuesta (con o sin valencia) | numérica | avisos | Desde el esquema 13 |
| `tasa_respuesta` | Proporción de avisos respondidos (respondidos ÷ mostrados) | numérica | 0 a 1 | Desde el esquema 13, si se mostró alguno |

: Tabla A.1. Ánimo

### Cuerpo

*Fuente:* Reloj y báscula (Health Connect), MacroFactor, sesiones, escalada

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `dolor` | Dolor físico | numérica | — | Si hay datos de la fuente |
| `hrv` | Variabilidad cardiaca (HRV) | numérica | — | Si hay datos de la fuente |
| `fc_reposo` | Frecuencia cardiaca en reposo | numérica | — | Si hay datos de la fuente |
| `fc_media` | Frecuencia cardiaca media del día | numérica | — | Si hay datos de la fuente |
| `fc_sueno` | Frecuencia cardiaca dormido | numérica | — | Si hay datos de la fuente |
| `spo2` | Saturación de oxígeno media (%) | numérica | % | Si hay datos de la fuente |
| `spo2_min` | Oxígeno mínimo de noche (%) | numérica | % | Si hay datos de la fuente |
| `pasos` | Pasos | numérica | — | Si hay datos de la fuente |
| `ejercicio_min` | Minutos de ejercicio (reloj) | numérica | — | Si hay datos de la fuente |
| `escalada` | Escalé (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `escalada_grado` | Grado máximo encadenado | numérica | — | Si hay datos de la fuente |
| `peso_kg` | Peso (kg) | numérica | kg | Si hay datos de la fuente |
| `temp_piel` | Variación de temperatura de la piel | numérica | — | Si hay datos de la fuente |
| `resp_rpm` | Frecuencia respiratoria | numérica | — | Si hay datos de la fuente |
| `cal_activas` | Calorías activas | numérica | — | Si hay datos de la fuente |
| `peso_tendencia_kg` | Peso de tendencia (MacroFactor) | numérica | — | Si hay datos de la fuente |
| `gimnasio` | Entrené en el gimnasio (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `volumen_kg` | Volumen del entrenamiento (kg) | numérica | kg | Si hay datos de la fuente |
| `series_gimnasio` | Series efectivas de gimnasio | numérica | — | Si hay datos de la fuente |
| `fuerza_ref_kg` | Fuerza de referencia: 1RM estimada en press inclinado con mancuernas (kg). La mejor del día con Epley, en series de 1 a 12 repeticiones sin calentamiento (`Macrofactor.fuerzaReferencia`, A.9) | numérica | kg | Si hay datos de la fuente: solo los días en que hiciste ese ejercicio; vacía los demás. Desde 10f (D.114) |

: Tabla A.2. Cuerpo

### Sueño

*Fuente:* Reloj (Health Connect), diario, teléfono (pantalla, carga)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `sueno_calidad` | Calidad de sueño percibida | numérica | — | Si hay datos de la fuente |
| `sueno_horas` | Horas de sueño (reloj) | numérica | — | Si hay datos de la fuente |
| `sueno_profundo` | Minutos de sueño profundo | numérica | — | Si hay datos de la fuente |
| `sueno_rem` | Minutos de sueño REM | numérica | — | Si hay datos de la fuente |
| `sueno_recordado` | Recordé un sueño (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `sueno_valencia` | Valencia del sueño | numérica | — | Si hay datos de la fuente |
| `carga_noche_h` | Hora de conectar el teléfono de noche | numérica | — | Si hay datos de la fuente |
| `sueno_inicio_h` | Hora de dormir (reloj) | numérica | — | Si hay datos de la fuente |
| `sueno_fin_h` | Hora de despertar (reloj) | numérica | — | Si hay datos de la fuente |
| `sueno_palabras` | Palabras del sueño escrito | numérica | — | Si hay datos de la fuente |
| `sueno_lucidez` | Lucidez del sueño (0 no, 1 parcial, 2 total) | numérica | — | Si hay datos de la fuente |
| `sueno_vividez` | Qué tan claro recuerdas el sueño (0 solo fragmentos, 1 partes claras, 2 casi completo y nítido) | numérica | — | Si hay datos de la fuente |
| `siesta` | Tomé siesta (0/1) | binaria (0/1) | — | Si hay siestas en el cronómetro |
| `siesta_min` | Minutos de siesta | numérica | — | Si hay siestas en el cronómetro |
| `cama_min` | Pantalla en la última hora antes de dormir (min) | numérica | min | Si hay datos de pantalla nocturna |
| `dormir_tras_tel_min` | Minutos entre soltar el teléfono y dormirte | numérica | — | Si hay datos de pantalla nocturna |
| `tel_tras_despertar_min` | Minutos entre despertar y tomar el teléfono | numérica | — | Si hay datos de uso al despertar |

: Tabla A.3. Sueño

### Teléfono

*Fuente:* Sensores del teléfono; importaciones de Google/YouTube

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `pantalla_min` | Minutos de pantalla | numérica | — | Si hay datos de la fuente |
| `desbloqueos` | Desbloqueos del teléfono | numérica | — | Si hay datos de la fuente |
| `fotos` | Fotos tomadas | numérica | — | Si hay datos de la fuente |
| `notificaciones` | Notificaciones recibidas | numérica | — | Si hay datos de la fuente |
| `canciones` | Canciones escuchadas (30 s o más, desde 10d) | numérica | — | Si hay datos de la fuente |
| `primer_uso_h` | Hora del primer uso del teléfono | numérica | — | Si hay datos de la fuente |
| `ultimo_uso_h` | Hora del último uso del teléfono | numérica | — | Si hay datos de la fuente |
| `audifonos_min` | Minutos con audífonos | numérica | — | Si hay datos de la fuente |
| `musica_min` | Minutos con audio sonando | numérica | — | Si hay datos de la fuente |
| `yt_vistos` | Videos de YouTube vistos | numérica | — | Si hay datos de la fuente |
| `yt_megusta` | «Me gusta» en YouTube | numérica | — | Si hay datos de la fuente |
| `yt_comentarios` | Comentarios en YouTube | numérica | — | Si hay datos de la fuente |
| `busquedas` | Búsquedas en Google | numérica | — | Si hay datos de la fuente |

: Tabla A.4. Teléfono

### Dinero

*Fuente:* Gastos e ingresos registrados

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `gasto_total` | Gasto del día | numérica | — | Si hay datos de la fuente |
| `gasto_imprevisto` | Hubo gasto imprevisto (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `gasto_imprevisto_monto` | Gasto imprevisto (monto): suma de los gastos imprevistos del día | numérica | tu moneda | Si hay datos de la fuente: 0 desde el primer gasto en los días con diario (`ceroDesde`, como `gasto_total`) y en los días con gastos sin ninguno imprevisto; vacía antes del primer gasto. Desde 10e (D.94) |
| `ingreso_total` | Ingresos del día | numérica | — | Si hay datos de la fuente |
| `balance_dinero` | Ingresos menos gastos del día | numérica | — | Si hay datos de la fuente |

: Tabla A.5. Dinero

### Texto

*Fuente:* Diario, cartas

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `palabras` | Palabras en el diario | numérica | — | Si hay datos de la fuente |
| `hitos_n` | Número de hitos | numérica | — | Si hay datos de la fuente |
| `carta_escrita` | Escribí una carta (0/1) | binaria (0/1) | — | Si hay datos de la fuente |

: Tabla A.6. Texto

### Social

*Fuente:* Diario (@personas), Tu gente, llamadas/SMS, WhatsApp (conteos)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `personas` | Personas distintas mencionadas: las nombradas con @ en el diario y, desde 10e, en los momentos no privados de ese día (D.93; antes, solo el diario) | numérica | — | Si hay datos de la fuente; vacía los días sin diario escrito ni momento con @ (10c, A.6) |
| `presencias_revisado` | Revisaste quién estuvo y en quién solo pensaste (0/1) | binaria (0/1) | — | Días con personas en el diario o ya revisados (desde 10c) |
| `llamadas` | Llamadas (entrantes y salientes) | numérica | — | Si hay datos de la fuente |
| `min_hablado` | Minutos al teléfono | numérica | — | Si hay datos de la fuente |
| `contactos` | Personas distintas por llamada o SMS | numérica | — | Si hay datos de la fuente |
| `wa_mios` | Mensajes de WhatsApp enviados | numérica | — | Si hay datos de la fuente |
| `wa_otros` | Mensajes de WhatsApp recibidos | numérica | — | Si hay datos de la fuente |
| `wa_chats` | Chats de WhatsApp activos | numérica | — | Si hay datos de la fuente |
| `cartas_otros` | Cartas escritas a otras personas | numérica | — | Si hay datos de la fuente |
| `cercanos` | Personas cercanas con las que estuve | numérica | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_familia` | Estuve con mi familia (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_pareja` | Estuve con mi pareja (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_amistades` | Estuve con una amistad (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_convivientes` | Estuve con quien vive conmigo (0/1) | binaria (0/1) | — | Si hay «Conocerte» con cercanas/convivientes o personas clasificadas |
| `vi_expareja` | Estuve con una expareja (0/1) | binaria (0/1) | — | Si alguna persona es o fue expareja |
| `vi_trabajo` | Estuve con un colega, compañero o mentor (0/1) | binaria (0/1) | — | Si hay personas de trabajo, compañeros o mentores |
| `personas_vistas` | Personas con las que estuve | numérica | — | Si hay presencias en el diario o momentos «pensando en» con personas |
| `personas_pensadas` | Personas en las que pensé sin verlas | numérica | — | Si hay presencias en el diario o momentos «pensando en» con personas |
| `personas_mensajes` | Personas con las que hablé por mensaje o llamada | numérica | — | Si hay presencias en el diario o momentos «pensando en» con personas |
| `penso_en_pareja` | Pensé en mi pareja sin verla (0/1) | binaria (0/1) | — | Si hay presencias y una pareja registrada |
| `penso_en_expareja` | Pensé en una expareja sin verla (0/1) | binaria (0/1) | — | Si hay presencias y una expareja |
| `penso_en_familia` | Pensé en mi familia sin verla (0/1) | binaria (0/1) | — | Si hay presencias |
| `penso_en_amistades` | Pensé en una amistad sin verla (0/1) | binaria (0/1) | — | Si hay presencias |
| `cercania_media` | Cercanía media de quien mencioné (1 a 7) | numérica | — | Si alguna persona tiene cercanía |
| `acompanado_pct` | Avisos de ánimo con alguien (0 a 1) | numérica | — | Si hay avisos con «con quién» |

: Tabla A.7. Social

### Mente

*Fuente:* Prueba de reacción

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `rt_media` | Tiempo de reacción (ms) | numérica | ms | Si hay datos de la fuente |

: Tabla A.8. Mente

### Movilidad

*Fuente:* Ubicaciones, visitas a lugares, actividad del teléfono

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `lugares_distintos` | Lugares distintos visitados | numérica | — | Si hay datos de la fuente |
| `distancia_km` | Kilómetros recorridos (aprox.) | numérica | — | Si hay datos de la fuente |
| `a_pie_min` | Minutos a pie | numérica | — | Si hay datos de la fuente |
| `vehiculo_min` | Minutos en vehículo | numérica | — | Si hay datos de la fuente |
| `coche_min` | Minutos en el coche (Bluetooth) | numérica | — | Si hay datos de la fuente |
| `sin_wifi_min` | Minutos sin wifi (fuera). Desde 10f, solo cuando el wifi estaba apagado o el sistema dijo que no había red wifi; los minutos sin dato van a `wifi_sin_dato_min` de `vm_conexiones.csv` (D.112, A.9) | numérica | min | Si hay datos de la fuente |
| `en_casa_min` | Minutos en casa | numérica | — | Si hay un lugar de tipo casa |

: Tabla A.9. Movilidad

### Entorno

*Fuente:* Open-Meteo, luna calculada, luz y barómetro del teléfono

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `t_max` | Temperatura máxima (°C) | numérica | °C | Si hay datos de la fuente |
| `t_min` | Temperatura mínima (°C) | numérica | °C | Si hay datos de la fuente |
| `precip_mm` | Lluvia (mm) | numérica | mm | Si hay datos de la fuente |
| `nubes_pct` | Nubosidad (%) | numérica | % | Si hay datos de la fuente |
| `luz_horas` | Horas de luz del día | numérica | — | Si hay datos de la fuente |
| `sol_horas` | Horas de sol directo | numérica | — | Si hay datos de la fuente |
| `presion_hpa` | Presión atmosférica (hPa) | numérica | hPa | Si hay datos de la fuente |
| `aqi_us` | Índice de calidad del aire | numérica | — | Si hay datos de la fuente |
| `pm25` | Partículas PM2.5 | numérica | — | Si hay datos de la fuente |
| `luna_ilum` | Luna iluminada (0 a 1) | numérica | — | Si hay datos de la fuente |
| `luz_alta_min` | Minutos con luz de exterior | numérica | — | Si hay datos de la fuente |
| `ruido_db` | Ruido medio al registrar (dB) | numérica | dB | Si hay datos de la fuente |
| `aire_libre_min` | Minutos al aire libre (estimado) | numérica | — | Si hay datos de la fuente |
| `presion_local_hpa` | Presión del barómetro del teléfono (hPa) | numérica | hPa | Si el teléfono tiene barómetro con lecturas |
| `cambio_presion_3h` | Mayor cambio de presión en 3 horas (hPa) | numérica | hPa | Si el teléfono tiene barómetro con lecturas |

: Tabla A.10. Entorno

### Agenda

*Fuente:* Calendario del teléfono

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `eventos` | Eventos en la agenda | numérica | — | Si hay datos de la fuente |
| `min_ocupado` | Minutos ocupados en agenda | numérica | — | Si hay datos de la fuente |

: Tabla A.11. Agenda

### Lectura

*Fuente:* Kindle (subrayados)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `subrayados` | Subrayados de Kindle | numérica | — | Si hay datos de la fuente |

: Tabla A.12. Lectura

### Nutrición

*Fuente:* MacroFactor

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `kcal` | Calorías consumidas | numérica | — | Si hay datos de la fuente |
| `proteina_g` | Proteína (g) | numérica | g | Si hay datos de la fuente |
| `carbohidratos_g` | Carbohidratos (g) | numérica | g | Si hay datos de la fuente |
| `grasa_g` | Grasa (g) | numérica | g | Si hay datos de la fuente |
| `fibra_g` | Fibra (g) | numérica | g | Si hay datos de la fuente |
| `alcohol_g` | Alcohol (g) | numérica | g | Si hay datos de la fuente |
| `cafeina_mg` | Cafeína (mg) | numérica | mg | Si hay datos de la fuente |
| `azucares_g` | Azúcares (g) | numérica | g | Si hay datos de la fuente |
| `balance_kcal` | Calorías menos gasto estimado | numérica | — | Si hay datos de la fuente |
| `primera_comida_h` | Hora del primer alimento (desde el 5 nov 2026, lo anotado antes de las 05:00 ya no cuenta como primero; D.156) | numérica | — | Si hay datos de la fuente |
| `ultima_comida_h` | Hora del último alimento; antes de las 05:00 suma 24, como `cafeina_ultima_h` (desde el 5 nov 2026; D.156) | numérica | — | Si hay datos de la fuente |
| `cafeina_ultima_h` | Hora de la última cafeína. Desde 10e (D.92), del día lógico, con las tomas de MacroFactor y de la app (antes de las 5:00 suma 24); antes, la de MacroFactor por día de calendario (A.8) | numérica | — | Si hay datos de la fuente |

: Tabla A.13. Nutrición

### Cultura

*Fuente:* Biblioteca (obras), citas

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `obras_dia` | Obras a las que les dediqué tiempo | numérica | — | Si hay datos de la fuente |
| `lectura` | Leí un libro o artículo (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `vi_pelicula_serie` | Vi una película o serie (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `jugue` | Jugué un videojuego (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `obra_terminada` | Terminé un libro, película, serie o juego (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `calificacion_obra` | Calificación de lo que terminé (1 a 10) | numérica | — | Si hay datos de la fuente |
| `citas_dia` | Citas y frases guardadas | numérica | — | Si hay datos de la fuente |

: Tabla A.14. Cultura

### Mundo

*Fuente:* Ubicaciones (ciudades, países, Patrimonio Mundial)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `ciudades_dia` | Ciudades distintas en el día | numérica | — | Si hay datos de la fuente |
| `fuera_de_casa` | Fuera de mi ciudad (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `extranjero` | En otro país (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `ciudad_nueva` | Primera vez en una ciudad (0/1) | binaria (0/1) | — | Si hay datos de la fuente |
| `unesco` | Pasé por un sitio del Patrimonio Mundial (0/1) | binaria (0/1) | — | Si hay datos de la fuente |

: Tabla A.15. Mundo

### Momentos

*Fuente:* Momentos con hora

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `n_madrugada` | Momentos de madrugada (antes de las 5) | numérica | — | Si hay datos de la fuente |
| `n_manana` | Momentos en la mañana (5 a 12) | numérica | — | Si hay datos de la fuente |
| `n_tarde` | Momentos en la tarde (12 a 19) | numérica | — | Si hay datos de la fuente |
| `n_noche` | Momentos en la noche (19 a 24) | numérica | — | Si hay datos de la fuente |
| `momentos_intensidad` | Intensidad media de los momentos no privados (1 a 5; desde el módulo 10a sin impulsos) | numérica | — | Si hay datos de la fuente |
| `momentos_valencia` | Valencia media de los momentos | numérica | — | Si hay datos de la fuente |
| `registros_animo` | Registros de ánimo en el día | numérica | — | Si hay registros de ánimo |

: Tabla A.16. Momentos

### Sustancias

*Fuente:* Consumos, MacroFactor, modelos de estimación

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `alcohol` | Tomé alcohol (0/1) | binaria (0/1) | — | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_bebidas` | Bebidas estándar | numérica | — | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_estimado_max` | Alcohol en sangre máximo estimado (g/L); desde 10e cuenta lo que seguía de la noche anterior (A.8) | numérica | g/L | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_al_dormir` | Alcohol en sangre estimado al dormirme (g/L) | numérica | g/L | Si hay consumos de alcohol o alcohol en MacroFactor |
| `alcohol_nivel_max` | Qué tan pedo llegué (0 a 10) | numérica | — | Si hay consumos de alcohol |
| `alcohol_horas` | Horas con efecto del alcohol | numérica | — | Si hay consumos de alcohol |
| `cannabis` | Cannabis (0/1) | binaria (0/1) | — | Si hay consumos de cannabis |
| `cannabis_nivel_max` | Nivel máximo con cannabis (0 a 10) | numérica | — | Si hay consumos de cannabis |
| `cannabis_horas` | Horas con efecto del cannabis | numérica | — | Si hay consumos de cannabis |
| `tabaco_n` | Cigarros o sesiones de vape | numérica | — | Si hay consumos de tabaco |
| `otra_sustancia` | Otra sustancia (0/1) | binaria (0/1) | — | Si hay consumos de «otra» |
| `cruda` | Cruda o bajón (0 a 5, del día anterior) | numérica | — | Si algún consumo tiene cruda |
| `cafeina_total_mg` | Cafeína del día (MacroFactor o anotada, mg) | numérica | mg | Si hay cafeína registrada o en MacroFactor |
| `cafeina_al_dormir_mg` | Cafeína en el cuerpo al dormirme (mg, estimada) | numérica | mg | Si hay cafeína registrada o en MacroFactor |

: Tabla A.17. Sustancias

### Privado

*Fuente:* Pantalla Privado. Desde el módulo 10a estas variables **no aparecen en ninguna lista ni análisis de la app**; el panel las calcula siempre (con o sin el interruptor) y van en la exportación para R (en `vm_variables.csv`, con `privada` = 1). La regla es `Privado.esVariable`: las de `Privado.VARIABLES`, las familias `med_` y `det_`, y el `hab_` y el `campana_hab_` del hábito ligado a las recaídas.

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `impulso` | Intensidad del impulso | numérica | — | Si se anotó |
| `recaida` | Recaída (0/1) | binaria (0/1) | — | Si se anotó |
| `n_recaida` | Recaídas registradas con hora | numérica | — | Desde la primera recaída con hora |
| `recaida_min` | Minutos de recaída | numérica | — | Si hay recaídas con duración |
| `libido_max` | Libido máxima del día (0 a 5) | numérica | — | Si hay registros de libido |
| `n_libido` | Registros de libido | numérica | — | Si hay registros de libido |
| `n_impulso` | Momentos de impulso | numérica | — | Días con diario desde el primer impulso (10c; antes, todos los días con diario). Hasta v84 estaba en el grupo «Momentos» y entraba en los análisis |
| `medicamentos_n` | Medicamentos tomados | numérica | — | Desde el primer día con medicamentos. En el código conserva el grupo «Salud» (así sale en `vm_variables.csv`), pero desde 10a es privada; hasta v84 entraba en los análisis |

: Tabla A.18. Privado

### Enfoque

*Fuente:* Cronómetro de enfoque y siestas

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `enfoque_min` | Minutos de enfoque (cronómetro) | numérica | — | Si hay tramos del cronómetro |

: Tabla A.19. Enfoque

### Suplementos

*Fuente:* Tomas de suplementos y modelos de carga

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `creatina_saturacion_pct` | Saturación de creatina estimada (%); desde 10e, con 3 g al día como dosis de referencia (A.8) | numérica | % | Si el suplemento correspondiente tiene datos |
| `probioticos_porciones` | Porciones de probióticos (anotadas y estimadas) | numérica | — | Si el suplemento correspondiente tiene datos |
| `probioticos_estimado` | Porciones de probióticos leídas en tu bitácora de comidas | numérica | — | Si el suplemento correspondiente tiene datos |

: Tabla A.20. Suplementos

### Laptop

*Fuente:* Cold Turkey (importación)

| Identificador | Descripción | Tipo | Unidad | Existe |
|---|---|---|---|---|
| `laptop_min` | Minutos en la laptop | numérica | — | Si se importó Cold Turkey |
| `laptop_web_min` | Minutos en sitios web (laptop) | numérica | — | Si se importó Cold Turkey con sitios web |

: Tabla A.21. Laptop

_Cuántas son. Regla de conteo: una variable fija es un `Variable("id", …)` con el identificador escrito tal cual en `Estadistica.variables()`. Desde el esquema 17 son **193, con 24 nombres de grupo** (187 desde 10f, 186 en la v91 y 184 en la v90). Las tablas de arriba traen **192 en 21 grupos** (186 hasta el esquema 16), porque: `dia_especial` («Calendario») y `campanas_n` («Campañas») van en la tabla siguiente, junto a las de su grupo; `n_impulso` se agrega aquí aunque el código la arma con una plantilla (`n_${it.id}` sobre `Catalogo.MOMENTOS`, que solo da esa), y `medicamentos_n`, la única del grupo «Salud», se lista en Privado. En la v81 eran 177 en 22 grupos; el esquema 13 (10b) agregó `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos`, `tasa_respuesta`, `campanas_n` y `dia_especial`, 10c, `presencias_revisado`, 10e, `error_prediccion_signo` y `gasto_imprevisto_monto` (D.94), y 10f, `fuerza_ref_kg` (D.114); el esquema 17 agregó tres contextos (`ctx_casa`, `ctx_fiesta`, `ctx_tramites`) y tres compañías (`con_hijos`, `con_mascota`, `con_mensaje`), D.132 y D.133. No todas existen en cada instalación: 133 se agregan siempre (entre ellas, las dos de 10e, los tres contextos nuevos y 4 privadas) y las otras 60 (entre ellas, la de 10f y las tres compañías nuevas), según la columna «Existe»._

### Variables con nombre generado

Estas se crean una por cada elemento que el usuario ha usado (o, en las de catálogo fijo, una por opción); el identificador lleva el id del elemento. Todas salen de `Estadistica.variables()`; las dos últimas filas tienen nombre fijo y van aquí con su grupo.

| Patrón | Descripción | Grupo |
|---|---|---|
| `n_<sentimiento>` | Registros con ‹sentimiento› (los usados, sin «otro») | Sentimientos |
| `n_<pensamiento>` | Registros pensando: ‹pensamiento› (los usados) | Momentos |
| `sueno_<categoria>` | Sueño: ‹categoría› (0/1); las categorías usadas, con valor solo en días con categorías | Sueños |
| `enfoque_<tipo>_min` | Enfoque: ‹tipo› (min); los tipos usados de estudio, lectura, idiomas, tesis, trabajo, creativo y, desde el esquema 17, otro (`enfoque_otro_min`, que junta todos los «otro» aunque tengan nombres distintos) | Enfoque |
| `horas_tipo_<tipo>` | Horas en lugares de tipo ‹tipo›; los tipos de tus lugares, de 14 posibles (12 hasta el esquema 17, que agregó restaurante o café y tienda) | Lugares |
| `laptop_<categoria>_min` | Minutos en la laptop: ‹categoría› (productivo, ocio, social, otro); con Cold Turkey | Laptop |
| `con_<id>` | Avisos con ‹pareja, amigos, familia, hijos, compañeros, desconocidos, mascota o mensaje› (0 a 1; las tres últimas compañías desde el esquema 17); si hay avisos con «con quién». «Mensaje» puede ir con «solo» y no cuenta como acompañado | Social |
| `ctx_<id>` | Contexto: ‹nombre› (0/1); siempre, los 13 contextos: trabajo, gym, viaje, enfermo, académico, ocio inmersivo, naturaleza, amigos, cita o tiempo en pareja (`ctx_romantico`), familia y, desde el esquema 17, casa o descanso, fiesta, trámites o pendientes | Contexto |
| `flow_<id>`, `sdt_necesidad_<id>`, `apreciacion_<id>` | Opción elegida en las tres preguntas científicas del día (0/1); siempre, 13 opciones (4, 5 y 4; «No aplica» no cuenta) | Lectura del día |
| `hab_<id>` | Hábito: ‹nombre› (0/1; vacío si ese día no lo contestaste, desde el esquema 17) | Hábitos |
| `supl_<id>_dosis/_tomado/_nivel/_porciones` | Dosis, toma, reserva o cantidad estimada y porciones del suplemento. Desde 10e, `_nivel` no existe en los suplementos de vida media menor de 6 horas (`Suplementos.VIDA_MEDIA_MIN_NIVEL`, A.8) | Suplementos |
| `campana_<reto>`, `campana_hab_<id>` | En campaña ese día (0/1; la tenías aceptada). 0 desde tu primera campaña, vacío antes. Desde el esquema 13 | Campañas |
| `campanas_n` | Campañas que tenías ese día. Desde el esquema 13 | Campañas |
| `dia_especial` | Ese día cae un cumpleaños, un aniversario o una fecha propia (0/1). Existe si hay alguna fecha guardada. Desde el esquema 13 | Calendario |

: Tabla A.22. Variables con nombre generado

**Pantalla y pasos, desde el esquema 13.** `pantalla_min`, `desbloqueos`, `primer_uso_h`, `ultimo_uso_h` y `pasos` se calculan por **día lógico** (de 05:00 a 05:00) en los días nuevos; los de antes quedaron por día de calendario. La ventana de cada día está en `vm_pantalla.csv:ventana`, `vm_salud.csv:ventana_pasos` y, en el panel exportado, `ventana_pantalla` y `ventana_pasos` (capítulo 2, sección 2.3.2).

## A.2 Familias de variables dinámicas

Cada familia crea una variable por cada elemento (persona, tema, lugar, app…) que aparece en al menos *Mín. días* días (en el análisis); en la exportación extendida basta con 2 días y se permiten hasta 400 por familia. *Máx.* es el tope en el análisis. Fuera de los días con dato de la fuente, el valor es 0 en las fechas de la *cobertura* de cada familia.

| Prefijo | Grupo | Máx. | Mín. días | Plantilla del nombre |
|---|---|---|---|---|
| `per_` | Personas | 15 | 5 | Aparece @‹elemento› (diario, mejor y peor momento, momentos, sueños o cartas) (0/1); 0 solo en días con diario escrito o momento con @ (10c) |
| `pen_` | Personas | 10 | 3 | Pensé en @‹elemento› sin verla (0/1) |
| `tema_` | Temas | 12 | 5 | Aparece #‹elemento› (0/1); 0 solo en días con diario escrito o momento con #tema (10c) |
| `hito_` | Hitos | 10 | 3 | Hito: ‹elemento› (0/1) |
| `lug_` | Lugares | 15 | 3 | Horas en «‹elemento›» (repartidas por día lógico desde el 5 nov 2026; D.155) |
| `app_` | Apps | 12 | 5 | Minutos en ‹elemento› |
| `ntf_` | Notificaciones | 8 | 5 | Notificaciones de ‹elemento› |
| `lap_` | Laptop | 10 | 4 | Minutos en ‹elemento› (laptop) |
| `web_` | Laptop | 10 | 4 | Minutos en ‹elemento› (navegador de la laptop) |
| `ytc_` | YouTube | 8 | 5 | Videos de ‹elemento› en YouTube |
| `gcat_` | Dinero | 15 | 5 | Gasto en ‹elemento› |
| `icat_` | Dinero | 8 | 3 | Ingreso de ‹elemento› |
| `art_` | Música | 10 | 5 | Canciones de ‹elemento› |
| `obr_` | Cultura | 10 | 5 | Le dediqué tiempo a «‹elemento›» (0/1) |
| `gen_` | Cultura | 10 | 5 | Leí, vi o jugué algo de ‹elemento› (0/1) |
| `med_` | Privado | 15 | 3 | Tomé ‹elemento› (0/1) |
| `det_` | Privado | 10 | 3 | Detonante: ‹elemento› (0/1) |
| `suenot_` | Sueño | 6 | 3 | Sueño realista / raro pero posible / imposible o surreal, o pesadilla (0/1); desde el esquema 17 (antes, el tipo) |
| `ciu_` | Mundo | 8 | 3 | Horas en ‹elemento› |
| `pais_` | Mundo | 6 | 3 | Horas en ‹elemento› |
| `musc_` | Cuerpo | 12 | 3 | Series de ‹elemento› |
| `act_` | Cuerpo | 6 | 3 | Minutos ‹elemento› |
| `nut_` | Nutrición | 60 | 14 | nombre del nutriente de MacroFactor traducido (p. ej., «Vitamina C (mg)»); excluye los ya fijos, los objetivos y los pasos/peso de MacroFactor |
| `ali_` | Nutrición | 12 | 5 | Comí ‹elemento› (0/1) |
| `esc_` | Cuestionarios | todos | — | «‹cuestionario›: ‹puntaje›»; desde 10c el puntaje vale para su día y los 6 previos (WHO-5, PSS-4) o los 29 previos (UCLA-3), y un día cubierto por dos respuestas se queda con la primera; la personalidad, hasta el test siguiente (máx. 365 días, `porDiaCuestionario`). Hasta 10c: 14 o 30 días previos, ganando la última |
| `pol_` | Política | ejes | — | «Brújula: ‹eje› (−100 ‹polo›, +100 ‹polo›)»; la posición de cada test vale hasta el test siguiente (máx. 400 días) |
| `vida_` | Etapas de vida | 14 + ciudades | — | «Etapa: ‹campo de Conocerte›»; se reconstruye con el historial de cambios. Desde 10c, vacía antes del primer guardado de «Conocerte» para los campos llenados ese día (`Perfiles.primerGuardado`) |

: Tabla A.23. Familias de variables dinámicas

## A.3 Tablas exportadas

La exportación genera 54 tablas CSV con datos (contadas en `Exportar.tablas()`: 53 antes del esquema 18, que agregó `vm_listas.csv`, A.12; 51 antes del esquema 16, que agregó `vm_ideas.csv` y `vm_ideas_revisiones.csv`, A.10; 49 antes de la entrega 10d, que agregó `vm_variables.csv` y `vm_familias.csv`; el esquema 13 había agregado `vm_personas.csv` y `vm_fechas_propias.csv`), más `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv`, `vm_diccionario.csv` (columnas `archivo`, `columna`, `tipo`, `descripcion` y `tabla`; desde 10f, también `desde_version` y `cambio`, A.9), `vm_respaldo.json`, `vm_datos.sqlite` (una tabla por cada CSV con datos, sin el «vm_», más `ubicaciones` y `diccionario`; no lleva el catálogo de fotos), un `LEEME.txt`, el propio `vestigium.R` y la carpeta `portadas/` (`Exportar.archivos` y `Exportar.zip`). La exportación resumida (`Exportar.zipResumido`) trae solo `vm_panel.csv`, su diccionario, el `LEEME.txt` y `vestigium.R`. Todos los CSV usan UTF-8 con BOM, fechas AAAA-MM-DD, booleanos 0/1, listas separadas por «|» y vacío para el dato faltante.

| Archivo | Contenido |
|---|---|
| `vm_panel.csv` | Una fila por día con lo principal de todas las fuentes |
| `vm_panel_extendido.csv` | Una fila por día con todas las variables: cada persona, lugar, app, categoría, artista, obra, ciudad, nutriente y más; desde 10d, el panel que analiza R |
| `vm_variables.csv` | Las variables que la app cruza entre sí, con su nombre y su grupo (las privadas, marcadas) (10d) |
| `vm_familias.csv` | Parejas de variables que la app esconde por obvias (la misma medida, una parte y su total…), en familias (10d) |
| `vm_dias.csv` | Diario: lo que llenaste cada día |
| `vm_momentos.csv` | Momentos y avisos de ánimo, uno por fila |
| `vm_tramos.csv` | Cronómetro: bloques de enfoque (estudio, lectura, idiomas, tesis…) y siestas, uno por fila |
| `vm_sustancias.csv` | Sustancias: cada consumo (alcohol, cannabis, tabaco, cafeína, otra) con su nivel, duración y cruda |
| `vm_horas.csv` | Una fila por hora: dormido, lugar, pantalla, notificaciones, luz, música, ánimo, cafeína y alcohol en el cuerpo, sustancias, enfoque, gastos y comidas |
| `vm_gastos.csv` | Gastos, uno por fila |
| `vm_notas.csv` | Notas sueltas: ideas, pensamientos, propósitos y recordatorios |
| `vm_ideas.csv` | Ideario: lo que aprendiste o pensaste, con quién lo dice, su fuente y cómo la tienes (esquema 16) |
| `vm_ideas_revisiones.csv` | Ideario: cada vez que volviste a ver una idea y qué respondiste (esquema 16) |
| `vm_suplementos.csv` | Cada toma de suplemento (y las porciones de probióticos estimadas de tu bitácora de comidas) |
| `vm_laptop.csv` | Minutos por app y por sitio en la laptop, por día (Cold Turkey) |
| `vm_red_nodos.csv` | Personas de tu red: relación, grupo de relación (columna `grupo`, desde 10c), cercanía, días mencionada y grado |
| `vm_red_vinculos.csv` | Pares de personas: cómo se conocen según tú y cuántos días aparecen juntas |
| `vm_personas.csv` | Tu gente: relación, cercanía, desde cuándo, nacimiento, fallecimiento y edad (esquema 13) |
| `vm_fechas_propias.csv` | Fechas propias que se repiten cada año (esquema 13) |
| `vm_experimentos.csv` | Experimentos personales: pregunta, hipótesis sellada, diseño, lavado y plan por semana (1 = con la acción); desde el esquema 13, también `fin` y `hecho` («¿Lo hiciste hoy?»); desde el 15, el criterio sellado (`sentido`, `umbral`) y la huella (`huella`, `intacto`) |
| `vm_metas.csv` | Metas: horizonte, fecha objetivo, estado, medida, progreso e historial de cambios |
| `vm_ingresos.csv` | Ingresos, uno por fila |
| `vm_escalas.csv` | Cuestionarios validados (WHO-5, PSS-4, UCLA-3, TIPI) |
| `vm_reaccion.csv` | Pruebas de reacción |
| `vm_salud.csv` | Health Connect por día (reloj y báscula) |
| `vm_pantalla.csv` | Tiempo en pantalla por día |
| `vm_visitas.csv` | Llegadas y salidas de tus lugares |
| `vm_entorno.csv` | Clima, luz, aire y luna por día; desde el esquema 15, de dónde salió el clima (`fuente`) |
| `vm_fotos.csv` | Fotos tomadas por día |
| `vm_agenda.csv` | Agenda por día |
| `vm_notificaciones.csv` | Notificaciones por día (solo conteos) |
| `vm_musica.csv` | Canciones y podcasts, uno por fila |
| `vm_actividad.csv` | Minutos por tipo de movimiento |
| `vm_ejercicio.csv` | Sesiones de ejercicio del reloj |
| `vm_escalada_sesiones.csv` | Sesiones de escalada |
| `vm_escalada_bloques.csv` | Bloques o vías de cada sesión |
| `vm_biblioteca.csv` | Libros, películas, series, juegos y artículos |
| `vm_citas.csv` | Citas de tus obras y frases guardadas con ♥ |
| `vm_listas.csv` | Listas libres: cada elemento, cuándo se agregó y cuándo se hizo (esquema 18) |
| `vm_mundo.csv` | Ciudades añadidas a mano, países rascados y sitios del Patrimonio Mundial marcados |
| `vm_cartas.csv` | Cartas: a tu yo del futuro y a otras personas (las selladas, sin su texto) |
| `vm_perfil.csv` | Conocerte: cómo está ahora y cada cambio con su fecha |
| `vm_politica.csv` | Brújula política: un test por año, ejes y respuestas |
| `vm_comunicacion.csv` | Llamadas y SMS por día (solo conteos) |
| `vm_conexiones.csv` | Wifi, audio, carga, batería y luz por día; desde el esquema 15, los minutos sin dato de wifi aparte (`wifi_sin_dato_min`) |
| `vm_subrayados.csv` | Subrayados y notas de Kindle |
| `vm_google_dia.csv` | YouTube y búsquedas de Google por día |
| `vm_google.csv` | YouTube y búsquedas de Google, una por fila |
| `vm_youtube.csv` | YouTube (CSV de Takeout): suscripciones, «me gusta», videos en listas, comentarios y chats (sin texto) |
| `vm_whatsapp.csv` | WhatsApp: un chat por día por fila (solo conteos) |
| `vm_nutricion.csv` | MacroFactor: nutrición, gasto energético y peso por día |
| `vm_comidas.csv` | MacroFactor: cada alimento registrado |
| `vm_entrenos.csv` | MacroFactor Workouts: resumen por día y por grupo muscular; desde el esquema 15, `fuerza_ref_kg` |
| `vm_series.csv` | MacroFactor Workouts: cada serie; desde el esquema 15, si es del ejercicio de referencia (`referencia`) |

: Tabla A.24. Tablas exportadas

## A.4 Columnas nuevas del esquema 13 (entrega 10b)

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_panel.csv` | `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos`, `tasa_respuesta` | Los avisos de ánimo del día (capítulo 3, sección 3.5.3) |
| `vm_panel.csv` | `n_muestreos` | Ahora cuenta **todos** los avisos respondidos, con o sin valencia (antes, solo con valencia) |
| `vm_panel.csv` | `campana_<reto>`, `campanas_n` | Campañas como variable (capítulo 9, sección 9.5) |
| `vm_panel.csv` | `ventana_pantalla`, `ventana_pasos` | `logica` o `calendario` |
| `vm_panel.csv` | `dia_especial`, `dia_especial_tipo` | Día especial y de qué tipo: `mi_cumpleanos`, `cumpleanos`, `fallecimiento`, `aniversario` u `otra` (lista) |
| `vm_dias.csv` | `ts_valencia` | Cuándo se llenó o cambió la valencia; decide `retroactivo` |
| `vm_experimentos.csv` | `fin`, `hecho` | Fecha de fin; respuestas de «¿Lo hiciste hoy?» como `fecha:1` o `fecha:0` (lista; sin fecha = sin respuesta) |
| `vm_pantalla.csv` | `ventana` | `logica` (05:00 a 05:00) o `calendario` (0:00 a 24:00) |
| `vm_salud.csv` | `ventana_pasos` | Igual, para los pasos |
| `vm_personas.csv` | `persona`, `relacion`, `detalle`, `cercania`, `desde`, `nacimiento`, `fallecimiento`, `edad`, `historial` | Fechas en AAAA-MM-DD, o --MM-DD sin año. `edad` = años cumplidos hoy, o al morir |
| `vm_fechas_propias.csv` | `nombre`, `fecha`, `mes_dia`, `anio`, `tipo` | Fechas propias que se repiten cada año |
| `vm_respaldo.json` | `avisos` | Los avisos de ánimo de cada día, para restaurarlos |

: Tabla A.25. Columnas nuevas del esquema 13 (entrega 10b)

## A.5 Columnas nuevas del esquema 14 (revisión de navegación, entrega NE)

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_momentos.csv`, `vm_gastos.csv`, `vm_ingresos.csv` | `ts_creado`, `ts_editado` | Cuándo se anotó y cuándo se corrigió por última vez. Vacíos en los registros de antes del esquema 14 |
| `vm_sustancias.csv`, `vm_tramos.csv`, `vm_suplementos.csv` | `ts_creado` | Cuándo se anotó. En `vm_suplementos.csv` va también `ts` (cuándo se tomó); las porciones estimadas de probióticos quedan vacías |
| `vm_gastos.csv` | `subcategoria` | Subcategoría dentro de la categoría (por ejemplo, Comida → Café). Vacía = sin subcategoría |
| `vm_dias.csv`, `vm_panel.csv` | `version_noche` | Con qué orden de preguntas se llenó la hoja de la noche (`VERSION_NOCHE`): 1 = el orden con que se creó el campo en la entrega NE; 2 = el orden nuevo (N-18, desde la entrega N4: el ánimo del momento primero; capítulo 3, sección 3.7.1). Vacío = no pasó por la hoja o es de antes del esquema 14 |
| `vm_salud.csv` | `sueno_descartadas` | Las sesiones del reloj de esa noche que marcaste «no cuenta» (fuente y horas, por ejemplo, «Samsung Health 15:10–16:05»); no entran ni en `sueno_min` ni en `siesta_reloj_min` |

: Tabla A.26. Columnas nuevas del esquema 14 (revisión de navegación, entrega NE)

**Cómo leer `ts_creado`.** La hora de un registro (`ts`, `inicio` o `fecha` + `hora`) dice **cuándo pasó**; `ts_creado`, **cuándo lo anotaste**. La diferencia dice si se anotó en el momento o después. En R, `vm_retraso(tabla, umbral_min = 60)` la calcula (`retraso_min`) y marca `tarde` si pasó más del umbral o cayó en otro día lógico (Apéndice C). El umbral es **[Diseño propio]**.

## A.6 Definiciones que cambiaron en la entrega 10c (cálculos)

No cambia lo que se guarda (`SCHEMA_V` = 14); cambia cómo se calcula el panel con lo ya guardado, **también en los días viejos**. Detalle y motivo en el Apéndice D (D.62 a D.78) y en el capítulo 2, sección 2.6.1. «Base» son los días en que la variable puede valer 0; fuera de ellos queda vacía.

| Variable | Definición desde 10c | Antes |
|---|---|---|
| `gasto_total`, `gasto_imprevisto` | 0 en los días con diario desde el **primer gasto** (`ceroDesde`) | 0 en todos los días con diario |
| `n_impulso` (privada) | 0 en los días con diario desde el **primer impulso** | 0 en todos los días con diario |
| `escalada` | 0 en los días con diario desde la **primera sesión de escalada** | 0 en todos los días con diario |
| `gimnasio` | 0 en los días con diario desde el **primer entrenamiento**, más los días con entrenamiento | 0 en cualquier fecha del panel desde el primer entrenamiento |
| `hab_<id>` | 1 si lo marcaste; 0 desde `Habito.desde`; vacío antes (los hábitos sin esa fecha, 0 siempre). Desde el esquema 17, en los días con `habitos_tri`, vacío si no lo contestaste (D.133) | 0 en todos los días con diario, también antes de existir (igual en `vm_dias.csv`) |
| `sueno_recordado` | 1 si recordaste; 0 solo si ese día dijiste algo del sueño (calidad o texto); si no, vacío | 0 en cualquier día con diario sin sueño recordado |
| `sdt_necesidad_autonomia`, `_competencia`, `_relacion` | También 1 si elegiste «las tres» | 0 con «las tres» |
| `personas`, `vi_*`, `cercanos`, `cercania_media`, `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `penso_en_*` | Solo en los días con diario escrito o con un momento no privado que nombra a alguien con @ (`Dinamicas.diasSociales`); vacías en los demás | En cualquier día con documento de diario o algún momento |
| `per_`, `pen_` | 0 en los días de `diasSociales` y en los días donde aparece otra persona; los temas del sueño cuentan solo si llevan @ | 0 en todos los días con diario o momentos no privados; cualquier etiqueta del sueño sin # contaba como persona |
| `tema_` | 0 en los días con diario escrito o con un momento que lleva algún #tema | 0 en todos los días con diario o momentos no privados |
| `vi_convivientes` | Compara con los convivientes pasados por `canon` (equivalencias en cadena) | Comparaba el texto tal cual |
| `cercania_media` | La cercanía de cada día respeta «sin cercanía» en el historial | Un tramo sin cercanía usaba la cercanía actual |
| `presencias_revisado` (nueva en el panel) | 1 si revisaste las presencias ese día; 0 si hay personas en el diario sin revisar; vacío si no | Solo en `vm_dias.csv` |
| `ejercicio_min` | 0 los días con datos del reloj (pasos o ritmo cardiaco) y sin sesión, desde la primera sesión | Vacío esos días |
| `distancia_km` | GPS (`Movilidad.kmPorDia`): 0 los días con ubicaciones y sin movimiento. En `vm_salud.csv`, `distancia_km` es la de Health Connect (otra fuente; el diccionario las distingue) | Vacío esos días |
| `lugares_distintos` | Por **día lógico** de la llegada | Por fecha de calendario de la llegada |
| `probioticos_estimado` | Solo los días con bitácora de MacroFactor (o con algo leído en ella) | 0 todos los días desde la primera toma |
| `esc_*` | Ventana del periodo preguntado (7 o 30 días) y la primera respuesta que cubre el día | Ventana de 14 o 30 días, ganando la última respuesta |
| `animo_cierre_valencia`, `animo_cierre_activacion` (y `animo_valencia`, `animo_activacion`, `registros_animo`) | Sin los cierres guardados otro día que el que cierran (`Estadistica.cierreFuera`) | Con todos los cierres de esa fecha |
| `vida_*` | Vacías antes del primer guardado de «Conocerte» para los campos llenados ese día | El valor se proyectaba hacia atrás |
| `alcohol` | 1 el día lógico de cada dosis (y el de los episodios sin cantidad); 0 los demás desde el primer registro | 1 el día del episodio, y también el día de las dosis: dos reglas a la vez |
| `alcohol_estimado_max`, `alcohol_al_dormir` | Peso más reciente entre báscula y tendencia de MacroFactor (si no hay anterior, el primero posterior; 70 kg solo sin ningún peso) y, con peso real, sexo, estatura y (en hombres) edad, la *r* de Watson; si no, *r* fija por sexo | La báscula ganaba siempre a MacroFactor, 70 kg antes de la primera pesada, *r* fija por sexo |
| `cafeina_total_mg`, `alcohol_bebidas` | Incluyen las tomas de MacroFactor sin hora legible (al mediodía de su día) | Esas tomas se perdían |
| `n_momentos` (`vm_panel.csv`) | Sin cambio de cálculo; el diccionario aclara que incluye los momentos privados | Igual |

: Tabla A.27. Definiciones que cambiaron en la entrega 10c (cálculos)

**Columnas nuevas en la exportación (10c).**

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_panel.csv` | `festivo` | Día de descanso obligatorio de la Ley Federal del Trabajo de México, art. 74 (0/1); vacío si en «Conocerte» no vives en México (`Fechas.esFestivoMx`) |
| `vm_panel.csv` | `dia_libre` | Sábado, domingo o festivo (0/1): la regla de día libre del jet lag social (`Fechas.esDiaLibre`) |
| `vm_red_nodos.csv` | `grupo` | Grupo de la relación: familia, pareja, amistad, trabajo u otro; el que da el color en la Red |

: Tabla A.28. Definiciones que cambiaron en la entrega 10c (cálculos)

**Por día de calendario.** Las descripciones del diccionario de `vm_panel.csv` y `vm_panel_extendido.csv` terminan con «· por día de calendario (0:00 a 24:00)» en las variables de `Diccionario.CALENDARIO` y en las familias `ciu_` y `pais_` (capítulo 2, sección 2.3.2).

## A.7 La exportación en la entrega 10d (R y exportación)

Tampoco cambia lo que se guarda (`SCHEMA_V` = 14). Cambian los valores de algunas columnas de `vm_panel.csv` y hay dos tablas nuevas. Detalle en el capítulo 2 (sección 2.8) y en el Apéndice D.

**`vm_panel.csv` con los valores de la app (P-30).** En cada columna que comparte nombre con el panel de la app, el valor es el de la app (`Exportar.comoLaApp`), con el mismo tipo que tenía la columna (0/1, entero o número). Las que más cambian:

| Columna | Desde 10d (= la app) | Antes en `vm_panel.csv` |
|---|---|---|
| `gasto_total` | 0 en los días con diario desde el primer gasto; vacío antes | 0 en cualquier fecha exportada |
| `ingreso_total` | 0 en los días con diario desde el primer ingreso; vacío antes | 0 en cualquier fecha exportada |
| `escalada`, `gimnasio` | 0 en los días con diario desde la primera sesión o el primer entreno; vacío antes | Falso en cualquier fecha exportada |
| `ejercicio_min` | 0 los días con datos del reloj y sin sesión, desde la primera sesión | Vacío esos días |
| `canciones` | Escuchas de 30 s o más (o sin duración), en la app y en el CSV | El CSV ya filtraba 30 s; la app contaba todas |
| `ultimo_uso_h` | +24 solo antes de las 5:00 (1:00 = 25; 11:00 = 11) | +24 antes de las 12:00 (11:00 = 35) |
| `audifonos_min`, `coche_min` | 0 en días con datos de conexiones y sin ese audio | Vacío esos días |
| `luna_ilum` | La iluminación calculada de la fase lunar, en todos los días del panel | La de `vm_entorno.csv` si existía |
| `ruido_db_medio` | `ruido_db` de la app: la media de todos los registros del día con ruido | La media solo de los momentos que no son avisos |

: Tabla A.29. La exportación en la entrega 10d (R y exportación)

**Once columnas con otro nombre en la app** (`Exportar.EQUIVALENTES`; no se renombran, y el diccionario lo dice en las dos):

| `vm_panel.csv` | Panel de la app | ¿Mismo valor? |
|---|---|---|
| `sueno_min` | `sueno_horas` | La misma fuente; en minutos frente a horas |
| `sueno_profundo_min` | `sueno_profundo` | Sí (toma el de la app) |
| `sueno_rem_min` | `sueno_rem` | Sí |
| `hrv_rmssd` | `hrv` | Sí |
| `km` | `distancia_km` | Sí |
| `valencia_muestreo` | `muestreo_valencia` | Sí |
| `luz_dia_min` | `luz_horas` | La misma fuente; en minutos frente a horas |
| `sol_min` | `sol_horas` | La misma fuente; en minutos frente a horas |
| `carga_noche_inicio_h` | `carga_noche_h` | Sí |
| `temp_piel_delta` | `temp_piel` | Sí |
| `ruido_db_medio` | `ruido_db` | Sí |

: Tabla A.30. La exportación en la entrega 10d (R y exportación)

**Tablas nuevas.**

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_variables.csv` | `variable` | Nombre de la columna en `vm_panel_extendido.csv` |
| `vm_variables.csv` | `nombre`, `grupo` | Qué es y su grupo, como lo dice la app |
| `vm_variables.csv` | `privada` | Seguimiento privado (0/1): la app no lo cruza; en R, solo con `privadas = TRUE` |
| `vm_familias.csv` | `familia` | La regla de la app que la produce (por ejemplo, `familia_3`, `total:per_`, `persona:ana`, `escala:who5`, `igual:distancia_km`) |
| `vm_familias.csv` | `variable` | Columna de `vm_panel_extendido.csv`, o su nombre en `vm_panel.csv` |
| `vm_familias.csv` | `lado` | 0 = obvia con todas las de la familia; 1 o 2 = obvia solo con las del otro lado |

: Tabla A.31. La exportación en la entrega 10d (R y exportación)

**Otras columnas que cambiaron (P-33).** `vm_dias.csv:schema_v` vale 0 en los días importados de Obsidian o de la versión web que no traían versión; `ts_creado` y `ts_editado` de los días de Obsidian quedan vacíos si la nota no los traía. `vm_ubicaciones.csv:precision_m` guarda la precisión de los puntos del formato viejo de Takeout (antes vacía). La descripción de `vm_red_nodos.csv:grado` aclara que usa 1 día juntas y hasta 400 personas, frente a 2 días y 45 personas en la app.

## A.8 Definiciones que cambiaron en la entrega 10e

Tampoco cambia lo que se guarda (`SCHEMA_V` = 14): cambia cómo se calculan algunas variables con lo ya guardado, **también en los días viejos**, y hay dos variables nuevas. Detalle y motivo en el Apéndice D (D.88 a D.107); los modelos de sustancias y suplementos se explican en el capítulo 5.

| Variable | Definición desde 10e | Antes |
|---|---|---|
| `error_prediccion_signo` (nueva) | Valencia de hoy − predicción de la noche anterior (−6 a +6; positiva, el día salió mejor de lo predicho). Los mismos días que `error_prediccion` (D.94) | No existía; solo el valor absoluto (H-2-10) |
| `gasto_imprevisto_monto` (nueva) | Suma de los gastos imprevistos del día; 0 desde el primer gasto en los días con diario (`ceroDesde`) y en los días con gastos sin ninguno imprevisto; vacía antes (D.94) | No existía; `gasto_imprevisto` solo dice si hubo alguno (0/1) (H-1-5) |
| `personas` | Personas distintas nombradas con @ en el diario **y** en los momentos no privados de ese día, en los mismos días en que puede valer 0 (`Dinamicas.diasSociales`) (D.93) | Solo el diario: un día con solo momentos valía 0 aunque nombraras a alguien (H-14-2) |
| `alcohol_estimado_max` | Cada día con alcohol mira de su primera dosis a 14 horas después, o hasta justo antes de la primera dosis del siguiente día con alcohol; la simulación incluye las dosis de las 24 horas previas, y se lee cada 5 minutos, como `Sustancias.alcoholMaximo` (D.88, D.89) | Cada día empezaba en 0 y se leía cada 10 minutos (H-4-5, H-14-5) |
| `alcohol` | En el hilo principal, cuando aún no están calculadas las dosis de MacroFactor: 1 los días con un episodio anotado y vacío los demás (D.90). Fuera de ese caso, como en A.6 | Esos días sin episodio valían 0 (H-14-4) |
| `cafeina_total_mg`, `alcohol_bebidas` (y lo que sale de las mismas dosis: `cafeina_al_dormir_mg`, `alcohol_estimado_max`, `alcohol_al_dormir`) | MacroFactor y la app se juntan toma por toma (`Sustancias.dosis`, `MISMA_TOMA` = 90 min). Cafeína: una toma de la app y una de MacroFactor a menos de 90 minutos y de cantidad parecida (de la mitad al doble) cuentan una vez, con la hora de la app. Alcohol: un episodio de la app y lo de MacroFactor entre su inicio y su fin (±90 minutos) son el mismo, y cuenta la fuente con más gramos (D.91) | Si MacroFactor tenía algo de esa sustancia ese día, se ignoraba todo lo de la app (H-4-2) |
| `cafeina_ultima_h` | Hora de la última de esas mismas dosis, por día lógico; antes de las 5:00 suma 24. En `vm_panel.csv`, la misma; si no se pudo calcular, la de MacroFactor. `cafeina_mg` sigue siendo el nutriente de MacroFactor tal cual (D.92) | La de MacroFactor, por su fecha de calendario (H-4-12, en parte) |
| `creatina_saturacion_pct` | Llega al 95 % en los días de carga (28 por omisión) con **3 g** al día, la dosis de referencia (`Suplementos.dosisReferencia`); con 5 g al día se llena antes, en unos 17 días (D.98) | Con la dosis que pusiste (5 g en el catálogo) llegaba al 95 % en 28 días (H-4-6) |
| `supl_<id>_nivel` | Solo en los suplementos con vida media de 6 horas o más (`Suplementos.VIDA_MEDIA_MIN_NIVEL`); la melatonina del catálogo (1 hora) ya no lo tiene (D.98) | También en los de vida media corta, que a las 5:00 casi siempre valían 0 (H-4-7, en parte) |

: Tabla A.32. Definiciones que cambiaron en la entrega 10e

**El diccionario y el LEEME (10e).** La descripción de `cafeina_al_dormir_mg`, `creatina_saturacion_pct` y cada `supl_<id>_nivel` termina con los parámetros vigentes al exportar (vida media, o días de carga y de lavado con la dosis de referencia) y con el aviso de que cambiarlos recalcula toda la historia (`Diccionario.conParametros`, D.99; H-4-11, en parte). El LEEME y el diccionario dicen «esquema 13» y «esquema 14» donde decían «v13» y «v14», y el LEEME dice que el esquema de cada día está en `schema_v` de `vm_dias.csv` (D.107, H-14-1). Las descripciones nuevas del diccionario son las de `error_prediccion_signo`, `gasto_imprevisto_monto`, `cafeina_ultima_h` y `vm_panel_extendido.csv:personas`.

## A.9 Columnas nuevas del esquema 15 (entrega 10f)

Cambia lo que se guarda: `SCHEMA_V` = 15. Una variable nueva en el panel de la app (`fuerza_ref_kg`) y columnas nuevas en la exportación, que no son variables del panel. Detalle y motivo en el Apéndice D (D.109 a D.118).

| Archivo | Columna | Qué es |
|---|---|---|
| `vm_panel.csv` | `desfase_utc_h` | Horas de diferencia con UTC a mediodía de ese día, con la zona que tenía el teléfono entonces (`Fechas.desfaseH`; −6 en la Ciudad de México). Desde 10f, cada hora y cada fecha de las tablas es la hora local de entonces (D.109, D.110; capítulo 2, sección 2.3.1) |
| `vm_panel.csv` | `cambio_horario` | 0/1: el desfase con UTC al empezar el día lógico no es el de su último instante (`Fechas.cambioDeHora`): horario de verano o un viaje. Ese día no duró 24 horas (fórmula B.24) |
| `vm_panel.csv` | `clima_reanalisis` | 0/1: el clima del día viene del archivo histórico de Open-Meteo (reanálisis, tipo ERA5) (1) o de los modelos de pronóstico (0); vacío si no hay clima o no se sabe de dónde vino (D.111; capítulo 4) |
| `vm_entorno.csv` | `fuente` | `archivo` o `pronostico`. Lo guardado antes del esquema 15 se deduce (`Entorno.fuente`): `archivo` si se pidió más de 80 días después de la fecha (`Entorno.DIAS_ARCHIVO`). Los días del último año que vinieron del pronóstico se vuelven a pedir al archivo cuando pasan de 80 días; el índice UV (`uv_max`), que el archivo no trae, se conserva del pronóstico (D.111) |
| `vm_conexiones.csv` | `wifi_sin_dato_min` | Minutos en que no se pudo saber si había wifi (sin permiso o sin respuesta del sistema). Desde 10f no cuentan en `sin_wifi_min`, que puede bajar desde esta versión; antes, sí contaban (D.112; capítulo 4) |
| `vm_experimentos.csv` | `sentido`, `umbral` | El criterio sellado: hacia dónde esperabas el cambio (1 sube, −1 baja; vacío = no lo dijiste) y la diferencia mínima que contaría como efecto, en las unidades de la medida (vacío = cualquiera con el intervalo lejos del cero). Fórmula B.22 (D.113; capítulo 8) |
| `vm_experimentos.csv` | `huella`, `intacto` | SHA-256 de lo sellado (`Experimentos.huella`) y si sigue coincidiendo con lo que hay ahora (1), si cambió después del sello (0) o si no tiene huella (vacío, experimentos de antes del esquema 15) |
| `vm_entrenos.csv`, `vm_panel.csv` | `fuerza_ref_kg` | La mejor 1RM estimada del día en el press inclinado con mancuernas (fórmula B.23); vacía los días sin ese ejercicio (D.114) |
| `vm_series.csv` | `referencia` | 0/1: la serie es del ejercicio de referencia (`Macrofactor.esReferencia`) |
| `vm_diccionario.csv` | `desde_version`, `cambio` | Desde qué versión de la app rige la definición actual de la columna y qué cambió entonces, con su fila del Apéndice D; vacío = sin cambios desde la v84 (`Diccionario.desde`; D.115, H-1-8). La tabla que las llena (`Diccionario.DESDE`, 165 entradas, con comodines como `per_*`) se armó con las filas del Apéndice D (capítulo 2, sección 2.8) |

: Tabla A.33. Columnas nuevas del esquema 15 (entrega 10f)

**Lo que cambia sin columna nueva.** `sin_wifi_min` (arriba) y todas las horas que la app saca de un instante: `primer_uso_h`, `ultimo_uso_h`, `cafeina_ultima_h`, las horas de `vm_horas.csv` y las demás `*_h` se calculan con la zona que tenía el teléfono entonces, o con el desfase de la propia nota (`Fechas.local`, `Fechas.localDe`). Mientras no viajes, nada cambia. Lo que el teléfono ya guardó sumado por día (sueño, pasos, pantalla, ubicaciones) no se recalcula. La plantilla «Creatina» mide desde 10f `fuerza_ref_kg` en lugar de `volumen_kg`; un experimento creado con la plantilla de antes sigue midiendo `volumen_kg`.

## A.10 Tablas nuevas del esquema 16 (Ideario)

Cambia lo que se guarda: `SCHEMA_V` = 16, con una colección nueva (`ideas`) y un ajuste (`Config.ideas_resurgir`, si sale «Una idea de antes» en el Día). No hay variables nuevas en el panel ni cambia ninguna columna de las tablas anteriores. Detalle en el capítulo 2 (sección 2.5) y en el Apéndice D (D.123 a D.126).

`vm_ideas.csv` tiene una fila por idea (`Exportar.filasIdeas`):

| Columna | Qué es |
|---|---|
| `id`, `creada`, `fecha`, `editada` | Identificador; cuándo se escribió (fecha y hora con su desfase); el día al que pertenece (por omisión, el día lógico en que se anotó; se puede cambiar); la última edición |
| `titulo` | La idea en una línea (opcional) |
| `texto` | Lo que entendiste, con tus palabras, con sus @personas y #temas |
| `origen` | `leido`, `visto` (o escuchado), `contado` o `propia`; vacío = sin decir |
| `quien` | Lista de autores o invitados que lo dicen. No son personas de Tu gente: no cuentan en la red social |
| `obra_id`, `obra_titulo`, `cita_id` | La obra y la cita de las que salió; `obra_titulo` queda vacío si la obra se borró |
| `fuente`, `enlace`, `donde` | De dónde salió cuando no hay obra (escrita a mano o al compartir), su enlace y el minuto o la página |
| `postura` | Cómo la tienes: `convence`, `piensa` (te hace pensar), `duda` o `desacuerdo`; vacío = sin decir |
| `fijada`, `n_revisiones` | 0/1 si está fijada arriba; cuántas veces la respondiste en «Una idea de antes» |
| `personas`, `temas` | Las @personas escritas en el texto; los temas del campo «Temas» (desde el esquema 17) y los #temas del texto, sin repetir (listas) |

: Tabla A.34. Tablas nuevas del esquema 16 (Ideario)

`vm_ideas_revisiones.csv` tiene una fila por respuesta (`Exportar.filasIdeasRevisiones`): `idea_id`, `fecha` (día lógico en que respondiste) y `respuesta` (`sigue`, «sigo pensando igual»; `cambio`, «ya no»; `ignorar`, «ahora no»). En R se leen como `vm$ideas` y `vm$ideas_revisiones`.

**Al leer los datos.** Las ideas no son una muestra de lo que aprendiste: solo está lo que decidiste anotar. `postura` y `revisiones` son autorreporte y empiezan vacías en casi todas las ideas. Una idea pasada desde una nota conserva la `creada` y la `fecha` de la nota; una pasada desde el diario es una copia con la fecha de ese día. `Diccionario.DESDE` no tiene entrada para estas columnas porque son nuevas: no hay una definición anterior con la cual compararlas.

## A.11 Cambios del esquema 17 (pantallas de captura)

Cambia lo que se guarda: `SCHEMA_V` = 17. El detalle está en el capítulo 3 y en el Apéndice D (desde D.127).

**Sueños** (`vm_dias.csv`, `vm_panel.csv`):

| Columna | Qué es |
|---|---|
| `sueno_rareza` | `realista`, `raro` (pero posible) o `imposible` (o surreal). En días de antes del esquema 17 se deduce del tipo: mundano = realista, bizarro = imposible |
| `sueno_pesadilla` | 1 si fue pesadilla (angustia fuerte o te despertó), 0 si dijiste que no y vacío si no se dijo. En días de antes, 1 si el tipo era «ansioso o pesadilla» |
| `sueno_tipo` | Ya no se pregunta; solo trae valor en los días de antes del esquema 17 que no se han corregido |
| `sueno_categorias`, `sueno_<categoría>` | Nueve ejes (prefijos `per_`, `esc_`, `int_`, `tema_`, `res_`, `sen_`, `est_`, `emo_`, `desp_`). El tipo «recurrente» de antes cuenta como `est_recurrente` |
| `suenot_realista`, `suenot_raro`, `suenot_imposible`, `suenot_pesadilla` (panel extendido) | Reemplazan a `suenot_<tipo>` |

: Tabla A.35. Cambios del esquema 17 (pantallas de captura)

**Momentos y hoja de la noche** (`vm_momentos.csv`, `vm_dias.csv`, `vm_panel.csv`):

| Columna | Qué es |
|---|---|
| `vm_momentos.csv:intensidades` | Lista `emoción:n` (1 un poco, 2 bastante, 3 mucho); solo las emociones a las que les pusiste intensidad |
| `vm_momentos.csv:pensamientos` | Lista de pensamientos. `pensamiento` queda con el primero, por compatibilidad |
| `vm_momentos.csv:detonante_otro`, `vm_dias.csv:detonante_otro` | El detonante escrito con «otro» (seguimiento privado) |
| `n_<emoción>` nuevas | `n_amor`, `n_decepcion`, `n_anhelo`…; solo desde que se usan. Antes del esquema 17 no se podían marcar |
| `n_<pensamiento>` nuevos | `n_trabajo`, `n_cuerpo`, `n_imaginando`, `n_nada`. Un registro con varios pensamientos cuenta en cada uno |
| `con_hijos`, `con_mascota`, `con_mensaje` | Igual que los demás `con_*`. «Mensaje» puede ir con «solo» y no cuenta como acompañado |
| `ctx_casa`, `ctx_fiesta`, `ctx_tramites` | Contextos nuevos; 0 en los días de antes (no existían) |
| `vm_dias.csv:ctx_otro` | Otro contexto escrito a mano |
| `vm_dias.csv:habitos_tri`, `hab_*` | Con `habitos_tri` = 1, un hábito sin respuesta queda vacío en `hab_*` en vez de 0 |

: Tabla A.36. Cambios del esquema 17 (pantallas de captura)

**Ideario** (`vm_ideas.csv`): `temas` reúne los temas del campo «Temas» (desde el esquema 17) y los `#temas` escritos en el texto, sin repetir. Antes eran solo los del texto.

**Dinero** (`vm_gastos.csv`): `se_repite` (0/1), si el gasto se repite (renta, suscripción, servicios); solo se exporta. Las categorías Vivienda y Servicios aparecen como cualquier otra en `gcat_*`.

## A.12 Cambios del esquema 18 (deseados y listas)

Cambia lo que se guarda: `SCHEMA_V` = 18. El detalle está en el capítulo 2 (sección 2.5) y en el Apéndice D (D.145 y D.146). **No hay variables nuevas en el panel**: lo de esta sección solo se exporta como dato crudo. **[Diseño propio]**: agregar algo a una lista no describe cómo fue un día, y sumar columnas casi siempre en cero solo agregaría comparaciones a `vm_relaciones`.

**Deseados** (`vm_biblioteca.csv`, tres columnas nuevas; vacías en las obras de antes):

| Columna | Qué es |
|---|---|
| `prioridad` | Si está pendiente, qué tan pronto la quieres: `pronto`, `cuando_pueda` o `algun_dia`; vacío = sin decir |
| `recomendo` | Quién te la recomendó: personas de tu gente, sin @ (lista) |
| `donde` | Dónde verla o conseguirla, escrito a mano |

: Tabla A.37. Cambios del esquema 18 (deseados y listas)

La descripción de `estado` ahora incluye `pausado`, que ya existía desde D.143 pero el diccionario no nombraba.

**Listas libres** (`vm_listas.csv`, `Exportar.filasListas`, una fila por elemento):

| Columna | Qué es |
|---|---|
| `lista_id`, `lista` | Identificador y nombre de la lista (lugares por visitar, cosas por comprar…) |
| `id`, `texto`, `nota`, `enlace` | El elemento, una nota y un enlace |
| `prioridad` | `pronto`, `cuando_pueda` o `algun_dia`; vacío = sin decir |
| `agregado` | Día lógico en que lo agregaste |
| `hecho` | Día lógico en que lo tachaste; vacío = pendiente |
| `dias_hasta_hecho` | Días entre `agregado` y `hecho` (vacío si sigue pendiente). *Ejemplo inventado:* agregado el 1 de octubre y hecho el 11 → 10 |

: Tabla A.38. Cambios del esquema 18 (deseados y listas)

En R se lee como `vm$listas`.


# Apéndice B. Fórmulas

_Todas las fórmulas del núcleo estadístico (capítulo 6), con la función de origen. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10c», «desde 10d», «desde 10e» o «desde 10f» y está en el Apéndice D (entrega 10c, D.62 a D.78: la app; entrega 10d, D.79 a D.87: R y la exportación; entrega 10e, D.88 a D.108; entrega 10f, D.109 a D.118). Las tres últimas (B.22 a B.24) no son del capítulo 6: son las fórmulas que agregó la entrega 10f (capítulos 8, 5 y 2). Notación: n = número de días (o semanas) con ambos datos; x̄ = promedio; s² = varianza muestral con n − 1; atanh y tanh son la tangente hiperbólica inversa y directa._

## B.1 Correlación de Pearson — `Estadistica.pearson`, función interna `r` de `Estadistica.relaciones`

$$r=\frac{\sum_{i=1}^{n}(x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_{i=1}^{n}(x_i-\bar x)^2\ \sum_{i=1}^{n}(y_i-\bar y)^2}}\qquad\qquad (B.1)$$

Condiciones: n ≥ 3 (`pearson`); si alguna suma de cuadrados es 0, devuelve `null`.

## B.2 Correlación de Spearman — `Estadistica.spearman`, `Estadistica.rangos`

$$\rho = r\big(\operatorname{rango}(x),\ \operatorname{rango}(y)\big)\qquad\qquad (B.2)$$

Rangos de 1 a n; a un grupo de valores empatados que ocupan los lugares i a j (base 0) se le asigna (i + j)/2 + 1.

## B.3 Resto sin tendencia — `Estadistica.series` (privada); `quitar` en `vm_relaciones`

$$\text{resto}_i = x_i-\frac{1}{|W_i|}\sum_{j\in W_i}x_j,\qquad W_i=\{\,j:\ |d_j-d_i|\le V,\ x_j\ \text{existe}\,\}\qquad\qquad (B.3)$$

d = número de día del calendario (o de semana × 7). V = 10 días (por día) o 35 días (por semana). El propio día está en W_i. Solo si |W_i| ≥ 3; si no, el resto falta.

**Variable lenta:** var(x) > 0, al menos 14 restos (8 por semana) y var(resto)/var(x) < 0.12.

## B.4 Autocorrelación de orden 1 — `Estadistica.autocorr` (privada); `ac1` en `vm_relaciones`

$$r_1 = r\big(\{x_i\},\{x_{i+1}\}\big)\quad\text{sobre los pares con } d_{i+1}-d_i=\text{paso}\qquad\qquad (B.4)$$

paso = 1 (por día) o 7 (por semana). Si hay menos de 10 pares (o la serie no varía), desde 10e r₁ = 0.3 (`Estadistica.AR1_SIN_DATO`, D.95; **[Diseño propio]**); hasta 10e, 0. Antes de usarse se recorta a [−0.5, 0.97].

**Desde 10c**, la misma autocorrelación sobre una serie con fechas es pública: `Estadistica.ar1(serie, paso)`, que devuelve `null` con menos de 10 pares. La usan «Cruzar dos variables» y «Todo lo que se mueve con algo», que con `null` toman 0.3 desde 10e (antes, 0), y la diferencia de medias (B.11a), que con `null` sigue tomando 0.

## B.5 Número efectivo de días — `Estadistica.relaciones`; desde 10c, también `Estadistica.correlacionEf`; en R, `vm_relaciones` y `vm_factores`

$$n_{Ef}=\operatorname{acotar}\Big(n\,\frac{1-r_a r_b}{1+r_a r_b},\ \min(4,n),\ n\Big)\qquad\qquad (B.5)$$

r_a, r_b: autocorrelaciones (B.4) de cada variable, sobre restos o valores según el modo; la que no se puede estimar vale 0.3 desde 10e (antes, 0), en la app y en R (`.vm_ar1_sin_dato`, también en `vm_cruce` y `vm_presion`). Si n_Ef ≤ 3, la pareja no se evalúa. Referencia: Bretherton et al. (1999); Pyper y Peterman (1998).

## B.6 Transformación de Fisher — `Estadistica.ic95`, `Estadistica.relaciones`, `Estadistica.correlacionEf`

$$z=\operatorname{atanh}(r)=\tfrac12\ln\frac{1+r}{1-r},\qquad EE_z=\frac{1}{\sqrt{N-3}}\qquad\qquad (B.6)$$

N = n (en `ic95`) o n_Ef (en `relaciones` y, desde 10c, en `correlacionEf`, que usan «Cruzar dos variables» y «Todo lo que se mueve con algo» con la misma fórmula B.5). r se recorta a ±0.9999. Hasta 10c, `factores` usaba N = n con ρ de Spearman.

## B.7 Intervalo de 95 % de una correlación

$$IC_{95}=\big[\tanh(z-1.96\,EE_z),\ \tanh(z+1.96\,EE_z)\big]\qquad\qquad (B.7)$$

`ic95` exige n ≥ 5.

## B.8 Valor p bilateral — `Analisis.pBilateral`, `Analisis.phi`

$$p=2\,\big(1-\Phi(|z|/EE_z)\big),\qquad \Phi(u)=\tfrac12\big(1+\operatorname{erf}(u/\sqrt2)\big)\qquad\qquad (B.8)$$

con la aproximación de Abramowitz y Stegun (1964, 7.1.26), para x = |u|/√2:

$$\operatorname{erf}(x)\approx 1-(a_1t+a_2t^2+a_3t^3+a_4t^4+a_5t^5)\,e^{-x^2},\quad t=\frac{1}{1+0.3275911\,x}\qquad\qquad (B.8·2)$$

a₁ = 0.254829592, a₂ = −0.284496736, a₃ = 1.421413741, a₄ = −1.453152027, a₅ = 1.061405429; error absoluto ≤ 1.5 × 10⁻⁷. Para u < 0 se usa la simetría. El resultado se recorta a [0, 1].

## B.9 Benjamini-Hochberg — `Analisis.bh`; `p.adjust(p, "BH")` en R

Con los m valores p ordenados p₍₁₎ ≤ … ≤ p₍ₘ₎:

$$q_{(k)}=\min_{j\ge k}\ \min\!\Big(1,\ \frac{m}{j}\,p_{(j)}\Big)\qquad\qquad (B.9)$$

Criterio «se sostiene»: q < 0.10 (lista de relaciones, factores, R); q < 0.05 y n ≥ 30 en «Tu año». En `relaciones`, `q` se calcula solo entre parejas no obvias y `qConObvias` entre todas.

**Orden de la lista** (`relaciones`): primero por

$$\text{seguro}=\begin{cases}\text{lo}&\text{si lo}>0\\ -\text{hi}&\text{si hi}<0\\ 0&\text{si el intervalo incluye el 0}\end{cases}\qquad\qquad (B.9·2)$$

de mayor a menor; luego por |r|.

**Tarjeta** (`ui/Relacion.kt`): grosor del lazo = (1.2 + 4.5·|r|) dp; punteado y opacidad 0.65 si q ≥ 0.10; «(≈ n_Ef independientes)» si n_Ef < 0.8 n; «con tendencia: r_bruta» si |r_bruta − r| ≥ 0.2.

## B.10 Panel semanal — `Estadistica.semanal`

$$\bar x_{s}=\frac{1}{|D_s|}\sum_{f\in D_s}x_f,\qquad D_s=\{\text{días de la semana } s \text{ (lunes a domingo) con dato}\},\ |D_s|\ge 3\qquad\qquad (B.10)$$

Para una variable 0/1 es la proporción de días con 1.

## B.11 Diferencia de medias — `Estadistica.diferenciaCompleta` (desde 10c); `.vm_dif_dias` en R (desde 10d)

Con n_a, n_b ≥ 3 y autocorrelaciones ρ_a, ρ_b acotadas a [0, 0.9] (0 si no se dan):

$$d=\bar a-\bar b,\qquad S_a=\frac{s_a^2}{n_a}\cdot\frac{1+\rho_a}{1-\rho_a},\qquad S_b=\frac{s_b^2}{n_b}\cdot\frac{1+\rho_b}{1-\rho_b},\qquad EE=\sqrt{S_a+S_b}\qquad\qquad (B.11)$$

$$n^{*}_a=\max\!\Big(2,\ n_a\frac{1-\rho_a}{1+\rho_a}\Big),\qquad gl=\operatorname{acotar}\Big(\frac{(S_a+S_b)^2}{S_a^2/(n^{*}_a-1)+S_b^2/(n^{*}_b-1)},\ 1,\ n^{*}_a+n^{*}_b-2\Big)\qquad\qquad (B.11·2)$$

$$IC_{95}=d\pm t_{0.975;\,gl}\,EE,\qquad p=p_T(d/EE,\ gl)\qquad\qquad (B.11·3)$$

Con ρ = 0 es la t de Welch (1947). Si EE = 0, el intervalo es el punto d. `Estadistica.diferencia(a, b)` devuelve (d, lo, hi). Hasta 10c: IC = d ± 1.96 · √(s_a²/n_a + s_b²/n_b), y en `factores` p = 2(1 − Φ(|d|/EE)).

## B.11a Autocorrelación de cada grupo — `Estadistica.ar1Grupos`, `diferenciaEnDias` (desde 10c)

Para dos grupos de días con fecha:

$$\tilde\rho(S)=\hat\rho_1(S)+\frac{1+4\,\hat\rho_1(S)}{|S|}\qquad\qquad (B.11a)$$

donde ρ̂₁ es B.4 sobre días seguidos (`ar1`) y |S| el número de días de la serie. ρ_a = ρ̃(a) si a tiene 10 pares de días seguidos; si no, ρ̃ de los dos grupos juntos, cada uno centrado en su media (un valor por fecha); si tampoco, 0. Después se acota a [0, 0.9] en B.11. La corrección de sesgo es la aproximación de primer orden que se atribuye a Marriott y Pope (1954) [sin verificar en el original]; se validó por simulación (con 30 + 30 días y ρ = 0.5, cobertura del 95 %).

**Día de la semana** (`Analisis.porDiaSemana`), con n ≥ 5 (desde 10c; antes n ≥ 2 y 1.96): x̄ ± t₀.₉₇₅;ₙ₋₁ · s/√n.

**Cambio reciente** (`Analisis.cambio`, con `diferenciaEnDias`): a = días con hoy − 30 < fecha ≤ hoy; b = días con hoy − 60 < fecha ≤ hoy − 30.

**Todo lo que se mueve con algo** (`Analisis.factores`, desde 10c): para una variable 0/1, `diferenciaEnDias` del objetivo (resto sin tendencia B.3, o valor) entre los días con 1 y con 0; para una continua, r de Pearson entre restos (o valores) con N = n_Ef (B.5 y B.6) y el p de B.8.

## B.12 Inestabilidad (MSSD) — `Analisis.dinamica`

$$\text{MSSD}=\frac{1}{|P|}\sum_{(i,i+1)\in P}(x_{i+1}-x_i)^2,\qquad P=\{\text{pares de días consecutivos de calendario con dato}\},\ |P|\ge 3\qquad\qquad (B.12)$$

## B.13 Inercia — `Analisis.dinamica`

$$\text{inercia}=r\big(\{x_i\},\{x_{i+1}\}\big),\quad (i,i+1)\in P,\ |P|\ge 10\qquad\qquad (B.13)$$

Otros índices de `dinamica`: % días difíciles = 100 · #{x ≤ 2}/n; racha buena = máximo de días consecutivos con x ≥ 5; racha dura = con x ≤ 3; intradía = promedio de las DE diarias de los registros de ánimo (días con ≥ 2).

## B.14 z robusto — `Analisis.atipicos`

$$z_i=\frac{x_i-M}{1.4826\cdot \mathrm{MAD}},\qquad M=x_{(\lfloor n/2\rfloor)},\quad \mathrm{MAD}=|x-M|_{(\lfloor n/2\rfloor)}\qquad\qquad (B.14)$$

(posición ⌊n/2⌋ en base 0 de los datos ordenados: para n par, el central superior). Requiere n ≥ 20 y MAD > 0. Atípico si |z| ≥ 3 (2.8 en «Tu año»). En R: `(valor − median(valor)) / mad(valor)`, con la mediana promediada para n par.

## B.14a Distribución t de Student — `Estadistica.pT`, `Estadistica.cuantilT` (desde 10c)

p de dos colas de un valor t con gl grados de libertad (gl real > 0, acotado a 10⁶):

$$p_T(t,gl)=I_{\,gl/(gl+t^2)}\Big(\frac{gl}{2},\ \frac12\Big)\qquad\qquad (B.14a)$$

I_x(a, b) es la beta incompleta regularizada, calculada con la fracción continua de Lentz (hasta 400 términos, tolerancia 10⁻¹²; si x > (a + 1)/(a + b + 2) se usa I_x(a, b) = 1 − I₁₋ₓ(b, a)) y ln Γ con la aproximación de Lanczos (g = 7, 9 coeficientes). El cuantil t_p (p > 0.5) es el t ≥ 0 con p_T(t, gl) = 2(1 − p), buscado por bisección (100 pasos); para p < 0.5, t_p = −t₁₋ₚ. Comprobado: t₀.₉₇₅ = 12.706 (gl 1), 4.303 (2), 2.571 (5), 2.042 (30).

## B.15 Correlación mínima detectable (potencia)

$$|r_{\min}|=\tanh\!\Big(\frac{z_{1-\alpha/2}+z_{1-\beta}}{\sqrt{n_{Ef}-3}}\Big)\qquad\qquad (B.15)$$

Con α = 0.05 y potencia 1 − β = 0.80: z = 1.96 + 0.84. No está en el código; sirve para interpretar los umbrales de 14, 30 y 90 días (sección 6.21).

## B.16 Modelo múltiple — `Analisis.modelo`

Con las k columnas de predictores X y el objetivo y, todos estandarizados (media 0, DE 1) en los n días completos (n ≥ k + 10):

$$\hat\beta=(X^\top X)^{-1}X^\top y,\qquad s^2=\frac{\sum (y-X\hat\beta)^2}{n-k-1},\qquad EE(\hat\beta_j)=\sqrt{s^2\,[(X^\top X)^{-1}]_{jj}}\qquad\qquad (B.16)$$

$$IC_{95}(\beta_j)=\hat\beta_j\pm t_{0.975;\,n-k-1}\,EE(\hat\beta_j),\qquad R^2=1-\frac{\sum (y-X\hat\beta)^2}{\sum y^2},\qquad R^2_{aj}=1-(1-R^2)\frac{n-1}{n-k-1}\qquad\qquad (B.16·2)$$

(desde 10c; antes, 1.96 y solo R²). La pantalla muestra R²_aj, con 0 como mínimo.

Sin intercepto: con todo centrado valdría 0. Los grados n − k − 1 son los que da `lm` con intercepto en `vm_modelo` (R, sobre los mismos datos estandarizados), así que los β y sus intervalos coinciden. Inversa por Gauss-Jordan con pivoteo parcial; si |pivote| < 10⁻¹⁰, no hay modelo. Con `conAyer`, se agrega como predictor el objetivo del día de calendario anterior.

## B.17 Media móvil — `Analisis.movil`; `vm_movil` en R

$$m_d=\frac{1}{|V_d|}\sum_{f\in V_d}x_f,\qquad V_d=\{f\in[d-(w-1),\,d]\ \text{con dato}\},\quad |V_d|\ge\max(2,\lfloor w/3\rfloor)\qquad\qquad (B.17)$$

w = 7 o 30 días.

## B.18 Palabras — `Analisis.palabras`

Para cada palabra w (≥ 3 letras, fuera de `Analisis.VACIAS`: 167 escritas, 146 distintas sin acentos, que es como se comparan; la misma lista en `vm_palabras` desde 10d) presente en al menos 5 días con nota y valencia:

$$\text{dif}_w=\overline{\text{valencia}}_{\{\text{días con } w\}}-\overline{\text{valencia}}_{\{\text{días con nota y valencia}\}}\qquad\qquad (B.18)$$

## B.19 Cifras de «Tus números» — `TusNumeros.ultimos`

$$\bar x=\frac{1}{|U|}\sum_{f\in U}x_f,\qquad U=\text{los últimos 30 días con dato entre hoy}-365\text{ y hoy}\qquad\qquad (B.19)$$

Derivadas: km/año = x̄_km · 365; ejercicio/semana = Σ minutos de las sesiones en [última − 27, última] / 4 (desde 10c; antes x̄_ejercicio · 7); fuerza/semana = #entrenos en [último − 27, último] / 4; tazas de café (reportes) = mg de cafeína / 95.

## B.20 Cero o faltante — `Estadistica.ceroDesde` (desde 10c)

$$\text{ceroDesde}(F, B)=\{\,f\in B:\ f\ge\min F\,\}\qquad\qquad (B.20)$$

F = fechas de la fuente (gastos, impulsos, escalada, entrenamientos, sesiones del reloj); B = días en que la sección se pudo responder (días con diario; para el ejercicio, días con datos del reloj). En esos días «no hubo» = 0; en los demás, faltante. Las variables sociales usan B = días sociales (diario escrito o momento no privado con @; `Dinamicas.diasSociales`).

## B.21 Ventana de los cuestionarios — `Dinamicas.porDiaCuestionario` (desde 10c)

Con respuestas (f_k, v_k) ordenadas por fecha y ventana w (7 en WHO-5 y PSS-4, 30 en UCLA-3):

$$\text{esc}(d)=v_{k^*},\qquad k^*=\min\{\,k:\ 0\le f_k-d\le w-1\,\}\qquad\qquad (B.21)$$

(la primera respuesta que cubre el día d). Con w = 0 (TIPI), cada puntaje vale desde su fecha hasta el test siguiente, como mucho 365 días y no más allá de hoy.

## B.22 Criterio sellado de un experimento — `Experimentos.criterio` (desde 10f); `criterio` en `vm_experimento`

Con la diferencia d = media con la acción − media sin ella y su intervalo de 95 % [lo, hi] de B.11 (`diferenciaEnDias`), el sentido sellado s (+1 «debería subir», −1 «debería bajar») y el umbral u ≥ 0 (en las unidades de la medida; 0 si no se puso):

$$\text{se cumple}\iff d\cdot s\ \ge\ u\ \ \text{y}\ \ \begin{cases}\text{lo}>0&\text{si } s=+1\\ \text{hi}<0&\text{si } s=-1\end{cases}\qquad\qquad (B.22)$$

Es decir: la diferencia va en el sentido dicho, mide al menos el umbral y su intervalo no toca el cero **del lado de s**. Sin sentido sellado, sin diferencia (menos de 3 días medidos en una condición) o con el plan en curso (`provisional`), no hay veredicto (`null` en la app, `NA` en R): no se decide a medio camino. La app guarda el umbral en valor absoluto. **[Diseño propio]** la regla «sentido + umbral + intervalo que no toca el cero».

*Ejemplo inventado.* s = +1, u = 0.5: con d = 0.6 e intervalo [0.1, 1.1] se cumple; con d = 0.4 e intervalo [0.1, 0.7], no (no llega al umbral); con u = 0 y d = 0.6 pero intervalo [−0.1, 1.3], tampoco (toca el cero). Con s = −1 y u = 0.5, d = −0.8 e intervalo [−1.4, −0.2] se cumple. (Comprobado con la expresión de `vm_experimento` en R 4.3.3.)

**Huella.** `Experimentos.huella` = SHA-256 (en hexadecimal) del texto UTF-8 que arma `textoSellado`: pregunta, acción, medida, hipótesis, umbral, sentido, diseño, inicio, semanas, plan (como «1» y «0»), lavado y hora del sello, en ese orden y separados por el carácter U+001F. `intacto` = la huella guardada es igual a la que da lo que hay ahora. **[Validado]** que SHA-256 detecta cambios, en el sentido de que es una función hash criptográfica estándar: cambiar una sola letra da otra huella. No impide cambiar lo sellado; solo lo hace visible.

## B.23 Fuerza de referencia — `Macrofactor.fuerzaReferencia`, `Macrofactor.e1rm` (desde 10f)

Para cada serie efectiva de un día (no de calentamiento: el tipo no contiene «Warm»), con peso p > 0 kg y r repeticiones, 1 ≤ r ≤ 12, la 1RM (el peso que se podría levantar una sola vez) estimada con Epley:

$$\text{e1RM}=\operatorname{redondeo}_{0.1}\Big(p\,\big(1+\tfrac{r}{30}\big)\Big),\qquad \text{fuerza\_ref\_kg}=\max_{\text{series del ejercicio de referencia}}\ \text{e1RM}\qquad\qquad (B.23)$$

El ejercicio de referencia es el press inclinado con mancuernas (`Macrofactor.esReferencia`): el nombre, en minúsculas, sin acentos y con todo lo que no sea letra o número cambiado por «_» (`Texto.slug`), contiene «inclin», además «mancuern», «dumbbell» o «db» como palabra suelta, y además «press», «banc» o «bench». Vacío los días sin ese ejercicio. *Ejemplo inventado:* 3 series de 22 kg × 10, 24 kg × 8 y 24 kg × 6 dan 29.3, 30.4 y 28.8 kg; `fuerza_ref_kg` = 30.4. **[Aproximación]** la 1RM de Epley (su fuente original está [sin verificar], capítulo 5); **[Diseño propio]** reconocer el ejercicio por su nombre.

## B.24 Duración del día lógico con cambio de hora — `Fechas.cambioDeHora`, `ui/pantallas/Dia.kt` (desde 10f)

Con a = desfase con UTC (en horas) al empezar el día lógico (`desfaseH(inicioDiaLogico)`) y b = desfase en su último instante (`desfaseH(finDiaLogico − 1 ms)`):

$$\text{duración (h)} = 24-(b-a)\qquad\qquad (B.24)$$

Si a ≠ b, `cambio_horario` = 1 y la pantalla del Día muestra la duración. *Ejemplos inventados:* el día del cambio al horario de verano en Madrid (de UTC+1 a UTC+2) dura 23 h; el de regreso, 25 h; un viaje de la Ciudad de México a Madrid en verano (de UTC−6 a UTC+2), 24 − 8 = 16 h. `desfase_utc_h` de `vm_panel.csv` es otro valor: el desfase a mediodía (`desfaseH` de las 12:00 de la fecha).


# Apéndice C. Funciones de R (`vestigium.R`)

_El script `app/src/main/assets/vestigium.R` viaja con cada exportación. Se pone en la misma carpeta que los `vm_*.csv` y se ejecuta con `source("vestigium.R")`. Requiere tidyverse; algunas funciones piden además `nlme`, `igraph`, `stringi`, `DBI` y `RSQLite`. Versión descrita: la de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la que describe todo el paper; el script cambió en el commit `3794fec`, dentro de la entrega 10d (commit principal `035982c`), que alineó R con la app (P-30 a P-32): R analiza el mismo panel que la app, esconde las mismas parejas obvias y repite las correcciones que la entrega 10c (commit `b943ec7`) hizo en la app (t de Student, autocorrelación, tendencia en «Todo lo que se mueve»). Después cambió en la **entrega 10e** (commit `de7a699`; D.95, D.97 y D.106), sin cambio de esquema: el 0.3 de autocorrelación cuando no se puede estimar, `vm_cruce` y `vm_presion` como «Cruzar dos variables», `sin_semana` en `vm_relaciones` y `vm_factores`, `vm_dinamica` por mes sin cruzar meses, `vm_red(min_dias)`, los 8 navegadores de `vm_laptop` y la función nueva `vm_experimento`. La última vez cambió en la **entrega 10f** (D.110 y D.113), con el esquema 15: la función nueva `vm_hora_local` y el criterio sellado en `vm_experimento`. Lo que cambió en el módulo 10 se marca «desde 10a…10f» y está en el Apéndice D. **Cómo se comprobó:** en la sesión de la entrega 10d se corrió `vestigium.R` con R 4.3.3 sobre CSV sintéticos (todas las funciones de C.2 que cambiaron, más `vm_red`), y la prueba `ExportacionPrueba.kt` fija que `vm_factores` y `vm_relaciones` dan los mismos números que `Analisis.factores` y `Estadistica.relaciones` con los mismos datos inventados (diferencia < 10⁻⁹). R no corre en la compilación automática. Después se corrió con una exportación real de la v89: cargó y analizó sin errores, y así se vio que al panel extendido le faltaban 57 variables de la app (D.87, commit `d4e8e62`). Después, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Lo de 10e se corrió con CSV sintéticos en la sesión de esa entrega (`vm_experimento`, `vm_red(min_dias)`, `vm_cruce`, `sin_semana` y `vm_dinamica`, con un caso comprobado a mano para la prueba de aleatorización y para la red), y en la sesión que puso al día este apéndice se volvieron a correr con R 4.3.3 y datos inventados `.vm_cruce_resumen` (el n efectivo con 0.3), `.vm_sin_semana`, `vm_dinamica`, `vm_red` y `vm_experimento`; ninguna prueba JVM fija sus cifras, y las que fija `ExportacionPrueba` no cambiaron. Lo de 10f se comprobó en la sesión que puso al día este apéndice, con R 4.3.3 y datos inventados: el cuerpo de `vm_hora_local` y la expresión de `criterio`, sueltos (no se corrió `vm_experimento` entero con un `vm_experimentos.csv` de esquema 15). Para cada función: qué hace, cómo y en qué se diferencia de la app. Las fórmulas están en el Apéndice B._

## C.1 Carga

| Función u objeto | Qué hace |
|---|---|
| `vm_carpeta`, `vm_zona` | Carpeta de los CSV (la del script) y zona horaria (la del sistema; si es UTC, `America/Mexico_City`). Se pueden definir antes de `source()`. |
| `vm_dic` | Lee `vm_diccionario.csv` (archivo, columna, tipo, descripción y tabla; desde el esquema 15, también `desde_version` y `cambio`, como texto). `vm_describir` no muestra esas dos: se ven con `vm_dic`. Vacío = sin cambios desde la v84 (capítulo 2, sección 2.8). |
| `vm_leer(archivo)` | Lee un CSV con el tipo de cada columna según el diccionario: fechas, horas, números, lógicos (1 → `TRUE`), fecha-hora ISO 8601 convertida a `vm_zona`, enteros. Avisa si hubo celdas ilegibles. |
| `vm_fecha_hora(x)` | Convierte texto ISO con desfase a fecha-hora en `vm_zona`; agrega «:00» cuando Java omitió los segundos. Es el mismo instante, pero con la hora de reloj de `vm_zona`: si estuviste en otra zona, no es la hora que viviste. La hora local de entonces está en las columnas `*_h` y en las fechas, que la app calcula con la zona que tenía el teléfono (esquema 15), y en `vm_hora_local`. |
| `vm_hora_local(x)` | Desde 10f (D.110). La hora de una marca ISO tal como se tomó, con su propio desfase y sin pasarla a `vm_zona`, en decimal: horas + minutos/60 (`2026-10-16T08:42:10-06:00` → 8.70; `…T23:30:00+02:00` → 23.5). Lee las dos cifras después de la «T» (`str_match`): ignora los segundos y da `NA` si el texto no trae hora. Recibe el **texto** de la exportación, no la columna ya convertida por `vm_leer` (por ejemplo, `read_csv("vm_momentos.csv", col_types = cols(ts = "c"))`). **Diferencia con la app:** una marca en UTC («Z», de importaciones) da la hora UTC, mientras que la app la pasa a la zona que tenía el teléfono entonces (`Fechas.localDe`); tampoco suma 24 a la madrugada, como hacen las horas de dormir. |
| `vm_lista(x)`, `vm_pares(datos, columna)` | Separan columnas «a\|b\|c» y «clave:n\|clave:n». |
| `vm` y objetos sueltos | `vm` es la lista de las tablas de `vm_archivos`; cada una queda también como objeto (`panel`, `dias`, `momentos`…). Son 47 de las 53 tablas de `Exportar.tablas()`, más `vm_ubicaciones.csv`; no carga `vm_citas`, `vm_mundo`, `vm_cartas`, `vm_perfil`, `vm_politica`, `vm_youtube` ni `vm_fotos_catalogo` (se leen con `vm_leer("vm_citas.csv")`, con sus tipos). Desde el esquema 13 incluye `personas` (`vm_personas.csv`) y `fechas_propias` (`vm_fechas_propias.csv`); desde 10d, `panel_extendido` (`vm_panel_extendido.csv`), `variables` (`vm_variables.csv`) y `familias` (`vm_familias.csv`); desde el esquema 16, `ideas` (`vm_ideas.csv`) e `ideas_revisiones` (`vm_ideas_revisiones.csv`). |
| `panel` (enriquecido) | Desde 10d, **`vm_panel_extendido.csv`** (si la exportación lo trae; si no, `vm_panel.csv`), pasado por `.vm_enriquecer()`: quita filas sin fecha, ordena, **completa todos los días del calendario** (`complete()`) y agrega `dia_semana` (1 = lunes), `fin_de_semana`, `semana`, `mes`, `valencia_ayer` y `valencia_manana` (si hay `valencia`) y `sueno_h = sueno_min/60` (si hay `sueno_min`). Todas las funciones de análisis lo usan por omisión. `vm$panel` (de `vm_panel.csv`, enriquecido igual) queda por compatibilidad y lo siguen usando `vm_ritmo` y `vm_por_hora` (columna `dia_libre`), `vm_lugares` y `vm_presion`. |
| `.vm_variables(privadas = FALSE)` | Las variables de `vm_variables.csv`: las que la app cruza (`Estadistica.variables(dinamicas = true)`, con las familias dinámicas recortadas como en la app). Con `privadas = TRUE` suma el seguimiento privado, marcado en la columna `privada`, que la app nunca cruza. Devuelve NULL si la exportación no trae ese archivo. |
| `.vm_no_cruzar` | Columnas que describen el calendario o cuánto se registró, no una conducta, y que `vm_relaciones` y `vm_factores` no cruzan nunca (P-32): `fecha`, `semana`, `mes`, `dia_semana`, `fin_de_semana`, `dia_libre`, `festivo`, `valencia_ayer`, `valencia_manana`, `con_diario` y `n_muestreos`. **[Diseño propio]**. |
| `vm_describir(archivo)`, `vm_resumen()` | Diccionario de una tabla; resumen de filas, columnas y rangos de fechas. |
| `vm_leer_sqlite(ruta)` | Lee `vm_datos.sqlite` con los mismos tipos. |

: Tabla C.1. Carga

**El panel de R es el de la app (H-5-1, resuelto en 10d; P-30).** Hasta la entrega 10d el script analizaba `vm_panel.csv`, que arma `Exportar.filasPanel()` con otras reglas que `Estadistica.panel()`. Desde 10d:
- `vm_panel_extendido.csv` empieza con las columnas de `vm_panel.csv` y suma todas las del panel de la app que no estén ya (con los nombres de la app: `sueno_horas`, `distancia_km`, `hrv`…), las variables dinámicas y las columnas de las tablas diarias.
- En `vm_panel.csv`, cada columna que **comparte nombre** con el panel de la app lleva **el valor de la app** (`Exportar.comoLaApp`), con sus reglas de 0 y vacío (`ceroDesde`, P-15): por ejemplo, `gasto_total`, `ingreso_total`, `escalada` y `gimnasio` valen 0 desde el primer registro y en días con diario, y antes quedan vacíos; `ejercicio_min` vale 0 en días con datos del reloj y sin sesión. Se conserva el tipo de la columna (0/1, entero o número) para que R la lea igual que antes.
- `ultimo_uso_h` suma 24 **solo antes de las 5:00**, como el día lógico (antes, en el CSV, antes de las 12; H-9-4, P-16). Las horas de dormir (`sueno_inicio_h`, `carga_noche_inicio_h`) siguen sumando 24 antes de las 12.
- `canciones` cuenta, también en la app, solo escuchas de 30 s o más (sin duración registrada, cuenta), que es lo que dice el diccionario (H-9-3).
- **Once columnas** de `vm_panel.csv` tienen otro nombre en la app (`Exportar.EQUIVALENTES`) y no se renombraron, para no romper scripts viejos: `sueno_min` = `sueno_horas` (en minutos), `sueno_profundo_min` = `sueno_profundo`, `sueno_rem_min` = `sueno_rem`, `hrv_rmssd` = `hrv`, `km` = `distancia_km`, `valencia_muestreo` = `muestreo_valencia`, `luz_dia_min` = `luz_horas` (en minutos), `sol_min` = `sol_horas` (en minutos), `carga_noche_inicio_h` = `carga_noche_h`, `temp_piel_delta` = `temp_piel` y `ruido_db_medio` = `ruido_db`. Las ocho de la misma unidad toman el valor de la app; las tres en minutos salen de la misma fuente. El diccionario lo dice en las dos columnas.

Lo que sigue distinto: R completa el calendario con renglones vacíos (no cambia ningún cálculo, porque los vacíos no entran) y el panel extendido trae columnas que la app no cruza (las de las tablas diarias con prefijo, `app_min_*`…); por eso `vm_relaciones` y `vm_factores` se limitan por omisión a las variables de `vm_variables.csv`.

## C.2 Funciones de análisis

### `vm_tendencia(var = "valencia", datos = panel)`
Puntos diarios con medias móviles de 7 y 30 días (`vm_movil`, B.17). **Igual que la app** (`Analisis.movil`), salvo que la app solo dibuja el último año.

### `vm_movil(x, k)`
Media hacia atrás de k renglones; como el panel está completado a todos los días, k renglones = k días. Exige max(2, k %/% 3) valores. **Igual que la app.**

### `vm_semana(var, datos)`
Media, n y EE = DE/√n por día de la semana; desde 10d, solo los días de la semana con **5 valores o más** y el intervalo con **t de Student con n − 1 grados** (`qt(.975, n - 1)`; con 5 valores, ±2.776 EE); gráfica de barras. **Igual que `Analisis.porDiaSemana`** (antes R usaba ±1.96 EE sin mínimo).

### `vm_cruce(x, y, desfase = 0, datos, metodo = "pearson")`
Desde 10e (D.106; H-5-7, resuelto en parte) es **«Cruzar dos variables» de la app**. Toma x de cada día y la y de `desfase` días de calendario después (por fecha, `match(fecha + desfase, fecha)`); exige 5 días con los dos datos. Imprime una tabla de una fila (`.vm_cruce_resumen`) y dibuja la dispersión con la recta y la banda de `lm`; la tabla queda también en `attr(g, "resumen")`.
- `r`: Pearson, el coeficiente principal. Con 5 días o más, su intervalo de 95 % (`lo`, `hi`) y su `p` por Fisher con el n efectivo (`n_ef`, B.5), con la autocorrelación de **toda** la serie de x y de toda la de y (`.vm_ar1`; si no se puede estimar, 0.3), recortadas a [−0.5, 0.97]; como `Estadistica.correlacionEf`.
- `r_sin_tendencia`: Pearson entre los restos de ±10 días (`.vm_sin_tendencia`), con al menos 5 pares, sin intervalo (como la cifra «r sin tendencia» de la pantalla).
- `rho`: ρ de Spearman.
- `metodo` (`"pearson"`, `"spearman"` o `"kendall"`) solo elige qué coeficiente va primero en el subtítulo de la gráfica; con `"kendall"` se agrega τ.

**Diferencias que quedan:** no hay modo por semana; R da el `p` (la pantalla solo dice si el intervalo incluye el cero) y lo calcula con `pnorm` exacto. Hasta 10e era `cor.test` con **Spearman** por omisión, n bruto y los valores tal cual (después de 10c, la app ya daba otro intervalo). **[Aproximación]**, como en la app (sección 6.12).

### `vm_familias.csv`, `vm_obvia(a, b)`
Desde 10d, las parejas obvias vienen de la app (P-31; H-5-2, resuelto). La exportación escribe `vm_familias.csv` con tres columnas (`familia`, `variable`, `lado`), generado por `Estadistica.familias()` a partir de **las mismas reglas de `obvia()`** (sección 6.9.2): las familias de `FAMILIAS`, los cuatro grupos («Lectura del día», «Momentos», «Sentimientos», «Sueños»), lugares, suplementos, sentimientos con `registros_animo`, categorías de sueño con `sueno_*`, cada familia dinámica con sus totales (`AGREGADOS`), las familias obvias entre sí (`ENTRE_SI`), la misma persona (`per_X` y `pen_X`), el mismo cuestionario y las campañas. Se calcula sobre las variables fijas y **todas** las dinámicas del panel extendido.

- **Lado 0:** todas las variables de la familia son obvias entre sí (por ejemplo, los nutrientes fijos).
- **Lados 1 y 2:** cada variable del lado 1 es obvia con cada una del lado 2, pero no con las de su mismo lado. Así se escriben las reglas «en estrella»: `per_ana` es obvia con `personas` y con `palabras`, pero `personas` y `palabras` no lo son por esa regla; una campaña es obvia con lo que busca mover, pero eso que mueve no lo es entre sí.
- Los nombres de `vm_panel.csv` (las once equivalencias de C.1) entran en las mismas familias que su variable de la app, con el mismo lado, y cada pareja forma además una familia propia («igual:…»): `km` con `distancia_km` es obvia, y `km` con `pasos` también.

`.vm_pares_obvios(vars)` arma, una sola vez, las claves «a|b» de las parejas obvias entre `vars`; `vm_obvia(a, b)` dice si una pareja lo es. **Coincide con la app:** `ExportacionPrueba.familiasIgualQueObvia` comprueba, para 45 variables inventadas que tocan todas las reglas, que «comparten familia» da lo mismo que `obvia()` en todos los pares. Si la exportación no trae `vm_familias.csv` (exportaciones viejas o la resumida), `vm_obvia` usa la lista vieja escrita en el script: 20 conjuntos (`vm_familias`), las `m_*` y `n_momentos` entre sí y las campañas con `vm_campana_mueve`, mucho más corta que la de la app.

### `vm_relaciones(sin_tendencia = TRUE, obvias = FALSE, min_n = 14, ventana = 10, privadas = FALSE, variables = NULL, datos = panel, sin_semana = FALSE)`
Todas las parejas de las variables que la app cruza: las de `vm_variables.csv` (`.vm_variables(privadas)`), en su orden, que existan en el panel y sean numéricas o lógicas, sin las de `.vm_no_cruzar`. Con `privadas = TRUE` entra también el seguimiento privado (la app nunca lo cruza; su pantalla Privado deja las correlaciones para R). Con `variables = c(...)` se eligen otras columnas. Sin `vm_variables.csv`, todas las columnas numéricas o lógicas menos `.vm_no_cruzar`.
- `quitar()`: resto de ±`ventana` días con al menos 3 valores (B.3).
- `ac1()`: autocorrelación de días consecutivos con ≥ 10 pares, recortada a [−0.5, 0.97] (B.4). Con menos de 10 pares o sin variación, desde 10e, **0.3** (`.vm_ar1_sin_dato`, como `AR1_SIN_DATO` de la app; D.95, H-5-13); antes, 0. **[Diseño propio]**.
- `sin_semana = TRUE` (desde 10e; D.106, H-5-6 en parte): solo con `sin_tendencia = TRUE` (si no, avisa y no hace nada). A los restos de cada variable se les resta el promedio de sus restos en su día de la semana, de lunes a domingo, con los días con dato (`.vm_sin_semana`), para que dos cosas que solo cambian con el fin de semana no parezcan relacionadas. La autocorrelación y r usan esos restos; si una variable es lenta se decide con los restos de antes. La app no tiene esta opción. **[Aproximación]** (supone un ritmo semanal fijo).
- lenta: var > 0, ≥ `min_n` restos, var(resto)/var < 0.12.
- Por pareja: n ≥ `min_n`; 0/1 con ≥ 4 de cada valor; r de Pearson; `bruta`; n_ef acotado a [4, n] (B.5); intervalo de Fisher con n_ef; p = 2·pnorm(−|z/EE|).
- Obvias según `vm_familias.csv`; `q = p.adjust(p, "BH")`, `firme = q < .1`, orden por «seguro» y |r|.

**Coincide con `Estadistica.relaciones`:** el mismo algoritmo, cota por cota, sobre el mismo panel, con las mismas variables y la misma lista de obvias; con `obvias = FALSE` las obvias no se calculan y BH se hace solo entre las demás (como `q` en la app); con `obvias = TRUE`, BH incluye todas (como `qConObvias`). `ExportacionPrueba.relacionesComoR` fija que, con 80 días inventados, r, intervalo, p, q y el orden coinciden (< 10⁻⁹). **Diferencias que quedan:** no hay modo por semana; el p usa `pnorm` exacto y la app la aproximación de Abramowitz y Stegun (diferencia < 10⁻⁶); `sin_semana` solo existe en R. Que con una exportación real den las mismas primeras parejas es lo que proponía comprobar P-30, y **se comprobó**: el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026).

### `vm_factores(objetivo = "valencia", desfase = 0, sin_tendencia = TRUE, min_n = 14, ventana = 10, privadas = FALSE, variables = NULL, datos = panel, sin_semana = FALSE)`
Desde 10d es **«Todo lo que se mueve con algo» de la app** (`Analisis.factores`; P-17 y P-32; H-5-3, resuelto también en R). Las piezas comunes, escritas igual que en `ciencia/Estadistica.kt`:
- `.vm_sin_tendencia(f, x, ventana)`: cada día menos el promedio de sus ±`ventana` días de calendario con dato, si hay al menos 3 (`sinTendencia`, B.3).
- `.vm_ar1(f, x)`: autocorrelación de un día al siguiente, solo con días seguidos; NULL con menos de 10 pares o sin variación (`ar1`, B.4). En las correlaciones, NULL se vuelve 0.3 desde 10e (`.vm_ar1_sin_dato`; antes, 0); en `.vm_dif_dias`, no.
- `.vm_dif_dias(fa, ya, fb, yb)`: diferencia de medias entre dos grupos de días con t de Welch y el error estándar de cada grupo multiplicado por √((1 + ρ)/(1 − ρ)), con ρ corregido por sesgo, ρ + (1 + 4ρ)/n (Marriott y Pope, 1954); si un grupo no tiene 10 pares de días seguidos, usa la ρ de los dos juntos (cada uno centrado en su media), y si tampoco, 0; ρ acotada a [0, 0.9]; n efectivo por grupo n(1 − ρ)/(1 + ρ) (mínimo 2); grados de libertad de Welch con esos n, acotados a [1, n_a + n_b − 2]; intervalo con `qt(.975, gl)` y p con `pt` (`diferenciaEnDias`, B.11 y B.11a).

Con eso, contra el objetivo (con `sin_tendencia`, sus restos) del día `f + desfase` (por fecha de calendario):
- La variable necesita al menos 10 días con dato.
- **0/1** (todos sus valores 0 o 1, con ≥ 5 días de cada uno con objetivo): `.vm_dif_dias` entre los días con 1 y con 0. Columnas `efecto`, `lo`, `hi`, `n`, `n_con`, `n_sin`, `p`.
- **Continua** (con `sin_tendencia`, sus restos; ≥ `min_n` pares): r de Pearson; n efectivo n(1 − r_x r_y)/(1 + r_x r_y), con las autocorrelaciones de la variable y del objetivo recortadas a [−0.5, 0.97] y n_ef acotado a [mín(4, n), n]; intervalo de Fisher con n_ef; p normal. Columna `n_ef`.
- Omite el objetivo, las columnas de `.vm_no_cruzar` y las **parejas obvias con el objetivo** según `vm_familias.csv` (sin ese archivo, la familia del objetivo en `vm_familias` y `vm_obvia`). Por omisión, solo las variables de `vm_variables.csv`.
- BH sobre todas las revisadas, `firme = q < .1`, orden por p; une la descripción del diccionario (del panel extendido o de `vm_panel.csv`).
- `sin_semana = TRUE` (desde 10e, solo con `sin_tendencia = TRUE`): al objetivo y a cada variable continua, ya sin tendencia, se les resta el promedio de sus restos en su día de la semana (`.vm_sin_semana`, como en `vm_relaciones`); las 0/1 se comparan contra el objetivo ya ajustado. La app no tiene esta opción.

**Coincide con la app:** `ExportacionPrueba.factoresComoR` fija, con 80 días inventados y en las cuatro combinaciones (con y sin tendencia, con y sin desfase), que efecto, intervalo, n, p y q son los de `Analisis.factores` (< 10⁻⁹). Hasta 10d usaba los valores tal cual, Spearman con n bruto y `t.test` sin corrección por autocorrelación.

### `vm_modelo(objetivo, predictores = c("sueno_h", "pasos", "pantalla_min"), ayer = TRUE, datos)`
Agrega `.ayer` = el objetivo del día anterior (`lag` sobre el panel completado: día de calendario anterior), deja los días completos, estandariza todo (objetivo y predictores, con la media y la DE de esos mismos días) y ajusta `lm`. Exige n ≥ número de predictores + 10. Imprime `summary`.

**El intercepto.** `lm` estima un intercepto (la constante de la recta) y `Analisis.modelo` no («sin intercepto porque todo está centrado»). No se contradicen: como cada columna tiene media 0 en los días que entran, el mejor intercepto es exactamente 0 (en `summary` sale un número del orden de 10⁻¹⁶, con p ≈ 1: no se interpreta), y con él o sin él salen los mismos β, los mismos residuos y el mismo R². La app descuenta igual un grado de libertad por esa media (`gl = n − k − 1`, con k predictores contando `ayer`), que es el que usa `lm`. Por eso, con los mismos datos, **los β, sus errores estándar, sus intervalos (`confint(m)`) y el R² ajustado son los de `Analisis.modelo`** desde 10c: los dos usan la t con n − k − 1 grados (antes la app usaba 1.96). `summary` muestra el R² y el R² ajustado; la app, el ajustado. Si alguien ajustara en R sin intercepto (`y ~ 0 + …`), `lm` usaría n − k grados y los intervalos saldrían un poco más estrechos que en la app.

### `vm_modelo_ar1(objetivo, predictores, datos)`
Mismo modelo sin el día anterior, ajustado con `nlme::gls(…, correlation = corAR1(form = ~ t), method = "ML")`, con t = días desde el primero. Los errores siguen un proceso AR(1): el parecido entre dos días separados por h días es φ^h. Comprobado en el código de nlme (v3.1-171): si la covariable entera tiene saltos, `corAR1` se convierte internamente en `corARMA(p = 1, q = 0)` y usa la distancia real en días, así que **los días faltantes se respetan**. No existe en la app. Es la opción más defendible para inferencia sobre efectos de un día (Hamaker y Wichers, 2017), aunque no quita tendencias ni ciclos semanales.

### `vm_dinamica(var = "valencia", datos)`
Desde 10e (D.106; H-5-12, resuelto) sigue las reglas de `Analisis.dinamica`. Solo usa los días con dato de `var`; se detiene si hay menos de **7** («la app pide al menos 7»). En total y por mes devuelve n, media, DE (con 2 días o más), `pares` (pares de días de calendario seguidos con dato), MSSD con **3 pares** o más e inercia con **10 pares** o más (si no, NA). **Cada mes usa solo sus propios pares**: su último día no se empareja con el primero del siguiente. **Diferencias con la app:** sirve para cualquier variable; por mes da también la inercia (con la misma regla de 10 pares), que la app no da; no calcula los días muy difíciles, las rachas ni la DE intradía. Hasta 10e emparejaba con `lead` el último día de un mes con el primero del siguiente y no tenía mínimos.

### `vm_ritmo(dias = 56)`
Desde `vm_salud`: hora de dormir (madrugada + 24), despertar (+ 24), duración, punto medio; filtra 2 < duración < 16 h (la app incluye los extremos); desde 10c usa solo las noches de los últimos `dias` días hasta la última fecha de `vm_salud` (la app cuenta hasta hoy; `dias = Inf` para toda la historia); agrupa en noches antes de día libre y laborales: una noche es libre si el día en que despiertas tiene `dia_libre` en el panel (sábado, domingo o, si vives en México, festivo; `Fechas.esDiaLibre`), y si el panel no trae esa columna (exportaciones viejas), si cae en sábado o domingo; imprime el jet lag social (diferencia absoluta de los puntos medios) y la DE de la hora de dormir. **Como `Analisis.ritmo` con reloj** (misma ventana de 56 días y misma regla de día libre desde 10c); R no aproxima con el teléfono ni exige mínimos.

### `vm_palabras(min_dias = 5)`
Tokens de 3+ letras de las notas (en minúsculas), sin las **mismas palabras vacías que la app** (`Analisis.VACIAS`: la misma lista, 169 escritas, 167 distintas, 146 distintas ya sin acentos), comparadas **sin acentos** (se quitan con `stringi`, igual que `Analisis.normal`); un conteo por día; para cada palabra con ≥ `min_dias` días con valencia: media y diferencia con la valencia media de los **días con nota**. **Igual que `Analisis.palabras`** desde 10d (P-32; la parte de R de H-5-10); `ExportacionPrueba.palabrasVaciasComoR` comprueba que las dos listas son la misma. Diferencias que quedan: R devuelve todas las palabras ordenadas y la app solo los 15 extremos de cada lado; ninguna de las dos da intervalo ni encoge las diferencias con pocos días (la otra mitad de H-5-10 sigue abierta).

### `vm_atipicos(vars = c("valencia", "sueno_horas", "pasos", "pantalla_min", "desbloqueos", "gasto_total", "notificaciones", "fotos", "distancia_km", "min_hablado", "wa_mios", "musica_min", "fc_reposo", "hrv", "palabras"), z = 3, datos)`
z robusto con `median` y `mad` (constante 1.4826) por variable; devuelve los días con |z| ≥ 3, del más reciente al más viejo. Desde 10d revisa **las mismas 15 variables que `Analisis.atipicos`**, con los nombres de la app (antes eran 8, con `sueno_h` y `km`). **Diferencias que quedan:** sin mínimo de 20 días ni límite de un año; mediana promediada para n par (la app toma la superior); R da una fila por variable y día, la app un día con sus razones.

### `vm_experimento(id, datos = panel, hoy = max(datos$fecha, na.rm = TRUE))`
Desde 10e (D.97; H-7-11, resuelto). Es **`Experimentos.resultado` de la app** (capítulo 8) para el experimento `id` de `vm_experimentos.csv`, más una prueba que la app no tiene.

**Qué recibe.** `id`, el del experimento (si no existe, dice cuáles hay); `datos`, el panel; `hoy`, el día a medias que no cuenta (por omisión, el último del panel, que suele ser el de la exportación). Del CSV lee `inicio`, `semanas`, `lavado_dias`, `plan` (una semana por posición, 1 = con la acción), `fin`, `resultado` (la variable), `diseno`, `hecho` («¿Lo hiciste hoy?», `fecha:1` o `fecha:0`) y `plantilla`; desde el esquema 15, también `sentido`, `umbral` e `intacto` (si el CSV no los trae, quedan en `NA`).

**Qué calcula, como la app.**
- **Días que cuentan:** los del plan hasta `hoy` (sin incluirlo) y hasta `fin`, con dato de la variable, sin los de lavado (los primeros `lavado_dias` tras cada cambio de condición y tras empezar con la acción). Cada día va a la condición que tocaba según el plan, la hayas hecho o no (intención de tratar).
- **Diferencia** (con − sin) con `.vm_dif_dias`: t de Welch y error estándar corregido por autocorrelación (B.11, B.11a), con al menos 3 días medidos en cada condición.
- **g de Hedges** = d · (1 − 3 / (4 (n₁ + n₂) − 9)), con d = diferencia / DE agrupada (capítulo 8, sección 8.6.5).
- **Tendencia** (`.vm_tendencia`): pendiente por día de valor ~ día + condición; «clara» si |t| > `qt(.975, n − 3)` (n − 2 si la condición no varía); nada con menos de 8 días.
- **Cumplimiento:** por condición, días, respondidos, hechos y proporción; sin respuesta no es «no». En la plantilla «creatina», un día sin respuesta toma `supl_creatina_tomado > 0`, como la app desde 10e (D.96). `cociente` = diferencia / (cumplimiento con − cumplimiento sin) si esa resta es de 0.2 o más.
- **Provisional** si el experimento sigue activo, sin `fin` y `hoy` no pasó del último día del plan.
- **Criterio sellado** (desde 10f, D.113; H-7-10): `criterio` = `diferencia · sentido ≥ umbral` (0 si no hay umbral) y el intervalo no toca el cero del lado del sentido (`lo > 0` si sube, `hi < 0` si baja), como `Experimentos.criterio` (fórmula B.22). `NA` si no hay sentido, si no hay diferencia o si es provisional: no se decide a medio camino. **[Diseño propio]**. `intacto` se copia del CSV, donde la app ya comparó la huella SHA-256 con lo sellado; R no recalcula la huella.

**La prueba de aleatorización.** Con k = ⌊semanas / 2⌋ pares de semanas, cada uno «SÍ no» o «no SÍ», arma los 2^k planes que el sorteo por pares pudo dar (el doble si sobra una semana, que puede ir de cualquier lado). A cada plan le aplica su propio lavado y calcula la diferencia de medias simple con los mismos datos; los que dejan menos de 3 días medidos en una condición no entran. `p_aleatorizacion` es la fracción de planes con |diferencia| ≥ |diferencia observada|: **exacta** (recorre todos los planes) y de **dos colas**. No supone que los días sean independientes: solo que el sorteo fue real. **[Validado]** como método (Kratochwill y Levin, 2010; Tanious y Onghena, 2019).
- **El p más chico posible.** El plan espejo (cada «SÍ» por «no») siempre es posible y, **sin lavado**, da exactamente la diferencia contraria, así que el mínimo real es **2 / planes**: con 12 semanas, 2/64 ≈ 0.031; con 8, 0.125. Con lavado el espejo cuenta otros días y puede no empatar. Se comprobó corriendo la función con datos inventados (4, 5 y 6 semanas, con y sin lavado): sin lavado, la distribución sale siempre simétrica. Por eso la nota que imprime la función calcula el mínimo con los datos (la proporción de planes con la diferencia más grande), en vez de dar 1 / planes.
- **Ejemplo.** 4 semanas sin lavado, plan «SÍ no no SÍ», 7 días medidos por semana. Los 4 planes dan +1.29 (el observado), +0.14, −0.14 y −1.29: dos tienen |diferencia| ≥ 1.29, *p* = 2/4 = 0.5, aunque con esos datos el *p* de Welch sea del orden de 10⁻⁷. Con 4 semanas la prueba nunca puede bajar de 0.5.
- **Sin prueba** (`NA`, con una nota): en «antes y después», porque no hubo sorteo, y si el plan guardado no es uno de los del sorteo por pares (experimentos sellados antes de la 10c).

**Qué devuelve.** Imprime una línea con el nombre, el diseño, la variable y los días por condición («PROVISIONAL» si lo es), la diferencia con su intervalo, su *p* y la *g*, la nota de la prueba y su *p*; si hay sentido sellado, una línea más («Criterio sellado: hacia arriba al menos 0.5, con el intervalo lejos del cero · se cumple», «no se cumple» o «se decide al terminar»), y un aviso si `intacto` es 0 («lo sellado cambió después del sello»). Devuelve, invisible, una lista: `id`, `nombre`, `resultado`, `diseno`, `plan`, `provisional`, `n_con`, `n_sin`, `media_con`, `media_sin`, `diferencia`, `lo`, `hi`, `p`, `g`, `tendencia`, `tendencia_clara`, `cumplimiento` (tabla), `cociente`, `posible_si` y `posible_no` (días de cada condición según el plan hasta su último día), `planes`, `p_aleatorizacion`, `distribucion` (la diferencia de cada plan), `nota` y, desde 10f, `sentido`, `umbral`, `intacto` y `criterio`. **Diferencias con la app:** la prueba de aleatorización solo existe aquí; ninguna prueba JVM fija que las cifras coincidan con `Experimentos.resultado` (se comprobó con CSV sintéticos, Apéndice D).

## C.3 Funciones descriptivas y temáticas

| Función | Qué hace | Relación con la app |
|---|---|---|
| `vm_totales(desde, hasta, datos)` | Para 43 columnas sumables (las que existan) (desde el esquema 13, también `avisos_planeados`, `avisos_mostrados` y `avisos_respondidos`): total, días con dato, primera y última fecha con valor > 0, promedio por día con dato. | Como la pantalla Totales. Desde 10d corre sobre el panel extendido, que trae las mismas columnas de `vm_panel.csv` (ya con los valores de la app). |
| `vm_buscar(texto, desde, hasta)` | Busca sin acentos en alimentos, personas («@»), temas («#»), artistas, canciones (≥ 30 s), lugares, ejercicios (sin calentamiento), canales, búsquedas, gastos, libros y chats. | Como el buscador de Totales (sin los sinónimos español-inglés de `Totales.SINONIMOS`). |
| `vm_fuerza(n = 6)` | 1RM estimado máximo por sesión de los n ejercicios más repetidos. | Capítulo 5. |
| `vm_mapa_animo()` | Registros de ánimo (`es_animo`) con coordenadas y su propia valencia, coloreados por ella. Desde el módulo 10a excluye impulso, recaída y libido y ya no rellena con la valencia del diario. | H-2-15 (parte de R) y H-9-12, resueltos. |
| `vm_por_hora(var)` | Promedio por hora (0–23) de una columna de `vm_horas.csv`, en días laborables y en días libres. Desde 10f, cada hora de `vm_horas.csv` es la hora local de entonces (la app la calcula con la zona que tenía el teléfono). Desde 10d, un día es libre si tiene `dia_libre` en `vm$panel` (sábado, domingo o, si vives en México, festivo: la regla de la app, `Fechas.esDiaLibre`); si el panel no trae esa columna (exportaciones viejas), si cae en sábado o domingo. | No hay equivalente en pantalla. |
| `vm_cruce_horas(x, y, desfase)` | Desde 10e (H-5-7), r de Pearson y ρ de Spearman, cada uno con su p de `cor.test`, entre dos columnas de `vm_horas.csv` en la misma hora o `desfase` horas después; exige 10 horas. Antes, solo Spearman. | La app no cruza horas, así que no hay n efectivo que copiar: ignora que horas consecutivas se parecen mucho más que días consecutivos, y su p es muy optimista (la salida lo dice). |
| `vm_sustancias()` | Registros, días, nivel máximo medio, horas y cruda por sustancia; cruda contra bebidas. | Descriptiva. |
| `vm_lugares()` | Horas por tipo de lugar por semana; valencia media de los registros de ánimo por tipo de lugar. | Descriptiva. |
| `vm_emociones(desde = NULL, hasta = NULL)` (desde el esquema 18, D.146) | De `vm_momentos.csv`, solo registros de ánimo (`es_animo`): por emoción, cuántos registros la nombran, en cuántos le pusiste intensidad, la intensidad media (1 a 3), cuántas veces fue «mucho» y la valencia media de esos registros; lo mismo por pensamiento y por compañía (`con_quien`); y una gráfica de barras de las 12 emociones más nombradas, coloreada por intensidad media. «No sé» no cuenta como emoción; un registro con varias emociones cuenta en cada una (como `n_*` en el panel). Lee las listas con `vm_lista` y las intensidades con `vm_pares`. | Descriptiva: la valencia media por emoción no es un efecto de la emoción (los registros con varias emociones se repiten en cada una). |
| `vm_presion()` | Desde 10e (H-5-7), el mismo resumen que `vm_cruce` (`.vm_cruce_resumen`) entre `cambio_presion_3h` y la valencia, sobre `vm$panel`: r de Pearson con intervalo y p por el n efectivo, r sin tendencia y ρ de Spearman; exige 5 días. Antes, solo Spearman con n bruto. La gráfica sigue con una curva `loess`. | Como «Cruzar dos variables» con esas dos variables. |
| `vm_retraso(tabla, umbral_min = 60)` | Desde el esquema 14: minutos entre cuándo pasó y cuándo se anotó cada registro (`retraso_min`), si se anotó en otro día lógico (`otro_dia`) y si fue `tarde` (más del umbral u otro día). Sirve para momentos, sustancias, tramos, suplementos, gastos e ingresos. | Descriptiva. El umbral de 60 min es **[Diseño propio]**; los registros de antes del esquema 14 quedan con NA. |
| `vm_red(declaradas = FALSE, min_juntos = 2, min_dias = 3)` | Red personal con igraph: densidad, grupos, aisladas, restricción de Burt, grado, composición. Desde 10c, la composición y el color usan la columna `grupo` de `vm_red_nodos.csv` (familia, pareja, amistad, trabajo…), como la Red de la app; si la exportación no la trae, la relación tal cual. Desde 10d (P-32; H-6-7 a y b, H-9-23): un par declarado «no se conocen» **nunca** es línea, aunque aparezcan juntas `min_juntos` días; `componentes` cuenta solo los grupos con al menos una línea (`sum(csize > 1)`) y las personas sin ninguna van aparte en `aisladas`; sin ningún par (o sin `vm_red_vinculos.csv`) ya no falla: da una red sin líneas. Desde 10e (D.106; H-6-8): solo entran las personas mencionadas al menos `min_dias` días distintos (`dias_mencion`: con @ en el diario o en los momentos no privados), y sus líneas se van con ellas; quien tiene alguna línea dicha en Tu gente (que no sea «no se conocen») entra siempre. Así alguien nombrado una vez no baja la densidad ni la restricción. Dice cuántas quedan; `min_dias = 0` usa a todas, como antes. El 3 es **[Diseño propio]**. | Capítulo 7. Igual que `RedPersonas.componentes`. Lo que sigue distinto (H-6-7 c y d, documentado en el diccionario): la exportación trae hasta 400 personas y el `grado` del CSV cuenta pares con 1 día o más juntas; la Red de la app dibuja 45 y pide 2 días; la app no tiene el mínimo de `min_dias` (en la app, H-6-8 queda solo documentado, capítulo 7). |
| `vm_laptop()` | Horas por categoría de Cold Turkey por semana (apps y sitios, sin las apps de navegador). | Descriptiva. Desde 10e (D.106; H-14-6, resuelto) quita los mismos 8 navegadores que la app (`.vm_navegadores` = `ColdTurkey.NAVEGADORES`: chrome, msedge, firefox, opera, brave, vivaldi, safari y arc), cuyos minutos ya están repartidos en los sitios; la comparación es exacta porque la exportación trae el nombre ya normalizado (minúsculas, sin espacios a los lados ni «.exe»). Hasta 10e quitaba solo los 5 primeros, y los minutos de vivaldi, safari y arc contaban dos veces. |
| `vm_guardar_graficas(carpeta)` | Guarda en PNG tendencia y semana de la valencia, ritmo y mapa de ánimo. | — |

: Tabla C.2. Funciones descriptivas y temáticas

## C.4 Qué usar en R para conclusiones más firmes

Con lo que ya trae el script, lo más sólido para una pregunta concreta («¿el sueño se asocia con mi ánimo?») es `vm_modelo_ar1()`, que respeta que los días se parecen y los días faltantes. Para explorar «todo con todo», `vm_relaciones()` y `vm_factores()` reproducen desde 10d lo que muestra la app, sobre el mismo panel y con las mismas parejas obvias, así que sirven para verificarla; con `privadas = TRUE` se cruza también el seguimiento privado, que la app no cruza en ninguna pantalla. Para el ciclo semanal (H-5-6), desde 10e `vm_relaciones(sin_semana = TRUE)` y `vm_factores(sin_semana = TRUE)` restan a los restos el promedio de cada día de la semana; en un modelo puede controlarse agregando `fin_de_semana` o `factor(dia_semana)` como predictor. Para un experimento, `vm_experimento(id)` da la prueba de aleatorización, la única que no supone días independientes, con el límite de su *p* mínimo.


# Apéndice D. Cambios metodológicos aplicados

_Cada cambio del módulo 10 con su motivo (hallazgo y propuesta de `revision_propuestas.md`) y la versión en que entró. El módulo 10 se hace en cuatro entregas (10a privacidad; 10b datos que no se guardaban; 10c cálculos; 10d R y exportación). Este apéndice crece con cada una._

## Entrega 10a · Privacidad (commit `a67bad0`; versión v85)

Decisiones del autor (27 oct 2026):
- D-1 y D-2, ampliadas: todo lo privado se ve solo en una pantalla de Privado y viaja a R.
- Los análisis de esa pantalla son los que ya tenía más una tendencia simple; las correlaciones, solo en R.
- El impulso deja de ser medida de los experimentos.
- Con datos privados, ningún respaldo sale del teléfono sin contraseña.

No cambia lo que se guarda: `SCHEMA_V` sigue en 12.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.1 | Regla única de lo privado. Son privados los momentos de impulso, recaída y libido; en el día, la intensidad del impulso, la recaída, los detonantes y los medicamentos; y el hábito ligado a las recaídas. También las variables `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `n_impulso`, `medicamentos_n`, `med_*`, `det_*` y `hab_<ligado>` | `data/Privado.kt` (nuevo); `Momento.privado` en `data/Modelo.kt` | Había tres nociones de «privado» que no coincidían con la regla declarada (H-9-39) |
| D.2 | Lo privado no aparece fuera de la pantalla Privado. Afecta a Día, Hoy, momentos, cinta, diarios, libreta, calendario, semana, año, totales, mapa, widgets, reportes, «Tu año», logros (también los propios), metas, campañas, experimentos, Constelación, Red, Tu gente, las sugerencias de @ y #, «Tus temas» y los análisis. Vale también con el interruptor encendido | Unos 30 archivos de `ui/`, `ciencia/`, `data/`, `reportes/` y `widget/` | H-8-1, H-8-2, H-8-3, H-8-16, H-6-1, H-9-1, H-9-38 (P-01 a P-03 y P-34) |
| D.3 | `Estadistica.variables()` excluye las variables privadas, salvo con `privadas = true`, que solo usa el diccionario. El panel las calcula siempre, sin depender del interruptor, para la exportación | `ciencia/Estadistica.kt`, `ciencia/Dinamicas.kt` | Que no lleguen a ningún análisis de la app, pero sí a R completas |
| D.4 | `per_`, `pen_` y `tema_` ya no leen momentos privados, y sus días ya no cuentan como «días con diario» para los ceros. `momentos_intensidad` y `n_madrugada`…`n_noche` ya no incluyen impulsos | `ciencia/Dinamicas.kt`, `ciencia/Estadistica.kt` | H-6-1, H-9-27. **Cambia valores**: algunas columnas `per_`/`tema_` pueden desaparecer del panel extendido |
| D.5 | Pantalla Privado (antes «Impulsos y recaídas»), siempre accesible desde Más. Tiene: registro por día con medicamentos y «lo de siempre»; con el interruptor, también impulso, recaída, detonantes y hábito ligado; el resumen que ya existía; una tendencia de 12 meses (recaídas, impulsos, días con medicamentos); los días que se tomó cada medicamento; y el registro editable de los últimos 30 momentos privados. Desde Día, el enlace «privado de este día» abre la fecha | `ui/pantallas/Recaidas.kt`, `ui/pantallas/Dia.kt`, `Mas.kt`, `Pantallas.kt`, `Sistema.kt` | Decisión del autor |
| D.6 | Con datos privados y sin contraseña, no se escribe en la carpeta, no se sube a Drive y no se puede compartir el respaldo completo. Hay aviso en Hoy y una notificación por semana. La exportación para R (a mano) sigue en claro, porque R necesita leerla, y la pantalla lo advierte | `data/Respaldos.kt` (`bloqueado`), `data/Drive.kt`, `avisos/Trabajo.kt`, `avisos/Avisos.kt`, `ui/pantallas/Respaldo.kt`, `Hoy.kt`, `Sistema.kt` | H-8-5 (en parte) |
| D.7 | Los reportes que se guardan solos (lunes y día 1) van en modo discreto y sin la frase del mejor momento | `reportes/Reportes.kt` | H-8-4 (en parte; P-06) |
| D.8 | Las coordenadas de un momento privado no se usan para pedir el clima | `sensores/Entorno.kt` | H-9-6 (P-04) |
| D.9 | Elegir «ninguno» como hábito ligado vuelve a funcionar: antes caía en la búsqueda por palabras clave | `data/Recaidas.kt` | Error encontrado al aplicar D.1 |
| D.10 | `vm_mapa_animo()` usa solo registros de ánimo con su propia valencia, sin seguimiento privado ni la valencia del diario prestada | `assets/vestigium.R` | H-2-15 (parte de R), H-9-12 (P-05) |
| D.11 | Prueba JVM de la regla | `app/src/test/.../PrivadoPrueba.kt` | Que una variable o un momento privado no vuelva a colarse sin que falle una prueba |

: Tabla D.1. Entrega 10a · Privacidad (commit a67bad0; versión v85)

**Efectos que conviene saber al leer los datos:**
- Con el interruptor apagado, antes no existían en el panel `n_recaida`, `recaida_min`, `libido_max`, `n_libido` ni `det_*`. Ahora existen si hay datos: el panel exportado puede traer columnas nuevas.
- La intensidad del impulso ya no está en el diario de la noche, sino en la pantalla Privado. Es posible que se anote menos días (capítulo 3, sección 3.16).
- Si no hay contraseña y existen datos privados (por ejemplo, un solo medicamento anotado), los respaldos automáticos quedan **en pausa** hasta que se ponga una.

**Capítulos actualizados:** 3 (secciones 3.7 y 3.16), 6 (secciones 6.3 y 6.6, familias dinámicas), 7 (secciones 7.2 y 7.13), 9 (secciones 9.3.1 y 9.4), 10 (secciones 10.1, 10.3, 10.5 y 10.10, y la Figura 10.1) y los apéndices A y C.

## Entrega 10b · Datos que no se guardaban (esquema 13)

Decisiones del autor (27 oct 2026):
- D-3: sí. La pantalla y los pasos pasan al día lógico.
- Días especiales (I-6): sí, con fechas de fallecimiento y con aviso de cumpleaños.
- Edad de las personas (I-7): se ve en Tu gente y va a R. No entra en los análisis de la app.

**`SCHEMA_V` sube de 12 a 13**, una sola vez para toda la entrega. Los respaldos viejos se siguen leyendo, porque todos los campos nuevos tienen valor por omisión. Al revés, una app con esquema 12 no puede restaurar un respaldo del 13: esta entrega es justo la que empieza a rechazarlo.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.12 | Los documentos que no se pueden leer se cuentan por colección y se muestran en Estado de los datos y en Hoy. Una configuración ilegible se copia a `config_ilegible` antes de seguir con una vacía | `data/Decodificar.kt` (nuevo), `data/Repo.kt` (`leer`, `ilegibles`, `configIlegible`), `ui/pantallas/EstadoDatos.kt`, `Hoy.kt` | H-1-3, H-9-2 (P-07) |
| D.13 | Restaurar: un respaldo con esquema más nuevo no se restaura. Gastos e ingresos repetidos no se pisan, ni en la base ni en memoria. Un experimento terminado no se reactiva y se juntan sus respuestas. «Verificar» dice solo «se lee completo» y compara respaldo y teléfono | `data/Importar.kt` (`esquemaFuturo`), `data/Fusion.kt` (nuevo), `data/Repo.kt`, `data/Respaldos.kt` | H-8-12, H-8-13 (en parte), H-9-28, H-9-33 (P-08) |
| D.14 | Drive no reemplaza la copia por una con menos del 90 % de días o momentos que la última (si esa tenía 20 o más): la guarda como `vestigium_revisar` y avisa. Subir a mano la reemplaza. En Drive y en la carpeta, los archivos en claro se borran solo después de escribir el cifrado | `data/Drive.kt`, `data/Respaldos.kt` (`empobrecido`, `aCarpeta`), `Config.drive_conteo`, `avisos/Avisos.kt` (`driveEmpobrecido`) | H-8-14 (en parte), H-9-37 (P-09) |
| D.15 | Colección nueva `avisos` (`AvisosDia`): planeados, vencidos y mostrados por día. Panel: `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos`, `tasa_respuesta` (familia obvia). `n_muestreos` cuenta todos los respondidos. La tarjeta de avisos sin contestar abre el aviso más antiguo | `data/AvisosAnimo.kt` (nuevo), `avisos/Trabajo.kt`, `data/Repo.kt`, `ciencia/Estadistica.kt`, `data/Exportar.kt`, `Hoy.kt`, `vestigium.R` | H-2-3, H-9-9, H-9-10, H-9-11 (P-10) |
| D.16 | Campañas: `aceptada` y `abandonada`. Abandonar ya no borra. No cuentan los días antes de aceptar. Un hábito desactivado no anula sus campañas. Panel: `campana_<reto>` y `campanas_n`, obvias con lo que cada reto mueve | `ciencia/Campanas.kt` (`tramo`, `diasActivos`, `MUEVE`, `obvia`), `ui/pantallas/Campanas.kt`, `ciencia/Estadistica.kt`, `data/Exportar.kt`, `data/Diccionario.kt`, `vestigium.R` (`vm_campana_mueve`) | H-8-7, H-9-34, H-9-35, H-9-36 (P-11) |
| D.17 | Experimentos: `fin` (al terminar) y `hecho` («¿Lo hiciste hoy?», en las dos condiciones). El resultado corta en `fin`, da el cumplimiento por condición y un cociente secundario. El aviso sale en todos los días del experimento hasta hoy, también ya terminado, y dice cuándo es lavado | `data/Lote3.kt` (`ultimoDia`, `cubre`, `enCurso`, `Cumplimiento`), `ui/pantallas/Dia.kt` (`AvisoExperimento`), `ui/pantallas/Lote3.kt`, `Explorar.kt`, `data/Exportar.kt` | H-7-2, H-7-8, H-9-29 (P-12, I-3) |
| D.18 | `Dia.ts_valencia`, y `retroactivo` según el día lógico de esa hora | `data/Reglas.kt` (nuevo), `data/Repo.kt`, `data/Exportar.kt` | H-2-2 (P-13) |
| D.19 | Pantalla y pasos por día lógico (05:00 a 05:00), con `PantallaDia.ventana` y `SaludDia.ventana_pasos`. La cinta y `vm_horas` ponen las horas 0 a 4 en la madrugada siguiente | `sensores/Pantalla.kt`, `sensores/Salud.kt`, `data/Fechas.kt`, `ui/pantallas/Cinta.kt`, `data/Exportar.kt` | H-3-1 (en parte), H-3-2 (P-16 ii) |
| D.20 | Días especiales: `Persona.nacimiento` y `fallecimiento` (año opcional) y `Config.fechas_propias`. Aviso en la mañana (se puede apagar), aviso en el Día y sección en Tu gente con los próximos. Panel: `dia_especial`; exportación: `dia_especial_tipo`, `vm_personas.csv` con la edad y `vm_fechas_propias.csv` | `data/Especiales.kt` (nuevo), `data/Personas.kt`, `ui/pantallas/Gente.kt`, `avisos/Trabajo.kt`, `avisos/Avisos.kt`, `ui/pantallas/Dia.kt`, `ciencia/Estadistica.kt`, `data/Exportar.kt`, `vestigium.R` | I-6, I-7 |
| D.21 | Pruebas JVM: decodificación, esquema futuro, fusión de gastos y experimentos, Drive empobrecido, cifras de avisos, campañas por día, experimento terminado con cumplimiento, valencia retroactiva y días especiales | `app/src/test/.../IntegridadPrueba.kt`, `DatosNuevosPrueba.kt` | Que cada regla falle en una prueba si se rompe |

: Tabla D.2. Entrega 10b · Datos que no se guardaban (esquema 13)

**Efectos que conviene saber al leer los datos:**
- **La serie de pantalla y pasos se parte** el día que se instala esta versión. Hay que analizar por tramo (`ventana_pantalla`, `ventana_pasos`). En el tramo nuevo, lo de la madrugada cuenta para el día anterior.
- **`n_muestreos` cambia de definición**: ahora cuenta también los avisos respondidos sin valencia, así que puede subir en los días viejos.
- **`retroactivo` puede cambiar** en días ya guardados: si se vuelve a guardar la valencia de un día viejo, su hora decide.
- **Lo que no se puede recuperar.** Las variables `avisos_*` y `tasa_respuesta` solo existen desde esta versión. Las campañas de antes no tienen fecha de aceptación (cuentan la semana completa), y los experimentos terminados antes no tienen `fin`.
- **Respaldos.** La v85 y las anteriores no revisan el esquema: si restauran un respaldo de esta versión, pierden en silencio los campos nuevos. Desde esta versión, la app rechaza cualquier respaldo de un esquema más nuevo que el suyo.

**Capítulos actualizados:**
- capítulo 2: secciones 2.3.2, 2.4.2, 2.4.3, 2.5 y 2.9;
- capítulo 3: secciones 3.5.3, 3.7.4 y 3.15.2;
- capítulo 4: secciones 4.3 y 4.4.1;
- capítulo 6: sección 6.9.2;
- capítulo 8: secciones 8.3.3, 8.6.1 y 8.7, y resúmenes;
- capítulo 9: sección 9.5 y resúmenes;
- capítulo 10: secciones 10.5, 10.6, 10.7, 10.9 y resúmenes;
- los apéndices A (variables, tablas y la sección A.4 nueva) y C.

## Revisión de navegación · Entregas N0 y NE (esquema 14)

Salen de `docs/navegacion/revision_navegacion.md`. Decisiones del autor del 27 oct 2026: aprobó las 30 propuestas y el orden de entregas, y contestó las preguntas P-1 a P-11 (§ 13 del informe).

**`SCHEMA_V` sube de 13 a 14**, una sola vez para todos los campos nuevos de la revisión. Los respaldos viejos se siguen leyendo, porque todos los campos nuevos tienen valor por omisión. Una app con esquema 13 rechaza un respaldo del 14 (`Importar.esquemaFuturo`).

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.22 | Corregir una recaída: cambiar solo el día, con la misma hora, ya la mueve. Borrarla desde el diálogo quita la marca del día si era la única | `ui/pantallas/Momentos.kt`, `data/Recaidas.kt` (`alBorrar`) | H-NAV-6 (N0) |
| D.23 | «Volver a lo del reloj» pide confirmación, avisa si el reloj no tiene esa noche y se puede deshacer. Restaurar un respaldo no pisa el sueño corregido a mano | `ui/EditarSueno.kt`, `data/Fusion.kt` (`salud`), `data/Importar.kt` | H-NAV-5 (N0) |
| D.24 | «Día difícil» se ve en la hoja de la noche y se puede quitar | `ui/pantallas/Dia.kt` | H-NAV-8 (N0) |
| D.25 | Cuándo se anotó (`ts_creado`) en momentos, gastos, ingresos, tomas, consumos y tramos, y cuándo se corrigió (`ts_editado`) en momentos, gastos e ingresos. La marca se pone donde se crea cada registro, no al guardar, para que «deshacer» o restaurar no marque como nuevo algo viejo | `data/Modelo.kt`, `ui/QuickActivity.kt`, `ui/pantallas/Gastos.kt`, `ui/pantallas/Lote2.kt`, `ui/pantallas/Momentos.kt`, `data/Enfoque.kt`, `data/Sustancias.kt`, `data/Lote3.kt`, `data/Exportar.kt`, `vestigium.R` (`vm_retraso`) | P-3, H-2-9 (en parte), H-NAV-9 (N-30) |
| D.26 | `Dia.version_noche`: cada día que pasa por la hoja de la noche guarda con qué orden de preguntas se llenó (1 por ahora) | `data/Modelo.kt` (`VERSION_NOCHE`), `ui/pantallas/Dia.kt`, `data/Exportar.kt` | P-8, preparación de N-18 |
| D.27 | Campos para lo que viene: `Gasto.subcategoria` y `Config.subcategorias` (N-29), `SaludDia.sueno_descartadas` (N-04) | `data/Modelo.kt`, `data/Exportar.kt`, `data/Diccionario.kt` | P-7, H-NAV-3 |
| D.28 | Papelera de 7 días para los registros que borras tú (sin pantalla todavía) | `data/Repo.kt` (`aPapelera`, `papelera`, `restaurar`, `limpiarPapelera`), `avisos/Trabajo.kt` | P-9 (N-14) |

: Tabla D.3. Revisión de navegación · Entregas N0 y NE (esquema 14)

**Efectos que conviene saber al leer los datos:**
- **`ts_creado` y `ts_editado` solo existen desde esta versión.** En los registros de antes van vacíos; `vm_retraso` los deja en NA. No se pueden reconstruir.
- **`version_noche` vale 1** en todos los días que pasen por la hoja desde esta versión. Cuando entre el orden nuevo (N-18), pasará a 2. Los análisis que crucen esa fecha deben leerse por tramo.
- **`subcategoria` y `sueno_descartadas` estarán vacías** hasta que lleguen sus pantallas (N-29 y N-04).

**Capítulos actualizados:** capítulo 4 (sueño, sección del día asignado), capítulo 10 (secciones 10.3.1, 10.7 y la 10.7.1 nueva) y los apéndices A (sección A.5) y C.

## Revisión de navegación · Entrega N1 (corregir donde se ve)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.29 | Las cifras del Tablero se tocan: el sueño abre su corrección; el ánimo y las palabras, la hoja de la noche; el gasto, Gastos; lo demás despliega «Lo que registró el teléfono» | `ui/pantallas/Hoy.kt` (`Tablero`, `TarjetaMetrica`), `ui/pantallas/Dia.kt`, `ui/Componentes.kt` (`Seccion(abrir)`) | N-01, P1 |
| D.30 | En el Día, cada fila de «Momentos de este día» y de «Enfoque y siestas» abre su corrección (momento, sustancia o tramo). Los tramos se corrigen por primera vez: tipo, inicio, duración y nota | `ui/pantallas/Dia.kt`, `ui/pantallas/EnfoqueSiestas.kt` (`DialogoTramo`) | N-02, P1 |
| D.31 | `DialogoMomento` corrige la fecha y la hora de cualquier registro y guarda `ts_editado`. Un momento viejo pasa a tipo `animo` al corregirlo | `ui/pantallas/Momentos.kt`, `data/Recaidas.kt` (`aOtraHora`) | N-03, H-NAV-2, H-2-9 |
| D.32 | Gastos e ingresos se corrigen en un diálogo, donde se tocan; registrar confirma con un aviso. El gasto y el consumo rápidos no se registran dos veces con un toque doble, y tocar fuera con un monto escrito avisa antes de cerrar | `ui/pantallas/Gastos.kt` (`DialogoGasto`, `DialogoIngreso`), `ui/QuickActivity.kt` | N-06 |
| D.33 | Deshacer en los borrados que no lo tenían (notas, metas, fechas propias, logros propios, citas, bloques, sitios ignorados, frases, ciudades, Patrimonio, reglas de nombres, categorías, mascotas y personas cercanas) y confirmación en los de un toque. La barra de Deshacer también en el registro rápido. Su botón «Deshacer» se llama ahora «Borrar este registro» | varios archivos de `ui/`, `ui/QuickActivity.kt` | N-13, P4 |

: Tabla D.4. Revisión de navegación · Entrega N1 (corregir donde se ve)

**Efectos al leer los datos:** desde esta versión, un registro de ánimo puede cambiar de hora y de día después de anotarlo; queda marcado con `ts_editado`. Los momentos viejos que se corrijan cambian de tipo a `animo`.

## Revisión de navegación · Entrega N2 (ayer y fechas)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.34 | Suplementos: «¿Cuándo la tomaste?» (ahora, ayer u otra hora) antes de «Tomé…»; «Últimas tomas» se tocan para corregir cantidad u hora, o quitarlas con Deshacer. En ⊕, tocar un suplemento ya tomado pregunta «otra toma» o «quitar la de hoy». El aviso «Ya los tomé» dice qué anotó y ofrece «Deshacer» durante 10 minutos; el mosaico rápido también dice qué anotó | `ui/pantallas/Lote3.kt` (`DialogoToma`), `data/Lote3.kt` (`registrar`, `registrarFaltantes`, `resumen`), `ui/QuickActivity.kt`, `avisos/Recordatorios.kt`, `avisos/Avisos.kt` (`tomasAnotadas`), `widget/Mosaicos.kt` | N-07, H-NAV-1 |
| D.35 | El registro rápido dice el día en palabras; el ánimo se puede poner «antes» (ayer u otra hora) y el registro se mueve con `Recaidas.aOtraHora`. El selector de hora tiene la ficha «ayer» | `ui/QuickActivity.kt` (`Encabezado`, `crear`), `ui/Selectores.kt` (`SelectorHora(ayer)`) | N-08 |
| D.36 | La calidad del sueño del widget y de la hoja rápida se guarda en el **día del despertar** (fecha del calendario), como las horas del reloj, aunque se anote antes de las 05:00. El widget muestra la calidad de ese mismo día | `ui/QuickActivity.kt` (`SuenoRapido`), `widget/Widgets.kt` | N-08, H-NAV-7 |
| D.37 | «Ponerte al día»: si faltan días (hasta 7, sin valencia ni diario, desde el primer día guardado), Hoy dice cuáles. La hoja de la noche los recorre uno tras otro; al sellar pasa al siguiente. La noche tiene una sección nueva, «Suplementos de este día», para anotar o quitar la toma de ese día. «Diario de ayer» ya no se va a las 14 h: se queda hasta llenarlo o descartarlo | `ui/pantallas/Hoy.kt` (`Pendientes.faltantes`), `ui/pantallas/Dia.kt` (`PantallaNoche`), `ui/pantallas/Calendario.kt` (`Seleccion.ponerse`) | N-09 |
| D.38 | Aviso del día lógico en palabras («Son las 01:30: lo que anotes va al lunes…») en el registro rápido, en Notas, en la noche de hoy y en la semana corta | `ui/Componentes.kt` (`AvisoMadrugada`) | N-10 |
| D.39 | Notas: el formulario dice para qué día es y deja cambiarlo; el diálogo de editar cambia también el tipo, la fecha y el recordatorio | `ui/pantallas/Lote2.kt` (`PantallaNotas`) | N-11 |
| D.40 | La pestaña Hoy abre siempre en hoy (también al volver a la app después de un minuto). «Abrir el día» desde otras pantallas apila el Día y «atrás» regresa | `ui/MainActivity.kt`, `ui/pantallas/Calendario.kt` (`Seleccion.abrir`, `limpiarFechas`), `ui/pantallas/Dia.kt` (`PantallaDia(apilado)`) | N-12 |

: Tabla D.5. Revisión de navegación · Entrega N2 (ayer y fechas)

**Efectos al leer los datos:**
- Desde esta versión hay más **tomas de suplementos y registros de ánimo anotados después**. Se distinguen con `ts_creado` frente a `ts` (`vm_retraso` en R, Apéndice C). Los niveles estimados de los suplementos se recalculan con las tomas corregidas; eso es lo esperado.
- Más días se llenan de memoria con «Ponerte al día». Quedan marcados como `retroactivo` (capítulo 3, sección 3.7.4).
- La calidad del sueño anotada entre las 00:00 y las 05:00 cambia de día: antes iba al día anterior y ahora va al del despertar. Los datos viejos no se mueven.

## Revisión de navegación · Entrega N3 (el Día como centro de corrección)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.41 | «Tu día en 24 horas», «Momentos de este día» y «Enfoque y siestas» son ahora una sola sección, «Tu día»: la línea de 24 horas y, debajo, todo lo que tiene hora ese día (momentos, consumos, tramos, gastos, ingresos, tomas y notas), cada fila corregible al tocarla. Plegada, dice cuántos registros tiene | `ui/pantallas/Dia.kt` (`TuDia`), `ui/Componentes.kt` (`Seccion(resumen)`) | N-17, N-26 |
| D.42 | «＋ agregar a este día…»: registro de ánimo con hora, gasto, nota y tomas de suplementos con la fecha de ese día ya puesta | `ui/pantallas/Dia.kt` (`TuDia`, `SuplementosDelDia`), `ui/QuickActivity.kt` (extra `FECHA`), `ui/pantallas/Gastos.kt` (`DialogoGasto(nuevo)`), `ui/pantallas/Lote2.kt` (`DialogoNota`, `Seleccion.notaPara`) | N-26 |
| D.43 | Lo que guardó un toque en el widget de ánimo o de sueño se ve arriba de la hoja («Guardado con el toque: 5/7») y se puede borrar o quitar | `ui/QuickActivity.kt` | N-15 |

: Tabla D.6. Revisión de navegación · Entrega N3 (el Día como centro de corrección)

**Efectos al leer los datos:** más registros con fecha pasada, agregados desde el Día. Llevan `ts_creado`, así que `vm_retraso()` los separa (H-NAV-9).

## Revisión de navegación · Entrega N4 (Hoy, la noche, Análisis y Vida)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.44 | Hoy, a cualquier hora, en el mismo orden: fecha y estado, la frase, «Tus últimos días», lo que falta (con lo de anoche, el experimento, «¿quién es?» y un contador de datos que faltan), «Llenar el día», el tablero con 4 cifras y «ver todo», y en la mañana el sueño si falta. Debajo, «Tu día», el sueño y «hace un año». Salen el personaje, la fila «cuestionarios · reacción · enfoque» y «pasar a la semana corta» (que pasa a Apariencia) | `ui/pantallas/Dia.kt` (`PantallaDia`), `ui/pantallas/Hoy.kt` (`Tablero(max)`, `DatosQueFaltan(compacto)`), `ui/pantallas/Sistema.kt` | N-16, P-10 |
| D.45 | «Tus últimos días»: 14 hilos con el color de la valencia, el alto del sueño frente a tu normal y un remate si el día está sellado. Hoy no se colorea hasta tener valencia y diario. Cada tema la dibuja a su manera. Se apaga en Apariencia (el ajuste del personaje) | `ui/pantallas/Hoy.kt` (`FranjaDias`) | N-28, P-10 |
| D.46 | **La hoja de la noche cambia de orden (`VERSION_NOCHE = 2`)**: «cómo estás ahora» (solo hoy), ánimo y cuerpo, predicción, contexto, hábitos y suplementos, diario, y lo opcional plegado. Los avisos del día se ven después de elegir la valencia; la activación usa una paleta secuencial | `ui/pantallas/Dia.kt` (`PantallaNoche`), `data/Modelo.kt` (`VERSION_NOCHE`, `cierrePermitido`), `ui/Componentes.kt` (`Escala(secuencial)`), `ui/pantallas/Lote2.kt` (`BloqueCierre`) | N-18, P-8, H-2-8, H-2-11 |
| D.47 | Análisis: arriba los experimentos y las relaciones más marcadas (5, con «ver las siguientes»); las herramientas en fichas; lo demás plegable. Las cifras van en una línea. Totales sale de Análisis | `ui/pantallas/Explorar.kt` | N-19 |
| D.48 | Vida: Campañas y «Dinero y sustancias» arriba; las galerías (tu año, logros, mapas, obras, otras vidas) juntas y plegadas | `ui/pantallas/Vida.kt` | N-20 |

: Tabla D.7. Revisión de navegación · Entrega N4 (Hoy, la noche, Análisis y Vida)

**Efectos al leer los datos:**
- **`version_noche` pasa de 1 a 2** desde esta versión. Los análisis que crucen la fecha (sobre todo valencia del día contra hábitos, y el cierre) deben leerse por tramo.
- Ya no se crean cierres en días pasados.
- La pregunta del experimento sube al primer alto de Hoy: se contestará más (H-NAV-9).

## Revisión de navegación · Entrega N5 (gastos en dos o tres toques)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.49 | Subcategorías de gasto con **monto habitual**: el fijado a mano o, si no, el que más se repite en tus últimos 10 gastos de esa subcategoría (si empatan, el más reciente). Elegir la subcategoría llena el monto | `data/Dinero.kt` (`montoHabitual`), `ui/QuickActivity.kt`, `ui/pantallas/Gastos.kt` | N-29, P-7 |
| D.50 | **Frecuentes**: tus 6 subcategorías más usadas en los últimos 90 días (y las de monto fijo), con su monto. Un toque registra el gasto y muestra «registrado · cambiar monto · deshacer»; un segundo toque en menos de 2 s no lo repite. También en el widget de gasto | `data/Dinero.kt` (`frecuentes`, `repetido`, `registrarFrecuente`), `ui/QuickActivity.kt`, `widget/Widgets.kt` | N-29 |
| D.51 | La hoja rápida es «Gasto o ingreso», con el selector arriba; en ⊕ el botón se llama igual | `ui/QuickActivity.kt` (`GastoRapido`) | N-29, P-7 |
| D.52 | En Gastos: «Categorías y subcategorías» (agregar, renombrar con la opción de cambiar los gastos anteriores, ordenar, quitar y fijar el monto habitual), el mes por subcategoría y la subcategoría en el diálogo de corrección. Sale el editor de Más → Datos | `ui/pantallas/Gastos.kt` (`EditorCategorias`, `DialogoDinero`), `ui/pantallas/Sistema.kt`, `data/Repo.kt` (`gasto`) | N-29, E-24 |

: Tabla D.8. Revisión de navegación · Entrega N5 (gastos en dos o tres toques)

**Efectos al leer los datos:**
- Los gastos de antes quedan sin subcategoría (`subcategoria` vacía en `vm_gastos.csv`).
- Registrar con un toque probablemente hará que se anoten **más gastos pequeños**: `gasto_total` puede subir desde esta versión sin que gastes más (H-NAV-9). El panel no cambia de fórmula (`gasto_total`, `gasto_imprevisto`).
- Renombrar una categoría con «cambiar también los gastos anteriores» reescribe esos gastos (quedan con `ts_editado`).

## Revisión de navegación · Entrega N6 (Más, buscador, nombres y pantallas huérfanas)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.53 | Más: un índice arriba («1 Registros · 2 Gente · 3 Medir · 4 Sistema») que abre y trae cada sección; las secciones se pliegan. Entran Gastos, Campañas, Experimentos, Lugares y «Días especiales y cumpleaños». El botón de Más mide 48 dp y dice «Más» | `ui/pantallas/Mas.kt`, `ui/Decoracion.kt` (`BotonAjustes`) | N-21 |
| D.54 | Buscador: pantallas que faltaban (Diario, Más, Lugares, cada herramienta de Análisis, Mundo, Patrimonio, Lista y pendientes, Nueva carta, Todos tus registros…) y **acciones** («corregir las horas de sueño» abre el Día con la corrección abierta, «anotar un suplemento de ayer», «ponerte al día»…). Busca por palabras sueltas, en cualquier orden y sin acentos, y muestra más de 8. Lo privado se encuentra por sus palabras de función, nunca por lo que escribiste. Las @personas y los #temas del resumen semanal llevan su texto al buscador | `ui/pantallas/Pantallas.kt` (`ACCIONES`, `buscar`), `ui/pantallas/Totales.kt`, `ui/pantallas/Semana.kt` | N-22, P-2 |
| D.55 | Un solo nombre para cada cosa: «Hoy» cuando el Día es hoy; «Llenar el día» en ⊕, el acceso directo, el widget («✎ diario» ahora abre la hoja) y el aviso de la noche; «Lista y pendientes»; cada herramienta con su nombre; «Cómo usar la app» y «Cómo leer tus datos». El Día y Privado tienen su «?». En los títulos de pantalla, el nombre del tema y el español van en la misma línea y del mismo tamaño, el del tema con más contraste | `ui/pantallas/Dia.kt`, `ui/Hoja.kt`, `ui/Estilo.kt` (`pantallaEnLinea`), `ui/QuickActivity.kt`, `res/values/strings.xml`, `widget/Widgets.kt`, `avisos/Avisos.kt`, `data/GuiaUso.kt` | N-23, P-1 |
| D.56 | Vuelve «Todos tus registros» (la lista editable de 30 días, con filtro por día), con un botón en Diario. Sale el código que ya no se usaba (`PantallaHall`, `RecuadroHall`, `CentroEntender`, `Portada`); la ruta `hall` sigue abriendo Logros. En Tu gente, «+ persona» sin mención previa | `ui/pantallas/Momentos.kt`, `ui/pantallas/Diarios.kt`, `ui/pantallas/Hall.kt`, `ui/pantallas/Gente.kt` | N-25, P-4 |

: Tabla D.9. Revisión de navegación · Entrega N6 (Más, buscador, nombres y pantallas huérfanas)

**Efectos al leer los datos:** ninguno en lo que se guarda. Una persona agregada a mano entra en «Tu gente» sin días hasta que la menciones.

## Revisión de navegación · Entrega N7 (corregir lo automático, papelera, gestos y accesibilidad)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.57 | En la corrección del sueño, cada sesión del reloj tiene «no cuenta» / «sí cuenta»; la sincronización la respeta y un respaldo no la borra. Con la noche corregida a mano, las siestas se vuelven a contar | `ui/EditarSueno.kt`, `sensores/Salud.kt`, `data/Modelo.kt` (`claveFuenteSueno`), `data/Fusion.kt` | N-04, H-NAV-3 |
| D.58 | Lugares: renombrar, cambiar el radio (agrandarlo busca visitas de 30 días), ignorar para siempre y quitar decidiendo qué pasa con sus visitas. En «Tu día», cada visita se corrige («fue en…» o «no estuve aquí») | `ui/pantallas/Medir.kt` (`DialogoVisita`), `data/Repo.kt` (`borrarLugar(id, pasarA)`, `guardarVisita`, `borrarVisita`), `data/Lugares.kt` (`reasignar`, `ignorarLugar`), `ui/pantallas/Dia.kt` | N-05, H-NAV-4 |
| D.59 | La papelera tiene pantalla (Más → Datos → Papelera, y en el buscador); lo privado borrado solo se recupera en Privado | `ui/pantallas/Papelera.kt`, `data/Papelera.kt`, `ui/pantallas/Recaidas.kt` | N-14, P-9 |
| D.60 | Gestos con alternativa visible: «elegir» en la foto del día, «＋ lugar aquí» en el mapa, − / + / «todo» en el mapa para rascar, flechas y pausa en Tu año en historias, y una sección «Atajos, corregir y deshacer» en la guía | `ui/pantallas/Dia.kt`, `ui/pantallas/Mapa.kt`, `ui/pantallas/Mundo.kt`, `ui/pantallas/TuAnio.kt`, `data/GuiaUso.kt` | N-24, P-7 |
| D.61 | Accesibilidad: la cuadrícula de ánimo se describe y se mueve con acciones de TalkBack; lupa y modo con 48 dp de toque; «Volver» fijo para TalkBack; «guardar» también abajo en el escritor; en Tu año, el primer toque dice el día y su ánimo | `ui/Selectores.kt`, `ui/Decoracion.kt`, `ui/Hoja.kt`, `ui/Libros.kt`, `ui/pantallas/Anno.kt` | N-27, P-10 |

: Tabla D.10. Revisión de navegación · Entrega N7 (corregir lo automático, papelera, gestos y accesibilidad)

**Efectos al leer los datos:**
- `sueno_min` y `siesta_reloj_min` pueden cambiar en los días donde marques «no cuenta», y `siesta_reloj_min` deja de congelarse en las noches corregidas.
- Las variables de lugar (`en_casa_min`, minutos por tipo, `lugares_distintos`) cambian en los días cuyas visitas corrijas o reasignes.

## Entrega 10c · Cálculos (commit `b943ec7`)

Decisiones del autor (27 oct 2026): todo aprobado, con D-4 = sí (festivos e I-2 con alcance mínimo). No cambia lo que se guarda: `SCHEMA_V` sigue en 14. Cambian los números que se calculan con lo ya guardado, también en los días viejos.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.62 | Cada cuestionario cubre solo los días que pregunta: WHO-5 y PSS-4, los 7 anteriores a responder (incluido el día); UCLA-3, los 30. Si dos respuestas cubren un día, se queda la primera que lo cubre (la más cercana después) | `ciencia/Dinamicas.kt` (`porDiaCuestionario`, `ventanaCuestionario`) | H-2-1 (P-14). Antes, un PSS-4 semanal dejaba todo el mes con el último puntaje |
| D.63 | Una sola regla de «cero» y «sin dato» (`ceroDesde`): 0 desde el primer registro de la fuente y en los días con diario; antes, vacío. Aplica a `gasto_total`, `gasto_imprevisto`, `n_impulso`, `escalada` y `gimnasio`. Además: `hab_<id>` vacío antes de que existiera el hábito (salvo marcado); `sueno_recordado` vacío si ese día no dijiste nada del sueño; las variables sociales (`personas`, `vi_*`, `cercanos`, `cercania_media`, `personas_*`, `penso_en_*`) y la base de `per_` y `pen_`, vacías los días sin diario escrito ni momento que nombre a alguien (en `tema_`, que tenga un #tema); `ejercicio_min` en 0 los días con datos del reloj y sin sesión; `distancia_km` en 0 los días con ubicaciones y sin movimiento; `probioticos_estimado` vacío sin bitácora de MacroFactor; «las tres» necesidades cuenta en autonomía, competencia y relación; `presencias_revisado` entra al panel (obvia con lo social) | `ciencia/Estadistica.kt` (`ceroDesde`, `panel`, `Movilidad.kmPorDia`), `ciencia/Dinamicas.kt` (`diasSociales`), `data/Lote3.kt`, `data/Exportar.kt` | H-1-1, H-1-6, H-2-4, H-4-8, H-6-13, H-9-16 (P-15). Los `ctx_*` siguen en 0 cuando no se marcan: es la convención de la pantalla |
| D.64 | `lugares_distintos` por día lógico. El LEEME y el diccionario dicen qué variables van por día de calendario: corazón, oxígeno, calorías activas, temperatura de la piel, respiración y peso del reloj, agenda, clima, luna, ciudades y países, y la pantalla y los pasos de antes del esquema 13 (el LEEME lo escribe «v13»; H-14-1) | `ciencia/Estadistica.kt`, `data/Exportar.kt` (`leeme`), `data/Diccionario.kt` (`CALENDARIO`) | H-1-2, H-3-1 (en parte), H-9-4 (P-16 i). La regla de +24 de `ultimo_uso_h` en R queda para 10d |
| D.65 | La t de Student exacta (`cuantilT`, `pT`, con la beta incompleta) y la autocorrelación de orden 1 de una serie con fechas (`ar1`). `diferencia()` usa t de Welch y multiplica el error estándar de cada grupo por √((1 + ρ)/(1 − ρ)), con ρ corregido por su sesgo en series cortas, ρ + (1 + 4ρ)/n, y acotado entre 0 y 0.9. Lo usan la Tendencia (30 contra 30 días), «Antes y después», los experimentos y las variables 0/1 de «Todo lo que se mueve» | `ciencia/Estadistica.kt` (`diferenciaCompleta`, `diferenciaEnDias`, `ar1Grupos`) | H-5-5, H-7-3 (P-17) |
| D.66 | «Todo lo que se mueve con algo» usa los mismos restos sin tendencia que la lista (con su interruptor, que llega desde la lista) y el p con el n efectivo; muestra r de Pearson, no ρ. «Cruzar dos variables» da el intervalo con el n efectivo y la r sin tendencia junto a la bruta. El modelo múltiple usa t con n − k − 1 grados y muestra el R² ajustado; su interruptor dice «Controlar por el valor del día anterior». «Día de la semana» pide 5 días por día de la semana y usa t con n − 1 | `ciencia/Analisis.kt` (`factores`, `modelo`, `porDiaSemana`, `cambio`), `ciencia/Estadistica.kt` (`sinTendencia`, `correlacionEf`), `ui/pantallas/Analisis.kt`, `ui/pantallas/Explorar.kt` | H-5-3, H-5-4, H-5-11 (P-17, P-28). `vm_factores` en R queda para 10d |
| D.67 | Experimentos: no se sella si con el lavado podrían quedar menos de 7 días en una condición (se prueban todos los sorteos posibles); la plantilla «Creatina» pasa a 12 semanas (14 días con la acción y 42 sin ella); el sorteo es por pares de semanas; la tendencia se mide con valor ~ día + condición; el aviso de diseño en bloque depende del plan real; mientras dura el plan el resultado dice «provisional» y no habla de señal; hoy no entra; «aún no hay suficientes días» distingue «falta esperar», «este plan nunca dará resultado» y «terminó sin suficientes»; el rótulo dice «Semanas con la acción según el plan»; el aviso del Día no sale antes del inicio; el panel de la pantalla se arma en segundo plano | `data/Lote3.kt` (`Experimentos.plan`, `diasPorCondicion`, `minimoPosible`, `enBloque`, `tendencia`, `resultado`), `ui/pantallas/Lote3.kt`, `ui/pantallas/Dia.kt` | H-7-1, H-7-4, H-7-5, H-7-6, H-7-8, H-7-9, H-7-13, H-9-30, H-9-31, H-9-32 (P-18, P-21). Los experimentos ya sellados conservan su plan |
| D.68 | «Antes y después»: los cambios de un mismo guardado van en una sola tarjeta, y el primer llenado de «Conocerte» no sale | `ui/pantallas/Explorar.kt` (`EtapasDeVida`) | H-7-12 (P-18), H-2-5 |
| D.69 | El ritmo de sueño usa las noches de los últimos 56 días (`vm_ritmo(dias = 56)` en R). Una sola regla de día libre, `Fechas.esDiaLibre`: sábado, domingo o, si en «Conocerte» vives en México, un festivo de la Ley Federal del Trabajo (art. 74), con sus reglas anteriores a 2006 y a 2024. El panel exportado gana `festivo` y `dia_libre`, y R lee esa columna. Con 1 a 6 noches del reloj, el aviso dice que se mezclan con las del teléfono | `ciencia/Analisis.kt` (`ritmo`, `ritmoDe`, `recientes`), `data/Fechas.kt` (`esFestivoMx`, `esDiaLibre`), `data/Exportar.kt`, `data/Diccionario.kt`, `ui/pantallas/Analisis.kt`, `vestigium.R` | H-4-9, H-4-10, H-9-15 (P-19), I-1 |
| D.70 | El alcohol: el peso es el más reciente entre la báscula y la tendencia de MacroFactor; antes del primero, el primero posterior; 70 kg solo si no hay ninguno. Con un peso real, sexo, estatura (100 a 250 cm) y, en hombres, edad, la r sale del agua corporal de Watson et al. (1980), r = agua / (0.80 · peso), acotada entre 0.45 y 0.95; si no, la r fija por sexo. Las tomas de MacroFactor sin hora legible van al mediodía. `alcohol` (0/1) sigue el día lógico de cada dosis (los episodios sin cantidad cuentan su propio día) | `data/Sustancias.kt` (`pesos`, `pesoDe`, `watsonR`, `rYPeso`, `msComida`), `ciencia/Estadistica.kt` | H-4-4 (en parte), H-9-13, H-9-17, H-9-24 (P-20), I-2 |
| D.71 | Las mismas cifras en todas las pantallas: la clave de la caché de dosis cambia con cada importación de MacroFactor, y en el hilo principal sin esa caché lo que depende de MacroFactor queda vacío (salvo `alcohol`, que queda en 0: H-14-4) | `ciencia/Estadistica.kt` (`sustancias`) | H-4-3, H-9-14 (P-21) |
| D.72 | Una sola 1RM estimada (`Macrofactor.e1rm`: Epley, de 1 a 12 repeticiones, sin calentamientos) en el reporte y en Tus apps. «Tus números»: el ejercicio semanal es la suma de las últimas 4 semanas con sesiones entre 4. Los días atípicos revisan `distancia_km` | `ciencia/Periodo.kt`, `ui/pantallas/TusApps.kt`, `ciencia/TusNumeros.kt`, `ciencia/Analisis.kt` (`atipicos`) | H-4-13 (P-22), H-5-9 (P-23), H-5-8 (P-24) |
| D.73 | Personas y red: `Texto.canon` sigue las uniones encadenadas hasta 8 pasos y une los ciclos (al primero en orden alfabético); `Perfiles.relacion`, `vi_convivientes`, los vínculos dichos y la cuenta de la Constelación en Vida usan el nombre unido; la Red colorea por grupo de relación y `vm_red_nodos.csv` gana `grupo` (R lo usa para la composición y el color); con «solo lo que dije», el grado y los grupos se recalculan; `per_` es obvia con todas las variables sociales; la cercanía se puede borrar, y pasar de «sin cercanía» a un valor (o al revés) queda en el historial; en los sueños, solo lo que lleva @ cuenta como persona | `data/Texto.kt`, `data/Perfiles.kt`, `data/Personas.kt`, `data/Lote3.kt` (`RedPersonas.dichos`, `grados`, `componentes`), `ui/pantallas/Lote3.kt`, `ciencia/Dinamicas.kt`, `ciencia/Estadistica.kt`, `ui/pantallas/Vida.kt`, `data/Exportar.kt`, `vestigium.R` | H-6-4, H-6-5, H-6-6, H-6-9, H-6-10, H-6-11, H-6-12, H-9-21, H-9-22 (P-25) |
| D.74 | Estado subjetivo: el panel ignora los «Cómo estás ahora» guardados otro día que el que cierran, también los viejos; el primer guardado de «Conocerte» deja historial y antes de esa fecha sus campos no dan etapa; un toque a menos de 100 ms del estímulo cuenta como anticipación y se repite; el ánimo por lugar usa el mismo criterio en la lista y en el mapa (lugar marcado o dentro del radio) y el texto ya no dice «últimos 60 días» de las medias; se corrigen los comentarios de la brújula política, de la separación de avisos y del ángulo de la Constelación, y el aviso de «escrito por adelantado» | `ciencia/Estadistica.kt` (`cierreFuera`), `ui/pantallas/Conocer.kt`, `data/Perfiles.kt` (`primerGuardado`), `ciencia/Dinamicas.kt`, `ui/pantallas/Medir.kt` (`enLugar`), `data/Politica.kt`, `avisos/Trabajo.kt`, `ui/pantallas/Constelacion.kt`, `ui/pantallas/Dia.kt` | H-2-5, H-2-6, H-2-7, H-2-8, H-2-12, H-2-14, H-2-15, H-6-14 (P-26) |
| D.75 | Las rachas sin recaída cuentan solo los días respondidos «no»; un día sin respuesta no suma ni reinicia, y se dice cuántos hubo desde la última | `data/Recaidas.kt` (`rachas`), `ui/pantallas/Recaidas.kt` | H-8-8 (P-27) |
| D.76 | Diccionario sin claves repetidas: las que chocaban pasan a «archivo:columna» (`antes`, `inicio`, `fin`, `min`, `dia_logico`, `categoria`, `minutos`, `duracion_s`, `grado`, `cantidad`); se describen las columnas de `vm_experimentos.csv`; `distancia_km` se separa en GPS (panel) y Health Connect (`vm_salud.csv`); `n_momentos` aclara que incluye lo privado. El aviso de potencia de Análisis distingue una pareja de la lista completa, y la etiqueta de `per_` menciona el mejor y el peor momento | `data/Diccionario.kt`, `ui/pantallas/Explorar.kt`, `ciencia/Dinamicas.kt` | H-5-16, H-6-2, H-9-8, H-9-19, H-9-20, H-9-25 (P-28) |
| D.77 | Reportes y Totales: el ánimo medio es la valencia del diario (como `valencia` en el panel y en R) y el reporte dice cuántos días tenían solo avisos; la hora de dormir usa la misma regla que el panel (`Exportar.horaDecimal`, corte a las 12); gastos e ingresos se cuentan por el día del diario | `ciencia/Periodo.kt`, `ciencia/Totales.kt`, `reportes/Reportes.kt` | H-5-15, H-8-10 (P-29) |
| D.78 | Pruebas JVM de cada regla: cuestionarios, `ceroDesde`, cuantiles de t, Welch, cobertura simulada (2000 réplicas, ρ = 0.5: entre 93 y 97 %), plan de «Creatina», sorteo por pares, hoy fuera del resultado, tendencia dentro de condición, ritmo reciente, festivos, peso, Watson, 1RM, atípicos, `canon`, red filtrada, cierres de otro día, rachas y diccionario | `app/src/test/.../CalculosPrueba.kt` | Que cada regla falle en una prueba si se rompe |

: Tabla D.11. Entrega 10c · Cálculos (commit b943ec7)

**Lo que no se cambió:** la corrección de Hedges para el d de Cohen (H-7-7) no cumple el criterio: se documenta en el capítulo 8. Las partes de R de P-16, P-17 y P-30 a P-33 son de la entrega 10d.

**Efectos que conviene saber al leer los datos:**
- **Más vacíos y menos ceros.** Muchas variables quedan vacías donde antes valían 0: gasto e impulsos antes del primer registro, hábitos antes de existir, `sueno_recordado` en días sin nada del sueño, lo social en días sin texto. Cambian medias, frecuencias y correlaciones en la dirección correcta, y la cobertura por variable baja.
- **Intervalos más anchos.** La Tendencia, los experimentos, «Antes y después», «Todo lo que se mueve», «Cruzar dos variables» y el modelo múltiple dan intervalos más anchos y p más grandes: algunas relaciones dejan de verse como «claras».
- **Cuestionarios.** Los `esc_*` del panel extendido cambian en todos los días ya respondidos, y las semanas sin respuesta quedan vacías.
- **Alcohol.** Con pesos registrados, `alcohol_estimado_max` y `alcohol_al_dormir` cambian (con Watson, normalmente bajan en hombres jóvenes y altos y suben en personas de más peso y edad). `cafeina_total_mg` y `alcohol_bebidas` pueden subir: ya no se pierden las tomas sin hora.
- **Personas.** Alguien partido en dos nombres por una unión encadenada se une; cambian algunas columnas `per_`.
- **Experimentos.** Los ya sellados no cambian de plan, pero su resultado se recalcula con las reglas nuevas.

**Lo que no se pudo comprobar en la sesión:**
- El valor 0.80 de la proporción de agua en la sangre de Watson et al. (1981): se comprobó la definición r = agua / (P · peso) en Searle (2015), pero no el valor de P en el texto original. Queda como constante (`Sustancias.AGUA_SANGRE`) con su nota.
- La forma exacta del sesgo de la autocorrelación en Marriott y Pope (1954): se comprobó la referencia, no el texto. La corrección se validó por simulación (ver D.78).

## Entrega 10d · R y exportación (commit `035982c`)

Decisiones del autor (27 oct 2026): todo aprobado. No cambia lo que se guarda (`SCHEMA_V` sigue en 14), pero sí el contenido de la exportación: cambian cifras de columnas de `vm_panel.csv` que ya existían, y el LEEME lo dice.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.79 | R analiza el panel de la app: `vestigium.R` carga `vm_panel_extendido.csv` y lo deja como `panel`, la tabla que usan por omisión todas las funciones; `vm$panel` (`vm_panel.csv`) queda por compatibilidad. Si no hay panel extendido (exportación resumida), se usa `vm_panel.csv` como antes | `vestigium.R` (`vm_archivos`, `.vm_enriquecer`) | H-5-1 (P-30) |
| D.80 | En `vm_panel.csv`, cada columna que comparte nombre con el panel de la app lleva el valor de la app, con sus reglas de 0 y vacío y conservando su tipo (0/1, entero o número): `gasto_total`, `ingreso_total`, `escalada` y `gimnasio` valen 0 desde el primer registro y en días con diario (antes, 0 o falso en cualquier fecha); `ejercicio_min`, 0 en días con datos del reloj y sin sesión; `ultimo_uso_h` suma 24 solo antes de las 5:00 (antes, antes de las 12), y lo mismo en todas las columnas comunes. `ruido_db_medio`, `km` y las otras equivalencias de la misma unidad toman el valor de su columna de la app | `data/Exportar.kt` (`comoLaApp`, `filasPanel`) | H-1-1 en la exportación, H-5-1, H-9-4, H-3-2 en R (P-30, P-16) |
| D.81 | `canciones` en la app cuenta solo las escuchas de 30 s o más (o sin duración registrada), lo que ya decía el diccionario. `luna_ilum` se calcula al final, para todos los días del panel (antes faltaba en los días que solo tenían nutrición o entreno) | `ciencia/Estadistica.kt` (`panel`) | H-9-3 (P-30) |
| D.82 | Las 11 columnas de `vm_panel.csv` que en la app se llaman distinto no se renombran (para no romper scripts viejos); el diccionario dice en cada una su par: `sueno_min`/`sueno_horas` (minutos frente a horas), `sueno_profundo_min`/`sueno_profundo`, `sueno_rem_min`/`sueno_rem`, `hrv_rmssd`/`hrv`, `km`/`distancia_km`, `valencia_muestreo`/`muestreo_valencia`, `luz_dia_min`/`luz_horas` (minutos frente a horas), `sol_min`/`sol_horas` (minutos frente a horas), `carga_noche_inicio_h`/`carga_noche_h`, `temp_piel_delta`/`temp_piel`, `ruido_db_medio`/`ruido_db`. `vm_variables.csv` lista las variables que la app cruza (con su nombre, su grupo y si son privadas) y `vestigium.R` cruza esas por omisión; con `privadas = TRUE` suma el seguimiento privado, que la app nunca cruza | `data/Exportar.kt` (`EQUIVALENTES`, `filasVariables`, `leeme`), `data/Diccionario.kt` (`conEquivalente`), `vestigium.R` (`.vm_variables`) | H-5-1 (P-30) |
| D.83 | `vm_familias.csv` (familia, variable, lado) trae las parejas que la app esconde por obvias, generadas con `Estadistica.familias` desde las mismas reglas de `obvia()`: `FAMILIAS`, grupos (lectura del día, momentos, sentimientos, sueños), lugares, suplementos, totales de cada prefijo (`AGREGADOS`), prefijos obvios entre sí (`ENTRE_SI`), la misma persona, el mismo cuestionario y las campañas; cada nombre de `vm_panel.csv` va con su par de la app. Con lado 0, todas las de la familia son obvias entre sí; con lados 1 y 2, solo las de un lado con las del otro. `vm_obvia`, `vm_relaciones` y `vm_factores` lo leen; sin ese archivo usan la lista vieja | `ciencia/Estadistica.kt` (`familias`, `obviaEnFamilias`), `data/Exportar.kt` (`filasFamilias`), `vestigium.R` (`.vm_pares_obvios`, `vm_obvia`) | H-5-2 (P-31) |
| D.84 | Otras funciones de R alineadas con la app: `vm_factores` es «Todo lo que se mueve con algo» (restos sin tendencia según `sin_tendencia`, r de Pearson con n efectivo; en las 0/1, la diferencia de medias con t de Welch y error estándar corregido por AR(1) con la corrección de Marriott y Pope); `vm_relaciones` y `vm_factores` no cruzan `fin_de_semana`, `dia_libre`, `festivo`, `con_diario` ni `n_muestreos`; `vm_semana` pide 5 días por día de la semana y usa t con n − 1; `vm_palabras` usa las mismas 167 palabras vacías que la app (sin importar acentos) y la misma referencia (la valencia media de los días con nota); `vm_atipicos` revisa las mismas variables que la app; `vm_por_hora` separa días laborables y libres con la columna `dia_libre` (fin de semana o festivo); `vm_red` no dibuja como línea a quienes «no se conocen» aunque aparezcan juntas, cuenta los grupos sin las personas aisladas (y las da aparte) y no falla si no hay ningún par. El diccionario documenta que el grado de `vm_red_nodos.csv` usa 1 día juntas y hasta 400 personas (la Red de la app, 2 días y 45 personas) | `vestigium.R` (`vm_factores`, `.vm_sin_tendencia`, `.vm_ar1`, `.vm_dif_dias`, `.vm_no_cruzar`, `vm_semana`, `vm_palabras`, `vm_atipicos`, `vm_por_hora`, `vm_red`), `data/Diccionario.kt` | H-5-3 y H-5-10 en R, H-9-15 en R, H-9-18, H-6-7, H-9-23 (P-32, P-17, P-19) |
| D.85 | Captura: el barómetro acepta lecturas de 300 a 1100 hPa (antes 850 a 1090: por encima de unos 1 450 m se perdían todas); Cold Turkey compara sitios por partes completas del dominio («x.com» ya no es dropbox.com; las apps siguen con «contiene»); un punto sin precisión cuenta como de 100 m, no de 0; el formato viejo de Takeout conserva la precisión de cada punto; las importaciones de la versión web y de Obsidian ya no ponen la hora de importar como sellos ni la versión actual como `schema_v` (usan los del archivo o los dejan vacíos, y `schema_v` = 0) | `sensores/Percepcion.kt` (`RANGO_HPA`), `data/Lote3.kt` (`ColdTurkey.coincide`), `data/Db.kt` (`PRECISION_SIN_DATO`), `data/Lugares.kt`, `ui/pantallas/Mapa.kt`, `data/Importar.kt` | H-3-3, H-3-6, H-3-8 (b), H-9-7, H-1-9, H-9-5 (P-33) |
| D.86 | Pruebas JVM: `vm_factores` y `vm_relaciones` dan los mismos números que la app con datos inventados (los esperados salieron de correr `vestigium.R`); las familias esconden exactamente las mismas parejas que `obvia()` en todos los pares de una lista de prueba; las palabras vacías de R son las de la app; los dominios de Cold Turkey y el rango del barómetro | `app/src/test/.../ExportacionPrueba.kt` | Que R y la app no vuelvan a separarse sin que una prueba falle |
| D.87 | El panel extendido trae también, con el nombre de la app, las variables `nut_`, `app_` y `act_` que la app cruza (antes solo iban con el nombre de sus tablas: `app_min_…`, nutrientes y movimiento). Sin ellas, R no cruzaba 57 de las variables de la app. Se encontró al correr `vestigium.R` con una exportación real de la v89 | `data/Exportar.kt` (`filasPanelExtendido`) | P-30 |

: Tabla D.12. Entrega 10d · R y exportación (commit 035982c)

**Efectos que conviene saber al leer los datos:**
- **`vm_panel.csv` cambia en columnas viejas.** `gasto_total`, `ingreso_total`, `escalada` y `gimnasio` quedan vacías antes del primer registro y en días sin diario; `ejercicio_min` gana ceros; `ultimo_uso_h` deja de sumar 24 entre las 5:00 y las 12:00 (un último uso a las 11:00 valía 35 y ahora vale 11); `canciones` puede bajar en la app.
- **R da otras cifras.** `vm_relaciones()` y `vm_factores()` analizan otra tabla, con otras variables y otras parejas obvias: sus listas cambian y deberían coincidir con las de la app.
- **Lecturas de presión en altura.** Si vives o viajas por encima de unos 1 450 m, empiezan a aparecer lecturas que antes se descartaban.
- **Laptop.** `laptop_social_min` y similares pueden bajar: ya no cuentan sitios que solo contenían el nombre.

**Lo que no se pudo comprobar en la sesión:**
- R no corre en CI. En la sesión se instaló R 4.3.3 y se corrió `vestigium.R` completo con CSV sintéticos (incluidas `vm_relaciones`, `vm_factores`, `vm_semana`, `vm_palabras`, `vm_atipicos` y `vm_red`); de ahí salieron los números que fija `ExportacionPrueba`. Después se corrió con una exportación real de la v89: carga y analiza sin errores, pero 57 variables de la app no estaban en el panel extendido con su nombre (D.87). Después, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026).
- El rango del barómetro se comprobó en las hojas de datos de dos sensores comunes (Bosch BMP280: 300 a 1100 hPa; ST LPS22HB: 260 a 1260 hPa); no se sabe qué sensor trae tu teléfono.
- `vm_cruce` (R) sigue con el n bruto: P-17 no la pedía.

## Entrega 10e · Cierre de hallazgos abiertos (después del módulo 14)

Decisión del autor (28 oct 2026): después de la lectura final, arreglar todo lo que tenga solución en el código, sin esperar a otra revisión. No cambia lo que se guarda (`SCHEMA_V` sigue en 14), salvo el formato de los respaldos cifrados (D.104), que no es parte de la base de datos. Cambian cifras de variables que ya existían, y hay dos variables nuevas.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.88 | Alcohol en sangre: el máximo del día (`alcohol_estimado_max`) mira de la primera dosis del día a 14 horas después, o hasta la primera dosis del siguiente día con alcohol, y la simulación incluye las dosis de las 24 horas previas: lo que seguía en la sangre de la noche anterior cuenta. Antes, cada día empezaba en 0 | `ciencia/Estadistica.kt` (`sustancias`) | H-4-5 |
| D.89 | El máximo del panel se lee cada 5 minutos (antes, cada 10), como la lista de episodios (`Sustancias.alcoholMaximo`): en un episodio sin alcohol en las horas previas, las dos dan la misma cifra (la lista solo mira desde 1 hora antes del episodio) | `ciencia/Estadistica.kt` (`sustancias`) | H-14-5 |
| D.90 | En el hilo principal, sin las dosis de MacroFactor ya calculadas, `alcohol` (0/1) queda vacío los días sin un episodio anotado (antes, 0) y en 1 los días con un episodio, como las demás variables de dosis | `ciencia/Estadistica.kt` (`sustancias`) | H-14-4 |
| D.91 | Cafeína y alcohol de MacroFactor y de la app se juntan toma por toma, no por día. Cafeína: una toma de la app se empareja con una de MacroFactor a menos de 90 minutos y de cantidad parecida (de la mitad al doble); la pareja cuenta una vez, con la hora y la cantidad de la app (la hora de MacroFactor es la del registro, no la de la toma). Alcohol: un episodio de la app y lo de MacroFactor entre su inicio y su fin (±90 minutos) son el mismo; cuenta la fuente con más gramos. Antes, si MacroFactor tenía algo ese día, se ignoraba todo lo de la app | `data/Sustancias.kt` (`dosis`, `MISMA_TOMA`) | H-4-2 |
| D.92 | `cafeina_ultima_h` sale de la misma lista de dosis que `cafeina_total_mg` (MacroFactor y la app, por día lógico; antes de las 5:00 suma 24). Antes, solo de MacroFactor y por día de calendario. En el hilo principal sin las dosis ya calculadas queda vacía, como las demás que dependen de MacroFactor. `cafeina_mg` sigue siendo el nutriente de MacroFactor tal cual | `ciencia/Estadistica.kt` (`sustancias`), `data/Exportar.kt` (`filasPanel`), `data/Diccionario.kt` | H-4-12 (en parte) |
| D.93 | `personas` cuenta a quien nombras con @ en el diario **y** en los momentos no privados de ese día, los mismos días en que puede valer 0 (`Dinamicas.diasSociales`). Antes, solo el diario: un día con solo momentos valía 0 aunque nombraras a alguien | `ciencia/Estadistica.kt` (`panel`), `data/Diccionario.kt` | H-14-2 |
| D.94 | Variables nuevas: `error_prediccion_signo` = valencia − predicción de la noche anterior (−6 a +6; positivo, el día salió mejor de lo predicho) y `gasto_imprevisto_monto` = suma de los gastos imprevistos del día (0 desde el primer gasto, en días con diario). Las dos son obvias con sus parientes: `error_prediccion_signo` con `prediccion`, `error_prediccion` y `valencia` (de la que se calcula; desde 10e, también `error_prediccion` con `valencia`), y `gasto_imprevisto_monto` con `gasto_total`, `gasto_imprevisto`, `balance_dinero` y las `gcat_` (desde 10e, `gasto_imprevisto` también con `balance_dinero`) | `ciencia/Estadistica.kt` (`variables`, `panel`, `FAMILIAS`), `ciencia/Dinamicas.kt` (`AGREGADOS`), `data/Diccionario.kt` | H-2-10, H-1-5 |
| D.95 | Cuando la autocorrelación de un día al siguiente no se puede estimar (menos de 10 pares de días seguidos, o sin variación), se usa 0.3 (`Estadistica.AR1_SIN_DATO`) en vez de 0 para el n efectivo: en «Las relaciones», en «Todo lo que se mueve con algo» y en «Cruzar dos variables». Igual en R (`.vm_ar1_sin_dato`). Las diferencias de medias no cambian | `ciencia/Estadistica.kt` (`autocorr`), `ciencia/Analisis.kt` (`factores`), `ui/pantallas/Explorar.kt`, `vestigium.R` | H-5-13 |
| D.96 | Experimentos: el tamaño del efecto es la g de Hedges, d · (1 − 3 / (4 · (n₁ + n₂) − 9)), y la tarjeta dice que la escala es relativa a cómo varían tus días. En la plantilla «Creatina», los días sin respuesta a «¿Lo hiciste hoy?» se completan con `supl_creatina_tomado` | `data/Lote3.kt` (`Experimentos.resultado`), `ui/pantallas/Lote3.kt` | H-7-7, H-9-26 |
| D.97 | R: `vm_experimento(id)` repite `Experimentos.resultado` (plan, lavado, días que cuentan, diferencia con Welch y error corregido, g de Hedges, tendencia, cumplimiento) y agrega una prueba de aleatorización exacta de dos colas: la diferencia observada contra la de todos los planes posibles del sorteo por pares (2^k). Sin lavado, el plan espejo da la misma diferencia con el signo contrario, así que el *p* más chico posible es 2 / planes (con 12 semanas, 0.031); la nota lo calcula con los datos. En «antes y después» no hay prueba (devuelve NA) | `vestigium.R` (`vm_experimento`) | H-7-11 |
| D.98 | Creatina: la reserva llega al 95 % en 28 días con **3 g** al día, la dosis del estudio de origen (Hultman et al., 1996), y no con la dosis que pusiste (5 g en el catálogo): con 5 g se llena antes. La referencia de 3 g está fija: cambiar la dosis de la creatina en la app ya no cambia lo que tarda en llenarse. Los suplementos con vida media menor de 6 horas (melatonina) ya no tienen `supl_x_nivel`: a las 5:00 casi siempre valía 0 | `data/Lote3.kt` (`Suplementos.dosisReferencia`, `carga`, `panel`, `VIDA_MEDIA_MIN_NIVEL`) | H-4-6, H-4-7 (en parte) |
| D.99 | El diccionario de cada exportación dice con qué parámetros se calcularon `cafeina_al_dormir_mg`, `creatina_saturacion_pct` y cada `supl_x_nivel` (vida media, días de carga y de lavado, dosis de referencia) y que cambiarlos recalcula toda la historia | `data/Diccionario.kt` (`conParametros`) | H-4-11 (en parte) |
| D.100 | «Tu año»: «el día más raro» usa todos los días del año elegido; antes, `atipicos` descartaba los anteriores a los últimos 365 desde hoy | `ciencia/Analisis.kt` (`atipicos(desde)`), `ciencia/TuAnio.kt` | H-14-3 |
| D.101 | Reportes: una diferencia con el periodo anterior menor que su error típico, √(s₁²/n₁ + s₂²/n₂), se dice «parecido al periodo anterior» en vez de «+x frente al periodo anterior». No descuenta la autocorrelación, así que se queda corto | `ciencia/Periodo.kt` (`errorDif`, `errorAnterior`), `reportes/Reportes.kt` (`cambio`) | H-8-11 |
| D.102 | Tu gente: al cambiar la relación o la cercanía se puede decir que cambió antes de hoy (no antes del cambio anterior); los días hasta esa fecha se analizan con la relación de antes | `ui/pantallas/Gente.kt` (`FormularioPersona`; usa el parámetro `hoy` que ya tenía `Personas.guardar`) | H-6-3 (en parte) |
| D.103 | Interfaz y textos: «Las relaciones más marcadas», por semana, abre la pareja en «Cruzar dos variables» por semanas (antes, la ficha por día); «Quitar tendencias» dice que no quita el ritmo de la semana; la lista remite a «Antes y después» cuando deja fuera etapas, cuestionarios o la brújula; la dinámica emocional llama «muy difíciles» a los días de 1 o 2 (la racha dura sigue siendo de 3 o menos); el texto de privacidad nombra todos los servicios externos y lo que recibe cada uno (Drive, los respaldos completos) | `ui/pantallas/Explorar.kt`, `ui/pantallas/Analisis.kt`, `ui/PrivacidadActivity.kt`, `data/Red.kt` | H-9-40, H-5-6 (en parte), H-5-14, H-5-12, H-8-17 |
| D.104 | Respaldos cifrados: formato `VESTIGIUM-MEI-CIFRADO-2`, con las iteraciones de PBKDF2-HMAC-SHA256 en el encabezado (600 000, lo que recomienda OWASP; antes 210 000) y el encabezado completo como dato autenticado. Los `.vmc` del formato 1 se siguen abriendo. Al cambiar o quitar la contraseña, la app avisa que las copias ya hechas siguen con la anterior | `data/Respaldos.kt`, `ui/pantallas/Respaldo.kt` | H-8-9, H-8-15 |
| D.105 | WhatsApp: en un chat corto donde ningún número de la fecha pasa de 12, se elige día/mes o mes/día según cuál deja los mensajes en orden (como vienen en el archivo); con empate, día/mes. Los avisos de multimedia se reconocen también en francés, portugués, italiano y alemán | `data/Importaciones.kt` (`whatsappLeer`, `MULTIMEDIA`) | H-3-10 |
| D.106 | Otras funciones de R: `vm_red(min_dias = 3)` deja fuera a quien se mencionó menos de 3 días, salvo que tenga una línea dicha en Tu gente (`min_dias = 0`, como antes); `vm_cruce` da Pearson (el principal, con intervalo y p con el n efectivo, como la app) y Spearman, y empareja el desfase por fecha (antes, por renglón); `vm_presion` usa el mismo resumen; `vm_cruce_horas` da Pearson y Spearman, con p sin corregir; `vm_relaciones` y `vm_factores` ganan `sin_semana = FALSE` (resta a los restos sin tendencia el promedio de su día de la semana); `vm_dinamica` calcula la inercia y el MSSD de cada mes solo con pares del mismo mes; `vm_laptop` excluye los mismos 8 navegadores que la app | `vestigium.R` | H-6-8, H-5-7, H-5-6 (en parte), H-5-12, H-14-6 |
| D.107 | El LEEME y el diccionario dicen «esquema 13» y «esquema 14» donde decían «v13» y «v14» (las versiones de la app son otras), y el LEEME dice dónde está el esquema de cada día (`schema_v` de `vm_dias.csv`) | `data/Exportar.kt` (`leeme`), `data/Diccionario.kt` | H-14-1 |
| D.108 | Pruebas JVM: g de Hedges, cumplimiento de «Creatina» con el registro de suplementos, atípicos de otro año, error típico de los reportes, dosis toma por toma y respaldos de los dos formatos (incluidos encabezados dañados) | `app/src/test/.../CierrePrueba.kt`, `RespaldoPrueba.kt` | Que cada regla falle en una prueba si se rompe |

: Tabla D.13. Entrega 10e · Cierre de hallazgos abiertos (después del módulo 14)

**Efectos que conviene saber al leer los datos:**
- **Alcohol.** `alcohol_estimado_max` puede subir los días que siguen a una noche con alcohol y sube un poco (hasta ≈ 0.0125 g/L) cuando el pico caía entre dos lecturas de 10 minutos. `alcohol_bebidas` y `cafeina_total_mg` pueden subir los días con registros en los dos lados que no eran la misma toma, y bajar donde una toma estaba dos veces con horas lejanas.
- **Personas.** `personas` sube los días con momentos que nombran a alguien.
- **Creatina.** Con 5 g al día, `creatina_saturacion_pct` llega antes al 95 % (unos 17 días en vez de 28).
- **Relaciones.** Las variables con pocos días seguidos (esporádicas, y las primeras semanas del modo semanal) tienen un n efectivo algo menor: algunas parejas pueden dejar de sostenerse.
- **Experimentos.** El tamaño del efecto baja un poco con pocos días (con 3 + 3 días, al 80 %).
- **Respaldos.** Las versiones anteriores de la app no abren los `.vmc` nuevos: para restaurar en otro teléfono, instala primero esta versión.

**Lo que no se pudo comprobar en la sesión:**
- Lo de Kotlin se compiló y se probó en GitHub Actions (pruebas JVM); no se probó en un teléfono. Cuánto tarda derivar la llave con 600 000 iteraciones en el teléfono no se midió (corre fuera del hilo principal).
- R se corrió con CSV sintéticos: `vm_experimento`, `vm_red(min_dias)`, `vm_cruce`, `sin_semana` y `vm_dinamica`, con un caso comprobado a mano para la prueba de aleatorización y para la red. Las cifras que fija `ExportacionPrueba` no cambian.
- Que el 0.3 de autocorrelación sea un buen valor por omisión es **[Diseño propio]**: es un valor prudente, no estimado.

## Entrega 10f · Decisiones del autor sobre los pendientes (esquema 15; versión v92)

Decisión del autor (28 oct 2026), hallazgo por hallazgo: respetar siempre la hora local del lugar donde se toma la nota y marcar los cambios de horario (H-1-7); poner desde qué versión rige cada columna (H-1-8); hacer las propuestas de H-3-4 y H-3-7; sellar el criterio como mejor convenga (H-7-10); medir la creatina con un ejercicio fijo, el press inclinado con mancuernas (H-7-14); dejar los logros como están (H-8-6); mostrar en la app la lista de servicios externos, en un lugar visible que no estorbe (H-8-17). Cambia lo que se guarda: `SCHEMA_V` sube a 15.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.109 | **Hora local de entonces.** La app guarda cada cambio de zona horaria del teléfono (`Config.zonas`, una lista de «desde qué instante, qué zona»), y cada instante se convierte con la zona que tenía el teléfono en ese momento (`Fechas.zonaEn`), no con la de hoy. Eso rige la hora, el día de calendario y el día lógico (`local`, `diaDeMillis`, `diaLogicoDeMillis`, `isoDeMillis`) y, al revés, los bordes de cada día (`inicioDiaLogico`, `finDiaLogico`, `inicioDia` y `finDia`, cada uno con la zona de esa hora: un día con cambio de horario dura 23, 25 o, en un viaje, otras horas). Una nota guardada con su desfase se lee con ese desfase (`localDe`, `horaDe`); una marca en UTC (de una importación) se pasa a la zona de entonces. El historial se anota al abrir la app, cada 15 minutos y cuando Android avisa de un cambio de zona (`TIMEZONE_CHANGED`). La primera vez se arma con lo ya guardado: los periodos en que dos o más notas seguidas traen un desfase que la zona de hoy no daría quedan con ese desfase, y todo lo demás, con la zona de hoy. Lo convierten así también Health Connect, la pantalla, las fotos (la hora de la cámara), WhatsApp, Kindle, la tabla por hora, la cinta, la estadística y la ventana de avisos de cada día (`planDelDia`). Al restaurar un respaldo, los dos historiales se juntan (`combinarConfig`; lo de aquí manda si coincide el instante) | `data/Fechas.kt`, `data/Repo.kt` (`registrarZona`), `avisos/Trabajo.kt`, `sensores/*`, `data/Exportar.kt` y las pantallas que convertían horas | H-1-7 |
| D.110 | **Marca del cambio de horario.** `vm_panel.csv` trae `desfase_utc_h` (horas de diferencia con UTC a mediodía, −6 en la Ciudad de México) y `cambio_horario` (1 si la hora del teléfono cambió durante el día lógico: horario de verano o un viaje). El Día lo dice debajo de la fecha: «cambio de horario: de UTC−6 a UTC+2 · el día duró 16 h». En R, `vm_hora_local(ts)` da la hora de una marca con su propio desfase; `vm_fecha_hora` sigue pasando todo a `vm_zona` (es el mismo instante) | `data/Exportar.kt` (`filasPanel`), `data/Fechas.kt` (`desfaseH`, `cambioDeHora`), `ui/pantallas/Dia.kt`, `vestigium.R` | H-1-7 |
| D.111 | **Fuente del clima.** Cada día guarda de dónde salió (`EntornoDia.fuente`: «archivo», el reanálisis histórico de Open-Meteo, o «pronostico»). En lo guardado antes se deduce de cuántos días habían pasado al pedirlo (`Entorno.fuente`, con la misma regla de 80 días, `DIAS_ARCHIVO`). Los días del último año que vinieron del pronóstico se vuelven a pedir al archivo cuando ya tienen más de 80 días, así que la serie deja de mezclar las dos fuentes. El archivo no trae el índice UV: si el día ya lo tenía del pronóstico, se conserva (y también lo del aire si el servicio no responde). Columnas: `fuente` en `vm_entorno.csv` y `clima_reanalisis` (0/1) en `vm_panel.csv` | `sensores/Entorno.kt`, `data/Modelo.kt`, `data/Exportar.kt` | H-3-4 |
| D.112 | **Wifi sin dato.** Si hay wifi se pregunta también a `ConnectivityManager`: si alguna red activa es wifi, cuenta como wifi aunque el nombre venga oculto («wifi (sin nombre)»). «Sin wifi» solo cuando el wifi está apagado o el sistema dice que no hay red wifi; si no se pudo saber, los minutos van a `wifi_sin_dato_min` y no a `sin_wifi_min` | `sensores/Percepcion.kt` (`wifi`), `data/Modelo.kt` (`ConexionDia`), `data/Exportar.kt` | H-3-7 |
| D.113 | **Experimentos: el criterio también se sella.** Al crear un experimento se puede decir hacia dónde se espera el cambio (`sentido`: sube o baja) y, si se quiere, la diferencia mínima que importaría (`umbral`, en las unidades de la medida). Al terminar el plan, la tarjeta dice si se cumplió: la diferencia va en ese sentido, mide al menos el umbral (0 si no hay) y su intervalo de 95 % no toca el cero (`Experimentos.criterio`). A medio camino no se decide. Lo sellado (pregunta, acción, medida, hipótesis, criterio, diseño, inicio, semanas, plan, lavado y hora del sello) lleva una huella SHA-256 (`huella`); si algo cambia después, la tarjeta lo avisa. `vm_experimentos.csv` trae `sentido`, `umbral`, `huella` e `intacto`, y `vm_experimento` da `criterio` | `data/Lote3.kt` (`textoSellado`, `huella`, `sellar`, `intacto`, `criterio`), `ui/pantallas/Lote3.kt`, `vestigium.R` | H-7-10 |
| D.114 | **Fuerza de referencia.** `fuerza_ref_kg` es la mejor 1RM estimada del día (Epley, series de 1 a 12 repeticiones sin calentamiento, `Macrofactor.e1rm`) en un solo ejercicio fijo: el press inclinado con mancuernas, escrito en inglés o en español (`Macrofactor.esReferencia`: el nombre tiene que decir «press», «inclin» y mancuernas, así que no entran las aperturas ni los curls). Vacío los días sin ese ejercicio. La plantilla «Creatina» la mide en vez de `volumen_kg`, y su nota dice que con entrenamiento progresivo la cifra sube sola. Se calcula al importar MacroFactor, al restaurar un respaldo que trae entrenos y, una vez, para lo ya importado. También en `vm_entrenos.csv`, y `vm_series.csv` marca las series del ejercicio (`referencia`) | `data/Macrofactor.kt`, `data/Repo.kt` (`completarFuerzaReferencia`), `data/Lote3.kt` (`PLANTILLAS`), `ciencia/Estadistica.kt` | H-7-14 |
| D.115 | **Desde qué versión rige cada columna.** `vm_diccionario.csv` trae `desde_version` (la versión de la app desde la que rige la definición actual de la columna) y `cambio` (qué cambió entonces, con su fila de este apéndice). Vacío = sin cambios desde la v84. El LEEME dice que se miren al comparar exportaciones de fechas distintas | `data/Diccionario.kt` (`DESDE`, `desde`), `data/Exportar.kt` (`leeme`) | H-1-8 |
| D.116 | **Lo que sale del teléfono, en la app.** Ajustes › Privacidad termina con «Qué sale del teléfono», plegada: cada servicio externo y qué recibe. La política de privacidad usa la misma lista (`Red.SERVICIOS`) | `data/Red.kt`, `ui/pantallas/Sistema.kt`, `ui/PrivacidadActivity.kt` | H-8-17 |
| D.117 | **Logros «Día perfecto» y «Remontada».** Se quedan como están, por decisión del autor. El riesgo (un incentivo pequeño a sesgar la valencia) queda documentado en el capítulo 9 | — | H-8-6 |
| D.118 | Pruebas JVM: hora local con un historial de zonas (un viaje y el horario de verano), bordes de un día con cambio de hora, fuente del clima deducida, ejercicio de referencia y su 1RM, y huella y criterio de un experimento | `app/src/test/.../ZonasPrueba.kt` | Que cada regla falle en una prueba si se rompe |

: Tabla D.14. Entrega 10f · Decisiones del autor sobre los pendientes (esquema 15; versión v92)

**Efectos que conviene saber al leer los datos:**
- **Hora local.** Mientras no viajes, nada cambia. Si viajaste antes de esta versión, lo que se guardó ya resumido por día (sueño, pasos, pantalla) se quedó con la zona que tenía el teléfono al sincronizar y no se recalcula; lo que se guarda como instante (ubicaciones, momentos, sesiones, dosis) se reparte en horas y días al leer, con el historial (los km por día, el Mundo, la tabla por hora, las horas del panel). Las columnas de hora no se marcan con 92 en `desde_version`, porque sin viajes no cambian.
- **Creatina.** `fuerza_ref_kg` solo existe los días con el press inclinado: con 14 días con la acción en la plantilla, hacerlo una vez por semana deja muy pocos días medidos (hacen falta 3 por condición). La nota de la plantilla pide hacerlo al menos dos veces por semana.
- **Clima.** Después de la primera sincronización, los días de hace 80 días a un año que venían del pronóstico cambian un poco sus cifras (son del reanálisis).
- **Wifi.** `sin_wifi_min` puede bajar desde esta versión, si el teléfono escondía el nombre de la red.
- **Creatina.** Un experimento con la plantilla de antes sigue midiendo `volumen_kg`; solo los nuevos usan `fuerza_ref_kg`.

**Lo que no se pudo comprobar en la sesión:**
- Lo de Kotlin se compiló y se probó en GitHub Actions (pruebas JVM); no se probó en un teléfono: ni el aviso `TIMEZONE_CHANGED`, ni la respuesta de `ConnectivityManager` cuando Android esconde el nombre de la red, ni el nombre exacto con que MacroFactor exporta el press inclinado con mancuernas (se reconocen las formas en inglés y en español).
- Que el archivo de Open-Meteo ya tenga los días de más de 80 días es lo que la app suponía desde antes; no se consultó en la sesión.

## Reporte para terapia (sin cambio de esquema)

Pedido del autor (30 oct 2026): un reporte semanal y quincenal para llevar a terapia. Decisiones del autor: los últimos 7 o 14 días, terminando ayer o en el día que se elija; sustancias y «lo mejor y lo peor del periodo» encendidos por omisión; «lo mejor y lo peor de cada día» y el seguimiento privado apagados; días y duración del gimnasio; alimentación contra el objetivo y los nutrientes de interés. No cambia lo que se guarda (las casillas no se guardan), así que `SCHEMA_V` sigue igual; tampoco cambian la exportación ni `vestigium.R`.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.119 | **Reporte para terapia.** Un PDF tamaño carta, de varias páginas y para imprimir, con la cobertura de datos, una tabla día por día, ánimo, cuestionarios, sueño, actividad, contacto sin nombres, gimnasio, alimentación y las secciones opcionales. Se genera en Reportes › «Para terapia» y se comparte con el menú de Android. Lo que lleva, lo que no y sus reglas están en el capítulo 9, sección 9.3.6 | `ciencia/Terapia.kt`, `reportes/ReporteTerapia.kt`, `ui/pantallas/Sistema.kt` | Pedido del autor |
| D.120 | **Lo privado en el reporte para terapia.** Única excepción a la regla de `Privado`: impulsos, recaídas, detonantes y medicamentos (nunca la libido) entran solo con una casilla que se marca al generarlo, no se guarda y empieza apagada | `ciencia/Terapia.kt` (`Opciones.privado`), `data/Privado.kt` (comentario) | Que salga solo cuando se elige |
| D.121 | Pruebas JVM: los nombres como iniciales, las calorías contra el objetivo (con días sin registro), la proporción de emociones desagradables, los impulsos superados y que lo privado y los textos de cada día solo salgan con su casilla; captura de la pantalla y de las páginas del reporte con los datos inventados de las capturas | `app/src/test/.../TerapiaPrueba.kt`, `Capturas.kt` (`reportesBotanico`, `reporteTerapia`) | Que cada regla falle en una prueba si se rompe |

: Tabla D.15. Reporte para terapia (sin cambio de esquema)

| D.122 | **Secciones elegibles.** Pedido del autor: elegir exactamente qué sale. El reporte se divide en 8 secciones (ánimo y emociones; cuestionarios; sueño; actividad, rutina y contacto; gimnasio y alimentación; sustancias; lo mejor y lo peor, con «también el de cada día» dentro; seguimiento privado) y un interruptor «Todo». Empiezan encendidas las que ya salían y apagadas las que estaban apagadas; la cobertura sale siempre y la tabla día por día lleva solo las columnas de lo elegido | `ciencia/Terapia.kt` (`Opciones`), `reportes/ReporteTerapia.kt`, `ui/pantallas/Sistema.kt`, `TerapiaPrueba.kt` (`secciones`) | Pedido del autor |

**Lo que no se pudo comprobar en la sesión:**
- El código se compila y se prueba en GitHub Actions; no se probó en un teléfono que el menú de compartir reciba el PDF ni cómo se ve impreso.

## Ideario (esquema 16)

Pedido del autor (2 nov 2026): una libreta para lo que aprende de libros, videos y conversaciones y para sus propias ideas, centralizada, con temas, fuente y quién lo dice, sin que llenarla canse. Decisiones del autor: se llama «Ideario» (no «Conocimiento»: caben ideas que se dudan o rechazan); Notas queda igual, con un botón para pasar una nota; los autores van en un campo aparte, no como @personas; la vista por temas muestra solo los #temas escritos en ideas.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.123 | **La colección `ideas`.** Cada idea guarda texto (lo único obligatorio), la idea en una línea, origen, quién lo dice, obra y cita, fuente, enlace, minuto o página, postura, fijada y sus revisiones, con la hora en que se escribió. Entra al respaldo, a la papelera de 7 días y a la búsqueda general. No agrega variables al panel, y sus #temas y @personas no cuentan en «Tus temas» ni en la red social | `data/Modelo.kt` (`Idea`, `RevisionIdea`, `SCHEMA_V` = 16), `data/Repo.kt`, `data/Papelera.kt`, `data/Exportar.kt` (`Respaldo`), `data/Importar.kt`, `ui/pantallas/Totales.kt` (`EnIdeas`) | Pedido del autor |
| D.124 | **Dónde se escribe.** Quinta libreta de las bibliotecas (por mes o por estanterías de #temas); compartir texto desde otra app abre una idea con el enlace y el título ya puestos (`Ideario.deCompartido`: enlaces de YouTube o podcasts dejan el origen en «visto»; Kindle, Goodreads o Amazon, en «leído»); en una obra, «Ideas» y «anotar lo que entendí» en cada cita; en el Día, las ideas del día en «Tu día»; en la hoja de la noche, «¿Aprendiste algo hoy?» (plegada, no guarda nada si no se usa); en Notas, «pasar al Ideario» (conserva la hora de creación); en una entrada del diario, «Pasar al Ideario» (una copia); y en el menú ⊕ | `ui/pantallas/Ideario.kt`, `Bibliotecas.kt`, `Biblioteca.kt`, `Galerias.kt`, `Dia.kt`, `Lote2.kt`, `Diarios.kt`, `ui/QuickActivity.kt`, `ui/MainActivity.kt`, `AndroidManifest.xml` | Pedido del autor: que sea fácil de llenar |
| D.125 | **«Una idea de antes».** En el Día de hoy, una idea de hace un mes, seis meses o un año (± 3 días), o cualquiera de más de 30 días; las respondidas descansan 30 días; una por día. Se responde «sigo pensando igual», «ya no» (y se ofrece cambiar la postura) o «ahora no». Se apaga en la libreta o en la propia tarjeta (`Config.ideas_resurgir`). No hay notificación: los horarios de avisos no cambian | `data/Ideario.kt` (`resurgir`, `respondidaHoy`, `haceCuanto`), `ui/pantallas/Ideario.kt` (`UnaIdeaDeAntes`) | Pedido del autor: volver a leer lo anotado. Aproximación a la repetición espaciada (capítulo 2, sección 2.5) |
| D.126 | **Exportación.** `vm_ideas.csv` y `vm_ideas_revisiones.csv`, con su diccionario y su lugar en `vm_archivos` de R. Pruebas JVM: estanterías solo con temas de ideas, voces sin repetir, elección de «Una idea de antes» y su descanso, lo compartido, la nota pasada al Ideario y el respaldo con y sin ideas; capturas de la libreta, la idea y la tarjeta | `data/Exportar.kt`, `data/Diccionario.kt`, `assets/vestigium.R`, `app/src/test/.../IdearioPrueba.kt`, `Capturas.kt` | Que cada regla falle en una prueba si se rompe |

: Tabla D.16. Ideario (esquema 16)

**Lo que no se pudo comprobar en la sesión:**
- El código se compila y se prueba en GitHub Actions; no se probó en un teléfono qué texto y qué asunto mandan de verdad YouTube, Kindle y el navegador al compartir.

## Pantallas de captura (esquema 17)

Pedido del autor (3 nov 2026): revisar todas las pantallas donde se anota a mano. Decisiones del autor:
- Los sueños se escriben en el libro de noche del tema, como el diario, y sus opciones dejan de pisarse.
- Más emociones en los momentos.
- El Ideario tiene un campo de temas y su propio fichero.
- Se arreglan las demás pantallas revisadas.

Las tres escalas 1–7 de la hoja de la noche no cambian. `SCHEMA_V` = 17. La versión indicada en el diccionario (`Diccionario.DESDE`) es la primera que puede traer el cambio.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.127 | **El «tipo» de sueño se parte en dos preguntas.** «¿Qué tan raro fue?» (`sueno_rareza`: realista, raro pero posible, imposible o surreal) y «¿Fue pesadilla?» (`sueno_pesadilla`). «Recurrente» queda solo en la estructura (`est_recurrente`). Los días viejos se traducen al leerlos (`Catalogo.rarezaDe`, `pesadillaDe`, `categoriasSuenoDe`) y al corregirlos. Las pesadillas del reporte y de la Biblioteca de sueños, la familia `suenot_`, `sueno_<categoría>`, la exportación y la importación usan la traducción. **Cambia valores:** `suenot_mundano`, `suenot_ansioso_pesadilla`, `suenot_bizarro_surreal` y `suenot_recurrente` desaparecen, y aparecen `suenot_realista`, `suenot_raro`, `suenot_imposible` y `suenot_pesadilla` | `data/Catalogo.kt`, `data/Modelo.kt`, `ciencia/Dinamicas.kt`, `ciencia/Estadistica.kt`, `ciencia/Terapia.kt`, `data/Exportar.kt`, `data/Importar.kt`, `data/Diccionario.kt`, `ui/LibroDiario.kt`, `ui/pantallas/Bibliotecas.kt` | Las cuatro opciones mezclaban rareza, angustia y repetición, eran excluyentes y «recurrente» repetía una categoría (capítulo 3, sección 3.12) |
| D.128 | **Ejes del sueño.** Se agregan tres ejes: cómo me fue, sensaciones y al despertar. Hay opciones nuevas en personajes, escenario, interacción, temas, estructura y emociones, que pasan a llamarse «Emociones del sueño». Algunas etiquetas cambian para no confundirse. «Solo fragmentos» sale de la estructura porque repetía la claridad del recuerdo. La vividez se rotula «Qué tan claro lo recuerdas», con los mismos ids. Las categorías a la vista son las 8 más usadas en 180 días (`Catalogo.suenoRapidas`) | `data/Catalogo.kt`, `ui/CategoriasSueno.kt` | Pedido del autor: faltaban opciones y había solapamientos |
| D.129 | **Un solo formulario del sueño.** Antes estaba copiado en el Día y en la pantalla rápida. La narración se escribe en el libro de noche del tema, a pantalla completa (`DialogoEscritor` en modo sueño); en la pantalla rápida va primero | `ui/CategoriasSueno.kt` (`FormularioSueno`), `ui/pantallas/Dia.kt`, `ui/QuickActivity.kt` | Pedido del autor: el cuadro de texto se quedaba corto |
| D.130 | **Más emociones y su intensidad.** `Catalogo.SENTIMIENTOS` pasa de 25 a 47 emociones (9 agradables, 10 desagradables y 3 mixtas nuevas, con los mismos ids de antes). Hay un solo selector (`SelectorEmociones`) en el registro rápido y al corregir: encabezados por tono, primero las más usadas, «otro…» con las que ya escribiste y «no sé» excluyente. Al corregir faltaban «otro» y la exclusión de «no sé». Intensidad opcional por emoción, de 1 a 3 (`Momento.intensidades`, solo exportada) | `data/Catalogo.kt`, `data/Modelo.kt`, `ui/SelectorEmociones.kt`, `ui/QuickActivity.kt`, `ui/pantallas/Momentos.kt`, `data/Exportar.kt` | Pedido del autor: faltaban emociones |
| D.131 | **Pensamientos múltiples.** «¿En qué piensas?» deja marcar varios (`Momento.pensamientos`), con cuatro opciones nuevas y una línea que distingue preocupación, darle vueltas y autocrítica. En modo discreto llevan letra; antes todos eran «·» e indistinguibles. `n_<pensamiento>` cuenta un registro en cada pensamiento que tenga. La regla de presencias («pensé en») usa la lista | `data/Catalogo.kt` (`pensamientosDe`), `ciencia/Estadistica.kt`, `ciencia/Terapia.kt`, `data/Presencias.kt`, pantallas que los muestran | Revisión de pantallas: eran de una sola opción y se pisaban |
| D.132 | **Con quién y cuándo se crea el registro.** Se agregan hijos, mascota y «por mensaje o llamada», que puede ir con «solo». «Compañeros» pasa a «compañeros de trabajo o escuela». Marcar solo «con quién» ya no crea un registro de ánimo. Al corregir un momento se pueden cambiar con quién, los detalles de la recaída (detonantes, qué hacías antes, aparato) y la libido (0–5 con su foco; antes salía 1–5 sin anclas) | `data/Catalogo.kt` (`alternarConQuien`), `ui/QuickActivity.kt`, `ui/pantallas/Momentos.kt` | Revisión de pantallas |
| D.133 | **Hoja de la noche.** Los hábitos se contestan con «sí», «no» o sin contestar (`Dia.habitos_tri`). En esos días, un hábito sin respuesta queda vacío en `hab_*` (resuelve en parte H-2-4). La barra cuenta lo que pide «Sellar» (ánimo y diario), y la predicción y los hábitos van como extras. El contexto se llama «Cómo fue el día», tiene tres opciones nuevas (`ctx_casa`, `ctx_fiesta`, `ctx_tramites`), «Romántico» pasa a «Cita o tiempo en pareja» y hay un «otro» escrito (`ctx_otro`). Los hitos se guardan como se escriben (sin `_`). En la lectura del día, «Me rebasó (ansiedad)» y «Me quedó chico (aburrimiento)» con los mismos ids, y «no aplica» en la evaluación de Lazarus. Los detonantes tienen nombre con acentos y un «otro» escrito (`detonante_otro`) | `ui/pantallas/Dia.kt`, `data/Catalogo.kt`, `data/Modelo.kt`, `ciencia/Estadistica.kt`, `data/Exportar.kt`, `data/Recaidas.kt`, `ui/pantallas/Recaidas.kt` | Revisión de pantallas; decisión del autor de aclarar la lectura del día |
| D.134 | **Temas del Ideario y su fichero.** Las ideas tienen un campo «Temas» (`Idea.temas`) con fichas de los temas que ya usas. Antes el tema solo se podía poner escribiendo `#tema` en el texto, y las fichas solo salían si ya existía alguno. Los temas de una idea son los del campo más los `#temas` del texto (`Ideario.temasDe`). Eso usan los cajones, la búsqueda y `vm_ideas.csv:temas`. El Ideario se ve como un fichero con un cajón por tema (`FicheroIdeas`); un cajón abierto muestra sus fichas, que se pasan deslizando (`PantallaCajonIdeas`) | `data/Modelo.kt`, `data/Ideario.kt`, `data/Exportar.kt`, `ui/pantallas/Ideario.kt`, `ui/pantallas/Fichero.kt` (nuevo), `ui/MainActivity.kt` | Pedido del autor: no había forma visible de ponerle tema a una idea, y quería un diseño propio por tema |
| D.135 | **La pestaña Diario muestra las cinco bibliotecas,** cada una con su objeto, en este orden: el estante del diario, la libreta de momentos, el estante de sueños, el fichero del Ideario y las cartas. Las pestañas de las bibliotecas siguen el mismo orden. Antes solo estaba el estante del diario y botones hacia las demás | `ui/pantallas/Diarios.kt`, `ui/pantallas/Bibliotecas.kt`, `ui/pantallas/LibretaMomentos.kt` | Pedido del autor |
| D.136 | **Gastos.** El monto acepta coma decimal (`Dinero.limpiarMonto`); antes «12,50» se guardaba como 1250. Las categorías de ingreso se pueden agregar, renombrar (con sus ingresos, si quieres), ordenar y quitar. Vivienda y Servicios entran a las categorías una sola vez (`Config.categorias_v17`). Los gastos tienen «¿se repite?» (`Gasto.se_repite`, solo se exporta). La nota es de varias líneas y el título del diálogo sigue la fecha elegida | `data/Dinero.kt`, `data/Modelo.kt`, `data/Catalogo.kt`, `ui/pantallas/Gastos.kt`, `ui/QuickActivity.kt`, `data/Exportar.kt` | Revisión de pantallas; la coma perdía datos |
| D.137 | **Consumos.** En alcohol y cafeína, «quitar el último» y «poner en 0» (antes la ficha se llamaba «borrar» y solo se podía sumar); en las demás, − y +. En todas se puede escribir el total con decimales. Al completar o corregir se pueden cambiar con quién, cómo, la cantidad de cualquier sustancia y el nombre de «otra». «Cruda o bajón» dice «al día siguiente». «No sé» en la hora de fin la deja vacía. Síntomas: + «Otro» | `ui/QuickActivity.kt` (`CantidadConsumo`, `CompletarConsumo`), `data/Catalogo.kt` | Revisión de pantallas |
| D.138 | **Escalada no borra sesiones con datos.** Al abrir la pantalla se borraban las sesiones sin bloques, nota, enlace al reloj ni energía, aunque tuvieran lugar, duración, piel, esfuerzo, satisfacción o dolor. Ahora solo se borran las que tienen todos los campos vacíos. El título dice «Sesión del ‹fecha›» al editar. Se ve la descripción del estilo elegido. Los rasgos tienen nombre con acentos. «Dolor o lesión» es de varias líneas | `ui/pantallas/Escalada.kt`, `data/Calculos.kt` (`Grados.nombreRasgo`) | Error que perdía datos |
| D.139 | **Gente.** Relaciones: sale «amistad cercana» (se ve como amistad y conserva su id) y entran roomie, vecino y profesional de salud. Las fechas de nacimiento y fallecimiento se eligen en el calendario, con «sin año». Una persona nueva se crea en un solo diálogo | `data/Personas.kt`, `ui/pantallas/Gente.kt` | Revisión de pantallas |
| D.140 | **Metas y logros propios.** El umbral puede ser «al menos» o «como mucho» (`Meta.umbral_max`, `LogroPropio.umbral_max`). Los logros propios se pueden editar. El objetivo conserva sus decimales. El horizonte sale de la fecha objetivo cuando hay una | `data/Metas.kt`, `ciencia/Logros.kt`, `ui/pantallas/Lote2.kt`, `ui/pantallas/Logros.kt`, `ui/pantallas/Trofeos.kt` | Revisión de pantallas (capítulo 9) |
| D.141 | **Suplementos propios.** «＋ otro suplemento» crea uno fuera del catálogo («solo porciones», ajustable). Al editar, los números que no se entienden se marcan y no se guardan; antes volvían en silencio al valor anterior | `ui/pantallas/Lote3.kt` | Revisión de pantallas |
| D.142 | **Lugares.** Un solo juego de campos (nombre, tipo, radio de 50 a 400 m en pasos de 25) en las tres formas de marcar un lugar (`CamposLugar`). En el mapa ya no se cierra sin nombre y se puede elegir el radio. Tipos nuevos: restaurante o café, y tienda. En una visita se corrigen la llegada y la salida, y «no estuve aquí» pide confirmación. La detección automática no pisa la corrección: solo cierra visitas abiertas y agrega las que no se enciman | `ui/pantallas/Medir.kt`, `ui/pantallas/Lote2.kt`, `ui/pantallas/Mapa.kt`, `data/Catalogo.kt` | Revisión de pantallas |
| D.143 | **Obras, enfoque y mundo.** Obras: tipos documental, podcast, audiolibro, cómic y álbum (sin buscador; se añaden a mano), estado «pausado» y citas editables. Enfoque: tipo «otro» con nombre (`Tramo.tipo_otro`; en el panel, `enfoque_otro_min`) y duraciones de hasta 4 h. Mundo: «hasta» no puede quedar antes de «desde», las fechas se pueden quitar y las ciudades anotadas a mano se pueden editar | `ui/pantallas/Biblioteca.kt`, `ui/pantallas/EnfoqueSiestas.kt`, `data/Catalogo.kt`, `ui/pantallas/Mundo.kt` | Revisión de pantallas |
| D.144 | **Consistencia de los formularios.** Los diálogos de corrección (gasto, ingreso, tramo, toma, nota, momento y visita) tienen «cancelar» abajo; «borrar» va adentro, en rojo y con confirmación. Antes, en varios, el único botón de salida era «Borrar». Las palabras son las mismas en todos («Guardar», «cancelar», «borrar»). «Lo mejor» y «lo peor» ya no tienen etiquetas en latín fijas. Las listas de etiquetas (medicamentos, hitos, nombres de un sueño, temas de una idea) no aceptan repetidos. Un recordatorio sin día avisa que no sonará | `ui/pantallas/Gastos.kt`, `EnfoqueSiestas.kt`, `Lote2.kt`, `Lote3.kt`, `Momentos.kt`, `Medir.kt`, `Dia.kt`, `ui/Componentes.kt` | Revisión de pantallas. No cambia ningún cálculo |

: Tabla D.17. Pantallas de captura (esquema 17)

## Deseados, listas y momentos en los reportes (esquema 18)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.145 | **Deseados y listas libres.** Las obras pendientes («Quiero leer y ver», `PantallaDeseados`) llevan qué tan pronto las quieres (`Obra.prioridad`: pronto, cuando pueda, algún día), quién te las recomendó (`Obra.recomendo`) y dónde verlas (`Obra.donde`); se añaden con solo el título y se ordenan por prioridad y luego por fecha. Nueva colección `listas` (`Lista`, `ElementoLista`) para lo que no es una obra, con fecha de agregado y de hecho. Nada de esto entra al panel; se exporta en `vm_biblioteca.csv` (tres columnas) y `vm_listas.csv` (A.12) | `data/Modelo.kt`, `data/Repo.kt`, `data/Exportar.kt`, `data/Diccionario.kt`, `ui/pantallas/Biblioteca.kt`, `ui/pantallas/Listas.kt`, `assets/vestigium.R` (`vm$listas`) | Pedido del autor: una forma más establecida de llevar la lista de deseados, y listas aparte para lo demás. **[Diseño propio]**: sin variables de panel, porque no describen el día y solo sumarían comparaciones |
| D.146 | **Lo nuevo de los momentos en pantallas y reportes.** Las intensidades, la lista de pensamientos y «con quién» (esquema 17) se muestran donde aparecen los registros (Tu día, la línea de tiempo del Diario, la lista de registros, la cinta, la libreta de momentos: «Tristeza (mucho)», «pensando: …», «con …»). Los reportes semanal, mensual y anual, Semana, Año y «Tu año» suman las emociones más nombradas (con sus «mucho»), los pensamientos y con quién (`Periodo.animoDe`, 9.2). El reporte para terapia nombra «otro» con lo escrito, anota los «mucho» y cuenta el detonante «otro» con su texto, igual que la pantalla Privado. En R, `vm_emociones()` (Apéndice C). **No cambia el panel**: `n_*`, `con_*`, `acompanado_pct` y la regla de terapia de contar «con quién» solo en los avisos siguen igual. La libreta de momentos mostraba el identificador del lugar en vez de su nombre; ya muestra el nombre | `data/Catalogo.kt` (`tituloDetallado`, `detalleMomento`, `conQuienTexto`), `ciencia/Periodo.kt`, `ciencia/Terapia.kt`, `ciencia/TuAnio.kt`, `data/Recaidas.kt`, `reportes/Reportes.kt`, `ui/pantallas/` (Dia, Diarios, Momentos, Cinta, LibretaMomentos, Bibliotecas, Semana, Anno), `assets/vestigium.R` | Pedido del autor: los momentos guardan más que antes y no se veía en los reportes. Conteos para leer, sin pesar por intensidad. **[Diseño propio]** |

: Tabla D.18. Deseados, listas y momentos en los reportes (esquema 18)

## Ubicaciones y visitas limpias (esquema 19)

Decisiones del autor (5 nov 2026):
- Corregir también lo ya guardado, no solo lo nuevo.
- Una visita cuenta desde 8 minutos.
- Cada visita guarda cómo se supo su salida.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.147 | **Cuándo termina una visita.** Si Android no avisa la salida de una geocerca, la salida se deduce. Llegar a un lugar lejano (círculos que no se tocan) cierra la visita abierta de otro. Dos puntos seguidos fuera del círculo, aun restando su imprecisión, la cierran en cada pulso. Un aviso de salida tardío cede ante los puntos si estos dicen que saliste al menos 15 min antes. Después de deducir una salida, las geocercas se registran de nuevo para que Android avise al volver, y una vuelta que ya pasó sale de las ubicaciones (`reconstruirEn`). Una visita de menos de 8 min se quita; si esa visita había cerrado la de un lugar donde seguías, esa se reabre. El mínimo de las reconstruidas baja de 10 a 8 min. Nueva columna `cierre` en `vm_visitas.csv` (`geocerca`, `otro_lugar`, `ubicaciones`, `reconstruida`, `manual`). Una corrección única aplica todo esto a la historia. Las geocercas ya no se registran cada vez que arranca el proceso, y al registrarse avisan también de dónde ya no estás (`INITIAL_TRIGGER_EXIT`) (4.4.3) | `data/Lugares.kt`, `data/Repo.kt` (`entrada`, `salida`, `cambiarVisitas`), `data/Modelo.kt` (`Visita.cierre`), `sensores/Ubicacion.kt`, `App.kt`, `avisos/Trabajo.kt`, `data/Exportar.kt`, `data/Diccionario.kt`, `ui/pantallas/Medir.kt` | Revisión del 5 nov 2026: visitas que quedaban abiertas decenas de horas, encimadas con las de otros lugares, y visitas de segundos. **Cambia valores** de `en_casa_min`, `horas_tipo_*` y `lug_*` hacia atrás. **[Diseño propio]** |
| D.148 | **Puntos repetidos.** La muestra de fondo y las geocercas no guardan un punto que ya está (misma hora, latitud y longitud), y la petición de ubicación se cancela al acabarse la espera. Una corrección única quitó los repetidos de las fuentes «app», «geocerca» y «momento» (4.4.2) | `data/Db.kt` (`ponerMuestra`, `quitarMuestrasRepetidas`), `sensores/Ubicacion.kt`, `avisos/Trabajo.kt` | H-3-8 (a): el mismo punto viejo quedaba guardado una vez por pulso mientras Android no tenía una lectura nueva |
| D.149 | **Cortes del día con cambio de horario.** Donde se sumaba «medianoche + 5 h» en milisegundos, se usa el inicio o el fin del día lógico con su zona (`Fechas.inicioDiaLogico`, `finDiaLogico`). La noche en que se atrasa el reloj, ese corte caía a las 04:00, todavía del mismo día lógico. En `en_casa_min` y en la actividad detectada (`Actividad.acumular`), el ciclo no terminaba nunca y la app se quedaba trabada. En el resto (galería, llamadas, mapa, reportes, suplementos), el corte se corría una hora | `ciencia/Estadistica.kt`, `data/Lugares.kt`, `sensores/Actividad.kt`, `Galeria.kt`, `Comunicacion.kt`, `data/Lote3.kt`, `reportes/Reportes.kt`, `ui/pantallas/` (Mapa, Recuerdos, LibretaMomentos, Lote3) | Revisión del 5 nov 2026. No afecta a quien vive en una zona sin horario de verano |

: Tabla D.19. Ubicaciones y visitas limpias (esquema 19)

## Que no se pierda nada (5 nov 2026)

No cambia lo que se guarda: `SCHEMA_V` sigue en 19.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.150 | **Restaurar sin pisar lo del teléfono.** Lugares, pantalla, entorno, fotos, agenda y sesiones: del respaldo solo entra lo que no está. Cartas: una ya abierta en el teléfono no se vuelve a sellar; una abierta en el respaldo queda abierta. Los días del respaldo se mezclan en el hilo principal, sobre lo que haya en ese momento. Los avisos de ánimo restaurados pasan a la copia de trabajo de la tarea de fondo. La fuerza de referencia se calcula con los entrenos del respaldo (10.7, 10.9) | `data/Importar.kt`, `data/Repo.kt` (`importarDias`, `importarAvisos`, `completarFuerzaReferencia`) | La pantalla promete «lo que ya tengas aquí no se duplica ni se borra», y el código decía «sin pisar lo tuyo», pero estas colecciones las pisaba el respaldo |
| D.151 | **Respaldo a la carpeta sin dejarlo a medias**: se escribe aparte y luego se renombra (10.5.1) | `data/Respaldos.kt` (`escribirSinRomper`) | Antes se escribía encima del único respaldo; un corte a la mitad lo dejaba truncado |
| D.152 | **La tarea de fondo.** Cada paso de las tandas diaria, nocturna y mensual se anota al terminar; si la tarea se corta, el siguiente pulso sigue donde se quedó (dos intentos por paso). Antes, la tanda se daba por hecha al empezar. Los errores ya no atrapan la orden de detenerse: antes, lo que seguía, el respaldo incluido, corría sin la protección del sistema. `Repo.cambiarConfig` es sincronizado: la tarea de fondo y la interfaz ya no se borran cambios una a otra, por ejemplo una persona que agregaste o la marca de un aviso, que entonces sonaba dos veces (4.1) | `avisos/Trabajo.kt` (`paso`, `intentar`), `data/Repo.kt` | Revisión del 5 nov 2026 |

: Tabla D.20. Que no se pierda nada (5 nov 2026)

## Cómo se mide (esquema 20, 5 nov 2026)

Decisión del autor (5 nov 2026): los avisos de ánimo, como sea mejor metodológicamente, es decir, a su hora exacta, guardando cuándo salieron y con las dos latencias por separado.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.153 | **Avisos de ánimo a su hora.** Cada aviso tiene una alarma exacta (`Muestreo.programar`, `MuestreoReceiver`) y el pulso queda de respaldo. Cada aviso mostrado guarda su hora real (`AvisosDia.reales`, `SCHEMA_V` 20). En `vm_momentos.csv`: `mostrado_ts` (nueva); `latencia_min` desde la hora real (vacía antes del esquema 20); `latencia_plan_min` (nueva), la medida de antes. Un aviso solo cuenta como mostrado si las notificaciones de la app y el canal de ánimo están activos (`Avisos.animoVisible`). Al cambiar de zona horaria, el plan del día se rehace (3.5.3) | `avisos/Muestreo.kt` (nuevo), `avisos/Trabajo.kt`, `avisos/Avisos.kt`, `data/Modelo.kt`, `data/Exportar.kt`, `data/Diccionario.kt`, `data/Repo.kt`, `AndroidManifest.xml` | Los avisos salían con el pulso, entre 0 y ~15 min tarde y más con el teléfono en reposo: se sesgaba la muestra hacia los ratos de uso, y la latencia incluía el retraso del propio aviso. **Cambia valores** de `latencia_min` |
| D.154 | **Ceros de los momentos en `vm_panel.csv`.** `n_momentos`, `m_*` y `n_muestreos` valen 0 solo desde el primer registro de su tipo y en días con diario (la regla P-15 del panel, `Estadistica.ceroDesde`); antes, sin dato (6.3.4) | `data/Exportar.kt` (`filasPanel`) | Se exportaban en 0 en todas las fechas, también antes de empezar a registrar: los días anteriores contaban como «sin momentos» y bajaban medias y correlaciones. **Cambia valores** |
| D.155 | **Periodos y repartos.** Los reportes de la semana y el mes en curso llegan hasta hoy (`Reportes.imagenSemana`, `mensual`; como ya hacía `imagenMes`). `lug_*` reparte cada visita entre los días lógicos que cruza, como `horas_tipo_*`. La energía de reportes, «Tu año» y Terapia es solo la del diario, como la valencia (H-8-10) | `reportes/Reportes.kt`, `ciencia/Dinamicas.kt`, `ciencia/Periodo.kt` | Dividían entre días que no habían pasado; las horas por lugar no cuadraban con su total; «Energía media» mezclaba dos instrumentos. **Cambia valores** de `lug_*` |
| D.156 | **Detalles de cálculo.** La primera y la última comida de MacroFactor ponen lo de antes de las 05:00 al final, y `ultima_comida_h` suma 24, como `cafeina_ultima_h` (se rehizo lo importado). 1RM con 1 repetición = el peso (Epley daba 3.3 % de más; se recalculó `fuerza_ref_kg`). «Tu año»: música y kilómetros por día con dato de la fuente (P-15), y la comparación con el año anterior funciona el 29 de febrero. Totales muestra la cafeína en mg. Logros: el avance de «Rayo» sin unidad engañosa, y «Remontada» con 3 días de calendario | `data/Macrofactor.kt`, `data/Exportar.kt`, `data/Repo.kt` (`rehacerHorarioComidas`), `ciencia/Estadistica.kt`, `ciencia/Terapia.kt`, `ciencia/TuAnio.kt`, `ciencia/Totales.kt`, `ciencia/Logros.kt`, `ui/pantallas/TusApps.kt`, `avisos/Trabajo.kt` | Revisión del 5 nov 2026. **Cambia valores** de `primera_comida_h`, `ultima_comida_h` y `fuerza_ref_kg` en los días afectados |

: Tabla D.21. Cómo se mide (esquema 20, 5 nov 2026)

## Memoria y velocidad (5 nov 2026)

No cambia lo que se guarda ni cómo se calcula: `SCHEMA_V` sigue en 20.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.157 | **Respaldo, exportación y restauración por partes.** El .zip se escribe entrada por entrada (el JSON con el codificador de flujo, las ubicaciones y el catálogo de fotos punto por punto); el cifrado y el descifrado van de archivo a archivo; al restaurar o verificar, el archivo se copia y se lee por partes. Un elemento ilegible del respaldo se salta y se avisa, en vez de tumbar la restauración entera; momentos, respuestas, reacciones, salud y canciones se restauran en lote. Si el respaldo a la carpeta lleva más de 3 días fallando, llega una notificación por semana (10.4, 10.5.1, 10.6, 10.7) | `data/Respaldos.kt`, `data/Exportar.kt` (`archivos`, `escribirRespaldo`, `escribirUbicaciones`), `data/Importar.kt` (`leer`, `tolerante`), `data/Repo.kt`, `data/Db.kt`, `data/Drive.kt`, `avisos/Trabajo.kt`, `avisos/Avisos.kt` | Con años de ubicaciones la app se quedaba sin memoria, y lo primero que fallaba era restaurar en un teléfono nuevo; el respaldo automático dejaba de salir sin aviso |
| D.158 | **Arranque y pantallas.** Al abrir, las colecciones se leen en paralelo (`Repo.init`). «Cruzar dos variables», el conteo de palabras del diario, la lista del diario y el calendario ya no recalculan todo en cada redibujo; la imagen del Hall se dibuja en otro hilo. En Art déco y Edo (decisión del autor), el fondo vivo, el papel tapiz y el emblema van en capas propias y el fondo vivo se dibuja a unos 30 cuadros por segundo | `data/Repo.kt`, `ui/pantallas/Explorar.kt`, `ui/pantallas/Diarios.kt`, `ui/pantallas/Calendario.kt`, `ui/pantallas/Hall.kt`, `ui/Animaciones.kt` (`animacionLigera`), `ui/Decoracion.kt`, `ui/MainActivity.kt` | El arranque se alargaba con cada año de datos; algunas pantallas se trababan al escribir o al tocar |

: Tabla D.22. Memoria y velocidad (5 nov 2026)

## Detalles (6 nov 2026)

No cambia lo que se guarda: `SCHEMA_V` sigue en 20.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.159 | **Datos.** Las fechas-hora llevan siempre segundos (`Fechas.ahoraIso`, `isoDeMillis`; capítulo 2). El tramo de actividad que quedó abierto se descarta al encender el teléfono (4.4.4). Health Connect: el peso, la grasa, la respiración, las temperaturas, la presión, el VO₂ máx. y las sesiones de ejercicio se leen en todas sus páginas, no solo en la primera (`Salud.leer`) | `data/Fechas.kt`, `avisos/Trabajo.kt` (`ArranqueReceiver`), `sensores/Salud.kt` | Excel y Python no leían las horas sin segundos; tras apagar el teléfono se sumaban horas de actividad que no pasaron; con más de 1000 registros se perdían los siguientes |
| D.160 | **Pantalla.** Singular y plural según la cifra en unas 250 frases (`porNumero`: «1 día», no «1 días»). Las cifras de los recuadros y los números de la escala de 0 a 10 se achican si no caben, en vez de cortarse; la barra de pestañas crece con la letra grande del sistema y TalkBack dice cuál está elegida. Momentos muestra de 200 en 200 con «ver más». Dos cierres posibles corregidos (Medir, Gastos). Los widgets se actualizan uno por uno aunque falle otro. La presentación de Art déco recorta sus rayos al marco. Otra app ya no puede abrir la captura rápida saltándose el candado | `data/Texto.kt` (`porNumero`), `ui/Componentes.kt`, `ui/MainActivity.kt`, `ui/pantallas/Momentos.kt`, `ui/pantallas/Medir.kt`, `ui/pantallas/Gastos.kt`, `widget/Widgets.kt`, `ui/Musica.kt`, `AndroidManifest.xml` y unas 50 pantallas | Detalles de la revisión del 5 nov 2026 |

: Tabla D.23. Detalles (6 nov 2026)


# Apéndice E. Glosario

_Reúne, en orden alfabético, los términos que cada capítulo explica en su mini-glosario, con el capítulo donde se explican. Cuando un término aparece en dos capítulos, se dan las dos explicaciones, porque cada una lo dice desde su tema. Se genera con `ensamblado/glosario.py`; no se edita a mano._

| Término | Qué significa | Cap. |
|---|---|---|
| **Absorción** | El paso de la sustancia del estómago a la sangre. Tarda un rato: por eso un café no hace efecto en el segundo en que lo tomas. | 5 |
| **Activación** | Cuánta **energía o agitación** hay en el cuerpo y la mente, sin importar si es agradable. De 1 (muy baja: agotado) a 7 (muy alta: acelerado). En inglés, *arousal*. | 3 |
| **AES-256** | El algoritmo de cifrado estándar más usado; «256» es el largo de la llave en bits. | 10 |
| **Aleatorizar (sortear)** | Decidir al azar qué periodos llevan la acción. Evita que tú elijas, sin querer, hacerla justo cuando ya ibas a estar mejor. | 8 |
| **Ancla** | El texto que acompaña a un número de una escala para que signifique lo mismo cada vez (p. ej., «4 = Neutral: plano»). | 3 |
| **Android Keystore** | Un almacén de llaves del sistema: la app puede usar una llave para cifrar, pero no puede sacarla del teléfono. | 10 |
| **AR(1)** (autocorrelación de orden 1) | Supuesto de que el parecido entre días decae de un día a otro de forma fija: lo que importa es cuánto se parece cada día al anterior. La app lo usa para corregir los intervalos de las correlaciones (n efectivo) y de las diferencias de medias (sección 6.6 y 6.15). | 6 |
| **Arrastre** (*carryover*) | Que el efecto de una condición siga actuando cuando ya empezó la otra. El lavado existe para evitarlo. | 8 |
| **Autocorrelación** | (cap. 6) Qué tanto el valor de hoy se parece al de ayer. Si duermes poco una semana entera, los días de esa semana se parecen entre sí. (cap. 8) Que un día se parezca al anterior (si hoy estás bien, mañana probablemente también). La autocorrelación de orden 1, *ρ*, va de −1 a 1; 0 significa días independientes. | 6, 8 |
| **Autorreporte** | Una respuesta que das tú sobre ti mismo. Es la única forma de medir lo subjetivo, y tiene sesgos conocidos (sección 3.4). | 3 |
| **Autoseguimiento** (*self-tracking*) | Registrar de forma sistemática datos sobre uno mismo: ánimo, sueño, pasos, gastos. Puede ser a mano (un diario) o con sensores (un reloj). | 1 |
| **Benjamini-Hochberg (BH) y q** | Una corrección para comparaciones múltiples. q < 0.10 quiere decir, aproximadamente: «de todas las relaciones que marco como firmes, espero que a lo más un 10 % sean azar». | 6 |
| **β estandarizado** | En un modelo con varias causas posibles: cuántas desviaciones estándar cambia el objetivo cuando un predictor sube una desviación estándar, con los demás fijos. | 6 |
| **Campaña** | Un reto opcional de lunes a domingo («5 noches de 7 horas»). | 9 |
| **Cegamiento** | Que quien recibe la acción no sepa si la está recibiendo (como con una pastilla de azúcar). En estos experimentos casi nunca es posible. | 8 |
| **Cercanía (IOS)** | Qué tan cerca sientes a alguien, de 1 a 7, eligiendo entre siete pares de círculos cada vez más encimados (Aron et al., 1992). | 7 |
| **Cifrar** | Convertir un archivo en algo ilegible sin una llave. | 10 |
| **Co-mención** | Que dos personas aparezcan mencionadas **el mismo día lógico**, aunque sea en textos distintos. Es la base de las líneas de la Constelación y de la Red. | 7 |
| **Comparaciones múltiples** | (cap. 1) Revisar muchas preguntas a la vez. Cuantas más se revisan, más «hallazgos» salen por puro azar (sección 1.6). (cap. 6) Revisar muchas parejas a la vez. Con 1000 parejas sin relación, unas 50 tendrán p < 0.05 por azar. | 1, 6 |
| **Componente** | Un grupo de nodos conectados entre sí por algún camino; dos componentes no tienen ninguna línea entre ellas. | 7 |
| **Condición** | Cada una de las dos situaciones que se comparan: **con** la acción («SÍ») o **sin** ella («no»). | 8 |
| **Confusor** | Una tercera cosa que mueve a las dos variables que se comparan, como el día de la semana o estar enfermo. Las protecciones de la app no lo eliminan. | 6 |
| **Constancia** | Cumplir algo **la mayoría** de los días de una ventana (por ejemplo, 25 de 30), sin necesidad de racha. | 9 |
| **Correlación (r)** | Un número de −1 a 1 que dice qué tanto dos variables suben y bajan juntas. +1: suben juntas siempre en línea recta; 0: ninguna relación lineal; −1: cuando una sube, la otra baja. | 6 |
| **Covariable que cambia con el tiempo** (*time-varying covariate*) | Un dato de contexto que no es fijo, sino que tiene un valor por periodo; por ejemplo, la relación que tenías con una persona cada día. | 7 |
| **d de Cohen** y **g de Hedges** | La diferencia entre las dos condiciones medida en «desviaciones estándar»: cuántas veces el vaivén normal de un día a otro (Cohen, 1988). La *g* de Hedges es la misma cifra con una pequeña corrección que quita lo que se infla con pocos días (Hedges, 1981); la app la usa desde 10e. | 8 |
| **Dato faltante (`null`)** | «No sé» para ese día. No es lo mismo que 0: 0 minutos de ejercicio es un dato; no tener reloj ese día es un faltante. | 6 |
| **Dato importado** | Lo que llega de un archivo que tú exportas de otro servicio y cargas en la app (Spotify, Google, WhatsApp…). | 4 |
| **Dato pasivo** | Lo que el teléfono o el reloj registran solos (pasos, pantalla, ubicación…). Tú no haces nada. | 4 |
| **Densidad** | Qué fracción de todas las líneas posibles existe. Con *n* personas hay *n(n−1)/2* pares posibles. | 7 |
| **Desfase** | Comparar lo de hoy con lo de **mañana** (¿dormir hoy se asocia con el ánimo de mañana?). | 6 |
| **Desviación estándar (DE)** | Qué tan lejos del promedio suelen caer los valores. | 6 |
| **Día de calendario** | El día normal, de 00:00 a 24:00. | 4 |
| **Día lógico** | (cap. 3) El día de la app: empieza a las 05:00 (capítulo 2). Un registro a la 01:00 cuenta para el día anterior. (cap. 4) El día de la app: empieza a las 05:00 (capítulo 2, sección 2.3). Un evento a las 01:00 cuenta para el día *anterior*. (cap. 7) El día de la app: empieza a las 05:00 (capítulo 2). | 3, 4, 7 |
| **Diseño ABAB (alterno)** | Alternar periodos sin la acción (A) y con ella (B) varias veces. En la app: semanas alternas, en un orden sorteado. | 8 |
| **Diseño antes/después (AB)** | Un solo periodo sin la acción y luego uno con ella. | 8 |
| **Eliminación** | Cómo el cuerpo la va quitando (sobre todo el hígado). | 5 |
| **Eliminación de orden cero** | Se va una **cantidad fija** por hora, sin importar cuánto haya. El alcohol se elimina así casi siempre, porque la enzima que lo procesa trabaja a su máxima velocidad desde los primeros tragos (Jones, 2010). | 5 |
| **Entre personas / dentro de la persona** | Una relación **entre personas** compara a unas personas con otras (Ana duerme más que Beto). Una relación **dentro de la persona** compara los días de alguien consigo mismo (Ana duerme más los sábados que los martes). Pueden ir en direcciones distintas (sección 1.4.2). | 1 |
| **Estacionario** | Un proceso cuyas reglas no cambian con el tiempo: su promedio, su variabilidad y sus relaciones son las mismas este año que hace cinco. Una vida casi nunca lo es del todo. | 1 |
| **Estado subjetivo** | Lo que solo tú puedes observar: cómo te sientes, qué piensas, qué tan estresado o solo te sientes. No hay sensor que lo mida; hay que preguntarlo. | 3 |
| **Diseño de caso único / N-de-1** | (cap. 1) Un estudio con **una sola persona** que se observa o se compara consigo misma muchas veces. «N» es el número de participantes; aquí, 1 (capítulo 8). (cap. 8) Un experimento con **una sola persona**, que se compara consigo misma en distintos periodos: unos con la acción y otros sin ella. «N» es el número de participantes; aquí, 1. | 1, 8 |
| **Factor de Widmark** (*r*) | La fracción del peso corporal en la que se reparte el alcohol (casi toda el agua del cuerpo). | 5 |
| **Falsa señal** (falso positivo) | Que el método diga «hay un efecto» cuando en realidad no lo hay. Un intervalo del 95 % bien hecho lo hace el **5 %** de las veces. | 8 |
| **Farmacocinética** | Cómo entra, se reparte y sale una sustancia del cuerpo con el tiempo. | 5 |
| **Gamificación** | Usar elementos de juego (puntos, medallas, retos, rachas) en algo que no es un juego (Deterding et al., 2011). | 9 |
| **GCM** | Un modo de usar AES que, además de cifrar, añade una **etiqueta de autenticación**: si alguien cambia un solo bit del archivo, el descifrado falla en vez de dar datos alterados (Dworkin, 2007). | 10 |
| **Grado** | Cuántas líneas tiene un nodo: con cuántas personas está unida. | 7 |
| **Grupo de relación** | La relación simplificada que usan los análisis: `familia`, `pareja`, `amistad`, `trabajo`, `casa` y, aparte, `expareja` y las demás (sección 7.4). | 7 |
| **Health Connect** | El sistema de salud de Android, donde otras apps (por ejemplo, la del reloj) escriben sus datos. La app solo pide permisos de lectura. | 4 |
| **hPa** | Hectopascales, la unidad de presión del aire (a nivel del mar, ~1013 hPa). | 4 |
| **Huella** (*hash*) | (cap. 8) Un código corto que se calcula a partir de un texto: si cambia una sola letra del texto, la huella sale completamente distinta. La app usa SHA-256, que da 64 caracteres (la tarjeta muestra los primeros 8). Sirve para notar que algo cambió, no para saber qué cambió. (cap. 10) Un resumen de 64 caracteres que se calcula de un texto: el mismo texto da siempre la misma huella, y cambiar una sola letra da una huella completamente distinta. No esconde nada ni usa contraseña; sirve para notar si algo cambió (sección 10.9). | 8, 10 |
| **Idiográfico** | Que busca entender **a una persona concreta**: «en mis días, ¿dormir más va con mejor ánimo?». Es el enfoque de esta app. | 1 |
| **Instrumento validado** | Un cuestionario publicado cuyas preguntas, escala y forma de puntuar se han probado en estudios (miden lo que dicen medir y dan resultados estables). | 3 |
| **Integridad** | Que los datos no se pierdan, no se dañen y no cambien sin que lo sepas. | 10 |
| **Intervalo de confianza de 95 %** | (cap. 6) El rango de valores compatibles con tus datos. Si va de 0.10 a 0.47, la relación real probablemente está en ese rango; si incluye el 0, podría no haber relación. (cap. 8) Un rango de valores plausibles para el efecto. Si se repitiera el experimento muchas veces con el mismo método, el 95 % de esos rangos contendría el efecto real… **si los supuestos del método se cumplen** (sección 8.6). | 6, 8 |
| **IV (vector de inicialización)** | Un número al azar que se usa una sola vez por cifrado para que el mismo contenido no produzca el mismo resultado. | 10 |
| **Jet lag social** | La diferencia entre el punto medio del sueño en los días libres y en los laborales: como si cada fin de semana viajaras a otro huso horario y volvieras el lunes (Wittmann et al., 2006). | 5 |
| **Lavado** (*washout*) | Días que **no cuentan** después de cada cambio de condición, para que el efecto de la condición anterior se vaya (y el de la nueva aparezca). | 8 |
| **Ley de Campbell** | Cuanto más se usa un indicador para premiar o decidir, más presión hay para distorsionarlo y más deja de reflejar lo que medía (Campbell, 1979). | 9 |
| **Logro** | Una medalla que se gana al llegar a una meta (100 días escritos, un día de 20 000 pasos). | 9 |
| **Maldición del ganador** | La relación más fuerte de una lista larga casi siempre exagera su tamaño, aunque sea real: al escoger la que dio la *r* más grande, se escoge también la que tuvo más suerte (Ioannidis, 2008). | 1 |
| **Mediana** | El valor del medio al ordenar una lista. Aguanta mejor los valores raros que el promedio. | 4 |
| **Mediana / MAD** | La mediana es el valor de en medio. La MAD (desviación absoluta mediana) es la mediana de las distancias a la mediana: una DE que no se deja arrastrar por los valores extremos. | 6 |
| **Medición en la vida diaria** | Preguntar o medir **mientras la persona vive su vida**, no en un laboratorio ni de memoria: avisos en el teléfono, sensores, diarios. En la literatura se llama *ambulatory assessment* o *ecological momentary assessment* (Trull y Ebner-Priemer, 2013; Shiffman et al., 2008). | 1 |
| **Mención** | Escribir `@nombre` en un texto (diario, momento, sueño, nota, carta). La app solo «ve» a una persona si está escrita así. | 7 |
| **MNAR** | «Faltante no al azar» (*missing not at random*): cuando la razón por la que falta un dato depende del propio dato. Ejemplo: no llenar el diario precisamente los días malos (Rubin, 1976; sección 3.15). | 3 |
| **Modelo circumplejo** | Propuesta de Russell (1980): casi todas las emociones se ubican en un plano con dos ejes independientes, valencia y activación. | 3 |
| **Modelo multinivel** (jerárquico, de efectos mixtos) | Modelo que trata de forma explícita que los días están anidados en semanas, meses y etapas: estima, por ejemplo, una relación en cada año y cuánto varía de un año a otro. La app no lo usa; es trabajo futuro. | 11 |
| **MSSD** | Promedio de los cuadrados de las diferencias entre días consecutivos de calendario con ambos datos: mide la inestabilidad de un día al siguiente (von Neumann et al., 1941). | 6 |
| **Muestreo de experiencia (ESM)** | Preguntar varias veces al día, en momentos al azar, «¿cómo estás ahora?». En inglés, *experience sampling method*; casi sinónimo de *ecological momentary assessment* (EMA). | 3 |
| **n / n efectivo (nEf)** | n: cuántos días tienen ambos datos. nEf: a cuántos días **independientes** equivalen, una vez descontado que se parecen a sus vecinos. | 6 |
| **Nombre canónico** | El nombre con el que la app cuenta a una persona después de aplicar las **equivalencias** (si dijiste que `@anita` es `@ana`, el canónico de `anita` es `ana`). | 7 |
| **Nomotético** | Que busca leyes que valgan **para la gente en general**: «en promedio, quien duerme más está de mejor ánimo». Es el enfoque habitual de la psicología y la medicina. | 1 |
| **p** | La probabilidad de ver una relación así de fuerte **por puro azar** si en realidad no hubiera ninguna. | 6 |
| **Panel** | (cap. 3) La tabla de análisis: una fila por día, una columna por variable (capítulo 2). (cap. 6) La tabla con **un renglón por día** y **una columna por variable**. Casi todos los análisis salen de ahí. | 3, 6 |
| **PBKDF2** | Una función que convierte una contraseña en una llave **a propósito lenta**: repite un cálculo muchas veces (las **iteraciones**) para que probar millones de contraseñas cueste mucho (Turan et al., 2010). | 10 |
| **Pearson / Spearman** | Dos formas de calcular la correlación. Pearson usa los valores; Spearman usa su **orden** (el día con más sueño es el 1.º, el siguiente el 2.º…), así que no le afectan los valores extremos y capta relaciones que suben sin ser rectas. | 6 |
| **Periodo anterior** | El periodo de la misma duración inmediatamente antes (la semana o el mes anterior). Los reportes se comparan contra él. | 9 |
| **Prerregistro** | Escribir la pregunta, la hipótesis y cómo vas a analizar **antes** de ver los datos, para no acomodar la conclusión a lo que salga (Nosek et al., 2018). | 8 |
| **Presencia** | Si ese día **estuviste con** la persona, solo **pensaste en** ella o hablaron por **mensajes** (capítulo 3, sección 3.13). | 7 |
| **Proxy** | (cap. 4) Una medida *indirecta* de lo que de verdad nos interesa. Ejemplo: «luz fuerte del teléfono» es un proxy de «estuve al aire libre», porque no mide el aire libre, sino la luz. (cap. 5) Una medida indirecta de lo que interesa (capítulo 4). | 4, 5 |
| **PSS-4** | Cuestionario de estrés percibido: cuatro preguntas de 0 (nunca) a 4 (muy a menudo), dos de ellas inversas; total de 0 a 16. En la app, cada 7 días. | 3 |
| **Pulso** | La tarea que la app ejecuta cada ~15 minutos en segundo plano (sección 4.2). | 4 |
| **Punto medio del sueño** | La hora a la mitad entre dormirte y despertar. | 5 |
| **Quantified Self** | El nombre con que se conoce a la comunidad y la práctica de medirse a uno mismo con números, sobre todo con tecnología (Swan, 2013). | 1 |
| **Quitar la tendencia** | Comparar cada día con sus vecinos en el calendario en vez de con todo tu historial. | 6 |
| **Racha** | Días **seguidos** cumpliendo algo. Un solo día sin cumplir la rompe. | 9 |
| **Reactividad** | Que medir algo cambie lo que se mide (capítulo 3, sección 3.14). | 9 |
| **Red (grafo)** | Un dibujo de **nodos** (personas) unidos por **aristas** o **vínculos** (líneas). En matemáticas se llama *grafo*. | 7 |
| **Red egocéntrica** | La red vista desde una persona central, el **ego** (tú), con sus contactos (los **alteri**) y las relaciones entre ellos (Perry et al., 2018). Es el tipo de red de esta app. | 7 |
| **Relación** | Qué es esa persona para ti (familia, pareja, amistad…). Se guarda con **historial**: la relación que tenía cada día. | 7 |
| **Relación obvia** | Dos variables que salen de la misma fuente o se calculan una de otra (calorías y proteína). Su correlación no dice nada nuevo de ti. | 6 |
| **Reporte** | Una imagen (y, para el mes y el año, también un PDF) que resume un periodo. La app la genera sola y la guarda en el teléfono. | 9 |
| **Restricción de Burt** | Una medida de qué tanto tus contactos **se conocen entre sí** (son redundantes). Baja = tus contactos están en mundos separados y tú eres el puente entre ellos (Burt, 2004; sección 7.11). | 7 |
| **Retrospectivo** | Que se responde **después**, recordando («¿cómo estuvo tu día?»). Lo opuesto a «momentáneo» («¿cómo estás ahora?»). | 3 |
| **RMSSD** | Una forma de medir la variabilidad del corazón (sección 4.5.2). | 4 |
| **Sal** | Bytes al azar que se mezclan con la contraseña, para que la misma contraseña dé llaves distintas en cada archivo. | 10 |
| **Saturación** | Qué tan llena está una reserva (aquí, la de creatina del músculo), de 0 a 100 %. | 5 |
| **Seguimiento privado** | Impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas. Desde el módulo 10a solo se ven en la pantalla Privado y viajan a R (sección 10.3). | 10 |
| **t de Student** | La curva que reemplaza a la normal cuando la variabilidad se estima con pocos datos: tiene colas más gruesas, y más cuanto menos grados de libertad. | 6 |
| **t de Welch** | La prueba t para comparar dos promedios sin suponer que los dos grupos varían lo mismo; admite grados de libertad no enteros (Welch, 1947). | 6 |
| **Tendencia** | (cap. 6) Un cambio lento, de semanas o meses (bajas de peso poco a poco; los días se acortan en otoño). (cap. 8) Que la medida suba o baje sola con el tiempo, por algo ajeno al experimento (la estación del año, avanzar en el gimnasio, una racha). | 6, 8 |
| **Transformación z de Fisher** | z = atanh(*r*): convierte una correlación en una escala donde su error es aproximadamente normal, para calcular su intervalo y su *p* (Fisher, 1915). | 6 |
| **Valencia** | Qué tan **agradable o desagradable** se siente algo. En la app, de 1 (muy desagradable) a 7 (muy agradable). | 3 |
| **Variable** | Algo que tiene un número cada día: horas de sueño, valencia, minutos en una app, si mencionaste a una persona (1) o no (0). | 6 |
| **Vida media** (T½) | El tiempo que tarda en irse **la mitad** de lo que hay. Si la vida media es 5 h, de 100 mg quedan 50 a las 5 h, 25 a las 10 h, 12.5 a las 15 h. Esto se llama eliminación **de primer orden** (se va una proporción fija por hora). | 5 |
| **Volumen de distribución** | El «tamaño del recipiente» en el que se diluye una sustancia. Con la misma cantidad, un recipiente más chico da una concentración más alta. | 5 |
| **WHO-5** | Cuestionario de bienestar: cinco frases, cada una de 0 (nunca) a 5 (todo el tiempo). En la app, cada 7 días. | 3 |
| **z robusto** | Qué tan lejos está un día de lo habitual, medido con la mediana y la MAD en lugar del promedio y la desviación estándar, para que los valores raros no muevan la regla. La app marca como atípico un día con |z| > 3. | 6 |
| **Zona horaria y desfase con UTC** | La zona es la regla de hora de un lugar («America/Mexico_City»); el desfase, cuántas horas va ese lugar respecto a la hora universal (UTC): −6 en la Ciudad de México, +2 en Madrid en verano. Un mismo instante es las 15:30 en un sitio y las 23:30 en el otro (sección 4.3). | 4 |

: Tabla E.1. Glosario


# Referencias

Las referencias marcadas **[sin verificar: …]** no se pudieron comprobar del todo en la sesión en que se citaron; el corchete dice qué parte falta. Todas las demás se comprobaron (DOI o enlace) en su sesión; el detalle está en `referencias.md`.

::: {custom-style="Bibliografía"}
Abramowitz, M., & Stegun, I. A. (Eds.). (1964). *Handbook of mathematical functions with formulas, graphs, and mathematical tables* (Applied Mathematics Series 55). National Bureau of Standards. https://personal.math.ubc.ca/~cbm/aands/

Android Developers. (s. f.). *Location: getAccuracy()* [Página web]. Android API reference. Recuperado el 28 de octubre de 2026, de https://developer.android.com/reference/android/location/Location#getAccuracy()

Armitage, P., McPherson, C. K., & Rowe, B. C. (1969). Repeated significance tests on accumulating data. *Journal of the Royal Statistical Society. Series A (General), 132*(2), 235–244. https://doi.org/10.2307/2343787

Aron, A., Aron, E. N., & Smollan, D. (1992). Inclusion of Other in the Self Scale and the structure of interpersonal closeness. *Journal of Personality and Social Psychology, 63*(4), 596–612. https://doi.org/10.1037/0022-3514.63.4.596

Asparouhov, T., Hamaker, E. L., & Muthén, B. (2018). Dynamic structural equation models. *Structural Equation Modeling: A Multidisciplinary Journal, 25*(3), 359–388. https://doi.org/10.1080/10705511.2017.1406803

Barnett, A. G., van der Pols, J. C., & Dobson, A. J. (2005). Regression to the mean: What it is and how to deal with it. *International Journal of Epidemiology, 34*(1), 215–220. https://doi.org/10.1093/ije/dyh299 **[sin verificar: coautores]**

Basner, M., & Dinges, D. F. (2011). Maximizing sensitivity of the Psychomotor Vigilance Test (PVT) to sleep loss. *Sleep, 34*(5), 581–591. https://doi.org/10.1093/sleep/34.5.581

Basner, M., Mollicone, D., & Dinges, D. F. (2011). Validity and sensitivity of a brief psychomotor vigilance test (PVT-B) to total and partial sleep deprivation. *Acta Astronautica, 69*(11–12), 949–959. https://doi.org/10.1016/j.actaastro.2011.07.015

Benjamini, Y., & Hochberg, Y. (1995). Controlling the false discovery rate: A practical and powerful approach to multiple testing. *Journal of the Royal Statistical Society: Series B (Methodological), 57*(1), 289–300. https://doi.org/10.1111/j.2517-6161.1995.tb02031.x

Benjamini, Y., & Yekutieli, D. (2001). The control of the false discovery rate in multiple testing under dependency. *The Annals of Statistics, 29*(4), 1165–1188. https://doi.org/10.1214/aos/1013699998

Bernard, H. R., Killworth, P., Kronenfeld, D., & Sailer, L. (1984). The problem of informant accuracy: The validity of retrospective data. *Annual Review of Anthropology, 13*, 495–517. https://doi.org/10.1146/annurev.an.13.100184.002431

Bosch Sensortec. (2021). *BMP280: Digital pressure sensor. Data sheet* (BST-BMP280-DS001-26, revisión 1.26). https://www.bosch-sensortec.com/media/boschsensortec/downloads/datasheets/bst-bmp280-ds001.pdf

Bretherton, C. S., Widmann, M., Dymnikov, V. P., Wallace, J. M., & Bladé, I. (1999). The effective number of spatial degrees of freedom of a time-varying field. *Journal of Climate, 12*(7), 1990–2009. https://doi.org/10.1175/1520-0442(1999)012<1990:TENOSD>2.0.CO;2

Burt, R. S. (2004). Structural holes and good ideas. *American Journal of Sociology, 110*(2), 349–399. https://doi.org/10.1086/421787

Campbell, D. T. (1979). Assessing the impact of planned social change. *Evaluation and Program Planning, 2*(1), 67–90. https://doi.org/10.1016/0149-7189(79)90048-X

Chinoy, E. D., Cuellar, J. A., Huwa, K. E., Jameson, J. T., Watson, C. H., Bessman, S. C., Hirsch, D. A., Cooper, A. D., Drummond, S. P. A., & Markwald, R. R. (2021). Performance of seven consumer sleep-tracking devices compared with polysomnography. *Sleep, 44*(5), zsaa291. https://doi.org/10.1093/sleep/zsaa291

Choe, E. K., Lee, N. B., Lee, B., Pratt, W., & Kientz, J. A. (2014). Understanding quantified-selfers' practices in collecting and exploring personal data. En *Proceedings of the SIGCHI Conference on Human Factors in Computing Systems* (pp. 1143–1152). ACM. https://doi.org/10.1145/2556288.2557372

Cleveland, W. S., & McGill, R. (1984). Graphical perception: Theory, experimentation, and application to the development of graphical methods. *Journal of the American Statistical Association, 79*(387), 531–554. https://doi.org/10.1080/01621459.1984.10478080

Cohen, J. (1988). *Statistical power analysis for the behavioral sciences* (2.ª ed.). Lawrence Erlbaum Associates. https://doi.org/10.4324/9780203771587

Cohen, S., Kamarck, T., & Mermelstein, R. (1983). A global measure of perceived stress. *Journal of Health and Social Behavior, 24*(4), 385–396. https://doi.org/10.2307/2136404

Conner, T. S., & Barrett, L. F. (2012). Trends in ambulatory self-report: The role of momentary experience in psychosomatic medicine. *Psychosomatic Medicine, 74*(4), 327–337. https://doi.org/10.1097/PSY.0b013e3182546f18

Cronbach, L. J., & Meehl, P. E. (1955). Construct validity in psychological tests. *Psychological Bulletin, 52*(4), 281–302. https://doi.org/10.1037/h0040957

Cruise, C. E., Broderick, J., Porter, L., Kaell, A., & Stone, A. A. (1996). Reactive effects of diary self-assessment in chronic pain patients. *Pain, 67*(2–3), 253–258. https://doi.org/10.1016/0304-3959(96)03125-9

Csikszentmihalyi, M., & Larson, R. (1987). Validity and reliability of the experience-sampling method. *Journal of Nervous and Mental Disease, 175*(9), 526–536. https://doi.org/10.1097/00005053-198709000-00004

Csikszentmihalyi, M., & LeFevre, J. (1989). Optimal experience in work and leisure. *Journal of Personality and Social Psychology, 56*(5), 815–822. https://doi.org/10.1037/0022-3514.56.5.815

Cuttone, A., Bækgaard, P., Sekara, V., Jonsson, H., Larsen, J. E., & Lehmann, S. (2017). SensibleSleep: A Bayesian model for learning sleep patterns from smartphone events. *PLOS ONE, 12*(1), e0169901. https://doi.org/10.1371/journal.pone.0169901

Daza, E. J. (2018). Causal analysis of self-tracked time series data using a counterfactual framework for N-of-1 trials. *Methods of Information in Medicine, 57*(S 01), e10–e21. https://doi.org/10.3414/ME16-02-0044

Deci, E. L., Koestner, R., & Ryan, R. M. (1999). A meta-analytic review of experiments examining the effects of extrinsic rewards on intrinsic motivation. *Psychological Bulletin, 125*(6), 627–668. https://doi.org/10.1037/0033-2909.125.6.627

Deci, E. L., & Ryan, R. M. (2000). The "what" and "why" of goal pursuits: Human needs and the self-determination of behavior. *Psychological Inquiry, 11*(4), 227–268. https://doi.org/10.1207/S15327965PLI1104_01

Dejonckheere, E., Mestdagh, M., Houben, M., Rutten, I., Sels, L., Kuppens, P., & Tuerlinckx, F. (2019). Complex affect dynamics add limited information to the prediction of psychological well-being. *Nature Human Behaviour, 3*(5), 478–491. https://doi.org/10.1038/s41562-019-0555-0 **[sin verificar: autores después del tercero]**

Deterding, S., Dixon, D., Khaled, R., & Nacke, L. (2011). From game design elements to gamefulness: Defining "gamification". En *Proceedings of the 15th International Academic MindTrek Conference* (pp. 9–15). ACM. https://doi.org/10.1145/2181037.2181040

Domhoff, G. W. (2003). *The scientific study of dreams: Neural networks, cognitive development, and content analysis*. American Psychological Association. **[sin verificar: subtítulo y ficha editorial]**

Dworkin, M. J. (2007). *Recommendation for block cipher modes of operation: Galois/Counter Mode (GCM) and GMAC* (NIST Special Publication 800-38D). National Institute of Standards and Technology. https://doi.org/10.6028/NIST.SP.800-38D

Eagle, N., Pentland, A., & Lazer, D. (2009). Inferring friendship network structure by using mobile phone data. *Proceedings of the National Academy of Sciences, 106*(36), 15274–15278. https://doi.org/10.1073/pnas.0900282106

Epstein, D. A., Ping, A., Fogarty, J., & Munson, S. A. (2015). A lived informatics model of personal informatics. En *Proceedings of the 2015 ACM International Joint Conference on Pervasive and Ubiquitous Computing* (pp. 731–742). ACM. https://doi.org/10.1145/2750858.2804250

Fieller, E. C., Hartley, H. O., & Pearson, E. S. (1957). Tests for rank correlation coefficients. I. *Biometrika, 44*(3–4), 470–481. https://doi.org/10.1093/biomet/44.3-4.470

Fisher, A. J., Medaglia, J. D., & Jeronimus, B. F. (2018). Lack of group-to-individual generalizability is a threat to human subjects research. *Proceedings of the National Academy of Sciences, 115*(27), E6106–E6115. https://doi.org/10.1073/pnas.1711978115 **[sin verificar: páginas]**

Fisher, R. A. (1915). Frequency distribution of the values of the correlation coefficient in samples from an indefinitely large population. *Biometrika, 10*(4), 507–521. https://doi.org/10.2307/2331838

Fruchterman, T. M. J., & Reingold, E. M. (1991). Graph drawing by force-directed placement. *Software: Practice and Experience, 21*(11), 1129–1164. https://doi.org/10.1002/spe.4380211102

Fuller, D., Colwell, E., Low, J., Orychock, K., Tobin, M. A., Simango, B., Buote, R., Van Heerden, D., Luan, H., Cullen, K., Slade, L., & Taylor, N. G. A. (2020). Reliability and validity of commercially available wearable devices for measuring steps, energy expenditure, and heart rate: Systematic review. *JMIR mHealth and uHealth, 8*(9), e18694. https://doi.org/10.2196/18694

Gächter, S., Starmer, C., & Tufano, F. (2015). Measuring the closeness of relationships: A comprehensive evaluation of the 'Inclusion of the Other in the Self' scale. *PLoS ONE, 10*(6), e0129478. https://doi.org/10.1371/journal.pone.0129478

Gelman, A., Hill, J., & Yajima, M. (2012). Why we (usually) don't have to worry about multiple comparisons. *Journal of Research on Educational Effectiveness, 5*(2), 189–211. https://doi.org/10.1080/19345747.2011.618213

Gelman, A., & Loken, E. (2014). The statistical crisis in science. *American Scientist, 102*(6), 460–465. https://doi.org/10.1511/2014.111.460 **[sin verificar: página final]**

Gosling, S. D., Rentfrow, P. J., & Swann, W. B., Jr. (2003). A very brief measure of the Big-Five personality domains. *Journal of Research in Personality, 37*(6), 504–528. https://doi.org/10.1016/S0092-6566(03)00046-1

Granger, C. W. J. (1969). Investigating causal relations by econometric models and cross-spectral methods. *Econometrica, 37*(3), 424–438. https://doi.org/10.2307/1912791 **[sin verificar: página final]**

Granger, C. W. J., & Newbold, P. (1974). Spurious regressions in econometrics. *Journal of Econometrics, 2*(2), 111–120. https://doi.org/10.1016/0304-4076(74)90034-7

Guyatt, G., Sackett, D., Taylor, D. W., Chong, J., Roberts, R., & Pugsley, S. (1986). Determining optimal therapy—randomized trials in individual patients. *New England Journal of Medicine, 314*(14), 889–892. https://doi.org/10.1056/NEJM198604033141406

Hall, C. S., & Van de Castle, R. L. (1966). *The content analysis of dreams*. Appleton-Century-Crofts.

Hamaker, E. L., & Wichers, M. (2017). No time like the present: Discovering the hidden dynamics in intensive longitudinal data. *Current Directions in Psychological Science, 26*(1), 10–15. https://doi.org/10.1177/0963721416666518

Hamari, J., Koivisto, J., & Sarsa, H. (2014). Does gamification work? A literature review of empirical studies on gamification. En *2014 47th Hawaii International Conference on System Sciences* (pp. 3025–3034). IEEE. https://doi.org/10.1109/HICSS.2014.377

Harpsøe, N. G., Andersen, L. P. H., Gögenur, I., & Rosenberg, J. (2015). Clinical pharmacokinetics of melatonin: A systematic review. *European Journal of Clinical Pharmacology, 71*(8), 901–909. https://doi.org/10.1007/s00228-015-1873-4

Hedges, L. V. (1981). Distribution theory for Glass's estimator of effect size and related estimators. *Journal of Educational Statistics, 6*(2), 107–128. https://doi.org/10.3102/10769986006002107

Hersbach, H., Bell, B., Berrisford, P., et al. (2020). The ERA5 global reanalysis. *Quarterly Journal of the Royal Meteorological Society, 146*(730), 1999–2049. https://doi.org/10.1002/qj.3803

Hughes, M. E., Waite, L. J., Hawkley, L. C., & Cacioppo, J. T. (2004). A short scale for measuring loneliness in large surveys: Results from two population-based studies. *Research on Aging, 26*(6), 655–672. https://doi.org/10.1177/0164027504268574

Hultman, E., Söderlund, K., Timmons, J. A., Cederblad, G., & Greenhaff, P. L. (1996). Muscle creatine loading in men. *Journal of Applied Physiology, 81*(1), 232–237. https://doi.org/10.1152/jappl.1996.81.1.232

Institute of Medicine. (2001). *Caffeine for the sustainment of mental task performance: Formulations for military operations*. National Academies Press. https://doi.org/10.17226/10219

Ioannidis, J. P. A. (2005). Why most published research findings are false. *PLoS Medicine, 2*(8), e124. https://doi.org/10.1371/journal.pmed.0020124

Ioannidis, J. P. A. (2008). Why most discovered true associations are inflated. *Epidemiology, 19*(5), 640–648. https://doi.org/10.1097/EDE.0b013e31818131e7

Jahng, S., Wood, P. K., & Trull, T. J. (2008). Analysis of affective instability in ecological momentary assessment: Indices using successive difference and group comparison via multilevel modeling. *Psychological Methods, 13*(4), 354–375. https://doi.org/10.1037/a0014173

Jones, A. W. (2010). Evidence-based survey of the elimination rates of ethanol from blood with applications in forensic casework. *Forensic Science International, 200*(1–3), 1–20. https://doi.org/10.1016/j.forsciint.2010.02.021

Jones, G. (2008). Pharmacokinetics of vitamin D toxicity. *The American Journal of Clinical Nutrition, 88*(2), 582S–586S. https://doi.org/10.1093/ajcn/88.2.582S

Kahneman, D., Fredrickson, B. L., Schreiber, C. A., & Redelmeier, D. A. (1993). When more pain is preferred to less: Adding a better end. *Psychological Science, 4*(6), 401–405. https://doi.org/10.1111/j.1467-9280.1993.tb00589.x

Kardous, C. A., & Shaw, P. B. (2014). Evaluation of smartphone sound measurement applications. *The Journal of the Acoustical Society of America, 135*(4), EL186–EL192. https://doi.org/10.1121/1.4865269

Kashdan, T. B., Barrett, L. F., & McKnight, P. E. (2015). Unpacking emotion differentiation: Transforming unpleasant experience by perceiving distinctions in negativity. *Current Directions in Psychological Science, 24*(1), 10–16. https://doi.org/10.1177/0963721414550708

Korotitsch, W. J., & Nelson-Gray, R. O. (1999). An overview of self-monitoring research in assessment and treatment. *Psychological Assessment, 11*(4), 415–425. https://doi.org/10.1037/1040-3590.11.4.415

Kratochwill, T. R., & Levin, J. R. (2010). Enhancing the scientific credibility of single-case intervention research: Randomization to the rescue. *Psychological Methods, 15*(2), 124–144. https://doi.org/10.1037/a0017736

Kravitz, R. L., Duan, N., & the DEcIDE Methods Center N-of-1 Guidance Panel (Eds.). (2014). *Design and implementation of N-of-1 trials: A user's guide* (AHRQ Publication No. 13(14)-EHC122-EF). Agency for Healthcare Research and Quality. https://effectivehealthcare.ahrq.gov/N-1-Trials.cfm **[sin verificar: enlace no abierto]**

Kuppens, P., Allen, N. B., & Sheeber, L. B. (2010). Emotional inertia and psychological maladjustment. *Psychological Science, 21*(7), 984–991. https://doi.org/10.1177/0956797610372634

Lazarus, R. S., & Folkman, S. (1984). *Stress, appraisal, and coping*. Springer. **[sin verificar: ficha editorial]**

Ley Federal del Trabajo. (1970, última reforma publicada en el DOF el 14 de mayo de 2026). Artículo 74. Cámara de Diputados del H. Congreso de la Unión. https://www.diputados.gob.mx/LeyesBiblio/pdf/LFT.pdf

Leys, C., Ley, C., Klein, O., Bernard, P., & Licata, L. (2013). Detecting outliers: Do not use standard deviation around the mean, use absolute deviation around the median. *Journal of Experimental Social Psychology, 49*(4), 764–766. https://doi.org/10.1016/j.jesp.2013.03.013

Li, I., Dey, A., & Forlizzi, J. (2010). A stage-based model of personal informatics systems. En *Proceedings of the SIGCHI Conference on Human Factors in Computing Systems* (pp. 557–566). ACM. https://doi.org/10.1145/1753326.1753409

Lieberman, M. D., Eisenberger, N. I., Crockett, M. J., Tom, S. M., Pfeifer, J. H., & Way, B. M. (2007). Putting feelings into words: Affect labeling disrupts amygdala activity in response to affective stimuli. *Psychological Science, 18*(5), 421–428. https://doi.org/10.1111/j.1467-9280.2007.01916.x

Little, R. J. A., & Rubin, D. B. (2019). *Statistical analysis with missing data* (3.ª ed.). Wiley. https://doi.org/10.1002/9781119482260

Marriott, F. H. C., & Pope, J. A. (1954). Bias in the estimation of autocorrelations. *Biometrika, 41*(3–4), 390–402. https://doi.org/10.1093/biomet/41.3-4.390

Meredith, W. (1993). Measurement invariance, factor analysis and factorial invariance. *Psychometrika, 58*(4), 525–543. https://doi.org/10.1007/BF02294825

Messick, S. (1995). Validity of psychological assessment: Validation of inferences from persons' responses and performances as scientific inquiry into score meaning. *American Psychologist, 50*(9), 741–749. https://doi.org/10.1037/0003-066X.50.9.741

Mohr, D. C., Zhang, M., & Schueller, S. M. (2017). Personal sensing: Understanding mental health using ubiquitous sensors and machine learning. *Annual Review of Clinical Psychology, 13*, 23–47. https://doi.org/10.1146/annurev-clinpsy-032816-044949

Molenaar, P. C. M. (2004). A manifesto on psychology as idiographic science: Bringing the person back into scientific psychology, this time forever. *Measurement: Interdisciplinary Research and Perspectives, 2*(4), 201–218. https://doi.org/10.1207/s15366359mea0204_1

National Institute on Alcohol Abuse and Alcoholism. (s. f.). *What is a standard drink?* Recuperado el 27 de octubre de 2026, de https://www.niaaa.nih.gov/alcohols-effects-health/what-standard-drink

Nehlig, A. (2018). Interindividual differences in caffeine metabolism and factors driving caffeine consumption. *Pharmacological Reviews, 70*(2), 384–411. https://doi.org/10.1124/pr.117.014407

Nosek, B. A., Ebersole, C. R., DeHaven, A. C., & Mellor, D. T. (2018). The preregistration revolution. *Proceedings of the National Academy of Sciences, 115*(11), 2600–2606. https://doi.org/10.1073/pnas.1708274114

OWASP Foundation. (s. f.). *Password storage cheat sheet*. OWASP Cheat Sheet Series. Recuperado el 28 de octubre de 2026, de https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html

Parry, D. A., Davidson, B. I., Sewall, C. J. R., Fisher, J. T., Mieczkowski, H., & Quintana, D. S. (2021). A systematic review and meta-analysis of discrepancies between logged and self-reported digital media use. *Nature Human Behaviour, 5*, 1535–1547. https://doi.org/10.1038/s41562-021-01117-5

Perry, B. L., Pescosolido, B. A., & Borgatti, S. P. (2018). *Egocentric network analysis: Foundations, methods, and models*. Cambridge University Press. https://doi.org/10.1017/9781316443255

Phillips, A. J. K., Clerx, W. M., O'Brien, C. S., Sano, A., Barger, L. K., Picard, R. W., Lockley, S. W., Klerman, E. B., & Czeisler, C. A. (2017). Irregular sleep/wake patterns are associated with poorer academic performance and delayed circadian and sleep/wake timing. *Scientific Reports, 7*, Artículo 3216. https://doi.org/10.1038/s41598-017-03171-4

Pinheiro, J., Bates, D., & R Core Team. (2026). *nlme: Linear and nonlinear mixed effects models* (versión 3.1-171) [Paquete de R]. https://search.r-project.org/CRAN/refmans/nlme/html/corAR1.html

Pyper, B. J., & Peterman, R. M. (1998). Comparison of methods to account for autocorrelation in correlation analyses of fish data. *Canadian Journal of Fisheries and Aquatic Sciences, 55*(9), 2127–2140. https://doi.org/10.1139/f98-104

Remor, E. (2006). Psychometric properties of a European Spanish version of the Perceived Stress Scale (PSS). *The Spanish Journal of Psychology, 9*(1), 86–93. https://doi.org/10.1017/S1138741600006004

Rintala, A., Wampers, M., Myin-Germeys, I., & Viechtbauer, W. (2019). Response compliance and predictors thereof in studies using the experience sampling method. *Psychological Assessment, 31*(2), 226–235. https://doi.org/10.1037/pas0000662

Roberts, S. (2004). Self-experimentation as a source of new ideas: Ten examples about sleep, mood, health, and weight. *Behavioral and Brain Sciences, 27*(2), 227–262. https://doi.org/10.1017/S0140525X04000068

Robinson, M. D., & Clore, G. L. (2002). Belief and feeling: Evidence for an accessibility model of emotional self-report. *Psychological Bulletin, 128*(6), 934–960. https://doi.org/10.1037/0033-2909.128.6.934

Roenneberg, T., Allebrandt, K. V., Merrow, M., & Vetter, C. (2012). Social jetlag and obesity. *Current Biology, 22*(10), 939–943. https://doi.org/10.1016/j.cub.2012.03.038

Romero, E., Villar, P., Gómez-Fraguela, J. A., & López-Romero, L. (2012). Measuring personality traits with ultra-short scales: A study of the Ten Item Personality Inventory (TIPI) in a Spanish sample. *Personality and Individual Differences, 53*(3), 289–293. https://doi.org/10.1016/j.paid.2012.03.035

Rubin, D. B. (1976). Inference and missing data. *Biometrika, 63*(3), 581–592. https://doi.org/10.1093/biomet/63.3.581

Russell, J. A. (1980). A circumplex model of affect. *Journal of Personality and Social Psychology, 39*(6), 1161–1178. https://doi.org/10.1037/h0077714

Russell, J. A., Weiss, A., & Mendelsohn, G. A. (1989). Affect Grid: A single-item scale of pleasure and arousal. *Journal of Personality and Social Psychology, 57*(3), 493–502. https://doi.org/10.1037/0022-3514.57.3.493

Schork, N. J. (2015). Personalized medicine: Time for one-person trials. *Nature, 520*(7549), 609–611. https://doi.org/10.1038/520609a

Schwarz, N., & Clore, G. L. (1983). Mood, misattribution, and judgments of well-being: Informative and directive functions of affective states. *Journal of Personality and Social Psychology, 45*(3), 513–523. https://doi.org/10.1037/0022-3514.45.3.513

Schwarz, N., Strack, F., & Mai, H.-P. (1991). Assimilation and contrast effects in part-whole question sequences: A conversational logic analysis. *Public Opinion Quarterly, 55*(1), 3–23. https://doi.org/10.1086/269239

Searle, J. (2015). Alcohol calculations and their uncertainty. *Medicine, Science and the Law, 55*(1), 58–64. https://doi.org/10.1177/0025802414524385

Shaffer, F., & Ginsberg, J. P. (2017). An overview of heart rate variability metrics and norms. *Frontiers in Public Health, 5*, Artículo 258. https://doi.org/10.3389/fpubh.2017.00258

Shiffman, S., Stone, A. A., & Hufford, M. R. (2008). Ecological momentary assessment. *Annual Review of Clinical Psychology, 4*, 1–32. https://doi.org/10.1146/annurev.clinpsy.3.022806.091415

Simmons, J. P., Nelson, L. D., & Simonsohn, U. (2011). False-positive psychology: Undisclosed flexibility in data collection and analysis allows presenting anything as significant. *Psychological Science, 22*(11), 1359–1366. https://doi.org/10.1177/0956797611417632

STMicroelectronics. (2016). *LPS22HB: MEMS nano pressure sensor: 260-1260 hPa absolute digital output barometer* (DocID027083 Rev 5). Copia en https://cdn.sparkfun.com/assets/a/7/b/5/e/LPS22HB.pdf

Stone, A. A., Shiffman, S., Schwartz, J. E., Broderick, J. E., & Hufford, M. R. (2002). Patient non-compliance with paper diaries. *BMJ, 324*(7347), 1193–1194. https://doi.org/10.1136/bmj.324.7347.1193

Storey, J. D., & Tibshirani, R. (2003). Statistical significance for genomewide studies. *Proceedings of the National Academy of Sciences, 100*(16), 9440–9445. https://doi.org/10.1073/pnas.1530509100

Swan, M. (2013). The Quantified Self: Fundamental disruption in big data science and biological discovery. *Big Data, 1*(2), 85–99. https://doi.org/10.1089/big.2012.0002

Tanious, R., & Onghena, P. (2019). Randomized single-case experimental designs in healthcare research: What, why, and how? *Healthcare, 7*(4), Artículo 143. https://doi.org/10.3390/healthcare7040143

Task Force of the European Society of Cardiology and the North American Society of Pacing and Electrophysiology. (1996). Heart rate variability: Standards of measurement, physiological interpretation, and clinical use. *Circulation, 93*(5), 1043–1065. https://doi.org/10.1161/01.CIR.93.5.1043

Tausczik, Y. R., & Pennebaker, J. W. (2010). The psychological meaning of words: LIWC and computerized text analysis methods. *Journal of Language and Social Psychology, 29*(1), 24–54. https://doi.org/10.1177/0261927X09351676

Topp, C. W., Østergaard, S. D., Søndergaard, S., & Bech, P. (2015). The WHO-5 Well-Being Index: A systematic review of the literature. *Psychotherapy and Psychosomatics, 84*(3), 167–176. https://doi.org/10.1159/000376585

Trull, T. J., & Ebner-Priemer, U. (2013). Ambulatory assessment. *Annual Review of Clinical Psychology, 9*, 151–176. https://doi.org/10.1146/annurev-clinpsy-050212-185510

Turan, M. S., Barker, E., Burr, W., & Chen, L. (2010). *Recommendation for password-based key derivation: Part 1: Storage applications* (NIST Special Publication 800-132). National Institute of Standards and Technology. https://doi.org/10.6028/NIST.SP.800-132

U.S. Bureau of Labor Statistics. (s. f.). *American Time Use Survey: Concepts and definitions* [Página web]. https://www.bls.gov/tus/concepts.htm

Vohra, S., Shamseer, L., Sampson, M., Bukutu, C., Schmid, C. H., Tate, R., Nikles, J., Zucker, D. R., Kravitz, R., Guyatt, G., Altman, D. G., Moher, D., & the CENT Group. (2015). CONSORT extension for reporting N-of-1 trials (CENT) 2015 Statement. *BMJ, 350*, h1738. https://doi.org/10.1136/bmj.h1738

von Neumann, J., Kent, R. H., Bellinson, H. R., & Hart, B. I. (1941). The mean square successive difference. *The Annals of Mathematical Statistics, 12*(2), 153–162. https://doi.org/10.1214/aoms/1177731746

Watson, P. E., Watson, I. D., & Batt, R. D. (1980). Total body water volumes for adult males and females estimated from simple anthropometric measurements. *The American Journal of Clinical Nutrition, 33*(1), 27–39. https://doi.org/10.1093/ajcn/33.1.27

Watson, P. E., Watson, I. D., & Batt, R. D. (1981). Prediction of blood alcohol concentrations in human subjects: Updating the Widmark equation. *Journal of Studies on Alcohol, 42*(7), 547–556. https://doi.org/10.15288/jsa.1981.42.547

Welch, B. L. (1947). The generalization of 'Student's' problem when several different population variances are involved. *Biometrika, 34*(1–2), 28–35. https://doi.org/10.1093/biomet/34.1-2.28

Westermann, R., Spies, K., Stahl, G., & Hesse, F. W. (1996). Relative effectiveness and validity of mood induction procedures: A meta-analysis. *European Journal of Social Psychology, 26*(4), 557–580. https://doi.org/10.1002/(SICI)1099-0992(199607)26:4<557::AID-EJSP769>3.0.CO;2-4

Wickham, H. (2014). Tidy data. *Journal of Statistical Software, 59*(10), 1–23. https://doi.org/10.18637/jss.v059.i10

Wilkinson, M. D., Dumontier, M., Aalbersberg, I. J., et al. (2016). The FAIR Guiding Principles for scientific data management and stewardship. *Scientific Data, 3*, Artículo 160018. https://doi.org/10.1038/sdata.2016.18

Wilson, T. D., & Gilbert, D. T. (2005). Affective forecasting: Knowing what to want. *Current Directions in Psychological Science, 14*(3), 131–134. https://doi.org/10.1111/j.0963-7214.2005.00355.x

Wittmann, M., Dinich, J., Merrow, M., & Roenneberg, T. (2006). Social jetlag: Misalignment of biological and social time. *Chronobiology International, 23*(1–2), 497–509. https://doi.org/10.1080/07420520500545979

Yule, G. U. (1926). Why do we sometimes get nonsense-correlations between time-series? A study in sampling and the nature of time-series. *Journal of the Royal Statistical Society, 89*(1), 1–63. https://doi.org/10.2307/2341482

Zheng, Y. (2015). Trajectory data mining: An overview. *ACM Transactions on Intelligent Systems and Technology, 6*(3), Artículo 29. https://doi.org/10.1145/2743025
:::
