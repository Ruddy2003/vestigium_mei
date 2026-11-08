# Capítulo 1. Introducción y marco teórico

> **Para qué sirve este capítulo.** Explica **qué es Vestigium Mei, qué tipo de conocimiento puede dar y por qué está construida como está**, y discute dos decisiones de fondo: que sea un sistema **para toda la vida** y que **todo pueda cruzarse con todo**. Describe el código de la v92 (`SCHEMA_V = 15`): la v90 (commit `8818d5a`), después de las entregas 10a a 10d del módulo 10 y de la revisión de navegación (N0 a N7), más la entrega 10e, que arregló los hallazgos abiertos que tenían solución en el código, y la 10f, que aplicó las decisiones del autor sobre los que seguían pendientes. Los ejemplos numéricos son **inventados**.

---

## 1.1 Qué es Vestigium Mei, en una página

Vestigium Mei («mi rastro», en latín) es una app de Android, escrita en Kotlin con Jetpack Compose, **con un solo usuario: su autor**. Su propósito es registrar, a lo largo de los años, el rastro de cada día y poder preguntarle a ese registro cosas sobre uno mismo. Todo vive en el teléfono y sale solo por los respaldos que el usuario activa y por consultas puntuales a servicios externos (capítulo 10).

**Tres capas de datos** (capítulo 2):

- **Activa**: lo que escribes o marcas. La valencia y la activación del día (escalas de 1 a 7), los avisos de ánimo al azar (3 al día por omisión entre las 10:00 y las 21:00), el diario con @personas y #temas, cuestionarios validados, sueños, gastos, hábitos (capítulo 3).
- **Pasiva**: lo que el teléfono y el reloj miden solos: pantalla, ubicación, pasos y sueño por Health Connect, clima, luz, ruido (capítulo 4).
- **Importada**: Spotify, Google Takeout, WhatsApp (solo conteos), MacroFactor, Kindle, Cold Turkey (capítulo 4).

**Una unidad de análisis: el día.** Todo se resume en el **panel diario**, una tabla con un renglón por día y una columna por variable (`Estadistica.panel`). El día va de **las 05:00 a las 04:59** del día siguiente (el *día lógico*, `Fechas.diaLogico`), para que la madrugada cuente con el día que se estaba viviendo; algunas variables de fuentes externas siguen yendo por día de calendario, y la exportación lo dice en cada una. Además de 193 variables fijas desde el esquema 17 (60 de ellas solo existen si hay datos de su fuente; versión completa, §2.7), la app crea **variables dinámicas**: una por cada persona, tema, lugar, app, artista, nutriente o etapa de vida que aparece en tus datos (27 prefijos; capítulo 2).

**Lo que devuelve:** análisis de relaciones entre cualquier par de variables, quitando tendencias y corrigiendo por días parecidos y por muchas comparaciones (capítulo 6); **experimentos personales** sorteados, con la hipótesis sellada antes de empezar (desde 10f, si se quiere, también el criterio de decisión; capítulo 8); reportes, «Tu año», logros y campañas (capítulo 9), y una **exportación** de 51 tablas CSV, un diccionario y un script de R (`vestigium.R`) que repite los análisis de la app.

**Lo privado.** Impulsos, recaídas, libido, masturbación e intimidad (desde el esquema 21), detonantes, medicamentos y el hábito ligado a las recaídas se ven **solo en la pantalla Privado** y viajan completos a R; no entran en ningún otro análisis de la app (capítulo 10). Aquí se describen solo en su mecánica.

---

## 1.2 El autoseguimiento y para qué sirve

El **autoseguimiento** (*self-tracking*) es registrar de forma sistemática datos sobre uno mismo. Lo nuevo de las últimas dos décadas es la **escala**: un teléfono y un reloj registran sin esfuerzo miles de datos al día, y esa facilidad dio lugar a la comunidad **Quantified Self** (Swan, 2013). Li, Dey y Forlizzi (2010) distinguen cinco etapas en lo que una persona hace con sus datos: **prepararse** (decidir qué medir), **recolectar**, **integrar**, **reflexionar** y **actuar**; un problema en una arrastra a las siguientes. Choe et al. (2014) encontraron que quienes se miden de forma intensiva registran demasiadas cosas y luego no saben sacar conclusiones confiables; Epstein et al. (2015), que la gente **deja de registrar, retoma y cambia de herramienta**. **[Validado]**: son modelos descriptivos bien establecidos.

| Etapa (Li et al., 2010) | Qué hace la app | Capítulo |
|---|---|---|
| Prepararse | Mide mucho por omisión; «todo con todo» | 2, 6 |
| Recolectar | Captura pasiva; avisos al azar; registro de un toque para los días difíciles | 3, 4 |
| Integrar | Un solo panel por día lógico | 2, 4 |
| Reflexionar | Análisis con protecciones estadísticas, reportes | 6, 9 |
| Actuar | Experimentos personales sorteados | 8 |
| (Abandono) | Constancia en lugar de rachas, campañas | 9 |

A la barrera de Choe et al. (2014) la app responde con un análisis que quita tendencias, descuenta que los días se parecen entre sí y corrige por las miles de comparaciones. **[Diseño propio]** la combinación; cada pieza está **[Validado]** en la literatura estadística (capítulo 6).

Hay razones para esperar que los datos ayuden a conocerse: **la memoria resume mal** (pesamos de más el peor momento y el final, y casi ignoramos la duración; Kahneman et al., 1993; Robinson y Clore, 2002), **lo que creemos que hacemos difiere de lo que hacemos** (Parry et al., 2021) y **los patrones lentos no se ven a simple vista**. Pero **no conozco un estudio que muestre que llevar un registro tan amplio como este mejore el bienestar**; lo documentado es que el autorregistro **cambia** la conducta registrada (Korotitsch y Nelson-Gray, 1999). El valor del sistema depende de lo bien que distinga lo que los datos muestran de lo que parecen mostrar.

---

## 1.3 Ciencia de una sola persona

La mayor parte de la psicología y la medicina es **nomotética**: estudia a muchas personas para encontrar lo que vale en promedio. Una persona concreta quiere saber otra cosa: «**en mí**, ¿dormir más va con mejor ánimo?». Esa pregunta es **idiográfica**. Molenaar (2004) mostró que la respuesta de grupo solo sirve para el individuo bajo una condición fuerte, la **ergodicidad**: que todas las personas sigan el mismo proceso y que ese proceso sea **estacionario** (que su promedio, su variabilidad y sus relaciones no cambien con el tiempo). Fisher, Medaglia y Jeronimus (2018) comprobaron que las estimaciones del grupo aproximan mal la variabilidad y las relaciones de cada participante. **[Validado]**.

Ejemplo (Figura 1.1): seis personas inventadas, 60 días cada una. **Entre personas**, la recta que une sus promedios sube **+0.55 puntos de valencia por hora de sueño**. Pero **dentro de** una de ellas, las noches en que duerme más que su propio promedio van con días algo peores: **−0.34 puntos por hora**. La regla del grupo no la describe.

![Figura 1.1](figuras/fig30_dentro_entre.png)

*Figura 1.1. Seis personas sintéticas: relación entre personas (azul, discontinua) y relación dentro de una persona (naranja). Script: `figuras/fig30_dentro_entre.py`.*

Por eso el panel compara **tus días entre sí**. Donde la app usa normas de población (los cortes del WHO-5, los umbrales de Cohen, las 7 h de sueño), los capítulos lo señalan como un préstamo.

Los datos vienen de **mediciones en la vida diaria** (*ambulatory assessment*: autorreportes en el momento, mediciones fisiológicas y conductuales con aparatos; Trull y Ebner-Priemer, 2013), cuya parte de autorreporte es el **muestreo de experiencia**, que pregunta varias veces al día en momentos al azar (Csikszentmihalyi y Larson, 1987; Shiffman et al., 2008); permiten estudiar **dinámicas dentro de la persona** (Hamaker y Wichers, 2017). **[Validado]** el enfoque.

Para saber qué **causa** qué, los **ensayos N-de-1** alternan al azar periodos con y sin una acción en una sola persona (Guyatt et al., 1986; Kravitz et al., 2014); en psicología se llaman **diseños de caso único** (Kratochwill y Levin, 2010). Roberts (2004) ilustra su valor para generar hipótesis y su límite: con una sola persona que sabe lo que espera, el resultado vale para ella y es vulnerable al efecto expectativa. **[Validado]** la lógica; **[Aproximación]** su versión en la app, sin cegamiento (capítulo 8).

| Pregunta | Herramienta | Qué permite decir |
|---|---|---|
| **Descriptiva**: ¿cómo soy? | Tus números, Dinámica emocional, Ritmo, reportes | Cómo fueron tus días |
| **De asociación**: ¿qué va con qué? | Las relaciones más marcadas, Cruzar dos variables, Todo lo que se mueve con algo, Modelo múltiple | Qué se mueve junto **en tus días**; no qué causa qué |
| **Causal**: ¿qué me cambia? | Experimentos personales | Si hacer esto, sabiendo que lo haces, cambió tu medida |

---

## 1.4 Por qué un sistema para toda la vida

La mayoría de los estudios de muestreo de experiencia duran una o dos semanas; Vestigium Mei está pensada para **no terminar** (capítulos 2 y 10). **[Diseño propio]**.

**A favor.** (1) **Potencia**: para una sola pregunta y días independientes, con potencia de 80 % y α = 0.05, con 90 días se detecta una correlación de **0.29**; con 365, de **0.15** (fórmula B.15). Con autocorrelación 0.5 en las dos variables, **0.37** y **0.19**; y en la lista de relaciones, cuyo umbral efectivo se parece al de α = 0.001, con esa misma autocorrelación, **0.52** y **0.27**. (2) **Lo que pasa poco**: 5 días con una persona (el mínimo de `per_`) o 60 días antes y 60 después de un cambio de vida piden tiempo. (3) **Los ciclos lentos**: separar «el invierno» de «lo que me pasó ese invierno» pide varios inviernos. (4) **Tu propia normalidad**: los días atípicos se juzgan contra tu mediana (`Analisis.atipicos`, al menos 20 días por variable).

**Costos.**

- **Cambia el instrumento.** Comparar épocas exige que midan lo mismo de la misma manera (**invarianza de la medición**; Meredith, 1993). Las costuras conocidas se marcan (`ventana_pantalla`, `ventana_pasos`, `version_noche`, `ts_creado` y `ts_editado` desde el esquema 14; `cambio_horario` y `clima_reanalisis` desde el 15); otras no: cambiar la vida media de la cafeína reescribe todo el pasado (H-4-11). Desde 10f, el diccionario tiene columnas para decir desde qué versión rige cada definición (`desde_version`, H-1-8).
- **Cambias tú.** La lista de relaciones usa **todos** los días del panel; quitar la tendencia corrige los niveles que cambian lento, no una **relación** que cambia con los años (H-11-1; capítulo 11).
- **Se abandona**, y un hueco largo no es «nada pasó» (Epstein et al., 2015; H-1-1). Y **los datos se pueden perder** (capítulo 10).

La app lo enfrenta guardando el dato crudo con su hora y calculando después, marcando la versión de cada día y de cada respaldo, y documentando cada cambio (Apéndice D de la versión completa). **[Diseño propio]**, coherente con los principios FAIR (Wilkinson et al., 2016).

---

## 1.5 Todo con todo, y su costo

La app no decide de antemano qué preguntas son interesantes: **cualquier variable puede compararse con cualquier otra** (capítulo 6), porque al empezar no se sabe qué conviene medir (Li et al., 2010). **[Diseño propio]**.

El costo son las **comparaciones múltiples**: cuantas más preguntas se revisan, más «hallazgos» salen por azar. Con *p* variables hay *p*(*p* − 1)/2 parejas: con 50, 1 225; con 150, 11 175; con 300, **44 850**. Sin ninguna relación real y con p < 0.05, saldrían unas **61**, **559** y **2 242** «relaciones». Las defensas (capítulo 6): **Benjamini-Hochberg** (`Analisis.bh`), que marca como firmes solo las de q < 0.10 y mantiene la **proporción esperada** de falsos descubrimientos entre ellas alrededor del 10 % (Benjamini y Hochberg, 1995); **las parejas obvias** (`Estadistica.obvia`, diez reglas), que no se cuentan; **topes por familia** (las 15 personas más mencionadas con al menos 5 días, las 12 apps con más minutos…), y **quitar la tendencia** y el **número efectivo de días**.

Su precio es la potencia: con 100 días independientes, una pareja necesita |*r*| ≥ **0.20** si fue la única pregunta, pero ≥ **0.45** si es la única relación real entre 44 850 (p < 0.10 / 44 850 ≈ 2.2 × 10⁻⁶); con 365 días, **0.10** frente a **0.24** (versión completa, §1.6.2).

**Lo que la corrección no cubre.** BH cuenta las parejas **de una lista**, no las otras maneras de mirar los datos (con o sin tendencia, por día o por semana, ocho filtros por tema, el desfase de «Cruzar dos variables», cualquier objetivo en «Todo lo que se mueve con algo», `privadas = TRUE` en R). Es el **jardín de senderos que se bifurcan** (Gelman y Loken, 2014; Simmons et al., 2011), y nada registra cuántos se recorrieron (H-11-2). Además, **la relación más fuerte de una lista larga casi siempre exagera su tamaño**: es la «maldición del ganador» (Ioannidis, 2008), una regresión a la media (Barnett, van der Pols y Dobson, 2005). **[Validado]** que la corrección es necesaria y que BH es adecuado para explorar; **[Aproximación]** su garantía aquí, porque las pruebas comparten variables y las bifurcaciones no entran en la cuenta.

En consecuencia (Ioannidis, 2005), una relación firme de la lista es **una buena pista, no una conclusión**, como dice la app («Pistas para entenderte. Ninguna prueba causas»); se confirma con datos nuevos o con un experimento (capítulo 11).

---

## 1.6 Qué no es este sistema

No es un **instrumento clínico**: WHO-5 y PSS-4 preguntan por 7 días y sus puntos de corte son orientativos (capítulo 3). No **mide el cuerpo** en todo: cafeína, alcohol en sangre, creatina y suplementos son **estimaciones con parámetros de población** (capítulo 5). No **generaliza**: vale para el autor, en el periodo analizado. No es un **estudio diseñado de antemano**: casi todo es exploratorio, salvo los experimentos personales, con su hipótesis sellada (y, desde 10f, si se quiere, su criterio; capítulo 8).

---

## 1.7 Cómo se califica cada decisión

- **[Validado]**: respaldada por literatura publicada o por un estándar (el modelo circumplejo del afecto, Russell, 1980; AES-256-GCM).
- **[Aproximación]**: razonable, con supuestos que conviene conocer (la luz alta del teléfono como señal de estar al aire libre; corregir por autocorrelación como en un proceso AR(1)).
- **[Diseño propio]**: decisión de la app sin un estándar detrás (el corte del día a las 05:00; el umbral de 0.12 para declarar «lenta» una variable).
- **[sin verificar]**: lo que no se pudo comprobar en una fuente durante la sesión; la lista está en `referencias.md`.

La regla es **no presentar heurísticas como modelos validados**: buena parte de la app es diseño propio, y eso obliga a leer cada resultado con la confianza que le corresponde.

---

## 1.8 Cómo está organizado el paper

| Capítulo | Pregunta que responde |
|---|---|
| 2. Arquitectura | Cómo se guarda cada dato, a qué día pertenece y cómo se distingue «cero» de «no sé» |
| 3. Estado subjetivo | Cómo se pregunta el ánimo, cuándo y con qué sesgos |
| 4. Datos pasivos | Qué mide el teléfono solo y qué tan confiable es |
| 5. Modelos fisiológicos | Qué cifras son cuentas con un modelo y no mediciones |
| 6. Estadística | Cómo se pasa de miles de registros a «esto va con aquello» |
| 7. Red social | Cómo se cuentan las personas |
| 8. Experimentos | Cómo probar algo en ti |
| 9. Reportes y motivación | Qué devuelve la app y cómo la motivación cambia los datos |
| 10. Privacidad e integridad | Cómo se protegen los datos y cómo no se pierden |
| 11. Discusión | Qué puede y qué no puede concluirse, y qué falta |

El paper se escribió por módulos, leyendo el código: los módulos 1 a 8 describieron y anotaron problemas en `hallazgos.md`, el 9 propuso cambios, el autor los aprobó y el 10 los aplicó en cuatro entregas; tras la lectura final, la 10e arregló los que quedaban y tenían solución en el código, y la 10f aplicó lo que el autor decidió sobre los ocho pendientes. Cada hallazgo lleva el número del **módulo** que lo encontró: H-1-n son del capítulo 2; H-2-n, del 3, y así hasta H-7-n, del 8; H-8-n, de los capítulos 9 y 10; H-9-n, de la revisión general; H-11-n, de los capítulos 1 y 11, y H-NAV-n, de la revisión de navegación.

---

## 1.9 Cómo leer esta versión corta

Es un **resumen fiel de la versión completa**: describe el mismo código (v92), con las mismas cifras, umbrales y marcas. El detalle exhaustivo está solo en la completa: todas las variables y tablas de la exportación (Apéndice A), las funciones de R (Apéndice C) y el historial de cambios del módulo 10 (Apéndice D). Las remisiones **«(versión completa, §N.k)»** llevan a la sección N.k de la completa, que tiene otra numeración.

---

## 1.10 En resumen

1. Vestigium Mei es **autoseguimiento de por vida para una sola persona**: datos activos, pasivos e importados en un panel de un renglón por día lógico (05:00 a 04:59), con análisis, experimentos, reportes y exportación a R.
2. Se apoya en el **Quantified Self** (Swan, 2013; Li et al., 2010), la **ciencia idiográfica** (Molenaar, 2004), la **medición en la vida diaria** (Trull y Ebner-Priemer, 2013) y los **ensayos N-de-1** (Guyatt et al., 1986).
3. Lo que vale para un grupo no tiene por qué valer para una persona (Figura 1.1); por eso la app compara tus días entre sí.
4. Un sistema **de por vida** da potencia y una línea base propia; a cambio, el instrumento y la persona cambian, y hay que marcar cada costura.
5. **Todo con todo** multiplica las comparaciones (44 850 parejas con 300 variables). BH, las parejas obvias, los topes, la tendencia y el n efectivo las contienen, pero no las bifurcaciones ni la maldición del ganador: una relación firme es una pista, no una conclusión.
6. Cada decisión lleva **[Validado]**, **[Aproximación]** o **[Diseño propio]**; lo no comprobado, **[sin verificar]**.
7. Dos hallazgos del módulo 11: las relaciones usan toda la historia (H-11-1) y las maneras de mirar los datos no se corrigen (H-11-2).
