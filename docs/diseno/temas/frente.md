# Frente exterior (`frente`): guía de diseño

La ficha del tema está en `docs/diseno/PLAN_TEMAS.md`, lote 10c. Esta guía baja la ficha a valores concretos para que cada parte del tema (y cada ayudante que la programe) siga la misma línea. Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota.

## La idea, en una frase

Una red clandestina contra el Directorio, un régimen que gobierna una galaxia. Todo se ve como la ciencia ficción de los años 70 hecha a mano: consolas curvas, pantallas redondas con trazos de radar, palancas de baquelita, pilotos ámbar, cartuchos de datos, mapas estelares impresos. Se vive desde el agente de la resistencia. Es analógico, pesado, gastado y tenso, sin brillo de neón ni pantallas planas.

**La base escondida en ruinas** (pedido del autor del 15 nov 2026, con sus imágenes de referencia). La sala de mando de la red está metida en unas ruinas antiguas de piedra:
- **el lugar:** muros de sillares viejos con raíces que cuelgan, techo de losa con manchas de humedad, hormigón y cables gruesos tirados por el piso, cajas de equipo apiladas, franjas de peligro amarillas y negras en el suelo;
- **la luz:** penumbra verdosa; lámparas de cúpula blanca y el resplandor de las pantallas;
- **las pantallas de vidrio grabado:** paneles de vidrio en un marco de base oscura, iluminados por el canto, con trazos rectos, curvas, órbitas y espirales que brillan en verde turquesa. Son la gran novedad del tema;
- **las consolas:** unas oscuras, de verde casi negro, con filas de botones cuadrados y pilotos; y la **consola blanca curva** con sus molduras redondas, como un mostrador anillado;
- **el refugio de cada uno:** una cabina estrecha de paredes blancas curvas, con un sillón color óxido y una manta, libros apilados y pantallas de tubo con texto. Es lo único cálido de la base.

Se toma la estética, no los diseños concretos de ninguna película: ni el templo ni la mesa de mando de la saga.

## Lo que no se toma de la saga que lo inspira

La inspiración es *Andor* (Star Wars) y las fotos de diseño de producción que mandó el autor, pero solo como **género**. Nunca:
- nombres de la saga (planetas, personajes, el Imperio, la Rebelión o la Alianza como nombres propios);
- la insignia rebelde (el pájaro de fuego) ni el engrane imperial;
- naves triangulares, cazas de alas hexagonales, soldados de armadura blanca;
- su alfabeto inventado, sus sonidos (el zumbido de los cazas, las espadas de luz) ni su música.

El régimen se llama **el Directorio**. La red, **el Frente**. Las células se nombran con una letra y un número («célula K-7»).

## Claro y oscuro: el mismo mundo

| Papel | Claro (la base con las lámparas encendidas) | Oscuro (la misma base en penumbra) |
|---|---|---|
| fondo | `#DADBD2` hormigón con un poco de verde | `#0E1614` verde casi negro de la piedra en sombra |
| superficie | `#ECEBE4` consola blanca | `#15201D` |
| variante | `#C9CBC1` | `#1D2A26` |
| borde | `#979B91` | `#33433E` |
| tinta | `#1E2523` verde negro | `#DCE5DF` |
| tenue | `#535B57` | `#92A29B` |
| primario | `#B5561C` óxido del sillón y de las lámparas | `#EE9A4A` ámbar encendido |
| sobre primario | `#FFF7EF` | `#2A1300` |
| contenedor | `#F1D9C6` | `#4A2A10` |
| sobre contenedor | `#2E1406` | `#FBE2C6` |
| secundario | `#2C5A52` verde del vidrio apagado | `#86D9C2` vidrio iluminado |
| contenedor secundario | `#C6DAD3` | `#1E3A33` |
| terciario | `#3E8C76` | `#6FD1B4` |
| error | `#A8201A` rojo de botón | `#FF7A6E` |

Colores de dibujo que se repiten en las figuras (`TrazosFrente`, en `ui/FrenteTrazos.kt`):
- **consola blanca** `#E6E5DD` y **consola oscura** `#1E2A26`;
- **piedra** `#8C8F84` (en sombra `#3B4440`) y **raíz** `#4A4636`;
- **vidrio grabado**: el trazo `#8FF0D2` con su halo, sobre el vidrio `#123A33` (a la luz, `#2F6F62`);
- **baquelita** `#2A2622` y **cromo gastado** `#B9B6AE`;
- **trazo de radar** `#9FD8A8` sobre la pantalla `#0F1A16`;
- **piloto rojo** `#D9372B` y **piloto ámbar** `#F2A33A`;
- **franja de peligro**: amarillo `#D9A62B` y negro;
- **óxido del sillón** `#A44A1C`;
- **mármol del Directorio** `#F3F1EC`, con su **bronce** `#9A7442`, solo para lo que es del régimen.

Paleta corta: piedra verdosa, consola blanca y oscura, el verde turquesa del vidrio y el ámbar y el óxido como lo único cálido. Nada de azul puro ni de magenta, que son de Ciudad neón; el turquesa del vidrio es verdoso y suave, nunca el cian brillante de Terminal.

## Letra y forma

- **Títulos:** Michroma (OFL; la ancha de la ciencia ficción de los 70), en mayúsculas.
- **Texto:** Chivo (OFL), grotesca firme.
- **Etiquetas y cifras:** Chivo Mono (OFL), como el rótulo grabado de una consola.
- **Esquinas:** `Esquinas(13, Esquina.REDONDA)`, la curva grande de las consolas de la base.

## La voz

Seca, operativa y en clave, como un radiooperador que no quiere que lo oigan. Frases cortas. Nunca grandilocuente. Tutea.

- **Fecha:** el nombre en clave del día («OPERACIÓN GARZA»), sacado de una lista propia de palabras y siempre el mismo para la misma fecha, más la ventana de transmisión («VENTANA 21:40»).
- **Números:** con la letra de las cifras (Chivo Mono), como en una pantalla de segmentos. Las tres letras del tema no traen los signos geométricos (▮ ● ○ ◆): no se usan en sus textos. No se usa «JORNADA · AÑO», que es de Distopía.
- **Ejemplos:** «Despacho recibido», «Transmisión cerrada», «Sin novedad en la red», «Canal limpio», «Cifrado».
- **Lo que nunca dice:** «rebelde», «imperio», «la fuerza», «galaxia muy lejana», ni ningún nombre de la saga.

## Los motivos de las figuras

- **El emblema:** un círculo roto arriba a la derecha (el hueco mira a −50°), con un punto en el centro, la célula, del que salen tres trazos rectos que se abren y se escapan por el hueco: la señal. No es un rayo en zigzag (eso sería el signo de la electricidad). Lo dibuja `TrazosFrente.emblemaRed` (`ui/FrenteTrazos.kt`).
- **El panel de vidrio grabado** con sus trazos y órbitas iluminados: el fondo vivo.
- **La pantalla redonda** con retícula y barrido de radar.
- **El muro de piedra con raíces**: el fondo de las pantallas y de los muebles.
- **La consola:** curva, con su fila de interruptores, sus pilotos y su perilla.
- **El cartucho de datos:** la libreta; un bloque con etiqueta y muesca.
- **El mapa estelar impreso:** la campaña, con rutas de salto punteadas.
- **El parche de misión bordado:** los trofeos.
- **La nave patrulla del Directorio**, de forma propia: un bloque largo con un anillo; nunca un triángulo.

## Sonido

Relés que cierran, pitidos cortos de consola, el teletipo, el chasquido de un interruptor y un sintetizador analógico de los 70 (onda de sierra filtrada, arpegios lentos). La música es tensa y propia; no cita ninguna obra.
