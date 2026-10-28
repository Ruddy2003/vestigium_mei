"""Figura 6.5 - Serie diaria con medias móviles de 7 y 30 días (datos SINTÉTICOS).
Replica Analisis.movil (y vm_movil en R): para cada día d del calendario se promedian los valores de los días
d, d-1, ..., d-(ventana-1) que existan, y el punto solo se dibuja si hay al menos max(2, ventana/3) días con dato
(división entera: 2 días para la de 7, 10 días para la de 30). La ventana es HACIA ATRÁS (no centrada).
Uso: python fig11_medias_moviles.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(11)
N = 200
t = np.arange(N)
base = 4.2 + 0.6 * np.sin(2 * np.pi * t / 120) - 0.004 * t          # cambio lento inventado
val = np.clip(np.round(base + rng.normal(0, 1.0, N)), 1, 7)
falta = rng.random(N) < 0.18                                          # ~18 % de días sin registro
falta[95:112] = True                                                  # un hueco largo (17 días)
y = np.where(falta, np.nan, val)

def movil(y, ventana):
    out = np.full(len(y), np.nan)
    minimo = max(2, ventana // 3)
    for i in range(len(y)):
        v = y[max(0, i - ventana + 1): i + 1]
        v = v[~np.isnan(v)]
        if len(v) >= minimo: out[i] = v.mean()
    return out

m7, m30 = movil(y, 7), movil(y, 30)
fig, ax = plt.subplots(figsize=(12, 4.4), dpi=160)
ax.plot(t, y, "o", ms=3.2, color=estilo.GRIS_MEDIO, label="valencia del día (1 a 7)")
ax.plot(t, m7, color=estilo.AZUL, lw=1.6, label="media móvil de 7 días (mín. 2 días con dato)")
ax.plot(t, m30, color=estilo.OCRE, lw=3, label="media móvil de 30 días (mín. 10 días con dato)")
ax.axvspan(95, 111, color=estilo.OCRE_PALIDO, alpha=.5)
ax.text(103, 6.75, "17 días sin registro: la de 7 se corta enseguida;\nla de 30 sigue mientras su ventana tenga ≥ 10 días\ncon dato y luego también se corta", ha="center", fontsize=8)
ax.set_ylim(0.6, 7.6); ax.set_xlabel("día"); ax.set_ylabel("valencia")
ax.set_title("Medias móviles hacia atrás: la línea de cada día solo mira el pasado, por eso se «retrasa» frente al cambio")
ax.legend(loc="lower left", fontsize=8, frameon=False)
ax.spines[["top", "right"]].set_visible(False)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig11_medias_moviles.png")
