# Revisión metodológica general · Lista de cambios propuestos (módulo 9)

_Hecho el 27 oct 2026 sobre el código de `main` en `fa527df`/`3abc1c9` (v84; `SCHEMA_V = 12`) y los capítulos 2 a 10 tal como están en el PR #3. Este documento es un **diagnóstico**: no cambia código. Lo aplica el módulo 10._

## Respuesta del autor (27 oct 2026)

Ninguna propuesta se tachó: todas quedan aprobadas. Las decisiones de la sección 2 quedan así:

- **D-1 y D-2, ampliadas.** **Todo** el seguimiento privado se ve **solo en una pantalla de Privacidad** (nueva, o la que ya existe ampliada) y no sale en ninguna otra visualización. Es decir: impulsos, recaídas, libido, detonantes, el hábito ligado a las recaídas y **los medicamentos**. **Sí** se exporta a R, como ahora. Esto amplía P-01 a P-03: ya no basta con sacarlo de las salidas compartibles; también sale de las pantallas comunes (Día, momentos, cinta, calendario, Explorar, Análisis, experimentos, logros, metas y widgets). El módulo 10 lo detalla como **P-34** antes de programarlo.
- **D-3: sí.** Pantalla y pasos pasan al día lógico (P-16, parte ii).
- **D-4: sí.** Festivos (I-1) y estatura para el alcohol (I-2), con el alcance mínimo descrito.
- **Dos funciones nuevas pedidas** (fuera del criterio del módulo 9; se definen antes de programarlas):
  - **I-6 · Días especiales propios:** cumpleaños de familiares y otras fechas que se repiten cada año.
  - **I-7 · Datos de las personas:** edad y otros, a partir de su fecha de nacimiento.

  Se recomienda que entren en el mismo aumento de `SCHEMA_V` de la fase 2, para no subir el esquema dos veces.
- **Preguntas abiertas para el módulo 10:**
  1. Si la pantalla de Privacidad debe tener sus propios análisis (tendencias, relación con el sueño) o si eso se deja solo a R.
  2. Si el impulso puede seguir siendo la medida de un experimento.
  3. Si hace falta contraseña para cualquier exportación que salga del teléfono (H-8-5).

## Cómo leer y anotar este documento

1. **Qué hice.** Leí otra vez, contra el código, los 109 hallazgos de `hallazgos.md` (módulos 1 a 8), uno por uno. Busqué además problemas que ningún módulo vio por separado, sobre todo **en las uniones**: una variable que un archivo calcula de un modo y otro usa de otro; la app frente a R; datos privados que llegan donde no deben. Los nuevos están al final de `hallazgos.md` como H-9-1 a H-9-40.
2. **Criterio para proponer cambiar código** (del plan). Solo si se cumple al menos una de estas condiciones:
   - **(a)** un cálculo es incorrecto o engaña;
   - **(b)** la app y R dan resultados distintos para lo mismo;
   - **(c)** un dato privado aparece donde no debe;
   - **(d)** se pierde o corrompe información;
   - **(e)** una variable no puede cruzarse con otras cuando debería.

   Lo que no cumple ninguna (funciones nuevas, rediseños, gustos) **se documenta en el paper y ya**.
3. **Tus cuatro ideas** (festivos, estatura para el alcohol, «¿Lo hiciste hoy?», registrar campañas y avisos) están evaluadas con el mismo criterio en la sección 5. Dos lo cumplen y entran como propuestas normales. Las otras dos no lo cumplen; las dejo escritas por si quieres hacerlas de todos modos, marcadas como **fuera del criterio**.
4. **Cómo anotar.** Tacha (`~~P-07~~`) o escribe un comentario debajo de lo que no quieras. **Lo que no taches se considera aprobado** para el módulo 10. En la sección 2 hay cuatro decisiones que son tuyas; cada una trae mi recomendación, que es la que se aplicará si no dices otra cosa.
5. **Límites.** No hay SDK de Android en este entorno: nada de lo propuesto está compilado ni probado en el teléfono. R tampoco está instalado aquí; las cifras de simulación se comprobaron con Python, reproduciendo la lógica del código (los scripts quedaron fuera del repositorio). Donde algo no se pudo comprobar, lo digo.

Las rutas de código son relativas a `app/src/main/java/io/github/ruddy2003/vestigium/`; «R» es `app/src/main/assets/vestigium.R`.

---

## 1. Resultado de la verificación en una mirada

- **Ningún hallazgo se descarta.** De los 109, 60 se confirman tal cual y 48 con matices: alguna cifra, el nombre de un archivo o el alcance. H-3-7 (wifi) queda como **parcial**: el mecanismo descrito no es probable, porque un SSID censurado cuenta como «con wifi», no como «sin wifi».
- **Cambia la gravedad de varios:**
  - Bajan: H-1-2, H-1-4, H-6-2, H-8-5 y H-8-12 (a baja); H-5-7 y H-5-12 (a muy baja).
  - Ninguno sube, pero tres son **más graves de lo que decían**:
    - H-1-3: una configuración ilegible se reemplaza por la de fábrica y se sobrescribe.
    - H-6-1: las @menciones escritas en recaídas llegan a «Tu gente», a las sugerencias al escribir, a `per_` y a `tema_`.
    - H-8-13: al restaurar, gastos e ingresos quedan distintos en la base y en la pantalla.
- **Las pistas preliminares del plan:**
  - 1 (experimentos: intervalo estrecho), 2 (z en vez de t) y 3 (sorteo libre): **confirmadas** (H-7-3, H-5-5, H-7-7, H-7-5).
  - 4 (constelación frente a red): **descartada en su forma original**, porque las dos excluyen recaída y libido con el mismo criterio. Pero la revisión destapó una fuga mayor en otras funciones (H-6-1).
  - 5 (Pearson y Spearman): **confirmada con matices**. Cada función de R usa el mismo coeficiente que su gemela de la app; la única diferencia real es `vm_cruce` frente a «Cruzar dos variables».
  - 6 (la constelación usa la relación de hoy): **confirmada**; se documenta.
- **Se encontraron 40 problemas nuevos** (H-9-1 a H-9-40). Los más importantes:
  - H-9-1: el conteo de impulsos entra en los análisis, en «La relación del año» y en los logros propios aunque el seguimiento privado esté apagado.
  - H-9-2: pérdida total de la configuración si no se puede leer.
  - H-9-9: un aviso de ánimo pendiente que se responde desde la tarjeta de Hoy se guarda como registro propio y la tarjeta no se apaga.
  - H-9-20: el diccionario de la exportación describe mal varias columnas por claves repetidas.

La tabla completa, hallazgo por hallazgo, está en la sección 6.

---

## 2. Decisiones que son tuyas (con mi recomendación)

**D-1 · ¿El impulso es «privado» en las salidas?**
- **El problema.** Para el código, solo la recaída y la libido son privadas (`Momento.privado`, `data/Modelo.kt:160`). En cambio, `CLAUDE.md`, el comentario de `ciencia/Periodo.kt:17` y la regla del documento de contexto ponen el impulso dentro del seguimiento privado. Hoy los impulsos salen en el reporte (con su nombre), en «Tu año», en los logros de momentos, en el mapa y en el widget.
- **Recomendación: sí.** Fuera de las salidas declaradas (reportes, Tu año, logros, mapas y widgets), y detrás del interruptor en las listas de análisis. Se sigue exportando a R, como ahora.
- **Cómo hacerlo sin romper nada.** No cambiar `Momento.privado`: tocarlo movería unos 40 sitios, incluidos cálculos del panel y la pantalla del Día. Mejor una propiedad nueva que solo usen las salidas (P-01).

**D-2 · ¿Los medicamentos son privados?**
- **El problema.** `CLAUDE.md` y el comentario de `Periodo` dicen que sí. El código los trata como salud normal: la sección del Día siempre está visible, y `medicamentos_n` y la familia `med_` entran siempre en las relaciones, en «La relación del año» y en los logros propios.
- **Recomendación: sí, en el mismo sentido que D-1.** Siguen visibles en el Día (es donde se anotan), pero quedan fuera de las salidas compartibles, y en los análisis solo aparecen con el interruptor encendido (P-03).

**D-3 · ¿Pantalla y pasos pasan al día lógico (corte a las 5:00) de aquí en adelante?** (H-3-1, alta)
- **El problema.** La pantalla, los pasos, la agenda, el clima, Mundo y `lugares_distintos` usan el día de calendario (00:00–24:00). Las notificaciones, el wifi, la percepción y casi todo lo demás usan el día lógico. En una misma fila del panel, la madrugada cuenta para unas variables y no para otras. Justo las de uso nocturno, que son las más ligadas al sueño.
- **Opciones:**
  - (i) Solo corregir el texto (`LEEME.txt` y el diccionario dicen hoy que todo empieza a las 5:00, y es falso).
  - (ii) Además, cambiar la ventana de pantalla y pasos al día lógico a partir de la versión nueva. Se guarda en cada día qué ventana se usó, así que hay que subir `SCHEMA_V`, y la serie queda partida en un antes y un después.
- **Recomendación: (ii).** La app es para toda la vida: cuanto antes se unifique, menor la parte de la historia mezclada. Si prefieres no partir la serie, tacha la parte (ii) de P-16.

**D-4 · Tus ideas fuera del criterio** (festivos e I-2, sección 5).
- **El criterio no las pide.** Si no las tachas, las aplico con el alcance mínimo que describo.
- **Recomendación:**
  - **I-1 (festivos): sí**, como variable `festivo` y como día libre en el jet lag social. Va después de P-19, que arregla primero la ventana de noches, un problema mayor.
  - **I-2 (estatura): sí, pero después de P-20.** Corregir de dónde sale el peso importa más que la estatura.
- **I-3 («¿Lo hiciste hoy?») sí entra en el criterio, en parte**, porque arregla un rótulo engañoso. La recomiendo con las condiciones que describo.

---

## 3. Lo que se ve al juntar las piezas

Estos seis patrones atraviesan varios módulos. Ningún capítulo los podía ver completos, y explican por qué varias propuestas se agrupan.

### 3.1 Hay tres ideas distintas de «privado», y ninguna coincide con la regla declarada

1. **`Momento.privado`** (recaída y libido). Lo usan los reportes, Tu año, los logros, el mapa, las presencias y la Red.
2. **El grupo de variables «Privado»** (`impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido` y `det_`), que solo existe con el interruptor encendido. Lo filtran las metas (`ui/pantallas/Lote2.kt:404`), pero **no** «Tu año» (`ciencia/TuAnio.kt:134`) ni los logros propios (`ui/pantallas/Logros.kt:257`).
3. **El interruptor `seguimiento_privado`.** Oculta o muestra pantallas.

Por los huecos entre las tres se cuelan cosas:
- `n_impulso` está en el grupo «Momentos» (`ciencia/Estadistica.kt:254`), así que entra en todo **siempre**.
- Los medicamentos no están en ninguna de las tres.
- El hábito ligado a las recaídas aparece por su nombre en reportes, logros, widgets y campañas.
- Los textos de las recaídas alimentan `per_` y `tema_` y «Tu gente», porque `Dinamicas.construir` y `Personas.menciones` no filtran nada.
- Las coordenadas de un momento privado pueden enviarse (redondeadas a 0.01°) a Open-Meteo los días sin GPS.

La solución no es parchar cada sitio, sino **dos funciones únicas** que todos consulten: `esReservado(momento)` para las salidas y `esPrivada(variable)` para los análisis. Eso hacen P-01 a P-05. Este problema de fondo se registra como H-9-39.

### 3.2 «No anoté» y «fue cero» se confunden, con cuatro reglas distintas

En el mismo panel conviven cuatro bases para rellenar con 0:
- **Todo día con diario:** `gasto_total`, `gasto_imprevisto`, `n_impulso` y `escalada`.
- **Desde el primer registro:** `ingreso_total`, `enfoque_*`, `n_<emoción>` y `n_recaida`.
- **Cualquier fecha con algún dato:** `gimnasio`.
- **Todo día con documento**, aunque no tenga texto: `vi_*`, `personas_vistas`, `per_`, `hab_*`, `ctx_*` y `sueno_recordado`.

Y al revés: `ejercicio_min` y `distancia_km` quedan **vacíos** en días en que sí se sabe que fueron 0.

Tres cosas empeoran el cuadro:
- Una recaída o un toque en el widget de hábitos crean el documento del día, y con eso un día de «no vi a nadie».
- R recibe otra regla más: `gasto_total` e `ingreso_total` valen 0 en **toda** fecha exportada.
- Como los días sin anotar no son al azar (suelen ser los peores o los más ocupados), esto sesga medias y correlaciones.

P-15 propone **una sola regla**: 0 desde el primer registro de esa fuente y solo en días en que la sección se pudo responder; vacío en todo lo demás. P-30 aplica la misma regla a la exportación.

### 3.3 La app y R analizan tablas distintas

`vm_relaciones()` aplica el mismo algoritmo que «Las relaciones más marcadas», pero sobre `vm_panel.csv`, que no es el panel de la app:
- **Faltan variables.** De unas 210 variables fijas, el CSV tiene unas 120, y ninguna dinámica.
- **Hay 11 nombres distintos para lo mismo.**
- **Las reglas de ceros son otras.**
- **Hay umbrales distintos:** `ultimo_uso_h` suma 24 si la hora es menor que 5 en la app y menor que 12 en R; `canciones` cuenta solo escuchas de 30 s o más en R y todas en la app, aunque el diccionario promete lo de R.
- **La lista de «obvias» es mucho más corta:** 19 familias frente a 56.
- **R analiza `fin_de_semana` como si fuera una variable más.**

El archivo que sí se parece a la app, `vm_panel_extendido.csv`, existe, pero el script no lo carga.

Mientras esto siga así, **R no sirve para comprobar lo que muestra la app**, y esa comprobación es la garantía que el paper promete. P-30 a P-32.

### 3.4 Los intervalos tratan días parecidos como si fueran independientes

La lista principal de relaciones corrige por autocorrelación: usa el número efectivo de días (nEf). Pero el resto de la app no lo hace:
- «Todo lo que se mueve con algo», que es la pantalla que se abre al tocar una tarjeta de esa lista;
- «Cruzar dos variables»;
- `Estadistica.diferencia`, que usan la tendencia de 30 contra 30 días, los experimentos y «Antes y después»;
- el modelo múltiple.

En estas pantallas el p es demasiado pequeño y se usa 1.96 aunque haya 3 datos. Resultado: la misma pareja puede salir «podría ser azar» en la lista y con ✓ un toque después. En simulación, con autocorrelación 0.8, las falsas alarmas suben de 5 % a unos 33 %.

P-17 propone **dos piezas comunes**: un cuantil de *t* y un factor de corrección AR(1). Todas las funciones las reutilizan.

### 3.5 Lo que se fecha «hoy» en lugar de cuando pasó

Varias variables toman la fecha del momento en que se edita algo, no la del hecho:
- el cambio de relación (H-6-3);
- el primer llenado de «Conocerte» (H-2-5);
- la cercanía (H-6-12);
- `retroactivo` (H-2-2);
- el cierre de un día pasado (H-2-8);
- el fin de un experimento (H-7-2);
- la aceptación de una campaña (H-9-34).

El efecto es siempre el mismo: lo de hoy se proyecta hacia atrás. P-12, P-13 y P-26 arreglan los casos baratos; los que necesitan un selector de fecha (H-6-3) solo se documentan.

### 3.6 Restaurar y respaldar no son operaciones seguras

- Un documento que no se puede leer desaparece en silencio, y si es la configuración, se pierde para siempre (H-1-3, H-9-2).
- Restaurar mezcla reglas distintas por colección:
  - en los días gana el teléfono;
  - en salud, pantalla y experimentos gana el respaldo;
  - en gastos e ingresos, la base y la pantalla quedan distintas.
- Restaurar no revisa la versión del esquema.
- Drive reemplaza la copia del día sin mirar si la nueva está empobrecida.
- Activar el cifrado borra las copias en claro sin avisar.
- Los datos nuevos que propongo guardar (avisos, fin de experimento, adherencia) heredarían estos problemas.

Por eso P-07 a P-09 van **antes** que P-10 a P-13 en el orden de trabajo.

---

## 4. Propuestas de cambio

Cada propuesta tiene: **qué cambia**, **por qué** (criterio y hallazgos), **dónde** (archivo y función), **riesgo**, **cómo se comprobará** y **qué capítulo actualizar**. «Prueba JVM» quiere decir una prueba en `app/src/test/`. Ya existe `RespaldoPrueba.kt`, y el workflow `capturas.yml` corre `testDebugUnitTest` y deja las fallas en `fallas.txt`.

### Orden sugerido para el módulo 10

1. **Fase 1, sin tocar lo que se guarda** (no sube `SCHEMA_V`): P-01 a P-09, P-14 a P-29 (salvo la parte (ii) de P-16), P-33 e I-1.
2. **Fase 2, un solo aumento de `SCHEMA_V` de 12 a 13** para todo lo que cambia lo guardado: P-10, P-11 (paso B), P-12, P-13, P-16 (ii) e I-3. Hacerlo en un solo salto evita cuatro versiones de esquema seguidas.
3. **Fase 3, R y exportación:** P-30 a P-32. Van al final porque dependen de los nombres y las reglas que dejen las fases 1 y 2.

---

### A. Privacidad (criterio c)

#### P-01 · Una sola regla para lo reservado en las salidas (D-1)
- **Qué cambia.** Se crea `val Momento.reservado get() = privado || tipo == "impulso"` en `data/Modelo.kt`. Las salidas que la regla declarada protege la usan en lugar de `privado`: reportes, Tu año, logros, mapa y widgets.
- **Por qué.** Es el criterio (c): los impulsos llegan hoy a salidas que se guardan en carpetas públicas o se comparten, y a los logros. Resuelve H-8-1 y la parte del impulso de H-6-1.
- **Dónde.**
  - `ciencia/Periodo.kt`: el filtro de `:75` y sus usos en `:86`, `:89` y `:173`.
  - `reportes/Reportes.kt:574`.
  - `ciencia/TuAnio.kt:111` y `:126`.
  - `ciencia/Logros.kt:122`.
  - `ui/pantallas/Mapa.kt:429`.
  - `widget/Widgets.kt:315`.
  - `ui/pantallas/Explorar.kt:238`: «A qué hora aparecen tus emociones» muestra impulsos sin mirar el interruptor; se condicionan a `seguimiento_privado`.
- **No cambia:**
  - `Momento.privado`;
  - el panel (`ciencia/Estadistica.kt:542` sigue igual, salvo lo que decide P-03);
  - la pantalla del Día y la de impulsos;
  - la exportación a R.
- **Riesgo.** Bajo. Bajan los conteos de momentos en los reportes y en los logros «Coleccionista», «Cien momentos» y «Mil momentos». Los logros ya ganados no se quitan, porque viven en `logros_vistos`; conviene comprobarlo.
- **Cómo se comprueba.**
  - Con un respaldo sintético que tenga impulsos: el reporte semanal, «Tu año», el mapa y el widget no muestran ninguno.
  - Capturas de Paparazzi del reporte y del widget.
- **Capítulos.** 9 (§ reportes y logros) y 10 (§ seguimiento privado).

#### P-02 · Las menciones escritas en recaídas no alimentan personas ni temas
- **Qué cambia.** Se filtra `!it.privado` en estos sitios:
  - `ciencia/Dinamicas.kt:116`, que alimenta `per_` y `tema_`;
  - `:104` (`diasDiario`), que es la base de los ceros;
  - `data/Personas.kt:62` (`menciones`), que alimenta «Tu gente», «¿Quién es @x?», los duplicados y «¿A quién conoce?»;
  - las sugerencias al escribir: `ui/Componentes.kt:455` (`Etiquetas.frecuentes`) y `ui/pantallas/Dia.kt:824` (`Detectados`);
  - la lista de temas de `data/TusTemas.kt:22`, pero no la función de renombrar, que debe seguir tocando todos los textos.
- **Por qué.** Criterio (c): lo escrito en una recaída aparece en pantallas comunes y en variables de análisis aunque el interruptor esté apagado (H-6-1). La libido no tiene campo de texto desde la app (`ui/QuickActivity.kt:357`), así que la fuga práctica viene de las recaídas.
- **Riesgo.**
  - Cambian los valores de `per_*` y `tema_*`; alguna columna puede desaparecer de `vm_panel_extendido.csv` si solo existía por recaídas.
  - No cambia la estructura del diccionario ni de R.
- **Cómo se comprueba.** Crear una recaída sintética con «@prueba» y «#prueba». La persona no debe salir en Tu gente, ni en la tarjeta «¿Quién es…?», ni en las sugerencias, ni en Explorar.
- **Capítulos.** 7 (tabla 7.2 y § privacidad) y 10.

#### P-03 · Las variables privadas, solo detrás del interruptor y nunca en lo que se comparte (D-2)
- **Qué cambia:**
  1. **`n_impulso`** pasa al grupo «Privado» y solo se declara con `seguimiento_privado` (`ciencia/Estadistica.kt:254`).
  2. **`medicamentos_n` y la familia `med_`** (`ciencia/Dinamicas.kt:172`) quedan detrás del interruptor, como ya pasa con `det_`.
  3. **«Tu año»** (`ciencia/TuAnio.kt:134`) excluye el grupo «Privado», `medicamentos_n`, `med_*` y `hab_<hábito ligado a recaídas>`.
  4. **Los logros propios** (`ui/pantallas/Logros.kt:257`) filtran el grupo «Privado» igual que las metas (`ui/pantallas/Lote2.kt:404`).
  5. **El hábito ligado a recaídas** (`data/Recaidas.kt`) se excluye de la constancia por hábito (`c_hab_*` en `ciencia/Logros.kt`), de los retos `habito:*` de `ciencia/Campanas.kt`, de la lista de hábitos del reporte (`reportes/Reportes.kt:634`) y del widget de hábitos.

  Para no repetir la regla en cada sitio, se crea `Estadistica.esPrivada(id)`.
- **Por qué.** Criterio (c). Resuelve H-8-2, H-8-3, H-8-16, H-9-1 y H-9-38. Además, la vía indirecta (recaída → hábito desmarcado → racha perdida) presiona para no registrar recaídas, y con eso los datos faltantes dejan de ser al azar.
- **Riesgo.** Medio-bajo. Con el interruptor apagado desaparecen de la lista de relaciones algunas parejas con `n_impulso` y `med_`. Un logro propio ya creado sobre una variable privada debe seguir funcionando; solo deja de ofrecerse al crear uno nuevo.
- **Cómo se comprueba.**
  - Con el interruptor apagado, `Estadistica.variables()` no contiene ninguna variable de `esPrivada`. Prueba JVM sobre una lista sintética.
  - «Tu año» con datos sintéticos de medicamentos no los elige.
- **Capítulos.** 6 (§ qué variables entran), 9 (§ logros y campañas) y 10.

#### P-04 · Las coordenadas de momentos privados no salen hacia el clima
- **Qué cambia.** En `sensores/Entorno.kt`, en `lugarDe()` (`:32-33`) y en el punto de respaldo de `sincronizar()` (`:47`), filtrar `!it.privado` (y `!it.reservado`, según D-1).
- **Por qué.** Criterio (c). Los días sin puntos de ubicación, la app usa las coordenadas de los momentos de ese día, incluidas las recaídas (que guardan coordenadas, `data/Recaidas.kt:40`), y las envía redondeadas a Open-Meteo (H-9-6). El impacto es bajo, porque no se envía el tipo de momento, pero contradice la regla.
- **Riesgo.** Nulo para los datos. Algún día sin GPS se queda sin clima o usa la última ubicación conocida.
- **Cómo se comprueba.** Leyendo el código. En el teléfono, un día sintético con solo una recaída con coordenadas: el clima de ese día no se pide con esas coordenadas.
- **Capítulos.** 4 (§ clima) y 10 (§ servicios externos).

#### P-05 · El mapa de ánimo de R solo usa registros de ánimo
- **Qué cambia.** En `vm_mapa_animo` (R:427-431), `filter(es_animo, !is.na(valencia))` y quitar el `coalesce(valencia, v_dia)`.
- **Por qué.** Hoy entran todos los momentos con coordenadas, incluidos recaídas, libido e impulsos, pintados con la valencia del diario. `vm_guardar_graficas` guarda ese mapa como imagen (R:542). Es criterio (c), además de (a): mezcla ánimo momentáneo con el recordado. Resuelve H-2-15 en su parte de R y H-9-12.
- **Riesgo.** Nulo. Si la columna `es_animo` no existe en `vm_momentos.csv`, se deriva del tipo en R (comprobarlo en el módulo 10).
- **Cómo se comprueba.** Con un `vm_momentos.csv` sintético: el mapa no dibuja puntos de tipo `recaida`, `libido` ni `impulso`.
- **Capítulos.** 3 (§ ánimo por lugar) y apéndice C.

#### P-06 · Los reportes que se guardan solos, en modo discreto
- **Qué cambia.** Los reportes automáticos de los lunes y del día 1 (`avisos/Trabajo.kt`, `reportes/Reportes.kt`, `guardarPng`/`guardarPdf`) se generan con el modo discreto y sin la frase del mejor momento. Los que generas a mano siguen como hoy.
- **Por qué.** Criterio (c), parcial (H-8-4). Se guardan en `Pictures/` y `Download/`, al alcance de la galería, de la copia automática de fotos a la nube y de otras apps con permiso de medios, sin que lo hayas decidido.
- **Por qué no algo más fuerte.** Moverlos al almacenamiento privado exigiría un `FileProvider` nuevo; eso es un rediseño y se documenta como alternativa.
- **Riesgo.** Bajo. Los reportes automáticos pierden nombres y la frase.
- **Cómo se comprueba.** Disparar la tarea del lunes con datos sintéticos y revisar el PNG.
- **Capítulos.** 9 (§ reportes) y 10.

---

### B. Integridad de los datos (criterio d)

#### P-07 · Lo que no se puede leer no se pierde ni se borra
- **Qué cambia:**
  1. En `data/Repo.kt`, `init`. Si existe `config` pero no se puede decodificar (`:78`), copiar el texto crudo a otra fila (`db.poner("config_ilegible", ahoraIso(), null, crudo)`) **antes** de seguir con `Config()`, y mostrar un aviso. Sin esto, el siguiente `cambiarConfig()` (`:493-498`) sobrescribe el original: se pierden personas, perfil, hábitos, suplementos, campañas y el interruptor privado.
  2. En `cargar()` (`:107-108`) y en las cargas parecidas (`:395`, `:428`, `:452`, `:454`), contar los documentos que fallan por colección y mostrar ese conteo en «Estado de los datos».
- **Por qué.** Criterio (d). Resuelve H-1-3 y H-9-2.
- **Riesgo.** Bajo. No cambia el modelo.
- **Cómo se comprueba.** Prueba JVM de una función pura de decodificación que devuelva `(leídos, fallidos)` con un JSON roto. En el teléfono, la sección de diagnóstico muestra 0.
- **Capítulos.** 2 (§ almacenamiento) y 10 (§ integridad).

#### P-08 · Restaurar: revisar la versión y no dejar datos a medias
- **Qué cambia:**
  1. En `data/Importar.kt`, `respaldoTexto()`: si el `schema_v` del respaldo es mayor que `SCHEMA_V`, no restaurar y avisar. Hoy los campos desconocidos se descartan en silencio (`ignoreUnknownKeys`).
  2. Gastos e ingresos (`data/Repo.kt:240-244` y `:252-256`): que la lista en memoria y la base queden iguales después de restaurar. Hoy, al reiniciar aparece en silencio la versión del respaldo.
  3. Los experimentos (`Importar.kt:66`) dejan de sobrescribirse enteros: gana el documento con más información (el que tenga `fin`, `hecho` o una fecha de edición más reciente). Es indispensable si se aprueban P-12 e I-3, porque un respaldo viejo reactivaría un experimento terminado y borraría las respuestas de adherencia.
  4. El texto de «Verificar» (`data/Respaldos.kt`, `verificar()`) pasa de «se lee completo y no le falta nada» a «se lee completo», y muestra los conteos del respaldo junto a los del teléfono.
- **Por qué.** Criterios (d) y (a). Resuelve H-8-12, H-9-28 y H-9-33, y una parte de H-8-13. La regla única de fusión para todas las colecciones (H-8-13 completo) es un rediseño; se documenta.
- **Riesgo.** Medio. Toca la ruta de restauración, que hoy funciona. Por eso cada punto necesita su prueba.
- **Cómo se comprueba.** Pruebas JVM en `RespaldoPrueba.kt`:
  - un respaldo con `schema_v = 99` se rechaza;
  - una restauración sintética de gastos deja la lista en memoria igual a la de la base;
  - un experimento con `fin` no se pisa con uno sin `fin`.
- **Capítulos.** 10 (§ restaurar y verificar).

#### P-09 · Drive no reemplaza una copia buena por una empobrecida
- **Qué cambia:**
  1. En `data/Drive.kt`, `subir()`: si el respaldo nuevo trae menos del 90 % de los días o momentos que el último subido, no reemplazar la copia del día; guardarlo aparte y avisar.
  2. Al activar el cifrado (`Drive.kt:113`), avisar antes de borrar las copias en claro, o conservar la más reciente hasta que exista una cifrada.
- **Por qué.** Criterio (d). Resuelve H-8-14 y H-9-37. Elegir qué copia restaurar sería una función nueva; se documenta.
- **Riesgo.** Bajo. El umbral del 90 % es una decisión de diseño; borrar días a propósito en masa dispararía el aviso, y eso es lo deseable.
- **Cómo se comprueba.** Prueba JVM de la función que compara conteos. En el teléfono, con una cuenta de prueba.
- **Capítulos.** 10.

---

### C. Información que hoy no se guarda y sesga los análisis (criterios d y e)

#### P-10 · Registrar los avisos de ánimo de cada día (tu idea; cumple el criterio)
- **Qué cambia.**
  - **Dónde se guarda.** Una colección nueva `avisos` con un documento por día lógico: `AvisosDia(fecha, planeados: Int, vencidos: List<String>, mostrados: List<String>)`. Las listas guardan la hora planeada en ISO, el mismo formato que `aviso_ts` del momento, para poder unir aviso y respuesta. Se respalda y se restaura como `notifs` (`data/Repo.kt:38`, `:84`, `:358-367`; `data/Exportar.kt:17`, `:760-772`; `data/Importar.kt`).
  - **Por qué no en `Config`.** `Config` se reescribe en cada pulso y el respaldo vacía `disparos` y `plan_muestreo` a propósito (`Exportar.kt:765`, `Importar.kt:114`).
  - **Por qué no en `Dia`.** Crearía documentos del día solo por los avisos, y eso empeora 3.2.
  - **Dónde se anota**, en `avisos/Trabajo.kt`, `avisos()`:
    - al crear o regenerar el plan (`:101-103`): `planeados = p.size`;
    - al marcar uno como vencido (`:106-107`): se agrega a `vencidos`;
    - si además se muestra (`ms − t < 50 min` y `Avisos.permitido(ctx)`, `:108`): se agrega a `mostrados`.
    - Con los avisos apagados no se anota nada, y el día queda vacío, no en 0.
  - **Panel** (`ciencia/Estadistica.kt`, `panel()` y `variables()`): `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos` (los momentos `muestreo` cuyo `aviso_ts` está en `mostrados`, tengan o no valencia) y `tasa_respuesta` = respondidos ÷ mostrados. Se marcan como «obvias» entre sí y con `muestreo_valencia` y `n_muestreos`.
  - **Exportación:** las 4 columnas en `filasPanel()` y su descripción en `Diccionario.kt`. En R se agregan a las exclusiones de `vm_relaciones` y a `vm_totales`.
  - **Dos problemas vecinos que se corrigen a la vez:**
    - **H-9-9.** La tarjeta «N avisos de ánimo sin contestar» de Hoy (`ui/pantallas/Hoy.kt:105-108`, `:603-606`) abre la captura **sin** `AVISO_MS`. La respuesta se guarda como registro propio (`animo`), no como `muestreo`, y la tarjeta nunca se apaga, lo que invita a registrar varias veces. Hay que pasar la hora del aviso vencido más antiguo sin respuesta.
    - **H-9-10.** Una sola definición de «aviso respondido»: hoy `n_muestreos` de `vm_panel` cuenta solo los que tienen valencia (`Exportar.kt:615`, `:642`), y el diccionario, Explorar y los widgets cuentan todos.
- **Por qué.** Criterios (d) y (e) (H-2-3). Sin el denominador no se puede calcular la tasa de respuesta, ni ver si las respuestas faltan justo en ciertos días u horas (datos que faltan sin ser al azar), ni usarla como covariable.
- **Lo que no se puede recuperar.** Los días anteriores quedan vacíos. Reconstruirlos con `planDelDia` no es fiable: depende de la configuración de ese día y de si el pulso corrió.
- **Riesgo.** Medio. Es una colección nueva, así que **sube `SCHEMA_V`** (fase 2). Si cambias los ajustes a mitad del día, el plan se regenera y las claves ya disparadas se conservan (H-9-11): se anota `planeados` del plan nuevo y se suman los vencidos previos, y queda documentado.
- **Cómo se comprueba.**
  - Poner 6 avisos y una ventana de 2 h ya pasada: se regenera el plan con horas vencidas; todos vencen y solo se muestran los de menos de 50 minutos.
  - Exportar y verificar en R que `respondidos` sea igual al conteo de `vm_momentos` con `tipo == "muestreo"` de ese día.
  - Respaldar, restaurar y comprobar que el dato sobrevive.
- **Capítulos.** 3 (§ muestreo de experiencia y datos faltantes), apéndice A y 2 (§ colecciones).

#### P-11 · Las campañas como variable del panel (tu idea; cumple el criterio)
- **Una precisión a la idea original.** Las campañas **sí** se guardan, con toda su historia, en `Config.campanas` (`data/Modelo.kt:210`, `:674`): `Campana(id, lunes, reto, meta, tema, clave)`. Las pasadas se pueden reconstruir por semanas completas. Lo que **no** se guarda:
  - el día en que la aceptaste (el id es aleatorio);
  - las abandonadas, porque «abandonar» las borra (`ui/pantallas/Campanas.kt:163`; H-9-36);
  - el avance, que se recalcula cada vez (`estado()`).

  Puede haber hasta 3 campañas a la vez (`Campanas.kt:168`), así que una sola variable categórica no sirve.
- **Qué cambia.**
  - **Paso A (fase 1, sin subir el esquema):**
    1. Una función pura `Campanas.diasActivos()` que devuelva, por fecha, los retos vigentes.
    2. En el panel: `campana_<reto>` (el reto de un hábito como `campana_hab_<id>`), 0/1 desde la primera campaña y vacío antes, más `campanas_n`. Codifica la **asignación**, no el cumplimiento.
    3. Una regla en `obvia()` que junte cada `campana_<reto>` con las variables que ese reto busca mover. El mapa reto → variables lo arma el módulo 10 desde `ciencia/Campanas.kt`.
    4. Exportación: las columnas en `filasPanel()`, una rama `campana_` en `Diccionario.describir` y la regla equivalente en `vm_obvia` de R.
  - **Paso B (fase 2, sube el esquema):**
    1. `Campana` gana `aceptada: String?` y `abandonada: String?`. «Abandonar» marca la fecha en vez de borrar; `diasActivos` corta en ella.
    2. Una campaña aceptada a mitad de semana deja de contar los días anteriores a la aceptación (H-9-34: hoy se puede ganar en el acto de aceptarla).
    3. Desactivar un hábito deja de anular las campañas ya ganadas y sus logros (H-9-35).
- **Por qué.** Criterio (e) (H-8-7). Una semana de campaña de sueño o de pasos es una intervención. Sin la variable, las correlaciones de esa conducta pueden deberse a la campaña, o a la razón por la que se eligió esa semana, y no hay forma de controlarlo.
- **Riesgo.** Bajo en el paso A. En el paso B, los respaldos viejos se siguen leyendo porque los campos nuevos tienen valor por omisión.
- **Lo que hay que documentar.**
  - Una campaña es **autoelegida**: no es un experimento.
  - Varias semanas seguidas de la misma campaña pueden clasificarse como variable «lenta» y quedar fuera en el modo sin tendencia.
  - Las campañas de avisos o de diario cambian cuántos datos faltan.
- **Cómo se comprueba.**
  - Prueba JVM de `diasActivos` con tres campañas sintéticas, una de ellas abandonada.
  - En R, unir `vm_panel` con las campañas leídas de `vm_respaldo.json` y comprobar que coinciden.
- **Capítulos.** 9 (§ campañas) y 6 (§ obvias).

#### P-12 · Un experimento terminado guarda su fecha de fin
- **Qué cambia.**
  - `Experimento` gana `fin: String?`.
  - «Terminar» (`ui/pantallas/Lote3.kt:490`) guarda el día lógico.
  - `Experimentos.resultado()` (`data/Lote3.kt:329-341`) corta en `fin`.
  - La tarjeta deja de decir «SÍ toca» después de terminar.
  - «En curso», en Análisis (`ui/pantallas/Explorar.kt:104`, `:456`), excluye los que tienen `fin` o el plan ya vencido.
  - La exportación lleva la columna `fin`, con su entrada `vm_experimentos.csv:fin` en el diccionario.
- **Por qué.** Criterios (a) y (d). Resuelve H-7-2 y H-9-29. Hoy, al terminar antes de tiempo, las semanas «SÍ» que quedaban, sin la acción, se cuentan como «con la acción».
- **Riesgo.** Bajo. Sube `SCHEMA_V` (fase 2). Necesita el punto 3 de P-08.
- **Cómo se comprueba.** Prueba JVM de `resultado` con un plan sintético terminado en la semana 3.
- **Capítulos.** 8.

#### P-13 · `retroactivo` según cuándo se llenó la valencia
- **Qué cambia.**
  - `Dia` gana `ts_valencia: String?`.
  - `guardarDia()` (`data/Repo.kt:117-125`) le pone la hora actual cuando la valencia pasa de vacía a llena o cambia, y la conserva si no.
  - `retroactivo = diaLogico(ts_valencia) > fecha`. Los días viejos sin `ts_valencia` conservan su valor actual.
  - Va con su columna en `filasDias()` y en el diccionario.
- **Por qué.** Criterios (a) y (d) (H-2-2). Hoy, si el documento del día lo crea otra cosa ese mismo día (el sueño desde la captura rápida, un hábito desde el widget, una recaída), el día queda con `retroactivo = false` para siempre, aunque la valencia se llene días después. Es justo la variable pensada para controlar el sesgo de recuerdo.
- **Riesgo.** Bajo. Sube `SCHEMA_V` (fase 2).
- **Cómo se comprueba.** Marcar un hábito hoy, poner la valencia mañana y exportar: `retroactivo = TRUE`.
- **Capítulos.** 3 (§ sesgo de recuerdo) y apéndice A.

---

### D. Cálculos que engañan o son incorrectos (criterio a)

#### P-14 · Los cuestionarios, solo en las semanas que preguntan
- **Qué cambia.** En `ciencia/Dinamicas.kt`, sección «Cuestionarios»:
  - `ventana` igual al periodo realmente preguntado (`:215`): 7 días para WHO-5 y PSS-4, 30 para UCLA-3. Coincide con `frecuenciaDias` de `ciencia/Escalas.kt`.
  - En el bucle de `:224`, poner el valor solo si ese día aún no lo tiene. Como las respuestas se recorren de la más vieja a la más nueva, cada día queda con la primera respuesta que lo cubre, en vez de con la última.
  - Corregir el comentario de `:211`.
- **Por qué.** Criterio (a), gravedad alta (H-2-1). Con un PSS-4 semanal, hoy todos los días de un mes quedan con el último puntaje, que puede venir de hasta 29 días después. Comprobado con el ejemplo sintético del hallazgo: respuestas 4, 6, 9, 5 y 7 los días 1, 8, 15, 22 y 29 dan hoy 7 en todo el mes; con el cambio, 4, 6, 9, 5 y 7 en sus semanas.
- **Riesgo.** Bajo. No cambia lo guardado. Cambian los `esc_*` del panel extendido, y las semanas sin respuesta quedan vacías, que es lo correcto.
- **Cómo se comprueba.** Prueba JVM con el ejemplo de arriba. En R, cada `esc_who5_*` es el puntaje de la primera respuesta en [día, día + 6].
- **Capítulos.** 3 (§ cuestionarios) y apéndice A.

#### P-15 · Una sola regla para «cero» y «sin dato»
- **Qué cambia** en `ciencia/Estadistica.kt`, `panel()` (y lo mismo en la exportación, vía P-30):
  1. **Desde el primer registro de su fuente:** `gasto_total`, `gasto_imprevisto` (`:289`, `:337-338`), `n_impulso` (`:290`) y `escalada` (`:416`). Antes de esa fecha, vacío. `gimnasio` pasa de «cualquier fecha con datos» (`:447`) a la misma regla.
  2. **`hab_<id>`** vacío antes de `Habito.desde` (`:288`; el campo ya existe en `data/Modelo.kt:357` y no se usa), también en `Exportar.kt:101`.
  3. **`sueno_recordado`** vacío salvo que el día tenga algún dato del sueño (calidad, texto o la propia marca) (`:277`).
  4. **Las variables sociales** (`vi_*`, `cercanos`, `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `penso_en_*`, `personas`) vacías los días sin texto de diario y sin momentos no privados con @ (`:280`, `:497-516`); la base `diasDiario` de `per_`, `pen_` y `tema_` (`Dinamicas.kt:104`), igual.
  5. **`ejercicio_min`** en 0, no vacío, los días con datos del reloj y sin sesión (`:408`). **`distancia_km`** en 0 los días con ubicaciones y sin movimiento.
  6. **`probioticos_estimado`** vacío los días sin bitácora de MacroFactor (`data/Lote3.kt:87-97`).
  7. **`presencias_revisado`** (0/1) entra al panel como covariable (H-1-6); ya va a R en `vm_dias.csv`.
  8. **Opcional:** con «las tres» necesidades, poner 1 en `autonomia`, `competencia` y `relacion` (H-2-4).

  Para que no vuelva a divergir, se escribe una función `ceroDesde(fuente)` común.
- **Por qué.** Criterio (a). Resuelve H-1-1, H-2-4, H-4-8, H-6-13 y H-9-16 (sección 3.2).
- **Lo que se queda como está.** Los contextos `ctx_*` siguen en 0 cuando no se marcan: es la convención declarada en la pantalla (`ui/pantallas/Dia.kt:496`). Se documenta.
- **Riesgo.** Medio. Cambian medias, frecuencias y correlaciones de muchas variables, en la dirección correcta, y aparecen más vacíos en la exportación. Los logros propios sobre `hab_*` pueden cambiar su progreso.
- **Cómo se comprueba.** Prueba JVM de `ceroDesde` con fuentes sintéticas. En el teléfono, comparar la cobertura por variable (`CoberturaVariables`) antes y después.
- **Capítulos.** 2 (§ panel), 6 (§ construcción del panel), 7 (§ variables sociales) y apéndice A.

#### P-16 · El día lógico, de verdad y dicho con honestidad (D-3)
- **Qué cambia:**
  - **(i) Fase 1:**
    - `lugares_distintos` agrupa por `Fechas.diaLogicoDeMillis(Fechas.millis(entrada))` (`ciencia/Estadistica.kt:351`). La propuesta original incluía otras fuentes, pero `reacciones`, `gastos.dia` y `kmPorDia` ya usan el día lógico.
    - `LEEME.txt` (`data/Exportar.kt:861`) y el diccionario dicen, por variable, qué ventana usa: calendario o día lógico.
    - `primer_uso_h` y `ultimo_uso_h` usan la misma regla de +24 en la app y en R (ver P-30).
  - **(ii) Fase 2, si no la tachas:** pantalla (`sensores/Pantalla.kt`) y pasos (`sensores/Salud.kt`) calculan sobre el día lógico de aquí en adelante. Cada día guarda `ventana = "logica"` o `"calendario"`, para que R pueda separar los dos tramos; los días viejos se quedan con `"calendario"`.
- **Por qué.** Criterios (a) y (e). Resuelve H-1-2, H-3-1, H-3-2 y H-9-4.
- **Riesgo.**
  - (i) es nulo.
  - (ii) **parte la serie**: el primer uso de una madrugada de 01:00 deja de aparecer como «primer uso del día» y pasa al día anterior. Las correlaciones que crucen el corte deben leerse por tramo. Sube `SCHEMA_V`.
- **Cómo se comprueba.** Prueba JVM de la ventana con una sesión sintética de 01:00 a 01:30. En el teléfono, comparar un día con madrugada.
- **Capítulos.** 2 (§ día lógico), 4 (§ pantalla y salud) y apéndice A.

#### P-17 · Intervalos y p que cuentan bien los días
- **Qué cambia.** Dos piezas nuevas en `ciencia/Estadistica.kt`:
  - `cuantilT(p, gl)`: el cuantil de la *t* de Student, con una aproximación numérica pequeña.
  - Una versión pública de `autocorr()`, para calcular el factor AR(1): √((1 + ρ)/(1 − ρ)).

  Se aplican en:
  1. **`diferencia()`** (`:74-82`): grados de libertad de Welch, y el error estándar multiplicado por el factor AR(1) de los residuos de cada grupo. Lo heredan la Tendencia de 30 contra 30 días, los experimentos y «Antes y después».
  2. **`Analisis.factores()`** (`ciencia/Analisis.kt:79-107`), la pantalla de «Todo lo que se mueve con algo»: los mismos restos sin tendencia que la lista (según el interruptor) y el p con nEf. Para las variables 0/1, la diferencia de medias con el factor AR(1). Lo mismo en `vm_factores` de R.
  3. **«Cruzar dos variables»** (`ui/pantallas/Explorar.kt:147-165`): el intervalo con nEf, y el r sin tendencia junto al bruto.
  4. **`Analisis.modelo()`** (`:138`): *t* con n − k − 1 grados de libertad y R² ajustado.
  5. **`porDiaSemana`**: mínimo de 5 días por día de la semana y *t* con n − 1 grados de libertad.
- **Por qué.** Criterio (a). Resuelve H-5-3 (alta), H-5-4, H-5-5, H-5-11 y H-7-3 (alta). Simulaciones de esta revisión:
  - con 3 + 3 días, el intervalo «de 95 %» cubre en realidad el 87.8 %;
  - con 30 + 30 días autocorrelacionados (ρ = 0.5), el 73.5 %;
  - en «Todo lo que se mueve», con ρ = 0.8, hay 32.5 % de falsas alarmas;
  - en los experimentos, el cambio baja las falsas señales del 15–24 % a alrededor del 8 %.
- **Riesgo.** Medio. Cambian muchas pantallas y algunas relaciones dejan de verse como «claras», que es justo lo buscado. La aproximación de `cuantilT` necesita prueba.
- **Cómo se comprueba.**
  - Pruebas JVM: `cuantilT(0.975, 2) ≈ 4.303`, `(0.975, 5) ≈ 2.571` y `(0.975, 30) ≈ 2.042`.
  - `diferencia()` con datos sintéticos conocidos.
  - Repetir en Kotlin, como prueba, la simulación de cobertura con semilla fija (por ejemplo, 2000 réplicas): debe dar entre 93 % y 97 %.
- **Capítulos.** 6 (§ intervalos y n efectivo), 8 (§ análisis del experimento), apéndices B y C.

#### P-18 · Experimentos que pueden dar resultado y que no se leen antes de tiempo
- **Qué cambia** en `data/Lote3.kt` (`Experimentos`) y en `ui/pantallas/Lote3.kt`:
  1. **Lavado (H-7-1):**
     - Antes de sellar, contar los días que cuentan en cada condición (`toca` + `enLavado`) y no dejar sellar si alguna queda con menos de 7.
     - La plantilla «Creatina» pasa a 12 semanas: quedan 14 días con la acción y 42 sin ella. Hoy da 0 días con la acción y nunca produce resultado.
  2. **Sorteo por pares de semanas (H-7-5)** (`ui/pantallas/Lote3.kt:451`): en cada par, una «SÍ» y una «no», en orden al azar. Con tendencia y sin efecto real, las falsas señales bajan del 14.2 % al 5.4 %.
  3. **La prueba de tendencia, dentro de cada condición (H-7-6):** la pendiente se calcula dentro de cada condición, o en el modelo valor ~ día + condición. Hoy, con un efecto real y sin tendencia, el aviso de «venía cambiando sola» sale en el 71 % de los antes/después.
  4. **El aviso de diseño en bloque** depende del plan real, no de `e.diseno` (H-9-30). Un sorteo libre que salió «SÍ SÍ SÍ no no no» recibe el aviso fuerte.
  5. **Mientras dure el plan,** el resultado se muestra como «provisional» y sin la frase de «señal» (H-7-4). Detenerse en la primera señal da 28 % de falsas señales con días independientes y 52 % con ρ = 0.3.
  6. **El día de hoy no entra en `resultado`** mientras no haya terminado (H-9-31): las variables que se acumulan (pantalla, pasos, enfoque) estarían a medias.
  7. **El mensaje «Aún no hay suficientes días»** distingue tres casos: falta esperar; el plan nunca dará resultado; el experimento ya terminó (H-9-32).
  8. **El aviso del Día** no muestra «semana 1» antes del inicio (H-7-9; `ui/pantallas/Dia.kt:322`): usar `Experimentos.toca`, que devuelve vacío antes del inicio.
  9. **El rótulo «Con la acción / sin ella»** pasa a «Semanas con la acción según el plan / sin ella» (`ui/pantallas/Lote3.kt:482`; H-7-8, criterio a débil).
  10. **En «Antes y después» (H-7-12),** los cambios de un mismo guardado se agrupan en una sola tarjeta. Hoy salen tarjetas repetidas con los mismos números.
- **Por qué.** Criterio (a).
- **Lo que no se cambia.** La corrección de Hedges (H-7-7) no cumple el criterio: se documenta.
- **Riesgo.** Medio. Los experimentos ya sellados conservan su plan. Las reglas nuevas (1 y 2) solo aplican a los nuevos.
- **Cómo se comprueba.**
  - Pruebas JVM: el plan de «Creatina» con 12 semanas deja 14 + 42 días.
  - Un sorteo por pares nunca produce más de dos semanas iguales seguidas.
  - `resultado` no incluye hoy.
  - Capturas de Paparazzi `l3ExperimentosSeda` y `diaV73Romano`.
- **Capítulos.** 8.

#### P-19 · El ritmo de sueño, con las noches recientes y una sola regla de día libre
- **Qué cambia:**
  1. **Ventana reciente.** `Analisis.ritmo()` (`ciencia/Analisis.kt:197-219`) calcula los promedios, el jet lag social y la regularidad con las noches de los últimos 56 días. Los umbrales (5 noches; 7 del reloj para no usar el teléfono) se aplican dentro de esa ventana. `vm_ritmo` en R gana el argumento `dias = 56`.
  2. **Una sola función** `Fechas.esDiaLibre(fecha)` para decidir qué noche es libre. Hoy la regla está repetida en 5 sitios: `Analisis.kt:212`, `ui/pantallas/Analisis.kt:368` y R:103, 305 y 436. En R, la función equivalente lee la columna `dia_libre` del panel (P-30), para que app y R no se separen. Esto prepara I-1.
  3. **El texto «Sin datos del reloj»** (`ui/pantallas/Analisis.kt:348`) cambia cuando hay de 1 a 6 noches del reloj mezcladas con las del teléfono.
- **Por qué.** Criterio (a) (H-4-9, H-4-10 y H-9-15). La pantalla pone «te duermes», «jet lag social» y «regularidad» junto a la gráfica de 30 noches, pero los calcula con toda la historia.
- **Riesgo.** Bajo. Las cifras pasan a describir tu presente.
- **Cómo se comprueba.** Prueba JVM: 60 noches viejas con 3 h de jet lag y 60 recientes con 1 h. Hoy da cerca de 2 h; con el cambio, cerca de 1 h.
- **Capítulos.** 5 (§ jet lag social) y apéndice C.

#### P-20 · Sustancias: el peso correcto y las tomas que se pierden
- **Qué cambia** en `data/Sustancias.kt`:
  1. **`pesoEn()`** (`:136-140`) toma el peso más reciente **entre las dos fuentes**: la báscula de salud y la tendencia de MacroFactor. Si no hay ninguno anterior a la fecha, usa el primero posterior; solo si no existe ningún peso, usa 70 kg, y el diccionario lo dice (`Diccionario.kt:61`, `:75`).
     - Hoy una pesada de hace años en la báscula le gana a un peso de MacroFactor de ayer, y antes de la primera pesada se usan 70 kg aunque haya pesos posteriores.
     - Es el error de mayor tamaño del modelo de alcohol: con 90 kg reales en vez de 70, el máximo pasa de 0.411 a 0.607 g/L (+48 %) (H-9-24).
  2. **Las tomas de MacroFactor sin hora legible** (`:101-103`, `:111-112`) se ponen al mediodía de su día en vez de perderse. Hoy desaparecen de `cafeina_total_mg` y `alcohol_bebidas`, aunque siguen en `cafeina_mg` y `alcohol_g` (H-9-13).
  3. **`alcohol` (0/1)** sigue una sola regla de día (`ciencia/Estadistica.kt:606` frente a `:637`): el día lógico de cada dosis (H-9-17).
- **Por qué.** Criterio (a) (H-4-4 en su parte del peso, H-9-13, H-9-17).
- **Riesgo.** Bajo. No cambia lo guardado.
- **Cómo se comprueba.** Prueba JVM: báscula de 80 kg el 2024-01-01 y tendencia de 70 kg el 2026-01-01. Hoy `pesoEn("2026-02-01")` da 80; debe dar 70.
- **Capítulos.** 5 (§ alcohol y cafeína).

#### P-21 · La misma variable vale lo mismo en todas las pantallas
- **Qué cambia:**
  1. El panel de Experimentos (`ui/pantallas/Lote3.kt:415`) se calcula en segundo plano (`produceState` + `Dispatchers.Default`), como ya hace `ui/pantallas/Analisis.kt:94`. Lo mismo `ui/pantallas/Lote2.kt:103` y `ui/pantallas/Logros.kt:333`.
  2. Como salvaguarda, en el hilo principal sin caché, las variables que dependen de MacroFactor quedan vacías en vez de dar otra cifra (H-4-3).
  3. La clave de la caché de dosis (`ciencia/Estadistica.kt:621`) incluye la versión de las comidas, no solo el número de días (H-9-14).
  4. La clave del panel de Experimentos deja de depender solo de `Repo.dias.size` (H-7-13), o desaparece al pasar a segundo plano.
- **Por qué.** Criterio (a). Hoy `cafeina_total_mg` o `alcohol_*` pueden valer distinto en Experimentos que en Explorar el mismo día.
- **Riesgo.** Bajo, de interfaz. Hay que revisar las capturas.
- **Cómo se comprueba.** Capturas de Paparazzi y, en el teléfono, comparar la misma variable en las dos pantallas.
- **Capítulos.** 5 (§ cafeína) y 6.

#### P-22 · Una sola repetición máxima estimada
- **Qué cambia.** `ciencia/Periodo.kt:107` y `ui/pantallas/TusApps.kt:261-262` usan `Macrofactor.e1rm` (1 a 12 repeticiones, sin calentamientos). Es la que ya usan la exportación, `vm_fuerza`, Fuerza y Totales.
- **Por qué.** Criterio (b) (H-4-13). La «mejor marca» del reporte no coincide con la de R.
- **Riesgo.** Nulo para los datos.
- **Cómo se comprueba.** Prueba JVM: series de 100×15 y 120×5 más un calentamiento de 130×8. Hoy `Periodo` da 164.7; debe dar 140.
- **Capítulos.** 5 y 9.

#### P-23 · «Tus números»: el ejercicio semanal bien contado
- **Qué cambia.** En `ciencia/TusNumeros.kt:84`, `ejercicio_sem` = la suma de los minutos de las últimas 4 semanas, dividida entre 4, como ya hace `fuerza_sem`.
- **Por qué.** Criterio (a) (H-5-9). Hoy se promedian solo los días con sesión y se multiplica por 7: tres sesiones de 45 min por semana dan 315 min/semana en vez de 135, y eso se compara con la recomendación de 150 a 300.
- **Lo que no se cambia.** Lo del aire libre se descarta como error: la etiqueta ya dice que es la luz que mide el teléfono.
- **Riesgo.** Nulo.
- **Cómo se comprueba.** Prueba JVM con el ejemplo de arriba.
- **Capítulos.** 6 (§ Tus números).

#### P-24 · Los días atípicos revisan la distancia
- **Qué cambia.** En `ciencia/Analisis.kt:258` (`atipicos()`), `"km"` → `"distancia_km"`.
- **Por qué.** Criterio (a) (H-5-8). La distancia nunca se revisa, así que los viajes no salen como días atípicos.
- **Riesgo.** Nulo.
- **Cómo se comprueba.** Un día sintético con 300 km sale en la lista.
- **Capítulos.** 6.

#### P-25 · Personas y red: nombres unidos de verdad y dibujos que cuadran
- **Qué cambia:**
  1. **`Texto.canon`** (`data/Texto.kt:14`) sigue la cadena de equivalencias hasta 8 pasos y corta los ciclos (H-6-11). Hoy `a → b` y `b → c` dejan a `a` como `b`, y los ciclos `a ↔ b` nunca unen.
  2. **`Perfiles.relacion`** (`data/Perfiles.kt:91-92`) y `vi_convivientes` (`ciencia/Estadistica.kt:508`) comparan con `canon` (H-6-4).
  3. **La Red** colorea por `Personas.grupo(relación)` (`ui/pantallas/Lote3.kt:344`). `vm_red_nodos.csv` gana la columna `grupo`, y R usa esa columna para la composición y el color (H-6-5).
  4. **Con «Solo lo que dije»,** el grado y los grupos se recalculan con las líneas filtradas (`data/Lote3.kt:249`, `ui/pantallas/Lote3.kt:337`, `:384`) (H-6-6).
  5. **Los vínculos declarados** pasan por `canon` (`data/Lote3.kt:206-209`, `:230-231`) (H-9-22).
  6. **`AGREGADOS["per_"]`** (`ciencia/Dinamicas.kt:22`) incluye `personas_vistas`, `personas_pensadas`, `personas_mensajes`, `vi_expareja`, `vi_trabajo`, `cercania_media` y `penso_en_*` (H-6-10).
  7. **La cercanía se puede borrar,** y el paso de «sin cercanía» a un valor queda en el historial (`data/Personas.kt:45-46`) (H-6-12).
  8. **Las tarjetas de personas** de `Explorar.kt:458` y `Vida.kt:81` aplican `canon` (H-6-9).
  9. **Las etiquetas del sueño sin símbolo** dejan de contar como personas en `per_` (`ciencia/Dinamicas.kt:114`): solo cuentan las que empiezan con @ (H-9-21).
- **Por qué.** Criterios (a), (b) y (d).
- **Riesgo.** Bajo. Alguna persona que estaba partida en dos se une; cambian algunas columnas `per_`.
- **Cómo se comprueba.** Pruebas JVM de `canon` (cadena y ciclo) y de `componentes` con aristas filtradas.
- **Capítulos.** 7.

#### P-26 · Estado subjetivo: cuatro arreglos pequeños y tres comentarios
- **Qué cambia:**
  1. **El cierre de un día pasado (H-2-8).**
     - En `panel()` (`ciencia/Estadistica.kt:398`, y en `animo_valencia` y `registros_animo`, `:302-305`), solo cuentan los cierres con `diaLogico(ts) == fecha`. Esto arregla también los datos viejos.
     - `BloqueCierre` (`ui/pantallas/Lote2.kt:503`) solo se ofrece el día lógico actual.
  2. **El primer guardado de «Conocerte»** deja historial (`CambioPerfil(hoy, campo, "", valor)`) y `valorEn()` no asigna etapa antes de esa fecha (`ciencia/Dinamicas.kt:239-240`, `:248`, `:254`) (H-2-5). Los perfiles ya llenados no tienen arreglo: se documenta.
  3. **La prueba de reacción:** un toque a menos de 100 ms del estímulo cuenta como anticipación y se repite el intento (`ui/pantallas/Medir.kt:181-182`) (H-2-12).
  4. **El ánimo por lugar** (`ui/pantallas/Medir.kt`) usa un solo criterio de pertenencia en la lista y en el mapa (lugar **o** distancia ≤ radio), y el texto «Últimos 60 días» se corrige, porque las medias usan toda la historia (H-2-15).
  5. **Comentarios que dicen algo falso:**
     - `data/Politica.kt:4-5` (H-2-6): estar de acuerdo con todo sí empuja.
     - `avisos/Trabajo.kt`, `planDelDia` (H-2-7): la separación mínima es 2 × min(45 min, tramo/4), y puede bajar a 10 min.
     - `ui/pantallas/Constelacion.kt:109` (H-6-14): el ángulo no es fijo por nombre.
  6. **El texto «se marcará como escrito por adelantado»** (`ui/pantallas/Dia.kt:291`) se cambia por uno que no prometa una marca inexistente (H-2-14). La marca se puede derivar en R con `diaLogico(ts_creado) < fecha`.
- **Por qué.** Criterio (a).
- **Riesgo.** Nulo o bajo.
- **Cómo se comprueba.** Prueba JVM del filtro de cierres. Capturas del Día y de Medir.
- **Capítulos.** 3.

#### P-27 · Las rachas sin recaída cuentan solo los días respondidos
- **Qué cambia.** En `data/Recaidas.kt`, `resumen()`: un día cuenta como limpio solo si hay respuesta «no» ese día; los días sin respuesta se muestran aparte. Se reescribe como función pura.
- **Por qué.** Criterio (a) (H-8-8). Si los días sin registro son los difíciles, la racha se infla.
- **Riesgo.** Bajo. Las rachas mostradas pueden acortarse.
- **Cómo se comprueba.** Prueba JVM con una secuencia sintética: sí, no, vacío, no.
- **Capítulos.** 10 (§ seguimiento privado; solo la mecánica).

#### P-28 · Textos, etiquetas y diccionario que dicen algo falso
- **Qué cambia:**
  1. **`Diccionario.kt`:**
     - Las claves repetidas pasan a la forma `"vm_x.csv:columna"`, que `describir` ya busca primero (`:266`). Hoy gana la última clave de un `mapOf`, y por eso:
       - `vm_escalada_bloques.grado` se describe como «Con cuántas personas está unida en tu red»;
       - `vm_momentos.antes`, como «Cómo estaba»;
       - `dia_logico`, en todas las tablas, como un gasto;
       - además chocan `categoria`, `minutos` y `cantidad` (H-9-20).
     - Las columnas de `vm_experimentos.csv` se describen: hoy `accion` recibe la descripción de Google (H-9-25).
     - `distancia_km` se separa en «GPS» (panel) y «Health Connect» (`vm_salud.csv`) (H-9-8).
     - `n_momentos` aclara que incluye recaídas y libido (H-9-19).
  2. **`LEEME.txt`** dice qué variables usan el día de calendario (P-16 (i)).
  3. **Etiquetas de la interfaz:**
     - el interruptor del modelo múltiple: «Controlar por el valor del día anterior» (`ui/pantallas/Analisis.kt:286`) (H-5-11);
     - el aviso de potencia de Explorar se matiza: 90 días bastan para una relación moderada en **una** pareja; en la lista completa, solo para las fuertes (H-5-16);
     - la etiqueta de `per_` menciona el mejor y el peor momento (H-6-2).
- **Por qué.** Criterio (a), de documentación.
- **Riesgo.** Nulo.
- **Cómo se comprueba.** Prueba JVM: ninguna clave del diccionario aparece dos veces con descripciones distintas.
- **Capítulos.** 2 y apéndice A.

#### P-29 · Reportes y Totales con las mismas fechas y el mismo ánimo que el panel
- **Qué cambia:**
  - En `ciencia/Periodo.kt`:
    - `horaDec` se sustituye por `Exportar.horaDecimal`, que usa un corte de 12 h; hoy `horaDec` usa 15 h;
    - los ingresos se cuentan por `dia`, no por `fecha` (`:199`);
    - `valDe()` usa solo la valencia del diario y dice cuántos días vienen de los avisos.
  - En `ciencia/Totales.kt:130-131`, gastos e ingresos por `dia`.
- **Por qué.** Criterios (b) y (a) (H-5-15 y H-8-10). El ánimo medio del reporte no coincide con `mean(panel$valencia)` en R, y un ingreso de madrugada cae en otro día que su gasto.
- **Riesgo.** Bajo. Cambian ligeramente las cifras de los reportes.
- **Cómo se comprueba.** Comparar el ánimo medio de una semana sintética en el reporte y en R.
- **Capítulos.** 9.

---

### E. La app y R (criterio b)

#### P-30 · R analiza el mismo panel que la app
- **Qué cambia:**
  1. **`vestigium.R`, `vm_archivos`** (R:77-88): carga `vm_panel_extendido.csv`, y las funciones de análisis lo usan por omisión. `vm_panel.csv` se queda como tabla de compatibilidad.
  2. **`data/Exportar.kt`, `filasPanel()`:** en las columnas que comparten nombre, el valor es el del panel de la app. Se agregan a la lista `nuevas` (`:626-633`, `:680`): `gasto_total`, `ingreso_total`, `escalada`, `gimnasio`, `canciones` y `ultimo_uso_h`. Así heredan la regla de P-15 y el +24 con corte en 5. `ruido_db_medio` se toma del `ruido_db` de la app.
  3. **`canciones` en la app** (`ciencia/Estadistica.kt:464`): filtrar escuchas de 30 s o más (`(it.ms ?: 30_000) >= 30_000`), que es lo que promete el diccionario (H-9-3).
  4. **La columna `dia_libre`** en el panel exportado (P-19). R deja de calcular su propia regla.
  5. **Los 11 nombres distintos** se documentan en una tabla de equivalencias del diccionario. No se renombran, para no romper scripts viejos.
- **Por qué.** Criterios (b), (e) y (a). Resuelve H-5-1 (alta), H-1-1 en la exportación, H-9-3 y H-9-4.
- **Riesgo.** Medio. Cambian las cifras de `vm_panel.csv` en columnas que ya existían. No sube `SCHEMA_V` (no cambia lo guardado), pero sí el contenido de la exportación; `LEEME.txt` lo dice.
- **Cómo se comprueba.**
  - Una exportación sintética: cada columna común a `vm_panel_extendido.csv` y `Estadistica.panel()` es idéntica. Prueba JVM sobre `filasPanel` con un `Repo` sintético, si es factible; si no, en el teléfono, con R.
  - `vm_relaciones()` y «Las relaciones más marcadas» dan las mismas 10 primeras parejas con los mismos r, salvo redondeo.
- **Capítulos.** 2 (§ exportación), 6 (§ app frente a R), apéndices A y C.

#### P-31 · R esconde las mismas relaciones obvias que la app
- **Qué cambia.**
  - La exportación escribe `vm_familias.csv` (familia, variable), generado desde `FAMILIAS` y `AGREGADOS` de la app con los nombres del panel exportado.
  - `vm_obvia` (R) lo lee y aplica las mismas reglas extra de `obvia()` (`ciencia/Estadistica.kt:752-770`): mismo prefijo dinámico, misma persona, mismo cuestionario, `n_` de sentimientos entre sí y con `registros_animo`, suplementos y lugares.
- **Por qué.** Criterio (b) (H-5-2). En R hoy pueden salir como «firmes» parejas triviales, por ejemplo `valencia` con `animo_valencia`, y BH corrige sobre otro número de pruebas.
- **Riesgo.** Bajo.
- **Cómo se comprueba.** El número de pruebas de `vm_relaciones()` coincide con el que reporta la app para el mismo panel.
- **Capítulos.** 6 y apéndice C.

#### P-32 · Otras funciones de R alineadas con la app
- **Qué cambia en R:**
  1. `vm_palabras`: la misma lista de palabras vacías que la app (167) y la misma media de referencia, los días con nota (R:322) (H-5-10).
  2. `vm_relaciones` y `vm_factores`: excluyen `fin_de_semana`, `dia_libre`, `con_diario` y `n_muestreos` (R:103, 197) (H-9-18).
  3. `vm_red()`:
     - excluye los pares «no_se_conocen» también en la rama derivada (R:492);
     - cuenta los grupos sin los aislados: `sum(components(g)$csize > 1)` (R:499);
     - no falla si no hay ningún par (R:491-493) (H-6-7 en sus incisos a y b, H-9-23).
     - Los incisos c y d de H-6-7 (45 frente a 400 personas; grado con 1 frente a 2 días) se documentan en el diccionario.
  4. `vm_mapa_animo`: ver P-05.
- **Por qué.** Criterio (b).
- **Riesgo.** Nulo para la app.
- **Cómo se comprueba.** CSV sintéticos. R no corre en CI: el módulo 10 lo prueba a mano y lo dice.
- **Capítulos.** Apéndice C, 6 y 7.

---

### F. Sensores e importaciones (criterios a y d, menores)

#### P-33 · Cuatro arreglos de captura
- **Qué cambia:**
  1. **Barómetro** (`sensores/Percepcion.kt:70`): el filtro 850–1090 hPa pasa a 300–1100 hPa. Que ese sea el rango típico de los sensores de teléfono **no se comprobó** en esta sesión; el módulo 10 debe verificarlo o usar un filtro relativo a la mediana local. Por encima de unos 1 450 m, hoy se descartan todas las lecturas (H-3-3). No conozco tu altitud: si vives o viajas por debajo, el cambio no tiene efecto.
  2. **Cold Turkey** (`data/Lote3.kt`, `ColdTurkey`): comparar por dominio exacto o por sufijo de dominio, en vez de «contiene». Así «x.com» deja de coincidir con «dropbox.com» (H-3-6). Las correcciones manuales siguen teniendo prioridad.
  3. **Puntos sin precisión** (`data/Lugares.kt:23`, `data/Db.kt:131`): se les asigna una precisión prudente (por ejemplo, 100 m) en vez de 0 m. La importación del formato viejo de Takeout (`data/Importar.kt:274-280`) conserva `accuracy` (H-3-8, H-9-7).
  4. **Importaciones de Obsidian y de la versión web** (`data/Importar.kt:150-158`, `:225`, `:231`): no ponen como `ts_creado`/`ts_editado` la hora de importación ni `schema_v` = 12; dejan esos campos vacíos y el `origen` dice de dónde vino (H-1-9, H-9-5).
- **Por qué.** Criterios (a) y (d).
- **Riesgo.** Bajo. Con el filtro barométrico nuevo pueden aparecer lecturas que antes se descartaban.
- **Cómo se comprueba.** Pruebas JVM del comparador de dominios y del filtro.
- **Capítulos.** 4 y 2.

---

## 5. Tus ideas, evaluadas

### I-1 · Días festivos automáticos (México) — **fuera del criterio estricto; recomendado con alcance mínimo**

**Qué está bien en la idea:**
- `ritmo()` decide los días libres solo por el día de la semana (`ciencia/Analisis.kt:212`). Las noches libres son las que terminan en sábado o domingo, que coincide con la regla del MCTQ: el sueño de la víspera de un día libre.
- Un lunes festivo es libre, así que la noche del domingo al lunes debería contar como libre, y hoy cuenta como laboral.
- No existe en todo el código ninguna noción de festivo, vacaciones ni día libre. Las piezas cercanas (`Perfil.horario`, el contexto `ctx_trabajo` y la agenda) no sirven: no tienen fecha por día o son manuales.

**Por qué no cumple el criterio.** El cálculo actual no es incorrecto: es la definición declarada de «día libre». Agregar festivos es una función nueva. Su efecto sobre el jet lag social es pequeño: unos 7 días hábiles al año, el 2.8 % de las noches laborales, que mueven el punto medio laboral unos 4 minutos. El error grande de esa pantalla es otro, la ventana de noches, y lo corrige P-19. Las **vacaciones** (que en un estudiante pueden ser el 24 % de las noches, con un sesgo de unas 0.6 h) no las arregla ningún calendario fijo.

**Por qué la recomiendo igual:**
- La variable `festivo` sirve como **covariable de calendario**, como el día de la semana. Los festivos mueven sueño, pantalla, gasto, alcohol y ánimo; ayudan a separar «día libre» de «fin de semana» en R; y atenúan en parte H-5-6, porque quitar la tendencia no quita el ciclo semanal.
- Cuesta poco, porque P-19 ya deja la regla de día libre en un solo lugar.

**El calendario, comprobado en la sesión** en el texto vigente de la Ley Federal del Trabajo (art. 74; última reforma publicada el 14-05-2026):
- 1 de enero;
- primer lunes de febrero;
- tercer lunes de marzo;
- 1 de mayo;
- 16 de septiembre;
- tercer lunes de noviembre;
- **1 de octubre** de cada seis años, cuando hay transmisión del Poder Ejecutivo Federal (fracción VII, reformada en el DOF del 30-09-2024);
- 25 de diciembre;
- el día de la jornada electoral (fracción IX), que se omite: cae en domingo, así que no cambia nada.

**Dos precisiones para los datos históricos:**
- **La transmisión del Ejecutivo.** Antes de la reforma de 2024 era el 1 de diciembre (2000, 2006, 2012 y 2018). Lo deduzco de que la fracción fue reformada en 2024; no leí el texto anterior.
- **Los «lunes».** El artículo fue reformado el 17-01-2006; la regla de los lunes rige desde entonces. Antes, los festivos de febrero, marzo y noviembre eran en su fecha fija (5 de febrero, 21 de marzo y 20 de noviembre), lo que importa para la historia importada anterior a 2006. Que la regla viniera de esa reforma lo deduzco de la nota del artículo; no leí el decreto.

**Jueves y Viernes Santo no están en la ley.** Recomiendo dejarlos fuera de `festivo`.

**Alcance mínimo, si no lo tachas:**
1. `Fechas.esFestivoMx(fecha)`, una función pura con la lista de arriba, solo cuando el país de «Conocerte» es México.
2. `Fechas.esDiaLibre(fecha)` (de P-19) = sábado, domingo o festivo. `ritmo()` la aplica a la fecha de despertar.
3. Las variables `festivo` (0/1) y `dia_libre` (0/1) en el panel, en `filasPanel()` junto a `dia_semana` (`data/Exportar.kt:640`) y en el diccionario. R usa la columna, no su propia regla.
4. No cambia lo guardado; no sube `SCHEMA_V`.

**Riesgo.** Bajo. **Comprobación:** prueba JVM con las fechas de 2025 a 2027 (por ejemplo, el 3 de febrero de 2025 y el 1 de octubre de 2024 son festivos; el 5 de febrero de 2025 no). **Capítulos:** 5 (§ jet lag social) y apéndice A.

### I-2 · La estatura para el alcohol (Watson et al., 1980) — **fuera del criterio estricto; recomendado después de P-20**

**Qué cambiaría.** Hoy *r* (la fracción del cuerpo donde se reparte el alcohol) es fija por sexo: 0.68, 0.55, o 0.62 si no se dice (`widmarkR()`). Con Watson, *r* sale del agua corporal total:
- **Hombres:** TBW = 2.447 − 0.09516·edad + 0.1074·estatura_cm + 0.3362·peso_kg.
- **Mujeres:** TBW = −2.097 + 0.1069·estatura_cm + 0.2466·peso_kg.
- *r* = TBW / (P · peso), donde P es la proporción de agua en la sangre.
- **Qué está comprobado:** las ecuaciones de Watson et al. (1980) se citan en `referencias.md` con DOI comprobado en el módulo 4.
- **Qué no:** el valor habitual de P es 0.80, atribuido a Watson et al. (1981), pero **no se pudo comprobar** en esta sesión: solo se vio la ficha bibliográfica, no el texto. El módulo 10 debe verificarlo antes de fijarlo, o dejarlo como constante con su nota.

**La app tiene los datos:** la edad por fecha (`Perfiles.edad`), el sexo actual, el peso por fecha (`pesoEn`, que corrige P-20) y la estatura actual (`Perfil.estatura_cm`). En un adulto, que la estatura sea la de hoy no importa.

**Ejemplo con la simulación de la app** (3 bebidas de 14 g a los 0, 40 y 80 minutos):

| Persona (sintética) | *r* fija | *r* de Watson | Máximo hoy | Máximo con Watson |
|---|---|---|---|---|
| Hombre, 30 años, 175 cm, 70 kg | 0.68 | 0.749 | 0.607 g/L | 0.527 g/L (−13 %) |
| Hombre, 30 años, 190 cm, 70 kg | 0.68 | 0.777 | 0.607 g/L | 0.497 g/L (−18 %) |
| Hombre, 55 años, 170 cm, 95 kg | 0.68 | 0.624 | 0.375 g/L | 0.434 g/L (+16 %) |
| Mujer, 30 años, 160 cm, 60 kg | 0.55 | 0.621 | 0.998 g/L | 0.852 g/L (−15 %) |

**Por qué no cumple el criterio.** La *r* de Widmark es un valor publicado y la app lo declara. Watson reduce el error, pero no lo elimina: la incertidumbre de *r* sigue siendo de alrededor de un 9 % (coeficiente de variación 0.092, según Gullberg, citado por Searle, 2015). Es una mejora, no la corrección de un error. **El error que sí cumple el criterio es el peso** (P-20): con 90 kg reales frente a 70 por omisión, el máximo cambia un 48 %, más que con cualquier *r*.

**Alcance mínimo, si no lo tachas:**
- Usar Watson solo si hay sexo, fecha de nacimiento (para los hombres), estatura entre 100 y 250 cm y **un peso real**, no los 70 kg por omisión. Si falta algo, se queda la *r* fija.
- Acotar *r* entre 0.45 y 0.95.
- La *r* se calcula dentro de `alcoholEn`, con el peso de ese día.
- El diccionario dice qué *r* se usó.
- No sube `SCHEMA_V`.

**Comprobación:** prueba JVM con las cuatro personas de la tabla. **Capítulo:** 5 (§ alcohol).

### I-3 · «¿Lo hiciste hoy?» en el aviso del experimento — **el dato es nuevo (fuera del criterio), pero corrige un rótulo engañoso; recomendado con condiciones**

**Cómo es hoy.** El aviso del experimento en el Día (`ui/pantallas/Dia.kt:319-324`) es una barra de solo texto (`Aviso`, `ui/Componentes.kt:529-534`), casi al final de la página. Sale para cualquier fecha abierta y no menciona los días de lavado. Ahí ya están a mano el experimento, la semana, la condición y la fecha.

**Qué dicen las simulaciones** (efecto real +0.5; cumplimiento del 70 % en semanas «SÍ» y del 10 % en semanas «no»):
- **El análisis según el plan** («intención de tratar», el actual) da +0.30: el efecto se diluye, pero el análisis es **válido**. Es lo que se usa en los ensayos clínicos, precisamente porque no depende de cuándo decides cumplir.
- **El análisis «por lo hecho»** da +0.50 si cumples al azar, pero **+1.08, el doble**, si lo haces más los días buenos. Es engañoso como análisis principal.
- **El cociente** efecto según el plan ÷ (cumplimiento en «SÍ» − cumplimiento en «no») da +0.46 a +0.50. Es una aproximación razonable que supone que saber la condición no influye por sí mismo, algo dudoso sin cegamiento.

**Por qué no cumple el criterio.** El análisis actual no es incorrecto y no se pierde nada que exista. Pero el rótulo «Con la acción / sin ella» sí sugiere que se midió lo hecho; eso lo arregla P-18 (punto 9) con o sin esta idea. Lo que la idea añade, y no tiene sustituto, es **saber cuánto cumpliste**: sin eso no se puede distinguir «no funciona» de «no lo hice».

**Alcance mínimo, si no lo tachas:**
1. **Dónde guardarlo.** `Experimento.hecho: Map<String, Boolean>` (fecha → sí o no; si no hay entrada, «sin respuesta»).
   - Mejor que en `Dia`: no crea días vacíos, no toca `retroactivo` y `resultado` lo tiene a mano.
   - **No** copiar el patrón de los hábitos, que borra la clave al desmarcar y confunde «no» con «no respondí».
2. **La interfaz.** Dos fichas, «sí» y «no», dentro del aviso, para fechas hasta hoy. Se pregunta **en las dos condiciones**: en las semanas «no» mide si lo hiciste igual, y así la propia pregunta sesga poco el contraste.
3. **El análisis.** El principal sigue siendo según el plan. Se agrega:
   - el % de cumplimiento en semanas «SÍ» y en semanas «no», y los días sin respuesta;
   - como secundario, el cociente de arriba con su advertencia, no el «por lo hecho».
4. **Exportación.** Una columna `hecho` en `vm_experimentos.csv` (`fecha:1|fecha:0`), con su descripción. R ya puede leerla con `vm_pares`.
5. **Requisitos.** Va con P-12 (fecha de fin; si no, al terminar desaparece el aviso y ya no se puede responder) y con el punto 3 de P-08 (restaurar sin pisar). Sube `SCHEMA_V` (fase 2).
6. **Para la plantilla «Creatina»,** la adherencia ya existe en el registro de suplementos (`supl_creatina_tomado`). Puede mostrarse sin preguntar (H-9-26).

**Riesgos que hay que documentar en el capítulo 8:**
- Preguntar a diario puede cambiar la conducta (reactividad); su tamaño no se pudo estimar.
- «No respondí» no es «no lo hice», y probablemente se responde menos en los días malos.
- Responder días después mete sesgo de memoria.
- Como el aviso está abajo en el Día, faltarán respuestas.

**Comprobación:** prueba JVM de `resultado` con un mapa `hecho` sintético (% de cumplimiento y cociente). Capturas del Día. **Capítulo:** 8.

### I-4 e I-5 · Registrar solas la campaña activa y los avisos mostrados — **cumplen el criterio**

Las dos son las propuestas **P-11** (campañas, criterio e) y **P-10** (avisos, criterios d y e). Una precisión a la idea: las campañas **sí** quedan guardadas, con su lunes de inicio, en la configuración. Lo que falta es convertirlas en variable del panel y no borrar las abandonadas. Los avisos, en cambio, de verdad no quedan: el plan se poda a 4 días, los disparos a 40 y ninguno de los dos viaja en el respaldo.

---

## 6. Tabla de verificación de los 109 hallazgos

Veredicto: **C** = confirmado; **C·m** = confirmado con matices (ver la sección 7); **P** = parcial. «Doc» = solo se documenta en el paper.

| Hallazgo | Veredicto | Criterio | Gravedad (revisada) | Destino |
|---|---|---|---|---|
| H-1-1 Relleno con cero | C·m | a, b | Alta | P-15, P-30 |
| H-1-2 Dos criterios de día | C·m | a | Baja (antes Media) | P-16 |
| H-1-3 Documentos ilegibles | C (más grave) | d | Media | P-07 |
| H-1-4 `coerceInputValues` | C·m | — | Baja (antes Media) | Doc |
| H-1-5 `gasto_imprevisto` | C·m | — | Baja | Doc |
| H-1-6 Menciones = «estuve» | C (alcance mayor) | e (solo app) | Media | P-15 (covariable) + Doc |
| H-1-7 Zona horaria actual | C·m | — | Baja | Doc |
| H-1-8 Diccionario sin versiones | C·m | — | Baja | Doc |
| H-1-9 Sellos de importados | C·m | a menor | Baja | P-33 |
| H-3-1 Criterio de día por fuente | C (lista incompleta) | a, e | Alta | P-16 (D-3) |
| H-3-2 Primer y último uso | C | a, b | Media | P-16, P-30 |
| H-3-3 Filtro barométrico | C | d (condicional) | Alta condicional | P-33 |
| H-3-4 Clima mixto | C·m | — | Baja–Media | Doc |
| H-3-5 Distancia | C·m | — | Baja | Doc |
| H-3-6 Cold Turkey | C·m | a menor | Baja | P-33 |
| H-3-7 Wifi nulo | P | — | Baja | Doc |
| H-3-8 Ubicación | C·m | a menor | Baja | P-33 |
| H-3-9 Spotify | C·m | — | Baja | Doc |
| H-3-10 WhatsApp | C·m | — | Baja | Doc |
| H-2-1 Cuestionarios | C | a | Alta | P-14 |
| H-2-2 `retroactivo` | C | a, d | Media | P-13 |
| H-2-3 Avisos no guardados | C·m | d, e | Media | P-10 |
| H-2-4 «No respondí» = 0 | C·m | a (parcial) | Media | P-15 |
| H-2-5 Perfil hacia atrás | C | a | Media | P-26 |
| H-2-6 Brújula política | C | comentario | Baja | P-26 (comentario) |
| H-2-7 Separación de avisos | C·m | comentario | Baja | P-26 (comentario) |
| H-2-8 Cierre de día pasado | C | a | Baja–Media | P-26 |
| H-2-9 Editar momentos | C | — | Baja | Doc |
| H-2-10 `error_prediccion` | C | — | Baja | Doc |
| H-2-11 Anclaje en la interfaz | C·m | — | Baja | Doc |
| H-2-12 Prueba de reacción | C·m | a débil | Baja | P-26 |
| H-2-13 Columnas `m_*` | C·m | — | Baja | Doc (nombres en P-30) |
| H-2-14 «Por adelantado» | C·m | a (texto) | Baja | P-26 |
| H-2-15 Ánimo por lugar | C·m | a, b | Baja | P-26, P-05 |
| H-4-1 Cafeína | C·m | — | Baja | Doc + corrección |
| H-4-2 Una fuente por día | C | — | Media | Doc |
| H-4-3 Hilo principal | C | a | Media | P-21 |
| H-4-4 Alcohol fijo | C·m | a (peso) | Media | P-20 (+ I-2) |
| H-4-5 Alcohol desde cero | C·m | — | Baja | Doc |
| H-4-6 Creatina | C·m | — | Baja | Doc |
| H-4-7 Suplementos | C | — | Baja | Doc |
| H-4-8 Probióticos | C | a leve | Baja | P-15 |
| H-4-9 Jet lag social | C | a (ventana) | Media | P-19 (+ I-1) |
| H-4-10 Reloj y teléfono | C·m | — | Baja | Doc (texto en P-19) |
| H-4-11 Parámetros | C | — | Baja | Doc |
| H-4-12 Cuatro cafeínas | C·m | — | Baja | Doc |
| H-4-13 e1RM | C | b | Baja | P-22 |
| H-5-1 `vm_panel` ≠ panel | C·m | a, b, e | Alta | P-30 |
| H-5-2 Obvias en R | C | b | Media | P-31 |
| H-5-3 «Todo lo que se mueve» | C | a | Alta | P-17 |
| H-5-4 «Cruzar dos variables» | C | a | Media | P-17 |
| H-5-5 `diferencia()` | C | a | Media | P-17 |
| H-5-6 Ciclo semanal | C | — | Media | Doc (+ I-1) |
| H-5-7 Pearson / Spearman | C·m | — | Muy baja | Doc |
| H-5-8 `km` en atípicos | C | a | Baja | P-24 |
| H-5-9 Tus números | C·m | a (ejercicio) | Media | P-23 |
| H-5-10 Palabras | C | b | Baja | P-32 |
| H-5-11 Modelo múltiple | C | a | Baja | P-17, P-28 |
| H-5-12 Dos «difícil» | C·m | — | Muy baja | Doc |
| H-5-13 Límites de la corrección | C | — | Baja | Doc |
| H-5-14 Lentas | C·m | — | Baja | Doc |
| H-5-15 Reportes y Totales | C | b leve | Baja | P-29 |
| H-5-16 Aviso de potencia | C | a (texto) | Baja | P-28 |
| H-6-1 Privados en personas | C·m (más amplio) | c | Media | P-02, P-01 |
| H-6-2 `per_` mezcla | C·m | — | Baja (antes Media) | Doc (etiqueta en P-28) |
| H-6-3 Fecha del cambio de relación | C | — | Media | Doc |
| H-6-4 Conocerte sin equivalencias | C·m | a | Baja | P-25 |
| H-6-5 Colores de la Red | C | a, b | Baja | P-25 |
| H-6-6 «Solo lo que dije» | C | a | Baja | P-25 |
| H-6-7 App y `vm_red` | C | b | Media | P-32 |
| H-6-8 Densidad y Burt | C | — | Baja | Doc |
| H-6-9 Fuentes de «quién aparece» | C | a (parcial) | Baja | P-25 + Doc |
| H-6-10 Pares por construcción | C·m | a | Baja | P-25 |
| H-6-11 Equivalencias | C (y ciclos) | a, d | Baja | P-25 |
| H-6-12 Cercanía | C | a, d | Baja | P-25 |
| H-6-13 Sociales en 0 | C | a | Media | P-15 |
| H-6-14 Relación actual | C | — | Baja | Doc (comentario en P-26) |
| H-6-15 IOS | C·m | — | Baja | Doc |
| H-7-1 Lavado / «Creatina» | C·m | a | Alta | P-18 |
| H-7-2 «Terminar» sin fecha | C | a, d | Media | P-12 |
| H-7-3 Intervalo estrecho | C | a | Alta | P-17, P-18 |
| H-7-4 Resultado a diario | C | a | Media | P-18 |
| H-7-5 Sorteo libre | C | a débil | Media (4 sem.) / Baja (≥ 8) | P-18 |
| H-7-6 Prueba de tendencia | C | a | Media | P-18 |
| H-7-7 Hedges | C | — | Baja | Doc |
| H-7-8 Adherencia | C | — (rótulo: a débil) | Media | P-18 (rótulo), I-3 |
| H-7-9 «Semana 1» antes | C | a | Baja | P-18 |
| H-7-10 Sellado | C·m | — | Baja | Doc |
| H-7-11 Sin función de R | C | — | Baja | Doc |
| H-7-12 «Antes y después» | C | a (duplicados) | Media | P-18, P-17 |
| H-7-13 Caché de Experimentos | C·m | — | Muy baja | P-21 (de paso) |
| H-7-14 Medida de «Creatina» | C | — | Baja | Doc |
| H-8-1 Impulsos no privados | C | c | Alta | P-01 (D-1) |
| H-8-2 «Relación del año» | C·m | c | Media | P-03 |
| H-8-3 Recaída → logros | C | c | Media | P-03 |
| H-8-4 Reportes públicos | C | c (parcial) | Media | P-06 |
| H-8-5 Respaldos en claro | C·m | — | Baja (antes Media) | Doc |
| H-8-6 Logros por valencia | C | — | Baja | Doc |
| H-8-7 Campañas | C·m | e | Media | P-11 |
| H-8-8 Rachas | C·m | a | Media | P-27 |
| H-8-9 PBKDF2 | C | — | Baja | Doc |
| H-8-10 Ánimo mezclado | C | b | Baja | P-29 |
| H-8-11 Cambios sin incertidumbre | C·m | — | Baja | Doc |
| H-8-12 «Verificar» | C·m | a, d | Baja (antes Media) | P-08 |
| H-8-13 Restaurar | C (peor) | d | Media | P-08 (parcial) + Doc |
| H-8-14 Respaldo malo | C·m | d | Media | P-09 |
| H-8-15 Contraseña vieja | C | — | Baja | Doc |
| H-8-16 Medicamentos | C | c (vía H-8-2) | Baja | P-03 (D-2) |
| H-8-17 Servicios externos | C·m | — | Baja | Doc |

## 7. Correcciones al texto de los hallazgos y capítulos

Son matices que hay que pasar al paper. Ninguno cambia el sentido del hallazgo. Los aplica el módulo 10 al actualizar los capítulos, o el 14 en la lectura final.

**Módulo 1 (capítulo 2):**
- **H-1-2.** Solo `lugares_distintos` usa la fecha de calendario. `reacciones`, `gastos.dia` y `kmPorDia` ya usan el día lógico.
- **H-1-4.** La coerción solo actúa con `null` en un campo no anulable con valor por omisión, o con un *enum* desconocido; en `data/` no hay enums.
- **H-1-5.** La etiqueta ya dice «(0/1)», y `AGREGADOS` no mezcla escalas en ningún cálculo.
- **H-1-6.** En los momentos también: nombrar a alguien en un momento de ansiedad, estando solo, cuenta como «estuve». `presencias_revisado` sí llega a R por `vm_dias.csv`.

**Módulo 3 (capítulo 4):**
- **H-3-1.** A la lista se suman Mundo y `lugares_distintos`.
- **H-3-7.** El mecanismo descrito no es probable: un SSID censurado cuenta como «con wifi». Solo se cuenta «sin wifi» si `networkId == −1` y la IP es 0.
- **H-3-8.** La importación de Maps está en `data/Importar.kt` (`cronologia`), no en `Importaciones.kt`.
- **H-3-9.** El historial importado ya usa `ms_played`; el problema queda solo en la API de recientes.
- **H-3-10.** El código está en `data/Importaciones.kt`, no en `Importar.kt`.

**Módulo 2 (capítulo 3):**
- **H-2-3.** `plan_muestreo` conserva 4 claves (hoy y 3 días atrás), no 3. «Mostrado» significa «se intentó mostrar».
- **H-2-4.** Las preguntas científicas no respondidas quedan **vacías**, no en 0; el 0 aparece solo con `no_aplica` y con «las tres», y con «las tres» la información no se pierde, porque existe su columna.
- **H-2-7.** Con la ventana mínima permitida (2 h) y 6 avisos, la separación mínima baja a 10 min.
- **H-2-13.** Las columnas `m_*` están en `vm_panel.csv`, no en `vm_dias.csv`, y el diccionario ya avisa que son registros viejos.
- **H-2-15.** En R, `vm_mapa_animo` incluye también momentos privados (ver P-05).

**Módulo 4 (capítulo 5):**
- **H-4-1.** El ejemplo de «12 mg» corresponde a una vida media de **2.5 h**, no de 1.5 h (con 1.5 h serían 1.9 mg). El deslizador de la app solo permite de 2 a 10 h.
- **H-4-5.** Solo afecta a `alcohol_estimado_max`: `alcohol_al_dormir` sí arrastra las 18 h previas.
- **Para el capítulo 5, por la reforma de 2024:** la transmisión del Ejecutivo es el 1 de octubre, no el 1 de diciembre.

**Módulo 5 (capítulo 6):**
- **H-5-1.** Hay 11 nombres distintos, no 8: se suman `sueno_profundo`, `sueno_rem` y `sol_horas`. `m_*` no incluye momentos privados; solo `n_momentos` los cuenta. Y `vm_panel_extendido.csv` tampoco es exactamente el panel de la app: cuando un nombre coincide, gana `filasPanel`.
- **H-5-7.** Cada función de R usa el mismo coeficiente que su gemela de la app; solo `vm_cruce` difiere.
- **H-5-9.** Lo del aire libre no es un error: la etiqueta ya dice que es la luz que mide el teléfono.

**Módulo 6 (capítulo 7):**
- **H-6-1.** La libido no tiene campo de texto desde la app. La fuga también llega a `tema_`, a las sugerencias al escribir y a «¿A quién conoce?».
- **H-6-15.** Las etiquetas verbales no van en cada par: solo aparece la del valor elegido.

**Módulo 7 (capítulo 8):**
- **H-7-1.** Con lavado de 28 días y 12 semanas, el 95 % de los sorteos no da resultado, no el 100 %. Con lavado de 7: 50 / 20 / 7 / 1 % con 4 / 6 / 8 / 12 semanas.
- **H-7-4.** Las cifras exactas de la simulación son 27.8 % y 51.8 %.

**Módulo 8 (capítulos 9 y 10):**
- **H-8-5.** La app ya avisa del modo en claro. Además, quitar el texto de las cartas selladas del `vm_respaldo.json` suelto sería **peligroso**: se puede restaurar desde ese archivo, y borraría el texto de las cartas del teléfono. La propuesta original no debe aplicarse.

## 8. Lo que se queda solo como documentación

Los hallazgos marcados «Doc» en la tabla se explican en su capítulo como **límites conocidos**, sin cambiar código. Los más importantes para la discusión del capítulo 11:

- **El ciclo semanal sobrevive a quitar la tendencia** (H-5-6). En simulación, un efecto común de fin de semana deja r ≈ 0.12 después de quitar la tendencia. El remedio está en R: `factor(dia_semana)` o `dia_libre` en los modelos.
- **La fecha de los cambios de relación** es la de la edición (H-6-3), y el perfil y la cercanía llenados por primera vez se proyectan hacia atrás (H-2-5, H-6-12, en su parte sin arreglo).
- **La cafeína y el alcohol son estimaciones** con parámetros de población (H-4-1, H-4-4, H-4-11), y el diccionario no versiona las fórmulas (H-1-8).
- **El sellado del experimento no fija el análisis** (H-7-10). No hay prueba de aleatorización ni función de R (H-7-11). Las etiquetas de *d* vienen de comparaciones entre personas (H-7-7).
- **Logros que premian valores de la valencia** (H-8-6, la ley de Campbell). Los reportes comparan sin incertidumbre (H-8-11). El cifrado tiene iteraciones por debajo de la recomendación actual de OWASP: 600 000 para PBKDF2-HMAC-SHA256, comprobada en la sesión del módulo 8 (H-8-9).
- **Los servicios externos no declarados** (H-8-17): catálogos de obras (TMDB, RAWG, TVmaze, Open Library, Crossref, Google Books) y mosaicos de OpenStreetMap. No reciben nada del diario, pero el documento de contexto debe listarlos.
