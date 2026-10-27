"""Figura 8.3 - Cómo una tendencia crea una diferencia falsa en un diseño de antes y después, y cómo la prueba de tendencia
de Experimentos.resultado (pendiente contra el día, |b/EE| > 2) se enciende con un efecto real. Datos SINTÉTICOS.
Uso: python fig16_tendencia_diseno.py [salida.png]"""
import sys, math
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

rng = np.random.default_rng(21)

def en_lavado(plan, lav, d):
    if lav <= 0: return False
    cambios = ([0] if plan[0] else []) + [k * 7 for k in range(1, len(plan)) if plan[k] != plan[k - 1]]
    return any(0 <= d - c < lav for c in cambios)

def evaluar(y, plan, lav):
    """Lo que hace la app: diferencia con 1.96 y pendiente del resultado contra el día (todas las condiciones juntas)."""
    toca = np.repeat(plan, 7); idx = np.array([d for d in range(len(y)) if not en_lavado(plan, lav, d)])
    a, b = y[idx][toca[idx]], y[idx][~toca[idx]]
    se = math.sqrt(a.var(ddof=1) / len(a) + b.var(ddof=1) / len(b)); d = a.mean() - b.mean()
    x = idx.astype(float); yy = y[idx]; mx, my = x.mean(), yy.mean(); sxx = ((x - mx) ** 2).sum()
    bb = ((x - mx) * (yy - my)).sum() / sxx; res = ((yy - (my + bb * (x - mx))) ** 2).sum() / (len(yy) - 2)
    return d, (d - 1.96 * se > 0 or d + 1.96 * se < 0), abs(bb / math.sqrt(res / sxx)) > 2

def plan_libre(s):
    p = [True] * (s // 2) + [False] * (s - s // 2); rng.shuffle(p); return p
def plan_pares(s):
    return sum(([True, False] if rng.random() < .5 else [False, True] for _ in range(s // 2)), [])

SEMILLA_EJEMPLO = 1
TEND = 0.02           # la medida sube 0.02 por día sola (≈ 1.1 puntos en 8 semanas), en unidades de desviación diaria
fig, ejes = plt.subplots(1, 3, figsize=(13, 4.4), dpi=160, gridspec_kw={"width_ratios": [1, 1, 1.05]})
ejemplos = [("Antes y después (8 semanas, sin efecto real)", [False] * 4 + [True] * 4, 0),
            ("Semanas alternas por pares (8 semanas, sin efecto real)", [False, True, True, False, False, True, True, False], 0)]
ej = np.random.default_rng(SEMILLA_EJEMPLO)                       # semilla de un caso típico (la «señal» falsa sale en ~54 %)
for ax, (tit, plan, lav) in zip(ejes[:2], ejemplos):
    n = len(plan) * 7; y = TEND * np.arange(n) + ej.normal(size=n); toca = np.repeat(plan, 7)
    d, senal, _ = evaluar(y, plan, lav)
    ax.scatter(np.arange(n)[toca], y[toca], s=14, color="#2a6f97", label="con la acción")
    ax.scatter(np.arange(n)[~toca], y[~toca], s=14, color="#e09f3e", label="sin la acción")
    ax.plot([0, n - 1], [0, TEND * (n - 1)], color="#6b7280", ls="--", lw=1, label="tendencia (la causa real)")
    ax.axhline(y[toca].mean(), color="#2a6f97", lw=1); ax.axhline(y[~toca].mean(), color="#e09f3e", lw=1)
    ax.set_title(tit, loc="left", fontsize=8.8, color="#1f2937")
    ax.text(.02, .97, f"diferencia {d:+.2f} → {'«señal»' if senal else 'sin señal'}", transform=ax.transAxes, va="top", fontsize=8.5,
            color="#c1121f" if senal else "#1f2937", weight="bold")
    ax.set_xlabel("día", fontsize=8.5); ax.tick_params(labelsize=7.5); ax.legend(fontsize=7, loc="lower right", frameon=False)
ejes[0].set_ylabel("resultado (unidades de desviación diaria)", fontsize=8.3)

N = 3000
def tasa(fn): return np.mean([fn() for _ in range(N)])
def sim(plan_fn, s, lav, tend=0.0, efecto=0.0, que=1):
    plan = plan_fn(s); n = s * 7; y = tend * np.arange(n) + efecto * np.repeat(plan, 7) + rng.normal(size=n)
    return evaluar(y, plan, lav)[que]
ad = lambda s: [False] * (s // 2) + [True] * (s - s // 2)
barras = [("antes/\ndespués", tasa(lambda: sim(ad, 8, 0, tend=TEND))),
          ("alternas\nlibre", tasa(lambda: sim(plan_libre, 8, 1, tend=TEND))),
          ("alternas\npor pares", tasa(lambda: sim(plan_pares, 8, 1, tend=TEND))),
          ("efecto real:\nantes/\ndespués", tasa(lambda: sim(ad, 8, 0, efecto=0.8, que=2))),
          ("efecto real:\nalternas\nlibre", tasa(lambda: sim(plan_libre, 8, 1, efecto=0.8, que=2)))]
c = ["#c1121f", "#e09f3e", "#6a994e", "#8d99ae", "#8d99ae"]
b = ejes[2]
b.bar(range(5), [t for _, t in barras], color=c)
for i, (_, t) in enumerate(barras): b.text(i, t + .01, f"{t:.0%}", ha="center", fontsize=8.3)
b.axhline(.05, color="#1f2937", ls="--", lw=1)
b.set_xticks(range(5)); b.set_xticklabels([n for n, _ in barras], fontsize=7.2)
b.axvline(2.5, color="#d1d5db", lw=1)
b.text(1, .97, "«señal» falsa con tendencia,\nsin efecto real", transform=b.get_xaxis_transform(), ha="center", va="top", fontsize=7.5)
b.text(3.5, .97, "«ojo: tendencia» sin tendencia,\ncon efecto real d = 0.8", transform=b.get_xaxis_transform(), ha="center", va="top", fontsize=7.5)
b.set_ylim(0, 1.05); b.tick_params(axis="y", labelsize=7.5)
b.set_title(f"Qué tan seguido pasa ({N} simulaciones)", loc="left", fontsize=8.8, color="#1f2937")
fig.suptitle("Figura 8.3  La tendencia y el diseño de antes y después (datos sintéticos)", x=0.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.tight_layout(rect=(0, 0, 1, .92))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig16_tendencia_diseno.png")
for n, t in barras: print(n.replace("\n", " "), round(t, 3))
