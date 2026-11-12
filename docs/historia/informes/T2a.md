# Tanda T2a: los cuentos de Kichizō, Mary y José

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A2. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los tres primeros personajes del grupo 2, con la gramática y el tono que aprobaste en P3a. Desde esta tanda, en **Otras vidas** y con **cualquier tema**, sus detalles empiezan con «Hoy, en su vida»:
- **Kichizō**, carpintero de Edo;
- **Mary**, hiladora de Manchester;
- **José**, peón de una hacienda pulquera.

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasKichizo.kt` | Los textos de Kichizō y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasMary.kt` | Los de Mary |
| `vidas/piezas/PiezasJose.kt` | Los de José |
| `vidas/piezas/TodasLasPiezas.kt` | Registra las tres |
| `app/src/test/…/vidas/CuentoG2Prueba.kt` | 7 pruebas propias de estos tres |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Dos cuentos de muestra

**José, la fiesta del santo de la hacienda** (13 de junio de 2027, a las 9:30). Título: «La fiesta del santo de la hacienda: misa, cohetes y jaripeo». Los párrafos 1, 4 y 5 son de ese día.

> Hoy es día de San Antonio, el santo de la hacienda, y es la fiesta grande del año. Desde la madrugada truenan los cohetes en el patio, y en la capilla, adornada con flores y papel picado, el cura dice misa cantada. El patrón, cuando viene, se sienta adelante.
>
> (…)
>
> Las muchachas solteras le piden novio a San Antonio, y algunas, para obligarlo, lo ponen de cabeza en un rincón hasta que se los da. Lupe le pregunta a su madre si ella también le pidió novio al santo. Petra dice que no, que le pidió otra cosa, y mira a José.

**Mary en la huelga** (30 de agosto de 2029, a las 9:30). Está en el estado «paro»: la mañana, el trabajo, el pueblo, la gente y el anochecer son de la fábrica parada.

> Por la mañana Mary sale a la calle con las demás a ver si hay noticias. Hay grupos en todas las esquinas, y hombres que leen en voz alta lo que dicen los periódicos de los huelguistas. Nadie sabe cuánto va a durar. Todos saben que el pan no espera.
>
> (…)
>
> A esta hora, en vez de estar en la fábrica, busca algo que hacer por unos peniques: lava ropa con su madre, cuida niños de una vecina, recoge carbón caído junto al canal. No hay trabajo, porque nadie trabaja, y los que podrían pagar tampoco pagan.
>
> (…)
>
> Esta noche no hay que levantarse temprano, y sin embargo nadie se acuesta tarde: no hay vela que gastar. Mary se sienta en la puerta con las vecinas, a oscuras, y se cuentan lo que se dice: que van a ganar, que van a perder, que llegan más soldados.

## Cómo se adaptó la gramática

- **El «dato de su época»** de cada párrafo sale de lo que se sabe de cada lugar:
  - **Kichizō:** la casa de vecindad con un pozo y una letrina para todo el callejón, los vendedores que pregonan por la mañana, el cepillo y la sierra que se tiran hacia uno, los ensambles sin clavos, la pausa del té en la obra, el baño con su sala de arriba, las brigadas de bomberos que tiran casas, los vigilantes de la noche con sus tablillas, el libro de alquiler.
  - **Mary:** el despertador que golpea los vidrios con una vara, los zuecos con herraduras, el calor húmedo de la sala de hilar, las multas del capataz, las que leen los labios por el ruido, la casa adosada por detrás, el mercado de la noche, el periódico de los cartistas leído en voz alta.
  - **José:** el alabado del amanecer, el raspador y el acocote, el maguey que se capa, el tinacal, el tren del pulque, la tienda de raya y sus vales, el metate, los rurales.
- **Los estados de A2 cambian el cuento:**
  - **Kichizō** tiene tres: en la **aldea** de su hermano (temporada 3) amanece, trabaja y duerme allá; **sin casa** (temporada 4) amanece, come y duerme en el templo; en **Ōyama** (temporada 5) todo el día es la peregrinación.
  - **Mary:** en el **paro** (temporada 3) no hay fábrica; en **casa** con Tom (temporada 4) tampoco.
  - **José**, **herido** (temporada 3), no raspa: se queda en el cuarto y lo cura la curandera.
- **El arco «milpa» de José** entra en el párrafo del pueblo: el pegujal pelón en secas, el maíz que asoma, la milpa alta y las mazorcas.
- **Mary no tiene arcos:** su párrafo del pueblo no usa `{entorno}`.

## Lo que cada cuento respeta de su calendario

**Kichizō**
- Okiku, desde que nace (temporada 2); Ume la espera desde el 8 de octubre de la 1. Shinsuke, desde que entra de aprendiz (4). Kinta va a la escuela del templo y desde la temporada 5 barre el taller de Genbei.
- Las fiestas que vuelven cada año no nombran a Okiku ni a Shinsuke. La fiesta de las muñecas dice «en el barrio» y no «en el callejón», porque en la temporada 4 cae con el callejón quemado.

**Mary**
- Jem, desde el día en que lo conoce (25 de abril de la temporada 1); novio hasta la boda y marido desde entonces. Vive con sus padres hasta que se mudan al cuarto (temporada 3).
- Su padre teje en casa hasta que vende el telar, hace lo que sale después, y carga bultos en la estación desde la temporada 4.
- Billy, en la escuela dominical, y desde la temporada 2 también en la fábrica. Tom, desde que nace (4). Bridget, hasta que se va (5).
- Las fiestas que vuelven cada año no nombran a Jem, a Tom ni a Bridget, ni dicen con quién vive.

**José**
- Rafael, desde que nace (temporada 2); Cuca, desde que nace (4); los embarazos de Petra entre medias. Doña Refugio, hasta que muere (4). Nicolás, hasta que huye (5).
- Las fiestas que vuelven cada año no nombran a ninguno de los cuatro. Los muertos de Todos Santos son «los suyos que se fueron chiquitos», que vale todos los años.

## Cuánto texto tiene cada uno

| | Lucio (T1b) | Kichizō | Mary | José |
|---|---|---|---|---|
| Variantes de los ocho párrafos | 79 | 98 (29 de sus estados) | 85 (13) | 71 (5) |
| …de ellas, con fecha | 10 | 5 | 11 | 6 |
| Oraciones de tiempo y de estación | 27 | 29 | 29 | 23 |
| Objetos del día | 12 | 12 | 12 | 12 |
| Oraciones de entorno y de mundo | 17 | 18 | 9 | 19 |
| Días especiales de la temporada 1 | 19 | 19, con 70 párrafos | 16, con 51 | 19, con 76 |
| **Textos en total** | | **263** | **222** | **237** |
| Letras en total | | 45,789 | 37,369 | 39,908 |
| **Cuento medio** (2027, a las 9 y a las 19) | 2,070 | **2,056** | **2,024** | **2,132** |

- **El largo** queda dentro de lo aprobado (de 1,800 a 2,150 de media). Kichizō salió primero con 2,192: recorté los objetos, la gente y el pueblo hasta 2,056.
- **Esta tanda llevó unas 123,000 letras.**

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **112 pruebas**: las 105 de antes y 7 nuevas.
- **Las pruebas generales** de `CuentoPrueba` revisan solas a los tres nuevos: piezas bien escritas frente a su calendario, ningún año, ningún cuento repetido de 2026 a 2031, el largo, el mismo cuento todo el día salvo el trabajo, la certeza de cada día especial y cuántos párrafos cambia cada especial.
  - Una me hizo cambiar un texto: el 10 de agosto de 2027 (uno de sus días de prueba) es el día en que Chencho va por primera vez a los magueyes, y ese especial cambiaba el párrafo del trabajo, que así no cambiaba con la hora. Ahora la lección del acocote va en el párrafo de la gente.
- **Las 7 nuevas** (`CuentoG2Prueba`):
  - que estén las tres, con la temporada 1 escrita, sin problemas frente a su calendario y con su género;
  - Kichizō: Okiku, Shinsuke y el embarazo de Ume, cada uno a su tiempo, y sus fiestas sin nombres que llegan;
  - Kichizō: en la aldea, sin casa y en Ōyama amanece y trabaja allá a toda hora, y fuera de esos días no sale nada de ellos;
  - Mary: Jem, el cuarto con Jem, Tom, Bridget y el telar de su padre, cada uno a su tiempo;
  - Mary: en la huelga y en las semanas con Tom no amanece ni trabaja en la fábrica;
  - José: Rafael, Cuca, doña Refugio y Nicolás, cada uno a su tiempo;
  - José: con la mano herida no raspa, y fuera de esas dos semanas sí.
- **Las pruebas nuevas fallan cuando deben:** quité la fecha de Tom y la de Nicolás. Fallaron las dos («Tom antes de nacer», «Nicolás después de irse»), y se restauraron.
- **Leí nueve cuentos enteros**, tres de cada uno, en días normales, especiales y de estado. Un arreglo salió de ahí: en los estados el párrafo de la gente seguía en casa (la viuda Otake «la de al lado» con el callejón quemado, o Kichizō en la aldea). Ahora la aldea, el templo, Ōyama y la huelga tienen su propia variante de la gente.
- **Compilación de Android:** pendiente; se agrega aquí al terminar la corrida de `apk.yml`.
- **Capturas:** 0, como dice el plan.

## Lo que queda para después

- **Los textos de las temporadas 2 a 5** (los especiales nuevos de cada año) son de las tandas Y. Los estados de esas temporadas ya tienen sus variantes de todos los días.
- **En los días de fiesta el párrafo del trabajo sigue siendo el de un día normal**, como desde el piloto.
