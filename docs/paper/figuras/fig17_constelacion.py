"""Figura 7.2 - Constelación con datos SINTÉTICOS, con las mismas fórmulas que PantallaConstelacion
(ui/pantallas/Constelacion.kt). Personas y fechas inventadas. Uso: python fig17_constelacion.py [salida.png]"""
import sys, math
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

rng = np.random.default_rng(3)
PERIODO = 365                                   # ficha «un año»
# (nombre, grupo de relación, días mencionada en el periodo, días desde la última mención)
gente = [("ana", "casa", 210, 0), ("beto", "pareja", 160, 1), ("caro", "familia", 95, 3), ("dani", "amistad", 80, 6),
         ("eli", "trabajo", 70, 2), ("fer", "amistad", 44, 20), ("gabo", "familia", 30, 45), ("hugo", "trabajo", 26, 9),
         ("ines", None, 18, 120), ("juli", "expareja", 15, 240), ("kike", "amistad", 12, 60), ("lau", None, 9, 300),
         ("memo", "trabajo", 8, 30), ("nora", None, 5, 200), ("oli", "familia", 4, 330), ("pau", None, 3, 15)]
gente.sort(key=lambda g: -g[2])                 # la app ordena por días mencionada (de más a menos)
COLOR = {"casa": estilo.AZUL, "familia": estilo.VERDE, "pareja": estilo.ROJO, "amistad": estilo.OCRE_CLARO, "trabajo": "#8d99ae",
         "expareja": estilo.ROJO_CLARO, None: estilo.TINTA}

def h(nombre):                                  # String.hashCode de Kotlin/Java, con «and 0x7fffffff»
    x = 0
    for ch in nombre: x = (31 * x + ord(ch)) & 0xFFFFFFFF
    return x & 0x7FFFFFFF

maxd = max(g[2] for g in gente)
R = 1.0
pos = {}
for i, (n, rel, dias, hace) in enumerate(gente):
    ang = i * 2.399963 + (h(n) % 1000) / 1000.0 * 0.35            # ángulo de girasol + azar fijo por nombre
    d = (0.30 + 0.64 * min(max(hace / PERIODO, 0), 1) ** 0.6) * R * 0.94   # distancia según hace cuánto apareció
    r = 2.5 + 7 * math.sqrt(dias / maxd)                           # radio (en dp) según días mencionada
    pos[n] = (d * math.cos(ang), d * math.sin(ang), r, rel, dias, hace)

# Días juntas (co-mención el mismo día), inventados; la app dibuja solo los pares con 2 días o más.
juntos = {("ana", "beto"): 40, ("ana", "caro"): 22, ("caro", "gabo"): 9, ("caro", "oli"): 2, ("dani", "fer"): 12,
          ("dani", "kike"): 6, ("eli", "hugo"): 15, ("eli", "memo"): 5, ("hugo", "memo"): 4, ("beto", "dani"): 3,
          ("fer", "kike"): 1, ("ines", "lau"): 1}

fig, (a, b) = plt.subplots(1, 2, figsize=(11.5, 5.6), dpi=160, gridspec_kw={"width_ratios": [1.15, 1]})
a.set_aspect("equal"); a.set_xlim(-1.08, 1.08); a.set_ylim(-1.08, 1.08); a.axis("off")
for k in (0.33, 0.66, 1.0):
    a.add_patch(plt.Circle((0, 0), R * 0.94 * k, fill=False, ec=estilo.GRIS_MEDIO, lw=.7, alpha=.6))
for (p, q), n in juntos.items():
    if n < 2: continue
    x1, y1, *_ = pos[p]; x2, y2, *_ = pos[q]
    a.plot([x1, x2], [y1, y2], color=estilo.AZUL, alpha=min(0.12 + 0.06 * n, 0.5), lw=min(0.6 + n * 0.15, 2.0) * 1.2, zorder=1)
a.scatter([0], [0], s=40, color=estilo.AZUL, zorder=4); a.text(0, -0.07, "tú", ha="center", va="top", fontsize=8, color=estilo.AZUL)
for i, (n, (x, y, r, rel, dias, hace)) in enumerate(pos.items()):
    c = COLOR[rel]
    a.scatter([x], [y], s=(r * 2.2 * 1.6) ** 2, color=c, alpha=.18, lw=0, zorder=2)
    a.scatter([x], [y], s=(r * 2.2) ** 2, color=c, zorder=3)
    if i < 14: a.text(x, y - 0.035 - r * 0.006, "@" + n, ha="center", va="top", fontsize=7, color=estilo.TINTA)
a.text(-1.05, 1.03, "anillos: guía visual (no son umbrales); solo las 14 primeras llevan nombre", fontsize=7, color=estilo.GRIS)
a.set_title("Constelación de un año (16 personas inventadas)", loc="left", fontsize=9.5, color=estilo.TINTA)

# Panel derecho: cómo salen distancia y tamaño.
b.set_title("De dónde sale la posición y el tamaño", loc="left", fontsize=9.5, color=estilo.TINTA)
x = np.linspace(0, PERIODO, 200)
b.plot(x, (0.30 + 0.64 * (x / PERIODO) ** 0.6) * 0.94, color=estilo.AZUL, label="distancia al centro (fracción del radio)")
b.set_xlabel("días desde la última mención (periodo de 365 días)", fontsize=8.5); b.set_ylabel("distancia / radio", fontsize=8.5)
b.set_ylim(0, 1); b.tick_params(labelsize=8)
for n in ("ana", "fer", "juli", "oli"):
    _, _, r, rel, dias, hace = pos[n]
    yy = (0.30 + 0.64 * (hace / PERIODO) ** 0.6) * 0.94
    b.scatter([hace], [yy], color=COLOR[rel], zorder=3); b.annotate(f"@{n}: hace {hace} d", (hace, yy), xytext=(-60 if hace > 300 else 6, -12), textcoords="offset points", fontsize=7.5)
b.text(10, 0.93, "d = (0.30 + 0.64·(días/365)^0.6) · 0.94 · radio\nradio de la estrella = 2.5 + 7·√(días mencionada / máximo) dp",
       fontsize=7.8, color=estilo.TINTA, va="top")
hand = [plt.Line2D([], [], marker="o", ls="", color=COLOR[k], label=l) for k, l in
        [("casa", "vive contigo"), ("familia", "familia"), ("pareja", "pareja"), ("amistad", "amistad"), ("trabajo", "trabajo"),
         ("expareja", "expareja (tenue)"), (None, "sin clasificar / otra")]]
b.legend(handles=hand, loc="lower right", fontsize=7.5, frameon=False, title="color = relación ACTUAL", title_fontsize=7.5)
fig.suptitle("La constelación: tamaño = días mencionada, cercanía al centro = recencia (datos sintéticos)", x=0.01, ha="left",
             fontsize=11, weight="bold", color=estilo.TINTA)
fig.tight_layout(rect=(0, 0, 1, .94))
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig17_constelacion.png")
