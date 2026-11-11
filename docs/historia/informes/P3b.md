# Tanda P3b — Los cuentos de Pierre, Kei y Amaru

Fecha: 11 nov 2026. Rama: `claude/modest-maxwell-zkj0mm`, encima de P1, P2 y P3a (`claude/ecstatic-turing-576qks`). Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros tres personajes del piloto, con la gramática y el tono que aprobaste en P3a. Desde esta tanda, en **Otras vidas**:
- con el tema **Gótico**, el detalle de Pierre empieza con «Hoy, en su vida»;
- con **Ciudad neón**, también los de Kei y Amaru.

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasPierre.kt` | Los textos de Pierre y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasKei.kt` | Los de Kei |
| `vidas/piezas/PiezasAmaru.kt` | Los de Amaru |
| `vidas/piezas/TodasLasPiezas.kt` | Registra las tres |
| `app/src/test/…/vidas/CuentoPrueba.kt` | Seis pruebas nuevas (ver «Verificación») |

- **El plan pedía un solo archivo, `PiezasPiloto.kt`.** Hice uno por personaje, igual que `PiezasDemeas.kt`: cada uno tiene unas 40,000 letras, y así cada tanda Y de los años que siguen abre solo el suyo.
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios.**
- **No lee ningún dato tuyo.**

## Dos cuentos de muestra

**Pierre, San Juan** (24 de junio de 2027, a las 9:00). Título: «San Juan: hogueras, y la cabeza del Bautista en su altar». Los párrafos 1, 4 y 8 son de ese día.

> Hoy es San Juan, el día más largo del año, y en Amiens es fiesta doble: la catedral guarda la cabeza del Bautista, traída de Constantinopla. Desde temprano llegan peregrinos de toda Picardía a verla, con su bastón y su cara de haber andado mucho.
>
> Es verano, cuando se trabaja de sol a sol y el sol no tiene prisa. El cielo está limpio, de un azul que los vidrieros quisieran para sí. Los viejos de la logia dicen que el tiempo ya no es el de antes, y lo dicen todos los años.
>
> A esta hora está en la logia, la cabaña de tablas pegada a la obra donde los canteros trabajan a cubierto. Se oye un golpeteo de mazos que no van a compás, y el polvo flota en la luz como harina. Cada uno tiene su banco, y nadie se sienta en el de otro.
>
> La cabeza está en un relicario de oro, y los peregrinos pasan delante en fila, tocando lo que pueden. Fuera, en la plaza, se venden velas, medallas de plomo con la cara del santo y empanadas, y no siempre se sabe cuál de las tres cosas deja más ganancia.
>
> Colin quiere ser cantero como su padre, y se pasa las tardes tallando trozos de yeso con un clavo. Le salen caras, perros, una catedral entera del tamaño de un pan. Pierre le dice que están torcidas, y luego las guarda en el arcón.
>
> Hoy, sin saber por qué, se fija en algo de todos los días. La plantilla es una tabla fina recortada con el perfil de la moldura; la da el maestro, se apoya en la piedra y se sigue con la punta, y así cien canteros que no se conocen tallan la misma pieza.
>
> No tiene tierra ni la quiere: la tierra ata, y un cantero va donde hay obra. Tiene un mazo, una marca y un sitio en la logia, y hay días en que eso le parece mucho. Su padre era labrador, y a su padre le parecía poco.
>
> Al caer la tarde, que hoy cae tardísimo, se encienden las hogueras de San Juan en las plazas y en los campos. Los jóvenes saltan por encima del fuego para tener suerte, y los viejos los miran pensando en lo que se va a quemar. Las brasas se llevan a casa en una teja, contra el rayo.

**Amaru, el día en que llega Kei** (12 de diciembre de 2027, a las 3:00). Título: «Una repartidora nueva pide fideos con doble caldo». Ese mismo día, el cuento de Kei es «Cena por primera vez en un puesto de fideos del nivel −3»: es la misma noche contada desde los dos lados del mostrador.

> Hoy, casi al final de la noche, llega al callejón una repartidora que no conoce, empujando una bici azul sin batería. Tiene la cara de quien no ha comido desde ayer. Amaru le tiende un cable para la bici sin que ella lo pida, porque así se hace con los que llegan empujando.
>
> (…)
>
> A esta hora la repartidora se sienta en el banco de la punta, el que cojea, y pide fideos con doble caldo. Lo dice sin pensar, como quien pide lo de siempre en un sitio donde no ha estado nunca. Amaru le llena el tazón hasta el borde y le pone más cebollín del que toca, sin saber por qué.
>
> (…)
>
> Al cerrar, Amaru se da cuenta de que no le ha preguntado el nombre. Da igual: si vuelve, ya sabe lo que pide. Celso, desde la farmacia, le pregunta quién era. Él dice que una del doble caldo, y Celso asiente, como si eso fuera un nombre, que en el callejón lo es.

## Cómo se adaptó la gramática

- **Pierre** sigue el patrón de Demeas sin cambios: la mañana es la mañana y el anochecer, la noche.
- **Kei y Amaru viven al revés.** Ella duerme de 6 a 12 y él de 10 a 17. Su «mañana» es el despertar (mediodía o media tarde) y su «anochecer» es cuando vuelven a casa o cierran, de madrugada. Sus cuentos están en una ciudad donde siempre es de noche (`SIEMPRE_NOCHE`), así que ningún párrafo habla del sol salvo como recuerdo.
- **El «dato de su época»** de cada párrafo, en los dos de ficción, es un detalle de su ciudad (el implante, el tren colgante, el mercado flotante, el caldo de tanque), dicho como lo diría alguien de allí.
- **En el párrafo del trabajo**, cada cosa que hacen en «Un día suyo» tiene sus tres variantes o más. Kei suma «tiempo propio» y Amaru «casa y cuidados» (la compra en el mercado flotante). Pierre come dos veces (a las 4:30 y a mediodía), así que sus variantes de comer no dicen ni dónde ni a qué hora.

## Lo que cada cuento respeta de su calendario

**Pierre**
- **«Herido»** (del 27 de mayo al 24 de agosto de la temporada 2): el trabajo, la mañana y el sueño son otros. No talla ni sube: separa cinceles, mira trabajar a los demás o ayuda en casa con una mano.
- **Al andamio solo sube de abril a julio.** No sube con la obra parada desde septiembre de la temporada 1, ni con los muros tapados en invierno, ni en agosto de la temporada 2, cuando vuelve con piedra blanda.
- **Quién aparece y quién se va:**
  - Colin es aprendiz desde el 2 de julio de la temporada 1; antes, talla yeso en casa.
  - Agnès aparece al nacer, en la temporada 2.
  - Jehanne deja de estar en casa cuando se casa, en la 4.
  - Maese Thomas pasa por la logia hasta que deja la obra a Renaud, en la 4, y desde entonces pasa Renaud.
  - El dintel de Enguerrand solo sale mientras lo talla, de octubre a noviembre de la temporada 1.
- **Gautier solo sale de octubre a marzo, todos los años.** En la temporada 3 pasa el verano en la hueste, y una variante no puede decir «menos de abril a septiembre de ese año». Así se le ve en invierno, cuando la logia está más llena.
- **Lo que se ve alrededor** (`{entorno}`) sigue los arcos: los muros tapados con paja o los andamios llenos según `andamios`, los campos de waide según `guede` y las calles con agua en la crecida de la temporada 3.

**Kei**
- **«Sin_bici»** (temporada 2) y **«sin_implante»** (temporada 3) tienen su mañana, su trabajo y su vuelta a casa. Sin implante también cambian el barrio, el tiempo y el objeto: ningún párrafo le pone anuncios en el ojo.
- **Hasta el 12 de diciembre de la temporada 1 el puesto de fideos no es suyo.** Desde entonces termina ahí algunas noches, con los repartidores (a las 4, cuando Amaru come con ellos).
- **Hasta el 5 de diciembre de la temporada 3** él es «el de los fideos»; desde entonces, Amaru.
- **Quién se va y quién llega:** Río hasta que deja los repartos (temporada 2), Dalia hasta que cierra el taller (temporada 4), la novata desde la temporada 3.
- **Lo que se acaba:** la deuda del implante, hasta la última cuota (temporada 2); «afuera no hay nada», hasta que pedalea al borde de la ciudad (temporada 5).

**Amaru**
- **La repartidora de la bici azul** llega el 12 de diciembre de la temporada 1. Se llama Kei desde el 5 de diciembre de la 3.
- **Quién llega y quién se va:**
  - Su hermana: «hace años que no sabe de ella» hasta la carta (temporada 1), y luego le escribe.
  - El guardia de Áurea, desde la temporada 2.
  - Sami, desde la 3.
  - Celso, en la farmacia hasta que se jubila (temporada 5).
- **Hasta la temporada 5** «hace años que no sube a la superficie».
- **El letrero:** del 12 al 28 de noviembre de la temporada 2 está apagado, y el cuento lo dice («Con el letrero apagado, hay quien pasa de largo…»). Después parpadea «a su manera», con la pieza del mercado. Sale del arco `letrero`, el mismo que tiene que dibujar el cuadro (decisión 18).
- **Lo que Kei y Amaru comparten** (las fiestas de la ciudad, la marea, la compra de Colibrí) tiene un texto distinto para cada uno: ella lo vive en la bici y él detrás de la barra.

**Las fiestas que vuelven cada año** se escribieron para que valgan los cinco años:
- no nombran a nadie que llegue o se vaya después (Agnès, Jehanne, Sami, el guardia, Río, Dalia, la novata);
- no ponen a Pierre a tallar (en la temporada 2 caen con el hombro roto);
- no nombran a Kei ni a Amaru entre sí, salvo la última noche del año, que cae siempre después de que se conocen.

## Cuánto texto tiene cada uno

| | Demeas (P3a) | Pierre | Kei | Amaru |
|---|---|---|---|---|
| Variantes de los ocho párrafos | 89 | 76 (7 de «herido») | 83 (7 «sin_bici», 7 «sin_implante») | 70 |
| …de ellas, con fecha o meses | 7 | 14 | 10 | 11 |
| Oraciones de tiempo y de estación | 26 | 29 | 21 | 21 |
| Objetos del día | 12 | 12 | 12 | 12 |
| Oraciones de entorno y de mundo | 12 | 21 | 20 | 23 |
| Días especiales de la temporada 1 | 23, con 106 párrafos | 21, con 81 | 17, con 74 | 17, con 70 |
| **Textos en total** | **245** | **219** | **210** | **196** |
| Letras en total | 50,993 | 42,967 | 42,347 | 39,813 |
| **Cuento medio** (2027, a las 9 y a las 19) | 1,914 | **2,016** | **2,010** | **2,049** |

- **El largo** queda un poco por encima de Demeas: unas 100 letras más por cuento, entre 5 y 7 segundos de lectura. Está dentro de lo que pide la prueba (de 1,800 a 2,150 de media).
- **Lo que pesa** son los días especiales: entre 70 y 80 párrafos por personaje. Un personaje que no viaja ni cambia de estado necesitó unos 200 textos, no los 150 que estimaba P3a. La diferencia está en que cada persona que llega o se va pide su variante con fecha.
- **Para las T, tres personajes por tanda caben.** Esta tanda llevó unas 125,000 letras.

## Verificación

- **Arnés local** (Kotlin 2.1.0, como se describe en el informe de P1): compila sin avisos y pasan las **78 pruebas** (35 de P1, 18 de P2 y 25 del cuento).
- **Las pruebas generales** de `CuentoPrueba` revisan solas a los tres nuevos, igual que a Demeas:
  - las piezas están bien escritas frente a su calendario: variantes, objetos, climas, estaciones, mundos y todos los días especiales;
  - ningún año escrito, ningún cuento repetido de 2026 a 2031, ocho párrafos enteros, el largo aprobado;
  - el mismo cuento todo el día salvo el trabajo, el título del objeto, la certeza de cada día especial, la nota de ficción, y el primer día de un especial con 3 o 4 párrafos.
- **Seis pruebas nuevas:**
  - `pierreNoTallaHeridoNiSubeAlAndamioFueraDeTiempo`;
  - `enElCuentoDePierreNadieApareceAntesNiDespuesDeTiempo` (Colin, Agnès, Jehanne, Gautier, el dintel, maese Thomas y Renaud);
  - `keiYAmaruSeConocenElMismoDia` (el 12 de diciembre, y los nombres desde la temporada 3);
  - `keiSinBiciNoPedaleaYSinImplanteNoVeAnuncios`;
  - `enLosCuentosDeKeiYAmaruNadieApareceAntesNiDespuesDeTiempo`;
  - `elLetreroApagadoSeVeEnElCuentoDeAmaru`.
- **Comprobé que esas pruebas fallan cuando deben.** Quité a propósito la fecha de Río y los meses de Gautier: las dos pruebas fallaron («Río sigue repartiendo», «Gautier en verano»). Luego lo dejé como estaba.
- **Leí cuentos enteros** de los tres en días normales, especiales y con estado. Así salieron tres arreglos:
  - Kei sin bici volvía a casa «pedaleando», y sin implante seguía viendo anuncios: ahora esos estados tienen sus propias variantes.
  - La logia tenía «veinte mazos» en los meses en que solo quedan seis bancos.
  - Pierre volvía herido «con el polvo de la piedra en las cejas».
- **Compilación de Android:** la corrida 204 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `bc0648e`).
- **Capturas:** 0, como dice el plan. Las pruebas de `vidas/` vuelven a correr en la siguiente corrida de capturas.

## Lo que no se puede comprobar aquí

- **Cómo se ven las secciones nuevas** de Gótico y Ciudad neón: la captura `cuento` de P3a solo saca Helénico. Es la misma sección y el mismo código que la de Demeas, así que no debería haber sorpresas. Las verán las capturas de P4b, que son de esos temas.
- **Los datos de época de Pierre** salen de lo que sé de la Amiens del siglo XIII y de las páginas abiertas en P2 (la catedral, la waide, las reliquias), pero no abrí una fuente por párrafo. Por ejemplo: la cuerda de trece nudos, el cincel dentado, las tarjas de la panadera, la cogida de la waide a mano o el juego de pelota entre parroquias. Es un cuento, y la nota al pie lo dice.
- **Un detalle de lluvia:** el clima de la ciudad neón deja llover algún día de la temporada seca. Ese día el cuento dice «Llueve… Es temporada seca». No es un error del sorteo, pero suena raro. Si te molesta, se arregla en el calendario (quitar la lluvia de los meses secos) o con otras oraciones; no lo toqué porque el calendario no es de esta tanda.

## Para las tandas que siguen

- **P4a y P4b:** lo que los cuentos de Pierre, Kei y Amaru dicen y se puede ver ya está en sus calendarios (estados y arcos).
  - **Pierre:** los andamios y el andamio caído.
  - **Kei:** la bici que falta mientras está «sin_bici».
  - **Amaru:** el letrero apagado, las máquinas en la boca del callejón y el agua.
  - El cuento no inventa nada que el cuadro no pueda leer de la ficha.
- **Las T:** el patrón queda igual que en P3a. Las fechas de cada persona (`desde` y `hasta`) son lo que más tiempo lleva, y conviene hacer una prueba por personaje de «nadie aparece antes ni después de tiempo», como las de esta tanda.
- **Y2028:**
  - los textos de Pierre herido de la temporada 2 ya existen como variantes; faltan los días especiales de ese año (la caída, el eclipse, Agnès);
  - Kei necesitará los del choque y Amaru los de la quemadura;
  - para la mano quemada de Amaru no hay estado en el calendario: si la tanda quiere que el cuento lo note más de un día, tendrá que agregarlo.
