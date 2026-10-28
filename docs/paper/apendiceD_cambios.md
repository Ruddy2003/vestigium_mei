# Apéndice D · Cambios metodológicos aplicados

_Cada cambio del módulo 10 con su motivo (hallazgo y propuesta de `revision_propuestas.md`) y la versión en que entró. El módulo 10 se hace en cuatro entregas (10a privacidad; 10b datos que no se guardaban; 10c cálculos; 10d R y exportación). Este apéndice crece con cada una._

## Entrega 10a · Privacidad (commit `a67bad0`; versión v85)

Decisiones del autor (27 oct 2026):
- D-1 y D-2, ampliadas: todo lo privado se ve solo en una pantalla de Privado y viaja a R.
- Los análisis de esa pantalla son los que ya tenía más una tendencia simple; las correlaciones, solo en R.
- El impulso deja de ser medida de los experimentos.
- Con datos privados, ningún respaldo sale del teléfono sin contraseña.

No cambia lo que se guarda: `SCHEMA_V` sigue en 12.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.1 | Regla única de lo privado. Son privados los momentos de impulso, recaída y libido; en el día, la intensidad del impulso, la recaída, los detonantes y los medicamentos; y el hábito ligado a las recaídas. También las variables `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `n_impulso`, `medicamentos_n`, `med_*`, `det_*` y `hab_<ligado>` | `data/Privado.kt` (nuevo); `Momento.privado` en `data/Modelo.kt` | Había tres nociones de «privado» que no coincidían con la regla declarada (H-9-39) |
| D.2 | Lo privado no aparece fuera de la pantalla Privado. Afecta a Día, Hoy, momentos, cinta, diarios, libreta, calendario, semana, año, totales, mapa, widgets, reportes, «Tu año», logros (también los propios), metas, campañas, experimentos, Constelación, Red, Tu gente, las sugerencias de @ y #, «Tus temas» y los análisis. Vale también con el interruptor encendido | Unos 30 archivos de `ui/`, `ciencia/`, `data/`, `reportes/` y `widget/` | H-8-1, H-8-2, H-8-3, H-8-16, H-6-1, H-9-1, H-9-38 (P-01 a P-03 y P-34) |
| D.3 | `Estadistica.variables()` excluye las variables privadas, salvo con `privadas = true`, que solo usa el diccionario. El panel las calcula siempre, sin depender del interruptor, para la exportación | `ciencia/Estadistica.kt`, `ciencia/Dinamicas.kt` | Que no lleguen a ningún análisis de la app, pero sí a R completas |
| D.4 | `per_`, `pen_` y `tema_` ya no leen momentos privados, y sus días ya no cuentan como «días con diario» para los ceros. `momentos_intensidad` y `n_madrugada`…`n_noche` ya no incluyen impulsos | `ciencia/Dinamicas.kt`, `ciencia/Estadistica.kt` | H-6-1, H-9-27. **Cambia valores**: algunas columnas `per_`/`tema_` pueden desaparecer del panel extendido |
| D.5 | Pantalla Privado (antes «Impulsos y recaídas»), siempre accesible desde Más. Tiene: registro por día con medicamentos y «lo de siempre»; con el interruptor, también impulso, recaída, detonantes y hábito ligado; el resumen que ya existía; una tendencia de 12 meses (recaídas, impulsos, días con medicamentos); los días que se tomó cada medicamento; y el registro editable de los últimos 30 momentos privados. Desde Día, el enlace «privado de este día» abre la fecha | `ui/pantallas/Recaidas.kt`, `ui/pantallas/Dia.kt`, `Mas.kt`, `Pantallas.kt`, `Sistema.kt` | Decisión del autor |
| D.6 | Con datos privados y sin contraseña, no se escribe en la carpeta, no se sube a Drive y no se puede compartir el respaldo completo. Hay aviso en Hoy y una notificación por semana. La exportación para R (a mano) sigue en claro, porque R necesita leerla, y la pantalla lo advierte | `data/Respaldos.kt` (`bloqueado`), `data/Drive.kt`, `avisos/Trabajo.kt`, `avisos/Avisos.kt`, `ui/pantallas/Respaldo.kt`, `Hoy.kt`, `Sistema.kt` | H-8-5 (en parte) |
| D.7 | Los reportes que se guardan solos (lunes y día 1) van en modo discreto y sin la frase del mejor momento | `reportes/Reportes.kt` | H-8-4 (en parte; P-06) |
| D.8 | Las coordenadas de un momento privado no se usan para pedir el clima | `sensores/Entorno.kt` | H-9-6 (P-04) |
| D.9 | Elegir «ninguno» como hábito ligado vuelve a funcionar: antes caía en la búsqueda por palabras clave | `data/Recaidas.kt` | Error encontrado al aplicar D.1 |
| D.10 | `vm_mapa_animo()` usa solo registros de ánimo con su propia valencia, sin seguimiento privado ni la valencia del diario prestada | `assets/vestigium.R` | H-2-15 (parte de R), H-9-12 (P-05) |
| D.11 | Prueba JVM de la regla | `app/src/test/.../PrivadoPrueba.kt` | Que una variable o un momento privado no vuelva a colarse sin que falle una prueba |

**Efectos que conviene saber al leer los datos:**
- Con el interruptor apagado, antes no existían en el panel `n_recaida`, `recaida_min`, `libido_max`, `n_libido` ni `det_*`. Ahora existen si hay datos: el panel exportado puede traer columnas nuevas.
- La intensidad del impulso ya no está en el diario de la noche, sino en la pantalla Privado. Es posible que se anote menos días (capítulo 3, sección 3.16).
- Si no hay contraseña y existen datos privados (por ejemplo, un solo medicamento anotado), los respaldos automáticos quedan **en pausa** hasta que se ponga una.

**Capítulos actualizados:** 3 (secciones 3.7 y 3.16), 6 (secciones 6.3 y 6.6, familias dinámicas), 7 (secciones 7.2 y 7.13), 9 (secciones 9.3.1 y 9.4), 10 (secciones 10.1, 10.3, 10.5 y 10.10, y la Figura 10.1) y los apéndices A y C.

## Entrega 10b · Datos que no se guardaban (esquema 13)

Decisiones del autor (27 oct 2026):
- D-3: sí. La pantalla y los pasos pasan al día lógico.
- Días especiales (I-6): sí, con fechas de fallecimiento y con aviso de cumpleaños.
- Edad de las personas (I-7): se ve en Tu gente y va a R. No entra en los análisis de la app.

**`SCHEMA_V` sube de 12 a 13**, una sola vez para toda la entrega. Los respaldos viejos se siguen leyendo, porque todos los campos nuevos tienen valor por omisión. Al revés, una app con esquema 12 no puede restaurar un respaldo del 13: esta entrega es justo la que empieza a rechazarlo.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.12 | Los documentos que no se pueden leer se cuentan por colección y se muestran en Estado de los datos y en Hoy. Una configuración ilegible se copia a `config_ilegible` antes de seguir con una vacía | `data/Decodificar.kt` (nuevo), `data/Repo.kt` (`leer`, `ilegibles`, `configIlegible`), `ui/pantallas/EstadoDatos.kt`, `Hoy.kt` | H-1-3, H-9-2 (P-07) |
| D.13 | Restaurar: un respaldo con esquema más nuevo no se restaura. Gastos e ingresos repetidos no se pisan, ni en la base ni en memoria. Un experimento terminado no se reactiva y se juntan sus respuestas. «Verificar» dice solo «se lee completo» y compara respaldo y teléfono | `data/Importar.kt` (`esquemaFuturo`), `data/Fusion.kt` (nuevo), `data/Repo.kt`, `data/Respaldos.kt` | H-8-12, H-8-13 (en parte), H-9-28, H-9-33 (P-08) |
| D.14 | Drive no reemplaza la copia por una con menos del 90 % de días o momentos que la última (si esa tenía 20 o más): la guarda como `vestigium_revisar` y avisa. Subir a mano la reemplaza. En Drive y en la carpeta, los archivos en claro se borran solo después de escribir el cifrado | `data/Drive.kt`, `data/Respaldos.kt` (`empobrecido`, `aCarpeta`), `Config.drive_conteo`, `avisos/Avisos.kt` (`driveEmpobrecido`) | H-8-14 (en parte), H-9-37 (P-09) |
| D.15 | Colección nueva `avisos` (`AvisosDia`): planeados, vencidos y mostrados por día. Panel: `avisos_planeados`, `avisos_mostrados`, `avisos_respondidos`, `tasa_respuesta` (familia obvia). `n_muestreos` cuenta todos los respondidos. La tarjeta de avisos sin contestar abre el aviso más antiguo | `data/AvisosAnimo.kt` (nuevo), `avisos/Trabajo.kt`, `data/Repo.kt`, `ciencia/Estadistica.kt`, `data/Exportar.kt`, `Hoy.kt`, `vestigium.R` | H-2-3, H-9-9, H-9-10, H-9-11 (P-10) |
| D.16 | Campañas: `aceptada` y `abandonada`. Abandonar ya no borra. No cuentan los días antes de aceptar. Un hábito desactivado no anula sus campañas. Panel: `campana_<reto>` y `campanas_n`, obvias con lo que cada reto mueve | `ciencia/Campanas.kt` (`tramo`, `diasActivos`, `MUEVE`, `obvia`), `ui/pantallas/Campanas.kt`, `ciencia/Estadistica.kt`, `data/Exportar.kt`, `data/Diccionario.kt`, `vestigium.R` (`vm_campana_mueve`) | H-8-7, H-9-34, H-9-35, H-9-36 (P-11) |
| D.17 | Experimentos: `fin` (al terminar) y `hecho` («¿Lo hiciste hoy?», en las dos condiciones). El resultado corta en `fin`, da el cumplimiento por condición y un cociente secundario. El aviso sale en todos los días del experimento hasta hoy, también ya terminado, y dice cuándo es lavado | `data/Lote3.kt` (`ultimoDia`, `cubre`, `enCurso`, `Cumplimiento`), `ui/pantallas/Dia.kt` (`AvisoExperimento`), `ui/pantallas/Lote3.kt`, `Explorar.kt`, `data/Exportar.kt` | H-7-2, H-7-8, H-9-29 (P-12, I-3) |
| D.18 | `Dia.ts_valencia`, y `retroactivo` según el día lógico de esa hora | `data/Reglas.kt` (nuevo), `data/Repo.kt`, `data/Exportar.kt` | H-2-2 (P-13) |
| D.19 | Pantalla y pasos por día lógico (05:00 a 05:00), con `PantallaDia.ventana` y `SaludDia.ventana_pasos`. La cinta y `vm_horas` ponen las horas 0 a 4 en la madrugada siguiente | `sensores/Pantalla.kt`, `sensores/Salud.kt`, `data/Fechas.kt`, `ui/pantallas/Cinta.kt`, `data/Exportar.kt` | H-3-1 (en parte), H-3-2 (P-16 ii) |
| D.20 | Días especiales: `Persona.nacimiento` y `fallecimiento` (año opcional) y `Config.fechas_propias`. Aviso en la mañana (se puede apagar), aviso en el Día y sección en Tu gente con los próximos. Panel: `dia_especial`; exportación: `dia_especial_tipo`, `vm_personas.csv` con la edad y `vm_fechas_propias.csv` | `data/Especiales.kt` (nuevo), `data/Personas.kt`, `ui/pantallas/Gente.kt`, `avisos/Trabajo.kt`, `avisos/Avisos.kt`, `ui/pantallas/Dia.kt`, `ciencia/Estadistica.kt`, `data/Exportar.kt`, `vestigium.R` | I-6, I-7 |
| D.21 | Pruebas JVM: decodificación, esquema futuro, fusión de gastos y experimentos, Drive empobrecido, cifras de avisos, campañas por día, experimento terminado con cumplimiento, valencia retroactiva y días especiales | `app/src/test/.../IntegridadPrueba.kt`, `DatosNuevosPrueba.kt` | Que cada regla falle en una prueba si se rompe |

**Efectos que conviene saber al leer los datos:**
- **La serie de pantalla y pasos se parte** el día que se instala esta versión. Hay que analizar por tramo (`ventana_pantalla`, `ventana_pasos`). En el tramo nuevo, lo de la madrugada cuenta para el día anterior.
- **`n_muestreos` cambia de definición**: ahora cuenta también los avisos respondidos sin valencia, así que puede subir en los días viejos.
- **`retroactivo` puede cambiar** en días ya guardados: si se vuelve a guardar la valencia de un día viejo, su hora decide.
- **Lo que no se puede recuperar.** Las variables `avisos_*` y `tasa_respuesta` solo existen desde esta versión. Las campañas de antes no tienen fecha de aceptación (cuentan la semana completa), y los experimentos terminados antes no tienen `fin`.
- **Respaldos.** La v85 y las anteriores no revisan el esquema: si restauran un respaldo de esta versión, pierden en silencio los campos nuevos. Desde esta versión, la app rechaza cualquier respaldo de un esquema más nuevo que el suyo.

**Capítulos actualizados:**
- capítulo 2: secciones 2.3.2, 2.4.2, 2.4.3, 2.5 y 2.9;
- capítulo 3: secciones 3.5.3, 3.7.4 y 3.15.2;
- capítulo 4: secciones 4.3 y 4.4.1;
- capítulo 6: sección 6.9.2;
- capítulo 8: secciones 8.3.3, 8.6.1 y 8.7, y resúmenes;
- capítulo 9: sección 9.5 y resúmenes;
- capítulo 10: secciones 10.5, 10.6, 10.7, 10.9 y resúmenes;
- los apéndices A (variables, tablas y la sección A.4 nueva) y C.

## Revisión de navegación · Entregas N0 y NE (esquema 14)

Salen de `docs/navegacion/revision_navegacion.md`. Decisiones del autor del 27 oct 2026: aprobó las 30 propuestas y el orden de entregas, y contestó las preguntas P-1 a P-11 (§ 13 del informe).

**`SCHEMA_V` sube de 13 a 14**, una sola vez para todos los campos nuevos de la revisión. Los respaldos viejos se siguen leyendo, porque todos los campos nuevos tienen valor por omisión. Una app con esquema 13 rechaza un respaldo del 14 (`Importar.esquemaFuturo`).

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.22 | Corregir una recaída: cambiar solo el día, con la misma hora, ya la mueve. Borrarla desde el diálogo quita la marca del día si era la única | `ui/pantallas/Momentos.kt`, `data/Recaidas.kt` (`alBorrar`) | H-NAV-6 (N0) |
| D.23 | «Volver a lo del reloj» pide confirmación, avisa si el reloj no tiene esa noche y se puede deshacer. Restaurar un respaldo no pisa el sueño corregido a mano | `ui/EditarSueno.kt`, `data/Fusion.kt` (`salud`), `data/Importar.kt` | H-NAV-5 (N0) |
| D.24 | «Día difícil» se ve en la hoja de la noche y se puede quitar | `ui/pantallas/Dia.kt` | H-NAV-8 (N0) |
| D.25 | Cuándo se anotó (`ts_creado`) en momentos, gastos, ingresos, tomas, consumos y tramos, y cuándo se corrigió (`ts_editado`) en momentos, gastos e ingresos. La marca se pone donde se crea cada registro, no al guardar, para que «deshacer» o restaurar no marque como nuevo algo viejo | `data/Modelo.kt`, `ui/QuickActivity.kt`, `ui/pantallas/Gastos.kt`, `ui/pantallas/Lote2.kt`, `ui/pantallas/Momentos.kt`, `data/Enfoque.kt`, `data/Sustancias.kt`, `data/Lote3.kt`, `data/Exportar.kt`, `vestigium.R` (`vm_retraso`) | P-3, H-2-9 (en parte), H-NAV-9 (N-30) |
| D.26 | `Dia.version_noche`: cada día que pasa por la hoja de la noche guarda con qué orden de preguntas se llenó (1 por ahora) | `data/Modelo.kt` (`VERSION_NOCHE`), `ui/pantallas/Dia.kt`, `data/Exportar.kt` | P-8, preparación de N-18 |
| D.27 | Campos para lo que viene: `Gasto.subcategoria` y `Config.subcategorias` (N-29), `SaludDia.sueno_descartadas` (N-04) | `data/Modelo.kt`, `data/Exportar.kt`, `data/Diccionario.kt` | P-7, H-NAV-3 |
| D.28 | Papelera de 7 días para los registros que borras tú (sin pantalla todavía) | `data/Repo.kt` (`aPapelera`, `papelera`, `restaurar`, `limpiarPapelera`), `avisos/Trabajo.kt` | P-9 (N-14) |

**Efectos que conviene saber al leer los datos:**
- **`ts_creado` y `ts_editado` solo existen desde esta versión.** En los registros de antes van vacíos; `vm_retraso` los deja en NA. No se pueden reconstruir.
- **`version_noche` vale 1** en todos los días que pasen por la hoja desde esta versión. Cuando entre el orden nuevo (N-18), pasará a 2. Los análisis que crucen esa fecha deben leerse por tramo.
- **`subcategoria` y `sueno_descartadas` estarán vacías** hasta que lleguen sus pantallas (N-29 y N-04).

**Capítulos actualizados:** capítulo 4 (sueño, sección del día asignado), capítulo 10 (secciones 10.3.1, 10.7 y la 10.7.1 nueva) y los apéndices A (sección A.5) y C.


## Revisión de navegación · Entrega N1 (corregir donde se ve)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.29 | Las cifras del Tablero se tocan: el sueño abre su corrección; el ánimo y las palabras, la hoja de la noche; el gasto, Gastos; lo demás despliega «Lo que registró el teléfono» | `ui/pantallas/Hoy.kt` (`Tablero`, `TarjetaMetrica`), `ui/pantallas/Dia.kt`, `ui/Componentes.kt` (`Seccion(abrir)`) | N-01, P1 |
| D.30 | En el Día, cada fila de «Momentos de este día» y de «Enfoque y siestas» abre su corrección (momento, sustancia o tramo). Los tramos se corrigen por primera vez: tipo, inicio, duración y nota | `ui/pantallas/Dia.kt`, `ui/pantallas/EnfoqueSiestas.kt` (`DialogoTramo`) | N-02, P1 |
| D.31 | `DialogoMomento` corrige la fecha y la hora de cualquier registro y guarda `ts_editado`. Un momento viejo pasa a tipo `animo` al corregirlo | `ui/pantallas/Momentos.kt`, `data/Recaidas.kt` (`aOtraHora`) | N-03, H-NAV-2, H-2-9 |
| D.32 | Gastos e ingresos se corrigen en un diálogo, donde se tocan; registrar confirma con un aviso. El gasto y el consumo rápidos no se registran dos veces con un toque doble, y tocar fuera con un monto escrito avisa antes de cerrar | `ui/pantallas/Gastos.kt` (`DialogoGasto`, `DialogoIngreso`), `ui/QuickActivity.kt` | N-06 |
| D.33 | Deshacer en los borrados que no lo tenían (notas, metas, fechas propias, logros propios, citas, bloques, sitios ignorados, frases, ciudades, Patrimonio, reglas de nombres, categorías, mascotas y personas cercanas) y confirmación en los de un toque. La barra de Deshacer también en el registro rápido. Su botón «Deshacer» se llama ahora «Borrar este registro» | varios archivos de `ui/`, `ui/QuickActivity.kt` | N-13, P4 |

**Efectos al leer los datos:** desde esta versión, un registro de ánimo puede cambiar de hora y de día después de anotarlo; queda marcado con `ts_editado`. Los momentos viejos que se corrijan cambian de tipo a `animo`.

## Revisión de navegación · Entrega N2 (ayer y fechas)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.34 | Suplementos: «¿Cuándo la tomaste?» (ahora, ayer u otra hora) antes de «Tomé…»; «Últimas tomas» se tocan para corregir cantidad u hora, o quitarlas con Deshacer. En ⊕, tocar un suplemento ya tomado pregunta «otra toma» o «quitar la de hoy». El aviso «Ya los tomé» dice qué anotó y ofrece «Deshacer» durante 10 minutos; el mosaico rápido también dice qué anotó | `ui/pantallas/Lote3.kt` (`DialogoToma`), `data/Lote3.kt` (`registrar`, `registrarFaltantes`, `resumen`), `ui/QuickActivity.kt`, `avisos/Recordatorios.kt`, `avisos/Avisos.kt` (`tomasAnotadas`), `widget/Mosaicos.kt` | N-07, H-NAV-1 |
| D.35 | El registro rápido dice el día en palabras; el ánimo se puede poner «antes» (ayer u otra hora) y el registro se mueve con `Recaidas.aOtraHora`. El selector de hora tiene la ficha «ayer» | `ui/QuickActivity.kt` (`Encabezado`, `crear`), `ui/Selectores.kt` (`SelectorHora(ayer)`) | N-08 |
| D.36 | La calidad del sueño del widget y de la hoja rápida se guarda en el **día del despertar** (fecha del calendario), como las horas del reloj, aunque se anote antes de las 05:00. El widget muestra la calidad de ese mismo día | `ui/QuickActivity.kt` (`SuenoRapido`), `widget/Widgets.kt` | N-08, H-NAV-7 |
| D.37 | «Ponerte al día»: si faltan días (hasta 7, sin valencia ni diario, desde el primer día guardado), Hoy dice cuáles. La hoja de la noche los recorre uno tras otro; al sellar pasa al siguiente. La noche tiene una sección nueva, «Suplementos de este día», para anotar o quitar la toma de ese día. «Diario de ayer» ya no se va a las 14 h: se queda hasta llenarlo o descartarlo | `ui/pantallas/Hoy.kt` (`Pendientes.faltantes`), `ui/pantallas/Dia.kt` (`PantallaNoche`), `ui/pantallas/Calendario.kt` (`Seleccion.ponerse`) | N-09 |
| D.38 | Aviso del día lógico en palabras («Son las 01:30: lo que anotes va al lunes…») en el registro rápido, en Notas, en la noche de hoy y en la semana corta | `ui/Componentes.kt` (`AvisoMadrugada`) | N-10 |
| D.39 | Notas: el formulario dice para qué día es y deja cambiarlo; el diálogo de editar cambia también el tipo, la fecha y el recordatorio | `ui/pantallas/Lote2.kt` (`PantallaNotas`) | N-11 |
| D.40 | La pestaña Hoy abre siempre en hoy (también al volver a la app después de un minuto). «Abrir el día» desde otras pantallas apila el Día y «atrás» regresa | `ui/MainActivity.kt`, `ui/pantallas/Calendario.kt` (`Seleccion.abrir`, `limpiarFechas`), `ui/pantallas/Dia.kt` (`PantallaDia(apilado)`) | N-12 |

**Efectos al leer los datos:**
- Desde esta versión hay más **tomas de suplementos y registros de ánimo anotados después**. Se distinguen con `ts_creado` frente a `ts` (`vm_retraso` en R, Apéndice C). Los niveles estimados de los suplementos se recalculan con las tomas corregidas; eso es lo esperado.
- Más días se llenan de memoria con «Ponerte al día». Quedan marcados como `retroactivo` (capítulo 3, sección 3.7.4).
- La calidad del sueño anotada entre las 00:00 y las 05:00 cambia de día: antes iba al día anterior y ahora va al del despertar. Los datos viejos no se mueven.

## Revisión de navegación · Entrega N3 (el Día como centro de corrección)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.41 | «Tu día en 24 horas», «Momentos de este día» y «Enfoque y siestas» son ahora una sola sección, «Tu día»: la línea de 24 horas y, debajo, todo lo que tiene hora ese día (momentos, consumos, tramos, gastos, ingresos, tomas y notas), cada fila corregible al tocarla. Plegada, dice cuántos registros tiene | `ui/pantallas/Dia.kt` (`TuDia`), `ui/Componentes.kt` (`Seccion(resumen)`) | N-17, N-26 |
| D.42 | «＋ agregar a este día…»: registro de ánimo con hora, gasto, nota y tomas de suplementos con la fecha de ese día ya puesta | `ui/pantallas/Dia.kt` (`TuDia`, `SuplementosDelDia`), `ui/QuickActivity.kt` (extra `FECHA`), `ui/pantallas/Gastos.kt` (`DialogoGasto(nuevo)`), `ui/pantallas/Lote2.kt` (`DialogoNota`, `Seleccion.notaPara`) | N-26 |
| D.43 | Lo que guardó un toque en el widget de ánimo o de sueño se ve arriba de la hoja («Guardado con el toque: 5/7») y se puede borrar o quitar | `ui/QuickActivity.kt` | N-15 |

**Efectos al leer los datos:** más registros con fecha pasada, agregados desde el Día. Llevan `ts_creado`, así que `vm_retraso()` los separa (H-NAV-9).

## Revisión de navegación · Entrega N4 (Hoy, la noche, Análisis y Vida)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.44 | Hoy, a cualquier hora, en el mismo orden: fecha y estado, la frase, «Tus últimos días», lo que falta (con lo de anoche, el experimento, «¿quién es?» y un contador de datos que faltan), «Llenar el día», el tablero con 4 cifras y «ver todo», y en la mañana el sueño si falta. Debajo, «Tu día», el sueño y «hace un año». Salen el personaje, la fila «cuestionarios · reacción · enfoque» y «pasar a la semana corta» (que pasa a Apariencia) | `ui/pantallas/Dia.kt` (`PantallaDia`), `ui/pantallas/Hoy.kt` (`Tablero(max)`, `DatosQueFaltan(compacto)`), `ui/pantallas/Sistema.kt` | N-16, P-10 |
| D.45 | «Tus últimos días»: 14 hilos con el color de la valencia, el alto del sueño frente a tu normal y un remate si el día está sellado. Hoy no se colorea hasta tener valencia y diario. Cada tema la dibuja a su manera. Se apaga en Apariencia (el ajuste del personaje) | `ui/pantallas/Hoy.kt` (`FranjaDias`) | N-28, P-10 |
| D.46 | **La hoja de la noche cambia de orden (`VERSION_NOCHE = 2`)**: «cómo estás ahora» (solo hoy), ánimo y cuerpo, predicción, contexto, hábitos y suplementos, diario, y lo opcional plegado. Los avisos del día se ven después de elegir la valencia; la activación usa una paleta secuencial | `ui/pantallas/Dia.kt` (`PantallaNoche`), `data/Modelo.kt` (`VERSION_NOCHE`, `cierrePermitido`), `ui/Componentes.kt` (`Escala(secuencial)`), `ui/pantallas/Lote2.kt` (`BloqueCierre`) | N-18, P-8, H-2-8, H-2-11 |
| D.47 | Análisis: arriba los experimentos y las relaciones más marcadas (5, con «ver las siguientes»); las herramientas en fichas; lo demás plegable. Las cifras van en una línea. Totales sale de Análisis | `ui/pantallas/Explorar.kt` | N-19 |
| D.48 | Vida: Campañas y «Dinero y sustancias» arriba; las galerías (tu año, logros, mapas, obras, otras vidas) juntas y plegadas | `ui/pantallas/Vida.kt` | N-20 |

**Efectos al leer los datos:**
- **`version_noche` pasa de 1 a 2** desde esta versión. Los análisis que crucen la fecha (sobre todo valencia del día contra hábitos, y el cierre) deben leerse por tramo.
- Ya no se crean cierres en días pasados.
- La pregunta del experimento sube al primer alto de Hoy: se contestará más (H-NAV-9).

## Revisión de navegación · Entrega N5 (gastos en dos o tres toques)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.49 | Subcategorías de gasto con **monto habitual**: el fijado a mano o, si no, el que más se repite en tus últimos 10 gastos de esa subcategoría (si empatan, el más reciente). Elegir la subcategoría llena el monto | `data/Dinero.kt` (`montoHabitual`), `ui/QuickActivity.kt`, `ui/pantallas/Gastos.kt` | N-29, P-7 |
| D.50 | **Frecuentes**: tus 6 subcategorías más usadas en los últimos 90 días (y las de monto fijo), con su monto. Un toque registra el gasto y muestra «registrado · cambiar monto · deshacer»; un segundo toque en menos de 2 s no lo repite. También en el widget de gasto | `data/Dinero.kt` (`frecuentes`, `repetido`, `registrarFrecuente`), `ui/QuickActivity.kt`, `widget/Widgets.kt` | N-29 |
| D.51 | La hoja rápida es «Gasto o ingreso», con el selector arriba; en ⊕ el botón se llama igual | `ui/QuickActivity.kt` (`GastoRapido`) | N-29, P-7 |
| D.52 | En Gastos: «Categorías y subcategorías» (agregar, renombrar con la opción de cambiar los gastos anteriores, ordenar, quitar y fijar el monto habitual), el mes por subcategoría y la subcategoría en el diálogo de corrección. Sale el editor de Más → Datos | `ui/pantallas/Gastos.kt` (`EditorCategorias`, `DialogoDinero`), `ui/pantallas/Sistema.kt`, `data/Repo.kt` (`gasto`) | N-29, E-24 |

**Efectos al leer los datos:**
- Los gastos de antes quedan sin subcategoría (`subcategoria` vacía en `vm_gastos.csv`).
- Registrar con un toque probablemente hará que se anoten **más gastos pequeños**: `gasto_total` puede subir desde esta versión sin que gastes más (H-NAV-9). El panel no cambia de fórmula (`gasto_total`, `gasto_imprevisto`).
- Renombrar una categoría con «cambiar también los gastos anteriores» reescribe esos gastos (quedan con `ts_editado`).

## Revisión de navegación · Entrega N6 (Más, buscador, nombres y pantallas huérfanas)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.53 | Más: un índice arriba («1 Registros · 2 Gente · 3 Medir · 4 Sistema») que abre y trae cada sección; las secciones se pliegan. Entran Gastos, Campañas, Experimentos, Lugares y «Días especiales y cumpleaños». El botón de Más mide 48 dp y dice «Más» | `ui/pantallas/Mas.kt`, `ui/Decoracion.kt` (`BotonAjustes`) | N-21 |
| D.54 | Buscador: pantallas que faltaban (Diario, Más, Lugares, cada herramienta de Análisis, Mundo, Patrimonio, Lista y pendientes, Nueva carta, Todos tus registros…) y **acciones** («corregir las horas de sueño» abre el Día con la corrección abierta, «anotar un suplemento de ayer», «ponerte al día»…). Busca por palabras sueltas, en cualquier orden y sin acentos, y muestra más de 8. Lo privado se encuentra por sus palabras de función, nunca por lo que escribiste. Las @personas y los #temas del resumen semanal llevan su texto al buscador | `ui/pantallas/Pantallas.kt` (`ACCIONES`, `buscar`), `ui/pantallas/Totales.kt`, `ui/pantallas/Semana.kt` | N-22, P-2 |
| D.55 | Un solo nombre para cada cosa: «Hoy» cuando el Día es hoy; «Llenar el día» en ⊕, el acceso directo, el widget («✎ diario» ahora abre la hoja) y el aviso de la noche; «Lista y pendientes»; cada herramienta con su nombre; «Cómo usar la app» y «Cómo leer tus datos». El Día y Privado tienen su «?». En los títulos de pantalla, el nombre del tema y el español van en la misma línea y del mismo tamaño, el del tema con más contraste | `ui/pantallas/Dia.kt`, `ui/Hoja.kt`, `ui/Estilo.kt` (`pantallaEnLinea`), `ui/QuickActivity.kt`, `res/values/strings.xml`, `widget/Widgets.kt`, `avisos/Avisos.kt`, `data/GuiaUso.kt` | N-23, P-1 |
| D.56 | Vuelve «Todos tus registros» (la lista editable de 30 días, con filtro por día), con un botón en Diario. Sale el código que ya no se usaba (`PantallaHall`, `RecuadroHall`, `CentroEntender`, `Portada`); la ruta `hall` sigue abriendo Logros. En Tu gente, «+ persona» sin mención previa | `ui/pantallas/Momentos.kt`, `ui/pantallas/Diarios.kt`, `ui/pantallas/Hall.kt`, `ui/pantallas/Gente.kt` | N-25, P-4 |

**Efectos al leer los datos:** ninguno en lo que se guarda. Una persona agregada a mano entra en «Tu gente» sin días hasta que la menciones.

## Revisión de navegación · Entrega N7 (corregir lo automático, papelera, gestos y accesibilidad)

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.57 | En la corrección del sueño, cada sesión del reloj tiene «no cuenta» / «sí cuenta»; la sincronización la respeta y un respaldo no la borra. Con la noche corregida a mano, las siestas se vuelven a contar | `ui/EditarSueno.kt`, `sensores/Salud.kt`, `data/Modelo.kt` (`claveFuenteSueno`), `data/Fusion.kt` | N-04, H-NAV-3 |
| D.58 | Lugares: renombrar, cambiar el radio (agrandarlo busca visitas de 30 días), ignorar para siempre y quitar decidiendo qué pasa con sus visitas. En «Tu día», cada visita se corrige («fue en…» o «no estuve aquí») | `ui/pantallas/Medir.kt` (`DialogoVisita`), `data/Repo.kt` (`borrarLugar(id, pasarA)`, `guardarVisita`, `borrarVisita`), `data/Lugares.kt` (`reasignar`, `ignorarLugar`), `ui/pantallas/Dia.kt` | N-05, H-NAV-4 |
| D.59 | La papelera tiene pantalla (Más → Datos → Papelera, y en el buscador); lo privado borrado solo se recupera en Privado | `ui/pantallas/Papelera.kt`, `data/Papelera.kt`, `ui/pantallas/Recaidas.kt` | N-14, P-9 |
| D.60 | Gestos con alternativa visible: «elegir» en la foto del día, «＋ lugar aquí» en el mapa, − / + / «todo» en el mapa para rascar, flechas y pausa en Tu año en historias, y una sección «Atajos, corregir y deshacer» en la guía | `ui/pantallas/Dia.kt`, `ui/pantallas/Mapa.kt`, `ui/pantallas/Mundo.kt`, `ui/pantallas/TuAnio.kt`, `data/GuiaUso.kt` | N-24, P-7 |
| D.61 | Accesibilidad: la cuadrícula de ánimo se describe y se mueve con acciones de TalkBack; lupa y modo con 48 dp de toque; «Volver» fijo para TalkBack; «guardar» también abajo en el escritor; en Tu año, el primer toque dice el día y su ánimo | `ui/Selectores.kt`, `ui/Decoracion.kt`, `ui/Hoja.kt`, `ui/Libros.kt`, `ui/pantallas/Anno.kt` | N-27, P-10 |

**Efectos al leer los datos:**
- `sueno_min` y `siesta_reloj_min` pueden cambiar en los días donde marques «no cuenta», y `siesta_reloj_min` deja de congelarse en las noches corregidas.
- Las variables de lugar (`en_casa_min`, minutos por tipo, `lugares_distintos`) cambian en los días cuyas visitas corrijas o reasignes.


## Entrega 10c · Cálculos (commit `b943ec7`)

Decisiones del autor (27 oct 2026): todo aprobado, con D-4 = sí (festivos e I-2 con alcance mínimo). No cambia lo que se guarda: `SCHEMA_V` sigue en 14. Cambian los números que se calculan con lo ya guardado, también en los días viejos.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.62 | Cada cuestionario cubre solo los días que pregunta: WHO-5 y PSS-4, los 7 anteriores a responder (incluido el día); UCLA-3, los 30. Si dos respuestas cubren un día, se queda la primera que lo cubre (la más cercana después) | `ciencia/Dinamicas.kt` (`porDiaCuestionario`, `ventanaCuestionario`) | H-2-1 (P-14). Antes, un PSS-4 semanal dejaba todo el mes con el último puntaje |
| D.63 | Una sola regla de «cero» y «sin dato» (`ceroDesde`): 0 desde el primer registro de la fuente y en los días con diario; antes, vacío. Aplica a `gasto_total`, `gasto_imprevisto`, `n_impulso`, `escalada` y `gimnasio`. Además: `hab_<id>` vacío antes de que existiera el hábito (salvo marcado); `sueno_recordado` vacío si ese día no dijiste nada del sueño; las variables sociales (`personas`, `vi_*`, `cercanos`, `cercania_media`, `personas_*`, `penso_en_*`) y la base de `per_` y `pen_`, vacías los días sin diario escrito ni momento que nombre a alguien (en `tema_`, que tenga un #tema); `ejercicio_min` en 0 los días con datos del reloj y sin sesión; `distancia_km` en 0 los días con ubicaciones y sin movimiento; `probioticos_estimado` vacío sin bitácora de MacroFactor; «las tres» necesidades cuenta en autonomía, competencia y relación; `presencias_revisado` entra al panel (obvia con lo social) | `ciencia/Estadistica.kt` (`ceroDesde`, `panel`, `Movilidad.kmPorDia`), `ciencia/Dinamicas.kt` (`diasSociales`), `data/Lote3.kt`, `data/Exportar.kt` | H-1-1, H-1-6, H-2-4, H-4-8, H-6-13, H-9-16 (P-15). Los `ctx_*` siguen en 0 cuando no se marcan: es la convención de la pantalla |
| D.64 | `lugares_distintos` por día lógico. El LEEME y el diccionario dicen qué variables van por día de calendario: corazón, oxígeno, calorías activas, temperatura de la piel, respiración y peso del reloj, agenda, clima, luna, ciudades y países, y la pantalla y los pasos de antes de la v13 | `ciencia/Estadistica.kt`, `data/Exportar.kt` (`leeme`), `data/Diccionario.kt` (`CALENDARIO`) | H-1-2, H-3-1 (en parte), H-9-4 (P-16 i). La regla de +24 de `ultimo_uso_h` en R queda para 10d |
| D.65 | La t de Student exacta (`cuantilT`, `pT`, con la beta incompleta) y la autocorrelación de orden 1 de una serie con fechas (`ar1`). `diferencia()` usa t de Welch y multiplica el error estándar de cada grupo por √((1 + ρ)/(1 − ρ)), con ρ corregido por su sesgo en series cortas, ρ + (1 + 4ρ)/n, y acotado entre 0 y 0.9. Lo usan la Tendencia (30 contra 30 días), «Antes y después», los experimentos y las variables 0/1 de «Todo lo que se mueve» | `ciencia/Estadistica.kt` (`diferenciaCompleta`, `diferenciaEnDias`, `ar1Grupos`) | H-5-5, H-7-3 (P-17) |
| D.66 | «Todo lo que se mueve con algo» usa los mismos restos sin tendencia que la lista (con su interruptor, que llega desde la lista) y el p con el n efectivo; muestra r de Pearson, no ρ. «Cruzar dos variables» da el intervalo con el n efectivo y la r sin tendencia junto a la bruta. El modelo múltiple usa t con n − k − 1 grados y muestra el R² ajustado; su interruptor dice «Controlar por el valor del día anterior». «Día de la semana» pide 5 días por día de la semana y usa t con n − 1 | `ciencia/Analisis.kt` (`factores`, `modelo`, `porDiaSemana`, `cambio`), `ciencia/Estadistica.kt` (`sinTendencia`, `correlacionEf`), `ui/pantallas/Analisis.kt`, `ui/pantallas/Explorar.kt` | H-5-3, H-5-4, H-5-11 (P-17, P-28). `vm_factores` en R queda para 10d |
| D.67 | Experimentos: no se sella si con el lavado podrían quedar menos de 7 días en una condición (se prueban todos los sorteos posibles); la plantilla «Creatina» pasa a 12 semanas (14 días con la acción y 42 sin ella); el sorteo es por pares de semanas; la tendencia se mide con valor ~ día + condición; el aviso de diseño en bloque depende del plan real; mientras dura el plan el resultado dice «provisional» y no habla de señal; hoy no entra; «aún no hay suficientes días» distingue «falta esperar», «este plan nunca dará resultado» y «terminó sin suficientes»; el rótulo dice «Semanas con la acción según el plan»; el aviso del Día no sale antes del inicio; el panel de la pantalla se arma en segundo plano | `data/Lote3.kt` (`Experimentos.plan`, `diasPorCondicion`, `minimoPosible`, `enBloque`, `tendencia`, `resultado`), `ui/pantallas/Lote3.kt`, `ui/pantallas/Dia.kt` | H-7-1, H-7-4, H-7-5, H-7-6, H-7-8, H-7-9, H-7-13, H-9-30, H-9-31, H-9-32 (P-18, P-21). Los experimentos ya sellados conservan su plan |
| D.68 | «Antes y después»: los cambios de un mismo guardado van en una sola tarjeta, y el primer llenado de «Conocerte» no sale | `ui/pantallas/Explorar.kt` (`EtapasDeVida`) | H-7-12 (P-18), H-2-5 |
| D.69 | El ritmo de sueño usa las noches de los últimos 56 días (`vm_ritmo(dias = 56)` en R). Una sola regla de día libre, `Fechas.esDiaLibre`: sábado, domingo o, si en «Conocerte» vives en México, un festivo de la Ley Federal del Trabajo (art. 74), con sus reglas anteriores a 2006 y a 2024. El panel exportado gana `festivo` y `dia_libre`, y R lee esa columna. Con 1 a 6 noches del reloj, el aviso dice que se mezclan con las del teléfono | `ciencia/Analisis.kt` (`ritmo`, `ritmoDe`, `recientes`), `data/Fechas.kt` (`esFestivoMx`, `esDiaLibre`), `data/Exportar.kt`, `data/Diccionario.kt`, `ui/pantallas/Analisis.kt`, `vestigium.R` | H-4-9, H-4-10, H-9-15 (P-19), I-1 |
| D.70 | El alcohol: el peso es el más reciente entre la báscula y la tendencia de MacroFactor; antes del primero, el primero posterior; 70 kg solo si no hay ninguno. Con un peso real, sexo, estatura (100 a 250 cm) y, en hombres, edad, la r sale del agua corporal de Watson et al. (1980), r = agua / (0.80 · peso), acotada entre 0.45 y 0.95; si no, la r fija por sexo. Las tomas de MacroFactor sin hora legible van al mediodía. `alcohol` (0/1) sigue el día lógico de cada dosis (los episodios sin cantidad cuentan su propio día) | `data/Sustancias.kt` (`pesos`, `pesoDe`, `watsonR`, `rYPeso`, `msComida`), `ciencia/Estadistica.kt` | H-4-4 (en parte), H-9-13, H-9-17, H-9-24 (P-20), I-2 |
| D.71 | Las mismas cifras en todas las pantallas: la clave de la caché de dosis cambia con cada importación de MacroFactor, y en el hilo principal sin esa caché lo que depende de MacroFactor queda vacío | `ciencia/Estadistica.kt` (`sustancias`) | H-4-3, H-9-14 (P-21) |
| D.72 | Una sola 1RM estimada (`Macrofactor.e1rm`: Epley, de 1 a 12 repeticiones, sin calentamientos) en el reporte y en Tus apps. «Tus números»: el ejercicio semanal es la suma de las últimas 4 semanas con sesiones entre 4. Los días atípicos revisan `distancia_km` | `ciencia/Periodo.kt`, `ui/pantallas/TusApps.kt`, `ciencia/TusNumeros.kt`, `ciencia/Analisis.kt` (`atipicos`) | H-4-13 (P-22), H-5-9 (P-23), H-5-8 (P-24) |
| D.73 | Personas y red: `Texto.canon` sigue las uniones encadenadas hasta 8 pasos y une los ciclos (al primero en orden alfabético); `Perfiles.relacion`, `vi_convivientes`, los vínculos dichos y la cuenta de la Constelación en Vida usan el nombre unido; la Red colorea por grupo de relación y `vm_red_nodos.csv` gana `grupo` (R lo usa para la composición y el color); con «solo lo que dije», el grado y los grupos se recalculan; `per_` es obvia con todas las variables sociales; la cercanía se puede borrar, y pasar de «sin cercanía» a un valor (o al revés) queda en el historial; en los sueños, solo lo que lleva @ cuenta como persona | `data/Texto.kt`, `data/Perfiles.kt`, `data/Personas.kt`, `data/Lote3.kt` (`RedPersonas.dichos`, `grados`, `componentes`), `ui/pantallas/Lote3.kt`, `ciencia/Dinamicas.kt`, `ciencia/Estadistica.kt`, `ui/pantallas/Vida.kt`, `data/Exportar.kt`, `vestigium.R` | H-6-4, H-6-5, H-6-6, H-6-9, H-6-10, H-6-11, H-6-12, H-9-21, H-9-22 (P-25) |
| D.74 | Estado subjetivo: el panel ignora los «Cómo estás ahora» guardados otro día que el que cierran, también los viejos; el primer guardado de «Conocerte» deja historial y antes de esa fecha sus campos no dan etapa; un toque a menos de 100 ms del estímulo cuenta como anticipación y se repite; el ánimo por lugar usa el mismo criterio en la lista y en el mapa (lugar marcado o dentro del radio) y el texto ya no dice «últimos 60 días» de las medias; se corrigen los comentarios de la brújula política, de la separación de avisos y del ángulo de la Constelación, y el aviso de «escrito por adelantado» | `ciencia/Estadistica.kt` (`cierreFuera`), `ui/pantallas/Conocer.kt`, `data/Perfiles.kt` (`primerGuardado`), `ciencia/Dinamicas.kt`, `ui/pantallas/Medir.kt` (`enLugar`), `data/Politica.kt`, `avisos/Trabajo.kt`, `ui/pantallas/Constelacion.kt`, `ui/pantallas/Dia.kt` | H-2-5, H-2-6, H-2-7, H-2-8, H-2-12, H-2-14, H-2-15, H-6-14 (P-26) |
| D.75 | Las rachas sin recaída cuentan solo los días respondidos «no»; un día sin respuesta no suma ni reinicia, y se dice cuántos hubo desde la última | `data/Recaidas.kt` (`rachas`), `ui/pantallas/Recaidas.kt` | H-8-8 (P-27) |
| D.76 | Diccionario sin claves repetidas: las que chocaban pasan a «archivo:columna» (`antes`, `inicio`, `fin`, `min`, `dia_logico`, `categoria`, `minutos`, `duracion_s`, `grado`, `cantidad`); se describen las columnas de `vm_experimentos.csv`; `distancia_km` se separa en GPS (panel) y Health Connect (`vm_salud.csv`); `n_momentos` aclara que incluye lo privado. El aviso de potencia de Análisis distingue una pareja de la lista completa, y la etiqueta de `per_` menciona el mejor y el peor momento | `data/Diccionario.kt`, `ui/pantallas/Explorar.kt`, `ciencia/Dinamicas.kt` | H-5-16, H-6-2, H-9-8, H-9-19, H-9-20, H-9-25 (P-28) |
| D.77 | Reportes y Totales: el ánimo medio es la valencia del diario (como `valencia` en el panel y en R) y el reporte dice cuántos días tenían solo avisos; la hora de dormir usa la misma regla que el panel (`Exportar.horaDecimal`, corte a las 12); gastos e ingresos se cuentan por el día del diario | `ciencia/Periodo.kt`, `ciencia/Totales.kt`, `reportes/Reportes.kt` | H-5-15, H-8-10 (P-29) |
| D.78 | Pruebas JVM de cada regla: cuestionarios, `ceroDesde`, cuantiles de t, Welch, cobertura simulada (2000 réplicas, ρ = 0.5: entre 93 y 97 %), plan de «Creatina», sorteo por pares, hoy fuera del resultado, tendencia dentro de condición, ritmo reciente, festivos, peso, Watson, 1RM, atípicos, `canon`, red filtrada, cierres de otro día, rachas y diccionario | `app/src/test/.../CalculosPrueba.kt` | Que cada regla falle en una prueba si se rompe |

**Lo que no se cambió:** la corrección de Hedges para el d de Cohen (H-7-7) no cumple el criterio: se documenta en el capítulo 8. Las partes de R de P-16, P-17 y P-30 a P-33 son de la entrega 10d.

**Efectos que conviene saber al leer los datos:**
- **Más vacíos y menos ceros.** Muchas variables quedan vacías donde antes valían 0: gasto e impulsos antes del primer registro, hábitos antes de existir, `sueno_recordado` en días sin nada del sueño, lo social en días sin texto. Cambian medias, frecuencias y correlaciones en la dirección correcta, y la cobertura por variable baja.
- **Intervalos más anchos.** La Tendencia, los experimentos, «Antes y después», «Todo lo que se mueve», «Cruzar dos variables» y el modelo múltiple dan intervalos más anchos y p más grandes: algunas relaciones dejan de verse como «claras».
- **Cuestionarios.** Los `esc_*` del panel extendido cambian en todos los días ya respondidos, y las semanas sin respuesta quedan vacías.
- **Alcohol.** Con pesos registrados, `alcohol_estimado_max` y `alcohol_al_dormir` cambian (con Watson, normalmente bajan en hombres jóvenes y altos y suben en personas de más peso y edad). `cafeina_total_mg` y `alcohol_bebidas` pueden subir: ya no se pierden las tomas sin hora.
- **Personas.** Alguien partido en dos nombres por una unión encadenada se une; cambian algunas columnas `per_`.
- **Experimentos.** Los ya sellados no cambian de plan, pero su resultado se recalcula con las reglas nuevas.

**Lo que no se pudo comprobar en la sesión:**
- El valor 0.80 de la proporción de agua en la sangre de Watson et al. (1981): se comprobó la definición r = agua / (P · peso) en Searle (2015), pero no el valor de P en el texto original. Queda como constante (`Sustancias.AGUA_SANGRE`) con su nota.
- La forma exacta del sesgo de la autocorrelación en Marriott y Pope (1954): se comprobó la referencia, no el texto. La corrección se validó por simulación (ver D.78).
