# Plan · Revisión de navegación, corrección de datos y saturación

_Plan para que un chat nuevo haga la revisión completa. Escrito el 27 oct 2026. Este documento **no cambia nada**: dice qué revisar, cómo, y qué entregar._

---

## 0. Qué se busca

El autor quiere que la app sea:

1. **Fácil de navegar.** Llegar a cualquier cosa sin pensar.
2. **Fácil de corregir.** Cuando algo quedó mal o se olvidó, se arregla en el momento y sin buscar. Ejemplos del propio autor:
   - «Mi reloj metió mal el sueño de anoche; no veo un botón a la vista para corregirlo.»
   - «Ayer olvidé poner que tomé creatina; no sé cómo ponerlo.»
   - «Se guardó mal este momento; no veo dónde modificarlo.»
3. **Menos saturada.** Lo que no hace falta ver siempre, o sirve poco, va a un lugar más oculto, pero **fácil de encontrar de forma intuitiva**.

El método es **simular dos o tres días de uso real**, con olvidos y errores, y anotar cada fricción.

**Qué no es esta revisión:**
- No cambia cálculos, umbrales ni lo que se guarda.
- No toca el paper salvo en un caso: si una propuesta cambia **dónde** se registra algo y el paper lo describe (ver § 9).
- **No cambia código de la app.** Solo puede agregar capturas de prueba (§ 6, Fase 6).
- Termina en un **informe con propuestas**. El autor las aprueba punto por punto y solo después se implementan, en otra tarea.

---

## 1. Cuándo hacerla

- Va **después de la entrega 10b del módulo 10**, cuando ya esté mezclada a `main`. La 10b agrega pantallas y campos:
  - días especiales (cumpleaños, fechas propias, aniversarios de fallecimiento);
  - nacimiento y fallecimiento de las personas en Tu gente;
  - avisos de cumpleaños;
  - «¿Lo hiciste hoy?» en el aviso del experimento;
  - registro automático de los avisos de ánimo y de la campaña activa.

  La revisión tiene que incluirlos.
- **Antes de empezar, comprueba** en `docs/paper/apendiceD_cambios.md` y en la tabla de `docs/paper/PLAN_PAPER_METODOLOGIA.md` si la 10b ya está hecha.
  - Si no lo está, **avísale al autor** y pregúntale si sigue de todos modos.
  - Si sigue, marca en el informe como «pendiente de revisar tras 10b» todo lo que la 10b vaya a tocar.
- La 10c (cálculos) y la 10d (R y exportación) casi no tocan pantallas. Pueden hacerse antes o después de implementar lo que salga de esta revisión.

---

## 2. Reglas que siguen valiendo

Lee primero `CLAUDE.md`. En especial:

- **Todo en español.**
- **No toques la firma** ni la menciones.
- **Nunca uses datos reales.** Todo escenario, captura o ejemplo usa una persona inventada, con nombres, lugares, textos y cifras inventados. Usa nombres genéricos como «Ana», «Luis» o «Café Centro», **nunca el nombre del autor**. Si ves en `Capturas.kt` un nombre que parezca real, anótalo como hallazgo; no lo copies.
- **Lo privado se queda solo en la pantalla Privado** (entrega 10a). La regla está en `data/Privado.kt` y la vigila `app/src/test/.../PrivadoPrueba.kt`.
  - Ninguna propuesta puede sacar a la vista, en otra pantalla, impulsos, recaídas, libido, medicamentos, detonantes ni el hábito ligado.
  - Tampoco pueden aparecer en atajos, widgets, buscador, sugerencias o notificaciones.
  - Lo privado se describe solo en su mecánica: puedes revisar si la pantalla Privado es fácil de usar, pero sin ejemplos personales.
- Los cambios de código, cuando se aprueben, van en rama aparte. Se compilan en Actions y se revisan las capturas antes de pasar a `main`.
- Desarrolla y empuja solo en la rama que te asignen en ese chat.

---

## 3. Limitación y cómo compensarla

**No hay SDK de Android ni emulador en el entorno de Claude.** No se puede tocar la app de verdad. La simulación se hace de tres formas complementarias:

1. **Recorrido por el código.** Para cada escenario, sigue la ruta real:
   - desde que se abre la app: pestaña inicial, `PantallaDia`;
   - por cada `nav.ir(...)`, `nav.pestana(...)`, botón, toque largo y diálogo;
   - hasta lograr el objetivo o comprobar que no se puede.

   Cuenta los **toques** y anota si cada paso es **visible** (está en pantalla sin desplazarse), **descubrible** (se adivina que está ahí) o **escondido** (hay que saberlo).
2. **Capturas de Paparazzi con datos sintéticos** de los tres días (Fase 6). Sirven para ver las pantallas **llenas**:
   - cuánto miden;
   - qué queda arriba y qué abajo;
   - qué tan saturadas se ven.

   Las capturas están en la rama `capturas`. Las genera `.github/workflows/capturas.yml`, que se lanza con `workflow_dispatch`.
3. **Heurísticas de usabilidad** (§ 7), aplicadas pantalla por pantalla.

Di explícitamente en el informe qué no se pudo comprobar. Por ejemplo:
- la fluidez real;
- el alcance con el pulgar en un teléfono concreto;
- el teclado tapando campos;
- la velocidad de carga.

Esas cosas quedan como preguntas para que el autor las pruebe en su teléfono.

---

## 4. Lo que hay hoy (punto de partida)

Una foto tomada el 27 oct 2026, antes de la 10b. **Vuelve a comprobarla en el código vigente.**

- **Navegación:** clase `Nav` en `ui/MainActivity.kt`, con `ir`, `atras`, `pestana` y `reemplazar`. Es una pestaña activa con una pila de pantallas encima.
- **Barra inferior** (`PESTANAS` en `MainActivity.kt`):
  - Hoy (`dia`), Diario (`diario`), el botón central «＋» Registrar, Vida (`vida`) y Análisis (`entender`);
  - **toque largo** en cada pestaña abre un atajo: `dia`→noche, `diario`/`registrar`→ánimo rápido, `vida`→semana. Revisa si alguien lo descubriría.
- **Unas 75 rutas** en el `when` de `MainActivity.kt`. Hay alias: `hall`=`logros` y `buscar`=`totales(buscar = true)`.
- **Más** (`ui/pantallas/Mas.kt`) tiene secciones numeradas:
  1. Registros especiales;
  2. Tu gente y sustancias;
  3. Medir;
  4. Sistema;
  
  y además Activos, Archivados, Cómo leer tus datos, Cuestionarios y Marcos y referencias.
- **Buscador de pantallas:** `ui/pantallas/Pantallas.kt`.
- **Registro rápido:** `ui/QuickActivity.kt`, con los modos `animo`, `momento`, `menu`, `gasto`, `sueno`, `minimo`, `enfoque`, `siesta` y `consumo`.
- **Widgets:** `widget/Widgets.kt`, `Mosaicos.kt` y `Piel.kt`.
- **Notificaciones con acciones** (`avisos/Avisos.kt`):
  - «Día difícil: solo un número»;
  - «Importar ahora» y «Recordar mañana»;
  - «Ya los tomé» y «En una hora» para los suplementos.

  Hay accesos con ruta (`abrirApp(ctx, id, "datos")`): **renombrar una ruta los rompe**.
- **Corrección y deshacer:**
  - `ui/Deshacer.kt`: barra de 5 s para deshacer después de borrar;
  - `ui/EditarSueno.kt`: corrige el sueño. Hoy está **dentro del Día de esa fecha** («corregir las horas de sueño», `Dia.kt` ~l. 243);
  - `DialogoMomento` (`ui/pantallas/Momentos.kt`): edita momentos desde Momentos, Diarios y Privado. **Comprueba desde dónde más se puede abrir**, por ejemplo la línea del tiempo de Hoy, la cinta o el Día;
  - `Seleccion` (`ui/pantallas/Calendario.kt`): pasa una fecha elegida a otra pantalla.
- **Hoy** (`ui/pantallas/Hoy.kt`):
  - `TeFaltaHoy` (con `ocultar`);
  - `DatosQueFaltan`;
  - `FraseDelDia`;
  - `Tablero` (métricas reordenables con `mover`);
  - `LineaDelTiempo`;
  - `Portada`.
- **Día lógico:** empieza a las **05:00** (`data/Fechas.kt`). Algo anotado a la 1 a. m. cuenta para el día anterior. Esto es una fuente probable de «se guardó en otro día».
- **Tamaño:** 49 archivos en `ui/pantallas/`, unas 17 000 líneas. La captura de la noche (`nocheMedieval`) mide **5200 px** de alto, señal de saturación.

**Tipos de datos que el usuario registra o puede corregir** (ver `data/Modelo.kt` y `data/Repo.kt`, funciones `guardar*` y `borrar*`):

- **Día** (`Dia`): ánimo, diario de la noche, hábitos, y todo lo demás que guarde por día.
- Momentos.
- Tramos.
- Consumos o sustancias.
- Suplementos y tomas.
- Gastos e ingresos.
- Notas.
- Metas.
- Salud del día: sueño, siestas y el resto de los datos de salud.
- Enfoque (`PlanEnfoque`, `Sesion`).
- Fuerza (`EntrenoDia`, `Serie`).
- Escalada (`Ascenso`).
- Nutrición (`NutricionDia`, `Comida`).
- Lugares, visitas y lugares ignorados.
- Personas, con sus cambios de relación.
- Cartas.
- Obras y citas.
- Experimentos.
- Campañas.
- Cuestionarios (`Respuesta`) y la prueba de reacción (`Reaccion`).
- Perfil (Conocerte) y la prueba política.
- Laptop.
- Frases guardadas.
- Logros propios.
- Predicción del año.
- Vínculos.
- Privado.
- Lo que agregue la 10b: días especiales y fechas de nacimiento.

**Datos automáticos** (sensores, Health Connect, ubicación, pantalla, notificaciones, música, fotos, agenda, chats importados): no se registran a mano, pero **pueden llegar mal**. Para cada uno pregunta:
- ¿se puede corregir, ignorar o borrar un dato mal detectado?
- ¿dónde?

---

## 5. Personaje y guion de los tres días

Usa una persona **inventada**:
- «Ana», 30 años, oficinista;
- tiene un reloj que sincroniza el sueño;
- toma creatina y vitamina D;
- va al gimnasio dos veces por semana;
- escribe el diario en la noche;
- gasta en café;
- tiene dos hábitos: «Leer 20 min» y «Sin pantalla después de las 23»;
- tiene un experimento activo y una campaña.

Las fechas son inventadas: un martes, un miércoles y un sábado.

**Para cada escenario anota** (formato en § 8):
- qué quiere hacer Ana;
- desde dónde empieza, que casi siempre es abrir la app;
- la ruta actual, paso a paso, con el número de toques;
- si cada paso es visible, descubrible o escondido;
- si se puede deshacer;
- dónde se ve el resultado después;
- la fricción y su gravedad.

### Día 1 · martes normal (lo básico tiene que ser rápido)

- **E-01** Al despertar, ve en Hoy cómo durmió. ¿Se entiende de dónde viene el dato?
- **E-02** Registra su ánimo de la mañana desde la app, desde el widget y desde la notificación. Compara los tres caminos.
- **E-03** Marca que tomó creatina y vitamina D: desde la app y desde la notificación «Ya los tomé».
- **E-04** Anota un gasto de café al salir.
- **E-05** Anota un momento con una persona (@Luis) y un tema (#trabajo) a mediodía.
- **E-06** Registra un entrenamiento de fuerza con 3 ejercicios y sus series.
- **E-07** En la noche, escribe el diario y marca los hábitos del día.
- **E-08** Revisa qué le falta por registrar hoy (`TeFaltaHoy` y `DatosQueFaltan`).
- **E-09** Revisa su experimento: qué toca hoy y, tras la 10b, «¿Lo hiciste hoy?».

### Día 2 · miércoles con errores (corregir tiene que ser obvio)

- **E-10** **El reloj registró mal el sueño de anoche:** marcó 3 h en vez de 7 h. Corregirlo desde Hoy, donde lo está viendo.
- **E-11** **Se le olvidó marcar ayer la creatina.** Ponerla en la fecha de ayer.
- **E-12** **Un momento de ayer quedó con la hora equivocada** o con el tipo equivocado, por ejemplo «idea» en vez de «alegría». Encontrarlo y modificarlo.
- **E-13** Anotó el café dos veces por un toque doble. Borrar el duplicado y comprobar que se puede deshacer.
- **E-14** A la 1:30 a. m. escribe una nota. ¿En qué día cayó? ¿Lo entiende Ana? ¿Puede moverla al otro día si se equivocó?
- **E-15** Cambia el ánimo que marcó en la mañana porque tocó el número equivocado.
- **E-16** Marcó un hábito por error. Desmarcarlo.
- **E-17** Registró mal el peso de una serie de ayer. Corregirlo.
- **E-18** La ubicación detectó un lugar equivocado: dice «Gimnasio» y era «Café Centro». Corregirlo, o ignorar ese sitio para siempre.
- **E-19** Una siesta que marcó el reloj no fue siesta, solo estuvo quieta en el sofá. Quitarla.
- **E-20** Borró sin querer un momento. Recuperarlo. ¿Qué pasa si ya pasaron los 5 s?
- **E-21** Quiere ver y editar todo lo de ayer en un solo lugar.

### Día 3 · sábado con olvidos y consultas (encontrar tiene que ser intuitivo)

- **E-22** No abrió la app el jueves ni el viernes. Rellenar lo esencial de esos dos días (ánimo, hábitos, suplementos, sueño) sin ir pantalla por pantalla.
- **E-23** Busca algo que escribió el mes pasado sobre «la mudanza», con el buscador y desde Diario.
- **E-24** Quiere ver cuánto gastó en café este mes.
- **E-25** Agrega a una persona nueva con su fecha de nacimiento (10b). Luego quiere ver su edad.
- **E-26** Agrega un día especial propio, como el cumpleaños de su mamá, y comprueba que le avisará (10b).
- **E-27** Quiere cambiar la hora de un aviso, por ejemplo el del diario de la noche. ¿Dónde está?
- **E-28** Quiere saber si su respaldo está al día y si está cifrado.
- **E-29** Quiere exportar para R.
- **E-30** Termina su experimento y quiere ver el resultado.
- **E-31** Quiere registrar un consumo de alcohol de anoche.
- **E-32** Quiere ver «cómo va» en general: Vida, Análisis, semana y año. ¿Sabe qué pestaña abrir para qué?
- **E-33** Se le olvidó en qué parte de Más estaba una pantalla que usa una vez al mes, como Medir o los Cuestionarios. Encontrarla.
- **E-34** En un día difícil solo quiere poner un número y salir. Ver qué tan corto es el camino.
- **E-35** Usa la pantalla Privado para anotar un medicamento de ayer y corregir uno mal puesto. Revisa solo la mecánica, sin ejemplos personales.

### Recorridos completos

Además de los escenarios sueltos:

- **Recorre cada pantalla de cada ruta al menos una vez**, sin dejar ninguna fuera.
- Agrega escenarios si encuentras flujos que estos no cubren, numerados desde E-36.
- **Revisa las rutas huérfanas**, a las que no se llega desde ningún lado.
- **Revisa los callejones sin salida**, pantallas que no llevan a ningún sitio útil.

---

## 6. Fases de trabajo

Usa subagentes de exploración en paralelo si el volumen lo pide, uno por área: registro, corrección, consulta, ajustes y datos automáticos. **Las conclusiones se verifican en el código antes de escribirlas.** Las afirmaciones del tipo «no se puede» exigen haber buscado todas las entradas posibles: botones, toques largos, diálogos, buscador, widgets y notificaciones.

### Fase 1 · Mapa actual

Una tabla con **todas** las rutas del `when` de `MainActivity.kt`, con estas columnas:

| Ruta | Pantalla (archivo) | Cómo se llega (desde dónde, toques desde el arranque) | Qué muestra | Qué permite crear, editar o borrar | ¿Se puede elegir fecha? | ¿Deshacer? | Uso esperado (diario / semanal / mensual / raro) |
|---|---|---|---|---|---|---|---|

Después, un **árbol de navegación** que va de las pestañas a las pantallas y de ahí a los diálogos. Puede ir en texto o en mermaid. Incluye también las entradas desde fuera de la app: widgets, notificaciones, `QuickActivity` y atajos de toque largo.

Señala:
- rutas huérfanas;
- alias;
- rutas a las que se llega por más de un camino con nombres distintos;
- pantallas cuyo nombre no coincide con su título;
- la pestaña «Hoy», cuyo id es `dia`, frente a la pantalla «Día» de una fecha.

### Fase 2 · Matriz de datos × operaciones

Para **cada tipo de dato** del § 4, una fila con estas columnas, cada una con el camino y los toques, o «no se puede»:

| Dato | Crear hoy | Crear en otra fecha (ayer / hace 3 días) | Editar | Cambiar fecha u hora | Borrar | Deshacer | Dónde se ve después |
|---|---|---|---|---|---|---|---|

Incluye los **datos automáticos**: ¿se pueden corregir, ignorar o borrar?

Marca en rojo los huecos, por ejemplo «se crea pero no se edita» o «se edita solo desde una pantalla escondida».

### Fase 3 · Simulación de los tres días

Recorre los escenarios E-01 a E-35, y los que agregues, con el formato del § 8.

### Fase 4 · Inventario de saturación

Para **Hoy, Diario, la noche, Vida, Análisis, Más y el Día de una fecha**, haz una tabla con:
- cada tarjeta o sección;
- cuándo aparece (condición del código);
- para qué sirve;
- con qué frecuencia aporta algo: a diario, a veces, raro o nunca;
- si el usuario ya puede ocultarla o moverla (por ejemplo, `TeFaltaHoy(ocultar)` o el `Tablero` reordenable).

Mide la saturación con números:
- el alto de la captura en px;
- el número de tarjetas;
- el número de botones o enlaces;
- cuántas pantallas de alto hay que desplazarse en un teléfono de unos 2400 px.

Clasifica cada elemento en una de cinco opciones:
- **se queda a la vista**;
- **se pliega** («ver más»);
- **se mueve** a otra pantalla;
- **se fusiona** con otra;
- **se propone quitar**. Esta última solo como propuesta: el autor decide.

Lo que se esconda tiene que quedar **encontrable**: con el buscador, con «ver más» en el mismo lugar o con una entrada obvia en Más.

### Fase 5 · Heurísticas

Aplica las heurísticas del § 7 pantalla por pantalla y anota cada violación con su pantalla y su línea de código.

### Fase 6 · Capturas de los escenarios (solo pruebas)

- Agrega en `app/src/test/.../Capturas.kt`, o mejor en un archivo nuevo como `CapturasEscenario.kt`, **un juego de datos sintéticos de los tres días de Ana**. Usa las funciones `Repo.*EnMemoria` que ya usan las capturas.
- Captura con esos datos Hoy, la noche, el Día de ayer, Momentos, Diario, Vida, Análisis, Más y las pantallas donde se corrige.
- Usa **dos o tres temas** (por ejemplo medieval claro, romano oscuro y ciber) para que lo visual del tema no tape el problema de orden.
- **Solo se toca `app/src/test/`**, en la rama del chat. Lanza `capturas.yml` y revisa `errores.txt`, `fallas.txt` y los PNG en la rama `capturas`.
- No pases esto a `main` sin que el autor lo vea. Si el autor prefirió que no se toque nada, sáltate esta fase y dilo.

### Fase 7 · Propuesta

Ver el § 8. Incluye:
- los principios;
- el mapa nuevo;
- la lista de cambios N-01, N-02, …;
- las entregas en que se agrupan;
- las preguntas para el autor.

Si ayuda, haz **bocetos** de baja fidelidad del antes y el después de las pantallas principales, en texto o como una página aparte.

---

## 7. Heurísticas de evaluación

**Generales** (Nielsen, 10 heurísticas de usabilidad). Cita la fuente y comprueba el enlace en la sesión; si no puedes, márcala «[sin verificar]»:
1. Visibilidad del estado del sistema.
2. Coincidencia entre el sistema y el mundo real.
3. Control y libertad del usuario: deshacer y salir.
4. Consistencia y estándares.
5. Prevención de errores.
6. Reconocer antes que recordar.
7. Flexibilidad y eficiencia: atajos para el que ya sabe.
8. Diseño estético y minimalista.
9. Ayudar a reconocer, diagnosticar y recuperarse de errores.
10. Ayuda y documentación.

**Propias de esta app.** Son principios candidatos; confírmalos o descártalos con lo que encuentres:
- **P1 · Corregir donde se ve.** Todo dato que se muestra (cifra, ficha, línea del tiempo) se corrige tocándolo ahí mismo.
- **P2 · La fecha siempre visible y cambiable.** En toda pantalla de registro se ve a qué día lógico va lo que anotas y se puede cambiar a ayer u otra fecha en un toque.
- **P3 · Ayer a un toque.** Rellenar lo olvidado de ayer, o de los días sin abrir la app, sin recorrer pantallas.
- **P4 · Todo borrado se deshace.** Revisa que `Deshacer` cubra todos los borrados y que las ediciones importantes también se puedan revertir.
- **P5 · Lo de todos los días, en el primer alto de pantalla.** Lo de cada semana, a un toque. Lo raro, en Más o en el buscador.
- **P6 · Un solo nombre para cada cosa.** El mismo término en pestaña, título, buscador y Más.
- **P7 · Los gestos ocultos tienen alternativa visible.** Todo toque largo o deslizamiento tiene otro camino visible.
- **P8 · Lo automático se puede corregir o ignorar** (sueño, lugares, siestas, pantalla).
- **P9 · El día lógico se explica cuando importa.** Antes de las 05:00, avisar en qué día va lo que se anota.
- **P10 · Alcance con una mano.** Las acciones frecuentes, en la mitad de abajo.
- **Accesibilidad:**
  - objetivos táctiles de al menos 48 dp;
  - `contentDescription` en íconos;
  - contraste suficiente en los temas;
  - texto que no dependa solo del color.

---

## 8. Entregable

Un archivo **`docs/navegacion/revision_navegacion.md`**, en lenguaje claro y con esta estructura:

1. **Resumen.** Las 10 fricciones más graves y los principios propuestos.
2. **Qué no se pudo comprobar** sin teléfono, como preguntas para que el autor lo pruebe.
3. **Mapa actual**, de la Fase 1.
4. **Matriz de datos × operaciones**, de la Fase 2.
5. **Escenarios.** Uno por bloque, con este formato:
   ```
   ### E-10 · Corregir el sueño que el reloj registró mal
   Objetivo: …
   Ruta actual: Hoy → (toca la cifra de sueño: ¿pasa algo?) → … (N toques; paso 2 escondido)
   Deshacer: sí/no
   Fricción: …
   Gravedad: alta / media / baja
   Ver propuesta: N-03
   ```
6. **Inventario de saturación**, de la Fase 4, con sus mediciones.
7. **Violaciones de heurísticas**, de la Fase 5, con la pantalla y el archivo:línea.
8. **Propuestas N-01, N-02, …**, cada una con:
   - Problema: los escenarios E-xx y heurísticas que resuelve.
   - Cambio: qué se hace, en palabras simples.
   - Dónde: los archivos y funciones.
   - Riesgo:
     - lo privado;
     - las rutas usadas por widgets, notificaciones o accesos directos;
     - la costumbre del autor;
     - cualquier riesgo para los datos.
   - Esfuerzo: chico / medio / grande.
   - Si toca el paper (§ 9).
   - Si cambia lo que se guarda: casi nunca debería. Si pasa, dilo, porque obliga a subir `SCHEMA_V`.
   - Cómo se comprueba: qué captura o prueba.
9. **Mapa propuesto**: pestañas, Hoy, Más, y dónde queda cada pantalla movida.
10. **Entregas sugeridas.** Grupos pequeños en orden de impacto, cada uno compilable y revisable por separado, por ejemplo N1 corregir, N2 ayer y fechas, N3 Hoy menos saturado, N4 Más y buscador.
11. **Preguntas para el autor**, todo lo que sea gusto personal o costumbre.
12. **Bocetos**, si los hay.

Si se hizo la Fase 6, sube también las capturas de escenario a la rama del chat, sin datos reales.

**Al terminar:**
1. Haz commit en la rama asignada.
2. Empuja la rama.
3. Dale al autor un resumen corto: las fricciones principales, cuántas propuestas hay y qué tiene que decidir.
4. **Para ahí.** No implementes nada hasta que el autor apruebe las propuestas.

---

## 9. Relación con el paper

Según `CLAUDE.md`, el acomodo de pantallas y los textos de la interfaz **no obligan a tocar el paper**. Pero el paper menciona rutas como «Más → Privado» o «el diario de la noche», sobre todo en el capítulo 3 (cómo se registra) y en los apéndices.

En cada propuesta que mueva **dónde se registra algo**:
- busca en `docs/paper/` las menciones con `grep -rn "Más →\|pantalla\|diario de la noche" docs/paper`;
- anota qué sección habría que actualizar cuando se implemente.

Si la propuesta cambia la probabilidad de que algo se registre, también es un efecto metodológico. Por ejemplo, mover un campo fuera de la noche hace que se anote menos, como pasó con el impulso (capítulo 3, sección 3.16). Esos casos van además a `docs/paper/hallazgos.md`, con el formato del módulo.

---

## 10. Lista de comprobación antes de entregar

- [ ] Las ~75 rutas aparecen en el mapa, sin faltar ninguna.
- [ ] Cada tipo de dato del § 4, más los de la 10b, tiene su fila en la matriz.
- [ ] Están E-01 a E-35, cada uno con ruta, toques y gravedad.
- [ ] Los tres ejemplos del autor (E-10, E-11 y E-12) tienen propuesta concreta.
- [ ] Están revisadas las entradas desde fuera de la app: widgets, notificaciones, registro rápido y toques largos.
- [ ] El inventario de saturación tiene mediciones, no solo opiniones.
- [ ] Ninguna propuesta saca lo privado de su pantalla.
- [ ] Ninguna propuesta rompe rutas usadas por notificaciones o widgets sin decir cómo se migran.
- [ ] Ningún dato real en el informe ni en las capturas.
- [ ] Lo no comprobado está dicho como no comprobado.
- [ ] Las referencias están comprobadas o marcadas «[sin verificar]».
- [ ] Commit y push en la rama asignada, sin tocar `main`.

---

## 11. Mensaje para el chat nuevo

Copia esto en el chat nuevo:

> Lee `CLAUDE.md` y después `docs/navegacion/PLAN_REVISION_NAVEGACION.md`, y haz esa revisión completa siguiendo el plan al pie de la letra. Primero comprueba si la entrega 10b ya está en `main`, como dice el § 1. No cambies código de la app: solo puedes agregar capturas de prueba con datos inventados (Fase 6). Entrega `docs/navegacion/revision_navegacion.md` y para ahí, sin implementar nada hasta que yo apruebe las propuestas.
