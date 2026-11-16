# Plan de mejoras de noviembre de 2026

Lo que pidió el autor el 16 de noviembre de 2026 después de comparar los 39 temas en capturas. Se trabaja en la rama `ccr-ca31af2d-ua0p3j`, que parte de la rama de los cinco temas nuevos. Cada parte se hace por separado y se enseña antes de seguir.

## Cómo se trabaja

- El dibujo y el código los hacen agentes; quien dirige la sesión escribe el encargo de cada uno, mira cada imagen (composición, luz, sentido) y pide que se rehaga lo que no convenza.
- Los muebles se ven en el arnés de `herramientas/arnes_muebles/`; la compilación y las capturas de la app, en GitHub Actions (`comparacion.yml`, que publica en la rama `comparacion-ci`; es temporal).
- Nada de esto cambia lo que se registra ni cómo se calcula. Lo único nuevo que se guarda es opcional y va en campos nuevos (los cuadros guardados, los personajes seguidos, el modo por hora): se anota en cada parte.

## Las partes

| Parte | Qué | Estado |
|---|---|---|
| 0 | Este plan, las reglas de los muebles rehechos (`MUEBLES.md`, versión 2) y el arnés | en curso |
| 1 | Arreglos: el aviso que sonaba junto con la música al estrenar tema; el modo claro de día y oscuro de noche | el aviso, hecho (falta probarlo en el teléfono); el modo, pendiente |
| 2 | Piloto de muebles: Ideario rehecho y biblioteca completada en Laboratorio, Helénico y Gótico. El autor aprueba la vara | pendiente |
| 3 | Los otros 31 Idearios y bibliotecas, en tandas de 3 | pendiente |
| 4 | Los muebles de Vida: revisar los 39 y mejorar los que lo pidan | pendiente |
| 5 | Psicodelia nueva | pendiente |
| 6 | Fondos de pantalla con las escenas | pendiente |
| 7 | Lo temático nuevo: el toque, la hora y el clima, los mensajes breves, las esperas, el selector de fecha y de hora | pendiente |
| 8 | Los cuadros y los avisos: seguir personajes (con notificación y sonido propio), «Hoy en la historia» bajo el cuento, el cuadro a pantalla completa y arriba del cuento con su ambiente, la galería de cuadros y su mueble en Vida, el ronroneo del gato, y las notificaciones que hoy no suenan | pendiente |

## Decisiones del autor, parte por parte

### Muebles (partes 2 a 4)

Están en `MUEBLES.md`, versión 2. En corto: los 34 Idearios se rehacen; solo el mueble y la pared, sin ventanas ni paisajes; objetos y luz; noche como la de Galia y Art nouveau; tres tipos de libro; nivel de los cuadros.

Orden de los Idearios, de los más simples a los que ya tenían carácter:
1. Laboratorio, Ruinas, Navidad, Prehistoria, Mesoamérica, Mongol, Terminal, Ciudad neón, Refugio, Orden, Distopía.
2. Botánico, Horror cósmico, Tierras Antiguas, Pirata, Ruta de la Seda, Al-Ándalus, Egipto, Templario, Abismo, Espacio, Soviético, Helénico.
3. Romano, Urbe, Medieval, Gótico, Vapor, Colonos, Napoleónico, Art déco, Edo, Renacimiento, Revolución.

### El modo claro y oscuro (parte 1)

`config.modo` gana un valor, «hora»: claro de día y oscuro de noche. Los otros tres (claro, oscuro, como el teléfono) siguen igual.

### Psicodelia (parte 5)

- **Dos caras que no son el claro y el oscuro:** la ciudad de los 70, sobria, de día; la psicodelia, de noche. Cada cara tiene su claro y su oscuro. Con el modo «hora», de día sale la ciudad en claro y de noche la psicodelia en oscuro.
- **La ciudad de los 70:** urbana, cálida y gastada. Las referencias del autor: un estudio de grabación con cintas de carrete y lámpara de pantalla; el interior de un autobús al atardecer; una calle de barrio con letreros, coches y escaleras de incendio. Color de película vieja: ocres, naranja quemado, café, verde botella.
- **La psicodelia:** seria y pensada, con una paleta fija y corta. Las referencias del autor: un cartel a una tinta con grabado fino y líneas que ondulan, y uno de tres tintas planas (magenta, rojo y azul). Nada de imágenes de estereotipo (flores, símbolo de la paz, arcoíris).
- **Con un consumo de cannabis en curso** (la app ya guarda su periodo y su nivel), la psicodelia se satura mucho más. Solo se lee lo anotado; no se guarda nada nuevo.
- **Se quita la regla de «sin drogas»** del tema: el personaje del cuento consume de forma explícita de vez en cuando, y los textos del tema pueden aludir a ello. Hay que cambiar la guía (`temas/psicodelia.md`), las fuentes, el cuento y la prueba que lo vigilaba.
- **Para después (con el cuadro del tema):** la escena se vuelve muy psicodélica la noche en que el personaje consume.

### Fondos de pantalla (parte 6)

- El teléfono es un Pixel 10.
- La escena del tema como fondo, recortada en una franja vertical, en inicio y en bloqueo, y que cambie con la hora.
- El cambio automático con el tema ya existe (`fondo_telefono_auto`) y a veces no ocurre: hay que encontrar por qué.

### Lo temático nuevo (parte 7)

La respuesta al tocar; que el fondo y los muebles reaccionen a la hora, el clima o la estación; los mensajes breves; las esperas; el interior del selector de fecha y de hora. Sonido de ambiente: poco y opcional (por definir).

### Los cuadros (parte 8)

- **Seguir a un personaje** (hasta 6): cuando su historia tiene un día importante, sale su banderita avisando.
- **«Hoy en la historia»:** un botón debajo del del cuento.
- **Galería:** «me gusta» a un cuadro para guardarlo tal cual (el tema, la vida, el día y la hora); se ven en un mueble nuevo de la pestaña Vida.
- **El gato del cuadro** suena al acariciarlo.

## Respuestas del autor (16 nov 2026, segunda ronda)

1. **Las ventanas que ya existen se quedan por ahora** (los Idearios de Arcontes, Galia, Art nouveau y Psicodelia; las bibliotecas de Urbe, Pirata, Revolución, Ruinas, Navidad, Espacio y Abismo). La regla de «solo mueble y pared» vale para lo que se rehace.
2. **Psicodelia se queda en San Francisco a fines de los 60**, con su personaje y su cuento. De las referencias de los 70 se toma solo el estilo.
3. **Seguir a un personaje avisa también con una notificación del teléfono,** con un sonido nuevo, propio de la historia.
4. **La galería guarda el cuadro como imagen,** además de sus datos (tema, vida, día y hora), para que no cambie cuando se mejore el dibujo.
5. **Sonido de ambiente:**
   - nada continuo en la app;
   - el cuadro se puede ver a pantalla completa;
   - el cuadro sale arriba en la pantalla del cuento del día, y al abrir el cuento suena el ambiente del cuadro completo: suave, pero no necesariamente muy tenue;
   - el gato del cuadro ronronea al acariciarlo.
6. **Nada pasa a `main` hasta que todo esté arreglado.**

Además, el autor notó que **algunas notificaciones no suenan**: hay que revisarlas una por una y arreglarlas (parte 8). Lo que se ve en el código: solo cuatro canales llevan el sonido del tema (ánimo, diario, cuestionarios y fin de bloque de enfoque); el de reportes se crea con importancia baja, que en Android es sin sonido; los de recordatorios y lugares no llevan sonido del tema.
