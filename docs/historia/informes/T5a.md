# Tanda T5a: los cuentos de Tomás, Ochiyo y Marie-Jeanne

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de A5. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los tres primeros personajes del grupo 5. En **Otras vidas**, con su tema, empiezan con «Hoy, en su vida»:
- **Tomás**, grumete de un balandro pirata en las Bahamas y el Caribe, de 1718 a 1722 (tema Pirata);
- **Ochiyo**, sirvienta de una casa de té en una posta del Tōkaidō, de 1830 a 1834 (Japón Edo);
- **Marie-Jeanne**, lavandera en un barco-lavadero del Sena, en París, de 1789 a 1793 (Revolución).

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasTomas.kt` | Los textos de Tomás y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasOchiyo.kt` | Los de Ochiyo |
| `vidas/piezas/PiezasMarieJeanne.kt` | Los de Marie-Jeanne |
| `vidas/piezas/TodasLasPiezas.kt` | Registra los tres |
| `vidas/anios/AnioG5.kt` | Cuatro retoques de A5 que pedían los cuentos (ver abajo) |
| `app/src/test/…/vidas/CuentoG5Prueba.kt` | 7 pruebas propias de estos tres (archivo nuevo; T5b agregará las suyas) |
| `docs/historia/informes/A5.md` | El resultado de su compilación |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`).
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Los estados se viven entero.** La carena de cada marzo (Tomás, en un cayo), su mes en Nassau, los tres meses de peregrinos hacia Ise (Ochiyo) y el Sena helado de enero (Marie-Jeanne) tienen su mañana, su pueblo, su tiempo, su anochecer y un texto para cada cosa que hacen en el día, con al menos tres variantes de lo que más se repite. Los dos días de Versalles los cuenta entero su día especial, día por día.
2. **Los que trabajan por turnos no dicen la hora.** Tomás trabaja tres guardias, come dos veces y duerme dos; Ochiyo y Marie-Jeanne trabajan dos tramos y comen dos veces. Sus textos valen a cualquiera de esas horas: el baño de la posta se «prepara para los que se queden», no «para los que llegaron a dormir».
3. **Los días libres y las fiestas de guardar tienen su párrafo de trabajo.** Ochiyo no trabaja en sus dos días libres del año (los pasa en la aldea con su madre). Marie-Jeanne no lava en Pascua, la Fête-Dieu, la Asunción, Todos los Santos, Navidad ni el martes de carnaval; en la Bastilla, tampoco. Tomás trabaja siempre: en un barco pirata no hay fiesta que pare la guardia.
4. **El pueblo lee su arco:** el casco del balandro (limpio después de la carena, sucio antes de la siguiente), el precio del arroz en la posta y el del pan en París.

## Cuatro retoques del calendario de A5

Al escribir los cuentos vi cuatro cosas del calendario que harían decir a los cuentos algo falso, y las cambié (no cambian ninguna fecha ni ningún estado):
- el martes de carnaval de Marie-Jeanne decía «crepes en la sección», pero las secciones de París son de 1790: ahora, «en la escalera»;
- la Asunción decía «la procesión del voto del rey», que en 1792 ya no tenía rey: ahora, «la procesión de la Virgen por la Cité»;
- entre sus objetos estaba el asignado, que no existe hasta 1790 y saldría en el cuento desde enero de 1789: ahora es el sueldo, la moneda de cobre;
- el resumen de la temporada 5 de Ochiyo decía que Gonta se va al norte, pero el calendario no tiene ese día: lo quité del resumen.

## Un cuento de muestra

**Marie-Jeanne, en el Sena helado** (10 de enero de 2027, a las 12:15):

> A Marie-Jeanne la despierta el silencio: sin el ruido de las paletas, sin los gritos de los barqueros, sin el agua. El Sena lleva semanas helado de orilla a orilla, y París entero está callado y blanco. Se queda un rato en la cama, para no gastar el calor.
>
> (…)
>
> A esta hora come la sopa que dan en la parroquia a los pobres del barrio, en una cola larga de gente que no se mira a la cara: agua caliente con pan y un poco de grasa. Lleva un cuenco para Paul. No le da vergüenza. Le dio el primer día.
>
> (…)
>
> Al caer la tarde las calles se vacían y el barrio se queda a oscuras: no hay aceite para los faroles. Solo brilla el río helado, blanco bajo la luna. Marie-Jeanne lo mira desde la ventana y piensa en el barco, atrapado allá abajo. El hielo cruje como un viejo.

## Lo que cada cuento respeta de su calendario

**Tomás**
- Kofi le enseña hasta que cae con la fiebre (julio de la temporada 3); después de su muerte, se le recuerda.
- El capitán es Grady hasta que la tripulación lo echa (noviembre de la 2); después, Hollis.
- El velero se queda en Nassau el día de la votación (temporada 1): los textos de todos los días no lo nombran.
- Cada **carena**, los cinco años, en el cayo; su mes en **Nassau** (temporada 1).
- El pueblo lee el casco.
- Las fiestas que vuelven cada año no nombran a Kofi, a Grady ni a Hollis.

**Ochiyo**
- Osen, desde que llega (marzo de la temporada 1); la niña asustada hasta que se escapa una noche (mayo de la 3), y casada con el hijo del herrero desde septiembre de la 4.
- Sakichi, desde que lo conoce (octubre de la 1); prometidos desde mayo de la 4, casados desde noviembre de la 5.
- Los **peregrinos de Ise** (de mayo a agosto de la temporada 1) y sus **dos días libres** de cada año.
- El pueblo lee el arroz, que sube con el hambre de Tenpō.
- Las fiestas que vuelven cada año no nombran a Osen ni a Sakichi.

**Marie-Jeanne**
- Jacques, aguador; en la guardia del barrio desde julio de la temporada 1; en la frontera de julio de la 4 a junio de la 5, y después con la pierna mala.
- Paul, en la calle hasta que empieza a ayudar en el barco (noviembre de la 1); lee desde septiembre de la 5.
- Antoine, el impresor, desde que llega al barrio (mayo de la 2).
- La asamblea del barrio, desde la Bastilla.
- El **Sena helado** (enero de la temporada 1).
- El pueblo lee el pan.
- Las fiestas que vuelven cada año no nombran a Jacques (que falta en la Navidad de 1792 y en la Pascua de 1793) ni a Antoine.

## Cuánto texto tiene cada uno

| | Tomás | Ochiyo | Marie-Jeanne |
|---|---|---|---|
| Variantes de los ocho párrafos | 105 (41 de sus dos estados) | 92 (23 de su estado) | 89 (19 de su estado) |
| …de ellas, con fecha o con meses | 6 | 9 | 11 |
| Oraciones de tiempo y de estación | 19 | 29 | 29 |
| Frases de entorno y de mundo | 9 | 9 | 9 |
| Objetos del día | 12 | 12 | 12 |
| Días especiales de la temporada 1 | 17, con 56 párrafos | 18, con 58 | 19, con 70 |
| **Textos en total** | **237** | **236** | **245** |
| Letras en total | 42,698 | 42,065 | 44,109 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,121** | **2,141** | **2,161** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **166 pruebas**: las 159 de antes y 7 nuevas.
- **Lo que encontraron las pruebas generales:** el día de los huracanes de Tomás cambiaba el párrafo del tiempo con un `{cielo}` dentro, que en los días especiales no se llena; lo quité.
- **Las 7 nuevas** (`CuentoG5Prueba`):
  - que estén los tres, sin problemas frente a su calendario y con su género;
  - Tomás: Kofi vivo y muerto, Grady y Hollis a su tiempo; la carena de los cinco años y Nassau, que se notan y que varían;
  - Ochiyo: Osen, Sakichi, la promesa y la boda a su tiempo; los peregrinos de Ise, que se notan y que varían; y que en sus dos días libres de los cinco años no trabaja;
  - Marie-Jeanne: Jacques en la guardia, en la frontera y de vuelta; Antoine; Paul en el barco y leyendo, a su tiempo; el Sena helado, que se nota y que varía; y que no lava en Pascua de los cinco años ni en la Asunción, Todos los Santos y Navidad mientras se celebran.
- **Las pruebas nuevas fallan cuando deben:** le quité a Sakichi su fecha de llegada, a Kofi la de su muerte y a la Navidad de Marie-Jeanne su párrafo de trabajo; fallaron las tres, y se restauró.
- **Leí dieciséis cuentos enteros**, cinco o seis de cada uno. Salieron de ahí:
  - en la Navidad, Marie-Jeanne lavaba: ahora el barco está cerrado;
  - en la posta, con los peregrinos llenando el camino, el cielo de lluvia decía «el camino se vacía»;
  - un cielo nublado de Marie-Jeanne seguido de «la ropa se seca sola» del verano;
  - el barco-lavadero «desde el que se ve» salía dos veces en el mismo cuento (en el tiempo y en el pueblo);
  - los textos del trabajo de Ochiyo hablaban de los viajeros «que se quedan a dormir» a las 9 de la mañana;
  - el segundo día de Versalles hablaba de la ropa tendida en el barco: ahora tiene su propio párrafo del tiempo.
- **Al repasar los textos antes de probarlos** corregí: las casas sobre los puentes de París, que se derribaron en 1786–1788; una vieja lavandera que «lavaba a su lado desde hacía veinte años» cuando Marie-Jeanne llegó a París a los quince; el dato de un objeto con `{nombre}`, que ahí no se llena; un deseo de Tanabata que Ochiyo «no sabe escribir» y que en la temporada 3 ya sabría; la Nochebuena puesta el 25 de diciembre; y la escarapela del 20 de julio de 1789, que todavía era azul y roja (el blanco llegó el 27, según la página de la Bastilla).
- **Compilación de Android:** la corrida 222 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `acf6532`).
- **Capturas:** 0, como dice el plan.

## Lo que no se pudo comprobar

- **Las costumbres del barco** (las reglas firmadas, el voto, la carena en un cayo escondido, las tortugas guardadas vivas, la bandera roja sin cuartel, el coy aprendido de los indios) vienen de lo que se sabe de los piratas de esos años; la carena en la playa sí está en la página de A5. **[Aproximación]**
- **Las de la posta** (el mantear a alguien después de la limpieza del hollín, la sardina en la puerta del setsubun y las habas por cada año, el caballo de pepino de Bon, los farolillos al río, las sirvientas que llaman a los viajeros desde la puerta, el sereno con sus tablillas, los cobradores del fin de año, los criados que se escapan a Ise sin permiso) vienen de lo que se sabe del Japón de Edo, no de páginas abiertas en la sesión, salvo lo que ya estaba en A5. **[Aproximación]**
- **Las de París** (el barco-lavadero y su secadero, la colada con ceniza, el buey gordo del carnaval, la hoguera de San Juan en la plaza de Grève, los quince sueldos de Réveillon, los fusiles de los Inválidos) vienen de lo que se sabe del París de 1789. La muchacha del tambor, los cañones arrastrados y los panes en las bayonetas de la marcha a Versalles sí están en su página. **[Aproximación]**
- **Las personas son inventadas**, salvo los piratas, los reyes, los daimios sin nombre y los hechos de las noticias, que siguen las fuentes de A5.
