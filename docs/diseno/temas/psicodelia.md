# Psicodelia (`psicodelia`): guía de diseño

**Versión 2 (16 nov 2026).** Reescrita con lo que decidió el autor al ver el tema terminado. La versión 1 (la del 15 nov 2026: Modak, Fredoka, el verde limón de la luz negra, la flor que vibra) queda en la historia de git y resumida en la ficha (`docs/diseno/PLAN_TEMAS.md`, lote 10g). El plan para llevar esta guía al código está en [`psicodelia_plan.md`](psicodelia_plan.md); la lámina de muestra, en [`muestras/psicodelia_caras.png`](muestras/psicodelia_caras.png) (la dibuja `herramientas/muestras/psicodelia_caras.py`).

Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota. Las fechas, los lugares y las frases se comprueban en [`fuentes_psicodelia.md`](fuentes_psicodelia.md): lo que esa tabla no sostiene no se usa.

## La idea, en una frase

**Una ciudad de los 70, sobria y gastada, que de noche se vuelve cartel.** El tema sigue en San Francisco a fines de los 60, con David Hale y su calendario de 1965 a 1969 (de las fotos de los 70 se toma el aspecto, no la época ni la ciudad). De día es la ciudad: la ventanilla del trolebús por la tarde, las casas victorianas grises, el papel de foto amarillento, el naranja quemado, el verde botella y el café. De noche es la psicodelia: tres tintas planas, como de serigrafía, sobre papel crema o sobre negro de terciopelo, con rayos que ondulan, bandas que fluyen y letras de bloque que llenan su forma. **Y con un consumo de cannabis en curso, la noche se satura**: las tintas suben de croma, las bandas se mueven y se salen del marco, el registro se corre. El texto y los datos se leen igual siempre.

Lo que el autor pidió, en sus palabras: «una base de los años 70 sobria, urbana, pero que a veces se haga súper psicodélico»; «una psicodelia más seria pero sí colorida, pero pensada»; «no imágenes estereotipadas de los 70».

## Lo que sí y lo que no

### Fuera de las dos caras: los tópicos gastados

| Fuera | Por qué | Qué entra en su lugar |
|---|---|---|
| Margaritas, flores de pétalos redondos, «flower power» dibujado | Es la imagen más gastada del tema, y la v1 la usaba de emblema | El **disco del día** como emblema (ver «Los motivos») |
| El símbolo de la paz como adorno, en todas partes | Se volvió calcomanía; en 1967 era de la marcha, no del cartel | Solo donde la historia lo pide (el 9 nov 1968 del calendario de David); nunca en la decoración |
| El arcoíris de siete colores | Tópico; además rompe la paleta corta | El **degradado de tintero partido** (comprobado: el periódico del barrio, `fuentes`, sección 2), pero entre **dos** de las tres tintas, no de siete colores |
| La lámpara de cera | Tópico, y su forma es marca (corrección 11) | Las **luces líquidas** del retroproyector (comprobado, sección 5), dibujadas como manchas planas de tinta |
| La letra burbuja (Modak) y la redonda blanda (Fredoka) | Lo que el autor llamó «colorido y bonito»; la burbuja no es la letra de los carteles | Letra de bloque que llena su forma (Chango) y un grotesco americano sobrio (Libre Franklin) |
| El «hippie» de disfraz: cinta en la frente, gafas redondas de colores, chaleco de flecos, teñido anudado, la camioneta pintada | Disfraz de fiesta temática | Gente que no se dibuja (regla de siempre), objetos con uso: plumillas, frascos de tinta, carpetas, pases de autobús |
| La hoja de cannabis, los hongos de caricatura, la espiral hipnótica, la cara sonriente, el ojo en la pirámide | Tópicos de camiseta; además, la hoja haría del consumo un adorno | Nada. El consumo no se dibuja: se nota en cómo se ve la noche (ver «La intensidad») |
| El verde limón de la luz negra y el magenta de neón juntos | Acercaba el oscuro a Ciudad neón | Negro de terciopelo con magenta, bermellón y ultramar, y el crema del papel |
| Las tildes que ondulan en los números («~17~») | Adorno de chiste | Números entre paréntesis, como en un horario: «(17)» |
| Las calles enteras de casas de colores | Son de los 70, no de 1967 (corrección 1) | Casi todas grises; una pintada, desteñida por la foto |

### Lo que se rescata de la v1

- **El día como disco** (recurso): «Disco 290 · sábado 17 de octubre · lado B, pista 4». Funciona en las dos caras y ata el tema a la música sin un solo tópico.
- **El mes como álbum:** «Álbum de octubre».
- **La FM de noche** (el 106.9, de medianoche a las 6 desde el 12 feb 1967; comprobado): es la voz natural de la cara de noche.
- **El periódico del barrio** (libreta y cartas), con su tinta de tintero partido, ahora a dos tintas.
- **El cartel de la semana** de David: el motivo de la cara de noche *es* su cartel.
- **Las luces líquidas**, sin lámpara de cera: la luz de la noche en los muebles.
- **Las 33 frases del día** (sección 10 de las fuentes), en inglés con su español al lado.
- **Los nombres por su calle o su frecuencia** (corrección 6) y **las casas casi todas grises** (corrección 1).

### Lo que se va

La flor que vibra (emblema), la paz como adorno, la lámpara de cera (trofeo, gato, Ideario), la cinta de la frente y las gafas del gato, las margaritas del mostrador y de la maceta, el arcoíris pintado en el vidrio, la calcomanía de flor de las postales, el tubo de luz negra como objeto de cada mueble, Modak, Fredoka y los colores de la v1 (`TrazosPsicodelia` entero).

### Las drogas: ya no son tabú, pero el tema no las vende

El autor quitó la regla de «sin drogas» (16 nov 2026): el personaje consume en el cuento de vez en cuando, y el tema y la app pueden aludir a ello. Lo que sigue valiendo, y por qué, está en «La voz». En la decoración, el consumo **no se dibuja nunca** (ni hojas, ni hongos, ni humo de caricatura): se nota solo como intensidad.

## Las dos caras

Hasta ahora, en todos los temas, el claro y el oscuro eran el mismo mundo con otra luz (ficha 10a). Psicodelia agrega un segundo eje: **la cara**, que decide la hora. Cada cara es un mundo propio y tiene su claro y su oscuro, que sí son el mismo mundo con otra luz. Son cuatro variantes.

| | Claro | Oscuro |
|---|---|---|
| **Ciudad** (de 5:00 a 21:00) | La calle por la tarde, desde la ventanilla del trolebús | El cuarto con la lámpara de pantalla encendida y el tocadiscos de maleta |
| **Psicodelia** (de 21:00 a 5:00) | El cartel impreso: tintas planas sobre papel crema | El cartel de luz negra: tintas sobre negro de terciopelo |

Las horas son las de `ModoHora` (`OSCURECE = 21`, `AMANECE = 5`), las mismas del cuadro de «Otras vidas» y del día lógico. Así, con el modo «hora», el tema pone la ciudad clara de día y la psicodelia oscura de noche, como pidió el autor.

### La ciudad

- **El mundo.** San Francisco a fines de los 60 vista como una foto de revista de los 70: el sol bajo de la tarde, la niebla que entra a ras, las victorianas grises con su mirador y su cornisa (alguna pintada, desteñida), los postes de madera, los cables del trolebús (al Haight se llegaba en trolebús o autobús: `fuentes`, sección 4), el coche aparcado en sombra, el pasamanos del autobús. Dentro: el cuarto con la lámpara de pantalla plisada, el tocadiscos de maleta, los discos de canto contra la pared.
- **El aspecto (de las tres fotos del autor):** color de película vieja (los negros se levantan hacia el café, las luces se van al crema, los rojos se apagan hacia el ladrillo), grano de película fino y un poco cálido, viñeta suave, luz con dirección (de la izquierda, de la tarde), sombras de color café y no grises. Sobria: pocos colores, nada brilla, nada se mueve sin motivo.
- **Lo que no es:** ni Nueva York (las escaleras de incendio y los letreros rojos verticales de la foto son de otra ciudad: se toma su color, no sus objetos), ni letreros de neón (Ciudad neón), ni la ciencia ficción de los 70 (Frente exterior).
- **Motivos:** la ventanilla del trolebús con la calle; la victoriana gris con su mirador; los cables y los postes; el tocadiscos de maleta bajo la lámpara; la etiqueta del disco del día; el pase de autobús (15 centavos hasta marzo de 1969, comprobado); la carpeta de dibujos.
- **El fondo vivo:** casi quieto. La mancha de sol que se corre muy despacio por la pared (un ciclo de 40 s) y el grano de película. De noche (ciudad·oscuro), el cono de la lámpara que respira un 3 %.

### La psicodelia

- **El mundo.** El cartel de la semana de David, el que se pega en los postes y se ve en la sala con las luces apagadas. Dos maneras de imprimirlo, que son el claro y el oscuro: en **papel crema** (una tinta azul negra que graba, más dos de color) y para **luz negra** (las tres tintas sobre negro de terciopelo, comprobado en `fuentes`, sección 5: «printed with fluorescent ink against backgrounds of velvet black»).
- **Las tres tintas, fijas:** magenta, bermellón y ultramar. Más el papel (crema) y la tinta de grabar (azul negra en el claro; en el oscuro, el crema hace de cuarta tinta). **Nada más.** Lo que haga falta entre medias sale de **sobreimprimir** dos tintas (magenta sobre ultramar da el violeta del terciario), como en una serigrafía de verdad. No hay degradados, salvo el del tintero partido entre dos tintas.
- **La disciplina** (de los dos carteles que mandó el autor, que son inspiración de estilo, no se copia ninguno ni sus figuras):
  - del primero, la **una sola tinta que graba**: rayos que ondulan, trama de líneas para los medios tonos, el marco de arco, mucho papel libre;
  - del segundo, las **tintas planas que fluyen**: bandas paralelas que se abren alrededor de una figura central, sin sombrear;
  - de los dos, la **simetría** y una **figura central** (aquí, siempre el disco del día, nunca una cara ni un cuerpo).
- **Motivos:** el disco del día como sol; los rayos que ondulan; las bandas que fluyen; la trama de líneas; el marco de arco del cartel; las luces líquidas como manchas planas; las letras de bloque. Sin flores, sin paz, sin ojos.
- **El fondo vivo:** las bandas respiran (amplitud de 2 dp, un ciclo de 12 s) y el disco gira a la vista una vuelta cada 3 minutos (33 ⅓ es demasiado: marea). Lo que hace la intensidad, abajo.

### Las cuatro paletas

Los colores de la interfaz (los papeles de Material). Contraste WCAG calculado con `python3 herramientas/muestras/psicodelia_caras.py --contraste`, que imprime la tabla entera; ninguna cifra baja de su mínimo (tinta ≥ 7; tenue, primario, secundario, terciario y error ≥ 4.5 sobre el fondo; sobre-primario ≥ 4.5 sobre el primario).

| Papel | Ciudad · claro (la tarde desde el trolebús) | Ciudad · oscuro (la lámpara de pantalla) | Psicodelia · claro (el cartel en papel) | Psicodelia · oscuro (el cartel de luz negra) |
|---|---|---|---|---|
| fondo | `#E9DFCC` papel de foto amarillento | `#1B1511` el cuarto a media luz | `#F2E8D3` papel de cartel | `#0F0C17` negro de terciopelo |
| superficie | `#F3ECDD` | `#241C16` | `#F9F3E6` | `#17131F` |
| variante | `#DCD0B9` | `#2F251D` | `#E7DBC2` | `#211B2B` |
| borde | `#A39478` | `#54463A` | `#3A3458` la línea de tinta | `#6E6488` |
| tinta | `#2A2119` café casi negro | `#EEE3D0` | `#1E1A3A` tinta azul negra | `#F3E9D6` el papel |
| tenue | `#5C4F41` | `#B9AA94` | `#4E4868` | `#B9AFC4` |
| primario | `#9C4219` naranja quemado del atardecer | `#E8975A` la pantalla de la lámpara | `#974164` magenta en reposo | `#DD80A4` magenta en reposo |
| sobre primario | `#FFF4E8` | `#2A1103` | `#FFF2F7` | `#2B0716` |
| contenedor | `#F1D3BD` | `#5A2E12` | `#F3D3DF` | `#5C1438` |
| sobre contenedor | `#3A1906` | `#FBDDC6` | `#3F0520` | `#FFD9EA` |
| secundario | `#3D5838` verde botella del estudio | `#A3BB93` verde de fieltro | `#404D99` ultramar | `#94A1DB` ultramar |
| contenedor secundario | `#D3DCC4` | `#2E3B29` | `#D6DAF0` | `#232C66` |
| terciario | `#7A5713` ocre mostaza | `#D9B865` luz de sodio | `#6A3E8E` violeta de sobreimpresión | `#B79BE0` violeta de sobreimpresión |
| error | `#A01E2C` rojo de letrero | `#FF8E84` | `#A8381E` bermellón | `#FF8E70` bermellón |

| Contraste sobre el fondo | Ciudad · claro | Ciudad · oscuro | Psicodelia · claro | Psicodelia · oscuro |
|---|---|---|---|---|
| tinta | 11.9 | 14.2 | 13.7 | 16.1 |
| tenue | 6.0 | 8.0 | 7.0 | 9.2 |
| primario | 5.0 | 7.8 | 5.3 | 7.1 |
| secundario | 6.0 | 8.7 | 6.3 | 7.7 |
| terciario | 5.0 | 9.5 | 6.4 | 8.1 |
| error | 5.8 | 8.1 | 5.3 | 8.6 |
| sobre primario (sobre el primario) | 6.0 | 7.6 | 5.9 | 6.7 |
| sobre contenedor (sobre el contenedor) | 11.2 | 8.9 | 12.1 | 10.2 |
| tinta sobre superficie | 13.4 | 13.2 | 15.0 | 15.2 |

En la psicodelia el error es el bermellón y el terciario el violeta de sobreimpresión, para que las tres tintas no se repartan cuatro papeles. El error siempre va con su ícono y su texto, nunca solo con el color.

### Los colores de dibujo

Van en dos objetos nuevos, `TrazosCiudad` y `TrazosTinta` (ver el plan), y reemplazan a `TrazosPsicodelia`.

- **La ciudad** (14 colores, todos apagados):
  - el cielo de la tarde, de la bruma `#D9CDB6` a la luz baja `#EDBE8C`, y el sol velado `#F6DDB4`;
  - las victorianas: grises cálidos `#A0968A`, `#958B7E`, `#A89C8C`; la pintada, un verde azul desteñido `#7E958E`; lo que mira al sol, aclarado hacia `#F4E6CC`; la sombra, hacia el café `#4A3528`;
  - los postes y los cables `#3E3128`; el pasamanos `#8C8270` con su brillo `#D8CDB5`; el coche `#7A3424`;
  - el cuarto de noche: la pared `#1F1712`, la lámpara encendida `#F2C996` con su luz `#F0A35A`, la madera `#6E4630`, la etiqueta del disco `#C8662A`.
  - **El grano y la película:** grano monocromo un poco cálido (fuerza de 0.22 de día y 0.30 de noche, como en la lámina), viñeta hacia el café `#3A2410`. Es lo que hace «foto de los 70»; sin él, la ciudad parece plana.
- **La psicodelia: las tintas y su intensidad.** Cada tinta tiene un valor de reposo (i = 0) y uno pleno (i = 1), a la misma luminosidad (en OKLCH, cambia solo el croma: del 62 % al 100 % del máximo que cabe en sRGB). Por eso el contraste casi no se mueve.

| Tinta | Papel · i = 0 | Papel · i = 0.5 | Papel · i = 1 | Negro · i = 0 | Negro · i = 0.5 | Negro · i = 1 |
|---|---|---|---|---|---|---|
| magenta | `#A94870` | `#B73570` | `#C50E70` | `#DC7BA1` | `#EC6DA2` | `#FB5BA2` |
| bermellón | `#C56851` | `#D55A3C` | `#E5481E` | `#E0907B` | `#EF876B` | `#FD7C5A` |
| ultramar | `#3F4C9E` | `#3A44B3` | `#3739C9` | `#8693D7` | `#8190EA` | `#7C8CFC` |
| tinta de grabar | `#1E1A3A` | igual | igual | (el crema `#F3E9D6`) | igual | igual |

Las tintas de dibujo nunca llevan texto. El bermellón sobre papel da 3.2 (vale para dibujo, que pide 3; no para letra).

## La intensidad

### De dónde sale

Una sola cifra, `i`, de 0 a 1, que el tema **solo lee** de lo que ya está anotado; no registra ni pregunta nada.

```
i(ahora) = mín(1, máx sobre c de nivel(c, ahora) / 10)

  donde c recorre Repo.consumos con:
    c.sustancia == "cannabis"
    "cannabis" no está en config.sustancias_ocultas
    millis(c.inicio) ≤ ahora ≤ Sustancias.finEfectivo(c, ahora)

  y nivel(c, ahora) = Sustancias.nivelEn(c, ahora) ?: 5.0
```

- `Sustancias.nivelEn` (en `data/Sustancias.kt`) es la misma interpolación que usa la exportación (`Exportar.nivelSustancia`, columna `cannabis_nivel`): une las lecturas de nivel (0 a 10) con rectas; si solo hay máximo, sube de 0 al máximo en el primer 35 % del episodio, se queda hasta el 65 % y baja a 0 al final; mientras el consumo sigue abierto, se queda en la última lectura.
- `Sustancias.finEfectivo`: el fin anotado; si sigue abierto y empezó hace menos de 18 horas, «ahora»; si quedó abierto de otro día, 4 horas después del inicio. Así un consumo que se olvidó cerrar no deja el tema encendido para siempre.
- **Sin nivel anotado** (`nivelEn` da null porque no hay lecturas ni máximo): cuenta como 5, i = 0.5. Se sabe que hay un consumo en curso, no cuánto.
- **Varios consumos a la vez:** el mayor (como `nivelSustancia`).
- **Se respeta `sustancias_ocultas`:** si el cannabis está oculto en las pantallas, el tema no reacciona. Ese interruptor existe para que nadie que mire el teléfono lo vea; un tema que se enciende lo delataría.
- **Solo cannabis**, como pidió el autor. El alcohol y la sustancia «otra» no cuentan (ver «Lo que debe decidir el autor»).
- **En la pantalla:** el cambio de `i` se anima en 4 s (y en 10 s cuando un consumo se cierra y cae a 0), para que no salte.

### Qué hace cada cosa con ella (cara psicodelia)

| | i = 0 (sin consumo) | i = 0.5 | i = 1 |
|---|---|---|---|
| Tintas de dibujo | de reposo (62 % del croma) | 81 % | 100 % |
| Primario, secundario, terciario (oscuro) | `#DD80A4`, `#94A1DB`, `#B79BE0` | `#EC73A5`, `#909EEB`, `#BE95F0` | `#FA63A5`, `#8C9CFB`, `#C58CFF` |
| Primario, secundario, terciario (claro) | `#974164`, `#404D99`, `#6A3E8E` | `#A43164`, `#3B47AD`, `#6F3399` | `#B11265`, `#383EC1`, `#7428A6` |
| Tintas en uso | dos (magenta y ultramar) + la del papel | las tres | las tres |
| Bandas del motivo | 7 por lado, amplitud 6 dp, trazo de 3 dp | 9 por lado, 17 dp, 5 dp | 10 por lado, 28 dp, 6 dp |
| Movimiento del fondo vivo | respira: 2 dp, ciclo de 12 s | fluye: las bandas avanzan una banda cada 20 s | fluye una banda cada 8 s; el disco gira una vuelta por minuto |
| Registro de las tintas | exacto | corrido 2 dp | corrido 4 dp (magenta a la derecha, bermellón a la izquierda) |
| Anillos alrededor del disco | no | 5, alternando tintas | 7 |
| El marco del cartel | todo dentro | todo dentro | las bandas se salen del marco |
| Títulos grandes de bloque (solo los decorativos) | rectos | doblados por una onda de 2 dp, con una segunda pasada en magenta corrida 1.5 dp | onda de 5 dp, segunda pasada corrida 3 dp |

Con valores intermedios se interpola (el número de bandas se redondea). La lámina dibuja las tres columnas.

**Con las animaciones apagadas** (`config.animaciones`) **o con ahorro de energía:** cambian los colores, el registro, los anillos y la onda de los títulos; nada se mueve.

### La regla de legibilidad

El texto y los datos **nunca** pierden contraste, aunque todo lo demás vibre:
1. `fondo`, `superficie`, `variante`, `borde`, `tinta`, `tenue`, los «sobre» y los contenedores **no cambian con `i`**. Solo cambian el primario, el secundario y el terciario, a luminosidad fija (la tabla de arriba: ninguno baja de 6.4 en el oscuro ni de 5.3 en el claro).
2. Las letras del texto y de las cifras **no se deforman nunca**: lo que se dobla son los títulos decorativos grandes (la presentación, el encabezado del día, el logro).
3. Ningún dibujo pasa por detrás de un texto sin una placa opaca (`superficie`) entre medias: los motivos van en las cabeceras, los márgenes y los huecos.
4. Las pantallas del **seguimiento privado** (impulsos, recaídas, libido, medicamentos) y las de registrar sustancias se dibujan siempre con i = 0 (ver «La voz»).

### De día, con un consumo en curso

**La cara la sigue decidiendo la hora: de día, la ciudad no se convierte en psicodelia.** Pero se tiñe, poco y con tope:
- una **fuga de luz** de película (el borde que se veló al cargar el carrete) en bermellón de reposo, desde la esquina de arriba a la derecha de los motivos, con una opacidad de `0.18 × i` (0.09 con i = 0.5; 0.18 con i = 1);
- el grano pasa de monocromo a **grano de color** (la fuerza sube de 0.22 a `0.22 + 0.1 × i`);
- en el motivo, los cables del trolebús y los bordes de las casas se **corren de registro** como en la cara de noche, pero solo 1 dp con i = 1.
- Los colores de la interfaz de la ciudad no cambian.

**Por qué:** las caras y la intensidad son dos ejes distintos, y así se mantienen previsibles: la hora dice qué mundo es; el consumo, cuánto se calienta. Si un consumo de mediodía trajera la noche, el tema dejaría de decir la hora, que es para lo que el autor lo pidió. La fuga de luz es lo que le pasa a una foto de verdad, así que la ciudad se altera sin dejar de ser ciudad, y la psicodelia queda como lo que el autor quiso: algo que llega «a veces».

## Cuándo sale cada cara

La cara la decide siempre la hora (`ModoHora.esDeNoche`); la claridad, el modo de la app (`oscuroSegun`). Las 8 combinaciones:

| `config.modo` | De 5:00 a 21:00 | De 21:00 a 5:00 |
|---|---|---|
| «claro» | ciudad · claro | psicodelia · claro |
| «oscuro» | ciudad · oscuro | psicodelia · oscuro |
| «hora» | ciudad · claro | psicodelia · oscuro |
| «sistema» (o un valor desconocido) | ciudad · claro u oscuro, como el teléfono | psicodelia · claro u oscuro, como el teléfono |

La intensidad se suma encima de cualquiera de las cuatro: entera en la psicodelia y como fuga de luz en la ciudad.

## Letra y forma

- **Ciudad:**
  - **Títulos:** Libre Franklin ExtraBold (800), con el espaciado apretado (−3.5 %) de los titulares de los 70. Es un Franklin Gothic libre (Impallari Type, OFL, sin nombre reservado): el grotesco americano de los periódicos y los letreros del siglo, sobrio y de la calle.
  - **Texto:** Libre Franklin Regular (400).
  - **Rótulos y cifras:** Libre Franklin SemiBold (600); los rótulos en mayúsculas con un 12 a 16 % de espaciado, como las placas de un autobús.
- **Psicodelia:**
  - **Títulos:** **Chango** (Fontstage, OFL, reserva su nombre: recortada, va renombrada «Bloque Vestigium», principio 6). Letra de bloque muy pesada, ancha, con los huecos casi cerrados: **llena su forma**, como pedía Wes Wilson de Roller (`fuentes`, sección 5), y tiene la punta un poco blanda sin ser burbuja. Solo en títulos y en lo decorativo, nunca en un párrafo ni en una cifra de datos.
  - **Texto, rótulos y cifras:** Libre Franklin, igual que la ciudad.
- **¿Una sola familia para las dos caras?** Para leer, sí: **Libre Franklin en todo el texto y en todas las cifras de las dos caras.** El texto no cambia de noche porque la legibilidad no debe depender de la hora, y porque así se nota que es la misma ciudad. Lo que cambia es la voz grande: el titular sobrio de día, el bloque de cartel de noche. Una sola familia para todo (por ejemplo, Franklin Black de noche) no alcanza: no llena su forma y no se distinguiría de un periódico.
- **Comprobado en la sesión (16 nov 2026):** las dos están en `google/fonts` con OFL (`ofl/librefranklin`, `ofl/chango`); ninguna está en `res/font/` (se revisaron los nombres internos de sus 135 archivos). Chango se separa de las de títulos pesados que ya hay (Russo One, Oswald, Abril Fatface, Londrina): es ancha y de huecos cerrados. Libre Franklin se separa de Chivo (Frente exterior), que es más geométrica, y de Overpass y Space Grotesk.
- **Comprobado también:** las dos traen ¿ ¡ ñ Ñ, las vocales con tilde y diéresis, « » ’ … · — – (se leyó su tabla de caracteres). **Por revisar al recortarlas:** la forma del 8 de Chango en tamaño chico (si se confunde, las cifras de los títulos salen de Libre Franklin Black, como se hizo con Modak).
- **Signos:** los dos traen · • — – « » ’ … ¿ ¡. No se usan ✿ ☮ ♪ como adorno de texto.
- **Esquinas:**
  - **Ciudad:** `Esquinas(11, Esquina.REDONDA)`, la ventanilla del trolebús y la pantalla de la tele de entonces. Libre en `UnicidadTemas` (las redondas de 11 y 12 están libres).
  - **Psicodelia:** `Esquinas(13, Esquina.ARCO)`, el marco de arco del cartel. Libre (los arcos usados son de 4, 5, 6, 8, 9, 11 y 12).
  - **Botones:** en la ciudad, la pastilla redondeada de la esquina (radio de 1.6 veces el de la forma `small`); en la psicodelia, el arco: arriba redondo, abajo casi recto. Llenos para el principal, tonales (contenedor secundario) para el secundario. Sin sombras en la psicodelia (es tinta plana); sombra de color café, muy corta, en la ciudad.

## La fecha, los números y la voz

### La fecha y los números

- **La fecha** (igual en las dos caras): «Disco 290 · sábado 17 de octubre», y el día de hoy con su lado y su pista: «· lado B, pista 4» (`Psicodelia.fecha`, sin cambios).
- **El mes:** «Álbum de octubre» (sin cambios).
- **Números:** entre paréntesis, «(17)», como los números de un horario o de una lista de canciones; los años y lo que pasa de 999, tal cual. Iguales en las dos caras, para que un dato no cambie de aspecto con la hora. Reemplaza a «~17~» (`Psicodelia.numero`; hay que cambiar su prueba). Libre en `UnicidadTemas` (comprobado con `grep` en `Estilo.numeral`).
- **Textos en inglés:** solo las frases del día y rótulos muy cortos (principio 7).

### La voz

Dos registros de la misma persona: **de día**, la del barrio y del oficio, tranquila y concreta (la niebla, el trolebús, el cartel que hay que entregar, la imprenta); **de noche**, la de la sala y la radio, un poco más suelta y sensorial (la luz que se mueve en la pared, el disco entero en el 106.9, el aire de la sala). Tutea. Sin jerga de época impostada («groovy», «man», «hippie» en boca del tema).

- **Ejemplos de día:** «Pon el disco del día», «La niebla se retira a mediodía», «El cartel sale el jueves», «Lado A».
- **Ejemplos de noche:** «Lado B», «Suena el 106.9», «Se encienden las luces de la pared», «Sale el cartel de la semana».

**Lo que cambia ahora que el consumo se puede nombrar, y la línea:**

1. **Se puede nombrar, con palabras llanas.** En el cuento y en los textos culturales del tema (frases, historia, etiquetas del calendario de David), la marihuana, el LSD o los hongos se llaman por su nombre, como cualquier otra cosa de la época. La jerga de entonces solo en boca de un personaje y entre comillas, nunca en la voz del tema.
2. **El tema muestra, no comenta.** La intensidad se ve; **ningún texto de la interfaz habla del consumo de la persona que usa la app** («estás…», «buen viaje», «sube el volumen»). Lo único que dice algo del consumo propio son las pantallas de Sustancias, como hasta ahora, con su voz neutra.
3. **Nunca invita ni premia.** Ningún aviso sugiere consumir ni lo pone a una hora; ningún logro, trofeo, sello, racha ni celebración se gana por consumir ni por no consumir; el tema no lleva cuenta de nada de eso.
4. **El seguimiento privado queda fuera del tema.** En las pantallas de impulsos, recaídas, libido y medicamentos, y al registrar una sustancia, el tema se dibuja con i = 0 y la voz es la neutra de la app. Una racha que se rompe nunca es «un mal viaje», «un bajón» ni «se rayó el disco». La racha del sello del día («N días sin que pare el disco») es la de los días sellados y se queda; no se mezcla con nada de sustancias.
5. **Sin frivolizar ni sermonear.** Ni chistes de consumo ni avisos de salud pegados al tema: la información de salud es de las pantallas de la app, no del tema.
6. **Las palabras que siguen prohibidas en la voz del tema** (no en el cuento): las de la jerga que celebra («colocado», «flipar», «pedo», «viajazo», «subidón»), «mal viaje» y todo lo que toque rachas, recaídas o impulsos (las palabras ya están en `JERGA_DE_DROGAS` de las pruebas; ver el plan).

**Las frases del día:** las 33 de `fuentes_psicodelia.md`, sección 10, sin cambios. La de Blake sobre las puertas de la percepción (n.º 1) se puede usar ahora sin el cuidado de antes; la del exceso («The road of excess…») **sigue fuera**, por el seguimiento de impulsos y recaídas.

## Los motivos de las figuras

- **El emblema: el disco del día.** De día, la etiqueta del disco, sobria (el número del día en Libre Franklin, el lado). De noche, el mismo disco hecho sol, con rayos que ondulan o bandas que fluyen alrededor. Un solo objeto en dos estados es la idea del tema.
- **Ciudad:** la ventanilla del trolebús con la calle; la victoriana gris con su mirador y su cornisa (y la de frontón en punta); los postes y los cables; el pasamanos; el tocadiscos de maleta y la lámpara; la carpeta de dibujos; el pase de autobús.
- **Psicodelia:** los rayos que ondulan con trama; las bandas que fluyen; los anillos alternos; el marco de arco; las luces líquidas como manchas planas; las letras de bloque.
- **Los trofeos** (9, reemplazan a los de la v1): el disco del día, el cartel de la semana, la plumilla y el frasco de tinta, el pincel de rotulista del viejo Sal, el pase de autobús, la entrada de un baile (2 dólares, comprobado), el retroproyector de las luces, la radio con el dial en el 106.9, la carpeta de dibujos. Cada uno con su versión de ciudad (dibujado con luz y grano) y de tinta (plano, a dos tintas).

## Los muebles

Con la regla de la Versión 2 de [`MUEBLES.md`](../MUEBLES.md) para todos los temas: solo el mueble y la pared de detrás, sin ventanas ni paisajes; objetos con uso; luz con dirección; noche de verdad. Los muebles de la v1 tenían ventana (Vida e Ideario), lámpara de cera, margaritas y arcoíris: se rehacen.

**El mismo mueble en las dos caras, pintado de dos maneras.** La forma, los objetos y su sitio son los mismos (un solo dibujo de geometría); lo que cambia es el pintor: la ciudad lo pinta con luz, sombra de color y grano; la psicodelia lo imprime en tintas planas con trama. Así no se dibujan seis muebles dos veces, y el autor ve que es su mismo cuarto de noche.

- **Biblioteca (Diario): la repisa de tablas sobre ladrillos** (se completa, no se rehace: es la de la v1 sin la lámpara de cera, las margaritas ni la calcomanía de arcoíris).
  - Tres tipos de entrada: el disco de larga duración en su funda impresa a dos tintas; la carpeta de cartón con carteles, atada con cinta; el cuaderno de bocetos de tapa negra.
  - Objetos: el tocadiscos de maleta con un disco a medias, la lata de café con plumillas y pinceles, dos frascos de tinta (uno destapado), la regla T apoyada contra la pared, el rollo de cinta de pintor, el cartel de la semana clavado con chinchetas (propio, a tres tintas), un pase de autobús metido en el canto de una tabla.
- **Ideario: la cajonera de planos del dibujante** (reemplaza al mostrador de la tienda de discos, que era recurso sin comprobar y tenía ventana). Cada tema es un cajón ancho y bajo, con su tarjeta de rótulo en el tirador de latón; dentro, los pliegos de las fichas, más cuanto más lleno, uno que asoma; el «sin tema» es la caja de cartón de los recortes. Encima: la mesa de dibujo inclinada con el cartel a medias, el retroproyector de Ellen apagado, los frascos de tinta.
- **Tablero (Vida y Diario): el corcho sobre la mesa de dibujo**, con las notas clavadas, los bocetos, una entrada de baile, un recorte del periódico del barrio.
- **Vida: la mesa de dibujo contra la pared**, con su lámpara de brazo, sin la ventana de guillotina.
- **La luz:**
  - **Ciudad · claro:** la luz de la tarde entra de arriba a la izquierda desde fuera del cuadro; mancha clara en la pared, sombras café.
  - **Ciudad · oscuro:** la lámpara de pantalla, sobre la repisa o la mesa, es la luz: halo en la pared, filo cálido en los cantos que la miran, lo lejano se pierde.
  - **Psicodelia · claro:** el mueble impreso: tintas planas, trama de líneas en las sombras, el papel en las luces. La luz sigue viniendo de la izquierda (la trama va a la derecha de cada volumen).
  - **Psicodelia · oscuro:** la luz es **la proyección de las luces líquidas en la pared** (el retroproyector queda fuera del cuadro): tres manchas planas de tinta, magenta, bermellón y ultramar, que encienden los cantos de lo que tienen delante. Es luz del tema dibujada en la pared, no una ventana. Con la intensidad, las manchas se mueven más y suben de croma, y la de bermellón aparece desde i = 0.5.

## El fondo, el ícono, el sello y los widgets

- **El papel de fondo** (`papel_psicodelia`): el papel de foto de la ciudad. La psicodelia tiene el suyo, el papel de cartel (o el negro de terciopelo), con la trama apenas visible.
- **El ícono** de la app: fijo, la etiqueta del disco del día en sobrio (ciudad). Cambiar el ícono dos veces al día reordena el lanzador y no se hace.
- **El sello del día** (`huella_psicodelia`): fijo, el de la ciudad. Lo que se archiva no depende de la hora ni de un consumo.
- **Los widgets** (siempre oscuros): cara por hora, con la tarea de cada 15 minutos (ciudad · oscuro de día, psicodelia · oscuro de noche). Sin intensidad.
- **Los fondos del teléfono:** las dos caras, para que se elija; no cambian solos.

## El gato

- **Ciudad:** el atigrado naranja de la v1, pintado como en una foto de los 70: sin gafas ni cinta. Con las patas manchadas de tinta (las manos manchadas de los operadores de luces, comprobado en `fuentes`, sección 5).
- **Psicodelia:** el mismo gato impreso a dos tintas, con las rayas en trama; con intensidad, las rayas ondulan (en la banda del gato, con la misma tabla).
- **Acciones especiales** (reemplazan a la lámpara de cera y a las burbujas): persigue las manchas de luz líquida que se mueven por la pared · se sienta sobre el cartel recién impreso y deja sus huellas de tinta.

## Sonido

- **Cambia con la cara, no con la intensidad.** Un sonido lo oye quien esté cerca; un consumo no debe oírse.
- **Ciudad:** el órgano de transistores bajo y suave, el pandero, el eco de cinta corto, el golpe de la aguja al bajar al disco, el clic de la perilla de la radio.
- **Psicodelia:** el sitar en zumbido, la guitarra con distorsión (para los logros), el eco de cinta largo que se repite.
- Los sonidos de los avisos van por canal de notificación (Android fija el sonido del canal al crearlo): se quedan los de la ciudad. Todos los instrumentos ya existían en 1967 (`fuentes`, sección 8); nombres genéricos; música propia.

## Se distingue de

- **Frente exterior:** también son los 70, pero allí es ciencia ficción de consola (hueso, ámbar, radar, Chivo y Michroma); aquí es la calle de verdad vista en foto (café, naranja quemado, verde botella, grano) y el cartel de concierto.
- **Ciudad neón:** letreros que brillan, ciencia ficción, solo de noche; aquí, de noche, tinta plana sobre negro, sin un solo letrero ni rejilla, y sin verde de sodio ni cian.
- **Art nouveau:** la curva tranquila y el oro; aquí, la curva de las bandas es simétrica y vibra, las tintas son planas y no hay planta ni oro.
- **Art déco:** rayos rectos y laca negra con oro; aquí, rayos que ondulan, tres tintas y papel.
- **Refugio:** también cambia de concepto, pero con el modo (el optimismo de los 50 de día, el fósforo de noche); aquí el concepto lo cambia la hora y cada cara tiene su claro y su oscuro.
- **Soviético y Distopía:** cartel de Estado, rojo y negro, ministerio; aquí, cartel de concierto, tres tintas, nadie da órdenes.
- **Roma (Urbe):** la ciudad de hoy en capas de piedra y ocre; aquí, la ciudad de madera y foto vieja de hace sesenta años.

## Lo que debe decidir el autor

1. **¿Solo cannabis?** La intensidad lee solo el cannabis, como pediste. ¿Debe contar también la sustancia «otra» (por ejemplo, hongos anotados ahí), y con qué peso? El alcohol queda fuera salvo que digas lo contrario.
2. **`sustancias_ocultas`:** si ocultas el cannabis en las pantallas, el tema no reacciona. ¿Te sirve así, o prefieres que reaccione igual?
3. **De día, con un consumo:** la propuesta es que la ciudad solo se tiña (fuga de luz, grano de color) y la psicodelia no entre antes de las 21:00. ¿O prefieres que, desde cierto nivel (por ejemplo 7), la psicodelia entre también de día?
4. **Los títulos de la psicodelia en Chango:** ¿te convence la letra de bloque de la lámina, o prefieres algo más alto y estrecho, más cerca de Roller (habría que dibujar los títulos a mano, sin fuente)?
5. **El cuento:** ¿qué sustancias y cuántas veces por temporada (la propuesta está en el plan: marihuana algunas noches, LSD una o dos veces por temporada desde 1966, hongos una sola vez)? ¿Y el alcohol y el tabaco, que hoy tampoco aparecen: vuelven como cosa de la época?
6. **Los números «(17)»:** ¿te gustan entre paréntesis, o los quieres sin adorno?
