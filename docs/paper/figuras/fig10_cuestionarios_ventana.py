"""Figura 3.3 - A qué días asigna el panel el puntaje de un cuestionario (datos SINTÉTICOS).
Replica Dinamicas.kt («Cuestionarios»): cada respuesta se copia a su día y a los (ventana - 1) días ANTERIORES, con
ventana = 30 para PSS-4 y UCLA-3 y 14 para WHO-5; las respuestas se recorren de la más vieja a la más nueva y cada una
SOBRESCRIBE lo anterior. La app pregunta el PSS-4 por «los últimos 7 días» y lo pide cada 7 días.
Resultado: un día recibe el puntaje de la respuesta más nueva cuya ventana lo cubre, que suele haberse respondido
3-4 semanas después y preguntar por otra semana. Uso: python fig10_cuestionarios_ventana.py [salida.png]"""
import sys
import datetime as dt
import matplotlib
MESES = ["ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"]
def fc(d): return f"{d.day:02d} {MESES[d.month - 1]}"
matplotlib.use("Agg")
import matplotlib.pyplot as plt

VENTANA, PERIODO = 30, 7
d0 = dt.date(2026, 3, 1)
resp = [(d0 + dt.timedelta(days=7 * k), v) for k, v in enumerate([4, 6, 9, 5, 7])]   # cada lunes, puntajes 0-16

panel = {}
for f, v in sorted(resp):                                  # como pon(): el último en escribir gana
    for k in range(VENTANA):
        panel[f - dt.timedelta(days=k)] = (v, f)

def real(dia):
    """La respuesta cuyo periodo de 7 días incluye ese día (lo que el día «debería» tener)."""
    for f, v in resp:
        if f - dt.timedelta(days=PERIODO - 1) <= dia <= f: return v
    return None

dias = sorted(panel)
fig, ax = plt.subplots(figsize=(12, 4.3), dpi=160)
x = [(d - d0).days for d in dias]
ax.step(x, [panel[d][0] for d in dias], where="mid", color="#b45309", lw=2, label="lo que guarda el panel (ventana de 30 días hacia atrás)")
xr = [(d - d0).days for d in dias if real(d) is not None]
ax.step(xr, [real(d) for d in dias if real(d) is not None], where="mid", color="#2a6f97", lw=2, ls="--",
        label="lo que pregunta cada respuesta (los 7 días previos)")
for f, v in resp:
    ax.plot((f - d0).days, v, "o", color="#111827", ms=6); ax.text((f - d0).days, v + 0.5, f"responde\n{fc(f)}: {v}", ha="center", fontsize=7.5)
ejemplo = d0 + dt.timedelta(days=2)
ax.annotate(f"{fc(ejemplo)}: el panel pone {panel[ejemplo][0]}\n(respuesta del {fc(panel[ejemplo][1])});\nlo correcto sería {real(ejemplo)}",
            xy=((ejemplo - d0).days, panel[ejemplo][0]), xytext=(-24, 11.5), fontsize=8, arrowprops=dict(arrowstyle="->", color="#374151"))
ticks = list(range(-28, 30, 7))
ax.set_xticks(ticks); ax.set_xticklabels([fc(d0 + dt.timedelta(days=t)) for t in ticks])
ax.set_ylim(0, 14); ax.set_ylabel("PSS-4 (0 a 16)")
ax.legend(frameon=False, fontsize=8.5, loc="upper right")
for s in ["right", "top"]: ax.spines[s].set_visible(False)
ax.set_title("PSS-4 respondido cada semana: el panel asigna a cada día un puntaje de otra semana", fontsize=10.5)
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig10_cuestionarios_ventana.png", bbox_inches="tight")
print(f"{ejemplo}: panel={panel[ejemplo]}, real={real(ejemplo)}")
