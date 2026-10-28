"""Figura 8.3 - Cómo una tendencia crea una diferencia falsa en un diseño de antes y después, y cómo se comporta la prueba de
tendencia de Experimentos.resultado. Antes de la entrega 10c: diferencia con 1.96 y pendiente contra el día con las dos
condiciones juntas (|b/EE| > 2). Desde la 10c: Estadistica.diferenciaEnDias (t de Welch con autocorrelación), sorteo por
pares y Experimentos.tendencia (valor ~ día + condición, |t| > cuantil de t con n − 3 grados). Datos SINTÉTICOS.
Uso: python fig22_tendencia_diseno.py [salida.png]"""
import sys, math
from functools import lru_cache
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

rng = np.random.default_rng(21)

def en_lavado(plan, lav, d):
    if lav <= 0: return False
    cambios = ([0] if plan[0] else []) + [k * 7 for k in range(1, len(plan)) if plan[k] != plan[k - 1]]
    return any(0 <= d - c < lav for c in cambios)

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

def tendencia_10c(x, c, y):
    """Copia de Experimentos.tendencia: pendiente común del día en valor ~ día + condición; clara si |t| > t(0.975, n − 3)."""
    X = np.column_stack([np.ones(len(x)), x, c]); beta, *_ = np.linalg.lstsq(X, y, rcond=None)
    res = y - X @ beta; s2 = (res ** 2).sum() / (len(y) - 3); cov = s2 * np.linalg.inv(X.T @ X)
    return beta[1], abs(beta[1] / math.sqrt(cov[1, 1])) > cuantil_t(round(len(y) - 3.0, 2))

def evaluar_10c(y, plan, lav):
    """Lo que hace la app desde la 10c: diferencia con t de Welch y autocorrelación, y tendencia dentro de cada condición."""
    toca = np.repeat(plan, 7); idx = np.array([d for d in range(len(y)) if not en_lavado(plan, lav, d)])
    ic, isn = idx[toca[idx]], idx[~toca[idx]]
    r = diferencia_10c(ic, y[ic], isn, y[isn])
    _, clara = tendencia_10c(idx.astype(float), toca[idx].astype(float), y[idx])
    return r[0], (r[1] > 0 or r[2] < 0), clara

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
    d, senal, _ = evaluar(y, plan, lav); _, senal10, _ = evaluar_10c(y, plan, lav)
    ax.scatter(np.arange(n)[toca], y[toca], s=14, color="#2a6f97", label="con la acción")
    ax.scatter(np.arange(n)[~toca], y[~toca], s=14, color="#e09f3e", label="sin la acción")
    ax.plot([0, n - 1], [0, TEND * (n - 1)], color="#6b7280", ls="--", lw=1, label="tendencia (la causa real)")
    ax.axhline(y[toca].mean(), color="#2a6f97", lw=1); ax.axhline(y[~toca].mean(), color="#e09f3e", lw=1)
    ax.set_title(tit, loc="left", fontsize=8.8, color="#1f2937")
    ax.text(.02, .97, f"diferencia {d:+.2f}\nantes de 10c: {'«señal»' if senal else 'sin señal'}\ndesde 10c: {'«señal»' if senal10 else 'sin señal'}",
            transform=ax.transAxes, va="top", fontsize=8.2, color="#1f2937", weight="bold")
    ax.set_xlabel("día", fontsize=8.5); ax.tick_params(labelsize=7.5); ax.legend(fontsize=7, loc="lower right", frameon=False)
ejes[0].set_ylabel("resultado (unidades de desviación diaria)", fontsize=8.3)

N = 2000
def tasa(fn): return np.mean([fn() for _ in range(N)])
def sim(plan_fn, s, lav, tend=0.0, efecto=0.0, que=1, nuevo=False):
    plan = plan_fn(s); n = s * 7; y = tend * np.arange(n) + efecto * np.repeat(plan, 7) + rng.normal(size=n)
    return (evaluar_10c if nuevo else evaluar)(y, plan, lav)[que]
ad = lambda s: [False] * (s // 2) + [True] * (s - s // 2)
grupos = [("«señal» falsa\ncon tendencia:\nantes/después", tasa(lambda: sim(ad, 8, 0, tend=TEND)), tasa(lambda: sim(ad, 8, 0, tend=TEND, nuevo=True))),
          ("«señal» falsa\ncon tendencia:\nalternas", tasa(lambda: sim(plan_libre, 8, 1, tend=TEND)), tasa(lambda: sim(plan_pares, 8, 1, tend=TEND, nuevo=True))),
          ("«ojo: tendencia»\nsin tendencia, efecto\nreal: antes/después", tasa(lambda: sim(ad, 8, 0, efecto=0.8, que=2)), tasa(lambda: sim(ad, 8, 0, efecto=0.8, que=2, nuevo=True))),
          ("«ojo: tendencia»\nsin tendencia, efecto\nreal: alternas", tasa(lambda: sim(plan_libre, 8, 1, efecto=0.8, que=2)), tasa(lambda: sim(plan_pares, 8, 1, efecto=0.8, que=2, nuevo=True)))]
b = ejes[2]
x = np.arange(len(grupos))
b.bar(x - .2, [g[1] for g in grupos], width=.4, color="#bc6c25", label="antes de 10c (alternas: sorteo libre)")
b.bar(x + .2, [g[2] for g in grupos], width=.4, color="#2a6f97", label="desde 10c (alternas: por pares)")
for i, g in enumerate(grupos):
    b.text(i - .2, g[1] + .01, f"{g[1]:.0%}", ha="center", fontsize=7.8); b.text(i + .2, g[2] + .01, f"{g[2]:.0%}", ha="center", fontsize=7.8, color="#2a6f97")
b.axhline(.05, color="#1f2937", ls="--", lw=1)
b.set_xticks(x); b.set_xticklabels([g[0] for g in grupos], fontsize=6.8)
b.axvline(1.5, color="#d1d5db", lw=1)
b.legend(fontsize=7, frameon=False, loc="upper right")
b.set_ylim(0, 1.05); b.tick_params(axis="y", labelsize=7.5)
barras = [(g[0] + " (antes)", g[1]) for g in grupos] + [(g[0] + " (10c)", g[2]) for g in grupos]
b.set_title(f"Qué tan seguido pasa ({N} simulaciones)", loc="left", fontsize=8.8, color="#1f2937")
fig.suptitle("Figura 8.3  La tendencia y el diseño de antes y después (datos sintéticos)", x=0.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.tight_layout(rect=(0, 0, 1, .92))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig22_tendencia_diseno.png")
for n, t in barras: print(n.replace("\n", " "), round(t, 3))
