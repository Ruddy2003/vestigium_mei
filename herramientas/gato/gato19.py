"""
Prueba 19 (lote 9a): los gatos que ya había, completos.
- Fondos que se mueven en los ocho que estaban quietos: la fuente del mosaico romano, la luz del vitral y la vela, el polvo
  en el rayo de luz del scriptorium, los estandartes de la Orden, las hojas y la hormiga del bordado, los banderines y la
  caravana de la Ruta de la Seda, la fuente del patio andalusí, el molino y los pájaros del cuaderno de Leonardo.
- Noches propias (dibujadas como noche, no recoloreadas) y días con más detalle donde el fondo era casi plano.
- El gato de noche con sus propios colores en Al-Ándalus, Botánico, Orden, Renacimiento y Ruta de la Seda.
- Objetos propios: la tetera de la Seda, el azahar de Al-Ándalus, la campana del pueblo de Colonos y el gorro frigio que
  lanza la Revolución.
- Terminal (id «ciber»): pelaje monocromo cian, consola con un volcado de memoria que cae, y «persigue el cursor».
Todo lo demás viene de gato18.py.

Los temas de aquí pasan por el camino de los fondos nuevos de gato9 (lo que se mueve va en ANIM, delante o detrás del
gato, y la sombra y el «ajustar» se leen del tema).
"""
import math
import random
import sys
from contextlib import contextmanager
from pathlib import Path

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato17 as g17      # noqa: E402
import gato18 as g18      # noqa: E402
from gato8 import hx, mezcla, ESC                                                     # noqa: E402
from gato9 import (W, H, ANIM, lienzo, degradado, ondas, perfil, luz, estrellas, nube, cuad, llama,  # noqa: E402
                   subir, columna)

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
for _m in ("gato8", "gato9", "gato10", "gato11", "gato12", "gato13", "gato15", "gato16", "gato17", "gato18"):
    sys.modules[_m].AQUI = AQUI

# Los temas de este archivo. Pasan a la lista de gato9 (fondos con ANIM), pero su gato sigue saliendo de la cadena de
# siempre: mientras se arma el tema se sacan de la lista, para que gato9 no los trate como suyos.
MIOS = ["romano", "gotico", "medieval", "ciber", "botanico", "orden", "seda", "andalus", "renacimiento", "espacio", "navidad"]
g9.NUEVOS.extend(MIOS)


@contextmanager
def _fuera():
    for n in MIOS: g9.NUEVOS.remove(n)
    try:
        yield
    finally:
        g9.NUEVOS.extend(MIOS)


def v(col, k):
    """Un color hacia el negro (k = 0 negro, 1 el color)."""
    return mezcla((0, 0, 0), col, k)


# =====================================================================
#  El gato de cada tema
# =====================================================================
_tema18 = g8.tema
CIAN, CIAN_C, CIAN_M = hx("00E5FF"), hx("C8FBFF"), hx("5FC4D2")


def _noche(nombre, t):
    """Los colores de noche del gato en los temas que tenían el mismo atlas de día y de noche."""
    col = dict(t["col"])
    if nombre == "andalus":
        # andalusOscuro: fondo 0B1716, oro E0B65A, verde 4FC1A6, almagre E07A5F. Azulejo bajo la luna: junta en penumbra.
        col = g9.de_noche(col, hx("18302D"), 0.14)
        col.update(E=hx("4FC1A6"), C=hx("3F6FC0"), G=hx("E0B65A"), Z=hx("4FC1A6"), M=hx("F3EAD6"))
        return dict(t, col=col, junta=mezcla(hx("FBF6EA"), hx("0B1716"), 0.62))
    if nombre == "botanico":
        # botanicoOscuro: lino 14120D, musgo A9C27A, óxido E09A5E, verde escarabajo 6FC2B0: hilos que brillan en la tela oscura.
        col = g9.de_noche(col, hx("2A251B"), 0.1)
        col.update(E=hx("A9C27A"), C=hx("A9C27A"), G=hx("6FC2B0"), Z=hx("A9C27A"), J=hx("E09A5E"))
        return dict(t, col=col)
    if nombre == "orden":
        # ordenOscuro: carmesí D0333B, latón D6B25A, acero 8FB3C8.
        col = g9.de_noche(col, hx("22262B"), 0.12)
        col.update(E=hx("D6B25A"), C=hx("D6B25A"), A=hx("C0303A"), B=mezcla(hx("C0303A"), hx("101215"), 0.5), G=hx("E8C870"))
        return dict(t, col=col)
    if nombre == "seda":
        # sedaOscuro: índigo 0F1426, oro E0B04F, granada E26A50, turquesa 4FC3C3.
        col = g9.de_noche(col, hx("1F2846"), 0.14)
        col.update(E=hx("4FC3C3"), A=hx("C8443A"), B=hx("2E4A9E"), Y=hx("2E4A9E"), G=hx("E0B04F"))
        return dict(t, col=col)
    if nombre in ("navidad", "espacio"):
        # Antes el mismo gato de día y de noche; de noche toma un poco del color de la escena (navidadOscuro, espacioOscuro).
        return dict(t, col=g9.de_noche(col, hx("1C3125") if nombre == "navidad" else hx("121A31"), 0.1))
    if nombre == "renacimiento":
        # De noche, el cuaderno es papel pardo oscuro: el gato va en tiza blanca con sombras de sanguina (renacimientoOscuro).
        # De noche el cuaderno es papel teñido: contorno de carbón, sombras de sanguina y luces de tiza blanca (tres lápices).
        return dict(t, sepia=hx("E6D8B8"), sanguina=hx("9A4A2A"), papel=hx("7A6448"), tiza=hx("F4ECD8"))
    return t


def tema(nombre, oscuro=False):
    if nombre not in MIOS:
        return _tema18(nombre, oscuro)
    with _fuera():
        t = dict(_tema18(nombre, oscuro))
    if oscuro:
        t = _noche(nombre, t)
        if nombre in g18.NOCHE_FONDO:
            t["fondo"] = hx(g18.NOCHE_FONDO[nombre])      # el fondo del esquema oscuro (solo para las hojas de revisión)
    t["oscuro"] = oscuro
    t["ajustar"] = nombre in g8.AJUSTAR
    t["sombra_col"] = (0, 0, 0) if oscuro else (20, 20, 24)
    t["sombra_a"] = 0 if nombre in g8.SIN_SOMBRA else ((70 if oscuro else 55) if nombre != "navidad" else (60 if oscuro else 45))
    if nombre == "ciber":
        t = _terminal(t)
    return t


for _m in ("gato8", "gato9", "gato10", "gato12", "gato13", "gato16", "gato17", "gato18"):
    sys.modules[_m].tema = tema


# ---------------------------------------------------------------------
#  Terminal: monocromo cian (Tema.kt, `ciber`: primario 00E5FF, secundario 5FC4D2, terciario C8FBFF, fondo 020506)
# ---------------------------------------------------------------------

def _terminal(t):
    k = lambda f: v(CIAN, f)
    claro = lambda f: mezcla(CIAN, CIAN_C, f)
    col = dict(t["col"])
    col.update(P=claro(0.5), L=CIAN_C, W=claro(0.15), S=k(0.55), K=k(0.3), D=k(0.4), T=k(0.62), E=CIAN_C, N=claro(0.2),
               R=k(0.7), C=CIAN, G=CIAN_C, A=k(0.2), B=CIAN, Y=k(0.2), J=CIAN_C, H=(230, 255, 255), M=k(0.2), Z=CIAN, O=CIAN_C)
    col.update(g17.colores("ciber", True, 0, t))
    return dict(t, col=col, fondo=hx("020506"), texto=hx("BDEFF5"), borde=hx("1A464D"), pantalla=True, reflejo=CIAN)


_pintar = g8.pintar


def pintar(g, t, cel, oscuro=False):
    """El boceto de noche lleva además luces de tiza blanca en lo claro del pelaje."""
    img = _pintar(g, t, cel, oscuro)
    if t["material"] != "boceto" or "tiza" not in t:
        return img
    s = cel * g8.SS
    capa = Image.new("RGBA", (g8.N * s, g8.N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(capa)
    w = max(1, int(0.16 * s))
    for r, c, v_ in g8.celdas(g):
        if v_ in "LH" or (v_ == "P" and (r * 2 + c) % 3 == 0):
            rnd = random.Random(r * 61 + c * 29)
            j = lambda: rnd.uniform(-0.08, 0.08) * s
            x0, y0 = c * s, r * s
            d.line([x0 + j(), y0 + s + j(), x0 + s + j(), y0 + j()], fill=t["tiza"] + (235 if v_ != "P" else 170,), width=w)
            if v_ == "H":
                d.rectangle([x0, y0, x0 + s, y0 + s], fill=t["tiza"] + (255,))
    capa = capa.resize((g8.N * cel, g8.N * cel), Image.LANCZOS)
    return Image.alpha_composite(img, capa)


g8.pintar = pintar


# =====================================================================
#  Objetos propios
# =====================================================================
rect = g17.rect


def m_cursor(L, a):
    """Terminal: el cursor de bloque (▮) que se escapa por la pantalla; parpadea y deja un guion bajo detrás."""
    x, y = a["obj"]; k = a["k"]
    if k % 2 == 0:
        rect(L, "b_U", x - 1.2, y - 2.0, x + 1.2, y + 2.0)
        rect(L, "b_Q", x - 1.2, y - 2.0, x - 0.4, y + 2.0)
    else:
        L.capsula("b_V", [(x - 1.2, y - 2.0), (x + 1.2, y - 2.0), (x + 1.2, y + 2.0), (x - 1.2, y + 2.0), (x - 1.2, y - 2.0)], 0.5)
    L.capsula("b_X", [(x - 4.6, y + 2.0), (x - 2.4, y + 2.0)], 0.55)


def o_tetera(L, a):
    """Ruta de la Seda: tetera de porcelana azul con motas blancas (choynak); en los cuadros 1 y 2 sirve el té en una piala."""
    x, y = a["obj"]; k = a["k"]
    inc = (0, 24, 32, 8)[k]
    r = math.radians(inc)
    P = lambda dx, dy: (x + dx * math.cos(r) - dy * math.sin(r), y + dx * math.sin(r) + dy * math.cos(r))
    L.capsula("f_U", [P(2.4, 0.4), P(4.0, -0.8), P(4.8, -2.6)], 0.8)                  # pico
    L.capsula("f_U", [P(-2.2, -1.6), P(-4.0, -1.0), P(-3.8, 1.4), P(-2.4, 1.8)], 0.6)  # asa
    L.elipse("f_U", *P(0, 0.4), 2.9, 2.5, inc)                                       # cuerpo
    for dx, dy in ((-1.3, -0.2), (0.9, 0.9), (0.2, -1.0), (-0.5, 1.5), (1.6, -0.4)):
        L.elipse("f_V", *P(dx, dy), 0.45, 0.45)                                       # motas de algodón
    L.elipse("f_V", *P(0, -2.0), 1.6, 0.6, inc)                                       # tapa
    L.elipse("f_X", *P(0, -2.9), 0.65, 0.65)                                          # botón de la tapa
    bx, by = x + 6.4, y + 12.4                                                        # la piala en el suelo
    L.poligono("b_V", [(bx - 2.4, by - 1.2), (bx + 2.4, by - 1.2), (bx + 1.5, by + 1.0), (bx - 1.5, by + 1.0)])
    L.capsula("b_U", [(bx - 2.2, by - 0.6), (bx + 2.2, by - 0.6)], 0.5)
    px, py = P(4.8, -2.6)
    if k in (1, 2):
        L.capsula("f_Q", [(px + 0.3, py + 0.2), (px + 1.0, py + 2.6), (bx - 0.2, by - 1.4)], 0.55)
    else:
        g17.vapor(L, "f_Q", px, py - 0.4, k)


def s_azahar(L, a):
    """Al-Ándalus: una ramita de naranjo con dos flores de azahar (cinco pétalos blancos) y una naranja chica."""
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_X", [(x + 1.0, y + 3.6), (x + 0.2, y + 0.6), (x - 0.6, y - 1.8)], 0.55)
    L.elipse("b_X", x + 1.9, y + 0.2, 1.5, 0.6, -35); L.elipse("b_X", x - 1.4, y + 1.2, 1.4, 0.55, 30)
    for cx, cy, r in ((x - 0.6, y - 2.8, 1.15), (x + 1.9, y - 1.6, 0.95)):
        for j in range(5):
            an = 2 * math.pi * j / 5 + k * 0.12
            L.elipse("b_U", cx + r * math.cos(an), cy + r * math.sin(an), 0.75, 0.75)
        L.elipse("b_V", cx, cy, 0.5, 0.5)
    L.elipse("b_G", x + 1.2, y + 2.4, 0.9, 0.9)
    if k in (1, 3):                                   # el aroma
        L.capsula("b_Q", [(x - 2.4, y - 4.4), (x - 3.0, y - 5.6)], 0.35)


def tocar(k):
    """Sentado, alza una campana de mano y la hace sonar (la campana del pueblo para llamar a la asamblea)."""
    s = (-1, 0, 1, 0)[k]
    p = dict(g8.SENTADO)
    p.update(inc=-4 + 2 * abs(s), cab=(37.8, 27.4))
    p["fn"] = g17.brazo((36, 42), (47.6 + 0.4 * s, 34.6), (1.4, 1.2))
    return p, dict(obj=(47.6 + 0.4 * s, 34.6), ang=24 * s, k=k)


def l_campana(L, a):
    x, y = a["obj"]; k = a["k"]
    r = math.radians(a["ang"])
    P = lambda dx, dy: (x + dx * math.cos(r) - dy * math.sin(r), y + dx * math.sin(r) + dy * math.cos(r))
    L.capsula("f_X", [P(0, 0.6), P(0, -2.0)], 1.2)                                     # mango de madera
    L.poligono("f_U", [P(-1.2, 1.0), P(1.2, 1.0), P(2.0, 3.0), P(2.9, 5.6), P(-2.9, 5.6), P(-2.0, 3.0)])
    L.capsula("f_V", [P(-3.0, 5.6), P(3.0, 5.6)], 0.8)                                 # labio
    L.elipse("f_K", *P(-1.6 * a["ang"] / 26, 6.6), 0.75, 0.75)                          # badajo
    if k % 2 == 0:                                                                     # el sonido
        sx = 1 if a["ang"] >= 0 else -1
        for j in (0, 1):
            L.capsula("f_Q", [P(sx * (4.0 + j * 1.5), 2.6 + j * 0.4), P(sx * (4.4 + j * 1.5), 5.0 + j * 0.4)], 0.4)


def lanzar(k):
    """Sentado, se quita el gorro frigio y lo lanza al aire (en el cuadro 0 lo tiene puesto y alza las patas)."""
    sube = (0.0, 6.0, 11.0, 5.0)[k]
    p = dict(g8.SENTADO)
    p.update(C=(32.5, 37.5), rc=7.4, cab=(35, 25), inc=-12 if k else -4)
    alto = 21.0 if k else 23.5
    p["fn"] = g17.brazo((35, 39), (42.2, alto), (1.6, 0.2))
    p["ff"] = g17.brazo((32, 39.5), (39.2, alto + 0.6), (1.6, 0.2))
    return p, dict(obj=(40.6, 12.0 - sube), giro=(0, -15, -30, -12)[k], k=k, sin_sombrero=k > 0)


def l_frigio(L, a):
    """El mismo gorro que lleva puesto (gato8, «frigio»), girando en el aire."""
    if a["k"] == 0: return
    x, y = a["obj"]
    r = math.radians(a["giro"])
    P = lambda dx, dy: (x + dx * math.cos(r) - dy * math.sin(r), y + dx * math.sin(r) + dy * math.cos(r))
    L.poligono("f_V", [P(-6.2, 3.9), P(-6.4, -0.9), P(-2.2, -3.9), P(3.8, -3.3), P(6.8, -1.3), P(4.4, -0.5), P(6.4, 3.7)])
    L.elipse("f_X", *P(-3.6, 1.9), 1.6, 1.6)
    L.elipse("f_U", *P(-3.6, 1.9), 0.8, 0.8)


_lado18 = g8.sombrero_lado


def sombrero_lado(L, cx, cy, inc, som):
    """Si la animación se quita el sombrero (el gorro que lanza la Revolución), no se dibuja en la cabeza."""
    ps = g17._actual.get("ps")
    if ps and ps.get("objeto") and ps["objeto"][1].get("sin_sombrero"):
        som = None
    _lado18(L, cx, cy, inc, som)


g8.sombrero_lado = sombrero_lado


g17.ACCIONES.update(tocar=(tocar, 4, "sentado"), lanzar=(lanzar, 4, "sentado"))
g17.ESCALA.update(tocar=1.7, lanzar=1.0)

g17.ESP["ciber"] = [("trabajar", g17.t_teclado, "teclea en la consola"), ("mirar", m_cursor, "persigue el cursor")]
g17.ESP["seda"][0] = ("sostener", o_tetera, "sirve té de la tetera")
g17.ESP["andalus"][1] = ("olfatear", s_azahar, "huele el azahar")
g17.ESP["colonos"][1] = ("tocar", l_campana, "toca la campana del pueblo")
g17.ESP["revolucion"][1] = ("lanzar", l_frigio, "lanza al aire el gorro frigio")

g17.COL["ciber"] = (v(CIAN, 0.45), v(CIAN, 0.2), CIAN, CIAN_C)
g17.EXTRA_COL[("ciber", 1)] = dict(U=CIAN, V=CIAN_M, X=v(CIAN, 0.5), Q=CIAN_C)
g17.EXTRA_COL[("seda", 0)] = dict(U=hx("2E5AAE"), V=hx("F4F1EA"), X=hx("E0B04F"), Q=hx("E9C98A"))
g17.EXTRA_COL[("andalus", 1)] = dict(U=hx("FFFDF6"), V=hx("F2C14E"), X=hx("2F6B3A"), Q=hx("FFF3C8"), G=hx("F08A1E"))
g17.EXTRA_COL[("colonos", 1)] = dict(U=hx("B8892B"), V=hx("8A6420"), X=hx("6A4A2A"), Q=hx("1F2A3A"))
g17.EXTRA_COL[("revolucion", 1)] = dict(U=hx("1F3A6E"), V=hx("B22234"), X=hx("FAF6EC"), Q=hx("FAF6EC"))
g18.ESCALA_ESP.update({("ciber", 1): 2.2, ("seda", 0): 1.55, ("andalus", 1): 1.6, ("colonos", 1): 1.7, ("revolucion", 1): 1.0})

_colores17 = g17.colores


def colores(nombre, oscuro, j, t):
    c = _colores17(nombre, oscuro, j, t)
    if nombre == "colonos" and j == 1 and oscuro:
        c["Q"] = hx("E3D9C2")          # las ondas del sonido, claras sobre la noche
    if nombre == "andalus" and j == 1:
        c["G"] = hx("F08A1E")          # la naranja (la letra G es la del broche en las otras animaciones)
    return c


g17.colores = colores

# Volver a registrar las animaciones propias (con su escala), como hace gato18.
for _n, _lista in g17.ESP.items():
    for _j, (_acc, _f, _) in enumerate(_lista):
        _fn, _k, _ = g17.ACCIONES[_acc]
        _cs = []
        for _i in range(_k):
            _p, _a = _fn(_i)
            _a["accion"] = _acc
            _a["escala"] = g18.ESCALA_ESP.get((_n, _j), g17.ESCALA[_acc])
            _cs.append(dict(_p, objeto=(_f, _a), semilla=_i))
        g8.ANIMS[f"esp_{_n}_{_j}"] = _cs
g8._spr.clear(); g8._rej.clear()


# =====================================================================
#  Herramientas comunes de los fondos
# =====================================================================

def bucle(n):
    """Caché por fase: lo que se dibuja en el cuadro tick depende solo de tick % n."""
    def deco(f):
        cache = {}

        def g(tick):
            k = tick % n
            if k not in cache:
                cache[k] = f(k)
            return cache[k]
        return g
    return deco


def pegar(img, parche, x, y):
    """Pega un parche (RGB o RGBA) en px de la banda."""
    if parche.mode == "RGBA":
        img.paste(parche, (x, y), parche)
    else:
        img.paste(parche, (x, y))


def dp_a_px(im):
    return im.resize((im.width * ESC, im.height * ESC), Image.NEAREST)


def creciente(im, cx, cy, r, col, corte, dy=-1):
    """Luna creciente a 1 px por dp: el disco menos otro disco corrido (sin pintar el cielo encima)."""
    px = im.load()
    for y in range(cy - r, cy + r + 1):
        for x in range(cx - r, cx + r + 1):
            if (x - cx) ** 2 + (y - cy) ** 2 <= r * r + 1 and (x - cx - corte) ** 2 + (y - cy - dy) ** 2 > r * r:
                if 0 <= x < W and 0 <= y < H: px[x, y] = col


def animado(nombre, o, cuadro_dp, periodo, delante=None, escala=True):
    """Registra en ANIM un fondo que cambia por fases: cuadro_dp(k) da la banda entera del cuadro k (a 1 px por dp, o ya a
    la escala de la banda si escala=False). delante(img, k), si se da, dibuja lo que va delante del gato."""
    @bucle(periodo)
    def cuadro_(k):
        im = cuadro_dp(k)
        return dp_a_px(im) if escala else im

    def atras(img, tick, d):
        if not d:
            img.paste(cuadro_(tick))

    fs = [atras]
    if delante:
        fs.append(lambda img, tick, d: delante(img, tick % periodo) if d else None)
    ANIM[(nombre, o)] = fs
    return cuadro_(0)


# ---------------------------------------------------------------------
#  Letra de 3×5 pixeles (pantallas y rótulos a 1 px por dp)
# ---------------------------------------------------------------------
LETRA = {
    "0": "111101101101111", "1": "010110010010111", "2": "111001111100111", "3": "111001111001111", "4": "101101111001001",
    "5": "111100111001111", "6": "111100111101111", "7": "111001010010010", "8": "111101111101111", "9": "111101111001111",
    "A": "010101111101101", "B": "110101110101110", "C": "011100100100011", "D": "110101101101110", "E": "111100110100111",
    "F": "111100110100100", "G": "011100101101011", "I": "111010010010111", "K": "101101110101101", "L": "100100100100111",
    "M": "101111111101101", "N": "110101101101101", "O": "010101101101010", "P": "110101110100100", "R": "110101110101101",
    "S": "011100010001110", "T": "111010010010010", "U": "101101101101111", "V": "101101101101010", "X": "101101010101101",
    "Y": "101101010010010", "Z": "111001010100111", ":": "000010000010000", ".": "000000000000010", "-": "000000111000000",
    ">": "100010001010100", "_": "000000000000111", "/": "001001010100100", "\\": "100100010001001", " ": "000000000000000",
    "|": "010010010010010", "=": "000111000111000", "%": "101001010100101",
    "H": "101101111101101", "J": "001001001101010", "Q": "010101101110011", "W": "101101111111101",
}


def patron(d, x, y, filas, mapa):
    """Dibuja un dibujo de pixeles escrito en texto (un carácter por pixel; «.» es vacío)."""
    for r, fila in enumerate(filas):
        for c, ch in enumerate(fila):
            if ch in mapa:
                d.point((x + c, y + r), fill=mapa[ch])


def texto(d, x, y, s, col):
    for ch in s:
        pat = LETRA.get(ch.upper(), LETRA[" "])
        for i, b in enumerate(pat):
            if b == "1":
                d.point((x + i % 3, y + i // 3), fill=col)
        x += 4
    return x


# =====================================================================
#  Terminal: una consola con un volcado de memoria; algunas columnas de hexadecimal caen (8a del plan).
# =====================================================================

def rejilla_px(im_dp):
    """Pixeles visibles de la pantalla: rejilla oscura entre pixeles, como en el fondo de antes."""
    big = dp_a_px(im_dp)
    a = np.asarray(big, np.float32)
    yy, xx = np.mgrid[0:big.height, 0:big.width]
    rej = np.where((xx % ESC == ESC - 1) | (yy % ESC == ESC - 1), 0.55, 1.0)
    return Image.fromarray(np.clip(a * rej[..., None], 0, 255).astype(np.uint8))


def fondo_terminal(t):
    negro = hx("020506")
    tenue, medio, vivo = v(CIAN, 0.2), v(CIAN, 0.42), CIAN
    rnd = random.Random(19)
    HEXA = "0123456789ABCDEF"
    filas = list(range(12, 66, 7))                    # renglones del volcado
    cols = list(range(4, W - 4, 4))                   # columnas de letra (4 dp)
    base_txt = {}
    for fy in filas:
        linea = "%04X: " % (0x0400 + filas.index(fy) * 16)
        while len(linea) < len(cols):
            linea += "".join(rnd.choice(HEXA) for _ in range(2)) + " "
            if len(linea) % 27 == 26: linea += " "
        for j, cx in enumerate(cols):
            base_txt[(cx, fy)] = linea[j] if j < len(linea) else " "
    lluvias = [(cols[j], (j * 7) % 13) for j in (5, 14, 22, 31, 43, 52, 61, 73, 84, 93)]
    P = 48                                             # 12 pasos de 4 cuadros

    def cuadro_dp(k):
        im, d = lienzo(negro)
        d.rectangle([0, 0, W, 7], fill=v(CIAN, 0.12))
        texto(d, 4, 1, "VESTIGIUM :: GATO.EXE", vivo)
        texto(d, W - 4 - 4 * 17, 1, "MEM 0412K  SYS OK", medio)
        d.line([0, 9, W, 9], fill=v(CIAN, 0.18))
        paso = k // 4
        brillo = {}
        for cx, fase in lluvias:                       # columnas que caen: cabeza clara y estela que se apaga
            cab = (paso + fase) % 12
            for j, fy in enumerate(filas):
                dist = cab - j
                if dist == 0: brillo[(cx, fy)] = (CIAN_C, True)
                elif 0 < dist <= 3: brillo[(cx, fy)] = ((vivo, medio, medio)[dist - 1], dist == 1)
        for (cx, fy), ch in base_txt.items():
            col, cambia = brillo.get((cx, fy), (medio if cx < 24 else tenue, False))
            if cambia and ch != " ":
                ch = HEXA[(hash((cx, fy, paso)) & 0xFFFF) % 16]
            texto(d, cx, fy, ch, col)
        d.line([0, 71, W, 71], fill=v(CIAN, 0.3))
        d.line([0, 74, W, 74], fill=v(CIAN, 0.55))
        x = texto(d, 4, 75, "C:\\GATO> PASEAR --DESPACIO", medio)
        if (k // 6) % 2 == 0:
            d.rectangle([x, 75, x + 2, 79], fill=vivo)
        return im

    return animado("ciber", True, lambda k: rejilla_px(cuadro_dp(k)), P, escala=False)


g8.FONDOS["ciber"] = fondo_terminal


# =====================================================================
#  Romano: el piso de mosaico con un emblema al centro: un cántaro que es fuente, con dos palomas en el borde (como las
#  palomas del mosaico de Plinio). El agua es de teselas que se mueven. De noche, el mosaico de basalto (blanco sobre negro,
#  como los de Ostia): el emblema es un cielo de teselas azules con luna y estrellas de oro que titilan.
# =====================================================================
GRECA = ["XXXXXXX.", "X.....X.", "X.XXX.X.", "X.X...X.", "X.XXXXXX"]


def teselar(grid, junta, x0=0, y0=0, x1=W, y1=H, m=2):
    """Pinta teselas (1 por dp) como el mosaico de gato8: cada una algo torcida y de tono propio, con junta entre ellas.
    El azar depende solo de la posición, así que cualquier pedazo sale igual que en la banda entera."""
    s = ESC * g8.SS
    ax0, ay0, ax1, ay1 = max(0, x0 - m), max(0, y0 - m), min(W, x1 + m), min(H, y1 + m)
    img = Image.new("RGB", ((ax1 - ax0) * s, (ay1 - ay0) * s), junta)
    d = ImageDraw.Draw(img); px = grid.load()
    g = 0.15 * s
    for y in range(ay0, ay1):
        for x in range(ax0, ax1):
            rnd = random.Random(x * 7919 + y * 104729 + 17)
            k = 1 + rnd.uniform(-0.05, 0.05)
            c = tuple(max(0, min(255, round(ch * k))) for ch in px[x, y])
            j = lambda: rnd.uniform(-0.07, 0.07) * s
            X0, Y0 = (x - ax0) * s + g / 2, (y - ay0) * s + g / 2
            X1, Y1 = (x - ax0 + 1) * s - g / 2, (y - ay0 + 1) * s - g / 2
            d.polygon([(X0 + j(), Y0 + j()), (X1 + j(), Y0 + j()), (X1 + j(), Y1 + j()), (X0 + j(), Y1 + j())], fill=c)
    img = img.resize(((ax1 - ax0) * ESC, (ay1 - ay0) * ESC), Image.LANCZOS)
    return img.crop(((x0 - ax0) * ESC, (y0 - ay0) * ESC, (x1 - ax0) * ESC, (y1 - ay0) * ESC))


def fondo_romano(t):
    o = t["oscuro"]
    tinta, porf, oro, bron, crema = hx("17191C"), hx("7C2433"), hx("B8892B"), hx("2D7A6F"), hx("F2EEE2")
    if not o:
        campo = [hx("ECEDEA"), hx("E4E2DA"), hx("F2F1EC"), hx("E8E6DF"), hx("ECEDEA")]
        linea, fondo_l, filete = tinta, crema, porf
        dentro = [hx("F2EEE2"), hx("EEE9DC"), hx("F5F2EA")]
        bronce = [hx("6B4A1E"), hx("A8782E"), hx("C9934A"), hx("E2B84E")]
        agua = [hx("3E6E9A"), hx("6F98C0"), hx("A9C1D8"), hx("F5F7F4")]
        paloma = [hx("8A8C88"), hx("B5B6B0"), hx("DAD9D2"), hx("4E6E5E")]
        junta = t.get("junta", hx("C9CBC4"))
    else:
        campo = [hx("0E1113"), hx("14181B"), hx("101315"), hx("1A1E22"), hx("0E1113")]
        linea, fondo_l, filete = hx("E7E3DA"), hx("14181B"), hx("5E1B27")
        dentro = [hx("17223A"), hx("1B2843"), hx("142035")]
        bronce = [hx("3A2A12"), hx("7A5A26"), hx("A8782E"), hx("D2AE5A")]
        agua = [hx("2E4A6A"), hx("4E6E92"), hx("8EA8C4"), hx("E7E3DA")]
        paloma = [hx("3A4048"), hx("5A6068"), hx("7A8088"), hx("2E4A44")]
        junta = hx("20252A")
    rnd = random.Random(5)
    base, d = lienzo(campo[0]); px = base.load()
    for y in range(H):
        for x in range(W):
            px[x, y] = rnd.choice(campo)
    for x in range(W):
        for y in (0, 6, 74):
            px[x, y] = porf if not o else filete
        for k in range(5):
            px[x, 1 + k] = linea if GRECA[k][x % 8] == "X" else fondo_l
            if 75 + k < H: px[x, 75 + k] = linea if GRECA[k][(x + 4) % 8] == "X" else fondo_l
    ex0, ey0, ex1, ey1 = 168, 11, 244, 69                 # el emblema
    for k, x0 in enumerate(range(26, W, 52)):              # florecitas del campo
        y0 = 26 if k % 2 == 0 else 52
        if ex0 - 6 <= x0 <= ex1 + 6: continue
        for dx, dy, c in [(0, 0, oro), (-1, 0, porf), (1, 0, porf), (0, -1, porf), (0, 1, porf), (-2, 0, bron), (2, 0, bron),
                          (0, -2, bron), (0, 2, bron), (-1, -1, crema), (1, -1, crema), (-1, 1, crema), (1, 1, crema)]:
            if o and c == crema: c = hx("C9CBC4")
            if o and c == porf: c = hx("A63A4D")
            px[x0 + dx, y0 + dy] = c
    # Marco del emblema: filete, trenza de dos cabos y filete de tinta.
    d.rectangle([ex0, ey0, ex1, ey1], fill=filete)
    for y in range(ey0 + 1, ey1):
        for x in range(ex0 + 1, ex1):
            u = x + y
            if ex0 + 1 <= x <= ex1 - 1 and ey0 + 1 <= y <= ey1 - 1:
                px[x, y] = (oro, crema if not o else hx("C9CBC4"), porf if not o else hx("A63A4D"), crema if not o else hx("C9CBC4"))[(u // 2) % 4]
    d.rectangle([ex0 + 4, ey0 + 4, ex1 - 4, ey1 - 4], fill=linea)
    ix0, iy0, ix1, iy1 = ex0 + 5, ey0 + 5, ex1 - 5, ey1 - 5
    for y in range(iy0, iy1 + 1):
        for x in range(ix0, ix1 + 1):
            px[x, y] = rnd.choice(dentro)
    cx = (ix0 + ix1) // 2                                  # el cántaro
    if o:
        for (mx, my) in ((ix0 + 11, iy0 + 8),):           # luna creciente
            d.ellipse([mx - 5, my - 5, mx + 5, my + 5], fill=hx("E7E3DA"))
            d.ellipse([mx - 3, my - 6, mx + 6, my + 4], fill=dentro[0])
    verde, verde2 = (hx("3F6B3A"), hx("6E9A4A")) if not o else (hx("1E3A30"), hx("2E5A44"))
    for bx in (cx - 22, cx - 17, cx + 17, cx + 22):                                            # matas al pie
        for q in range(5):
            px[bx + (q % 3) - 1, 62 - q] = verde if q % 2 else verde2
        px[bx, 57] = hx("9E3445") if not o else hx("A63A4D")
    d.polygon([(cx - 5, 62), (cx + 5, 62), (cx + 3, 59), (cx - 3, 59)], fill=bronce[1])       # pie
    d.line([cx - 5, 62, cx + 5, 62], fill=bronce[0])
    d.rectangle([cx - 1, 54, cx + 1, 59], fill=bronce[1]); d.point((cx - 1, 56), fill=bronce[3])
    d.chord([cx - 15, 26, cx + 15, 55], 0, 180, fill=bronce[2])                                 # cuenco
    d.chord([cx - 15, 26, cx + 15, 55], 70, 180, fill=bronce[1])
    d.arc([cx - 11, 30, cx + 11, 51], 15, 60, fill=bronce[3])
    for k_ in range(-12, 13, 4): d.line([cx + k_, 42, cx + k_ * 0.8, 49], fill=bronce[1])      # gallones
    d.line([cx - 16, 40, cx + 16, 40], fill=bronce[0]); d.line([cx - 15, 41, cx + 15, 41], fill=bronce[3])
    for sx in (-1, 1):                                                                         # asas
        d.line([cx + sx * 16, 40, cx + sx * 19, 41, cx + sx * 19, 45, cx + sx * 15, 48], fill=bronce[0])
        px[cx + sx * 19, 42] = bronce[3]
    d.line([cx - 14, 40, cx + 14, 40], fill=agua[1])
    palomas = [(cx - 12, 39, 1), (cx + 10, 39, 1)]
    zona = (ix0, iy0, ix1 + 1, iy1 + 1)
    estrellas_ = [(ix0 + 4, iy0 + 20), (ix0 + 22, iy0 + 4), (ix0 + 40, iy0 + 9), (ix1 - 5, iy0 + 3), (ix1 - 9, iy0 + 18),
                  (ix0 + 28, iy0 + 15), (ix0 + 6, iy0 + 30), (ix1 - 3, iy0 + 30)]
    DE_PIE = ["......hh", ".....hhe", "tbbbbbn.", "bbwwwbb.", ".bbbbb..", "..f.f..."]
    BEBE = ["........", "........", "tbbbbbn.", "bbwwwbnh", ".bbbbbhe", "..f.f..."]

    def paloma_(dd, x, y, s, baja):
        c0, c1, c2, cuello = paloma
        cols = dict(h=c1, e=tinta if not o else c0, b=c1, w=c2, t=c0, n=cuello, f=hx("9E3445") if not o else c0)
        for r, fila in enumerate(BEBE if baja else DE_PIE):
            for c, ch in enumerate(fila):
                if ch != ".":
                    dd.point((x + (c if s > 0 else 7 - c) - 3, y - 5 + r), fill=cols[ch])

    P = 64

    def grid_k(k):
        g = base.copy(); dd = ImageDraw.Draw(g); gp = g.load()
        # Chorro: sube por el centro y cae en dos arcos al cuenco; las gotas claras avanzan con k.
        for j, y in enumerate(range(19, 40)):
            gp[cx, y] = agua[3] if (y + k // 2) % 4 == 0 else agua[2 if j % 2 else 1]
        for sx in (-1, 1):
            prev = None
            for q in range(40):
                f = q / 39
                x = cx + sx * (1 + 8 * f); y = 19 - 3 * math.sin(math.pi * min(1, f * 2.2) / 2) + 24 * max(0, f - 0.2) ** 1.7
                if y > 39: break
                pt = (round(x), round(y))
                if pt == prev: continue
                prev = pt
                tono = agua[3] if (q // 2 - k // 2) % 5 == 0 else agua[2]
                gp[pt] = tono
        for x in range(cx - 13, cx + 14):                    # ondas en el agua del cuenco
            if (x + k // 4) % 6 == 0: gp[x, 40] = agua[3]
            elif (x + k // 4) % 6 == 3: gp[x, 40] = agua[2]
        for j, (x, y, sx) in enumerate(palomas):
            paloma_(dd, x, y, sx, (not o) and j == 0 and 32 <= k < 52)
        if o:
            for j, (sx_, sy_) in enumerate(estrellas_):
                if (j * 5 + k // 8) % 8 != 0:
                    gp[sx_, sy_] = bronce[3]
                    if j % 3 == 0:
                        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)): gp[sx_ + dx, sy_ + dy] = bronce[2]
        return g

    @bucle(P)
    def parche(k):
        return teselar(grid_k(k), junta, *zona)

    fondo = teselar(grid_k(0), junta)

    def vivo(img, tick, delante):
        if not delante:
            img.paste(parche(tick), (zona[0] * ESC, zona[1] * ESC))

    ANIM[("romano", o)] = [vivo]
    return fondo


g8.FONDOS["romano"] = fondo_romano


# =====================================================================
#  Gótico: el muro de ocho lancetas. De día el sol pasa por el vidrio: los colores se encienden, manchan de luz el
#  alféizar y en la luz flota polvo; dos velas arden en los pilares. De noche el vidrio se apaga (solo la luna azulea
#  algunas piezas) y la luz es la de las velas, que tiembla sobre la piedra.
# =====================================================================

def _vitral(o):
    Wp, Hp = W * ESC, H * ESC
    yy, xx = np.mgrid[0:Hp, 0:Wp].astype(np.float32)
    xd, yd = (xx + 0.5) / ESC, (yy + 0.5) / ESC
    img = np.zeros((Hp, Wp, 3), np.float32)
    if not o:
        piedra, junta_p = np.array(hx("8E8676"), np.float32), np.array(hx("6E675B"), np.float32)
    else:
        piedra, junta_p = np.array(hx("232842"), np.float32), np.array(hx("181C30"), np.float32)
    rr = np.asarray(Image.fromarray((np.random.default_rng(7).random((Hp // 9 + 2, Wp // 9 + 2)) * 255).astype(np.uint8)).resize((Wp, Hp), Image.BICUBIC), np.float32) / 255
    img[:] = piedra * (0.94 + 0.1 * rr[..., None])
    fila = np.floor(yd / 10)
    img[((yd % 10) < 0.5) | (((xd + (fila % 2) * 9) % 18) < 0.5)] = junta_p
    na = 8; Wa = W / na
    ai = np.floor(xd / Wa)
    x0, x1 = ai * Wa + 3.4, (ai + 1) * Wa - 3.4
    w = x1 - x0
    spring, apex, bottom = 22.0, 3.5, 69.0
    kk = (0.866 * w) / (spring - apex)
    yv = spring - (spring - yd) * kk
    dentro_x = (xd >= x0) & (xd <= x1)
    rect_ = dentro_x & (yd >= spring) & (yd <= bottom)
    arco = dentro_x & (yd < spring) & (yd >= apex) & ((xd - x1) ** 2 + (yv - spring) ** 2 <= w ** 2) & ((xd - x0) ** 2 + (yv - spring) ** 2 <= w ** 2)
    vent = rect_ | arco
    lado = 5.0
    a, b = (xd + yd) / lado, (xd - yd) / lado
    u, v_ = np.floor(a), np.floor(b)
    plomo = ((a - u) < 0.09) | ((b - v_) < 0.09)
    hsh = ((u * 92821 + v_ * 68917 + ai * 1237) % 97 + 97) % 97
    pal = [(45, hx("3F64D8")), (60, hx("26306E")), (66, hx("5C8DFF")), (76, hx("B3122E")), (86, hx("E2B34A")), (92, hx("2E8A6E")), (97, hx("6E4BC2"))]
    vidrio = np.zeros_like(img)
    lim_ant = 0
    for lim, col in pal:
        m = (hsh >= lim_ant) & (hsh < lim)
        vidrio[m] = col; lim_ant = lim
    cx = (x0 + x1) / 2; cy = 34.0
    dist = np.hypot(xd - cx, yd - cy); ang = np.arctan2(yd - cy, xd - cx)
    ros = dist <= 9.0
    sector = np.floor((ang + np.pi) / (np.pi / 4)) % 2
    vidrio[ros & (sector == 0)] = hx("B3122E"); vidrio[ros & (sector == 1)] = hx("E2B34A")
    vidrio[dist <= 3.2] = hx("5C8DFF")
    plomo = np.where(ros, (np.abs(dist - 9.0) < 0.45) | (np.abs(dist - 3.2) < 0.4) | ((np.abs(((ang + np.pi) % (np.pi / 4))) < 0.05) & (dist > 3.2)), plomo)
    gris = vidrio.mean(axis=2, keepdims=True)
    if not o:
        # El sol detrás: vidrio vivo, más claro arriba, con piezas que destellan.
        luz_ = 0.95 + 0.3 * (1 - yd / 70) + ((hsh % 7) - 3) * 0.035
        vidrio = (vidrio * 0.9 + gris * 0.1) * luz_[..., None] + 18
    else:
        # Sin sol: el vidrio casi negro; la luna, desde arriba a la derecha, azulea las lancetas de ese lado.
        luna = np.clip(1 - np.hypot(xd - 330, yd + 10) / 190, 0, 1)
        vidrio = (vidrio * 0.45 + gris * 0.55) * (0.22 + 0.32 * luna)[..., None] + np.array(hx("1A2356"), np.float32) * 0.25 * luna[..., None]
    vidrio[plomo] = hx("0B0D16")
    img[vent] = vidrio[vent]
    borde = vent & ~(np.roll(vent, 2, 0) & np.roll(vent, -2, 0) & np.roll(vent, 2, 1) & np.roll(vent, -2, 1))
    img[borde] = hx("0B0D16")
    sill = yd >= 71
    if not o:
        img[sill] = hx("A39A88"); img[(yd >= 71) & (yd < 72)] = hx("C9C0AC"); img[sill & ((xd % 24) < 0.4)] = hx("7E7666")
        # Manchas de color en el alféizar: la luz de cada lanceta, un poco corrida hacia la derecha (el sol viene de la izquierda).
        for k in range(na):
            m = (ai == k) & vent & (yd > 40)
            prom = vidrio[m].mean(axis=0) if m.any() else np.zeros(3)
            zona = sill & (yd >= 72) & (xd >= k * Wa + 6 + (yd - 72) * 1.2) & (xd <= (k + 1) * Wa - 4 + (yd - 72) * 1.2)
            img[zona] = img[zona] * 0.6 + prom * 0.55
    else:
        img[sill] = hx("2C3250"); img[(yd >= 71) & (yd < 72)] = hx("454C74"); img[sill & ((xd % 24) < 0.4)] = hx("1E2238")
    base = Image.fromarray(np.clip(img, 0, 255).astype(np.uint8))
    brillo = Image.fromarray(np.clip(vidrio * vent[..., None], 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(5))
    base = ImageChops.add(base, ImageChops.multiply(brillo, Image.new("RGB", base.size, (60, 60, 60) if not o else (25, 25, 25))))
    return base, Wa


def fondo_gotico(t):
    o = t["oscuro"]
    base, Wa = _vitral(o)
    velas = [(round(Wa * 2), 69), (round(Wa * 6), 69)]
    d = ImageDraw.Draw(base)
    halos = []
    if o:
        # Un halo fijo grande y, encima, un brillo chico que tiembla (dos estados): así cambia poco de un cuadro a otro.
        a = np.asarray(base, np.float32)
        yy, xx = np.mgrid[0:H * ESC, 0:W * ESC].astype(np.float32)

        def halo(r, f):
            capa = np.zeros_like(a)
            for vx, vy in velas:
                dd_ = np.hypot(xx - vx * ESC, (yy - (vy - 11) * ESC) * 1.15) / (r * ESC)
                capa += (np.clip(1 - dd_, 0, 1) ** 1.6)[..., None] * np.array([255, 170, 80], np.float32) * f
            return capa
        a = a + halo(48, 0.55)
        base = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8))
        halos = [None, halo(13, 0.22)]
    for vx, vy in velas:                                   # candelero de hierro y vela de cera
        cuad(d, vx - 4, vy, hx("2A2218"), 9, 2); cuad(d, vx - 1, vy - 2, hx("3A3022"), 3, 2)
        cuad(d, vx - 3, vy - 3, hx("4A3E2A"), 7, 1)
        cuad(d, vx - 1, vy - 11, hx("EFE6CF") if not o else hx("D9C9A2"), 3, 8); cuad(d, vx - 1, vy - 11, hx("FFF8E6") if not o else hx("F0E2BC"), 1, 8)
        cuad(d, vx, vy - 12, hx("2A2218"), 1, 1)
    base0 = base
    rnd = random.Random(31)
    motas = [(rnd.uniform(0, W), rnd.uniform(44, 70), rnd.uniform(0, 6.28), rnd.choice((1, 2))) for _ in range(22)]
    P = 96

    def vivo(img, tick, delante):
        k = tick % P
        dd = ImageDraw.Draw(img)
        if not delante:
            if o and (k // 3 + (k // 7) % 2) % 3 == 0:
                a = np.asarray(img, np.float32)
                img.paste(Image.fromarray(np.clip(a + halos[1], 0, 255).astype(np.uint8)))
            for j, (vx, vy) in enumerate(velas):
                llama(dd, vx + 0.5, vy - 12, k + j * 5, 0.75)
            return
        if not o:                                          # polvo que flota en la luz
            for j, (x0, y0, f, tam) in enumerate(motas):
                fase = 2 * math.pi * k / P
                x = round((x0 + 3 * math.sin(fase + f)) % W)
                y = round(y0 - 2.5 * math.sin(fase * 2 + f * 1.7))
                if (j + k // 12) % 5 == 0: continue
                cuad(dd, x, y, hx("FFF6D8") if tam == 2 else hx("F2E6C0"))
    ANIM[("gotico", o)] = [vivo]
    return base0


g8.FONDOS["gotico"] = fondo_gotico


# =====================================================================
#  Medieval: la página del scriptorium. De día entra un rayo de sol en diagonal con polvo que flota, y una vela arde en
#  el margen; en el margen de abajo, un caracol (las «drôleries» de los manuscritos) avanza por la vid. De noche la página
#  está a oscuras y solo la alumbra la vela: su luz tiembla sobre todo, también sobre el gato.
# =====================================================================

def caracol(d, x, y, k, pal):
    concha, espiral, cuerpo, tinta = pal
    d.rectangle([x - 4, y - 1, x + 3, y], fill=cuerpo)                      # pie
    d.ellipse([x - 3, y - 6, x + 2, y - 1], fill=concha, outline=tinta)       # concha
    d.point((x - 1, y - 4), fill=espiral); d.point((x, y - 3), fill=espiral); d.point((x - 1, y - 3), fill=espiral)
    d.line([x + 3, y - 1, x + 4, y - 4 + (k % 2)], fill=cuerpo); d.point((x + 4, y - 5 + (k % 2)), fill=tinta)
    d.line([x + 3, y - 1, x + 5, y - 3], fill=cuerpo); d.point((x + 5, y - 4), fill=tinta)


def fondo_medieval(t):
    o = t["oscuro"]
    pagina = g8.fondo_medieval(t)                          # la página de gato8 (pergamino, inicial, renglones, vid)
    a = np.asarray(pagina, np.float32)
    Hp, Wp = a.shape[:2]
    yy, xx = np.mgrid[0:Hp, 0:Wp].astype(np.float32)
    xd, yd = xx / ESC, yy / ESC
    vela = (394, 72)
    if not o:
        # Rayo de sol: una franja diagonal más clara y cálida, con bordes suaves.
        u = xd - 0.9 * (H - yd)
        f = np.clip(1 - np.abs(u - 118) / 46, 0, 1) ** 1.2 * np.clip(yd / 14, 0, 1)
        a = a * (1 + 0.1 * f[..., None]) + np.array([14, 10, 0], np.float32) * f[..., None]
    else:
        # Oscuridad: el pergamino toma el pardo de la noche (medievalOscuro 15100A) lejos de la vela.
        dist = np.hypot(xd - vela[0], (yd - vela[1] + 10) * 1.5)
        g = np.clip(1 - dist / 330, 0, 1) ** 1.3
        noche = np.array(hx("15100A"), np.float32)
        a = noche + (a - noche) * (0.3 + 0.7 * g)[..., None]
    base = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8))
    d = ImageDraw.Draw(base)
    vx, vy = vela
    cuad(d, vx - 6, vy, hx("6B4A1E"), 13, 2); cuad(d, vx - 5, vy - 1, hx("A97C1E"), 11, 1)       # palmatoria de latón
    cuad(d, vx - 1, vy - 3, hx("A97C1E"), 3, 2)
    cuad(d, vx - 2, vy - 15, hx("F3EAD6") if not o else hx("E8D8B2"), 5, 12)                   # vela de cera
    cuad(d, vx - 2, vy - 15, hx("FFFBF0") if not o else hx("F5E8C6"), 2, 12)
    cuad(d, vx - 1, vy - 16, hx("E8D8B2"), 3, 1); cuad(d, vx, vy - 17, hx("2A1B10"), 1, 1)
    pal = (hx("C9A04A"), hx("6B4A1E"), hx("B5A27A"), hx("3A2A1E")) if not o else (hx("8A6A2E"), hx("3A2A12"), hx("6E6046"), hx("1A120A"))
    mascaras = []
    if o:
        for r in (190, 205, 180, 198):
            dist = np.hypot(xx - vx * ESC, (yy - (vy - 14) * ESC) * 1.5)
            f = np.clip(1.12 - (dist / (r * ESC)) ** 1.3, 0.74, 1.0)
            mascaras.append(Image.fromarray(np.stack([f * 255, f * 240, f * 214], -1).astype(np.uint8)))
    rnd = random.Random(12)
    motas = [(rnd.uniform(70, 230), rnd.uniform(10, 70), rnd.uniform(0, 6.28)) for _ in range(26)]
    P = 192

    def vivo(img, tick, delante):
        k = tick % P
        dd = ImageDraw.Draw(img)
        if not delante:
            # El caracol: avanza 12 dp por la vid y vuelve (a medio camino se esconde un momento en su concha).
            ida = k / (P / 2) if k < P / 2 else 2 - k / (P / 2)
            x = round(150 + 12 * ida); y = round(75.8 + 1.3 * math.sin(x / 7.5)) - 1
            parche = Image.new("RGBA", (12, 9), (0, 0, 0, 0)); dp_ = ImageDraw.Draw(parche)
            caracol(dp_, 6, 8, k // 8, pal)
            if k >= P / 2: parche = parche.transpose(Image.FLIP_LEFT_RIGHT)
            parche = dp_a_px(parche)
            img.paste(parche, ((x - 6) * ESC, (y - 8) * ESC), parche)
            return
        if not o:
            for j, (x0, y0, f) in enumerate(motas):
                fase = 2 * math.pi * k / P
                x = round(x0 + 4 * math.sin(fase + f)); y = round(y0 - 3 * math.sin(2 * fase + f * 1.3))
                if (j + k // 16) % 4 == 0: continue
                cuad(dd, x, y, hx("FFFFFF") if j % 3 else hx("E8C870"))
        else:
            img.paste(ImageChops.multiply(img, mascaras[(tick // 2 + (tick // 7) % 3) % len(mascaras)]))
        llama(dd, vx + 0.5, vy - 16, k, 0.8)
    ANIM[("medieval", o)] = [vivo]
    return base


g8.FONDOS["medieval"] = fondo_medieval


# =====================================================================
#  Orden: la sala de la Orden. Mamparos de acero remachado, pilares con luces de estado, una viga de la que cuelgan los
#  estandartes carmesí con el engrane de latón (se mecen con el aire de la ventilación) y el lema en una placa.
#  De noche, la sala en penumbra: lámparas rojas en los pilares y los estandartes iluminados desde abajo.
# =====================================================================

def estandarte(o):
    """El estandarte a 1 px por dp (RGBA, 22×48): paño con orla, engrane de latón con espada y cola de golondrina."""
    carm, carm2, carm3 = (hx("9E1B22"), hx("7A1219"), hx("B8333A")) if not o else (hx("A8222B"), hx("6E1218"), hx("D0333B"))
    laton, laton2 = (hx("B8922E"), hx("7E6219")) if not o else (hx("D6B25A"), hx("8A6A20"))
    im = Image.new("RGBA", (22, 48), (0, 0, 0, 0)); d = ImageDraw.Draw(im)
    d.rectangle([1, 2, 20, 40], fill=carm)
    d.polygon([(1, 40), (10.5, 34), (20, 40), (20, 46), (10.5, 41), (1, 46)], fill=carm)
    d.line([1, 2, 1, 45], fill=carm2); d.line([20, 2, 20, 45], fill=carm3)
    d.line([3, 4, 3, 38], fill=laton2); d.line([18, 4, 18, 38], fill=laton2)                 # orla
    d.rectangle([0, 0, 21, 2], fill=laton2); d.line([0, 1, 21, 1], fill=laton)               # travesaño
    for k in range(8):
        an = 2 * math.pi * k / 8
        x, y = 10.5 + 6 * math.cos(an), 17 + 6 * math.sin(an)
        d.rectangle([x - 1, y - 1, x + 1, y + 1], fill=laton)
    d.ellipse([5, 12, 16, 22], fill=laton); d.ellipse([7, 14, 14, 20], fill=carm2)
    d.line([10.5, 9, 10.5, 29], fill=hx("DDE2E7") if o else hx("E2E5E8"))                    # espada
    d.line([7, 24, 14, 24], fill=laton); d.point((10.5, 26), fill=laton2)
    for x in range(4, 18, 3): d.point((x, 32), fill=laton)
    return im


def ondear(im, k, periodo, amp=1.6, desde=10):
    """El paño se mece: cada renglón se corre de lado, más cuanto más abajo (ondas que bajan)."""
    out = Image.new("RGBA", im.size, (0, 0, 0, 0))
    for y in range(im.height):
        f = max(0, (y - desde) / (im.height - desde))
        dx = round(amp * f * math.sin(2 * math.pi * k / periodo - y * 0.18))
        out.paste(im.crop((0, y, im.width, y + 1)), (dx, y))
    return out


def fondo_orden(t):
    o = t["oscuro"]
    if not o:
        pared = [hx("C8CDD3"), hx("D3D7DC"), hx("DDE1E5"), hx("E6E9EC")]
        acero = [hx("2A2F35"), hx("4B525A"), hx("7A828B"), hx("AEB4BB"), hx("CBD0D6"), hx("E2E5E8")]
        tinta, laton = hx("15181B"), hx("B8922E")
    else:
        pared = [hx("0C0E10"), hx("101215"), hx("14171A"), hx("171A1E")]
        acero = [hx("08090B"), hx("171A1E"), hx("22262B"), hx("3A4048"), hx("4E5560"), hx("626A75")]
        tinta, laton = hx("050607"), hx("D6B25A")
    im, d = lienzo(pared[1])
    degradado(im, 6, 72, pared[::-1] if not o else pared)
    for x0 in range(8, W, 34):                                       # placas remachadas del mamparo
        for y0 in (8, 40):
            d.rectangle([x0, y0, x0 + 33, y0 + 31], outline=mezcla(pared[1], acero[1], 0.35))
            for (x, y) in ((x0 + 2, y0 + 2), (x0 + 31, y0 + 2), (x0 + 2, y0 + 29), (x0 + 31, y0 + 29)):
                d.point((x, y), fill=acero[1]); d.point((x - 1, y - 1), fill=acero[4] if not o else acero[3])
    d.rectangle([0, 0, W, 6], fill=acero[1]); d.line([0, 6, W, 6], fill=acero[0]); d.line([0, 1, W, 1], fill=acero[2])
    for x in range(3, W, 8): d.point((x, 3), fill=acero[3])
    d.rectangle([0, 9, W, 10], fill=acero[2]); d.line([0, 9, W, 9], fill=acero[3])            # tubería
    for x in range(20, W, 46): d.rectangle([x, 8, x + 2, 11], fill=acero[1])
    pilares = [0, 103, 206, 309, W - 7]
    leds = []
    for px_ in pilares:
        columna(im, px_ - 4, 6, 74, 11, acero[1:5], estrias=False)
        d.rectangle([px_ - 5, 6, px_ + 7, 8], fill=acero[2]); d.rectangle([px_ - 5, 70, px_ + 7, 73], fill=acero[2])
        d.rectangle([px_ - 2, 30, px_ + 4, 42], fill=acero[0])                               # panel de estado
        for j in range(3):
            leds.append((px_ - 1 + j * 2, 33, j)); leds.append((px_ - 1 + j * 2, 36, j + 3))
        d.line([px_ - 1, 39, px_ + 3, 39], fill=laton)
        if o:
            luz(im, px_ + 1, 20, 22, hx("D0333B"), 0.22, 4, ry=16)                           # lámpara roja
    if o:
        for px_ in pilares:
            d.rectangle([px_ - 1, 17, px_ + 3, 21], fill=hx("FF6A6A")); d.line([px_ - 1, 17, px_ + 3, 17], fill=hx("FFC0C0"))
    centros = [51, 154, 257, 360]
    placa = (128, 56, 284, 64)
    d.rectangle(placa, fill=laton if not o else mezcla(laton, pared[0], 0.4))
    d.rectangle([placa[0] + 1, placa[1] + 1, placa[2] - 1, placa[3] - 1], outline=mezcla(laton, tinta, 0.4))
    lema = "ORDO . SCIENTIA . CVSTODIA"
    texto(d, (placa[0] + placa[2]) // 2 - len(lema) * 2, placa[1] + 2, lema, tinta if not o else hx("1A1408"))
    d.line([0, 74, W, 74], fill=acero[0])
    for y in range(75, 80):                                          # piso de rejilla
        for x in range(W):
            d.point((x, y), fill=acero[2] if (x % 3 == 0 or y % 2 == 0) else acero[0])
    est = estandarte(o)
    P = 32
    vivas = [(hx("5FD068"), hx("1E3A22")), (hx("E8C14A"), hx("3A3014")), (hx("E05050"), hx("3A1414"))]

    def cuadro_dp(k):
        f = im.copy(); dd = ImageDraw.Draw(f)
        e = ondear(est, k, P)
        for cx in centros:
            f.paste(e, (cx - 11, 7), e)
        for (x, y, j) in leds:
            enc = (j * 5 + k // 4 + x) % 7 not in (0, 3)
            c = vivas[j % 3]
            dd.point((x, y), fill=c[0] if enc else c[1])
        return f

    return animado("orden", o, cuadro_dp, P)


g8.FONDOS["orden"] = fondo_orden


# =====================================================================
#  Ruta de la Seda: la ciudad de la ruta (portal de azulejos y cúpula turquesa), los álamos, los montes nevados y las
#  dunas por donde pasa la caravana. En el campamento ondean banderines. De noche, la media luna, la ciudad con sus
#  luces y la caravana echada junto al fuego.
# =====================================================================

def camello(d, x, y, col, k=0, echado=False, carga=None):
    """Camello bactriano de perfil hacia la izquierda, a 1 px por dp (unos 13×9)."""
    if echado:
        d.rectangle([x - 4, y - 3, x + 4, y], fill=col)
        d.rectangle([x - 3, y - 5, x - 1, y - 3], fill=col); d.rectangle([x + 1, y - 5, x + 3, y - 3], fill=col)
        d.line([x - 4, y - 2, x - 6, y - 4], fill=col); d.rectangle([x - 8, y - 5, x - 6, y - 4], fill=col)
        return
    b = 1 if k % 4 in (1, 3) else 0
    d.rectangle([x - 4, y - 6 + b, x + 4, y - 4 + b], fill=col)
    d.rectangle([x - 3, y - 8 + b, x - 1, y - 6 + b], fill=col); d.rectangle([x + 1, y - 8 + b, x + 3, y - 6 + b], fill=col)
    if carga: d.rectangle([x - 1, y - 8 + b, x, y - 6 + b], fill=carga)
    d.line([x - 4, y - 5 + b, x - 6, y - 8 + b], fill=col); d.rectangle([x - 8, y - 9 + b, x - 6, y - 8 + b], fill=col)
    d.point((x + 5, y - 5 + b), fill=col)
    pasos = ((0, 0, 0, 0), (1, -1, -1, 1), (0, 0, 0, 0), (-1, 1, 1, -1))[k % 4]
    for lx, s in zip((-3, -2, 2, 3), pasos):
        d.line([x + lx, y - 3 + b, x + lx + s, y], fill=col)


def fondo_seda(t):
    o = t["oscuro"]
    if not o:
        cielo = [hx("9CC3D6"), hx("BBD5DE"), hx("D9E4DF"), hx("EFEBDC"), hx("F5EEDC")]
        monte, monte2, nieve = hx("A7AFC4"), hx("8E97B0"), hx("F4F4F0")
        duna = [(hx("E8D2A0"), hx("F2E2B8")), (hx("D9BC84"), hx("E6CC96")), (hx("CBA86E"), hx("D9BC84"))]
        muro, muro2, sombra_m = hx("D8B884"), hx("C9A46A"), hx("A8844E")
        turq, turq2, azul, oro = hx("2FA3A3"), hx("7FD0C8"), hx("23408E"), hx("E0B04F")
        alamo, alamo2, ventana, palo = hx("5E7A3A"), hx("7E9A4A"), hx("3A2E22"), hx("6A4A2A")
        cam = hx("8A6A44")
        colb = [hx("C0392B"), hx("E0B04F"), hx("23408E"), hx("1A8C8C"), hx("F5EEDC")]
    else:
        cielo = [hx("070A16"), hx("0B1020"), hx("0F1426"), hx("161D35"), hx("1F2846")]
        monte, monte2, nieve = hx("1A2240"), hx("141B34"), hx("4A5680")
        duna = [(hx("1E2238"), hx("262B44")), (hx("181C30"), hx("20253C")), (hx("14182A"), hx("1A1F34"))]
        muro, muro2, sombra_m = hx("3A3A52"), hx("30304A"), hx("24243A")
        turq, turq2, azul, oro = hx("2A6E78"), hx("4FC3C3"), hx("2B3A6E"), hx("E0B04F")
        alamo, alamo2, ventana, palo = hx("141C24"), hx("1A2430"), hx("F2C14E"), hx("2A1E14")
        cam = hx("3A2C26")
        colb = [hx("8A2E26"), hx("A88238"), hx("2B3A6E"), hx("2A6E78"), hx("8A8670")]
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 44, cielo)
    if o:
        estrellas(im, 41, 120, 0, 34, [hx("F2EAD3"), hx("B9B29F"), hx("E0B04F"), hx("8A93C0")])
        creciente(im, 305, 13, 5, hx("F2EAD3"), 3)
    else:
        nube(d, 150, 12, 30, hx("FFFFFF"), hx("F2F2EE"), hx("DCDCD6"))
        nube(d, 330, 8, 24, hx("FFFFFF"), hx("F2F2EE"), hx("DCDCD6"))
    picos = [(150, 22), (196, 27), (238, 20), (290, 26), (340, 21), (392, 25)]
    px = im.load()
    for x in range(118, W):
        top = min(p[1] + abs(x - p[0]) * 0.6 for p in picos)
        pico = min(picos, key=lambda p: p[1] + abs(x - p[0]) * 0.6)
        for y in range(round(top), 46):
            sol = x <= pico[0]
            if y - pico[1] < 4 and y < 30: px[x, y] = nieve if sol else mezcla(nieve, monte2, 0.35)
            else: px[x, y] = monte if sol else monte2
    crestas = []
    for k, (base_, amps) in enumerate(((46, ((2, 80), (1.5, 30))), (56, ((3, 110), (1.5, 45))), (66, ((2.5, 140), (1, 50))))):
        ys = ondas(31 + k, base_, amps); crestas.append(ys)
        perfil(im, ys, duna[k][0], borde=duna[k][1])
    # La ciudad: muralla de adobe, portal (pishtaq) con arco apuntado y azulejos, cúpula turquesa estriada y alminar.
    d.rectangle([4, 44, 112, 66], fill=muro); d.line([4, 44, 112, 44], fill=muro2)
    for x in range(6, 112, 6): d.rectangle([x, 42, x + 2, 44], fill=muro)
    d.rectangle([70, 30, 104, 66], fill=muro); d.rectangle([98, 30, 104, 66], fill=sombra_m)
    d.ellipse([74, 16, 100, 42], fill=turq)
    for x in range(76, 100, 3): d.line([x, 20 + abs(x - 87) // 3, x, 30], fill=turq2)
    d.rectangle([86, 13, 88, 16], fill=oro); d.rectangle([70, 29, 104, 31], fill=azul)
    d.rectangle([28, 24, 60, 66], fill=muro2); d.rectangle([30, 26, 58, 66], fill=azul)       # pishtaq
    d.rectangle([33, 29, 55, 66], fill=turq)
    d.polygon([(36, 66), (36, 40), (44, 32), (52, 40), (52, 66)], fill=ventana if not o else hx("8A5A1E"))
    for y in range(28, 66, 4): d.point((31, y), fill=oro); d.point((57, y), fill=oro)
    for x in range(34, 56, 3): d.point((x, 30), fill=oro if not o else turq2)
    d.rectangle([12, 18, 17, 66], fill=muro2); d.rectangle([11, 16, 18, 18], fill=turq)       # alminar
    for y in range(22, 60, 7): d.line([12, y, 17, y], fill=azul)
    for wx, wy in ((76, 50), (90, 50), (22, 54)): d.rectangle([wx, wy, wx + 1, wy + 2], fill=ventana)
    if o:
        luz(im, 44, 56, 12, hx("F2A040"), 0.35, 3)
    for ax_ in (118, 124, 131):                                    # álamos
        d.ellipse([ax_ - 2, 34, ax_ + 2, 60], fill=alamo); d.ellipse([ax_ - 2, 36, ax_, 56], fill=alamo2)
        d.line([ax_, 58, ax_, 64], fill=sombra_m)
    # El suelo: camino de arena y la orla de monedas de antes.
    degradado(im, 68, 74, [duna[2][0], duna[2][1]])
    d.line([0, 74, W, 74], fill=hx("1F1B3A") if not o else hx("05060C"))
    d.rectangle([0, 75, W, 80], fill=hx("23408E") if not o else hx("1F2846"))
    for x in range(5, W, 9):
        d.ellipse([x - 2, 75, x + 2, 79], fill=hx("E0B04F") if not o else hx("A88238")); d.point((x, 77), fill=hx("23408E") if not o else hx("1F2846"))
    # Campamento: tienda de fieltro y banderines entre dos varas.
    v1, v2, vy = 262, 404, 38
    for vx in (v1, v2):
        d.line([vx, vy, vx, 70], fill=palo); d.point((vx, vy - 1), fill=oro)
    cuerda = [(x, vy + 1 + 7 * math.sin(math.pi * (x - v1) / (v2 - v1))) for x in range(v1, v2 + 1)]
    d.line(cuerda, fill=palo if not o else hx("3A2E22"))
    banderas = [(x, round(vy + 1 + 7 * math.sin(math.pi * (x - v1) / (v2 - v1))) + 1) for x in range(v1 + 6, v2 - 3, 8)]
    d.polygon([(286, 70), (300, 56), (314, 70)], fill=hx("E6D6B8") if not o else hx("3A3A50"))
    d.polygon([(300, 56), (314, 70), (304, 70)], fill=hx("CDB894") if not o else hx("2E2E44"))
    d.polygon([(297, 70), (300, 63), (303, 70)], fill=hx("6A3A2A") if not o else hx("F2A040"))
    fuego = (340, 70)
    if o:
        luz(im, fuego[0], fuego[1], 30, hx("FF9A4A"), 0.3, 4, ry=6)
        luz(im, fuego[0], fuego[1] - 4, 10, hx("FFB45A"), 0.25, 3)
        d.ellipse([fuego[0] - 3, fuego[1] - 1, fuego[0] + 3, fuego[1] + 1], fill=hx("2A1C14"))
    caravana = [(214, 0), (228, 1), (242, 2), (256, 3)]
    P = 48

    def cuadro_dp(k):
        f = im.copy(); dd = ImageDraw.Draw(f)
        if not o:
            for j, (x, c) in enumerate(caravana):
                camello(dd, x, round(crestas[1][x]) + 1, cam, k // 3 + j, carga=(hx("C0392B"), hx("23408E"), hx("E0B04F"), hx("1A8C8C"))[c])
            hx_ = 202; hy_ = round(crestas[1][hx_]) + 1                                       # el que guía, a pie
            dd.line([hx_, hy_ - 5, hx_, hy_ - 2], fill=cam); dd.point((hx_, hy_ - 6), fill=cam)
            dd.line([hx_, hy_ - 2, hx_ - (k // 3) % 2, hy_], fill=cam); dd.line([hx_, hy_ - 2, hx_ + (k // 3 + 1) % 2, hy_], fill=cam)
        else:
            for x in (326, 356, 370):
                camello(dd, x, 71, cam, echado=True)
        for j, (x, y) in enumerate(banderas):                     # banderines que ondean
            ph = (k // 4 + j) % 4
            dd.polygon([(x - 2, y), (x + 2, y), (x + (0, 1, 0, -1)[ph], y + 5 - (ph % 2))], fill=colb[j % len(colb)])
        if o:
            rnd_ = random.Random(k // 3)
            for _ in range(3):                                    # chispas
                dd.point((fuego[0] + rnd_.randint(-2, 2), fuego[1] - rnd_.randint(6, 12)), fill=hx("FFC24A"))
        return f

    def delante(img, k):
        if o:
            llama(ImageDraw.Draw(img), fuego[0], fuego[1], k, 0.9)
    return animado("seda", o, cuadro_dp, P, delante=delante if o else None)


g8.FONDOS["seda"] = fondo_seda


# =====================================================================
#  Al-Ándalus: el patio. Arcos de herradura con dovelas rojas y blancas (como en Córdoba) sobre columnas finas, yesería
#  de sebka encima, el zócalo de alicatado abajo y, por los arcos, el jardín con cipreses y naranjos. Al centro, una fuente
#  de taza cuyo chorro cae y rebosa. De noche: estrellas y luna por los arcos, faroles encendidos y el agua que brilla.
# =====================================================================

def fondo_andalus(t):
    o = t["oscuro"]
    if not o:
        yeso, yeso2, sebka = hx("F3EBDA"), hx("E6DAC0"), hx("C9B48E")
        cielo = [hx("8FBCDD"), hx("B5D3E8"), hx("D9E8EF")]
        verde, azul, almagre, oro, tinta, blanco = hx("1E6B5C"), hx("2A4F9E"), hx("B5432A"), hx("E0B65A"), hx("2A1A12"), hx("FBF6EA")
        ciprés, ciprés2, naranjo, naranja, seto = hx("2F5A3A"), hx("3F7046"), hx("4E8A46"), hx("F08A1E"), hx("5E8A3A")
        agua = [hx("5FA3CF"), hx("A9D3EA"), hx("FFFFFF")]
        marmol, marmol2 = hx("F2EEE6"), hx("CFC6B4")
    else:
        yeso, yeso2, sebka = hx("18302D"), hx("132724"), hx("2B4A45")
        cielo = [hx("050C0C"), hx("08120F"), hx("0B1716")]
        verde, azul, almagre, oro, tinta, blanco = hx("1A5A4C"), hx("223E7A"), hx("7A2E1E"), hx("E0B65A"), hx("050A09"), hx("8A9A90")
        ciprés, ciprés2, naranjo, naranja, seto = hx("0A1A14"), hx("0E2018"), hx("10261C"), hx("A0601A"), hx("0E2018")
        agua = [hx("2A5A6A"), hx("6FA8B8"), hx("E8F4F0")]
        marmol, marmol2 = hx("4A5A54"), hx("2E3C38")
    im, d = lienzo(yeso)
    px = im.load()
    for y in range(0, 60):                                         # yesería: red de rombos
        for x in range(W):
            if (x + y) % 10 == 0 or (x - y) % 10 == 0: px[x, y] = sebka
    d.rectangle([0, 0, W, 4], fill=hx("6A4A2A") if not o else hx("1A120C"))   # alero de madera tallada
    for x in range(1, W, 4): d.rectangle([x, 4, x + 1, 6], fill=hx("4A321C") if not o else hx("120C08"))
    d.line([0, 7, W, 7], fill=oro)
    centros = [41, 123, 206, 289, 371]
    sp, r = 39, 23
    for cx in centros:
        d.rectangle([cx - r - 9, 9, cx + r + 9, 60], outline=oro)                            # alfiz
        d.rectangle([cx - r - 8, 10, cx + r + 8, 59], fill=yeso2)
        for y in range(10, 60):
            for x in range(cx - r - 8, cx + r + 9):
                if (x + y) % 6 == 0 and (x - y) % 6 == 0: px[x, y] = sebka
        for a in range(-200, 21, 1):                                                         # dovelas
            an = math.radians(a)
            sector = int((a + 200) // 18) % 2
            for rr in np.arange(r, r + 6.5, 0.5):
                x, y = cx + rr * math.cos(an), sp + rr * math.sin(an)
                if y <= sp + 7: px[round(x), round(y)] = almagre if sector else blanco
        # Hueco del arco: el jardín.
        for y in range(sp - r, 60):
            for x in range(cx - r, cx + r + 1):
                dentro = ((x - cx) ** 2 + (y - sp) ** 2 <= r * r) if y < sp + 7 else (abs(x - cx) <= r - 3)
                if y >= sp + 7 and abs(x - cx) > r - 3: dentro = False
                if dentro:
                    f = (y - (sp - r)) / (60 - (sp - r))
                    px[x, y] = cielo[min(2, int(f * 3))]
        if o:
            estrellas(im, cx, 8, sp - r + 2, sp + 4, [hx("F3EAD6"), hx("8A9A90")], x0=cx - r + 4, x1=cx + r - 4,
                      evitar=lambda x, y: (x - cx) ** 2 + (y - sp) ** 2 > (r - 2) ** 2)
        for k_, (dx, h) in enumerate(((-16, 26), (14, 22), (-4, 18))):                        # cipreses
            d.ellipse([cx + dx - 2, 60 - h, cx + dx + 2, 60], fill=ciprés); d.ellipse([cx + dx - 2, 60 - h + 2, cx + dx, 58], fill=ciprés2)
        d.ellipse([cx + 2, 44, cx + 12, 54], fill=naranjo)                                   # naranjo
        for (ox, oy) in ((4, 47), (9, 46), (7, 51)): px[cx + ox, oy] = naranja
        d.rectangle([cx - r, 54, cx + r - 1, 59], fill=seto)
    if o:
        creciente(im, centros[4] - 8, 24, 5, hx("F3EAD6"), -3)
    for cx in centros:                                                                       # columnillas de los arcos
        for m in (cx - r - 3, cx + r + 3):
            d.rectangle([m - 2, sp + 7, m + 2, 60], fill=marmol); d.line([m + 1, sp + 7, m + 1, 60], fill=marmol2)
            d.rectangle([m - 3, sp + 5, m + 3, sp + 7], fill=oro)
    d.rectangle([0, 60, W, 61], fill=oro)
    for x in range(2, W, 6): px[x, 60] = tinta
    for y in range(62, 74):                                                                  # zócalo de alicatado
        for x in range(W):
            u, v_ = x % 12 - 5.5, (y - 62) % 12 - 5.5
            tile = (x // 12) % 2
            den = abs(u) + abs(v_) <= 6.2 or (abs(u) <= 4.2 and abs(v_) <= 4.2)
            bor = den and not (abs(u) + abs(v_) <= 5.0 or (abs(u) <= 3.2 and abs(v_) <= 3.2))
            px[x, y] = blanco if bor else ((verde if tile else azul) if den else (almagre if (x + y) % 2 else oro))
    d.line([0, 74, W, 74], fill=tinta)
    for y in range(75, 80):
        for x in range(W):
            px[x, y] = blanco if x % 6 == 0 or y == 77 else mezcla(almagre, yeso if not o else hx("2B4A45"), 0.35)
    # La fuente de taza, al centro.
    fx = 206
    d.polygon([(fx - 22, 73), (fx + 22, 73), (fx + 20, 66), (fx - 20, 66)], fill=marmol)
    d.line([fx - 20, 66, fx + 20, 66], fill=marmol2); d.line([fx - 21, 69, fx + 21, 69], fill=verde)
    for x in range(fx - 20, fx + 21, 4): px[x, 69] = oro
    d.rectangle([fx - 19, 66, fx + 19, 67], fill=agua[0])
    d.rectangle([fx - 2, 56, fx + 2, 66], fill=marmol); d.line([fx + 1, 56, fx + 1, 66], fill=marmol2)
    d.chord([fx - 11, 48, fx + 11, 60], 0, 180, fill=marmol); d.line([fx - 11, 54, fx + 11, 54], fill=marmol2)
    d.line([fx - 10, 54, fx + 10, 54], fill=agua[0])
    faroles = [(centros[1], 22), (centros[3], 22)]
    P = 48

    def cuadro_dp(k):
        f = im.copy(); dd = ImageDraw.Draw(f); fp = f.load()
        for y in range(40, 54):                                    # el chorro
            fp[fx, y] = agua[2] if (y + k // 2) % 4 == 0 else agua[1]
        for sx in (-1, 1):
            for q in range(10):
                x = fx + sx * (1 + q * 0.7); y = 40 - 2 * math.sin(math.pi * min(1, q / 4) / 2) + max(0, q - 3) ** 1.6 * 0.6
                if y < 54: fp[round(x), round(y)] = agua[2] if (q - k // 2) % 4 == 0 else agua[1]
            for y in range(55, 66):                                # rebosa por el borde de la taza
                x = fx + sx * (11 + (y - 55) * 0.25)
                fp[round(x), y] = agua[2] if (y - k // 2) % 4 == 0 else agua[0]
        for x in range(fx - 18, fx + 19):                          # ondas en el estanque
            if (x + k // 3) % 7 == 0: fp[x, 66] = agua[2]
            elif (x + k // 3) % 7 == 3: fp[x, 66] = agua[1]
        if o:
            for j, (lx, ly) in enumerate(faroles):
                dd.line([lx, 9, lx, ly - 3], fill=hx("6A4A2A"))
                dd.rectangle([lx - 2, ly - 3, lx + 2, ly + 3], fill=oro)
                dd.rectangle([lx - 1, ly - 2, lx + 1, ly + 2], fill=hx("FFE7A0") if (k // 3 + j) % 5 else hx("F2C14E"))
                dd.point((lx, ly - 4), fill=oro)
        return f

    if o:
        for lx, ly in faroles:
            luz(im, lx, ly, 14, hx("F2B04A"), 0.28, 4)
    return animado("andalus", o, cuadro_dp, P)


g8.FONDOS["andalus"] = fondo_andalus


# =====================================================================
#  Botánico: un dechado de punto de cruz que es un herbario: «HERBARIVM» en hilo rojo, helechos, dedalera, hongo,
#  escarabajos y una mariposa clavados con alfiler, cada uno con su etiqueta. De una rama de roble caen hojas y por abajo
#  pasa una fila de hormigas. De noche, el bordado sobre lino oscuro: luna, estrellas de nudo, una polilla y luciérnagas.
# =====================================================================

def bordar(m, tela, hueco):
    """Cada pixel opaco de m (RGBA a 1 px por dp) es una cruz de hilo de 3×3 sobre la tela (como el bordado de gato8)."""
    a = np.asarray(m)
    out = np.empty((H, ESC, W, ESC, 3), np.uint8)
    out[:] = np.array(tela, np.uint8)
    out[:, 0, :, 0] = np.array(hueco, np.uint8)
    col = a[..., :3].astype(np.float32)
    osc = np.clip(col * 0.78, 0, 255).astype(np.uint8)
    hay = a[..., 3] > 0
    c8 = col.astype(np.uint8)
    for (i, j) in ((2, 0), (0, 2)):
        out[:, i, :, j][hay] = osc[hay]
    for i in range(3):
        out[:, i, :, i][hay] = c8[hay]
    return Image.fromarray(out.reshape(H * ESC, W * ESC, 3))


def hoja_roble(d, x, y, col, vena, giro=0):
    pts = ((0, 0), (1, 0), (-1, 1), (0, 1), (1, 1), (2, 1), (0, 2), (1, 2), (-1, 2)) if giro % 2 == 0 else \
          ((0, 0), (1, 0), (2, 0), (0, 1), (1, 1), (-1, 1), (1, 2), (0, 2), (2, 2))
    for dx, dy in pts:
        d.point((x + dx, y + dy), fill=col)
    d.point((x + 1 - giro % 2, y + 1), fill=vena)


def fondo_botanico(t):
    o = t["oscuro"]
    if not o:
        tela, hueco = hx("EFE8D6"), mezcla(hx("EFE8D6"), hx("BFAF8A"), 0.5)
        musgo, musgo2, oxido, teal, sepia, rojo = hx("3F5A2C"), hx("6E8A4A"), hx("8C4A22"), hx("2E5E5A"), hx("5E5340"), hx("8A1A1A")
        papel, flor, flor2, amarillo = hx("F7F2E4"), hx("B05A8A"), hx("D88AB4"), hx("D9A22E")
    else:
        tela, hueco = hx("14120D"), mezcla(hx("14120D"), hx("4A4231"), 0.5)
        musgo, musgo2, oxido, teal, sepia, rojo = hx("A9C27A"), hx("6E8A4A"), hx("E09A5E"), hx("6FC2B0"), hx("B8AC90"), hx("E06A5A")
        papel, flor, flor2, amarillo = hx("4A4231"), hx("C9A0D8"), hx("EDE4CF"), hx("F2D27A")
    m = Image.new("RGBA", (W, H), (0, 0, 0, 0)); dm = ImageDraw.Draw(m)
    A = lambda c: c + (255,)
    for x in range(W):                                             # orla de arriba: zigzag con florecitas
        dm.point((x, 2 + (1 if (x // 2) % 2 else 0)), fill=A(musgo))
    for x in range(6, W, 16):
        dm.point((x, 5), fill=A(rojo)); dm.point((x - 1, 6), fill=A(rojo)); dm.point((x + 1, 6), fill=A(rojo)); dm.point((x, 6), fill=A(amarillo))
    titulo = "HERBARIVM" if not o else "NOCTVRNA"
    tx = 206 - len(titulo) * 2
    texto(dm, tx, 9, titulo, A(rojo))

    def etiqueta(x, y, w=11):
        dm.rectangle([x, y, x + w, y + 4], fill=A(papel)); dm.rectangle([x, y, x + w, y + 4], outline=A(sepia))
        dm.line([x + 2, y + 2, x + w - 3, y + 2], fill=A(sepia))

    def helecho(x0, y0, alto=18):
        for k in range(alto):
            x, y = x0 + int(k * 0.35), y0 - k
            dm.point((x, y), fill=A(musgo))
            if k % 2 == 0 and k < alto - 1:
                for j in range(1, max(2, 5 - k // 4)):
                    dm.point((x - j, y + j // 2), fill=A(musgo if j < 3 else musgo2)); dm.point((x + j, y + j // 2), fill=A(musgo if j < 3 else musgo2))

    def escarabajo(x0, y0):
        patron(dm, x0 - 4, y0 - 5, ["s.......s", ".s.....s.", "..s.t.s..", "...ttt...", ".s.tTt.s.", "..ttttt..", "s.tTtTt.s",
                                     "..ttttt..", "s.ttrtt.s", "..ttttt..", "...ttt..."],
               dict(s=A(sepia), t=A(teal), T=A(mezcla(teal, (255, 255, 255), 0.45)), r=A(rojo)))

    def dedalera(x0, yb):
        dm.line([x0, yb, x0, yb - 36], fill=A(musgo))
        for k, y in enumerate(range(yb - 34, yb - 8, 3)):
            s = 1 if k % 2 else -1
            dm.point((x0 + s, y), fill=A(flor)); dm.point((x0 + 2 * s, y + 1), fill=A(flor)); dm.point((x0 + s, y + 1), fill=A(flor2))
            dm.point((x0 + 3 * s, y + 2), fill=A(flor)); dm.point((x0 + 2 * s, y + 2), fill=A(flor2))
        for s in (-1, 1):
            dm.line([x0, yb - 2, x0 + 5 * s, yb - 8], fill=A(musgo)); dm.line([x0, yb - 3, x0 + 4 * s, yb - 8], fill=A(musgo2))

    def hongo(x0, yb):
        dm.rectangle([x0 - 2, yb - 9, x0 + 1, yb], fill=A(hx("D2C29C") if not o else hx("B8AC90")))
        dm.line([x0 - 3, yb - 6, x0 + 2, yb - 6], fill=A(sepia))
        dm.chord([x0 - 9, yb - 20, x0 + 8, yb - 6], 180, 360, fill=A(rojo))
        for dx, dy in ((-5, -15), (0, -17), (4, -14), (-1, -13), (-7, -12), (6, -12)):
            dm.point((x0 + dx, yb + dy), fill=A(papel if not o else hx("EDE4CF")))

    def mariposa(x0, y0, c1=None, c2=None):
        patron(dm, x0 - 5, y0 - 4, [".oo.....oo.", "oyyo...oyyo", "oyooo.oooyo", ".ooooSoooo.", "..oooSooo..",
                                     "..yyoSoyy..", ".yyy.S.yyy.", "..y.....y.."],
               dict(o=A(c1 or oxido), y=A(c2 or amarillo), S=A(sepia)))

    for x in range(1, H - 6):                                      # orlas de los lados
        for x0 in (1, W - 3):
            if x % 4 < 2: dm.point((x0 + (x // 4) % 2, x), fill=A(musgo))
    if not o:
        helecho(16, 62, 28); etiqueta(10, 66)
        escarabajo(50, 30); etiqueta(44, 40, 12)
        dedalera(84, 64); etiqueta(78, 66)
        mariposa(126, 30); dm.point((126, 24), fill=A(sepia)); etiqueta(120, 40, 12)
        hongo(164, 64); etiqueta(158, 66)
        helecho(262, 62, 28); etiqueta(256, 66)
        escarabajo(296, 30); etiqueta(290, 40, 12)
        dedalera(330, 64); etiqueta(324, 66)
    else:
        creciente(m, 40, 26, 6, A(hx("EDE4CF")), 3)
        rnd = random.Random(9)
        for _ in range(30):
            x, y = rnd.randrange(6, W - 6), rnd.randrange(16, 44)
            if abs(x - 206) < 26 and y < 16: continue
            dm.point((x, y), fill=A(hx("EDE4CF") if rnd.random() < 0.6 else amarillo))
        helecho(16, 62, 28); helecho(262, 62, 28)
        for x0 in (84, 330):                                       # flores que abren de noche (dondiego)
            dm.line([x0, 64, x0, 46], fill=A(musgo))
            for an in range(0, 360, 45):
                dm.point((x0 + round(3 * math.cos(math.radians(an))), 43 + round(3 * math.sin(math.radians(an)))), fill=A(flor2))
                dm.point((x0 + round(2 * math.cos(math.radians(an))), 43 + round(2 * math.sin(math.radians(an)))), fill=A(flor2))
            dm.point((x0, 43), fill=A(amarillo))
            dm.line([x0, 56, x0 + 4, 52], fill=A(musgo2)); dm.line([x0, 60, x0 - 4, 56], fill=A(musgo2))
        hongo(164, 64)
        mariposa(126, 32, hx("8A7A9A"), hx("C9B8A0"))              # polilla
        patron(dm, 382, 4, [".b...b.", ".bbbbb.", "bWbbbWb", "bEbbbEb", ".bbnbb.", ".bWWWb.", ".bWWWb.", "..bbb..", "..f.f.."],
               dict(b=A(hx("8A7458")), W=A(hx("C9B8A0")), E=A(amarillo), n=A(oxido), f=A(amarillo)))   # lechuza en la rama
    # Rama de roble (de ella caen las hojas).
    rx, ry = 360, 14
    dm.line([W, ry - 2, rx, ry + 2], fill=A(sepia)); dm.line([rx + 14, ry, rx + 10, ry + 6], fill=A(sepia))
    for (hx_, hy_, g_) in ((rx + 2, ry + 3, 0), (rx + 9, ry + 6, 1), (rx + 18, ry - 1, 0), (rx + 26, ry + 1, 1), (rx + 36, ry - 3, 0)):
        hoja_roble(dm, hx_, hy_, musgo, musgo2, g_)
    dm.point((rx + 14, ry + 8), fill=A(oxido)); dm.point((rx + 14, ry + 7), fill=A(sepia))       # bellota
    dm.line([0, 74, W, 74], fill=A(sepia))
    for x in range(0, W, 2): dm.point((x, 79), fill=A(musgo2 if (x // 2) % 3 else musgo))
    P = 96
    caidas = [(rx + 6, ry + 8, 0), (rx + 22, ry + 4, 48)]
    rnd = random.Random(4)
    luciernagas = [(rnd.randrange(30, 400), rnd.randrange(40, 70), rnd.randrange(0, 24)) for _ in range(14)]

    def cuadro_dp(k):
        mm = m.copy(); d = ImageDraw.Draw(mm)
        for (x0, y0, desfase) in caidas:                           # hojas que caen meciéndose
            q = (k + desfase) % P
            if q < 80:
                y = y0 + q * 0.75
                x = x0 - q * 0.35 + 4 * math.sin(q * 0.16)
                if y < 71: hoja_roble(d, round(x), round(y), oxido if desfase else amarillo, musgo2, (q // 8) % 2)
        if not o:
            for x0 in range(-12, W, 12):                           # hilera de hormigas
                x = x0 + k // 8
                for dx in (0, 2, 4): d.point((x + dx, 77), fill=A(oxido))
                for dx in (1, 3):
                    pie = (k // 4 + dx) % 2
                    d.point((x + dx, 78), fill=A(sepia)); d.point((x + dx + (1 if pie else 0) - 1, 76), fill=A(sepia))
        else:
            for j, (x, y, f) in enumerate(luciernagas):            # luciérnagas que se encienden
                q = (k // 4 + f) % 24
                if q < 6:
                    d.point((x, y - q // 2), fill=A(hx("F2E86A") if q in (1, 2, 3) else hx("A9C27A")))
        return bordar(mm, tela, hueco)

    return animado("botanico", o, cuadro_dp, P, escala=False)


g8.FONDOS["botanico"] = fondo_botanico


# =====================================================================
#  Renacimiento: el cuaderno de Leonardo. Escritura en espejo, el hombre de Vitruvio (cuadrado y círculo), un engrane y,
#  nuevos, un molino de viento cuyas aspas giran y un estudio del vuelo de los pájaros. De noche, la misma página en papel
#  pardo oscuro dibujada con tiza: el estudio de la luna con su luz cenicienta, el molino y murciélagos.
# =====================================================================

def fondo_renacimiento(t):
    o = t["oscuro"]
    Wp, Hp, S = W * ESC, H * ESC, ESC
    if not o:
        papel_, tinta_, sang_ = hx("F0E4C6"), hx("3A2614"), hx("8C3B1E")
        sep, san, leve = mezcla(papel_, tinta_, 0.65), mezcla(papel_, sang_, 0.6), mezcla(papel_, tinta_, 0.3)
    else:
        papel_, tinta_, sang_ = hx("1A130C"), hx("EFE0BE"), hx("E08A5E")
        sep, san, leve = mezcla(papel_, tinta_, 0.62), mezcla(papel_, sang_, 0.55), mezcla(papel_, tinta_, 0.25)
    img = g8.papel(Image.new("RGB", (Wp, Hp), papel_), 0.05 if not o else 0.12)
    d = ImageDraw.Draw(img)
    rnd = random.Random(14)
    for yb in (9, 16, 23):                                          # escritura en espejo
        x = 200 * S
        while x > 22 * S:
            n = rnd.randint(2, 6)
            for _ in range(n):
                d.line([x, yb * S, x + 1.4 * S, (yb - 3.2) * S], fill=sep, width=1)
                d.arc([x - 1.2 * S, (yb - 1.2) * S, x + 0.4 * S, yb * S + 1], 0, 180, fill=sep)
                x -= 1.9 * S
            x -= 2.6 * S
    cx, cy = 318 * S, 38 * S
    if not o:                                                       # Vitruvio
        d.ellipse([cx - 31 * S, cy - 31 * S, cx + 31 * S, cy + 31 * S], outline=sep, width=1)
        d.rectangle([cx - 26 * S, cy - 26 * S, cx + 26 * S, cy + 26 * S], outline=sep, width=1)
        for a_, b_ in (((-26, -26), (26, 26)), ((26, -26), (-26, 26)), ((-31, 0), (31, 0)), ((0, -31), (0, 31))):
            d.line([cx + a_[0] * S, cy + a_[1] * S, cx + b_[0] * S, cy + b_[1] * S], fill=leve, width=1)
        d.arc([cx - 40 * S, cy - 40 * S, cx + 40 * S, cy + 40 * S], 200, 250, fill=san, width=1)
    else:                                                           # la luna con su luz cenicienta (Códice Leicester)
        r = 24 * S
        d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=mezcla(papel_, tinta_, 0.12), outline=leve)
        rr = 26 * S
        yy, xx = np.mgrid[0:Hp, 0:Wp].astype(np.float32)
        luna = ((xx - cx) ** 2 + (yy - cy) ** 2 <= r * r) & ((xx - cx + 13 * S) ** 2 + (yy - cy) ** 2 > rr * rr)
        a = np.asarray(img).copy(); a[luna] = mezcla(tinta_, papel_, 0.12)
        img = Image.fromarray(a); d = ImageDraw.Draw(img)
        for k in range(-20, 21, 3):                                 # achurado de la parte en sombra
            d.line([cx - 20 * S, cy + k * S, cx - 4 * S, cy + (k - 6) * S], fill=mezcla(papel_, tinta_, 0.2))
        d.ellipse([cx - r, cy - r, cx + r, cy + r], outline=leve)
        for k in range(6):                                          # fases dibujadas en fila
            fx, fy, fr = (250 + k * 9) * S, 66 * S, 3 * S
            d.ellipse([fx - fr, fy - fr, fx + fr, fy + fr], outline=sep)
            d.pieslice([fx - fr, fy - fr, fx + fr, fy + fr], 270 - k * 30, 90 + k * 30, fill=sep)
        for (sx_, sy_) in ((262, 8), (280, 14), (360, 6), (380, 18), (392, 10), (236, 30)):
            d.line([sx_ * S - 3, sy_ * S, sx_ * S + 3, sy_ * S], fill=tinta_); d.line([sx_ * S, sy_ * S - 3, sx_ * S, sy_ * S + 3], fill=tinta_)
    gx, gy = 228 * S, 40 * S                                        # engrane
    d.ellipse([gx - 9 * S, gy - 9 * S, gx + 9 * S, gy + 9 * S], outline=sep)
    for k in range(12):
        an = 2 * math.pi * k / 12
        d.line([gx + 9 * S * math.cos(an), gy + 9 * S * math.sin(an), gx + 12 * S * math.cos(an), gy + 12 * S * math.sin(an)], fill=sep, width=2)
    # El molino: torre de piedra achurada y caperuza.
    mx, my = 104, 48
    d.polygon([((mx - 7) * S, 70 * S), ((mx + 7) * S, 70 * S), ((mx + 5) * S, (my + 2) * S), ((mx - 5) * S, (my + 2) * S)], outline=sep)
    for k in range(0, 28, 2):
        d.line([(mx + 1) * S, (my + 4 + k) * S, (mx + 5 + k * 0.07) * S, (my + 1 + k) * S], fill=san)
    d.polygon([((mx - 6) * S, (my + 2) * S), ((mx + 6) * S, (my + 2) * S), (mx * S, (my - 5) * S)], outline=sep)
    d.rectangle([(mx - 2) * S, 63 * S, (mx + 2) * S, 70 * S], outline=sep)
    d.line([(mx - 22) * S, 70 * S, (mx + 24) * S, 70 * S], fill=leve)
    for k in range(6): d.line([(mx + 10 + k * 3) * S, 70 * S, (mx + 14 + k * 3) * S, 66 * S], fill=leve)
    d.line([0, 74 * S, Wp, 74 * S], fill=sep, width=1)
    for x in range(0, W, 4): d.line([x * S, 74 * S, x * S, (75.4 if x % 20 else 77) * S], fill=sep)
    base = img
    hub = (mx * S, (my - 1) * S)
    P = 96
    rnd = random.Random(3)
    aves = [(rnd.uniform(0, 6.28), rnd.uniform(0.8, 1.2)) for _ in range(5)]

    def vivo(img, tick, delante):
        if delante: return
        k = tick % P
        dd = ImageDraw.Draw(img)
        a0 = math.pi / 2 * (k // 12) / 8                            # un cuarto de vuelta por bucle, en 8 pasos (cuatro aspas iguales)
        for j in range(4):
            an = a0 + j * math.pi / 2
            ca, sa = math.cos(an), math.sin(an)
            p0 = (hub[0] + 3 * S * ca, hub[1] + 3 * S * sa); p1 = (hub[0] + 19 * S * ca, hub[1] + 19 * S * sa)
            dd.line([hub, p1], fill=sep, width=2)
            q0 = (p0[0] - 4 * S * sa, p0[1] + 4 * S * ca); q1 = (p1[0] - 4 * S * sa, p1[1] + 4 * S * ca)
            dd.line([q0, q1], fill=sep)
            for f in (0.25, 0.5, 0.75, 1.0):                         # el enrejado de la vela
                m0 = (p0[0] + (p1[0] - p0[0]) * f, p0[1] + (p1[1] - p0[1]) * f)
                dd.line([m0, (m0[0] - 4 * S * sa, m0[1] + 4 * S * ca)], fill=san)
        dd.ellipse([hub[0] - 2, hub[1] - 2, hub[0] + 2, hub[1] + 2], fill=sep)
        for j, (f, r_) in enumerate(aves):                          # pájaros (de noche, murciélagos) en vuelo
            an = 2 * math.pi * (k // 8 * 8) / P + f
            x = (140 + j * 16 + 12 * math.cos(an) * r_) * S; y = (40 + 6 * math.sin(an * 2) * r_ - j * 2) * S
            ala = ((k // 8 + j) % 2) * 2 - 1
            if not o:
                dd.line([x - 4 * S, y - 1.5 * S * ala, x, y], fill=sep, width=1); dd.line([x, y, x + 4 * S, y - 1.5 * S * ala], fill=sep, width=1)
            else:
                pts = [(x - 5 * S, y - 1.5 * S * ala), (x - 2 * S, y), (x, y + S), (x + 2 * S, y), (x + 5 * S, y - 1.5 * S * ala), (x, y - S)]
                dd.polygon(pts, outline=sep)
    ANIM[("renacimiento", o)] = [vivo]
    return base


g8.FONDOS["renacimiento"] = fondo_renacimiento


# =====================================================================
#  Espacio: de día, la plataforma de lanzamiento (cielo claro, el cohete en su torre, vapor que sale de la base, el radar
#  que gira y la Luna pálida en el cielo). De noche, la Luna de gato8 (nebulosa, el planeta anillado), ahora con la Tierra
#  en el cielo y un módulo de descenso con su luz que parpadea.
# =====================================================================

def fondo_espacio(t):
    o = t["oscuro"]
    if o:
        base = g8.fondo_espacio(t)
        estrellas_ = list(g8.EXTRA["espacio"]["estrellas"])
        m = Image.new("RGBA", (W, H), (0, 0, 0, 0)); d = ImageDraw.Draw(m)
        ex, ey = 70, 18                                            # la Tierra
        d.ellipse([ex - 7, ey - 7, ex + 7, ey + 7], fill=hx("2E5FB8") + (255,))
        for (x, y, w) in ((ex - 4, ey - 4, 4), (ex + 1, ey - 1, 5), (ex - 5, ey + 2, 3), (ex + 2, ey + 4, 2)):
            d.line([x, y, x + w, y], fill=hx("4E9A5A") + (255,))
        for (x, y, w) in ((ex - 3, ey - 6, 5), (ex - 6, ey, 3), (ex + 3, ey + 2, 3)):
            d.line([x, y, x + w, y], fill=hx("F2F4FA") + (255,))
        d.chord([ex - 7, ey - 7, ex + 7, ey + 7], 300, 120, fill=(3, 5, 12, 150))
        lx, ly = 240, 70                                           # módulo de descenso
        d.polygon([(lx - 5, ly - 9), (lx + 5, ly - 9), (lx + 7, ly - 4), (lx - 7, ly - 4)], fill=hx("C9A040") + (255,))
        d.rectangle([lx - 4, ly - 13, lx + 4, ly - 9], fill=hx("B8BEC8") + (255,)); d.point((lx + 2, ly - 12), fill=hx("26324F") + (255,))
        for s in (-1, 1):
            d.line([lx + 5 * s, ly - 5, lx + 8 * s, ly], fill=hx("8E9AB8") + (255,)); d.line([lx + 7 * s, ly, lx + 9 * s, ly], fill=hx("8E9AB8") + (255,))
        d.line([lx, ly - 13, lx, ly - 17], fill=hx("8E9AB8") + (255,))
        big = m.resize((W * ESC, H * ESC), Image.NEAREST); base.paste(big, (0, 0), big)
        P = 60

        def vivo(img, tick, delante):
            if delante: return
            k = tick % P
            dd = ImageDraw.Draw(img)
            for j, (sx, sy) in enumerate(estrellas_):
                if (j * 7 + k // 3) % 5 == 0:
                    dd.rectangle([sx * ESC - 1, sy * ESC - 1, sx * ESC + ESC, sy * ESC + ESC], fill=hx("FFFFFF"))
            if (k // 6) % 5 == 0:
                cuad(dd, lx, ly - 18, hx("FF5A4A"))
        ANIM[("espacio", True)] = [vivo]
        return base
    cielo = [hx("6E9ED8"), hx("8FB4E2"), hx("B3CCEC"), hx("D6E2F2"), hx("EEF1F6")]
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 52, cielo)
    creciente(im, 372, 12, 4, hx("E8ECF4"), -2)                                                     # la Luna de día
    nube(d, 40, 14, 34, hx("FFFFFF"), hx("F2F5FA"), hx("D6DEEA")); nube(d, 350, 22, 26, hx("FFFFFF"), hx("F2F5FA"), hx("D6DEEA"))
    perfil(im, ondas(71, 54, ((2, 120), (1, 40))), hx("9DB0C8"), borde=hx("B3C4D8"))                  # lomas lejanas
    perfil(im, ondas(72, 62, ((1.5, 150), (1, 60))), hx("8FA27A"), borde=hx("A4B68C"))
    d.rectangle([0, 68, W, 74], fill=hx("C9CCD2")); d.line([0, 68, W, 68], fill=hx("E2E5EA"))
    for x in range(0, W, 20): d.line([x, 69, x, 73], fill=hx("B3B7BF"))
    d.line([0, 74, W, 74], fill=hx("4E5A70"))
    for y in range(75, 80):
        for x in range(W):
            d.point((x, y), fill=hx("E8601C") if ((x + y) // 4) % 2 else hx("F4F4F0"))         # franjas de peligro
    # Torre de servicio (celosía roja) y el cohete.
    tx = 250
    d.rectangle([tx, 14, tx + 10, 68], outline=hx("B3261E"))
    for y in range(14, 68, 6):
        d.line([tx, y, tx + 10, y + 6], fill=hx("B3261E")); d.line([tx + 10, y, tx, y + 6], fill=hx("B3261E"))
    for y in (24, 40, 54): d.line([tx + 10, y, tx + 16, y], fill=hx("8A1E18"))
    rx = 284
    d.rectangle([rx - 6, 22, rx + 6, 62], fill=hx("F4F4F0")); d.rectangle([rx + 3, 22, rx + 6, 62], fill=hx("D6DAE2"))
    d.polygon([(rx - 6, 22), (rx, 6), (rx + 6, 22)], fill=hx("F4F4F0")); d.polygon([(rx, 6), (rx + 6, 22), (rx + 3, 22)], fill=hx("D6DAE2"))
    d.rectangle([rx - 6, 30, rx + 6, 33], fill=hx("1B3A8C")); d.rectangle([rx - 6, 48, rx + 6, 50], fill=hx("E8601C"))
    texto(d, rx - 1, 36, "V", hx("1B3A8C")); texto(d, rx - 1, 41, "M", hx("1B3A8C"))
    for s in (-1, 1):
        d.polygon([(rx + 6 * s, 52), (rx + 11 * s, 62), (rx + 6 * s, 62)], fill=hx("1B3A8C"))
    d.rectangle([rx - 4, 62, rx + 4, 64], fill=hx("4E5A70"))
    d.rectangle([rx - 14, 64, rx + 14, 68], fill=hx("6E7684")); d.line([rx - 14, 64, rx + 14, 64], fill=hx("9AA2B0"))
    # Edificio de control con su antena de radar.
    bx = 40
    d.rectangle([bx, 50, bx + 46, 68], fill=hx("E6E9EE")); d.rectangle([bx + 40, 50, bx + 46, 68], fill=hx("C9CED8"))
    for wx in range(bx + 4, bx + 38, 7): d.rectangle([wx, 55, wx + 4, 58], fill=hx("6E9ED8"))
    d.rectangle([bx + 20, 61, bx + 26, 68], fill=hx("4E5A70")); d.line([bx, 50, bx + 46, 50], fill=hx("4E5A70"))
    texto(d, bx + 4, 44, "CONTROL", hx("1B3A8C"))
    d.line([bx + 34, 50, bx + 34, 38], fill=hx("4E5A70"))
    P = 48

    def cuadro_dp(k):
        f = im.copy(); dd = ImageDraw.Draw(f)
        ang = 2 * math.pi * k / P                                  # el radar gira (se ve de lado: se ensancha y se angosta)
        w_ = round(6 * abs(math.cos(ang)))
        dd.ellipse([bx + 34 - w_, 32, bx + 34 + w_, 38], fill=hx("D6DAE2"), outline=hx("4E5A70")) if w_ > 0 else dd.line([bx + 34, 32, bx + 34, 38], fill=hx("4E5A70"))
        for j, y in enumerate((14, 30, 46)):                       # luces de la torre
            if (k // 6 + j) % 3 == 0: dd.point((tx + 5, y), fill=hx("FF3A2A"))
        for j in range(5):                                         # vapor de la base del cohete
            q = (k + j * 10) % 48
            if q < 40:
                r_ = 1 + q // 10
                x = rx + (12 + q * 0.6) * (1 if j % 2 else -1); y = 66 - q * 0.12
                dd.ellipse([x - r_, y - r_, x + r_, y + r_], fill=mezcla(hx("FFFFFF"), hx("D6E2F2"), q / 48))
        return f

    return animado("espacio", False, cuadro_dp, P)


g8.FONDOS["espacio"] = fondo_espacio


# =====================================================================
#  Navidad: de día, el bosque nevado con una cabaña de troncos (humo de la chimenea), un muñeco de nieve y nieve que cae
#  despacio. De noche, el bosque de gato8: la guirnalda de foquitos y la nevada, ahora con luna y la cabaña encendida.
# =====================================================================

def fondo_navidad(t):
    o = t["oscuro"]
    rnd = random.Random(9)
    g8.EXTRA["navidad"] = dict(luces=[], copos=[])                 # exportar.py lo busca; aquí la nieve va en ANIM
    if not o:
        cielo = [hx("B9CCD8"), hx("CFDCE4"), hx("E2EAEE"), hx("F2F4F2")]
        pino, pino2, nieve, nieve2 = hx("2E5A3E"), hx("1B4A2E"), hx("FFFFFF"), hx("DCE6EE")
        lejos = hx("A8BCC4")
    else:
        cielo = [hx("050B08"), hx("08120D"), hx("0B1611"), hx("0E1A14")]
        pino, pino2, nieve, nieve2 = hx("1C3125"), hx("2E4A39"), hx("F3EFE6"), hx("B5C2B8")
        lejos = hx("101E17")
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 60, cielo)
    if o:
        for _ in range(40): d.point((rnd.randrange(W), rnd.randrange(2, 30)), fill=mezcla(hx("F3EFE6"), hx("0E1A14"), rnd.uniform(0.3, 0.7)))
        creciente(im, 330, 14, 6, hx("F3EFE6"), 3)
    else:
        nube(d, 120, 12, 40, hx("FFFFFF"), hx("F2F5F6"), hx("DCE4E8"))
    perfil(im, ondas(81, 50, ((3, 140), (2, 60))), lejos)

    def abeto(x, h, col, col2, cargado):
        for k in range(4):
            y0 = 72 - h + k * h // 4; w = 3 + k * 3
            d.polygon([(x, y0), (x - w, y0 + h // 3), (x + w, y0 + h // 3)], fill=col)
            d.polygon([(x, y0), (x + w, y0 + h // 3), (x + 1, y0 + h // 3)], fill=col2)
            if cargado:
                d.line([x - w + 1, y0 + h // 3 - 1, x - 1, y0 + h // 3 - 2], fill=nieve); d.point((x, y0), fill=nieve)
        d.rectangle([x - 1, 70, x + 1, 72], fill=hx("4A3422"))
    for x in range(6, W, 17):
        if 150 < x < 214: continue
        abeto(x + rnd.randint(-4, 4), rnd.randint(22, 34), pino if not o else hx("17291F"), pino2 if not o else hx("1C3125"), not o)
    for x in range(30, W, 70):
        if 140 < x < 224: continue
        abeto(x + rnd.randint(-8, 8), rnd.randint(34, 44), pino2 if not o else pino2, pino if not o else hx("24402F"), True)
    # Cabaña de troncos con techo nevado.
    cx = 182
    madera, madera2 = (hx("7A4A2A"), hx("5A3420")) if not o else (hx("3A2618"), hx("2A1A10"))
    d.rectangle([cx - 16, 54, cx + 16, 71], fill=madera)
    for y in range(56, 71, 3): d.line([cx - 16, y, cx + 16, y], fill=madera2)
    d.polygon([(cx - 20, 55), (cx, 41), (cx + 20, 55)], fill=nieve); d.line([cx - 20, 55, cx + 20, 55], fill=nieve2)
    d.rectangle([cx + 8, 40, cx + 11, 49], fill=hx("6A4A3A")); d.rectangle([cx + 7, 39, cx + 12, 40], fill=nieve)
    luzv = hx("F2C94C") if o else hx("6E8A9A")
    for wx in (cx - 12, cx + 6):
        d.rectangle([wx, 59, wx + 5, 64], fill=luzv); d.line([wx + 2, 59, wx + 2, 64], fill=madera2); d.line([wx, 61, wx + 5, 61], fill=madera2)
    d.rectangle([cx - 3, 61, cx + 2, 71], fill=madera2); d.point((cx + 1, 66), fill=hx("F2C94C"))
    d.ellipse([cx - 3, 57, cx + 2, 60], outline=hx("1B5E20") if not o else hx("66BB6A"))     # corona en la puerta
    if o:
        luz(im, cx - 9, 62, 12, hx("F2C94C"), 0.25, 3); luz(im, cx + 9, 62, 12, hx("F2C94C"), 0.25, 3)
    # Muñeco de nieve.
    sx = 236
    d.ellipse([sx - 6, 60, sx + 6, 72], fill=nieve); d.ellipse([sx - 4, 51, sx + 4, 60], fill=nieve)
    d.ellipse([sx - 6, 60, sx + 6, 72], outline=nieve2); d.point((sx - 2, 54), fill=hx("1E2A22")); d.point((sx + 1, 54), fill=hx("1E2A22"))
    d.line([sx, 56, sx + 3, 57], fill=hx("E8601C")); d.line([sx - 4, 59, sx + 4, 59], fill=hx("B71C1C")); d.line([sx + 3, 59, sx + 4, 63], fill=hx("B71C1C"))
    d.rectangle([sx - 4, 48, sx + 3, 51], fill=hx("1E2A22")); d.line([sx - 5, 51, sx + 4, 51], fill=hx("1E2A22"))
    for x in range(W):
        top = 71 + round(1.2 * math.sin(x / 9))
        d.line([x, top, x, 80], fill=nieve); d.point((x, 79), fill=mezcla(nieve, nieve2, 0.6))
    luces = []
    if o:
        for k, x in enumerate(range(5, W, 9)):
            y = 4 + 3 * math.sin(math.pi * (x % 61) / 61)
            d.point((x, y - 1), fill=hx("1C3125")); luces.append((x, round(y), (hx("E5484D"), hx("66BB6A"), hx("F2C94C"))[k % 3]))
        d.line([(x, 3 + 3 * math.sin(math.pi * (x % 61) / 61)) for x in range(W)], fill=hx("1C3125"))
    copos = [(rnd.uniform(0, W), rnd.uniform(0, H), rnd.choice((1, 2)), rnd.random() < 0.25) for _ in range(40 if o else 26)]
    P = 160
    base = subir(im)

    def vivo(img, tick, delante):
        k = tick % P
        dd = ImageDraw.Draw(img)
        if not delante:
            for j, (lx, ly, col) in enumerate(luces):
                enc = (j + k // 4) % 3 != 0
                dd.rectangle([lx * ESC, ly * ESC, lx * ESC + ESC - 1, ly * ESC + ESC - 1], fill=col if enc else mezcla(col, (0, 0, 0), 0.6))
            for j in range(6):                                     # humo de la chimenea
                q = (k + j * 8) % 48
                if q < 40:
                    x = cx + 9.5 + q * 0.18 + math.sin(q / 6 + j) * 0.8; y = 38 - q * 0.45
                    cuad(dd, round(x), round(y), mezcla(hx("9AA8B4") if not o else hx("4A5A50"), cielo[1], q / 50), 1 + q // 16, 1)
            return
        for (x0, y0, vy, grande) in copos:                         # nieve que cae (un dp cada vy cuadros, periodo P)
            y = (y0 + k * H / P * vy) % H
            x = (x0 + 2 * math.sin(2 * math.pi * (k / P) * 2 + x0)) % W
            r = ESC if grande else ESC - 1
            dd.rectangle([round(x) * ESC, round(y) * ESC, round(x) * ESC + r - 1, round(y) * ESC + r - 1], fill=nieve if o or grande else hx("C2D2DE"))
            if not o and grande:
                dd.rectangle([round(x) * ESC, round(y) * ESC + r - 1, round(x) * ESC + r - 1, round(y) * ESC + r - 1], fill=hx("B4C6D4"))
    ANIM[("navidad", o)] = [vivo]
    return base


g8.FONDOS["navidad"] = fondo_navidad


# =====================================================================
#  Helénico de día (el fondo es de gato9): más vida en el Egeo, una nave con la vela henchida que cabecea entre las
#  columnas, gaviotas que planean y nubes.
# =====================================================================
_heleno9 = g8.FONDOS["heleno"]


def fondo_heleno(t):
    big = _heleno9(t)
    if t["oscuro"]:
        return big
    m = Image.new("RGBA", (W, H), (0, 0, 0, 0)); d = ImageDraw.Draw(m)
    for (x, y, w) in ((26, 24, 30), (94, 20, 32), (340, 24, 26)):
        nube(d, x, y, w, hx("FFFFFF") + (255,), hx("EEF3F4") + (255,), hx("D3E0E6") + (255,))
    for x0 in (90, 218):                                            # olivos en macetas, entre las columnas
        d.rectangle([x0 - 3, 58, x0 + 3, 63], fill=hx("B5612E") + (255,)); d.line([x0 - 3, 58, x0 + 3, 58], fill=hx("8A4A22") + (255,))
        d.line([x0, 58, x0, 50], fill=hx("6A5A3A") + (255,))
        for (dx, dy, r) in ((-3, 48, 3), (2, 46, 3), (0, 44, 2), (4, 50, 2)):
            d.ellipse([x0 + dx - r, dy - r, x0 + dx + r, dy + r], fill=hx("7A8A5A") + (255,))
        for (dx, dy) in ((-4, 47), (1, 45), (3, 49), (-1, 49)): d.point((x0 + dx, dy), fill=hx("A8B48A") + (255,))
    capa = m.resize((W * ESC, H * ESC), Image.NEAREST)
    big.paste(capa, (0, 0), capa)
    P = 84                                                          # múltiplo de 21, el bucle del brillo del mar

    def nave(img, tick, delante):
        if delante: return
        k = tick % P
        dd = ImageDraw.Draw(img)
        x, y = 296, 52 + (1 if (k // 21) % 2 else 0)               # cabecea un dp
        cuad(dd, x - 9, y - 1, hx("3A2618"), 18, 2); cuad(dd, x - 7, y + 1, hx("3A2618"), 14, 1)
        cuad(dd, x + 9, y - 2, hx("3A2618"), 2, 1); cuad(dd, x - 11, y - 3, hx("3A2618"), 2, 2)       # proa y popa
        cuad(dd, x - 6, y, hx("B5612E"), 12, 1)
        cuad(dd, x, y - 13, hx("4A3422"), 1, 12)
        for j in range(5):                                          # vela henchida
            cuad(dd, x - 4 + (1 if j in (1, 2, 3) else 0) * ((k // 21) % 2), y - 12 + j * 2, hx("F3EFE6"), 8, 2)
        cuad(dd, x - 2, y - 9, hx("8E2C48"), 4, 4)
        for j in range(5):                                          # remos
            ox = x - 6 + j * 3
            cuad(dd, ox + ((k // 7 + j) % 2), y + 2, hx("3A2618"), 1, 2)
        for j in range(3):                                          # gaviotas
            an = 2 * math.pi * k / P + j * 2.1
            gx, gy = 190 + j * 30 + 14 * math.cos(an), 26 + 4 * math.sin(an) + j * 3
            ala = (k // 5 + j) % 2
            cuad(dd, round(gx) - 2, round(gy) - ala, hx("4A4A52")); cuad(dd, round(gx) - 1, round(gy), hx("F7F7F2"))
            cuad(dd, round(gx), round(gy), hx("F7F7F2")); cuad(dd, round(gx) + 1, round(gy) - ala, hx("4A4A52"))
    ANIM[("heleno", False)] = ANIM.get(("heleno", False), []) + [nave]
    return big


g8.FONDOS["heleno"] = fondo_heleno
g9.FONDOS9["heleno"] = fondo_heleno


# =====================================================================
#  Revisión: un cuadro con el gato y varios momentos del fondo, de día y de noche
# =====================================================================

def vista(n, ticks=(0, 6, 13, 27), archivo=None):
    filas = []
    for o in ((True,) if n == "ciber" else (False, True)):
        t = tema(n, o)
        f = g8.FONDOS[n](t)
        img = g8.cuadro_banda(n, o, t, f, ("caminar", 2, 40.0, 1, 0.0), 0, None)
        guarda = ANIM.get((n, o), [])
        ANIM[(n, o)] = []                      # los otros dos gatos, sin volver a pintar el fondo encima
        img = g8.cuadro_banda(n, o, t, img, (f"esp_{n}_0", 1, 170.0, -1, 0.0), 0, None)
        img = g8.cuadro_banda(n, o, t, img, (f"esp_{n}_1", 2, 300.0, 1, 0.0), 0, None)
        ANIM[(n, o)] = guarda
        filas.append(img)
        for k in ticks[1:]:
            filas.append(g8.cuadro_banda(n, o, t, f, ("quieto", 0, -1000.0, 1, 0), k, None))
    hoja = Image.new("RGB", (W * ESC, len(filas) * (H * ESC + 6)), (128, 128, 128))
    for k, f in enumerate(filas):
        hoja.paste(f, (0, k * (H * ESC + 6)))
    hoja.save(AQUI / (archivo or f"g19_{n}.png"))


def bandas(n):
    for o in ((True,) if n == "ciber" else (False, True)):
        et = [b[2] for b in g8.BANDAS if b[0] == n][0]
        g8.gif_banda(g17.secuencia(n), [(n, o, et)], "_g19" + ("_oscuro" if o else ""))


if __name__ == "__main__":
    que = sys.argv[1] if len(sys.argv) > 1 else "vista"
    nombres = sys.argv[2:] or MIOS
    if que == "vista":
        for n in nombres:
            vista(n)
    elif que == "gif":
        for n in nombres:
            bandas(n)
    elif que == "hojas":
        g17.hojas("g19_animaciones")
        claro = list(g8.BANDAS)
        g8.BANDAS[:] = [(n, True, e) for n, _, e in claro]
        g17.hojas("g19_animaciones_oscuro")
        g8.BANDAS[:] = claro
    print("listo")
