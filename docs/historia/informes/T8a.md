# Tanda T8a: los cuentos de la escriba y la doctora

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A8. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de dos personajes de ficción del grupo 8. En **Otras vidas**, con su tema, empiezan con «Hoy, en su vida»:
- **la escriba** de la Orden del Candil, en Peñaseca, una ciudadela sobre una meseta del páramo, en 2287 (tema Orden);
- **la doctora**, botánica del Claustro, un laboratorio sellado bajo una ciudad en ruinas, en 2287 (Laboratorio).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasEscriba.kt` | Los textos de la escriba y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasCientifica.kt` | Los de la doctora |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los dos |
| `app/src/test/…/vidas/CuentoG8Prueba.kt` | 5 pruebas nuevas, propias de estos dos |
| `docs/historia/informes/A8.md` | El resultado de su compilación |

- **No toca el motor ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Cómo se hizo

Por primera vez, el primer borrador de cada cuento lo escribió un **subagente**, uno por personaje y en paralelo, con un encargo largo: las reglas que comprueban las pruebas, lo aprendido en los grupos 6 y 7, los cuentos del grupo 7 como modelo, y el calendario y la ficha de su personaje. Los subagentes no tocaron el repositorio ni compilaron.

Lo demás lo hice yo, igual que en las tandas anteriores:
- leí entero cada borrador y lo comparé con su calendario;
- lo integré y corrí todas las pruebas;
- leí quince cuentos de muestra;
- escribí las pruebas propias y las saboteé.

## Lo que decidí

1. **Sin el hueco del nombre:** «la escriba», «la doctora». Como la ficha se llama «La doctora», la prueba de que el nombre de la ficha no aparezca solo vale para la escriba («Una escriba»).
2. **La escriba come tres veces y trabaja dos** (patrulla por la mañana; taller y archivo por la tarde). Esos textos no dicen la hora. Cada noche recita el Código del Candil, que es su tramo de rezo.
3. **Desde que espera un hijo, nada de patrullas largas.** Los dos textos de patrulla por el páramo llevan fecha y dejan de salir el día que se lo dice a Damián. Desde entonces su trabajo es la ronda del borde de la meseta, el taller y el archivo.
4. **La doctora vive sin cielo.** Su tiempo cuenta el aire de las rejillas, las lámparas, la humedad y el cielo de las pantallas de la Vigía.
   - **Arriba (temporada 5) el tiempo es el del estado.** El calendario sigue diciendo «sin clima» los días normales de arriba, así que el hueco del cielo daría una frase de lámparas. Por eso el párrafo del tiempo de arriba tiene tres textos propios, sin el hueco del cielo.
   - Los cielos de sol y de lluvia ya existen, pero hoy no los usa ningún texto. Los traerán los especiales de la temporada 5 (`sol` y `lluvia`).
5. **Arriba nada se adelanta:** antes del día del sol no se nombra el sol; Neva sale solo desde que la encuentra, y Lauro, solo desde que lo ve de lejos.
6. **Sus días de fiesta tienen su «no trabaja»:**
   - la escriba: el año nuevo, la Fundación, la Vigilia de los Candiles y la Noche Larga;
   - la doctora: el año nuevo, la Fundación, la luz larga y la noche larga. En la temporada 2 su calendario no tiene Fundación, por la fiebre.
7. **El pueblo:** el de la escriba lee primero la Garza cuando está rota en el patio y, si no, la cisterna. El de la doctora lee los jardines, también cuando están vacíos tras el vaciado.

## Los estados

- **La escriba:**
  - «expedicion» (temporada 2): siete semanas en la góndola de la Garza, el sur visto desde arriba, los sacos de la Bóveda desde que entran en ella. Damián sale solo como «el piloto», porque el día en que empiezan cae en medio del viaje.
  - «encierro» (3): un mes en el archivo con el núcleo, la llave por fuera, las notas a la maestre por debajo de la puerta.
  - «herida» (4): cinco semanas en la enfermería, con Casilda; Nieves le cuenta la patrulla que ahora manda. El cierre de los postigos de ese año cae dentro de la herida: su texto vale visto desde la cama.
- **La doctora:**
  - «cuarentena» (2): encerrada en el ala de Botánica con un catre, la mascarilla, el parte del altavoz y su hijo por la pantalla.
  - «experimento» (3): tres semanas en la campana de vidrio con tierra y aire de arriba. Lauro está fuera, apuntando.
  - «cierre» (4): la luz roja, el destello apagado, los vigías que preguntan, la tarjeta gris de Lauro en el bolsillo.
  - «arriba» (5): la tienda en las ruinas, la siembra, Tomé y dos vigías, el aire que nadie ajusta.

Cada estado tiene tres mañanas, un texto para cada cosa que hace a lo largo del día, dos de pueblo y dos de anochecer.

## Un cuento de muestra

**La escriba, el día de la caza grande** (27 de agosto de 2027, a las 19):

> Hoy, otro día de caza, la escriba se despierta con las manos tiesas de sal y tiene que abrirlas despacio, dedo a dedo, como quien abre una caja vieja. Abajo, en el llano, los caballeros ya han salido. Se oyen sus motores entre las rocas, lejos.
>
> (…)
>
> A esta hora pesa la carne colgada con una balanza de antes del Fuego que solo funciona si se le da un golpe. Le da el golpe. La aguja salta. Apunta. Le da otro golpe, por si acaso. La aguja salta a otro número. Apunta la media.

## Lo que cada cuento respeta de su calendario

**La escriba**
- **Jimena:** la encuentran en febrero de la 1, aprende las letras desde octubre, lee sola desde septiembre de la 2, es candilera desde abril de la 4 y patrulla desde febrero de la 5.
- **Fermín:** ve hasta julio de la 1; le da las llaves en enero de la 3; muere en noviembre de la 4.
- **Ulises:** patrulla hasta junio de la 3; desde entonces trabaja en la fragua.
- **Damián:**
  - solo el piloto hasta junio de la 2;
  - la pierna rota de julio a noviembre de la 3;
  - el cuarto juntos desde noviembre de la 3;
  - ella espera un hijo desde octubre de la 4, y Ciro nace en mayo de la 5.
- **Nieves** manda la patrulla desde la emboscada (abril de la 4).
- **El núcleo:** desde la Bóveda (mayo de la 2) hasta la última página (agosto de la 5).
- **Las fiestas que vuelven cada año** no nombran a ninguno de ellos: solo a la maestre Amparo y a Rosendo, el cocinero, que están los cinco años.

**La doctora**
- **Lauro:** desde febrero de la 1, pregunta desde agosto, se va en septiembre de la 4 y ella lo ve de lejos en mayo de la 5. Antes de él, un asistente viejo sin nombre.
- **Hilda:** en el laboratorio de al lado hasta diciembre de la 1; retirada hasta que muere, en enero de la 3.
- **Tomé:** quiere entrar en Mentes desde octubre de la 1, entra en julio de la 2 y quiere vivir arriba desde octubre de la 5.
- **Coral:** el permiso en julio de la 2; Alba nace en agosto de la 3 y anda desde marzo de la 4.
- **Neva:** encerrada de febrero a mayo de la 2, arriba otra vez desde mayo de la 5.
- **El Consejo:** el rector Mora hasta marzo de la 3; la rectora Ibarra desde entonces; la decisión de guardar la grama y el voto que la comparte.
- **Las fiestas que vuelven cada año** no nombran a ninguno de ellos: dicen «su hijo», «la jefa de los jardines», «el Consejo». Solo nombran a Bruno, el del destello.

## Cuánto texto tiene cada uno

| | La escriba | La doctora |
|---|---|---|
| Variantes de los ocho párrafos | 130 (39 de sus estados) | 156 (63) |
| …de ellas, con fecha o con meses | 33 | 36 |
| Oraciones de tiempo y de estación | 33 | 15 |
| Frases de entorno y de mundo | 13 | 11 |
| Objetos del día | 12 | 12 |
| Días especiales de la temporada 1 | 20, con 76 párrafos | 19, con 68 |
| **Textos en total** | **300** | **298** |
| Letras en total | 57,673 | 64,390 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,113** | **2,188** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **217 pruebas**: las 212 de antes y 5 nuevas.
- **Las pruebas generales pasaron a la primera** con los dos borradores. La doctora queda cerca del tope de largo (2,188 de 2,200).
- **Las 5 nuevas** (`CuentoG8Prueba`):
  - que estén los dos, sin problemas frente a su calendario, con su género y sin el hueco del nombre;
  - la escriba: Jimena en sus cinco etapas, Fermín, Ulises, Damián (el cuarto, la pierna, el hijo), Ciro, Nieves y el núcleo a su tiempo; nada de patrullas largas desde que espera;
  - la escriba: la expedición, el encierro y la herida a todas las horas, que varían, y sus cuatro fiestas sin trabajo;
  - la doctora: Lauro, Hilda, Tomé, Coral y Alba, Neva, el rector y la rectora, la mancha verde y el sol a su tiempo;
  - la doctora: la cuarentena, la campana, el cierre y la subida a todas las horas, que varían; que arriba el tiempo sea el del estado, y sus cuatro fiestas sin trabajo.
- **Las pruebas nuevas fallan cuando deben.** Hice cinco sabotajes:
  - Fermín muerto desde que pierde la vista;
  - la comida del viaje convertida en sueño;
  - la noche larga de la doctora sin «no trabaja»;
  - Coral nombrada en el censo, que vuelve cada año;
  - uno de los tiempos de arriba sin su estado.

  Las cuatro primeras fallaron, cada una por lo suyo. La quinta no falló, y con razón: los otros dos tiempos de arriba la cubrían. La repetí quitándoles el estado a los tres, y entonces sí falló. Después se restauró todo.
- **Leí quince cuentos enteros**, siete de la escriba y ocho de la doctora: días normales, los tres estados de la escriba, los cuatro de la doctora y varios especiales. De ahí salió un pensamiento de la doctora: decía que a la gente de arriba solo la ha visto «en las pantallas», y eso deja de ser verdad cuando sube. Ahora tiene fecha y sale hasta la subida.
- **Al leer los borradores contra sus calendarios** comprobé:
  - quién va en la Garza;
  - el paso del Cuervo;
  - que la Fundación de la temporada 2 de la doctora no existe, por la fiebre, así que su texto de fiesta no choca con la cuarentena;
  - que ninguna fiesta que vuelve cae dentro de un estado. La excepción son los postigos de la escriba en la temporada 4, que ya están escritos para eso.
- **Compilación de Android:** la corrida 234 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `098f1d1`).
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Todo es inventado**, como sus fichas. **[Diseño propio]**
  - La orden, la ciudadela, la Garza, el Código del Candil y sus fiestas.
  - El Claustro, los Semejantes, la grama y las fiestas del Claustro.
  - Ningún nombre ni término viene de una obra existente.
- **Desde 2032**, cuando la temporada 5 se repite y sus sucesos se mueven, alguna variante con fecha puede salir unos días antes o después, como en el grupo 7.

## Para la tanda que sigue

- **T8b (Ruth y Brenna):** sus borradores ya están escritos con el mismo encargo. Los dos tienen nombre propio y usan el hueco del nombre. Brenna no tiene «trabajo»: su trabajo es el camino, y pasa cada invierno con el paso cerrado.
