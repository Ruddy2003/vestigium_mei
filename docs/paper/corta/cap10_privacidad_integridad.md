# Capítulo 10. Privacidad, seguridad e integridad de los datos

> **Para qué sirve este capítulo.** Explica **dónde viven tus datos, quién puede verlos y qué tan seguro es que no se pierdan**: el almacenamiento en el teléfono, el seguimiento privado, el cifrado del respaldo, los respaldos automáticos, la verificación, la restauración y los servicios externos. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`). El seguimiento privado (impulsos, recaídas, libido, medicamentos) se describe **solo en su mecánica**, sin ejemplos ni datos personales, y sin reproducir las palabras clave que el código usa para reconocer ciertos hábitos.

---

## 10.1 Dónde viven los datos

- **En el teléfono**, en una base SQLite de documentos JSON (`vestigium.db`, capítulo 2), en el almacenamiento privado de la app.
- **Sin copia automática de Android**: el manifiesto declara `android:allowBackup="false"`, así que la copia de seguridad del sistema **no** sube la base a la nube. Los únicos respaldos son los que hace la app (sección 10.4). **[Validado]** como decisión: evita que el diario termine en una copia que no controlas.
- **La base no tiene un cifrado propio**: depende del cifrado del almacenamiento que hace Android cuando el teléfono tiene bloqueo de pantalla. Cómo funciona ese cifrado en cada versión de Android no se verificó **[sin verificar]**.
- **Bloqueo de la app** (opcional, `Config.bloqueo`, apagado por omisión): al abrirla pide huella o el bloqueo del teléfono (`BiometricPrompt`) y vuelve a pedirlo si estuvo **más de 60 segundos** en segundo plano. Si el teléfono no tiene ningún bloqueo configurado, la app no puede pedirlo y queda abierta. Protege contra miradas casuales, no contra alguien con acceso técnico al teléfono. **[Diseño propio]**.

---

## 10.2 El seguimiento privado

### 10.2.1 Qué es y dónde se ve

El **seguimiento privado** son los impulsos, recaídas, libido, detonantes, medicamentos y el hábito ligado a las recaídas. Desde el módulo 10a hay **una sola regla, en un solo archivo** (`data/Privado.kt`). Es privado:

- todo momento de tipo `impulso`, `recaida` o `libido` (`Momento.privado`, que usa `Privado.TIPOS`);
- en el día, la intensidad del impulso (0 a 5), si hubo recaída, los detonantes y los **medicamentos**;
- el **hábito ligado a las recaídas**: con el interruptor encendido, el que elegiste en Privacidad o, si no elegiste, un hábito activo cuyo nombre contenga ciertas palabras clave; con el interruptor apagado, solo el que elegiste.

**Dónde se ve: solo en la pantalla Privado** (Más → Privado). **Dónde no se ve: en ninguna otra parte de la app**, ni con el interruptor encendido: ni en Día, Hoy, calendario, mapa o widgets; ni en reportes, «Tu año», logros (también los propios), campañas o experimentos; ni en la red de personas, las variables `per_`, `pen_` y `tema_` o las listas de análisis (`Estadistica.variables()` excluye las variables privadas). La lista completa está en la versión completa, §10.3.1.

**El interruptor `Config.seguimiento_privado`** (apagado por omisión) solo decide qué se **ofrece** en la pantalla Privado: apagado, medicamentos y momentos de impulso; encendido, también la intensidad del impulso del día, recaídas, libido y detonantes. Fuera de Privado, nunca se muestra.

**La exportación para R lleva todo, siempre.** El panel calcula las variables privadas aunque el interruptor esté apagado, y la exportación las lleva; desde la entrega 10d, `vm_variables.csv` las marca (`privada` = 1). Por omisión, `vm_relaciones()` y `vm_factores()` de R **no** las cruzan, para dar lo mismo que la app; con `privadas = TRUE` sí.

**Registrar una recaída** (`Recaidas.registrar`) guarda el momento con hora y lugar, marca el día con recaída y **desmarca el hábito ligado**. El **resumen privado** (`Recaidas.resumen`) incluye, entre otras cosas, cuántos impulsos pasaron **sin recaída en las 3 horas siguientes** y las rachas (versión completa, §10.3.1).

**Lo que no se puede filtrar:** si escribes algo privado en el texto libre del diario o de un momento común, la app no tiene forma de saberlo.

### 10.2.2 Las rachas: solo los días respondidos (H-8-8)

**Hasta la entrega 10c**, `Recaidas.resumen` empezaba a contar desde el primer día con respuesta y, a partir de ahí, **todo día que no era de recaída contaba como limpio**, aunque no se hubiera contestado nada. Si los días sin registro son justamente los difíciles, la racha queda inflada (capítulo 3).

**Desde la entrega 10c (D.75)** las rachas salen de una función pura, `Recaidas.rachas(recaidas, limpios, hoy)`:

- un día **con recaída** (marcado en el día o con un momento de recaída) reinicia la racha a 0;
- un día cuenta como **limpio** solo si ese día se respondió «no» (`Dia.recaida == false`); suma 1;
- un día **sin respuesta** no suma ni reinicia: se cuenta aparte, como «sin respuesta» desde la última recaída. El día de hoy, mientras no se responda, no cuenta como sin respuesta.

Ejemplo con una secuencia inventada de cuatro días (sí, no, sin respuesta, no): antes, racha actual 3; ahora, **2**, con 1 día sin respuesta. Lo comprueba `CalculosPrueba.rachasSoloConRespuesta`. **[Diseño propio]**: no contar ni romper con los días sin respuesta es un punto medio; romper la racha castigaría olvidar el registro, y contarlos la inflaría.

### 10.2.3 Qué se cerró y las reglas declaradas

Hasta v84, el seguimiento privado se filtraba por seis vías, todas cerradas en el módulo 10a: los impulsos (H-8-1), «la relación del año» (H-8-2), el hábito ligado y los logros propios (H-8-3), los medicamentos (H-8-16), la exportación en claro (H-8-5, cerrada solo en lo que toca a lo privado) y las @menciones de los momentos privados (H-6-1). Detalle en la versión completa, §10.3.3.

**Por qué los logros nunca se ligan al seguimiento privado.** Un logro por «días sin recaída» convertiría el seguimiento privado en un marcador, con la presión de la ley de Campbell (capítulo 9) justo sobre los datos más delicados; uno «por registrar impulsos» premiaría registrarlos. Desde el módulo 10a la regla se cumple. Las sustancias **no** son privadas según la regla declarada.

La Figura 10.1 resume qué tipo de dato llega a cada salida. Comparadas con el código de v90, las reglas que el documento de contexto del proyecto (`VESTIGIUM_MEI_CONTEXTO.md`, v81, sección 7) llama «no negociables» se cumplen en casi todo:

| Regla declarada | ¿Se cumple? |
|---|---|
| WhatsApp y comentarios de YouTube: nunca guardar texto | **Sí** (`ChatDia` guarda solo conteos por chat y día) |
| Notificaciones: no leer su texto | **Sí** (`sensores/Oyente.kt` cuenta solo la app y la hora) |
| Tokens de Spotify y claves de API solo en el teléfono | **Sí** (preferencias privadas, fuera de los respaldos) |
| Fotos nunca se copian | **Sí** (el catálogo guarda hora, coordenadas y aparato) |
| Cartas selladas: sin contenido en búsqueda, CSV ni «Tu año» | **En parte**: `vm_respaldo.json` lleva el texto (H-8-5) |
| Seguimiento privado nunca en reportes, «Tu año», logros, mapas ni widgets; sí en R | **Sí desde el módulo 10a**, con una regla más estricta |
| «Nada pasa por servidores ajenos salvo Drive, Open-Meteo y Spotify» | **No del todo** (sección 10.7; H-8-17); desde 10e, el texto de privacidad de la app nombra todos los servicios, y desde 10f también Ajustes › Privacidad |

![Figura 10.1](figuras/fig24_flujo_privacidad.png)

*Figura 10.1. Qué tipo de dato llega a cada salida, según el código después del módulo 10a. Borde rojo: hallazgo que sigue abierto (H-8-5). Script: `figuras/fig24_flujo_privacidad.py`.*

---

## 10.3 El respaldo cifrado

**Qué contiene.** `Respaldos.archivo` arma un **.zip** con `vm_respaldo.json` (diario, momentos, cartas, ajustes… todo), `vm_ubicaciones.csv`, `vm_fotos_catalogo.csv` (solo hora y coordenadas de las fotos, **nunca la foto**) y las portadas. Si pusiste contraseña, ese .zip se **cifra** (se convierte en algo ilegible sin una llave) y se guarda como **.vmc**; el .zip en claro se borra.

**Cómo se cifra exactamente** (`Respaldos.cifrar`):

1. **Sal**: 16 bytes al azar (`SecureRandom`), nueva en cada respaldo. La sal se mezcla con la contraseña para que la misma contraseña dé llaves distintas en cada archivo.
2. **Llave**: **PBKDF2** con HMAC-SHA256, **600 000 iteraciones** desde 10e (antes, 210 000), 256 bits (`ITERACIONES`, `llave`). PBKDF2 convierte la contraseña en una llave **a propósito lenta**: repite un cálculo muchas veces (las iteraciones) para que probar millones de contraseñas cueste mucho (Turan et al., 2010).
3. **IV** (vector de inicialización, un número al azar que se usa una sola vez): 12 bytes, nuevo en cada respaldo.
4. **Cifrado**: **AES-256** en modo **GCM** con etiqueta de **128 bits**. GCM, además de cifrar, añade una **etiqueta de autenticación**: si alguien cambia un solo bit del archivo, el descifrado falla en vez de dar datos alterados (Dworkin, 2007). El encabezado completo entra como **dato adicional autenticado**: no se cifra, pero si alguien lo cambia, el descifrado falla.
5. **Formato** (formato 2, desde 10e, D.104): encabezado de 27 bytes (la marca `VESTIGIUM-MEI-CIFRADO-2` y las iteraciones en 4 bytes) · sal (16) · IV (12) · datos cifrados con su etiqueta. La app sigue abriendo el formato 1 de antes (`VESTIGIUM-MEI-CIFRADO-1`, siempre a 210 000 iteraciones), pero una versión anterior a 10e no abre un .vmc nuevo.

**Qué protege.** Sin la contraseña, el contenido es ilegible, y cualquier cambio del archivo hace fallar el descifrado. Como la sal y el IV son nuevos en cada respaldo, nunca se repite una combinación de llave e IV. Las elecciones siguen las recomendaciones del NIST (Dworkin, 2007; Turan et al., 2010). **[Validado]**. **Qué no protege:** el tamaño y las fechas del archivo se ven, y quien tenga el .vmc puede probar contraseñas sin límite en su propia computadora.

**Las iteraciones (H-8-9, resuelto en 10e).** La guía de OWASP recomienda, para PBKDF2 con HMAC-SHA256, **600 000 iteraciones** (OWASP, s. f.). Desde 10e la app usa esa cifra (`ITERACIONES = 600_000` en `data/Respaldos.kt`) **[Validado]**; hasta 10e usaba 210 000, unas 2.9 veces menos «peaje» por intento, y los .vmc de entonces siguen así. Cuánto tarda en el teléfono no se midió **[sin verificar]**. La app exige al menos **8 caracteres**; una contraseña humana no es aleatoria y se adivina mucho antes que una al azar: **la longitud y el azar de la contraseña importan mucho más que las iteraciones.** (Detalle en la versión completa, §10.4.3.)

**La contraseña en el teléfono.** `ponerContrasena` la guarda **cifrada con una llave AES-256-GCM del Android Keystore** (un almacén de llaves del sistema: la app puede usar la llave, pero no sacarla del teléfono). La contraseña **nunca viaja** en ningún respaldo; si la olvidas, los .vmc no se pueden abrir. **[Diseño propio]** razonable. Si la cambias, las copias que ya existen siguen cifradas con la **anterior**: no se recifran. Desde 10e la app lo avisa al cambiarla y pide confirmar antes de quitarla (H-8-15, D.104).

---

## 10.4 Respaldos automáticos

**A una carpeta** (`Respaldos.aCarpeta`), una vez al día (la primera vez que corre la tarea diaria después de las 6:00, `Trabajo.diario`):

- **Con contraseña**: se escribe solo `vestigium_respaldo.vmc` y se borran los archivos en claro de antes; desde el esquema 13 (P-09), **después** de que el .vmc quedó escrito (H-9-37).
- **Sin contraseña**: se escriben todos los CSV de la exportación, `vm_respaldo.json`, `vestigium.R` y `vestigium_respaldo.zip`, en claro.
- **Sin contraseña y con datos privados** (desde el módulo 10a): **no se escribe nada** (`Respaldos.bloqueado`); la pantalla Hoy lo avisa y llega una notificación por semana hasta que pongas contraseña.

**A Google Drive** (`data/Drive.kt`):

- Usa el permiso **`drive.file`**: la app solo ve los archivos que ella misma creó, **nunca el resto de tu Drive**. **[Validado]** como el permiso mínimo necesario.
- Cada día **reemplaza** el respaldo del día; guarda una **copia semanal** (se conservan las **8** más recientes) y una **mensual** (las **12** más recientes) **[Diseño propio]**. Con contraseña solo sube el .vmc; sin ella, los CSV y el .zip en claro. Con datos privados y sin contraseña no sube nada, igual que la carpeta.
- **No se reemplaza una copia buena por una mucho más pobre** (esquema 13, P-09). `Respaldos.empobrecido` compara cuántos días y momentos trae el respaldo nuevo con los del último que reemplazó la copia (`Config.drive_conteo`). Si trae **menos del 90 %** de cualquiera de los dos (y el anterior tenía 20 o más), no toca ninguna copia: lo guarda aparte como `vestigium_revisar` y avisa. El umbral de 90 % y el mínimo de 20 son **[Diseño propio]** (H-8-14).

**Cuándo falla.**

- **Sin contraseña, todo queda en claro fuera del teléfono** (H-8-5), incluido el diario completo. La **exportación para R**, que pides a mano, va siempre sin cifrar (R necesita leer los CSV) y la pantalla lo advierte.
- **Las cartas selladas** van sin texto en `vm_cartas.csv`, pero `vm_respaldo.json`, que se exporta junto, lleva **el texto completo** (H-8-5): el sello se puede saltar abriendo el JSON.
- «Restaurar desde Drive» **toma siempre la más reciente** (`Drive.ultimaCopia`, sin contar `vestigium_revisar`). Para traer una semanal o mensual anterior hay que bajarla a mano.

**Los reportes, en carpetas públicas (H-8-4).** Los reportes semanales y mensuales se generan solos (capítulo 9) y se guardan con `MediaStore` en `Pictures/VestigiumMei` y `Download/VestigiumMei`, fuera del almacenamiento privado. Ahí los ve la galería, otras apps con permiso y, si está activada, la copia automática de fotos. El reporte excluye el seguimiento privado y los nombres de lugares, pero incluye ciudades, peso, dinero y ánimo. Desde el módulo 10a, los que se guardan solos van en modo discreto, con iniciales, y sin la frase del mejor momento (`Reportes.reporte(automatico = true)`). **[Diseño propio]**: guardarlos solos en una carpeta pública convierte cada lunes en una salida de datos que no elegiste.

---

## 10.5 Verificar sin restaurar

`Respaldos.verificar` (desde un archivo) y `Drive.verificar` (la copia más reciente de Drive) descifran el archivo si está cifrado (**si está dañado, aquí falla**, gracias a la etiqueta de GCM), decodifican `vm_respaldo.json` completo y muestran un **informe** de lo que trae.

**Qué garantiza.** Que el archivo se abre, que no fue alterado (si está cifrado) y que el JSON se puede leer de punta a punta. **Un respaldo que nunca se probó no es un respaldo**: **[Validado]** como práctica. Que Java compruebe la suma CRC-32 de cada entrada del .zip no se verificó en la documentación **[sin verificar]**.

**Qué no garantizaba** (H-8-12). Hasta el esquema 12 el mensaje decía «El respaldo se lee completo y no le falta nada», pero la app **no comparaba** nada. Desde el 13 (P-08) dice solo «El respaldo se lee completo» y pone lado a lado respaldo / teléfono en días, momentos, gastos y experimentos. Además, como el lector de JSON ignora los campos que no conoce (`ignoreUnknownKeys = true`), restaurar en una versión vieja un respaldo más nuevo **perdía en silencio** los campos nuevos. Desde el esquema 13, `Importar.esquemaFuturo` **rechaza** restaurar un respaldo con `schema_v` mayor que el de la app, antes de tocar nada: desde 10f, una app de la v91 o anterior (esquema 14) no restaura un respaldo de la v92 (esquema 15). **[Validado]** como práctica.

---

## 10.6 Restaurar: cómo se combinan los datos

`Respaldos.restaurar` acepta el .vmc, el .zip completo, la exportación extendida o el JSON suelto. Con la app vacía, se trae todo. Con datos, `Importar.respaldoTexto` **combina**, pero con reglas distintas según la colección (H-8-13). Las principales (tabla completa en la versión completa, §10.7):

| Colección | Si ya existe en el teléfono… |
|---|---|
| Días | **gana el teléfono** (`importarDias(sobrescribir = false)`) |
| Momentos, gastos e ingresos | se agregan los que no existan (por identificador) |
| Salud, pantalla, lugares, cartas, entorno, canciones… | **gana el respaldo**; desde el esquema 14, el sueño que corregiste a mano no lo pisa un respaldo que no lo tenía corregido (`Fusion.salud`; H-NAV-5) |
| Experimentos (desde el esquema 13) | gana el teléfono, pero se conserva la fecha de fin y las respuestas que solo trae el respaldo (`Fusion.experimento`) |
| MacroFactor | solo si en el teléfono **no hay nada** de MacroFactor |

**Por qué importa.** Si restauras sobre un teléfono que tiene una versión **incompleta** de un día (solo el sueño de esa mañana), el diario completo del respaldo para ese día **no se recupera**. En cambio, para la salud, un respaldo viejo puede **pisar** datos más nuevos del teléfono. Ninguna de las dos cosas se avisa. **[Diseño propio]** (inconsistente).

**La papelera** (desde el esquema 14). Antes de borrar un registro que hiciste tú, `Repo` copia el documento a la colección `papelera`; lo que lleva más de 7 días se vacía en la tarea diaria. La papelera **no viaja** en los respaldos. Lo privado no aparece en la pantalla «Papelera»: se recupera en la de la pantalla Privado (`Papelera.esPrivado`). **[Diseño propio]**.

---

## 10.7 Servicios externos

La app no incluye analítica, publicidad ni reporte de errores (no hay dependencias de ese tipo en `app/build.gradle.kts`). Habla con estos servicios:

| Servicio | Qué se envía |
|---|---|
| **Google Drive** | Los archivos de respaldo (cifrados o no), solo si lo activas |
| **Open-Meteo** (clima y aire) | La **mediana de las ubicaciones del día, redondeada a 2 decimales** (`Entorno.r2`: ≈ 1.1 km en latitud) y las fechas |
| **Spotify** | Autorización con redirección a `127.0.0.1` y consultas con tu permiso |
| **TMDB, RAWG, TVmaze, Google Books, Open Library, Crossref** | El **título o DOI** que buscas |
| **Mosaicos de OpenStreetMap** | Qué zona del mapa estás viendo |
| **Health Connect, uso del teléfono, ubicación** | Nada: se leen **dentro** del teléfono |

El documento de contexto dice que «nada pasa por servidores ajenos salvo Drive del usuario, Open-Meteo (clima) y Spotify API»; hay más, aunque los demás solo reciben títulos de obras o la zona del mapa (H-8-17). Desde 10e, el texto de privacidad de la app los nombra todos con lo que recibe cada uno (D.103), y dice que Drive recibe los respaldos completos (cifrados si pones contraseña) y que los demás no reciben tu diario, tu salud ni tu ruta. **Desde 10f** (D.116), Ajustes › Privacidad termina con «Qué sale del teléfono», una sección plegada que lista cada servicio y qué recibe; sale de la misma lista que el texto de privacidad (`Red.SERVICIOS`), así que los dos dicen lo mismo, aunque nada obliga a anotar ahí un servicio nuevo. El autor está de acuerdo con lo que se envía (H-8-17, resuelto). **[Validado]** como enfoque general: el procesamiento es local y solo salen las consultas necesarias. Las políticas de privacidad de cada servicio no se verificaron **[sin verificar]**.

---

## 10.8 En resumen

1. Los datos viven solo en el teléfono, sin copia automática de Android **[Validado]**; salen solo por los respaldos que tú activas y por consultas puntuales (clima con la ubicación redondeada, títulos de obras, mapas).
2. El **respaldo cifrado** usa AES-256-GCM con sal e IV nuevos en cada archivo **[Validado]**. Desde 10e, PBKDF2 usa las 600 000 iteraciones que recomienda OWASP (antes, 210 000; H-8-9), y la app avisa que las copias viejas siguen con la contraseña anterior (H-8-15); lo que más importa es una contraseña larga.
3. **Sin contraseña, todo sale en claro**, incluido el texto de las cartas selladas (H-8-5). Desde el módulo 10a, si hay datos privados, el respaldo no sale sin contraseña; solo la exportación para R, a mano, va en claro y lo advierte.
4. **El seguimiento privado** se ve, desde el módulo 10a, **solo en la pantalla Privado** y va completo a R. Desde la entrega 10c, las rachas cuentan solo los días respondidos «no» (H-8-8).
5. **Verificar** un respaldo es una buena práctica **[Validado]**; desde el esquema 13 dice solo lo que comprueba y no restaura un respaldo de un esquema más nuevo (H-8-12).
6. **Restaurar** combina con reglas distintas según la colección (H-8-13); en Drive, un respaldo mucho más pobre ya no reemplaza al bueno (H-8-14).
7. Los reportes automáticos se guardan en carpetas públicas, en modo discreto y sin la frase del mejor momento (H-8-4).
8. La entrega 10e resolvió H-8-9, H-8-11 y H-8-15, y 10e y 10f, H-8-17 (la lista de servicios, ahora también en los ajustes). En 10f, H-8-6 (logros que premian la valencia) se resolvió por decisión del autor: se quedan, con el riesgo documentado (capítulo 9). Ninguno de H-8 sigue pendiente; H-8-4, H-8-5, H-8-13 y H-8-14 están resueltos solo en parte.
