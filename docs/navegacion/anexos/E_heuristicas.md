# Anexo E · Heurísticas de usabilidad (Fase 5 del plan de navegación)

Revisión hecha leyendo el código vigente (27 oct 2026) en `app/src/main/java/io/github/ruddy2003/vestigium/`. Las rutas de archivo se dan relativas a esa carpeta. No se modificó ningún archivo del repositorio. Todo lo que dice «sin teléfono no se puede confirmar» depende de cómo se ve o se siente en el aparato y no se puede deducir del código.

**Fuente de las heurísticas generales:** Nielsen, J. (1994, actualizado el 30 ene 2024). *10 Usability Heuristics for User Interface Design*. Nielsen Norman Group. https://www.nngroup.com/articles/ten-usability-heuristics/ — **enlace comprobado en la sesión** (WebFetch): sus títulos son los diez del § 7 del plan.

**Dato de contexto importante:** el tema por defecto es `"romano"` (`data/Modelo.kt:620`). Con ese tema, `Estilo.pantalla` (`ui/Estilo.kt:65-66`) pone de título el nombre en latín y deja el español como comentario pequeño («DIES // día»). Además, el botón de volver dice «← cd ..» (`ui/Estilo.kt:203-204`) y el botón de guardar del diario dice «SIGNARE» (`ui/Libros.kt:254-255`).

---

## 1. P7 · Gestos ocultos

El código no usa `SwipeToDismiss` ni `anchoredDraggable`. Hay 3 `combinedClickable`, 15 `pointerInput` y 1 toque largo de osmdroid. Todos los gestos están en la tabla.

| # | Pantalla | Archivo:línea | Gesto | Qué hace | ¿Hay otro camino visible? | Veredicto |
|---|---|---|---|---|---|---|
| 1 | Barra inferior · Hoy | `ui/MainActivity.kt:215`, `288-290` | toque largo | abre «Llenar el día» (hoja de la noche) | Sí: la tarjeta «Llenar el día» con el botón «llenar/abrir» en Día (`ui/pantallas/Dia.kt:296` y `309`, `TarjetaNoche` l. 323+) | Cumple P7. El atajo solo se explica al fondo de Más (`ui/pantallas/Mas.kt:144`) |
| 2 | Barra inferior · Diario | `ui/MainActivity.kt:215`, `291` | toque largo | ánimo rápido (`QuickActivity` modo `animo`) | Sí: el botón «¿Cómo estás ahora?» de Diario (`ui/pantallas/Diarios.kt:123`) | Cumple |
| 3 | Barra inferior · ＋ | `ui/MainActivity.kt:201`, `292` | toque largo | ánimo rápido | Sí: ＋ → «Ánimo ahora» (`ui/QuickActivity.kt:204`), un toque más | Cumple |
| 4 | Barra inferior · Vida | `ui/MainActivity.kt:215`, `293` | toque largo | Resumen semanal | Sí: la tarjeta «Resumen semanal» en Vida (`ui/pantallas/Vida.kt:74`) | Cumple |
| 5 | Barra inferior · Análisis | `ui/MainActivity.kt:215`, `295` | toque largo | buscador (`"buscar"`) | Sí: la lupa del encabezado (`ui/pantallas/Explorar.kt:102`) | Cumple. Es raro que el atajo de «Análisis» sea un buscador general |
| 6 | Llenar el día · Foto del día | `ui/pantallas/Dia.kt:765-767`, sección en `494-495` | toque largo en la miniatura | elige (o quita) la foto del día | **No.** El toque normal abre la foto en la galería (l. 766). Solo lo explica el subtítulo de la sección (l. 494) | **Viola P7.** Desmarcarla también exige el toque largo (alterna en l. 495) |
| 7 | Mapa (`mapa`) | `ui/pantallas/Mapa.kt:492`; texto en `451` | toque largo en el mapa (osmdroid) | crea un lugar en ese punto (diálogo l. 524-548) | **A medias.** «Lugares» (`ui/pantallas/Medir.kt:267`) guarda la ubicación *actual*, pero no un punto cualquiera. No hay botón «＋ lugar aquí» en el mapa | Viola P7 en parte. El aviso es un comentario de 11 sp (l. 451) |
| 8 | Día | `ui/pantallas/Dia.kt:153-162` | deslizar a los lados | día anterior o siguiente | Sí: las flechas ‹ › (l. 194 y 209), «hoy» (l. 208) y la fecha ▾ que abre el calendario (l. 177) | Cumple |
| 9 | Entrada o sueño a pantalla completa | `ui/Libros.kt:1110-1116` | deslizar | página anterior o siguiente | Sí: ‹ › en la barra (l. 1122-1125), pero son `Text.clickable` pequeños | Cumple P7; falla 48 dp |
| 10 | Bibliotecas (diario, sueños, momentos, cartas) | `ui/pantallas/Bibliotecas.kt:120-125` | deslizar | cambia de biblioteca | Sí: `SelectorBibliotecas` (l. 135, 147) | Cumple |
| 11 | Libreta de momentos (`hoja_momentos`) | `ui/pantallas/LibretaMomentos.kt:301-307` | deslizar | hoja anterior o siguiente | Sí: «‹ fecha» y «›» (l. 316 y siguientes) | Cumple |
| 12 | Cualquier escala 1–7 / 0–5 (`Escala`) | `ui/Componentes.kt:284-290` | arrastrar sobre la fila | cambia el valor | Sí: cada número se toca (l. 296-300). El texto dice «toca o desliza» (l. 312) | Cumple. Ojo: arrastrar en horizontal puede chocar con el deslizar de Día si la escala está dentro de Día (sin teléfono no se puede confirmar) |
| 13 | Ánimo ahora (`CuadriculaAfecto`) | `ui/Selectores.kt:110-115` | toque en un `Canvas` de 7×7 | valencia y activación | El toque se ve, pero el lienzo no tiene semántica: con **TalkBack no hay ningún camino** | Falla accesibilidad (§ 4), no P7 |
| 14 | Mapa para rascar (`rascar`) | `ui/pantallas/Mundo.kt:715-735` | pellizcar o arrastrar (zoom), **doble toque** (vuelve al mundo entero), toque (elige país) | navegar el mapa | Zoom: no hay botones +/−. Doble toque: solo lo dice el texto «Dos toques: todo el mundo» (l. 744). Elegir país: también con las fichas de «tus países» (l. 747) | El doble toque y el zoom violan P7 |
| 15 | Tu año en historias | `ui/pantallas/TuAnio.kt:646-651` | toque en el 30 % izquierdo o derecho, **toque largo = pausa** | avanzar, retroceder, pausar | **No hay flechas ni botón de pausa.** Es la convención de las «historias» de Instagram | Viola P7 (bajo impacto: es una pantalla anual) |
| 16 | Tu año · rascar | `ui/pantallas/TuAnio.kt:229-235` | arrastrar | descubre la tarjeta | Lo dice el texto «rasca aquí» (l. 241). No hay botón «descubrir» | Viola P7 (bajo impacto) |
| 17 | La cinta | `ui/pantallas/Cinta.kt:271-283` | toque en el `Canvas` | muestra el detalle de un bloque | Lo explican el subtítulo (l. 245) y «Toca un bloque…» (l. 341). Sin semántica | Cumple P7; falla accesibilidad |
| 18 | Constelación | `ui/pantallas/Constelacion.kt:99-100` | toque en estrella | ver a esa persona | Texto «Toca una estrella…» (l. 142) | Cumple P7; falla accesibilidad |
| 19 | Red | `ui/pantallas/Lote3.kt:358-359` | toque en nodo | resalta a la persona | No hay texto que lo explique | Viola P7 a medias; falla accesibilidad |
| 20 | Tu año (`anno`) | `ui/pantallas/Anno.kt:88-93` | toque en una celda de un lienzo de 31×12 | abre ese día | Lo dice el subtítulo (l. 69). Las celdas miden ~10 dp | Cumple P7; falla 48 dp |
| 21 | Prueba de reacción | `ui/pantallas/Medir.kt:200` | `onPress` | medir la reacción | Es el propósito de la pantalla | No aplica |

**Conclusión P7.** Los cinco atajos de la barra sí tienen camino visible. P7 se confirma como principio útil. Las violaciones reales son la **foto del día**, el **marcar lugar en el mapa** y los gestos de mapa e historias. El código no tiene **ningún deslizar para borrar**: borrar siempre es un botón, y eso es bueno.

---

## 2. P6 · Un solo nombre para cada cosa

Comparación de (a) la ruta en `ui/MainActivity.kt:322-400`, (b) el título de `Hoja(...)`, (c) la fila de Más (`ui/pantallas/Mas.kt`), (d) el buscador (`ui/pantallas/Pantallas.kt:12-67`) y (e) la barra (`ui/MainActivity.kt:184-186`).

Nota: `ui/ContenedoresMas.kt`, `ui/SalonesMas.kt` y `ui/MapasMas.kt` **no** son entradas de Más. Son dibujos de los temas del «lote 2»: libretas, salones y mapas de campaña (`ContenedoresMas.kt:5-9`, `SalonesMas.kt:5-10`, `MapasMas.kt:5-9`). El sufijo «Mas» confunde a quien mantiene el código: es P6 a nivel de código.

| Cosa | (e) Barra | (a) Ruta | (b) Título en pantalla | (c) Más | (d) Buscador | Problema |
|---|---|---|---|---|---|---|
| El día | «Hoy» | `dia` | «Día» (`Dia.kt:164`, `est.pantalla("Día","DIES")` l. 168); romano «DIES» | — | «Día» (`Pantallas.kt:13`, palabra «hoy») | **Hoy ≠ Día.** Además, la pestaña Diario tiene una sección llamada «Hoy» (`Diarios.kt:126`) |
| La hoja de la noche | — | `noche`; el intent externo `"diario"` también abre la noche (`MainActivity.kt:249`) | «Llenar el día» (`Dia.kt:416`); romano «VESPERA» | — | «Llenar el día» | Hay cuatro nombres: «Llenar el día», «hoja de la noche» (`Mas.kt:144`, guía `GuiaUso.kt:31` «Hoja de la noche»), «Diario de hoy» (botón del ＋, `QuickActivity.kt:211`) y «diario» (id del intent). El botón «Diario de hoy» **no** abre la pestaña Diario: abre la noche |
| Diario | «Diario» | `diario` (pestaña) y alias `diarios` | «Diario» (`Diarios.kt:121`) | — | **no está** (solo «Biblioteca del diario» y «Llenar el día», que tiene la palabra «diario») | El id `diario` vale por dos: pestaña dentro de la app y hoja de la noche desde fuera (`MainActivity.kt:249` vs `253`). El buscador no encuentra la pestaña |
| Análisis | «Análisis» | `entender`/`explorar` → `PantallaExplorar` | «Análisis» (`Explorar.kt:101`) | — | «Análisis» (ruta `entender`) | Tres nombres en el código para lo mismo: Análisis/entender/explorar. **Y otra pantalla también se titula «Análisis»:** la ruta `analisis` → `PantallaAnalisis` (`Analisis.kt:101`), que son las herramientas y no está en el buscador. `Pantallas.kt:6` todavía dice «Entender» |
| Vida | «Vida» | `vida` | «Vida» | — | «Vida» | Coincide |
| Más | (no está en la barra) | `mas` | «Más» (`Mas.kt:53`); romano «CETERA» | — | **no está** | Solo se llega con la tuerca «Ajustes y más» (`Decoracion.kt:1364`) de 36 dp. El nombre accesible es «Ajustes y más» y el título es «Más» |
| Buscar | — | `buscar` = `PantallaTotales(buscar=true)` | «Buscar» con el subtítulo de Totales: «Todo lo acumulado…» (`Totales.kt:86`) | — | — | La pantalla de buscar se presenta como Totales (subtítulo y fichas de periodo) |
| Logros | — | `logros` y alias `hall` | «Logros» (`Logros.kt:156`), con la sección «Hall de logros» (l. 176) | — | «Logros» | `PantallaHall` («Hall de logros», `Hall.kt:365-370`) y `RecuadroHall` (`Hall.kt:500`) **no se usan**: código muerto con otro nombre |
| Sueño (dormir) y sueños (soñar) | — | `sueno` = **un sueño soñado** (`PantallaSueno`, `Bibliotecas.kt:286`); `biblioteca_suenos` | «Biblioteca de sueños» | «Biblioteca de sueños» | «Biblioteca de sueños» | `sueno` se usa también para dormir: sección «Sueño» de Día (`Dia.kt:219`), modo `M_SUENO` del ＋ («Sueño», `QuickActivity.kt:211`, que mezcla calidad del dormir con el texto del sueño, l. 665-694), foco «con tu sueño» (`Explorar.kt:286`) y `sueno_horas`. En ESPACIO, «Enfoque y siestas» se llama «CONCENTRACIÓN Y SUEÑO» (`NombresNuevos.kt`) |
| Obras | — | `obras` → `PantallaObras` y `biblioteca` → `PantallaBiblioteca` | **las dos dicen «Obras»** (`Galerias.kt:357`, `Biblioteca.kt:190`) | — | `obras` = «**Biblioteca**» (`Pantallas.kt:48`) | Se cruzan con «Biblioteca del diario» y «Biblioteca de sueños». El botón que lleva a `biblioteca` dice «lista y pendientes» (`Galerias.kt:362`) |
| Mapas y lugares | — | `mapa`, `mapas`, `lugares`, `lugar_nuevo`/`lugares_especiales` | «Mapa», «Mapas», «Lugares» (`Medir.kt:244`), «Lugares especiales» | solo «Lugares especiales» (`Mas.kt:135`) | «Mapa», «Mapas», «Lugares especiales»; **falta «Lugares»** | Cuatro pantallas con nombres casi iguales. El botón que lleva a `lugares` dice «Ver el mapa emocional de tus lugares» (`Explorar.kt:314`) |
| Tu año | — | `anno` y `tu_anio` | «Tu año» y «Tu año en historias» | — | los dos | Nombres casi iguales para cosas distintas (cuadrícula vs resumen) |
| Privado | — | `recaidas` | «Privado» (`Recaidas.kt:77`) | «Privado» | «Privado» | Ruta, título y guía no coinciden: la ruta es `recaidas` y la guía usa la clave «Impulsos y recaídas» (`GuiaUso.kt:87`), así que el «?» no aparece en Privado |
| Momentos | — | `momentos` → `PantallaMomentos` | «Momentos» (`Momentos.kt:70`) | — | no | **Pantalla huérfana.** La petición externa `momentos` se redirige a la pestaña Diario (`MainActivity.kt:251`) y no encontré ningún `nav.ir("momentos")`. Hay además «Libreta de momentos» y «Momentos de este día» |
| Guías | — | `guia_uso`, `guia` | «Guía», «Guía científica» | las dos | las dos | Contenido repetido con nombres distintos: «Cómo leerte» (`GuiaUso.kt:66`) y «Cómo leer tus datos» (`Mas.kt:203`) |
| Ajustes | — | `ajustes`/`apariencia`, `avisos`, `privacidad` | «Apariencia», «Avisos y recordatorios», «Privacidad» | igual | igual | Coinciden, pero la tuerca se llama «Ajustes y más» y abre Más, no Ajustes |
| Gimnasio | — | `fuerza` | «Gimnasio» | «Gimnasio» | «Gimnasio» | La ruta no coincide (bajo impacto) |
| Herramienta «semana» | — | `"semana"` = Resumen semanal | «Resumen semanal» | — | «Resumen semanal» | En `Analisis.kt:73` el id `semana` es «Día de la semana»: choque de ids en el código |

**Renombrado por tema:** `Estilo.pantalla` (`Estilo.kt:65-73`) y `Temas.nombre` (`Temas.kt:185`) cambian el **título grande** de casi todas las pantallas según el tema. Por ejemplo, en MEDIEVAL Gastos→«Libro de cuentas», Más→«Otros folios», Análisis→«Quaestiones»; en ESPACIO Diario→«BITÁCORA», Gastos→«COMBUSTIBLE»; en DISTOPIA Día→«PARTE DIARIO». La barra inferior, en cambio, siempre dice «Hoy/Diario/Vida/Análisis» (`MainActivity.kt:223`). El español queda en el comentario pequeño de casi todos los temas (`Estilo.kt:50-61`). Es una violación sistemática de P6 y de Nielsen 2 (lenguaje del mundo real). Es de diseño propio y deliberada: el autor decide si vale la pena.

---

## 3. Buscador de pantallas (`ui/pantallas/Pantallas.kt`)

- **Cómo se llega.** No es una pantalla propia: va incrustado en `PantallaTotales(buscar = true)` (`Totales.kt:103`, `EnPantallas`).
  - Se abre con la lupa de 36 dp en Día (`Dia.kt:171`), Diario (`Diarios.kt:121`), Vida (`Vida.kt:72`), Análisis (`Explorar.kt:102`), Más (`Mas.kt:53`) y Gastos (`Gastos.kt:91`).
  - También con el toque largo en la pestaña Análisis (`MainActivity.kt:295`).
  - Y con las fichas @persona y #tema del Resumen semanal (`Semana.kt:131-132`). Estas abren el buscador **vacío**, sin la persona o el tema tocado: el usuario lo tiene que volver a escribir.
- **Cómo busca** (`Pantallas.buscar`, l. 72-76).
  - Ignora mayúsculas y acentos. Exige al menos 2 letras.
  - Busca la consulta **entera como subcadena** dentro de «título + detalle + palabras + título del tema actual». «mapa emocional» o «gastos mes» no encuentran nada, porque las palabras tienen que ir seguidas.
  - Muestra como máximo 8 resultados (`l.take(8)`, l. 113).
- **Sinónimos.** Sí, a mano, en el campo `palabras`: por ejemplo «pedo cannabis weed mota cruda» para Sustancias (l. 28) y «pomodoro tesis chino» para Enfoque (l. 31). La lista no tiene sinónimos de corrección («corregir», «editar», «borrar», «deshacer», «ayer»), ni de «sueño» en el sentido de dormir para la sección del Día, ni «dinero» → Vida.
- **Pantallas que lista:** 55 (l. 12-67).
- **Rutas del `when` que no están en el buscador** (comparación automática: 79 rutas, 55 entradas). Las de detalle se pueden quedar fuera, pero hay faltas reales:
  - **Faltan y deberían estar:**
    - `diario`: la pestaña Diario.
    - `mas`: Más.
    - `lugares`: Lugares, `PantallaLugares`, donde se guardan y cambian lugares.
    - `analisis`: las herramientas de análisis (tendencia, modelo, dinámica, ritmo, palabras, atípicos).
    - `momentos`: la pantalla huérfana.
    - `mundo`, `rascar`, `unesco`, `ciudad_nueva`, `fondo_telefono`.
    - `biblioteca`: la lista de pendientes de Obras.
    - `carta_nueva`: escribir una carta.
  - **De detalle (se aceptan fuera):** `entrada`, `sueno`, `carta`, `obra`, `ascenso`, `buscar_obra`, `hoja_momentos`.
  - **Alias:** `ajustes`, `diarios`, `explorar`, `hall`, `lugares_especiales`, `buscar`.
  - **Lo contrario:** `guia_uso` está en el buscador sin entrada propia en el `when`, pero la cubre el `else` (`MainActivity.kt:398`). Funciona.
- **El comentario está desactualizado:** dice «desde Más o Entender» (`Pantallas.kt:6`).

---

## 4. Accesibilidad

**Recuentos (`grep` en `ui/`):**
- `Icon(…, contentDescription = null)`: **0**. La app no usa `Icon` de Material: sus íconos son `Canvas` dibujados y glifos de texto. El único `contentDescription = null` es la miniatura de foto (`Dia.kt:787`), que es decorativa. Está bien.
- Elementos con descripción explícita (`semantics { contentDescription }`): **6** en toda la UI:
  - `IconoPestana` (`IconosPestana.kt:31`);
  - Emblema (`Decoracion.kt:84`, que es decorativo y **no debería** leerse);
  - Modo claro u oscuro (`Decoracion.kt:1246`);
  - Buscar (`:1271`);
  - Ajustes (`:1364`);
  - Guardar frase (`FrasesGuardadas.kt:66`).
- Elementos tocables en la UI (aprox.): 98 `.clickable`, 176 `TextButton`, 189 `Ficha`, 69 `BotonPrincipal`, 96 `BotonSecundario`, 26 `OutlinedButton`/`OutlinedIconButton`, 21 `Surface(onClick)`, 3 `combinedClickable`, 15 `pointerInput`. Casi todos llevan texto, así que TalkBack los lee.
- **Botones cuyo único texto es un glifo** (TalkBack lee «signo de interrogación», «flecha», «estrella»…): **≈ 31**.
- **Lienzos tocables sin semántica:** **9**. Son `CuadriculaAfecto`, Cinta, Constelación, Red, Tu año, Mapa para rascar (dos), historias de Tu año y rascar.
- **Objetivos de menos de 48 dp seguros** (tamaño fijo sin `minimumInteractiveComponentSize`):
  - 4 botones redondos (36 dp ×3, 40 dp ×1);
  - 21 `Text(...).clickable` con relleno pequeño;
  - 1 `TextButton` con `Modifier.size(36.dp)` (`Componentes.kt:385`).

  Los `TextButton`, `Surface(onClick)` y `Ficha` de Material 3 suelen ampliar el área táctil a 48 dp aunque se vean más chicos. No lo cuento como falla, pero hay que comprobarlo en el teléfono (Accessibility Scanner).
- **Texto de menos de 11 sp:** 27 casos (el mínimo, 7 sp, en `Calendario.kt:212`).

**Los 20 casos más relevantes:**

| # | Archivo:línea | Pantalla | Problema |
|---|---|---|---|
| 1 | `ui/Selectores.kt:110-115` | Ánimo ahora (la entrada más frecuente de la app) | Cuadrícula 7×7 en un `Canvas` con `detectTapGestures`: sin semántica, **no se puede usar con TalkBack ni con teclado**. Los colores codifican valencia y energía; hay rótulos de texto en las esquinas (l. 130-135) |
| 2 | `ui/Decoracion.kt:1270` | Encabezado de las 4 pestañas, Más y Gastos | Lupa de **36 dp** |
| 3 | `ui/Decoracion.kt:1363` | Igual | Tuerca «Ajustes y más» de **36 dp**: es la única puerta a Más |
| 4 | `ui/Decoracion.kt:1243` | Todas las `Hoja` | Botón de modo claro u oscuro de **36 dp**, junto a los otros dos: fácil tocarlo sin querer |
| 5 | `ui/Hoja.kt:57` | Unas 20 pantallas | Botón de ayuda «?» sin descripción (TalkBack: «signo de interrogación») |
| 6 | `ui/Hoja.kt:41` y `ui/Estilo.kt:203-207` | Todas | El botón de volver lee el texto del tema: «← cd ..» (romano, por defecto), «◄ НАЗАД» (URSS), «← رجوع» (Andalus), «◄ БУЦАХ» (Mongol). No tiene descripción fija «Volver» |
| 7 | `ui/Libros.kt:1122-1127` | Entrada o sueño abierto | ‹ › ⋯ como `Text.clickable` con relleno horizontal de 10 dp: ~30×32 dp, sin descripción. El ⋯ abre las acciones (editar, PDF…) |
| 8 | `ui/Libros.kt:1090-1091` | Lectura y escritura del diario | «Volver» como `Text.clickable`, ~40 dp de alto |
| 9 | `ui/Libros.kt:1192-1194` | Escritor del diario | **Guardar** como `Text` con fondo y `.clickable`: no se anuncia como botón, ~36 dp de alto; el texto cambia con el tema («SIGNARE», «git commit», «TRANSMITIR»…, l. 254-260) |
| 10 | `ui/Cartas.kt:410` | Carta abierta | ⋯ igual que el #7 |
| 11 | `ui/pantallas/TuAnio.kt:631`, `637` | Historias | ⤓ y ✕ como `Text.clickable`, ~28 dp de alto |
| 12 | `ui/pantallas/Hoy.kt:166-170` | Día · «si tienes tiempo» | Invitaciones y «hoy no» como `Text.clickable` con relleno vertical de 4 dp (~28 dp) |
| 13 | `ui/pantallas/Dia.kt:177-178` | Día | La fecha «▾» que abre el calendario es `Text.clickable` sin rol de botón ni descripción |
| 14 | `ui/Componentes.kt:385` | Toda `ListaEtiquetas` (temas del sueño, etc.) | «×» para quitar, `TextButton` con `size(36.dp)` y sin descripción de *qué* se quita |
| 15 | `ui/pantallas/Anno.kt:88-93` | Tu año | Celdas de unos 10 dp en un lienzo, sin semántica. La información es **solo color** (valencia, l. 100) |
| 16 | `ui/pantallas/Cinta.kt:271-283` | La cinta | Lienzo tocable sin semántica |
| 17 | `ui/pantallas/Hoy.kt:422-423`, `ui/pantallas/Hall.kt:432-433` | Editar tablero, Hall | ↑ ↓ sin descripción («flecha arriba» sin decir de qué fila) |
| 18 | `ui/pantallas/Dia.kt:194`, `209`; `Cinta.kt:247`, `252`; `Recaidas.kt:82`, `84`; `Calendario.kt:119`, `124`; `Anno.kt:71`, `76`; `Gastos.kt:162`, `164` | Navegación por fechas | ‹ › sin descripción («día anterior», «mes siguiente») |
| 19 | `ui/pantallas/Pantallas.kt:138` | Más | ☆/★ para fijar, sin descripción (TalkBack: «estrella blanca» o «estrella negra») |
| 20 | `ui/pantallas/Mundo.kt:715-735` | Mapa para rascar | Zoom solo con pellizco y doble toque, sin botones ni semántica |

**Información que depende solo del color:**
- Tu año (`Anno.kt:100`) y la capa de ánimo del Calendario (`Calendario.kt:195-217`): la valencia es solo color, sin número.
- Leyenda de Análisis «● suben juntas / ● una sube, la otra baja» (`Explorar.kt:195-196`). Se compensa porque cada tarjeta muestra el signo «%+.2f» (`Relacion.kt:61`).
- La barra lateral de valencia en Diario (`Diarios.kt:211`) se compensa con «x/7» (l. 215).
- **Buenas prácticas presentes:**
  - `Escala` añade una franja de color y el número (`Componentes.kt:302-305`);
  - `Ficha` activa añade un cuadrito y negrita (`Componentes.kt:327-328`);
  - la barra inferior usa `onLongClickLabel` para anunciar el atajo (`MainActivity.kt:201`, `215`).
- **Contraste:** no se puede medir sin renderizar los temas. Hay textos y botones con alfa bajo: etiquetas sin elegir de la barra al 72 % (`MainActivity.kt:219`, `224`), flechas desactivadas al 30 % (`Libros.kt:1122`) y la marca del calendario de 7 sp al 70 % (`Calendario.kt:212`). Conviene medirlo con capturas por tema.

---

## 5. Nielsen

### N1 · Visibilidad del estado
- **A qué día va lo que anotas.**
  - Día: fecha grande (`Dia.kt:177`), «estado = guardando… / guardado / vacío · retroactivo» (`Dia.kt:196-197`) y aviso de madrugada (`Dia.kt:291`). Bien.
  - Llenar el día: la fecha va en el subtítulo (`Dia.kt:416-417`). Bien.
  - `QuickActivity` muestra «hora · fecha del día lógico» en todos sus modos (`QuickActivity.kt:195`). **No explica** que a la 1:30 la fecha que se ve es la de ayer: no hay aviso como el de `Dia.kt:291`. `SelectorHora` sí lo avisa, pero solo al elegir una hora (`Selectores.kt:67`).
- **No se puede cambiar el día:** en el ánimo rápido (no tiene selector de hora, `QuickActivity.kt:253-325`), en `SuenoRapido` (`:666`, siempre el día lógico de hoy) ni en `Minimo` (`:699`). Viola P2.
- **Confirmación de lo guardado.**
  - `QuickActivity` sí confirma: Toast «Gasto registrado» (`:656`), «Guardado» (`:562`, `:614`) y «Guardado a las HH:MM» en el ánimo (`:269`).
  - En **Gastos** (pantalla), al registrar no hay mensaje: solo el sonido de `BotonPrincipal`, se limpia el formulario (`Gastos.kt:150`) y el gasto aparece en la lista, que puede quedar debajo del pliegue.
  - Al **tocar un gasto para corregirlo**, se carga en el formulario de arriba (`Gastos.kt:217-222`) sin desplazar la pantalla: si estabas abajo, no ves que algo cambió. Sin teléfono no se puede confirmar.
  - Notas: «Guardar nota» limpia el campo sin mensaje (`Lote2.kt:267-269`).
- **Estados que se calculan:** «Calculando…» y «Sumando…» se muestran (`Explorar.kt:186`, `Totales.kt:107`). Bien.

### N2 · Coincidencia con el mundo real
- Títulos en latín, griego, ruso o árabe según el tema (§ 2). Los textos de «guardar» y «volver» también cambian (`Libros.kt:254-260`, `Estilo.kt:203-207`).
- La notación de código es parte del estilo pero oscurece el sentido para un no programador: «estado = », «= null · sin marcar» (`Componentes.kt:311`), «= 1/= 0» en hábitos (`Dia.kt:452`), «git commit».
- «Deshacer» en `QuickActivity` significa **borrar el registro** (`QuickActivity.kt:319`, `:374`, `:402`): no es lo mismo que el «deshacer» de 5 s de la app.

### N3 · Control y libertad
- **`Deshacer` (5 s, `Deshacer.kt`):** de 18 llamadas `Repo.borrar*` en la UI, solo 8 ofrecen Deshacer.
  - **Sin confirmación ni deshacer:**
    - borrar una **nota** (`Lote2.kt:289`), con el botón «borrar» al lado de «guardar» en el mismo diálogo;
    - borrar una **meta** (`Lote2.kt:479`);
    - borrar el momento en `QuickActivity` (`:319`, `:369`, `:402`): solo un Toast.
  - **Con dos pasos y Deshacer:** gastos e ingresos («Quitar» → «¿Borrar?», `Gastos.kt:184`, `228`), momentos («Borrar» → «¿Borrar de verdad?», `Momentos.kt:160-167`) y lugares (`Medir.kt:295`).
- **`Deshacer.ofrecer` dentro de `QuickActivity`** (`QuickActivity.kt:606`): la barra solo se dibuja en `MainActivity` (`MainActivity.kt:404` es el único `CapaDeshacer()`). Si el registro rápido se abrió desde un widget o un aviso, la barra no se ve en ese momento. Es probable que salga después o nunca: sin teléfono no se puede confirmar.
- **Salir sin guardar.**
  - El registro rápido se cierra al tocar fuera (`QuickActivity.kt:118-121`). Para ánimo, sueño y consumo no importa (se guarda al tocar).
  - En **Gasto rápido** (`:620-662`), tocar fuera **descarta en silencio** el monto escrito, sin preguntar.
  - El diario y la noche se guardan solos (`Dia.kt:380-381`, `Libros.kt:1247`). Bien.
- **Corregir la hora de un ánimo:** no se puede. `DialogoMomento` solo cambia la hora de las recaídas (`Momentos.kt:155`).

### N4 · Consistencia (ver también § 7)
- Tres componentes distintos para «‹ ›»: `TextButton`, `OutlinedButton` y `OutlinedIconButton` (§ 4, #18).
- Rótulos de guardar distintos: «Guardar», «guardar», «Listo», «Registrar», «Guardar cambios», «Guardar nota», «Sellar el día», «llenar/abrir», «SIGNARE».

### N5 · Prevención de errores (doble toque que duplica)
- **Ningún botón de guardar se bloquea mientras guarda.** La única guarda `ocupado` está en Fondos de teléfono (`FondosTelefono.kt:238`) y en Datos (`Sistema.kt:475`).
- **Registro rápido · Gasto:** «Registrar» crea `Gasto(Repo.nuevoId(), …)` y llama a `finish()` (`QuickActivity.kt:641-657`). Un segundo toque antes de que la ventana se cierre crearía **otro gasto con otro id**. Lo mismo con «Guardar» de sustancia (`:558-563`, `sus.nuevo(...)`). Riesgo bajo pero real; sin teléfono no se puede medir la ventana de tiempo.
- **Gastos (pantalla):** después de registrar, `limpiar()` deja `monto = ""` (`Gastos.kt:89`, `150`). Un segundo toque cae en «Escribe un monto mayor a cero.»: está **protegido de hecho**.
- **Notas, Cuestionarios y Lugar nuevo:** crean un id nuevo en cada toque (`Lote2.kt:268`, `Medir.kt:133`). Solo los protege que el botón se desactive o el estado cambie en la siguiente recomposición: un doble toque muy rápido podría duplicar. Es teórico.
- **Ánimo rápido:** no duplica. `asegurar()` crea el registro una sola vez y luego lo actualiza (`QuickActivity.kt:264-268`).
- **Botones peligrosos junto a los frecuentes:** modo claro u oscuro junto a la lupa (§ 4, #4); «borrar» junto a «guardar» en Notas.

### N6 · Reconocer antes que recordar
- Los atajos de toque largo solo se documentan en una línea al fondo de Más (`Mas.kt:144`).
- Más (≈ 35 filas) está detrás de una tuerca de 36 dp; no es una pestaña.
- Las fichas «Tus atajos» (`Pantallas.kt:144-161`) ayudan, pero hay que descubrir la ☆.
- Los «Momentos de este día» en Día no se tocan: no hay forma de reconocer allí que se pueden corregir (`Dia.kt:257-262`). En Diario sí se tocan (`Diarios.kt:225`), pero la «Línea de tiempo» empieza plegada (`Diarios.kt:142`).

### N7 · Flexibilidad y eficiencia
Hay atajos para expertos: toque largo en las pestañas, ★ en Más, widgets y botones de los avisos. Bien. P5/P7 piden solo que no sean el único camino.

### N8 · Estético y minimalista
No es objeto de este informe (Fase 4). Se nota que Día apila unas 10 secciones (`Dia.kt:288-367`).

### N9 · Reconocer y recuperarse de errores
- Los mensajes de error de formularios son claros: «Escribe un monto mayor a cero.», «Elige una categoría.» (`Gastos.kt:126-127`).
- `Text("Pantalla desconocida: $actual")` (`MainActivity.kt:399`) no deja volver, aunque el botón atrás del sistema sí funciona (`BackHandler`, l. 260).

### N10 · Ayuda y documentación
- **«?» por pantalla:** `Hoja.kt:55-57` usa `GuiaUso.PANTALLAS` (`data/GuiaUso.kt:85-91`), con la clave **en el título en español**. Fallos:
  - **Día no tiene «?»:** usa un `encabezado` propio (`Dia.kt:164-176`) y la clave «Día» nunca se usa.
  - **Privado no tiene «?»:** la clave es «Impulsos y recaídas».
  - «Momentos» apunta a una pantalla huérfana.
  - Gastos, Vida, Más, Mapa, Buscar, Cinta y Calendario no tienen sección de guía.
- **`PantallaGuiaUso`** (`Lote3.kt:512-531`): 15 secciones y nada sobre gestos (toque largo, deslizar), corregir o borrar, «deshacer», ni el día que empieza a las 05:00. `grep` sin resultados en `GuiaUso.kt` para «5:00 | madrugada | mantén | desliza | corregir | borrar | deshacer».
- **Dos guías que se pisan:** «Guía» («Cómo leerte», `GuiaUso.kt:66`) y «Guía científica» («Cómo leer tus datos», `Mas.kt:203`).
- **El plan § 4 está desactualizado:** «Activos», «Archivados», «Cómo leer tus datos» y «Marcos y referencias» no están en Más. Están dentro de Hábitos (`Mas.kt:162`, `187`) y de Guía científica (`Mas.kt:203`, `221`).

---

## 6. P10 · Alcance con una mano

| Acción frecuente | Dónde está | Archivo:línea | Veredicto |
|---|---|---|---|
| Registrar (＋) | barra inferior, centro | `MainActivity.kt:200-207` | Bien |
| Registro rápido (ánimo, gasto, sueño, consumo): «Listo», «Registrar», «Guardar» | hoja anclada **abajo** (`Alignment.BottomCenter`, `QuickActivity.kt:120`), botón al final y a la derecha | `QuickActivity.kt:321`, `641`, `558`, `694` | Bien |
| Llenar el día: siguiente, sellar | barra fija abajo + botón al final | `Dia.kt:539`, `543-566` | Bien |
| **Guardar el diario** (escritor a pantalla completa) | **arriba a la derecha** | `Libros.kt:1191-1194` | **Mal:** es lo más frecuente de la noche. Se compensa en parte porque se guarda solo (`Libros.kt:1247`) y «volver» también guarda |
| Volver (todas las `Hoja`) | arriba a la izquierda | `Hoja.kt:41` | Esperable en Android (está el gesto atrás del sistema) |
| Buscar, Más, modo | arriba a la derecha, 36 dp | `Hoja.kt:58-60` (más `BotonModo`), `Dia.kt:171-173` | **Mal:** zona difícil y objetivo chico; Más solo está ahí |
| Día anterior o siguiente | arriba, bajo la fecha | `Dia.kt:194`, `209` | Regular (se compensa con deslizar) |
| Gastos (pantalla): «Registrar» | en la primera tarjeta, debajo del formulario | `Gastos.kt:124` | Regular: mitad de la pantalla; con el teclado abierto puede quedar tapado (sin teléfono no se puede confirmar) |
| Diálogos (`AlertDialog`, 20 en la UI) | botones en la parte baja del diálogo, que va centrado | p. ej. `Momentos.kt:150`, `Lote2.kt:288`, `EditarSueno.kt:70` | Aceptable |
| Notas: «Guardar nota» | arriba, en la primera tarjeta | `Lote2.kt:267` | Regular |
| Cuestionarios: «Guardar» | al final, tras las preguntas | `Medir.kt:132` | Bien |

---

## 7. Consistencia: patrones distintos para lo mismo

| Operación | Variantes encontradas |
|---|---|
| **Editar un registro** | En **Gastos**, tocar la fila carga el formulario de arriba en la misma pantalla (`Gastos.kt:217`; ingresos l. 175). En **Notas** y **Metas**, tocar abre un `AlertDialog` (`Lote2.kt:284-290`, `470+`). En **Momentos** (Diario o Privado), tocar abre `DialogoMomento` (`Diarios.kt:225`, `Recaidas.kt:72`). En **Día**, «Momentos de este día» no se puede tocar (`Dia.kt:257-262`), y la cinta solo muestra el detalle (`Cinta.kt:340-342`). El **sueño** se corrige con un enlace de texto que abre un diálogo (`Dia.kt:242-246`). El **tipo de lugar**, con el texto «· cambiar tipo» en línea (`Medir.kt:288-290`). La **foto del día**, con toque largo (`Dia.kt:767`). |
| **Borrar** | «Quitar» → «¿Borrar?» + Deshacer (Gastos). «Borrar» → «¿Borrar de verdad?» + Deshacer (Momentos). «borrar» inmediato y sin deshacer (Notas, Metas). «Deshacer» = borrar con Toast (registro rápido). «deshacer» = quitar la última toma (`Lote3.kt:100`). No hay deslizar para borrar en ningún sitio: consistente en eso. |
| **Elegir otro día** | Flechas `TextButton` (Día, Cinta, Privado), `OutlinedButton` (Calendario, Tu año), `OutlinedIconButton` (Gastos, por mes), `Ficha` «‹ fecha ›» (`Mapa.kt:460-462`), deslizar (Día, libros), calendario (fecha ▾ en Día) y `SelectorHora` con fecha (Gastos, consumos). El registro rápido de ánimo no permite elegir día. |
| **Registrar un gasto** | Hay dos formularios casi iguales: la pantalla (`Gastos.kt:94-153`, con ingresos) y el rápido (`QuickActivity.kt:620-662`, sin ingresos). Fecha por defecto: `Fechas.hoyCalendario()` en los dos (no el día lógico); la madrugada se marca aparte (`Gastos.kt:224`). |
| **Pantalla vs diálogo** | Nota, meta, sueño y momento: diálogo. Carta, lugar nuevo y ciudad: pantalla (`carta_nueva`, `lugar_nuevo`, `ciudad_nueva`). Diario: diálogo a pantalla completa (`Libros.kt:1237`). Ánimo, gasto y sueño rápidos: otra Activity (`QuickActivity`). |
| **Cerrar** | «✕» (Enfoque, Tu año), «cancelar», el botón de volver del tema y tocar fuera (registro rápido). |
| **Mismo dato, dos formularios** | El sueño se llena en Día (`Dia.kt:219-247`) y en el ＋ «Sueño» (`QuickActivity.kt:665-694`) con los mismos campos. Bien que coincidan, pero solo Día permite corregir las horas. |

---

## 8. Principios del § 7 que esto confirma o matiza

- **P6:** se confirma. Es el problema más extendido (§ 2) y viene en parte de una decisión de estilo (los nombres por tema).
- **P7:** se confirma, con pocas violaciones y concretas (foto del día, marcar lugar, gestos de mapa e historias).
- **P10:** se confirma. La hoja rápida y la noche ya lo cumplen; fallan el encabezado (buscar, Más) y guardar el diario.
- **Accesibilidad:** el problema principal no son los `contentDescription` de íconos (no hay `Icon`), sino:
  1. la cuadrícula de ánimo sin semántica;
  2. unos 31 botones de solo un glifo;
  3. los tres botones de 36 dp del encabezado;
  4. los textos de volver y guardar que cambian con el tema.
- **P4:** se confirma que no todo borrado se deshace (Notas, Metas, registro rápido).
- **P2/P9:** se cumplen en Día y la noche, pero no en el registro rápido (no hay aviso de madrugada ni se puede cambiar el día).
