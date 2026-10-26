# Apéndice B. Fórmulas

_Todas las fórmulas del núcleo estadístico (capítulo 6), tal como están en el código de v81 (commit `fa527df`). Cada una lleva la función de origen. Notación: n = número de días (o semanas) con ambos datos; x̄ = promedio; s² = varianza muestral con n − 1; atanh y tanh son la tangente hiperbólica inversa y directa._

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

paso = 1 (por día) o 7 (por semana). Si hay menos de 10 pares, r₁ = 0. Antes de usarse se recorta a [−0.5, 0.97].

## B.5 Número efectivo de días — `Estadistica.relaciones`

$$ n_{Ef}=\operatorname{acotar}\Big(n\,\frac{1-r_a r_b}{1+r_a r_b},\ \min(4,n),\ n\Big) $$

r_a, r_b: autocorrelaciones (B.4) de cada variable, sobre restos o valores según el modo. Si n_Ef ≤ 3, la pareja no se evalúa. Referencia: Bretherton et al. (1999); Pyper y Peterman (1998).

## B.6 Transformación de Fisher — `Estadistica.ic95`, `Estadistica.relaciones`, `Analisis.factores`

$$ z=\operatorname{atanh}(r)=\tfrac12\ln\frac{1+r}{1-r},\qquad EE_z=\frac{1}{\sqrt{N-3}} $$

N = n (en `ic95` y `factores`) o n_Ef (en `relaciones`). r se recorta a ±0.9999 (±0.999 en el p de `factores`).

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

## B.11 Diferencia de medias — `Estadistica.diferencia`

Con n_a, n_b ≥ 3:

$$ d=\bar a-\bar b,\qquad EE=\sqrt{\frac{s_a^2}{n_a}+\frac{s_b^2}{n_b}},\qquad IC_{95}=d\pm1.96\,EE $$

En `factores` (0/1): p = 2(1 − Φ(|d|/EE)), con EE recuperado como (hi − lo)/(2·1.96).

**Día de la semana** (`Analisis.porDiaSemana`), con n ≥ 2: x̄ ± 1.96 · s/√n.

**Cambio reciente** (`Analisis.cambio`): a = días con fecha > hoy − 30; b = días con hoy − 60 < fecha ≤ hoy − 30.

## B.12 Inestabilidad (MSSD) — `Analisis.dinamica`

$$ \text{MSSD}=\frac{1}{|P|}\sum_{(i,i+1)\in P}(x_{i+1}-x_i)^2,\qquad P=\{\text{pares de días consecutivos de calendario con dato}\},\ |P|\ge 3 $$

## B.13 Inercia — `Analisis.dinamica`

$$ \text{inercia}=r\big(\{x_i\},\{x_{i+1}\}\big),\quad (i,i+1)\in P,\ |P|\ge 10 $$

Otros índices de `dinamica`: % días difíciles = 100 · #{x ≤ 2}/n; racha buena = máximo de días consecutivos con x ≥ 5; racha dura = con x ≤ 3; intradía = promedio de las DE diarias de los registros de ánimo (días con ≥ 2).

## B.14 z robusto — `Analisis.atipicos`

$$ z_i=\frac{x_i-M}{1.4826\cdot \mathrm{MAD}},\qquad M=x_{(\lfloor n/2\rfloor)},\quad \mathrm{MAD}=|x-M|_{(\lfloor n/2\rfloor)} $$

(posición ⌊n/2⌋ en base 0 de los datos ordenados: para n par, el central superior). Requiere n ≥ 20 y MAD > 0. Atípico si |z| ≥ 3 (2.8 en «Tu año»). En R: `(valor − median(valor)) / mad(valor)`, con la mediana promediada para n par.

## B.15 Correlación mínima detectable (potencia)

$$ |r_{\min}|=\tanh\!\Big(\frac{z_{1-\alpha/2}+z_{1-\beta}}{\sqrt{n_{Ef}-3}}\Big) $$

Con α = 0.05 y potencia 1 − β = 0.80: z = 1.96 + 0.84. No está en el código; sirve para interpretar los umbrales de 14, 30 y 90 días (sección 6.21).

## B.16 Modelo múltiple — `Analisis.modelo`

Con las k columnas de predictores X y el objetivo y, todos estandarizados (media 0, DE 1) en los n días completos (n ≥ k + 10):

$$ \hat\beta=(X^\top X)^{-1}X^\top y,\qquad s^2=\frac{\sum (y-X\hat\beta)^2}{n-k-1},\qquad EE(\hat\beta_j)=\sqrt{s^2\,[(X^\top X)^{-1}]_{jj}} $$

$$ IC_{95}(\beta_j)=\hat\beta_j\pm1.96\,EE(\hat\beta_j),\qquad R^2=1-\frac{\sum (y-X\hat\beta)^2}{\sum y^2} $$

Sin intercepto. Inversa por Gauss-Jordan con pivoteo parcial; si |pivote| < 10⁻¹⁰, no hay modelo. Con `conAyer`, se agrega como predictor el objetivo del día de calendario anterior.

## B.17 Media móvil — `Analisis.movil`; `vm_movil` en R

$$ m_d=\frac{1}{|V_d|}\sum_{f\in V_d}x_f,\qquad V_d=\{f\in[d-(w-1),\,d]\ \text{con dato}\},\quad |V_d|\ge\max(2,\lfloor w/3\rfloor) $$

w = 7 o 30 días.

## B.18 Palabras — `Analisis.palabras`

Para cada palabra w (≥ 3 letras, fuera de la lista de vacías) presente en al menos 5 días con nota y valencia:

$$ \text{dif}_w=\overline{\text{valencia}}_{\{\text{días con } w\}}-\overline{\text{valencia}}_{\{\text{días con nota y valencia}\}} $$

## B.19 Cifras de «Tus números» — `TusNumeros.ultimos`

$$ \bar x=\frac{1}{|U|}\sum_{f\in U}x_f,\qquad U=\text{los últimos 30 días con dato entre hoy}-365\text{ y hoy} $$

Derivadas: km/año = x̄_km · 365; ejercicio/semana = x̄_ejercicio · 7; fuerza/semana = #entrenos en [último − 27, último] / 4; tazas de café (reportes) = mg de cafeína / 95.
