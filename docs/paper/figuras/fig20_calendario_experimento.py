"""Figura 8.1 - Calendario de dos experimentos SINTÉTICOS con las reglas de Experimentos.toca / enLavado
(data/Lote3.kt), vigentes desde la entrega 10c: la plantilla «Creatina» dura 12 semanas y el aviso del Día no sale
antes del inicio. Uso: python fig20_calendario_experimento.py [salida.png]"""
import sys
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)
from matplotlib.patches import Rectangle

def en_lavado(plan, lavado, d):
    """Copia de Experimentos.enLavado: los primeros `lavado` días tras cada cambio (y tras empezar con la acción)."""
    if lavado <= 0: return False
    cambios = ([0] if plan[0] else []) + [k * 7 for k in range(1, len(plan)) if plan[k] != plan[k - 1]]
    return any(0 <= d - c < lavado for c in cambios)

CON, SIN, LAV = estilo.AZUL, estilo.OCRE_CLARO, estilo.REJILLA
DIAS = ["L", "M", "X", "J", "V", "S", "D"]

def calendario(ax, plan, lavado, titulo, semana_previa=False):
    filas = len(plan) + (1 if semana_previa else 0)
    ax.set_xlim(-2.6, 7.2); ax.set_ylim(filas + .2, -1.0); ax.axis("off")
    for j, n in enumerate(DIAS): ax.text(j + .5, -.45, n, ha="center", fontsize=8, color=estilo.GRIS)
    con = sin = 0
    if semana_previa:
        for j in range(7):
            ax.add_patch(Rectangle((j + .06, .06), .88, .88, fc="white", ec=estilo.GRIS_MEDIO, ls="--"))
            if j >= 1: ax.text(j + .5, .5, "!", ha="center", va="center", fontsize=9, color=estilo.OCRE, weight="bold")
        ax.text(-.15, .5, "semana previa", ha="right", va="center", fontsize=7.5, color=estilo.OCRE)
    off = 1 if semana_previa else 0
    for s, toca in enumerate(plan):
        ax.text(-.15, s + off + .5, f"sem. {s + 1}: {'SÍ' if toca else 'no'}", ha="right", va="center", fontsize=8, color=estilo.TINTA)
        for j in range(7):
            d = s * 7 + j; lav = en_lavado(plan, lavado, d)
            ax.add_patch(Rectangle((j + .06, s + off + .06), .88, .88, fc=LAV if lav else (CON if toca else SIN), ec="white",
                                   hatch="///" if lav else None))
            if not lav:
                if toca: con += 1
                else: sin += 1
    ax.set_title(titulo, loc="left", fontsize=9.5, color=estilo.TINTA)
    ax.text(-2.5, filas + .1, f"cuentan: {con} días con la acción, {sin} sin ella", fontsize=8.5, color=estilo.TINTA, va="top", weight="bold")
    return con, sin

fig, (a, b) = plt.subplots(1, 2, figsize=(11.5, 6.4), dpi=160)
plan_a = [True, False, False, True, True, False]               # un sorteo posible de 3 «SÍ» y 3 «no»
r1 = calendario(a, plan_a, 2, "Semanas alternas sorteadas · 6 semanas · 2 días de lavado")
plan_b = [False] * 6 + [True] * 6                              # plantilla «Creatina» (10c): antes y después, 12 semanas, 28 días de lavado
r2 = calendario(b, plan_b, 28, "Plantilla «Creatina» · antes y después · 12 semanas · 28 de lavado")
hand = [Rectangle((0, 0), 1, 1, fc=CON), Rectangle((0, 0), 1, 1, fc=SIN), Rectangle((0, 0), 1, 1, fc=LAV, hatch="///")]
fig.legend(hand, ["con la acción (cuenta)", "sin la acción (cuenta)", "lavado (no cuenta)"],
           loc="lower center", ncol=3, fontsize=8, frameon=False)
fig.suptitle("Qué días cuentan en un experimento (datos sintéticos)", x=0.01, ha="left", fontsize=11, weight="bold", color=estilo.TINTA)
fig.tight_layout(rect=(0, .06, 1, .94))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig20_calendario_experimento.png")
print("alterno:", r1, " creatina:", r2)
