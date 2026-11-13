# Tanda T8b: los cuentos de Ruth y Brenna

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T8a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros dos personajes de ficción del grupo 8. Con esta tanda, **los cuatro del grupo 8 tienen cuento**: en **Otras vidas**, con su tema, empiezan con «Hoy, en su vida».
- **Ruth**, colona de El Remanso, un pueblo que se reconstruye junto a un arroyo del llano, en 2288 (tema Colonos);
- **Brenna**, guía de caminos en la posada del Cuerno, al pie de un paso de montaña, en el año 312 de los Caminos (Tierras Antiguas).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasRuth.kt` | Los textos de Ruth y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasBrenna.kt` | Los de Brenna |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los dos |
| `app/src/test/…/vidas/CuentoG8Prueba.kt` | 5 pruebas más, propias de estos dos |
| `docs/historia/informes/T8a.md` | El resultado de su compilación |

- **No toca el motor ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Cómo se hizo

Igual que en T8a:
- Un subagente por personaje escribió el primer borrador, en paralelo con los de T8a, con el mismo encargo.
- Yo leí cada borrador entero contra su calendario, lo integré y corrí todas las pruebas.
- Leí quince cuentos de muestra.
- Escribí las pruebas propias y las saboteé.

## Lo que decidí

1. **Con su nombre:** Ruth y Brenna tienen nombre propio, y el cuento usa el hueco del nombre. La prueba comprueba que el nombre salga cada día.
2. **El día de Ruth:**
   - trabaja en dos tramos (el campo y el muro; la guardia y las reparaciones) y come dos veces, sin decir la hora;
   - por la tarde, la fogata y la radio de Los Silos.
3. **El día de Brenna no tiene «trabajo»:**
   - su trabajo es el camino, en dos tramos: guiar a la compañía, y el paso con sus marcas;
   - después cuida mapas y monturas, y por la noche está en la posada.

   Sus textos de comer y de camino no dicen ni la hora ni el sitio, para que valgan en la cocina de la posada y en una piedra del paso.
4. **El muro de Ruth:** el pueblo lee su arco, que vuelve a los mismos valores con el muro de tablas y con el de piedra. Por eso las frases del entorno solo dicen lo que le falta o lo que ya cierra. De qué está hecho lo dicen variantes con fecha: tablas, tablas quemadas, piedra que sube, piedra terminada.
5. **El pueblo de Brenna** lee primero el refugio del collado (a medio hacer o terminado) y, si aún no se ha empezado, la veta nueva.
6. **Sus días de fiesta tienen su «no trabaja»:**
   - Ruth: el año nuevo, la fundación, la noche de las canciones (salvo la guardia), los elotes y la noche más larga;
   - Brenna: el año nuevo, la feria (sus dos días), la Primera Runa, el Hierro, la Noche del Arpa y la última noche del año.
   - La Noche de las Linternas de Brenna cae el 3 de febrero, una de las fechas en que una prueba general compara el trabajo de la 1 y de las 8. Por eso cambia la mañana, el anochecer y lo que piensa, pero no el trabajo.

## Los estados

- **Ruth:**
  - «auxilio» (temporadas 1, 4 y 5): en otro pueblo del llano que pidió ayuda por la radio. Como se repite tres años, sus textos no dicen qué pueblo es ni quién va con ella.
  - «asedio» (3): nueve días rodeados, sin luz de noche y con el agua contada. Esteban no sale, porque muere en medio del asedio.
  - «encierro» (4): la ventisca, con los pasillos cavados en la nieve, la casa común y su madre enferma.
- **Brenna:**
  - «paso_cerrado» (cada invierno): la pala, las raquetas hasta la primera piedra, las runas que enseña a los chicos y los mapas que copia. Vuelve cada año, así que solo nombra a Hilde, la posadera, que está los cinco.
  - «travesia» (3): un mes al frente de la Compañía de Verano, hasta Torremuda y de vuelta. Tiene textos distintos antes y después de llegar.
  - «crianza» (5): las semanas con la recién nacida en el cuarto alto. Las dos fiestas que caen dentro (los bastones y la feria) no la nombran.

## Un cuento de muestra

**Ruth, el segundo día del asedio** (1 de agosto de 2029, a las 9):

> Ruth se despierta en el andador del muro, donde se quedó dormida sentada, con la escopeta entre las rodillas. Es de día. Afuera, en el llano, siguen las hogueras de los saqueadores, humeando, en un círculo que no se ha movido. Cuenta los humos. Los de ayer.
>
> (…)
>
> Al caer la tarde no se enciende el generador: un pueblo con luz es un blanco. El Remanso se queda a oscuras con el llano, y en el llano se encienden las hogueras de los de fuera, una a una, como un collar alrededor del cuello. Ruth sube al muro.

## Lo que cada cuento respeta de su calendario

**Ruth**
- **Nora:** aprende a leer hasta octubre de la 1, hace guardia desde octubre de la 4 y habla por la radio desde septiembre de la 5.
- **Elías:** lo espera desde julio de la 2, nace en marzo de la 3 y camina desde marzo de la 4.
- **Agustina,** su madre, hasta febrero de la 4.
- **Gabino:** en la casa de al lado hasta julio de la 4, en Cuatro Molinos después, y con su mujer y su niña desde octubre de la 5.
- **Esteban,** hasta el asedio. **Matías,** con el brazo herido desde entonces.
- **Joaquín y Lidia,** desde abril de la 2, y el jabón desde diciembre.
- **Celia,** solo su voz hasta que la conoce en junio de la 5.
- **Ella misma:** jefa de las guardias de noche desde mayo de la 2.
- **El muro y la torre:** el hueco del este hasta noviembre de la 1, el fuego, la piedra y la última piedra; la torre del agua desde octubre de la 2.
- **Las fiestas que vuelven cada año** no nombran a ninguno de ellos: solo a Marta, la que lleva el pueblo, y a Eusebio, el de las gallinas y el aguardiente, que están los cinco años.

**Brenna**
- **Tobin:** llega en junio de la 1; la canción, en mayo de la 2; se lo pide en noviembre de la 3; el cuarto alto, en enero de la 4; ella espera desde septiembre de la 4; la niña nace en abril de la 5.
- **El viejo Arn:**
  - guía hasta que se pierde en la niebla, en octubre de la 2;
  - la runa «camino que sigue», desde octubre de la 1;
  - su linterna, desde abril de la 3; su runa en Torremuda, desde agosto de la 3;
  - el refugio con su nombre, desde septiembre de la 4.
- **Neli:** desde agosto de la 2, aprendiz desde abril de la 3, guía su tramo desde mayo de la 4 y lleva sola una compañía desde agosto de la 5.
- **Corvin,** desde la veta nueva hasta que se va al valle en junio de la 5. **Bastián,** de abril de la 1 a junio de la 4.
- **Orla** guarda el libro de crónicas hasta octubre de la 5; desde entonces lo guarda ella.
- **El mapa del paso,** desde febrero de la 1. **El pino,** hasta el rayo de julio de la 5. **El refugio de la Cabra,** hasta el alud de febrero de la 2.
- **Las fiestas que vuelven cada año** no nombran a ninguno de ellos ni a la niña.

## Cuánto texto tiene cada uno

| | Ruth | Brenna |
|---|---|---|
| Variantes de los ocho párrafos | 127 (36 de sus estados) | 172 (69) |
| …de ellas, con fecha o con meses | 33 | 47 |
| Oraciones de tiempo y de estación | 31 | 29 |
| Frases de entorno y de mundo | 11 | 13 |
| Objetos del día | 12 | 12 |
| Días especiales de la temporada 1 | 20, con 87 párrafos | 20, con 77 |
| **Textos en total** | **304** | **339** |
| Letras en total | 57,618 | 67,535 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,079** | **2,101** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **222 pruebas**: las 217 de antes y 5 nuevas.
- **Las pruebas generales pasaron a la primera** con los dos borradores.
- **Las 5 nuevas** (`CuentoG8Prueba`):
  - que estén los dos, sin problemas frente a su calendario, con su género y con su nombre en el cuento;
  - Ruth: Nora, Elías, Agustina, Gabino, Esteban, Matías, Joaquín y Lidia, Celia, la jefatura de las guardias, la torre y cada etapa del muro a su tiempo;
  - Ruth: el auxilio de los tres años, el asedio y la ventisca a todas las horas, que varían, y sus cinco fiestas sin trabajo;
  - Brenna: Tobin y la niña, Arn y su runa, Neli, Corvin, Bastián, Orla, el pino, el refugio de la Cabra y el mapa a su tiempo;
  - Brenna: dos inviernos con el paso cerrado, la travesía y la crianza a todas las horas, que varían, y sus fiestas sin trabajo, la feria con sus dos días.
- **Las pruebas nuevas fallan cuando deben.** Hice cinco sabotajes:
  - Agustina viva hasta que Elías camina;
  - una comida del asedio convertida en sueño;
  - la noche más larga de Ruth sin «no trabaja»;
  - Orla nombrada en el recuento, que vuelve cada año;
  - las mañanas de la crianza sin su estado.

  En la primera corrida fallaron tres, cada una por lo suyo. Los otros dos no se llegaron a ver, porque otra comprobación de la misma prueba falló antes. Los repetí solos y fallaron también, cada uno por lo suyo. Después se restauró todo.
- **Leí quince cuentos enteros**, ocho de Ruth y siete de Brenna: días normales, todos sus estados y varias fechas de los cinco años. No encontré nada que corregir.
- **Al leer los borradores contra sus calendarios** comprobé:
  - que ninguna fiesta que vuelve cae dentro de los estados de Ruth;
  - que las fiestas de Brenna que caen en la crianza valen allí;
  - que las reglas del entorno de Brenna van en el orden justo: la roca entera solo puede salir antes de que se abra la veta;
  - que la jauría y el cierre del muro, que nombran a Esteban, son solo de la temporada 1.
- **Compilación de Android:** la corrida 237 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `ab22f16`).
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Todo es inventado**, como sus fichas. Ningún nombre ni término viene de una obra existente. **[Diseño propio]**
  - El Remanso, Los Silos, Piedra Blanca y Cuatro Molinos, y las fiestas del llano.
  - La posada del Cuerno, el Gremio de los Caminos, las runas, Torremuda y las fiestas del paso.
- **Desde 2032**, cuando la temporada 5 se repite, la salida de Ruth con las semillas puede caer junto a la noche de las canciones o la siembra, cuyos textos pasan en el pueblo. Además, alguna variante con fecha puede salir unos días antes o después, como en el grupo 7.

## Lo que sigue

Con T8b terminan las tandas del grupo 8. Antes de la revisión general, el autor lee un resumen de cada personaje y dice si hay que cambiar algo.

Lo que salió de eso y de la revisión está en `docs/historia/informes/Revision_cuentos.md`. Los nombres de este informe ya son los nuevos: Elías, Gabino, Esteban, Matías y Eusebio.
