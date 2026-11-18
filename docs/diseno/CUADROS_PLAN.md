# Los cuadros (parte 8): el plan técnico

Diseño técnico de la parte 8 del plan de mejoras (`PLAN_MEJORAS.md`): seguir personajes, el cuento con su cuadro y su ambiente, la galería de cuadros guardados. Escrito el 17 nov 2026 sobre la rama de los temas nuevos (commit `ebd0fe9e`, `SCHEMA_V` = 26). Las rutas cortas son relativas a `app/src/main/java/io/github/ruddy2003/vestigium/`.

Lo que este trabajo **ya deja hecho** (tres commits):
1. este plan;
2. **todo lo que se guarda**, de una vez y con pruebas: `SCHEMA_V` = 27, los campos nuevos de `Config`, sus funciones puras (`data/Historias.kt`), la carpeta de las imágenes (`data/CarpetaCuadros.kt`), el respaldo, la importación, el diccionario (una nota) y el Apéndice D (D.199);
3. **los puntos de enganche**, con nombres y firmas definitivos y cuerpos mínimos, ya llamados desde su sitio: rutas, botones vacíos, la familia de canal, el contrato del mueble, los dos interruptores de Ajustes y dos funciones de lectura (`DiasEspeciales`, `cuadroDeVida`).

Después vienen **tres encargos que se pueden hacer a la vez** (A, B y C, sección 4), cada uno en sus propios archivos. Ninguno vuelve a tocar `data/`.

---

## 1. Cómo está hecho hoy (leído el 17 nov 2026)

| Trozo | Dónde | Qué hace hoy |
|---|---|---|
| El cuadro en Hoy | `ui/escenas/CuadroDelDia.kt` (`CuadroDelDia`, `BotonCuento`, `MarcaEspecial`), llamado desde `ui/pantallas/Dia.kt` (≈ l. 249) | El paisaje 4:3 con el gato; debajo, el botón ancho del cuento. Toque corto: el gato (caricia = vibración `Sentidos.Evento.TOQUE`). Pulsación larga de 700 ms: «Hoy en la historia», con la banderita que se llena. La marca de día especial va sobre el borde. **Desde el 17 nov 2026:** la pulsación larga abre el cuadro a pantalla completa (sin botón de ampliar), «Hoy en la historia» queda solo en su botón (con un reloj de arena) y la banderita sale solo en un día especial del personaje, como su marca (la estrellita ya no) |
| El dibujo y sus gestos | `ui/escenas/VistaEscena.kt` (`VistaEscena`, `pintarQuieta`, `MemoriaQuieta`, `gestosDelCuadro`) | Tres capas: quieta (imagen pintada fuera del hilo con `pintarQuieta`), viva (15 cuadros/s) y el gato (`CapaGato`). Un solo detector de gestos. `pintarQuieta(escena, momento, ancho, alto, densidad)` no toca la pantalla |
| El motor de escenas | `ui/escenas/EscenaVida.kt` (`EscenaVida`, `Momento`, `escenaDe`, `CuadroHoy`, `cuadroDeHoy`) | 16 temas con escena hoy (Romano, Urbe, Neón, Helénico, Medieval, Distopía, Vapor, Ruinas, Seda, Gótico, Arcano, Espacio, Al-Ándalus, Renacimiento, Pirata, Revolución). `Momento.hora` va en tramos de 20 min; `Momento.sello` decide cuándo se repinta |
| Las banderitas | `ui/escenas/Banderitas.kt` (`Banderita`, `banderitaHistoria(tema)`) | Una por tema con escena (26 × 40 dp), la de «Hoy en la historia». Sin texto |
| El gato del cuadro | `ui/escenas/GatoCuadro.kt` (`CapaGato`), `GatoDibujo.kt`, `GatoEsqueleto.kt`, `Pelajes.kt` | La caricia avisa por `alAcariciar`; **no se guarda nada** (D.190) |
| Día especial | `vidas/Modelos.kt` (`DiaEspecial`: `id`, `fecha` MM-DD, `duracion` de 1 a 365, `tipo`, `titulo`, `escena`, `certeza`), `vidas/Ficha.kt` (`FichaDelDia.especiales`, `EspecialDelDia(dia, inicio, numero)`, `FichaDelDia.inicios(cal, anio)`) | «Hoy es especial» = `FichaDelDia.de(dia, vida).especiales` no vacío. Cuentan los que empezaron en el año y los del anterior que no terminan. Desde 2032 los sucesos personales se sortean de nuevo (`inicios`) |
| El cuento | `vidas/Cuento.kt` (`Cuento.de(dia, vida, hora)`), pantalla `ui/pantallas/OtrasVidas.kt` (`PantallaCuento`, `HoyEnSuVida`), ruta `cuento?vida=<id>` en `ui/MainActivity.kt` | Siempre el cuento del día lógico de hoy; abrirlo no deja rastro. Arriba, la fila de las vidas con cuento del tema |
| Hoy en la historia | `historia/Efemerides.kt`, `ui/pantallas/HoyEnLaHistoria.kt` (`PantallaHoyEnLaHistoria`, ruta `historia_hoy`) | Caché en `filesDir/historia/`, fuera del respaldo. «en la época de X ›» abre `cuento?vida=<id>` |
| Lo guardado | `data/Modelo.kt` (`Config`, `SCHEMA_V`), `data/Repo.kt` (`Repo.cambiarConfig`, sincronizado), `data/Importar.kt` (`combinarConfig`) | `Config` es **un solo documento JSON** en la colección `config`; viaja entero en `vm_respaldo.json`. Cosas parecidas viven ahí: `frases_guardadas`, `campanas`, `logros_vistos`, `hall_logros` |
| Respaldo de archivos | `data/Respaldos.kt` (`escribirZip`, `restaurarArchivo`), `data/FotoDelDia.kt`, `data/Portadas.kt` | El .zip lleva `portadas/` y `fotos_dia/` (copias de `filesDir`); al restaurar se copian las que falten. La cápsula del año lleva `fotos_dia/` de ese año |
| Exportación | `data/Exportar.kt`, `data/Diccionario.kt` (`DESDE`), `assets/vestigium.R` | Las preferencias de `Config` (esquemas 24 y 26) no se exportan |
| Avisos | `avisos/Avisos.kt` (`FAMILIAS`, `tono(base)` → «aviso» o «solemne», `nombreSonido` → `n_<tema>_<tono>`, `canal(ctx, base)` con un canal por tema «<base>_<tema>_<serie>», `serialDe`, `Voz`) | 8 familias; el sonido va por nombre (`android.resource://…/raw/n_<tema>_<tono>`). Básico no tiene sonido propio |
| El pulso | `avisos/Trabajo.kt` (`avisos(ctx)`, `diario(ctx)`, `yaFue`/`marcar` sobre `Config.disparos`, que se olvidan a los 40 días) | Cada 15 min. Los avisos de una vez al día usan `marcar("clave:$hoy")` |
| Sonido en la app | `ui/Sentidos.kt` (`SoundPool`, solo con `Config.sonidos` y el teléfono en modo normal), `ui/Musica.kt` (`MediaPlayer`, la presentación del tema) | `Config.sonidos` viene **apagado** por omisión |
| Muebles de Vida | `ui/vida/DisenoVida.kt` (`DisenoVida`, `MuebleVida`, `EstadoMueble`, `disenoVida(tema)`), 39 `ui/vida/Vida<Tema>.kt`; pantalla `ui/pantallas/Vida.kt` (`PantallaVida`) | `MuebleVida` es un enum que los 39 temas usan en un `when` exhaustivo (`override fun area`) |
| Sonidos | `herramientas/afinados.py` (bloques e instrumentos por tema), `herramientas/musica.py` (importa `afinados`) | Escriben `.ogg` en `res/raw` con ffmpeg |

---

## 2. Lo que se guarda (esquema 27) — **hecho**

### 2.1 Dónde y por qué

Todo en **`Config`**, como `frases_guardadas`, `campanas` y `logros_vistos`:
- viaja solo en el respaldo y en Drive (va dentro de `vm_respaldo.json`), sin tocar `Repo`, `Db`, `Respaldo` ni los conteos del manifiesto;
- se lee y se escribe con `Repo.cambiarConfig`, que ya tiene candado (el pulso y la interfaz lo cambian a la vez);
- una colección nueva en la base habría pedido tocar la carga en `Repo.init`, la clase `Respaldo`, `conteoDe`, la papelera y la importación de cada colección: más riesgo para datos que no son medidas.

**El costo:** `Config` se reescribe entera en cada cambio. Con un uso alto (dos cuentos al día, 8 claves de uso diarias, 50 cuadros al año) crece unos **100 a 150 KB por año** (unos 80 KB de cuentos leídos, 40 de días de uso y 15 de cuadros). A los diez años, de 1 a 1,5 MB por escritura: SQLite lo escribe en unos milisegundos, pero codificarlo en JSON tarda más y, cuando lo cambia la interfaz, pasa en el hilo de la pantalla (no lo medí). No es un problema en los primeros años; si se nota, se mudan a una colección propia.

### 2.2 Los campos (`data/Modelo.kt`)

| Campo | Tipo | Por omisión | Para qué |
|---|---|---|---|
| `seguidos` | `List<Seguido(vida, desde, hasta?)>` | vacío | A quién sigues. Dejar de seguir pone `hasta` y **no borra** (el logro A9 necesita «desde cuándo»). Tope: 6 activos (`Seguir.TOPE`) |
| `avisos_historia` | `Boolean` | `true` | El interruptor de la notificación (Ajustes › Avisos) |
| `historia_avisada` | `Map<String, String>` | vacío | «vida\|id del día especial\|inicio» → instante del aviso. Un día especial dura hasta 365 días: se avisa **una vez por cada día especial** aunque dure 30, y no depende de `disparos` (que se olvidan a los 40 días). Se olvida a los 400 días de su inicio (`Seguir.podarAvisos`) |
| `cuentos_leidos` | `List<CuentoLeido(vida, dia, leido, ts?, especiales)>` | vacío | Uno por personaje y día del cuento. `leido` es el día lógico de la lectura: `leido == dia` es «el mismo día». `especiales` guarda los ids y el inicio de los días especiales de ese día **tal como eran** (si se corrige un calendario, lo leído no cambia; propuesta de `LOGROS_NUEVOS.md` 4.1). Se queda la primera lectura |
| `uso_dias` | `Map<String, List<String>>` | vacío | Clave → días lógicos sin repetir y en orden. Solo las claves de `UsoDias` (abajo) |
| `cuadros_guardados` | `List<CuadroGuardado(id, tema, vida, dia, hora, guardado, archivo, claves)>` | vacío | Cada «me gusta». `guardado` es el instante (lo pidió `LOGROS_NUEVOS.md`, sección 4); `hora` la del momento pintado; `claves` las claves de escena del día (para A13 y para el pie de la galería) |
| `ambiente_cuadro` | `Boolean` | `true` | El ambiente del cuento se puede apagar sin apagar los demás sonidos. Suena solo con `sonidos` encendido y el teléfono en modo normal. **Lo agregué sin que el autor lo pidiera**, para no tener que subir el esquema otra vez si lo quiere (ver 7) |

Las claves de `UsoDias`: `historia_hoy` y `historia_epoca` (los anota el encargo B); `respaldo_ok`, `ensayo_ok`, `exportar`, `widget`, `fondo_escena` y `tema:<id>` quedan **declaradas para la parte 9, pero nadie las anota todavía** (ver 7, pregunta 3). **No hay clave del gato:** el autor no contestó la pregunta 3 de `LOGROS_NUEVOS.md` y sigue valiendo D.190 (las caricias no se guardan). Tampoco ninguna de Privado ni de Sustancias: `UsoDias.anotar` rechaza cualquier clave que no sea de su lista.

### 2.3 Las funciones (`data/Historias.kt`, puras)

Todas reciben la `Config` y devuelven la nueva (o la misma, si no cambia nada). Se usan así: `Repo.cambiarConfig { c -> Seguir.seguir(c, vida, Fechas.diaLogico()) }`.

| Objeto | Funciones |
|---|---|
| `Seguir` | `TOPE` (6), `activos(c)`, `sigue(c, vida)`, `puedeSeguir(c, vida)`, `seguir(c, vida, hoy)`, `dejar(c, vida, hoy)`, `claveAviso(vida, especial, inicio)`, `avisado(c, clave)`, `anotarAviso(c, clave, ts, hoy)`, `podarAvisos(c, hoy)` |
| `CuentosLeidos` | `sePuedeLeer(dia, hoy)` (nunca un día futuro), `leido(c, vida, dia)`, `marcar(c, vida, dia, hoy, ts, especiales)`, `mismoDia(l)` |
| `UsoDias` | las claves, `FIJAS`, `tema(id)`, `valida(clave)`, `anotar(c, clave, dia)`, `dias(c, clave)` |
| `CuadrosGuardados` | `CARPETA` («cuadros»), `ANCHO` 1200, `ALTO` 900, `CALIDAD` 90, `idValido`, `nombre(id)`, `nuevo(…)`, `guardar(c, g)`, `quitar(c, id)`, `todos(c)`, `tramo(hora)`, `yaGuardado(c, tema, vida, dia, hora)` (el mismo tramo de 20 min), `sobrantes(c, nombres)` |
| `combinarHistorias(c, r)` | Lo del esquema 27 al importar un respaldo con datos aquí: unión sin perder nada, el tope respetado, la primera lectura de cada cuento, solo claves válidas. Lo llama `Importar.combinarConfig` |

### 2.4 Las imágenes (`data/CarpetaCuadros.kt`, con Android)

- **Carpeta:** `filesDir/cuadros/<id>.jpg`. **Formato:** JPEG al 90 %, 1200 × 900 (4:3, como el cuadro): a todo el ancho de un Pixel en vertical (1080 px) se ve nítido, y cada una pesa unos 200 a 350 KB (el grano del gouache comprime mal). PNG pesaría 4 a 6 veces más.
- `CarpetaCuadros.escribir(ctx, id) { archivo -> … }` escribe primero en «.nuevo» y renombra al final: nunca queda una imagen a medias con su nombre. `archivo(ctx, g)`, `borrar(ctx, g)`, `limpiar(ctx, c)` (borra las que ningún cuadro usa, salvo las de la última hora).
- **Respaldo:** entran. `Respaldos.escribirZip` pone `cuadros/` en el .zip (también en Drive, que sube ese mismo .zip) y `restaurarArchivo` copia las que falten (solo nombres válidos). Con 100 cuadros, el respaldo crece unos 25 MB; las fotos del día ya suman unos 150 MB al año, así que no cambia el orden de magnitud.
- **No entran** en la exportación para R (`Exportar.zip`) ni en la cápsula del año: son el dibujo de la app, no un dato tuyo. Sus datos sí van en la cápsula, dentro de `vm_respaldo.json`. La galería lo debe decir en una línea (encargo C).

### 2.5 Exportación: nada

Ningún campo nuevo se exporta. Es estado de la app (qué leíste, a quién sigues, qué te gustó), no una medida de tu día, y no sirve para comparar años; exportarlo obligaría a fijarlo con la congelación. Lo dice una nota en `Diccionario.kt` (junto a `VERSION_ESQUEMA_25`) y la fila D.199. Si algún día se quiere analizar, se exporta en una tabla nueva, sin tocar las que ya existen. `vestigium.R` no cambia.

### 2.6 Migración y pruebas

Una configuración del esquema 26 se lee con todo vacío y los dos interruptores encendidos (`EsquemaVeintisietePrueba`). Pruebas JUnit puras, **corridas de verdad** en un proyecto Gradle de escritorio que compila los archivos reales (sección 8):
- `app/src/test/.../EsquemaVeintisietePrueba.kt` (4): la configuración vieja, los registros mínimos, ida y vuelta;
- `app/src/test/.../HistoriasPrueba.kt` (13): el tope, dejar y volver a seguir, el aviso que no se repite y se olvida a los 400 días, nunca un cuento futuro, la primera lectura, los días de uso sin repetir y sin claves ajenas (gato, privado, sustancias), los cuadros, y la combinación al importar;
- `app/src/test/.../vidas/DiasEspecialesPrueba.kt` (5): con todos los calendarios reales.
- `EsquemaCatorcePrueba.esquema` pasa a 27.

---

## 3. Los puntos de enganche — **hechos**

Ya están llamados desde su sitio; cada encargo llena los suyos sin tocar el sitio que los llama.

| Enganche | Archivo | Firma | Lo llena |
|---|---|---|---|
| Familia de canal | `avisos/Avisos.kt` | `const val CANAL_HISTORIA = "historia"` (todavía **fuera** de `FAMILIAS` y `tono`) | A |
| Días especiales de hoy y pasados | `vidas/DiasEspeciales.kt` (**completo y probado**) | `DiasEspeciales.hoy(vida, hoy)`, `conEspecialHoy(vidas, hoy): List<Pair<String, List<EspecialDelDia>>>`, `pasados(vida, hoy, desde = PRIMER_DIA): List<Especial>` (del más reciente al más viejo, nunca futuros), `PRIMER_DIA` = «2026-01-01» | — (A lo usa) |
| Botón seguir | `ui/seguir/Seguir.kt` | `@Composable fun BotonSeguir(vida: OtrasVidas.Vida, modifier: Modifier = Modifier)` — llamado en `PantallaCuento`, debajo del retrato | A |
| Banderitas en Hoy | `ui/seguir/Seguir.kt` | `@Composable fun AvisosSeguidos(hoy: String, nav: Nav, modifier: Modifier = Modifier)` — llamado en `Dia.kt` debajo del cuadro o del gato de pixel | A |
| Pantalla de seguidos | `ui/seguir/Seguir.kt` | `@Composable fun PantallaSeguidos(nav: Nav)` — ruta `seguidos` (en `MainActivity` y en el buscador) | A |
| Cuento de otro día | `ui/pantallas/OtrasVidas.kt` | `PantallaCuento(nav, vidaId, dia: String? = null)`; ruta `cuento?vida=<id>&dia=<AAAA-MM-DD>`. Un día futuro se queda en hoy; con un día pasado no sale la fila de vidas | B (el resto de la pantalla) |
| El cuadro de una vida | `ui/escenas/EscenaVida.kt` (**completo**) | `fun cuadroDeVida(vida: String, dia: String): CuadroHoy?` — la escena de su tema con su ficha de ese día (Neón: cada uno la suya) | — (B y C lo usan) |
| Pantalla completa | `ui/escenas/CuadroCompleto.kt` | `@Composable fun PantallaCuadroCompleto(nav: Nav, tema: String, dia: String)` — ruta `cuadro?tema=<id>&dia=<AAAA-MM-DD>` | B |
| Pintar un cuadro | `ui/galeria/Galeria.kt` | `fun pintarCuadro(cuadro: CuadroHoy, hora: Double, ancho = 1200, alto = 900): ImageBitmap` (hoy, solo la parte quieta) | C |
| Guardar un cuadro | `ui/galeria/Galeria.kt` | `suspend fun guardarCuadro(ctx: Context, cuadro: CuadroHoy, hora: Double): CuadroGuardado?` (hoy, `null`) | C |
| «Me gusta» | `ui/galeria/Galeria.kt` | `@Composable fun BotonMeGusta(cuadro: CuadroHoy, hora: Double, modifier: Modifier = Modifier)` — lo ponen B en la pantalla completa y arriba del cuento | C |
| Recuadro en Vida | `ui/galeria/Galeria.kt` | `@Composable fun RecuadroGaleria(nav: Nav)` — llamado en `PantallaVida`, después de Otras vidas | C |
| Galería | `ui/galeria/Galeria.kt` | `@Composable fun PantallaGaleriaCuadros(nav: Nav)` — ruta `galeria_cuadros` (en `MainActivity` y en el buscador) | C |
| Contrato del mueble | `ui/vida/DisenoVida.kt` | `fun areaGaleria(): Rect` y `fun DrawScope.muebleGaleria(e: EstadoMueble, oscuro: Boolean)`, los dos con cuerpo por omisión (delegan en `MuebleGaleriaComun`). **No es una entrada de `MuebleVida`**: agregarla rompería el `when` exhaustivo de los 39 `Vida<Tema>.kt`, que reescriben `area` | — |
| Dibujo por omisión | `ui/vida/MuebleGaleria.kt` | `internal object MuebleGaleriaComun { fun DrawScope.dibujar(e, oscuro, nicho: Rect) }` (hoy vacío) | C |
| Interruptores | `ui/pantallas/Sistema.kt` | «Días especiales de quien sigues» (Avisos, `avisos_historia`) y «Ambiente del cuadro» (Apariencia, con Sonidos encendido, `ambiente_cuadro`) | — (ya funcionan) |

Las pantallas vacías dicen «Todavía en preparación» y los botones vacíos no ocupan sitio: la app compila y se ve igual que antes mientras los encargos trabajan. **Nada de esto pasa a `main` hasta que estén los tres** (`PLAN_MEJORAS.md`, respuestas de la segunda ronda, 6).

---

## 4. Los encargos

Regla para los tres: **`data/` no se toca** (si falta algo, se pide al director). Tampoco la firma, `ui/FondosTelefono.kt`, los cuadros y muebles ya dibujados (`Escena<Tema>*.kt`, `paisaje/**`, `GatoCuadro.kt`, `GatoDibujo.kt`, `GatoEsqueleto.kt`, `Pelajes.kt`, `Banderitas.kt`, `Vida<Tema>.kt`), el tema Psicodelia, `vidas/**` (salvo leerlo) ni `Capturas.kt`: cada uno pone sus capturas de Paparazzi en una clase suya (`CapturasSeguir.kt`, `CapturasCuadroCompleto.kt`, `CapturasGaleria.kt`) con su superficie para `CAPTURAS` («seguir», «cuadro_completo», «galeria»), copiando de `Capturas.kt` lo mínimo para montar una pantalla.

### A · Seguir personajes

**Qué hace.**
1. **Elegir a quién se sigue.** `BotonSeguir` debajo del retrato del cuento: «Seguir a Demeas» / «Lo sigues · dejar de seguir». Con el tope lleno, el botón dice «Sigues a 6: deja a uno para seguir a otro» y lleva a `seguidos`. `PantallaSeguidos`: los que sigues (con «dejar») y, debajo, **todas las vidas con calendario** (`Calendarios.TODOS`), no solo las del tema puesto: seguir es por personaje y sobrevive al cambio de tema. Se entra desde ahí, desde las banderitas de Hoy («ver todos ›») y desde el buscador.
2. **La banderita en Hoy** (`AvisosSeguidos`): con `DiasEspeciales.conEspecialHoy(Seguir.activos(c), hoy)`, una fila de banderitas, una por personaje, con su nombre y el título del día especial. La banderita es la de su tema (`banderitaHistoria(TemaId del vida.tema)`, dibujada llena); para los doce comunes y los temas sin banderita, un distintivo común con los colores del tema puesto (como `MarcaEspecial`). Tocarla abre `cuento?vida=<id>`. Ya leído hoy (`CuentosLeidos.leido`), se ve apagada. Sin ninguno, no ocupa sitio. No se ve en la semana corta (Hoy no la muestra).
3. **La notificación.** En `Trabajo.avisos`, una vez por pulso: si `c.avisos && c.avisos_historia`, desde la hora de despertar (`c.hora_sueno`) hasta las 21:00, para cada día especial de `conEspecialHoy` que no esté `Seguir.avisado` (clave con su `inicio`), **un solo aviso** que junte a todos («Hoy Demeas: Las Panateneas. Kei: …»), y `Seguir.anotarAviso` por cada uno. Así un día especial de 30 días avisa una vez, y si empiezas a seguir a alguien a mitad de uno, te avisa ese día. Tocarlo abre el cuento (si es uno solo) o Hoy. Texto con la voz del tema: un campo nuevo en `Avisos.Voz` **con valor por omisión** (para no tocar las 39 voces de golpe), que A llena tema por tema. La decisión de qué avisar va en un archivo puro de A (`avisos/HistoriaPendiente.kt`, sin Android) con su prueba JUnit.
4. **El canal** `CANAL_HISTORIA`: se agrega a `FAMILIAS`, `tono` (un valor nuevo, «historia»), `nombreCanal` («Días especiales de quien sigues»), `descripcionCanal` e `importancia` (DEFAULT). `nombreSonido` dará `n_<tema>_historia`. `AvisosTemaPrueba` se amplía (la lista de tonos y que cada tema con sonidos tenga el de historia).
5. **El sonido nuevo: uno por tema**, `n_<tema>_historia.ogg`, 38 archivos (Básico sigue con el del teléfono). **Por qué por tema y no uno común:** todas las notificaciones de la app suenan con el sonido de su tema y el canal ya es por tema (Android no deja cambiar el sonido de un canal), así que un sonido común sería el único aviso que no cambia con el tema; y el autor lo pidió «distinto de los demás avisos del tema», no de los demás temas. Se genera con un guion nuevo, `herramientas/sonidos_historia.py`, que importa `afinados` (como `musica.py`) para usar la escala y los instrumentos de cada tema: un motivo corto (1,5 a 2,5 s), distinto del `aviso` (más corto) y del `solemne` (más largo), que suene a «hoy pasa algo»: por ejemplo, tres notas que suben y una campanilla o su equivalente en el tema. Mismo volumen RMS que los demás (`escribir(…, rms_db=-19)`).
6. **Los días especiales pasados** de un personaje, en `PantallaSeguidos` (al tocar a uno): `DiasEspeciales.pasados(vida, hoy)`, agrupados por año, con su tipo y su certeza y una marca si ya lo leíste (`CuentosLeidos.leido(c, vida, e.diaDelCuento)`). Tocar uno abre `cuento?vida=<id>&dia=<e.diaDelCuento>`. Los futuros no salen nunca.

**Archivos que toca:** `ui/seguir/**` (nuevo o los suyos), `avisos/Avisos.kt`, `avisos/Trabajo.kt`, `avisos/HistoriaPendiente.kt` (nuevo), `herramientas/sonidos_historia.py` (nuevo), `res/raw/n_*_historia.ogg` (nuevos), `app/src/test/.../AvisosTemaPrueba.kt`, `CoberturaTemas.kt` (si hace falta la cobertura del sonido), sus pruebas y capturas nuevas.
**No toca:** `OtrasVidas.kt`, `Dia.kt`, `MainActivity.kt`, `Pantallas.kt`, `Sistema.kt` (sus llamadas ya están), `CuadroDelDia.kt`.
**Cómo se comprueba sin SDK:** la lógica de qué avisar y de las banderitas, con JUnit en el proyecto de escritorio (sección 8); los sonidos, oyéndolos y midiendo su RMS y su duración con el guion; las pantallas, en capturas de Actions; la notificación, solo en el teléfono (decirlo).

### B · El cuento y el cuadro

**Qué hace.**
1. **«Hoy en la historia» bajo el botón del cuento**, en `CuadroDelDia`: un botón más bajo que el del cuento, del color secundario del tema, con la banderita del tema dibujada pequeña a la izquierda y «Hoy en la historia». Solo cuando hay `alHistoria` (el interruptor de Privacidad). La pulsación larga se queda (es el gesto de siempre).
2. **El cuadro a pantalla completa** (`PantallaCuadroCompleto`): fondo negro, sin barras del sistema, el cuadro centrado a todo lo que quepa (4:3; en horizontal, toda la pantalla), con su gato y sus caricias, la parte viva animada, y `BotonMeGusta` en una esquina. Un toque fuera del gato (o «atrás») sale. Se entra con un botón pequeño de «ampliar» sobre la esquina de abajo del cuadro de Hoy (los toques del cuadro ya son del gato y de la historia) y tocando el cuadro de arriba del cuento. El cuadro sale de `cuadroDeVida` o, para Hoy, de `cuadroDeHoy`; un día futuro no se abre.
3. **El cuadro arriba en la pantalla del cuento** (`PantallaCuento`): `cuadroDeVida(v.id, diaCuento)`, calculado fuera del hilo de la pantalla; si no hay (los doce comunes, los temas sin escena), no sale nada. Con su gato y su «me gusta».
4. **El ambiente sonoro al abrir el cuento.** Un ambiente por tema con escena, `a_<tema>_ambiente.ogg` (16 hoy; cada tanda de escenas nueva agrega el suyo), **sintetizado** con un guion nuevo, `herramientas/ambientes.py`, que importa `afinados`: el lugar del cuadro (mar y remos en Helénico, fragua y lluvia en Vapor, tráfico y lluvia de neón…), de 40 a 60 s, que empalme consigo mismo (cola cruzada) para ir en bucle sin chasquido. Una versión de noche donde la escena cambie mucho (`a_<tema>_ambiente_noche`), si da tiempo. Se toca con `MediaPlayer` en bucle, volumen 0,35–0,5 («suave, pero no necesariamente muy tenue»), con fundido de entrada de 2 s y de salida de 1,5 s; se calla al salir de la pantalla, al irse la app a segundo plano (ciclo de vida) y si entra una llamada. **Solo suena** con `Config.sonidos`, `Config.ambiente_cuadro` y el teléfono en modo normal, y **no suena si ya hay música** (`AudioManager.isMusicActive`): no se pide el foco del audio para no bajar lo que estés oyendo. En el resto de la app, nada continuo. Va en un archivo suyo (`ui/escenas/AmbienteCuadro.kt`).
5. **El ronroneo del gato.** Al acariciarlo (`alAcariciar` en `CuadroDelDia`, en la pantalla completa y arriba del cuento): un ronroneo corto (1,2 a 2 s) además de la vibración, con las mismas condiciones que los demás sonidos (`Config.sonidos`, modo normal). Uno común, `s_gato_ronroneo.ogg` (el gato es el mismo animal en todos los temas), sintetizado en el mismo guion de ambientes: ruido rosa modulado a 25 Hz con un bajo de 50 a 150 Hz. **No se guarda nada** (D.190).
6. **Leer = llegar al final.** En `PantallaCuento`, cuando el último párrafo del cuento queda a la vista (un marcador después de `HoyEnSuVida`, con `onGloballyPositioned` contra el alto de la ventana), `Repo.cambiarConfig { CuentosLeidos.marcar(it, v.id, diaCuento, Fechas.diaLogico(), Fechas.ahoraIso(), especiales) }`, con los `especiales` de `cuento.ficha.especiales` (`EspecialLeido(e.dia.id, e.inicio.toString())`). Una vez por apertura. Un día pasado leído hoy cuenta como leído después.
7. **Los días de uso:** `UsoDias.HISTORIA_HOY` al abrir «Hoy en la historia» y `UsoDias.HISTORIA_EPOCA` al abrir un cuento desde «en la época de…» (`HoyEnLaHistoria.kt`).
8. **El cuento de un día pasado** (`PantallaCuento` con `dia`): el título de la sección dice el día («El 21 de marzo, en su vida») en vez de «Hoy, en su vida».

**Archivos que toca:** `ui/escenas/CuadroDelDia.kt`, `ui/escenas/CuadroCompleto.kt`, `ui/escenas/AmbienteCuadro.kt` (nuevo), `ui/pantallas/OtrasVidas.kt` (solo `PantallaCuento` y `HoyEnSuVida`), `ui/pantallas/HoyEnLaHistoria.kt`, `herramientas/ambientes.py` (nuevo), `res/raw/a_*_ambiente*.ogg` y `s_gato_ronroneo.ogg` (nuevos), sus pruebas y capturas.
**No toca:** `VistaEscena.kt` (si hace falta un parámetro nuevo, se pide), `Avisos.kt`, `Trabajo.kt`, `Dia.kt`, `MainActivity.kt`, `ui/galeria/**`, `ui/seguir/**`.
**Cómo se comprueba sin SDK:** el fundido y las condiciones de sonido, como funciones puras con JUnit; los ambientes y el ronroneo, oyéndolos y midiendo que el bucle empalme (la diferencia entre el final y el principio); las pantallas, en capturas; el sonido en la app y «llegar al final», solo en el teléfono (decirlo).

### C · La galería

**Qué hace.**
1. **Pintar y guardar** (`pintarCuadro`, `guardarCuadro`): fuera de la pantalla (`Dispatchers.Default`), a 1200 × 900, la parte quieta, la viva en el instante quieto (`RELOJ_QUIETO`) y, si se puede sin tocar `GatoCuadro.kt`, el gato sentado en su primera ancla; si no, sin gato, y se dice. La densidad, la de un teléfono de 412 dp (`ancho / 412f`), para que los trazos en dp salgan iguales. JPEG con `asAndroidBitmap().compress(JPEG, CuadrosGuardados.CALIDAD, …)` dentro de `CarpetaCuadros.escribir`; **después** `CuadrosGuardados.nuevo` (con `Repo.nuevoId()`, `Fechas.ahoraIso()` y `Momento(ficha, hora).claves`) y `Repo.cambiarConfig { CuadrosGuardados.guardar(it, g) }`.
2. **El «me gusta»** (`BotonMeGusta`): un corazón (o el motivo del tema) que se llena; `CuadrosGuardados.yaGuardado` dice si ya está puesto en este tramo de 20 min. Quitarlo borra los datos y la imagen, con «deshacer» (`ui/Deshacer.kt`). Mientras pinta, se nota (unas décimas de segundo).
3. **La galería** (`PantallaGaleriaCuadros`): cuadrícula de miniaturas (decodificadas con `inSampleSize`), del más nuevo al más viejo; al tocar una, la imagen grande con sus datos (tema, personaje, día, hora, «guardado el …», sus claves si las hay) y «quitar». Una línea: «Las imágenes viajan en el respaldo completo; no van en la exportación para R». Al abrirla, `CarpetaCuadros.limpiar` en segundo plano.
4. **El mueble nuevo de la pestaña Vida** (`RecuadroGaleria` y `MuebleGaleriaComun.dibujar`): como `RecuadroOtrasVidas`, un lienzo de ≈ 360 × 190 dp con `with(disenoVida(Estilo.tema)) { muebleGaleria(estado, oscuro) }` y, en `areaGaleria()`, las últimas 3 o 4 miniaturas. **El dibujo por omisión vale para los 39 temas** solo con los colores de `EstadoMueble` (un riel o una moldura con cuadros colgados, sus marcos, la sombra en la pared, luz con dirección y noche de verdad), con la receta de `MUEBLES.md`. Sin cuadros, el mueble vacío con una invitación («Dale ♥ a un cuadro para guardarlo aquí»). Cada tema podrá dibujar el suyo después, en su `Vida<Tema>.kt`, reescribiendo `muebleGaleria` (no en esta parte).
5. **El arnés:** un caso más en `herramientas/arnes_muebles/` (modo `vida`) para ver el mueble en claro y oscuro con 0, 1 y 5 cuadros.

**Archivos que toca:** `ui/galeria/**`, `ui/vida/MuebleGaleria.kt`, `herramientas/arnes_muebles/**` (solo el caso de la galería), sus pruebas y capturas.
**No toca:** `DisenoVida.kt` (el contrato ya está), los `Vida<Tema>.kt`, `Vida.kt`, `MainActivity.kt`, `Pantallas.kt`, `VistaEscena.kt`, `GatoCuadro.kt`, `CuadroDelDia.kt`, `OtrasVidas.kt`.
**Cómo se comprueba sin SDK:** el mueble, en el arnés (dibujarlo y mirarlo al menos cuatro veces, con `ARNES_TEMAS=0,1,5`); pintar el cuadro fuera de pantalla también se puede probar en el arnés si compila `escenas/` (si no, en capturas); guardar en disco y el respaldo, solo en el teléfono.

### ¿Un cuarto encargo?

Si se quiere repartir en cuatro: **D · Los sonidos** (los 38 `n_<tema>_historia`, los ambientes y el ronroneo, con sus dos guiones), que no comparte archivos con nadie. A y B solo nombrarían los recursos: el canal de A va por nombre (no depende de que existan para compilar); B sí usa `R.raw.a_<tema>_ambiente` y tendría que esperar a D o buscarlos por nombre.

### Quién toca qué (para que nadie se pise)

| Archivo | A | B | C |
|---|---|---|---|
| `ui/seguir/**` | ✔ | | |
| `avisos/Avisos.kt`, `avisos/Trabajo.kt`, `avisos/HistoriaPendiente.kt` | ✔ | | |
| `herramientas/sonidos_historia.py`, `res/raw/n_*_historia.ogg` | ✔ | | |
| `ui/escenas/CuadroDelDia.kt`, `CuadroCompleto.kt`, `AmbienteCuadro.kt` | | ✔ | |
| `ui/pantallas/OtrasVidas.kt` (`PantallaCuento`, `HoyEnSuVida`), `HoyEnLaHistoria.kt` | | ✔ | |
| `herramientas/ambientes.py`, `res/raw/a_*`, `s_gato_ronroneo.ogg` | | ✔ | |
| `ui/galeria/**`, `ui/vida/MuebleGaleria.kt`, `herramientas/arnes_muebles/**` | | | ✔ |
| `data/**`, `vidas/**`, `MainActivity.kt`, `Pantallas.kt`, `Dia.kt`, `Vida.kt`, `Sistema.kt`, `DisenoVida.kt`, `EscenaVida.kt`, `VistaEscena.kt` | — | — | — |

---

## 5. La congelación de 2027

- **Nada de esto cambia qué se registra ni cómo se calcula.** Lo nuevo son campos opcionales de `Config` que no son medidas y no se exportan. `SCHEMA_V` sube una sola vez (27), con su fila D.199.
- **No cuenta hacia atrás:** lo leído, seguido o usado antes de la versión que trae el esquema 27 no está guardado. Los logros de la parte 9 que salgan de estos campos empiezan a contar desde esa versión.
- **Lo que podría chocar, y cómo se evitó:**
  - **Las caricias del gato.** El autor pide que ronronee; las caricias siguen sin guardarse (D.190), ni sus días. Si el autor quiere el logro «El gato» (A14), hace falta su respuesta, y entonces es una clave nueva en `UsoDias`, sin subir el esquema (el campo ya existe).
  - **«Leer» depende del código, no de un dato:** si después se cambia qué cuenta como «llegar al final», lo guardado antes no cambia (es la primera lectura, tal cual). Cambiar la regla no rompe la comparación porque no es una medida, pero conviene anotarlo en el Apéndice D si pasa.
  - **Los días especiales pasados se calculan del calendario de hoy.** Si se corrige un calendario, la lista de pasados cambia; lo ya leído no (se guardó con sus ids e inicios).
  - **El sonido nuevo de la notificación** cambia cuándo y cómo te enteras, no lo que registras. Un aviso más al día puede hacer que abras la app más seguido (y registres más esos días). Es lo mismo que ya pasa con los demás avisos; no lo trato como hallazgo, pero el capítulo 9 (9.7, reactividad) lo podría mencionar cuando se actualice.

---

## 6. Lo que el paper necesita

- **Hecho en este trabajo:** la fila D.199 del Apéndice D y la línea de `cuadros/` en el capítulo 10 (10.4.1, qué contiene el respaldo).
- **Para los encargos, nada**: son pantallas, sonidos y avisos que no cambian ningún cálculo (regla de `CLAUDE.md`). Si A agrega una familia de avisos, el capítulo 9 no la describe hoy, así que no hay que tocarlo.

---

## 7. Decisiones propias y preguntas al autor

**Decisiones mías** (dichas para que se puedan revisar):
1. Todo en `Config`, no en una colección nueva (2.1).
2. `ambiente_cuadro`, un interruptor que el autor no pidió, para no subir el esquema si lo quiere.
3. Seguir guarda la historia (`hasta`) en vez de borrar.
4. `PRIMER_DIA` de los días especiales pasados: el 1 de enero de 2026, para que haya qué leer ya en el año del avance. **[Diseño propio]**.
5. Un aviso por día especial, aunque dure 30 días, y un solo aviso que junte a todos los seguidos del día.
6. JPEG 1200 × 900 al 90 % para las imágenes.
7. Sonido de historia por tema (38), ambiente por tema con escena (16) y ronroneo común (1).
8. El ambiente no suena si ya hay música y no pide el foco del audio.

**Preguntas:**
1. **El ambiente y `Config.sonidos`.** Los sonidos de la app vienen **apagados** por omisión; tal como se pidió («respeta el ajuste de sonidos»), el ambiente tampoco suena hasta que los enciendas. ¿Está bien, o quieres que el ambiente dependa solo de su propio interruptor?
2. **El gato:** ¿se queda sin logro (D.190) o se guardan solo los días en que lo acariciaste (A14 de `LOGROS_NUEVOS.md`)? Hoy no se guarda nada.
3. **Las claves de uso de la parte 9** (respaldo verificado, ensayo, exportación, widget, tema de cada día) están declaradas pero nadie las anota. Cada día que pasa sin anotarlas es un día que esos logros no contarán. ¿Las conecto ya (unas líneas en `Respaldos`, `Exportar` y el pulso), aunque los logros se hagan después?
4. **¿Leer exige un tiempo mínimo?** Hoy basta llegar al final (lo que dijiste). Un cuento se lee en minuto y medio; bajar de golpe hasta el final también contaría.

---

## 8. Cómo se comprobó

- **Pruebas que corrí de verdad:** un proyecto Gradle de escritorio en el espacio de trabajo de la sesión (`prueba_parte8/`, fuera del repositorio) que compila los archivos reales de `data/Modelo.kt`, `data/Historias.kt`, `data/Catalogo.kt`, `data/Fechas.kt`, `data/Texto.kt`, `data/OtrasVidas.kt` y `vidas/**`, con kotlinx-serialization 1.7.3 (la misma de la app) y unos sustitutos copiados tal cual de lo que importa Android (`Suplementos.INICIALES`, `Privado.TIPOS`, `CiudadVisitada`, `SitioMarcado`, `FechaPropia`). Corren `EsquemaVeintisietePrueba`, `HistoriasPrueba` y `DiasEspecialesPrueba`: **22 pruebas, todas pasan.** Los encargos pueden ampliarlo para sus archivos puros.
- **Lo que no pude compilar** (no hay SDK de Android): `CarpetaCuadros.kt`, los cambios de `Respaldos.kt`, `Importar.kt`, `Avisos.kt`, `MainActivity.kt`, `OtrasVidas.kt`, `Dia.kt`, `Vida.kt`, `Pantallas.kt`, `Sistema.kt`, `DisenoVida.kt`, `EscenaVida.kt` y los enganches nuevos (`ui/seguir/`, `ui/galeria/`, `ui/escenas/CuadroCompleto.kt`, `ui/vida/MuebleGaleria.kt`). Los releí y comprobé con grep cada nombre que usan; los compila la corrida de Actions que lance el director.
- **Lo que solo se ve en el teléfono:** el respaldo con `cuadros/` y su restauración, la notificación y su sonido, el ambiente, el ronroneo.
