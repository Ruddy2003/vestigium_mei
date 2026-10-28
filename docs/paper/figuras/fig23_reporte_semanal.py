"""Figura 9.1 - Imitación SIMPLIFICADA de «La semana de un vistazo» del reporte semanal (Reportes.semanaVisual), con datos
SINTÉTICOS y las reglas de dibujo del código: medalla del color del ánimo con el número redondeado, hojas de diario
(una hoja = max(10, ⌈máx/10⌉) palabras, tope 10), barras de sueño de 0 a 10 h con línea a 7 h y en rojo bajo 6.3 h,
huellas de pasos (una cada 2,000, tope 10). Uso: python fig23_reporte_semanal.py [salida.png]"""
import sys, math
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)
from matplotlib.colors import to_rgb
from matplotlib.patches import Circle, FancyBboxPatch, Rectangle

DIAS = ["lun 6", "mar 7", "mié 8", "jue 9", "vie 10", "sáb 11", "dom 12"]
animo = [4.5, 5, None, 3, 6, 6.5, 5]          # diario o, si falta, promedio de los registros de ánimo (Periodo.valDe)
palabras = [180, 40, 0, 320, 95, 0, 610]
sueno = [7.4, 6.1, 6.8, 5.2, 7.9, 8.6, 7.1]
pasos = [6200, 9800, 4100, 3500, 12600, 15400, 7300]
ERROR, LINEA, PRIM = estilo.ROJO, estilo.GRIS_CLARO, estilo.AZUL

def mezcla(a, b, t): a, b = to_rgb(a), to_rgb(b); return tuple(x + (y - x) * t for x, y in zip(a, b))
def color_animo(v):
    """Lienzo.colorAnimo: del color de alerta, pasando por la línea, al color del tema."""
    t = (v - 1) / 6
    return mezcla(ERROR, LINEA, t * 2) if t < .5 else mezcla(LINEA, PRIM, (t - .5) * 2)

fig, ax = plt.subplots(figsize=(9.2, 7.4), dpi=160)
ax.set_xlim(0, 7); ax.set_ylim(-11.6, 1.2); ax.axis("off")
for i, d in enumerate(DIAS): ax.text(i + .5, .75, d, ha="center", fontsize=9, weight="bold", color=estilo.TINTA)

def rotulo(y, t): ax.text(0.02, y, t, fontsize=8, color=estilo.GRIS, style="italic")
rotulo(.15, "ánimo")
for i, v in enumerate(animo):
    if v is None: ax.scatter([i + .5], [-.6], s=1000, facecolor="none", edgecolor=estilo.GRIS_MEDIO, linestyle="--")
    else:
        ax.scatter([i + .5], [-.6], s=1000, color=[color_animo(v)])
        n = int(math.floor(v + .5))                      # String.format("%.0f") redondea la mitad hacia arriba
        ax.text(i + .5, -.6, f"{n}", ha="center", va="center", fontsize=11, color="white", weight="bold")

maxp = max(palabras); por_hoja = max(10, -(-maxp // 10))
rotulo(-1.4, f"páginas escritas · una hoja = {por_hoja} palabras")
base = -3.6
for i, p in enumerate(palabras):
    hojas = 0 if p == 0 else min(10, max(1, -(-p // por_hoja)))
    for k in range(hojas):
        dx = -.015 if k % 2 == 0 else .015
        ax.add_patch(FancyBboxPatch((i + .25 + dx, base + k * .21), .5, .16, boxstyle="round,pad=0.01", fc="#f3f4f6", ec=estilo.GRIS_MEDIO, lw=.8))
    if p: ax.text(i + .5, base - .3, f"{p}", ha="center", fontsize=7.5, color=estilo.GRIS, family="monospace")

rotulo(-4.25, "sueño (h) · línea punteada = 7 h; barra roja si < 6.3 h")
b0, alto = -7.0, 2.4
ax.plot([0, 7], [b0 + alto * .7, b0 + alto * .7], ls="--", color=estilo.GRIS_MEDIO, lw=.9)
for i, s in enumerate(sueno):
    h = min(max(s, 0), 10) / 10 * alto
    ax.add_patch(Rectangle((i + .38, b0), .24, h, color=mezcla(ERROR, LINEA, .3) if s < 6.3 else estilo.VERDE))
    ax.text(i + .5, b0 - .3, f"{s:.1f}", ha="center", fontsize=7.5, color=estilo.GRIS, family="monospace")

rotulo(-7.65, "pasos · una huella = 2,000")
for i, p in enumerate(pasos):
    k = min(10, -(-p // 2000))
    for j in range(k):
        fila, col = divmod(j, 2)
        ax.scatter([i + .38 + col * .24], [-10.6 + fila * .52], s=60, color="#8d99ae")
    ax.text(i + .5, -11.25, f"{p:,}", ha="center", fontsize=7.5, color=estilo.GRIS, family="monospace")

fig.suptitle("«La semana de un vistazo» del reporte semanal (imitación con datos sintéticos)", x=.01, ha="left",
             fontsize=10.5, weight="bold", color=estilo.TINTA)
fig.text(.01, .005, "El miércoles no hay ánimo: círculo punteado. El sábado (6.5) se muestra como 7 y el lunes (4.5) como 5.\nLas cifras de arriba del reporte (días con diario, ánimo medio, sueño, pasos…) se omiten aquí.",
         fontsize=7.5, color=estilo.TINTA_2)
fig.tight_layout(rect=(0, .05, 1, .95))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig23_reporte_semanal.png")
