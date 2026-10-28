# Pendientes para el módulo 14 (lista del módulo 13)

_El módulo 13 (corrección mecánica, 28 oct 2026) no cambia contenido científico. Lo que parecía un error de fondo quedó aquí, sin corregir, para que el módulo 14 lo revise contra el código vigente (v90, commit `8818d5a`, `SCHEMA_V = 14`). Los números de línea son los de los archivos de capítulo al terminar el módulo 13._

## 1. Cabeceras y textos que siguen describiendo la v81

- **Cabeceras de versión.** Los capítulos 2, 3, 4, 6, 7, 8, 9 y 10 y los apéndices A y B dicen en la línea 3 que describen la v81 (commit `fa527df`); el capítulo 2 dice además `SCHEMA_V = 12`. El capítulo 1 (línea 5) y el resumen dicen v90 y esquema 14, y el cuerpo de esos capítulos ya incluye las entregas 10a a 10d. Conviene una sola fórmula: «escrito sobre la v81 y puesto al día hasta la v90».
- **Resúmenes de cierre que dicen «no se cambió código»**: capítulo 2 (línea 326, «9 hallazgos… que no cambian el código en esta fase»), capítulo 3 (línea 604) y capítulo 4 (línea 523). Son ciertos para su módulo, pero hoy confunden: varios de esos hallazgos ya se resolvieron en el módulo 10.
- **Numeración de hallazgos.** Los hallazgos llevan el número del módulo, no del capítulo (H-2-n en el capítulo 3, H-6-n en el 7, H-7-n en el 8, H-8-n y H-9-n en el 9 y el 10). Falta una línea que lo explique, quizá en la sección 1.7 o al inicio de `hallazgos.md`.

## 2. Algo descrito como pendiente que en otra parte está resuelto

| Dónde | Qué dice | Qué dice el resto |
|---|---|---|
| Cap. 3, punto 5 del resumen (línea 601) y fila L-3.3 de la tabla de amenazas (línea 559) | No se guarda cuántos avisos se mostraron; no hay denominador (H-2-3) | Desde el esquema 13 existe `tasa_respuesta` (líneas 170-185 y 505) |
| Cap. 3, fila L-3.7 (línea 563) | No usar `retroactivo` como control hasta corregirlo (H-2-2) | Corregido en 10b / esquema 13 (líneas 298 y 600) |
| Cap. 3, sección 3.15.2 | El diario de ayer queda pendiente «hasta las 14:00» | Contradice D.37 |
| Cap. 4, punto 2 del resumen (línea 518) | Pantalla, reloj, agenda y clima usan el día de calendario | Desde el esquema 13, pantalla y pasos usan el día lógico (4.3, línea 73; 4.4.1, línea 109) |
| Cap. 4, línea 71 y fila L-4.2 (línea 480) | La propuesta es unificar el criterio (H-3-1) | La línea 73 dice que H-3-1 quedó resuelto en parte |
| Cap. 4, líneas 223-225 (H-3-3) | Describe el filtro 850–1 090 hPa en presente, como problema | Corregido en 10d (D.85: 300 a 1 100 hPa) |
| Cap. 6, 6.8.6, tercer punto | Sigue como si H-5-3 no estuviera resuelto | Resuelto en 10c (app) y 10d (R) |
| Cap. 7, línea 200 | `Presencias.delDia` excluye los momentos privados «(`recaida`, `libido`)» | Desde 10a, privado es `impulso`, `recaida` o `libido` (líneas 46 y 477) |
| Cap. 7, 7.12 | «R usa otra lista» de parejas obvias | Resuelto en 10d (`vm_familias.csv`) |
| Cap. 8, línea 435 | La lista de hallazgos resueltos omite H-7-2 (resuelto en el esquema 13, líneas 270 y 274) | — |
| Cap. 9, línea 166 | Las campañas no se guardan como variable (H-8-7), en presente | Desde el esquema 13 sí (líneas 168 y 238) |
| Cap. 10, puntos 7 y 8 del resumen (líneas 288-289) | Los reportes se guardan con nombres y frases del diario (H-8-4); las reglas fallan en impulsos y medicamentos (H-8-1, H-8-2) | Resuelto en 10a (líneas 87, 112 y 183) |
| Cap. 10, línea 179 | «Hasta el esquema 12…» seguido de presente («el respaldo de ese día sobrescribe el anterior») | Resuelto (líneas 170 y 287) |
| Cap. 11, línea 23 | H-5-3 «resuelto en la app en 10c y en R en 10d», pero contado como «en parte» | Revisar su estado en `hallazgos.md` y el conteo de 11.1 |

## 3. Cifras que no cuadran o no se pudieron reconstruir

- **Cap. 6, líneas 537 y 541**: «con 14 días sería de ~7 %» (cuánto más estrecho queda el intervalo con 1.96 en vez de t). Con 14 días y k = 4 hay 9 grados de libertad, t = 2.262 y la diferencia es ~13 %; con k = 1, ~11 %. Ninguna combinación da 7 %.
- **Cap. 6, líneas 651, 655 y 757**: la lista de palabras vacías es «~170» en un lugar y «167» en otro; comprobar el número exacto en `Analisis.VACIAS`.
- **Cap. 5, líneas 159-161**, columna «Máximo con Watson»: 0.45/0.56 da −19.6 % (la tabla dice −19 %) y 0.39/0.34 da +14.7 % (dice +16 %). Quizá se calcularon sin redondear; revisar.
- **Cap. 5, línea 238**: «quedan 14 días con la acción y 42 sin ella» con 12 semanas; cotejar con el capítulo 8 (Figura 8.1).
- **Cap. 8, líneas 128 y 257**: «con 4 o 6 semanas no se puede llegar a una conclusión con el criterio habitual», pero con 8 semanas el *p* mínimo de la prueba por pares es 0.0625, también mayor que 0.05. Además se recomienda «ir a 10 o 12 semanas», y la app solo ofrece 4, 6, 8 o 12 (línea 62).
- **Cap. 10, línea 143**: la guía de OWASP se cita con «220 000 para HMAC-SHA512». Según la memoria de quien corrigió, la cifra de OWASP para SHA-512 es 210 000, la misma que usa la app. Verificar contra la fuente; si es así, cambia lo que se dice de la app.
- **Cap. 10, línea 98**: «por las tres vías indirectas de arriba (puntos 1 y 3)»: son dos puntos, y la lista de la línea 87 habla de «seis vías».
- **Cap. 10, líneas 46-48 frente a 62**: no coinciden en qué ofrece Privado con el interruptor apagado (en 48 el impulso solo aparece encendido; en 62, apagado ya hay medicamentos e impulsos).
- **Cap. 3, línea 549**: «unos 30 segundos (10 intentos)»; con esperas de 1.5 a 4.0 s más 0.6 s por intento sale más cerca de 35-40 s.
- **Cap. 3, líneas 410 y 414**: los rangos de la brújula política se traslapan en ±20 y ±60 («al centro» entre −20 y 20; «hacia…» de 20 a 60). Revisar en `data/Politica.kt` qué categoría toca a los bordes.
- **Apéndice A, línea 339**: dice «177 en 22 grupos»; las tablas de A.1 traen 183 identificadores fijos en 21 grupos. El número de variables fijas tampoco coincide entre capítulos (unas 150 en el capítulo 6, ~177 en el 2, unas 210 en `revision_propuestas.md`).
- **Cap. 6, Figura 6.5**: el pie dice «18 % de días sin registro y un hueco de 17 días»; comprobar con su script.

## 4. Frases ambiguas o mal encadenadas

- **Cap. 2, línea 266**: «Las tres que cambian de unidad… se dejan como estaban, porque salen de la misma fuente. Por eso `ruido_db_medio` ahora es el `ruido_db` de la app.» El «Por eso» no se sigue, y `ruido_db_medio` está entre las ocho de igual unidad.
- **Cap. 2, línea 70, y apéndice D, D.64 (línea 201)**: «la pantalla y los pasos de antes de la v13»; parece «del esquema 13».
- **Cap. 8, línea 149**: «con las 8 semanas de antes, todo el periodo con la acción quedaba en lavado» no dice que se refiere al plan viejo de 8 semanas (línea 159).
- **Cap. 9, línea 170**: «así lo que mueve no queda “obvio” entre sí».
- **Apéndice D, D.70 (línea 207)**: cita «Watson et al. (1980)» donde quizá corresponde la de 1981 (las dos existen en `referencias.md`: 1980 para el agua corporal, 1981 para la concentración de alcohol); revisar cuál sostiene lo que dice D.70.

## 5. «Ánimo» y «valencia»

Regla que siguen los capítulos: **«ánimo»** es el área general (valencia y activación juntas) y el nombre de algunas pantallas o cifras de la interfaz («Ánimo del día», «ánimo medio» en Tu año y en los reportes); **`valencia`** es la variable. El módulo 13 no cambió ningún uso. Casos donde «ánimo» designa en realidad el número de valencia, por si el módulo 14 prefiere decir «valencia (lo que la app llama ánimo)» la primera vez:

- Cap. 3, línea 315: la pregunta de predicción («¿Cómo crees que estará tu ánimo mañana?») se compara con la valencia (`error_prediccion`).
- Cap. 6, líneas 533 y 613: el modelo y la dinámica se calculan sobre `valencia`.
- Cap. 9, líneas 34-48, 79, 98, 102-107 y 210: «ánimo del día» y «ánimo medio» son la valencia del diario (D.77 lo dice así).
- Cap. 11, línea 78: `muestreo_valencia` descrito como «tu ánimo durante el día».

## 6. Otros

- **Cap. 3, líneas 256-258**: Westermann et al. (1996), Schwarz, Strack y Mai (1991) y Schwarz y Clore (1983) se citan y están en `referencias.md`, pero no en la lista «Referencias citadas en este capítulo» del propio capítulo (en el paper ensamblado no importa: usa la lista única).
- **Apéndice B**: el orden de las secciones no es el numérico (B.14a va después de B.15). No se renumeró porque los capítulos citan esos números.
- **Apéndice C, línea 83, y B, línea 151**: `vm_modelo` usa `lm` con intercepto y la app «sin intercepto»; es coherente (en R las variables van centradas), pero quizá convenga decirlo.
- **Extensión**: el paper ensamblado tiene ~262 páginas, muy por encima de las 60-100 del plan.

## Cómo quedó (módulo 14, 28 oct 2026)

Todos los puntos se revisaron contra el código de la v90. Resumen:

- **Sección 1.** Todas las cabeceras dicen ahora que el capítulo se escribió sobre la v81 y está al día con la v90 (`8818d5a`, esquema 14). Los resúmenes de cierre dicen qué hallazgos resolvió después el módulo 10, y el capítulo 1 (sección 1.9) y cada cabecera explican que el número del hallazgo es el del módulo.
- **Sección 2.** Corregido en cada capítulo; las filas de las tablas de amenazas y los resúmenes describen el estado de la v90, con la historia en pasado. H-5-3 quedó resuelto del todo en `hallazgos.md`.
- **Sección 3.**
  - «~7 %» → 13 % en el mínimo de días que permite `Analisis.modelo` (siempre 9 grados de libertad) y 10 % con 14 días y un predictor.
  - Palabras vacías: 169 escritas, 167 distintas, 146 distintas sin acentos.
  - La tabla de Watson estaba mal entera; se rehízo con una réplica exacta de `Sustancias.alcoholEn`.
  - Los «14 + 42 días» estaban bien.
  - Experimentos: con 4, 6 u 8 semanas no se llega a *p* < 0.05; solo con 12.
  - OWASP: la guía dice hoy 220 000 para SHA-512, así que el texto estaba bien.
  - Las «tres vías» de Privado son tres vías en dos puntos.
  - Qué ofrece Privado con el interruptor apagado: se rehízo la lista según `ui/pantallas/Recaidas.kt`.
  - La prueba de reacción dura unos 36 s de media (la pantalla dice «unos 30 segundos»).
  - Brújula política: ±20 ya es «hacia» y ±60 ya es «muy hacia».
  - Variables fijas: 184 en 24 grupos (128 siempre, 56 si hay datos). Los capítulos 1, 2 y 6 y el Apéndice A dicen lo mismo.
  - El pie de la Figura 6.5 decía 18 % de días faltantes; en realidad faltan 55 de 200 días (27.5 %), con un hueco de 19 días seguidos.
- **Sección 4.** Frases rehechas. «v13» es el esquema 13; el LEEME de la app lo escribe así (H-14-1). D.70 cita bien a Watson et al. (1980), por el agua corporal.
- **Sección 5.** Los capítulos ya dicen qué es «ánimo» en cada lugar: la predicción del capítulo 3 se compara con la valencia, y el «ánimo del día» del capítulo 9 es la valencia del diario.
- **Sección 6.**
  - Las tres referencias del capítulo 3 se agregaron a su lista.
  - B.14a va ahora antes de B.15, sin renumerar.
  - El intercepto de `vm_modelo` se aclara en los apéndices B y C.
- **Además:** la Figura 2.3 ponía 0 en días sin diario en `n_ansiedad` y `gimnasio`; se corrigió el script y la imagen. También hay seis hallazgos nuevos, de gravedad baja o muy baja (H-14-1 a H-14-6).
