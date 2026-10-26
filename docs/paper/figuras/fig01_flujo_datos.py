"""Figura 2.1 - Flujo de datos de Vestigium Mei (captura -> almacén -> panel -> exportación -> R).
Diagrama esquemático; no usa datos reales. Uso: python fig01_flujo_datos.py [salida.png]"""
import sys
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch

INK, MUTED, SURF = "#1f2937", "#6b7280", "#ffffff"
COL = {"activa": "#2a6f97", "pasiva": "#6a994e", "importada": "#bc6c25", "nucleo": "#4b5563", "salida": "#7b5ea7"}

fig, ax = plt.subplots(figsize=(11, 5.3), dpi=160)
ax.set_xlim(0, 11); ax.set_ylim(0.9, 6.2); ax.axis("off")

def caja(x, y, w, h, titulo, sub, color):
    ax.add_patch(FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.02,rounding_size=0.12",
                                fc=SURF, ec=color, lw=1.8))
    ax.text(x + w / 2, y + h * 0.66, titulo, ha="center", va="center", fontsize=9.5, color=INK, weight="bold")
    ax.text(x + w / 2, y + h * 0.28, sub, ha="center", va="center", fontsize=7.6, color=MUTED)

def flecha(x1, y1, x2, y2, color=MUTED):
    ax.add_patch(FancyArrowPatch((x1, y1), (x2, y2), arrowstyle="-|>", mutation_scale=11, lw=1.3, color=color))

# Capa 1: captura
ax.text(1.55, 5.95, "1 · Captura", ha="center", fontsize=10, color=INK, weight="bold")
caja(0.2, 4.7, 2.7, 0.95, "Activa", "diario, avisos de ánimo,\nmomentos, gastos, notas", COL["activa"])
caja(0.2, 3.45, 2.7, 0.95, "Pasiva", "pantalla, sueño, pasos,\nubicación, clima, notificaciones", COL["pasiva"])
caja(0.2, 2.2, 2.7, 0.95, "Importada", "Takeout, WhatsApp, Cold Turkey,\nNutrición, respaldos", COL["importada"])

# Capa 2: almacén
ax.text(5.0, 5.95, "2 · Almacén", ha="center", fontsize=10, color=INK, weight="bold")
caja(3.9, 4.3, 2.2, 1.35, "Repo (memoria)", "34 colecciones\nestado observable", COL["nucleo"])
caja(3.9, 2.2, 2.2, 1.65, "SQLite local", "docs(col,id,fecha,json)\nubicaciones · fotos", COL["nucleo"])
flecha(3.0, 5.1, 3.85, 5.0); flecha(3.0, 3.9, 3.85, 4.6); flecha(3.0, 2.7, 3.85, 3.0)
flecha(5.0, 4.25, 5.0, 3.9); ax.text(5.12, 4.07, "escritura inmediata", fontsize=7, color=MUTED)

# Capa 3: derivación
ax.text(7.35, 5.95, "3 · Derivación", ha="center", fontsize=10, color=INK, weight="bold")
caja(6.6, 4.3, 1.9, 1.35, "Día lógico", "corte a las 05:00\nasigna cada evento a un día", COL["nucleo"])
caja(6.6, 2.2, 1.9, 1.65, "Panel diario", "fecha × variable\nnull ≠ 0 estructural", COL["nucleo"])
flecha(6.15, 5.0, 6.55, 5.0); flecha(7.55, 4.25, 7.55, 3.9); flecha(6.15, 3.0, 6.55, 3.0)

# Capa 4: salida
ax.text(9.85, 5.95, "4 · Salida", ha="center", fontsize=10, color=INK, weight="bold")
caja(9.0, 4.3, 1.85, 1.35, "Exportación", "48 CSV · SQLite\ndiccionario · zip", COL["salida"])
caja(9.0, 2.2, 1.85, 1.65, "Análisis", "vestigium.R (tidy)\nmódulos 3–5 del paper", COL["salida"])
flecha(8.55, 3.0, 8.95, 4.7); flecha(9.92, 4.25, 9.92, 3.9)

ax.text(5.5, 1.5, "Convención de lectura: las flechas indican quién alimenta a quién.\n"
        "Los colores de borde distinguen el origen del dato (activo, pasivo, importado) y se conservan en el capítulo 2.",
        ha="center", fontsize=8, color=MUTED)
ax.set_title("Figura 2.1  Flujo de datos de Vestigium Mei", loc="left", fontsize=11.5, color=INK, weight="bold", pad=6)
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig01_flujo_datos.png")
