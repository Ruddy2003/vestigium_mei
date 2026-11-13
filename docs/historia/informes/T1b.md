# Tanda T1b: los cuentos de Lucio, Alice y Citlali

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T1a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros tres personajes del grupo 1. Con esto, los seis del grupo tienen cuento. En **Otras vidas**, con **cualquier tema**, sus detalles empiezan con «Hoy, en su vida»:
- **Lucio**, zapatero de Roma;
- **Alice**, campesina de una aldea inglesa;
- **Citlali**, que cultiva una chinampa en Tenochtitlan.

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasLucio.kt` | Los textos de Lucio y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasAlice.kt` | Los de Alice |
| `vidas/piezas/PiezasCitlali.kt` | Los de Citlali |
| `vidas/piezas/TodasLasPiezas.kt` | Registra las tres |
| `vidas/anios/AnioG1.kt` | Dos arreglos al calendario de Citlali (ver «Lo que cambió en el calendario de Citlali») |
| `app/src/test/…/vidas/CuentoG1Prueba.kt` | 4 pruebas más, una por personaje y dos para Citlali |
| `docs/historia/fuentes.md` | Los dos arreglos de Citlali |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`).
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que cambió en el calendario de Citlali

Al escribir su cuento salieron dos problemas del calendario que hice en A1. Los dos son de contenido, no del motor.

1. **La cosecha de la temporada 1 caía con la chinampa ahogada.**
   - La inundación es el 28 de agosto y la cosecha, el 25 de septiembre.
   - Como el texto de una fiesta que vuelve se comparte los cinco años, en la temporada 1 la cosecha lleva otro id, `cosecha_ahogada`: «La cosecha que no fue: lo poco que se salvó del agua».
   - Es la regla 9 del piloto, la de la fiesta que cae en un viaje.
2. **Faltaba dónde vive la familia mientras la casa está bajo el agua.**
   - Sin un estado, el cuento la ponía a amanecer en su chinampa.
   - Ahora tiene el estado **«fuera»**, del 28 de agosto al 19 de octubre de la temporada 1: viven con unos primos de Yaotl en el barrio alto.
   - El 20 de octubre vuelven a rehacer la chinampa.
   - Es como el «mar» de Demeas: el cuento lo lee de la ficha, y si algún día hay cuadro, también.

## Dos cuentos de muestra

**Citlali, fuera de casa** (20 de septiembre de 2027, a las 9:30). Título: «El chiquihuite». Está en el estado «fuera».

> La primera luz encuentra a Citlali ya despierta, sentada contra la pared del patio con Xocoyotl dormido en las piernas. La casa de los parientes es pequeña, y ahora duermen en ella tres familias. Nadie se queja, porque nadie tiene adónde ir.
>
> (…)
>
> A esta hora ayuda en el dique del barrio con otras mujeres, pasando cestas de piedra y lodo de mano en mano para cerrar una brecha. Nadie la ha llamado: va porque el agua no pregunta de quién es la chinampa que se lleva.
>
> Desde el barrio alto se ve la ciudad como nunca: medio bajo el agua, con los templos asomando y las canoas yendo por encima de lo que eran calles y huertos. En algún sitio de esa agua está su chinampa. Donde estaba la chinampa hay agua quieta, con hojas y ramas flotando.

**Alice, un día de noviembre** (15 de noviembre de 2027, a las 9:30). Título: «El cubo».

> En casa se desayuna de pie: un trozo de pan oscuro y un trago de cerveza floja, que es más segura que el agua del pozo. Walter ya se ha ido a la tierra del señor, que es la que se trabaja primero. Alice le guarda su pan para cuando vuelva.
>
> (…)
>
> En la aldea se dice que el rey es fuerte, y que por eso los campos están tranquilos. A sus treinta años, Alice ha enterrado a dos hijos que no llegaron al año. Están en el cementerio de la iglesia, junto al muro, sin piedra. Cuando va a misa pasa por ahí y les dice algo sin mover los labios.

## Cómo se adaptó la gramática

- **Lucio** vive en una ínsula sin cocina, trabaja en un taller abierto a la calle y pasa la tarde en las termas y en el foro. El cuento no repite lo que ya dice «Su vida» (el trigo del Estado, el baño por una moneda, los cien días de fiesta).
  - **Sus fiestas** son las de Roma: las Saturnales con su rey de la fiesta, las Vestalias con los burros de los molinos, las Quinquatrias con la escuela cerrada.
- **Alice** duerme en dos tandas, el primer sueño y el segundo, y el cuento lo usa en el párrafo del trabajo de la noche.
  - **Su trabajo cambia con la estación:** siembra en marzo y abril, escarda en mayo y junio, el heno en junio y julio, espiga en septiembre, rompe terrones en octubre y noviembre, y trilla de diciembre a febrero.
- **Citlali** vive en el lago: la canoa, el mercado de Tlatelolco, el cacao que se muerde por si está relleno de tierra.
  - **Las veintenas con sacrificios** (Tlacaxipehualiztli, Tóxcatl, Atemoztli) se cuentan como las vería alguien de allí: con respeto, sin detalle y sin juzgar.

## Lo que cada cuento respeta de su calendario

**Lucio**
- Félix es aprendiz del 12 de enero de la temporada 1 hasta que abre su puesto en la 5; desde entonces es «la competencia».
- Marco anda por la calle hasta que empieza la escuela (24 de marzo de la temporada 1), va a la escuela hasta la 5 y luego trabaja en el taller por las tardes.
- Sexto es marinero hasta que deja el mar (2 de diciembre de la temporada 4); después, trabaja en el taller.
- Tito aparece al nacer (temporada 4); el embarazo de Tertia, desde octubre de la 3.
- Las fiestas que vuelven cada año no nombran a Félix, a Sexto ni a Tito.

**Alice**
- Edith, hasta que muere (temporada 2).
- Agnes, en casa hasta que entra a servir (temporada 2); después viene los domingos.
- Joan, casada en la aldea vecina desde su boda (temporada 1); Matilda, desde que nace (temporada 3).
- Las fiestas que vuelven cada año no nombran a ninguna de las cuatro.

**Citlali**
- Del 28 de agosto al 1 de diciembre de la temporada 1 no se cultiva ni se cosecha nada en la chinampa: se saca lodo, se clavan estacas y se hacen almácigos. Las variantes de cultivo llevan `hasta = "inundacion"` o `desde = "almacigo"`.
- Las flores para vender, desde que las siembra (temporada 4).
- Xóchitl, desde que nace (temporada 2); Papan borda hasta que pierde la vista (temporada 3).
- Mazatl: su manta de guerrero desde el primer cautivo (temporada 4) y su mujer desde la boda (5).
- Las fiestas que vuelven cada año no nombran a Xóchitl.

## Cuánto texto tiene cada uno

| | Naa | Ata | Ipy | Lucio | Alice | Citlali |
|---|---|---|---|---|---|---|
| Variantes de los ocho párrafos | 67 | 83 | 81 | 69 | 69 | 85 (13 de «fuera») |
| …de ellas, con fecha o meses | 6 | 7 | 11 | 11 | 11 | 11 |
| Oraciones de tiempo y de estación | 21 | 31 | 22 | 27 | 29 | 23 |
| Objetos del día | 12 | 12 | 12 | 12 | 12 | 12 |
| Oraciones de entorno y de mundo | 13 | 19 | 19 | 15 | 12 | 21 |
| Días especiales de la temporada 1 | 17, con 71 párrafos | 16, con 68 | 18, con 80 | 19, con 78 | 22, con 87 | 17, con 70 |
| **Textos en total** | **184** | **213** | **214** | **201** | **209** | **211** |
| Letras en total | 35,018 | 38,758 | 40,656 | 38,401 | 40,943 | 40,336 |
| **Cuento medio** (2027, a las 9 y a las 19) | 1,888 | 1,930 | 1,942 | **2,070** | **2,005** | **1,992** |

- **El largo** queda dentro de lo aprobado (de 1,800 a 2,150 de media).
- **Esta tanda llevó unas 120,000 letras.**

## Verificación

- **Arnés local** (Kotlin 2.1.0, como en P1): compila sin avisos y pasan las **96 pruebas**:
  - 35 de P1, 18 de P2 y 25 del cuento;
  - 7 de A1 y 11 de los cuentos del grupo 1 (7 de T1a y 4 nuevas).
- **Las pruebas generales** de `CuentoPrueba` revisan solas a los tres nuevos: piezas bien escritas, ningún año, ningún cuento repetido de 2026 a 2031, ocho párrafos, el largo aprobado, el título del objeto, la certeza y los días especiales.
- **Cuatro pruebas nuevas** (`CuentoG1Prueba`):
  - **Lucio:** Félix, Sexto, Tito y Marco aparecen a su tiempo, y sus fiestas no nombran a Félix, a Sexto ni a Tito.
  - **Alice:** Edith, Agnes, Joan y Matilda aparecen a su tiempo, la trilla va en invierno y la escarda en mayo y junio, y sus fiestas no nombran a las cuatro.
  - **Citlali, el estado «fuera»:** del 28 de agosto al 19 de octubre amanece y trabaja, a cualquier hora, con los parientes. De la inundación al primer almácigo no cultiva, y las flores empiezan en la temporada 4.
  - **Citlali, las personas:** Xóchitl, Papan y Mazatl aparecen a su tiempo.
- **Comprobé que esas pruebas fallan cuando deben.** Quité a propósito el `hasta` de Edith y el `desde` de una variante de cosecha de Citlali: las dos pruebas fallaron («Edith después de morir», «cosecha lo que da la chinampa con la chinampa ahogada»). Luego lo dejé como estaba.
- **Leí cuentos enteros** de los tres, en días normales, especiales y con estado. De ahí salió el estado «fuera» de Citlali.
- **Compilación de Android:** la corrida 211 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `8b8b992`).
- **Capturas:** 0, como dice el plan.

## Lo que no se puede comprobar aquí

- **Cómo se ve la sección** con estos personajes: es la misma de Demeas, pero ninguna captura la ha sacado todavía con un personaje común.
- **Los datos de época de los párrafos** salen de las páginas abiertas en A1 y de lo que sé de cada lugar; no abrí una fuente por párrafo. Por ejemplo:
  - Lucio: el vendedor que arranca pelos en las termas (Séneca), las vendas de lana de la estatua de Saturno, los muñecos de lana de las Compitales;
  - Alice: la carrera de sacos, el oso en la cerveza de la parroquia, el pastel del haba de Reyes;
  - Citlali: el joven de Tóxcatl que rompe una flauta en cada escalón, los collares de maíz reventado, el golpe que se dio Ahuízotl huyendo del agua.

  Es un cuento, y la nota al pie lo dice.
- **Mientras dura una fiesta, el párrafo del trabajo sigue siendo el de un día normal.** Lucio, por ejemplo, cose en las Saturnales. Es así desde el piloto: el día especial cambia 3 o 4 párrafos y el del trabajo casi nunca es uno de ellos.

## Para las tandas que siguen

- **El grupo 1 está completo** (calendarios y cuentos de la temporada 1).
- **Lo que aprendí para las T:** al escribir el cuento conviene leer el calendario buscando los días en que el personaje no puede estar donde dicen las variantes de siempre. Si no está en casa, hace falta un estado. Si una fiesta que vuelve cae en un año raro, le hace falta otro id. Mejor arreglarlo en el calendario que retorcer el texto.
- **Y2028:**
  - Lucio necesitará los especiales del año del Tíber y de la fiebre de Lucila;
  - Alice, los del príncipe de Gales y la muerte de Edith;
  - Citlali, los de la ciudad que se levanta y el nacimiento de Xóchitl, y el texto de la cosecha de siempre (`cosecha`), que en la temporada 1 no se usó.
