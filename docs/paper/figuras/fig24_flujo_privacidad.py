"""Figura 10.1 - Qué tipo de dato llega a cada salida de la app, según el código después del módulo 10a (ver capítulo 10).
No contiene datos: es una tabla de diseño. Las celdas con borde rojo son hallazgos que siguen abiertos.
Uso: python fig24_flujo_privacidad.py [salida.png]"""
import sys
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)
from matplotlib.patches import Rectangle

COL = ["Pantallas\n(salvo Privado)", "Reportes\n(a mano /\nautomáticos)", "«Tu año»\n(historias para\ncompartir)",
       "Logros, metas\ny campañas", "Análisis\nde la app", "Exportación\npara R\n(a mano)", "Carpeta y Drive\nsin contraseña", "Respaldo\ncon contraseña\n(.vmc)", "Servicios\nexternos"]
# (texto, nivel, hallazgo) · nivel: 0 no llega · 1 agregado o conteo · 2 contenido · 3 cifrado
PRIV = [("solo en\nPrivado", 0, 0), ("no", 0, 0), ("no", 0, 0), ("no", 0, 0), ("no (solo\nel panel para R)", 0, 0),
        ("sí, siempre\n(sin cifrar)", 2, 0), ("no sale\n(en pausa)", 0, 0), ("cifrado", 3, 0), ("no", 0, 0)]
F = {
    "Texto del diario": [("sí", 2, 0), ("frase del mejor\nmomento / no", 2, 0), ("conteos y\npalabra del año", 1, 0), ("cita de 22 palabras\nen el trofeo", 2, 0),
                         ("conteos", 1, 0), ("completo", 2, 0), ("completo (en pausa\nsi hay privado)", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
    "@personas": [("sí", 2, 0), ("nombres /\niniciales", 2, 0), ("la más mencionada", 2, 0), ("conteo", 1, 0), ("per_, vi_…", 2, 0),
                  ("sí", 2, 0), ("sí (en pausa\nsi hay privado)", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
    "Ubicación": [("sí", 2, 0), ("ciudades y mapa\nsin nombres", 1, 0), ("ciudades, km", 1, 0), ("conteos", 1, 0), ("lug_, km", 2, 0),
                  ("puntos GPS", 2, 0), ("puntos GPS (en pausa\nsi hay privado)", 2, 0), ("cifrado", 3, 0), ("Open-Meteo: mediana\ndiaria a 0.01°", 1, 0)],
    "Impulsos, recaídas,\nlibido, detonantes": PRIV,
    "Medicamentos": PRIV,
    "Hábito ligado\na las recaídas": PRIV,
    "Cartas selladas": [("no hasta\nsu fecha", 0, 0), ("conteo", 1, 0), ("no", 0, 0), ("conteos", 1, 0), ("conteo", 1, 0),
                        ("CSV sin texto,\nJSON con texto", 2, 1), ("CSV sin texto,\nJSON con texto", 2, 1), ("cifrado", 3, 0), ("no", 0, 0)],
    "Salud, sueño, pasos": [("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("sí", 2, 0), ("cifrado", 3, 0), ("no", 0, 0)],
}
COLOR = {0: "#f3f4f6", 1: estilo.AZUL_PALIDO, 2: estilo.AZUL_CLARO, 3: estilo.VERDE_CLARO}
filas = list(F)
fig, ax = plt.subplots(figsize=(15.5, 7.4), dpi=160)
ax.set_xlim(-2.2, len(COL)); ax.set_ylim(len(filas), -1.15); ax.axis("off")
for j, c in enumerate(COL): ax.text(j + .5, -.12, c, ha="center", va="bottom", fontsize=7.6, color=estilo.TINTA, weight="bold")
for i, f in enumerate(filas):
    ax.text(-.08, i + .5, f, ha="right", va="center", fontsize=8.5, color=estilo.TINTA, weight="bold")
    for j, (t, n, h) in enumerate(F[f]):
        ax.add_patch(Rectangle((j + .03, i + .05), .94, .9, fc=COLOR[n], ec=estilo.ROJO if h else "white", lw=2.2 if h else 1))
        ax.text(j + .5, i + .5, t, ha="center", va="center", fontsize=6.6, color=estilo.TINTA)
ley = [Rectangle((0, 0), 1, 1, fc=COLOR[k]) for k in (0, 1, 2, 3)] + [Rectangle((0, 0), 1, 1, fc="white", ec=estilo.ROJO, lw=2)]
ax.legend(ley, ["no llega", "agregado o conteo", "contenido", "cifrado", "hallazgo abierto (H-8-5)"], loc="lower center",
          bbox_to_anchor=(.45, -.1), ncol=5, fontsize=8, frameon=False)
fig.suptitle("Qué datos llegan a cada salida (después del módulo 10a)", x=.01, ha="left", fontsize=11, weight="bold", color=estilo.TINTA)
fig.tight_layout(rect=(0, .03, 1, .95))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig24_flujo_privacidad.png")
