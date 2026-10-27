# Revisión de navegación, corrección de datos y saturación

_Actualizada el 27 oct 2026 con tus respuestas a las preguntas: § 13 y propuestas N-28 a N-30. Hecha el 27 oct 2026 sobre `main` en el commit `87f7f2c`: versión con la entrega 10b, esquema 13. Sigue `docs/navegacion/PLAN_REVISION_NAVEGACION.md`. **No cambia nada de la app.** Solo agrega capturas de prueba con datos inventados (`app/src/test/.../CapturasEscenario.kt`). Todo lo que aquí se propone espera tu aprobación, punto por punto._

**Cómo leer este informe**

- Las rutas de archivo son relativas a `app/src/main/java/io/github/ruddy2003/vestigium/`, salvo que se diga otra cosa.
- «Toques» cuenta los toques **desde que se abre la app**, que arranca en la pestaña **Hoy**. No cuenta los desplazamientos.
- Cada paso se marca así:
  - **(v) visible**: está a la vista;
  - **(d) descubrible**: está en pantalla, pero hay que bajar o leer;
  - **(e) escondido**: toque largo, sección plegada, menú «⋯», dentro de un diálogo o al fondo de una pantalla larga.
- El detalle completo, con archivo:línea en cada celda, está en cinco anexos (`docs/navegacion/anexos/`). Aquí va el resumen de cada uno y lo que hace falta para decidir:
  - **A** · Mapa de rutas y entradas desde fuera: `A_mapa_rutas.md`.
  - **B** · Matriz de datos × operaciones: `B_matriz_datos.md`.
  - **C** · Datos automáticos, Deshacer, día lógico, `DialogoMomento` y `Seleccion`: `C_automaticos_deshacer_dia_logico.md`.
  - **D** · Inventario de saturación: `D_saturacion.md`.
  - **E** · Heurísticas y accesibilidad: `E_heuristicas.md`.
- La persona de los escenarios es inventada: «Ana», 30 años, oficinista. Lo mismo los nombres de su gente, sus lugares («Café Centro») y sus cifras.

---

## 1. Resumen

### 1.1 Las 10 fricciones más graves

1. **La creatina de ayer no se puede anotar** (E-11, E-22). Una toma de suplemento se guarda siempre con la hora actual (`data/Lote3.kt:31-34`). Las tomas de días anteriores no se pueden ni corregir ni borrar (`ui/pantallas/Lote3.kt:100`, `:138-147`). En ⊕, cada toque en «Tomé…» suma otra toma aunque ya diga «✓» (`ui/QuickActivity.kt:234`). Es exactamente el segundo ejemplo que diste.
2. **Corregir un momento es difícil, y la hora no se puede corregir** (E-12, E-15).
   - En Hoy y en el Día de cualquier fecha, la lista «Momentos de este día» no se puede tocar (`ui/pantallas/Dia.kt:250-268`). Tampoco la línea de 24 horas, la cinta ni la libreta.
   - El único camino es Diario → «Línea de tiempo», que está plegada → buscar el día → tocar la fila (`ui/pantallas/Diarios.kt:142`, `:226`).
   - Ahí se cambian la emoción, el pensamiento, la valencia y el texto, pero **no la hora ni la fecha**, salvo en una recaída.
   - La pantalla «Momentos», que tenía la lista editable, ya no se abre desde ningún lado (`ui/MainActivity.kt:251`, `:325`).
   - Es tu tercer ejemplo.
3. **Las cifras que ves no se corrigen ahí** (E-10, E-19, E-01).
   - El sueño de 3 h sale en el Tablero, pero esa tarjeta no se puede tocar (`ui/pantallas/Hoy.kt:450`).
   - Para corregirlo hay que buscar «corregir las horas de sueño» dentro de la sección Sueño (`Dia.kt:243-246`). En la mañana está arriba; el resto del día queda varias pantallas abajo: en el miércoles de Ana, a unos 4400 px del inicio (Fase 6).
   - La siesta que el reloj inventó no se puede quitar en ningún lado.
   - Es tu primer ejemplo.
4. **No hay un lugar donde ver y corregir todo lo de un día** (E-21, E-22).
   - El Día de ayer deja llenar el ánimo, el diario, los hábitos, el sueño y el experimento.
   - Los momentos, los tramos y lo automático solo se ven.
   - Los gastos, las tomas y las notas de ese día ni siquiera aparecen.
   - Cada pantalla de registro (Suplementos, Notas, Cuestionarios…) guarda siempre en hoy (anexo C, § 5).
5. **Los lugares no se corrigen** (E-18).
   - No se puede renombrar un lugar, cambiar su radio ni decir «esa visita fue en otro sitio» (`ui/pantallas/Medir.kt:288-295`).
   - Borrar el lugar deja sus visitas huérfanas (`data/Repo.kt:305`).
   - «No guardar nunca aquí» solo existe para sitios que aún no guardaste (`ui/pantallas/Lote2.kt:186`).
6. **Borrar casi nunca se puede deshacer** (E-20, E-38).
   - De 34 maneras de borrar, 20 no tienen Deshacer (anexo C, § 2.2). Notas, metas, fechas propias y logros propios se borran con un toque, sin confirmar.
   - La barra de Deshacer solo existe en la ventana principal, no en el registro rápido (`MainActivity.kt:404`).
   - No hay papelera: la copia diaria del respaldo se reemplaza cada mañana.
7. **Hoy está saturado y lo diario no está arriba** (E-07, E-08, E-09).
   - Hoy mide unos 6000 a 7600 px (de 3 a 3,5 alturas de pantalla).
   - A mediodía, el primer alto lo ocupan la frase del día, el personaje, «Te falta hoy» y «¿Quién es @x?».
   - El Tablero empieza fuera, hacia y ≈ 2350. «Llenar el día» queda hacia y ≈ 3800. La respuesta del experimento está casi al fondo.
8. **No se avisa a qué día va lo que anotas de madrugada** (E-14).
   - El aviso «Pasa de medianoche…» solo sale en el Día (`Dia.kt:291`).
   - El registro rápido muestra la fecha con el estilo del tema (números romanos, «JORNADA 287»…), sin decir «esto va a ayer».
   - Notas no muestra la fecha. Una nota no se puede mover de día.
9. **Un mismo nombre para cosas distintas, y nombres distintos para la misma cosa** (E-32, E-33, E-37).
   - La pestaña «Hoy» abre una pantalla titulada «Día», que puede estar en otra fecha.
   - «Diario» significa cuatro cosas distintas.
   - Hay dos pantallas «Obras» y dos «Análisis».
   - Los temas cambian el título de casi todas las pantallas (anexo E, § 2). En medieval, la pestaña «Análisis» se titula «Quaestiones», y sus tarjetas, «Pruebas» y «Suma de todo»; el Día se llama «Del día» y la noche «Completas» (capturas de Ana).
10. **El buscador y Más no alcanzan para encontrar lo raro** (E-33).
    - El buscador lista 55 de 79 rutas. Faltan, por ejemplo, Lugares (donde se corrigen y borran lugares), la pestaña Diario y las herramientas de Análisis.
    - Busca la frase entera, sin palabras sueltas, y muestra 8 resultados como máximo. No conoce palabras como «corregir», «borrar» o «ayer».
    - Más mide unas cuatro pantallas, está detrás de una tuerca de 36 dp y no es pestaña.

### 1.2 Principios propuestos

Los del plan (§ 7) se confirman. Abajo, cómo queda cada uno y dos nuevos:

| Principio | Qué pide | Estado hoy |
|---|---|---|
| **P1 · Corregir donde se ve** | Tocar una cifra, una fila o un bloque abre su corrección. | Falla en el Tablero, la línea de 24 h, la cinta, «Momentos de este día» y «Enfoque y siestas». Se cumple en Gastos y en Diario → Línea de tiempo. |
| **P2 · La fecha siempre visible y cambiable** | Toda pantalla de registro dice a qué día va lo que anotas y deja cambiarlo. | Se cumple en el Día, la noche, Gastos, Sustancias y Enfoque. Falla en el ánimo rápido, el sueño rápido, Suplementos, Notas, Cuestionarios y Reacción. |
| **P3 · Ayer a un toque** | Rellenar lo olvidado sin recorrer pantallas. | A medias: «Diario de ayer» en «Te falta hoy», solo antes de las 14 h y solo para ayer. |
| **P4 · Todo borrado se deshace** | Deshacer en todos los borrados y confirmación en los de un toque. | Falla en 20 de 34. |
| **P5 · Lo diario en el primer alto** | Lo de cada día, arriba; lo semanal, a un toque; lo raro, en Más o el buscador. | Falla en Hoy, la noche y Análisis. Se cumple en Diario y en el registro rápido. |
| **P6 · Un solo nombre para cada cosa** | El mismo nombre en la pestaña, el título, el buscador y Más. | Falla en muchos lugares (§ 7). |
| **P7 · Los gestos ocultos tienen alternativa visible** | Todo toque largo o deslizar tiene otro camino. | Se cumple en la barra y en el Día. Falla en la foto del día, marcar un lugar en el mapa, el zoom del mapa para rascar y las historias de Tu año. |
| **P8 · Lo automático se corrige o se ignora** | Sueño, siestas, lugares, pantalla. | Solo la noche, las fotos con lugar, los países y los sitios candidatos. |
| **P9 · El día lógico se explica cuando importa** | Antes de las 05:00, decir a qué día va. | Solo en el Día y en el selector de hora. |
| **P10 · Alcance con una mano** | Lo frecuente, en la mitad de abajo. | Se cumple en el registro rápido y la noche. Fallan guardar el diario (arriba a la derecha) y la tuerca de Más. |
| **P11 · El Día es el lugar donde se corrige** (nuevo) | El Día de cualquier fecha muestra **todo** lo registrado ese día (ánimo, momentos, tomas, gastos, notas, tramos, consumos, lo automático) y cada cosa se corrige tocándola ahí. | No existe. Une P1, P2 y P3 en un solo lugar que ya conoces, sin pantallas nuevas. |
| **P12 · Lo anotado tarde queda marcado** (nuevo, metodológico) | Lo que se registra para otra fecha o se corrige después guarda cuándo se hizo. | Solo el `Dia` (`retroactivo`, `ts_valencia`). Lo necesitan las propuestas N-03, N-07, N-08 y N-09 (H-NAV-9, H-2-9). |

### 1.3 En números

- **30 propuestas** (N-01 a N-30; las tres últimas salen de tus respuestas), en **8 entregas** (§ 10).
- Las que resuelven tus tres ejemplos: **N-01 y N-04** (sueño), **N-07 y N-09** (creatina de ayer), **N-02 y N-03** (momento mal guardado).
- **Cambian lo que se guarda**, y por eso suben `SCHEMA_V`:
  - N-04 (siestas descartadas);
  - N-30, con N-03 y N-08 (cuándo se anotó y cuándo se corrigió);
  - N-18 (`version_noche`);
  - N-29 (subcategorías);
  - N-14 (papelera);
  - N-05, si se recuerdan las visitas quitadas.

  Se juntan en **una sola subida, a 14**: la entrega NE (§ 10). Las demás solo tocan pantallas.
- **Ninguna propuesta saca lo privado de su pantalla.**
- **Ninguna renombra rutas** usadas por widgets, avisos, accesos directos o atajos con ★ (§ 8, «Riesgo»).
- Hay **9 hallazgos nuevos** en `docs/paper/hallazgos.md` (H-NAV-1 a H-NAV-9), entre ellos dos errores con datos del seguimiento privado (H-NAV-6), descritos solo en su mecánica.

---

## 2. Qué no se pudo comprobar sin teléfono

Pruébalo tú cuando puedas. Cada punto dice qué buscar.

1. **El primer alto real de Hoy en tu teléfono.** Las capturas usan un Pixel 6 (1080 × 2400 px). Las barras del sistema y la de pestañas dejan unos 2050 px útiles. ¿Qué ves sin desplazarte a las 8, a las 13 y a las 21 h?
2. **Si la barra de Deshacer aparece** al borrar un consumo desde el registro rápido (`QuickActivity.kt:606`). El código la pide, pero esa ventana no la dibuja.
3. **Si arrastrar sobre una escala del Día cambia también de día.** Las escalas se deslizan (`ui/Componentes.kt:284-290`) y el Día cambia de fecha al deslizar a los lados (`Dia.kt:153-162`).
4. **El teclado sobre los campos**: el monto de Gastos, la nota rápida y el diálogo de notas.
5. **El alcance con el pulgar** de la lupa, la tuerca y el botón de modo (36 dp, arriba a la derecha).
6. **«Atrás» después de abrir un día desde el calendario o el buscador.** `Seleccion.abrir` vacía la pila (`ui/pantallas/Calendario.kt:78`). Lo probable es que «atrás» cierre la app.
7. **Si un toque doble muy rápido en «Registrar» del gasto rápido crea dos gastos** (`QuickActivity.kt:641-657`, sin bloqueo).
8. **El contraste** de los textos al 72 % y al 30 % de opacidad en los temas que usas (anexo E, § 4).
9. **La cuadrícula de ánimo con TalkBack**: no tiene semántica (`ui/Selectores.kt:110`).
10. **La velocidad**: Análisis y el Tablero calculan al abrir; no se midió.

---

## 3. Mapa actual (Fase 1)

Detalle: **anexo A**, con las 79 rutas, cada una en su fila con 8 columnas, más el árbol completo y las entradas desde fuera.

### 3.1 Estructura

```
BARRA:  Hoy (dia) · Diario (diario) · ＋ (registro rápido, no es pestaña) · Vida (vida) · Análisis (entender)
        toque largo: Hoy→noche · Diario/＋→ánimo rápido · Vida→Resumen semanal · Análisis→buscador
ENCABEZADO de cada pestaña: 🔍 buscador · ⚙ Más · modo claro/oscuro   (36 dp, arriba a la derecha)

HOY (título «Día»)  → calendario, noche («Llenar el día»), cinta, libreta, Cuestionarios, Reacción, reloj de enfoque,
                       Tu gente, Datos, Permisos, Estado de los datos; [EditarSueno]
DIARIO               → ánimo rápido, escribir hoy, Biblioteca del diario ⇄ sueños ⇄ momentos ⇄ cartas,
                       Línea de tiempo (plegada) [DialogoMomento]
VIDA                 → Resumen semanal, cinta, Calendario, Tu año, Tu gente, Constelación, Totales, Mapa,
                       Tu año en historias, Logros, Campañas, Mapas (Mundo, Rascar, Patrimonio, Lugares, Ciudades),
                       Obras, Otras vidas, Gastos, Sustancias, Enfoque y siestas
ANÁLISIS             → Experimentos, Totales, 8 herramientas («Análisis»), Cobertura, Lugares
MÁS (⚙)              → ★ atajos · 1 Registros especiales (10) · 2 Tu gente y sustancias (5, con Privado) ·
                       3 Medir (7) · 4 Sistema (11)
```

### 3.2 Entradas desde fuera

Detalle en el anexo A, § 3.

- **Widgets**: ánimo (guarda al tocar y no ofrece Deshacer), momento, Hoy, gasto, sueño (guarda al tocar), hábitos (marca hoy sin abrir la app), Mes, anillos, foto, cifra y enfoque.
- **Avisos**:
  - ánimo, sueño, «lo de anoche», diario (con «Día difícil: solo un número»), cuestionarios, carta, días especiales, logros, Tu año, importar, metas o notas, lugar especial y respaldos;
  - suplementos, con «Ya los tomé» y «En una hora»: no se pueden deshacer y no dicen qué se anotó.
- **Accesos directos del ícono** (22 alias, uno por tema): momento, gasto, «Diario de hoy» (abre la noche), ánimo y sueño.
- **Mosaicos de ajustes rápidos**: momento, gasto, ánimo, enfoque y suplementos. El de suplementos anota sin abrir nada y sin deshacer.
- **Registro rápido** (`QuickActivity`): solo el gasto, el consumo, la recaída y el enfoque dejan elegir la fecha.

### 3.3 Señalamientos

- **Rutas huérfanas**:
  - `momentos` (`PantallaMomentos`): nadie la abre. Un aviso con `momentos` va a la pestaña Diario (`MainActivity.kt:251`).
  - `hall` (`PantallaHall`, `RecuadroHall`): código muerto.
  - Código muerto relacionado: `CentroEntender` (`Explorar.kt:432`), `Portada` (`Hoy.kt:618`) y la rama `seccion == "Sueño"` de `Dia.kt:144`.
- **Alias que nadie usa**: `diarios`, `ajustes`, `lugares_especiales` y `explorar`.
- **Ni en el buscador ni en Más**, solo por enlaces dentro de otras pantallas: `lugares`, `mundo`, `rascar`, `unesco`, `ciudad_nueva`, `biblioteca`, `analisis` y `carta_nueva`, entre otras. `lugares` es la más escondida: Vida → Mapas → Lugares.
- **No están en Más** (sí en Vida, Análisis o el buscador): `gastos`, `semana`, `calendario`, `campanas`, `experimentos`, `logros`, `tu_anio`, `anno`, `cinta`, `constelacion`, `totales` y `mapa`.
- **Hoy frente a Día**:
  - la pestaña se llama «Hoy», pero la pantalla se titula «Día» y recuerda la última fecha abierta (`Dia.kt:103-105`). Al volver de otra pestaña puede estar en ayer;
  - el buscador «Día» apila una segunda copia;
  - «abrir el día» desde otras pantallas vacía la pila.
- **«Diario»** es la pestaña, pero:
  - el acceso directo y el ⊕ «Diario de hoy» abren la **noche**;
  - el widget «✎ diario» y el aviso «Llena tu diario» abren el **Día**;
  - la noche tiene una sección «Diario».
- **Fechas que se arrastran**: `Seleccion.noche` y `Seleccion.cinta` no se limpian. «Llenar el día» desde el buscador abre la última fecha que llenaste, no hoy.
- **Sin botón atrás visible**: Gastos y Momentos (fueron pestañas), y Día, Vida y Análisis cuando se apilan desde el buscador o un ★.

---

## 4. Matriz de datos × operaciones (Fase 2)

Detalle: **anexo B**, con unos 45 tipos de dato y el camino en cada celda. Este resumen marca con 🔴 lo que no se puede.

| Dato | Crear hoy | Crear en otra fecha | Editar | Cambiar fecha u hora | Borrar | Deshacer |
|---|---|---|---|---|---|---|
| Día: ánimo, diario, hábitos, predicción | Hoy → «Llenar el día» (2 t) | Día ‹ → llenar (3 t) | igual | — | vaciar; 🔴 no se borra un día | 🔴 |
| Sueño subjetivo (calidad, sueño recordado) | Hoy → Sueño; ⊕ Sueño; widget | Día de esa fecha → Sueño; 🔴 rápidos solo hoy | igual | — | vaciar | 🔴 |
| Horas de sueño (reloj o a mano) | automático o `EditarSueno` | Día de esa fecha → Sueño → corregir | igual (5-8 t) | sí | 🔴 «esta noche no cuenta» | «volver a lo del reloj» (🔴 puede borrar lo anotado) |
| Siesta del reloj | automática | — | 🔴 | 🔴 | 🔴 | — |
| Registro de ánimo / momento | ⊕ → Ánimo (3 t); widget (1 t) | 🔴 no se puede | Diario → Línea de tiempo (e) → fila (≥ 4 t); 🔴 no desde el Día | 🔴 no | diálogo, 2 toques | 5 s |
| Tramo de enfoque o siesta | ⊕ → Enfoque o Siesta | «¿ya pasó? anótalo» (e, ≈ 7 t) | 🔴 no | 🔴 no | Más → Enfoque → quitar (1 toque, sin confirmar) | 5 s |
| Consumo (sustancia) | ⊕ → Sustancias (4 t) | «otra fecha u hora…» (d) | Sustancias → historial → fila; 🔴 ocultas después de 3 días | sí | 1 toque, sin confirmar | 🔴 no se ve en el registro rápido |
| Gasto | ⊕ → Gasto (5 t); widget | «otra fecha u hora…» | Vida → Dinero → tocar fila (el formulario de arriba cambia sin desplazar la pantalla) | sí | 2 toques | 5 s |
| Ingreso | Vida → Dinero → «+ Ingreso» (sin modo rápido) | sí | igual que gasto | sí | 2 toques | 5 s |
| **Toma de suplemento** | ⊕ «Tomé…» (2 t); aviso; mosaico | 🔴 **no se puede** | 🔴 no | 🔴 no | solo la última de hoy | 🔴 |
| Nota | Más → Notas (4 t); ⊕ Nota | 🔴 no | solo el texto | 🔴 no | 1 toque, sin confirmar | 🔴 |
| Meta | Más → Metas | fechas propias | sí | sí | 1 toque, sin confirmar | 🔴 |
| Visita a un lugar | automática | — | 🔴 | 🔴 | 🔴 | — |
| Lugar | Lugares (escondido) o toque largo en el mapa | — | 🔴 solo el tipo | — | 2 toques (deja visitas huérfanas) | 5 s |
| Persona | solo si la mencionaste con @ | — | Tu gente → diálogo | — | 🔴 no | — |
| Cambio de relación | automático | 🔴 siempre con fecha de hoy (H-6) | 🔴 | 🔴 | 🔴 | — |
| Nacimiento y fallecimiento (10b) | Tu gente → persona | se escribe la fecha | sí | sí | vaciar | 🔴 |
| Fecha propia (10b) | Tu gente → Días especiales → «+ fecha propia» | se escribe | sí | sí | 1 toque, sin confirmar | 🔴 |
| Aviso de cumpleaños (10b) | interruptor solo en Tu gente | — | — | — | — | — |
| «¿Lo hiciste hoy?» (10b) | Día → al fondo | Día de esa fecha | tocar la otra opción | — | tocar la elegida | sí |
| Experimento | Análisis → Experimentos | 🔴 siempre empieza el lunes siguiente | 🔴 sellado (a propósito) | — | 🔴 no existe | — |
| Campaña | Vida → Campañas | 🔴 solo esta semana | 🔴 | — | solo «abandonar» | 🔴 |
| Cuestionario / Reacción | Día → botones; Más → Medir | 🔴 solo hoy | 🔴 | 🔴 | 🔴 no existe | — |
| Fuerza y nutrición | 🔴 solo importando MacroFactor | lo trae el archivo | 🔴 | 🔴 | reimportar reemplaza | — |
| Escalada | Más → Escalada → Nueva sesión | «cambiar fecha» (🔴 no mueve la hora) | sí | fecha sí | sesión 2 toques; bloque 1 toque | sesión 5 s; 🔴 bloque |
| Obras / Citas / Cartas | Vida → Obras; obra → Citas; Diario → cartas | inicio y fin sí; 🔴 citas y cartas solo hoy | obra sí; 🔴 citas no; cartas a otros sí | — | 2 toques | obra y carta 5 s; 🔴 cita |
| Frases, logros propios, predicción del año | ♥; Logros (al fondo); Tu año (enero y diciembre) | — | 🔴 logros no | — | logro 1 toque, sin confirmar | 🔴 |
| Privado (solo la mecánica) | Más → Privado; ⊕ | ‹ › en Privado; la recaída con hora | Registro → ✎ | 🔴 solo la recaída, con el error de H-NAV-6 | 2 toques | 5 s (🔴 la recaída no revierte el día) |
| Lo automático (pantalla, notificaciones, música, agenda, llamadas, chats, laptop) | — | — | 🔴 | 🔴 | 🔴 (reimportar reemplaza) | — |

**Día lógico** (anexo B, § 2; anexo C, § 3):
- casi todo usa el corte de las 05:00;
- **gastos, ingresos, agenda, clima, visitas y el plan de avisos de ánimo** usan la fecha de calendario;
- el widget de gastos suma «hoy» por calendario; la app, por día lógico. Un café a la 1:30 cuenta en «hoy» en el widget y en ayer en la app.

---

## 5. Escenarios

Ana usa el tema medieval claro. Las capturas de la Fase 6 son de sus tres días: el martes normal, el miércoles con errores y el sábado, después de no abrir la app el jueves ni el viernes.

### Día 1 · martes normal

### E-01 · Ver cómo durmió
Objetivo: ver las horas de sueño y saber de dónde salen.
Ruta actual:
- Mañana (de 5 a 11 h): Hoy → sección Sueño, arriba. Al final de su tarjeta: «horologium = 7 h 0 min dormido según tu reloj» (`Dia.kt:241-242`), 0 t.
- Resto del día: Tablero → «sueño 7.0 h» (d, hacia y ≈ 2600), 0 t + desplazar.
- De qué reloj salió solo se ve dentro de «corregir las horas de sueño» (`EditarSueno.kt:61-65`).

Deshacer: no aplica.
Fricción:
- La etiqueta en latín («horologium») en el tema romano y en otros.
- La cifra del Tablero no se puede tocar para ver su origen.

Gravedad: baja. Ver propuesta: N-01.

### E-02 · Registrar el ánimo de la mañana (app, widget y aviso)
Objetivo: comparar los tres caminos.
Ruta actual:
- **App**:
  - ⊕ (v) → «Ánimo ahora» (v) → tocar la cuadrícula (v): **3 t**. Se guarda al primer toque (`QuickActivity.kt:264-268`).
  - Toque largo en ⊕ o en Diario (e): 2 t.
  - Diario → «¿Cómo estás ahora?» (v): 2 t.
- **Widget**: tocar 1-7: **1 t**. Se guarda en el acto, sin Deshacer (`QuickActivity.kt:318-319`).
- **Aviso**: tocar → cuadrícula: **2 t**.

Deshacer: en la app, sí («Deshacer» en la misma hoja; en realidad borra el registro). Desde el widget, no.
Fricción:
- Hoy no tiene botón de ánimo: está en Diario y en ⊕.
- El widget usa una escala de 1 a 7; la app, una cuadrícula de dos ejes. Son medidas distintas (capítulo 3, sección 3.3.4).
- Un toque equivocado en el widget no tiene vuelta atrás a la mano.

Gravedad: media (por el widget). Ver propuesta: N-15, N-16.

### E-03 · Marcar creatina y vitamina D
Ruta actual:
- **App**: ⊕ (v) → «Tomé creatina · 5 g» (v, al pie del menú): **2 t**; otro toque para la vitamina D.
- Más → 1. Suplementos → «Tomé…»: 3 t.
- **Aviso**: «Ya los tomé»: **1 t**. Anota la dosis habitual de los que faltan hoy y no dice cuáles.

Deshacer: solo en Más → Suplementos, «deshacer» la última de hoy. Desde ⊕, el aviso o el mosaico, no.
Fricción: en ⊕, volver a tocar «✓ creatina» **suma otra toma** (`QuickActivity.kt:234`) en lugar de ofrecer quitarla.
Gravedad: media. Ver propuesta: N-07.

### E-04 · Gasto de café al salir
Ruta actual:
- ⊕ → Gasto → monto → Comida → «Registrar»: **5 t** (v).
- Widget de gasto → categoría → monto → «Registrar»: 3 t.

Deshacer: no en el registro rápido (sí luego, en Gastos).
Fricción:
- Se guarda con la fecha de calendario.
- El gasto **no aparece en el Día** (solo en Vida → Dinero, la cinta y el calendario).

Gravedad: baja. Ver propuesta: N-26.

### E-05 · Momento con @Luis y #trabajo a mediodía
Ruta actual: ⊕ → «Ánimo ahora» → cuadrícula → emoción «alegría» → escribir «comida con @luis #trabajo» → «Listo»: **≈ 6 t**, todos visibles.
Deshacer: sí, en la misma hoja.
Fricción:
- Los tipos viejos («idea», «alegría» como tipo) ya no existen: ahora son emociones y pensamientos dentro del registro de ánimo. Bien resuelto.
- Si Luis no estaba clasificado, al día siguiente aparece «¿Quién es @luis?» en Hoy.

Gravedad: baja. Ver propuesta: —.

### E-06 · Entrenamiento de fuerza con 3 ejercicios
Ruta actual: **no se puede en la app.** Las series solo llegan importando MacroFactor: Más → 4. Datos → Importar (`data/Repo.kt:498-507`). «Gimnasio» (`fuerza`) es de solo lectura.
Deshacer: —.
Fricción: si Ana no usa MacroFactor, el gimnasio no queda registrado, salvo como sesión del reloj o con el reto «movimiento». Es una decisión de diseño: la app no quiere duplicar MacroFactor.
Gravedad: media (según tu costumbre). Ver propuesta: pregunta P-6 (§ 11).

### E-07 · Escribir el diario y marcar los hábitos en la noche
Ruta actual:
- Hoy → «Llenar el día» → «llenar»: 1 t. Desde las 18 h la tarjeta va arriba; a mediodía, hacia y ≈ 3800 (d).
- Hábitos (2 t) → valencia (1 t) → bajar → tocar el objeto del Diario (1 t; en la captura de Ana empieza hacia y ≈ 3400, segunda pantalla) → escribir → «guardar» (arriba a la derecha) → bajar → «Sellar el día» (1 t).
- Total: **≈ 7 t + 2 pantallas de desplazamiento**.
- Atajo: toque largo en Hoy (e).

Deshacer: no (se guarda solo).
Fricción:
- En la noche, el diario empieza en la segunda pantalla.
- «Cómo estás ahora» (unos 950 px) y lo opcional quedan entre el diario y «sellar».
- El botón de guardar del escritor está arriba a la derecha, con un texto que cambia según el tema («SIGNARE», «git commit»…).

Gravedad: media. Ver propuesta: N-18, N-27.

### E-08 · Revisar qué falta hoy
Ruta actual: Hoy → «Te falta hoy» (v, hacia y ≈ 1350) + «Datos que faltan» (v) + la tarjeta «Llenar el día» con ○ ánimo ○ diario ○ predicción.
Deshacer: —.
Fricción:
- Lo que falta está en tres sitios: «Te falta hoy», «Llenar el día» y el aviso del experimento, que va al fondo.
- «Te falta hoy» oculta a propósito el diario y la predicción porque ya los muestra «Llenar el día», pero esa tarjeta está fuera del primer alto a mediodía.

Gravedad: media. Ver propuesta: N-16.

### E-09 · Revisar el experimento y responder «¿Lo hiciste hoy?»
Ruta actual: Hoy → bajar casi hasta el fondo: después del Tablero, la línea de 24 h, los momentos, el sueño, los tramos y los días especiales → «sí» o «no» (`Dia.kt:326-328`, `:870-886`): **1 t + unas 2,5 pantallas**. No llega en ningún aviso.
Deshacer: sí (tocar otra vez).
Fricción: es una respuesta diaria y está casi al fondo. Si se olvida, el cumplimiento queda «sin respuesta».
Gravedad: media (afecta a la calidad del experimento). Ver propuesta: N-16.

### Día 2 · miércoles con errores

### E-10 · Corregir el sueño que el reloj registró mal (3 h en vez de 7 h)
Objetivo: corregirlo desde Hoy, donde lo está viendo.
Ruta actual:
- Hoy → la cifra «sueño 3.0 h ↓58 %» del Tablero: **tocarla no hace nada** (`Hoy.kt:450`).
- Hay que bajar a la sección Sueño:
  - en la mañana está arriba;
  - a mediodía y en la noche, después del Tablero, los botones, la línea de 24 h y los momentos: en la captura del miércoles de Ana, el título «Del sueño» está hacia y ≈ 4400.
- → «corregir las horas de sueño» (d, al final de la tarjeta, después de la calidad y «¿Recordaste un sueño?») → «Me dormí»: fecha y hora → «Desperté»: fecha y hora → «Guardar».
- Total: **de 5 a 8 t + de 0 a 2 pantallas**. Si el día ya pasó: ‹ (1 t) y lo mismo.

Deshacer: «quitar la corrección y volver a lo del reloj». Es un toque, sin confirmar, y si el reloj no tenía esa noche **borra lo anotado** (H-NAV-5).
Fricción:
- La cifra no lleva a su corrección.
- El botón cambia de lugar según la hora.
- El texto del selector («De madrugada: cuenta para el…») confunde al poner 00:30 como hora de dormir.

Gravedad: **alta** (tu ejemplo). Ver propuesta: N-01, N-04.

### E-11 · Olvidó marcar la creatina ayer
Ruta actual: **no se puede.** Suplementos, ⊕, el aviso y el mosaico anotan siempre ahora (`data/Lote3.kt:31-34`). Tampoco se puede desde el Día de ayer.
Deshacer: —.
Fricción: el nivel estimado de creatina baja como si no la hubiera tomado (H-NAV-1).
Gravedad: **alta** (tu ejemplo). Ver propuesta: N-07, N-09.

### E-12 · Un momento de ayer quedó con la hora o el tipo equivocado
Ruta actual:
- El Día de ayer muestra «15:00 Idea · Comida con @luis…» en «Momentos de este día», pero **la fila no se toca** (`Dia.kt:257-262`).
- Camino real: pestaña Diario (1 t) → desplegar «Línea de tiempo» (2 t, e; empieza plegada en cada sesión) → buscar la tarjeta de ayer → tocar la fila (3 t) → `DialogoMomento`.
- Ahí:
  - **la emoción y el pensamiento sí se cambian**: alegría en lugar de idea, en los registros de ánimo nuevos;
  - **la hora no** (solo en recaídas);
  - en un momento viejo de tipo «idea», el pensamiento «idea» no se puede quitar, porque `pensamientoDe` cae al tipo (`data/Catalogo.kt:125`).

Deshacer: no para la edición; sí para borrar.
Fricción: hay que saber que se corrige en Diario y no donde se ve, y la hora no se arregla.
Gravedad: **alta** (tu ejemplo). Ver propuesta: N-02, N-03.

### E-13 · Café anotado dos veces por un toque doble
Ruta actual: Vida (1 t) → bajar hasta «Dinero» (2 t, d, tercera pantalla de Vida) → lista del mes → «Quitar» en el duplicado (3 t) → «¿Borrar?» (4 t).
Deshacer: sí, 5 s.
Fricción:
- El gasto no se ve en el Día, que es donde Ana miraría «lo de ayer».
- Gastos no está en Más. El registro rápido no se protege del doble toque (anexo E, N5).

Gravedad: media. Ver propuesta: N-26, N-06.

### E-14 · Una nota escrita a la 1:30
Ruta actual:
- ⊕ → «Nota» (abre Notas) → tipo → texto → «Guardar nota». El formulario **no muestra la fecha**.
- La nota cae en el día anterior (`Lote2.kt:268`). La lista dice «recordatorio · mar 27 oct».
- ¿La puede mover? **No**: el diálogo solo edita el texto (`Lote2.kt:284-290`).

Deshacer: borrar no tiene Deshacer ni confirmación (`Lote2.kt:289`).
Fricción: nada avisa del corte de las 05:00, salvo el Día. Si se equivoca, no lo puede arreglar.
Gravedad: media. Ver propuesta: N-10, N-11.

### E-15 · Cambiar el ánimo de la mañana (tocó el número equivocado)
Ruta actual:
- Si la hoja rápida sigue abierta: tocar otra celda de la cuadrícula (1 t; se actualiza).
- Si ya la cerró: Diario → «Línea de tiempo» (e) → fila → `DialogoMomento` → valencia de 1 a 7 → «Guardar»: ≈ 5 t.
- La valencia **del día** (en la noche): tocar otro número (2-3 t).

Deshacer: no.
Fricción: igual que E-12. El diálogo usa dos escalas de 1 a 7 en vez de la cuadrícula (mismos valores; otra forma de verlo).
Gravedad: media. Ver propuesta: N-02.

### E-16 · Desmarcar un hábito marcado por error
Ruta actual: Hoy → «Llenar el día» → casilla (2 t). Widget de hábitos: un toque alterna.
Deshacer: tocar otra vez.
Fricción: ninguna importante.
Gravedad: baja. Ver propuesta: —.

### E-17 · Corregir el peso de una serie de ayer
Ruta actual: **no se puede en la app.** Hay que corregir en MacroFactor y volver a importar (`data/Repo.kt:498-506` reemplaza el rango).
Gravedad: media (por diseño). Ver propuesta: pregunta P-6.

### E-18 · La ubicación dice «Gimnasio» y era «Café Centro»
Ruta actual:
- Corregir la visita: **no se puede**.
- Renombrar el lugar o achicar su radio: **no se puede**.
- Lo único posible: Vida → bajar a Mapas (1 + d) → «Lugares» (2 t) → «Quitar» (3 t) → «¿Borrar?» (4 t). Sus visitas quedan huérfanas y salen como «?» (`Dia.kt:620`).
- «Ignorar este sitio para siempre» solo existe cuando la app pregunta por un sitio nuevo.
- Lugares no está en Más ni en el buscador.

Deshacer: sí, 5 s, al borrar el lugar.
Gravedad: **alta**. Ver propuesta: N-05.

### E-19 · Una siesta que el reloj marcó no fue siesta
Ruta actual: **no se puede quitar** (`siesta_reloj_min`). Solo se ve en «Lo que registró el teléfono», que se pliega solo. `EditarSueno` la lista como fuente «siesta», pero no deja descartarla.
Gravedad: media. Ver propuesta: N-04.

### E-20 · Borró sin querer un momento
Ruta actual: dentro de 5 s, «Deshacer» en la barra. Después: **no hay forma**. No hay papelera. Restaurar un respaldo devuelve todo lo borrado desde esa copia, y la copia diaria se reemplaza cada mañana (anexo C, § 2.3).
Gravedad: media. Ver propuesta: N-13, N-14.

### E-21 · Ver y editar todo lo de ayer en un solo lugar
Ruta actual: Hoy → ‹ (1 t): el Día de ayer.
- **Se edita**: ánimo, diario, hábitos y predicción (en «Llenar el día»), calidad y horas del sueño, y la respuesta del experimento.
- **Solo se ve**: momentos, consumos, tramos, la línea de 24 h y lo automático.
- **No aparece**: gastos, tomas de suplementos, notas, cuestionarios ni obras de ese día.

Gravedad: **alta**. Ver propuesta: N-26 (P11).

### Día 3 · sábado con olvidos y consultas

### E-22 · Rellenar jueves y viernes, que no abrió la app
Objetivo: ánimo, hábitos, suplementos y sueño de los dos días.
Ruta actual:
- «Te falta hoy» ofrece «Diario de ayer» (solo el viernes y solo antes de las 14 h) → Día del viernes → «Llenar el día» → valencia, hábitos (≈ 5 t) → atrás → sección Sueño → calidad (2 t).
- Luego ‹ al jueves y lo mismo.
- **Suplementos: imposible.**
- Total: **≈ 16 t** para los dos días, sin los suplementos.

Fricción: nada dice «te faltan jueves y viernes». La noche muestra todo, no solo lo esencial (salvo con «solo lo que falta»).
Gravedad: **alta**. Ver propuesta: N-09.

### E-23 · Buscar lo que escribió sobre «la mudanza»
Ruta actual:
- 🔍 (1 t) → escribir «mudanza» → resultados en el diario (`PantallaTotales(buscar)`): 2 t + texto.
- Otra vía: Diario → «Línea de tiempo» (e) → buscador → filtros.

Fricción: dos buscadores distintos. El de la lupa se presenta con el subtítulo de Totales.
Gravedad: baja. Ver propuesta: N-22.

### E-24 · Cuánto gastó en café este mes
Ruta actual: Vida → Dinero → Gastos (mes actual, totales por categoría). «Café» es una nota, no una categoría, así que no se puede filtrar. Totales tampoco separa por nota.
Gravedad: baja. Ver propuesta: pregunta P-7.

### E-25 · Agregar a una persona nueva con su fecha de nacimiento (10b) y ver su edad
Ruta actual:
- Una persona **solo existe si la mencionaste con @** en algún texto.
- Luego: Más → 2. Tu gente → «Sin clasificar» → persona → diálogo → «nacimiento» DD/MM/AAAA → guardar: ≈ 5 t. La edad aparece en Tu gente.

Fricción: no hay «+ persona» para alguien que aún no nombraste.
Gravedad: media. Ver propuesta: N-25.

### E-26 · Agregar el cumpleaños de su mamá y comprobar que avisará (10b)
Ruta actual: Más → Tu gente → «Días especiales» (d, a media pantalla) → «+ fecha propia» → diálogo → guardar: ≈ 5 t. El aviso es el interruptor «aviso en la mañana», que solo está ahí (`Gente.kt:250-252`).
Fricción:
- El aviso de cumpleaños no aparece en Más → Sistema → «Avisos y recordatorios» ni en el buscador.
- Borrar una fecha propia es un toque, sin confirmar ni deshacer.

Gravedad: media. Ver propuesta: N-22, N-13.

### E-27 · Cambiar la hora del aviso del diario
Ruta actual: ⚙ (1 t) → bajar a «4 · Sistema» (d, hacia y ≈ 5900 de Más) → «Avisos y recordatorios» (2 t) → hora del diario. O 🔍 «avisos».
Gravedad: baja. Ver propuesta: N-21.

### E-28 · Saber si el respaldo está al día y cifrado
Ruta actual: ⚙ → Sistema → «Datos» o «Estado de los datos» (2 t + bajar). «Datos que faltan», en Hoy, avisa si el respaldo tiene más de 36 h.
Gravedad: baja.

### E-29 · Exportar para R
Ruta actual: ⚙ → Sistema → «Datos» → Exportar: 3 t + bajar.
Gravedad: baja.

### E-30 · Terminar el experimento y ver el resultado
Ruta actual: Análisis → «Experimentos» (2 t) → «Terminar experimento» (3 t) → la tarjeta muestra el resultado y el cumplimiento.
Fricción: Experimentos no está en Más, y desde ahí no se llega al Día donde se responde.
Gravedad: baja. Ver propuesta: N-21.

### E-31 · Registrar el alcohol de anoche
Ruta actual: ⊕ → Sustancias → Alcohol → «¿Desde cuándo?» → «otra fecha u hora…» → fecha → hora → bebidas → «Guardar»: ≈ 8 t (d).
Fricción:
- Bien resuelto.
- Si la sustancia está oculta, pasados 3 días ya no se corrige ni se borra.
- Borrar es un toque y su Deshacer no se ve.

Gravedad: baja. Ver propuesta: N-13.

### E-32 · Ver «cómo va» en general
Ruta actual:
- Vida tiene Resumen semanal, Calendario, Tu año y Totales.
- Análisis tiene relaciones, experimentos y herramientas. Su primer alto son cifras y 7 herramientas; las relaciones empiezan, según el estimado, hacia y ≈ 4500.
- El toque largo en Vida abre la semana.

Fricción: «cómo va» está en Vida (la semana); «por qué» está en Análisis. Totales está en las dos. Análisis no tiene secciones plegables.
Gravedad: media. Ver propuesta: N-19, N-20.

### E-33 · Encontrar Medir o Cuestionarios, que usa una vez al mes
Ruta actual:
- ⚙ → bajar a «3 · Medir» (d) → fila. O 🔍 «cuestionarios» (2 t + texto).
- Funciona para lo que está en el buscador. Para Lugares, Mundo o las herramientas de Análisis, no.

Gravedad: media. Ver propuesta: N-21, N-22.

### E-34 · Día difícil: solo un número y salir
Ruta actual:
- Desde el aviso del diario: «Día difícil: solo un número» → número: **2 t**.
- Dentro de la app no hay este atajo: Hoy → «Llenar el día» → valencia (2 t), con toda la hoja alrededor.
- La «semana corta» existe, pero solo se activa al fondo de Hoy.
- La marca «día difícil» **no se puede quitar** (H-NAV-8).

Gravedad: baja. Ver propuesta: N-16.

### E-35 · Privado: anotar un medicamento de ayer y corregir uno mal puesto (solo la mecánica)
Ruta actual: ⚙ → 2. «Privado» (2 t) → ‹ (3 t) → marcar el medicamento (4 t). Corregir es tocarlo otra vez. Los registros privados se editan desde «Registro» → ✎ → `DialogoMomento`.
Fricción: dos errores de mecánica (H-NAV-6):
- cambiar solo la fecha de una recaída, con la misma hora, no se guarda;
- borrarla desde el diálogo no revierte la marca del día.

La pantalla Privado tampoco tiene su «?» de ayuda: la clave de la guía quedó como «Impulsos y recaídas».
Gravedad: media. Ver propuesta: N-23 y la corrección de H-NAV-6.

### Escenarios agregados

### E-36 · La pestaña Hoy abre en otro día
Ruta actual: Ana abrió el lunes desde el Calendario, fue a Vida y tocó «Hoy». La pestaña muestra **el lunes** (`Seleccion.diaAbierto`, `Dia.kt:103-105`). Solo lo delatan la fecha y el botón «hoy».
Gravedad: media (posible fuente de «lo anoté en otro día»). Ver propuesta: N-12.

### E-37 · «Diario de hoy» lleva a lugares distintos
Ruta actual:
- El acceso directo y ⊕ «Diario de hoy» abren la noche.
- El widget «✎ diario» y el aviso «Llena tu diario» abren el Día.
- La pestaña Diario es la biblioteca.

Gravedad: media. Ver propuesta: N-23.

### E-38 · Borrar una nota sin querer
Ruta actual: Notas → tocar la nota (para editarla) → el botón «borrar» está al lado de «guardar» → se borra **sin confirmar ni deshacer**.
Gravedad: media. Ver propuesta: N-13.

### E-39 · Un toque equivocado en el widget de ánimo
Ruta actual: el valor se guarda al tocar y la hoja que se abre no ofrece «Deshacer» (`QuickActivity.kt:319`, condición `inicial == null`). Para corregirlo: Diario → Línea de tiempo → fila → editar o borrar (≈ 5 t).
Gravedad: media. Ver propuesta: N-15.

---

## 6. Inventario de saturación (Fase 4)

Detalle: **anexo D**, con cada tarjeta, su condición en el código, su frecuencia y la propuesta.

### 6.1 Mediciones

Píxeles en un Pixel 6, 1080 px de ancho. «Pantallas» = alto del contenido ÷ 2200 px útiles (con las barras reales quedan unos 2050).

**Capturas de escenario** (los datos de Ana, run 87 de `capturas.yml`, commit `26d1079`; las 22 sin fallas):

| Pantalla | Captura | Alto del contenido (px) | Pantallas |
|---|---|---|---|
| Hoy (sábado, sin ánimo aún) | `anaHoyMedieval` / `Romano` / `Ciber` | 6553 / 6081 / 6049 | 3,0 / 2,8 / 2,7 |
| Día del miércoles (con errores) | `anaMiercolesMedieval` / `Romano` | 5765 / 5277 | 2,6 / 2,4 |
| Día del jueves (vacío) | `anaJuevesMedieval` | 4674 | 2,1 |
| Noche de hoy (sin nada lleno) | `anaNocheMedieval` / `Romano` | ≥ 9000 (llega al borde) | ≥ 4,1 |
| Diario | `anaDiarioMedieval` / `Ciber` | 2773 / 2769 | 1,3 |
| Vida | `anaVidaMedieval` / `Romano` | 5474 / 5338 | 2,5 / 2,4 |
| Análisis (31 días de datos) | `anaAnalisisMedieval` / `Ciber` | ≥ 8000 (cortada en «Personas y temas») | ≥ 3,6 |
| Más | `anaMasMedieval` / `Romano` | ≥ 8000 (llega al borde) | ≥ 3,6 |
| Gastos (un mes) | `anaGastosRomano` | ≥ 6600 (llega al borde) | ≥ 3,0 |
| Experimentos | `anaExperimentosRomano` | 5734 | 2,6 |
| Suplementos | `anaSuplementosMedieval` | 4263 | 1,9 |
| Tu gente / Notas / Momentos | `anaPersonasCiber` / `anaNotasMedieval` / `anaMomentosMedieval` | 2451 / 1943 / 2267 | 1,1 / 0,9 / 1,0 |

Los lienzos más altos (de 11 000 a 16 000 px) agotaron la memoria de Java en el CI (run 85). Por eso la noche, Análisis, Más y Gastos se dan como mínimo.

**Capturas anteriores, con datos más cargados** (anexo D, § 0.3):
- Hoy: 7602 px (`diaV73Romano`, unas 3,5 pantallas).
- Noche: de 8000 a 8800 px, estimado.
- Más: de 7900 a 8400 px, estimado.
- Análisis: de 10 000 a 14 000 px con un año de datos, estimado por código.
- Semana corta: 871 px.

**Conteos** (anexo D):

| Pantalla | Tarjetas o secciones | Elementos tocables |
|---|---|---|
| Hoy | 16 visibles a mediodía (24 tipos posibles) | ≈ 36 |
| Noche | 13 secciones + barra fija | ≈ 63 |
| Más | 4 tarjetas con 32 filas | ≈ 67 |
| Análisis | 15 bloques | ≈ 50, más hasta 36 filas de listas |
| Vida | 17 tarjetas | 20 |
| Diario (plegado) | 6 secciones | ≈ 20 |

### 6.2 Lo que hoy está en el primer alto de Hoy (a mediodía)

1. Encabezado, con la fecha grande que abre el calendario.
2. Estado ‹ › y racha.
3. **Frase del día** (de 250 a 420 px, según el tema).
4. «¿Cómo terminó lo de anoche?», si hay un consumo sin completar.
5. **Personaje** del tema (unos 230 px).
6. **Te falta hoy**.
7. «¿Quién es @x?».

**Quedan fuera del primer alto**: el Tablero (hacia y ≈ 2350), «Llenar el día» (hacia y ≈ 3800), la línea de 24 h, los momentos, el sueño (fuera de la mañana) y el experimento. Además, Hoy **cambia de orden según la hora** (`Dia.kt:186-190`): en la mañana va primero el sueño, desde las 18 h «Llenar el día» y a mediodía los pendientes.

### 6.3 Clasificación propuesta (resumen)

| Pantalla | Se queda a la vista | Se pliega | Se mueve | Se fusiona | Se propone quitar |
|---|---|---|---|---|---|
| **Hoy** | fecha y estado; «Te falta hoy»; «Llenar el día» compacto a toda hora; Tablero compacto (3-4 cifras + «ver todo») | la frase (a una línea); la línea de 24 h; «Hace un año»; lo del teléfono (ya lo está) | personaje (más abajo o junto al estado); la fila cuestionarios/reacción/enfoque (salen como invitación cuando tocan; siguen en Más y en el buscador); «pasar a semana corta» (a Apariencia, **después** de agregarlo al buscador) | «Lo de anoche», el experimento, «¿Quién es?» y un contador de «Datos que faltan» → dentro de «Te falta hoy»; 24 h + momentos + enfoque y siestas → una sección «Tu día» | `Portada` (código muerto) |
| **Día de otra fecha** | igual que Hoy, con «Llenar el día» arriba | la frase | — | — | la fila de 3 botones (abren las de hoy y no sirven en otra fecha) |
| **Noche** | hábitos, ánimo y cuerpo, diario, predicción, sellar | obras y foto, si no hay nada en curso | «Cómo estás ahora» antes de lo opcional | — | — |
| **Diario** | todo | exportar (ya lo está) | exportar → biblioteca del diario o Datos | — | — |
| **Vida** | rejilla de 8; Campañas; Dinero y sustancias (sube) | Tu año en historias, Mapas, Obras, Otras vidas → «Galerías ▸» | — | Totales (repetido en Análisis) | — |
| **Análisis** | las 3-5 relaciones más marcadas (suben), Experimentos | herramientas (como rejilla), cruzar variables, ¿qué tan bien te conoces?, emociones por hora, con quién, listas, lugares, antes y después | Cobertura (ya está en Más) | cifras + ayuda de potencia en una línea | — |
| **Más** | las 4 secciones | cada sección, con su número y cuántas filas tiene | — | «Guía» y «Guía científica» (P6) | — |

Nada de lo que se esconde se pierde: cada cosa queda en el buscador, en «ver más» en el mismo sitio o en Más (anexo D, § 9.2, tabla «Lo que se esconde y por dónde se encuentra»). **Antes de moverlos** hay que agregar al buscador dos que hoy faltan: «semana corta» y «Hace un año».

---

## 7. Violaciones de heurísticas (Fase 5)

Detalle: **anexo E**, con las tablas completas. Fuente de las generales: Nielsen, J. (1994, actualizado el 30 ene 2024). *10 Usability Heuristics for User Interface Design*. Nielsen Norman Group. https://www.nngroup.com/articles/ten-usability-heuristics/ (enlace comprobado en la sesión).

| Heurística | Violación principal | Dónde |
|---|---|---|
| N1 · Visibilidad del estado | El registro rápido no dice que a la 1:30 la fecha es la de ayer. Gastos y Notas no confirman lo guardado. Al tocar un gasto, el formulario de arriba cambia sin que se vea | `QuickActivity.kt:193-197`; `Gastos.kt:150`, `:217-222`; `Lote2.kt:267-269` |
| N2 · Lenguaje del mundo real | Títulos en latín, griego, ruso o árabe según el tema; «← cd ..», «SIGNARE», «git commit»; «estado = », «= null» | `Estilo.kt:65-73`, `:203-207`; `Libros.kt:254-260`; `Componentes.kt:311` |
| N3 · Control y libertad | 20 de 34 borrados sin Deshacer; «Deshacer» del registro rápido = borrar; tocar fuera del gasto rápido descarta lo escrito | anexo C, § 2.2; `QuickActivity.kt:118-121`, `:319` |
| N4 · Consistencia | Editar de 5 maneras (formulario arriba, diálogo, sin edición, enlace, toque largo); borrar de 4; tres tipos de botón ‹ › | anexo E, § 7 |
| N5 · Prevención de errores | Ningún botón de guardar se bloquea al guardar (gasto y sustancia rápidos podrían duplicar); «borrar» junto a «guardar» en Notas; el modo claro/oscuro junto a la lupa | `QuickActivity.kt:641-657`, `:558-563`; `Lote2.kt:288-289`; `Decoracion.kt:1243` |
| N6 · Reconocer antes que recordar | Los toques largos solo se explican al fondo de Más; las filas de momentos del Día no parecen tocables (y no lo son) | `Mas.kt:144`; `Dia.kt:257-262` |
| N7 · Flexibilidad | Bien: atajos, ★, widgets y acciones en avisos | — |
| N8 · Minimalismo | Hoy, la noche, Más y Análisis miden de 3 a 4 pantallas o más | § 6 |
| N9 · Recuperarse de errores | No hay papelera; «volver a lo del reloj» puede borrar la noche | anexo C, § 2.3; H-NAV-5 |
| N10 · Ayuda | Día y Privado no tienen «?»; la guía no explica gestos, corregir, deshacer ni el corte de las 05:00; dos guías con nombres casi iguales | `Hoja.kt:55-57`; `data/GuiaUso.kt:85-91` |
| P7 · Gestos | La foto del día solo con toque largo; marcar un lugar en el mapa solo con toque largo; zoom y doble toque del mapa para rascar; pausa de Tu año | `Dia.kt:765-767`; `Mapa.kt:492`; `Mundo.kt:715-735`; `TuAnio.kt:646-651` |
| P10 · Una mano | Guardar el diario, arriba a la derecha; lupa, tuerca y modo de 36 dp arriba a la derecha (la tuerca es la única puerta a Más) | `Libros.kt:1191-1194`; `Decoracion.kt:1243-1364` |
| Accesibilidad | La cuadrícula de ánimo no se usa con TalkBack; ≈ 31 botones que son solo un glifo; 9 lienzos tocables sin semántica; Tu año y el Calendario dan la valencia solo con color | `Selectores.kt:110-115`; anexo E, § 4 |

---

## 8. Propuestas

Cada propuesta dice qué cambia en palabras simples. El detalle técnico es una guía para quien la implemente: la decisión es tuya.

**Reglas que valen para todas**:
- **Ninguna saca lo privado de su pantalla.** Las filas tocables del Día siguen filtrando `!it.privado` (`Dia.kt:251`), y el hábito ligado sigue fuera.
- **Ninguna renombra una ruta.** Si cambia un nombre, cambia solo el texto que ves; la cadena de la ruta queda igual. Así no se rompen los widgets, los avisos ya mostrados, los accesos directos ni los ★ guardados en `favoritos_mas`, que viajan en los respaldos (anexo A, § 3.8).
- Las que cambian lo que se guarda lo dicen, porque obligan a subir `SCHEMA_V` y a actualizar el diccionario, R y el paper.

### N-01 · Tocar una cifra del Tablero abre su corrección o su origen
- **Problema:** E-10 y E-01; P1.
- **Cambio:** cada tarjeta del Tablero se puede tocar:
  - sueño → `EditarSueno` de esa fecha;
  - ánimo → «Llenar el día» en «Ánimo y cuerpo»;
  - gasto → los gastos de ese día (N-26);
  - pasos, pantalla y kcal → «Lo que registró el teléfono», desplegado.

  La tarjeta muestra un «›» discreto.
- **Dónde:** `ui/pantallas/Hoy.kt`, `TarjetaMetrica` (`:438-482`) recibe un `onClick`. `Tablero(fecha)` recibe la acción por métrica desde `PantallaDia`.
- **Riesgo:** ninguno para lo privado ni para las rutas. El Tablero ya no excluye nada privado (el ánimo es público).
- **Esfuerzo:** chico.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** captura de Hoy con el «›»; y en el teléfono, tocar «sueño» abre el diálogo de esa fecha.

### N-02 · Las filas de «Momentos de este día», la línea de 24 h y la cinta se tocan para corregir
- **Problema:** E-12, E-15 y E-39; P1 y P11.
- **Cambio:**
  - en el Día, tocar una fila de «Momentos de este día» abre `DialogoMomento` (los momentos) o `CompletarConsumo` (las sustancias);
  - en la cinta, el detalle de un momento trae «corregir»;
  - las filas de «Enfoque y siestas» abren un diálogo para corregir el tramo (hoy no hay ninguno).
- **Dónde:**
  - `ui/pantallas/Dia.kt:250-268` (filas con `clickable`) y `:833-847` (tramos);
  - `ui/pantallas/Cinta.kt:339-343`;
  - un `DialogoTramo` nuevo que reutilice `SelectorHora` y `SelectorDuracion`, más `Repo.guardarTramo` (ya existe).
- **Riesgo:** lo privado no se ve en esas listas, así que no aparece. `CompletarConsumo` vive en `QuickActivity`: abrirla desde la app con `consumo_id` ya funciona (`SustanciasPantalla.kt:39-45`).
- **Esfuerzo:** medio.
- **Paper:** capítulo 3, sección 3.15.3. Dice que se edita «desde Momentos», una pantalla que hoy no se abre; pasaría a decir «desde el Día, el Diario o Privado».
- **Cambia lo que se guarda:** no (salvo con N-03).
- **Cómo se comprueba:** captura del Día con las filas marcadas como tocables; en el teléfono, editar un momento desde el Día.

### N-03 · `DialogoMomento` deja corregir la hora y la fecha, y lo deja marcado
- **Problema:** E-12; H-NAV-2, H-2-9 y H-NAV-6.
- **Cambio:**
  - el diálogo muestra la fecha y la hora y deja cambiarlas con `SelectorHora`, igual que ya hace con las recaídas. La fecha se recalcula con el día lógico de la hora nueva;
  - en los momentos viejos («idea», «alegría» como tipo), al cambiar la emoción o el pensamiento el registro pasa a ser de tipo «animo», y deja de contar el tipo viejo.
  - Se corrige el error de la recaída: se compara la marca de tiempo completa, no solo «HH:mm».
- **Dónde:**
  - `ui/pantallas/Momentos.kt:107-171`;
  - `data/Recaidas.kt` (`moverHora` sirve de modelo para mover cualquier momento);
  - `data/Catalogo.kt:124-125`.
- **Riesgo:** mover un registro de ánimo cambia su bloque horario y su distancia al aviso (capítulo 3, sección 3.5). Por eso conviene **marcar la edición** (P12).
- **Esfuerzo:** medio.
- **Paper:** capítulo 3, secciones 3.5 y 3.15.3, y H-2-9 (se resolvería).
- **Cambia lo que se guarda:** **sí**, si se agrega `ts_editado` a `Momento` (propuesta de H-2-9). Sube `SCHEMA_V` y suma una columna a `vm_momentos`. Sin `ts_editado`, no cambia, pero la edición queda invisible.
- **Cómo se comprueba:** una prueba unitaria de «mover un momento a otro día» (fecha lógica correcta); captura del diálogo.

### N-04 · Corregir el sueño completo: descartar siestas y proteger la corrección
- **Problema:** E-10 y E-19; H-NAV-3 y H-NAV-5; P8.
- **Cambio:**
  - En `EditarSueno`, cada fuente que llegó del reloj lleva «no cuenta». Sirve para la siesta del sofá o para una noche que no fue tuya.
  - «Volver a lo del reloj» pide confirmación y ofrece Deshacer.
  - Si el reloj no tiene esa noche, lo dice antes («el reloj no tiene esta noche: se quedará vacía»).
  - Restaurar un respaldo **no pisa** una noche corregida a mano (`sueno_manual`).
- **Dónde:**
  - `ui/EditarSueno.kt`;
  - `sensores/Salud.kt:186-231` (leer las fuentes descartadas y no congelar la siesta);
  - `data/Importar.kt:72` (o mejor `data/Fusion`);
  - `ui/Deshacer.kt`.
- **Riesgo:** datos. Hay que guardar qué fuente se descartó para que la próxima sincronización la respete.
- **Esfuerzo:** medio.
- **Paper:** capítulo 4 (sueño y siestas) y el Apéndice A (`siesta_reloj_min`).
- **Cambia lo que se guarda:** **sí**: un campo nuevo en `SaludDia`, por ejemplo `sueno_descartadas: List<String>`. Sube `SCHEMA_V`.
- **Cómo se comprueba:** una prueba de `Salud` con una fuente descartada; captura del diálogo con «no cuenta».

### N-05 · Lugares que se corrigen: renombrar, radio, visitas
- **Problema:** E-18; H-NAV-4; P8.
- **Cambio:**
  - En **Lugares**: «renombrar», «cambiar radio» (las opciones del alta) e «ignorar este sitio para siempre», también para un lugar ya guardado.
  - En el Día (en «Lo que registró el teléfono» o en la pista «lugares» de N-17): tocar una visita → «fue en…» (elegir otro lugar), o «no estuve aquí».
  - Al borrar un lugar, se pregunta: «¿qué hago con sus N visitas? pasarlas a… / quitarlas».
  - **Lugares** entra en el buscador (palabras «lugar, ubicación, corregir lugar») y en Más.
- **Dónde:**
  - `ui/pantallas/Medir.kt:230-300`;
  - `data/Repo.kt` (`guardarVisita` y `borrarVisita`, y `borrarLugar` con sus visitas);
  - `data/Lugares.kt`;
  - `ui/pantallas/Dia.kt:615-622`;
  - `ui/pantallas/Pantallas.kt`.
- **Riesgo:**
  - Datos: cambiar el radio no rehace solo las visitas pasadas. Hay que decidir si se llama a `Lugares.reconstruir` (últimos 30 días).
  - Rutas: `lugares` se queda igual.
- **Esfuerzo:** grande.
- **Paper:** capítulo 4 (ubicación) y H-1-2.
- **Cambia lo que se guarda:** no (`Visita.lugar` ya existe; borrar es quitar documentos). Si «no estuve aquí» debe recordarse para que `reconstruir` no la vuelva a crear, **sí** (una lista en `Config`; sube `SCHEMA_V`).
- **Cómo se comprueba:** una prueba de «reasignar una visita» y de «borrar un lugar con visitas»; captura de Lugares.

### N-06 · Corregir un gasto se ve, y guardar se confirma
- **Problema:** E-13; N1 y N5.
- **Cambio:**
  - Tocar un gasto o un ingreso abre un diálogo de corrección (con «Quitar» dentro), en lugar de cambiar el formulario de arriba sin desplazar.
  - «Registrar» muestra «Gasto registrado» y bloquea el botón mientras guarda, también en el gasto rápido.
  - El gasto rápido pregunta antes de descartar un monto escrito al tocar fuera.
- **Dónde:** `ui/pantallas/Gastos.kt:94-153` y `:175-230`; `ui/QuickActivity.kt:118-121` y `:620-662`.
- **Riesgo:** ninguno.
- **Esfuerzo:** chico.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** capturas de Gastos y del diálogo.

### N-07 · Suplementos: anotar en otra fecha y corregir tomas pasadas
- **Problema:** E-11, E-03 y E-22; H-NAV-1; P2 y P3. **Tu segundo ejemplo.**
- **Cambio:**
  - En **Suplementos**, debajo de «Tomé…», una ficha «¿cuándo? ahora · ayer · otra fecha u hora…» (el `SelectorHora` de siempre).
  - «Últimas tomas» deja tocar una toma para cambiar la cantidad o la hora, o quitarla (con Deshacer).
  - En **⊕**, tocar «✓ creatina» cuando ya está tomada pregunta «¿otra toma o quitar la de hoy?», en lugar de sumar otra.
  - El aviso «Ya los tomé» dice qué anotó y ofrece «deshacer» en la misma notificación durante unos minutos.
- **Dónde:**
  - `ui/pantallas/Lote3.kt:80-150`;
  - `data/Lote3.kt:31-34` (`registrar` ya acepta `ts`);
  - `ui/QuickActivity.kt:226-241`;
  - `avisos/Recordatorios.kt:111-116`.
- **Riesgo:** datos. El nivel estimado se recalcula con las tomas nuevas (es lo correcto). Hay que marcar la toma anotada tarde (P12).
- **Esfuerzo:** medio.
- **Paper:** capítulo 5 (suplementos) y H-NAV-1 (se resolvería). Si se marca la toma tardía, también el diccionario (`vm_tomas`, si existe la tabla) y el Apéndice A.
- **Cambia lo que se guarda:** **sí**, si se marca con `ts_creado` en `TomaSuplemento`. Si no, no.
- **Cómo se comprueba:** una prueba de «tomar ayer» (la fecha lógica es la de ayer) y captura de Suplementos.

### N-08 · Los registros rápidos dicen el día y dejan cambiarlo
- **Problema:** E-14, E-02 y E-22; P2 y P9; H-NAV-7.
- **Cambio:**
  - En el encabezado de la hoja rápida (ánimo, sueño, momento), una línea en español llano: «martes 27 · ahora». Al tocarla: «ayer · otra hora…».
  - De madrugada se lee: «Son las 01:30: esto va al **lunes 26** (los días empiezan a las 5:00) · cambiar».
  - El sueño rápido y el widget de sueño guardan la calidad en el día del despertar.
- **Dónde:**
  - `ui/QuickActivity.kt:163-168` (`crear` recibe `ts`), `:193-197` (`Encabezado`), `:253-325` y `:665-695`;
  - `Estilo.fecha` se deja para los títulos, no para este aviso.
- **Riesgo:** metodológico. Un registro de ánimo con hora pasada deja de ser «momentáneo» (capítulo 3, sección 3.4). Debe quedar marcado (P12), por ejemplo `origen = "tarde"` o el `ts_creado` que pide H-2-9, y el panel debería poder excluirlos.
- **Esfuerzo:** medio.
- **Paper:** capítulo 3, secciones 3.4, 3.5 y 3.12; H-NAV-7 y H-NAV-9.
- **Cambia lo que se guarda:** **sí**, si se agrega la marca (sube `SCHEMA_V`, diccionario y R). Si solo se usa `origen` con un valor nuevo, no cambia el esquema, pero sí el diccionario.
- **Cómo se comprueba:** captura de la hoja rápida a las 01:30 (fijando la hora en la prueba).

### N-09 · «Ponerte al día»: rellenar los días que faltan
- **Problema:** E-22, E-11 y E-21; P3.
- **Cambio:**
  - Si hay días sin valencia entre el último registrado y hoy (hasta 7), «Te falta hoy» muestra «Te faltan jueves y viernes →».
  - Abre la hoja de la noche de esos días en modo «solo lo esencial»: ánimo, hábitos, calidad del sueño y una fila nueva **«Suplementos de este día: ☐ creatina ☐ vitamina D»**.
  - Arriba: «‹ jueves · viernes ›» para pasar de un día al otro.
  - Al terminar, vuelve a Hoy.
  - «Diario de ayer» deja de depender de las 14 h: se queda hasta que se llene o se descarte.
- **Dónde:**
  - `ui/pantallas/Hoy.kt:92-120` (`Pendientes.deHoy`);
  - `ui/pantallas/Dia.kt:371-545` (la noche, con un modo «esencial» que ya existe a medias: `noche_solo_falta`);
  - la fila de suplementos usa N-07.
- **Riesgo:** metodológico. Más días rellenados después significa más datos retrospectivos. Ya quedan marcados como `retroactivo` (`Dia.ts_valencia`, 10b). Lo privado no se toca: el hábito ligado sigue fuera.
- **Esfuerzo:** medio.
- **Paper:** capítulo 3, secciones 3.7.4 («retroactivo») y 3.15 (datos que faltan); H-NAV-9.
- **Cambia lo que se guarda:** no (solo con la marca de N-07).
- **Cómo se comprueba:** captura de Hoy con días faltantes y de la hoja en modo esencial (los datos de Ana tienen jueves y viernes vacíos).

### N-10 · Aviso del día lógico donde se anota
- **Problema:** E-14; P9.
- **Cambio:** el mismo aviso de `Dia.kt:291`, pero en español llano y con el nombre del día («va al lunes 26»), en el registro rápido (N-08), en Notas, en la noche (si se abre entre las 00:00 y las 05:00) y en el aviso «Ya los tomé». También en la semana corta, que hoy lo pierde (`Dia.kt:212`).
- **Dónde:** un componente `AvisoMadrugada(fecha)` en `ui/Componentes.kt`, usado en esos lugares.
- **Riesgo:** ninguno.
- **Esfuerzo:** chico.
- **Paper:** capítulo 2 (día lógico), una frase.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** captura a las 01:30 (hora fijada en la prueba).

### N-11 · Notas: se ve la fecha, se cambia y se corrige el tipo
- **Problema:** E-14 y E-38.
- **Cambio:**
  - El formulario muestra «para el martes 27 · cambiar».
  - El diálogo de editar deja cambiar el texto, el tipo, la fecha y el recordatorio.
  - «Borrar» pasa a ser «borrar → ¿seguro?», con Deshacer.
- **Dónde:** `ui/pantallas/Lote2.kt:247-310`.
- **Riesgo:** ninguno.
- **Esfuerzo:** chico.
- **Paper:** no.
- **Cambia lo que se guarda:** no (`Nota.fecha` y `tipo` ya existen).
- **Cómo se comprueba:** captura de Notas y del diálogo.

### N-12 · Hoy abre en hoy
- **Problema:** E-36 y E-37; P2.
- **Cambio:**
  - Tocar la pestaña «Hoy» desde otra pestaña lleva siempre a hoy (`Seleccion.diaAbierto` se olvida al cambiar de pestaña).
  - Al volver a la app después de un rato, Hoy vuelve a hoy.
  - `Seleccion.noche` y `Seleccion.cinta` se limpian al salir, así el buscador abre la noche y la cinta de hoy.
  - «Abrir el día» desde otras pantallas **apila** el Día, en vez de vaciar la pila, para que «atrás» regrese.
- **Dónde:**
  - `ui/MainActivity.kt:280-287` (`tocar`), `:142-153` (`onStart`);
  - `ui/pantallas/Calendario.kt:78` (`Seleccion.abrir`);
  - `ui/pantallas/Dia.kt:103-105` y `:373`.
- **Riesgo:** costumbre. Si te gusta que Hoy recuerde el día abierto, se puede dejar un «volver al lunes» en Hoy.
- **Esfuerzo:** chico.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** a mano en el teléfono; no hay captura que lo muestre.

### N-13 · Deshacer en todos los borrados, y confirmar los de un toque
- **Problema:** E-20, E-26, E-31 y E-38; P4 y N3.
- **Cambio:**
  - Los 20 borrados sin Deshacer de la tabla del anexo C, § 2.2, pasan por `Deshacer.ofrecer`: notas, metas, tomas, citas, bloques, fechas propias, logros propios, sitios ignorados, ciudades, frases, equivalencias, categorías, la corrección del sueño…
  - Los de un toque (notas, metas, fechas propias, logros, tramos, consumos) piden confirmación, como Gastos.
  - `CapaDeshacer` se dibuja también en `QuickActivity`.
  - El botón «Deshacer» de la hoja rápida pasa a llamarse **«Borrar este registro»**.
- **Dónde:** `ui/Deshacer.kt`, `ui/QuickActivity.kt`, y cada sitio de la tabla.
- **Riesgo:** ninguno para los datos. Deshacer vuelve a guardar la copia.
- **Esfuerzo:** medio (muchos sitios, cada uno chico).
- **Paper:** capítulo 10 (integridad), una línea.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** una prueba por tipo de borrado (borrar → deshacer → el documento vuelve igual).

### N-14 · Papelera de 7 días (aprobada en tu respuesta a P-9)
- **Problema:** E-20; N9.
- **Cambio:** lo borrado va a una colección `papelera` durante 7 días. Desde Más → Datos → «Papelera» se puede recuperar.
- **Dónde:** `data/Repo.kt` (cada `borrar*`), `data/Db.kt`, una pantalla nueva y la limpieza en `avisos/Trabajo.kt`.
- **Riesgo:**
  - Privacidad: lo privado borrado iría a la papelera. La pantalla de la papelera **no** debe mostrarlo fuera de Privado, así que la papelera de lo privado vive en Privado.
  - Respaldos: la papelera no debería viajar en ellos.
- **Esfuerzo:** grande.
- **Paper:** capítulo 10.
- **Cambia lo que se guarda:** **sí** (colección nueva; sube `SCHEMA_V`).
- **Cómo se comprueba:** pruebas de borrar y recuperar, y de que lo privado no salga de Privado (`PrivadoPrueba`).

### N-15 · Un toque equivocado en un widget o un aviso se puede deshacer
- **Problema:** E-02 y E-39; N3; H-NAV-8.
- **Cambio:**
  - La hoja que se abre después del widget de ánimo o de sueño muestra «guardado: 5/7 · borrar».
  - La acción «Día difícil» se puede quitar desde la noche.
  - El mosaico de suplementos muestra un aviso breve con «deshacer».
- **Dónde:** `ui/QuickActivity.kt:101-113`, `:318-319` y `:698-707`; `ui/pantallas/Dia.kt` (la noche); `widget/Mosaicos.kt:51-75`.
- **Riesgo:** ninguno.
- **Esfuerzo:** chico.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** captura de la hoja con la valencia inicial.

### N-16 · Hoy: lo diario en el primer alto
- **Problema:** E-07, E-08, E-09, E-02 y E-34; P5 y N8.
- **Cambio:** a cualquier hora, el primer alto de Hoy queda así (boceto en § 12):
  1. Fecha y estado, en una franja.
  2. **«Te falta hoy»**, que absorbe:
     - «lo de anoche»;
     - «¿Lo hiciste hoy?» del experimento, con sí y no ahí mismo;
     - «¿Quién es @x?» como invitación;
     - un contador «2 datos que faltan →» que despliega `DatosQueFaltan`.
  3. **«Llenar el día» compacto** (○ ánimo ○ diario ○ predicción · hábitos 1/2 · botón), siempre.
  4. **Tablero compacto**: las primeras 4 cifras de tu tablero, con «ver todo».
  5. En la mañana, si falta, la calidad del sueño (la ficha ya lleva a la sección).

  **Arriba de todo**, después de la fecha y el estado, se quedan **la frase del día**, como ahora (tu respuesta a P-10), y **«Tus últimos días»** (N-28), en lugar del personaje.

  Debajo, plegado: «Tu día» (N-17), sueño, «Hace un año», lo del teléfono.

  **Se mueven o salen**:
  - la fila «cuestionarios · reacción · enfoque»: salen como invitación cuando tocan, y siguen en Más, el buscador y ⊕;
  - **el personaje sale de Hoy** (tu respuesta a P-10); su lugar lo ocupa N-28;
  - «pasar a la semana corta»: a Apariencia y al buscador.

  En el Día de otra fecha no salen los tres botones.
- **Dónde:** `ui/pantallas/Dia.kt:184-338`; `ui/pantallas/Hoy.kt` (`TeFaltaHoy`, `Tablero`, `FraseDelDia`); `ui/Personaje.kt`.
- **Riesgo:**
  - Costumbre: es el cambio más visible.
  - Metodológico: la respuesta del experimento sube y se contestará más (bien), y el orden de la mañana deja de cambiar.
  - Lo privado no cambia.
- **Esfuerzo:** medio.
- **Paper:** capítulo 3 (el experimento se contesta desde Hoy; capítulo 8, «¿Lo hiciste hoy?») y H-NAV-9. Hay que anotar en el Apéndice D la versión del cambio.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** capturas de Hoy a las 8, 13 y 21 h con la hora fijada; el primer alto (2200 px) debe tener las cinco piezas.

### N-17 · «Tu día»: una sola sección para lo que pasó
- **Problema:** E-21; N8; P11.
- **Cambio:**
  - «Tu día en 24 horas», «Momentos de este día» y «Enfoque y siestas» se vuelven una sección, «Tu día»: arriba la línea de 24 h y debajo la lista por hora (momentos, consumos, tramos, gastos y tomas), cada fila tocable (N-02, N-26).
  - Plegada, muestra una línea («5 cosas con hora · 2 lugares»).
- **Dónde:** `ui/pantallas/Dia.kt:250-268`, `:312-319`, `:833-847`; `ui/pantallas/Hoy.kt:503-610`.
- **Riesgo:** ninguno. Lo privado sigue fuera.
- **Esfuerzo:** medio.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** captura del miércoles de Ana.

### N-18 · La noche, ordenada con el mayor rigor posible
- **Problema:** E-07; P5. Tu respuesta a P-8: «de la forma que se consiga el mayor rigor científico».
- **El problema de fondo:** cada pregunta de la noche puede cambiar las respuestas que vienen después. Hoy el orden es: hábitos → ánimo y cuerpo → contexto → diario → obras → foto → predicción → lo opcional → «Cómo estás ahora» → sellar. Tiene tres problemas:
  1. **«Cómo estás ahora» va al final**, después de recordar todo el día y de escribir el diario. Recordar a propósito lo que te pasó cambia el ánimo del momento; de hecho, es una forma habitual de inducir un ánimo en el laboratorio (Westermann et al., 1996). Además, ponerle nombre a lo que sientes al escribir baja la intensidad de la emoción (Lieberman et al., 2007). Así, el «cierre» no mide cómo estabas al cerrar el día, sino cómo quedaste después de repasarlo y escribirlo. Es justo lo que este dato quiere evitar (capítulo 3, sección 3.7.5: «un dato momentáneo comparable entre días»).
  2. **Los hábitos van antes de la valencia del día.** Cuando se pregunta algo específico antes de un juicio general («¿cumpliste tus hábitos?» antes de «¿qué tan agradable fue el día?»), lo específico se mete en el juicio general o se excluye de él. Con eso cambia la relación entre las dos respuestas (Schwarz, Strack y Mai, 1991). Para el análisis importa: el panel cruza la valencia con los hábitos.
  3. **Mientras eliges la valencia ves los valores de tus avisos de ese día** («avisos = 09:40→5»). Es un ancla (H-2-11).
- **Cambio (orden nuevo, siempre el mismo):**
  1. **«Cómo estás ahora»** (la cuadrícula), lo primero, antes de repasar nada. Solo cuando la hoja es la del día lógico de hoy. En un día pasado no aparece, como propone H-2-8: si no, se guardaría «ahora» con la fecha de otro día.
  2. **Ánimo y cuerpo del día**: valencia, activación y dolor. Es el juicio general, antes de cualquier pregunta específica. La línea de los avisos solo se muestra **después** de elegir la valencia (H-2-11), y la activación usa una paleta que no sugiere «bueno o malo» (H-2-11).
  3. **Predicción de mañana**, junto al juicio del día y antes de escribir.
  4. **Contexto** (casillas de hechos).
  5. **Hábitos** (casillas de hechos).
  6. **Diario** (texto libre, lo que más mueve el ánimo) y presencias.
  7. Plegado, en este orden fijo: lectura del día, lo mejor y lo peor, hitos, obras de hoy, foto del día (con un botón visible «elegir», P7), «privado de este día →».
  8. Sellar.
- **Por qué este orden y no otro:** no hay un orden libre de efectos. Si el ánimo del momento va primero, puede teñir el juicio del día; es el efecto de usar el ánimo como información (Schwarz y Clore, 1983). Se elige proteger el dato **momentáneo**, porque ese es el único fin del cierre. El juicio del día es retrospectivo y el ánimo del momento ya lo tiñe en cualquier orden. El mismo artículo de Schwarz y Clore encontró que, cuando la persona se fija en su ánimo actual, este pesa menos en el juicio. Es una **[Aproximación]**: viene de estudios de encuestas y de laboratorio, no de una prueba con esta app. La forma de saberlo en tus datos es comparar los dos tramos (abajo).
- **Lo que hace riguroso el cambio:**
  - **El orden queda fijo**: «solo lo que falta» oculta lo ya contestado, pero no reordena.
  - **Cada día guarda con qué orden se llenó**: un campo `Dia.version_noche` (1 = el orden de hoy; 2 = el nuevo). Así se separan los tramos en R, igual que `ventana_pantalla` en la 10b.
  - **El Apéndice D anota la versión** en que entró.
- **Dónde:** `ui/pantallas/Dia.kt:420-545` (orden), `:457-468` (avisos después de la valencia), `:765-768` (foto); `ui/pantallas/Lote2.kt:494-507` (`BloqueCierre` solo hoy); `ui/Componentes.kt` (`Escala` con otra paleta para la activación).
- **Riesgo:** costumbre, porque llenarás la noche en otro orden. Metodológico: es un cambio a propósito y queda medido por `version_noche`.
- **Esfuerzo:** chico.
- **Paper:** **sí**:
  - capítulo 3, secciones 3.7.1 (contenido y orden), 3.7.5 (el cierre pasa al principio y solo para hoy) y 3.4;
  - H-2-8 y H-2-11 se resolverían;
  - H-NAV-9;
  - Apéndice A (`version_noche`);
  - referencias: las cuatro de abajo, ya comprobadas.
- **Cambia lo que se guarda:** **sí**, `Dia.version_noche`. Sube `SCHEMA_V` (se junta con las demás marcas, § 10), y cambian el diccionario y R (`vm_dias`, `vm_panel`).
- **Cómo se comprueba:** captura de la noche en dos temas (la cuadrícula arriba y los avisos ocultos hasta elegir); una prueba de que el cierre no se guarda en un día pasado.
- **Referencias** (DOI comprobados en la sesión con Crossref):
  - Lieberman, M. D., Eisenberger, N. I., Crockett, M. J., Tom, S. M., Pfeifer, J. H. y Way, B. M. (2007). Putting feelings into words. *Psychological Science, 18*(5), 421-428. https://doi.org/10.1111/j.1467-9280.2007.01916.x
  - Schwarz, N. y Clore, G. L. (1983). Mood, misattribution, and judgments of well-being: Informative and directive functions of affective states. *Journal of Personality and Social Psychology, 45*(3), 513-523. https://doi.org/10.1037/0022-3514.45.3.513
  - Schwarz, N., Strack, F. y Mai, H.-P. (1991). Assimilation and contrast effects in part-whole question sequences: A conversational logic analysis. *Public Opinion Quarterly, 55*(1), 3-23. https://doi.org/10.1086/269239
  - Westermann, R., Spies, K., Stahl, G. y Hesse, F. W. (1996). Relative effectiveness and validity of mood induction procedures: A meta-analysis. *European Journal of Social Psychology, 26*(4), 557-580. https://doi.org/10.1002/(SICI)1099-0992(199607)26:4<557::AID-EJSP769>3.0.CO;2-4

### N-19 · Análisis: los resultados primero
- **Problema:** E-32; P5.
- **Cambio:**
  - Arriba: Experimentos y «las relaciones más marcadas» (3-5, con «ver más»).
  - Las herramientas pasan a una rejilla de fichas.
  - Todo lo demás (cruzar variables, ¿qué tan bien te conoces?, emociones por hora, con quién, listas, lugares, antes y después), en secciones plegables.
  - Las cifras y la ayuda de potencia, en una línea.
- **Dónde:** `ui/pantallas/Explorar.kt:100-280`.
- **Riesgo:** ninguno.
- **Esfuerzo:** chico.
- **Paper:** capítulo 6, si describe el orden de la pantalla (revisar).
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** captura de Análisis de Ana.

### N-20 · Vida: lo semanal arriba y las galerías juntas
- **Problema:** E-32 y E-13; P5.
- **Cambio:** después de la rejilla de 8, Campañas y «Dinero y sustancias». Debajo, plegado, «Galerías ▸» (Tu año en historias, Logros, Mapas, Obras, Otras vidas). Totales sale de Análisis y se queda solo en Vida.
- **Dónde:** `ui/pantallas/Vida.kt:72-102`; `ui/pantallas/Explorar.kt:105`.
- **Riesgo:** costumbre.
- **Esfuerzo:** chico.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** captura de Vida.

### N-21 · Más: índice arriba, secciones plegables y lo que falta
- **Problema:** E-27, E-30 y E-33; P5 y P10.
- **Cambio:**
  - Arriba de Más, cuatro fichas «1 Registros · 2 Gente · 3 Medir · 4 Sistema» que llevan a su sección, cada una plegable y con su cuenta.
  - Se agregan las que faltan:
    - Gastos, Experimentos, Campañas y Lugares, en «1 Registros» y «3 Medir»;
    - «Días especiales y cumpleaños», con su aviso, en «2 Gente».
  - La tuerca pasa a 48 dp y dice «Más» (P6).
- **Dónde:** `ui/pantallas/Mas.kt:47-148`; `ui/Decoracion.kt:1243-1370`.
- **Riesgo:** ninguno. Las rutas no cambian.
- **Esfuerzo:** chico.
- **Paper:** no. El capítulo 10 dice «Más → Privado», y eso sigue igual.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** captura de Más.

### N-22 · Buscador completo, por palabras y con acciones
- **Problema:** E-23, E-26 y E-33; P6.
- **Cambio:**
  - **Agregar las rutas que faltan**: `diario` (pestaña), `mas`, `lugares`, `analisis` (cada herramienta), `mundo`, `rascar`, `unesco`, `ciudad_nueva`, `biblioteca` («Pendientes de leer o ver»), `carta_nueva`, la semana corta y «Hace un año».
  - **Buscar por palabras sueltas**, en cualquier orden.
  - Mostrar más de 8 resultados.
  - **Acciones** además de pantallas: «corregir sueño», «quitar siesta», «suplemento de ayer», «cambiar hora de un momento», «borrar gasto», «aviso de cumpleaños», «día lógico / madrugada». Cada una lleva al lugar exacto, por ejemplo al Día de ayer con `EditarSueno` abierto.
  - Las fichas @persona y #tema del Resumen semanal llevan su texto al buscador.
- **Dónde:** `ui/pantallas/Pantallas.kt:36-120`; `ui/pantallas/Semana.kt:131-132`.
- **Riesgo:** **privacidad**. Tu respuesta a P-2: que el buscador encuentre todo, no solo «Privado». Lo entiendo así:
  - el buscador indexa **todas las pantallas y acciones**, también las de Privado, con sus palabras de función («impulso», «recaída», «medicamentos», «registrar un impulso»…);
  - **lo que escribiste** en lo privado (textos, detonantes) no sale en los resultados fuera de Privado, porque lo prohíbe la regla de la entrega 10a (`data/Privado.kt`, vigilada por `PrivadoPrueba`).

  Si querías que también se busque ese contenido, dímelo: habría que cambiar esa regla a propósito.
- **Esfuerzo:** medio.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** una prueba unitaria de `Pantallas.buscar` («corregir sueño», «lugares», «gastos mes»).

### N-23 · Un solo nombre para cada cosa (sin tocar las rutas)
- **Problema:** E-32, E-35 y E-37; P6 y N2.
- **Cambio** (solo textos):
  - el Día se titula **«Hoy»** cuando es hoy y «Día» en otra fecha;
  - **«Llenar el día»** en todas partes: ⊕ «Diario de hoy» → «Llenar el día», y el acceso directo igual;
  - el widget «✎ diario» y el aviso «Llena tu diario» abren la noche, como dice el comentario de `MainActivity.kt:248`;
  - `biblioteca` se titula «Pendientes» y `obras`, «Obras», también en el buscador;
  - las herramientas (`analisis`) llevan el nombre de cada una, no «Análisis»;
  - «Guía» → «Cómo usar la app» y «Guía científica» → «Cómo leer tus datos»;
  - la clave de la guía de Privado se arregla, para que tenga su «?»;
  - el Día tiene su «?».
  - **Temas** (tu respuesta a P-1): el nombre del tema y el nombre en español van **en la misma línea y del mismo tamaño**, por ejemplo «Quaestiones · Análisis». El del tema va con **más contraste** (color principal y peso medio); el español, un poco más tenue. Hoy el español sale abajo, en letra pequeña de comentario (`Estilo.kt:50-61`).
- **Dónde:** `ui/pantallas/Dia.kt:164-169`; `ui/QuickActivity.kt:211`; `res/xml/accesos.xml` (solo el rótulo); `widget/Widgets.kt:170`; `avisos/Avisos.kt:173`; `ui/pantallas/Biblioteca.kt:190`; `ui/pantallas/Analisis.kt:101`; `ui/pantallas/Mas.kt:121`, `:141`; `data/GuiaUso.kt:85-91`; `ui/Estilo.kt:65-73`.
- **Riesgo:**
  - Rutas: **ninguna cambia**. El acceso directo conserva el id `diario` y solo cambia su rótulo.
  - Costumbre: los nombres en latín son parte del estilo que elegiste.
- **Esfuerzo:** chico.
- **Paper:** capítulo 3, sección 3.7 (dice «diario de la noche» y «hoja de la noche»): conviene unificar con «Llenar el día».
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** capturas de Hoy, la noche y Más en romano, medieval y ciber.

### N-24 · Gestos con alternativa visible
- **Problema:** P7.
- **Cambio:**
  - foto del día: botón «elegir» (ver N-18);
  - Mapa: botón «＋ lugar aquí», que usa el centro del mapa;
  - mapa para rascar: botones + y −, y «ver todo el mundo»;
  - historias de Tu año: flechas y pausa;
  - los toques largos de la barra, explicados en la guía (sección «Atajos»), no solo al fondo de Más.
- **Dónde:** `ui/pantallas/Dia.kt:765-768`; `ui/pantallas/Mapa.kt:451-550`; `ui/pantallas/Mundo.kt:715-750`; `ui/pantallas/TuAnio.kt:625-655`; `data/GuiaUso.kt`.
- **Riesgo:** ninguno.
- **Esfuerzo:** chico.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** capturas.

### N-25 · Pantallas huérfanas y crear personas a mano
- **Problema:** E-25; anexo A, § 4.1.
- **Cambio:**
  - **Quitar** el código muerto: `PantallaHall`, `RecuadroHall`, `CentroEntender`, `Portada` y la rama «Sueño» de `Dia.kt:144`.
  - **`PantallaMomentos`**: decidir entre revivirla o quitarla (pregunta P-4, explicada otra vez en § 13). Mi recomendación es quitarla: con N-26, todo se corrige desde el Día de cada fecha. La ruta `momentos` se queda como desvío a la pestaña Diario, como hoy.
  - En Tu gente, «+ persona» sin mención previa, con nombre, relación y nacimiento.
- **Dónde:** `ui/pantallas/Hall.kt:365-510`; `ui/pantallas/Explorar.kt:432`; `ui/pantallas/Hoy.kt:617-623`; `ui/pantallas/Momentos.kt:57-101`; `ui/pantallas/Gente.kt`; `data/Personas.kt`.
- **Riesgo:**
  - Rutas: `hall` se queda como alias de `logros`, por si algún ★ la guardó.
  - Datos: una persona creada a mano sin mención no tiene días. Es correcto.
- **Esfuerzo:** chico (quitar) o medio (persona a mano).
- **Paper:** capítulo 7, si describe cómo nace una persona (revisar).
- **Cambia lo que se guarda:** no (`Persona` ya existe en `Config`).
- **Cómo se comprueba:** compila; captura de Tu gente.

### N-26 · El Día como centro de corrección (P11)
- **Problema:** E-21, E-22, E-13, E-04 y E-11; P1, P2, P3 y P11.
- **Cambio:** el Día de cualquier fecha muestra, dentro de «Tu día» (N-17), **todo** lo de ese día, y cada fila se corrige al tocarla:
  - momentos y consumos → sus diálogos (N-02);
  - tramos → `DialogoTramo` (N-02);
  - **gastos** → el diálogo de N-06;
  - **tomas** → el de N-07;
  - **notas** → el de N-11;
  - visitas → N-05;
  - sueño → `EditarSueno` (N-01).

  Al final de «Tu día», «＋ agregar a este día…» abre, **con la fecha ya puesta**, el gasto, la toma, la nota o el registro de ánimo con hora (N-08).
- **Dónde:** `ui/pantallas/Dia.kt`, más los diálogos de N-02, N-06, N-07 y N-11. `Seleccion` pasa la fecha al registro rápido como extra (`QuickActivity` ya recibe extras).
- **Riesgo:**
  - Privacidad: lo privado no se lista; se sigue llegando por «privado de este día →».
  - Metodológico: más registros con fecha pasada; deben quedar marcados (P12, H-NAV-9).
- **Esfuerzo:** grande (es la suma de N-02, N-06, N-07 y N-11, más el «＋ agregar»).
- **Paper:** capítulo 3, sección 3.15.3; H-NAV-9.
- **Cambia lo que se guarda:** solo lo que cambien N-03, N-07 y N-08.
- **Cómo se comprueba:** capturas del miércoles de Ana con todo listado; en el teléfono, corregir los cuatro errores del miércoles sin salir del Día.

### N-27 · Accesibilidad y alcance
- **Problema:** anexo E, § 4 y § 6; P10.
- **Cambio:**
  - la cuadrícula de ánimo con semántica: 49 celdas con nombre, o una alternativa con dos escalas para TalkBack;
  - lupa, tuerca y modo de 48 dp; el modo, lejos de la lupa;
  - `contentDescription` en los ≈ 31 botones que son solo un glifo (‹ › × ⋯ ★ ↑↓ ?);
  - el botón de volver con una descripción fija («Volver»), aunque el texto cambie con el tema;
  - «Guardar» del escritor del diario también abajo;
  - Tu año y la capa de ánimo del Calendario muestran el número al tocar.
- **Dónde:** `ui/Selectores.kt:102-140`; `ui/Decoracion.kt:1243-1370`; `ui/Hoja.kt:41-60`; `ui/Libros.kt:1090-1200`; `ui/pantallas/Anno.kt`, `Calendario.kt`.
- **Riesgo:** ninguno.
- **Esfuerzo:** medio.
- **Paper:** no.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** Accessibility Scanner en el teléfono; capturas.

### N-28 · «Tus últimos días» en lugar del personaje
- **Problema:** tu respuesta a P-10. El personaje no se usa y no se ve bien; quieres algo **dinámico, que salga de tus datos** y que mejore mucho cómo se ve Hoy. Además sirve a P3 (ver de un vistazo qué días quedaron vacíos) y a E-22.
- **Cambio:** una franja a todo lo ancho, de unos 140 dp de alto, justo debajo de la frase. Muestra **tus últimos 14 días** como 14 «hilos» verticales:
  - **Color**: la valencia de ese día, con la misma paleta divergente del Calendario (`Tonos.valencia`): tonos fríos para los días malos, cálidos para los buenos.
  - **Alto**: las horas de sueño de esa noche frente a tu normal. Más alto, más sueño.
  - **Remate**: una pequeña huella (el sello que ya existe, `VistaHuella`) si el día quedó sellado; un punto si tiene diario sin sellar.
  - **Días sin nada**: solo el contorno, hueco. Así el jueves y el viernes de Ana se ven vacíos sin leer nada.
  - **Hoy**: el último hilo se va llenando durante el día (sueño → ánimo → diario), como los anillos del widget.
  - **Debajo, una línea**: «esta semana: ánimo 4,6 (↑0,3) · sueño 6 h 50 · 2 días sin llenar →».
  - **Al tocar** un hilo se abre ese día (apilado, con «atrás»). La línea de abajo lleva a «ponerte al día» (N-09).
  - **Cada tema lo dibuja a su manera**, con los recursos de dibujo que ya tiene (`ui/Decoracion.kt` y `Tonos`, en `ui/Tema.kt`): en medieval, franjas iluminadas con remate dorado; en ciber, barras con líneas de escaneo; en romano, columnas.
- **Alternativa más simple** (si la franja no convence en las capturas): solo **la huella del día** creciendo con lo que llenas, al lado de la fecha.
- **Dónde:** un componente nuevo `FranjaDias` en `ui/pantallas/Hoy.kt`. `TarjetaPersonaje` sale de `ui/pantallas/Dia.kt:290`. En Apariencia, el interruptor «Personaje» (`ui/pantallas/Sistema.kt:315`) pasa a ser «Tus últimos días».
- **Riesgo:**
  - Privacidad: usa solo la valencia, el sueño y si hay diario. Nada privado.
  - **Metodológico (reactividad)**: ver todos los días tu historial de ánimo puede influir en cómo calificas el día (capítulo 3, sección 3.14). El Calendario ya lo muestra, pero esto lo pone en primer plano. Para no influir en la noche, la franja **no aparece en la hoja de la noche** y el hilo de hoy no muestra el color hasta que el día está sellado.
- **Esfuerzo:** medio (es sobre todo diseño). Conviene revisar las capturas en 3 o 4 temas antes de pasarlo a `main`.
- **Paper:** capítulo 3, sección 3.14 (reactividad), una línea con la fecha de la versión.
- **Cambia lo que se guarda:** no.
- **Cómo se comprueba:** capturas de Hoy con los datos de Ana en medieval, romano, ciber y gótico.

### N-29 · Gastos en dos o tres toques, con subcategorías e ingresos
- **Problema:** tu respuesta a P-7 y E-24. La forma actual es limitada:
  - el gasto rápido pide siempre escribir el monto;
  - no hay subcategorías;
  - los ingresos solo se anotan en Vida → Dinero;
  - las categorías propias existen, pero escondidas en Más → Datos (`ui/pantallas/Sistema.kt:728-736`).
- **Cambio:**
  1. **Subcategorías con monto habitual.** Cada categoría puede tener subcategorías, por ejemplo Comida → Café, Comida → Súper, Transporte → Camión. Cada subcategoría tiene un **monto habitual**: por omisión, el monto que más se repite en tus últimos 10 gastos de esa subcategoría; se puede fijar a mano.
  2. **Frecuentes arriba.** La hoja de gasto abre con una fila de **frecuentes**: tus 6 subcategorías más usadas, con su monto («☕ Café $55», «🚌 Camión $12»). Tocar una registra el gasto en el acto y muestra «Café $55 registrado · cambiar monto · deshacer».
     - Camino: **⊕ → Gasto → Café = 3 toques**, sin escribir.
     - Si no es frecuente: categoría → subcategoría → «Registrar» (4-5 toques, sin escribir si tiene monto habitual).
     - Escribir solo cuando el gasto es muy específico: «otro monto» y la nota siguen ahí, debajo.
  3. **Ingresos en la misma hoja.** Arriba, un selector **«Gasto | Ingreso»**. Con «Ingreso» cambian las categorías (sueldo, beca…) y el botón dice **«Registrar ingreso»**. En ⊕ el botón pasa a llamarse **«Gasto o ingreso»**.
     - Es la mejor de tus dos opciones: un botón aparte en ⊕ ocuparía lugar en el menú de todos los días para algo que pasa pocas veces al mes, y la misma hoja te deja el mismo camino para las dos cosas.
  4. **Categorías y subcategorías propias, a la vista.** En Gastos, «editar categorías»: agregar, renombrar, ordenar y quitar categorías y subcategorías, y fijar el monto habitual. Al renombrar, la app pregunta si cambia también los gastos anteriores. También estará en el buscador («categorías de gasto»). Se quita el editor escondido de Más → Datos.
  5. **El widget de gasto** muestra tus 6 frecuentes con su monto: **1 toque** registra.
  6. **Gastos por subcategoría**: en Gastos, el mes por categoría se despliega por subcategoría («Comida $2,300 → Café $620»). Resuelve E-24.
- **Dónde:**
  - `ui/QuickActivity.kt:618-662` (`GastoRapido`);
  - `ui/pantallas/Gastos.kt`;
  - `widget/Widgets.kt:180-200`;
  - `data/Modelo.kt` (`Gasto.subcategoria`, `Config.subcategorias`);
  - `ui/pantallas/Sistema.kt:728-736`;
  - `data/Exportar.kt:357` (columna nueva), `data/Diccionario.kt`;
  - `ui/pantallas/Pantallas.kt`.
- **Riesgo:**
  - Datos: los gastos viejos quedan sin subcategoría. Se puede ofrecer una sola vez: «tienes 40 gastos con la nota "café": ¿pasarlos a Comida → Café?».
  - Métodos: el panel solo usa `gasto_total` y `gasto_imprevisto` (`ciencia/Estadistica.kt:367-368`), que no cambian. Registrar con un toque probablemente hará que se anoten **más** gastos pequeños: `gasto_total` puede subir a partir de esa versión sin que gastes más (H-NAV-9).
  - Doble toque: el registro de un toque necesita su «deshacer» y no repetirse si se toca dos veces en menos de 2 s.
- **Esfuerzo:** medio.
- **Paper:** Apéndice A (columna `subcategoria` de `vm_gastos.csv`) y H-NAV-9 (la completitud cambia).
- **Cambia lo que se guarda:** **sí**: `Gasto.subcategoria` y `Config.subcategorias` (con el monto habitual). Sube `SCHEMA_V`; cambian el diccionario y, si lee las columnas por nombre, `vestigium.R`.
- **Cómo se comprueba:** capturas de la hoja de gasto (gasto e ingreso) y de Gastos por subcategoría; una prueba del monto habitual.

### N-30 · La fecha de anotación queda en todo registro (P12)
- **Problema:** tu respuesta a P-3: que quede marcado lo anotado para otra fecha, y también **cuándo** se anotó. H-NAV-9, H-2-9.
- **Cambio:** cada registro guarda, además de su hora (cuándo pasó), **cuándo lo anotaste** y, si se corrige, **cuándo lo corregiste**:
  - `ts_creado` en momentos, tomas, gastos, ingresos, consumos y tramos (el día y las notas ya lo tienen: `Dia.ts_creado`, `Nota.creada`);
  - `ts_editado` en momentos (H-2-9), gastos e ingresos;
  - lo que ya existía antes de la versión queda sin marca (vacío), como en la 10b.
- **En R:** con las dos horas se calcula el retraso de cada registro. La regla de «anotado tarde» se decide en el análisis, no en la app. Por ejemplo: más de 60 minutos después de su hora, o en otro día lógico (umbral **[Diseño propio]**, que se documenta en el paper). `vestigium.R` suma una función para filtrar o separar los registros tardíos.
- **Dónde:** `data/Modelo.kt` (campos nuevos), `data/Repo.kt` (`guardar*` pone `ts_creado` si viene vacío), `data/Exportar.kt`, `data/Diccionario.kt`, `app/src/main/assets/vestigium.R`, `data/Fusion` (conservar la marca al restaurar).
- **Riesgo:** ninguno para lo privado (la marca no dice nada del contenido). Datos: los registros viejos no tienen marca; R los trata como «sin dato», no como «a tiempo».
- **Esfuerzo:** chico en la app, medio en R y en el paper.
- **Paper:** capítulo 3 (secciones 3.4 y 3.15), Apéndices A y C, H-2-9 (se resolvería) y H-NAV-9.
- **Cambia lo que se guarda:** **sí** (sube `SCHEMA_V`).
- **Cómo se comprueba:** pruebas de `Repo` (la marca se pone al crear, no cambia al editar y sobrevive a la fusión de respaldos); una prueba de exportación con las columnas nuevas.

### Fuera de la aprobación: errores que conviene corregir ya

Son fallas del código, no decisiones de diseño. Están en `docs/paper/hallazgos.md`:
- **H-NAV-6**: dos errores al corregir y borrar una recaída (solo la mecánica).
- **H-NAV-5**: «volver a lo del reloj» puede borrar una noche anotada.
- **H-NAV-8**: «Día difícil» no se puede quitar.

Pueden ir en la primera entrega de código o antes, en una rama aparte.

---

## 9. Mapa propuesto

```
BARRA (sin cambios de rutas): Hoy · Diario · ＋ · Vida · Análisis   (toques largos iguales, explicados en la guía)

HOY / DÍA (título «Hoy» o «Día · fecha»; con el tema: «Del día · Hoy», los dos del mismo tamaño)
 ├─ fecha y estado (‹ › hoy); en la madrugada, «va al lunes 26»
 ├─ frase del día (arriba, como ahora)
 ├─ Tus últimos días: 14 hilos (color = ánimo, alto = sueño, huella = sellado, hueco = sin llenar) → tocar abre el día
 ├─ Te falta hoy: ayer y días sin abrir (→ ponerte al día) · sueño · avisos · experimento sí/no · lo de anoche ·
 │                quién es @x · N datos que faltan
 ├─ Llenar el día (compacto, a toda hora)
 ├─ Tablero compacto (4 cifras tocables → su corrección) · ver todo
 ├─ ▸ Tu día: línea de 24 h + lista por hora (momentos, consumos, tramos, gastos, tomas, notas, visitas), todo tocable
 │           + «＋ agregar a este día…»
 ├─ ▸ Sueño (calidad, sueño recordado, corregir horas y siestas)
 ├─ ▸ Hace un año · días especiales
 └─ ▸ Lo que registró el teléfono
DIARIO: igual (Línea de tiempo plegada); los momentos también se corrigen desde Hoy
VIDA: rejilla 8 · Campañas · Dinero y sustancias · ▸ Galerías (Tu año en historias, Logros, Mapas, Obras, Otras vidas)
ANÁLISIS: Experimentos · relaciones más marcadas · herramientas en fichas · ▸ el resto
MÁS (⚙ «Más», 48 dp): índice 1-4 · ★ atajos
 ├─ 1 Registros: Escalada · Gimnasio · Enfoque y siestas · Hábitos · Suplementos · Notas · Metas · Gastos · Cartas · Sueños · Frases
 ├─ 2 Gente: Tu gente · Días especiales y cumpleaños · Red · Tus temas · Sustancias · Privado
 ├─ 3 Medir: Salud · Tus apps · Laptop · Cuestionarios · Reacción · Experimentos · Campañas · Lugares · Brújula · Conocerte
 └─ 4 Sistema: Cómo usar la app · Apariencia (con semana corta) · Avisos · Privacidad · Datos · Estado · Cobertura ·
               Lugares especiales · Reportes · Permisos · Cómo leer tus datos
```

**Dónde queda cada cosa movida**

| Qué | Antes | Después | Cómo se encuentra |
|---|---|---|---|
| Cuestionarios, reacción, enfoque (botones) | Hoy, debajo del Tablero | invitación en «Te falta hoy» cuando tocan; Más → 3 / 1; ⊕ (enfoque) | buscador |
| Personaje | Hoy, primer alto | sale de Hoy; en su lugar, «Tus últimos días» (N-28) | Apariencia («Tus últimos días») |
| Categorías de gasto | Más → Datos (escondido) | Gastos → «editar categorías» | buscador «categorías de gasto» (nuevo) |
| Ingresos | Vida → Dinero → «+ Ingreso» | además en ⊕ → «Gasto o ingreso» → «Ingreso» | buscador |
| Pantalla Momentos | sin acceso | según P-4 | — |
| Pasar a la semana corta | al fondo de Hoy | Apariencia | buscador «semana corta» (nuevo) |
| Respuesta del experimento | al fondo de Hoy | «Te falta hoy» | — |
| Momentos, enfoque y siestas, 24 h | tres secciones de Hoy | «Tu día» | — |
| Totales | Vida y Análisis | Vida | buscador |
| Herramientas de Análisis | 8 filas de menú | fichas | buscador (nuevo) |
| Lugares | Vida → Mapas → Lugares | además en Más → 3 | buscador (nuevo) |
| Gastos, Experimentos, Campañas | solo Vida o Análisis | además en Más | buscador |
| Aviso de cumpleaños | Tu gente → Días especiales | además en Más → 2 y en Avisos | buscador (nuevo) |

---

## 10. Entregas sugeridas

Cada una se compila y se revisa por separado: una rama, capturas y luego `main`. Van en orden de impacto. Con tus respuestas, los cambios a lo que se guarda se juntan en **una sola entrega (NE)**, para subir `SCHEMA_V` una sola vez: de 13 a 14. Las entregas de pantallas que vienen después ya encuentran los campos listos.

| Entrega | Propuestas | Por qué en este orden | ¿Sube `SCHEMA_V`? |
|---|---|---|---|
| **N0 · Errores** | correcciones de H-NAV-5, H-NAV-6 y H-NAV-8; el nombre del perfil en `Capturas.kt` (P-11) | Son fallas: no esperan decisiones de diseño | No |
| **NE · Esquema 14** | los campos de N-30 (`ts_creado`, `ts_editado`), N-18 (`version_noche`), N-29 (`subcategoria`, `subcategorias`), N-04 (siestas descartadas) y N-14 (papelera); exportación, diccionario, `vestigium.R` y paper | Una sola subida de esquema, con sus pruebas, antes de las pantallas | **Sí, a 14** |
| **N1 · Corregir donde se ve** | N-01, N-02, N-03, N-06, N-13 | Resuelve tus ejemplos 1 y 3 y casi todo el «no veo dónde se corrige» | No (usa NE) |
| **N2 · Ayer y fechas** | N-07, N-08, N-09, N-10, N-11, N-12 | Resuelve tu ejemplo 2 y los días sin abrir la app | No (usa NE) |
| **N3 · El Día como centro** | N-17, N-26, N-15 | Junta N1 y N2 en un solo lugar | No |
| **N4 · Hoy, la noche y las pestañas** | N-16, N-28, N-18, N-19, N-20 | Lo más visible, con el orden riguroso de la noche | No (usa NE) |
| **N5 · Dinero** | N-29 | Gastos en dos o tres toques, subcategorías e ingresos | No (usa NE) |
| **N6 · Más, buscador y nombres** | N-21, N-22, N-23, N-25 | Encontrar lo raro | No |
| **N7 · Sueño, lugares, papelera, gestos y accesibilidad** | N-04, N-05, N-14, N-24, N-27 | Lo más grande y lo que toca los sensores | No (usa NE), salvo que N-05 recuerde visitas quitadas |

---

## 11. Preguntas para el autor

Ya las contestaste: tus respuestas están en el § 13. Solo queda pendiente **P-4**, que explico otra vez allí.

---

## 12. Bocetos

### Hoy, a mediodía: antes y después (primer alto, unos 2200 px)

Con tus respuestas a P-1 y P-10: el nombre del tema y el español en una línea, la frase arriba y «Tus últimos días» en lugar del personaje.

```
ANTES                                   DESPUÉS
┌──────────────────────────────┐        ┌──────────────────────────────┐
│ Día            🔍 ⚙ ☾ (emb)  │        │ Del día · Hoy        🔍 ⚙ ☾  │
│ xxvij de otubre ▾            │        │ martes 27 de octubre ▾       │
│ martes · hoy · ◑             │        │ ‹ guardado · racha 30      › │
│ ‹ estado = guardado        › │        ├──────────────────────────────┤
├──────────────────────────────┤        │ Sentencia del día            │
│ Sentencia del día            │        │ Sic stude quasi semper…      │
│ Sic stude quasi semper…      │        │ Estudia como si fueras a…  ♡ │
│ Estudia como si fueras a…    │        ├──────────────────────────────┤
│ — Isidoro de Sevilla      ♡  │        │ TUS ÚLTIMOS DÍAS             │
├──────────────────────────────┤        │ ▌▐▌▐▌▌▐▌▐▌ ▌ ┆ ┆ ▏            │
│ (personaje) ¿Qué haremos hoy?│        │ (color=ánimo, alto=sueño,    │
├──────────────────────────────┤        │  ┆ = jue y vie vacíos, ▏= hoy)│
│ LO QUE RESTA                 │        │ semana: ánimo 4,6 ↑ · 2 días │
│ [Calidad del sueño →]        │        │ sin llenar →                 │
│ si tienes tiempo: WHO-5 →    │        ├──────────────────────────────┤
│ reacción → brújula → hoy no  │        │ TE FALTA HOY                 │
├──────────────────────────────┤        │ [Calidad del sueño →]        │
│ TABLA (empieza aquí, abajo)  │        │ [Experimento: ¿leíste? sí|no]│
└──────────────────────────────┘        │ [Faltan jueves y viernes →]  │
  «Llenar el día» ≈ y 3800              ├──────────────────────────────┤
  experimento ≈ casi al fondo           │ LLENAR EL DÍA  ○ánimo ○diario│
                                        │ ○predicción · hábitos 0/2 [▶]│
                                        └──────────────────────────────┘
                                          debajo: Tablero compacto (4 cifras ›),
                                          ▸ Tu día · ▸ Sueño · ▸ Hace un año …
```

Con la frase arriba y la franja, el Tablero compacto quedaría justo en el borde del primer alto (unos 2050 a 2400 px, según el largo de la frase y el tema). Si prefieres verlo sin bajar, la frase podría mostrarse en una línea y abrirse al tocarla. Lo comprobaré en las capturas.

### Día del miércoles: «Tu día», todo tocable (N-17, N-26)

```
▾ Tu día
  [línea de 24 h: sueño ▇▇  Oficina ▇▇▇ Gimnasio▇  café | momentos ·  · ]
  08:15  Gasto · Comida $55 · café            ›   (tocar → corregir o quitar)
  08:15  Gasto · Comida $55 · café            ›   ← el duplicado
  08:40  Vitamina D · 2000 UI                 ›
  09:00  Ánimo · 2/7                          ›
  13:05  Gimnasio (visita)                    ›   (tocar → «fue en… Café Centro»)
  15:00  Idea · Comida con @luis…             ›   (tocar → hora 13:00, alegría)
  01:30  Nota · Llamar al banco               ›   (de la madrugada siguiente)
  ＋ agregar a este día…  (gasto · toma · nota · ánimo con hora)
▾ Sueño   3 h 0 min según el reloj · siesta 35 min   [corregir horas y siestas]
```

### Ponerte al día (N-09)

```
┌──────────────────────────────┐
│ ← Ponerte al día             │
│   ‹ jueves 29 · viernes 30 › │
├──────────────────────────────┤
│ Ánimo del día   1 2 3 4 5 6 7│
│ Hábitos  ☐ Leer 20 min       │
│          ☐ Sin pantalla 23 h │
│ Suplementos  ☐ creatina 5 g  │
│              ☐ vitamina D    │
│ Sueño (calidad) 1 2 3 4 5    │
│ ▸ diario (opcional)          │
│            [siguiente día →] │
└──────────────────────────────┘
```

---

## 13. Tus respuestas (27 oct 2026) y qué cambian

| Pregunta | Tu respuesta | Qué cambia |
|---|---|---|
| **P-1** · Nombres por tema | Los dos del mismo tamaño y en la misma línea; el del tema con más contraste, el español un poco menos | N-23 (títulos «Quaestiones · Análisis»), § 9 y el boceto de Hoy |
| **P-2** · Privado en el buscador | Que salga todo, no solo «Privado» | N-22: el buscador indexa todas las pantallas y acciones, también las de Privado con sus palabras. **Lo que escribiste en lo privado sigue sin salir fuera de Privado** (regla de la 10a). Si querías también ese contenido, dímelo |
| **P-3** · Marcar lo anotado tarde | Sí, y que se guarde también la fecha de anotación | Propuesta nueva **N-30** (`ts_creado` y `ts_editado`); entra en la entrega NE (esquema 14) |
| **P-4** · Pantalla Momentos | No quedó clara | Explicada abajo |
| **P-5** · Hoy recuerda el día abierto | Que siempre vuelva a hoy | N-12 tal cual |
| **P-6** · Fuerza a mano | Por ahora, que dependa de MacroFactor | E-06 y E-17 quedan como están, por decisión tuya. No hay propuesta |
| **P-7** · Gastos | La forma actual es muy limitada: subcategorías (Comida → Café), dos o tres toques sin escribir, ingresos a la mano y categorías propias | Propuesta nueva **N-29**. Para los ingresos elegí el selector «Gasto \| Ingreso» dentro de la misma hoja, con el botón «Registrar ingreso» (razones en N-29) |
| **P-8** · Orden de la noche | El que dé más rigor científico | **N-18 reescrita**: el cierre va primero; después el juicio general del día; los avisos se ven solo después de elegir; las casillas de hechos y el diario al final; el orden queda fijo y se guarda `version_noche`. Lleva 4 referencias comprobadas |
| **P-9** · Papelera | Sí | N-14 aprobada; entra en NE y N7 |
| **P-10** · Personaje y frase | El personaje se ve feo y no lo usas; quieres algo dinámico con tus datos que mejore mucho el aspecto; la frase, arriba | Propuesta nueva **N-28** («Tus últimos días»); el personaje sale de Hoy; la frase se queda arriba (N-16) |
| **P-11** · Nombre en las capturas | Da igual | Lo cambio por uno inventado en la entrega N0, porque las reglas del repositorio piden no subir nombres reales |

### P-4, otra vez y más claro

Hay una pantalla vieja, **«Momentos»**, que antes era una pestaña. Lista tus registros de ánimo y momentos de los últimos 30 días, y tocar uno lo abre para corregirlo o borrarlo. **Hoy ningún botón lleva a ella**: quedó escondida cuando la pestaña pasó a ser «Diario». Su código sigue en la app (`ui/pantallas/Momentos.kt:57-101`).

Las opciones:
- **A · Revivirla** como «Todos tus registros», con un botón en Diario y entrada en el buscador. Sería una lista larga, para buscar un momento cuando no recuerdas el día.
- **B · Quitarla.** Con N-02 y N-26, cada momento se corrige tocándolo en el Día de su fecha. Diario → «Línea de tiempo» ya sirve para buscar por texto, @persona o #tema.

**Recomiendo B**: un solo lugar para corregir (P11) y menos código que mantener. Elige A si a veces buscas un momento sin saber de qué día fue y la línea de tiempo no te basta.

### Lo que falta para empezar

1. Tu elección en **P-4**.
2. Tu aprobación de las **30 propuestas** (todas o con excepciones) y del **orden de entregas** del § 10. Con eso empiezo por N0 (errores) y NE (esquema 14), cada una en su rama, con capturas antes de pasar a `main`.

---

## Anexo · Capturas de la Fase 6

- **Archivo:** `app/src/test/java/io/github/ruddy2003/vestigium/CapturasEscenario.kt` (solo pruebas).
- **Datos:** los de Ana, todos inventados.
  - **Martes**: normal, con un entrenamiento del reloj.
  - **Miércoles**, con errores:
    - el reloj marcó 3 h de sueño y una siesta de 35 min;
    - el café está dos veces;
    - un momento de tipo «idea» a las 15:00 que era alegría a las 13:00;
    - solo vitamina D, sin creatina;
    - una visita en «Gimnasio» que era en «Café Centro»;
    - una nota de la 1:30 que cayó en el miércoles.
  - **Jueves y viernes**: vacíos.
  - **Sábado (hoy)**: en la mañana, sin ánimo.
- **Temas:** medieval claro, romano oscuro y ciber (la noche, solo en los dos primeros: en ciber no cabía en la memoria del CI).
- **Pantallas:** Hoy, el Día del miércoles y del jueves, la noche, Diario, Vida, Análisis, Más, Momentos, Gastos, Suplementos, Tu gente, Notas y Experimentos.
- **Dónde verlas:** en la rama `capturas`, con el prefijo `io.github.ruddy2003.vestigium_CapturasEscenario_ana…`.

**Limitaciones de las capturas**:
- **La hora la pone el CI.** El orden de Hoy cambia con la hora (`LocalTime.now()`), y las capturas salieron a la hora en que corrió Actions: entre las 17:40 y las 18:30 UTC, la franja de mediodía. Para comparar mañana, mediodía y noche hace falta poder fijar la hora en la prueba, lo que obliga a tocar código de la app.
- **El estado se comparte.** Las capturas viejas (`Capturas.kt`) no limpian todo el estado entre pruebas (anexo D, § 0.2). Las de escenario cargan todo lo que usan, pero el orden de las pruebas no está garantizado.
- **Los lienzos tienen un tope.** Más de unos 9000 px agota la memoria de las pruebas en el CI.
- **Los diálogos no salen.** `EditarSueno` y `DialogoMomento` son ventanas aparte que Paparazzi no dibuja.
