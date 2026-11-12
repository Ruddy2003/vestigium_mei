# Tanda P5a — El gato del cuadro: esqueleto, poses y estados

Fecha: 12 nov 2026. Rama: `claude/gallant-goldberg-bsc3pm`, encima de P4b (en el mismo PR, que seguía abierto). Plan: `docs/plan_historia_y_escenas.md`, sección 3.4.

## Qué trae

El gato que vivirá dentro del cuadro de Hoy. Ya no es una tira de imágenes, como el de pixel: es un esqueleto que se dibuja con código, se porta igual que el de pixel y recibe la luz de la escena en la que está.

| Archivo | Qué tiene |
|---|---|
| `ui/escenas/GatoEsqueleto.kt` | La postura (`Pose`) y las 13 animaciones, cuadro por cuadro (`Gatuno.poseDe`) |
| `ui/escenas/GatoDibujo.kt` | El dibujo del cuerpo sobre una postura (`gato`), el pelaje (`Pelaje`) y la luz que le llega (`LuzGato`) |
| `ui/escenas/GatoCuadro.kt` | Cómo se porta (`SimulacionGato`) y la capa del gato dentro del cuadro (`CapaGato`) |
| `ui/escenas/EscenaVida.kt`, `VistaEscena.kt` | El motor: cada escena le da al gato su luz (`luzDelGato`) y su tamaño (`tallaDelGato`); el cuadro tiene una tercera capa para él |
| `EscenaHeleno.kt`, `EscenaGotico.kt`, `EscenaNeon.kt` | La luz del gato en cada una |
| `app/src/test/…/GatoCuadroPrueba.kt` | 9 pruebas |
| `app/src/test/…/Capturas.kt` | La superficie `gatoCuadro`: la hoja de poses y el gato en su escena |

- **En la app todavía no se ve nada:** el cuadro, con su gato, entra en Hoy en la tanda P6.
- **No cambia nada de lo que se guarda** ni lee ningún dato nuevo: usa el mismo `Gato.humor` que el gato de pixel.
- **El gato de pixel no se tocó** (`ui/Gato.kt`).
- **Las capturas de las escenas (`cuadro`) siguen sin gato:** así las imágenes aprobadas no cambian.

## Cómo es

**El esqueleto.** Una postura dice dónde están la cadera, el hombro, la cabeza, los cuatro pies y la cola, más las orejas, los ojos, la boca y el pelo erizado. Las rodillas y los codos salen solos.
- Con talla 1 mide unas 33 unidades de largo sin la cola y 33 de alto hasta las orejas: su tamaño real junto a las cosas del primer plano.
- **En el cuadro va a talla 1.3,** algo más grande de lo real, porque a su tamaño costaba encontrarlo. **[Diseño propio]**

**Las 13 animaciones** son las del gato de pixel, con los mismos cuadros cada una:

| Animación | Cuadros | Qué hace |
|---|---|---|
| caminar | 8 | Las patas van una tras otra; la cola, en alto |
| caminar triste | 8 | Lo mismo con la cabeza gacha y la cola baja |
| quieto | 4 | Respira, mueve la punta de la cola y parpadea |
| quieto triste | 4 | Cabizbajo |
| girar | 2 | Se le ve de escorzo y luego de frente |
| saltar | 6 | Se agazapa, se impulsa, va por el aire, cae y se recoge |
| jugar | 6 | Menea la grupa, salta sobre algo y le da manotazos |
| enojado | 3 | Lomo arqueado, pelo de punta, orejas gachas, bufa |
| sentarse | 4 | De pie a sentado (al revés, se levanta) |
| sentado | 2 | Mueve la punta de la cola |
| echarse | 3 | De sentado a enroscado (al revés, despierta) |
| asearse | 6 | Se lame una mano y se la pasa por la cara |
| dormir | 3 | Enroscado, respira despacio |

- **Al caminar, los pies no patinan:** lo que avanza en ocho cuadros es justo una zancada. Una prueba lo mide.
- **Cada tema podrá sumar sus dos acciones propias** (tocar la lira, sorber fideos): el pelaje trae sus posturas y lo que lleva en las manos. Llegan en P5b.

**El dibujo.** Al gouache, como las escenas.
- El cuerpo entero se pinta en una capa aparte, con todas sus partes del mismo color, para que no se noten las juntas.
- Encima, y solo donde ya hay gato, van el dibujo del pelaje y la luz: el lomo claro, el vientre en sombra, un lado más claro que el otro.
- **Lleva un borde oscuro del lado de la sombra,** del color de su pelo oscurecido. No es un contorno negro: es lo que lo despega del fondo. **[Diseño propio]**
- De noche le brillan los ojos y la pupila se le abre.
- Arroja su sombra en el suelo, hacia el lado contrario a la luz.

**El pelaje** (`Pelaje`): tres colores (manto, claro y marca), el color de los ojos, un collar opcional y uno de cinco dibujos.

| Dibujo | Cómo es |
|---|---|
| liso | De un solo color |
| atigrado | Rayas en el lomo, anillos en la cola y en las patas |
| manchas | Manchas grandes de dos colores |
| bicolor | El pecho, el hocico y las cuatro patas claros |
| puntas | La cara, las orejas, las patas y la cola oscuras |

- **Por ahora todos los temas llevan el pelaje común:** gris atigrado, de ojos verdes. El pardo que probé primero se confundía con el barro y el suelo de Helénico.
- El pelaje de cada tema (el de su gato de pixel) y su accesorio llegan en P5b, con `pelajeDe`.

**La luz.** El gato no trae luz propia: se la da la escena en el sitio donde pisa.
- **Helénico y Gótico:** la del sol o la luna de ese momento, con su eclipse. Al atardecer se dora; de noche se azula.
- **Ciudad neón:** la de los focos del callejón. Su sombra la tumba el foco que más lo alumbra.
  - Toma el color de los letreros solo en parte (al 65 %). Con la luz entera cambiaba de color de un paso a otro (ámbar junto al puesto, verde agua junto a la farmacia) y dejaba de parecer el mismo gato. **[Diseño propio]**

## Cómo se porta

**Igual que el de pixel.** `SimulacionGato` es la misma máquina de estados de `ui/Gato.kt`, con los mismos tiempos y las mismas probabilidades, a 12,5 cuadros por segundo.

| Cómo estás | Qué hace el gato |
|---|---|
| Normal | Camina, se detiene, se sienta, se asea, se echa, salta y juega |
| Alegre | Juega y salta más; camina más ligero |
| Triste | Camina lento, con la cola baja, y pasa más rato sentado. No juega ni salta |
| Enojado | A ratos se eriza |
| Cansado | Camina al 70 %, salta y juega la mitad, y duerme siestas más largas |
| De noche (de 22 a 5 h) | Casi solo duerme |

- **Camina por el tramo de suelo que le da la escena** (el ancla `SUELO`), por delante de todo. Al llegar al borde da la vuelta.
- **Lo demás del ánimo es lo de siempre:** sale de cómo dormiste y de cómo calificaste el día (`Gato.humor`). No se añadió nada.
- **Con las animaciones apagadas, con ahorro de energía o en las capturas, queda sentado** en medio de su tramo.
- **Las caricias, mirar hacia donde tocas y reaccionar al fuego, a la lluvia o al objeto del día** son de P5b.

## Lo que decidí y no estaba en el plan

1. **La máquina de estados está copiada, no compartida.** La del gato de pixel vive en un archivo que no compila sin Android, y moverla era tocar algo que funciona y que no puedo probar aquí. Si un día cambia una, hay que cambiar la otra; una prueba vigila que los cuadros y los pasos de un estado a otro sigan siendo los mismos. **[Diseño propio]**
2. **La luz del gato la da la escena.** El motor ganó `luzDelGato` y `tallaDelGato`, con valores por omisión que sirven para cualquier escena con sol.
3. **Sus pasos son el 80 % de los del gato de pixel,** porque en el cuadro es algo más chico que en su banda.
4. **La lluvia cae por detrás del gato:** su capa va encima de la parte viva de la escena. P4a había dejado esto por decidir.
5. **El gato tapa lo que tiene detrás.** Camina por delante de las cosas del primer plano; no se mete entre ellas.
6. **Una superficie de capturas nueva, `gatoCuadro`,** en vez de meter al gato en las de `cuadro`.
7. **Al girar se le ve de frente,** con las dos orejas y los dos ojos. El de pixel tenía dos cuadros de giro pintados a mano.

## Para las tandas que siguen

- **P5b:**
  - `Pelajes.kt` con `pelajeDe(tema)`, exhaustivo y sin `else`; los pelajes de Helénico, Gótico y Ciudad neón, con su accesorio (`Pelaje.accesorio`) y sus dos acciones (`EspecialGato`);
  - la fila «Cuadro: gato» de `TEMAS.md` y una superficie `cuadro_gato` en `CoberturaTemas`;
  - las reacciones a las anclas: la simulación solo conoce hoy el tramo de suelo. Para ir a un sitio hace falta un destino (`irA`), que hoy no existe;
  - la caricia y el toque: `Gatuno.MEDIO_LARGO` y `Gatuno.ALTO` dan la caja para saber si el dedo cayó sobre él.
- **Una escena nueva** no tiene que hacer nada por el gato si tiene sol y un ancla `SUELO` de al menos 110 unidades. Si su luz es propia (un interior, focos), cambia `luzDelGato`, como Ciudad neón.
- **P6:** `VistaEscena(…, humor = Gato.humor(hoy), pelaje = pelajeDe(tema))`. Sin `humor` no hay gato.
- **En el arnés,** `ui/Gato.kt` no compila: hay que poner en `otras/` un `object Gato` con solo `Animo` y `Humor`.

## Verificación

- **Pruebas:** 38 pasan en el arnés (las 29 de antes y 9 del gato).
  - las 13 animaciones tienen los cuadros del gato de pixel, y todas sus posturas caben en su sitio;
  - al caminar los pies no patinan y siempre apoya al menos dos patas;
  - con la misma semilla hace siempre lo mismo;
  - nunca se sale de su tramo de suelo, con ningún ánimo, y en un tramo donde no cabe se queda en medio;
  - los pasos de un estado a otro son los del gato de pixel;
  - el ánimo, el cansancio y la noche cambian lo que hace, como dice la tabla de arriba;
  - hace las acciones propias de su tema cuando las tiene;
  - cada escena le da suelo, talla y una luz válida a todas horas.
- **Barrido:** 173,760 dibujos, 0 fallas.
  - 77,760: todas las posturas con seis pelajes, en las tres escenas, a seis horas, con tres tiempos y mirando a los dos lados.
  - 96,000: el gato suelto en cada escena, con cada ánimo, cansado o no, de día y de noche.
- **Peso:** 1.3 ms por cuadro del gato en el arnés, a 380 × 285. Se traza 12,5 veces por segundo y no obliga a rehacer la parte quieta de la escena.
- **El movimiento** lo vi en el arnés: dos películas de 26 segundos, una en Helénico y otra en Ciudad neón, hechas con los cuadros que da la simulación.
- **Compilación de Android y capturas:** ver «Resultado en Actions», abajo.

## Lo que quedó flojo

- **El giro de frente es pobre:** el cuerpo es el de perfil estrechado, no un cuerpo visto de frente.
- **Sentado se ve algo encorvado,** y dormido es un bulto con poca forma.
- **Las patas de atrás tienen dos huesos,** no tres: les falta el corvejón.
- **No hay cuadros intermedios:** se mueve a 12,5 cuadros por segundo, como el de pixel, y los cambios de postura son secos.
- **En Ciudad neón, echado en la parte oscura del callejón, se pierde.**
- **Tapa las cosas del primer plano** cuando pasa por delante, también el objeto del día.
- **Las manchas y las puntas están menos trabajadas** que el atigrado.

## Lo que no se puede comprobar aquí

- **El rendimiento y la batería en el teléfono:** el gato suma una capa pequeña que se rehace 12,5 veces por segundo. Hay que medirlo en P6.
- **Que el movimiento se vea fluido en el teléfono:** las películas del arnés usan otro motor de dibujo.

## Resultado en Actions

_Pendiente: se llena al terminar las corridas._
