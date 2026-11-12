# Tanda T4b: los cuentos de Bat e Iacopo

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T4a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros dos personajes del grupo 4. Con esta tanda, **los cinco del grupo 4 tienen cuento**: en **Otras vidas**, con su tema, empiezan con «Hoy, en su vida».
- **Bat**, pastor nómada a orillas del Kerulen, de 1220 a 1224 (tema Mongol);
- **Iacopo**, aprendiz en el taller de Domenico Ghirlandaio, en Florencia, de 1490 a 1494 (Renacimiento).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasBat.kt` | Los textos de Bat y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasIacopo.kt` | Los de Iacopo |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los dos |
| `vidas/piezas/Piezas{Odo,Zaynab}.kt` | Las fiestas de guardar con su propio párrafo de trabajo (ver abajo) |
| 16 piezas de tandas anteriores | Frases de «mundo» que contaban como pasado lo que llega a mitad de año (ver abajo) |
| `app/src/test/…/vidas/CuentoG4Prueba.kt` | 5 pruebas más, propias de estos dos |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **El verano de Bat es un estado largo**, como el de Ama: cuatro meses al año en el llano junto al río, con su ordeño de yeguas, su airag, su caza de marmotas y sus luchas. Cada hora tiene tres variantes, y también la mañana, el pueblo, el tiempo y el anochecer.
2. **El campamento de invierno también se vive en mayo y en octubre.** Los textos de todo el año no hablan de nieve ni de manos congeladas: lo que es solo del frío lleva sus meses.
3. **En las fiestas de guardar no se trabaja.** En Pascua, la Anunciación, San Juan, Todos los Santos y San Lucas, Iacopo tiene su propio párrafo de trabajo («a esta hora no trabaja…»). Al revisarlo vi lo mismo en T4a: Odo copiaba en la Candelaria, en san Benito, en Pentecostés, en Todos los Santos y en san Martín, y Zaynab tejía el día de las almunias. Ahora no. En el castillo de Bernat y en la estepa de Bat las fiestas no paran el trabajo, porque los caballos y el ganado no esperan.
4. **El taller sin el maestro.** Desde que muere Domenico (enero de la temporada 5), la comida, la gente y lo que se dice del taller cambian: los textos de todos los días no hablan de él como vivo.

## Un error que venía de antes y que arreglé en todas las piezas

La frase de **mundo** de cada temporada sale en el pensamiento **todo el año**, del 1 de enero al 31 de diciembre. Varias mías contaban como ya pasado un hecho que llega a mitad de año: «se habla de la muerte de Lorenzo» desde enero, cuando muere en abril; «se habla del invierno del hielo» antes de que llegue; la muerte de Kennedy desde enero de 1963.

Las revisé todas, las de esta tanda y las de las tandas T1 a T4a, y reescribí 48 para que valgan el año entero (7 de esta tanda, 18 de T4a, 10 del grupo 3, 8 del grupo 2 y 5 del grupo 1): como tema que se comenta («se habla de Piero, el hijo de Lorenzo, que no se parece a su padre») o como algo que se espera («se habla de la guerra contra los partos, que se huele en el aire»). Lo que pasa en un día concreto ya lo cuentan los días especiales. La encontró una prueba nueva de esta tanda (la del invierno del hielo de Bat) y busqué el resto a mano. No hay una prueba general que la vigile, porque una máquina no distingue «se habla de la batalla» de «se habla de la guerra»; queda anotado aquí para las tandas que siguen.

## Un cuento de muestra

**Bat, en el verano del llano** (3 de julio de 2027, a las 5:30), durante la esquila:

> Bat sale de la tienda al amanecer con un cuenco de airag del día anterior y echa unas gotas hacia el cielo, hacia la tierra y hacia los cuatro lados. En verano la ofrenda es de airag, que es lo mejor que hay. Luego se bebe el resto. Hoy hace calor.
>
> (…)
>
> El río en verano es ancho y lento, con orillas de hierba y de piedras, y bancos de arena donde los niños se bañan y los caballos beben. Hay garzas, patos, y peces que nadie pesca. Es el centro del verano: todo está cerca del río.
>
> (…)
>
> Al anochecer vuelven los que estaban lejos con los caballos, y el campamento se llena de ruido: relinchos, perros, gritos, niños. Las mujeres reparten la carne. Los hombres beben. Arriba, la primera estrella, sola. Bat la saluda.

## Lo que cada cuento respeta de su calendario

**Bat**
- Khulan espera desde agosto de la temporada 1; Saran, desde que nace (marzo de la 2) y montando sola desde mayo de la 5.
- El potro blanco, desde que nace (mayo de la 1).
- Su madre manda en el campamento hasta que enferma (enero de la 3), está enferma hasta que muere (marzo de la 4) y después su tienda ya no está.
- Temür sigue en la guerra hasta que vuelve (julio de la 5).
- El invierno del hielo se recuerda desde febrero de la temporada 4.
- Cada **verano** en el llano, los cinco años.
- El pueblo lee la hierba.
- Las fiestas que vuelven cada año no nombran a Saran, a Temür ni al potro blanco.

**Iacopo**
- Caterina, desde que la conoce (julio de la temporada 1); prometidos desde julio de la 4.
- Nanna, soltera hasta su boda (octubre de la 3) y casada después.
- El maestro Domenico, vivo hasta enero de la temporada 5; después, el taller sin él.
- La tabla, de marzo de la 4 a mayo de la 5, y después en casa del sedero.
- Miguel Ángel en el jardín de los Médici, hasta la muerte de Lorenzo (abril de la 3).
- En el **Mugello** (temporada 3) amanece, pinta, come, sale y duerme allá, con su propio cielo y su pueblo.
- El pueblo lee la capilla y la tabla.
- Las fiestas que vuelven cada año no nombran a Caterina ni a Domenico.

## Cuánto texto tiene cada uno

| | Bat | Iacopo |
|---|---|---|
| Variantes de los ocho párrafos | 96 (26 de su estado) | 86 (19) |
| …de ellas, con fecha | 13 | 12 |
| Oraciones de tiempo y de estación | 29 | 29 |
| Frases de entorno y de mundo | 17 | 19 |
| Objetos del día | 12 | 12 |
| Días especiales de la temporada 1 | 17, con 68 párrafos | 16, con 55 |
| **Textos en total** | **258** | **237** |
| Letras en total | 43,502 | 40,020 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,112** | **2,181** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **154 pruebas**: las 149 de antes y 5 nuevas.
- **Lo que encontraron las pruebas:**
  - El cuento medio de Iacopo salía en 2,255 letras, 55 por encima del tope. Como era más que «un poco», acorté las frases de entorno del taller y tres datos de objeto, y quedó en 2,181.
  - La prueba de Bat encontró la frase de mundo del invierno del hielo antes de tiempo, y con ella el problema de arriba.
- **Las 5 nuevas** (`CuentoG4Prueba`):
  - que estén los dos, sin problemas frente a su calendario y con su género;
  - Bat: Khulan, Saran, el potro blanco, su madre, Temür y el invierno del hielo a su tiempo; los cinco veranos en el llano, que se notan y que varían;
  - Iacopo: Caterina, Nanna, el maestro, la tabla y Miguel Ángel a su tiempo; el Mugello, que se nota y que varía; y que en Pascua y en San Juan de los cinco años no trabaja.
- **Las pruebas nuevas fallan cuando deben:** adelanté la vuelta de Temür y le quité a Iacopo su párrafo de trabajo de Pascua; fallaron las dos, y se restauró.
- **Leí diez cuentos enteros**, cinco de cada uno. Salieron de ahí, además de lo de arriba:
  - en octubre, el pueblo de Bat decía «sale la hierba nueva»: el arco de la hierba vale lo mismo en primavera que en otoño, así que esas frases ahora valen en las dos;
  - la copa de plata «en la que bebe su madre», que muere en la temporada 4, y Saran «atada en la cuna» cuando ya monta sola;
  - Iacopo pintaba el domingo de Pascua (ver «Lo que decidí»).
- **Al repasar los textos antes de probarlos** corregí: los datos de objeto y las frases de entorno que usaban `{nombre}`, que en esos lugares no se llena; una comida con el maestro «de buen humor» que habría seguido saliendo después de su muerte; la capilla Sixtina, que en 1490 era «la del papa Sixto»; y el palio de San Juan, que no sé si se corría sin jinete.
- **Compilación de Android:** pendiente; se agrega aquí al terminar la corrida de `apk.yml`.
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Las costumbres de la estepa** (el lado de los hombres y el de las mujeres en la tienda, el umbral que no se pisa, la ofrenda de leche, la cuchara de nueve agujeros, la caza de marmotas con un trapo blanco, la caza en círculo, el violín de cabeza de caballo en 1220) vienen de lo que se sabe de los mongoles, sobre todo de viajeros de unas décadas después. **[Aproximación]**
- **Las del taller y la ciudad** (moler en pórfido, el yeso, el bol y el ágata, el cartón y el estarcido, la cofradía de San Lucas, la feria de la Anunciata y su fresco, el carro de Pascua en 1490, el palio, el calcio en carnaval) vienen de lo que se sabe del Quattrocento florentino, no de páginas abiertas en la sesión. **[Aproximación]**
- **Las personas son inventadas**, salvo los pintores del taller, Miguel Ángel, Savonarola, los Médici y los generales y monjes de las noticias de Bat, que siguen las fuentes de A4.
