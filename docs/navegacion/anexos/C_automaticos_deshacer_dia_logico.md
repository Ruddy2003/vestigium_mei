# Anexo C · Datos automáticos, deshacer, día lógico, DialogoMomento y Seleccion

Revisión hecha leyendo el código vigente (27 oct 2026), sin compilar ni ejecutar. Rutas relativas a
`app/src/main/java/io/github/ruddy2003/vestigium/`. «Toques» se cuentan desde que la app abre en Hoy
(`PantallaDia`, `MainActivity.kt:235`, pestaña `dia`), sin contar desplazamientos. Las selecciones de fecha
y hora de Android (`DatePickerDialog` y `TimePickerDialog`, que abre `SelectorHora`, `ui/Selectores.kt:51-55`) cuentan
como 2 toques de «Aceptar», más los giros del reloj.

Las búsquedas de entradas cubrieron: `onClick`, `clickable`, `combinedClickable`/`onLongClick`, `pointerInput`/`detectTapGestures`,
diálogos, el buscador (`ui/pantallas/Pantallas.kt:10-90`), `Mas.kt`, los widgets (`widget/`), las notificaciones (`avisos/`)
y `QuickActivity`.

---

## 0. Correcciones al punto de partida del plan (§4)

1. **`PantallaMomentos` no se puede abrir.** La ruta `"momentos"` existe en el `when` (`ui/MainActivity.kt:325`), pero
   nadie llama a `nav.ir("momentos")`. La única entrada, por intent, se desvía a la pestaña Diario (`MainActivity.kt:251`:
   `p == "momentos" → pestana = "diario"`). Tampoco está en el buscador ni en Más. Así que `DialogoMomento` **no** se abre
   «desde Momentos»: es código muerto en la práctica (`ui/pantallas/Momentos.kt:56-101`).
2. **EditarSueno no corrige siestas.** Solo maneja la noche: hora de dormir, hora de despertar y minutos despierto
   (`ui/EditarSueno.kt:33-82`). La siesta del reloj (`siesta_reloj_min`) no se puede editar en ninguna parte.
3. La lista de «tipos de datos» del §4 dice «Lugares, visitas y lugares ignorados». Las **visitas** no tienen ninguna
   operación desde la interfaz: ni ver una por una, ni corregirla, ni borrarla.

---

## 1. Datos automáticos: ¿se pueden corregir, ignorar o borrar?

### 1.1 Resumen

| Fuente | ¿Corregir? | ¿Ignorar o excluir? | ¿Borrar? | ¿La sincronización respeta lo corregido? | Dónde / toques |
|---|---|---|---|---|---|
| Sueño de la noche (Health Connect) | **Sí**: dormí, desperté, despierto en medio (`EditarSueno.kt:52-57`) | Solo «volver a lo del reloj» | No directamente (se puede «volver al reloj») | **Sí**: con `sueno_manual` la lectura solo actualiza `sueno_fuentes` (`sensores/Salud.kt:186`) | Hoy → sección Sueño → «corregir las horas de sueño» (`Dia.kt:243-246`). De 5 a 8 toques, ver 1.2 |
| Siesta del reloj (`siesta_reloj_min`) | **No** | **No** | **No** | Se recalcula en cada lectura (`Salud.kt:223, 230`). Si la noche está corregida, se **congela** en el último valor (el `return@intentar` de `Salud.kt:186` sale antes de recalcularla) | Solo se ve: `Dia.kt:605` («siesta = … según el reloj»), al fondo de «Lo que registró el teléfono» |
| Siesta anotada o cronometrada (`Tramo` con tipo «siesta») | No se edita (solo borrar y volver a crear) | — | **Sí**, con Deshacer (`EnfoqueSiestas.kt:101`) | No le afecta (es manual) | Más (1) → «Enfoque y siestas» (2) → «quitar» (3). En Hoy, `TramosDelDia` (`Dia.kt:832-847`) solo se ve |
| Etapas del sueño | Se **borran** al cambiar las horas a mano (`EditarSueno.kt:72-76`) | — | — | Igual que el sueño | — |
| Pasos, ritmo, HRV, SpO₂, peso, etc. (Health Connect) | **No** | **No** | **No** | Se sobrescriben en cada lectura: los últimos 3 días cada mañana, 1 día a las 21 h (`avisos/Trabajo.kt:171, 189`) y al tocar «↻ sincronizar» (`Dia.kt:729-731`) | Solo se ven (`Dia.kt:596-612`) |
| Sesiones de ejercicio del reloj (`Sesion`) | **No** | **No** | **No** | Se reescriben con el mismo id de Health Connect (`Salud.kt:346-353`) | Solo se ven (`Dia.kt:617`). En Escalada se pueden enlazar con una sesión propia (`Escalada.kt:84-88`) |
| Pantalla (uso de apps, desbloqueos) | **No** | **No** (solo se excluyen los lanzadores y la propia app, `Pantalla.kt:46`) | **No** | Se recalcula (`Pantalla.kt:142-146`) | Solo se ve (`Dia.kt:623-628`) |
| Notificaciones por app | **No** | **No** (sin lista de apps excluidas en `Oyente.kt`) | **No** | Contador acumulado (`Repo.contarNotif`) | Solo se ve |
| Música detectada por notificación (`Escucha`) | **No** | **No** | No a mano. Se **reemplaza sola** al importar el historial de Spotify (`Importaciones.kt:101-105`) o al leer la API (`Spotify.kt:255`) | — | Solo se ve |
| Ubicación: lugares guardados (`Lugar`) | Solo el **tipo** (`Medir.kt:288-290`). **No** el nombre, el radio ni la posición | — | **Sí**, en 2 toques y con Deshacer (`Medir.kt:295`) | — | Ver 1.4 |
| Ubicación: visitas (`Visita`) | **No** | **No** | **No** (al borrar el lugar, sus visitas quedan huérfanas) | Las crea la geocerca (`Ubicacion.kt:101-102`) o `Lugares.reconstruir` (`Lugares.kt:113`) | No hay pantalla de visitas |
| Ubicación: sitios candidatos | — | **Sí**: «no guardar nunca aquí» (150 m, `Lugares.kt:93-98`, botón en `Lote2.kt:186`) | «quitar» de la lista de ignorados (`Lote2.kt:208`, sin confirmar ni deshacer) | — | Notificación «¿Es un lugar especial?» (`Avisos.kt:313`) → «no guardar nunca aquí»: 1 toque. Desde Hoy: Más (1) → «Lugares especiales» (2) → botón (3), solo si hay un candidato pendiente |
| Fotos con lugar (catálogo de `FotosLugar`) | **Sí, la mejor resuelta**: qué aparatos son tuyos, relojes mal puestos, fotos compartidas, y «sí estuve / no cuenta» en fotos raras (`Mundo.kt:544-631`) | **Sí** | Se rehace con `FotosLugar.aplicar` (`FotosLugar.kt:320-340`) | Sí: las decisiones se guardan en `Config` (`fotos_revisadas`, `fotos_aparatos`) | Pestaña Vida → Mundo (ruta `mundo`) → «De dónde vienen tus fotos» |
| Países y patrimonio que salen de ubicaciones y fotos | Países automáticos: «no cuenta (solo pasé)» (`Mundo.kt:763`). Patrimonio: «no cuenta» (`Mundo.kt:931-933`) | Sí | Las **ciudades automáticas no**: solo se quitan las que anotaste a mano (`Mundo.kt:430-440`) | Sí (listas en `Config`) | Mundo / Rascar / Unesco |
| Fotos del día (conteo de la galería) | **No** | **No** | **No** | Se recuenta (`Galeria.kt:74-84`) | Solo se ve |
| Agenda (eventos del calendario) | **No** | **No** (no hay filtro por calendario) | **No** | Se recalcula para 4 días (`Agenda.kt:67-70`) | Solo se ve |
| Llamadas y SMS | **No** | **No** | **No** | Se recalcula (`Comunicacion.kt:70-72`) | Solo se ve |
| Chats de WhatsApp importados | **No** | **No** | **No** (volver a importar reemplaza por id `wa_<hash(chat|fecha)>`, `Importaciones.kt:450`, `Repo.kt:491-495`) | — | Solo se ven |
| Laptop (Cold Turkey) | **No** | **No** | **No**. Reimportar reemplaza los días que trae el archivo (`Repo.kt:253-258`, `Lote3.kt:157-167`) | — | Más → Laptop: solo se ve |
| MacroFactor (comida, series, entrenos) | **No** en la app | **No** | **No**. Reimportar borra y repone el rango de fechas (`Repo.kt:498-506`) | — | Gimnasio (`Fuerza.kt`) solo despliega y abre el día. **E-17 no se puede resolver en la app**: hay que corregir en MacroFactor y volver a importar |
| Respaldo restaurado | — | — | — | Restaurar **sobrescribe** el registro de salud de cada día que trae el respaldo (`Importar.kt:72`), así que puede deshacer correcciones de sueño hechas después del respaldo | Más → Datos |

**Conclusión P8 («lo automático se puede corregir o ignorar»): se cumple solo para el sueño de la noche, las fotos con
lugar, los países y patrimonio, y los sitios candidatos. No se cumple para la siesta del reloj, las visitas, el nombre
y el radio de un lugar, la pantalla, las notificaciones, la música, la agenda, las llamadas, los chats, la laptop ni
MacroFactor.**

### 1.2 EditarSueno a fondo (`ui/EditarSueno.kt`)

**Única llamada:** `ui/pantallas/Dia.kt:246`. Vive dentro de `bloqueSueno` (`Dia.kt:217-248`), en la tarjeta de la sección
«Sueño». No se abre desde ningún otro lugar: ni el Tablero, ni la línea del tiempo, ni la cinta, ni «Lo que registró el
teléfono», ni widgets, notificaciones o `QuickActivity`. Se buscó `EditarSueno` en todo el código y solo aparece ahí.

**Qué deja corregir:**
- «Me dormí»: `SelectorHora` (`EditarSueno.kt:53`), con fecha y hora (abre DatePicker y TimePicker).
- «Desperté»: lo mismo (`:55`).
- «Despierto en medio (opcional)»: 0, 10, 20, 30, 45 o 60 min, o «otra» (`:57`). Al abrir solo se rellena si la noche ya
  estaba corregida a mano (`:40`).
- Comprobación: el despertar va después de dormir y el total queda entre 30 min y 20 h (`:43`).
- Muestra lo que llegó del reloj esa noche (`sueno_fuentes`, `:61-65`), con su estado («noche», «siesta», «otra fuente, no se suma»).
- «quitar la corrección y volver a lo del reloj» (`:66-67`). Pone `sueno_manual = false` y vuelve a leer con `resincronizarDia`
  (`Salud.kt:264-268`), que relee **todos** los días desde esa fecha hasta hoy (de 1 a 60). Es un solo toque, sin confirmar
  y sin deshacer. **Si el reloj no tenía esa noche** (el caso típico de una noche anotada a mano), la relectura pone
  `sueno_min = null` (`Salud.kt:226-231`) y **lo anotado se pierde**.
- Al guardar con horas distintas a las del reloj, se borran las etapas profundo, REM y ligero (`:70-76`).

**Qué no deja:** no deja corregir ni quitar la siesta (`siesta_reloj_min`). Tampoco elegir otra de las fuentes que
llegaron, ni corregir la calidad (eso está en la misma tarjeta, fuera del diálogo).

**¿Sobrevive a la sincronización?** Sí. `sueno_manual = true` hace que la lectura de sueño termine antes de tocar las
horas (`Salud.kt:186`). Las demás métricas de esa fecha (HRV, ritmo durante el sueño y SpO₂ mínima) se calculan con la
ventana corregida (`Salud.kt:236-239`). Hay dos excepciones: (a) restaurar un respaldo antiguo la pisa (`Importar.kt:72`);
(b) la siesta del reloj se congela (ver 1.1).

**¿La cifra de sueño se puede tocar?**
- Tablero: `TarjetaMetrica` (`Hoy.kt:438-482`) es un `Surface` sin `onClick` (`:450`). **No se puede tocar.**
- Línea del tiempo («Tu día en 24 horas», `Hoy.kt:503-605`): es un `Canvas` sin gestos (`:559`). **No se puede tocar.**
- Cinta (`Cinta.kt:271-278`): al tocar el bloque se ve «Dormiste de … a …», pero no hay forma de corregir.
- «Lo que registró el teléfono» (`Dia.kt:597-605`): son `Clave`, solo texto (`Componentes.kt:261-271`).
- `Portada` (`Hoy.kt:618-623`): es una imagen y no tiene nada que ver con el sueño.
- Semana corta: `Cifra` «sueño» (`Lote2.kt:526`) no se puede tocar. El botón «Sueño» abre `QuickActivity` en modo `M_SUENO`
  (`Lote2.kt:538-540`), y ese modo solo pregunta la narración y la calidad (`QuickActivity.kt:665-695`), no las horas.
- Widget de sueño: muestra «reloj 3 h 00» (`Widgets.kt:214-215`) y al tocarlo guarda la calidad (`:219`). Tampoco
  corrige las horas.
- «Datos que faltan» → «No llegó el sueño de anoche…» (`Hoy.kt:211-212`) solo ofrece «sincronizar», no «anotarlo a mano».

**Toques para E-10 (3 h en vez de 7 h), con la noche mal en las dos horas:**
- **De 5:00 a 11:59** (`manana`, `Dia.kt:189`). La sección Sueño va arriba (`Dia.kt:295`), pero el botón está al final de
  la tarjeta, después de calidad, «¿Recordaste un sueño?» y lo demás. Toques: «corregir las horas de sueño» (1) → chip
  de «Me dormí» (2) → fecha «Aceptar» (3) → hora «Aceptar» (4) → chip de «Desperté» (5) → «Aceptar» (6) → «Aceptar» (7)
  → «Guardar» (8). Si solo está mal una de las dos horas, son 5. Si plegaste la sección antes, +1.
- **Después de las 12:00**, `bloqueSueno` se pinta después del Tablero, la tarjeta de la noche, la línea del tiempo y los
  momentos (`Dia.kt:317`). Son los mismos toques, con bastante más desplazamiento.
- **En semana corta**: no se puede desde Hoy. Primero «volver a Hoy completo» (`Lote2.kt:543`): +1 toque.
- **Viendo otro día**: el diálogo corrige `Repo.salud[fecha]` de la fecha abierta en Día, así que se puede corregir
  cualquier noche pasada con ‹ o el calendario.

**Detalle confuso:** `SelectorHora` avisa «De madrugada: cuenta para el …» cuando la hora elegida es antes de las 5
(`Selectores.kt:66-68`). En «Me dormí 00:30» o «Desperté 04:30» ese aviso no aplica: la noche se guarda en la fecha del
despertar (`Salud.kt:181-182`). El texto confunde justo aquí.

**Etiqueta inexacta:** `LibroDiario.kt:54` escribe «… según el reloj» aunque la noche se haya corregido a mano. `Dia.kt:242`
y `Dia.kt:601` sí dicen «(corregido a mano)» o «a mano».

### 1.3 Siestas (E-19)

- **Siesta del reloj**: no se puede quitar. Es la suma de las sesiones del reloj que no son la noche, es decir, las que
  terminan después del mediodía o no son el bloque más largo (`Salud.kt:197-223`). Solo se ve en `Dia.kt:605`. La lista de
  fuentes de `EditarSueno` las muestra marcadas «siesta», pero no deja descartarlas. **Imposible.**
- **Siesta propia** (`Tramo`, cronometrada o «anotar una siesta que ya pasó», `EnfoqueActivity.kt:326-330`): se quita en
  Más (1) → «Enfoque y siestas» (2) → «quitar» (3), con Deshacer (`EnfoqueSiestas.kt:90-104`). No se puede editar la
  duración ni la hora.

### 1.4 Lugares (E-18: dice «Gimnasio» y era «Café Centro»)

- **Renombrar un lugar: imposible.** No hay ningún `guardarLugar(l.copy(nombre = …))`. Las únicas escrituras son:
  crear (`Medir.kt:267`, `Mapa.kt:541`, `Lugares.kt:85`), cambiar el tipo (`Medir.kt:290`), restaurar al deshacer
  (`Medir.kt:295`) e importar (`Importar.kt:71`).
- **Cambiar el radio o la posición de un lugar ya guardado: imposible.** El radio solo se elige al crearlo. Si «Gimnasio»
  tiene un radio que alcanza el café de al lado, no hay forma de achicarlo.
- **Corregir una visita** (decir que esa visita fue en otro lugar): imposible. No hay pantalla de visitas.
- **Borrar el lugar**: Hoy → Más (1) → «Lugares especiales» (2) → al final, «ver el mapa emocional de tus lugares ›»
  (3, `Lote2.kt:213`) → en «Tus lugares», «Quitar» (4) → «¿Borrar?» (5) (`Medir.kt:295-297`). Ofrece Deshacer.
  `PantallaLugares` no está en el buscador (busca «lugar» y salen «Lugares especiales» y «Mapa»). También se llega desde
  Análisis (`Explorar.kt:275`) y desde Mapas → «Lugares» (`Galerias.kt:312`).
  - Al borrar, **las visitas no se borran** (`Repo.borrarLugar`, `Repo.kt:305`). Quedan con un id de lugar que ya no existe:
    salen como «?» en «Lo que registró el teléfono» (`Dia.kt:620`) y sin nombre en la línea del tiempo (`Hoy.kt:524`).
    `minutosPorTipo` las ignora (`Lugares.kt:140`).
  - Si después creas «Café Centro», `reconstruir` arma visitas nuevas de los últimos 30 días con el id nuevo
    (`Lugares.kt:113-133`). Las huérfanas siguen ahí.
  - Los momentos guardan `lugar = id` al registrarse (`QuickActivity.kt:184`) y también se quedan apuntando al lugar borrado.
- **Ignorar «para siempre»**: solo existe para **candidatos** sin guardar («no guardar nunca aquí», `Lote2.kt:186` →
  `Lugares.ignorar`, 150 m). No existe para un lugar ya guardado. Para un sitio que no quieres que vuelva a contar, el
  camino real es: borrar el lugar, esperar a que la app vuelva a preguntar por él y entonces elegir «no guardar nunca aquí».
- **¿Lo corregido sobrevive?** El tipo y el borrado, sí. Lo ignorado se guarda en `Config.lugares_ignorados` y
  `Lugares.estancia` lo respeta (`Lugares.kt:32`).

### 1.5 Otras observaciones

- «Lo que registró el teléfono» (`Dia.kt:334-337`) usa `autoPlegar = true`: se vuelve a plegar en cada sesión y en cada
  día (`Componentes.kt:187-192`). Lo automático queda doblemente escondido.
- No hay ninguna opción para «excluir esta app de pantalla o notificaciones», «excluir este calendario» o «este chat no cuenta».

---

## 2. Deshacer

### 2.1 Cómo funciona `ui/Deshacer.kt`

- `Deshacer.ofrecer(texto, alExpirar, restaurar)` (`:38-41`): si había una oferta pendiente, **ejecuta su `alExpirar`** y
  la reemplaza. Solo hay **una** oferta a la vez.
- `CapaDeshacer` (`:49-63`) espera 5 s (`:52`) y llama a `expirar`. La barra está **solo en `MainActivity`**
  (`MainActivity.kt:404`), por encima de todas las pantallas.
- El borrado ocurre **antes** de mostrar la barra: `db.borrar` es inmediato (`Repo.kt:176` y siguientes). «Deshacer» vuelve
  a guardar la copia que quedó en memoria. Tras los 5 s solo se pierde esa copia. Si la app se cierra durante esos 5 s,
  el borrado queda.
- En `QuickActivity` (otra Activity) **no hay `CapaDeshacer`**. Ahí, `Deshacer.ofrecer` (`QuickActivity.kt:606`) deja la
  oferta en memoria y la barra solo aparecería al volver a `MainActivity`, si el proceso sigue vivo. Desde el código no
  se puede saber si se llega a ver: depende del ciclo de vida.

### 2.2 Tabla de borrados

| Qué se borra | Dónde (archivo:línea) | ¿Confirma antes? | ¿Ofrece Deshacer? | ¿Qué pasa tras los 5 s? |
|---|---|---|---|---|
| Momento (desde el Diario o Privado) | `ui/pantallas/Momentos.kt:161-165` | Sí, 2 toques («Borrar» → «¿Borrar de verdad?») | Sí | Se pierde la copia en memoria |
| Momento recién creado («Deshacer» del registro rápido de ánimo) | `QuickActivity.kt:319` | No | No (el botón es el propio deshacer de la creación). Avisa con Toast | — |
| Impulso o recaída recién creados («Deshacer») | `QuickActivity.kt:367-372` | No | No (revierte también el día con `Recaidas.deshacer`) | — |
| Libido, o el impulso que la acompaña | `QuickActivity.kt:399, 402` | No | No | — |
| Consumo (sustancia) | `QuickActivity.kt:605-607` | No (1 toque «borrar») | Sí, pero la barra no está en `QuickActivity` (ver 2.1) | Se pierde la copia |
| Tramo (enfoque o siesta) | `EnfoqueSiestas.kt:101` | **No** (1 toque «quitar») | Sí | Se pierde la copia |
| Gasto | `Gastos.kt:228` | Sí («Quitar» → «¿Borrar?») | Sí | Se pierde la copia |
| Ingreso | `Gastos.kt:184` | Sí, 2 toques | Sí | Se pierde la copia |
| Lugar | `Medir.kt:295` | Sí, 2 toques | Sí | Se pierde la copia. Las visitas quedan huérfanas desde el primer momento |
| Sesión de escalada | `Escalada.kt:281` | Sí, 2 toques | Sí | Se pierde la copia |
| Sesiones de escalada vacías (automático, al abrir Escalada) | `Escalada.kt:76` | — (silencioso) | No | — |
| Bloque dentro de una sesión de escalada | `Escalada.kt:261` | No | No | — |
| Obra (libro, película…) | `Biblioteca.kt:524` | Sí, 2 toques | Sí | **La portada se borra del disco** (`alExpirar = Portadas.borrar`) |
| Obras sin título (automático) | `Biblioteca.kt:188` | — (silencioso) | No | — |
| Portada de una obra | `Biblioteca.kt:453` | No | No (el archivo se borra en el acto) | — |
| Fechas de una obra | `Biblioteca.kt:490` | No | No | — |
| Cita de una obra | `Biblioteca.kt:549-550` | Sí («×» → «¿borrar?») | No | — |
| Carta | `Bibliotecas.kt:514, 524-526` | Sí (diálogo «¿Borrar esta carta?») | Sí | Se pierde la copia |
| Nota | `Lote2.kt:289` | **No** (1 toque «borrar» en el diálogo de edición) | **No** | — |
| Meta | `Lote2.kt:479` | **No** | **No** | — |
| Toma de suplemento (la última de **hoy**) | `Lote3.kt:100` («deshacer») | No | No | — |
| Tema (#) unido, renombrado o pasado a hito | `TusTemas.kt:60` | — | Sí (`TusTemas.restaurar`) | Se pierde la copia |
| Sitio ignorado | `Lote2.kt:208` | No | No | — |
| Fecha propia (día especial) | `Gente.kt:272` | No | No | — |
| Logro propio | `Trofeos.kt:342-344`, `Logros.kt:229-231` | No | No | — |
| Frase guardada (♥) | `FrasesGuardadas.kt:132-135` (2 toques), `Libros.kt:1072-1080` (tocar y «quitar ♥») | Sí, 2 toques | No | — |
| Ciudad anotada a mano | `Mundo.kt:437-440` | Sí (diálogo) | No | — |
| País rascado a mano | `Mundo.kt:762` («tapar otra vez») | No | No (se vuelve a rascar) | — |
| Patrimonio marcado a mano | `Mundo.kt:931` («quitar») | Diálogo de detalle | No | — |
| Equivalencia de nombres | `Sistema.kt:713` | No | No | — |
| Categoría de gasto | `Sistema.kt:732` («×») | No | No | — |
| Mascota o persona cercana (Conocerte) | `Conocer.kt:124, 145` | No | No | — |
| Corrección manual del sueño | `EditarSueno.kt:62-67` | No | No. **Si el reloj no tenía esa noche, se pierde lo anotado** | — |
| Escuchas detectadas por notificación | `Importaciones.kt:105`, `Spotify.kt:255` | — (automático al importar) | No | — |
| Comidas y series de MacroFactor | `Repo.kt:500` (`borrarRango` al reimportar) | — | No | — |
| Ubicaciones de fotos | `FotosLugar.kt:329` (se rehacen) | — | — (se regeneran desde el catálogo) | — |
| Suscripciones de YouTube | `Repo.kt:324-328` (al importar) | — | No | — |

**Lo que no está en la tabla, porque no se borra sino que se archiva o termina:**
- Hábitos: «Archivar» y «Reactivar» (`Mas.kt:174, 192`).
- Suplementos: «dejar de usar» (`Lote3.kt:209`); se reactivan desde la ficha del catálogo (`Lote3.kt:120-126`).
- Personas: «no preguntar» (`Gente.kt:138`).
- Campañas: abandonar (`Campanas.kt:164`); no se encontró cómo deshacerlo.
- Experimentos: «Terminar experimento» (`Lote3.kt:503`); no se encontró cómo reanudarlo.

**Sin deshacer, hay borrados reales en:** notas, metas, tomas, citas, bloques de escalada, portadas, fechas de obra,
sitios ignorados, fechas propias, logros propios, frases guardadas, ciudades, equivalencias, categorías, mascotas y
personas cercanas, la corrección manual del sueño, y los automáticos (sesiones de escalada vacías y obras sin título).

### 2.3 ¿Papelera o recuperación después de los 5 s?

- **No hay papelera.** `db.borrar` borra la fila (`Repo.kt:176` y siguientes).
- La única forma de recuperar algo es **restaurar un respaldo** (Más → Datos). La restauración agrega los momentos cuyo id
  no existe (`Importar.kt:58`) y los días sin sobrescribir (`:56`). Pero **sobrescribe** salud y pantalla (`:72-73`) y
  restaura todo lo borrado desde esa copia, no solo lo que se quería recuperar.
- La copia automática de la carpeta tiene **nombre fijo y se reemplaza cada mañana** (`Respaldos.kt:121-136, 174-197`,
  la llama `Trabajo.kt:180`). Lo que se creó y se borró el mismo día no queda en ninguna copia. En Drive sí hay copias
  semanales y mensuales (`Drive.kt:134-150`).

### 2.4 ¿Las ediciones se pueden revertir?

- **Ánimo y escalas del día**: se guardan solos a los 700 ms (`Dia.kt:120`) y no hay deshacer. Tocar otro número lo
  cambia; tocar el mismo lo borra (`Componentes.kt:300`, `onCambio(if (sel) null else i)`). `Repo.guardarDia` actualiza
  `ts_editado` y `ts_valencia` (`Repo.kt:150`), pero no guarda el valor anterior.
- **Hábito marcado**: la casilla se vuelve a tocar (`Dia.kt:449`; widget en `Widgets.kt:518-521`).
- **Respuesta del experimento**: tocar la respuesta elegida la borra (`Dia.kt:879-882`).
- **Momento editado** (`DialogoMomento`, «Guardar»): sobrescribe sin deshacer (`Momentos.kt:151-158`).
- **Gasto o ingreso editado**: sobrescribe sin deshacer (`Gastos.kt:131-147`).
- **Nota editada**: sobrescribe y marca `editada` (`Lote2.kt:288`), sin el texto anterior.
- **Meta**: guarda un historial de cambios (`CambioMeta`, `Modelo.kt:375`), pero no hay botón para revertir.
- **Sueño**: «volver a lo del reloj» es la única edición con vuelta atrás explícita.

**P4 («todo borrado se deshace») no se cumple** en 20 de los 34 casos de la tabla. Las ediciones no tienen deshacer en
ningún caso.

---

## 3. Día lógico (`data/Fechas.kt`, corte a las 05:00)

`Fechas.diaLogico()` (`Fechas.kt:23-26`): antes de las 5:00 devuelve el día anterior. `diaLogicoDeMillis` (`:41`) hace lo
mismo con una marca de tiempo. `hoyCalendario()` (`:28`) devuelve la fecha real.

### 3.1 Qué usa cada registro

| Registro | Fecha que guarda | Origen |
|---|---|---|
| Día (ánimo, diario, hábitos, sueño subjetivo) | Lógica | `Dia.kt:103`, `QuickActivity.kt:110, 666, 699`, `Widgets.kt:519` |
| Momento | Lógica | `QuickActivity.kt:165` |
| Recaída con otra hora | Lógica de la hora nueva | `Recaidas.kt:41` |
| Nota | Lógica | `Lote2.kt:268` |
| Toma de suplemento | Lógica de la marca de tiempo | `data/Lote3.kt:31-33`; notificación «Ya los tomé» en `Recordatorios.kt:110-114` |
| Tramo (enfoque o siesta) | Lógica | `Enfoque.kt:172, 290`; `Enfoque.agregar` con una hora antes de las 5 pasa al día siguiente del calendario (`Enfoque.kt:279-282`) |
| Consumo | Lógica de su inicio | `Sustancias.kt:33`, `QuickActivity.kt:612` |
| Respuesta a cuestionario, prueba de reacción | Lógica | `Medir.kt:133, 189` |
| Sesión de escalada | Lógica (o la de la sesión del reloj) | `Escalada.kt:85` |
| **Gasto e ingreso** | **Calendario** (`fecha`) más la hora. `dia` se calcula: antes de las 5, el día anterior (`Modelo.kt:244, 260`) | `Gastos.kt:131, 141`, `QuickActivity.kt:648` |
| Sesión de ejercicio del reloj | Lógica | `Salud.kt:349` |
| **Salud (Health Connect)** | **Calendario**: el sueño va con la fecha del despertar; los pasos usan la ventana lógica de 05:00 a 05:00 (`Salud.kt:155-161`); el resto, de 00:00 a 24:00 (`:149-153`) | `Salud.kt` |
| Pantalla | Lógica (de 05:00 a 05:00) | `Pantalla.kt:40-41, 144` |
| Fotos del día, llamadas y SMS | Lógica (de 05:00 a 05:00) | `Galeria.kt:32-33`, `Comunicacion.kt:22-23` |
| Actividad física (reconocimiento) | Lógica | `Actividad.kt:48-49` |
| Notificaciones y música | Lógica | `Oyente.kt:56, 121` |
| Chats importados | Lógica | `Importaciones.kt:434` |
| **Agenda** | **Calendario** (de 00:00 a 24:00) | `Agenda.kt:22, 53, 69` |
| **Entorno (clima)** | **Calendario** | `Entorno.kt:32, 46` |
| **Visitas** | Se guardan por la fecha de calendario de la entrada (`Repo.kt:311`). Día las filtra igual (`Dia.kt:584`), pero `minutosPorTipo` las reparte por día lógico (`Lugares.kt:145`) | — |
| Plan de avisos de ánimo | **Calendario** | `Trabajo.kt:97-101`, `AvisosAnimo.kt:8` |
| Widget de gastos «hoy» | **Calendario** (`it.fecha == hoy`) | `Widgets.kt:184-185`. El calendario de la app suma por `dia` (`Calendario.kt:85`), así que un café a la 1:30 cuenta en «hoy» en el widget y en ayer en la app |

### 3.2 ¿Hay aviso antes de las 05:00?

Textos encontrados (búsqueda de «5:00», «05:00», «madrugada», «día anterior», «medianoche»):
- **`Dia.kt:291`**: «Pasa de medianoche: sigues llenando el día $fecha. Los días empiezan a las 5:00.» Solo sale en Día,
  con el día de hoy abierto y antes de las 5. Muestra la fecha en crudo (2026-10-13). **No sale en semana corta**, porque
  `Dia.kt:212` termina la pantalla antes.
- **`Selectores.kt:66-68`**: «De madrugada: cuenta para el …». Solo sale si se **eligió** una hora antes de las 5 (`valor != null`).
  Con «ahora» (`valor == null`) no sale, que es justo el caso de E-14.
- `Gastos.kt:224`: en la lista sale «01:30 de la madrugada» cuando `dia != fecha`.
- `Hoy.kt:604` y `Cinta.kt:63, 153`: explican que la línea va de 0:00 a 5:00 del día siguiente.
- `LibretaMomentos.kt:233`: es un comentario del código, no se ve en la app.

**No hay aviso** en `QuickActivity` (ánimo, momento, gasto con «ahora», sueño, mínimo, consumo), Notas, la hoja de la noche,
Suplementos ni la notificación «Ya los tomé».

### 3.3 Cómo se ve la fecha en las pantallas de registro

- **QuickActivity**: `Encabezado` (`QuickActivity.kt:193-197`) muestra «HH:mm · Estilo.fecha(diaLogico)». A la 1:30 se ve
  «01:30 · [fecha de ayer]», pero en el estilo del tema: números romanos en ROMANO, «JORNADA 287 · AÑO 2026» en DISTOPIA,
  griego en HELENO (`Estilo.kt:114-122`). No dice que es el día anterior ni por qué. No se puede cambiar el día (salvo
  la hora de una recaída, el consumo y el gasto).
- **DialogoMomento** (`Momentos.kt:123`): el título es «tipo · hora». **No muestra la fecha** y no se puede cambiar.
- **Notas** (`Lote2.kt:255-270`): el formulario no muestra la fecha. La lista sí: «tipo · lun 13 oct» (`Lote2.kt:300`).
  El diálogo de edición (`Lote2.kt:284-290`) solo cambia el texto: **no** la fecha ni el tipo.
- **Hoja de la noche** (`Dia.kt:416-417`): subtítulo «fecha · martes 13 de octubre · hoy». Antes de las 5, `Fechas.relativo`
  dice «hoy» para la fecha de ayer en el calendario (`Fechas.kt:68-77`). Es correcto según la regla, pero no lo explica.
- **Suplementos** (`Lote3.kt:82-100`): solo hoy. No se puede anotar una toma de ayer (E-11) ni borrar una de días anteriores.

### 3.4 ¿Se puede mover una nota o un momento a otro día?

- **Nota: no.** El diálogo solo edita el texto (`Lote2.kt:286-288`).
- **Momento: no**, salvo una **recaída**: `DialogoMomento` muestra «Empezó» solo si `tipo == "recaida"` (`Momentos.kt:142-148`).
  - **Error:** en `Momentos.kt:156` la condición compara solo «HH:mm» (`Fechas.horaDe(...) != Fechas.horaDe(m.ts)`). Si
    cambias la fecha y dejas la misma hora y minuto, se llama a `guardarMomento(n)` con el `ts` viejo y **el cambio de
    día se pierde sin avisar**.
- **Gasto e ingreso: sí.** `SelectorHora` con fecha en el formulario de edición (`Gastos.kt:118-119, 215-221`).
- **Consumo: sí.** `CompletarConsumo` cambia el inicio y recalcula la fecha (`QuickActivity.kt:582, 612`).
- **Tramo: no** (solo borrar y volver a anotar con fecha, `EnfoqueActivity.kt:326-330`).

**P9 se cumple a medias**: hay un aviso en Día (no en semana corta) y otro en `SelectorHora`. Faltan en todos los registros
rápidos y en Notas.

---

## 4. DialogoMomento: desde dónde se abre

Definición: `ui/pantallas/Momentos.kt:105-171`. Todas las llamadas (búsqueda de `DialogoMomento`):

| Llamada | Pantalla | ¿Se llega? | Cómo |
|---|---|---|---|
| `Momentos.kt:100` | `PantallaMomentos` | **No** (ruta muerta, ver §0) | — |
| `Diarios.kt:182` (lo abre `Diarios.kt:166`, filas en `:224-231`) | Pestaña Diario → sección «Línea de tiempo» | Sí | Diario (1) → desplegar «Línea de tiempo» (2; `autoPlegar = true`, `Diarios.kt:142`, empieza plegada en cada sesión) → buscar el día → tocar el momento (3). Muestra hasta 8 momentos por día (`:225`); el resto sale como «+N momentos más» y no se puede tocar (`:232`) |
| `Recaidas.kt:72` (filas en `:215-229`) | Privado (`recaidas`) | Sí | Solo momentos privados. Más → … o Día → «privado de este día» (`Dia.kt:531`) |

**No se abre desde:**
- La línea del tiempo de Hoy (`Hoy.kt:503-605`): un `Canvas` sin gestos.
- La sección «Momentos de este día» de Día (`Dia.kt:249-268`): filas `Row` sin `clickable`. Solo «abrir la hoja de la libreta →».
- La hoja de la libreta (`LibretaMomentos.kt:274-325`): solo ofrece compartir, navegar y abrir el día.
- La cinta (`Cinta.kt:271-342`): tocar solo muestra el detalle.
- La biblioteca de momentos, el buscador o Totales (`Totales.kt:108` abre Día), widgets, notificaciones y `QuickActivity`
  (que tiene su propio formulario, solo para el momento que se acaba de crear).

**Qué deja cambiar:**
- Texto: siempre.
- Si es un registro de ánimo (`Catalogo.esRegistro`): valencia, energía, emociones y pensamiento. Si no, intensidad
  (`:127-141`).
- Hora y duración: **solo en recaídas** (`:142-148`), y con el error de 3.4.
- **Tipo: nunca.** E-12 («idea» en vez de «alegría») no tiene solución: hay que borrar el momento y volver a crearlo, que
  además queda con la hora de ahora.
- **Fecha: nunca** (salvo recaídas, con el error).
- **Con quién: tampoco** (`con_quien` no está en el diálogo), aunque `QuickActivity` lo pregunta (`QuickActivity.kt:302-312`).

Borrar: 2 toques más Deshacer (`:161-168`).

---

## 5. Seleccion (`ui/pantallas/Calendario.kt:57-79`)

Es un objeto global con estado de Compose: cada pantalla deja la fecha en un campo y la siguiente la lee.
`Seleccion.abrir(nav, fecha)` (`:78`) pone `pedida` y `ultimoMes` y salta a la pestaña Día con `nav.pestana("dia")`, que
**vacía la pila**: «atrás» ya no vuelve a la pantalla de origen.

| Campo | Lo escriben | Lo lee |
|---|---|---|
| `pedida` | `abrir()` desde Calendario (`:115`), Análisis (`Analisis.kt:406`), Recuerdos (`Recuerdos.kt:86`), Cinta (`Cinta.kt:344`), Fuerza (`Fuerza.kt:118`), Diario (`Diarios.kt:166, 281`), Semana (`Semana.kt:87, 116`), Libreta (`LibretaMomentos.kt:320`), Bibliotecas (`:302`), Totales (`:108`), Año (`Anno.kt:92`); y `MainActivity.kt:245-250, 272-276, 285` | Solo `PantallaDia` (`Dia.kt:103, 112-118`). La consume y la pone en null |
| `diaAbierto` | `Dia.kt:105` | `Dia.kt:103` (recuerda el día al cambiar de pestaña) |
| `noche` | `Dia.kt:138`, `MainActivity.kt:249, 275` | `PantallaNoche` (`Dia.kt:373`), con `remember`: no cambia si la pantalla ya está abierta |
| `seccion` | `Dia.kt:138`, `MainActivity.kt:249, 275` | `Dia.kt:142-150`, `Dia.kt:394-399` |
| `privado` | `Dia.kt:531` | `Recaidas.kt:69-70` (lo borra al salir) |
| `cinta` | `Dia.kt:314` | `Cinta.kt:70-71` |
| `momentos` | `Dia.kt:263`, `LibretaMomentos.kt:174` | `LibretaMomentos.kt:281` |
| `entrada`, `sueno`, `carta` | Bibliotecas, Diario y Totales | Lectores de entrada, sueño y carta |
| `ultimoMes` | `Dia.kt:123, 167`, `abrir()` | Calendario |

**Pantallas que respetan la fecha elegida en Día:** la hoja de la noche (ánimo, diario, hábitos, predicción), la sección
Sueño (calidad y `EditarSueno`), la respuesta del experimento, la libreta, la cinta, Privado y «Lo que registró el teléfono».

**Pantallas que ignoran la fecha y usan hoy (o la hora actual)**, aunque se abran desde un Día pasado:
- Los botones de Día «✎ cuestionarios», «⚡ reacción» y «◷ enfoque» (`Dia.kt:301-308`): guardan con `diaLogico()`
  (`Medir.kt:133, 189`).
- Todo `QuickActivity`: ánimo, momento, sueño rápido, mínimo, menú «＋» (`QuickActivity.kt:165, 666, 699`). Gasto y consumo
  empiezan en «ahora», pero dejan elegir la fecha.
- Suplementos (`Lote3.kt:82`, `hoy` fijo).
- Notas (`Lote2.kt:268`).
- Escalada «Nueva sesión» (`Escalada.kt:85`).
- Gastos: abre en el mes de calendario actual (`Gastos.kt:79`) y no lee `Seleccion`; la fecha se elige a mano.
- Hábitos desde el widget (`Widgets.kt:519`).
- «Tu gente», Cartas (`hoy`) y Metas.

**Consecuencia para E-11 y E-21:** desde el Día de ayer se pueden llenar el ánimo, el diario, los hábitos, la calidad y
las horas del sueño, y el experimento. **No** se pueden agregar ni corregir desde ahí momentos, tomas de suplementos,
gastos, notas, cuestionarios ni tramos. «Ver y editar todo lo de ayer en un solo lugar» (E-21) no existe: Día de ayer
muestra los momentos, los tramos, las sustancias y lo automático solo para leer.

---

## 6. Lo que no se pudo determinar desde el código

- Si la barra de Deshacer de un consumo borrado en `QuickActivity` llega a verse (depende de si `MainActivity` sigue viva
  detrás).
- El alto real en pantalla del botón «corregir las horas de sueño» (cuánto hay que desplazarse); hacen falta capturas.
- Si `Deshacer` choca con el gesto de deslizar para cambiar de día (`Dia.kt:153-162`) o con la escala deslizable
  (`Componentes.kt:284-290`). Es probable que en Día el arrastre horizontal sobre una `Escala` cambie el valor **y**
  cuente para cambiar de día, pero hay que probarlo en un teléfono.
