# Logros nuevos (parte 9 del plan de mejoras)

Investigación y propuesta del 16 de noviembre de 2026, en la rama `ccr-ca31af2d-ua0p3j` (`SCHEMA_V` = 26). No cambia código: dice cómo funcionan hoy los logros, qué funciones de la app no tienen ninguno y qué juego nuevo se propone. Las rutas cortas son relativas a `app/src/main/java/io/github/ruddy2003/vestigium/`.

Lo que pidió el autor: «Agregar un set de nuevos logros sobre las nuevas features que tiene la app, por ejemplo, leer todos los eventos especiales de una historia, guardar cierta cantidad de escenas, etc. Checa qué otras cosas no tienen logros y expándelo.»

---

## 1. Cómo funcionan hoy los logros

### 1.1 Dónde se definen

Todo está en un solo archivo: `ciencia/Logros.kt` (399 líneas).

- La clase `Logros.Logro` (líneas 24–33) tiene: `id`, `nombre`, `descripcion`, `icono`, `categoria`, `nivel`, `meta`, `progreso`, `conseguido` (el día en que se logró, o nada), `propio`, `secreto` y `unidad`.
- `calcular()` (líneas 91–331) arma la lista entera, sección por sección.
- Hay tres ayudas para contar:
  - `escalones()` (93–98): una serie de metas con el mismo dato (1, 7, 30, 100… días). El nivel es el lugar en la serie: el 1.º es bronce, el 2.º plata, el 3.º oro y del 4.º en adelante legendario (`coerceAtMost(4)`).
  - `racha()` (52–61): días seguidos.
  - `constancia()` (67–81): «k de los últimos n días», sin necesidad de racha.
- Los «logros propios» los inventa el autor en la pantalla Logros (`LogroPropio`, `data/Modelo.kt:1060`); se calculan con `serie()` y `propio()` (334–360).

En `ui/logros/` **no** están los logros: están los pedestales, placas, salones del hall, marcadores de campaña y objetos de «Tu año» de cada tema (`ui/logros/DisenoLogros.kt`, interfaz en las líneas 17–96 y reparto por tema en la 109).

### 1.2 Cómo se calcula cada uno: se derivan, no se guardan

**Cada logro se vuelve a calcular con los datos cada vez.** Lo dice el propio archivo (`ciencia/Logros.kt:18–22`): «Se calculan de tus datos; nada se guarda salvo cuáles ya viste». La fecha `conseguido` también sale de los datos: es el día del registro que hizo llegar a la meta (`alLlegar`, `acumulado`, `racha().second`, `constancia().second`).

Lo único que se guarda, en `Config` (`data/Modelo.kt`):

| Campo | Línea | Para qué |
|---|---|---|
| `logros_vistos` | 915 | id → día en que se vio. Sirve para festejar solo los nuevos |
| `hall_logros` | 933 | Los ids que se exhiben en el hall (hasta 12) |
| `logros_propios` | 913 | Los logros que inventó el autor |
| `disparos["logro_aviso:<id>"]` | — | Que ya se avisó por notificación. Se borra a los 40 días (`avisos/Trabajo.kt:235–238`) |

Consecuencias que importan para lo nuevo:

1. **Todo logro necesita un dato con fecha.** Si el dato no tiene fecha (por ejemplo, `Lugar` no la tiene, `data/Modelo.kt:399–406`), hay que sacarla de otro sitio (la primera `Visita` a ese lugar) o el logro no se puede hacer bien. Si la fecha se moviera cada día, el logro «cambiaría de día» cada vez que se abre.
2. **Los ids no se pueden cambiar.** `logros_vistos` y `hall_logros` guardan ids. Un logro renombrado en su id vuelve a festejarse y desaparece del hall. Los ids nuevos siguen la forma `base_meta` (por ejemplo `ideas_50`): el detalle busca «el siguiente de la serie» con `id.substringBeforeLast('_')` (`ui/pantallas/Trofeos.kt:222–223`) y el hall agrupa igual (`ui/pantallas/Hall.kt:2778`).
3. **Hay una memoria intermedia** (`cache`, `ciencia/Logros.kt:37–39`) que solo se recalcula si cambia una de estas colecciones: días, momentos, salud, entrenos, obras, cartas, reacciones, respuestas, gastos, pantalla, config, ascensos, sesiones, Mundo, el día lógico y los tramos de enfoque. **Un logro sobre ideas, notas, listas, lugares, visitas o experimentos debe agregar esa colección a `clave()`**, o se quedará sin actualizar hasta que cambie otra cosa. (Hoy ya pasa con los subrayados de Kindle: se usan en la línea 205 y no están en la clave.)

### 1.3 Niveles y metales

Cuatro niveles: 1 bronce, 2 plata, 3 oro, 4 legendario (`ciencia/Logros.kt:26`). Los metales están en `ui/pantallas/Trofeos.kt:72–79` (`METAL`, `NIVELES`); el legendario es cristal azul y brilla con la chispa de su tema (`destellos`, `Trofeos.kt:151`). Cada tema dice los cuatro niveles con su voz (`nivelesLogro`, `ui/Celebracion.kt:151–197`; por ejemplo Roma: «ex aere, ex argento, ex auro, triumphalis»).

### 1.4 Las 9 categorías

`Logros.CATEGORIAS` (`ciencia/Logros.kt:35`): **Diario, Mundo, Cuerpo, Mente, Cultura, Gente, Constancia, Curiosos y Tuyos.**

**El trofeo es uno por categoría y por tema, no uno por logro.** Todos los logros de «Cultura» en Roma son el mismo arco de triunfo; solo cambia el metal (según el nivel) y una semilla pequeña (`l.id.hashCode()`). Las figuras están en `ui/TrofeosTema.kt` (13 temas, `figura`, línea 81) y en `ui/TrofeosMas.kt` (los otros 26).

### 1.5 Cómo se nombran por tema

- **El nombre de cada logro no cambia con el tema.** «Primera página», «Cazador de sueños» o «Camaleón» son textos fijos de `ciencia/Logros.kt`; ningún archivo de `ui/nombres/` ni de `ui/Temas.kt` los traduce (la placa y la celebración usan `l.nombre` tal cual: `Trofeos.kt:169`, `Celebracion.kt:134`).
- **Lo que sí tiene nombre por tema es el trofeo de cada categoría:** `TrofeosTema.nombre(tema, categoria)` (`ui/TrofeosTema.kt:31–32`), con los mapas `NOMBRES` de `TrofeosTema.kt:34–61` (13 temas) y `TrofeosMas.NOMBRES` (`ui/TrofeosMas.kt:37`, 26 temas). Ejemplo: en Helénico, Diario es «Ánfora» y Cultura es «Lira». Si una categoría no está en el mapa, se usa el nombre de «Tuyos».
- También cambian con el tema el título de la sección («Logros» → «PALMAE» en Roma, `ui/Tema.kt:727`; «Gestas» en Medieval, `ui/Temas.kt:640`) y los niveles (1.3).

Por eso, en la propuesta, «el nombre por tema» no hace falta: basta con un nombre neutro por logro. Ponerle nombre propio a cada logro en cada tema serían 40 × 39 = 1560 nombres; no lo recomiendo.

### 1.6 Cómo se celebran

- **En la app, al momento:** `VigiaLogros` (`ui/Celebracion.kt:82–98`) mira las colecciones; 1.8 segundos después de un cambio recalcula, marca como vistos los nuevos y festeja hasta 5 de los conseguidos en los últimos 7 días, ordenados por nivel. La primera vez solo los cuenta, sin festejar el historial. `CapaLogro` (`Celebracion.kt:102–148`) dibuja el trofeo en grande con el motivo del tema girando, vibra y suena (`Sentidos.Evento.LOGRO`; cada tema tiene su `s_<id>_logro`, que vigila `CoberturaTemas.kt:100`), lanza la lluvia de figuras y ofrece «ponerlo en el hall».
  - Ojo: la lista de cosas que vigila (`Celebracion.kt:84–85`) no incluye toda la `Config`, solo `logros_propios`, `campanas` y `politica`. Un logro que sale de un campo nuevo de `Config` debe agregarse ahí o no se festejará al momento.
- **Por notificación:** la tarea de 15 minutos, una vez al día desde las 18:00, avisa de los conseguidos en los últimos 3 días que no se han visto ni avisado (`avisos/Trabajo.kt:396–405`), con `Avisos.logros` (`avisos/Avisos.kt:887–896`, canal del diario).
- **Al abrir Logros:** la tarjeta «¡Nuevos logros!» (`ui/pantallas/Logros.kt:559–593`).

### 1.7 Dónde se ven

| Lugar | Archivo | Qué muestra |
|---|---|---|
| Sala de trofeos (pantalla Logros) | `ui/pantallas/Logros.kt:550` (`PantallaLogros`), `ui/pantallas/Trofeos.kt:190` (`SalaTrofeos`) | Todos, por categoría, con filtros «conseguidos» y «cerca» |
| Detalle de un trofeo | `Trofeos.kt:219` (`DetalleTrofeo`) | Cuándo y cómo se consiguió (`Logros.comoSeConsiguio`, `ciencia/Logros.kt:368`) o cuánto falta, y el siguiente de la serie |
| Hall | `ui/pantallas/Hall.kt:2760–2854`, `HallImagen.kt` | Hasta 12 elegidos, en el salón del tema; se puede compartir como imagen |
| Recuadro de Logros en Vida | `ui/pantallas/Logros.kt:519–547` (`RecuadroLogros`) | El salón en pequeño con 5 trofeos |
| «Tu año» | `ciencia/TuAnio.kt:246` | Las medallas ganadas en el año |
| Reportes | `reportes/Reportes.kt:1742` | Los logros del periodo |

Las «medallas» de la vitrina de «Otras vidas» en Vida (`ui/pantallas/OtrasVidas.kt:214–260`, `MedallaVida`) **no son logros**: son los retratos de cada personaje.

### 1.8 Qué pruebas los vigilan

No hay ninguna prueba que revise las condiciones de los logros. Las que tocan algo:

- `UnicidadTemas.kt:65`: los nombres de los trofeos no se repiten entre temas. Tiene su propia copia de las 9 categorías (`UnicidadTemas.kt:105`).
- `UnicidadTemas.kt:77`: cada tema tiene su propio diseño de logros (`disenoLogros`).
- `CoberturaTemas.kt:57`: las figuras, el salón, la campaña y la libreta no se revisan aquí porque sus `when (tema)` obligan al compilador a cubrir los 39 temas. `CoberturaTemas.kt:100`: el sonido `logro` de cada tema.
- `Capturas.kt:954, 1532, 1736, 1755`: las capturas del hall y de la sala usan ids fijos (`diario_30`, `paises_5`…) y `Logros.CATEGORIAS`.

### 1.9 Qué hay que tocar para agregar un logro

**Un logro nuevo en una categoría que ya existe:**

| Archivo | Qué |
|---|---|
| `ciencia/Logros.kt` | La condición en `calcular()`, con `escalones` o `Logro(...)` y un id nuevo. Si lee una colección nueva, agregarla a `clave()` (líneas 38–39) |
| `ui/Celebracion.kt:84–85` | Agregar la colección o el campo a lo que vigila `VigiaLogros` |
| `ui/pantallas/Trofeos.kt:87–112` | Si la unidad es nueva («cuadros», «cuentos», «ideas», «temas», «notas»…), sumarla a `SINGULAR` para que no diga «1 cuadros». Si la descripción empieza con un verbo irregular o de 3 letras («Usa», «Oye»), sumarlo a `pasado()` para que la celebración diga «Usaste» |
| `docs/paper/cap09_reportes_motivacion.md` (sección 9.4) | El capítulo describe los logros y qué premian: se actualiza en el mismo PR (regla de `CLAUDE.md`) |

**Costo por los 39 temas: ninguno.** No hay que dibujar figuras ni poner nombres: el trofeo es el de la categoría.

**Una categoría nueva** (por ejemplo «Historias»):

| Archivo | Qué |
|---|---|
| `ciencia/Logros.kt:35` | Agregarla a `CATEGORIAS` |
| `ui/TrofeosTema.kt` y `ui/TrofeosMas.kt` | **39 figuras nuevas** (una rama más en el `when (categoria)` de cada tema; hoy caería en la de «Tuyos») y **39 nombres** en los dos mapas `NOMBRES` |
| `UnicidadTemas.kt:105`, `Capturas.kt` | Su lista de categorías |

Es decir, unas 39 figuras pequeñas con la vara de calidad de cada tema, más 39 nombres que no se repitan. Es el único costo grande de esta parte.

### 1.10 ¿Un logro nuevo cambia «lo que se guarda»?

- **Si sale de datos que ya existen, no.** Es solo cálculo: no sube `SCHEMA_V`, no toca `Diccionario.kt` ni `vestigium.R`, y no rompe la congelación (los logros no son una medida ni se exportan).
- **Si necesita un dato nuevo, sí.** Es un campo nuevo y opcional (no cambia ninguno que ya exista), así que **no rompe la congelación**, pero cambia lo que se guarda en el respaldo:
  - sube `SCHEMA_V` de 26 a 27 (una sola vez para todo el lote), como se hizo con las preferencias del esquema 24 y el 26;
  - lleva su fila en `docs/paper/apendiceD_cambios.md` (como la D.190 y la D.198);
  - se mezcla al importar un respaldo en `data/Importar.kt:243–246`. Si no se agrega ahí, al importar se pierde lo del respaldo;
  - no toca `Diccionario.kt` ni `vestigium.R` mientras no se exporte;
  - **no cuenta hacia atrás:** lo que pasó antes de la versión que lo trae no está guardado.

---

## 2. Inventario de los logros que existen

Hay **135 logros fijos**, más 3 por cada hábito activo visible y uno por cada logro propio. Algunos solo aparecen si hay datos de esa fuente: los de distancia a casa si `Perfiles.casa()` existe, «Maratonista» si hay kilómetros, los de escalada, enfoque, subrayados y campañas si hay alguno. Todas las líneas son de `ciencia/Logros.kt`. (S) = secreto: no se ve qué pide hasta lograrlo.

### Diario (29)

| id | Nombre | Condición exacta | Línea |
|---|---|---|---|
| `diario_1…1000` | Primera página, Una semana escrita, Un mes de páginas, Cien días, Un año en papel, Mil días | Días con nota no vacía **o** valencia: 1, 7, 30, 100, 365, 1000 | 102–104 |
| `palabras_*` | Un cuento… Un millón de palabras | Palabras del diario acumuladas: 10 000, 50 000, 100 000, 500 000, 1 000 000 | 105–107 |
| `racha_diario_7/30/100` | Siete seguidos, Un mes sin faltar, Cien seguidos | Mejor racha de días con diario | 108–111 |
| `suenos_1/10/50/100` | Primer sueño… Archivo onírico | Días con `sueno_recordado` | 112–114 |
| `lucido` (S) | Sueño lúcido | Un sueño con lucidez «total» o «parcial» | 115–117 |
| `carta_1` | Carta al futuro | Una carta (dice «sellada», pero cuenta cualquiera, también a otras personas) | 118–119 |
| `cartas_10/25` | Corresponsal, Epistolario | Cartas escritas (todas) | 120 |
| `carta_abierta` | Del pasado | Abrir una carta propia | 121–123 |
| `momentos_10/100/1000` | Coleccionista, Cien momentos, Mil momentos | Momentos no privados que no son aviso | 124–126 |
| `avisos_50/250/1000` | Atento, Muy atento, Siempre presente | Avisos de ánimo contestados (`tipo == "muestreo"`) | 127–129 |

### Mundo (21)

| id | Nombre | Condición | Línea |
|---|---|---|---|
| `ciudades_2…100` | Nueva ciudad… Cien ciudades | Ciudades distintas (`Mundo.resumen`): 2, 10, 25, 50, 100 | 134–136 |
| `paises_2…40` | Nuevo país… Embajador | Países: 2, 5, 10, 20, 40 | 137–139 |
| `continentes_2/3/5` | Dos, Tres, Cinco continentes | Continentes pisados | 140–143 |
| `unesco_1…50` | Patrimonio de la humanidad… Medio centenar | Sitios del Patrimonio Mundial: 1, 10, 25, 50 | 144–146 |
| `lejos_500…10000` | Lejos de casa… Las antípodas | Ciudad más lejana de casa: 500, 2000, 5000, 10 000 km | 147–153 |

### Cuerpo (21)

| id | Nombre | Condición | Línea |
|---|---|---|---|
| `pasos_dia_*` | Diez mil… Ultramaratón a pie | Un día con 10 000, 20 000, 30 000, 40 000 pasos | 158–162 |
| `pasos_total_*` | Un millón… La vuelta a México | Pasos acumulados: 1, 5, 10 y 25 millones | 163–164 |
| `maraton` | Maratonista | 42.195 km en un día | 165–166 |
| `entrenos_1…250` | Primer entreno… Hierro | Entrenos con series: 1, 10, 50, 100, 250 | 167–169 |
| `volumen_*` | Cien… Mil toneladas | Kilos levantados acumulados: 100, 500 y 1000 t | 170–172 |
| `escalada_1/25/100` | Primera pared… Cien paredes | Sesiones de escalada | 173–175 |
| `madrugador` | Madrugador | 30 días despertando antes de las 6:00 con más de 3 h de sueño | 176–177 |

### Mente (14)

| id | Nombre | Condición | Línea |
|---|---|---|---|
| `reaccion_1` | Reflejos | Una prueba de reacción | 181 |
| `reacciones_30/100` | Un mes de reflejos, Cien pruebas | Pruebas hechas | 182 |
| `reaccion_rapida` | Rayo | Una prueba con mediana < 250 ms | 183–185 |
| `cuestionarios_1/12/50` | Autoconocimiento… Científico de ti | Cuestionarios contestados | 186–188 |
| `brujula` | Brújula | Hacer el test político | 189 |
| `conocerte` | Conócete | Llenar «Conocerte» | 190–192 |
| `enfoque_10…1000` | Diez horas… Mil horas | Horas de enfoque sin siestas: 10, 50, 100, 500, 1000 | 317–319 |

### Cultura (9)

| id | Nombre | Condición | Línea |
|---|---|---|---|
| `obras_1…100` | Primera obra… Cien obras | Obras terminadas (de cualquier tipo): 1, 10, 50, 100 | 195–197 |
| `libros_12` | Un libro al mes | 12 libros terminados en un mismo año | 198–202 |
| `frases_10/50` | Coleccionista de frases, Florilegio | Frases del día guardadas con ♥ | 203–204 |
| `subrayados_100/1000` | Lápiz en mano, Mil subrayados | Subrayados de Kindle | 205–207 |

### Gente (3)

| id | Nombre | Condición | Línea |
|---|---|---|---|
| `personas_10/50/150` | Tu gente, Muchas caras, El número de Dunbar | @personas distintas en el diario | 210–215 |

### Constancia (18 + 3 por hábito)

| id | Nombre | Condición | Línea |
|---|---|---|---|
| `c_diario_*_30` | Escritura constante I–III | Diario 20, 25, 28 de 30 días | 225 |
| `c_pasos_*_30` | Caminante I–III | ≥ 8000 pasos 15, 20, 25 de 30 días | 226 |
| `c_sueno_*_30` | Buen dormir I–III | ≥ 7 h de sueño 15, 20, 25 de 30 noches | 227 |
| `c_gym_*_56` | Disciplina I–III | Entrenar 16, 20, 24 de 56 días | 228 |
| `c_reac_*_30` | Mente afilada I–II | Prueba de reacción 15, 25 de 30 días | 229 |
| `c_hab_<id>_*_30` | (nombre del hábito) I–III | El hábito marcado 15, 20, 25 de 30 días; sin el ligado a recaídas (`Privado.habitosVisibles`) | 230–232 |
| `campanas_1/5/20/52` | Primera campaña… Un año de campañas | Campañas semanales ganadas | 320–322 |

### Curiosos (20, de ellos 15 secretos)

| id | Nombre | Condición | Línea |
|---|---|---|---|
| `dia_perfecto` (S) | Día perfecto | Un día con valencia 7 | 235 |
| `remontada` (S) | Remontada | Valencia ≥ 6 con un ≤ 2 en los 3 días de calendario anteriores | 236–239 |
| `noctambulo` (S) | Noctámbulo | Editar el diario entre las 2:00 y las 4:59 | 240–241 |
| `desconectado` | Desconectado | Un día con 1 a 59 min de pantalla | 242–243 |
| `aniversario` | Aniversario | Un año desde el primer día de diario | 244–246 |
| `gastos_100/1000` | Contador, Libro mayor | Gastos registrados | 247–248 |
| `camaleon` | Camaleón | Que la rotación de temas cambie el tema una vez | 249–250 |
| `lucido_total` (S) | Lúcido de verdad | Un sueño con lucidez «total» | 256–258 |
| `cien_animo` (S) | Cien estados de ánimo | 100 registros de ánimo | 259–260 |
| `anio_memorable` (S) | Un año memorable | Una fecha señalada en cada mes de un mismo año | 261–268 |
| `carta_un_anio` (S) | Correo del pasado | Abrir una carta propia escrita hace ≥ 365 días | 269–274 |
| `tres_anios` (S) | Tres veces el mismo día | Diario el mismo día del calendario en 3 años | 275–279 |
| `paleta` (S) | Paleta de sentimientos | 10 emociones distintas en 7 días | 280–289 |
| `siete_de_siete` (S) | Siete de siete | Una campaña cumplida los 7 días | 290–292 |
| `muchos_frentes` (S) | Muchos frentes | 10 #temas distintos en 30 días | 293–301 |
| `semana_completa` (S) | Semana completa | 7 días seguidos con diario, valencia, calidad de sueño y un registro de ánimo | 302–305 |
| `agradecido` (S) | Agradecido | 30 registros con la emoción «gratitud» | 306–307 |
| `alba` (S) | Alba | Un registro de ánimo entre las 5:00 y las 5:59 | 308–310 |
| `desahogo` (S) | Desahogo | Una entrada de ≥ 1000 palabras | 311–314 |

### Tuyos

Los logros propios (`ciencia/Logros.kt:324–330` y `345–360`): fuente (hábito, #tema o @persona, o una variable del panel), umbral, y modo «total», «racha» o «días» (con o sin ventana). No se muestran los de algo privado.

---

## 3. Qué funciones de la app no tienen ningún logro

Recorrido por `ui/pantallas/Pantallas.kt` (índice, líneas 73–143) y las colecciones de `data/Repo.kt:21–65`.

| Función | Qué dato la mide hoy | ¿Logro? |
|---|---|---|
| **Otras vidas: el cuento del día** | Nada. El cuento se calcula al vuelo (`vidas/Cuento.kt:143`); abrirlo no deja rastro (`ui/pantallas/OtrasVidas.kt:444–472`) | No |
| **Otras vidas: días especiales de cada historia** | Están en los calendarios (`DiaEspecial`, `vidas/Modelos.kt:145`; 50 calendarios en `vidas/anios/Calendarios.kt:13`), pero no se guarda cuáles se leyeron | No |
| **Otras vidas: temporadas** | Se sabe por el año (`Temporadas`, `vidas/Modelos.kt:18–38`: de 2027 a 2031 corren la 1 a la 5) | No |
| **El cuadro del día** | Nada: se dibuja al vuelo (`ui/escenas/CuadroDelDia.kt`) | No |
| **Hoy en la historia** | Solo un caché en `filesDir/historia/`, fuera de la base y del respaldo, que se borra a los 30 días (`historia/Efemerides.kt:211–218`) | No |
| **El gato (acariciarlo, sus acciones)** | Nada: «Las caricias no se guardan en ningún sitio» (`ui/escenas/GatoCuadro.kt:580`; decisión D.190 del Apéndice D) | No |
| **Ideario** (ideas, posturas, «hecho comprobado», temas, «Una idea de antes») | `Repo.ideas` (`Idea`, `data/Modelo.kt:467–492`): fecha, origen, fuente, obra, postura, temas, revisiones | No |
| **Libreta de momentos** | `Repo.momentos` | Sí (`momentos_*`, `avisos_*`, `cien_animo`) |
| **Sueños** | `Dia.sueno_*` | Sí, solo cuántos (y lucidez); nada del relato (`sueno_texto`) ni de las categorías |
| **Cartas al futuro** | `Repo.cartas` | Sí |
| **Cartas a otras personas** | `Carta.para` no vacío | Solo mezcladas con las demás |
| **Notas** (ideas, propósitos, recordatorios) | `Repo.notas` (`Nota`, `data/Modelo.kt:447–459`) | No |
| **Metas** | `Repo.metas` | No |
| **Listas** | `Repo.listas` (elementos con fecha de hecho) | No |
| **Frases guardadas** | `Config.frases_guardadas` (con su tema) | Sí (`frases_*`) |
| **Foto del día** | `Dia.foto`, `Dia.foto_archivo` | No |
| **Enfoque** (bloques) | `Repo.tramos` (`plan_min` desde el esquema 21) | Solo horas (`enfoque_*`) |
| **Siestas** | `Tramo.tipo == "siesta"` | No (a propósito, ver 6) |
| **Mapas y Mundo** (países, ciudades, sitios) | `Mundo.resumen` | Sí |
| **Lugares especiales** | `Repo.lugares` (sin fecha) y `Repo.visitas` | No |
| **Obras** (libros, películas, series…) | `Repo.obras`: tipo, estado, días, citas, prioridad, quién la recomendó | Solo terminadas en total y 12 libros en un año |
| **Citas de las obras** | `Obra.citas` | No |
| **Deseados** | `Obra.prioridad` | No |
| **Tu gente, la red, presencias** | `Config.personas`, `Config.vinculos`, `Dia.presencias_ok` | Solo @personas mencionadas |
| **Gastos e ingresos** | `Repo.gastos`, `Repo.ingresos` | Solo gastos contados |
| **Sustancias** | `Repo.consumos` | No (y así debe quedar) |
| **Seguimiento privado** | `Dia.impulso`, `recaida`, `medicamentos`; momentos privados | No (y así debe quedar) |
| **Salud y sueño (Health Connect)** | `Repo.salud` | Sí, pero de valor (pasos, 7 h); nada de «registrar» |
| **Escalada, gimnasio** | `Repo.ascensos`, `Repo.entrenos` | Sí |
| **Suplementos** | `Repo.tomas` | No |
| **Experimentos** | `Repo.experimentos` (`sellada`, `fin`, `plan`) | No |
| **Cuestionarios** | `Repo.respuestas` (WHO-5 y PSS-4 cada 7 días, UCLA-3 cada 30, TIPI cada 365; `ciencia/Escalas.kt`) | Solo cuántos |
| **Prueba de reacción** | `Repo.reacciones` | Sí |
| **Brújula política** | `Config.politica` | Solo la primera |
| **Conocerte** | `Config.perfil` | Sí |
| **Campañas y retos semanales** | `Config.campanas` | Sí (ganadas, siete de siete); nada de variedad de retos |
| **Respaldos** | `Config.respaldo_ultimo`, `respaldo_verificado`, `respaldo_ensayo` (+ resultado), `bd_revision`. **Solo la última vez**, sin historia | No |
| **Exportación y uso de R** | Nada: no se guarda cuándo se exportó. La app no puede saber si se usó R | No |
| **Temas visuales** (probar temas) | Solo el tema de hoy (`Config.tema`), `tema_rotado`, y el tema con que se escribió una carta o se guardó una frase | Solo «Camaleón» (la rotación) |
| **Fondos de pantalla** | `Config.fondo_telefono_auto` (un interruptor) | No |
| **Widgets** | Nada guardado | No |
| **«Tu año»** | `Config.predicciones_anio` (las predicciones de enero, por año) | No |
| **Reportes** | Se generan solos; los `disparos` se borran a los 40 días | No |
| **Importaciones** (Spotify, YouTube, Kindle…) | `Config.importado` (fuente → última vez) | Solo subrayados |

---

## 4. Las funciones que se están construyendo

De `docs/diseno/PLAN_MEJORAS.md`, partes 1, 6 y 8.

| Función | Estado | Dato que dejaría | Para logros, pedirle a esa parte… |
|---|---|---|---|
| Seguir hasta 6 personajes, con aviso en sus días importantes | Pendiente (parte 8) | La lista de seguidos (campo nuevo) | Guardar **desde cuándo** se sigue a cada uno (`vida`, `desde`). Sin fecha no hay día del logro |
| Galería de cuadros con «me gusta» | Pendiente (parte 8) | Cada cuadro: imagen, tema, vida, día y hora (campo o colección nueva) | Guardar también **cuándo se guardó** (no solo el día del cuadro) |
| Cuadro a pantalla completa y cuento con ambiente sonoro | Pendiente (parte 8) | Nada | — |
| Botón «Hoy en la historia» bajo el cuento | Pendiente (parte 8) | Nada | — |
| Modo claro/oscuro por hora | **Hecho** en esta rama (`Config.modo = "hora"`, esquema 26, `ui/ModoHora.kt`) | Solo la preferencia | — (sin logro: es un interruptor, ver 6) |
| Fondos de pantalla con escenas | Pendiente (parte 6) | `fondo_telefono_auto` y lo que la parte agregue | Anotar el primer día en que se puso una escena |

### 4.1 ¿Se guarda qué cuentos o días especiales se han leído?

**No, en ningún sitio.** Revisado:

- `data/Modelo.kt`: no hay ningún campo sobre cuentos, vidas, cuadros ni días especiales (la búsqueda de «leído», «visto», «cuento», «seguido» no da nada en `Config` ni en otra clase).
- `data/OtrasVidas.kt`: solo datos fijos de los personajes (`BASE`, `DE_TEMAS`).
- `vidas/Cuento.kt:143–150` y `vidas/Ficha.kt`: el cuento y la ficha se calculan con el día y el id; no leen ni escriben nada.
- `ui/pantallas/OtrasVidas.kt:444–503` (`PantallaCuento`, `HoyEnSuVida`): abrir el cuento no guarda nada; `rememberSaveable` solo recuerda qué vida se eligió en esa pantalla.
- `ui/escenas/CuadroDelDia.kt`: el botón del cuento (`BotonCuento`, línea 166) solo navega.

Dos datos de diseño que fijan cómo tiene que ser el logro:

1. **Un día especial solo se puede leer el día en que ocurre.** La pantalla del cuento siempre usa el día lógico de hoy (`HoyEnSuVida`, línea 470). Un día especial dura `duracion` días (una cosecha, hasta 30); se cuenta como leído si se abrió en cualquiera de ellos (`EspecialDelDia.numero`, `vidas/Ficha.kt:67`).
2. **Cada año real es una temporada.** 2026 muestra la 1 como avance; de 2027 a 2031, la 1 a la 5; desde 2032 se repite la 5 con sus sucesos sorteados de nuevo (`Temporadas.vuelta`). Por eso la temporada se identifica por **personaje y año real**, no solo por su número. Una temporada tiene del orden de 15 a 30 días especiales (la primera de Demeas tiene 27: 13 fiestas áticas y 14 propias, `vidas/anios/AnioPiloto.kt:142–158`).

**Campo nuevo y opcional que hace falta:**

```kotlin
/** Esquema 27: los cuentos de «Otras vidas» que abriste. Uno por personaje y día lógico. */
val cuentos_leidos: List<CuentoLeido> = emptyList()

@Serializable
data class CuentoLeido(
    val vida: String,                         // id del personaje (OtrasVidas.Vida.id)
    val dia: String,                          // día lógico en que lo abriste (AAAA-MM-DD)
    val especiales: List<String> = emptyList(), // ids de los DiaEspecial de ese día (FichaDelDia.especiales)
)
```

Por qué guardar también los ids y no solo el día: con el día y el personaje se podrían recalcular, pero si después se corrige un calendario (se agrega o mueve un día especial), el logro cambiaría hacia atrás. Guardado tal cual, lo leído queda fijo. Ocupa poco: como mucho una fila por personaje y día.

Para lo demás que hoy no deja rastro (Hoy en la historia, el gato, los temas usados, respaldos y exportaciones) propongo **un solo campo genérico**, en vez de uno por función:

```kotlin
/** Esquema 27: en qué días lógicos se usó cada cosa (solo para logros). Clave → días, sin repetir y en orden. */
val uso_dias: Map<String, List<String>> = emptyMap()
```

Claves propuestas: `historia_hoy`, `historia_epoca` (abrir un cuento desde «en la época de…»), `gato` y `gato:<tema>`, `tema:<id>` (el tema estuvo puesto ese día), `respaldo_ok` (respaldo verificado), `ensayo_ok` (ensayo de restauración bien), `exportar`, `widget` (la tarea de 15 minutos ve un widget puesto), `fondo_escena`. **Nunca** una clave de Privado ni de Sustancias.

Guardar días y no conteos evita premiar los toques repetidos («acaricia 500 veces») y cuesta poco. Los dos campos suben `SCHEMA_V` a 27, se mezclan al importar (unión de listas) y no se exportan.

---

## 5. La propuesta: 40 logros nuevos

Nombres de trabajo neutros. «Ya»: se puede programar hoy con datos que existen. «Campo nuevo»: necesita `cuentos_leidos` o `uso_dias` (sección 4.1), que se pueden agregar ya. «Parte 8» o «Parte 6»: depende de una función que aún no existe.

Todas las condiciones cuentan **registrar o usar**, nunca el valor de una medida (sección 6).

### A. Historias y cuadros (14)

Categoría propuesta: **Cultura**, salvo el gato (Curiosos). Si el autor quiere una categoría propia, ver pregunta 1.

| # | Nombre de trabajo | Condición exacta | Niveles | Dato | Estado | Por qué |
|---|---|---|---|---|---|---|
| A1 | Lector de cuentos | Días lógicos distintos con algún `CuentoLeido` | 7, 30, 100, 365, 1000 | `cuentos_leidos` | Campo nuevo | El cuento del día es la función nueva más grande y no tiene ningún logro |
| A2 | Muchas vidas | Personajes distintos con algún `CuentoLeido` | 3, 10, 25, 50 | `cuentos_leidos` | Campo nuevo | Invita a conocer los 50 calendarios, no solo el del tema puesto |
| A3 | Días especiales | Días especiales leídos: pares distintos (vida, año real, id) | 1, 10, 50, 200 | `cuentos_leidos.especiales` | Campo nuevo | Premia estar ahí el día de la fiesta, la batalla o la boda |
| A4 | La temporada entera | Temporadas (vida, año real ≥ 2027) con **todos** sus días especiales leídos | 1, 3, 6 | `cuentos_leidos` + el calendario (`Calendarios.de(vida)`) | Campo nuevo | Es el logro que pidió el autor: «leer todos los eventos especiales de una historia» |
| A5 | La historia entera | Un personaje con sus 5 temporadas completas (2027 a 2031) | 1 (legendario) | Igual que A4 | Campo nuevo | La meta de cinco años; no se puede ganar antes de diciembre de 2031 |
| A6 | Testigo de la historia | Días especiales leídos con `certeza == HISTORICO` (`vidas/Modelos.kt:86`) | 5, 25, 100 | Igual que A3 | Campo nuevo | Separa lo que pasó de verdad de lo inventado; enseña historia |
| A7 | Hoy en la historia | Días distintos en `uso_dias["historia_hoy"]` | 7, 30, 100, 365 | `uso_dias` | Campo nuevo | La pantalla existe (D.191) y no tiene logro |
| A8 | En su época (S) | Abrir un cuento desde «en la época de…» de Hoy en la historia (`alCuento`, `ui/pantallas/HoyEnLaHistoria.kt:83`) | 1 | `uso_dias["historia_epoca"]` | Campo nuevo | Premia unir las dos funciones |
| A9 | Compañía | Seguir a 1 personaje; tener 6 seguidos a la vez | 1, 6 | Lista de seguidos con fecha | Parte 8 | Estrena la función de seguir |
| A10 | Galería | Cuadros guardados con «me gusta» | 1, 10, 50, 100, 250 | Galería | Parte 8 | «Guardar cierta cantidad de escenas», como pidió el autor |
| A11 | Galería de temas | Temas distintos entre los cuadros guardados | 3, 8, 16 | Galería (`tema`) | Parte 8 | Hoy 16 temas tienen cuadro (`ui/escenas/EscenaVida.kt:185–226`); cuando lleguen más, se suma un nivel |
| A12 | Las horas del cuadro | Cuadros guardados en las 6 franjas de luz (amanecer, mañana, mediodía, tarde, atardecer, noche), con `luzDe(hora, regimen)` (`vidas/Ficha.kt:48`) | 6 (oro) | Galería (`hora`) | Parte 8 | Lleva a mirar el mismo paisaje a otras horas |
| A13 | Cuadro de fiesta (S) | Un cuadro guardado en un día cuya escena cambia (`FichaDelDia.escena` no vacía: fiesta, luto, eclipse…) | 1 | Galería + calendario | Parte 8 | Un hallazgo bonito; solo cuenta el calendario del personaje, nunca la saturación de Psicodelia por un consumo tuyo |
| A14 | El gato | Días distintos en `uso_dias["gato"]` | 7, 30, 100, 365 | `uso_dias` | Campo nuevo (ver pregunta 3) | Una caricia al día cuenta lo mismo que cien: premia volver, no tocar sin parar |

### B. Ideas y escritura (8)

| # | Nombre de trabajo | Categoría | Condición exacta | Niveles | Dato | Estado | Por qué |
|---|---|---|---|---|---|---|---|
| B1 | Ideario | Mente | Ideas guardadas (`Repo.ideas`, por `fecha`) | 1, 10, 50, 100, 250 | Existente | Ya | El Ideario (esquema 16) no tiene ningún logro |
| B2 | Cuatro orígenes | Mente | Ideas de los 4 orígenes: leído, visto, contado y propia (`Catalogo.ORIGENES_IDEA`) | 4 (plata) | Existente | Ya | Invita a anotar también lo que se te ocurre y lo que te cuentan |
| B3 | Estanterías | Mente | Temas distintos entre las ideas (`Ideario.temasDe`, `data/Ideario.kt:25`) | 5, 15, 30 | Existente | Ya | Premia ordenar el Ideario |
| B4 | Volver a pensarlo | Mente | Respuestas a «Una idea de antes» con «sigue» o «cambio» (no «ignorar») | 10, 50, 150 | `Idea.revisiones` | Ya | Las dos respuestas cuentan igual: no premia cambiar de opinión ni mantenerla |
| B5 | Notas | Diario | Notas guardadas (`Repo.notas`, por `creada`) | 10, 50, 200 | Existente | Ya | Las notas no tienen logro |
| B6 | Cartas a otros | Diario | Cartas con `para` no vacío; y a personas distintas | 1, 5, 15 cartas; 5 personas | Existente | Ya | Separa las cartas a otras personas de las selladas |
| B7 | Sueños con relato | Diario | Días con `sueno_texto` de 30 palabras o más (`Texto.palabras`) | 10, 50, 150 | Existente | Ya | Premia escribir el sueño, no qué tipo de sueño fue |
| B8 | Foto del día | Diario | Días con `foto` o `foto_archivo` | 30, 100, 365 | Existente | Ya | Una función diaria sin logro |

### C. Mundo, obras y gente (6)

| # | Nombre de trabajo | Categoría | Condición exacta | Niveles | Dato | Estado | Por qué |
|---|---|---|---|---|---|---|---|
| C1 | Por tipo de obra | Cultura | Obras terminadas de cada tipo: películas 10, 50, 100; series 5, 25; juegos 5, 25; artículos 10, 50 (cuatro series con id propio, como `obras_pelicula_10`) | Los de cada serie | `Obra.tipo`, `Obra.fin` | Ya | Hoy solo cuentan el total y los libros |
| C2 | Citas | Cultura | Citas copiadas en las obras (`Obra.citas`, por `Cita.fecha`) | 10, 50, 200 | Existente | Ya | Las citas no tienen logro |
| C3 | De la lista a la mano | Cultura | Obras terminadas que tenían prioridad de deseado (`prioridad` no vacía) | 5, 25 | Existente | Ya. **[Aproximación]**: la prioridad no dice si se puso antes de empezarla | Une los deseados con lo terminado |
| C4 | Recomendadas | Gente | Obras terminadas con `recomendo` no vacío | 1, 10 | Existente | Ya | Une Tu gente con lo que lees y ves |
| C5 | Lugares con nombre | Mundo | Lugares especiales; la fecha de cada uno es su primera `Visita` (`Lugar` no tiene fecha) | 5, 15, 40 | `Repo.lugares` + `Repo.visitas` | Ya | El mapa emocional no tiene logro |
| C6 | Quién estuvo | Gente | Días con `presencias_ok` | 30, 100 | Existente | Ya. La fecha es la del día revisado, no la de la revisión | Premia revisar, un trabajo de limpieza de datos |

### D. Constancia y cuidado de los datos (9)

| # | Nombre de trabajo | Categoría | Condición exacta | Niveles | Dato | Estado | Por qué |
|---|---|---|---|---|---|---|---|
| D1 | Reloj puesto | Constancia | Noches con sueño del reloj (`SaludDia.sueno_min != null`), k de 30, con `constancia()` | 20, 25, 28 de 30 | Existente | Ya | Premia tener el dato, no dormir más (lo contrario de «Buen dormir») |
| D2 | Cuestionario semanal | Constancia | Semanas (lunes distintos) con WHO-5 contestado, de las últimas 12 | 8, 10, 12 de 12 | `Repo.respuestas` | Ya | Los cuestionarios solo tienen un conteo total |
| D3 | Los cuatro cuestionarios | Mente | WHO-5, PSS-4, UCLA-3 y TIPI contestados al menos una vez | 4 (plata) | Existente | Ya | Que se conozcan todos |
| D4 | Copia a salvo | Constancia | Meses distintos con un respaldo verificado (`uso_dias["respaldo_ok"]`); y un ensayo de restauración bien (`uso_dias["ensayo_ok"]`) | 1, 6, 12 meses; 1 ensayo | `uso_dias` | Campo nuevo (hoy solo se guarda la última vez) | Con la congelación, perder datos es lo peor que puede pasar |
| D5 | Para R | Curiosos | Trimestres distintos con una exportación (`uso_dias["exportar"]`) | 1, 4 | `uso_dias` | Campo nuevo | La app no puede saber si se usó R; sí si se exportó |
| D6 | Lo que creías en enero | Mente | Años con predicciones escritas (`Config.predicciones_anio`) | 1, 3, 5 | Existente | Ya | Premia escribirlas, no acertarlas |
| D7 | Experimentador | Mente | Un experimento sellado (`sellada`); experimentos terminados (pasó `Experimentos.ultimoDia`) | 1 sellado; 1, 3 terminados | `Repo.experimentos` | Ya | Los experimentos no tienen logro. No cuenta el resultado |
| D8 | Muchos retos | Constancia | Retos distintos aceptados en campañas (`Campana.reto`) | 5, 10 | `Config.campanas` | Ya | Invita a probar retos, no a repetir el mismo |
| D9 | Bloques cumplidos | Mente | Tramos con plan (`plan_min != null`) y `min >= plan_min`, sin siestas | 10, 50, 200 | `Repo.tramos` | Ya | Hoy solo cuentan las horas |

### E. Exploración de la app (3)

| # | Nombre de trabajo | Categoría | Condición exacta | Niveles | Dato | Estado | Por qué |
|---|---|---|---|---|---|---|---|
| E1 | Probar temas | Curiosos | Temas distintos en `uso_dias["tema:<id>"]` | 5, 15, 30, 39 | `uso_dias` | Campo nuevo. Se puede sembrar con `Carta.tema`, `FraseGuardada.tema` y `Campana.tema` | 39 temas y ningún logro por conocerlos |
| E2 | En la pantalla de inicio | Curiosos | Un día con un widget de la app puesto (`uso_dias["widget"]`) | 1 | `uso_dias` | Campo nuevo | Los widgets no tienen logro |
| E3 | Fondo con escena | Curiosos | Un día con una escena como fondo del teléfono (`uso_dias["fondo_escena"]`) | 1 | `uso_dias` | Parte 6 | Estrena los fondos nuevos |

### Resumen

| Estado | Cuántos | Cuáles |
|---|---|---|
| Ya, con datos que existen | 21 | B1–B8, C1–C6, D1–D3, D6–D9 |
| Con un campo nuevo (`cuentos_leidos` o `uso_dias`), que se puede agregar ya | 13 | A1–A8, A14, D4, D5, E1, E2 |
| Dependen de la parte 8 | 5 | A9–A13 |
| Dependen de la parte 6 | 1 | E3 |

Orden de trabajo sugerido: primero los 21 que ya se pueden hacer (un solo PR, solo `ciencia/Logros.kt` y la sección 9.4 del paper); después los dos campos nuevos, en el mismo PR que sube `SCHEMA_V` a 27; y los de la galería y los seguidos, dentro de la parte 8.

---

## 6. Lo que NO debe tener logro, y por qué

### 6.1 Reglas

1. **Nada sobre sustancias.** Ni contar consumos, ni días sin consumo, ni la Psicodelia saturada (que sale de un consumo en curso). Premiar «no consumir» también cuenta consumos y empuja a no registrarlos.
2. **Nada del seguimiento privado** (impulsos, recaídas, libido, medicamentos), ni de forma directa ni por un camino de lado (el hábito ligado a las recaídas, H-8-3).
3. **Ninguna racha rota es castigo.** Un logro ganado no se pierde. Las rachas que existen ya cumplen esto: miden la mejor racha de la historia y la fecha en que se alcanzó por primera vez (`ciencia/Logros.kt:108–111`), no la racha de hoy.
4. **Se premia registrar, no el valor registrado.** Un logro no puede pedir un ánimo alto, dormir más, una emoción concreta, una postura concreta o un resultado. Si lo hace, el dato empieza a medir las ganas de ganar el logro (ley de Campbell, capítulo 9, sección 9.7 del paper).

### 6.2 Funciones que se quedan sin logro a propósito

| Función | Por qué no |
|---|---|
| Sustancias, cafeína, alcohol en la comida | Regla 1 |
| Privado (impulsos, recaídas, libido, medicamentos) | Regla 2 |
| Suplementos | Son una intervención (la creatina es una plantilla de experimento): premiar tomarlos cambia lo que se mide |
| Siestas | Contarlas premiaría dormir de día, que mueve el sueño de la noche |
| «Hecho comprobado» y las demás posturas del Ideario | Premiar una postura empuja a etiquetar distinto. Se premian las ideas (B1–B4), no cómo se tienen |
| Metas logradas | `Meta.estado = "lograda"` lo marca el autor y muchas metas son sobre una medida (dormir, pantalla). Mejor premiar revisarlas, si se quiere |
| Calificación de las obras | Es un valor |
| Valor de los cuestionarios, del ánimo, de la reacción | Regla 4 |
| Gastos (más allá de lo que ya hay) | Contar días con gasto premiaría gastar |
| Modo por hora, ver a pantalla completa, el ambiente sonoro | Son un interruptor o algo pasivo; un logro por eso no vale nada |
| Las acciones del gato (dormir, cazar…) | Las decide el gato al azar; no hay nada que el autor haga |
| Reportes | Se generan solos |

### 6.3 Logros que ya existen y rompen la regla

**Premian un valor que escribe el autor (regla 4):**

| id | Qué premia | Línea | Nota |
|---|---|---|---|
| `dia_perfecto` | Valencia 7 | 235 | Ya señalado (H-8-6). **El autor decidió dejarlo** (D.117; paper 9.4.3) |
| `remontada` | Valencia ≥ 6 tras un ≤ 2 | 236–239 | Igual: H-8-6, D.117 |
| `lucido`, `lucido_total` | Marcar lucidez parcial o total en un sueño | 115–117, 256–258 | No están en H-8-6. Empujan a marcar «lúcido» |
| `agradecido` | 30 registros con la emoción «gratitud» | 306–307 | Empuja a elegir esa emoción |
| `paleta` | 10 emociones distintas en una semana | 280–289 | Empuja a marcar más emociones de las que se sintieron |
| `c_hab_*` | Marcar un hábito como cumplido 15, 20, 25 de 30 días | 230–232 | El hábito lo marca el autor; es su conducta, pero dicha por él |

**Premian una conducta medida por un sensor** (no sesgan lo que se escribe, porque «un reloj no se deja convencer», pero sí cambian lo que se mide): `pasos_dia_*`, `pasos_total_*`, `maraton`, `c_pasos_*`, `c_sueno_*` (dormir ≥ 7 h), `madrugador`, `desconectado`, `volumen_*`, `enfoque_*`, `reaccion_rapida` (rendimiento) y las campañas. El paper los acepta como intervención (9.4.3 y 9.7). Con la regla estricta de esta parte («registrar, no el valor») también la romperían; ver pregunta 4.

**`noctambulo`** (240–241) premia escribir entre las 2 y las 5 de la mañana: un empujón, pequeño y de una sola vez, a desvelarse.

**Logros propios sobre sustancias (regla 1, la más clara).** El formulario de logros propios ofrece todas las variables de `Estadistica.variables(dinamicas = false)` (`ui/pantallas/Logros.kt:680`), y esa lista incluye el grupo «Sustancias» (`ciencia/Estadistica.kt:332–345`: `alcohol`, `alcohol_bebidas`, `cannabis`, `cannabis_nivel_max`, `tabaco_n`…) y `alcohol_g` y `cafeina_mg`. El filtro de `ciencia/Logros.kt:327–329` solo quita lo privado (`Privado.esVariable`), no las sustancias, que no son privadas. Así, hoy se puede crear un logro como «cannabis 10 días» o «20 bebidas». Ya lo notaba H-8-3 («las de sustancias no son privadas según la regla declarada, así que ahí no hay conflicto»), pero con la regla de esta parte sí hay conflicto. Propuesta: quitar el grupo «Sustancias», `alcohol_g` y `cafeina_mg` del formulario y no mostrar los logros propios que ya existan sobre ellas.

### 6.4 Dos detalles que no son de la regla

- `carta_1` dice «Escribe una carta sellada», pero cuenta cualquier carta, también las de otras personas (`ciencia/Logros.kt:118–119`).
- Los subrayados no están en la clave del caché (`ciencia/Logros.kt:38–39` contra la línea 205): sus logros pueden tardar en actualizarse.

---

## 7. Preguntas para el autor

1. **¿Una categoría nueva para las historias y los cuadros?** Sin ella, los 14 del grupo A van en Cultura (y el gato en Curiosos) y no hay que dibujar nada. Con ella («Historias» u «Otras vidas») hay que dibujar 39 figuras de trofeo y poner 39 nombres, uno por tema (sección 1.9).
2. **¿Qué cuenta como «leer» un cuento?** Propongo abrir su pantalla (o el cuento bajo el cuadro, cuando exista). Otras opciones son llegar al final o quedarse un tiempo. Y: **¿se podrán leer después los días especiales que se pasaron** (un archivo de la temporada)? Si sí, «la temporada entera» (A4) deja de premiar estar ahí ese día y se vuelve un logro de archivo.
3. **El gato:** la decisión D.190 dice que las caricias no se guardan. El logro A14 guardaría solo **los días** en que se acarició (no cuántas veces ni a qué hora). ¿Lo permites, o se queda sin logro?
4. **Los logros que ya premian un valor:** Día perfecto y Remontada (que decidiste dejar en D.117), Sueño lúcido, Lúcido de verdad, Agradecido, Paleta de sentimientos y la constancia de hábitos; y los de conducta por sensor (pasos, sueño de 7 h, Madrugador, Desconectado). ¿Se quitan, se cambian por una versión de registro (por ejemplo, «anota 30 sueños con lucidez dicha», sea cual sea), o se quedan? Quitarlos no borra datos, pero sí trofeos del hall.
5. **¿Cierro ya los logros propios sobre sustancias?** Por la regla debería hacerse (sección 6.3). Si tienes alguno creado, desaparecería de la sala.
