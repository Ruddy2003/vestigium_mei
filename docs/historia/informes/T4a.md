# Tanda T4a: los cuentos del hermano Odo, el hermano Bernat y Zaynab

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A4. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los tres primeros personajes del grupo 4. En **Otras vidas**, con su tema, empiezan con «Hoy, en su vida»:
- el **hermano Odo**, monje copista en una abadía benedictina del Rin (tema Scriptorium);
- el **hermano Bernat**, sargento del Temple en el castillo de Safed (Templario);
- **Zaynab**, tejedora de seda en Córdoba (Al-Ándalus).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasOdo.kt` | Los textos de Odo y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasBernat.kt` | Los de Bernat |
| `vidas/piezas/PiezasZaynab.kt` | Los de Zaynab |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los tres |
| `vidas/piezas/Piezas{Cayo,Pay,Kan,Nanai,Jose,Mundo,Mary}.kt` | Doce textos de tandas anteriores que decían la hora (ver abajo) |
| `app/src/test/…/vidas/CuentoG4Prueba.kt` | 7 pruebas propias de estos tres (archivo nuevo; T4b agregará las suyas) |
| `app/src/test/…/vidas/CuentoPrueba.kt` | Una prueba general nueva: lo que se hace varias veces al día no dice la hora |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **«Odo» y «Bernat», sin «Hermano».** En su ficha se llaman «Hermano Odo» y «Hermano Bernat», y el hueco del nombre los ponía así en medio de una frase («después de prima, Hermano Odo se lava…»). En estos dos cuentos escribo el nombre a secas, como se llamaban en el claustro y en la orden. Una prueba vigila que no vuelva a salir «Hermano» con mayúscula. No cambié las fichas.
2. **El Ramadán de Zaynab cambia el día entero.** Cada hora tiene su variante de Ramadán: la comida del alba antes de amanecer, el trabajo en ayunas, las horas de comer en que no se come, el baño corto, la cocina para romper el ayuno y la noche en la calle. También la mañana, el tiempo, el pueblo y el anochecer.
3. **Los estados largos varían**, como en T3a: Lorsch (seis semanas), la enfermería (siete) y Acre (cinco) tienen al menos tres mañanas, tres trabajos y tres anocheceres distintos, y una prueba lo vigila.
4. **Lo que cambia con los años va con fecha:** la Biblia de Odo (la primera inicial, el primer tomo, la Biblia en el coro), su vista y su llave de bibliotecario; el Moro cojo y el comendador nuevo de Bernat; la tela del califa, la de la boda y el taller de Zaynab.

## Un error que venía de antes y que arreglé en todas las piezas

El párrafo del trabajo se elige por **lo que hace** el personaje a esa hora, no por la hora. Si alguien come dos veces al día, el mismo texto puede salir en las dos comidas; si reza cuatro veces, en las cuatro. Varios textos míos de esta tanda y de tandas anteriores lo olvidaban:
- «cena» en comidas que también son de mediodía (Odo, Bernat, Cayo, K'an, José, Alguien del mundo);
- «desayuno», «antes de salir» o «al mediodía» (Mary, Pay, Nanai-vandak, Zaynab);
- «después de maitines» o «en plena noche» en rezos y sueños que también son de día (Odo, Bernat).

Reescribí esos textos para que valgan a cualquiera de sus horas, como los de Demeas, que el autor aprobó («a esta hora come…»). Y agregué una prueba general, `loQueSeHaceVariasVecesAlDiaNoDiceLaHora`, que revisa todas las piezas: si una actividad se hace dos o más veces al día, sus textos no pueden decir «cena», «desayuno», «mediodía», «medianoche» ni «madrugada». Las tandas que siguen ya no pueden repetir el error.

## Un cuento de muestra

**Zaynab, en Ramadán** (20 de julio de 2027, a las 20:00):

> Zaynab se despierta en plena noche para la comida del alba, la única hasta que se ponga el sol. Alí se come los dátiles con los ojos cerrados. Yusuf bebe agua y más agua. Luego suena la llamada del alba, y ya no se come ni se bebe nada más hasta la noche.
>
> (…)
>
> A esta hora cocina para romper el ayuno, con hambre y con sed, que es cuando mejor se cocina: sopa de sémola, pan, dátiles, cordero con garbanzos y buñuelos con miel. La casa huele a especias. Alí pregunta cada poco si ya se puso el sol. Todavía no.
>
> (…)
>
> Anochece, y después de cenar Zaynab sale con Yusuf a las calles, que están llenas de gente y de luz. Compran buñuelos con miel en un puesto, saludan a los vecinos, oyen a un recitador en una esquina. Vuelven tarde. Mañana, otra vez el ayuno.

## Lo que cada cuento respeta de su calendario

**Odo**
- Heinrich, desde que llega (enero de la temporada 2).
- Gerhard es el bibliotecario hasta que enferma (noviembre de la 2), está en la enfermería hasta que muere (enero de la 3), y después se le recuerda.
- Gertrudis, su hermana, con Hildegarda desde mayo de la temporada 1.
- La piedra de leer desde octubre de la 4; la llave de la biblioteca, desde abril de la 5.
- En **Lorsch** (temporada 2) amanece, copia, reza, come y duerme allá, con su propio cielo y su pueblo.
- El pueblo lee el huerto del claustro.
- Las fiestas que vuelven cada año no nombran a Heinrich, a Gerhard ni a Gertrudis.

**Bernat**
- Ramon está en Egipto hasta que llega la noticia de Mansura (febrero de la temporada 1); desde la misa por los muertos, se le recuerda.
- Pere, desde que llega (marzo de la 2) hasta que muere (agosto de la 4); después, su tumba.
- El Moro cojea desde octubre de la temporada 2; el comendador nuevo, desde enero de la 5.
- **Herido** (temporada 2), amanece, trabaja, reza, come y duerme en la enfermería; en **Acre** (3), en la casa del Temple junto al mar.
- El pueblo lee la torre del sur, que sube.
- Las fiestas que vuelven cada año no nombran a Ramon ni a Pere.

**Zaynab**
- Alí, sin escuela, en la escuela (desde marzo de la temporada 1) y de aprendiz con su padre (desde abril de la 5).
- Juliana, soltera hasta su boda (mayo de la temporada 1) y casada después.
- El embarazo, desde septiembre de la 2; Maryam, desde que nace (mayo de la 3); Rabia, la aprendiza, desde febrero de la 4.
- Cada **Ramadán**, los cinco años.
- El pueblo lee el río.
- Las fiestas que vuelven cada año no nombran a Maryam ni a Rabia, ni dicen si Alí va a la escuela.

## Cuánto texto tiene cada uno

| | Odo | Bernat | Zaynab |
|---|---|---|---|
| Variantes de los ocho párrafos | 85 (19 de su estado) | 101 (37 de sus dos estados) | 100 (25 del Ramadán) |
| …de ellas, con fecha | 11 | 9 | 14 |
| Oraciones de tiempo y de estación | 29 | 29 | 31 |
| Frases de entorno y de mundo | 19 | 17 | 17 |
| Objetos del día | 12 | 12 | 12 |
| Días especiales de la temporada 1 | 18, con 62 párrafos | 18, con 61 | 16, con 58 |
| **Textos en total** | **243** | **256** | **254** |
| Letras en total | 40,356 | 43,991 | 43,090 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,182** | **2,199** | **2,162** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **149 pruebas**: las 141 de antes, 7 de `CuentoG4Prueba` y la general nueva.
- **Lo que encontraron las pruebas generales:** con «Hermano Odo» el cuento medio de Odo salía en 2,213 letras, por encima del tope de 2,200. Al escribir «Odo» bajó a 2,182 y el tope no se tocó.
- **Las 7 nuevas** (`CuentoG4Prueba`):
  - que estén los tres, sin problemas frente a su calendario, con su género y sin «Hermano» en medio de las frases;
  - Odo: Heinrich, Gerhard, Gertrudis y la piedra de leer a su tiempo; Lorsch, que se nota y que varía;
  - Bernat: Ramon, Pere, el Moro y los dos comendadores a su tiempo; la enfermería y Acre, que se notan y que varían;
  - Zaynab: el embarazo, Maryam, Rabia, la escuela de Alí y la boda de Juliana a su tiempo; los cinco Ramadanes, que se notan y que varían.
- **Las pruebas nuevas fallan cuando deben:** puse «cena» en un texto de comer de Odo y adelanté a Maryam; fallaron la general nueva y la de Zaynab, y se restauró.
- **Leí quince cuentos enteros**, cinco de cada uno. Además de lo de la hora, salieron de ahí:
  - el nombre con «Hermano» (ver «Lo que decidí»);
  - un huerto con rosales en flor a fines de septiembre;
  - un anochecer en la enfermería que contaba «treinta y dos, treinta y tres» días, y que sale cualquier noche de las siete semanas;
  - el día de la fiesta del fin del ayuno, Zaynab seguía en el telar: ahora el taller está cerrado los tres días.
- **Al repasar los textos antes de probarlos** corregí: dos textos de todos los días que nombraban a Heinrich antes de que llegara; uno que decía lo que «vivió» Gerhard estando vivo; las doce lecturas de los maitines de Navidad (había puesto nueve); el escudo templario (blanco con la cruz roja, no blanco y negro, que era el estandarte); el comendador, que en la temporada 5 es otro; una lupa y un sereno que no existían en la Córdoba del 970; y un detalle de la muerte del maestre que no comprobé.
- **Compilación de Android:** pendiente; se agrega aquí al terminar la corrida de `apk.yml`.
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Las costumbres de cada lugar** (la sangría de los monjes, las señas del claustro, los trece padrenuestros de maitines, la escudilla compartida de los templarios, sus tres días de carne, la pérdida del manto por un año y un día, las comidas del Ramadán en Córdoba, el tintorero judío y el carmesí del grano de las encinas) vienen de lo que se sabe de esas épocas, no de páginas abiertas en la sesión. **[Aproximación]**
- **Las personas son inventadas**, salvo Hildegarda, el rey Luis, el maestre Guillermo, el califa al-Hakam y su hijo, que siguen las fuentes de A4.
