"""Figura 5.4 - Ritmo de sueño y jet lag social como en Analisis.ritmo (ciencia/Analisis.kt): cada noche se guarda con el día en
que despiertas; las noches que terminan en sábado o domingo son «libres»; punto medio = (inicio + fin)/2; jet lag social =
|media de puntos medios libres − media de puntos medios laborales| (Wittmann et al., 2006); regularidad = desviación estándar
de la hora de dormir. 21 noches SINTÉTICAS. Uso: python fig28_jetlag_social.py [salida.png]"""
import sys
from datetime import date, timedelta
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(4)
inicio_dia = date(2026, 3, 2)                       # lunes
noches = []
for k in range(21):
    despierta = inicio_dia + timedelta(days=k)
    libre = despierta.isoweekday() >= 6             # sábado o domingo (Analisis.ritmo: dayOfWeek >= 6)
    ini = (25.75 if libre else 24.25) + rng.normal(0, .35)      # 01:45 o 00:15 (horas desde la medianoche anterior)
    fin = (34.25 if libre else 30.75) + rng.normal(0, .25)      # 10:15 o 06:45 del día siguiente (+24)
    noches.append((despierta, ini, fin, libre))
lab = [(i + f) / 2 for _, i, f, l in noches if not l]; lib = [(i + f) / 2 for _, i, f, l in noches if l]
msw, msf = np.mean(lab), np.mean(lib); sjl = abs(msf - msw); sd = np.std([i for _, i, _, _ in noches], ddof=1)
dur_lab = np.mean([f - i for _, i, f, l in noches if not l]); dur_lib = np.mean([f - i for _, i, f, l in noches if l])
def hhmm(h): h %= 24; return f"{int(h):02d}:{int(round((h % 1) * 60)) % 60:02d}"

fig, ax = plt.subplots(figsize=(10, 5.6), dpi=160)
DIAS = "lun mar mié jue vie sáb dom".split()
for j, (d, i, f, l) in enumerate(noches):
    ax.barh(j, f - i, left=i, height=.7, color=estilo.OCRE if l else estilo.AZUL, alpha=.85)
    ax.plot([(i + f) / 2] * 2, [j - .35, j + .35], color="white", lw=1.5)
    ax.text(17.9, j, f"{DIAS[d.isoweekday() - 1]} {d.day}", ha="right", va="center", fontsize=7)
ax.axvline(msw, color=estilo.AZUL, ls="--", lw=1.2); ax.axvline(msf, color=estilo.OCRE, ls="--", lw=1.2)
ax.annotate("", xy=(msf, 21.6), xytext=(msw, 21.6), arrowprops=dict(arrowstyle="<->", color=estilo.TINTA))
ax.text((msw + msf) / 2, 22.6, f"jet lag social = {sjl:.2f} h", ha="center", va="center", fontsize=9, weight="bold")
ax.text(msw - .1, -1.4, f"punto medio laboral\n{hhmm(msw)}", ha="right", fontsize=7.5, color=estilo.AZUL)
ax.text(msf + .1, -1.4, f"punto medio libre\n{hhmm(msf)}", ha="left", fontsize=7.5, color=estilo.OCRE)
ax.set_xlim(18, 38); ax.set_ylim(-2, 23.3); ax.invert_yaxis(); ax.set_yticks([])
ax.set_xticks(range(18, 39, 2)); ax.set_xticklabels([hhmm(h) for h in range(18, 39, 2)], fontsize=7.5)
ax.set_xlabel("hora (de las 18:00 a las 14:00 del día siguiente, como la gráfica de la app)", fontsize=8)
ax.text(37.8, 10, f"duración media\nlaboral {dur_lab:.1f} h\nlibre {dur_lib:.1f} h\n\nregularidad:\nla hora de dormir\nvaría ±{sd * 60:.0f} min", ha="right", fontsize=8,
        bbox=dict(fc="white", ec=estilo.GRIS_CLARO))
ax.text(17.9, -1.0, "día en que\ndespiertas", ha="right", fontsize=7, color=estilo.GRIS)
fig.suptitle("Figura 5.4  Jet lag social con 21 noches sintéticas", x=.01, ha="left", fontsize=11, weight="bold", color=estilo.TINTA)
fig.text(.01, .01, "Azul: noches antes de un día laboral (despiertas de lunes a viernes). Naranja: noches que terminan en sábado o domingo. La raya blanca es el punto medio.",
         fontsize=7.5, color=estilo.TINTA_2)
fig.tight_layout(rect=(0, .03, 1, .95))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig28_jetlag_social.png")
print(f"MSW={hhmm(msw)} ({msw:.2f}) MSF={hhmm(msf)} ({msf:.2f}) SJL={sjl:.2f} h; sd dormir={sd*60:.0f} min; dur lab {dur_lab:.2f} lib {dur_lib:.2f}; n lab {len(lab)} lib {len(lib)}")
