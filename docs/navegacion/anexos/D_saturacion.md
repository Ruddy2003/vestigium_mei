# Anexo D · Inventario de saturación (Fase 4, con P5)

Revisión hecha sobre el código de `HEAD` = `87f7f2c`. Las capturas de la rama `capturas` usadas aquí salen de esa misma ejecución (run 83, commit `87f7f2c`), así que el código y las imágenes coinciden. No se modificó ningún archivo del repositorio.

Rutas abreviadas: `ui/` = `app/src/main/java/io/github/ruddy2003/vestigium/ui/`, `data/` = `…/vestigium/data/`.

Leyenda de frecuencia (cuánto aporta a un usuario que registra a diario):
- **a diario**: lo ve o lo usa casi todos los días;
- **a veces**: unas veces por semana o solo cuando hay datos;
- **raro**: una vez al mes o menos;
- **nunca**: no se ve nunca en el uso real.

Leyenda de propuesta: **queda** (a la vista) · **pliega** («ver más» o sección plegada) · **mueve** (a otra pantalla) · **fusiona** · **quitar (propuesta)**.

---

## 0. Cómo se midió y qué datos tienen las capturas

### 0.1 Método

- El lienzo de Paparazzi es un `PIXEL_6` de 1080 px de ancho. La altura la fija cada test con `screenHeight`:
  - `dia()`: 6200, `Capturas.kt:115`;
  - `pantalla()`: el valor de cada test, `Capturas.kt:200-207` y `:335`;
  - `v67()`: `:743` y `:753-754`;
  - `v73()`: `:868` y `:871-877`.
- La altura del lienzo **no es** la del contenido. `Hoja` usa `verticalScroll` (`ui/Hoja.kt:39`): lo que no cabe se corta sin aviso.
- **Script** (de trabajo, fuera del repositorio):
  - para cada fila, calcula la fracción de píxeles que difieren en más de 28 niveles de gris de **la fila de fondo más parecida** entre las 240 de abajo; esto tolera texturas que se repiten en vertical;
  - el contenido termina en la última fila con más del 2 % de píxeles distintos.
- **Limitaciones del script**:
  - si el contenido llena el lienzo, las filas «de fondo» de referencia ya tienen contenido y el número sale falso: unos 250 a 350 px antes del borde;
  - por eso cada captura se revisó **a ojo**, en tiras reducidas al 40 %;
  - cuando el contenido llega al borde, el alto real se estima por suma de bloques y se marca «**estimado**».
- **Pantallas útiles**:
  - con la consigna del plan (§ 6, Fase 4): 2400 − 200 = **2200 px**;
  - con el código real es menos: barra de pestañas de 66 dp = 173 px (`ui/MainActivity.kt:197`), más la barra de estado y la de gestos (unos 60 a 110 px cada una). En la práctica, **unos 2050 px**;
  - en la noche, la `BarraNoche` flotante (`ui/pantallas/Dia.kt:544-569`, unos 70 dp) tapa otros 185 px.
- Todas las `Hoja` terminan con `Spacer(56.dp)` = 147 px vacíos (`ui/Hoja.kt:86`). La hoja de la noche agrega 72 dp más (`Dia.kt:541`).

### 0.2 Datos de ejemplo de las capturas

`object Ejemplo` (`app/src/test/.../Capturas.kt:1132-1180`) carga:
- **Días**:
  - los 35 días anteriores, cada uno con valencia 3 a 6 (al azar, con semilla 7), una de cuatro notas inventadas con `@ana`, `@luis`, `#tesis` y `#protocolo`, `sueno_calidad = 3` y `mejor_momento`;
  - **hoy vacío** (`valencia = null`, `sueno_calidad = null`).
- **Salud**: 36 días con sueño de 380 a 470 min (23:40 → 07:10), de 6000 a 12 000 pasos y HRV.
- **Pantalla**: de 180 a 300 min y de 60 a 100 desbloqueos.
- **Gastos**: de 0 a 2 por día.
- **Otros diarios**: notificaciones, clima (24°/11°) y nutrición (kcal y proteína).
- **Lugares y visitas**: 5 lugares; hoy, casa → colegio → casa.
- **Momentos**: 2 de hoy (ánimo 5/7 a las 11:47, preocupación a las 16:20).
- **Una sesión de pesas**; ingresos.

`ejemploV67()` (`Capturas.kt:721`) suma:
- 4 tramos (tesis, siesta, idiomas; lectura ayer);
- momentos: muestreo, alegría con `@ana @luis #cumple`;
- **momentos privados** de ayer (impulso y recaída). Sirven para comprobar que Privado no se filtra a otras pantallas, y no se filtra.

`ejemploV73()` (`Capturas.kt:841`) suma:
- consumos: alcohol ayer **sin completar**, que dispara `LoDeAnoche`; dos cafeínas y cannabis hoy;
- momentos de ánimo con `@marta`, `@anita` y `@luis`;
- un sueño escrito de ayer;
- personas clasificadas: ana y luis.

Ojo con tres cosas:
1. **El estado se filtra entre tests**:
   - `Ejemplo.cargar()` no limpia consumos, tramos ni obras;
   - por eso `diaRomano` (que solo llama a `Ejemplo.cargar()`) muestra «¿Cómo terminó lo de anoche? Alcohol» y la línea «enfoque»;
   - y `nocheMedieval` muestra portadas de obras («Don Quijote…»);
   - las capturas son **un día cargado, no un día típico**.
2. **La hora del CI manda el orden de Hoy** (`Dia.kt:186-190`, `Hoy.kt:100-118`):
   - en las capturas, `TeFaltaHoy` muestra «Calidad del sueño» y las invitaciones «bienestar (WHO-5)», «prueba de reacción» y «brújula política», pero no «Diario de hoy»;
   - y la tarjeta «Llenar el día» (VESPERA) sale **después** del tablero;
   - eso corresponde a la franja de mediodía: entre las 12 y las 17 h con `hora_diario = 22` (`data/Modelo.kt:595`), porque `tarde` es desde `max(22 − 4, 14) = 18`.
3. **Tablero por defecto**: `TABLERO_INICIAL` = sueño, ánimo, pasos, pantalla, gasto y kcal (`Hoy.kt:386`).

### 0.3 Mediciones

| Captura | Pantalla | Lienzo (px) | Contenido (px) | Pantallas de 2200 px | Nota |
|---|---|---|---|---|---|
| `diaV73Romano` | Hoy, mediodía, datos v73 | 8000 | **7602** medido; con el espaciador final, unos 7750 | **3,5** | Única captura de Hoy que no se corta. «Lo que registró el teléfono» está plegada; abierta suma de 600 a 1200 px. |
| `diaRomano` | Hoy | 6200 | **≥ 6200** (cortada a media sección «Sueño») | ≥ 2,8 | Lleno. Con los datos de `diaV73Romano` serían unos 7300. |
| `diaMedieval` | Hoy | 6200 | ≥ 6200 (cortada en «Del sueño») | ≥ 2,8 | Lleno. |
| `diaCiber` | Hoy | 6200 | ≥ 6200 (cortada en «sueño») | ≥ 2,8 | Lleno. La frase del día en ciber mide unos 420 px, más que en romano (unos 250). |
| `semanaCortaAntiguas` | Hoy en «semana corta» (solo `SemanaCorta`, sin encabezado) | 2400 | **871** | 0,4 | Referencia: el modo mínimo ya existe (`ui/pantallas/Lote2.kt:515`). |
| `nocheMedieval` | Hoja de la noche | 5200 | ≥ 5200; **unos 8000 a 8800 estimado** | **≈ 3,6 a 4** | Cortada en «Lo que leí o vi hoy» (y ≈ 4510). Falta: foto, predicción, 3 plegadas, privado, «Cómo estás ahora» (cuadrícula de 7×7 con `aspectRatio(1.25f)`, unos 950 px, `ui/Selectores.kt:110`) y el botón sellar. El plan dice «5200 px»: ese número es el lienzo, no el contenido. |
| `nocheV67Seda` | Hoja de la noche | 4400 | ≥ 4400 (cortada en «Lo que leí») | ≥ 2 | Igual. |
| `diariosColonos` | Diario | 3400 | **≈ 2470** (último: el título «Exportar»), unos 2620 con el espaciador | **1,1** | Línea de tiempo y Exportar plegadas (`autoPlegar`). |
| `diarioNapoleon` | Diario | 4200 | **≈ 3640** | 1,7 | El estante de cartas de Napoleón ocupa el doble que el de Colonos: el alto depende del tema. |
| `diarioV73Seda` | Diario | 2600 | ≥ 2600 (lleno) | ≥ 1,2 | Lleno. |
| `vidaRomano` | Vida | 4200 | ≥ 4200; **unos 5600 a 5700 estimado** | **≈ 2,6** | Cortada en «Otras vidas». Faltan «Otras vidas» (150 dp más pie) y «Dinero y sustancias» (2 filas). |
| `vidaCiber` | Vida | 4200 | ≥ 4200 | ≥ 1,9 | Igual. |
| `entenderRomano` | Análisis | 2600 | ≥ 2600; la captura solo llega a «VII · Palabras del diario» | ≥ 1,2 | La captura **no ve** casi nada de Análisis. Estimado por código con 36 días: unos 9000 px; con un año de datos, de 10 000 a 14 000 px (de 4 a 6 pantallas). |
| `masLaboratorio` | Más (sin atajos) | 3000 | ≥ 3000 (cortada en «02 Tu gente…») | ≥ 1,4 | Lleno. |
| `masV67Romano` | Más (3 atajos fijados) | 4400 | ≥ 4400; **unos 8000 a 8400 estimado** | **≈ 3,7** | Cortada en «III · Medir». Faltan 7 filas de Medir y 11 de Sistema (unos 175 px cada una) y 2 párrafos de ayuda. |
| `masGotico` / `masBotanico` | Más | 3600 / 3800 | llenos | — | Igual. |

**Lectura**:
- Hoy mide unas **3,5 pantallas** con datos ricos (unas 3,7 con los 2050 px reales).
- La noche mide de 3,6 a 4; Más, unas 3,7; Vida, unas 2,6; Diario, de 1,1 a 1,7 (depende del tema). Análisis es la más larga si se abre todo.
- **Las capturas de 6200, 5200, 4400, 4200, 3000 y 2600 px están cortadas**: para medir de verdad hay que subir `screenHeight` (Fase 6). Eso solo toca `app/src/test/`.

---

## 1. Hoy (`PantallaDia` con `fecha == hoy`, `ui/pantallas/Dia.kt:101-340`)

**El orden cambia con la hora** (`Dia.kt:186-190` y `:294-297`):
- `manana = esHoy && hora in 5..11`;
- `nocheYa = (fecha < hoy) || (esHoy && (hora >= max(hora_diario − 4, 14) || hora < 5))`; con la configuración por defecto, desde las 18 h;
- en la mañana va primero el bloque «Sueño»; en la noche, «Llenar el día»; a mediodía, los pendientes.
- Si `c.semana_corta` está activo, Hoy se reduce a `SemanaCorta` y termina ahí (`Dia.kt:212`, `return@Hoja`).

La tabla sigue el orden de **mediodía**, que es el de las capturas. Al final se indican las variantes.

| # | Tarjeta o sección (función, archivo:línea) | Cuándo aparece (condición exacta) | Para qué sirve | Frecuencia | ¿Ya se oculta, pliega o mueve? | Propuesta |
|---|---|---|---|---|---|---|
| 1 | Encabezado: título «Día», 🔍 buscar, ⚙ Más, modo claro u oscuro, emblema; **fecha grande que abre el calendario**; línea «martes… · hoy · luna» (`Dia.kt:164-182`, `BotonBuscar` en :171, `BotonAjustes` en :172, fecha en :177-178) | Siempre | Navegar y ver la fecha | a diario | No | **Queda** (cumple P2). Unos 330 px: podría compactarse a una sola línea de fecha. |
| 2 | Fila de estado: ‹ · «estado = guardado / guardando… / vacío» · «racha N» · [hoy] · › (`Dia.kt:193-210`) | Siempre. «hoy» solo si `!esHoy` (:208); la racha si `r >= 2` (:206) | Cambiar de día; ver si se guardó | a diario | No | **Queda** |
| — | `SemanaCorta` (`Lote2.kt:515`) | `esHoy && c.semana_corta` (`Dia.kt:212`): **sustituye todo lo de abajo** | Hoy mínimo: 4 cifras y 3 botones | — | Sí: `Repo.config.semana_corta`, «volver a Hoy completo» (`Lote2.kt:543`) | **Queda** (es el modelo de P5). |
| 3 | `FraseDelDia` (`Hoy.kt:295-343`; llamada en `Dia.kt:214`) | Siempre (no depende de `esHoy`) | Frase del tema, con ♥ para guardarla | a diario (la ves), pero no es acción | No (no hay ajuste) | **Pliega** a una línea (texto y autor, ♥), o **mueve** debajo del bloque de acciones. Unos 250 px en romano y 420 en ciber. Queda encontrable en «Frases guardadas» (`Pantallas.kt:70`, `Mas.kt:81`). |
| 4 | `SugerirSemanaCorta` (`Lote2.kt:549-565`; `Dia.kt:215`) | `esHoy`, sin `semana_corta`, `semana_corta_sugerida != hoy` y los 3 últimos días en `c.hoy_no` (`Lote2.kt:552-554`) | Ofrecer el modo mínimo tras 3 «hoy no» | raro | «no, gracias» por hoy (:560) | **Queda** (es rara y oportuna). |
| 5 | `LoDeAnoche` «¿Cómo terminó lo de anoche?» (`Dia.kt:851-863`; :216) | `esHoy && Sustancias.porCompletar()` no vacío (:853-854) | Completar duración, máximo y cruda de un consumo | a veces | No. Si la sustancia está en `sustancias_ocultas` dice «Un registro del…» (:859) | **Fusiona** con `TeFaltaHoy` como una ficha «Completar lo de anoche →». Se mantiene la redacción anónima de `Sustancias.visible`. No es dato de `data/Privado.kt`, pero no conviene darle más peso. |
| 6 | `TarjetaPersonaje` (`ui/Personaje.kt:339-360`; `Dia.kt:290`) | `esHoy && Repo.config.personaje` (por defecto `true`, `Modelo.kt:706`) | Personaje del tema con una frase, la racha, el ánimo y la temperatura | a diario (decorativo) | Sí: Apariencia → «Personaje del tema» (`ui/pantallas/Sistema.kt:315`) | **Mueve** más abajo (después del Tablero), o **fusiona** con la fila de estado como figura pequeña. Unos 230 px en el primer alto. |
| 7 | Aviso «Pasa de medianoche…» (`Dia.kt:291`) | `esHoy && hora < 5` | Explicar el día lógico (P9) | raro (de madrugada) | No | **Queda** (P9). |
| 8 | Aviso «Este día todavía no llega…» (`Dia.kt:292`) | `fecha > hoy` | Escribir por adelantado | nunca en Hoy | — | **Queda** (solo en fechas futuras). |
| 9 | **Bloque «Sueño»** (`bloqueSueno`, `Dia.kt:218-249`) | Mañana: aquí, primero (:295). Si no, en el #17 | Calidad del sueño, sueño recordado (abre 8 campos más) y horas («corregir las horas de sueño», :243-246) | a diario (en la mañana) | `Seccion(plegable = true)`: recuerda el pliegue en `config.plegadas`, :219 | **Queda** primero en la mañana. Fuera de la mañana, **pliega** si ya está lleno. |
| 10 | **`TeFaltaHoy`** (`Hoy.kt:142-194`; `Dia.kt:272-284`) | `esHoy` (:270). Pide hasta 4 «esenciales» (`ESENCIALES`, `Hoy.kt:139`: `p:ayer` antes de las 14 h, `p:sueno`, `p:aviso`, `p:diario` desde las 18 h) y hasta 5 invitaciones (escalas, reacción desde las 8 h, predicción, gasto desde las 20 h, política) (`Hoy.kt:92-120`). Recibe `ocultar = {p:diario, p:prediccion}` (`Dia.kt:272`): esos los muestra la tarjeta de la noche. Si no queda nada: «✓ Hoy completo» solo si son las 21 h o más con valencia; si no, solo las invitaciones (:175-179) | Lo que falta hoy, con salto directo | **a diario** | Sí: «hoy no» descarta las invitaciones de hoy (:169-171); «no hace falta: diario de ayer» (:188-190); `config.avisos_ignorados` | **Queda**, arriba del todo. Es el núcleo de P5. Recibe por fusión los #5, #12, #22 y #11 (resumido). |
| 11 | `DatosQueFaltan` (`Hoy.kt:253-288`; `Dia.kt:285`) | `esHoy` y `Faltas.revisar` no vacía (`Hoy.kt:203-249`). Cubre: sueño del reloj desde las 10 h; ubicación sin muestra por más de 3 h; clima o pantalla de ayer desde las 9 h; Spotify más de 2 h; configuración ilegible; registros ilegibles; respaldo pausado o de más de 36 h; lector de notificaciones desde las 13 h; MacroFactor de más de 10 días | Avisar de fuentes automáticas caídas | a veces | Sí: «ignorar hoy», «no volver a avisar» y «volver a avisar de todo» (:275-286) | **Pliega** a una sola línea («N datos que faltan →») que despliega la lista, o **mueve** el detalle a «Estado de los datos» (`estado_datos`, `Pantallas.kt:88`, `Mas.kt:131`) con un contador en Hoy. Cada falta lleva 3 botones: es de lo que más crece. |
| 12 | `TarjetaQuienEs` compacta «¿Quién es @x?» (`ui/pantallas/Gente.kt:126-145`; `Dia.kt:286`) | `esHoy && c.personas_pospuesto != hoy` y `Personas.pendientes()` no vacía (Gente.kt:129) | Clasificar personas mencionadas | a veces hasta que clasificas, luego nunca | «después» la pospone por hoy (`data/Personas.kt:58`); «no preguntar» (dentro del formulario) | **Fusiona** como invitación de `TeFaltaHoy` («¿quién es @x? →»), o **mueve** a Vida → «Tu gente», que ya muestra «N por clasificar» (`Vida.kt:79-80`). Encontrable: `personas` en el buscador (`Pantallas.kt:49`); conviene sumar «clasificar quién es» a sus `palabras`. |
| 13 | **Tablero** «Tus cifras del día y la semana…»: 6 `TarjetaMetrica` en 3 filas, más «editar tablero» (`Hoy.kt:389-428`; `Dia.kt:300`) | Siempre (también en otras fechas) | Cifras del día contra tu normal de 28 días, con barras de 7 días | **a diario** (mirar) | Sí: elegir y **reordenar** con ↑↓ (`Hoy.kt:409-427`, `mover` en :430, `config.tablero`). La sección **no** es plegable. Las cifras **no se pueden tocar** (P1: `Surface` sin `onClick`, `Hoy.kt:450`) | **Queda**, pero **compacta**: las primeras 3 o 4 cifras en una fila, más «ver más». Hoy ocupa unos 1000 px y empieza en y ≈ 2350: **fuera del primer alto**. |
| 14 | Fila de 3 botones: «✎ cuestionarios», «⚡ reacción», «◷ enfoque» (`Dia.kt:301-308`) | Siempre | Atajos a medidas ocasionales | a veces (cuestionarios unas 2 por mes; reacción y enfoque según la persona) | No | **Mueve**: cuestionarios y reacción ya salen como invitación en `TeFaltaHoy` cuando tocan (`Hoy.kt:107-111`); enfoque tiene modo rápido, widget y fila en Más. Siguen encontrables en Más → Medir (`Mas.kt:107`, `:109`), Más → Registros (`:64`) y el buscador (`Pantallas.kt:77`, `:78`, `:55`). Duplicación: ver §8. |
| 15 | `TarjetaNoche` «Llenar el día»: ○ ánimo, ○ diario, ○ predicción, hábitos n/N, botón «llenar/abrir» (`Dia.kt:343-364`) | Noche (`nocheYa`): arriba (:296). Si no, aquí (:309) | Ir a la hoja de la noche; ver qué falta | **a diario** | No | **Queda** y **sube** al primer alto en toda franja, como una línea compacta: es la acción diaria principal. Hoy, a mediodía, está en y ≈ 3800. |
| 16 | «Tu día en 24 horas»: `LineaDelTiempo` y «abrir la cinta completa →» (`Dia.kt:312-315`; `Hoy.kt:503-605`) | Siempre. Si no hay nada con hora: «Todavía no hay nada…» (`Hoy.kt:542`) | Ver el día por pistas: sueño, lugares, ejercicio, enfoque, momentos, teléfono, luz, música, fotos, gastos | a diario (mirar) | `plegable = true` (se recuerda) | **Pliega** por defecto (`plegadaPorDefecto`), con un resumen de una línea. La cinta queda en Vida (`Vida.kt:75`) y en el buscador (`Pantallas.kt:39`). |
| 17 | «Momentos de este día»: lista por hora y «abrir la hoja de la libreta →» (`Dia.kt:250-268`; :316) | `delDia` (no privados) o `sustanciasDia` (visibles) no vacíos (:251-253) | Lista de momentos y consumos del día | a diario si registras | `plegable = true`. Las filas **no se tocan** para editar (P1) | **Fusiona** con #16 (una sección «Tu día»: línea y lista) o con #19. Filtra bien lo privado (`!it.privado`, :251). |
| 18 | Bloque «Sueño» (posición fuera de la mañana) (`Dia.kt:317`) | `!manana` | Igual que #9 | a diario (en la mañana); el resto del día ya está lleno | Plegable | **Pliega** si `sueno_calidad != null`. |
| 19 | `TramosDelDia` «Enfoque y siestas» (`Dia.kt:833-847`; :319) | Hay tramos ese día (:835) | Lista de tramos | a veces | `plegable = true` | **Fusiona** con #17 (ya están como pista «enfoque» en #16). |
| 20 | Aviso de días especiales (`Dia.kt:321-323`) | `Especiales.de(fecha)` no vacío | Cumpleaños y fechas especiales | raro | No | **Queda** (corta y oportuna). |
| 21 | `HaceUnAnio` «Este día hace un año» (`ui/pantallas/Recuerdos.kt:74-118`; `Dia.kt:324`) | Hay algún recuerdo no vacío en los 5 años anteriores (Recuerdos.kt:81) | Recuerdos de la misma fecha | hoy raro (la app es nueva); con años de uso, a diario | `plegable = true` | **Queda** plegable, al fondo. No está en el buscador. |
| 22 | `AvisoExperimento` más «¿Lo hiciste hoy?» sí/no (`Dia.kt:870-886`; :326-328) | Experimento que cubre la fecha, `fecha <= hoy`, resultado no privado (:326) | Registrar la condición del experimento | a diario mientras dura un experimento | No | **Fusiona** con `TeFaltaHoy` (pendiente «Experimento: ¿lo hiciste hoy?») en Hoy. En otras fechas se queda donde está. Encontrable: `experimentos` (`Pantallas.kt:81`). |
| 23 | «pasar a la semana corta (solo lo mínimo)» (`Dia.kt:330-332`) | `esHoy` | Activar el modo mínimo | raro | — | **Mueve** a Apariencia o al menú de Hoy. **No está en el buscador**: hay que agregarlo («semana corta», «mínimo») antes de moverlo. |
| 24 | «Lo que registró el teléfono» (`Vestigios`, `Dia.kt:576-736`; :335-337) | Siempre | Datos automáticos (`clave = valor`), más «↻ sincronizar» y «sensores →» (:728-733) | a veces (consulta) | `autoPlegar = true`: se vuelve a plegar al cambiar de día o de sesión (`ui/Componentes.kt:184-190`) | **Queda** plegada al fondo (ya lo está). |
| — | `Portada` (`Hoy.kt:617-623`) | **Sin llamadas** en el código (`grep` solo encuentra la definición) | — | nunca | — | **Quitar (propuesta)**: es código muerto. Informativo, porque no ocupa pantalla. |

### Cuentas de Hoy

**Tarjetas o secciones**:
- 16 visibles en la captura de mediodía con datos v73: encabezado, estado, frase, lo de anoche, personaje, te falta hoy, quién es, tablero (con **6** tarjetas de cifras dentro), fila de 3 botones, llenar el día, 24 h, momentos, sueño, enfoque y siestas, enlace a semana corta y vestigios;
- **24 tipos posibles**: 21 a la vez si coincide todo.

**Elementos tocables** en la captura de mediodía (sin abrir nada): **unos 36**.
- Encabezado: 4 (buscar, Más, modo, fecha).
- Estado: 2.
- ♥: 1.
- Lo de anoche: 1.
- `TeFaltaHoy`: 5 (1 ficha, 3 invitaciones, «hoy no»).
- ¿Quién es?: 2.
- Tablero: 1.
- Fila de botones: 3.
- Llenar el día: 1.
- 24 h: 2 (encabezado plegable y cinta).
- Momentos: 2.
- Sueño: 8 (encabezado, 5 de escala, interruptor, «corregir las horas»).
- Tramos: 1.
- Semana corta: 1.
- Vestigios: 1.

Con «¿Recordaste un sueño?» activado se suman unos 20 (escalas, fichas de lucidez, vividez y tipo, categorías, etiquetas, editor, dictar). Cada falta de `DatosQueFaltan` suma 3.

**Condiciones que hacen crecer Hoy**:
- la hora: en la mañana el bloque de sueño (unos 700 px) va arriba;
- consumos sin completar (#5);
- personas sin clasificar (#12);
- fuentes caídas (#11, más o menos 150 px por falta);
- `sueno_recordado = true` (unos 1200 px más);
- número de momentos y consumos del día (unos 65 px por fila, #17);
- tramos (#19);
- días especiales;
- recuerdos de años anteriores (hasta 5 tarjetas);
- experimentos activos;
- número de pistas de la línea de 24 h (hasta 11);
- abrir «Lo que registró el teléfono» (de 600 a 1200 px);
- «editar tablero» abierto (16 filas).

**Posición en `diaV73Romano`** (y en px reales; escala de lectura 0,4):

| Elemento | y (px) |
|---|---|
| Encabezado | 0 a 330 |
| Estado | ≈ 400 |
| Frase | 500 a 750 |
| Lo de anoche | 775 a 1060 |
| Personaje | 1100 a 1325 |
| Te falta hoy | 1350 a 1930 |
| ¿Quién es? | 1960 a 2270 |
| **— límite de 2200 px —** | |
| Título TABVLA | ≈ 2350 |
| Cifras del tablero | 2590 a 3430 |
| Botones | 3575 a 3750 |
| Llenar el día | 3800 a 3990 |
| 24 h | 4075 a 4690 |
| Momentos | ≈ 4800 a 5780 |
| Sueño | ≈ 5800 a 6380 |
| Enfoque y siestas | ≈ 6400 a 7100 |
| Semana corta | ≈ 7370 |
| Vestigios | ≈ 7540 a 7600 |

---

## 2. El Día de una fecha (`PantallaDia` con `fecha != hoy`)

Es la misma función. Al no ser hoy, se apagan:
- `TarjetaPersonaje`, `LoDeAnoche`, `SugerirSemanaCorta` y `pendientes()`, que solo corren si `esHoy` (`Dia.kt:215-216`, `:270`, `:290`);
- el enlace a la semana corta (`:330`).

Además:
- aparece el botón «hoy» en la fila de estado (`:208`);
- para fechas pasadas, `nocheYa = true` (`:190`): «Llenar el día» va **arriba**, justo después de la frase;
- el bloque «Sueño» va después de «Momentos» (`:317`);
- el gesto de deslizar a los lados cambia de día (`:153-162`). Su alternativa visible son las flechas ‹ › (P7 cumplido).

| # | Elemento | Cuándo | Frecuencia | Propuesta |
|---|---|---|---|---|
| 1 | Encabezado y fecha que abre el calendario | Siempre | a diario | **Queda** |
| 2 | Estado ‹ › con [hoy] | Siempre | a diario | **Queda** |
| 3 | Frase del día **de esa fecha** (`Frases.del(fecha, tema)`, `Hoy.kt:297`) | Siempre | raro (útil solo como recuerdo) | **Pliega** o **quitar (propuesta)** en fechas que no son hoy |
| 4 | Aviso de fecha futura | `fecha > hoy` | raro | **Queda** |
| 5 | «Llenar el día» (`TarjetaNoche`) | Fecha pasada: arriba (:296). Fecha futura: después de los botones (:309) | a veces (rellenar ayer, P3) | **Queda** arriba |
| 6 | Tablero (cifras de esa fecha) | Siempre | a veces | **Queda** compacto |
| 7 | 3 botones: cuestionarios, reacción, enfoque | **Siempre, también en otras fechas**, aunque abren las de hoy | nunca útil en otra fecha | **Quitar (propuesta)** fuera de hoy (lo que abren no depende de la fecha) |
| 8 | 24 h | Siempre | a veces | **Queda** plegable |
| 9 | Momentos | Hay momentos o consumos | a veces | **Queda** (fusionado con 8) |
| 10 | Sueño, con «corregir las horas de sueño» | Siempre | a veces (corregir, P8) | **Queda** |
| 11 | Enfoque y siestas | Hay tramos | a veces | **Fusiona** con 9 |
| 12 | Especiales, hace un año, experimento | Según los datos | raro | **Queda** |
| 13 | Lo que registró el teléfono | Siempre (`autoPlegar`) | a veces | **Queda** plegada |

**Medición**: no hay captura de una fecha que no sea hoy. Con los datos de `diaV73Romano`, quitar #5, #6, #10, #12 y el enlace de Hoy (unos 290 + 230 + 580 + 310 + 100 px) da **unos 6000 px estimados, unas 2,7 pantallas**.

**Tocables**: unos 30. Sin `TeFaltaHoy` ni ¿Quién es?; con «hoy».

---

## 3. La noche (`PantallaNoche`, `ui/pantallas/Dia.kt:370-549`)

La regla de «solo lo que falta» (`c.noche_solo_falta`, por defecto `true`, `Modelo.kt:714`; `ver(sec)` en `Dia.kt:432`) **oculta las secciones que ya estaban llenas al abrir la hoja**:
- ánimo, si valencia, activación y dolor están llenos;
- contexto;
- diario;
- predicción;
- foto (`:422-431`).

Lo demás sale siempre.

| # | Tarjeta o sección (archivo:línea) | Cuándo aparece | Para qué sirve | Frecuencia | ¿Ya se oculta o pliega? | Propuesta |
|---|---|---|---|---|---|---|
| 1 | Encabezado de `Hoja` «Llenar el día», con ← atrás, fecha y «Se guarda solo…» (`Dia.kt:416-417`) | Siempre | Contexto y salir | a diario | — | **Queda** |
| 2 | «Ya está: ✓…» o «Todo el día en una hoja», más la ficha «solo lo que falta / mostrar todo» (`:433-437`) | Siempre | Elegir entre ver todo o solo lo pendiente | a diario | Es el interruptor (`config.noche_solo_falta`) | **Queda** |
| 3 | Hábitos: casillas (`:438-456`) | **Siempre** (no pasa por `ver`). Sin hábitos: «+ agregar hábitos» | Marcar hábitos | a diario | Sin pliegue. El hábito privado se excluye (`Privado.habitosVisibles`, `:439`) | **Queda**. Si no hay hábitos, **pliega** a una línea. |
| 4 | Ánimo y cuerpo: valencia de 1 a 7, activación de 1 a 7, dolor de 0 a 5, avisos del día (`:457-468`) | `ver("ánimo")` | Lo esencial del día | a diario | Se oculta si ya estaba lleno | **Queda** |
| 5 | Contexto: 10 fichas (`:469-476`) | `ver("contexto")` | Etiquetas del día | a diario | Igual | **Queda** |
| 6 | Diario: objeto del tema, dictar, etiquetas detectadas, presencia de personas, «abrir la biblioteca» (`:477-490`) | `ver("diario")` | Escribir la nota | a diario | Igual | **Queda** |
| 7 | Lo que leí o vi hoy: hasta 12 portadas, «buscar otra…», «ver obras» (`:491-493`; `ObrasDeHoy` en `ui/pantallas/Biblioteca.kt:596-622`) | Siempre | Marcar obras del día | a veces | `plegable` (se recuerda) | **Pliega** por defecto si no hay obras en curso. Si no, queda. |
| 8 | Foto del día: galería y foto elegida (`:494-496`; `Imagenes` en `:741-780`) | `ver("foto")` | Elegir la foto del día (con toque largo) | a veces | `plegable` | **Pliega** por defecto. El toque largo no tiene alternativa visible (P7). |
| 9 | Predicción de 1 a 7 (`:497-499`) | `ver("predicción")` | Predicción del ánimo de mañana | a diario | Se oculta si ya estaba llena | **Queda** |
| 10 | Rótulo «opcional» (`:502`) | Siempre | Separador | — | — | **Queda** |
| 11 | Lectura del día: 3 preguntas (`:503-519`) | Siempre | flow, SDT y apreciación | a veces | `plegadaPorDefecto` | **Queda** plegada |
| 12 | Lo mejor y lo peor (`:520-526`) | Siempre | Dos líneas | a veces | `plegadaPorDefecto` | **Queda** plegada |
| 13 | Hitos (`:527-529`) | Siempre | Etiquetas de hitos | raro | `plegadaPorDefecto` | **Queda** plegada |
| 14 | «privado de este día →» (`:531-533`) | Siempre | Ir a Privado con la fecha | según la persona | No | **Queda** discreto. No se propone mostrar nada privado aquí (`data/Privado.kt:3-7`). |
| 15 | Cómo estás ahora: `BloqueCierre`, cuadrícula de afecto de 7×7 (`:535`; `Lote2.kt:494-507`; `Selectores.kt:102`) | Siempre | Medición de cierre con la hora real | a diario | No | **Queda**, pero es el bloque más alto (unos 950 px). Podría ir **antes** de lo opcional (#10 a #13), o reducirse. |
| 16 | «Sellar el día / Guardar y volver», más lo que falta (`:537-541`) | Siempre | Cerrar | a diario | — | **Queda** |
| 17 | `BarraNoche` fija: progreso n de 4 y «siguiente →» (`:544-569`) | Siempre (flotante) | Saltar a lo que falta | a diario | — | **Queda**. Tapa unos 185 px del contenido y se suma a la barra de pestañas. |

**Cuentas de la noche**:
- **Tarjetas o secciones**: 13 secciones más la barra fija; 17 elementos con encabezado y botón.
- **Tocables** en la captura (2 hábitos, nada lleno, 5 portadas): **unos 63**.
  - Atrás y modo: 2.
  - Ficha de «solo lo que falta»: 1.
  - Hábitos: 2.
  - Escalas de ánimo: 7 + 7 + 6 = 20.
  - Contexto: 10.
  - Diario: 3 (objeto, dictar, biblioteca).
  - Obras: 1 + 5 + 2.
  - Foto: 2.
  - Predicción: 7.
  - Plegadas: 3.
  - Privado: 1.
  - Cuadrícula: 1 (49 celdas).
  - Sellar: 1.
  - «siguiente»: 1.

**Condiciones que hacen crecer la noche**:
- `noche_solo_falta = false` o el día vacío: todo visible;
- número de hábitos (unos 125 px cada uno);
- `@personas` en la nota: fichas de presencia y sugerencias de corrección (`Detectados`, `:792-820`);
- obras en curso (fila de portadas);
- permiso de fotos;
- abrir las 3 plegadas: Lectura del día mide unos 900 px abierta.

**Primer alto** (`nocheV67Seda` y `nocheMedieval`, unos 2200 px):
- encabezado;
- «solo lo que falta»;
- Hábitos;
- Ánimo y cuerpo completo (unos 1100 px).

El Diario (la acción principal de la noche) empieza en y ≈ 3150 a 3300: **segunda pantalla**.

---

## 4. Diario (pestaña `diario` → `PantallaDiario`, `ui/pantallas/Diarios.kt:85-184`)

La ruta `diario` se dibuja con `PantallaDiario` (`ui/MainActivity.kt:324`). Ojo:
- el **toque largo** en la pestaña Diario abre el ánimo rápido (`MainActivity.kt:292`);
- el enlace profundo `abrir(…,"diario")` lleva a la hoja de la noche, no a esta pestaña (`MainActivity.kt:249`, `:275`).

| # | Tarjeta o sección (archivo:línea) | Cuándo aparece | Para qué sirve | Frecuencia | ¿Ya se oculta o pliega? | Propuesta |
|---|---|---|---|---|---|---|
| 1 | Encabezado de `Hoja` «Diario» con 🔍 y ⚙ (`Diarios.kt:121`) | Siempre | — | a diario | — | **Queda** |
| 2 | `BotonAnimo` «¿Cómo estás ahora?», con el último registro (`:123`; `:188-200`) | Siempre | Registro de ánimo rápido (QuickActivity) | **a diario** | No | **Queda** arriba |
| 3 | «Hoy»: objeto del diario del tema (`:126-128`) | Siempre | Escribir el diario de hoy a pantalla completa | **a diario** | No | **Queda** |
| 4 | Biblioteca del diario: estante con las últimas 9 entradas, «entrar a la biblioteca →» y «☾ sueños», «✦ momentos», «✉ cartas» (`:131-140`) | Siempre. Sin entradas: «Aquí se irán guardando…» | Leer entradas | a veces | No | **Pliega** el estante. Su alto depende del tema: de 700 a 1500 px (Colonos frente a Napoleón). Se dejan los 4 botones en una fila. «cartas» y «sueños» están repetidos en Más (`Mas.kt:77`, `:79`): ver §8. |
| 5 | Línea de tiempo: buscador, filtros (tipo, años, con foto, largas), tarjetas por día y «ver 30 días más» (`:142-169`) | Siempre | Buscar y releer | a veces | `autoPlegar`: se repliega al cerrar la app o al cambiar de día | **Queda** plegada |
| 6 | Exportar: PDF, HTML, Markdown, Obsidian y Texto (`:171-180`) | Siempre | Exportar entradas | raro | `autoPlegar` | **Mueve** a Más → Datos o a la biblioteca del diario. **Pliega** como está mientras tanto. |

**Cuentas del Diario**:
- **Tarjetas o secciones**: 6.
- **Tocables plegado**: unos 20.
  - Encabezado: 3.
  - Botón de ánimo: 1.
  - Objeto del diario: 1.
  - Estante: hasta 9.
  - «entrar»: 1.
  - Sueños, momentos y cartas: 3.
  - Dos encabezados plegables: 2.
- **Abierta la línea de tiempo**:
  - 1 buscador;
  - 4 fichas de tipo, N de años y 2 más;
  - hasta 30 tarjetas de día, cada una con 2 + 8 tocables;
  - «ver más».

**Crece con**: el tema (alto del estante), abrir la línea de tiempo (unos 30 × 250 px) y abrir Exportar.

**Medición**: de 2470 px (Colonos) a 3640 px (Napoleón). **Es la pestaña que mejor cumple P5**: ánimo y diario de hoy en los primeros 900 px.

---

## 5. Vida (`PantallaVida`, `ui/pantallas/Vida.kt:60-104`)

| # | Tarjeta o sección (archivo:línea) | Cuándo | Para qué sirve | Frecuencia | ¿Ocultable? | Propuesta |
|---|---|---|---|---|---|---|
| 1 | Encabezado y subtítulo (`Vida.kt:72`) | Siempre | — | — | — | **Queda** |
| 2 | Rejilla de 8 `Tarjetita` (`:73-84`): Resumen semanal (ánimo y flecha), La cinta, Calendario, Tu año, Tu gente (con «N por clasificar»), Constelación, Totales, Mapa | Siempre | Entradas con cifra viva | Resumen semanal: **semanal**. Cinta: a diario o a veces. Calendario y Tu año: a veces. Tu gente: a veces. Constelación, Totales y Mapa: raro | No | **Queda** la rejilla (es el «a un toque» de lo semanal). Totales está repetido en Análisis (`Explorar.kt:105`): ver §8. |
| 3 | `RecuadroTuAnio` (`TuAnio.kt:755-774`; Vida.kt:86), 110 dp | Siempre | Tu año en historias | raro (anual) | No | **Mueve** a una `Tarjetita` en la rejilla, o **pliega** con todos los recuadros bajo «Galerías ▸» |
| 4 | `RecuadroLogros` (`Logros.kt:116-136`; :87), 118 dp | Siempre | Medallas | a veces | No | **Queda** con «¡N nuevos!» o **pliega** en «Galerías» |
| 5 | `RecuadroCampanas` (`Campanas.kt:223-232`; :88), 112 dp | Siempre (vacío: «elige un reto») | Reto de la semana | **semanal** | No | **Queda** (es semanal), compacto |
| 6 | `RecuadroMapas` (`Galerias.kt:190-197`; :89), 168 dp | Siempre | Mapa para rascar | raro | No | **Pliega** en «Galerías» |
| 7 | `RecuadroObras` (`Galerias.kt:200-226`; :90), 168 dp | Siempre | Portadas | a veces | No | **Pliega** en «Galerías» |
| 8 | `RecuadroOtrasVidas` (`OtrasVidas.kt:227-249`; :91), 150 dp | Siempre | Comparación histórica | raro | No | **Pliega** en «Galerías» o **mueve** a Más |
| 9 | «Dinero y sustancias»: Dinero, Sustancias (días con algo en 7 días, solo las visibles) y Enfoque y siestas (`:93-102`) | Siempre | Cifras del mes y de la semana | a veces o semanal | No | **Sube** sobre los recuadros (son cifras semanales). Respeta `Sustancias.visible` (:96). |

**Cuentas de Vida**:
- **Tarjetas**: 17 (8 + 6 + 3).
- **Tocables**: 20 (17 tarjetas y 3 del encabezado).
- **Crece**: casi nada; el alto es fijo, unos 5600 a 5700 px. Lo dan los dibujos de los recuadros (de 110 a 168 dp, más el pie de unos 70 dp cada uno). Solo los textos varían.
- **Primer alto** (`vidaRomano`): la rejilla de 8 (hasta y ≈ 1330), «Tu año en historias» y «Logros». Campañas (semanal) queda en la segunda pantalla. «Dinero y sustancias», en la tercera.

---

## 6. Análisis (pestaña `entender` → `PantallaExplorar`, `ui/pantallas/Explorar.kt:68-284`)

| # | Tarjeta o sección (archivo:línea) | Cuándo | Para qué sirve | Frecuencia | ¿Ocultable? | Propuesta |
|---|---|---|---|---|---|---|
| 1 | Encabezado y subtítulo (`Explorar.kt:101-102`) | Siempre | — | — | — | **Queda** |
| 2 | 2 `Tarjetita`: Experimentos (en curso) y Totales (`:103-106`) | Siempre | Entradas | Experimentos: semanal. Totales: raro | No | Experimentos **queda**. Totales **fusiona**: está repetido en Vida. |
| 3 | 3 cifras: días con diario, registros propios, avisos respondidos (`:108-112`) | Siempre | Volumen de datos | raro | No | **Fusiona** con #4 en una línea |
| 4 | Ayuda de potencia («Faltan N días… pista, no evidencia») (`:113`) | Siempre (cambia el texto con 90 días o más) | Advertencia metodológica | a veces | No | **Queda**, una línea |
| 5 | Herramientas: 8 `FilaMenu` (`:115-122`; `HERRAMIENTAS` en `ui/pantallas/Analisis.kt:71-80`) | Siempre | Análisis finos | a veces | No | **Pliega** o pasa a una rejilla 2×4 de fichas. Hoy ocupa unos 1500 px del primer alto. |
| 6 | Cobertura: mapa de calor de 16 semanas y enlace (`:124-127`) | Siempre | Huecos de registro | raro | No | **Mueve**: ya está «Cobertura de variables» en Más → Sistema (`Mas.kt:133`) y en el buscador (`Pantallas.kt:66`). O **pliega**. |
| 7 | Fichas «por día / por semana» y ayuda (`:129-135`) | Siempre | Granularidad | a veces | — | **Queda**, junto a #9 |
| 8 | Cruzar dos variables: X, Y, desfase, dispersión, r, ρ, n, texto (`:137-167`) | Siempre. Sin datos: «Hacen falta al menos 3…» | Correlación a mano | a veces | No | **Pliega** («Cruzar ▸») |
| 9 | Las relaciones más marcadas: 9 focos, 2 interruptores, hasta 15 `TarjetaRelacion` (`:169-219`; `agrupar(lista, 15)` en :189) | Siempre. Vacío con menos de 14 días | **Lo principal** de la pestaña | semanal | No | **Sube** al primer alto, con las 3 a 5 primeras y «ver más» |
| 10 | ¿Qué tan bien te conoces? (`:221-232`) | Siempre (vacío con menos de 5 pares) | Calibración de la predicción | raro | No | **Pliega** |
| 11 | A qué hora aparecen tus emociones (`:234-250`) | Siempre (vacío con menos de 5 registros) | Emociones por hora | raro | No | **Pliega** |
| 12 | `ConQuien` (`:252`; `:506-533`) | Hay momentos de ánimo con `con_quien` | Ánimo según compañía | raro | No | **Pliega** |
| 13 | Personas, temas e hitos: 3 listas de hasta 12 (`:253-263`; `Lista` en :323-337) | Siempre | Menciones | raro | No | **Pliega** o **mueve** a Totales (ya hay enlace en :262) |
| 14 | Lugares y ánimo (`:265-276`) | Siempre | Ánimo por lugar | raro | No | **Pliega** |
| 15 | `EtapasDeVida` «Antes y después» (`:476-499`; :278) | Hay `perfil_historial` y panel | Comparar 60 días antes y después | raro | No | **Pliega** |

**Cuentas de Análisis**:
- **Tarjetas o secciones**: 15 bloques. Dentro de ellos van hasta 15 tarjetas de relación y 3 listas.
- **Tocables**: unos 50 sin contar las listas.
  - Encabezado: 3.
  - Tarjetitas: 2.
  - Herramientas: 8.
  - Enlace de cobertura: 1.
  - Por día o por semana: 2.
  - Ejes X e Y: 2.
  - Interruptor: 1.
  - Focos: 9.
  - Interruptores: 2.
  - Relaciones: 15.
  - Enlace a Totales: 1.
  - Lugares: N, más el enlace al mapa: 1.

  Las listas suman hasta 36 filas tocables.

**Crece con**: días de datos (más relaciones), número de lugares y ciudades, emociones registradas, cambios de perfil.

**Primer alto** (`entenderRomano`): tarjetitas, cifras, ayuda y **las 7 primeras herramientas**. Nada de resultados: las relaciones empiezan, estimado, en y ≈ 4500 o más.

**Única pestaña sin ninguna sección plegable.**

---

## 7. Más (`PantallaMas`, `ui/pantallas/Mas.kt:47-148`)

Notas previas:
- Más **no es pestaña**: se abre con ⚙ (`BotonAjustes`) desde el encabezado de Hoy, Diario, Vida y Análisis (por ejemplo `Dia.kt:172` y `Vida.kt:72`).
- `ui/ContenedoresMas.kt`, `ui/SalonesMas.kt` y `ui/MapasMas.kt` **no son la pantalla Más**. Son el dibujo de libretas, salones y mapas de campaña de los temas del «lote 2» (`ContenedoresMas.kt:22-27`, `SalonesMas.kt:22-28`, `MapasMas.kt:20-25`). No intervienen aquí.
- El § 4 del plan dice que Más tiene además «Activos, Archivados, Cómo leer tus datos, Cuestionarios y Marcos y referencias». **No es así**:
  - Activos y Archivados son de `PantallaHabitos` (`Mas.kt:162`, `:187`);
  - Cómo leer tus datos, Cuestionarios y Marcos y referencias son de `PantallaGuia` (`Mas.kt:203`, `:211`, `:221`);
  - viven en el mismo archivo, pero son otras pantallas.

| # | Sección (archivo:línea) | Cuándo | Para qué sirve | Frecuencia | ¿Ocultable o movible? | Propuesta |
|---|---|---|---|---|---|---|
| 1 | Encabezado «Más» con ← y 🔍 (`Mas.kt:53`) | Siempre | — | — | — | **Queda**. El buscador es la vía principal para lo raro. |
| 2 | `AtajosFijos` «Tus atajos» (`Pantallas.kt:144-161`; Mas.kt:54) | `favoritos_mas` no vacío | Pantallas fijadas con ★ | a diario o a veces | Sí: ★ en cada fila (`Pantallas.kt:136-138`) | **Queda**. Es el mecanismo «mover» que ya existe; podría reutilizarse en Hoy o Vida. |
| 3 | 1 · Registros especiales: 9 filas (Escalada, Gimnasio, Enfoque y siestas, Hábitos, Suplementos, Notas, Metas, Cartas, Biblioteca de sueños, Frases guardadas) (`:56-83`) | Siempre | Registros no diarios | a veces | ★ para fijar | **Queda**. Enfoque, Cartas y Sueños están repetidos fuera: ver §8. |
| 4 | 2 · Tu gente y sustancias: 5 filas (Tu gente, Red, Tus temas, Sustancias, **Privado**) (`:84-98`) | Siempre | Gente y consumo | a veces | ★ | **Queda**. «Privado» es su única entrada visible (además del enlace de la noche, `Dia.kt:531`). Debe quedarse aquí y discreta. |
| 5 | 3 · Medir: 7 filas (Salud, Tus apps, Laptop, Cuestionarios, Prueba de reacción, Brújula política, Conocerte) (`:99-118`) | Siempre | Instrumentos | raro o a veces | ★ | **Queda** (aquí debe vivir lo raro, P5) |
| 6 | 4 · Sistema: 11 filas (Guía, Apariencia, Avisos, Privacidad, Datos, Estado de los datos, Cobertura, Lugares especiales, Reportes, Permisos, Guía científica) (`:119-143`) | Siempre | Ajustes | raro | ★ | **Queda**. «Guía» y «Guía científica» tienen nombres casi iguales (P6). |
| 7 | Ayuda de los atajos de toque largo (`:144-145`) | Siempre | Explicar los gestos ocultos (P7) | raro | — | **Queda**, pero los gestos deberían tener alternativa visible además de este texto |
| 8 | Versión y `schema_v` (`:146`) | Siempre | — | nunca | — | **Queda** (pie) |

**Cuentas de Más**:
- **Tarjetas**: 4 tarjetas con 32 `FilaMas`, más los atajos.
- **Tocables**: unos **67**.
  - 32 filas × 2 (fila y ★) = 64.
  - Atrás, buscar y modo: 3.
  - Más los atajos fijados.
- **Crece con**: atajos fijados (unas 2 filas de fichas cada 3).

**Primer alto**: atajos y la sección 1 entera. La sección «Sistema» empieza, estimado, en y ≈ 5900.

**Medición estimada**: unas 7900 px sin atajos, unas 8400 con 3.

Es largo, pero **es una lista**: para P5 está bien que lo raro esté aquí. El problema es la profundidad de scroll hasta Sistema y Permisos. Propuesta: secciones plegables con su número a la vista, o un índice de 4 fichas arriba.

---

## 8. Duplicados entre pantallas (fusiones posibles, P6)

| Acceso | Dónde aparece |
|---|---|
| Cuestionarios | Hoy, botón (`Dia.kt:302`) · `TeFaltaHoy` cuando toca (`Hoy.kt:107-109`) · Más → Medir (`Mas.kt:107`) · buscador (`Pantallas.kt:77`) |
| Reacción | Hoy, botón (`Dia.kt:303`) · `TeFaltaHoy` (`Hoy.kt:111`) · Más (`Mas.kt:109`) · buscador (`:78`) |
| Enfoque y siestas | Hoy, botón (`Dia.kt:304`) · Hoy, sección (`Dia.kt:319`) · Vida (`Vida.kt:100`) · Más (`Mas.kt:64`) · buscador (`:55`) |
| La cinta | Hoy, enlace (`Dia.kt:314`) · Vida (`Vida.kt:75`) · buscador (`:39`) |
| Totales | Vida (`Vida.kt:82`) · Análisis (`Explorar.kt:105`) · enlace en Análisis (`:262`) |
| Tu gente | Hoy, «¿Quién es?» (`Dia.kt:286`) · Vida (`Vida.kt:79`) · Más (`Mas.kt:87`) |
| Sustancias | Vida (`Vida.kt:99`) · Más (`Mas.kt:94`) |
| Cartas y sueños | Diario (`Diarios.kt:136`, `:138`) · Más (`Mas.kt:77`, `:79`) |
| Cobertura | Análisis (`Explorar.kt:124-127`) · Más (`Mas.kt:133`) |
| Momentos del día | Hoy, «Momentos de este día» · Hoy, pista «momentos» en 24 h · Diario, línea de tiempo · Vida, cinta |

---

## 9. Hoy: el primer alto de pantalla, hoy y según P5

### 9.1 Lo que hoy cabe en los primeros 2200 px

**A mediodía**, con los datos de `diaV73Romano`:
1. encabezado: «DIES», fecha grande, día y luna, y 4 botones (`Dia.kt:164-182`);
2. fila de estado ‹ guardado · racha 35 › (`:193-210`);
3. **Frase del día** (`:214`);
4. «¿Cómo terminó lo de anoche?» (`:216`);
5. **Personaje** con «¡35 días seguidos…!» (`:290`);
6. **Te falta hoy**: Calidad del sueño, más 3 invitaciones (`:272`);
7. «¿Quién es @marta?» (`:286`), cortado en el borde.

**No caben**:
- el Tablero (empieza en y ≈ 2350);
- **«Llenar el día»** (y ≈ 3800);
- la línea de 24 h;
- los momentos del día.

**En la mañana** (de 5 a 11 h): en lugar de 6 y 7 entra el bloque «Sueño» completo (`:295`), y `TeFaltaHoy` queda a media altura o debajo.

**En la noche** (desde las 18 h): «Llenar el día» sí entra (`:296`), pero lo empujan hacia abajo la frase y el personaje.

**No hay ningún botón de registro de ánimo en Hoy**:
- está en la pestaña Diario (`Diarios.kt:123`), en el ⊕ y en la semana corta (`Lote2.kt:538`);
- `TeFaltaHoy` solo lo ofrece si hay avisos sin contestar (`Hoy.kt:105-106`).

### 9.2 Lo que debería estar según P5

**Primer alto (todos los días)**, en este orden:
1. Fecha y estado (P2), en una franja compacta.
2. `TeFaltaHoy`, ampliado por fusión con:
   - lo de anoche (#5);
   - el experimento del día (#22);
   - ¿quién es @x? (#12), como invitación;
   - un contador de `DatosQueFaltan` (#11).
3. **«Llenar el día»** en forma compacta (una línea con ○ ánimo ○ diario ○ predicción · hábitos n/N y el botón), a cualquier hora.
4. **Tablero compacto**: las primeras 3 o 4 cifras de `config.tablero`, que ya se elige y reordena, con «ver todo».
5. En la mañana, solo si falta, la calidad del sueño. El bloque completo puede abrirse desde la ficha «Calidad del sueño →», que ya hace `mostrar("Sueño")` (`Dia.kt:131-134`, `:275`).
6. Opcional: la frase del día **en una línea**.

Presupuesto aproximado:

| Bloque | px |
|---|---|
| Encabezado | 330 |
| Estado | 100 |
| Te falta hoy | 450 a 600 |
| Llenar el día | 200 |
| Tablero compacto | 350 a 500 |
| Frase en una línea | 120 |
| **Total** | **1550 a 1850** |

Cabe en 2050 px reales. Como referencia, la semana corta mide 871 px.

**A un toque (lo semanal)**:
- resumen semanal y campañas: en Vida, en la rejilla; el toque largo en Vida abre la semana, con alternativa visible en `Vida.kt:74`;
- cuestionarios: cuando tocan, como invitación de `TeFaltaHoy`;
- línea de 24 h y momentos del día: plegados en Hoy, en una sola sección «Tu día», y la cinta en Vida;
- «editar tablero».

**Lo raro, en Más o en el buscador**:
- fila de 3 botones: cuestionarios (`escalas`), reacción (`reaccion`) y enfoque (`enfoque`), que ya están en Más y en el buscador;
- personaje: ya se apaga en Apariencia (`Sistema.kt:315`);
- «pasar a la semana corta»: **hay que agregarla al buscador o a Apariencia** antes de moverla;
- brújula política: invitación anual, más Más → Medir;
- «Lo que registró el teléfono»: ya plegado al fondo;
- «Hace un año»: plegado al fondo.

**Lo que se esconde y por dónde se encuentra** (todo lo propuesto queda encontrable):

| Se esconde o mueve | Se encuentra en |
|---|---|
| Cuestionarios | `Pantallas.kt:77`, `Mas.kt:107`, `TeFaltaHoy` |
| Reacción | `Pantallas.kt:78`, `Mas.kt:109`, `TeFaltaHoy` |
| Enfoque | `Pantallas.kt:55`, `Mas.kt:64`, `Vida.kt:100` |
| Cinta | `Pantallas.kt:39`, `Vida.kt:75` |
| Tu gente | `Pantallas.kt:49`, `Vida.kt:79`, `Mas.kt:87` (sumar «clasificar» y «quién es» a `palabras`) |
| Estado de los datos | `Pantallas.kt:88`, `Mas.kt:131` |
| Frases guardadas | `Pantallas.kt:70`, `Mas.kt:81` |
| Personaje | Apariencia (`Pantallas.kt:84`, palabra «personaje») |
| Semana corta | **falta**: añadir una entrada al buscador o un interruptor en Apariencia |
| Hace un año | **no está en el buscador**: sigue plegado en el Día de cada fecha |

**Privacidad**:
- Nada de lo propuesto saca a la vista impulsos, recaídas, libido, medicamentos ni detonantes (`data/Privado.kt:3-7`).
- Los únicos accesos a Privado siguen siendo:
  - Más → «Privado» (`Mas.kt:96`);
  - «privado de este día →» en la noche (`Dia.kt:531`).
- Los momentos privados ya se filtran en Hoy (`Dia.kt:251`), en la línea (`Hoy.kt:531`) y en el Diario (`Diarios.kt:104`).
- El hábito ligado a recaídas se excluye en la noche y en «Llenar el día» (`Dia.kt:345`, `:409`, `:439`).
- Las sustancias no son «privado» según `Privado.kt`, pero tienen `sustancias_ocultas`. Al fusionar «Lo de anoche» en `TeFaltaHoy` se conserva el texto anónimo de `Dia.kt:859`.

---

## 10. Otras observaciones (para la Fase 5)

- **P1**: en Hoy no se pueden tocar:
  - las cifras del Tablero (`Hoy.kt:450`);
  - las filas de «Momentos de este día» (`Dia.kt:257-262`);
  - las de «Enfoque y siestas» (`Dia.kt:839-844`).

  Para editarlas hay que ir a la libreta o a la cinta. En el Diario las filas de momentos sí abren `DialogoMomento` (`Diarios.kt:226`).
- **P2**: `PantallaDia` arranca en `Seleccion.diaAbierto` (`Dia.kt:103-105`). Si antes abriste otra fecha, la pestaña «Hoy» puede mostrar esa fecha. El título dice «Día», no «Hoy» (`Dia.kt:164`, `:169`), pero la pestaña dice «Hoy» (`MainActivity.kt:185`). Es un posible choque P6.
- **P7**: hay dos gestos con texto o alternativa:
  - la foto del día se elige con **toque largo** (`Dia.kt:767`); solo lo explica el texto de ayuda;
  - deslizar para cambiar de día tiene las flechas como alternativa (bien).
- **Accesibilidad**: el botón «✎ cuestionarios» parte el texto en 3 líneas («cuestionari / os») en romano (`diaV73Romano`, y ≈ 3600). La fila de 3 botones `weight(1f)` no cabe a 1080 px.
- **Capturas**: para medir de verdad (Fase 6), hay que:
  - subir `screenHeight` (Hoy ≥ 9000, noche ≥ 9000, Más ≥ 9000, Vida ≥ 6000, Análisis ≥ 14 000);
  - limpiar el estado entre tests (consumos, tramos y obras que se filtran);
  - fijar la hora; hoy el orden depende del reloj del CI (`LocalTime.now()` en `Dia.kt:186` y `Hoy.kt:93`).
