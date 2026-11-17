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
- **Las bibliotecas no se rehacen, se completan** con tres cosas: noche de verdad, tres tipos de entrada y objetos del tema en los huecos.
- **Solo el mueble y la pared de detrás.** Nada de ventanas, paisajes ni exteriores. Sí objetos y sí iluminación.
- **El nivel de detalle y de calidad es el de los cuadros** de «Otras vidas» (`ESCENAS.md`).
- **La noche, como la de Galia y Art nouveau:** todos los temas llegan a ese nivel.
- **La noche se enciende con el modo oscuro.** El modo puede ser claro, oscuro, como el teléfono o por hora (claro de día, oscuro de noche).

## Las reglas de un mueble rehecho

Son las diez de `ESCENAS.md` llevadas a un interior sin ventana. Si una no se puede cumplir al pie de la letra, se cumple su propósito y se anota.

1. **El encuadre.** El mueble de frente contra su pared. La pared se ve arriba (la corona) y asoma a los lados y entre los objetos. No hay ventana, puerta abierta, cielo ni paisaje, ni reflejo de ellos.
2. **La luz tiene dirección.** De día viene de arriba a la izquierda, de algo que queda fuera del cuadro:
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

## Cómo se revisa

- **Arnés de escritorio** en `herramientas/arnes_muebles/` (ver su `LEEME.md`): dibuja el Ideario y la biblioteca de cualquier tema, en claro y en oscuro, con los archivos reales de la app.
- **La vara son los Idearios rehechos** (noviembre de 2026), no Galia y Art nouveau: un mueble nuevo no puede verse más pobre que ellos.
- Cada mueble se dibuja, se mira y se corrige al menos tres veces antes de entregarlo, en claro y en oscuro.
- Se compara al lado del Ideario de Galia y del de Art nouveau, y con el cuadro del tema si lo tiene.
- El autor ve cada tanda antes de darla por buena. El piloto es Laboratorio, Helénico y Gótico.
