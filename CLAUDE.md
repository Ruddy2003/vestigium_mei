# Vestigium Mei — guía para Claude

App personal de Android (Kotlin + Jetpack Compose) para registrar, a lo largo de los años, el rastro de cada día. Todo se guarda en el teléfono (SQLite de documentos JSON) y se exporta a CSV + un script de R. Hay un solo usuario: el autor del repositorio. Todo se escribe en **español**: código, comentarios, textos de la app, commits y documentos.

## Dónde está cada cosa

- `app/src/main/java/io/github/ruddy2003/vestigium/`: todo el código.
  - `data/`: modelo (`Modelo.kt`, con `SCHEMA_V`), almacenamiento (`Repo.kt`, `Db.kt`), fechas y «día lógico» (`Fechas.kt`), exportación (`Exportar.kt`, `Diccionario.kt`), importaciones.
  - `sensores/`: datos pasivos (Health Connect, pantalla, ubicación, percepción…).
  - `ciencia/`: panel diario y estadística (`Estadistica.kt`, `Dinamicas.kt`, `Analisis.kt`), cuestionarios (`Escalas.kt`).
  - `avisos/`: notificaciones y la tarea periódica de 15 minutos (`Trabajo.kt`).
  - `ui/`: pantallas y componentes.
- `app/src/main/assets/vestigium.R`: funciones de R que viajan con cada exportación.
- `herramientas/`: scripts de Python para generar recursos (íconos, sonidos, arte).
- `docs/paper/`: el paper metodológico (ver abajo).
- `.github/workflows/`: `apk.yml` compila y publica la APK; `capturas.yml` genera capturas de diseño (Paparazzi) en la rama `capturas`.

## Compilar y publicar

- No hay SDK de Android en el entorno de Claude: **la compilación se verifica en GitHub Actions**. Cada push a `main` compila la APK y publica un Release (`versionCode` = número de ejecución). Los errores de compilación quedan en la rama `ci-log`; las capturas, en la rama `capturas`.
- Los cambios de código van en una **rama aparte**, se compila con `workflow_dispatch` o al mezclar, se revisan las capturas y solo después se pasa a `main`.
- Si un cambio altera lo que se guarda, **sube `SCHEMA_V`** en `data/Modelo.kt` y, si afecta la exportación, actualiza `Diccionario.kt` y `vestigium.R`.
- **No toques la firma** (`app/vestigium.jks` y `signingConfigs` en `app/build.gradle.kts`): con otra llave las versiones nuevas ya no se instalan encima y se perderían datos. Nunca la menciones en documentos.
- No mandes la APK por el chat; di qué versión salió.

## Privacidad (siempre)

- Nunca subas al repositorio datos reales: exportaciones (`vm_*.csv`, `.sqlite`, `.zip`), respaldos, capturas con datos, nombres de personas, texto del diario, lugares ni cifras personales.
- El seguimiento privado (impulsos, recaídas, libido, medicamentos) se describe solo en su mecánica, nunca con ejemplos personales.
- Archivos temporales de trabajo: fuera del repositorio o en carpetas ignoradas.

## El paper metodológico (`docs/paper/`)

Un documento largo, solo para el autor, que explica qué hace la app, cómo y por qué, con su base científica. Se hace por **módulos**; el plan completo, el reparto de modelos y la tabla de estado están en `docs/paper/PLAN_PAPER_METODOLOGIA.md`. Cuando se pida «haz el módulo N», lee primero ese plan y haz **solo** ese módulo.

Archivos:
- `capNN_<tema>.md`: un capítulo por archivo. `apendiceX_<tema>.md`: apéndices.
- `figuras/figNN_<nombre>.py` + su `.png`: un script por figura, con **datos sintéticos**; ejecútalo, revisa la imagen y guarda el PNG.
- `hallazgos.md`: problemas metodológicos encontrados, en formato H-<módulo>-<n> (Dónde / Qué pasa / Por qué importa / Gravedad / Propuesta). Cada módulo agrega los suyos al final.
- `referencias.md`: APA 7, cada una con DOI o enlace **comprobado en la sesión**; lo no comprobado se marca «[sin verificar]».

Reglas de escritura:
- **Lenguaje muy accesible para alguien no experto, pero sin perder exactitud.** Cada término técnico se explica la primera vez con palabras simples y un ejemplo. Las cifras, umbrales, fórmulas y condiciones del código se dan tal cual, con el nombre de la función de origen. Si simplificar obligaría a decir algo inexacto, se deja el término exacto y se explica al lado. Es mejor algo difícil pero correcto que algo fácil pero equivocado.
- Para cada función: qué hace, cómo exactamente (según el código), razonamiento científico, ejemplo trabajado con números inventados, cuándo falla.
- Marca cada decisión como **[Validado]**, **[Aproximación]** o **[Diseño propio]**. No presentes heurísticas como modelos validados.
- Lo que no puedas comprobar, dilo explícitamente.
- **Los módulos de contenido (1 a 8, 11 a 14) no cambian código**: solo anotan hallazgos. El código se cambia únicamente en el módulo 10, con lo aprobado en `revision_propuestas.md`.
- Al terminar un módulo: marca su fila como «hecho <fecha>» en el plan, haz commit en una rama `paper-modulo-N` y abre un pull request hacia `main`.

### Mantener el paper al día cuando cambia la app

- Si un cambio de código altera algo que `docs/paper/` describe (qué se mide, cómo se calcula, umbrales, ventanas, horarios de avisos, variables del panel o de la exportación, funciones de R), **actualiza esa sección en el mismo PR** y dilo en la descripción del PR. Si el cambio resuelve un hallazgo, márcalo en `hallazgos.md` como «Resuelto en <versión/commit>».
- Si cambia lo que se calcula y la figura lo ilustra, actualiza también su script y su PNG.
- **No hace falta tocar el paper** en cambios que no afectan la metodología: colores, temas, íconos, sonidos, textos de la interfaz, acomodo de pantallas, animaciones, widgets solo visuales, correcciones de errores que no cambian ningún cálculo.
- Si el capítulo que tocaría aún no existe (su módulo está pendiente), no lo escribas: basta con que el módulo correspondiente lea el código vigente cuando se haga.
