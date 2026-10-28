"""Figura 6.4 - La tarjeta de relación de «Las relaciones más marcadas» (ejemplos SINTÉTICOS).
Imita ui/Relacion.kt (TarjetaRelacion y lazo, tema por omisión): dos recuadros con las variables, un lazo entre ellos
de grosor (1.2 + 4.5·|r|) dp; verde si r >= 0 («suben juntas»), rojo si r < 0; punteado y con 65 % de opacidad si
q >= 0.10 («podría ser azar»). Debajo, el texto de detalle que arma Explorar.kt. Uso: python fig15_tarjeta_relacion.py [salida.png]"""
import sys
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)
from matplotlib.patches import FancyBboxPatch

VERDE, ROJO = estilo.VERDE, estilo.ROJO
ej = [
    ("Horas de sueño (reloj)", "sueño", "Valencia del día", "ánimo", +0.42, 0.003, "142 días (≈ 98 independientes) · moderada"),
    ("Minutos de pantalla", "teléfono", "Pasos", "cuerpo", -0.27, 0.04, "120 días · débil · con tendencia: -0.49"),
    ("Aparece @persona_a (0/1)", "personas", "Activación del día", "ánimo", +0.18, 0.31, "64 días · débil · podría ser azar"),
]
fig, ax = plt.subplots(figsize=(9, 6.6), dpi=160); ax.set_xlim(0, 10); ax.set_ylim(0, 3 * 2.2 + 0.35); ax.axis("off")
for i, (a, ga, b, gb, r, q, det) in enumerate(ej):
    y0 = 3 * 2.2 - (i + 1) * 2.2 + .35
    ax.add_patch(FancyBboxPatch((0.2, y0 + .55), 9.6, 1.5, boxstyle="round,pad=0.02", fc="white", ec=estilo.GRIS_MEDIO))
    for x0, nom, g in [(0.45, a, ga), (6.15, b, gb)]:
        ax.add_patch(FancyBboxPatch((x0, y0 + .85), 3.4, 1.0, boxstyle="round,pad=0.03", fc=estilo.AZUL_PALIDO, ec=estilo.TINTA_2, lw=1.3))
        ax.text(x0 + 1.7, y0 + 1.45, nom, ha="center", va="center", fontsize=9)
        ax.text(x0 + 1.7, y0 + 1.08, g, ha="center", va="center", fontsize=7, color=estilo.GRIS)
    azar = q >= 0.10; col = VERDE if r >= 0 else ROJO
    grosor_dp = 1.2 + 4.5 * abs(r)
    ax.plot([3.85, 6.15], [y0 + 1.35, y0 + 1.35], color=col, lw=grosor_dp * 1.4, alpha=.65 if azar else 1, ls=(0, (2.5, 2)) if azar else "-",
            solid_capstyle="round")
    ax.text(5.0, y0 + 1.72, f"{r:+.2f}", ha="center", fontsize=9, color=col,
            bbox=dict(boxstyle="round,pad=0.2", fc="white", ec=col, alpha=1))
    ax.text(5.0, y0 + .65, det, ha="center", fontsize=8, color=estilo.TINTA_2)
    ax.text(9.95, y0 + 2.12, f"grosor {grosor_dp:.2f} dp · q = {q:.2f}", ha="right", fontsize=7, color=estilo.GRIS_MEDIO)
ax.text(5, .05, "● verde: suben juntas   ● rojo: una sube, la otra baja   ·   más gruesa = más fuerte   ·   punteada = q ≥ 0.10 (podría ser azar)",
        ha="center", fontsize=7.5, color=estilo.TINTA_2)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig15_tarjeta_relacion.png")
