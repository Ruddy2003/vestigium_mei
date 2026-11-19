# Pendientes y forma de trabajo (17 nov 2026)

Lo que quedó sin hacer cuando se juntaron en `main` las escenas (tandas S9 a S18) y el plan de mejoras con los cinco temas nuevos, y cómo se trabajó, para poder retomarlo sin la conversación original. El detalle de cada cosa está en el documento que se cita; aquí está la lista entera en un solo sitio.

## 1. Cómo se ha trabajado

- **Reparto.** Quien dirige la sesión lee el cuento, escribe la composición de cada cuadro, mira las imágenes y escribe la revisión. **Todo el código y el dibujo lo hacen agentes con Opus** (decisión del autor: gastar lo menos posible del modelo más caro sin perder calidad). El cierre de cada tanda (mezclas, pruebas, documentos, informe, Actions) también lo hace un agente.
- **Una tanda S, paso a paso:**
  1. Un agente lee el cuento y el calendario de la vida y escribe la «lectura»: qué tiene que verse, estados, arcos, objetos, días señalados, riesgos de parecido.
  2. Quien dirige fija la composición (desde dónde se mira, qué manda, la luz, lo que no se repite de otros cuadros).
  3. Un agente por cuadro, en su propio árbol de trabajo, dibuja en el arnés de escritorio al lado de Helénico y de los cuadros aprobados más parecidos. Primero la composición sola; luego el detalle.
  4. Quien dirige mira una hoja por entrega y devuelve una revisión con puntos concretos por orden de peso. Se repite hasta que llega (dos a cuatro rondas).
  5. Aprobada la composición, dos agentes más por tema: los doce objetos del día, y la banderita con el gato.
  6. El agente del cuadro integra objetos, banderita y gato, y hace el pulido en la misma ronda.
  7. Mezcla en la rama de trabajo, y cierre: pruebas completas, limpieza sin cambiar un píxel, registro (`DIAS_CUADRO`, `TEMAS.md`, `ESCENAS.md`, la fila del plan), imágenes de referencia, barrido de 2026 a 2032, peso y capas, informe en `docs/historia/informes/S<n>.md`, y capturas y compilación en Actions.
- **Reglas de proceso que salieron caras cuando no se cumplieron:**
  - No más de tres agentes pesados a la vez en una máquina de 4 núcleos y 16 GB.
  - Los árboles de trabajo de los agentes nacen de una rama vieja: lo primero es traer la rama de trabajo.
  - **Compilar el conjunto antes de subir una mezcla:** dos temas que compilan por separado pueden no compilar juntos (pasó con dos clases del mismo nombre en `ui/escenas/`). Los nombres de primer nivel llevan el tema.
  - Archivos de menos de unas 600 líneas y funciones cortas: el compilador de CI va justo de memoria.
  - **Un agente no se queda parado esperando un trabajo en segundo plano:** el contenedor se da por inactivo y se reinicia. Lo largo se parte en trozos y se consulta en primer plano; commit y push después de cada paso.
  - Un agente que dice que le negaron un permiso no se sustituye: se le cuenta al autor.

## 2. La vara

- **Helénico es el piso, no el techo.** En cada aspecto, el nivel es el del cuadro aprobado que mejor lo hace (`docs/diseno/ESCENAS.md`, sección 2 bis).
- **La lógica de la luz:** luz con forma sobre penumbra de color; que camina con la hora; que se ve en el aire; que enciende lo pintado; y de noche, dos luces opuestas (una cálida que alumbra de verdad y una fría).
- **Lo que más se devolvió en las revisiones:**
  - la composición antes que el detalle (un punto de vista mal elegido no se arregla pintando);
  - el primer plano que es una repisa vacía, sin nada grande a la altura de los ojos;
  - los alzados de frente y las franjas paralelas;
  - planos grandes de un solo color, sin materia;
  - lo repetido como pegatina (casas, bocas de mina, almenas iguales);
  - líneas de regla en lo natural;
  - de noche, lo encendido que no alumbra;
  - prendas colgadas que parecen una figura;
  - el gato mal alumbrado por el cuadro (cabeza clara y cuerpo oscuro; sin lado cálido junto a la lumbre).
- **Contenido:** sin personas ni nada que lo parezca, sin letras ni números, sin iconografía de ninguna obra conocida; **manda el cuento, no el tema de la app**; los signos inventados se comprueban contra alfabetos reales.
- **Límites técnicos:** peso por debajo de 1.5 veces Helénico; hasta seis capas de sombra en la parte quieta y ninguna en la viva; determinista; barrido de siete años sin fallas.
- **Si no alcanza el tiempo, menos cuadros bien hechos, no más cuadros peores.**

## 3. Lo pendiente

### Máxima prioridad
- **Poner el paper al día con el esquema 28: el tipo de presencia «llamada» y la columna `personas_llamadas`** (y `vm_dias.csv:presencias_llamada`). Decisión del autor (17 nov 2026): el código salió sin tocar el paper, que se pone al día después y antes que cualquier otra cosa. Lo que hay que tocar:
  - **La fila nueva del Apéndice D, D.200** (`docs/paper/apendiceD_cambios.md`): el número está **reservado** y ya lo citan las dos entradas de `DESDE` en `data/Diccionario.kt` (`personas_llamadas` y `vm_dias.csv:presencias_llamada`). Qué decir: «llamada» (`Presencias.LLAMADA`) es un valor nuevo en `Dia.presencias`; hasta el esquema 27 las llamadas se marcaban como «mensajes»; todo lo que ya existía la cuenta como mensaje (`Presencias.aDistancia`, `comoAntes`): `personas_mensajes` sigue siendo «mensaje o llamada» y la columna `presencias` la escribe como `mensajes`; lo nuevo va aparte: `personas_llamadas` (vacía antes del primer día con una llamada marcada) y `vm_dias.csv:presencias_llamada`; `SCHEMA_V` = 28; versión de la app: la de `VERSION_ESQUEMA_28` en `Diccionario.kt`.
  - **Capítulo 3** (`cap03_estado_subjetivo.md`): la tabla de mediciones (fila «Presencias (estuve / pensé / mensajes)», hacia la línea 42, que pasa a cuatro estados) y la sección de las presencias (hacia las líneas 553-560: el toque estuve → pensé → mensaje → llamada → estuve).
  - **Capítulo 7** (`cap07_red_social.md`): la tabla de variables (fila `personas_vistas`, `personas_pensadas`, `personas_mensajes`, hacia la línea 222) con `personas_llamadas` y su condición; su versión corta (`corta/cap07_red_social.md`, hacia la línea 75).
  - **Capítulo 2** (`cap02_arquitectura.md`): la lista de variables sociales (hacia la línea 206) y el ejemplo de los estados «estuve / pensé / mensajes» (hacia la línea 263).
  - **Capítulo 6** (`cap06_estadistica.md`): la lista de las sociales que quedan vacías antes de su primer uso (hacia la línea 76), con `personas_llamadas`, que sigue su propia regla (vacía antes de la primera llamada); y las familias obvias, donde `personas_llamadas` entró en las dos de las personas (no hay familia nueva).
  - **Capítulo 11** (`cap11_discusion.md`): la fila de `per_<x>` como presencia (hacia la línea 212), con los cuatro estados.
  - **Apéndice A** (`apendiceA_variables.md`): la fila nueva de `personas_llamadas` junto a `personas_mensajes` (hacia la línea 151), la nota de la línea 525 y el recuento de variables fijas (hacia la línea 361: hay una más, `Variable("personas_llamadas", …)`, que solo aparece si hay alguna llamada).
  - **`AGREGADOS["per_"]`** (`ciencia/Dinamicas.kt`), donde el paper los enumere: lleva `personas_llamadas`.
  - **El paper ensamblado y la versión corta** (`docs/paper/ensamblado/construir.sh`), que además ya venían atrasados (sección 5).

### Escenas
- **S19, Colonos:** el único de los 34 temas de antes sin cuadro.
- **Los cuadros de los cinco temas nuevos:** ninguno tiene todavía (Frente exterior, Arcontes, Galia, Art nouveau y Psicodelia). Lo que necesita quien dibuje Psicodelia y Frente exterior está en `docs/diseno/PLAN_MEJORAS.md`.
- **El sonido de ambiente** de los cuentos de las tandas S9 a S18 (`herramientas/ambientes.py`): sin archivo, no suena nada.
- **El peso de noche de Tierras Antiguas:** 1.69 a 1.77 a tamaño grande, por encima del límite; Orden y la noche de Laboratorio, en 1.49. Mirarlo en el teléfono y decidir si se aligera.
- **Cuadros aparte que pide el calendario y hoy son una variante del cuadro principal:** el cañón del sur (la expedición de Orden), Torremuda (la travesía de Tierras Antiguas), las ruinas de arriba (Laboratorio), el cuarto de 2030 (Abismo), el hospital de Prusia y el pueblo de Borgoña (Napoleónico), la gira y la época sin club (Art déco), el sanatorio (Soviético) y los lugares en tierra de Botánico.
- **Calendarios que se repiten o vuelven atrás desde 2032** (el cuadro sigue a la ficha): Refugio, Laboratorio, Orden, Tierras Antiguas.
- **Marcas inventadas de Tierras Antiguas:** rehechas porque coincidían con runas reales; las nuevas se comprobaron de memoria, sin tabla.
- **Biblioteca `paisaje/`:** posible desplazamiento equivocado de la parte de muro en `sombraArrojada` (sin verificar; no se tocó porque cambiaría cuadros aprobados). Y la pregunta de si la perspectiva pequeña que varios cuadros repiten pasa a la biblioteca.
- **Lo flojo de cada cuadro** está dicho sin adornos al final de cada informe (`docs/historia/informes/S9.md` a `S18.md`).

### Aprobado por el autor el 17 nov 2026 («visto bueno a todo»)
Los cuadros de S9 a S18 tal como están; las acciones de los gatos cambiadas respecto del gato de pixel cuando el cuento no las tenía o rozaban una obra conocida; el segundo cuadro de Laboratorio (el centro del Claustro) y cuándo sale; la memoria del compilador.

### Del plan de mejoras (`docs/diseno/PLAN_MEJORAS.md`)
- **Probar en el teléfono:** los arreglos de avisos y el modo según la hora (parte 1), Psicodelia (el cambio de cara y la intensidad), los fondos de pantalla con las escenas (parte 6) y lo nuevo de los cuadros (parte 8).
- **Decidido el 17 nov 2026:** se rehacen las otras 36 bibliotecas, las libretas de momentos y el tablero de notas (sección 4, al final). Los demás muebles de Vida, como estaba escrito en `PLAN_MEJORAS.md`.
- **Sin hacer:** lo temático nuevo (parte 7) y los logros nuevos (parte 9).
- **Sus preguntas abiertas** sobre los Idearios.

### Otros
- **Las ramas remotas** (revisadas las 40 el 17 nov 2026): todas están ya dentro de `main` salvo tres, con un commit cada una: `ccr-c2dc1f0e-ta3g8x` (CI: borrar las APK viejas de Actions; nunca se mezcló, decidir si se quiere) y las temporales `comparacion-ci` y `temas-nuevos-ci`. No hay trabajo perdido en ramas; las ya mezcladas y las temporales se pueden borrar cuando el autor quiera.

## 4. Comentarios del autor del 17 nov 2026 (interfaz)

Una lista de comentarios del autor sobre la interfaz, de la que se hizo **solo lo sencillo**, en la rama `ccr-e50e410b-nuv2vd` (un commit por punto). Nada de esto cambia lo que se guarda, salvo «llamada» (esquema 28). Compila (APK en Actions, corrida 290, sin publicar) y tiene sus capturas a pedido (`CapturasInterfaz`, superficie `interfaz`; corrida 318 en Helénico y Medieval, con datos inventados). Falta probarlo todo en el teléfono.

### Lo que se hizo

- **Los dos botones bajo el cuadro de Hoy** (`ui/escenas/CuadroDelDia.kt`): «Hoy en la historia» mide y pesa lo mismo que el del cuento (64 dp, el mismo filete, su renglón pequeño), sin la banderita y con un reloj de arena dibujado, igual en todos los temas. El botón del cuento lleva el ícono de la pestaña Diario del tema; los 20 temas cuyo ícono de Diario es el de Momentos reciclado (`DIARIO_RECICLADO` en `ui/IconosPestana.kt`) conservan el libro abierto.
- **El cuadro de Hoy:** sin el botón de ampliar; mantener el dedo lo abre a pantalla completa (ya no abre «Hoy en la historia»: eso queda en su botón); la banderita del tema solo sale en un día especial del personaje y hace de marca del día especial (la estrellita sobre el borde ya no sale). A pantalla completa se acerca con dos dedos (1× a 4×), se arrastra acercado y dos toques vuelven a 1×; acercado, un toque ya no cierra (la ✕ y «atrás» sí). En la pantalla del cuento, la fila de vidas trae también a los personajes que sigues (dicen «sigues»).
- **«¿Quién es?»** (`ui/pantallas/Gente.kt`): «vincular con otros» despliega «¿A quién conoce?».
- **Biblioteca del diario** (`ui/pantallas/Diarios.kt`): debajo de la entrada, «Personas de este día», plegado, con las fichas de presencia de Hoy, que escriben en las presencias de ese día.
- **«Llamada», tipo de presencia nuevo (esquema 28, con el visto bueno del autor del 17 nov 2026):** la ficha «mensaje» vuelve a decir solo eso y sale «llamada» (en Hoy, en «¿Estuviste o pensaste?» y en la Biblioteca). Todo lo que ya existía la cuenta igual que un mensaje (`personas_mensajes`, la columna `presencias`), y lo nuevo va aparte: `personas_llamadas` en el panel y `presencias_llamada` en `vm_dias.csv`. El paper queda pendiente (arriba, «Máxima prioridad»).
- **La red** (`ui/pantallas/Social.kt`): tercera ficha, «solo lo que escribí»: solo las co-menciones, más gruesas cuantas más veces aparecen juntas (antes el grosor de las co-menciones no dependía de eso); las fichas de vista y de filtro van ahora en filas que se parten, para que las tres se vean.
- **La barra de la semana** (`SemanaEnCurso`, `ui/pantallas/Hoy.kt`): debajo de la fecha de Día, de lunes a domingo, con la celda del calendario y el color de la valencia del diario (sin valencia en el diario, vacío; los días que vienen, vacíos y tenues; hoy, marcado y sin color hasta tener valencia y diario) y una «V» chata que abre el calendario. Reemplaza al botón chico del calendario y a «Tus últimos días» (borrados). Su ajuste en Apariencia se llama ahora «Colores de la semana».
- **El fondo del teléfono** (`ui/FondosTelefono.kt`): el cambio automático ponía los fondos donde decía lo último que se tocó a mano en la pantalla de los fondos; con un «en bloqueo» alguna vez, cada cambio de tema ponía solo el de bloqueo. Ahora pone siempre los dos, cada uno por su lado, y si el teléfono rechaza uno queda en la Bitácora («fondo de inicio» / «fondo de bloqueo»). **Sin probar en el teléfono:** si el de inicio sigue sin cambiar, mirar la Bitácora en Estado de los datos y probar a mano «en los dos» en la pantalla de los fondos.
- **Lentitud de los temas con muebles nuevos:** el «brillo» de los objetos que se tocan era el aura de `Modifier.halo` (`ui/Escena.kt`), que en reposo trazaba cada objeto 17 veces; ahora en reposo no se dibuja en ningún tema (al tocar sale igual). En Psicodelia, la banda bajo el título, la tarjeta de la frase y los títulos de sección ya no se recomponen en cada cuadro mientras anima la intensidad.

### Lo que queda

- **La noche como botón grande** (decidido, falta hacerlo): en Hoy, dos botones grandes iguales; arriba el de la noche pasada, debajo el del día. El de la noche abre su propia pantalla con el diario de sueños arriba y la calidad del sueño debajo, y queda sellado cuando hay calidad puesta y contestado si recordó un sueño (y, si sí, llenado lo del sueño). Se quitan de Hoy la sección «Sueño» y lo de sueños de abajo. Al terminar de llenar la noche se pide un «cómo estás ahora» **nuevo** (cómo se siente al despertar): es un registro nuevo y opcional, no se mueve el del cierre del día; sube `SCHEMA_V` y va a `Diccionario.kt` (y al Apéndice D y a `vestigium.R` si se exporta). Los nombres por tema se voltean: el botón del día deja de llamarse «noche/víspera» y nombra el día; el del sueño nombra la noche. Son unos 40 temas y el autor autorizó escribirlos sin pedirle aprobación.
- **Sonido y música del cuadro** (pedido nuevo): que el sonido de ambiente suene también al abrir el cuadro a pantalla completa; una música sutil de la época, muy baja y tranquila, como si sonara en algún lugar no muy cercano de la escena; en un día especial que lo pida, música más potente y enérgica; y que el sonido de ambiente se adapte a los sucesos solo en los días especiales (los días normales suenan igual). Hoy el ambiente solo suena en la pantalla del cuento (`AmbienteCuadro`, `AmbienteDelCuadro`).
- **Retocar los primeros cuadros**, con lo que el autor ve en cada uno: Helénico (el fondo ya se ve muy por detrás de los demás; la iluminación también), Gótico (la nave de la iglesia floja; no tiene la iluminación nueva; la noche), Romano (lo mismo), Al-Ándalus (la noche muy oscura, sin luces de casas), Pirata, Distopía, Horror cósmico y Ciudad verde (sin la iluminación nueva). Cada retoque se le enseña antes de darlo por bueno. Ojo: Helénico es «el piso» de la vara (`ESCENAS.md`); si se retoca, el piso sube y se actualiza su imagen de referencia.
- **Sacar los cuadros como imagen PNG** desde la app (cualquier día, hora y estado): chico, ya existe `pintarFondosDelCuadro` (y `pintarQuieta` en `ui/escenas/VistaEscena.kt`).
- **Cuadros propios hechos a mano dentro de la app** (el fondo de una escena, objetos de otra, animales de otra): el autor lo quiere solo si es fácil. Tal como lo pide no lo es: los animales y casi todo lo de cada cuadro está pintado dentro del cuadro, no son piezas sueltas. Queda anotada una versión reducida por si la quiere: un cuadro existente de fondo y, encima, objetos del día y gatos de cualquier tema colocados a mano.
- **Rendimiento de los temas con muebles nuevos:** ver «Rendimiento (19 nov 2026)», abajo.
- **Lo que no se hizo de la lista y por qué:** el botón de ampliar se quitó solo del cuadro de Hoy; el cuadro de arriba del cuento (`CuadroDelCuento`) lo conserva, porque ahí un toque fuera del gato ya lo amplía y el pedido era sobre Hoy: decidir si se quita también. Lo demás de la lista se hizo.

### Rendimiento (19 nov 2026)

El autor, con la v292: «sigue súper bugueado y lento el de Psicodelia». Se midió en el arnés (`herramientas/arnes_muebles/medir.sh`: lo que tarda en grabarse cada dibujo, cuántas operaciones lleva y cuánto ocupa) y se revisaron las capturas de Psicodelia en sus cuatro variantes, con intensidad 0 y 1.

- **Hecho.** El brillo (`Modifier.halo`) ya no existe en ningún tema ni mueble: al tocar, el objeto se hunde un poco (una escala de su capa). El grano de película de la ciudad (`TrazosCiudad.grano`) se dibujaba punto por punto, y en Android eso es una operación por punto: el estante del diario de la ciudad llevaba ~91 000 en el arnés (con medio año de entradas, varias veces más) y los muebles de Vida ~80 000, que el teléfono volvía a pintar en cada cuadro mientras se movía el fondo vivo; ahora es una imagen de un píxel por celda, en caché (estante: 92 409 → 4 150 operaciones; Vida: 81 246 → 1 227). La trama de rayas de la tinta va en un solo trazo (tinta clara: estante 9 429 → 5 864, Vida 6 909 → 1 829, Ideario 8 788 → 3 346). La intensidad anima en 12 pasos (antes, todo Psicodelia se volvía a trazar 60 veces por segundo durante 4 a 10 s). Cachés que no llevaban la cara (la muestra del tema en Apariencia, la lámina de rascar). El papel tapiz ya no se rehace al cambiar de pantalla (todos los temas). Fallos visuales: «Trastienda» cortada, los títulos de las portadas de Obras ilegibles con intensidad alta, el título de un diálogo bajo el sol de la tinta, el sol de un aviso sobre el texto.
- **Queda, sin poder medirse aquí** (el arnés mide la CPU con Skia de escritorio, no la GPU del teléfono ni cuántos cuadros se pierden): cuánto pesa en el Pixel el fondo vivo (en todos los temas obliga a volver a pintar la pantalla entera unas 30 veces por segundo; el de la tinta de Psicodelia arma ~6 000 puntos de caminos y una capa en cada cuadro), el emblema animado, y si Diario y Vida ya van fluidos. Para saberlo: en el teléfono, Opciones de desarrollador → «Profile HWUI rendering» (en barras) con Psicodelia de día en Diario → «La discoteca del diario», desplazando, y otra vez de noche; y con el fondo vivo apagado (Ajustes) para comparar.
- **Queda, anotado:** Frente y Galia (lote 10) llevan listas de dibujo grandes en sus muebles (Frente ~24 000–29 000 operaciones en estante, Ideario y Vida; Galia ~8 000–15 000; Helénico ~1 700–5 800), por miles de círculos y trazos sueltos: se pintan en cada cuadro mientras se mueve el fondo vivo. En las piezas comunes, Galia lleva 3 703 operaciones en la mezcla de una pantalla (Helénico, 609): cada separador, 281 (el cordón y la bellota); Nouveau y Arcontes tardan 6 a 8 veces más que Helénico en grabarse. No se tocaron (otros agentes rehacen esas bibliotecas): la receta sería guardar en caché como imagen lo que no cambia (como `PiezasPsicodelia.enCache`). La salida barata sería guardar cada mueble como capa (`CompositingStrategy.Offscreen`), pero cambia cómo se mezcla con lo de atrás si el mueble usa modos de mezcla: hay que revisarlo tema por tema. El lacre de un sobre listo (`ui/Cartas.kt`) sigue latiendo con una animación continua, dentro de la escena de Diario (que es una capa: se vuelve a pintar entera en cada cuadro del lacre). Las escalas de color del ánimo de Psicodelia (`juegoTonos`, `Disenios.kt`) son las de la versión 1 (verde lima y magenta neón) en las dos caras: en la ciudad, sobre todo de noche, los días de la semana, los volúmenes del estante y la rejilla de emociones salen en magenta y lima; decidir con el autor si la ciudad lleva otras.

### Decidido por el autor el 17 nov 2026: se rehacen los demás muebles

- **Las bibliotecas:** las otras 36 se rehacen (hasta hoy era una decisión pendiente, vistos los tres pilotos de Helénico, Laboratorio y Gótico). Qué es rehacer una y lo que pide el autor: `docs/diseno/PLAN_MEJORAS.md`, «Las bibliotecas» (solo `estante()` y `volumen()` de `Estante<Tema>`, en un archivo nuevo `ui/biblioteca/MuebleEstante<Tema>.kt`).
- **Las libretas de momentos:** el objeto de la libreta cerrada sobre su soporte, en la escena de la Libreta de momentos (`ui/pantallas/LibretaMomentos.kt`). Lo dibuja cada tema en `DisenoBiblioteca.libreta()` (con `pestanaDia()` y `rotuloMes()` del índice), dentro del mismo `Estante<Tema>` de `ui/biblioteca/Biblioteca<Época>.kt`, no en `ui/vida/`.
- **Las notas:** el tablero de notas y cada nota (`DisenoVida.tableroNotas()` y `notaTablero()`, en `ui/vida/Vida<Tema>.kt`; lo monta `ui/vida/MuebleNotas.kt`).
- **Cómo:** la receta de `PLAN_MEJORAS.md`, «Cómo se rehace un mueble» (las reglas de `MUEBLES.md`, versión 2; la vara, los 34 Idearios rehechos y las tres bibliotecas piloto; un agente por mueble con su dirección de arte; quien dirige mira cada imagen en claro y en oscuro; no más de cuatro agentes a la vez; la clase de cada recipiente sale de la semilla; los casos de 0, 1 y 5 temas y una corrida de capturas en Actions). El arnés ya dibuja los muebles de Vida y el tablero de notas (`herramientas/arnes_muebles`, modo `vida`); la libreta todavía no: hay que añadirle ese modo, como se hizo con `ideario` y `biblioteca`.

## 5. Pendientes de otros documentos

Lo que quedó sin hacer o sin decidir en otros documentos del repositorio, que esta lista no recogía (revisados el 17 nov 2026, en solo lectura). Cada punto se cotejó con el código y con los documentos posteriores; lo que no se pudo comprobar lo dice. En `app/src/main/java/` no hay `TODO` ni `FIXME`: ningún trabajo pendiente se esconde en comentarios.

### Metodología y paper
- **«Con quién» y «solo por mensaje»:** el panel y el reporte de terapia cuentan solo los registros de aviso (Explorar y los reportes periódicos, todos). Además, «mensaje» sin «solo» cuenta como acompañado, al revés de lo que dice el Apéndice A.11. Falta decidir la regla. Conviene hacerlo antes de 2027, porque cambia `acompanado_pct` (H-18-1 y H-18-2, `docs/paper/hallazgos.md`; sigue igual en `Estadistica.kt` y `Terapia.kt`).
- **Fechas señaladas «solo para mí»:** su texto sale entero en los reportes que se guardan en la galería. La casilla propuesta, un campo opcional, no se hizo, aunque el autor aprobó aplicar el resto de la revisión (hallazgo 28, `docs/revision_general_2026-11.md`; `Senalada` no tiene ese campo).
- **`video_id` de YouTube sale vacío en lo importado:** para revisar la cabecera hace falta un Takeout de muestra. Si se arregla, cambia lo guardado (hallazgo 33 e, `revision_general_2026-11.md`).
- **Los nulos que se vuelven valores por omisión al leer** (`coerceInputValues`): el hallazgo dice «queda para una revisión con el teléfono» (H-1-4, `hallazgos.md`).
- **Restos de hallazgos «resueltos en parte» que todavía son trabajo:** elegir qué copia de Drive se restaura (H-8-14); el texto de las cartas aún selladas sigue abierto en el JSON de exportación (H-8-5); los reportes automáticos siguen en carpetas públicas (H-8-4). Los demás restos de los 26 hallazgos «en parte» son límites que el paper ya documenta (`hallazgos.md`).
- **El paper ensamblado y la versión corta están atrasados:** la última vez que se generaron fue el 8 nov 2026, con el esquema 21. Los capítulos y el Apéndice D ya van por el esquema 27. Hay que volver a correr `docs/paper/ensamblado/construir.sh` (comprobado por las fechas de los commits).

### Navegación y robustez
- **Pantalla de rescate:** la base dañada ya se aparta en vez de borrarse, pero falta la pantalla que la copie tal cual a una carpeta. Hoy «Estado de los datos» solo avisa (hallazgo 4, `revision_general_2026-11.md`).
- **Contorno de control de 3:1** en todo lo que se toca, en todos los esquemas, con una prueba automática de contraste. Los esquemas siguen usando el mismo color en `outline` y en `outlineVariant`, y la prueba no existe (hallazgo 12).
- **Caché de todo el panel** según la versión de los datos: solo `kmPorDia` la tiene (hallazgo 22).
- **«No estuve aquí» no se recuerda:** si después se agranda el radio del lugar, la visita quitada puede volver (N-05 en `docs/navegacion/revision_navegacion.md`; H-NAV-4).
- **Lo que quedó fuera de N-27 y N-29:** el número del ánimo al tocar la capa del Calendario; la descripción para TalkBack en los unos 31 botones de un solo signo; y la oferta de pasar los gastos viejos a una subcategoría («Diferencias con lo propuesto», `revision_navegacion.md`; sin comprobar si se hizo después).

### Diseño y temas
- **Helénico:** una rama del olivo tapa media luna a las 21:00 (`docs/diseno/ESCENAS.md`, regla 2; sin comprobar si sigue así). Es el cuadro aprobado: si se toca, se le enseña al autor.
- **Los días de uso que nadie anota todavía** (respaldo verificado, ensayo, exportación, widget, fondo con escena, tema del día). No cuentan hacia atrás: cada día sin anotarlos es un día que sus logros no verán. Falta que el autor diga si se conectan ya, antes de hacer los logros (`docs/diseno/CUADROS_PLAN.md`, pregunta 3).
- **Lo que quedó sin verificar en las fuentes de los cinco temas nuevos:** está en la sección «Lo que queda sin verificar» de cada `docs/diseno/temas/fuentes_*.md` y en `PLAN_TEMAS.md`, 10h. Entre otras cosas, si los nombres de las salas, la radio y el festival de Psicodelia son marcas registradas.

### Cuadros e historias
- **C1, el cierre de las escenas:** `CoberturaTemas` sin pendientes, `TEMAS.md` completo, el informe, la lista para probar en el teléfono y una corrida de capturas (`docs/plan_historia_y_escenas.md`, sección 8). Al hacerlo, poner al día lo que sigue diciendo «falta que el autor los vea» en cuadros ya aprobados (las filas S9 a S11 del plan; Prehistoria, Art déco, Abismo y los exteriores de Napoleónico en `ESCENAS.md`) y la tabla de lotes de `TEMAS.md`, donde el lote 10 aparece «sin programar».
- **Antes del 1 de enero de 2028, la temporada 2:**
  - ocho tandas T con los párrafos especiales de cada personaje;
  - las S chicas con los lugares que estrena: Samarcanda (unos 150 días de las temporadas 2 y 4; hoy se ve el campamento a las puertas), el hospital y la pampa.
  Lo mismo cada año hasta 2031 (`plan_historia_y_escenas.md`, Y2028 a Y2031; `docs/historia/informes/S7.md`).
- **Nieve en el trópico en Botánico:** el viaje tiene una sola tabla de clima, así que algunos días nieva cerca del ecuador (por ejemplo, en julio de 2031). El cuento y el cuadro lo siguen. Falta que el autor decida si se corrige el calendario (`S13.md`, punto 8).
- **Probar en el teléfono lo de las tandas anteriores:**
  - que ya no hay tirón al abrir Hoy (P6);
  - los gestos y la vibración del gato (P5b);
  - «Hoy en la historia» (H1 a H3);
  - el costo de dibujo de los muebles de Vida y Diario y del aura encendida (`docs/diseno/informes/E6_*.md`).
  Cada informe trae su lista; sin comprobar si ya se probó.

### Código
- **Restos de limpieza** (hallazgo 34, `revision_general_2026-11.md`):
  - el formato del dinero sigue copiado en cuatro sitios, con y sin centavos (`Gastos.kt`, `Reportes.kt`, `Totales.kt` y `Widgets.kt`);
  - el halo privado y el formato de horas, sin comprobar;
  - `PiezaLogro` (`ui/pantallas/Hall.kt`) ya no lo usa nadie, y `docs/diseno/informes/RESUMEN.md` pedía decidir si se borra.
  Los ajustes muertos de ese hallazgo ya están resueltos.
