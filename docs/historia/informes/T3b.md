# Tanda T3b: los cuentos de K'an y Nanai-vandak

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T3a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros dos personajes del grupo 3. Con esta tanda, **los cinco del grupo 3 tienen cuento**: en **Otras vidas**, con su tema, empiezan con «Hoy, en su vida».
- **K'an**, escriba y pintor de códices en una ciudad maya, del 750 al 754 (tema Mesoamérica);
- **Nanai-vandak**, mercader sogdiano de caravana entre Samarcanda y Chang'an, en los mismos años (Ruta de la Seda).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasKan.kt` | Los textos de K'an y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasNanai.kt` | Los de Nanai-vandak |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los dos |
| `vidas/anios/AnioG3.kt` | Dos arreglos al calendario de Nanai-vandak (ver abajo) |
| `app/src/test/…/vidas/CuentoG3Prueba.kt` | 5 pruebas más, propias de estos dos |
| `app/src/test/…/vidas/AnioG3Prueba.kt` | Una comprobación más: el orden del camino hacia el oeste |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`).
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper. Los calendarios de Otras vidas no se guardan.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **En Nanai-vandak lo normal es el camino.** Al revés que en los demás, las variantes de todos los días son las de la caravana (el caravasar, los camellos, el desierto), y las dos ciudades donde pasa el verano son estados, cada una con tres mañanas, once textos de trabajo repartidos entre sus horas, tres pueblos, tres anocheceres y dos tiempos. Son unos cinco meses al año en cada ciudad, así que vigilé que no se repitan.
2. **El camino se ve en el pueblo.** En la caravana, el párrafo del pueblo lee el arco «ruta»: los valles de Sogdiana cerca de Samarcanda, las montañas y los pasos, los desiertos con sus oasis, el corredor del oeste y, ya cerca de Chang'an, las colinas de loess con casas cavadas en ellas.
3. **K'an pasa en Yaxchilan casi todo el mes de la embajada.** El viaje en canoa se cuenta en los días de salir y de volver; en medio, amanece en la casa de los huéspedes, copia los dinteles de la ciudad, reza en sus templos y duerme con el ruido del río grande. Sus textos valen antes y después de la subida al trono de Pájaro Jaguar IV.
4. **Los dos son hombres**, como en su ficha.

## Dos errores del calendario de A3 que encontré al escribir

- **El camino de las temporadas 3 y 5 iba al revés.** Saliendo de Chang'an hacia el oeste, Nanai-vandak pasaba por Kucha el 10 de noviembre y por el corredor del Hexi el 1 de diciembre, pero el Hexi está entre Chang'an y Kucha. Además, llegar a Kucha en 43 días serían unos 70 km diarios, más del doble de lo que anda una caravana.
  - En la temporada 3, ahora nieva en el corredor el 25 de octubre y llega a Kucha el 20 de diciembre.
  - En la temporada 5 los sucesos no pueden pasar de mediados de noviembre (por la regla de los años que se mueven), así que cambié Kucha por **Liangzhou** (20 de octubre), la ciudad de los sogdianos del corredor, donde le preguntan por Farn.
  - Una comprobación nueva en `AnioG3Prueba` vigila que, yendo al oeste, el corredor venga antes que Kucha.
- **La luna de otoño prometía «pasteles redondos»**, pero los pasteles de luna son de siglos después de los Tang. El título dice ahora «se mira la luna con vino en los tejados de Chang'an». **[Aproximación]**

## Un cuento de muestra

**Nanai-vandak, en Chang'an** (2 de junio de 2027, a las 9):

> Nanai-vandak se despierta en la casa de Shi Ping, en el barrio de junto al mercado del oeste, con los tambores de la mañana: en Chang'an los barrios tienen muros y puertas, que se cierran de noche y se abren con los tambores. Suenan cientos, por toda la ciudad, uno detrás de otro.
>
> (…)
>
> A esta hora vende lo que trajo del oeste: el vidrio, el almizcle, la plata, los tapetes. Los compradores chinos tocan el vidrio como si fuera a deshacerse. Nanai-vandak les cuenta de dónde viene, alargando el camino un poco, porque el camino también se cobra.
>
> (…)
>
> Al caer la tarde suenan los tambores de la noche por toda la ciudad, y la gente corre para llegar a su barrio antes de que cierren las puertas. Nanai-vandak llega a tiempo, por poco. Dentro del barrio la vida sigue: tabernas, lámparas, música. Fuera, las avenidas se quedan vacías.

## Lo que cada cuento respeta de su calendario

**K'an**
- Ix Sak espera desde agosto de la temporada 2; su hija, desde que nace (marzo de la 3).
- Ah K'in, su maestro, hasta que muere (julio de la 3); después, un pensamiento dice que K'an ocupa su lugar sin nombrarlo.
- La estela se ve en obra en el pueblo mientras se talla (de febrero de la temporada 1 a mayo de la 2) y, desde que se levanta, K'an la lee cada mañana.
- En la **embajada** (temporada 3) amanece, trabaja, come, reza y duerme en Yaxchilan, con su tiempo y su pueblo.
- El pueblo lee la milpa: sin sembrar, con matas, alta, doblada.
- Las fiestas que vuelven cada año no nombran a la hija ni al maestro.

**Nanai-vandak**
- Farn se queda en Samarcanda hasta que sale con su padre (septiembre de la temporada 2); viaja con él; se enamora de la hija de Shi Ping (julio de la 3) y se queda en Chang'an; se casa en julio de la 5.
- Su hija se casa en julio de la temporada 2; el nieto, desde que nace (agosto de la 4).
- En **Chang'an** (temporadas 1, 3 y 5) y en **Samarcanda** (2 y 4) amanece, trabaja, come, reza, duerme y anochece allá; en enero y febrero de todos los años, en el camino.
- Las fiestas que vuelven cada año, y los días de llegar y salir de Chang'an y de Dunhuang, que también se repiten, no nombran a Farn, a su hija ni al nieto.

## Cuánto texto tiene cada uno

| | K'an | Nanai-vandak |
|---|---|---|
| Variantes de los ocho párrafos | 87 (22 de su estado) | 109 (44 de sus dos ciudades) |
| …de ellas, con fecha | 7 | 10 |
| Oraciones de tiempo y de estación | 21 | 29 |
| Frases de entorno y de mundo | 21 | 21 |
| Objetos del día | 12 | 12 |
| Días especiales de la temporada 1 | 16, con 63 párrafos | 16, con 49 |
| **Textos en total** | **240** | **256** |
| Letras en total | 39,362 | 44,445 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,133** | **2,169** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **135 pruebas**: las 130 de antes y 5 nuevas.
- **Las 5 nuevas** (`CuentoG3Prueba`):
  - que estén los dos, sin problemas frente a su calendario y con su género;
  - K'an: Ix Sak, la hija y el maestro a su tiempo, y la estela en obra solo mientras se talla;
  - K'an: el mes en Yaxchilan, que se nota y que varía;
  - Nanai-vandak: Farn, la hija y el nieto a su tiempo;
  - Nanai-vandak: los cinco veranos en Chang'an o en Samarcanda, que se notan y que varían, y los inviernos en el camino.
- **Las pruebas fallan cuando deben:** adelanté a la hija de K'an y devolví Kucha a noviembre; fallaron la de la hija, la del orden del camino y una general (que encontró 76 días seguidos sin nada especial), y se restauró.
- **Leí diez cuentos enteros**, cinco de cada uno. Salieron de ahí, además de los dos errores del calendario:
  - En octubre, un tiempo de K'an decía «el maíz que sube» mientras se cosecha.
  - Un anochecer de todo el año veía salir a Venus de tarde, pero Venus pasa meses sin verse de noche: ahora K'an la busca, y dice que unos meses sale de tarde, otros de mañana y otros se esconde.
  - Un cielo de Chang'an hablaba de «un azul que solo hay en el desierto».
  - A un mes de camino hacia el oeste, el pueblo hablaba de «las colinas de cerca de la capital»: separé el tramo del arco, y el loess solo sale cerca de Chang'an.
  - Al repasar los textos antes de probarlos quité dos detalles aztecas que no son mayas (la carga de ocho mil granos de cacao y el cacao falsificado), una escalera de Yaxchilan que se talló después de 752 y una exageración sobre las avenidas de Chang'an (la mayor medía unos 150 metros de ancho).
- **Compilación de Android:** pendiente; se agrega aquí al terminar la corrida de `apk.yml`.
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Las costumbres de las fiestas chinas** (las tablillas de melocotonero y el bambú que revienta del año nuevo, el vino que se bebe del más joven al más viejo, las ramas de sauce de las despedidas, las agujas de Qixi) son de la época Tang según lo que recuerdo de la literatura sobre ella; no las comprobé en la sesión. **[Aproximación]**
- **Los horarios de los mercados de Chang'an** (abrir al mediodía con tambores y cerrar antes del anochecer con gongs) y el toque de queda de los barrios siguen las normas Tang que recuerdo; tampoco los comprobé en la sesión. **[Aproximación]**
- **La vida diaria de K'an** (la casa de la escritura, los aprendices, la cueva de Chaak) es una reconstrucción a partir de lo que se sabe de los escribas mayas del Clásico tardío. **[Aproximación]**
- **Las personas son inventadas**, salvo los gobernantes y generales que se nombran (Pájaro Jaguar IV, Gao Xianzhi, An Lushan, Abu Muslim), que siguen las fuentes de A3.
