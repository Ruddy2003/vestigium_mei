# Tanda T5b: los cuentos de Jean-Baptiste y del naturalista

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T5a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros dos personajes del grupo 5. Con esta tanda, **los cinco del grupo 5 tienen cuento**: en **Otras vidas**, con su tema, empiezan con «Hoy, en su vida».
- **Jean-Baptiste**, soldado de infantería, de la campaña de Rusia a su pueblo de Borgoña, de 1812 a 1816 (tema Napoleónico);
- **el joven naturalista**, a bordo de un bergantín que da la vuelta al mundo, de 1832 a 1836 (Botánico).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasJeanBaptiste.kt` | Los textos de Jean-Baptiste y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasNaturalista.kt` | Los del naturalista |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los dos |
| `vidas/anios/AnioG5.kt` | Tres estados nuevos que pedían los cuentos (ver abajo) |
| `app/src/test/…/vidas/CuentoG5Prueba.kt` | 5 pruebas más, propias de estos dos |
| `app/src/test/…/vidas/AnioG5Prueba.kt` | Comprueba los tres estados nuevos |
| `docs/historia/informes/T5a.md` | El resultado de su compilación |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`).
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Dos vidas que cambian de sitio, contadas por estados.** Jean-Baptiste tiene cinco: el cuartel antes de salir, Moscú, la retirada, el hospital de Prusia y su pueblo, donde pasa casi tres de sus cinco años. El naturalista tiene cuatro: sus temporadas en tierra, el mes enfermo, las Galápagos y su casa en Inglaterra. Cada uno con su mañana, su tiempo, su pueblo, su anochecer y un texto para cada cosa que hace a cada hora.
2. **Un mismo estado, varios lugares.** Las temporadas «en tierra» del naturalista son la casa de Río (1832), la pampa con los gauchos (1833) y los Andes (1834 y 1835). Los textos de cada lugar llevan sus fechas, así que en Río no salen gauchos ni en la pampa sale Botafogo.
3. **En el pueblo, la viña cambia con los meses:** se poda en invierno, se cava y se atan los brotes en primavera, se deshoja en verano y en otoño se trabaja en la bodega del tonelero. En el verano frío de 1816 el cielo lo dice.
4. **«El naturalista», sin hueco de nombre.** Su ficha se llama «Un joven naturalista», que en medio de una frase no funciona. Como con Odo y Bernat, el cuento escribe el nombre a mano, y una prueba vigila que no aparezca «joven naturalista». Como su ficha, no nombra a Darwin, al capitán ni al barco; sí nombra a Jemmy, el fueguino, que está en la página del viaje.
5. **Las fiestas que vuelven cada año valen en todas partes.** La Navidad de Jean-Baptiste cae en Prusia, en Maguncia y tres veces en su pueblo; la del naturalista, en el mar y, la última, en casa. Sus textos no dicen dónde están.

## Tres estados nuevos en el calendario de A5

Los cuentos los necesitaban para no decir cosas falsas; ninguno cambia una fecha:
- **«cuartel»** (Jean-Baptiste, del 20 de enero al 28 de febrero de la temporada 1): sin él, las últimas semanas en el cuartel salían como marchas por caminos de Rusia;
- **«hospital»** (Jean-Baptiste, del 10 de enero al 19 de marzo de la temporada 2): el invierno curándose los pies en Prusia;
- **«enfermo»** (el naturalista, del 20 de septiembre al 26 de octubre de la temporada 3): el mes en cama en Valparaíso cae en medio de su estancia en los Andes, y el estado nuevo va antes que «tierra» para mandar sobre ella.

El comentario de cada calendario dice qué tiene que mostrar el cuadro en esos días (el patio del cuartel, una sala de catres, un cuarto con una ventana al puerto), y `AnioG5Prueba` comprueba los tres.

## Un cuento de muestra

**Jean-Baptiste, a orillas del Berezina** (27 de noviembre de 2027, a las 9):

> A Jean-Baptiste lo despierta un disparo cerca: los cosacos rondan el vivac de noche y se llevan a los que duermen lejos del fuego. Lucien ya está de pie con el fusil. Esperan. No pasa nada más. Se van con la primera luz.
>
> (…)
>
> A esta hora espera en la orilla, en el frío, junto a un fuego, con Lucien, que tiene fiebre y ya no puede andar. Los oficiales dicen que se cruce de noche, que de día los rusos disparan. Lucien dice que mañana. Jean-Baptiste dice que mañana.
>
> (…)
>
> Al anochecer, junto al río, miles de hombres esperan sin cruzar, alrededor de fogatas, como si los puentes no fueran a desaparecer nunca. Jean-Baptiste le da a Lucien su último trozo de caballo. Lucien se lo come despacio y le dice que mañana estará mejor.

## Lo que cada cuento respeta de su calendario

**Jean-Baptiste**
- Lucien marcha con él hasta que se queda en la otra orilla del Berezina (29 de noviembre de la temporada 1); después, se le recuerda, y desde que vuelve al pueblo, su padre el herrero.
- El capitán Fabre, hasta el regimiento nuevo (marzo de la 2); el sargento Morel desde entonces, que le enseña a leer desde julio y le escribe desde Lyon en la 5.
- Margot, desde que la conoce (agosto de la 3); prometidos desde noviembre de la 4; casados desde febrero de la 5; espera un hijo desde agosto.
- **El cuartel, Moscú, la retirada, el hospital y el pueblo**, cada uno con lo suyo.
- El pueblo de campaña lee cuántos quedan de su compañía.
- Las fiestas que vuelven cada año no nombran a Lucien, a Morel ni a Margot.

**El naturalista**
- Los tres fueguinos viajan a bordo hasta la misión (enero de la temporada 2).
- El muchacho que le ayuda, desde abril de la 2.
- **En tierra**: Río (1), la pampa (2) y los Andes (3 y 4), cada uno con sus fechas; **enfermo** en Valparaíso (3); las **Galápagos** (4); **su casa** (5).
- El pueblo del bergantín lee los cajones de especímenes, que se vacían con cada envío.
- Las fiestas que vuelven cada año no nombran a los fueguinos ni al muchacho.

## Cuánto texto tiene cada uno

| | Jean-Baptiste | El naturalista |
|---|---|---|
| Variantes de los ocho párrafos | 186 (119 de sus estados) | 177 (119 de sus estados) |
| …de ellas, con fecha o con meses | 31 | 62 |
| Oraciones de tiempo y de estación | 29 | 20 |
| Frases de entorno y de mundo | 9 | 9 |
| Objetos del día | 12 | 12 |
| Días especiales de la temporada 1 | 21, con 74 párrafos | 16, con 48 |
| **Textos en total** | **346** | **302** |
| Letras en total | 66,283 | 59,090 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,097** | **2,194** |

Son los cuentos más grandes hasta ahora, porque cada estado es casi un cuento aparte.

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **171 pruebas**: las 166 de antes y 5 nuevas.
- **Un error mío que encontré a tiempo:** para no repetir los textos de los Andes en dos tramos (antes y después del mes enfermo) los había copiado con fechas distintas, y la prueba general de textos repetidos lo habría rechazado. El mes enfermo pasó a ser un estado propio.
- **Las 5 nuevas** (`CuentoG5Prueba`):
  - que estén los dos, sin problemas frente a su calendario y con su género, y que el naturalista no use el hueco del nombre;
  - Jean-Baptiste: Lucien, Fabre, Morel, la lectura y Margot (prometida, mujer y embarazada) a su tiempo;
  - Jean-Baptiste: el cuartel, Moscú, la retirada, el hospital y el pueblo, que se notan a todas las horas y que varían;
  - el naturalista: los fueguinos y el muchacho a su tiempo;
  - el naturalista: Río, la pampa y los Andes sin mezclarse, el mes enfermo, las Galápagos y su casa, que se notan y que varían.
- **Las pruebas nuevas fallan cuando deben:** le quité a Lucien la fecha en que se queda en el Berezina y le cambié al mes enfermo su estado; fallaron las dos, y se restauró.
- **Leí trece cuentos enteros**, seis o siete de cada uno. Salieron de ahí:
  - los cielos de Jean-Baptiste hablaban de «la columna» y salían también en el cuartel, el hospital y el pueblo: ahora no dicen dónde está;
  - el pueblo del naturalista decía que «la cabina se estrecha» de cajones también en la pampa, en Río y en su casa: el entorno de los cajones solo sale a bordo;
  - en su casa de Inglaterra, la estación decía que «al otro lado del mundo las estaciones van al revés»;
  - en las Galápagos, una lluvia fuerte seguida de «el sol pega como en un horno»;
  - dos frases seguidas con «el sol pega» en el verano del pueblo.
- **Al repasar los textos antes de probarlos** corregí: unos cosacos en un puesto de guardia de los textos de todos los días, que también salía en Bélgica en 1815; una tortuga gigante a la que el naturalista se subía en un texto de todos los días, cuando subirse a una es un día especial de la temporada 4; que los zapatos de la tropa eran iguales para los dos pies (lo dice ahora el dato de las botas, que en el cuento siguen llamándose botas); la Cruz del Sur «que aprendió a encontrar en el ecuador» en enero, antes de cruzarlo; y una partida de ajedrez con el capitán que no comprobé.
- **Compilación de Android:** la corrida 223 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `22ecb88`).
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **La vida del soldado** (los ocho del rancho, el alto de cada hora, las cantineras, la instrucción, las camas compartidas del cuartel, el santo de los soldados en Moscú, comer caballo con pólvora, meterse en los caballos muertos, las monjas del hospital) y **la del pueblo de viñas** (la poda con dos yemas, el ángelus en la viña, el tonelero) vienen de lo que se sabe de esos años, no de páginas abiertas en la sesión. **[Aproximación]**
- **La del viaje** (la hamaca sobre la mesa de las cartas, las pasas contra el mareo, la red de popa, el «filósofo», las boleadoras enredadas, el mate, la puna y las cebollas, las papas que no se cuecen en la altura, las iguanas que comen algas, la frase del padre sobre la forma de su cabeza) viene del diario y de las cartas de Darwin, de memoria; lo que sí dice la página del viaje (Praia y las conchas, el ecuador, Bahía, Río y las cartas, Botafogo y el bote anegado, el primer cajón, Punta Alta, Tierra del Fuego, los esclavos, las fechas) va en `fuentes.md` desde A5. **[Aproximación]**
- **Las personas son inventadas**, salvo los hechos y los hombres de las noticias, Jemmy y el capitán sin nombre, que siguen las fuentes de A5.
