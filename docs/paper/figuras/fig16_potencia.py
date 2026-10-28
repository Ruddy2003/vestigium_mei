"""Figura 6.6 - Por qué 14, 30 y 90 días: la correlación más pequeña que se alcanza a detectar (cálculo, sin datos).
Para una prueba bilateral con potencia de 80 %: |r_min| = tanh((z_{1-α/2} + z_{0.80}) / sqrt(nEf - 3)), aproximación
de Fisher. Se dibuja con días independientes (nEf = n), con autocorrelación 0.5 en ambas variables
(nEf = n·0.75/1.25 = 0.6 n, fórmula de Estadistica.relaciones) y con un umbral estricto α = 0.001, que es del orden
del que exige Benjamini-Hochberg cuando se revisan miles de pares y casi ninguno es real. Uso: python fig16_potencia.py [salida.png]"""
import sys
import numpy as np
from statistics import NormalDist
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

Z = NormalDist().inv_cdf
def rmin(nef, alfa=.05, pot=.80): return np.tanh((Z(1 - alfa / 2) + Z(pot)) / np.sqrt(nef - 3))
n = np.arange(10, 401)
fig, ax = plt.subplots(figsize=(11, 4.8), dpi=160)
ax.plot(n, rmin(n), color=estilo.AZUL, lw=2.4, label="días independientes, α = 0.05")
ax.plot(n, rmin(0.6 * n), color=estilo.OCRE, lw=2.4, label="autocorrelación 0.5 en ambas (nEf = 0.6 n), α = 0.05")
ax.plot(n, rmin(0.6 * n, alfa=.001), color=estilo.OCRE_OSCURO, lw=1.8, ls="--", label="lo mismo con α = 0.001 (muchas comparaciones)")
for d in (14, 30, 90):
    ax.axvline(d, color=estilo.GRIS_MEDIO, lw=.8, ls=":")
    ax.annotate(f"{d} días: |r| ≥ {rmin(d):.2f}\n(con r₁=0.5: {rmin(0.6 * d):.2f})", (d, rmin(d)), (d + 8, rmin(d) + .07), fontsize=8, bbox=dict(fc="white", ec="none", pad=1),
                arrowprops=dict(arrowstyle="->", color=estilo.GRIS))
for y, et in [(.1, "nula/débil"), (.3, "débil/moderada"), (.5, "moderada/fuerte")]:
    ax.axhline(y, color=estilo.REJILLA, lw=1); ax.text(400, y + .01, et, ha="right", fontsize=7, color=estilo.GRIS)
ax.set_ylim(0, 1); ax.set_xlim(10, 400)
ax.set_xlabel("días con ambos datos (n)"); ax.set_ylabel("|r| más pequeña detectable (potencia 80 %)")
ax.set_title("Con pocos días solo se ven relaciones enormes; con 90 días independientes, ya se ven las moderadas (|r| ≈ 0.3)")
ax.legend(fontsize=8, frameon=False, loc="upper right")
ax.spines[["top", "right"]].set_visible(False)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig16_potencia.png")
for d in (14, 30, 90, 180, 365): print(d, round(float(rmin(d)), 3), round(float(rmin(0.6 * d)), 3), round(float(rmin(0.6 * d, .001)), 3))
