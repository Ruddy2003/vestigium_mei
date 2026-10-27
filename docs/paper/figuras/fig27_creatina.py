"""Figura 5.3 - Reserva de creatina con el modelo «carga» de Suplementos.carga (data/Lote3.kt):
día con dosis d: nivel += (1 − (1 − s)^(d/dosis))·(100 − nivel), con s = 1 − 0.05^(1/28);
día sin dosis: nivel ·= (1 − b), con b = 1 − 0.05^(1/35). Datos SINTÉTICOS. Uso: python fig27_creatina.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

CARGA, LAVADO, DOSIS = 28, 35, 5.0
s = 1 - 0.05 ** (1 / CARGA); b = 1 - 0.05 ** (1 / LAVADO)
def serie(dosis_por_dia):
    nivel, out = 0.0, []
    for d in dosis_por_dia:
        nivel = nivel + (1 - (1 - s) ** (d / DOSIS)) * (100 - nivel) if d > 0 else nivel * (1 - b)
        out.append(nivel)
    return np.array(out)

N = 100
mant = [5.0] * 56 + [0.0] * (N - 56)                      # 5 g diarios 8 semanas y se deja
carga = [20.0] * 6 + [5.0] * 50 + [0.0] * (N - 56)          # carga de 20 g × 6 días, luego 5 g
tres = [3.0] * 56 + [0.0] * (N - 56)                        # 3 g diarios (el protocolo lento de Hultman et al., 1996)
dias = np.arange(1, N + 1)
fig, ax = plt.subplots(figsize=(10, 4.8), dpi=160)
for y, nom, c, ls in [(serie(mant), "5 g/día (la dosis de referencia de la app)", "#2a6f97", "-"),
                      (serie(carga), "20 g/día × 6 días, luego 5 g/día", "#6a994e", "--"),
                      (serie(tres), "3 g/día (registrados contra una dosis de referencia de 5 g)", "#bc6c25", ":")]:
    ax.plot(dias, y, color=c, ls=ls, lw=2, label=nom)
ax.axvline(56.5, color="#1f2937", lw=.8); ax.text(57.5, 92, "se deja de tomar", fontsize=8)
ax.axhline(95, color="#9ca3af", lw=.8, ls="--"); ax.text(99, 96.5, "95 %: «lleno» según la app (día 28 con la dosis de referencia)", fontsize=7.5, color="#6b7280", ha="right")
ax.axhline(100 * 0.05 * serie(mant)[55] / 100, color="#9ca3af", lw=.6, ls=":")
ax.annotate("35 días después: 5 % de lo que había", (56 + 35, serie(mant)[56 + 34]), xytext=(70, 35), fontsize=7.5,
            arrowprops=dict(arrowstyle="-", color="#6b7280", lw=.8))
ax.annotate("Hultman et al. (1996): con 3 g/día se\nllega al mismo +20 % en 28 días; tras\ndejarla, a los 30 días ya no difiere del inicio",
            (28, serie(tres)[27]), xytext=(30, 45), fontsize=7.5, color="#374151", arrowprops=dict(arrowstyle="-", color="#bc6c25", lw=.8))
ax.set_xlabel("día", fontsize=8.5); ax.set_ylabel("reserva estimada (0 a 100 %)", fontsize=8.5); ax.set_ylim(0, 105); ax.set_xlim(0, N)
ax.legend(fontsize=8, frameon=False, loc="lower left", bbox_to_anchor=(.13, .02))
fig.suptitle("Figura 5.3  Carga y lavado de creatina con el modelo de la app (datos sintéticos)", x=.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.text(.01, .01, "El 100 % es la saturación del modelo, no una concentración medida. En el músculo, la carga completa equivale a ≈ +20 % de creatina total (Hultman et al., 1996).",
         fontsize=7.5, color="#374151")
fig.tight_layout(rect=(0, .04, 1, .94))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig27_creatina.png")
print(f"s={s:.4f} b={b:.4f}; 5g día28={serie(mant)[27]:.1f}; carga día6={serie(carga)[5]:.1f}; 3g día28={serie(tres)[27]:.1f}; "
      f"5g día56={serie(mant)[55]:.1f} → 30 días después {serie(mant)[85]:.1f}, 35 días después {serie(mant)[90]:.1f}")
