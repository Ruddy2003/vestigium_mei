"""Figura 2.2 - Día lógico (corte 05:00) con eventos sintéticos de una noche. Uso: python fig03_dia_logico.py [salida.png]"""
import sys
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

ev = [("21:30", 21.5, "cena"), ("23:50", 23.83, "aviso de ánimo"), ("00:40", 24.67, "diario (nota)"),
      ("02:10", 26.17, "última pantalla"), ("04:55", 28.92, "se duerme"), ("05:05", 29.35, "primer uso al despertar")]
fig, ax = plt.subplots(figsize=(10.5, 2.9), dpi=160)
ax.axvspan(5, 29, ymin=.25, ymax=.55, color=estilo.AZUL_CLARO, alpha=.55, lw=0)
ax.axvspan(29, 34, ymin=.25, ymax=.55, color=estilo.REJILLA, lw=0)
ax.axvline(29, color=estilo.TINTA, lw=1.2, ls="--")
ax.text(24, 0.10, "Día lógico lunes 12  (05:00 lun → 04:59 mar)", ha="center", fontsize=9, color=estilo.TINTA, weight="bold")
ax.text(31.5, 0.30, "Día lógico\nmartes 13", ha="center", fontsize=8.5, color=estilo.TINTA_2)
for i, (h, x, t) in enumerate(ev):
    ax.plot(x, 0.40, "o", ms=6, color=estilo.AZUL if x < 29 else estilo.GRIS)
    ha = "right" if h == "04:55" else ("left" if h == "05:05" else "center")
    dx = -0.15 if h == "04:55" else (0.15 if h == "05:05" else 0)
    ax.text(x + dx, 0.62 + 0.22 * (i % 3 if h not in ("04:55", "05:05") else 0), f"{h}\n{t}", ha=ha, fontsize=7.4, color=estilo.TINTA)
ax.set_xlim(19.5, 34); ax.set_ylim(0, 1.45); ax.set_yticks([])
ax.set_xticks([20, 24, 29, 32]); ax.set_xticklabels(["20:00 lun", "00:00 mar", "05:00 mar\n(corte)", "08:00 mar"], fontsize=8)
for s in ["top", "right", "left"]: ax.spines[s].set_visible(False)
ax.set_title("Un evento a las 00:40 del martes pertenece al lunes lógico", loc="left", fontsize=11, weight="bold", color=estilo.TINTA)
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig03_dia_logico.png")
