# Psicodelia v2: el plan técnico

Cómo llevar al código la guía [`psicodelia.md`](psicodelia.md) (versión 2, 16 nov 2026). **Aquí no se programa nada todavía**: es el orden de trabajo, dónde va cada cosa y qué pruebas cambian. Las rutas de código son de `app/src/main/java/io/github/ruddy2003/vestigium/`.

**Lo que no cambia en ningún momento:** no se guarda nada nuevo (no sube `SCHEMA_V`, no se tocan `Exportar`, `Diccionario` ni `vestigium.R`); el tema solo **lee** `Repo.consumos`, `Repo.config` y la hora. No cambia ningún cálculo que el paper describa: la intensidad es una lectura nueva de `Sustancias.nivelEn`, que ya existe, y no se exporta. Tampoco se toca la firma.

## 1. «Cara» e «intensidad» de un tema, sin romper los otros 38

### Lo que hay hoy (leído el 16 nov 2026)

- `ui/Tema.kt`: `TemaVestigium` decide `oscuro = modoOscuro(tema)` y arma `MaterialTheme(colorScheme = esquemaDe(tema, oscuro), typography = tipoDe(tema, oscuro), shapes = formas(radioDe(tema)))`. Casi todo lo demás recibe `(tema, oscuro)`: las interfaces de dibujo (`DisenoIdeas.mueble(d, oscuro)`, `compartimento(c, oscuro)`, `DisenoComun`, `DisenoBiblioteca`, `DisenoVida`, `DisenoLogros`) son funciones de `DrawScope`, no `@Composable`, así que **no pueden leer un `CompositionLocal`**.
- `ui/ModoHora.kt`: `ModoHora.esDeNoche` (21:00 a 5:00), `oscuroSegun(modo, soloOscuro, telefonoOscuro, noche)`, y `RelojModo.noche`, un estado de Compose que pone al día `RelojDelModo()` con una espera hasta el próximo cambio (no un sondeo) y al volver a la app. Hoy `RelojDelModo()` solo corre con `config.modo == "hora"`.
- Fuera de Compose: `modoOscuroDe(ctx, tema)` (arranque, fondo vivo, reportes, avisos); `Widgets.colores()` (siempre oscuros); `Huella.kt`; `IconoApp` (alias de actividad); los avisos por canal.

### La propuesta

**Un estado global de instantánea (snapshot state), como `RelojModo`, que los dibujos leen directamente.** Leer un `mutableStateOf` dentro de un `DrawScope` hace que Compose vuelva a dibujar solo ese dibujo cuando cambia, sin recomponer la pantalla; así llega a las funciones de dibujo sin tocar sus firmas.

En un archivo nuevo, `ui/CaraTema.kt`:

```kotlin
/** Las dos caras de un tema que las tiene. Hoy solo Psicodelia: BASE = la ciudad, INTENSA = la psicodelia. */
enum class Cara { BASE, INTENSA }

/** ¿Qué cara toca? Puro, sin Android: la hora decide; los temas sin caras siempre van en BASE. */
fun caraSegun(tema: TemaId, noche: Boolean): Cara = if (tema.conCaras && noche) Cara.INTENSA else Cara.BASE

/** La intensidad, de 0 a 1, a partir de los consumos. Puro y con pruebas (ver 1.2). */
object Intensidad { fun de(consumos: List<Consumo>, ocultas: List<String>, ahora: Long): Float = … }

/** Lo que leen los dibujos. */
object EstadoTema {
    val cara: Cara get() = forzada ?: caraSegun(Estilo.tema, RelojModo.noche)
    var intensidad by mutableFloatStateOf(0f); internal set   // la animada, la que se dibuja
    /** Para las capturas y el arnés: fija la cara y la intensidad. */
    var forzada by mutableStateOf<Cara?>(null)
    fun forzar(cara: Cara?, i: Float?) { … }
}
```

- `TemaId` gana dos propiedades con valor por omisión `false`, `conCaras` y `conIntensidad`, que solo `PSICODELIA` pone en `true`. Los otros 38 no cambian ni una línea.
- **El esquema y la letra:** `esquemaDe(tema, oscuro)` y `tipoDe(tema, oscuro)` siguen con su firma; dentro, la rama `TemaId.PSICODELIA -> psicodeliaEsquema(oscuro, EstadoTema.cara)` lee la cara. Lo mismo `radioDe(tema)` (dos `Esquinas`). Los `when` siguen siendo exhaustivos y sin ramas agrupadas (principio 1a).
- **El color con intensidad:** el `colorScheme` de Material **no** se anima con `i` (recomponer todo el árbol 60 veces por segundo durante 4 s es caro). Los tres papeles que cambian (primario, secundario, terciario) los leen ya interpolados los componentes propios de Psicodelia (`ComunPsicodelia`, sus botones y fichas) desde `TrazosTinta.rol(papel, oscuro, EstadoTema.intensidad)`. El `colorScheme` lleva los valores de reposo, que son legibles. Así, lo genérico de Material (un `Switch`, un diálogo) se queda en reposo, y lo del tema se calienta.
- **Lo que se guarda en caché:** todo `remember(oscuro) { … imagen … }` de los dibujos de Psicodelia pasa a `remember(oscuro, cara, escalón(i))`, con `escalón(i) = round(i * 8) / 8` (nueve escalones), y lo que se mueve se dibuja encima en cada cuadro, como ya hacen los cuadros.

### 1.1 Cómo se actualiza

- **La cara:** al cruzar las 21:00 y las 5:00, con el `RelojModo` que ya existe. Cambia una línea de `TemaVestigium`: el reloj corre si `config.modo == "hora"` **o si `tema.conCaras`** (Psicodelia cambia de cara con cualquier modo). Sin sondeos: una espera hasta el próximo cambio y la puesta al día al volver a la app (`ON_RESUME`).
- **La intensidad:** un `@Composable MedidorIntensidad()` que va en `TemaVestigium` solo si `tema.conIntensidad`:
  - lee `Repo.consumos` y `Repo.config.sustancias_ocultas`, que ya son estados: al empezar, leer o cerrar un consumo (`Sustancias.nuevo`, `lectura`, `cerrar` pasan por `Repo.guardarConsumo`, que reemplaza la lista) se recalcula solo;
  - **si hay un consumo de cannabis abierto**, y solo entonces, vuelve a calcular cada 2 minutos (el nivel interpolado cambia despacio; con nada abierto no hay ningún temporizador);
  - recalcula también en `ON_RESUME`;
  - anima el valor nuevo hacia `EstadoTema.intensidad` con un `Animatable` (4 s al subir o al cambiar; 10 s al caer a 0).
- **Las pantallas en cero:** las del seguimiento privado y las de Sustancias envuelven su contenido en `SinIntensidad { … }`, que pone a 0 la intensidad que leen sus dibujos (un `CompositionLocal` que los componentes de Psicodelia consultan antes de leer `EstadoTema`). Ver la guía, «La voz», punto 4.

### 1.2 La fórmula, en código y con pruebas

`Intensidad.de` es la fórmula de la guía, sin nada de Android, para probarla con JUnit (`app/src/test/.../IntensidadPrueba.kt`, nueva):

```kotlin
fun de(consumos: List<Consumo>, ocultas: List<String>, ahora: Long): Float {
    if ("cannabis" in ocultas) return 0f
    val n = consumos.asSequence()
        .filter { it.sustancia == "cannabis" }
        .filter { ahora >= Fechas.millis(it.inicio) && ahora <= Sustancias.finEfectivo(it, ahora) }
        .map { Sustancias.nivelEn(it, ahora, ahora) ?: 5.0 }
        .maxOrNull() ?: return 0f
    return (n / 10).toFloat().coerceIn(0f, 1f)
}
```

Casos que la prueba fija: sin consumos, 0; uno abierto con lectura 6, 0.6; abierto sin nivel, 0.5; cerrado hace una hora, 0; abierto de hace 20 horas, 0 (`finEfectivo` lo corta a 4 h); dos a la vez, el mayor; cannabis en `ocultas`, 0; alcohol con nivel 9, 0.

### 1.3 Fuera de Compose: qué sigue la cara y qué se queda fijo

| Superficie | Cara | Intensidad | Por qué |
|---|---|---|---|
| Ícono de la app (`IconoApp`, alias) | fija: ciudad | no | Cambiar el alias dos veces al día reordena el lanzador y puede cerrar la app |
| Sello del día (`Huella.kt`, `huella_psicodelia`) | fijo: ciudad | no | Lo archivado no depende de la hora ni de un consumo |
| Widgets (`widget/Widgets.kt`, siempre oscuros) | por hora, en la tarea de 15 min (`avisos/Trabajo.kt`, que ya llama a `Widgets.actualizar`) | no | Se ven desde la pantalla de inicio; a lo sumo 15 min de retraso al cambiar |
| Fondos del teléfono (`FondosTelefono.kt`) | se ofrecen las dos; no cambian solos | no | El fondo lo pone la persona |
| Textos de los avisos (`avisos/Avisos.kt`, `Voz`) | por hora, al armar el aviso (`caraSegun(tema, ModoHora.esDeNoche(ahora))`) | no | El texto se puede elegir al enviar |
| Sonidos de los avisos (canal) | fijos: ciudad | no | Android fija el sonido del canal al crearlo |
| Sonidos dentro de la app (`Musica.kt`, `Sentidos.kt`) | por cara | no | Un consumo no debe oírse |
| Arranque (`Arranque.kt`, `MainActivity`) | por hora | no | Antes de que haya estado, con `ModoHora` directo |
| Gato (`Gato.kt`, `assets/gato/gato.json`) | por cara | sí, en la banda | Ver la guía |
| Cuadro de «Otras vidas» (cuando exista) | no: lo decide la ficha del día de David | no | El cuadro cuenta la vida de David, no la de la persona |

### 1.4 Lo que tienen que vigilar las pruebas

- `CoberturaTemas`: sin cambios para los 38; para Psicodelia, los recursos de las dos caras (drawables de widget de las dos, el gato con sus claves de cara).
- `UnicidadTemas`: compara los valores de la cara `BASE` de cada tema, como hoy; **además** comprueba que los de la cara `INTENSA` de Psicodelia (letra de títulos, esquinas) no choquen con ningún tema.
- Capturas (Paparazzi, `Capturas.kt`): para Psicodelia, las cuatro variantes más i = 0.5 y 1 con `EstadoTema.forzar`. Las de los demás temas no cambian.

## 2. Inventario de superficies, por tandas

Las cifras son las líneas de hoy (`wc -l`) o cuántas ramas `TemaId.PSICODELIA` tiene un archivo compartido. «Rehacer» quiere decir dibujar de nuevo; «tocar», cambiar valores o textos.

### Tanda 1: base, paleta y letras (pequeña, primero)

| Qué | Archivo | Hoy | Trabajo |
|---|---|---|---|
| `Cara`, `EstadoTema`, `Intensidad`, `MedidorIntensidad`, `SinIntensidad` | `ui/CaraTema.kt` (nuevo) | — | ~150 líneas nuevas |
| Reloj con `conCaras`, medidor | `ui/Tema.kt` (`TemaVestigium`), `ui/ModoHora.kt` | 1445 / 88 | ~10 líneas |
| Cuatro esquemas y dos formas | `ui/Tema.kt` (`psicodeliaClaro/Oscuro`, `radioDe`) | 11 ramas | Reemplazar 2 esquemas por 4 |
| Letras | `ui/Disenios.kt` (`tipoPsicodelia`, `LetraExtra.modak/fredoka`), `herramientas/fuentes.py` (3 salidas), `res/font/` | 2 ramas | Quitar Modak y Fredoka (solo las usa Psicodelia); agregar `franklin_regular/semibold/extrabold` y `bloque_titulo` (Chango renombrada); licencia en `assets/licencias/fuentes_OFL.txt` |
| Colores de dibujo | `ui/PsicodeliaTrazos.kt` | 472 | Rehacer como `TrazosCiudad` y `TrazosTinta` |
| Números, fecha | `ui/Psicodelia.kt`, `Estilo.numeral` | 42 | «(17)» |
| Pruebas | `IntensidadPrueba` (nueva), `PsicodeliaPrueba` | 27 | Ver 1.2 y «(17)» |

### Tanda 2: decoración y componentes

| Qué | Archivo | Hoy | Trabajo |
|---|---|---|---|
| Lo común del tema (marcos, separadores, fondo vivo, encabezados, botones) | `ui/comun/ComunPsicodelia.kt` | 1464 | Rehacer casi todo, en dos pintores |
| Ramas en lo compartido | `Decoracion.kt` (10), `Componentes.kt` (9), `ControlesTema.kt` (5), `IconosPestana.kt` (8), `IconosCinta.kt` (1), `Animaciones.kt` (2), `Celebracion.kt` (2), `Contenedores.kt` (1), `Marcas.kt` (1), `Selectores.kt` (2) | 41 ramas | Tocar; los íconos de pestaña, rehacer (8) |
| Libros, libreta, cartas | `Libros.kt` (20), `LibroDiario.kt` (6), `Cartas.kt` (3), `Hoja.kt` | 29 ramas | Rehacer el periódico del barrio a dos tintas; la calcomanía de flor de las postales se va |

### Tanda 3: muebles (la más grande)

| Qué | Archivo | Hoy | Trabajo |
|---|---|---|---|
| Biblioteca: la repisa de tablas | `ui/biblioteca/BibliotecaPsicodelia.kt` | 2318 | Completar (quitar tópicos, tres entradas, objetos, noche) y separar geometría de pintor |
| Ideario: la cajonera de planos | `ui/ideario/IdeasPsicodelia.kt` | 1515 | Rehacer entero (era el mostrador de la tienda de discos, con ventana) |
| Vida y tablero | `ui/vida/VidaPsicodelia.kt` | 2360 | Rehacer sin ventana; el corcho sobre la mesa de dibujo |
| Revisión | `herramientas/arnes_muebles/` (de otro encargo; se usa, no se toca aquí) | — | Las cuatro variantes más i = 1, al lado de Galia y Art nouveau |

Las tres siguen `MUEBLES.md`, Versión 2. **Separar la geometría del pintor** (una función que dice qué hay y dónde; dos que lo pintan) es lo que hace que las dos caras no dupliquen las 6000 líneas.

### Tanda 4: logros, mapa, salón

| Qué | Archivo | Hoy | Trabajo |
|---|---|---|---|
| Logros y trofeos | `ui/logros/LogrosPsicodelia.kt`, `TrofeosTema.kt`, `TrofeosMas.kt`, `pantallas/Trofeos.kt` (5) | 471 + 7 ramas | Rehacer los 9 trofeos (guía, «Los motivos») |
| Salón y hall | `pantallas/Hall.kt` (10), `HallImagen.kt` (1), `Logros.kt` (1) | 12 ramas | Tocar |
| Mapa | `pantallas/Mapa.kt` (5), `MapasMas.kt` (4) | 9 ramas | Tocar: tintas planas de noche |
| Otras pantallas | `Hoy.kt` (3), `TuAnio*.kt` (4), `Calendario.kt` (2), `Mundo.kt` (2), `Galerias.kt`, `Cinta.kt`, `FrasesGuardadas.kt`, `EstiloGrafica.kt`, `Relacion.kt` (2) | ~18 ramas | Tocar (las cifras de `Hoy.kt` usan Modak: pasan a Libre Franklin) |

### Tanda 5: ícono, sello, papel, widgets

| Qué | Archivo | Hoy | Trabajo |
|---|---|---|---|
| Ícono | `herramientas/iconos.py` (4 menciones), `ic_launcher_psicodelia.xml`, `logo_psicodelia.webp`, `logo_fondo_psicodelia.jpg` | — | Rehacer: la etiqueta del disco |
| Sello | `herramientas/sellos.py` (10), `huella_psicodelia.webp`, `huella_fondo_psicodelia.jpg` | — | Rehacer, de ciudad |
| Papel | `herramientas/arte.py` (4), `papel_psicodelia.jpg` | — | Dos papeles: foto y cartel |
| Widgets | `widget/Widgets.kt` (3), 4 drawables + v31 | — | Ocho drawables (dos caras), elección por hora |
| Fondos del teléfono | `ui/FondosTelefono.kt` (3) | — | Las dos caras |

### Tanda 6: el gato

`herramientas/gato/gato21.py` (22 menciones de Psicodelia en 6164 líneas), `assets/gato/gato.json` (`psicodelia_c`, `psicodelia_o` y las nuevas de la cara de noche), `ui/Gato.kt` (1 rama), `ui/escenas/Pelajes.kt` (1). Quitar gafas y cinta; dos acciones nuevas (guía, «El gato»).

### Tanda 7: sonidos

`herramientas/musica.py` (6), `herramientas/afinados.py` (8), `ui/Musica.kt` (5), `ui/Sentidos.kt` (4), `avisos/Avisos.kt` (canales), y los 17 archivos de `res/raw` de Psicodelia. Un segundo juego para la cara de noche en lo que suena dentro de la app (presentación, logro, anual, tecla); los de los canales se quedan.

### Tanda 8: textos

`ui/nombres/NombresPsicodelia.kt` (222), `Estilo.kt` (17 ramas), `Temas.kt` (5; la descripción de `TemaId.PSICODELIA` en `Tema.kt`), `Frases.kt` (3), `Textos.kt` (2), `NombresNuevos.kt`, `NombresCompletos.kt`, `avisos/Avisos.kt` (`Voz`, `VozEnfoque`), `ciencia/Campanas.kt` (2), `Claves.kt`, `Arranque.kt`, `QuickActivity.kt` (3), `EnfoqueActivity.kt` (2), `reportes/Reportes.kt` (3). Quitar «✿», «☮», «burbuja», «lámpara»; aplicar la línea de la voz (guía, «La voz»), con su prueba (abajo, 3.4).

**Tamaño total:** unas 9000 líneas propias del tema a rehacer o tocar, más ~130 ramas en archivos compartidos. Es del tamaño de un tema nuevo; las tandas 1 y 2 juntas ya dejan ver las dos caras en toda la app.

## 3. El cuento de David

### 3.1 Qué vigila hoy la ausencia de drogas

- `app/src/test/.../vidas/AnioG9Prueba.kt`:
  - `JERGA_DE_DROGAS` (línea 778): una lista de expresiones que no pueden salir en ningún texto. Mezcla tres cosas distintas: nombres de sustancias («marihuana», «lsd», «hongos»…), jerga («colocado», «viaje», «flipar»…) y palabras del seguimiento privado («racha», «recaída», «impulso», «clínica»), más el alcohol y el tabaco.
  - `nadaDeDrogasEnSuVidaNiEnSuCalendario()` (línea 644): pasa esa lista por la vida, las temporadas y los días; exige que no estén la clínica gratuita (7 jun 1967) ni la primera tienda de Haight (3 ene 1966); fija los títulos del entierro del hippie y del encuentro del parque.
- `app/src/test/.../vidas/CuentoG9Prueba.kt`, `nadaDeDrogasEnElCuento()` (línea 664): la misma lista sobre todas las piezas de `PiezasDavid` y sobre los cuentos armados de 1967 (2029) y de junio de 1969 (2029-06, en el calendario de la app).
- Los comentarios que dicen la regla: `vidas/piezas/PiezasDavid.kt` (cabecera), `vidas/anios/AnioG9.kt` (líneas 41 y 1141), `data/OtrasVidas.kt` (línea 985), `fuentes_psicodelia.md` (secciones 9 y la nota «no se investigaron»).

### 3.2 Cómo deben cambiar

Partir `JERGA_DE_DROGAS` en tres listas, con nombres que digan lo que vigilan:

| Lista nueva | Qué tiene | Dónde se prohíbe |
|---|---|---|
| `LO_PRIVADO` | racha, recaída, impulso, abstinencia, sobriedad, «mal viaje», «bajón» | En todo: cuento, calendario, vida **y voz del tema**. Así el cuento nunca dialoga con el seguimiento privado |
| `JERGA_QUE_CELEBRA` | colocado, flipar, subidón, viajazo, pedo, «puesto», «high», «stoned» | En todo, salvo en un diálogo entre comillas de un personaje (la prueba lo permite solo dentro de «…» y en una lista cerrada de piezas) |
| `NOMBRES_DE_SUSTANCIAS` | marihuana, LSD, ácido, hongos, peyote, mescalina, fumar, alcohol, vino, cerveza, tabaco… | **Ya no se prohíben en el cuento**; sí en la voz del tema (la guía: «el tema muestra, no comenta») |

Y pruebas nuevas, en lugar de la de ausencia:
- `elConsumoSaleSoloLasNochesQueToca`: los nombres de sustancias salen solo en los días con clave `humo` o `noche_tinta` (3.3) y en las variantes marcadas para esas noches; nunca en Navidad, Acción de Gracias, el día de la visita de su madre, ni en los días de Monterey (decisión de cuidado: son los días de familia o de trabajo).
- `cuantasVecesPorTemporada`: de 3 a 6 noches por temporada (ver 3.3), y nunca dos en la misma semana.
- `cadaNocheLlevaSuClave`: todo día de consumo lleva su clave de escena, y toda clave de consumo es de un día de consumo.
- `laVozDelTemaNoHablaDelConsumo` (nueva, fuera de G9): recorre los textos de interfaz de `TemaId.PSICODELIA` (`NombresPsicodelia`, `Estilo` con el tema puesto, `Avisos.Voz`) y exige que no salgan ni `LO_PRIVADO`, ni `JERGA_QUE_CELEBRA`, ni `NOMBRES_DE_SUSTANCIAS`.
- Se mantienen `MARCAS_DE_PSICODELIA`, las casas grises y las frases comprobadas.
- La de los días quitados se afloja: la clínica gratuita (7 jun 1967) y la primera tienda de Haight (3 ene 1966) **pueden** entrar ahora, con su fuente; el lema del Be-In sigue fuera (es un lema, y tiene derechos **[Aproximación]**).
- `unosVeinteDiasPorTemporada…` exige hoy de 18 a 23 días especiales por temporada: con las noches nuevas hay que pasarlo a **de 21 a 28**, o quitar algún día menor para hacer sitio.

### 3.3 Dónde y cómo entra el consumo en la historia (propuesta; no se escribe todavía)

**Lo que se sabe de la época** (abierto en la sesión del 16 nov 2026; ver 3.5):
- El LSD era **legal en California hasta el 6 de octubre de 1966**; ese día, en el Panhandle, se hizo el Love Pageant Rally contra la ley nueva, con miles de personas. Comprobado.
- El Be-In del 14 ene 1967 fue una protesta contra esa ley, y ese día se repartió LSD. Comprobado (ya estaba en la sección 9 de las fuentes, como algo a quitar).
- «Along with LSD, cannabis started to be much used during this period» (*Summer of Love*). Comprobado.
- La primera tienda de Haight (3 ene 1966) vendía artículos para marihuana y LSD. Comprobado.
- Los hongos de psilocibina se conocieron en EE. UU. por el artículo de los Wasson en *Life* del **13 de mayo de 1957**. Comprobado. **Qué tan fáciles de conseguir eran en San Francisco entre 1965 y 1969, [sin verificar]**: la página habla de una «explosión» de su uso, sin fecha; el cultivo casero se popularizó más tarde **[sin verificar]**. Por eso son la sustancia más rara de la historia.
- La marihuana estaba prohibida en EE. UU. desde la ley federal de 1937 (*Marihuana Tax Act*). Comprobado (página de la historia legal del cannabis; el detalle de las penas de California en 1967, **[sin verificar]**).

**Qué y cuántas veces** (de 3 a 6 noches por temporada; nunca más de una por mes salvo en el verano de 1967):

| Temporada | Marihuana | LSD | Hongos | Notas |
|---|---|---|---|---|
| 1 · 1965 | 1 (otoño, en la cocina del departamento, la ofrece alguien de paso) | 0 | 0 | Recién llegado: mira más de lo que hace |
| 2 · 1966 | 2 | 1 (verano, antes del 6 de octubre, cuando aún es legal) | 0 | El 6 de octubre de 1966 entra como día histórico (el Panhandle, la ley nueva) |
| 3 · 1967 | 3 | 2 (una en la sala de la calle Sutter, con las luces de Ellen; otra tranquila, en Buena Vista Park, mirando entrar la niebla) | 0 | El Be-In puede decir ya, con su fuente, que se repartía LSD; la clínica gratuita (7 jun) como día de la ciudad |
| 4 · 1968 | 2 | 1 | 1 (otoño; alguien los trae de fuera; [sin verificar] que fueran comunes: se cuenta como cosa rara) | Una noche mala de un amigo, que pasa sin drama: lo cuidan, al día siguiente está cansado |
| 5 · 1969 | 2 | 0 | 0 | Ya no le interesa el LSD; lo dice de pasada, sin moraleja |

**Cómo se cuenta:**
- **Con sobriedad:** con la misma voz que el resto del cuento (sensorial, concreta, de barrio). Lo que cambia es lo que ve y oye: la luz de la pared, el tiempo que se estira, el dibujo que de pronto entiende. Nunca más espectacular que el resto de la noche.
- **Sin glorificar:** no le da ideas geniales ni «abre su mente»; a veces dibuja peor al día siguiente; una noche no pasa nada; el cansancio existe.
- **Sin sermonear:** nadie muere ni se arruina por una noche; no hay narrador que advierta. Lo que pasa, pasa (la noche mala del amigo), y se cuenta como lo demás.
- **Nadie real consume en el cuento:** la gente real sigue saliendo de lejos y solo con lo que dicen las fuentes (que se repartió LSD en el Be-In, sí; quién lo tomó, no).
- **Ni el alcohol ni el tabaco como premio:** si vuelven (pregunta 5 de la guía), como cosa de la época: los demás fuman, en Acción de Gracias hay vino.

**En qué días:** como **días especiales** (`suceso`) en `AnioG9.kt`, con su título y su clave, en un **jueves, viernes o sábado** (los días de baile, corrección 17) y lejos de los días ya puestos. Para el resto de la temporada, unas **6 a 10 variantes** de la noche en `PiezasDavid.kt` (párrafo de la gente o del anochecer) que mencionan el humo dulce de la sala o un cigarrillo de marihuana que pasa de mano en mano **sin que David lo tome**, para que el ambiente de la época esté sin que el consumo sea de cada día.

### 3.4 La clave de escena para el cuadro

Las escenas leen el día de su personaje con `Momento` (`ui/escenas/EscenaVida.kt`): `m.con("clave")` es verdad si alguno de los días especiales de hoy lleva esa clave en su `escena` (`Momento.claves = ficha.especiales.map { it.dia.escena }`), y `m.hacer` dice qué hace el personaje a esa hora (`Hacer.GENTE` de 20 a 2 en la agenda de David). El calendario de David ya usa claves (`carteles`, `rotulo`, `radio`, `parque`, `mudanza`, `luz_negra`, `pintura`, `cerrado`, `fiesta`; comentario de la línea 1196).

**Dos claves nuevas:**

| Clave | Días | Qué hace el cuadro (cuando exista) |
|---|---|---|
| `humo` | las noches de marihuana | De 20 a 4 h, la sala o el cuarto con una bruma baja y tibia; las luces líquidas se mueven un poco más. Cambio leve |
| `noche_tinta` | las noches de LSD y de hongos | **De 20 a 4 h, el cuadro entero pasa a la manera de la cara de noche del tema con i = 1**: la escena se imprime en tres tintas planas, las bandas fluyen desde las luces de la pared, el registro se corre. Es lo que pidió el autor («la escena cambia a una muy psicodélica»). Fuera de esas horas, el cuadro es el de siempre |

- Se escriben en el calendario ya ahora (como `escena = "noche_tinta"` en el `suceso`), aunque el cuadro no exista, para que la tanda que lo dibuje no tenga que tocar la historia. El comentario de «Lo que el cuadro tiene que mostrar» de `AnioG9.kt` las suma a su lista.
- Son de **David**, no de la persona: el cuadro no lee la intensidad del tema (1.3). Si la persona tiene un consumo en curso y David no, el cuadro no cambia.
- La prueba `cadaNocheLlevaSuClave` (3.2) garantiza que el cuento y el cuadro cuenten lo mismo (decisión 18 de `plan_historia_y_escenas.md`).

### 3.5 Fuentes de esta sección (abiertas el 16 nov 2026, en el wikitexto de cada página)

| Dato | Cita | Página | Veredicto |
|---|---|---|---|
| El LSD, ilegal en California desde el 6 oct 1966; el Love Pageant Rally en el Panhandle ese día | «took place on October 6, 1966—the day LSD became illegal—in the 'panhandle' of Golden Gate Park» | [Love Pageant Rally](https://en.wikipedia.org/wiki/Love_Pageant_Rally) | Comprobado (la página cita sobre todo a un sitio de 2008 y el libro de Perry) |
| El motivo del Be-In | «a new California law banning the use of the psychedelic drug LSD that had come into effect on October 6, 1966» | [Human Be-In](https://en.wikipedia.org/wiki/Human_Be-In) | Comprobado (dos páginas dan la misma fecha) |
| El cannabis en el verano del amor | «Along with LSD, cannabis started to be much used during this period» | [Summer of Love](https://en.wikipedia.org/wiki/Summer_of_Love) | Comprobado |
| La clínica gratuita | «Haight Ashbury Free Clinics was founded on June 7, 1967» | [Summer of Love](https://en.wikipedia.org/wiki/Summer_of_Love) | Comprobado (ya en `fuentes`, sección 2) |
| La primera tienda de Haight | «opened on Haight Street on January 3, 1966, offering hippies a spot to purchase marijuana and LSD» | [Haight-Ashbury](https://en.wikipedia.org/wiki/Haight-Ashbury) | Comprobado |
| Los hongos en EE. UU. | «publishing an article on their experiences in Life on May 13, 1957» | [Psilocybin mushroom](https://en.wikipedia.org/wiki/Psilocybin_mushroom) | Comprobado. Su uso en San Francisco en 1965–1969: **[sin verificar]** |
| La prohibición federal de 1937 | «the Marihuana Tax Act of 1937» | [Legal history of cannabis in the United States](https://en.wikipedia.org/wiki/Legal_history_of_cannabis_in_the_United_States) | Comprobado. Las penas de California en 1967: **[sin verificar]** |

Cuando se escriba el cuento, estas filas pasan a `fuentes_psicodelia.md` como sección nueva, y la sección 9 se reescribe: ya no es «lo que hay que quitar», sino «lo que se usa con fuente y lo que sigue fuera» (el lema del Be-In; la frase del exceso de Blake).

## 4. ¿Sirve para otros temas?

Sí, con cuidado. La idea general es **una base sobria y un estado que se enciende con algo que la app ya sabe**. Funciona mejor cuando el disparador es del mundo (la hora, el tiempo, una fecha) o de algo que la persona hace a propósito (un bloque de enfoque); funciona peor, y puede hacer daño, cuando es un estado interno.

| Tema | Base | Estado intenso | Disparador que la app ya conoce |
|---|---|---|---|
| Gótico | la piedra gris de día | los vitrales encendidos por los relámpagos | el tiempo del día que la app ya baja (`EntornoDia`: `precip_mm` y `codigo`, en `Repo.entorno`) |
| Pirata | el puerto en calma | la cubierta en temporal, el horizonte inclinado | el viento y el tiempo del día (`EntornoDia.viento_max` y `codigo`) |
| Horror cósmico | el archivo del pueblo | la noche de luna llena: las manchas de tinta se mueven | la fase de la luna, que el tema ya calcula para su fecha |
| Laboratorio | la mesa ordenada | «experimento en marcha»: luz roja de grabación, todo lo demás se apaga | un bloque de enfoque en curso (`EnfoqueActivity`) |
| Espacio | la nave de crucero | el salto: la ventana se estira | un bloque de enfoque en curso (calma la interfaz en vez de agitarla) |
| Navidad | el pueblo nevado de diciembre | la nochebuena encendida | la fecha (24 y 25 de diciembre) |
| Romano | el foro de diario | el triunfo, con laureles | la racha de días sellados (la del sello del día; nunca la del seguimiento privado) |
| Mongol | la estepa | el festival de verano | la fecha (julio) |

**Lo que no conviene:**
- **Reaccionar al ánimo registrado, al sueño o a las respuestas de los cuestionarios.** Si el aspecto de la app cambia con lo que se anota, lo que se anota después puede cambiar (reactividad de la medida: la persona responde al tema y no a sí misma). Esta app existe para medir; la apariencia nunca debe volver a la medida.
- **Reaccionar al seguimiento privado** (impulsos, recaídas, libido, medicamentos), en ningún sentido: ni premiar una racha ni castigar una recaída.
- **Premiar un consumo.** Psicodelia es la excepción que pidió el autor, y por eso lleva sus frenos (sin celebración, sin texto, sin sonido, con tope en la ciudad, en cero en las pantallas privadas). Aun así, un aspecto que se enciende al registrar un consumo puede funcionar como recompensa por registrarlo (o por consumir): es un riesgo de medida que conviene anotar en el paper como hallazgo cuando le toque al módulo de sustancias (los módulos de contenido solo anotan; aquí no se toca `hallazgos.md`).
- **Cambiar lo que se guarda.** Ningún estado de tema se guarda ni se exporta.
- **Sondeos.** Cada disparador tiene que llegar como evento o como espera hasta el próximo cambio, como `RelojModo`.

## 5. Lo que no se pudo comprobar en esta sesión

- La facilidad de conseguir hongos de psilocibina en San Francisco entre 1965 y 1969, y las penas de California por marihuana en 1967.
- Cómo se ve Chango recortada en tamaño chico dentro de la app (el 8 y la ñ en 12 sp): solo se vio en PIL.
- El coste real de leer `EstadoTema.intensidad` en los dibujos durante la animación de 4 s (se propone; se mide en el teléfono).
- Que el reloj de cara con cualquier modo no choque con el filtro nocturno (`Config.nocturno`, de 22 a 5 h), que es otro ajuste.
