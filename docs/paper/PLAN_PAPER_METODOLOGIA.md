# Plan del paper metodológico de Vestigium Mei

_Creado el 26 oct 2026 sobre la versión v81 (commit `fa527df`). Este archivo es la guía que cada sesión debe leer antes de trabajar._

## Cómo se usa

1. Antes de cada módulo, configura el modelo y el esfuerzo de la tabla.
2. Escribe: **«Haz el módulo N del plan del paper»**.
3. La sesión lee este archivo, hace solo ese módulo, guarda lo que produce en `docs/paper/` (en una rama propia, con un pull request para revisarlo) y marca el módulo como hecho en la tabla de estado.
4. Los módulos van en orden. Del 1 al 8 se pueden hacer en cualquier orden entre sí, pero todos deben estar listos antes del 9.

## Tabla de módulos

| # | Módulo | Modelo | Esfuerzo | Capítulo que produce | Estado |
|---|---|---|---|---|---|
| 1 | Inventario y arquitectura de datos | Sonnet | Medio | Cap. 2 y Apéndice A | hecho 2026-10-26 |
| 2 | Medición del estado subjetivo | Opus | Alto | Cap. 3 | hecho 2026-10-26 |
| 3 | Sensores, datos pasivos e importaciones | Sonnet | Medio | Cap. 4 | hecho 2026-10-26 |
| 4 | Modelos fisiológicos y variables derivadas | Opus | Alto | Cap. 5 | hecho 2026-10-27 |
| 5 | Núcleo estadístico (app y R) | Opus | Alto | Cap. 6, Apéndices B y C | hecho 2026-10-26 |
| 6 | Personas y red social | Sonnet | Alto | Cap. 7 | hecho 2026-10-27 |
| 7 | Experimentos personales (N-de-1) | Opus | Alto | Cap. 8 | hecho 2026-10-27 |
| 8 | Reportes, motivación, privacidad e integridad | Sonnet | Medio | Caps. 9 y 10 | hecho 2026-10-27 |
| 9 | Revisión metodológica general (diagnóstico) | Opus | Alto | Lista de cambios propuestos | hecho 2026-10-27 |
| 10 | Aplicar los cambios aprobados | Sonnet | Alto | Código + Apéndice D | hecho 2026-10-28 (10a y 10b el 2026-10-27; 10c y 10d el 2026-10-28; 10e, cierre de hallazgos después del 14, el 2026-10-28; 10f, decisiones del autor sobre los pendientes, esquema 15, el 2026-10-28) |
| 11 | Introducción, marco teórico, discusión y limitaciones | Opus | Alto | Caps. 1 y 11 | hecho 2026-10-28 (resumen en `resumen.md`) |
| 12 | Ensamblado: figuras, formato y referencias | Sonnet | Medio | Documento completo | hecho 2026-10-28 (`ensamblado/`: .md, .docx y .pdf; también como documento de Claude) |
| 13 | Corrección mecánica | Haiku | Bajo | Documento corregido | hecho 2026-10-28 (glosario en `apendiceE_glosario.md`; lista para el 14 en `pendientes_modulo14.md`) |
| 14 | Lectura final de contenido | Opus | Medio | Versión final | hecho 2026-10-28 (versión completa y versión corta en `ensamblado/` y en el documento de Claude) |

**Por qué este reparto.** Opus va donde un error de razonamiento arruina el capítulo: medición psicológica, modelos fisiológicos, estadística, diseño experimental, la revisión y la discusión. Sonnet va donde el trabajo es leer mucho código y describirlo con precisión, programar figuras y ensamblar. Haiku solo hace trabajo mecánico que no requiere juicio científico.

## Reglas comunes para todas las sesiones

**Lenguaje (regla para TODOS los módulos).** El paper es solo para el autor, para entender qué hace la app, cómo y por qué. Se escribe en un lenguaje muy accesible para alguien que no es experto: cada término técnico se explica la primera vez con palabras simples y un ejemplo. **Pero la simplicidad nunca sustituye a la exactitud**: las cifras, umbrales, fórmulas y condiciones del código se dan tal cual; no se redondean ni se «suavizan» para que suene más fácil. Si una explicación sencilla obligaría a decir algo inexacto, se mantiene el término técnico exacto y se explica al lado. Lo que no se pudo comprobar se marca explícitamente como tal. Es preferible dejar algo difícil de entender, pero correcto, que algo fácil pero equivocado.

**Acceso al código.** Desde el 26 oct 2026 el paper vive en el propio repositorio (`docs/paper/`) y se trabaja con Claude Code; las reglas resumidas están en `CLAUDE.md` en la raíz. Todo el código está en `app/src/main/java/io/github/ruddy2003/vestigium/` y las funciones de R en `app/src/main/assets/vestigium.R`. El contexto general de la app está en el documento `VESTIGIUM_MEI_CONTEXTO.md` del Project de claude.ai (no está en el repositorio); si un módulo lo necesita (p. ej., el 8), pídelo.

**Qué se guarda y dónde.** Todo va al repositorio, bajo `docs/paper/`:

- `docs/paper/capNN_<tema>.md`: el borrador del capítulo, en español, en Markdown.
- `docs/paper/figuras/figNN_<nombre>.py`: un script por figura. Cada script genera su PNG con datos sintéticos y no lee nada del teléfono. Cada módulo ejecuta sus scripts, revisa la imagen y guarda el PNG junto al script; el módulo 12 los vuelve a dibujar todos con una paleta común.
- `docs/paper/hallazgos.md`: lista compartida de posibles problemas metodológicos. Cada módulo agrega los suyos al final, con el formato de abajo. **Ningún módulo de contenido cambia el código**; solo anota.
- `docs/paper/referencias.md`: las referencias usadas, en formato APA 7, cada una con un enlace o DOI comprobado en la sesión. Si una referencia no se pudo comprobar, se marca «[sin verificar]» y no se cita como hecho.
- Al terminar, se actualiza la columna «Estado» de este archivo («hecho» y la fecha).

**Formato de cada hallazgo:**

```
### H-<módulo>-<n> · <título corto>
- Dónde: archivo y función
- Qué pasa: descripción concreta
- Por qué importa: consecuencia para la validez de los datos o del análisis
- Gravedad: alta / media / baja
- Propuesta: el cambio mínimo que lo resolvería (o «solo documentar»)
```

**Cómo escribir cada capítulo.** Para cada función que el capítulo cubre:

1. **Qué hace**, en lenguaje claro.
2. **Cómo lo hace exactamente**, tal como está en el código: fórmulas, umbrales, ventanas, mínimos de días. Las fórmulas van en notación matemática y con el nombre de la función de origen.
3. **Razonamiento científico**: qué problema resuelve, qué teoría o literatura lo respalda y qué supuestos asume.
4. **Ejemplo trabajado** con números inventados, paso a paso.
5. **Cuándo falla**: límites y sesgos conocidos.
6. **Figuras**: cuáles necesita, con su script.

Distinguir siempre entre lo que es **validado en la literatura**, lo que es una **aproximación razonable** y lo que es una **decisión de diseño propia**. No presentar heurísticas como modelos validados.

**Privacidad.** Ningún dato real del usuario aparece en el paper: ni nombres de personas, ni texto del diario, ni lugares, ni cifras reales. Todos los ejemplos son sintéticos. El paper puede describir que existen módulos privados (impulsos, recaídas, libido, medicamentos) y cómo se protegen, pero sin datos ni ejemplos personales. Tampoco se mencionan el keystore ni contraseñas del repositorio.

**Extensión orientativa del paper completo:** 60 a 100 páginas con figuras.

## Detalle de cada módulo

### Módulo 1 · Inventario y arquitectura de datos (Sonnet, medio)
- **Leer:** `data/Modelo.kt`, `data/Repo.kt`, `data/Db.kt`, `data/Exportar.kt`, `data/Diccionario.kt`, `data/Catalogo.kt`, `data/Fechas.kt`, `data/Texto.kt`, `ciencia/Dinamicas.kt` (solo para listar familias de variables), cabecera y `vm_leer` de `vestigium.R`.
- **Producir:** `cap02_arquitectura.md` y `apendiceA_variables.md`.
- **Debe cubrir:** separación entre captura activa y pasiva; el «día lógico» que empieza a las 5:00 y por qué; almacenamiento (SQLite de documentos); el panel diario como unidad de análisis; familias de variables dinámicas (`per_`, `tema_`, `lug_`, apps, nutrientes…); exportación (CSV, SQLite, diccionario, script de R); versiones de esquema.
- **Figuras:** diagrama del flujo de datos de la captura a R; diagrama de la estructura del panel diario.
- **Apéndice A:** tabla de todas las variables del panel con fuente, tipo, unidad y descripción (puede generarse desde `Diccionario.kt`).

### Módulo 2 · Medición del estado subjetivo (Opus, alto)
- **Leer:** `data/Catalogo.kt`, `ciencia/Escalas.kt`, las partes de `data/Modelo.kt` sobre `Dia` y `Momento`, `avisos/Avisos.kt` y `avisos/Trabajo.kt` (horarios del muestreo), `data/Presencias.kt`, `data/Perfiles.kt`, `data/Politica.kt`, pantallas `Dia.kt`, `Momentos.kt`, `Conocer.kt`, `Medir.kt`.
- **Producir:** `cap03_estado_subjetivo.md`.
- **Debe cubrir:** modelo circumplejo de Russell (valencia y activación) y por qué escalas de 1 a 7; muestreo de experiencia (ESM): cuándo y cómo se dispara, sesgo de recuerdo frente a registro en el momento; emociones discretas y «no sé nombrarlo»; cuestionarios validados y sus adaptaciones (qué se cambió respecto a la versión original y qué implica para la validez); predicción del día siguiente; sueños por ejes; presencias («estuve», «pensé», «mensajes»); reactividad de la medición (registrar cambia lo registrado); datos faltantes no aleatorios (dejar de registrar los días malos) y el registro de un toque.
- **Figuras:** el circumplejo con ejemplos sintéticos; línea de tiempo de un día con avisos de muestreo; ejemplo de cómo un día faltante sesga el promedio.

### Módulo 3 · Sensores, datos pasivos e importaciones (Sonnet, medio)
- **Leer:** todo `sensores/`, `data/Lugares.kt`, `data/Barometro.kt`, `data/Importaciones.kt`, `data/Importar.kt`, `data/Macrofactor.kt`, `data/Spotify.kt`, la parte de Cold Turkey en `data/Lote3.kt`, `data/Mundo.kt`, pantalla `EstadoDatos.kt`.
- **Producir:** `cap04_datos_pasivos.md`.
- **Debe cubrir:** cada fuente con qué mide, con qué precisión, qué hace cuando falta y cómo se alinea al día lógico; detección de lugares (20 min a menos de 75 m) y reconstrucción de visitas; calibración del barómetro con Open-Meteo; la incompletitud conocida de micronutrientes de MacroFactor; cobertura por variable; por qué ciertas fuentes se importan en lugar de medirse.
- **Figuras:** tabla visual de fuentes y frecuencia; ejemplo sintético de detección de un lugar; mapa de cobertura (días con dato por variable).

### Módulo 4 · Modelos fisiológicos y variables derivadas (Opus, alto)
- **Leer:** `data/Sustancias.kt`, la parte de suplementos de `data/Lote3.kt`, `data/Calculos.kt`, `data/Enfoque.kt`, la herramienta de ritmo de sueño en `ciencia/Analisis.kt`, las partes de sueño, aire libre y km en `ciencia/Estadistica.kt`, pantallas `Cinta.kt` y `SustanciasPantalla.kt`.
- **Producir:** `cap05_modelos_fisiologicos.md`.
- **Debe cubrir:** cafeína en el cuerpo (vida media usada y su variabilidad entre personas); alcohol en sangre con la fórmula de Widmark (constantes usadas y sus límites); modelo de carga de creatina (28 días para llenar, 35 para vaciar, decaimiento geométrico al 5 %); exposición a probióticos estimada por palabras; jet lag social (Wittmann et al., 2006) y regularidad del sueño; noches aproximadas por uso del teléfono cuando falta el reloj; minutos al aire libre. Para cada modelo: de dónde salen los parámetros, qué tan preciso puede ser y que son **estimaciones, no mediciones**.
- **Figuras:** curva de cafeína tras dos dosis; curva de alcohol de Widmark; curva de saturación y lavado de creatina; ejemplo de cálculo del jet lag social.

### Módulo 5 · Núcleo estadístico (Opus, alto)
- **Leer:** `ciencia/Estadistica.kt` completo, `ciencia/Analisis.kt`, `ciencia/Dinamicas.kt`, `ciencia/Periodo.kt`, `ciencia/TusNumeros.kt`, `ciencia/Totales.kt`, pantallas `Explorar.kt` y `Analisis.kt`, y todas las funciones de análisis de `vestigium.R`.
- **Producir:** `cap06_estadistica.md`, `apendiceB_formulas.md`, `apendiceC_funciones_R.md`.
- **Debe cubrir:** construcción del panel; quitar tendencia restando el promedio de ±10 días y por qué; variables «lentas»; Pearson y Spearman (cuál se usa dónde); tamaño de muestra efectivo por autocorrelación de orden 1; intervalo con la transformación z de Fisher; corrección de Benjamini-Hochberg; relaciones «obvias» y cómo se excluyen; agrupación de pares casi repetidos; análisis por semana; «Todo lo que se mueve con algo» con desfase de un día; modelo múltiple con el día anterior y β estandarizados; dinámica emocional (DE, MSSD, inercia); palabras del diario; días atípicos; potencia (por qué 14, 30 y 90 días). Comparar app y R función por función y anotar diferencias en `hallazgos.md`.
- **Figuras:** serie con medias móviles de 7 y 30 días; ejemplo de correlación espuria por tendencia, antes y después de quitarla; efecto de la autocorrelación en el n efectivo; ilustración de Benjamini-Hochberg con 200 pruebas simuladas; tarjeta de relación como la de la app (lazo, grosor, punteado).

### Módulo 6 · Personas y red social (Sonnet, alto)
- **Leer:** `data/Personas.kt`, la parte de red en `data/Lote3.kt`, `data/Presencias.kt`, `data/Perfiles.kt`, pantallas `Gente.kt`, `Constelacion.kt` y la pantalla Red en `ui/pantallas/Lote3.kt`, la función `vm_red` de `vestigium.R`, y cómo se crean las variables `per_`, `pen_` y `vi_` en `ciencia/Estadistica.kt` y `ciencia/Dinamicas.kt`.
- **Producir:** `cap07_red_social.md`.
- **Debe cubrir:** menciones con @ y equivalencias; relaciones con historial (la relación de cada día); cercanía con los círculos de Aron (IOS); constelación (tamaño, distancia, colores, líneas) frente a la pantalla Red (fuerzas, vínculos declarados, componentes); co-mención como aproximación de co-presencia y sus límites; métricas en R (densidad, restricción de Burt, composición) explicadas para quien no sabe de redes; cómo entran las personas en las correlaciones.
- **Figuras:** constelación sintética con la leyenda de colores; red sintética con un puente entre dos grupos para explicar la restricción de Burt; tabla de relación según la fecha.

### Módulo 7 · Experimentos personales N-de-1 (Opus, alto)
- **Leer:** el objeto `Experimentos` en `data/Lote3.kt`, la pantalla de experimentos en `ui/pantallas/Lote3.kt`, el aviso de experimento en `ui/pantallas/Dia.kt`, `Estadistica.diferencia`, la sección «Antes y después» (`EtapasDeVida`) de `ui/pantallas/Explorar.kt`.
- **Producir:** `cap08_experimentos.md`.
- **Debe cubrir:** lógica de los diseños N-de-1 (ABAB y antes/después) y la literatura que los respalda; sellado de pregunta e hipótesis como prerregistro; si el plan de semanas se sortea de verdad y cómo; días de lavado y cómo se calculan; diferencia de medias con su intervalo; d de Cohen con desviación agrupada y sus umbrales; prueba de tendencia (pendiente con |b/EE| > 2); plantillas y por qué la creatina va en antes/después; «Antes y después» de cambios de vida (60 días contra 60 días) como estudio observacional, no experimento; falta de cegamiento y efecto expectativa.
- **Figuras:** calendario de un experimento ABAB con días de lavado marcados; resultado sintético con ambas distribuciones y el d de Cohen; ejemplo de cómo una tendencia crea una diferencia falsa en un diseño antes/después.

### Módulo 8 · Reportes, motivación, privacidad e integridad (Sonnet, medio)
- **Leer:** `reportes/Reportes.kt`, `ciencia/Logros.kt`, `ciencia/Campanas.kt`, `ciencia/TuAnio.kt`, `data/Respaldos.kt`, `data/Drive.kt`, `data/Recaidas.kt`, sección 7 de `VESTIGIUM_MEI_CONTEXTO.md`, pantallas `Respaldo.kt` y `EstadoDatos.kt`.
- **Producir:** `cap09_reportes_motivacion.md` y `cap10_privacidad_integridad.md`.
- **Debe cubrir:** qué muestra cada reporte y qué decisiones de visualización se tomaron; logros, campañas y temas como sistema de motivación, y su riesgo para la validez (registrar para ganar puntos, reactividad); por qué los logros nunca se ligan a sustancias, impulsos, recaídas ni libido; seguimiento privado y dónde aparece y dónde no; cifrado del respaldo (AES-256-GCM, PBKDF2 con 210 000 iteraciones) y verificación sin restaurar; procesamiento local y servicios externos usados.
- **Figuras:** ejemplo sintético del reporte semanal; diagrama de qué datos llegan a cada salida (pantallas, reportes, R).

### Módulo 9 · Revisión metodológica general (Opus, alto)
- **Leer:** todos los capítulos de `docs/paper/`, `hallazgos.md` completo y el código que cada hallazgo menciona.
- **Producir:** `docs/paper/revision_propuestas.md`.
- **Qué hace:** confirma o descarta cada hallazgo leyendo el código; busca problemas que los módulos anteriores no vieron, sobre todo en cómo se conectan las piezas (una variable que un módulo define y otro usa distinto, diferencias entre app y R, datos privados que se cuelan en análisis o pantallas donde no deben); y propone cambios.
- **Criterio para proponer un cambio de código.** Solo si cumple al menos una de estas condiciones: (a) un cálculo es incorrecto o engaña al usuario; (b) la app y R dan resultados distintos para lo mismo sin razón; (c) un dato privado aparece donde no debe; (d) se pierde o corrompe información; (e) una variable no puede cruzarse con otras cuando debería. **No** cuentan funciones nuevas, rediseños ni preferencias de estilo. Si basta con documentarlo en el paper, se documenta.
- **Formato de cada propuesta:** qué cambia, por qué, archivo y función, riesgo, cómo se comprobará y qué capítulo hay que actualizar.
- **Al terminar:** la sesión presenta la lista y se detiene. Tú tachas o comentas lo que no quieras antes de pedir el módulo 10. Lo que no taches se considera aprobado.

### Módulo 10 · Aplicar los cambios aprobados (Sonnet, alto)
- **Leer:** `revision_propuestas.md` con tus anotaciones.
- **Qué hace:** aplica solo los cambios aprobados, con el flujo de siempre: rama aparte, compilar en GitHub Actions, revisar capturas, pasar a `main` y verificar la firma. Sube el número de esquema si cambia algún dato guardado. Actualiza el script de R si el cambio lo afecta. Actualiza los capítulos afectados y escribe `apendiceD_cambios.md` con cada cambio, su motivo y la versión en que entró.
- **Si algo no es trivial** (un cambio que toca muchos archivos o que tiene más de una forma razonable de hacerse), se detiene y pregunta.
- **No manda el APK por el chat**: solo dice qué versión salió.

### Módulo 11 · Introducción, marco teórico, discusión y limitaciones (Opus, alto)
- **Leer:** todos los capítulos ya corregidos, `apendiceD_cambios.md`, `referencias.md`.
- **Producir:** `cap01_introduccion.md` y `cap11_discusion.md`, más un resumen (abstract) de 250 palabras.
- **Debe cubrir:** el Quantified Self y el autoconocimiento longitudinal; diseños de un solo sujeto e idiográficos; por qué un sistema de por vida y no por etapa; el principio de que todo pueda cruzarse con todo, y su costo (comparaciones múltiples); qué puede y qué no puede concluirse de estos datos; validez de constructo, sesgos y reactividad en conjunto; trabajo futuro (modelos multinivel, autocorrelación AR(1) en R, validación de las heurísticas).

### Módulo 12 · Ensamblado (Sonnet, medio)
- **Al empezar, pregunta el formato final:** documento de Claude (se puede exportar a Word o PDF) o archivo .docx directo.
- **Qué hace:** ejecuta todos los scripts de figuras y revisa cada imagen; une los capítulos en orden; numera figuras, tablas y ecuaciones; arma la tabla de contenido; da formato APA 7 a citas y lista de referencias, y elimina las que sigan «[sin verificar]» o las marca de forma visible; aplica una paleta de colores sobria y la misma en todas las figuras.

### Módulo 13 · Corrección mecánica (Haiku, bajo)
- **Qué hace:** ortografía, acentos, puntuación, términos usados de forma consistente (por ejemplo, siempre «valencia» y no a veces «ánimo» para la misma variable), que cada figura y tabla citada exista, que la numeración sea continua y que el glosario cubra los términos técnicos. **No cambia contenido científico**: si algo parece un error de fondo, lo anota en una lista para el módulo 14.

### Módulo 14 · Lectura final de contenido (Opus, medio)
- **Qué hace:** lee el paper completo como lo leería un revisor; revisa la lista que dejó el módulo 13 (`pendientes_modulo14.md`); comprueba que ningún dato real se haya colado; verifica que lo que dice el paper coincide con el código de la versión final; corrige lo necesario y entrega la versión final.

## Pistas preliminares para la revisión (por verificar en los módulos 5, 6, 7 y 9)

Encontradas al planear, leyendo el código de v81. No son conclusiones: cada módulo debe confirmarlas o descartarlas y pasarlas a `hallazgos.md`.

1. **Experimentos: intervalos demasiado estrechos.** `Experimentos.resultado` y `Estadistica.diferencia` tratan cada día como independiente, pero los días de una misma semana se parecen entre sí. El intervalo y la claridad del efecto pueden exagerarse. Opciones: usar un n efectivo como el de las correlaciones o una prueba de aleatorización por bloques de semanas.
2. **Experimentos: z en vez de t.** `Estadistica.diferencia` usa 1.96 fijo; con pocas observaciones por condición debería usarse t. El d de Cohen no aplica la corrección de Hedges para muestras pequeñas.
3. **Experimentos: tipo de sorteo.** El plan sí se sortea (`ui/pantallas/Lote3.kt`, `shuffled` de la mitad de semanas «sí» y la mitad «no») y queda guardado. Pero un sorteo libre puede producir «SÍ SÍ SÍ no no no», que en la práctica es un antes/después y queda expuesto a tendencias. En diseños N-de-1 se suele sortear por pares de semanas (en cada par, una «sí» y una «no»). Evaluar si conviene cambiarlo.
4. **Constelación y red con criterios distintos.** La constelación toma los momentos excepto el tipo `recaida`; la red excluye todos los momentos privados. Si un momento privado (impulso, libido) menciona a alguien, cuenta para la constelación. Revisar si es intencional.
5. **Pearson y Spearman mezclados.** La lista de relaciones usa Pearson sobre residuos sin tendencia; «Todo lo que se mueve con algo» muestra ρ; `vm_cruce` en R usa Spearman por defecto. Verificar que app y R den lo mismo para lo mismo.
6. **Relación actual en la constelación.** La constelación colorea con la relación de hoy, mientras los análisis usan la relación de cada día. Para visualizar puede estar bien, pero hay que documentarlo.

## Tabla de capítulos del paper

1. Introducción y marco teórico (módulo 11)
2. Arquitectura y flujo de datos (módulo 1)
3. Medición del estado subjetivo (módulo 2)
4. Sensores, datos pasivos e importaciones (módulo 3)
5. Modelos fisiológicos y variables derivadas (módulo 4)
6. Análisis estadístico (módulo 5)
7. Personas y red social (módulo 6)
8. Experimentos personales (módulo 7)
9. Reportes, visualización y motivación (módulo 8)
10. Privacidad, seguridad e integridad de los datos (módulo 8)
11. Discusión, limitaciones y trabajo futuro (módulo 11)
- Apéndice A: diccionario de variables (módulo 1)
- Apéndice B: fórmulas (módulo 5)
- Apéndice C: funciones de R (módulo 5)
- Apéndice D: cambios metodológicos aplicados (módulo 10)
