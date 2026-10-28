"""Figura 3.4 - Cómo los días que faltan sesgan el promedio (datos SINTÉTICOS).
Diez días con su valencia «real» (1-7). Si los tres días malos no se registran (el caso típico: el día malo es el que
menos ganas da de abrir la app), el promedio de lo registrado sube. Con el «registro de un toque» (día difícil: solo
un número) esos días quedan y el promedio vuelve a ser el real.
Uso: python fig09_dia_faltante.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

real = np.array([5, 6, 5, 2, 6, 5, 3, 6, 5, 2])
falta = real <= 3                         # los días malos no se registran
m_real = real.mean(); m_obs = real[~falta].mean()

fig, (a, b) = plt.subplots(1, 2, figsize=(11, 4.2), dpi=160, sharey=True)
dias = np.arange(1, 11)
a.bar(dias[~falta], real[~falta], color=estilo.AZUL, width=0.7, label="registrado")
a.bar(dias[falta], real[falta], color="none", edgecolor=estilo.OCRE, hatch="//", width=0.7, label="no registrado (día malo)")
a.axhline(m_real, color=estilo.TINTA, lw=1.2, ls="--"); a.text(10.55, m_real - 0.25, f"real {m_real:.2f}", va="center", fontsize=9)
a.axhline(m_obs, color=estilo.AZUL, lw=1.2); a.text(10.55, m_obs + 0.25, f"registrado {m_obs:.2f}", va="center", fontsize=9, color=estilo.AZUL)
a.set_title("Sin registro mínimo: el promedio sube", fontsize=10)
a.legend(frameon=False, fontsize=8.5, loc="upper left", ncol=2)
b.bar(dias[~falta], real[~falta], color=estilo.AZUL, width=0.7, label="diario completo")
b.bar(dias[falta], real[falta], color=estilo.AZUL_CLARO, width=0.7, label="solo un número (día difícil)")
b.axhline(m_real, color=estilo.TINTA, lw=1.2, ls="--"); b.text(10.55, m_real + 0.45, f"real = registrado\n{m_real:.2f}", va="center", fontsize=9)
b.set_title("Con el «registro de un toque»: el promedio es el real", fontsize=10)
b.legend(frameon=False, fontsize=8.5, loc="upper left", ncol=2)
for ax in (a, b):
    ax.set_xticks(dias); ax.set_xlabel("día"); ax.set_ylim(0, 8.3); ax.set_xlim(0.4, 13)
    for s in ["right", "top"]: ax.spines[s].set_visible(False)
a.set_ylabel("valencia del día (1 a 7)")
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig09_dia_faltante.png", bbox_inches="tight")
print(f"media real {m_real:.2f}; media de lo registrado {m_obs:.2f}; sesgo {m_obs - m_real:+.2f}")
