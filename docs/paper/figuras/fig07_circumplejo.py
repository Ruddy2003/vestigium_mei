"""Figura 3.1 - Los dos ejes del ánimo con datos SINTÉTICOS.
Izquierda: la cuadrícula de 7 x 7 de la app (CuadriculaAfecto): un toque da valencia (1-7, izquierda a derecha)
y activación (1-7, abajo arriba). Las cuatro esquinas llevan las palabras guía que usa la app.
Derecha: el modelo circumplejo de Russell (1980) como ilustración: emociones conocidas colocadas en un círculo
según agrado y activación. Las posiciones son APROXIMADAS e ilustrativas: la app no coloca sus 25 sentimientos
en el círculo (solo los agrupa en agradables, desagradables y mixtos).
Uso: python fig07_circumplejo.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(7)
# 40 registros inventados de un mes: valencia y activación enteras 1..7.
val = np.clip(np.round(rng.normal(4.6, 1.2, 40)), 1, 7)
act = np.clip(np.round(rng.normal(4.0, 1.4, 40)), 1, 7)

fig, (a, b) = plt.subplots(1, 2, figsize=(11.5, 5.2), dpi=160)

# --- Cuadrícula 7 x 7 ---
for v in range(1, 8):
    for ac in range(1, 8):
        a.add_patch(plt.Rectangle((v - 0.47, ac - 0.47), 0.94, 0.94, fc=estilo.REJILLA, ec="white", lw=1))
cuenta = {}
for v, ac in zip(val, act):
    cuenta[(v, ac)] = cuenta.get((v, ac), 0) + 1
for (v, ac), n in cuenta.items():
    a.add_patch(plt.Rectangle((v - 0.47, ac - 0.47), 0.94, 0.94, fc=estilo.AZUL, alpha=min(0.25 + 0.18 * n, 0.95), ec="white", lw=1))
    a.text(v, ac, str(n), ha="center", va="center", fontsize=8, color="white" if n >= 3 else estilo.TINTA)
a.axhline(4, color=estilo.GRIS, lw=1); a.axvline(4, color=estilo.GRIS, lw=1)
for txt, (x, y, ha, va) in {"tenso, estresado": (0.6, 7.45, "left", "bottom"), "emocionado, alegre": (7.4, 7.45, "right", "bottom"),
                            "triste, apagado": (0.6, 0.45, "left", "top"), "tranquilo, relajado": (7.4, 0.45, "right", "top")}.items():
    a.text(x, y, txt, ha=ha, va=va, fontsize=8.5, color=estilo.TINTA_2, style="italic")
a.set_xlim(0.4, 7.6); a.set_ylim(0.0, 8.0); a.set_xticks(range(1, 8)); a.set_yticks(range(1, 8))
a.set_xlabel("Valencia: qué tan agradable (1 = muy desagradable, 7 = muy agradable)")
a.set_ylabel("Activación: cuánta energía (1 = muy baja, 7 = muy alta)")
a.set_title("La cuadrícula de la app (7 × 7)\nnúmeros = cuántos de 40 registros sintéticos cayeron ahí", fontsize=10)
for s in a.spines.values(): s.set_visible(False)

# --- Circumplejo ilustrativo ---
b.set_aspect("equal"); b.add_patch(plt.Circle((0, 0), 1, fc="none", ec=estilo.GRIS_MEDIO, lw=1.2))
b.axhline(0, color=estilo.GRIS_CLARO, lw=1); b.axvline(0, color=estilo.GRIS_CLARO, lw=1)
emociones = {"entusiasmo": 50, "alegría": 20, "calma": -35, "relajación": -60, "cansancio": -100, "tristeza": -150,
             "aburrimiento": -125, "estrés": 138, "ansiedad": 118, "enojo": 158, "miedo": 100, "sorpresa": 78}
for nombre, ang in emociones.items():
    x, y = np.cos(np.radians(ang)), np.sin(np.radians(ang))
    b.plot(x, y, "o", color=estilo.AZUL, ms=6)
    b.text(x * 1.13, y * 1.13, nombre, ha="center", va="center", fontsize=8.5)
b.text(1.32, 0, "agradable →", va="center", fontsize=9, color=estilo.TINTA_2); b.text(-1.32, 0, "← desagradable", va="center", ha="right", fontsize=9, color=estilo.TINTA_2)
b.text(0, 1.3, "más activación ↑", ha="center", fontsize=9, color=estilo.TINTA_2); b.text(0, -1.33, "menos activación ↓", ha="center", fontsize=9, color=estilo.TINTA_2)
b.set_xlim(-1.9, 1.9); b.set_ylim(-1.5, 1.5); b.axis("off")
b.set_title("Idea del circumplejo (Russell, 1980)\nposiciones aproximadas, solo para ilustrar", fontsize=10)

fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig07_circumplejo.png", bbox_inches="tight")
