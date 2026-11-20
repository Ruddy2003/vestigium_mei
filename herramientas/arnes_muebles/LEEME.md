# Arnés de los muebles

Dibuja en PNG, en esta máquina y sin Android, los muebles de la pestaña Diario: el mueble del Ideario, el estante del diario, el libro del día y la libreta de momentos con su índice. Usa los archivos reales de la app, enlazados desde `app/src/main` (no copiados), y los composables reales `MuebleIdeas` y `EstanteTema`, con los mismos datos de ejemplo que las capturas de Paparazzi (`Capturas.kt`). Sirve para redibujar un mueble y ver el resultado en segundos, sin esperar a CI.

## Uso

```
herramientas/arnes_muebles/dibujar.sh ideario <tema> <claro|oscuro> <salida.png>
herramientas/arnes_muebles/dibujar.sh biblioteca <tema> <claro|oscuro> <salida.png>
herramientas/arnes_muebles/dibujar.sh hoja <ideario|biblioteca|galeria|tapa|libreta> <claro|oscuro> <salida.png> <tema1,tema2,...>
herramientas/arnes_muebles/dibujar.sh tapa <tema> <claro|oscuro|hoja> <salida.png>
herramientas/arnes_muebles/dibujar.sh libreta <tema> <claro|oscuro|hoja> <salida.png>
herramientas/arnes_muebles/dibujar.sh trazos psicodelia <claro|oscuro> <salida.png>
herramientas/arnes_muebles/dibujar.sh comun psicodelia <claro|oscuro> <salida.png>
herramientas/arnes_muebles/dibujar.sh logros psicodelia <claro|oscuro> <salida.png>
```

- `<tema>` es el id de `TemaId` (`heleno`, `gotico`, `laboratorio`, `galia`…). Con un id que no existe, el script dice cuáles hay.
- `ideario`: lo mismo que la captura `temaMueble` «_b»: `FicheroIdeas` con 9 temas (`ideasConTemas(9)`), con 16 dp de margen.
- `biblioteca`: lo mismo que el estante de la captura `temaBibliotecas`: las 35 entradas de `Ejemplo.cargar()` repartidas por mes, con su rótulo (`Estilo.mesConComun`). Solo el estante, con 16 dp de margen a los lados.
- `hoja`: varios temas lado a lado, cada uno con su nombre encima.
- 1080 px de ancho y 2.625 px por dp, como el Pixel 6 de Paparazzi. El alto es el que ocupa el mueble.
- Fondo: el color `background` del esquema del tema, liso (sin la textura de `fondoTema`).
- `ARNES_HOY=AAAA-MM-DD` fija el «hoy» de los ejemplos (por omisión, el día lógico de hoy). Cambia las fechas de las entradas y, con ellas, cómo se reparten en el estante. Para comparar con una captura de CI, pon el día en que corrió; las de noviembre de 2026 usan `ARNES_HOY=2026-11-15`.
- `ARNES_TEMAS=0,1,5` dibuja un Ideario por cada número de temas, uno bajo otro (es la captura `temaMueble` «_a»: sin temas, con uno y con cinco); por omisión, 9 (la «_b»). `ARNES_MAX=6` corta el Ideario a ese número de compartimentos y pone la placa «y N más» (la «_c»). **Todo Ideario nuevo se dibuja también así:** un mueble que solo se probó con 9 temas puede colgarse o romperse con 0.
- `ARNES_SUENOS=1` dibuja la biblioteca como estante de los Sueños: es el `noche` de `EstanteTema`, aparte del claro y el oscuro (la luna en lugar del sello y la luz propia que cada tema enciende ahí). Lleva los 14 sueños de la captura `temaSuenos` (`suenosEjemplo` de `Capturas.kt`, uno cada dos días desde ayer): solo los días con sueño, cada volumen con el ancho de su texto y el color de su valencia. `medir.sh` también la acepta.
- `ARNES_CARA=base|intensa` y `ARNES_INTENSIDAD=0..1` fijan la cara y la intensidad de los temas que las tienen (hoy, Psicodelia: `base` es la ciudad, `intensa` la psicodelia; ver `ui/CaraTema.kt` y `docs/diseno/temas/psicodelia_base.md`). Llaman a `EstadoTema.forzar` antes de dibujar. **Por omisión, `base` y 0**, así el dibujo no depende de la hora a la que se corre (en la app, la cara la decide la hora). Las cuatro variantes de Psicodelia son `base` y `intensa`, cada una en `claro` y `oscuro`; por ejemplo, `ARNES_CARA=intensa ARNES_INTENSIDAD=1 dibujar.sh ideario psicodelia oscuro salida.png`. Los temas sin caras no cambian con ellas.
- `trazos`: una hoja de muestra de los trazos de Psicodelia (`ui/PsicodeliaTrazos.kt`, en `arnes/MuestraTrazos.kt`): con `ARNES_CARA=base`, los de la ciudad (`TrazosCiudad`: casas, poste, cables, viñeta, grano, ladrillo, persiana, letrero y los colores); con `intensa`, los de la tinta (`TrazosTinta`: rayos con trama, bandas a tres tintas con su registro, anillos, marco de arco, letras que llenan su forma y los colores), según `ARNES_INTENSIDAD`. No es un mueble: sirve para ver la base antes de usarla.
- `comun`: una hoja de muestra de las piezas de la tanda 2 de Psicodelia (`arnes/MuestraComun.kt`): todas las de `ui/comun/ComunPsicodelia.kt` a su tamaño de uso (campos en sus cinco estados, separadores, botones de texto, filas, vacío, avisos, cifras, las cuatro tarjetas, la balanza, el mes, los asientos, la hoja rápida, las relaciones, el marco de la cinta), con un texto de ejemplo encima para ver que se lee; las de `ui/PsicodeliaPiezas.kt` (emblema, banda bajo el título, subrayado, esquinas, marca de escala, botón, perilla y riel, diálogo, fondo vivo) y los ocho íconos de pestaña (`ui/IconosPestana.kt`). Según `ARNES_CARA` y `ARNES_INTENSIDAD`.
- `logros`: una hoja de muestra de los logros de Psicodelia v2 (`arnes/MuestraLogros.kt`): los nueve trofeos sobre su pedestal (`ui/logros/LogrosPsicodelia.kt`) en los cuatro metales, a 150 y a 56 dp y uno sin ganar, el pedestal y la placa del hall, el salón, el plano de la campaña (`ui/PsicodeliaPantallas.kt`) con el camino, las paradas, el trolebús y la meta, y los objetos de Tu año. Según `ARNES_CARA` y `ARNES_INTENSIDAD`.
- `ARNES_APP=/ruta/a/app/src/main` dibuja otra copia de la app, por ejemplo otro árbol de trabajo. Compila en `build-otras/<clave>/` y no toca la compilación normal.

## El modo vida

```
herramientas/arnes_muebles/dibujar.sh vida <tema> <claro|oscuro> <salida.png>
```

Dibuja, uno bajo otro, lo que cada tema pone en la pestaña Vida a través de `DisenoVida` (`ui/vida/Vida<Tema>.kt`, elegido con `disenoVida`), para cualquiera de los 39 temas:

- Social (230 dp, semilla 5), Mapas (220 dp, semilla 9) y Otras vidas (190 dp, semilla 7), cada uno con su título encima como `Recuadro` (`ui/pantallas/Galerias.kt`) y en una escena como la de la app: el mismo degradado de fondo, las mismas esquinas y, en los modos oscuros, la lámpara de la escena en Social y Otras vidas (`luzEnPared` y `luzEnMueble`, sacadas tal cual de `Escena.kt` con `piezas`). Falta la textura de la pared (`paredTema`), que el mueble tapa casi entera porque ocupa toda la escena.
- Los tres íconos de «Tus mapas» (`iconoMapa`: `unesco`, `rascar` y `lugares`), cada uno en su lienzo de 70 dp de alto dentro de una tarjeta, sin el marco del mapa (`marcoDelMapa`).
- El tablero de notas de la pestaña Diario con el composable real `TableroDeNotas` (`ui/vida/MuebleNotas.kt`) y las cuatro notas de `Capturas.notasYMetas` (una idea fijada, un propósito, un recordatorio y un pensamiento), en su escena con 10 dp de relleno.

Lo que va dentro de los nichos (el grafo de tu gente, el mapa para rascar, las medallas) son figuras de relleno con los colores del esquema, en `arnes/Vida.kt`: esas pantallas no se compilan aquí. Sirven para ver dónde cae el contenido y si se lee sobre el nicho. Las notas, en cambio, son las de verdad, con su texto.

Funcionan igual `ARNES_HOY` (la fecha de las notas), `ARNES_APP`, `ARNES_CARA` y `ARNES_INTENSIDAD`; por ejemplo, las cuatro variantes de Psicodelia y la intensidad máxima:

```
dibujar.sh vida psicodelia claro ciudad_claro.png
dibujar.sh vida psicodelia oscuro ciudad_oscuro.png
ARNES_CARA=intensa dibujar.sh vida psicodelia claro tinta_claro.png
ARNES_CARA=intensa dibujar.sh vida psicodelia oscuro tinta_oscuro.png
ARNES_CARA=intensa ARNES_INTENSIDAD=1 dibujar.sh vida psicodelia oscuro tinta_oscuro_i1.png
```

Enlaza la carpeta entera `ui/vida/**` (así un `Vida<Tema>.kt` nuevo entra solo).

## El modo galería

```
herramientas/arnes_muebles/dibujar.sh galeria <tema> <claro|oscuro> <salida.png>
herramientas/arnes_muebles/dibujar.sh hoja galeria <claro|oscuro> <salida.png> heleno,laboratorio,gotico
```

El mueble de la galería de cuadros de la pestaña Vida (parte 8, `docs/diseno/CUADROS_PLAN.md`), en `arnes/Galeria.kt`: el recuadro de 190 dp con el composable real `ParedDeCuadros` (`ui/vida/MuebleGaleria.kt`), que pone el mueble del tema (`DisenoVida.muebleGaleria`; por omisión, `MuebleGaleriaComun`), las miniaturas en sus huecos y la luz encima. Uno bajo otro con 0, 1 y 5 cuadros; `ARNES_CUADROS=0,1,5` elige cuántos. Las miniaturas son cinco paisajes de relleno pintados en el arnés (las escenas no se compilan aquí): sirven para ver cómo caen en los marcos y con la luz.

## Los modos tapa y libreta

```
herramientas/arnes_muebles/dibujar.sh tapa <tema> <claro|oscuro|hoja> <salida.png>
herramientas/arnes_muebles/dibujar.sh libreta <tema> <claro|oscuro|hoja> <salida.png>
```

Dibujan, en `arnes/TapaLibreta.kt`, los dos muebles de la pestaña Diario que faltaban, montados como en la app y con datos inventados:

- **`tapa`**: el libro del día (el diario de hoy, cerrado) con el composable real `TapaDiario` (ui/Libros.kt, sacado tal cual con `piezas`): el objeto del tema (`DisenoBiblioteca.tapa`, con `EstadoTapa`) recortado a su lienzo de `altoTapa` y, encima, el texto que pone la pantalla en `cajaTapa` (`letraTapa`, `tamTapa`, renglones de 1.28 × `tamTapa`, hasta `lineasTapa` con «…», `centradoTapa`, `tintaTapa` con alfa 0.92 si hay texto y 0.62 si es la pista, centrado en la caja a lo alto, y se desvanece al abrirse). El texto es lo escrito en un renglón, hasta 160 letras; vacío, la pista del tema (`pistaDia` o, de noche, `pistaNoche`). La fecha es la de `ARNES_HOY`.
- **`libreta`**: la pestaña Momentos de la biblioteca (`BibliotecaMomentosLibreta`, ui/pantallas/LibretaMomentos.kt) sin las pestañas de las bibliotecas de arriba: la libreta con el composable real `LibretaCerrada` (sacado tal cual), con 16 dp arriba; la ayuda; el buscador real (`CampoTema`); y el índice: por cada mes (los 3 más recientes), su rótulo (`rotuloMes`, en una franja de 34 dp con 18 dp arriba y 8 abajo) y sus días como pestañas (`pestana`, 44×56 dp, en un `FlowRow` con 6 dp entre columnas y 8 entre filas), cada una con el número del día, la letra de su semana y el color del ánimo medio de sus registros (`Tonos.valencia`; sin ánimo, `null`), como lo calcula la pantalla. Si quedan más meses, «ver más meses».
- **`hoja`** en lugar de `claro|oscuro`: el claro y el oscuro lado a lado, con los mismos estados (los temas de solo oscuro, solo el oscuro). Con el modo `hoja` de siempre (`dibujar.sh hoja tapa claro salida.png heleno,medieval,espacio`), varios temas lado a lado.
- 1080 px de ancho (411 dp) salvo con `ARNES_ANCHO`. Encima de cada estado va un rótulo chico que dice qué estado es y dónde va montado (no es de la app).

Variables (todas opcionales):

| Variable | Qué hace | Por omisión |
|---|---|---|
| `ARNES_TAPA=vacio,corto,largo,noche,noche_vacio,abierto` | Los estados de la tapa, uno bajo otro. `vacio`: sin escribir (la pista); `corto`: el texto de la captura de CI (52 palabras); `largo`: una entrada inventada de 4 808 palabras; `noche`: el sueño escrito (`EstadoTapa.noche`); `noche_vacio`: el sueño sin escribir; `abierto`: el corto a medio abrir (0.55, como en CI). | `vacio,corto,largo,noche` |
| `ARNES_MOMENTOS=0,pocos,muchos,40` | Los conjuntos de momentos de la libreta, uno bajo otro. `pocos`: los de la captura `libreta` de CI (hoy y ayer); `muchos`: 150 días hacia atrás con huecos (123 días en 6 meses, de los que se ven 3; uno de cada seis días sin ánimo); un número: tantos días seguidos; `0`: sin momentos (la pantalla solo pone el aviso de vacío; debajo, aparte, la libreta con 0 hojas como prueba). | `pocos,muchos` |
| `ARNES_MESES=N` | Cuántos meses del índice se ven (la pantalla enseña 3 y «ver más meses»). | 3 |
| `ARNES_ABIERTO=0..1` | Cuánto se ha abierto el objeto (la animación de 300 ms al tocarlo), en todos los estados. | el del estado (0) |
| `ARNES_LUGAR=diario\|suelto\|biblioteca` | Dónde va montado. Tapa: `diario` la pone en la escena de la pestaña Diario (sección «Hoy»: `Escena` con 12 dp de relleno a los lados y 14 arriba y abajo, y la lámpara de los modos oscuros), `suelto` a todo el ancho de la pantalla (Biblioteca de sueños, formulario del sueño, pantalla Día). Libreta: `diario` la pone en la escena de la pestaña Diario (sección «Libreta de momentos», sin relleno ni lámpara y sin índice); si no, la de la biblioteca con su índice. | Como en la app: la tapa de día en la escena de Diario y la de noche suelta; la libreta en la biblioteca. |
| `ARNES_ANCHO=dp` | El ancho de la pantalla en dp (de 200 a 900), para probar teléfonos angostos (360, 320). Solo en estos dos modos. | 411.4 (1080 px) |

Además valen `ARNES_HOY`, `ARNES_APP`, `ARNES_CARA` e `ARNES_INTENSIDAD`.

Ejemplos (desde la raíz del repositorio):

```
# Los estados principales de un tema, en claro y oscuro, en una sola imagen:
herramientas/arnes_muebles/dibujar.sh tapa heleno hoja tapa_heleno.png
herramientas/arnes_muebles/dibujar.sh libreta heleno hoja libreta_heleno.png
# Solo vacío y escrito, en oscuro; y el sueño sin escribir y el objeto a medio abrir:
ARNES_TAPA=vacio,corto herramientas/arnes_muebles/dibujar.sh tapa medieval oscuro tapa.png
ARNES_TAPA=noche_vacio,abierto herramientas/arnes_muebles/dibujar.sh tapa medieval claro tapa_extremos.png
# Los casos extremos: sin momentos, un teléfono de 320 dp, la entrada larguísima:
ARNES_MOMENTOS=0,1,muchos ARNES_ANCHO=320 herramientas/arnes_muebles/dibujar.sh libreta espacio claro libreta_extremos.png
ARNES_TAPA=largo ARNES_ANCHO=320 herramientas/arnes_muebles/dibujar.sh tapa espacio oscuro tapa_angosta.png
# La libreta como se ve en la pestaña Diario, abriéndose:
ARNES_LUGAR=diario ARNES_ABIERTO=0.55 ARNES_MOMENTOS=pocos herramientas/arnes_muebles/dibujar.sh libreta espacio oscuro libreta_diario.png
# Para comparar con las capturas de CI del día en que corrieron:
ARNES_HOY=2026-11-19 ARNES_MOMENTOS=pocos herramientas/arnes_muebles/dibujar.sh libreta heleno claro libreta_ci.png
# Medir la tapa y la libreta con su índice de tres temas:
herramientas/arnes_muebles/medir.sh tapa,libreta,indice heleno,medieval,espacio
```

Lo que distingue cada estado (lo que el tema recibe para dibujar):

- `EstadoTapa`: la fecha como la escribe el tema (`Estilo.fecha`), cuántas palabras, si está en blanco, si es de noche (el sueño), cuánto se ha abierto y una semilla (`fecha.hashCode()`). No lleva ánimo. El tema dibuja dentro la fecha y `palabrasTapa(n)`; el extracto o la pista los pone la pantalla encima.
- `EstadoLibreta`: el nombre del contenedor del tema («Tablillas de momentos», «Códice de momentos», «Bitácora de vuelo»), la cuenta («2 tablillas · desde el mié 18 nov»), cuántas hojas (engorda con ellas), cuánto se ha abierto y una semilla (`hojas × 31 + 7`). El tema los dibuja dentro.
- `EstadoPestana`: el número del día, la letra de la semana (L M X J V S D), el color del ánimo medio (o `null`) y una semilla (`fecha.hashCode()`). No hay pestaña «elegida» ni «hoy»: todas son iguales salvo por esos datos.
- `rotuloMes`: el texto de `Estilo.mesConComun` («ΔΙΟΣ · noviembre de 2026», «otubre · octubre de 2026», «NOV · 2026»).

En Actions, lo mismo sale con las superficies `tapa` (el libro del día: vacío, escrito y larguísimo en la escena de Diario, y el sueño suelto) y `libreta` (la pestaña Momentos de la biblioteca con los dos días de `pocos`) de `capturas.yml`.

Comparado con las capturas de CI del 19 nov 2026 (`libretaA3*_…_biblioteca`, `tapaA2*`), en Helénico, Scriptorium y Espacio, en claro y en oscuro: la libreta coincide (diferencia media de 0.3 a 0.7 sobre 255); la ayuda, el buscador y el índice, de 1.8 a 6.7 (casi todo es la textura del fondo de CI, que aquí no está); la tapa, de 3.3 a 6.7, porque el extracto es texto de Compose y aquí sale de 1 a 3 % más ancho (corta el renglón una palabra antes; ver abajo). Además, la queraía de los números griegos de Helénico («ΙΘʹ») sale con otra letra de respaldo: en el teléfono y en CI la pone la letra del sistema.

En qué difiere de la app:

- La escena de la pestaña Diario es la de `EscenaArnes` (arnes/Vida.kt): el mismo degradado, las mismas esquinas y la misma lámpara de los modos oscuros (`luzEnPared` detrás y `luzEnMueble` encima del mueble, sacadas tal cual), sin la textura de la pared (`paredTema`). Sin el título de la sección («Hoy», «Libreta de momentos») ni lo que va debajo.
- En la libreta, el aviso de vacío (`Vacio`) es solo su texto, y «ver más meses» es solo su texto (en la app, un `BotonTexto` del tema): esas piezas comunes no se compilan aquí. Faltan arriba el título y las pestañas de las bibliotecas.
- Con `ARNES_ABIERTO`, la libreta no es `LibretaCerrada` (que lo anima con un `Animatable` propio) sino la misma llamada a `libreta()` con ese valor fijo; la tapa sí es `TapaDiario`, que lo recibe.
- Sin consumos: los días de la libreta salen solo de los momentos (en la app también cuentan los días con una sustancia anotada).


```
herramientas/arnes_muebles/medir.sh [grupo,grupo,...] [tema,tema,...]
```

Mide el «peso» del dibujo (`arnes/Medir.kt`, 19 nov 2026) y saca una tabla separada por tabuladores. Grupos: `biblioteca` (el estante con las 35 entradas, armado con la escena real y un espía que anota cada llamada de `DisenoBiblioteca`), `ideario` (el mueble con 9 temas y sus compartimentos, con la cuenta de lugares de `MuebleIdeas`), `vida` (Social, Mapas y Otras vidas), `comun` (una mezcla de piezas comunes como la de una pantalla), `decoracion` (emblema, banda, subrayado y filete de Psicodelia), `fondo` (un cuadro del fondo vivo de Psicodelia), `tapa` (el libro del día: solo `DisenoBiblioteca.tapa`, recortado a su lienzo, al ancho de la escena de Diario; el estado es el primero de `ARNES_TAPA`, por omisión `corto`; el extracto es un `Text` de Compose, igual en todos los diseños, y no se cuenta), `libreta` (la libreta y su índice: `libreta`, el rótulo de cada mes visible y una pestaña por día, con el primer conjunto de `ARNES_MOMENTOS`, por omisión `muchos`: unas 65 pestañas en 3 meses) e `indice` (solo los rótulos y las pestañas). `ARNES_ABIERTO` y `ARNES_MESES` también valen aquí. Temas: por omisión Psicodelia en sus ocho variantes (las dos caras, claro y oscuro, intensidad 0, 0.5 y 1), Helénico, Laboratorio y los otros cuatro del lote 10.

- `grabar_us`: lo que tarda en correr el código de dibujo y grabarse como lista de operaciones de Skia (en el teléfono, lo que pasa en el hilo de la interfaz cada vez que la pieza se vuelve a dibujar). Mediana de hasta 200 vueltas.
- `pintar_us`: pintar esa lista en píxeles con Skia en la CPU (en el teléfono lo hace la GPU; sirve para comparar, no como cifra del teléfono).
- `ops` y `KB`: cuántas operaciones lleva la lista y cuánto ocupa. Es lo que el teléfono vuelve a pintar en cada cuadro mientras algo de la pantalla se mueve (el fondo vivo, el emblema).
- `primera_ms`: la primera vez, con las cachés vacías.
- `MEDIR_DETALLE=1`: además, cada llamada por separado (el mueble y cada volumen o compartimento).

Los tiempos varían de una corrida a otra si la máquina está ocupada; `ops` y `KB` no.

## Cuánto tarda (medido en esta máquina, 4 núcleos)

- Primera vez, desde cero (descarga de dependencias aparte): **~1 min 45 s**. Arranca el daemon de Gradle y compila unas 90 000 líneas.
- Volver a dibujar sin cambios: **3 a 9 s**. La compilación ya está al día; el resto es arrancar la JVM y dibujar. Galia es el más lento, con unos 8 s de dibujo.
- Tras editar el cuerpo de un mueble (un color o un trazo dentro de `IdeasGalia.kt` o de `BibliotecaContemporanea.kt`, 12 000 líneas): **4 a 12 s** en total. La compilación es incremental.
- Archivo nuevo (`ideario/IdeasLaboratorio.kt`): **~5 s**. Entra solo y no hay que tocar el arnés.
- Si cambia una interfaz de la que dependen todos (`DisenoIdeas`, `DisenoBiblioteca`, `Tema.kt`), Gradle recompila casi todo: **~1 min 15 s**.

## Varias llamadas a la vez

Se pueden hacer varias llamadas a la vez, desde procesos distintos, cada una con su archivo de salida:

- La compilación va bajo un candado (`flock` sobre `build/.candado`): una llamada compila y las demás esperan. Luego cada una lee el classpath dentro del candado.
- Cada llamada dibuja con una **copia del jar con nombre por su contenido** (`build/instantaneas/arnes-<hash>.jar`). Así una compilación nueva no cambia lo que otra llamada está leyendo. Las copias de más de una hora se borran.
- El PNG se escribe en un temporal y se renombra.

Probado con 3 llamadas en paralelo justo después de editar un archivo: las 3 salieron con el cambio, en 14 s en total.

## Qué enlaza (tal cual, desde `app/src/main/java/...`)

- `ui/ideario/**`, `ui/biblioteca/**` y `ui/muebles/**`: las carpetas enteras, así un archivo nuevo entra solo.
- `ui/PsicodeliaPiezas.kt`, `ui/comun/ComunPsicodelia.kt` e `ui/IconosPestana.kt` (para la muestra `comun`); `ui/logros/LogrosPsicodelia.kt` y `ui/PsicodeliaPantallas.kt` (para la muestra `logros`).
- `ui/vida/**` (modo `vida`): los muebles de Vida de los 39 temas, `DisenoVida`, `MuebleNotas` y `Nicho`.
- `ui/*Trazos.kt`, `ui/Galia.kt`, `Nouveau.kt`, `Psicodelia.kt`, `Frente.kt` y `Arcontes.kt`.
- `ui/Tema.kt` (`TemaId`, `Letra`, los esquemas, `tipoDe`, `Tonos`, `PaletaBasica`), `ui/ModoHora.kt` y `ui/CaraTema.kt` (la cara y la intensidad: `Cara`, `EstadoTema`, `Intensidad`).
- `ui/Disenios.kt` (`LetraExtra` y las escalas de color de los volúmenes).
- `ui/Estilo.kt`, junto con lo que usa para nombrar meses y fechas: `Temas.kt`, `Vapor.kt`, `CuentaMaya.kt`, `NombresNuevos.kt`, `Textos.kt`, `Claves.kt` y `ui/nombres/**`.
- `data/Modelo.kt`, `Texto.kt`, `Fechas.kt`, `Catalogo.kt`, `Especiales.kt` e `Ideario.kt`.
- Las letras de `app/src/main/res/font/`.

## Piezas sacadas de archivos reales (en cada compilación, a `build/generado`)

Algunos archivos no compilan enteros en escritorio. Para esos, Gradle copia en cada compilación solo las declaraciones que hacen falta, con su texto tal cual (`piezas` en `build.gradle.kts`). Si cambian en la app, el cambio llega solo:

| Archivo | Declaraciones |
|---|---|
| `Escena.kt` | `halo` y `luzCalida` (el aura de los objetos que se tocan) |
| `Escena.kt` | `luzEnPared` y `luzEnMueble` (la lámpara de la escena en los modos oscuros; modo `vida`) |
| `Libros.kt` | `Pagina` y `Rayas` |
| `data/Mundo.kt` | `CiudadVisitada` y `SitioMarcado` |
| `Decoracion.kt` | `piezasMano`, `cruzPatada`, `rad` y `mas` (la cruz la usa `IconosPestana.kt`) |
| `logros/DisenoLogros.kt` | el contrato `DisenoLogros` y sus clases de estado, sin `disenoLogros` |
| `comun/DisenoComun.kt` | el contrato `DisenoComun` y sus clases de estado, sin `disenoComun` (que nombra los 40 objetos) |
| `pantallas/Fichero.kt` | `cajones` y `rotuloCajon` |
| `ArchivoIdeas.kt` | `ArchivoIdeas` y `archivoIdeas` («7 óstraka») |
| `Libros.kt` | `TapaDiario`, `pistaDia` y `pistaNoche` (la tapa con su texto encima; modo `tapa`) |
| `pantallas/LibretaMomentos.kt` | `LibretaCerrada` y `pluralHoja` (la libreta con su nombre y su cuenta; modo `libreta`) |
| `Contenedores.kt` | `Contenedor` y `contenedorDe` (el nombre de la libreta y de sus hojas en cada tema) |

`R` también se genera, a partir de `res/font` y de `res/drawable*`.

## Sustitutos (escritos a mano, en `src/main/kotlin`)

- `sustitutos/Repo.kt`: `Repo`, con solo `config` y `dias`, en memoria.
- `sustitutos/Sentidos.kt`: `Sentidos.Evento` y `rememberSentir()`, que no hacen nada (vibración y sonido).
- `sustitutos/Fuentes.kt`: `Font(resId, weight, style)` de Android, que carga el `.ttf` con la API de escritorio.
- `sustitutos/Pantallas.kt` y `sustitutos/Cartas.kt`: `simboloCalificacion` y `selloCarta`, vacíos. Los muebles no los dibujan: el primero es para la ficha fijada de Helénico y el segundo para el sobre de Frente exterior.
- `sustitutos/Emblema.kt`: `Emblema`, vacío. Tema.kt lo usa en el selector de temas.
- `sustitutos/Datos.kt`: `Privado.TIPOS` y `Suplementos.INICIALES`, los valores por omisión que nombra `Modelo.kt`.
- `sustitutos/Sustancias.kt`: `Sustancias.finEfectivo` y `nivelEn`, que no hacen nada (las nombra `Intensidad`, en `CaraTema.kt`; el `Sustancias` real arrastra la base de datos). `Repo.consumos` está vacío: la intensidad se fija con `ARNES_INTENSIDAD`.
- `android/*.kt`: cascarones de `Activity`, `Context`, `Configuration`, `View`, `Window`, `WindowCompat` y `LocalView`, solo para que `Tema.kt` compile entero.
- `arnes/Dibujar.kt`: el programa. Reproduce la llamada de `FicheroIdeas` a `MuebleIdeas` y la de `BibliotecaDiario`/`EstantePorMeses` a `EstanteTema`, y en lugar de `TemaVestigium` usa `MaterialTheme(esquemaDe, tipoDe)`.
- `arnes/TapaLibreta.kt`: los modos `tapa` y `libreta`, con sus datos de ejemplo; reproduce el índice de `BibliotecaMomentosLibreta` (ver «Los modos tapa y libreta»).

## Diferencias que quedan frente a las capturas de CI

Comparado con las capturas de CI de heleno, galia y laboratorio, en claro y en oscuro, la diferencia media es de **0.6 a 1.9** por canal (sobre 255). La forma, las proporciones, los colores, el modo oscuro, el aura y las letras coinciden. Lo que sigue distinto:

- **Texto de Compose** (los rótulos del Ideario): sale de 1 a 3 % más ancho que en Paparazzi; por ejemplo, «#aprender» mide 164 px aquí y 159 px en CI. Por eso un rótulo largo se corta una letra antes («#historia de l…» en lugar de «#historia de la…»). Además, el corte con «…» puede dejar un espacio antes de los puntos («#historia …»). Se debe al motor de texto: Skia de escritorio contra el de layoutlib de Android. Cambiar el *hinting* no lo arregló. El texto dibujado con `TextMeasurer` (`textoEn`, el de los volúmenes y las placas de la biblioteca) coincide.
- **Fondo**: aquí es liso; en CI lleva la textura del tema (`fondoTema`).
- Las capturas de CI tienen un alto fijo (2600 o 3000 px) y no se desplazan. Si el mueble no cabe, pasa una de dos cosas:
  - El Ideario de Galia (2815 px) queda **aplastado**: en CI, `height(alto)` choca con el alto de la pantalla, y la base se dibuja más arriba, sobre la última fila.
  - El estante de Galia queda cortado abajo.
  El arnés los dibuja enteros, como se ven en el teléfono al desplazar. En el Ideario de Galia, la parte de abajo difiere de la de CI por esto, no por un error del arnés.

## Si deja de compilar

Puede pasar si un archivo enlazado empieza a usar algo nuevo de fuera: otro objeto de `ui/`, de `data/` o de Android. `dibujar.sh` muestra los errores con su archivo y su línea. Hay tres arreglos, en este orden de preferencia:

1. Enlazar el archivo que lo define: agregarlo a `enlazados` en `build.gradle.kts`.
2. Si ese archivo arrastra media app, sacar solo la declaración: agregarla a `piezas`.
3. Si es algo de Android o de pantallas que los muebles no dibujan, escribir un sustituto en `src/main/kotlin`: mismo paquete y nombre, y que no haga nada.

Nunca se cambia la app para que el arnés compile.

## Limitaciones

- Dibuja el mueble del Ideario, el estante del diario, la tapa, la libreta con su índice y los muebles de Vida. No dibuja los interiores, las fichas, la página abierta, la hoja de la libreta ni las cartas. Agregarlos es repetir en `Dibujar.kt` la llamada de su pantalla.
- Sin la textura del fondo (`fondoTema`), sin la escala de letra del usuario (siempre 1) y sin animaciones: es un instante quieto, como en las capturas.
- No es Paparazzi: la tipografía se parece mucho pero no es idéntica (ver arriba). Para la aprobación final siguen valiendo las capturas de CI.
- Compose Multiplatform 1.7.3 con Kotlin 2.1.0. La app usa Compose de Android 1.7.6 (BOM 2024.12.01).
