# Plan: los temas completos (21 actuales + 13 nuevos = 34), con diseño propio en cada superficie y su gato

## Contexto

La app tiene 21 temas (`TemaId`, `ui/Tema.kt:63`). Su cobertura es muy desigual:

- **Seis temas sin diseño completo.** Solo 15 temas recibieron un «lote de diseño» con trofeos, salón, libreta y mapa de campaña propios. Los otros 6 (CIB, REF, ORD, LAB, ESP y URS) caen en respaldos genéricos «por familia».
- **Superficies pobres o compartidas.** Muchas superficies tienen ramas `else`, figuras compartidas o versiones pobres.
- **Regla de los temas base.** Una regla (`ui/Bases.kt`) hace que lo nuevo se dibuje solo para 6 temas base.
- **Partes genéricas.** Muchas pantallas, componentes, textos, sonidos y recursos son iguales para todos los temas.
- **Dos huellas.** El ícono y el sello del día usan huellas distintas.

Una auditoría completa del código, hecha sobre la rama más reciente (que incluye `main` y el gato), encontró además:

- **El compilador no vigila todo.** Muchos despachos por tema no los vigila: mapas por texto, `if (tema == …)`, `in setOf(…)` y ramas que agrupan varios temas.
- **Huecos que el primer borrador no cubría:**
  - en contenido: reportes, Mapa.kt, cartas, componentes de Material, barra inferior, Quick, Enfoque, Bloqueo y la pantalla de arranque;
  - en sonido: los sonidos de error y de rascar;
  - en código: un error de colores en Deshacer.

Lo que pidió el autor:

- **Cobertura completa:**
  - Los 21 temas al 100 %: figuras y fondos, texturas, textos y tipografía, sonido y vibración. Incluye las pantallas que hoy son genéricas.
  - Se ignora la regla de los temas base.
- **Huella:**
  - Una sola huella, la del ícono, en todos los lugares donde *ya* hay una huella. No se agregan huellas en sitios nuevos.
  - El ícono queda sobrio, con un poco más de diseño, y sin que cambie mucho de un tema a otro.
  - El sello del día conserva todo lo que rodea a la huella; solo cambia la huella.
  - Los fondos no se tocan, salvo para mejorar los demasiado simples, sin salirse de su línea y sin ponerles huella.
- **Emblema:** el emblema dibujado de cada tema se queda.
- **13 temas nuevos:** pirata, ciudad neón, ciudad verde, Roma (la ciudad de hoy), templario, horror cósmico, Mesoamérica, Egipto, Japón Edo, prehistoria, vapor victoriano, art déco y abismo.
- **Cyberpunk:** el actual pasa a llamarse «Terminal». Cada tema nuevo debe distinguirse de sus parecidos.
- **El gato y las vidas:** cada tema nuevo tiene su gato de la banda de Día y su «otra vida».

**Por ahora no se cambia código.** Se implementa por lotes, cada uno en su rama y con su PR.

## Principios

1. **Cobertura vigilada, no supuesta.** Al terminar, la cobertura se garantiza de tres formas.
   - **(a) `when` exhaustivos.** Todo despacho de diseño será un `when (tema)` exhaustivo, sin `else` y sin ramas que agrupen temas (`TemaId.A, TemaId.B ->`). Hay que convertir:
     - los mapas por texto:
       - `ui/Sentidos.kt:241` y `avisos/Avisos.kt:32-47`. Este último hoy cae en «romano» si falta el tema.
       - `ciencia/Campanas.kt:132-178` (`TITULOS` y `ETAPAS`), que también cae en «romano».
       - `ui/NombresNuevos.kt`, `ui/Temas.kt:96` (`MARCAS`) y `Temas.widget`;
     - los `if (tema == …)` y los `in setOf(…)`, por ejemplo:
       - `ui/Componentes.kt:202,213,514`;
       - `ui/Libros.kt:348,355,443,1213`;
       - `ui/Hoja.kt:59`, `ui/Cartas.kt:105`;
       - `ui/Sentidos.kt:314-315`, `ui/LibroDiario.kt:245,365`, `ui/FondosTelefono.kt:195`;
       - `reportes/Reportes.kt:56,146`, `pantallas/HallImagen.kt:59`, `pantallas/Dia.kt:192,202`;
     - el `when {}` con condiciones de `pantallas/Mapa.kt:81`;
     - los despachos por otro enum (`soporte(t)`, `contenedorDe`, `salonDe`, `Bases.de`). Siguen sirviendo como ayudantes, pero la rama de dibujo final debe ser por tema.
   - **(b) Prueba de cobertura** (`app/src/test/.../CoberturaTemas.kt`, nueva). Recorre `TemaId.entries` y exige, para cada tema:
     - entrada en cada mapa que quede (sonidos, avisos, títulos y etapas, nombres, marcas, rótulos de widget);
     - al menos una vida en `OtrasVidas.DE_TEMAS`;
     - el `activity-alias` `.Icono<Id>` en `AndroidManifest.xml`. Hoy, si falta uno, `IconoApp.kt:24-28` cancela el cambio de ícono **para todos los temas** sin avisar.
     - `ic_launcher_<id>.xml`;
     - `logo_<id>`, `logo_fondo_<id>`, `huella_<id>`, `huella_fondo_<id>` y `papel_<id>`;
     - los 4 drawables de widget y su copia en v31;
     - los 13 archivos de `raw` más `s_tecla_<id>`, `s_rascar_<id>`, `s_<id>_error` y `s_<id>_cambio`;
     - las claves `<id>_c` y `<id>_o` en `assets/gato/gato.json`, con sus 2 acciones especiales.
   - **(c) Prueba de unicidad.** Para todo lo que se puede comparar como valor, exige que no haya dos temas iguales:
     - textos y nombres de pantalla;
     - numeral(17), mes(10), semana, atrás, claves y prefijos;
     - familia de letra de título y de texto;
     - radio de esquinas y forma;
     - escala musical, patrón de vibración y archivo de sonido;
     - nombres de trofeos y de etapas.

     Los dibujos se revisan en las capturas.

     Además, una regla de grep en el mismo PR (o una prueba que lea el código fuente) prohíbe `TemaId\.\w+,\s*TemaId\.` y `else ->` en los archivos de diseño.
2. **«Propio» quiere decir** una figura o un texto que no se comparte con otro tema y que no es una sola línea de color. Se pueden reutilizar ayudantes (`columnaClasica`, `greca`…) siempre que el resultado se distinga.
3. **Todo raster es reproducible.** Todo PNG, JPG, WEBP u OGG que se toque sale de un script en `herramientas/`. Lo que se extraiga de un raster viejo (por ejemplo, el ornamento de un sello) se guarda **una vez** como fuente en `herramientas/fuentes/` (`ornamento_<id>.png`, `fondo_sello_<id>.jpg`), y el script parte de ahí.
4. **Sin cambios de datos ni de cálculos.**
   - No sube `SCHEMA_V`, porque el id del tema ya se guarda como texto.
   - No se tocan `Exportar`, `Diccionario` ni `vestigium.R`.
   - No se toca el paper: son cambios visuales, de texto de interfaz y de sonido. Las gráficas cambian solo su adorno, nunca los datos, las escalas ni `Tonos`.
   - Una excepción a vigilar: si al escribir las vidas cambia el cálculo del parecido (`distancia`), entonces sí se actualiza el paper.
5. **Todo en español y con invención propia.** No se usan nombres, criaturas, lugares ni símbolos con derechos de autor (Lovecraft, Star Wars, películas de piratas…).
6. **Fuentes:** solo con licencia OFL o Apache.
   - Se recortan a un subconjunto con `pyftsubset`. `fontTools` no está instalado: se instala con `pip` en el lote 0.
   - Se renombran si su licencia reserva el nombre, como ya hace el repo («Gaceta Vestigium», «Orbita Vestigium»).
   - Su licencia se agrega a `app/src/main/assets/licencias/` (la OFL en `fuentes_OFL.txt` y la de Apache en un archivo aparte).
7. **En el reporte del día, lo que se lee para entender un dato se entiende.** Lo pidió el autor después del lote 5.
   - Vale para la pantalla Día y sus capturas: las claves de «Lo que registró el teléfono» (sueño, pasos…), los rótulos de sus fichas y el título de «Llenar el día».
   - La palabra del tema no se quita, se complementa: si va en griego, árabe, cirílico, mongol o chino, lleva su español al lado o debajo (`Estilo.claveEspanol`, `Estilo.conEspanol`).
   - El latín, el italiano, el francés y el castellano antiguo se parecen al español y van solos.
   - En el resto de la app las palabras en otras lenguas se quedan como están.
   - Lo vigila `LegibilidadDia`.

## Lote 0: base de trabajo

- **`docs/diseno/TEMAS.md`**: la matriz superficie × tema (P/S/C/G) que sale de las auditorías, con una fila de estado por lote. Cada PR marca sus celdas.
- **Pruebas** `CoberturaTemas` y `UnicidadTemas` (principio 1). Al principio fallan en muchas celdas; se activan por superficie conforme avanzan los lotes, con una lista de excepciones que se vacía hasta llegar a cero.
- **Capturas por tema** (`Capturas.kt`): ayudantes que recorren `TemaId.entries` en claro y en oscuro.
  - Hoy hay altos fijos que se cortan con 34 temas: `gatos` 6200, `selectorTemas` 5600, `sellos` 2600, `iconos`, `personajes`, `marcas`, `barras`, `calificaciones`, `temasAjustes` y `trofeosTemas`. Pasan a calcular el alto o a repartirse en varias pruebas, por la memoria del CI (commit `9e4e8ff`).
  - Ayudantes nuevos:
    - `trofeos`, `salon`, `piezas`, `hallImagen`, `campana` y `libreta`;
    - `fondoVivo`, `fiesta` y `relacion`;
    - `huella`, `icono` y `mapaFormas` (`Formas` y `MarcoMapa`, fuera de osmdroid);
    - `calificacion`, `reporte`, `reporteMes` y `vozAnio`;
    - `calendario`, `constelacion`, `medalla` y `graficas`;
    - `dialogo`, `barra`, `quick`, `enfoque`, `bloqueo` y `cartas`.
  - Hoy `objetos1-3` y `sobres1-3` dejan fuera a REV y ANT.
- **CI sin publicar.** Hoy `apk.yml` con `workflow_dispatch` hace `gh release create … --latest`, así que lanzarlo desde una rama publicaría un Release. Se agrega una entrada `publicar` (que por defecto no publica en ramas que no sean `main`) o un trabajo de solo compilar. `capturas.yml` lleva `|| true`: hay que revisar `fallas.txt` y `errores.txt` en la rama `capturas`.
- **Hoja de contacto** de rasters: `muestra()` de `herramientas/iconos.py` deja la cuadrícula fija de 7×3 y calcula las filas para N temas.
- **Error a corregir:** `Deshacer` (`ui/Deshacer.kt:55-59`) usa `inverseSurface`, `inverseOnSurface` e `inversePrimary`, que ningún esquema define (`Disenios.kt:41-61`, `Tema.kt:87-143`). Hoy sale el lila o gris por defecto de Material en todos los temas. Se definen en cada esquema.

## Lote 1: una sola huella (ícono, sello, monocromo)

La huella maestra es `herramientas/fuentes/huella_icono.jpg`, la que ya usa `iconos.py:mascara()`.

### 1a. Ícono (`herramientas/iconos.py`, que se reescribe)

Se generan `logo_<id>.png` (432² RGBA) y `logo_fondo_<id>.jpg` para todos los temas.

La composición es la misma en todos: la huella del mismo tamaño y en la misma posición, dentro del círculo seguro del 66 %. Cada tema añade **solo** tres cosas:

1. **Una textura de fondo propia y tenue.** Hoy hay 6, una por tema base; pasan a ser una por tema.
2. **La tinta de la huella** en el color del tema, con un acabado discreto: leve dorado, tinta ferrogálica, fósforo…
3. **Un filete o anillo fino** con el motivo del tema en miniatura, siempre del mismo grosor y radio.

Otros cambios en los scripts:

- **`arte.py`** hoy también escribe `logo_<t>` para 7 temas (`arte.py:51-54,508-509`). Esa rama se quita para que `iconos.py` sea la única fuente del ícono.
- **`logo_mono.png`** se regenera desde la maestra.
- **`res/drawable/ic_stat.xml`**, que hoy es otra huella hecha a mano, se cambia por una versión vectorial simplificada de la maestra, legible a 24 dp. También la usan los mosaicos de ajustes rápidos.

### 1b. Sello del día (`huella_<id>.png` + `huella_fondo_<id>.jpg`; `ui/Huella.kt:84` `recursosHuella`)

Se conserva el ornamento: la ficha de DIS, el escarabajo de BOT, el lacre de NAP, el círculo de REV, el marco de ORD, la mira de LAB, la cuña de URS…

Se reemplaza la huella por la del ícono, a la misma escala y en la misma posición.

Se hace con un script nuevo, `herramientas/sellos.py`, que absorbe las funciones de sello de `arte.py`:

- **Los 7 temas de `arte.py`** (BOT, GOT, ESP, AND, MON, REN y ANT): su `mascara()` (`arte.py:10-11`), que hoy toma `huella_romano.png`, pasa a tomar la maestra.
- **Los otros 14 temas** se hacen en dos pasos:
  1. **Extracción, una sola vez:** se separa el ornamento del PNG viejo, quitando la elipse de la huella, y se guarda en `herramientas/fuentes/ornamento_<id>.png` y `fondo_sello_<id>.jpg` (principio 3).
  2. **Composición:** el script pinta la huella maestra con el material que hoy tiene cada sello (el color o el degradado, muestreados) y vuelve a poner encima lo que se le enciman (el escarabajo de BOT, la banda de URS).

Se revisa con una comparación de antes y después en la hoja de contacto.

`SelloHuella` y `VistaHuella` (`ui/Huella.kt:108-152`) no cambian de diseño. El texto «racha de N días» (147) pasa a `Estilo` como texto por tema; el arreglo del sello no cambia.

### 1c. Fondos del sello

Se quedan como están, salvo los demasiado simples, que se mejoran sin salirse de su línea y **sin huella**:

| Tema | Cambio |
|---|---|
| COL | De crema liso a papel de gaceta con fibras y retícula de columnas |
| DIS | Papel de oficio con grano y marcas de sello tenues |
| NAV | Las luces de siempre, más nieve fina |
| REF y ESP | Se revisan; solo se tocan si quedan planos |

### 1d. Dónde sale la huella

Solo donde ya había una. No se agrega en ningún sitio nuevo, y eso vale también para los temas nuevos. Los lugares son:

- el ícono del lanzador;
- `logo_mono`;
- `ic_stat` (notificaciones y ajustes rápidos);
- el sello del día (`CapaHuella`, `MainActivity.kt:423`);
- la pantalla de bloqueo (`MainActivity.kt:179`);
- el respaldo del fondo de pantalla vivo cuando no hay captura (`FondosTelefono.kt:377` → `logo()` en 500). Ojo: el fondo de pantalla normal dibuja el **Emblema**, no la huella, y eso no cambia.

Las pisadas de pasos (`Hall.kt:214`, `TuAnioVisual.kt:177`, `Reportes.kt:338-340`) no son la huella digital y no se tocan.

## Lote 2: los 6 temas sin diseño completo

Los temas son CIB, REF, ORD, LAB, ESP y URS. Se hace un PR por tema, y cada tema entra completo y en el mismo cambio, porque comparte el filtro `*.TEMAS`. Cada PR incluye:

1. **Trofeos** (`ui/TrofeosMas.kt`):
   - `figuraMas`, con las 9 categorías: Diario, Mundo, Cuerpo, Mente, Cultura, Gente, Constancia, Curiosos y Tuyos;
   - `pedestalMas`;
   - `NOMBRES`, con 9 nombres que no se repitan con ningún otro tema;
   - la entrada en `TrofeosMas.TEMAS`.
2. **Salón** (`ui/SalonesMas.kt`): `escenaMas` (que respete `columnas`) y `pedestalMas`.
3. **Campaña:**
   - en `ui/MapasMas.kt`: `fondoMas`, `rutaMas`, `etapaMas`, `victoriaMas`, `puntos` y una rama en `marcador`;
   - en `ciencia/Campanas.kt`: sus `TITULOS` y `ETAPAS`, alineados con el mapa.
4. **Libreta:** un valor nuevo de `Contenedor`, más `contenedorDe`, `cerradoMas`, `fondoHojaMas`, `encuadernacionMas` y `papel`.
5. **Imagen del hall** (`pantallas/HallImagen.kt:53,79`): sale sola de lo anterior.

Para no dibujar dos veces, **CIB se diseña aquí directamente como Terminal** (ver 8a): la ciudad y el neón se guardan para NEON.

| Tema | Trofeos | Salón | Campaña (mapa · ruta · marcador · victoria) | Libreta |
|---|---|---|---|---|
| CIB (Terminal) | Trofeos en arte ASCII dentro de ventanas de consola | Sala de computadoras centrales: cintas, consolas y luces de estado | Traza de red entre servidores · paquetes de nodo en nodo · cursor `▮` · «ACCESS GRANTED» | Consola de registro, con hojas de log |
| REF | Placas y medallas de chapa con el átomo | Sala común del refugio: puerta de engranaje, tuberías | Corte del refugio por niveles · pasillos y escaleras · ficha amarilla · salida a la superficie | Carpeta del supervisor con broche |
| ORD | Estandartes, escudos y piezas forjadas | Sala capitular de acero con estandartes y remaches | Mapa táctico · flechas de avance · banderín · fortaleza tomada | Códice técnico con tapas remachadas |
| LAB | Matraces, microscopios y placas de mérito | Laboratorio limpio: vitrinas y campana de extracción | Placa de Petri o protocolo · pipeta · gota · cultivo completo | Cuaderno de laboratorio cuadriculado con cinta de muestras |
| ESP | Parches de misión, cohetes y sondas | Cúpula de observatorio con telescopio | Sistema solar · órbita con `puntos` propios · nave · aterrizaje | Bitácora de vuelo |
| URS | Medallas del trabajo y estrellas | Estación de metro constructivista | Transiberiano de Moscú a Vladivostok con `puntos` geográficos · locomotora · estrella | Cuaderno escolar (тетрадь) |

**Etapas de campaña que cambian:** hoy son pobres o no cuadran con el mapa nuevo. Hay que reescribir:

- LAB, que hoy es «Día 1…6»;
- CIB, que hoy es «NODO 01…06»;
- URS, que hoy es FÁBRICA, KOLJÓS… y pasa a ser las estaciones del Transiberiano;
- ORD, que hoy termina en «Reliquia» y pasa a terminar en la fortaleza;
- DIS, que hoy es «SECTOR n».

Se reutilizan `geo()` de `MapasMas.kt` y los patrones de `columnas` de `SalonesMas.kt`.

## Lote 3: completar las superficies que ya existen (todos los temas)

Para cada superficie se dicen los temas que hoy la comparten, la tienen genérica o pobre, y lo que cambia. Todo se vuelve propio para cada tema.

### Campaña, salón, hall y trofeos

- **Marcador** (`MapasMas.kt:218-250`): propio para ROM (legionario con estandarte), MED (vela), GOT (peregrino con venera) y REN (punta de plata).
- **`victoria`:**
  - separar ROM de HEL y MED de GOT;
  - enriquecer las simples: COL, NAP, BOT, NAV, SED, AND, MON, REN, REV, ANT y DIS.
- **`puntos`:** pasa a devolver `List<Offset>` (no nulo), con una ruta propia para cada tema. Las `ETAPAS` cuadran con los puntos.
- **Salón:** enriquecer `atrio` (ROM) y `stoa` (HEL) (`SalonesTema.kt:99-122`).
- **Pedestal:** separar el de ROM y el de REN (`TrofeosTema.kt:71-76`).
- **Nombres de trofeos repetidos:** «Arco de triunfo» (ROM y NAP), «Cúpula» (ROM y REN) y «Astrolabio» (REN y AND).
- **Sala de trofeos** (`pantallas/Trofeos.kt:309-322`): pared y repisa propias en cada tema (mármol, pupitre de acero, estante de laboratorio, anaquel de bambú…). También son propios los `destellos` y la `Placa`.
- **Piezas del hall** (`pantallas/Hall.kt:91-320`):
  - techos de ciudad (155-161), templo (192-201), borde (104-109) y sello de pasaporte (171-176): uno por tema, en lugar de uno por grupo o familia;
  - **las otras 17 piezas** (204-319), que hoy son genéricas: cada una toma el material y el trazo del tema mediante un ayudante `trazoTema(t)`. Además, `estandarte`, `moneda`, `libro` y `sobre` tienen una figura propia por tema;
  - `METALES_HALL`: un matiz por tema.
- **Imagen del hall** (`HallImagen.kt:59`): marca o colofón propio por tema. Hoy solo ROM escribe «VESTIGIVM MEI».

### Animación y celebración

- **Fondo vivo** (`Animaciones.kt:95-197`) y **fondo de pantalla vivo** (`FondosTelefono.kt:408-413`): se unen en un solo despacho con un tipo propio para cada tema. Hoy se contradicen en 11 temas, y LAB ya usa «polen» en el fondo de pantalla.
  - Ejemplos:
    - ROM: chispas de lucerna;
    - HEL: hojas de olivo;
    - MED: polvo de oro en un rayo de luz;
    - DIS: ceniza de archivo;
    - REF: vapor, de día y de noche;
    - ORD: chispas de fragua;
    - LAB: polen o burbujas;
    - COL: hojas de periódico;
    - NAP: destellos de charreteras;
    - REV: confeti tricolor tenue;
    - URS: estrellas rojas;
    - ANT: runas que brillan.
  - El «barrido» se queda solo en REF.
- **Lluvia de celebración** (`Decoracion.kt:1297-1358`):
  - una propia para cada tema;
  - se separan las parejas CIB/DIS, HEL/ORD y NAP/URS;
  - se dibujan MED y COL;
  - se cumplen las «gotas de tinta» y las «brasas» que promete el comentario de la línea 1295.
- **Celebración de logro** (`Celebracion.kt:113-140`): los 16 rayos genéricos pasan a un motivo por tema (laureles, haces de neón, vitral…), y los nombres de nivel se escriben en la voz del tema.
- **Transiciones entre pantallas** (`MainActivity.kt:435-484`): una por tema. Hoy HEL, LAB, NAV y ANT son casi iguales, y ROM se parece a COL.
- **`CapaTransicionTema`, `entrada` y `presion`** (`Animaciones.kt:227-248`): un matiz por tema.
- **`periodo`** (`Decoracion.kt:65-71`): uno por tema. Hoy ROM=REN, DIS=GOT y ESP=REV.

### Relación, Hoy, Día, Mundo y Tu año

- **Relación** (`Relacion.kt`): `lazo`, `Recuadro` y `adorno` propios para todos.
- **Hoy** (`pantallas/Hoy.kt`):
  - frase del día (319-370), `estiloCifra` (520-529) y `FranjaDias` (666-735): una versión por tema;
  - `LineaDelTiempo` (538-640) y la mini gráfica de `TarjetaMetrica` (503) usan el estilo de gráfica del tema (ver lote 4);
  - el corazón de `CorazonFrase` (`FrasesGuardadas.kt:53-90`) pasa a una forma por tema.
- **Día** (`pantallas/Dia.kt:192,202`): el «_» de CIB y la fecha más grande de ROM pasan a `Estilo` como sufijo de título y estilo de fecha por tema.
- **Mundo** (`Mundo.kt:201-208`): la lámina para rascar tiene metal y motivo propios; `TuAnio.Rascar` (225-247) reutiliza la misma lámina.
- **Tu año:**
  - `voz` (`TuAnio.kt:126-148`): repasarla tema por tema, por ejemplo AND con fecha hégira y sin repetir «Crónica»;
  - `MesesAnimo` (262): usar `Estilo.mes`;
  - «Libros del año» (`TuAnioVisual.kt:89-133`): una forma por tema, sin `Bases.de`;
  - «Cosas del año» (138-187): objetos con el aire del tema;
  - `fondoTema` para ROM y HEL.

### Libros, cartas y libreta

- **Objeto cerrado** (`Libros.kt`):
  - propio para ROM (rollo con umbilicus), HEL (sin la greca de MON), NAP (pasa del sobre genérico a un boletín con lacre), ESP (que no sea la pantalla de CIB), COL, REN y URS;
  - letra del objeto (362, hoy Garamond fija) y pistas (275-287, hoy por soporte): por tema.
- **Página** (`pagina` 868-890):
  - ROM y HEL con papel distinto;
  - letra de página propia para cada tema (hoy Garamond en 9 temas).
- **Adorno de página** (`FondoPagina` 894-973): nuevo para ROM, HEL, SED, LAB y DIS, y se separan COL y URS.
- **Estante:**
  - madera y lomo propios (madera: ROM, HEL, MED, COL, AND, URS, REV y ANT; lomo: MED, ORD, LAB y COL);
  - fecha del lomo con la letra del tema y `Estilo.mes` (721-722 y 659, hoy `MESES3` fijo);
  - rollo de ROM con adorno (729-760).
- **De noche:**
  - `materiaNoche` y `paginaNoche` (164-190) para los 18 temas que caen en `else`;
  - `CieloNoche` (237-251) y `EncabezadoSueno` (1042-1062): cielo, luna y estrellas por tema.
- **Encabezado de página** (1018-1038): propio en los 21, no solo el emblema.
- **Textos de lectura y escritura** (1127-1216): por tema. Incluye «No hay nada escrito…», «Lo mejor / Lo peor» y «¿Cómo fue el día?».
- **Cartas** (`Cartas.kt`, `LibroDiario.kt:306-424`):
  - forma del sobre (`formaSobre(t)`) y sello (`sello(t)`) por tema, en lugar de un solo sobre y una sola gota de cera: sobre con ventana en CIB, sello de goma en DIS, cápsula en ESP, etiqueta en LAB…;
  - marco y filete del PDF de la carta por tema;
  - textos (287-474);
  - el `textoBoton = "listo"` (457) se quita para que mande `textoGuardar`.
- **Libreta:**
  - HORAS se separa por completo de CODICE;
  - MANUSCRITO y MAPAS dejan de compartir encuadernación (`ContenedoresMas.kt:287`);
  - el `papel` deja de tener `else -> PERGAMINO` (`ContenedoresMas.kt:43`).
- **Diario exportado** (`LibroDiario.kt`):
  - fuentes por tema (130-152): se separan COL/REV y ROM/ANT;
  - capitular por tema, tanto en el PDF del diario (279) como en el de la carta (392), con el mismo criterio en los dos;
  - filete con rombo (266-268, 387-388) y CSS del HTML (158-188): por tema;
  - las listas «clásicas» (245, 365) pasan a `when`.

### Decoración base y componentes

- **Emblemas** (`Decoracion.kt:90-460`):
  - se mejoran CIB, HEL (sol de Vergina con 16 rayos y animación), MED, REF, LAB, NAV y AND;
  - NAP deja el laurel de ROM y pasa a un águila imperial o una abeja con la N.
- **Textura de fondo** (`fondoTema`, 1023-1228):
  - nueva para ROM, HEL y REF de día;
  - se enriquecen las simples;
  - se agrega `fondoTema` en `PrivacidadActivity` y en `QuickActivity`.
- **Banda bajo los títulos** (`Componentes.kt:90-127`):
  - ROM pasa a ovas y dardos, distinta de la greca de HEL;
  - se enriquecen DIS, LAB, COL, SED y MON.
- **Componentes** (`Componentes.kt`), todos con versión por tema:
  - `Seccion` (183-245): estilo de título, separador y el «▸/▾»;
  - `BotonPrincipal` (506-518) y `BotonSecundario` (521);
  - `Tarjeta` (248): esquina o filete;
  - `Vacio` (541): ilustración pequeña;
  - `Aviso` (554);
  - `Etiqueta` (256), con su prefijo único;
  - `Clave` (284), con su «= »;
  - `Escala` (297-339): marcas y textos;
  - `Opciones` (365), `Interruptor` (384), `ListaEtiquetas` (396) y `Editor` (429-474), que hoy es una barra de «archivo de código» para todos, y sus nombres de archivo en latín;
  - `Cifra` (531), `FilaMenu` (562) y `Pastilla` (573).
- **Personaje** (`Personaje.kt`):
  - atuendo propio para cada tema (86-317): ANT con yelmo rúnico y báculo; SED con gorro de caravanero; AND con turbante con tiraz; GOT con tonsura; se usan `LAUREL` y `YELMO`, que hoy no se usan; se enriquecen los simples;
  - **frases y globo de diálogo (65-77, 345)**: por tema.
- **Marcas de @persona y #tema** (`Marcas.kt:54-79`):
  - una combinación distinta por tema;
  - con solo 7 `Forma` no alcanza, así que se añaden formas nuevas (sello de cera, cartucho, cinta, recuadro de terminal…);
  - se rompen HEL≡AND≈NAV, CIB≈ESP, GOT≈ANT, ORD≈REV, la persona en sello (DIS, SED y URS) y la persona subrayada (ROM, MON, ORD y COL).
- **Fondo de pantalla del teléfono** (`FondosTelefono.kt`): `marca` (149-156) y marco (194-215) propios en cada tema; hoy hay 2 marcos.
- **Íconos de pestaña** (`IconosPestana.kt`):
  - «día»: se separan ROM y HEL;
  - «diario»: CIB tiene ícono propio (hoy usa `documento()`, que es genérico);
  - «registrar» y «ajustes»: propios en todos los temas;
  - `gastos()`: se usa o se borra.
- **Botones** (`Decoracion.kt:1236-1377`):
  - modo, buscar y ajustes, por tema;
  - el texto «Más» (1375), por tema.
- **Barra inferior** (`MainActivity.kt:189-234`):
  - indicador, botón central y divisor, por tema;
  - nombres de las pestañas en la voz del tema, desde el diccionario de `Temas`.
- **Bloqueo** (`MainActivity.kt:176-186`): textos («VESTIGIVM MEI», «tu diario está sellado», «Desbloquear») y composición por tema. La huella cambia según el lote 1.
- **Vista previa de tema** (`Tema.kt:381-415`): forma y cuadritos por tema; COL y SED dejan de compartir el título «Jornada».

### Mapa, galerías, reportes, widgets y avisos

- **`pantallas/Mapa.kt`:**
  - `filtroMapa` (76-146) pasa a un `when (tema)` exhaustivo, con una rama explícita para ROM;
  - `Formas.dibujar` (166-206): se separan CIB/LAB, MED/NAP, NAV/URS y MON/REN, y cada tema tiene su detalle;
  - `MarcoMapa` (566-577): uno por tema (hoy hay 2);
  - rosa de los vientos (582): una por tema;
  - el texto de la línea 451 usa `Estilo.comentario`.
- **`pantallas/Galerias.kt`:**
  - `simboloCalificacion` (105-141): se separan ORD, URS y NAP, que hoy son tres estrellas de 5 puntas, y se corrige el KDoc que habla de una «abeja»;
  - `Recuadro`, `dibujoMapa` y `TarjetaMapa` (170-270): por tema.
- **Bibliotecas y Biblioteca:**
  - íconos de las pestañas de `Bibliotecas.kt:171`, por tema;
  - portada de respaldo de una obra (`Biblioteca.kt:150-170`) con la letra del tema.
- **`reportes/Reportes.kt`** (reporte semanal, mensual y anual, y el `Lienzo` del hall):
  - `cifra` (56-57), `ornamento` (109-141) y la marca (146): por tema. Hoy ROM=HEL, CIB=REF, DIS=URS=ORD, y 14 temas en `else`;
  - `mesNombre` (555 y 855): usa `Estilo.mes`;
  - las gráficas usan el estilo de gráfica del lote 4.
- **Widgets** (`widget/Widgets.kt` y `res/drawable/widget_*`):
  - forma propia por tema: esquinas, filete y muesca. Hacen falta `layer-list` o vectores;
  - la copia de `drawable-v31` deja de forzar `system_app_widget_background_radius`, para no perder el radio del tema en Android 12+;
  - títulos del modo discreto (146, 161: «NVNC» y «SCRIBERE») desde `Estilo.widget`;
  - el calendario del widget (281) deja la letra monoespaciada fija;
  - se quitan los rótulos repetidos entre temas (ESTADO ×4, DESCANSO ×4…);
  - la vista previa del selector de widgets (xml estático) se queda en ROM, porque Android no permite cambiarla.
- **Notificaciones:**
  - `setColor` con el primario del tema;
  - textos de aviso en la voz del tema (`Avisos.kt:133-364`);
  - Enfoque (`data/Enfoque.kt:221-266`): textos, color, y el canal de «fin de bloque» con el sonido del tema en lugar del sonido del sistema.
- **Accesos directos** (`res/drawable/ic_sc_*`, color fijo): se generan dinámicos con `ShortcutManager` y el color del tema.
- **Fondo vivo:** la miniatura (`res/xml/fondo_vivo.xml`, fija en `logo_fondo_espacio`) se queda fija, porque Android no la deja cambiar; se documenta.

## Lote 4: pantallas genéricas, gráficas, controles y arranque

Solo cambia la presentación, sin tocar datos:

| Qué | Cambio por tema |
|---|---|
| Calendario (`Celda` 208) | Celda, día de hoy, día sellado |
| Cinta (271), `IconosCinta.kt` | Línea de tiempo, nodos e íconos |
| Constelación (99) | Forma de la estrella: sello, moneda, runa, led… |
| `MedallaLogro` (`Logros.kt:73-104`) | Medalla |
| `OtrasVidas.kt` (426, 442, 464) | Barras y reloj de 24 h |
| `Selectores`, `CategoriasSueno` (`CuadriculaAfecto`), `Ficha` | Forma de la celda o del chip |

**Estilo de gráfica por tema.** Un ayudante nuevo, `EstiloGrafica(tema)`, define el punto, el trazo, la rejilla, el eje y la letra de las cifras. Se aplica a **todas** las gráficas, sin cambiar escalas, datos ni `Tonos`:

- Explorar: `Lista` 345 (hoy con `Letra.codigo` fija), `Horas` 362, `Cobertura` 377, `Dispersion` 403, `EtapasDeVida` 457 y `ConQuien` 491;
- Analisis: 178, 219, 266, 304 y 372;
- Anno: 91 y 130;
- TusApps: 356–463;
- Totales: 229;
- Salud: 143, 168 y 196;
- Medir: 146 y 376;
- Fuerza: 72 y 101;
- Escalada: 154;
- Gastos: 444;
- Gente: 66;
- EnfoqueSiestas: 92;
- Recaidas: 262 y 278;
- Politica: 183 y 235;
- Lote3: 194, 283 y 382 (`PantallaRed`);
- Recuerdos: 96;
- `LineaDelTiempo` y `TarjetaMetrica` de Hoy;
- los reportes.

**Controles de Material con capa de tema.** Se crean envoltorios en `Componentes.kt` con forma y adorno por tema:

- `Dialogo`, que sustituye a los 29 `AlertDialog` de `ui/`, incluidos `EditarSueno` y Enfoque;
- `Deslizador` (Slider) y `Progreso`;
- `Interruptor`, que ya existe pero usa el `Switch` sin tema;
- `Menu` (DropdownMenu: `Libros.kt:1133`, `Cartas.kt:411`);
- `BarraDeshacer`;
- selectores de fecha y hora en Compose, en lugar de los `DatePickerDialog` y `TimePickerDialog` nativos (`Selectores.kt:58-59,82`).

**Otras pantallas:**

- **Hoja de Quick** (`QuickActivity.kt:127-256, 403, 736`): forma por tema (hoy 6 dp fijos), `fondoTema`, y títulos desde `Estilo`.
- **Enfoque** (`EnfoqueActivity.kt`):
  - esfera (113-135) y tira de sesión (139-159), por tema;
  - el reloj (196) usa `displayLarge`, que ningún tema define. Se define en `tipografia()` de cada tema; hoy sale con la letra del sistema.
- **Pantalla de arranque:** hoy `Theme.Vestigium` usa `windowBackground=@color/fondo` fijo, y en modo claro CIB arranca con un destello claro.
  - Se crea un estilo `Theme.Vestigium.Arranque.<Id>` por tema en `values/themes.xml`.
  - Se elige con `SplashScreen.setSplashScreenTheme`, porque un alias no puede llevar tema. Eso solo funciona en API 33+; en API 30-32 se queda un fondo neutro oscuro.
- **Presentación del tema** (`Musica.kt:208-238` `CapaPresentacionTema`): composición y «toca para seguir», por tema.
- `Respaldo.kt` pasa a usar `Hoja`.
- **Sistema** (`pantallas/Sistema.kt`):
  - el texto fijo «El cyberpunk solo existe en oscuro» (306) se generaliza;
  - la vista previa deja de usar `modoOscuro(ROMANO)` (261).

## Lote 5: textos y tipografía

### Nombres y voz (`Temas.kt`, `NombresNuevos.kt`, `Estilo.kt`)

- **Diccionarios completos:** todos los títulos tienen nombre en todos los temas, sin entradas iguales al español.
  - Los diccionarios más pobres son NAV, URS, SED y AND.
  - Hay entradas idénticas al español en MED, LAB, COL, NAV, SED y URS.
  - Algunas claves solo existen en ROM, HEL o ANT («Pantallas», «Registro», «Lugares especiales»…).
  - CIB pasa a tener diccionario propio de consola; hoy es `emptyMap()`.
- **Claves nuevas** para los textos que hoy son genéricos: bloqueo, pestañas, «Más», personaje, niveles, presentación, cartas, lectura, escritor, pistas, `Escala`, `ListaEtiquetas`, `Editor`, racha, avisos y widgets discretos. Se leen con `Estilo.texto(clave)`, y la prueba de cobertura las exige para todos los temas.
- **Comentario bajo el título:**
  - `comentarioNuevo`: propio en todos los temas (hoy 12 caen en `else`);
  - el prefijo `comentario` (22-42) es único por tema; hoy ROM=CIB=ORD y HEL=NAP.
- **`mayusculas`** (45): una decisión explícita para cada tema.

### Calendario y números (`Estilo.kt`)

| Elemento | Qué cambia |
|---|---|
| `numeral` (98-117) | Propio para BOT, NAV, MON (usa `Temas.mongol`, que hoy no se usa), REN, ORD≠LAB, NAP≠ROM, ANT≠ROM y GOT≠ROM≠MED |
| `mes` (159-183) | Propio para CIB, DIS, REF/ORD, LAB, COL, BOT/NAV, ESP y MON. REV usa los meses republicanos. GOT se distingue de ROM |
| `semana` (185-200) | Propia para todos. Hoy CIB=DIS=REF=ORD=ESP, COL=BOT=NAV, NAP=REV y MED≈GOT; ANT copia el español |
| `semanaCorta` (203-206) | Propia para todos (hoy 18 en `else`) |
| `atras` (215-220) | Único; hoy LAB=COL=NAV y BOT=SED |
| `prefijo` (221-226) | Único por tema |
| `clave` (237-249) | En la lengua o la jerga del tema. Hoy hay tres grupos: latín en ROM, MED y GOT; español en mayúsculas en DIS, ORD, URS, ESP, MON, REV y ANT; español en minúsculas en REF, LAB, COL, NAP, BOT, NAV, SED y AND. Pasa a: NAP en francés, SED en chino, AND en árabe, URS en ruso… |
| Marcas del calendario (`Temas.kt:96-114`) | Para CIB, HEL, DIS, REF, ORD, LAB y COL; ampliar las de SED |

### Tipografía (`Disenios.kt`, `Tema.kt`, `LibroDiario.kt`)

- **Letra de título única** por tema. Hoy comparten COL y REV (Fell) y ROM y ANT (Cinzel).
- **Letra de texto única** por tema. Hoy:
  - Garamond: HEL, MED, COL, BOT, NAV, SED, GOT y ANT;
  - Plex Sans: ROM, DIS, ORD, URS y MON;
  - Cormorant: REN y REV;
  - Plex Mono: CIB y REF.

  Se reparten entre familias distintas, todas recortadas al alfabeto latino (unos 60 KB cada una): Crimson, Lora, Libre Baskerville, Cardo, Alegreya, EB Garamond, Spectral, Source Serif, IBM Plex Serif, Work Sans, Barlow…
- **Letra de etiqueta:** es la de texto o la de título de cada tema, salvo en los temas de terminal.
- **`tipografia()`** se ajusta por tema (pesos, tamaños, espaciado) e incluye `displayLarge` (Enfoque).
- **`radioDe`** (`Tema.kt:232`): un valor o una forma única por tema, por ejemplo `CutCornerShape` en ORD y DIS. Hoy DIS=URS, CIB=REV, ORD=GOT=REN, MED=NAP=ANT, HEL=SED, NAV=MON y REF=AND.
- Lo mismo en `pagina().texto` y en `LibroDiario.fuentes`.

### Frases y otros textos

- **Frases** (`Frases.kt`): al menos 20 por tema. Hoy tienen menos: MON 10, AND 13, ORD 13, REF 14, GOT/NAV/REN/SED 15, ESP/REV/URS 17 y LAB/NAP 18.
- **`textoGuardar`:** único por tema. Hoy HEL=BOT y MED=COL.
- **`Libros.pista`** y **`pistaSueno`:** por tema.

## Lote 6: sonido y vibración

- **Escala del toque** (`Sentidos.kt:302-310`, `herramientas/afinados.py`): propia para cada tema. Hoy 12 no tienen y hay parejas ROM=ESP, CIB=GOT y HEL=COL. También `ruidoso` y `CUARTAS` (312-322) pasan a ser por tema.
- **Tecla** (375-384): `s_tecla_<id>.ogg` propio. Hoy hay 4 grupos y BOT, NAV y MON caen en `else`.
- **Vibración:** propia en elegir, abrir y cambio (42-58), y también en **error** (48), que hoy es común a todos.
- **Sonidos nuevos por tema:** `s_<id>_error` y `s_<id>_cambio`. Hoy error no suena y cambio reutiliza el sonido de abrir (348).
- **Rascar** (387-390): un sonido y una vibración por metal o tema, en lugar de uno único.
- **Música** (`herramientas/musica.py`):
  - enriquecer la presentación y la música anual de ESP, ORD, URS y REF;
  - revisar también los tonos `t_*_llamada` y `t_*_notificacion` y los avisos `n_*`.
- **`n_lugar.ogg`** queda en un script.

## Lote 7: limpieza y cierre

- **Se eliminan:**
  - `Bases.kt` y los filtros `TrofeosTema.TEMAS`, `SalonesTema.TEMAS` y `MapasCampana.TEMAS`;
  - el respaldo `dibujarTrofeo` y `figura()` (`pantallas/Trofeos.kt:117,128,149-276`);
  - `salonDe()` y `fondoFamilia` (`HallImagen.kt:111`);
  - `Contenedor.ESPIRAL`, junto con su dibujo en `LibretaMomentos.kt:105-150,293` y en `Contenedores.kt:27,61,76,100,279,347`;
  - todos los `else` y las ramas agrupadas de diseño.
- `trofeosTemas` en `Capturas.kt:925` pasa a recorrer `TemaId.entries`.
- Se corrigen los KDoc que dicen «los siete temas», «temas base», etc., y el KDoc de `data/Modelo.kt:683-684`.
- Se borran los colores muertos de `res/values/colors.xml` y `herramientas/__pycache__`.
- Las pruebas `CoberturaTemas` y `UnicidadTemas` quedan sin excepciones, y `TEMAS.md` queda todo en P.

## Lote 8: Terminal y 13 temas nuevos (de 21 a 34)

Va después del lote 7. Para entonces el compilador y las pruebas de cobertura y unicidad obligan a que cada tema nuevo esté completo. Se hace un PR por tema.

### 8a. El Cyberpunk pasa a «Terminal»

- **Se conserva el id `"ciber"`.** Está guardado en la configuración, en `Campana.tema`, en `Carta.tema` y en `FraseGuardada.tema` (`Modelo.kt:257,518,918`).
  - Solo cambian `nombre` y `descripcion`.
  - Las campañas aceptadas antes se volverán a dibujar con el mapa de Terminal, y las frases guardadas dirán «Terminal» (`FrasesGuardadas.kt:101`). Es aceptable y se avisa en el PR.
- **Concepto:** pura consola. Monocromo cian sobre negro, cursor y ASCII.
  - Pierde el magenta, que pasa a Ciudad neón junto con el ámbar y el verde de sodio.
  - No usa fósforo verde ni ámbar, porque eso ya es del **Refugio de noche** (`juegoTonos` 258, `tipoRefugioOscuro` 113, `pagina` 887).
  - Tampoco usa el «barrido», que es de REF. Su fondo vivo es un **volcado de memoria**: columnas de hexadecimal que caen.
- **Fuente:** se queda con Share Tech Mono; Ciudad neón usa otra.
- **Siguen siendo de CIB** su sonido (`s_ciber_*`), su widget, su sello y su ícono, con la paleta nueva.
- **Lo que se mueve a Ciudad neón:**
  - la llovizna de neón;
  - la «otra vida» de Kei (`OtrasVidas.kt:496`);
  - el dron del gato;
  - el título de campaña «OP: NEON RUN» (`ciencia/Campanas.kt:138`).
- **Sigue siendo solo oscuro.**

### 8b. Los 13 temas nuevos

En la tabla, «se distingue de» dice qué lo separa de los temas parecidos. Todos los nombres, lugares y personajes son inventados.

| id · nombre | Concepto y paleta | Fecha y números | Emblema · libreta · campaña | Se distingue de |
|---|---|---|---|---|
| `pirata` · Pirata | Carta náutica, brea, lona, latón; sepia sobre pergamino húmedo | «Bitácora, singladura N»; horas por campanadas de guardia | Calavera con rosa de los vientos · bitácora atada con cabo · islas con «X» de tesoro | NAP y COL. COL deja de llamar a su libreta y a su trofeo «Diario de a bordo» (`Contenedores.kt:38`, `TrofeosMas.kt:38`) y pasa a «Cuaderno del pueblo» |
| `neon` · Ciudad neón | Niveles bajos de una megaciudad: neón sobre vapor, letreros en varios alfabetos, puestos de comida, callejones; magenta, ámbar, verde de sodio | Hora de la megaciudad, «nivel −N», distrito | Letrero de neón · ticket de un puesto · bajada por los niveles | Terminal (sin ciudad), ESP (cielo) y DIS (Estado) |
| `ruinas` · Ciudad verde | Ciudad tomada por la vegetación: hormigón agrietado, hiedra, musgo, óxido | «Día N tras el silencio», contado desde el primer registro; estación | Semáforo con hiedra · cuaderno de superviviente con hojas prensadas · calles tomadas por el bosque | REF (bajo tierra) y BOT (estudio de la naturaleza) |
| `urbe` · Roma | La ciudad de hoy en capas: placas de mármol de las calles, ocre, travertino, cúpulas, fuentes, motoneta, ruinas entre el tráfico, el papado como capa histórica | «Roma, sabato 17 ottobre 2026 · AUC MMDCCLXXIX»; barrios (rioni) | Placa de calle con loba · cuaderno con boletos de tranvía · paseo por los barrios | ROM (Imperio), REN (taller) y HEL |
| `templario` · Templario | Cruzada militar: cota de malla, sobreveste blanca con cruz roja, castillo de frontera, arena | Calendario juliano (13 días de diferencia); vigilias de la noche | Cruz patada sobre escudo · regla de la orden con sello · marcha de fortaleza en fortaleza | MED (copistas), GOT (catedral) y ORD (acero técnico) |
| `arcano` · Horror cósmico | Sectas y lo innombrable: papel de archivo manchado, tinta verde-negra, sellos, tentáculos sugeridos, faro en la niebla | Fase lunar calculada; numerales tachados y corregidos | Ojo dentro de un signo inventado · diario de investigador con recortes · costa hacia el faro | GOT (fe), ANT (fantasía heroica) y DIS (Estado) |
| `meso` · Mesoamérica | Códice en papel amate, jade, cinabrio, azul maya | Tzolk'in, haab y cuenta larga; números de puntos y barras | Glifo del día · códice plegado en biombo · calzada blanca (sacbé) entre ciudades | SED, MON y AND |
| `egipto` · Egipto | Papiro, lapislázuli, oro, cornalina | Calendario civil de 3 estaciones, 12 meses de 30 días y 5 días añadidos (aproximado); numerales jeroglíficos | Ojo protector · papiro con cartucho · el Nilo río arriba | ROM, HEL y AND |
| `edo` · Japón Edo | Washi, tinta sumi, ukiyo-e, bermellón de sello hanko, índigo | 72 microestaciones; meses tradicionales; kanji (reutiliza `Temas.chino`) | Sello hanko · cuaderno cosido a la japonesa · camino real de posta en posta | SED y MON |
| `cueva` · Prehistoria | Ocre, carbón, caliza, manos en negativo | Lunas contadas desde el primer registro; muescas en hueso | Mano en negativo · bolsa de piel · migración tras las manadas | ANT y BOT |
| `vapor` · Vapor victoriano | Latón, engranes, planos, ladrillo, hollín, gas de alumbrado | Hora de ferrocarril; numerales de manómetro | Engrane con manómetro · cuaderno de patentes · ruta ferroviaria | ORD, LAB y COL |
| `deco` · Art déco | Años veinte: negro, oro, marfil, abanicos geométricos, rascacielos, trasatlánticos | Entrada de cabaret o pasaje de barco; numerales escalonados | Abanico solar · libreta de laca · crucero de puerto en puerto | NAP, REN y URS |
| `abismo` · Abismo | En claro, zona iluminada azul; en oscuro, zona abisal con bioluminiscencia; batiscafo, sonar | Los días de racha se muestran como metros de profundidad y presión; «inmersión N» | Medusa luminosa · bitácora de batiscafo · bajada por capas | ESP (aquí se baja en lugar de subir) |

Los ids `egipto` y `edo` coinciden con ids de vidas de `BASE` (`OtrasVidas.kt:117,185`). Son espacios de nombres distintos (`TemaId` frente a `porId`), así que no chocan; se deja anotado en el KDoc.

**Fuentes candidatas** (OFL salvo que se diga otra cosa; a confirmar). Ninguna repite las que ya hay:

| Tema | Fuente |
|---|---|
| pirata | Pirata One |
| neon | Chakra Petch, con un subconjunto de Noto Sans JP/KR para los letreros |
| ruinas | Special Elite (Apache) |
| urbe | Titillium Web |
| templario | Uncial Antiqua o MedievalSharp. **No** Grenze Gotisch, que ya es la de MED (`gotica_*`) |
| arcano | Spectral |
| meso | Alegreya Sans SC, con los números de puntos y barras dibujados o de Noto Sans Mayan Numerals |
| egipto | El Messiri para el texto, y los jeroglíficos de Noto Sans Egyptian Hieroglyphs, que no trae letras latinas |
| edo | Shippori Mincho, recortada |
| cueva | Amatic SC |
| vapor | Abril Fatface |
| deco | Poiret One o Limelight |
| abismo | Barlow Condensed. **No** Exo 2, que se parece a Orbitron (ESP) |

### 8c. Todo lo que necesita cada tema nuevo `<id>` (con su alias `<Id>`)

**Kotlin**
- **Base del tema:**
  - el valor en `TemaId` (con `soloOscuro` si aplica);
  - `esquemaDe` en claro y oscuro, con los colores `inverse*`;
  - `tipoDe` y `tipografia()` (con `displayLarge`), `radioDe` y la vista previa (`titulo`, `linea`);
  - `juegoTonos` y `juegoCalor`.
- **`Estilo`:** comentario, `comentarioNuevo`, `mayusculas`, pantalla y sección, numeral, fecha, mes, semana, `semanaCorta`, marca, atrás, prefijo, widget, clave y `texto(clave)`.
- **Nombres:** `Temas` (nombres, marcas, rótulos de widget, el calendario propio) y `NombresNuevos`.
- **Decoración** (`Decoracion.kt`): emblema, `periodo`, `fondoTema`, la banda y `CapaFiesta`.
- **Componentes:** todos los del lote 3, más la barra inferior, el bloqueo y la transición.
- **Personaje:** atuendo y frases.
- **Marcas** (`Marcas.kt`) y **fondo de pantalla del teléfono** (`marca`, marco, `tipo`, `logo`).
- **Íconos de pestaña:** día, diario, registrar, vida, entender y ajustes.
- **Fondo vivo** (`Animaciones`) y **Relación** (`Relacion`).
- **Libros:** soporte, materia, `materiaNoche`, `paginaNoche`, objeto cerrado, página, adorno de página, estante (madera, lomo, rollo), encabezados, pistas, `rotuloSueno`, `colofonSueno`, `colofon` y `textoGuardar`.
- **Cartas:** papelería, `formaSobre`, sello, letra y textos.
- **Diario exportado** (`LibroDiario`): fuentes, capitular, filete y CSS.
- **Frases:** al menos 20, más el rótulo.
- **Sello** (`Huella`): `textoSello` y `recursosHuella`.
- **Música:** las 4 funciones.
- **Sentidos:** patrones, sonidos, escala, tecla, `ruidoso`, error, cambio y rascar.
- **Trofeos:** las 9 figuras, el pedestal y los 9 nombres.
- **Salón:** escena y pedestal, las piezas del hall, `METALES_HALL` y la sala de trofeos.
- **Campaña:** fondo, ruta, etapa, victoria, `puntos` y marcador, con `TITULOS` y `ETAPAS`.
- **Libreta:** el valor de `Contenedor` y sus 5 funciones.
- **Mapa:** `filtroMapa`, `Formas`, `MarcoMapa` y la rosa de los vientos.
- **Galerías:** `simboloCalificacion`, `Recuadro` y `TarjetaMapa`.
- **Mundo:** `lamina`.
- **Hoy:** frase, cifra, franja y corazón.
- **Día:** sufijo y fecha.
- **Tu año:** voz, volumen, cosas y `Rascar`.
- **Reportes:** cifra, ornamento y marca.
- **`EstiloGrafica`**, la **pantalla de arranque** y los **controles de Material**.
- **Widgets:** `colores()` y los títulos del modo discreto.
- **Avisos:** la entrada en `SONIDOS`, el color y los textos. **Enfoque:** las notificaciones.
- **Otras vidas:** su vida (8d).
- **Selector de temas** (`Sistema.kt`): su grupo de época.

**`res/`**
- **Ícono y sello** (`drawable-nodpi/`):
  - `logo_<id>.png` y `logo_fondo_<id>.jpg`, generados por `iconos.py`;
  - `huella_<id>.png` y `huella_fondo_<id>.jpg`, generados por `sellos.py`;
  - `papel_<id>.jpg` (720×1280), generado por el script de papeles.
- **Widgets** (`drawable/`): `widget_fondo_<id>`, `widget_boton_<id>`, `widget_boton_activo_<id>` y `widget_boton_ambar_<id>`, más `drawable-v31/widget_fondo_<id>`.
- **Lanzador:** `mipmap-anydpi-v26/ic_launcher_<id>.xml`, con `logo_fondo_<id>`, `logo_<id>` y `logo_mono`.
- **`AndroidManifest.xml`:** `<activity-alias android:name=".Icono<Id>" android:enabled="false" android:exported="true" android:icon/roundIcon="@mipmap/ic_launcher_<id>" android:targetActivity=".ui.MainActivity">`, con el intent-filter MAIN/LAUNCHER y `meta-data android.app.shortcuts=@xml/accesos`.
- **Sonidos** (`raw/`):
  - de `afinados.py` (función `<id>()`, `S['<id>']`, su llamada a `nuevos(...)` dentro de `azar_propio('<id>')` y la tupla `todos`): `s_<id>_{toque,guardar,logro,pasar,elegir,abrir,tema,error,cambio}.ogg`, `n_<id>_{aviso,solemne}.ogg`, `s_tecla_<id>.ogg` y `s_rascar_<id>.ogg`;
  - de `musica.py` (`P['<id>']` y `ARGS['<id>']`): `s_<id>_presentacion.ogg`, `s_<id>_anual.ogg`, `t_<id>_llamada.ogg` y `t_<id>_notificacion.ogg`.
- **Fuente** (`font/`): `<familia>_<peso>.ttf`, recortada y, si hace falta, renombrada.
- **Arranque** (`values/themes.xml`): `Theme.Vestigium.Arranque.<Id>`.

**`assets/`**
- `licencias/`: la línea de la fuente (o un texto de Apache aparte).
- `gato/<id>_{c,o}_*` y sus claves en `gato.json` (lote 9).

**`herramientas/`**
- `iconos.py` (en `TEMAS`, con su textura), `sellos.py`, el script de papeles, `afinados.py` y `musica.py`.
- La cadena del gato (9c).
- `fuentes/`: la fuente original y su licencia, y el ornamento si se extrae.

**Pruebas:** los ayudantes de captura que recorren `TemaId.entries`, y `CoberturaTemas` y `UnicidadTemas` (principio 1).

### 8d. La «otra vida» de cada tema nuevo (`data/OtrasVidas.kt`, `DE_TEMAS`)

**Qué lleva cada `Vida`:**
- Identidad: `id`, `nombre`, `rol`, `lugar`, `cuando`, `anio` y `edad`.
- Enlace con el tema: `dibujo = "tema"` y `tema = <id>`.
- Relato: 3 párrafos de `historia`.
- Agenda: `dia`, con tramos `t(desde, hasta, Hacer.…, "texto")` que cubren las 24 horas.
- Datos y fuentes: `datos` para las 24 `MEDIDAS` (cada dato con su certeza `m`, `e`, `r` o `i`) y `fuentes`.
- Si es inventada, además: `ficticia = true` e `inspiracion`.

**Reglas:**
- No repetir a las 12 vidas de `BASE` (Naa, Ipy, Lucio, Citlali, Kichizō, Mary, etc.).
- Las cifras y la agenda salen de fuentes que se comprueban al escribirlas. Lo que no se pueda comprobar se marca `r` (reconstruido), no `e`.

| Tema | id · nombre · rol | Lugar · cuándo · edad | Certeza | Agenda del día (`dia`) | Lo que destaca |
|---|---|---|---|---|---|
| Pirata | `grumete` · Tomás · Grumete | Balandro pirata en el Caribe · 1718 · 14 | e | 0–4 dormir · 4–8 trabajo «baldear la cubierta en la guardia del alba» · 8–9 comer · 9–12 trabajo «remendar velas y cabos» · 12–13 comer «galleta y grog» · 13–16 dormir · 16–20 trabajo «guardias cortas de la tarde» · 20–24 gente «canto y dados» | Sueño partido, mucho aire libre, alcohol diario, siempre la misma gente |
| Ciudad neón | `fideos` · Amaru · Vendedor de fideos en un puesto de callejón | Niveles bajos de una megaciudad · 2077 · 47 | i | 10–17 dormir · 17–18 cuidar «compra en el mercado flotante» · 18–4 trabajo «el puesto, entre vapor y letreros» · 4–5 comer «con los repartidores» · 5–10 libre | Cientos de clientes y pocos amigos; casi sin luz de día. **Kei** (`OtrasVidas.kt:496`) pasa de `ciber` a `neon` como segunda vida |
| Terminal | `operadora` · Carmen · Operadora de computadora central, turno de noche | Centro de cómputo de un banco · 1978 · 27 | e | 8–15 dormir · 15–17 cuidar · 17–18 libre · 18–21.5 gente · 21.5–22 camino · 22–6 trabajo «cargar cintas, vigilar la consola, imprimir listados» · 6–6.5 comer · 6.5–8 camino | Turno de noche ante una pantalla; sustituye a Kei como vida de `ciber` |
| Ciudad verde | `semillera` · Una recolectora · Recolectora de semillas | Una ciudad que el bosque recuperó · año 40 tras el silencio · 33 | i | 21–5.5 dormir · 5.5–6 comer · 6–11 trabajo «huerto en la azotea» · 11–13 camino «calles con hiedra» · 13–14 comer · 14–17 trabajo «secar y guardar semillas» · 17–19 cuidar · 19–21 gente «fogata» | Pasos altísimos, nada de pantallas, sueño según el sol |
| Roma | `restauradora` · Giulia · Restauradora de frescos | Roma · 2025 · 38 | m y e (ISTAT) | 23.5–7 dormir · 7–8 cuidar · 8–8.5 camino «en motoneta» · 8.5–13 trabajo «andamio en una iglesia» · 13–14 comer · 14–18 trabajo · 18–19.5 libre «paseo de la tarde» · 19.5–20.5 cuidar · 20.5–22 comer «cena tardía» · 22–23.5 gente | La vida de hoy más cercana a la tuya, en otra ciudad |
| Templario | `sargento` · Hermano Bernat · Sargento de una orden militar | Castillo de frontera en el Levante · 1250 · 29 | e (la Regla de la orden) | 20–0 dormir · 0–1.5 rezar «maitines» · 1.5–5.5 dormir · 5.5–6 rezar «prima» · 6–11 trabajo «caballos, armas, guardia» · 11–12 comer «en silencio, con lectura» · 12–17 trabajo «instrucción y muralla» · 17–18 rezar «vísperas» · 18–19 comer · 19–20 rezar «completas» | Sueño cortado por el rezo, dos comidas, silencio |
| Horror cósmico | `archivista` · El archivista · Archivista de un pueblo costero | Un pueblo de pescadores en la niebla · 1926 · 44 | i | 3–9 dormir «mal» · 9–10 comer · 10–17 trabajo «catalogar legajos» · 17–18 camino «por el muelle» · 18–19 comer · 19–3 libre «lee lo que no debería» | Poco sueño, casi sin gente, lectura altísima |
| Mesoamérica | `escriba_maya` · K'an · Escriba y pintor de códices | Una ciudad maya de las tierras bajas · hacia 750 · 30 | r | 21–5 dormir · 5–6 comer «atole» · 6–12 trabajo «pintar glifos en amate» · 12–13 comer · 13–16 trabajo «cuentas del calendario» · 16–18 rezar «ritual en el templo» · 18–19 comer «tamales y cacao» · 19–21 gente | Uno de los pocos que leen; no repite a Citlali |
| Egipto | `escriba_tumba` · Pay · Escriba de la tumba | Deir el-Medina · hacia 1200 a. C. · 34 | e (los registros de la aldea, que anotan hasta las ausencias) | 20–5 dormir · 5–6 comer · 6–6.5 camino «subir al valle» · 6.5–11 trabajo «anotar raciones, herramientas, ausencias» · 11–12 comer «siesta» · 12–16 trabajo · 16–17 camino · 17–18 cuidar · 18–20 gente | Semana de 10 días; casi nadie alrededor sabe leer. Una mujer escriba sería muy rara; si se prefiere una mujer, cantora del templo o tejedora |
| Japón Edo | `posadera` · Ochiyo · Sirvienta de una casa de té en una posta | Una posta del camino real entre Edo y Kioto · 1830 · 22 | e | 22–5 dormir · 5–7 cuidar «fuego y barrer» · 7–8 comer · 8–13 trabajo «servir té a los viajeros» · 13–14 comer · 14–21 trabajo «recibir viajeros, baños y cenas» · 21–22 libre | Cientos de caras de paso; no repite a Kichizō |
| Prehistoria | `pintora_cueva` · Ama · Pintora de cuevas | Un valle del suroeste de Europa · hace unos 17,000 años · 26 | r (estudios que atribuyen muchas manos en negativo a mujeres) | 20–6 dormir «junto al fuego» · 6–7 comer · 7–12 trabajo «cazar y recolectar» · 12–14 libre · 14–16 trabajo «moler ocre y carbón» · 16–18 rezar «pintar en lo hondo de la cueva, con lámparas de grasa» · 18–20 gente | Frío de glaciación; no repite a Naa |
| Vapor victoriano | `fogonero` · Albert · Fogonero de locomotora | Una línea del norte de Inglaterra · 1880 · 24 | e | 22–4.5 dormir · 4.5–5 comer · 5–5.5 camino · 5.5–17.5 trabajo «palear carbón, turno de 12 h» · 17.5–18 camino · 18–19 comer · 19–22 gente «la taberna» | Calorías altísimas, cientos de kilómetros sin caminar; no repite a Mary |
| Art déco | `pianista` · Lou · Pianista de un club de jazz | Nueva York · 1926 · 29 | e | 6–13 dormir · 13–14 comer · 14–17 libre «ensayar» · 17–19 cuidar · 19–20 comer · 20–21 camino · 21–4 trabajo «tocar en el club» · 4–6 gente «la sesión de después» | Música 8 h al día, vida de noche, alcohol en plena Prohibición |
| Abismo | `buzo_esponjas` · Nikos · Buzo esponjero de escafandra | Un barco esponjero frente a Florida · 1910 · 26 | e | 20–4.5 dormir «en cubierta» · 4.5–5 comer · 5–11 trabajo «inmersiones con casco de cobre» · 11–12.5 comer · 12.5–17 trabajo «limpiar y ensartar esponjas» · 17–18 comer · 18–20 gente «canciones de la isla» | Horas bajo el agua, tripulación pequeña y fija |
| Tierras Antiguas (le falta hoy) | `cartografa` · Una cartógrafa · Cartógrafa de una compañía de viaje | Una aldea de paso entre bosques y montañas · Luna de las Hojas · 30 | i | 21–5 dormir · 5–6 comer · 6–12 camino «por el sendero» · 12–13 comer · 13–17 camino · 17–19 trabajo «dibujar el mapa del día» · 19–21 gente «canciones junto al fuego» | Kilómetros altísimos, cero pantallas |

Notas:
- El KDoc de `OtrasVidas` («una más por tema») pasa a «una o dos». `visibles()` y `ciencia/TuAnio.kt:153` ya aceptan varias vidas.
- El `tema` de Kei pasa a `"neon"`. Su `id` no cambia: se comprueba con grep que nada guardado dependa de su tema.
- La medalla (`pantallas/OtrasVidas.kt:200-215`) sale sola con el emblema.
- El paper no describe estas vidas (solo aparecen como galería en `apendiceD_cambios.md` D.48).

### 8e. Efectos de pasar a 34 temas

- **Selector de temas** (`Sistema.kt:265-280`) y **fichas de rotación** (291-303): agrupados por época (Antigüedad, Edad Media, Edad Moderna, siglos XIX–XX, Futuro, Fantasía).
- **Rotación** (`Rotacion.kt`): automática.
- **Peso de la APK.** Cada tema suma entre 1 y 1.5 MB (sonidos, imágenes, papel, fuente y gato), así que se suman entre 13 y 20 MB. Para compensar: bajar la tasa de bits de los OGG largos, recortar las fuentes, y valorar `shrinkResources`. No hay minificación hoy.
- **Datos:** no cambia el esquema. Se actualiza la lista de ejemplos del KDoc de `Modelo.kt:683-684`.

## Lote 9: el gato del tema (banda de Día)

### Qué existe

El gato está en la rama `claude/quirky-thompson-3km48y` (3 commits encima de `main`, **todavía sin mezclar**).

- **Código:** `ui/Gato.kt`. Lo llama `pantallas/Dia.kt:249` (`GatoDelDia`); el interruptor está en `Modelo.kt:704`.
- **Ánimo:** `Gato.humor()` lo calcula con el sueño y el ánimo del día. `Animo` puede ser NORMAL, ALEGRE, TRISTE o ENOJADO, y además el gato puede estar cansado o ser de noche.
- **Animaciones:** 13 comunes (caminar, quieto, sentarse, sentado, echarse, dormir, saltar, asearse, jugar, enojado, caminar triste, quieto triste y girar), más 2 propias de cada tema, que están en el diccionario `ESP` de `herramientas/gato/gato17.py:461`.
- **Arte:** lo dibujan `herramientas/gato/gato8.py` a `gato18.py`, y `exportar.py` lo pasa a `assets/gato/` (unos 7.4 MB para 41 claves).
- **Capturas:** `gatosClaro` y `gatosOscuro`.

**Requisito:** mezclar esa rama antes del lote 9, o construir encima de ella.

### 9a. Completar los gatos que ya existen

- **Fondo que se mueve** para los 8 que hoy lo tienen quieto:
  - ROM: fuente;
  - GOT: luz de vitral y vela;
  - MED: polvo en un rayo de luz y vela;
  - ORD: estandartes;
  - BOT: hojas y hormiga;
  - SED: banderines y caravana;
  - AND: fuente;
  - REN: molino y pájaros.
- **Noches propias:**
  - ESP y NAV tienen hoy el juego de claro y oscuro idéntico byte a byte;
  - el fondo de GOT es igual de día y de noche;
  - las noches de ROM, MED, BOT, ORD, SED, AND y REN son solo un recoloreado (`gato18.py:89-97`, `NOCHE_FONDO`), y pasan a dibujarse como noche.
- **Fondos casi planos:** se enriquecen AND, SED, BOT, ORD y HEL de día.
- **Atlas del gato idéntico entre claro y oscuro** en AND, BOT, ORD, REN y SED: se separan.
- **Objetos compartidos:**
  - SED toma té de una tetera, en lugar de la taza de REF;
  - AND huele un azahar, en lugar de la flor de BOT;
  - COL toca la campana del pueblo, en lugar del martillo de URS;
  - REV lanza el gorro frigio, en lugar del papel de NAP.
- **Terminal:**
  - pelaje monocromo cian;
  - mantiene «teclea» y las líneas de escaneo (`post`);
  - cambia el dron por «persigue el cursor»;
  - en `exportar.py:210`, la sombra cian fija pasa a leerse del tema.
- **Cómo se hace:** un script `herramientas/gato/gato19.py` que siga la cadena (importa `gato18` y cambia solo lo suyo), una fila más en `README.md`, y volver a exportar.

### 9b. El gato de cada tema nuevo

Todos heredan las 13 animaciones comunes y la reacción al ánimo. Lo propio de cada tema es el pelaje y el material, el accesorio, el fondo de día y de noche con lo que se mueve, y 2 acciones.

| Tema | Pelaje y material | Accesorio | Fondo claro / oscuro (lo que se mueve) | Sus 2 acciones |
|---|---|---|---|---|
| Pirata | Atigrado naranja, grabado sepia de carta náutica | Parche y pañuelo | Cubierta con mar / farol y estrellas (olas, gaviotas) | Abre un cofre y saca una moneda · sigue a una gaviota |
| Ciudad neón | Negro con reflejos magenta y ámbar, contorno de luz (se suma como pantalla) | Chaqueta impermeable con capucha | Callejón con letreros en varios alfabetos y un puesto, de día con smog ámbar / de noche con neón (lluvia, vapor, letreros que parpadean) | Sorbe fideos con palillos · sigue al dron de reparto |
| Ciudad verde | Tricolor, acuarela con musgo | Collar de cuerda con una hoja | Autos cubiertos de hiedra y semáforo apagado / luciérnagas (hojas que caen) | Huele un hongo en el asfalto · cuida un brote en una lata |
| Roma | Atigrado gris, fresco sobre estuco ocre | Pañuelo | Callejón con ropa tendida, motoneta, cúpula, columnas en ruinas con gatos / farolas y cúpula iluminada (ropa al viento, palomas) | Come un helado · persigue una paloma |
| Templario | Blanco y gris, esmalte heráldico | Sobreveste con cruz y cota de malla | Muralla de frontera / hogueras del campamento (estandartes, fuego) | Alza el estandarte blanco y negro · afila la espada |
| Horror cósmico | Negro con ojos que brillan, aguatinta verde-negra | Amuleto con signo inventado | Costa con faro en la niebla / luna, el faro gira y a ratos asoma un tentáculo (niebla, haz del faro) | Ve algo en el agua y eriza el lomo · olfatea un símbolo de tiza |
| Mesoamérica | Jaguarundi rojizo, códice (contorno grueso, color plano sobre amate) | Collar de jade y una pluma | Pirámide en la selva / Venus y fogata (guacamayas, humo) | Bebe cacao espumoso · sigue a un colibrí |
| Egipto | Mau moteado, pintura de tumba de perfil | Collar ancho de cuentas (usekh) y arete | El Nilo con papiros / techo de tumba azul con estrellas (barca, agua) | Toca el sistro · sigue a un ibis |
| Japón Edo | Bobtail blanco con manchas, ukiyo-e con degradado bokashi | Collar rojo con cascabel | Puente, monte, olas / farolillos (pétalos o nieve según la estación) | Levanta la pata como gato de la suerte · mira saltar un koi |
| Prehistoria | Pintura rupestre en ocre y carbón | Collar de conchas | Entrada de la cueva / manos y bisontes en la pared (el fuego titila) | Sopla ocre y deja la silueta de su pata · sigue una chispa |
| Vapor victoriano | Atigrado gris, latón y plano técnico | Gafas de aviador y chaleco | Fábrica con chimeneas / farolas de gas (humo, engranes) | Baja una palanca de vapor · sigue a un dirigible |
| Art déco | Negro, laca con líneas de oro | Pajarita y perlas | Rascacielos y trasatlántico / reflectores (barren el cielo) | Toca la trompeta · se abanica con plumas |
| Abismo | Gris azulado, bioluminiscente de noche (se suma como luz) | Casco de buzo de latón con ojos de buey (no la escafandra de ESP) | Zona iluminada con peces / zona abisal con medusas (burbujas, medusas que laten) | Nada entre burbujas · sigue la luz de un pez abisal |

### 9c. Cómo se hace

**Script `herramientas/gato/gato20.py`** (o uno por tanda). Para cada tema nuevo:
- una entrada en `g8.BANDAS` (`gato8.py:2341`). Sin ella, `exportar.py:176` nunca exporta el tema;
- una rama en `tema()` con los colores de su esquema (como en `gato8.py:520`), su `rayas` y su `material`. El material es un pintor nuevo si el estilo no existe: códice, pintura de tumba, ukiyo-e, rupestre, fresco, laca déco o bioluminiscencia;
- su accesorio;
- sus fondos: `g8.FONDOS` y `g9.NUEVOS`;
- lo que se mueve: `g9.ANIM[(<id>, o)]`;
- su entrada en `g17.ESP` y en `g17.COL` (y en `EXTRA_COL` y `g18.ESCALA_ESP` si hace falta), y en `AJUSTAR` y `SIN_SOMBRA`;
- si es «pantalla» o «solo oscuro»: las condiciones `n == "ciber"` de `exportar.py:179,186,210,221,252` pasan a leerse de la definición del tema.

**Exportar:** `python3 herramientas/gato/exportar.py app/src/main/assets/gato <id>`, un tema a la vez. Todos tardan unos 20 minutos por cada 21 temas.

**Revisar:** las hojas y los GIF en `herramientas/gato/salida/` (no se suben), y después las capturas `gatosClaro` y `gatosOscuro` con alto calculado.

**Prueba:** `CoberturaTemas` comprueba que `gato.json` tenga `<id>_c` y `<id>_o` (solo `_o` si es `soloOscuro`) con sus 2 acciones especiales.

## Archivos principales

- **Kotlin** (`app/src/main/java/io/github/ruddy2003/vestigium/`):
  - Base de los temas: `ui/` `Tema.kt`, `Disenios.kt`, `Estilo.kt`, `Temas.kt`, `NombresNuevos.kt`, `Frases.kt` y `Bases.kt` (se borra).
  - Decoración, animación y personaje: `ui/` `Decoracion.kt`, `Componentes.kt`, `Animaciones.kt`, `Celebracion.kt`, `Personaje.kt` y `Marcas.kt`.
  - Cartas, libros y diario: `ui/` `Relacion.kt`, `Libros.kt`, `Cartas.kt` y `LibroDiario.kt`.
  - Sonido e íconos: `ui/` `Sentidos.kt`, `Musica.kt`, `IconosPestana.kt` e `IconosCinta.kt`.
  - Fondo de pantalla, huella y gato: `ui/` `FondosTelefono.kt`, `Huella.kt` y `Gato.kt`.
  - Actividades: `ui/` `MainActivity.kt`, `QuickActivity.kt`, `EnfoqueActivity.kt`, `PrivacidadActivity.kt`, `Deshacer.kt`, `EditarSueno.kt` y `Selectores.kt`.
  - Salones, trofeos, mapas y contenedores: `ui/` `Salones*`, `Trofeos*`, `Mapas*` y `Contenedores*`.
  - Pantallas: todas las de `ui/pantallas/`, por las gráficas. Con cambios propios de tema: `Hoy`, `Dia`, `Mundo`, `TuAnio`, `TuAnioVisual`, `Hall`, `HallImagen`, `Trofeos`, `Campanas`, `Mapa`, `Galerias`, `Biblioteca`, `Bibliotecas`, `Sistema`, `LibretaMomentos`, `OtrasVidas` y `FrasesGuardadas`.
  - Fuera de `ui/`:
    - `reportes/Reportes.kt` y `widget/Widgets.kt`;
    - `avisos/Avisos.kt` y `data/Enfoque.kt` (solo notificaciones);
    - `ciencia/Campanas.kt` (títulos y etapas);
    - `data/OtrasVidas.kt` (vidas);
    - `data/Modelo.kt` (solo el KDoc).
- **Recursos:**
  - `res/drawable-nodpi/{logo,logo_fondo,huella,huella_fondo,papel}_*`;
  - `res/drawable/{ic_stat,widget_*}` y `res/drawable-v31/`;
  - `res/mipmap-anydpi-v26/`, `res/raw/`, `res/font/` y `res/values/themes.xml`;
  - `AndroidManifest.xml`;
  - `assets/gato/` y `assets/licencias/`.
- **Scripts:** `herramientas/iconos.py`, `arte.py`, `sellos.py` (nuevo), el script de papeles (nuevo), `afinados.py`, `musica.py` y `gato/*.py`.
- **Pruebas:** `Capturas.kt`, y las nuevas `CoberturaTemas.kt` y `UnicidadTemas.kt`.
- **CI:** `.github/workflows/apk.yml`, para poder compilar sin publicar.
- **Intocables:** la lógica de `data/`, la exportación CSV y R, y `docs/paper/`.

## Verificación (en cada lote)

1. **Rasters:** correr el script de `herramientas/` en local (hay PIL y numpy; `fontTools` se instala con pip), y revisar la hoja de contacto de N temas antes de hacer commit. En la huella, comparar antes y después y comprobar que el ornamento quedó intacto.
2. **Compilar:** aquí no hay SDK de Android. Se hace push de la rama del lote y se lanza el trabajo de **solo compilar** (lote 0) y `capturas.yml` con `workflow_dispatch` sobre esa rama. Si falla la compilación, se revisa la rama `ci-log`. No se lanza `apk.yml` con publicación desde ramas que no sean `main`.
3. **Pruebas:** `CoberturaTemas` y `UnicidadTemas` en verde para las superficies del lote, con la lista de excepciones más corta que antes. Además, `fallas.txt` y `errores.txt` vacíos en la rama `capturas` (el trabajo tiene `|| true`, así que no avisa solo).
4. **Capturas:** revisar en la rama `capturas` cada tema, en claro y en oscuro, de la superficie que se tocó.
5. **Grep:** sin `else ->` ni ramas agrupadas `TemaId.X, TemaId.Y` en los archivos de diseño del lote. `TEMAS.md` actualizado.
6. **PR hacia `main`** solo después de revisar las capturas. Al mezclar, el Release dice qué versión salió.

**Orden:** 0 → 1 (huella) → 2 (los 6 temas, un PR cada uno; CIB ya como Terminal) → 3 → 4 → 5 → 6 → 7 → 8 (primero Terminal y Ciudad neón, después un PR por tema nuevo, cada uno con su gato y su vida). El lote 9a puede ir en paralelo en cuanto se mezcle la rama del gato.
