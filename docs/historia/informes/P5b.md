# Tanda P5b — El gato del cuadro: reacciones, caricia y pelajes

Fecha: 12 nov 2026. Rama: `claude/gallant-goldberg-bsc3pm`, encima de P5a (en el mismo PR, que seguía abierto). Plan: `docs/plan_historia_y_escenas.md`, sección 3.4.

## Qué trae

El gato del cuadro ya es el de cada tema, reacciona a lo que hay en la escena y responde al dedo.

| Archivo | Qué tiene |
|---|---|
| `ui/escenas/Pelajes.kt` | `pelajeDe(tema)` y los pelajes de Helénico, Gótico y Ciudad neón, con su accesorio y sus dos acciones |
| `ui/escenas/GatoCuadro.kt` | Las reacciones a las anclas, la caricia y el toque (`SimulacionGato.entorno`, `acariciar`, `tocar`); el corazón; dónde queda sentado cuando no se anima |
| `ui/escenas/GatoEsqueleto.kt` | Siete animaciones nuevas, las de las reacciones |
| `ui/escenas/GatoDibujo.kt` | De frente o de espaldas, las patas quedan rectas y no se hunden en el suelo |
| `ui/escenas/VistaEscena.kt`, `EscenaVida.kt` y las tres escenas | El cuadro acepta toques; el ancla del fuego dice dónde se echa el gato y dónde está la lumbre |
| `app/src/test/…/GatoCuadroPrueba.kt`, `EscenasPrueba.kt` | 8 pruebas nuevas (17 del gato en total) |
| `Capturas.kt`, `CoberturaTemas.kt`, `docs/diseno/TEMAS.md` | La captura `gatoCuadro` con pelajes, acciones y reacciones; la superficie `cuadro_gato`; faltan 29 temas |

- **En la app todavía no se ve nada:** el cuadro, con su gato, entra en Hoy en la tanda P6.
- **No cambia nada de lo que se guarda.** Las caricias no se guardan en ningún sitio.
- **El gato de pixel no se tocó.**

## Los tres gatos

Cada uno lleva los colores, el accesorio y las dos acciones de su gato de pixel.

| Tema | Pelaje | Accesorio | Sus dos acciones |
|---|---|---|---|
| Helénico | Miel, con el pecho, las patas y la punta de la cola blancos; ojos azules | Clámide púrpura con ribete y fíbula de oro | Toca la lira · alza la antorcha |
| Gótico | Azul noche, liso; ojos dorados | Collar rojo con medallón de oro | Sostiene una vela · mira una estrella de la bóveda |
| Ciudad neón | Negro, liso; ojos verdes | Chaqueta de lluvia con capucha, banda ámbar y ribete magenta | Sorbe fideos con palillos · sigue al dron de reparto |

- **El miel de Helénico es más oscuro que el crema de su gato de pixel:** con el crema se perdía sobre el suelo y el encalado. **[Diseño propio]**
- **El collar de Gótico va pintado en el accesorio,** no con el collar común: echado, el común le cruzaba el lomo como un tajo rojo.
- **El accesorio se ve desde todos los lados:** de perfil, de tres cuartos, de espaldas, sentado y echado.
- **Las acciones tienen los mismos cuadros que en el gato de pixel** (4 o 6) y todas parten de estar sentado.

## Cómo reacciona a la escena

La escena le dice qué hay y dónde (sus anclas), y él a ratos deja lo que hacía para ir.

| Lo que hay | Qué hace |
|---|---|
| **El fuego, cuando está encendido** | Va a su sitio junto a la lumbre, se sienta de cara a ella un buen rato y acaba echándose a dormir, más de lo normal |
| **El objeto del día** | Se acerca por un lado (no se pone encima), lo huele y a veces le da con la mano |
| **El mirador** | Salta arriba (al murete, a la pila de sillares, a las cajas), se queda de espaldas mirando a lo lejos unos segundos y baja por donde subió |
| **Lluvia o nieve** | En cuanto empieza, deja lo que hace (si dormía, se despierta) y va al abrigo. No sale de ahí mientras dure: se sienta, se asea, duerme |
| **El adorno de una fiesta** | Se sienta debajo, lo mira hacia arriba y cada tanto le tira un manotazo |

- **El ánimo también cuenta aquí:** alegre reacciona más; triste o enojado, casi nada. Cansado, triste o de noche prefiere el fuego y casi no sube al mirador.
- **No repite la misma reacción dos veces seguidas** y deja pasar entre 7 y 14 segundos entre una y otra.
- **Subido al mirador se le pinta al 82 %:** está un poco más lejos.
- **Sin anclas se porta exactamente igual que el gato de pixel.** Una prueba compara los dos cuadro a cuadro.

## Cómo responde al dedo

- **Un toque corto sobre él es una caricia.**
  - Se sienta, cierra los ojos y levanta la cabeza hacia la mano. Le sale un corazón que sube y se desvanece, con un resplandor suave alrededor.
  - Si estaba echado o dormido, no se levanta: mueve una oreja y la punta de la cola.
  - Si estaba en el mirador, mueve las orejas y sigue mirando.
  - El cuadro avisa de la caricia (`alAcariciar`) para que P6 ponga la vibración corta, respetando el ajuste de vibración.
- **Un toque en otro sitio:** si queda cerca, se vuelve hacia ahí y mira a quien lo ve; si queda lejos, camina hasta ahí y mira. Dormido no se entera.
- **Responde en cuanto pisa el suelo.** Si iba por el aire (un salto, el brinco al mirador), espera a caer: nunca da un brinco de sitio.
- **El dedo tiene holgura:** la zona que cuenta como «sobre el gato» es algo mayor que él, porque es chico.
- **Con las animaciones apagadas** la caricia sigue saliendo: el corazón aparece quieto un segundo.

## Lo que decidí y no estaba en el plan

1. **«Le gusta» es el corazón, no un texto.** El plan decía «corazón, "le gusta", aura». En el cuadro no hay letras; el nombre de la acción para lectores de pantalla lo pone P6. **[Diseño propio]**
2. **El aura es un resplandor pintado dentro del cuadro,** no el `Modifier.halo` de los objetos de Vida: aquel depende del tema y de Android, y este tenía que verse igual en el arnés.
3. **La vibración la pone P6.** El código del gato no puede leer tus ajustes (así se puede probar fuera de Android); avisa, y quien lo usa decide.
4. **Los toques los recoge un solo detector,** en la capa del gato. P6 tiene que añadir ahí la pulsación larga de «Hoy en la historia»: dos detectores, uno dentro de otro, se quitarían los toques.
5. **Cuando no se anima, ya no queda siempre en medio:** se sienta en el sitio más despejado de su tramo, lejos del fuego y del objeto del día. En Helénico el medio caía justo sobre el brasero.
6. **El ancla del fuego cambió de sentido:** su `x` es donde se echa el gato y su `hasta`, dónde está la lumbre, para que la mire. Antes solo había un punto y el gato se ponía encima.
7. **Siete animaciones más,** además de las 13 comunes: dejarse acariciar (sentado y echado), oler, vigía de espaldas, mirar (de pie y sentado) y mirar hacia arriba con manotazo.
8. **Los pelajes se dibujaron aparte, a la vez que las reacciones,** en un arnés con copia de las fuentes, y se integraron al final.

## Para probar en el teléfono

Esto no se puede comprobar aquí: las capturas son imágenes quietas y el arnés no tiene dedo. Hay que hacerlo cuando P6 ponga el cuadro en Hoy.

**La caricia y el toque**
- [ ] Un toque corto sobre el gato da el corazón y la vibración corta, y él se sienta con los ojos cerrados.
- [ ] No hace falta atinarle justo: basta tocar cerca.
- [ ] Acariciarlo dormido no lo levanta.
- [ ] Un toque lejos lo hace caminar hasta ahí; uno cerca, volverse y mirar.
- [ ] **Desplazar la pantalla pasando el dedo por encima del cuadro no cuenta como caricia ni como toque.**
- [ ] La pulsación larga de «Hoy en la historia» (700 ms) no dispara además una caricia.
- [ ] Con la vibración apagada en Ajustes, no vibra.

**Las reacciones** (con el día y la hora en que la escena las ofrece)
- [ ] En Helénico, a la hora de comer de Demeas, va al brasero y se echa a su lado.
- [ ] Sube al murete, se queda de espaldas y baja sin atravesar nada.
- [ ] Va al banco y huele el objeto del día sin taparlo.
- [ ] Un día de lluvia se va a su abrigo y no sale.
- [ ] Un día de fiesta mira las guirnaldas y les tira manotazos.
- [ ] En Ciudad neón, con el puesto abierto, se echa junto a la olla.

**El movimiento**
- [ ] Camina sin patinar y da la vuelta de espaldas.
- [ ] El brinco al mirador se ve como un salto, no como un deslizamiento.
- [ ] Las acciones propias (la lira, la vela, los fideos…) se entienden a tamaño real.
- [ ] No da tirones al entrar a Hoy ni al desplazar la pantalla.

**El ánimo**
- [ ] Con un día bien calificado juega y salta más; con uno malo camina despacio y cabizbajo.
- [ ] Habiendo dormido poco, duerme más.
- [ ] De 22 a 5 h casi solo duerme.

**Lo que no debe pasar**
- [ ] Con las animaciones apagadas o con ahorro de energía queda sentado, sin tapar el fuego ni el objeto.
- [ ] La batería no baja más de lo normal con Hoy abierto un rato.

## Para las tandas que siguen

- **P6:** `VistaEscena(…, humor = Gato.humor(hoy), pelaje = pelajeDe(tema) ?: Pelaje.COMUN, toques = true, alAcariciar = { vibración corta })`.
  - La pulsación larga va en el mismo detector de toques que la caricia (`CapaGato`, en `GatoCuadro.kt`).
  - Las tres acciones con nombre para lectores de pantalla: acariciar al gato, leer el cuento y abrir «Hoy en la historia».
- **Las tandas S (un tema nuevo):**
  1. su rama en `pelajeDe`, con los colores, el accesorio y las dos acciones de su gato de pixel (`assets/gato/gato.json` dice cuáles y con cuántos cuadros);
  2. sus anclas en la escena: `SUELO` (al menos 110 unidades), `MIRADOR`, `OBJETO`, `ABRIGO`, `FUEGO` solo si está encendido (con `hasta` = dónde está la lumbre) y `ADORNO` solo en fiesta;
  3. si su luz es propia, `luzDelGato`;
  4. borrar el tema de `SIN_CUADRO` y poner en **P** su celda de «Cuadro: gato»;
  5. mirar al gato en cada ancla dentro de la escena (la captura `gatoCuadro` trae una imagen de reacciones).
- **El accesorio y la utilería se pintan encima del gato,** fuera de su capa: tienen que ser formas opacas.

## Verificación

- **Pruebas:** 46 pasan en el arnés (17 del gato; 8 son de esta tanda).
  - sin anclas se porta, cuadro a cuadro, como antes de que existieran las reacciones;
  - va al fuego, huele el objeto desde un lado, mira el adorno desde debajo y sube al mirador y baja;
  - sin fuego ni adorno entre sus anclas, no va a ellos;
  - cuando llueve se guarece y no sale, con cualquier ánimo, y vuelve a salir cuando escampa;
  - se deja acariciar en menos de un segundo, sin moverse de sitio; dormido no se levanta;
  - con un toque lejos, llega y mira; con uno cerca, se vuelve y mira;
  - con anclas, lluvia y toques al azar, nunca se sale del cuadro ni da brincos de sitio;
  - se sabe si un dedo cayó sobre él, también en el mirador;
  - cuando no se anima, queda donde no tapa el fuego ni el objeto;
  - el pelaje va siempre junto con la escena.
- **Barrido:** 287,976 dibujos, 0 fallas.
  - 153,576: todas las posturas (también las de las reacciones) con todos los pelajes, en las tres escenas, a seis horas y con tres tiempos.
  - 134,400: el gato suelto en cada escena con las anclas de un día normal, uno de fiesta y uno de lluvia, con su pelaje y sus acciones, cada ánimo, y toques y caricias al azar.
  - Aparte, el barrido de los pelajes: 84,672 dibujos sin fallas, con todos los cuadros de las seis acciones y el accesorio visto desde 12 ángulos.
- **Peso:** 2.0 ms por cuadro del gato en el arnés, a 380 × 285.
- **El movimiento** lo vi en tres películas de unos 40 segundos (una por tema), con caricias y toques.
- **Compilación de Android y capturas:** ver «Resultado en Actions», abajo.

**Dos fallos que cazaron las pruebas nuevas, ya corregidos:**
- Si lo acariciabas justo mientras saltaba al mirador, aparecía de golpe arriba.
- Sentado de espaldas (en el mirador), las patas de atrás se hundían bajo el suelo.

## Lo que quedó flojo

- **El gato de Ciudad neón se ve pardo oscuro, no negro,** junto a la lámpara ámbar del puesto; echado en la parte oscura del callejón se pierde, y lo que se lee es la chaqueta.
- **De espaldas con la cola en alto,** la clámide y la chaqueta se recortan con una banda recta donde pasa la cola.
- **La ropa no sabe del lomo erizado:** enojado, queda un poco por debajo de las púas.
- **La lira y la estrella son chicas a tamaño real.**
- **Sentado de espaldas en el mirador** es una columna con dos patas: le falta forma de pera.
- **Tapa las cosas del primer plano** cuando pasa por delante. Al oler el objeto se pone a un lado, pero al caminar lo cruza.
- **En el abrigo de Gótico queda pegado al borde derecho del cuadro.**
- **No hay cuadros intermedios:** va a 12,5 cuadros por segundo, como el de pixel.

## Resultado en Actions

- **Primer envío** (`c8c5055`): la corrida 272 de `capturas.yml` (`superficies = gatoCuadro`, con los tres temas y `solo = true`) **terminó bien**, con 9 imágenes y sin fallas. `despachos.txt` salió vacío y `cobertura.txt` ya trae la superficie `cuadro_gato` con los 29 temas que faltan.

**Después de los arreglos** (el sitio junto al fuego, dónde queda sentado sin animación y cómo se retratan las reacciones; `2976529`):

- **Compilación de Android:** la corrida 239 de `apk.yml`, sin publicar, **terminó bien**.
- **Capturas:** la corrida 273 **terminó bien**, con 9 imágenes y sin fallas. Revisé las reacciones de Helénico y la hoja de poses de Ciudad neón: el gato, su pelaje y su accesorio **se ven igual que en el arnés.**
  - **La hoja de poses salía recortada:** con las siete animaciones nuevas y las dos acciones ya no cabía en el alto que tenía. Se corrigió (el alto ahora sale de lo que hay que mostrar) y se repitió la corrida: ver abajo.

**La hoja de poses completa** (`daaa6f2`):

- La corrida 274 de `capturas.yml` **terminó bien**, con 9 imágenes y sin fallas (`errores.txt` y `fallas.txt` vacíos).
- La hoja de Gótico ya sale entera: mide 1080 × 6781 y llega hasta las dos acciones del tema, «sostiene una vela» (4 cuadros) y «mira una estrella de la bóveda» (6 cuadros). Revisé el final de la imagen: están las siete animaciones nuevas y las dos acciones, con su utilería.
- El cambio fue solo en la captura (`Capturas.kt`); la app no cambió, así que la compilación de la corrida 239 sigue valiendo.

**Corridas de capturas usadas en P5b:** 3 (272, 273 y 274). El plan pedía 2; la tercera fue por la hoja recortada.
