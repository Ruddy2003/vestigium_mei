# Tanda P4b — Los cuadros de Gótico y de Ciudad neón

Fecha: 11 nov 2026. Rama: `claude/gallant-goldberg-bsc3pm`, encima de P4a (va en el mismo PR, porque el de P4a seguía abierto). Plan: `docs/plan_historia_y_escenas.md`. Guía de arte: `docs/diseno/ESCENAS.md`.

## Qué trae

Los otros dos cuadros del piloto, hechos con la guía de arte y con el de Helénico como vara.

**El autor los vio junto al de Helénico y aprobó los dos el 12 nov 2026.** Sus imágenes quedaron como referencia en `docs/diseno/escenas/` (`referencia_gotico.jpg` y `referencia_neon.jpg`).

| Archivo | Qué tiene |
|---|---|
| `ui/escenas/EscenaGotico.kt` | La obra de la catedral de Amiens desde el patio de la logia de Pierre |
| `ui/escenas/EscenaNeon.kt`, `EscenaNeonCosas.kt` | El callejón del nivel −3, con el puesto de Amaru; las cosas del primer plano, la bici de Kei y los 24 objetos |
| `ui/escenas/BanderitaGotico.kt`, `BanderitaNeon.kt` | Un gonfalón azul con rosetón dorado; una caja de luz con un reloj de arena de neón |
| `ui/escenas/paisaje/Focos.kt` | Biblioteca nueva: luces que no son el sol (letreros, faroles, lámparas) |
| `ui/escenas/paisaje/Ciudad.kt` | Biblioteca nueva: torres, anuncios sin texto, pasarelas, tuberías y cables |
| `ui/escenas/paisaje/Cosas.kt`, `Sombras.kt` | `Cosa` sale de Helénico a la biblioteca; `rayoFijo`, la luz de un farol o una lámpara |
| `app/src/test/…/EscenaGoticoPrueba.kt`, `EscenaNeonPrueba.kt` | 16 pruebas nuevas (7 y 9) |
| `Capturas.kt`, `CoberturaTemas.kt`, `docs/diseno/TEMAS.md` | Los días de muestra de Pierre, Amaru y Kei; faltan 29 temas |

- **En la app todavía no se ve nada:** el cuadro entra en Hoy en la tanda P6.
- **No cambia nada de lo que se guarda** ni lee ningún dato tuyo.
- **El cuadro de Helénico sale idéntico** después de mover `Cosa` y de cambiar `Rayo`: comparé las dos imágenes punto por punto.

## El cuadro de Gótico

**Qué se ve.** La obra desde el patio de la logia de los canteros. **[Aproximación]**: es una composición, no una reconstrucción; el hastial con el arco cerrado con tablas y la vista hacia el barrio bajo son míos.

- **A la izquierda:** la catedral. La nave ya cubierta, con sus arbotantes; el hastial con el gran arco cerrado con tablas; delante, la cabecera a medio subir, con sus andamios y la grúa de rueda.
- **A la derecha, cuesta abajo:** las casas de entramado en tres hileras, la atalaya de la comuna, el Somme con su puente y su molino, la muralla, los campos de trigo y de waide y un molino de viento.
- **Delante:** el patio. La pila de sillares con marcas de cantero, un tonel, un cubo, la artesa del mortero, el hogar con su caldero de cobre, el capitel que talla bajo una cabria, el banco con el objeto del día y medio muro de zarzo.
- **El marco:** el poste y el alero de paja de la logia.

**Qué cuenta, igual que el cuento:**

| Lo que dice la ficha | Lo que se ve |
|---|---|
| Arco `coro` | La altura de la cabecera |
| Arco `andamios` | Cuántos cuerpos de andamio hay (de 0 a 5). Por debajo de 0.3, la obra tapada con paja y la grúa recogida |
| Clave `obra_parada` | Sin andamios y los muros tapados, desde el 3 de septiembre de la temporada 1 |
| Estado `herido` | El andamio caído y su piedra tapada con un saco |
| Arco `agua`, clave `inundacion` | El Somme turbio y fuera de cauce |
| Arcos `campo` y `guede` | El trigo y la waide, como vayan |
| Claves `fiesta`, `procesion`, `hogueras`, `luto`, `campanas`, `eclipse` | Guirnaldas y banderines; pendones y colgaduras; la leña de día y las hogueras de noche; un paño oscuro y velas; pájaros que salen de la atalaya; el sol tapado |
| El tramo del día | La grúa cargada a sus horas de trabajo; el fuego del hogar al comer o con gente; el farol del poste de noche |
| El objeto del día | Los 12, cada uno con su dibujo, en el banco |

Una prueba compara con el calendario, todos los días de 2026 a 2032, el estado de la obra (`EscenaGotico.obra`), el andamio caído, la crecida y la altura del coro.

**Las diez reglas en Gótico:**

| # | Cómo se cumple |
|---|---|
| 1 Luz | `Rayo(luces)`. Todo tiene cara de luz y de sombra. Cada cosa arroja sombra en el suelo, en la pila y en el zarzo, y también sobre las cosas que tiene detrás. El alero deja medio patio en sombra por la mañana y una franja a mediodía |
| 2 Cielo | `cielo` y `nubes` con la semilla del día. Enmarca el alero con su poste; no tapa la luna ni la banderita |
| 3 Paleta | 23 colores con nombre: caliza crema, plomo azul gris, roble, paja y teja, con el azul de la waide como acento |
| 4 Agua | El Somme: `agua`, `bajio`, `reflejoRoto` de casas, árboles e iglesia, `estela` y `destellos` |
| 5 Primer plano | Patrón `Cosa` con 10 cosas de 20 a 70 unidades, encimadas, con lascas, chorretones de cal, verdín y polvo de talla |
| 6 Cruces | El poste de abajo arriba, la cabria en diagonal, y la catedral y la atalaya sobre el horizonte (al 43 % del alto) |
| 7 Textura | Hiladas y manchas en la piedra, rayas de cincel, el zarzo tejido vara a vara, vetas; `grano` al final |
| 8 Noche | El farol alumbra el zarzo y el suelo; brasas en el hogar; ventanas en la villa y en la atalaya |
| 9 Bordes | Orillas con puntos a mano; barro y juncos entre la hierba y el agua; verdín al pie de los muros |
| 10 Lejanía | Tres hileras de casas con costado y sombra; la muralla delante de los campos; lomas, un seto, un molino y dos filas de montes |

## El cuadro de Ciudad neón

**Qué se ve.** El callejón del nivel −3. Es el mismo para los dos: el puesto es de Amaru y el callejón queda en las rutas de Kei.

- **A la izquierda:** el puesto de fideos, con su toldo de rayas y el letrero del tazón magenta.
- **A la derecha:** la farmacia de guardia de Celso, con su cruz verde agua.
- **Al fondo y arriba:** la boca del callejón, tres filas de torres, pasarelas, anuncios sin texto, un tren colgante y una rendija por la que se adivina el día de fuera.
- **Delante:** la bombona, las cajas, el cubo y la fregona, los bancos rojos, el cajón con el objeto del día, las bolsas de basura y, los días de Kei, su bici.
- **El marco:** los cables de lado a lado, el poste con su farol y un alero de chapa.

**Es la primera escena «siempre de noche»,** así que las reglas se adaptaron como dice la sección 5 de la guía:

| # | Cómo se cumple o se adapta |
|---|---|
| 1 Luz | **Adaptada.** No hay sol. Hay focos: el letrero magenta, la lámpara del puesto, el farol ámbar, la cruz, el escaparate y la avenida. Cada cosa pide su color al alumbrado (`Alumbrado.en`) y lleva un filo de cada luz. La sombra de cada cosa la tumba el foco que más la alumbra (`rayoDeFoco`). Las sombras cambian cuando el puesto abre o cierra |
| 2 Cielo | **Adaptada.** La franja entre los edificios: torres, anuncios que cambian cada día, pasarelas, tren, ropa tendida y la rendija |
| 3 Paleta | Tres luces (magenta, verde agua y ámbar) sobre azul violeta |
| 4 Superficie | **Adaptada.** El suelo mojado: baldosas, carril de bicis, charcos que reflejan el tazón, un reflejo por cada luz (`reflejoMojado`) y la lámina de agua con tablones según el arco `agua` |
| 5 Primer plano | Patrón `Cosa`, con las cosas grandes, encimadas, con óxido y manchas |
| 6 Cruces | El poste, la fregona, la bajante de la farmacia, los cables y la bici |
| 7 Textura | Desconchones, chorreras, carteles viejos, grietas; `grano` al final |
| 8 Noche | Es la regla central aquí: se leen formas y volúmenes a toda hora. En el apagón alumbran las velas |
| 9 Bordes | Lo construido va recto pero gastado: bordillo, canaleta, zócalos oscuros, remiendos de asfalto |
| 10 Lejanía | Tres capas de torres con bruma, pasarelas a dos alturas y la avenida al fondo |

**Qué cambia con la hora.** Como no hay día y noche, el cuadro cambia con la vida del callejón:

| Hora | Qué manda |
|---|---|
| 7:00 | Todo duerme: el puesto recogido, con los bancos apilados y el cubo de fregar; la cruz, el farol y una bombilla |
| 13:00 | La farmacia abierta, con su escaparate; el puesto cerrado; un poco de claridad por la rendija |
| 21:00 | El letrero magenta y el puesto abierto |

- **El puesto** sigue el horario de Amaru (abierto de 18:00 a 4:00), también los días de Kei. Una prueba lo compara con sus tramos.
- **La farmacia abre de 8:00 a 20:00** y el resto está de guardia. **[Diseño propio]**
- **La rendija de arriba deja ver el día de fuera.** **[Aproximación]**: en los niveles bajos es siempre de noche; esa claridad es lo único que llega.

**Qué cuenta, igual que el cuento:**

| Lo que dice la ficha | Lo que se ve |
|---|---|
| Arco `letrero` | El tazón encendido, remendado, roto o apagado (`EscenaNeon.letrero`) |
| Clave `apagon` | El callejón a oscuras: gas y velas |
| Arco `pintas` | Carteles y pintadas, sin texto; al final, un mural en la cortina del puesto |
| Arco `obra`, clave `maquinas` | Las máquinas en la boca del callejón |
| Arco `agua` | La lámina de agua y los tablones |
| Arco `luces` | Las ristras de fin de año |
| **Estado `sin_bici` de Kei** | **La bici no está**, a ninguna hora |
| Arco `bici` | Al volver, el cuadro de la bici con la soldadura a la vista |
| Claves `fiesta`, `faroles`, `velas`, `huelga`, `toque`, `cadena` | Banderines; faroles de papel; velas y retratos de luz; pancartas y cintas rojas; el puesto cerrado a las 21:00, con cinta y foco rojo; la cuerda de trapos delante de las máquinas |
| De quién es el día | Los días de Kei está su bici: esperando a sus horas de trabajo (**[Diseño propio]**: el cuento no dice que espere ahí) y enchufada al cable del cocinero mientras cena. El objeto del día es el de cada uno |

## La biblioteca

- **`Focos.kt`:** `Foco` (una luz en un sitio), `Alumbrado` (la luz de fondo más los focos encendidos), `planoH`, `planoV` y `cuerpo` (pintar un suelo, una pared o un bulto con esa luz), `charcoDeLuz`, `rayoDeFoco`, `tuboNeon`, `reflejoMojado` y `haloDeBruma`. Sirve para los interiores (Refugio, Laboratorio, Espacio) y para Art déco.
- **`Ciudad.kt`:** `torre`, `anuncio`, `pasarela`, `tuberia` y `cable`.
- **`Cosas.kt`:** `Cosa`, que era privada de Helénico.
- **`Sombras.kt`:** `Rayo` lleva ahora sus valores en el constructor. `Rayo(luces)` sigue dando el del sol; `rayoFijo(…)` da el de una luz que no se mueve.
- **No se cambió cómo se ve ninguna pieza que ya existía.**

## Los días de las capturas

| Personaje | Días |
|---|---|
| Pierre (10) | `normal` (10 may 2027), `fiesta` (9 jun 2030, la boda de Jehanne), **`parada`** (3 sep 2027), **`herido`** (27 may 2028), `crecida`, `procesion`, `invierno`, `coro` (10 oct 2031), `eclipse` y `hogueras` |
| Amaru (8) | `normal` (5 may 2027), `faroles`, **`apagon`** (21 ago 2028), `roto`, `marea`, `velas`, `nochevieja` y `maquinas` (2 sep 2030) |
| Kei (7) | `normal` (6 may 2027), **`sinbici`** (25 mar 2028), `soldada` (8 abr 2028), `huelga`, `toque`, `cadena` y `tribunal` |

- **El día normal de Pierre es el 10 de mayo,** no el 5: el 5 llueve en Amiens.
- **Las capturas de Kei son a las 7:00, las 17:00 y las 21:00.** A las 13:00 ella anda repartiendo y el cuadro sería casi el de Amaru.

## Cómo se hizo

- **Las dos escenas se dibujaron a la vez,** cada una en un arnés propio y fuera del repositorio, para que una a medias no rompiera la compilación de la otra. Después se integraron.
- Antes, en un commit aparte, la biblioteca ganó lo que las dos iban a compartir (`Cosa` y `rayoFijo`).

## Verificación

- **Pruebas:** 29 pasan juntas en el arnés (las 13 de P4a, 7 de Gótico y 9 de Ciudad neón).
- **Barrido de Gótico:** 18,238 dibujos, 0 fallas (todos los días de 2026 a 2032 a cinco horas, todos los tiempos por estación en 12 días especiales y todos los objetos).
- **Barrido de Ciudad neón:** 24,188 dibujos, 0 fallas (lo mismo, con los dos personajes).
  - No cupo en una sola corrida: la primera se cortó a los 13 minutos sin que se sepa por qué. Se corrió por partes.
- **Peso:** unos 65 ms por cuadro pequeño (190 px) en el arnés, como Helénico. Gótico lleva 4 capas de sombra; Ciudad neón, 1.
- **Hojas vistas junto a Helénico:** las tres horas, todos los días de muestra, todos los objetos y los tiempos.
- **Compilación de Android y capturas:** ver «Resultado en Actions», abajo.

## Lo que quedó flojo

**Gótico:**
- **La paleta es más pálida que la de Helénico:** domina el crema de la piedra.
- **La nave** es la parte más simple del cuadro.
- **La crecida y la paja se leen, pero con poca fuerza** a tamaño pequeño. No están los tablones sobre cubos que menciona el cuento.
- **La obra parada de septiembre se distingue de la de invierno solo por la estación:** el patio no cambia.
- **Sin dibujar:** el dintel de Enguerrand, la gárgola de la temporada 5, la dovela de Colin (la piedra es siempre el capitel de hiedra) y el cierre de la capilla de 1243.
- **El luto apenas se nota,** y las campanas volteadas son muy pequeñas: lo que se ve son los pájaros.
- **La saya azul sobre la pila** mientras trabaja es un añadido, no está en el cuento.
- Solo se dibujó a 380 de ancho; el lienzo escala entero.

**Ciudad neón:**
- **Las sombras arrojadas se ven sobre todo junto al puesto.** En el primer plano caen hacia quien mira y casi salen del cuadro. Es la regla 1 más débil que en Helénico.
- **El lado derecho es más pobre que el izquierdo:** la pared de la farmacia y el alero tienen poco detalle.
- **Con lluvia y tormenta el cuadro quieto se parece al nublado:** las gotas y el fogonazo van en la parte viva. No hay rayo dibujado, porque saldría encima de los edificios.
- **La niebla es propia de la escena:** la de la biblioteca dejaba una barra en mitad del callejón.
- **Claves sin dibujo propio:** `dia_red`, `dia_aurea`, `feria` y `descuentos` salen como una fiesta cualquiera. Falta el aviso de demolición pegado en la cortina.

## Lo que no se puede comprobar aquí

- **El rendimiento y la batería,** como en P4a: hay que medirlo en el teléfono en P6.
- **El movimiento:** el parpadeo del letrero, el vapor, la lluvia y los destellos del río no se ven en una imagen quieta.

## Resultado en Actions

Sobre el commit de la tanda (`f5bc71f`).

- **Compilación de Android:** la corrida 208 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, **terminó bien** (no se publicó ningún Release).
- **Capturas:** la corrida 268 de `capturas.yml` (`superficies = cuadro`, `temas = gotico,neon`, `solo = true`) **terminó bien**, en unos 6 minutos, sin errores de compilación ni pruebas fallidas.
  - Dejó 27 imágenes en la rama `capturas`: 10 de Gótico, 8 de Amaru, 7 de Kei y las 2 de la biblioteca.
  - Revisé el día normal de Gótico y el de Kei: **se ven igual que en el arnés.** Las luces de neón, los reflejos del suelo y las sombras salen con el motor de dibujo de Android.
  - `despachos.txt` y `unicidad.txt` salieron vacíos; `cobertura.txt` trae justo los 29 temas que faltan.
  - En esa corrida corrieron también en Actions las 29 pruebas de escenas.
- **Corridas de capturas usadas:** 1 de las 2 que preveía el plan. La otra queda para los cambios que pida el autor al ver los cuadros.
- Con `solo`, la rama `capturas` queda solo con estas 27 imágenes hasta la siguiente corrida completa.
