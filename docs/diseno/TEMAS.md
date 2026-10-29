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
- **Los dibujos** se revisan en las capturas por tema (`Capturas.kt`, sección «capturas por tema»). Se piden con la entrada `superficies` de `capturas.yml`, por ejemplo `campana,libreta`, o `todas`. Cada imagen se llama `io.github.ruddy2003.vestigium_Capturas_tema<Superficie>_<tema>_<claro|oscuro>.png` en la rama `capturas`. Las superficies son: `trofeos`, `salon`, `piezas`, `hallImagen`, `campana`, `libreta`, `fondoVivo`, `fiesta`, `relacion`, `huella`, `icono`, `mapaFormas`, `calificacion`, `reporte`, `reporteMes`, `vozAnio`, `calendario`, `constelacion`, `medalla`, `graficas`, `dialogo`, `barra`, `enfoque`, `bloqueo` y `cartas`. La del registro rápido (`quick`) no se puede capturar todavía: sus pantallas son funciones privadas de `QuickActivity`; se agrega en el lote 4, cuando su hoja salga a un componente. Los diálogos tampoco: Paparazzi no dibuja otra ventana, así que `dialogo` muestra por ahora la barra de Deshacer.
- **El estado de las pruebas** de cada corrida de capturas queda en la rama `capturas`: `cobertura.txt`, `unicidad.txt` y `despachos.txt`, listos para pegar en la prueba.

## Cómo se usa en cada lote

1. Antes de empezar, busca en esta matriz las filas del lote y anota en el PR qué celdas va a cambiar.
2. Al terminar, cambia esas celdas (normalmente a **P**), borra de las listas de pendientes de las pruebas lo que ya no falta y baja las cuentas de despachos de los archivos que tocaste.
3. Lanza `capturas.yml` con las superficies del lote y revisa cada tema en claro y en oscuro.
4. Marca la fila del lote en «Estado por lote».

## Estado por lote

| Lote | Qué hace | Estado |
|---|---|---|
| 0 | Base de trabajo: esta matriz, pruebas de cobertura y unicidad, capturas por tema, CI sin publicar, hoja de contacto de N temas, colores de Deshacer | hecho 29 oct 2026 |
| 1 | Una sola huella (ícono, sello, monocromo) | pendiente |
| 2 | Los 6 temas sin diseño completo (CIB como Terminal, REF, ORD, LAB, ESP, URS), un PR por tema | pendiente |
| 3 | Completar las superficies que ya existen | pendiente |
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
| ROM | 36 | 4 | 27 | 47 | 6 |
| CIB | 34 | 4 | 23 | 48 | 9 |
| HEL | 35 | 4 | 25 | 49 | 7 |
| MED | 41 | 3 | 26 | 43 | 7 |
| DIS | 43 | 7 | 17 | 43 | 8 |
| REF | 34 | 7 | 19 | 49 | 9 |
| ORD | 26 | 6 | 23 | 56 | 9 |
| LAB | 29 | 9 | 16 | 58 | 8 |
| COL | 31 | 8 | 24 | 50 | 7 |
| NAP | 37 | 3 | 21 | 50 | 7 |
| BOT | 51 | 1 | 12 | 50 | 6 |
| NAV | 38 | 6 | 19 | 48 | 7 |
| SED | 45 | 7 | 15 | 47 | 6 |
| GOT | 44 | 3 | 24 | 43 | 6 |
| ESP | 34 | 4 | 17 | 56 | 7 |
| AND | 47 | 6 | 14 | 47 | 6 |
| MON | 47 | 4 | 14 | 48 | 7 |
| REN | 45 | 2 | 19 | 47 | 7 |
| URS | 29 | 6 | 19 | 58 | 8 |
| REV | 42 | 2 | 18 | 52 | 6 |
| ANT | 41 | 3 | 18 | 51 | 7 |

Los 6 temas del lote 2 (CIB, REF, ORD, LAB, ESP y URS) son los que más dependen de respaldos genéricos; BOT, AND, MON, SED y REN, los más completos.

## Lote 1 · Una sola huella

La huella del ícono pasa a ser la única (ícono, monocromo, notificaciones, sello del día, bloqueo, fondo vivo sin captura).

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Ícono: textura del fondo | `herramientas/iconos.py` (`TEMAS`) | P | C | C | C | P | C | C | C | C | P | P | C | P | P | C | C | C | C | C | C | C | Hoy hay 6 texturas, una por tema base; los demás usan la de su base con otro color (CIB, ESP, LAB, REF y URS la de DIS; MED, REN, ANT y ORD la de GOT; REV la de NAP; HEL la de ROM; MON y AND la de SED; COL y NAV la de BOT). |
| Ícono: tinta y filete con el motivo del tema | `herramientas/iconos.py` | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | Hoy la huella es de un solo color plano y no hay filete. |
| Sello del día: la huella es la del ícono | `ui/Huella.kt:84` (`recursosHuella`), `herramientas/arte.py` | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ | El ornamento de cada sello es propio y se conserva; lo que falta es cambiar la huella. |
| Sello del día: fondo | `huella_fondo_<id>.jpg` | P | P | P | P | S | P | P | P | S | P | P | S | P | P | P | P | P | P | P | P | P | REF y ESP: revisar si quedan planos. |

## Lote 2 · Los 6 temas sin diseño completo

CIB, REF, ORD, LAB, ESP y URS caen en respaldos por familia (`salonDe`, `dibujarTrofeo`, `Contenedor.ESPIRAL`). La prueba `CoberturaTemas` los vigila (`trofeos`, `salon`, `campana_mapa`, `libreta`).

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Trofeos: figuras de las 9 categorías | `ui/TrofeosTema.kt`, `ui/TrofeosMas.kt`, respaldo `pantallas/Trofeos.kt:117` | P | G | P | P | P | G | G | G | P | P | P | P | P | P | G | P | P | P | G | P | P | Los 6 dibujan el mismo trofeo genérico. |
| Trofeos: pedestal | `ui/TrofeosTema.kt:71` | C | G | P | P | P | G | G | G | P | P | P | P | P | P | G | P | P | C | G | P | P | ROM y REN comparten rama (solo cambia el color de la piedra). |
| Trofeos: nombres | `TrofeosTema.nombre` | C | ✗ | P | P | P | ✗ | ✗ | ✗ | P | C | P | P | P | P | ✗ | C | P | C | ✗ | P | P | «Arco de triunfo» ROM y NAP; «Cúpula» ROM y REN; «Astrolabio» REN y AND. Los 6 no tienen nombres. |
| Salón del hall: escena | `ui/SalonesTema.kt`, `ui/SalonesMas.kt`, respaldo `salonDe` (`pantallas/Hall.kt:79`) | P | G | P | P | P | G | G | G | P | P | P | P | P | P | G | P | P | P | G | P | P | Respaldo: CIB, REF, LAB y ESP salen idénticos (laboratorio); ORD, castillo; URS, taller. |
| Campaña: mapa, ruta y etapas dibujadas | `ui/MapasCampana.kt`, `ui/MapasMas.kt` | P | G | P | P | P | G | G | G | P | P | P | P | P | P | G | P | P | P | G | P | P | Los 6 usan la ruta en zigzag y la bandera genéricas. |
| Campaña: títulos y nombres de etapas | `ciencia/Campanas.kt:132-178` | P | S | P | P | S | P | S | S | P | P | P | P | P | P | P | P | P | P | S | P | P | LAB «Día 1…6», CIB «NODO 01…06», DIS «SECTOR n», URS no cuadra con el mapa nuevo, ORD termina en «Reliquia». |
| Libreta de momentos (contenedor) | `contenedorDe` (`ui/Contenedores.kt:45`) | P | G | P | C | P | G | G | G | P | P | P | P | P | C | G | C | P | P | G | P | C | Los 6 caen en `ESPIRAL`. MED y GOT (códice y horas) comparten papel y encuadernación; AND y ANT (manuscrito y mapas) comparten encuadernación. |
| Imagen del hall: escena | `pantallas/HallImagen.kt:53` | P | G | P | P | P | G | G | G | P | P | P | P | P | P | G | P | P | P | G | P | P | Los 6 usan `fondoFamilia` (por `salonDe`). |

## Lote 3 · Completar las superficies que ya existen

Lo que ya tiene diseño por tema, pero compartido, por grupos o pobre.

| Superficie | Dónde | ROM | CIB | HEL | MED | DIS | REF | ORD | LAB | COL | NAP | BOT | NAV | SED | GOT | ESP | AND | MON | REN | URS | REV | ANT | Nota |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Campaña: marcador | `MapasMas.kt:218` | G | G | P | G | P | G | G | G | P | P | P | P | P | G | G | P | P | G | G | P | P | Punto simple en ROM, MED, GOT, REN y los 6. |
| Campaña: victoria | `MapasCampana.kt:154`, `MapasMas.kt:188` | C | G | C | C | S | G | G | G | S | S | S | S | S | C | G | S | S | P | G | S | S | ROM y HEL comparten; MED y GOT comparten; los 6 usan la bandera genérica. REN, NAP y REV tienen la suya, pero el plan pide enriquecer las simples. |
| Campaña: puntos de la ruta | `MapasMas.puntos` (`:27`) | G | G | G | G | P | G | G | G | P | G | P | P | P | G | G | P | P | G | G | G | P | Los demás usan el zigzag de `pantallas/Campanas.kt:80`. |
| Salón: atrio (ROM) y stoa (HEL) | `SalonesTema.kt:99-122` | S | G | S | P | P | G | G | G | P | P | P | P | P | P | G | P | P | P | G | P | P | Pobres; se enriquecen. |
| Sala de trofeos: pared, repisa, destellos y placa | `pantallas/Trofeos.kt:140, 280, 309-322` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | La repisa es de la misma madera en todos. |
| Hall: techos de ciudad | `pantallas/Hall.kt:155` | C | C | C | C | C | C | C | C | G | C | G | G | C | C | C | C | C | C | G | G | G | Grupos: ROM·HEL·REN; AND·SED·MON; GOT·MED·ORD·NAP; CIB·ESP·LAB·DIS·REF. El resto cae en `else`. |
| Hall: templo | `pantallas/Hall.kt:192` | G | G | G | C | G | G | G | G | G | G | G | G | C | C | G | P | C | G | G | G | G | SED y MON comparten; GOT y MED comparten; los otros 16, el templo clásico. |
| Hall: borde de medalla y sello de pasaporte | `pantallas/Hall.kt:104, 171` | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | Por familia de `salonDe` (4 familias). |
| Hall: las otras 17 piezas y sus metales | `pantallas/Hall.kt:204-319`, `METALES_HALL` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Iguales en todos; los metales van por nivel, no por tema. |
| Imagen del hall: marca | `HallImagen.kt:59` | P | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Solo ROM escribe «VESTIGIVM MEI». |
| Fondo vivo de la app | `ui/Animaciones.kt:101-114` | G | P | C | G | G | S | G | G | G | G | P | P | P | P | P | P | P | P | G | G | G | HEL de noche usa las estrellas de ESP; REF solo tiene barrido de noche; 10 temas no tienen nada (y el «polen» no lo usa nadie). |
| Fondo de pantalla vivo del teléfono | `ui/FondosTelefono.kt:408-413` | C | P | C | C | C | C | C | P | C | C | P | P | P | P | P | P | P | C | C | C | C | «Polvo» en REN, HEL (claro), MED, ROM, COL, NAP, REV y ANT; «barrido» en REF, ORD, DIS y URS. No coincide con el fondo vivo de la app. |
| Lluvia de celebración | `ui/Decoracion.kt:1297` (`CapaFiesta`) | P | C | C | G | C | P | C | P | G | C | P | P | P | P | P | P | P | P | C | P | P | CIB y DIS: cuadritos; HEL y ORD: rayas; URS y NAP: estrellas; MED y COL: círculo del `else`. |
| Celebración de logro: rayos y nombres de nivel | `ui/Celebracion.kt:113-140` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | 16 rayos y «bronce, plata, oro, legendario» en todos. |
| Transición entre pantallas | `ui/MainActivity.kt:435-484` | C | P | C | P | P | P | P | C | C | P | P | C | P | P | P | P | P | P | P | P | C | Cada tema tiene su rama, pero HEL, LAB, NAV y ANT se ven casi iguales, y ROM se parece a COL (auditoría del plan). |
| Cambio de tema, entrada y presión | `ui/Animaciones.kt:50-65, 227-248` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | El mismo círculo y los mismos resortes; solo cambia el color. |
| `periodo` de la decoración | `ui/Decoracion.kt:65-71` | C | P | P | P | C | P | P | P | P | P | P | P | P | C | C | P | P | C | P | C | P | ROM = REN (6000), DIS = GOT (7000), ESP = REV (9000). |
| Tarjeta de relación (marco, adorno y lazo) | `ui/Relacion.kt:78-169` | C | C | C | C | C | C | C | C | C | C | P | P | P | P | C | P | P | C | C | C | C | Tarjetas idénticas: LAB = ESP; ORD = URS = REF; MED = NAP = COL = REN; ROM = HEL = REV = ANT. CIB y DIS comparten piezas con otros. |
| Hoy: frase del día | `pantallas/Hoy.kt:319-370` | G | C | G | P | P | C | C | C | G | G | G | G | G | G | G | G | G | G | G | G | G | REF, ORD, LAB y CIB comparten el marco; los otros 15, la raya a la izquierda. |
| Hoy: cifras del tablero | `Hoy.kt:520` (`estiloCifra`) | P | G | G | C | G | G | G | G | G | G | G | C | G | C | G | G | G | P | G | G | G | MED, NAV y GOT comparten Garamond. |
| Hoy: franja de los últimos días | `Hoy.kt:695` (`FranjaDias`) | P | P | G | C | G | G | G | G | G | G | G | G | G | C | G | G | G | G | G | G | G | MED y GOT comparten el arco. |
| Hoy: corazón de frase guardada | `FrasesGuardadas.kt:53` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G |  |
| Día: sufijo del título y tamaño de la fecha | `pantallas/Dia.kt:192, 202` | P | P | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | El «_» de CIB y la fecha grande de ROM son casos sueltos. |
| Mundo: lámina para rascar | `pantallas/Mundo.kt:201` | C | P | C | C | C | C | C | G | C | C | P | G | C | C | G | C | G | C | C | G | G | Oro: ROM, HEL, MED, GOT, AND, NAP, SED, URS; cobre: REN, COL; acero: DIS, REF, ORD; plata (`else`): LAB, NAV, ESP, MON, REV, ANT. |
| Tu año: voz | `pantallas/TuAnio.kt:126` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | Todos tienen rama; el plan pide repasarla (AND con fecha hégira, sin repetir «Crónica»). |
| Tu año: libros del año | `pantallas/TuAnioVisual.kt:62-133` | P | C | C | C | P | C | C | C | C | P | P | C | P | P | C | C | C | C | C | C | C | Van por `Bases.de`: cada tema toma el libro de su base. |
| Tu año: cosas del año, rascar y meses de ánimo | `TuAnioVisual.kt:138-187`, `TuAnio.kt:225-262` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G |  |
| Libros: objeto cerrado del diario | `ui/Libros.kt:307` (por `soporte`) | S | C | S | P | P | P | P | P | S | G | P | P | P | P | C | P | P | S | S | P | P | CIB y ESP comparten la terminal; NAP cae en el sobre genérico; el plan pide rehacer ROM, HEL, COL, REN y URS. |
| Libros: materia y página de noche | `Libros.kt:166-190` | G | P | G | G | G | P | G | G | G | G | G | G | G | G | P | G | G | G | G | G | G | Los 18 restantes oscurecen su color con la misma fórmula. |
| Libros: cielo de noche y encabezado del sueño | `Libros.kt:237, 1042` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Misma luna y estrellas; solo cambian el color y la letra en REF y CIB. |
| Libros: papel de la página | `Libros.kt:868` (`pagina`) | C | S | C | P | S | S | S | S | S | S | P | S | P | P | S | P | P | P | S | P | P | ROM y HEL comparten el papiro; 10 temas no tienen imagen de papel. |
| Libros: adorno de la página | `Libros.kt:903` (`FondoPagina`) | G | P | G | P | G | P | P | G | C | P | P | P | G | P | P | P | P | P | C | P | P | COL y URS comparten rama. |
| Libros: estante (madera) | `Libros.kt:641` | G | · | G | G | · | · | P | P | G | · | P | · | P | P | · | G | P | P | G | G | G | Madera por defecto; el estante no se dibuja en los temas de sobre, carpeta o pantalla. |
| Libros: lomo o rollo | `Libros.kt:698-760` | S | · | P | S | · | · | S | S | S | · | P | · | P | P | · | P | P | P | P | P | P | Lomo liso en MED, COL, ORD y LAB; rollo liso en ROM. |
| Libros: encabezado de página | `Libros.kt:1022` | G | P | G | G | P | P | G | P | G | G | G | G | G | G | G | G | G | G | P | G | G | Los 16 restantes ponen solo su emblema. |
| Libros: «guardar» y textos de lectura y escritura | `Libros.kt:254, 1127-1216` | P | P | C | C | P | P | P | P | C | P | C | P | P | P | P | P | P | P | P | P | P | `textoGuardar`: HEL = BOT («guardar») y MED = COL («asentar»). El texto de ayuda del escritor es el mismo en 20 temas. |
| Libros: pistas | `Libros.kt:275-287` | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | Una por soporte (5), no por tema. |
| Cartas: forma del sobre y sello | `ui/Cartas.kt:142` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Una sola geometría y una gota de cera con el emblema. |
| Cartas: papelería | `Cartas.kt:110` | G | C | G | G | P | P | G | G | G | C | G | C | G | G | C | G | G | G | G | G | G | NAP y NAV (sobre) y CIB y ESP (pantalla) comparten; los demás usan una fórmula. |
| Diario exportado: fuentes | `ui/LibroDiario.kt:130` | C | P | P | P | P | P | P | P | C | P | P | P | P | P | P | P | P | P | P | C | C | COL y REV comparten título (Fell); ROM y ANT, la letra de inscripción. |
| Diario exportado: capitular | `LibroDiario.kt:279, 392` | G | G | G | P | G | G | G | G | G | G | G | G | G | S | G | G | G | G | G | G | G | Solo MED en el diario; MED y GOT en la carta. |
| Diario exportado: filete y CSS | `LibroDiario.kt:158-188, 266, 387` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Mismo marco y mismo CSS; solo cambian colores y letra. |
| Emblema | `ui/Decoracion.kt:90-460` | P | S | S | S | P | S | P | S | P | C | P | S | P | P | P | S | P | P | P | P | P | NAP reutiliza el laurel de ROM; el plan pide mejorar CIB, HEL, MED, REF, LAB, NAV y AND. |
| Textura de fondo (`fondoTema`) | `Decoracion.kt:1023-1228` | G | P | G | P | P | S | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | ROM y HEL no tienen; REF solo de noche. Falta en `PrivacidadActivity` y `QuickActivity`. |
| Banda bajo los títulos | `ui/Componentes.kt:90-127` (`Meandro`) | C | P | C | P | S | P | P | S | S | P | P | P | S | P | P | P | S | P | P | P | P | La de ROM es casi la greca de HEL; el plan pide enriquecer DIS, LAB, COL, SED y MON. |
| Sección (título, separador, ▸/▾) | `Componentes.kt:183-245` | G | S | G | P | P | P | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | CIB solo cambia el color del número y el separador. |
| Botones principal y secundario | `Componentes.kt:506-521` | G | G | G | C | G | G | G | G | G | G | G | G | G | C | G | G | G | P | G | G | G | MED y GOT solo cambian el tamaño. |
| Tarjeta, Vacío, Aviso, Escala, Opciones, Interruptor, Etiquetas, Editor, Cifra, FilaMenu y Pastilla | `ui/Componentes.kt` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Solo cambian colores, letra y radio. El editor es una barra de «archivo de código» para todos. |
| Personaje: atuendo | `ui/Personaje.kt:86-108` | P | P | P | P | P | P | P | P | P | P | P | P | S | S | P | S | P | P | P | P | S | Ninguno es idéntico, pero ANT, SED, AND y GOT repiten sombrero u objeto (capucha, turbante, farol); `LAUREL` y `YELMO` no se usan. |
| Personaje: frases y globo | `Personaje.kt:65-77, 345` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G |  |
| Marcas de @persona y #tema | `ui/Marcas.kt:54-79` | C | C | C | P | C | P | C | C | C | P | C | C | C | C | C | C | C | P | C | C | C | HEL = AND (idénticas); NAV como HEL; GOT ≈ ANT; ORD ≈ REV; LAB ≈ BOT; CIB ≈ ESP; persona en sello en DIS, SED y URS; persona subrayada en ROM, MON, ORD y COL. |
| Fondo del teléfono: marca | `ui/FondosTelefono.kt:149-156` | P | P | P | C | P | P | G | G | G | G | G | G | G | C | P | P | P | C | P | P | P | MED, GOT y REN escriben lo mismo; 7 temas caen en «Vestigium Mei». |
| Fondo del teléfono: marco | `FondosTelefono.kt:194-215` | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | Hay 2 marcos: esquinas (CIB, ESP, LAB, REF, ORD) y doble filete (los demás). |
| Íconos de pestaña: día y diario | `ui/IconosPestana.kt:66-148` | C | G | C | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | El «día» de HEL se parece al sol de ROM; el «diario» de CIB es el documento genérico. |
| Íconos de pestaña: registrar y ajustes | `IconosPestana.kt:72-73` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | El mismo «+» y el mismo engrane. «más» es igual a «vida» en todos. |
| Botones de modo, buscar y ajustes; texto «Más» | `Decoracion.kt:1236-1380` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G |  |
| Barra inferior: indicador, botón central y divisor | `MainActivity.kt:198-234` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Solo cambian los íconos y el radio. |
| Pantalla de bloqueo | `MainActivity.kt:176-186` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Mismos textos y composición; el sello sí es del tema. |
| Vista previa del tema | `ui/Tema.kt:381-415` | P | P | P | P | P | P | P | P | C | P | P | P | C | P | P | P | P | P | P | P | P | COL y SED comparten el título «Jornada»; la tarjeta tiene 6 dp fijos. |
| Mapa: filtro de colores | `pantallas/Mapa.kt:76-146` | G | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | ROM cae en el caso por defecto. |
| Mapa: formas de los lugares | `Mapa.kt:166-206` (`Formas`) | S | C | S | C | S | S | S | C | S | C | P | C | S | P | S | S | C | C | C | P | P | Comparten forma CIB y LAB, MED y NAP, NAV y URS, MON y REN; solo GOT, BOT, REV y ANT tienen detalle. |
| Mapa: marco y rosa de los vientos | `Mapa.kt:559-590` (`MarcoMapa`) | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | C | Dos marcos; rosa de 8 puntas en AND y de 4 en el resto. |
| Galerías: símbolo de calificación | `pantallas/Galerias.kt:105` | P | P | P | P | P | P | C | P | P | C | P | P | P | P | P | P | P | P | C | P | P | ORD, URS y NAP son estrellas de 5 puntas. |
| Galerías: recuadro, mapa y tarjeta de mapa | `Galerias.kt:170-273` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G |  |
| Bibliotecas: íconos de pestañas y portada de respaldo | `Bibliotecas.kt:171`, `Biblioteca.kt:150` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G |  |
| Reportes: cifra | `reportes/Reportes.kt:56` | G | C | G | C | G | C | G | G | C | G | G | C | C | C | G | G | G | C | G | G | G | Grupos: REN·GOT·MED·SED·COL·NAV (letra de texto) y CIB·REF (mono). |
| Reportes: ornamento | `Reportes.kt:111` | C | C | C | G | C | C | C | G | G | G | G | G | G | G | G | G | G | G | C | G | G | ROM = HEL; CIB = REF; DIS = URS = ORD. |
| Reportes: marca y mes | `Reportes.kt:146, 555, 855` | P | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Solo ROM escribe su marca; el mes no usa `Estilo.mes`. |
| Widgets: colores | `widget/Widgets.kt:28` | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P | P |  |
| Widgets: forma | `res/drawable/widget_*`, `drawable-v31` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | En Android 12+ la copia de `drawable-v31` impone el radio del sistema a todos. |
| Widgets: títulos del modo discreto y letra del calendario | `Widgets.kt:146, 161, 281` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | «NVNC», «SCRIBERE» y monoespaciada en todos. |
| Notificaciones: color y textos | `avisos/Avisos.kt:133-364`, `data/Enfoque.kt:221-266` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Sin `setColor`; los textos no cambian con el tema (el sonido sí). |
| Accesos directos | `res/drawable/ic_sc_*` | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | Color fijo. |

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
| Numeral | `Estilo.numeral` (`Estilo.kt:98`) | C | P | P | C | P | P | C | C | P | C | G | G | P | C | P | P | G | G | P | P | C | Según el plan: ORD = LAB, NAP = ROM, ANT = ROM, GOT ≈ ROM ≈ MED; BOT, NAV, MON y REN sin numeral propio. |
| Mes | `Estilo.mes` (`Estilo.kt:159`) | C | C | P | P | S | C | C | S | C | C | C | C | P | C | C | P | S | P | P | C | P | CIB, REF, ORD y ESP: español abreviado; COL, BOT y NAV: español largo; REV = NAP (francés); GOT y ROM en latín; DIS, LAB y MON, pobres. |
| Semana | `Estilo.semana` (`Estilo.kt:185`) | P | C | P | C | C | C | C | S | C | C | C | C | P | C | C | P | P | P | P | C | S | CIB = DIS = REF = ORD = ESP; COL = BOT = NAV; NAP = REV; MED ≈ GOT; ANT y LAB copian el español. |
| Semana corta (widget del mes) | `Estilo.semanaCorta` (`Estilo.kt:203`) | P | P | P | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | G | 18 temas caen en `else`. |
| «Atrás» | `Estilo.atras` (`Estilo.kt:215`) | P | P | P | P | P | P | P | C | C | P | C | C | C | P | P | P | P | P | P | P | P | LAB = COL = NAV; BOT = SED. |
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

- **`logo_mono`, `ic_stat` y los mosaicos de ajustes rápidos** son uno solo para todos los temas, a propósito; en el lote 1 pasan a la huella maestra.
- **La vista previa del widget en el selector** es un XML estático y se queda en ROM (Android no deja cambiarla).
- **La miniatura del fondo vivo** (`res/xml/fondo_vivo.xml`) se queda fija por la misma razón.
- **`Bases.kt`** solo lo usa hoy `TuAnioVisual` (los libros del año); `Bases.TODAS` no lo usa nadie. Se borra en el lote 7.
- **Código muerto encontrado en la auditoría** (se quita en el lote 7): el `else -> PERGAMINO` de `ContenedoresMas.papel` (todos los contenedores tienen rama); la rama de DIS en `MapasMas.marcador` (la tapa un `if` anterior); los `else` de `cerradoMas`, `victoriaMas`, `escenaMas`, `pedestalMas` y `TrofeosMas.pedestalMas`, que nunca se alcanzan porque quien los llama ya filtra por `*.TEMAS`; el tipo «polen» de `FondoVivo`, que ningún tema usa en la app.
