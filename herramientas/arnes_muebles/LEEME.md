# Arnés de los muebles

Dibuja en PNG, en esta máquina y sin Android, los muebles de la pestaña Diario: el mueble del Ideario y el estante del diario. Usa los archivos reales de la app, enlazados desde `app/src/main` (no copiados), y los composables reales `MuebleIdeas` y `EstanteTema`, con los mismos datos de ejemplo que las capturas de Paparazzi (`Capturas.kt`). Sirve para redibujar un mueble y ver el resultado en segundos, sin esperar a CI.

## Uso

```
herramientas/arnes_muebles/dibujar.sh ideario <tema> <claro|oscuro> <salida.png>
herramientas/arnes_muebles/dibujar.sh biblioteca <tema> <claro|oscuro> <salida.png>
herramientas/arnes_muebles/dibujar.sh hoja <ideario|biblioteca> <claro|oscuro> <salida.png> <tema1,tema2,...>
```

- `<tema>` es el id de `TemaId` (`heleno`, `gotico`, `laboratorio`, `galia`…). Con un id que no existe, el script dice cuáles hay.
- `ideario`: lo mismo que la captura `temaMueble` «_b»: `FicheroIdeas` con 9 temas (`ideasConTemas(9)`), con 16 dp de margen.
- `biblioteca`: lo mismo que el estante de la captura `temaBibliotecas`: las 35 entradas de `Ejemplo.cargar()` repartidas por mes, con su rótulo (`Estilo.mesConComun`). Solo el estante, con 16 dp de margen a los lados.
- `hoja`: varios temas lado a lado, cada uno con su nombre encima.
- 1080 px de ancho y 2.625 px por dp, como el Pixel 6 de Paparazzi. El alto es el que ocupa el mueble.
- Fondo: el color `background` del esquema del tema, liso (sin la textura de `fondoTema`).
- `ARNES_HOY=AAAA-MM-DD` fija el «hoy» de los ejemplos (por omisión, el día lógico de hoy). Cambia las fechas de las entradas y, con ellas, cómo se reparten en el estante. Para comparar con una captura de CI, pon el día en que corrió; las de noviembre de 2026 usan `ARNES_HOY=2026-11-15`.
- `ARNES_SUENOS=1` dibuja la biblioteca como estante de los Sueños: es el `noche` de `EstanteTema`, aparte del claro y el oscuro (la luna en lugar del sello y la luz propia que cada tema enciende ahí). Los volúmenes salen delgados porque los días de ejemplo no traen texto de sueño.
- `ARNES_APP=/ruta/a/app/src/main` dibuja otra copia de la app, por ejemplo otro árbol de trabajo. Compila en `build-otras/<clave>/` y no toca la compilación normal.

## Cuánto tarda (medido en esta máquina, 4 núcleos)

- Primera vez, desde cero (descarga de dependencias aparte): **~1 min 45 s**. Arranca el daemon de Gradle y compila unas 90 000 líneas.
- Volver a dibujar sin cambios: **3 a 9 s**. La compilación ya está al día; el resto es arrancar la JVM y dibujar. Galia es el más lento, con unos 8 s de dibujo.
- Tras editar el cuerpo de un mueble (un color o un trazo dentro de `IdeasGalia.kt` o de `BibliotecaContemporanea.kt`, 12 000 líneas): **4 a 12 s** en total. La compilación es incremental.
- Archivo nuevo (`ideario/IdeasLaboratorio.kt`): **~5 s**. Entra solo y no hay que tocar el arnés.
- Si cambia una interfaz de la que dependen todos (`DisenoIdeas`, `DisenoBiblioteca`, `Tema.kt`), Gradle recompila casi todo: **~1 min 15 s**.

## Varias llamadas a la vez

Se pueden hacer varias llamadas a la vez, desde procesos distintos, cada una con su archivo de salida:

- La compilación va bajo un candado (`flock` sobre `build/.candado`): una llamada compila y las demás esperan. Luego cada una lee el classpath dentro del candado.
- Cada llamada dibuja con una **copia del jar con nombre por su contenido** (`build/instantaneas/arnes-<hash>.jar`). Así una compilación nueva no cambia lo que otra llamada está leyendo. Las copias de más de una hora se borran.
- El PNG se escribe en un temporal y se renombra.

Probado con 3 llamadas en paralelo justo después de editar un archivo: las 3 salieron con el cambio, en 14 s en total.

## Qué enlaza (tal cual, desde `app/src/main/java/...`)

- `ui/ideario/**`, `ui/biblioteca/**` y `ui/muebles/**`: las carpetas enteras, así un archivo nuevo entra solo.
- `ui/*Trazos.kt`, `ui/Galia.kt`, `Nouveau.kt`, `Psicodelia.kt`, `Frente.kt` y `Arcontes.kt`.
- `ui/Tema.kt` (`TemaId`, `Letra`, los esquemas, `tipoDe`, `Tonos`, `PaletaBasica`) y `ui/ModoHora.kt`.
- `ui/Disenios.kt` (`LetraExtra` y las escalas de color de los volúmenes).
- `ui/Estilo.kt`, junto con lo que usa para nombrar meses y fechas: `Temas.kt`, `Vapor.kt`, `CuentaMaya.kt`, `NombresNuevos.kt`, `Textos.kt`, `Claves.kt` y `ui/nombres/**`.
- `data/Modelo.kt`, `Texto.kt`, `Fechas.kt`, `Catalogo.kt`, `Especiales.kt` e `Ideario.kt`.
- Las letras de `app/src/main/res/font/`.

## Piezas sacadas de archivos reales (en cada compilación, a `build/generado`)

Algunos archivos no compilan enteros en escritorio. Para esos, Gradle copia en cada compilación solo las declaraciones que hacen falta, con su texto tal cual (`piezas` en `build.gradle.kts`). Si cambian en la app, el cambio llega solo:

| Archivo | Declaraciones |
|---|---|
| `Escena.kt` | `halo` y `luzCalida` (el aura de los objetos que se tocan) |
| `Libros.kt` | `Pagina` y `Rayas` |
| `data/Mundo.kt` | `CiudadVisitada` y `SitioMarcado` |
| `Decoracion.kt` | `piezasMano` |
| `pantallas/Fichero.kt` | `cajones` y `rotuloCajon` |
| `ArchivoIdeas.kt` | `ArchivoIdeas` y `archivoIdeas` («7 óstraka») |

`R` también se genera, a partir de `res/font` y de `res/drawable*`.

## Sustitutos (escritos a mano, en `src/main/kotlin`)

- `sustitutos/Repo.kt`: `Repo`, con solo `config` y `dias`, en memoria.
- `sustitutos/Sentidos.kt`: `Sentidos.Evento` y `rememberSentir()`, que no hacen nada (vibración y sonido).
- `sustitutos/Fuentes.kt`: `Font(resId, weight, style)` de Android, que carga el `.ttf` con la API de escritorio.
- `sustitutos/Pantallas.kt` y `sustitutos/Cartas.kt`: `simboloCalificacion` y `selloCarta`, vacíos. Los muebles no los dibujan: el primero es para la ficha fijada de Helénico y el segundo para el sobre de Frente exterior.
- `sustitutos/Emblema.kt`: `Emblema`, vacío. Tema.kt lo usa en el selector de temas.
- `sustitutos/Datos.kt`: `Privado.TIPOS` y `Suplementos.INICIALES`, los valores por omisión que nombra `Modelo.kt`.
- `android/*.kt`: cascarones de `Activity`, `Context`, `Configuration`, `View`, `Window`, `WindowCompat` y `LocalView`, solo para que `Tema.kt` compile entero.
- `arnes/Dibujar.kt`: el programa. Reproduce la llamada de `FicheroIdeas` a `MuebleIdeas` y la de `BibliotecaDiario`/`EstantePorMeses` a `EstanteTema`, y en lugar de `TemaVestigium` usa `MaterialTheme(esquemaDe, tipoDe)`.

## Diferencias que quedan frente a las capturas de CI

Comparado con las capturas de CI de heleno, galia y laboratorio, en claro y en oscuro, la diferencia media es de **0.6 a 1.9** por canal (sobre 255). La forma, las proporciones, los colores, el modo oscuro, el aura y las letras coinciden. Lo que sigue distinto:

- **Texto de Compose** (los rótulos del Ideario): sale de 1 a 3 % más ancho que en Paparazzi; por ejemplo, «#aprender» mide 164 px aquí y 159 px en CI. Por eso un rótulo largo se corta una letra antes («#historia de l…» en lugar de «#historia de la…»). Además, el corte con «…» puede dejar un espacio antes de los puntos («#historia …»). Se debe al motor de texto: Skia de escritorio contra el de layoutlib de Android. Cambiar el *hinting* no lo arregló. El texto dibujado con `TextMeasurer` (`textoEn`, el de los volúmenes y las placas de la biblioteca) coincide.
- **Fondo**: aquí es liso; en CI lleva la textura del tema (`fondoTema`).
- Las capturas de CI tienen un alto fijo (2600 o 3000 px) y no se desplazan. Si el mueble no cabe, pasa una de dos cosas:
  - El Ideario de Galia (2815 px) queda **aplastado**: en CI, `height(alto)` choca con el alto de la pantalla, y la base se dibuja más arriba, sobre la última fila.
  - El estante de Galia queda cortado abajo.
  El arnés los dibuja enteros, como se ven en el teléfono al desplazar. En el Ideario de Galia, la parte de abajo difiere de la de CI por esto, no por un error del arnés.

## Si deja de compilar

Puede pasar si un archivo enlazado empieza a usar algo nuevo de fuera: otro objeto de `ui/`, de `data/` o de Android. `dibujar.sh` muestra los errores con su archivo y su línea. Hay tres arreglos, en este orden de preferencia:

1. Enlazar el archivo que lo define: agregarlo a `enlazados` en `build.gradle.kts`.
2. Si ese archivo arrastra media app, sacar solo la declaración: agregarla a `piezas`.
3. Si es algo de Android o de pantallas que los muebles no dibujan, escribir un sustituto en `src/main/kotlin`: mismo paquete y nombre, y que no haga nada.

Nunca se cambia la app para que el arnés compile.

## Limitaciones

- Solo el mueble del Ideario (la vista «_b», con 9 temas) y el estante del diario. No dibuja las vistas «_a» y «_c» del Ideario, los interiores, las fichas, la tapa, la libreta ni las cartas. Agregarlos es repetir en `Dibujar.kt` la llamada de su pantalla.
- Sin la textura del fondo (`fondoTema`), sin la escala de letra del usuario (siempre 1) y sin animaciones: es un instante quieto, como en las capturas.
- No es Paparazzi: la tipografía se parece mucho pero no es idéntica (ver arriba). Para la aprobación final siguen valiendo las capturas de CI.
- Compose Multiplatform 1.7.3 con Kotlin 2.1.0. La app usa Compose de Android 1.7.6 (BOM 2024.12.01).
