"""Figura 5.2 - Alcohol en sangre estimado con la simulación de Sustancias.alcoholEn (data/Sustancias.kt): pasos de 5 min,
cada dosis entra en 30 min, eliminación constante β = 0.15 g/L/h, volumen r·W (Widmark). Tres bebidas estándar (14 g)
repartidas como en Sustancias.dosis (episodio de 20:00 a 23:00 → en el primer 70 %). Desde la entrega 10c, con peso real, sexo,
estatura y (en hombres) edad, r sale del agua corporal de Watson et al. (1980): r = agua / (0.80 · peso), acotada en [0.45, 0.95]
(Sustancias.watsonR); la curva punteada lo muestra para el mismo hombre con 30 años y 180 cm. Datos SINTÉTICOS.
Uso: python fig26_alcohol_widmark.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

PASO = 5 / 60          # horas
def dosis_episodio(inicio, fin, bebidas, gramos=14.0):
    tramo = (fin - inicio) * 0.7; k = int(np.ceil(bebidas))
    return [(inicio + (0 if k == 1 else tramo * i / (k - 1)), (bebidas - (k - 1) if i == k - 1 else 1.0) * gramos) for i in range(k)]

def simular(dosis, r, peso, beta=0.15, hasta=32.0):
    t = dosis[0][0]; g = 0.0; ts, cs = [], []
    while t <= hasta:
        for (h, gr) in dosis:
            if 0 <= t - h < 0.5 - 1e-9: g += gr * PASO / 0.5
        g = max(g - beta * r * peso * PASO, 0.0)
        ts.append(t); cs.append(g / (r * peso)); t += PASO
    return np.array(ts), np.array(cs)

def watson_r(sexo, edad, estatura, peso):
    """Copia de Sustancias.watsonR."""
    agua = 2.447 - 0.09516 * edad + 0.1074 * estatura + 0.3362 * peso if sexo == "hombre" else -2.097 + 0.1069 * estatura + 0.2466 * peso
    return min(max(agua / (0.80 * peso), 0.45), 0.95)

d = dosis_episodio(20.0, 23.0, 3)
rw = watson_r("hombre", 30, 180, 75)
casos = [("hombre, 75 kg (r = 0.68)", 0.68, 75, estilo.AZUL), ("mujer, 60 kg (r = 0.55)", 0.55, 60, estilo.VIOLETA), ("sin sexo, 70 kg por omisión (r = 0.62)", 0.62, 70, estilo.GRIS)]
fig, ax = plt.subplots(figsize=(10, 4.8), dpi=160)
salida = []
for nom, r, w, c in casos:
    ts, cs = simular(d, r, w)
    ax.plot(ts, cs, color=c, lw=2, label=nom)
    i = cs.argmax(); cero = ts[(ts > ts[i]) & (cs <= 0)]
    salida.append((nom, cs.max(), ts[i], cero[0] if len(cero) else None))
ts, cs = simular(d, rw, 75)
ax.plot(ts, cs, color=estilo.AZUL, lw=1.6, ls=":", label=f"el mismo hombre con 30 años y 180 cm: r de Watson = {rw:.3f}")
i = cs.argmax(); cero = ts[(ts > ts[i]) & (cs <= 0)]
salida.append((f"hombre 75 kg con Watson (r = {rw:.3f})", cs.max(), ts[i], cero[0] if len(cero) else None))
# Banda de incertidumbre para el hombre: β de 0.10 a 0.20 g/L/h (Jones, 2010)
_, lo = simular(d, 0.68, 75, beta=0.20); ts, hi = simular(d, 0.68, 75, beta=0.10)
n = min(len(lo), len(hi)); ax.fill_between(ts[:n], lo[:n], hi[:n], color=estilo.AZUL, alpha=.12, lw=0, label="hombre con β de 0.10 a 0.20 g/L/h")
for h, g in d: ax.annotate(f"{g:.0f} g", (h, 0), xytext=(h, -.09), ha="center", fontsize=7.5, arrowprops=dict(arrowstyle="->", color=estilo.TINTA))
ax.axhline(.5, color=estilo.GRIS_MEDIO, lw=.8, ls="--"); ax.text(31.9, .51, "0.5 g/L (referencia visual; la app no usa umbrales)", fontsize=7.5, color=estilo.GRIS, ha="right")
ax.set_xticks(range(20, 33, 1)); ax.set_xticklabels([f"{h % 24:02d}:00" for h in range(20, 33)], fontsize=7.5)
ax.set_xlim(19.5, 32); ax.set_ylim(-.12, 1.0); ax.set_ylabel("alcohol en sangre estimado (g/L ≈ ‰)", fontsize=8.5)
ax.legend(fontsize=8, frameon=False, loc="upper right")
fig.suptitle("Tres bebidas estándar con el modelo de Widmark de la app (datos sintéticos)", x=.01, ha="left", fontsize=11, weight="bold", color=estilo.TINTA)
fig.text(.01, .01, "Las tres bebidas (14 g cada una) se reparten como hace la app: un episodio de 20:00 a 23:00 → bebidas a las 20:00, 21:03 y 22:06.", fontsize=7.5, color=estilo.TINTA_2)
fig.tight_layout(rect=(0, .04, 1, .94))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig26_alcohol_widmark.png")
for nom, m, tm, z in salida: print(f"{nom}: máximo {m:.3f} g/L a las {int(tm)%24:02d}:{int(round((tm%1)*60)):02d}; llega a 0 a las {int(z)%24:02d}:{int(round((z%1)*60)):02d}")
print("dosis:", [(round(h, 2), g) for h, g in d])
