# Capítulo 11. Discusión, limitaciones y trabajo futuro

> **Para qué sirve este capítulo.** Junta lo que dicen los capítulos 2 a 10 para responder tres preguntas: **qué se puede concluir y qué no** de los datos de Vestigium Mei; **qué amenazas a la validez quedan**, vistas en conjunto; y **qué falta**, en la app, en R y en este documento. No describe funciones nuevas: cada afirmación remite al capítulo donde está el detalle. Describe el código de la v90 (commit `8818d5a`, `SCHEMA_V = 14`); las cuentas de hallazgos son las del campo «Estado» de `hallazgos.md` al 28 de octubre de 2026. Los ejemplos son inventados.

---

## 11.1 Qué está bien resuelto

Antes de las limitaciones, conviene decir qué partes son sólidas, porque son las que justifican confiar en el resto con la cautela debida.

1. **«Cero» y «no sé» se distinguen con una sola regla.** Desde la entrega 10c, una variable vale 0 solo desde el primer registro de su fuente y en los días en que se pudo responder (`Estadistica.ceroDesde`; capítulo 2). Es la aplicación correcta de la teoría de datos faltantes (Rubin, 1976): un cero falso baja las medias y crea correlaciones por el solo calendario de adopción. **[Validado]** el principio.
2. **El ánimo se mide de varias formas que no se mezclan.** Avisos al azar dentro de tramos (muestreo estratificado), registros propios, el diario de la noche, el cierre del día y cuestionarios guardan variables distintas (capítulo 3), y desde la entrega 10c los reportes tampoco las mezclan. **[Validado]** el diseño.
3. **La lista de relaciones tiene tres protecciones a la vez**: quita la tendencia (±10 días), descuenta que los días se parecen entre sí (n efectivo) y corrige por las miles de comparaciones (Benjamini-Hochberg, q < 0.10), además de esconder las parejas obvias (capítulo 6). Desde la 10c, «Todo lo que se mueve con algo», «Cruzar dos variables» y las diferencias de medias usan la misma maquinaria. **[Diseño propio]** la combinación; cada pieza, **[Validado]**.
4. **R repite a la app.** Desde la entrega 10d, `vestigium.R` analiza el mismo panel, con las mismas variables y parejas obvias, y una prueba JVM fija que `vm_relaciones` y `vm_factores` dan los mismos números que la app con datos inventados. Con una exportación real, el autor comprobó que las primeras parejas de `vm_relaciones()` coinciden con las de «Las relaciones más marcadas» (28 oct 2026). R es así un **verificador independiente** del teléfono.
5. **Los experimentos son experimentos de verdad**: el plan se sortea por pares de semanas, empieza en lunes, la hipótesis queda sellada antes de empezar y el intervalo usa t de Welch con el error estándar corregido por autocorrelación. En simulación, sin efecto real y con autocorrelación de 0.3 a 0.5, da una falsa señal el 3 o 4 % de las veces, frente al 14 a 24 % del método anterior a la 10c (capítulo 8). **[Validado]** la lógica; el método es conservador.
6. **Las costuras se marcan.** Cuando una regla de medición cambió, cada día guarda con qué regla se midió (`ventana_pantalla`, `ventana_pasos`, `version_noche`), y el Apéndice D de la versión completa dice qué cambió y en qué versión.
7. **Lo privado tiene una sola regla** (`data/Privado.kt`) y una prueba que falla si algo se cuela (`PrivadoPrueba`); el respaldo se cifra con AES-256-GCM, se verifica sin restaurar y no se restaura si es de un esquema más nuevo (capítulo 10).

**El saldo de la revisión.** Los módulos 1 a 9 y la revisión de navegación anotaron **158 hallazgos** (149 de los módulos y 9 H-NAV). Según su campo «Estado», revisado en el módulo 14, **97 están resueltos**, **24 resueltos en parte** y **37 abiertos**: 31 de gravedad baja (uno, baja a media) y 6 de gravedad media; ninguno de gravedad alta queda abierto del todo. Los dos de gravedad alta marcados «en parte» son H-1-1 (una función que se deja de usar sigue contando 0) y H-3-1 (las lecturas del reloj, la agenda, el clima y las ciudades siguen por día de calendario, ahora declarado en cada variable). Se suman los dos de este capítulo (H-11-1 y H-11-2, que se documentan, no se corrigen) y seis de gravedad baja o muy baja de la lectura final contra el código (H-14-1 a H-14-6).

---

## 11.2 Qué se puede concluir y qué no

### 11.2.1 Por herramienta

El capítulo 1 distinguió preguntas descriptivas, de asociación y causales. Esta tabla dice qué afirmación permite cada herramienta.

| Herramienta | Puede decir | No puede decir | Por qué |
|---|---|---|---|
| Tus números, Totales, reportes | Cuánto, cuándo y cuántos días con dato | Que una diferencia con la semana anterior sea real | Esas comparaciones no tienen noción de azar (H-8-11) |
| Dinámica emocional | Qué tan variable y persistente es tu valencia del diario | Lo mismo que esos índices en la literatura | Allí se calculan con mediciones momentáneas; aquí, con un valor recordado por día |
| Las relaciones más marcadas | Qué se mueve junto **de un día a sus vecinos** | Qué causa qué; qué se mueve junto **a lo largo de semanas o meses** | Quitar la tendencia borra los efectos lentos; no hay control de confusores |
| Lo mismo, con desfase de un día | Que lo de hoy **ayuda a predecir** lo de mañana | Que lo cause | Es causalidad de Granger (1969): precedencia y predicción |
| Modelo múltiple | La asociación de un predictor con los demás que elegiste fijos | El efecto de lo que no pusiste | Confusores no medidos; supone residuos independientes |
| Antes y después | Cómo cambiaron tus días alrededor de un cambio de vida | Que el cambio lo causara | Muchas cosas cambian a la vez; hasta 56 comparaciones sin corregir |
| Experimentos personales | Que **hacer la acción, sabiendo que la haces**, cambió tu medida en ese periodo | Que el efecto sea fisiológico, que dure o que valga en otra etapa | Sin cegamiento, con potencia modesta (capítulo 8) |

### 11.2.2 Asociación no es causa, aquí tampoco

Las protecciones de la lista atacan el **azar** y las **tendencias**, no los otros dos motivos por los que dos cosas se mueven juntas sin que una cause la otra:

- **Un tercer factor** (un *confusor*) que mueve a las dos. El más común en datos diarios es **el día de la semana**: duermes más y te sientes mejor el sábado, y la lista sin tendencia lo conserva entero, porque una ventana de 21 días contiene tres semanas exactas (H-5-6; versión completa, §6.5.6). Otros probables: estar enfermo, viajar, una temporada de trabajo, la estación.
- **La dirección.** ¿Dormir bien mejora el ánimo, o el buen ánimo deja dormir? El desfase de un día ayuda, pero no decide: si el ánimo es persistente, el de ayer puede causar el sueño de anoche y el ánimo de hoy a la vez.

Por eso la regla práctica del paper es: **una relación firme en la lista es una hipótesis; un experimento personal es la forma de probarla** (capítulo 8).

### 11.2.3 Qué tan lejos llega una conclusión

Aun la mejor conclusión vale **para el autor, en el periodo analizado**. A otras personas, nada: es la consecuencia directa del argumento idiográfico (Molenaar, 2004; capítulo 1). A tu futuro, solo si el proceso es aproximadamente **estacionario** (las mismas reglas, la misma vida): un cambio de trabajo, de ciudad o de pareja puede cambiar la relación entre el sueño y el ánimo, no solo sus niveles.

De aquí sale un hallazgo nuevo (**H-11-1**): **la lista de relaciones usa toda la historia**. `PantallaExplorar` pasa a `Estadistica.relaciones` el panel completo, desde el primer día. Quitar la tendencia corrige los **niveles** que cambian lento, pero no una **relación** que cambia con los años: si durante dos años el sueño y el ánimo iban muy juntos y en los últimos dos ya no, la lista muestra un promedio que no describe ninguno de los dos periodos. **[Aproximación]**: es el supuesto de estacionariedad, razonable a corto plazo y cada vez menos cuanto más larga es la historia.

---

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

Lo importante no es que haya proxies (toda medición del mundo real lo es en algún grado), sino **leer cada relación con el nombre verdadero de sus variables**. «`per_ana` se asocia con mejor valencia» quiere decir «los días en que escribo sobre Ana con @ los recuerdo como mejores», que puede deberse a verla, a pensar en ella o a que esos días escribo más. Una relación de `palabras` o de `personas` con cualquier otra cosa es, en buena parte, una relación con **cuánto escribiste ese día**. Cronbach y Meehl (1955) proponen validar un constructo por su **red nomológica** (si mide lo que dice, debe relacionarse con otras medidas como predice la teoría); varias cosas se miden dos veces por caminos distintos, y eso permite algunas de esas comprobaciones en R (sección 11.7.3).

---

## 11.4 Sesgos, en conjunto

Vistos juntos, los sesgos de cada capítulo se agrupan en siete fuentes.

| Fuente | Qué produce | Marca en los datos |
|---|---|---|
| **Qué días faltan** | Los días malos se registran menos (faltante no al azar, MNAR) | `dia_dificil`, `tasa_respuesta` (desde el esquema 13), días sin diario |
| **Cuándo se anotó** | Lo anotado tarde es recuerdo, no experiencia (H-NAV-9) | `retroactivo`, `ts_creado` y `ts_editado` (desde el 14), `vm_retraso()` |
| **Costuras del instrumento** | Series partidas en tramos no del todo comparables | `ventana_pantalla`, `ventana_pasos`, `version_noche`, `schema_v`; no la vida media de la cafeína (H-4-11) ni las definiciones del diccionario (H-1-8) |
| **Qué día es** | En una fila, unas variables cuentan la madrugada y otras no | `Diccionario.CALENDARIO`; las horas viejas se leen con la zona actual (H-1-7) |
| **Confusores comunes** | Relaciones debidas al día de la semana, la estación, una enfermedad | `dia_semana`, `fin_de_semana`, `festivo`, `dia_libre`; de enfermedad, solo `ctx_enfermo` |
| **Cómo se eligió el análisis** | Más hallazgos por azar; la más fuerte exagera su tamaño | Ninguna (H-11-2) |
| **La historia completa** | Una relación promedio de etapas distintas | `vida_*` y las fechas de «Conocerte» (H-11-1) |

Tres observaciones. **Los sesgos no son independientes**: un día difícil es a la vez un día con menos registros, más probablemente anotado tarde y quizá de enfermedad; los tres empujan en la misma dirección: los días que quedan en el análisis son mejores que los que faltan. **Varias marcas existen solo desde hace poco** (esquemas 13 y 14), y para los años anteriores `tasa_respuesta`, `ts_creado` y `ts_editado` quedan vacíos. **La fuente 6 es la única sin ninguna marca**: no hay forma de saber, después, cuántas maneras de mirar los datos se probaron; es la razón del flujo confirmatorio de la sección 11.7.4.

---

## 11.5 Reactividad, en conjunto

**Reactividad** es que medir algo cambie lo que se mide (capítulo 3). Aquí tiene cuatro fuentes:

1. **Registrar.** El autoseguimiento tiende a aumentar las conductas deseables y reducir las indeseables (Korotitsch y Nelson-Gray, 1999); en el ánimo y el dolor el efecto parece pequeño (Cruise et al., 1996); poner nombre a una emoción puede bajar su intensidad (Lieberman et al., 2007).
2. **Lo que la pantalla muestra**: los valores de tus avisos al calificar el día (desde `version_noche = 2`, solo después de elegir la valencia), la franja «Tus últimos días», la hipótesis escrita del experimento (efecto expectativa).
3. **Los premios.** Logros y campañas premian registrar, conductas y, en dos logros secretos, valores de la valencia (H-8-6). La ley de Campbell (1979) dice que un indicador que se usa para premiar se distorsiona (capítulo 9).
4. **El propósito mismo.** La app quiere que te conozcas y, con eso, que cambies. Eso es bueno para la vida y malo para la observación: los datos describen una vida que se observa a sí misma.

No se elimina; se **marca y se separa**: las campañas son variables del panel desde el esquema 13 (`campana_<reto>`, `campanas_n`); los tramos de cada cambio de interfaz están fechados (y en parte en los datos, `version_noche`); las primeras semanas de cada medición nueva se pueden analizar aparte en R, y los experimentos dan la única estimación con la reactividad **igual en las dos condiciones**. **[Aproximación]**: nada de esto cuantifica la reactividad; solo permite ver si una relación cambia cuando cambia la presión de medir.

---

## 11.6 Limitaciones de este paper

1. **Se escribió leyendo el código, no usándolo.** La compilación y las pruebas JVM corren en GitHub Actions, pero ninguna sesión ejecutó la app en un teléfono; lo que depende de Android quedó como suposición marcada.
2. **R se probó con datos reales en un solo punto.** Con CSV sintéticos en las sesiones de 10d y con una exportación real de la v89, que reveló que faltaban 57 variables en el panel extendido; después, el autor comprobó la coincidencia de `vm_relaciones()` con la lista. Las demás funciones (`vm_factores`, `vm_semana`, `vm_palabras`, `vm_red`…) solo se compararon con datos inventados.
3. **Hay afirmaciones sin verificar**, listadas en `referencias.md`: entre ellas, los puntos de corte del WHO-5 (≤ 28) y del UCLA-3 (≥ 6) **[sin verificar]**.
4. **Las simulaciones son simulaciones**: las cifras de falsas señales, cobertura y potencia salen de datos inventados con autocorrelación de orden 1 y efectos constantes.
5. **Muchas manos**: sesiones y modelos distintos; puede quedar alguna frase o cifra desfasada.
6. **La literatura no es una revisión sistemática.**
7. **Autor y sujeto son la misma persona**, y no hubo revisión externa del método; las sesiones de Claude funcionaron como revisión, con los límites anteriores.

---

## 11.7 Trabajo futuro

Son **propuestas**, no cambios: el código solo cambia con lo aprobado en una revisión como la del módulo 9. Varias se hacen en R con lo que ya se exporta.

### 11.7.1 Modelos multinivel

Los días están anidados en semanas, meses y etapas. Un **modelo multinivel** (jerárquico o de efectos mixtos) estima, por ejemplo, la relación entre el sueño y el ánimo **en cada año** y cuánto varía de un año a otro, prestando información entre años para que los que tienen pocos datos no den estimaciones extremas. Dos usos: **relaciones que cambian con el tiempo** (H-11-1), con la pendiente variando por año o por etapa de `vida_*`, por ejemplo con modelos dinámicos de ecuaciones estructurales (DSEM; Asparouhov, Hamaker y Muthén, 2018), que combinan autocorrelación, efectos variables y datos faltantes (**[Validado]** el método; **[Aproximación]** su uso con periodos de un solo sujeto como unidades); y **familias con muchos miembros** (`per_`, `lug_`, palabras), donde el modelo **encoge** los miembros con pocos datos hacia el promedio de su familia y reduce la maldición del ganador (Gelman, Hill y Yajima, 2012); es la respuesta de fondo a H-5-10.

### 11.7.2 Autocorrelación en R

Hoy `vm_relaciones` y `vm_factores` corrigen como la app, y `vm_modelo_ar1` ajusta el modelo múltiple con **errores AR(1)** (`nlme::gls` con `corAR1(form = ~ t)`), que respeta que cada día se parece al anterior. Lo que falta, de lo más barato a lo más costoso:

1. **Usar lo que ya existe.** El ciclo semanal (H-5-6) se controla agregando `dia_libre` o `fin_de_semana` como predictor: `vm_modelo_ar1("valencia", c("sueno_h", "pasos", "dia_libre"))`. La estabilidad en el tiempo (H-11-1) se revisa pasando un periodo: `vm_relaciones(datos = filter(panel, fecha >= as.Date("2026-01-01")))`. Estas dos llamadas no se ejecutaron en la sesión que las propuso.
2. **`vm_cruce`** sigue con Spearman y el n bruto (H-5-7).
3. **El valor de ayer y los errores AR(1) a la vez**: hoy `vm_modelo` tiene lo primero y `vm_modelo_ar1` lo segundo.
4. **`vm_experimento`**, con la **prueba de aleatorización** sobre todos los planes posibles (H-7-11): con el sorteo por pares, el p mínimo es 1/2^(N/2); con 12 semanas, 0.016.

### 11.7.3 Validar las heurísticas

Muchas reglas **[Diseño propio]** o **[Aproximación]** se pueden poner a prueba con los propios datos, porque la app mide algunas cosas dos veces o porque el usuario ya corrige algunas a mano. La más fácil y útil: comparar **las noches del teléfono** (último y primer uso) con **las del reloj**; el panel ya trae `sueno_inicio_h`, `sueno_fin_h`, `primer_uso_h` y `ultimo_uso_h`, y el resultado sería el error típico, en minutos, de la hora de dormir y despertar aproximadas. Otras: las líneas de la Red frente a los vínculos declarados en «¿A quién conoce?»; `per_<x>` frente a las presencias revisadas; la valencia del diario frente a `muestreo_valencia` y el cierre del mismo día; la ventana de ±10 días y el umbral de 0.12 frente a `vm_relaciones(ventana = 7)`, `14` o `28` (análisis de sensibilidad), y la detección de lugares (75 m, 20 min, 120 m) frente a las visitas corregidas, aunque «no estuve aquí» hoy no se guarda (versión completa, §11.7.3).

### 11.7.4 Del hallazgo a la conclusión: un flujo confirmatorio

La lista de relaciones es **exploratoria**, y no hay forma de contar después las maneras en que se miraron los datos. La salida estándar en ciencia es separar **explorar** de **confirmar** (Nosek et al., 2018). Aplicado a un solo sujeto, sin cambiar la app:

1. **Explorar** en un periodo (por ejemplo, un año) con la lista de la app y con R.
2. **Escribir antes** las pocas relaciones que se quieren confirmar, con su dirección y la variable de control.
3. **Confirmar** con el periodo siguiente, que no se miró, con una sola prueba por relación (o pocas, corregidas entre sí) y el modelo AR(1) de R.
4. **Experimentar** con las que sobreviven y son accionables.

Este flujo responde a la vez a las comparaciones múltiples (en el paso 3 hay pocas), a la maldición del ganador (el tamaño se estima en datos nuevos) y a la estacionariedad (si la relación desaparece en el segundo periodo, era de una etapa). Su costo es el tiempo. **[Validado]** el principio; **[Diseño propio]** su adaptación.

### 11.7.5 Lo que queda abierto y por comprobar

Los seis hallazgos de gravedad media abiertos del todo son H-1-4 (`coerceInputValues` puede convertir un valor inválido en el de omisión sin registrarlo), H-3-4 (el clima viene del pronóstico o del reanálisis sin guardar cuál), H-4-2 (si MacroFactor tiene cafeína o alcohol ese día, lo anotado en la app se ignora), H-5-6 (el ciclo semanal), H-6-3 (la fecha de un cambio de relación es la de la edición) y H-NAV-9 (los cambios de dónde y cuándo se registra cambian cuánto se registra). Once más están resueltos en parte con una parte media pendiente (versión completa, §11.7.5).

Faltan además tres comprobaciones: app frente a R con datos reales **más allá de la lista** (`vm_factores` y `vm_red`, con `min_juntos = 2` y las mismas 45 personas); **R en la compilación automática**, porque hoy un cambio en `vestigium.R` que rompa algo solo se nota al usarlo, y las afirmaciones **[sin verificar]** que sostienen umbrales que la app muestra (WHO-5 ≤ 28, UCLA-3 ≥ 6, jet lag social de 1 hora).

---

## 11.8 Conclusión

Vestigium Mei intenta algo que la literatura del autoseguimiento describe como difícil: no solo juntar datos de una vida, sino **sacar de ellos conclusiones que resistan el azar, las tendencias y el deseo de encontrar algo**. Después de la revisión de los módulos 1 a 10, la parte de análisis está a la altura de esa intención en lo que se puede pedir a una app personal: distingue «cero» de «no sé», mide el ánimo de varias formas sin mezclarlas, corrige por días parecidos y por muchas comparaciones, ofrece experimentos sorteados con intervalos honestos y deja que R verifique lo que muestra el teléfono.

Lo que no puede resolver ninguna app es la naturaleza de sus datos: **una sola persona, que sabe que se mide, que cambia con los años y que elige qué mirar**. Por eso sus conclusiones son **de asociación y para ti**, salvo las de los experimentos, que son causales con las condiciones del capítulo 8. Leídas con el nombre verdadero de cada variable y como pistas que se confirman con datos nuevos o con un experimento, las relaciones del panel pueden decir cosas que la memoria no dice. Leídas como verdades, engañan con la misma precisión con la que se calcularon.

---

## 11.9 En resumen

1. Lo sólido: una sola regla para «cero» y «no sé», el ánimo medido de varias formas, la lista con tres protecciones, R alineado con la app, experimentos sorteados por pares con intervalos corregidos, costuras marcadas y una sola regla de privacidad. De 158 hallazgos quedan 37 abiertos (6 de gravedad media, ninguno alto).
2. Lo que se puede concluir depende de la herramienta: descripción, asociación de corto plazo, predicción con desfase o efecto causal en un experimento. Nada generaliza a otras personas, y solo vale para tu futuro si tu vida no cambió en lo importante.
3. La lista usa toda la historia (H-11-1) y las maneras de mirar los datos no entran en ninguna corrección (H-11-2).
4. Muchas variables miden algo distinto de su nombre: recuerdos, proxies, modelos o cuánto escribes.
5. Los sesgos se agrupan en siete fuentes que suelen empujar en la misma dirección; la única sin marca es cómo se eligió el análisis.
6. La reactividad no se elimina; se marca (campañas, tramos, experimentos).
7. El paper se escribió leyendo el código, R se comparó con datos reales solo en la lista y quedan afirmaciones sin verificar.
8. Trabajo futuro: modelos multinivel, completar la corrección AR(1) en R, validar las heurísticas con lo que se mide dos veces y un flujo que separe explorar de confirmar.
