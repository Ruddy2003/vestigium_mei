"""Figura 5.1 - Cafeína en el cuerpo tras dos dosis, con la fórmula de Sustancias.cafeinaEn (data/Sustancias.kt):
C(t) = Σ D · min(1, Δt/45 min) · 0.5^(Δt / T½). Datos SINTÉTICOS. Uso: python fig25_cafeina.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

DOSIS = [(8.0, 95.0), (14.0, 150.0)]       # (hora, mg): un café a las 8:00 y uno grande a las 14:00
DORMIR = 23.5

def cafeina(t, vida):
    total = np.zeros_like(t)
    for h, mg in DOSIS:
        dt = t - h
        m = dt > 0
        total[m] += mg * np.minimum(dt[m] / 0.75, 1) * 0.5 ** (dt[m] / vida)
    return total

t = np.linspace(6, 32, 2000)
fig, ax = plt.subplots(figsize=(10, 4.8), dpi=160)
estilos = {2.5: ("#9ca3af", "--"), 5.0: ("#2a6f97", "-"), 9.5: ("#bc6c25", ":")}
res = {}
for vida, (c, ls) in estilos.items():
    y = cafeina(t, vida)
    etiqueta = f"vida media {vida:g} h" + (" (la de la app por omisión)" if vida == 5 else "")
    ax.plot(t, y, color=c, ls=ls, lw=2.2 if vida == 5 else 1.6, label=etiqueta)
    al_dormir = float(cafeina(np.array([DORMIR]), vida)[0])
    bajo = t[(t > 14.75) & (y < 50)]
    res[vida] = (al_dormir, bajo[0] if len(bajo) else None)
    ax.scatter([DORMIR], [al_dormir], color=c, zorder=3)
ax.axvline(DORMIR, color="#1f2937", lw=.8); ax.text(DORMIR - .15, 212, "te duermes 23:30", fontsize=8, ha="right")
ax.axhline(50, color="#6b7280", lw=.8, ls="--"); ax.text(6.1, 53, "50 mg (umbral que muestra la app)", fontsize=7.5, color="#6b7280")
for h, mg in DOSIS: ax.annotate(f"{mg:.0f} mg", (h, 0), xytext=(h, -28), ha="center", fontsize=8, arrowprops=dict(arrowstyle="->", color="#1f2937"))
ax.set_xticks(range(6, 33, 2)); ax.set_xticklabels([f"{h % 24:02d}:00" for h in range(6, 33, 2)], fontsize=7.5)
ax.set_ylabel("cafeína en el cuerpo (mg, estimada)", fontsize=8.5); ax.set_ylim(-35, 230); ax.set_xlim(6, 32)
ax.legend(fontsize=8, frameon=False, loc="upper right")
fig.suptitle("Figura 5.1  Cafeína en el cuerpo tras dos dosis (datos sintéticos)", x=.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.text(.01, .01, "Rango de vidas medias del informe del Institute of Medicine (2001): media ≈ 5 h, de 1.5 a 9.5 h entre personas. Se muestran 2.5, 5 y 9.5 h.",
         fontsize=7.5, color="#374151")
fig.tight_layout(rect=(0, .04, 1, .94))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig25_cafeina.png")
for v, (a, b) in res.items(): print(f"T½={v}: al dormir {a:.1f} mg; baja de 50 mg a las {b if b is None else f'{int(b)%24:02d}:{int((b%1)*60):02d}'}")
