# Tanda P4a — El motor de escena, la biblioteca de paisaje y el Pireo de Demeas

Fecha: 11 nov 2026. Rama: `claude/gallant-goldberg-bsc3pm`, encima de P3b (`claude/modest-maxwell-zkj0mm`). Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

El dibujo del cuadro que irá en Hoy: el paisaje del personaje de cada tema, pintado con código. Esta tanda deja el motor, la biblioteca de piezas que compartirán los 32 paisajes y el primero de ellos, el de Helénico.

**En la app todavía no se ve nada:** el cuadro entra en Hoy en la tanda P6. Por ahora se ve en las capturas.

| Archivo | Qué tiene |
|---|---|
| `ui/escenas/EscenaVida.kt` | El motor: el momento que pinta una escena, la interfaz de las escenas, el lienzo, y qué tema tiene escena (`escenaDe`) |
| `ui/escenas/VistaEscena.kt` | El cuadro: junta la parte quieta, la parte viva y la banderita |
| `ui/escenas/paisaje/` (8 archivos) | La biblioteca de paisaje: luz, cielo, tierra, agua, casas, fuego, tiempo y adornos |
| `ui/escenas/EscenaHeleno.kt` | El paisaje de Helénico: el puerto de Zea desde el patio de Demeas |
| `ui/escenas/Banderitas.kt` | La banderita «Hoy en la historia» de cada tema (`banderitaHistoria`) y la de Helénico |
| `ui/escenas/EscenaPlana.kt` | Un paisaje hecho solo con piezas de la biblioteca: es su muestra y la plantilla de las tandas S |
| `app/src/test/…/Capturas.kt`, `CapturasAPedido.kt` | La superficie `cuadro`, el filtro por tema y la corrida «solo» |
| `.github/workflows/capturas.yml` | Dos entradas nuevas: `temas` y `solo` |
| `app/src/test/…/EscenasPrueba.kt`, `CapturasAPedidoPrueba.kt` | 13 pruebas |
| `app/src/test/…/CoberturaTemas.kt`, `docs/diseno/TEMAS.md` | El cuadro de cada tema entra a la cuenta de pendientes: faltan 31 |
| `docs/diseno/ESCENAS.md`, `docs/diseno/escenas/referencia_heleno.jpg` | La guía de arte de las escenas y la imagen del cuadro que sirve de vara |

- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.** Todo sale de la fecha, la hora y el personaje.

## El cuadro de Helénico

**Qué se ve.** El puerto de Zea, en el Pireo, desde el patio de la casa de Demeas. **[Aproximación]**: es una composición con lo que había allí, no la vista desde un punto real.

- **Al fondo:** el golfo con Salamina a la izquierda; a la derecha, el llano del Ática.
  - La costa tiene calas, puntas y peñas, y una franja de arena y roca entre la hierba y el agua: lo que el mar le ha ido comiendo.
  - Detrás, los campos, un olivar y los Muros Largos que suben a la ciudad.
  - Atenas trepa por una loma en tres hileras de casas, con su muralla delante, hasta el pie de la Acrópolis con el Partenón. Detrás quedan el Licabeto y los montes.
- **En medio:** la dársena. En la otra orilla, la fila de cobertizos de la flota y la torre de la bocana. En esta orilla, a la izquierda, el cobertizo del «Aura», la nave de Demeas.
- **Delante:** el patio, visto un poco desde arriba.
  - El murete encalado, gastado, con un vano a la izquierda por donde se baja a la playa.
  - Las cosas de la casa, grandes y arrimadas unas a otras: el candelero, dos ánforas, la tinaja, el brasero, el banco de piedra con el objeto del día, un canasto y un rollo de cuerda colgado.
  - A la derecha crece un olivo. Su tronco cruza el cuadro de abajo arriba y su copa lo enmarca.
- **No hay personas.** Se ve lo que dejan: el fuego, la olla, las naves, las antorchas.

**Qué cuenta, igual que el cuento** (decisión 18). Todo sale de la ficha del día:

| Lo que dice la ficha | Lo que se ve |
|---|---|
| Arco `nave` en 0 (de noviembre al 12 de abril) | El «Aura» dentro de su cobertizo, sobre los puntales. A sus horas de trabajo, la escalera y el caldero de la brea con su humo negro |
| Arco `nave` en 1 | El «Aura» en el agua: amarrada, o remando en la dársena a las horas en que él trabaja |
| Estado `mar` | **Su nave no está.** El cobertizo, vacío; el brasero, apagado |
| Arco `flota` | Cuántas naves hay en la dársena (de 0 a 7). En invierno, ninguna: asoma la proa de cada una en su cobertizo |
| Claves `partida` y `regreso` | La fila de naves que sale por la bocana hacia mar abierto, o que entra |
| Arco `campo`, clave `cosecha` | La cebada brota, verdea, dora y queda en rastrojo; en la siega, las gavillas |
| Arco `estragos` | Campos del color de la ceniza, con rodales negros, y las dos casas de labor en ruinas |
| Clave `humo` | Tres columnas de humo negro en el llano; de noche, su resplandor |
| Arco `refugiados` | Las chozas arrimadas a los Muros Largos (de 0 a 11) |
| Arco `peste`, clave `piras` | Humos delgados junto a la ciudad, con su punto de fuego |
| Clave `fiesta` | Guirnaldas en el murete, en el cobertizo y en los cobertizos de enfrente; una corona en la popa del «Aura»; cintas en las demás naves. De noche, antorchas en el muelle |
| Clave `antorchas` | Las antorchas del muelle, con su reflejo en el agua |
| Clave `luto` | Un paño oscuro sobre el murete y un lécito blanco en el banco |
| Clave `eclipse` | De las 13:12 a las 16:12 el sol es una media luna, el día se apaga y asoman estrellas |
| Arco `higuera` | El alcorque vacío hasta que la planta; después crece cada año, se pela en invierno y da higos a finales del verano |
| El objeto del día | El mismo del que habla el cuento. Son 12, cada uno con su dibujo: nueve van sobre el banco; el remo, apoyado en el murete; la red y el manto, colgados de él |
| El tramo de «Un día suyo» | Comer: el brasero encendido con la olla. Con gente: la lucerna del candelero, que alumbra el muro, la copa y la jarra. Dormir: brasas |
| La estación | El color del llano (verde en invierno, pajizo en verano) y las hojas de la higuera |
| El tiempo | Nubes, lluvia, tormenta con relámpagos, viento con el mar rizado |

**Cómo está pintado.** El autor vio la primera versión y pidió rehacer el aspecto: de día parecía una ilustración de catálogo. Escogió entre tres variantes dibujadas en el arnés (gouache, acuarela y estampa) y se quedó con la primera. **[Diseño propio]**

| Lo que fallaba | Cómo quedó |
|---|---|
| La luz no tenía dirección | Viene del lado del sol. Todo arroja sombra: larga por la mañana y por la tarde, corta a mediodía. Las vasijas, el banco, el tronco, los montes y las casas tienen un lado con luz y otro en sombra. La copa del olivo da sombra rota, con claros, sobre el murete, el suelo y las cosas |
| El cielo estaba vacío | Con el cielo despejado, cada día trae una de cuatro formaciones de nubes. Por arriba enmarca la copa del olivo |
| Los colores eran «de fábrica» | Paleta corta: mar turquesa, cielo que baja a crema en el horizonte, luz blanca y cálida, sombras azul violeta |
| El agua era plana | Honda lejos y clara junto a la orilla, con espuma. Refleja las naves, los cobertizos, la torre y la costa. Lleva la calle de luz del sol bajo o de la luna |
| El primer plano era una repisa | Cosas más grandes, que se enciman. El murete tiene desconchones, chorreras, grietas y el zócalo gastado; el suelo es de losas |
| Tres franjas sin nada que las cruce | El tronco del olivo, el pilar del vano, el remo en diagonal y la tinaja las cruzan |
| Todo era liso | Un grano muy fino sobre toda la parte quieta |

- **Por qué una prueba puede vigilarlo:** dónde está la nave no se decide dentro del dibujo, sino en una función aparte (`EscenaHeleno.nave`), que dice «cobertizo», «amarrada», «remando» o «ausente». La prueba la compara con el calendario todos los días de 2026 a 2032.

## Cómo quedó el motor

**El momento** (`Momento`). Es lo único que una escena puede leer: la ficha del día y la hora. De ahí saca la franja de luz, qué hace el personaje a esa hora, las claves de escena, los arcos y un azar propio del día.
- Lee **todas** las claves de escena de hoy, no solo la del día que manda. El 5 de julio de la temporada 1 coinciden la partida y la invasión, y se ven las dos.

**Dos partes** (`EscenaVida`):
- **La quieta:** cielo, lejanía, plano medio, primer plano, arcos y adornos. Se traza una vez y se guarda como imagen.
  - Se vuelve a trazar cada **veinte minutos** (para que la luz avance), cuando el personaje pasa a otro tramo de su día, al cambiar de día o de tamaño.
- **La viva:** lo que se mueve. Gotas, copos, relámpagos, humo, llamas y los destellos del agua. Se traza encima, unas **15 veces por segundo**.
  - Con las animaciones apagadas, con ahorro de energía o en las capturas, se traza una sola vez y queda quieta: el humo y las llamas se ven, pero no se mueven.
  - El reloj se detiene solo mientras la app no está en pantalla.

**El lienzo.** Todas las escenas se dibujan en 380 × 285 unidades (una unidad es un dp en un teléfono de 412 dp). En un teléfono más angosto todo se encoge a la vez, así que no hay que revisar cada tamaño por separado.

**Si una escena falla al dibujar,** el cuadro se queda con lo que llevaba pintado y avisa, en vez de cerrar la app. Un adorno no puede tumbar la pantalla de Hoy.

**Las anclas del gato.** Cada escena dice dónde puede estar el gato en cada momento: el suelo por donde camina, un sitio alto para mirar, el fuego (solo si está encendido), el objeto del día, un sitio a cubierto y un adorno. Las usará la tanda P5.

## La luz

- **No es un velo encima del dibujo.** Cada cosa pide su color a `Luces.tono`, que hace dos cosas:
  - lo tiñe con la luz de la hora y del tiempo que hace;
  - lo desvanece hacia la bruma cuanto más lejos está.
- Lo que da luz propia (una llama, una ventana encendida) se pinta tal cual: por eso de noche resalta.
- **El cielo despejado** sale de 12 instantes del día; entre uno y otro los colores cambian poco a poco.
- **La paleta del día es corta y va junta.** No hay azul puro: así el cielo y el agua se llevan con los ocres de la tierra.

| Hora | Cómo es |
|---|---|
| 5:00 | Primera claridad, horizonte anaranjado |
| 6:24 | Amanecer cálido; el sol acaba de salir por la derecha |
| 7:00 | Mañana: bruma cálida que lo junta todo |
| 8:00 a 17:30 | Día: azul verdoso apagado arriba, crema en el horizonte; la luz más blanca a las 14:00 |
| 19:18 | Tarde dorada; el sol baja por la izquierda, sobre el mar |
| 20:18 | Atardecer rosa y naranja |
| 21:00 | Anochecer: todavía queda un resplandor; ya hay que encender |
| 22:30 a 1:00 | Noche cerrada, con luna y estrellas |

- **El sol** sale a las 5:36 y se pone a las 20:24 todo el año, cruza el cuadro de derecha a izquierda y a mediodía queda por encima del cuadro. **[Aproximación]**: no cambia con la estación ni con el lugar.
- **Las sombras** (`Rayo`, en `Sombras.kt`) salen de esa misma luz. **[Aproximación]**: el sol se dibuja dentro del cuadro, pero la luz entra de lado, del lado en que está el sol, y un poco desde delante. Con el sol de frente todo quedaría a contraluz.
  - Caen hacia el otro lado y hacia el fondo; si alcanzan el murete, suben por él.
  - Son azul violeta y oscurecen lo que tienen debajo una sola vez, aunque se encimen.
  - De noche, con el cielo cubierto o en un eclipse, no hay.
- **El tiempo que hace** agrisa el cielo y apaga la luz: nublado, niebla, nieve, lluvia y tormenta, de menos a más. Con el cielo cubierto no se ven el sol ni las estrellas.
- **Los interiores** (Refugio, Laboratorio, Espacio) no tienen cielo: solo luz encendida o tenue.
- Las tres horas de las capturas (7, 13 y 21 h) dan tres luces distintas.

## La biblioteca de paisaje

| Archivo | Piezas |
|---|---|
| `Luces.kt` | La luz de cada hora y de cada tiempo; el eclipse |
| `Sombras.kt` | De dónde viene la luz (`Rayo`); la capa de sombras; la sombra que arroja una cosa en el suelo y en la pared de detrás; la sombra rota de un follaje; el lado de luz y el de sombra de un cuerpo redondo |
| `Cielo.kt` | El cielo con sus pinceladas y su calima, las estrellas, la luna, el sol (y su media luna en un eclipse), nubes de algodón en pisos, las cuatro formaciones del cielo despejado, capas de nube baja, velos e hilos de nube alta |
| `Tierra.kt` | Montes e islas con sus laderas de luz y de sombra; el color de la hierba y de las hojas en cada estación; sembrados por etapa, quemados o con gavillas; árbol de hoja caduca, olivo, ciprés, pino, matas y briznas |
| `Agua.kt` | El mar o un río: hondo lejos, claro cerca, con rachas, calmas, rizos y crestas; la estela del sol o de la luna; el reflejo roto de lo que está sobre el agua; el agua clara de una orilla; los destellos que se mueven y el reflejo de una luz |
| `Casas.kt` | Una casa con cinco techos (teja, paja, azotea, pizarra y madera), encendida o no, y sus daños hasta la ruina; una muralla |
| `Fuego.kt` | Llama, resplandor, brasas, humo (blanco o negro, que el viento tumba) y antorcha |
| `Tiempo.kt` | Lluvia, nieve, relámpago, ráfagas y polvo; los velos de la niebla y de la lluvia |
| `Adornos.kt` | Guirnalda, banderines o cintas, corona y paño (de luto o de fiesta) |
| `Textura.kt` | El grano del papel |

- **Lo que el paisaje de Helénico no usa** (la nieve, la niebla, el polvo, cuatro de los cinco techos, los pinos, los banderines) se ve en la **escena llana**, que tiene sus dos capturas (`…_cuadroBiblioteca_biblioteca_1.png` y `_2.png`).
- **Lo que falta para otras escenas** lo agrega la tanda que lo necesite: los interiores, la ciudad de noche de Ciudad neón, la catedral.

## La banderita

- Cada tema tiene la suya (`banderitaHistoria`), siempre junto con su escena.
- **La de Helénico:** una cinta de estela púrpura que cuelga de una varilla de bronce, con greca dorada y el sol de Vergina. Mide 26 × 40 dp y va dentro del cuadro, arriba a la izquierda.
- Ya sabe llenarse: al mantener el dedo se tiñe de oro desde abajo. El gesto es de P6.
- Sus colores son fijos: no cambian con la hora de la escena, para que siempre se distinga del paisaje.

## Capturas: el filtro por tema y la corrida «solo»

`capturas.yml` tiene ahora tres entradas:

| Entrada | Qué hace |
|---|---|
| `superficies` | Como antes: qué capturas por tema se quieren (`cuadro`, `cuento`…) |
| `temas` | De esas, solo los temas que se digan (`heleno`, o `gotico,neon`). Vacía: todos. Un id mal escrito hace fallar la prueba, para que no salga una corrida vacía que parezca buena |
| `solo` | Se saltan las capturas de siempre y corren solo las pedidas |

- **Por qué hacía falta `solo`.** El filtro por tema no acorta la corrida por sí solo: las más de 500 capturas de siempre corren igual. Con `solo`, se saltan antes de que Paparazzi prepare nada.
- **El costo de `solo`:** la rama `capturas` queda solo con las imágenes pedidas hasta la siguiente corrida completa (cualquier cambio de código que llegue a `main` la rehace).
- **Las pruebas que no son capturas siguen corriendo siempre:** las de `vidas/`, las de escenas, la cobertura y los despachos.
- **Cómo se reconoce una captura a pedido:** por su nombre. Las de una superficie en todos los temas se llaman `tema<Superficie>`; las demás están en una lista corta. Una prueba lee `Capturas.kt` y falla si el nombre de alguna no dice la verdad.

**La superficie `cuadro`** saca, para cada tema con escena, una imagen por día de muestra con el cuadro a tres horas. Los días de Helénico:

| Imagen | Día | Qué enseña |
|---|---|---|
| `normal` | 5 may 2027 | Primavera: el «Aura» en el agua y la cebada dorada |
| `fiesta` | 12 may 2027 | Las Targelias: guirnaldas y, de noche, antorchas |
| `guerra` | 25 jun 2027 | La primera invasión: humo en el llano y chozas junto al muro |
| `zarpa` | 5 jul 2027 | **Zarpa la flota: su nave ya no está** |
| `invierno` | 3 feb 2027 | Lluvia, y el «Aura» en su cobertizo |
| `eclipse` | 3 ago 2027 | A las 13:00, a las 14:42 y a las 16:30 |
| `regreso` | 18 sep 2027 | La flota vuelve |
| `luto` | 22 nov 2027 | El entierro de los primeros muertos |
| `siega` | 20 may 2027 | Las gavillas |
| `peste` | 16 ago 2028 | Campos arrasados y piras, en plenas Panateneas |
| `antorchas` | 1 jun 2029 | Las Bendidias, a las 13:00, a las 20:30 y a las 22:30 |
| `higuera` | 1 sep 2031 | La higuera, cuatro años después, con sus higos |

## Lo que decidí y no estaba en el plan

1. **La luz va en el color de cada cosa, no en una capa encima.** El plan la ponía como la capa 4. Así lo lejano se desvanece de verdad y lo encendido resalta de noche. **[Diseño propio]**
2. **El cuadro quieto se rehace cada veinte minutos,** no solo al cambiar de franja. Así la luz avanza sin brincos (una prueba lo mide) y el costo es un trazado cada veinte minutos.
3. **También se rehace cuando el personaje cambia de tramo:** el brasero se enciende a la hora en que come, aunque la franja de luz sea la misma.
4. **En el eclipse el sol se dibuja dentro del cuadro.** A esa hora estaría por encima, y sin verlo no se entendería por qué se apaga el día. **[Aproximación]**
5. **A sus horas de trabajo el «Aura» rema en la dársena** (en temporada) o la calafatean en el cobertizo (en invierno). El plan pedía que el tramo se notara en el entorno; estas dos formas son mías. **[Diseño propio]**
6. **Una fiesta, de noche, enciende antorchas** aunque su clave sea solo `fiesta`.
7. **El agua siempre brilla,** así que el cuadro de Helénico siempre pide cuadros de animación (mientras las animaciones estén encendidas). Es una capa ligera, pero hay que medirla en el teléfono.
8. **La escena llana** no es de ningún tema. El plan pedía «una versión plana» sin decir para qué; quedó como muestra de la biblioteca y plantilla.
9. **La entrada `solo`** y la protección ante fallas del dibujo, explicadas arriba.
10. **Los despachos por tema son tres,** todos exhaustivos y sin `else`: `escenaDe`, `banderitaHistoria` y `sinCuadro` (cuáles no tendrán cuadro nunca). `pelajeDe` llega con el gato.
11. **La guía de arte** (`docs/diseno/ESCENAS.md`). El autor pidió que lo decidido al repintar el cuadro quede escrito para los temas que siguen, con el mismo nivel y adaptado a cada tema. Tiene las diez reglas, los errores ya cometidos, cómo se adapta cada regla y una lista de comprobación. Quedó como decisión 20 del plan y está enlazada desde `CLAUDE.md`. Tres cosas de la guía son mías y el autor puede cambiarlas **[Diseño propio]**:
    - la primera escena de cada tipo nuevo (interior, siempre de noche, a bordo, sin agua) se le enseña antes de seguir con las demás de la tanda;
    - si una tanda no alcanza a dejar todos sus temas a ese nivel, entrega menos temas bien hechos;
    - una tanda S puede agregar piezas a la biblioteca, pero no cambiar cómo se ve una que ya existe sin volver a enseñar las escenas aprobadas.

## Para las tandas que siguen

- **P4b y las S (una escena nueva):**
  1. copia `EscenaPlana.kt` como `Escena<Tema>.kt` para tener el orden de las capas, y dibuja encima lo propio; cada color pasa por `Luces.tono`;
  2. lo que el cuento dice y se puede ver, sácalo a una función que una prueba pueda leer (como `EscenaHeleno.nave`);
  3. **sigue la guía de arte, `docs/diseno/ESCENAS.md`.** Tiene las diez reglas que salieron del repintado, cómo se adaptan a cada tema, los errores ya cometidos y la lista de comprobación. En corto:
     - `Rayo(luces)` y una `capaSombra` con la sombra de cada cosa (`sombraArrojada`) y la del árbol o el alero que enmarque (`sombraDeFollaje`);
     - `volumen` para los cuerpos redondos;
     - `agua`, `bajio`, `reflejoRoto` y `estela` si hay agua;
     - algo que cruce las franjas y enmarque por arriba;
     - cosas grandes y arrimadas en primer plano, no en fila;
     - orillas con entrantes y una franja de paso entre dos materiales; lo lejano, en varias filas;
     - `grano` al final de la parte quieta;
     - **la vara es `EscenaHeleno.kt`, no la escena llana:** la nueva se dibuja al lado de Helénico (`docs/diseno/escenas/referencia_heleno.jpg`) y no puede verse más pobre;
  4. cambia su rama en `escenaDe` y en `banderitaHistoria`;
  5. agrega sus días de muestra a `DIAS_CUADRO`, en `Capturas.kt`, con un día en que el cuento cambie lo que se ve;
  6. borra el tema de `SIN_CUADRO` en `CoberturaTemas` y pon sus celdas en **P** en `TEMAS.md`;
  7. pide las capturas con `superficies = cuadro`, `temas = <los suyos>` y `solo = true`.
- **No uses `Offset` en un `vararg`** (Kotlin no lo permite) ni construyas un `Color` con cuentas que puedan pasar de 1: la app se cerraría. Para mezclar colores, `lerp`.
- **P5 (el gato):** las anclas salen de `escena.anclas(momento)`. El gato va en una tercera capa de `VistaEscena`, encima de la viva; hay que decidir si la lluvia cae delante o detrás de él.
- **P6 (el cuadro en Hoy):** usa `VistaEscena(escena, ficha, hora, …)`.
  - `escena.vida(díaLógico)` dice de quién es el cuadro hoy (en Ciudad neón se alternan).
  - `animar` = animaciones encendidas y sin ahorro de energía.
  - `lleno` llena la banderita durante la pulsación larga.
  - `alFallar` recibe el error si una escena falla.
  - La hora hay que refrescarla al menos cada veinte minutos.

## Verificación

- **Arnés de Compose de escritorio,** en el scratchpad, sin subirlo. Compila `ui/escenas/` y `vidas/` tal como están en el repositorio y dibuja el cuadro a una imagen.
  - Vi nueve hojas de imágenes: las tres horas, lluvia de invierno, fiesta, invasión, el día en que zarpa la flota, eclipse, luto, peste, regreso, antorchas, la higuera crecida, los 12 objetos y la escena llana con nieve y con niebla.
  - Al repintar el cuadro volví a ver las mismas hojas con el aspecto nuevo, más las tres variantes entre las que escogió el autor.
  - A 300, 380 y 420 dp de ancho. El plan pedía 300, 360 y 420; como el dibujo se escala entero con el ancho, 360 queda cubierto.
- **Barrido:** dibujé todos los días de 2026 a 2032, cinco horas cada día, más todos los tiempos y estaciones con las dos escenas y todos los objetos: **17,158 dibujos, 0 fallas.** Lo repetí con el cuadro repintado, con el mismo resultado.
  - Antes de arreglarlo encontró un error de verdad: con nieve, un color se pasaba de 1 y la app se habría cerrado. Ahora hay una prueba que recorre todas las horas, tiempos y eclipses.
- **Pruebas** (en el arnés, y en Actions con las capturas): **13**.
  - cada escena tiene su banderita, su personaje y su calendario; solo Terminal, Navidad y Básico se quedan sin cuadro;
  - la luz es válida a todas horas con todo tiempo, no da saltos, y de noche enciende las luces;
  - el sol cruza el cuadro y en el eclipse se ve;
  - el cuadro quieto solo cambia cada veinte minutos o al cambiar de tramo;
  - **la nave de Demeas está donde dice el cuento** en cada fecha clave, y en ningún día de 2026 a 2032 el cuadro contradice a la ficha;
  - las anclas del gato caben en el cuadro;
  - el filtro de temas, la corrida «solo» y los nombres de las capturas a pedido.
- **Compilación de Android y capturas:** ver «Resultado en Actions», abajo.

**Cómo rearmar el arnés de dibujo** (para P4b, P5 y las S). En este entorno el repositorio de Google está bloqueado, así que no sirve el Compose de la app; sí se resuelve, solo con Maven Central, el de escritorio 1.5.12.

```kotlin
// build.gradle.kts  (repo = app/src/main/java/io/github/ruddy2003/vestigium)
plugins { kotlin("jvm") version "2.1.0"; id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"; application }
kotlin { jvmToolchain(21) }
sourceSets { main { kotlin.setSrcDirs(listOf("$repo/vidas", "$repo/ui/escenas", "otras", "src/main/kotlin")) } }
dependencies { implementation("org.jetbrains.compose.desktop:desktop-jvm-linux-x64:1.5.12") }
```

- En `otras/` van un enlace a `data/OtrasVidas.kt` y una copia de los enums `Epoca` y `TemaId` de `ui/Tema.kt`.
- El programa dibuja con `ImageComposeScene(ancho, alto, Density(2f)) { VistaEscena(…, animar = false) }` y guarda el PNG.
- **Ojo:** es un Compose más viejo que el de la app (1.5 frente a 1.7). Lo que compila aquí compila allá, salvo `Path.quadraticTo`, que aquí no existe: en `ui/escenas/` las curvas van con `cubicTo`.

## Resultado en Actions

Las corridas que valen son las últimas, sobre el commit del cuadro repintado (`97395f3`).

- **Capturas:** la corrida 267 de `capturas.yml` (`superficies = cuadro`, `temas = heleno`, `solo = true`) **terminó bien**, sin errores de compilación ni pruebas fallidas.
  - Dejó 14 imágenes en la rama `capturas`: las 12 de Helénico (`…_temaCuadro_heleno_remero_<día>.png`) y las 2 de la biblioteca (`…_cuadroBiblioteca_biblioteca_1.png` y `_2.png`).
  - Revisé la del día normal: **se ve igual que en el arnés.** Las sombras, los reflejos y el grano salen con el motor de dibujo de Android.
  - `despachos.txt` y `unicidad.txt` salieron vacíos; `cobertura.txt` trae justo los 31 temas que faltan.
  - En esa corrida corrieron también en Actions las pruebas de `vidas/`, las 13 de esta tanda y la de «despachos en cero».
- **Compilación de Android:** la corrida 207 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, **terminó bien** (no se publicó ningún Release).
- **Cuántas corridas se usaron:** 3 de capturas, contra las 2 que preveía el plan.
  - La 265 fue la del primer cuadro. Tardó 4 minutos, contra los cerca de 40 de una corrida completa.
  - La 266 y la 267 son del cuadro repintado, que el plan no preveía. Cada una tardó unos 7 minutos.
  - Con `solo`, la rama `capturas` queda solo con estas 14 imágenes hasta la siguiente corrida completa.

## Lo que no se puede comprobar aquí

- **El rendimiento y la batería.** El cuadro traza su parte viva unas 15 veces por segundo. Hay que medirlo en la APK, cuando P6 lo ponga en Hoy.
  - La parte quieta pesa más que antes: lleva el grano (34,000 puntos) y dos capas de sombra. Solo se vuelve a trazar cada veinte minutos o al cambiar el tramo del día.
  - En el arnés, dibujar un cuadro entero pequeño (190 px de ancho) toma unos 60 ms. En el teléfono es más grande y puede notarse un tirón al abrir Hoy: hay que medirlo.
- **El movimiento.** Las capturas y el arnés muestran un instante quieto: no se ve cómo suben el humo, cómo cae la lluvia ni cómo brilla el agua.
- **Que se vea igual en el teléfono que en el arnés.** El arnés usa otro Compose y otro motor de dibujo. Las capturas de Actions sí usan los de Android.
- **El parecido histórico del puerto.** Los cobertizos, los Muros Largos, el Partenón (terminado en 432 a. C.) y los trirremes son como los describen las fuentes abiertas en P2; el lugar exacto de cada cosa en el cuadro es una composición.
