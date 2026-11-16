# Psicodelia v2: la base (tanda 1)

Lo que dejó la tanda 1 para las tandas siguientes: qué nombres existen, cómo se leen la cara y la intensidad, cómo se guarda en caché y qué queda de la versión 1 por retirar. La guía es [`psicodelia.md`](psicodelia.md) (manda su última sección, «Decisiones del autor después de ver la lámina»); el plan, [`psicodelia_plan.md`](psicodelia_plan.md). Las rutas de código son de `app/src/main/java/io/github/ruddy2003/vestigium/`.

## Lo que cambió respecto del plan

- **La cara** pasa a la psicodelia de noche (21:00 a 5:00) **o con un consumo en curso** (`intensidad > 0`), también de día. La claridad sigue siendo la del modo de la app: de día, con el modo «hora» o «claro», un consumo da la psicodelia en claro; con «oscuro», en oscuro.
- **La intensidad** cuenta cualquier sustancia con nivel (alcohol, cannabis, «otra»), no lee `sustancias_ocultas` y va por tramos: nivel 0 → 0, nivel 3 → 0.5, nivel 8 o más → 1. Un consumo abierto sin nivel cuenta como 3. Varios a la vez: el mayor.
- **Las cuatro paletas** viven en `ui/Disenios.kt` (donde estaban las dos de la versión 1), no en `ui/Tema.kt`; `Tema.kt` solo despacha.
- **El máximo, un poco más fuerte** que en la lámina: va en `TrazosTinta.medidas` (abajo).

## Nombres que ya existen

### `ui/CaraTema.kt`

| Nombre | Qué es |
|---|---|
| `enum class Cara { BASE, INTENSA }` | BASE = la ciudad; INTENSA = la psicodelia. |
| `TemaId.conCaras`, `TemaId.conIntensidad` | En el constructor de `TemaId` (`ui/Tema.kt`), `false` por omisión; solo `PSICODELIA` las pone en `true`. |
| `caraSegun(tema, noche, intensidad): Cara` | Puro. INTENSA si el tema tiene caras y (`noche` o `intensidad > 0`). |
| `Intensidad.deNivel(nivel: Double): Float` | La escala por tramos. `NIVEL_SIN_DATO = 3.0`, `NIVEL_MEDIO = 3.0`, `NIVEL_PLENO = 8.0`. |
| `Intensidad.de(consumos, ahora): Float` | El mayor de los consumos en curso (`enCurso`: sustancia con nivel y `inicio ≤ ahora ≤ Sustancias.finEfectivo`), con `Sustancias.nivelEn(c, ahora, ahora) ?: 3`. |
| `Intensidad.enCurso(c, ahora)`, `Intensidad.hayAbierto(consumos, ahora)` | Ayudantes (el medidor solo vuelve a mirar cada 2 min si `hayAbierto`). |
| `EstadoTema.cara` / `EstadoTema.caraDe(tema)` | La cara del tema en uso / de un tema. Forzada si la hay; si no, `caraSegun(tema, RelojModo.noche, intensidadObjetivo)`. |
| `EstadoTema.intensidad` | La animada (la que se dibuja), de 0 a 1. |
| `EstadoTema.intensidadObjetivo` | A la que va la animación; con esta se decide la cara (cambia al instante; los colores la siguen en 4 s). |
| `EstadoTema.caraSinIntensidad(tema)` | La cara de la hora, como si no hubiera consumo. |
| `EstadoTema.forzar(cara, intensidad)` | Para capturas y arnés; `null` vuelve a la de verdad. Solo afecta a los temas con caras. |
| `EstadoTema.escalon(i)` | `round(i · 8) / 8`: nueve escalones, para la clave de caché. |
| `@Composable MedidorIntensidad()` | Va en `TemaVestigium` si `tema.conIntensidad`. Recalcula al cambiar `Repo.consumos`, en `ON_RESUME` y cada 2 min solo con un consumo abierto; anima 4 s al subir o cambiar, 10 s al caer a 0. |
| `@Composable SinIntensidad { … }`, `LocalSinIntensidad` | Para las pantallas del seguimiento privado y de Sustancias: dentro, `intensidadLocal()` da 0 y `caraLocal()` la cara de la hora; si esa cara no es la de la app, el contenido va con el esquema, la letra y las formas de esa cara. **Todavía no lo usa ninguna pantalla** (lo ponen las tandas que tocan esas pantallas). |
| `@Composable caraLocal()`, `@Composable intensidadLocal()` | Lo que deben leer los componentes de Psicodelia desde un `@Composable` (respetan `SinIntensidad` y `LocalCaraFija`). |
| `LocalCaraFija` | (Tanda 5.) Una cara fija para un trozo de interfaz que no sigue ni la hora ni los consumos: los fondos del teléfono. Dentro, `caraLocal()` da esa cara e `intensidadLocal()` da 0. Null por omisión. |

En `TemaVestigium` (`ui/Tema.kt`) el reloj de la hora (`RelojDelModo`) corre ahora también si `tema.conCaras`, con cualquier modo; el medidor, solo si `tema.conIntensidad`. Los dos se saltan en modo de edición (Paparazzi).

### `ui/Tema.kt` y `ui/Disenios.kt`

- `esquemaDe(tema, oscuro)`, `tipoDe(tema, oscuro)` y `radioDe(tema)` siguen con su firma y eligen por `EstadoTema.caraDe(tema)`. Hay versiones con la cara explícita: `esquemaDe(tema, oscuro, cara)`, `tipoDe(tema, oscuro, cara)`, `radioDe(tema, cara)` y `formasDe(tema, cara)`. Los `when (tema)` siguen exhaustivos; solo la rama `PSICODELIA` mira la cara.
- Esquemas: `psicodeliaCiudadClaro`, `psicodeliaCiudadOscuro`, `psicodeliaTintaClaro`, `psicodeliaTintaOscuro`, y `psicodeliaEsquema(oscuro, cara)`. Los colores sueltos, en `PaletasPsicodelia.de(oscuro, cara)` (`PaletaPsicodelia`: fondo, superficie, variante, borde, tinta, tenue, primario, sobrePrimario, contenedor, sobreContenedor, secundario, contSecundario, terciario, error). Son los de la tabla «Las cuatro paletas» de la guía. `onSecondary`, `onTertiary` y `onError` son el fondo (oscuro) o la superficie (claro).
- Contraste (calculado con la fórmula WCAG, también con los roles calientes de i = 0.5 y 1): tinta ≥ 11.9 sobre el fondo y ≥ 10.4 sobre la variante; tenue, primario, secundario, terciario y error ≥ 5.0 sobre el fondo; cada «sobre» ≥ 5.1 sobre su color.
- Letra: `tipoPsicodelia(cara)` = `tipoPsicodeliaCiudad` (títulos `LetraExtra.franklinTitular`, Libre Franklin 800, espaciado −0.9 sp) o `tipoPsicodeliaTinta` (títulos `LetraExtra.bloque`, «Bloque Vestigium» = Chango renombrada, escala 0.86). Texto en `LetraExtra.franklin` (400; 600 en Medium y SemiBold; 800 en Bold y ExtraBold) y rótulos y cifras en `LetraExtra.franklinRotulo` (600), en las dos caras. Recursos: `res/font/franklin_regular`, `franklin_semibold`, `franklin_extrabold`, `bloque_titulo` (de `herramientas/fuentes.py`; licencias en `assets/licencias/fuentes_OFL.txt`). El 8 de Chango se revisó a 12, 14 y 18 sp: no se confunde con la S.
- Esquinas: ciudad `Esquinas(11, REDONDA)`; psicodelia `Esquinas(13, ARCO)`. El reloj grande (`letraReloj`): 62 sp y −0.4 en la ciudad, 50 sp y 0.6 en la psicodelia.
- Números (`Psicodelia.numero`, `Estilo.numeral`): «(17)»; los años y lo que pasa de 999, tal cual. La fecha no cambió.

### `ui/PsicodeliaTrazos.kt`

**`TrazosCiudad`** (todos apagados): `BRUMA #D9CDB6`, `LUZ_BAJA #EDBE8C`, `SOL_VELADO #F6DDB4`; `CASA_GRIS #A0968A`, `CASA_GRIS_OSCURA #958B7E`, `CASA_GRIS_CLARA #A89C8C`, `CASA_PINTADA #7E958E` (y `CASAS`, en ese orden); `AL_SOL #F4E6CC`, `SOMBRA #4A3528`; `POSTE #3E3128`; `PASAMANOS #8C8270`, `PASAMANOS_BRILLO #D8CDB5`; `COCHE #7A3424` (también el ladrillo); `PARED_NOCHE #1F1712`, `LAMPARA #F2C996`, `LAMPARA_LUZ #F0A35A`, `MADERA #6E4630`, `ETIQUETA #C8662A`; `LETRERO #A01E2C`; `VINETA #3A2410`. Constantes: `GRANO_DIA 0.22`, `GRANO_NOCHE 0.30`, `VINETA_DIA 0.28`, `VINETA_NOCHE 0.35`.

Funciones: `alSol(c, k = 0.35)`, `enSombra(c, k = 0.42)`, `pelicula(c)` (cualquier color pasado por la película vieja, **[Diseño propio]**). Trazos (`DrawScope`): `grano(fuerza, semilla, celda, zona)` (caro: en caché), `vineta(fuerza, color)`, `cables(desde, hasta, comba, separacion, grosor)`, `poste(base, alto, grueso)`, `ladrillos(zona, hilada, color, junta, semilla)`, `persiana(zona, lamas, color, abierta, detras)`, `placaLetrero(zona, fondo, filete, radio)` (el texto lo pone quien llama).

**`TrazosTinta`**: `PAPEL #F2E8D3`, `NEGRO #0F0C17`, `GRABAR #1E1A3A`, `CREMA #F3E9D6`; `papel(oscuro)`, `grabado(oscuro)`.

| `tinta(Tinta, oscuro, i)` | papel i = 0 / 0.5 / 1 | negro i = 0 / 0.5 / 1 |
|---|---|---|
| `MAGENTA` | `#A94870` / `#B73570` / `#C50E70` | `#DC7BA1` / `#EC6DA2` / `#FB5BA2` |
| `BERMELLON` | `#C56851` / `#D55A3C` / `#E5481E` | `#E0907B` / `#EF876B` / `#FD7C5A` |
| `ULTRAMAR` | `#3F4C9E` / `#3A44B3` / `#3739C9` | `#8693D7` / `#8190EA` / `#7C8CFC` |

| `rol(Rol, oscuro, i)` | claro i = 0 / 0.5 / 1 | oscuro i = 0 / 0.5 / 1 |
|---|---|---|
| `PRIMARIO` | `#974164` / `#A43164` / `#B11265` | `#DD80A4` / `#EC73A5` / `#FA63A5` |
| `SECUNDARIO` | `#404D99` / `#3B47AD` / `#383EC1` | `#94A1DB` / `#909EEB` / `#8C9CFB` |
| `TERCIARIO` | `#6A3E8E` / `#6F3399` / `#7428A6` | `#B79BE0` / `#BE95F0` / `#C58CFF` |

Entre medias se interpola en dos tramos (Oklab, el `lerp` de Compose). Con i = 0 los roles son los del esquema.

`sobreimpresion(a, b, oscuro)`: multiplica sobre papel; sobre negro, mezcla a partes iguales (**[Aproximación]**).

`medidas(i): Medidas` (dp y por segundo; con i = 0 / 0.5 / 1): `bandasPorLado` 7 / 9 / 11, `amplitud` 6 / 17 / 32, `grosor` 3 / 5 / 6.5, `registro` 0 / 2 / 5, `anillos` 0 / 5 / 8, `bermellon` (opacidad de la tercera tinta) 0 hasta i = 0.25 y 1 desde i = 0.5, `fueraDelMarco` desde i = 0.9, `ondaTitulo` 0 / 2 / 6, `registroTitulo` 0 / 1.5 / 3.5, `respiro` 2, `bandasPorSegundo` 0 / 1/20 / 1/7, `vueltasPorSegundo` 1/180 / 1/120 / 1/50. El máximo es un poco más fuerte que el de la lámina (decisión 2).

Trazos: `corrimiento(tinta, registro)` y `DrawScope.conRegistro(tinta, registro) { … }` (magenta a la derecha y arriba, bermellón a la izquierda y abajo, ultramar quieto); `DrawScope.bandas(centro, alto, n, separacion, amplitud, grosor, color, frecuencia, fase, abre)`; `rayos(centro, r0, r1, n, amplitud, ondas, fase, lleno): Path`; `DrawScope.trama(forma, paso, grosor, color, angulo)` (el grabado fino); `arco(caja, radio, poco): Path` (el marco del cartel); las letras que llenan su forma: `letras(medidor, texto, estilo, espacio): Letras` (medir una vez, en caché) y `DrawScope.llenarForma(letras, caja, color, forma, onda, frecuencia, fase)` con `FormaLetras.RECTA`, `BARRIL`, `ARCO` o `CUENCO`. Solo para títulos decorativos: el texto y las cifras de datos no se deforman.

Una hoja de muestra de todo esto: `herramientas/arnes_muebles/dibujar.sh trazos psicodelia <claro|oscuro> salida.png` con `ARNES_CARA` y `ARNES_INTENSIDAD` (`arnes/MuestraTrazos.kt`).

### Los sonidos (tanda 7)

- **Dos juegos, uno por cara**, en lo que suena dentro de la app. La ciudad (BASE) se queda con los nombres de siempre (`s_psicodelia_<evento>`, `s_tecla_psicodelia`, `s_psicodelia_presentacion`, `s_psicodelia_anual`): órgano de transistores suave, pandero, eco de cinta corto, la aguja y la perilla de la radio. La psicodelia (INTENSA) va en `s_psicodelia_tinta_<evento>`, `s_tecla_psicodelia_tinta`, `s_psicodelia_tinta_presentacion` y `s_psicodelia_tinta_anual`: sitar en zumbido, guitarra con distorsión en el logro, eco de cinta largo, el rasero y el bastidor de la serigrafía, el pincel de rotulista. El rascado del mapa es uno solo (`s_rascar_psicodelia`, la misma moneda).
- **El sonido sigue la cara de la hora, nunca la intensidad:** `Sentidos.caraDelSonido(tema)` = `EstadoTema.caraSinIntensidad(tema)`. Con un consumo de día, la pantalla pasa a la psicodelia pero se sigue oyendo la ciudad (un consumo no debe oírse). La cara forzada (capturas, arnés) sí manda.
- En código: `Sentidos.SONIDOS` (BASE, como antes), `Sentidos.SONIDOS_INTENSA` (solo los temas con caras), `Sentidos.sonidosDe(tema, cara)`; `Musica.presentacion(t, cara)` y `Musica.anual(t, cara)`, con la cara de la hora por omisión. Pruebas: `SonidosCaraPrueba` y la superficie `sonidos_intensa` de `CoberturaTemas`.
- **No cambian con la cara:** los avisos (`n_psicodelia_aviso`, `n_psicodelia_solemne`, que ahora son de la ciudad: perilla, órgano suave y pandero, sin sitar) y los tonos del teléfono (`t_psicodelia_llamada`, `t_psicodelia_notificacion`, los mismos de antes: el guion los reproduce idénticos).
- Generadores: `herramientas/afinados.py` (`psicodelia` y `psicodelia_tinta`) y `herramientas/musica.py` (`hacer_psicodelia_ciudad` y `hacer_psicodelia`).

## Cómo leer la cara y la intensidad

**Desde un `@Composable`** (componentes, pantallas): `val cara = caraLocal()` y `val i = intensidadLocal()`; los colores calientes, `TrazosTinta.rol(Rol.PRIMARIO, oscuro, i)`. Así respetan `SinIntensidad`.

**Desde un `DrawScope`** (las interfaces de dibujo `DisenoIdeas`, `DisenoBiblioteca`, `DisenoVida`, `DisenoComun`, `DisenoLogros` no son `@Composable`): leer `EstadoTema.cara` y `EstadoTema.intensidad` directamente. Es estado de instantánea: Compose vuelve a dibujar solo ese dibujo cuando cambia. Ojo: un dibujo que lee `EstadoTema` directamente **no** se entera de `SinIntensidad`; si un dibujo puede salir en una pantalla privada o de Sustancias, que reciba la cara y la intensidad desde su `@Composable` (`caraLocal()`, `intensidadLocal()`).

```kotlin
// En un mueble (DrawScope), con lo caro en caché y lo que se mueve encima:
val cara = EstadoTema.cara
val i = EstadoTema.intensidad
when (cara) {
    Cara.BASE -> pintarCiudad(oscuro)          // TrazosCiudad
    Cara.INTENSA -> pintarTinta(oscuro, i)     // TrazosTinta, con TrazosTinta.medidas(i)
}
```

**Lo que se guarda en caché:** la clave es `remember(oscuro, cara, EstadoTema.escalon(i)) { … imagen … }` (nueve escalones de intensidad); lo que se mueve (la respiración, el flujo de las bandas, el giro del disco) se dibuja encima en cada cuadro. Con las animaciones apagadas (`config.animaciones`) o ahorro de energía, nada se mueve, pero los colores, el registro, los anillos y la onda de los títulos sí cambian.

**Fuera de Compose** (avisos, widgets, arranque, reportes): `esquemaDe(tema, oscuro)` lee `EstadoTema`, que fuera de la app puede estar viejo (`RelojModo` solo se pone al día con la app abierta). Para elegir por hora ahí, `caraSegun(tema, ModoHora.esDeNoche(LocalTime.now()), 0f)` y `esquemaDe(tema, oscuro, cara)` (plan, 1.3).

**Pruebas y capturas:** `EstadoTema.forzar(Cara.X, i)` y luego `forzar(null, null)`. `UnicidadTemas` ya compara la letra y las esquinas de las dos caras de Psicodelia con la versión de `tipoDe`/`radioDe` con cara explícita. **Las capturas de Paparazzi (`Capturas.kt`) todavía no fuerzan nada**: Psicodelia sale con la cara de la hora a la que corra CI. La tanda que haga sus capturas debe forzar las cuatro variantes más i = 0.5 y 1.

## El ícono, el sello, el papel, los widgets y los fondos (tanda 5)

- **Ícono** (`herramientas/iconos.py`, `logo_psicodelia.webp`, `logo_fondo_psicodelia.jpg`): fijo, de la ciudad. La etiqueta del disco del día, naranja quemado, puesta en su vinilo (con la zona lisa de salida, los surcos y el brillo de la luz de la izquierda), la huella impresa en el café de la ciudad y, en el anillo, el filete de la etiqueta con su leyenda («DISCO DEL DÍA», «LADO A · LARGA DURACIÓN»). El alias no cambia con la hora.
- **Sello** (`herramientas/sellos.py`, `huella_psicodelia.webp`, `huella_fondo_psicodelia.jpg`): fijo, de la ciudad. La misma etiqueta de cerca, como foto de los 70: el papel satinado con el roce del plato, la banda crema con la leyenda menuda alrededor, «DISCO DEL DÍA», «LADO A» y «33⅓ R.P.M.», el vinilo alrededor, luz de la izquierda, viñeta y grano.
- **Papel** (`herramientas/arte.py`): `papel_psicodelia.jpg` es el de la ciudad (la copia de foto mate perla, con su margen); `papel_psicodelia_cartel.jpg`, el del cartel (papel crema con los rayos y el marco de arco del otro lado, apenas visibles). `pagina(TemaId.PSICODELIA)` (`ui/Libros.kt`) elige con `EstadoTema.caraDe(t)`. La página de leer es siempre clara (`oscura = false`), así que el negro de terciopelo no tiene papel propio; la de los sueños (`paginaNoche`) lo tapa con su cielo.
- **Widgets** (`widget/Widgets.kt`): siempre oscuros; la cara la elige la hora cada vez que se pintan (`caraSegun(TemaId.PSICODELIA, ModoHora.esDeNoche(LocalTime.now()), 0f)`; la tarea de 15 minutos los vuelve a pintar), sin intensidad. Recursos: `widget_fondo_psicodelia`, `widget_boton_psicodelia`, `widget_boton_activo_psicodelia`, `widget_boton_ambar_psicodelia` (la ciudad: el cuarto con la lámpara) y los mismos con `_tinta` al final (el cartel de luz negra); los dos fondos también en `drawable-v31`. La letra de las cifras dibujadas, Libre Franklin SemiBold. `CoberturaTemas` exige los ocho (más los dos de v31) en todo tema con caras, y `papel_<id>_cartel`.
- **Fondos del teléfono** (`ui/FondosTelefono.kt`): la pantalla ofrece «la ciudad» y «el cartel»; la cara elegida se guarda en las preferencias de los fondos (`FondosTelefono.cara` / `elegirCara`; no es un dato) y no cambia sola con la hora ni con un consumo. `FondoTelefonoVista(…, cara)` pinta con `esquemaDe/tipoDe(tema, oscuro, cara)` y pone `LocalCaraFija`; el marco tiene dos pintores (`marcoPsicodeliaCiudad`: la ventanilla del trolebús, la calle de victorianas, los postes y los cables; `marcoPsicodeliaCartel`: el marco de arco, los rayos con trama, los anillos y las marcas de registro). Los archivos de un tema con caras llevan la cara en el nombre. Lo que lee `EstadoTema` directamente (el fondo vivo, el emblema, si su tanda no los pasa a `caraLocal()`) sigue la cara de la app, no la elegida. Otra parte del plan rehará los fondos con las escenas.

## La voz (tanda 8)

- **Dos voces, por cara:** el comentario de cada pantalla («Lado A · pista 4 · hoy» en la ciudad, «Lado B · pista 4 · hoy» en el cartel; `Estilo.comentarioNuevo`), «atrás» («← de vuelta a la calle» / «← de vuelta a la sala»), la vista previa del selector y los avisos (`Avisos.vozPsicodelia(cara)` y `Avisos.vozEnfoquePsicodelia(cara)`, elegidos con `caraSegun(tema, ModoHora.esDeNoche(LocalTime.now()), 0f)`: un aviso nunca cambia por un consumo). Dentro de la app, `Estilo` lee `EstadoTema.cara`. Los títulos de pantalla, las pestañas y los widgets son los mismos en las dos caras.
- **Signos:** comentario «◉ » (el disco del día), prefijo «‣ », la fecha grande abre con «◉».
- **Lo privado y lo de Sustancias van con la voz de la app:** `NombresPsicodelia.NEUTROS` (sin nombre ni comentario del tema; esos títulos ya no están en ningún diccionario de Psicodelia).
- **La prueba:** `app/src/test/.../VozPsicodeliaPrueba.kt` (`laVozDelTemaNoHablaDelConsumo` y tres más) define `LO_PRIVADO`, `JERGA_QUE_CELEBRA` y `NOMBRES_DE_SUSTANCIAS` en el paquete raíz de las pruebas y recorre los nombres, `Estilo` en las dos caras, `Textos`, los widgets, las claves, los avisos y las campañas. Fuera quedan las frases del día, las fechas del calendario y el cuento. `UnicidadTemas` compara los textos de `Estilo` también con la cara intensa fijada.

## Lo de la versión 1 que queda por retirar

| Qué | Dónde | Quién lo usa todavía |
|---|---|---|
| `TrazosPsicodelia` entero (la flor, la paz, el arcoíris, el verde limón, las burbujas, la casa victoriana de colores, el botón de solapa) | `ui/PsicodeliaV1Trazos.kt` (sin cambios; solo se movió y se marcó) | `comun/ComunPsicodelia.kt`, `ideario/IdeasPsicodelia.kt`, `biblioteca/BibliotecaPsicodelia.kt`, `vida/VidaPsicodelia.kt`, `logros/LogrosPsicodelia.kt`, `Decoracion.kt`, `Componentes.kt`, `ControlesTema.kt`, `IconosPestana.kt`, `IconosCinta.kt`, `Animaciones.kt`, `Celebracion.kt`, `Marcas.kt`, `Relacion.kt`, `Libros.kt`, `MapasMas.kt`, `TrofeosMas.kt`, `EstiloGrafica.kt`, `Musica.kt`, `MainActivity.kt`, `EnfoqueActivity.kt`, `pantallas/Hoy.kt`, `Mapa.kt`, `Trofeos.kt`, `Cinta.kt`, `OtrasVidas.kt`, `Calendario.kt`, `TuAnioVisual.kt`, `Hall.kt` (ya no `FondosTelefono.kt`: tanda 5) |
| `LetraExtra.modak`, `fredoka`, `fredokaRotulo` y los archivos `burbuja_titulo`, `fredoka_regular`, `fredoka_semibold` (con sus entradas en `herramientas/fuentes.py`, `CON_SIGNOS_NOTO` y la licencia) | `ui/Disenios.kt`, `res/font/` | `comun/ComunPsicodelia.kt`, `ideario/IdeasPsicodelia.kt`, `biblioteca/BibliotecaPsicodelia.kt`, `pantallas/Hoy.kt`, `Libros.kt`, `LibroDiario.kt`, `EstiloGrafica.kt`, `reportes/Reportes.kt` (ya no `widget/Widgets.kt`: tanda 5) |
| Los textos con «✿», «☮», «♪», «~» | Retirados por la tanda 8 de `Estilo.kt`, `Tema.kt`, `Textos.kt`, `Temas.kt`, `NombresNuevos.kt`, `nombres/NombresPsicodelia.kt`, `avisos/Avisos.kt` y `reportes/Reportes.kt` (la letra de las cifras) | Quedan en archivos de otras tandas: `Huella.kt` («Fin del lado B ☮»), `pantallas/HallImagen.kt` («✿ … ✿», «paz y amor»), `MainActivity.kt` (el bloqueo, «☮ Hoy hay baile…»), `Musica.kt` (comillas «✿»), `pantallas/Hoy.kt` (comillas de la frase «✿»), `Componentes.kt` («♪», «♫», «✿»), `Cartas.kt` («calcomanía»), el dibujo del reporte en `reportes/Reportes.kt` (la flor y la paz) |
| La vista previa del tema (la forma del cartel que se derrite y la flor) | `Tema.kt` (ramas `PSICODELIA` de la vista previa) | tanda 2 |

Cuando el último de una fila deje de usarlo, se borra (y, para las letras, se quitan sus salidas de `fuentes.py` y su línea de la licencia).
