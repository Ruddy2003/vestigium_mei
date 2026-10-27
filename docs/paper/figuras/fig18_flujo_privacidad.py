"""Figura 10.1 - Qué tipo de dato llega a cada salida de la app, según el código de v81 (ver capítulo 10).
No contiene datos: es una tabla de diseño. Las celdas con borde rojo son hallazgos (H-6-n o H-8-n).
Uso: python fig18_flujo_privacidad.py [salida.png]"""
import sys
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

COL = ["Pantallas\n(seguimiento\nprivado apagado)", "Reportes\n(se guardan en\nImágenes/Descargas)", "«Tu año»\n(historias para\ncompartir)",
       "Logros y\ncampañas", "Panel y\nanálisis", "Exportación,\ncarpeta, Drive\nsin contraseña", "Respaldo\ncon contraseña\n(.vmc)", "Servicios\nexternos"]
# (texto, nivel, hallazgo) · nivel: 0 no llega · 1 agregado o conteo · 2 contenido · 3 cifrado
F = {
    "Texto del diario": [("sí", 2, 0), ("frase del mejor\nmomento (90 car.)", 2, 0), ("conteos y\npalabra del año", 1, 0), ("cita de 22 palabras\nen el trofeo", 2, 0),
                         ("conteos", 1, 0), ("completo", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
    "@personas": [("sí", 2, 0), ("nombres\n(iniciales si discreto)", 2, 0), ("la más mencionada", 2, 0), ("conteo", 1, 0), ("per_, vi_…", 2, 0),
                  ("sí", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
    "Ubicación": [("sí", 2, 0), ("ciudades y mapa\nsin nombres", 1, 0), ("ciudades, km", 1, 0), ("conteos", 1, 0), ("lug_, km", 2, 0),
                  ("puntos GPS", 2, 0), ("cifrado", 3, 0), ("Open-Meteo: mediana\ndiaria a 0.01°", 1, 0)],
    "Momentos de impulso": [("sí", 2, 0), ("conteo por tipo\ny @menciones", 1, 1), ("«lo que pasó\nuna vez»", 1, 1), ("cuentan como\nmomentos", 1, 1),
                            ("per_, presencias", 2, 1), ("sí", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
    "Recaídas y libido": [("no", 0, 0), ("no", 0, 0), ("relación del año\n(si está activo)", 1, 1), ("recaída apaga\nel hábito", 1, 1), ("per_ siempre;\nresto si activo", 2, 1),
                          ("sí, siempre", 2, 1), ("cifrado", 3, 0), ("no", 0, 0)],
    "Medicamentos": [("sí", 2, 0), ("no", 0, 0), ("relación del año", 1, 1), ("no", 0, 0), ("medicamentos_n,\nmed_", 2, 0),
                     ("sí", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
    "Cartas selladas": [("no hasta\nsu fecha", 0, 0), ("conteo", 1, 0), ("no", 0, 0), ("conteos", 1, 0), ("conteo", 1, 0),
                        ("CSV sin texto,\nJSON con texto", 2, 1), ("cifrado", 3, 0), ("no", 0, 0)],
    "Salud, sueño, pasos": [("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
}
COLOR = {0: "#f3f4f6", 1: "#dbe7f0", 2: "#9cc0d9", 3: "#c7e3c0"}
filas = list(F)
fig, ax = plt.subplots(figsize=(14, 6.6), dpi=160)
ax.set_xlim(-2.2, len(COL)); ax.set_ylim(len(filas), -1.15); ax.axis("off")
for j, c in enumerate(COL): ax.text(j + .5, -.12, c, ha="center", va="bottom", fontsize=7.6, color="#1f2937", weight="bold")
for i, f in enumerate(filas):
    ax.text(-.08, i + .5, f, ha="right", va="center", fontsize=8.5, color="#1f2937", weight="bold")
    for j, (t, n, h) in enumerate(F[f]):
        ax.add_patch(Rectangle((j + .03, i + .05), .94, .9, fc=COLOR[n], ec="#c1121f" if h else "white", lw=2.2 if h else 1))
        ax.text(j + .5, i + .5, t, ha="center", va="center", fontsize=6.6, color="#1f2937")
ley = [Rectangle((0, 0), 1, 1, fc=COLOR[k]) for k in (0, 1, 2, 3)] + [Rectangle((0, 0), 1, 1, fc="white", ec="#c1121f", lw=2)]
ax.legend(ley, ["no llega", "agregado o conteo", "contenido", "cifrado", "hallazgo (H-6 o H-8)"], loc="lower center",
          bbox_to_anchor=(.45, -.1), ncol=5, fontsize=8, frameon=False)
fig.suptitle("Figura 10.1  Qué datos llegan a cada salida (código de v81)", x=.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.tight_layout(rect=(0, .03, 1, .95))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig18_flujo_privacidad.png")
