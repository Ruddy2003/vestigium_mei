"""Figura 3.3 - A qué días asigna el panel el puntaje de un cuestionario (datos SINTÉTICOS).
Replica Dinamicas.kt («Cuestionarios») antes y después de la entrega 10c (D.62):
- Antes: cada respuesta se copiaba a su día y a los 29 anteriores (ventana de 30 para PSS-4), de la más vieja a la más
  nueva, y cada una SOBRESCRIBÍA lo anterior: un día recibía la respuesta más nueva cuya ventana lo cubría.
- Ahora (Dinamicas.porDiaCuestionario): la ventana es la que pregunta la app (7 días para WHO-5 y PSS-4, 30 para UCLA-3)
  y un día cubierto por dos respuestas se queda con la primera (putIfAbsent), la más cercana después de él.
Uso: python fig10_cuestionarios_ventana.py [salida.png]"""
import sys
import datetime as dt
import matplotlib
MESES = ["ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"]
def fc(d): return f"{d.day:02d} {MESES[d.month - 1]}"
matplotlib.use("Agg")
import matplotlib.pyplot as plt

d0 = dt.date(2026, 3, 1)
resp = [(d0 + dt.timedelta(days=7 * k), v) for k, v in enumerate([4, 6, 9, 5, 7])]   # cada semana, puntajes 0-16

antes = {}
for f, v in sorted(resp):                                  # como el pon() de antes: el último en escribir gana
    for k in range(30):
        antes[f - dt.timedelta(days=k)] = (v, f)

ahora = {}
for f, v in sorted(resp):                                  # porDiaCuestionario: ventana 7, la primera que cubre gana
    for k in range(7):
        ahora.setdefault(f - dt.timedelta(days=k), (v, f))

fig, ax = plt.subplots(figsize=(12, 4.3), dpi=160)
da = sorted(antes)
ax.step([(d - d0).days for d in da], [antes[d][0] for d in da], where="mid", color="#b45309", lw=1.8, ls="--",
        label="antes de la entrega 10c: ventana de 30 días hacia atrás, gana la última respuesta")
dn = sorted(ahora)
ax.step([(d - d0).days for d in dn], [ahora[d][0] for d in dn], where="mid", color="#2a6f97", lw=2.2,
        label="ahora: los 7 días que pregunta cada respuesta (gana la primera que cubre el día)")
for f, v in resp:
    ax.plot((f - d0).days, v, "o", color="#111827", ms=6); ax.text((f - d0).days, v + 0.5, f"responde\n{fc(f)}: {v}", ha="center", fontsize=7.5)
ejemplo = d0 + dt.timedelta(days=2)
ax.annotate(f"{fc(ejemplo)}: antes {antes[ejemplo][0]} (respuesta del {fc(antes[ejemplo][1])});\nahora {ahora[ejemplo][0]} (respuesta del {fc(ahora[ejemplo][1])})",
            xy=((ejemplo - d0).days, ahora[ejemplo][0]), xytext=(-24, 11.5), fontsize=8, arrowprops=dict(arrowstyle="->", color="#374151"))
ticks = list(range(-28, 30, 7))
ax.set_xticks(ticks); ax.set_xticklabels([fc(d0 + dt.timedelta(days=t)) for t in ticks])
ax.set_ylim(0, 14); ax.set_ylabel("PSS-4 (0 a 16)")
ax.legend(frameon=False, fontsize=8.5, loc="upper right")
for s in ["right", "top"]: ax.spines[s].set_visible(False)
ax.set_title("PSS-4 respondido cada semana: a qué días se asigna cada puntaje, antes y ahora", fontsize=10.5)
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig10_cuestionarios_ventana.png", bbox_inches="tight")
print(f"{ejemplo}: antes={antes[ejemplo]}, ahora={ahora[ejemplo]}")
