# Anexo A · Fase 1 · Mapa actual de navegación

_Revisión hecha sobre el código de `main` (último commit `22e6713`, con la 10b ya mezclada: `docs/paper/PLAN_PAPER_METODOLOGIA.md:25` dice «10a y 10b hechas 2026-10-27»). No se cambió ningún archivo del repositorio._

**Convenciones**

- Todas las rutas de archivo son relativas a `app/src/main/java/io/github/ruddy2003/vestigium/`. `MA` = `ui/MainActivity.kt`.
- **Toques** se cuentan desde el arranque: la app abre en la pestaña **Hoy** (id `dia`, `MA:236`), que muestra `PantallaDia` (`ui/pantallas/Dia.kt:102`).
  - ⚙ = botón de ajustes del encabezado (abre **Más**). Está en Día (`Dia.kt:172`), Diario (`Diarios.kt:121`), Vida (`Vida.kt:72`) y Análisis (`Explorar.kt:102`).
  - 🔍 = botón del buscador (ruta `buscar`). Está en Día (`Dia.kt:171`), Diario, Vida, Análisis, Gastos (`Gastos.kt:91`) y Más (`Mas.kt:53`).
  - «Buscador» = 🔍 + escribir + tocar el resultado: **2 toques + texto**. La lista de pantallas del buscador es `Pantallas.TODAS` (`ui/pantallas/Pantallas.kt:36-92`), y se muestra en `EnPantallas` (`Pantallas.kt:107`) dentro de `PantallaTotales(buscar = true)` (`Totales.kt:103`).
  - «Más §n» = sección numerada de `PantallaMas` (`ui/pantallas/Mas.kt:56-143`): §1 Registros especiales, §2 Tu gente y sustancias, §3 Medir, §4 Sistema. Desde el arranque son **2 toques** (⚙ + fila). §3 y §4 casi seguro quedan bajo el pliegue y piden desplazarse; **no se comprobó** en un teléfono.
- **¿Deshacer?** se refiere a la barra de 5 s de `ui/Deshacer.kt` (`Deshacer.ofrecer`, que se muestra con `CapaDeshacer`, `MA:404`). Si hay otro mecanismo, se dice cuál.
- Títulos: se da el título base en español que recibe `Hoja(...)`. **Cada tema puede cambiarlo**, porque `Hoja` usa `Estilo.pantalla(titulo, latin)` (`ui/Hoja.kt:45`, `ui/Estilo.kt:65-73`, nombres por tema en `ui/NombresNuevos.kt`). Por ejemplo, en romano «La cinta» se muestra «HORAE». Aquí no se compara título por tema.

---

## 1. Tabla de rutas

En el `when (val actual = destino.ruta)` (`MA:322-399`) hay **79 cadenas** en 76 ramas, más el prefijo `guia_uso:<x>` (`MA:398`) y la rama `else`, que pinta «Pantalla desconocida» (`MA:399`). Las filas siguen el orden del `when`. Los alias van en la misma fila.

| Ruta | Pantalla (archivo:línea) | Cómo se llega (desde dónde, toques desde el arranque) | Qué muestra (1 línea) | Qué permite crear/editar/borrar | ¿Elegir fecha? | ¿Deshacer? | Uso esperado |
|---|---|---|---|---|---|---|---|
| `dia` (`MA:323`) | `PantallaDia`, `ui/pantallas/Dia.kt:102`; título «Día» con encabezado propio (`Dia.kt:164-169`) | **0** (arranque). Pestaña Hoy: 1. `Seleccion.abrir` → `nav.pestana("dia")` (`Calendario.kt:78`) desde Calendario, Tu año, Resumen semanal, Diario, Entrada, Sueño, Libreta, Cinta, Gimnasio, Días atípicos, «Hace un año» y la búsqueda en el diario. Buscador «Día» (`Pantallas.kt:37`), que hace `nav.ir("dia")` y **apila otra copia**. Botón «Empezar» de la bienvenida (`Sistema.kt:231`). Desde fuera: widgets y aviso del diario (ver §3) | El día abierto: estado, frase, sueño, «Te falta hoy», datos que faltan, tablero, tarjeta de la noche, línea de 24 h, momentos, tramos, días especiales, «hace un año», experimentos, lo del teléfono | Edita el `Dia`: calidad, texto y detalles del sueño (`Dia.kt:219-247`) y las horas de sueño con `EditarSueno` (`Dia.kt:243-246`). Responde «¿Lo hiciste hoy?» del experimento (`Dia.kt:866-881`). Pasa a la semana corta (`Dia.kt:330`) | **Sí**: ‹ › (`Dia.kt:194`, `:209`), deslizar a los lados (`Dia.kt:153-162`), tocar la fecha abre el calendario (`Dia.kt:177-178`), «hoy» (`Dia.kt:208`) | No (se guarda solo, `Dia.kt:120`). `EditarSueno` tiene «quitar la corrección» (`ui/EditarSueno.kt:67`) | Diario |
| `diario` (`MA:324`), alias `diarios` | `PantallaDiario`, `ui/pantallas/Diarios.kt:86`; título «Diario» (`Diarios.kt:121`) | Pestaña Diario: **1**. `diarios`: **no lo envía nadie** (grep de `"diarios"` solo da `MA:252` y `MA:324`). Ojo: un intent externo con `diario` **no** abre esta pantalla, abre la noche (`MA:249`). Un intent con `diarios` lo intercepta `MA:252` (pestaña Diario + `biblioteca_diario`), así que el alias del `when` nunca se usa | Botón «¿Cómo estás ahora?», objeto «Hoy» para escribir el diario, estante de la biblioteca, línea de tiempo con buscador y filtros (plegada), exportar (plegado) | Crea un momento (ánimo, vía QuickActivity, `Diarios.kt:123`). Escribe el diario **de hoy** (`EscribirDia`, `Diarios.kt:127`, `:183`). Edita o borra momentos con `DialogoMomento` (`Diarios.kt:182`) | No (solo hoy; el pasado se abre por Entrada o «abrir el día») | Sí, al borrar un momento (`Momentos.kt:164`) | Diario |
| `momentos` (`MA:325`) | `PantallaMomentos`, `ui/pantallas/Momentos.kt:57`; título «Momentos» (`Momentos.kt:70`), **sin botón atrás** | **Huérfana**: ningún `nav.ir("momentos")`, no está en Más ni en el buscador. Un intent externo `momentos` lo intercepta `MA:251` y abre la pestaña Diario, y nadie lo envía. Solo la usan las capturas (`app/src/test/.../Capturas.kt:882`) | Momentos de los últimos 30 días; botón de ánimo | Edita o borra con `DialogoMomento` (`Momentos.kt:100`) | No | Sí (`Momentos.kt:164`) | — (inaccesible) |
| `gastos` (`MA:326`) | `PantallaGastos`, `ui/pantallas/Gastos.kt:70`; título «Gastos», **sin botón atrás** (`Gastos.kt:91`) | Vida → tarjeta «Dinero» (`Vida.kt:98`): 2, al final de Vida. Invitación «gastaste algo hoy →» en Día (`Dia.kt:280`), solo desde las 20 h y sin gastos ese día (`Hoy.kt:117`): 1. Buscador. **No está en Más** | Formulario de gasto o ingreso, mes con ‹ ›, totales, balanza, listas | Crea, edita (tocar la fila carga el formulario «Corregir…») y borra gastos e ingresos | **Sí**: `SelectorHora` con «otra fecha u hora…» (`Gastos.kt:119`, `ui/Selectores.kt:47-65`) | Sí (`Gastos.kt:184`, `:228`) | Diario |
| `entender` (`MA:327`), alias `explorar` | `PantallaExplorar`, `ui/pantallas/Explorar.kt:69`; título «Análisis» (`Explorar.kt:101`) | Pestaña Análisis: **1**. Buscador «Análisis» (`P("entender")`, `Pantallas.kt:47`), que **apila** la pantalla de pestaña. `explorar`: nadie la envía; un intent externo con `explorar` va a `MA:253` | Tarjetas Experimentos y Totales, cifras, herramientas, cobertura, relaciones, personas y temas, lugares y ánimo, etapas | Nada | No | — | Semanal |
| `vida` (`MA:328`) | `PantallaVida`, `ui/pantallas/Vida.kt:61`; título «Vida» | Pestaña Vida: **1**. Buscador (`Pantallas.kt:46`), que apila | Rejilla de tarjetas (semana, cinta, calendario, año, gente, constelación, totales, mapa), recuadros (Tu año, Logros, Campañas, Mapas, Obras, Otras vidas), dinero, sustancias, enfoque | Nada | No | — | Semanal |
| `personas` (`MA:329`) | `PantallaPersonas`, `ui/pantallas/Gente.kt:150`; título «Tu gente» | Más §2 (`Mas.kt:87`): 2. Vida → «Tu gente» (`Vida.kt:79-80`): 2. Tarjeta «¿quién es?» en Día (`Dia.kt:286`, condicional): 1. Red → botón (`Lote3.kt:391`). Buscador. Aviso de días especiales (`avisos/Avisos.kt:215`) | Quién es cada @persona, días especiales, posibles duplicados, sin clasificar | Clasifica y edita personas en un diálogo (`Gente.kt:209`, `:262`): relación, cercanía, nacimiento y fallecimiento (10b). Agrega y quita días especiales («+ fecha propia», «quitar», `Gente.kt:272`). Une duplicados | Sí, para fechas de nacimiento y días especiales (no es «registrar en otra fecha») | No | Mensual |
| `temas` (`MA:330`) | `PantallaTusTemas`, `ui/pantallas/TusTemas.kt:51`; título «Tus temas» | Más §2 (`Mas.kt:92`): 2. Buscador | #temas, duplicados, temas de un solo día | Une, renombra o pasa a hito | No | Sí (`TusTemas.kt:60`) | Mensual |
| `presencias` (`MA:331`) | `PantallaPresencias`, `TusTemas.kt:144`; título «¿Estuviste o pensaste?» | Tu gente → fila «¿Estuviste o **solo** pensaste?» (`Gente.kt:159`, solo si hay días por revisar): 3. Buscador | Días con @personas por revisar | Cambia cada mención: estuve con / pensé en / mensajes | No (es una lista de días) | No | Semanal |
| `hoja_momentos` (`MA:332`) | `PantallaHojaMomentos`, `ui/pantallas/LibretaMomentos.kt:274`; rótulo «Libreta de momentos», **sin barra inferior** (`MA:309`) | Día → «abrir la hoja de la libreta →» (`Dia.kt:263`, solo si hay momentos ese día): 1 + desplazar. Libreta (`LibretaMomentos.kt:174`): 3 | La hoja de momentos de un día | Solo lectura; compartir; «abrir el día» (`LibretaMomentos.kt:320`) | Navega ‹ › entre los días con momentos | — | Semanal |
| `sustancias` (`MA:333`) | `PantallaSustancias`, `ui/pantallas/SustanciasPantalla.kt:62`; título «Sustancias» | Más §2 (`Mas.kt:94`): 2. Vida → «Sustancias» (`Vida.kt:99`): 2. Buscador | Episodios abiertos, cafeína, falta completar, últimos 30 días, historial | Crea con «+ sustancia» y edita o completa: las dos cosas abren QuickActivity `consumo` (`SustanciasPantalla.kt:39-45`). Borra dentro de QuickActivity (`QuickActivity.kt:606`) | Sí, en QuickActivity: `SelectorHora` (`QuickActivity.kt:520`, `:582-584`) | Se llama a `Deshacer.ofrecer` (`QuickActivity.kt:606`), pero **QuickActivity no tiene `CapaDeshacer`**: la barra no se ve ahí. No se comprobó si aparece al volver a la app | Semanal |
| `mas` (`MA:334`) | `PantallaMas`, `Mas.kt:48`; título «Más» | ⚙ en Día: **1**; en las otras pestañas: 2. Primer arranque: es el fondo de la pila `["mas","permisos","conocer"]` (`MA:240`) | Atajos fijados (★), secciones §1-§4 y una ayuda de los toques largos (`Mas.kt:144`) | Fijar o quitar ★ (`Pantallas.kt:136-138`, guarda **nombres de ruta** en `config.favoritos_mas`) | — | — | Semanal |
| `escalas` (`MA:335`) | `PantallaEscalas`, `ui/pantallas/Medir.kt:77`; título «Cuestionarios» | Día → botón «✎ cuestionarios» (`Dia.kt:302`): 1. Invitación en «Te falta hoy» (`Dia.kt:279`): 1. Más §3 (`Mas.kt:107`): 2. Buscador. Aviso de cuestionarios (`Avisos.kt:191`) | Lista de instrumentos con su próxima fecha e historial | Responde un cuestionario (`Repo.guardarRespuesta`, `Medir.kt:134`). No edita ni borra respuestas | No | No | Semanal |
| `reaccion` (`MA:336`) | `PantallaReaccion`, `Medir.kt:160`; título «Prueba de reacción» | Día → «⚡ reacción» (`Dia.kt:303`): 1. Invitación (`Dia.kt:282`). Más §3 (`Mas.kt:109`): 2. Buscador | La prueba de 30 s y su resultado | Crea una `Reaccion` (`Medir.kt:189`) | No | No | Diario |
| `lugares` (`MA:337`) | `PantallaLugares`, `Medir.kt:230`; título «Lugares» | Vida → Mapas → mosaico «Lugares» (`Galerias.kt:312`): 3. Análisis → «Ver el mapa emocional de tus lugares ›» (`Explorar.kt:275`): 2 + mucho desplazamiento. Lugares especiales → enlace (`Lote2.kt:213`). **No está en Más ni en el buscador** | Mapa emocional, marcar dónde estoy, tus lugares | Crea un lugar con la ubicación actual (`Medir.kt:267`), edita (`:290`), borra (`:295`) | No | Sí (`Medir.kt:295`) | Mensual |
| `habitos` (`MA:338`) | `PantallaHabitos`, `Mas.kt:151`; título «Hábitos» | Más §1 (`Mas.kt:66`): 2. Noche → «+ agregar hábitos» (`Dia.kt:443`, solo si no hay ninguno). Buscador. Widget de hábitos vacío (`widget/Widgets.kt:246`) | Activos, sugeridos, archivados | Crea, archiva y reactiva. **No renombra ni borra** | No | No | Raro |
| `experimentos` (`MA:339`) | `PantallaExperimentos`, `ui/pantallas/Lote3.kt:402`; título «Experimentos personales» | Análisis → tarjeta (`Explorar.kt:104`): 2. Buscador. **No está en Más** | Experimentos en curso y terminados, con su cumplimiento | Crea («Sellar y empezar el lunes», `Lote3.kt:454`) y termina (`Lote3.kt:503`). Las respuestas diarias se dan en el Día | No (empieza el lunes siguiente) | No | Semanal |
| `cartas` (`MA:340`) | `PantallaBibliotecas(nav,"cartas")`, `ui/pantallas/Bibliotecas.kt:107`; título «Cartas» (`Bibliotecas.kt:127`) | Diario → «✉ cartas» (`Diarios.kt:138`): 2. Más §1 (`Mas.kt:77`): 2. Selector de bibliotecas (`nav.reemplazar`, `Bibliotecas.kt:116`). Buscador. Aviso de carta (`Avisos.kt:202`) | Cartas selladas, abiertas y a otras personas | Lleva a escribir una carta (`Bibliotecas.kt:375`) y a abrir una (`:396-405`) | — | — | Raro |
| `carta_nueva` (`MA:341`) | `PantallaCartaNueva`, `Bibliotecas.kt:421`; título «Escribir una carta» | Cartas → «✉ escribir una carta» (`Bibliotecas.kt:375`): 3. Historias de Tu año (`TuAnio.kt:660`) | Editor de la carta | Crea la carta (`Bibliotecas.kt:468`) | Sí: la fecha de apertura (`DatePickerDialog`, `Bibliotecas.kt:454`) | — | Raro |
| `conocer` (`MA:342`) | `PantallaConocer`, `ui/pantallas/Conocer.kt:67`; título «Conocerte» | Primer arranque (`MA:239-240`). Más §3 (`Mas.kt:115`): 2. Buscador | Perfil: tú, con quién vives, a quién ves, pareja, dedicación, costumbres | Edita el perfil; agrega y quita mascotas y personas cercanas (`Conocer.kt:124`, `:145`) | Sí, la fecha de nacimiento (`Conocer.kt:96`) | No | Raro |
| `politica` (`MA:343`) | `PantallaPolitica`, `ui/pantallas/Politica.kt:70`; título «Brújula política» | Más §3 (`Mas.kt:112`): 2. Invitación anual (`Dia.kt:281`, `Hoy.kt:119`). Buscador | El test y la brújula por año | Crea el test del año | No | No | Anual |
| `mapas` (`MA:344`) | `PantallaMapas`, `ui/pantallas/Galerias.kt:287`; título «Mapas» | Vida → recuadro «Mapas» (`Galerias.kt:193`): 2. Buscador | Mosaicos: Mundo, Mapa para rascar, Patrimonio, Tus calles, Lugares, Ciudades visitadas | Nada | — | — | Mensual |
| `obras` (`MA:345`) | `PantallaObras`, `Galerias.kt:343`; título «Obras» | Vida → recuadro «Obras» (`Galerias.kt:204-205`): 2. Buscador, que la llama **«Biblioteca»** (`Pantallas.kt:73`) | Rejilla de portadas con filtros | «+ añadir» (`Galerias.kt:360`); abre cada obra | — | — | Mensual |
| `salud` (`MA:346`) | `PantallaSalud`, `ui/pantallas/Salud.kt:66`; título «Salud» | Más §3 (`Mas.kt:101`): 2. Buscador | Últimos 30 días frente a la OMS | Nada | No | — | Mensual |
| `tus_apps` (`MA:347`) | `PantallaTusApps`, `ui/pantallas/TusApps.kt:74`; título «Tus apps» | Más §3 (`Mas.kt:103`): 2. Buscador | YouTube, música, comida, Kindle, WhatsApp… | Nada | — | — | Mensual |
| `otras_vidas` (`MA:348`) | `PantallaOtrasVidas`, `ui/pantallas/OtrasVidas.kt:257`; título «Otras vidas» | Vida → último recuadro (`OtrasVidas.kt:231`): 2. Buscador | Comparación con vidas de otras épocas | Nada | — | — | Raro |
| `logros` (`MA:349`), alias `hall` (`MA:386`) | `PantallaLogros`, `ui/pantallas/Logros.kt:138`; título «Logros», con la sección «Hall de logros» dentro (`Logros.kt:176`) | Vida → recuadro «Logros» (`Logros.kt:126`): 2. Buscador. Aviso de logros (`Avisos.kt:238`). `hall`: solo `RecuadroHall` (`Hall.kt:507`), **que nadie llama** | Hall, salas de trofeos, logros propios | «+ Inventar un logro»; borrar un logro propio (`Logros.kt:231`); elegir piezas del hall | No | No | Raro |
| `tu_anio` (`MA:350`) | `PantallaTuAnio`, `ui/pantallas/TuAnio.kt:680`; título «Tu año en historias» | Vida → primer recuadro (`TuAnio.kt:760`): 2. Buscador. Aviso «Tu año está listo» (`Avisos.kt:249`) | Las historias del año y las predicciones | Guarda predicciones; desde las historias, escribe una carta (`TuAnio.kt:660`) | No | — | Anual |
| `carta` (`MA:351`) | `PantallaCarta`, `Bibliotecas.kt:490`; título «Carta», **sin barra** (`MA:309`) | Cartas (`Bibliotecas.kt:396`, `:400`, `:405`): 3. Buscar → resultados en cartas (`Totales.kt:109`) | Lectura de una carta | Edita y borra (`Bibliotecas.kt:518-526`) | — | Sí (`Bibliotecas.kt:526`) | Raro |
| `sueno` (`MA:352`) | `PantallaSueno`, `Bibliotecas.kt:286` (`LecturaTema`); **sin barra** | Biblioteca de sueños → estante (`Bibliotecas.kt:278`): 3 | Un sueño a pantalla completa | Editar (`EscribirSueno`), compartir, «abrir el día» | Navega ‹ › entre sueños | No | Semanal |
| `biblioteca_suenos` (`MA:353`) | `PantallaBibliotecas(nav,"suenos")`, `Bibliotecas.kt:107`; título «Biblioteca de sueños» | Diario → «☾ sueños» (`Diarios.kt:136`): 2. Más §1 (`Mas.kt:79`): 2. Selector. Buscador | Estante de sueños | Abre un sueño | — | — | Semanal |
| `reportes` (`MA:354`) | `PantallaReportes`, `ui/pantallas/Sistema.kt:444`; título «Reportes» | Más §4 (`Mas.kt:137`): 2 + desplazar. Buscador | Generar ahora los reportes semanal, mensual o anual | Genera archivos | — | — | Mensual |
| `datos` (`MA:355`) | `PantallaDatos`, `Sistema.kt:471`; título «Datos» | Más §4 (`Mas.kt:129`): 2 + desplazar. «Datos que faltan» en Día (`Hoy.kt:270`, condicional): 1. Gimnasio → «ir a Datos →» (`Fuerza.kt:50`). Estado de los datos (`EstadoDatos.kt:136-143`, `:180`). Buscador. Avisos de importar, respaldo y Drive (`Avisos.kt:265`, `:266`, `:325`, `:337`, `:347`) | Exportar, respaldos, Drive, Spotify, importar, etiquetas equivalentes, categorías de gasto | Configuración, importaciones y respaldos; categorías de gasto (`Sistema.kt:612`) | `elegirFecha` para posponer (`Sistema.kt:669`) | — | Mensual |
| `permisos` (`MA:356`) | `PantallaPermisos`, `Sistema.kt:88`; título «Permisos y sensores», o **«Bienvenido»** en el primer arranque (`Sistema.kt:111`) | Primer arranque (`MA:240`). Más §4 (`Mas.kt:139`): 2. Día → «sensores →» o «dar acceso →» (`Dia.kt:732`, `:746`, en una sección que se pliega sola). «Datos que faltan» (`Hoy.kt:269`). Estado de los datos (`EstadoDatos.kt:180`, `:200`). Buscador | Cada permiso con su estado | Activa permisos | — | — | Raro |
| `ajustes` (alias) / `apariencia` (`MA:357`) | `PantallaAjustes(nav,"apariencia")`, `Sistema.kt:252`; título «Apariencia» (`Sistema.kt:258`) | Más §4 `apariencia` (`Mas.kt:123`): 2. Buscador (`Pantallas.kt:84`). `ajustes`: **nadie lo envía** (grep solo da `ui/IconosPestana.kt:73` y `ui/Decoracion.kt:1368`, que son íconos) | Tema, modo, ícono, letra, animaciones, personaje, sonidos | Configuración | — | — | Raro |
| `avisos` (`MA:358`) | `PantallaAjustes(nav,"avisos")`; título «Avisos y recordatorios» | Más §4 (`Mas.kt:125`): 2. Buscador | Sonidos, avisos de ánimo, horas de sueño y diario (`Sistema.kt:388-394`), cuestionarios | Configuración | — | — | Raro |
| `privacidad` (`MA:359`) | `PantallaAjustes(nav,"privacidad")`; título «Privacidad» | Más §4 (`Mas.kt:127`): 2. Estado de los datos (`EstadoDatos.kt:101`). Buscador | Huella, modo discreto, seguimiento privado, ubicación, sustancias ocultas | Configuración; enlace a Privado (`Sistema.kt:421`) | — | — | Raro |
| `guia` (`MA:360`) | `PantallaGuia`, `Mas.kt:201`; título «Guía científica» | Más §4 (`Mas.kt:141`): 2. Guía de uso → botón (`Lote3.kt:528`). Buscador | Cómo leer los datos, cuestionarios, referencias | Nada | — | — | Raro |
| `calendario` (`MA:361`) | `PantallaCalendario`, `ui/pantallas/Calendario.kt:101`; título «Calendario» | Día → tocar la fecha «▾» (`Dia.kt:177-178`): **1**. Vida → tarjeta (`Vida.kt:77`): 2. Buscador. Widget Mes (`Widgets.kt:283`) | El mes por capas, un resumen y «ir a una fecha…» | Nada; tocar un día abre el Día (`Calendario.kt:115`) | **Sí** («ir a una fecha…», `Calendario.kt:164-167`; mes ‹ ›) | — | Semanal |
| `escalada` (`MA:362`) | `PantallaEscalada`, `ui/pantallas/Escalada.kt:74`; título «Escalada» | Más §1 (`Mas.kt:59`): 2. Buscador | Sesiones, progreso, sesiones del reloj sin bloques | Crea una sesión (`Escalada.kt:88`); borra sola las sesiones vacías (`Escalada.kt:76`) | La sesión nueva es de hoy o de una sesión del reloj | — | Semanal |
| `ascenso` (`MA:363`) | `PantallaAscenso`, `Escalada.kt:188`; título de la sesión («Nueva sesión» o «Escalada») | Escalada (`Escalada.kt:88`, `:129`): 3 | Una sesión: cuándo y dónde, bloques, cómo te sentiste | Edita la sesión y los bloques; borra la sesión (`Escalada.kt:281`) | **Sí**: «cambiar fecha» (`Escalada.kt:209`) | Sí (`Escalada.kt:281`) | Semanal |
| `biblioteca` (`MA:364`) | `PantallaBiblioteca`, `ui/pantallas/Biblioteca.kt:183`; título **«Obras»** (`Biblioteca.kt:190`) | Obras → «lista y pendientes» (`Galerias.kt:362`): 3. Noche → «Lo que leí o vi hoy» → «ver obras» (`Biblioteca.kt:618`). **No está en Más ni en el buscador** | Pendientes, terminados, archivados, Kindle, buscadores y claves de API | Buscar y añadir; borra solas las obras sin título (`Biblioteca.kt:188`) | — | — | Mensual |
| `obra` (`MA:365`) | `PantallaObra`, `Biblioteca.kt:416` | Obras (`Galerias.kt:393`), lista (`Biblioteca.kt:273`, `:299`), Frases guardadas (`FrasesGuardadas.kt:129`), al terminar Buscar obra (`Biblioteca.kt:367`, `:405`): 3-4 | La ficha de una obra | Estado, calificación, fechas, citas, portada; borrar (`Biblioteca.kt:524`) | Sí: inicio y fin (`Biblioteca.kt:429`) | Sí (`Biblioteca.kt:524`) | Mensual |
| `buscar_obra` (`MA:366`) | `PantallaBuscarObra`, `Biblioteca.kt:321`; título «Buscar obra» o «Buscar portada» | Obras «+ añadir» (`Galerias.kt:360`), Biblioteca (`Biblioteca.kt:196`), Obra → portada (`Biblioteca.kt:450`), Noche → «buscar otra…» (`Biblioteca.kt:617`) | Búsqueda en línea por título o DOI | Crea una obra (`Biblioteca.kt:357-361`) | — | — | Mensual |
| `anno` (`MA:367`) | `PantallaAnno`, `ui/pantallas/Anno.kt:58`; título «Tu año» | Vida → tarjeta «Tu año» (`Vida.kt:78`): 2. Buscador | El año en píxeles, mes por mes, resumen | Guardar imagen; tocar un cuadro abre ese día (`Anno.kt:92`) | Navega por el año | — | Mensual |
| `mapa` (`MA:368`) | `PantallaMapa`, `ui/pantallas/Mapa.kt:389`; título «Mapa» | Vida → tarjeta «Mapa» (`Vida.kt:83`): 2. Mapas → «Tus calles» (`Galerias.kt:309`): 3. Lugares → «ver con calles →» (`Medir.kt:248`). Buscador | Ánimo, calor, ruta del día y lugares sobre el mapa | Marca un lugar en el mapa (`Mapa.kt:541`) | Periodos y ruta del día | — | Mensual |
| `mundo` (`MA:369`) | `PantallaMundo`, `ui/pantallas/Mundo.kt:357`; título «Mundo» | Mapas → «Mundo» (`Galerias.kt:304`): 3. **No está en el buscador ni en Más** | Ciudades y países | Quita ciudades agregadas a mano (`Mundo.kt:431-439`) | — | No | Raro |
| `fondo_telefono` (`MA:370`) | `PantallaFondoTelefono`, `ui/FondosTelefono.kt:230`; título «Fondo del teléfono» | Apariencia → «fondo del teléfono con este tema →» (`Sistema.kt:282`): 3 | Vista previa y elección del fondo | Configuración | — | — | Raro |
| `rascar` (`MA:371`) | `PantallaRascar`, `Mundo.kt:659`; título «Mapa para rascar» (`Mundo.kt:710`) | Mapas (`Galerias.kt:292`, `:305`): 3. Mundo (`Mundo.kt:383`): 4 | Mapa para rascar países | Marca países visitados | — | — | Raro |
| `unesco` (`MA:372`) | `PantallaUnesco`, `Mundo.kt:854`; título «Patrimonio Mundial» | Mapas (`Galerias.kt:308`): 3. Mundo (`Mundo.kt:464`) | Sitios de la UNESCO por país | «estuve aquí» / «quitar» (`Mundo.kt:937`) | — | No | Raro |
| `ciudad_nueva` (`MA:373`) | `PantallaCiudadNueva`, `Mundo.kt:780`; título «Añadir ciudad» | Mapas → «**Ciudades visitadas**» (`Galerias.kt:313`): 3. Mundo → «Añadir una ciudad» (`Mundo.kt:414`) | Buscar una ciudad | Agrega una ciudad visitada | Sí (`Mundo.kt:796`) | — | Raro |
| `analisis` (`MA:374`) | `PantallaAnalisis`, `ui/pantallas/Analisis.kt:89`; título **«Análisis»**, el mismo que la pestaña | Análisis → «Herramientas» (`Explorar.kt:119`): 2. Fichas de personas, temas y lugares (`Explorar.kt:99`): 2 + desplazar | Una herramienta (tendencia, semana, factores, modelo, dinámica, ritmo, palabras, atípicos) | Nada; los días atípicos abren el Día (`Analisis.kt:406`) | — | — | Semanal |
| `fuerza` (`MA:375`) | `PantallaFuerza`, `ui/pantallas/Fuerza.kt:43`; título **«Gimnasio»** | Más §1 «Gimnasio» (`Mas.kt:61`): 2. Buscador «Gimnasio» | Entrenamientos importados de MacroFactor | Solo lectura (se corrige en MacroFactor y se reimporta); tocar un entrenamiento abre el Día (`Fuerza.kt:118`) | — | — | Semanal |
| `totales` (`MA:376`) | `PantallaTotales`, `ui/pantallas/Totales.kt:62`; título «Totales» | Vida (`Vida.kt:82`): 2. Análisis → tarjeta (`Explorar.kt:105`): 2. «Ver menciones… en Totales ›» (`Explorar.kt:262`). Buscador | Todo lo acumulado por periodo | Nada | Periodo «desde…» (`Totales.kt:94`) | — | Mensual |
| `buscar` (`MA:377`) | `PantallaTotales(nav, buscar=true)`; título «Buscar» (`Totales.kt:86`) | 🔍 en Día: **1**. Toque largo en Análisis (`MA:295`): 1. Chips @persona y #tema de Resumen semanal (`Semana.kt:131-132`), que **no pasan la consulta** | Campo de búsqueda: pantallas, diario, cartas e índices | Nada | Periodo | — | Semanal |
| `semana` (`MA:378`) | `PantallaSemana`, `ui/pantallas/Semana.kt:64`; título «Resumen semanal» | Toque largo en Vida (`MA:294`): 1. Vida → tarjeta (`Vida.kt:74`): 2. Buscador | La semana contra la anterior | Nada; tocar un día abre el Día (`Semana.kt:87`, `:116`) | Semanas ‹ › | — | Semanal |
| `frases` (`MA:379`) | `PantallaFrasesGuardadas`, `ui/pantallas/FrasesGuardadas.kt:97`; título «Frases guardadas» | Más §1 (`Mas.kt:81`): 2. Buscador | Frases con ♥ | Quitar ♥ (con confirmación) | — | No | Raro |
| `noche` (`MA:380`) | `PantallaNoche`, `Dia.kt:371`; título **«Llenar el día»** (`Dia.kt:416`) | Día → tarjeta «Llenar el día» → «llenar» o «abrir» (`Dia.kt:296`, `:309`, `:361`): 1. Toque largo en Hoy (`MA:291`): 1. «Te falta hoy» → diario o predicción (`Dia.kt:276-277`). Semana corta → «Diario» (`Lote2.kt:537`). Acceso directo «Diario de hoy» y menú ＋ «Diario de hoy» (vía `MA:249`). Buscador «Llenar el día» | La hoja de la noche: hábitos, ánimo y cuerpo, contexto, diario, obras de hoy, foto, predicción, lo opcional, «Cómo estás ahora», sellar | Edita el `Dia` (`Dia.kt:380-404`). Crea el registro «Cómo estás ahora» (`Lote2.kt:504`). Enlace a Privado del día (`Dia.kt:531`) | **No dentro de la pantalla**: usa `Seleccion.noche` (`Dia.kt:373`). Desde el buscador abre **la última fecha que se usó** (ver §4) | No | Diario |
| `entrada` (`MA:381`) | `PantallaEntrada`, `Diarios.kt:260` (`LecturaTema`); **sin barra** | Diario → estante (`Diarios.kt:133`): 2. Línea de tiempo (`Diarios.kt:166`). Biblioteca del diario (`Bibliotecas.kt:222`): 3 | Una entrada a pantalla completa | Editar (`EscribirDia`), PDF, compartir, «Abrir el día» (`Diarios.kt:273-281`) | Navega ‹ › entre entradas | No | Semanal |
| `biblioteca_diario` (`MA:382`) | `PantallaBibliotecas(nav,"diario")`; título «Biblioteca del diario» | Diario → «entrar a la biblioteca →» (`Diarios.kt:134`): 2. Noche (`Dia.kt:489`). Intent `diarios` (`MA:252`, nadie lo envía). Selector. Buscador | Estante de entradas | Abre una entrada | — | — | Semanal |
| `biblioteca_momentos` (`MA:383`) | `PantallaBibliotecas(nav,"momentos")`; título «Libreta de momentos» | Diario → «✦ momentos» (`Diarios.kt:137`): 2. Selector. Buscador | Hojas por día con momentos | Abre una hoja | — | — | Semanal |
| `cinta` (`MA:384`) | `PantallaCinta`, `ui/pantallas/Cinta.kt:69`; título «La cinta» | Día → «abrir la cinta completa →» (`Dia.kt:314`, en la sección plegable «Tu día en 24 horas»): 1 + desplazar. Vida → tarjeta (`Vida.kt:75-76`): 2. Buscador | El día hora por hora | Solo lectura; «abrir este día ›» (`Cinta.kt:344`) | **Sí**, ‹ › (`Cinta.kt:247-252`) | — | Semanal |
| `constelacion` (`MA:385`) | `PantallaConstelacion`, `ui/pantallas/Constelacion.kt:77`; título «Constelación» | Vida → tarjeta (`Vida.kt:81`): 2. Buscador | Tu gente como un cielo | Nada (no tiene `clickable` ni `nav`) | — | — | Raro |
| `campanas` (`MA:387`) | `PantallaCampanas`, `ui/pantallas/Campanas.kt:144`; título «Campañas» | Vida → recuadro (`Campanas.kt:228`): 2. Buscador. **No está en Más** | La campaña de la semana con su mapa; las anteriores | Elegir y aceptar una campaña | No | No | Semanal |
| `recaidas` (`MA:388`) | `PantallaRecaidas`, `ui/pantallas/Recaidas.kt:65`; título **«Privado»** | Más §2 «Privado» (`Mas.kt:96`): 2. Noche → «privado de este día →» (`Dia.kt:531`): 2 + desplazar. Privacidad → «abrir Privado →» (`Sistema.kt:421`, solo con seguimiento privado). Buscador «Privado» (`Pantallas.kt:83`) | Lo privado del día y sus patrones (mecánica: medicamentos, impulsos, recaídas, libido) | Crea por QuickActivity `momento` con tipo (`Recaidas.kt:73-75`), edita y borra con `DialogoMomento` (`Recaidas.kt:72`); medicamentos del día | **Sí**, ‹ › (`Recaidas.kt`; la fecha inicial es `Seleccion.privado`, que se limpia al salir, `:69-70`) | Sí, al borrar un momento | Diario |
| `estado_datos` (`MA:389`) | `PantallaEstadoDatos`, `ui/pantallas/EstadoDatos.kt:155`; título «Estado de los datos» | Más §4 (`Mas.kt:131`): 2. «Datos que faltan» → «estado» (`Hoy.kt:271`). Buscador | Cada fuente en verde, ámbar, rojo o gris | Nada; cada fila lleva a su arreglo (`EstadoDatos.kt:180`) | — | — | Raro |
| `enfoque` (`MA:390`) | `PantallaEnfoque`, `ui/pantallas/EnfoqueSiestas.kt:44`; título «Enfoque y siestas» | Más §1 (`Mas.kt:64`): 2. Vida → tarjeta (`Vida.kt:100`): 2. Buscador. (El botón «◷ enfoque» del Día abre el reloj, no esta ruta: `Dia.kt:304-307`) | Registro de tramos, últimos 30 días | «Empezar o anotar» (reloj, `EnfoqueSiestas.kt:49`); quitar un tramo (`EnfoqueSiestas.kt:101`) | Sí, en el reloj: anotar lo que ya pasó con `SelectorHora` (`ui/EnfoqueActivity.kt:318-326`) | Sí (`EnfoqueSiestas.kt:101`) | Diario |
| `cobertura` (`MA:391`) | `PantallaCoberturaVariables`, `ui/pantallas/Lote2.kt:101`; título «Cobertura de variables» | Análisis → «cobertura de cada variable…» (`Explorar.kt:126`): 2. Más §4 (`Mas.kt:133`). Estado de los datos (`EstadoDatos.kt:199`). Buscador | Días con dato de cada variable | Nada | — | — | Raro |
| `lugar_nuevo` (`MA:392`), alias `lugares_especiales` | `PantallaLugarNuevo`, `Lote2.kt:156`; título «Lugares especiales» | Más §4 (`Mas.kt:135`): 2. Lugares → enlace (`Medir.kt:252`). Aviso «¿Es un lugar especial?» (`Avisos.kt:313`). Buscador. `lugares_especiales`: **nadie lo envía** (es solo el id del canal, `Avisos.kt:24`) | Sitios esperando respuesta, cómo pregunta, sitios ignorados | Guardar el sitio, «ahora no», «no guardar nunca aquí»; quitar un sitio ignorado (`Lote2.kt:208`) | — | No | Raro |
| `notas` (`MA:393`) | `PantallaNotas`, `Lote2.kt:247`; título «Notas» | Más §1 (`Mas.kt:72`): 2. Menú ＋ «Nota» (`QuickActivity.kt:212-218`): 2. Aviso de metas o notas (`Avisos.kt:284`). Buscador | Ideas, propósitos, recordatorios | Crea (`Lote2.kt:268`), edita el texto, fija, marca hecha, borra (`Lote2.kt:289`) | Solo la fecha del recordatorio (`Lote2.kt:264`); la nota **siempre** queda en el día lógico de hoy (`Lote2.kt:268`) | **No** | Semanal |
| `metas` (`MA:394`) | `PantallaMetas`, `Lote2.kt:317`; título «Metas» | Más §1 (`Mas.kt:75`): 2. Aviso de metas (`Avisos.kt:284`). Buscador | Metas por plazo, por revisar, cerradas | Crea, edita, revisa, borra (`Lote2.kt:479`) | Sí, sus fechas (`Lote2.kt:415`, `:457`) | **No** | Mensual |
| `suplementos` (`MA:395`) | `PantallaSuplementos`, `Lote3.kt:80`; título «Suplementos» | Más §1 (`Mas.kt:69`): 2. Aviso de suplementos (`Avisos.kt:298`). Buscador | Cada suplemento activo con las tomas de hoy y su nivel | «Tomé…» (hoy), «Carga ×4», ajustar, agregar del catálogo, hora del recordatorio | **No**: solo hoy (`Lote3.kt:89`, `:97`; `data/Lote3.kt:31`) | Botón propio «deshacer» que borra la última toma de hoy (`Lote3.kt:100`), sin la barra | Diario |
| `laptop` (`MA:396`) | `PantallaLaptop`, `Lote3.kt:240`; título «Laptop» | Más §3 (`Mas.kt:105`): 2. Buscador | Minutos por app y sitio | Importar CSV | — | — | Semanal |
| `red` (`MA:397`) | `PantallaRed`, `Lote3.kt:332`; título «Red» | Más §2 (`Mas.kt:90`): 2. Buscador | Quién conoce a quién | Botón a Tu gente (`Lote3.kt:391`) | — | — | Raro |
| `guia_uso`, `guia_uso:<x>` (`MA:398`) | `PantallaGuiaUso(nav, x)`, `Lote3.kt:512`; título «Guía» | Más §4 «Guía» (`Mas.kt:121`): 2. Buscador «Guía». Botón «?» en el título de las pantallas listadas en `GuiaUso.PANTALLAS` (`data/GuiaUso.kt:85-91`, vía `LocalGuia`, `MA:321`, `ui/Hoja.kt:55-57`) → `guia_uso:<sección>`. «Ver toda la guía» (`nav.reemplazar`, `Lote3.kt:527`) | La guía de uso completa o una sección | Nada | — | — | Raro |

**Cuenta**: 79 cadenas + `guia_uso:<x>`. Cada una está en una fila.

---

## 2. Árbol de navegación

```
ARRANQUE
 ├─ 1.ª vez: pila [mas → permisos("Bienvenido") → conocer]   (MA:238-241); «Empezar» → pestaña dia (Sistema.kt:231)
 └─ normal: pestaña «Hoy» (dia)

BARRA INFERIOR (MA:184-186, MA:193-229; se oculta en entrada, sueno, carta y hoja_momentos, MA:309)
 │  toque en la pestaña activa sin pila: sube al inicio; en Hoy, además, vuelve a hoy (MA:282-285)
 │  toque en otra pestaña: vacía la pila (MA:286)
 │
 ├─ HOY  [id dia → PantallaDia «Día»]               largo → noche de hoy (MA:291)
 │   ├─ 🔍 buscar ── resultados → cualquier ruta de Pantallas.TODAS; diario → Día; cartas → carta
 │   ├─ ⚙ mas ── Más (ver abajo)
 │   ├─ fecha ▾ → calendario ── día → Día (pestaña) · «ir a una fecha…»
 │   ├─ ‹ › / deslizar / «hoy»: cambiar de día dentro de la misma pantalla
 │   ├─ Sueño: [EditarSueno] «corregir las horas de sueño» (Dia.kt:243-246)
 │   ├─ Te falta hoy: ayer · sueño · aviso→QuickActivity ánimo · diario/predicción→noche · escalas · gastos · politica · reaccion
 │   ├─ Datos que faltan: permisos · datos · estado_datos · sincronizar/reconectar (acción en el lugar)
 │   ├─ tarjeta «¿quién es?» → personas
 │   ├─ Tablero + botones: escalas · reaccion · ◷ enfoque (QuickActivity→EnfoqueActivity)
 │   ├─ tarjeta «Llenar el día» → noche «Llenar el día»
 │   │     ├─ «+ agregar hábitos» → habitos
 │   │     ├─ [DialogoEscritor] diario · «abrir la biblioteca del diario» → biblioteca_diario
 │   │     ├─ Lo que leí o vi hoy → buscar_obra · biblioteca
 │   │     ├─ «privado de este día →» → recaidas «Privado»
 │   │     └─ «Sellar el día» → atrás
 │   ├─ Tu día en 24 horas → cinta ── «abrir este día» → Día
 │   ├─ Momentos de este día → hoja_momentos ── «abrir el día» → Día
 │   ├─ Hace un año → Día de esa fecha
 │   ├─ Experimento: «¿Lo hiciste hoy?» (en el lugar)
 │   └─ Lo que registró el teléfono (se pliega solo) → permisos
 │
 ├─ DIARIO [id diario → PantallaDiario «Diario»]    largo → QuickActivity ánimo (MA:292)
 │   ├─ «¿Cómo estás ahora?» → QuickActivity ánimo
 │   ├─ Hoy: [EscribirDia] (solo hoy)
 │   ├─ estante → entrada ── [EscribirDia] · «Abrir el día» → Día
 │   ├─ «entrar a la biblioteca» → biblioteca_diario ⇄ biblioteca_suenos ⇄ biblioteca_momentos ⇄ cartas (selector y deslizar = reemplazar)
 │   │     ├─ biblioteca_suenos → sueno ── [EscribirSueno] · «Abrir el día»
 │   │     ├─ biblioteca_momentos → hoja_momentos
 │   │     └─ cartas → carta_nueva [DialogoCarta] · carta (borrar con Deshacer)
 │   ├─ Línea de tiempo (plegada): [DialogoMomento] · día → Día · entrada
 │   └─ Exportar (plegado)
 │
 ├─ ＋ REGISTRAR [id registrar: no es pestaña; abre QuickActivity «menu»] (MA:281)   largo → QuickActivity ánimo (MA:293)
 │   └─ Ánimo · Enfoque · Siesta · Sustancias · Gasto · Sueño · «Diario de hoy»→noche · «Nota»→notas · Impulso · [Recaída · Libido] · Tomé <suplemento>
 │
 ├─ VIDA [id vida → PantallaVida «Vida»]            largo → vida + semana (MA:294)
 │   ├─ tarjetas: semana · cinta · calendario · anno · personas · constelacion · totales · mapa
 │   ├─ recuadros: tu_anio(→carta_nueva) · logros · campanas · mapas · obras · otras_vidas
 │   │     ├─ mapas → mundo(→rascar, unesco, ciudad_nueva) · rascar · unesco · mapa · lugares(→mapa, lugar_nuevo) · ciudad_nueva
 │   │     └─ obras → buscar_obra · biblioteca(→buscar_obra, obra) · obra(→buscar_obra)
 │   └─ Dinero y sustancias: gastos · sustancias · enfoque
 │
 └─ ANÁLISIS [id entender → PantallaExplorar «Análisis»]   largo → buscar (MA:295)
     ├─ tarjetas: experimentos · totales
     ├─ Herramientas (8) → analisis «Análisis» ── días atípicos → Día
     ├─ cobertura · «Ver menciones… en Totales» → totales · fichas → analisis
     └─ Lugares y ánimo → lugares

MÁS (⚙)  [AtajosFijos ★ arriba, Mas.kt:54]
 ├─ §1 Registros especiales: escalada(→ascenso) · fuerza «Gimnasio» · enfoque · habitos · suplementos · notas · metas · cartas · biblioteca_suenos · frases(→obra)
 ├─ §2 Tu gente y sustancias: personas(→presencias) · red(→personas) · temas · sustancias · recaidas «Privado»
 ├─ §3 Medir: salud · tus_apps · laptop · escalas · reaccion · politica · conocer
 └─ §4 Sistema: guia_uso(→guia) · apariencia(→fondo_telefono) · avisos · privacidad(→recaidas) · datos · estado_datos(→permisos, datos, privacidad, cobertura) · cobertura · lugar_nuevo(→lugares) · reportes · permisos · guia

DIÁLOGOS PRINCIPALES (no son rutas)
 DialogoMomento (Momentos.kt:107): se abre desde momentos, diario y recaidas (Momentos.kt:100, Diarios.kt:182, Recaidas.kt:72).
   No se abre desde la línea de 24 h del Día, la cinta, la libreta ni «Momentos de este día».
   Solo cambia la hora si es una recaída (Momentos.kt:118, :142-147).
 EditarSueno (ui/EditarSueno.kt:33): solo desde el bloque Sueño del Día (Dia.kt:246).
 Ficha de persona (Gente.kt:209, :262) · Marcar este lugar (Mapa.kt:520-545)
 Nota (Lote2.kt:286-290) · Meta (Lote2.kt ~415-479)
 ElegirVariable (Analisis.kt:126) · DialogoNuevaClave (Respaldo.kt:183) · DetalleTrofeo (Trofeos.kt:330)
```

---

## 3. Entradas desde fuera de la app

### 3.1 Cómo se interpreta el extra `pantalla` (`MainActivity.PANTALLA`, `MA:106`; `Raiz`, `MA:243-256`)

| Valor | Qué abre |
|---|---|
| `dia` | Pestaña Hoy con `Seleccion.pedida = hoy` (`MA:247`, `:254`) |
| `diario` | **Hoja de la noche de hoy**, en «Ánimo y cuerpo» (`MA:249`). No abre la pestaña Diario |
| `dia:<AAAA-MM-DD>` | Pestaña Hoy en esa fecha (`MA:250`) |
| `momentos` | Pestaña Diario (`MA:251`). Nadie lo envía |
| `diarios` | Pestaña Diario + `biblioteca_diario` (`MA:252`). Nadie lo envía |
| `explorar`, `entender` | Pestaña Análisis (`MA:253`) |
| `vida` u otra pestaña | Esa pestaña (`MA:254`) |
| Cualquier otra cadena | Pestaña Hoy + pila `[valor]` (`MA:254`). Si no está en el `when`, sale «Pantalla desconocida: …» (`MA:399`) |

### 3.2 Widgets (`widget/Widgets.kt`; los colores salen de `Piel.kt`, que no navega)

| Widget (receptor, `AndroidManifest.xml`) | Zona tocable | Qué hace |
|---|---|---|
| Ánimo ahora (`vistaAnimo`, `Widgets.kt:141`) | 7 botones `a1..a7` (`:151`) | QuickActivity `animo` con `VALENCIA=i+1`: **se guarda al tocar** como «animo» (`QuickActivity.kt:101`); luego abre el formulario. Ahí **no hay «Deshacer»**, porque el registro venía prellenado (`QuickActivity.kt:318`) |
| Momento (`vistaMomento`, `:156`) | 8 sentimientos `m1..m8` (`:167`) | QuickActivity `momento` + `TIPO`: guarda un registro de ánimo con esa emoción (`QuickActivity.kt:88-99`) |
| | «otro…» `m_mas` (`:169`) | QuickActivity `menu` |
| | «✎ diario» `m_diario` (`:170`) | `abrir(ctx,"dia")` → **el Día**, no la hoja de la noche (ver §4) |
| Hoy (`vistaHoy`, `:298`) | Todo el widget `raiz_hoy` (`:321-322`) | `"dia"` |
| Gasto (`vistaGasto`, `:180`) | 6 categorías `g1..g6` (`:197`) | QuickActivity `gasto` con la categoría |
| | «+ otro monto» `g_otro` (`:200`) | QuickActivity `gasto` |
| Sueño (`vistaSueno`, `:206`) | Calidad `s1..s5` (`:219`) | QuickActivity `sueno` con `CALIDAD`: **se guarda al tocar** en el día lógico de hoy (`QuickActivity.kt:105-112`) |
| | «☾ recuerdo un sueño» `s_sueno` (`:221`) | QuickActivity `sueno` con `RECORDADO` |
| Hábitos (`vistaHabitos`, `:227`) | Cada hábito (`:243-244`) | Broadcast a `HabitoReceiver` (`:515-525`): marca o desmarca **hoy** sin abrir la app |
| | Sin hábitos: todo el widget (`:246`) | `"habitos"` |
| Mes (`vistaMes`, `:251`) | Todo el widget `raiz_mes` (`:283`) | `"calendario"` |
| Hoy en anillos (`vistaAnillos`, `:327`) | `raiz_anillos` (`:358`) | `"dia"` |
| Foto del día (`vistaFoto`, `:363`) | `raiz_foto` | `"dia:<fecha de la foto>"` (`:381`) o `"dia"` (`:386`) |
| Una cifra (`vistaCifra`, `:407`) | `raiz_cifra` (`:417`) | Broadcast a `CifraReceiver` (`:552-559`): pasa a la cifra siguiente. No abre la app |
| Enfoque (`vistaEnfoque`, `:422`) | `raiz_enfoque` (`:449`) | QuickActivity `enfoque` → EnfoqueActivity |

### 3.3 Notificaciones (`avisos/Avisos.kt`; `abrirApp`, `:124-127`; `quick`, `:118-122`)

| Aviso (función:línea) | Al tocarlo | Acciones |
|---|---|---|
| ¿Cómo te sientes ahora? (`muestreo`, `:129`) | QuickActivity `animo` con `AVISO_MS` (se guarda como «muestreo») | — |
| ¿Recuerdas lo que soñaste? (`sueno`, `:142`) | QuickActivity `sueno` | — |
| ¿Cómo terminó lo de anoche? (`loDeAnoche`, `:154`) | QuickActivity `consumo` con el id (`CompletarConsumo`) | — |
| ¿Cómo estuvo tu día? (`diario`, `:168`) | **`"dia"`** (`:173`): abre el Día, no la noche | «Día difícil: solo un número» → QuickActivity `minimo` (`:174`) |
| Cuestionarios (`escalas`, `:179`) | `"escalas"` (`:191`) | — |
| Carta del pasado (`carta`, `:197`) | `"cartas"` (`:202`) | — |
| Días especiales (`especial`, `:208`) | `"personas"` (`:215`) | — |
| Reporte listo (`reporte`, `:220`) | Abre el archivo (ACTION_VIEW, `:222`) | — |
| Logros (`logros`, `:232`) | `"logros"` (`:238`) | — |
| Tu año (`tuAnio`, `:244`) | `"tu_anio"` (`:249`) | — |
| Toca importar (`importar`, `:255`) | `"datos"` (`:265`) | «Importar ahora» → `"datos"` (`:266`). «Recordar mañana» → `RecordatorioReceiver` MANANA (`:258`, `Recordatorios.kt:106-110`) |
| Metas y notas (`metas`, `:273`) | `"metas"`, o `"notas"` si no hay metas (`:284`) | — |
| Suplementos (`suplementos`, `:290`) | `"suplementos"` (`:298`) | «Ya los tomé» → TOME: registra la dosis de los que faltan **hoy** (`Recordatorios.kt:111-116`). «En una hora» → UNA_HORA (`:117-122`). Ninguna se puede deshacer desde el aviso |
| ¿Es un lugar especial? (`lugar`, `:306`) | `"lugar_nuevo"` (`:313`) | — |
| Respaldos en pausa / Drive (`:320`, `:331`, `:342`) | `"datos"` (`:325`, `:337`, `:347`) | — |
| Cronómetro de enfoque (`data/Enfoque.kt:240`) | EnfoqueActivity (`Enfoque.kt:225-227`) | Empezar, Reanudar, Saltar descanso, Pausar, Terminar → `EnfoqueReceiver` (`Enfoque.kt:229-230`, `:244-256`) |
| Fin de bloque (`Enfoque.kt:266`) | (no se revisó el intent) | — |

### 3.4 Accesos directos del ícono (`app/src/main/res/xml/accesos.xml`)

Están enlazados en **22 `activity-alias`**, uno por tema (`AndroidManifest.xml:82-356`). Todos apuntan a `.ui.MainActivity` y cargan `@xml/accesos`.

| Id | Destino | Extra |
|---|---|---|
| `momento` («Registrar un momento») | QuickActivity | `modo=menu` |
| `gasto` | QuickActivity | `modo=gasto` |
| `diario` («Diario de hoy») | MainActivity | `pantalla=diario` → noche de hoy |
| `animo` | QuickActivity | `modo=animo` |
| `sueno` | QuickActivity | `modo=sueno` |

No hay accesos dinámicos: grep de `ShortcutManager` sin resultados.

### 3.5 Mosaicos de ajustes rápidos (`widget/Mosaicos.kt`; servicios `QS_TILE`, `AndroidManifest.xml:496-514`)

- Momento → QuickActivity `menu` (`:33`).
- Gasto → `gasto` (`:34`).
- Ánimo → `animo` (`:35`).
- Enfoque → `enfoque` (`:38`); se enciende mientras corre.
- **Suplementos** → registra la dosis habitual de los que faltan hoy **sin abrir nada** y sin deshacer (`:51-75`).

### 3.6 QuickActivity (`ui/QuickActivity.kt`)

El modo llega en el extra `modo` (`:64-70`). Sin modo se usa `menu` (`:77`). En ningún caso se elige el día del registro salvo donde se dice.

| Modo | Qué hace | ¿Elegir fecha? | Deshacer |
|---|---|---|---|
| `animo` (`Animo`, `:253`) | Cuadrícula valencia × energía; se guarda al primer toque (`asegurar`, `:264-268`). Luego emociones, pensamiento, nota, con quién, y botones + sustancia, + impulso (+ recaída, + libido con seguimiento privado) | **No**: ahora y `Fechas.diaLogico()` (`:165`) | «Deshacer» borra el registro si se creó en esta apertura y no venía prellenado (`:318-319`) |
| `momento` | Con `tipo` de sentimiento o pensamiento: guarda un registro de ánimo y pasa a `animo` (`:88-99`). Con `impulso`, `recaida` o `libido`: pantalla «privado» (`MomentoCaptura`, `:349`) | Solo la recaída cambia la hora («otra fecha u hora…», `:428`) | «Deshacer» (`:369`, `:402`) |
| `menu` (`Registrar`, `:201`) | Ánimo, Enfoque, Siesta, Sustancias, Gasto, Sueño, «Diario de hoy», «Nota», Impulso, [Recaída, Libido] (`:210-213`); botones «Tomé <suplemento>» (`:226-241`) | — | Las tomas desde aquí no se deshacen |
| `gasto` (`GastoRapido`, `:620`) | Monto, categoría, hora | **Sí**: `SelectorHora` con «otra fecha u hora…» (`:636`, `:648`) | No |
| `sueno` (`SuenoRapido`, `:665`) | Texto del sueño, calidad y detalles | **No**: día lógico de hoy (`:666`) | No |
| `minimo` (`Minimo`, `:698`) | «Día difícil»: solo la valencia del día | **No**: hoy (`:699`) | No |
| `enfoque`, `siesta` | Abren EnfoqueActivity y se cierran (`:79`, `:146`, `:456`) | En EnfoqueActivity, «anotar lo que ya pasó» con `SelectorHora` (`EnfoqueActivity.kt:318-326`) | — |
| `consumo` (`ConsumoRapido`, `:475`; `CompletarConsumo`, `:570`) | Nuevo episodio, o completar o editar uno con `consumo_id` | **Sí**: `SelectorHora` (`:520`, `:582-584`) | «borrar» + `Deshacer.ofrecer` (`:606`), pero la barra no se ve dentro de QuickActivity |

### 3.7 Otras actividades

- `EnfoqueActivity` (`ui/EnfoqueActivity.kt:67`, `exported=false`): el reloj a pantalla completa. Se abre desde QuickActivity y desde la notificación del cronómetro. Solo se cierra con ✕ (`:176`, `:268`); no navega a la app.
- `PrivacidadActivity`: la explicación de Health Connect (`AndroidManifest.xml`, intent `ACTION_SHOW_PERMISSIONS_RATIONALE`). No navega a rutas.

### 3.8 Nombres que usa alguien de fuera o que quedan guardados (renombrarlos rompe algo)

- **Rutas en `pantalla`**:
  - `dia` y `dia:<fecha>`: widgets `Widgets.kt:170`, `:321`, `:358`, `:381`, `:386`; aviso `Avisos.kt:173`.
  - `diario`: `accesos.xml` y `QuickActivity.kt:211`, `:218`.
  - `notas`: `QuickActivity.kt:212`, `:218` y `Avisos.kt:284`.
  - `habitos` (`Widgets.kt:246`), `calendario` (`Widgets.kt:283`).
  - `escalas`, `cartas`, `personas`, `logros`, `tu_anio`, `datos`, `metas`, `suplementos`, `lugar_nuevo` (todas en `Avisos.kt`).
- Las que `Raiz` trata aparte: `momentos`, `diarios`, `explorar`, `entender` (`MA:251-253`).
- **Una notificación ya mostrada guarda su `PendingIntent`**: si cambia el nombre de la ruta y la notificación sigue en la bandeja, al tocarla sale «Pantalla desconocida».
- **Rutas guardadas en los datos**: `config.favoritos_mas` guarda las rutas fijadas con ★ (`data/Modelo.kt:710`; se usa en `Pantallas.kt:129-145`) y **viaja en los respaldos** (`data/Importar.kt:142`). Renombrar una ruta fijada hace desaparecer el atajo sin aviso (`Pantallas.de` devuelve null).
- **Modos de QuickActivity**: `menu`, `gasto`, `animo`, `sueno` (en `accesos.xml`), y `animo`, `momento`, `menu`, `gasto`, `sueno`, `enfoque` (widgets y mosaicos), y `minimo`, `consumo` (avisos). También los nombres de los extras (`modo`, `pantalla`, `tipo`, `valencia`, `categoria`, `calidad`, `recordado`, `consumo_id`, `aviso_ms`, `notif`, `origen`).
- **Claves de `GuiaUso.PANTALLAS`**: son títulos de pantalla, no rutas (`GuiaUso.kt:85-91`). Cambiar un título apaga su «?».

---

## 4. Señalamientos

### 4.1 Rutas huérfanas y alias muertos

Cada afirmación sale de `grep -rn '"<ruta>"'` en todo `app/src/main/java` (y, para los accesos, en `res/xml` y en el manifiesto).

- **`momentos` (`PantallaMomentos`)**: huérfana del todo. No hay `nav.ir`, no está en Más ni en el buscador, y el intent externo `momentos` se desvía a la pestaña Diario (`MA:251`). Nadie lo envía. Era la pestaña antigua: no tiene botón atrás (`Momentos.kt:70`). Lo que hacía (lista de 30 días con edición) solo vive ahora en la línea de tiempo plegada de Diario.
- **`hall`**: solo la usa `RecuadroHall` (`Hall.kt:500-507`), y **nadie llama** a `RecuadroHall`. `PantallaHall` (`Hall.kt:365`) tampoco se usa; la ruta va a `PantallaLogros`, que ya trae el hall dentro (`Logros.kt:176`). Es código muerto.
- **`diarios`, `ajustes`, `lugares_especiales`, `explorar`**: alias del `when` que nadie envía, ni dentro ni fuera de la app. Además, `diarios` y `explorar` los intercepta `Raiz` antes de llegar al `when`.
- **Código muerto relacionado**:
  - `CentroEntender` (`Explorar.kt:432`): tarjetas de semana, calendario, año, totales, mapa, experimentos, cinta y constelación. No se llama.
  - `Portada` (`Hoy.kt:618`): no se llama.
  - La rama `Seleccion.seccion == "Sueño"` de `PantallaDia` (`Dia.kt:144-147`): nadie asigna «Sueño» (solo `"Ánimo y cuerpo"`, `MA:249`, `:275`).
- **Solo por buscador** (no están en Más ni en otro enlace de menú): ninguna en sentido estricto. Pero `semana`, `calendario`, `cinta`, `constelacion`, `anno`, `mapa`, `totales`, `tu_anio`, `logros`, `campanas`, `experimentos`, `mapas`, `obras`, `otras_vidas` y `gastos` **no están en Más**: solo se llega por Vida o Análisis, o por el buscador. Si el usuario busca en Más, no las encuentra.
- **Sin buscador y sin Más** (solo por enlaces dentro de otras pantallas): `lugares`, `mundo`, `rascar`, `unesco`, `ciudad_nueva`, `biblioteca`, `obra`, `buscar_obra`, `ascenso`, `analisis`, `carta`, `carta_nueva`, `sueno`, `entrada`, `hoja_momentos`, `fondo_telefono`. En el buscador, «lugares» o «mundo» no devuelven la pantalla.
  - `lugares` es la más escondida: Vida → Mapas → Lugares (3 toques), o el final de Análisis.
  - `PantallaLugares` es donde se borran lugares (E-18).
- **Solo por notificación**: ninguna. Todas las rutas que abren los avisos también tienen camino dentro.

### 4.2 Más de un camino con nombres distintos

| Ruta | Nombres que ve el usuario |
|---|---|
| `noche` | Título «Llenar el día»; tarjeta del Día «Llenar el día» (`Dia.kt:356`); acceso directo y menú ＋ «**Diario de hoy**»; toque largo en «Hoy»; «Te falta hoy» → «Diario de hoy» (`Hoy.kt:114`); guía «Hoja de la noche»; en Más se le dice «la hoja de la noche» (`Mas.kt:66`, `:144`) |
| `dia` | Pestaña «**Hoy**»; título «**Día**»; buscador «Día · El día de hoy» (`Pantallas.kt:37`); widget «✎ diario» y aviso «Llena tu diario» también abren el Día |
| `diario` (pestaña) | «Diario». Pero el extra `pantalla=diario` abre la noche (`MA:249`) y la noche tiene una sección «Diario» (`Dia.kt:477`) |
| `obras` / `biblioteca` | `obras`: título «Obras», buscador «**Biblioteca**» (`Pantallas.kt:73`). `biblioteca`: título «**Obras**» (`Biblioteca.kt:190`), botón «lista y pendientes» (`Galerias.kt:362`) o «ver obras» (`Biblioteca.kt:618`). Dos pantallas con el mismo título y nombres cruzados. Además «Biblioteca del diario» y «Biblioteca de sueños» son otras pantallas |
| `fuerza` | Título, Más y buscador: «Gimnasio» |
| `recaidas` | Título, Más y buscador: «Privado»; en la noche, «privado de este día»; en `GuiaUso.PANTALLAS` y `NombresNuevos` todavía es «Impulsos y recaídas» |
| `analisis` y `entender` | Las dos se titulan «Análisis» (`Analisis.kt:101`, `Explorar.kt:101`) |
| `presencias` | Título «¿Estuviste o pensaste?»; fila en Tu gente «¿Estuviste o **solo** pensaste?» (`Gente.kt:159`) |
| `ciudad_nueva` | Mosaico «Ciudades visitadas» (`Galerias.kt:313`, con el ícono de «mundo»); botón «Añadir una ciudad»; título «Añadir ciudad» |
| `mapa` / `mapas` / `mundo` | «Mapa» (tarjeta de Vida y título) / «Mapas» (recuadro) / «Tus calles» (mosaico que abre `mapa`) / «Mundo». Rutas casi homónimas |
| `anno` / `tu_anio` | «Tu año» / «Tu año en historias», las dos en Vida |
| `lugar_nuevo` | Título y Más «Lugares especiales»; el aviso dice «¿Es un lugar especial?» |
| `semana` | «Resumen semanal»; toque largo en Vida; en Más, «Vida, el resumen semanal» (`Mas.kt:144`) |
| `escalas` | «Cuestionarios»; botón «✎ cuestionarios»; la ruta se llama `escalas` |
| `apariencia` | Más «Apariencia»; el ícono ⚙ dice «Ajustes y más» (`Decoracion.kt:1368`) pero abre **Más**, no Apariencia |

### 4.3 Títulos que no coinciden con la ruta, con Más o con el buscador

- Ruta ≠ título: `dia`→«Día» (pestaña «Hoy»), `entender`→«Análisis», `personas`→«Tu gente», `escalas`→«Cuestionarios», `fuerza`→«Gimnasio», `recaidas`→«Privado», `noche`→«Llenar el día», `anno`→«Tu año», `biblioteca`→«Obras», `obras`→«Obras» (buscador «Biblioteca»), `lugar_nuevo`→«Lugares especiales», `ciudad_nueva`→«Añadir ciudad», `rascar`→«Mapa para rascar», `unesco`→«Patrimonio Mundial», `buscar`→«Buscar» (es `PantallaTotales`), `semana`→«Resumen semanal», `presencias`→«¿Estuviste o pensaste?», `guia_uso`→«Guía» y `guia`→«Guía científica» (dos «Guía»), `permisos`→«Bienvenido» la primera vez.
- El «?» de la guía no sale donde debería:
  - **Día**: usa un encabezado propio, y con `encabezado` `Hoja` no pinta el «?» (`Hoja.kt:43-57`).
  - **Privado**: la clave sigue siendo «Impulsos y recaídas» (`GuiaUso.kt:87`).
  - **Momentos**: la pantalla es huérfana.
  - En cambio, «Análisis» muestra el «?» en las dos pantallas.

### 4.4 Pestaña «Hoy» (id `dia`) frente al «Día» de una fecha

- La pestaña se llama «Hoy» (`MA:185`), pero la pantalla se titula «Día» (`Dia.kt:169`) y **puede mostrar cualquier fecha**:
  - flechas, deslizar y calendario (`Dia.kt:123`, `:153-162`, `:177`);
  - `Seleccion.abrir` lleva **cualquier** fecha a esa pestaña (`Calendario.kt:78`).
- La fecha abierta se recuerda en `Seleccion.diaAbierto` (`Dia.kt:103-105`). Al volver a «Hoy» desde otra pestaña, `tocar` solo reinicia a hoy si ya estabas en Hoy y sin pila (`MA:282-286`). **La pestaña «Hoy» puede abrir en ayer o en otra fecha**, con todo editable. Solo lo avisan la fecha del encabezado y el botón «hoy» (`Dia.kt:208`).
- `Seleccion.abrir` usa `nav.pestana("dia")`, que **vacía la pila**. Al abrir un día desde Calendario, Entrada, Cinta, Libreta, Semana, Tu año, Gimnasio, Atípicos o el buscador, se pierde el camino de vuelta. El «atrás» del sistema con la pila vacía ya no lo atrapa `BackHandler` (`MA:260`): **sale de la app** (comportamiento estándar de Android; no se comprobó en un teléfono).
- `Pantallas.TODAS` ofrece «Día · El día de hoy». Ese resultado hace `nav.ir("dia")`: **apila** una segunda `PantallaDia` sobre la pestaña, con la misma `diaAbierto`, y la barra queda sin pestaña marcada.
- Lo mismo pasa con «Vida» y «Análisis» desde el buscador o los ★ (`Pantallas.kt:46-47`): se apilan pantallas de pestaña **sin botón atrás** (`Vida.kt:72`, `Explorar.kt:101`).
- **«Diario de hoy» y «diario»**:
  - El acceso directo y el menú ＋ abren la **noche** (`MA:249`).
  - El widget «✎ diario» (`Widgets.kt:170`) y el aviso «¿Cómo estuvo tu día? Llena tu diario» (`Avisos.kt:173`) abren **el Día**. El comentario de `MA:248` dice que «los avisos de la noche» abren la hoja; el código del aviso no lo hace.
  - La pestaña «Diario» es otra cosa: biblioteca y línea de tiempo.
- **Fechas que se arrastran** entre usos (no se limpian al salir):
  - `Seleccion.noche`: el buscador «Llenar el día» abre la noche de la **última** fecha que se llenó, no la de hoy (`Dia.kt:373`; solo se asigna en `Dia.kt:138` y `MA:249`, `:275`).
  - `Seleccion.cinta` (`Cinta.kt:70-71`): la tarjeta de Vida dice «cosas con hora **hoy**», pero puede abrir otro día.
  - `Seleccion.momentos` y `Seleccion.entrada`: lo mismo.
  - En cambio, `Seleccion.privado` sí se limpia (`Recaidas.kt:70`).
- La línea de 24 h del Día, la cinta, la libreta y «Momentos de este día» **no abren `DialogoMomento`**. Para corregir un momento visto en Hoy hay que ir a Diario → Línea de tiempo (plegada) → buscar el día → tocar el momento. Y solo se cambia la hora si es una recaída (`Momentos.kt:118`, `:142-147`).

### 4.5 Callejones sin salida y salidas escondidas

- **Sin botón atrás visible** (solo el gesto del sistema o la barra):
  - `gastos` (`Gastos.kt:91`) y `momentos` (`Momentos.kt:70`), que fueron pestañas;
  - `dia`, `vida` y `entender` cuando se apilan desde el buscador o un ★.
- **Sin enlaces útiles salvo atrás**: `constelacion` (no tiene `clickable` ni `nav`), `salud`, `tus_apps`, `otras_vidas`, `cobertura`, `guia`, `reportes`, `laptop`, `escalas`, `reaccion`, `politica`, `conocer`, `habitos`, `suplementos`, `notas`, `metas`, `campanas`, `logros`, `experimentos`, `temas`, `presencias`, `frases`, `unesco`, `rascar`, `ciudad_nueva`, `fondo_telefono`, `avisos`, `apariencia`. Varias deberían llevar a su dato del día. Por ejemplo, Suplementos no deja anotar en otra fecha, y Experimentos no lleva al Día, donde se responde «¿Lo hiciste hoy?».
- `PantallaEscalas` abre cada cuestionario como otra `Hoja` dentro de la misma ruta (`Medir.kt:115`): el «atrás» del sistema sale de Cuestionarios, no del cuestionario. Es probable por el código; **no se comprobó**.
- Los chips @persona y #tema de Resumen semanal (`Semana.kt:131-132`) abren el buscador **vacío**: se pierde lo que se tocó.
- El toque largo de las pestañas solo se explica en un texto al final de Más (`Mas.kt:144`) y en las etiquetas de accesibilidad (`MA:201`, `:215`): es **escondido**.

### 4.6 Lo que no se pudo comprobar

- La altura real de Más y de Día: qué secciones quedan bajo el pliegue y cuántos desplazamientos hacen falta.
- Si la barra de Deshacer que se ofrece desde QuickActivity aparece después en la app.
- El efecto de «atrás» con la pila vacía en el teléfono del autor.
- El texto exacto que cada tema pone en los títulos: `Estilo.pantalla` y `NombresNuevos` cambian el nombre visible.
- Una observación para la revisión de privacidad, no de navegación: el buscador de pantallas indexa «Privado» con las palabras «impulso recaída libido medicamentos» (`Pantallas.kt:83`), y el menú ＋ muestra «Impulso» (`QuickActivity.kt:212`). Son nombres de función, no datos, pero conviene que la revisión de privacidad confirme que lo permite `data/Privado.kt` y `PrivadoPrueba`.
