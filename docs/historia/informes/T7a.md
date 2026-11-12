# Tanda T7a: los cuentos de la astronauta, la recolectora y el funcionario

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A7. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de tres de los cinco personajes de ficción del grupo 7. En **Otras vidas**, con su tema, empiezan con «Hoy, en su vida»:
- **la astronauta**, ingeniera de vuelo en una nave rumbo a Alfa Centauri, año 15 del viaje (tema Espacio);
- **la recolectora de semillas**, en una ciudad que el bosque recuperó, año 40 tras el silencio (Ciudad verde);
- **el funcionario**, empleado de un ministerio en la capital de un Estado que todo lo vigila, en 1984 (Distopía).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasAstronauta.kt` | Los textos de la astronauta y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasSemillera.kt` | Los de la recolectora |
| `vidas/piezas/PiezasFuncionario.kt` | Los del funcionario |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los tres |
| `app/src/test/…/vidas/CuentoG7Prueba.kt` | 7 pruebas propias de estos tres |
| `docs/historia/informes/A7.md` | El resultado de su compilación |

- **No toca el motor ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Sin el hueco del nombre.** Sus fichas se llaman «Una astronauta», «Una recolectora» y «Un funcionario», que en medio de una frase no funcionan. Como con el naturalista, el cuento escribe a mano «la astronauta», «la recolectora», «el funcionario», y una prueba vigila que no aparezca el nombre de la ficha.
2. **La nave no tiene cielo.** El párrafo del tiempo de la astronauta cuenta la ventana (las estrellas quietas, el Sol detrás, la estrella de delante) y la luz de a bordo; sus estaciones son las de la granja.
3. **El funcionario no copia la novela.** Su ficha se inspira en *1984*, pero ninguna palabra, persona ni oficina sale de ella: aquí hay «la pantalla», «el ministerio», el Fundador, la Guardia Joven, la Semana de la Gratitud y el Vocabulario Oficial, todo inventado. Sus días de fiesta (el año nuevo, el Fundador, el Desfile, la Victoria) tienen su «no trabaja».
4. **Lo que hacen varias veces al día no dice la hora.** La astronauta trabaja, come y tiene un rato propio dos veces (el ejercicio y la ventana); la recolectora trabaja y come dos veces; el funcionario trabaja tres (la gimnasia y los dos tramos del ministerio).
5. **Los viajes de la recolectora valen para los dos:** el del jardín botánico (temporada 2) y el de la costa (5) comparten textos, que no dicen quién va con ella.

## Los estados

- **La astronauta:** «parada» (cada octubre, el reactor apagado: media luz, frío, silencio, ella dentro del reactor) e «ingravidez» (temporada 3: todo flota, se come de bolsas, se duerme atada a la pared).
- **La recolectora:** «expedicion» (carreteras rotas, casas que nadie ha pisado en cuarenta años, turnos para vigilar de noche).
- **El funcionario:** «campana» (noventa horas, catres en la oficina, luces que no se apagan) y «provincia» (dos días en casa de su hermana: un pueblo sin pantallas, sopa de verdad).

Cada pueblo lee su arco: la granja de la nave, el puente viejo mientras se arregla (y si no, el huerto de la azotea) y las cámaras de la capital.

## Un cuento de muestra

**El funcionario, el día que Anselm no viene** (20 de febrero de 2027, a las 10):

> Hoy Anselm, su compañero de la mesa de al lado, no ha venido a trabajar. Su silla está vacía, su máquina tapada, su tubo cerrado. A media mañana ha pasado alguien del personal y se ha llevado sus cosas en una caja. Nadie ha preguntado nada.
>
> (…)
>
> A esta hora le llega por el tubo una carpeta con una instrucción: buscar el nombre de Anselm en los registros de la sección y quitarlo. Lo encuentra en cuarenta sitios: listas, actas, la foto de la fiesta del año pasado. Lo quita de todos. En la foto, ahora, hay un hueco con una planta.

## Lo que cada cuento respeta de su calendario

**La astronauta**
- Su madre vive hasta que llega la noticia (marzo de la temporada 2); desde entonces, sus mensajes guardados.
- Sana: aprendiz desde junio de la 1, ingeniera desde septiembre de la 2, madre desde abril de la 5.
- Ade: amigo hasta que le pide vivir juntos (diciembre de la 3); juntos desde febrero de la 4.
- Las fiestas que vuelven cada año no nombran a Sana, a Ade, a Varga, a Nilo ni a su madre.

**La recolectora**
- Lía, desde mayo de la 1. Amalia sube a la azotea hasta agosto de la 3, está en cama hasta febrero de la 4 y desde entonces se la recuerda.
- Teo: juntos desde diciembre de la 2; ella espera desde abril de la 4; su hija nace en diciembre de la 4.
- El puente roto hasta junio de la 4; arreglado desde entonces.
- Las fiestas que vuelven cada año no nombran a Lía, a Amalia ni a Teo.

**El funcionario**
- Anselm, hasta que desaparece (febrero de la 1); después, su mesa la tiene otro.
- Mira, de marzo de la 2 a agosto de la 3. El vecino del violín, hasta junio de la 2.
- Las fiestas que vuelven cada año no nombran a Anselm ni a Mira.

## Cuánto texto tiene cada uno

| | La astronauta | La recolectora | El funcionario |
|---|---|---|---|
| Variantes de los ocho párrafos | 100 (30 de sus estados) | 85 (14) | 94 (26) |
| …de ellas, con fecha o con meses | 8 | 12 | 8 |
| Oraciones de tiempo y de estación | 10 | 29 | 29 |
| Frases de entorno y de mundo | 9 | 11 | 9 |
| Objetos del día | 12 | 12 | 12 |
| Días especiales de la temporada 1 | 19, con 60 párrafos | 17, con 59 | 17, con 56 |
| **Textos en total** | **227** | **232** | **236** |
| Letras en total | 42,653 | 40,197 | 41,683 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,181** | **2,172** | **2,170** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **202 pruebas**: las 195 de antes y 7 nuevas.
- **Las pruebas generales me pararon una vez:** la astronauta pasaba del largo medio (2,242 letras, con un tope de 2,200); le quité una frase a dieciocho textos.
- **Las 7 nuevas** (`CuentoG7Prueba`):
  - que estén los tres, sin problemas frente a su calendario, con su género y sin el hueco del nombre;
  - la astronauta: su madre, Sana y Ade a su tiempo;
  - la astronauta: la parada (dos octubres) y el anillo parado a todas las horas, y que varían;
  - la recolectora: Lía, Amalia, Teo, la hija y el puente a su tiempo;
  - la recolectora: los dos viajes a todas las horas;
  - el funcionario: Anselm, Mira y el vecino del violín a su tiempo;
  - el funcionario: las dos campañas, la visita a su hermana y sus cuatro fiestas sin trabajo, los cinco años.
- **Una prueba nueva atrapó un error mío:** los especiales de la siembra y de la cosecha de la astronauta, que vuelven cada año, nombraban a Ade; ahora dicen «el botánico».
- **Las pruebas nuevas fallan cuando deben:** les hice seis sabotajes a la vez (Sana desde el primer día, el anillo parado sin sus textos de trabajo, el viaje de la recolectora sin los suyos, Teo viviendo con ella desde siempre, el Día del Fundador sin «no trabaja», el funcionario como mujer); fallaron las seis pruebas tocadas, cada una por lo suyo, y la séptima siguió bien. Se restauró todo.
- **Leí dieciocho cuentos enteros**, seis de cada uno. Salieron de ahí:
  - «quince años» en textos de todos los años de la astronauta (de viaje, de lluvia, de ajedrez), que en la temporada 5 ya son diecinueve;
  - el tiempo y la estación de la recolectora hablaban de «la azotea» también de viaje;
  - en la campaña, el funcionario miraba el cielo «al salir del portal», sin salir del ministerio; en la provincia, el verano hablaba de los bloques de la capital; y un «Duran una hora».
- **Al repasar los textos antes de probarlos** corregí: Sana en el especial de la parada, que vuelve cada año; un «desayuno» en un especial; y un «cena» en un pensamiento.
- **Un límite que queda:** el funcionario hace la gimnasia ante la pantalla en el mismo tipo de tramo que el trabajo del ministerio, así que a las 7 puede salir un texto de la oficina y a media mañana no sale la gimnasia (la gimnasia va en la mañana).
- **Compilación de Android:** la corrida 228 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `5d876e7`).
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Todo es inventado**, como sus fichas. Lo único de la física de verdad en la nave (la décima de la velocidad de la luz, los treinta mil kilómetros por segundo, los dos años de los mensajes, los −270 grados de fuera) sigue lo que dice `fuentes.md` desde A7. **[Aproximación]**
