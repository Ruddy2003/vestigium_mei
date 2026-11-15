# Psicodelia (`psicodelia`): guía de diseño

La ficha del tema está en `docs/diseno/PLAN_TEMAS.md`, lote 10g. Esta guía baja la ficha a valores concretos para que cada parte del tema (y cada ayudante que la programe) siga la misma línea. Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota. Las fechas, los lugares y las frases se comprueban en `docs/diseno/temas/fuentes_psicodelia.md`: lo que esa tabla no sostiene no se usa.

## La idea, en una frase

San Francisco en 1967, el verano del amor, vivido en la ciudad desde la mesa de un dibujante de carteles de conciertos: las casas victorianas (casi todas grises o desteñidas, alguna pintada de colores fuertes), la niebla de la mañana, los departamentos compartidos, el periódico del barrio con su tinta de arcoíris, la radio FM de noche y las salas de baile con sus luces líquidas. **Movimiento contenido:** el fondo respira; el caleidoscopio y las letras que se derriten salen solo en momentos (al guardar, en los logros). El resto se lee tranquilo.

## Lo que sí y lo que no

- **Sí:** lo que las fuentes fechan en 1967 (el Be-In del 14 de enero, la FM libre desde el 12 de febrero, la comida gratis del Panhandle, Monterey del 16 al 18 de junio), los carteles de las salas de baile (las letras de bloque que vienen de Alfred Roller y la Secesión de Viena, los colores complementarios que vibran), las luces líquidas (aceite y tinta entre dos vidrios del retroproyector), la música de la escena (guitarra con distorsión, sitar, órgano de transistores, eco de cinta, pandero), el símbolo de la paz (dominio público) y los textos de dominio público que se leían entonces.
- **Sí, como recurso** (se dice que es recurso): el día como disco, la hora como pista; el gato en tinta fluorescente de serigrafía; el incienso; las tiendas de discos; cómo se dibuja todo.
- **No: las drogas.** No aparecen nunca: ni en dibujos, ni en textos, ni en chistes, ni en palabras de jerga («viaje», «mal viaje», «ácido»…). La app lleva un seguimiento privado de impulsos y recaídas: este tema **nunca toca los registros** con su estética (una racha que se rompe no es «un mal viaje»). Lo que hay que dejar fuera está en la sección 9 de las fuentes. El nombre «Psicodelia» se queda como nombre del estilo artístico y la app no lo explica.
- **No:** marcas comerciales (los nombres de las salas, de la radio y del festival van por su calle o su frecuencia: «la sala de la calle Sutter», «la FM del 106.9»; la camioneta pintada sin marca; la lámpara de cera, no «de lava»), canciones, letras de canciones, carteles reales copiados, caras de músicos reales, calles enteras de casas de colores (eso es de los 70) ni nada de Ciudad neón (letreros, rejillas, ciencia ficción).

## Claro y oscuro: el mismo mundo

La misma ciudad. De día, la calle en verano: la niebla de la mañana, el sol de la tarde, las casas grises con alguna pintada, los colores saturados de los carteles pegados en los postes. De noche, la misma ciudad dentro de la sala de baile: la luz negra hace brillar el verde limón, el magenta y el violeta sobre el negro, y las luces líquidas respiran en las paredes. **Se separa de Ciudad neón** por las formas (orgánicas, blandas, que se derriten; nunca letreros ni rejillas) y por el color principal: aquí el oscuro lo lleva el verde limón de la luz negra; el magenta va segundo.

| Papel | Claro (la calle de día) | Oscuro (la sala de baile con luz negra) |
|---|---|---|
| fondo | `#F2ECE3` la niebla de la mañana | `#120A1C` el negro violeta de la luz negra |
| superficie | `#FAF6F0` | `#1A1126` |
| variante | `#E6DED2` | `#251935` |
| borde | `#B9AC9D` | `#43335A` |
| tinta | `#2A1C32` | `#F2EAF7` |
| tenue | `#61546A` | `#B4A7C4` |
| primario | `#B0246A` magenta de cartel | `#C9F04A` verde limón fluorescente |
| sobre primario | `#FFF4F8` | `#1C2600` |
| contenedor | `#F6D3E4` | `#33410F` |
| sobre contenedor | `#3D0624` | `#ECFFB8` |
| secundario | `#2E7344` verde complementario | `#FF5CBE` magenta fluorescente |
| contenedor secundario | `#D2EBD5` | `#4F1640` |
| terciario | `#A84B0A` naranja de cartel | `#AE91FF` violeta de luz negra |
| error | `#B3261E` | `#FF8A80` |

Contraste sobre el fondo (WCAG): tinta 13.6 y 16.5; tenue 6.0 y 8.5; primario 5.4 y 14.8; secundario 4.9 y 6.9; terciario 4.9 y 7.6; error 5.6 y 8.5. Sobre primario 5.9 y 12.1; sobre contenedor 12.3 y 10.3.

Colores de dibujo (`TrazosPsicodelia`, en `ui/PsicodeliaTrazos.kt`):
- **los complementarios que vibran** (como en los carteles: un color junto a su opuesto de igual valor): magenta `#E0218A` con verde `#2FBF71`; naranja `#FF7A1A` con azul `#2F6BFF`; amarillo `#FFD23F` con violeta `#7B3FD1`;
- **las casas**: el gris de la madera desteñida `#A9A49B` y el blanco gastado `#E4DED3`; las pocas pintadas, en turquesa `#2BB3A8`, rosa `#F28DB2` y amarillo `#F2C14E`;
- **la niebla** `#E9EDF0` y **el cielo** de la tarde `#F7C99B`;
- **la luz negra**: verde limón `#C9F04A`, magenta `#FF5CBE`, violeta `#AE91FF` y naranja fluorescente `#FF9F43`, sobre `#120A1C`;
- **las luces líquidas**: gotas de aceite que se mezclan, en magenta, ámbar y verde con bordes que se funden;
- **el papel del periódico** `#EFE7D6` y su **tinta de arcoíris** (el tintero partido: del magenta al amarillo al azul en una sola pasada).

## Letra y forma

- **Títulos:** Modak (OFL), letra burbuja gruesa, como las letras infladas de los carteles. Donde el título sea grande (la presentación, el logro), puede dibujarse ondulando o derritiéndose, solo en ese momento.
- **Texto:** Fredoka (OFL), redonda y legible, de un ancho normal.
- **Rótulos y cifras:** Fredoka en negrita y un poco más ancha.
- **Signos que se les agregan** (copiados de Noto Sans Symbols y Noto Sans Symbols 2, OFL, en `herramientas/fuentes.py`): ✿ ☼ ♪ ♫ ☮ ← → ↑ ↓. Traen además · • — – « » ’ … ~ ¿ ¡ y las letras del español. **No** traen ● ○ ◆ ★ ✦ ※ ≈ ∞ en Fredoka: no se usan en textos.
- **Esquinas:** `Esquinas(18, Esquina.REDONDA)`: la burbuja, todo blando.

## La fecha y los números

- **El día como disco:** cada día del año es un disco: «Disco 290 · sábado 17 de octubre». El día de hoy lleva además la cara y la pista: el lado A de las 0 a las 11 y el lado B de las 12 a las 23; la pista es la hora dentro de su lado (de la 1 a la 12): a las 21 h, «lado B, pista 10». **Recurso.**
- **El mes:** «Álbum de octubre», y con el año, «Álbum de octubre de 2026».
- **Números:** entre tildes que ondulan: «~17~». Los años, tal cual.
- **Textos en inglés** solo en las frases del día (de dominio público, con su español al lado) y en rótulos muy cortos que se entienden; lo demás, en español (principio 7).

## La voz

Cálida, abierta, de barrio y de amigos; un poco soñadora, sin jerga de drogas. Tutea. Habla de la calle, de la niebla, del cartel de la semana, de la radio de noche, del baile, de la gente.
- **Ejemplos:** «Pon el disco del día», «Lado B», «Sale el cartel de la semana», «La niebla entra por el puente», «Paz», «Suena la radio de noche».
- **Las frases del día:** las 33 de `fuentes_psicodelia.md`, sección 10 (Blake, Whitman, Thoreau, Emerson, Gibran, Kabir en la traducción de Tagore, el *Tao Te Ching* y la *Gita* en traducciones viejas), en inglés con su español al lado.
- **Lo que nunca dice:** nada de drogas, nada de jerga («viaje», «flipar», «colocado»), nada que convierta una racha rota o un registro privado en chiste; nada de nombres de bandas ni de canciones.

## Los motivos de las figuras

- **El emblema:** una flor de pétalos redondos que vibra en colores complementarios (magenta sobre verde, naranja sobre azul), con el símbolo de la paz de una línea si cabe.
- **Las luces líquidas:** gotas que se mezclan y respiran.
- **El cartel de concierto:** letras de bloque que llenan su forma, colores que vibran (propio, sin copiar ninguno).
- **La casa victoriana** de San Francisco, gris, con alguna pintada; la niebla; el tranvía de cable.
- **El disco de vinilo** con su etiqueta, la radio de válvulas, la guitarra, el sitar, el pandero.
- **El periódico del barrio** con su tinta de arcoíris.
- **El símbolo de la paz** (Holtom, 1958; dominio público), la flor, los botones de solapa, la cinta para la frente, la lámpara de cera.

## El fondo y los muebles

- **El fondo de pantalla:** de día, un papel de cartel con una mancha de tinta de arcoíris muy suave en un borde; de noche, la pared de la sala de baile con las luces líquidas tenues.
- **Fondo vivo:** las luces líquidas que se mezclan y respiran despacio, más tenues de día. Nada que parpadee.
- **Los muebles del Diario** (cuando le toque): el cuarto del departamento compartido: la mesa de dibujo con el cartel de la semana, el tocadiscos y los discos, la radio, los carteles en la pared, la ventana con la niebla y la calle.

## Sonido

Guitarra con distorsión para los logros, el sitar, el órgano de transistores, el eco de cinta y el pandero, con nombres genéricos. La música es propia y no cita ninguna canción.

## Se distingue de

- **Ciudad neón:** ciencia ficción, letreros y rejillas; aquí, formas orgánicas, y el verde limón de la luz negra manda en el oscuro.
- **Art nouveau:** la misma curva (los carteles de 1967 la tomaron de Viena), pero allí tranquila; aquí vibra.
- **Refugio:** el optimismo de los 50; aquí, la contracultura de fines de los 60.
- **Soviético y Revolución francesa:** cartel de Estado o de revolución; aquí, cartel de concierto.
- **Art déco:** el cabaret de los 20.
