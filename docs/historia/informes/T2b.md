# Tanda T2b: los cuentos de Rosa, Alguien del mundo y Alguien de México

Fecha: 12 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T2a. Plan: `docs/plan_historia_y_escenas.md`.

## Qué trae

Los cuentos de la temporada 1 de los otros tres personajes del grupo 2. Con esta tanda, **los doce personajes comunes tienen cuento**: en **Otras vidas**, con cualquier tema, todos empiezan con «Hoy, en su vida».
- **Rosa**, secretaria en la Ciudad de México en 1960;
- **Alguien del mundo**, una mujer de una ciudad del norte de la India;
- **Alguien de México**, un hombre de una ciudad grande del centro del país.

| Archivo | Qué tiene |
|---|---|
| `vidas/piezas/PiezasRosa.kt` | Los textos de Rosa y, arriba, lo que tienen que respetar de su calendario |
| `vidas/piezas/PiezasMundo.kt` | Los de Alguien del mundo |
| `vidas/piezas/PiezasMexico.kt` | Los de Alguien de México |
| `vidas/piezas/TodasLasPiezas.kt` | Registra las tres |
| `app/src/test/…/vidas/CuentoG2Prueba.kt` | 4 pruebas más, propias de estos tres |
| `app/src/test/…/vidas/CuentoPrueba.kt` | El tope del largo medio pasa de 2,150 a 2,200 (ver abajo) |

- **No toca el motor** (`Cuento.kt`, `Ficha.kt`) **ni los calendarios.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que decidí

1. **Los dos «Alguien»: una mujer y un hombre**, como pediste. Alguien del mundo es mujer y Alguien de México, hombre.
2. **Los dos «Alguien» no tienen nombre en el cuento.** El cuento no usa `{nombre}`: dice «ella», «él» o nada («Se levanta antes que los demás…»), para que sigan siendo «alguien». Su gente sí tiene nombre: Ravi, el marido de ella, y Sonu, su hermano; Daniela, la esposa de él, y Toño, su compadre. Una prueba vigila que su nombre de la lista no aparezca en el cuento.
3. **El tope del largo pasa a 2,200.** Alguien del mundo quedó en 2,171 letras de media. Me dijiste que si un cuento se pasaba un poco lo dejara, así que en vez de recortarlo subí el tope de la prueba general de 2,150 a 2,200, con un comentario que lo explica. El mínimo (1,800 de media y 1,450 el más corto) no cambia.

## Un cuento de muestra

**Alguien del mundo, en la aldea** (25 de julio de 2028, a las 9:30). Está en el estado «aldea», cuidando a su madre:

> Se despierta en la casa de sus padres, en la aldea, con el canto de los gallos y el olor del fuego de estiércol con que su madre ya está calentando el agua. Aquí no hay alarma: hay luz. Sale al patio descalza, como cuando era niña.
>
> (…)
>
> A esta hora ayuda a su madre, que todavía está débil, con lo de la casa: barre el patio con la escoba de ramas, saca agua de la bomba de mano y le cocina el arroz y las lentejas, como su madre le enseñó y como ya no hace en la ciudad.
>
> (…)
>
> Al anochecer, en la aldea, se va la luz como todas las noches, y su madre enciende una lámpara de queroseno. Se oyen los grillos y, a lo lejos, un altavoz del templo. Ella se sienta junto a su madre en la oscuridad y le cuenta cosas de Anu, y su madre se ríe bajito.

## Lo que cada cuento respeta de su calendario

**Rosa**
- Arturo es su novio hasta la boda (17 de junio de la temporada 2) y su marido desde entonces. Vive en la vecindad de su mamá hasta la vuelta de Acapulco; luego, en Santa María la Ribera.
- Claudia, desde que nace (temporada 3); el embarazo, desde octubre de la 2. Beto entra al taller en la 2.
- Los estados: en **Acapulco** amanece, pasea y cena allá; en la **incapacidad** se queda en casa con Claudia y el pueblo es su colonia nueva, no el centro.
- Las fiestas que vuelven cada año no nombran a Arturo ni a Claudia. El Día de las Madres es de doña Carmen, y las posadas y los Reyes pasan «en la vecindad de su mamá», que vale antes y después de la boda.

**Alguien del mundo**
- Anu va al kínder desde abril de la temporada 1; Kabir, desde que nace (temporada 3); el embarazo, desde septiembre de la 2. Viven en dos cuartos de una azotea hasta la mudanza (noviembre de la 3).
- En la **aldea** (temporada 2) amanece, trabaja por el teléfono, come y duerme allá, y el cielo es el del campo.

**Alguien de México**
- Mateo va a la guardería, al kínder desde agosto de la temporada 1 y a la primaria desde la 4. Valeria, desde que nace (temporada 3).
- Las tres casas: el departamento chico (hasta marzo de la 3), uno más grande y más lejos, y la casa del fraccionamiento (desde febrero de la 5).
- En la **playa** (temporada 2) el día entero pasa allá, con su cielo y su pueblo.

## Cuánto texto tiene cada uno

| | Rosa | Alguien del mundo | Alguien de México |
|---|---|---|---|
| Variantes de los ocho párrafos | 86 (16 de sus estados) | 81 (11) | 80 (10) |
| …de ellas, con fecha | 7 | 9 | 9 |
| Oraciones de tiempo y de estación | 23 | 29 | 23 |
| Objetos del día | 12 | 12 | 12 |
| Oraciones de mundo | 11 | 11 | 11 |
| Días especiales de la temporada 1 | 16, con 57 párrafos | 20, con 67 | 20, con 70 |
| **Textos en total** | **225** | **236** | **232** |
| Letras en total | 39,584 | 42,834 | 41,373 |
| **Cuento medio** (2027, a las 9 y a las 19) | **2,073** | **2,171** | **2,103** |

## Verificación

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **116 pruebas**: las 112 de antes y 4 nuevas.
- **Dos cosas que encontraron las pruebas generales:**
  - Un especial de Alguien del mundo usaba `{edad}`, y la prueba de los especiales compara el texto sin llenar ese hueco. Lo quité de ese párrafo.
  - El largo de Alguien del mundo (ver «Lo que decidí»).
- **Las 4 nuevas** (`CuentoG2Prueba`):
  - que estén las tres, sin problemas frente a su calendario, con su género, y que los dos «Alguien» no usen su nombre;
  - Rosa: novio, marido, la vecindad, Claudia, sus fiestas, Acapulco y la incapacidad;
  - Alguien del mundo: Kabir, la azotea antes de la mudanza, sus fiestas y la aldea;
  - Alguien de México: Valeria, las tres casas, sus fiestas y la playa.
- **Las pruebas nuevas fallan cuando deben:** quité la fecha de Kabir y falló («Kabir antes de nacer»); se restauró.
- **Leí nueve cuentos enteros**, tres de cada uno. Un arreglo salió de ahí: en los estados, el tiempo y el pueblo seguían en la ciudad (en Acapulco, Rosa miraba el cielo «antes de entrar a la oficina»). Ahora Acapulco, la incapacidad, la aldea y la playa tienen su propio tiempo y su propio pueblo.
- **Compilación de Android:** pendiente; se agrega aquí al terminar la corrida de `apk.yml`.
- **Capturas:** 0, como dice el plan.
