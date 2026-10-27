"""Figura 8.2 - Resultado de un experimento SINTÉTICO calculado como Experimentos.resultado y Estadistica.diferencia
(intervalo con 1.96, d de Cohen con desviación agrupada) y tasa de falsas señales por simulación.
Uso: python fig21_resultado_experimento.py [salida.png]"""
import sys, math
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

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

def corrida(phi, efecto=0.0, semanas=6, lav=1, mirar=False):
    plan = [True] * (semanas // 2) + [False] * (semanas - semanas // 2); rng.shuffle(plan)
    n = semanas * 7; toca = np.repeat(plan, 7); y = ar1(n, phi) + efecto * toca
    idx = np.array([d for d in range(n) if not en_lavado(plan, lav, d)])
    for L in (range(1, len(idx) + 1) if mirar else [len(idx)]):
        ii = idx[:L]; r = diferencia(y[ii][toca[ii]], y[ii][~toca[ii]])
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
try:
    from scipy.stats import t as tdist; tq = tdist.ppf(.975, gl)
except Exception:
    tq = 2.03                                                     # aproximación de t(0.975) con ~36 gl si no hay scipy
rho = 0.3; fac = math.sqrt((1 + rho) / (1 - rho))                # inflación del error con autocorrelación 0.3 (aprox.)

fig, (a, b) = plt.subplots(1, 2, figsize=(12, 4.9), dpi=160, gridspec_kw={"width_ratios": [1.25, 1]})
dias = np.arange(n)
a.scatter(dias[cuenta & toca], y[cuenta & toca] + rng.uniform(-.08, .08, (cuenta & toca).sum()), color="#2a6f97", s=22, label="con la acción")
a.scatter(dias[cuenta & ~toca], y[cuenta & ~toca] + rng.uniform(-.08, .08, (cuenta & ~toca).sum()), color="#e09f3e", s=22, label="sin la acción")
a.scatter(dias[~cuenta], y[~cuenta], facecolor="none", edgecolor="#9ca3af", s=22, label="lavado (no cuenta)")
for s, t in enumerate(plan): a.axvspan(s * 7 - .5, s * 7 + 6.5, color="#2a6f97" if t else "#e09f3e", alpha=.06)
a.axhline(a_.mean(), color="#2a6f97", lw=1); a.axhline(b_.mean(), color="#e09f3e", lw=1)
a.set_ylim(.5, 9.6); a.set_yticks(range(1, 8)); a.set_xlabel("día del experimento", fontsize=8.5); a.set_ylabel("valencia del día", fontsize=8.5); a.tick_params(labelsize=8)
a.legend(fontsize=7.5, loc="lower left", frameon=False, ncol=3)
txt = (f"con: {a_.mean():.2f} (n={na})   sin: {b_.mean():.2f} (n={nb})\n"
       f"diferencia {d:+.2f}\n"
       f"  app (1.96):           {lo:+.2f} a {hi:+.2f}\n"
       f"  con t de Welch ({tq:.2f}):  {d - tq * se:+.2f} a {d + tq * se:+.2f}\n"
       f"  con autocorr. 0.3:    {d - 1.96 * se * fac:+.2f} a {d + 1.96 * se * fac:+.2f}\n"
       f"d de Cohen {dc:.2f} (Hedges: {dc * J:.2f})")
a.text(.01, .985, txt, transform=a.transAxes, ha="left", va="top", fontsize=7.6, family="monospace", color="#1f2937",
       bbox=dict(fc="white", ec="#d1d5db"))
a.set_title("Un experimento inventado: 6 semanas alternas, 1 día de lavado, efecto real +0.5", loc="left", fontsize=9.3, color="#1f2937")

rng = np.random.default_rng(8)
N = 3000
casos = [("días\nindependientes", 0.0, False), ("autocorr.\n0.3", 0.3, False), ("autocorr.\n0.5", 0.5, False),
         ("mirar cada día\n(independientes)", 0.0, True), ("mirar cada día\n(autocorr. 0.3)", 0.3, True)]
tasas = [np.mean([corrida(phi, mirar=m) for _ in range(N)]) for _, phi, m in casos]
cols = ["#2a6f97", "#6a994e", "#6a994e", "#bc6c25", "#bc6c25"]
b.bar(range(len(casos)), tasas, color=cols)
for i, t in enumerate(tasas): b.text(i, t + .01, f"{t:.0%}", ha="center", fontsize=8.5)
b.axhline(.05, color="#1f2937", ls="--", lw=1); b.text(4.45, .057, "5 % prometido", ha="right", fontsize=7.5)
b.set_xticks(range(len(casos))); b.set_xticklabels([c[0] for c in casos], fontsize=7.3)
b.set_ylabel("experimentos SIN efecto que dan «señal»", fontsize=8.3); b.tick_params(axis="y", labelsize=8); b.set_ylim(0, max(tasas) + .08)
b.set_title(f"Falsas señales del método de la app ({N} simulaciones)", loc="left", fontsize=9.3, color="#1f2937")
fig.suptitle("Figura 8.2  El resultado de un experimento y qué tan seguido engaña (datos sintéticos)", x=0.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.tight_layout(rect=(0, 0, 1, .93))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig21_resultado_experimento.png")
print(txt); print(dict(zip([c[0].replace("\n", " ") for c in casos], [round(t, 3) for t in tasas])))
