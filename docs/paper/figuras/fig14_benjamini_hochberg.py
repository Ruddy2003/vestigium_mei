"""Figura 6.4 - Corrección de Benjamini-Hochberg con 200 pruebas simuladas (datos SINTÉTICOS).
180 pares sin relación real y 20 con una relación verdadera (r = 0.35) en n = 90 días independientes.
Se calcula p con Fisher (como Estadistica.relaciones) y q con Analisis.bh: se ordenan los p de menor a mayor y
q_(k) = min_{j>=k} p_(j) * m / j. La app llama «se sostiene» a q < 0.10. Uso: python fig14_benjamini_hochberg.py [salida.png]"""
import sys
import numpy as np
from math import erfc, sqrt
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(14)
N, M0, M1, RHO, Q = 90, 180, 20, 0.35, 0.10
def una(rho):
    x = rng.normal(size=N); y = rho * x + sqrt(1 - rho ** 2) * rng.normal(size=N)
    z = np.arctanh(np.corrcoef(x, y)[0, 1]) * sqrt(N - 3); return erfc(abs(z) / sqrt(2))
p = np.array([una(0) for _ in range(M0)] + [una(RHO) for _ in range(M1)])
real = np.array([False] * M0 + [True] * M1)
m = len(p)
def bh(p):
    orden = np.argsort(p); q = np.empty(m); minimo = 1.0
    for k in range(m - 1, -1, -1):
        i = orden[k]; minimo = min(minimo, p[i] * m / (k + 1)); q[i] = minimo
    return q
q = bh(p)
orden = np.argsort(p); k = np.arange(1, m + 1)
crudo = p < 0.05; corr = q < Q
fig, ax = plt.subplots(1, 2, figsize=(12, 4.6), dpi=160, gridspec_kw={"width_ratios": [1.6, 1]})
ax[0].scatter(k, p[orden], s=14, c=np.where(real[orden], estilo.AZUL, estilo.GRIS_MEDIO), zorder=3)
ax[0].plot(k, Q * k / m, color=estilo.OCRE, lw=2, label=f"línea de BH: {Q} · k / {m}")
ax[0].axhline(.05, color=estilo.TINTA, ls="--", lw=1, label="umbral sin corregir: p < 0.05")
ax[0].set_xlim(0, 60); ax[0].set_ylim(0, .2)
ax[0].set_xlabel("rango k (pares ordenados por p, de menor a mayor; se muestran los primeros 60)"); ax[0].set_ylabel("p")
ax[0].set_title("Azul: relación real · gris: sin relación")
ax[0].legend(fontsize=8, frameon=False, loc="upper left")
filas = [("sin corregir (p < 0.05)", crudo), (f"Benjamini-Hochberg (q < {Q})", corr)]
ax[1].axis("off")
txt = f"{m} pruebas: {M1} reales, {M0} sin relación\n\n"
for nom, s in filas:
    txt += f"{nom}\n  aciertos: {(s & real).sum()} de {M1}\n  falsas alarmas: {(s & ~real).sum()}\n  de lo marcado, falso: {((s & ~real).sum() / max(1, s.sum())):.0%}\n\n"
ax[1].text(0, .95, txt, va="top", fontsize=10, family="monospace")
fig.suptitle("Revisar muchas parejas a la vez: sin corrección, varias «relaciones» son azar; BH limita la proporción de falsas", fontsize=10)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig14_benjamini_hochberg.png")
print(txt)
