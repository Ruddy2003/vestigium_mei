# Apéndice B. Fórmulas

_Las fórmulas esenciales del núcleo estadístico (capítulo 6), con la función de origen. Describe el código de la v91 (la v90, commit `8818d5a`, más la entrega 10e; `SCHEMA_V = 14`). Se conservan las etiquetas B.n de la versión completa, que trae además la media móvil (B.17), las cifras de «Tus números» (B.19) y la ventana de los cuestionarios (B.21). Notación: n = número de días (o semanas) con ambos datos; x̄ = promedio; s² = varianza muestral con n − 1; atanh y tanh son la tangente hiperbólica inversa y directa._

---

### B.1 Correlación de Pearson — `Estadistica.pearson`, función interna `r` de `Estadistica.relaciones`

$$ r=\frac{\sum_{i=1}^{n}(x_i-\bar x)(y_i-\bar y)}{\sqrt{\sum_{i=1}^{n}(x_i-\bar x)^2\ \sum_{i=1}^{n}(y_i-\bar y)^2}} $$

Condiciones: n ≥ 3 (`pearson`); si alguna suma de cuadrados es 0, devuelve `null`.

### B.2 Correlación de Spearman — `Estadistica.spearman`, `Estadistica.rangos`

$$ \rho = r\big(\operatorname{rango}(x),\ \operatorname{rango}(y)\big) $$

Rangos de 1 a n; a un grupo de valores empatados que ocupan los lugares i a j (base 0) se le asigna (i + j)/2 + 1.

### B.3 Resto sin tendencia — `Estadistica.series` (privada); `quitar` en `vm_relaciones`

$$ \text{resto}_i = x_i-\frac{1}{|W_i|}\sum_{j\in W_i}x_j,\qquad W_i=\{\,j:\ |d_j-d_i|\le V,\ x_j\ \text{existe}\,\} $$

d = número de día del calendario (o de semana × 7). V = 10 días (por día) o 35 días (por semana). El propio día está en W_i. Solo si |W_i| ≥ 3; si no, el resto falta.

**Variable lenta:** var(x) > 0, al menos 14 restos (8 por semana) y var(resto)/var(x) < 0.12.

### B.4 Autocorrelación de orden 1 — `Estadistica.autocorr` (privada), `Estadistica.ar1`; `ac1` en `vm_relaciones`

$$ r_1 = r\big(\{x_i\},\{x_{i+1}\}\big)\quad\text{sobre los pares con } d_{i+1}-d_i=\text{paso} $$

paso = 1 (por día) o 7 (por semana). Si hay menos de 10 pares (o la serie no varía), desde 10e r₁ = 0.3 (`Estadistica.AR1_SIN_DATO`, D.95; **[Diseño propio]**); hasta 10e, 0. Antes de usarse se recorta a [−0.5, 0.97]. `Estadistica.ar1(serie, paso)` es la misma autocorrelación sobre una serie con fechas: devuelve `null` con menos de 10 pares; las correlaciones toman entonces 0.3 desde 10e (antes, 0) y la diferencia de medias (B.11a), 0.

### B.5 Número efectivo de días — `Estadistica.relaciones`, `Estadistica.correlacionEf`; en R, `vm_relaciones` y `vm_factores`

$$ n_{Ef}=\operatorname{acotar}\Big(n\,\frac{1-r_a r_b}{1+r_a r_b},\ \min(4,n),\ n\Big) $$

r_a, r_b: autocorrelaciones (B.4) de cada variable, sobre restos o valores según el modo; la que no se puede estimar vale 0.3 desde 10e (antes, 0), en la app y en R (`.vm_ar1_sin_dato`, también en `vm_cruce` y `vm_presion`). Si n_Ef ≤ 3, la pareja no se evalúa. Referencia: Bretherton et al. (1999); Pyper y Peterman (1998).

### B.6 Transformación de Fisher — `Estadistica.ic95`, `Estadistica.relaciones`, `Estadistica.correlacionEf`

$$ z=\operatorname{atanh}(r)=\tfrac12\ln\frac{1+r}{1-r},\qquad EE_z=\frac{1}{\sqrt{N-3}} $$

N = n (en `ic95`) o n_Ef (en `relaciones` y `correlacionEf`). r se recorta a ±0.9999.

### B.7 Intervalo de 95 % de una correlación

$$ IC_{95}=\big[\tanh(z-1.96\,EE_z),\ \tanh(z+1.96\,EE_z)\big] $$

`ic95` exige n ≥ 5.

### B.8 Valor p bilateral — `Analisis.pBilateral`, `Analisis.phi`

$$ p=2\,\big(1-\Phi(|z|/EE_z)\big),\qquad \Phi(u)=\tfrac12\big(1+\operatorname{erf}(u/\sqrt2)\big) $$

erf se calcula con la aproximación 7.1.26 de Abramowitz y Stegun (1964), con error absoluto ≤ 1.5 × 10⁻⁷ (coeficientes en la versión completa, §B.8). El resultado se recorta a [0, 1].

### B.9 Benjamini-Hochberg — `Analisis.bh`; `p.adjust(p, "BH")` en R

Con los m valores p ordenados p₍₁₎ ≤ … ≤ p₍ₘ₎:

$$ q_{(k)}=\min_{j\ge k}\ \min\!\Big(1,\ \frac{m}{j}\,p_{(j)}\Big) $$

Criterio «se sostiene»: q < 0.10 (lista de relaciones, factores, R); q < 0.05 y n ≥ 30 en «Tu año». En `relaciones`, `q` se calcula solo entre parejas no obvias y `qConObvias` entre todas. La lista se ordena, de mayor a menor, por el extremo del intervalo más cercano al cero (lo si lo > 0; −hi si hi < 0; 0 si el intervalo incluye el 0), y luego por |r|.

### B.10 Panel semanal — `Estadistica.semanal`

$$ \bar x_{s}=\frac{1}{|D_s|}\sum_{f\in D_s}x_f,\qquad D_s=\{\text{días de la semana } s \text{ (lunes a domingo) con dato}\},\ |D_s|\ge 3 $$

Para una variable 0/1 es la proporción de días con 1.

### B.11 Diferencia de medias — `Estadistica.diferenciaCompleta`; `.vm_dif_dias` en R

Con n_a, n_b ≥ 3 y autocorrelaciones ρ_a, ρ_b acotadas a [0, 0.9] (0 si no se dan):

$$ d=\bar a-\bar b,\qquad S_a=\frac{s_a^2}{n_a}\cdot\frac{1+\rho_a}{1-\rho_a},\qquad S_b=\frac{s_b^2}{n_b}\cdot\frac{1+\rho_b}{1-\rho_b},\qquad EE=\sqrt{S_a+S_b} $$

$$ n^{*}_a=\max\!\Big(2,\ n_a\frac{1-\rho_a}{1+\rho_a}\Big),\qquad gl=\operatorname{acotar}\Big(\frac{(S_a+S_b)^2}{S_a^2/(n^{*}_a-1)+S_b^2/(n^{*}_b-1)},\ 1,\ n^{*}_a+n^{*}_b-2\Big) $$

$$ IC_{95}=d\pm t_{0.975;\,gl}\,EE,\qquad p=p_T(d/EE,\ gl) $$

Con ρ = 0 es la t de Welch (1947). Si EE = 0, el intervalo es el punto d.

### B.11a Autocorrelación de cada grupo — `Estadistica.ar1Grupos`, `diferenciaEnDias`

$$ \tilde\rho(S)=\hat\rho_1(S)+\frac{1+4\,\hat\rho_1(S)}{|S|} $$

ρ̂₁ es B.4 sobre días seguidos (`ar1`) y |S| el número de días de la serie. ρ_a = ρ̃(a) si a tiene 10 pares de días seguidos; si no, ρ̃ de los dos grupos juntos, cada uno centrado en su media; si tampoco, 0. Después se acota a [0, 0.9] en B.11. La corrección se atribuye a Marriott y Pope (1954) [sin verificar en el original]; se validó por simulación.

**Día de la semana** (`Analisis.porDiaSemana`), con n ≥ 5: x̄ ± t₀.₉₇₅;ₙ₋₁ · s/√n. **Cambio reciente** (`Analisis.cambio`): a = días con hoy − 30 < fecha ≤ hoy; b = días con hoy − 60 < fecha ≤ hoy − 30.

### B.12 Inestabilidad (MSSD) — `Analisis.dinamica`

$$ \text{MSSD}=\frac{1}{|P|}\sum_{(i,i+1)\in P}(x_{i+1}-x_i)^2,\qquad P=\{\text{pares de días consecutivos de calendario con dato}\},\ |P|\ge 3 $$

### B.13 Inercia — `Analisis.dinamica`

$$ \text{inercia}=r\big(\{x_i\},\{x_{i+1}\}\big),\quad (i,i+1)\in P,\ |P|\ge 10 $$

### B.14 z robusto — `Analisis.atipicos`

$$ z_i=\frac{x_i-M}{1.4826\cdot \mathrm{MAD}},\qquad M=x_{(\lfloor n/2\rfloor)},\quad \mathrm{MAD}=|x-M|_{(\lfloor n/2\rfloor)} $$

(posición ⌊n/2⌋ en base 0 de los datos ordenados: para n par, el central superior). Requiere n ≥ 20 y MAD > 0. Atípico si |z| ≥ 3 (2.8 en «Tu año»). En R, la mediana se promedia para n par.

### B.14a Distribución t de Student — `Estadistica.pT`, `Estadistica.cuantilT`

p de dos colas de un valor t con gl grados de libertad (gl real > 0, acotado a 10⁶):

$$ p_T(t,gl)=I_{\,gl/(gl+t^2)}\Big(\frac{gl}{2},\ \frac12\Big) $$

I_x(a, b) es la beta incompleta regularizada, calculada con la fracción continua de Lentz y ln Γ con la aproximación de Lanczos (g = 7). El cuantil t_p (p > 0.5) es el t ≥ 0 con p_T(t, gl) = 2(1 − p), buscado por bisección (100 pasos); para p < 0.5, t_p = −t₁₋ₚ. Comprobado: t₀.₉₇₅ = 12.706 (gl 1), 4.303 (2), 2.571 (5), 2.042 (30).

### B.15 Correlación mínima detectable (potencia)

$$ |r_{\min}|=\tanh\!\Big(\frac{z_{1-\alpha/2}+z_{1-\beta}}{\sqrt{n_{Ef}-3}}\Big) $$

Con α = 0.05 y potencia 1 − β = 0.80: z = 1.96 + 0.84. No está en el código; sirve para interpretar los umbrales de 14, 30 y 90 días.

### B.16 Modelo múltiple — `Analisis.modelo`

Con las k columnas de predictores X y el objetivo y, todos estandarizados (media 0, DE 1) en los n días completos (n ≥ k + 10):

$$ \hat\beta=(X^\top X)^{-1}X^\top y,\qquad s^2=\frac{\sum (y-X\hat\beta)^2}{n-k-1},\qquad EE(\hat\beta_j)=\sqrt{s^2\,[(X^\top X)^{-1}]_{jj}} $$

$$ IC_{95}(\beta_j)=\hat\beta_j\pm t_{0.975;\,n-k-1}\,EE(\hat\beta_j),\qquad R^2=1-\frac{\sum (y-X\hat\beta)^2}{\sum y^2},\qquad R^2_{aj}=1-(1-R^2)\frac{n-1}{n-k-1} $$

La pantalla muestra R²_aj, con 0 como mínimo. Sin intercepto: con todo centrado valdría 0. Los grados n − k − 1 son los que da `lm` con intercepto en `vm_modelo`, así que los β y sus intervalos coinciden. Inversa por Gauss-Jordan con pivoteo parcial; si |pivote| < 10⁻¹⁰, no hay modelo. Con `conAyer`, se agrega como predictor el objetivo del día de calendario anterior.

### B.18 Palabras — `Analisis.palabras`

Para cada palabra w (≥ 3 letras, fuera de `Analisis.VACIAS`: 167 escritas, 146 distintas sin acentos, que es como se comparan) presente en al menos 5 días con nota y valencia:

$$ \text{dif}_w=\overline{\text{valencia}}_{\{\text{días con } w\}}-\overline{\text{valencia}}_{\{\text{días con nota y valencia}\}} $$

### B.20 Cero o faltante — `Estadistica.ceroDesde`

$$ \text{ceroDesde}(F, B)=\{\,f\in B:\ f\ge\min F\,\} $$

F = fechas de la fuente (gastos, impulsos, escalada, entrenamientos, sesiones del reloj); B = días en que la sección se pudo responder (días con diario; para el ejercicio, días con datos del reloj). En esos días «no hubo» = 0; en los demás, faltante. Las variables sociales usan B = días sociales (diario escrito o momento no privado con @; `Dinamicas.diasSociales`).
