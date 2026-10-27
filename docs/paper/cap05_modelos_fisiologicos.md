# Capítulo 5. Modelos fisiológicos y variables derivadas

> **Para qué sirve este capítulo.** Algunas variables de la app no se miden: se **calculan con un modelo** a partir de otros datos. La cafeína que te queda en el cuerpo al dormir, el alcohol en sangre, la reserva de creatina del músculo, el «jet lag social» o los minutos al aire libre son **estimaciones**, no mediciones. Este capítulo explica cada modelo: qué calcula, con qué fórmula exacta y qué parámetros, de dónde salen esos parámetros, qué tan preciso puede ser y cuándo falla. Todo corresponde a la versión v81 (commit `fa527df`). **Los ejemplos usan datos inventados.**
>
> **Cómo leerlo.** Igual que los capítulos anteriores: lenguaje sencillo, fórmulas y cifras del código tal cual, con el nombre de la función. Cada decisión se marca como **[Validado]**, **[Aproximación]** o **[Diseño propio]**. Los problemas se numeran H-4-n y se detallan en `hallazgos.md`. **Ningún número de este capítulo es una medición de tu cuerpo**: son cuentas con parámetros promedio de la literatura.

---

## 5.1 Mini-glosario

| Término | Qué significa aquí |
|---|---|
| **Farmacocinética** | Cómo entra, se reparte y sale una sustancia del cuerpo con el tiempo. |
| **Absorción** | El paso de la sustancia del estómago a la sangre. Tarda un rato: por eso un café no hace efecto en el segundo en que lo tomas. |
| **Eliminación** | Cómo el cuerpo la va quitando (sobre todo el hígado). |
| **Vida media** (T½) | El tiempo que tarda en irse **la mitad** de lo que hay. Si la vida media es 5 h, de 100 mg quedan 50 a las 5 h, 25 a las 10 h, 12.5 a las 15 h. Esto se llama eliminación **de primer orden** (se va una proporción fija por hora). |
| **Eliminación de orden cero** | Se va una **cantidad fija** por hora, sin importar cuánto haya. El alcohol se elimina así casi siempre, porque la enzima que lo procesa trabaja a su máxima velocidad desde los primeros tragos (Jones, 2010). |
| **Volumen de distribución** | El «tamaño del recipiente» en el que se diluye una sustancia. Con la misma cantidad, un recipiente más chico da una concentración más alta. |
| **Factor de Widmark** (*r*) | La fracción del peso corporal en la que se reparte el alcohol (casi toda el agua del cuerpo). |
| **Saturación** | Qué tan llena está una reserva (aquí, la de creatina del músculo), de 0 a 100 %. |
| **Punto medio del sueño** | La hora a la mitad entre dormirte y despertar. |
| **Jet lag social** | La diferencia entre el punto medio del sueño en los días libres y en los laborales: como si cada fin de semana viajaras a otro huso horario y volvieras el lunes (Wittmann et al., 2006). |
| **Proxy** | Una medida indirecta de lo que interesa (capítulo 4). |

---

## 5.2 Mapa: qué se estima y con qué

| Qué | Función (archivo) | Parámetros | Variables del panel | Tipo |
|---|---|---|---|---|
| Cafeína en el cuerpo | `Sustancias.cafeinaEn` (`data/Sustancias.kt`) | absorción 45 min; vida media 5 h (ajustable) | `cafeina_total_mg`, `cafeina_al_dormir_mg` (y `cafeina_mg`, `cafeina_ultima_h` de MacroFactor) | Aproximación |
| Alcohol en sangre | `Sustancias.alcoholEn` | absorción 30 min; β = 0.15 g/L/h; *r* = 0.68, 0.55 o 0.62; peso | `alcohol_bebidas`, `alcohol_estimado_max`, `alcohol_al_dormir` | Aproximación |
| Nivel subjetivo de un episodio | `Sustancias.nivelEn` | tus lecturas de 0 a 10 | `alcohol_nivel_max`, `cannabis_nivel_max`, `*_horas` | Diseño propio |
| Reserva de creatina | `Suplementos.carga` (`data/Lote3.kt`) | 28 días para llenar, 35 para vaciar | `creatina_saturacion_pct`, `supl_creatina_nivel` | Aproximación |
| Suplementos con vida media | `Suplementos.cantidadEn` | vitamina D 360 h, magnesio 12 h, melatonina 1 h, omega-3 60 h | `supl_<id>_nivel` | Aproximación |
| Probióticos | `Suplementos.probioticosEstimados` | palabras clave en la bitácora de comidas | `probioticos_porciones`, `probioticos_estimado` | Diseño propio |
| Ritmo de sueño y jet lag social | `Analisis.ritmo` (`ciencia/Analisis.kt`) | sábado y domingo = días libres | solo en la pantalla Análisis | Validado (definición) / Aproximación |
| Noches sin reloj | `Analisis.ritmo` | último y primer uso del teléfono | solo en Análisis | Aproximación |
| Aire libre, km | `Estadistica.panel`, `Movilidad.kmPorDia` | luz ≥ 1000 lux, actividad; trayectos en línea recta | `aire_libre_min`, `distancia_km` | Aproximación (capítulo 4) |
| Otras | `Luna`, `Grados` (`data/Calculos.kt`), fórmula de Epley (reportes) | — | `luna_ilum`, grados de escalada | Validado / Aproximación |

---

## 5.3 Cafeína en el cuerpo

### 5.3.1 Qué hace

Estima cuántos miligramos de cafeína tienes en el cuerpo en cada momento. Se usa en la pantalla de sustancias («ahora», «baja de 50 mg a las…»), en la cinta del día y en el panel (`cafeina_al_dormir_mg`).

### 5.3.2 Cómo lo hace exactamente (`Sustancias.cafeinaEn`)

Para cada dosis *D* (mg) tomada hace Δ*t* horas:

$$C(t) = \sum_{\text{dosis}} D \cdot \min\!\left(1, \frac{\Delta t}{0.75}\right) \cdot 0.5^{\Delta t / T_{1/2}}$$

- El primer factor sube en línea recta de 0 a 1 en **45 minutos** (0.75 h): la absorción.
- El segundo es la eliminación de primer orden con la vida media *T*½ de `Config.cafeina_vida_media_h`, **5 h por omisión** (se cambia en Privacidad).
- Las dosis se suman: cada café se va por su cuenta.

**De dónde salen las dosis** (`Sustancias.dosis`): de **MacroFactor** (cada alimento con su hora y su `cafeina_mg`) y de lo que anotes en la app. **Por día y sustancia, una sola fuente**: si MacroFactor tiene algo de cafeína ese día, **mandan los datos de MacroFactor** y lo anotado en la app ese día se ignora, para no contar dos veces.

**Variables del panel:**

- `cafeina_total_mg`: suma de las dosis del día lógico (de ambas fuentes, con la regla anterior), desde el primer día con cafeína.
- `cafeina_al_dormir_mg`: la cafeína estimada **en el instante en que te dormiste** según el reloj, sumando las dosis de las **36 horas** previas. Va en el día del sueño (el día en que despiertas).
- `cafeina_mg` y `cafeina_ultima_h` vienen **directo de MacroFactor** (total del día y hora de la última), sin el modelo.

### 5.3.3 Razonamiento científico

El informe del Institute of Medicine (2001) resume: la cafeína se absorbe **rápida y casi completamente** (alrededor del **99 % en 45 minutos**, con el pico en sangre entre 15 y 120 minutos), y su vida media en adultos sanos es en promedio de **unas 5 horas**, con un rango individual de **1.5 a 9.5 horas**. El embarazo, la obesidad, los anticonceptivos orales, el tabaco y la altitud la cambian. Nehlig (2018) explica que esa variabilidad depende en gran parte de la enzima hepática CYP1A2, que metaboliza el 95 % de la cafeína y tiene variantes genéticas.

Los parámetros de la app (45 min y 5 h) coinciden con esos promedios. **[Aproximación]**: el modelo es razonable para el promedio, pero tu vida media real puede estar en cualquier punto de ese rango.

### 5.3.4 Ejemplo trabajado (Figura 5.1)

Un café de **95 mg a las 8:00** y uno grande de **150 mg a las 14:00**; te duermes a las **23:30**. Con *T*½ = 5 h:

- Primer café: han pasado 15.5 h → 95 × 0.5^(15.5/5) = 95 × 0.117 = **11.1 mg**.
- Segundo café: han pasado 9.5 h → 150 × 0.5^(9.5/5) = 150 × 0.268 = **40.2 mg**.
- `cafeina_al_dormir_mg` = **51.3 mg**.

Con la vida media más corta del rango típico (2.5 h) serían **12 mg**; con la más larga (9.5 h), **106 mg**. **La misma tarde da una cifra que varía casi nueve veces según la persona.** Por eso la cifra solo sirve **para compararte contigo mismo** (si fijas tu vida media), no como dato absoluto.

![Figura 5.1](figuras/fig25_cafeina.png)

*Figura 5.1. Cafeína estimada tras dos dosis con tres vidas medias. Script: `figuras/fig25_cafeina.py`.*

### 5.3.5 Cuándo falla

- **El pico queda un poco bajo y temprano.** Como la rampa de absorción se multiplica por la eliminación desde el primer minuto, a los 45 min queda 0.5^(0.75/5) = **90 %** de la dosis. Un modelo farmacocinético estándar (absorción y eliminación de primer orden) da otra forma de curva. Para dosis separadas por horas, la diferencia es pequeña. **[Aproximación]**.
- **Una sola vida media para toda la historia** (H-4-1): si la cambias, **todas** las `cafeina_al_dormir_mg` pasadas se recalculan con la nueva, sin dejar registro.
- **La regla de una sola fuente por día** (H-4-2): si en MacroFactor anotaste un café y en la app otro distinto el mismo día, el de la app se pierde. Y la hora de MacroFactor es la hora en que registraste el alimento, que puede no ser la hora en que lo tomaste.
- **Las cifras cambian según dónde se calculen** (H-4-3): para no trabar la pantalla, cuando el panel se calcula en el hilo principal **sin un cálculo previo guardado**, `Estadistica.sustancias` usa solo lo anotado en la app y **omite MacroFactor**. Explorar, Análisis y la cobertura de variables calculan en segundo plano, pero la pantalla de experimentos (capítulo 8) calcula el panel en el hilo principal: ahí `cafeina_total_mg`, `cafeina_al_dormir_mg` y las del alcohol pueden ser distintas (si no hay un cálculo previo guardado con la misma clave).
- **Cuatro variables de cafeína** con fuentes distintas: `cafeina_mg` y `cafeina_ultima_h` solo de MacroFactor; `cafeina_total_mg` y `cafeina_al_dormir_mg` de ambas (H-4-12).

---

## 5.4 Alcohol en sangre: el modelo de Widmark

### 5.4.1 Qué hace

Estima la concentración de alcohol en sangre (g/L, aproximadamente lo mismo que «por mil», ‰) a lo largo de la noche. Se usa en la cinta del día, en la lista de episodios («≈ 0.45 g/L estimado») y en el panel.

### 5.4.2 Cómo lo hace exactamente (`Sustancias.alcoholEn`)

La fórmula clásica de Widmark (de los años treinta) dice que la concentración es el alcohol tomado repartido en el agua del cuerpo, menos lo que el hígado ya quitó:

$$C(t) = \frac{A}{r \cdot W} - \beta \cdot t$$

con *A* = gramos de alcohol, *W* = peso (kg), *r* = factor de Widmark y β = velocidad de eliminación. La app la aplica como una **simulación en pasos de 5 minutos**:

1. Cada dosis entra **repartida en 30 minutos** (en cada paso de 5 min entra 1/6 de la dosis).
2. En cada paso, el cuerpo quita β · *r* · *W* · (5/60) gramos, con **β = 0.15 g/L por hora**, sin bajar de 0.
3. La concentración es gramos en el cuerpo ÷ (*r* · *W*).

**Los parámetros:**

- *r* (`widmarkR`): **0.68** si en Conocerte o Salud dijiste «hombre», **0.55** si «mujer», **0.62** si no dijiste nada.
- *W* (`pesoEn`): el peso más reciente hasta ese día, de la báscula o de MacroFactor; **70 kg** si no hay ninguno.
- **Bebida estándar** = **14 g** de alcohol puro (`GRAMOS_BEBIDA`), la definición de Estados Unidos (NIAAA, s. f.). Otros países usan otras cifras.

**De dónde salen las dosis** (`Sustancias.dosis`): de MacroFactor (`alcohol_g` de cada alimento, con su hora) o, los días en que MacroFactor no tiene alcohol, de los episodios que anotas. Las bebidas de un episodio se **reparten** en el primer 70 % del episodio (si dijiste cuándo terminó) o una cada 40 minutos (si no).

**Variables del panel** (desde el primer día con alcohol):

- `alcohol_bebidas`: gramos del día ÷ 14.
- `alcohol_estimado_max`: el máximo de una simulación por día, desde la primera dosis y durante **14 horas**, con muestras cada 10 minutos.
- `alcohol_al_dormir`: la concentración en el instante en que te dormiste, con las dosis de las **18 horas** previas.
- Aparte, `alcohol_nivel_max` y `alcohol_horas` vienen de **tu nivel subjetivo** (sección 5.5), no del modelo.

### 5.4.3 Razonamiento científico

- **β = 0.15 g/L/h** es el valor que Jones (2010) recomienda como **promedio para bebedores moderados**, en una revisión de la evidencia forense: en ayunas, de 0.10 a 0.15; después de comer, de 0.15 a 0.20; en personas con consumo intenso, hasta 0.25–0.35. La eliminación es casi de orden cero porque la enzima se satura desde los primeros tragos. **[Validado]** como promedio.
- **Los factores *r*** de 0.68 y 0.55 son los valores clásicos de Widmark para hombres y mujeres; el 0.62 «sin sexo» es un punto intermedio **[Diseño propio]**. En realidad *r* depende de la composición corporal: hay fórmulas que lo estiman con el agua corporal total a partir de peso, estatura, edad y sexo (Watson et al., 1980).
- **La incertidumbre es grande.** Searle (2015) revisa cómo se propaga el error en estos cálculos: Gullberg había propuesto un coeficiente de variación fijo de ±21 % para la concentración calculada, y Searle muestra que el error **no es fijo** y hay que calcularlo en cada caso. **[Aproximación]**: el modelo da un orden de magnitud, no una cifra confiable al centésimo.

### 5.4.4 Ejemplo trabajado (Figura 5.2)

Tres bebidas estándar (42 g) en un episodio de 20:00 a 23:00. La app las coloca a las **20:00, 21:03 y 22:06** (el primer 70 % de 3 h son 2.1 h, repartidas en 2 intervalos).

- **Hombre de 75 kg** (*r* = 0.68): *r* · *W* = 51. Si todo entrara de golpe: 42/51 = 0.82 g/L. Con la absorción y la eliminación simultáneas, la simulación da un **máximo de 0.42 g/L a las 22:35** y llega a **0 a la 01:25**. (Con la fórmula simple: 0.82 − 0.15 × 2.58 h = 0.44 g/L en el pico, y 0.82/0.15 = 5.5 h después de empezar, 01:29: muy parecido).
- **Mujer de 60 kg** (*r* = 0.55): *r* · *W* = 33 → máximo **0.87 g/L**, en 0 a las **04:25**.
- **Sin sexo ni peso** (*r* = 0.62, 70 kg): máximo **0.57 g/L**.

Si en el hombre β fuera 0.10 o 0.20 en lugar de 0.15, el final de la curva se movería más de una hora en cada dirección (banda sombreada).

![Figura 5.2](figuras/fig26_alcohol_widmark.png)

*Figura 5.2. Tres bebidas estándar con la simulación de la app para tres perfiles; la banda muestra β de 0.10 a 0.20 g/L/h. Script: `figuras/fig26_alcohol_widmark.py`.*

### 5.4.5 Cuándo falla

- **Parámetros fijos** (H-4-4): β no se ajusta a si comiste o no (que según Jones cambia de 0.10–0.15 a 0.15–0.20); *r* depende solo del sexo aunque la app ya conoce tu **estatura** (Conocerte) y podría usar la fórmula de Watson; sin peso registrado se usa **70 kg sin avisar**.
- **No muestra incertidumbre**: dice «≈ 0.45 g/L estimado», pero el error razonable es de varias décimas.
- **Unidades**: la fórmula de Widmark da gramos por kilo de peso corporal; la app lo presenta como g/L de sangre. La diferencia (por la densidad y el contenido de agua de la sangre) es pequeña frente a las demás incertidumbres, pero existe.
- **Absorción de 30 minutos fija**: con el estómago lleno la absorción es más lenta y el pico más bajo y tardío.
- **Cada día se simula por separado** (H-4-5): `alcohol_estimado_max` empieza en 0 con la primera dosis del día lógico y no arrastra lo del día anterior. Si un episodio cruza las 5:00, las bebidas posteriores caen en el día siguiente y ese día arranca de cero.
- **Una sola fuente por día** y **dependencia del hilo** (H-4-2 y H-4-3, igual que la cafeína).

---

## 5.5 Episodios con nivel subjetivo (`Sustancias.nivelEn`)

Para alcohol y cannabis (las sustancias «con nivel»), además de las dosis, puedes decir **qué tan fuerte lo sientes** de 0 a 10, una o varias veces. La curva del episodio se arma así:

- Con lecturas: se unen con líneas rectas, empezando en el nivel de la primera lectura; si el máximo que dijiste después (la mañana siguiente) es mayor que lo leído, se agrega ese máximo **a la mitad del episodio**; si el episodio terminó, baja a 0 al final.
- Sin lecturas, solo con el máximo: un trapecio que sube de 0 al máximo en el primer 35 % del episodio, se queda hasta el 65 % y baja a 0 al final.
- **Fin efectivo** (`finEfectivo`): el que dijiste; si el episodio sigue abierto y empezó hace menos de 18 horas, «ahora»; si quedó abierto de otro día, **4 horas** después del inicio.

Variables: `alcohol_nivel_max`, `cannabis_nivel_max` (el mayor nivel del día) y `alcohol_horas`, `cannabis_horas` (duración). **[Diseño propio]**: son **autorreportes**, no estimaciones fisiológicas; la forma de trapecio y el «4 horas por omisión» son decisiones de la app. Es útil tener ambas medidas: la estimada (Widmark) y la sentida.

---

## 5.6 Suplementos: tres modelos

`Suplementos` (`data/Lote3.kt`) ofrece tres modelos, elegibles por suplemento y con parámetros editables en la app. Catálogo inicial:

| Suplemento | Dosis de referencia | Modelo | Parámetros |
|---|---|---|---|
| Creatina | 5 g | carga | 28 días para llenar, 35 para vaciar |
| Vitamina D | 2000 UI | vida media | 360 h (15 días) |
| Magnesio | 300 mg | vida media | 12 h |
| Melatonina | 1 mg | vida media | 1 h |
| Omega-3 | 1000 mg | vida media | 60 h |
| Probióticos | 1 porción | exposición | — |

Para todos se guardan, por día, `supl_<id>_dosis` (lo anotado) y `supl_<id>_tomado` (0/1), desde la primera toma.

### 5.6.1 Carga: la creatina (`Suplementos.carga`)

La creatina no actúa como una pastilla que sube y baja en horas: se **acumula** en el músculo durante semanas. El modelo representa esa reserva de 0 a 100 %:

$$s = 1 - 0.05^{1/\text{carga\_días}}, \qquad b = 1 - 0.05^{1/\text{lavado\_días}}$$

- Un día con dosis *d*: $\text{nivel} \mathrel{+}= \left(1 - (1-s)^{d/\text{dosis}}\right)\cdot(100 - \text{nivel})$: se llena una fracción de **lo que falta**.
- Un día sin dosis: $\text{nivel} \mathrel{\times}= (1 - b)$: se vacía una fracción de **lo que hay**.

Con 28 y 35 días, *s* = 0.1015 y *b* = 0.0820. El diseño garantiza dos cosas: con la dosis de referencia todos los días, a los **28 días** queda lleno al **95 %** (falta el 5 %); y sin tomarla, a los **35 días** queda el **5 %** de lo que había. Las variables son `supl_creatina_nivel` y `creatina_saturacion_pct`.

**Ejemplo (Figura 5.3).**

- 5 g diarios: día 28 → **95.0 %**.
- Carga de 20 g × 6 días (4 veces la dosis de referencia): cada día se llena 1 − (1 − 0.1015)⁴ = 34.8 % de lo que falta; al día 6 → **92.3 %**.
- 3 g diarios (0.6 veces la referencia): día 28 → **83.4 %**.
- Tras 8 semanas a 5 g (99.8 %) se deja: 30 días después → **7.7 %**; 35 días → **5.0 %**.

**De dónde salen los parámetros.** Hultman et al. (1996) midieron la creatina del músculo en 31 hombres: aumentó alrededor de un **20 %** tras 6 días a 20 g diarios; se mantuvo con 2 g diarios; **30 días después de dejarla ya no difería** del valor inicial; y con **3 g diarios** se alcanzó el mismo 20 % de forma gradual en **28 días**. **[Aproximación]**: los 28 días del modelo vienen de ese protocolo lento y el vaciado de 35 días es compatible con los 30 días medidos (el modelo dice 7.7 % a los 30 días).

![Figura 5.3](figuras/fig27_creatina.png)

*Figura 5.3. La reserva de creatina del modelo con tres formas de tomarla y al dejarla. Script: `figuras/fig27_creatina.py`.*

**Cuándo falla** (H-4-6):

- **La dosis de referencia define la velocidad.** Hultman et al. llenaron la reserva con **3 g** en 28 días; con la referencia de 5 g de la app, 3 g diarios dan solo 83 % al día 28. El modelo dice que se llena más lento de lo que dice el estudio en que se basa.
- **El 100 % es del modelo**, no una concentración: no considera la creatina de la dieta (carne, pescado), el punto de partida de cada persona ni que hay personas que casi no responden a la suplementación.
- **Interacción con los experimentos**: la plantilla de experimento de creatina usa 28 días de lavado, que en su diseño elimina todos los días con creatina (capítulo 8, H-7-1).

### 5.6.2 Vida media (`Suplementos.cantidadEn`)

La cantidad «en el cuerpo» en un instante es la suma de cada toma multiplicada por 0.5^(horas transcurridas / vida media), **sin fase de absorción**. La variable `supl_<id>_nivel` se evalúa **al final del día lógico** (las 5:00 del día siguiente).

**Los parámetros:**

- **Vitamina D, 360 h (15 días)**: coincide con la vida media de **25(OH)D**, la forma que circula en sangre, de unos 15 días; la vitamina D almacenada en la grasa tiene una vida media de unos 2 meses (Jones, 2008). **[Aproximación]**: el modelo suma «UI en el cuerpo», que no es lo que mide un análisis de sangre. Con 2000 UI diarias, el modelo se estabiliza en 2000 / (1 − 0.5^(24/360)) ≈ **44 000 UI**: una cifra sin interpretación fisiológica directa, útil solo para ver la tendencia.
- **Melatonina, 1 h**: la vida media de la melatonina oral va de unos 28 a 126 minutos según la revisión de Harpsøe et al. (2015). **[Validado]** como orden de magnitud. Pero si la tomas a las 23:00, a las 5:00 han pasado 6 vidas medias y queda el 1.6 %: **`supl_melatonina_nivel` es casi siempre ≈ 0** y no sirve para el análisis (H-4-7); la variable útil es `supl_melatonina_tomado`.
- **Magnesio, 12 h; omega-3, 60 h**: no encontré en esta sesión una fuente que respalde esos valores **[sin verificar]**. El magnesio no se comporta como un fármaco con una vida media única (el cuerpo regula sus reservas), y los omega-3 se incorporan a las membranas durante semanas.

### 5.6.3 Exposición: los probióticos (`Suplementos.probioticosEstimados`)

No hay farmacocinética: solo se cuentan **porciones**. A lo que anotes se suma **una porción por cada alimento de MacroFactor cuyo nombre contenga** alguna de las palabras clave que configures (por ejemplo, «yogur» o «kéfir»; comparación sin acentos ni mayúsculas). Variables: `probioticos_porciones` (anotadas + estimadas) y `probioticos_estimado` (solo las estimadas). **[Diseño propio]**: es una cuenta de exposición, sin relación con las unidades formadoras de colonias ni con si el producto tiene cultivos vivos (H-4-8).

---

## 5.7 Ritmo de sueño y jet lag social (`Analisis.ritmo`)

### 5.7.1 Qué hace

La herramienta «Ritmo» de Análisis muestra a qué hora te duermes y despiertas en promedio, cuánto duermes entre semana y en fin de semana, la **regularidad** y el **jet lag social**, con una barra por noche (Figura 5.4). No crea variables del panel; el panel tiene aparte `sueno_inicio_h` y `sueno_fin_h` del reloj.

### 5.7.2 Cómo lo hace exactamente

1. **Noches del reloj**: inicio y fin del sueño en hora decimal; si el inicio es antes de las 12:00, se le suman 24 (dormirte a la 1:00 cuenta como 25.0, para promediarlo bien con las 23:00); al fin se le suman 24 siempre. La noche se guarda con **el día en que despiertas**.
2. **Noches del teléfono** (si faltan del reloj): inicio = **último uso del teléfono** del día (debe ser **a las 18:00 o después**); fin = **primer uso** del día siguiente (a las **4:00 o después**).
3. Si hay **7 o más** noches del reloj, se usan solo esas; si no, se completan con las del teléfono en las fechas sin reloj.
4. Se descartan las noches de menos de **2 h** o más de **16 h**. Hacen falta al menos **5** noches.
5. **Días libres** = las noches que terminan en **sábado o domingo** (es decir, la noche del viernes y la del sábado). Las demás son laborales.
6. **Punto medio** de cada noche = (inicio + fin)/2. **MSW** = promedio de los puntos medios laborales; **MSF** = de los libres (cada uno con al menos 2 noches).
7. **Jet lag social** = |MSF − MSW|.
8. **Regularidad** = desviación estándar de la hora de dormirte (en la pantalla, «la hora de dormir varía ±*N* min»).
9. Duraciones medias en laborales y libres.

### 5.7.3 Razonamiento científico

Wittmann et al. (2006) definieron el jet lag social como la discrepancia entre el horario biológico y el social: las personas de cronotipo tardío duermen muy distinto en días laborales y libres, acumulan deuda de sueño entre semana y la compensan en el fin de semana. Lo midieron con el cuestionario de cronotipo de Múnich (MCTQ) y lo asociaron con peor bienestar y más consumo de estimulantes, sobre todo en jóvenes. Roenneberg et al. (2012), en un estudio poblacional grande, encontraron que el jet lag social se asocia con un índice de masa corporal más alto, más allá de la duración del sueño. **[Validado]** como definición y como variable asociada a la salud.

La app dice en la pantalla que «más de 1 hora se ha asociado con peor ánimo y salud». La asociación está respaldada por esos estudios; **el umbral de 1 hora no lo pude verificar** en esta sesión **[sin verificar]**.

Sobre la **regularidad**: existen medidas específicas, como el **Índice de Regularidad del Sueño** (SRI) de Phillips et al. (2017), que compara, minuto a minuto, si estabas dormido o despierto a la misma hora en días consecutivos; en estudiantes, la irregularidad se asoció con un ritmo circadiano retrasado y peor rendimiento académico. La app usa algo más simple: la desviación estándar de la hora de dormirte. **[Aproximación]**.

**Las noches del teléfono** son un proxy: usar el teléfono por última vez no es dormirte. Cuttone et al. (2017) mostraron que, con un modelo estadístico de los eventos de encender y apagar la pantalla, se puede estimar el sueño con buena exactitud (0.89 frente a pulseras de sueño); la app usa una regla mucho más simple. **[Aproximación]**.

### 5.7.4 Ejemplo trabajado (Figura 5.4)

Tres semanas inventadas. Entre semana te duermes hacia las **00:15** y despiertas a las **06:45**; las noches del viernes y el sábado, hacia las **01:45** y las **10:15** (con variación al azar).

- **MSW** = **03:26**; **MSF** = **06:03** → jet lag social = **2.62 h**.
- Duración media: **6.7 h** entre semana y **8.4 h** en fin de semana: la deuda que se paga el sábado.
- Regularidad: la hora de dormir «varía **±53 min**». Ojo: casi toda esa variación es la diferencia **sistemática** entre semana y fin de semana; la desviación estándar no distingue «me duermo cada día a una hora distinta» de «me duermo siempre igual entre semana y siempre igual el fin de semana, pero distinto».

![Figura 5.4](figuras/fig28_jetlag_social.png)

*Figura 5.4. 21 noches sintéticas dibujadas como en la app, con los puntos medios y el jet lag social. Script: `figuras/fig28_jetlag_social.py`.*

### 5.7.5 Cuándo falla (H-4-9 y H-4-10)

- **Sábado y domingo son siempre los días libres.** No se usan el horario de Conocerte, los días festivos, las vacaciones ni los turnos. Si trabajas un sábado o tienes vacaciones, el cálculo los clasifica mal.
- **Toda la historia junta**: los promedios usan **todas** las noches registradas, aunque la gráfica muestre solo las últimas 30. Con años de datos, el jet lag social de hoy se mezcla con el de otras épocas de tu vida.
- **El valor absoluto** pierde la dirección (si en días libres duermes antes o después), aunque casi siempre es después.
- **Reloj y teléfono mezclados** cuando hay menos de 7 noches del reloj: dos medidas distintas en un mismo promedio.
- **Hora de dormir con dos cortes distintos**: aquí y en el panel, las horas antes de las 12:00 suman 24; en los reportes (`Periodo`), las anteriores a las 15:00 (H-5-15).

---

## 5.8 Aire libre y kilómetros

Ambas están descritas con detalle en el capítulo 4. En resumen:

- **`aire_libre_min`** = el mayor entre los minutos con luz de **1000 lux o más** en el sensor del teléfono (`luz_alta_min`) y los minutos caminando, corriendo o en bici. Es un proxy que **subestima** (el teléfono en el bolsillo no ve la luz) y que no distingue un parque de una calle. **[Aproximación]**.
- **`distancia_km`** suma la distancia **en línea recta** entre puntos de ubicación consecutivos, solo si pasaron entre 1 s y 2 h, la velocidad implícita es menor a 70 m/s y la distancia es mayor a 30 m. Es una **cota inferior** del recorrido real (H-3-5). **[Aproximación]**.

---

## 5.9 Otras variables derivadas

- **Fase lunar** (`Luna`, `data/Calculos.kt`): edad de la luna a partir de una luna nueva de referencia (6 ene 2000, 18:14 UTC) y del **mes sinódico medio** de 29.530588853 días, evaluada al mediodía local; iluminación = (1 − cos 2π*f*)/2 (variable `luna_ilum`). **[Validado]** como aproximación astronómica: el mes real varía alrededor de ese promedio, así que la fase puede desviarse algunas horas; no lo cuantifiqué **[sin verificar]**.
- **Enfoque y siestas** (`Enfoque`, `data/Enfoque.kt`): el cronómetro guarda cada bloque como un tramo **sin contar las pausas**, redondeado a minutos; los bloques de **menos de 1 minuto** no se guardan. Con meta y descansos, la meta se reparte en bloques iguales (2 h con 3 descansos = 4 bloques de 30 min). Los tramos anotados después con hora antes de las 5:00 se asignan a la madrugada del día lógico. Variables: `enfoque_min`, `enfoque_<tipo>_min`, `siesta`, `siesta_min`. Son **mediciones** (de tu cronómetro), no modelos.
- **Repetición máxima estimada** (1RM): peso × (1 + repeticiones/30), la **fórmula de Epley**, una de las habituales para estimar el máximo; no verifiqué su fuente original **[sin verificar]**. Se aplica con **tres criterios distintos** (H-4-13): en la exportación y en `vm_fuerza()` de R (`Macrofactor.e1rm`), series de 1 a **12** repeticiones sin calentamientos; en los reportes (`Periodo`), de 1 a **15** repeticiones, incluidos calentamientos; en `TusApps.kt`, sin límite de repeticiones. Las fórmulas de este tipo pierden exactitud con muchas repeticiones, así que la misma serie puede dar «mejor marca» en un lugar y no en otro.
- **Grados de escalada** (`Grados`): los grados de Fontainebleau se convierten a la escala V con una tabla fija (6A = V3, 7A = V6…) y los de YDS a número (5.10a = 10, 5.10b = 10.25…). **[Aproximación]**: las tablas de equivalencia publicadas no coinciden entre sí.

---

## 5.10 Estimaciones, no mediciones

Todo este capítulo comparte tres advertencias:

1. **Parámetros promedio.** Los modelos usan valores promedio de la literatura; tu cuerpo puede estar lejos de ese promedio (la vida media de la cafeína varía entre 1.5 y 9.5 h). Los resultados sirven sobre todo para **compararte contigo mismo** con los mismos parámetros.
2. **Sin incertidumbre visible.** Ninguna pantalla ni variable dice cuánto podría equivocarse la cifra.
3. **Parámetros que reescriben el pasado** (H-4-11). La vida media de la cafeína y los parámetros de los suplementos se pueden cambiar, y al hacerlo **todas** las variables derivadas pasadas se recalculan, sin guardar qué parámetro se usó ni cuándo cambió. Dos exportaciones de fechas distintas pueden no ser comparables (relacionado con H-1-8).

Cuando una variable estimada aparece relacionada con otra en Explorar, la relación es tan buena como el modelo: por ejemplo, `cafeina_al_dormir_mg` es, en la práctica, una **combinación de la cantidad y la hora** de tus cafés. Si se relaciona con dormir peor, eso dice que «tomar más o más tarde» se asocia con dormir peor; no que tu sangre tuviera exactamente esos miligramos.

---

## 5.11 Resumen de decisiones

| Decisión | Clasificación | Respaldo |
|---|---|---|
| Cafeína: absorción en 45 min, vida media 5 h por omisión | Aproximación | Institute of Medicine (2001); Nehlig (2018) |
| Cafeína: vida media ajustable | Diseño propio (bien fundado) | Nehlig (2018) sobre la variabilidad |
| Alcohol: Widmark con β = 0.15 g/L/h | Validado como promedio | Jones (2010) |
| *r* = 0.68 / 0.55 / 0.62; peso 70 kg por omisión | Aproximación / Diseño propio | Watson et al. (1980) como alternativa; Searle (2015) sobre la incertidumbre |
| Bebida estándar de 14 g | Validado (definición de EE. UU.) | NIAAA (s. f.) |
| Nivel subjetivo con interpolación y trapecio | Diseño propio | — |
| Creatina: 28 días para llenar, 35 para vaciar | Aproximación | Hultman et al. (1996) |
| Vitamina D 15 días; melatonina 1 h | Aproximación / Validado (orden de magnitud) | Jones (2008); Harpsøe et al. (2015) |
| Magnesio 12 h; omega-3 60 h | Diseño propio | [sin verificar] |
| Probióticos por palabras clave | Diseño propio | — |
| Jet lag social | Validado (definición) | Wittmann et al. (2006); Roenneberg et al. (2012) |
| Regularidad = desviación estándar de la hora de dormir | Aproximación | Phillips et al. (2017), el SRI como alternativa |
| Noches por uso del teléfono | Aproximación | Cuttone et al. (2017) sobre lo que se puede lograr |
| Aire libre, km | Aproximación | capítulo 4 |
| Fase lunar con el mes sinódico medio | Validado (aproximación astronómica) | — |

---

## 5.12 Resumen del capítulo

1. La cafeína y el alcohol se estiman con modelos farmacocinéticos sencillos y **parámetros promedio bien elegidos** (5 h de vida media y 45 min de absorción; β = 0.15 g/L/h), pero la variación entre personas es enorme: la misma tarde de café puede dejar de 12 a 106 mg al dormir según la vida media.
2. El alcohol usa *r* por sexo y 70 kg por omisión, sin aprovechar la estatura que la app ya conoce, y no muestra su incertidumbre (H-4-4).
3. Las variables de cafeína y alcohol tienen dos problemas de cálculo: lo anotado en la app se ignora los días en que MacroFactor tiene algo (H-4-2), y en el hilo principal sin caché se omite MacroFactor (H-4-3).
4. El modelo de creatina se basa en Hultman et al. (1996), pero su dosis de referencia de 5 g lo hace más lento que el estudio con 3 g (H-4-6). El nivel de melatonina al final del día es casi siempre 0 (H-4-7).
5. El jet lag social está bien definido, pero usa sábado y domingo fijos y toda la historia sin ventana (H-4-9); la regularidad es una desviación estándar simple.
6. Todas estas cifras son **estimaciones**: sirven para compararte contigo mismo con parámetros fijos, y cambiar un parámetro reescribe el pasado (H-4-11).
7. Se identificaron **13 hallazgos** (H-4-1 a H-4-13). No se cambió código.

---

### Referencias citadas en este capítulo

Ver `referencias.md`. Citas: Cuttone et al. (2017); Harpsøe et al. (2015); Hultman et al. (1996); Institute of Medicine (2001); Jones, A. W. (2010); Jones, G. (2008); Nehlig (2018); NIAAA (s. f.); Phillips et al. (2017); Roenneberg et al. (2012); Searle (2015); Watson, Watson y Batt (1980); Wittmann et al. (2006).
