"""
Prueba 17: dos animaciones propias de cada tema (13 comunes + 2 del tema = 15).
Seis acciones base (sostener, trabajar, levantar, olfatear, mirar arriba y flotar) y, encima, el objeto de cada tema
(tablilla de cera, vela, pluma, teclado, mariposa, sello, lira, taza, espada, matraz, bandera, catalejo, regalo, ábaco…).
Todo lo demás (gato, fondos) viene de gato16.py.
"""
import math
import random
import sys
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato16 as g16      # noqa: E402
from gato8 import hx, mezcla, fuente, ESC   # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
for nombre in ("gato8", "gato9", "gato10", "gato11", "gato12", "gato13", "gato15", "gato16"):
    sys.modules[nombre].AQUI = AQUI
N, SUELO = g8.N, g8.SUELO

# ---------------------------------------------------------------------
#  Capas para los objetos: detrás de la pata cercana (b_*) y delante de todo (f_*). Letras U V X Q = colores del objeto.
# ---------------------------------------------------------------------
LETRAS = "UVAGCXQK"
i_ = [c[0] for c in g8.CAPAS].index("juguetel") + 1
for k, l in enumerate(LETRAS):
    g8.CAPAS.insert(i_ + k, ("b_" + l, l, "prop", 0.4))
g8.CAPAS.extend([("f_" + l, l, "propf", 0.4) for l in LETRAS])
g8.RANGO.update(prop=2.8, propf=8)

import inspect   # noqa: E402
_src = inspect.getsource(g8.acabar)
_ancla = "    for r in range(N - 1):\n        for c in range(N):\n            if out[r][c] in \"PLW\" and out[r + 1][c] == \"K\":"
assert _ancla in _src
_src = _src.replace(_ancla, """    for r in range(N):          # objetos: contorno de tinta donde tocan al gato
        for c in range(N):
            if parte[r][c] in ("prop", "propf") and out[r][c] in "UVX":
                for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    rr, cc = r + dr, c + dc
                    if dentro(rr, cc) and parte[rr][cc] not in ("prop", "propf") and (parte[r][c] == "propf" or RANGO.get(parte[rr][cc], 0) < RANGO["prop"]):
                        out[r][c] = "K"; break
""" + _ancla)
_viejo = 'if ch[r][c] in ".K" or (parte[r][c] == "sombrero" and ch[r][c] in "OH"):'
assert _viejo in _src
_src = _src.replace(_viejo, 'if ch[r][c] in ".K" or (parte[r][c] == "sombrero" and ch[r][c] in "OH") or (parte[r][c] in ("prop", "propf") and ch[r][c] == "Q"):')
exec(_src, g8.__dict__)

# El boceto del Renacimiento no conocía estas letras: se dibujan como trazos de sanguina.
import inspect   # noqa: E402
_src = inspect.getsource(g8.pintar_boceto).replace('elif v in "MZABCJY":', 'elif v in "MZABCJYUVXQ":')
exec(_src, g8.__dict__)

# ---------------------------------------------------------------------
#  Acciones base (mirando a la derecha, coordenadas de la cuadrícula de 64)
# ---------------------------------------------------------------------

def brazo(hom, paw, codo=(1.0, 0.8)):
    j1 = (hom[0] + (paw[0] - hom[0]) * 0.42 + codo[0], hom[1] + (paw[1] - hom[1]) * 0.42 + codo[1])
    j2 = (hom[0] + (paw[0] - hom[0]) * 0.76 + codo[0] * 0.45, hom[1] + (paw[1] - hom[1]) * 0.76 + codo[1] * 0.45)
    return [hom, j1, j2, paw]


def sostener(k):
    s = math.sin(2 * math.pi * k / 4)
    p = dict(g8.SENTADO)
    p.update(inc=-3 + 2.5 * s, cab=(38.5, 27.5 + 0.3 * s), ojo="cerrado" if k == 3 else "abierto")
    p["fn"] = brazo((36, 43), (44.8, 41.4 + 0.5 * s), (0.6, 1.8))
    p["ff"] = brazo((32.5, 43), (42.2, 42.2 + 0.5 * s), (0.6, 1.8))
    return p, dict(obj=(44.2, 40.2 + 0.5 * s), k=k)


TRAB = [(46.5, 55.0, 0.0), (48.8, 52.4, 1.0), (50.4, 54.8, 0.3), (48.0, 56.6, 0.0)]


def trabajar(k):
    px, py, l = TRAB[k]
    p = dict(g8.SENTADO)
    p.update(inc=13, cab=(39.6, 29.2))
    p["fn"] = brazo((36, 43), (px, py), (1.8, -0.4))
    p["ff"] = brazo((32.5, 43), (41.8, SUELO), (0.6, 0.4))
    return p, dict(obj=(52.5, 56.6), pata=(px, py), k=k)


def levantar(k):
    s = math.sin(2 * math.pi * k / 4)
    p = dict(g8.SENTADO)
    p.update(C=(32.5, 37.5), rc=7.4, cab=(35, 25), inc=-10)
    p["fn"] = brazo((35, 39), (42.4, 20.2 + 0.7 * s), (1.6, 0.2))
    p["ff"] = brazo((32, 39.5), (39.4, 20.8 + 0.7 * s), (1.6, 0.2))
    return p, dict(obj=(41.2, 17.6 + 0.7 * s), k=k)


def olfatear(k):
    s = math.sin(2 * math.pi * k / 4)
    p = dict(g8.DE_PIE)
    p.update(cab=(50.0, 44.6 + 0.4 * (k % 2)), inc=30, ojo="medio")
    p["cola"] = [(x + 0.7 * s * (i / 5) ** 1.5, y) for i, (x, y) in enumerate(g8.DE_PIE["cola"])]
    return p, dict(obj=(57.2, 55.2), k=k)


def mirar_arriba(k):
    t = 2 * math.pi * k / 6
    p = dict(g8.SENTADO)
    p.update(inc=-24 + 4 * math.cos(t), cab=(38.2, 27.2))
    p["cola"] = [(x + (0.8 * math.sin(t) if i >= 4 else 0), y) for i, (x, y) in enumerate(g8.SENTADO["cola"])]
    return p, dict(obj=(49 + 6 * math.cos(t), 12 + 3 * math.sin(t)), k=k)


def flotar(k):
    s = math.sin(2 * math.pi * k / 4)
    p = g8.mover(g8.DE_PIE, 0, -4.5 - 1.2 * s)
    p["fn"] = brazo((40, 45 - 4.5 - 1.2 * s), (45, 52 - 2 * s), (1.2, 0.4))
    p["hn"] = g8.p_tra((21, 43 - 4.5 - 1.2 * s), (17, 54.5 + s))
    p["cola"] = [(x, y - 1.6 * s * (i / 5)) for i, (x, y) in enumerate(p["cola"])]
    p["inc"] = -4 + 3 * s
    return p, dict(obj=(52, 12 - 1.5 * s), k=k)


ACCIONES = dict(sostener=(sostener, 4, "sentado"), trabajar=(trabajar, 4, "sentado"), levantar=(levantar, 4, "sentado"),
                olfatear=(olfatear, 4, "quieto"), mirar=(mirar_arriba, 6, "sentado"), flotar=(flotar, 4, "quieto"))

# ---------------------------------------------------------------------
#  Objetos. Cada uno dibuja en L con las capas b_*/f_* alrededor de su ancla.
# ---------------------------------------------------------------------

def rect(L, capa, x0, y0, x1, y1):
    L.poligono(capa, [(x0, y0), (x1, y0), (x1, y1), (x0, y1)])


def flama(L, capa, x, y, k, alto=3.2):
    a = alto * (1 + 0.15 * ((k * 7) % 3 - 1))
    L.poligono(capa, [(x - 1.1, y), (x - 0.6, y - a * 0.5), (x + 0.2 * ((k % 2) * 2 - 1), y - a), (x + 0.7, y - a * 0.45), (x + 1.1, y)])


def vapor(L, capa, x, y, k):
    for j in range(2):
        yy = y - 2 - ((k + j * 2) % 4) * 1.4
        L.elipse(capa, x - 0.8 + j * 1.6 + 0.5 * math.sin(k + j), yy, 0.55, 0.7)


# --- sostener
def o_escudo(L, a):           # romano: escudo (scutum) rojo con umbo de oro, delante del pecho
    x, y = a["obj"]
    rect(L, "f_U", x - 4.2, y - 6.5, x + 3.2, y + 7.5)
    L.capsula("f_X", [(x - 0.5, y - 6.2), (x - 0.5, y + 7.2)], 0.7)
    L.elipse("f_X", x - 0.5, y + 0.5, 1.8, 1.8)
    L.capsula("f_X", [(x - 4.2, y - 6.5), (x + 3.2, y - 6.5)], 0.6)


def o_vela(L, a):
    x, y = a["obj"]; k = a["k"]
    rect(L, "b_X", x - 2.6, y + 1.2, x + 2.6, y + 2.4)
    rect(L, "b_U", x - 1.2, y - 4.5, x + 1.2, y + 1.4)
    flama(L, "b_Q", x, y - 4.6, k, 3.4); flama(L, "b_V", x, y - 4.6, k, 1.6)


def o_libro(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_V", [(x - 6, y - 3.5), (x, y - 2.2), (x + 6, y - 3.5), (x + 6, y + 3.8), (x, y + 5), (x - 6, y + 3.8)])
    L.poligono("b_U", [(x - 5.4, y - 3.0), (x, y - 1.8), (x + 5.4, y - 3.0), (x + 5.4, y + 3.0), (x, y + 4.2), (x - 5.4, y + 3.0)])
    for j in range(3):
        L.capsula("b_K", [(x - 4.6, y - 1.6 + j * 1.5), (x - 1, y - 0.8 + j * 1.5)], 0.35)
        L.capsula("b_K", [(x + 1, y - 0.8 + j * 1.5), (x + 4.6, y - 1.6 + j * 1.5)], 0.35)
    if k == 2:                 # pasa la hoja
        L.poligono("b_Q", [(x, y - 1.8), (x + 2.6, y - 6.5), (x + 4.4, y - 5.6), (x, y + 4.2)])


def o_lira(L, a):
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_U", [(x - 3.6, y + 4), (x - 4.4, y - 2), (x - 2.6, y - 6)], 1.1)
    L.capsula("b_U", [(x + 3.6, y + 4), (x + 4.4, y - 2), (x + 2.6, y - 6)], 1.1)
    L.capsula("b_U", [(x - 3.8, y - 4.6), (x + 3.8, y - 4.6)], 0.9)
    L.elipse("b_V", x, y + 4.2, 4.2, 1.8)
    for j in (-1.8, 0, 1.8):
        L.capsula("b_Q", [(x + j + (0.3 if (k % 2 and j == 0) else 0), y - 4.2), (x + j, y + 3)], 0.35)


def o_taza(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_U", [(x - 2.8, y - 2.2), (x + 2.8, y - 2.2), (x + 2.2, y + 2.6), (x - 2.2, y + 2.6)])
    L.capsula("b_V", [(x - 2.7, y - 0.6), (x + 2.7, y - 0.6)], 0.8)
    L.capsula("b_U", [(x + 2.6, y - 1.2), (x + 4.0, y), (x + 2.4, y + 1.4)], 0.7)
    vapor(L, "b_Q", x, y - 2.2, k)


def o_escudo_engrane(L, a):   # orden: escudo carmesí con engrane de latón
    x, y = a["obj"]
    L.poligono("f_U", [(x - 4.4, y - 6), (x + 3.6, y - 6), (x + 3.6, y + 2), (x - 0.4, y + 7.4), (x - 4.4, y + 2)])
    for j in range(8):
        an = 2 * math.pi * j / 8
        L.elipse("f_X", x - 0.4 + 2.6 * math.cos(an), y - 0.8 + 2.6 * math.sin(an), 0.7, 0.7)
    L.elipse("f_X", x - 0.4, y - 0.8, 2.0, 2.0); L.elipse("f_U", x - 0.4, y - 0.8, 0.8, 0.8)


def o_matraz(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_U", [(x - 0.9, y - 6), (x + 0.9, y - 6), (x + 0.9, y - 2.4), (x + 3.4, y + 3), (x - 3.4, y + 3), (x - 0.9, y - 2.4)])
    L.poligono("b_V", [(x - 2.4, y + 0.6), (x + 2.4, y + 0.6), (x + 3.2, y + 2.8), (x - 3.2, y + 2.8)])
    L.elipse("b_Q", x - 0.6 + (k % 2), y - 0.6 - (k % 3) * 1.2, 0.5, 0.5)


def o_papel(sello=False, ruta=False):
    def f(L, a):
        x, y = a["obj"]
        rect(L, "b_U", x - 4.8, y - 4.4, x + 4.8, y + 4.0)
        for j in range(4):
            L.capsula("b_K", [(x - 3.8, y - 2.8 + j * 1.8), (x + (1.6 if j == 3 else 3.6), y - 2.8 + j * 1.8)], 0.35)
        if sello: L.elipse("b_V", x + 2.6, y + 2.6, 1.3, 1.3)
        if ruta:
            L.capsula("b_V", [(x - 3.8, y + 2.6), (x - 1.4, y - 0.4), (x + 1.2, y + 1.6), (x + 3.8, y - 2.4)], 0.5)
            L.capsula("b_V", [(x + 3.0, y - 3.2), (x + 4.4, y - 1.8)], 0.4); L.capsula("b_V", [(x + 4.4, y - 3.2), (x + 3.0, y - 1.8)], 0.4)
    return f


def o_regalo(L, a):
    x, y = a["obj"]
    rect(L, "b_U", x - 3.6, y - 3.0, x + 3.6, y + 3.6)
    L.capsula("b_V", [(x, y - 3), (x, y + 3.6)], 1.0); L.capsula("b_V", [(x - 3.6, y), (x + 3.6, y)], 1.0)
    L.elipse("b_V", x - 1.4, y - 4.0, 1.4, 0.9, 20); L.elipse("b_V", x + 1.4, y - 4.0, 1.4, 0.9, -20)


def o_cuenco(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_U", [(x - 4.0, y - 1.2), (x + 4.0, y - 1.2), (x + 2.6, y + 2.6), (x - 2.6, y + 2.6)])
    L.capsula("b_V", [(x - 3.6, y), (x + 3.6, y)], 0.7)
    L.elipse("b_Q", x, y - 1.4, 3.4, 0.6)
    vapor(L, "b_Q", x, y - 1.6, k)


def o_farol(L, a):
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_U", [(x, y - 7.2), (x, y - 5.6)], 0.5)
    L.poligono("b_U", [(x - 2.4, y - 5.2), (x + 2.4, y - 5.2), (x + 2.0, y + 2.6), (x - 2.0, y + 2.6)])
    rect(L, "b_Q", x - 1.4, y - 4.0, x + 1.4, y + 1.6)
    flama(L, "b_V", x, y + 0.8, k, 2.6)
    L.capsula("b_U", [(x - 2.6, y + 2.6), (x + 2.6, y + 2.6)], 0.8)


def o_mapa(L, a):
    x, y = a["obj"]
    L.elipse("b_X", x - 5.4, y, 0.9, 4.6); L.elipse("b_X", x + 5.4, y, 0.9, 4.6)
    rect(L, "b_U", x - 5.2, y - 4.0, x + 5.2, y + 4.0)
    L.capsula("b_V", [(x - 4, y + 2.6), (x - 1.6, y - 0.6), (x + 1.0, y + 1.4), (x + 3.4, y - 2.0)], 0.45)
    L.poligono("b_K", [(x + 2.2, y - 0.6), (x + 3.2, y - 2.8), (x + 4.2, y - 0.6)])


def o_catalejo(L, a):
    x, y = a["obj"]
    L.capsula("b_U", [(x - 3.6, y + 1.2), (x + 5.6, y - 3.4)], 2.0)
    L.capsula("b_X", [(x + 3.4, y - 2.3), (x + 6.4, y - 3.8)], 2.4)
    L.capsula("b_V", [(x - 1.0, y - 0.1), (x - 0.4, y - 0.4)], 2.2)


# --- trabajar (objeto en el suelo y herramienta en la pata)
def t_tablilla(L, a):
    x, y = a["obj"]; px, py = a["pata"]
    rect(L, "b_U", x - 6, y - 3.2, x + 5, y + 2.2); rect(L, "b_V", x - 5, y - 2.4, x + 4, y + 1.4)
    for j in range(2): L.capsula("b_Q", [(x - 4.2, y - 1.4 + j * 1.6), (x + (a["k"] * 0.8 - 1), y - 1.4 + j * 1.6)], 0.35)
    L.capsula("f_X", [(px + 0.4, py - 0.6), (px + 2.6, py - 4.6)], 0.6)


def t_pluma(L, a):
    x, y = a["obj"]; px, py = a["pata"]
    rect(L, "b_U", x - 6, y - 2.2, x + 5, y + 2.2)
    for j in range(2): L.capsula("b_K", [(x - 5, y - 0.8 + j * 1.5), (x + a["k"] * 0.9 - 1.5, y - 0.8 + j * 1.5)], 0.3)
    L.elipse("b_V", x + 6.8, y + 0.4, 1.4, 1.6)
    L.capsula("f_Q", [(px + 0.4, py - 0.8), (px + 1.6, py - 3.6), (px + 3.8, py - 7.6)], 0.9)
    L.capsula("f_X", [(px + 1.4, py - 3.4), (px + 3.6, py - 7.4)], 0.35)


def t_teclado(L, a):
    x, y = a["obj"]
    L.poligono("b_U", [(x - 6.5, y + 1.6), (x + 5.5, y + 1.6), (x + 4.5, y - 1.6), (x - 5.5, y - 1.6)])
    for j in range(5):
        L.elipse("b_Q", x - 4.5 + j * 2.2, y - 0.4 + (0.1 if j % 2 else 0), 0.45, 0.4)
    rect(L, "b_V", x - 4.4, y - 8.6, x + 3.6, y - 3.8)
    for j in range(2): L.capsula("b_Q", [(x - 3.4, y - 7.2 + j * 1.6), (x - 3.4 + ((a["k"] + j) % 4) * 1.6 + 1, y - 7.2 + j * 1.6)], 0.4)


def t_sello(L, a):
    x, y = a["obj"]; px, py = a["pata"]; k = a["k"]
    rect(L, "b_U", x - 6, y - 2.2, x + 5, y + 2.2)
    for j in range(2): L.capsula("b_X", [(x - 5, y - 1.0 + j * 1.6), (x - 1, y - 1.0 + j * 1.6)], 0.35)
    if k in (2, 3): L.elipse("b_Q", x + 2, y, 1.6, 1.2)
    L.capsula("f_V", [(px + 1.2, py - 4.6), (px + 1.2, py - 1.2)], 1.0)
    rect(L, "f_V", px - 0.4, py - 1.4, px + 2.8, py + 0.2)


def t_llave(L, a):
    x, y = a["obj"]; px, py = a["pata"]
    rect(L, "b_U", x - 5, y - 4.4, x + 4.6, y + 2.2); L.capsula("b_V", [(x - 5, y - 1.4), (x + 4.6, y - 1.4)], 0.8)
    L.elipse("b_Q", x + 2.6, y - 3.2, 0.5, 0.5)
    L.capsula("f_X", [(px + 0.2, py - 0.4), (px + 3.8, py - 3.6)], 0.9)
    L.elipse("f_X", px + 4.2, py - 4.0, 1.2, 1.2); L.elipse("f_K", px + 4.6, py - 4.4, 0.45, 0.45)


def t_pipeta(L, a):
    x, y = a["obj"]; px, py = a["pata"]; k = a["k"]
    L.elipse("b_U", x, y + 0.8, 5.2, 1.6); L.elipse("b_V", x, y + 0.6, 3.6, 0.9)
    L.capsula("f_U", [(px + 1.0, py - 7.4), (px + 1.0, py + 0.6)], 0.9)
    L.capsula("f_X", [(px + 1.0, py - 8.6), (px + 1.0, py - 7.2)], 1.2)
    if k == 1: L.elipse("f_V", px + 1.0, py + 2.2, 0.5, 0.6)


def t_martillo(base="tabla"):
    def f(L, a):
        x, y = a["obj"]; px, py = a["pata"]
        if base == "tabla":
            rect(L, "b_U", x - 7, y - 1.4, x + 5, y + 2.2); L.elipse("b_K", x + 1.6, y - 0.2, 0.45, 0.45)
            L.capsula("b_X", [(x + 1.6, y - 1.4), (x + 1.6, y - 2.6)], 0.5)
        else:
            L.poligono("b_V", [(x - 6, y - 3.2), (x + 5, y - 3.2), (x + 7, y - 2.2), (x + 3, y - 1.2), (x + 2, y + 2.2), (x - 3, y + 2.2), (x - 4, y - 1.2)])
        L.capsula("f_U" if base == "tabla" else "f_X", [(px + 0.4, py - 0.4), (px + 3.2, py - 4.6)], 0.8)
        rect(L, "f_X" if base == "tabla" else "f_V", px + 1.6, py - 6.8, px + 5.2, py - 4.6)
    return f


def t_abaco(L, a):
    x, y = a["obj"]; k = a["k"]
    rect(L, "b_U", x - 6, y - 5.4, x + 5, y + 2.2); rect(L, "b_Q", x - 5, y - 4.4, x + 4, y + 1.2)
    for j in range(3):
        yy = y - 3.2 + j * 1.8
        L.capsula("b_K", [(x - 5, yy), (x + 4, yy)], 0.3)
        for b in range(3):
            off = (2.4 if (j == k % 3 and b == 2) else 0)
            L.elipse("b_V", x - 3.8 + b * 1.4 + off, yy, 0.6, 0.6)


def t_carbon(L, a):
    x, y = a["obj"]; px, py = a["pata"]; k = a["k"]
    rect(L, "b_U", x - 6, y - 2.4, x + 5, y + 2.2)
    L.capsula("b_V", [(x - 4.6, y + 1), (x - 2.4, y - 1.2), (x - 0.4, y + 0.6), (x - 0.4 + k * 0.9, y - 1.0)], 0.4)
    L.elipse("b_V", x + 2.6, y - 0.2, 1.4, 1.2)
    L.capsula("f_K", [(px + 0.4, py - 0.6), (px + 2.4, py - 3.8)], 0.7)


# --- levantar
def l_bandera(colores, estrella=False):
    def f(L, a):
        x, y = a["obj"]; k = a["k"]
        L.capsula("b_X", [(x - 0.6, y + 8.6), (x - 0.6, y - 9.6)], 0.8)
        L.elipse("b_G", x - 0.6, y - 10.0, 0.8, 0.8)
        n = len(colores); ola = lambda u: 0.8 * math.sin(k * math.pi / 2 + u * 1.8)
        for j, c in enumerate(colores):
            x0, x1 = x + 8.6 * j / n, x + 8.6 * (j + 1) / n
            L.poligono("b_" + c, [(x0, y - 9.2 + ola(j / n)), (x1, y - 9.2 + ola((j + 1) / n)), (x1, y - 3.2 + ola((j + 1) / n)), (x0, y - 3.2 + ola(j / n))])
        if estrella:
            L.poligono("b_Q", sys.modules["gato10"].estrella5(x + 3.0, y - 6.4 + ola(0.35), 1.6, 0.7))
    return f


def l_antorcha(L, a):
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_X", [(x - 0.4, y + 7.2), (x, y - 1.6)], 1.0)
    L.poligono("b_U", [(x - 1.8, y - 1.4), (x + 1.8, y - 1.4), (x + 1.2, y + 0.6), (x - 1.2, y + 0.6)])
    flama(L, "b_V", x, y - 1.4, k, 4.6); flama(L, "b_Q", x, y - 1.4, k, 2.4)


def l_espada(L, a):
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_X", [(x - 0.2, y + 4.8), (x + 0.6 * math.sin(k), y - 10.4)], 1.6)
    L.capsula("b_Q", [(x - 0.2, y + 2.8), (x + 0.6 * math.sin(k), y - 9.0)], 0.4)
    L.capsula("b_V", [(x - 2.6, y + 3.6), (x + 2.4, y + 3.6)], 0.9)
    L.capsula("b_U", [(x - 0.2, y + 3.8), (x - 0.2, y + 7.6)], 1.0)
    if k == 1: L.elipse("b_Q", x + 0.4, y - 7.6, 0.6, 1.2)


def l_gema(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_U", [(x - 2.6, y - 1.2), (x - 1.2, y - 3.2), (x + 1.2, y - 3.2), (x + 2.6, y - 1.2), (x, y + 2.8)])
    L.poligono("b_V", [(x - 1.2, y - 3.2), (x + 1.2, y - 3.2), (x + 0.6, y - 1.2), (x - 0.6, y - 1.2)])
    if k % 2 == 0:
        L.capsula("b_Q", [(x + 3.6, y - 5.2), (x + 3.6, y - 2.6)], 0.4); L.capsula("b_Q", [(x + 2.3, y - 3.9), (x + 4.9, y - 3.9)], 0.4)


# --- olfatear (en el suelo, delante de la nariz)
def s_flor(L, a):
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_X", [(x, y + 3.2), (x - 0.4, y - 1.6)], 0.6)
    L.elipse("b_X", x + 1.2, y + 1.2, 1.2, 0.5, -30)
    for j in range(5):
        an = 2 * math.pi * j / 5 + k * 0.1
        L.elipse("b_U", x - 0.4 + 1.3 * math.cos(an), y - 2.8 + 1.3 * math.sin(an), 0.9, 0.9)
    L.elipse("b_V", x - 0.4, y - 2.8, 0.7, 0.7)


# --- mirar arriba (algo que vuela)
def m_estrella(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_U", sys.modules["gato10"].estrella5(x, y, 2.6, 1.1))
    if k % 2: L.elipse("b_Q", x + 2.6, y - 2.4, 0.5, 0.5)


def m_mariposa(L, a):
    x, y = a["obj"]; k = a["k"]
    w = 2.4 if k % 2 == 0 else 0.9
    L.elipse("b_U", x - 1.4, y - 0.8, w, 1.7); L.elipse("b_U", x + 1.4, y - 0.8, w, 1.7)
    L.elipse("b_V", x - 1.0, y + 1.0, w * 0.6, 1.0); L.elipse("b_V", x + 1.0, y + 1.0, w * 0.6, 1.0)
    L.capsula("b_K", [(x, y - 1.6), (x, y + 1.8)], 0.5)


def m_dron(L, a):
    x, y = a["obj"]; k = a["k"]
    L.elipse("b_U", x, y, 2.6, 1.6); L.elipse("b_Q", x + 0.8, y + 0.2, 0.8, 0.8)
    L.capsula("b_X", [(x - 4.4, y - 1.8), (x + 4.4, y - 1.8)], 0.5)
    for s in (-1, 1):
        L.capsula("b_V", [(x + s * 4.4 - (1.8 if k % 2 else 0.6), y - 2.4), (x + s * 4.4 + (1.8 if k % 2 else 0.6), y - 2.4)], 0.4)


def m_ojo(L, a):
    x, y = a["obj"]; k = a["k"]
    L.elipse("b_U", x, y, 3.4, 2.0); L.elipse("b_Q", x + (0.6 if k % 3 else -0.6), y, 1.2, 1.2); L.elipse("b_V", x + (0.6 if k % 3 else -0.6), y, 0.5, 0.5)
    for s in (-1, 1): L.capsula("b_X", [(x + s * 3.6, y - 1.6), (x + s * 5.4, y - 2.4 if k % 2 else y - 0.8)], 0.5)


def m_copo(L, a):
    x, y = a["obj"]; k = a["k"]
    for j in range(3):
        an = math.pi * j / 3 + k * 0.2
        L.capsula("b_Q", [(x - 2.8 * math.cos(an), y - 2.8 * math.sin(an)), (x + 2.8 * math.cos(an), y + 2.8 * math.sin(an))], 0.7)
    L.elipse("b_Q", x, y, 0.9, 0.9)


def m_ave(L, a):
    x, y = a["obj"]; k = a["k"]
    alas = -2.4 if k % 2 == 0 else 1.2
    L.elipse("b_U", x, y, 2.0, 1.0)
    L.poligono("b_U", [(x - 1.4, y), (x - 5.4, y + alas), (x - 3.6, y + 0.6)]); L.poligono("b_U", [(x + 1.4, y), (x + 5.4, y + alas), (x + 3.6, y + 0.6)])
    L.elipse("b_V", x + 2.2, y - 0.4, 0.8, 0.7); L.poligono("b_X", [(x + 2.8, y - 0.6), (x + 3.8, y - 0.2), (x + 2.8, y + 0.1)])


def m_planeta(L, a):
    x, y = a["obj"]; k = a["k"]
    L.elipse("b_U", x, y, 2.4, 2.4); L.elipse("b_Q", x - 0.8, y - 0.8, 0.7, 0.6)
    L.capsula("b_V", [(x - 4.2, y + 0.8), (x + 4.2, y - 0.8)], 0.5)
    L.capsula("b_X", [(x - 7 - k, y - 4 + k * 0.5), (x - 4.6 - k, y - 3 + k * 0.5)], 0.4)


def m_maquina(L, a):          # renacimiento: el ornitóptero de Leonardo, en miniatura
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_U", [(x - 3.4, y), (x + 3.4, y)], 0.6)
    ala = -2.6 if k % 2 == 0 else 0.6
    L.poligono("b_V", [(x - 0.6, y), (x - 6.4, y + ala), (x - 4.6, y + ala + 1.6), (x - 0.6, y + 0.8)])
    L.poligono("b_V", [(x + 0.6, y), (x + 6.4, y + ala), (x + 4.6, y + ala + 1.6), (x + 0.6, y + 0.8)])
    L.capsula("b_U", [(x - 6.4, y + ala), (x - 0.6, y)], 0.35); L.capsula("b_U", [(x + 6.4, y + ala), (x + 0.6, y)], 0.35)


def nada(L, a):
    pass


# ---------------------------------------------------------------------
#  Las dos animaciones de cada tema: (acción, objeto, nombre) y colores del objeto (U, V, X, Q)
# ---------------------------------------------------------------------
ESP = {
    "romano": [("trabajar", t_tablilla, "escribe en su tablilla de cera"), ("sostener", o_escudo, "monta guardia con su escudo")],
    "gotico": [("sostener", o_vela, "sostiene una vela"), ("mirar", m_estrella, "mira una estrella de la bóveda")],
    "medieval": [("trabajar", t_pluma, "copia con pluma y tintero"), ("sostener", o_libro, "lee un libro y pasa la hoja")],
    "ciber": [("trabajar", t_teclado, "teclea en un teclado holográfico"), ("mirar", m_dron, "sigue a un dron")],
    "botanico": [("olfatear", s_flor, "huele una flor"), ("mirar", m_mariposa, "sigue a una mariposa")],
    "distopia": [("trabajar", t_sello, "sella un expediente"), ("mirar", m_ojo, "lo vigila un ojo volador")],
    "heleno": [("sostener", o_lira, "toca la lira"), ("levantar", l_antorcha, "alza la antorcha")],
    "refugio": [("sostener", o_taza, "toma su café"), ("trabajar", t_llave, "repara con la llave")],
    "orden": [("levantar", l_espada, "alza la espada"), ("sostener", o_escudo_engrane, "se cubre con el escudo")],
    "laboratorio": [("sostener", o_matraz, "observa un matraz"), ("trabajar", t_pipeta, "pone gotas con la pipeta")],
    "colonos": [("levantar", l_bandera("UUU", estrella=True), "alza la bandera"), ("trabajar", t_martillo("tabla"), "clava una tabla")],
    "napoleon": [("sostener", o_catalejo, "mira por el catalejo"), ("sostener", o_papel(sello=True), "lee un despacho sellado")],
    "navidad": [("sostener", o_regalo, "sostiene un regalo"), ("mirar", m_copo, "mira caer un copo")],
    "seda": [("sostener", o_taza, "bebe té"), ("trabajar", t_abaco, "cuenta con el ábaco")],
    "urss": [("levantar", l_bandera("UUU", estrella=True), "alza la bandera roja"), ("trabajar", t_martillo("yunque"), "martilla en el yunque")],
    "espacio": [("flotar", nada, "flota sin gravedad"), ("mirar", m_planeta, "mira un planeta y una estrella fugaz")],
    "andalus": [("sostener", o_farol, "sostiene un farol"), ("olfatear", s_flor, "huele un jazmín")],
    "mongol": [("mirar", m_ave, "sigue al águila"), ("sostener", o_cuenco, "bebe de su cuenco")],
    "renacimiento": [("trabajar", t_carbon, "dibuja con carboncillo"), ("mirar", m_maquina, "mira volar el ornitóptero")],
    "revolucion": [("levantar", l_bandera("UQV"), "alza la tricolor"), ("sostener", o_papel(), "lee el cuaderno de quejas")],
    "antiguas": [("sostener", o_mapa, "consulta el mapa"), ("levantar", l_gema, "alza una gema que brilla")],
}

C_ = lambda *hs: tuple(hx(h) for h in hs)
COL = {   # U, V, X, Q (de día); de noche se usan los mismos salvo los temas con material especial
    "romano": C_("8A5A2E", "3A3530", "B8892B", "E4D9B8"), "gotico": C_("F0E9D8", "E0405E", "E2B34A", "FFD27A"),
    "medieval": C_("F3E6C4", "1F3F8F", "9E1B1B", "FFFFFF"), "ciber": C_("0E6A7A", "6A1A48", "00E5FF", "E8FF3A"),
    "botanico": C_("C9604A", "E0B04F", "3F5A2C", "2E5E5A"), "distopia": C_("ECE9E1", "141414", "8A8780", "B3121B"),
    "heleno": C_("C9A04A", "8A5A2E", "6B4A22", "F2E6B0"), "refugio": C_("F8F1DE", "1C6E6A", "8A928F", "E8E2D0"),
    "orden": C_("9E1B22", "C9A040", "C9CED4", "FFFFFF"), "laboratorio": C_("DDEFF4", "0B8FA1", "4D5D6B", "FFFFFF"),
    "colonos": C_("2E4E86", "A23B2A", "7A5A2E", "F8F3E8"), "napoleon": C_("B8892B", "14213D", "D9B25B", "F4EEDF"),
    "navidad": C_("E5484D", "66BB6A", "F2C94C", "FFFFFF"), "seda": C_("F5EEDC", "23408E", "8A5A2E", "E8E2D0"),
    "urss": C_("C1121F", "8A8780", "161414", "F7F0DE"), "espacio": C_("FFB86B", "8AB4FF", "C59BFF", "FFFFFF"),
    "andalus": C_("E0B65A", "F2A23A", "1E6B5C", "FFF3C8"), "mongol": C_("7A5A3A", "1F5FA8", "C48A1E", "F1EDE3"),
    "renacimiento": C_("F7EDD4", "8C3B1E", "3A2614", "F0E4C6"), "revolucion": C_("1F3A6E", "B22234", "6A4A2A", "FAF6EC"),
    "antiguas": C_("3E9A6A", "8CC79A", "9A7B2E", "FFFFFF"),
}
# Pequeños ajustes de color por objeto (la flor de Al-Ándalus es un jazmín blanco; la tablilla de despacho, papel).
EXTRA_COL = {("gotico", 0): dict(Q=hx("FFB040"), V=hx("FFF3C8")), ("gotico", 1): dict(U=hx("E2B34A"), Q=hx("FFF3C8")),
             ("heleno", 1): dict(U=hx("8A6A2E"), V=hx("F08A2A"), Q=hx("FFE07A"), X=hx("6B4A22")),
             ("navidad", 1): dict(U=hx("E8F4FF"), Q=hx("FFFFFF")), ("mongol", 0): dict(U=hx("6A4A2A"), V=hx("F1EDE3"), X=hx("C48A1E")),
             ("renacimiento", 1): dict(U=hx("6A4A2A"), V=hx("C9A87A")), ("espacio", 1): dict(U=hx("FFB86B"), V=hx("C59BFF"), X=hx("E8ECF8")),
             ("botanico", 1): dict(U=hx("D9772E"), V=hx("2E5E5A"), K=None), ("andalus", 1): dict(U=hx("FBF6EA"), V=hx("E0B65A"), X=hx("3F6B3A")), ("napoleon", 1): dict(U=hx("F8F3E6"), V=hx("A3172E")),
             ("revolucion", 1): dict(U=hx("F2ECDD")), ("antiguas", 0): dict(U=hx("E9DFC4"), V=hx("8E1B24"), X=hx("8A6A3E")),
             ("seda", 0): dict(U=hx("FBF6EA"), V=hx("1A8C8C"), Q=hx("F0E6D0")), ("mongol", 1): dict(U=hx("8A5A3A"), V=hx("1F5FA8"), Q=hx("F6F2E8"))}


def colores(nombre, oscuro, j, t):
    U, V, X, Q = COL[nombre]
    c = dict(U=U, V=V, X=X, Q=Q)
    c.update({k: v for k, v in EXTRA_COL.get((nombre, j), {}).items() if v is not None})
    if t["material"] == "dos_tintas" and oscuro:          # negativo: «cuánta tinta» lleva cada color
        neg = lambda v: tuple(255 - x for x in v) if not (v[0] > v[1] + 50 and v[0] > v[2] + 40) else v
        c = {k: neg(v) for k, v in c.items()}
    if t["material"] == "fosforo":
        verde = lambda f: mezcla((0, 0, 0), hx("3DFF7F"), f)
        c = dict(U=verde(0.8), V=verde(0.45), X=verde(0.55), Q=mezcla(hx("3DFF7F"), hx("D8FFE2"), 0.6))
    return c


# ---------------------------------------------------------------------
#  Enganche: las animaciones «esp_<tema>_<j>» se registran en ANIMS; el objeto se dibuja con la pose (vía sombrero_lado).
# ---------------------------------------------------------------------
_actual = {}
_dibujar = g8.dibujar
_lado16 = g8.sombrero_lado


def dibujar(ps, acc):
    _actual["ps"] = ps
    return _dibujar(ps, acc)


ESCALA = dict(sostener=1.5, trabajar=1.4, levantar=1.45, olfatear=1.35, mirar=1.6, flotar=1.0)


class Escala:
    """Dibuja el objeto más grande: escala alrededor de su ancla (capas b_*) o de la pata (capas f_* de las herramientas)."""
    def __init__(self, L, c_b, c_f, s):
        self.L, self.c_b, self.c_f, self.s = L, c_b, c_f, s

    def _p(self, capa, x, y):
        c = self.c_f if capa.startswith("f_") else self.c_b
        return (c[0] + (x - c[0]) * self.s, c[1] + (y - c[1]) * self.s)

    def poligono(self, capa, pts):
        self.L.poligono(capa, [self._p(capa, *q) for q in pts])

    def elipse(self, capa, cx, cy, rx, ry, ang=0.0):
        self.L.elipse(capa, *self._p(capa, cx, cy), rx * self.s, ry * self.s, ang)

    def capsula(self, capa, pts, w):
        self.L.capsula(capa, [self._p(capa, *q) for q in pts], w * self.s)


def sombrero_lado(L, cx, cy, inc, som):
    _lado16(L, cx, cy, inc, som)
    ps = _actual.get("ps")
    if ps and ps.get("objeto"):
        f, a = ps["objeto"]
        f(Escala(L, a["obj"], a.get("pata", a["obj"]), ESCALA[a["accion"]]), a)


g8.dibujar, g8.sombrero_lado = dibujar, sombrero_lado

for nombre, lista in ESP.items():
    for j, (accion, f, _) in enumerate(lista):
        fn, n, _base = ACCIONES[accion]
        cuadros = []
        for k in range(n):
            p, a = fn(k)
            a["accion"] = accion
            p = dict(p, objeto=(f, a), semilla=k)
            cuadros.append(p)
        g8.ANIMS[f"esp_{nombre}_{j}"] = cuadros

_tema16 = g8.tema


def tema(nombre, oscuro=False):
    t = _tema16(nombre, oscuro)
    col = dict(t["col"])
    for j in range(2):
        pass
    c = colores(nombre, oscuro, 0, t)
    col.update(c)
    t = dict(t, col=col)
    return t


for nombre in ("gato8", "gato9", "gato10", "gato12", "gato13", "gato16"):
    sys.modules[nombre].tema = tema

# Los colores de U/V/X/Q cambian entre las dos animaciones de algunos temas: la rejilla es la misma, pero el sprite se pinta
# con los colores de esa animación.
_sprite8 = g8.sprite


def sprite(nombre, oscuro, t, anim, i, espejo):
    if anim.startswith("esp_"):
        j = int(anim.rsplit("_", 1)[1])
        col = dict(t["col"]); col.update(colores(nombre, oscuro, j, t))
        t = dict(t, col=col)
        k = (nombre, oscuro, anim, i, espejo)
        if k not in g8._spr:
            g = g8.rejilla(anim, i, t, espejo)
            g8._spr[k] = g8.pintar_plano(g, t, ESC) if t["material_banda"] == "plano" else g8.pintar(g, t, ESC, oscuro)
        return g8._spr[k]
    return _sprite8(nombre, oscuro, t, anim, i, espejo)


g8.sprite = sprite


# ---------------------------------------------------------------------
#  Una secuencia de muestra para la banda: camina, hace la animación 1, camina, hace la 2 (con las vueltas en los bordes).
# ---------------------------------------------------------------------

def secuencia(nombre, segundos=18.0, x0=150.0):
    DT, vel = g8.DT, g8.VEL
    cs, x, d, i = [], x0, 1, 0

    def caminar(seg):
        nonlocal x, d, i
        for _ in range(int(seg / DT)):
            x += d * vel * DT
            if not ((x + 58 <= W) if d > 0 else (x + 6 >= 0)):
                x -= d * vel * DT
                cs.extend([("girar", 0, x, d, 0)] * 3 + [("girar", 1, x, d, 0)] * 4)
                d = -d
                cs.extend([("girar", 0, x, d, 0)] * 3)
            cs.append(("caminar", i % 8, x, d, 0)); i += 1

    def especial(j):
        acc = ESP[nombre][j][0]
        n, base = ACCIONES[acc][1], ACCIONES[acc][2]
        if base == "sentado":
            for k in range(4):
                cs.extend([("sentarse", k, x, d, 0)] * 2)
            cs.extend([("sentado", 0, x, d, 0)] * 4)
        else:
            cs.extend([("quieto", 0, x, d, 0)] * 4)
        vueltas = 3 if n == 4 else 2
        for _ in range(vueltas):
            for k in range(n):
                cs.extend([(f"esp_{nombre}_{j}", k, x, d, 0)] * 3)
        if base == "sentado":
            cs.extend([("sentado", 0, x, d, 0)] * 3)
            for k in range(3, -1, -1):
                cs.extend([("sentarse", k, x, d, 0)] * 2)
        else:
            cs.extend([("quieto", 0, x, d, 0)] * 3)

    caminar(1.6); especial(0); caminar(2.6); especial(1); caminar(1.6)
    while len(cs) < int(segundos / DT):
        caminar(0.4)
    return cs[:int(segundos / DT)]


W = g8.ANCHO_DP


def hojas(archivo_base, por_hoja=7, cel=3):
    nombres = [n for n, _, _ in g8.BANDAS]
    lado = N * cel
    for h in range(0, len(nombres), por_hoja):
        grupo = nombres[h:h + por_hoja]
        ancho = 200 + 10 * (lado + 4) + 30
        img = Image.new("RGB", (ancho, 34 + len(grupo) * (lado + 26)), (250, 249, 246)); d = ImageDraw.Draw(img)
        d.text((10, 8), "Animaciones propias de cada tema (×3)", font=fuente("DejaVuSans-Bold.ttf", 16), fill=(25, 25, 28))
        for r, n in enumerate(grupo):
            o = dict(g8.BANDAS_dict)[n] if hasattr(g8, "BANDAS_dict") else [b[1] for b in g8.BANDAS if b[0] == n][0]
            t = tema(n, o)
            y = 34 + r * (lado + 26)
            d.rectangle([0, y - 2, ancho, y + lado + 20], fill=t["fondo"])
            d.text((10, y + lado // 2 - 18), g9.ETIQ.get(n, n).split(" · ")[0], font=fuente("DejaVuSans-Bold.ttf", 14), fill=t["texto"])
            x = 200
            for j in range(2):
                d.text((x, y + lado + 4), ESP[n][j][2], font=fuente("DejaVuSans.ttf", 11), fill=t["texto"])
                acc = ESP[n][j][0]
                for k in range(ACCIONES[acc][1]):
                    s = sprite(n, o, t, f"esp_{n}_{j}", k, False)
                    if s.mode == "RGBA":
                        img.paste(s, (x, y), s)
                    else:
                        base = img.crop((x, y, x + lado, y + lado)); img.paste(ImageChops.add(base, s), (x, y))
                    x += lado + 4
                x += 26
        img.save(AQUI / f"{archivo_base}_{h // por_hoja + 1}.png")


def resumen(ruta, columnas=3, escala=1.0):
    temas_ = [(n, o, et, tema(n, o)) for n, o, et in g8.BANDAS]
    fondos = {n: g8.FONDOS[n](t) for n, o, et, t in temas_}
    vin = g8.viñeta_ciber()
    secs = {n: secuencia(n) for n, o, et, t in temas_}
    filas = math.ceil(len(temas_) / columnas)
    bw, bh = int(W * escala), int(g8.ALTO_DP * escala)
    cw, ch_ = bw + 12, bh + 18
    out = []
    for k in range(len(next(iter(secs.values())))):
        lam = Image.new("RGB", (columnas * cw + 10, filas * ch_ + 8), (250, 249, 246)); dl = ImageDraw.Draw(lam)
        for j, (n, o, et, t) in enumerate(temas_):
            b = g8.cuadro_banda(n, o, t, fondos[n], secs[n][k], k, vin if n == "ciber" else None).resize((bw, bh), Image.LANCZOS)
            x, y = 10 + (j % columnas) * cw, 6 + (j // columnas) * ch_
            dl.text((x, y), et.split(" · ")[0], font=fuente("DejaVuSans.ttf", 11), fill=(40, 40, 46))
            lam.paste(b, (x, y + 14))
        out.append(lam)
    out[0].save(ruta, save_all=True, append_images=out[1:], duration=int(g8.DT * 1000), loop=0, quality=80, method=4)


if __name__ == "__main__":
    que = sys.argv[1:] or ["hojas", "bandas", "resumen"]
    if "hojas" in que:
        hojas("animaciones_tema")
    if "bandas" in que:
        for n in sys.argv[2:] or ("romano", "medieval", "espacio"):
            o = [b[1] for b in g8.BANDAS if b[0] == n][0]
            g8.gif_banda(secuencia(n), [(n, o, [b[2] for b in g8.BANDAS if b[0] == n][0])], "_especiales")
    if "resumen" in que:
        resumen(AQUI / "todas_las_bandas_especiales.webp")
    print("listo")
