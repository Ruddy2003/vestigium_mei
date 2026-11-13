# Tanda T1a: los cuentos de Naa, Ata e Ipy

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A1. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los tres primeros personajes del grupo 1, con la gramática y el tono que aprobaste en P3a. Son personajes comunes, así que desde esta tanda, en **Otras vidas** y con **cualquier tema**, sus detalles empiezan con «Hoy, en su vida»:
- **Naa**, cazadora y recolectora;
- **Ata**, agricultor de Çatalhöyük;
- **Ipy**, campesino de Tebas.

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasNaa.kt` | Los textos de Naa y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasAta.kt` | Los de Ata |
| `vidas/piezas/PiezasIpy.kt` | Los de Ipy |
| `vidas/piezas/TodasLasPiezas.kt` | Registra las tres |
| `app/src/test/…/vidas/CuentoG1Prueba.kt` | 7 pruebas propias de estos tres |
| `app/src/test/…/vidas/CuentoPrueba.kt` | Un cambio: «un personaje sin piezas no tiene cuento» ya no puede usar a Naa (ahora tiene). Usa a uno sin calendario y, mientras quede alguno, a uno con calendario y sin piezas |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Dos cuentos de muestra

**Naa, la noche sin luna** (14 de julio de 2027, a las 9:30). Título: «La noche sin luna: con los fuegos apagados, los hombres bailan y las mujeres cantan». Los párrafos 4, 5 y 8 son de ese día.

> Antes de que salga el sol se oye a los hombres afuera, hablando bajo de huellas y de vientos. Naa sale de la choza de hierba, que se hizo en una tarde y aguanta lo que tiene que aguantar, y se estira hasta que le suena la espalda.
>
> (…)
>
> Hoy no habrá luna, y todos lo saben desde hace días: la han visto adelgazar hasta desaparecer. Esta noche se baila. Los hombres sacan de sus bolsas las plumas negras de avestruz, los cascabeles para el tobillo y las capas oscuras.
>
> Las mujeres se sientan aparte, donde no se las ve, y empiezan a cantar en la oscuridad. Uno a uno, los hombres salen de detrás de un árbol y bailan golpeando el suelo con el pie derecho, y cada uno le dedica su baile a alguien: a un hijo, a una mujer, a un amigo.
>
> (…)
>
> Esta noche los fuegos se apagan y el campamento queda más negro que el cielo. Solo se oyen el canto, los cascabeles y el golpe de los pies en la tierra. Cuando las mujeres se levantan y entran al baile, Naa va con ellas, y por un rato no sabe dónde termina ella y dónde empieza el canto.

**Ipy en la obra del rey** (20 de septiembre de 2027, a las 9:30). Título: «El odre». Está en el estado «obra»: la mañana, el trabajo, el pueblo, la gente y el anochecer pasan en la otra orilla.

> Ipy despierta en el campamento de la obra, en la otra orilla, entre cientos de hombres que duermen como él, en el suelo, bajo un techo de palmas. Un escriba pasa contando cabezas antes de que salga el sol. Al que falta, se le apunta.
>
> (…)
>
> A esta hora arrastra piedra. Un bloque grande va sobre un trineo de madera, y veinte hombres tiran de las cuerdas mientras otro echa agua delante para que resbale. Un capataz marca el ritmo con palmas. Ipy tira y no piensa, que es lo mejor que se puede hacer.
>
> (…)
>
> Al caer la tarde se acaba el trabajo, y el escriba apunta en un trozo de cerámica lo que hizo cada cuadrilla. Los hombres se lavan en el canal, comen y se tumban. Ipy mira hacia la otra orilla, donde se encienden las lámparas de las aldeas, y busca la suya.

## Cómo se adaptó la gramática

- **Naa y Ata viven antes de la escritura.** El cuento no dice ningún año ni usa palabras de calendario (semanas, meses): el tiempo se cuenta en lunas, lluvias y secas. Una prueba vigila que no aparezca «a. C.» en sus cuentos.
- **El «dato de su época»** de cada párrafo sale de lo que se sabe de cada lugar:
  - **Naa:** los cazadores y recolectores de hoy (los hadza): el palo de cavar endurecido al fuego, las cáscaras de huevo de avestruz, el pájaro que guía a la miel, la cera que se esconde para que siga con hambre, la comida que se reparte, el baile de la noche sin luna.
  - **Ata:** el pueblo sin calles donde se entra por el techo, los muertos bajo la plataforma, el enlucido, la obsidiana, la figurilla en el arcón del grano.
  - **Ipy:** el cigoñal, la cerveza con caña, el pan con arena, el ojo de Horus, el trueque, la cuerda del escriba, la siembra con cabras que entierran el grano.
- **Los estados de A1 cambian el cuento:**
  - **Ata en la montaña de la obsidiana** (estado «montana», doce días en las temporadas 1 y 5): la mañana, el trabajo (las seis cosas que hace en «Un día suyo»), el pueblo, la gente y el anochecer pasan allá.
  - **Ipy en la obra del rey** (estado «obra», del 28 de agosto al 27 de octubre de cada año): duerme y trabaja en la otra orilla; la gente es su cuadrilla y la familia llega por recados.
- **El trabajo cambia con la estación** (`meses`):
  - Ata siembra en octubre y noviembre, escarda de marzo a junio, junta estiércol en verano y corta juncos en invierno.
  - Ipy espanta pájaros del trigo verde de noviembre a febrero, siega de marzo a mayo, trilla en junio y julio, y siembra en noviembre y diciembre.
  - Ipy solo duerme en el techo de abril a octubre.

## Lo que cada cuento respeta de su calendario

**Naa**
- Sese aparece hasta que muere (temporada 3).
- Mai, desde que nace (temporada 2); el embarazo, del 2 de septiembre de la temporada 1 a ese día.
- Iru no aparece mientras vive en otro campamento: de julio de la temporada 2 a octubre de la 3. Vuelve «con un niño nuevo».
- Las fiestas que vuelven cada año no nombran a Sese, a Mai, a Iru ni a Ekwa, que pasa herido parte de la temporada 4. Una prueba lo vigila.

**Ata**
- Ari, su madre, hasta que muere (temporada 2); Seli, desde que nace (temporada 4); el embarazo de Nesu, entre los dos.
- Las fiestas que vuelven cada año no dicen si Ata sube o no a la montaña, porque solo sube en las temporadas 1 y 5.
- **Un arreglo al leer los cuentos:** Tamu subía y bajaba la escalera «veinte veces al día» antes del día en que la sube solo por primera vez (2 de noviembre de la temporada 1). Ahora sus variantes de todos los días no lo ponen en la escalera.

**Ipy**
- Baket, su madre, hasta que muere (temporada 3); Amenemope, desde que nace (temporada 4); el embarazo de Henut, entre los dos.
- La Fiesta del Valle de la temporada 3 cae once días después de la muerte de Baket. Su texto, que vale para los cinco años, habla de «los suyos» sin nombrar a nadie.

## Cuánto texto tiene cada uno

| | Demeas (P3a) | Pierre (P3b) | Naa | Ata | Ipy |
|---|---|---|---|---|---|
| Variantes de los ocho párrafos | 89 | 76 | 67 | 83 (15 de «montana») | 81 (15 de «obra») |
| …de ellas, con fecha o meses | 7 | 14 | 6 | 7 | 11 |
| Oraciones de tiempo y de estación | 26 | 29 | 21 | 31 | 22 |
| Objetos del día | 12 | 12 | 12 | 12 | 12 |
| Oraciones de entorno y de mundo | 12 | 21 | 13 | 19 | 19 |
| Días especiales de la temporada 1 | 23, con 106 párrafos | 21, con 81 | 17, con 71 | 16, con 68 | 18, con 80 |
| **Textos en total** | **245** | **219** | **184** | **213** | **214** |
| Letras en total | 50,993 | 42,967 | 35,018 | 38,758 | 40,656 |
| **Cuento medio** (2027, a las 9 y a las 19) | 1,914 | 2,016 | **1,888** | **1,930** | **1,942** |

- **El largo** queda dentro de lo aprobado (de 1,800 a 2,150 de media).
  - Naa es la más corta porque sus párrafos del tiempo eran breves. Al leerla los alargué un poco: pasó de 1,859 a 1,888.
- **Esta tanda llevó unas 114,000 letras.** Tres personajes por tanda caben.

## Verificación

- **Arnés local** (Kotlin 2.1.0, como en P1): compila sin avisos y pasan las **92 pruebas**:
  - 35 de P1, 18 de P2 y 25 del cuento;
  - 7 de A1 y 7 nuevas de esta tanda.
- **Las pruebas generales** de `CuentoPrueba` revisan solas a los tres nuevos:
  - piezas bien escritas frente a su calendario;
  - ningún año escrito y ningún cuento repetido de 2026 a 2031;
  - ocho párrafos enteros y el largo aprobado;
  - el mismo cuento todo el día salvo el trabajo;
  - el título del objeto en un día normal, y la certeza de cada día especial;
  - el primer día de cada especial cambia 3 o 4 párrafos, y los especiales largos cambian al menos 4 párrafos de un día al siguiente.
- **Siete pruebas nuevas** (`CuentoG1Prueba`):
  - que estén las tres, con la temporada 1 escrita, y que Naa y Ata no digan «a. C.»;
  - Naa: nadie aparece antes ni después de tiempo, y sus fiestas no nombran a nadie que llegue o se vaya;
  - Ata: en la montaña no amanece ni trabaja en casa, y fuera de ella no sale nada de la montaña;
  - Ata: Ari, Seli, la siembra y la escarda, cada una en su tiempo;
  - Ipy: de agosto a octubre de los cinco años trabaja en la obra a cualquier hora, y en casa no sale nada de la obra;
  - Ipy: Baket, Amenemope, la siega y el techo en verano, cada cosa en su tiempo.
- **Comprobé que esas pruebas fallan cuando deben.** Quité a propósito el `hasta` de Sese y los meses de dormir en el techo: las dos pruebas fallaron («Sese después de morir», «duerme en el techo con frío»). Luego lo dejé como estaba.
- **Leí cuentos enteros** de los tres, en días normales, especiales y con estado. Salieron cuatro arreglos:
  - En febrero, que en la sabana es la seca corta, Naa veía «los baobabs sin hojas», que es cosa de la seca larga.
  - Una frase del objeto («le enseñó a cuidarlo») no casaba con objetos que se comen, como la miel.
  - Ipy amanecía en el techo en diciembre «cuando hace calor».
  - Tamu y la escalera (ver arriba).
- **Compilación de Android:** la corrida 210 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `248d67f`).
- **Capturas:** 0, como dice el plan. Las pruebas de `vidas/` vuelven a correr en la siguiente corrida de capturas.

## Lo que no se puede comprobar aquí

- **Cómo se ve la sección** en la pantalla de un personaje común: es la misma sección y el mismo código que la de Demeas, pero ahora sale con cualquier tema. La verán las capturas de la siguiente corrida que saque `otrasVidas`.
- **Los datos de época de los párrafos** salen de las páginas abiertas en A1 y de lo que sé de cada lugar; no abrí una fuente por párrafo. Por ejemplo:
  - Naa: las cáscaras de avestruz enterradas en los caminos, que es cosa de los san;
  - Ata: el juego de tabas;
  - Ipy: el escriba que apunta en un trozo de cerámica, el obrero de las tumbas que falta por un escorpión, el pilar de Osiris al final de Joiak.

  Es un cuento, y la nota al pie lo dice.

## Para las tandas que siguen

- **T1b (Lucio, Alice y Citlali):** el patrón queda igual. Ninguno de los tres tiene estados.
  - **Alice:** su Pascua cambia cada año, pero el texto de cada fiesta es uno solo.
  - **Citlali:** su chinampa se ahoga en agosto de la temporada 1 (arco `chinampa`). Las variantes del trabajo y del pueblo tienen que saberlo: el entorno lo dice solo, pero el trabajo en la chinampa necesita `meses` o una fecha (`desde = "almacigo"`).
- **Y2028:**
  - Naa necesitará los especiales del año de Mai (el lago que se come la orilla, la pelea de Iru);
  - Ata, los de la muerte de Ari y el monte que humea;
  - Ipy, los del jubileo.
