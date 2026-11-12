# Tanda T3a: los cuentos de Cayo, Pay y Ama

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A3. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los tres primeros personajes del grupo 3. En **Otras vidas**, con su tema, empiezan con «Hoy, en su vida»:
- **Cayo**, legionario en Viminacium, sobre el Danubio (tema Romano);
- **Pay**, escriba de la cuadrilla de las tumbas reales en Deir el-Medina (Egipto);
- **Ama**, pintora de cuevas hace unos 17,000 años (Prehistoria).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasCayo.kt` | Los textos de Cayo y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasPay.kt` | Los de Pay |
| `vidas/piezas/PiezasAma.kt` | Los de Ama |
| `vidas/piezas/TodasLasPiezas.kt` | Registra las tres |
| `app/src/test/…/vidas/CuentoG3Prueba.kt` | 7 pruebas propias de estos tres (archivo nuevo; T3b agregará las suyas) |
| `app/src/test/…/vidas/CuentoPrueba.kt` | Una prueba general que usaba al legionario como ejemplo de «personaje sin calendario» ahora busca sola uno que no lo tenga |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Los estados largos tienen varias variantes por párrafo.** Al leer los primeros cuentos vi que el verano de Ama en la meseta (más de tres meses cada año) tenía una sola mañana, un solo pueblo, un solo anochecer y un solo texto por hora de trabajo: todo el verano habría dicho lo mismo. Ahora tiene tres de cada uno. Hice lo mismo, en menor medida, con las seis semanas de Cayo en el camino del desfiladero y las diez de Pay en el paso. Una prueba nueva vigila que en esos estados salgan al menos tres mañanas, tres trabajos y tres anocheceres distintos.
2. **En verano Ama no pinta: graba.** Lejos de la cueva, a la hora de pintar talla caballos en placas de piedra y en asta, o mira a los caballos vivos para aprendérselos. Es lo que se ha encontrado en los campamentos al aire libre del Magdaleniense. **[Aproximación]**
3. **El cuento de Ama no cuenta los años**, como el de Naa: ni números, ni «a. C.», ni semanas, ni meses. Sí dice «año» e «invierno», que son ciclos que se ven. Una prueba lo vigila.
4. **Ama es mujer y Cayo y Pay son hombres**, como en su ficha.

## Un cuento de muestra

**Ama, en la meseta** (11 de julio de 2028, a las 8):

> Ama sale de la tienda y se estira. Huele a hierba, a humo y a la carne que se seca en los palos. Los perros ya andan alrededor de los palos, mirando la carne, y un niño los espanta con una piedra. En el verano todo el mundo está de buen humor.
>
> (…)
>
> A esta hora caza en la meseta con los demás: se tienden en la hierba alta contra el viento, con los propulsores listos, y esperan a que la manada de caballos se acerque. Ama no respira. Cuando Tor da la señal, las azagayas vuelan todas a la vez.
>
> (…)
>
> Al caer la tarde, en la meseta, el sol se va despacio sobre la estepa, enorme y rojo, y las manadas son sombras que se mueven lejos. Ama lo mira sentada en una piedra, con un hueso grabado en las manos, y piensa en la cueva, que la espera.

## Lo que cada cuento respeta de su calendario

**Cayo**
- Sabina, desde que la conoce (enero de la temporada 1); su embarazo, desde marzo de la 3; Gaia, desde que nace (noviembre de la 3).
- Durio, el recluta, desde marzo de la 2. Atilio, hasta que se va a la guerra de oriente (septiembre de la 4).
- Las guardias de noche y de puerta, hasta que lo hacen inmune (julio de la 3).
- En el **camino** del desfiladero (temporada 2) amanece, trabaja, come, duerme y cena allá, con su propio cielo y su propio pueblo.
- El pueblo lee la muralla que sube y, desde la partida de la vejilación, las calles sin fuegos.
- Las fiestas que vuelven cada año no nombran a Sabina, a Gaia ni a Durio.

**Pay**
- Tamit espera desde septiembre de la temporada 1; Meryt, desde que nace (mayo de la 2).
- Tiy, su madre, hasta que muere (febrero de la 3). Paneb es el capataz hasta que lo llevan ante el visir (junio de la 3); ese año se sigue hablando de la denuncia y después no se le nombra.
- En el **paso** (temporada 4, mientras se acaba la tumba del rey muerto) amanece, trabaja, come, se cura y duerme allá.
- El pueblo lee el río (la crecida) y la tumba del rey, que avanza, se cierra y vuelve a empezar.

**Ama**
- El embarazo, desde agosto de la temporada 1; Lin, desde que nace (abril de la 2).
- Yemi, su madre, hasta que muere (febrero de la 3); los textos de todos los días hablan de lo que su madre «le enseñó», que vale antes y después. Sel, desde la reunión (julio de la 3).
- El friso: llega a la mitad en noviembre de la 3 y se termina en octubre de la 5.
- Cada **verano** en la meseta (de junio a septiembre, los cinco años).
- El pueblo lee la nieve del valle.

## Cuánto texto tiene cada uno

| | Cayo | Pay | Ama |
|---|---|---|---|
| Variantes de los ocho párrafos | 84 (19 de su estado) | 92 (24) | 100 (30) |
| …de ellas, con fecha | 9 | 5 | 8 |
| Oraciones de tiempo y de estación | 29 | 24 | 29 |
| Frases de entorno y de mundo | 19 | 19 | 17 |
| Objetos del día | 12 | 12 | 12 |
| Días especiales de la temporada 1 | 17, con 58 párrafos | 16, con 60 | 16, con 55 |
| **Textos en total** | **238** | **243** | **249** |
| Letras en total | 39,268 | 41,467 | 40,724 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,089** | **2,181** | **2,013** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **130 pruebas**, 7 de ellas nuevas.
- **Lo que encontraron las pruebas generales:**
  - Al registrar a Cayo, una prueba que lo usaba como ejemplo de personaje sin calendario dejó de servir; ahora busca sola uno que no tenga.
  - Un «mundo» de Ama usaba `{nombre}`, y en las frases de mundo ese hueco no se llena. Lo quité.
- **Las 7 nuevas** (`CuentoG3Prueba`):
  - que estén las tres, sin problemas frente a su calendario y con su género;
  - Cayo: Sabina, Gaia, Durio y Atilio a su tiempo; el camino del desfiladero, que se nota y que varía;
  - Pay: Meryt, Tiy y Paneb a su tiempo; el paso, que se nota y que varía;
  - Ama: Lin, Yemi y Sel a su tiempo; los cinco veranos en la meseta, que se notan y que varían, y que el cuento no cuenta los años.
- **Las pruebas nuevas fallan cuando deben:** adelanté a Lin y dejé el verano de Ama con una sola mañana; fallaron las dos («Lin antes de nacer», «pocas mañanas distintas»), y se restauró.
- **Leí trece cuentos enteros**, de días comunes, de estados y de especiales. Además de lo del punto 1 de «Lo que decidí», salieron de ahí:
  - Cielos que hablaban de «el abrigo» o de «la nieve» y que podían caer en julio en la meseta: los reescribí para que valgan en los dos sitios.
  - Dos textos de Ama decían «su madre dice…» en presente, y su madre muere en la temporada 3: ahora dicen «le enseñó».
  - Dos datos que corregí en lo que yo mismo había escrito: el desfiladero está a dos días de remo de Viminacium, no a medio; y en primavera Sirio no «sube», sino que se pone cada noche más temprano, hasta que reaparece al amanecer con la crecida.
- **Compilación de Android:** pendiente; se agrega aquí al terminar la corrida de `apk.yml`.
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **La vida diaria de Ama** es una reconstrucción a partir de lo que se sabe del Magdaleniense (abrigos, campamentos de verano, caza de renos en los vados, placas grabadas, manos sopladas). **[Aproximación]**
- **Las personas de los tres son inventadas** (Sabina, Atilio, Tamit, Huy, Tor, Oko, Neta…); Paneb es real y lo que se dice de él sigue la denuncia que se conserva (ver `fuentes.md`, A3).
