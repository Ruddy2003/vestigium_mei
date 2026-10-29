"""
Prueba 16: vuelve el casco de legionario al gato romano, más grande (el gato se ve chiquito debajo) y de un gris acero
más oscuro, con un brillo, para que se distinga del mármol. Lo demás viene de gato15.py (colonos, soviético, romano).
"""
import math
import sys
from pathlib import Path

from PIL import Image, ImageDraw

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato12 as g12      # noqa: E402
import gato15 as g15      # noqa: E402
from gato8 import hx      # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
for nombre in ("gato8", "gato9", "gato10", "gato11", "gato12", "gato13", "gato15"):
    sys.modules[nombre].AQUI = AQUI

ACERO, ACERO_N, PENACHO, ORO = hx("858E98"), hx("7A838E"), hx("A8283C"), hx("E2B84E")


def orejas_encima(L, cx, cy, inc):
    """Orejas que salen por arriba del casco grande (más largas que las de la cabeza, porque la base queda tapada)."""
    a = math.radians(inc); p = lambda x, y: g8.rot(cx, cy, x, y, a)
    o = g12._ultimas["orejas"]
    pt = lambda u, v: (u[0] + (v[0] - u[0]) * o, u[1] + (v[1] - u[1]) * o)
    L.poligono("oreja2l", [p(*q) for q in [(-7.2, -6.0), pt((-6.2, -12.4), (-10.6, -6.8)), (-3.2, -7.2)]])
    L.poligono("oreja2", [p(*q) for q in [(0.0, -7.4), pt((2.8, -12.8), (-6.0, -9.2)), (5.6, -5.8)]])
    L.poligono("oreja2i", [p(*q) for q in [(1.4, -7.2), pt((2.8, -11.0), (-4.6, -8.4)), (4.2, -6.2)]])


def sombrero_lado(L, cx, cy, inc, som):
    if som != "galea":
        return g12._lado10(L, cx, cy, inc, som)
    a = math.radians(inc)
    p = lambda x, y: g8.rot(cx, cy, x, y, a)
    P = lambda pts: [p(*q) for q in pts]
    L.poligono("sombrero", P([(-1.0 + 9.2 * math.cos(t), -2.8 + 6.0 * math.sin(t)) for t in [math.pi + k * math.pi / 20 for k in range(21)]]))
    L.poligono("sombrero", P([(-8.6, -3.6), (-11.6, -0.6), (-10.8, 0.6), (-7.2, -1.4)]))          # guardanuca
    L.poligono("sombrero", P([(-3.4, -3.0), (1.0, -3.0), (0.6, 2.2), (-2.6, 2.6)]))               # carrillera
    L.capsula("sombrero3", P([(-10.0, -2.7), (8.0, -2.7)]), 0.7)                                   # filo de oro
    L.elipse("sombrero3", *p(-1.2, -0.2), 0.6, 0.6)                                                 # remache
    L.elipse("sombreroh", *p(-5.0, -5.8), 1.4, 0.8, inc - 25)                                       # brillo del acero
    L.elipse("sombrero3", *p(-2.0, -8.6), 1.1, 0.8)                                                 # portapenacho
    L.poligono("sombrero2", P([(-2.2 + 3.0 * math.cos(t), -9.0 + 4.4 * math.sin(t)) for t in [math.pi + k * math.pi / 12 for k in range(13)]]))
    orejas_encima(L, cx, cy, inc)


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if som != "galea":
        return g12._frente10(L, cx, cy, som, desv)
    dx = desv * 1.6
    L.poligono("sombrero", [(cx + dx + 9.6 * math.cos(t), cy - 2.8 + 5.8 * math.sin(t)) for t in [math.pi + k * math.pi / 20 for k in range(21)]])
    for s in (-1, 1):
        L.poligono("sombrero", [(cx + dx + s * 9.4, cy - 2.8), (cx + dx + s * 5.8, cy - 2.8), (cx + dx + s * 6.2, cy + 2.4), (cx + dx + s * 8.8, cy + 2.0)])
    L.capsula("sombrero3", [(cx + dx - 9.6, cy - 2.7), (cx + dx + 9.6, cy - 2.7)], 0.7)
    L.elipse("sombreroh", cx + dx - 4.6, cy - 5.4, 1.3, 0.8, -20)
    L.elipse("sombrero2", cx + dx, cy - 10.4, 2.0, 3.4)
    L.elipse("sombrero3", cx + dx, cy - 8.4, 1.1, 0.8)
    for s in (-1, 1):                                # de espaldas: las dos orejas salen por arriba del casco
        k = 0.88 if (s > 0 and desv > 0) else 1.0
        L.poligono("oreja2", [(cx + s * 7.6 * k, cy - 5.2), (cx + s * 6.0 * k, cy - 12.2), (cx + s * 2.6, cy - 7.2)])


g8.sombrero_lado, g8.sombrero_frente = sombrero_lado, sombrero_frente

_tema15 = g8.tema


def tema(nombre, oscuro=False):
    t = _tema15(nombre, oscuro)
    if nombre == "romano":
        col = dict(t["col"])
        col.update(M=ACERO if not oscuro else ACERO_N, Z=PENACHO, G=ORO, H=hx("FFFFFF"))
        t = dict(t, col=col, acc=(t["acc"][0] if isinstance(t["acc"], tuple) else t["acc"], "galea"))
    return t


for nombre in ("gato8", "gato9", "gato10", "gato12", "gato13"):
    sys.modules[nombre].tema = tema


def cabezas(archivo):
    """La cabeza con el casco, ampliada, en varias poses (de lado, sentado, de espaldas, enojado, dormido)."""
    t = tema("romano", False)
    fr = [("caminar", 2), ("sentado", 0), ("girar", 0), ("girar", 1), ("enojado", 0), ("dormir", 0)]
    cel = 7; lado = 64 * cel
    img = Image.new("RGB", (len(fr) * lado, lado), hx("ECEDEA"))
    for k, (a, i) in enumerate(fr):
        s = g8.pintar(g8.rejilla(a, i, t), t, cel)
        img.paste(s, (k * lado, 0), s)
    img.save(AQUI / archivo)


if __name__ == "__main__":
    que = sys.argv[1:] or ["cabezas", "banda", "hoja"]
    if "cabezas" in que:
        cabezas("casco_poses.png")
    s_, cs = g8.buscar_semilla()
    if "banda" in que:
        g8.gif_banda(cs, [("romano", False, g8.BANDAS[0][2])])
    if "hoja" in que:
        g8.hoja("romano", False, "hoja_sprites_romano.png")
    print("listo")
