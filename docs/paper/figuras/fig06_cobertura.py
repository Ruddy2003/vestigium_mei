"""Figura 4.3 - Mapa de cobertura SINTÉTICO: días con dato (oscuro) y sin dato (claro) por variable, a lo largo de 90 días.
Uso: python fig06_cobertura.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(5)
D = 90
vars_ = ["diario (valencia)", "pasos (reloj)", "sueño (reloj)", "pantalla_min", "ubicación (km)", "clima", "wifi/luz/audio",
         "notificaciones", "canciones (Spotify)", "videos vistos (Takeout)", "WhatsApp", "laptop (Cold Turkey)", "nutrición (MacroFactor)"]
M = np.zeros((len(vars_), D), bool)
M[0] = rng.random(D) > .08
M[1] = True; M[1, 30:34] = False                       # reloj sin batería 4 días
M[2] = M[1] & (rng.random(D) > .05)
M[3, 10:] = True; M[3, 55:58] = False                  # permiso de uso desde el día 11
M[4] = rng.random(D) > .03
M[5] = True; M[5, 0:3] = False
M[6] = rng.random(D) > .06; M[6, 60:64] = False        # teléfono apagado
M[7, 20:] = True
M[8] = True; M[8, :15] = False                         # Spotify conectado el día 16
M[9, 0:60] = True                                      # Takeout exportado una vez: cubre hasta el día 60
M[10, 5:40] = True; M[10, 70:] = True                  # dos exportaciones de chat
M[11] = False; M[11, 25:85] = rng.random(60) > .5     # laptop: irregular
M[12, 0:45] = True                                     # nutrición: importada por periodos
cov = M.mean(axis=1) * 100
fig, (ax, bx) = plt.subplots(1, 2, figsize=(11, 5), dpi=160, gridspec_kw={"width_ratios": [4.2, 1], "wspace": .05})
ax.imshow(M, aspect="auto", cmap=matplotlib.colors.ListedColormap([estilo.REJILLA, estilo.AZUL]), interpolation="nearest")
ax.set_yticks(range(len(vars_))); ax.set_yticklabels(vars_, fontsize=8.5)
ax.set_xticks(range(0, D, 15)); ax.set_xticklabels([f"d{i+1}" for i in range(0, D, 15)], fontsize=8)
ax.set_xticks(np.arange(-.5, D, 1), minor=True); ax.set_yticks(np.arange(-.5, len(vars_), 1), minor=True)
ax.grid(which="minor", color="white", lw=.6); ax.tick_params(which="both", length=0)
for s in ax.spines.values(): s.set_visible(False)
bx.barh(range(len(vars_)), cov, color=estilo.AZUL, height=.7); bx.set_ylim(len(vars_) - .5, -.5); bx.set_xlim(0, 100)
bx.set_yticks([]); bx.tick_params(labelsize=8); bx.set_xlabel("% de días con dato", fontsize=8.5)
for i, c in enumerate(cov): bx.text(c + 2, i, f"{c:.0f}%", va="center", fontsize=7.5, color=estilo.TINTA_2)
for s in ["top", "right", "left"]: bx.spines[s].set_visible(False)
fig.suptitle("Cobertura sintética de 90 días: claro = sin dato (null), oscuro = con dato", x=0.01, ha="left", fontsize=11.5, weight="bold", color=estilo.TINTA)
fig.subplots_adjust(left=.2,right=.97,top=.9,bottom=.12)
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig06_cobertura.png")
