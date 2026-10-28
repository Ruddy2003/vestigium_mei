# Apéndice B. Fórmulas

_Todas las fórmulas del núcleo estadístico (capítulo 6), con la función de origen. Se escribió sobre la v81 (commit `fa527df`) y está al día con el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`), la versión que describe todo el paper; lo que cambió en el módulo 10 se marca «desde 10c», «desde 10d», «desde 10e» o «desde 10f» y está en el Apéndice D (entrega 10c, D.62 a D.78: la app; entrega 10d, D.79 a D.87: R y la exportación; entrega 10e, D.88 a D.108; entrega 10f, D.109 a D.118). Las tres últimas (B.22 a B.24) no son del capítulo 6: son las fórmulas que agregó la entrega 10f (capítulos 8, 5 y 2). Notación: n = número de días (o semanas) con ambos datos; x̄ = promedio; s² = varianza muestral con n − 1; atanh y tanh son la tangente hiperbólica inversa y directa._

---

## B.1 Correlación de Pearson — `Estadistica.pearson`, función interna `r` de `Estadistica.relaciones`

$$ r=\frac{\sum_{i=1}^{n}(x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_{i=1}^{n}(x_i-\bar x)^2\ \sum_{i=1}^{n}(y_i-\bar y)^2}} $$

Condiciones: n ≥ 3 (`pearson`); si alguna suma de cuadrados es 0, devuelve `null`.

## B.2 Correlación de Spearman — `Estadistica.spearman`, `Estadistica.rangos`

$$ \rho = r\big(\operatorname{rango}(x),\ \operatorname{rango}(y)\big) $$

Rangos de 1 a n; a un grupo de valores empatados que ocupan los lugares i a j (base 0) se le asigna (i + j)/2 + 1.

## B.3 Resto sin tendencia — `Estadistica.series` (privada); `quitar` en `vm_relaciones`

$$ \text{resto}_i = x_i-\frac{1}{|W_i|}\sum_{j\in W_i}x_j,\qquad W_i=\{\,j:\ |d_j-d_i|\le V,\ x_j\ \text{existe}\,\} $$

d = número de día del calendario (o de semana × 7). V = 10 días (por día) o 35 días (por semana). El propio día está en W_i. Solo si |W_i| ≥ 3; si no, el resto falta.

**Variable lenta:** var(x) > 0, al menos 14 restos (8 por semana) y var(resto)/var(x) < 0.12.

## B.4 Autocorrelación de orden 1 — `Estadistica.autocorr` (privada); `ac1` en `vm_relaciones`

$$ r_1 = r\big(\{x_i\},\{x_{i+1}\}\big)\quad\text{sobre los pares con } d_{i+1}-d_i=\text{paso} $$

paso = 1 (por día) o 7 (por semana). Si hay menos de 10 pares (o la serie no varía), desde 10e r₁ = 0.3 (`Estadistica.AR1_SIN_DATO`, D.95; **[Diseño propio]**); hasta 10e, 0. Antes de usarse se recorta a [−0.5, 0.97].

**Desde 10c**, la misma autocorrelación sobre una serie con fechas es pública: `Estadistica.ar1(serie, paso)`, que devuelve `null` con menos de 10 pares. La usan «Cruzar dos variables» y «Todo lo que se mueve con algo», que con `null` toman 0.3 desde 10e (antes, 0), y la diferencia de medias (B.11a), que con `null` sigue tomando 0.

## B.5 Número efectivo de días — `Estadistica.relaciones`; desde 10c, también `Estadistica.correlacionEf`; en R, `vm_relaciones` y `vm_factores`

$$ n_{Ef}=\operatorname{acotar}\Big(n\,\frac{1-r_a r_b}{1+r_a r_b},\ \min(4,n),\ n\Big) $$

r_a, r_b: autocorrelaciones (B.4) de cada variable, sobre restos o valores según el modo; la que no se puede estimar vale 0.3 desde 10e (antes, 0), en la app y en R (`.vm_ar1_sin_dato`, también en `vm_cruce` y `vm_presion`). Si n_Ef ≤ 3, la pareja no se evalúa. Referencia: Bretherton et al. (1999); Pyper y Peterman (1998).

## B.6 Transformación de Fisher — `Estadistica.ic95`, `Estadistica.relaciones`, `Estadistica.correlacionEf`

$$ z=\operatorname{atanh}(r)=\tfrac12\ln\frac{1+r}{1-r},\qquad EE_z=\frac{1}{\sqrt{N-3}} $$

N = n (en `ic95`) o n_Ef (en `relaciones` y, desde 10c, en `correlacionEf`, que usan «Cruzar dos variables» y «Todo lo que se mueve con algo» con la misma fórmula B.5). r se recorta a ±0.9999. Hasta 10c, `factores` usaba N = n con ρ de Spearman.

## B.7 Intervalo de 95 % de una correlación

$$ IC_{95}=\big[\tanh(z-1.96\,EE_z),\ \tanh(z+1.96\,EE_z)\big] $$

`ic95` exige n ≥ 5.

## B.8 Valor p bilateral — `Analisis.pBilateral`, `Analisis.phi`

$$ p=2\,\big(1-\Phi(|z|/EE_z)\big),\qquad \Phi(u)=\tfrac12\big(1+\operatorname{erf}(u/\sqrt2)\big) $$

con la aproximación de Abramowitz y Stegun (1964, 7.1.26), para x = |u|/√2:

$$ \operatorname{erf}(x)\approx 1-(a_1t+a_2t^2+a_3t^3+a_4t^4+a_5t^5)\,e^{-x^2},\quad t=\frac{1}{1+0.3275911\,x} $$

a₁ = 0.254829592, a₂ = −0.284496736, a₃ = 1.421413741, a₄ = −1.453152027, a₅ = 1.061405429; error absoluto ≤ 1.5 × 10⁻⁷. Para u < 0 se usa la simetría. El resultado se recorta a [0, 1].

## B.9 Benjamini-Hochberg — `Analisis.bh`; `p.adjust(p, "BH")` en R

Con los m valores p ordenados p₍₁₎ ≤ … ≤ p₍ₘ₎:

$$ q_{(k)}=\min_{j\ge k}\ \min\!\Big(1,\ \frac{m}{j}\,p_{(j)}\Big) $$

Criterio «se sostiene»: q < 0.10 (lista de relaciones, factores, R); q < 0.05 y n ≥ 30 en «Tu año». En `relaciones`, `q` se calcula solo entre parejas no obvias y `qConObvias` entre todas.

**Orden de la lista** (`relaciones`): primero por

$$ \text{seguro}=\begin{cases}\text{lo}&\text{si lo}>0\\ -\text{hi}&\text{si hi}<0\\ 0&\text{si el intervalo incluye el 0}\end{cases} $$

de mayor a menor; luego por |r|.

**Tarjeta** (`ui/Relacion.kt`): grosor del lazo = (1.2 + 4.5·|r|) dp; punteado y opacidad 0.65 si q ≥ 0.10; «(≈ n_Ef independientes)» si n_Ef < 0.8 n; «con tendencia: r_bruta» si |r_bruta − r| ≥ 0.2.

## B.10 Panel semanal — `Estadistica.semanal`

$$ \bar x_{s}=\frac{1}{|D_s|}\sum_{f\in D_s}x_f,\qquad D_s=\{\text{días de la semana } s \text{ (lunes a domingo) con dato}\},\ |D_s|\ge 3 $$

Para una variable 0/1 es la proporción de días con 1.

## B.11 Diferencia de medias — `Estadistica.diferenciaCompleta` (desde 10c); `.vm_dif_dias` en R (desde 10d)

Con n_a, n_b ≥ 3 y autocorrelaciones ρ_a, ρ_b acotadas a [0, 0.9] (0 si no se dan):

$$ d=\bar a-\bar b,\qquad S_a=\frac{s_a^2}{n_a}\cdot\frac{1+\rho_a}{1-\rho_a},\qquad S_b=\frac{s_b^2}{n_b}\cdot\frac{1+\rho_b}{1-\rho_b},\qquad EE=\sqrt{S_a+S_b} $$

$$ n^{*}_a=\max\!\Big(2,\ n_a\frac{1-\rho_a}{1+\rho_a}\Big),\qquad gl=\operatorname{acotar}\Big(\frac{(S_a+S_b)^2}{S_a^2/(n^{*}_a-1)+S_b^2/(n^{*}_b-1)},\ 1,\ n^{*}_a+n^{*}_b-2\Big) $$

$$ IC_{95}=d\pm t_{0.975;\,gl}\,EE,\qquad p=p_T(d/EE,\ gl) $$

Con ρ = 0 es la t de Welch (1947). Si EE = 0, el intervalo es el punto d. `Estadistica.diferencia(a, b)` devuelve (d, lo, hi). Hasta 10c: IC = d ± 1.96 · √(s_a²/n_a + s_b²/n_b), y en `factores` p = 2(1 − Φ(|d|/EE)).

## B.11a Autocorrelación de cada grupo — `Estadistica.ar1Grupos`, `diferenciaEnDias` (desde 10c)

Para dos grupos de días con fecha:

$$ \tilde\rho(S)=\hat\rho_1(S)+\frac{1+4\,\hat\rho_1(S)}{|S|} $$

donde ρ̂₁ es B.4 sobre días seguidos (`ar1`) y |S| el número de días de la serie. ρ_a = ρ̃(a) si a tiene 10 pares de días seguidos; si no, ρ̃ de los dos grupos juntos, cada uno centrado en su media (un valor por fecha); si tampoco, 0. Después se acota a [0, 0.9] en B.11. La corrección de sesgo es la aproximación de primer orden que se atribuye a Marriott y Pope (1954) [sin verificar en el original]; se validó por simulación (con 30 + 30 días y ρ = 0.5, cobertura del 95 %).

**Día de la semana** (`Analisis.porDiaSemana`), con n ≥ 5 (desde 10c; antes n ≥ 2 y 1.96): x̄ ± t₀.₉₇₅;ₙ₋₁ · s/√n.

**Cambio reciente** (`Analisis.cambio`, con `diferenciaEnDias`): a = días con hoy − 30 < fecha ≤ hoy; b = días con hoy − 60 < fecha ≤ hoy − 30.

**Todo lo que se mueve con algo** (`Analisis.factores`, desde 10c): para una variable 0/1, `diferenciaEnDias` del objetivo (resto sin tendencia B.3, o valor) entre los días con 1 y con 0; para una continua, r de Pearson entre restos (o valores) con N = n_Ef (B.5 y B.6) y el p de B.8.

## B.12 Inestabilidad (MSSD) — `Analisis.dinamica`

$$ \text{MSSD}=\frac{1}{|P|}\sum_{(i,i+1)\in P}(x_{i+1}-x_i)^2,\qquad P=\{\text{pares de días consecutivos de calendario con dato}\},\ |P|\ge 3 $$

## B.13 Inercia — `Analisis.dinamica`

$$ \text{inercia}=r\big(\{x_i\},\{x_{i+1}\}\big),\quad (i,i+1)\in P,\ |P|\ge 10 $$

Otros índices de `dinamica`: % días difíciles = 100 · #{x ≤ 2}/n; racha buena = máximo de días consecutivos con x ≥ 5; racha dura = con x ≤ 3; intradía = promedio de las DE diarias de los registros de ánimo (días con ≥ 2).

## B.14 z robusto — `Analisis.atipicos`

$$ z_i=\frac{x_i-M}{1.4826\cdot \mathrm{MAD}},\qquad M=x_{(\lfloor n/2\rfloor)},\quad \mathrm{MAD}=|x-M|_{(\lfloor n/2\rfloor)} $$

(posición ⌊n/2⌋ en base 0 de los datos ordenados: para n par, el central superior). Requiere n ≥ 20 y MAD > 0. Atípico si |z| ≥ 3 (2.8 en «Tu año»). En R: `(valor − median(valor)) / mad(valor)`, con la mediana promediada para n par.

## B.14a Distribución t de Student — `Estadistica.pT`, `Estadistica.cuantilT` (desde 10c)

p de dos colas de un valor t con gl grados de libertad (gl real > 0, acotado a 10⁶):

$$ p_T(t,gl)=I_{\,gl/(gl+t^2)}\Big(\frac{gl}{2},\ \frac12\Big) $$

I_x(a, b) es la beta incompleta regularizada, calculada con la fracción continua de Lentz (hasta 400 términos, tolerancia 10⁻¹²; si x > (a + 1)/(a + b + 2) se usa I_x(a, b) = 1 − I₁₋ₓ(b, a)) y ln Γ con la aproximación de Lanczos (g = 7, 9 coeficientes). El cuantil t_p (p > 0.5) es el t ≥ 0 con p_T(t, gl) = 2(1 − p), buscado por bisección (100 pasos); para p < 0.5, t_p = −t₁₋ₚ. Comprobado: t₀.₉₇₅ = 12.706 (gl 1), 4.303 (2), 2.571 (5), 2.042 (30).

## B.15 Correlación mínima detectable (potencia)

$$ |r_{\min}|=\tanh\!\Big(\frac{z_{1-\alpha/2}+z_{1-\beta}}{\sqrt{n_{Ef}-3}}\Big) $$

Con α = 0.05 y potencia 1 − β = 0.80: z = 1.96 + 0.84. No está en el código; sirve para interpretar los umbrales de 14, 30 y 90 días (sección 6.21).

## B.16 Modelo múltiple — `Analisis.modelo`

Con las k columnas de predictores X y el objetivo y, todos estandarizados (media 0, DE 1) en los n días completos (n ≥ k + 10):

$$ \hat\beta=(X^\top X)^{-1}X^\top y,\qquad s^2=\frac{\sum (y-X\hat\beta)^2}{n-k-1},\qquad EE(\hat\beta_j)=\sqrt{s^2\,[(X^\top X)^{-1}]_{jj}} $$

$$ IC_{95}(\beta_j)=\hat\beta_j\pm t_{0.975;\,n-k-1}\,EE(\hat\beta_j),\qquad R^2=1-\frac{\sum (y-X\hat\beta)^2}{\sum y^2},\qquad R^2_{aj}=1-(1-R^2)\frac{n-1}{n-k-1} $$

(desde 10c; antes, 1.96 y solo R²). La pantalla muestra R²_aj, con 0 como mínimo.

Sin intercepto: con todo centrado valdría 0. Los grados n − k − 1 son los que da `lm` con intercepto en `vm_modelo` (R, sobre los mismos datos estandarizados), así que los β y sus intervalos coinciden. Inversa por Gauss-Jordan con pivoteo parcial; si |pivote| < 10⁻¹⁰, no hay modelo. Con `conAyer`, se agrega como predictor el objetivo del día de calendario anterior.

## B.17 Media móvil — `Analisis.movil`; `vm_movil` en R

$$ m_d=\frac{1}{|V_d|}\sum_{f\in V_d}x_f,\qquad V_d=\{f\in[d-(w-1),\,d]\ \text{con dato}\},\quad |V_d|\ge\max(2,\lfloor w/3\rfloor) $$

w = 7 o 30 días.

## B.18 Palabras — `Analisis.palabras`

Para cada palabra w (≥ 3 letras, fuera de `Analisis.VACIAS`: 167 escritas, 146 distintas sin acentos, que es como se comparan; la misma lista en `vm_palabras` desde 10d) presente en al menos 5 días con nota y valencia:

$$ \text{dif}_w=\overline{\text{valencia}}_{\{\text{días con } w\}}-\overline{\text{valencia}}_{\{\text{días con nota y valencia}\}} $$

## B.19 Cifras de «Tus números» — `TusNumeros.ultimos`

$$ \bar x=\frac{1}{|U|}\sum_{f\in U}x_f,\qquad U=\text{los últimos 30 días con dato entre hoy}-365\text{ y hoy} $$

Derivadas: km/año = x̄_km · 365; ejercicio/semana = Σ minutos de las sesiones en [última − 27, última] / 4 (desde 10c; antes x̄_ejercicio · 7); fuerza/semana = #entrenos en [último − 27, último] / 4; tazas de café (reportes) = mg de cafeína / 95.

## B.20 Cero o faltante — `Estadistica.ceroDesde` (desde 10c)

$$ \text{ceroDesde}(F, B)=\{\,f\in B:\ f\ge\min F\,\} $$

F = fechas de la fuente (gastos, impulsos, escalada, entrenamientos, sesiones del reloj); B = días en que la sección se pudo responder (días con diario; para el ejercicio, días con datos del reloj). En esos días «no hubo» = 0; en los demás, faltante. Las variables sociales usan B = días sociales (diario escrito o momento no privado con @; `Dinamicas.diasSociales`).

## B.21 Ventana de los cuestionarios — `Dinamicas.porDiaCuestionario` (desde 10c)

Con respuestas (f_k, v_k) ordenadas por fecha y ventana w (7 en WHO-5 y PSS-4, 30 en UCLA-3):

$$ \text{esc}(d)=v_{k^*},\qquad k^*=\min\{\,k:\ 0\le f_k-d\le w-1\,\} $$

(la primera respuesta que cubre el día d). Con w = 0 (TIPI), cada puntaje vale desde su fecha hasta el test siguiente, como mucho 365 días y no más allá de hoy.

## B.22 Criterio sellado de un experimento — `Experimentos.criterio` (desde 10f); `criterio` en `vm_experimento`

Con la diferencia d = media con la acción − media sin ella y su intervalo de 95 % [lo, hi] de B.11 (`diferenciaEnDias`), el sentido sellado s (+1 «debería subir», −1 «debería bajar») y el umbral u ≥ 0 (en las unidades de la medida; 0 si no se puso):

$$ \text{se cumple}\iff d\cdot s\ \ge\ u\ \ \text{y}\ \ \begin{cases}\text{lo}>0&\text{si } s=+1\\ \text{hi}<0&\text{si } s=-1\end{cases} $$

Es decir: la diferencia va en el sentido dicho, mide al menos el umbral y su intervalo no toca el cero **del lado de s**. Sin sentido sellado, sin diferencia (menos de 3 días medidos en una condición) o con el plan en curso (`provisional`), no hay veredicto (`null` en la app, `NA` en R): no se decide a medio camino. La app guarda el umbral en valor absoluto. **[Diseño propio]** la regla «sentido + umbral + intervalo que no toca el cero».

*Ejemplo inventado.* s = +1, u = 0.5: con d = 0.6 e intervalo [0.1, 1.1] se cumple; con d = 0.4 e intervalo [0.1, 0.7], no (no llega al umbral); con u = 0 y d = 0.6 pero intervalo [−0.1, 1.3], tampoco (toca el cero). Con s = −1 y u = 0.5, d = −0.8 e intervalo [−1.4, −0.2] se cumple. (Comprobado con la expresión de `vm_experimento` en R 4.3.3.)

**Huella.** `Experimentos.huella` = SHA-256 (en hexadecimal) del texto UTF-8 que arma `textoSellado`: pregunta, acción, medida, hipótesis, umbral, sentido, diseño, inicio, semanas, plan (como «1» y «0»), lavado y hora del sello, en ese orden y separados por el carácter U+001F. `intacto` = la huella guardada es igual a la que da lo que hay ahora. **[Validado]** que SHA-256 detecta cambios, en el sentido de que es una función hash criptográfica estándar: cambiar una sola letra da otra huella. No impide cambiar lo sellado; solo lo hace visible.

## B.23 Fuerza de referencia — `Macrofactor.fuerzaReferencia`, `Macrofactor.e1rm` (desde 10f)

Para cada serie efectiva de un día (no de calentamiento: el tipo no contiene «Warm»), con peso p > 0 kg y r repeticiones, 1 ≤ r ≤ 12, la 1RM (el peso que se podría levantar una sola vez) estimada con Epley:

$$ \text{e1RM}=\operatorname{redondeo}_{0.1}\Big(p\,\big(1+\tfrac{r}{30}\big)\Big),\qquad \text{fuerza\_ref\_kg}=\max_{\text{series del ejercicio de referencia}}\ \text{e1RM} $$

El ejercicio de referencia es el press inclinado con mancuernas (`Macrofactor.esReferencia`): el nombre, en minúsculas, sin acentos y con todo lo que no sea letra o número cambiado por «_» (`Texto.slug`), contiene «inclin», además «mancuern», «dumbbell» o «db» como palabra suelta, y además «press», «banc» o «bench». Vacío los días sin ese ejercicio. *Ejemplo inventado:* 3 series de 22 kg × 10, 24 kg × 8 y 24 kg × 6 dan 29.3, 30.4 y 28.8 kg; `fuerza_ref_kg` = 30.4. **[Aproximación]** la 1RM de Epley (su fuente original está [sin verificar], capítulo 5); **[Diseño propio]** reconocer el ejercicio por su nombre.

## B.24 Duración del día lógico con cambio de hora — `Fechas.cambioDeHora`, `ui/pantallas/Dia.kt` (desde 10f)

Con a = desfase con UTC (en horas) al empezar el día lógico (`desfaseH(inicioDiaLogico)`) y b = desfase en su último instante (`desfaseH(finDiaLogico − 1 ms)`):

$$ \text{duración (h)} = 24-(b-a) $$

Si a ≠ b, `cambio_horario` = 1 y la pantalla del Día muestra la duración. *Ejemplos inventados:* el día del cambio al horario de verano en Madrid (de UTC+1 a UTC+2) dura 23 h; el de regreso, 25 h; un viaje de la Ciudad de México a Madrid en verano (de UTC−6 a UTC+2), 24 − 8 = 16 h. `desfase_utc_h` de `vm_panel.csv` es otro valor: el desfase a mediodía (`desfaseH` de las 12:00 de la fecha).
