# Psicodelia (`psicodelia`): guía de diseño

**Versión 2 (16 nov 2026), al día con lo programado (17 nov 2026).** Reescrita con lo que decidió el autor al ver el tema terminado; después de ver la lámina, el autor contestó las preguntas de la guía (al final, «Decisiones del autor después de ver la lámina») y las secciones de abajo ya dicen lo que se programó. Lo técnico (los nombres del código, la caché, cómo se lee la cara) está en [`psicodelia_base.md`](psicodelia_base.md). La versión 1 (la del 15 nov 2026: Modak, Fredoka, el verde limón de la luz negra, la flor que vibra) queda en la historia de git y resumida en la ficha (`docs/diseno/PLAN_TEMAS.md`, lote 10g). El plan para llevar esta guía al código está en [`psicodelia_plan.md`](psicodelia_plan.md); la lámina de muestra, en [`muestras/psicodelia_caras.png`](muestras/psicodelia_caras.png) (la dibuja `herramientas/muestras/psicodelia_caras.py`).

Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota. Las fechas, los lugares y las frases se comprueban en [`fuentes_psicodelia.md`](fuentes_psicodelia.md): lo que esa tabla no sostiene no se usa.

## La idea, en una frase

**Una ciudad de los 70, sobria y gastada, que de noche se vuelve cartel.** El tema sigue en San Francisco a fines de los 60, con David Hale y su calendario de 1965 a 1969 (de las fotos de los 70 se toma el aspecto, no la época ni la ciudad). De día es la ciudad: la ventanilla del trolebús por la tarde, las casas victorianas grises, el papel de foto amarillento, el naranja quemado, el verde botella y el café. De noche es la psicodelia: tres tintas planas, como de serigrafía, sobre papel crema o sobre negro de terciopelo, con rayos que ondulan, bandas que fluyen y letras de bloque que llenan su forma. **Y con un consumo en curso, entra el cartel también de día y se satura**: las tintas suben de croma, las bandas se mueven y se salen del marco, el registro se corre. El texto y los datos se leen igual siempre.

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
| La hoja de cannabis, los hongos de caricatura, la espiral hipnótica, la cara sonriente, el ojo en la pirámide | Tópicos de camiseta; además, la hoja haría del consumo un adorno | Nada. El consumo no se dibuja: se nota en cómo se ve el cartel (ver «La intensidad») |
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

La flor que vibra (emblema), la paz como adorno, la lámpara de cera (trofeo, gato, Ideario), la cinta de la frente y las gafas del gato, las margaritas del mostrador y de la maceta, el arcoíris pintado en el vidrio, la calcomanía de flor de las postales, el tubo de luz negra como objeto de cada mueble, Modak, Fredoka y los colores de la v1 (`TrazosPsicodelia` entero). **Todo eso ya se retiró del código** (cierre de la versión 2, 17 nov 2026): el archivo, las letras y sus recursos se borraron.

### Las drogas: ya no son tabú, pero el tema no las vende

El autor quitó la regla de «sin drogas» (16 nov 2026): el personaje consume en el cuento de vez en cuando, y el tema y la app pueden aludir a ello. Lo que sigue valiendo, y por qué, está en «La voz». En la decoración, el consumo **no se dibuja nunca** (ni hojas, ni hongos, ni humo de caricatura): se nota solo como intensidad.

## Las dos caras

Hasta ahora, en todos los temas, el claro y el oscuro eran el mismo mundo con otra luz (ficha 10a). Psicodelia agrega un segundo eje: **la cara**, que deciden la hora y los consumos en curso. Cada cara es un mundo propio y tiene su claro y su oscuro, que sí son el mismo mundo con otra luz. Son cuatro variantes.

| | Claro | Oscuro |
|---|---|---|
| **Ciudad** (de 5:00 a 21:00, sin consumo en curso) | La calle por la tarde, desde la ventanilla del trolebús | El cuarto con la lámpara de pantalla encendida y el tocadiscos de maleta |
| **Psicodelia** (de 21:00 a 5:00, o con un consumo en curso a cualquier hora) | El cartel impreso: tintas planas sobre papel crema | El cartel de luz negra: tintas sobre negro de terciopelo |

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
- **Más dibujo de cartel** (decisión 1 del autor): además de las bandas y el disco, la cara lleva figuras de cartel en todo lo que dibuja (la corona de rayos que ondulan del disco hecho sol, el marco de arco, la trama de grabado fino, las letras de bloque que llenan su forma, las luces líquidas como manchas planas, las marcas de registro de la prueba de imprenta); cada mueble y cada pieza se imprime, no solo se colorea.

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

Una sola cifra, `i`, de 0 a 1, que el tema **solo lee** de lo que ya está anotado; no registra ni pregunta nada (`Intensidad.de`, en `ui/CaraTema.kt`; pruebas en `IntensidadPrueba`).

```
i(ahora) = deNivel(máx sobre c de nivel(c, ahora))      (0 si no hay ninguno)

  donde c recorre Repo.consumos con:
    la sustancia de c tiene nivel (Catalogo.Sustancia.conNivel: hoy alcohol, cannabis y «otra»)
    millis(c.inicio) ≤ ahora ≤ Sustancias.finEfectivo(c, ahora)

  nivel(c, ahora) = Sustancias.nivelEn(c, ahora, ahora) ?: 3

  deNivel(n) = 0                       si n ≤ 0
             = 0.5 · n / 3             si 0 < n ≤ 3
             = 0.5 + 0.5 · (n − 3) / 5 si 3 < n < 8
             = 1                       si n ≥ 8
```

- **Cualquier sustancia con nivel** (decisión 4 del autor): el alcohol, el cannabis y la «otra». El tabaco y la cafeína no tienen nivel y no cuentan.
- **La escala, por tramos:** nivel 3 da la intensidad media (0.5) y nivel 8 la máxima (1); entre medias, en línea recta; por encima de 8, sigue en 1.
- `Sustancias.nivelEn` (en `data/Sustancias.kt`) es la misma interpolación que usa la exportación (`Exportar.nivelSustancia`): une las lecturas de nivel (0 a 10) con rectas; si solo hay máximo, sube de 0 al máximo en el primer 35 % del episodio, se queda hasta el 65 % y baja a 0 al final; mientras el consumo sigue abierto, se queda en la última lectura.
- `Sustancias.finEfectivo`: el fin anotado; si sigue abierto y empezó hace menos de 18 horas, «ahora»; si quedó abierto de otro día, 4 horas después del inicio. Así un consumo que se olvidó cerrar no deja el tema encendido para siempre.
- **Sin nivel anotado:** cuenta como nivel 3 (i = 0.5). Se sabe que hay un consumo en curso, no cuánto.
- **Varios consumos a la vez:** el mayor (como `nivelSustancia`).
- **Reacciona aunque la sustancia esté en `sustancias_ocultas`** (decisión 5 del autor).
- **En la pantalla:** el cambio de `i` se anima en 4 s (y en 10 s cuando un consumo se cierra y cae a 0), para que no salte (`MedidorIntensidad`). El medidor vuelve a mirar al cambiar los consumos, al volver a la app y, solo con un consumo abierto, cada 2 minutos.
- **No se guarda ni se exporta:** no sube `SCHEMA_V` ni cambia `Exportar`, `Diccionario` ni `vestigium.R`.

### Qué hace cada cosa con ella (cara psicodelia)

| | i = 0 (sin consumo) | i = 0.5 | i = 1 |
|---|---|---|---|
| Tintas de dibujo | de reposo (62 % del croma) | 81 % | 100 % |
| Primario, secundario, terciario (oscuro) | `#DD80A4`, `#94A1DB`, `#B79BE0` | `#EC73A5`, `#909EEB`, `#BE95F0` | `#FA63A5`, `#8C9CFB`, `#C58CFF` |
| Primario, secundario, terciario (claro) | `#974164`, `#404D99`, `#6A3E8E` | `#A43164`, `#3B47AD`, `#6F3399` | `#B11265`, `#383EC1`, `#7428A6` |
| Tintas en uso | dos (magenta y ultramar) + la del papel | las tres | las tres |
| Bandas del motivo | 7 por lado, amplitud 6 dp, trazo de 3 dp | 9 por lado, 17 dp, 5 dp | 11 por lado, 32 dp, 6.5 dp |
| Movimiento del fondo vivo | respira: 2 dp, ciclo de 12 s; el disco, una vuelta cada 3 min | fluye: las bandas avanzan una banda cada 20 s; el disco, una vuelta cada 2 min | fluye una banda cada 7 s; el disco gira una vuelta cada 50 s |
| Registro de las tintas | exacto | corrido 2 dp | corrido 5 dp (magenta a la derecha, bermellón a la izquierda) |
| Anillos alrededor del disco | no | 5, alternando tintas | 8 |
| El marco del cartel | todo dentro | todo dentro | desde i = 0.9, las bandas se salen del marco |
| Títulos grandes de bloque (solo los decorativos) | rectos | doblados por una onda de 2 dp, con una segunda pasada en magenta corrida 1.5 dp | onda de 6 dp, segunda pasada corrida 3.5 dp |

Con valores intermedios se interpola (el número de bandas se redondea). La lámina dibuja i = 0, 0.5 y 1 con los valores de antes; los de i = 1 se subieron un poco (decisión 2 del autor: el máximo, un poco más fuerte que en la lámina) y son los de esta tabla (`TrazosTinta.medidas`). El bermellón entra desde i = 0.25 y está entero desde i = 0.5.

**Con las animaciones apagadas** (`config.animaciones`) **o con ahorro de energía:** cambian los colores, el registro, los anillos y la onda de los títulos; nada se mueve.

### La regla de legibilidad

El texto y los datos **nunca** pierden contraste, aunque todo lo demás vibre:
1. `fondo`, `superficie`, `variante`, `borde`, `tinta`, `tenue`, los «sobre» y los contenedores **no cambian con `i`**. Solo cambian el primario, el secundario y el terciario, a luminosidad fija (la tabla de arriba: ninguno baja de 6.4 en el oscuro ni de 5.3 en el claro).
2. Las letras del texto y de las cifras **no se deforman nunca**: lo que se dobla son los títulos decorativos grandes (la presentación, el encabezado del día, el logro).
3. Ningún dibujo pasa por detrás de un texto sin una placa opaca (`superficie`) entre medias: los motivos van en las cabeceras, los márgenes y los huecos.
4. Las pantallas del **seguimiento privado** (impulsos, recaídas, libido, medicamentos) y las de Sustancias se dibujan siempre con i = 0 y con la cara de la hora (ver «La voz»). En el código, `SinIntensidad` envuelve `PantallaRecaidas`, `PantallaSustancias` y, en la hoja rápida, la captura de lo privado y la de un consumo; mientras una de ellas está abierta, toda la app lee i = 0 y la cara de la hora.

### De día, con un consumo en curso

**Entra la psicodelia** (decisión 3 del autor): la ciudad no se tiñe, cambia la cara. Con el modo «hora» o «claro», de día sale el cartel en papel crema; con «oscuro», el cartel de luz negra. Cuando el consumo se cierra (o el medidor lo da por terminado con `finEfectivo`), la cara vuelve a la de la hora en cuanto la intensidad a la que va (`intensidadObjetivo`) llega a 0, y los colores bajan en 10 s.

Lo que **no** cambia con un consumo de día: el sonido (sigue la cara de la hora: un consumo no debe oírse), los avisos (eligen su voz por la hora), los widgets (por la hora, sin intensidad), el ícono y el sello (fijos, de la ciudad) y las pantallas privadas y de Sustancias (siempre con la cara de la hora y i = 0).

## Cuándo sale cada cara

La cara la deciden la hora (`ModoHora.esDeNoche`, de 21:00 a 5:00) y los consumos en curso (`caraSegun(tema, noche, intensidad)`: INTENSA si es de noche **o** si la intensidad es mayor que 0); la claridad, el modo de la app (`oscuroSegun`). Sin consumo, las 8 combinaciones:

| `config.modo` | De 5:00 a 21:00 | De 21:00 a 5:00 |
|---|---|---|
| «claro» | ciudad · claro | psicodelia · claro |
| «oscuro» | ciudad · oscuro | psicodelia · oscuro |
| «hora» | ciudad · claro | psicodelia · oscuro |
| «sistema» (o un valor desconocido) | ciudad · claro u oscuro, como el teléfono | psicodelia · claro u oscuro, como el teléfono |

Con un consumo en curso, la columna de 5:00 a 21:00 pasa a la psicodelia con la misma claridad (con «hora», psicodelia · claro). La intensidad se suma encima, solo en la psicodelia.

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
- **Comprobado también:** las dos traen ¿ ¡ ñ Ñ, las vocales con tilde y diéresis, « » ’ … · — – (se leyó su tabla de caracteres). **Revisado al recortarlas:** el 8 de Chango a 12, 14 y 18 sp no se confunde con la S; las cifras de los títulos se quedan en Chango.
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

Con la regla de la Versión 2 de [`MUEBLES.md`](../MUEBLES.md) para todos los temas: solo el mueble y la pared de detrás, sin ventanas ni paisajes; objetos con uso; luz con dirección; noche de verdad. Los muebles de la v1 tenían ventana (Vida e Ideario), lámpara de cera, margaritas y arcoíris: se rehicieron todos.

**El mismo mueble en las dos caras, pintado de dos maneras.** La forma, los objetos y su sitio son los mismos (un solo dibujo de geometría); lo que cambia es el pintor: la ciudad lo pinta con luz, sombra de color y grano; la psicodelia lo imprime en tintas planas con trama. El autor ve que es su mismo cuarto de noche.

- **Biblioteca (Diario): la repisa de tablas sobre ladrillos** (`MuebleEstantePsicodelia.kt`).
  - Tres tipos de entrada: el disco de larga duración en su funda impresa a dos tintas; la carpeta de cartón con carteles, atada con cinta; el cuaderno de bocetos de tapa negra.
  - Objetos: el tocadiscos de maleta con un disco a medias, la lámpara de pantalla plisada, la lata de café con plumillas y pinceles, dos frascos de tinta (uno destapado), la regla T apoyada contra la pared, el rollo de cinta de pintor, el cartel de la semana clavado con chinchetas, un pase de autobús metido en el canto de una tabla.
  - Lo de alrededor (`BibliotecaPsicodelia.kt`): la tapa es el disco del día como un álbum (la portada es una foto de la calle de victorianas en la ciudad, o el cartel del disco hecho sol en la tinta); la página, la funda interior con la etiqueta del disco; la libreta, el periódico del barrio, «La Niebla», a dos tintas de tintero partido, sobre la mesa de la cocina contra la pared con el cartel de la semana (sin ventana); las cartas, postales de 1967 atadas con bramante y selladas con una etiqueta de disco; el buscador, el dial de la radio que se va al 106.9; las pestañas, los separadores de una caja de discos; las obras, apoyadas en la tabla sobre ladrillos bajo los carteles de David; el recuerdo, un volante de mimeógrafo (en la tinta, a dos tintas).
- **Ideario: la cajonera de planos del dibujante** (`IdeasPsicodelia.kt`; reemplaza al mostrador de la tienda de discos). Cada tema es un cajón ancho y bajo, con su tarjeta en el portatarjetas de latón; dentro, los pliegos; el «sin tema» es la caja de cartón de los recortes. Encima: la mesa de dibujo inclinada con el cartel a medias, la lámpara de brazo, el retroproyector de las luces líquidas, los frascos de tinta. **Por dentro**, el cajón abierto visto desde arriba (los pliegos de otras semanas, un cartel impreso, la regla de acero, las paredes de roble); **las fichas son los pliegos**: papel de dibujo con cinta de pintor y su cajetín a lápiz en la ciudad, prueba de imprenta con sus marcas de registro y la tira de las tintas en la tinta. Las marcas de postura van con el disco del día (hecho, convence con su corona de rayos, piensa, duda a lápiz, desacuerdo partido) y la de fijada es una chincheta redonda. El archivo se llama «cajón de planos» y cada ficha, «pliego».
- **Vida: la mesa de dibujo contra la pared, con su lámpara de brazo** (`VidaPsicodelia.kt`), sin la ventana de guillotina: Social (el pliego de estraza clavado sobre la mesa), Mapas (la mesa de dibujo de frente), Otras vidas (la caja de tipos de imprenta).
- **Tablero (Vida y Diario): el corcho sobre la mesa de dibujo**, con las notas clavadas, los bocetos, una entrada de baile, un recorte del periódico del barrio.
- **La luz:**
  - **Ciudad · claro:** la luz de la tarde entra de arriba a la izquierda desde fuera del cuadro; mancha clara en la pared, sombras café.
  - **Ciudad · oscuro:** la lámpara de pantalla (en la biblioteca) o la de brazo (en el Ideario y en Vida) es la luz: halo en la pared, filo cálido en los cantos que la miran, lo lejano se pierde.
  - **Psicodelia · claro:** el mueble impreso: tintas planas, trama de líneas en las sombras, el papel en las luces. La luz sigue viniendo de la izquierda (la trama va a la derecha de cada volumen).
  - **Psicodelia · oscuro:** la luz es **la proyección de las luces líquidas en la pared** (el retroproyector queda fuera del cuadro o apagado): manchas planas de tinta, magenta y ultramar, que encienden los cantos de lo que tienen delante. Con la intensidad, las manchas crecen y suben de croma, y la de bermellón aparece desde i = 0.5.

## El fondo, el ícono, el sello y los widgets

- **El papel de fondo** (`papel_psicodelia`): el papel de foto de la ciudad. La psicodelia tiene el suyo, el papel de cartel (o el negro de terciopelo), con la trama apenas visible.
- **El ícono** de la app: fijo, la etiqueta del disco del día en sobrio (ciudad). Cambiar el ícono dos veces al día reordena el lanzador y no se hace.
- **El sello del día** (`huella_psicodelia`): fijo, el de la ciudad. Lo que se archiva no depende de la hora ni de un consumo.
- **Los widgets** (siempre oscuros): cara por hora, con la tarea de cada 15 minutos (ciudad · oscuro de día, psicodelia · oscuro de noche). Sin intensidad.
- **Los fondos del teléfono:** las dos caras («la ciudad» y «el cartel»), para que se elija; la elegida se guarda en las preferencias de los fondos y no cambia sola con la hora ni con un consumo.
- **La pantalla de bloqueo, la presentación y el reloj de enfoque** también tienen sus dos caras: la puerta de la sala con los cables y los postes o con la corona de rayos; la copia de foto o el cartel de arco; las pistas del disco como rayas del dial o como rayos que ondulan.

## El gato

- **Ciudad:** el atigrado naranja de la v1, pintado como en una foto de los 70: sin gafas ni cinta. Con las patas manchadas de tinta (las manos manchadas de los operadores de luces, comprobado en `fuentes`, sección 5). Banda `psicodelia`.
- **Psicodelia:** el mismo gato impreso a dos tintas, con las rayas en trama; con la intensidad, la banda cambia de escalón (`psicodelia_tinta`, `_tinta_media` desde i = 0.25 y `_tinta_plena` desde i = 0.75): más croma, la tercera tinta, el registro corrido y las rayas que ondulan (`Gato.clave`, pruebas en `GatoClavePrueba`).
- **Acciones especiales** (reemplazan a la lámpara de cera y a las burbujas): persigue las manchas de luz líquida que se mueven por la pared · se sienta sobre el cartel recién impreso y deja sus huellas de tinta.
- En las pantallas privadas y de Sustancias, la banda va con i = 0 y la cara de la hora.

## Sonido

- **Sigue la cara de la hora, nunca la intensidad** (`Sentidos.caraDelSonido`): con un consumo de día, la pantalla pasa al cartel, pero se sigue oyendo la ciudad. Un sonido lo oye quien esté cerca; un consumo no debe oírse.
- **Ciudad** (`s_psicodelia_<evento>`): el órgano de transistores bajo y suave, el pandero, el eco de cinta corto, el golpe de la aguja al bajar al disco, el clic de la perilla de la radio.
- **Psicodelia** (`s_psicodelia_tinta_<evento>`): el sitar en zumbido, la guitarra con distorsión (para los logros), el eco de cinta largo que se repite, el rasero de la serigrafía, el pincel de rotulista.
- Los sonidos de los avisos van por canal de notificación (Android fija el sonido del canal al crearlo): se quedan los de la ciudad, como los tonos del teléfono. Todos los instrumentos ya existían en 1967 (`fuentes`, sección 8); nombres genéricos; música propia.

## Se distingue de

- **Frente exterior:** también son los 70, pero allí es ciencia ficción de consola (hueso, ámbar, radar, Chivo y Michroma); aquí es la calle de verdad vista en foto (café, naranja quemado, verde botella, grano) y el cartel de concierto.
- **Ciudad neón:** letreros que brillan, ciencia ficción, solo de noche; aquí, de noche, tinta plana sobre negro, sin un solo letrero ni rejilla, y sin verde de sodio ni cian.
- **Art nouveau:** la curva tranquila y el oro; aquí, la curva de las bandas es simétrica y vibra, las tintas son planas y no hay planta ni oro.
- **Art déco:** rayos rectos y laca negra con oro; aquí, rayos que ondulan, tres tintas y papel.
- **Refugio:** también cambia de concepto, pero con el modo (el optimismo de los 50 de día, el fósforo de noche); aquí el concepto lo cambia la hora y cada cara tiene su claro y su oscuro.
- **Soviético y Distopía:** cartel de Estado, rojo y negro, ministerio; aquí, cartel de concierto, tres tintas, nadie da órdenes.
- **Roma (Urbe):** la ciudad de hoy en capas de piedra y ocre; aquí, la ciudad de madera y foto vieja de hace sesenta años.

## Lo que decidió el autor

Las seis preguntas que esta guía dejó abiertas se contestaron el 16 nov 2026 (abajo, en «Decisiones del autor después de ver la lámina») y ya están programadas:

1. **¿Solo cannabis?** No: cuenta cualquier sustancia con nivel (alcohol, cannabis, «otra»), con la escala por tramos.
2. **`sustancias_ocultas`:** el tema reacciona igual.
3. **De día, con un consumo:** entra la psicodelia (en claro con el modo «hora»), no una ciudad teñida.
4. **Los títulos en Chango:** se quedan (la dirección «va bien»).
5. **El cuento:** la marihuana una vez por semana; el LSD, más o menos una vez al mes; los hongos, unas pocas veces al año.
6. **Los números «(17)»:** se quedan.

## Decisiones del autor después de ver la lámina (16 nov 2026)

Mandan sobre lo que diga el resto de esta guía. Las secciones de arriba ya se corrigieron para que coincidan (cierre de la versión 2, 17 nov 2026).

1. **La dirección va bien** en color y en forma general. En el producto final, la cara psicodelia lleva **más formas y dibujos de cartel** (figuras, grabado fino, letras que llenan su forma), no solo bandas y el disco.
2. **La intensidad máxima, un poco más fuerte** que la de la lámina.
3. **Con un consumo en curso de día, entra la psicodelia en claro.** La ciudad no se tiñe: cambia la cara.
4. **Cuenta cualquier sustancia que tenga nivel** (hoy: alcohol, cannabis y «otra»; el tabaco y la cafeína no tienen nivel). La escala: **nivel 3 da la intensidad media (0.5) y nivel 8 la máxima (1)**; entre medias, en línea recta (de 0 a 3 y de 3 a 8); por encima de 8, sigue en 1.
5. **Si la sustancia está en la lista de ocultas, el tema reacciona igual.**
6. **El cuento:** la marihuana, una vez por semana; el LSD, más o menos una vez al mes; los hongos, unas pocas veces al año.
7. **Para después, con el cuadro del tema:** cada sustancia cambia la escena de una forma distinta, que intente reproducir visualmente su efecto. Solo queda anotado.
