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

