# Los temas: superficie por superficie

_Matriz de trabajo del plan de diseño (`docs/diseno/PLAN_TEMAS.md`). Se creó en el lote 0, el 29 oct 2026, con el código de `main` (v94) y la auditoría del plan. Cada PR de diseño marca aquí las celdas que vuelve propias y su fila en «Estado por lote»._

## Cómo se lee

Cada fila es una **superficie** (algo que se ve, se oye o se lee y que puede cambiar con el tema); cada columna, un tema. En cada celda:

| Letra | Quiere decir | Ejemplo |
|---|---|---|
| **P** | **Propio**: una figura o un texto que no comparte con ningún otro tema y que no es solo un cambio de color (principio 2 del plan). | El emblema del Romano. |
| **S** | **Simple** o pobre: tiene versión propia, pero se queda corta (casi plana, un solo trazo, un texto que no dice nada del tema). | El fondo del sello de Colonos, crema liso. |
| **C** | **Compartido**: igual o casi igual que el de otro tema. La nota de la fila dice con cuál. | La lluvia de celebración de Ciber y Distopía (cuadritos en los dos). |
| **G** | **Genérico**: cae en un `else`, en un respaldo «por familia» o es igual para todos los temas. | La medalla de logro. |
| **✗** | **Falta**: no existe todavía (un recurso, una entrada, un sonido). | `s_<id>_error.ogg`. |
| **·** | No aplica. | La lámina de rascar en un tema que no la usa. |

Las abreviaturas de las columnas son las del plan:

| ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| romano | ciber | heleno | medieval | distopia | refugio | orden | laboratorio | colonos | napoleon | botanico | navidad | seda | gotico | espacio | andalus | mongol | renacimiento | urss | revolucion | antiguas |

Van en el orden de `TemaId` (el que usan las pruebas y las capturas).

## Qué vigila el código y qué se revisa a ojo

- **Las pruebas** (`app/src/test/.../CoberturaTemas.kt` y `UnicidadTemas.kt`) miden lo que se puede medir: entradas en los mapas, recursos, sonidos, ícono, gato, frases, nombres, letras, esquinas, vibración. Su lista de pendientes es la fuente de verdad de esas filas; aquí se copian para tenerlas a la vista. Si una fila de esta matriz y la lista de la prueba no coinciden, manda la prueba.
- **La cuenta de despachos** (`CoberturaTemas.despachosPorTema`) dice, por archivo, cuántas ramas juntan temas, cuántas comparaciones `== TemaId.X` o `in setOf(TemaId.…)` y cuántos `else ->` en un `when (tema)` quedan. Ninguna cuenta puede subir; al final del lote 7 quedan en cero.
- **Los dibujos** se revisan en las capturas por tema (`Capturas.kt`, sección «capturas por tema»). Se piden con la entrada `superficies` de `capturas.yml`, por ejemplo `campana,libreta`, o `todas`. Cada imagen se llama `io.github.ruddy2003.vestigium_Capturas_tema<Superficie>_<tema>_<claro|oscuro>.png` en la rama `capturas`. Las superficies son: `trofeos`, `salon`, `piezas`, `hallImagen`, `campana`, `libreta`, `fondoVivo`, `fiesta`, `relacion`, `huella`, `icono`, `mapaFormas`, `calificacion`, `reporte`, `reporteMes`, `vozAnio`, `calendario`, `constelacion`, `medalla`, `graficas`, `dialogo`, `barra`, `enfoque`, `bloqueo` y `cartas`; desde el lote 3, además, `campanaVictoria`, `decoracion`, `componentes`, `previa`, `fondoTelefono`, `personaje`, `lamina`, `librosAnio`, `cosasAnio`, `galerias`, `bibliotecas`, `widgets`, `dia`, `hoy`, `cartaAbierta`, `hojaLibreta` y `libros`. La del registro rápido (`quick`) no se puede capturar todavía: sus pantallas son funciones privadas de `QuickActivity`; se agrega en el lote 4, cuando su hoja salga a un componente. Los diálogos tampoco: Paparazzi no dibuja otra ventana, así que `dialogo` muestra por ahora la barra de Deshacer.
- **El estado de las pruebas** de cada corrida de capturas queda en la rama `capturas`: `cobertura.txt`, `unicidad.txt` y `despachos.txt`, listos para pegar en la prueba.

## Cómo se usa en cada lote

1. Antes de empezar, busca en esta matriz las filas del lote y anota en el PR qué celdas va a cambiar.
2. Al terminar, cambia esas celdas (normalmente a **P**), borra de las listas de pendientes de las pruebas lo que ya no falta y baja las cuentas de despachos de los archivos que tocaste.
3. Lanza `capturas.yml` con las superficies del lote y revisa cada tema en claro y en oscuro.
4. Marca la fila del lote en «Estado por lote» y recalcula el resumen: `python3 herramientas/resumen_temas.py docs/diseno/TEMAS.md`.

## Estado por lote

| Lote | Qué hace | Estado |
|---|---|---|
| 0 | Base de trabajo: esta matriz, pruebas de cobertura y unicidad, capturas por tema, CI sin publicar, hoja de contacto de N temas, colores de Deshacer | hecho 29 oct 2026 |
| 1 | Una sola huella (ícono, sello, monocromo, notificaciones) | hecho 29 oct 2026 |
| 2 | Los 6 temas sin diseño completo (CIB como Terminal, REF, ORD, LAB, ESP, URS) | hecho 29 oct 2026 |
| 3 | Completar las superficies que ya existen | hecho 29 oct 2026 |
| 4 | Pantallas genéricas, gráficas, controles y arranque | pendiente |
| 5 | Textos y tipografía | pendiente |
| 6 | Sonido y vibración | pendiente |
| 7 | Limpieza y cierre: sin `else`, sin ramas agrupadas, pruebas sin pendientes, todo en P | pendiente |
| 8 | Terminal y 13 temas nuevos (de 21 a 34) | pendiente |
| 9 | El gato de cada tema | pendiente (la rama del gato aún no está en `main`) |

## Resumen por tema

Cuántas superficies de la matriz están en cada estado (sin contar los «no aplica»). Sirve para ver de un vistazo qué temas están más lejos del 100 %.

| Tema | P | S | C | G | ✗ |
|---|---|---|---|---|---|
| ROM | 99 | 1 | 8 | 10 | 4 |
| CIB | 94 | 1 | 8 | 11 | 6 |
| HEL | 100 | 1 | 6 | 10 | 5 |
| MED | 100 | 2 | 5 | 10 | 5 |
| DIS | 95 | 3 | 6 | 10 | 6 |
| REF | 94 | 3 | 7 | 10 | 6 |
| ORD | 93 | 4 | 9 | 10 | 6 |
| LAB | 96 | 6 | 5 | 10 | 5 |
| COL | 95 | 3 | 9 | 10 | 5 |
| NAP | 93 | 3 | 8 | 11 | 5 |
| BOT | 98 | 1 | 7 | 12 | 4 |
| NAV | 91 | 4 | 8 | 12 | 5 |
| SED | 96 | 4 | 7 | 11 | 4 |
| GOT | 99 | 2 | 6 | 11 | 4 |
| ESP | 96 | 3 | 6 | 11 | 4 |
| AND | 100 | 3 | 4 | 11 | 4 |
| MON | 97 | 3 | 5 | 12 | 5 |
| REN | 99 | 2 | 4 | 12 | 5 |
| URS | 96 | 5 | 5 | 11 | 5 |
| REV | 97 | 2 | 8 | 11 | 4 |
| ANT | 96 | 1 | 9 | 11 | 5 |

Desde el lote 2 ningún tema cae en los respaldos de trofeos, salón, campaña ni libreta.

## Lote 1 · Una sola huella

La huella del ícono pasa a ser la única (ícono, monocromo, notificaciones, sello del día, bloqueo, fondo vivo sin captura).

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Ícono: textura del fondo | `herramientas/iconos.py` (`TEMAS`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 1: una textura propia y tenue por tema (travertino, pantalla, mármol de Paros, vitela, papel, hormigón, acero cepillado, cuadrícula, gaceta, mármol, herbario, fieltro con nieve, seda, piedra, polvo de estrellas, lazo de ocho, fieltro de yurta, trazos de construcción, cartel, verjurado y pizarra). |
| Ícono: tinta y anillo con el motivo del tema | `herramientas/iconos.py` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 1: tinta del tema con acabado (dorado, ferrogálica, fósforo, esmalte o tinta) y un anillo fino de radio y grosor fijos con su motivo (laurel, barra de carga, perlas, cuadrifolios, muescas, engrane, remaches, escala, cuerda, estrellas, hojas, copos, monedas, tréboles, órbita, estrella de ocho, rombos, compás, estrellas rojas, tricolor y runas). Algunos motivos comparten figura y cambian en número y tamaño (DIS y LAB: muescas; NAP, AND y URS: estrellas). |
| Sello del día: la huella es la del ícono | `herramientas/sellos.py`, `ui/Huella.kt` (`recursosHuella`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 1: la huella maestra a la escala y en la posición de la vieja, con el material de cada tema; los ornamentos se conservan. |
| Sello del día: fondo | `huella_fondo_<id>.jpg` (`sellos.py`, `fuentes/fondo_sello_<id>.jpg`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 1: COL (gaceta con columnas), DIS (grano y sellos tenues), NAV (nieve fina) y REF (líneas de barrido) se enriquecieron; ESP no hacía falta. |
| Sello del día: texto de la racha | `Estilo.racha` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 1: antes «racha de N días» en todos. |

## Lote 2 · Los 6 temas sin diseño completo

CIB, REF, ORD, LAB, ESP y URS caían en respaldos por familia (`salonDe`, `dibujarTrofeo`, `Contenedor.ESPIRAL`). La prueba `CoberturaTemas` los vigila (`trofeos`, `salon`, `campana_mapa`, `libreta`); desde el lote 2 esas cuatro superficies no tienen pendientes.

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Trofeos: figuras de las 9 categorías | `ui/TrofeosTema.kt`, `ui/TrofeosMas.kt`, respaldo `pantallas/Trofeos.kt:117` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: los 6 ya tienen sus figuras (ASCII en consola, chapa con el átomo, piezas forjadas, vidrio de laboratorio, misiones espaciales, medallas soviéticas). |
| Trofeos: pedestal | `ui/TrofeosTema.kt:71` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: los 6 con pedestal propio. Lote 3: ROM (basa de travertino con tabla ansada) y REN (losa sobre balaustres) se separan. |
| Trofeos: nombres | `TrofeosTema.nombre` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: los 6 con nombres que no repite nadie. Lote 3: Panteón (ROM), Arco de la Estrella (NAP) y Nocturlabio (REN); ninguno se repite. |
| Salón del hall: escena | `ui/SalonesTema.kt`, `ui/SalonesMas.kt`, respaldo `salonDe` (`pantallas/Hall.kt:79`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: sala de computadoras centrales (CIB), sala común con puertas de engranaje (REF), sala capitular de acero (ORD), laboratorio limpio (LAB), cúpula del observatorio (ESP) y estación de metro (URS). |
| Campaña: mapa, ruta y etapas dibujadas | `ui/MapasCampana.kt`, `ui/MapasMas.kt` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: traza de red, corte del refugio, mapa táctico, microplaca, sistema solar y Transiberiano. |
| Campaña: títulos y nombres de etapas | `ciencia/Campanas.kt:132-178` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: etapas nuevas de CIB, DIS, REF, ORD, LAB y URS. Lote 3: siete etapas en todos, alineadas con sus puntos; BOT «Sabana», NAP «Kehl» y «Schönbrunn»; ningún nombre se repite. |
| Libreta de momentos (contenedor) | `contenedorDe` (`ui/Contenedores.kt:45`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: consola, carpeta, códice técnico, cuaderno de laboratorio, bitácora y cuaderno escolar. Lote 3: el libro de horas (GOT) se separa del códice (MED) en papel y encuadernación, y el manuscrito (AND) de los mapas (ANT) en encuadernación; `papel` ya no tiene respaldo. |
| Imagen del hall: escena | `pantallas/HallImagen.kt:53` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 2: los 6 ya usan su salón (antes `fondoFamilia`). |

## Lote 3 · Completar las superficies que ya existen

Lo que ya tiene diseño por tema, pero compartido, por grupos o pobre. Desde el lote 3, los archivos de estas superficies no tienen ramas agrupadas, comparaciones ni `else` por tema (la prueba `despachosPorTema` pasó de 52 cuentas a 13). Quedan fuera de P: la materia y la página de noche de 18 temas (S), el papel sin imagen de 10 temas (S) y la letra del diario exportado de SED y ANT, que comparten familia (C).

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Campaña: marcador | `MapasMas.kt:218` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: legionario con el vexilo (ROM), vela en su palmatoria (MED), peregrino con venera (GOT) y estilete de punta de plata (REN). Lote 2: cursor, ficha amarilla, banderín, pipeta, nave y locomotora. |
| Campaña: victoria | `MapasCampana.kt:154`, `MapasMas.kt:188` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: una escena por tema en un solo `when` (corona de laurel en ROM, sol de Vergina en HEL, cruz de Santiago en MED, campana en GOT; enriquecidas COL, NAP, BOT, NAV, SED, AND, MON, REN, REV, ANT y DIS). |
| Campaña: puntos de la ruta | `MapasMas.puntos` (`:27`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `MapasMas.puntos` devuelve `List<Offset>` con siete paradas propias en los 21 temas; ya no hay zigzag. |
| Salón: atrio (ROM) y stoa (HEL) | `SalonesTema.kt:99-122` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: atrio con compluvio, pilastras corintias y paneles pompeyanos; stoa con el Egeo al fondo, escudos de bronce y columnas jónicas. Los demás, desde el lote 2. |
| Sala de trofeos: pared, repisa, destellos y placa | `pantallas/Trofeos.kt:140, 280, 309-322` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `tonoPared`, `paredTema`, `repisaTema`, `chispa` y `Placa` por tema (mármol, riel de servidores, estante de vidrio, anaquel de bambú, repisa de chimenea…). |
| Hall: techos de ciudad | `pantallas/Hall.kt:155` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: un `when` de 21 ramas. |
| Hall: templo | `pantallas/Hall.kt:192` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: uno por tema (anfiteatro, torre de servidores, ger, trilito, invernadero, árbol…). |
| Hall: borde de medalla y sello de pasaporte | `pantallas/Hall.kt:104, 171` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: uno por tema; `pieza` ya no usa `salonDe`. |
| Hall: las otras 17 piezas y sus metales | `pantallas/Hall.kt:204-319`, `METALES_HALL` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `trazoTema` (grosor, punta, guiones, línea doble y material) en las 17; estandarte, moneda, libro y sobre con figura propia; las otras 13 comparten figura y cambian trazo y material. `metalHall` tiñe el metal del nivel con el matiz del tema. |
| Imagen del hall: marca | `HallImagen.kt:59` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `marcaHall` con marca y colofón en la voz de cada tema. |
| Fondo vivo de la app | `ui/Animaciones.kt:101-114` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: un solo despacho (`vivoTema`) para la app y el teléfono, con un movimiento propio por tema; el barrido queda solo en REF. |
| Fondo de pantalla vivo del teléfono | `ui/FondosTelefono.kt:408-413` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: el mismo de la app (`pintarVivoTema`); ya no se contradicen. |
| Lluvia de celebración | `ui/Decoracion.kt:1297` (`CapaFiesta`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: 21 ramas; separadas CIB/DIS, HEL/ORD y NAP/URS; MED con gotas de tinta y COL con brasas. |
| Celebración de logro: rayos y nombres de nivel | `ui/Celebracion.kt:113-140` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `motivoLogro` y `nivelesLogro` por tema. |
| Transición entre pantallas | `ui/MainActivity.kt:435-484` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: HEL, LAB, NAV, ANT, COL, ORD, NAP, MON, REN y SED cambiaron; ya ninguna se parece a otra. |
| Cambio de tema, entrada y presión | `ui/Animaciones.kt:50-65, 227-248` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `matizDe` da por tema la forma de la apertura, la entrada de las secciones y el resorte del botón. |
| `periodo` de la decoración | `ui/Decoracion.kt:65-71` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: un valor distinto por tema. |
| Tarjeta de relación (marco, adorno y lazo) | `ui/Relacion.kt:78-169` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: recuadro, adorno y lazo propios en los 21. |
| Hoy: frase del día | `pantallas/Hoy.kt:319-370` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: un marco por tema. |
| Hoy: cifras del tablero | `Hoy.kt:520` (`estiloCifra`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: una letra por tema; NAP y REV comparten la casa Bodoni (derecha y espaciada frente a cursiva). |
| Hoy: franja de los últimos días | `Hoy.kt:695` (`FranjaDias`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: una barra por tema. |
| Hoy: corazón de frase guardada | `FrasesGuardadas.kt:53` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: una forma por tema (`formaCorazon`). |
| Día: sufijo del título y tamaño de la fecha | `pantallas/Dia.kt:192, 202` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `Estilo.sufijoTitulo` y `Estilo.fechaGrande`. |
| Mundo: lámina para rascar | `pantallas/Mundo.kt:201` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `laminaRascar` y `teselaLamina`: metal y motivo grabado por tema; `TuAnio.Rascar` usa la misma. |
| Tu año: voz | `pantallas/TuAnio.kt:126` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: repasada; AND con la hégira (`Temas.hegira`) y sin «Crónica» repetida. |
| Tu año: libros del año | `pantallas/TuAnioVisual.kt:62-133` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `volumen` con 21 ramas; ya no usa `Bases.de`. |
| Tu año: cosas del año, rascar y meses de ánimo | `TuAnioVisual.kt:138-187`, `TuAnio.kt:225-262` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: ficha propia y tres objetos por tema; la lámina de Mundo; `MesesAnimo` con `Estilo.mes`. |
| Libros: objeto cerrado del diario | `ui/Libros.kt:307` (por `soporte`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `formaObjeto` por tema, con su letra y su pista (rollo con umbilicus en ROM, boletín con lacre en NAP, tableta de control en ESP…). |
| Libros: materia y página de noche | `Libros.kt:166-190` | S | P | S | S | S | P | S | S | S | S | S | S | S | S | P | S | S | S | S | S | S | Lote 3: cada tema tiene su cielo y su luz (`nocheDe`), pero 18 temas siguen oscureciendo materia y página con la misma fórmula. |
| Libros: cielo de noche y encabezado del sueño | `Libros.kt:237, 1042` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: luna, estrellas y letra del sueño por tema. |
| Libros: papel de la página | `Libros.kt:868` (`pagina`) | P | S | P | P | S | S | S | S | S | S | P | S | P | P | S | P | P | P | S | P | P | Lote 3: HEL deja el papiro de ROM (pergamino dibujado) y cada tema tiene su letra de página. Siguen sin imagen de papel los que no la tenían. |
| Libros: adorno de la página | `Libros.kt:903` (`FondoPagina`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: nuevo en ROM, HEL, SED, LAB y DIS; COL y URS separados. |
| Libros: estante (madera) | `Libros.kt:641` | P | · | P | P | · | · | P | P | P | · | P | · | P | P | · | P | P | P | P | P | P | Lote 3: `TablaEstante` con color y canto por tema. |
| Libros: lomo o rollo | `Libros.kt:698-760` | P | · | P | P | · | · | P | P | P | · | P | · | P | P | · | P | P | P | P | P | P | Lote 3: nervios, escudo, código de barras y tejuelo; el rollo de ROM con cornua y sillybos; el mes sale de `Estilo.mes`. |
| Libros: encabezado de página | `Libros.kt:1022` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: rótulo propio además del emblema. |
| Libros: «guardar» y textos de lectura y escritura | `Libros.kt:254, 1127-1216` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: los 21 `textoGuardar` son distintos; textos de día vacío, noche sin sueño, «Lo mejor / Lo peor» y ayuda del escritor por tema. |
| Libros: pistas | `Libros.kt:275-287` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: por tema (`pistaDia`, `pistaNoche`). |
| Cartas: forma del sobre y sello | `ui/Cartas.kt:142` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `formaSobre` (solapa, esquinas y cierre), `adornoSobre` y `selloCarta` por tema. |
| Cartas: papelería | `Cartas.kt:110` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `papeleria` con una rama por tema; textos en `vozCarta`. |
| Diario exportado: fuentes | `ui/LibroDiario.kt:130` | P | P | P | P | P | P | P | P | P | P | P | P | C | P | P | P | P | P | P | P | C | Lote 3: separadas COL/REV y ROM/ANT. SED y ANT usan la misma familia Almendra (regular y negrita). |
| Diario exportado: capitular | `LibroDiario.kt:279, 392` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `inicial` y `conInicial`, con el mismo criterio en el diario y en la carta. |
| Diario exportado: filete y CSS | `LibroDiario.kt:158-188, 266, 387` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `filete`, `marco` y `cssTema` por tema. |
| Emblema | `ui/Decoracion.kt:90-460` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: nuevos o mejorados en CIB, HEL (sol de Vergina animado), MED, REF, LAB, NAV y AND; NAP con la N imperial y abejas. |
| Textura de fondo (`fondoTema`) | `Decoracion.kt:1023-1228` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: ROM (mármol), HEL (guijarros) y REF de día (papel tapiz atómico); también en Privacidad y en el registro rápido. |
| Banda bajo los títulos | `ui/Componentes.kt:90-127` (`Meandro`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: ovas y dardos en ROM; DIS, LAB, COL, SED y MON enriquecidas. |
| Sección (título, separador, ▸/▾) | `Componentes.kt:183-245` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: estilo, separador y par de glifos por tema. |
| Botones principal y secundario | `Componentes.kt:506-521` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: forma y adorno por tema. |
| Tarjeta, Vacío, Aviso, Escala, Opciones, Interruptor, Etiquetas, Editor, Cifra, FilaMenu y Pastilla | `ui/Componentes.kt` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `pielDe(t)` con los rasgos de cada tema; el editor deja la barra de «archivo de código» (tablilla de cera, consola, pluma y tintero, máquina de escribir…). |
| Personaje: atuendo | `ui/Personaje.kt:86-108` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: 21 sombreros y 21 objetos sin repetir; `YELMO` en ANT; `LAUREL` y `CAPUCHA_NEON` se borraron. |
| Personaje: frases y globo | `Personaje.kt:65-77, 345` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: frases (`Voz`) y globo por tema. Hoy la tarjeta no se usa en ninguna pantalla; solo sale en las capturas. |
| Marcas de @persona y #tema | `ui/Marcas.kt:54-79` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `Forma` pasa de 7 a 31; la forma de la persona es distinta en cada tema. |
| Fondo del teléfono: marca | `ui/FondosTelefono.kt:149-156` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: una por tema. |
| Fondo del teléfono: marco | `FondosTelefono.kt:194-215` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: uno por tema. |
| Íconos de pestaña: día y diario | `ui/IconosPestana.kt:66-148` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: HEL con reloj de sol; CIB con papel de teletipo. |
| Íconos de pestaña: registrar y ajustes | `IconosPestana.kt:72-73` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: propios en los 21; «vida» deja de ser igual a «más»; `gastos()` se borró. |
| Botones de modo, buscar y ajustes; texto «Más» | `Decoracion.kt:1236-1380` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `formaBoton`, `astroModo`, `lupaTema` y `textoMas` por tema (el engrane sale de `IconoPestana`). |
| Barra inferior: indicador, botón central y divisor | `MainActivity.kt:198-234` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: los tres por tema; nombres de las pestañas desde los diccionarios del tema. |
| Pantalla de bloqueo | `MainActivity.kt:176-186` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: textos y composición por tema (once disposiciones); el sello no cambia. |
| Vista previa del tema | `ui/Tema.kt:381-415` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: forma de la tarjeta y cuadritos por tema; SED pasa a «Etapa». |
| Mapa: filtro de colores | `pantallas/Mapa.kt:76-146` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `when` exhaustivo con rama de ROM. |
| Mapa: formas de los lugares | `Mapa.kt:166-206` (`Formas`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: una forma con detalle por tema. |
| Mapa: marco y rosa de los vientos | `Mapa.kt:559-590` (`MarcoMapa`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `marcoDelMapa` y `rosaDelMapa` por tema. |
| Galerías: símbolo de calificación | `pantallas/Galerias.kt:105` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: ORD cruz de malta, NAP abeja, URS engrane con estrella. |
| Galerías: recuadro, mapa y tarjeta de mapa | `Galerias.kt:170-273` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: con el marco, las formas y la rosa del tema. El `Recuadro` lo usan también Campañas, Logros, Tu año y Otras vidas. |
| Bibliotecas: íconos de pestañas y portada de respaldo | `Bibliotecas.kt:171`, `Biblioteca.kt:150` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: cada pictograma lleva el símbolo de calificación del tema; la portada, su letra y un adorno propio. |
| Reportes: cifra | `reportes/Reportes.kt:56` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `letraCifra` con un archivo de letra distinto por tema. |
| Reportes: ornamento | `Reportes.kt:111` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: una figura por tema. |
| Reportes: marca y mes | `Reportes.kt:146, 555, 855` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `marcaReporte` por tema; el mes sale de `Estilo.mes` (hereda lo que ese despacho tenga, lote 5). |
| Widgets: colores | `widget/Widgets.kt:28` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P |  |
| Widgets: forma | `res/drawable/widget_*`, `drawable-v31` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: silueta, filete y adorno por tema en `layer-list`; la copia de `drawable-v31` ya no impone el radio del sistema. |
| Widgets: títulos del modo discreto y letra del calendario | `Widgets.kt:146, 161, 281` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `tituloDiscreto` por tema; el calendario usa la letra del tema. Los rótulos repetidos entre temas (ESTADO, DESCANSO…) viven en `Estilo`/`Temas` y van en el lote 5. |
| Notificaciones: color y textos | `avisos/Avisos.kt:133-364`, `data/Enfoque.kt:221-266` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `setColor` con el primario del tema; voz por tema en `Avisos.voz` y `vozEnfoque`; el fin de bloque de Enfoque suena con el aviso del tema (un canal por tema). |
| Accesos directos | `res/drawable/ic_sc_*` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Lote 3: `AccesosDirectos.publicar` los genera con el color del tema; `accesos.xml` queda vacío. Los accesos ya fijados en la pantalla de inicio quedan deshabilitados y hay que volver a fijarlos. |

## Lote 4 · Pantallas genéricas, gráficas, controles y arranque

Hoy son iguales en todos los temas salvo colores, letra y radio.

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Calendario, cinta, constelación, medalla de logro y barras de Otras vidas | `Calendario.kt:208`, `Cinta.kt`, `IconosCinta.kt`, `Constelacion.kt`, `Logros.kt:77`, `OtrasVidas.kt:426-464` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G |  |
| Gráficas (punto, trazo, rejilla, eje, cifras) | todas las pantallas con gráficas | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Falta `EstiloGrafica(tema)`. |
| Diálogos, deslizadores, progreso, menús, barra de Deshacer y selectores de fecha | 29 `AlertDialog`, `Selectores.kt`, `ui/Deshacer.kt` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | La barra de Deshacer ya toma sus colores del tema (lote 0). |
| Hoja del registro rápido | `ui/QuickActivity.kt:135, 239, 403, 736` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | 6 dp fijos y sin textura. |
| Enfoque: esfera y tira | `ui/EnfoqueActivity.kt:113-159` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | El reloj usa `displayLarge`, que ningún tema define. |
| Pantalla de arranque | `res/values/themes.xml` | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | No hay `Theme.Vestigium.Arranque.<Id>`; la prueba `arranque` lo vigila. |
| Presentación del tema | `ui/Musica.kt:208-238` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Composición y «toca para seguir» iguales; cambian emblema, frase y música. |
| Selector de temas | `pantallas/Sistema.kt:265-306` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | En orden del enum, de 3 en 3; las vistas previas usan `modoOscuro(ROMANO)`. |

## Lote 5 · Textos y tipografía

Las filas con valor comparable las vigila `UnicidadTemas` (y las que faltan, `CoberturaTemas`).

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Diccionario de nombres de pantalla | `ui/Temas.kt`, `ui/NombresNuevos.kt` | P | ✗ | P | S | P | P | P | S | S | P | P | S | S | P | P | S | P | P | S | P | P | CIB no tiene; NAV, URS, SED y AND son pobres; MED, LAB, COL, NAV, SED y URS tienen entradas iguales al español. |
| Prefijo del comentario | `Estilo.comentario` (`Estilo.kt:22`) | C | C | C | P | P | P | C | P | P | C | P | P | P | P | P | P | P | P | P | P | P | ROM = CIB = ORD («// »); HEL = NAP («— »). |
| Comentario bajo el título | `Estilo.comentarioNuevo` (`Estilo.kt:50`) | G | G | G | P | P | P | P | P | P | G | G | G | G | G | G | G | G | G | G | G | G | 12 temas caen en `else`; ROM, CIB y HEL no ponen comentario. |
| Numeral | `Estilo.numeral` (`Estilo.kt:98`) | C | P | P | P | P | P | C | C | P | C | G | G | P | P | P | P | G | G | P | P | C | Con 17: ORD = LAB, NAP = ROM = ANT; BOT, NAV, MON y REN escriben la cifra sin más. GOT y MED no coinciden con ROM, pero se le parecen (plan). |
| Mes | `Estilo.mes` (`Estilo.kt:159`) | P | C | P | P | S | C | C | S | C | C | C | C | P | P | C | P | S | P | P | C | P | CIB, REF, ORD y ESP: español abreviado; COL, BOT y NAV: español largo; REV = NAP (francés). DIS, LAB y MON son pobres. GOT y ROM no coinciden, pero los dos van en latín. |
| Semana | `Estilo.semana` (`Estilo.kt:185`) | P | C | P | P | C | C | C | S | C | C | C | C | P | P | C | P | P | P | P | C | C | CIB = DIS = REF = ORD = ESP = ANT; COL = BOT = NAV; NAP = REV. MED y GOT casi iguales; LAB copia el español. |
| Semana corta (widget del mes) | `Estilo.semanaCorta` (`Estilo.kt:203`) | P | G | P | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | 18 temas caen en `else`; la de CIB sale igual que esas. |
| «Atrás» | `Estilo.atras` (`Estilo.kt:215`) | P | P | P | P | P | P | P | C | C | P | C | C | C | P | P | P | P | P | P | P | P | LAB = COL = NAV; BOT = SED. |
| Nombres de pantalla | `Estilo.pantalla` con 12 títulos comunes (`UnicidadTemas.PANTALLAS`) | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | Todos los temas repiten al menos uno con otro tema (casi siempre el español en mayúsculas). |
| Prefijo de etiqueta | `Estilo.prefijo` (`Estilo.kt:221`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P |  |
| Claves «clave = valor» | `Estilo.clave` (`Estilo.kt:237`) | C | P | P | C | C | C | C | C | C | C | C | C | C | C | C | C | C | P | C | C | C | Latín en ROM, MED y GOT; español en mayúsculas en DIS, ORD, URS, ESP, MON, REV y ANT; español en minúsculas en REF, LAB, COL, NAP, BOT, NAV, SED y AND. |
| Marcas del calendario | `Estilo.marca`, `Temas.MARCAS` | P | ✗ | ✗ | P | ✗ | ✗ | ✗ | ✗ | ✗ | P | P | P | S | P | P | P | P | P | P | P | P | SED tiene solo dos. |
| Letra de título | `tipoDe` (`ui/Tema.kt`) | C | P | P | P | P | P | P | P | C | P | P | P | P | P | P | P | P | P | P | C | C | COL = REV (Fell); ROM = ANT (Cinzel). |
| Letra de texto | `tipoDe` | C | C | C | C | C | C | C | P | C | P | C | C | C | C | P | P | C | C | C | C | C | Garamond: HEL, MED, COL, BOT, NAV, SED, GOT, ANT; Plex Sans: ROM, DIS, ORD, URS, MON; Cormorant: REN, REV; Plex Mono: CIB, REF. |
| Radio de las esquinas | `radioDe` (`Tema.kt:232`) | P | C | C | C | C | C | C | P | P | C | P | C | C | C | P | C | C | C | C | C | C | DIS = URS (0); CIB = REV (1); ORD = GOT = REN (2); MED = NAP = ANT (3); HEL = SED (6); NAV = MON (8); REF = AND (10). |
| Frases del día (al menos 20) | `ui/Frases.kt` | P | P | P | P | P | S | S | S | P | S | P | S | S | S | S | S | S | S | S | S | P | Con menos de 20: MON 10, AND 13, ORD 13, REF 14, GOT, NAV, REN y SED 15, ESP, REV y URS 17, LAB y NAP 18. |

## Lote 6 · Sonido y vibración

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Escala del toque | `Sentidos.acordeToque` (`ui/Sentidos.kt:302`) | C | C | C | ✗ | ✗ | ✗ | ✗ | P | C | ✗ | P | ✗ | ✗ | C | C | P | ✗ | ✗ | ✗ | ✗ | ✗ | Sin escala: 12 temas. ROM = ESP, CIB = GOT, HEL = COL. |
| Tecla del diario | `Sentidos.tecla` (`:375`) | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | 4 sonidos para 21 temas (máquina, pluma, digital, suave). Falta `s_tecla_<id>`. |
| Vibración (elegir, abrir, cambio, error) | `Sentidos.patron` (`:41`) | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | El error es igual en todos; elegir, abrir y cambio van por grupos o caen en `else`. |
| Sonidos de error y de cambio | `res/raw/s_<id>_error`, `s_<id>_cambio` | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | El error no suena y el cambio reutiliza el de abrir. |
| Rascar | `Sentidos.rascar` (`:387`) | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Un solo sonido y una sola vibración. |
| Música: presentación y anual | `herramientas/musica.py` | P | P | P | P | P | S | S | P | P | P | P | P | P | P | S | P | P | P | S | P | P | El plan pide enriquecer ESP, ORD, URS y REF. |

## Lotes 8 y 9 · Recursos de cada tema, gato y otra vida

Lo que la prueba `CoberturaTemas` exige por tema. Los recursos que ya existen salen como P aunque su diseño cambie en otro lote.

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Alias del ícono, `ic_launcher_<id>`, `logo_*`, `huella_*` | `AndroidManifest.xml`, `res/mipmap-anydpi-v26`, `res/drawable-nodpi` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P |  |
| Papel `papel_<id>` | `res/drawable-nodpi` | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | P | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | P | P | Hoy los papeles van por material (`papel_papiro`, `papel_vitela`…), no por tema. |
| Drawables de widget | `res/drawable/widget_*`, `drawable-v31` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | ROM usa los nombres sin sufijo. |
| Los 13 sonidos del tema | `res/raw` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P |  |
| Gato de la banda de Día | `assets/gato/gato.json` | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | Está en la rama `claude/quirky-thompson-3km48y`, todavía sin mezclar. |
| Otra vida del tema | `data/OtrasVidas.kt` (`DE_TEMAS`) | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | ✗ |  |

## Fuera de la matriz

- **`logo_mono`, `ic_stat` y los mosaicos de ajustes rápidos** son uno solo para todos los temas, a propósito. Desde el lote 1 son la huella maestra (`ic_stat` lo genera `herramientas/notificacion.py`).
- **La vista previa del widget en el selector** es un XML estático y se queda en ROM (Android no deja cambiarla).
- **La miniatura del fondo vivo** (`res/xml/fondo_vivo.xml`) se queda fija por la misma razón.
- **`Bases.kt`** solo lo usa hoy `TuAnioVisual` (los libros del año); `Bases.TODAS` no lo usa nadie. Se borra en el lote 7.
- **Código que el lote 3 dejó sin uso o a medias** (se revisa en el lote 7): las bandas `Filete`, `Regla`, `Vineta`, `Monedas` y `Alhan` de `Decoracion.kt`; `TarjetaPersonaje` y `FiguraPersonaje`, que solo usan las capturas; `Avisos.SONIDOS`, que ahora se deriva de `sonidosDe` (la superficie `avisos_mapa` de la prueba ya no puede fallar); `salonDe` (por familia), que siguen usando `Hall.kt` y `HallImagen.kt` como ayudante; `soporte()`, que usa `Cartas.kt`.
- **Código muerto encontrado en la auditoría** (se quita en el lote 7): el `else -> PERGAMINO` de `ContenedoresMas.papel` (todos los contenedores tienen rama); la rama de DIS en `MapasMas.marcador` (la tapa un `if` anterior); los `else` de `cerradoMas`, `victoriaMas`, `escenaMas`, `pedestalMas` y `TrofeosMas.pedestalMas`, que nunca se alcanzan porque quien los llama ya filtra por `*.TEMAS`; el tipo «polen» de `FondoVivo`, que ningún tema usa en la app.
