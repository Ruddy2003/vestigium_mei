# Plan de mejoras de noviembre de 2026

Lo que pidió el autor el 16 de noviembre de 2026 después de comparar los 39 temas en capturas.

> **Dónde está el código.** Todo este trabajo, y también **los cinco temas nuevos** (Frente exterior `frente` —el del espía de la red entre mundos—, Arcontes `arcontes`, Galia `galia`, Art nouveau `nouveau` y Psicodelia `psicodelia`), **está en `main` desde el 17 nov 2026**: se mezcló la rama del plan (`ccr-ca31af2d-ua0p3j`) con la de escenas (`ccr-e50e410b-nuv2vd`, tandas hasta la S18) y de ahí pasó a `main` (ver «Paso a `main`» al final). `MUEBLES.md` en su versión 2 y `LOGROS_NUEVOS.md` también están ya en `main`.

## Cómo se trabaja

- El dibujo y el código los hacen agentes; quien dirige la sesión escribe el encargo de cada uno, mira cada imagen (composición, luz, sentido) y pide que se rehaga lo que no convenza. **Los agentes van con Opus** (decisión del autor: usar lo menos posible el modelo más caro sin perder calidad).
- Los muebles se ven en el arnés de `herramientas/arnes_muebles/` (lee su `LEEME.md`); la compilación y las capturas de la app, en GitHub Actions (`apk.yml` y `capturas.yml`; el flujo temporal `comparacion.yml` se borró al pasar el código a `main`).
- Nada de esto cambia lo que se registra ni cómo se calcula. Lo único nuevo que se guarda es opcional y va en campos nuevos: se anota en cada parte y en el Apéndice D.

## Las partes

| Parte | Qué | Estado (16 nov 2026, noche) |
|---|---|---|
| 0 | Este plan, las reglas de los muebles rehechos (`MUEBLES.md`, versión 2) y el arnés | hecho |
| 1 | Arreglos: el aviso que sonaba junto con la música al estrenar tema; las notificaciones que no sonaban; el modo claro de día y oscuro de noche | hecho y compilado en Actions; **falta probarlo en el teléfono** |
| 2 | Piloto de Idearios (Laboratorio, Helénico y Gótico) | hecho y aprobado por el autor |
| 3 | Los otros 31 Idearios, y un piloto de tres bibliotecas rehechas | **hecho.** Los 34 Idearios están rehechos, compilados y capturados en Actions (corridas 12, 13 y 14, sin fallas). Bibliotecas piloto hechas: Helénico, Laboratorio y Gótico (`ui/biblioteca/MuebleEstante<Tema>.kt`). **Las otras 36 bibliotecas no se empiezan** hasta que el autor diga, vistos los tres pilotos |
| 4 | Los muebles de Vida | **no se hace ahora**: queda escrita abajo |
| 5 | Psicodelia nueva | **hecho (17 nov 2026).** Compilado y capturado en Actions (corrida 26). Las dos caras (ciudad y tinta), cada una en claro y oscuro, con la intensidad por consumo en curso; componentes, muebles, biblioteca, Ideario, textos, sonidos, gato, widgets y el cuento de David. La versión 1 está borrada entera. Lo que se hizo y lo que cambió respecto del plan: `temas/psicodelia.md`, `temas/psicodelia_plan.md` y `temas/psicodelia_base.md`. **Falta:** el cuadro de David (lo dibuja otro agente; ver «Para quien dibuje los cuadros…») y probar en el teléfono el cambio de cara al anochecer y la intensidad con un consumo abierto |
| 6 | Fondos de pantalla con las escenas | **hecho (17 nov 2026)** y compilado en Actions (corrida 23); **falta probarlo en el teléfono** (lista abajo) |
| 7 | Lo temático nuevo: el toque, la hora y el clima, los mensajes breves, las esperas, el selector de fecha y de hora | **no se hace ahora**: queda escrita abajo |
| 8 | Los cuadros: seguir personajes (con notificación y sonido propio), «Hoy en la historia» bajo el cuento, el cuadro a pantalla completa y arriba del cuento con su ambiente, la galería y su mueble en Vida, el ronroneo del gato. Deja puestos los campos que necesita la parte 9 | **hecho (17 nov 2026)**; compilado y capturado en Actions (corridas 24, 26, 27 y 28, sin fallas). **Falta probarlo en el teléfono** (lista abajo, en «Los cuadros (parte 8)»). El plan técnico y lo que se guarda: `CUADROS_PLAN.md` (`SCHEMA_V` = 27, D.199). Los logros no se hicieron (parte 9) |
| 9 | Logros nuevos | **no se hace ahora**: queda escrita abajo y en `LOGROS_NUEVOS.md` |

## Decisiones del autor, parte por parte

### Muebles (partes 2 a 4)

Están en `MUEBLES.md`, versión 2. En corto: los 34 Idearios se rehacen; solo el mueble y la pared, sin ventanas ni paisajes; objetos y luz; noche como la de Galia y Art nouveau; tres tipos de libro; nivel de los cuadros.

Orden de los Idearios, de los más simples a los que ya tenían carácter:
1. Laboratorio, Ruinas, Navidad, Prehistoria, Mesoamérica, Mongol, Terminal, Ciudad neón, Refugio, Orden, Distopía.
2. Botánico, Horror cósmico, Tierras Antiguas, Pirata, Ruta de la Seda, Al-Ándalus, Egipto, Templario, Abismo, Espacio, Soviético, Helénico.
3. Romano, Urbe, Medieval, Gótico, Vapor, Colonos, Napoleónico, Art déco, Edo, Renacimiento, Revolución.

### El modo claro y oscuro (parte 1)

`config.modo` gana un valor, «hora»: claro de día y oscuro de noche. Los otros tres (claro, oscuro, como el teléfono) siguen igual.

### Psicodelia (parte 5)

- **Dos caras que no son el claro y el oscuro:** la ciudad de los 70, sobria, de día; la psicodelia, de noche. Cada cara tiene su claro y su oscuro. Con el modo «hora», de día sale la ciudad en claro y de noche la psicodelia en oscuro.
- **La ciudad de los 70:** urbana, cálida y gastada. Las referencias del autor: un estudio de grabación con cintas de carrete y lámpara de pantalla; el interior de un autobús al atardecer; una calle de barrio con letreros, coches y escaleras de incendio. Color de película vieja: ocres, naranja quemado, café, verde botella.
- **La psicodelia:** seria y pensada, con una paleta fija y corta. Las referencias del autor: un cartel a una tinta con grabado fino y líneas que ondulan, y uno de tres tintas planas (magenta, rojo y azul). Nada de imágenes de estereotipo (flores, símbolo de la paz, arcoíris).
- **Con un consumo de cannabis en curso** (la app ya guarda su periodo y su nivel), la psicodelia se satura mucho más. Solo se lee lo anotado; no se guarda nada nuevo.
- **Se quita la regla de «sin drogas»** del tema: el personaje del cuento consume de forma explícita de vez en cuando, y los textos del tema pueden aludir a ello. Hay que cambiar la guía (`temas/psicodelia.md`), las fuentes, el cuento y la prueba que lo vigilaba.
- **Para después (con el cuadro del tema):** la escena se vuelve muy psicodélica la noche en que el personaje consume.

### Fondos de pantalla (parte 6)

- El teléfono es un Pixel 10.
- La escena del tema como fondo, recortada en una franja vertical, en inicio y en bloqueo, y que cambie con la hora.
- El cambio automático con el tema ya existe (`fondo_telefono_auto`) y a veces no ocurre: hay que encontrar por qué.

**Por qué a veces no cambia (leído en el código, 16 nov 2026; sin probar en el teléfono):**
1. **El fondo solo se puede pintar con la app abierta en su pantalla principal.** `CapturaFondoTelefono` (en `ui/FondosTelefono.kt`) lo compone escondido y le saca una foto; espera al menos 0,6 s por fondo y los pinta en fila (el de hoy, el de hoy en el otro modo y, con la rotación, los dos de mañana). **Y no pone el fondo hasta haber pintado todos.** Si cambias de tema y sales enseguida a ver cómo quedó, no dio tiempo: ni se pintó ni se puso. Después el pulso de 15 minutos (`FondosTelefono.alDia`) no puede hacer nada, porque solo sabe poner un archivo ya pintado. Es la causa más probable de «a veces sí y a veces no»: depende de cuántos segundos te quedas en la app.
2. **Solo se dejan pintados hoy y mañana** (`necesarios`). Si un día registras desde los avisos o el widget sin abrir la app, al día siguiente no hay archivo del tema nuevo y el fondo se queda con el anterior hasta que la abras.
3. **Una foto que falla no se reintenta** hasta que cambie el tema o el modo.
4. El cambio de las 5:00 lo pone el pulso, que el teléfono puede retrasar si está en reposo; llega, pero tarde.
5. El modo nuevo «según la hora» cambia el fondo a las 21:00 y a las 5:00 también con el pulso: mismo retraso.

**Cómo se arregla en esta parte:** las escenas se dibujan con código puro (sin componer pantallas), así que el fondo nuevo se puede pintar **fuera de la pantalla, desde el pulso**, cualquier tema a cualquier hora y con la app cerrada. Con eso desaparecen 1, 2 y 3. Además: poner el fondo de hoy en cuanto esté pintado el suyo (no esperar a los demás) y, al cambiar de tema dentro de la app, pintarlo y ponerlo en el momento.

**Lo que se hizo (17 nov 2026).** No cambia nada de lo que se guarda ni de la exportación: la elección del fondo va en las preferencias de los fondos del teléfono (`getSharedPreferences("fondos")`), no en `Config`, y no sube `SCHEMA_V`.

- **El cuadro como fondo** (`ui/escenas/FondoCuadro.kt`). El cuadro (4:3) se pinta a la altura de la pantalla (en el Pixel 10, 3232 × 2424) y se toma una franja vertical del ancho de la pantalla (1080), un tercio del cuadro. Solo se pinta la franja: el lienzo se corre y lo de fuera no ocupa memoria (unos 10 MB por imagen). Se pinta la parte quieta y la viva en un instante fijo, sin gato.
  - **Bloqueo:** la franja tal cual, con una sombra suave en el 42 % de arriba (donde va el reloj) y otra más leve abajo (los accesos).
  - **Inicio:** la misma franja bajo un velo del color de fondo del tema **en oscuro** (46 % de día, 28 % de noche). Se probó un velo claro en los temas claros y los nombres blancos de los íconos no se leían sobre el cielo; con el oscuro se leen en los 16 cuadros.
  - **La franja de cada cuadro** (`EscenaVida.franjaFondo`, el centro en fracción del ancho; 0.5 por omisión), escogida mirando cada cuadro de día y de noche: Al-Ándalus 0.5 (la fuente y el brasero), Horror cósmico 0.5 (el faro y la mesa), Distopía 0.5 (la avenida y la torre), Espacio 0.55 (la escotilla y la consola), Gótico 0.4 (el portal y la grúa), Helénico 0.5 (la trirreme y la tinaja), Medieval 0.5 (ventanas y pupitres), Ciudad neón 0.3 (el puesto de fideos), Pirata 0.6 (el mástil y el cañón), Renacimiento 0.45 (la cúpula por la ventana y la mesa), Revolución 0.4 (la catedral y la tina), Romano 0.45 (el campamento y el escudo), Ruinas 0.45 (el árbol y la torre inclinada), Ruta de la Seda 0.7 (el camello y el farol), Roma 0.5 (el altar y el andamio), Vapor 0.58 (la locomotora). Las escenas que cambian de lugar según el día (la azotea de Roma, el cayo de Pirata, Chang'an) usan la misma franja.
- **Cuándo cambia** (`ui/FondoCambio.kt`, `ClaveFondo` y `tocaCambiarFondo`). El fondo puesto se anota con su tema, su modo, su cara (Psicodelia), su estilo y, con cuadro, el día lógico y el **tramo de luz** del cuadro (`Momento.luz.franja`: amanecer de 5 a 8, mañana de 8 a 12, mediodía de 12 a 16, tarde de 16 a 19, atardecer de 19 a 21 y noche de 21 a 5; Espacio, encendido y tenue). Si el que tocaría ahora es el mismo, no se pone (ponerlo no es gratis: el teléfono recalcula sus colores y puede parpadear). Otro tema, modo, cara o estilo se pone en el momento. Solo otra luz u otro día, como pronto diez minutos después del anterior: como el pulso es de 15, como mucho una vez por pulso. Un día normal con cuadro son **seis cambios** (prueba `conElPulsoDe15MinutosCadaTramoNuevoSePone`). Lo dice la pantalla bajo el interruptor.
- **El arreglo del automático** (`FondosTelefono.revisar`, que llaman el pulso por `RotacionTema.propagar` y la app):
  - con cuadro, lo pinta él mismo fuera de la pantalla y lo pone, también con la app cerrada (causas 1, 2 y 3);
  - con papel (temas sin cuadro, o si se eligió el papel), pone el archivo ya pintado; `CapturaFondoTelefono` pinta el de hoy primero y lo pone **en cuanto está**, sin esperar a los demás; una foto que falla vuelve al final de la fila (hasta tres vueltas) y se reintenta cada vez que se vuelve a la app;
  - al cambiar de tema, modo, estilo o cara dentro de la app, y al volver a ella, revisa en el momento (con tres reintentos si falla);
  - de uno en uno (`@Synchronized`): si el pulso y la app llegan a la vez, el segundo ve que ya está puesto;
  - las causas 4 y 5 (el pulso que el teléfono retrasa en reposo) siguen: llega, pero tarde. Al abrir la app se pone al día.
  - El papel de los temas con cuadro ya no se deja pintado en archivo (salvo con el fondo vivo puesto, que dibuja encima del papel).
- **La pantalla de fondos:** «el cuadro» o «el papel» (si el tema tiene cuadro), la vista previa de bloqueo e inicio a pantalla completa (con el cuadro, pintado a la hora de ahora) y ponerlo en inicio, bloqueo o los dos. La elección se guarda al poner el fondo y es la que sigue el automático. Con el cuadro no salen claro/oscuro ni las caras: el cuadro es el mismo en los dos modos.
- **El fondo vivo** no cambió: sigue con el papel, y el fijo no lo pisa (`vivoActivo`).
- **Pruebas:** `FondoTelefonoPrueba` (11, JUnit puro): la clave, cuándo cambia, el recorte de la franja (nunca se sale, guarda la proporción, cubre la pantalla) y la franja de cada cuadro. Pasan en un arnés de escritorio; en Actions, falta correrlas.

**Qué falta probar en el Pixel 10:**
1. Que la franja salga a la medida exacta en bloqueo y en inicio (sin corrimiento hacia un lado, que es lo que arreglaba `setBitmapWithCrops`), y que el reloj se lea.
2. Que los nombres de los íconos se lean sobre el fondo de inicio con el cuadro de día (con velo oscuro) en un tema claro.
3. Que con la app cerrada el fondo cambie solo: al pasar de un tramo de luz a otro (por ejemplo a las 8:00 o a las 21:00) y a las 5:00 con la rotación de temas. Mirar a qué hora llegó (el pulso puede retrasarse).
4. Que al cambiar de tema dentro de la app y salir enseguida, el fondo ya haya cambiado (con cuadro, en uno o dos segundos).
5. Cuánto tarda en pintarse el cuadro en el teléfono (en el escritorio, unas 0,5 s por cuadro) y si se nota un parpadeo al ponerlo.
6. Con un tema sin cuadro (Terminal, Navidad): que el papel se siga poniendo como antes.
7. Que el fondo vivo siga funcionando y que el automático no lo quite.

### Lo temático nuevo (parte 7)

La respuesta al tocar; que el fondo y los muebles reaccionen a la hora, el clima o la estación; los mensajes breves; las esperas; el interior del selector de fecha y de hora. Sonido de ambiente: poco y opcional (por definir).

### Los cuadros (parte 8)

- **Seguir a un personaje** (hasta 6): cuando su historia tiene un día importante, sale su banderita avisando.
- **«Hoy en la historia»:** un botón debajo del del cuento.
- **Galería:** «me gusta» a un cuadro para guardarlo tal cual (el tema, la vida, el día y la hora); se ven en un mueble nuevo de la pestaña Vida.
- **El gato del cuadro** suena al acariciarlo.

**Cómo quedó hecho (17 nov 2026):**
- **Seguir** (`ui/seguir/`, `data/Historias.kt`, `vidas/DiasEspeciales.kt`, `avisos/HistoriaPendiente.kt`): el botón va debajo del retrato, en el cuento; las banderitas, en Hoy; la lista con los días pasados, en la ruta `seguidos`. La notificación sale una vez por día especial, entre la hora de despertar y las 21:00, por el canal «Días especiales de quien sigues», con un sonido por tema (`n_<tema>_historia.ogg`, de `herramientas/sonidos_historia.py`).
- **El cuento** (`ui/pantallas/OtrasVidas.kt`, `ui/escenas/CuadroCompleto.kt`, `AmbienteCuadro.kt`, `CuentoReglas.kt`): el cuadro va arriba, con su gato, el «me gusta», el botón de ampliar y un altavoz. La pantalla completa es una ventana encima de donde estés; se sale tocando fuera del gato, con la ✕ o con «atrás». «Leído» se marca al llegar al final, sin tiempo mínimo.
- **El ambiente** (31 archivos `a_<tema>_ambiente[_noche].ogg`, de `herramientas/ambientes.py`): suena solo en el cuento, en bucle y suave. **No depende del interruptor «Sonidos»**: tiene el suyo («Ambiente del cuadro», en Sistema, y el altavoz del cuadro) y calla con el teléfono en silencio o en vibración, con una llamada, al apagar la pantalla y al salir. El ronroneo sí depende de «Sonidos».
- **La galería** (`ui/galeria/`, `ui/vida/MuebleGaleria.kt`, `data/CarpetaCuadros.kt`): el «me gusta» pinta el cuadro a 1200 × 900 con su gato sentado y lo guarda como JPEG en `files/cuadros/`, con sus datos en `Config.cuadros_guardados`. El mueble de Vida es una pared de cuadros con los cuatro últimos; es **uno solo para todos los temas** (toma sus colores). Si más adelante se quiere uno por tema, cada tema reescribe `DisenoVida.muebleGaleria` dejando los marcos en los sitios de `MuebleGaleriaComun.huecos`.
- **Las caricias no se guardan** (D.190 sigue igual).
- **De los días de uso** (`Config.uso_dias`) solo se anotan ya los dos de esta parte: abrir «Hoy en la historia» y abrir un cuento desde «en la época de…». Los demás nombres existen en `UsoDias` pero nadie los anota todavía (respaldo verificado, ensayo, exportación, widget, fondo con escena y tema del día): eso es de la parte 9.

**Qué probar en el teléfono:**
- Seguir a un personaje y dejarlo; llenar los seis y ver el aviso del tope. Con «probar historia» (Sistema, avisos) se oye el sonido nuevo sin esperar a un día especial.
- Un día especial de alguien que sigues: que salga su banderita en Hoy y llegue la notificación una sola vez.
- El cuento: que el cuadro salga arriba, que el ambiente empiece suave, que el bucle empalme sin chasquido y que se calle al salir, al apagar la pantalla, con una llamada, en silencio y al poner música. Los ambientes están sintetizados y nadie los ha oído: los más dudosos son la fuente de Al-Ándalus, la pluma de Medieval y los murmullos de gente.
- La pantalla completa, en vertical y en horizontal: sin barras, y que un toque fuera del gato salga. Al tocar el cuadro fuera del gato, el gato da un paso hacia ahí antes de abrirse: decir si molesta.
- El ronroneo, con «Sonidos» encendido.
- El «me gusta»: que se note mientras pinta, que el cuadro guardado sea igual al de la pantalla, quitarlo y deshacer, y que el mueble de Vida muestre los últimos.
- Un respaldo completo y su restauración, con cuadros guardados.

## Respuestas del autor (16 nov 2026, segunda ronda)

1. **Las ventanas que ya existen se quedan por ahora** (los Idearios de Arcontes, Galia, Art nouveau y Psicodelia; las bibliotecas de Urbe, Pirata, Revolución, Ruinas, Navidad, Espacio y Abismo). La regla de «solo mueble y pared» vale para lo que se rehace.
2. **Psicodelia se queda en San Francisco a fines de los 60**, con su personaje y su cuento. De las referencias de los 70 se toma solo el estilo.
3. **Seguir a un personaje avisa también con una notificación del teléfono,** con un sonido nuevo, propio de la historia.
4. **La galería guarda el cuadro como imagen,** además de sus datos (tema, vida, día y hora), para que no cambie cuando se mejore el dibujo.
5. **Sonido de ambiente:**
   - nada continuo en la app;
   - el cuadro se puede ver a pantalla completa;
   - el cuadro sale arriba en la pantalla del cuento del día, y al abrir el cuento suena el ambiente del cuadro completo: suave, pero no necesariamente muy tenue;
   - el gato del cuadro ronronea al acariciarlo.
6. **Nada pasa a `main` hasta que todo esté arreglado.**

Además, el autor notó que **algunas notificaciones no suenan**: hay que revisarlas una por una y arreglarlas (parte 8). Lo que se ve en el código: solo cuatro canales llevan el sonido del tema (ánimo, diario, cuestionarios y fin de bloque de enfoque); el de reportes se crea con importancia baja, que en Android es sin sonido; los de recordatorios y lugares no llevan sonido del tema.

## Respuestas del autor (16 nov 2026, tercera ronda)

- **Psicodelia:** la lámina va bien. Sus decisiones (más dibujo de cartel, la intensidad máxima más fuerte, la psicodelia clara de día con un consumo en curso, cualquier sustancia con nivel, media con nivel 3 y máxima con nivel 8, reacciona aunque esté oculta, la frecuencia del consumo en el cuento) están al final de `temas/psicodelia.md`.
- **Muebles de Vida:** no se redibujan; reciben noche de verdad y luz con dirección en las mismas tandas que las bibliotecas.
- **Logros nuevos (parte 9):** un juego de logros sobre las funciones nuevas y sobre lo que hoy no tiene ninguno.

## Respuestas del autor (16 nov 2026, cuarta ronda)

- **El piloto de Idearios (Laboratorio, Helénico y Gótico) queda aprobado** como vara: se sigue con los demás. El autor añade que, visto el nivel, tal vez haya que redibujar también los demás muebles (bibliotecas y Vida), no solo completarlos: se decide con el piloto de bibliotecas.
- **Logros nuevos (parte 9):**
  - categoría nueva para las historias y los cuadros, con su trofeo en los 39 temas;
  - «leer» un cuento es llegar hasta el final; los días especiales ya pasados se pueden leer después, los futuros nunca; hay un logro por leerlos todos y otro que premia leer un día especial el mismo día en que ocurre;
  - se agregan logros secretos nuevos (el autor no quiere saber cuáles);
  - se pueden agregar logros que premien registrar; **no se borra ninguno de los que ya existen**;
  - la opción de crear logros propios sobre sustancias se queda, pero ningún logro nuevo de la app premia un consumo;
  - las caricias al gato siguen sin guardarse (el autor no contestó esa pregunta; vale la decisión anterior, D.190).

## Respuestas del autor (16 nov 2026, quinta ronda): qué se cierra esta semana

1. **Se termina la parte 3:** los Idearios que faltan, los pilotos de biblioteca de Laboratorio y Gótico y la compilación en Actions de los Idearios nuevos. **No se empiezan las otras bibliotecas** hasta que el autor vea los tres pilotos.
2. **Después, solo las partes 5, 6 y 8, en ese orden.** Al terminar la 5 se avisa al autor y se deja escrito lo que necesita el agente que va a dibujar las escenas de Psicodelia y de Frente exterior (el tema del espía).
3. **La parte 8 deja puestos los campos de la parte 9** (`cuentos_leidos`, `uso_dias` y lo de los cuadros guardados), subiendo `SCHEMA_V` una sola vez y anotándolo en el Apéndice D. Los logros no se hacen.
4. **Las partes 4, 7 y 9 no se hacen:** quedan escritas aquí.
5. **A `main` pasan ya solo los documentos** que hacen falta para encontrar los temas nuevos (las guías de `docs/diseno/temas/`, este plan y un puntero en `CLAUDE.md`). Sin código.
6. **El código no pasa a `main` sin avisar al autor.** Hay otras ramas trabajando sobre `main` (las de escenas): antes de mezclar se trae `main` a la rama, se revisan los choques y se le cuenta al autor qué se encontró.
7. **Los agentes, con Opus.**

## Para quien dibuje los cuadros de Psicodelia y de Frente exterior

Los dos temas nuevos que todavía no tienen cuadro en «Otras vidas» son **Psicodelia** (`psicodelia`; David, dibujante de carteles, San Francisco, 1965–1969) y **Frente exterior** (`frente`, el del espía; Tadeo, ladrón de piezas y agente de la red, en el puerto de Halde). Esto es lo que necesita quien los dibuje.

**Dónde está el código.** En `main`, desde el 17 nov 2026 (antes estaba en la rama `ccr-ca31af2d-ua0p3j`).

**Qué leer, en este orden:**
1. `CLAUDE.md` (el apartado de las escenas) y **entera** `docs/diseno/ESCENAS.md`: el estilo, las diez reglas, la vara (el cuadro de Helénico) y la receta para dibujar en el arnés de escenas (`docs/historia/informes/P4a.md`).
2. `docs/plan_historia_y_escenas.md`: cómo se reparten las tandas y la decisión 18 (el cuento y el cuadro cuentan lo mismo).
3. La guía de cada tema: `docs/diseno/temas/psicodelia.md` (empezando por su última sección, «Decisiones del autor») con `psicodelia_base.md` (los nombres de colores y trazos que ya existen: `TrazosCiudad`, `TrazosTinta`, `EstadoTema`), y `docs/diseno/temas/frente.md`. Sus fichas, en `PLAN_TEMAS.md`.
4. La historia de cada personaje, que es lo que el cuadro tiene que contar:
   - David: `vidas/anios/AnioG9.kt` (busca el comentario «Lo que el cuadro tiene que mostrar»: ahí están todas las claves de escena) y `vidas/piezas/PiezasDavid.kt`.
   - Tadeo: `vidas/piezas/PiezasTadeo.kt` y su año, también en `vidas/anios/AnioG9.kt` (busca `agente_red` y su comentario «Lo que el cuadro tiene que mostrar»).
5. Un cuadro reciente como modelo de código: `ui/escenas/EscenaNeon.kt` o `EscenaVapor*.kt`, y `ui/escenas/EscenaVida.kt` (`Momento`: la hora, `m.hacer`, `m.con("clave")`).

**Lo propio de Psicodelia:**
- **El cuadro no lee la cara ni la intensidad del tema.** La cara y la intensidad dependen de la hora y de lo que anota la persona; el cuadro cuenta la vida de David. Lo que cambia el cuadro son las claves de su día.
- **Dos claves nuevas, ya escritas en el calendario** (y vigiladas por pruebas): `humo`, los viernes por la noche (marihuana: de 20 a 4 h, una bruma baja y tibia, las luces un poco más vivas; cambio leve) y `noche_tinta`, las noches de LSD y de hongos (de 20 a 4 h **el cuadro entero pasa a la manera del cartel**: tres tintas planas, bandas que fluyen desde las luces de la pared, el registro corrido). El comentario de `AnioG9.kt` describe qué cambia en cada una y en qué se distinguen el LSD y los hongos.
- **Anotado por el autor para después:** que cada sustancia cambie la escena de una forma distinta, intentando reproducir visualmente su efecto. Los textos de esas noches en `PiezasDavid.kt` ya lo describen con detalle; el cuadro puede apoyarse en ellos.
- De día el cuadro es la ciudad sobria (la calle de casas victorianas grises, la niebla, los cables del trolebús); sin tópicos (flores, símbolo de la paz, arcoíris, furgonetas).
- El gato: en `ui/escenas/Pelajes.kt` la rama de Psicodelia está en `null` porque no había cuadro; el pelaje nuevo es el atigrado naranja de la banda (`herramientas/gato/`).

**Archivos que NO debe tocar** (otros trabajos dependen de ellos tal como están):
- `ui/escenas/paisaje/**`: no se cambia cómo se ve ninguna pieza que ya usa un cuadro aprobado. Agregar piezas nuevas sí se puede.
- Los cuadros de los demás temas (`Escena<Tema>*.kt`), `GatoCuadro.kt`, `GatoDibujo.kt`, `GatoEsqueleto.kt`.
- `vidas/**` (las historias y sus pruebas): si al cuadro le falta una clave, se pide; no se cambia la historia para acomodar el dibujo.
- `data/Modelo.kt` y `SCHEMA_V` (un cuadro no guarda nada), `data/OtrasVidas.kt` salvo que haga falta una clave de escena.
- Todo lo de los muebles y del tema: `ui/ideario/**`, `ui/biblioteca/**`, `ui/vida/**`, `ui/comun/**`, `ui/CaraTema.kt`, `ui/PsicodeliaTrazos.kt`, `ui/PsicodeliaPiezas.kt`, `ui/Tema.kt`, `herramientas/arnes_muebles/**`.
- La firma (`app/vestigium.jks`, `signingConfigs`) y los flujos de `.github/`.

**Lo que sí toca:** archivos nuevos `ui/escenas/EscenaPsicodelia*.kt`, `EscenaFrente*.kt`, `PelajePsicodelia.kt`, `PelajeFrente.kt`, `BanderitaPsicodelia.kt`, `BanderitaFrente.kt`, sus imágenes de referencia en `docs/diseno/escenas/`, y **una línea por tema** en los tres repartos: `EscenaVida.kt` (los dos `when`), `Banderitas.kt` y `Pelajes.kt`.

**Dos cosas que un cuadro nuevo trae consigo desde las partes 6 y 8** (las dos se encuentran solas, sin tocar ningún reparto):
- **La franja del fondo del teléfono:** `override val franjaFondo` en la escena (de 0 a 1: dónde cae, a lo ancho del cuadro, la franja vertical que se usa de fondo de pantalla). Se elige donde esté lo que cuenta la escena; modelos en `EscenaHeleno.kt` y `EscenaRomano.kt`.
- **El ambiente del cuento:** `res/raw/a_<tema>_ambiente.ogg` y, si la noche suena distinto, `a_<tema>_ambiente_noche.ogg`. Se generan con `herramientas/ambientes.py` (agregando el tema al guion) y la app los busca por nombre. Sin archivo, ese cuento simplemente no suena. Al agregarlo, pon también el tema en la lista de `CuentoReglasPrueba.losAmbientesQueHayEnElRepositorio`.

**Ojo con `main`:** otras ramas de escenas trabajan sobre `main` y esos tres repartos y `paisaje/` son justo donde pueden chocar. Conviene que quien dibuje estos dos cuadros traiga `main` a su rama antes de empezar (`git merge origin/main`) y diga qué chocó; y que su trabajo vuelva a esta rama, no a `main` directamente.

## Lo que queda pendiente, para quien lo retome

Todo lo de esta sección está sin hacer. Cada punto dice qué decidió el autor, dónde está el código y cómo se trabaja, para poder retomarlo sin la conversación original.

### Cómo se rehace un mueble (vale para bibliotecas y Vida)

- **Las reglas** están en `MUEBLES.md`, «Versión 2». **La vara** son los 34 Idearios rehechos (`ui/ideario/Ideas<Tema>.kt`; el piloto aprobado: Laboratorio, Helénico y Gótico) y las tres bibliotecas piloto (`ui/biblioteca/MuebleEstante<Tema>.kt`).
- **Un agente por mueble**, en su propio árbol de trabajo, con un encargo en dos partes: uno común (qué decidió el autor, qué se entrega, lo que no se toca, cómo se dibuja, el ciclo de dibujar, mirar y corregir al menos cuatro veces, el informe) y una dirección de arte del tema escrita por quien dirige (cómo está hoy, el mundo del tema y su «otra vida» en `data/OtrasVidas.kt`, la idea, la pared, el objeto principal y los demás, lo que va al pie, las tres variantes, la noche, lo que no debe repetir del otro mueble del tema, los anacronismos que evitar).
- **Quien dirige mira cada imagen** en claro y en oscuro y devuelve lo que no llegue. Lo que más se devolvió: la noche pareja en el cuerpo del mueble (cada compartimento o volumen tiene que apagarse con la distancia a la luz dibujada), lo guardado demasiado chico dentro de su celda, todos los recipientes del mismo color, la misma variante repetida, el rótulo que tapa el recipiente o corta el título, telas tiesas, objetos «de caja».
- **No más de cuatro agentes dibujando a la vez** en una máquina de 16 GB: el arnés compila unas 100 000 líneas y se queda sin memoria. Se compila con `-Pkotlin.compiler.execution.strategy=in-process`.
- **La clase de cada recipiente sale de la semilla (el tema), nunca de su sitio**; el sitio (`fila`, `col`, `repisa`, `centro`) es solo para la luz.
- **Seguridad:** ningún bucle cuyo paso pueda valer cero. Cada mueble se dibuja también con 0, 1 y 5 temas (`ARNES_TEMAS=0,1,5`) y cortado con su placa (`ARNES_MAX=6`), y al final se pide una corrida de capturas en Actions: ahí salió un cuelgue que el arnés no veía.

### Las bibliotecas (36 por rehacer, si el autor lo aprueba)

- **Decisión pendiente del autor:** ver los tres pilotos (Helénico, Laboratorio, Gótico) y decir si se rehacen las demás. Hasta entonces no se toca ninguna.
- **Qué es rehacer una:** solo `estante()` y `volumen()` del objeto `Estante<Tema>` (en `ui/biblioteca/Biblioteca<Época>.kt`), que pasan a un archivo nuevo `ui/biblioteca/MuebleEstante<Tema>.kt` al que el objeto delega; más `override val auraEnReposo = false`. La tapa, la libreta, el sobre, el buzón y lo demás no se tocan.
- **Qué pide el autor:** solo el mueble y la pared, sin ventanas; nivel de los Idearios rehechos; tres tipos de entrada que sean objetos distintos del tema; objetos arriba y en los huecos libres de las repisas; noche de verdad.
- **Lo que ya está preparado en el contrato** (`DisenoBiblioteca.kt`, `EstanteBiblioteca.kt`): `Volumen.repisa`, `repisas` y `centro` (para apagar cada volumen con la distancia a la luz), `DisposicionEstante.ocupado` (hasta dónde llegan los volúmenes de cada repisa: de ahí al costado, el hueco está libre para objetos) y `auraEnReposo`.
- **Lo que cada volumen tiene que seguir diciendo:** el día legible, el mes, el color del ánimo bien visible, lo lleno según las palabras, y la luna en el estante de los Sueños (`v.noche`, que no es el modo oscuro). Son cuatro estados: claro y oscuro, diario y Sueños (`ARNES_SUENOS=1` en el arnés).
- **Las ventanas que hoy tienen** las bibliotecas de Urbe, Pirata, Revolución, Ruinas, Navidad, Espacio y Abismo desaparecen al rehacerlas (la regla vale para lo que se rehace).
- **Orden sugerido:** el de los Idearios (arriba), empezando por las más planas.

### Parte 4 · Los muebles de Vida

- **Qué son:** los muebles de la pestaña Vida (el de las notas y los nichos), un archivo por tema en `ui/vida/Vida<Tema>.kt`, contrato en `ui/vida/DisenoVida.kt`. El arnés todavía no los dibuja: hay que añadirle un modo `vida` (como se hizo con `ideario` y `biblioteca`, en `herramientas/arnes_muebles/src/main/kotlin/arnes/Dibujar.kt`).
- **Lo decidido:** primero se dijo «no se redibujan; reciben noche de verdad y luz con dirección». Después de ver los Idearios rehechos, el autor añadió que tal vez haya que redibujarlos también. **Se decide junto con las bibliotecas**, cuando vea los pilotos: si las bibliotecas se rehacen, lo coherente es rehacer Vida con las mismas reglas.
- **Además, un mueble nuevo en Vida** para la galería de cuadros guardados: ese sí se hace, en la parte 8.

### Parte 7 · Lo temático nuevo

Cinco cosas que hoy ningún tema tiene y que el autor pidió para los 39 (son solo de aspecto: no guardan nada):
1. **Respuesta al tocar:** que botones, fichas y muebles respondan al toque con algo del tema (un brillo, un hundimiento, una mancha de tinta), no solo con la onda genérica. Las piezas comunes de cada tema están en `ui/comun/` (`DisenoComun.kt` y `Comun<Época>.kt`) y los controles en `ui/ControlesTema.kt`.
2. **Reaccionar a la hora, el clima y la estación:** que el fondo y los muebles cambien un poco con la hora del día (ya existe `ui/ModoHora.kt` para saber si es de noche), con el clima que la app ya registra y con la estación.
3. **Los mensajes breves** (los avisos que salen un momento abajo) con la forma del tema.
4. **Las esperas** (cuando algo carga) con un dibujo del tema en lugar del círculo genérico.
5. **El interior del selector de fecha y de hora** (`ui/Selectores.kt`): hoy solo el marco es del tema.
- **Sonido:** nada continuo en la app. El único ambiente sonoro es el del cuadro al abrir el cuento (parte 8).
- **Cómo hacerlo:** primero un piloto en tres temas muy distintos (por ejemplo Helénico, Laboratorio y Gótico) para que el autor apruebe cada una de las cinco cosas; después, por tandas. Las capturas de Paparazzi (`app/src/test/.../Capturas.kt`, con `CAPTURAS=…` en `.github/comparacion.env`) sirven para ver los 39 a la vez.

### Parte 9 · Logros nuevos

- **Todo el detalle está en `LOGROS_NUEVOS.md`:** los 135 logros de hoy (se recalculan, no se guardan), qué funciones no tienen ninguno y los 40 propuestos.
- **Decisiones del autor:** categoría nueva para las historias y los cuadros, con su trofeo en los 39 temas (`ui/logros/`, `ui/TrofeosTema.kt`); «leer» un cuento es llegar hasta el final; los días especiales pasados se pueden leer después y los futuros nunca; un logro por leerlos todos y otro por leer un día especial el mismo día; secretos nuevos sin decírselos al autor; se pueden agregar logros que premien registrar; **no se borra ninguno**; ningún logro nuevo premia un consumo; las caricias al gato no se guardan.
- **Los campos que necesitan** (`Config.cuentos_leidos`, `Config.uso_dias` y los cuadros guardados) los deja puestos la parte 8, con `SCHEMA_V` 27. Con eso, los 40 propuestos son programables sin tocar otra vez lo que se guarda.

### Preguntas abiertas al autor

1. **Bibliotecas y Vida:** ¿se rehacen las otras 36 bibliotecas y los muebles de Vida? (Esperan a que vea los tres pilotos.)
2. **Al-Ándalus (Ideario):** el interior del compartimento usa un alicatado nazarí, posterior a la época del tema: ¿se cambia? ¿Se quita la puerta de lacería?
3. **Templario (Ideario):** el escudo es de lágrima; hacia 1250 ya era triangular: ¿se cambia?
4. **Urbe (Ideario):** es el quiosco de periódicos arrimado a un muro romano, sin cielo ni calle, pero es un mueble de exterior: ¿vale, o se cambia por un interior?
5. **Prehistoria (Ideario):** es el más flojo de los 34 (la zona del nombre y las viseras de roca): ¿se retoma?
6. **Tierras Antiguas (Ideario):** con la letra ancha del tema la fecha del rótulo aún se corta en algunos títulos.

### Limpieza antes de pasar el código a `main`

- **Quitar de `CLAUDE.md` el puntero** «Trabajo en curso fuera de `main`», que solo vale mientras el código esté en la rama. **Hecho** (17 nov 2026).
- **Borrar el flujo temporal de capturas:** `.github/workflows/comparacion.yml`, `.github/comparacion.env` y la rama `comparacion-ci` (ya existe `capturas.yml` para lo de siempre). **Hecho el 17 nov 2026** con los archivos, y también con `temas-nuevos.yml` y `.github/temas-nuevos.env` (decía «se borra al pasar los temas a `main`»); las ramas remotas `comparacion-ci` y `temas-nuevos-ci` siguen ahí: las borra el autor si quiere.
- **Revisar `gradle.properties`:** se subió la memoria del compilador (`kotlin.daemon.jvmargs=-Xmx9g`, `org.gradle.jvmargs=-Xmx3g`) porque con los Idearios nuevos 6 GB ya no alcanzaban. Hay que comprobar que `apk.yml` compila con eso. **Comprobado el 17 nov 2026**: con las escenas y los Idearios juntos, `apk.yml` compila.
- **Actualizar `MUEBLES.md`:** su sección «Cómo se revisa» ya apunta al arnés; falta decir que la vara son los Idearios rehechos, no Galia y Art nouveau. **Hecho el 17 nov 2026.**
- **El paper:** nada de esto cambia la metodología, salvo los campos nuevos (Apéndice D: fila D.198 del modo «hora» y la de la parte 8).
- **Probar en el teléfono** lo de la parte 1 (el aviso doble al estrenar tema, las notificaciones que no sonaban, el modo «según la hora»).

### Paso a `main`

- Cuando estén las partes 3, 5, 6 y 8 se avisa al autor y **se espera su visto bueno**.
- Antes de mezclar: traer `main` a la rama (`git fetch origin main && git merge origin/main`), revisar los choques y contárselos al autor. Los choques más probables: `ui/escenas/` (las ramas de escenas trabajan ahí), `ui/Tema.kt`, `data/Modelo.kt` (`SCHEMA_V`) y `docs/paper/apendiceD_cambios.md` (números de fila).
- **Mezcla del 17 nov 2026.** Se trajo esta rama y `main` a la rama de escenas (`ccr-e50e410b-nuv2vd`): chocaron `gradle.properties` (se quedó Kotlin 9 GB y Gradle 3 GB) y `CoberturaTemas.kt` (`SIN_CUADRO` = `colonos`, más los cinco temas nuevos en `SIN_CUADRO_LOTE10`). Las escenas de S9 a S18 llevan ya su franja de fondo. De ahí pasa a `main`.
- **Ensayo del 17 nov 2026.** `main` ya está traído a la rama (solo aportó documentos; sin choques). Lo que todavía no está en `main` y chocará cuando entre:
  - **La rama grande de escenas** (`ccr-e50e410b-nuv2vd`, tandas hasta la S18: unos 235 commits, 216 archivos en `ui/escenas/`). Mezclada de prueba con esta rama, choca solo en dos archivos, los dos fáciles:
    - `gradle.properties`: las dos subieron la memoria del compilador (aquí a 9 GB, allá a 8 GB). Se queda la mayor, y hay que comprobar en Actions que con las escenas **y** los Idearios juntos alcanza.
    - `app/src/test/.../CoberturaTemas.kt`: la lista `SIN_CUADRO`. Allá queda solo `colonos`; aquí se agregaron los temas nuevos que aún no tienen cuadro. Se toma la de allá y se le suman los temas nuevos sin cuadro (`psicodelia`, `frente` y los que falten).
  - **Dos ramas más viejas de escenas** (`ccr-ef0ce532-g0omes`, S11, y `claude/gallant-goldberg-bsc3pm`, S10) chocan en el mismo `CoberturaTemas.kt`; parecen estar ya contenidas en la grande.
  - **Lo que no sale como choque pero hay que mirar después de mezclar:** que los cuadros nuevos compilen contra `EscenaVida` de esta rama (`claves`, `franjaFondo`); que cada cuadro nuevo tenga una franja de fondo razonable (por omisión toma el centro); que los cuentos de los cuadros nuevos no tienen ambiente (no suena nada: se agregan con `herramientas/ambientes.py`); y las pruebas de guarda (`CoberturaTemas`, `UnicidadTemas`).
