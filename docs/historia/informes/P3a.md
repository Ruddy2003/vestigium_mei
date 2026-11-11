# Tanda P3a — El motor del cuento, el cuento de Demeas y «Hoy, en su vida»

Fecha: 11 nov 2026. Rama: `claude/ecstatic-turing-576qks`. Plan: `docs/plan_historia_y_escenas.md`.

## Lo que tienes que decidir

**El tono.** Esta tanda fija cómo suenan los cuentos; las 16 tandas de textos que siguen lo copian. Abajo hay dos cuentos enteros de muestra y las reglas que seguí. Si algo no te gusta (muy seco, muy largo, demasiado dato, poco personaje), dilo ahora: cambiarlo después cuesta 45 personajes.

## Qué trae

Desde esta tanda se ve algo en la app: en **Otras vidas**, con el tema Helénico, el detalle de Demeas empieza con la sección **«Hoy, en su vida»**: el cuento de hoy.

| Archivo | Qué tiene |
|---|---|
| `vidas/Cuento.kt` | El motor: arma el cuento del día con las piezas del personaje y su ficha |
| `vidas/piezas/PiezasDemeas.kt` | Los textos de Demeas y, arriba, las reglas de gramática y de tono |
| `vidas/piezas/TodasLasPiezas.kt` | El registro de piezas. Las tandas de textos agregan aquí las suyas |
| `ui/pantallas/OtrasVidas.kt` | La sección «Hoy, en su vida» |
| `ui/MainActivity.kt` | La ruta `otras_vidas?vida=<id>&seccion=cuento`, que abre directo el cuento |
| `vidas/anios/AnioPiloto.kt` | Tres ajustes al calendario de Demeas (ver abajo) |
| `app/src/test/…/vidas/CuentoPrueba.kt` | 18 pruebas |
| `app/src/test/…/Capturas.kt` | La captura `cuentoDelDia` (con `CAPTURAS=cuento`) |

- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.** El cuento sale de la fecha, la hora y el personaje.

## Dos cuentos de muestra

**Un día normal** (3 de febrero de 2027, a las 9:00). Título: «El remo».

> Demeas se levanta a oscuras, cuando cantan los gallos del vecino. Moja pan de cebada en un poco de vino puro, que es todo el desayuno, y sale sin despertar a nadie.
>
> En invierno las naves duermen bajo techo y el puerto se queda a medio hablar. Llueve desde la madrugada, una lluvia fina que lo empapa todo sin hacer ruido.
>
> A esta hora tiene las manos negras de brea. Están repasando las costuras del casco, tabla por tabla, y el trierarca mira desde la sombra: la nave es de la ciudad, pero los arreglos los paga él.
>
> En el puerto grande descargan trigo, pieles, pescado seco y gente que habla raro. Todo lo que Atenas come pasa primero por delante de su casa.
>
> Cares tiene la edad de preguntarlo todo: por qué flota la madera, quién les pinta los ojos a las naves, si los delfines duermen. Demeas contesta lo que sabe e inventa el resto.
>
> Hay cosas que uno no mira hasta que faltan. Su remo es de abeto, largo como dos hombres y medio, y lleva una marca cerca del puño: en la flota cada remero carga con el suyo y responde por él.
>
> Piensa a veces en lo raro que es su oficio: mover una casa de madera por el agua a fuerza de brazos, sin ver nunca hacia dónde va.
>
> Se cena con las manos, mojando el pan en la olla. Cares se duerme sentado antes de terminar, y Demeas lo lleva a su jergón sin que abra los ojos.

**Un día especial** (3 de agosto de 2027, a las 14:00). Título: «El sol se vuelve una media luna en pleno día». Certeza: histórico. Los párrafos 1, 3, 5 y 8 están escritos para ese día; los demás son de los de siempre, en su versión «en el mar».

> El día empieza como cualquier otro de este verano: empujar la nave, embarcar, remar con la costa a la derecha.
>
> Es verano, la estación de remar y la de la guerra. Hace un día claro, de esos en que las islas parecen más cerca.
>
> Pasado el mediodía la luz se vuelve rara, como de tarde. Alguien grita y todos levantan la vista: al sol le falta un pedazo. Se va adelgazando hasta quedar en media luna, y en pleno día asoman unas estrellas.
>
> Cada tarde hay que buscar una playa con agua dulce cerca, y no todas la tienen. Donde la hay, la flota entera hace cola con los odres.
>
> Nadie rema. El flautista se ha quedado con la flauta en la boca, sin soplar. El piloto, que ha visto de todo, dice que eso pasa y que se pasa; en la voz se le nota que no está seguro.
>
> La dracma es de plata y tiene la lechuza por un lado y la diosa por el otro; como casi nadie lleva bolsa, las monedas chicas viajan en la boca, debajo de la lengua. Es poca cosa, pero un remero tiene pocas cosas y las conoce todas.
>
> No tiene tierra ni la tendrá. Tiene un remo, un voto y un sitio en un banco, y hay días en que eso le parece mucho y días en que le parece nada.
>
> El sol vuelve a estar entero antes de ponerse, y esa noche en la playa no se habla de otra cosa. Unos dicen que es un aviso y otros que hoy cambia la luna y nada más. Demeas no dice nada: lo vio, y le basta.

Los dos son tal como los arma la app. Tienen 255 y 276 palabras: se leen en un minuto.

## La gramática

Las reglas completas están al principio de `PiezasDemeas.kt`, que es el archivo que copian las demás tandas.

- **Ocho párrafos, siempre en el mismo orden:** la mañana, el tiempo, el trabajo, el pueblo, la gente, el objeto, lo que piensa y el anochecer.
- **Cada párrafo se puede leer junto a cualquier otro.** No empieza con «también» ni «por eso», y no da por dicho nada de otro párrafo. Es lo que permite combinarlos.
- **Todo en presente y en tercera persona.** Así se lee igual a las ocho de la mañana que a las once de la noche.
- **El párrafo del trabajo es el único que cambia con la hora.** Empieza con «A esta hora» y dice lo que el personaje hace en ese tramo de «Un día suyo»: trabaja, come, está con otros o duerme.
- **Huecos que llena la ficha:** el nombre, la edad con letras, la estación, el tiempo que hace, el objeto del día, lo que se ve alrededor según cómo va la guerra y lo que se dice del mundo ese año.
- **Concordancia de género:** `{cansado|cansada}` elige según el personaje. Demeas no la necesita; está probada para las que sí.
- **Día especial:** el primer día cambia 3 o 4 párrafos. Si dura más:
  - una fiesta de pocos días tiene texto para cada día (las Antesterias: las Tinajas, las Jarras, las Ollas);
  - algo largo (la siega, la invasión) cambia un párrafo cada día, con tres o cuatro versiones que se turnan.
- **El título** es el del día especial. En un día normal es el del objeto del día: «El remo», «La dracma», «Los higos».

## El tono

- Frases cortas y cosas que se ven, se oyen o se huelen.
- Un dato de su época por párrafo, dicho como lo diría alguien de allí, sin explicarlo como en un libro.
- Sin años, sin «en aquella época», sin palabras de hoy y sin moraleja.
- Humor seco, de poca cosa. El personaje no es un héroe ni una víctima: es alguien que hace su trabajo.
- No repite lo que ya dice «Su vida» (cuánto gana, cuántos reman).

## Cómo quedó el motor

- **Las variantes de cada día.** Para cada lugar, el motor toma las variantes que valen hoy y la ficha dice cuál toca, con el reparto de P1: ninguna sale dos días seguidos y ninguna combinación se repite.
- **Cuatro cosas que una variante puede pedir** (todas opcionales):
  - `estado`: solo sale en un estado que dura semanas. Demeas está «en el mar» del día en que zarpa al día en que vuelve, y entonces la mañana, el pueblo, la gente, el trabajo y el anochecer son otros.
  - `hacer`: solo sale cuando a esa hora hace eso (para el párrafo del trabajo).
  - `desde` y `hasta`: vale a partir de un día del calendario, o hasta la víspera. Su padre deja de aparecer el día en que muere; el tío aparece en el patio cuando llega.
  - `meses`: solo sale en esos meses. De noviembre a abril la nave está guardada y ningún párrafo lo pone a remar.
- **Lo que se ve alrededor cambia con los arcos del calendario.** Antes de la invasión no hay chozas junto a los muros ni campos quemados; después, sí.
- **La edad avanza** con la temporada y sale con letras («a sus treinta y un años»).
- **Un día especial sin texto todavía** (los de las temporadas 2 a 5 los escribe la tanda de cada año) conserva su título y su nota de certeza; el cuento de ese día es el de un día normal.
- **Un personaje sin piezas no tiene cuento** y su sección no aparece.

**Cuánto texto tiene Demeas**

| Qué | Cuántos |
|---|---|
| Variantes de los ocho párrafos | 89 (64 en tierra y 25 en el mar) |
| Oraciones de tiempo y de estación | 26 |
| Objetos del día, con su oración | 12 |
| Oraciones de entorno y de mundo | 12 |
| Párrafos de los 23 días especiales de la temporada 1 | 106 |

## La pantalla

- **Dónde:** en Otras vidas, justo debajo del retrato y antes de «Su vida».
- **Qué muestra:**
  - el título del cuento y, si es día especial, qué clase de día es;
  - una línea con la edad que avanza, el año de su mundo y la estación («Demeas, 30 años · 431 a. C. · invierno»);
  - en 2026, un aviso de que es un avance;
  - los ocho párrafos;
  - al pie: la certeza de cada día especial de hoy con su explicación, la nota **«Relato de ficción con base histórica»**, una línea que aclara que Demeas no existió, y las fuentes (las del día y las del personaje) como enlaces.
- **La edad del retrato no cambia,** como pide el plan: la que avanza solo aparece en el cuento.
- **La ruta directa** `otras_vidas?vida=remero&seccion=cuento` abre la pantalla con esa vida elegida y baja sola hasta el cuento. Todavía nada la usa: la usarán el cuadro de Hoy (P6) y «Hoy en la historia» (H2).
- **Los enlaces de las fuentes** abren el navegador solo si los tocas. La app no se conecta a nada.

## Lo que decidí y no estaba en el plan

1. **El título de un día normal es el objeto del día.** El plan no decía de dónde sale. Es corto, cambia cada día y sirve para la cabecera del cuadro. **[Diseño propio]**
2. **El cuento cambia un párrafo con la hora.** El plan dice que el cuento «usa el tramo como contexto». Lo resolví con un solo párrafo que empieza con «A esta hora»; los otros siete son los mismos todo el día.
3. **Estados, fechas y meses en las variantes.** Sin eso el cuento ponía a Demeas en su casa mientras estaba en el mar, a su padre vivo después de muerto, o remando con la nave guardada.
4. **Tres ajustes al calendario de Demeas** que pidió el cuento:
   - cada invasión y cada viaje de la flota tienen su propio `id`, porque no comparten texto (antes cuatro invasiones se llamaban igual);
   - la flota vuelve el 18 de septiembre y no el 2 de octubre, para que esté en casa en los Misterios;
   - las Panateneas de la temporada 1 tienen otro `id`, porque ese año las pasa en el mar.
   - De ahí sale una regla nueva para los calendarios, la 9: la fiesta de siempre que cae en un viaje lleva otro `id`.
5. **Una regla más para los textos:** la fiesta que vuelve cada año no nombra a nadie que vaya a morir o que todavía no haya nacido, porque su texto se usa los cinco años.
6. **La captura es la superficie `cuento`,** no `otrasVidas`. La de `otrasVidas` saca la pantalla en los 34 temas (68 imágenes) y solo uno tiene cuento. La nueva saca cinco imágenes de Helénico.

## Para las tandas que siguen

- **P3b y las T:** copian `PiezasDemeas.kt`, escriben las suyas y las agregan a `TodasLasPiezas`. Las pruebas generales de `CuentoPrueba` las revisan solas con `Piezas.problemas()`:
  - al menos 6 variantes por lugar y 5 que valgan todo el año;
  - al menos 3 variantes de trabajo por cada cosa que el personaje hace en «Un día suyo»;
  - un objeto por cada objeto del calendario, y oraciones para cada tiempo y cada estación de su lugar;
  - texto para todos los días especiales de las temporadas escritas; el primer día, 3 o 4 párrafos;
  - ningún año escrito, ningún hueco desconocido.
- **Volumen real:** Demeas llevó 245 textos cortos, no los 120 del plan. La diferencia son las variantes «en el mar» (25), las de cada hora del día y los días de las fiestas largas. Un personaje que no viaja ni cambia de estado necesita unos 150.
- **Y2028 a Y2031:** escriben los días especiales de cada temporada en `especiales` y agregan esa temporada a `temporadasEscritas`. Si un personaje cambia de estado (enfermo, de viaje), agregan variantes con ese `estado`.
- **P6:** el título para la cabecera del cuadro es `Cuento.de(día, vida, hora).titulo`, y `especial` enciende la marca.

## Verificación

- **Arnés local:** compila sin avisos y pasan las **70 pruebas** (35 de P1, 17 de P2 y 18 nuevas).
- **Las que pide el plan:**
  - **no aparece ningún año actual** en ningún cuento de 2026 a 2032;
  - **ningún cuento se repite**: ni en 365 días ni en los seis años de 2026 a 2031, a seis horas distintas del día;
  - **cada día especial dice su certeza**, y lo inventado nunca lleva fuente;
  - **la nota «Relato de ficción con base histórica»** está siempre.
- **Las demás:**
  - siempre son ocho párrafos enteros, sin huecos ni espacios de más, y el cuento no pasa de 2,400 letras;
  - es el mismo cuento todo el día, salvo el párrafo del trabajo;
  - el primer día de cada día especial cambia 3 o 4 párrafos;
  - en un especial largo, de un día al siguiente cambian al menos cuatro párrafos;
  - Demeas está en el mar del 5 de julio al 17 de septiembre, y en tierra no sale ninguna variante del mar;
  - nadie aparece antes ni después de tiempo (el padre, el tío, la hermana casada);
  - de noviembre a abril ningún día normal lo pone a remar;
  - el cuento sigue la guerra: lo que se ve desde el muro solo aparece después de la invasión.
- **Compilación de Android y capturas:** ver el final del PR; se lanzaron `apk.yml` y `capturas.yml` (con `superficies = cuento`) sobre la rama.

## Lo que no se puede comprobar aquí

- **Que el desplazamiento automático al cuento funcione** con la ruta directa: Paparazzi no lo muestra. Hay que probarlo en el teléfono cuando P6 la use.
- **Que el cuento se lea bien en una pantalla chica** con la letra agrandada: las capturas usan la letra al 100 %.
- **Los datos de época de los párrafos** (el cojín, las monedas en la boca, las fiestas) vienen de lo que sé de las fuentes antiguas y de las páginas abiertas en P2; no se abrió una fuente por cada párrafo. Son cuentos, y la nota al pie lo dice.
