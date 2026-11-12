# Guía de arte de las escenas de «Otras vidas»

_Creada el 11 nov 2026, al cerrar la tanda P4a. Recoge lo que el autor decidió ese día sobre el aspecto del cuadro, después de ver el primer paisaje de Helénico y pedir que se rehiciera. **Toda sesión que dibuje o cambie una escena lee esta guía entera antes de empezar** (P4b, S1 a S7 y las S chicas de cada año)._

## 0. Cómo se usa

- **La vara de calidad es el cuadro de Helénico** (`ui/escenas/EscenaHeleno.kt`), tal como está en el código.
  - Su imagen de referencia: `docs/diseno/escenas/referencia_heleno.jpg` (5 de mayo de 2027 a las 7:00, las 13:00 y las 21:00, dibujado con el arnés).
  - **Aprobadas también por el autor** (12 nov 2026), y por eso ejemplos válidos de su tipo: Gótico, otro exterior con sol (`referencia_gotico.jpg`, 10 de mayo de 2027), y Ciudad neón, la escena siempre de noche (`referencia_neon.jpg`: arriba Amaru el 5 de mayo de 2027 a las 7:00, 13:00 y 21:00; abajo Kei el 6 de mayo a las 7:00, 17:00 y 21:00). La vara sigue siendo Helénico.
  - Una escena nueva está terminada cuando, puesta al lado de esa imagen, **no se ve más pobre en ningún punto**: ni más vacía, ni más lisa, ni más plana, ni con las cosas más chicas.
- **La escena llana (`EscenaPlana.kt`) no es la vara.** Sirve para copiar el orden de las capas y ver cómo se lee el momento. Su dibujo es mucho más simple que el que se pide.
- **Las reglas de la sección 2 son obligatorias.** Lo que cambia de un tema a otro es cómo se cumplen (sección 5), no si se cumplen.
- **Si un tema no puede cumplir una regla al pie de la letra** (no tiene cielo, o sol, o agua), se cumple su propósito con lo que el tema sí tiene. Nunca se quita la regla sin poner algo en su lugar. Qué se cambió y por qué va en el informe de la tanda.
- **Si la guía y el código de Helénico no coinciden,** manda el código y se corrige la guía en el mismo PR.
- Esta guía es de **aspecto**. Es la decisión 20 del plan (`docs/plan_historia_y_escenas.md`). Qué cuenta cada escena lo dice ese plan (sección 3.3 y decisiones 8 y 18) y el comentario del calendario de cada personaje.

## 1. El estilo

**Gouache.** **[Diseño propio]**: el autor lo escogió entre tres variantes (gouache, acuarela y estampa).

- Formas de color opaco, con el borde limpio, como pintadas con pincel plano.
- La luz viene de un lado y todo arroja sombra.
- Pocos colores, que se llevan entre sí.
- Un grano fino de papel encima.

**Lo que no es:**
- **No es acuarela:** nada de manchas aguadas ni bordes que se corren.
- **No es estampa:** nada de contornos de tinta ni de planos de un solo color sin matiz.
- **No es ilustración de catálogo:** nada iluminado igual por todos lados, ni cosas sueltas puestas en fila.
- Sin contornos negros. Sin personas. Sin texto.

## 2. Las diez reglas

Cada una nació de un defecto que el autor señaló en el primer cuadro. La columna «Se comprueba» dice qué mirar en la hoja del arnés.

| # | Regla | El defecto que corrige (palabras del autor) | Se comprueba |
|---|---|---|---|
| 1 | La luz tiene dirección | «Nada proyecta sombra y todo está iluminado igual» | A las 7:00 y a las 13:00 las sombras tienen distinto largo y caen hacia el lado contrario al sol. Cada cuerpo tiene un lado claro y otro en sombra |
| 2 | El cielo tiene vida | «Es un degradado liso con la misma nube en el mismo sitio» | Dos días despejados seguidos no traen las mismas nubes. Algo enmarca el cuadro por arriba |
| 3 | La paleta es corta y va junta | «Colores de fábrica: el cielo y el mar son casi el mismo azul primario» | No hay azul puro ni colores saturados que no se repitan en otra parte del cuadro. La luz es blanca y cálida; las sombras, azul violeta |
| 4 | La superficie grande tiene profundidad | «El agua es plana: un solo color con rayitas, casi sin reflejos» | El agua cambia de color de lejos a cerca, refleja lo que tiene encima y se aclara en la orilla |
| 5 | El primer plano es un sitio, no una repisa | «Objetos pequeños, alineados y separados»; «que no se vean puestos para la foto, sino como elementos del entorno» | Las cosas son grandes, se tapan unas a otras, están a distintas profundidades y tienen señales de uso |
| 6 | Algo cruza las franjas | «Tres franjas horizontales sin nada que las cruce ni enmarque» | Al menos dos cosas verticales o diagonales atraviesan el cielo, el plano medio y el primer plano |
| 7 | Nada es liso | «No hay grano ni textura» | Las superficies grandes tienen desgaste, vetas o manchas, y el grano cubre toda la parte quieta |
| 8 | La noche está al nivel del día | «También mejora la noche» | A las 21:00 se distinguen las formas y los volúmenes, no solo siluetas. Lo encendido alumbra lo que tiene cerca |
| 9 | Los bordes son naturales | «El borde de la costa es muy lineal»; «es pasto que pasa directo a agua» | Ninguna orilla, loma o camino es una curva lisa. Entre dos materiales hay una franja de paso |
| 10 | Lo lejano tiene profundidad | «El pueblo se ve muy plano, muy pegado al suelo» | Lo que está lejos tiene poco detalle, pero varias filas, lado de luz y de sombra, y algo delante y detrás |

### Regla 1. La luz tiene dirección

- **De dónde viene:** `Rayo(luces)`, en `paisaje/Sombras.kt`. Se crea una vez al empezar la parte quieta y se pasa a todo lo que tenga volumen.
  - **[Aproximación]**: el sol se dibuja dentro del cuadro, pero la luz entra de lado y un poco desde delante. Con el sol de frente todo quedaría a contraluz.
- **Sombras arrojadas:** dentro de `capaSombra(rayo) { c -> … }`. Oscurecen lo que tienen debajo una sola vez, aunque se encimen.
  - De algo que está de pie: `sombraArrojada(rayo, silueta, pie, c, suelo, paredArriba)`. Se tumba en el suelo y sube por la pared de detrás.
  - De un follaje, una parra o un toldo calado: `sombraDeFollaje`. Es una masa con claros por donde pasa el sol.
  - De lo lejano o lo muy pequeño: un óvalo corto basta (ver `sombraCorta` en Helénico).
- **Lado de luz y de sombra:**
  - cuerpos redondos (vasijas, troncos, cestos, columnas): `volumen`;
  - cuerpos de caras planas (casas, muros, cajas): la cara que mira a la luz, más clara y cálida; la otra, con `mult(color, rayo.color, …)`;
  - montes e islas: `monte` ya lo hace.
- **Lo que falla si se olvida:**
  - Un árbol no recibe la sombra de su propia copa en el tronco: queda una mancha oscura. En Helénico el tronco va con `bajoElFollaje = false`.
  - Una sombra de follaje hecha con manchas sueltas parece piel de vaca. Tiene que ser una masa con huecos.
  - De noche, con el cielo cubierto o en un eclipse no hay sombras arrojadas. Ese día la forma la dan `volumen` y el contraste entre claros y oscuros: hay que mirar también una hoja con lluvia.

### Regla 2. El cielo tiene vida

- `cielo(luces, horizonte, semilla)` con la semilla del día: trae pinceladas y calima.
- `nubes(…)` escoge con la semilla del día una de cuatro formaciones del cielo despejado (borregos, torres, velos, calima), además de las del mal tiempo.
- **El marco de arriba** es de la escena, no de la biblioteca: algo cercano que entra por un lado y cubre parte del cielo. En Helénico es la copa del olivo.
  - No debe tapar el sol de las 7:00 ni la luna de las 21:00. **Pendiente en Helénico:** una rama tapa media luna a las 21:00.
  - Tampoco debe tapar la banderita (arriba a la izquierda, 26 × 40 unidades, a 14 del borde).
- **Lo que falla si se olvida:**
  - Nubes hechas de círculos sueltos parecen palomitas. Van en pisos, con el relleno macizo y la base plana.
  - Hojas claras y grandes repartidas al azar parecen confeti. Van chicas, siguiendo cada ramilla, sobre una masa oscura; las claras, solo en el fleco.
  - Un árbol hecho con una bola sobre un palo parece una paleta. Las hojas grandes se dibujan una por una (ver `hojaDeHiguera`).

### Regla 3. La paleta es corta y va junta

- **El cielo es de todos:** sale de los 12 instantes de `HITOS`, en `paisaje/Luces.kt`. De día es un azul verdoso apagado que baja a crema en el horizonte.
- **Cada escena escribe su paleta al principio del archivo:** de 12 a 18 colores con nombre (en Helénico, `MARMOL`, `TERRACOTA`, `EGEO`…). Los colores sueltos dentro del dibujo son variaciones de esos.
- **Luz cálida, sombra fría.** El lado de la luz tira a crema; el de la sombra, a azul violeta (`rayo.color`). Nunca se oscurece con negro ni con gris.
- **Los colores se repiten.** El de un tejado vuelve en una vasija; el del agua, en una sombra. Un color que aparece una sola vez se nota como un pegote.
- **El agua no usa el azul del cielo:** `MAR`, `MAR_HONDO` y `MAR_ORILLA` son turquesas, y cada escena puede pasar los suyos a `agua`.
- **Todo color pasa por `luces.tono`,** menos lo que da luz propia.

### Regla 4. La superficie grande tiene profundidad

Con agua:
- `agua`: honda y oscura lejos, clara cerca, con rachas de viento, calmas y rizos curvos.
- `bajio` y espuma en cada orilla.
- `reflejoRoto` debajo de **todo** lo que toca el agua o está en su borde: naves, muelles, casas, peñas, la costa.
- `estela`: la calle de luz del sol bajo o de la luna.
- En la parte viva, `destellos` y el `reflejo` de cada luz encendida.

Sin agua, ver la sección 5: la regla pasa a la superficie más grande que tenga el cuadro.

### Regla 5. El primer plano es un sitio, no una repisa

- **Tamaño.** En Helénico el primer plano empieza a los tres cuartos del alto (y = 218 de 285). La tinaja mide unas 60 unidades de alto, una quinta parte del cuadro; las ánforas, de 40 a 46; el banco, 90 de ancho.
- **Se enciman.** Cada cosa tapa un poco a otra. Se ordenan por dónde pisan (`sortedBy { pie.y }`) y se pintan de atrás adelante.
- **Están donde las dejaría alguien:** arrimadas al muro, apoyadas, colgadas, ladeadas. No en fila, no centradas, no a la misma distancia.
- **A distintas profundidades:** unas contra el muro, otras en medio del suelo, alguna cortada por el borde del cuadro.
- **Con uso:** desconchones, chorreras, zócalo comido, marcas de torno, salitre, hojas caídas.
- **El patrón de Helénico** (`patio`, `Cosa`): cada cosa es una silueta, un pie y una función que la pinta. Con eso salen, en este orden:
  1. el muro y el suelo;
  2. una capa de sombras: la de cada cosa y la del follaje sobre el muro y el suelo;
  3. las cosas;
  4. otra capa, más tenue, con la sombra del follaje recortada sobre cada cosa.
- **El objeto del día** va en un sitio fijo y bien a la vista, pero no aislado. En Helénico, nueve van sobre el banco; el remo, apoyado; la red y el manto, colgados.
- **Queda suelo libre para el gato:** un tramo de suelo sin estorbos, de al menos 110 unidades de largo, y un sitio alto (las anclas `SUELO` y `MIRADOR`). El gato mide unas 43 unidades de largo y otras tantas de alto, camina por delante de todo y tapa lo que tiene detrás: conviene que el objeto del día no quede justo en su camino.
- **El gato recibe la luz de la escena.** Con sol no hay que hacer nada. Si la luz es propia (focos, un interior), la escena cambia `luzDelGato`, como hace `EscenaNeon`.

### Regla 6. Algo cruza las franjas

- En Helénico: el tronco del olivo (de abajo arriba), el pilar del vano, el remo en diagonal y la tinaja, que sube por encima del murete.
- La vista es la de **alguien de pie** (decisión 8 del plan), que ve el suelo delante de sí: ni a ras de suelo ni a vista de pájaro. Así el suelo del primer plano se ve y las sombras tienen dónde caer.
- El horizonte no va a la mitad: en Helénico está al 45 % del alto.

### Regla 7. Nada es liso

- `grano(azar, fuerza)` es **lo último** de la parte quieta, con una semilla fija de la escena (no la del día), para que no baile de una hora a otra. Fuerza: `0.6f + 0.4f * rayo.dia`.
- Cada superficie grande lleva lo suyo: vetas, losas, juntas, manchas de humedad, hierbas en las grietas.
- Los degradados largos se rompen con pinceladas (`mancha`).

### Regla 8. La noche está al nivel del día

- Todo lo de las reglas 4 a 7 vale igual de noche. La noche no es excusa para dejar siluetas.
- **Lo encendido alumbra lo que tiene cerca:** el muro detrás de una lámpara, el suelo bajo un brasero, el agua bajo una antorcha (`resplandor`, `reflejo`).
- **Lo muy oscuro se pierde de noche.** Un objeto negro o de barniz oscuro necesita un filo de luz cálida que crezca con `luces.noche`.
- **Una lámpara dentro de un cobertizo parece un incendio.** Se pone donde su luz caiga sobre una pared.
- A las 21:00 todavía queda resplandor en el horizonte: sirve para recortar la lejanía.

### Regla 9. Los bordes son naturales

- Las orillas, lomas y caminos se dibujan con **puntos puestos a mano**, con entrantes, salientes y tramos rectos (ver `COSTA`, de 21 puntos), no con una curva de cuatro puntos.
- **Entre dos materiales hay una franja de paso.** Entre la hierba y el agua de Helénico, de abajo arriba: agua clara y espuma, arena en las calas y roca en las puntas, un talud de tierra en sombra y, por fin, la hierba.
  - Lo mismo entre un muro y el suelo (zócalo gastado, hierbas), entre un camino y el campo, entre la nieve y la roca.
- Unas peñas o matas sueltas delante del borde lo rompen.

### Regla 10. Lo lejano tiene profundidad

- **Poco detalle, pero en capas.** El caserío de Helénico sube por una loma en tres hileras: cada una más grande, más nítida y un poco más abajo que la anterior (ver `ciudad`, `hilera`, `casita`).
- Cada casa lejana tiene su lado de luz y su lado de sombra, y una sombra corta en el suelo.
- **Algo delante y algo detrás:** una muralla o unos árboles por delante; un cerro y una calima por detrás, que lo despegan de los montes.
- El terreno no es plano: lomas suaves, bancales, un olivar.
- `luces.tono(color, lejos)` hace el resto: cuanto más lejos, más bruma.

## 3. El orden de las capas

Es el de `EscenaHeleno.quieta`. Una escena nueva lo sigue, cambiando lo que no tenga.

1. `cielo`, `astros`, `nubes`.
2. Montes lejanos; la superficie grande (`agua`); islas; sus reflejos.
3. La lejanía con su pueblo y sus campos.
4. El plano medio, sus sombras y sus reflejos; la `estela`.
5. Lo que el cuento mueve (la nave, la obra, el carro), en el sitio que diga una función que una prueba pueda leer.
6. El primer plano, con el patrón de la regla 5.
7. El marco de arriba.
8. `tiempoQuieto` (el velo de la lluvia o de la niebla) y `grano`.

En la parte viva solo va lo que se mueve: gotas, copos, humo, llamas, destellos y reflejos de luces. **Nada de sombras ni de grano ahí:** se traza unas 15 veces por segundo.

## 4. Errores que ya se cometieron

Antes de dar una hoja por buena, revisa que no esté ninguno.

| Se veía como… | Qué era | Cómo se arregló |
|---|---|---|
| Palomitas o burbujas | Nubes de círculos sueltos | Pisos, relleno macizo, base plana |
| Una barra clara bajo las nubes cercanas | La base de la nube se fundía igual lejos que cerca | El fundido de la base depende de `lejos` |
| Confeti | Hojas claras y grandes al azar | Hojas chicas alineadas con la ramilla; claras solo en el fleco |
| Piel de vaca | Sombra de follaje hecha de manchas | Una masa con claros (`BlendMode.Clear`) |
| Una mancha azul detrás del muro | Un óvalo de sombra que nadie arrojaba | Cada sombra sale de la silueta de algo que existe |
| Manchas de suciedad en las vasijas | La sombra del follaje, igual de fuerte sobre las cosas que sobre el suelo | La segunda capa de sombra va más tenue (0.8) |
| Una paleta | Árbol de bola y palo | Hojas una por una y su sombra en el suelo |
| Un incendio en el cobertizo | Una lámpara encendida en un hueco oscuro | La lámpara, junto a una pared que alumbre |
| Un hueco negro | Copa y jarra de barniz negro, de noche | Un filo cálido que crece con la noche |
| El sol tapado | Una rama delante del sol de las 7:00 | Se movió la rama: el marco deja ver los astros |
| Una raya | Costa de curva lisa | Puntos a mano: calas, puntas, peñas |
| Césped hasta el agua | Sin franja de paso | Arena, roca y talud entre la hierba y el agua |
| Una pegatina | Pueblo en una sola fila | Tres hileras en una loma, con muralla delante y cerro detrás |

## 5. Cómo se adapta a cada tema

**El principio.** Cada regla tiene un propósito. Cuando el tema no tiene la cosa de la que habla la regla, se busca en ese tema qué cumple el mismo propósito.

| Regla | Su propósito | Si el tema no tiene eso |
|---|---|---|
| 1. Luz con dirección | Que las cosas tengan volumen y el cuadro cambie con la hora | La luz sale de lo que alumbre ahí: una lámpara, un letrero, una hoguera, una ventana |
| 2. Cielo con vida | Que la mitad de arriba no esté vacía ni sea igual cada día | Lo que ocupe la parte de arriba (un techo, una bóveda, una pared de roca, un casco de nave) lleva detalle, y algo en él cambia con el día |
| 3. Paleta corta | Que todo parezca del mismo cuadro | Vale siempre. Cambian los colores, no la regla |
| 4. Superficie con profundidad | Que el plano más grande no sea una mancha de un color | La regla pasa a la arena, la nieve, el empedrado, la hierba o el piso |
| 9. Bordes naturales | Que nada parezca trazado con regla sin serlo | En lo construido los bordes sí son rectos, pero están gastados, rotos o sucios |

**Por tipo de escena:**

- **Exterior con sol** (casi todos los temas). Se aplica todo tal cual.
  - Cambian la paleta local, la flora, las casas y **el marco**, que tiene que ser de ese lugar: una parra, un alero, un toldo, un arco, una palmera, una vela con su jarcia, el borde de una tienda, una viga, una rama.
  - El cielo de `HITOS` es de todos y **no se retoca para un tema:** cambiaría Helénico. Si el lugar pide otro cielo (polvo del desierto, gris del norte, un cielo que no es de la Tierra), la escena tiñe las `Luces` que le da el momento en una función propia, como hace Helénico con su eclipse (`luces(m)`).
- **Sin agua** (desierto, estepa, montaña, ciudad). No se inventa un río para cumplir la regla 4.
  - Arena: dunas con su cara de luz y su cara de sombra, rizos del viento, piedras con su sombra.
  - Nieve: ventisqueros, huellas, sombras azules largas.
  - Hierba: rachas que la peinan, senderos, matas, cambio de color de lejos a cerca.
  - Empedrado o asfalto: losas, juntas, charcos después de la lluvia con su `reflejoRoto`.
- **A bordo de un barco** (Pirata, Botánico, Abismo, Revolución con su barco-lavadero). El agua lo rodea todo: es la protagonista. El primer plano es la cubierta y el marco es la jarcia, una vela o un toldo.
- **Siempre de noche** (Ciudad neón, hecha en P4b: `EscenaNeon.kt` es el ejemplo). `Rayo(luces)` no da sombras de noche, así que la regla 1 se cumple con las luces del lugar, con las piezas de `paisaje/Focos.kt`:
  - la escena arma la lista de focos encendidos en ese momento y con ella un `Alumbrado`; cada cosa le pide su color (`Alumbrado.en`) en vez de pedírselo a `luces.tono`;
  - la sombra de cada cosa la tumba el foco que más la alumbra (`rayoDeFoco`, dentro de una `capaSombra`); para una sola luz que manda, `rayoFijo`;
  - cada letrero o farol tiñe lo que tiene cerca y deja en sombra el otro lado;
  - el suelo mojado refleja las luces (`reflejoRoto`, `reflejo`);
  - la paleta corta son dos o tres colores de luz sobre un fondo azul violeta oscuro;
  - el «cielo» es la franja entre los edificios: bruma, cables, ventanas que se encienden y se apagan según la hora;
  - lo que el día le da a los demás temas (que el cuadro cambie con la hora) aquí lo dan los letreros, las ventanas y la lluvia. Hay que decidirlo y probarlo: tres horas distintas tienen que dar tres cuadros distintos.
- **Interiores con dos luces** (Refugio, Laboratorio, Espacio). No hay cielo ni tiempo.
  - Con la luz encendida, `Rayo(luces)` da una sombra fija, como de mediodía. No sirve: la luz tiene que venir de las lámparas que se ven en el cuadro, con `Foco`, `Alumbrado` y `rayoDeFoco` (`paisaje/Focos.kt`), como en Ciudad neón.
  - Con la luz tenue no da ninguna: la forma la dan las luces de guardia, las pantallas y lo que brille.
  - La parte de arriba es el techo o la pared del fondo: tuberías, paneles, cables, estantes. En Espacio, la ventana pequeña con la galaxia.
  - Lo que cambia con el día: qué está encendido, qué hay sobre la mesa, cómo va el cultivo.
- **Interiores con ventana o puerta** (un taller, un scriptorium, una oficina, un club). La luz del sol entra por el hueco y cae en el suelo y en la pared como una mancha que se mueve con la hora: es la sombra arrojada al revés. Por el hueco se ve el cielo de `cielo` y `nubes`.
- **Luz de fuego** (Prehistoria, un campamento, una posada de noche). De noche la hoguera es el sol de la escena: lo cercano a ella, cálido por ese lado y oscuro por el otro.
- **Mal tiempo frecuente** (niebla, lluvia, nieve). Con el cielo cubierto no hay sombras arrojadas. Para que no quede plano: suelo mojado más oscuro y con reflejos, luces encendidas antes, capas de bruma entre los planos (`estrato`, `tiempoQuieto`).
- **Ficción con colores propios** (Horror cósmico, Distopía, Orden, Tierras Antiguas). La paleta puede alejarse mucho de la de Helénico, pero conserva su forma: un color que domina, otro que lo acompaña, la luz de un matiz y la sombra del contrario.

**Si la biblioteca no alcanza** (una luz fija para interiores, un suelo mojado, una bóveda), la tanda que lo necesita lo agrega a `paisaje/`, con estas condiciones:
- **Agregar piezas, sí. Cambiar cómo se ve una que ya existe, no,** salvo que mejore todas las escenas hechas. En ese caso se vuelven a dibujar las hojas de las escenas ya aprobadas, se comparan con su referencia y se le enseñan al autor.
- **Lo que una segunda escena necesite de otra se sube a la biblioteca en vez de copiarlo:** `desconchon`, `hilera` y `casita`, `hojaDeHiguera`, `sombraCorta` siguen siendo privados de `EscenaHeleno.kt` (`Cosa` ya se subió en P4b, a `paisaje/Cosas.kt`). Al subir algo, el cuadro del que sale tiene que quedar idéntico: se comparan las dos imágenes punto por punto.
- Las tandas S **no tocan el motor** (`EscenaVida.kt`, `VistaEscena.kt`, `vidas/`).

**Los temas que faltan** (Gótico y Ciudad neón se hicieron en P4b). Dónde vive cada personaje y por dónde empezar. Son sugerencias: la tanda decide después de leer el calendario y el cuento.

| Tema | Personaje y lugar | Tipo | Marco posible | Superficie grande |
|---|---|---|---|---|
| Romano | Cayo, un campamento en el Danubio | Exterior | El borde de una tienda, un estandarte | El río |
| Egipto | Pay, Deir el-Medina | Exterior, sin agua | Un toldo de estera, una palmera | La ladera de piedra y arena |
| Prehistoria | Ama, un valle del suroeste de Europa | Exterior y fuego | La boca de la cueva | El valle con su río |
| Mesoamérica | K'an, una ciudad maya | Exterior | Una ceiba, un alero de palma | La plaza estucada |
| Ruta de la Seda | Nanai-vandak, entre Samarcanda y Chang'an | Exterior, sin agua | El borde de una tienda, la carga de un camello | El desierto o la estepa |
| Scriptorium | Hermano Odo, una abadía | Interior con ventana, o claustro | Un arco del claustro | El suelo de losas; el huerto |
| Templario | Hermano Bernat, un castillo del Levante | Exterior, sin agua | Una almena, un estandarte | El llano seco |
| Al-Ándalus | Zaynab, Córdoba | Patio | Un arco, un naranjo | El patio con su alberca |
| Mongol | Bat, la estepa | Exterior, sin agua | El borde de la yurta | La estepa |
| Renacimiento | Iacopo, un taller de Florencia | Interior con ventana | El marco de la ventana, un caballete | El suelo del taller; los tejados |
| Pirata | Tomás, un balandro en el Caribe | A bordo | La jarcia y la vela | El mar |
| Japón Edo | Ochiyo, una posta del camino real | Exterior | Un alero, una rama de arce o de cerezo | El camino |
| Revolución francesa | Marie-Jeanne, un barco-lavadero en el Sena | A bordo, en ciudad | La ropa tendida | El río |
| Napoleónico | Jean-Baptiste, la campaña de Rusia | Exterior; nieve al final | Un abedul, un fusil en pabellón | El llano; la nieve |
| Botánico | El naturalista, un bergantín | A bordo | La jarcia | El mar |
| Vapor victoriano | Albert, una línea del norte de Inglaterra | Exterior, con humo | Una marquesina, una señal | Las vías |
| Abismo | Nikos, un barco esponjero | A bordo | El pescante, un toldo | El mar |
| Art déco | Lou, Nueva York | Ciudad, de noche tiene mucho que decir | Una escalera de incendios, un rótulo | La calle o la azotea |
| Soviético | Liudmila, Moscú | Ciudad con nieve | El marco de una ventana, un abedul | El patio entre bloques |
| Roma | Giulia, restauradora en Roma | Ciudad | Un andamio, un pino | Los tejados |
| Espacio | La astronauta, la nave | Interior con dos luces | Cables y una compuerta | La pared de consolas |
| Ciudad verde | La semillera, una ciudad que el bosque recuperó | Exterior | Una rama, una viga rota con hiedra | La calle cubierta de verde |
| Distopía | El funcionario, la capital | Ciudad | Un altavoz, un cartel sin texto | La plaza |
| Refugio | La residente, un refugio bajo tierra | Interior con dos luces | Tuberías | El piso y los tanques |
| Horror cósmico | El archivista, un pueblo de pescadores en la niebla | Exterior con niebla | Una farola, una red tendida | El mar y el muelle |
| Orden | La escriba, una ciudadela en el páramo | Exterior | Una almena, un pendón | El páramo |
| Laboratorio | La doctora, un laboratorio subterráneo | Interior con dos luces | Conductos y lámparas | El piso y las mesas |
| Colonos | Ruth, un pueblo que se reconstruye | Exterior | Un porche, un árbol joven | El campo y el camino |
| Tierras Antiguas | Brenna, una posada al pie de un paso | Exterior de montaña | El alero de la posada, un pino | El camino y la ladera |

## 6. Lo que no cambia con el tema

- **Sin personas.** Se ve su mundo y lo que dejan.
- **La escena solo lee el `Momento`** y cuenta lo mismo que el cuento (decisión 18). Lo que el cuento mueve se decide en una función que una prueba pueda leer (como `EscenaHeleno.nave`).
- **Determinista:** el mismo día y la misma hora dan el mismo cuadro. Lo fijo del lugar usa un azar con semilla fija; lo del día, `m.azar(…)`.
- **Igual en claro y en oscuro.**
- **Lienzo de 380 × 285 unidades.**
- **Peso.** El grano va una sola vez. En Helénico hay cuatro capas de sombra en la parte quieta; una escena nueva no debería pasar de seis. Ninguna capa (`saveLayer`) en la parte viva.
- **Lo que cierra la app o no compila:**
  - un `Color` hecho con cuentas que pasen de 1 (para mezclar, `lerp`);
  - `Offset` en un `vararg`;
  - `Path.quadraticTo` (en el arnés no existe: las curvas van con `cubicTo`).
- **Despachos en cero:** la rama del tema en `escenaDe` y en `banderitaHistoria`, sin `else`.

## 7. Cómo se trabaja y cuándo está terminada

1. **Leer** el comentario del calendario del personaje y su cuento. Hacer la lista de lo que tiene que verse y cuándo.
2. **Componer.** Decidir desde dónde se mira, qué va en cada plano, qué enmarca y qué cruza. Escribir la paleta.
3. **Dibujar en el arnés** (la receta está en `docs/historia/informes/P4a.md`) el día normal a las 7:00, las 13:00 y las 21:00.
4. **Ponerla al lado de Helénico.** En la misma hoja, arriba Helénico vuelto a dibujar ese día y abajo la escena nueva. Mirarla también ampliada (densidad 2 o 3): el primer plano y la lejanía.
5. **Pasar la lista de la sección 8.** Lo que no pase se arregla antes de seguir. Lo normal son varias vueltas.
6. **Enseñársela al autor.**
   - La primera escena de un **tipo nuevo** (el primer interior, la primera siempre de noche, la primera a bordo, la primera sin agua) se le enseña a tres horas, junto a Helénico, **antes** de seguir con los demás temas de la tanda, y se espera su visto bueno.
   - Las demás se le enseñan al terminar la tanda, cada una junto a Helénico. El PR no se da por bueno hasta que las haya visto.
7. **Las demás hojas:** cada día especial, todos los objetos del día, todos los tiempos y estaciones, y los anchos de 300, 380 y 420.
8. **El barrido:** todos los días de 2026 a 2032 a varias horas, sin ninguna falla.
9. **Capturas en Actions** (`superficies = cuadro`, `temas = <los suyos>`, `solo = true`) y comparar una con el arnés.
10. **El informe de la tanda** lleva, por cada escena, la tabla de las diez reglas con cómo se cumple cada una y qué se adaptó.

## 8. Lista de comprobación

Se copia al informe de la tanda, una por escena. Todas tienen que quedar en «sí», o en «no aplica» con su reemplazo.

- [ ] A las 7:00 y a las 13:00 las sombras arrojadas tienen distinto largo y dirección.
- [ ] Cada cuerpo del primer plano tiene lado de luz y lado de sombra.
- [ ] Lo que enmarca da sombra sobre el muro, el suelo y las cosas.
- [ ] La mitad de arriba no está vacía, y cambia de un día a otro.
- [ ] Algo enmarca por arriba sin tapar el sol, la luna ni la banderita.
- [ ] La paleta está escrita al principio del archivo; no hay azul puro ni un color que salga una sola vez.
- [ ] Las sombras son azul violeta, no grises ni negras.
- [ ] La superficie grande cambia de lejos a cerca, y refleja o recibe lo que tiene encima.
- [ ] Las cosas del primer plano son grandes, se enciman y están a distintas profundidades.
- [ ] Ninguna cosa parece puesta para la foto: todas están donde las dejaría alguien, y tienen uso.
- [ ] Al menos dos cosas cruzan las franjas.
- [ ] Las superficies grandes tienen textura y el grano va al final de la parte quieta.
- [ ] A las 21:00 se leen las formas, y cada luz encendida alumbra algo.
- [ ] Ninguna orilla ni loma es una curva lisa, y entre dos materiales hay una franja de paso.
- [ ] Lo lejano tiene varias filas, luz y sombra, algo delante y algo detrás.
- [ ] No aparece ninguno de los errores de la sección 4.
- [ ] Con lluvia o niebla el cuadro no queda plano.
- [ ] Puesta al lado de la referencia de Helénico, no se ve más pobre en ningún punto.
- [ ] El objeto del día se ve a las tres horas, en todos sus dibujos.
- [ ] Lo que el cuento cambia ese día se ve, y una prueba lo vigila.
- [ ] El autor la vio junto a Helénico.
