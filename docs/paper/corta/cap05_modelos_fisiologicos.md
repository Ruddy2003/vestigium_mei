# Capítulo 5. Modelos fisiológicos y variables derivadas

> **Para qué sirve este capítulo.** Algunas variables de la app no se miden: se **calculan con un modelo** a partir de otros datos. La cafeína que te queda al dormir, el alcohol en sangre, la reserva de creatina o el «jet lag social» son **estimaciones**, no mediciones. Este capítulo explica cada modelo: qué calcula, con qué fórmula y parámetros, de dónde salen y cuándo falla. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); lo de las entregas 10e y 10f se marca «desde 10e» y «desde 10f». Los ejemplos usan datos inventados, y los problemas se numeran H-4-n (por el módulo 4). **Ningún número de este capítulo es una medición de tu cuerpo**: son cuentas con parámetros promedio de la literatura.

---

## 5.1 Qué se estima y con qué

Casi todos los modelos de este capítulo son de *farmacocinética*: cómo entra una sustancia al cuerpo (**absorción**, el paso del estómago a la sangre), se reparte y sale (**eliminación**, sobre todo por el hígado).

| Qué | Función | Parámetros | Tipo |
|---|---|---|---|
| Cafeína en el cuerpo | `Sustancias.cafeinaEn` | absorción 45 min; vida media 5 h (ajustable) | Aproximación |
| Alcohol en sangre | `Sustancias.alcoholEn` | absorción 30 min; β = 0.15 g/L/h; *r* de Watson o fija | Aproximación |
| Nivel subjetivo | `Sustancias.nivelEn` | tus lecturas de 0 a 10 | Diseño propio |
| Reserva de creatina | `Suplementos.carga` | 28 días para llenar con 3 g al día, 35 para vaciar | Aproximación |
| Otros suplementos | `Suplementos.cantidadEn` | vida media por suplemento | Aproximación |
| Jet lag social | `Analisis.ritmo` | noches de los últimos 56 días | Validado (definición) / Aproximación |

---

## 5.2 Cafeína en el cuerpo (`Sustancias.cafeinaEn`)

Estima la cafeína en el cuerpo en cada momento. Para cada dosis *D* (mg) tomada hace Δ*t* horas:

$$C(t) = \sum_{\text{dosis}} D \cdot \min\!\left(1, \frac{\Delta t}{0.75}\right) \cdot 0.5^{\Delta t / T_{1/2}}$$

El primer factor sube en línea recta de 0 a 1 en **45 minutos** (0.75 h): la absorción. El segundo es la eliminación con la **vida media** *T*½, el tiempo que tarda en irse **la mitad** de lo que hay: con 5 h, de 100 mg quedan 50 a las 5 h y 25 a las 10 h. Esto se llama eliminación **de primer orden** (se va una proporción fija por hora). La vida media es `Config.cafeina_vida_media_h`, **5 h por omisión** (ajustable de 2 a 10 h). Las dosis se suman.

**De dónde salen las dosis** (`Sustancias.dosis`): de **MacroFactor** y de lo que anotes en la app. Desde la 10e (D.91) se juntan **toma por toma**: una toma de la app y una de MacroFactor a **90 minutos o menos** y de cantidad parecida (**de la mitad al doble**) son la misma y cuentan una vez, con la hora y los miligramos de la app (la hora de MacroFactor es la del registro, no la de la toma). Antes, si MacroFactor tenía algo ese día, lo anotado en la app se ignoraba. Los 90 minutos y el rango son **[Diseño propio]**. Desde la 10c, un alimento de MacroFactor sin hora legible se pone **al mediodía de su día** (`msComida`) en lugar de perderse.

**Variables.** `cafeina_total_mg` suma las dosis del día lógico; `cafeina_al_dormir_mg` es la cafeína estimada **en el instante en que te dormiste** según el reloj, con las dosis de las **36 horas** previas, y va en el día en que despiertas. `cafeina_ultima_h` es, desde la 10e (D.92), la hora de la última dosis del día lógico en esa misma lista (antes de las 5:00 suma 24); antes salía solo de MacroFactor. `cafeina_mg` sigue viniendo **directo de MacroFactor**, sin el modelo.

**Por qué.** Según el Institute of Medicine (2001), la cafeína se absorbe rápida y casi completamente (alrededor del **99 % en 45 minutos**), y su vida media en adultos sanos es en promedio de **unas 5 horas**, con un rango individual de **1.5 a 9.5 horas**. Nehlig (2018) explica que esa variabilidad depende en gran parte de la enzima hepática CYP1A2, que metaboliza el 95 % de la cafeína y tiene variantes genéticas. **[Aproximación]**: el modelo es razonable para el promedio, pero tu vida media real puede estar en cualquier punto de ese rango.

**Ejemplo (Figura 5.1).** Un café de **95 mg a las 8:00** y uno de **150 mg a las 14:00**; te duermes a las **23:30**. Con *T*½ = 5 h: 95 × 0.5^(15.5/5) = **11.1 mg** más 150 × 0.5^(9.5/5) = **40.2 mg**, así que `cafeina_al_dormir_mg` = **51.3 mg**. Con una vida media de 2.5 h serían **12 mg**; con 9.5 h, **106 mg**. **La misma tarde da una cifra que varía casi nueve veces según la persona**: sirve para compararte contigo mismo, no como dato absoluto.

![Figura 5.1](figuras/fig25_cafeina.png)

*Figura 5.1. Cafeína estimada tras dos dosis con tres vidas medias. Script: `figuras/fig25_cafeina.py`.*

**Cuándo falla.**

- **Pico bajo y temprano.** Como la rampa se multiplica por la eliminación desde el primer minuto, a los 45 min queda el **90 %** de la dosis. Para dosis separadas por horas la diferencia con un modelo estándar es pequeña. **[Aproximación]**.
- **Una sola vida media para toda la historia**: si la cambias, **todas** las `cafeina_al_dormir_mg` pasadas se recalculan, sin dejar registro. (Este punto y el anterior son H-4-1, resuelto por documentación.)
- **Juntar dos fuentes** (H-4-2, resuelto en 10e): dos cafés distintos y parecidos, uno en cada app y a menos de 90 minutos, cuentan como uno; la misma taza registrada en MacroFactor mucho después cuenta dos veces.
- **Cuatro variables de cafeína** (H-4-12, resuelto en parte): desde la 10e tres salen de las mismas dosis; `cafeina_mg` sigue siendo el nutriente de MacroFactor.

---

## 5.3 Alcohol en sangre: el modelo de Widmark (`Sustancias.alcoholEn`)

**Qué hace.** Estima la concentración de alcohol en sangre (g/L, aproximadamente lo mismo que «por mil») a lo largo de la noche. La fórmula clásica de Widmark dice que la concentración es el alcohol tomado repartido en el agua del cuerpo, menos lo que el hígado ya quitó:

$$C(t) = \frac{A}{r \cdot W} - \beta \cdot t$$

con *A* = gramos de alcohol, *W* = peso (kg), *r* = **factor de Widmark** (la fracción del peso en la que se reparte el alcohol, casi toda el agua del cuerpo) y β = velocidad de eliminación. *r* · *W* funciona como el «tamaño del recipiente»: con la misma cantidad, uno más chico da una concentración más alta. El alcohol se elimina **de orden cero** (una **cantidad fija** por hora), porque la enzima que lo procesa trabaja a su máxima velocidad desde los primeros tragos (Jones, 2010).

**Cómo lo hace.** Una **simulación en pasos de 5 minutos**: cada dosis entra repartida en **30 minutos**; en cada paso el cuerpo quita β · *r* · *W* · (5/60) gramos, con **β = 0.15 g/L por hora**, sin bajar de 0; la concentración es gramos en el cuerpo ÷ (*r* · *W*). Una **bebida estándar** son **14 g** (`GRAMOS_BEBIDA`), la definición de Estados Unidos (NIAAA, s. f.).

**Los parámetros** (desde la 10c, calculados **para cada día** con `rYPeso`):

- *W* (`pesoDe`): el peso **más reciente hasta ese día** entre la báscula de Health Connect y la tendencia de MacroFactor (el mismo día gana la báscula); si no hay ninguno antes, el primero posterior. Solo sin ningún peso registrado se usan **70 kg** (`PESO_OMISION`).
- *r*: con un **peso real**, sale del **agua corporal total** de Watson et al. (1980) (`watsonR`):
  - hombres: agua (L) = 2.447 − 0.09516 · edad + 0.1074 · estatura (cm) + 0.3362 · peso (kg);
  - mujeres: agua (L) = −2.097 + 0.1069 · estatura (cm) + 0.2466 · peso (kg);
  - *r* = agua / (0.80 · peso), acotada entre **0.45 y 0.95**; el 0.80 (`AGUA_SANGRE`) es la proporción de agua en la sangre que usan Watson et al. (1981).

  Hacen falta el sexo, la estatura (entre **100 y 250 cm**) y, en los hombres, la edad. Si falta algo, o el peso es el de omisión, se usa la *r* fija (`widmarkR`): **0.68** «hombre», **0.55** «mujer», **0.62** si no dijiste nada.

**De dónde salen las dosis.** De MacroFactor y de los episodios que anotas. Desde la 10e (D.91), un episodio y lo de MacroFactor entre su inicio y su fin (**±90 minutos**) son lo mismo, y cuenta la fuente con más gramos **[Diseño propio]**; antes, si MacroFactor tenía alcohol ese día, se ignoraban los episodios.

**Variables.** `alcohol_bebidas` = gramos del día ÷ 14; `alcohol_estimado_max`, desde la 10e (D.88 y D.89), el máximo de la simulación desde la primera dosis del día hasta **14 horas** después (o hasta la primera dosis del siguiente día con alcohol), **incluyendo las dosis de las 24 horas previas** y con muestras **cada 5 minutos**, como la lista de episodios; `alcohol_al_dormir`, la concentración al dormirte con las dosis de las **18 horas** previas; `alcohol` (0/1), 1 el día lógico de cada dosis (o del inicio de un episodio sin cantidad).

**Por qué.** **β = 0.15 g/L/h** es el valor que Jones (2010) recomienda como **promedio para bebedores moderados**: en ayunas, de 0.10 a 0.15; después de comer, de 0.15 a 0.20; con consumo intenso, hasta 0.25–0.35. **[Validado]** como promedio. Los *r* de 0.68 y 0.55 son los valores clásicos de Widmark; el 0.62 es un punto intermedio **[Diseño propio]**. En realidad *r* depende de la composición corporal: según Searle (2015), es el agua corporal total dividida entre el peso por la proporción de agua en la sangre. Usar Watson es **[Aproximación]** (mejor que la fija): según Searle (2015), la *r* conserva una incertidumbre de alrededor de un 9 %. **El valor 0.80 [sin verificar]**: no se leyó en el texto original de Watson et al. (1981). El error de la concentración calculada **no es fijo** (Searle, 2015): el modelo da un orden de magnitud, no una cifra al centésimo.

**Ejemplo.** Tres bebidas estándar (42 g) de 20:00 a 23:00; la app las coloca a las **20:00, 21:03 y 22:06** (repartidas en el primer 70 % del episodio). Un **hombre de 75 kg** con *r* = 0.68 tiene *r* · *W* = 51: si todo entrara de golpe serían 42/51 = 0.82 g/L, pero con absorción y eliminación simultáneas la simulación da un **máximo de 0.42 g/L a las 22:35** y llega a **0 a la 01:25**. Una **mujer de 60 kg** (*r* = 0.55) llega a **0.87 g/L**. Con la *r* de Watson, el mismo hombre a los 30 años y 180 cm tiene *r* = **0.736** y su máximo baja a **0.36 g/L**. El error que más pesaba no era *r*, sino el **peso**: un hombre de 90 kg calculado con 70 kg sale con un máximo de **0.61 g/L** en lugar de **0.41 g/L** (más tablas y una figura en la versión completa, §5.4.4).

**Cuándo falla.**

- **Parámetros fijos** (H-4-4, resuelto en parte): β no se ajusta a si comiste; sin ningún peso se usan **70 kg sin avisar** en la pantalla.
- **No muestra incertidumbre**: dice «≈ 0.45 g/L estimado», pero el error razonable es de varias décimas.
- **Absorción de 30 minutos fija**: con el estómago lleno el pico es más bajo y tardío.
- **Cada día se simulaba por separado** (H-4-5, resuelto en 10e): un episodio que cruzaba las 5:00 dejaba las bebidas posteriores en el día siguiente, que arrancaba de cero. Ahora ese día arrastra lo anterior; un pico que llega después de la primera bebida del día siguiente cuenta en ese día.
- **Dos máximos** (H-14-5, resuelto en 10e): el panel miraba cada 10 minutos y la lista de episodios cada 5, y podía dar hasta 0.0125 g/L menos. Con un episodio por día y sin alcohol en las horas previas, ahora dan lo mismo.

---

## 5.4 Episodios con nivel subjetivo (`Sustancias.nivelEn`)

Para alcohol y cannabis puedes decir **qué tan fuerte lo sientes** de 0 a 10. Con lecturas, la curva las une con líneas rectas (y baja a 0 al terminar el episodio); solo con el máximo, es un trapecio que sube de 0 al máximo en el primer 35 % del episodio, se queda hasta el 65 % y baja a 0 al final. Si el episodio quedó abierto de otro día, termina **4 horas** después del inicio (`finEfectivo`). Variables: `alcohol_nivel_max`, `cannabis_nivel_max`, `alcohol_horas`, `cannabis_horas`. **[Diseño propio]**: son **autorreportes**, no estimaciones fisiológicas; tener la medida estimada y la sentida es útil.

---

## 5.5 Suplementos (`data/Lote3.kt`)

`Suplementos` ofrece tres modelos, elegibles por suplemento y con parámetros editables. Para todos se guardan `supl_<id>_dosis` (la suma de las tomas del día), `supl_<id>_tomado` (0/1) y, desde el esquema 21, `supl_<id>_n` (cuántas tomas). Desde ese esquema se pueden anotar varias tomas al día, y cada suplemento dice cuántas lleva (`tomas_dia`), lo que decide el recordatorio (con varias horas posibles).

**Carga: la creatina (`Suplementos.carga`).** La creatina no sube y baja en horas: se **acumula** en el músculo durante semanas. El modelo representa esa reserva de 0 a 100 % (su **saturación**):

$$s = 1 - 0.05^{1/\text{carga\_días}}, \qquad b = 1 - 0.05^{1/\text{lavado\_días}}$$

Un día con dosis *d* se llena una fracción de **lo que falta**, $\left(1 - (1-s)^{d/\text{ref}}\right)\cdot(100 - \text{nivel})$; un día sin dosis se vacía una fracción de **lo que hay** (nivel × (1 − *b*)). *ref* es la dosis de referencia (`Suplementos.dosisReferencia`): desde la 10e (D.98), **3 g en la creatina**, aunque el catálogo diga 5 g. Con 28 y 35 días, *s* = 0.1015 y *b* = 0.0820: con 3 g diarios, al día **28** queda lleno al **95 %**, y sin tomarla, a los **35 días** queda el **5 %** de lo que había. Con 5 g diarios se llega al 95 % el **día 17** (28 × 3/5 = 16.8). Las variables son `supl_creatina_nivel` y `creatina_saturacion_pct`.

Hultman et al. (1996) midieron que la creatina del músculo aumentaba alrededor de un **20 %** tras 6 días a 20 g diarios, que **30 días después de dejarla ya no difería** del valor inicial y que con **3 g diarios** se alcanzaba el mismo 20 % en **28 días**. **[Aproximación]**; que 5 g llenen en 17 días sale de la forma del modelo, no del estudio **[Diseño propio]**. **Cuándo falla** (H-4-6, resuelto en 10e): hasta la 10e la referencia era 5 g y 3 g diarios daban solo 83 % al día 28, más lento que la fuente. La referencia de 3 g está fija en el código: cambiar la dosis en la app ya no cambia la velocidad. Y el 100 % es del modelo, no una concentración: no considera la dieta, el punto de partida ni a quienes casi no responden.

**Vida media (`Suplementos.cantidadEn`).** La cantidad es la suma de cada toma por 0.5^(horas / vida media), **sin fase de absorción**, evaluada **al final del día lógico** (las 5:00 del día siguiente). Desde la 10e (D.98), `supl_<id>_nivel` solo existe con vida media de **6 h o más** (`VIDA_MEDIA_MIN_NIVEL`, **[Diseño propio]**).

- **Vitamina D, 360 h (15 días)**: coincide con la vida media de **25(OH)D**, la forma que circula en sangre (Jones, 2008). **[Aproximación]**: el modelo suma «UI en el cuerpo», que no es lo que mide un análisis; con 2000 UI diarias se estabiliza en unas **44 000 UI**, útil solo como tendencia.
- **Melatonina, 1 h**: la vida media oral va de unos 28 a 126 minutos (Harpsøe et al., 2015). **[Validado]** como orden de magnitud. Pero tomada a las 23:00, a las 5:00 queda el 1.6 %: `supl_melatonina_nivel` **era casi siempre ≈ 0**, y desde la 10e ya no existe (H-4-7, resuelto en parte); la variable útil es `supl_melatonina_tomado`.
- **Magnesio, 12 h; omega-3, 60 h**: sin fuente que los respalde **[sin verificar]**.

**Exposición: los probióticos (`Suplementos.probioticosEstimados`).** Solo se cuentan **porciones**: a lo anotado se suma una por cada alimento de MacroFactor cuyo nombre contenga una palabra clave configurada (como «yogur»). Desde la 10c, `probioticos_estimado` queda vacía los días sin bitácora de MacroFactor. **[Diseño propio]**: no dice nada de cultivos vivos (H-4-8).

---

## 5.6 Ritmo de sueño y jet lag social (`Analisis.ritmo`)

**Qué hace.** La herramienta «Ritmo» de Análisis muestra a qué hora te duermes y despiertas, cuánto duermes antes de días laborales y libres, la **regularidad** y el **jet lag social**: la diferencia entre el **punto medio del sueño** (la hora a la mitad entre dormirte y despertar) en días libres y en laborales, como si cada fin de semana viajaras a otro huso horario (Wittmann et al., 2006). No crea variables del panel.

**Cómo.**

1. Solo noches que terminan en los **últimos 56 días** (`Analisis.recientes`, desde la 10c).
2. **Noches del reloj**: si el inicio es antes de las 12:00 se le suman 24 (dormirte a la 1:00 cuenta como 25.0, para promediarlo bien con las 23:00). Desde la 10f (D.109; H-1-7), cada hora se lee con su propio desfase, en la hora local de donde dormiste; antes se pasaba a la zona de hoy, y una noche de 23:30 a 07:30 en Madrid (UTC+2), leída de vuelta en UTC−6, «duraba» 32 h y se descartaba.
3. **Noches del teléfono**, si faltan del reloj: inicio = **último uso** del día lógico (a las **18:00 o después**, o de madrugada); fin = **primer uso** del día siguiente (a las **4:00 o después**). Con **7 o más** noches del reloj se usan solo esas; si no, se completan con las del teléfono, y la pantalla lo avisa.
4. Se descartan noches de menos de **2 h** o más de **16 h**; hacen falta al menos **5**.
5. **Días libres** (`Fechas.esDiaLibre`): el día en que despiertas es **sábado, domingo** o, si dijiste que vives en México, un **festivo** de la Ley Federal del Trabajo, art. 74 (`Fechas.esFestivoMx`; lista en la versión completa, §5.7.2). La misma regla da `festivo` y `dia_libre` en el panel exportado.
6. **MSW** = promedio de los puntos medios laborales; **MSF**, de los libres (cada uno con al menos 2 noches). **Jet lag social** = |MSF − MSW|.
7. **Regularidad** = desviación estándar de la hora de dormirte («varía ±*N* min»).

**Por qué.** Wittmann et al. (2006) lo definieron como la discrepancia entre el horario biológico y el social y lo asociaron con peor bienestar y más consumo de estimulantes; Roenneberg et al. (2012) lo asociaron con un índice de masa corporal más alto, más allá de la duración del sueño. **[Validado]** como definición y como variable asociada a la salud. La pantalla dice que «más de 1 hora se ha asociado con peor ánimo y salud»: **el umbral de 1 hora [sin verificar]**. Para la regularidad hay medidas específicas (el índice de Phillips et al., 2017); la desviación estándar es más simple. **[Aproximación]**. Las noches del teléfono son un proxy (una medida indirecta): usar el teléfono por última vez no es dormirte; Cuttone et al. (2017) estimaron bien el sueño con un modelo estadístico de la pantalla, y la app usa una regla mucho más simple. **[Aproximación]**.

**Ejemplo (Figura 5.2).** Tres semanas inventadas: entre semana te duermes hacia las **00:15** y despiertas a las **06:45**; viernes y sábado, hacia las **01:45** y las **10:15**. **MSW** = **03:26**, **MSF** = **06:03**: jet lag social = **2.62 h**. Duermes **6.7 h** entre semana y **8.4 h** el fin de semana. La hora de dormir «varía **±53 min**», pero casi toda esa variación es la diferencia **sistemática** entre semana y fin de semana: la desviación estándar no la distingue de dormirse cada día a una hora distinta.

![Figura 5.2](figuras/fig28_jetlag_social.png)

*Figura 5.2. 21 noches sintéticas dibujadas como en la app, con los puntos medios y el jet lag social. Script: `figuras/fig28_jetlag_social.py`.*

**Cuándo falla** (H-4-9 y H-4-10, resueltos en parte). Los días libres no incluyen vacaciones, turnos ni el horario de Conocerte: un sábado trabajado se clasifica mal, y las vacaciones pesan mucho más que los festivos. El valor absoluto pierde la dirección. Con menos de 7 noches del reloj se mezclan dos medidas distintas en un mismo promedio; la pantalla lo avisa, pero las sigue mezclando.

---

## 5.7 Otras variables derivadas

- **Aire libre y kilómetros** (capítulo 4): `aire_libre_min` es el mayor entre los minutos con **1000 lux o más** y los minutos caminando, corriendo o en bici, y **subestima**; `distancia_km` es una **cota inferior** del recorrido (H-3-5). **[Aproximación]**.
- **Fase lunar** (`Luna`): con el **mes sinódico medio** de 29.530588853 días; iluminación = (1 − cos 2π*f*)/2 (`luna_ilum`). **[Validado]** como aproximación astronómica; cuánto se desvía de la fase real no se cuantificó **[sin verificar]**.
- **Enfoque y siestas** (`data/Enfoque.kt`): bloques del cronómetro **sin contar las pausas**; los de **menos de 1 minuto** no se guardan. Son **mediciones** de tu cronómetro, no modelos. Desde el esquema 21, un bloque no se corta en su meta: avisa y sigue contando hasta que pasas al descanso o terminas. Se guarda lo real (`min`) y lo planeado (`plan_min`), así que `enfoque_min` incluye lo de más (H-21-1).
- **Repetición máxima estimada** (1RM, el mayor peso que podrías levantar una vez): peso × (1 + repeticiones/30), la **fórmula de Epley** (con 1 repetición, el peso mismo desde el 5 nov 2026); su fuente original **[sin verificar]**. Desde la 10c hay **un solo criterio** en toda la app (`Macrofactor.e1rm`): series de 1 a **12** repeticiones, sin calentamientos. *Ejemplo:* con 100 kg × 15, 120 kg × 5 y un calentamiento de 130 kg × 8, da 120 × (1 + 5/30) = **140 kg** (H-4-13, resuelto). Desde la 10f (D.114; H-7-14), `fuerza_ref_kg` es la mayor de esas cifras del día en un solo ejercicio fijo, el press inclinado con mancuernas, reconocido por su nombre en inglés o en español (`Macrofactor.esReferencia`, **[Diseño propio]**); vacía los días sin ese ejercicio. La plantilla de experimento de la creatina la mide en vez del volumen, que cambia según qué entrenaste; si vas subiendo de peso, la cifra sube sola.
- **Grados de escalada** (`Grados`): tablas fijas de equivalencia. **[Aproximación]**: las tablas publicadas no coinciden entre sí.

---

## 5.8 Estimaciones, no mediciones

1. **Parámetros promedio.** Tu cuerpo puede estar lejos del promedio; los resultados sirven sobre todo para **compararte contigo mismo** con los mismos parámetros.
2. **Sin incertidumbre visible.** Ninguna pantalla ni variable dice cuánto podría equivocarse la cifra.
3. **Parámetros que reescriben el pasado** (H-4-11, resuelto en parte). Al cambiar la vida media de la cafeína o los parámetros de un suplemento, **todas** las variables derivadas pasadas se recalculan, sin guardar qué parámetro se usó antes: dos exportaciones de fechas distintas pueden no ser comparables. Desde la 10e (D.99), el diccionario de cada exportación dice con qué parámetros se calcularon `cafeina_al_dormir_mg`, `creatina_saturacion_pct` y cada `supl_<id>_nivel`.

Una relación con una variable estimada es tan buena como el modelo: `cafeina_al_dormir_mg` es, en la práctica, una **combinación de la cantidad y la hora** de tus cafés. Si se relaciona con dormir peor, dice que «tomar más o más tarde» se asocia con dormir peor; no que tu sangre tuviera exactamente esos miligramos.

---

## 5.9 En resumen

1. La cafeína y el alcohol se estiman con modelos sencillos y **parámetros promedio bien elegidos** (5 h de vida media y 45 min de absorción; β = 0.15 g/L/h), pero la variación entre personas es enorme: la misma tarde de café deja de 12 a 106 mg al dormir según la vida media.
2. Desde la 10c, el alcohol usa el peso más reciente entre la báscula y MacroFactor y, con los datos, la *r* de Watson; sin pesos usa 70 kg y no muestra su incertidumbre (H-4-4, en parte).
3. Desde la 10e, MacroFactor y la app se juntan toma por toma (H-4-2) y el máximo de alcohol arrastra la noche anterior (H-4-5).
4. El modelo de creatina se basa en Hultman et al. (1996) y, desde la 10e, usa su dosis de 3 g como referencia (H-4-6); la melatonina ya no tiene `nivel` (H-4-7, en parte).
5. El jet lag social está bien definido; usa las noches de los últimos 56 días y los festivos de México, pero no las vacaciones, y la regularidad es una desviación estándar simple.
6. Todas son **estimaciones**: sirven para compararte contigo mismo, y cambiar un parámetro reescribe el pasado (H-4-11).
7. De los 13 hallazgos del módulo 4, **7 están resueltos** (H-4-1 por documentación, H-4-2, H-4-3, H-4-5, H-4-6, H-4-8 y H-4-13) y **6 resueltos en parte** (H-4-4, H-4-7, H-4-9, H-4-10, H-4-11 y H-4-12).
