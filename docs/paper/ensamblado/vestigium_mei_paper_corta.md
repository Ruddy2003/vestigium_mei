---
title: "Vestigium Mei"
subtitle: "Qué hace la app, cómo y por qué: el paper metodológico (versión corta)"
date: "Versión de la app: v92 (esquema 15), más el Ideario (esquema 16) y las pantallas de captura (esquema 17) en los capítulos que las describen · 3 de noviembre de 2026"
lang: es-MX
toc-title: "Índice"
---



# Resumen {.unnumbered}

Vestigium Mei es una app de Android para registrar, durante toda la vida, el rastro de cada día de una sola persona. Combina datos que se escriben (valencia y activación con avisos al azar y un diario nocturno; cuestionarios validados; personas nombradas con @), datos que el teléfono y el reloj miden solos (pantalla, ubicación, sueño, pasos) y archivos importados. Todo se resume en un panel de un renglón por día, de 05:00 a 04:59 (salvo algunas fuentes, por día de calendario), que distingue «cero» de «sin dato» con una sola regla. La app cruza cualquier variable con cualquier otra: quita tendencias restando el promedio de ±10 días, descuenta que los días se parecen con un número efectivo de días y corrige por miles de comparaciones con Benjamini-Hochberg. Ofrece experimentos personales con semanas sorteadas por pares, hipótesis sellada e intervalos corregidos por autocorrelación, y exporta todo a R, que repite los análisis. Este documento describe cada función según el código de la versión 92 (esquema de datos 15) con su base científica y sus límites, y separa lo validado de lo aproximado y lo propio. Las revisiones anotaron 166 problemas metodológicos: 134 se resolvieron, 31 en parte y 1 sigue pendiente. Las conclusiones posibles son descriptivas y de asociación dentro de la persona, salvo las de los experimentos, causales sin cegamiento. Las limitaciones principales son los datos faltantes no al azar, variables que miden algo distinto de su nombre, la reactividad y suponer que las relaciones no cambian con los años. Se propone confirmar cada hallazgo con datos nuevos.


# Capítulo 1. Introducción y marco teórico

> **Para qué sirve este capítulo.** Explica **qué es Vestigium Mei, qué tipo de conocimiento puede dar y por qué está construida como está**, y discute dos decisiones de fondo: que sea un sistema **para toda la vida** y que **todo pueda cruzarse con todo**. Describe el código de la v92 (`SCHEMA_V = 15`): la v90 (commit `8818d5a`), después de las entregas 10a a 10d del módulo 10 y de la revisión de navegación (N0 a N7), más la entrega 10e, que arregló los hallazgos abiertos que tenían solución en el código, y la 10f, que aplicó las decisiones del autor sobre los que seguían pendientes. Los ejemplos numéricos son **inventados**.

## 1.1 Qué es Vestigium Mei, en una página

Vestigium Mei («mi rastro», en latín) es una app de Android, escrita en Kotlin con Jetpack Compose, **con un solo usuario: su autor**. Su propósito es registrar, a lo largo de los años, el rastro de cada día y poder preguntarle a ese registro cosas sobre uno mismo. Todo vive en el teléfono y sale solo por los respaldos que el usuario activa y por consultas puntuales a servicios externos (capítulo 10).

**Tres capas de datos** (capítulo 2):

- **Activa**: lo que escribes o marcas. La valencia y la activación del día (escalas de 1 a 7), los avisos de ánimo al azar (3 al día por omisión entre las 10:00 y las 21:00), el diario con @personas y #temas, cuestionarios validados, sueños, gastos, hábitos (capítulo 3).
- **Pasiva**: lo que el teléfono y el reloj miden solos: pantalla, ubicación, pasos y sueño por Health Connect, clima, luz, ruido (capítulo 4).
- **Importada**: Spotify, Google Takeout, WhatsApp (solo conteos), MacroFactor, Kindle, Cold Turkey (capítulo 4).

**Una unidad de análisis: el día.** Todo se resume en el **panel diario**, una tabla con un renglón por día y una columna por variable (`Estadistica.panel`). El día va de **las 05:00 a las 04:59** del día siguiente (el *día lógico*, `Fechas.diaLogico`), para que la madrugada cuente con el día que se estaba viviendo; algunas variables de fuentes externas siguen yendo por día de calendario, y la exportación lo dice en cada una. Además de 193 variables fijas desde el esquema 17 (60 de ellas solo existen si hay datos de su fuente; versión completa, §2.7), la app crea **variables dinámicas**: una por cada persona, tema, lugar, app, artista, nutriente o etapa de vida que aparece en tus datos (27 prefijos; capítulo 2).

**Lo que devuelve:** análisis de relaciones entre cualquier par de variables, quitando tendencias y corrigiendo por días parecidos y por muchas comparaciones (capítulo 6); **experimentos personales** sorteados, con la hipótesis sellada antes de empezar (desde 10f, si se quiere, también el criterio de decisión; capítulo 8); reportes, «Tu año», logros y campañas (capítulo 9), y una **exportación** de 51 tablas CSV, un diccionario y un script de R (`vestigium.R`) que repite los análisis de la app.

**Lo privado.** Impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas se ven **solo en la pantalla Privado** y viajan completos a R; no entran en ningún otro análisis de la app (capítulo 10). Aquí se describen solo en su mecánica.

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

: Tabla 1.1. El autoseguimiento y para qué sirve

A la barrera de Choe et al. (2014) la app responde con un análisis que quita tendencias, descuenta que los días se parecen entre sí y corrige por las miles de comparaciones. **[Diseño propio]** la combinación; cada pieza está **[Validado]** en la literatura estadística (capítulo 6).

Hay razones para esperar que los datos ayuden a conocerse: **la memoria resume mal** (pesamos de más el peor momento y el final, y casi ignoramos la duración; Kahneman et al., 1993; Robinson y Clore, 2002), **lo que creemos que hacemos difiere de lo que hacemos** (Parry et al., 2021) y **los patrones lentos no se ven a simple vista**. Pero **no conozco un estudio que muestre que llevar un registro tan amplio como este mejore el bienestar**; lo documentado es que el autorregistro **cambia** la conducta registrada (Korotitsch y Nelson-Gray, 1999). El valor del sistema depende de lo bien que distinga lo que los datos muestran de lo que parecen mostrar.

## 1.3 Ciencia de una sola persona

La mayor parte de la psicología y la medicina es **nomotética**: estudia a muchas personas para encontrar lo que vale en promedio. Una persona concreta quiere saber otra cosa: «**en mí**, ¿dormir más va con mejor ánimo?». Esa pregunta es **idiográfica**. Molenaar (2004) mostró que la respuesta de grupo solo sirve para el individuo bajo una condición fuerte, la **ergodicidad**: que todas las personas sigan el mismo proceso y que ese proceso sea **estacionario** (que su promedio, su variabilidad y sus relaciones no cambien con el tiempo). Fisher, Medaglia y Jeronimus (2018) comprobaron que las estimaciones del grupo aproximan mal la variabilidad y las relaciones de cada participante. **[Validado]**.

Ejemplo (Figura 1.1): seis personas inventadas, 60 días cada una. **Entre personas**, la recta que une sus promedios sube **+0.55 puntos de valencia por hora de sueño**. Pero **dentro de** una de ellas, las noches en que duerme más que su propio promedio van con días algo peores: **−0.34 puntos por hora**. La regla del grupo no la describe.

![Figura 1.1. Seis personas sintéticas: relación entre personas (azul, discontinua) y relación dentro de una persona (naranja). Script: `figuras/fig30_dentro_entre.py`.](figuras/fig30_dentro_entre.png){width=100%}

Por eso el panel compara **tus días entre sí**. Donde la app usa normas de población (los cortes del WHO-5, los umbrales de Cohen, las 7 h de sueño), los capítulos lo señalan como un préstamo.

Los datos vienen de **mediciones en la vida diaria** (*ambulatory assessment*: autorreportes en el momento, mediciones fisiológicas y conductuales con aparatos; Trull y Ebner-Priemer, 2013), cuya parte de autorreporte es el **muestreo de experiencia**, que pregunta varias veces al día en momentos al azar (Csikszentmihalyi y Larson, 1987; Shiffman et al., 2008); permiten estudiar **dinámicas dentro de la persona** (Hamaker y Wichers, 2017). **[Validado]** el enfoque.

Para saber qué **causa** qué, los **ensayos N-de-1** alternan al azar periodos con y sin una acción en una sola persona (Guyatt et al., 1986; Kravitz et al., 2014); en psicología se llaman **diseños de caso único** (Kratochwill y Levin, 2010). Roberts (2004) ilustra su valor para generar hipótesis y su límite: con una sola persona que sabe lo que espera, el resultado vale para ella y es vulnerable al efecto expectativa. **[Validado]** la lógica; **[Aproximación]** su versión en la app, sin cegamiento (capítulo 8).

| Pregunta | Herramienta | Qué permite decir |
|---|---|---|
| **Descriptiva**: ¿cómo soy? | Tus números, Dinámica emocional, Ritmo, reportes | Cómo fueron tus días |
| **De asociación**: ¿qué va con qué? | Las relaciones más marcadas, Cruzar dos variables, Todo lo que se mueve con algo, Modelo múltiple | Qué se mueve junto **en tus días**; no qué causa qué |
| **Causal**: ¿qué me cambia? | Experimentos personales | Si hacer esto, sabiendo que lo haces, cambió tu medida |

: Tabla 1.2. Ciencia de una sola persona

## 1.4 Por qué un sistema para toda la vida

La mayoría de los estudios de muestreo de experiencia duran una o dos semanas; Vestigium Mei está pensada para **no terminar** (capítulos 2 y 10). **[Diseño propio]**.

**A favor.** (1) **Potencia**: para una sola pregunta y días independientes, con potencia de 80 % y α = 0.05, con 90 días se detecta una correlación de **0.29**; con 365, de **0.15** (fórmula B.15). Con autocorrelación 0.5 en las dos variables, **0.37** y **0.19**; y en la lista de relaciones, cuyo umbral efectivo se parece al de α = 0.001, con esa misma autocorrelación, **0.52** y **0.27**. (2) **Lo que pasa poco**: 5 días con una persona (el mínimo de `per_`) o 60 días antes y 60 después de un cambio de vida piden tiempo. (3) **Los ciclos lentos**: separar «el invierno» de «lo que me pasó ese invierno» pide varios inviernos. (4) **Tu propia normalidad**: los días atípicos se juzgan contra tu mediana (`Analisis.atipicos`, al menos 20 días por variable).

**Costos.**

- **Cambia el instrumento.** Comparar épocas exige que midan lo mismo de la misma manera (**invarianza de la medición**; Meredith, 1993). Las costuras conocidas se marcan (`ventana_pantalla`, `ventana_pasos`, `version_noche`, `ts_creado` y `ts_editado` desde el esquema 14; `cambio_horario` y `clima_reanalisis` desde el 15); otras no: cambiar la vida media de la cafeína reescribe todo el pasado (H-4-11). Desde 10f, el diccionario tiene columnas para decir desde qué versión rige cada definición (`desde_version`, H-1-8).
- **Cambias tú.** La lista de relaciones usa **todos** los días del panel; quitar la tendencia corrige los niveles que cambian lento, no una **relación** que cambia con los años (H-11-1; capítulo 11).
- **Se abandona**, y un hueco largo no es «nada pasó» (Epstein et al., 2015; H-1-1). Y **los datos se pueden perder** (capítulo 10).

La app lo enfrenta guardando el dato crudo con su hora y calculando después, marcando la versión de cada día y de cada respaldo, y documentando cada cambio (Apéndice D de la versión completa). **[Diseño propio]**, coherente con los principios FAIR (Wilkinson et al., 2016).

## 1.5 Todo con todo, y su costo

La app no decide de antemano qué preguntas son interesantes: **cualquier variable puede compararse con cualquier otra** (capítulo 6), porque al empezar no se sabe qué conviene medir (Li et al., 2010). **[Diseño propio]**.

El costo son las **comparaciones múltiples**: cuantas más preguntas se revisan, más «hallazgos» salen por azar. Con *p* variables hay *p*(*p* − 1)/2 parejas: con 50, 1 225; con 150, 11 175; con 300, **44 850**. Sin ninguna relación real y con p < 0.05, saldrían unas **61**, **559** y **2 242** «relaciones». Las defensas (capítulo 6): **Benjamini-Hochberg** (`Analisis.bh`), que marca como firmes solo las de q < 0.10 y mantiene la **proporción esperada** de falsos descubrimientos entre ellas alrededor del 10 % (Benjamini y Hochberg, 1995); **las parejas obvias** (`Estadistica.obvia`, diez reglas), que no se cuentan; **topes por familia** (las 15 personas más mencionadas con al menos 5 días, las 12 apps con más minutos…), y **quitar la tendencia** y el **número efectivo de días**.

Su precio es la potencia: con 100 días independientes, una pareja necesita |*r*| ≥ **0.20** si fue la única pregunta, pero ≥ **0.45** si es la única relación real entre 44 850 (p < 0.10 / 44 850 ≈ 2.2 × 10⁻⁶); con 365 días, **0.10** frente a **0.24** (versión completa, §1.6.2).

**Lo que la corrección no cubre.** BH cuenta las parejas **de una lista**, no las otras maneras de mirar los datos (con o sin tendencia, por día o por semana, ocho filtros por tema, el desfase de «Cruzar dos variables», cualquier objetivo en «Todo lo que se mueve con algo», `privadas = TRUE` en R). Es el **jardín de senderos que se bifurcan** (Gelman y Loken, 2014; Simmons et al., 2011), y nada registra cuántos se recorrieron (H-11-2). Además, **la relación más fuerte de una lista larga casi siempre exagera su tamaño**: es la «maldición del ganador» (Ioannidis, 2008), una regresión a la media (Barnett, van der Pols y Dobson, 2005). **[Validado]** que la corrección es necesaria y que BH es adecuado para explorar; **[Aproximación]** su garantía aquí, porque las pruebas comparten variables y las bifurcaciones no entran en la cuenta.

En consecuencia (Ioannidis, 2005), una relación firme de la lista es **una buena pista, no una conclusión**, como dice la app («Pistas para entenderte. Ninguna prueba causas»); se confirma con datos nuevos o con un experimento (capítulo 11).

## 1.6 Qué no es este sistema

No es un **instrumento clínico**: WHO-5 y PSS-4 preguntan por 7 días y sus puntos de corte son orientativos (capítulo 3). No **mide el cuerpo** en todo: cafeína, alcohol en sangre, creatina y suplementos son **estimaciones con parámetros de población** (capítulo 5). No **generaliza**: vale para el autor, en el periodo analizado. No es un **estudio diseñado de antemano**: casi todo es exploratorio, salvo los experimentos personales, con su hipótesis sellada (y, desde 10f, si se quiere, su criterio; capítulo 8).

## 1.7 Cómo se califica cada decisión

- **[Validado]**: respaldada por literatura publicada o por un estándar (el modelo circumplejo del afecto, Russell, 1980; AES-256-GCM).
- **[Aproximación]**: razonable, con supuestos que conviene conocer (la luz alta del teléfono como señal de estar al aire libre; corregir por autocorrelación como en un proceso AR(1)).
- **[Diseño propio]**: decisión de la app sin un estándar detrás (el corte del día a las 05:00; el umbral de 0.12 para declarar «lenta» una variable).
- **[sin verificar]**: lo que no se pudo comprobar en una fuente durante la sesión; la lista está en `referencias.md`.

La regla es **no presentar heurísticas como modelos validados**: buena parte de la app es diseño propio, y eso obliga a leer cada resultado con la confianza que le corresponde.

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

: Tabla 1.3. Cómo está organizado el paper

El paper se escribió por módulos, leyendo el código: los módulos 1 a 8 describieron y anotaron problemas en `hallazgos.md`, el 9 propuso cambios, el autor los aprobó y el 10 los aplicó en cuatro entregas; tras la lectura final, la 10e arregló los que quedaban y tenían solución en el código, y la 10f aplicó lo que el autor decidió sobre los ocho pendientes. Cada hallazgo lleva el número del **módulo** que lo encontró: H-1-n son del capítulo 2; H-2-n, del 3, y así hasta H-7-n, del 8; H-8-n, de los capítulos 9 y 10; H-9-n, de la revisión general; H-11-n, de los capítulos 1 y 11, y H-NAV-n, de la revisión de navegación.

## 1.9 Cómo leer esta versión corta

Es un **resumen fiel de la versión completa**: describe el mismo código (v92), con las mismas cifras, umbrales y marcas. El detalle exhaustivo está solo en la completa: todas las variables y tablas de la exportación (Apéndice A), las funciones de R (Apéndice C) y el historial de cambios del módulo 10 (Apéndice D). Las remisiones **«(versión completa, §N.k)»** llevan a la sección N.k de la completa, que tiene otra numeración.

## 1.10 En resumen

1. Vestigium Mei es **autoseguimiento de por vida para una sola persona**: datos activos, pasivos e importados en un panel de un renglón por día lógico (05:00 a 04:59), con análisis, experimentos, reportes y exportación a R.
2. Se apoya en el **Quantified Self** (Swan, 2013; Li et al., 2010), la **ciencia idiográfica** (Molenaar, 2004), la **medición en la vida diaria** (Trull y Ebner-Priemer, 2013) y los **ensayos N-de-1** (Guyatt et al., 1986).
3. Lo que vale para un grupo no tiene por qué valer para una persona (Figura 1.1); por eso la app compara tus días entre sí.
4. Un sistema **de por vida** da potencia y una línea base propia; a cambio, el instrumento y la persona cambian, y hay que marcar cada costura.
5. **Todo con todo** multiplica las comparaciones (44 850 parejas con 300 variables). BH, las parejas obvias, los topes, la tendencia y el n efectivo las contienen, pero no las bifurcaciones ni la maldición del ganador: una relación firme es una pista, no una conclusión.
6. Cada decisión lleva **[Validado]**, **[Aproximación]** o **[Diseño propio]**; lo no comprobado, **[sin verificar]**.
7. Dos hallazgos del módulo 11: las relaciones usan toda la historia (H-11-1) y las maneras de mirar los datos no se corrigen (H-11-2).


# Capítulo 2. Inventario y arquitectura de datos

> **Para qué sirve este capítulo.** Explica *cómo se obtiene, se guarda, se asigna a un día y se organiza* cada dato de Vestigium Mei, y por qué cada decisión es razonable (o no). Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo que cambió en las entregas 10e y 10f se marca «desde 10e» y «desde 10f». Los ejemplos numéricos son **sintéticos**. Cada decisión se marca como **[Validado]** (respaldada por literatura o por un estándar), **[Aproximación]** (razonable, con supuestos que conviene conocer) o **[Diseño propio]** (decisión de la app sin referente directo). Los hallazgos de este capítulo son H-1-n (módulo 1); los H-9-n son de la revisión general.

## 2.1 Propósito y principios de diseño

Vestigium Mei es un instrumento de **autoseguimiento cuantificado (quantified self, QS) de un solo sujeto** (diseño N-de-1, es decir, un estudio cuya muestra es una sola persona): una persona produce, durante años, un registro denso de su ánimo, cuerpo, conducta, entorno y relaciones. La pregunta metodológica central es: *¿qué estructura de datos permite hacer inferencia razonable sobre un solo individuo, sin falsear lo que realmente se midió?*

La arquitectura responde con cinco principios:

1. **Un día es la unidad de análisis.** Casi todo se resume en una fila por día (el *panel diario*). Las mediciones intradía (avisos de ánimo, ubicación por hora) se conservan en tablas aparte. **[Aproximación]**: pierde la variación dentro del día.
2. **No inventar datos.** Un dato ausente no se convierte en cero salvo que haya una razón estructural (sección 2.6). Confundir «no medido» con «cero» sesga medias, correlaciones y modelos (Rubin, 1976; Little y Rubin, 2019).
3. **Conservar el dato crudo y derivarlo después.** Cada evento se guarda con su marca de tiempo completa, y el panel y los resúmenes se calculan al analizar o exportar, así que se pueden re-derivar con otra regla (por ejemplo, otro corte de día). *Límite:* pantalla, pasos y lo que Health Connect entrega por día se guardan ya sumados (`PantallaDia`, `SaludDia`) y no se pueden re-derivar (sección 2.3).
4. **Esquema flexible que crece con los años.** Cada colección guarda documentos JSON; añadir un campo no obliga a migrar la base.
5. **Todo es exportable**: tablas en formato ordenado (*tidy*, Wickham, 2014), diccionario y un script en R, por reproducibilidad y principios FAIR (Wilkinson et al., 2016).

## 2.2 Las tres capas de captura: activa, pasiva e importada

El origen de un dato determina su tipo de error, por lo que la app conserva esa distinción.

| Capa | Ejemplos | Error típico |
|---|---|---|
| **Activa** (la persona lo teclea o marca) | Diario del día, avisos de ánimo, momentos, gastos, notas | Sesgo de recuerdo, de deseabilidad social, fatiga de registro, días sin llenar |
| **Pasiva** (sensores y APIs del teléfono) | Pantalla, desbloqueos, pasos, sueño del reloj, ubicación, clima, luz | Error de instrumento, cambios de permisos u hora, huecos por apagado |
| **Importada** (archivos que la persona carga) | Takeout de YouTube/Google, WhatsApp, Cold Turkey, nutrición, respaldos | Cobertura parcial, formato cambiante, zonas horarias distintas |

: Tabla 2.1. Las tres capas de captura: activa, pasiva e importada

**Razonamiento científico.** Lo pasivo reduce la carga y el sesgo de recuerdo; lo activo captura lo que ningún sensor ve. El **muestreo de experiencias** (*experience sampling*, ESM: preguntar varias veces al día «¿cómo estás ahora?»; Csikszentmihalyi y Larson, 1987) justifica los avisos de ánimo: preguntar *en el momento* reduce el sesgo retrospectivo frente al diario nocturno. **[Validado]** el principio; **[Diseño propio]** la frecuencia y el contenido exacto de los avisos.

**Cuándo falla.** Dos fuentes que miden lo mismo no son intercambiables. Por eso la app guarda la valencia del diario y la de los avisos como variables distintas (`valencia`, `muestreo_valencia`, `animo_valencia`). *Ejemplo sintético:* un día el diario marca `valencia = 3` y los avisos promedian `muestreo_valencia = 5.3`. Eso sugeriría que el recuerdo nocturno está dominado por el peor momento o por el final del día (la «regla del pico y el final», Kahneman et al., 1993). Esa diferencia **es un resultado de investigación**, no un error (capítulo 3).

## 2.3 Tiempo: marcas ISO y día lógico

### 2.3.1 Marcas de tiempo

`Fechas.ahoraIso()` genera, por ejemplo, `2026-10-16T08:42:10-06:00`: fecha y hora **locales con el desfase respecto a UTC** (el horario universal de referencia). Las fechas «de día» son `YYYY-MM-DD`. Este formato ISO-8601 identifica un instante físico sin ambigüedad y conserva la hora «de reloj de pared», necesaria para ritmos circadianos (a qué hora se duerme, cuándo se usa el teléfono). **[Validado]** (estándar ISO 8601).

**La hora local de entonces (desde 10f, D.109 y D.110; H-1-7, resuelto).** Hasta 10f, los eventos pasados se interpretaban con la zona **actual** del teléfono (`Fechas.zona()`): tras un viaje, un evento antiguo con otro desfase se reconvertía a la hora de hoy, y la hora de dormir o el día de una nota podían moverse. Ahora la configuración guarda cada cambio de zona horaria (`Config.zonas`, «desde este instante, esta zona»), y cada instante se lee con la zona que tenía el teléfono entonces (`Fechas.zonaEn`); una nota con su desfase se lee con ese desfase (`Fechas.localDe`). El cambio se anota al abrir la app, cada 15 minutos y cuando Android avisa (`TIMEZONE_CHANGED`). Para lo guardado antes, el historial se arma con las notas: un periodo con otro desfase empieza si **dos notas seguidas** lo traen. *Ejemplo sintético:* una toma de cafeína anotada en Madrid como `…T08:00:00+02:00` antes se leía a las 00:00 en la Ciudad de México y caía en el día lógico anterior; ahora se lee a las 08:00 y cuenta para su día. `vm_panel.csv` marca los días con cambio de horario o de zona (`cambio_horario`, con `desfase_utc_h`); un día así no dura 24 horas sino 24 − (desfase final − desfase inicial): 23 o 25 con el horario de verano, 16 en un viaje de UTC−6 a UTC+2. **[Validado]** el principio; **[Diseño propio]** cómo se arma el historial. *Cuándo falla:* lo que el teléfono ya guardó sumado por día (sueño, pasos, pantalla) no se recalcula, y el historial reconstruido solo ve los periodos en que anotaste algo.

### 2.3.2 Día lógico (corte a las 05:00)

`Fechas.diaLogico` asigna al **día anterior** cualquier evento entre las 00:00 y las 04:59 (si la hora es menor que 5, resta un día).

*Razonamiento científico.* La medianoche no es una frontera natural de la conducta: quien se acuesta a las 01:30 «todavía está viviendo» el día anterior. La *American Time Use Survey* usa un día-diario que va de las 4:00 a.m. a las 3:59 a.m. por la misma razón (U.S. Bureau of Labor Statistics, s. f.). Cortar en un periodo de mínima actividad reduce los episodios partidos entre dos días. **[Validado]** el principio; **[Diseño propio]** el valor exacto 05:00.

*Ejemplo sintético.* Una nota del martes a las 00:40 y la última pantalla a las 02:10 pertenecen al **lunes** lógico; el primer uso a las 05:05 ya es del martes. `ultimo_uso_h` suma 24 si la hora es menor que 5 (02:10 → 26.17), para que «más tarde» siga siendo un número mayor. Antes de las 05:00, las pantallas donde se anota dicen el corte en palabras (`AvisoMadrugada`). La calidad del sueño va, en cambio, al día del despertar (capítulo 3).

**Pantalla y pasos, desde el esquema 13.** Antes se sumaban de 0:00 a 24:00; ahora `Pantalla.calcular` y `Salud.sincronizar` usan la ventana de 05:00 a 05:00. Cada día guarda qué ventana usó (`"logica"` o `"calendario"`), y **la serie se parte en dos tramos**: una madrugada de teléfono de 01:00 a 01:30 antes contaba para el día nuevo y ahora para el anterior. Las correlaciones que crucen la fecha del cambio deben leerse por tramo; en R se separan con `ventana_pantalla` y `ventana_pasos` de `vm_panel.csv`. **[Diseño propio]**.

**Lo que sigue por día de calendario** (0:00 a 24:00): las variables del reloj y la báscula, la agenda, el clima y el aire, la luna y las de ciudades y países (lista exacta en `Diccionario.CALENDARIO`; versión completa, §2.3.2). El diccionario y el `LEEME.txt` lo dicen; antes el LEEME afirmaba que **todo** empezaba a las 5:00 (H-9-4).

**Cuándo falla.** (a) Quien trabaja de noche o duerme de madrugada con regularidad rompe el supuesto de que a las 05:00 «hay poca actividad»; la regla no se adapta a la persona (limitación L-1). (b) En una misma fila del panel, la madrugada cuenta para unas variables y no para otras, aunque ahora se declara. Entre las visitas, `lugares_distintos` y `en_casa_min` ya usan el mismo día lógico de la llegada (H-1-2, resuelto en 10c).

## 2.4 El almacén: SQLite, documentos JSON y `Repo`

### 2.4.1 Base de datos y lectura tolerante

`Db` (`SQLiteOpenHelper`, archivo `vestigium.db`) es un **almacén de documentos sobre SQLite**: cada colección (`dias`, `momentos`, `gastos`…) es un conjunto de filas de la tabla `docs` (clave `(col, id)`, con `fecha` y el `json` del documento). Las ubicaciones, muy numerosas y de tamaño fijo, van en su propia tabla con columnas numéricas, por eficiencia. Añadir campos no obliga a migrar, y SQLite da transacciones atómicas y robustez ante apagados. **[Diseño propio]**, práctica común en apps móviles.

La lectura es **tolerante**. `ignoreUnknownKeys` ignora los campos que el código no conoce: permite leer respaldos de versiones futuras, pero descarta en silencio lo que no entiende. `coerceInputValues` sustituye `null` o valores inválidos por el valor por omisión, y así puede convertir un «sin dato» en un valor (H-1-4, abierto). Los documentos que no se pueden decodificar quedan fuera de la app, del panel y de la exportación; hasta el esquema 12 desaparecían **sin aviso** (H-1-3), y desde el 13 se cuentan por colección (`Repo.ilegibles`) y se avisan en «Estado de los datos» y en Hoy. Si la configuración no se puede leer, su texto se copia a `config_ilegible` **antes** de seguir con una vacía, para que el siguiente cambio de ajustes no la sobrescriba (H-9-2).

### 2.4.2 `Repo`: memoria con escritura inmediata

`Repo` mantiene en memoria **35 colecciones** que precarga al iniciar (entre ellas, desde el esquema 13, `avisos`: un documento por día con los avisos de ánimo planeados, vencidos y mostrados; capítulo 3). Cada `guardarX` hace **escritura inmediata** (*write-through*): primero a SQLite (`db.poner`), luego a la memoria. Así se pierde lo mínimo si la app se cierra, y los análisis sobre miles de días son instantáneos; el costo es la memoria, aceptable para un solo usuario. **[Validado]** (patrón estándar de persistencia).

**Metadatos de calidad en `Dia`.** `guardarDia` añade `ts_creado`, `ts_editado`, `schema_v` y **`retroactivo`**: si el día se llenó después de que su día lógico terminó. Desde el esquema 13, `Dia` guarda **`ts_valencia`**, la hora en que la valencia pasó de vacía a llena o cambió (`Reglas.tsValencia`), y `retroactivo` = el día lógico de `ts_valencia` es posterior a la fecha del día (`Reglas.retroactivo`). Antes se decidía al crear el documento, y fallaba si otra cosa lo creaba ese mismo día y la valencia se llenaba días después (H-2-2; capítulo 3). `retroactivo` es un **moderador** del sesgo de recuerdo: un día llenado a posteriori se recuerda, no se vive. En la literatura de diarios se recomienda registrar *cuándo* se llenó cada entrada (Stone et al., 2002). **[Diseño propio]**.

## 2.5 Catálogo de colecciones

Las colecciones se agrupan por familia: ánimo y diario (`dias`, `momentos`, `respuestas`…), cuerpo y salud, teléfono y pantalla, dinero (`gastos`, `ingresos`), lugar y movimiento (`lugares`, `visitas`, la tabla `ubicaciones`…), entorno, cultura y mente, sociales, cognición (`reacciones`) y ciencia personal (`experimentos`). El detalle campo a campo está en el Apéndice A.

**Unidad de observación.** `momentos` (varios por día, con hora), `gastos` (por transacción), `visitas` (con entrada y salida) y `ubicaciones` (puntos) son **eventos**, no días. El paso de evento a día (sección 2.6) es una fuente de decisiones que afectan los resultados: un día con un solo aviso de ánimo tiene una media tan ruidosa como una observación única.

## 2.6 El panel diario y las reglas de relleno

`Estadistica.panel()` devuelve un mapa `fecha → (id de variable → Double?)`. Cada celda tiene **tres estados** posibles (Figura 2.1):

| Estado | Significado | Representación |
|---|---|---|
| **Valor** | Se observó y vale *x* | número |
| **Cero estructural** | Se sabe que *podría haberse registrado* y no ocurrió | `0.0` |
| **Sin dato** | No hay información | `null` (vacío en CSV, `NA` en R) |

: Tabla 2.2. El panel diario y las reglas de relleno

![Figura 2.1. Panel diario sintético de 30 días con los tres estados posibles de cada celda: sin dato (null), cero estructural y valor observado. El gasto vale 0 solo desde el primer gasto y en días con diario (`Estadistica.ceroDesde`). Script: `figuras/fig02_estructura_panel.py`.](figuras/fig02_estructura_panel.png){width=100%}

### 2.6.1 La regla general

Una fila se crea para cada fecha que aparece en *alguna* fuente. Una variable recibe `null` si su fuente no tiene registro ese día, salvo en los casos de **relleno con cero**. Desde la entrega 10c la regla es una sola, `Estadistica.ceroDesde(fuente, base)`: devuelve los días de `base` (los que pudieron responderse) iguales o posteriores al primer registro de la fuente. En esos días «no hubo» vale 0; antes del primer registro, o fuera de la base, la celda queda vacía. Las variantes principales:

| Variables | Pueden valer 0… |
|---|---|
| `gasto_total`, `gasto_imprevisto`, `n_impulso`, `escalada`, `gimnasio`; emociones `n_*`, `registros_animo`, `ingreso_total` y otras | desde el primer registro de su fuente, en días con diario (en algunas, o con datos de esa fuente) |
| Hábitos `hab_<id>` | desde `Habito.desde` (el día en que se creó el hábito); desde el esquema 17, vacío en los días en que no lo contestaste |
| `sueno_recordado` | solo si ese día se dijo algo del sueño |
| Variables sociales (`personas`, `vi_*`, `per_`, `pen_`…) | solo en días en que se pudo nombrar a alguien (`Dinamicas.diasSociales`); desde 10e, `personas` cuenta a quien nombras en el diario y en los momentos no privados (D.93) |
| `ctx_*`, `dia_dificil`, `palabras`, `hitos_n` y otras | en todos los días con diario |
| Pasos, sueño del reloj, pantalla, clima, HRV… | nunca: sin lectura no hay dato |

: Tabla 2.3. La regla general

La lista completa está en la versión completa, §2.6.1.

**Razonamiento científico.** Es una aplicación de la teoría de datos faltantes (Rubin, 1976): un dato puede faltar *completamente al azar* (MCAR), *al azar según lo observado* (MAR) o *no al azar* (MNAR, cuando la razón por la que falta depende del propio dato). Rellenar con cero es válido solo si el cero es el valor *verdadero*. Como los días sin anotar no son al azar (suelen ser los peores o los más ocupados), un cero falso no solo baja la media: sesga las correlaciones. **[Validado]** la distinción cero/nulo; **[Aproximación]** la heurística «desde el primer registro», que supone que el uso de la función es continuo desde que se empieza.

*Ejemplo sintético.* Con diario desde el día 1 y el primer gasto el día 40, `gasto_total` queda vacío los días 1 a 39; un día 41 con diario y sin gasto vale 0, y un día 42 sin documento de diario, vacío. Rellenar con 0 desde el día 1 bajaría la media y podría crear una correlación falsa con cualquier variable que *empieza* a registrarse más tarde, producto solo del calendario de adopción.

**Cuándo falla.** (a) «Desde el primer registro» no distingue *abandono de la función* de *ausencia real*: si se deja de registrar emociones tres meses, esos meses quedan en 0 (H-1-1, vigente para el abandono; la inconsistencia entre fuentes que también señalaba quedó resuelta en 10c). (b) La base «días con diario» supone que, si abriste el diario, pudiste anotar un gasto; un día en que solo marcaste la valencia cuenta como «sin gasto». (c) Los hábitos creados antes de que existiera `Habito.desde` siguen en 0 desde el principio. Desde la entrega 10d, `vm_panel.csv` usa estas mismas reglas (sección 2.8).

### 2.6.2 Agregación de eventos a día y otras variables

La operación sigue la naturaleza física de la variable: las cantidades extensivas (minutos, gastos, pasos) se **suman**; los estados intensivos (valencia) se **promedian**; los eventos raros se **cuentan** o se marcan 0/1. Así, `muestreo_valencia` es la media de los avisos del día (`null` si no hay avisos); `escalada_grado` toma el máximo, porque el desempeño en escalada se evalúa por su pico; `aire_libre_min` toma el máximo entre la luz alta del teléfono y el tiempo a pie, corriendo o en bici (dos aproximaciones a lo mismo, **[Aproximación]**). `gasto_imprevisto` es **binaria** (¿hubo algún gasto imprevisto?); desde 10e, el monto va en `gasto_imprevisto_monto`, con la misma regla de 0 que `gasto_total` (H-1-5, resuelto, D.94). `error_prediccion_signo` (desde 10e) es la valencia de hoy menos la predicción de ayer (capítulo 3). **[Diseño propio]** cada operador.

Las escalas del diario (valencia y activación, 1–7) son **ordinales**: la distancia entre 3 y 4 no es necesariamente la de 6 a 7; los análisis usan métodos de rangos (Spearman, capítulo 6) cuando la variable es ordinal. Algunos códigos se vuelven números que solo preservan el orden: `sueno_lucidez` (no = 0, parcial = 1, total = 2). `luna_ilum` se calcula con una fórmula astronómica para cada fecha: es **exógena y completa**, útil como control negativo (si aparece una asociación con la luna, probablemente es ruido o multiplicidad).

## 2.7 Familias de variables dinámicas

Hay **193** variables fijas desde el esquema 17 (cada identificador que `Estadistica.variables()` escribe con su nombre; 187 desde 10f, 186 en la v91 y 184 en la v90): 133 existen siempre y 60 solo si hay datos de su fuente. El esquema 17 sumó tres contextos y tres compañías (D.132, D.133). Las de 10e son `error_prediccion_signo` y `gasto_imprevisto_monto` (D.94); la de 10f, `fuerza_ref_kg`, la mejor 1RM estimada del día en press inclinado con mancuernas (D.114; capítulo 8). Siete son privadas: la app no las cruza y van a R. Además, el sistema crea **variables dinámicas** por cada elemento que la persona usa: una por persona (`per_*`, `pen_*`), tema, lugar, app, categoría de gasto, artista, ciudad, nutriente… (27 prefijos, Apéndice A), y también hábitos, emociones usadas y campañas.

- **Análisis:** entran solo las más frecuentes de cada familia, con un tope y un mínimo de días (por ejemplo, `per_`: máx. 15, mín. 5 días; `Dinamicas.familia`).
- **Exportación extendida (`vm_panel_extendido.csv`):** todas las que aparecen en al menos 2 días, hasta 400 por familia.

*Razonamiento científico.* Con *p* variables hay *p(p−1)/2* pares posibles; crecer sin límite dispararía la **multiplicidad** (con muchas pruebas, algunas salen «significativas» por azar). El filtro por frecuencia la limita, pero elige mirando los mismos datos, así que la corrección de falsos descubrimientos del capítulo 6 es imprescindible. **[Aproximación]**.

El mapa `AGREGADOS` (en `ciencia/Dinamicas.kt`, usado por `Estadistica.obvia`) evita comparar variables **obviamente relacionadas** (`app_instagram` contra `pantalla_min`: una es parte de la otra), y `ENTRE_SI` evita pares dentro de familias que salen de lo mismo (un nutriente y un alimento, una ciudad y su país). Una correlación entre parte y todo es una tautología, no un hallazgo. **[Validado]** el principio; **[Diseño propio]** los pares concretos.

*Ejemplo.* Si la persona menciona `@ana` en 12 de 60 días, existe `per_ana` (0/1 por día); `Texto.canon` unifica «@Ana», «@ana_m» y los apodos declarados. Una mención en el diario cuenta como **«estuve con»** por omisión, pero una mención no es presencia (H-1-6): desde la entrega 10c, la marca de día revisado entra al panel como `presencias_revisado` (0/1), para comparar días revisados y sin revisar (capítulo 7).

## 2.8 Exportación, diccionario y R

La app exporta **51 tablas CSV** de datos (`Exportar.tablas()`, Apéndice A), más el diccionario `vm_diccionario.csv`, `LEEME.txt`, respaldos y el script `vestigium.R`. Las principales son `vm_panel.csv` (una fila por día; tabla de compatibilidad para scripts viejos), `vm_panel_extendido.csv` (el panel de la app más todas las dinámicas con al menos 2 días; el que analiza R) y las tablas por evento (`vm_momentos`, `vm_gastos`…).

**Principio tidy** (ordenado). Cada variable es una columna, cada observación una fila y cada tipo de unidad observacional una tabla (Wickham, 2014): `vm_panel` (unidad = día) y `vm_gastos` (unidad = transacción) conviven sin mezclar granularidades. El diccionario sirve a la interoperabilidad y reutilización (FAIR: *Findable, Accessible, Interoperable, Reusable*; Wilkinson et al., 2016). **[Validado]**. Desde 10c describe cada columna por archivo, para que columnas homónimas de tablas distintas no compartan descripción (H-9-20).

**R analiza el mismo panel que la app (entrega 10d).** Antes, `vm_panel.csv` tenía reglas propias (por ejemplo, `gasto_total` valía 0 en cualquier fecha) y R no servía para comprobar la app (H-5-1). Ahora:

1. `Exportar.comoLaApp` pone en cada columna de `vm_panel.csv` que también existe en `Estadistica.panel()` el valor de la app, con sus reglas de 0 y vacío, conservando el tipo de la columna. **[Diseño propio]**.
2. Once columnas tienen otro nombre en `vm_panel.csv` (por ejemplo, `km` es `distancia_km`); no se renombraron para no romper scripts viejos, y el diccionario dice la equivalencia (`Exportar.EQUIVALENTES`).
3. `vestigium.R` carga `vm_panel_extendido.csv` como `panel`, y `vm_relaciones()` y `vm_factores()` cruzan por omisión las variables que cruza la app (`vm_variables.csv`) y esconden las mismas parejas obvias (`vm_familias.csv`).

Una prueba JVM (`ExportacionPrueba`) comprueba con datos inventados que la app da los mismos números que R, y con una exportación real el autor comprobó que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026).

**Desde qué versión rige cada columna (desde 10f, D.115; H-1-8).** Hasta 10f, el diccionario describía el *significado* pero no versionaba cada variable: si una definición cambiaba, solo reflejaba la actual, y una exportación antigua podía no ser comparable. Ahora `vm_diccionario.csv` trae `desde_version` y `cambio` (con su fila del Apéndice D; vacío = sin cambios desde la v84). La tabla (`Diccionario.DESDE`, 165 entradas armadas con el Apéndice D, con comodines como `per_*`) se escribe a mano: un cambio que no se anote ahí deja «vacío». Desde 10e, el diccionario dice los parámetros con que se calcularon las variables de modelos (vida media, carga y lavado; D.99), y el LEEME dice «esquema 13» y «esquema 14», no «v13» y «v14», y dónde está el esquema de cada día (`schema_v` de `vm_dias.csv`; D.107).

## 2.9 Integridad, respaldo e importación

- **Transacciones.** Las importaciones masivas (`importarDias`) usan `db.transaccion { }`: se aplican todas o ninguna, y por omisión no pisan días existentes.
- **Importar sin inventar.** Desde la entrega 10d, los días importados guardan los sellos de tiempo que traiga el archivo, o los dejan vacíos, y `origen` dice de dónde vinieron (`web`, `obsidian`). Sigue sin haber una marca de «importado el día X» (H-1-9, resuelto en parte).
- **Restaurar.** Un respaldo con `schema_v` mayor que el de la app no se restaura, porque los campos desconocidos se descartarían en silencio; los gastos e ingresos que ya existen (mismo id) no se tocan (capítulo 10).
- **Identificadores.** `nuevoId()` = UUID recortado a 16 caracteres hexadecimales (64 bits): con 10⁵ registros, la probabilidad de al menos una colisión es ≈ 3×10⁻¹⁰.

## 2.10 Amenazas a la validez

| # | Amenaza | Qué hace hoy el sistema |
|---|---|---|
| L-1 | Día lógico fijo (05:00) | Corte único; no se adapta a la persona |
| L-2 | Cero frente a nulo | Una sola regla, `ceroDesde`; el abandono sigue contando como 0 |
| L-3 | Autoselección del registro | `retroactivo`; `con_diario` en `vm_panel.csv` |
| L-4 | Medición reactiva (medirse cambia lo medido) | Inevitable; documentar periodos de adaptación |
| L-5 | Cambios de instrumento (también viajes y cambios de horario) | `schema_v`; `ventana_pantalla` y `ventana_pasos`; desde el esquema 15, `cambio_horario` |
| L-6 | Multiplicidad por variables dinámicas | Filtros por frecuencia; corrección en el capítulo 6 |
| L-7 | Autocorrelación (días seguidos no son independientes) | Tratada en el capítulo 6 |
| L-8 | Generalización (un solo sujeto) | Los resultados describen a esa persona |

: Tabla 2.4. Amenazas a la validez

## 2.11 En resumen

1. La app conserva el origen de cada dato (activo, pasivo o importado), y así puede comparar autorreporte con medición objetiva.
2. El **día lógico** (corte 05:00) asigna los eventos nocturnos al día vivido; desde 10f, cada instante se lee con la zona que tenía el teléfono entonces. Pantalla y pasos lo usan desde el esquema 13 (la serie se parte en dos tramos); otras variables siguen por día de calendario, y la exportación lo declara.
3. El almacén (SQLite + documentos JSON + `Repo` con escritura inmediata) privilegia flexibilidad y pérdida mínima de datos; su lectura tolerante puede convertir un «sin dato» en un valor por omisión (H-1-4).
4. El panel distingue **valor, cero estructural y sin dato**; desde la entrega 10c hay una sola regla, `ceroDesde`: 0 desde el primer registro de la fuente y solo en los días que se pudieron responder.
5. Las variables dinámicas (una por persona, lugar, tema…) exigen control de multiplicidad.
6. La exportación sigue principios tidy y FAIR, y desde la entrega 10d R analiza el mismo panel que la app.
7. El módulo 1 anotó 9 hallazgos (H-1-1 a H-1-9). El módulo 10 resolvió H-1-1 (salvo el abandono de una función), H-1-2, H-1-3 y H-1-6, y en parte H-1-9; la entrega 10e, H-1-5, y la 10f, H-1-7 y H-1-8. Sigue abierto solo H-1-4.


# Capítulo 3. Medición del estado subjetivo

> **Para qué sirve este capítulo.** Explica **cómo mide la app lo que sientes y piensas**: el ánimo en el momento y el del día, las emociones con nombre, los cuestionarios, los sueños, con quién estuviste y algunas mediciones raras. Para cada cosa dice qué se pregunta, cuándo, en qué escala, cómo se convierte en número y qué tan sólido es eso. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo que cambió en las entregas 10e y 10f se marca «desde 10e» y «desde 10f». **Los ejemplos numéricos son inventados.** Los hallazgos se numeran H-2-n (módulo 2). El seguimiento privado se describe solo en su mecánica.

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

: Tabla 3.1. Qué se mide y cuándo

## 3.2 Los dos ejes del ánimo: valencia y activación

Russell (1980) propuso que casi todas las emociones se pueden ubicar en un plano con **dos ejes independientes**: qué tan agradable es la experiencia (**valencia**) y cuánta energía o agitación implica (**activación**, *arousal*). La *calma* es agradable y poco activada; la *ansiedad*, desagradable y muy activada. Como las emociones quedan alrededor de un círculo, se llama **circumplejo**. **[Validado]**: es uno de los modelos de afecto más usados, aunque no el único. Separar la activación permite ver, por ejemplo, si después de dormir poco no estás «peor», sino «más apagado».

**La cuadrícula del momento.** Para el ánimo en el momento, una **cuadrícula de 7 × 7** (`CuadriculaAfecto`) da dos números con **un solo toque**. Si el toque cae en la posición horizontal `x` de un cuadro de ancho `W`, la valencia es `⌊7·x/W⌋ + 1`; si cae en la altura `y` (medida desde arriba) de un cuadro de alto `H`, la activación es `7 − ⌊7·y/H⌋` (ambas limitadas a 1–7). Es una versión de la **Affect Grid** (Russell, Weiss y Mendelsohn, 1989), una escala de un solo reactivo para agrado y activación. **[Aproximación]**: la idea está validada, pero el formato 7 × 7 y las palabras de las esquinas son de la app.

**La escala del día.** Para el ánimo del día hay **dos filas de números del 1 al 7** con un texto por número, llamado **ancla** (`Catalogo.ANCLAS`): en valencia, de «1 = Muy desagradable: angustia, desesperanza» a «7 = Muy agradable: entusiasmo pleno», con «4 = Neutral: plano»; en activación, de «1 = Muy baja: agotado, apagado» a «7 = Muy alta: acelerado, inquieto». También pide **dolor o molestia física de 0 a 5**. Las anclas existen para que un «5» signifique lo mismo hoy que dentro de dos años. **[Aproximación]**: práctica común; las anclas concretas no están validadas. Las de activación mezclan un poco de valencia («3 = relajado, en calma» suena agradable), un efecto pequeño de tamaño desconocido (parte de H-2-11).

Las dos escalas **comparten nombre y rango pero no son el mismo instrumento**: una tiene anclas y pregunta por todo el día; la otra, por *ahora*. Un 5 en una no equivale necesariamente a un 5 en la otra.

## 3.3 Tres formas de responder «¿cómo estuve?»

Conner y Barrett (2012) distinguen el **yo que experimenta** (cómo te sientes *ahora*), el **yo que recuerda** (cómo *recuerdas* que te sentiste) y el **yo que cree** (cómo *crees* que eres en general). Cuanto más lejos está el momento que se reporta, más se apoya la respuesta en creencias generales (Robinson y Clore, 2002). **[Validado]**. La app mide las tres: los avisos y registros propios, el diario de la noche (una medida **retrospectiva**: se responde después, recordando) y los cuestionarios. El cierre del día es momentáneo, pero siempre al final del día.

**El recuerdo no es un promedio.** Al evaluar un periodo, la gente pesa de más el **momento más intenso** y **cómo terminó** (regla «pico-final»; Kahneman et al., 1993). *Ejemplo (inventado):* tres avisos con 6, 5 y 6 (promedio 5.67); a las 21:30, una discusión fuerte; en el diario pones **3** y el cierre lo marcas en 2. Las tres cifras son «verdad», pero de cosas distintas, y por eso se guardan las tres.

**Cómo se calcula cada una** (`Estadistica.panel`): `muestreo_valencia` es el promedio de la valencia de los registros de tipo `muestreo` del día lógico; `animo_valencia`, el de **todos** los registros de ánimo (incluido el cierre); `animo_cierre_valencia`, la del **último** cierre; `valencia`, el número del diario. Las relaciones entre ellas se marcan como «obvias» (capítulo 6).

**Anclaje (H-2-11).** Ver los valores de tus avisos mientras calificas el día puede **anclar** (arrastrar) la respuesta hacia ellos. Desde la entrega N4 esa línea solo aparece **después** de elegir la valencia (`PantallaNoche`). **[Diseño propio]**.

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

: Tabla 3.2. Cómo elige las horas

Dos avisos seguidos quedan separados al menos **2 × mín(45 min, tramo/4)**: 90 minutos solo si cada tramo dura 3 horas o más; con 4 avisos, 82.5 minutos, y con 6, 55 (H-2-7).

![Figura 3.1. Un día sintético con los valores por omisión: tres avisos dentro de sus franjas posibles, una respuesta rápida, un aviso que caduca y los demás registros del día. Script: `figuras/fig08_dia_muestreo.py` (replica las reglas de `planDelDia`, pero no el generador al azar de Kotlin, así que las horas exactas difieren).](figuras/fig08_dia_muestreo.png){width=100%}

### 3.4.2 Cómo llega el aviso y cómo se cuenta

El plan se revisa en el **pulso** de cada ~15 minutos (capítulo 4). Un aviso vencido se muestra **solo si han pasado menos de 50 minutos** desde su hora, y se borra solo a los 60 minutos. Si respondes tocándolo, el registro es de tipo **`muestreo`**, con la hora planeada en `aviso_ts` (y la exportación calcula `latencia_min`); si abres la app por tu cuenta, es **`animo`** (iniciativa propia).

Desde el esquema 13, la colección `avisos` anota por día los avisos **planeados**, **vencidos** y **mostrados**, y el panel tiene `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos` y **`tasa_respuesta`** = respondidos ÷ mostrados (`AvisosAnimo.cifras`). *Ejemplo (inventado):* 4 planeados; 3 vencen y 2 se muestran (el tercero llegó 70 minutos tarde porque el teléfono dormía); respondes uno: tasa 0.5. **[Validado]** el propósito; **[Diseño propio]** la forma de contarla. Hasta el esquema 12 no se guardaba cuántos avisos se mostraron (H-2-3), así que esos días no tienen tasa de respuesta, la primera cifra de un estudio de este tipo: combinando varios, Rintala et al. (2019) encontraron una respuesta promedio de 78 %, más baja temprano en la mañana y que bajaba con los días de estudio.

**Por qué separar avisos y registros propios.** Cuando decides registrar, sueles hacerlo **porque pasó algo**: un muestreo por evento, sesgado si se usa como «cómo estoy en general». La medida más limpia del ánimo momentáneo es `muestreo_valencia`; `animo_valencia` mezcla los dos tipos. **[Validado]** el principio. El mapa emocional de lugares usa todos los registros (H-2-15; versión completa, §3.5.4).

## 3.5 Emociones con nombre, pensamientos y compañía

En cualquier registro de ánimo se puede marcar (todo opcional):
- **qué sientes**, de 47 emociones desde el esquema 17 (`Catalogo.SENTIMIENTOS`: 18 agradables, 23 desagradables y 6 mixtas; antes eran 25), más «otro…» y «no sé». A cada una se le puede poner **cuánto**: un poco, bastante o mucho. La intensidad no entra al panel: se exporta y, desde el esquema 18, se muestra al lado de cada emoción en las pantallas y los reportes («Tristeza (mucho)»).
- **en qué piensas**, varios a la vez desde el esquema 17;
- una nota con @personas y #temas;
- **con quién estás**, que incluye hijos, mascota y «por mensaje o llamada».

«Enojo» y «miedo» caen en la misma zona de los dos ejes y son experiencias distintas. Nombrar con precisión lo que sientes (*granularidad emocional*) se asocia con manejar mejor las emociones desagradables (Kashdan, Barrett y McKnight, 2015). **[Validado]** el concepto; la app solo guarda los nombres, no calcula un índice. «No sé» también es información y tiene su variable, `n_no_se`. **[Diseño propio]**. Como hay más emociones desagradables que agradables, **los conteos no son comparables entre sí**.

**En el panel**, `n_<emoción>` cuenta los registros del día con esa emoción, y `registros_animo`, los registros del día; desde el primer registro de ánimo, un día con diario sin registros vale **0** (H-1-1, capítulo 2). Con los avisos que tienen compañía marcada se calculan `acompanado_pct`, `con_*`, `valencia_acompanado` y `valencia_solo`. En `vm_dias.csv`, las columnas `m_<tipo>` cuentan solo los tipos de momento viejos y valen 0 desde que se unieron ánimo y momentos (H-2-13, resuelto en parte: el diccionario lo advierte).

Ponerle palabras a una emoción (*affect labeling*) puede reducir su intensidad: nombrar la emoción de una cara redujo la respuesta de la amígdala (Lieberman et al., 2007). **Registrar puede cambiar un poco lo registrado** (sección 3.13).

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

## 3.7 La predicción de mañana

«¿Cómo crees que estará tu ánimo mañana?», de 1 a 7, con anclas de agrado, así que se compara con la **valencia** del día siguiente (solo si existen las dos):

$$\text{error\_prediccion} = |\,\text{predicción de ayer} - \text{valencia de hoy}\,|\qquad\qquad (3.1)$$

Solemos equivocarnos al predecir cómo nos sentiremos (*pronóstico afectivo*; Wilson y Gilbert, 2005). **[Validado]** la idea; **[Diseño propio]** la variable. El valor absoluto quita la dirección: predecir 6 y obtener 4 (optimista) y predecir 3 y obtener 5 (pesimista) dan el mismo error, 2. Por eso, desde 10e, el panel tiene también el error con signo (D.94; H-2-10, resuelto):

$$\text{error\_prediccion\_signo} = \text{valencia de hoy} - \text{predicción de ayer}\qquad\qquad (3.2)$$

De −6 a +6; positivo si el día salió mejor de lo predicho. En el ejemplo, −2 y +2. **[Diseño propio]**. Se mueve con `valencia` por construcción; por eso la app esconde esa pareja como obvia, igual que con `prediccion` y `error_prediccion`.

## 3.8 Cuestionarios validados

### 3.8.1 Los cuatro instrumentos

Un **instrumento validado** es un cuestionario publicado cuyas preguntas, escala y puntuación se han probado en estudios. Están en `ciencia/Escalas.kt` y solo se guardan con **todos** los reactivos contestados.

**WHO-5 (bienestar), cada 7 días.** Cinco frases de 0 a 5; bruto = suma (0–25), porcentaje = bruto × 4 (0–100) (Topp et al., 2015). La app interpreta **≤ 28** como «valor bajo» y **≤ 50** como «por debajo de lo habitual». En la literatura, ≤ 50 suele usarse para tamizaje de depresión (conviene evaluar más); que ≤ 28 indique probable depresión no se comprobó en la fuente primaria **[sin verificar]**. El original pregunta por **2 semanas**; la app, por **7 días**, así que los cortes son orientativos.

**PSS-4 (estrés percibido), cada 7 días.** Cuatro preguntas de 0 a 4, dos **inversas** (se puntúan al revés):

$$\text{total} = p_1 + p_4 + (4 - p_2) + (4 - p_3) \quad (0\text{–}16)\qquad\qquad (3.3)$$

(Cohen, Kamarck y Mermelstein, 1983). La versión española citada (Remor, 2006) validó las de 14 y 10 preguntas, no la de 4. El original pregunta por el último mes; la app, por 7 días. **[Validado]** el instrumento; **[Aproximación]** el periodo. *Ejemplo:* p1 = 2, p2 = 3, p3 = 1, p4 = 2 → 2 + 2 + 1 + 3 = **8**.

**UCLA-3 (soledad), cada 30 días.** Tres preguntas de 1 a 3; total 3–9 (Hughes et al., 2004). El corte «6 o más» que muestra la app aparece en estudios que usan la escala, pero no se encontró la fuente que lo establece **[sin verificar]**. **[Validado]** el instrumento.

**TIPI (personalidad), cada 365 días.** Diez frases de 1 a 7; cada rasgo de los cinco grandes promedia una frase directa y una inversa (8 − respuesta), por ejemplo extraversión = (t1 + (8 − t6)) / 2 (Gosling, Rentfrow y Swann, 2003; Romero et al., 2012; los cinco rasgos en la versión completa, §3.9.1). Sirve para ver **cambios grandes** con los años. **[Validado]**.

`Escalas.proxima` fija la siguiente fecha (última respuesta + 7, 30 o 365 días), y el recordatorio insiste más con el atraso (`Escalas.horasAviso`). La regularidad importa porque cada instrumento pregunta por un periodo.

### 3.8.2 Cómo pasan al panel (H-2-1)

Un cuestionario se responde **un día**, pero habla de **un periodo**, así que el puntaje se copia a varios días. Hasta la entrega 10c, WHO-5 se copiaba a 14 días y PSS-4 y UCLA-3 a 30, hacia atrás, y **cada respuesta sobrescribía** a las anteriores: un día podía recibir el «estrés» de una respuesta dada hasta 29 días después. Gravedad **alta**.

Ahora `Dinamicas.porDiaCuestionario` usa una ventana igual al periodo que pregunta cada instrumento: WHO-5 y PSS-4, el día de la respuesta y los **6 anteriores**; UCLA-3, el día y los **29 anteriores**; TIPI, del día de la respuesta hacia adelante hasta la siguiente (como mucho un año). Si dos respuestas cubren el mismo día, se queda **la primera** que lo cubre (`putIfAbsent`): la más cercana **después** de ese día, la que pregunta por él. Un día sin respuesta que lo cubra queda vacío.

*Ejemplo (inventado, Figura 3.2).* PSS-4 los días 1, 8, 15, 22 y 29 de marzo, con 4, 6, 9, 5 y 7. Antes, del 1 al 29 **todos** los días quedaban con 7 y la semana de 9 desaparecía. Ahora el 3 de marzo solo lo cubre la respuesta del 8: **6**, y cada semana conserva su puntaje (`CalculosPrueba.cuestionarioPorSemana`). **[Diseño propio]** la regla; **[Aproximación]** suponer que el puntaje vale igual para cada día del periodo.

![Figura 3.2. PSS-4 sintético respondido cada semana. Línea discontinua: lo que guardaba el panel hasta la entrega 10c (ventana de 30 días, gana la última respuesta). Línea continua: lo que guarda ahora (los 7 días que pregunta cada respuesta, gana la primera que cubre el día). Script: `figuras/fig10_cuestionarios_ventana.py` (replica las dos versiones de `Dinamicas.kt`).](figuras/fig10_cuestionarios_ventana.png){width=100%}

## 3.9 La brújula política

Una vez al año, 25 afirmaciones **escritas para la app** (`data/Politica.kt`), 5 por eje (economía, autoridad, valores, mundo y ambiente), de 1 a 5. Cada afirmación tiene un **signo** (+1 o −1) según el polo al que acerca estar de acuerdo:

$$\text{posición} = \frac{\sum (\text{respuesta} - 3) \times \text{signo} \times 100}{2 \times \text{afirmaciones respondidas}} \quad (-100 \text{ a } +100)\qquad\qquad (3.4)$$

`Politica.lado` dice «muy hacia…» desde ±60, «hacia…» desde ±20 y «al centro» entre −20 y 20 (sin incluirlos). Mezclar signos busca neutralizar la **aquiescencia** (tender a estar de acuerdo con todo), pero el reparto es 3 contra 2: «muy de acuerdo» en las 25 deja cada eje en **±20**, «hacia…» en los cinco (H-2-6). Gravedad baja, porque el sesgo es constante y sirve para compararte contigo mismo. **[Diseño propio]**.

## 3.10 «Conocerte»: el perfil y las etapas de vida

«Conocerte» (`data/Perfiles.kt`) pregunta lo grande de tu vida (con quién vives, pareja, ocupación, ciudad…) y guarda cada cambio con su fecha; de ahí salen las variables de **etapa de vida** (`vida_en_pareja`, `vida_vive_solo`…). `valorEn(campo, fecha)` da el último cambio antes o en esa fecha; si no hay, el valor «antes» del primer cambio posterior; si nunca hubo cambios, el actual. Por eso, llenar el perfil por primera vez proyectaba el valor sobre **todos los días anteriores** (H-2-5). Desde 10c el primer guardado deja historial (`Perfiles.primerGuardado`): si llenas «en pareja» el 10 de octubre, `vida_en_pareja` vale 1 desde ese día y queda vacía antes. Los perfiles llenados antes de esa versión siguen proyectándose hacia atrás. **[Diseño propio]**.

## 3.11 Sueño y sueños

A las **8:00** por omisión, si el día no tiene relato ni la marca «recordé un sueño», llega un aviso. La pantalla pone **primero la narración**, porque el contenido de los sueños se olvida muy rápido; que sea cuestión de minutos es la afirmación de la app **[sin verificar]**. Desde el esquema 17 la narración se escribe en el libro de noche del tema, a pantalla completa, como el diario. Se registra la **calidad del sueño como la sentiste** (1–5, en el día del despertar). Si recordaste un sueño, también:
- su valencia (1–7);
- si fue **pesadilla** (angustia fuerte o te despertó);
- **qué tan raro** fue (realista, raro pero posible, imposible);
- **qué tan claro** lo recuerdas;
- la lucidez;
- categorías en **nueve ejes** (`Catalogo.SUENO_EJES`), con las ocho más usadas a la vista.

Hasta el esquema 16 había una sola pregunta de «tipo» (mundano, ansioso o pesadilla, bizarro, recurrente) que mezclaba esas cosas. Los días de antes se traducen al leerlos: mundano → realista; bizarro → imposible; ansioso → pesadilla; recurrente → «se repite». **[Aproximación]** la rareza, a la idea de *bizarreness*.

Las categorías se basan en el sistema de **Hall y Van de Castle (1966)**. **[Aproximación]**: el original se aplica leyendo el relato con reglas detalladas; aquí quien soñó elige categorías simplificadas, útiles para patrones propios, no para compararse con las normas. La idea de que los sueños reflejan preocupaciones de la vida despierta (hipótesis de la **continuidad**) es de Domhoff (2003).

En el panel, `sueno_lucidez` y `sueno_vividez` valen 0, 1 o 2 (supone saltos iguales, **[Aproximación]**). `sueno_recordado` vale 1 si recordaste un sueño, 0 solo si ese día dijiste algo del sueño sin recordarlo, y vacío en los demás (H-2-4, resuelto en 10c). Desde el esquema 17, la familia `suenot_` es rareza y pesadilla (`suenot_realista`, `suenot_raro`, `suenot_imposible`, `suenot_pesadilla`) en lugar del tipo.

## 3.12 Presencias: estuve, pensé, mensajes

Al nombrar a una @persona en el diario, un toque sobre el nombre dice cómo fue: **estuve con**, **pensé en** o **mensajes** (`data/Presencias.kt`); por omisión, «estuve con» (H-1-6). Si alguien aparece solo en momentos, cuenta como «pensé en» si el momento era de tipo «pensando en» o «recuerdo» **y** estabas solo o sin compañía marcada; si no, «estuve con». Lo marcado en el diario manda, y los momentos privados no se usan. Nombrar a alguien no es haberlo visto. **[Diseño propio]**. Su uso en la red de personas está en el capítulo 7.

## 3.13 Reactividad: medir cambia lo medido

**Reactividad** es que registrar cambie lo registrado: porque te hace prestar atención, porque nombrar una emoción la modifica o porque quieres verte bien en tus datos (capítulo 9). En pacientes con dolor crónico que lo registraban cada hora durante una semana, el registro no cambió en promedio el dolor reportado (Cruise et al., 1996). **[Validado]** que puede no haber reactividad apreciable en el ánimo o el dolor; no se puede descartar para hábitos y conductas. Conviene no sacar conclusiones de las primeras semanas de cada medición nueva. «Tus últimos días» en Hoy pone tu historial de valencia en primer plano; el hilo de hoy no se colorea hasta tener valencia y diario, y se puede apagar. **[Diseño propio]**.

## 3.14 Datos que faltan: por qué no da igual cuándo faltan

Lo típico es no llenar el diario precisamente los días malos. Cuando la probabilidad de que falte un dato depende del propio dato se habla de faltante **no al azar** (MNAR; Rubin, 1976), y el sesgo no se puede corregir solo con los datos que sí hay. *Ejemplo (inventado):* diez días con valencias 5, 6, 5, 2, 6, 5, 3, 6, 5, 2 (promedio **4.50**); sin los tres días malos, el promedio es 38 / 7 = **5.43**.

**El registro de un toque.** La notificación del diario trae «**Día difícil: solo un número**»: una sola escala de valencia que guarda la del día con `dia_dificil = 1` (`QuickActivity.Minimo`). En el ejemplo, esos tres números devuelven el promedio a 4.50. **[Diseño propio]** con buen fundamento. Además, Hoy muestra como pendientes los días de los 7 anteriores sin valencia ni diario (`Pendientes.faltantes`).

**Editar después.** Hasta el esquema 13 un registro de ánimo corregido no guardaba que fue editado, y un dato momentáneo podía volverse retrospectivo sin que se supiera (H-2-9). Desde el 14, cada corrección guarda `ts_editado` y cada registro nuevo `ts_creado`; en R, `vm_retraso()` dice qué se anotó tarde (Apéndice C).

## 3.15 Seguimiento privado (solo la mecánica)

Tres registros: **impulso** (0 a 5 en el día; momentos de 1 a 5), **recaída** (sí o no; momentos con hora, duración, detonantes, aparato y qué hacías antes) y **libido** (0 a 5 y foco), con anclas propias. Desde el módulo 10a, junto con los medicamentos, se anotan y se ven solo en la pantalla Privado y no entran en los análisis de la app, pero **sí** en el panel que se exporta a R (`n_recaida`, `recaida_min`, `n_impulso`…), para entender qué días y contextos lo hacen más difícil (capítulo 10). **[Diseño propio]**. Efectos posibles, sin comprobar: menos reactividad y más datos faltantes.

## 3.16 Una medida que no es autorreporte: la prueba de reacción

`PantallaReaccion` hace **10 intentos**: tras una espera al azar de **1.5 a 4.0 s** aparece un círculo que hay que tocar lo antes posible. Guarda la media, la mediana, los **lapsos** (respuestas de **500 ms o más**) y las **anticipaciones** (toques antes del círculo o, desde 10c, a menos de 100 ms de él; el intento se repite).

Es una versión muy corta de la **prueba de vigilancia psicomotora (PVT)**, la medida estándar de cómo la falta de sueño afecta la atención: 10 minutos, esperas de 2 a 10 s y lapsos de 500 ms o más (Basner y Dinges, 2011). La versión breve validada (PVT-B) dura 3 minutos, con esperas de 1 a 4 s y un umbral de lapso de 355 ms (Basner, Mollicone y Dinges, 2011). La de la app dura unos 36 s en promedio, no los «unos 30 segundos» que dice (H-2-12, cálculo propio), y mide en una pantalla táctil con retrasos que varían entre teléfonos. **[Aproximación]**: útil para tendencias grandes, comparándote contigo mismo. `rt_media` es el promedio de las medias de las pruebas del día.

## 3.17 En resumen

1. El ánimo se mide en **dos ejes** (valencia y activación) y de **tres formas**: en el momento, al recordar el día y como creencia general. Tienen sesgos distintos y **no son intercambiables**.
2. Los avisos usan **muestreo aleatorio estratificado**: 3 al día por omisión, uno por tramo de 10:00–21:00, con margen de mín(45 min, tramo/4). Desde el esquema 13 hay `tasa_respuesta` (H-2-3).
3. `muestreo_valencia` es la medida más limpia del ánimo momentáneo; los registros propios se sesgan hacia los eventos.
4. El hallazgo más grave era que **los cuestionarios se asignaban a días de otras semanas** (H-2-1); desde 10c cada puntaje cubre solo los días que pregunta.
5. En 10c dejaron de codificarse como 0 varios «no respondí» (H-2-4), y el perfil nuevo ya no se proyecta hacia atrás (H-2-5); `retroactivo` usa `ts_valencia` desde el esquema 13 (H-2-2).
6. El **registro de un toque** es una buena defensa contra los días malos sin registrar (MNAR).
7. Los cuestionarios están bien puntuados, pero WHO-5 y PSS-4 usan 7 días en vez de su periodo original, y algunos cortes no se pudieron verificar.
8. De los 15 hallazgos del módulo 2 ya no queda ninguno pendiente: H-2-10 se resolvió en 10e (D.94), y H-2-5, H-2-6, H-2-13 y H-2-15, en parte.


# Capítulo 4. Sensores, datos pasivos e importaciones

> **Para qué sirve este capítulo.** Explica, fuente por fuente, **qué mide la app sin que tú escribas nada** (datos *pasivos*: lo que el teléfono o el reloj registran solos) y **qué entra cuando cargas un archivo** (datos *importados*): cómo se obtiene, qué tan confiable es, qué pasa cuando falta y a qué día se asigna. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo que cambió en las entregas 10e y 10f se marca «desde 10e» y «desde 10f». Los ejemplos son **inventados**. Los problemas se numeran H-3-n (por el módulo que los encontró) y se detallan en `hallazgos.md`.

## 4.1 Cómo y cuándo llegan los datos

**El «pulso» de 15 minutos.** La app programa una tarea periódica (`Pulso`, con `PeriodicWorkRequestBuilder` de WorkManager) **cada 15 minutos**. En cada pulso guarda un punto de ubicación, toma una «muestra de percepción» (wifi, audio, carga, luz, presión), revisa si llevas rato en un lugar nuevo, consulta Spotify (si pasaron al menos 25 minutos desde la última vez), decide si toca algún aviso y, una vez al día, sincroniza lo demás. 15 minutos es el **mínimo** que Android permite, y el sistema puede **retrasar** la tarea para ahorrar batería: el pulso es una *intención*, no un reloj exacto. Por eso los cálculos que dependen de él usan el tiempo *real* entre lecturas (sección 4.5).

**La sincronización diaria.** Después de las 06:00 (`Trabajo.kt`, función `diario`) la app trae reloj (últimos **3** días), pantalla (**2**), fotos (3), agenda, llamadas/SMS (3) y clima, y hace un respaldo; a partir de las 21:00 hay una segunda pasada más corta, y una vez al mes rellena historia atrasada (versión completa, §4.2.2). Si la app no corrió durante más días que esa ventana, esos días **no se recuperan solos**; hay botones que leen hasta 30 días del reloj y 7 de pantalla.

**Permisos.** Sin permiso no hay dato, y la app **no inventa nada**: la variable queda vacía (`null`, capítulo 2). La pantalla **Estado de los datos** (`EstadoDatos.kt`) pone cada fuente en *Al día*, *Atrasada*, *Faltan permisos* o *Apagada*. Saber *por qué* falta un dato importa: un hueco por «sin permiso» no es uno porque «no pasó nada». Pero **la app no guarda el motivo en el panel**: el análisis ve solo una celda vacía (limitación L-4.1, sección 4.10).

## 4.2 Dos relojes: día lógico y día de calendario

El **día lógico** de la app va de 05:00 a 04:59 (capítulo 2): un evento a la 01:00 cuenta para el día *anterior*. El **día de calendario** va de 00:00 a 24:00. **No todas las fuentes usan el mismo:**

| Usan **día lógico** | Usan **día de calendario** |
|---|---|
| Ubicación (km), movimiento, wifi/audio/carga/luz/presión, notificaciones, música, fotos, llamadas/SMS, Spotify, Google Takeout, WhatsApp, Kindle, ejercicio del reloj y, **desde el esquema 13, pantalla y pasos** | Corazón, oxígeno, calorías y demás lecturas del reloj, agenda, clima/aire, ciudades y países (`Mundo`), Cold Turkey y MacroFactor; pantalla y pasos **hasta el esquema 12** |

: Tabla 4.1. Dos relojes: día lógico y día de calendario

El **sueño** del reloj va al **día en que despiertas**.

**Ejemplo, como era hasta el esquema 12.** Un lunes usas el teléfono de 00:30 a 01:30. Esa hora de **pantalla** se sumaba al **martes** (calendario), pero las **notificaciones** y el **wifi** de esa hora iban al **lunes** (lógico): dentro de una misma fila, las variables no hablaban del mismo día. Es **H-3-1** (gravedad alta, porque afecta el uso nocturno, el más interesante para el sueño). Para quien se duerme antes de la medianoche el efecto es pequeño; para quien usa el teléfono de madrugada, es real. **[Aproximación]** mientras queden variables por calendario.

**Lo que cambió en el esquema 13.** La pantalla y los pasos pasan al día lógico de aquí en adelante. Cada día guarda qué ventana usó (`PantallaDia.ventana`, `SaludDia.ventana_pasos`: `"logica"` o `"calendario"`), y el panel exportado lleva `ventana_pantalla` y `ventana_pasos` para analizar por separado los dos tramos de la serie. La app no recalcula sola los días de antes (Android solo guarda unos días de eventos de uso). Desde la 10c, el diccionario marca «por día de calendario» las variables que siguen así (`Diccionario.CALENDARIO`). H-3-1 queda **resuelto en parte**. En los días nuevos también se corrige **H-3-2**: `primer_uso_h` ya no toma un uso de la 01:00 como «primer uso», y la corrección «+24 si la hora es menor a 5» de `ultimo_uso_h` sí actúa (`Estadistica.panel`).

**La hora local de entonces (desde 10f; H-1-7, D.109 y D.110).** Hasta la v91, cada instante se convertía con la zona horaria que el teléfono tiene **hoy**: tras un viaje a Madrid (UTC+2), una hora de dormir de las 23:30 de allá se leía, ya de vuelta en UTC−6, como las 15:30. Desde 10f la app guarda cada cambio de zona del teléfono (`Config.zonas`; se anota al abrir la app, en cada pulso y con el aviso `TIMEZONE_CHANGED`) y convierte cada instante con la zona de entonces (`Fechas.zonaEn`); una nota con desfase se lee con el suyo. Así lo hacen Health Connect, la pantalla, las fotos, WhatsApp, Kindle y las ubicaciones. Un día con cambio de horario dura 23 o 25 horas (el de un viaje, otras), y `vm_panel.csv` lo marca con `desfase_utc_h` y `cambio_horario`. Para lo de antes, el historial se arma con las notas: un periodo cuenta con otro desfase si **dos notas seguidas** lo traen; lo demás queda con la zona de hoy. **[Diseño propio]**.

## 4.3 Pantalla (`sensores/Pantalla.kt`)

Android guarda una bitácora de eventos de uso, y la app arma con ella «tramos»: cuando una app pasa a primer plano (`ACTIVITY_RESUMED`) se cierra el tramo anterior y se abre uno nuevo (**no cuentan** el lanzador, es decir, la pantalla de inicio, ni la propia Vestigium Mei); el tramo termina cuando la app sale (`ACTIVITY_PAUSED`), cuando la pantalla se apaga (`SCREEN_NON_INTERACTIVE`) o al final de la ventana. Los **desbloqueos** son los eventos `KEYGUARD_HIDDEN`. `total_min` suma todos los tramos, **sin descartar nada**; `por_app_min` guarda solo apps con **≥ 1 minuto** (máximo **40**), y cada tramo se reparte entre las horas que cruza (`por_hora_min`).

Es **tiempo de apps en primer plano**, no «pantalla encendida»: un video que ves sin tocar cuenta; la pantalla de inicio encendida no. Por eso **no coincidirá exactamente** con «Bienestar digital». **[Diseño propio]** la definición. Se mide de forma pasiva porque lo que la gente *dice* que usa los medios digitales **solo se correlaciona moderadamente** con lo que registran los dispositivos (Parry et al., 2021). **[Validado]**.

Si el reloj registró la noche, se calculan `cama_min` (pantalla en la **última hora antes de dormirte**), `dormir_tras_tel_min` (de soltar el teléfono, último uso en las 3 horas previas, a dormirte) y `tel_tras_despertar_min` (de despertar al primer uso en las 4 horas siguientes). Sirven para *cruzar* con tu sueño, no para afirmar causalidad. **[Aproximación]**: heredan el error de la hora de dormir del reloj.

## 4.4 Ubicación, distancia y lugares

### 4.4.1 Puntos y distancia (`sensores/Ubicacion.kt`)

En cada pulso, con permiso de ubicación *en segundo plano*, la app pide una lectura de **potencia equilibrada** (`PRIORITY_BALANCED_POWER_ACCURACY`: en general usa wifi y antenas antes que el GPS; gasta poca batería, pero es menos precisa). Además registra **geocercas**: un círculo de radio `max(radio del lugar, 80 m)` por cada lugar guardado (hasta **95**), y Android avisa al **entrar** y al **salir**.

Según la documentación de Android (`Location.getAccuracy`), el campo `precision` es el **radio horizontal con 68 % de confianza**: hay 68 % de probabilidad de que la posición verdadera esté dentro de ese círculo. Es normal ver errores de decenas o cientos de metros. Desde la 10d, un punto importado **sin** precisión cuenta como de **100 m** (`Db.PRECISION_SIN_DATO`), no de 0 m. **[Diseño propio]**.

`Movilidad.kmPorDia` suma la distancia en línea recta entre cada par de puntos consecutivos (fórmula del *haversine*, radio terrestre 6 371 km) **si** pasaron entre **1 segundo y 2 horas**, la velocidad implícita es menor a **70 m/s** (descarta saltos imposibles) y la distancia es mayor a **30 m** (descarta el «temblor» del GPS quieto). Va al día lógico del segundo punto; un día con puntos pero sin tramos que cuenten vale **0 km**. Es una **cota inferior**: la línea recta siempre es ≤ el camino real, y se queda más corta los días con menos puntos. **[Aproximación]** (**H-3-5**, resuelto por documentación: no se pondera por fuente).

### 4.4.2 Lugares y visitas (`data/Lugares.kt`)

Cuando llevas un rato en un sitio que no tienes guardado, la app pregunta si es un «lugar especial». La regla (`estancia`; por omisión radio **75 m** y **20 min**): toma los puntos recientes, **sin fotos** y con **precisión ≤ 120 m**; hacen falta **≥ 2 puntos** y el último debe tener **≤ 25 min**; desde el último punto va hacia atrás **mientras cada punto esté a ≤ 75 m del último** (no del centro del grupo), y si ese tramo dura **≥ 20 min**, es candidato. Para no molestar (`revisar`): solo entre **08:00 y 22:00**, **máximo 2 preguntas al día** y **3 veces** por sitio.

Como los puntos llegan cada ~15 minutos, con 2 puntos no se llega a 20 min: en la práctica hacen falta **3 lecturas dentro del radio**, unos **30 minutos** (Figura 4.1).

![Figura 4.1. Detección de un lugar nuevo con datos sintéticos: un punto cada 15 minutos y un radio de 75 m; la app pregunta en el minuto 60, 30 minutos después de llegar. Script: `figuras/fig05_deteccion_lugar.py`.](figuras/fig05_deteccion_lugar.png){width=100%}

Al guardar un lugar, `reconstruir` rehace las visitas de los **30 días** previos (huecos de hasta **2 horas** no rompen una visita; **solo cuentan visitas de ≥ 10 minutos**). Detectar *estancias* (*stay points*) con un radio y un tiempo mínimo es la técnica estándar de la minería de trayectorias GPS (Zheng, 2015); la app usa una versión simple, sin agrupamiento por densidad. **[Aproximación]**. Falla en interiores (el GPS «salta»), con estancias de menos de 30 minutos y con dos lugares a menos de ~75 m (se funden).

## 4.5 Movimiento y «percepción»

**Movimiento (`sensores/Actividad.kt`).** El **reconocimiento de actividad** de Android avisa cuando *empieza* o *termina* «a pie», «corriendo», «en bici», «en vehículo» o «quieto», y la app suma los minutos; parte los tramos en el corte de las **05:00** e **ignora los de más de 20 horas** (probable aviso perdido). Es un clasificador de Android, no validado en esta app. **[Aproximación]**. «Quieto» es ambiguo (el teléfono en la mesa mientras caminas).

**La regla de los minutos (`sensores/Percepcion.kt`).** Cada pulso toma una **muestra** del estado del teléfono y le atribuye **el tiempo transcurrido desde la muestra anterior**:

- Sin muestra previa → **15 min**.
- Si pasaron **más de 2 horas** → solo **15 min** («para no inventar»).
- En otro caso → los minutos reales, **con tope de 60**.

Con muestras a las 10:00, 10:16, 10:31 y 12:45 se cuentan 15 + 16 + 15 + 15 = 61 min, aunque entre 10:31 y 12:45 pasaron 134. La app prefiere **subestimar** a inventar: `wifi_min + sin_wifi_min` (más `wifi_sin_dato_min`, desde 10f) **no suma 1 440**; son **minutos observados**. **[Diseño propio]**.

**Qué se mide en cada muestra:**

- **Wifi (desde 10f, D.112; H-3-7).** Si alguna red activa es wifi según `ConnectivityManager`, cuenta como wifi aunque Android oculte el nombre («wifi (sin nombre)»). Va a `sin_wifi_min` solo si el wifi está apagado o el sistema dice que no hay red wifi; si no se pudo saber, a `wifi_sin_dato_min`. Hasta la v91, «no se pudo saber» contaba como «sin wifi», así que `sin_wifi_min` puede bajar desde 10f. No se probó en un teléfono. **[Diseño propio]**.
- **Audio, música, carga y batería.** La **carga nocturna** (enchufado entre las **20:00 y las 04:00**, **> 3 h y < 16 h**) se guarda como marca aproximada de cuándo dormiste.
- **Luz.** `luz_alta_min` = minutos con **≥ 1 000 lux**. El sensor mide en el bolsillo, no donde están tus ojos, y subestima el aire libre; `aire_libre_min` toma el **máximo** entre la luz alta y el tiempo a pie, corriendo o en bici: un *proxy*, es decir, una medida indirecta (no mide el aire libre, sino la luz). Los 1 000 lux son una regla propia, sin estándar detrás. **[Aproximación]**.
- **Presión.** Se **descartan lecturas fuera de 300–1 100 hPa** (`Percepcion.RANGO_HPA`; hPa = hectopascales, ~1013 a nivel del mar).

**Presión y altitud (H-3-3, resuelto en 10d).** El filtro anterior, 850–1 090 hPa, descartaba **todas** las lecturas por encima de unos **1 450 m**. El rango nuevo es el de medida del sensor BMP280 (Bosch Sensortec, 2021); el LPS22HB mide de 260 a 1 260 hPa (STMicroelectronics, 2016). Qué sensor trae tu teléfono **no se comprobó**. **[Aproximación]**. Del día se guarda también el **cambio de presión en ~3 horas** de mayor valor absoluto, para *poder cruzarlo* con el ánimo o el dolor (la evidencia es mixta). Como el barómetro tiene un **sesgo constante**, `Barometro.ajuste` le suma la **mediana** (el valor del medio al ordenar, que aguanta mejor los valores raros que el promedio) de sus diferencias con Open-Meteo en los **60 días más recientes con ≥ 8 lecturas** (necesita **≥ 3 días**); los *cambios* no se ven afectados. **[Diseño propio]**.

## 4.6 Ruido, notificaciones, fotos, agenda y llamadas

**Ruido (`sensores/Ambiente.kt`).** Al registrar algo, puede escuchar el micrófono **1.5 segundos** y **descartar el audio**. Calcula la raíz del promedio de los cuadrados de las muestras (RMS, la intensidad media de la señal) y convierte:

$$\text{dB} = 20 \cdot \log_{10}\left(\frac{\text{RMS}}{32768}\right) + 90\qquad\qquad (4.1)$$

Es un **nivel relativo**, no decibeles de presión sonora: la precisión de las apps de sonómetro **varía mucho entre dispositivos y apps** (Kardous y Shaw, 2014). Sirve para comparar tus lugares entre sí. **[Aproximación]**.

**Notificaciones y música (`sensores/Oyente.kt`).** Un servicio del sistema **solo anota** de qué app y a qué hora llega cada notificación; **no lee ni guarda el texto**, y no cuenta actualizaciones de la misma notificación en **menos de 60 segundos**. El mismo servicio anota lo que se reproduce. Contar notificaciones mide **interrupciones externas** (Mohr et al., 2017, sobre el «sensado personal»). **[Aproximación]**. Si el servicio no está activo, esas notificaciones **nunca se cuentan**.

**Fotos (`sensores/Galeria.kt`, `FotosLugar.kt`).** Solo **metadatos**: cuántas fotos y videos hubo ese día lógico; **las imágenes nunca salen del teléfono**. Para el mapa, se lee el lugar que la cámara guarda en el archivo (**EXIF**). Son posiciones aisladas y de fiabilidad variable. **[Diseño propio]**.

**Agenda (`Agenda.kt`)**: eventos y **minutos ocupados** (uniendo los que se enciman), por día de calendario. **Llamadas y SMS (`Comunicacion.kt`)**: solo **conteos** y **personas distintas** (cada número se reduce a sus **últimos 8 dígitos** y se descarta si tiene menos de 6); **los números se usan en memoria y se descartan**. **[Aproximación]**: un número no es una persona.

## 4.7 Clima, aire y luna (`sensores/Entorno.kt`, `data/Calculos.kt`)

El clima viene de **Open-Meteo**; solo se envían **coordenadas redondeadas a 0.01°** (~1 km) y fechas, **nunca tu ruta**. `lugarDe` toma la **mediana** de latitud y de longitud de los puntos del día. Se piden temperatura, lluvia, sol, viento, humedad, nubosidad, presión, **PM2.5** y **PM10** (partículas de menos de 2.5 y 10 micrómetros) y calidad del aire (versión completa, §4.4.10).

**Dos fuentes (H-3-4).** Si el día tiene **más de 80 días**, la app pide el **archivo histórico** (reanálisis: el clima pasado reconstruido con modelos y observaciones, con conjuntos como ERA5; Hersbach et al., 2020); si es más reciente, el **pronóstico**. Hasta la v91, un día con **al menos 2 días** quedaba completo y no se volvía a pedir, así que los días sincronizados con entre 2 y 80 días **quedaban para siempre con valores del pronóstico**, sin que constara la fuente. Desde 10f (D.111), cada día guarda su fuente (`EntornoDia.fuente`; en lo de antes se deduce con la misma regla de 80 días), y los días del último año que vinieron del pronóstico se vuelven a pedir al archivo cuando pasan de 80 días. El archivo no trae el **índice UV**: `uv_max` sigue siendo solo del pronóstico (si el día lo tenía, se conserva). Columnas: `fuente` en `vm_entorno.csv` y `clima_reanalisis` en `vm_panel.csv`; los últimos 80 días siguen siendo de pronóstico. **[Aproximación]**. No se probó con la red. Y es el clima de la zona, no el de donde estuviste.

**La luna** se **calcula** (`Luna.fase`): edad = días desde una luna nueva de referencia (6-ene-2000 18:14 UTC) **módulo 29.530588853** (el mes sinódico medio); **iluminación = (1 − cos(2π·fase)) / 2**, al mediodía local. Como no depende de ti, sirve de **control negativo**: si algo «se asocia con la luna», sospecha de azar.

## 4.8 El reloj (Health Connect)

La app lee el reloj a través de **Health Connect**, el sistema de salud de Android donde otras apps escriben sus datos; solo pide **permisos de lectura**.

**Sueño (`Salud.noche`).**

1. Lee las sesiones que **terminan** ese día de calendario, desde el mediodía anterior, y toma **una sola fuente por noche** (prefiere la que trae **etapas**), para no sumar la misma noche dos veces.
2. **Une sesiones separadas por menos de 1 hora**. **La noche** es el bloque más largo que **empezó antes del mediodía**; los demás son **siestas** (`siesta_reloj_min`). Lo que marcaste «no cuenta» no entra.
3. **Minutos dormido:** si las etapas cubren **al menos 60 %** de la sesión, se suman las etapas de sueño; si no, la sesión completa menos lo despierto.

*Ejemplo:* una sesión de 23:40 a 07:10 dura 450 min; las etapas suman 400 (≥ 60 % de 450 = 270), así que se usan: profundo 70 + REM 95 + ligero 210 = **375 min**, `sueno_horas = 6.25`. Una corrección **manual** (`sueno_manual`) no la sobrescriben ni la sincronización ni, desde el esquema 14, un respaldo que no la tenía (`Fusion.salud`).

**Confiabilidad.** Contra la polisomnografía (el estándar de laboratorio), siete dispositivos de consumo tuvieron sensibilidad para el sueño ≥ 0.93 pero **especificidad para la vigilia de solo 0.18–0.54**, y tres **sobrestimaron** el sueño total (Chinoy et al., 2021; dispositivos distintos al de la app). **[Validado]** el patrón; **no hay validación del reloj de esta app**. Las **etapas** son lo menos fiable.

| Variable | Cómo se calcula |
|---|---|
| `fc_media`, mínimo, máximo | Agregación de Health Connect sobre el día de calendario |
| `fc_reposo` | Promedio de los registros de reposo del día |
| `fc_sueno`, `spo2_min` | Frecuencia cardiaca media y oxígeno mínimo durante la noche |
| `hrv` (RMSSD) | Promedio de los registros de la noche; sin noche, de 18:00 del día anterior a 14:00 |

: Tabla 4.2. El reloj (Health Connect)

El RMSSD es la **raíz cuadrada del promedio de los cuadrados de las diferencias entre intervalos de latidos consecutivos** (en ms): el corazón no late a intervalos iguales, y un valor mayor suele reflejar más actividad parasimpática («descanso y recuperación») (Task Force of the ESC and NASPE, 1996; Shaffer y Ginsberg, 2017). **[Validado]** el significado; **[Aproximación]** el uso: varía mucho entre personas y días, así que se compara con **tu propia línea base**.

Una revisión de dispositivos de consumo encontró **pasos y frecuencia cardiaca razonablemente exactos en laboratorio** (frecuencia cardiaca con error mediano ≈ −1 %; pasos con error medio ≈ −9 % y mediano ≈ −2 %), pero **ninguna marca midió bien el gasto de energía** (Fuller et al., 2020). **[Validado]** para pasos y corazón; de `cal_activas` y `cal_totales` conviene desconfiar. Las demás lecturas se guardan tal como el reloj o la báscula las escriben.

| Dato del reloj | Calidad esperada |
|---|---|
| Pasos, frecuencia cardiaca en reposo | Buena |
| Sueño total | Razonable; tiende a sobrestimar |
| HRV nocturno | Razonable, ruidoso entre noches |
| Etapas del sueño, calorías | Baja |

: Tabla 4.3. El reloj (Health Connect)

## 4.9 Importaciones: lo que cargas tú

Traen datos que el teléfono no ve, con tres riesgos: llegan **a destiempo**, **parciales** y en **formatos que cambian**.

**Spotify (`data/Spotify.kt`).** (a) La **conexión directa** lee cada **~25–30 min** las **últimas 50** reproducciones; si no se consulta a tiempo, las de más **se pierden**. Como Spotify da la hora en que **terminó** la canción y la app resta la duración completa, una canción parada a la mitad queda con un inicio **demasiado temprano** (**H-3-9**; error de minutos; resuelto por documentación: la app solo recibe la duración de la canción). (b) El **historial descargable** trae cuánto escuchaste de verdad (`ms_played`) y **reemplaza** lo anterior en esos días, lo que corrige ese error. `canciones` cuenta solo reproducciones de **≥ 30 s**.

**Google Takeout y Kindle.** Del historial de YouTube y búsquedas se quitan los anuncios; un «video visto» es un **evento** de abrirlo, **no una duración**. De comentarios **solo se guarda la fecha**. En Kindle, la fecha es **cuándo subrayaste**, no cuándo leíste.

**WhatsApp.** **Solo conteos y tiempos, ningún texto**. Una **conversación nueva** es un mensaje que llega **más de 4 horas** después del anterior; un **tiempo de respuesta** cuenta **si fue en menos de 12 horas**; un chat es **de grupo** con **más de 2 autores**. **Fechas ambiguas (H-3-10, resuelto en 10e, D.105):** si en todo el chat ni el primer ni el segundo número de la fecha pasan de 12, la app prueba día/mes y mes/día y se queda con la lectura en que los mensajes van en orden, como vienen en el archivo (menos retrocesos de fecha); con empate, día/mes (hasta la v90, siempre día/mes). Un chat de un solo día sigue sin poder decidirse. Los avisos de multimedia se reconocen en español, inglés, francés, portugués, italiano y alemán (`Importaciones.MULTIMEDIA`). **[Aproximación]** las medidas; **[Diseño propio]** la regla de las fechas.

**Cold Turkey (laptop).** Minutos por app y sitio, repartidos en productivo, ocio, social y otro con listas de palabras. Desde la 10d (`ColdTurkey.coincide`), en los **sitios** la palabra debe ocupar **partes completas del dominio** («x.com» coincide con `m.x.com`, ya no con `dropbox.com`), lo que corrige **H-3-6**. **[Diseño propio]**.

**MacroFactor (`data/Macrofactor.kt`).** Nutrición, alimentos y series de entrenamiento del `.xlsx`; la hora de la **última cafeína** (desde 10e, `cafeina_ultima_h` del panel sale de las tomas de MacroFactor y de la app, por día lógico; capítulo 5); series efectivas **sin calentamientos**. Lo que registra **depende de lo que anotas**, y su gasto energético es un **algoritmo propio de MacroFactor**, no una medición. El 1RM estimado se explica en el capítulo 5. Otras importaciones, en la versión completa, §4.6.7.

## 4.10 Mundo sin conexión, huecos y amenazas a la validez

**El mundo sin conexión (`data/Mundo.kt`).** Para convertir coordenadas en países y ciudades la app no manda tus ubicaciones a internet: trae fronteras de Natural Earth y unas **135 mil localidades de GeoNames**. El país se decide con la regla **par-impar** (*ray casting*, que dice si un punto cae dentro de un contorno); la ciudad es la localidad más cercana a **menos de 3 km** o la principal (≥ 5 mil habitantes) a menos de **30 km**. **En la frontera** el país puede ser el equivocado. **[Aproximación]**.

**Cómo se ve un dato que falta.** Una celda sin dato queda **vacía**, no en 0: los datos de sensores **nunca se rellenan con ceros**. Si **el hueco no es azar** (el reloj se agota justo las noches de poco sueño), el resultado se **sesga** aunque se ignoren las celdas vacías (datos faltantes «no al azar», Rubin, 1976; capítulo 2).

| # | Amenaza | Estado |
|---|---|---|
| L-4.1 | No se guarda el motivo de un hueco | Solo se ve en vivo |
| L-4.2 | Dos criterios de día | Resuelto en parte (H-3-1) |
| L-4.3 | Minutos «observados» por muestreo | Subestiman a propósito |
| L-4.4 | Proxies (luz → aire libre; apps ≠ pantalla encendida) | Sin validar |
| L-4.5 | Instrumentos de consumo | Sin calibración propia |
| L-4.6 | Importaciones tardías y parciales | Recordatorios |
| L-4.7 | Procedencia mixta del clima | Resuelto en 10f (H-3-4): fuente por día y reanálisis a los 80 días; el UV, solo del pronóstico |
| L-4.8 | Presión descartada en altura | Resuelto en 10d (H-3-3) |
| L-4.9 | Medir cambia la conducta | Inevitable en N-de-1 |

: Tabla 4.4. Mundo sin conexión, huecos y amenazas a la validez

## 4.11 En resumen

1. Los datos pasivos llegan por un **pulso de ~15 min** y una **sincronización diaria**; los importados, **cuando tú los cargas**. Sin permiso no hay dato, y el motivo del hueco no queda en el histórico.
2. **No todas las fuentes usan el mismo «día»** (H-3-1): desde el esquema 13, pantalla y pasos van por día lógico; el corazón, el oxígeno, la agenda, el clima y las ciudades siguen por calendario, y el diccionario lo marca. Desde 10f, cada instante se lee con la zona horaria que tenía el teléfono entonces (H-1-7).
3. Los minutos de wifi, audio, carga y luz son **estimaciones por muestreo** que **subestiman** a propósito.
4. El **sueño** del reloj está bien cuidado en el cálculo, pero es un dato de consumo: bueno para la duración, débil para la vigilia y las etapas.
5. **Detectar un lugar** necesita unos **30 min** reales, no 20; la distancia es una cota inferior.
6. La 10d corrigió el barómetro (H-3-3), Cold Turkey (H-3-6) y la precisión de los puntos importados; la 10e, las fechas ambiguas de WhatsApp (H-3-10); la 10f, la fuente del clima (H-3-4) y el wifi sin dato (H-3-7), y H-3-5 y H-3-9 quedaron resueltos por documentación. Quedan en parte H-3-1 y H-3-8 (inciso a); ninguno pendiente.


# Capítulo 5. Modelos fisiológicos y variables derivadas

> **Para qué sirve este capítulo.** Algunas variables de la app no se miden: se **calculan con un modelo** a partir de otros datos. La cafeína que te queda al dormir, el alcohol en sangre, la reserva de creatina o el «jet lag social» son **estimaciones**, no mediciones. Este capítulo explica cada modelo: qué calcula, con qué fórmula y parámetros, de dónde salen y cuándo falla. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo de las entregas 10e y 10f se marca «desde 10e» y «desde 10f». Los ejemplos usan datos inventados, y los problemas se numeran H-4-n (por el módulo 4). **Ningún número de este capítulo es una medición de tu cuerpo**: son cuentas con parámetros promedio de la literatura.

## 5.1 Qué se estima y con qué

Casi todos los modelos de este capítulo son de *farmacocinética*: cómo entra una sustancia al cuerpo (**absorción**, el paso del estómago a la sangre), se reparte y sale (**eliminación**, sobre todo por el hígado).

| Qué | Función | Parámetros | Tipo |
|---|---|---|---|
| Cafeína en el cuerpo | `Sustancias.cafeinaEn` | absorción 45 min; vida media 5 h (ajustable) | Aproximación |
| Alcohol en sangre | `Sustancias.alcoholEn` | absorción 30 min; β = 0.15 g/L/h; *r* de Watson o fija | Aproximación |
| Nivel subjetivo | `Sustancias.nivelEn` | tus lecturas de 0 a 10 | Diseño propio |
| Reserva de creatina | `Suplementos.carga` | 28 días para llenar con 3 g al día, 35 para vaciar | Aproximación |
| Otros suplementos | `Suplementos.cantidadEn` | vida media por suplemento | Aproximación |
| Jet lag social | `Analisis.ritmo` | noches de los últimos 56 días | Validado (definición) / Aproximación |

: Tabla 5.1. Qué se estima y con qué

## 5.2 Cafeína en el cuerpo (`Sustancias.cafeinaEn`)

Estima la cafeína en el cuerpo en cada momento. Para cada dosis *D* (mg) tomada hace Δ*t* horas:

$$C(t) = \sum_{\text{dosis}} D \cdot \min\!\left(1, \frac{\Delta t}{0.75}\right) \cdot 0.5^{\Delta t / T_{1/2}}\qquad\qquad (5.1)$$

El primer factor sube en línea recta de 0 a 1 en **45 minutos** (0.75 h): la absorción. El segundo es la eliminación con la **vida media** *T*½, el tiempo que tarda en irse **la mitad** de lo que hay: con 5 h, de 100 mg quedan 50 a las 5 h y 25 a las 10 h. Esto se llama eliminación **de primer orden** (se va una proporción fija por hora). La vida media es `Config.cafeina_vida_media_h`, **5 h por omisión** (ajustable de 2 a 10 h). Las dosis se suman.

**De dónde salen las dosis** (`Sustancias.dosis`): de **MacroFactor** y de lo que anotes en la app. Desde la 10e (D.91) se juntan **toma por toma**: una toma de la app y una de MacroFactor a **90 minutos o menos** y de cantidad parecida (**de la mitad al doble**) son la misma y cuentan una vez, con la hora y los miligramos de la app (la hora de MacroFactor es la del registro, no la de la toma). Antes, si MacroFactor tenía algo ese día, lo anotado en la app se ignoraba. Los 90 minutos y el rango son **[Diseño propio]**. Desde la 10c, un alimento de MacroFactor sin hora legible se pone **al mediodía de su día** (`msComida`) en lugar de perderse.

**Variables.** `cafeina_total_mg` suma las dosis del día lógico; `cafeina_al_dormir_mg` es la cafeína estimada **en el instante en que te dormiste** según el reloj, con las dosis de las **36 horas** previas, y va en el día en que despiertas. `cafeina_ultima_h` es, desde la 10e (D.92), la hora de la última dosis del día lógico en esa misma lista (antes de las 5:00 suma 24); antes salía solo de MacroFactor. `cafeina_mg` sigue viniendo **directo de MacroFactor**, sin el modelo.

**Por qué.** Según el Institute of Medicine (2001), la cafeína se absorbe rápida y casi completamente (alrededor del **99 % en 45 minutos**), y su vida media en adultos sanos es en promedio de **unas 5 horas**, con un rango individual de **1.5 a 9.5 horas**. Nehlig (2018) explica que esa variabilidad depende en gran parte de la enzima hepática CYP1A2, que metaboliza el 95 % de la cafeína y tiene variantes genéticas. **[Aproximación]**: el modelo es razonable para el promedio, pero tu vida media real puede estar en cualquier punto de ese rango.

**Ejemplo (Figura 5.1).** Un café de **95 mg a las 8:00** y uno de **150 mg a las 14:00**; te duermes a las **23:30**. Con *T*½ = 5 h: 95 × 0.5^(15.5/5) = **11.1 mg** más 150 × 0.5^(9.5/5) = **40.2 mg**, así que `cafeina_al_dormir_mg` = **51.3 mg**. Con una vida media de 2.5 h serían **12 mg**; con 9.5 h, **106 mg**. **La misma tarde da una cifra que varía casi nueve veces según la persona**: sirve para compararte contigo mismo, no como dato absoluto.

![Figura 5.1. Cafeína estimada tras dos dosis con tres vidas medias. Script: `figuras/fig25_cafeina.py`.](figuras/fig25_cafeina.png){width=100%}

**Cuándo falla.**

- **Pico bajo y temprano.** Como la rampa se multiplica por la eliminación desde el primer minuto, a los 45 min queda el **90 %** de la dosis. Para dosis separadas por horas la diferencia con un modelo estándar es pequeña. **[Aproximación]**.
- **Una sola vida media para toda la historia**: si la cambias, **todas** las `cafeina_al_dormir_mg` pasadas se recalculan, sin dejar registro. (Este punto y el anterior son H-4-1, resuelto por documentación.)
- **Juntar dos fuentes** (H-4-2, resuelto en 10e): dos cafés distintos y parecidos, uno en cada app y a menos de 90 minutos, cuentan como uno; la misma taza registrada en MacroFactor mucho después cuenta dos veces.
- **Cuatro variables de cafeína** (H-4-12, resuelto en parte): desde la 10e tres salen de las mismas dosis; `cafeina_mg` sigue siendo el nutriente de MacroFactor.

## 5.3 Alcohol en sangre: el modelo de Widmark (`Sustancias.alcoholEn`)

**Qué hace.** Estima la concentración de alcohol en sangre (g/L, aproximadamente lo mismo que «por mil») a lo largo de la noche. La fórmula clásica de Widmark dice que la concentración es el alcohol tomado repartido en el agua del cuerpo, menos lo que el hígado ya quitó:

$$C(t) = \frac{A}{r \cdot W} - \beta \cdot t\qquad\qquad (5.2)$$

con *A* = gramos de alcohol, *W* = peso (kg), *r* = **factor de Widmark** (la fracción del peso en la que se reparte el alcohol, casi toda el agua del cuerpo) y β = velocidad de eliminación. *r* · *W* funciona como el «tamaño del recipiente»: con la misma cantidad, uno más chico da una concentración más alta. El alcohol se elimina **de orden cero** (una **cantidad fija** por hora), porque la enzima que lo procesa trabaja a su máxima velocidad desde los primeros tragos (Jones, 2010).

**Cómo lo hace.** Una **simulación en pasos de 5 minutos**: cada dosis entra repartida en **30 minutos**; en cada paso el cuerpo quita β · *r* · *W* · (5/60) gramos, con **β = 0.15 g/L por hora**, sin bajar de 0; la concentración es gramos en el cuerpo ÷ (*r* · *W*). Una **bebida estándar** son **14 g** (`GRAMOS_BEBIDA`), la definición de Estados Unidos (NIAAA, s. f.).

**Los parámetros** (desde la 10c, calculados **para cada día** con `rYPeso`):

- *W* (`pesoDe`): el peso **más reciente hasta ese día** entre la báscula de Health Connect y la tendencia de MacroFactor (el mismo día gana la báscula); si no hay ninguno antes, el primero posterior. Solo sin ningún peso registrado se usan **70 kg** (`PESO_OMISION`).
- *r*: con un **peso real**, sale del **agua corporal total** de Watson et al. (1980) (`watsonR`):
  - hombres: agua (L) = 2.447 − 0.09516 · edad + 0.1074 · estatura (cm) + 0.3362 · peso (kg);
  - mujeres: agua (L) = −2.097 + 0.1069 · estatura (cm) + 0.2466 · peso (kg);
  - *r* = agua / (0.80 · peso), acotada entre **0.45 y 0.95**; el 0.80 (`AGUA_SANGRE`) es la proporción de agua en la sangre que usan Watson et al. (1981).

  Hacen falta el sexo, la estatura (entre **100 y 250 cm**) y, en los hombres, la edad. Si falta algo, o el peso es el de omisión, se usa la *r* fija (`widmarkR`): **0.68** «hombre», **0.55** «mujer», **0.62** si no dijiste nada.

**De dónde salen las dosis.** De MacroFactor y de los episodios que anotas. Desde la 10e (D.91), un episodio y lo de MacroFactor entre su inicio y su fin (**±90 minutos**) son lo mismo, y cuenta la fuente con más gramos **[Diseño propio]**; antes, si MacroFactor tenía alcohol ese día, se ignoraban los episodios.

**Variables.** `alcohol_bebidas` = gramos del día ÷ 14; `alcohol_estimado_max`, desde la 10e (D.88 y D.89), el máximo de la simulación desde la primera dosis del día hasta **14 horas** después (o hasta la primera dosis del siguiente día con alcohol), **incluyendo las dosis de las 24 horas previas** y con muestras **cada 5 minutos**, como la lista de episodios; `alcohol_al_dormir`, la concentración al dormirte con las dosis de las **18 horas** previas; `alcohol` (0/1), 1 el día lógico de cada dosis (o del inicio de un episodio sin cantidad).

**Por qué.** **β = 0.15 g/L/h** es el valor que Jones (2010) recomienda como **promedio para bebedores moderados**: en ayunas, de 0.10 a 0.15; después de comer, de 0.15 a 0.20; con consumo intenso, hasta 0.25–0.35. **[Validado]** como promedio. Los *r* de 0.68 y 0.55 son los valores clásicos de Widmark; el 0.62 es un punto intermedio **[Diseño propio]**. En realidad *r* depende de la composición corporal: según Searle (2015), es el agua corporal total dividida entre el peso por la proporción de agua en la sangre. Usar Watson es **[Aproximación]** (mejor que la fija): según Searle (2015), la *r* conserva una incertidumbre de alrededor de un 9 %. **El valor 0.80 [sin verificar]**: no se leyó en el texto original de Watson et al. (1981). El error de la concentración calculada **no es fijo** (Searle, 2015): el modelo da un orden de magnitud, no una cifra al centésimo.

**Ejemplo.** Tres bebidas estándar (42 g) de 20:00 a 23:00; la app las coloca a las **20:00, 21:03 y 22:06** (repartidas en el primer 70 % del episodio). Un **hombre de 75 kg** con *r* = 0.68 tiene *r* · *W* = 51: si todo entrara de golpe serían 42/51 = 0.82 g/L, pero con absorción y eliminación simultáneas la simulación da un **máximo de 0.42 g/L a las 22:35** y llega a **0 a la 01:25**. Una **mujer de 60 kg** (*r* = 0.55) llega a **0.87 g/L**. Con la *r* de Watson, el mismo hombre a los 30 años y 180 cm tiene *r* = **0.736** y su máximo baja a **0.36 g/L**. El error que más pesaba no era *r*, sino el **peso**: un hombre de 90 kg calculado con 70 kg sale con un máximo de **0.61 g/L** en lugar de **0.41 g/L** (más tablas y una figura en la versión completa, §5.4.4).

**Cuándo falla.**

- **Parámetros fijos** (H-4-4, resuelto en parte): β no se ajusta a si comiste; sin ningún peso se usan **70 kg sin avisar** en la pantalla.
- **No muestra incertidumbre**: dice «≈ 0.45 g/L estimado», pero el error razonable es de varias décimas.
- **Absorción de 30 minutos fija**: con el estómago lleno el pico es más bajo y tardío.
- **Cada día se simulaba por separado** (H-4-5, resuelto en 10e): un episodio que cruzaba las 5:00 dejaba las bebidas posteriores en el día siguiente, que arrancaba de cero. Ahora ese día arrastra lo anterior; un pico que llega después de la primera bebida del día siguiente cuenta en ese día.
- **Dos máximos** (H-14-5, resuelto en 10e): el panel miraba cada 10 minutos y la lista de episodios cada 5, y podía dar hasta 0.0125 g/L menos. Con un episodio por día y sin alcohol en las horas previas, ahora dan lo mismo.

## 5.4 Episodios con nivel subjetivo (`Sustancias.nivelEn`)

Para alcohol y cannabis puedes decir **qué tan fuerte lo sientes** de 0 a 10. Con lecturas, la curva las une con líneas rectas (y baja a 0 al terminar el episodio); solo con el máximo, es un trapecio que sube de 0 al máximo en el primer 35 % del episodio, se queda hasta el 65 % y baja a 0 al final. Si el episodio quedó abierto de otro día, termina **4 horas** después del inicio (`finEfectivo`). Variables: `alcohol_nivel_max`, `cannabis_nivel_max`, `alcohol_horas`, `cannabis_horas`. **[Diseño propio]**: son **autorreportes**, no estimaciones fisiológicas; tener la medida estimada y la sentida es útil.

## 5.5 Suplementos (`data/Lote3.kt`)

`Suplementos` ofrece tres modelos, elegibles por suplemento y con parámetros editables. Para todos se guardan `supl_<id>_dosis` y `supl_<id>_tomado` (0/1).

**Carga: la creatina (`Suplementos.carga`).** La creatina no sube y baja en horas: se **acumula** en el músculo durante semanas. El modelo representa esa reserva de 0 a 100 % (su **saturación**):

$$s = 1 - 0.05^{1/\text{carga\_días}}, \qquad b = 1 - 0.05^{1/\text{lavado\_días}}\qquad\qquad (5.3)$$

Un día con dosis *d* se llena una fracción de **lo que falta**, $\left(1 - (1-s)^{d/\text{ref}}\right)\cdot(100 - \text{nivel})$; un día sin dosis se vacía una fracción de **lo que hay** (nivel × (1 − *b*)). *ref* es la dosis de referencia (`Suplementos.dosisReferencia`): desde la 10e (D.98), **3 g en la creatina**, aunque el catálogo diga 5 g. Con 28 y 35 días, *s* = 0.1015 y *b* = 0.0820: con 3 g diarios, al día **28** queda lleno al **95 %**, y sin tomarla, a los **35 días** queda el **5 %** de lo que había. Con 5 g diarios se llega al 95 % el **día 17** (28 × 3/5 = 16.8). Las variables son `supl_creatina_nivel` y `creatina_saturacion_pct`.

Hultman et al. (1996) midieron que la creatina del músculo aumentaba alrededor de un **20 %** tras 6 días a 20 g diarios, que **30 días después de dejarla ya no difería** del valor inicial y que con **3 g diarios** se alcanzaba el mismo 20 % en **28 días**. **[Aproximación]**; que 5 g llenen en 17 días sale de la forma del modelo, no del estudio **[Diseño propio]**. **Cuándo falla** (H-4-6, resuelto en 10e): hasta la 10e la referencia era 5 g y 3 g diarios daban solo 83 % al día 28, más lento que la fuente. La referencia de 3 g está fija en el código: cambiar la dosis en la app ya no cambia la velocidad. Y el 100 % es del modelo, no una concentración: no considera la dieta, el punto de partida ni a quienes casi no responden.

**Vida media (`Suplementos.cantidadEn`).** La cantidad es la suma de cada toma por 0.5^(horas / vida media), **sin fase de absorción**, evaluada **al final del día lógico** (las 5:00 del día siguiente). Desde la 10e (D.98), `supl_<id>_nivel` solo existe con vida media de **6 h o más** (`VIDA_MEDIA_MIN_NIVEL`, **[Diseño propio]**).

- **Vitamina D, 360 h (15 días)**: coincide con la vida media de **25(OH)D**, la forma que circula en sangre (Jones, 2008). **[Aproximación]**: el modelo suma «UI en el cuerpo», que no es lo que mide un análisis; con 2000 UI diarias se estabiliza en unas **44 000 UI**, útil solo como tendencia.
- **Melatonina, 1 h**: la vida media oral va de unos 28 a 126 minutos (Harpsøe et al., 2015). **[Validado]** como orden de magnitud. Pero tomada a las 23:00, a las 5:00 queda el 1.6 %: `supl_melatonina_nivel` **era casi siempre ≈ 0**, y desde la 10e ya no existe (H-4-7, resuelto en parte); la variable útil es `supl_melatonina_tomado`.
- **Magnesio, 12 h; omega-3, 60 h**: sin fuente que los respalde **[sin verificar]**.

**Exposición: los probióticos (`Suplementos.probioticosEstimados`).** Solo se cuentan **porciones**: a lo anotado se suma una por cada alimento de MacroFactor cuyo nombre contenga una palabra clave configurada (como «yogur»). Desde la 10c, `probioticos_estimado` queda vacía los días sin bitácora de MacroFactor. **[Diseño propio]**: no dice nada de cultivos vivos (H-4-8).

## 5.6 Ritmo de sueño y jet lag social (`Analisis.ritmo`)

**Qué hace.** La herramienta «Ritmo» de Análisis muestra a qué hora te duermes y despiertas, cuánto duermes antes de días laborales y libres, la **regularidad** y el **jet lag social**: la diferencia entre el **punto medio del sueño** (la hora a la mitad entre dormirte y despertar) en días libres y en laborales, como si cada fin de semana viajaras a otro huso horario (Wittmann et al., 2006). No crea variables del panel.

**Cómo.**

1. Solo noches que terminan en los **últimos 56 días** (`Analisis.recientes`, desde la 10c).
2. **Noches del reloj**: si el inicio es antes de las 12:00 se le suman 24 (dormirte a la 1:00 cuenta como 25.0, para promediarlo bien con las 23:00). Desde la 10f (D.109; H-1-7), cada hora se lee con su propio desfase, en la hora local de donde dormiste; antes se pasaba a la zona de hoy, y una noche de 23:30 a 07:30 en Madrid (UTC+2), leída de vuelta en UTC−6, «duraba» 32 h y se descartaba.
3. **Noches del teléfono**, si faltan del reloj: inicio = **último uso** del día lógico (a las **18:00 o después**, o de madrugada); fin = **primer uso** del día siguiente (a las **4:00 o después**). Con **7 o más** noches del reloj se usan solo esas; si no, se completan con las del teléfono, y la pantalla lo avisa.
4. Se descartan noches de menos de **2 h** o más de **16 h**; hacen falta al menos **5**.
5. **Días libres** (`Fechas.esDiaLibre`): el día en que despiertas es **sábado, domingo** o, si dijiste que vives en México, un **festivo** de la Ley Federal del Trabajo, art. 74 (`Fechas.esFestivoMx`; lista en la versión completa, §5.7.2). La misma regla da `festivo` y `dia_libre` en el panel exportado.
6. **MSW** = promedio de los puntos medios laborales; **MSF**, de los libres (cada uno con al menos 2 noches). **Jet lag social** = |MSF − MSW|.
7. **Regularidad** = desviación estándar de la hora de dormirte («varía ±*N* min»).

**Por qué.** Wittmann et al. (2006) lo definieron como la discrepancia entre el horario biológico y el social y lo asociaron con peor bienestar y más consumo de estimulantes; Roenneberg et al. (2012) lo asociaron con un índice de masa corporal más alto, más allá de la duración del sueño. **[Validado]** como definición y como variable asociada a la salud. La pantalla dice que «más de 1 hora se ha asociado con peor ánimo y salud»: **el umbral de 1 hora [sin verificar]**. Para la regularidad hay medidas específicas (el índice de Phillips et al., 2017); la desviación estándar es más simple. **[Aproximación]**. Las noches del teléfono son un proxy (una medida indirecta): usar el teléfono por última vez no es dormirte; Cuttone et al. (2017) estimaron bien el sueño con un modelo estadístico de la pantalla, y la app usa una regla mucho más simple. **[Aproximación]**.

**Ejemplo (Figura 5.2).** Tres semanas inventadas: entre semana te duermes hacia las **00:15** y despiertas a las **06:45**; viernes y sábado, hacia las **01:45** y las **10:15**. **MSW** = **03:26**, **MSF** = **06:03**: jet lag social = **2.62 h**. Duermes **6.7 h** entre semana y **8.4 h** el fin de semana. La hora de dormir «varía **±53 min**», pero casi toda esa variación es la diferencia **sistemática** entre semana y fin de semana: la desviación estándar no la distingue de dormirse cada día a una hora distinta.

![Figura 5.2. 21 noches sintéticas dibujadas como en la app, con los puntos medios y el jet lag social. Script: `figuras/fig28_jetlag_social.py`.](figuras/fig28_jetlag_social.png){width=100%}

**Cuándo falla** (H-4-9 y H-4-10, resueltos en parte). Los días libres no incluyen vacaciones, turnos ni el horario de Conocerte: un sábado trabajado se clasifica mal, y las vacaciones pesan mucho más que los festivos. El valor absoluto pierde la dirección. Con menos de 7 noches del reloj se mezclan dos medidas distintas en un mismo promedio; la pantalla lo avisa, pero las sigue mezclando.

## 5.7 Otras variables derivadas

- **Aire libre y kilómetros** (capítulo 4): `aire_libre_min` es el mayor entre los minutos con **1000 lux o más** y los minutos caminando, corriendo o en bici, y **subestima**; `distancia_km` es una **cota inferior** del recorrido (H-3-5). **[Aproximación]**.
- **Fase lunar** (`Luna`): con el **mes sinódico medio** de 29.530588853 días; iluminación = (1 − cos 2π*f*)/2 (`luna_ilum`). **[Validado]** como aproximación astronómica; cuánto se desvía de la fase real no se cuantificó **[sin verificar]**.
- **Enfoque y siestas** (`data/Enfoque.kt`): bloques del cronómetro **sin contar las pausas**; los de **menos de 1 minuto** no se guardan. Son **mediciones** de tu cronómetro, no modelos.
- **Repetición máxima estimada** (1RM, el mayor peso que podrías levantar una vez): peso × (1 + repeticiones/30), la **fórmula de Epley**; su fuente original **[sin verificar]**. Desde la 10c hay **un solo criterio** en toda la app (`Macrofactor.e1rm`): series de 1 a **12** repeticiones, sin calentamientos. *Ejemplo:* con 100 kg × 15, 120 kg × 5 y un calentamiento de 130 kg × 8, da 120 × (1 + 5/30) = **140 kg** (H-4-13, resuelto). Desde la 10f (D.114; H-7-14), `fuerza_ref_kg` es la mayor de esas cifras del día en un solo ejercicio fijo, el press inclinado con mancuernas, reconocido por su nombre en inglés o en español (`Macrofactor.esReferencia`, **[Diseño propio]**); vacía los días sin ese ejercicio. La plantilla de experimento de la creatina la mide en vez del volumen, que cambia según qué entrenaste; si vas subiendo de peso, la cifra sube sola.
- **Grados de escalada** (`Grados`): tablas fijas de equivalencia. **[Aproximación]**: las tablas publicadas no coinciden entre sí.

## 5.8 Estimaciones, no mediciones

1. **Parámetros promedio.** Tu cuerpo puede estar lejos del promedio; los resultados sirven sobre todo para **compararte contigo mismo** con los mismos parámetros.
2. **Sin incertidumbre visible.** Ninguna pantalla ni variable dice cuánto podría equivocarse la cifra.
3. **Parámetros que reescriben el pasado** (H-4-11, resuelto en parte). Al cambiar la vida media de la cafeína o los parámetros de un suplemento, **todas** las variables derivadas pasadas se recalculan, sin guardar qué parámetro se usó antes: dos exportaciones de fechas distintas pueden no ser comparables. Desde la 10e (D.99), el diccionario de cada exportación dice con qué parámetros se calcularon `cafeina_al_dormir_mg`, `creatina_saturacion_pct` y cada `supl_<id>_nivel`.

Una relación con una variable estimada es tan buena como el modelo: `cafeina_al_dormir_mg` es, en la práctica, una **combinación de la cantidad y la hora** de tus cafés. Si se relaciona con dormir peor, dice que «tomar más o más tarde» se asocia con dormir peor; no que tu sangre tuviera exactamente esos miligramos.

## 5.9 En resumen

1. La cafeína y el alcohol se estiman con modelos sencillos y **parámetros promedio bien elegidos** (5 h de vida media y 45 min de absorción; β = 0.15 g/L/h), pero la variación entre personas es enorme: la misma tarde de café deja de 12 a 106 mg al dormir según la vida media.
2. Desde la 10c, el alcohol usa el peso más reciente entre la báscula y MacroFactor y, con los datos, la *r* de Watson; sin pesos usa 70 kg y no muestra su incertidumbre (H-4-4, en parte).
3. Desde la 10e, MacroFactor y la app se juntan toma por toma (H-4-2) y el máximo de alcohol arrastra la noche anterior (H-4-5).
4. El modelo de creatina se basa en Hultman et al. (1996) y, desde la 10e, usa su dosis de 3 g como referencia (H-4-6); la melatonina ya no tiene `nivel` (H-4-7, en parte).
5. El jet lag social está bien definido; usa las noches de los últimos 56 días y los festivos de México, pero no las vacaciones, y la regularidad es una desviación estándar simple.
6. Todas son **estimaciones**: sirven para compararte contigo mismo, y cambiar un parámetro reescribe el pasado (H-4-11).
7. De los 13 hallazgos del módulo 4, **7 están resueltos** (H-4-1 por documentación, H-4-2, H-4-3, H-4-5, H-4-6, H-4-8 y H-4-13) y **6 resueltos en parte** (H-4-4, H-4-7, H-4-9, H-4-10, H-4-11 y H-4-12).


# Capítulo 6. Análisis estadístico

> **Para qué sirve este capítulo.** Explica cómo la app pasa de miles de registros a frases como «cuando duermes más, tu ánimo tiende a subir»: cómo arma la tabla de un renglón por día (el *panel*), cómo compara cada variable con cada otra y qué hace para no dejarse engañar por las tendencias, por los días que se parecen a sus vecinos y por el azar de revisar miles de parejas. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), puesto al día con las entregas 10e (D.88 a D.108; lo que cambió se marca «desde 10e») y 10f (D.109 a D.118; «desde 10f»). Las fórmulas están en el Apéndice B. **Los ejemplos numéricos son inventados.**

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

: Tabla 6.1. Mapa: qué análisis hay y con qué protecciones

Lo importante: **«Las relaciones más marcadas» y, desde la entrega 10c, «Todo lo que se mueve con algo» aplican las tres protecciones a la vez**. Las demás son descriptivas o aplican una o dos; la app lo dice: «Pistas para entenderte. Ninguna prueba causas».

## 6.2 El panel diario: la tabla de la que sale todo

### 6.2.1 Qué es

`Estadistica.panel()` devuelve un mapa `fecha → (variable → valor)`. Cada **fecha** es un día lógico (de 5:00 a 4:59 del día siguiente; capítulo 2), y desde 10f cada momento se asigna a su día y a su hora con la zona que tenía el teléfono entonces (D.109). Cada **valor** es un número o `null` (un **dato faltante**: «no sé» para ese día, que no es lo mismo que 0). Las variables 0/1 se guardan como 0.0 y 1.0. Qué variables entran en los análisis lo decide `Estadistica.variables()`: 193 con nombre fijo en 24 grupos desde el esquema 17 (valencia, sueño, pasos, clima…; 133 existen siempre y 60 **solo aparecen si hay datos**; el esquema 17 sumó tres contextos y tres compañías; en 10e se agregaron `error_prediccion_signo` y `gasto_imprevisto_monto`, D.94, y en 10f `fuerza_ref_kg`, la mejor 1RM estimada del día en press inclinado con mancuernas, D.114) y las **dinámicas** (6.2.3). Las privadas nunca: desde el módulo 10a se excluyen siempre y solo viajan a R. **[Diseño propio]**.

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

## 6.3 La correlación: Pearson y Spearman

La **correlación** responde «¿cuando una sube, la otra tiende a subir (o a bajar)?» con un número de −1 a 1: +1, suben juntas siempre en línea recta; 0, ninguna relación lineal; −1, cuando una sube la otra baja.

**Pearson** (`Estadistica.pearson`, B.1), con n ≥ 3 pares:

$$r = \frac{\sum_i (x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_i (x_i-\bar x)^2\,\sum_i (y_i-\bar y)^2}}\qquad\qquad (6.1)$$

Si una variable no cambia, devuelve `null`. **Spearman** (`Estadistica.spearman`, B.2) es Pearson aplicado a los **rangos** (el orden de los valores); a los empates se les da el promedio de los lugares que ocupan (dos valores en los lugares 3.º y 4.º reciben 3.5). **[Validado]**. La **fuerza** (`Estadistica.fuerza`): |r| < 0.1 «prácticamente nula»; < 0.3 «débil»; < 0.5 «moderada»; de 0.5 en adelante «fuerte», los cortes de Cohen (1988). **[Validado]** como convención; Cohen mismo advertía que dependen del campo.

Pearson mide relaciones **rectas** y se deja arrastrar por un día extremo; Spearman mide relaciones **que solo suben o solo bajan** y es robusto a extremos, pero con muchos empates pierde información. La app usa Pearson en la lista, en «Todo lo que se mueve con algo» y en «Cruzar dos variables» (que muestra también Spearman); sobre los restos sin tendencia es razonable, porque al restar el promedio de los vecinos los valores se vuelven más simétricos. **[Aproximación]**. En R, desde 10e (D.106), `vm_cruce` y `vm_presion` dan lo mismo que «Cruzar dos variables» (Pearson con nEf como principal, la r sin tendencia y Spearman) y `vm_cruce_horas` da los dos coeficientes; hasta 10e daban solo Spearman (H-5-7, resuelto en parte).

## 6.4 Quitar la tendencia

### 6.4.1 El problema

Si dos cosas cambian lentamente con el tiempo, **parecen relacionadas aunque no tengan nada que ver**: son las «correlaciones sin sentido» entre series de tiempo (Yule, 1926) y las «regresiones espurias» (Granger y Newbold, 1974). **[Validado]**. En otoño los días se acortan y, por otras razones, estás bajando de peso: «horas de luz» y «peso» salen correlacionadas solo porque ambas bajan con los meses.

### 6.4.2 Cómo lo hace exactamente

`Estadistica.series()` (privada) toma, para cada día *i* con dato, **todos los días con dato a 10 días de calendario o menos** (de *i* − 10 a *i* + 10, incluido el propio día; `VENTANA = 10`). Si hay al menos **3**, el **resto** del día es su valor menos el promedio de esa ventana (B.3); si no, el resto queda vacío:

$$\text{resto}_i = x_i - \frac{1}{|W_i|}\sum_{j\in W_i} x_j,\qquad W_i=\{j:\ |d_j-d_i|\le 10,\ x_j\ \text{existe}\},\ |W_i|\ge 3\qquad\qquad (6.2)$$

La ventana es de días de calendario: si faltaron días tiene menos valores, pero no se estira. **[Diseño propio]** la elección de ±10 días; la técnica (restar una media móvil centrada) es un filtro pasa-altas estándar. **[Aproximación]**. R hace lo mismo en `vm_relaciones(…, ventana = 10)`.

**Ejemplo.** Valencia de los días 1 a 15 (faltan el 7 y el 13): 5, 4, 6, **3**, 5, 5, —, 4, 6, 5, 4, 5, —, 3, 6. Para el día 4, los días con dato a 10 o menos de distancia son del 1 al 14, menos el 7 y el 13: 12 días. Su promedio es 55/12 = **4.583**; el resto, 3 − 4.583 = **−1.583**. El día 4 fue 1.6 puntos peor que sus semanas vecinas, y eso es lo que se compara, no el 3.

![Figura 6.1. Dos variables sintéticas sin relación día a día que solo comparten un cambio lento: con los valores tal cual, r = −0.36; con los restos de ±10 días, r = +0.01. Script: `figuras/fig12_tendencia_espuria.py` (replica `Estadistica.series`).](figuras/fig12_tendencia_espuria.png){width=100%}

### 6.4.3 Variables «lentas»

`series()` marca una variable como **lenta** si su varianza original es mayor que 0, tiene al menos **14** restos (8 por semana) y var(resto) / var(x) < **0.12**. Con el interruptor encendido las lentas se omiten y la pantalla lo dice. Ejemplos: horas de luz, etapas de vida, brújula política. **[Diseño propio]** (el corte de 0.12 no viene de la literatura).

### 6.4.4 Cuándo falla

- **El ciclo de la semana no se quita (H-5-6).** Una ventana de 21 días contiene exactamente tres semanas: el promedio **borra** el patrón semanal y lo **deja entero** en los restos. Si dos variables cambian ambas con el fin de semana, su correlación sin tendencia puede deberse solo al calendario. **[Aproximación]**. Desde 10e (D.103, D.106; resuelto en parte) la ayuda del interruptor lo dice, y en R `vm_relaciones` y `vm_factores` tienen `sin_semana = TRUE`, que resta a los restos de cada variable el promedio de sus restos en su día de la semana: si tus sábados promedian +0.8 h de sueño sobre sus semanas vecinas, un sábado con +1.0 h queda en +0.2 h. La app no tiene esa opción.
- **Cambios bruscos.** Si te mudas, los restos de los ~10 días cercanos al cambio son raros.
- **Efectos lentos reales.** Si dormir mal durante semanas te deprime poco a poco, quitar la tendencia borra justo ese efecto. Por eso el interruptor existe y la tarjeta avisa cuando la correlación «con tendencia» difiere en 0.2 o más.

## 6.5 Los días no son independientes: el número efectivo de días

### 6.5.1 El problema

Las fórmulas clásicas suponen que cada día es una observación **independiente**. Pero tus días se parecen a sus vecinos (eso es la **autocorrelación**): una semana de exámenes tiene varios días seguidos con poco sueño y mal ánimo, y contarlos como 7 pruebas independientes hace que la relación parezca más segura de lo que es. En la simulación de la Figura 6.2, con dos series sin relación y autocorrelación de 0.8, el intervalo ingenuo «encuentra» una relación en el **35 %** de los casos en vez del 5 %.

### 6.5.2 Cómo lo hace exactamente

1. `Estadistica.autocorr` (B.4): Pearson entre cada día y el siguiente, **solo con pares de días consecutivos** con dato; con **menos de 10 pares** (o sin variación), desde 10e se supone **0.3** (`Estadistica.AR1_SIN_DATO`; antes, 0, que no corregía). Se calcula sobre restos o valores, según el modo, sobre **toda la serie** de la variable, y se recorta a [−0.5, 0.97].
2. **Días efectivos** (nEf, B.5), con *n* los días con ambos datos y *r_a*, *r_b* las autocorrelaciones:

$$n_{Ef} = n\,\frac{1-r_a r_b}{1+r_a r_b},\qquad \text{acotado a } [\min(4,n),\ n]\qquad\qquad (6.3)$$

Si nEf ≤ 3, la pareja no se evalúa.

**Por qué.** Es la aproximación clásica para la correlación entre dos series con autocorrelación de orden 1, AR(1) (el supuesto de que el parecido entre días decae geométricamente) (Bretherton et al., 1999; Pyper y Peterman, 1998). Lo que infla la incertidumbre es que **ambas** series sean persistentes; si solo una lo es, no hay inflación. **[Validado]** como aproximación de primer orden. Las cotas son prudentes: la superior impide que los días efectivos superen a los reales; el tope de 0.97 evita dividir casi entre cero. **[Diseño propio]**.

**Ejemplo.** Sueño y valencia, sin tendencia: n = 120; autocorrelaciones 0.4 y 0.3, producto 0.12. nEf = 120 × 0.88 / 1.12 = **94.3**. Como es menor que 0.8 × 120 = 96, la tarjeta escribe «120 días (≈ 94 independientes)».

![Figura 6.2. Izquierda: días efectivos según la autocorrelación (la misma en ambas variables) para 30, 90 y 365 días. Derecha: 4000 parejas simuladas de series AR(1) independientes de 90 días; con la autocorrelación real de 0.8, el intervalo con n da falsas alarmas el 35 % de las veces y el de la app, el 4.6 %. Script: `figuras/fig13_n_efectivo.py`.](figuras/fig13_n_efectivo.png){width=100%}

### 6.5.3 Cuándo falla (H-5-13)

- **Variables esporádicas (resuelto en 10e, D.95)**: con menos de 10 pares de días seguidos, la autocorrelación se tomaba como 0 y no se corregía nada; ahora se supone 0.3, en la app y en R (`.vm_ar1_sin_dato`), salvo en las diferencias de medias, donde sigue en 0. **[Diseño propio]**: es un valor prudente, no estimado. Por ejemplo, 40 días en común, una variable sin pares seguidos y la valencia con 0.5: antes nEf = 40; ahora 40 × 0.85 / 1.15 = **29.6**. Con r = 0.35, el intervalo pasa de 0.04 a 0.60 a −0.01 a 0.63: ahora incluye el cero.
- **Toda la serie, no los días compartidos**: si una variable tiene dos años y la otra dos meses, la autocorrelación de la primera usa los dos años.
- **Persistencia más larga que un día**: la fórmula de primer orden se queda corta; quitar la tendencia reduce mucho este problema.

Desde la entrega 10c, «Cruzar dos variables» y «Todo lo que se mueve con algo» usan la misma fórmula (`Estadistica.correlacionEf`, con `Estadistica.ar1`, que también exige 10 pares; sin ellos, 0.3 desde 10e), y las diferencias de medias, el factor AR(1) de 6.10.

## 6.6 Intervalo de confianza y valor p: la transformación de Fisher

El **intervalo de confianza de 95 %** es el rango de valores compatibles con tus datos: si incluye el 0, podría no haber relación. El **valor p** es la probabilidad de ver una relación así de fuerte por puro azar si no hubiera ninguna. Para *r* con *n* días (o nEf), la app usa la **transformación z de Fisher** (Fisher, 1915) (B.6–B.8):

$$z=\operatorname{atanh}(r),\qquad EE=\frac{1}{\sqrt{n-3}},\qquad IC_{95}=\big[\tanh(z-1.96\,EE),\ \tanh(z+1.96\,EE)\big]\qquad\qquad (6.4)$$

`Estadistica.ic95(r, n)` exige n ≥ 5 y recorta *r* a ±0.9999. En `relaciones` se usa **nEf** en lugar de *n*, y *p* = 2 · (1 − Φ(|z| / EE)), con Φ (la normal acumulada) calculada por `Analisis.phi` con la aproximación 7.1.26 de Abramowitz y Stegun (1964), de error del orden de 10⁻⁷. **[Validado]**. La distribución de *r* es asimétrica cerca de ±1; atanh la vuelve aproximadamente normal con varianza 1/(n − 3): es el método estándar. **[Validado]**. Usarlo con nEf es la combinación habitual. **[Aproximación]**.

**Ejemplo (continúa el de 6.5.2).** r = 0.30, nEf = 94.29: z = 0.3095; EE = 0.1047; IC = **0.10 a 0.47**; z/EE = 2.957 → **p = 0.0031**. Con *n* = 120 sin corregir habría salido IC 0.13 a 0.45 y p = 0.0008: casi cuatro veces «más significativo» de lo que es.

**La t de Student.** Para las diferencias de medias, el modelo múltiple y el día de la semana, la app usa la **t de Student**, la curva que reemplaza a la normal cuando la variabilidad se estima con pocos datos: tiene colas más gruesas cuanto menos grados de libertad (con 5, el 95 % central llega a ±2.57; con 30, a ±2.04; con muchos, a ±1.96). `Estadistica.pT` y `Estadistica.cuantilT` la calculan de forma exacta (B.14a), comprobada contra las tablas en `CalculosPrueba.cuantilesDeT`. **[Validado]**.

**Cuándo falla.** El intervalo de la correlación usa 1.96 aun con nEf pequeño; con Fisher es lo estándar y el error es pequeño frente al de no corregir por días parecidos. **[Aproximación]**. Para variables 0/1 con pocos «1» la aproximación es menos precisa; la app exige al menos **4 días de cada valor** para evaluarlas.

## 6.7 Muchas comparaciones a la vez: Benjamini-Hochberg

### 6.7.1 El problema

Con 300 variables hay 300 × 299 / 2 = **44 850** parejas. Si ninguna tuviera relación real, con el umbral clásico p < 0.05 saldrían **unas 2 240 «relaciones» por puro azar**.

### 6.7.2 Cómo lo hace exactamente

`Analisis.bh(p)` (B.9) ordena los *m* valores p de menor a mayor y calcula

$$q_{(k)} = \min_{j\ge k}\ \min\!\Big(1,\ p_{(j)}\,\frac{m}{j}\Big)\qquad\qquad (6.5)$$

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

: Tabla 6.2. Ejemplo

Las cinco primeras tienen q < 0.10. Con m = 44 850, la mejor pareja necesita p < 0.10/44 850 = 2.2 × 10⁻⁶ si es la única real; con nEf = 100 eso equivale a |r| ≳ **0.45**. Por eso la app avisa: «Con pocos días es normal que nada se sostenga».

![Figura 6.3. 200 pruebas simuladas (20 relaciones reales con r = 0.35, 180 sin relación, 90 días). Sin corregir: 19 aciertos y 9 falsas alarmas (32 % de lo marcado es falso). Con BH a q < 0.10: 15 aciertos y 1 falsa alarma (6 %). Script: `figuras/fig14_benjamini_hochberg.py` (replica `Analisis.bh`).](figuras/fig14_benjamini_hochberg.png){width=100%}

### 6.7.5 Cuándo falla

- **El número de pruebas depende del modo** (por día o por semana, con o sin tendencia), así que la misma pareja puede tener q distinto en cada modo.
- **Excluir las obvias no es neutral**: baja *m* y hace la corrección más permisiva. Está bien porque no son descubrimientos, pero la lista debe estar completa y ser la misma en app y R (H-5-2; desde 10d lo es, 6.8).

## 6.8 Las relaciones «obvias» y sus familias

**Qué hace.** Marca las parejas cuya correlación es casi segura por construcción, porque salen de la misma fuente o se calculan una de otra: calorías con proteína, pasos con minutos a pie, «minutos en Instagram» con «minutos de pantalla».

**Cómo.** `Estadistica.obvia(a, b)` devuelve verdadero si se cumple **cualquiera** de diez reglas; las principales: ambas están en una misma familia de `Estadistica.FAMILIAS` (59 conjuntos desde 10e, 58 en la v90, como todos los nutrientes fijos, `valencia` con `animo_valencia` o, desde 10f, `fuerza_ref_kg` con las demás del gimnasio); una es dinámica y la otra uno de sus **totales** (`Dinamicas.AGREGADOS`: `app_*` con `pantalla_min`); `per_X` y `pen_X` de la misma persona; dos puntajes del mismo cuestionario (las diez, en la versión completa, §6.9.2). Las obvias se calculan igual pero se esconden («Mostrar también las obvias») y no cuentan en BH por omisión.

**Por qué y cuándo falla.** No hay regla estadística para decidir qué es «obvio»: es una lista curada a mano. **[Diseño propio]**. Olvidar una familia hace que aparezca como «hallazgo» algo trivial; marcar como obvio algo que no lo es esconde una relación interesante. Caso discutible: se declaran obvias **todas** las parejas entre sentimientos, aunque «registros con tristeza» con «registros con alegría» podría ser informativa.

**En R (H-5-2, resuelto en 10d).** La exportación escribe `vm_familias.csv`, generado por `Estadistica.familias()` con las **mismas** diez reglas, y `vm_obvia` lo lee. Cada familia tiene un **lado**: con lado 0 todas sus variables son obvias entre sí; con lados 1 y 2, cada una de un lado lo es con cada una del otro, pero no con las de su lado. Así `per_ana` es obvia con `personas` y con `palabras` sin que `personas` y `palabras` lo sean entre sí. `ExportacionPrueba.familiasIgualQueObvia` comprueba, con 45 variables inventadas, que da lo mismo que `obvia()` en cada par. **[Diseño propio]**.

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

## 6.10 La diferencia de medias: t de Welch y factor AR(1)

### 6.10.1 Qué hace y cómo

`Estadistica.diferenciaCompleta` y `diferenciaEnDias` comparan el promedio de dos grupos de días y dicen qué tan seguro es que la diferencia no sea azar, teniendo en cuenta que con pocos días la incertidumbre es mayor de lo que dice la normal y que los días seguidos se parecen. Con al menos 3 valores por grupo (B.11 y B.11a):

1. Medias y varianzas muestrales (con n − 1) de cada grupo.
2. **Autocorrelación de cada grupo** (`Estadistica.ar1Grupos`): la del grupo si tiene al menos 10 pares de días seguidos; si no, la de los dos grupos juntos, cada uno centrado en su media; si tampoco, 0. Como en series cortas sale **más baja** que la real, se corrige: ρ̂ + (1 + 4ρ̂)/n, con n los días del grupo (o de los dos). Se acota a **[0, 0.9]**: solo corrige hacia arriba.
3. **Error estándar con el factor AR(1)** √((1 + ρ)/(1 − ρ)) (`Estadistica.factorAR1`):

$$EE=\sqrt{\frac{s_a^2}{n_a}\,\frac{1+\rho_a}{1-\rho_a}+\frac{s_b^2}{n_b}\,\frac{1+\rho_b}{1-\rho_b}}\qquad\qquad (6.6)$$

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

## 6.11 Las demás herramientas

**Modelo múltiple** (`Analisis.modelo`, B.16). Pregunta si el sueño se asocia con el ánimo **manteniendo fijos** otros predictores. Usa solo días completos, exige n ≥ k + 10, estandariza cada columna y resuelve mínimos cuadrados sin intercepto; el intervalo es β ± t(0.975; n − k − 1) · EE, y muestra el R² ajustado. Opcionalmente incluye el valor del objetivo el día anterior: es la idea de los modelos autorregresivos con predictores (Hamaker y Wichers, 2017). **[Aproximación]**: no tiene errores autocorrelados ni es multinivel, no quita tendencias y supone residuos independientes (H-5-11).

**Dinámica emocional** (`Analisis.dinamica`). Con la `valencia` del diario y al menos 7 días: media, DE, **MSSD** (promedio de los cuadrados de las diferencias entre días consecutivos, B.12) e **inercia** (Pearson entre cada día y el siguiente, con al menos 10 pares, B.13). Son índices clásicos (von Neumann et al., 1941; Jahng et al., 2008; Kuppens et al., 2010). **[Validado]** como índices, pero en muestras grandes añadieron poca información sobre el bienestar más allá de la media y la DE (Dejonckheere et al., 2019) y aquí se calculan con un valor por día. **[Aproximación]**. Desde 10e (H-5-12, resuelto) la tarjeta llama «muy difíciles» a los días de 1 o 2, distintos de la «racha dura» (3 o menos), y `vm_dinamica` en R calcula cada mes solo con pares del mismo mes, como la app.

**Palabras del diario** (`Analisis.palabras`, B.18): la diferencia de valencia de los días con cada palabra (en al menos 5 días), sin diccionario de emociones (a diferencia de LIWC; Tausczik y Pennebaker, 2010) ni prueba. **[Diseño propio]**. Con 5 días, la media varía ±0.9 puntos solo por azar si tu DE es 1 (H-5-10).

**Días atípicos** (`Analisis.atipicos`, B.14): z robusto con la mediana y la MAD × 1.4826 (una DE que no se deja arrastrar por los extremos) en 15 variables con al menos 20 días; atípico si |z| ≥ 3 (Leys et al., 2013). **[Validado]**. Revisa el último año; «Tu año», desde 10e (D.100; H-14-3), todos los días del año elegido.

El ritmo de sueño está en el capítulo 5; las cifras descriptivas, en la versión completa, §6.20.

## 6.12 Potencia: por qué 14, 30 y 90 días

La **potencia** es la probabilidad de detectar una relación que **sí existe**. La pantalla dice: «Con 90 días o más ya se puede detectar una relación moderada en una pareja concreta; en esta lista, que revisa todas a la vez, solo las fuertes». La correlación más pequeña detectable con potencia de 80 % y α = 0.05 es (B.15)

$$|r_{\min}|=\tanh\!\Big(\frac{1.96+0.84}{\sqrt{n_{Ef}-3}}\Big)\qquad\qquad (6.7)$$

| Días | Días independientes | Autocorrelación 0.5 en ambas (nEf = 0.6 n) | Igual, con α = 0.001 |
|---|---|---|---|
| 14 | 0.69 | 0.84 | 0.94 |
| 30 | 0.49 | 0.62 | 0.79 |
| 90 | **0.29** | 0.37 | 0.52 |
| 365 | 0.15 | 0.19 | 0.27 |

: Tabla 6.3. Potencia: por qué 14, 30 y 90 días

La frase es **correcta para una sola relación con días independientes**: con 90 días se detecta |r| ≈ 0.29. **[Validado]** en ese sentido estricto. En la lista los días no son independientes y se revisan miles de parejas, así que el umbral efectivo se parece más a la última columna: con 90 días solo se sostienen relaciones de 0.5 o más. Los 14 días son un **mínimo para calcular**, no para creer; los 30, una convención razonable para «pista». **[Diseño propio]**. El aviso cuenta días con diario, no días con ambos datos de cada pareja (H-5-16).

## 6.13 App y R

Desde la entrega 10d, `vestigium.R` analiza el mismo panel que la app (`vm_panel_extendido.csv`, con once nombres dobles documentados en `Exportar.EQUIVALENTES`; H-5-1, resuelto), con las mismas variables (`vm_variables.csv`) y las mismas obvias. `vm_relaciones` y `vm_factores` aplican el mismo algoritmo que la app; `ExportacionPrueba.kt` lo fija con datos inventados, y el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de la pantalla (28 oct 2026). Desde 10e, `vm_cruce` también coincide con «Cruzar dos variables» (H-5-7), `vm_experimento` recalcula los experimentos y agrega una prueba de aleatorización (desde 10f, también el criterio sellado), y `vm_relaciones(sin_semana = TRUE)` quita el ritmo de la semana, que la app no puede quitar; `vm_modelo_ar1` solo existe en R (Pinheiro et al., 2026). Tabla función por función en la versión completa, §6.22.

## 6.14 Qué protege y qué no

En «Las relaciones más marcadas» y «Todo lo que se mueve con algo», las protecciones cubren **las tendencias** (±10 días, 6.4), **los días parecidos** (nEf, 6.5, o el factor AR(1), 6.10) y **el azar de las muchas pruebas** (BH a q < 0.10, 6.7, sin las obvias, 6.8).

**No** cubren:

- **confusores**: una tercera cosa que mueve a las dos, como el día de la semana o estar enfermo;
- **la dirección**: ¿el sueño mejora el ánimo o el buen ánimo te deja dormir? El desfase de un día solo ayuda;
- **el ciclo semanal**, que quitar la tendencia deja entero en la app (H-5-6; en R lo quita `sin_semana = TRUE`), ni **los efectos lentos reales**, que borra;
- **los faltantes no aleatorios**: si dejas de escribir los días malos, todo se calcula con los días que quedan.

Además, la garantía formal de BH no está demostrada con pruebas que comparten variables. Una relación firme en la lista es una **pista seria, no una prueba de causa**.

## 6.15 En resumen

1. Todo sale de un **panel** de un renglón por día, con una regla general para distinguir «0» de «no sé»: 0 desde el primer registro y donde se pudo responder (`ceroDesde`).
2. «Las relaciones más marcadas» **quita tendencias** (±10 días), **descuenta los días parecidos** (nEf) y **corrige por miles de comparaciones** (BH, q < 0.10), sin contar las obvias.
3. «Todo lo que se mueve con algo» usa la misma maquinaria, y las diferencias de medias usan **t de Welch** con el factor AR(1).
4. **El ciclo semanal no se quita** en la app (H-5-6; en R, `sin_semana`), y las etapas de vida nunca aparecen como relación sin tendencia: desde 10e la lista remite a «Antes y después» (H-5-14).
5. Desde 10d, **R analiza el mismo panel**, con las mismas variables y obvias: sirve para verificar la app.
6. Con 90 días independientes se ve una relación de 0.3; con miles de parejas y días parecidos, hace falta más de 0.5.
7. Ninguna protección cubre confusores ni dirección: lo firme es una pista, no una causa.


# Capítulo 7. Personas y red social

> **Para qué sirve este capítulo.** Explica **cómo la app registra a la gente de tu vida**: cómo reconoce a una persona cuando la nombras con @, cómo guarda qué es para ti y cómo eso cambia con los años, cómo mide qué tan cerca la sientes, cómo dibuja la **Constelación** y la **Red**, qué calcula R con `vm_red()` y cómo entran las personas en las correlaciones. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); la entrega 10f no cambió nada de lo que trata este capítulo. Los hallazgos se numeran H-6-n y están en `hallazgos.md`. **Las personas, fechas y números de los ejemplos son inventados.**

## 7.1 Menciones con @ y equivalencias

La app no intenta adivinar nombres en el texto libre: **solo cuenta a una persona si la escribes con @** (una **mención**). Así evita confundir «Rosa» (persona) con «rosa» (color) y no necesita reconocer nombres propios.

- **Reconocer la mención** (`Texto.personas`): busca la expresión regular `@([\p{L}\p{N}_]+)`, es decir, una arroba seguida de letras (de cualquier alfabeto, con acentos), números o guion bajo. El nombre se pasa a **minúsculas**: `@Ana`, `@ana` y `@ANA` son lo mismo; `@ana_m` es distinta de `@ana`.
- **Nombre canónico** (`Texto.canon`): el nombre con el que la app cuenta a una persona después de aplicar las **equivalencias** (si dijiste que `@anita` es `@ana`, el canónico de `anita` es `ana`). `canon` sigue las uniones hasta **8 pasos** (`anita → ana → ana_g` da `ana_g`); en un ciclo, todos cuentan como **el primero en orden alfabético**. **[Diseño propio]**.
- **Posibles duplicados** (`Personas.duplicados`): la app pregunta «¿son la misma persona?» si dos nombres (1) son iguales al quitar los guiones bajos; (2) tienen ambos 4 letras o más y su **distancia de Levenshtein** es ≤ 2 (cuántas letras hay que cambiar, agregar o quitar para pasar de uno a otro: `daniel` → `danie` es 1); o (3) tienen ambos 3 letras o más, uno empieza con el otro y la diferencia de largo es ≤ 3 (`ana` y `anita`).

Marcar a mano es más fiable que reconocer nombres automáticamente, y las equivalencias resuelven apodos sin reescribir el diario. **[Diseño propio]**, razonable. *Ejemplo:* escribes `@Dani` 6 días y `@daniel` 3 días; cumplen las reglas 2 y 3, la app pregunta y, si los unes, todas las funciones cuentan **9 días** (o menos si algún día aparecen ambas: un día cuenta una vez).

**Cada función lee textos distintos**, así que «aparecer» no significa lo mismo en todas partes (H-6-9): la variable `personas` del panel lee el diario y, desde 10e, los momentos no privados (D.93); la Constelación y la Red, el diario y los momentos; la familia `per_`, además, el sueño, el mejor y el peor momento y las cartas (tabla en la versión completa, §7.2).

**Cuándo falla.** Si escribes «comí con mi mamá» sin `@`, la app no la ve: todas las variables sociales cuentan **menciones marcadas**, no contactos reales. Dos personas distintas con el mismo `@nombre` se cuentan como una. Y la misma tabla `Config.equivalencias` sirve para `@personas` y `#temas`: una equivalencia creada para una persona también cambiaría un tema con ese nombre (H-6-11, parte vigente).

## 7.2 Quién es cada persona: relaciones con historial

En «Tu gente» dices qué es cada @persona para ti, entre 12 opciones (`Personas.RELACIONES`). Para los análisis se **agrupan** (`Personas.grupo`):

| Relación guardada | Grupo |
|---|---|
| familia, casa | `familia` |
| pareja | `pareja` |
| amistad, amistad cercana | `amistad` |
| colega, compañero de escuela, mentor, trabajo | `trabajo` |
| expareja, conocido, lejana, mascota, otra | se quedan como están |

: Tabla 7.1. Quién es cada persona: relaciones con historial

`Perfiles.relacion(persona, perfil, fecha)` usa, en este orden: el grupo de la relación que tenía en «Tu gente» en esa fecha; si no está, la relación de «a quién ves más» en Conocerte (capítulo 3); si tampoco, `casa` si está en «quiénes viven contigo»; si no, ninguna (cuenta en los totales, pero no en `vi_familia`, `vi_pareja`, etc.).

**Cómo se guarda el historial.** Cada `Persona` (`data/Modelo.kt`) tiene `relacion`, `cercania` y un `historial` de `CambioRelacion(hasta, relacion, cercania)`. Al guardar (`Personas.guardar`), si la relación o la cercanía cambió (incluido ponerla o quitarla), se agrega `CambioRelacion(hasta = hoy, relación anterior, cercanía anterior)`, donde «hoy» es, por omisión, el **día lógico** en que guardas (el día de la app, que empieza a las 05:00; capítulo 2). Desde 10e (D.102), al cambiar la relación o la cercanía puedes decir que cambió antes («cambió antes de hoy…»): no después de hoy ni antes del cambio anterior. **[Diseño propio]**. Al consultar (`Personas.relacionEn(nombre, fecha)`), se ordena el historial por `hasta` y se toma la **primera** entrada con `fecha < hasta`; si ninguna cumple, la relación actual (`cercaniaEn` hace lo mismo). Es decir, **cada entrada vale para los días anteriores a su `hasta`**; el propio día del cambio ya tiene la relación nueva.

*Ejemplo.* Una persona es amistad desde el 1 feb 2025; el 15 sep 2025 la cambias a pareja y el 20 abr 2026 a expareja. El historial guarda `(hasta 2025-09-15, amistad)` y `(hasta 2026-04-20, pareja)`. Para el 14 sep 2025 da amistad; del 15 sep 2025 al 19 abr 2026, pareja; desde el 20 abr 2026, expareja. Si estuviste con ella el 2 abr 2026, se enciende `vi_pareja`, no `vi_expareja`.

**Razonamiento.** Si los análisis usaran la relación de hoy, todos los días con quien hoy es expareja contarían como «estuve con una expareja», incluidos los días en que era tu pareja. Guardar la relación por fecha es la forma correcta de tratar una **covariable que cambia con el tiempo** (un dato de contexto que tiene un valor por periodo). **[Diseño propio]**, bien planteado.

**Cuándo falla.** Si no dices cuándo cambió, la fecha del cambio es la fecha en que lo anotas: si la ruptura fue el 28 mar y la anotas el 20 abr, esos 23 días cuentan como pareja (H-6-3; hasta 10e no había otra opción). Antes de la primera clasificación se proyecta la relación más antigua conocida (como el perfil proyectado hacia atrás, H-2-5). Y Conocerte no tiene historial por persona: «a quién ves más» y «quiénes viven contigo» se leen con su valor **actual** para todas las fechas (H-6-4, parte vigente). Otros casos límite: versión completa, §7.4.5.

## 7.3 Cercanía: los círculos de Aron (escala IOS)

La **escala de Inclusión del Otro en el Yo** (IOS) de Aron, Aron y Smollan (1992) es una pregunta de **un solo reactivo** con dibujos: siete pares de círculos, «yo» y «otro», que van de separados a casi completamente encimados; eliges el par que mejor describe tu relación. Los autores la validaron con buena confiabilidad, relación con otras medidas de cercanía y capacidad de predecir si una relación de pareja seguía meses después. Gächter, Starmer y Tufano (2015) la compararon con seis medidas en 772 adultos y concluyeron que es una medida fiable de la cercanía **subjetiva**; un índice combinado de las otras escalas correlacionó ρ = .85 con la IOS. **[Validado]** como instrumento.

`CirculosCercania` (`ui/pantallas/Gente.kt`) dibuja siete pares de círculos de radio *r*, con distancia entre centros

$$d_i = r\,(2.1 - 0.31\,(i-1)), \quad i = 1,\dots,7\qquad\qquad (7.1)$$

de 2.1 *r* (no se tocan) a 0.24 *r* (casi encimados). Al elegir uno aparece su palabra (`CERCANIA`), de 1 «Casi nada en común» a 7 «Casi parte de mí». Es opcional y se puede desmarcar. No pude comprobar cómo están espaciados los círculos en la publicación original **[sin verificar]**.

**Diferencias con el original** **[Aproximación]** (H-6-15): las palabras ayudan a que un «5» signifique lo mismo con el tiempo, pero cambian el instrumento; y se responde **una vez por persona**, no por día, sin recordatorio para revisarla. Por eso `cercania_media` (sección 7.4) mide **a quién nombras**, no **cómo te sentiste con ellas ese día**.

## 7.4 Presencias y variables del panel

**Presencias** (`data/Presencias.kt`, capítulo 3). Cada persona de un día tiene un estado: `estuve`, `pense` o `mensajes`. En el diario, el estado por omisión es **«estuve»** si no tocas el nombre (H-1-6). Quien aparece **solo en momentos** cuenta como «pensé» si el momento era «pensando en…» o «recuerdo» sin compañía marcada, y como «estuve» en otro caso. El diario manda sobre los momentos, y `Presencias.delDia` **excluye los momentos privados** (sección 7.10).

### 7.4.1 Variables fijas (`Estadistica.panel`)

Se calculan solo los días en que **se pudo nombrar a alguien** (`Dinamicas.diasSociales`): con texto en el diario o con un momento no privado que nombra a alguien con @. Los demás quedan **vacíos**, no en 0. El grupo de relación es el **de ese día**.

| Variable | Qué cuenta |
|---|---|
| `personas` | @personas distintas (canónicas) en el **texto del diario** y, desde 10e, en los **momentos no privados**, sin importar su estado |
| `cercanos` | personas «estuve» de grupo `familia`, `pareja`, `amistad` o `casa` |
| `vi_familia`, `vi_pareja`, `vi_amistades`, `vi_expareja`, `vi_trabajo` | 1 si estuviste con alguien que ese día era de ese grupo |
| `vi_convivientes` | 1 si estuviste con alguien de la lista **actual** de convivientes que tiene algún grupo |
| `personas_vistas`, `personas_pensadas`, `personas_mensajes` | cuántas personas tuvieron cada estado |
| `penso_en_pareja`, `penso_en_expareja`, `penso_en_familia`, `penso_en_amistades` | 1 si pensaste sin ver a alguien de ese grupo |
| `cercania_media` | promedio de `cercaniaEn` de **todas** las personas del día que tengan cercanía |

: Tabla 7.2. Variables fijas (Estadistica.panel)

Condiciones exactas y las variables `cartas_otros` y `presencias_revisado`: versión completa, §7.7.1. La compañía de los avisos (`acompanado_pct`…) es otra fuente, independiente (capítulo 3).

### 7.4.2 Una variable por persona (`Dinamicas.construir`)

- **`per_<nombre>`** vale 1 si la persona se menciona ese día en el diario, el sueño (texto o temas con `@`), el mejor o peor momento, un momento **no privado** o una carta; 0 en los demás días en que se pudo nombrar a alguien; vacío en el resto. En los análisis entran las **15** personas con más días, con **al menos 5 días**; en la exportación extendida, todas las de al menos 2 días (hasta 400).
- **`pen_<nombre>`** vale 1 si `Presencias.delDia` le da «pensé». Entran las **10** con más días, con **al menos 3**.

**No existe una variable por persona de «estuve con @x»**: `per_` mezcla estar con ella, pensarla, soñarla o escribirle, y su etiqueta lo dice (H-6-2).

*Ejemplo.* Diario: «Desayuné con @ana y @beto. Me acordé de @caro.», y marcas a `@caro` como «pensé». Un momento «pensando en…» sin compañía nombra a `@dani`. Sueñas con `@eli`. Relaciones: ana familia (cercanía 6), beto pareja (7), caro amistad cercana (5), dani amistad (4), eli colega (sin cercanía). Resulta: `personas` = 4 (ana, beto, caro y dani; el sueño no cuenta); `personas_vistas` = 2 y `personas_pensadas` = 2 (eli no cuenta: los sueños no entran en presencias); `cercanos` = 2; `vi_familia` = `vi_pareja` = 1, `vi_amistades` = 0, `penso_en_amistades` = 1; `cercania_media` = (6 + 7 + 5 + 4)/4 = **5.5**, que incluye a las pensadas; `per_` = 1 para las cinco y `pen_caro` = `pen_dani` = 1.

**Cuándo falla.** Un 0 quiere decir «no la nombré», no «no la vi»: un diario escrito sin @ cuenta como «no vi a nadie» (H-6-13, resuelto en parte). «Estuve» por omisión infla las presencias (H-1-6).

## 7.5 Co-mención como aproximación de co-presencia

La Constelación y la Red unen a dos personas cuando **aparecen el mismo día lógico**, aunque sea en textos distintos (una **co-mención**). **[Aproximación]** débil de que se relacionen: el mismo día no es el mismo momento (si ves a tu hermana en la mañana y a un compañero en la noche, aparecen juntos aunque nunca se hayan visto); no distingue «estuve» de «pensé»; depende de cuánto escribes; y lo que la gente reporta de sus interacciones coincide solo en parte con lo observado (Bernard et al., 1984) y se desvía según qué tan reciente y llamativa fue la interacción (Eagle et al., 2009). Por eso la app exige **al menos 2 días juntas** para una línea derivada (`minJuntos = 2`), y la Red permite **declarar** quién conoce a quién, que es más fiable.

## 7.6 La Constelación

Tu gente como un cielo (`ui/pantallas/Constelacion.kt`): tú en el centro, cada persona una estrella, **más grande** si la mencionaste más días y **más cerca del centro** si la mencionaste hace poco. `Constelacion.calcular` toma las menciones del diario y de los momentos no privados del periodo (90 días, 365 por omisión, o «siempre»), se queda con las **80** personas con más días y une los pares que aparecen juntos **2 días o más**. La distancia al centro, como fracción del radio, es

$$d = \left(0.30 + 0.64\cdot \min\!\left(1, \frac{\text{días desde la última mención}}{P}\right)^{0.6}\right)\cdot 0.94\qquad\qquad (7.2)$$

con *P* el periodo en días: alguien mencionado hoy queda a 0.28 del radio; alguien no mencionado en todo el periodo, a 0.88. El tamaño es 2.5 + 7·√(días / días de la más mencionada) dp (unidades de pantalla). El ángulo solo evita que se encimen: **no significa nada**. El color es el grupo de relación **actual**. *Ejemplo:* en un año, `@fer` tiene 44 días y la última hace 20; la más mencionada, 210. Distancia: (20/365)^0.6 = 0.175; (0.30 + 0.64·0.175)·0.94 = **0.387**. Tamaño: 2.5 + 7·√(44/210) = **5.7 dp**.

Es una **visualización**, no un análisis: no produce variables. **[Diseño propio]**. La distancia mide **recencia de la mención**, no cercanía emocional. Límites: una expareja sale con su color de hoy aunque en el periodo haya sido tu pareja casi todo el tiempo (H-6-14); quien viste a diario 11 meses y no mencionas desde hace 30 días queda más lejos que alguien nombrado una vez ayer. Parámetros del dibujo: versión completa, §7.9.2.

## 7.7 La pantalla Red

Quién conoce a quién, como un **grafo**: **nodos** (personas) unidos por **aristas** (líneas). Está en `PantallaRed` (`ui/pantallas/Lote3.kt`) y `RedPersonas` (`data/Lote3.kt`).

**Vínculos declarados.** En la ficha de una persona, «¿A quién conoce?» permite marcar, con toques sucesivos, «se conocen», «familia entre sí», «pareja», «colegas» o «no se conocen» (`RedPersonas.siguiente`). Se guarda un `Vinculo(a, b, tipo)` **simétrico** y con nombres canónicos. «No se conocen» es información explícita: ese par **no** cuenta como unido, aunque aparezcan juntas.

**Cálculo** (`RedPersonas.calcular(minJuntos = 2, max = 45)`), con **todo el historial**: los nodos son las **45** personas con más días **más** las que tengan algún vínculo declarado; las aristas, los pares con `juntos ≥ 2` **o** declarados, y cuentan todas menos las «no se conocen». El **grado** de un nodo es con cuántas personas está unido.

**Las tres cifras.** **Personas** (*n*). **Densidad**: qué fracción de las líneas posibles existe, líneas que cuentan / (*n*(*n*−1)/2), incluyendo a las personas sin líneas. **Grupos** (`componentes`): cuántas **componentes** (grupos conectados entre sí por algún camino) hay, **sin contar a quienes no tienen ninguna línea** visible. *Ejemplo:* 10 personas, un triángulo A-B-C, D-E y E-F, G-H, e I y J sueltas: densidad = 6/45 = **13 %**; grupos = **3**.

**El dibujo.** `RedPersonas.acomodar` usa un **modelo de fuerzas** del tipo de Fruchterman y Reingold (1991): todos los nodos se repelen como imanes del mismo polo y cada línea es un resorte que acerca a los unidos; tras **300** vueltas, los que se conocen quedan juntos. **[Validado]** como técnica de dibujo; no es una medida (parámetros: versión completa, §7.10.5).

**Cuándo falla.** La densidad depende de cuántas personas sueltas haya: con 45 nodos hay 990 pares posibles, y las personas poco mencionadas y sin líneas la bajan sin que tu red sea «menos unida».

## 7.8 Métricas en R: `vm_red()`

La exportación usa `RedPersonas.calcular(minJuntos = 1, max = 400)` y escribe `vm_red_nodos.csv` (persona, relación, grupo, cercanía, `dias_mencion`, `grado`) y `vm_red_vinculos.csv` (`a`, `b`, `tipo_declarado`, `dias_juntos`). `vm_red(declaradas = FALSE, min_juntos = 2, min_dias = 3)` quita siempre los pares «no se conocen», se queda con los declarados **o** (si `declaradas = FALSE`) con los que tienen `dias_juntos ≥ min_juntos`, deja fuera a quien se mencionó menos de `min_dias` días salvo que tenga una línea dicha (desde 10e, D.106; con `min_dias = 0`, todos los nodos del archivo, como antes; el 3 es **[Diseño propio]**), arma el grafo y calcula con `igraph`:

- **`densidad`** = `igraph::edge_density(g)`, sin el ego.
- **`componentes`** = `sum(igraph::components(g)$csize > 1)`, como la app, y aparte **`aisladas`** = `sum(igraph::components(g)$csize == 1)`.
- **`restriccion_burt`**: agrega un nodo «yo» unido a todas las personas y calcula `igraph::constraint` para «yo».
- **`grado`** y **`composicion`** (`table(relacion)`): cuántas personas hay de cada relación, una de las descripciones más usadas de una **red egocéntrica**, la vista desde una persona central (el ego, tú) con sus contactos y las relaciones entre ellos (Perry et al., 2018).

**Restricción de Burt.** Burt llamó **huecos estructurales** a la ausencia de conexión entre dos de tus contactos: si conoces a dos personas que no se conocen entre sí, tú eres el puente, y eso da acceso a ideas y opciones distintas (Burt, 2004). La **restricción** (*constraint*) mide lo contrario: qué tanto tu atención está «encerrada» en contactos redundantes, que se conocen entre sí. La fórmula (la que documenta `igraph::constraint`) es

$$C_i = \sum_{j \ne i} \Big(p_{ij} + \sum_{q \ne i,j} p_{iq}\,p_{qj}\Big)^2, \qquad p_{ij} = \frac{a_{ij}+a_{ji}}{\sum_{k \ne i}(a_{ik}+a_{ki})}\qquad\qquad (7.3)$$

donde *a*<sub>ij</sub> = 1 si *i* y *j* están unidos; sin pesos, *p*<sub>ij</sub> es 1/(grado de *i*). El término de adentro suma la inversión **indirecta**: si tus contactos se conocen, cada uno te «llega» también por los demás y la restricción sube.

*Ejemplo (Figura 7.1).* Tú y 6 personas. Si **nadie se conoce**, cada *p* = 1/6 y *C* = 6 × (1/6)² = **0.167** (en general, 1/*n*). Con **dos grupos de 3**, para cada contacto: directo 1/6, indirecto 2 × (1/6)(1/3) = 1/9; (5/18)² × 6 = **0.463**. Si **todos se conocen**, (11/36)² × 6 = **0.560**. Con **dos grupos y un puente** entre ellos, **0.483**: para ti, el puente sube la restricción, porque tus dos mundos ya no dependen solo de ti. Coinciden con `networkx.constraint`; no pude ejecutar R para confirmarlos con `igraph` **[sin verificar]**.

![Figura 7.1. Restricción de Burt del ego y densidad en cuatro redes de 6 personas (datos sintéticos). Script: `figuras/fig18_burt_puente.py`.](figuras/fig18_burt_puente.png){width=100%}

**Validez.** Las métricas son estándar **[Validado]** como definiciones (Burt, 2004; Perry et al., 2018). Lo que no está validado es la red sobre la que se calculan **[Aproximación]**: las líneas derivadas son co-menciones y los nodos, personas **mencionadas**. Además, la restricción y la densidad dependen mucho del número de personas: con el ego unido a todas, la restricción mínima es 1/*n*, y con 400 personas puede bajar hasta 0.0025 solo por haber mencionado a mucha gente una vez (H-6-8). Por eso, desde 10e, `vm_red()` pide por omisión 3 días de mención; para comparar en el tiempo, conviene usar el mismo `min_dias` en todos los periodos. Entre la app y R quedan dos diferencias, documentadas en el diccionario: personas (45 frente a hasta 400) y grado del CSV (1 día juntas, no 2) (H-6-7).

## 7.9 Cómo entran las personas en las correlaciones

Las variables de la sección 7.4 entran en Explorar con el mismo método que las demás (capítulo 6). Lo específico: solo 15 `per_` y 10 `pen_`, así que las personas poco frecuentes **no se buscan** en la app (en R sí, con `vm_panel_extendido.csv` y `variables = c(...)`). Se excluyen los pares **obvios**, que se mueven juntos por construcción (`Estadistica.obvia`): cada `per_` con las variables agregadas de personas (`Dinamicas.AGREGADOS["per_"]`), cada `pen_` con `personas`, `personas_pensadas` y los `penso_en_*`, `per_x` con `pen_x`, y las familias de `Estadistica.FAMILIAS` (lista: versión completa, §7.12); R lee la misma lista en `vm_familias.csv`. Dos personas entre sí (`per_ana` con `per_beto`) no se excluyen: su relación es su co-mención, la misma información de la Red.

## 7.10 Privacidad: dónde entran los momentos privados

Un momento es privado si su tipo es `impulso`, `recaida` o `libido` (`Momento.privado`, regla única en `data/Privado.kt`). Sus @menciones y #temas **no entran en nada de este capítulo** (presencias, `vi_*`, `per_`, `pen_`, Constelación, Red, `vm_red_*`, «Tu gente» ni las sugerencias al escribir), con o sin el seguimiento privado activado. Solo se ven en la pantalla Privado y van completos a `vm_momentos.csv`. Hasta v84, recaídas y libido entraban en `per_` y en «Tu gente», y los impulsos en todo (H-6-1).

## 7.11 En resumen

1. La app reconoce a las personas **solo por `@nombre`**, con equivalencias en cadena; cada función lee textos distintos, así que «aparecer» no significa lo mismo en todas partes (H-6-9).
2. La **relación con historial** es una buena decisión: los análisis usan la relación de cada día. Límites: si no dices cuándo cambió, vale la fecha de la anotación (desde 10e se puede elegir; H-6-3, en parte), lo anterior a la primera clasificación se proyecta y Conocerte no tiene historial (H-6-4).
3. La **cercanía** usa la escala IOS, validada, con etiquetas y respondida una vez por persona: `cercania_media` describe a quién nombras, no cómo te fue ese día.
4. Un 0 en las variables sociales es «no la nombré». **No hay variable por persona de «estuve con @x»** (H-6-2).
5. La **Constelación** es una visualización de frecuencia y recencia; la **Red** combina co-menciones (≥ 2 días), una aproximación débil, con vínculos declarados y un dibujo de fuerzas estándar.
6. `vm_red()` calcula densidad, componentes, **restricción de Burt** y composición; las definiciones son estándar, pero los resultados dependen mucho del número de personas incluidas, y desde 10e deja fuera por omisión a quien se mencionó menos de 3 días (H-6-8).
7. Los momentos privados no entran en nada de este capítulo.
8. Ninguno de los 15 hallazgos del módulo 6 queda pendiente: 10 se resolvieron (H-6-8 en 10e, y H-6-15, las diferencias con la IOS original, con su descripción) y 5 en parte (H-6-3, H-6-4, H-6-9, H-6-11 y H-6-13).


# Capítulo 8. Experimentos personales (N-de-1)

> **Para qué sirve este capítulo.** Explica cómo la app te ayuda a **probar algo en ti mismo** (dormirte antes, tomar creatina) y a decidir si funcionó: la lógica de estos diseños, qué hace exactamente el código (sorteo de semanas, días de lavado, diferencia de medias, tamaño del efecto, prueba de tendencia) y qué tan confiable es el resultado. También trata «Antes y después» de Explorar, que **parece** un experimento pero no lo es. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo que cambió en 10f se marca «desde 10f». Los hallazgos se numeran H-7-n y están en `hallazgos.md`. Los ejemplos y las **simulaciones** (miles de experimentos inventados en los que *sabemos* la verdad) usan datos inventados.

## 8.1 Por qué experimentar en uno mismo

El resto de la app encuentra **relaciones**: «los días que duermes más, estás de mejor ánimo». Pero una relación no dice qué causa qué: quizá dormir más mejora el ánimo, quizá el buen ánimo te deja dormir, quizá los fines de semana causan ambas cosas.

Un **experimento** rompe ese nudo: **tú decides** cuándo hacer la acción, y si además lo decide **el azar** (si se **aleatoriza** o sortea), lo demás (el día de la semana, tu humor previo, el clima) queda repartido entre las dos **condiciones**: con la acción («SÍ») y sin ella («no»). Si después hay diferencia, lo más probable es que la cause la acción.

Los **ensayos N-de-1** son ensayos aleatorizados en **una sola persona** («N» es el número de participantes; aquí, 1), que se compara consigo misma en periodos con y sin la acción. Se propusieron en medicina para elegir el tratamiento de un paciente concreto (Guyatt et al., 1986); la guía de la AHRQ explica cómo diseñarlos (Kravitz et al., 2014) y la extensión CONSORT para N-de-1 (CENT 2015) dice qué hay que reportar (Vohra et al., 2015). En psicología existen como **diseños de caso único**, a los que se ha insistido en añadir aleatorización (Kratochwill y Levin, 2010). **[Validado]**: la lógica de alternar condiciones al azar en una misma persona está bien establecida.

**Cuándo sirven**: la acción tiene un efecto **rápido** que también **se va rápido**; la medida se toma **muchas veces** (diaria, idealmente); el problema es **estable** durante el experimento; y la alternancia se puede **repetir**. La creatina no las cumple (sección 8.8).

## 8.2 Cómo se crea y se sella un experimento

**Lo que escribes** (`PantallaExperimentos`, modelo `Experimento`). Nombre, pregunta (opcional), **acción** e **hipótesis**; **una sola medida** del panel, de una lista corta (valencia, sueño, pantalla, tiempo de reacción, volumen en el gimnasio, desde 10f la fuerza de referencia `fuerza_ref_kg`, enfoque…); desde 10f, opcional, **qué contaría como efecto**: «debería subir» o «debería bajar» (`sentido`) y, si quieres, la diferencia mínima que te importaría (`umbral`, en las unidades de la medida); el **diseño**, «Semanas alternas sorteadas» (`alterno`) o «Antes y después» (`antes_despues`: un periodo sin la acción y luego uno con ella); la **duración**, 4, 6, 8 o 12 semanas; y los **días de lavado** tras cada cambio: ninguno, 1, 2, 7 o 28 (sección 8.4).

Si con esa combinación **podría** quedar alguna condición con menos de **7 días que cuentan** (`Experimentos.MIN_DIAS`), la pantalla lo avisa y **no deja sellar**. `Experimentos.minimoPosible` prueba **todos** los planes que el sorteo podría dar y toma el peor, así que no se puede «sortear otra vez hasta que salga bien». El mínimo de 7 es **[Diseño propio]**: una semana completa, para que cada condición tenga al menos un día de cada día de la semana.

**El sellado.** Al tocar «Sellar y empezar el lunes» se guarda la hora exacta (`sellada = Fechas.ahoraIso()`), se **sortea el plan** (sección 8.3) y el experimento empieza **el lunes siguiente**: `inicio = lunesDe(hoy) + 7 días`. Después no se pueden editar la pregunta, la hipótesis, la variable ni el plan, ni borrar el experimento. Es un **prerregistro** casero: escribir la pregunta, la hipótesis y qué vas a medir **antes** de ver los datos, para no acomodar la conclusión a lo que salga (Simmons et al., 2011; Nosek et al., 2018). **[Diseño propio]** inspirado en una práctica **[Validado]**. Hasta 10f no había huella del texto sellado y se fijaba la variable, pero **no el análisis**: no qué ibas a considerar «funcionó» (H-7-10).

**Desde 10f (D.113): el criterio y la huella.** Al sellar, `Experimentos.sellar` guarda el SHA-256 (`Experimentos.huella`) de lo sellado en un orden fijo (`Experimentos.textoSellado`: pregunta, acción, medida, hipótesis, umbral, sentido, diseño, inicio, semanas, plan, lavado y hora del sello). Una **huella** es un código que sale completamente distinto si cambia una sola letra; SHA-256 es una función hash criptográfica estándar, así que detecta cualquier cambio **[Validado]** (como propiedad de SHA-256). La tarjeta la recalcula (`Experimentos.intacto`) y dice si coincide o avisa que «lo sellado cambió después del sello»; los experimentos de antes del esquema 15 no tienen huella. Al terminar el plan (no a medio camino), `Experimentos.criterio` dice si se cumplió: la diferencia, contada en el sentido sellado, mide al menos el umbral (0 si no hay) **y** su intervalo de 95 % no toca el cero de ese lado. **[Diseño propio]**. *Ejemplo (inventado), con el experimento de la sección 8.5.2:* sellaste «debería subir» y 0.3; diferencia +0.58 con intervalo de −0.46 a +1.62: **no se cumple** (toca el cero); +0.20 con intervalo de +0.02 a +0.38: tampoco (menor que 0.3). Límites: el criterio es opcional; el umbral se compara con la diferencia estimada, no con el borde del intervalo; y la huella **avisa, no impide**: no lleva llave secreta, así que quien edite un respaldo a propósito puede recalcularla. Sigue siendo un sello de **honor**, ahora con una alarma.

**El aviso diario** (`AvisoExperimento`). En los días que cubre el experimento (`Experimentos.cubre`), nunca en días futuros, el día dice qué toca esa semana, si es día de lavado, y pregunta **«¿Lo hiciste hoy?»** («sí» / «no») **en las dos condiciones**. La respuesta se guarda en `Experimento.hecho`. Preguntar cada día puede cambiar la conducta (reactividad); no se pudo estimar cuánto.

## 8.3 El sorteo del plan (`Experimentos.plan`)

- **Alterno**: se sortea **por pares de semanas**. Las semanas se agrupan de dos en dos (1–2, 3–4…) y en cada par se sortea, con la misma probabilidad, si va «SÍ, no» o «no, SÍ»; con un número impar de semanas, la última se sortea sola. Hay $2^{N/2}$ planes posibles: 4 con 4 semanas, 8 con 6, 16 con 8 y 64 con 12.
- **Antes y después**: primero ⌊*N*/2⌋ semanas «no» y luego el resto «SÍ». **No se sortea nada.**

**[Validado]** como aleatorización. **Un acierto del diseño**: como todo empieza en lunes y cada condición dura semanas completas, cada condición tiene la misma cantidad de lunes, martes…, y el efecto del día de la semana no se cuela en la comparación.

**Por qué por pares** (H-7-5, resuelto). Hasta la entrega 10c del módulo 10, las semanas se barajaban libremente, y podía salir «SÍ SÍ SÍ no no no», que en la práctica es un antes y después: con 4 semanas, el **33 %** de las veces. Por pares, nunca hay más de dos semanas seguidas iguales, y una tendencia lenta afecta casi por igual a ambas condiciones. Con una tendencia de 0.02 desviaciones por día y sin efecto real, el sorteo libre de 8 semanas daba una falsa señal el **14 %** de las veces y el sorteo por pares, alrededor del **5 %** (con el método de cálculo anterior a la 10c; versión completa, §8.4.2). Si un plan sellado antes con sorteo libre quedó en bloque (`Experimentos.enBloque`), la tarjeta avisa que se lee como un antes y después (H-9-30).

## 8.4 Qué días cuentan: el lavado

El **lavado** (*washout*) son días que **no cuentan** después de cada cambio de condición, para evitar el **arrastre** (*carryover*): que el efecto de una condición siga actuando cuando ya empezó la otra.

**Las reglas** (`Experimentos.toca` y `Experimentos.enLavado`, `data/Lote3.kt`). Con *d* = días desde `inicio`, la semana es ⌊*d*/7⌋ y la condición es `plan[semana]`. Los **días de cambio** *c* son el día 0 **si** el plan empieza con «SÍ», y el primer día de cada semana cuyo valor difiere del de la anterior. Un día es de lavado si $0 \le d - c < L$ para algún cambio, con *L* = `lavado_dias`. El lavado va **en los dos sentidos**: de «SÍ» a «no» (para que se vaya el efecto) y de «no» a «SÍ» (para que tenga tiempo de aparecer).

*Ejemplo.* Plan SÍ no no SÍ SÍ no, 2 días de lavado. Cambios: días 0, 7, 21 y 35; días de lavado: 0–1, 7–8, 21–22 y 35–36. Cuentan **17 días con la acción** (21 − 4) y **17 sin ella**; la semana 3 («no» tras «no») no pierde ningún día.

**Razonamiento.** Excluir los primeros días de cada periodo es una forma estándar de lidiar con el arrastre. **[Aproximación]** razonable: la duración correcta depende de cada acción, y la app la deja a tu criterio (o a la plantilla).

**El problema grave que tuvo** (H-7-1, resuelto en la 10c). Antes del mínimo de 7 días, el lavado podía **comerse una condición entera**: la plantilla «Creatina» (entonces de 8 semanas, antes y después, 28 días de lavado) contaba **28 días sin la acción y 0 con ella**, y no podía dar resultado nunca. Ahora dura 12 semanas: cambio el día 42, lavado de los días 42 a 69, y cuentan **42 días sin la acción y 14 con ella** (calendarios en la versión completa, §8.5).

## 8.5 El resultado: diferencia, intervalo y tamaño del efecto

### 8.5.1 Qué calcula (`Experimentos.resultado` y `Estadistica.diferenciaEnDias`)

Recorre los días desde `inicio` hasta el último día del plan, hasta hoy o hasta el día en que lo terminaste (`Experimento.fin`), lo que llegue antes; **hoy no entra**, porque las variables que se acumulan (pantalla, pasos, enfoque) estarían a medias. Cada día que está en el experimento, no es de lavado y **tiene dato** va a la lista «con» o «sin».

**1. Diferencia de medias con su intervalo**, si hay **al menos 3 días en cada condición**. Un **intervalo de confianza del 95 %** es un rango de valores plausibles para el efecto: si se repitiera el experimento muchas veces con el mismo método, el 95 % de esos rangos contendría el efecto real… **si los supuestos se cumplen**. El supuesto delicado es la **autocorrelación**: que un día se parezca al anterior. La de orden 1, *ρ*, va de −1 a 1; 0 significa días independientes. El cálculo:

- *ρ* de cada condición (`Estadistica.ar1Grupos`): la correlación entre pares de días **seguidos** de esa condición, si hay al menos 10 pares; si no, la de las dos condiciones juntas (cada una centrada en su media); si tampoco, 0. Como en series cortas sale más baja que la real, se corrige: *ρ* + (1 + 4*ρ*)/*n* (la aproximación de primer orden atribuida a Marriott y Pope, 1954; la forma exacta no se leyó en el artículo **[sin verificar]**), y se acota entre 0 y 0.9: solo se corrige hacia un intervalo más ancho.
- El error estándar de cada condición se multiplica por √((1 + *ρ*)/(1 − *ρ*)), y su número efectivo de días es *n*(1 − *ρ*)/(1 + *ρ*), nunca menos de 2 (`Estadistica.diferenciaCompleta`):

$$EE = \sqrt{\frac{s_c^2}{n_c}\cdot\frac{1+\rho_c}{1-\rho_c} + \frac{s_s^2}{n_s}\cdot\frac{1+\rho_s}{1-\rho_s}}\qquad\qquad (8.1)$$

- Los grados de libertad son los de Welch con esos *n* efectivos, y el intervalo usa el cuantil de la *t* de Student (`Estadistica.cuantilT(0.975, gl)`):

$$\bar{x}_c - \bar{x}_s \;\pm\; t_{0.975,\,gl}\cdot EE\qquad\qquad (8.2)$$

donde $\bar{x}$ son las medias, $s^2$ las varianzas muestrales y *n* los días de cada condición.

**2. Mensaje.** Mientras dura el plan (`Experimentos.enCurso`), la tarjeta dice «Provisional: el plan sigue en curso. Mirar el resultado a medio camino y parar en la primera «señal» engaña; espera a que termine», sin veredicto. Al terminar: si el intervalo no toca el cero, «es una señal para ti»; si lo toca, «todavía no se distingue del azar»; y, desde 10f, si sellaste un criterio, «Según el criterio que sellaste: se cumple» o «no se cumple».

**3. g de Hedges** (desde 10e, D.96; antes, la *d* de Cohen sin corregir): la diferencia medida en «desviaciones estándar», cuántas veces el vaivén normal de un día a otro (Cohen, 1988), con la desviación agrupada $s_p$ y la corrección de Hedges (1981), que quita lo que la *d* se infla con pocos días:

$$d = \frac{\bar{x}_c - \bar{x}_s}{s_p}, \qquad s_p = \sqrt{\frac{(n_c-1)s_c^2 + (n_s-1)s_s^2}{n_c+n_s-2}}, \qquad g = d \cdot \Big(1 - \frac{3}{4(n_c+n_s) - 9}\Big)\qquad\qquad (8.3)$$

La palabra (`Experimentos.tamano`): |*g*| < 0.2 «muy pequeño», < 0.5 «pequeño», < 0.8 «mediano» y desde 0.8 «grande», los puntos de referencia de Cohen (1988); desde 10e la tarjeta añade que es «para cómo varían tus días; no se compara con los estudios entre personas».

**4. Prueba de tendencia** (sección 8.7).

### 8.5.2 Ejemplo trabajado (Figura 8.1, izquierda)

Experimento inventado: 6 semanas alternas (SÍ no no SÍ SÍ no), 1 día de lavado, medida = valencia del día, con un efecto real de +0.5 y autocorrelación real de 0.3. Cuentan 19 días por condición. Medias 4.84 y 4.26: diferencia **+0.58**. Varianzas 0.474 y 0.871; $s_p$ = 0.820 y *d* = 0.71; con la corrección, 1 − 3/(4 × 38 − 9) = 0.979, *g* = **0.69**, «mediano» (antes de 10e, la tarjeta decía 0.71).

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

: Tabla 8.1. Por qué hacía falta corregirlo

La autocorrelación de 0.3 a 0.5 es ilustrativa **[sin verificar]**. **El método actual es conservador**: da menos del 5 % de falsas señales. **El costo es el poder** (la probabilidad de ver un efecto que sí existe): con autocorrelación 0.3 y un efecto real de 0.5 desviaciones, 6 semanas lo detectan el **13 %** de las veces; con un efecto de 0.8, el **29 %**; con 12 semanas, el **30 %** y el **62 %**.

![Figura 8.1. Izquierda: un experimento inventado con su resultado calculado como antes de la 10c, con cada corrección por separado y como desde la 10c. Derecha: con qué frecuencia el método de la app da una «señal» cuando la acción no hace nada, antes y desde la 10c. Script: `figuras/fig21_resultado_experimento.py`.](figuras/fig21_resultado_experimento.png){width=100%}

### 8.5.4 Qué sería más adecuado: la prueba de aleatorización

**[Validado]** en la literatura de caso único (Kratochwill y Levin, 2010; Tanious y Onghena, 2019): calcular la diferencia con el plan que de verdad se sorteó y con **cada uno de los otros planes que pudieron salir**; el valor *p* es la fracción de planes que dan una diferencia igual o mayor que la real. **No supone que los días sean independientes**, porque se apoya solo en que el sorteo fue real. Su límite: en una prueba de una cola, el *p* más pequeño posible es 1 / número de planes, que con el sorteo por pares es 0.25 con 4 semanas, 0.125 con 6, 0.0625 con 8 y 0.016 con 12: **con 4, 6 u 8 semanas no se puede llegar a *p* < 0.05**, por grande que sea el efecto. No está en la app; desde 10e (D.97) la hace `vm_experimento(id)` en R (H-7-11): repite el resultado de la app y compara la diferencia observada con la de los 2^k planes del sorteo por pares, cada uno con su lavado, con *p* de dos colas; en «antes y después» devuelve NA. Desde 10f devuelve también `sentido`, `umbral`, `intacto` (lo que exportó la app; R no recalcula la huella) y `criterio`, con la misma regla que la app. Como el plan **espejo** (cada «SÍ» cambiado por «no») da, sin lavado, la misma diferencia con el signo contrario, el mínimo real es **2 / planes**: con 12 semanas, 2/64 ≈ 0.031, todavía bajo 0.05.

### 8.5.5 El tamaño del efecto en una sola persona

Desde 10e lleva la corrección de Hedges (1981), que importa solo con muy pocos días: con 3 + 3, el factor es 0.80 (H-7-7, resuelto). Más importante: Cohen propuso 0.2, 0.5 y 0.8 para diferencias **entre personas**; aquí la desviación es la de **tus días**, que suele ser menor, así que el mismo cambio da un *d* más grande. **[Aproximación]**: útil como escala dentro de la app, no para comparar con la literatura.

## 8.6 La adherencia: lo asignado y lo hecho

El resultado compara **lo asignado** por el plan: si en una semana «SÍ» te dormiste tarde tres noches, esos días cuentan igual como «con la acción». Es el análisis por **intención de tratar** de los ensayos clínicos, legítimo (estima el efecto de *intentar* la acción) **[Validado]**, pero diluye el efecto si no la cumples.

«Terminar» guarda el día lógico en `Experimento.fin`, y el resultado deja de contar los días posteriores (H-7-2). Con las respuestas de «¿Lo hiciste hoy?», `Experimentos.resultado` da el **cumplimiento** sin contar el lavado: la tarjeta muestra el porcentaje de días en que lo hiciste en las semanas «SÍ» y en las «no» (H-7-8). En la plantilla «Creatina», desde 10e (D.96, H-9-26), los días sin respuesta se completan con el registro de suplementos (`supl_creatina_tomado`), que existe desde la primera toma anotada. Si el cumplimiento difiere al menos 20 puntos entre condiciones, da como dato secundario el **cociente** (`Resultado.cociente`): la diferencia según el plan entre la diferencia de cumplimiento. *Ejemplo:* diferencia +0.30; lo hiciste el 70 % de los días «SÍ» y el 10 % de los «no»; cociente = 0.30 ÷ 0.60 = **+0.50**. Estima el efecto de **hacerlo de verdad**, suponiendo que saber qué semana tocaba no cambia nada por sí solo, lo que es dudoso sin cegamiento (sección 8.9). **[Aproximación]**.

No se calcula el análisis «por lo hecho» (días hechos contra no hechos): si cumples más en los días buenos, exagera el efecto (en una simulación del módulo 9 lo **duplicaba**).

## 8.7 La prueba de tendencia (`Experimentos.tendencia`)

Una **tendencia** es que la medida suba o baje sola con el tiempo, por algo ajeno al experimento (la estación, avanzar en el gimnasio). En un **antes/después**, el periodo «después» se verá mejor si la medida sube sola, aunque la acción no haga nada: es la principal debilidad de esos diseños, y la razón de alternar y sortear (Kratochwill y Levin, 2010).

Con al menos 8 días que cuentan y tienen dato (sin hoy), la app ajusta por mínimos cuadrados, **a la vez**, la tendencia y la condición:

$$y = a + b\,i + c\,\text{con} + \varepsilon\qquad\qquad (8.4)$$

con *i* = número de día y «con» = 1 los días con la acción. *b* es la **pendiente dentro de cada condición**: cuánto cambia la medida por día una vez descontado el efecto de la condición. El aviso («Ojo: dentro de cada condición, tu medida venía cambiando sola…») salta si |*b*/EE(*b*)| supera el cuantil 0.975 de la *t* con *n* − 3 grados de libertad.

**Por qué así** (H-7-6). Hasta la 10c, la recta se ajustaba con las dos condiciones juntas y confundía un **efecto real** con una tendencia: con *d* = 0.8 y sin tendencia, el aviso aparecía en el **71 %** de los antes/después. Ahora, en esa situación, sale el **5 %** de las veces en los antes/después y el **6 %** en los alternos: lo esperado por azar. **[Validado]** como método (regresión con tendencia y condición).

**Lo que no resuelve.** Con una tendencia de 0.02 desviaciones por día y **sin efecto real**, el antes/después da una falsa señal el **39 %** de las veces y las semanas alternas por pares, el **2 %**. En ese antes/después la prueba avisa solo el **22 %** de las veces, porque dentro de cada mitad hay poca información para estimar la pendiente; y el efecto que se muestra no descuenta la tendencia (detalle y figura en la versión completa, §8.8).

## 8.8 Las plantillas (`Experimentos.PLANTILLAS`)

| Plantilla | Acción | Medida | Diseño | Semanas | Lavado |
|---|---|---|---|---|---|
| Creatina | 5 g al día | `fuerza_ref_kg` (hasta 10f, `volumen_kg`) | antes/después | 12 | 28 |
| Hora de dormir | en la cama antes de las 00:00 | `valencia` | alterno | 6 | 1 |
| Pantalla antes de dormir | sin pantalla la última hora | `sueno_calidad` | alterno | 6 | 1 |
| Cafeína en la tarde | nada después de las 14:00 | `sueno_horas` | alterno | 6 | 2 |

: Tabla 8.2. Las plantillas (Experimentos.PLANTILLAS)

**Por qué la creatina va en antes/después.** La creatina se acumula en el músculo: Hultman et al. (1996) midieron que la creatina total del músculo sube alrededor de un 20 % tras 6 días a 20 g diarios, se mantiene con 2 g diarios durante 30 días más y, 30 días después de dejarla, ya no difería del valor inicial. Si la reserva tarda semanas en llenarse y en vaciarse, alternar semanas solo compararía «medio lleno» con «medio lleno». El razonamiento es correcto.

**La medida** (H-7-14). Hasta 10f era `volumen_kg`, que depende de **qué sesión** hiciste (pierna o brazo), no solo de tu fuerza. Desde 10f (D.114) es `fuerza_ref_kg`: la mejor 1RM estimada del día (Epley, `Macrofactor.e1rm`: peso × (1 + repeticiones/30), series de 1 a 12 repeticiones sin calentamiento) en **un solo ejercicio fijo**, el press inclinado con mancuernas; vacía los días sin él. **[Aproximación]** (la 1RM de Epley). El ejercicio se reconoce por su nombre (`Macrofactor.esReferencia`: el nombre sin acentos contiene «press», «inclin» y «mancuern», «dumbbell» o «db»), en inglés o en español **[Diseño propio]**; no se comprobó el nombre exacto que usa MacroFactor, y un nombre sin «press» no contaría (las aperturas y los curls quedan fuera). *Ejemplo (inventado):* 24 kg × 8 da 24 × (1 + 8/30) = 30.4 kg y 26 kg × 5 da 30.3 kg: `fuerza_ref_kg` = 30.4. Lo que no resuelve: **antes/después + fuerza que progresa** sigue siendo la peor combinación para la tendencia (si vas subiendo de peso, la cifra sube sola, y la nota de la plantilla lo advierte), y solo cuentan los días con ese ejercicio: con 14 días con la acción, hacerlo una vez por semana deja unos 2 días medidos (hacen falta 3), y la nota de la plantilla pide al menos dos por semana. Los experimentos creados antes de 10f siguen midiendo `volumen_kg`.

Las otras tres son razonables: efectos que aparecen y se van en uno o dos días, medidas diarias y lavado corto. **[Aproximación]**: los lavados no vienen de un estudio específico.

## 8.9 Cegamiento y efecto expectativa

**Cegamiento** es que quien recibe la acción no sepa si la está recibiendo (como con una pastilla de azúcar). Aquí es imposible: **sabes** si te acostaste antes o si tomaste café. Importa sobre todo con medidas **subjetivas**: si crees que dormir antes te hará sentir mejor, es probable que califiques tus días un poco mejor en las semanas «SÍ», aunque el sueño no haya cambiado. Es el **efecto expectativa** o placebo; la extensión CENT pide reportar si hubo cegamiento justamente por esto (Vohra et al., 2015).

Ayuda preferir **medidas objetivas** (`sueno_horas` del reloj en vez de `sueno_calidad`), mirar el resultado **solo al final** y, para la valencia, preferir `muestreo_valencia` (avisos al azar en el momento, capítulo 3); no pude comprobar cuánto reduce el sesgo de expectativa **[sin verificar]**. **[Aproximación]**: sin cegamiento, el resultado responde a «¿hacer esto **sabiendo que lo hago** me cambia?», una pregunta legítima para uso personal que no aísla el efecto fisiológico.

## 8.10 «Antes y después» en Explorar: un estudio observacional

`EtapasDeVida` (`ui/pantallas/Explorar.kt`) toma los **8 días más recientes** en que guardaste cambios en «Conocerte» (mudanza, pareja, trabajo…) y compara siete variables (valencia, horas de sueño, minutos de pantalla, pasos, gasto, personas mencionadas y palabras escritas) entre los días −1 a −60 y 0 a 59 respecto al cambio, con la misma `Estadistica.diferenciaEnDias`.

Nadie sorteó cuándo mudarte, y esos cambios vienen con muchos otros. Es un **estudio observacional** del tipo de *serie de tiempo interrumpida* (comparar una serie antes y después de un evento). **[Aproximación]**: útil para generar ideas, no para concluir causas. Fallos (H-7-12): hasta **56 comparaciones** (8 × 7) sin corregir, así que aunque nada cambiara cabe esperar unas **3** «claras» por azar; tendencias; la fecha es la del registro, no la del cambio real; y `personas` y `palabras` miden **cuánto escribes**, no tu vida social.

## 8.11 Lo que el resultado puede y no puede decir

**Puede decir**, si el experimento fue alterno, sorteado, con lavado razonable, una medida objetiva, suficientes semanas, sellaste el criterio (desde 10f) y miraste solo al final: «en mi caso, en este periodo, hacer esto se asoció con este cambio, y es poco probable que sea solo por cómo cayeron las semanas». **No puede decir** que el efecto sea igual en otra época de tu vida ni que sea fisiológico y no expectativa, y no dice nada si el diseño fue antes/después con una medida que tiende a subir o bajar sola.

## 8.12 En resumen

1. Los experimentos siguen la lógica de los **ensayos N-de-1**: semanas con y sin la acción, **sorteadas por pares**, que empiezan en lunes, con la hipótesis **sellada** y, desde 10f, si quieres, el criterio, con una huella que avisa si algo cambió (H-7-10).
2. No se deja sellar si alguna condición podría quedar con menos de 7 días; antes, la plantilla «Creatina» nunca podía dar resultado (H-7-1).
3. El **intervalo** usa la *t* con grados de Welch y el error estándar corregido por autocorrelación: 3–4 % de falsas señales en la simulación, frente al 14–24 % del método anterior, a costa de poder. Mientras dura el plan, es **provisional**.
4. La **prueba de aleatorización** es lo más adecuado; no está en la app, pero desde 10e la hace `vm_experimento()` en R (H-7-11), y con el sorteo por pares solo 12 semanas pueden dar *p* < 0.05.
5. La **prueba de tendencia** separa tendencia y condición, pero un antes/después con tendencia real sigue dando falsas señales el 39 % de las veces.
6. El análisis es **según el plan**; «¿Lo hiciste hoy?» da el cumplimiento. Sin cegamiento, conviene usar medidas objetivas y mirar solo al final.
7. «Antes y después» en Explorar es **observacional**, con hasta 56 comparaciones sin corregir (H-7-12).
8. La entrega 10e resolvió H-7-7 (*g* de Hedges) y H-7-11 (`vm_experimento`), y la 10f, H-7-10 (el criterio se sella, con huella) y H-7-14 («Creatina» mide `fuerza_ref_kg`). Del módulo 7 no queda ninguno pendiente: 12 resueltos, y H-7-6 y H-7-12 solo en parte.


# Capítulo 9. Reportes, visualización y motivación

> **Para qué sirve este capítulo.** Explica qué te devuelve la app a cambio de registrar: los **reportes** (semanal, mensual y anual), **«Tu año»**, los **logros** y las **campañas** semanales; de dónde sale cada cifra y qué decisiones de visualización se tomaron. Después discute el lado científico: un sistema de motivación ayuda a que no falten datos, pero también puede **cambiar lo que se mide**. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`). **Los ejemplos son inventados.** Qué datos privados llegan a cada salida está en el capítulo 10.

## 9.1 De dónde salen las cifras: `Periodo.calcular`

Los reportes y «Tu año» no calculan nada por su cuenta: usan un mismo resumen del periodo, `Periodo.calcular(ini, fin)` (`ciencia/Periodo.kt`). Las reglas que más pesan:

| Cifra | Cómo se calcula exactamente |
|---|---|
| **Ánimo del día** (`valDe`) | La valencia del diario, como `valencia` en el panel y en R (desde la entrega 10c). |
| **Ánimo medio** | Promedio de los «ánimos del día» que existen. Si hubo días con valencia solo en los avisos (`diasSoloAvisos`), el reporte añade «*N* días solo con avisos no cuentan». |
| **Sueño medio** | Promedio de las noches del reloj con **más de 120 minutos** (las más cortas se descartan como siestas o errores). |
| **Hora de dormir media** | `Exportar.horaDecimal`, como el panel y R: las anteriores a las **12:00** suman 24 (la 01:00 cuenta como 25:00). |
| **Mejor marca** | `Macrofactor.e1rm`, la misma de la exportación: peso × (1 + repeticiones/30) (**fórmula de Epley**), solo en series de **1 a 12** repeticiones y sin calentamientos, redondeada a 0.1 kg. |
| **Gasto e ingresos** | Por el **día del diario** (`Gasto.dia`, `Ingreso.dia`: antes de las 5:00 cuenta para el día anterior), igual que el panel. |
| **Comparación con el periodo anterior** | Solo si el periodo anterior tiene **al menos 3 días** con dato. |
| **Emociones, pensamientos y con quién** (desde el esquema 18) | `Periodo.animoDe`, con los registros de ánimo no privados: cada emoción cuenta una vez por registro, «no sé» no cuenta, «otro» va con lo que escribiste y se dice cuántas veces fue «mucho». Conteos para leer: no entran a ningún promedio ni al panel. **[Diseño propio]** |

: Tabla 9.1. De dónde salen las cifras: Periodo.calcular

El **periodo anterior** es el de la misma duración inmediatamente antes. Las demás cifras están en la versión completa, §9.2.

**Detalle a saber** (H-8-10 y H-5-15, resueltos en 10c, D.77). Hasta la entrega 10c, el «ánimo del día» mezclaba dos instrumentos distintos: la valencia recordada del diario y, cuando faltaba, el promedio de los registros momentáneos, que no son la misma medida (capítulo 3). Ejemplo: una semana con valencia del diario 5, 6 y 4 en tres días y avisos con promedio 3 en otros dos: antes, ánimo medio (5 + 6 + 4 + 3 + 3) / 5 = 4.2; ahora (5 + 6 + 4) / 3 = **5.0**, «2 días solo con avisos no cuentan». La **activación** del día sigue completándose con los avisos cuando falta la del diario.

## 9.2 Los reportes

### 9.2.1 Cuándo se generan y qué muestran

La tarea periódica (`avisos/Trabajo.kt`) genera, sin que lo pidas, **el semanal** cada **lunes desde las 8:00**, de la semana anterior (una imagen PNG), y **el mensual** cada **día 1 desde las 9:00**, del mes anterior (PNG y PDF). **El anual** se pide a mano. Se guardan en las carpetas públicas del teléfono, **`Pictures/VestigiumMei`** y **`Download/VestigiumMei`**. Desde el módulo 10a, los que se generan solos van **siempre en modo discreto** (iniciales en vez de nombres) y **sin la frase del mejor momento**; los que pides a mano siguen tu ajuste (privacidad: capítulo 10, H-8-4).

El reporte (`Reportes.cuerpo`) pone arriba hasta **9 cifras** y después **solo las secciones que tienen datos**. El mensual incluye un **mapa abstracto**: cada lugar guardado es un punto sobre una cuadrícula, **sin calles ni nombres**. **El modo discreto** (`Config.discreto`, apagado por omisión) cambia en los reportes los nombres de las personas por iniciales (`Reportes.persona`).

### 9.2.2 Decisiones de visualización

La Figura 9.1 imita «La semana de un vistazo» con datos inventados y las reglas de `semanaVisual`:

- **Ánimo**: una **medalla** por día con el color del ánimo y el número (`%.0f`), que desde 10c es la valencia del diario, un entero de 1 a 7. El color va del color de alerta (1), pasando por un gris neutro (4), al color del tema (7) (`Lienzo.colorAnimo`).
- **Diario**: hojas apiladas; una hoja = max(10, ⌈máximo de la semana / 10⌉) palabras, hasta 10 hojas.
- **Sueño**: barras de 0 a 10 h, con una línea punteada en **7 h** y la barra en tono de alerta si la noche duró **menos de 6.3 h**.
- **Pasos**: ⌈pasos / 2 000⌉ huellas, hasta 10.

![Figura 9.1. «La semana de un vistazo», imitada con datos inventados y las mismas reglas del código. El ánimo de la figura trae dos días con decimales (4.5 y 6.5), como podía pasar hasta 10c; desde entonces el número de la medalla siempre es un entero. Script: `figuras/fig23_reporte_semanal.py`.](figuras/fig23_reporte_semanal.png){width=100%}

**Qué dice la investigación sobre gráficas.** Cleveland y McGill (1984) midieron qué tan bien la gente lee distintas codificaciones: la **posición sobre una escala común** (la altura de una barra con su eje) se lee con mucha más precisión que el **área** o el **color**. Aplicado a la app **[Aproximación]**: las barras de sueño, con eje y número, son la codificación más precisa; la medalla del ánimo codifica con color, que se lee mal, pero lleva el número escrito; las hojas y huellas son **conteos de iconos**, buenos solo para comparaciones gruesas. La línea de 7 h y el corte de 6.3 h son **[Diseño propio]**: 7 h coincide con la recomendación habitual mínima de sueño para adultos **[sin verificar]**; el 6.3 h no tiene una fuente conocida. El color «de alerta» **juzga** el dato (rojo = malo), lo que refuerza la idea de que hay valores «correctos» (sección 9.6).

### 9.2.3 Comparar con el periodo anterior

`Reportes.cambio` escribe «+0.4 de ánimo frente al periodo anterior» cuando la diferencia es al menos **0.05** (o **0.5** en las cifras sin decimales); si no, «igual que el periodo anterior». Hasta 10e no había ninguna noción de ruido (H-8-11). Desde 10e (D.101), si la diferencia es menor que su **error típico**, √(*s*₁²/*n*₁ + *s*₂²/*n*₂) (`Periodo.errorDif`, con al menos 3 días en cada periodo), dice «parecido al periodo anterior». *Ejemplo:* ánimo 5, 6, 4, 5 esta semana y 4, 6, 3, 5, 5 la anterior: diferencia 0.4, error típico 0.65, «parecido». **[Aproximación]**: un error típico es poco exigente y no descuenta que los días seguidos se parecen, así que el filtro se queda corto; conviene leer estas flechas como «qué pasó», no como «qué cambió de verdad».

## 9.3 «Tu año»

`TuAnio.calcular` usa el mismo `Periodo` del año y le suma equivalencias, récords, la palabra del año y otras piezas (versión completa, §9.3.5). Tres merecen atención:

- **El día más raro** sale de `Analisis.atipicos` (umbral 2.8, 15 variables fijas). Desde 10e usa todos los días del año elegido (D.100); antes solo devolvía días de los últimos 365 contados desde hoy, y al ver un año pasado podía faltar.
- **«La relación del año»** es la correlación no obvia con mayor |*r*|, con *q* < 0.05 y *n* ≥ 30, si el año tiene al menos 40 días. Elegir el máximo |*r*| entre cientos de pares sobrestima su tamaño: es la que tuvo más suerte además de ser real (un sesgo de selección; capítulo 6). El *r* mostrado será, en promedio, más alto que el verdadero. **[Aproximación]**.
- **Las predicciones de enero** (8 preguntas) incluyen tu **ánimo medio**: crean una expectativa sobre la variable que luego mides. Es una forma de **reactividad**, es decir, de que medir algo cambie lo que se mide (sección 9.6).

## 9.4 Logros

### 9.4.1 Qué son

Un **logro** es una medalla que se gana al llegar a una meta (`ciencia/Logros.kt`), en cuatro niveles (bronce, plata, oro, legendario). **Se calculan de tus datos cada vez**; lo único que se guarda es cuáles ya viste. Tres tipos principales:

| Tipo | Ejemplos (metas exactas) |
|---|---|
| **Cantidad** (`escalones`) | Diario: 1, 7, 30, 100, 365 y 1000 días; avisos contestados: 50, 250 y 1000 |
| **Récord** | 20 000 pasos en un día; reacción con mediana < 250 ms |
| **Constancia** (`constancia`) | Escribe 20, 25 o 28 de 30 días; 7 h de sueño 15, 20 o 25 de 30 noches; cada hábito activo 15, 20 o 25 de 30 días (salvo el ligado a las recaídas) |

: Tabla 9.2. Qué son

Además hay **rachas** de diario (7, 30 y 100 días seguidos), **secretos** que aparecen al lograrlos y **logros propios** (una fuente, un umbral y un modo que eliges), que desde el módulo 10a no se ofrecen sobre nada privado (versión completa, §9.4.1).

### 9.4.2 Constancia en lugar de rachas

Una **racha** son días **seguidos** cumpliendo algo: un solo día sin cumplir la rompe. La **constancia** es cumplir **la mayoría** de los días de una ventana (25 de 30), sin necesidad de racha. Las rachas castigan un día perdido con volver a cero; la constancia tolera días malos. **[Diseño propio]** con buen fundamento: para un registro de por vida lo que importa es **no abandonar**, y «25 de 30» premia eso sin convertir un tropiezo en fracaso.

### 9.4.3 Lo que premian, y por qué importa para los datos

| Premian… | Ejemplos | Efecto sobre los datos |
|---|---|---|
| **Registrar** | días de diario, avisos contestados, momentos | Menos datos faltantes (bueno). Riesgo: entradas de relleno. |
| **Una conducta** | pasos, sueño ≥ 7 h, entrenar, hábitos | Cambian la conducta medida: es una intervención. |
| **Un valor de la medida subjetiva** | «Día perfecto» (7 de 7), «Remontada» (un ≥ 6 tras un ≤ 2 en alguno de los 3 días con valencia anteriores) | Premian **un número que tú escribes** (H-8-6). Se quedan por decisión del autor (D.117). |
| **Un rendimiento** | «Rayo» (reacción < 250 ms) | Los tiempos pueden mejorar por práctica y no por estar más despierto. |

: Tabla 9.3. Lo que premian, y por qué importa para los datos

Los dos logros del tercer grupo son **secretos** y se ganan una sola vez, lo que reduce mucho el incentivo. **[Diseño propio]**, de bajo riesgo en la práctica, pero contrario al principio de no premiar el valor de una medida. **Desde 10f se quedan como están, por decisión del autor** (D.117). El riesgo que queda es **un incentivo pequeño a sesgar la valencia del diario** (poner un 7 en vez de un 6), que desaparece en cuanto se gana cada uno; que sean «secretos» casi no protege, porque el único usuario, el autor, sabe que existen. Si se sospecha el efecto, se puede comparar la proporción de 7 antes y después del día en que se ganó (más en la versión completa, §9.4.3).

## 9.5 Campañas

Una **campaña** (`ciencia/Campanas.kt`) es un reto opcional de lunes a domingo con una meta de días; al aceptarla se puede elegir de 3 a 7. Hay 11 tipos de reto, por ejemplo «Noches de 7 horas» (el reloj marca ≥ 420 min de sueño; meta 5) o «Menos de 3 horas de pantalla» (pantalla < 180 min; meta 4); la tabla completa está en la versión completa, §9.5. Según `Campanas.estado`, cada día cumplido avanza una etapa en un «mapa» narrativo; se **gana** al llegar a la meta y se **pierde** cuando ya no da tiempo (días cumplidos + días que quedan < meta).

**Qué son científicamente.** Una campaña de sueño **es una intervención**: esa semana intentas dormir más. **Hasta el esquema 12 no se guardaban como variable del panel** (solo vivían en `Config.campanas`) (H-8-7). Si en esas semanas también estuviste de mejor ánimo, la app veía «dormir más se asocia con mejor ánimo», sin poder distinguir si fue el sueño, el empujón de la campaña o lo que te llevó a elegirla. Un experimento personal (capítulo 8) separa esto; una campaña, no.

**Desde el esquema 13 (P-11) la campaña es una variable del panel.** Hay una columna `campana_<reto>` por cada reto que hayas tenido (por ejemplo `campana_sueno7`): vale **1** los días que tenías ese reto aceptado y **0** los demás, desde el primer día de tu primera campaña; antes, sin dato. `campanas_n` dice cuántas tenías ese día (puede haber hasta tres a la vez). Codifica la **asignación**, no el cumplimiento. Cada `campana_<reto>` se marca como «obvia» con lo que ese reto busca mover (`Campanas.MUEVE`; capítulo 6), para que no aparezca como hallazgo «en las semanas de campaña de sueño dormiste más». En R, desde la entrega 10d, `vm_obvia` usa la misma regla (`vm_familias.csv`). Lo que sí queda para el análisis es lo interesante: si en esas semanas cambió **otra** cosa, como el ánimo. **[Diseño propio]**.

También desde el esquema 13, una campaña aceptada el miércoles ya no cuenta el lunes ni el martes (`Campana.aceptada`; H-9-34), y abandonarla no la borra (H-9-36; más en la versión completa, §9.5). Al leerlas, recuerda que una campaña es **autoelegida**, no sorteada y que varias semanas seguidas de la misma hacen que la variable cambie poco y pueda clasificarse como «lenta» (capítulo 6).

## 9.6 Motivar sin distorsionar

**Lo que dice la evidencia.** Deterding et al. (2011) definen la **gamificación** como el uso de elementos de diseño de juegos (puntos, medallas, retos, rachas) en contextos que no son juegos. La revisión de Hamari, Koivisto y Sarsa (2014) encontró que la mayoría de los estudios reportan efectos positivos, pero que dependen mucho del contexto y de quién la usa. **[Validado]** que *puede* funcionar; no que funcione siempre. El metaanálisis de Deci, Koestner y Ryan (1999) encontró que las recompensas **tangibles y esperadas** reducen la motivación intrínseca (hacer algo porque te interesa), mientras que la retroalimentación verbal positiva tiende a aumentarla. Las medallas de la app están a medio camino **[Aproximación]**. Además, el autoseguimiento ya es reactivo: tiende a aumentar las conductas deseables y a reducir las indeseables (Korotitsch y Nelson-Gray, 1999); premiar ciertos valores suma un segundo empujón. En los rastreadores personales, el abandono es uno de los problemas centrales (Li et al., 2010).

**El dilema.** La app tiene dos objetivos que tiran en direcciones opuestas: **que no falten datos** (los que faltan no faltan al azar, capítulo 3) y **que los datos midan la vida tal como es**. Si registrar o ciertos valores dan premios, los datos empiezan a medir, en parte, el deseo de ganar el premio. Es la **ley de Campbell**: cuanto más se usa un indicador para premiar o decidir, más presión hay para distorsionarlo y más deja de reflejar lo que medía (Campbell, 1979).

**Cómo lo resuelve la app:**

- **Bien**: la mayoría de los logros premian **registrar** o **conductas objetivas medidas por sensores** (pasos y sueño del reloj, pantalla del teléfono), no lo que escribes.
- **Bien**: la constancia tolera días malos, así que no hay incentivo para inventar un día… salvo en las rachas de diario.
- **Mejorable**: «Día perfecto» y «Remontada» premian valores de la valencia (H-8-6) y se quedan por decisión del autor (D.117); las predicciones de enero incluyen el ánimo medio. Las campañas, desde el esquema 13, sí quedan como variable (H-8-7, resuelto).
- **No se puede evitar**: que ver tus reportes cambie tu conducta. Es, en parte, el propósito de la app.

**Recomendación práctica** (no requiere cambiar código): al analizar una variable que también fue meta de una campaña o un logro, mirar si su relación con otras cambia antes y después de empezar a perseguir esa meta.

## 9.7 En resumen

1. Los reportes y «Tu año» usan un mismo resumen, `Periodo.calcular`; desde la entrega 10c su «ánimo del día» es solo la valencia del diario (H-8-10).
2. Los reportes se generan solos (lunes y día 1) y se guardan en carpetas públicas; los automáticos van en modo discreto y sin la frase del mejor momento (capítulo 10, H-8-4).
3. La visualización combina codificaciones precisas (barras con eje y números) con otras gruesas (colores, iconos) **[Aproximación]**; las comparaciones con el periodo anterior dicen «parecido» desde 10e cuando la diferencia es menor que su error típico, un filtro que se queda corto (H-8-11).
4. «La relación del año» es el máximo |*r*| de muchos pares y sobrestima su tamaño **[Aproximación]**.
5. Los logros premian sobre todo **registrar** y **conductas medidas por sensores**, y prefieren la **constancia** a las rachas **[Diseño propio]**. Dos logros secretos premian valores de la valencia (H-8-6); desde 10f se quedan por decisión del autor (D.117), con un riesgo pequeño y documentado.
6. Las campañas son **intervenciones**. Desde el esquema 13 son variables del panel (`campana_<reto>`, `campanas_n`), obvias con lo que cada reto mueve (H-8-7, resuelto).
7. La gamificación puede ayudar a no abandonar, con efectos que dependen del contexto (Hamari et al., 2014); las recompensas pueden cambiar la motivación y lo que se mide (Campbell, 1979).


# Capítulo 10. Privacidad, seguridad e integridad de los datos

> **Para qué sirve este capítulo.** Explica **dónde viven tus datos, quién puede verlos y qué tan seguro es que no se pierdan**: el almacenamiento en el teléfono, el seguimiento privado, el cifrado del respaldo, los respaldos automáticos, la verificación, la restauración y los servicios externos. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`). El seguimiento privado (impulsos, recaídas, libido, medicamentos) se describe **solo en su mecánica**, sin ejemplos ni datos personales, y sin reproducir las palabras clave que el código usa para reconocer ciertos hábitos.

## 10.1 Dónde viven los datos

- **En el teléfono**, en una base SQLite de documentos JSON (`vestigium.db`, capítulo 2), en el almacenamiento privado de la app.
- **Sin copia automática de Android**: el manifiesto declara `android:allowBackup="false"`, así que la copia de seguridad del sistema **no** sube la base a la nube. Los únicos respaldos son los que hace la app (sección 10.4). **[Validado]** como decisión: evita que el diario termine en una copia que no controlas.
- **La base no tiene un cifrado propio**: depende del cifrado del almacenamiento que hace Android cuando el teléfono tiene bloqueo de pantalla. Cómo funciona ese cifrado en cada versión de Android no se verificó **[sin verificar]**.
- **Bloqueo de la app** (opcional, `Config.bloqueo`, apagado por omisión): al abrirla pide huella o el bloqueo del teléfono (`BiometricPrompt`) y vuelve a pedirlo si estuvo **más de 60 segundos** en segundo plano. Si el teléfono no tiene ningún bloqueo configurado, la app no puede pedirlo y queda abierta. Protege contra miradas casuales, no contra alguien con acceso técnico al teléfono. **[Diseño propio]**.

## 10.2 El seguimiento privado

### 10.2.1 Qué es y dónde se ve

El **seguimiento privado** son los impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas. Desde el módulo 10a hay **una sola regla, en un solo archivo** (`data/Privado.kt`). Es privado:

- todo momento de tipo `impulso`, `recaida` o `libido` (`Momento.privado`, que usa `Privado.TIPOS`);
- en el día, la intensidad del impulso (0 a 5), si hubo recaída, los detonantes y los **medicamentos**;
- el **hábito ligado a las recaídas**: con el interruptor encendido, el que elegiste en Privacidad o, si no elegiste, un hábito activo cuyo nombre contenga ciertas palabras clave; con el interruptor apagado, solo el que elegiste.

**Dónde se ve: solo en la pantalla Privado** (Más → Privado). **Dónde no se ve: en ninguna otra parte de la app**, ni con el interruptor encendido: ni en Día, Hoy, calendario, mapa o widgets; ni en reportes, «Tu año», logros (también los propios), campañas o experimentos; ni en la red de personas, las variables `per_`, `pen_` y `tema_` o las listas de análisis (`Estadistica.variables()` excluye las variables privadas). La lista completa está en la versión completa, §10.3.1.

**El interruptor `Config.seguimiento_privado`** (apagado por omisión) solo decide qué se **ofrece** en la pantalla Privado: apagado, medicamentos y momentos de impulso; encendido, también la intensidad del impulso del día, recaídas, libido y detonantes. Fuera de Privado, nunca se muestra.

**La exportación para R lleva todo, siempre.** El panel calcula las variables privadas aunque el interruptor esté apagado, y la exportación las lleva; desde la entrega 10d, `vm_variables.csv` las marca (`privada` = 1). Por omisión, `vm_relaciones()` y `vm_factores()` de R **no** las cruzan, para dar lo mismo que la app; con `privadas = TRUE` sí.

**Registrar una recaída** (`Recaidas.registrar`) guarda el momento con hora y lugar, marca el día con recaída y **desmarca el hábito ligado**. El **resumen privado** (`Recaidas.resumen`) incluye, entre otras cosas, cuántos impulsos pasaron **sin recaída en las 3 horas siguientes** y las rachas (versión completa, §10.3.1).

**Lo que no se puede filtrar:** si escribes algo privado en el texto libre del diario o de un momento común, la app no tiene forma de saberlo.

### 10.2.2 Las rachas: solo los días respondidos (H-8-8)

**Hasta la entrega 10c**, `Recaidas.resumen` empezaba a contar desde el primer día con respuesta y, a partir de ahí, **todo día que no era de recaída contaba como limpio**, aunque no se hubiera contestado nada. Si los días sin registro son justamente los difíciles, la racha queda inflada (capítulo 3).

**Desde la entrega 10c (D.75)** las rachas salen de una función pura, `Recaidas.rachas(recaidas, limpios, hoy)`:

- un día **con recaída** (marcado en el día o con un momento de recaída) reinicia la racha a 0;
- un día cuenta como **limpio** solo si ese día se respondió «no» (`Dia.recaida == false`); suma 1;
- un día **sin respuesta** no suma ni reinicia: se cuenta aparte, como «sin respuesta» desde la última recaída. El día de hoy, mientras no se responda, no cuenta como sin respuesta.

Ejemplo con una secuencia inventada de cuatro días (sí, no, sin respuesta, no): antes, racha actual 3; ahora, **2**, con 1 día sin respuesta. Lo comprueba `CalculosPrueba.rachasSoloConRespuesta`. **[Diseño propio]**: no contar ni romper con los días sin respuesta es un punto medio; romper la racha castigaría olvidar el registro, y contarlos la inflaría.

### 10.2.3 Qué se cerró y las reglas declaradas

Hasta v84, el seguimiento privado se filtraba por seis vías, todas cerradas en el módulo 10a: los impulsos (H-8-1), «la relación del año» (H-8-2), el hábito ligado y los logros propios (H-8-3), los medicamentos (H-8-16), la exportación en claro (H-8-5, cerrada solo en lo que toca a lo privado) y las @menciones de los momentos privados (H-6-1). Detalle en la versión completa, §10.3.3.

**Por qué los logros nunca se ligan al seguimiento privado.** Un logro por «días sin recaída» convertiría el seguimiento privado en un marcador, con la presión de la ley de Campbell (capítulo 9) justo sobre los datos más delicados; uno «por registrar impulsos» premiaría registrarlos. Desde el módulo 10a la regla se cumple. Las sustancias **no** son privadas según la regla declarada.

La Figura 10.1 resume qué tipo de dato llega a cada salida. Comparadas con el código de v90, las reglas que el documento de contexto del proyecto (`VESTIGIUM_MEI_CONTEXTO.md`, v81, sección 7) llama «no negociables» se cumplen en casi todo:

| Regla declarada | ¿Se cumple? |
|---|---|
| WhatsApp y comentarios de YouTube: nunca guardar texto | **Sí** (`ChatDia` guarda solo conteos por chat y día) |
| Notificaciones: no leer su texto | **Sí** (`sensores/Oyente.kt` cuenta solo la app y la hora) |
| Tokens de Spotify y claves de API solo en el teléfono | **Sí** (preferencias privadas, fuera de los respaldos) |
| Fotos nunca se copian | **Sí** (el catálogo guarda hora, coordenadas y aparato) |
| Cartas selladas: sin contenido en búsqueda, CSV ni «Tu año» | **En parte**: `vm_respaldo.json` lleva el texto (H-8-5) |
| Seguimiento privado nunca en reportes, «Tu año», logros, mapas ni widgets; sí en R | **Sí desde el módulo 10a**, con una regla más estricta |
| «Nada pasa por servidores ajenos salvo Drive, Open-Meteo y Spotify» | **No del todo** (sección 10.7; H-8-17); desde 10e, el texto de privacidad de la app nombra todos los servicios, y desde 10f también Ajustes › Privacidad |

: Tabla 10.1. Qué se cerró y las reglas declaradas

![Figura 10.1. Qué tipo de dato llega a cada salida, según el código después del módulo 10a. Borde rojo: hallazgo que sigue abierto (H-8-5). Script: `figuras/fig24_flujo_privacidad.py`.](figuras/fig24_flujo_privacidad.png){width=100%}

## 10.3 El respaldo cifrado

**Qué contiene.** `Respaldos.archivo` arma un **.zip** con `vm_respaldo.json` (diario, momentos, cartas, ajustes… todo), `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv` (solo hora y coordenadas de las fotos, **nunca la foto**) y las portadas. Si pusiste contraseña, ese .zip se **cifra** (se convierte en algo ilegible sin una llave) y se guarda como **.vmc**; el .zip en claro se borra.

**Cómo se cifra exactamente** (`Respaldos.cifrar`):

1. **Sal**: 16 bytes al azar (`SecureRandom`), nueva en cada respaldo. La sal se mezcla con la contraseña para que la misma contraseña dé llaves distintas en cada archivo.
2. **Llave**: **PBKDF2** con HMAC-SHA256, **600 000 iteraciones** desde 10e (antes, 210 000), 256 bits (`ITERACIONES`, `llave`). PBKDF2 convierte la contraseña en una llave **a propósito lenta**: repite un cálculo muchas veces (las iteraciones) para que probar millones de contraseñas cueste mucho (Turan et al., 2010).
3. **IV** (vector de inicialización, un número al azar que se usa una sola vez): 12 bytes, nuevo en cada respaldo.
4. **Cifrado**: **AES-256** en modo **GCM** con etiqueta de **128 bits**. GCM, además de cifrar, añade una **etiqueta de autenticación**: si alguien cambia un solo bit del archivo, el descifrado falla en vez de dar datos alterados (Dworkin, 2007). El encabezado completo entra como **dato adicional autenticado**: no se cifra, pero si alguien lo cambia, el descifrado falla.
5. **Formato** (formato 2, desde 10e, D.104): encabezado de 27 bytes (la marca `VESTIGIUM-MEI-CIFRADO-2` y las iteraciones en 4 bytes) · sal (16) · IV (12) · datos cifrados con su etiqueta. La app sigue abriendo el formato 1 de antes (`VESTIGIUM-MEI-CIFRADO-1`, siempre a 210 000 iteraciones), pero una versión anterior a 10e no abre un .vmc nuevo.

**Qué protege.** Sin la contraseña, el contenido es ilegible, y cualquier cambio del archivo hace fallar el descifrado. Como la sal y el IV son nuevos en cada respaldo, nunca se repite una combinación de llave e IV. Las elecciones siguen las recomendaciones del NIST (Dworkin, 2007; Turan et al., 2010). **[Validado]**. **Qué no protege:** el tamaño y las fechas del archivo se ven, y quien tenga el .vmc puede probar contraseñas sin límite en su propia computadora.

**Las iteraciones (H-8-9, resuelto en 10e).** La guía de OWASP recomienda, para PBKDF2 con HMAC-SHA256, **600 000 iteraciones** (OWASP, s. f.). Desde 10e la app usa esa cifra (`ITERACIONES = 600_000` en `data/Respaldos.kt`) **[Validado]**; hasta 10e usaba 210 000, unas 2.9 veces menos «peaje» por intento, y los .vmc de entonces siguen así. Cuánto tarda en el teléfono no se midió **[sin verificar]**. La app exige al menos **8 caracteres**; una contraseña humana no es aleatoria y se adivina mucho antes que una al azar: **la longitud y el azar de la contraseña importan mucho más que las iteraciones.** (Detalle en la versión completa, §10.4.3.)

**La contraseña en el teléfono.** `ponerContrasena` la guarda **cifrada con una llave AES-256-GCM del Android Keystore** (un almacén de llaves del sistema: la app puede usar la llave, pero no sacarla del teléfono). La contraseña **nunca viaja** en ningún respaldo; si la olvidas, los .vmc no se pueden abrir. **[Diseño propio]** razonable. Si la cambias, las copias que ya existen siguen cifradas con la **anterior**: no se recifran. Desde 10e la app lo avisa al cambiarla y pide confirmar antes de quitarla (H-8-15, D.104).

## 10.4 Respaldos automáticos

**A una carpeta** (`Respaldos.aCarpeta`), una vez al día (la primera vez que corre la tarea diaria después de las 6:00, `Trabajo.diario`):

- **Con contraseña**: se escribe solo `vestigium_respaldo.vmc` y se borran los archivos en claro de antes; desde el esquema 13 (P-09), **después** de que el .vmc quedó escrito (H-9-37).
- **Sin contraseña**: se escriben todos los CSV de la exportación, `vm_respaldo.json`, `vestigium.R` y `vestigium_respaldo.zip`, en claro.
- **Sin contraseña y con datos privados** (desde el módulo 10a): **no se escribe nada** (`Respaldos.bloqueado`); la pantalla Hoy lo avisa y llega una notificación por semana hasta que pongas contraseña.

**A Google Drive** (`data/Drive.kt`):

- Usa el permiso **`drive.file`**: la app solo ve los archivos que ella misma creó, **nunca el resto de tu Drive**. **[Validado]** como el permiso mínimo necesario.
- Cada día **reemplaza** el respaldo del día; guarda una **copia semanal** (se conservan las **8** más recientes) y una **mensual** (las **12** más recientes) **[Diseño propio]**. Con contraseña solo sube el .vmc; sin ella, los CSV y el .zip en claro. Con datos privados y sin contraseña no sube nada, igual que la carpeta.
- **No se reemplaza una copia buena por una mucho más pobre** (esquema 13, P-09). `Respaldos.empobrecido` compara cuántos días y momentos trae el respaldo nuevo con los del último que reemplazó la copia (`Config.drive_conteo`). Si trae **menos del 90 %** de cualquiera de los dos (y el anterior tenía 20 o más), no toca ninguna copia: lo guarda aparte como `vestigium_revisar` y avisa. El umbral de 90 % y el mínimo de 20 son **[Diseño propio]** (H-8-14).

**Cuándo falla.**

- **Sin contraseña, todo queda en claro fuera del teléfono** (H-8-5), incluido el diario completo. La **exportación para R**, que pides a mano, va siempre sin cifrar (R necesita leer los CSV) y la pantalla lo advierte.
- **Las cartas selladas** van sin texto en `vm_cartas.csv`, pero `vm_respaldo.json`, que se exporta junto, lleva **el texto completo** (H-8-5): el sello se puede saltar abriendo el JSON.
- «Restaurar desde Drive» **toma siempre la más reciente** (`Drive.ultimaCopia`, sin contar `vestigium_revisar`). Para traer una semanal o mensual anterior hay que bajarla a mano.

**Los reportes, en carpetas públicas (H-8-4).** Los reportes semanales y mensuales se generan solos (capítulo 9) y se guardan con `MediaStore` en `Pictures/VestigiumMei` y `Download/VestigiumMei`, fuera del almacenamiento privado. Ahí los ve la galería, otras apps con permiso y, si está activada, la copia automática de fotos. El reporte excluye el seguimiento privado y los nombres de lugares, pero incluye ciudades, peso, dinero y ánimo. Desde el módulo 10a, los que se guardan solos van en modo discreto, con iniciales, y sin la frase del mejor momento (`Reportes.reporte(automatico = true)`). **[Diseño propio]**: guardarlos solos en una carpeta pública convierte cada lunes en una salida de datos que no elegiste.

## 10.5 Verificar sin restaurar

`Respaldos.verificar` (desde un archivo) y `Drive.verificar` (la copia más reciente de Drive) descifran el archivo si está cifrado (**si está dañado, aquí falla**, gracias a la etiqueta de GCM), decodifican `vm_respaldo.json` completo y muestran un **informe** de lo que trae.

**Qué garantiza.** Que el archivo se abre, que no fue alterado (si está cifrado) y que el JSON se puede leer de punta a punta. **Un respaldo que nunca se probó no es un respaldo**: **[Validado]** como práctica. Que Java compruebe la suma CRC-32 de cada entrada del .zip no se verificó en la documentación **[sin verificar]**.

**Qué no garantizaba** (H-8-12). Hasta el esquema 12 el mensaje decía «El respaldo se lee completo y no le falta nada», pero la app **no comparaba** nada. Desde el 13 (P-08) dice solo «El respaldo se lee completo» y pone lado a lado respaldo / teléfono en días, momentos, gastos y experimentos. Además, como el lector de JSON ignora los campos que no conoce (`ignoreUnknownKeys = true`), restaurar en una versión vieja un respaldo más nuevo **perdía en silencio** los campos nuevos. Desde el esquema 13, `Importar.esquemaFuturo` **rechaza** restaurar un respaldo con `schema_v` mayor que el de la app, antes de tocar nada: desde 10f, una app de la v91 o anterior (esquema 14) no restaura un respaldo de la v92 (esquema 15). **[Validado]** como práctica.

## 10.6 Restaurar: cómo se combinan los datos

`Respaldos.restaurar` acepta el .vmc, el .zip completo, la exportación extendida o el JSON suelto. Con la app vacía, se trae todo. Con datos, `Importar.respaldoTexto` **combina**, pero con reglas distintas según la colección (H-8-13). Las principales (tabla completa en la versión completa, §10.7):

| Colección | Si ya existe en el teléfono… |
|---|---|
| Días | **gana el teléfono** (`importarDias(sobrescribir = false)`) |
| Momentos, gastos e ingresos | se agregan los que no existan (por identificador) |
| Salud, pantalla, lugares, cartas, entorno, canciones… | **gana el respaldo**; desde el esquema 14, el sueño que corregiste a mano no lo pisa un respaldo que no lo tenía corregido (`Fusion.salud`; H-NAV-5) |
| Experimentos (desde el esquema 13) | gana el teléfono, pero se conserva la fecha de fin y las respuestas que solo trae el respaldo (`Fusion.experimento`) |
| MacroFactor | solo si en el teléfono **no hay nada** de MacroFactor |

: Tabla 10.2. Restaurar: cómo se combinan los datos

**Por qué importa.** Si restauras sobre un teléfono que tiene una versión **incompleta** de un día (solo el sueño de esa mañana), el diario completo del respaldo para ese día **no se recupera**. En cambio, para la salud, un respaldo viejo puede **pisar** datos más nuevos del teléfono. Ninguna de las dos cosas se avisa. **[Diseño propio]** (inconsistente).

**La papelera** (desde el esquema 14). Antes de borrar un registro que hiciste tú, `Repo` copia el documento a la colección `papelera`; lo que lleva más de 7 días se vacía en la tarea diaria. La papelera **no viaja** en los respaldos. Lo privado no aparece en la pantalla «Papelera»: se recupera en la de la pantalla Privado (`Papelera.esPrivado`). **[Diseño propio]**.

## 10.7 Servicios externos

La app no incluye analítica, publicidad ni reporte de errores (no hay dependencias de ese tipo en `app/build.gradle.kts`). Habla con estos servicios:

| Servicio | Qué se envía |
|---|---|
| **Google Drive** | Los archivos de respaldo (cifrados o no), solo si lo activas |
| **Open-Meteo** (clima y aire) | La **mediana de las ubicaciones del día, redondeada a 2 decimales** (`Entorno.r2`: ≈ 1.1 km en latitud) y las fechas |
| **Spotify** | Autorización con redirección a `127.0.0.1` y consultas con tu permiso |
| **TMDB, RAWG, TVmaze, Google Books, Open Library, Crossref** | El **título o DOI** que buscas |
| **Mosaicos de OpenStreetMap** | Qué zona del mapa estás viendo |
| **Health Connect, uso del teléfono, ubicación** | Nada: se leen **dentro** del teléfono |

: Tabla 10.3. Servicios externos

El documento de contexto dice que «nada pasa por servidores ajenos salvo Drive del usuario, Open-Meteo (clima) y Spotify API»; hay más, aunque los demás solo reciben títulos de obras o la zona del mapa (H-8-17). Desde 10e, el texto de privacidad de la app los nombra todos con lo que recibe cada uno (D.103), y dice que Drive recibe los respaldos completos (cifrados si pones contraseña) y que los demás no reciben tu diario, tu salud ni tu ruta. **Desde 10f** (D.116), Ajustes › Privacidad termina con «Qué sale del teléfono», una sección plegada que lista cada servicio y qué recibe; sale de la misma lista que el texto de privacidad (`Red.SERVICIOS`), así que los dos dicen lo mismo, aunque nada obliga a anotar ahí un servicio nuevo. El autor está de acuerdo con lo que se envía (H-8-17, resuelto). **[Validado]** como enfoque general: el procesamiento es local y solo salen las consultas necesarias. Las políticas de privacidad de cada servicio no se verificaron **[sin verificar]**.

## 10.8 En resumen

1. Los datos viven solo en el teléfono, sin copia automática de Android **[Validado]**; salen solo por los respaldos que tú activas y por consultas puntuales (clima con la ubicación redondeada, títulos de obras, mapas).
2. El **respaldo cifrado** usa AES-256-GCM con sal e IV nuevos en cada archivo **[Validado]**. Desde 10e, PBKDF2 usa las 600 000 iteraciones que recomienda OWASP (antes, 210 000; H-8-9), y la app avisa que las copias viejas siguen con la contraseña anterior (H-8-15); lo que más importa es una contraseña larga.
3. **Sin contraseña, todo sale en claro**, incluido el texto de las cartas selladas (H-8-5). Desde el módulo 10a, si hay datos privados, el respaldo no sale sin contraseña; solo la exportación para R, a mano, va en claro y lo advierte.
4. **El seguimiento privado** se ve, desde el módulo 10a, **solo en la pantalla Privado** y va completo a R. Desde la entrega 10c, las rachas cuentan solo los días respondidos «no» (H-8-8).
5. **Verificar** un respaldo es una buena práctica **[Validado]**; desde el esquema 13 dice solo lo que comprueba y no restaura un respaldo de un esquema más nuevo (H-8-12).
6. **Restaurar** combina con reglas distintas según la colección (H-8-13); en Drive, un respaldo mucho más pobre ya no reemplaza al bueno (H-8-14).
7. Los reportes automáticos se guardan en carpetas públicas, en modo discreto y sin la frase del mejor momento (H-8-4).
8. La entrega 10e resolvió H-8-9, H-8-11 y H-8-15, y 10e y 10f, H-8-17 (la lista de servicios, ahora también en los ajustes). En 10f, H-8-6 (logros que premian la valencia) se resolvió por decisión del autor: se quedan, con el riesgo documentado (capítulo 9). Ninguno de H-8 sigue pendiente; H-8-4, H-8-5, H-8-13 y H-8-14 están resueltos solo en parte.


# Capítulo 11. Discusión, limitaciones y trabajo futuro

> **Para qué sirve este capítulo.** Junta lo que dicen los capítulos 2 a 10 para responder tres preguntas: **qué se puede concluir y qué no** de los datos de Vestigium Mei; **qué amenazas a la validez quedan**, vistas en conjunto; y **qué falta**, en la app, en R y en este documento. No describe funciones nuevas: cada afirmación remite al capítulo donde está el detalle. Describe el código de la v92 (la v91 más la entrega 10f, que llevó al código las decisiones del autor sobre los pendientes; `SCHEMA_V = 15`); las cuentas de hallazgos son las del campo «Estado» de `hallazgos.md` al 28 de octubre de 2026, después de 10f. Los ejemplos son inventados.

## 11.1 Qué está bien resuelto

Antes de las limitaciones, conviene decir qué partes son sólidas, porque son las que justifican confiar en el resto con la cautela debida.

1. **«Cero» y «no sé» se distinguen con una sola regla.** Desde la entrega 10c, una variable vale 0 solo desde el primer registro de su fuente y en los días en que se pudo responder (`Estadistica.ceroDesde`; capítulo 2). Es la aplicación correcta de la teoría de datos faltantes (Rubin, 1976): un cero falso baja las medias y crea correlaciones por el solo calendario de adopción. **[Validado]** el principio.
2. **El ánimo se mide de varias formas que no se mezclan.** Avisos al azar dentro de tramos (muestreo estratificado), registros propios, el diario de la noche, el cierre del día y cuestionarios guardan variables distintas (capítulo 3), y desde la entrega 10c los reportes tampoco las mezclan. **[Validado]** el diseño.
3. **La lista de relaciones tiene tres protecciones a la vez**: quita la tendencia (±10 días), descuenta que los días se parecen entre sí (n efectivo) y corrige por las miles de comparaciones (Benjamini-Hochberg, q < 0.10), además de esconder las parejas obvias (capítulo 6). Desde la 10c, «Todo lo que se mueve con algo», «Cruzar dos variables» y las diferencias de medias usan la misma maquinaria. **[Diseño propio]** la combinación; cada pieza, **[Validado]**.
4. **R repite a la app.** Desde la entrega 10d, `vestigium.R` analiza el mismo panel, con las mismas variables y parejas obvias, y una prueba JVM fija que `vm_relaciones` y `vm_factores` dan los mismos números que la app con datos inventados. Con una exportación real, el autor comprobó que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). R es así un **verificador independiente** del teléfono.
5. **Los experimentos son experimentos de verdad**: el plan se sortea por pares de semanas, empieza en lunes, la hipótesis (y, desde 10f, el criterio de decisión, con una huella que avisa si cambia) queda sellada antes de empezar y el intervalo usa t de Welch con el error estándar corregido por autocorrelación. En simulación, sin efecto real y con autocorrelación de 0.3 a 0.5, da una falsa señal el 3 o 4 % de las veces, frente al 14 a 24 % del método anterior a la 10c (capítulo 8). **[Validado]** la lógica; el método es conservador.
6. **Las costuras se marcan.** Cuando una regla de medición cambió, cada día guarda con qué regla se midió (`ventana_pantalla`, `ventana_pasos`, `version_noche`), y el Apéndice D de la versión completa dice qué cambió y en qué versión.
7. **Lo privado tiene una sola regla** (`data/Privado.kt`) y una prueba que falla si algo se cuela (`PrivadoPrueba`); el respaldo se cifra con AES-256-GCM, se verifica sin restaurar y no se restaura si es de un esquema más nuevo (capítulo 10).

**El saldo de la revisión.** Los módulos 1 a 9 y la revisión de navegación anotaron **158 hallazgos** (149 de los módulos y 9 H-NAV). Según su campo «Estado», puesto al día con las entregas 10e y 10f, **126 están resueltos** (incluidos los resueltos documentándolos y uno por decisión del autor), **31 resueltos en parte** y **1 pendiente** (antes de 10f, 119, 31 y 8; antes de 10e, 97, 24 y 37). La 10f resolvió seis en el código (H-1-7, H-1-8, H-3-4, H-3-7, H-7-10 y H-7-14) y uno por decisión del autor (H-8-6, los logros que premian la valencia se quedan). Queda pendiente solo H-1-4, de gravedad media; ninguno de gravedad alta queda pendiente del todo. Los dos de gravedad alta marcados «en parte» son H-1-1 (una función que se deja de usar sigue contando 0) y H-3-1 (las lecturas del reloj, la agenda, el clima y las ciudades siguen por día de calendario, ahora declarado en cada variable). Se suman los dos de este capítulo (H-11-1 y H-11-2, resueltos documentándolos) y seis de gravedad baja o muy baja de la lectura final contra el código (H-14-1 a H-14-6), que 10e corrigió todos: en total, **166 hallazgos, 134 resueltos, 31 en parte y 1 pendiente**.

## 11.2 Qué se puede concluir y qué no

### 11.2.1 Por herramienta

El capítulo 1 distinguió preguntas descriptivas, de asociación y causales. Esta tabla dice qué afirmación permite cada herramienta.

| Herramienta | Puede decir | No puede decir | Por qué |
|---|---|---|---|
| Tus números, Totales, reportes | Cuánto, cuándo y cuántos días con dato | Que una diferencia con la semana anterior sea real | Desde 10e dicen «parecido» si la diferencia es menor que su error típico, un filtro que se queda corto (H-8-11) |
| Dinámica emocional | Qué tan variable y persistente es tu valencia del diario | Lo mismo que esos índices en la literatura | Allí se calculan con mediciones momentáneas; aquí, con un valor recordado por día |
| Las relaciones más marcadas | Qué se mueve junto **de un día a sus vecinos** | Qué causa qué; qué se mueve junto **a lo largo de semanas o meses** | Quitar la tendencia borra los efectos lentos; no hay control de confusores |
| Lo mismo, con desfase de un día | Que lo de hoy **ayuda a predecir** lo de mañana | Que lo cause | Es causalidad de Granger (1969): precedencia y predicción |
| Modelo múltiple | La asociación de un predictor con los demás que elegiste fijos | El efecto de lo que no pusiste | Confusores no medidos; supone residuos independientes |
| Antes y después | Cómo cambiaron tus días alrededor de un cambio de vida | Que el cambio lo causara | Muchas cosas cambian a la vez; hasta 56 comparaciones sin corregir |
| Experimentos personales | Que **hacer la acción, sabiendo que la haces**, cambió tu medida en ese periodo | Que el efecto sea fisiológico, que dure o que valga en otra etapa | Sin cegamiento, con potencia modesta (capítulo 8) |

: Tabla 11.1. Por herramienta

### 11.2.2 Asociación no es causa, aquí tampoco

Las protecciones de la lista atacan el **azar** y las **tendencias**, no los otros dos motivos por los que dos cosas se mueven juntas sin que una cause la otra:

- **Un tercer factor** (un *confusor*) que mueve a las dos. El más común en datos diarios es **el día de la semana**: duermes más y te sientes mejor el sábado, y la lista sin tendencia lo conserva entero, porque una ventana de 21 días contiene tres semanas exactas (H-5-6; en R, desde 10e, `sin_semana = TRUE` lo quita; versión completa, §6.5.6). Otros probables: estar enfermo, viajar, una temporada de trabajo, la estación.
- **La dirección.** ¿Dormir bien mejora el ánimo, o el buen ánimo deja dormir? El desfase de un día ayuda, pero no decide: si el ánimo es persistente, el de ayer puede causar el sueño de anoche y el ánimo de hoy a la vez.

Por eso la regla práctica del paper es: **una relación firme en la lista es una hipótesis; un experimento personal es la forma de probarla** (capítulo 8).

### 11.2.3 Qué tan lejos llega una conclusión

Aun la mejor conclusión vale **para el autor, en el periodo analizado**. A otras personas, nada: es la consecuencia directa del argumento idiográfico (Molenaar, 2004; capítulo 1). A tu futuro, solo si el proceso es aproximadamente **estacionario** (las mismas reglas, la misma vida): un cambio de trabajo, de ciudad o de pareja puede cambiar la relación entre el sueño y el ánimo, no solo sus niveles.

De aquí sale un hallazgo nuevo (**H-11-1**): **la lista de relaciones usa toda la historia**. `PantallaExplorar` pasa a `Estadistica.relaciones` el panel completo, desde el primer día. Quitar la tendencia corrige los **niveles** que cambian lento, pero no una **relación** que cambia con los años: si durante dos años el sueño y el ánimo iban muy juntos y en los últimos dos ya no, la lista muestra un promedio que no describe ninguno de los dos periodos. **[Aproximación]**: es el supuesto de estacionariedad, razonable a corto plazo y cada vez menos cuanto más larga es la historia.

## 11.3 Validez de constructo, en conjunto

Un número puede ser preciso y aun así no medir lo que su nombre dice. La **validez de constructo** es justo eso: si una medición refleja el concepto que pretende medir (Cronbach y Meehl, 1955). Messick (1995) añadió que lo que se valida no es el instrumento, sino **las interpretaciones** de sus números. La pregunta útil es: **cuando leo el nombre de una variable, ¿qué estoy autorizado a creer que midió?** Los capítulos 3 a 7 permiten agrupar las variables en cuatro familias; algunos ejemplos (la tabla entera está en la versión completa, §11.3.2):

| Familia | Variable | Lo que mide de verdad |
|---|---|---|
| Autorreporte | `valencia` | Cómo **recuerdas** el día por la noche, en una escala 1–7 con anclas propias |
| Autorreporte | `esc_who5_*`, `esc_pss4_*` | Los mismos reactivos, pero preguntando por 7 días en lugar de 2 semanas (WHO-5) o un mes (PSS-4) |
| Proxy | `aire_libre_min` | El mayor entre los minutos con ≥ 1000 lux en el sensor del teléfono y los minutos a pie, corriendo o en bici; subestima |
| Proxy | `sueno_horas` | La estimación del reloj, que tiende a sobrestimar el sueño |
| Proxy | `ruido_db` | Un nivel relativo del micrófono, no decibeles calibrados |
| Modelo | `cafeina_al_dormir_mg` | Una cuenta con vida media de 5 h (la real va de 1.5 a 9.5 h) |
| Modelo | `alcohol_estimado_max` | La fórmula de Widmark con β = 0.15 g/L/h; un orden de magnitud |
| Conteo | `per_<x>` | A quién **nombraste con @**: mezcla haber estado, pensado, soñado y escrito |
| Conteo | `palabras` | Cuánto escribiste |
| Conteo | `campana_<reto>` | Que tenías el reto **aceptado** (asignación, no cumplimiento) |

: Tabla 11.2. Validez de constructo, en conjunto

Lo importante no es que haya proxies (toda medición del mundo real lo es en algún grado), sino **leer cada relación con el nombre verdadero de sus variables**. «`per_ana` se asocia con mejor valencia» quiere decir «los días en que escribo sobre Ana con @ los recuerdo como mejores», que puede deberse a verla, a pensar en ella o a que esos días escribo más. Una relación de `palabras` o de `personas` con cualquier otra cosa es, en buena parte, una relación con **cuánto escribiste ese día**. Cronbach y Meehl (1955) proponen validar un constructo por su **red nomológica** (si mide lo que dice, debe relacionarse con otras medidas como predice la teoría); varias cosas se miden dos veces por caminos distintos, y eso permite algunas de esas comprobaciones en R (sección 11.7.3).

## 11.4 Sesgos, en conjunto

Vistos juntos, los sesgos de cada capítulo se agrupan en siete fuentes.

| Fuente | Qué produce | Marca en los datos |
|---|---|---|
| **Qué días faltan** | Los días malos se registran menos (faltante no al azar, MNAR) | `dia_dificil`, `tasa_respuesta` (desde el esquema 13), días sin diario |
| **Cuándo se anotó** | Lo anotado tarde es recuerdo, no experiencia (H-NAV-9) | `retroactivo`, `ts_creado` y `ts_editado` (desde el 14), `vm_retraso()` |
| **Costuras del instrumento** | Series partidas en tramos no del todo comparables | `ventana_pantalla`, `ventana_pasos`, `version_noche`, `schema_v`; desde 10e, el diccionario dice los parámetros vigentes, pero no su historial (H-4-11); desde 10f, desde qué versión vale cada definición (`desde_version`; H-1-8) y si el clima es de reanálisis (`clima_reanalisis`; H-3-4) |
| **Qué día es** | En una fila, unas variables cuentan la madrugada y otras no | `Diccionario.CALENDARIO`; desde 10f, cada hora se lee con la zona de entonces, y `desfase_utc_h` y `cambio_horario` marcan los días con cambio de hora (H-1-7) |
| **Confusores comunes** | Relaciones debidas al día de la semana, la estación, una enfermedad | `dia_semana`, `fin_de_semana`, `festivo`, `dia_libre`; de enfermedad, solo `ctx_enfermo` |
| **Cómo se eligió el análisis** | Más hallazgos por azar; la más fuerte exagera su tamaño | Ninguna (H-11-2) |
| **La historia completa** | Una relación promedio de etapas distintas | `vida_*` y las fechas de «Conocerte» (H-11-1) |

: Tabla 11.3. Sesgos, en conjunto

Tres observaciones. **Los sesgos no son independientes**: un día difícil es a la vez un día con menos registros, más probablemente anotado tarde y quizá de enfermedad; los tres empujan en la misma dirección: los días que quedan en el análisis son mejores que los que faltan. **Varias marcas existen solo desde hace poco** (esquemas 13 y 14), y para los años anteriores `tasa_respuesta`, `ts_creado` y `ts_editado` quedan vacíos. **La fuente 6 es la única sin ninguna marca**: no hay forma de saber, después, cuántas maneras de mirar los datos se probaron; es la razón del flujo confirmatorio de la sección 11.7.4.

## 11.5 Reactividad, en conjunto

**Reactividad** es que medir algo cambie lo que se mide (capítulo 3). Aquí tiene cuatro fuentes:

1. **Registrar.** El autoseguimiento tiende a aumentar las conductas deseables y reducir las indeseables (Korotitsch y Nelson-Gray, 1999); en el ánimo y el dolor el efecto parece pequeño (Cruise et al., 1996); poner nombre a una emoción puede bajar su intensidad (Lieberman et al., 2007).
2. **Lo que la pantalla muestra**: los valores de tus avisos al calificar el día (desde `version_noche = 2`, solo después de elegir la valencia), la franja «Tus últimos días», la hipótesis y el criterio escritos del experimento (efecto expectativa).
3. **Los premios.** Logros y campañas premian registrar, conductas y, en dos logros secretos, valores de la valencia (H-8-6; se quedan por decisión del autor). La ley de Campbell (1979) dice que un indicador que se usa para premiar se distorsiona (capítulo 9).
4. **El propósito mismo.** La app quiere que te conozcas y, con eso, que cambies. Eso es bueno para la vida y malo para la observación: los datos describen una vida que se observa a sí misma.

No se elimina; se **marca y se separa**: las campañas son variables del panel desde el esquema 13 (`campana_<reto>`, `campanas_n`); los tramos de cada cambio de interfaz están fechados (y en parte en los datos, `version_noche`); las primeras semanas de cada medición nueva se pueden analizar aparte en R, y los experimentos dan la única estimación con la reactividad **igual en las dos condiciones**. **[Aproximación]**: nada de esto cuantifica la reactividad; solo permite ver si una relación cambia cuando cambia la presión de medir.

## 11.6 Limitaciones de este paper

1. **Se escribió leyendo el código, no usándolo.** La compilación y las pruebas JVM corren en GitHub Actions, pero ninguna sesión ejecutó la app en un teléfono; lo que depende de Android quedó como suposición marcada.
2. **R se probó con datos reales en un solo punto.** Con CSV sintéticos en las sesiones de 10d y con una exportación real de la v89, que reveló que faltaban 57 variables en el panel extendido; después, el autor comprobó la coincidencia de `vm_relaciones()` con la lista. Las demás funciones (`vm_factores`, `vm_semana`, `vm_palabras`, `vm_red`…) solo se compararon con datos inventados.
3. **Hay afirmaciones sin verificar**, listadas en `referencias.md`: entre ellas, los puntos de corte del WHO-5 (≤ 28) y del UCLA-3 (≥ 6) **[sin verificar]**.
4. **Las simulaciones son simulaciones**: las cifras de falsas señales, cobertura y potencia salen de datos inventados con autocorrelación de orden 1 y efectos constantes.
5. **Muchas manos**: sesiones y modelos distintos; puede quedar alguna frase o cifra desfasada.
6. **La literatura no es una revisión sistemática.**
7. **Autor y sujeto son la misma persona**, y no hubo revisión externa del método; las sesiones de Claude funcionaron como revisión, con los límites anteriores.

## 11.7 Trabajo futuro

Son **propuestas**, no cambios: el código solo cambia con lo aprobado en una revisión como la del módulo 9. Varias se hacen en R con lo que ya se exporta.

### 11.7.1 Modelos multinivel

Los días están anidados en semanas, meses y etapas. Un **modelo multinivel** (jerárquico o de efectos mixtos) estima, por ejemplo, la relación entre el sueño y el ánimo **en cada año** y cuánto varía de un año a otro, prestando información entre años para que los que tienen pocos datos no den estimaciones extremas. Dos usos: **relaciones que cambian con el tiempo** (H-11-1), con la pendiente variando por año o por etapa de `vida_*`, por ejemplo con modelos dinámicos de ecuaciones estructurales (DSEM; Asparouhov, Hamaker y Muthén, 2018), que combinan autocorrelación, efectos variables y datos faltantes (**[Validado]** el método; **[Aproximación]** su uso con periodos de un solo sujeto como unidades); y **familias con muchos miembros** (`per_`, `lug_`, palabras), donde el modelo **encoge** los miembros con pocos datos hacia el promedio de su familia y reduce la maldición del ganador (Gelman, Hill y Yajima, 2012); es la respuesta de fondo a H-5-10.

### 11.7.2 Autocorrelación en R

Hoy `vm_relaciones` y `vm_factores` corrigen como la app, y `vm_modelo_ar1` ajusta el modelo múltiple con **errores AR(1)** (`nlme::gls` con `corAR1(form = ~ t)`), que respeta que cada día se parece al anterior. Desde la entrega 10e, además, `sin_semana = TRUE` quita el ciclo semanal en `vm_relaciones` y `vm_factores`, `vm_cruce` da Pearson con el n efectivo y Spearman (H-5-7), y `vm_experimento` hace la **prueba de aleatorización** sobre todos los planes del sorteo por pares (H-7-11; el *p* mínimo real es 2/2^(N/2) sin lavado: con 12 semanas, ≈ 0.031). Lo que falta, de lo más barato a lo más costoso:

1. **Usar lo que ya existe.** El ciclo semanal (H-5-6) se quita en la lista con `sin_semana = TRUE` y, en el modelo, se controla agregando `dia_libre` o `fin_de_semana` como predictor: `vm_modelo_ar1("valencia", c("sueno_h", "pasos", "dia_libre"))`. La estabilidad en el tiempo (H-11-1) se revisa pasando un periodo: `vm_relaciones(datos = filter(panel, fecha >= as.Date("2026-01-01")))`. Estas llamadas no se ejecutaron en la sesión que las propuso.
2. **El valor de ayer y los errores AR(1) a la vez**: hoy `vm_modelo` tiene lo primero y `vm_modelo_ar1` lo segundo.

### 11.7.3 Validar las heurísticas

Muchas reglas **[Diseño propio]** o **[Aproximación]** se pueden poner a prueba con los propios datos, porque la app mide algunas cosas dos veces o porque el usuario ya corrige algunas a mano. La más fácil y útil: comparar **las noches del teléfono** (último y primer uso) con **las del reloj**; el panel ya trae `sueno_inicio_h`, `sueno_fin_h`, `primer_uso_h` y `ultimo_uso_h`, y el resultado sería el error típico, en minutos, de la hora de dormir y despertar aproximadas. Otras: las líneas de la Red frente a los vínculos declarados en «¿A quién conoce?»; `per_<x>` frente a las presencias revisadas; la valencia del diario frente a `muestreo_valencia` y el cierre del mismo día; la ventana de ±10 días y el umbral de 0.12 frente a `vm_relaciones(ventana = 7)`, `14` o `28` (análisis de sensibilidad), y la detección de lugares (75 m, 20 min, 120 m) frente a las visitas corregidas, aunque «no estuve aquí» hoy no se guarda (versión completa, §11.7.3).

### 11.7.4 Del hallazgo a la conclusión: un flujo confirmatorio

La lista de relaciones es **exploratoria**, y no hay forma de contar después las maneras en que se miraron los datos. La salida estándar en ciencia es separar **explorar** de **confirmar** (Nosek et al., 2018). Aplicado a un solo sujeto, sin cambiar la app:

1. **Explorar** en un periodo (por ejemplo, un año) con la lista de la app y con R.
2. **Escribir antes** las pocas relaciones que se quieren confirmar, con su dirección y la variable de control. Los experimentos lo hacen dentro de la app desde 10f (sentido y diferencia mínima sellados); las relaciones de la lista, no.
3. **Confirmar** con el periodo siguiente, que no se miró, con una sola prueba por relación (o pocas, corregidas entre sí) y el modelo AR(1) de R.
4. **Experimentar** con las que sobreviven y son accionables.

Este flujo responde a la vez a las comparaciones múltiples (en el paso 3 hay pocas), a la maldición del ganador (el tamaño se estima en datos nuevos) y a la estacionariedad (si la relación desaparece en el segundo periodo, era de una etapa). Su costo es el tiempo. **[Validado]** el principio; **[Diseño propio]** su adaptación.

### 11.7.5 Lo que queda abierto y por comprobar

Después de la entrega 10f, el único hallazgo pendiente del todo es H-1-4, de gravedad media (`coerceInputValues` puede convertir un valor inválido en el de omisión sin registrarlo). La 10f resolvió H-3-4, el otro de gravedad media: cada día guarda de dónde salió el clima (`clima_reanalisis`) y los días de pronóstico se cambian por el reanálisis a los 80 días. 10e resolvió H-4-2 (cafeína y alcohol se juntan toma por toma) y dejó en parte H-5-6 (el ciclo semanal: en R se quita, en la app no), H-6-3 (la fecha del cambio de relación se puede elegir; lo anterior a la primera clasificación se proyecta) y H-NAV-9 (leer por tramo queda en cada análisis). En total, catorce de gravedad media están resueltos en parte con una parte pendiente (versión completa, §11.7.5).

Faltan además cuatro comprobaciones: app frente a R con datos reales **más allá de la lista** (`vm_factores` y `vm_red`, con `min_juntos = 2`, `min_dias = 0` y las mismas 45 personas, y `vm_experimento` con un experimento real); lo de 10f que depende del teléfono o de la red (el aviso de cambio de zona y la hora tras un viaje real, el wifi con el nombre oculto, el nombre con que MacroFactor exporta el press inclinado y el archivo de Open-Meteo); **R en la compilación automática**, porque hoy un cambio en `vestigium.R` que rompa algo solo se nota al usarlo, y las afirmaciones **[sin verificar]** que sostienen umbrales que la app muestra (WHO-5 ≤ 28, UCLA-3 ≥ 6, jet lag social de 1 hora).

## 11.8 Conclusión

Vestigium Mei intenta algo que la literatura del autoseguimiento describe como difícil: no solo juntar datos de una vida, sino **sacar de ellos conclusiones que resistan el azar, las tendencias y el deseo de encontrar algo**. Después de la revisión de los módulos 1 a 10, la parte de análisis está a la altura de esa intención en lo que se puede pedir a una app personal: distingue «cero» de «no sé», mide el ánimo de varias formas sin mezclarlas, corrige por días parecidos y por muchas comparaciones, ofrece experimentos sorteados con intervalos honestos y deja que R verifique lo que muestra el teléfono.

Lo que no puede resolver ninguna app es la naturaleza de sus datos: **una sola persona, que sabe que se mide, que cambia con los años y que elige qué mirar**. Por eso sus conclusiones son **de asociación y para ti**, salvo las de los experimentos, que son causales con las condiciones del capítulo 8. Leídas con el nombre verdadero de cada variable y como pistas que se confirman con datos nuevos o con un experimento, las relaciones del panel pueden decir cosas que la memoria no dice. Leídas como verdades, engañan con la misma precisión con la que se calcularon.

## 11.9 En resumen

1. Lo sólido: una sola regla para «cero» y «no sé», el ánimo medido de varias formas, la lista con tres protecciones, R alineado con la app, experimentos sorteados por pares con intervalos corregidos, costuras marcadas y una sola regla de privacidad. De los 158 hallazgos de los módulos 1 a 9 y la navegación, después de 10f, 126 están resueltos, 31 en parte y 1 pendiente (H-1-4, de gravedad media; ninguno alto); con los del módulo 11 y los del 14, 166: 134, 31 y 1.
2. Lo que se puede concluir depende de la herramienta: descripción, asociación de corto plazo, predicción con desfase o efecto causal en un experimento. Nada generaliza a otras personas, y solo vale para tu futuro si tu vida no cambió en lo importante.
3. La lista usa toda la historia (H-11-1) y las maneras de mirar los datos no entran en ninguna corrección (H-11-2).
4. Muchas variables miden algo distinto de su nombre: recuerdos, proxies, modelos o cuánto escribes.
5. Los sesgos se agrupan en siete fuentes que suelen empujar en la misma dirección; la única sin marca es cómo se eligió el análisis.
6. La reactividad no se elimina; se marca (campañas, tramos, experimentos).
7. El paper se escribió leyendo el código, R se comparó con datos reales solo en la lista y quedan afirmaciones sin verificar.
8. Trabajo futuro: modelos multinivel, completar la corrección AR(1) en R, validar las heurísticas con lo que se mide dos veces, un flujo que separe explorar de confirmar, resolver H-1-4 y probar en un teléfono lo que 10f dejó sin comprobar.


# Apéndice B. Fórmulas

_Las fórmulas esenciales del núcleo estadístico (capítulo 6), con la función de origen. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`). Se conservan las etiquetas B.n de la versión completa, que trae además la media móvil (B.17), las cifras de «Tus números» (B.19), la ventana de los cuestionarios (B.21) y dos fórmulas de la entrega 10f fuera del núcleo estadístico: la fuerza de referencia con Epley (B.23) y la duración de un día con cambio de hora (B.24). Notación: n = número de días (o semanas) con ambos datos; x̄ = promedio; s² = varianza muestral con n − 1; atanh y tanh son la tangente hiperbólica inversa y directa._

### B.1 Correlación de Pearson — `Estadistica.pearson`, función interna `r` de `Estadistica.relaciones`

$$r=\frac{\sum_{i=1}^{n}(x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_{i=1}^{n}(x_i-\bar x)^2\ \sum_{i=1}^{n}(y_i-\bar y)^2}}\qquad\qquad (B.1)$$

Condiciones: n ≥ 3 (`pearson`); si alguna suma de cuadrados es 0, devuelve `null`.

### B.2 Correlación de Spearman — `Estadistica.spearman`, `Estadistica.rangos`

$$\rho = r\big(\operatorname{rango}(x),\ \operatorname{rango}(y)\big)\qquad\qquad (B.2)$$

Rangos de 1 a n; a un grupo de valores empatados que ocupan los lugares i a j (base 0) se le asigna (i + j)/2 + 1.

### B.3 Resto sin tendencia — `Estadistica.series` (privada); `quitar` en `vm_relaciones`

$$\text{resto}_i = x_i-\frac{1}{|W_i|}\sum_{j\in W_i}x_j,\qquad W_i=\{\,j:\ |d_j-d_i|\le V,\ x_j\ \text{existe}\,\}\qquad\qquad (B.3)$$

d = número de día del calendario (o de semana × 7). V = 10 días (por día) o 35 días (por semana). El propio día está en W_i. Solo si |W_i| ≥ 3; si no, el resto falta.

**Variable lenta:** var(x) > 0, al menos 14 restos (8 por semana) y var(resto)/var(x) < 0.12.

### B.4 Autocorrelación de orden 1 — `Estadistica.autocorr` (privada), `Estadistica.ar1`; `ac1` en `vm_relaciones`

$$r_1 = r\big(\{x_i\},\{x_{i+1}\}\big)\quad\text{sobre los pares con } d_{i+1}-d_i=\text{paso}\qquad\qquad (B.4)$$

paso = 1 (por día) o 7 (por semana). Si hay menos de 10 pares (o la serie no varía), desde 10e r₁ = 0.3 (`Estadistica.AR1_SIN_DATO`, D.95; **[Diseño propio]**); hasta 10e, 0. Antes de usarse se recorta a [−0.5, 0.97]. `Estadistica.ar1(serie, paso)` es la misma autocorrelación sobre una serie con fechas: devuelve `null` con menos de 10 pares; las correlaciones toman entonces 0.3 desde 10e (antes, 0) y la diferencia de medias (B.11a), 0.

### B.5 Número efectivo de días — `Estadistica.relaciones`, `Estadistica.correlacionEf`; en R, `vm_relaciones` y `vm_factores`

$$n_{Ef}=\operatorname{acotar}\Big(n\,\frac{1-r_a r_b}{1+r_a r_b},\ \min(4,n),\ n\Big)\qquad\qquad (B.5)$$

r_a, r_b: autocorrelaciones (B.4) de cada variable, sobre restos o valores según el modo; la que no se puede estimar vale 0.3 desde 10e (antes, 0), en la app y en R (`.vm_ar1_sin_dato`, también en `vm_cruce` y `vm_presion`). Si n_Ef ≤ 3, la pareja no se evalúa. Referencia: Bretherton et al. (1999); Pyper y Peterman (1998).

### B.6 Transformación de Fisher — `Estadistica.ic95`, `Estadistica.relaciones`, `Estadistica.correlacionEf`

$$z=\operatorname{atanh}(r)=\tfrac12\ln\frac{1+r}{1-r},\qquad EE_z=\frac{1}{\sqrt{N-3}}\qquad\qquad (B.6)$$

N = n (en `ic95`) o n_Ef (en `relaciones` y `correlacionEf`). r se recorta a ±0.9999.

### B.7 Intervalo de 95 % de una correlación

$$IC_{95}=\big[\tanh(z-1.96\,EE_z),\ \tanh(z+1.96\,EE_z)\big]\qquad\qquad (B.7)$$

`ic95` exige n ≥ 5.

### B.8 Valor p bilateral — `Analisis.pBilateral`, `Analisis.phi`

$$p=2\,\big(1-\Phi(|z|/EE_z)\big),\qquad \Phi(u)=\tfrac12\big(1+\operatorname{erf}(u/\sqrt2)\big)\qquad\qquad (B.8)$$

erf se calcula con la aproximación 7.1.26 de Abramowitz y Stegun (1964), con error absoluto ≤ 1.5 × 10⁻⁷ (coeficientes en la versión completa, §B.8). El resultado se recorta a [0, 1].

### B.9 Benjamini-Hochberg — `Analisis.bh`; `p.adjust(p, "BH")` en R

Con los m valores p ordenados p₍₁₎ ≤ … ≤ p₍ₘ₎:

$$q_{(k)}=\min_{j\ge k}\ \min\!\Big(1,\ \frac{m}{j}\,p_{(j)}\Big)\qquad\qquad (B.9)$$

Criterio «se sostiene»: q < 0.10 (lista de relaciones, factores, R); q < 0.05 y n ≥ 30 en «Tu año». En `relaciones`, `q` se calcula solo entre parejas no obvias y `qConObvias` entre todas. La lista se ordena, de mayor a menor, por el extremo del intervalo más cercano al cero (lo si lo > 0; −hi si hi < 0; 0 si el intervalo incluye el 0), y luego por |r|.

### B.10 Panel semanal — `Estadistica.semanal`

$$\bar x_{s}=\frac{1}{|D_s|}\sum_{f\in D_s}x_f,\qquad D_s=\{\text{días de la semana } s \text{ (lunes a domingo) con dato}\},\ |D_s|\ge 3\qquad\qquad (B.10)$$

Para una variable 0/1 es la proporción de días con 1.

### B.11 Diferencia de medias — `Estadistica.diferenciaCompleta`; `.vm_dif_dias` en R

Con n_a, n_b ≥ 3 y autocorrelaciones ρ_a, ρ_b acotadas a [0, 0.9] (0 si no se dan):

$$d=\bar a-\bar b,\qquad S_a=\frac{s_a^2}{n_a}\cdot\frac{1+\rho_a}{1-\rho_a},\qquad S_b=\frac{s_b^2}{n_b}\cdot\frac{1+\rho_b}{1-\rho_b},\qquad EE=\sqrt{S_a+S_b}\qquad\qquad (B.11)$$

$$n^{*}_a=\max\!\Big(2,\ n_a\frac{1-\rho_a}{1+\rho_a}\Big),\qquad gl=\operatorname{acotar}\Big(\frac{(S_a+S_b)^2}{S_a^2/(n^{*}_a-1)+S_b^2/(n^{*}_b-1)},\ 1,\ n^{*}_a+n^{*}_b-2\Big)\qquad\qquad (B.11·2)$$

$$IC_{95}=d\pm t_{0.975;\,gl}\,EE,\qquad p=p_T(d/EE,\ gl)\qquad\qquad (B.11·3)$$

Con ρ = 0 es la t de Welch (1947). Si EE = 0, el intervalo es el punto d.

### B.11a Autocorrelación de cada grupo — `Estadistica.ar1Grupos`, `diferenciaEnDias`

$$\tilde\rho(S)=\hat\rho_1(S)+\frac{1+4\,\hat\rho_1(S)}{|S|}\qquad\qquad (B.11a)$$

ρ̂₁ es B.4 sobre días seguidos (`ar1`) y |S| el número de días de la serie. ρ_a = ρ̃(a) si a tiene 10 pares de días seguidos; si no, ρ̃ de los dos grupos juntos, cada uno centrado en su media; si tampoco, 0. Después se acota a [0, 0.9] en B.11. La corrección se atribuye a Marriott y Pope (1954) [sin verificar en el original]; se validó por simulación.

**Día de la semana** (`Analisis.porDiaSemana`), con n ≥ 5: x̄ ± t₀.₉₇₅;ₙ₋₁ · s/√n. **Cambio reciente** (`Analisis.cambio`): a = días con hoy − 30 < fecha ≤ hoy; b = días con hoy − 60 < fecha ≤ hoy − 30.

### B.12 Inestabilidad (MSSD) — `Analisis.dinamica`

$$\text{MSSD}=\frac{1}{|P|}\sum_{(i,i+1)\in P}(x_{i+1}-x_i)^2,\qquad P=\{\text{pares de días consecutivos de calendario con dato}\},\ |P|\ge 3\qquad\qquad (B.12)$$

### B.13 Inercia — `Analisis.dinamica`

$$\text{inercia}=r\big(\{x_i\},\{x_{i+1}\}\big),\quad (i,i+1)\in P,\ |P|\ge 10\qquad\qquad (B.13)$$

### B.14 z robusto — `Analisis.atipicos`

$$z_i=\frac{x_i-M}{1.4826\cdot \mathrm{MAD}},\qquad M=x_{(\lfloor n/2\rfloor)},\quad \mathrm{MAD}=|x-M|_{(\lfloor n/2\rfloor)}\qquad\qquad (B.14)$$

(posición ⌊n/2⌋ en base 0 de los datos ordenados: para n par, el central superior). Requiere n ≥ 20 y MAD > 0. Atípico si |z| ≥ 3 (2.8 en «Tu año»). En R, la mediana se promedia para n par.

### B.14a Distribución t de Student — `Estadistica.pT`, `Estadistica.cuantilT`

p de dos colas de un valor t con gl grados de libertad (gl real > 0, acotado a 10⁶):

$$p_T(t,gl)=I_{\,gl/(gl+t^2)}\Big(\frac{gl}{2},\ \frac12\Big)\qquad\qquad (B.14a)$$

I_x(a, b) es la beta incompleta regularizada, calculada con la fracción continua de Lentz y ln Γ con la aproximación de Lanczos (g = 7). El cuantil t_p (p > 0.5) es el t ≥ 0 con p_T(t, gl) = 2(1 − p), buscado por bisección (100 pasos); para p < 0.5, t_p = −t₁₋ₚ. Comprobado: t₀.₉₇₅ = 12.706 (gl 1), 4.303 (2), 2.571 (5), 2.042 (30).

### B.15 Correlación mínima detectable (potencia)

$$|r_{\min}|=\tanh\!\Big(\frac{z_{1-\alpha/2}+z_{1-\beta}}{\sqrt{n_{Ef}-3}}\Big)\qquad\qquad (B.15)$$

Con α = 0.05 y potencia 1 − β = 0.80: z = 1.96 + 0.84. No está en el código; sirve para interpretar los umbrales de 14, 30 y 90 días.

### B.16 Modelo múltiple — `Analisis.modelo`

Con las k columnas de predictores X y el objetivo y, todos estandarizados (media 0, DE 1) en los n días completos (n ≥ k + 10):

$$\hat\beta=(X^\top X)^{-1}X^\top y,\qquad s^2=\frac{\sum (y-X\hat\beta)^2}{n-k-1},\qquad EE(\hat\beta_j)=\sqrt{s^2\,[(X^\top X)^{-1}]_{jj}}\qquad\qquad (B.16)$$

$$IC_{95}(\beta_j)=\hat\beta_j\pm t_{0.975;\,n-k-1}\,EE(\hat\beta_j),\qquad R^2=1-\frac{\sum (y-X\hat\beta)^2}{\sum y^2},\qquad R^2_{aj}=1-(1-R^2)\frac{n-1}{n-k-1}\qquad\qquad (B.16·2)$$

La pantalla muestra R²_aj, con 0 como mínimo. Sin intercepto: con todo centrado valdría 0. Los grados n − k − 1 son los que da `lm` con intercepto en `vm_modelo`, así que los β y sus intervalos coinciden. Inversa por Gauss-Jordan con pivoteo parcial; si |pivote| < 10⁻¹⁰, no hay modelo. Con `conAyer`, se agrega como predictor el objetivo del día de calendario anterior.

### B.18 Palabras — `Analisis.palabras`

Para cada palabra w (≥ 3 letras, fuera de `Analisis.VACIAS`: 167 escritas, 146 distintas sin acentos, que es como se comparan) presente en al menos 5 días con nota y valencia:

$$\text{dif}_w=\overline{\text{valencia}}_{\{\text{días con } w\}}-\overline{\text{valencia}}_{\{\text{días con nota y valencia}\}}\qquad\qquad (B.18)$$

### B.20 Cero o faltante — `Estadistica.ceroDesde`

$$\text{ceroDesde}(F, B)=\{\,f\in B:\ f\ge\min F\,\}\qquad\qquad (B.20)$$

F = fechas de la fuente (gastos, impulsos, escalada, entrenamientos, sesiones del reloj); B = días en que la sección se pudo responder (días con diario; para el ejercicio, días con datos del reloj). En esos días «no hubo» = 0; en los demás, faltante. Las variables sociales usan B = días sociales (diario escrito o momento no privado con @; `Dinamicas.diasSociales`).

### B.22 Criterio sellado de un experimento — `Experimentos.criterio` (desde 10f); `criterio` en `vm_experimento`

Con la diferencia d y su intervalo [lo, hi] de B.11, el sentido sellado s (+1 sube, −1 baja) y el umbral u ≥ 0 (0 si no se puso):

$$\text{se cumple}\iff d\cdot s\ \ge\ u\ \ \text{y}\ \ \begin{cases}\text{lo}>0&\text{si } s=+1\\ \text{hi}<0&\text{si } s=-1\end{cases}\qquad\qquad (B.22)$$

Sin sentido, sin diferencia o con el plan en curso, no hay veredicto. Lo sellado lleva una huella SHA-256 (`Experimentos.huella`) que delata cualquier cambio posterior. **[Diseño propio]** la regla; **[Validado]** SHA-256 como función hash criptográfica estándar.


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

Deci, E. L., Koestner, R., & Ryan, R. M. (1999). A meta-analytic review of experiments examining the effects of extrinsic rewards on intrinsic motivation. *Psychological Bulletin, 125*(6), 627–668. https://doi.org/10.1037/0033-2909.125.6.627

Deci, E. L., & Ryan, R. M. (2000). The "what" and "why" of goal pursuits: Human needs and the self-determination of behavior. *Psychological Inquiry, 11*(4), 227–268. https://doi.org/10.1207/S15327965PLI1104_01

Dejonckheere, E., Mestdagh, M., Houben, M., Rutten, I., Sels, L., Kuppens, P., & Tuerlinckx, F. (2019). Complex affect dynamics add limited information to the prediction of psychological well-being. *Nature Human Behaviour, 3*(5), 478–491. https://doi.org/10.1038/s41562-019-0555-0 **[sin verificar: autores después del tercero]**

Deterding, S., Dixon, D., Khaled, R., & Nacke, L. (2011). From game design elements to gamefulness: Defining "gamification". En *Proceedings of the 15th International Academic MindTrek Conference* (pp. 9–15). ACM. https://doi.org/10.1145/2181037.2181040

Domhoff, G. W. (2003). *The scientific study of dreams: Neural networks, cognitive development, and content analysis*. American Psychological Association. **[sin verificar: subtítulo y ficha editorial]**

Dworkin, M. J. (2007). *Recommendation for block cipher modes of operation: Galois/Counter Mode (GCM) and GMAC* (NIST Special Publication 800-38D). National Institute of Standards and Technology. https://doi.org/10.6028/NIST.SP.800-38D

Eagle, N., Pentland, A., & Lazer, D. (2009). Inferring friendship network structure by using mobile phone data. *Proceedings of the National Academy of Sciences, 106*(36), 15274–15278. https://doi.org/10.1073/pnas.0900282106

Epstein, D. A., Ping, A., Fogarty, J., & Munson, S. A. (2015). A lived informatics model of personal informatics. En *Proceedings of the 2015 ACM International Joint Conference on Pervasive and Ubiquitous Computing* (pp. 731–742). ACM. https://doi.org/10.1145/2750858.2804250

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

Leys, C., Ley, C., Klein, O., Bernard, P., & Licata, L. (2013). Detecting outliers: Do not use standard deviation around the mean, use absolute deviation around the median. *Journal of Experimental Social Psychology, 49*(4), 764–766. https://doi.org/10.1016/j.jesp.2013.03.013

Li, I., Dey, A., & Forlizzi, J. (2010). A stage-based model of personal informatics systems. En *Proceedings of the SIGCHI Conference on Human Factors in Computing Systems* (pp. 557–566). ACM. https://doi.org/10.1145/1753326.1753409

Lieberman, M. D., Eisenberger, N. I., Crockett, M. J., Tom, S. M., Pfeifer, J. H., & Way, B. M. (2007). Putting feelings into words: Affect labeling disrupts amygdala activity in response to affective stimuli. *Psychological Science, 18*(5), 421–428. https://doi.org/10.1111/j.1467-9280.2007.01916.x

Little, R. J. A., & Rubin, D. B. (2019). *Statistical analysis with missing data* (3.ª ed.). Wiley. https://doi.org/10.1002/9781119482260

Marriott, F. H. C., & Pope, J. A. (1954). Bias in the estimation of autocorrelations. *Biometrika, 41*(3–4), 390–402. https://doi.org/10.1093/biomet/41.3-4.390

Meredith, W. (1993). Measurement invariance, factor analysis and factorial invariance. *Psychometrika, 58*(4), 525–543. https://doi.org/10.1007/BF02294825

Messick, S. (1995). Validity of psychological assessment: Validation of inferences from persons' responses and performances as scientific inquiry into score meaning. *American Psychologist, 50*(9), 741–749. https://doi.org/10.1037/0003-066X.50.9.741

Mohr, D. C., Zhang, M., & Schueller, S. M. (2017). Personal sensing: Understanding mental health using ubiquitous sensors and machine learning. *Annual Review of Clinical Psychology, 13*, 23–47. https://doi.org/10.1146/annurev-clinpsy-032816-044949

Molenaar, P. C. M. (2004). A manifesto on psychology as idiographic science: Bringing the person back into scientific psychology, this time forever. *Measurement: Interdisciplinary Research and Perspectives, 2*(4), 201–218. https://doi.org/10.1207/s15366359mea0204_1

Nehlig, A. (2018). Interindividual differences in caffeine metabolism and factors driving caffeine consumption. *Pharmacological Reviews, 70*(2), 384–411. https://doi.org/10.1124/pr.117.014407

Nosek, B. A., Ebersole, C. R., DeHaven, A. C., & Mellor, D. T. (2018). The preregistration revolution. *Proceedings of the National Academy of Sciences, 115*(11), 2600–2606. https://doi.org/10.1073/pnas.1708274114

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
