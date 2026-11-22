# Los muebles de la pestaña Diario: la luz de los cuadros

Cómo se llevan los muebles de la pestaña Diario hacia los cuadros de «Otras vidas» sin cambiarlos. Es la receta que el autor aprobó el 14 de noviembre de 2026 con el piloto de Helénico, en su **versión sutil**: el mueble debe seguir viéndose como el de antes, un poco más vivo. Remite a [`ESCENAS.md`](ESCENAS.md) (las diez reglas) sin cambiarlo.

## Qué entra y qué no

- **Entran** los dibujos que se ven en la pestaña Diario:
  - el estante, los volúmenes, la tapa, la libreta y el sobre (`ui/biblioteca/`);
  - el mueble y los compartimentos del Ideario (`ui/ideario/`);
  - el tablero y las notas (`ui/vida/`, `tableroNotas` y `notaTablero`).
- Los mismos dibujos salen dentro de la Biblioteca, la Libreta, los Sueños, el Ideario y las Cartas, y ahí cambian igual.
- **No entran:** las piezas que solo salen dentro (página, pestañas, buzón, fichas, campo, repisa de Obras), los muebles de la pestaña Vida, la pared de `Escena` ni el cielo de los sueños.
- **Nunca cambian:** la silueta, las medidas, las cajas de texto (`cajaTapa`, `rotulo`, `placa`, el nicho de las notas), las animaciones ni las auras.

## La receta (versión sutil)

Las piezas están en `ui/muebles/Luz.kt`, que no usa el motor de los cuadros (los muebles dibujan en píxeles, no en el lienzo de 380 × 285).

1. **Sombra con color.** `Color.Black.copy(alpha = a)` pasa a `sombraMueble(a)` y `Color.White.copy(alpha = a)` a `luzMueble(a)`. La sombra es un violeta muy apagado (`SOMBRA_MUEBLE`) y la luz, crema.
2. **Luz con dirección.** Viene de arriba a la izquierda, igual en todos los muebles:
   - lo que tiene cuerpo lleva su lado de sombra a la derecha (columnas por planos, ánforas con el claro a la izquierda);
   - deja su sombra en lo que tiene detrás o debajo (`sombraContacto`, la sombra de las columnas en el muro).
3. **Nada es liso, sin exagerar.** Pinceladas tenues en las superficies grandes (`pinceladas`, alfa de 0,04 a 0,1). Grano fino al final solo en los muebles grandes (`granoMueble`: estante, libreta, mueble del Ideario y tablero). En los objetos chicos con aura no va grano, porque cuadraría el aura.
4. **La noche, como antes y apenas fría.** El modo oscuro conserva la profundidad que ya tenía cada mueble; solo cambia el color hacia el que oscurece, un negro un poco frío (por ejemplo `0xFF100E16` en lugar de `0xFF0F0C0A`). En la versión fuerte del piloto, la noche era una multiplicación por un aire azul violeta (`deNoche`); al autor le pareció demasiado y se dejó de usar. `deNoche` queda en `Luz.kt` por si se quiere más adelante.
5. **Un tono nuevo por mueble, no el de siempre.** Cada objeto de tema tiene funciones de tono (`t`, `lc`, `cc`, `n`) que también usan la página, el buzón o los muebles de Vida. No se cambian: se agrega una versión para los muebles del Diario (`tm`, `lcm`, `ccm`, `nm`) y se usa solo en ellos.
6. **Menos degradados.** Si un dibujo los tenía de sobra (las 50 manchas del muro del Ideario), pasan a pinceladas opacas. Paparazzi pinta de negro los últimos degradados de una imagen cuando son muchos.

## Cómo se revisa

- **Arnés de escritorio** (fuera del repositorio, en el scratchpad):
  - Compose de escritorio 1.5.12 desde Maven Central. Compila los archivos del tema enlazados más `ui/muebles/Luz.kt`, las interfaces sin su despachador, y un sustituto con `TemaId`, `Pagina`, `Letra`, `LetraExtra` y `simboloCalificacion`.
  - En Compose 1.5, `quadraticTo` se llama `quadraticBezierTo`; el arnés agrega una extensión `Path.quadraticTo` por paquete.
  - Dibuja cada mueble en claro y en oscuro con `CanvasDrawScope` y lo guarda en PNG.
- **Captura** `temaDiarioVara` (superficie `diarioVara`): el cuadro de Helénico a la hora del modo (13 h en claro, 21 h en oscuro) y la pestaña Diario del tema. Se pide con `superficies=diarioVara` y `temas=<los de la tanda>`.
- El autor ve cada tema junto al cuadro de Helénico antes de darlo por bueno.

## Los demás temas

Van después de las escenas S1 a S19 (lo decidió el autor), de 2 a 3 temas por tanda: primero Gótico y Ciudad neón, luego por archivo de época.

---

# Versión 2 (16 nov 2026): muebles rehechos

El autor comparó los 39 temas en capturas (los 34 de antes junto a Frente exterior, Arcontes, Galia, Art nouveau y Psicodelia) y decidió ir más lejos que la versión sutil. Lo de arriba sigue valiendo para lo que no se rehace; esta sección manda en lo que sí.

## Lo que decidió el autor

- **Los 34 Idearios se rehacen.** Estaban muy por debajo de los nuevos: un mueble de frente, el mismo recipiente repetido y adorno solo arriba.
- **Las bibliotecas no se rehacen, se completan** con tres cosas: noche de verdad, tres tipos de entrada y objetos del tema en los huecos. **Cambió el 17 nov 2026:** vistos los tres pilotos, el autor decidió rehacerlas todas (`PLAN_MEJORAS.md`, «Las bibliotecas»).
- **El mueble y la pared de detrás.** Es un interior: nada de paisajes ni exteriores. Sí objetos y sí iluminación. Los Idearios, sin ventanas. **Las bibliotecas pueden tener una ventana** (decisión del autor del 19 nov 2026: le gustan, pero con lógica y no en todos los temas); ver «Las ventanas de las bibliotecas», abajo.
- **El nivel de detalle y de calidad es el de los cuadros** de «Otras vidas» (`ESCENAS.md`).
- **La noche, como la de Galia y Art nouveau:** todos los temas llegan a ese nivel.
- **La noche se enciende con el modo oscuro.** El modo puede ser claro, oscuro, como el teléfono o por hora (claro de día, oscuro de noche).

## Las reglas de un mueble rehecho

Son las diez de `ESCENAS.md` llevadas a un interior (sin ventana en los Idearios; en las bibliotecas, con una si el tema la pide). Si una no se puede cumplir al pie de la letra, se cumple su propósito y se anota.

1. **El encuadre.** El mueble de frente contra su pared. La pared se ve arriba (la corona) y asoma a los lados y entre los objetos. No hay puerta abierta, cielo ni paisaje, ni reflejo de ellos. En los Idearios tampoco hay ventana; en las bibliotecas puede haber una, con las condiciones de «Las ventanas de las bibliotecas».
2. **La luz tiene dirección.** De día viene de arriba a la izquierda, de algo que queda fuera del cuadro (en una biblioteca con ventana, de la ventana):
   - una mancha de luz en la pared, más clara arriba a la izquierda;
   - cada volumen con su lado de luz y su lado de sombra;
   - el mueble deja su sombra en la pared, a su derecha y debajo de cada saliente;
   - cada objeto deja su sombra en lo que pisa y en lo que tiene detrás;
   - dentro de cada compartimento, la sombra de la tabla de arriba.
3. **La noche está al nivel del día.** No es el dibujo de día con un velo.
   - Todo se hunde hacia el color de noche del tema.
   - Alumbran una o dos luces **del tema**, dibujadas en el cuadro (la lámpara, la vela, la pantalla, las brasas), puestas donde su luz caiga sobre la pared.
   - Lo cercano a la luz se enciende: halo en la pared, filo cálido en los cantos que la miran, brillo en los compartimentos vecinos. Lo lejano casi se pierde.
   - Los rótulos se siguen leyendo (contraste de 4.5 a 1 o más sobre su placa).
4. **La paleta es corta y va junta.** De 12 a 18 colores con nombre al principio del archivo. La sombra tiene color (`sombraMueble`), nunca negro ni gris; la luz es crema (`luzMueble`). Los colores se repiten de un objeto a otro. Si el tema tiene cuadro, la paleta sale de la suya.
5. **Nada es liso.** Vetas, pinceladas, desgaste, manchas de uso y grano fino al final (`pinceladas`, `granoMueble`).
6. **Objetos con uso, no adornos en fila.** De 5 a 9 objetos del tema, sobre el mueble, a su pie, colgados de la pared y en los compartimentos vacíos:
   - de alturas distintas, encimados, descentrados, alguno cortado por el borde;
   - puestos donde los dejaría alguien: apoyados, colgados, ladeados;
   - en los temas históricos, cada objeto es de su época y de su lugar. Lo dudoso no se pone.
7. **Los compartimentos varían.** Al menos tres variantes según la `semilla` (otro recipiente, otro desgaste, otra forma de asomar las fichas). Se ve cuántas fichas hay (`n`): más lleno con más. El de «sin tema» se distingue. Se conserva la animación de abrir (`abierto` de 0 a 1).
8. **La composición.** Un punto donde el ojo cae primero, asimetría, y las siluetas de los objetos rompen la línea de arriba del mueble. La caja del rótulo queda limpia.
9. **Lo que no cambia.**
   - El contrato de `DisenoIdeas`: `rotulo`, `placa`, `salida`, las zonas de toque y la animación.
   - El interior, las fichas y sus marcas (`interior`, `forma`, `adornoFicha`, `marcaPostura`, `marcaFijada`), salvo pedido.
   - Sí pueden cambiar `cols`, `proporcion`, `corona`, `base`, `lado` y `hueco`, revisando la vista corta de la pestaña Diario y la pantalla entera del Ideario.
10. **Lo técnico.**
    - Cada Ideario rehecho va en su archivo, `ui/ideario/Ideas<Tema>.kt`, como los temas nuevos.
    - Todo el dibujo es código, determinista (azar con semilla fija) y en fracciones del ancho.
    - **Ningún bucle cuyo paso pueda valer cero.** Si el paso sale del ancho o del alto del lienzo, se protege (`maxOf(1f, h * 0.05f)`). Un lienzo puede llegar con alto cero cuando el mueble no cabe, y un bucle así cuelga la app. `MuebleIdeas` y `EstanteTema` ya no dibujan sin sitio (`sinSitio()`), pero la regla sigue valiendo. Todo mueble nuevo se dibuja en el arnés también con 0, 1 y 5 temas (`ARNES_TEMAS`) y cortado con su placa (`ARNES_MAX`).
    - Pocos degradados (Paparazzi pinta de negro los últimos cuando son muchos) y un dibujo que no pese: el compartimento se vuelve a pintar en cada cuadro de la animación.

## Las bibliotecas: lo que se les agrega

- **Tres tipos de entrada** por tema, elegidos con la `semilla` (como `clase(v.semilla)` en `BibliotecaGalia.kt`). Los tres son objetos del tema, no el mismo con otro color. Se conserva lo que cada entrada dice: el día, el color del ánimo, el grosor según las palabras y la marca de la noche.
- **Objetos del tema** donde las repisas quedan vacías, y los de arriba con uso (regla 6).
- **Noche de verdad** (regla 3).
- No cambia la silueta del estante, sus medidas ni las cajas de texto.

## Las ventanas de las bibliotecas (19 nov 2026)

El autor cambió la regla: **las bibliotecas no pierden las ventanas.** Le gustan, pero con lógica y no en todos los temas. Donde la haya:

- **Es la fuente de luz de día.** La mancha de luz en la pared, el lado de luz de los volúmenes y las sombras salen de ella (el derrame alumbrado, el rayo, la trama en el suelo), no de un foco cualquiera.
- **Cambia de noche.** Deja ver la noche de fuera (la luna, las estrellas) y deja de mandar: alumbra la luz del tema dibujada en el cuadro (el candil, la lámpara), como pide la regla 3.
- **No se come el mueble.** El mueble sigue mandando: la ventana no tapa volúmenes ni el objeto protagonista (en las tres de esta tanda va arriba y a un costado).
- **Solo donde tiene lógica.** Un interior que no la tendría (un sótano, una cueva) se queda sin ella, con la luz de algo que queda fuera del cuadro.

Así lo hicieron las tres de la tanda del 19 nov 2026: Scriptorium (la ventana abocinada arriba a la izquierda, con su rayo de sol; la luna, en los Sueños), Al-Ándalus (la ventana de herradura con su celosía, que dibuja la trama en el derrame y en el suelo; de noche alumbra el candil) y Espacio (el ojo de buey a un costado: de día, la luz fría de los focos del anillo; de noche, el anillo y la Vía Láctea, y manda la lámpara de trabajo). Después, Roma contemporánea (la ventana honda con su persiana verde arriba a la izquierda: de día, el sol rayado por las lamas en la cornisa y en la primera repisa; de noche, la lámpara de pantalla plisada) y Ruta de la Seda, donde la ventana queda fuera del cuadro: solo se ve el dibujo de su reja de palos en la cal, de sol de día y de luna de noche, junto a la lámpara de barro. Las ventanas que ya tenían las bibliotecas de antes (Pirata, Revolución, Ruinas, Navidad y Abismo) no desaparecen al rehacerlas: se quedan si cumplen esto.

## El peso del dibujo

- **Lo fijo no se recalcula por volumen ni por dibujo.** Los caminos, colores y medidas que no dependen del volumen se arman una vez y se comparten; cada volumen solo calcula lo suyo.
- **El peso, por debajo de 1.5 veces el del piloto de Helénico,** en claro y en oscuro: `herramientas/arnes_muebles/medir.sh biblioteca heleno,<tema>` (lo que tarda en grabarse y en pintarse el estante con sus 35 entradas).
- **Vigilar las operaciones** (`ops` y `KB` en la misma tabla), que fue lo que más pesó en Psicodelia: miles de puntos o trazos sueltos que el teléfono vuelve a pintar en cada cuadro mientras se mueve el fondo vivo. Las piezas iguales van en un solo camino con un solo trazo, y el grano en una imagen en caché. Si las operaciones pasan de unas dos veces las de Helénico, se dice qué pieza las gasta.
- Las cinco de la tanda del 19 nov 2026, frente a Helénico (operaciones, claro y oscuro): Espacio 1.29 y 1.36, Al-Ándalus 0.95 y 0.99, Scriptorium 1.19 y 1.26, Ruta de la Seda 0.97 y 1.01, Roma contemporánea 0.85 y 0.83. En tiempo (grabar y pintar), todas entre 1.0 y 1.35.
- Las cinco de la tanda del 22 nov 2026 (operaciones, claro y oscuro): Art déco 0.66 y 0.81, Japón Edo 1.19 y 1.21, Renacimiento 0.56 y 0.53, Egipto 0.57 y 0.52, Romano 0.76 y 0.72. En tiempo, todas entre 0.73 y 1.33 (lo más alto, Art déco en oscuro).

## Lo que más se devolvió en las bibliotecas (tanda del 19 nov 2026)

- **Volúmenes chicos y pálidos.** Tienen que llenar unos cuatro quintos del alto de la repisa, con el color del ánimo en todo el cuerpo, no en una franja.
- **Las tres clases que se confunden.** Cada clase de entrada, con su silueta propia (alta y delgada, baja y ancha, blanda), no la misma forma con otro adorno.
- **Una sola temperatura en la paleta.** Hace falta lo cálido y lo frío juntos.
- **El fondo de los casilleros que no deja resaltar los volúmenes.** El fondo, quieto y en otro valor que lo que tiene delante.
- **El estante de los Sueños como un filtro parejo.** Necesita su propia luz (un anochecer con color, el charco de una luz del tema), no el dibujo de día con un velo.
- **Muros de piedras hinchadas o de bloques de regla.** Piedras de caras planas, desiguales, con juntas abiertas y desgaste.
- **Pintura decorativa que grita más que los volúmenes.** Cenefas y pinturas, apagadas y gastadas: lo que manda son las entradas.
- **Falta de un objeto protagonista arriba.** Sobre el mueble, uno que mande (la prensa, el ventilador a medio arreglar), con uso.

## Cómo se revisa

- **Arnés de escritorio** en `herramientas/arnes_muebles/` (ver su `LEEME.md`): dibuja el Ideario y la biblioteca de cualquier tema, en claro y en oscuro, con los archivos reales de la app.
- **Capturas en Actions** de una biblioteca: superficie `bibliotecas` (la del diario) y, desde el 19 nov 2026, `suenos` (el estante de los Sueños con 14 sueños de ejemplo, que en el arnés sale con volúmenes delgados), con `temas=<los de la tanda>`. El libro del día: superficie `tapa`; la libreta de momentos con su índice: superficie `libreta` (la pestaña Momentos de la biblioteca, con dos días).
- **La vara son los Idearios rehechos** (noviembre de 2026), no Galia y Art nouveau: un mueble nuevo no puede verse más pobre que ellos. **En una biblioteca, la vara es el Ideario rehecho de su mismo tema**, puesto al lado a la misma escala (ver «La segunda tanda de bibliotecas», abajo).
- Cada mueble se dibuja, se mira y se corrige al menos tres veces antes de entregarlo, en claro y en oscuro.
- Se compara al lado del Ideario de su tema y con el cuadro del tema si lo tiene.
- El autor ve cada tanda antes de darla por buena. El piloto es Laboratorio, Helénico y Gótico.

---

# La segunda tanda de bibliotecas (22 nov 2026)

Art déco, Japón Edo, Renacimiento, Egipto y Romano. El autor devolvió una primera entrega de cuatro de ellas: «el nivel está muy por debajo del Ideario». Se habían comparado con la biblioteca de Helénico; fue un error. Lo que salió de rehacerlas vale para las que faltan.

## La vara de una biblioteca

**Es el Ideario rehecho de su mismo tema** (no el piloto de Helénico), puesto al lado a la misma escala en cada hoja: Ideario, biblioteca y Sueños, en claro arriba y en oscuro abajo, y recortes a tamaño real de una repisa junto a un casillero del Ideario. La biblioteca es otro mueble del mismo taller y de la misma casa. Se le copia el oficio al Ideario, no los objetos. Se compara en siete puntos:

1. **El mueble, una pieza con oficio y con cuerpo,** no tablas finas entre dos postes: cornisa o remate con sus molduras, friso, costados con grueso, divisiones con moldura, zócalo o patas, herrajes. Se ve su hondura: el canto de dentro de costados y repisas, y la sombra de la tabla de arriba en cada hueco.
2. **Color decidido** en la pared (con su decoración) y en el interior de los huecos, que contrasta con lo guardado. Paleta corta pero saturada, con claros y oscuros de verdad; nada de grises y pardos con el ánimo en pastel lavado.
3. **Lo guardado, grande y modelado:** cada entrada llena del 80 al 90 % del alto de su hueco, con tono base, plano de luz, plano de sombra, un brillo, sus detalles finos (costuras, cordeles, clavos, vetas) y su sombra en la repisa y en la vecina. Las tres clases son tres objetos que se entienden de un vistazo, no tres prismas con una etiqueta.
4. **La corona, un bodegón solapado:** una pieza grande que manda, objetos unos delante de otros, cada uno con su sombra en la pared, algo colgado y la pared decorada alrededor. No cosas chicas en fila sobre un paño vacío.
5. **El pie:** suelo con dibujo (alfombra, tatami, mosaico) y objetos modelados cortados por el borde.
6. **La noche con drama:** una llama dibujada que arde y tiñe la pared de su color, lo cercano encendido con su filo de luz y lo lejano hundido en un oscuro con color, nunca en un gris azulado parejo.
7. **El acabado:** bordes nítidos, filete de luz en cada canto que mira a la luz, línea de sombra bajo cada cosa, grano fino encima. Nada de planos lisos grandes.

Se hacen muchas vueltas (al menos ocho), y no se entrega mientras el Ideario se vea claramente mejor en alguno de los siete puntos.

## Lo que salió

- **El día se lee igual de grande en las tres clases de entrada** (unos 16 sp): la etiqueta crece lo justo. En las entradas angostas, cuidar que no tape el objeto (quedó flojo en Japón Edo y Romano).
- **De noche el ánimo usa su color de día.** En modo oscuro, `Tonos.valencia` da el color de la escala de noche del tema (`juegoTonos(tema, true)`), pensada para la interfaz: el ánimo de en medio pasa de claro a casi negro y los volúmenes claros salen negros. Aquí la entrada es un objeto del cuarto: `tonoDeDia<Tema>` ubica el color en la escala de noche (el tramo y la fracción que más se le acercan) y toma el mismo punto en la de día; si no está en la escala, lo deja igual. Después, la luz dibujada lo apaga según la distancia. Las bibliotecas rehechas antes no lo hacen (decisión pendiente en `docs/PENDIENTES.md`).
- **Las texturas, como baldosa pintada una vez** (el grano del galuchat, el ladrillo, las escamas de la palmera, el papel) y repetida, no punto por punto.
- **Lo fijo, pintado una sola vez:** la corona y el pie se pintan en una imagen guardada por tamaño y modo, y en cada dibujo solo se pega. Ojo con la memoria (Egipto guarda hasta ~12 MB; Renacimiento, ~2.8 MB por modo) y con que no se vea costura entre lo guardado y lo demás: si el borde no cae en un píxel entero, lo guardado y lo que sigue se solapan un píxel, o asoma el fondo en una línea a todo el ancho (pasó en Japón Edo y en Egipto: la imagen llega ahora al píxel entero de abajo y, debajo de esa fila, lleva la corona corrida un píxel; ver `CapaEdo.pegar`). Lo mismo vale para lo que se pinta en cada dibujo: dos piezas que se tocan en un borde a medio píxel dejan asomar el fondo (cada una cubre solo su parte de esa fila). Se solapan: antes de pintarlas, va debajo esa fila entera, de píxel entero y del color de una de las dos, solo a lo ancho de las dos (si se pasa de ellas, sale un punto oscuro en el extremo). Cambiar el recorte o el tamaño de una pieza para redondear el borde no sirve: Skia pinta distinto todo lo de dentro. Se revisa a tamaño real y con varios anchos y densidades de pantalla, porque depende de dónde cae el borde (el arnés dibuja la biblioteca siempre a 1080 px y 2.625; para otros tamaños hace falta un dibujo aparte con otra `ImageComposeScene`). La prueba más segura: dibujar el mueble sobre un fondo de otro color (magenta) y compararlo con el normal; lo que cambia dentro del mueble es fondo que asoma.
- **Las ventanas, según el lugar:** Art déco, sótano sin ventana (de día, el haz de la calle por la escalera; de noche, el aplique); Japón Edo, el ranma (celosía alta); Renacimiento, la impannata (ventana de tela encerada); Egipto, sin ventana (el sol de la puerta y, de noche, dos lámparas); Romano, un ventanuco de espejuelo y la linterna.
- **Las marcas inventadas se revisan contra símbolos reales:** se quitaron un orbe con cruz y una cifra suelta. Ninguna marca puede leerse como letra, cifra, cruz u otro signo conocido.

## Cómo se itera

- **Gradle entero, solo al final.** Para cada vuelta, se compilan solo los archivos del tema con el compilador de Kotlin suelto (el `kotlin-compiler-embeddable` 2.1.0 que ya está en la caché de Gradle) contra el jar del arnés ya compilado (`herramientas/arnes_muebles/build/instantaneas/classpath.txt`, con `-Xfriend-paths=<el jar>` y `-module-name main`), y se dibuja con `arnes.DibujarKt` poniendo esas clases delante en el classpath. Si el objeto del tema cambia o el archivo del tema usa un `const val` de `Biblioteca<Época>.kt`, ese archivo entra también en la compilación (las constantes se copian en quien las usa). Una vuelta tarda uno o dos minutos.
- **Con varios agentes a la vez, Gradle se queda sin memoria.** La compilación entera del arnés pide unos 4.5 GB, en proceso (`-Dorg.gradle.jvmargs=-Xmx4608m` y `-Pkotlin.compiler.execution.strategy=in-process`); con 3 GB se ahoga. Nunca `gradlew --stop`, que detiene también los demonios de los demás agentes: el propio se cierra por su número de proceso.
- **`ARNES_TEMAS` y `ARNES_MAX` no cambian la biblioteca** (son del Ideario). Los casos raros de la biblioteca se prueban moviendo `ARNES_HOY` (por ejemplo, el 1 de un mes y el 2 de diciembre).

---

# El libro del día y la libreta de momentos (20 nov 2026)

La prueba en tres temas (Helénico, Scriptorium y Espacio) quedó aprobada por el autor el 20 nov 2026: «funciona, se ven bien; no es impresionante». Se hace igual en los demás. **La vara siguen siendo los Idearios rehechos**, y los dos muebles se ven de la misma casa que el Ideario y la biblioteca del tema sin repetir sus objetos ni su luz. Lo que queda y en qué van: `docs/PENDIENTES.md`.

## Qué se rehace y dónde

- **Se rehacen** cuatro funciones del objeto `Estante<Tema>` (`ui/biblioteca/Biblioteca<Época>.kt`): `tapa()` (el diario de hoy, cerrado; `EstadoTapa`), `libreta()` (la libreta cerrada en su rincón; `EstadoLibreta`), `pestana()` (cada día del índice; `EstadoPestana`) y `rotuloMes()`. Con la tapa pueden cambiar `tintaTapa()` y `palabrasTapa()` y, si hace falta, `letraTapa` y `tamTapa`.
- **No se rehace** la página abierta (`encabezado`, `pagina`, `barra`, `botonGuardar`), ni el estante, los volúmenes, el sobre o el buzón, ni las pantallas que los montan (`Libros.kt`, `pantallas/Diarios.kt`, `pantallas/LibretaMomentos.kt`), ni lo que se guarda.
- **El código** va en `ui/biblioteca/MuebleTapa<Tema>*.kt` y `MuebleLibreta<Tema>*.kt` (partidos cuando pasan de unas 600 líneas: el rincón, el índice, las piezas), con los nombres de primer nivel con el tema; el objeto solo delega (como `EstanteHeleno`). Lo compartido entre los dos muebles del tema va en un archivo con el tema (`MuebleEspacioLuz.kt`).
- **El arnés:** `dibujar.sh tapa <tema> hoja` (vacío, escrito, larguísimo y el sueño, en claro y oscuro) y `dibujar.sh libreta <tema> hoja` (pocos y muchos días). Los extremos: `ARNES_TAPA=noche_vacio,abierto`, `ARNES_MOMENTOS=0,1,muchos`, `ARNES_ANCHO=320` y `420`, y `ARNES_LUGAR=diario ARNES_ABIERTO=0.55`. El peso: `medir.sh tapa,libreta,indice <tema>`, frente al de antes.
- **En Actions:** `capturas.yml` con las superficies `tapa` y `libreta` y `temas=<los de la tanda>` (con `solo=true`, la corrida es corta y la rama `capturas` queda solo con esas). Ahí se ve lo que el arnés no trae: la textura de la pared de la escena del Diario y su lámpara de los modos oscuros.

## El contrato (lo que no se mueve)

- **Alto fijo, ancho variable.** `altoTapa` (184 a 188 dp) y `altoLibreta` (252 a 266 dp) no cambian; el ancho sí (unos 355 dp en la escena de Diario, unos 380 suelta, menos en un teléfono chico). El entorno se estira a lo ancho; el objeto no se deforma.
- **Nada sale del lienzo** (`clipRect`): sombras y luces, dentro.
- **La caja del extracto (`cajaTapa`) no se achica:** el extracto de la entrada es el dato principal del libro del día. Cae sobre algo claro y quieto (el papiro, una cédula, una tarjeta) que de noche se apaga poco: su tinta se lee con un contraste de 4.5 a 1 o más, en claro y en oscuro.
- **Las pistas nombran el objeto** («toca para desplegar el rollo», «…abrir el códice», «…abrir la bitácora de vuelo»; viven en `Libros.kt`, compartido): el objeto nuevo es lo que la pista dice o se propone el texto nuevo aparte, sin tocar `Libros.kt` en la rama del tema.
- **Las pestañas del índice** miden 44×56 dp, van 7 por fila (5 a 320 dp) y salen por decenas: baratas (de 30 a 35 operaciones cada una, no más de unas 45), sin textos nuevos (el día y la letra de la semana) y del mismo material que la libreta.
- **Con 0 momentos** la app no dibuja la libreta, pero el arnés sí (0 hojas): no se rompe ni se cuelga.

## Lo que salió de las revisiones

- **El color de la tapa no sale del día:** se leería como un ánimo, y `EstadoTapa` no lo lleva. Un color fijo del tema (en Scriptorium, badana carmesí; en el sueño, índigo).
- **El libro del día es una franja (casi 2:1) donde manda el extracto.** Su entorno es poco y bien puesto: la superficie donde descansa, con su canto y la sombra con color; una franja de pared con la luz de un lado; dos o tres objetos chicos a los lados, que no le quitan sitio a la caja.
- **La libreta sí lleva un rincón entero** (mueble o soporte, pared con algo que cruce o enmarque, luz con dirección, objetos del tema con uso) y **dice qué objeto es a la primera.** Se devolvieron las tablillas que se leían como caja o pizarra, el códice que era una tabla grande y la carpeta en perspectiva que parecía un cajón. Engorda con las hojas, y el nombre y la cuenta se leen sobre ella sin taparla.
- **En el índice, el día sin ánimo se distingue de un ánimo neutro por la forma**, no solo por el color: un neutro claro se confunde con el material. Scriptorium quita la lengüeta; Helénico pone un cordón delgado de lana cruda en lugar de la vuelta de lana teñida; Espacio, plástico transparente en lugar de un color lleno.
- **La noche, con charco y dos temperaturas, no con velo:** una luz del tema dibujada (la lucerna, el brasero, la vela, la lámpara) cuyo charco enciende lo cercano y deja hundirse lo lejano, y una fría opuesta (la luna, el piloto de guardia).
- **El sueño es el mismo rincón más tarde, no un filtro:** otra luz (la luna manda, la lucerna arde baja, la lámpara se apaga y queda el piloto) y algún detalle del tema (las adormideras de Hipnos, el creciente de oro), no el dibujo de día recoloreado.

## El peso

Lo de siempre (lo fijo se arma una vez por tamaño; ningún `Brush` ni `Path` nuevo por pestaña; ni desenfoques, ni capas, ni animaciones continuas) y cuatro técnicas que salieron en esta prueba:

- **Los números de los días se miden una vez.** El medidor de texto de la pantalla guarda 8 medidas, el índice tiene 31 números y, si el número no cabe en su caja, `textoEn` lo achica midiéndolo hasta cinco veces en cada dibujo. Se guarda la medida ya hecha por número y por modo (`capaEn`, en `DisenoBiblioteca.kt`; `numeroDia` en `MuebleLibretaMedievalIndice.kt` y `MuebleLibretaHelenoIndice.kt`). Grabar el índice queda en 1.4 a 2.9 ms en los dos temas (midiendo en cada dibujo, de 15 a 47 ms), igual al píxel. Ojo: `medir.sh` usa un medidor de 64 medidas, no de 8 como la pantalla, así que no ve las medidas que en la app se pierden por falta de sitio.
- **El mapa de luz** (Espacio, `MapaLuzEspacio`): las piezas se pintan con la luz entera y encima va una imagen chica (unas decenas de píxeles por lado) con lo que la luz se apaga con la distancia a la lámpara, pintada una vez por tamaño y por modo y estirada en un solo dibujo, en lugar de un degradado por pieza.
- **Degradados grandes de dos colores, solo donde llega la luz:** la luz de Helénico pasó de capas sobre todo el lienzo a franjas opacas en diagonal (pintar la tapa, de 24–33 a 12–13 ms); Scriptorium dejó los degradados en dos colores y sin grano (de 26–36 a 16–17 ms).
- **Quitar lo que queda tapado:** sombras solo donde asoman, nada debajo de lo opaco.
