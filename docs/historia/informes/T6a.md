# Tanda T6a: los cuentos de Albert, Nikos y Lou

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A6. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de tres de los cinco personajes del grupo 6. En **Otras vidas**, con su tema, empiezan con «Hoy, en su vida»:
- **Albert**, fogonero de locomotora en una línea del noreste de Inglaterra, de 1880 a 1884 (tema Vapor victoriano);
- **Nikos**, buzo esponjero de escafandra en un barco de Tarpon Springs, en el golfo de México, de 1910 a 1914 (Abismo);
- **Lou** (Louise, de Baltimore), pianista de un club de jazz de Harlem, de 1926 a 1930 (Art déco).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasAlbert.kt` | Los textos de Albert y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasNikos.kt` | Los de Nikos |
| `vidas/piezas/PiezasLou.kt` | Los de Lou |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los tres |
| `app/src/test/…/vidas/CuentoG6Prueba.kt` | 7 pruebas propias de estos tres |
| `docs/historia/informes/A6.md` | El resultado de su compilación |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios de A6.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Lou es mujer.** La ficha decía solo «Lou»; como pediste equilibrar, es Louise, de Baltimore, hija de una lavandera que le pagó las clases de piano. Albert y Nikos son hombres.
2. **Albert trabaja en las fiestas.** En los días de fiesta de los bancos los trenes de excursión van llenos: esos días su párrafo de trabajo cuenta el tren de la costa, el de las escuelas dominicales o el de la gala de Durham. No hay día de guardar en su cuento.
3. **Dónde vive Albert, año por año.** Su calendario dice que se muda a la calle de los ferroviarios en la temporada 4. Antes vive con su madre y con Tom en la calle de los mineros, y desde la boda con Ellen en otra casa, que el cuento no describe; los textos de todos los días dicen «su calle» y «su casa», y solo los fechados nombran cada sitio.
4. **Nikos trabaja dos veces al día y come tres.** Sus textos de trabajo cuentan unas veces el fondo y otras la cubierta (limpiar y ensartar esponjas), y valen a cualquier hora del tramo. En sus fiestas de guardar (Navidad y Epifanía con el calendario viejo, San Basilio, Pascua, la Dormición y San Nicolás) su párrafo dice que no trabaja.
5. **La isla de Nikos cambia de dueño.** Kálymnos era turca hasta mayo de 1912 y desde entonces italiana. Las fiestas que vuelven cada año no dicen de quién es: dicen que «todavía no es de Grecia».
6. **Lou come dos veces y vive al revés:** duerme de mañana, ensaya y se arregla por la tarde, toca de noche. Sus textos no dicen «desayuno», «cena» ni «madrugada» donde salen dos veces al día.

## Los estados

- **Albert:** «baja» (temporada 3, un mes en casa con el brazo quemado: una mañana, un trabajo para cada hora, una calle y un anochecer propios) y «maquinista» (desde que aprueba, en la 5: a los mandos, enseñándole el fuego a su fogonero).
- **Nikos:** «puerto» (cada invierno, de mediados de diciembre a fines de enero: la casa de huéspedes, el astillero, la lonja, el café de los griegos; desde la temporada 5, la casa de madera con Eleni) y «enfermo» (temporada 4: cinco semanas en un cuarto de la casa de huéspedes, con la enfermedad de los buzos).
- **Lou:** «gira» (temporada 3: los teatros negros del sur, el vagón de los negros, las casas de la iglesia donde duermen) y «sin_club» (temporada 5: las fiestas de alquiler y la búsqueda de otro club).

Cada pueblo lee su arco: el viaducto que se levanta sobre el valle (Albert), las esponjas que llenan el barco (Nikos, solo a bordo) y cuánta gente llena el club (Lou, que baja con el crac).

## Un cuento de muestra

**Albert, atascado en la nieve** (8 de diciembre de 2027, a las 10):

> Hoy cayó una nevada enorme sobre el páramo, y el viento la amontonó en la trinchera de la línea, donde la vía va hundida entre dos taludes. El tren de Albert entró en la trinchera, la máquina empujó, patinó, empujó, y se quedó clavada en la nieve hasta los topes.
>
> (…)
>
> Albert piensa, en la plataforma, de noche, con el fuego tapado para que dure el carbón, que hay sitios peores para quedarse atascado: por lo menos aquí hay fuego. Los pasajeros del tren se acercan a calentarse a la máquina. Una señora le da un trozo de pastel. Es el mejor pastel de su vida.
>
> Anochece en la trinchera, y no se ve más que la nieve que cae delante del farol de la máquina. El guardafrenos sale a pie por la vía, con una linterna, a avisar a la estación más cercana. Dentro de los coches, los pasajeros se tapan con lo que tienen. Nadie va a casa esta noche.

## Lo que cada cuento respeta de su calendario

**Albert**
- Ellen, desde que la conoce (mayo de la temporada 1); prometidos desde febrero de la 2; casados desde enero de la 3.
- Vive con su madre hasta la boda; la calle de los ferroviarios, desde la mudanza (abril de la 4); Annie, desde que nace (mayo de la 4).
- Tom va a la escuela hasta junio de la 2 y desde entonces baja a la mina.
- Hebburn conduce su máquina hasta que Albert aprueba (septiembre de la 5); desde entonces, cultiva puerros y saluda a los trenes.
- Las fiestas que vuelven cada año no nombran a Ellen, a Annie ni a Hebburn.

**Nikos**
- Stelios lleva su cuerda hasta marzo de la temporada 2; Yannis desde entonces.
- Eleni: su madre le escribe de ella desde septiembre de la 3; llega a Florida en noviembre de la 4; casados desde febrero de la 5; la casa de madera desde mayo; espera un hijo desde julio.
- Las fiestas que vuelven cada año no nombran a Eleni, a Yannis ni a Stelios.

**Lou**
- El club de Sully, de enero de la temporada 1 a febrero de la 5; el club nuevo, desde mayo de la 5.
- Rafe, desde que lo conoce (febrero de la 2); juntos desde agosto; en Chicago de agosto de la 4 a abril de la 5; casados desde septiembre de la 5.
- Las fiestas que vuelven cada año no nombran a Rafe ni a Sully.

## Cuánto texto tiene cada uno

| | Albert | Nikos | Lou |
|---|---|---|---|
| Variantes de los ocho párrafos | 95 (22 de sus estados) | 97 (33) | 102 (29) |
| …de ellas, con fecha o con meses | 14 | 10 | 10 |
| Oraciones de tiempo y de estación | 29 | 20 | 27 |
| Frases de entorno y de mundo | 11 | 9 | 11 |
| Objetos del día | 12 | 12 | 12 |
| Días especiales de la temporada 1 | 18, con 55 párrafos | 20, con 61 | 17, con 51 |
| **Textos en total** | **238** | **235** | **239** |
| Letras en total | 43,147 | 43,840 | 44,517 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,187** | **2,143** | **2,164** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **184 pruebas**: las 177 de antes y 7 nuevas.
- **Las pruebas generales me pararon dos veces:** a Albert le faltaba una quinta persona de todos los días (agregué a Sid, el muchacho que despierta a los del primer turno) y a Nikos un sexto texto del barco (el barco anclado sobre el banco, con los pelícanos).
- **Las 7 nuevas** (`CuentoG6Prueba`):
  - que estén los tres, sin problemas frente a su calendario y con su género;
  - Albert: Ellen (prometida y mujer), su madre, Tom, Annie, la calle de los ferroviarios y Hebburn a su tiempo;
  - Albert: la baja y el regulador, que se notan a todas las horas y que varían;
  - Nikos: Stelios, Yannis, Eleni (de carta, en Florida, mujer y embarazada) y la casa de madera a su tiempo;
  - Nikos: el puerto y el mes enfermo a todas las horas, y que no trabaja en sus seis fiestas de guardar de los cinco años;
  - Lou: Rafe (juntos, en Chicago, marido), Sully y el club nuevo a su tiempo;
  - Lou: la gira y los meses sin club a todas las horas, y que varían.
- **Las pruebas nuevas fallan cuando deben:** les hice siete sabotajes a la vez (Ellen sin fecha de llegada, la baja sin sus textos de trabajo, la Navidad vieja sin «no trabaja», Eleni en la última noche del año, «Rafe, su marido» desde que vuelve de Chicago, la gira sin sus textos de trabajo y Lou como hombre); fallaron las siete, cada una por lo suyo, y se restauró todo.
- **Leí dieciocho cuentos enteros**, seis de cada uno. Salieron de ahí:
  - el día de la nevada, Albert pasaba la noche atascado y el anochecer de siempre lo mandaba a casa: el especial tiene ahora su propio anochecer;
  - un pensamiento de Nikos decía «en cubierta» también en tierra;
  - «En lluvias el agua del golfo…», que con el nombre de su estación no suena;
  - un tiempo y una estación de Lou que decían «Harlem» también de gira.
- **Al repasar los textos antes de probarlos** corregí:
  - Albert: la calle de los ferroviarios en los textos de todos los días, antes de mudarse; comidas en la plataforma y con su madre en los textos de comer, que salen también en casa y después de la boda; un patio en la baja, cuando todavía no tiene casa con patio; Annie, recién nacida, tendiéndole los brazos;
  - Nikos: la isla «de los turcos» en dos fiestas de cada año, que en 1913 y 1914 ya era italiana; una radio en 1913; la lonja de mitad de año, que en la temporada 4 cae mientras está enfermo y lo ponía a ensartar esponjas (ahora cuenta el reparto de la paga);
  - Lou: dos especiales que usaban párrafos que no existen (no habría compilado); el 4 de julio de la temporada 3, que cae de gira y la ponía en el club de Harlem; que el sindicato de músicos de Nueva York no admitía a los negros, que no puedo afirmar; Valentino, que murió el 23 de agosto y no el 24 (ahora «los periódicos dicen»); y los meses sin club, que no tenían qué hacer por la tarde y la mandaban a ensayar en un club que ya no existe.
- **Compilación de Android:** pendiente; se agrega aquí al terminar la corrida de `apk.yml`.
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

Nada de esto se abrió en la sesión: viene de lo que se sabe de esos años, de memoria. **[Aproximación]**
- **Albert:** el llamador que despierta a los del turno, el primer visitante del año con carbón, pan y sal, la campana de las tortitas, las excursiones de las escuelas dominicales, las bandas y los estandartes de la gala, el penique para el Guy, el partido de fútbol del día de las cajas, el examen de maquinista, la máquina de seis ruedas acopladas, la lata del té, el sindicato de los ferroviarios y sus reuniones; del puente del Tay, que no quedó nadie y que los buzos buscaban.
- **Nikos:** las islas de los buzos (Kálymnos, Symi, Halki), la cruz en el agua, la torta de San Basilio con la moneda, la lonja con un cuarto por capitán, la iglesia de madera, el ojo pintado en la proa, el cubo de cristal para mirar el fondo, la señal de los tirones, el pan cocido dos veces, el agua que brilla de noche, las píldoras contra el cometa, la frase de Mark Twain sobre el cometa.
- **Lou:** la calle 133 y su apodo, el metro de la avenida Lenox, la casa de huéspedes, las tarjetas de las fiestas de alquiler, los duelos de pianistas, el desfile de Pascua, el vagón de los negros y las casas donde dormían las compañías de gira, el Savoy (abierto a negros y blancos, sus dos escenarios), Gertrude Ederle (hija de un carnicero alemán, catorce horas), Valentino (treinta y un años) y Marcus Garvey preso en Atlanta en 1926.
- **Las personas son inventadas**, y también el nombre del barco de Nikos; los hechos y los nombres de las noticias siguen las fuentes de A6.
