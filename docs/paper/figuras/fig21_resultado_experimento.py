"""Figura 8.2 - Resultado de un experimento SINTÉTICO calculado como Experimentos.resultado: antes de la entrega 10c
(Welch con 1.96 fijo) y desde la 10c (Estadistica.diferenciaEnDias: t de Welch con el error estándar de cada grupo
corregido por su autocorrelación de orden 1, ρ + (1 + 4ρ)/n, acotada en [0, 0.9]); tamaño del efecto con desviación agrupada: d de Cohen
hasta la entrega 10e y, desde 10e (D.96), g de Hedges = d · (1 − 3 / (4 · (n₁ + n₂) − 9));
y tasa de falsas señales por simulación con los dos métodos.
Uso: python fig21_resultado_experimento.py [salida.png]"""
import sys, math
from functools import lru_cache
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(8)

def en_lavado(plan, lav, d):
    if lav <= 0: return False
    cambios = ([0] if plan[0] else []) + [k * 7 for k in range(1, len(plan)) if plan[k] != plan[k - 1]]
    return any(0 <= d - c < lav for c in cambios)

def ar1(n, phi):
    """Ruido con autocorrelación de orden 1 (cada día se parece al anterior en `phi`), varianza 1."""
    x = np.empty(n); x[0] = rng.normal()
    for i in range(1, n): x[i] = phi * x[i - 1] + math.sqrt(1 - phi ** 2) * rng.normal()
    return x

def diferencia(a, b):
    """Copia de Estadistica.diferencia: Welch con 1.96 fijo."""
    if len(a) < 3 or len(b) < 3: return None
    se = math.sqrt(a.var(ddof=1) / len(a) + b.var(ddof=1) / len(b)); d = a.mean() - b.mean()
    return d, d - 1.96 * se, d + 1.96 * se

# ---- Copia de Estadistica.cuantilT / pT (t de Student exacta, con la beta incompleta) ----
_C = [0.99999999999980993, 676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059,
      12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7]
def _lng(x):
    if x < .5: return math.log(math.pi / abs(math.sin(math.pi * x))) - _lng(1 - x)
    z = x - 1; a = _C[0]; t = z + 7.5
    for i in range(1, 9): a += _C[i] / (z + i)
    return .5 * math.log(2 * math.pi) + (z + .5) * math.log(t) - t + math.log(a)
def _bi(a, b, x):
    if x <= 0: return 0.0
    if x >= 1: return 1.0
    if x > (a + 1) / (a + b + 2): return 1 - _bi(b, a, 1 - x)
    fr = math.exp(_lng(a + b) - _lng(a) - _lng(b) + a * math.log(x) + b * math.log(1 - x)); f = c = 1.0; d = 0.0
    for i in range(401):
        m = i // 2
        num = 1.0 if i == 0 else (m * (b - m) * x / ((a + 2 * m - 1) * (a + 2 * m)) if i % 2 == 0 else -((a + m) * (a + b + m) * x) / ((a + 2 * m) * (a + 2 * m + 1)))
        d = 1 + num * d; d = 1 / (d if abs(d) > 1e-300 else 1e-300); c = 1 + num / c; c = c if abs(c) > 1e-300 else 1e-300
        f *= c * d
        if abs(1 - c * d) < 1e-12: break
    return fr * (f - 1) / a
def p_t(t, gl): g = min(gl, 1e6); return min(1.0, max(0.0, _bi(g / 2, .5, g / (g + t * t))))
@lru_cache(maxsize=None)
def cuantil_t(gl):
    lo, hi = 0.0, 1.0
    while p_t(hi, gl) > .05 and hi < 1e7: hi *= 2
    for _ in range(80):
        m = (lo + hi) / 2
        if p_t(m, gl) > .05: lo = m
        else: hi = m
    return (lo + hi) / 2

def _ar1(dias, v):
    """Copia de Estadistica.ar1: correlación de los pares de días seguidos; None con menos de 10 pares."""
    m = dict(zip(dias, v)); p = [(x, m[d + 1]) for d, x in zip(dias, v) if d + 1 in m]
    if len(p) < 10: return None
    a = np.array(p); r = np.corrcoef(a[:, 0], a[:, 1])[0, 1]
    return None if not np.isfinite(r) else r

def diferencia_10c(da, a, db, b):
    """Copia de Estadistica.diferenciaEnDias (entrega 10c). da, db: número de día de cada valor."""
    if len(a) < 3 or len(b) < 3: return None
    corr = lambda d, v: (lambda r: None if r is None else r + (1 + 4 * r) / len(v))(_ar1(list(d), list(v)))
    ma, mb = a.mean(), b.mean()
    j = sorted(set(zip(list(da), list(a - ma))) | set(zip(list(db), list(b - mb))))
    comun = corr([x for x, _ in j], [y for _, y in j]) or 0.0
    ra = corr(da, a); rb = corr(db, b); ra = comun if ra is None else ra; rb = comun if rb is None else rb
    ra = min(max(ra, 0), .9); rb = min(max(rb, 0), .9)
    nA = max(len(a) * (1 - ra) / (1 + ra), 2); nB = max(len(b) * (1 - rb) / (1 + rb), 2)
    sa = a.var(ddof=1) / len(a) * (1 + ra) / (1 - ra); sb = b.var(ddof=1) / len(b) * (1 + rb) / (1 - rb)
    se = math.sqrt(sa + sb); d = ma - mb
    if se == 0: return d, d, d, ra, rb, 0.0
    gl = min(max((sa + sb) ** 2 / (sa * sa / (nA - 1) + sb * sb / (nB - 1)), 1), nA + nB - 2)
    t = cuantil_t(round(gl, 2))
    return d, d - t * se, d + t * se, ra, rb, gl

def corrida(phi, efecto=0.0, semanas=6, lav=1, mirar=False, nuevo=False):
    plan = [True] * (semanas // 2) + [False] * (semanas - semanas // 2); rng.shuffle(plan)
    if nuevo: plan = sum(([True, False] if rng.random() < .5 else [False, True] for _ in range(semanas // 2)), [])  # sorteo por pares (10c)
    n = semanas * 7; toca = np.repeat(plan, 7); y = ar1(n, phi) + efecto * toca
    idx = np.array([d for d in range(n) if not en_lavado(plan, lav, d)])
    for L in (range(1, len(idx) + 1) if mirar else [len(idx)]):
        ii = idx[:L]
        if nuevo:
            ic, isn = ii[toca[ii]], ii[~toca[ii]]
            r = diferencia_10c(ic, y[ic], isn, y[isn])
        else:
            r = diferencia(y[ii][toca[ii]], y[ii][~toca[ii]])
        if r and (r[1] > 0 or r[2] < 0): return True
    return False

# --- Panel A: un experimento inventado (valencia 1-7), 6 semanas alternas, 1 día de lavado, efecto real de +0.5.
plan = [True, False, False, True, True, False]
rng = np.random.default_rng(5)                                  # semilla elegida para un caso que ilustra bien el problema
n = 42; toca = np.repeat(plan, 7)
y = np.clip(np.round(4.6 + 0.5 * toca + 0.9 * ar1(n, 0.3)), 1, 7)
cuenta = np.array([not en_lavado(plan, 1, d) for d in range(n)])
a_, b_ = y[cuenta & toca], y[cuenta & ~toca]
d, lo, hi = diferencia(a_, b_)
va, vb = a_.var(ddof=1), b_.var(ddof=1); na, nb = len(a_), len(b_)
sp = math.sqrt(((na - 1) * va + (nb - 1) * vb) / (na + nb - 2)); dc = d / sp
J = 1 - 3 / (4 * (na + nb) - 9)                                   # corrección de Hedges (aproximación habitual)
se = math.sqrt(va / na + vb / nb)
gl = (va / na + vb / nb) ** 2 / ((va / na) ** 2 / (na - 1) + (vb / nb) ** 2 / (nb - 1))   # grados de libertad de Welch
from math import isfinite
tq = cuantil_t(round(gl, 2))
rho = 0.3; fac = math.sqrt((1 + rho) / (1 - rho))                # inflación del error con autocorrelación 0.3 (aprox.)
dias = np.arange(n)
n10 = diferencia_10c(dias[cuenta & toca], a_, dias[cuenta & ~toca], b_)   # lo que da la app desde la entrega 10c

fig, (a, b) = plt.subplots(1, 2, figsize=(12, 4.9), dpi=160, gridspec_kw={"width_ratios": [1.25, 1]})
a.scatter(dias[cuenta & toca], y[cuenta & toca] + rng.uniform(-.08, .08, (cuenta & toca).sum()), color=estilo.AZUL, s=22, label="con la acción")
a.scatter(dias[cuenta & ~toca], y[cuenta & ~toca] + rng.uniform(-.08, .08, (cuenta & ~toca).sum()), color=estilo.OCRE_CLARO, s=22, label="sin la acción")
a.scatter(dias[~cuenta], y[~cuenta], facecolor="none", edgecolor=estilo.GRIS_MEDIO, s=22, label="lavado (no cuenta)")
for s, t in enumerate(plan): a.axvspan(s * 7 - .5, s * 7 + 6.5, color=estilo.AZUL if t else estilo.OCRE_CLARO, alpha=.06)
a.axhline(a_.mean(), color=estilo.AZUL, lw=1); a.axhline(b_.mean(), color=estilo.OCRE_CLARO, lw=1)
a.set_ylim(.5, 9.6); a.set_yticks(range(1, 8)); a.set_xlabel("día del experimento", fontsize=8.5); a.set_ylabel("valencia del día", fontsize=8.5); a.tick_params(labelsize=8)
a.legend(fontsize=7.5, loc="lower left", frameon=False, ncol=3)
txt = (f"con: {a_.mean():.2f} (n={na})   sin: {b_.mean():.2f} (n={nb})\n"
       f"diferencia {d:+.2f}\n"
       f"  antes de 10c (1.96):  {lo:+.2f} a {hi:+.2f}\n"
       f"  solo t de Welch ({tq:.2f}): {d - tq * se:+.2f} a {d + tq * se:+.2f}\n"
       f"  1.96 y autocorr. 0.3: {d - 1.96 * se * fac:+.2f} a {d + 1.96 * se * fac:+.2f}\n"
       f"  app desde 10c:        {n10[1]:+.2f} a {n10[2]:+.2f}\n"
       f"    (ρ con {n10[3]:.2f}, sin {n10[4]:.2f}; {n10[5]:.1f} gl)\n"
       f"g de Hedges {dc * J:.2f} (app desde 10e; d de Cohen {dc:.2f})")
a.text(.01, .985, txt, transform=a.transAxes, ha="left", va="top", fontsize=7.6, family="monospace", color=estilo.TINTA,
       bbox=dict(fc="white", ec=estilo.GRIS_CLARO))
a.set_title("Un experimento inventado: 6 semanas alternas, 1 día de lavado, efecto real +0.5", loc="left", fontsize=9.3, color=estilo.TINTA)

rng = np.random.default_rng(8)
N = 3000
casos = [("días\nindependientes", 0.0, False), ("autocorr.\n0.3", 0.3, False), ("autocorr.\n0.5", 0.5, False),
         ("mirar cada día\n(independientes)", 0.0, True), ("mirar cada día\n(autocorr. 0.3)", 0.3, True)]
tasas = [np.mean([corrida(phi, mirar=m) for _ in range(N)]) for _, phi, m in casos]
# Desde la entrega 10c: sorteo por pares, t de Welch con autocorrelación, y mientras dura el plan no se habla de «señal»
# (el resultado es «provisional»): solo cuenta mirar al final.
nuevas = [np.mean([corrida(phi, nuevo=True) for _ in range(N)]) for _, phi, m in casos[:3]]
x = np.arange(len(casos))
b.bar(x - .2, tasas, width=.4, color=estilo.OCRE, label="antes de 10c")
b.bar(x[:3] + .2, nuevas, width=.4, color=estilo.AZUL, label="desde 10c")
for i, t in enumerate(tasas): b.text(i - .2, t + .01, f"{t:.0%}", ha="center", fontsize=7.8)
for i, t in enumerate(nuevas): b.text(i + .2, t + .01, f"{t:.0%}", ha="center", fontsize=7.8, color=estilo.AZUL)
b.text(1.2, max(tasas) * .62, "desde 10c, mientras dura el plan\nel resultado es «provisional» y no\nhabla de «señal»: solo cuenta\nmirar al final", ha="center", fontsize=7.2, color=estilo.AZUL)
b.axhline(.05, color=estilo.TINTA, ls="--", lw=1); b.text(4.45, .057, "5 % prometido", ha="right", fontsize=7.5)
b.legend(fontsize=7.5, frameon=False, loc="upper left")
b.set_xticks(x); b.set_xticklabels([c[0] for c in casos], fontsize=7.3)
b.set_ylabel("experimentos SIN efecto que dan «señal»", fontsize=8.3); b.tick_params(axis="y", labelsize=8); b.set_ylim(0, max(tasas) + .08)
b.set_title(f"Falsas señales del método de la app ({N} simulaciones)", loc="left", fontsize=9.3, color=estilo.TINTA)
fig.suptitle("El resultado de un experimento y qué tan seguido engaña (datos sintéticos)", x=0.01, ha="left", fontsize=11, weight="bold", color=estilo.TINTA)
fig.tight_layout(rect=(0, 0, 1, .93))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig21_resultado_experimento.png")
print(txt); print(dict(zip([c[0].replace("\n", " ") for c in casos], [round(t, 3) for t in tasas]))); print("10c:", [round(t, 3) for t in nuevas])
