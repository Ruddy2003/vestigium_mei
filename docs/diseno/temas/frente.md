# Frente exterior (`frente`): guía de diseño

La ficha del tema está en `docs/diseno/PLAN_TEMAS.md`, lote 10c. Esta guía baja la ficha a valores concretos para que cada parte del tema (y cada ayudante que la programe) siga la misma línea. Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota.

## La idea, en una frase

Una red clandestina contra el Directorio, un régimen que gobierna una galaxia. Todo se ve como la ciencia ficción de los años 70 hecha a mano: consolas curvas color hueso, pantallas redondas con trazos de radar, palancas de baquelita, pilotos ámbar, cartuchos de datos, mapas estelares impresos. Se vive desde el agente de la resistencia. Es analógico, pesado, gastado y tenso, sin brillo de neón ni pantallas planas.

## Lo que no se toma de la saga que lo inspira

La inspiración es *Andor* (Star Wars) y las fotos de diseño de producción que mandó el autor, pero solo como **género**. Nunca:
- nombres de la saga (planetas, personajes, el Imperio, la Rebelión o la Alianza como nombres propios);
- la insignia rebelde (el pájaro de fuego) ni el engrane imperial;
- naves triangulares, cazas de alas hexagonales, soldados de armadura blanca;
- su alfabeto inventado, sus sonidos (el zumbido de los cazas, las espadas de luz) ni su música.

El régimen se llama **el Directorio**. La red, **el Frente**. Las células se nombran con una letra y un número («célula K-7»).

## Claro y oscuro: el mismo mundo

| Papel | Claro (la sala de día) | Oscuro (la misma sala de noche) |
|---|---|---|
| fondo | `#E4DED1` hueso cálido | `#121518` pizarra con un poco de petróleo |
| superficie | `#F0EBE0` panel de consola | `#1A1F23` |
| variante | `#D4CCBC` | `#252B30` |
| borde | `#A69E8E` | `#3A4249` |
| tinta | `#23252A` pizarra casi negra | `#E7E1D4` hueso |
| tenue | `#5A5D63` | `#A8A49A` |
| primario | `#B85C0F` ámbar quemado de piloto | `#F0A04B` ámbar encendido |
| sobre primario | `#FFF8EE` | `#2A1600` |
| contenedor | `#F4DDBF` | `#4A2C0C` |
| sobre contenedor | `#2B1A08` | `#FBE3C4` |
| secundario | `#3E4C57` acero de consola | `#9FB4C0` |
| contenedor secundario | `#CBD3D8` | `#23323B` |
| terciario | `#4F7A5A` verde de pantalla de radar | `#8FC79A` |
| error | `#A8201A` rojo de botón | `#FF7A6E` |

Colores de dibujo que se repiten en las figuras:
- **baquelita**: negro cálido `#2A2622`;
- **cromo gastado**: `#B9B6AE`;
- **trazo de radar**: verde pálido `#9FD8A8` sobre la pantalla `#0F1A16`;
- **piloto rojo** `#D9372B` y **piloto ámbar** `#F2A33A`;
- **mármol del Directorio** `#F3F1EC`, con su **bronce** `#9A7442`, solo para lo que es del régimen.

Paleta corta: hueso, pizarra, ámbar, y el verde del radar como acento. Nada de azul puro, de magenta ni de cian brillante: esos son de Ciudad neón y Terminal.

## Letra y forma

- **Títulos:** Michroma (OFL; la ancha de la ciencia ficción de los 70), en mayúsculas.
- **Texto:** Chivo (OFL), grotesca firme.
- **Etiquetas y cifras:** Chivo Mono (OFL), como el rótulo grabado de una consola.
- **Esquinas:** `Esquinas(13, Esquina.REDONDA)`, la curva grande de las consolas de la sala de control.

## La voz

Seca, operativa y en clave, como un radiooperador que no quiere que lo oigan. Frases cortas. Nunca grandilocuente. Tutea.

- **Fecha:** el nombre en clave del día («OPERACIÓN GARZA»), sacado de una lista propia de palabras y siempre el mismo para la misma fecha, más la ventana de transmisión («VENTANA 21:40»).
- **Números:** con la letra de las cifras (Chivo Mono), como en una pantalla de segmentos. Las tres letras del tema no traen los signos geométricos (▮ ● ○ ◆): no se usan en sus textos. No se usa «JORNADA · AÑO», que es de Distopía.
- **Ejemplos:** «Despacho recibido», «Transmisión cerrada», «Sin novedad en la red», «Canal limpio», «Cifrado».
- **Lo que nunca dice:** «rebelde», «imperio», «la fuerza», «galaxia muy lejana», ni ningún nombre de la saga.

## Los motivos de las figuras

- **El emblema:** un círculo roto arriba a la derecha (el hueco mira a −50°), con un punto en el centro, la célula, del que salen tres trazos rectos que se abren y se escapan por el hueco: la señal. No es un rayo en zigzag (eso sería el signo de la electricidad). Lo dibuja `TrazosFrente.emblemaRed` (`ui/FrenteTrazos.kt`).
- **La pantalla redonda** con retícula y barrido de radar, que es el fondo vivo.
- **La consola:** curva, con su fila de interruptores, sus pilotos y su perilla.
- **El cartucho de datos:** la libreta; un bloque con etiqueta y muesca.
- **El mapa estelar impreso:** la campaña, con rutas de salto punteadas.
- **El parche de misión bordado:** los trofeos.
- **La nave patrulla del Directorio**, de forma propia: un bloque largo con un anillo; nunca un triángulo.

## Sonido

Relés que cierran, pitidos cortos de consola, el teletipo, el chasquido de un interruptor y un sintetizador analógico de los 70 (onda de sierra filtrada, arpegios lentos). La música es tensa y propia; no cita ninguna obra.
