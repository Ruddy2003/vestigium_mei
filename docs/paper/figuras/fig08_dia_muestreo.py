"""Figura 3.2 - Un día de la app con datos SINTÉTICOS: cuándo pregunta y qué se registra.
Replica Programador.planDelDia (Trabajo.kt): la ventana [ventana_inicio, ventana_fin] se parte en n tramos iguales;
en cada tramo se elige una hora al azar dejando un margen de min(45 min, tramo/4) a cada lado.
Valores por omisión de la app: 3 avisos, ventana 10:00-21:00, aviso de sueño a las 8:00, diario a las 22:00.
El generador aleatorio NO es el de Kotlin (Random(fecha.hashCode())), así que las horas exactas difieren de las de la app;
las reglas (tramos y márgenes) son las mismas.
Uso: python fig08_dia_muestreo.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

N, INI, FIN = 3, 10 * 60, 21 * 60           # minutos desde las 00:00
tramo = (FIN - INI) / N
margen = min(45, tramo / 4)
rng = np.random.default_rng(3)
plan = [INI + i * tramo + margen + rng.random() * (tramo - 2 * margen) for i in range(N)]
# Respuestas inventadas: la primera a los 6 min, la segunda nunca (el aviso caduca a los 60 min), la tercera a los 22 min.
lat = [6, None, 22]

def hm(m):
    m = m % 1440
    return f"{int(m // 60):02d}:{int(m % 60):02d}"

fig, ax = plt.subplots(figsize=(12, 4.4), dpi=160)
ax.set_xlim(5 * 60, 29 * 60); ax.set_ylim(0, 5.35)
ax.axvspan(5 * 60, 29 * 60, ymin=0, ymax=1, color="#f9fafb")
ax.axvspan(INI, FIN, ymin=0.6, ymax=0.9, color=estilo.REJILLA)
ax.text(INI, 2.75, "ventana de avisos 10:00–21:00 (franjas azules = donde puede caer cada aviso)", fontsize=8.5, color=estilo.TINTA_2)
for i in range(N):
    a0 = INI + i * tramo
    ax.axvline(a0, ymin=0.6, ymax=0.9, color=estilo.GRIS_MEDIO, lw=0.8, ls=":")
    ax.axvspan(a0 + margen, a0 + tramo - margen, ymin=0.6, ymax=0.9, color=estilo.AZUL_CLARO)
    ax.text(a0 + tramo / 2, 4.62, f"tramo {i + 1}: puede caer\n{hm(a0 + margen)}–{hm(a0 + tramo - margen)}", ha="center", fontsize=8, color=estilo.TINTA)
for t, l in zip(plan, lat):
    ax.plot([t, t], [3.05, 4.3], color=estilo.AZUL, lw=2)
    ax.plot(t, 4.3, "v", color=estilo.AZUL, ms=8)
    ax.text(t, 3.95, " aviso " + hm(t), ha="left", fontsize=8.5, color=estilo.AZUL)
    if l is None:
        ax.plot([t, t + 60], [2.35, 2.35], color=estilo.OCRE, lw=2)
        ax.text(t + 30, 2.2, "sin respuesta: caduca\na los 60 min", ha="center", va="top", fontsize=8, color=estilo.OCRE)
    else:
        ax.plot(t + l, 2.35, "o", color=estilo.VERDE, ms=7)
        ax.text(t + l, 2.15, f"respuesta\nlatencia {l} min", ha="center", va="top", fontsize=8, color=estilo.VERDE)
# Otros momentos del día
otros = [(8 * 60, "aviso de sueño\n8:00 (dura 3 h)"), (13 * 60 + 10, "registro por\niniciativa propia"), (22 * 60, "aviso del diario\n22:00"),
         (23 * 60 + 15, "cierre del día:\n«cómo estás ahora»")]
for t, txt in otros:
    ax.plot(t, 1.0, "s", color=estilo.TINTA_2, ms=7)
    arriba = t > 23 * 60   # el cierre va arriba de su marca para no pisar el aviso del diario
    ax.text(t, 1.3 if arriba else 0.35, txt, ha="center", fontsize=8, color=estilo.TINTA_2)
ax.axvline(29 * 60 - 1, color=estilo.TINTA, lw=1)
ax.axvline(24 * 60, color=estilo.GRIS_MEDIO, lw=0.8, ls="--"); ax.text(24 * 60 + 5, 1.6, "medianoche", fontsize=8, color=estilo.GRIS)
ticks = list(range(5 * 60, 29 * 60 + 1, 120))
ax.set_xticks(ticks); ax.set_xticklabels([hm(t) for t in ticks])
ax.set_yticks([])
for s in ["left", "right", "top"]: ax.spines[s].set_visible(False)
ax.set_title("Un día lógico (05:00 → 05:00): avisos de ánimo al azar dentro de tramos, y lo demás que se registra", fontsize=10.5)
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig08_dia_muestreo.png", bbox_inches="tight")
print("plan:", [hm(t) for t in plan], "margen", margen, "min; separación mínima posible", 2 * margen, "min")
