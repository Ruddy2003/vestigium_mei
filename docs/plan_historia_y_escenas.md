# Plan: «Hoy en la historia», cuentos y escenas de «Otras vidas»

_Creado el 10 nov 2026 sobre la v199 (`SCHEMA_V = 23`). Es la guía que cada sesión de este trabajo debe leer antes de empezar. Este plan no cambia código._

## 0. Cómo se usa

1. Antes de cada tanda, configura el modelo y el esfuerzo de la tabla (sección 9).
2. Escribe «Haz la tanda X del plan de historia y escenas».
3. La sesión trabaja en una rama propia y verifica según la sección 7.
   - **Si la tanda dibuja o cambia una escena** (P4b, las S y las S chicas), lee antes, entera, la guía de arte: `docs/diseno/ESCENAS.md`. Sus diez reglas son obligatorias y la vara de calidad es el cuadro de Helénico.
4. Al terminar, marca su fila como «hecho <fecha>», deja un informe en `docs/historia/informes/<tanda>.md` y abre un PR.

## 1. Inventario (v199, `SCHEMA_V = 23`)

**1.1 «Otras vidas»: 47 personajes** (`data/OtrasVidas.kt`, 884 líneas)

- **Nada se guarda ni se exporta**: todo se calcula al vuelo.
- **12 comunes** (`BASE`), visibles en todos los temas:
  - Naa (paleolítico), Ata (Çatalhöyük), Ipy (Tebas), Lucio (Roma), Alice (aldea inglesa), Citlali (Tenochtitlan);
  - Kichizō (Edo), Mary (Manchester), José (hacienda pulquera), Rosa (Ciudad de México en 1960), «Alguien del mundo» y «Alguien de México» (2025).
- **35 propios de un tema** (`DE_TEMAS`). Ciudad neón tiene dos: Kei y Amaru.
- **10 son de ficción.**
- **Campos de cada `Vida`:**
  - id, nombre, rol, lugar, cuándo, año, dibujo, tema y edad;
  - `historia` (párrafos) y `dia` (tramos horarios con 8 actividades);
  - `datos` (24 medidas con su certeza y una nota);
  - `fuentes`, `ficticia` e `inspiracion`.
- **No hay** fechas propias estructuradas, nada por estación ni por clima.
- **Dónde se ve:** `ui/pantallas/OtrasVidas.kt` (`PantallaOtrasVidas` y `DetalleVida`: retrato, «Su vida», «Un día suyo», «Tú y X» y fuentes), el recuadro de Vida, el buscador y «Tu año».

**1.2 El gato** (`ui/Gato.kt`, 553 líneas)

- **Arte:** atlas WebP en `assets/gato/`, unos 15 MB, generados con Python (`herramientas/gato/`).
  - Hay 34 temas; Terminal y Ciudad neón solo existen en oscuro.
  - Básico no tiene gato.
- **Animaciones:** 13 comunes más 2 propias de cada tema.
- **Movimiento:** una máquina de estados con azar fijado por una semilla, a 12,5 cuadros por segundo.
- **Ánimo** (`Gato.humor`):
  - sale del sueño y la valencia de hoy;
  - también del enojo o la frustración anotados en momentos que no son privados;
  - de noche (de 22 a 5 h), se porta distinto.
- **Se queda quieto** con ahorro de energía o con las animaciones apagadas.
- **En Hoy:** mide 90 dp y va entre la frase y «Tus últimos días» (`Dia.kt:260`).
- **Interruptor:** `Config.gato`.
- **Pruebas:** `CoberturaTemas` y las capturas `gatosClaro` y `gatosOscuro`.

**1.3 Qué se reutiliza de Escena, la lámpara y los muebles**

- **`ui/Escena.kt` no dibuja muebles.** Es un contenedor: pared del tema, esquinas (`radioDe`) y una capa `Offscreen` que se guarda como imagen.
  - Sirve como contenedor del cuadro.
- **La «lámpara»** es un charco de luz fijo, solo en modo oscuro.
  - Dentro del cuadro no se usa: allí la luz viene de la hora.
  - Sus degradados sirven de modelo.
- **Los muebles** (`ui/vida/DisenoVida.kt` y los 34 `Vida<Tema>.kt`) dibujan con `DrawScope`.
  - Se reutiliza su **patrón**: una interfaz, un archivo por tema, un `when` exhaustivo y una versión plana de respaldo.
  - Sus dibujos no se reutilizan.
- **Otras piezas útiles:**
  - `Modifier.halo`: el aura al tocar, para el gato;
  - `Seccion(plegable)` y `Config.plegadas`: plegar sin un campo nuevo;
  - `Gato.humor` y `ahorroDeEnergia`;
  - `Red.get` y `Red.SERVICIOS`;
  - el buscador (`Pantallas.kt`) y `nav.ir`.
- **Marco: no hay.** Por decisión del autor, ni se reutiliza uno ni se dibuja uno nuevo.

**1.4 Temas y «despachos en cero»**

- `TemaId` tiene 35 entradas: 34 temas y Básico.
- **La regla:**
  - todo lo que elige según el tema es un `when (tema)` exhaustivo;
  - sin `else`;
  - sin ramas que agrupen temas;
  - sin `== TemaId.X` ni `in setOf(...)`.
- **Quién la vigila:** `CoberturaTemas.despachosPorTema`, con la lista permitida vacía.
- **Cuidado:** la búsqueda también cuenta `when (t|tema) { … else -> }`. Por eso el código nuevo no llama `t` ni `tema` a lo que no es un tema.
- **Cómo conviven las escenas con los temas:**
  - Cada tema pinta el entorno de **su** personaje, así que la escena solo existe en su tema.
  - Hay cuatro despachos exhaustivos de 35 ramas:
    - `escenaDe(tema)`;
    - `pelajeDe(tema)` (llega con el gato, en P5);
    - `banderitaHistoria(tema)`;
    - `sinCuadro(tema)`: cuáles no lo tendrán nunca. Sirve para que la prueba de cobertura cuente los que faltan.
  - **Navidad, Terminal y Básico** devuelven `null` en su propia rama: siguen con el gato de pixel, o sin gato en Básico.
  - Los temas que aún no tienen escena también devuelven `null` y usan el gato de pixel.
  - **La escena es igual en claro y en oscuro.** Su luz es la de la hora.

**1.5 La pantalla Hoy, medida**

Es `PantallaDia` (`Dia.kt`), una `Column` con desplazamiento. Las cuentas usan el tema Romano, un teléfono de 412 dp de ancho, la letra al 100 % y las 9:00 con el sueño pendiente.

| Bloque | Desde–hasta (dp) | Alto |
|---|---|---|
| Encabezado | 0–156 | ~156 (138–190) |
| Fila de estado | 156–208 | 52 |
| Frase del día | 208–381 | ~173 (140–220) |
| Gato | 381–471 | 90 |
| Tus últimos días | 471–672 | 201 (234) |
| Te falta hoy | 672–929 | ~257 (0–413) |
| «1 dato que falta» | 929–977 | 48 |
| Llenar el día | 977–1093 | ~116 |
| Tablero | 1093–1505 | ~412 |
| Sueño (de 5 a 11 h, si falta) | 1505–~1850 | ~344 |

El alto útil de un teléfono va de unos 640 a 720 dp.

## 2. Decisiones del autor

1. **El cuadro pinta al personaje de cada tema:** 33 personajes en 32 temas.
   - Navidad y Terminal quedan fuera: sin cuadro, y Tom y Carmen sin cuento.
   - En Ciudad neón, Kei y Amaru se alternan por día.
   - Básico no tiene cuadro.
2. **Los 12 comunes** tienen cuento en «Otras vidas», sin escena. En total, 45 personajes con cuento.
3. **Los personajes de ficción** llevan un calendario inventado.
4. **Libertad histórica:**
   - si existe el dato histórico, se usa;
   - si no, se inventa uno verosímil;
   - se permiten fechas exactas aunque no sean del todo precisas.
5. **Su vida avanza:** cada año real es un año más en la vida del personaje.
   - Se planean 5 años: 2027 a 2031.
   - Nadie muere; lo que pase después se decide más adelante. Vale para los protagonistas: la gente que los rodea sí puede morir (aprobado el 11 nov 2026, con el padre de Demeas en la peste).
6. **El cuento** tiene unos 8 párrafos, con mucha variedad.
   - Hay unos 20 días especiales al año.
   - No todos son fiestas: también hay sucesos de su vida.
7. **Marca de día especial:** un distintivo como de notificación, puesto sobre el cuadro pero fuera del dibujo.
   - Se queda todo el día.
8. **Las escenas:**
   - van al exterior siempre que el tema lo permita;
   - son paisajes artísticos a la altura de la vista, con un pueblo, una iglesia, un castillo, una casa o una cueva al fondo;
   - tienen el paso del día, con estas excepciones:
     - **Ciudad neón:** siempre de noche, en los niveles bajos de la ciudad.
     - **Refugio** y **Laboratorio** (el instituto): solo dos estados, encendido de día y tenue de noche.
     - **Espacio:** el interior de la nave, con muchos elementos y una ventana pequeña desde la que se ve una galaxia.
   - **No se ven personas.** Se ve el entorno según el tema, y el gato.
   - El tramo del día del personaje se nota en el entorno: el fuego a la hora de comer, la puerta abierta, las herramientas.
   - Reaccionan a lo que pasa:
     - adornos en las fiestas;
     - cultivos que crecen a lo largo del año;
     - casas destruidas en la guerra.
9. **La astronauta cambia de historia.** Ahora va en una misión futura **a Alfa Centauri, solo de ida**.
   - Cambian su vida, sus datos y sus cuentos.
   - El tema Espacio no cambia.
10. **El gato:**
    - uno por tema, dibujado con código, con el pelaje y el accesorio de su gato de pixel;
    - se mueve como el de pixel y reacciona a la escena;
    - con un toque corto le sale un corazón: «le gusta»;
    - el gato de pixel queda como opción en Ajustes;
    - nunca hay dos gatos a la vez.
11. **El acomodo:**
    - arriba, en el lugar del gato;
    - abierto por omisión;
    - se pliega a una línea de 58 dp;
    - en proporción 4:3.
12. **Sin marco.** Dentro, arriba, va una banderita «Hoy en la historia» con la forma de cada tema.
13. **Cómo se abre cada cosa:**
    - **«Hoy en la historia»:** mantener presionado el cuadro 700 ms. Con el gato de pixel, no; en Básico, solo desde el buscador.
    - **El cuento:** «leer ›», en la cabecera.
    - **Plegar:** el resto de la cabecera.
14. **«Hoy en la historia»** viene encendido por omisión y solo manda la fecha.
    - **Ningún dato tuyo se conecta con Wikipedia.**
    - Por eso no hay historia de tus ciudades.
15. **Los dos ajustes nuevos** van con los demás, en `Config`. Viajan en el respaldo y `SCHEMA_V` sube a 24.
16. **Una nota en el cap. 9 del paper** sobre la reactividad del gato.
17. **Costo:** se aplican solo los ahorros que no recortan contenido (sección 8).
18. **El cuadro cuenta lo mismo que el cuento** (decidido el 11 nov 2026, al aprobar P3a).
    - Lo que el cuento dice y se puede ver, se ve en el cuadro ese mismo día.
    - Ejemplo: la nave de Demeas aparece en el cuadro; el día en que zarpa deja de estar y no vuelve hasta que él vuelve.
    - Para que no se contradigan, los dos leen lo mismo: la ficha del día (sección 3.1).
19. **El tono y el largo del cuento están aprobados** (11 nov 2026): el de Demeas en P3a, con unas 1,950 letras por cuento, que se leen en minuto y medio.
20. **El aspecto del cuadro está aprobado** (11 nov 2026, en P4a): el de Helénico, después de rehacerlo.
    - El autor vio el primer cuadro, señaló que de día parecía una ilustración de catálogo y escogió entre tres variantes: se quedó con la de **gouache**.
    - Lo que pidió: luz con dirección y sombras, cielo con vida y algo que enmarque, paleta corta sin azul puro, agua con profundidad y reflejos, un primer plano grande con cosas que se enciman y que no parezcan puestas para la foto, grano fino, la noche al mismo nivel que el día, orillas naturales con su franja gastada por el agua, y un pueblo lejano con profundidad.
    - **Todas las escenas quedan a ese nivel.** Las reglas, cómo se adaptan a cada tema y la lista para comprobarlo están en `docs/diseno/ESCENAS.md`.

## 3. Diseño

**3.1 Su año y la ficha del día**

- **Paquete nuevo `vidas/`**, en Kotlin puro, sin Android, para que se pueda probar aquí mismo.
- **Temporadas:** `temporada = clamp(añoReal − 2026, 1, 5)`.
  - De 2027 a 2031 corren las temporadas 1 a 5.
  - En 2026 se muestra la 1 como avance.
  - Desde 2032 se repite la 5 con los sucesos personales sorteados de nuevo. Lo que pase después se decide más adelante.
  - Con la temporada avanzan la edad, el año histórico (`anio + temporada − 1`) y el estado del mundo.
- **El calendario de las 5 temporadas** está en `vidas/anios/Anio<Grupo>.kt`, como datos en el código. Se escribe de una vez, para que la historia sea continua.
  - **Unos 20 días especiales por temporada.** Cada uno es un `DiaEspecial(fecha MM-DD, duración, tipo, título, estado de escena, certeza, fuente?)`.
    - El tipo es fiesta, cosecha, guerra, rito o suceso personal.
    - Se pueden encadenar: «enferma» y después «se recupera».
  - **Arcos de escena**, cada uno con un valor de 0 a 1 según el día del año:
    - etapa del cultivo;
    - avance de una obra;
    - daños y reconstrucción;
    - adornos.
    - Los arcos se encadenan entre temporadas.
  - **Certeza de cada dato:**
    - «histórico», con fuente si se abrió en la sesión;
    - «aproximado»;
    - «inventado», nunca con enlace.
  - **Fechas exactas, sin margen de días.** Un día especial dura lo que diga su `duración`; una cosecha puede durar 30 días.
- **`FichaDelDia.de(diaLogico, vidaId)`** es **determinista** y se calcula una vez por día lógico, que empieza a las 5:00.
  - La leen el cuento, la escena y el cuadro.
  - **Qué lleva:**
    - la temporada, la edad, la semilla y la estación del lugar del personaje;
    - el clima, sorteado de una tabla mensual **[Aproximación]**;
    - el día especial, con la bandera `especial` que enciende la marca;
    - el valor de cada arco;
    - el objeto del día;
    - las piezas de los 8 párrafos;
    - el tramo horario actual;
    - el **estado** del personaje, si está en alguno que dure días o semanas («en el mar», «herido», «sin bici»).
  - **Es lo único que leen el cuento y el cuadro,** y por eso cuentan lo mismo (decisión 18). Lo que el cuento dice y se puede ver va en el calendario de una de tres formas:
    - un **estado**, si el personaje cambia de sitio o de vida por un tiempo;
    - un **arco**, si algo cambia despacio;
    - una **clave de escena**, si es cosa de un día.
  - **No lee ningún dato tuyo.**
- **La luz** sale de `luzDe(hora, regimen)`:
  - 6 franjas en casi todos los temas;
  - `SIEMPRE_NOCHE` en Ciudad neón;
  - `DOS_ESTADOS` en Refugio, Laboratorio y Espacio, donde es el ciclo de luz de a bordo.

**3.2 El cuento** (`vidas/Cuento.kt`)

- **Unos 8 párrafos, cada uno con su lugar:**
  1. la mañana y la luz;
  2. el tiempo y la estación;
  3. el trabajo del tramo;
  4. el pueblo y el entorno;
  5. la gente cercana;
  6. el objeto del día;
  7. lo que piensa;
  8. el anochecer.
- **Cada lugar tiene de 6 a 8 variantes,** con concordancia de género y huecos que llena la ficha.
- **Largo aprobado:** tres o cuatro oraciones por párrafo (unas 240 letras) y unas 1,950 letras por cuento.
- **En un día especial,** 3 o 4 párrafos se cambian por texto escrito para ese día.
- **Las pruebas vigilan:**
  - que no aparezca ningún año actual;
  - que no se repita un cuento en 365 días;
  - que cada día especial tenga su certeza;
  - que se vea la nota «Relato de ficción con base histórica».
- **Dónde vive:**
  - en la sección «Hoy, en su vida», arriba en `DetalleVida`, con sus fuentes al pie;
  - se abre directo con `otras_vidas?vida=<id>&seccion=cuento`.
- **Volumen:** unos 50 párrafos de variantes y unos 70 especiales por personaje en la temporada 1. En las temporadas 2 a 5 solo se escriben los especiales nuevos.
  - **Medido en P3a:** Demeas llevó 89 variantes y 106 párrafos especiales, más 50 oraciones sueltas, porque pasa parte del año en el mar. Un personaje que no cambia de estado necesita unas 60 variantes.
  - **Medido en P3b:** Pierre, Kei y Amaru llevaron de 196 a 219 textos cada uno (de 70 a 83 variantes y de 70 a 81 párrafos especiales), unas 40,000 letras por personaje. Lo que más suma son las personas que llegan o se van, cada una con su variante con fecha.

**3.3 La escena** (`ui/escenas/`)

- **Piezas** (hechas en P4a; ver `docs/historia/informes/P4a.md`):
  - `Momento`: la ficha del día y la hora. Es lo único que una escena puede leer;
  - la interfaz `EscenaVida`, con su parte quieta, su parte viva y las anclas del gato;
  - un `Escena<Tema>.kt` por tema, y `escenaDe(tema)`;
  - `VistaEscena`: el cuadro, que junta las dos partes y la banderita;
  - `EscenaPlana`: un paisaje hecho solo con la biblioteca. Ningún tema lo usa; es la muestra de la biblioteca en las capturas y la plantilla de cada escena nueva.
- **El lienzo:** todas las escenas se dibujan en 380 × 285 unidades y se escalan al ancho del cuadro.
- **El aspecto** lo fija la guía de arte, `docs/diseno/ESCENAS.md` (decisión del autor del 11 nov 2026, al ver el primer cuadro de Helénico): estilo gouache, diez reglas, cómo se adaptan a cada tema y la lista de comprobación. La vara es `EscenaHeleno.kt`, con su imagen de referencia en `docs/diseno/escenas/`. `EscenaPlana` solo da el orden de las capas.
- **Biblioteca de paisaje compartida** (`ui/escenas/paisaje/`), hecha en el piloto: cielos por franja, nubes, lluvia, nieve, niebla, vegetación por estación, cultivos por etapa, agua con sus reflejos, montes, casas y techos por época, humo, fuego, adornos genéricos y ruinas. También la luz con dirección y las sombras que arroja (`Sombras.kt`) y el grano del papel (`Textura.kt`): toda escena los usa, para que el día no quede liso. Desde P4b, además: las cosas del primer plano (`Cosas.kt`), las luces que no son el sol, para las escenas de noche y los interiores (`Focos.kt`), y las piezas de ciudad (`Ciudad.kt`). Cada escena la combina y le agrega lo propio de su tema.
- **Capas, de abajo hacia arriba:**
  1. cielo y lejanía: montes, mar, el pueblo, la iglesia o el castillo del fondo;
  2. plano medio;
  3. primer plano, con las anclas del gato y el objeto del día;
  4. luz;
  5. estación y clima, con partículas animadas;
  6. arcos;
  7. adornos del día especial, nunca personas;
  8. el gato.
- **La luz (capa 4) no es un velo:** cada cosa pide su color a `Luces.tono`, que lo tiñe según la hora y el tiempo y lo desvanece con la distancia. Lo que da luz propia se pinta tal cual.
- **Qué se guarda como imagen:** todo lo que no se mueve (las capas 1 a 3, los arcos, los adornos y su luz). Se vuelve a trazar cada veinte minutos, para que la luz avance, y al cambiar el tramo del día del personaje, el día o el tamaño.
- **Qué se traza en cada cuadro,** unas 15 veces por segundo: las partículas del tiempo, el humo, las llamas y los destellos del agua. Con las animaciones apagadas o con ahorro de energía queda quieto.
- **Si una escena falla al dibujar,** el cuadro se queda como estaba: no cierra la app.
- **La escena cuenta lo mismo que el cuento** (decisión 18). Dibuja según la ficha del día:
  - el **estado** del personaje: con Demeas «en el mar», su nave no está en el puerto;
  - los **arcos**: la nave en su cobertizo o en el agua, el puerto medio vacío, el humo en el horizonte, la obra que sube;
  - la **clave de escena** del día especial y el **objeto del día**, que es el mismo del que habla el cuento.
  - El comentario de cada calendario (`vidas/anios/`) dice qué tiene que mostrar el cuadro de ese personaje.
- **Interiores:**
  - **Refugio y Laboratorio:** bajo tierra, con dos estados de luz.
  - **Espacio:** el interior de la nave rumbo a Alfa Centauri, con consolas, cables, plantas del cultivo de a bordo, compuertas y objetos flotantes o sujetos. Una ventana pequeña, una fracción de la vista, muestra una galaxia.
- **Medidas:** 4:3, a todo el ancho, sin marco y con las esquinas del tema.
  - 380 × 285 dp en un teléfono de 412 dp;
  - 328 × 246 dp en uno de 360 dp.
- **La marca de día especial:**
  - es un distintivo pequeño en la esquina de arriba a la derecha, montado sobre el borde, que no es parte del dibujo;
  - también sale en la cabecera plegada;
  - se queda todo el día.

**3.4 El gato del cuadro** (`ui/escenas/GatoCuadro.kt`)

- **Un esqueleto compartido** con poses por cuadros clave.
  - Las 13 animaciones se escriben una vez.
  - Por tema solo cambian el pelaje, el dibujo, el accesorio y las 2 acciones especiales (`pelajeDe(tema)`).
- **Se porta como el de pixel:** la misma máquina de estados, el mismo ritmo y el mismo `Gato.humor`.
- **Reacciona a las anclas de la escena:** el fuego, la ventana, el objeto, la lluvia, los adornos.
- **Con un toque corto sobre él,** la caricia: corazón, «le gusta», aura y una vibración corta.
- **Con un toque en otro sitio,** mira o va hacia ahí.
- **Las caricias no se guardan.**
- **Se queda quieto** con ahorro de energía, sin animaciones o en las capturas.

**3.5 El cuadro en Hoy** (`ui/escenas/CuadroDelDia.kt`, desde `Dia.kt`)

- **Qué sale:**
  - con `gato_estilo = "cuadro"` y una escena para el tema, el cuadro;
  - si no, el gato de pixel, si está encendido;
  - nunca los dos.
- **La cabecera** (58 dp) es una `Seccion` plegable que se recuerda en `Config.plegadas`. Lleva el título del cuento, la marca, «leer ›» y el glifo para plegar.
- **La banderita** va dentro del cuadro, arriba a la izquierda.
- **La pulsación larga** de 700 ms abre «Hoy en la historia».
  - Mientras se mantiene, la banderita se va llenando.
  - Se cancela si el dedo se mueve más de 8 dp.
- **Accesibilidad:** tres acciones con nombre.

| Estado (412 dp) | Bloque | Te falta hoy | Llenar el día | Tablero |
|---|---|---|---|---|
| Hoy (gato) | 90 | 672 | 977 | 1093 |
| Cuadro abierto | 343 (10 + 48 + 285) | 925 (↓253) | 1230 (↓253) | 1346 (↓253) |
| Cuadro plegado | 58 | 640 (↑32) | 945 (↑32) | 1061 (↑32) |
| Abierto en 360 dp | 304 | ↓214 | ↓214 | ↓214 |

**3.6 «Hoy en la historia»** (`ui/pantallas/HoyEnLaHistoria.kt`, ruta `historia_hoy`)

- **Cómo se llega:** con la pulsación larga del cuadro y desde el buscador. No tiene recuadro en Vida ni texto en Hoy.
- **Qué sale del teléfono:** solo el mes y el día (`historia/Efemerides.kt` con `Red.get`).
- **De dónde salen los eventos:** H1 comprueba primero cuál de estos dos sirve.
  - (a) `api.wikimedia.org/feed/v1/wikipedia/es/onthisday/events/MM/DD`. **[Sin verificar en español: la red de esta sesión bloquea Wikipedia]**.
  - (b) La página «10 de noviembre», sección «Acontecimientos», con la API de MediaWiki.
- **Caché:**
  - un archivo por fecha en `filesDir/historia/`, fuera de la base, del respaldo y de la exportación;
  - se renueva a los 30 días;
  - el trabajo de 15 minutos baja la fecha de mañana;
  - sin conexión, muestra lo guardado.
- **Licencia:** «Wikipedia en español, CC BY-SA 4.0», con el enlace a cada artículo.
- **Enlace a un cuento:** si el año del evento cae en la época de un personaje que el tema deja ver (`epoca`, por omisión `anio ± 30`), lleva «en la época de Demeas ›», que abre su cuento.
  - No se copia texto.
  - La astronauta, al ser del futuro, no tiene eventos.

## 4. Sin redundancia

| Pantalla | Muestra | De dónde sale | NO muestra |
|---|---|---|---|
| Hoy, el cuadro | Paisaje sin personas, gato, banderita, marca y **título** del cuento | `FichaDelDia`, `escenaDe`, `pelajeDe`, `Gato.humor` | El texto del cuento, eventos, datos o comparación |
| Hoy en la historia | Eventos reales del día y «en la época de X ›» | Wikipedia y su caché | Cuentos, escenas, nada tuyo |
| Otras vidas › detalle | «Hoy, en su vida» (cuento y fuentes), retrato, Su vida, Un día suyo, Tú y X, fuentes | `FichaDelDia`, `Cuento`, `OtrasVidas` | La escena, el gato, los eventos |
| Vida › recuadro, y Tu año | Como hoy | — | El cuento |
| Ajustes | Estilo del gato y «Hoy en la historia» | `Config` | — |

**Lo que aparece en dos sitios:**
1. **El título del cuento** sale en la cabecera del cuadro y en la sección «Hoy, en su vida». En Hoy es solo un enlace.
2. **El tramo horario:** el cuento lo usa como contexto, pero no repite el reloj de «Un día suyo».
3. **«Su vida»:** el cuento no la repite. La edad del retrato se queda fija; la que avanza solo aparece en el cuento.
4. **Los eventos reales** viven solo en «Hoy en la historia».
5. **Los dos gatos** nunca salen juntos.

## 5. Privacidad: qué sale del teléfono

| Función | Qué sale | A dónde | Interruptor |
|---|---|---|---|
| Hoy en la historia | El mes y el día; como en toda petición, el servidor ve la IP | Wikimedia | «Hoy en la historia (Wikipedia)» en Ajustes › Privacidad; encendido por omisión |
| Cuento, escena, gato, ficha | Nada | — | — |
| Fuentes | Nada, hasta que tocas un enlace, que se abre en el navegador | — | — |

- Wikipedia se suma a `Red.SERVICIOS`, así que aparece sola en «Qué sale del teléfono» y en el texto de privacidad.
- Ninguna función lee el seguimiento privado.

## 6. Congelación de 2027

- **Nada cambia qué se registra ni cómo se calcula lo que se guarda:**
  - la ficha, el cuento y la escena se calculan al vuelo;
  - la caché va fuera de la base;
  - las caricias no se guardan;
  - las temporadas son contenido del código.
- **Lo guardado solo cambia en dos preferencias de `Config`**, `gato_estilo` e `historia_hoy`, que se agregan juntas en P6.
  - Tienen valor por omisión.
  - `SCHEMA_V` sube a 24.
  - Una fila en el Apéndice D: «solo preferencias de interfaz».
  - No hay columnas nuevas, así que `Diccionario.kt` y `vestigium.R` no cambian.
- **La astronauta cambia en `data/OtrasVidas.kt`**: su historia, sus tramos, sus datos (ahora inventados), `ficticia = true` y sus fuentes.
  - No es un dato guardado, así que no rompe la congelación.
  - Cambia lo que se muestra en «Tú y la astronauta» y puede cambiar «a quién te pareciste» en «Tu año». Lo dice el informe de su tanda.

## 7. Cómo se verifica cada tanda

- **Tandas de datos y texto** (P1, P2, P3b, A y T): son Kotlin puro.
  - Se compilan y se prueban **en el entorno de Claude**, con las pruebas de JVM del paquete `vidas/` en un arnés en el scratchpad, que no se sube.
  - En Actions solo se compila la APK (`apk.yml`).
  - **No piden capturas.** Sus pruebas vuelven a correr en la siguiente corrida de capturas.
  - Si el arnés no se puede armar, esa tanda pide 1 corrida.
- **Tandas de dibujo:** usan el **arnés de Compose de escritorio** (como en E6) para ver los dibujos a 300, 360 y 420 dp antes de gastar capturas.
  - **Antes de pedir capturas,** cada escena pasa la lista de comprobación de `docs/diseno/ESCENAS.md` (sección 8) y se dibuja a las 7:00, las 13:00 y las 21:00 **al lado del cuadro de Helénico**: no puede verse más pobre en ningún punto.
  - **El autor la ve junto a Helénico.** La primera escena de un tipo nuevo (interior, siempre de noche, a bordo, sin agua) se le enseña antes de seguir con los demás temas de la tanda; las demás, al terminar.
  - El informe lleva, por escena, la tabla de las diez reglas: cómo se cumple cada una y qué se adaptó al tema.
  - Luego se pide `capturas.yml` con `superficies = cuadro`, `temas = <los suyos>` y `solo = true`.
  - `temas` (`CAPTURAS_TEMAS`) deja solo esos temas; `solo` (`CAPTURAS_SOLO`) salta las capturas de siempre, que es lo que acorta la corrida. Con `solo`, la rama `capturas` queda solo con las imágenes pedidas hasta la siguiente corrida completa. Los dos se agregaron en P4a.
  - Cada tanda agrega los días de muestra de sus personajes a `DIAS_CUADRO` (`Capturas.kt`).
  - En este entorno el arnés usa Compose de escritorio 1.5.12, el único que se resuelve sin el repositorio de Google. La receta está en el informe de P4a.
- **Las tandas S** también sacan la captura `otrasVidas` de los cuentos de su grupo.
- **Las tandas de expansión no tocan el motor:** `vidas/Ficha.kt`, `Cuento.kt`, `EscenaVida` ni `GatoCuadro`.
  - **La biblioteca de paisaje:** una tanda S puede **agregarle** piezas que su tema necesite (una luz fija para interiores, un suelo mojado). **No cambia cómo se ve una pieza que ya existe,** porque cambiaría las escenas ya aprobadas; si hace falta, sigue lo que dice `docs/diseno/ESCENAS.md` (sección 5).
- **Las tandas con temas:**
  - actualizan `docs/diseno/TEMAS.md`, en sus tres filas (desde P4a): «Cuadro: escena», «Cuadro: gato» y «Cuadro: banderita»;
  - bajan los pendientes en `CoberturaTemas` (las superficies `cuadro_escena` y `cuadro_banderita`, con la lista `SIN_CUADRO`).

## 8. Tandas

**Piloto: el motor completo y 3 temas.**
- **Helénico, con Demeas.** Remero en Atenas en el 431 a. C., cuando empieza la guerra del Peloponeso. Prueba la guerra y su avance; en la temporada 2 llega la peste del 430.
- **Gótico, con Pierre.** Cantero en Amiens en 1240. Prueba la catedral que crece y el campo.
- **Ciudad neón, con Kei y Amaru.** Ficción. Prueba el calendario inventado, la noche permanente, dos personajes en un tema y un tema solo oscuro.

| Tanda | Qué hace | Archivos | Terminada cuando | Capturas |
|---|---|---|---|---|
| P1 | Motor: temporadas, ficha, arcos, luz y días especiales | `vidas/Ficha.kt`, `vidas/Modelos.kt`, pruebas | La ficha es determinista y una por día; probados los bordes del año, la temporada 6 o más (se repite la 5) y la continuidad de los arcos | 0 |
| P2 | Las 5 temporadas de Demeas, Pierre, Kei y Amaru | `vidas/anios/AnioPiloto.kt`, `docs/historia/fuentes.md` | Pruebas de certeza, fuentes y continuidad | 0 |
| P3a | Motor del cuento, cuento de Demeas y la sección «Hoy, en su vida» | `vidas/Cuento.kt`, `vidas/piezas/PiezasDemeas.kt`, `ui/pantallas/OtrasVidas.kt` | Pruebas; captura `otrasVidas`; **el autor aprueba el tono** | 1 |
| P3b | Cuentos de Pierre, Kei y Amaru | `vidas/piezas/PiezasPierre.kt`, `PiezasKei.kt`, `PiezasAmaru.kt` (uno por personaje, como el de Demeas) | Pruebas | 0 |
| P4a | Motor de escena, biblioteca de paisaje, filtro de capturas, paisaje de Helénico y su banderita | `ui/escenas/EscenaVida.kt`, `VistaEscena.kt`, `EscenaPlana.kt`, `paisaje/`, `EscenaHeleno.kt`, `Banderitas.kt`, `Capturas.kt`, `CapturasAPedido.kt`, `capturas.yml` | Capturas `cuadro` a las 7, 13 y 21 h, en 3 días (normal, fiesta, guerra), y **el día en que zarpa la flota, sin su nave** (decisión 18); despachos en 0 | 2 |
| P4b | Gótico y Ciudad neón | `EscenaGotico.kt`, `EscenaNeon.kt` | Ídem; con un día en que el cuento cambie lo que se ve (la obra parada, Kei sin bici); **la lista de `docs/diseno/ESCENAS.md` completa y el autor las vio junto a Helénico** (Ciudad neón es la primera siempre de noche: se le enseña antes de terminar) | 2 |
| P5a | Gato: esqueleto, poses y estados | `ui/escenas/GatoCuadro.kt` | Hoja de poses; pruebas de los estados | 2 |
| P5b | Reacciones, caricia y pelajes de los 3 temas | `GatoCuadro.kt`, `Pelajes.kt` | Capturas; lista de qué probar en el teléfono | 2 |
| P6 | El cuadro en Hoy: plegable, marca, gestos, ajustes, `SCHEMA_V` 24, nota del cap. 9 | `CuadroDelDia.kt`, `Dia.kt`, `Sistema.kt`, `data/Modelo.kt`, Apéndice D, `cap09` | Capturas `hoy` y `cuadro`, abiertas, plegadas y con marca | 2 |

**Hoy en la historia** (puede hacerse después de P1; H2 necesita P3a).

| Tanda | Qué hace | Archivos | Terminada cuando | Capturas |
|---|---|---|---|---|
| H1 | Comprobar la API; pantalla, caché, interruptor, `Red.SERVICIOS`, cap. 10 y Apéndice D | `historia/Efemerides.kt`, `HoyEnLaHistoria.kt`, `Red.kt`, `Trabajo.kt`, `Pantallas.kt`, `MainActivity.kt` | Pruebas del lector con una muestra guardada; capturas con y sin conexión | 1 |
| H2 | Enlace del evento al cuento | La pantalla y `vidas/` | Prueba del rango de épocas; captura | 1 |

**Expansión** (41 personajes, sin tocar el motor).

| Grupo | Personajes |
|---|---|
| G1 (comunes I) | Naa, Ata, Ipy, Lucio, Alice, Citlali |
| G2 (comunes II) | Kichizō, Mary, José, Rosa, Alguien del mundo, Alguien de México |
| G3 | Cayo (romano), Pay (egipto), Ama (cueva), K'an (meso), Nanai-vandak (seda) |
| G4 | Hermano Odo (medieval), Hermano Bernat (templario), Zaynab (andalus), Bat (mongol), Iacopo (renacimiento) |
| G5 | Tomás (pirata), Ochiyo (edo), Marie-Jeanne (revolución), Jean-Baptiste (napoleón), el naturalista (botánico) |
| G6 | Albert (vapor), Nikos (abismo), Lou (deco), Liudmila (urss), Giulia (urbe) |
| G7 (ficción) | La astronauta (espacio, ahora rumbo a Alfa Centauri), la semillera (ruinas), el funcionario (distopía), la residente (refugio), el archivista (arcano) |
| G8 (ficción) | La escriba (orden), la doctora (laboratorio), Ruth (colonos), Brenna (antiguas) |

- **A1 a A8: las 5 temporadas de cada grupo.**
  - Archivos: `vidas/anios/Anio<Gn>.kt`.
  - A7 también rehace la vida de la astronauta en `data/OtrasVidas.kt`.
  - 0 capturas cada una.
- **T: los cuentos de la temporada 1, 3 personajes por tanda.**
  - Dos tandas por grupo, de T1a a T8b: 16 tandas.
  - Archivos: `vidas/piezas/Piezas<...>.kt`.
  - 0 capturas cada una.
- **S: escena, pelaje, especiales y banderita, 4 o 5 temas por tanda.**
  - Cada escena lee de la ficha el estado, los arcos y la clave de escena de su personaje, para contar lo mismo que el cuento (decisión 18). Sus capturas incluyen un día en que el cuento cambie lo que se ve.
  - Cada S va después del A de sus personajes.
  - **Cada escena sigue la guía de arte** (`docs/diseno/ESCENAS.md`) y queda al nivel del cuadro de Helénico. Terminada cuando su lista de comprobación está completa y el autor la vio junto a Helénico.
  - Archivos: `ui/escenas/Escena<Tema>.kt`.
  - 2 capturas cada una.

| Tanda | Temas |
|---|---|
| S1 | Romano, Egipto, Prehistoria, Mesoamérica |
| S2 | Ruta de la Seda, Scriptorium, Templario, Al-Ándalus |
| S3 | Mongol, Renacimiento, Pirata, Japón Edo |
| S4 | Revolución francesa, Napoleónico, Botánico, Vapor victoriano |
| S5 | Abismo, Art déco, Soviético, Roma |
| S6 | Espacio (el interior de la nave), Ciudad verde, Distopía, Refugio |
| S7 | Horror cósmico, Orden, Laboratorio, Colonos, Tierras Antiguas |

- **C1. Cierre:**
  - `CoberturaTemas` sin pendientes;
  - `TEMAS.md` completo;
  - el informe;
  - la lista para probar en el teléfono;
  - 1 corrida de capturas.
- **Y2028 a Y2031: los especiales de cada temporada nueva.**
  - Antes de cada 1 de enero, 8 tandas T con los párrafos especiales de esa temporada para los 45 personajes, de unos 6 personajes cada una.
  - Los calendarios ya están escritos desde las A.
  - Si alguna escena necesita un estado nuevo (la casa reconstruida, la obra terminada), va en una tanda S chica.

## 9. Tabla de estado

| Tanda | Modelo | Esfuerzo | Por qué | Capturas | Estado |
|---|---|---|---|---|---|
| P1 | claude-fable-5-2 | high | Motor y diseño de datos: un error se hereda en 45 vidas y en 5 años | 0 | hecho 11 nov 2026 (`docs/historia/informes/P1.md`) |
| P2 | claude-fable-5-2 | high | Fija el patrón de los calendarios: historia verosímil, continuidad en 5 años y qué es real y qué inventado | 0 | hecho 11 nov 2026 (`docs/historia/informes/P2.md`) |
| P3a | claude-fable-5-2 | high | Fija la gramática de los 8 párrafos y el tono que copiarán todas las T | 1 | hecho 11 nov 2026 (`docs/historia/informes/P3a.md`); **tono y largo aprobados por el autor el 11 nov 2026** |
| P3b | claude-opus-5-5 | medium | Texto con el tono ya aprobado | 0 | hecho 11 nov 2026 (`docs/historia/informes/P3b.md`) |
| P4a | claude-fable-5-2 | high | Motor de escena, biblioteca de paisaje y primer paisaje con código | 2 | hecho 11 nov 2026 (`docs/historia/informes/P4a.md`) |
| P4b | claude-fable-5-2 | high | Paisajes dibujados con código | 2 | hecho 11 nov 2026 (`docs/historia/informes/P4b.md`); **falta que el autor vea los dos cuadros junto al de Helénico** |
| P5a | claude-fable-5-2 | **xhigh** | Un esqueleto animado que comparten 32 temas: estados, capas, rendimiento. Un fallo se paga 32 veces y Paparazzi no ve el movimiento | 2 | pendiente |
| P5b | claude-fable-5-2 | high | Se apoya en el esqueleto ya resuelto. Los gestos se ajustan después de probarlos en el teléfono | 2 | pendiente |
| P6 | claude-fable-5-2 | high | Integración en Hoy, medidas y esquema | 2 | pendiente |
| H1 | claude-opus-5-5 | medium | Red, caché y pantalla con patrones que ya existen | 1 | pendiente |
| H2 | claude-opus-5-5 | medium | Una regla simple de años | 1 | pendiente |
| A1 | claude-opus-5-5 | high | Calendarios verosímiles con libertad creativa y el patrón de P2. La continuidad de 5 años pide high | 0 | hecho 11 nov 2026 (`docs/historia/informes/A1.md`) |
| A2 | claude-opus-5-5 | high | Ídem | 0 | hecho 12 nov 2026 (`docs/historia/informes/A2.md`) |
| A3 | claude-opus-5-5 | high | Ídem | 0 | hecho 12 nov 2026 (`docs/historia/informes/A3.md`) |
| A4 | claude-opus-5-5 | high | Ídem | 0 | hecho 12 nov 2026 (`docs/historia/informes/A4.md`) |
| A5–A8 | claude-opus-5-5 | high | Ídem | 0 c/u | pendiente |
| T1a | claude-opus-5-5 | medium | Variantes de texto con el tono aprobado | 0 | hecho 12 nov 2026 (`docs/historia/informes/T1a.md`) |
| T1b | claude-opus-5-5 | medium | Variantes de texto con el tono aprobado | 0 | hecho 12 nov 2026 (`docs/historia/informes/T1b.md`) |
| T2a | claude-opus-5-5 | medium | Ídem | 0 | hecho 12 nov 2026 (`docs/historia/informes/T2a.md`) |
| T2b | claude-opus-5-5 | medium | Ídem | 0 | hecho 12 nov 2026 (`docs/historia/informes/T2b.md`) |
| T3a | claude-opus-5-5 | medium | Ídem | 0 | hecho 12 nov 2026 (`docs/historia/informes/T3a.md`) |
| T3b | claude-opus-5-5 | medium | Ídem | 0 | hecho 12 nov 2026 (`docs/historia/informes/T3b.md`) |
| T4a–T8b | claude-opus-5-5 | medium | Ídem | 0 c/u | pendiente |
| S1–S7 | claude-fable-5-2 | high | Paisajes, gato y banderita dibujados con código | 2 c/u | pendiente |
| C1 | claude-opus-5-5 | medium | Revisión y cierre sin dibujo nuevo | 1 | pendiente |
| Y2028–Y2031 | claude-opus-5-5 | medium | Párrafos especiales con el tono y el calendario ya fijos | 0 c/u | — |

**Corridas de capturas de la temporada 1:** 13 del piloto + 2 de H + 14 de S + 1 de C1 = **30 corridas**. Antes de los ahorros eran 57. Cada año nuevo no pide corridas, salvo una S chica si hace falta.

**Cómo se bajó el costo sin quitar contenido:**
1. **Las tandas de datos y texto se prueban en el entorno de Claude**, no en capturas. Ahorran unas 25 corridas.
2. **Capturas filtradas por tema:** corridas más cortas.
3. **Las 5 temporadas de cada calendario se escriben de una vez.** No hay tandas de calendario cada año, y la historia queda continua.
4. **Los calendarios los hace Opus high** en vez de Fable, con el patrón fijado por Fable en P2.
5. **P5b baja a high:** se apoya en el esqueleto de P5a.
6. **Biblioteca de paisaje compartida:** las tandas S combinan piezas y caben 4 o 5 temas. **Ojo:** combinar piezas no basta para llegar al nivel pedido; cada escena lleva además su primer plano, su marco y su lejanía dibujados para ella (`docs/diseno/ESCENAS.md`). Si una tanda no alcanza a dejar todos sus temas a ese nivel, entrega menos temas bien hechos y lo dice.
7. **Navidad y Terminal fuera:** 2 escenas y 2 personajes menos.
8. **Los cuentos de cada grupo se capturan dentro de su S.**
- No se usa max.

## 10. Paper

- **H1:**
  - cap. 10, § 10.8: Wikipedia entra a la tabla de servicios, que pasa de 5 a 6, y al texto;
  - una fila en el Apéndice D.
- **P6:**
  - una fila en el Apéndice D (`SCHEMA_V` 24, solo preferencias);
  - la versión del esquema en el cap. 2, si la cita;
  - **una nota en el cap. 9, § 9.7:** el gato y el cuadro reaccionan a tu ánimo y a tu sueño, así que ver tu estado reflejado puede influir en lo que registras (reactividad). **[Diseño propio]**.
- **A7:** el paper no describe a la astronauta ni sus números, así que no hay nada que cambiar. Si algún capítulo la cita, se corrige ahí.
- **Lo demás:** el paper no describe «Otras vidas» ni el gato, así que según `CLAUDE.md` no se escribe un capítulo nuevo. Ninguna figura cambia.

## 11. Riesgos y lo que no se puede comprobar sin teléfono

- **La API en español no está comprobada:** la red de esta sesión bloquea Wikipedia. Antes de H1 hay que permitir en el entorno `es.wikipedia.org`, `en.wikipedia.org` y `api.wikimedia.org`. Las tandas P2 y A que busquen fuentes necesitan los mismos sitios.
- **El arnés local:** si no se puede compilar Kotlin en el entorno, las tandas de datos y texto vuelven a pedir 1 corrida cada una, unas 25 más.
- **Volumen de texto:** unos 120 párrafos por personaje en la temporada 1, más los especiales de cada año. Si el tono no se aprueba en P3a, las T no empiezan.
- **Rendimiento:**
  - un paisaje de 380 × 285 dp con partículas y un gato a 12,5 cuadros por segundo, sobre el fondo vivo;
  - hay que medirlo en la APK y vigilar la batería, porque Paparazzi no lo mide.
- **Gestos y vibración:** los 700 ms, la caricia frente al desplazamiento y el aura encendida no se ven en las capturas.
- **Medidas en dp:** están calculadas con la letra al 100 %. Con letra más grande, todo baja más.
- **El nivel de las escenas:** las tandas S se midieron para 4 o 5 temas cada una, cuando se pensaba que bastaba con combinar piezas de la biblioteca. Con el nivel aprobado (decisión 20), cada escena lleva bastante dibujo propio.
  - Puede que una S no alcance para todos sus temas. En ese caso entrega menos temas bien hechos y lo dice; no baja el nivel.
  - También pesa más al dibujarse (el grano y las capas de sombra): hay que medirlo en el teléfono en P6, antes de hacer las 29 escenas que faltan.
- **Continuidad:** un suceso de un año, como una herida o una casa quemada, tiene que seguir al año siguiente. La prueba revisa que los arcos empalmen entre temporadas.
- **Después de 2031:** la temporada 5 se repite. El autor decide entonces cómo sigue.

## 12. Preguntas que quedan

Ninguna antes de empezar. Lo siguiente lo decide el autor en P3a, al aprobar el tono del cuento de Demeas.
