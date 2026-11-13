# Tanda T7b: los cuentos de la residente y el archivista

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T7a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros dos personajes de ficción del grupo 7. Con esta tanda, **los cinco del grupo 7 tienen cuento**: en **Otras vidas**, con su tema, empiezan con «Hoy, en su vida».
- **la residente**, técnica de purificación de agua en un refugio bajo tierra, en 2105 (tema Refugio);
- **el archivista** de un pueblo de pescadores en la niebla, en 1926 (Horror cósmico).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasResidente.kt` | Los textos de la residente y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasArchivista.kt` | Los del archivista |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los dos |
| `app/src/test/…/vidas/CuentoG7Prueba.kt` | 5 pruebas más, propias de estos dos |
| `docs/historia/informes/T7a.md` | El resultado de su compilación |

- **No toca el motor ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Sin el hueco del nombre**, como en T7a: «la residente», «el archivista». La ficha del archivista se llama precisamente «El archivista», así que la prueba de que el nombre de la ficha no aparezca solo vale para la residente.
2. **Bajo tierra no hay cielo.** El tiempo de la residente cuenta las luces, el aire y el cielo pintado del comedor. Su único clima de verdad es la lluvia del día en que sale a mojarse (temporada 5), que tiene su cielo propio.
3. **Lo raro del archivista se insinúa y nunca se explica:** un signo que aparece en sus cuadernos, un nombre que se repite cada cien años, una isla en un mapa, escaleras que solo se ven en la marea más baja, luces en el agua. No hay criaturas ni nombres de ninguna obra.
4. **Sus días de fiesta tienen su «no trabaja»:** el año nuevo, los fundadores, el sol falso y la Nochebuena de la residente; el año nuevo, San Pedro, el Carmen y la Navidad del archivista.

## Los estados

- **La residente:** «racionamiento» (temporada 2: dos litros por persona, la cisterna rota), «cuarentena» (3: el sector cerrado, su madre al otro lado de la compuerta) y «abierto» (5: la Puerta abierta, el viento, el arroyo de fuera, el primer fuego, las estrellas).
- **El archivista:** «fiebre» (temporada 3: tres semanas en la posada, sueños de escaleras de agua) y «faro» (4: la lámpara, los ciento veinte escalones, lo que se ve en el agua desde arriba).

El pueblo de la residente lee la cisterna cuando está baja y, si no, los tanques de cultivo; el del archivista, lo catalogado del archivo.

## Un cuento de muestra

**El archivista, el día que ve el signo** (14 de septiembre de 2027, a las 22):

> Hoy, al abrir el cuaderno donde apunta lo de cada día, el archivista ha visto en el margen de una página un dibujo a lápiz: un signo, como una letra de un alfabeto que no conoce, con tres trazos y un círculo. Está hecho con su lápiz. Con su mano. No lo recuerda.
>
> (…)
>
> El archivista piensa que ha visto el signo tantas veces en el archivo que su mano lo ha aprendido sola, sin que él se diera cuenta, como se aprende una letra de niño. Piensa que es eso. Que tiene que ser eso. Lo piensa hasta que se lo cree. Casi.

## Lo que cada cuento respeta de su calendario

**La residente**
- Lina, con permiso para un hijo desde octubre de la 1 y madre desde julio de la 2.
- Noa: le regala un disco; piden cuarto en noviembre de la 2; viven juntas desde mayo de la 3; fuera con la expedición de mayo a julio de la 4; la residente espera desde marzo de la 5.
- La vieja Dorotea, hasta diciembre de la 3; el supervisor nuevo, desde noviembre de la 3; la Puerta, abierta desde septiembre de la 5.
- Las fiestas que vuelven cada año no nombran a Lina, a Noa ni a Dorotea.

**El archivista**
- El signo, desde septiembre de la 1; el mapa de la isla, desde octubre de la 2.
- El profesor: una postal al principio; no contesta desde febrero de la 2; una línea en abril de la 5.
- Baltasar, el torrero, hasta octubre de la 4; el faro a turnos; un torrero nuevo desde marzo de la 5.
- Las fiestas que vuelven cada año no nombran a Baltasar.

## Cuánto texto tiene cada uno

| | La residente | El archivista |
|---|---|---|
| Variantes de los ocho párrafos | 113 (40 de sus estados) | 93 (27) |
| …de ellas, con fecha o con meses | 15 | 9 |
| Oraciones de tiempo y de estación | 12 | 28 |
| Frases de entorno y de mundo | 11 | 11 |
| Objetos del día | 12 | 12 |
| Días especiales de la temporada 1 | 16, con 52 párrafos | 16, con 51 |
| **Textos en total** | **236** | **231** |
| Letras en total | 45,110 | 40,640 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,198** | **2,186** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **207 pruebas**: las 202 de antes y 5 nuevas.
- **Las pruebas generales me pararon tres veces:** los dos cuentos pasaban del largo medio (2,259 y 2,238 letras, con un tope de 2,200), y les quité una frase a los textos más largos; y una variante del faro usaba un hueco de fecha que su función corta no aceptaba (no compilaba).
- **Las 5 nuevas** (`CuentoG7Prueba`):
  - que estén los dos, sin problemas frente a su calendario, con su género y sin el hueco del nombre;
  - la residente: Lina, Noa (el cuarto, la vida juntas, la expedición, el hijo), Dorotea, el supervisor y la Puerta a su tiempo;
  - la residente: el racionamiento, la cuarentena y la Puerta abierta a todas las horas, que varían, y sus cuatro fiestas sin trabajo;
  - el archivista: el signo, el profesor y los dos torreros a su tiempo;
  - el archivista: la fiebre y el faro a todas las horas, que varían, y sus cuatro fiestas sin trabajo.
- **Las pruebas nuevas fallan cuando deben:** les hice cinco sabotajes a la vez (Noa fuera desde siempre, la Puerta abierta sin sus textos de trabajo, la residente como hombre, Baltasar vivo para siempre, el faro sin sus textos de trabajo); fallaron las cinco pruebas, cada una por lo suyo, y se restauró todo.
- **Leí doce cuentos enteros**, seis de cada uno. De ahí salió un pensamiento de la residente («qué habrá detrás de la Puerta… lo que le pesa es no saberlo») que seguía con la Puerta ya abierta: ahora tiene fecha y uno para después.
- **Al repasar los textos antes de probarlos** corregí: una variante de gente repetida, «hace seis años» y «veintidós años» en textos de todos los años, Lina en la fiesta del sol falso (que vuelve cada año), una «cena» en un especial de trabajo; en el archivista, el especial del aceite (cada año) ponía al torrero bebiendo en la taberna, cuando el de la temporada 5 «no baja nunca al pueblo»; unas barcas de 1926 «con los motores parados»; y una errata.
- **Compilación de Android:** la corrida 229 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `a6b26c8`).
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Todo es inventado**, como sus fichas. Las fiestas del pueblo del archivista (la Candelaria, San Pedro, el Carmen, los difuntos) son las de muchos pueblos de pescadores, contadas de memoria y como ficción. **[Aproximación]**
