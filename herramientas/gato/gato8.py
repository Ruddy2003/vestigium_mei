"""
Prueba 8 (vuelta por la espalda, sombreros y los 21 temas). Antes, prueba 7 (vuelta completa, juego, enojo y tristeza, pierna trasera estirada). Antes, prueba 6: gato animado con accesorio por tema, patas traseras de gato, giro en el borde y fondos que dan vida al material del tema que vive en una banda de 80 dp arriba de Día.
Sprite 64×64 armado con formas → cuadrícula. Animaciones por cuadros + máquina de estados con tiempos al azar.
Colores copiados de ui/Tema.kt (Paleta, ciber) y ui/Disenios.kt (juegoTonos y esquemas).
"""
import math
import random
from pathlib import Path

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
N = 64          # celdas del sprite
R = 8           # resolución por celda al rasterizar
SS = 4          # sobremuestreo al pintar materiales
F = "/usr/share/fonts/truetype/dejavu/"
fuente = lambda n, t: ImageFont.truetype(F + n, t)

# =====================================================================
#  Utilidades
# =====================================================================

def hx(s):
    return tuple(int(s[i:i + 2], 16) for i in (0, 2, 4))


def mezcla(a, b, t):
    return tuple(round(x + (y - x) * t) for x, y in zip(a, b))


def rot(cx, cy, x, y, a):
    ca, sa = math.cos(a), math.sin(a)
    return cx + x * ca - y * sa, cy + x * sa + y * ca


def catmull(pts, n=16):
    p = [pts[0]] + list(pts) + [pts[-1]]
    out = []
    for i in range(1, len(p) - 2):
        p0, p1, p2, p3 = p[i - 1], p[i], p[i + 1], p[i + 2]
        for k in range(n):
            t = k / n
            out.append(tuple(0.5 * ((2 * p1[j]) + (-p0[j] + p2[j]) * t + (2 * p0[j] - 5 * p1[j] + 4 * p2[j] - p3[j]) * t * t
                                    + (-p0[j] + 3 * p1[j] - 3 * p2[j] + p3[j]) * t ** 3) for j in range(2)))
    out.append(tuple(pts[-1]))
    return out

# =====================================================================
#  Lienzo de formas → cuadrícula (letra + parte del cuerpo por celda)
# =====================================================================
# capa: (letra, parte, umbral). El orden es el orden de dibujo.
CAPAS = [
    ("lejos", "D", "lejos", 0.45), ("cola", "P", "cola", 0.45),
    ("cuerpo", "P", "cuerpo", 0.45), ("claro", "L", "cuerpo", 0.45), ("juguete", "J", "juguete", 0.4), ("juguetel", "B", "juguete", 0.3), ("pierna", "P", "pierna", 0.45),
    ("pata", "T", "pierna", 0.45), ("collar", "C", "collar", 0.35), ("joya", "G", "collar", 0.3),
    ("capa", "A", "capa", 0.45), ("broche", "G", "capa", 0.3),
    ("cabeza", "P", "cabeza", 0.45), ("cabclaro", "L", "cabeza", 0.45), ("interior", "N", "cabeza", 0.4),
    ("ojo", "E", "cabeza", 0.3), ("rubor", "R", "cabeza", 0.3), ("linea", "K", "cabeza", 0.3), ("brillo", "H", "cabeza", 0.25),
    ("nariz", "N", "cabeza", 0.3), ("auri", "A", "auri", 0.4), ("auriband", "B", "auri", 0.3),
]


class Lienzo:
    def __init__(self):
        self.img = {c[0]: Image.new("L", (N * R, N * R), 0) for c in CAPAS}

    def _d(self, capa):
        return ImageDraw.Draw(self.img[capa])

    def poligono(self, capa, pts):
        self._d(capa).polygon([(x * R, y * R) for x, y in pts], fill=255)

    def elipse(self, capa, cx, cy, rx, ry, ang=0.0):
        a = math.radians(ang)
        self.poligono(capa, [rot(cx, cy, rx * math.cos(t), ry * math.sin(t), a) for t in np.linspace(0, 2 * math.pi, 40)])

    def capsula(self, capa, pts, w):
        d = self._d(capa)
        for (x0, y0), (x1, y1) in zip(pts, pts[1:]):
            d.line([x0 * R, y0 * R, x1 * R, y1 * R], fill=255, width=max(1, int(w * R)))
        for x, y in pts:
            d.ellipse([(x - w / 2) * R, (y - w / 2) * R, (x + w / 2) * R, (y + w / 2) * R], fill=255)

    def curva(self, capa, pts, anchos):
        """Curva suave con un ancho por punto de control."""
        d = self._d(capa)
        c = catmull(pts)
        n = len(c) - 1
        for i, (x, y) in enumerate(c):
            u = i / n * (len(anchos) - 1)
            k = min(int(u), len(anchos) - 2)
            w = anchos[k] + (anchos[k + 1] - anchos[k]) * (u - k)
            d.ellipse([(x - w / 2) * R, (y - w / 2) * R, (x + w / 2) * R, (y + w / 2) * R], fill=255)
        return c

    def rejilla(self):
        ch = [["."] * N for _ in range(N)]
        parte = [[None] * N for _ in range(N)]
        for capa, letra, p, u in CAPAS:
            a = np.asarray(self.img[capa], dtype=np.float32).reshape(N, R, N, R).mean(axis=(1, 3)) / 255
            for r, c in zip(*np.nonzero(a > u)):
                ch[r][c] = letra; parte[r][c] = p
        return ch, parte

# =====================================================================
#  Poses (mirando a la derecha). Una pose es un dict de números y puntos; se interpola entre poses.
# =====================================================================

def p_del(top, paw, lift=0.0):
    """Pata delantera: hombro, codo, muñeca, mano."""
    j1 = (top[0] + (paw[0] - top[0]) * 0.45 + 0.9 * lift, top[1] + (paw[1] - top[1]) * 0.45)
    j2 = (top[0] + (paw[0] - top[0]) * 0.78 + 0.6 * lift, top[1] + (paw[1] - top[1]) * 0.78 - 0.3 * lift)
    return [top, j1, j2, paw]


def p_tra(top, paw, lift=0.0):
    """Pata trasera estirada: cadera, rodilla (casi bajo la cadera), corvejón (atrás), pie."""
    rod = (top[0] + 2.2 + 0.3 * (paw[0] - top[0]), top[1] + 6.0)
    cor = (paw[0] - 1.4 + 0.3 * lift, paw[1] - 5.0 + 0.4 * lift)
    return [top, rod, cor, paw]


def pose(**k):
    base = dict(inc=0.0, orejas=0.0, ojo="abierto", lengua=False, viento=0.0, lomo=0.0, erizo=0.0, bola=None, semilla=0)
    base.update(k)
    return base


SUELO = 59.6
# Hombros y caderas de pie: patas algo más cortas que el lince, cuerpo más bajo (más gato).
HOM_F, HOM_FF, CAD_N, CAD_F = (40, 45), (36, 45), (21, 43), (24, 43)
PIE_F, PIE_FF, PIE_N, PIE_F2 = 41.5, 36, 20.5, 24
ANCHO_COLA = [3.8, 4.2, 4.4, 4.4, 4.2, 3.6]

DE_PIE = pose(H=(21, 41.5), rh=7.4, C=(38, 42), rc=8.2, cab=(45, 28.5),
              fn=p_del(HOM_F, (PIE_F, SUELO)), ff=p_del(HOM_FF, (PIE_FF, SUELO)),
              hn=p_tra(CAD_N, (PIE_N, SUELO)), hf=p_tra(CAD_F, (PIE_F2, SUELO)),
              cola=[(14, 39), (10, 34), (8.8, 27.5), (10.2, 21.5), (13.6, 18.6), (16.4, 20.4)], colaw=ANCHO_COLA)

SENTADO = pose(H=(25, 50), rh=9.0, C=(34, 40), rc=7.6, cab=(38.5, 27.5), inc=-4,
               fn=[(36, 43), (36.5, 50), (36.5, 55), (37, SUELO)], ff=[(32.5, 43), (32.5, 50), (32.5, 55), (33, SUELO)],
               hn=[(25, 51), (31, 54.5), (24, 57.5), (30, SUELO)], hf=[(27, 51), (32, 54.5), (26, 57.5), (32, SUELO)],
               cola=[(17, 54.5), (11, 56.8), (13, 58.8), (24, 59.2), (35, 58.8), (40.5, 57.2)], colaw=[3.8, 4.2, 4.4, 4.2, 4.0, 3.4])

DORMIDO = pose(H=(25, 52), rh=8.5, C=(37, 52.5), rc=7.5, cab=(42.5, 50.5), inc=18, orejas=0.35, ojo="cerrado",
               fn=[(41, 56), (44, 58.5), (47, 59.4), (48.5, SUELO)], ff=[(40, 56), (43, 58.5), (46, 59.4), (47.5, SUELO)],
               hn=[(25, 54), (29, 57), (26, 59), (29.5, SUELO)], hf=[(26, 54), (30, 57), (27, 59), (30.5, SUELO)],
               cola=[(16.5, 51), (11.5, 55.5), (16, 59.0), (28, 59.4), (41, 59.0), (50, 57)], colaw=[4.2, 4.6, 4.6, 4.4, 4.0, 3.4])


def interp(a, b, t):
    out = {}
    for k in a:
        va, vb = a[k], b[k]
        if isinstance(va, (int, float)) and not isinstance(va, bool):
            out[k] = va + (vb - va) * t
        elif isinstance(va, tuple):
            out[k] = tuple(x + (y - x) * t for x, y in zip(va, vb))
        elif isinstance(va, list):
            out[k] = [interp({"q": x}, {"q": y}, t)["q"] for x, y in zip(va, vb)]
        else:
            out[k] = va if t < 0.5 else vb
    return out


def mover(p, dx=0.0, dy=0.0, partes=("H", "C", "cab", "fn", "ff", "hn", "hf", "cola")):
    q = dict(p)
    for k in partes:
        v = q[k]
        q[k] = (v[0] + dx, v[1] + dy) if isinstance(v, tuple) else [(x + dx, y + dy) for x, y in v]
    return q


def caminar(k, n=8):
    f = k / n
    def pie(base_x, desfase):
        u = (f + desfase) % 1
        if u < 0.6:
            return base_x + 3.6 - 7.2 * (u / 0.6), SUELO, 0.0
        v = (u - 0.6) / 0.4
        return base_x - 3.6 + 7.2 * v, SUELO - 2.4 * math.sin(math.pi * v), math.sin(math.pi * v)
    bob = -0.5 * abs(math.sin(2 * math.pi * f * 2))
    p = mover(DE_PIE, 0, bob, ("H", "C", "cab", "cola"))
    x, y, l = pie(PIE_N, 0.0);   p["hn"] = p_tra((CAD_N[0], CAD_N[1] + bob), (x, y), l)
    x, y, l = pie(PIE_F, 0.25);  p["fn"] = p_del((HOM_F[0], HOM_F[1] + bob), (x, y), l)
    x, y, l = pie(PIE_F2, 0.5);  p["hf"] = p_tra((CAD_F[0], CAD_F[1] + bob), (x, y), l)
    x, y, l = pie(PIE_FF, 0.75); p["ff"] = p_del((HOM_FF[0], HOM_FF[1] + bob), (x, y), l)
    s = math.sin(2 * math.pi * f)
    p["cola"] = [(x + s * 0.25 * i, y) for i, (x, y) in enumerate(p["cola"])]
    p["cab"] = (p["cab"][0], p["cab"][1] + 0.3 * math.sin(2 * math.pi * f * 2))
    p["viento"] = 0.35 + 0.15 * math.sin(2 * math.pi * f * 2)
    return p


def respirar(base, k, n, amp=0.35, cola=0.8):
    s = math.sin(2 * math.pi * k / n)
    p = dict(base)
    p["rh"] += amp * s; p["rc"] += amp * s
    p["cab"] = (p["cab"][0], p["cab"][1] - 0.25 * s)
    p["cola"] = [(x + cola * s * (i / 5) ** 2, y) for i, (x, y) in enumerate(p["cola"])]
    return p


AGACHADO = mover(DE_PIE, 0, 2.5, ("H", "C", "cab", "cola"))
AGACHADO.update(fn=p_del((40, 47.5), (42.5, SUELO), 0.5), ff=p_del((36, 47.5), (37, SUELO), 0.5), inc=4,
                hn=[(21, 47), (25.5, 52.5), (19.5, 55.5), (20.5, SUELO)], hf=[(24, 47), (28.5, 52.5), (22.5, 55.5), (24, SUELO)])
IMPULSO = pose(H=(22, 41), rh=7.6, C=(37, 35), rc=8.0, cab=(43.5, 23.5), inc=-12, viento=0.6,
               fn=[(39, 38), (43, 35.5), (46, 33.5), (48, 32.5)], ff=[(36, 39), (40, 37), (43, 35.5), (45, 35)],
               hn=[(22, 43), (18, 48.5), (13, 54), (10, SUELO)], hf=[(24, 43), (20, 48.5), (15, 54), (12, SUELO)],
               cola=[(14, 38), (9, 36), (5, 32.5), (3.2, 28), (4, 23.5), (6, 21)], colaw=ANCHO_COLA)
AIRE = pose(H=(20, 37), rh=7.4, C=(37, 35.5), rc=7.9, cab=(44, 26), inc=-2, viento=1.0,
            fn=[(40, 38.5), (45, 40.5), (49, 41.5), (52, 42)], ff=[(37, 38.5), (42, 41.5), (46, 43), (49, 43.5)],
            hn=[(20, 39), (15, 42), (10, 44), (6, 45.5)], hf=[(22, 39), (17, 42.5), (12, 45), (8, 47)],
            cola=[(13, 35), (8, 34), (4, 32), (2, 29), (2.2, 25), (3.6, 22.5)], colaw=ANCHO_COLA)
BAJADA = pose(H=(20, 34.5), rh=7.4, C=(36, 38.5), rc=7.9, cab=(43, 30), inc=10, viento=0.8,
              fn=[(39, 42.5), (41, 48), (42, 53), (43, 56.5)], ff=[(36, 42.5), (38, 48), (39, 53), (40, 56.5)],
              hn=[(20, 36.5), (24, 41), (18, 43), (21, 46)], hf=[(22, 36.5), (26, 41), (20, 43), (23, 46)],
              cola=[(13, 31.5), (9, 27.5), (8, 22), (10, 17), (13.6, 15), (16, 17)], colaw=ANCHO_COLA)
CAIDA = pose(H=(21, 42.5), rh=7.8, C=(38, 44.5), rc=8.2, cab=(45, 32), inc=8, viento=0.4,
             fn=p_del((40, 47.5), (43, SUELO), 0.6), ff=p_del((36, 47.5), (38, SUELO), 0.6),
             hn=[(21, 44.5), (25, 51), (20, 54), (22, SUELO)], hf=[(24, 44.5), (28, 51), (23, 54), (25.5, SUELO)],
             cola=[(14, 39), (9.5, 35), (7.5, 28.5), (9, 22.5), (12.6, 19.5), (15.6, 21)], colaw=ANCHO_COLA)
SALTO = [AGACHADO, IMPULSO, AIRE, BAJADA, CAIDA, DE_PIE]


def aseo(paw, inc, ojo="cerrado", lengua=False, cab_dy=0.0):
    p = dict(SENTADO)
    p["fn"] = [(36, 43), ((36 + paw[0]) / 2 + 3, (43 + paw[1]) / 2 + 3), ((36 + paw[0]) / 2 + 4.5, (43 + paw[1]) / 2 - 1), paw]
    p.update(inc=inc, ojo=ojo, lengua=lengua, cab=(SENTADO["cab"][0], SENTADO["cab"][1] + cab_dy))
    return p


ASEO = [aseo((40, 53), -2, "abierto"), aseo((43.6, 35.0), 18, lengua=True, cab_dy=1.0), aseo((43.0, 35.9), 24, lengua=True, cab_dy=1.4),
        aseo((43.6, 35.0), 18, lengua=True, cab_dy=1.0), aseo((41.6, 30.4), 30, cab_dy=1.6), aseo((43.0, 32.5), 22, cab_dy=1.2)]


# ---- Jugar (cuando estás alegre o divertido): reverencia de juego, menea el trasero, salta sobre el ovillo, lo atrapa y lo lanza.
BOLA = (56, 57.2)
JUEGO = [
    pose(H=(22, 38.5), rh=7.4, C=(37, 46.5), rc=7.4, cab=(44.5, 40.5), inc=4, bola=BOLA,
         fn=[(39, 50), (43, 54.5), (47, 58), (50, SUELO)], ff=[(36, 50), (40, 54.5), (44, 58), (47, SUELO)],
         hn=p_tra((21, 40.5), (20, SUELO)), hf=p_tra((24, 40.5), (23.5, SUELO)),
         cola=[(15, 34), (11.5, 28), (10.5, 21), (11.5, 15), (14, 12), (16.5, 13)], colaw=ANCHO_COLA),
    None,  # meneo (se arma abajo)
    pose(H=(25, 37), rh=7.2, C=(40, 38), rc=7.6, cab=(47, 30), inc=6, viento=0.8, bola=BOLA,
         fn=[(43, 42), (47, 45.5), (50, 49), (52, 52)], ff=[(40, 42), (44, 46), (47, 50), (49, 53)],
         hn=[(24, 39), (19, 43), (15, 46), (11, 48)], hf=[(26, 39), (21, 43.5), (17, 47), (13, 49.5)],
         cola=[(18, 35), (13, 33), (9, 31), (7, 27.5), (7.5, 23.5), (9.5, 21)], colaw=ANCHO_COLA),
    pose(H=(25, 40), rh=7.4, C=(41, 47), rc=7.4, cab=(48, 39.5), inc=14, bola=BOLA,
         fn=[(44, 50), (48, 52), (52, 53.5), (55, 54.4)], ff=[(41, 50), (45, 53), (49, 55), (52, 56)],
         hn=p_tra((24, 42), (23, SUELO)), hf=p_tra((27, 42), (26.5, SUELO)),
         cola=[(18, 36), (14, 31), (13, 24), (14.5, 18), (17.5, 16), (19.5, 18)], colaw=ANCHO_COLA),
    None, None,  # manotazo y mirar la bola (se arman abajo)
]
_j1 = mover(JUEGO[0], 0.8, -0.4, ("H",)); _j1["cola"] = [(x + (1.6 if i >= 3 else 0.6 * i / 3), y) for i, (x, y) in enumerate(JUEGO[0]["cola"])]
JUEGO[1] = _j1
_j4 = dict(DE_PIE); _j4.update(fn=[(40, 45), (44, 46), (47.5, 46.5), (50, 47.5)], inc=-6, bola=(59, 57.2))
JUEGO[4] = _j4
_j5 = respirar(DE_PIE, 1, 4); _j5.update(inc=-14, bola=(61, 57.2))
JUEGO[5] = _j5

# ---- Enojado: lomo arqueado, cola erizada y parada, orejas hacia atrás, bufido. Tiembla un poco.
def enojado(k):
    p = pose(H=(20.5 + 0.25 * (k % 2), 41), rh=7.4, C=(37.5, 41.5), rc=8.0, cab=(44.5, 31.5), inc=10, orejas=0.95, lengua=True,
             lomo=5.5, erizo=1.0, semilla=k,
             fn=p_del((40, 45), (42.5, SUELO)), ff=p_del((36, 45), (37, SUELO)),
             hn=p_tra((20.5, 43), (19, SUELO)), hf=p_tra((23.5, 43), (22.5, SUELO)),
             cola=[(14, 37), (12.5, 31), (12, 25), (12.4 + 0.3 * k, 19), (13.4 + 0.4 * k, 14), (14.6 + 0.4 * k, 10.5)],
             colaw=[5.2, 6.4, 7, 7, 6.6, 5.4])
    return p

# ---- Triste: cola caída que roza el suelo, cabeza baja, orejas bajas, ojos entrecerrados.
COLA_BAJA = [(14, 40), (9, 42.5), (5.5, 46.5), (4, 51), (4.4, 55.5), (6.8, 58)]
def triste(p, k=0, n=8):
    q = dict(p)
    s = math.sin(2 * math.pi * k / n)
    q["cola"] = [(x + 0.4 * s * (i / 5), y) for i, (x, y) in enumerate(COLA_BAJA)]
    q["cab"] = (p["cab"][0] - 0.5, p["cab"][1] + 3.0)
    q.update(inc=12, orejas=0.45, ojo="medio", viento=0.1)
    return q

ANIMS = {
    "jugar": JUEGO,
    "enojado": [enojado(k) for k in range(3)],
    "caminar_triste": [triste(caminar(k), k) for k in range(8)],
    "quieto_triste": [triste(respirar(DE_PIE, k, 4), k, 4) for k in range(4)],
    "caminar": [caminar(k) for k in range(8)],
    "quieto": [respirar(DE_PIE, k, 4) for k in range(4)],
    "sentarse": [interp(DE_PIE, SENTADO, t) for t in (0.25, 0.5, 0.75, 1.0)],
    "sentado": [respirar(SENTADO, k, 2, 0.3, 0.5) for k in range(2)],
    "echarse": [interp(SENTADO, DORMIDO, t) for t in (0.34, 0.67, 1.0)],
    "dormir": [respirar(DORMIDO, k, 3, 0.45, 0.3) for k in range(3)],
    "saltar": SALTO,
    "asearse": ASEO,
}
for p in ANIMS["dormir"]:
    p["ojo"] = "cerrado"

# =====================================================================
#  Dibujo de una pose
# =====================================================================

def cabeza(L, cx, cy, inc, orejas, ojo, lengua, acc):
    """Cabeza redonda de gato: hocico corto, mejilla llena, ojos grandes con brillo, orejas anchas."""
    a = math.radians(inc)
    p = lambda x, y: rot(cx, cy, x, y, a)
    o = orejas
    pt = lambda u, v: (u[0] + (v[0] - u[0]) * o, u[1] + (v[1] - u[1]) * o)
    lej = [(-5.4, -2.4), pt((-3.8, -9.0), (-9.2, -4.4)), (-0.8, -5.0)]
    cer = [(-0.8, -5.0), pt((1.8, -9.8), (-5.8, -7.4)), (4.6, -3.4)]
    dentro = [(0.4, -4.8), pt((1.8, -8.2), (-4.4, -6.6)), (3.2, -3.9)]
    L.poligono("lejos", [p(*q) for q in lej])
    L.elipse("cabeza", cx, cy, 6.6, 5.8, inc)
    L.elipse("cabeza", *p(-2.2, 3.0), 3.4, 2.4, inc)          # mejilla
    L.elipse("cabeza", *p(4.4, 1.7), 2.5, 2.0, inc)           # hocico corto
    L.poligono("cabeza", [p(*q) for q in cer])
    L.poligono("interior", [p(*q) for q in dentro])
    L.elipse("cabclaro", *p(4.2, 2.9), 2.0, 1.1, inc)         # barbilla
    if ojo == "abierto":
        L.elipse("ojo", *p(2.4, -0.7), 1.35, 1.55, inc)
        L.elipse("linea", *p(2.9, -0.5), 0.6, 0.95, inc)
        L.elipse("brillo", *p(2.0, -1.5), 0.45, 0.45)
    elif ojo == "medio":
        L.elipse("ojo", *p(2.4, -0.2), 1.35, 0.8, inc)
        L.capsula("linea", [p(1.0, -0.8), p(3.8, -0.6)], 0.6)
        L.elipse("linea", *p(2.9, -0.1), 0.5, 0.45, inc)
    else:
        L.capsula("linea", [p(1.1, -0.4), p(2.4, 0.4), p(3.8, -0.3)], 0.65)
    L.elipse("rubor", *p(2.0, 2.6), 1.3, 0.65, inc)
    L.elipse("nariz", *p(6.6, 1.0), 0.6, 0.5)
    L.capsula("linea", [p(6.2, 2.8), p(5.2, 3.3)], 0.5)
    if lengua:
        L.elipse("nariz", *p(6.2, 3.7), 0.95, 1.2, inc)
    if acc == "auriculares":
        # Audífonos: arco sobre la cabeza y copa en el costado.
        L.capsula("auriband", [p(-2.4, -3.0), p(-1.8, -6.6), p(0.4, -7.4), p(1.6, -5.6)], 1.3)
        L.elipse("auri", *p(-1.9, -0.4), 2.6, 3.0, inc)
        L.elipse("auriband", *p(-1.9, -0.4), 0.9, 1.5, inc)


def _cuello(C, cab):
    a = (C[0] + 2, C[1] - 5); b = (cab[0] - 1, cab[1] + 3.5)
    dx, dy = b[0] - a[0], b[1] - a[1]; n = math.hypot(dx, dy) or 1
    return a, b, -dy / n, dx / n


def collar(L, C, cab, acc):
    a, b, px, py = _cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.55, a[1] + (b[1] - a[1]) * 0.55)
    e1, e2 = (m[0] - 4.4 * px, m[1] - 4.4 * py), (m[0] + 4.4 * px, m[1] + 4.4 * py)
    L.capsula("collar", [e1, e2], 1.6)
    j = max((e1, e2), key=lambda q: q[0] + q[1])
    if acc == "colgante":   # gótico: colgante de rosetón con rubí al centro
        L.elipse("joya", j[0] - 0.4 * px, j[1] + 2.4, 2.2, 2.2)
        L.elipse("interior", j[0] - 0.4 * px, j[1] + 2.4, 0.95, 0.95)
    else:
        L.elipse("joya", j[0] - 0.6 * px, j[1] - 0.6 * py + 0.8, 1.0, 1.0)


def paliacate(L, C, cab):
    """Paliacate árabe (kufiya) anudado al cuello: banda ancha, triángulo grande sobre el pecho y puntas del nudo atrás."""
    a, b, px, py = _cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.45, a[1] + (b[1] - a[1]) * 0.45)
    e1, e2 = (m[0] - 4.8 * px, m[1] - 4.8 * py), (m[0] + 4.8 * px, m[1] + 4.8 * py)
    L.capsula("capa", [e1, e2], 4.0)
    fr = max((e1, e2), key=lambda q: q[0] + q[1]); at = e1 if fr is e2 else e2
    L.poligono("capa", [(fr[0] - 5.0 * (fr[0] - at[0]) / 9.6, fr[1] - 5.0 * (fr[1] - at[1]) / 9.6 - 0.5), (fr[0] + 1.8, fr[1] + 0.2),
                        (fr[0] + 0.4, fr[1] + 9.6)])
    L.poligono("capa", [at, (at[0] - 4.4, at[1] + 2.2), (at[0] - 1.6, at[1] + 3.6)])
    L.poligono("capa", [at, (at[0] - 4.6, at[1] - 0.6), (at[0] - 3.6, at[1] + 1.6)])


def capa(L, ps):
    """Capita roja: nace del cuello (fíbula de oro al frente), cubre los hombros y se va hacia atrás con el viento."""
    H, C, rh, rc, v = ps["H"], ps["C"], ps["rh"], ps["rc"], ps["viento"]
    a, b, px, py = _cuello(C, ps["cab"])
    m = (a[0] + (b[0] - a[0]) * 0.3, a[1] + (b[1] - a[1]) * 0.3)
    e1, e2 = (m[0] - 4.6 * px, m[1] - 4.6 * py), (m[0] + 4.6 * px, m[1] + 4.6 * py)
    fr = max((e1, e2), key=lambda q: q[0] + q[1]); at = e1 if fr is e2 else e2
    ct, ht = (C[0], C[1] - rc), (H[0], H[1] - rh)
    dx, dy = ht[0] - ct[0], ht[1] - ct[1]; n = math.hypot(dx, dy) or 1
    ux, uy = dx / n, dy / n                 # hacia la cola
    nx, ny = -uy, ux                        # hacia arriba (afuera del lomo)
    lp = lambda t, up=0.0, back=0.0: (ct[0] + dx * t + nx * up + ux * back, ct[1] + dy * t + ny * up + uy * back)
    pts = [fr, at, lp(0.08, 1.4), lp(0.45, 1.0 + 1.5 * v, 1.5 * v), lp(0.7, 0.6 + 3.0 * v, 3.5 * v),
           lp(0.66, -rh * 0.55 + 2.0 * v, 3.0 * v), lp(0.52, -rc * 0.75 + 1.0 * v, 1.2 * v), lp(0.36, -rc * 0.55),
           lp(0.2, -rc * 0.9), lp(0.02, -rc * 0.75)]
    L.poligono("capa", pts)
    L.capsula("capa", [fr, at], 2.2)
    L.elipse("broche", fr[0] + 0.4, fr[1] + 0.3, 1.3, 1.3)


def chaleco(L, ps):
    """Ciber: chaleco ajustado oscuro con orilla de neón magenta y una franja de luz amarilla."""
    H, C, rh, rc = ps["H"], ps["C"], ps["rh"], ps["rc"]
    ct, ht = (C[0], C[1] - rc), (H[0], H[1] - rh)
    dx, dy = ht[0] - ct[0], ht[1] - ct[1]; n = math.hypot(dx, dy) or 1
    nx, ny = -dy / n, dx / n
    lp = lambda t, up=0.0: (ct[0] + dx * t + nx * up, ct[1] + dy * t + ny * up)
    L.poligono("capa", [lp(-0.04, 0.9), lp(0.5, 0.9), lp(0.56, -rh * 0.95), lp(0.3, -rc * 1.15), lp(0.02, -rc * 1.1),
                        (C[0] + rc * 0.75, C[1] - rc * 0.05), (C[0] + rc * 0.6, C[1] - rc * 0.75)])
    L.capsula("broche", [lp(0.06, -rc * 0.62), lp(0.46, -rc * 0.5)], 0.9)   # franja de luz (no puntos: parecían ojos)


def dibujar(ps, acc):
    L = Lienzo()
    H, C, cab = ps["H"], ps["C"], ps["cab"]
    L.curva("lejos", ps["ff"], [3.8, 3.4, 3.0, 2.9])
    L.capsula("lejos", ps["hf"][:2], 4.4)
    L.curva("lejos", ps["hf"][1:], [4.0, 3.0, 2.8])
    for k in ("ff", "hf"):
        L.elipse("lejos", ps[k][-1][0] + 0.6, ps[k][-1][1] - 0.2, 2.2, 1.2)
    c = L.curva("cola", ps["cola"], ps["colaw"])
    rnd = random.Random(ps["semilla"])
    if ps["erizo"] > 0:   # cola erizada: púas hacia afuera a lo largo de la cola
        for i in range(4, len(c) - 1, 3):
            (x0, y0), (x1, y1) = c[i - 1], c[i + 1]
            dx, dy = x1 - x0, y1 - y0; n = math.hypot(dx, dy) or 1
            nx, ny = -dy / n, dx / n
            w = ps["colaw"][min(5, int(i / (len(c) - 1) * 5))] / 2
            for lado in (-1, 1):
                l = w + ps["erizo"] * rnd.uniform(1.0, 2.4)
                L.poligono("cola", [(c[i][0] - dx / n * 0.9, c[i][1] - dy / n * 0.9), (c[i][0] + lado * nx * l, c[i][1] + lado * ny * l),
                                    (c[i][0] + dx / n * 0.9, c[i][1] + dy / n * 0.9)])
    L.capsula("cuerpo", [H, C], ps["rh"] + ps["rc"] - 1)
    L.elipse("cuerpo", *H, ps["rh"], ps["rh"]); L.elipse("cuerpo", *C, ps["rc"], ps["rc"])
    mx, my = (H[0] + C[0]) / 2, (H[1] + C[1]) / 2
    if ps["lomo"] > 0:    # lomo arqueado (enojo)
        L.elipse("cuerpo", mx, my - ps["lomo"], math.hypot(C[0] - H[0], C[1] - H[1]) * 0.42, (ps["rh"] + ps["rc"]) / 2 * 0.8)
        if ps["erizo"] > 0:
            for k in range(5):
                x = mx - 6 + 3 * k; top = my - ps["lomo"] - (ps["rh"] + ps["rc"]) / 2 * 0.8 + 0.4
                L.poligono("cuerpo", [(x - 1.0, top + 1.2), (x + rnd.uniform(-0.4, 0.4), top - ps["erizo"] * rnd.uniform(1.0, 1.8)), (x + 1.0, top + 1.2)])
    if ps["bola"] is not None:   # ovillo
        bx, by = ps["bola"]
        L.elipse("juguete", bx, by, 2.6, 2.6)
        L.capsula("juguetel", [(bx - 1.8, by - 0.6), (bx + 1.6, by + 1.0)], 0.5)
        L.capsula("juguetel", [(bx - 0.8, by - 2.0), (bx + 0.6, by + 1.9)], 0.5)
    ang = math.degrees(math.atan2(C[1] - H[1], C[0] - H[0]))
    L.elipse("claro", mx + 1, my + (ps["rh"] + ps["rc"]) * 0.33, math.hypot(C[0] - H[0], C[1] - H[1]) * 0.42, 1.8, ang)
    L.capsula("cuerpo", [(C[0] + 2, C[1] - 5), (cab[0] - 1, cab[1] + 3.5)], 9)
    L.elipse("claro", C[0] + ps["rc"] * 0.5, C[1] - ps["rc"] * 0.3, 2.8, 4.0, 15)       # pecho claro
    # Muslo trasero: es parte del cuerpo (sin contorno propio, que lo hacía ver de juguete); la pierna sale de él y se afina.
    hip, rod = ps["hn"][0], ps["hn"][1]
    L.elipse("cuerpo", (hip[0] + rod[0]) / 2, (hip[1] + rod[1]) / 2 - 0.8, 4.0, 4.8,
             math.degrees(math.atan2(rod[1] - hip[1], rod[0] - hip[0])) - 90)
    L.curva("pierna", ps["hn"][1:], [3.8, 3.0, 2.8])
    L.curva("pierna", ps["fn"], [4.0, 3.5, 3.1, 3.0])
    for k in ("hn", "fn"):
        q = ps[k]; x, y = q[-1]
        dx, dy = x - q[-2][0], y - q[-2][1]; n = math.hypot(dx, dy) or 1
        L.capsula("pata", [(x - dx / n * 2.4, y - dy / n * 2.4), (x, y)], 3.2)
        L.elipse("pata", x + 0.7, y - 0.2, 2.3, 1.2)
    if acc in ("collar", "colgante", "auriculares", "chaleco"):
        collar(L, C, cab, acc)
    elif acc == "paliacate":
        paliacate(L, C, cab)
    elif acc == "capa":
        capa(L, ps)
    if acc == "chaleco":
        chaleco(L, ps)
    cabeza(L, cab[0], cab[1], ps["inc"], ps["orejas"], ps["ojo"], ps["lengua"], acc)
    return L.rejilla()

# =====================================================================
#  Pelaje (patrón del tema) y acabado: contornos internos, contorno exterior, sombra inferior
# =====================================================================
RANGO = {"lejos": 0, "cola": 1, "cuerpo": 2, "pierna": 3, "collar": 4, "capa": 4.5, "cabeza": 5, "auri": 6}


def acabar(ch, parte, estilo):
    rayas, acc = estilo
    afuera = set(); pila = [(r, c) for r in range(N) for c in (0, N - 1)] + [(r, c) for c in range(N) for r in (0, N - 1)]
    while pila:
        r, c = pila.pop()
        if (r, c) in afuera or not (0 <= r < N and 0 <= c < N) or ch[r][c] != ".": continue
        afuera.add((r, c)); pila += [(r + 1, c), (r - 1, c), (r, c + 1), (r, c - 1)]
    ch = [f[:] for f in ch]; parte = [f[:] for f in parte]
    for r in range(N):
        for c in range(N):
            if ch[r][c] == "." and (r, c) not in afuera:
                ch[r][c], parte[r][c] = "P", "cuerpo"
    g = [f[:] for f in ch]
    for r in range(N):
        for c in range(N):
            if g[r][c] == "P" and parte[r][c] in ("cuerpo", "cola", "pierna"):
                if rayas == "atigrado" and (c + 0.2 * r + 1.2 * math.sin(r * 0.5)) % 6 < 1.4:
                    g[r][c] = "W"
                elif rayas == "mechones" and (c + r) % 4 == 0 and (c * 7 + r * 13) % 17 < 7:
                    g[r][c] = "W"
            if acc == "paliacate" and g[r][c] == "A" and r % 3 == 0 and c % 2 == 0:
                g[r][c] = "Y"                                     # cuadrícula de la kufiya
    out = [f[:] for f in g]
    dentro = lambda r, c: 0 <= r < N and 0 <= c < N and ch[r][c] != "."
    for r in range(N):
        for c in range(N):
            if g[r][c] in "PLWAY" and parte[r][c] in RANGO and parte[r][c] not in ("collar", "auri"):
                for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    rr, cc = r + dr, c + dc
                    if dentro(rr, cc) and parte[rr][cc] in RANGO and RANGO[parte[rr][cc]] < RANGO[parte[r][c]] and not (parte[r][c] == "pierna" and parte[rr][cc] == "cuerpo"):
                        out[r][c] = "S" if g[r][c] in "PLW" else "B"; break
    for r in range(N):
        for c in range(N):
            if ch[r][c] != "." and ch[r][c] != "K" and any(not dentro(r + dr, c + dc) for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                out[r][c] = "K"
    for r in range(N - 1):
        for c in range(N):
            if out[r][c] in "PLW" and out[r + 1][c] == "K":
                out[r][c] = "S"
            elif out[r][c] in "AY" and out[r + 1][c] == "K":
                out[r][c] = "B"
    return out

# =====================================================================
#  Temas
# =====================================================================

def tema(nombre, oscuro):
    """Colores del gato y del fondo, sacados de juegoTonos y de los esquemas del tema."""
    if nombre == "romano":
        # juegoTonos(ROMANO) claro: 9E3445 E4E2DA 2D7A6F B8892B · Paleta: marmol ECEDEA, veta C9CBC4, tinta 17191C, oroOscuro 8E6A1F, oro D2AE5A
        if not oscuro:
            P = mezcla(hx("F3EEE2"), hx("D2AE5A"), 0.5)
            S = mezcla(P, hx("8E6A1F"), 0.45)
            K = mezcla(hx("8E6A1F"), hx("17191C"), 0.6)
            col = dict(P=P, L=hx("F8F4EA"), W=mezcla(P, hx("B8892B"), 0.55), S=S, K=K, D=mezcla(S, K, 0.3), T=hx("6B5330"),
                       E=hx("2D7A6F"), N=mezcla(hx("9E3445"), hx("F7F7F4"), 0.45), R=P, C=hx("9E3445"), G=hx("D2AE5A"))
            return dict(col=col, fondo=hx("ECEDEA"), borde=hx("C9CBC4"), sombra=mezcla(hx("C9CBC4"), hx("17191C"), 0.15),
                        junta=hx("C9CBC4"), rayas="atigrado", material="romano", texto=hx("17191C"))
        # oscuro: juegoTonos A63A4D 2A2F34 3FA393 D2AE5A · basalto 0E1113, basaltoVeta 20252A, marfil E7E3DA
        P = mezcla(hx("E7E3DA"), hx("D2AE5A"), 0.55)
        S = mezcla(P, hx("8E6A1F"), 0.5)
        K = hx("2A2014")
        col = dict(P=P, L=hx("F2EEE6"), W=mezcla(P, hx("B8892B"), 0.5), S=S, K=K, D=mezcla(S, K, 0.35), T=hx("7A5E36"),
                   E=hx("3FA393"), N=mezcla(hx("A63A4D"), hx("E7E3DA"), 0.35), R=P, C=hx("A63A4D"), G=hx("F2D27A"))
        return dict(col=col, fondo=hx("0E1113"), borde=hx("2B3137"), sombra=hx("1B2024"), junta=hx("20252A"),
                    rayas="atigrado", material="romano", texto=hx("E7E3DA"))
    if nombre == "gotico":
        if not oscuro:
            # goticoClaro: fondo EDE7DA, tinta 141A33, primario 1F3A9E, secundario B3122E, terciario B8892B
            P = mezcla(hx("141A33"), hx("4F5570"), 0.45)
            col = dict(P=P, L=mezcla(P, hx("A8AEC8"), 0.35), W=mezcla(P, hx("4F5570"), 0.5), S=hx("141A33"), K=hx("07091A"),
                       D=mezcla(hx("141A33"), hx("07091A"), 0.3), T=hx("141A33"), E=hx("E2B34A"), N=hx("B3122E"), R=P, C=hx("B3122E"), G=hx("E2B34A"))
            return dict(col=col, fondo=hx("EDE7DA"), borde=hx("C9BC9F"), sombra=mezcla(hx("EDE7DA"), hx("141A33"), 0.15),
                        plomo=hx("2A2B33"), rayas="mechones", material="gotico", texto=hx("141A33"))
        P = mezcla(hx("26306E"), hx("5C8DFF"), 0.3)
        col = dict(P=P, L=mezcla(P, hx("B0AFC8"), 0.3), W=mezcla(P, hx("5C8DFF"), 0.35), S=hx("1A2356"), K=hx("05070F"),
                   D=hx("111842"), T=hx("1A2356"), E=hx("E2B34A"), N=hx("E0405E"), R=P, C=hx("E0405E"), G=hx("E2B34A"))
        return dict(col=col, fondo=hx("0A0F2A"), borde=hx("2E3A7A"), sombra=hx("111842"), plomo=hx("05070F"),
                    rayas="mechones", material="gotico", texto=hx("F0E9D8"))
    if nombre == "medieval":
        # medievalClaro/Oscuro: pergamino, tinta 2A1B10, rúbrica 9E1B1B / C9473B, lapislázuli 1F3F8F / 8DA5E0, oro A97C1E / D5A84A
        claro = not oscuro
        P = hx("F8F3E8") if claro else hx("F3EAD6")
        S = mezcla(P, hx("8DA5E0"), 0.55)
        K = mezcla(hx("1F3F8F"), hx("2A1B10"), 0.4)
        rojo = hx("9E1B1B") if claro else hx("C9473B")
        oro = mezcla(hx("A97C1E"), hx("F2D27A"), 0.35) if claro else hx("D5A84A")
        col = dict(P=P, L=hx("FFFFFF"), W=mezcla(P, hx("8DA5E0"), 0.3), S=S, K=K, D=mezcla(S, hx("1F3F8F"), 0.3), T=S,
                   E=hx("1F3F8F"), N=mezcla(rojo, hx("FFFFFF"), 0.45), R=P, C=rojo, G=oro)
        fondo = hx("EFE3C8") if claro else hx("15100A")
        return dict(col=col, fondo=fondo, borde=hx("CDB98E") if claro else hx("3F3122"), sombra=mezcla(fondo, hx("2A1B10"), 0.15 if claro else 0.0),
                    filete=oro, rayas="mechones", material="medieval", texto=hx("2A1B10") if claro else hx("EDDFC2"))
    if nombre == "ciber":
        # ciber (solo oscuro): fondo 05070B, cian 00E5FF, magenta FF2E88, amarillo E8FF3A, texto D6F3F7
        cian, fondo = hx("00E5FF"), hx("05070B")
        col = dict(P=mezcla(hx("D6F3F7"), cian, 0.12), L=hx("FFFFFF"), W=mezcla(cian, hx("FFFFFF"), 0.45), S=mezcla(cian, hx("0A0E15"), 0.5),
                   K=mezcla(cian, fondo, 0.25), D=mezcla(cian, fondo, 0.55), T=mezcla(hx("FF2E88"), fondo, 0.2),
                   E=hx("E8FF3A"), N=hx("FF2E88"), R=hx("FF2E88"), C=hx("FF2E88"), G=hx("E8FF3A"))
        return dict(col=col, fondo=fondo, borde=hx("15404A"), sombra=hx("0E1622"), rayas="mechones", material="ciber", texto=hx("D6F3F7"))
    raise ValueError(nombre)

_tema_base = tema


def tema(nombre, oscuro):
    """Base de la prueba 4 + ojos con brillo, rubor leve y el accesorio del tema."""
    t = _tema_base(nombre, oscuro)
    col = dict(t["col"])
    col["H"] = (255, 255, 255)
    if nombre != "ciber":
        col["R"] = mezcla(col["N"], col["P"], 0.35)
    col["Y"] = col["L"]
    if nombre == "romano":
        # Capita roja: el rojo bajo de juegoTonos con orilla de pórfido (7C2433); fíbula de oro (G).
        col.update(A=hx("9E3445") if not oscuro else hx("A63A4D"), B=hx("7C2433") if not oscuro else hx("5E1B27"))
        acc = "capa"
    elif nombre == "gotico":
        col.update(A=col["C"], B=col["S"]); acc = "colgante"
    elif nombre == "medieval":
        rojo = col["C"]
        col.update(A=rojo, B=mezcla(rojo, col["K"], 0.45), Y=hx("F8F3E8") if not oscuro else hx("F3EAD6")); acc = "paliacate"
    else:
        col.update(A=hx("1A2232"), B=hx("FF2E88")); acc = "chaleco"
    col["J"] = hx("E8FF3A") if nombre == "ciber" else col["A"]      # ovillo
    return dict(t, col=col, acc=acc)

# =====================================================================
#  Pintores (RGBA con fondo transparente)
# =====================================================================

def celdas(g):
    for r, f in enumerate(g):
        for c, v in enumerate(f):
            if v != ".":
                yield r, c, v


def pintar_plano(g, t, cel):
    img = Image.new("RGBA", (N * cel, N * cel), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    for r, c, v in celdas(g):
        d.rectangle([c * cel, r * cel, (c + 1) * cel - 1, (r + 1) * cel - 1], fill=t["col"][v] + (255,))
    return img


def pintar_romano(g, t, cel):
    s = cel * SS
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    gap = 0.14 * s
    for r, c, v in celdas(g):
        d.rectangle([c * s, r * s, (c + 1) * s, (r + 1) * s], fill=t["junta"] + (255,))
    for r, c, v in celdas(g):
        rnd = random.Random(r * 97 + c * 13 + 7)
        k = 1 + rnd.uniform(-0.06, 0.06)
        col = tuple(max(0, min(255, round(ch * k + rnd.uniform(-4, 4)))) for ch in t["col"][v])
        j = lambda: rnd.uniform(-0.06, 0.06) * s
        x0, y0, x1, y1 = c * s + gap / 2, r * s + gap / 2, (c + 1) * s - gap / 2, (r + 1) * s - gap / 2
        d.polygon([(x0 + j(), y0 + j()), (x1 + j(), y0 + j()), (x1 + j(), y1 + j()), (x0 + j(), y1 + j())], fill=col + (255,))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def pintar_ciber(g, t, cel):
    """Devuelve RGB sobre negro (se suma a la banda) para que brille."""
    s = cel * SS
    capa = Image.new("RGB", (N * s, N * s), (0, 0, 0))
    d = ImageDraw.Draw(capa)
    ins = 0.1 * s; ancho = (s - 2 * ins) / 3
    for r, c, v in celdas(g):
        b = t["col"][v]; x0, y0 = c * s, r * s
        for k in range(3):
            canal = [0, 0, 0]; canal[k] = b[k]
            col = tuple(min(255, round(0.88 * b[i] + 0.12 * canal[i] * 1.6)) for i in range(3))
            d.rounded_rectangle([x0 + ins + k * ancho + 0.03 * s, y0 + ins, x0 + ins + (k + 1) * ancho - 0.03 * s, y0 + s - ins], radius=0.08 * s, fill=col)
    brillo = capa.filter(ImageFilter.GaussianBlur(s * 0.8))
    img = ImageChops.add(capa, ImageChops.multiply(brillo, Image.new("RGB", capa.size, (150, 150, 150))))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def pintar_gotico(g, t, cel, oscuro):
    s = cel * SS
    vidrio = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0))
    d = ImageDraw.Draw(vidrio)
    for r, c, v in celdas(g):
        rnd = random.Random(r * 31 + c * 7 + 3)
        for k in range(4, 0, -1):
            f = k / 4; m = (1 - f) * s * 0.45
            col = tuple(max(0, min(255, round(ch * (1.2 - 0.38 * f + rnd.uniform(-0.04, 0.04))))) for ch in t["col"][v])
            d.rectangle([c * s + m, r * s + m, (c + 1) * s - m, (r + 1) * s - m], fill=col + (255,))
    d = ImageDraw.Draw(vidrio)
    pl = t["plomo"] + (255,); grueso, fino = 0.22 * s, 0.07 * s
    v_ = lambda r, c: g[r][c] if 0 <= r < N and 0 <= c < N else "."
    for r in range(N + 1):
        for c in range(N):
            a, b = v_(r - 1, c), v_(r, c)
            if a == b == ".": continue
            w = fino if a == b else grueso
            d.rectangle([c * s - w / 2, r * s - w / 2, (c + 1) * s + w / 2, r * s + w / 2], fill=pl)
    for r in range(N):
        for c in range(N + 1):
            a, b = v_(r, c - 1), v_(r, c)
            if a == b == ".": continue
            w = fino if a == b else grueso
            d.rectangle([c * s - w / 2, r * s - w / 2, c * s + w / 2, (r + 1) * s + w / 2], fill=pl)
    halo = vidrio.filter(ImageFilter.GaussianBlur(s * 0.9))
    a = halo.getchannel("A").point(lambda q: int(q * (0.55 if oscuro else 0.25)))
    halo.putalpha(a)
    out = Image.alpha_composite(halo, vidrio)
    return out.resize((N * cel, N * cel), Image.LANCZOS)


def pintar_medieval(g, t, cel):
    s = cel * SS
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    lleno = lambda r, c: 0 <= r < N and 0 <= c < N and g[r][c] != "."
    for r in range(N):
        for c in range(N):
            if g[r][c] == "." and any(lleno(r + dr, c + dc) for dr in (-1, 0, 1) for dc in (-1, 0, 1)):
                d.rectangle([c * s, r * s, (c + 1) * s, (r + 1) * s], fill=t["filete"] + (255,))
    for r, c, v in celdas(g):
        d.rectangle([c * s, r * s, (c + 1) * s, (r + 1) * s], fill=t["col"][v] + (255,))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def pintar(g, t, cel, oscuro=False):
    m = t["material"]
    if m == "romano": return pintar_romano(g, t, cel)
    if m == "ciber": return pintar_ciber(g, t, cel)
    if m == "gotico": return pintar_gotico(g, t, cel, oscuro)
    return pintar_medieval(g, t, cel)

# =====================================================================
#  Rejillas de todos los cuadros (caché)
# =====================================================================
_rej = {}


def cabeza_frente(L, cx, cy, desv, acc):
    """Cabeza de frente (desv=0) o en tres cuartos (desv>0, mira a la derecha): dos ojos, nariz al centro, boca en «w»."""
    dx = desv * 2.4
    for sx in (-1, 1):
        k = 0.85 if (sx < 0 and desv > 0) else 1.0
        L.poligono("cabeza", [(cx + sx * 6.4 * k + dx * 0.3, cy - 1.6), (cx + sx * 4.8 * k + dx * 0.3, cy - 10.0), (cx + sx * 0.6 + dx * 0.3, cy - 4.8)])
        L.poligono("interior", [(cx + sx * 5.0 * k + dx * 0.3, cy - 3.2), (cx + sx * 4.6 * k + dx * 0.3, cy - 8.0), (cx + sx * 2.6 + dx * 0.3, cy - 4.4)])
    L.elipse("cabeza", cx, cy, 6.6, 5.6)
    for sx in (-1, 1):
        L.elipse("cabeza", cx + sx * 4.0, cy + 2.4, 2.6, 2.0)
    for sx in (-1, 1):
        rx = 0.9 if (sx < 0 and desv > 0) else 1.2
        ex = cx + sx * 2.7 + dx
        L.elipse("ojo", ex, cy - 0.4, rx, 1.45)
        L.elipse("linea", ex + 0.15, cy - 0.2, 0.5, 0.9)
        L.elipse("brillo", ex - 0.4, cy - 1.2, 0.42, 0.42)
        L.elipse("rubor", cx + sx * 3.9 + dx * 0.5, cy + 2.3, 1.2, 0.6)
    L.elipse("cabclaro", cx + dx, cy + 2.7, 2.4, 1.5)
    L.elipse("nariz", cx + dx, cy + 1.5, 0.7, 0.5)
    L.capsula("linea", [(cx + dx - 1.2, cy + 2.9), (cx + dx - 0.5, cy + 3.4), (cx + dx, cy + 2.9), (cx + dx + 0.5, cy + 3.4), (cx + dx + 1.2, cy + 2.9)], 0.45)


def accesorio_giro(L, acc, cx, ny, desv, frente):
    """Accesorio visto de frente o en tres cuartos. cx: centro del cuello; ny: altura del cuello."""
    dx = desv * 2.0
    if acc in ("collar", "colgante", "chaleco", "auriculares"):
        L.capsula("collar", [(cx - 4.4, ny), (cx + 4.4, ny)], 1.6)
        if acc == "colgante":
            L.elipse("joya", cx + dx, ny + 2.4, 2.2, 2.2); L.elipse("interior", cx + dx, ny + 2.4, 0.95, 0.95)
        else:
            L.elipse("joya", cx + dx, ny + 0.9, 1.0, 1.0)
    if acc == "paliacate":
        L.capsula("capa", [(cx - 4.8, ny), (cx + 4.8, ny)], 3.8)
        L.poligono("capa", [(cx - 4.2, ny), (cx + 4.2, ny), (cx + dx, ny + 8.0)])
    if acc == "capa":
        L.capsula("capa", [(cx - 4.6, ny), (cx + 4.6, ny)], 2.2)
        if frente:
            L.poligono("capa", [(cx - 6.5, ny), (cx - 9.6, ny + 2.0), (cx - 10.2, ny + 12.5), (cx - 7.8, ny + 13.0)])
            L.poligono("capa", [(cx + 6.5, ny), (cx + 9.6, ny + 2.0), (cx + 10.2, ny + 12.5), (cx + 7.8, ny + 13.0)])
        else:
            L.poligono("capa", [(cx - 3, ny - 0.5), (cx - 9, ny + 0.5), (cx - 17, ny + 2.5), (cx - 18.5, ny + 7.5), (cx - 12, ny + 9.5), (cx - 4, ny + 6)])
        L.elipse("broche", cx + dx, ny + 0.3, 1.3, 1.3)
    if acc == "chaleco":
        if frente:
            L.poligono("capa", [(cx - 7.0, ny + 2.5), (cx + 7.0, ny + 2.5), (cx + 7.4, ny + 12), (cx - 7.4, ny + 12)])
            L.capsula("broche", [(cx - 5.2, ny + 8.5), (cx + 5.2, ny + 8.5)], 0.9)
        else:
            L.poligono("capa", [(cx - 2, ny + 2.5), (cx + 5.0, ny + 3.0), (cx + 4.8, ny + 11.5), (cx - 8, ny + 12.5), (cx - 15, ny + 9), (cx - 12, ny + 3.5)])
            L.capsula("broche", [(cx - 11, ny + 8.0), (cx + 2.5, ny + 8.5)], 0.9)


def dibujar_giro(i, acc):
    """Cuadros de la vuelta: 0 = tres cuartos (mirando a la derecha y hacia ti), 1 = de frente."""
    L = Lienzo()
    if i == 1:
        L.curva("cola", [(36, 47), (41, 44), (44, 38), (44.5, 32), (42.5, 28), (40, 28.5)], ANCHO_COLA)
        for x in (25.5, 38.5):
            L.capsula("lejos", [(x, 50), (x - 0.5 if x < 32 else x + 0.5, 58.6)], 3.4)
            L.elipse("lejos", x - 0.5 if x < 32 else x + 0.5, 59.0, 2.4, 1.3)
        L.elipse("cuerpo", 32, 45, 8.2, 9.0)
        L.elipse("cuerpo", 32, 35.5, 5.6, 4.2)
        L.elipse("claro", 32, 43, 4.2, 6.2)
        for x in (29, 35):
            L.curva("pierna", [(x, 47), (x, 53), (x, SUELO)], [4.0, 3.6, 3.4])
            L.capsula("pata", [(x, 57.4), (x, SUELO)], 3.4); L.elipse("pata", x, 59.4, 2.2, 1.2)
        accesorio_giro(L, acc, 32, 34.0, 0.0, True)
        cabeza_frente(L, 32, 27.5, 0.0, acc)
    else:
        L.curva("cola", [(19, 40), (14, 35), (12.5, 28), (14, 22), (17.5, 20), (19.5, 22)], ANCHO_COLA)
        L.curva("lejos", [(22, 44), (20.6, 52), (21.4, SUELO)], [3.6, 3.0, 2.8]); L.elipse("lejos", 22, 59.4, 2.2, 1.2)
        L.curva("lejos", [(35, 47), (35.2, 53), (35.4, SUELO)], [3.8, 3.4, 3.2]); L.elipse("lejos", 35.8, 59.4, 2.2, 1.2)
        L.elipse("cuerpo", 24, 41.5, 7.0, 7.0)
        L.capsula("cuerpo", [(24, 42), (34, 43.5)], 14.5)
        L.elipse("cuerpo", 34.5, 43.5, 8.0, 8.5)
        L.elipse("cuerpo", 36.5, 35.5, 5.2, 4.2)
        L.elipse("cuerpo", 25.5, 46, 4.0, 4.8)
        L.elipse("claro", 36, 43, 3.6, 5.8)
        L.curva("pierna", [(26, 49), (23.6, 54), (24.6, SUELO)], [3.8, 3.0, 2.8])
        L.curva("pierna", [(38.5, 47), (39, 53), (39, SUELO)], [4.0, 3.6, 3.4])
        for x in (24.6, 39):
            L.capsula("pata", [(x, 57.4), (x, SUELO)], 3.2); L.elipse("pata", x + 0.5, 59.4, 2.2, 1.2)
        accesorio_giro(L, acc, 36.5, 34.0, 0.6, False)
        cabeza_frente(L, 37.5, 28, 0.6, acc)
    return L.rejilla()


def rejilla(anim, i, t, espejo=False):
    estilo = (t["rayas"], t["acc"])
    if anim == "girar":
        k = ("girar", i, estilo, espejo)
        if k not in _rej:
            b = ("girar", i, estilo, False)
            if b not in _rej:
                ch, parte = dibujar_giro(i, t["acc"])
                _rej[b] = acabar(ch, parte, estilo)
            _rej[k] = [f[::-1] for f in _rej[b]] if espejo else _rej[b]
        return _rej[k]
    k = (anim, i, estilo, espejo)
    if k not in _rej:
        base = (anim, i, estilo, False)
        if base not in _rej:
            ch, parte = dibujar(ANIMS[anim][i], t["acc"])
            _rej[base] = acabar(ch, parte, estilo)
        _rej[k] = [f[::-1] for f in _rej[base]] if espejo else _rej[base]
    return _rej[k]

# =====================================================================
#  Máquina de estados (tiempos al azar)
# =====================================================================
DT = 0.08                  # 12,5 cuadros por segundo
ANCHO_DP, ALTO_DP = 412, 80
VEL = 26.0                 # dp/s caminando
TICKS_ANIM = dict(quieto=3, sentarse=2, levantarse=2, sentado=4, asearse=2, echarse=2, despertar=2, dormir=5, saltar=2)
SALTO_DX = [0, 3.0, 4.5, 4.5, 2.0, 0]       # dp por tick en cada cuadro del salto
SALTO_DY = [0, -2, -9, -5, 0, 0]


def simular(seed, animo="normal", segundos=20.0, x0=250.0):
    """Máquina de estados. animo: normal, alegre (juega y salta más), triste (camina lento, cola baja, se sienta y duerme más),
    enojado (a ratos se eriza)."""
    rnd = random.Random(seed)
    total = int(segundos / DT)
    x, dirn = x0, 1
    cuadros, giros = [], 0
    cam = "caminar_triste" if animo == "triste" else "caminar"
    qui = "quieto_triste" if animo == "triste" else "quieto"
    vel = VEL * {"triste": 0.6, "alegre": 1.15}.get(animo, 1.0)
    T = {
        "normal": dict(caminar=[("quieto", 0.3), ("sentarse", 0.3), ("saltar", 0.2), ("jugar", 0.1), ("caminar", 0.1)],
                       quieto=[("caminar", 0.55), ("sentarse", 0.25), ("saltar", 0.1), ("jugar", 0.1)],
                       sentado=[("asearse", 0.45), ("echarse", 0.35), ("levantarse", 0.2)]),
        "alegre": dict(caminar=[("jugar", 0.4), ("saltar", 0.3), ("quieto", 0.1), ("sentarse", 0.1), ("caminar", 0.1)],
                       quieto=[("jugar", 0.45), ("saltar", 0.25), ("caminar", 0.3)],
                       sentado=[("levantarse", 0.6), ("asearse", 0.4)]),
        "triste": dict(caminar=[("quieto", 0.35), ("sentarse", 0.45), ("caminar", 0.2)],
                       quieto=[("caminar", 0.4), ("sentarse", 0.6)],
                       sentado=[("echarse", 0.5), ("asearse", 0.1), ("levantarse", 0.4)]),
        "enojado": dict(caminar=[("erizarse", 0.45), ("quieto", 0.25), ("sentarse", 0.15), ("caminar", 0.15)],
                        quieto=[("erizarse", 0.4), ("caminar", 0.6)],
                        sentado=[("levantarse", 0.6), ("asearse", 0.4)]),
    }[animo]

    def elegir(op):
        u, a = rnd.random(), 0.0
        for nombre, p in op:
            a += p
            if u <= a: return nombre
        return op[-1][0]

    def lim(xx, d):
        return (xx + 58 <= ANCHO_DP) if d > 0 else (xx + 6 >= 0)

    def durar(e):
        return int({"caminar": rnd.uniform(1.6, 3.2), "quieto": rnd.uniform(1.0, 2.0), "sentado": rnd.uniform(1.0, 2.0) * (1.6 if animo == "triste" else 1),
                    "dormir": rnd.uniform(3.0, 4.5), "erizarse": rnd.uniform(1.5, 2.4)}.get(e, 0) / DT)

    estado, resto, i = "caminar", durar("caminar"), 0
    while len(cuadros) < total:
        if estado == "caminar":
            x += dirn * vel * DT
            if not lim(x, dirn):
                x -= dirn * vel * DT
                cuadros += [("girar", 0, x, dirn, 0)] * 3 + [("girar", 1, x, dirn, 0)] * 4
                dirn = -dirn; giros += 1
                cuadros += [("girar", 0, x, dirn, 0)] * 3
            cuadros.append((cam, i % 8, x, dirn, 0))
            i += 1; resto -= 1
            if resto <= 0:
                estado = elegir(T["caminar"]); i = 0
        elif estado == "quieto":
            cuadros.append((qui, (i // 3) % 4, x, dirn, 0)); i += 1; resto -= 1
            if resto <= 0:
                estado = elegir(T["quieto"]); i = 0
        elif estado == "saltar":
            if not lim(x + dirn * 34, dirn):
                estado = "quieto"; resto = durar("quieto"); continue
            for k in range(6):
                for _ in range(TICKS_ANIM["saltar"]):
                    x += dirn * SALTO_DX[k]
                    cuadros.append(("saltar", k, x, dirn, SALTO_DY[k]))
            estado = elegir([("caminar", 0.6), ("quieto", 0.4)]); i = 0
        elif estado == "jugar":
            if not lim(x + dirn * 10, dirn):
                estado = "quieto"; resto = durar("quieto"); continue
            for k, n, dx in ((0, 3, 0), (1, 3, 0), (0, 3, 0), (1, 3, 0), (2, 2, 1.5), (3, 4, 0.3), (4, 3, 0), (5, 4, 0)):
                for _ in range(n):
                    x += dirn * dx
                    cuadros.append(("jugar", k, x, dirn, -3 if k == 2 else 0))
            estado = elegir([("caminar", 0.5), ("quieto", 0.2), ("jugar", 0.3 if animo == "alegre" else 0.0)]); i = 0
        elif estado == "erizarse":
            cuadros.append(("enojado", (i // 2) % 3, x, dirn, 0)); i += 1; resto -= 1
            if resto <= 0:
                estado = "quieto"; i = 0
        elif estado in ("sentarse", "levantarse", "echarse", "despertar"):
            base = "sentarse" if estado in ("sentarse", "levantarse") else "echarse"
            orden = range(len(ANIMS[base])) if estado in ("sentarse", "echarse") else range(len(ANIMS[base]) - 1, -1, -1)
            for k in orden:
                for _ in range(TICKS_ANIM[estado]):
                    cuadros.append((base, k, x, dirn, 0))
            estado = {"sentarse": "sentado", "echarse": "dormir", "despertar": "sentado", "levantarse": "caminar"}[estado]; i = 0
        elif estado == "sentado":
            cuadros.append(("sentado", (i // 4) % 2, x, dirn, 0)); i += 1; resto -= 1
            if resto <= 0:
                estado = elegir(T["sentado"]); i = 0
        elif estado == "asearse":
            for vuelta in range(2):
                for k in (range(6) if vuelta == 0 else range(1, 6)):
                    for _ in range(TICKS_ANIM["asearse"]):
                        cuadros.append(("asearse", k, x, dirn, 0))
            cuadros.append(("sentado", 0, x, dirn, 0))
            estado = elegir([("echarse", 0.5), ("levantarse", 0.5)]); i = 0
        elif estado == "dormir":
            cuadros.append(("dormir", (i // TICKS_ANIM["dormir"]) % 3, x, dirn, 0)); i += 1; resto -= 1
            if resto <= 0:
                estado, i = "despertar", 0
        if resto <= 0 and estado in ("caminar", "quieto", "sentado", "dormir", "erizarse") and i == 0:
            resto = durar(estado)
    cuadros = cuadros[:total]
    return cuadros, {c[0] for c in cuadros}, giros


def buscar_semilla(animo="normal", pedidos=("caminar", "quieto", "sentarse", "asearse", "dormir", "saltar", "girar"), segundos=20.0):
    for s in range(3000):
        cs, vistos, giros = simular(s, animo, segundos)
        if set(pedidos) <= vistos:
            return s, cs
    raise RuntimeError("ninguna semilla recorre todos los estados: " + animo)

# =====================================================================
#  Banda
# =====================================================================
ESC = 3      # pixeles por dp (teléfono de 3×)
Y_SUELO = 74  # dp: dónde pisa el gato dentro de la banda
BANDAS = [("romano", False, "Romano · un mosaico que cobra vida"), ("gotico", False, "Gótico · frente a un vitral"),
          ("medieval", False, "Medieval · al margen de un libro del scriptorium"), ("ciber", True, "Ciber · dentro de una pantalla")]
PINTOR_BANDA = {"romano": "romano", "gotico": "plano", "medieval": "medieval", "ciber": "ciber"}
AJUSTAR = {"romano", "ciber"}   # el gato se mueve de tesela en tesela / de pixel en pixel


def fondo_romano(t):
    """Piso de mosaico: campo de mármol, grecas de tinta arriba y abajo, filetes de pórfido y florecitas."""
    W, H = ANCHO_DP, ALTO_DP
    s = ESC * SS
    img = Image.new("RGB", (W * s, H * s), t["junta"])
    d = ImageDraw.Draw(img)
    tinta, porf, oro, bron, crema = hx("17191C"), hx("7C2433"), hx("B8892B"), hx("2D7A6F"), hx("F2EEE2")
    marmol = [hx("ECEDEA"), hx("E4E2DA"), hx("F2F1EC"), hx("E8E6DF"), hx("ECEDEA")]
    greca = ["XXXXXXX.", "X.....X.", "X.XXX.X.", "X.X...X.", "X.XXXXXX"]
    flores = {}
    for k, x0 in enumerate(range(26, W, 52)):
        y0 = 26 if k % 2 == 0 else 50
        for dx, dy, c in [(0, 0, oro), (-1, 0, porf), (1, 0, porf), (0, -1, porf), (0, 1, porf), (-2, 0, bron), (2, 0, bron), (0, -2, bron), (0, 2, bron),
                          (-1, -1, crema), (1, -1, crema), (-1, 1, crema), (1, 1, crema)]:
            flores[(x0 + dx, y0 + dy)] = c
    for y in range(H):
        for x in range(W):
            rnd = random.Random(x * 7919 + y * 104729)
            if y in (0, 6, 74):
                c = porf
            elif 1 <= y <= 5:
                c = tinta if greca[y - 1][x % 8] == "X" else crema
            elif 75 <= y <= 79:
                c = tinta if greca[y - 75][(x + 4) % 8] == "X" else crema
            else:
                c = flores.get((x, y), rnd.choice(marmol))
            k = 1 + rnd.uniform(-0.05, 0.05)
            c = tuple(max(0, min(255, round(ch * k))) for ch in c)
            j = lambda: rnd.uniform(-0.07, 0.07) * s
            g = 0.15 * s
            x0, y0, x1, y1 = x * s + g / 2, y * s + g / 2, (x + 1) * s - g / 2, (y + 1) * s - g / 2
            d.polygon([(x0 + j(), y0 + j()), (x1 + j(), y0 + j()), (x1 + j(), y1 + j()), (x0 + j(), y1 + j())], fill=c)
    return img.resize((W * ESC, H * ESC), Image.LANCZOS)


def fondo_gotico(t):
    """Vitral: ocho lancetas apuntadas con vidrios en rombo, un rosetón en cada una, piedra y alféizar."""
    W, H = ANCHO_DP * ESC, ALTO_DP * ESC
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    xd, yd = (xx + 0.5) / ESC, (yy + 0.5) / ESC
    img = np.zeros((H, W, 3), np.float32)
    piedra, junta_p = np.array(hx("2A2F48"), np.float32), np.array(hx("1C2036"), np.float32)
    img[:] = piedra
    # Sillares de la pared.
    fila = np.floor(yd / 10)
    jh = (yd % 10) < 0.5
    jv = ((xd + (fila % 2) * 9) % 18) < 0.5
    img[jh | jv] = junta_p
    na = 8; Wa = ANCHO_DP / na
    ai = np.floor(xd / Wa)
    x0, x1 = ai * Wa + 3.4, (ai + 1) * Wa - 3.4
    w = x1 - x0
    spring, apex, bottom = 22.0, 3.5, 69.0
    kk = (0.866 * w) / (spring - apex)
    yv = spring - (spring - yd) * kk
    dentro_x = (xd >= x0) & (xd <= x1)
    rect = dentro_x & (yd >= spring) & (yd <= bottom)
    arco = dentro_x & (yd < spring) & (yd >= apex) & ((xd - x1) ** 2 + (yv - spring) ** 2 <= w ** 2) & ((xd - x0) ** 2 + (yv - spring) ** 2 <= w ** 2)
    vent = rect | arco
    # Vidrios en rombo (cuadrales) con color por pieza.
    lado = 5.0
    a, b = (xd + yd) / lado, (xd - yd) / lado
    u, v = np.floor(a), np.floor(b)
    plomo = ((a - u) < 0.09) | ((b - v) < 0.09)
    hsh = ((u * 92821 + v * 68917 + ai * 1237) % 97 + 97) % 97
    pal = [(45, hx("3F64D8")), (60, hx("26306E")), (66, hx("5C8DFF")), (76, hx("B3122E")), (86, hx("E2B34A")), (92, hx("2E8A6E")), (97, hx("6E4BC2"))]
    vidrio = np.zeros_like(img)
    lim_ant = 0
    for lim, col in pal:
        m = (hsh >= lim_ant) & (hsh < lim)
        vidrio[m] = col; lim_ant = lim
    # Rosetón en cada lanceta.
    cx = (x0 + x1) / 2; cy = 34.0
    dist = np.hypot(xd - cx, yd - cy); ang = np.arctan2(yd - cy, xd - cx)
    ros = dist <= 9.0
    sector = np.floor((ang + np.pi) / (np.pi / 4)) % 2
    vidrio[ros & (sector == 0)] = hx("B3122E"); vidrio[ros & (sector == 1)] = hx("E2B34A")
    vidrio[dist <= 3.2] = hx("5C8DFF")
    plomo = np.where(ros, (np.abs(dist - 9.0) < 0.45) | (np.abs(dist - 3.2) < 0.4) | ((np.abs(((ang + np.pi) % (np.pi / 4))) < 0.05) & (dist > 3.2)), plomo)
    luz = 0.72 + 0.38 * (1 - yd / 70) + ((hsh % 7) - 3) * 0.025
    vidrio = vidrio * luz[..., None]
    gris = vidrio.mean(axis=2, keepdims=True)
    vidrio = (vidrio * 0.55 + gris * 0.45) * 0.68
    vidrio[plomo] = hx("0B0D16")
    img[vent] = vidrio[vent]
    # Marco de plomo grueso alrededor de cada lanceta.
    borde = vent & ~(np.roll(vent, 2, 0) & np.roll(vent, -2, 0) & np.roll(vent, 2, 1) & np.roll(vent, -2, 1))
    img[borde] = hx("0B0D16")
    # Alféizar de piedra donde camina el gato.
    sill = yd >= 71
    img[sill] = hx("3A4060"); img[(yd >= 71) & (yd < 72)] = hx("5A6290")
    img[sill & ((xd % 24) < 0.4)] = hx("262B44")
    base = Image.fromarray(np.clip(img, 0, 255).astype(np.uint8))
    # Resplandor del vidrio.
    m = Image.fromarray((vent * 255).astype(np.uint8))
    brillo = Image.fromarray(np.clip(vidrio * vent[..., None], 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(5))
    base = ImageChops.add(base, ImageChops.multiply(brillo, Image.new("RGB", base.size, (40, 40, 40))))
    return base


def fondo_medieval(t):
    """Página de pergamino: inicial iluminada, dos renglones de escritura, pautado rojo y una vid en el margen inferior."""
    W, H = ANCHO_DP * ESC, ALTO_DP * ESC
    rng = np.random.default_rng(3)
    ruido = Image.fromarray(((rng.normal(0, 1, (H // 18 + 2, W // 18 + 2)) * 40 + 128).clip(0, 255)).astype(np.uint8)).resize((W, H), Image.BICUBIC)
    rr = (np.asarray(ruido, np.float32) - 128) / 128
    base = np.array(hx("EFE3C8"), np.float32)
    yy = np.mgrid[0:H, 0:W][0] / H
    tono = 1 + 0.035 * rr - 0.05 * np.clip(yy - 0.85, 0, 1) * 4
    img = Image.fromarray(np.clip(base * tono[..., None], 0, 255).astype(np.uint8))
    d = ImageDraw.Draw(img)
    S = ESC
    tinta, rojo, lapis, oro = hx("3A2A1E"), hx("9E1B1B"), hx("1F3F8F"), mezcla(hx("A97C1E"), hx("F2D27A"), 0.35)
    for yb in (9.5, 17.5):
        d.line([22 * S, yb * S + 1, 406 * S, yb * S + 1], fill=mezcla(hx("EFE3C8"), rojo, 0.16))
    # Inicial iluminada.
    d.rectangle([4 * S, 2 * S, 19 * S, 18 * S], fill=oro)
    d.rectangle([4 * S + 3, 2 * S + 3, 19 * S - 3, 18 * S - 3], fill=lapis)
    d.text((11.5 * S, 10 * S), "V", font=ImageFont.truetype(F + "DejaVuSerif-Bold.ttf", 13 * S), fill=oro, anchor="mm")
    for cx, cy in [(5.2, 3.2), (17.8, 3.2), (5.2, 16.8), (17.8, 16.8)]:
        d.ellipse([cx * S - 2, cy * S - 2, cx * S + 2, cy * S + 2], fill=hx("FFFFFF"))
    # Escritura: palabras de trazos (mínimas), con astas y una palabra en rojo.
    rnd = random.Random(8)
    for li, (yb, fin) in enumerate(((9.5, 404), (17.5, 300))):
        x = 22.5
        primera = True
        while x < fin:
            n = rnd.randint(2, 7)
            col = rojo if (li == 1 and primera) else tinta
            for _ in range(n):
                alto = 3.6 + (2.2 if rnd.random() < 0.14 else 0)
                bajo = 1.6 if rnd.random() < 0.07 else 0
                d.line([x * S, (yb - alto) * S, x * S, (yb + bajo) * S], fill=col, width=2)
                d.line([x * S, yb * S, (x + 0.8) * S, (yb - 0.5) * S], fill=col, width=1)
                x += 1.9
            x += 2.6; primera = False
    # Vid en el margen inferior: tallo ondulado, hojas de lapislázuli y rojo, y bezantes de oro.
    pts = [(xx * S, (75.8 + 1.3 * math.sin(xx / 7.5)) * S) for xx in np.arange(0, ANCHO_DP + 0.5, 0.5)]
    d.line(pts, fill=hx("5E4A35"), width=2)
    for k, xx in enumerate(np.arange(4, ANCHO_DP, 9)):
        yy0 = 75.8 + 1.3 * math.sin(xx / 7.5)
        arriba = k % 2 == 0
        cy = yy0 + (-1.9 if arriba else 1.9)
        col = lapis if k % 3 else rojo
        d.ellipse([(xx - 2.2) * S, (cy - 1.1) * S, (xx + 2.2) * S, (cy + 1.1) * S], fill=col, outline=hx("2A1B10"))
        d.ellipse([(xx + 4.5) * S - 2, (yy0 + 0.0) * S - 2, (xx + 4.5) * S + 2, (yy0 + 0.0) * S + 2], fill=oro)
    return img


def fondo_ciber(t):
    """Pantalla: piso de cuadrícula en perspectiva, horizonte magenta, rótulo de terminal y pixeles visibles."""
    W, H = ANCHO_DP, ALTO_DP
    fondo = hx("05070B")
    img = Image.new("RGB", (W, H), fondo)
    d = ImageDraw.Draw(img)
    hor = 44
    rnd = random.Random(4)
    for _ in range(40):
        d.point((rnd.randrange(W), rnd.randrange(12, hor - 2)), fill=mezcla(hx("D6F3F7"), fondo, rnd.uniform(0.5, 0.85)))
    for k in range(1, 10):
        y = hor + (H - hor) * (k / 9) ** 1.8
        d.line([0, y, W, y], fill=mezcla(hx("FF2E88"), fondo, 0.62))
    for xb in range(-700, 1112, 30):
        d.line([206 + (xb - 206) * 0.06, hor, xb, H], fill=mezcla(hx("00E5FF"), fondo, 0.7))
    d.line([0, hor, W, hor], fill=mezcla(hx("FF2E88"), fondo, 0.25))
    big = img.resize((W * ESC, H * ESC), Image.NEAREST)
    # Pixeles visibles: rejilla oscura entre pixeles.
    a = np.asarray(big, np.float32)
    yy, xx = np.mgrid[0:H * ESC, 0:W * ESC]
    rej = np.where((xx % ESC == ESC - 1) | (yy % ESC == ESC - 1), 0.55, 1.0)
    big = Image.fromarray(np.clip(a * rej[..., None], 0, 255).astype(np.uint8))
    dd = ImageDraw.Draw(big)
    dd.text((10, 8), "VESTIGIUM :: gato.exe", font=fuente("DejaVuSansMono.ttf", 13), fill=hx("FF2E88"))
    dd.text((W * ESC - 10, 8), "SYS OK", font=fuente("DejaVuSansMono.ttf", 13), fill=hx("00E5FF"), anchor="ra")
    return big


def viñeta_ciber():
    W, H = ANCHO_DP * ESC, ALTO_DP * ESC
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    nx, ny = (xx - W / 2) / (W / 2), (yy - H / 2) / (H / 2)
    v = np.clip(1.05 - 0.22 * nx ** 4 - 0.45 * ny ** 4, 0, 1)
    r = 18
    for (cx, cy) in [(r, r), (W - r, r), (r, H - r), (W - r, H - r)]:
        esq = ((xx < r) if cx == r else (xx > W - r)) & ((yy < r) if cy == r else (yy > H - r))
        v[esq & (np.hypot(xx - cx, yy - cy) > r)] = 0
    m = Image.fromarray((v * 255).astype(np.uint8))
    return Image.merge("RGB", (m, m, m))


FONDOS = {"romano": fondo_romano, "gotico": fondo_gotico, "medieval": fondo_medieval, "ciber": fondo_ciber}
_spr = {}


def sprite(nombre, oscuro, t, anim, i, espejo):
    k = (nombre, oscuro, anim, i, espejo)
    if k not in _spr:
        g = rejilla(anim, i, t, espejo)
        _spr[k] = pintar_plano(g, t, ESC) if PINTOR_BANDA[nombre] == "plano" else pintar(g, t, ESC, oscuro)
    return _spr[k]


def lineas_escaneo(img):
    lin = Image.new("L", img.size, 255); d = ImageDraw.Draw(lin)
    for y in range(0, img.height, 2):
        d.line([0, y + 1, img.width, y + 1], fill=205)
    return ImageChops.multiply(img, Image.merge("RGB", (lin, lin, lin)))


def cuadro_banda(nombre, oscuro, t, fondo, c, tick, extra):
    anim, i, x, dirn, dy = c
    if nombre in AJUSTAR:
        x, dy = round(x), round(dy)
    img = fondo.copy()
    cx = (x + (31 if dirn > 0 else 33)) * ESC
    k = 1 + dy / 14
    caja = [cx - 19 * ESC * k, (Y_SUELO + 0.4) * ESC - 1.3 * ESC * k, cx + 19 * ESC * k, (Y_SUELO + 0.4) * ESC + 1.3 * ESC * k]
    if nombre == "romano":
        capa = Image.new("RGBA", img.size, (0, 0, 0, 0)); ImageDraw.Draw(capa).ellipse(caja, fill=(23, 25, 28, 60))
        img = Image.alpha_composite(img.convert("RGBA"), capa).convert("RGB")
    elif nombre == "gotico":
        ImageDraw.Draw(img).ellipse(caja, fill=hx("262B44"))
    spr = sprite(nombre, oscuro, t, anim, i, dirn < 0)
    px, py = int(round(x * ESC)), int(round((Y_SUELO - 60 + dy) * ESC))
    if nombre == "ciber":
        capa = Image.new("RGB", img.size, (0, 0, 0)); capa.paste(spr, (px, py))
        dr = ImageDraw.Draw(capa)
        dr.ellipse(caja, fill=mezcla(hx("00E5FF"), (0, 0, 0), 0.82))      # reflejo en el piso
        img = ImageChops.add(img, capa)
        if (tick // 6) % 2 == 0:
            ImageDraw.Draw(img).rectangle([10 + 21 * 8 + 4, 9, 10 + 21 * 8 + 11, 21], fill=hx("FF2E88"))
        img = ImageChops.multiply(lineas_escaneo(img), extra)
    else:
        img.paste(spr, (px, py), spr)
    return img


def gif_banda(cuadros, bandas=None, sufijo=""):
    """Dos GIF por tema (cada uno con su paleta): banda a tamaño real y banda a ×3."""
    W3 = ANCHO_DP * ESC
    for nombre, oscuro, etiqueta in (bandas or BANDAS):
        t = tema(nombre, oscuro)
        fondo = FONDOS[nombre](t)
        extra = viñeta_ciber() if nombre == "ciber" else None
        G, Rl = [], []
        for k, c in enumerate(cuadros):
            b = cuadro_banda(nombre, oscuro, t, fondo, c, k, extra)
            g = Image.new("RGB", (W3 + 20, ALTO_DP * ESC + 36), (250, 249, 246))
            ImageDraw.Draw(g).text((10, 6), etiqueta + " · ×3: los pixeles reales de un teléfono de 3×", font=fuente("DejaVuSans.ttf", 15), fill=(40, 40, 46))
            g.paste(b, (10, 28)); G.append(g)
            r = Image.new("RGB", (ANCHO_DP + 20, ALTO_DP + 26), (250, 249, 246))
            ImageDraw.Draw(r).text((10, 4), etiqueta.split(" · ")[0] + " · tamaño real (80 dp)", font=fuente("DejaVuSans.ttf", 11), fill=(40, 40, 46))
            r.paste(b.resize((ANCHO_DP, ALTO_DP), Image.LANCZOS), (10, 20)); Rl.append(r)
        guardar_gif(G, AQUI / f"banda_{nombre}{sufijo}_x3.gif")
        guardar_gif(Rl, AQUI / f"banda_{nombre}{sufijo}_real.gif")
        G[len(G) // 3].save(AQUI / f"_revisar_{nombre}{sufijo}.png")


def guardar_gif(cs, ruta):
    muestra = Image.new("RGB", (cs[0].width, cs[0].height * 6))
    for j, k in enumerate(range(0, len(cs), max(1, len(cs) // 6))[:6]):
        muestra.paste(cs[k], (0, j * cs[0].height))
    pal = muestra.quantize(255, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE)
    q = [c.quantize(palette=pal, dither=Image.Dither.NONE) for c in cs]
    q[0].save(ruta, save_all=True, append_images=q[1:], duration=int(DT * 1000), loop=0, optimize=True, disposal=1)

# =====================================================================
#  Hoja de sprites y comparación de temas
# =====================================================================
FILAS_HOJA = [("caminar", 8), ("dar la vuelta", 2), ("jugar (alegre)", 6), ("erizado (enojado)", 3),
              ("caminar (triste)", 8), ("quieto (triste)", 4), ("quieto (respira)", 4), ("sentarse", 4), ("sentado (respira)", 2), ("echarse", 3), ("dormir", 3), ("saltar", 6), ("asearse", 6)]
CLAVE = {"caminar": "caminar", "dar la vuelta": "girar", "jugar (alegre)": "jugar", "erizado (enojado)": "enojado",
         "caminar (triste)": "caminar_triste", "quieto (triste)": "quieto_triste", "quieto (respira)": "quieto", "sentarse": "sentarse", "sentado (respira)": "sentado", "echarse": "echarse",
         "dormir": "dormir", "saltar": "saltar", "asearse": "asearse"}


def hoja(nombre, oscuro, archivo, cel=5, material=False):
    t = tema(nombre, oscuro)
    lado = N * cel; sep = 12; izq = 190
    W = izq + 8 * (lado + sep) + 10
    H = 70 + len(FILAS_HOJA) * (lado + 34)
    img = Image.new("RGB", (W, H), t["fondo"])
    d = ImageDraw.Draw(img)
    d.text((14, 14), f"Hoja de sprites · {nombre} {'oscuro' if oscuro else 'claro'} · 64×64 · ampliado ×{cel}" + (" · con material" if material else " · pixeles planos"),
           font=fuente("DejaVuSans-Bold.ttf", 20), fill=t["texto"])
    d.text((14, 42), "Cada cuadro es una celda de la cuadrícula; la línea punteada es el suelo.", font=fuente("DejaVuSans.ttf", 14), fill=t["texto"])
    for fi, (et, n) in enumerate(FILAS_HOJA):
        y = 70 + fi * (lado + 34)
        d.text((14, y + lado // 2 - 10), et, font=fuente("DejaVuSans-Bold.ttf", 16), fill=t["texto"])
        d.text((14, y + lado // 2 + 12), f"{n} cuadros", font=fuente("DejaVuSans.ttf", 13), fill=t["texto"])
        for k in range(n):
            x = izq + k * (lado + sep)
            d.rectangle([x - 1, y - 1, x + lado, y + lado], outline=t["borde"])
            for xx in range(x, x + lado, 8):
                d.line([xx, y + int(SUELO * cel) + cel, xx + 3, y + int(SUELO * cel) + cel], fill=t["borde"])
            g = rejilla(CLAVE[et], k, t)
            spr = pintar(g, t, cel, oscuro) if material else pintar_plano(g, t, cel)
            if t["material"] == "ciber" and material:
                base = img.crop((x, y, x + lado, y + lado)); img.paste(ImageChops.add(base, spr), (x, y))
            else:
                img.paste(spr, (x, y), spr)
            d.text((x + 4, y + 2), str(k + 1), font=fuente("DejaVuSans.ttf", 12), fill=t["texto"])
    img.save(AQUI / archivo)


def comparar_temas():
    casos = [("romano", False), ("romano", True), ("gotico", False), ("gotico", True), ("medieval", False), ("medieval", True), ("ciber", True)]
    poses = [("caminar", 2), ("girar", 0), ("girar", 1), ("jugar", 0), ("enojado", 0), ("quieto_triste", 0), ("dormir", 0)]
    cel = 4; lado = N * cel
    W = 170 + len(poses) * (lado + 8)
    H = 40 + len(casos) * (lado + 8)
    img = Image.new("RGB", (W, H), (250, 249, 246))
    d = ImageDraw.Draw(img)
    d.text((10, 10), "Mismo gato y mismas animaciones; el tema decide pelaje, ojos, accesorio y material (×4). Romano: capita · gótico: rosetón · medieval: kufiya · ciber: chaleco de neón", font=fuente("DejaVuSans-Bold.ttf", 16), fill=(25, 25, 28))
    for i, (n, o) in enumerate(casos):
        t = tema(n, o); y = 40 + i * (lado + 8)
        d.rectangle([0, y - 4, W, y + lado + 4], fill=t["fondo"])
        d.text((10, y + lado // 2 - 18), n.capitalize(), font=fuente("DejaVuSans-Bold.ttf", 16), fill=t["texto"])
        d.text((10, y + lado // 2 + 2), "oscuro" if o else "claro", font=fuente("DejaVuSans.ttf", 13), fill=t["texto"])
        for j, (a, k) in enumerate(poses):
            x = 170 + j * (lado + 8)
            spr = pintar(rejilla(a, k, t), t, cel, o)
            if t["material"] == "ciber":
                base = img.crop((x, y, x + lado, y + lado)); img.paste(ImageChops.add(base, spr), (x, y))
            else:
                img.paste(spr, (x, y), spr)
    img.save(AQUI / "temas_gato7.png")


# =====================================================================
#  PRUEBA 8: vuelta por la espalda, sombreros y los 21 temas
# =====================================================================

def esc(c, k):
    return tuple(max(0, min(255, round(x * k))) for x in c)


# Capas nuevas: la cola vista de espaldas (va delante del cuerpo) y el sombrero.
CAPAS.insert([c[0] for c in CAPAS].index("broche") + 1, ("colafrente", "P", "colafrente", 0.45))
CAPAS.extend([("sombrero", "M", "sombrero", 0.4), ("sombrero2", "Z", "sombrero", 0.35), ("sombrero3", "G", "sombrero", 0.3),
              ("vidrio", "O", "sombrero", 0.35), ("sombreroh", "H", "sombrero", 0.3)])
RANGO.update(colafrente=2.6, sombrero=7)


# ---------------------------------------------------------------------
#  Sombreros (de lado, siguiendo la inclinación de la cabeza; y de frente/espaldas para la vuelta)
# ---------------------------------------------------------------------

def sombrero_lado(L, cx, cy, inc, som):
    if not som: return
    a = math.radians(inc)
    p = lambda x, y: rot(cx, cy, x, y, a)
    P = lambda pts: [p(*q) for q in pts]
    if som == "salacot":
        L.elipse("sombrero", *p(0.2, -5.4), 5.8, 3.4, inc)
        L.capsula("sombrero", P([(-7.2, -4.0), (7.4, -4.4)]), 1.5)
        L.capsula("sombrero2", P([(-5.4, -4.9), (5.6, -5.1)]), 1.1)
    elif som == "bicornio":
        L.poligono("sombrero", P([(-8.2, -3.8), (-4.5, -8.6), (0, -10.4), (4.5, -8.6), (8.2, -3.8), (0, -6.0)]))
        L.capsula("sombrero2", P([(-7.8, -4.2), (-4.4, -8.4), (0, -10.0), (4.4, -8.4), (7.8, -4.2)]), 0.7)
        L.elipse("sombrero3", *p(2.6, -6.8), 1.2, 1.2)
    elif som == "gorro":
        L.poligono("sombrero", P([(-6.6, -4.4), (-4.4, -10.4), (0.6, -11.2), (6.6, -4.6)]))
        L.poligono("sombrero", P([(-4.6, -10.6), (-8.4, -11.8), (-9.8, -9.0), (-5.4, -8.2)]))
        L.capsula("sombrero2", P([(-6.8, -4.2), (6.8, -4.5)]), 2.3)
        L.elipse("sombrero2", *p(-9.6, -9.2), 1.8, 1.8)
    elif som == "turbante":
        L.elipse("sombrero", *p(-0.2, -5.4), 6.8, 3.8, inc)
        L.capsula("sombrero2", P([(-5.8, -3.8), (3.6, -8.4)]), 0.75)
        L.capsula("sombrero2", P([(-3.6, -3.2), (6.0, -6.4)]), 0.75)
        L.elipse("sombrero3", *p(3.6, -5.6), 1.1, 1.1)
        L.capsula("sombrero2", P([(3.6, -6.6), (4.6, -10.2)]), 0.8)
    elif som == "boina":
        L.elipse("sombrero", *p(-0.8, -5.9), 6.8, 2.5, inc - 8)
        L.capsula("sombrero", P([(-0.4, -8.0), (0.2, -9.4)]), 0.8)
    elif som == "frigio":
        L.poligono("sombrero", P([(-6.2, -4.4), (-6.4, -9.2), (-2.2, -12.2), (3.8, -11.6), (6.8, -9.6), (4.4, -8.8), (6.4, -4.6)]))
        L.elipse("sombrero3", *p(-3.6, -6.4), 1.6, 1.6)
        L.elipse("sombrero2", *p(-3.6, -6.4), 0.8, 0.8)
    elif som == "punta":
        L.poligono("sombrero", P([(-5.6, -4.6), (0.4, -13.4), (5.8, -4.8)]))
        L.capsula("sombrero2", P([(-6.8, -4.4), (6.8, -4.7)]), 2.6)
        L.elipse("sombrero3", *p(0.4, -13.6), 1.0, 1.0)
    elif som == "laurel":
        for k in range(9):
            ang = math.radians(196 + k * 17)
            x, y = 6.2 * math.cos(ang), -1.2 + 5.8 * math.sin(ang)
            L.elipse("sombrero", *p(x, y), 1.5, 0.7, inc + math.degrees(ang) + 90)
    elif som == "casco":
        pts = [p(0.6 + 9.8 * math.cos(t), -1.4 + 9.4 * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 60)]
        L.capsula("vidrio", pts, 1.1)
        L.capsula("sombreroh", [p(0.6 + 8.2 * math.cos(t), -1.4 + 7.8 * math.sin(t)) for t in np.linspace(math.radians(200), math.radians(250), 8)], 0.9)


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if not som: return
    dx = desv * 1.6
    if som == "salacot":
        L.elipse("sombrero", cx + dx, cy - 5.4, 6.2, 3.6)
        L.capsula("sombrero", [(cx - 7.8 + dx, cy - 4.2), (cx + 7.8 + dx, cy - 4.2)], 1.5)
        L.capsula("sombrero2", [(cx - 5.8 + dx, cy - 5.0), (cx + 5.8 + dx, cy - 5.0)], 1.1)
    elif som == "bicornio":
        L.poligono("sombrero", [(cx - 8.6 + dx, cy - 4.0), (cx - 4 + dx, cy - 9.4), (cx + dx, cy - 10.8), (cx + 4 + dx, cy - 9.4), (cx + 8.6 + dx, cy - 4.0), (cx + dx, cy - 6.2)])
        L.capsula("sombrero2", [(cx - 8.2 + dx, cy - 4.3), (cx - 4 + dx, cy - 9.0), (cx + dx, cy - 10.4), (cx + 4 + dx, cy - 9.0), (cx + 8.2 + dx, cy - 4.3)], 0.7)
    elif som == "gorro":
        L.poligono("sombrero", [(cx - 6.6, cy - 4.4), (cx - 2.2, cy - 11), (cx + 3.4, cy - 13.4), (cx + 4.8, cy - 10.2), (cx + 6.6, cy - 4.4)])
        L.capsula("sombrero2", [(cx - 7.0, cy - 4.3), (cx + 7.0, cy - 4.3)], 2.3)
        L.elipse("sombrero2", cx + 4.6, cy - 13.6, 1.6, 1.6)
    elif som == "turbante":
        L.elipse("sombrero", cx + dx, cy - 5.4, 7.2, 3.8)
        L.capsula("sombrero2", [(cx - 6.2 + dx, cy - 4.0), (cx + 4.4 + dx, cy - 8.4)], 0.75)
        L.capsula("sombrero2", [(cx - 4.0 + dx, cy - 3.2), (cx + 6.4 + dx, cy - 6.6)], 0.75)
    elif som == "boina":
        L.elipse("sombrero", cx + dx, cy - 6.0, 7.2, 2.5)
        L.capsula("sombrero", [(cx + dx, cy - 8.0), (cx + 0.6 + dx, cy - 9.4)], 0.8)
    elif som == "frigio":
        L.poligono("sombrero", [(cx - 6.4, cy - 4.4), (cx - 5.8, cy - 10.0), (cx, cy - 12.6), (cx + 4.2, cy - 11.2), (cx + 2.4, cy - 9.6), (cx + 6.4, cy - 4.4)])
    elif som == "punta":
        L.poligono("sombrero", [(cx - 6.0, cy - 4.6), (cx, cy - 13.6), (cx + 6.0, cy - 4.6)])
        L.capsula("sombrero2", [(cx - 7.2, cy - 4.4), (cx + 7.2, cy - 4.4)], 2.6)
        L.elipse("sombrero3", cx, cy - 13.8, 1.0, 1.0)
    elif som == "laurel":
        for k in range(10):
            ang = math.radians(190 + k * 17.7)
            L.elipse("sombrero", cx + 6.4 * math.cos(ang), cy - 1.2 + 5.8 * math.sin(ang), 1.5, 0.7, math.degrees(ang) + 90)
    elif som == "casco":
        L.capsula("vidrio", [(cx + 10.0 * math.cos(t), cy - 1.6 + 9.6 * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 60)], 1.1)
        L.capsula("sombreroh", [(cx + 8.4 * math.cos(t), cy - 1.6 + 8.0 * math.sin(t)) for t in np.linspace(math.radians(200), math.radians(250), 8)], 0.9)


def collar(L, C, cab, acc):
    a, b, px, py = _cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.55, a[1] + (b[1] - a[1]) * 0.55)
    e1, e2 = (m[0] - 4.4 * px, m[1] - 4.4 * py), (m[0] + 4.4 * px, m[1] + 4.4 * py)
    L.capsula("collar", [e1, e2], 1.6)
    j = max((e1, e2), key=lambda q: q[0] + q[1])
    if acc == "colgante":
        L.elipse("joya", j[0] - 0.4 * px, j[1] + 2.4, 2.2, 2.2)
        L.elipse("interior", j[0] - 0.4 * px, j[1] + 2.4, 0.95, 0.95)
    elif acc == "placa":   # distopía: placa de identificación colgada del collar
        x, y = j[0] - 0.4 * px, j[1] + 1.2
        L.poligono("joya", [(x - 1.6, y), (x + 1.6, y), (x + 1.6, y + 3.6), (x - 1.6, y + 3.6)])
        L.capsula("linea", [(x - 0.8, y + 1.3), (x + 0.8, y + 1.3)], 0.45)
    else:
        L.elipse("joya", j[0] - 0.6 * px, j[1] - 0.6 * py + 0.8, 1.0, 1.0)


CON_COLLAR = ("collar", "colgante", "placa", "chaleco", "traje", "tabardo", "bata")
CON_CHALECO = ("chaleco", "traje", "tabardo", "bata")


def dibujar(ps, acc):
    cu, som = acc
    L = Lienzo()
    H, C, cab = ps["H"], ps["C"], ps["cab"]
    L.curva("lejos", ps["ff"], [3.8, 3.4, 3.0, 2.9])
    L.capsula("lejos", ps["hf"][:2], 4.4)
    L.curva("lejos", ps["hf"][1:], [4.0, 3.0, 2.8])
    for k in ("ff", "hf"):
        L.elipse("lejos", ps[k][-1][0] + 0.6, ps[k][-1][1] - 0.2, 2.2, 1.2)
    c = L.curva("cola", ps["cola"], ps["colaw"])
    rnd = random.Random(ps["semilla"])
    if ps["erizo"] > 0:
        for i in range(4, len(c) - 1, 3):
            (x0, y0), (x1, y1) = c[i - 1], c[i + 1]
            dx, dy = x1 - x0, y1 - y0; n = math.hypot(dx, dy) or 1
            nx, ny = -dy / n, dx / n
            w = ps["colaw"][min(5, int(i / (len(c) - 1) * 5))] / 2
            for lado in (-1, 1):
                l = w + ps["erizo"] * rnd.uniform(1.0, 2.4)
                L.poligono("cola", [(c[i][0] - dx / n * 0.9, c[i][1] - dy / n * 0.9), (c[i][0] + lado * nx * l, c[i][1] + lado * ny * l),
                                    (c[i][0] + dx / n * 0.9, c[i][1] + dy / n * 0.9)])
    L.capsula("cuerpo", [H, C], ps["rh"] + ps["rc"] - 1)
    L.elipse("cuerpo", *H, ps["rh"], ps["rh"]); L.elipse("cuerpo", *C, ps["rc"], ps["rc"])
    mx, my = (H[0] + C[0]) / 2, (H[1] + C[1]) / 2
    if ps["lomo"] > 0:
        L.elipse("cuerpo", mx, my - ps["lomo"], math.hypot(C[0] - H[0], C[1] - H[1]) * 0.42, (ps["rh"] + ps["rc"]) / 2 * 0.8)
        if ps["erizo"] > 0:
            for k in range(5):
                x = mx - 6 + 3 * k; top = my - ps["lomo"] - (ps["rh"] + ps["rc"]) / 2 * 0.8 + 0.4
                L.poligono("cuerpo", [(x - 1.0, top + 1.2), (x + rnd.uniform(-0.4, 0.4), top - ps["erizo"] * rnd.uniform(1.0, 1.8)), (x + 1.0, top + 1.2)])
    if ps["bola"] is not None:
        bx, by = ps["bola"]
        L.elipse("juguete", bx, by, 2.6, 2.6)
        L.capsula("juguetel", [(bx - 1.8, by - 0.6), (bx + 1.6, by + 1.0)], 0.5)
        L.capsula("juguetel", [(bx - 0.8, by - 2.0), (bx + 0.6, by + 1.9)], 0.5)
    ang = math.degrees(math.atan2(C[1] - H[1], C[0] - H[0]))
    L.elipse("claro", mx + 1, my + (ps["rh"] + ps["rc"]) * 0.33, math.hypot(C[0] - H[0], C[1] - H[1]) * 0.42, 1.8, ang)
    L.capsula("cuerpo", [(C[0] + 2, C[1] - 5), (cab[0] - 1, cab[1] + 3.5)], 9)
    L.elipse("claro", C[0] + ps["rc"] * 0.5, C[1] - ps["rc"] * 0.3, 2.8, 4.0, 15)
    hip, rod = ps["hn"][0], ps["hn"][1]
    L.elipse("cuerpo", (hip[0] + rod[0]) / 2, (hip[1] + rod[1]) / 2 - 0.8, 4.0, 4.8,
             math.degrees(math.atan2(rod[1] - hip[1], rod[0] - hip[0])) - 90)
    L.curva("pierna", ps["hn"][1:], [3.8, 3.0, 2.8])
    L.curva("pierna", ps["fn"], [4.0, 3.5, 3.1, 3.0])
    for k in ("hn", "fn"):
        q = ps[k]; x, y = q[-1]
        dx, dy = x - q[-2][0], y - q[-2][1]; n = math.hypot(dx, dy) or 1
        L.capsula("pata", [(x - dx / n * 2.4, y - dy / n * 2.4), (x, y)], 3.2)
        L.elipse("pata", x + 0.7, y - 0.2, 2.3, 1.2)
    if cu in CON_COLLAR:
        collar(L, C, cab, cu)
    if cu in ("paliacate", "panuelo"):
        paliacate(L, C, cab)
    if cu in ("capa", "manta"):
        capa(L, ps)
    if cu in CON_CHALECO:
        chaleco(L, ps)
    cabeza(L, cab[0], cab[1], ps["inc"], ps["orejas"], ps["ojo"], ps["lengua"], cu)
    sombrero_lado(L, cab[0], cab[1], ps["inc"], som)
    return L.rejilla()


# ---------------------------------------------------------------------
#  La vuelta por la espalda: tres cuartos de espaldas y de espaldas
# ---------------------------------------------------------------------

def cabeza_espalda(L, cx, cy, desv):
    for sx in (-1, 1):
        k = 0.88 if (sx > 0 and desv > 0) else 1.0
        L.poligono("cabeza", [(cx + sx * 6.4 * k, cy - 1.6), (cx + sx * 4.8 * k, cy - 10.0), (cx + sx * 0.6, cy - 4.8)])
    L.elipse("cabeza", cx, cy, 6.5, 5.6)
    for sx in (-1, 1):
        L.elipse("cabeza", cx + sx * 4.4, cy + 2.2, 2.6, 2.2)
    if desv > 0:   # asoma la mejilla y la punta del hocico a la derecha
        L.elipse("cabeza", cx + 5.8, cy + 1.8, 2.2, 1.7)
        L.elipse("cabclaro", cx + 6.6, cy + 2.6, 1.2, 0.8)


def acc_espalda(L, cu, cx, ny, todo):
    """Accesorio visto de espaldas (todo=True) o en tres cuartos de espaldas. cx: centro del cuello; ny: altura."""
    if cu in CON_COLLAR:
        L.capsula("collar", [(cx - 4.6, ny), (cx + 4.6, ny)], 1.6)
    if cu in ("paliacate", "panuelo"):
        L.capsula("capa", [(cx - 4.9, ny), (cx + 4.9, ny)], 3.6)
        L.poligono("capa", [(cx, ny), (cx - 2.6, ny + 5.2), (cx - 0.6, ny + 5.6)])
        L.poligono("capa", [(cx, ny), (cx + 2.8, ny + 4.8), (cx + 0.8, ny + 5.6)])
    if cu in ("capa", "manta"):
        L.capsula("capa", [(cx - 4.8, ny), (cx + 4.8, ny)], 2.2)
        if todo:
            L.poligono("capa", [(cx - 5, ny - 0.5), (cx + 5, ny - 0.5), (cx + 8.8, ny + 10.5), (cx + 6.4, ny + 15.5), (cx, ny + 13.6),
                                (cx - 6.4, ny + 15.5), (cx - 8.8, ny + 10.5)])
        else:
            L.poligono("capa", [(cx + 3, ny - 0.6), (cx - 4, ny), (cx - 12, ny + 3.6), (cx - 19, ny + 7.5), (cx - 18.4, ny + 12.6),
                                (cx - 10, ny + 13.4), (cx - 2, ny + 9.4), (cx + 4, ny + 5)])
    if cu in CON_CHALECO:
        if todo:
            L.poligono("capa", [(cx - 7.4, ny + 2), (cx + 7.4, ny + 2), (cx + 8.0, ny + 12.5), (cx - 8.0, ny + 12.5)])
            L.capsula("broche", [(cx - 6.0, ny + 8.5), (cx + 6.0, ny + 8.5)], 0.9)
        else:
            L.poligono("capa", [(cx + 3, ny + 1.5), (cx - 6, ny + 3), (cx - 15, ny + 7), (cx - 16, ny + 12.4), (cx - 4, ny + 11.4), (cx + 4, ny + 7.4)])
            L.capsula("broche", [(cx - 13.5, ny + 9.5), (cx + 1.5, ny + 6.8)], 0.9)


def dibujar_giro(i, acc):
    """Vuelta por la espalda: 0 = tres cuartos de espaldas (mirando a la derecha y hacia el fondo), 1 = de espaldas."""
    cu, som = acc
    L = Lienzo()
    if i == 1:
        for x in (29.4, 34.6):
            L.capsula("lejos", [(x, 50), (x, 58.6)], 3.0); L.elipse("lejos", x, 59.2, 2.0, 1.1)
        L.elipse("cuerpo", 32, 43, 7.8, 9.0)
        L.elipse("cuerpo", 32, 35.5, 5.6, 4.0)
        for sx in (-1, 1):
            L.elipse("cuerpo", 32 + sx * 5.2, 49, 5.0, 6.0)
            L.curva("pierna", [(32 + sx * 5.6, 52), (32 + sx * 6.0, 56), (32 + sx * 5.8, SUELO)], [3.8, 3.4, 3.2])
            L.capsula("pata", [(32 + sx * 5.8, 57.6), (32 + sx * 5.8, SUELO)], 3.2); L.elipse("pata", 32 + sx * 5.8, 59.3, 2.2, 1.2)
        acc_espalda(L, cu, 32, 34.5, True)
        L.curva("colafrente", [(32, 50), (32.4, 44), (33.2, 38), (35.4, 32.5), (38.2, 30.5), (40, 32)], ANCHO_COLA)
        cabeza_espalda(L, 32, 28, 0.0)
        sombrero_frente(L, 32, 28, som)
    else:
        L.curva("lejos", [(36, 46), (37, 53), (37.5, SUELO)], [3.6, 3.2, 3.0]); L.elipse("lejos", 38, 59.3, 2.0, 1.1)
        L.curva("lejos", [(40, 45), (41, 52), (41.6, SUELO)], [3.4, 3.0, 2.8]); L.elipse("lejos", 42, 59.3, 2.0, 1.1)
        L.curva("lejos", [(30, 50), (30.5, 55), (30.2, SUELO)], [3.6, 3.0, 2.8]); L.elipse("lejos", 30.6, 59.3, 2.0, 1.1)
        L.capsula("cuerpo", [(26, 45), (38, 40.5)], 13.0)
        L.elipse("cuerpo", 25.5, 46, 8.0, 8.0)
        L.elipse("cuerpo", 38.5, 40, 6.2, 6.4)
        L.elipse("cuerpo", 40.5, 34.5, 4.6, 3.6)
        L.curva("pierna", [(22, 51), (21, 55.5), (22, SUELO)], [4.0, 3.4, 3.2])
        L.capsula("pata", [(22, 57.6), (22, SUELO)], 3.2); L.elipse("pata", 22.4, 59.3, 2.3, 1.2)
        acc_espalda(L, cu, 40.5, 33.5, False)
        L.curva("colafrente", [(23, 45), (19, 40.5), (17.5, 34), (18.5, 28), (21.5, 25.5), (23.6, 27.2)], ANCHO_COLA)
        cabeza_espalda(L, 41.5, 28, 0.6)
        sombrero_frente(L, 41.5, 28, som, 0.6)
    return L.rejilla()


# ---------------------------------------------------------------------
#  Acabado (con patrones de accesorio y sin rellenar el interior del casco)
# ---------------------------------------------------------------------

def acabar(ch, parte, estilo):
    rayas, cu, som, pat = estilo
    afuera = set(); pila = [(r, c) for r in range(N) for c in (0, N - 1)] + [(r, c) for c in range(N) for r in (0, N - 1)]
    while pila:
        r, c = pila.pop()
        if (r, c) in afuera or not (0 <= r < N and 0 <= c < N) or ch[r][c] != ".": continue
        afuera.add((r, c)); pila += [(r + 1, c), (r - 1, c), (r, c + 1), (r, c - 1)]
    ch = [f[:] for f in ch]; parte = [f[:] for f in parte]
    vistos = set()
    for r in range(N):           # solo se rellenan huecos chicos (el interior del casco se queda vacío)
        for c in range(N):
            if ch[r][c] == "." and (r, c) not in afuera and (r, c) not in vistos:
                comp, pila = [], [(r, c)]
                while pila:
                    q = pila.pop()
                    if q in vistos or not (0 <= q[0] < N and 0 <= q[1] < N) or ch[q[0]][q[1]] != ".": continue
                    vistos.add(q); comp.append(q); pila += [(q[0] + 1, q[1]), (q[0] - 1, q[1]), (q[0], q[1] + 1), (q[0], q[1] - 1)]
                if len(comp) <= 6:
                    for rr, cc in comp:
                        ch[rr][cc], parte[rr][cc] = "P", "cuerpo"
    g = [f[:] for f in ch]
    for r in range(N):
        for c in range(N):
            if g[r][c] == "P" and parte[r][c] in ("cuerpo", "cola", "pierna", "colafrente"):
                if rayas == "atigrado" and (c + 0.2 * r + 1.2 * math.sin(r * 0.5)) % 6 < 1.4:
                    g[r][c] = "W"
                elif rayas == "mechones" and (c + r) % 4 == 0 and (c * 7 + r * 13) % 17 < 7:
                    g[r][c] = "W"
            if g[r][c] == "A" and parte[r][c] == "capa":
                if pat == "cuadricula" and r % 3 == 0 and c % 2 == 0: g[r][c] = "Y"
                elif pat == "puntos" and r % 2 == 0 and (c + r // 2) % 3 == 0: g[r][c] = "Y"
                elif pat == "rayas" and r % 3 == 0: g[r][c] = "Y"
    out = [f[:] for f in g]
    dentro = lambda r, c: 0 <= r < N and 0 <= c < N and ch[r][c] != "."
    for r in range(N):
        for c in range(N):
            if g[r][c] in "PLWAY" and parte[r][c] in RANGO and parte[r][c] not in ("collar", "auri", "sombrero"):
                for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    rr, cc = r + dr, c + dc
                    if dentro(rr, cc) and parte[rr][cc] in RANGO and RANGO[parte[rr][cc]] < RANGO[parte[r][c]] and not (parte[r][c] == "pierna" and parte[rr][cc] == "cuerpo"):
                        out[r][c] = "S" if g[r][c] in "PLW" else "B"; break
    for r in range(N):
        for c in range(N):
            if ch[r][c] in ".K" or (parte[r][c] == "sombrero" and ch[r][c] in "OH"):
                continue
            if any(not dentro(r + dr, c + dc) for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                out[r][c] = "K"
    for r in range(N - 1):
        for c in range(N):
            if out[r][c] in "PLW" and out[r + 1][c] == "K":
                out[r][c] = "S"
            elif out[r][c] in "AY" and out[r + 1][c] == "K":
                out[r][c] = "B"
    return out


def rejilla(anim, i, t, espejo=False):
    estilo = (t["rayas"], t["acc"][0], t["acc"][1], t["patron_acc"])
    k = (anim, i, estilo, espejo)
    if k not in _rej:
        b = (anim, i, estilo, False)
        if b not in _rej:
            ch, parte = dibujar_giro(i, t["acc"]) if anim == "girar" else dibujar(ANIMS[anim][i], t["acc"])
            _rej[b] = acabar(ch, parte, estilo)
        _rej[k] = [f[::-1] for f in _rej[b]] if espejo else _rej[b]
    return _rej[k]


# ---------------------------------------------------------------------
#  Temas: los cuatro de antes y los diecisiete nuevos
# ---------------------------------------------------------------------
_tema7 = tema


def pelaje(P, K, **k):
    """Colores del gato a partir del pelaje (P) y la tinta del contorno (K); lo demás se deriva si no se da."""
    c = dict(P=P, K=K)
    c["S"] = k.get("S", mezcla(P, K, 0.28)); c["L"] = k.get("L", mezcla(P, (255, 255, 255), 0.55))
    c["W"] = k.get("W", mezcla(P, K, 0.18)); c["D"] = k.get("D", mezcla(c["S"], K, 0.3)); c["T"] = k.get("T", mezcla(P, K, 0.45))
    c["E"] = k.get("E", (90, 140, 70)); c["N"] = k.get("N", (226, 150, 160)); c["C"] = k.get("C", K); c["G"] = k.get("G", (210, 174, 90))
    c["A"] = k.get("A", c["C"]); c["B"] = k.get("B", mezcla(c["A"], K, 0.45)); c["Y"] = k.get("Y", c["L"])
    c["M"] = k.get("M", c["A"]); c["Z"] = k.get("Z", c["B"]); c["O"] = k.get("O", (200, 225, 255)); c["J"] = k.get("J", c["A"])
    c["R"] = k.get("R", mezcla(c["N"], P, 0.35)); c["H"] = (255, 255, 255)
    return c


def T(col, fondo, texto, borde, rayas="ninguno", material="plano", acc=("collar", None), patron_acc=None, **extra):
    d = dict(col=col, fondo=fondo, texto=texto, borde=borde, sombra=mezcla(fondo, texto, 0.18), rayas=rayas, material=material,
             material_banda=material, acc=acc, patron_acc=patron_acc)
    d.update(extra)
    return d


def tema(nombre, oscuro=False):
    if nombre in ("romano", "gotico", "medieval", "ciber"):
        t = _tema7(nombre, oscuro)
        col = dict(t["col"]); col.setdefault("M", col["A"]); col.setdefault("Z", col["B"]); col.setdefault("O", (200, 225, 255))
        return dict(t, col=col, acc=(t["acc"], None), patron_acc="cuadricula" if nombre == "medieval" else None,
                    material_banda="plano" if nombre == "gotico" else t["material"])
    if nombre == "botanico":
        # botanicoClaro: papel EFE8D6, tinta 2A2318, musgo 3F5A2C, óxido 8C4A22, verde escarabajo 2E5E5A
        P = mezcla(hx("E3D9C0"), hx("8C4A22"), 0.42)
        col = pelaje(P, hx("2A2318"), W=mezcla(P, hx("5E3A1A"), 0.45), L=hx("F7F2E4"), E=mezcla(hx("3F5A2C"), hx("A9C27A"), 0.4),
                     C=hx("3F5A2C"), G=hx("2E5E5A"), M=mezcla(hx("E3D9C0"), hx("FFFFFF"), 0.25), Z=hx("3F5A2C"), J=hx("8C4A22"))
        return T(col, hx("EFE8D6"), hx("2A2318"), hx("BFAF8A"), "atigrado", "puntocruz", ("collar", "salacot"))
    if nombre == "distopia":
        # distopiaClaro: papel DEDBD2, tinta 141414, rojo de cartel B3121B
        col = pelaje(hx("ECE9E1"), hx("141414"), S=hx("A7A296"), W=hx("ECE9E1"), L=hx("FFFFFF"), D=hx("6E6B63"), T=hx("474744"),
                     E=hx("B3121B"), N=hx("B3121B"), R=hx("ECE9E1"), C=hx("141414"), G=hx("ECE9E1"), J=hx("B3121B"))
        return T(col, hx("DEDBD2"), hx("141414"), hx("A7A296"), "ninguno", "dos_tintas", ("placa", None),
                 tintas=dict(papel=hx("ECE9E1"), negro=hx("141414"), rojo=hx("B3121B")))
    if nombre == "heleno":
        # helenoClaro: púrpura 5A2A82, azul 1E5F8C, oro A27B22; barro = oro mezclado con el rojo de juegoTonos (8E2C48)
        barro = mezcla(hx("A27B22"), hx("8E2C48"), 0.45)
        col = pelaje(hx("1E1410"), hx("1E1410"), M=hx("5A2A82"), C=hx("5A2A82"), E=hx("F3EFE6"))
        return T(col, barro, hx("1E1410"), hx("1E1410"), "ninguno", "figuras_negras", ("ninguno", "laurel"),
                 terracota=barro, añadido=hx("5A2A82"), blanco=hx("F3EFE6"))
    if nombre == "refugio":
        P = mezcla(hx("F8F1DE"), hx("8A928F"), 0.45)
        col = pelaje(P, hx("1F2A2A"), W=mezcla(P, hx("4C5752"), 0.3), E=hx("D9572B"), C=hx("D9572B"), A=hx("1C6E6A"),
                     B=mezcla(hx("1C6E6A"), hx("1F2A2A"), 0.4), G=hx("B98A15"))
        return T(col, hx("F1E8D0"), hx("1F2A2A"), hx("CDBE95"), "mechones", "plano", ("traje", None))
    if nombre == "orden":
        P = mezcla(hx("D3D7DC"), hx("4B525A"), 0.35)
        col = pelaje(P, hx("15181B"), W=mezcla(P, hx("2F4858"), 0.35), E=mezcla(hx("7E6219"), hx("F2D27A"), 0.5), C=hx("7E6219"),
                     A=hx("9E1B22"), B=mezcla(hx("9E1B22"), hx("15181B"), 0.45), G=mezcla(hx("7E6219"), hx("F2D27A"), 0.35))
        return T(col, hx("E2E5E8"), hx("15181B"), hx("AEB4BB"), "atigrado", "plano", ("tabardo", None))
    if nombre == "laboratorio":
        P = mezcla(hx("E6EDF2"), hx("4D5D6B"), 0.5)
        col = pelaje(P, hx("12202B"), E=mezcla(hx("5E4FD1"), hx("FFFFFF"), 0.15), C=hx("5E4FD1"), A=hx("FFFFFF"), B=hx("0B8FA1"),
                     G=hx("0B8FA1"), J=hx("0B8FA1"))
        return T(col, hx("F4F7F9"), hx("12202B"), hx("CBD6DF"), "ninguno", "plano", ("bata", None))
    if nombre == "colonos":
        P = mezcla(hx("F8F3E8"), hx("7A5A2E"), 0.45)
        col = pelaje(P, hx("1F2A3A"), W=mezcla(P, hx("7A5A2E"), 0.55), E=mezcla(hx("7A5A2E"), hx("E3C27A"), 0.6), A=hx("A23B2A"),
                     Y=hx("F8F3E8"), C=hx("1F3A5F"))
        return T(col, hx("F2EBDC"), hx("1F2A3A"), hx("CDBFA2"), "atigrado", "plano", ("panuelo", None), "puntos")
    if nombre == "napoleon":
        P = mezcla(hx("EAE1CB"), hx("4F5874"), 0.45)
        col = pelaje(P, hx("14213D"), E=hx("B8892B"), C=hx("A3172E"), G=hx("D9B25B"), M=hx("14213D"), Z=hx("B8892B"))
        return T(col, hx("F4EEDF"), hx("14213D"), hx("CDBF9C"), "ninguno", "plano", ("collar", "bicornio"))
    if nombre == "navidad":
        col = pelaje(hx("E8A35E"), hx("2A1A10"), W=hx("C27434"), L=hx("F3EFE6"), E=hx("66BB6A"), C=hx("66BB6A"), G=hx("F2C94C"),
                     M=hx("E5484D"), Z=hx("F3EFE6"), J=hx("E5484D"))
        return T(col, hx("0E1A14"), hx("F3EFE6"), hx("2E4A39"), "atigrado", "plano", ("collar", "gorro"), sombra=hx("C8D2CC"))
    if nombre == "seda":
        P = mezcla(hx("F5EEDC"), hx("B98A4A"), 0.5)
        col = pelaje(P, hx("1F1B3A"), W=mezcla(P, hx("8A5A2A"), 0.35), E=hx("1A8C8C"), A=hx("C0392B"), B=hx("23408E"), Y=hx("23408E"),
                     G=hx("E0B04F"))
        return T(col, hx("F5EEDC"), hx("1F1B3A"), hx("D4C29A"), "mechones", "plano", ("manta", None), "rayas")
    if nombre == "urss":
        col = pelaje(hx("F7F0DE"), hx("161414"), S=hx("C9B994"), W=hx("F7F0DE"), L=hx("FFFFFF"), D=hx("8A7F6A"), T=hx("4A4440"),
                     E=hx("161414"), N=hx("C1121F"), R=hx("F7F0DE"), A=hx("C1121F"), C=hx("C1121F"))
        return T(col, hx("EFE6D2"), hx("161414"), hx("C9B994"), "ninguno", "dos_tintas", ("panuelo", None),
                 tintas=dict(papel=hx("F7F0DE"), negro=hx("161414"), rojo=hx("C1121F")))
    if nombre == "espacio":
        col = pelaje(hx("9AA3B5"), hx("0A0F1E"), W=hx("6E778A"), L=hx("E8ECF8"), E=hx("FFB86B"), C=hx("FFB86B"), A=hx("F2F4FA"),
                     B=hx("FFB86B"), G=hx("8AB4FF"), O=mezcla(hx("8AB4FF"), hx("FFFFFF"), 0.35), J=hx("C59BFF"))
        return T(col, hx("03050C"), hx("E8ECF8"), hx("26324F"), "atigrado", "plano", ("traje", "casco"), sombra=hx("1A2033"))
    if nombre == "andalus":
        P = mezcla(hx("F3EBDA"), hx("B5432A"), 0.38)
        col = pelaje(P, hx("2A1A12"), W=mezcla(P, hx("B5432A"), 0.4), E=hx("1E6B5C"), C=hx("2A4F9E"), G=hx("E0B65A"),
                     M=hx("FBF6EA"), Z=hx("1E6B5C"))
        return T(col, hx("F3EBDA"), hx("2A1A12"), hx("D2C29E"), "atigrado", "azulejo", ("collar", "turbante"), junta=hx("FBF6EA"))
    if nombre == "mongol":
        P = mezcla(hx("E6DFCF"), hx("7A6E62"), 0.5)
        col = pelaje(P, hx("18202E"), W=mezcla(P, hx("3E352C"), 0.35), E=hx("C48A1E"), C=hx("1F5FA8"), G=hx("C48A1E"),
                     M=hx("C2412D"), Z=hx("8A6A44"))
        return T(col, hx("F1EDE3"), hx("18202E"), hx("CCC0A6"), "mechones", "plano", ("collar", "punta"))
    if nombre == "renacimiento":
        col = pelaje(hx("F7EDD4"), hx("3A2614"), W=mezcla(hx("F7EDD4"), hx("8C3B1E"), 0.3), E=hx("3A2614"), M=hx("8C3B1E"), Z=hx("3A2614"))
        return T(col, hx("F0E4C6"), hx("3A2614"), hx("CBB687"), "mechones", "boceto", ("ninguno", "boina"),
                 sepia=hx("3A2614"), sanguina=hx("8C3B1E"), papel=hx("F7EDD4"))
    if nombre == "revolucion":
        P = mezcla(hx("FAF6EC"), hx("57524A"), 0.4)
        col = pelaje(P, hx("1C1A17"), W=mezcla(P, hx("1C1A17"), 0.25), E=hx("3F6B3A"), C=hx("1F3A6E"), G=hx("FFFFFF"),
                     M=hx("B22234"), Z=hx("1F3A6E"))
        return T(col, hx("F2ECDD"), hx("1C1A17"), hx("C8BB9C"), "mechones", "plano", ("collar", "frigio"))
    if nombre == "antiguas":
        P = mezcla(hx("DCD5C3"), hx("57534A"), 0.45)
        col = pelaje(P, hx("1E1D1A"), W=mezcla(P, hx("1E1D1A"), 0.25), E=hx("9A7B2E"), A=hx("2E5A3E"), B=hx("1F3A28"), G=hx("8E1B24"))
        return T(col, hx("E9E3D4"), hx("1E1D1A"), hx("B9B09A"), "atigrado", "plano", ("capa", None))
    raise ValueError(nombre)


# ---------------------------------------------------------------------
#  Materiales nuevos
# ---------------------------------------------------------------------

def pintar_puntocruz(g, t, cel):
    """Botánico: cada pixel es una cruz de hilo (punto de cruz), con el hilo de arriba más claro."""
    s = cel * SS
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    m, w = 0.1 * s, max(1, int(0.42 * s))
    for r, c, v in celdas(g):
        col = t["col"][v]; x0, y0 = c * s, r * s
        d.rectangle([x0 + 0.12 * s, y0 + 0.12 * s, x0 + 0.88 * s, y0 + 0.88 * s], fill=esc(col, 0.9) + (150,))
        d.line([x0 + m, y0 + s - m, x0 + s - m, y0 + m], fill=esc(col, 0.78) + (255,), width=w)
        d.line([x0 + m, y0 + m, x0 + s - m, y0 + s - m], fill=col + (255,), width=w)
        d.line([x0 + m + w * 0.3, y0 + m, x0 + s - m, y0 + s - m - w * 0.3], fill=esc(col, 1.15) + (255,), width=max(1, int(w * 0.3)))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def clase_tinta(col):
    r, g_, b = col
    lum = 0.3 * r + 0.59 * g_ + 0.11 * b
    if r > g_ + 50 and r > b + 40: return "rojo", 0
    if lum < 75: return "negro", 1.0
    if lum < 205: return "trama", (205 - lum) / 130
    return "papel", 0


def pintar_dos_tintas(g, t, cel, tintas=None, celdas_iter=None, ancho=N, alto=N, opaco=True, semilla=0):
    """Distopía / soviético: impreso a dos tintas (negro y rojo) sobre papel; los grises son trama de puntos; el rojo, algo corrido."""
    tin = tintas or t["tintas"]
    s = cel * SS
    img = Image.new("RGBA", (ancho * s, alto * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    items = list(celdas_iter if celdas_iter is not None else ((r, c, t["col"][v]) for r, c, v in celdas(g)))
    rojos = []
    for r, c, col in items:
        x0, y0 = c * s, r * s
        k, f = clase_tinta(col)
        if opaco: d.rectangle([x0, y0, x0 + s, y0 + s], fill=tin["papel"] + (255,))
        if k == "negro":
            d.rectangle([x0, y0, x0 + s, y0 + s], fill=tin["negro"] + (255,))
        elif k == "trama":
            rr = s * 0.62 * math.sqrt(max(0.05, min(1.0, f)))
            d.ellipse([x0 + s / 2 - rr, y0 + s / 2 - rr, x0 + s / 2 + rr, y0 + s / 2 + rr], fill=tin["negro"] + (255,))
        elif k == "rojo":
            rojos.append((x0, y0))
    for x0, y0 in rojos:   # el rojo va en otra pasada y queda un poco corrido (mal registro)
        d.rectangle([x0 + 0.14 * s, y0 - 0.1 * s, x0 + s + 0.14 * s, y0 + s - 0.1 * s], fill=tin["rojo"] + (255,))
    rnd = random.Random(semilla + 7)
    for r, c, col in items:   # tinta gastada: puntitos sin tinta
        if rnd.random() < 0.08:
            x, y = c * s + rnd.uniform(0, s), r * s + rnd.uniform(0, s)
            d.ellipse([x - 0.08 * s, y - 0.08 * s, x + 0.08 * s, y + 0.08 * s], fill=tin["papel"] + (255,))
    return img.resize((ancho * cel, alto * cel), Image.LANCZOS)


def pintar_figuras_negras(g, t, cel):
    """Helénico: figura negra de los vasos; los contornos internos quedan incisos (se ve el barro) y el laurel va en púrpura."""
    neg = (30, 20, 16); inc = mezcla(neg, t["terracota"], 0.6)
    mapa = {"E": t["blanco"], "H": t["blanco"], "S": inc, "M": t["añadido"], "Z": t["añadido"], "G": t["añadido"], "C": t["añadido"],
            "N": t["añadido"], "J": t["añadido"]}
    img = Image.new("RGBA", (N * cel, N * cel), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    for r, c, v in celdas(g):
        d.rectangle([c * cel, r * cel, (c + 1) * cel - 1, (r + 1) * cel - 1], fill=mapa.get(v, neg) + (255,))
    return img


def pintar_boceto(g, t, cel):
    """Renacimiento: dibujo a tinta sepia con sombreado de sanguina, como en los cuadernos de Leonardo."""
    s = cel * SS
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    sep, san, pap = t["sepia"], t["sanguina"], t["papel"]
    w = max(1, int(0.13 * s))
    for r, c, v in celdas(g):
        x0, y0 = c * s, r * s
        rnd = random.Random(r * 131 + c * 17)
        d.rectangle([x0, y0, x0 + s, y0 + s], fill=esc(pap, 0.97) + (245,))
        j = lambda: rnd.uniform(-0.08, 0.08) * s
        if v == "K":
            d.rectangle([x0 + j(), y0 + j(), x0 + s + j(), y0 + s + j()], fill=sep + (255,))
        elif v in "SDT":
            for k in (0.25, 0.75):
                d.line([x0 + k * s - 0.25 * s + j(), y0 + s + j(), x0 + k * s + 0.25 * s + j(), y0 + j()], fill=san + (255,), width=w)
        elif v == "W":
            d.line([x0 + j(), y0 + s + j(), x0 + s + j(), y0 + j()], fill=san + (200,), width=w)
        elif v == "P":
            if (r + c) % 3 == 0:
                d.line([x0 + j(), y0 + s + j(), x0 + s + j(), y0 + j()], fill=san + (190,), width=w)
        elif v in "EG":
            d.ellipse([x0 + 0.2 * s, y0 + 0.2 * s, x0 + 0.8 * s, y0 + 0.8 * s], fill=sep + (230,))
        elif v in "NR":
            d.rectangle([x0, y0, x0 + s, y0 + s], fill=san + (110,))
        elif v in "MZABCJY":
            d.line([x0 + j(), y0 + s + j(), x0 + s + j(), y0 + j()], fill=san + (230,), width=w)
            d.line([x0 + j(), y0 + j(), x0 + s + j(), y0 + s + j()], fill=san + (230,), width=w)
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def pintar_azulejo(g, t, cel):
    """Al-Ándalus: cada pixel es un azulejo esmaltado, con junta blanca y su brillo."""
    s = cel * SS
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    gap = 0.1 * s
    for r, c, v in celdas(g):
        d.rectangle([c * s, r * s, (c + 1) * s, (r + 1) * s], fill=t["junta"] + (255,))
    for r, c, v in celdas(g):
        col = t["col"][v]; x0, y0 = c * s + gap / 2, r * s + gap / 2; x1, y1 = (c + 1) * s - gap / 2, (r + 1) * s - gap / 2
        d.rectangle([x0, y0, x1, y1], fill=col + (255,))
        d.ellipse([x0 + 0.12 * s, y0 + 0.1 * s, x0 + 0.42 * s, y0 + 0.32 * s], fill=esc(col, 1.25) + (255,))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def pintar(g, t, cel, oscuro=False):
    m = t["material"]
    if m == "romano": return pintar_romano(g, t, cel)
    if m == "ciber": return pintar_ciber(g, t, cel)
    if m == "gotico": return pintar_gotico(g, t, cel, oscuro)
    if m == "medieval": return pintar_medieval(g, t, cel)
    if m == "puntocruz": return pintar_puntocruz(g, t, cel)
    if m == "dos_tintas": return pintar_dos_tintas(g, t, cel)
    if m == "figuras_negras": return pintar_figuras_negras(g, t, cel)
    if m == "boceto": return pintar_boceto(g, t, cel)
    if m == "azulejo": return pintar_azulejo(g, t, cel)
    return pintar_plano(g, t, cel)


# ---------------------------------------------------------------------
#  Fondos de los temas nuevos (casi todos a 1 pixel por dp, como el gato)
# ---------------------------------------------------------------------
W_DP, H_DP = ANCHO_DP, ALTO_DP
EXTRA = {}   # cosas animadas de cada fondo (luces, nieve, estrellas)


def dp(col):
    im = Image.new("RGB", (W_DP, H_DP), col)
    return im, ImageDraw.Draw(im)


def subir(im):
    return im.resize((W_DP * ESC, H_DP * ESC), Image.NEAREST)


def papel(im_big, fuerza=0.035, semilla=3):
    W, H = im_big.size
    rng = np.random.default_rng(semilla)
    ruido = Image.fromarray(((rng.normal(0, 1, (H // 18 + 2, W // 18 + 2)) * 40 + 128).clip(0, 255)).astype(np.uint8)).resize((W, H), Image.BICUBIC)
    rr = (np.asarray(ruido, np.float32) - 128) / 128
    a = np.asarray(im_big, np.float32) * (1 + fuerza * rr)[..., None]
    return Image.fromarray(np.clip(a, 0, 255).astype(np.uint8))


def cruz(d, x0, y0, s, col):
    d.line([x0, y0 + s - 1, x0 + s - 1, y0], fill=esc(col, 0.78))
    d.line([x0, y0, x0 + s - 1, y0 + s - 1], fill=col)


def fondo_botanico(t):
    tela = hx("EFE8D6")
    big = Image.new("RGB", (W_DP * ESC, H_DP * ESC), tela); d = ImageDraw.Draw(big)
    hueco = mezcla(tela, hx("BFAF8A"), 0.5)
    for y in range(0, H_DP * ESC, ESC):
        for x in range(0, W_DP * ESC, ESC):
            d.point((x, y), fill=hueco)
    m = Image.new("RGBA", (W_DP, H_DP), (0, 0, 0, 0)); dm = ImageDraw.Draw(m)
    musgo, oxido, teal, sepia, rojo = hx("3F5A2C"), hx("8C4A22"), hx("2E5E5A"), hx("5E5340"), hx("8A1A1A")
    for x in range(W_DP):
        dm.point((x, 2 + (1 if (x // 2) % 2 else 0)), fill=musgo + (255,))
    for x0 in range(14, W_DP, 48):        # helechos
        for k in range(16):
            x, y = x0 + int(k * 0.4), 27 - k
            dm.point((x, y), fill=musgo + (255,))
            if k % 2 == 0 and k < 15:
                for j in range(1, max(2, 5 - k // 4)):
                    dm.point((x - j, y + j // 2), fill=musgo + (255,)); dm.point((x + j, y + j // 2), fill=musgo + (255,))
    for x0 in range(38, W_DP, 48):        # escarabajos clavados con alfiler
        y0 = 14
        dm.point((x0, y0 - 3), fill=sepia + (255,))
        dm.line([x0 - 1, y0 - 2, x0 + 1, y0 - 2], fill=mezcla(teal, (0, 0, 0), 0.3) + (255,))
        dm.rectangle([x0 - 2, y0 - 1, x0 + 2, y0 + 3], fill=teal + (255,))
        dm.line([x0, y0 - 1, x0, y0 + 3], fill=mezcla(teal, (0, 0, 0), 0.4) + (255,))
        dm.point((x0 - 1, y0), fill=mezcla(teal, (255, 255, 255), 0.4) + (255,))
        dm.point((x0, y0 + 1), fill=rojo + (255,))
        for yy in (y0 - 1, y0 + 1, y0 + 3):
            dm.point((x0 - 3, yy), fill=sepia + (255,)); dm.point((x0 + 3, yy), fill=sepia + (255,))
    dm.line([0, 75, W_DP, 75], fill=sepia + (255,))
    for x0 in range(4, W_DP, 12):          # hilera de hormigas
        for dx in (0, 2, 4):
            dm.point((x0 + dx, 77), fill=oxido + (255,))
        for dx in (1, 3):
            dm.point((x0 + dx, 78), fill=sepia + (255,)); dm.point((x0 + dx, 76), fill=sepia + (255,))
    a = np.asarray(m)
    for y, x in zip(*np.nonzero(a[..., 3])):
        cruz(d, x * ESC, y * ESC, ESC, tuple(int(v) for v in a[y, x, :3]))
    return big


def dos_tintas_fondo(t, dibujar_fn):
    m, dm = dp(t["tintas"]["papel"])
    dibujar_fn(m, dm)
    a = np.asarray(m)
    items = [(y, x, tuple(int(v) for v in a[y, x])) for y in range(H_DP) for x in range(W_DP)]
    capa = pintar_dos_tintas(None, t, ESC, celdas_iter=items, ancho=W_DP, alto=H_DP, semilla=2)
    base = papel(Image.new("RGB", capa.size, t["tintas"]["papel"]), 0.05)
    base.paste(capa, (0, 0), capa)
    return base


def fondo_distopia(t):
    rojo, negro, gris = t["tintas"]["rojo"], t["tintas"]["negro"], hx("A7A296")
    def dib(m, d):
        d.polygon([(-10, 80), (60, 80), (180, -2), (118, -2)], fill=rojo)
        cx, cy = 334, 32
        for k in range(14):
            an = math.pi * k / 13
            d.line([cx + 30 * math.cos(an), cy - 16 * math.sin(an), cx + 44 * math.cos(an), cy - 26 * math.sin(an)], fill=gris)
        d.ellipse([cx - 26, cy - 13, cx + 26, cy + 13], fill=hx("ECE9E1"), outline=negro, width=2)
        d.ellipse([cx - 9, cy - 9, cx + 9, cy + 9], fill=rojo); d.ellipse([cx - 4, cy - 4, cx + 4, cy + 4], fill=negro)
        f = ImageFont.truetype(F + "DejaVuSans-Bold.ttf", 9)
        d.text((196, 4), "EXPEDIENTE N.º 0412", font=f, fill=negro)
        for k in range(4):
            d.line([196, 18 + k * 5, 196 + (80 if k < 3 else 46), 18 + k * 5], fill=gris, width=2)
        for x in range(246, 286, 3):
            d.line([x, 54, x, 64], fill=negro if (x * 7) % 5 else gris)
        for (x, y) in ((6, 6), (404, 6)):
            d.ellipse([x - 3, y - 3, x + 3, y + 3], outline=negro); d.line([x - 5, y, x + 5, y], fill=negro); d.line([x, y - 5, x, y + 5], fill=negro)
        d.line([0, 74, W_DP, 74], fill=negro, width=1)
        d.rectangle([0, 75, W_DP, 80], fill=gris)
    return dos_tintas_fondo(t, dib)


def fondo_urss(t):
    rojo, negro, gris = t["tintas"]["rojo"], t["tintas"]["negro"], hx("C9B994")
    def dib(m, d):
        d.ellipse([282, 4, 342, 64], fill=rojo)
        d.polygon([(0, 8), (150, 34), (0, 60)], fill=rojo)
        d.polygon([(190, 80), (204, 80), (290, -2), (276, -2)], fill=negro)
        d.polygon([(232, 80), (238, 80), (330, -2), (324, -2)], fill=negro)
        d.rectangle([360, 10, 404, 18], fill=negro); d.rectangle([370, 24, 404, 28], fill=gris); d.rectangle([150, 52, 186, 58], fill=gris)
        for k in range(6):
            d.line([60 + k * 14, 66, 74 + k * 14, 54], fill=negro)
        d.line([0, 74, W_DP, 74], fill=negro); d.rectangle([0, 75, W_DP, 80], fill=rojo)
    return dos_tintas_fondo(t, dib)


def greca_negra(d, y0, color, fondo, desfase=0):
    pat = ["XXXXXXX.", "X.....X.", "X.XXX.X.", "X.X...X.", "X.XXXXXX"]
    for yy, fila in enumerate(pat):
        for x in range(W_DP):
            d.point((x, y0 + yy), fill=color if fila[(x + desfase) % 8] == "X" else fondo)


def fondo_heleno(t):
    barro, neg = t["terracota"], (30, 20, 16)
    m, d = dp(barro)
    greca_negra(d, 1, neg, barro)
    d.line([0, 7, W_DP, 7], fill=neg); d.line([0, 74, W_DP, 74], fill=neg)
    for x in range(0, W_DP, 6):
        d.polygon([(x, 80), (x + 3, 75), (x + 6, 80)], fill=neg)
    for x0 in range(52, W_DP, 104):       # rosetas de puntos, como en la decoración de los vasos
        for k in range(8):
            an = 2 * math.pi * k / 8
            x, y = x0 + 6 * math.cos(an), 38 + 6 * math.sin(an)
            d.ellipse([x - 1.5, y - 1.5, x + 1.5, y + 1.5], fill=neg)
        d.ellipse([x0 - 2, 36, x0 + 2, 40], fill=neg)
    big = subir(m)
    a = np.asarray(big, np.float32)
    yy = np.mgrid[0:big.height, 0:big.width][0] / big.height
    brillo = 1 + 0.08 * np.exp(-((yy - 0.35) ** 2) / 0.02)   # reflejo del barniz del vaso
    return papel(Image.fromarray(np.clip(a * brillo[..., None], 0, 255).astype(np.uint8)), 0.03)


def fondo_refugio(t):
    pared, tinta, teal, nar, oro = mezcla(hx("F1E8D0"), hx("1C6E6A"), 0.12), hx("1F2A2A"), hx("1C6E6A"), hx("D9572B"), hx("B98A15")
    m, d = dp(pared)
    for x0 in range(0, W_DP, 46):
        d.rectangle([x0 + 1, 9, x0 + 44, 70], fill=mezcla(pared, (255, 255, 255), 0.18 if (x0 // 46) % 2 else 0.08), outline=mezcla(pared, tinta, 0.3))
        for (x, y) in ((x0 + 3, 11), (x0 + 42, 11), (x0 + 3, 68), (x0 + 42, 68)):
            d.point((x, y), fill=mezcla(pared, tinta, 0.5))
    d.rectangle([0, 3, W_DP, 6], fill=teal)
    for x in range(10, W_DP, 40):
        d.rectangle([x, 2, x + 3, 7], fill=mezcla(teal, tinta, 0.4))
    for x0 in (70, 210):                   # estrellas atómicas
        for k in range(8):
            an = math.pi * k / 4
            d.line([x0, 38, x0 + 9 * math.cos(an), 38 + 9 * math.sin(an)], fill=nar)
        d.ellipse([x0 - 2, 36, x0 + 2, 40], fill=oro)
    cx, cy = 392, 40                        # puerta redonda del refugio
    for k in range(18):
        an = 2 * math.pi * k / 18
        d.polygon([(cx + 27 * math.cos(an - 0.08), cy + 27 * math.sin(an - 0.08)), (cx + 31 * math.cos(an), cy + 31 * math.sin(an)),
                   (cx + 27 * math.cos(an + 0.08), cy + 27 * math.sin(an + 0.08))], fill=mezcla(pared, tinta, 0.45))
    d.ellipse([cx - 27, cy - 27, cx + 27, cy + 27], fill=hx("C9C3B0"), outline=tinta)
    d.ellipse([cx - 20, cy - 20, cx + 20, cy + 20], outline=mezcla(hx("C9C3B0"), tinta, 0.4))
    d.ellipse([cx - 6, cy - 6, cx + 6, cy + 6], fill=oro, outline=tinta)
    d.line([0, 74, W_DP, 74], fill=tinta)
    for y in range(75, 80):
        for x in range(W_DP):
            d.point((x, y), fill=oro if ((x + y) // 3) % 2 else tinta)
    return subir(m)


def fondo_orden(t):
    acero, tinta, carm, laton = hx("E2E5E8"), hx("15181B"), hx("9E1B22"), hx("7E6219")
    m, d = dp(acero)
    for x0 in range(0, W_DP, 30):
        d.rectangle([x0, 0, x0 + 29, 73], fill=hx("D3D7DC") if (x0 // 30) % 2 else acero, outline=hx("AEB4BB"))
        for y in (3, 36, 70):
            d.point((x0 + 2, y), fill=hx("4B525A")); d.point((x0 + 27, y), fill=hx("4B525A"))
    for x0 in range(40, W_DP, 120):         # estandartes carmesí con engrane de latón
        d.rectangle([x0 - 10, 0, x0 + 10, 40], fill=carm)
        d.polygon([(x0 - 10, 40), (x0, 34), (x0 + 10, 40), (x0 + 10, 46), (x0, 40), (x0 - 10, 46)], fill=carm)
        d.line([x0 - 10, 2, x0 + 10, 2], fill=laton)
        for k in range(8):
            an = 2 * math.pi * k / 8
            d.rectangle([x0 + 6 * math.cos(an) - 1, 18 + 6 * math.sin(an) - 1, x0 + 6 * math.cos(an) + 1, 18 + 6 * math.sin(an) + 1], fill=laton)
        d.ellipse([x0 - 5, 13, x0 + 5, 23], fill=laton); d.ellipse([x0 - 2, 16, x0 + 2, 20], fill=carm)
    d.text((132, 52), "ORDO · SCIENTIA · CVSTODIA", font=ImageFont.truetype(F + "DejaVuSansMono-Bold.ttf", 8), fill=mezcla(acero, tinta, 0.75))
    d.line([0, 74, W_DP, 74], fill=tinta)
    for y in range(75, 80):
        for x in range(W_DP):
            d.point((x, y), fill=hx("4B525A") if (x % 3 == 0 or y % 2 == 0) else hx("2A2F35"))
    return subir(m)


def fondo_laboratorio(t):
    blanco, tinta, cian, viol, gris = hx("F4F7F9"), hx("12202B"), hx("0B8FA1"), hx("5E4FD1"), hx("CBD6DF")
    m, d = dp(blanco)
    for x in range(0, W_DP, 6): d.line([x, 0, x, 73], fill=mezcla(blanco, cian, 0.12))
    for y in range(0, 74, 6): d.line([0, y, W_DP, y], fill=mezcla(blanco, cian, 0.12))
    def matraz(x, liq):
        d.polygon([(x - 2, 52), (x + 2, 52), (x + 2, 60), (x + 8, 73), (x - 8, 73), (x - 2, 60)], outline=viol)
        d.polygon([(x - 5, 67), (x + 5, 67), (x + 7, 72), (x - 7, 72)], fill=liq)
    matraz(40, cian); matraz(300, viol)
    for k in range(4):                      # gradilla con tubos
        x = 100 + k * 5
        d.rectangle([x, 56, x + 2, 72], outline=gris); d.rectangle([x + 1, 64 - k * 2, x + 1, 71], fill=(cian, viol, hx("1E8E5A"), cian)[k])
    d.rectangle([96, 66, 122, 68], fill=gris)
    d.rectangle([226, 66, 240, 72], fill=hx("4D5D6B")); d.line([233, 66, 228, 50], fill=hx("4D5D6B"), width=3)
    d.rectangle([224, 46, 234, 50], fill=hx("4D5D6B")); d.rectangle([236, 58, 246, 60], fill=hx("4D5D6B"))
    d.rectangle([356, 58, 370, 72], outline=cian); d.rectangle([357, 65, 369, 71], fill=mezcla(cian, blanco, 0.5))
    d.line([0, 74, W_DP, 74], fill=hx("4D5D6B"))
    for x in range(0, W_DP, 4):
        d.line([x, 74, x, 75 if x % 20 else 77], fill=hx("4D5D6B"))
    d.rectangle([0, 76, W_DP, 80], fill=hx("FFFFFF"))
    for x in range(0, W_DP, 40): d.line([x, 76, x, 80], fill=gris)
    return subir(m)


def fondo_colonos(t):
    gaceta, tinta, marino, ladrillo, madera = hx("F2EBDC"), hx("1F2A3A"), hx("1F3A5F"), hx("A23B2A"), hx("7A5A2E")
    m, d = dp(gaceta)
    pts = [(x, 4 + 3 * math.sin(math.pi * (x % 103) / 103)) for x in range(W_DP)]
    d.line(pts, fill=tinta)
    for k, x in enumerate(range(4, W_DP, 10)):
        y = 4 + 3 * math.sin(math.pi * (x % 103) / 103)
        d.polygon([(x - 3, y), (x + 3, y), (x, y + 6)], fill=(marino, ladrillo, hx("FFFFFF"))[k % 3])
    rnd = random.Random(5); x = 0
    while x < W_DP:
        w = rnd.randint(30, 48); h = rnd.randint(18, 28); top = 74 - h
        muro = rnd.choice([ladrillo, madera, hx("E7DDC8")])
        d.rectangle([x + 1, top, x + w - 1, 73], fill=muro, outline=tinta)
        d.polygon([(x - 1, top), (x + w // 2, top - 10), (x + w + 1, top)], fill=marino)
        for wx in range(x + 5, x + w - 6, 10):
            d.rectangle([wx, top + 5, wx + 4, top + 10], fill=hx("F2D27A") if rnd.random() < 0.5 else marino, outline=tinta)
        d.rectangle([x + w // 2 - 3, 66, x + w // 2 + 3, 73], fill=mezcla(madera, tinta, 0.3))
        x += w + rnd.randint(4, 12)
    d.line([0, 74, W_DP, 74], fill=tinta)
    for y in range(75, 80):
        for x in range(W_DP):
            d.point((x, y), fill=mezcla(madera, tinta, 0.35) if (x + (y // 2) * 4) % 9 == 0 else madera)
    return subir(m)


def fondo_napoleon(t):
    papel_, azul, oro, carm, tinta = hx("F4EEDF"), hx("1E3A8A"), hx("B8892B"), hx("A3172E"), hx("14213D")
    m, d = dp(papel_)
    d.rectangle([0, 0, W_DP, 9], fill=azul)
    for x in range(4, W_DP, 14):            # abejas imperiales
        d.point((x, 4), fill=oro); d.point((x, 5), fill=oro); d.point((x, 6), fill=mezcla(oro, tinta, 0.3))
        d.point((x - 1, 3), fill=mezcla(oro, papel_, 0.5)); d.point((x + 1, 3), fill=mezcla(oro, papel_, 0.5))
    d.line([0, 10, W_DP, 10], fill=oro)
    for x0 in range(20, W_DP, 70):          # guirnaldas de laurel dorado
        pts = [(x0 + k, 12 + 9 * math.sin(math.pi * k / 60)) for k in range(61)]
        d.line(pts, fill=oro)
        for k in range(4, 60, 6):
            x, y = pts[k]; d.point((x, y - 1), fill=hx("6E7A3A")); d.point((x + 1, y + 1), fill=hx("6E7A3A"))
        d.ellipse([x0 - 2, 10, x0 + 2, 14], fill=oro)
    for xx in (0, W_DP - 14):
        d.rectangle([xx, 11, xx + 13, 73], fill=carm)
        for k in range(3): d.line([xx + 3 + k * 4, 11, xx + 3 + k * 4, 73], fill=mezcla(carm, tinta, 0.35))
    d.line([0, 74, W_DP, 74], fill=tinta)
    a, b = mezcla(oro, hx("7A5A2E"), 0.6), mezcla(oro, hx("7A5A2E"), 0.3)
    for y in range(75, 80):
        for x in range(W_DP):
            d.point((x, y), fill=a if ((x + y) // 3 + (x - y) // 3) % 2 else b)
    return subir(m)


def fondo_navidad(t):
    m, d = dp(hx("0E1A14"))
    for y in range(0, 60):
        d.line([0, y, W_DP, y], fill=mezcla(hx("050B08"), hx("0E1A14"), y / 60))
    rnd = random.Random(9)
    for _ in range(30): d.point((rnd.randrange(W_DP), rnd.randrange(2, 30)), fill=mezcla(hx("F3EFE6"), hx("0E1A14"), rnd.uniform(0.3, 0.7)))
    def pino(x, h, col, nieve):
        for k in range(4):
            y0 = 72 - h + k * h // 4; w = 3 + k * 3
            d.polygon([(x, y0), (x - w, y0 + h // 3), (x + w, y0 + h // 3)], fill=col)
            if nieve: d.line([x - w + 1, y0 + h // 3 - 1, x - w + 3, y0 + h // 3 - 2], fill=hx("F3EFE6")); d.point((x, y0), fill=hx("F3EFE6"))
    for x in range(6, W_DP, 17): pino(x + rnd.randint(-4, 4), rnd.randint(22, 34), hx("1C3125"), False)
    for x in range(30, W_DP, 70): pino(x + rnd.randint(-8, 8), rnd.randint(34, 44), hx("2E4A39"), True)
    for x in range(W_DP):
        top = 71 + round(1.2 * math.sin(x / 9))
        d.line([x, top, x, 80], fill=hx("F3EFE6")); d.point((x, 79), fill=mezcla(hx("F3EFE6"), hx("2E4A39"), 0.25))
    luces = []
    for k, x in enumerate(range(5, W_DP, 9)):
        y = 4 + 3 * math.sin(math.pi * (x % 61) / 61)
        d.point((x, y - 1), fill=hx("1C3125")); luces.append((x, round(y), (hx("E5484D"), hx("66BB6A"), hx("F2C94C"))[k % 3]))
    d.line([(x, 3 + 3 * math.sin(math.pi * (x % 61) / 61)) for x in range(W_DP)], fill=hx("1C3125"))
    EXTRA["navidad"] = dict(luces=luces, copos=[(rnd.uniform(0, W_DP), rnd.uniform(0, H_DP), rnd.uniform(0.15, 0.45), rnd.uniform(-0.15, 0.15), rnd.random() < 0.25) for _ in range(46)])
    return subir(m)


def fondo_seda(t):
    m, d = dp(hx("F5EEDC"))
    for y in range(0, 60):
        d.line([0, y, W_DP, y], fill=mezcla(hx("F5EEDC"), hx("D4C29A"), y / 90))
    d.ellipse([352, 10, 370, 28], fill=mezcla(hx("E0B04F"), hx("C0392B"), 0.25))
    lej, cer = mezcla(hx("D4C29A"), hx("C0392B"), 0.12), hx("D4C29A")
    for x in range(W_DP):
        y = round(46 + 5 * math.sin(x / 40)); d.line([x, y, x, 80], fill=lej)
    for k in range(4):                      # caravana en la cresta
        x = 118 + k * 15; y = round(46 + 5 * math.sin(x / 40)) - 1
        c = hx("59546F")
        d.rectangle([x, y - 5, x + 7, y - 3], fill=c); d.point((x + 3, y - 6), fill=c); d.line([x + 8, y - 6, x + 9, y - 8], fill=c)
        for lx in (x + 1, x + 6): d.line([lx, y - 3, lx, y], fill=c)
    for x in range(W_DP):
        y = round(62 + 5 * math.sin(x / 55 + 1)); d.line([x, y, x, 80], fill=cer)
    d.line([0, 74, W_DP, 74], fill=hx("1F1B3A"))
    d.rectangle([0, 75, W_DP, 80], fill=hx("23408E"))
    for x in range(5, W_DP, 9):              # monedas con agujero cuadrado
        d.ellipse([x - 2, 75, x + 2, 79], fill=hx("E0B04F")); d.point((x, 77), fill=hx("23408E"))
    return subir(m)


def fondo_espacio(t):
    W, H = W_DP * ESC, H_DP * ESC
    base = np.zeros((H, W, 3), np.float32) + np.array(hx("03050C"), np.float32)
    rng = np.random.default_rng(12)
    for col, cx, cy, sx, sy in ((hx("C59BFF"), 120, 25, 90, 30), (hx("8AB4FF"), 230, 40, 110, 26), (hx("C59BFF"), 330, 15, 60, 20)):
        yy, xx = np.mgrid[0:H, 0:W] / ESC
        g = np.exp(-(((xx - cx) / sx) ** 2 + ((yy - cy) / sy) ** 2))
        ruido = np.asarray(Image.fromarray(((rng.random((H // 30 + 2, W // 30 + 2)) * 255)).astype(np.uint8)).resize((W, H), Image.BICUBIC), np.float32) / 255
        base += (g * (0.25 + 0.35 * ruido))[..., None] * np.array(col, np.float32) * 0.5
    img = Image.fromarray(np.clip(base, 0, 255).astype(np.uint8))
    m = Image.new("RGBA", (W_DP, H_DP), (0, 0, 0, 0)); d = ImageDraw.Draw(m)
    rnd = random.Random(21); estrellas = []
    for _ in range(90):
        x, y = rnd.randrange(W_DP), rnd.randrange(0, 66); estrellas.append((x, y))
        d.point((x, y), fill=mezcla(hx("E8ECF8"), (0, 0, 0), rnd.uniform(0, 0.6)) + (255,))
    d.ellipse([331, 12, 359, 40], fill=hx("FFB86B") + (255,))
    for k in range(4): d.line([333, 18 + k * 6, 357, 18 + k * 6], fill=mezcla(hx("FFB86B"), hx("8A4A1A"), 0.3) + (255,))
    d.arc([318, 21, 372, 31], 160, 380, fill=hx("8E9AB8") + (255,))
    for x in range(W_DP):
        top = 70 + round(1.0 * math.sin(x / 13))
        d.line([x, top, x, 80], fill=hx("3A4256") + (255,)); d.point((x, top), fill=hx("5A6480") + (255,))
    for cx in (40, 150, 260, 380):
        d.ellipse([cx - 6, 74, cx + 6, 78], fill=hx("26324F") + (255,)); d.arc([cx - 6, 74, cx + 6, 78], 180, 360, fill=hx("5A6480") + (255,))
    big = m.resize((W, H), Image.NEAREST); img.paste(big, (0, 0), big)
    EXTRA["espacio"] = dict(estrellas=rnd.sample(estrellas, 25))
    return img


def fondo_andalus(t):
    yeso, tinta, verde, azul, almagre, oro = hx("F3EBDA"), hx("2A1A12"), hx("1E6B5C"), hx("2A4F9E"), hx("B5432A"), hx("E0B65A")
    m, d = dp(yeso)
    for y in range(0, 40):                  # yesería: red de rombos (sebka)
        for x in range(W_DP):
            if (x + y) % 12 == 0 or (x - y) % 12 == 0: d.point((x, y), fill=mezcla(yeso, hx("6A5640"), 0.25))
    d.rectangle([0, 40, W_DP, 42], fill=oro)
    for x in range(2, W_DP, 6): d.point((x, 41), fill=tinta)
    for y in range(43, 74):                 # alicatado: estrellas de ocho puntas
        for x in range(W_DP):
            u, v = x % 12 - 5.5, (y - 43) % 12 - 5.5
            tile = (x // 12 + (y - 43) // 12) % 2
            dentro_ = abs(u) + abs(v) <= 6.2 or (abs(u) <= 4.2 and abs(v) <= 4.2)
            borde_ = dentro_ and not (abs(u) + abs(v) <= 5.0 or (abs(u) <= 3.2 and abs(v) <= 3.2))
            c = hx("FFFFFF") if borde_ else ((verde if tile else azul) if dentro_ else (almagre if (x + y) % 2 else oro))
            d.point((x, y), fill=c)
    d.line([0, 74, W_DP, 74], fill=tinta)
    for y in range(75, 80):
        for x in range(W_DP):
            d.point((x, y), fill=hx("FBF6EA") if x % 6 == 0 or y == 77 else mezcla(almagre, yeso, 0.35))
    return subir(m)


def fondo_mongol(t):
    azul, blanco, rojo, oro, tinta = hx("1F5FA8"), hx("F1EDE3"), hx("C2412D"), hx("C48A1E"), hx("18202E")
    m, d = dp(azul)
    for y in range(0, 56): d.line([0, y, W_DP, y], fill=mezcla(azul, blanco, y / 80))
    for cx, cy in ((60, 12), (190, 8), (300, 16)):
        for dx, r in ((-6, 4), (0, 6), (7, 4)): d.ellipse([cx + dx - r, cy - r // 2, cx + dx + r, cy + r // 2 + 1], fill=hx("FFFFFF"))
    lej, cer = mezcla(azul, oro, 0.5), hx("8C9A50")
    for x in range(W_DP):
        y = round(48 + 4 * math.sin(x / 37)); d.line([x, y, x, 80], fill=lej)
    for x in (96, 112):                     # caballos a lo lejos
        y = round(48 + 4 * math.sin(x / 37)) - 1
        d.rectangle([x, y - 4, x + 5, y - 2], fill=tinta); d.line([x + 6, y - 5, x + 7, y - 6], fill=tinta)
        for lx in (x, x + 5): d.line([lx, y - 2, lx, y], fill=tinta)
    for x in range(W_DP):
        y = round(60 + 3 * math.sin(x / 50 + 2)); d.line([x, y, x, 80], fill=cer)
    cx = 306                                 # ger
    d.rectangle([cx - 18, 50, cx + 18, 66], fill=blanco, outline=tinta)
    d.pieslice([cx - 20, 38, cx + 20, 62], 180, 360, fill=blanco, outline=tinta)
    d.line([cx - 18, 54, cx + 18, 54], fill=rojo); d.line([cx - 18, 56, cx + 18, 56], fill=oro)
    d.rectangle([cx - 4, 57, cx + 4, 66], fill=rojo, outline=oro); d.ellipse([cx - 3, 37, cx + 3, 41], fill=oro)
    d.line([0, 74, W_DP, 74], fill=mezcla(cer, tinta, 0.4))
    for x in range(0, W_DP, 5): d.line([x, 73, x + 1, 71], fill=mezcla(cer, tinta, 0.3))
    d.rectangle([0, 75, W_DP, 80], fill=mezcla(cer, tinta, 0.2))
    return subir(m)


def fondo_renacimiento(t):
    W, H, S = W_DP * ESC, H_DP * ESC, ESC
    img = papel(Image.new("RGB", (W, H), hx("F0E4C6")), 0.05)
    d = ImageDraw.Draw(img)
    sep = mezcla(hx("F0E4C6"), hx("3A2614"), 0.65); san = mezcla(hx("F0E4C6"), hx("8C3B1E"), 0.6); leve = mezcla(hx("F0E4C6"), hx("3A2614"), 0.3)
    cx, cy = 318 * S, 38 * S
    d.ellipse([cx - 31 * S, cy - 31 * S, cx + 31 * S, cy + 31 * S], outline=sep, width=1)
    d.rectangle([cx - 26 * S, cy - 26 * S, cx + 26 * S, cy + 26 * S], outline=sep, width=1)
    for a_, b_ in (((-26, -26), (26, 26)), ((26, -26), (-26, 26)), ((-31, 0), (31, 0)), ((0, -31), (0, 31))):
        d.line([cx + a_[0] * S, cy + a_[1] * S, cx + b_[0] * S, cy + b_[1] * S], fill=leve, width=1)
    d.arc([cx - 40 * S, cy - 40 * S, cx + 40 * S, cy + 40 * S], 200, 250, fill=san, width=1)
    rnd = random.Random(14)
    for yb in (9, 16, 23):                  # escritura en espejo (inclinada hacia atrás)
        x = 200 * S
        while x > 22 * S:
            n = rnd.randint(2, 6)
            for _ in range(n):
                d.line([x, yb * S, x + 1.4 * S, (yb - 3.2) * S], fill=sep, width=1)
                d.arc([x - 1.2 * S, (yb - 1.2) * S, x + 0.4 * S, yb * S + 1], 0, 180, fill=sep)
                x -= 1.9 * S
            x -= 2.6 * S
    gx, gy = 228 * S, 40 * S                 # engrane bosquejado
    d.ellipse([gx - 9 * S, gy - 9 * S, gx + 9 * S, gy + 9 * S], outline=sep)
    for k in range(12):
        an = 2 * math.pi * k / 12
        d.line([gx + 9 * S * math.cos(an), gy + 9 * S * math.sin(an), gx + 12 * S * math.cos(an), gy + 12 * S * math.sin(an)], fill=sep, width=2)
    for k in range(6): d.line([60 * S + k * 3 * S, 60 * S, 60 * S + k * 3 * S + 20 * S, 40 * S], fill=san)
    d.line([0, 74 * S, W, 74 * S], fill=sep, width=1)
    for x in range(0, W_DP, 4): d.line([x * S, 74 * S, x * S, (75.4 if x % 20 else 77) * S], fill=sep)
    return img


def fondo_revolucion(t):
    W, H, S = W_DP * ESC, H_DP * ESC, ESC
    m, d = dp(hx("F2ECDD"))
    for k, c in enumerate((hx("1F3A6E"), hx("FAF6EC"), hx("B22234"))):
        d.rectangle([k * 10, 0, k * 10 + 9, 73], fill=c)
    tronco, verde = hx("6A4A2A"), hx("3F6B3A")
    d.rectangle([368, 40, 372, 73], fill=tronco)
    for cx, cy, r in ((370, 30, 14), (358, 36, 9), (382, 36, 9), (370, 20, 9)):
        d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=verde)
    for k, c in enumerate((hx("1F3A6E"), hx("FFFFFF"), hx("B22234"))):
        d.line([363 + k * 3, 42, 361 + k * 3, 52], fill=c)
    d.line([0, 74, W_DP, 74], fill=hx("1C1A17"))
    rnd = random.Random(3)
    for y0 in (75, 78):
        x = -(y0 % 2) * 3
        while x < W_DP:
            w = rnd.randint(4, 6); d.rectangle([x, y0, x + w - 1, y0 + 2], fill=mezcla(hx("8A857A"), hx("57524A"), rnd.random())); x += w + 1
    big = papel(subir(m), 0.05)
    dd = ImageDraw.Draw(big)
    dd.text((40 * S, 6 * S), "LIBERTÉ · ÉGALITÉ · FRATERNITÉ", font=ImageFont.truetype(F + "DejaVuSerif-Bold.ttf", 9 * S), fill=hx("1C1A17"))
    dd.line([40 * S, 20 * S, 330 * S, 20 * S], fill=hx("B22234"), width=2)
    return big


def fondo_antiguas(t):
    W, H, S = W_DP * ESC, H_DP * ESC, ESC
    m, d = dp(hx("E9E3D4"))
    tinta, verde, rubi, oro = hx("57534A"), hx("2E5A3E"), hx("8E1B24"), hx("9A7B2E")
    rnd = random.Random(6)
    x = 6                                    # runas inventadas
    while x < W_DP - 6:
        for _ in range(rnd.randint(2, 4)):
            a_, b_ = (x + rnd.randint(0, 3), 2 + rnd.randint(0, 5)), (x + rnd.randint(0, 3), 2 + rnd.randint(0, 5))
            d.line([a_, b_], fill=tinta)
        x += 7
    d.line([0, 9, W_DP, 9], fill=tinta)
    for x0 in range(150, 290, 16):          # montañas
        h = rnd.randint(12, 20)
        d.polygon([(x0, 46), (x0 + 9, 46 - h), (x0 + 18, 46)], fill=hx("E9E3D4"), outline=tinta)
        for k in range(2, h - 2, 3): d.line([x0 + 9 + k * 0.4, 46 - h + k, x0 + 9 + k * 0.4 + 3, 46 - h + k + 2], fill=tinta)
    for k in range(14):                     # bosque
        x0, y0 = 20 + rnd.randint(0, 100), 30 + rnd.randint(0, 22)
        d.line([x0, y0 + 3, x0, y0 + 6], fill=tinta); d.ellipse([x0 - 3, y0 - 3, x0 + 3, y0 + 3], fill=verde, outline=tinta)
    d.line([(x, 60 + 4 * math.sin(x / 18)) for x in range(300, W_DP)], fill=hx("5A7A9A"), width=2)
    cx, cy = 384, 26                         # rosa de los vientos
    for k in range(8):
        an = math.pi * k / 4; r = 10 if k % 2 == 0 else 6
        d.line([cx, cy, cx + r * math.cos(an), cy + r * math.sin(an)], fill=rubi if k % 2 == 0 else oro)
    d.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], fill=oro)
    for x in range(0, W_DP, 4): d.line([x, 74, x + 1, 74], fill=rubi)
    big = papel(subir(m), 0.06)
    return big


FONDOS.update(botanico=fondo_botanico, distopia=fondo_distopia, urss=fondo_urss, heleno=fondo_heleno, refugio=fondo_refugio, orden=fondo_orden,
              laboratorio=fondo_laboratorio, colonos=fondo_colonos, napoleon=fondo_napoleon, navidad=fondo_navidad, seda=fondo_seda,
              espacio=fondo_espacio, andalus=fondo_andalus, mongol=fondo_mongol, renacimiento=fondo_renacimiento, revolucion=fondo_revolucion,
              antiguas=fondo_antiguas)

BANDAS = [("romano", False, "Romano · un mosaico que cobra vida"), ("gotico", False, "Gótico · frente a un vitral"),
          ("medieval", False, "Medieval · al margen de un libro del scriptorium"), ("ciber", True, "Ciber · dentro de una pantalla"),
          ("botanico", False, "Botánico · bordado en punto de cruz"), ("distopia", False, "Distopía · impreso a dos tintas en un cartel"),
          ("heleno", False, "Helénico · figura negra en un vaso"), ("refugio", False, "Refugio · en el pasillo del refugio"),
          ("orden", False, "Orden · en la sala de la Orden"), ("laboratorio", False, "Laboratorio · sobre la mesa del laboratorio"),
          ("colonos", False, "Colonos · por la calle del pueblo"), ("napoleon", False, "Napoleónico · en el salón imperial"),
          ("navidad", True, "Navidad · en el bosque nevado"), ("seda", False, "Ruta de la Seda · con la caravana"),
          ("urss", False, "Soviético · cartel constructivista"), ("espacio", True, "Espacio · en la Luna"),
          ("andalus", False, "Al-Ándalus · frente al alicatado"), ("mongol", False, "Mongol · en la estepa"),
          ("renacimiento", False, "Renacimiento · en el cuaderno de Leonardo"), ("revolucion", False, "Revolución · en el cartel de la plaza"),
          ("antiguas", False, "Tierras Antiguas · sobre el mapa")]
AJUSTAR = {"romano", "ciber", "botanico", "andalus", "distopia", "urss"}
SIN_SOMBRA = {"medieval", "heleno", "renacimiento", "ciber"}


def sprite(nombre, oscuro, t, anim, i, espejo):
    k = (nombre, oscuro, anim, i, espejo)
    if k not in _spr:
        g = rejilla(anim, i, t, espejo)
        _spr[k] = pintar_plano(g, t, ESC) if t["material_banda"] == "plano" else pintar(g, t, ESC, oscuro)
    return _spr[k]


def cuadro_banda(nombre, oscuro, t, fondo, c, tick, extra):
    anim, i, x, dirn, dy = c
    if nombre in AJUSTAR:
        x, dy = round(x), round(dy)
    img = fondo.copy()
    cx = (x + (31 if dirn > 0 else 33)) * ESC
    k = 1 + dy / 14
    caja = [cx - 19 * ESC * k, (Y_SUELO + 0.4) * ESC - 1.3 * ESC * k, cx + 19 * ESC * k, (Y_SUELO + 0.4) * ESC + 1.3 * ESC * k]
    if nombre == "navidad":                  # foquitos que titilan
        d = ImageDraw.Draw(img)
        for j, (lx, ly, col) in enumerate(EXTRA["navidad"]["luces"]):
            enc = (j + tick // 4) % 3 != 0
            d.rectangle([lx * ESC, ly * ESC, lx * ESC + ESC - 1, ly * ESC + ESC - 1], fill=col if enc else mezcla(col, (0, 0, 0), 0.6))
    if nombre == "espacio":
        d = ImageDraw.Draw(img)
        for j, (sx, sy) in enumerate(EXTRA["espacio"]["estrellas"]):
            if (j * 7 + tick // 3) % 5 == 0:
                d.rectangle([sx * ESC - 1, sy * ESC - 1, sx * ESC + ESC, sy * ESC + ESC], fill=hx("FFFFFF"))
    if nombre not in SIN_SOMBRA:
        capa = Image.new("RGBA", img.size, (0, 0, 0, 0)); ImageDraw.Draw(capa).ellipse(caja, fill=(20, 20, 24, 55 if nombre != "navidad" else 40))
        img = Image.alpha_composite(img.convert("RGBA"), capa).convert("RGB")
    spr = sprite(nombre, oscuro, t, anim, i, dirn < 0)
    px, py = int(round(x * ESC)), int(round((Y_SUELO - 60 + dy) * ESC))
    if nombre == "ciber":
        capa = Image.new("RGB", img.size, (0, 0, 0)); capa.paste(spr, (px, py))
        ImageDraw.Draw(capa).ellipse(caja, fill=mezcla(hx("00E5FF"), (0, 0, 0), 0.82))
        img = ImageChops.add(img, capa)
        if (tick // 6) % 2 == 0:
            ImageDraw.Draw(img).rectangle([10 + 21 * 8 + 4, 9, 10 + 21 * 8 + 11, 21], fill=hx("FF2E88"))
        img = ImageChops.multiply(lineas_escaneo(img), extra)
    else:
        img.paste(spr, (px, py), spr)
    if nombre == "navidad":                  # nieve que cae delante del gato
        d = ImageDraw.Draw(img)
        for (x0, y0, vy, vx, grande) in EXTRA["navidad"]["copos"]:
            yy = (y0 + tick * vy * 3) % H_DP; xx = (x0 + tick * vx * 3 + 2 * math.sin((tick + x0) / 10)) % W_DP
            r = ESC if grande else ESC - 1
            d.rectangle([xx * ESC, yy * ESC, xx * ESC + r - 1, yy * ESC + r - 1], fill=hx("F3EFE6"))
    return img


def resumen_webp(cuadros, ruta, columnas=3):
    """Todas las bandas a tamaño real en una sola animación (WebP: colores reales, sin el límite de 256 del GIF)."""
    temas_ = [(n, o, et, tema(n, o)) for n, o, et in BANDAS]
    fondos = {n: FONDOS[n](t) for n, o, et, t in temas_}
    vin = viñeta_ciber()
    filas = math.ceil(len(temas_) / columnas)
    cw, ch_ = W_DP + 12, H_DP + 18
    out = []
    for k, c in enumerate(cuadros):
        lam = Image.new("RGB", (columnas * cw + 12, filas * ch_ + 10), (250, 249, 246)); dl = ImageDraw.Draw(lam)
        for j, (n, o, et, t) in enumerate(temas_):
            b = cuadro_banda(n, o, t, fondos[n], c, k, vin if n == "ciber" else None).resize((W_DP, H_DP), Image.LANCZOS)
            x, y = 10 + (j % columnas) * cw, 6 + (j // columnas) * ch_
            dl.text((x, y), et.split(" · ")[0], font=fuente("DejaVuSans.ttf", 11), fill=(40, 40, 46))
            lam.paste(b, (x, y + 14))
        out.append(lam)
    out[0].save(ruta, save_all=True, append_images=out[1:], duration=int(DT * 1000), loop=0, quality=80, method=4)


def comparar_temas():
    poses = [("caminar", 2), ("girar", 0), ("girar", 1), ("jugar", 0), ("enojado", 0), ("quieto_triste", 0), ("dormir", 0)]
    cel = 3; lado = N * cel
    W = 200 + len(poses) * (lado + 6)
    H = 40 + len(BANDAS) * (lado + 6)
    img = Image.new("RGB", (W, H), (250, 249, 246))
    d = ImageDraw.Draw(img)
    d.text((10, 10), "Los 21 temas: mismo gato y mismas animaciones; cada tema decide pelaje, accesorio, sombrero y material (×3).",
           font=fuente("DejaVuSans-Bold.ttf", 15), fill=(25, 25, 28))
    for i, (n, o, et) in enumerate(BANDAS):
        t = tema(n, o); y = 40 + i * (lado + 6)
        d.rectangle([0, y - 3, W, y + lado + 3], fill=t["fondo"])
        d.text((10, y + lado // 2 - 18), et.split(" · ")[0], font=fuente("DejaVuSans-Bold.ttf", 14), fill=t["texto"])
        d.text((10, y + lado // 2 + 2), et.split(" · ")[1], font=fuente("DejaVuSans.ttf", 10), fill=t["texto"])
        for j, (a, k) in enumerate(poses):
            x = 200 + j * (lado + 6)
            spr = pintar(rejilla(a, k, t), t, cel, o)
            if t["material"] == "ciber":
                base = img.crop((x, y, x + lado, y + lado)); img.paste(ImageChops.add(base, spr), (x, y))
            else:
                img.paste(spr, (x, y), spr)
    img.save(AQUI / "temas_gato8.png")


if __name__ == "__main__":
    import sys
    que = sys.argv[1:] or ["hoja", "temas", "banda", "animos", "resumen"]
    if "hoja" in que:
        hoja("botanico", False, "hoja_sprites_botanico.png", material=True)
        hoja("distopia", False, "hoja_sprites_distopia.png", material=True)
        hoja("romano", False, "hoja_sprites_romano.png")
    if "temas" in que:
        comparar_temas()
    s_, cs = buscar_semilla()
    if "banda" in que:
        print("normal: semilla", s_, "estados", sorted({c[0] for c in cs}))
        solo = [b for b in BANDAS if b[0] in sys.argv[2:]] or None
        gif_banda(cs, solo)
    if "animos" in que:
        for animo, pedidos in (("alegre", ("jugar", "saltar", "girar")), ("triste", ("caminar_triste", "quieto_triste", "sentarse", "girar")),
                               ("enojado", ("enojado", "caminar", "girar"))):
            s2, cs2 = buscar_semilla(animo, pedidos, 15.0)
            gif_banda(cs2, [("romano", False, f"Romano · ánimo {animo}")], "_" + animo)
    if "resumen" in que:
        resumen_webp(cs, AQUI / "todas_las_bandas.webp")
    print("listo")
