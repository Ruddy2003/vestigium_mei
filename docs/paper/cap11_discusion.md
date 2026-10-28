# Capítulo 11. Discusión, limitaciones y trabajo futuro

> **Para qué sirve este capítulo.** Junta lo que dicen los capítulos 2 a 10 para responder tres preguntas: **qué se puede concluir y qué no** de los datos de Vestigium Mei; **qué amenazas a la validez quedan**, vistas en conjunto y no capítulo por capítulo; y **qué falta**, tanto en la app y en R como en este propio documento. No describe funciones nuevas: cada afirmación remite al capítulo donde está el detalle, con el nombre de la función de origen.
>
> **Qué versión describe.** La v90 (commit `8818d5a`, `SCHEMA_V = 14`), después de las entregas 10a a 10d del módulo 10 y de la revisión de navegación. Las cuentas de hallazgos son las del campo «Estado» de `hallazgos.md` al 28 de octubre de 2026.
>
> **Cómo leerlo.** Igual que los demás capítulos. Las etiquetas **[Validado]**, **[Aproximación]** y **[Diseño propio]** se explican en el capítulo 1 (sección 1.8). Los ejemplos son inventados.

---

## 11.1 Qué está bien resuelto

Antes de las limitaciones, conviene decir qué partes del sistema son sólidas, porque son las que justifican confiar en el resto con la cautela debida.

1. **«Cero» y «no sé» se distinguen con una sola regla.** Desde la entrega 10c, una variable vale 0 solo desde el primer registro de su fuente y en los días en que se pudo responder (`Estadistica.ceroDesde`; capítulo 2, sección 2.6). Es la aplicación correcta de la teoría de datos faltantes (Rubin, 1976): un cero falso baja las medias y crea correlaciones por el solo calendario de adopción. **[Validado]** el principio.
2. **El ánimo se mide de varias formas que no se mezclan.** Avisos al azar dentro de tramos (muestreo estratificado), registros propios, el diario de la noche, el cierre del día y cuestionarios guardan variables distintas (capítulo 3). La app no las trata como intercambiables, y desde la entrega 10c los reportes tampoco (capítulo 9, sección 9.2). **[Validado]** el diseño.
3. **La lista de relaciones tiene tres protecciones a la vez**: quita la tendencia (±10 días), descuenta que los días se parecen entre sí (n efectivo) y corrige por las miles de comparaciones (Benjamini-Hochberg, q < 0.10), además de esconder las parejas obvias (capítulo 6). Desde la entrega 10c, «Todo lo que se mueve con algo», «Cruzar dos variables» y las diferencias de medias usan la misma maquinaria: t de Student y la corrección por autocorrelación. **[Diseño propio]** la combinación; cada pieza, **[Validado]**.
4. **R repite a la app.** Desde la entrega 10d, `vestigium.R` analiza el mismo panel, con las mismas variables y las mismas parejas obvias, y una prueba JVM fija que `vm_relaciones` y `vm_factores` dan los mismos números que la app con datos inventados (capítulo 6, sección 6.22; Apéndice C). Con datos reales, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Eso convierte a R en un **verificador independiente** de lo que muestra el teléfono.
5. **Los experimentos son experimentos de verdad**: el plan se sortea por pares de semanas, empieza en lunes (cada condición tiene los mismos días de la semana), la hipótesis queda sellada antes de empezar, y el intervalo usa t de Welch con el error estándar corregido por autocorrelación. En simulación, sin efecto real y con días que se parecen a sus vecinos (autocorrelación de 0.3 a 0.5), da una falsa señal el 3 o 4 % de las veces, frente al 14 a 24 % del método de antes de la entrega 10c (capítulo 8, sección 8.6.3). **[Validado]** la lógica; el método es conservador.
6. **Las costuras se marcan.** Cuando una regla de medición cambió, cada día guarda con qué regla se midió (`ventana_pantalla`, `ventana_pasos`, `version_noche`) y el Apéndice D dice qué cambió y en qué versión.
7. **Lo privado tiene una sola regla** (`data/Privado.kt`) y una prueba que falla si algo se cuela (`PrivadoPrueba`); el respaldo se cifra con AES-256-GCM, se verifica sin restaurar y no se restaura si es de un esquema más nuevo (capítulo 10).

**El saldo de la revisión.** Los módulos 1 a 9 y la revisión de navegación anotaron **158 hallazgos** en `hallazgos.md` (149 de los módulos y 9 H-NAV). Según su campo «Estado», revisado en el módulo 14, **97 están resueltos**, **24 resueltos en parte** y **37 abiertos**. De los abiertos, 31 son de gravedad baja (uno, baja a media) y 6 de gravedad media; ninguno de gravedad alta queda abierto del todo. Los dos de gravedad alta marcados «en parte» son H-1-1 (una sola regla para «cero» y «no sé», pero una función que se deja de usar sigue contando 0) y H-3-1 (las lecturas del reloj, la agenda, el clima y las ciudades siguen por día de calendario, ahora declarado en cada variable). La sección 11.7.5 lista los de gravedad media que siguen abiertos. A ellos se suman los dos de este capítulo (H-11-1 y H-11-2, que se documentan, no se corrigen) y seis de gravedad baja o muy baja que encontró la lectura final contra el código (H-14-1 a H-14-6; por ejemplo, que «el día más raro» de un año pasado sale incompleto, o que R descuenta menos navegadores que la app).

---

## 11.2 Qué se puede concluir y qué no

### 11.2.1 Por tipo de pregunta

El capítulo 1 (sección 1.4.5) distinguió tres tipos de pregunta. Esta tabla dice, para cada herramienta, qué afirmación permite y cuál no.

| Herramienta | Puede decir | No puede decir | Por qué |
|---|---|---|---|
| Tus números, Totales, reportes | Cuánto, cuándo y cuántos días con dato | Que una diferencia con la semana anterior sea real | Las comparaciones con el periodo anterior no tienen noción de azar (H-8-11) |
| Dinámica emocional | Qué tan variable y persistente es tu valencia del diario | Lo mismo que esos índices en la literatura | Allí se calculan con mediciones momentáneas varias veces al día; aquí, con un valor recordado por día (capítulo 6, sección 6.16.3) |
| Las relaciones más marcadas | Qué se mueve junto **de un día a sus vecinos**, descontando tendencias, días parecidos y muchas comparaciones | Qué causa qué; qué se mueve junto **a lo largo de semanas o meses** | Quitar la tendencia borra los efectos lentos (capítulo 6, sección 6.5.6); no hay control de confusores |
| Lo mismo, con desfase de un día | Que lo de hoy **ayuda a predecir** lo de mañana | Que lo cause | Es causalidad en el sentido de Granger (1969): precedencia y predicción. Un tercer factor persistente produce lo mismo |
| Modelo múltiple (con el valor de ayer) | La asociación de un predictor manteniendo fijos los demás que elegiste | El efecto de lo que no pusiste en el modelo | Los confusores no medidos siguen ahí; el error estándar supone residuos independientes (capítulo 6, sección 6.14.5) |
| Antes y después | Cómo cambiaron tus días alrededor de un cambio de vida | Que el cambio de vida lo causara | Es una serie de tiempo interrumpida: muchas cosas cambian a la vez, y hasta 56 comparaciones sin corregir (capítulo 8, sección 8.11) |
| Experimentos personales | Que **hacer la acción, sabiendo que la haces**, cambió tu medida en ese periodo | Que el efecto sea fisiológico; que dure; que valga en otra etapa de tu vida | Sin cegamiento, con potencia modesta y con un análisis según lo asignado (capítulo 8, secciones 8.10 y 8.12) |

### 11.2.2 Asociación no es causa, aquí tampoco

Las protecciones de la lista (sección 11.1) atacan el **azar** y las **tendencias**. No atacan los otros dos motivos por los que dos cosas se mueven juntas sin que una cause la otra:

- **Un tercer factor** (un *confusor*) que mueve a las dos. El más común en datos diarios es **el día de la semana**: duermes más y te sientes mejor el sábado, y la lista sin tendencia lo conserva entero, porque una ventana de 21 días contiene tres semanas exactas (H-5-6; capítulo 6, sección 6.5.6). Otros confusores probables: estar enfermo, viajar, una temporada de trabajo, la estación.
- **La dirección.** ¿Dormir bien mejora el ánimo, o el buen ánimo deja dormir? El desfase de un día ayuda, pero no decide: si el ánimo es persistente, el ánimo de ayer puede causar el sueño de anoche y el ánimo de hoy a la vez.

Por eso la regla práctica del paper es: **una relación firme en la lista es una hipótesis; un experimento personal es la forma de probarla** (capítulo 8).

### 11.2.3 Qué tan lejos llega una conclusión

Aun la mejor conclusión de la app vale **para el autor, en el periodo analizado**. Dos límites:

- **A otras personas, nada.** Es la consecuencia directa del argumento idiográfico (Molenaar, 2004; capítulo 1, sección 1.4): la app no puede decir nada sobre cómo funcionan otros.
- **A tu futuro, solo si nada importante cambió.** Una conclusión sobre tus días de este año vale para los del próximo si el proceso es aproximadamente **estacionario**: las mismas reglas, la misma vida. Un cambio de trabajo, de ciudad o de pareja puede cambiar la relación entre el sueño y el ánimo, no solo sus niveles. Un experimento N-de-1 vale para la persona y el periodo en que se hizo (capítulo 8, sección 8.12).

Este segundo límite tiene una consecuencia concreta, que el módulo 11 anota como hallazgo nuevo (**H-11-1**): **la lista de relaciones usa toda la historia**. `PantallaExplorar` pasa a `Estadistica.relaciones` el panel completo, desde el primer día. Quitar la tendencia corrige los **niveles** que cambian lento, pero no una **relación** que cambia con los años: si durante dos años el sueño y el ánimo iban muy juntos y en los últimos dos ya no, la lista muestra un promedio que no describe ninguno de los dos periodos. En un registro de una semana esto no importa; en uno de diez años, sí. **[Aproximación]**: es el supuesto de estacionariedad, razonable a corto plazo y cada vez menos cuanto más larga es la historia.

---

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

**b) Proxies: medidas indirectas de otra cosa.** Miden algo cercano al nombre, casi siempre por debajo.

| Variable | Lo que el nombre sugiere | Lo que mide de verdad | Capítulo |
|---|---|---|---|
| `aire_libre_min` | Minutos al aire libre | El mayor entre los minutos con ≥ 1000 lux en el sensor del teléfono y los minutos a pie, corriendo o en bici; subestima | 4.4.5, 5.8 |
| `pantalla_min` | Tiempo con el teléfono | Minutos de apps en primer plano, sin el lanzador | 4.4.1 |
| `distancia_km` | Kilómetros recorridos | Suma de tramos en línea recta entre puntos de ubicación con filtros; cota inferior | 4.4.2 |
| `ruido_db` | Ruido del ambiente | Un nivel relativo del micrófono, no decibeles calibrados | 4.4.6 |
| `sueno_horas` | Horas dormidas | La estimación del reloj, que tiende a sobrestimar el sueño porque detecta mal la vigilia | 4.5.1 |
| Noches del teléfono (Ritmo) | Cuándo dormiste | Último uso del teléfono en la noche y primer uso en la mañana | 5.7.3 |

**c) Modelos con parámetros de población.** No son mediciones de tu cuerpo, sino cuentas.

| Variable | Lo que mide de verdad | Capítulo |
|---|---|---|
| `cafeina_al_dormir_mg` | Una combinación de cuánta cafeína tomaste y a qué hora, con una vida media de 5 h (la real va de 1.5 a 9.5 h) | 5.3 |
| `alcohol_estimado_max`, `alcohol_al_dormir` | La fórmula de Widmark con β = 0.15 g/L/h y la *r* de Watson o fija; un orden de magnitud | 5.4 |
| `creatina_saturacion_pct`, `supl_*_nivel` | La reserva de un modelo, no una concentración | 5.6 |

**d) Conteos de lo que escribes.** Miden tu escritura tanto como tu vida.

| Variable | Lo que el nombre sugiere | Lo que mide de verdad | Capítulo |
|---|---|---|---|
| `personas`, `per_<x>` | A quién viste | A quién **nombraste con @** (en `per_`, también en sueños, cartas y momentos); `per_` mezcla haber estado, pensado, soñado y escrito | 7.7 |
| `vi_*`, `personas_vistas` | Con quién estuviste | Con quién marcaste «estuve», que es el estado por omisión de cualquier mención en el diario | 7.6, 7.7 |
| `cercania_media` | Qué tan cerca te sentiste ese día | La cercanía guardada de las personas que nombraste; cambia solo cuando la editas | 7.5.2 |
| `palabras` | — | Cuánto escribiste; entra en muchas parejas porque los días con más texto tienen más de todo lo que se cuenta en el texto | 6.15.5, 8.11.3 |
| Líneas de la Red y la Constelación | Quién se conoce | Quiénes nombraste el mismo día, al menos 2 días | 7.8 |
| `campana_<reto>` | Que hiciste el reto | Que lo tenías **aceptado** (asignación, no cumplimiento) | 9.5 |

### 11.3.3 Qué implica

Lo importante no es que haya proxies (toda medición del mundo real lo es en algún grado), sino **leer cada relación con el nombre verdadero de sus variables**. «`per_ana` se asocia con mejor valencia» quiere decir «los días en que escribo sobre Ana con @ los recuerdo como mejores», que puede deberse a verla, a pensar en ella o a que esos días escribo más. Del mismo modo, una relación de `palabras` o de `personas` con cualquier otra cosa es, en buena parte, una relación con **cuánto escribiste ese día**.

Cronbach y Meehl (1955) proponen validar un constructo por su **red nomológica**: si una medición mide lo que dice, debe relacionarse con otras medidas como predice la teoría. Los datos de la app permiten algunas de esas comprobaciones en R, porque varias cosas se miden dos veces por caminos distintos (sección 11.7.3).

---

## 11.4 Sesgos, en conjunto

Cada capítulo describió sus sesgos. Vistos juntos, se agrupan en siete fuentes. Para cada una, la tabla dice qué marca existe en los datos para detectarla.

| Fuente | Qué produce | Marca en los datos | Capítulo |
|---|---|---|---|
| **Qué días faltan** | Los días malos se registran menos: las medias suben y las correlaciones se calculan sobre «los días con ganas» (faltante no al azar, MNAR) | `dia_dificil` (registro de un toque), `tasa_respuesta` (desde el esquema 13), días sin diario | 3.15 |
| **Cuándo se anotó** | Lo anotado tarde es recuerdo, no experiencia; desde la revisión de navegación hay más datos anotados tarde («Ponerte al día», tomas de ayer, «＋ agregar a este día») | `retroactivo` (por la hora de la valencia desde el esquema 13), `ts_creado` y `ts_editado` (desde el 14), `vm_retraso()` en R | 3.7.4, 3.15.3; H-NAV-9 |
| **Costuras del instrumento** | Series partidas en tramos que no son comparables del todo | `ventana_pantalla`, `ventana_pasos`, `version_noche`, `schema_v`; no la vida media de la cafeína ni las definiciones del diccionario (H-4-11, H-1-8) | 2.3.2, 3.7.1; Ap. D |
| **Qué día es** | En una misma fila, unas variables cuentan la madrugada y otras no | El diccionario marca cada variable por día de calendario (`Diccionario.CALENDARIO`); las horas de eventos viejos se leen con la zona actual del teléfono (H-1-7) | 2.3, 4.3 |
| **Confusores comunes** | Relaciones que se deben al día de la semana, la estación, una enfermedad | `dia_semana`, `fin_de_semana`, `festivo`, `dia_libre` en el panel exportado; no hay marca de enfermedad salvo el contexto `ctx_enfermo` | 6.5.6; H-5-6 |
| **Cómo se eligió el análisis** | Más hallazgos por azar de los que dice cada prueba; la relación más fuerte exagera su tamaño | Ninguna: la app no registra qué modos se miraron | 1.6.3; H-11-2 |
| **La historia completa** | Una relación promedio de etapas de vida distintas | `vida_*` y las fechas de «Conocerte»; en R se puede analizar por periodo (sección 11.7.2) | 11.2.3; H-11-1 |

Tres observaciones sobre la tabla:

1. **Los sesgos no son independientes.** Un día difícil es a la vez un día con menos registros (fuente 1), más probablemente anotado tarde (fuente 2) y quizá un día de enfermedad (fuente 5). Los tres empujan en la misma dirección: los días que quedan en el análisis son mejores que los que faltan.
2. **Varias marcas existen solo desde hace poco** (esquemas 13 y 14). Para los años anteriores no se pueden reconstruir: `tasa_respuesta`, `ts_creado` y `ts_editado` quedan vacíos (Apéndice D).
3. **La fuente 6 es la única sin ninguna marca.** No hay forma de saber, después, cuántas maneras de mirar los datos se probaron antes de encontrar algo. Es la razón del flujo confirmatorio de la sección 11.7.4.

---

## 11.5 Reactividad, en conjunto

**Reactividad** es que medir algo cambie lo que se mide (capítulo 3, sección 3.14). En esta app tiene cuatro fuentes, y la cuarta es buscada a propósito.

1. **Registrar.** El autoseguimiento tiende a aumentar las conductas deseables y reducir las indeseables (Korotitsch y Nelson-Gray, 1999). En el ánimo y el dolor el efecto parece pequeño (Cruise et al., 1996); poner nombre a una emoción puede bajar su intensidad (Lieberman et al., 2007).
2. **Lo que la pantalla muestra.** Los valores de tus avisos al calificar el día (desde `version_noche = 2`, solo después de elegir la valencia), la franja «Tus últimos días» en Hoy, la predicción de tu ánimo medio en enero, la hipótesis escrita del experimento (efecto expectativa) (capítulos 3, 8 y 9).
3. **Los premios.** Logros y campañas dan premios por registrar, por conductas y, en dos logros secretos, por valores de la valencia (H-8-6). La ley de Campbell (1979) dice que un indicador que se usa para premiar se distorsiona (capítulo 9, sección 9.7).
4. **El propósito mismo.** La app quiere que te conozcas y, con eso, que cambies: dormir más tras ver tu sueño, gastar menos tras ver tus gastos. Eso es bueno para la vida y malo para la observación: los datos no describen una vida «sin intervenir», sino una vida que se observa a sí misma.

**Qué se puede hacer.** No eliminarla, sino **marcarla y separarla**:

- Las campañas son variables del panel desde el esquema 13 (`campana_<reto>`, `campanas_n`): se pueden usar como covariable o comparar periodos con y sin campaña (capítulo 9, sección 9.5).
- Los tramos de cada cambio de interfaz están fechados en el Apéndice D y, en parte, en los datos (`version_noche`).
- Las primeras semanas de cada medición nueva se pueden excluir o analizar aparte en R (capítulo 3, sección 3.14).
- Los experimentos personales dan la única estimación de un efecto con la reactividad **igual en las dos condiciones**: registras igual en las semanas «SÍ» y en las «no».

**[Aproximación]**: ninguna de estas medidas cuantifica la reactividad; solo permiten ver si una relación cambia cuando cambia la presión de medir.

---

## 11.6 Limitaciones de este paper

Este documento tiene sus propios límites, que conviene decir con la misma franqueza que los de la app.

1. **Se escribió leyendo el código, no usándolo.** Las descripciones salen de leer Kotlin y R. La compilación y las pruebas JVM corren en GitHub Actions, pero ninguna sesión ejecutó la app en un teléfono. Comportamientos que dependen de Android (cuánto conserva el sistema los eventos de uso, si un wifi sin nombre devuelve nulo, qué sensor de presión trae el teléfono) quedaron como suposiciones marcadas.
2. **R se probó con datos reales en un solo punto.** El script se ejecutó con CSV sintéticos en las sesiones de 10d y con una exportación real de la v89, que reveló que faltaban 57 variables en el panel extendido (D.87). Después, el autor comprobó con una exportación real que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). Las demás funciones de R (`vm_factores`, `vm_semana`, `vm_palabras`, `vm_red`…) solo se compararon con la app con datos inventados.
3. **Hay afirmaciones sin verificar.** `referencias.md` lista, por capítulo, las afirmaciones marcadas **[sin verificar]**: entre ellas, el tamaño original de la Affect Grid, los puntos de corte del WHO-5 (≤ 28) y del UCLA-3 (≥ 6), las vidas medias del magnesio y el omega-3, el valor 0.80 de Watson et al. (1981) y la forma exacta de la corrección de Marriott y Pope (1954). No se presentan como hechos, pero el lector debe tenerlas en cuenta.
4. **Las simulaciones son simulaciones.** Las cifras de falsas señales, cobertura y potencia (capítulos 6 y 8) salen de datos inventados con supuestos concretos (autocorrelación de orden 1, efectos constantes). Con datos reales, el comportamiento puede ser otro.
5. **Muchas manos.** Los capítulos los escribieron sesiones distintas, con modelos distintos, y se actualizaron en cada entrega del módulo 10. Puede haber frases que todavía describan el estado anterior a una entrega o cifras que no coincidan entre capítulos; los módulos 13 y 14 existen para encontrarlas.
6. **La literatura no es una revisión sistemática.** Las referencias se eligieron para respaldar o discutir decisiones concretas; no garantizan que no haya trabajos que digan otra cosa.
7. **Autor y sujeto son la misma persona.** Quien diseñó la app, quien la usa y quien aprueba los cambios es el mismo. No hubo una revisión externa del método. Las sesiones de Claude que escribieron el paper funcionaron como revisión, con los límites de 1 a 6.

---

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

Lo que falta, ordenado de lo más barato a lo más costoso:

1. **Usar lo que ya existe.** El ciclo semanal (H-5-6) se puede controlar hoy agregando `fin_de_semana` o `dia_libre` como predictor: por ejemplo, `vm_modelo_ar1("valencia", c("sueno_h", "pasos", "dia_libre"))`. Y la estabilidad en el tiempo (H-11-1) se puede revisar pasando un periodo a la lista: `vm_relaciones(datos = filter(panel, fecha >= as.Date("2026-01-01")))`, y comparando con otro periodo. No ejecuté estas dos llamadas en esta sesión: `fecha` es una fecha de R (`.vm_enriquecer` completa el calendario con `seq(…, by = "day")`) y ambas funciones aceptan `datos`, pero no las corrí.
2. **`vm_cruce`** sigue con Spearman y el n bruto (H-5-7): podría usar el n efectivo como el resto.
3. **`vm_modelo` con errores AR(1) y el valor de ayer a la vez.** Hoy son dos funciones: `vm_modelo` tiene la opción del valor de ayer, pero supone residuos independientes; `vm_modelo_ar1` tiene los errores AR(1), pero no el valor de ayer.
4. **`vm_experimento`**, que replique `Experimentos.resultado` y agregue la **prueba de aleatorización** sobre todos los planes posibles (H-7-11; capítulo 8, sección 8.6.4). Con el sorteo por pares, el p mínimo es 1/2^(N/2): con 12 semanas, 0.016.

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

La primera fila es la más fácil y la más útil: el panel ya trae `sueno_inicio_h`, `sueno_fin_h`, `primer_uso_h` y `ultimo_uso_h`, así que es una comparación de una tarde en R.

### 11.7.4 Del hallazgo a la conclusión: un flujo confirmatorio

La sección 1.6 y la tabla de 11.4 terminan en el mismo lugar: la lista de relaciones es **exploratoria**, y no hay forma de contar después las maneras en que se miraron los datos. La salida estándar en ciencia es separar **explorar** de **confirmar** (Nosek et al., 2018). Aplicado a un solo sujeto, sin cambiar la app:

1. **Explorar** en un periodo (por ejemplo, un año) con la lista de la app y con R.
2. **Escribir antes** las pocas relaciones que se quieren confirmar, con su dirección y la variable de control (por ejemplo, el día libre). Puede ser un texto con fecha en el repositorio de análisis personal.
3. **Confirmar** con el periodo siguiente, que no se miró, con una sola prueba por relación (o pocas, corregidas entre sí) y el modelo AR(1) de R.
4. **Experimentar** con las que sobreviven y son accionables: el experimento personal es la única herramienta que responde a «qué me cambia».

Este flujo responde a tres problemas a la vez: las comparaciones múltiples (en el paso 3 hay pocas), la maldición del ganador (el tamaño se estima en datos nuevos) y la estacionariedad (si la relación desaparece en el segundo periodo, era de una etapa). Su costo es el tiempo: hay que esperar el segundo periodo. **[Validado]** el principio (prerregistro y confirmación con datos nuevos); **[Diseño propio]** su adaptación.

### 11.7.5 Hallazgos de gravedad media que siguen abiertos

Para una futura ronda de revisión con el mismo criterio del módulo 9. Abiertos del todo:

| Hallazgo | Qué queda |
|---|---|
| H-1-4 | `coerceInputValues` puede convertir un nulo o un valor inválido en el valor por omisión sin registrarlo |
| H-3-4 | El clima de cada día viene del pronóstico o del reanálisis según cuándo sincronizó la app, sin guardar cuál |
| H-4-2 | Cafeína y alcohol: si MacroFactor tiene algo ese día, lo anotado en la app se ignora |
| H-5-6 | Quitar la tendencia no quita el ciclo semanal (remedio en R, sección 11.7.2) |
| H-6-3 | La fecha de un cambio de relación es la de la edición, y lo anterior a la primera clasificación se proyecta |
| H-NAV-9 | Los cambios de dónde y cuándo se registra cambian cuánto se registra; hay que leer por tramo |

Resueltos en parte, con una parte de gravedad media pendiente: H-2-5 (perfiles llenados antes de la 10c), H-4-4 (sin pesos se usan 70 kg; no se muestra la incertidumbre), H-4-9 (vacaciones y turnos no cuentan como días libres), H-5-9 (aire libre con otra variable en «Tus números»), H-6-13 (un diario escrito sin @ cuenta como «no vi a nadie»), H-7-6 (en un antes/después puro, tendencia y efecto se separan mal), H-7-12 (las comparaciones de «Antes y después» no se corrigen), H-8-4 (reportes en carpetas públicas), H-8-5 (la exportación para R va en claro, a propósito, y el JSON lleva el texto de las cartas selladas), H-8-13 (reglas de restauración distintas por colección) y H-8-14 (no se puede elegir qué copia de Drive restaurar).

### 11.7.6 Comprobaciones pendientes

- **App frente a R con datos reales, más allá de la lista**: la lista de relaciones ya coincide (P-30; comprobado por el autor el 28 oct 2026). Faltaría lo mismo con «Todo lo que se mueve con algo» (`vm_factores`) y la Red (`vm_red`, con `min_juntos = 2` y las mismas 45 personas).
- **R en la compilación automática**: hoy R no corre en CI; las pruebas JVM fijan los números que dio R con datos inventados, pero un cambio en `vestigium.R` que rompa algo solo se nota al usarlo.
- **Las afirmaciones [sin verificar]** de `referencias.md`, sobre todo las que sostienen umbrales que la app muestra (WHO-5 ≤ 28, UCLA-3 ≥ 6, jet lag social de 1 hora).

---

## 11.8 Conclusión

Vestigium Mei intenta algo que la literatura del autoseguimiento describe como difícil: no solo juntar datos de una vida, sino **sacar de ellos conclusiones que resistan el azar, las tendencias y el deseo de encontrar algo**. Después de la revisión de los módulos 1 a 10, la parte de análisis está a la altura de esa intención en lo que se puede pedir a una app personal: distingue «cero» de «no sé», mide el ánimo de varias formas sin mezclarlas, corrige por días parecidos y por muchas comparaciones, ofrece experimentos sorteados con intervalos honestos y deja que R verifique lo que muestra el teléfono.

Lo que no puede resolver ninguna app es la naturaleza de sus datos: **una sola persona, que sabe que se mide, que cambia con los años y que elige qué mirar**. Por eso sus conclusiones son **de asociación y para ti**, salvo las de los experimentos, que son causales con las condiciones del capítulo 8. Leídas así, con el nombre verdadero de cada variable y como pistas que se confirman con datos nuevos o con un experimento, las relaciones del panel pueden decir cosas que la memoria no dice. Leídas como verdades, engañan con la misma precisión con la que se calcularon.

---

## 11.9 Resumen del capítulo

1. Lo sólido: una sola regla para «cero» y «no sé», el ánimo medido de varias formas, la lista de relaciones con tres protecciones, R alineado con la app, experimentos sorteados por pares con intervalos corregidos, costuras marcadas y una sola regla de privacidad. De 158 hallazgos, 97 están resueltos, 24 en parte y 37 abiertos (6 de gravedad media, ninguno alto); el módulo 14 sumó seis de gravedad baja (H-14-1 a H-14-6).
2. Lo que se puede concluir depende de la herramienta: descripción, asociación de corto plazo dentro de tus días, predicción con desfase o efecto causal en un experimento. Ninguna generaliza a otras personas, y solo valen para tu futuro si tu vida no cambió en lo importante.
3. La lista de relaciones usa toda la historia, como si las relaciones fueran las mismas en todas las etapas (H-11-1, nuevo), y las maneras de mirar los datos no entran en ninguna corrección (H-11-2, nuevo).
4. Muchas variables miden algo distinto de su nombre: recuerdos, proxies, modelos o cuánto escribes. Hay que leer cada relación con el nombre verdadero de sus variables.
5. Los sesgos se agrupan en siete fuentes que suelen empujar en la misma dirección (los días que quedan son mejores que los que faltan). La única sin marca en los datos es cómo se eligió el análisis.
6. La reactividad viene de registrar, de lo que muestra la pantalla, de los premios y del propósito mismo de la app. No se elimina; se marca (campañas, tramos, experimentos).
7. El paper tiene límites propios: se escribió leyendo el código, R se comparó con la app con datos reales solo en la lista de relaciones y quedan afirmaciones sin verificar.
8. Trabajo futuro: modelos multinivel y DSEM, completar la corrección AR(1) en R (y usar ya `dia_libre` y los periodos), validar las heurísticas con lo que la app mide dos veces y un flujo confirmatorio que separe explorar de confirmar.

---

### Referencias citadas en este capítulo

Ver `referencias.md`. Citas: Asparouhov, Hamaker y Muthén (2018); Campbell (1979); Cronbach y Meehl (1955); Cruise et al. (1996); Gelman, Hill y Yajima (2012); Granger (1969); Korotitsch y Nelson-Gray (1999); Lieberman et al. (2007); Marriott y Pope (1954); Messick (1995); Molenaar (2004); Nosek et al. (2018); Rubin (1976); Watson, Watson y Batt (1981).
