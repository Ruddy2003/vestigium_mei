"""Figura 4.2 - Detección de un lugar nuevo con datos SINTÉTICOS: mismos umbrales que la app
(radio 75 m, 20 min, precisión <= 120 m, punto cada 15 min). Uso: python fig05_deteccion_lugar.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

RADIO, MINUTOS, PREC_MAX = 75.0, 20, 120.0
rng = np.random.default_rng(11)
# Trayecto: llega al sitio, se queda ~1 h. Un punto cada 15 min (como el pulso de la app).
t = np.arange(0, 91, 15)                                  # minutos desde la llegada
base = np.array([[400, 0], [120, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0]], float)[: len(t)]
ruido = rng.normal(0, 18, base.shape); ruido[:2] = 0
pts = base + ruido
prec = np.array([40, 35, 25, 30, 22, 28, 30], float)[: len(t)]

def detectar(pts, t, prec):
    """Replica Lugares.estancia: hacia atrás mientras sigas dentro del radio del ÚLTIMO punto."""
    resultados = []
    for k in range(2, len(t) + 1):
        p, tt, pr = pts[:k], t[:k], prec[:k]
        ok = pr <= PREC_MAX
        p, tt = p[ok], tt[ok]
        if len(p) < 2: resultados.append(None); continue
        i = len(p) - 1
        while i > 0 and np.hypot(*(p[i - 1] - p[-1])) <= RADIO: i -= 1
        tramo = tt[i:]
        resultados.append(tt[-1] if (len(tramo) >= 2 and tt[-1] - tramo[0] >= MINUTOS) else None)
    return resultados

res = detectar(pts, t, prec)
disparo = next((r for r in res if r is not None), None)

fig, (a, b) = plt.subplots(1, 2, figsize=(11, 4.6), dpi=160, gridspec_kw={"width_ratios": [1.1, 1]})
a.set_aspect("equal", adjustable="datalim"); a.set_xlim(-130, 430); a.set_ylim(-200, 200)
a.add_patch(plt.Circle(pts[-1] if disparo is None else pts[list(t).index(disparo)], RADIO, fc=estilo.AZUL_CLARO, ec=estilo.AZUL, alpha=.55))
a.plot(pts[:, 0], pts[:, 1], "-", color=estilo.GRIS_MEDIO, lw=1)
a.scatter(pts[:, 0], pts[:, 1], c=t, cmap="Blues", edgecolor=estilo.TINTA, s=60, zorder=3)
for x, y, m in zip(pts[:, 0], pts[:, 1], t):
    if m in (0, 15):
        a.annotate(f"minuto {m}", (x, y), textcoords="offset points", xytext=(0, 10), ha="center", fontsize=7.5, color=estilo.TINTA_2)
a.annotate("minutos 30 a 90:\nel punto se queda\ndentro del radio", (pts[2, 0], pts[2, 1]), xytext=(110, -120), fontsize=7.5, color=estilo.TINTA_2,
           arrowprops=dict(arrowstyle="-", color=estilo.GRIS_MEDIO, lw=.8))
a.set_xlabel("metros (este)", fontsize=8.5); a.set_ylabel("metros (norte)", fontsize=8.5); a.tick_params(labelsize=8)
a.set_title("Puntos GPS (uno cada 15 min) y radio de 75 m", loc="left", fontsize=9.5, color=estilo.TINTA)
d = [np.hypot(*(p - pts[-1])) for p in pts]
b.axhline(RADIO, color=estilo.OCRE, lw=1.2, ls="--"); b.text(t[0], RADIO + 8, "radio 75 m", fontsize=8, color=estilo.OCRE)
b.plot(t, d, "o-", color=estilo.AZUL); b.set_xlabel("minutos desde el inicio del recorrido", fontsize=8.5)
b.set_ylabel("distancia al último punto (m)", fontsize=8.5); b.tick_params(labelsize=8)
if disparo is not None:
    b.axvline(disparo, color=estilo.TINTA, lw=1); b.text(disparo + 1.5, max(d) * .6, f"minuto {int(disparo)}: 30 min después\nde llegar (2 lecturas no bastan\ncon un punto cada 15 min)\n→ la app pregunta", fontsize=8, color=estilo.TINTA)
b.set_title("Cuánto tiempo llevas dentro del radio", loc="left", fontsize=9.5, color=estilo.TINTA)
fig.suptitle("Detección de un lugar nuevo (datos sintéticos)", x=0.01, ha="left", fontsize=11.5, weight="bold", color=estilo.TINTA)
fig.tight_layout(rect=(0, 0, 1, .93))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig05_deteccion_lugar.png")
print("detección en el minuto", disparo)
