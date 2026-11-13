# Capítulo 10. Privacidad, seguridad e integridad de los datos

> **Para qué sirve este capítulo.** Explica **dónde viven tus datos, quién puede verlos y qué tan seguro es que no se pierdan**: el almacenamiento en el teléfono, el seguimiento privado y por dónde se filtra, el cifrado del respaldo, la verificación sin restaurar, los respaldos automáticos (carpeta y Google Drive), la restauración y los servicios externos con los que la app habla. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper (en 10e cambió el formato de los respaldos cifrados, D.104; en 10f, la lista de servicios externos llegó a los ajustes, D.116, y los experimentos ganaron una huella de lo sellado, D.113); lo que cambió en el módulo 10 se marca «desde 10a…10f» o «desde el esquema 13» y está en el Apéndice D.
>
> **Cómo leerlo.** Igual que los capítulos anteriores. Las cifras criptográficas y las reglas vienen del código, con el nombre de la función. Los hallazgos son H-8-n (módulo 8, junto con el capítulo 9); los H-9-n son del módulo 9. Las **reglas de privacidad declaradas** vienen de la sección 7 del documento de contexto del proyecto (`VESTIGIUM_MEI_CONTEXTO.md`, v81); la sección 10.3.4 las compara con el código.
>
> **Privacidad del propio capítulo.** El seguimiento privado (impulsos, recaídas, libido, medicamentos) se describe **solo en su mecánica**: no hay ejemplos ni datos personales, y tampoco se reproducen las palabras clave que el código usa para reconocer ciertos hábitos.

---

## 10.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Cifrar** | Convertir un archivo en algo ilegible sin una llave. |
| **AES-256** | El algoritmo de cifrado estándar más usado; «256» es el largo de la llave en bits. |
| **GCM** | Un modo de usar AES que, además de cifrar, añade una **etiqueta de autenticación**: si alguien cambia un solo bit del archivo, el descifrado falla en vez de dar datos alterados (Dworkin, 2007). |
| **IV (vector de inicialización)** | Un número al azar que se usa una sola vez por cifrado para que el mismo contenido no produzca el mismo resultado. |
| **PBKDF2** | Una función que convierte una contraseña en una llave **a propósito lenta**: repite un cálculo muchas veces (las **iteraciones**) para que probar millones de contraseñas cueste mucho (Turan et al., 2010). |
| **Sal** | Bytes al azar que se mezclan con la contraseña, para que la misma contraseña dé llaves distintas en cada archivo. |
| **Android Keystore** | Un almacén de llaves del sistema: la app puede usar una llave para cifrar, pero no puede sacarla del teléfono. |
| **Seguimiento privado** | Impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas. Desde el módulo 10a solo se ven en la pantalla Privado y viajan a R (sección 10.3). |
| **Integridad** | Que los datos no se pierdan, no se dañen y no cambien sin que lo sepas. |
| **Huella (SHA-256)** | Un resumen de 64 caracteres que se calcula de un texto: el mismo texto da siempre la misma huella, y cambiar una sola letra da una huella completamente distinta. No esconde nada ni usa contraseña; sirve para notar si algo cambió (sección 10.9). |

---

## 10.2 Dónde viven los datos

- **En el teléfono**, en una base SQLite de documentos JSON (`vestigium.db`, capítulo 2) en el almacenamiento privado de la app, al que otras apps no tienen acceso.
- **La copia de Android, solo si la enciendes** (desde el esquema 22, D.168). Hasta entonces el manifiesto declaraba `android:allowBackup="false"` y la copia de seguridad del sistema no se llevaba nada. Ahora hay un agente propio (`CopiaAndroid`) que **por omisión no manda nada**. Si lo enciendes en Datos, cuando Android hace su copia se lleva una foto coherente de la base (`VACUUM INTO`, en `files/copia_android/`). En una instalación nueva con la misma cuenta, esa base vuelve antes de abrir la app (`CopiaAndroid.restaurarSiHace`, solo si todavía no hay base).
  - **Solo cifrada.** Únicamente sale si el transporte va cifrado de punta a punta con el bloqueo de pantalla (`FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED`) o de teléfono a teléfono. Lo exigen las reglas `res/xml/copia_android.xml` (`disableIfNoEncryptionCapabilities`) y `copia_android_vieja.xml` (`requireFlags`), y lo comprueba el agente.
  - **Hasta 24 MB.** Android admite hasta 25 MB por app; con más, no se manda y se anota en Datos. No lleva las fotos ni la contraseña del respaldo.
  - **[Diseño propio]** la regla de apagada por omisión. No comprobé en esta sesión cómo trata cada fabricante esas reglas **[sin verificar]**.
- **Escritura segura y revisión semanal** (desde el 9 nov 2026, esquema 22, D.166). La base usa el registro aparte de SQLite (*write-ahead log*, WAL: cada cambio se escribe primero en un archivo de registro y solo después se pasa a la base, así que un corte de luz a la mitad no deja la base a medias) (`Db.onConfigure`). Una vez por semana, la tarea diaria corre `PRAGMA quick_check` (`Db.revisar`), una revisión rápida de que la base está sana; el resultado queda en `Config.bd_revision` y `bd_revision_ok`, se ve en Datos y, si falla, llega una notificación. **[Validado]** como práctica: es la revisión que SQLite ofrece para eso. No comprobé en esta sesión cuánto tarda en una base de varios años **[sin verificar]**.
- **La base no tiene un cifrado propio**: depende del cifrado del almacenamiento que hace Android cuando el teléfono tiene bloqueo de pantalla. No verifiqué en esta sesión cómo funciona ese cifrado en cada versión de Android **[sin verificar]**.
- **Bloqueo de la app** (opcional, `Config.bloqueo`, apagado por omisión): al abrirla pide huella o el bloqueo del teléfono (`BiometricPrompt`, con `BIOMETRIC_WEAK` o credencial del dispositivo) y vuelve a pedirlo si estuvo **más de 60 segundos** en segundo plano. Si el teléfono no tiene ningún bloqueo configurado, la app **no puede** pedirlo y queda abierta. Es una protección contra miradas casuales, no contra alguien con acceso técnico al teléfono.

---

## 10.3 El seguimiento privado

### 10.3.1 Qué es y cómo funciona

_Esta sección describe el código después del módulo 10a (decisiones D-1 y D-2 de `revision_propuestas.md`). Hasta v84 la regla era otra; el cambio está en el Apéndice D._

- **Una sola regla, en un solo archivo** (`data/Privado.kt`). Es privado:
  - todo momento de tipo `impulso`, `recaida` o `libido` y, desde el esquema 21, `masturbacion` e `intimidad` (`Momento.privado`, que usa `Privado.TIPOS`; capítulo 3, sección 3.16);
  - en el día, la intensidad del impulso (0 a 5), si hubo recaída, los detonantes y los **medicamentos**;
  - el **hábito ligado a las recaídas**. Con el interruptor encendido es el que elegiste en Privacidad o, si no elegiste, un hábito activo cuyo nombre contenga ciertas palabras clave. Con el interruptor apagado, solo el que elegiste. Elegir «ninguno» funciona; hasta v84 caía de nuevo en las palabras clave.
- **Dónde se ve: solo en la pantalla Privado** (Más → Privado; antes «Impulsos y recaídas»). Siempre, con el interruptor encendido o apagado (`ui/pantallas/Recaidas.kt`):
  - los medicamentos del día, con un botón «lo de siempre» que copia los del último día con medicamentos;
  - «Registrar un impulso ahora» (un momento de impulso con hora);
  - los impulsos por hora, la tendencia de los últimos 12 meses (impulsos y días con medicamentos), los días que tomaste cada medicamento y el registro de los últimos 30 momentos privados, que se pueden corregir.

  Con el interruptor encendido, además:
  - en el día, la intensidad del impulso (0 a 5), si hubo recaída, los detonantes y el hábito ligado;
  - los botones para registrar una recaída, la libido y, desde el esquema 21, una masturbación o un encuentro de intimidad, con su resumen (veces en 30 días, la última y, en la intimidad, el nivel más alto, la satisfacción media y cuántas veces la comodidad fue baja, como dato y sin juicios);
  - el resto del resumen privado (rachas, recaídas por hora y por día de la semana, lugares, detonantes…), los días con recaída en la tendencia y la sección de libido.

  Desde Día, el enlace «privado de este día» abre la pantalla en esa fecha. Las correlaciones de estas variables se hacen **solo en R**, como decidiste.
- **Dónde no se ve: en ninguna otra parte de la app**, ni con el interruptor encendido:
  - Día, Hoy, momentos, cinta, diarios, libreta, calendario, semana, año, totales, mapa y widgets;
  - reportes, «Tu año», logros (también los propios), metas, campañas y experimentos (el impulso dejó de ser una medida posible);
  - Constelación, Red, Tu gente, las sugerencias de @ y # al escribir, «Tus temas», y las variables `per_`, `pen_` y `tema_`. La única excepción es la ficha de una persona en Tu gente, desde el esquema 21 (decisión del autor). Con el interruptor encendido, la ficha dice cuántas veces hubo intimidad con ella y cuándo fue la última (`Personas.intimidadCon`); con el interruptor apagado no dice nada. Las @personas escritas en la descripción de un momento privado no cuentan como menciones;
  - todas las listas de análisis: `Estadistica.variables()` excluye `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `n_impulso`, `medicamentos_n`, las del esquema 21 (`n_masturbacion`, `masturbacion_min`, `masturbacion_satisfaccion`, `n_intimidad`, `intimidad_nivel`, `intimidad_satisfaccion`, `intimidad_comodidad`, `n_coito`, `intimidad_distancia`), las familias `med_` y `det_` y `hab_` del hábito ligado.
- **El interruptor `Config.seguimiento_privado`** (apagado por omisión) solo decide qué se **ofrece** en la pantalla Privado: con él apagado, medicamentos y momentos de impulso; con él encendido, también la intensidad del impulso del día, recaídas, libido y detonantes (la lista de arriba). Ya no decide qué se muestra fuera de Privado: fuera, nunca se muestra.
- **La exportación para R lleva todo, siempre.** El panel calcula las variables privadas aunque el interruptor esté apagado, y `vm_momentos.csv`, `vm_dias.csv`, `vm_panel_extendido.csv` y `vm_respaldo.json` las llevan. Desde la entrega 10d, `vm_variables.csv` lista las variables que la app cruza y marca las privadas (`privada` = 1, con `Privado.esVariable`). Por omisión, `vm_relaciones()` y `vm_factores()` de R **no** las cruzan, para dar lo mismo que la app; con `privadas = TRUE` sí, y esa es la vía para hacer en R las correlaciones del seguimiento privado. Las coordenadas de un momento privado ya no se usan para pedir el clima (`Entorno.lugarDe`).
- **Registrar una recaída** (`Recaidas.registrar`) guarda el momento con hora y lugar, marca el día con recaída y **desmarca el hábito ligado**. Se puede deshacer tal cual (`deshacer`) y cambiar la hora (`moverHora`), que busca la ubicación de esa hora (la más cercana en ±30 min) y descarta la luz y el ruido medidos al registrar. Desde el esquema 14 (H-NAV-6): cambiar solo el día, con la misma hora, también la mueve, porque se compara el minuto completo; y borrarla desde el diálogo de Privado quita la marca del día si era la única recaída de ese día (`alBorrar`; el día queda sin respuesta, porque no se sabe qué había antes).
- **Una masturbación con pornografía** (esquema 21) crea su recaída gemela (`Recaidas.registrarDesdeMasturbacion`). La gemela marca el día y el hábito como cualquier recaída, y las dos quedan ligadas (`vinculo`). Borrar la masturbación, o cambiar su estímulo, quita la gemela. Borrar la gemela deja la masturbación sin vínculo.
- **El resumen privado** (`Recaidas.resumen`):
  - impulsos y recaídas por hora; recaídas por día de la semana;
  - lugares, detonantes, qué hacías antes y aparato;
  - minutos entre un impulso y la recaída que le siguió (dentro de 24 h);
  - cuántos impulsos pasaron **sin recaída en las 3 horas siguientes**;
  - días desde la última recaída y rachas.
- **Lo que no se puede filtrar:** si escribes algo privado en el texto libre del diario o de un momento común, la app no tiene forma de saberlo.

### 10.3.2 Las rachas: solo los días respondidos (H-8-8)

**Hasta la entrega 10c**, `Recaidas.resumen` empezaba a contar desde el primer día con respuesta y, a partir de ahí, **todo día que no era de recaída contaba como limpio**, aunque ese día no se hubiera contestado nada. Es el mismo problema de los datos faltantes del capítulo 3 (sección 3.15): si los días sin registro son justamente los difíciles, la racha queda inflada.

**Desde la entrega 10c (D.75)** las rachas salen de una función pura, `Recaidas.rachas(recaidas, limpios, hoy)`:

- un día **con recaída** (marcado en el día o con un momento de recaída) reinicia la racha a 0;
- un día cuenta como **limpio** solo si ese día se respondió «no» (`Dia.recaida == false`); suma 1;
- un día **sin respuesta** no suma ni reinicia: se cuenta aparte, como «sin respuesta» desde la última recaída. El día de hoy, mientras no se responda, no cuenta como sin respuesta.

La más larga (`rachaMaxima`) y la actual (`rachaActual`) salen de ahí, y la pantalla Privado dice «días respondidos sin» y, si los hay, cuántos días quedaron sin respuesta desde la última («no se sabe»). Ejemplo con una secuencia inventada de cuatro días (sí, no, sin respuesta, no): antes, racha actual 3; ahora, **2**, con 1 día sin respuesta. Lo comprueba `CalculosPrueba.rachasSoloConRespuesta`. **[Diseño propio]**: no contar ni romper con los días sin respuesta es un punto medio; romper la racha castigaría olvidar el registro, y contarlos la inflaría.

### 10.3.3 Dónde aparecía hasta v84 y qué cambió

La Figura 10.1 resume qué tipo de dato llega a cada salida. Hasta v84, el seguimiento privado se ocultaba de casi todo, pero se filtraba por seis vías. Todas se cerraron en el módulo 10a (la 5, en lo que toca a lo privado: H-8-5 sigue abierto en lo demás):

1. **Los momentos de impulso no eran «privados» para el código** (H-8-1). Contaban en el reporte, en «Tu año», en los logros de momentos, en el mapa, en un widget, en la semana, el calendario, el año, la Constelación, la Red y las presencias. Ahora `Momento.privado` incluye el impulso.
2. **«La relación del año» podía usar variables privadas y `medicamentos_n`** (H-8-2), y `n_impulso` entraba siempre, aun con el interruptor apagado, porque estaba en el grupo «Momentos» (H-9-1). Ahora `variables()` las excluye.
3. **La recaída apagaba un hábito con logros, campañas y rachas visibles** (H-8-3, H-9-38), y los logros propios aceptaban variables privadas. Ahora el hábito ligado solo se ve y se marca en Privado (`Privado.habitosVisibles`), y los logros propios sobre algo privado no se ofrecen ni se muestran.
4. **Los medicamentos no estaban detrás de ninguna regla** (H-8-16). Ahora son privados.
5. **La exportación salía en claro a la carpeta y a Drive** (H-8-5). Ahora, con datos privados y sin contraseña, el respaldo no sale del teléfono (sección 10.5).
6. **Las @menciones y #temas de los momentos privados** entraban en `per_`, `tema_`, «Tu gente», «¿Quién es @x?» y las sugerencias al escribir (H-6-1). Ahora se filtran.

**Sobre la pregunta del plan («por qué los logros nunca se ligan a sustancias, impulsos, recaídas ni libido»).** El documento de contexto lo afirma de los 12 logros secretos y, en general, de todo el seguimiento privado. El razonamiento es correcto: un logro por «días sin recaída» o por «tomar menos alcohol» convertiría el seguimiento privado en un marcador, con la presión de la ley de Campbell (capítulo 9, sección 9.7) justo sobre los datos más delicados; y uno «por registrar impulsos» premiaría registrarlos.

Hasta v84 la afirmación era **casi** cierta, salvo por tres vías indirectas, recogidas en los puntos 1 y 3 de arriba: los impulsos contaban en los logros de momentos, el hábito ligado tenía logros de constancia y campañas, y los logros propios aceptaban variables privadas. Desde el módulo 10a se cumple. Las sustancias **no** son privadas según la regla declarada («Sustancias NO son privadas: dato normal»), así que un logro propio sobre ellas no rompe ninguna regla; solo conviene recordar el riesgo de la ley de Campbell.

### 10.3.4 Las reglas declaradas frente al código

La sección 7 del documento de contexto llama a estas reglas «no negociables». Comparación con el código de v90 (donde cambió desde v81, se dice):

| Regla declarada | ¿Se cumple? | Dónde se comprobó |
|---|---|---|
| WhatsApp y comentarios de YouTube: nunca guardar texto | **Sí** | `ChatDia` guarda solo conteos por chat y día (`mios`, `otros`); de los comentarios y chats de YouTube solo se guarda cuándo y en qué video (`data/Modelo.kt`, `Importaciones.kt`) |
| Notificaciones: no leer su texto | **Sí** | `sensores/Oyente.kt` cuenta solo la app y la hora |
| Tokens de Spotify y claves de API solo en el teléfono | **Sí** | Preferencias privadas (`Spotify.prefs`, `Portadas.prefs` «claves_busqueda»), fuera de `Config` y por tanto fuera de los respaldos |
| `allowBackup=false` | **Sí** | Manifiesto |
| Fotos nunca se copian | **Sí** | El catálogo guarda hora, coordenadas y aparato, no la imagen |
| Cartas selladas: sin contenido en búsqueda, CSV ni «Tu año» | **En parte** | El CSV, «Tu año» y la búsqueda cumplen (la búsqueda en cartas solo mira las legibles, `Carta.legible`, en `ui/pantallas/Totales.kt`); `vm_respaldo.json`, que se exporta junto al CSV, lleva el texto (H-8-5) |
| Seguimiento privado (impulsos, recaídas, libido, detonantes, medicamentos) nunca en reportes, «Tu año», logros, mapas ni widgets; sí en la exportación a R | **Sí desde el módulo 10a**, con una regla más estricta: fuera de la pantalla Privado no aparece en ninguna salida. Hasta v84 fallaba en los impulsos y los medicamentos (H-8-1, H-8-2). **Una excepción elegida, desde D.119**: el reporte para terapia lleva impulsos, recaídas, detonantes y medicamentos (nunca la libido) solo si se marca su casilla al generarlo; la casilla no se guarda y empieza apagada cada vez (capítulo 9, sección 9.3.6) | `data/Privado.kt`, `Momento.privado`, `Estadistica.variables`, `ciencia/Terapia.kt` (`Opciones.privado`) |
| Sustancias no son privadas | **Sí** | Se tratan como dato normal |
| La imagen del hall no lleva texto del diario | **Sí** | `HallImagen.kt` («Sin textos del diario ni nada privado»); la cita del diario solo aparece en el detalle del trofeo dentro de la app |
| «Nada pasa por servidores ajenos salvo Drive, Open-Meteo y Spotify» (sección 1) | **No del todo** | También hay consultas a TMDB, RAWG, TVmaze, Google Books, Open Library, Crossref y a los mosaicos de OpenStreetMap (sección 10.8; H-8-17). Desde 10e, el texto de privacidad de la app los nombra todos (D.103), y desde 10f también Ajustes › Privacidad, con la misma lista (`Red.SERVICIOS`, D.116); el documento de contexto, que no está en el repositorio, sigue con la lista corta |

![Figura 10.1](figuras/fig24_flujo_privacidad.png)

*Figura 10.1. Qué tipo de dato llega a cada salida, según el código después del módulo 10a. Borde rojo: hallazgo que sigue abierto (H-8-5). Script: `figuras/fig24_flujo_privacidad.py`.*

---

## 10.4 El respaldo cifrado

### 10.4.1 Qué contiene

`Respaldos.armar` (antes `archivo`) arma un **.zip** con `vm_respaldo.json` (diario, momentos, cartas, ajustes, Conocerte… todo), `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv` (solo hora y coordenadas de las fotos con lugar, **nunca la foto**) y las portadas de libros y películas. Si pusiste contraseña, ese .zip se cifra y se guarda como **.vmc**; el .zip en claro se borra.

Desde el 9 nov 2026 (esquema 22, D.166) lleva además:
- **`vm_extra.json`** (`Respaldos.Extra`): lo que no está en `vm_respaldo.json` pero tampoco se debe perder. Es la papelera, los ajustes que no se pudieron leer (`config_ilegible`) y, **con su texto tal cual**, los registros que la app no pudo leer al abrir (`Repo.crudosIlegibles`). Antes, como el respaldo se arma con lo que está en memoria, esos tres se quedaban fuera de todo respaldo. Al restaurar vuelven a la base (los ilegibles, en una colección aparte `ilegible_<colección>`, para que una versión futura pueda leerlos).
- **`fotos_dia/`** (esquema 22, D.168): la copia de la foto de cada día (capítulo 2).
- **`manifiesto.json`** (`Respaldos.Manifiesto`), al final: cuándo y con qué versión se hizo, el esquema, cuántos registros trae de cada colección (`conteoDe`: días, momentos, gastos, ingresos, cuestionarios, cartas, experimentos, notas, metas, ideas, listas, obras, tramos, consumos, tomas, lugares, visitas, salud, pantalla, canciones, comidas, series, escalada y avisos) y, de cada archivo del zip, su **huella SHA-256** y su tamaño. Una huella es un número de 64 cifras que se calcula con todos los bytes del archivo: si cambia uno solo, la huella es otra.

**Antes de salir, se revisa** (`Respaldos.armar`, `verificarZip`). El .zip recién hecho se vuelve a abrir. De cada archivo se recalcula la huella y se compara con la del manifiesto, y `vm_respaldo.json` se decodifica completo para comparar sus cifras con las del manifiesto. Si se cifra, el .vmc se descifra otra vez a un archivo aparte y tiene que dar **el mismo zip, byte por byte** (misma huella). Si algo no coincide, el respaldo no sale y el error queda en `Config.respaldo_error`. Si no hay memoria para decodificar el JSON completo, basta con que coincidan las huellas: los bytes son los que se escribieron. **[Validado]** como práctica: comprobar lo escrito antes de confiar en ello. El costo: descifrar otra vez repite la derivación de la llave (unos segundos con 600 000 iteraciones, 10.4.3), en segundo plano.

### 10.4.2 Cómo se cifra exactamente (`Respaldos.cifrar`)

1. **Sal**: 16 bytes al azar (`SecureRandom`), nueva en cada respaldo.
2. **Llave**: PBKDF2 con HMAC-SHA256, **600 000 iteraciones** desde 10e (antes, 210 000), 256 bits (`ITERACIONES`, `llave`).
3. **IV**: 12 bytes al azar, nuevo en cada respaldo.
4. **Cifrado**: AES-256 en modo GCM con etiqueta de **128 bits**. El encabezado completo entra como **dato adicional autenticado**: no se cifra, pero si alguien lo cambia, el descifrado falla.
5. **Formato del archivo** (formato 2, desde 10e, D.104): encabezado de 27 bytes, que son la marca `VESTIGIUM-MEI-CIFRADO-2` (23 bytes) y el número de iteraciones (4 bytes, entero con el byte más significativo primero) · sal (16) · IV (12) · datos cifrados con su etiqueta. Como las iteraciones viajan en el archivo, se pueden subir más adelante sin inventar otro formato, y como van dentro del dato autenticado, cambiarlas hace fallar el descifrado.

**Por partes** (desde el 5 nov 2026, D.157). El .zip se escribe entrada por entrada, directo de la memoria o de la base al archivo: `vm_respaldo.json` sale con el codificador de flujo (`Exportar.escribirRespaldo`), y las ubicaciones y el catálogo de fotos, punto por punto (`Db.recorrerPuntos`). El cifrado y el descifrado van de archivo a archivo, en trozos de 64 KB (`Respaldos.cifrarArchivo`, `descifrarArchivo`), con el mismo formato 2. Antes se armaba todo como texto: el JSON completo, las ubicaciones completas, el .zip leído entero, su versión cifrada y la copia con el encabezado, todo a la vez en memoria. Con años de ubicaciones (por ejemplo, un historial viejo de Google Maps), la app se podía quedar sin memoria, y el respaldo automático dejaba de salir sin aviso. GCM tiene un límite que no cambia: Android guarda internamente lo cifrado hasta comprobar la etiqueta, así que al descifrar el contenido sí ocupa memoria una vez **[sin verificar]** en cada versión de Android. Nunca se escribe nada sin comprobar: el contenido descifrado solo sale después de la etiqueta. Lo comprueba `RespaldoPrueba.cifradoPorArchivos`: lo cifrado por archivos se abre en memoria y al revés, con otra contraseña no abre ni deja nada escrito, y un archivo truncado no abre.

**Los dos formatos** (`Respaldos.formato`, `descifrar`). La app escribe siempre el formato 2, pero sigue abriendo el **formato 1** de las versiones anteriores: marca `VESTIGIUM-MEI-CIFRADO-1` (23 bytes, el único dato autenticado) · sal · IV · datos, siempre con 210 000 iteraciones (`ITERACIONES_1`). Al leer un formato 2, si las iteraciones del encabezado no están entre 100 000 y 10 000 000 (`ITER_MIN`, `ITER_MAX`), el archivo se da por dañado sin intentar descifrarlo: una cifra enorme colgaría la app derivando la llave. Lo comprueban las pruebas JVM `RespaldoPrueba` (los dos formatos y encabezados dañados; D.108). La consecuencia práctica: **una versión de la app anterior a 10e no abre un .vmc nuevo**; para restaurar en otro teléfono, primero hay que instalar esta versión.

**Qué protege.** Sin la contraseña, el contenido es ilegible, y cualquier cambio o daño del archivo hace que el descifrado **falle** (no devuelve datos alterados). Como la sal y el IV son nuevos en cada respaldo, nunca se repite una combinación de llave e IV. Las elecciones (AES-GCM con IV de 96 bits y etiqueta de 128; PBKDF2 con sal de 128 bits) siguen las recomendaciones del NIST (Dworkin, 2007; Turan et al., 2010). **[Validado]**.

**Qué no protege.** El tamaño del archivo y sus fechas se ven. Y el punto débil de todo cifrado con contraseña es **adivinar la contraseña**: quien tenga el .vmc puede probar contraseñas sin límite en su propia computadora.

### 10.4.3 Las iteraciones (H-8-9, resuelto en 10e)

Las iteraciones son el «peaje» por cada intento de adivinar. La guía de OWASP para guardar contraseñas recomienda, para PBKDF2 con HMAC-SHA256, **600 000 iteraciones** (y 220 000 para HMAC-SHA512; cifras comprobadas en la página el 28 de octubre de 2026) (OWASP, s. f.). **Desde 10e la app usa exactamente eso**: 600 000 con SHA-256 (`PBKDF2WithHmacSHA256`, `ITERACIONES = 600_000` en `data/Respaldos.kt`, D.104). **[Validado]**: sigue la recomendación actual. Hasta 10e usaba **210 000**, unas 2.9 veces menos peaje que lo recomendado para SHA-256. (Algunas fuentes secundarias atribuyen a una versión anterior de la guía 210 000 para SHA-512, la cifra que tenía la app; no lo pude confirmar en la fuente **[sin verificar]**. Aunque fuera así, la app usaba SHA-256.) Los .vmc hechos antes de 10e siguen con 210 000: no se vuelven a cifrar solos, y quedan protegidos como antes mientras existan. Cuánto tarda en el teléfono derivar la llave con 600 000 iteraciones no se midió; el código lo hace fuera del hilo principal, y su comentario lo estima en «un segundo o más» **[sin verificar]**.

**Ejemplo (cuentas exactas, sin velocidades).** La app exige al menos **8 caracteres** (`ui/pantallas/Respaldo.kt`). Si fueran 8 letras minúsculas al azar, hay 26⁸ ≈ 2.1 × 10¹¹ contraseñas posibles; probarlas todas cuesta, con las 600 000 iteraciones de ahora, 2.1 × 10¹¹ × 600 000 ≈ 1.3 × 10¹⁷ cálculos de HMAC; con las 210 000 de antes de 10e eran ≈ 4.4 × 10¹⁶. Pero una contraseña humana (una palabra con un número) no es aleatoria y se adivina mucho antes. **La longitud y el azar de la contraseña importan mucho más que las iteraciones.** No doy tiempos, porque dependen del equipo del atacante y no los verifiqué.

### 10.4.4 La contraseña en el teléfono

`ponerContrasena` guarda la contraseña **cifrada con una llave AES-256-GCM del Android Keystore** (en las preferencias privadas `respaldo_secreto`). Así la app puede hacer respaldos cifrados sola, cada día, sin pedírtela. La contraseña **nunca viaja** en ningún respaldo. Si la olvidas, los .vmc no se pueden abrir: la propia pantalla lo advierte.

**Detalle** (H-8-15, resuelto en 10e): si cambias la contraseña, las copias que ya existen (en Drive, las semanales y mensuales, y las que guardaste o compartiste) siguen cifradas con la **anterior**; la app no las vuelve a cifrar. Para restaurar una copia vieja hará falta recordar esa contraseña. Hasta 10e no se avisaba. Desde 10e (D.104, `ui/pantallas/Respaldo.kt`): al **cambiarla**, el diálogo de la contraseña nueva lo advierte antes de escribirla y el mensaje final lo repite; al **quitarla**, la app pide confirmar («¿Quitar la contraseña?») y explica que las copias cifradas la siguen pidiendo. **[Diseño propio]**: avisar es lo mínimo; recifrar las copias viejas no se hizo.

---

## 10.5 Respaldos automáticos

### 10.5.1 A una carpeta (`Respaldos.aCarpeta`)

Una vez al día (la primera vez que corre la tarea diaria después de las 6:00, `Trabajo.diario`), si elegiste una carpeta:

- **Con contraseña**: se escribe solo `vestigium_respaldo.vmc` y se **borran** los archivos en claro que hubiera de antes (`vm_*.csv`, `vm_*.json`, `LEEME.txt`, `vestigium.R`, `vestigium_*.zip`). Desde el esquema 13 (P-09) se borran **después** de que el .vmc quedó escrito: si la escritura falla, la copia en claro sigue ahí (H-9-37).
- **Sin contraseña**: se escriben **todos** los CSV de la exportación, el diccionario, `vm_respaldo.json`, `vestigium.R` y `vestigium_respaldo.zip`, en claro.
- **Sin contraseña y con datos privados** (desde el módulo 10a): **no se escribe nada** (`Respaldos.bloqueado`). Hoy muestra «Tus respaldos están en pausa…» (el primero de «Te falta hoy») y, desde el 9 nov 2026, llega una notificación **cada día** (antes, una por semana) hasta que pongas contraseña.
- **Copias con fecha** (desde el 9 nov 2026, D.166). Antes había **un solo archivo**, que se reescribía cada día: un día malo pisaba la única copia buena de la carpeta. Ahora `vestigium_respaldo.zip` (o `.vmc`) es solo «la última», y en la subcarpeta `copias` quedan:
  - una por día, `vestigium_dia_AAAA-MM-DD`: las últimas **7**;
  - una por semana, `vestigium_semana_<lunes>`: las últimas **8**;
  - una por mes, `vestigium_mes_AAAA-MM`: las últimas **12**;
  - la **base de datos tal cual**, una vez por semana (`vestigium_bd_<lunes>.sqlite`, cifrada como `.sqlite.vmc` si hay contraseña): las últimas **4**. Se hace con `VACUUM INTO` (`Db.copiaCruda`), que escribe una copia coherente aunque la app esté escribiendo. Es la red por si algún día el JSON del respaldo no se pudiera leer.

  La limpieza la decide `Respaldos.sobran`. Con contraseña, ninguna copia en claro se queda en `copias`. Los números 7, 8, 12 y 4 son **[Diseño propio]**, iguales a los de Drive.
- **Guarda contra respaldos que encogen** (desde el 9 nov 2026, D.166), igual que Drive (10.5.2). Si el nuevo trae menos del 90 % de los días o de los momentos del último que reemplazó la copia (`Config.respaldo_conteo`, solo si ese tenía 20 o más), no se toca nada. Se guarda aparte como `vestigium_revisar` y llega una notificación. «Aceptar el respaldo más pequeño» (o «Respaldar ahora») en Datos lo acepta y fija el nuevo punto de comparación (`aCarpeta(forzar = true)`).
- **Se lee de vuelta** (desde el 9 nov 2026). Después de escribir la última en la carpeta, se lee desde la carpeta y su huella tiene que ser la del archivo que se escribió; si no, cuenta como falla («¿Falta espacio?»). Solo entonces se anotan `respaldo_ultimo`, `respaldo_verificado`, lo que traía (`respaldo_detalle`: días, momentos y tamaño) y su conteo.
- **Sin dejarlo a medias** (desde el 5 nov 2026, D.151). El respaldo (`.zip` o `.vmc`) se escribe primero a un archivo aparte (`….nuevo`) y solo cuando está completo toma el nombre del respaldo; el anterior se borra al final (`Respaldos.escribirSinRomper`). Antes se escribía encima del único respaldo, y si la tarea se cortaba o el teléfono se apagaba a la mitad, quedaba truncado. Si la carpeta no deja renombrar archivos, se escribe encima como antes. Los CSV sueltos se siguen escribiendo encima: son una copia de lo que ya lleva el respaldo.
- **Si falla, avisa** (desde el 5 nov 2026, D.157; más pronto desde el 9 nov 2026, D.166). Si el respaldo a la carpeta no se pudo escribir (sin espacio, la carpeta ya no deja escribir, sin memoria, no pasó la revisión…) y van **más de 2 días** desde el último, llega una notificación **cada día** con el motivo: «Tu respaldo automático no está saliendo». Hasta el 9 nov 2026 era a los 3 días y una vez por semana. Los 2 días son **[Diseño propio]**.

### 10.5.1 bis El estado de los respaldos y el ensayo del mes (desde el 9 nov 2026, D.166)

- **Semáforo en Datos** (`EstadoRespaldos`, `Respaldos.luz`). Hay una luz general y una por destino: carpeta, Drive, base de datos y ensayo del mes. **Verde** si la última copia buena tiene 36 horas o menos; **amarilla** hasta 48; **roja** después, o si nunca hubo, si los respaldos están en pausa o si no hay ningún destino elegido. La general mira la copia buena más reciente fuera del teléfono (`Respaldos.ultimaFuera`: la carpeta revisada o Drive). Las 36 y 48 horas son **[Diseño propio]**: con un respaldo diario a partir de las 6:00, un día sin copia ya es amarillo.
- **En Hoy**, el aviso del respaldo va **primero** en «Te falta hoy». Sale cuando los respaldos están en pausa, cuando **no hay ningún destino** elegido y ya hay 3 días registrados («Tus datos solo están en este teléfono»), y cuando pasan 36 horas sin copia revisada, con el motivo del último error.
- **El ensayo del mes** (`Respaldos.ensayo`, tanda mensual de la tarea de fondo). Una vez al mes se abre la última copia de la carpeta como si se fuera a restaurar: se descifra con la contraseña guardada, se compara con su manifiesto y se cuentan sus días y momentos contra los del teléfono. El resultado queda en `Config.respaldo_ensayo` y se ve en Datos. No cambia nada. Es la versión automática de «un respaldo que nunca se probó no es un respaldo» (10.6). **[Diseño propio]**.

### 10.5.1 ter La cápsula del año (desde el 9 nov 2026, D.168)

`Respaldos.capsula` arma el año entero en un archivo que se lee **sin la app**:
- todas las tablas CSV con su diccionario, la misma información en `vm_datos.sqlite`, `vm_respaldo.json` y `vestigium.R`;
- las fotos del día de ese año;
- el libro del diario del año en PDF (capítulo 9).

Va a `capsulas/` en la carpeta de respaldo y nunca se borra sola. Con contraseña, va cifrada. Sale sola la primera mañana de enero con la del año que terminó, y se puede pedir en Datos. Después de escribirla, se lee de vuelta y se compara su huella. Las tablas llevan todos los años registrados hasta ese día, no solo el de la cápsula. **[Diseño propio]**.

### 10.5.2 A Google Drive (`data/Drive.kt`)

- Usa el permiso **`drive.file`**: la app solo ve los archivos que ella misma creó, **nunca el resto de tu Drive**. **[Validado]** como el permiso mínimo necesario.
- **Por partes** (desde el 9 nov 2026, D.168). El respaldo sube con una sesión reanudable de Google (`Red.subirPorPartes`, `uploadType=resumable`): el contenido sale directo del archivo, sin cargarlo en memoria. Al restaurar o verificar, la copia se baja directo a un archivo (`Red.bajarA`). Antes, el respaldo entero y otra copia para el envío se leían en memoria, y con años de datos la subida podía fallar.
- Todo va a una carpeta «Vestigium Mei». Cada día **reemplaza** el respaldo del día (y, sin contraseña, los CSV); guarda una **copia semanal** fechada con el lunes (se conservan las **8** más recientes) y una **mensual** (las **12** más recientes).
- Con contraseña, solo sube el .vmc y borra de Drive los archivos en claro anteriores; sin contraseña, sube los CSV y el .zip en claro. Desde el esquema 13 (P-09), los archivos en claro se borran solo **después** de subir el .vmc (H-9-37).
- **No se reemplaza una copia buena por una mucho más pobre** (esquema 13, P-09). Antes de subir, `Respaldos.empobrecido` compara cuántos días y momentos trae el respaldo nuevo con los del último que reemplazó la copia (`Config.drive_conteo`). Si el nuevo trae **menos del 90 %** de cualquiera de los dos (y el anterior tenía 20 o más), no toca la copia del día, la semanal ni la mensual. En su lugar lo guarda aparte como `vestigium_revisar` y avisa con una notificación. «Restaurar desde Drive» nunca elige esa copia. Si el borrado fue a propósito, «conectar y subir ahora» (a mano) reemplaza la copia y fija el nuevo punto de comparación. El umbral de 90 % y el mínimo de 20 son **[Diseño propio]**: borrar a propósito muchos días también dispara el aviso, y eso es lo que se busca (H-8-14).
- Con datos privados y sin contraseña, no sube nada (desde el módulo 10a), igual que la carpeta. Tampoco se puede **compartir** el respaldo completo desde la pantalla Respaldo: el botón se desactiva y explica por qué.
- Si el permiso de Google caduca, avisa una vez por semana.

### 10.5.3 Cuándo falla

- **Sin contraseña, todo queda en claro fuera del teléfono** (H-8-5), incluido el diario completo. Desde el módulo 10a, si hay datos privados, el respaldo no sale sin contraseña. La **exportación para R** (resumida o extendida, que pides a mano y compartes) es la excepción: R necesita leer los CSV, así que va sin cifrar, y la pantalla advierte que lleva los datos privados.
- **El costo de esa regla:** si nunca pones contraseña, los respaldos automáticos quedan en pausa mientras haya datos privados. Por eso el aviso es visible en Hoy y llega una notificación semanal.
- **Las cartas selladas** van **sin texto** en `vm_cartas.csv` («no se pueden leer antes de su fecha»), pero `vm_respaldo.json`, que se exporta junto, lleva **el texto completo** (H-8-5). El sello se puede saltar abriendo el JSON.
- **Hasta el esquema 12, un respaldo malo podía reemplazar a uno bueno** (H-8-14). Si un día los datos del teléfono se dañaban o se perdían (por ejemplo, tras reinstalar), el respaldo de ese día **sobrescribía** el anterior sin comprobar si traía muchos menos datos, y la copia semanal también salía mala si era la primera de la semana. Desde el 13, Drive aparta un respaldo mucho más pobre (sección 10.5.2). Lo que sigue igual: «Restaurar desde Drive» **toma siempre la más reciente** (`Drive.ultimaCopia`, por fecha de modificación, sin contar `vestigium_revisar`). Para traer una semanal o mensual anterior hay que bajarla a mano y usar «Restaurar desde un archivo».

### 10.5.4 Los reportes, en carpetas públicas (H-8-4)

Los reportes semanales y mensuales se generan **solos** (capítulo 9, sección 9.3.1) y se guardan con `MediaStore` en `Pictures/VestigiumMei` y `Download/VestigiumMei`, fuera del almacenamiento privado de la app. Ahí los ve la galería, cualquier app con permiso para leer imágenes o descargas y, si está activada, la copia automática de fotos del teléfono, que puede subirlos a la nube sin que lo decidas. El reporte excluye el seguimiento privado (desde el módulo 10a también los impulsos), las calles y los nombres de lugares, pero incluye ciudades, peso, dinero y ánimo. Hasta v84 incluía también los **nombres de personas** (salvo en modo discreto, apagado por omisión) y la **frase del mejor momento** del diario. Desde el módulo 10a, los reportes **que se guardan solos** (lunes y día 1) van siempre en modo discreto, con iniciales, y sin esa frase (`Reportes.reporte(automatico = true)`); los que pides a mano siguen como antes. **[Diseño propio]**: la idea de reportes «para compartir» es coherente, pero guardarlos solos en una carpeta pública convierte cada lunes en una salida de datos que no elegiste.

El **reporte para terapia** (desde D.119) va por otro camino: no se genera solo ni pasa por `MediaStore`. Se escribe en la carpeta interna de la app (`cache/export/terapia/`, la única que comparte el `FileProvider` de `res/xml/archivos.xml`), se borra al generar el siguiente y sale del teléfono solo por el menú de compartir, a donde elijas. Lleva más datos sensibles que los otros reportes (consumo, alimentación, textos de «lo mejor» y «lo peor» y, si lo marcas, lo privado), pero cada salida es una decisión tuya en ese momento. Lo que se haga con el archivo después (un correo, un chat) ya no lo controla la app.

---

## 10.6 Verificar sin restaurar

`Respaldos.verificar` (desde un archivo) y `Drive.verificar` (la copia más reciente de Drive):

1. Si está cifrado, lo descifra (con la contraseña guardada o la que escribas). **Si el archivo está dañado, aquí falla**, gracias a la etiqueta de GCM.
1. bis. Desde el 9 nov 2026 (D.166), si el .zip trae manifiesto, recalcula la huella de cada archivo y la compara (`verificarZip`). Si no coincide, lo dice y no sigue: «El respaldo no coincide con su manifiesto». Los respaldos de antes no traen manifiesto y se revisan como antes.
2. Lee el .zip (`contenido`): decodifica `vm_respaldo.json` completo y cuenta las líneas de ubicaciones y del catálogo de fotos y las portadas. Desde el 5 nov 2026 (D.157), el archivo se copia y se descifra a archivos temporales, y el JSON se decodifica directo del zip, sin pasarlo a texto.
3. Muestra un **informe** (`informe`): fecha y esquema del respaldo, días (y rango de fechas), días con diario, sueños, momentos, tramos, gastos, cartas, obras, días de salud y de pantalla, canciones, comidas, lugares, visitas, ubicaciones, fotos con lugar, portadas y algunos ajustes; y debajo, cuántos días, momentos y gastos hay **en este teléfono**.

**Qué garantiza.** Que el archivo se abre, que no fue alterado (si está cifrado) y que el JSON se puede leer de punta a punta. Al leer las entradas del .zip, Java comprueba la suma de verificación (CRC-32) de cada una; esto no lo comprobé en la documentación durante esta sesión **[sin verificar]**. Es una práctica muy buena: **un respaldo que nunca se probó no es un respaldo**. **[Validado]** como práctica.

**Qué no garantiza** (H-8-12):

- Hasta el esquema 12, el mensaje decía «**El respaldo se lee completo y no le falta nada**», pero la app **no comparaba** nada: mostraba las cifras del respaldo y las del teléfono una debajo de otra, y tú tenías que notar si no cuadraban. «No le falta nada» era más de lo que se comprobó. Desde el 13 (P-08) dice solo «El respaldo se lee completo», y pone lado a lado respaldo / teléfono en días, momentos, gastos y experimentos.
- **La versión del esquema.** Como el lector de JSON ignora los campos que no conoce (`ignoreUnknownKeys = true`), restaurar en una versión vieja de la app un respaldo hecho con una versión más nueva **perdía en silencio** los campos nuevos. Desde el esquema 13, `Importar.esquemaFuturo` **rechaza** restaurar un respaldo con `schema_v` mayor que el de la app, antes de tocar nada (ni días, ni ubicaciones, ni portadas), y «Verificar» lo advierte. Al revés, los campos que falten se siguen llenando con valores por omisión (H-1-4).

---

## 10.7 Restaurar: cómo se combinan los datos

`Respaldos.restaurar` acepta el .vmc, el .zip completo, la exportación extendida o el JSON suelto. La pantalla promete: «Lo que ya tengas aquí no se duplica ni se borra». Con la app vacía, se trae todo, incluidos los ajustes. Con datos, `Importar.respaldoTexto` **combina**, pero con reglas distintas según la colección (H-8-13):

| Colección | Si ya existe en el teléfono… |
|---|---|
| Días | **gana el teléfono**: el día del respaldo se ignora (`importarDias(sobrescribir = false)`) |
| Momentos, reacciones, obras, ascensos, chats, YouTube | se agregan los que no existan (por identificador) |
| Salud, canciones | **gana el respaldo**: se guardan encima. Desde el esquema 14, con una excepción: el sueño que corregiste a mano en el teléfono (`sueno_manual`) no lo pisa un respaldo que no lo tenía corregido (`Fusion.salud`; H-NAV-5) |
| Pantalla, lugares, entorno, fotos, agenda, sesiones (desde el 5 nov 2026, D.150) | **gana el teléfono**: del respaldo solo entran las fechas o los identificadores que no estén. Hasta entonces ganaba el respaldo: un lugar que renombraste o al que le cambiaste el radio volvía a su versión vieja |
| Cartas (desde el 5 nov 2026, D.150) | gana el teléfono, pero una carta que en el respaldo ya estaba abierta queda abierta. Hasta entonces ganaba el respaldo: una carta que abriste después del respaldo volvía a quedar sellada |
| Experimentos (desde el esquema 13) | gana el teléfono, pero se conserva lo que solo trae el respaldo: la fecha de fin (un respaldo viejo no reactiva uno terminado) y las respuestas de «¿Lo hiciste hoy?» (`Fusion.experimento`). Hasta el 12, ganaba el respaldo |
| Gastos e ingresos | se agregan los que no existan (por identificador). Hasta el esquema 12, los repetidos se sobrescribían en la base pero no en memoria, y al reiniciar aparecía en silencio la versión del respaldo (H-9-28) |
| Avisos de ánimo (desde el esquema 13) | se juntan los dos: el mayor número de planeados y la unión de vencidos y mostrados |
| Personas y fechas propias (desde el esquema 13) | las personas del teléfono mandan, pero toman la fecha de nacimiento o de fallecimiento que solo tenga el respaldo; las fechas propias se suman |
| Laptop, comunicación, conexiones | se agregan solo las fechas nuevas |
| MacroFactor (nutrición, comidas, series, entrenos) | solo si en el teléfono **no hay nada** de MacroFactor |
| Ajustes | se combinan (`combinarConfig`). El historial de zonas horarias (`Config.zonas`, desde el esquema 15; capítulo 2) se junta: los cambios de los dos, sin repetir instantes (si coinciden, manda el del teléfono) |
| Ubicaciones | se agregan las que no existan (por milisegundo); las de fotos se rehacen del catálogo |

**Por qué importa.** Si restauras un respaldo sobre un teléfono que tiene una versión **incompleta** de un día (por ejemplo, solo el sueño de esa mañana), el diario completo del respaldo para ese día **no se recupera**. En cambio, para la salud y las canciones, un respaldo viejo puede **pisar** datos más nuevos del teléfono. Ninguna de las dos cosas se avisa. Un lugar que quitaste en el teléfono vuelve si el respaldo lo trae: «combinar» no distingue lo que borraste de lo que nunca tuviste.

**Un elemento ilegible ya no tumba todo** (desde el 5 nov 2026, D.157). `Importar.leer` decodifica el respaldo directo del archivo. Si así no se puede (un momento con un dato que esta versión no entiende, por ejemplo), `Importar.tolerante` revisa cada colección que falla elemento por elemento: se quedan los que se leen y el mensaje final dice cuántos se saltaron y de qué colección. Si lo que no se lee son los ajustes, no se toman como si el teléfono fuera nuevo: los de aquí se quedan como están. Antes, un solo elemento hacía fallar la restauración entera con «No pude abrir el respaldo». Lo comprueba `RespaldoPrueba.unElementoIlegibleNoTumbaElRespaldo`. Hasta el 9 nov 2026, los documentos que la app ya no podía leer en el teléfono (los que cuenta Estado de los datos) no entraban en ningún respaldo. Desde el esquema 22 viajan con su texto tal cual en `vm_extra.json` (10.4.1). **[Diseño propio]**: saltar y avisar en vez de rechazar todo.

**En lote** (desde el 5 nov 2026, D.157). Momentos, respuestas de cuestionarios, pruebas de reacción, días de salud y canciones se guardan en una sola transacción y la lista en memoria se reordena una vez. Antes, cada momento se buscaba en toda la lista, se guardaba suelto y la lista se reordenaba entera: con miles de momentos, la restauración podía congelar la app. Las respuestas de cuestionarios que ya están en el teléfono (mismo identificador) ya no se vuelven a escribir.

**Durante la restauración** (desde el 5 nov 2026, D.150). La restauración corre en otro hilo. Los días se mezclan ya en el hilo principal, sobre lo que haya en ese momento: antes, un día que guardaras mientras se restauraba desaparecía de la memoria, y la siguiente edición partía de la versión vieja. Los avisos de ánimo del respaldo también pasan a la copia de trabajo de la tarea de fondo, que si no los borraba con el siguiente aviso del día.

---

### 10.7.1 La papelera (desde el esquema 14)

Antes de borrar un registro que hiciste tú (momentos, tramos, consumos, notas, metas, tomas, gastos, ingresos, lugares, cartas, sesiones de escalada y obras), `Repo` copia el documento tal como estaba a la colección `papelera` (`Borrado`: colección, id, el documento y cuándo se borró). Lo que lleva más de 7 días se vacía en la tarea diaria (`Repo.limpiarPapelera`). Lo que volvió con «deshacer» deja de contar como borrado. La papelera **no viaja** en los respaldos ni en la exportación. Lo que vive en los ajustes (fechas propias, logros propios, ciudades) no pasa por la papelera; para eso queda «deshacer». Desde la entrega N7 de la revisión de navegación hay una pantalla para recuperar (Más → Datos → «Papelera»): lista lo borrado con cuántos días le quedan y «recuperar». **Lo privado no aparece ahí** (ni siquiera se cuenta): un impulso, una recaída o la libido borrados se recuperan en la papelera de la pantalla Privado (`Papelera.esPrivado`, vigilado por `PrivadoPrueba`). Una sustancia que ocultaste sale como «Un registro oculto». **[Diseño propio]**.

## 10.8 Servicios externos

La app no incluye herramientas de analítica, publicidad ni reporte de errores (no hay dependencias de ese tipo en `app/build.gradle.kts`). Habla con estos servicios:

| Servicio | Para qué | Qué se envía |
|---|---|---|
| **Google Drive** (`googleapis.com`) | Respaldos (sección 10.5), solo si lo activas | Los archivos de respaldo (cifrados o no) |
| **Open-Meteo** (pronóstico, archivo histórico y calidad del aire) | Clima y aire del día (capítulo 4) | La **mediana de las ubicaciones del día, redondeada a 2 decimales** (`Entorno.r2`: ≈ 1.1 km en latitud) y las fechas. Desde 10f (D.111), los días del último año que vinieron del pronóstico se vuelven a pedir al archivo cuando pasan 80 días (`DIAS_ARCHIVO`): más consultas, pero con el mismo tipo de dato |
| **Spotify** | Historial de canciones (capítulo 4) | Autorización con redirección a `127.0.0.1` (solo dentro del teléfono) y luego consultas con tu permiso |
| **Catálogos de obras**: TMDB, RAWG, TVmaze, Google Books, Open Library, Crossref | Portadas y fichas de libros, películas, series, juegos y artículos | El **título o DOI** que buscas |
| **Mapas de OpenStreetMap** (osmdroid, mosaicos `MAPNIK`) | Fondo del mapa | Qué zona del mapa estás viendo (al pedir los mosaicos) |
| **Wikipedia en español** (`es.wikipedia.org/api/rest_v1/feed/onthisday/events/MM/DD`), desde el 13 nov 2026 (D.191) | «Hoy en la historia»: lo que pasó un día como hoy | **Solo el mes y el día** de hoy (al abrir la pantalla, si lo guardado no vale) o de mañana (el trabajo de 15 minutos, una vez al día). Se puede apagar en Ajustes › Privacidad |
| **Health Connect, uso del teléfono, ubicación** | Datos pasivos (capítulo 4) | Nada: se leen **dentro** del teléfono |

**«Hoy en la historia»** (`historia/Efemerides.kt`, D.191). La dirección que se pide lleva el mes y el día, y nada más: ni la fecha completa, ni el año, ni el tema, ni nada que salga de tus registros. Como en toda conexión, Wikipedia ve la dirección de red del teléfono. Lo que llega (el año, el texto y hasta tres artículos enlazados de cada acontecimiento) se guarda en `filesDir/historia/`, un archivo por fecha, **fuera de la base, del respaldo y de la exportación**: no es un dato tuyo y no cambia nada de lo que se registra. Lo guardado vale 30 días (`DIAS_VIGENTE`); el trabajo de 15 minutos deja lista la fecha de mañana y borra las fechas viejas, así que la carpeta ronda 1 MB. Con el interruptor apagado (`Config.historia_hoy`) no se conecta, se borra lo guardado y el cuadro de Hoy pierde la banderita y la pulsación larga. **[Diseño propio]**

El documento de contexto dice que «nada pasa por servidores ajenos salvo Drive del usuario, Open-Meteo (clima) y Spotify API»; la tabla muestra que hay más servicios, aunque los demás solo reciben títulos de obras o la zona del mapa que miras (H-8-17). (Google Books, que `Portadas` consulta para buscar libros, faltaba en esta tabla hasta esta actualización.) **Desde 10e** (D.103), el texto de privacidad de la app (`ui/PrivacidadActivity.kt`) los nombra todos y dice qué recibe cada uno: Open-Meteo, la zona aproximada y las fechas; OpenStreetMap, qué zona miras; los catálogos, solo el título o el DOI que escribes; Spotify, la petición de lo que escuchaste; Drive, tus respaldos. El documento de contexto no está en el repositorio y hay que corregirlo a mano. El texto dice también que Google Drive recibe los respaldos completos, diario incluido (cifrados si pones contraseña, en claro si no; sección 10.5), y que los demás servicios no reciben tu diario, tu salud ni tu ruta.

**Desde 10f, la lista está dentro de la app** (D.116; H-8-17, resuelto). Ajustes › Privacidad termina con una sección plegada, «Qué sale del teléfono», cerrada por omisión para que no estorbe (`ui/pantallas/Sistema.kt`). Abierta, muestra cada servicio y qué recibe, y una nota: Health Connect, las fotos, la ubicación y el diario se quedan en el teléfono, y solo salen al exportar, al elegir una carpeta de respaldo o al activar Google Drive. La lista sale de un solo lugar, `Red.SERVICIOS` (`data/Red.kt`), que usa también el texto de privacidad (`ui/PrivacidadActivity.kt`, que ahora termina con «Fuera de Google Drive, ninguno recibe tu diario, tu salud ni tu ruta»). Tiene **5 entradas**: Open-Meteo (la zona aproximada, a ~1 km, y las fechas), OpenStreetMap (los mosaicos de la zona que miras), los seis catálogos de obras juntos (solo el título o el DOI que escribes), Spotify si lo conectas (el permiso para leer lo que escuchaste) y Google Drive si lo activas (los respaldos completos, cifrados si pones contraseña). Por eso el resumen de la sección dice «5 servicios», aunque los catálogos son seis. Coincide con la tabla de arriba. El autor revisó la lista y está de acuerdo con lo que se envía (estado de H-8-17). **Desde el 13 nov 2026** (D.191) la lista tiene **6 entradas**: se sumó Wikipedia en español, que recibe solo el mes y el día para «Hoy en la historia»; el resumen de la sección dice ahora «6 servicios». **[Diseño propio]**: tener una sola lista evita que la pantalla y el texto digan cosas distintas, pero nada en el código obliga a anotar ahí un servicio nuevo; lo dice solo el comentario de `Red.SERVICIOS` («si se agrega un servicio, va aquí»), y ninguna prueba lo vigila.

**[Validado]** como enfoque general: el procesamiento es local y solo salen las consultas necesarias, con la ubicación redondeada para el clima. Lo que no es mínimo es el respaldo sin contraseña. No verifiqué las políticas de privacidad de cada servicio en esta sesión **[sin verificar]**.

---

## 10.9 Integridad: lo que ya se vio en otros capítulos

- **Documentos que no se pueden leer.** Hasta el esquema 12 se descartaban en silencio (H-1-3). Desde el 13 (P-07) se cuentan por colección y se muestran en «Estado de los datos» y en Hoy, y una configuración ilegible se copia aparte antes de quedar en blanco (H-9-2; capítulo 2). Los **valores inválidos se convierten en valores por omisión** (H-1-4).
- **Versión del esquema**: cada día guarda `schema_v` y cada respaldo el suyo (capítulo 2); desde 10f el vigente es el **15** (`SCHEMA_V` en `data/Modelo.kt`). Como un respaldo de un esquema más nuevo no se restaura (sección 10.6), una app de la v91 o anterior (esquema 14) no restaura un respaldo de la v92. Al revés, un respaldo de antes del 15 se restaura con los campos nuevos en su valor por omisión: sin fuente del clima (al exportar se deduce de la fecha en que se pidió, `Entorno.fuente`, D.111), con 0 minutos de wifi sin dato (el campo no existía), con los experimentos sin huella (`intacto` vacío) y sin fuerza de referencia. Esta última se calcula de las series del respaldo al restaurarlo (`Repo.completarFuerzaReferencia`, D.114), y además una sola vez para lo que ya estaba en el teléfono (la marca `fuerza_ref` en `avisos/Trabajo.kt`). No lo probé en un teléfono. Hasta el 5 nov 2026 el cálculo al restaurar no hacía nada: recorría la lista de entrenos en memoria, que todavía estaba vacía, y como la marca `fuerza_ref` ya estaba puesta, nunca se repetía. Ahora recibe los entrenos del respaldo (D.150).

**Esquemas 16 y 17.** Después de 10f, el esquema 16 agregó el Ideario y el 17, los campos de las pantallas de captura (Apéndice D, D.127 a D.144). Con la misma regla, una app con esquema 16 o anterior no restaura un respaldo del 17. Al revés, un respaldo de antes del 17 se restaura con los campos nuevos en su valor por omisión:
- Los sueños quedan sin rareza ni pesadilla. Se traducen de su «tipo» al leerlos (`Catalogo.rarezaDe`, `pesadillaDe`).
- Los días quedan sin la marca de hábitos de tres estados, así que siguen con la regla de antes: sin marca, 0.
- Las ideas quedan sin el campo de temas y cuentan solo sus #temas del texto.
- Los momentos quedan sin intensidades ni lista de pensamientos.
- `Config.categorias_v17` queda apagado, así que Vivienda y Servicios se agregan una vez a las categorías.

No lo probé en un teléfono.
- **Desde qué versión vale cada columna** (desde 10f, D.115; H-1-8, resuelto). Hasta la v91, el diccionario no versionaba variable por variable: si una definición cambiaba, una exportación nueva y una vieja traían columnas con el mismo nombre y distinto significado, sin aviso. Ahora `vm_diccionario.csv` trae dos columnas más: `desde_version` (la versión de la app desde la que rige la definición actual de esa columna) y `cambio` (qué cambió entonces, con su fila del Apéndice D). Salen de una tabla fija en el código, `Diccionario.DESDE`, que acepta claves «archivo:columna», «columna» o familias con «*» (por ejemplo `per_*`); si coinciden varias, gana la más larga (`Diccionario.desde`). Vacío quiere decir «sin cambios desde la v84»: antes de la v85 no hay registro. *Ejemplo:* `sin_wifi_min` dice 92, «ya no incluye los minutos sin dato (D.112)»; si comparas una exportación de la v91 con una de la v92, esa columna no es la misma medida. El LEEME pide mirar las dos columnas al comparar exportaciones de fechas distintas. **[Diseño propio]**: la tabla es tan completa como quien la mantiene; una prueba JVM (`ZonasPrueba`) comprueba algunas entradas, no que estén todas.
- **El sello de los experimentos** (desde 10f, D.113; H-7-10, resuelto; el método está en el capítulo 8). Al crear un experimento, `Experimentos.sellar` le pone una **huella** (`huella`): el SHA-256 de lo sellado, unido en un orden fijo (`textoSellado`): pregunta, acción, medida, hipótesis, umbral, sentido, diseño, inicio, semanas, plan, lavado y hora del sello. El nombre, el «¿Lo hiciste hoy?» y la fecha de fin no entran, porque cambian durante el experimento. Cada vez que se muestra, la app vuelve a calcularla (`Experimentos.intacto`): si coincide, la tarjeta enseña sus primeros 8 caracteres («coincide: nada de lo sellado cambió»); si no, avisa que la pregunta, la hipótesis, el criterio o el plan ya no son los del principio. `vm_experimentos.csv` trae `huella` e `intacto` (1, 0, o vacío en los experimentos de antes del esquema 15, que no tienen huella). *Ejemplo:* si después de empezar cambias la hipótesis «dormiré mejor» por «dormiré mejor o igual», la huella cambia por completo y la tarjeta lo dice. **[Validado]** que SHA-256 detecta cambios: es una función hash criptográfica estándar, y encontrar otro texto con la misma huella no es factible en la práctica. **[Diseño propio]** cómo se usa, con un límite que conviene decir: la huella se guarda junto a los datos y **sin ninguna llave**, así que nota los cambios hechos sin querer o sin recalcularla (una edición, un respaldo que reescribe el experimento), pero no los hechos a propósito: quien edite el JSON de un respaldo puede calcular la huella nueva con la misma receta, que está en el código. Protege contra acomodar el criterio sin darse cuenta, no contra hacer trampa.
- **Estado de los datos** (`EstadoDatos.kt`, capítulo 4): muestra por fuente si llegó hoy o ayer (verde), si está atrasada (ámbar), si falta un permiso (rojo) o si está apagada (gris). Es el monitor de integridad de los datos pasivos.
- **Días importados sin sellos** (H-1-9).

---

## 10.10 Resumen de decisiones

| Decisión | Clasificación | Respaldo |
|---|---|---|
| Datos solo en el teléfono, sin copia automática de Android | Validado (buena práctica) | — |
| Bloqueo opcional con huella o credencial, 60 s | Diseño propio | — |
| AES-256-GCM, IV de 12 bytes y sal de 16 nuevos en cada respaldo | Validado | Dworkin (2007); Turan et al. (2010) |
| PBKDF2-HMAC-SHA256 con 600 000 iteraciones desde 10e, guardadas en el encabezado (formato 2); antes, 210 000 | Validado (la recomendación actual); hasta 10e, Aproximación | OWASP (s. f.): 600 000 |
| Avisar al cambiar o quitar la contraseña que las copias viejas siguen con la anterior (10e) | Diseño propio | — |
| Contraseña guardada con el Android Keystore, nunca en el respaldo | Diseño propio (razonable) | — |
| Permiso `drive.file` | Validado (mínimo necesario) | — |
| 8 copias semanales y 12 mensuales | Diseño propio | — |
| Verificar sin restaurar | Validado como práctica | — |
| Combinar al restaurar con reglas distintas por colección | Diseño propio (inconsistente) | — |
| Ubicación redondeada a 0.01° para el clima | Diseño propio (razonable) | — |
| Seguimiento privado: visible solo en la pantalla Privado, siempre en la exportación para R | Diseño propio | — |
| Sin contraseña no sale ningún respaldo si hay datos privados (salvo la exportación para R, a mano) | Diseño propio | — |
| Contar lo ilegible y apartar la configuración dañada (esquema 13) | Diseño propio | — |
| No restaurar un respaldo de un esquema más nuevo (esquema 13) | Validado como práctica | — |
| No reemplazar en Drive una copia por otra con menos del 90 % de días o momentos (esquema 13) | Diseño propio | — |
| Una sola lista de servicios externos (`Red.SERVICIOS`) para el texto de privacidad y Ajustes › Privacidad (10f) | Diseño propio | — |
| Huella SHA-256 de lo sellado en cada experimento, sin llave (10f) | Validado (SHA-256 detecta cambios); su uso, Diseño propio (no impide recalcularla a propósito) | — |
| `desde_version` y `cambio` por columna en el diccionario, desde una tabla fija en el código (10f) | Diseño propio | — |

---

## 10.11 Resumen del capítulo

1. Los datos viven solo en el teléfono, sin copia automática de Android; la app puede pedir huella. Salen solo por los respaldos que tú activas y por consultas puntuales (clima con la ubicación redondeada, títulos de obras, mapas).
2. El **respaldo cifrado** usa AES-256-GCM con sal e IV nuevos en cada archivo: bien diseñado. Desde 10e, PBKDF2 usa las 600 000 iteraciones que recomienda hoy OWASP (antes, 210 000; H-8-9), con un formato nuevo que guarda las iteraciones en el encabezado y sigue abriendo los archivos viejos; al cambiar o quitar la contraseña, la app avisa que las copias ya hechas siguen con la anterior (H-8-15). Lo que más importa sigue siendo una contraseña larga.
3. **Sin contraseña, todo sale en claro** a la carpeta y a Drive, incluido el texto de las cartas selladas (H-8-5). Desde el módulo 10a, si hay datos privados, el respaldo no sale sin contraseña; solo la exportación para R, que pides a mano, va en claro y lo advierte.
4. **El seguimiento privado** (impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado) se ve, desde el módulo 10a, **solo en la pantalla Privado** y va completo a R. Hasta v84 se colaba por los impulsos, los medicamentos y el hábito ligado (H-8-1, H-8-2, H-8-3, H-9-39). Desde la entrega 10c, las rachas cuentan solo los días respondidos «no» (H-8-8, D.75).
5. **Verificar** un respaldo es una gran idea. Hasta el esquema 12, el mensaje prometía más de lo que comprobaba y no revisaba la versión del esquema (H-8-12). Desde el 13 dice solo lo que comprueba, compara respaldo y teléfono, y un respaldo de una versión más nueva no se restaura.
6. **Restaurar** combina con reglas distintas según la colección (H-8-13). Desde el esquema 13, los gastos e ingresos no se pisan y un experimento terminado no se reactiva. En Drive, un respaldo mucho más pobre ya no reemplaza al bueno: se guarda aparte y se avisa (H-8-14).
7. Los reportes se guardan solos en carpetas públicas (H-8-4; capítulo 9). Hasta v84 llevaban nombres de personas y la frase del mejor momento; desde el módulo 10a, los que se guardan solos van en modo discreto y sin esa frase.
8. Las reglas de privacidad declaradas se cumplen en casi todo (texto de WhatsApp, notificaciones, claves, fotos, seguimiento privado). Hasta v84 fallaban en los impulsos y los medicamentos (H-8-1, H-8-2, resueltos en 10a); siguen fallando en el texto de las cartas selladas dentro del JSON (H-8-5). La lista de servicios externos estaba incompleta (H-8-17); desde 10e, el texto de privacidad de la app la da completa, y desde 10f también Ajustes › Privacidad, con la misma lista (`Red.SERVICIOS`); falta corregir el documento de contexto.
9. El módulo 8 identificó, entre los capítulos 9 y 10, **17 hallazgos** (H-8-1 a H-8-17), sin cambiar código. Después, el módulo 10 resolvió H-8-1, H-8-2, H-8-3, H-8-7, H-8-8, H-8-10, H-8-12 y H-8-16 (10a a 10c) y H-8-9, H-8-11, H-8-15 y H-8-17 (10e), y en parte H-8-4, H-8-5, H-8-13 y H-8-14. La entrega 10f cerró los dos que quedaban: H-8-6 (los logros que premian valores de la valencia se quedan, por decisión del autor, con el riesgo documentado en el capítulo 9; D.117) y la parte que faltaba de H-8-17 (la lista en los ajustes, D.116). En total: 13 resueltos, 4 en parte y ninguno pendiente.
10. **Integridad, desde 10f** (esquema 15): cada experimento lleva una huella SHA-256 de lo sellado y la tarjeta avisa si cambió (D.113), aunque sin llave no impide una trampa hecha a propósito; y `vm_diccionario.csv` dice desde qué versión rige cada columna (`desde_version`, `cambio`; D.115, H-1-8). Un respaldo de la v92 no se restaura en una app más vieja.

---

### Referencias citadas en este capítulo

Ver `referencias.md`. Citas: Dworkin (2007); OWASP (s. f.); Turan, Barker, Burr y Chen (2010).
