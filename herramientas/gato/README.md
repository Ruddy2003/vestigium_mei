# El gato de la banda de Día

Estos scripts dibujan el gato en pixel art de la banda de Día (80 dp arriba de la pantalla) y lo exportan a
`app/src/main/assets/gato/`. La app no dibuja el gato, solo compone lo que viene de aquí (`ui/Gato.kt`).

```
python3 herramientas/gato/exportar.py app/src/main/assets/gato            # todos los temas (unos 20 min)
python3 herramientas/gato/exportar.py app/src/main/assets/gato romano     # solo uno
```

Necesita Pillow y numpy, y las fuentes DejaVu en `/usr/share/fonts/truetype/dejavu/`.

## Cómo están hechos

Son las pruebas que se fueron revisando una tras otra. Cada archivo importa los anteriores y cambia solo lo suyo:

| Archivo | Qué pone |
|---|---|
| `gato8.py` | El gato: sprite de 64×64 armado con formas, sus poses y animaciones, los 21 temas con su pelaje, accesorio y material (teselas, vidriera, pan de oro, pantalla, punto de cruz, dos tintas…), los primeros fondos y la máquina de estados. |
| `gato9.py` | Diez fondos rehechos (y sus versiones de noche) y lo que se mueve en ellos. |
| `gato10.py` | Laboratorio, tierras antiguas y el tricornio de colonos. |
| `gato11.py` | Las patas traseras en curva suave. |
| `gato12.py` | El casco de legionario y el grabado de colonos (la gaceta). |
| `gato13.py`, `gato15.py` | Colonos con menos detalle en el fondo, el gato grabado completo y el soviético simple. |
| `gato16.py` | El casco romano más grande y de acero más oscuro. |
| `gato17.py` | Las dos animaciones propias de cada tema (13 comunes + 2 = 15) y sus objetos. |
| `gato18.py` | Arreglos antes de pasarlo a la app: la espada, el ornitóptero, objetos más grandes y el fondo de noche de los temas que no lo tenían. |
| `gato19.py` | Lote 9a: los gatos que ya había, completos: fondos que se mueven en los que estaban quietos, noches dibujadas como noche (no recoloreadas), días con más detalle, el gato de noche con sus colores en Al-Ándalus, Botánico, Orden, Renacimiento y Seda, objetos propios (tetera, azahar, campana, gorro frigio) y Terminal en cian con su consola y el cursor. Sus temas pasan por el camino de fondos de gato9 (`NUEVOS`, `ANIM`). |
| `gato20*.py` | Lote 9b: los gatos de los temas nuevos, uno o varios archivos (cada uno registra sus temas en la cadena). |
| `gato20a.py` | Pirata (grabado sepia de carta náutica, pañuelo y parche, la cubierta), Ciudad neón (gato negro con contorno de luz que se suma como pantalla, impermeable con capucha, el callejón del puesto de fideos) y Roma de hoy (fresco sobre estuco, pañuelo al cuello, el vicolo con la cúpula y las ruinas). |
| `gato20b.py` | Templario (esmalte heráldico, sobreveste con cruz y malla; la muralla de frontera y el campamento), Egipto (pintura de tumba, mau con usekh y arete; el Nilo y la tumba) y Mesoamérica (códice sobre amate, jaguarundi con jade y pluma; la pirámide en la selva y Venus con la fogata). |
| `gato20c.py` | Ciudad verde (tricolor a la acuarela con musgo, collar de cuerda con hoja; calle con hiedra, luciérnagas), Horror cósmico (gato negro en aguatinta verde-negra, amuleto; faro en la niebla, tentáculo) y Abismo (casco de buzo de latón; de noche bioluminiscente, se suma como luz; arrecife y zona abisal con medusas). |
| `gato20d.py` | Japón Edo (ukiyo-e con bokashi, bobtail mi-ke con cascabel), Prehistoria (pintura rupestre, collar de conchas), Vapor victoriano (plano técnico y latón, gafas y chaleco) y Art déco (laca negra con oro, pajarita y perlas): pelaje, material, accesorio, fondos de día y de noche y sus 2 acciones. `python3 gato20d.py hoja|fondos|bandas [tema…]` deja las hojas en `salida/`. |
| `gato21.py` | Lote 10: los gatos de los cinco temas que siguen. Primera tanda, Frente exterior: gris pizarra pintado como un cuadro de concepto de los 70 (gouache sobre cartón: pincelada, luz cálida arriba, rebote frío abajo, grano), chaqueta de vuelo con cuello de borrego y auricular con micrófono; el fondo es la base escondida en ruinas (sillares con raíces, losa con humedad y una rendija, tubos de cobre, paneles de vidrio grabado con trazos turquesa, consolas oscuras con radar, la consola blanca curva, lámparas de cúpula, cajas, cables y la franja de peligro), de noche en penumbra verdosa. Se mueven el pulso de luz del vidrio, los pilotos, el radar, las pantallas, las raíces y, una vez por bucle, el escáner que entra por la rendija. Esconde un microfilm en el collar y se agacha cuando el escáner entra por la rendija del techo. Segunda tanda, Arcontes: gato negro con ojos de luz pintado a la encáustica (como los retratos de El Fayum: toques de cera que siguen la forma, el brillo de la cera y un filo violeta de noche), con el amuleto de Abraxas (jaspe verde grabado); el fondo es la orilla de Magdala y el lago de Galilea (la casa del salador, barcas con vela, la otra orilla de caliza, la palmera, el sol con su dorado de más), de noche con la lámpara, las esferas apenas y una grieta de luz blanca y violeta. Rasga el velo con la pata (se abre una raja de luz) y eriza el lomo ante una sombra que pasa. También Galia: gato montés pardo atigrado (raya del lomo, cola gruesa con anillos y la punta negra y roma) dibujado como un repujado de plata a la manera del caldero de Gundestrup (la lámina empujada, las rayas cinceladas y ennegrecidas, el punteado del punzón, lo pardo con oro gastado, la pátina en lo hondo y los destellos del metal; recurso del dibujo), con un torques de oro; el fondo es el oppidum arverno en su meseta (el murus gallicus con las cabezas de las vigas, la puerta con su torre, los techos de paja con su humo), los puys, la niebla de la mañana, el río, el robledal y el roble grande con su muérdago, el fuego con el caldero de bronce y, lejos, el campamento romano; de noche, la luna, las hogueras del oppidum y, en el llano, las del cerco romano en fila. Se mueven el humo, la niebla, las hojas, el agua, el fuego y sus chispas, dos cuervos y las hogueras lejanas. Se esconde entre el muérdago de un roble caído y le bufa a un casco romano tirado en el río. También Art nouveau: gato atigrado crema dibujado como un cartel litografiado de 1900 (pocas tintas planas de borde curvo, las luces de papel, una sombra violeta encima, el crachis en los medios tonos y la línea de pluma, más gruesa del lado de la sombra; el pintor toma las formas del dibujo a 8× y no de la cuadrícula), con un lazo de seda malva; el fondo es un invernadero de hierro y vidrio a la manera de los de Auteuil (paños en escama, friso de vidrio de colores, arcos con ménsulas de línea de látigo, la luz en franjas, la kentia, el estanque con nenúfares, los lirios, tres lámparas de vidrio de colores; adelante, el helecho arborescente y el plátano), de noche con las lámparas encendidas y la luna tras el vidrio. Se mueven las hojas, el reflejo del vidrio, una libélula (de noche, polillas en las lámparas), la luz de las lámparas y el agua. Persigue una libélula y se enrosca dibujando la línea de látigo. Y Psicodelia: gato atigrado naranja en tinta fluorescente de imprenta (tintas planas que vibran en complementarios, contorno de cartel, trama de puntos, registro corrido; de noche, bajo la luz negra), con gafas redondas de cristal rosa y una cinta en la frente; el fondo es una calle de San Francisco en 1967 (casas victorianas casi todas grises con dos pintadas, la calle que sube la cuesta en la niebla, el poste con carteles y los cables del trolebús), de noche con el farol, un mirador con luz negra y otro con luces líquidas. Se mueven la niebla, los carteles, una gaviota, las luces líquidas y las polillas del farol. Se queda hipnotizado mirando una lámpara de cera (no la forma de la de lava) y persigue las burbujas del show de luces. `python3 gato21.py hoja\|fondos\|bandas` deja las hojas en `salida/`. |
| `exportar.py` | Lo convierte en recursos para la app. Carga `gato19` y los `gato20*` y `gato21*` que existan. |

Cada script también se puede correr solo para ver su parte (hojas de sprites, bandas en GIF); lo que hace queda en
`salida/`, que no se sube.

## Qué exporta

Por cada tema y modo (`<tema>_c` claro, `<tema>_o` oscuro; los de `SOLO_OSCURO` en `exportar.py`, solo oscuro):

- `_gato.webp`: atlas con todos los cuadros del gato ya pintados con el material del tema, recortados.
- `_fondo.webp`: el fondo de la banda, a 3 px por dp (1236×240).
- `_mov.bin`, `_atras.webp`, `_delante.webp`: si el fondo se mueve (humo, banderas, nieve, pantallas…), el bucle
  en mosaicos de 2 dp: para cada cuadro, solo los mosaicos que cambian, detrás o delante del gato.
- `_luz<n>.webp`: la luz de vela de colonos de noche, que se multiplica encima de todo.
- `_post.webp`: en las pantallas (ciber, refugio de noche), las líneas de escaneo y la viñeta.
- `gato.json`: dónde está cada cuadro en el atlas, las animaciones propias del tema y cómo se compone la banda
  (sombra, si los pixeles del gato se suman como luz de pantalla, si se mueve de dp en dp).

Si cambias el dibujo, vuelve a exportar y revisa las capturas `gatosClaro` y `gatosOscuro` de Paparazzi.
