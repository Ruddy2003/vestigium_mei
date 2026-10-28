"""Figura 1.1 - El costo de cruzar todo con todo (cálculo, sin datos).
Izquierda: con p variables hay p(p-1)/2 parejas; si ninguna tuviera relación, con p < 0.05 saldrían por azar 5 % de ellas.
Derecha: la |r| que necesita la mejor pareja para sostenerse, según los días independientes (nEf). Para una sola pregunta
hecha de antemano basta p < 0.05; en la lista, si esa pareja es la única real entre m revisadas, Benjamini-Hochberg con
q < 0.10 le exige p < 0.10/m (Analisis.bh). Se usa la aproximación de Fisher de Estadistica.relaciones:
|r| = tanh(z / sqrt(nEf - 3)), con z el cuantil normal de dos colas. Uso: python fig29_todo_con_todo.py [salida.png]"""
import sys
import numpy as np
from statistics import NormalDist
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

Z = NormalDist().inv_cdf
def rnec(nef, p): return np.tanh(Z(1 - p / 2) / np.sqrt(nef - 3))

fig, (a, b) = plt.subplots(1, 2, figsize=(12, 4.8), dpi=160)

v = np.arange(10, 401)
parejas = v * (v - 1) / 2
a.plot(v, parejas, color=estilo.AZUL, lw=2.2, label="parejas posibles, p(p − 1)/2")
a.plot(v, 0.05 * parejas, color=estilo.OCRE, lw=2.2, label="«relaciones» por azar con p < 0.05")
for k in (50, 150, 300):
    m = k * (k - 1) / 2
    a.plot([k, k], [0.05 * m, m], color=estilo.GRIS_MEDIO, lw=.8, ls=":")
    a.annotate(f"{k} variables: {m:,.0f} parejas;\n≈ {0.05 * m:,.0f} falsas".replace(",", " "), (k, 0.05 * m), (k + 6, 0.05 * m / 6),
               fontsize=8, ha="left")
a.set_yscale("log"); a.set_xlim(10, 400); a.set_ylim(1, 3e5)
a.set_xlabel("variables que se cruzan"); a.set_ylabel("número de parejas (escala logarítmica)")
a.set_title("Cada variable nueva se cruza con todas las demás", fontsize=10)
a.legend(fontsize=8, frameon=False, loc="lower right")

n = np.arange(14, 731)
casos = [(None, 0.05, estilo.AZUL, "-", "una sola pregunta hecha de antemano (p < 0.05)"),
         (100, 0.10 / 100, estilo.VERDE, "-", "la única real entre 100 parejas (q < 0.10)"),
         (2000, 0.10 / 2000, estilo.OCRE, "-", "la única real entre 2 000 parejas"),
         (44850, 0.10 / 44850, estilo.OCRE_OSCURO, "--", "la única real entre 44 850 parejas (300 variables)")]
for m, p, col, ls, et in casos:
    b.plot(n, rnec(n, p), color=col, lw=2.2, ls=ls, label=et)
for nef in (100, 365):
    b.axvline(nef, color=estilo.GRIS_MEDIO, lw=.8, ls=":")
    b.annotate(f"nEf = {nef}: {rnec(nef, .05):.2f} frente a {rnec(nef, .10 / 44850):.2f}", (nef, rnec(nef, .10 / 44850)),
               (nef + 25, rnec(nef, .10 / 44850) + .12), fontsize=8, arrowprops=dict(arrowstyle="->", color=estilo.GRIS))
for y, et, xt, dy in [(.1, "nula/débil", 40, -.035), (.3, "débil/moderada", 725, .01), (.5, "moderada/fuerte", 725, .01)]:
    b.axhline(y, color=estilo.REJILLA, lw=1); b.text(xt, y + dy, et, ha="left" if xt < 100 else "right", fontsize=7, color=estilo.GRIS)
b.set_xlim(14, 730); b.set_ylim(0, 1)
b.set_xlabel("días independientes con ambos datos (nEf)"); b.set_ylabel("|r| que necesita para sostenerse")
b.set_title("Lo mismo se sostiene o no según cuántas cosas se miraron", fontsize=10)
b.legend(fontsize=8, frameon=False, loc="upper right")

for ax in (a, b): ax.spines[["top", "right"]].set_visible(False)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig29_todo_con_todo.png")
for k in (50, 150, 300): print(k, k * (k - 1) // 2, round(0.05 * k * (k - 1) / 2))
for nef in (30, 100, 365, 730): print(nef, [round(float(rnec(nef, p)), 3) for _, p, *_ in casos])
