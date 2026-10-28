"""Figura 2.3 - Estructura del panel diario con datos SINTÉTICOS: valor, cero estructural y dato ausente (null).
Uso: python fig02_estructura_panel.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)
from matplotlib.colors import ListedColormap
from matplotlib.patches import Patch

rng = np.random.default_rng(7)
dias, nd = 30, 30
vars_ = ["valencia", "sueno_horas", "pasos", "pantalla_min", "gasto_total", "n_ansiedad", "gimnasio", "kcal"]
NULL, ZERO, VAL = 0, 1, 2
M = np.full((len(vars_), dias), NULL)

M[0, :] = VAL; M[0, [9, 10, 21]] = NULL                    # diario: faltan 3 días
M[1, 3:] = VAL; M[1, [12, 13]] = NULL                       # reloj desde el día 4
M[2, 3:] = VAL; M[2, [12, 13]] = NULL
M[3, 8:] = VAL                                              # permiso de uso desde el día 9
g = rng.choice(dias, 9, replace=False)                      # gasto: 0 desde el primer gasto y solo en días con diario (ceroDesde, 10c)
M[4, g.min():] = ZERO; M[4, g] = VAL; M[4, [d for d in (9, 10, 21) if d >= g.min()]] = NULL
M[5, 5:] = ZERO; M[5, rng.choice(np.arange(5, dias), 8, replace=False)] = VAL  # cero desde el primer registro
M[6, 14:] = ZERO; M[6, rng.choice(np.arange(14, dias), 7, replace=False)] = VAL
M[7, 18:] = VAL; M[7, [24]] = NULL
for v in (5, 6):                                           # momentos y gimnasio: también solo en días con diario
    for d in (9, 10, 21):
        if M[v, d] == ZERO: M[v, d] = NULL

cmap = ListedColormap([estilo.REJILLA, estilo.AZUL_CLARO, estilo.AZUL])
fig, ax = plt.subplots(figsize=(10.5, 4.6), dpi=160)
ax.imshow(M, cmap=cmap, aspect="auto", interpolation="nearest")
ax.set_xticks(range(0, dias, 5)); ax.set_xticklabels([f"d{i+1}" for i in range(0, dias, 5)], fontsize=8)
ax.set_yticks(range(len(vars_))); ax.set_yticklabels(vars_, fontsize=8.5)
ax.set_xticks(np.arange(-.5, dias, 1), minor=True); ax.set_yticks(np.arange(-.5, len(vars_), 1), minor=True)
ax.grid(which="minor", color="white", lw=1.5); ax.tick_params(which="both", length=0)
for s in ax.spines.values(): s.set_visible(False)
ax.legend(handles=[Patch(fc=estilo.REJILLA, label="null (no hay dato)"), Patch(fc=estilo.AZUL_CLARO, label="0 estructural (no ocurrió)"),
                   Patch(fc=estilo.AZUL, label="valor observado")], loc="upper center", bbox_to_anchor=(.5, -.09), ncol=3, frameon=False, fontsize=8.5)
ax.set_title("Panel diario sintético: tres estados posibles de cada celda", loc="left", fontsize=11.5, weight="bold", color=estilo.TINTA)
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig02_estructura_panel.png")
