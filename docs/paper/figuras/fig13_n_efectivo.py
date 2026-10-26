"""Figura 6.3 - Efecto de la autocorrelación en el número de días efectivos (datos SINTÉTICOS).
Izquierda: la fórmula de Estadistica.relaciones, nEf = n (1 - ra rb) / (1 + ra rb), con ra y rb recortadas a
[-0.5, 0.97] y nEf limitado a [4, n]. Derecha: simulación de 4000 parejas de series AR(1) INDEPENDIENTES entre sí
(no hay relación real) con n = 90 días; se cuenta cuántas veces el intervalo de 95 % por Fisher excluye el cero
usando n (lo ingenuo) y usando nEf (lo que hace la app), con la autocorrelación estimada en cada serie como en
Estadistica.autocorr. Uso: python fig13_n_efectivo.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

def nef(n, ra, rb):
    ra, rb = np.clip(ra, -.5, .97), np.clip(rb, -.5, .97)
    return np.clip(n * (1 - ra * rb) / (1 + ra * rb), min(4, n), n)

rng = np.random.default_rng(13)
def ar1(n, phi):
    x = np.empty(n); x[0] = rng.normal() / np.sqrt(1 - phi ** 2)
    for i in range(1, n): x[i] = phi * x[i - 1] + rng.normal()
    return x
def acf1(x):
    a, b = x[:-1], x[1:]; return np.corrcoef(a, b)[0, 1]

N, REP = 90, 4000
phis = [0.0, 0.2, 0.4, 0.6, 0.8, 0.9]
ingenuo, corregido = [], []
for phi in phis:
    fi = fc = 0
    for _ in range(REP):
        x, y = ar1(N, phi), ar1(N, phi)
        r = np.corrcoef(x, y)[0, 1]; z = np.arctanh(r)
        fi += abs(z) * np.sqrt(N - 3) > 1.96
        ne = nef(N, acf1(x), acf1(y))
        fc += ne > 3 and abs(z) * np.sqrt(ne - 3) > 1.96
    ingenuo.append(fi / REP); corregido.append(fc / REP)

fig, ax = plt.subplots(1, 2, figsize=(12, 4.6), dpi=160)
rr = np.linspace(-0.5, 0.97, 300)
for n, c in [(30, "#9ca3af"), (90, "#2a6f97"), (365, "#111827")]:
    ax[0].plot(rr, nef(n, rr, rr), color=c, lw=2, label=f"n = {n} días")
ax[0].set_xlabel("autocorrelación de un día al siguiente (la misma en ambas variables)")
ax[0].set_ylabel("días efectivos (nEf)"); ax[0].set_yscale("log")
ax[0].set_title("Si hoy se parece a ayer, dos días seguidos no son dos pruebas")
for r1 in (0.5, 0.8):
    ax[0].annotate(f"r₁ = {r1}: 90 días ≈ {nef(90, r1, r1):.0f} efectivos", (r1, nef(90, r1, r1)), (r1 - .75, nef(90, r1, r1) * .55),
                   fontsize=8, arrowprops=dict(arrowstyle="->", color="#6b7280"))
ax[0].legend(fontsize=8, frameon=False)
x = np.arange(len(phis)); w = .38
ax[1].bar(x - w / 2, ingenuo, w, color="#b45309", label="intervalo con n (ingenuo)")
ax[1].bar(x + w / 2, corregido, w, color="#2a6f97", label="intervalo con nEf (la app)")
ax[1].axhline(.05, color="#111827", ls="--", lw=1); ax[1].text(len(phis) - .5, .058, "5 % esperado", ha="right", fontsize=8)
ax[1].set_xticks(x, [str(p) for p in phis]); ax[1].set_xlabel("autocorrelación real de ambas series")
ax[1].set_ylabel("falsas alarmas (sin relación real)")
ax[1].set_title(f"Simulación: {REP} parejas independientes de {N} días")
ax[1].legend(fontsize=8, frameon=False)
for a in ax: a.spines[["top", "right"]].set_visible(False)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig13_n_efectivo.png")
print("phi", phis); print("ingenuo", ingenuo); print("corregido", corregido)
