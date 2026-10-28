"""Figura 1.1 - Lo que vale para un grupo no tiene por qué valer para ti (datos sintéticos).
Seis personas inventadas, 60 días cada una. Entre personas: quien duerme más en promedio está, en promedio, de mejor
ánimo (la recta discontinua une los promedios de cada persona). Dentro de cada persona, la relación día a día es otra:
para la persona resaltada («tú»), las noches en que duerme más que su propio promedio no van con días mejores.
Ilustra el argumento idiográfico del capítulo 1 (Molenaar, 2004; Fisher et al., 2018). Uso: python fig30_dentro_entre.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(11)
medias = np.array([5.9, 6.3, 6.7, 7.1, 7.5, 7.9])        # horas de sueño promedio de cada persona
pendientes = np.array([0.6, 0.4, -0.35, 0.5, 0.1, 0.3])  # relación día a día dentro de cada persona
TU = 2
fig, ax = plt.subplots(figsize=(10.5, 5.2), dpi=160)
prom = []
for i, (mu, b) in enumerate(zip(medias, pendientes)):
    x = rng.normal(mu, 0.7, 60)
    y = 1.0 + 0.55 * mu + b * (x - mu) + rng.normal(0, 0.55, 60)
    y = np.clip(y, 1, 7)
    prom.append((x.mean(), y.mean()))
    if i == TU:
        ax.scatter(x, y, s=18, color=estilo.OCRE, alpha=.75, zorder=3, label="tus días (60)")
        bb, aa = np.polyfit(x, y, 1)
        xs = np.linspace(x.min(), x.max(), 2)
        ax.plot(xs, aa + bb * xs, color=estilo.OCRE, lw=2.2, zorder=4, label=f"dentro de ti: {bb:+.2f} puntos por hora")
        print("pendiente dentro de «tú»:", round(bb, 3))
    else:
        ax.scatter(x, y, s=10, color=estilo.GRIS_MEDIO, alpha=.45, zorder=2, label="días de otras cinco personas" if i == 0 else None)
px, py = np.array(prom).T
ax.scatter(px, py, s=70, color=estilo.AZUL, edgecolor="white", lw=1.5, zorder=5, label="promedio de cada persona")
bb, aa = np.polyfit(px, py, 1)
xs = np.linspace(5.2, 8.6, 2)
ax.plot(xs, aa + bb * xs, color=estilo.AZUL, lw=2, ls="--", zorder=4, label=f"entre personas: {bb:+.2f} puntos por hora")
print("pendiente entre personas:", round(bb, 3))
ax.set_xlabel("horas de sueño de la noche"); ax.set_ylabel("valencia del día (1 a 7)")
ax.set_xlim(4.2, 9.8); ax.set_ylim(1, 7)
ax.set_title("El grupo dice «más sueño, mejor ánimo»; tus propios días dicen otra cosa", fontsize=10)
ax.legend(fontsize=8, frameon=False, loc="lower right")
ax.spines[["top", "right"]].set_visible(False)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig30_dentro_entre.png")
