# Tanda T6b: los cuentos de Liudmila y Giulia

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T6a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros dos personajes del grupo 6. Con esta tanda, **los cinco del grupo 6 tienen cuento**: en **Otras vidas**, con su tema, empiezan con «Hoy, en su vida».
- **Liudmila**, obrera textil en Moscú, de 1975 a 1979 (tema Soviético);
- **Giulia**, restauradora de frescos en Roma, de 2025 a 2029 (Roma).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasLiudmila.kt` | Los textos de Liudmila y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasGiulia.kt` | Los de Giulia |
| `vidas/piezas/TodasLasPiezas.kt` | Registra las dos |
| `app/src/test/…/vidas/CuentoG6Prueba.kt` | 5 pruebas más, propias de estas dos |
| `docs/historia/informes/T6a.md` | El resultado de su compilación |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios de A6.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Los textos de todos los días no dicen dónde viven.** Liudmila se muda en la temporada 3 de un piso de dos cuartos a uno de tres en un barrio nuevo; Giulia vive en Testaccio hasta la 2 y luego con Matteo en el Pigneto. Lo de cada casa va en textos con fecha; los demás dicen «su calle», «la cocina», «el balcón».
2. **La suegra de Liudmila muere en la temporada 4.** Sus textos tienen tres tramos (en casa, enferma, y después «desde que murió Galina Petrovna»), y ningún texto de todos los años la nombra: ni el tiempo, ni la tele, ni el día de los cosmonautas. La Pascua que pinta huevos es suya y solo está en las temporadas en que vive.
3. **Liudmila no trabaja en las fiestas soviéticas de guardar:** el año nuevo, el 8 de marzo, los dos días de mayo, la Victoria, la Constitución y los dos días de noviembre. Los de mayo y noviembre tienen un texto para cada día. El día del ejército y el de los cosmonautas eran laborables, y en ellos trabaja.
4. **El andamio de Giulia tiene fechas.** Sus textos de andamio valen desde que sube por primera vez (enero de 2025) hasta que se quita el último (octubre de 2029); antes y después hace otras cosas (muestras, fotos, la relación final, otra iglesia). La capilla, sin andamio, tiene su propio texto.
5. **La temporada 1 de Giulia es 2025, que ya pasó:** la muerte de Francisco el lunes de Pascua, su funeral, el cónclave, León XIV y la canonización de Carlo Acutis salen como fueron (ver «Lo que no se pudo comprobar»). Los dos primeros días no trabaja.
6. **Giulia, embarazada en la temporada 5, deja los disolventes** y dirige desde abajo: lo dice su texto de gente.

## Los estados

- **Liudmila:** «sanatorio» (temporadas 2 y 4, tres semanas en el mar Negro): la gimnasia en la terraza, los baños de lodo, la playa de piedras, el comedor con mantel, el baile entre mujeres, el cine de verano. No nombran a Lena, que solo va en la 4.
- **Giulia:** «agosto» (cada año, la obra cerrada y Roma vacía): la relación técnica en casa con el ventilador, el tren de Ostia, la sandía en un quiosco del Tíber, la siesta, el cine de la isla del Tíber. No nombran a Matteo, que en el agosto de 2025 todavía no vive con ella.

Cada pueblo lee su arco: el plan del año en la nave de telares (Liudmila) y cuánto de la capilla está limpio (Giulia).

## Un cuento de muestra

**Giulia, el día del papa nuevo** (8 de mayo de 2027, que es su 8 de mayo de 2025, a las 19):

> Hoy, a última hora de la tarde, ha salido humo blanco de la chimenea de la Sixtina, y las campanas de San Pedro han empezado a tocar, y luego todas las de Roma. Hay papa. Es americano, de Chicago, ha vivido muchos años en Perú, y se llama León, León XIV.
>
> (…)
>
> Giulia piensa que ha visto la cosa que verán en los libros de historia, desde la plaza, con el polvo de la capilla todavía en el pelo. Piensa que un papa nuevo, para su oficio, es un cliente nuevo: el dueño de la mitad de las paredes de Roma. Se ríe sola de pensarlo.

## Lo que cada cuento respeta de su calendario

**Liudmila**
- El piso viejo hasta la mudanza (junio de la temporada 3); el nuevo desde entonces.
- Galina Petrovna en casa hasta septiembre de la 4, enferma hasta octubre, y desde entonces recordada.
- Tamara Ivánovna es la jefa de turno hasta mayo de la 5; desde entonces, Liudmila.
- **El sanatorio**, las dos veces.
- Las fiestas que vuelven cada año no nombran a Galina Petrovna ni a Tamara Ivánovna.

**Giulia**
- Teresa dirige la obra hasta febrero de la temporada 4; desde entonces, Giulia.
- Samir, de junio de la 1 a julio de la 5; después, desde Nápoles.
- Matteo: novio hasta marzo de la 2; juntos en el Pigneto hasta la boda (mayo de la 4); marido; y desde febrero de la 5, espera un hijo.
- El andamio, de enero de la 1 a octubre de la 5.
- **Agosto**, cada año.
- Las fiestas que vuelven cada año no nombran a Teresa, a Samir ni a Matteo.

## Cuánto texto tiene cada una

| | Liudmila | Giulia |
|---|---|---|
| Variantes de los ocho párrafos | 87 (15 de su estado) | 95 (17) |
| …de ellas, con fecha o con meses | 11 | 19 |
| Oraciones de tiempo y de estación | 28 | 25 |
| Frases de entorno y de mundo | 11 | 13 |
| Objetos del día | 12 | 12 |
| Días especiales de la temporada 1 | 18, con 60 párrafos | 22, con 67 |
| **Textos en total** | **234** | **248** |
| Letras en total | 42,786 | 45,973 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,182** | **2,185** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **189 pruebas**: las 184 de antes y 5 nuevas.
- **Las pruebas generales me pararon tres veces:** los dos cuentos se pasaban del largo medio permitido (2,239 y 2,214 letras, con un tope de 2,200), y recorté una frase a los textos más largos de cada una; y un especial de Liudmila terminaba en signo de interrogación.
- **Las 5 nuevas** (`CuentoG6Prueba`):
  - que estén las dos, sin problemas frente a su calendario y con su género;
  - Liudmila: los dos pisos, la suegra (en casa, enferma, recordada) y la jefa de turno a su tiempo;
  - Liudmila: el sanatorio a todas las horas, las dos veces, y que no trabaja en sus fiestas de guardar de los cinco años, con los segundos días de mayo y de noviembre;
  - Giulia: Samir, Teresa, Matteo (novio, juntos, marido), el embarazo y el andamio a su tiempo;
  - Giulia: agosto a todas las horas, y que no trabaja en sus fiestas de guardar de los cinco años, ni el día que muere el papa ni el de su funeral.
- **Las pruebas nuevas fallan cuando deben:** les hice seis sabotajes a la vez (la suegra recordada desde el primer día, el sanatorio sin sus textos de trabajo, el 8 de marzo sin «no trabaja», Samir en Nápoles desde siempre, agosto sin sus textos de trabajo, Giulia como hombre); fallaron las cinco pruebas, cada una por lo suyo. El del 8 de marzo quedó tapado por el del sanatorio, que cae antes en la misma prueba, así que lo repetí aparte junto con Ferragosto sin «no trabaja»: fallaron los dos. Se restauró todo.
- **Leí doce cuentos enteros**, seis de cada una. Salieron de ahí:
  - el día de León XIV, a las 19, el párrafo de la hora la ponía paseando por el Tíber mientras otro la mandaba corriendo a San Pedro: ahora el especial cuenta también ese párrafo;
  - «la lona» y «los focos de la obra» seguían saliendo después de quitado el último andamio.
- **Al repasar los textos antes de probarlos** corregí:
  - Liudmila: la suegra en textos de todos los años (el tiempo, la tele, el día de los cosmonautas), el sofá cama y «el piso pequeño» después de la mudanza, «la jefa de turno» en un texto de todos los días cuando desde la temporada 5 lo es ella, las estrellas del Kremlin vistas desde la cocina (en el barrio nuevo no se ven), y dos especiales con un párrafo que no existe (no habría compilado);
  - Giulia: un especial con un párrafo que no existe; Samir el día de León XIV, un mes antes de llegar a la obra; el evangelio que el viento pasa de páginas sobre el ataúd, que recuerdo del funeral de Juan Pablo II y no pude dar por bueno para el de Francisco; y el andamio en textos que valían también con la capilla terminada.
- **Un límite que queda:** los textos de camino de Liudmila mezclan el metro de ida con las colas de la vuelta, porque el motor no elige texto por hora sino por actividad; a las siete de la mañana puede salir una cola.
- **Compilación de Android:** la corrida 226 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `ef49539`).
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

Nada de esto se abrió en la sesión: viene de lo que se sabe, de memoria. **[Aproximación]**
- **Liudmila:** los ocho telares por tejedora, el idioma de señas de la nave, el tablero de honor, las tres colas de las tiendas, la bolsa «por si acaso», la moneda de cinco kopeks del metro y el vaso común de las máquinas de gaseosa, el libro a cambio de papel viejo, la radio de cable de las cocinas, el termo chino, los chales de Pávlovski Posad, la colonia Triple del día del ejército, las mimosas del Cáucaso, el sábado de trabajo voluntario y el tronco de Lenin, los huevos teñidos con piel de cebolla, los gladiolos del 1 de septiembre, la nevera ZIL; los baños de lodo y la gimnasia de los sanatorios.
- **Giulia:** el método italiano de reintegrar con rayitas, las compresas de papel japonés, las jornadas del pintor que se ven con luz rasante, la Superintendencia, las lentejas de año nuevo, el mercado de la Befana en la plaza Navona, la paloma de Pascua, la marcha del 25 de abril desde la Porta San Paolo, la patrulla acrobática del 2 de junio, la estatua de San Pedro vestida y los fuegos sobre el castillo el 29 de junio, los bomberos y la corona de la Inmaculada, el pescado de Nochebuena, el tren de Ostia y los quioscos de sandía, el cine de la isla del Tíber, los estorninos.
- **2025:** que Francisco murió temprano el lunes de Pascua en su casa del Vaticano, un día después de saludar a la gente desde el coche; el ataúd sencillo, el funeral con unas doscientas mil personas y el entierro en Santa María la Mayor junto al icono de la Virgen; el humo negro de la primera noche del cónclave y el blanco al día siguiente por la tarde; que León XIV es de Chicago, vivió en Perú y habló de paz en el balcón; que Carlo Acutis murió de leucemia a los quince años y se canonizó junto a un joven de Turín. Las fechas son las del calendario, que tienen fuente en `fuentes.md` desde A6.
- **Las personas son inventadas**; los hechos y los nombres de las noticias siguen las fuentes de A6.
