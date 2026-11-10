# Revisión general de la app (noviembre de 2026)

Diagnóstico pedido por el autor tras las primeras semanas de uso real. **No cambia código**: cada hallazgo trae una propuesta para decidir, y lo aprobado se aplica en otra sesión. No contiene datos personales; los ejemplos son inventados.

**Cómo se hizo.** Cinco lecturas en paralelo: navegación, redundancias, diseño, robustez y una auditoría de una exportación real contra sus reportes. De la auditoría aquí solo queda el mecanismo de cada problema, nunca las cifras. Las rutas son relativas a `app/src/main/java/io/github/ruddy2003/vestigium/`.

**Orden.** Los 34 hallazgos van de mayor a menor impacto; los diez primeros son el resumen de abajo. Cada uno dice su tema (datos, cálculo, navegación, diseño, reportes, redundancia, lentitud o exportación). Al final hay tres «menores y por vigilar», lo que está bien resuelto y lo que no se pudo comprobar.

**Cómo leer cada hallazgo.** *Dónde* (pantalla y archivo) · *Qué pasa* · *Por qué importa* · *Propuesta* · *Esfuerzo* (bajo: una tarde; medio: uno o dos días; alto: una semana o más) · *Congelación* (si rompe o no la regla de 2027: no cambiar qué se guarda ni cómo se calcula lo guardado).

**Qué significa «cambia lo guardado».** Es una corrección de error, permitida por la regla, pero pide subir `SCHEMA_V`, anotar la columna en `Diccionario.kt` (`DESDE`) y una fila en el Apéndice D. Conviene hacerlas **antes del 1 de enero de 2027**, para que 2027 empiece limpio. Los hallazgos metodológicos están además en `docs/paper/hallazgos.md` (H-22-1 a H-22-12).

**Lo ya resuelto** en la revisión de uso del 10 de noviembre (Apéndice D.171 a D.179) no se repite aquí: el cierre de la hoja de la noche, el regreso a «¿Cómo estás ahora?», la lista completa de personas, los días por persona en los reportes, el libro por meses del calendario, los promedios «al día», la comparación con el mes anterior y el desplazamiento de Diario y Vida.

---

## Los 10 cambios que más mejorarían la app

| # | Cambio | Por qué primero | Esfuerzo | Congelación |
|---|---|---|---|---|
| 1 | ~~Que el ánimo del cierre de la noche guarde **un** registro, no uno por toque~~ (hecho, D.177) | Hoy infla los conteos de registros de ánimo en el panel y en los reportes | Bajo | Cambia lo guardado (corrección) |
| 2 | Que una relectura vacía del reloj o del teléfono **no borre** datos buenos | Pérdida silenciosa justo al cambiar de teléfono | Bajo-medio | No rompe |
| 3 | ~~Declarar en qué zona horaria vivías **antes de instalar la app**~~ (decidido: no se cambia) | Todo lo importado de esa época está en la hora equivocada | Medio-alto | Cambia lo guardado (corrección) |
| 4 | Rescate ante una base dañada o un documento ilegible | Hoy Android borra el archivo y la app arranca vacía | Bajo-medio | No rompe |
| 5 | Una sola definición del «ánimo de la semana» | La misma semana da números distintos en Hoy, Vida, el widget y el reporte | Bajo | No rompe |
| 6 | Que todos los resúmenes (y los días por persona) salgan de la misma función | El libro, la pantalla y el reporte del mismo mes no cuadran entre sí | Medio | No rompe |
| 7 | Registrar el ánimo con un toque menos y «Listo» siempre a la vista | Es lo que más veces se hace al día | Medio | No rompe |
| 8 | Una sola manera de guardar en la hoja rápida, y que «sellar ↓» selle | Quita toques diarios y la duda de «¿ya se guardó?» | Bajo | No rompe |
| 9 | Que el respaldo que falla avise siempre y los reportes automáticos no se pierdan | El fallo que hoy no notifica es el más caro | Bajo | No rompe |
| 10 | El español siempre visible en las pestañas y en la tarjeta principal de Hoy | Con la rotación diaria de temas, los rótulos de entrada cambian cada día | Bajo | No rompe |

Le siguen de cerca los nombres de calendario inexactos (11) y las zonas de toque y el contraste (12).

**Toques por tarea** (contando el de cerrar; hoy → posible): sueño de la mañana 2 → 2 · cierre de la noche desde el aviso 6 → 5 · ánimo dentro de la app 4 → 3 · impulso 3 → 3 · gasto frecuente 4 → 3 · gasto nuevo 5–6 → 4–5 · diario 2–3 · nota 4 → 3 · sustancia 5 → 3 · ficha de una persona 4–5 → 3.

**Capturas.** La parte de diseño se apoya en unas 35 capturas de la rama `capturas` y en el contraste calculado sobre los esquemas de `ui/Disenios.kt`. Para esta revisión se generaron además, por tema y en claro y oscuro, la hoja rápida, la barra de pestañas, los controles, los componentes, el diálogo y las gráficas (ejecución 258 de «Capturas de diseño»): con ellas se confirmaron a la vista los hallazgos 10 (pestañas solo en la lengua del tema) y 12 (pestañas no elegidas con poco contraste). Siguen sin captura Hoy y Llenar el día por tema, el formulario «Ánimo ahora» y la app con letra grande del sistema; lo que depende de ellas se dedujo del código.

---

## Hallazgos, por impacto

### 1. El ánimo del cierre de la noche guarda un registro por cada toque
- **Estado:** **resuelto** el 10 de noviembre (esquema 23, Apéndice D.177): cada toque corrige el mismo registro y de cada noche quedó solo el más reciente; los demás fueron a la papelera.
- **Tema:** datos.
- **Dónde:** Llenar el día → «Cómo estás ahora» (`ui/pantallas/Lote2.kt`, `BloqueCierre`); la cuadrícula está en `ui/Selectores.kt` (`CuadriculaAfecto`).
- **Qué pasa:** la cuadrícula escucha los toques con `pointerInput(Unit)`, que se queda con la **primera** función que recibió. En esa primera función, «el registro previo de esta noche» todavía era «ninguno». Así que cada toque crea un momento nuevo con `origen = "cierre"` en lugar de corregir el anterior. Ejemplo inventado: tocas tres casillas hasta dar con la tuya y quedan tres registros con segundos de diferencia.
- **Por qué importa:** infla `registros_animo`, `n_momentos` y las filas de `vm_horas`, y con ellos «ánimo N / M» de los reportes y los «registros de ánimo» del de terapia. El valor del cierre que usa el panel (`animo_cierre_*`) es el último, así que ese sí queda bien.
- **Propuesta:** `rememberUpdatedState(onCambio)` dentro de `CuadriculaAfecto` (arregla todos sus usos) y buscar el registro previo dentro de la función. Aparte, decidir si se limpian los sobrantes ya guardados: propongo una acción en «Estado de los datos» que deje el último de cada noche y mande los demás a la papelera, mostrando antes cuántos son.
- **Esfuerzo:** bajo.
- **Congelación:** cambia lo guardado (corrección): `SCHEMA_V`, `DESDE` en `registros_animo` y `n_momentos`, Apéndice D. Anotado como H-22-1.

### 2. Una relectura vacía del sensor pisa datos buenos
- **Tema:** datos.
- **Dónde:** Ajustes → Sistema → «Sincronizar todo ahora» y al conceder Health Connect (`ui/pantallas/Sistema.kt`); `sensores/Salud.kt` (`sincronizar`), `sensores/Pantalla.kt`, `data/Fusion.kt`.
- **Qué pasa:** al releer un día, pasos, frecuencia cardiaca y sueño se sustituyen por lo que devuelva la fuente, aunque sea «nada»; la pantalla sin eventos se guarda como 0 minutos. En un teléfono nuevo, tras restaurar el respaldo, Health Connect y el uso de apps suelen llegar vacíos: la sincronización de 30 días deja en blanco días que venían completos. Además, la sincronización lee el día al empezar y lo guarda al final, así que una corrección manual del sueño hecha entretanto se pierde. Y al fusionar un respaldo «manda el respaldo» aunque sea más viejo que lo que hay.
- **Por qué importa:** es una pérdida silenciosa en el momento más delicado (cambiar de teléfono). El freno que evita respaldar una copia empobrecida solo mira días y momentos.
- **Propuesta:** no cambiar un valor por nulo o cero si la fuente vino vacía (ya se hace con `fc_reposo`, `spo2` y `hrv`); borrar el sueño solo si la fuente devolvió sesiones y ninguna sirve; un `Repo.cambiarSalud(fecha) { … }` sincronizado; en la fusión, que gane el `ts` más reciente.
- **Esfuerzo:** bajo-medio.
- **Congelación:** no rompe (no cambia campos ni fórmulas). Anotado como H-22-3.

### 3. Lo importado de antes de instalar la app está en la zona horaria de hoy
- **Estado:** **decidido el 10 de noviembre: no se cambia.** El autor prefiere dejar los datos importados como están. Queda como advertencia para el análisis (H-22-2).
- **Tema:** datos.
- **Dónde:** `data/Repo.kt` (el historial de zonas empieza con una sola entrada desde 1970), `data/Fechas.kt` (`zonaEn`), `data/Importaciones.kt`.
- **Qué pasa:** la app sabe en qué zona estás desde que se instaló, pero para todo lo anterior supone la zona del teléfono el día de la instalación. Si entonces vivías en otro huso, el historial importado (ubicaciones, YouTube, actividad de Google, fotos, huellas) queda corrido varias horas. Ejemplo inventado: con siete horas de diferencia, un video visto a las 21:00 queda como visto a las 14:00, o a las 4:00 del día siguiente, según hacia dónde sea el desfase.
- **Por qué importa:** «videos de madrugada», la primera y la última actividad del día y el día lógico de cada huella salen mal para años enteros. Lo registrado por el teléfono desde la instalación está bien.
- **Propuesta:** una pantalla «¿Dónde vivías?» en Ajustes para declarar periodos pasados (desde, hasta, zona), con una sugerencia sacada de las ubicaciones importadas; al guardar, recalcular `fecha` de lo importado. Mientras no se declare, no mostrar frases que dependan de la hora en esos periodos.
- **Esfuerzo:** medio-alto.
- **Congelación:** cambia lo guardado (corrección): `SCHEMA_V` y Apéndice D. Anotado como H-22-2.

### 4. Una base dañada se borra sola, y un documento ilegible se pisa
- **Tema:** datos.
- **Dónde:** `data/Db.kt` (sin `DatabaseErrorHandler`), `App.kt` (`Repo.init` sin protección), `data/Repo.kt` (lectura de `dias`, `salud`, `notifs`).
- **Qué pasa:** ante un error de corrupción, Android usa su manejador por defecto, que **elimina** el archivo; la app arranca vacía y con ajustes nuevos. Si `Repo.init` falla por otra causa, la app se cierra al abrir y tampoco deja exportar. Por otro lado, si un día guardado no se puede leer, se muestra vacío y la siguiente escritura lo reemplaza; solo los ajustes guardan copia de lo ilegible.
- **Por qué importa:** una base recuperable casi entera se pierde junto con todo lo posterior al último respaldo.
- **Propuesta:** un manejador que renombre el archivo dañado (no lo borre) y deje una marca; una pantalla de rescate si el arranque falla, que copie la base tal cual a una carpeta; al fallar una lectura, copiar el texto crudo a una colección `ilegible_<colección>` que el respaldo ya recoge.
- **Esfuerzo:** bajo-medio.
- **Congelación:** no rompe.

### 5. El «ánimo de la semana» tiene cuatro fórmulas
- **Tema:** cálculo.
- **Dónde:** Hoy, Vida, Semana, Calendario, «El año», el widget y los reportes (`ciencia/Periodo.kt` `valDe`; `ui/pantallas/Semana.kt`, `Vida.kt`, `Hoy.kt`, `Anno.kt`, `Calendario.kt`; `widget/Widgets.kt`).
- **Qué pasa:** los reportes usan solo la valencia del diario. Las pantallas rellenan los días sin valencia con la media de los registros de ánimo de ese día. «Esta semana» de Hoy son 7 días móviles; las demás van de lunes a domingo.
- **Por qué importa:** la misma semana muestra números distintos según dónde la mires, y el relleno mezcla dos instrumentos que el paper pide no mezclar (cap. 3, 3.3.4).
- **Propuesta:** una función `Periodo.animoDia(fecha)` para todos; el relleno con avisos, si se quiere conservar, como una capa aparte y rotulada («avisos»).
- **Esfuerzo:** bajo.
- **Congelación:** no rompe (son vistas). Anotado como H-22-4.

### 6. Los resúmenes y los días por persona no pasan por la misma función
- **Tema:** cálculo.
- **Dónde:** `ciencia/Periodo.kt` (reportes) frente a `ui/pantallas/Semana.kt`, `Anno.kt`, `Calendario.kt`, `ui/LibroDiario.kt` (`resumenMes`), `ciencia/Totales.kt`, `widget/Widgets.kt`.
- **Qué pasa:** «días con diario» es nota o valencia en un sitio, solo nota en otro y cualquier día guardado en otros dos. El sueño medio descarta las noches de menos de 120 minutos solo en `Periodo`. Los pasos medios cuentan o no los días en cero. El gasto va por día lógico en unos y por fecha de calendario en otros.
- **Por qué importa:** el resumen del libro, el de la pantalla y el del reporte del mismo mes no cuadran entre sí, y no hay manera de saber cuál «es el bueno».
- **Lo mismo con las personas:** `Personas.diasCon` (la que usan los reportes desde el 10 de noviembre) convive con ocho copias (`ui/pantallas/Constelacion.kt`, la Red en `data/Lote3.kt`, `Anno.kt`, `ui/LibroDiario.kt`, `Semana.kt`, `Totales.kt`, `ciencia/TusNumeros.kt`, `ciencia/Estadistica.kt`). Cada una mira fuentes distintas (solo la nota; nota y momentos; todo) y una no aplica las equivalencias de nombres: la misma persona tiene un número distinto en cada pantalla.
- **Propuesta:** `Periodo.calcular` y `Personas.diasCon` como únicas fuentes; las pantallas les piden las cifras que muestran.
- **Esfuerzo:** medio.
- **Congelación:** no rompe en pantallas ni reportes. La Red se exporta: si cambia su cuenta, anotarlo en `DESDE` y en el Apéndice D. Anotado como H-22-5.

### 7. El ánimo cuesta un toque de más y «Listo» queda al fondo
- **Tema:** navegación.
- **Dónde:** botón ⊕ → «Ánimo ahora» (`ui/QuickActivity.kt`: `MenuQuick`, `Animo`, `HojaQuick`).
- **Qué pasa:** ⊕ abre un menú y el ánimo es otro toque; el atajo (toque largo en ⊕) no se ve en ningún lado. «Listo» va al final de una columna larga y la hoja no tiene tope de alto: hay que desplazar para cerrar.
- **Por qué importa:** es el registro más frecuente del día.
- **Propuesta:** que ⊕ abra con la cuadrícula del ánimo arriba y el menú debajo; «Listo» fijo al pie de la hoja.
- **Esfuerzo:** medio.
- **Congelación:** no rompe (mismo registro y mismo origen).

### 8. Tres maneras de guardar, «atrás» pierde sin avisar y «sellar ↓» no sella
- **Tema:** navegación.
- **Dónde:** hoja rápida ⊕ (`ui/QuickActivity.kt`: `terminar`, `tocarFuera`, `GastoRapido`, `ConsumoRapido`), Notas (`ui/pantallas/Lote2.kt`), Llenar el día.
- **Qué pasa:** ánimo, sueño e impulso se guardan al tocar y se cierran con «Listo»; gasto y sustancia piden «Registrar» o «Guardar». Tocar fuera avisa solo si hay un monto escrito; «atrás» nunca avisa. Una nota a medio escribir se pierde con «atrás». En la hoja de la noche conviven «se guarda solo mientras escribes» y un botón «Guardar y volver»; y con el día completo, el botón «sellar ↓» de la barra fija (`ui/pantallas/Dia.kt`, `BarraNoche`) solo desplaza hasta «Sellar el día», que está después de las secciones opcionales: hay que tocar otra vez.
- **Por qué importa:** no saber si algo se guardó hace repetir registros (ver hallazgo 21) o perderlos, y cada noche termina con un toque y un desplazamiento de más.
- **Propuesta:** una regla: «Listo» donde ya se guardó, «Guardar» solo donde hace falta; un manejo común de «atrás» (de un submenú al menú; con algo sin guardar, el mismo aviso que al tocar fuera); el borrador de Notas en `rememberSaveable`; con el día completo, el botón de la barra dice «Sellar el día» y sella.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 9. El respaldo que falla puede no avisar, y los reportes automáticos se pierden si ese día algo falla
- **Tema:** datos.
- **Dónde:** `avisos/Trabajo.kt` (pasos de la tarea de fondo).
- **Qué pasa:** cada paso se intenta como mucho dos veces al día. El aviso «el respaldo falló» vive dentro del mismo paso: si el paso se corta dos veces (por tiempo o porque el sistema mata el proceso), ya no corre y el aviso nunca suena. Los errores se tragan sin dejar rastro. Los reportes semanal, mensual y «Tu año» se marcan como hechos antes de hacerse y solo se intentan en su día exacto: si fallan o el teléfono estuvo apagado, no salen.
- **Por qué importa:** con los años el respaldo tarda más, y este es justo el fallo silencioso.
- **Propuesta:** comprobar la antigüedad del último respaldo fuera del paso; una bitácora corta (paso, error, hora) visible en «Estado de los datos»; reportes «ese día o el primero posterior», marcados al terminar.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 10. Rótulos sin español en lo principal
- **Tema:** diseño.
- **Dónde:** barra de pestañas (`ui/MainActivity.kt`, `ui/Textos.kt`, `ui/Rotacion.kt`); tarjeta principal de Hoy (`ui/pantallas/Hoy.kt`, que toma solo la primera parte de `Estilo.seccion`).
- **Qué pasa:** las pestañas cambian de nombre e ícono con el tema; con la rotación diaria cambian cada día y solo la posición es estable. «Te falta hoy» y «Llenar el día» salen solo en la lengua del tema (árabe, latín, griego), sin la traducción pequeña que sí llevan las demás secciones.
- **Por qué importa:** son los dos puntos de entrada de todos los días.
- **Propuesta:** el español pequeño debajo (o un interruptor «pestañas en español»); en Hoy, usar el mismo comentario que usa `Seccion`; ampliar la prueba de legibilidad a esa tarjeta.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 11. Nombres que afirman un calendario que el dato no usa
- **Estado:** **resuelto en los reportes** el 10 de noviembre (Apéndice D.179): el encabezado y el pie dicen la fecha común y, debajo, la del tema. Sigue pendiente en las pantallas y el widget.
- **Tema:** diseño.
- **Dónde:** `ui/Estilo.kt` (`mes`, `fecha`), `ui/nombres/NombresTemplario.kt`, `ui/Temas.kt`; se ve en Diario, libreta de momentos, bibliotecas, el reporte mensual y el widget.
- **Qué pasa:** en Templario un mes del calendario común se rotula «<mes> juliano» y el conteo dice «este mes juliano», aunque los días contados son los del mes gregoriano; el encabezado y el pie de un mismo reporte pueden dar meses distintos. Revolución, Egipto y Neón hacen algo parecido con sus nombres de mes o con numeraciones inventadas que parecen dato. Solo el Calendario añade el mes común al lado.
- **Por qué importa:** en una pantalla es adorno; en un reporte que se guarda o se comparte, es una fecha falsa.
- **Propuesta:** un ayudante «mes del tema · mes común» para esos cinco usos; en los reportes y el PDF, el mes común siempre en grande (como ya hace el libro del diario); quitar «juliano» de los rótulos de conteo.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe. Anotado como H-22-11.

### 12. Zonas de toque pequeñas y poco contraste en lo que se toca
- **Tema:** diseño.
- **Dónde:** `ui/pantallas/Hoy.kt` (invitaciones, «hoy no»), `ui/pantallas/Dia.kt` (filas de «Tu día»), `ui/Componentes.kt` (cabecera plegable de `Seccion`, quitar etiqueta, fichas, casillas de `Escala`, botones secundarios), `ui/MainActivity.kt` (pestañas no elegidas), `ui/Disenios.kt` (esquemas de color).
- **Qué pasa:** las invitaciones miden unos 28 dp de alto; «hoy no», que las descarta todas, queda a 14 dp de la última y atenuado; las filas de «Tu día» miden unos 32 dp y van quince seguidas; la cabecera plegable, entre 22 y 29 dp (la guía de Android pide 48). Además, la pestaña no elegida usa el color de acento al 72 % y letra de 11 sp que puede bajar a 8: queda por debajo de 3:1 de contraste en ocho esquemas, varios de ellos oscuros. La superficie y el fondo casi no se distinguen (de 1.05 a 1.21) y el borde de 1 dp tampoco (de 1.4 a 2.2) en los 58 esquemas, así que fichas y casillas dependen de un borde que apenas se ve. El acento usado como texto baja de 4.5:1 en cuatro esquemas.
- **Por qué importa:** fallar el toque o descartar todo sin querer; y lo que se toca debe verse: con poca luz o al sol, desaparece.
- **Propuesta:** `heightIn(min = 48.dp)` en esas piezas; «hoy no» pasa a «ocultar por hoy», separado de la lista; `onSurfaceVariant` y un mínimo de 10 sp en las pestañas; un tono «contorno de control» de 3:1 o más para lo interactivo; una prueba automática de contraste sobre todos los esquemas.
- **Esfuerzo:** bajo (toque y pestañas) a medio (contornos).
- **Congelación:** no rompe.

### 13. Reimportar borra por rango de fechas, no por lo que trae el archivo
- **Tema:** datos.
- **Dónde:** Importar MacroFactor y Spotify (`data/Macrofactor.kt`, `data/Importaciones.kt`, `data/Repo.kt` `importarMacrofactor`, `data/Importar.kt`).
- **Qué pasa:** al importar se borra todo entre la primera y la última fecha del archivo. Un archivo de MacroFactor que solo trae nutrición borra las series de gimnasio de ese rango, y al revés. En Spotify, elegir archivos de años no contiguos borra las escuchas captadas por notificación en los años de en medio. Borrar e insertar van en transacciones separadas. Al restaurar un respaldo, nutrición, comidas y series solo entran si aquí no hay nada, sin avisar.
- **Por qué importa:** lo borrado queda en la papelera solo 7 días y el mensaje final no lo menciona.
- **Propuesta:** borrar por el conjunto de fechas que trae cada hoja, y solo si esa hoja vino; una sola transacción; en la restauración, completar los días que falten y decirlo en el mensaje.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 14. «Tu año» afirma cosas con muy pocos datos o con datos de antes de usar la app
- **Tema:** cálculo.
- **Dónde:** `ciencia/TuAnio.kt`, `ui/pantallas/TuAnio.kt`, `ciencia/Periodo.kt` (`mejorDia`).
- **Qué pasa:** (a) «dormías de X a Y» y «caminabas unos N pasos» son la **moda** en cajas (de 15 minutos, de 1,000 pasos): con pocas noches gana una caja con cuatro o cinco casos aunque la media esté una hora más tarde; (b) «tu mejor día de la semana» se decide con tres o cuatro días por barra; (c) «el día más raro» y «estabas en…» pueden salir del historial importado, meses antes del primer día de diario; (d) en un empate («el día más feliz») gana el primero sin decir que hubo empate; (e) «días escritos de N» divide entre todos los días del año aunque el diario empezara en octubre; (f) la «palabra del año», sin años anteriores con que comparar, es solo la más frecuente; (g) los kilómetros del reloj van por día de calendario y los pasos por día lógico.
- **Por qué importa:** es la pantalla más vistosa y la que más se presta a enseñar; una frase rotunda con cuatro datos detrás le quita crédito a las que sí están bien.
- **Propuesta:** un mínimo de casos por frase (por ejemplo, 14 noches para el horario y 5 días por barra para el día de la semana) y, por debajo, callar; usar la mediana y decir «la mitad de las noches»; limitar «lo raro» y «dónde estabas» a los días desde el primer registro propio; decir los empates; «N días escritos desde el <primer día>».
- **Esfuerzo:** medio.
- **Congelación:** no rompe (análisis). Anotado como H-22-6.

### 15. Lo que llega por ⊕ o por un aviso borra lo que tenías abierto
- **Tema:** navegación.
- **Dónde:** `ui/MainActivity.kt` (`Raiz`, al recibir un destino), `ui/QuickActivity.kt`, `avisos/Avisos.kt`.
- **Qué pasa:** ⊕ → Nota desde Diario → Biblioteca termina en Hoy → Notas: «atrás» ya no vuelve a donde estabas. Tocar un aviso a media carta o a medio cuestionario descarta lo abierto.
- **Por qué importa:** rompe la regla de «atrás vuelve a donde estaba» y puede costar un texto.
- **Propuesta:** si el destino viene de dentro de la app, apilarlo sobre lo abierto; desde un aviso, apilar también si hay algo abierto.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 16. En Hoy, lo que se puede hacer queda debajo del primer pantallazo
- **Tema:** diseño.
- **Dónde:** Hoy (`ui/pantallas/Dia.kt` y `Hoy.kt`).
- **Qué pasa:** antes de «Te falta hoy» van el título (dos o tres renglones), la fecha del tema, la fecha común, una banda, el estado («guardado · racha»), la frase, el gato y los últimos días. La tarjeta empieza entre 715 y 950 dp según el tema: fuera de la primera vista en casi cualquier teléfono. En Básico la fecha sale dos veces. Hoy mide unos 3,000 dp, y Llenar el día unos 3,400 con once secciones.
- **Por qué importa:** cada apertura empieza con un desplazamiento.
- **Propuesta:** subir «Te falta hoy» y «Llenar el día» por encima de la frase y el gato; una sola fecha en Básico; «guardado» como un indicador discreto.
- **Esfuerzo:** medio.
- **Congelación:** no rompe.

### 17. Lo frecuente está lejos y Gastos no tiene puerta
- **Tema:** navegación.
- **Dónde:** `ui/pantallas/Mas.kt`, `Vida.kt`, `Explorar.kt`, `Gastos.kt`, `Pantallas.kt` («Tus atajos»); `ui/QuickActivity.kt` (`Registrar`).
- **Qué pasa:** Gastos solo se abre desde Análisis o el Tablero, no está en «Más» y es la única pantalla apilada sin «←». Sustancias, Suplementos, Hábitos y Privado están a tres toques (⚙ → sección → fila). En ⊕ los suplementos se anotan en un toque, pero los gastos frecuentes están un nivel abajo, la última sustancia pide cinco toques y el monto no toma el foco.
- **Por qué importa:** las tareas diarias deberían estar a uno o dos toques.
- **Propuesta:** «Tus atajos» ★ también al pie del menú ⊕; Gastos en «Más» y con «←»; en ⊕, fichas de tres o cuatro gastos frecuentes y «otra vez: <última sustancia>»; foco en el monto.
- **Esfuerzo:** bajo-medio.
- **Congelación:** no rompe.

### 18. El reporte de terapia cuenta dos veces el gimnasio y da medias sin decir de cuántos días
- **Tema:** reportes.
- **Dónde:** `ciencia/Terapia.kt`, `reportes/ReporteTerapia.kt`.
- **Qué pasa:** el reloj guarda la sesión de gimnasio como tipo «otro», y la regla que evita duplicarla solo reconoce «fuerza» y «pesas»: los mismos días salen en «Gimnasio» y en «Otras sesiones». Algunas medias se dan aunque salgan de un solo día del periodo. «Mi mejor día» no dice cuando hay varios empatados. Los avisos «mostrados» pueden superar a los «planeados» cuando un día no tiene fila de plan.
- **Por qué importa:** es el documento que lee otra persona; cada cifra debe poder defenderse.
- **Propuesta:** no listar en «otras» la sesión del reloj de un día de gimnasio, sea cual sea su tipo; añadir «(N días)» a toda media; decir los empates; contar como planeados al menos los mostrados.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe (presentación). Anotado como H-22-12.

### 19. «Día difícil» mide por dónde entraste, no cómo fue el día
- **Tema:** cálculo.
- **Dónde:** botón de la notificación (`avisos/Avisos.kt`, `ui/QuickActivity.kt`), `reportes/ReporteTerapia.kt`, `ciencia/Estadistica.kt` (`dia_dificil`).
- **Qué pasa:** `dia_dificil` solo se enciende al registrar la valencia desde el botón «registro mínimo» de la notificación, sea cual sea la valencia. En la hoja del día solo se puede quitar, no poner.
- **Por qué importa:** la variable se llama «día difícil» en el panel y en el reporte, pero mide «usé el camino corto».
- **Propuesta:** rotularla «registro mínimo» en el reporte y en el diccionario. Si se quiere una marca real de día difícil, es una ficha nueva en la hoja del día: decidirlo **antes de 2027**.
- **Esfuerzo:** bajo.
- **Congelación:** el rótulo no rompe; la ficha nueva cambia cómo se registra. Anotado como H-22-8.

### 20. Impulso, recaída y detonantes viven en el Día y en el Momento, con escalas distintas
- **Tema:** cálculo.
- **Dónde:** Privado (`ui/pantallas/Recaidas.kt`), hoja rápida (`ui/QuickActivity.kt`), `data/Recaidas.kt`, `ciencia/Terapia.kt`, `data/Diccionario.kt`.
- **Qué pasa:** el día guarda una escala de 0 a 5 y el registro con hora otra de 1 a 5. Una recaída con hora marca también el día; un impulso con hora no toca la escala del día. El reporte cuenta los registros con hora y promedia la escala del día, sin decir que son dos mediciones.
- **Por qué importa:** quien lea el reporte puede creer que es una sola medida.
- **Propuesta:** conservar ambas; en Privado, mostrar junto a la escala del día los registros con hora de ese día y rotularla «el peor momento del día»; en el reporte, dos filas con su nombre.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe si solo cambian rótulos; unificar las escalas sí la rompería. Anotado como H-22-7.

### 21. Un doble toque guarda dos veces
- **Estado:** **resuelto en parte** el 10 de noviembre (Apéndice D.177): una toma de suplemento idéntica a otra ya no se guarda. Falta el guardián general de los botones.
- **Tema:** datos.
- **Dónde:** hoja rápida ⊕ (`ui/QuickActivity.kt`: suplementos, registros privados), huella del día.
- **Qué pasa:** la auditoría encontró registros gemelos con la misma marca de tiempo o a segundos de distancia en una toma de suplemento, en un registro con hora y en una huella. Los botones de guardar no se desactivan tras el primer toque.
- **Por qué importa:** pocos casos, pero cuentan doble en dosis y conteos.
- **Propuesta:** un guardián común: el botón se desactiva al primer toque y `Repo` ignora un registro idéntico (mismo tipo y mismos campos) llegado en menos de 2 segundos.
- **Esfuerzo:** bajo.
- **Congelación:** cambia lo guardado solo en que evita duplicados (corrección): anotarlo en el Apéndice D.

### 22. El panel carga todas las ubicaciones de la historia cada vez
- **Tema:** lentitud.
- **Dónde:** `ciencia/Estadistica.kt` (`panel`, `kmPorDia`), `data/Exportar.kt`, `ciencia/Logros.kt`.
- **Qué pasa:** cada cálculo del panel crea un objeto por cada punto de ubicación guardado. Hay doce llamadas sin caché, y una más por cada logro propio que dependa de una variable. Si no cabe en memoria, el error se captura y `distancia_km` sale vacía en la pantalla y en el CSV, sin aviso.
- **Por qué importa:** lentitud que crece con los años y una exportación que puede salir incompleta en silencio.
- **Propuesta:** recorrer con cursor (`kmEntre` ya lo hace) conservando la regla «0 km si hubo puntos»; caché del panel por versión de los datos; si falla, decirlo.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe si el resultado es idéntico (comprobarlo con una prueba).

### 23. «Tu día» filtra colecciones enteras en cada redibujado
- **Tema:** lentitud.
- **Dónde:** Hoy y Llenar el día (`ui/pantallas/Dia.kt`, `TuDia` y `Vestigios`; `Hoy.kt`); `ui/Cartas.kt` (`SobreCarta`).
- **Qué pasa:** nueve filtros sobre colecciones completas sin `remember`; el de visitas interpreta la fecha de todas las visitas de la historia. Se repiten con cada cambio del día, es decir, con cada tecla. El brillo del sobre de las cartas se lee al componer, no al dibujar.
- **Por qué importa:** es la pantalla donde se escribe; crecerá con cada mes de datos.
- **Propuesta:** índices por fecha en `Repo`, recalculados al guardar, o `remember(fecha, lista)`; leer el brillo en la fase de dibujo.
- **Esfuerzo:** bajo-medio.
- **Congelación:** no rompe.

### 24. El widget «hoy» desordena las cifras en al-Ándalus y no lleva español en Seda
- **Tema:** diseño.
- **Dónde:** `widget/Widgets.kt`.
- **Qué pasa:** la línea empieza con la clave en árabe y el renglón entero se voltea de derecha a izquierda: «6 h 54» se lee «h 54 6», y la fecha igual. En Seda las claves van solo en chino.
- **Por qué importa:** el widget es lo que se ve sin abrir la app, y ahí las cifras salen revueltas.
- **Propuesta:** aislar la dirección del texto (caracteres U+2066 y U+2069) y usar la clave con español, como el reporte del día.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 25. Importes y ejes en letra de adorno
- **Tema:** diseño.
- **Dónde:** Gastos y gráficas (`ui/EstiloGrafica.kt` `letraCifras`, `ui/pantallas/Gastos.kt`).
- **Qué pasa:** en Art déco el importe va en una letra fina y gris donde la coma parece punto; en Cueva, en una letra angosta; los ejes miden unos 9.7 sp. En una misma pantalla hay tres letras distintas para dinero.
- **Por qué importa:** las cifras son el contenido; el adorno no debe costar una lectura equivocada.
- **Propuesta:** importes en la letra de texto del tema con cifras tabulares y color pleno; la letra de adorno solo en ejes, con mínimo de 11 sp.
- **Esfuerzo:** bajo-medio.
- **Congelación:** no rompe.

### 26. La ficha de una persona está a 4 o 5 toques y el grafo no la abre
- **Tema:** navegación.
- **Dónde:** Vida → Social (`ui/pantallas/Social.kt`, `Gente.kt`).
- **Qué pasa:** tocar una persona en el grafo ofrece «ver en Tu gente →», que abre la lista general, no a esa persona. «Tu gente» existe dos veces: como pantalla y como sección plegada de Social.
- **Por qué importa:** relacionar personas y completar fichas es trabajo de muchas repeticiones.
- **Propuesta:** que el nodo abra directamente la ficha de esa persona; una sola «Tu gente».
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 27. Lo que se marca a mano entra en el reporte del mes como si hubiera pasado ese mes
- **Estado:** **resuelto** el 10 de noviembre (esquema 23, Apéndice D.178), como lo pidió el autor: al marcar se pregunta «hoy», «otra fecha» o «sin fecha»; sin fecha cuenta en el total y no en los reportes. Las marcas de antes quedaron sin fecha.
- **Tema:** reportes.
- **Dónde:** Mundo → Patrimonio (`ui/pantallas/Mundo.kt`; `data/Mundo.kt`, `SitioMarcado.fecha`) y los reportes (`ciencia/Periodo.kt`, `sitios`).
- **Qué pasa:** al marcar a mano un sitio de Patrimonio que visitaste hace años, el reporte toma la fecha de la marca: aparece entre los lugares del mes y suma al total del año.
- **Por qué importa:** el reporte del mes dice que estuviste donde no estuviste.
- **Propuesta:** pedir el año de la visita al marcar (opcional) y contar en el periodo solo los que tienen fecha de visita dentro de él; lo marcado sin fecha, solo en el total histórico.
- **Esfuerzo:** bajo.
- **Congelación:** añade un campo opcional (permitido); no cambia ninguno existente.

### 28. Las fechas señaladas salen tal cual en reportes que se guardan en la galería
- **Tema:** reportes.
- **Dónde:** reportes semanal, mensual y anual, sección «Lo memorable» (`reportes/Reportes.kt`, `Senaladas.etiquetas`).
- **Qué pasa:** el reporte automático va en modo discreto (iniciales en vez de nombres, sin la frase del mejor momento), pero el texto de las fechas señaladas se copia entero, diga lo que diga.
- **Por qué importa:** ese archivo queda en Imágenes y Descargas, al alcance de la galería y de su copia a la nube. Una fecha señalada puede tocar algo que no quieres ahí.
- **Propuesta:** una casilla «solo para mí» en cada fecha señalada (campo nuevo opcional); las marcadas no salen en reportes automáticos ni en imágenes para compartir.
- **Esfuerzo:** bajo.
- **Congelación:** campo opcional nuevo (permitido).

### 29. El análisis presenta como hallazgo lo que es el mismo hecho
- **Tema:** cálculo.
- **Dónde:** Análisis → relaciones (`ciencia/Estadistica.kt`, `FAMILIAS`; variables `ctx_*`, `gimnasio`, `vi_*`, `con_*`).
- **Qué pasa:** el contexto «gym», el día de gimnasio y el entrenamiento importado son el mismo hecho medido tres veces, y lo mismo pasa con «vi a mi familia» y «con familia». Ninguna variable `ctx_*` está en las familias de «relaciones obvias», así que salen como descubrimiento.
- **Por qué importa:** ocupan los primeros lugares de la lista y tapan las relaciones que sí dicen algo.
- **Propuesta:** añadirlas a `FAMILIAS`.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe (análisis). Anotado como H-22-9.

### 30. Tres motores para metas, logros propios y campañas
- **Tema:** cálculo.
- **Dónde:** `data/Metas.kt`, `ciencia/Logros.kt`, `ciencia/Campanas.kt`; editores en `ui/pantallas/Lote2.kt` y `Logros.kt`.
- **Qué pasa:** cada uno evalúa «cumplí hoy» con su propio código. En Metas un día cuenta con «mayor o igual» al umbral; en los logros propios, con «mayor», aunque el texto diga «desde». Los propósitos del año se pueden escribir en tres sitios (el perfil, una meta con año y una nota de tipo «propósito»).
- **Por qué importa:** el mismo umbral da resultados distintos según dónde lo pongas, y los propósitos se dispersan.
- **Propuesta:** una función `cumple()` y un editor compartidos, respetando la regla con que se creó cada registro; los propósitos, solo en Metas (los otros dos sitios enlazan ahí).
- **Esfuerzo:** medio.
- **Congelación:** no rompe si cada registro conserva su regla. Anotado como H-22-10.

### 31. Dos pantallas «Tu año» y el mismo contenido en demasiadas vistas
- **Tema:** redundancia.
- **Dónde:** `ui/pantallas/Anno.kt` (ruta `anno`) y `TuAnio.kt` (ruta `tu_anio`); `Momentos.kt`, `LibretaMomentos.kt`, `Diarios.kt`, `ui/LibroDiario.kt`.
- **Qué pasa:** hay dos pantallas con el mismo nombre; el «Resumen» de la primera usa cifras propias y su botón «Guardar imagen del año» genera el reporte con otras. «Todos tus registros» convive con la Libreta, el filtro del Diario y la Cinta. El diario se exporta en cinco formatos con dos motores de PDF.
- **Por qué importa:** más caminos que mantener y cifras que no coinciden.
- **Propuesta:** renombrar la primera «El año en píxeles» y quitar su resumen (o alimentarlo de `Periodo`); poder editar al tocar un renglón de la Libreta y retirar «Todos tus registros»; dejar a la vista Libro y Obsidian y el resto bajo «más formatos»; un solo motor de PDF. **Ningún dato se pierde**: son vistas.
- **Esfuerzo:** medio.
- **Congelación:** no rompe.

### 32. Rótulos que se salen y nombres distintos para lo mismo
- **Tema:** diseño.
- **Dónde:** línea de 24 horas de Hoy (`ui/pantallas/Hoy.kt`); textos en `ui/QuickActivity.kt`, `ui/Componentes.kt`, `avisos/Avisos.kt`.
- **Qué pasa:** en la línea de 24 horas el rótulo se escribe en blanco desde el inicio del bloque sin comprobar si cabe (pasa también en Básico). El mismo registro se llama «Ánimo ahora», «aviso de ánimo», «muestreo de hoy», «momento» y «registro» según la pantalla. La escala vacía dice dos veces «sin marcar».
- **Por qué importa:** texto encimado y un vocabulario que obliga a adivinar.
- **Propuesta:** el rótulo dentro solo si cabe y, si no, al lado; un glosario de dos términos («registro de ánimo» para el que tú abres o llega por aviso; «momento» para todo lo demás con hora); quitar el duplicado.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe.

### 33. Detalles de la exportación
- **Tema:** exportación.
- **Dónde:** `data/Diccionario.kt` (`tipo`), `data/Exportar.kt`, `sensores/Pantalla.kt`, `data/Importaciones.kt` (YouTube), `assets/vestigium.R`.
- **Qué pasa:** (a) una columna que aún no tiene datos se declara «texto» si su nombre no acaba en un sufijo conocido (`fc_reposo`, `hrv`, `wa_mios`…), y R la lee como texto; (b) `dia_especial` sale como `0/1` en unas filas y `0.0/1.0` en otras del panel extendido; (c) el panel extendido trae una fila de mañana (por la agenda y la luna); (d) `por_hora_min` trunca cada hora, así que sus 24 valores suman unos minutos menos que `total_min`; (e) `video_id` de YouTube sale vacío en todo lo importado, quizá por una cabecera no reconocida; (f) `vestigium.R` no carga siete tablas: cartas, citas, catálogo de fotos, mundo, perfil, política y YouTube.
- **Por qué importa:** nada de esto pierde datos, pero cada uno es una trampa al analizar en R.
- **Propuesta:** (a) una lista explícita de columnas numéricas; (b) un solo formato; (c) cortar el extendido en hoy; (d) redondear repartiendo el resto; (e) revisar la cabecera con un Takeout de muestra; (f) cargarlas.
- **Esfuerzo:** bajo cada una.
- **Congelación:** (d) y (e) cambian lo guardado (corrección); las demás solo cambian la exportación o R.

### 34. «Idea» en cuatro sitios, ajustes muertos, utilidades copiadas y código sin uso
- **Tema:** redundancia.
- **Dónde:** `data/Catalogo.kt` (tipo de momento, tipo de pensamiento y tipo de nota «idea») y el Ideario; `data/Modelo.kt` (`Config`); varios archivos de `ui/`.
- **Qué pasa:** una idea se puede anotar como momento, como nota o en el Ideario. En los ajustes hay campos sin uso o duplicados (`barometro_ajuste`, `enfoque_tipo` y `enfoque_inicio`, `suplementos_hora`, `salud_sexo` frente a `perfil.sexo`; `metas_hora` se lee pero no tiene control) y la Papelera aparece dos veces. Hay utilidades copiadas: el halo privado 21 veces en `ui/vida/`, el formato de dinero cuatro veces (con y sin centavos), el de horas y minutos tres, y el formulario de gasto dos. Y hay funciones sin ningún uso, justo las versiones «todo en memoria» que ya se sustituyeron: `Exportar.respaldoJson`, `csvUbicaciones` y `csvCatalogoFotos`; las versiones de `Respaldos.restaurar` y `verificar` que reciben todo en memoria; `Drive.bajar` con `Red.bajar`; `Fechas.zonaDelDia`; `Instant.local` en `sensores/Salud.kt`; y `Db.enTransaccion`, que duplica `transaccion`.
- **Por qué importa:** cada copia puede desviarse (el dinero ya sale con centavos en una pantalla y sin ellos en otras).
- **Propuesta:** en Notas, dejar de ofrecer «idea» y «pensamiento» para notas nuevas (las viejas se conservan); un `Formato.kt` y un halo compartido; retirar los campos muertos de la interfaz sin borrarlos del modelo; borrar las funciones sin uso.
- **Esfuerzo:** bajo.
- **Congelación:** no rompe (no se borra ningún campo guardado).

---

## Menores y por vigilar

- **Toques largos sin pista y pestañas apiladas** (navegación; `ui/MainActivity.kt`, `ciencia/Totales.kt`). El toque largo en Vida abre «Resumen semanal», que ya vive en Análisis; los de Diario y ⊕ hacen lo mismo entre sí; ninguno se anuncia. El buscador abre Hoy, Diario, Vida y Análisis como copia apilada, sin «←» ni pestaña marcada. *Propuesta:* en `Nav.ir`, si la ruta es una pestaña, cambiar de pestaña; toque largo en Vida → Social; una línea de ayuda con los toques largos. Esfuerzo bajo; no rompe.
- **Todo se carga en memoria en cada arranque del proceso** (lentitud; `App.kt`, `data/Repo.kt` `init`, `data/Respaldos.kt`). Cada pulso de fondo, widget o geocerca arranca el proceso y decodifica 38 colecciones completas; el respaldo añade las tablas grandes y la verificación lo decodifica todo otra vez. *No medido.* Riesgo, con los años, de «la app no responde» en los receptores y de un respaldo que no quepa en memoria. *Propuesta:* carga perezosa de lo que el fondo no usa (YouTube, WhatsApp, subrayados, huellas, laptop, respuestas) y respaldo colección por colección. Esfuerzo alto; no rompe.
- **Solo 95 lugares tienen geocerca, elegidos por orden de creación** (datos; `sensores/Ubicacion.kt`, `Repo.lugares.take(95)`). Con más de 95 lugares, los últimos nunca registran visitas, sin aviso. *Propuesta:* registrar los 95 más cercanos a la última ubicación, volver a registrar al moverse y avisar en «Estado de los datos». Esfuerzo medio; no rompe.

---

## Lo que está bien resuelto (no gastar tiempo ahí)

- **Guardado del diario y del día:** a los 700 ms, al salir y antes de navegar; cada escritor toca solo su campo.
- **Respaldos:** manifiesto con huella SHA-256, relectura, escritura a un archivo nuevo y renombrado, copias escalonadas, base cruda semanal, ensayo mensual de restauración y freno ante una copia empobrecida.
- **Fechas:** día lógico, historial de zonas desde la instalación y días de 23 o 25 horas, correctos en todo lo registrado por el teléfono.
- **Exportación:** JSON, CSV y SQLite traen las mismas filas; el diccionario cubre todas las columnas; no hay identificadores repetidos ni valores fuera de rango.
- **Reportes:** todas las cifras que se pudieron recalcular a mano salen de su fórmula; los problemas eran de divisores y rótulos.
- **Social, logros y fechas señaladas:** las fusiones de noviembre (Gente, Red y Cielo en Social; hitos como fechas señaladas) quedaron limpias.

## Lo que no se pudo comprobar

- Nada se probó en un teléfono: todo sale de leer el código, de las capturas de diseño y de una exportación.
- `vestigium.R` no se ejecutó (no hay R en el entorno); lo dicho sobre él es lectura del código.
- El tiempo de arranque y la memoria (hallazgo 22 y «por vigilar») no se midieron.
- Los Takeout originales no se vieron: lo de `video_id` (hallazgo 33) es una sospecha.
