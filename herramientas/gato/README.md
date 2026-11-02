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
| `gato19.py` | Lote 9a: los gatos que ya había, completos (fondos que se mueven, noches propias, objetos propios, Terminal en cian). |
| `gato20*.py` | Lote 9b: los gatos de los temas nuevos, uno o varios archivos (cada uno registra sus temas en la cadena). |
| `gato20a.py` | Pirata (grabado sepia de carta náutica, pañuelo y parche, la cubierta), Ciudad neón (gato negro con contorno de luz que se suma como pantalla, impermeable con capucha, el callejón del puesto de fideos) y Roma de hoy (fresco sobre estuco, pañuelo al cuello, el vicolo con la cúpula y las ruinas). |
| `gato20b.py` | Templario (esmalte heráldico, sobreveste con cruz y malla; la muralla de frontera y el campamento), Egipto (pintura de tumba, mau con usekh y arete; el Nilo y la tumba) y Mesoamérica (códice sobre amate, jaguarundi con jade y pluma; la pirámide en la selva y Venus con la fogata). |
| `gato20c.py` | Ciudad verde (tricolor a la acuarela con musgo, collar de cuerda con hoja; calle con hiedra, luciérnagas), Horror cósmico (gato negro en aguatinta verde-negra, amuleto; faro en la niebla, tentáculo) y Abismo (casco de buzo de latón; de noche bioluminiscente, se suma como luz; arrecife y zona abisal con medusas). |
| `gato20d.py` | Japón Edo (ukiyo-e con bokashi, bobtail mi-ke con cascabel), Prehistoria (pintura rupestre, collar de conchas), Vapor victoriano (plano técnico y latón, gafas y chaleco) y Art déco (laca negra con oro, pajarita y perlas): pelaje, material, accesorio, fondos de día y de noche y sus 2 acciones. `python3 gato20d.py hoja|fondos|bandas [tema…]` deja las hojas en `salida/`. |
| `exportar.py` | Lo convierte en recursos para la app. Carga `gato19` y los `gato20*` que existan. |

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
