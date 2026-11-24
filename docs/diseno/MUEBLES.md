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
- Las cinco de la tanda del 23 nov 2026 (operaciones, claro y oscuro): Vapor victoriano 0.57 y 0.53, Pirata 0.52 y 0.49, Mongol 0.89 y 0.84, Mesoamérica 0.78 y 0.74, Colonos 0.66 y 0.68. Grabar, entre 0.35 y 0.84 en mediana; pintar, entre 0.58 y 1.33 (lo más alto, Mesoamérica en oscuro y Colonos en claro; en la peor corrida suelta, 1.45, Vapor victoriano en claro). Como la máquina mete ruido, lo justo se mide con varias corridas y se da la mediana y la peor.

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

# La tercera tanda de bibliotecas (23 nov 2026)

Vapor victoriano, Pirata, Mongol, Mesoamérica y Colonos, con la misma vara que la segunda tanda (el Ideario de su tema, al lado y a la misma escala) y tres rondas cada una. Lo que se aprendió en las revisiones vale para las que faltan.

## La luz

- **La luz pinta lo que toca; no es una franja encima.** El sol de la ventana, el ocaso de los Sueños en claro y la luna de los Sueños en oscuro no se dibujan como una banda translúcida puesta sobre el dibujo terminado: esa banda se lee como un velo o como un vidrio. Se pinta cada superficie que la luz alcanza (la pared, la cornisa, el canto de una tabla, el frente de un volumen, el suelo) con el color que tendría bajo esa luz, y la mancha se quiebra en cada plano: se corre donde la pared queda más atrás que el mueble, se corta en el canto de cada tabla y no entra en la sombra franca de lo que tiene delante. Fuera de la mancha, lo de siempre; dentro, la misma cosa más encendida y con la temperatura de esa luz. En el aire, a lo sumo, un rastro muy tenue.
- **La noche: dos o tres charcos cálidos unidos por un degradado, y entre ellos oscuridad con color.** Cada luz del tema (la lámpara, el fogón, una vela en un hueco, la estufa al pie) abre su charco: lo cercano se enciende y echa su filo de luz. De un charco al otro se pasa por un degradado, no por escalones ni por tramos. Lo de en medio se hunde en el oscuro propio del tema (un añil, un verde botella, un rojo de sangre de toro), nunca en gris ni en lila.
- **Lo claro lejos de la lumbre se hunde como lo demás.** El papel, el crema y las etiquetas que de día son claros no se quedan claros de noche lejos de la luz, ni se vuelven lilas o grises: se apagan hacia el oscuro del tema y se siguen leyendo. Y el mueble no se va a negro: conserva su color, apagado, para que se lea su madera.

## Lo guardado y lo que acompaña

- **Lo que acompaña en los huecos con pocas entradas es grande y va agrupado:** ocupa del 70 al 90 % del alto del hueco, con dos o tres cosas solapadas (una alta detrás y algo bajo delante), no piezas chicas sueltas en fila. En un hueco muy ancho, dos grupos distintos.
- **Cada volumen dice su día y su mes, legibles, en su etiqueta, y nada más.** Ni textos bordados, pintados o estampados en el cuerpo, ni números o letras inventados en los objetos (en Mongol se quitó un texto bordado; los relojes y manómetros van sin cifras).
- **Los volúmenes varían dentro de su clase:** el alto, el ancho, un ladeo contra el vecino, la etiqueta o la tablilla a otra altura, el largo del lazo, el desgaste. Si no, la repisa se lee como una valla.
- **Cuidado con las siluetas que se leen como otra cosa.** Se miran a tamaño de teléfono, preguntándose qué más podrían ser: una luna llena que parece un ojo (va mejor una media luna recortada), un paquete de tejadillo que parece un cartón de leche, dos postes con un brazo que forman una letra, dos agujeros de una hebilla que parecen ojos, los cabos de un nudo que dibujan una letra.

## Lo técnico

- **Gradle del arnés necesita más memoria:** la compilación entera se quedó sin memoria con 3 GB. Se pasa en la orden `-Dorg.gradle.jvmargs=-Xmx5g` (o 6 GB) con `-Pkotlin.compiler.execution.strategy=in-process`, sin cambiar `gradle.properties`, y nunca dos a la vez en la misma máquina.
- **Lo fijo, en una o dos imágenes guardadas por modo, sin pasar de unos 3 MB por pantalla a 1080 px** (en esta tanda, de 2.1 a 3.0 MB; con 1440 px, casi el doble): lo que crece con el número de repisas (el cuerpo, los cajones del pie) se pinta en cada dibujo. Si el diario y los Sueños guardan cada uno su imagen, al visitar las dos pantallas se suman.

---

# La cuarta tanda de bibliotecas (23 nov 2026)

Art nouveau, Tierras Antiguas, Templario, Ciudad neón y Prehistoria, con la misma vara que las dos tandas anteriores (el Ideario de su tema, al lado y a la misma escala) y dos rondas de revisión cada una, después de las vueltas propias de cada agente. En las cinco, el mismo agente hizo después la libreta de momentos nueva y las mejoras simples de la tapa (abajo, «Decisión del 23 nov 2026 y segunda vuelta»).

## Qué es cada mueble

- **Art nouveau:** la biblioteca abierta de caoba del despacho de la tienda del taller, con papel-cuero dorado en los casilleros, el ramo en el jarrón arriba y, de noche, el aplique, el quinqué y la palmatoria; las entradas son el tomo de la revista, el rollo de carteles y el legajo de cartas.
- **Tierras Antiguas:** el armario de la crónica, de roble ahumado, en la sala de arriba de la posada del paso, contra el enlucido de lapislázuli sembrado de oro y con la ventana de vidrios en rombo en escorzo; arriba manda la drusa de amatista; las entradas son el tomo de la crónica, el rollo de varillas y el envoltorio de cartas en paño de lana.
- **Templario:** el armario-estante de cedro de la cámara alta, contra la cal con despiece fingido a la almagra y con el ajimez en el costado; las entradas son el libro de la casa, el estuche de cuero hervido y la escarcela de las cartas.
- **Ciudad neón** (solo oscuro): la alacena de laca roja del cuarto de arriba del puesto, contra el azulejo jade, con la ventana al callejón y el acuario encima; las entradas son el cartucho de datos, la lata de té y el saco de cordón de repartidor.
- **Prehistoria:** el andamio de varas de la boca de la cueva (postes de pino con su horqueta y zarzos atados con cuero crudo), con la piel tensada en su bastidor arriba y el hogar de lajas al pie; las entradas son el hueso de cuenta con sus muescas, la bolsa de piel y la piel enrollada.

## Lo que salió de las revisiones

- **Siluetas que se leían como otra cosa** (se miran a tamaño de teléfono): el hueso de cuenta, como un muslo de pollo (quedó de marfil viejo y mate, hincado en un pie de barro, con una sola cabeza); unos rollos, como botes; el saco de cordón, como una bolsa de regalo (ahora es de repartidor, con la boca caída a un lado y el cordón en un solo cabo con nudo, sin lazo); el globo de una lámpara, como una cara (ahora lleva una cenefa grabada continua).
- **Los signos se leen como letras:** un zigzag en el cuero se leía como letra; quedó una banda de dientes chicos.
- **Ni objetos de otra época:** un sobre se leía como de correo de hoy.
- **Lo que llena una superficie, sin rayarla:** un legajo rayado no se leía como papel; quedó como un fajo de sobres de un solo tinte.
- **El fondo de las repisas sale del lugar:** un fondo de un color ajeno al tema (azul marino en una cueva) se cambió por la pared de la cueva en sombra, parda, con su relieve y su humedad.
- **La luz de una ventana no puede parecer gente:** las dos luces de un ajimez, como dos manchas pálidas de pie, se leían como dos figuras. Se tienden y alargan con el ángulo del sol, unidas por la sombra del parteluz, y pintan lo que tocan (la regla de la tercera tanda).

---

# La quinta tanda de bibliotecas (24 nov 2026)

Napoleónico, Horror cósmico, Galia, Revolución francesa y Ciudad verde, con la misma vara (el Ideario de su tema, al lado y a la misma escala) y dos rondas de revisión cada una, después de las vueltas propias de cada agente. En las cinco, el mismo agente hizo después la libreta de momentos nueva y las mejoras simples de la tapa (abajo, «Decisión del 23 nov 2026 y segunda vuelta»).

## Qué es cada mueble

- **Napoleónico:** la librería de campaña del oficial de estado mayor (cajas de caoba con cantoneras de latón, forradas de paño verde por dentro, apiladas para hacer el estante) en el cuarto de una casa requisada, contra el enlucido amarillo Imperio, con el globo, la lámpara bouillotte y la trompa arriba; las entradas son el tomo de tafilete, la caja de archivo jaspeada y el ridículo de seda.
- **Horror cósmico:** la librería que un carpintero de ribera hizo con la teca de un barco, en la sala de lectura de arriba del archivo del pueblo de pescadores, contra el papel pintado de escamas verde mar, con la ventana de guillotina empañada y la lente del faro arriba; las entradas son el libro de registro hinchado por la humedad, la botella lacrada y el fardo en un trozo de vela.
- **Galia:** el estante de las cuentas de la casa grande, un bastidor de roble labrado a azuela contra el barro bañado de almagra, bajo el alero por dentro, con la tinaja, el ánfora, el cuerno y las mantas a cuadros arriba y el hogar al pie; sin ventana (la luz es la de la puerta); las entradas son la funda de lana a cuadros con tablillas, el cofre de mimbre y el zurrón de cuero.
- **Revolución francesa:** la librería pintada de sangre de toro, con los casilleros verde cardenillo, de la trastienda de la imprenta del barrio, contra el yeso con los pliegos tendidos a secar, con la caja de tipos arriba, la ventana del cajista y la estufa al pie; las entradas son el tomo en cartoné, el fajo de folletos atado con cinta y el rollo de carteles.
- **Ciudad verde:** la librería de roble de la biblioteca municipal, subida al cuarto de lectura bajo el lucernario roto, contra el yeso verde agua desconchado con el herbario tendido, con la máquina de escribir y la damajuana con un helecho arriba y el anafre al pie; las entradas son el libro salvado, la botella de las hojas enrolladas y el fardo de tela encerada.

## Lo que salió de las revisiones

- **La luz de ventana no es una franja encima:** en tres de las cinco, el sol entraba como franjas diagonales translúcidas puestas sobre el dibujo. Se rehízo como parches que pintan cada plano con su quiebre (la regla de la tercera tanda), y la luz de la puerta de Galia, que caía como una banda sobre la tinaja, pinta ahora el material de cada objeto.
- **Las tres clases de entrada se distinguen por la silueta:** unas se leían «libros, libros, libros»; la segunda y la tercera clase tienen que ser otra cosa a la primera (una botella con hombros y cuello, un fardo blando con su cordel).
- **La noche no es pareja:** un bastidor que de noche quedaba de un solo tono se partió en el oscuro con color del tema y la luz cálida junto a la lumbre.
- **Los Sueños, sin velo:** un velo rosa (el ocaso) o azul pizarra (la luna) sobre todo el estante se devolvió; el ocaso, bajo y melocotón, no naranja saturado, con el violeta solo en las sombras, y la luna en un parche chico de azul hondo con su filo.
- **Siluetas que se leían como otra cosa:** un salvavidas con cuatro ligadas iguales hacía una cruz en un círculo (quedaron tres ligadas desiguales); un cuerno colgado parecía un arco (ahora cuelga de un clavo por la boca); un fardo de tela parecía un pan (quedó como paquete, con sus esquinas y su cordel); un farolillo de lata agujereada parecía un rallador (lleva su puertecilla).
- **La tarjeta no tapa la entrada:** la tarjeta del día tapaba casi entera la bolsa de seda; quedó chica, colgada del cordón.

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

## Decisión del 23 nov 2026 y segunda vuelta

Tras una prueba de editar sin redibujar (en Pirata y Vapor victoriano), el autor decidió el 23 nov 2026: **la libreta de momentos se hace de nuevo en todos los temas; la portada del libro del día se queda como está, con mejoras simples** (editar, no redibujar). La segunda vuelta la hicieron los agentes de Art nouveau, Tierras Antiguas, Templario, Ciudad neón y Prehistoria y, después, los de Napoleónico, Horror cósmico, Galia, Revolución francesa y Ciudad verde, cada uno encima de su biblioteca aprobada y sin tocarla.

### La libreta, nueva

- **Qué se rehace:** `libreta()` y `pestana()` del objeto (y `rotuloMes()` solo si desentona con lo nuevo), en `ui/biblioteca/MuebleLibreta<Tema>*.kt`; el objeto solo delega. El contrato de arriba no cambia.
- **La vara:** la biblioteca recién aprobada del tema y su Ideario. La libreta es un mueble de la misma casa (mismos materiales, misma luz, misma mano) que no repite sus objetos ni su encuadre, y tiene que quedar por encima de las tres de la prueba del 20 nov.
- **Qué es:** un rincón entero con un solo objeto que se entiende a la primera como el sitio donde se apuntan los momentos del día en ese mundo, y que es lo que dice la pista de `Libros.kt`. Grande (ocupa lo más del alto), modelado, con su soporte, una pared con algo que cruce o enmarque, luz con dirección y dos o tres objetos del tema con uso. Engorda con las hojas; el nombre y la cuenta se leen sobre él sin taparlo.
- **El índice:** del mismo material que la libreta; el día sin ánimo se distingue por la forma; de 30 a 45 operaciones por pestaña, sin `Brush` ni `Path` nuevos por pestaña y con los números medidos una vez (`capaEn`).
- **La noche:** una llama o lámpara del tema dibujada, con su charco, y una luz fría opuesta; lo lejano se hunde en un oscuro con color.
- **Peso y extremos:** por debajo de 1.5 veces Helénico (`medir.sh tapa,libreta,indice`); lo fijo, en una imagen guardada de no más de unos 3 MB por modo; `ARNES_MOMENTOS=0,1,muchos`, `ARNES_ANCHO=320` y `420`, `ARNES_LUGAR=diario ARNES_ABIERTO=0.55`.

### La tapa, con mejoras simples

- **Se edita `tapa()` donde está.** Mismo objeto, misma silueta, misma composición, mismos textos y la misma `cajaTapa`. Tres cosas, que fueron las que funcionaron en la prueba:
  1. **Donde descansa:** el canto de la superficie donde se apoya y la sombra arrojada con color, debajo y al lado contrario a la luz.
  2. **La noche con lado:** cálida del lado de una llama del tema (si no hay ninguna dibujada, una chica, como la vela en su palmatoria), fría del otro, y lo lejano hundido en un oscuro con color.
  3. **El sueño:** la misma pieza con la luna mandando, no un filtro.
- **De día, poco:** filete de luz en los cantos que miran a la luz y sombra propia teñida. No se rehacen texturas.
- **Nunca se cambian degradados por bandas planas** para ahorrar peso: en la prueba quedaron escalones a la vista. Si un degradado pesa, se queda como estaba.
- **Se conservan todos los textos, marcas y dibujos que la tapa ya tiene** (son del autor), aunque sean escritura real o un emblema: la regla de «sin escritura ni marcas» vale solo para lo que se dibuja nuevo. Una tapa que los había quitado los tuvo que devolver tal cual.
- **De noche nada se lee peor que antes** (la fecha, el título): se mide. Sin velos de color sobre la pieza: el lado frío va en los cantos y en la sombra.
- **Límite:** dos vueltas, menos de unas 200 líneas cambiadas, peso no mayor de 1.3 veces el de antes y la tinta del extracto a 4.5 a 1 o más. Si la tapa pide rehacerse para verse bien, no se rehace: se dice.

### Lo que devolvieron las revisiones de las libretas

- **El objeto se lee a la primera y ocupa lo más del alto.** Se devolvieron: un códice frontal y plano que parecía la tapa de un arcón (le faltaban el grueso de las hojas, el lomo con volumen y el atril donde descansa); un ticket ancho colgado alto que parecía un cartel en un palo (tenía que verse ensartado en el pincho, ladeado y combado); un objeto chico con una franja lisa debajo.
- **Las superficies grandes, calmadas.** Un jaspeado menudo y saturado se comía el rótulo y parecía un cojín estampado: formas grandes, pocos valores, la paleta apagada del tema; mandan el rótulo y la silueta.
- **La luz de ventana o de luna pinta el material:** su mismo color, virado y un paso más claro, con un borde que se quiebra en cada plano. Se devolvieron un paño de luz como dos arcos pálidos gemelos (parecían dos figuras), un paño de luna translúcido (parecía un cristal apoyado en la pared) y un parche de luna gris blanquecino sobre cuero (parecía una chapa pegada). Si no se lee como luz, se quita y queda el filo frío en los cantos.
- **Objetos que parecen otra cosa:** una vara de tramos rojos y blancos parecía un bastón de caramelo (quedó de fresno a la vista con bandas gastadas).
- **El charco de la lámpara tiñe sin matar el color local:** el azulejo verde bajo la luz cálida va a coral con la junta todavía verde, no a pardo.
- **De la quinta tanda** (Napoleónico, Horror cósmico, Galia, Revolución francesa y Ciudad verde):
  - **El objeto no es un letrero:** un haz de tablillas de frente, con el nombre grande en la de arriba, se leía como un letrero, y su rincón era pobre. Quedó un haz de tablillas gruesas, desiguales y algo giradas, atado por encima con su nudo, y el rincón con lo de la casa (la cenefa, el poste, las hierbas, la manta, el cuenco de cera, el vaso, el ovillo). Un legajo frontal y plano también se leía como un cartel: ahora es una carpeta de vitela alabeada, con el grueso de los pliegos a la vista también con pocos despachos.
  - **La noche alumbra el objeto:** un registro quedaba apagado de noche porque la vela estaba lejos; la vela bajó al listón, junto a él, y su charco enciende media tapa sin quitarle su color. El pupitre que se leía como un cajón se dibujó en escorzo, con el tablero más ancho abajo, sus cantos, el listón de apoyo y las patas torneadas.
  - **Siluetas que hacen cara:** un tazón con dos saltaduras a la misma altura y el asa de lado hacía una cara; quedó una saltadura en el borde y el asa girada hacia atrás.
  - **Un roto no es un parche:** el roto de un papel pintado, dibujado como un parche plano, quedó con el borde irregular, el filo blanco de la fibra, dos lenguas de papel abarquilladas con su revés y su sombra, y dentro el yeso y el ladrillo; la luz pasa por encima del papel y del yeso con el mismo borde.
  - **Ni objetos de otra época:** un despacho se leía como un sobre de hoy; quedó una carta doblada en tres con el lacre en el borde del doblez.
  - **El día del índice se lee:** el número de cada pestaña va en un peso que se lea a tamaño de teléfono (una letra de trazo fino perdía el 1 y el 4), con un contraste de 4.5 a 1 o más sobre su pestaña, en claro y en oscuro.
  - **Las tapas conservan todos sus textos y emblemas:** las cinco tapas de mejoras simples guardan lo que ya tenían (títulos, divisas, escarapelas, monogramas, ojos, sellos), aunque sean escritura real o un emblema.
