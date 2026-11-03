"""
Prueba 20a (lote 9b): el gato de tres temas nuevos.
- Pirata: atigrado naranja grabado en sepia como una carta náutica (achurado horizontal, aguada sobre pergamino), con
  pañuelo en la cabeza y parche. Cubierta de un barco con el mar detrás; de noche, farol y estrellas. Abre un cofre y
  saca una moneda; sigue a una gaviota.
- Ciudad neón (solo oscuro, se suma como pantalla): gato negro con contorno de luz magenta y ámbar, impermeable con
  capucha. Callejón de los niveles bajos con letreros en varios alfabetos y un puesto de fideos, con lluvia, vapor y
  letreros que parpadean. Sorbe fideos con palillos; sigue al dron de reparto.
- Roma (la de hoy): atigrado gris pintado al fresco sobre estuco, con un pañuelo al cuello que vuela. Callejón con
  ropa tendida, motoneta, la cúpula y las ruinas con sus gatos; de noche, farolas y la cúpula iluminada. Come un
  helado; persigue una paloma.
Todo lo demás viene de la cadena (gato18.py). Colores de ui/Tema.kt (neon) y ui/Disenios.kt (pirata, urbe).
"""
import math
import random
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter

sys.path.insert(0, str(Path(__file__).parent))
import gato18 as g18      # noqa: E402,F401
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato12 as g12      # noqa: E402
import gato16 as g16      # noqa: E402
import gato17 as g17      # noqa: E402
from gato8 import hx, mezcla, fuente, ESC, SS   # noqa: E402
from gato9 import W, H, cuad, degradado, luz, lienzo, subir, tramar   # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
N, SUELO = g8.N, g8.SUELO
MIOS = ("pirata", "neon", "urbe")
_actual = {"tema": None}       # el último tema pedido (para las líneas de escaneo y la viñeta de las pantallas)

# =====================================================================
#  Accesorios: pañuelo de pirata con parche, capucha e impermeable, pañuelo al cuello que vuela
# =====================================================================


def _ps():
    return g17._actual.get("ps") or {}


def pañoleta_lado(L, cx, cy, inc):
    """Pañuelo atado en la cabeza (rojo con lunares), el nudo y sus puntas atrás; el parche sobre el ojo con su cinta."""
    a = math.radians(inc)
    p = lambda x, y: g8.rot(cx, cy, x, y, a)
    P = lambda pts: [p(*q) for q in pts]
    v = _ps().get("viento", 0.3)
    L.poligono("sombrero", P([(-7.0, 0.8), (-7.0, -2.8), (-4.8, -5.9), (-1.0, -6.9), (2.8, -6.1), (5.2, -3.9), (5.8, -2.4),
                              (3.4, -2.9), (0.2, -2.7), (-2.8, -1.4), (-5.0, 0.6)]))
    L.capsula("sombrero2", P([(5.6, -2.5), (3.2, -3.0), (0.2, -2.8), (-2.8, -1.5), (-5.2, 0.5)]), 0.8)   # el doblez
    for x, y in ((-3.6, -3.6), (0.4, -5.0), (3.2, -4.2), (-1.4, -2.6)):
        L.elipse("sombreroh", *p(x, y), 0.55, 0.55)
    # Nudo y puntas (se mueven con el viento al caminar).
    L.elipse("sombrero2", *p(-7.2, -0.8), 1.5, 1.4)
    L.poligono("sombrero", P([(-7.4, -1.6), (-11.4 - 1.2 * v, -2.6 + 1.6 * v), (-10.6 - 1.0 * v, -0.4 + 1.2 * v), (-7.6, 0.2)]))
    L.poligono("sombrero", P([(-7.2, -0.2), (-10.2 - 0.8 * v, 2.4 + 0.6 * v), (-8.4 - 0.4 * v, 3.0), (-6.8, 0.6)]))
    # Parche de cuero sobre el ojo, con su cinta hasta el pañuelo y hacia el hocico.
    L.elipse("vidrio", *p(2.5, -0.5), 1.75, 1.7, inc)
    L.capsula("vidrio", [p(1.4, -1.6), p(-1.6, -2.4)], 0.6)
    L.capsula("vidrio", [p(3.6, -1.6), p(5.6, -2.4)], 0.55)
    g16.orejas_encima(L, cx, cy, inc)


def pañoleta_frente(L, cx, cy, desv):
    dx = desv * 1.6
    L.elipse("sombrero", cx + dx, cy - 3.6, 7.2, 4.0)
    L.capsula("sombrero2", [(cx - 6.8 + dx, cy - 1.0), (cx + 6.8 + dx, cy - 1.0)], 0.8)
    for x, y in ((-3.6, -4.6), (0.4, -6.2), (3.8, -4.4), (-0.8, -2.8)):
        L.elipse("sombreroh", cx + dx + x, cy + y, 0.55, 0.55)
    v = _ps().get("viento", 0.3)
    L.elipse("sombrero2", cx + dx, cy - 0.4, 1.5, 1.4)                     # el nudo, en la nuca
    L.poligono("sombrero", [(cx + dx - 0.6, cy), (cx + dx - 2.8, cy + 5.0), (cx + dx - 1.0, cy + 5.4), (cx + dx + 0.2, cy + 0.4)])
    L.poligono("sombrero", [(cx + dx + 0.6, cy), (cx + dx + 3.0, cy + 4.4), (cx + dx + 1.2, cy + 5.2), (cx + dx - 0.2, cy + 0.4)])
    _orejas_espalda(L, cx, cy, desv)


def _orejas_espalda(L, cx, cy, desv):
    for s in (-1, 1):
        k = 0.88 if (s > 0 and desv > 0) else 1.0
        L.poligono("oreja2", [(cx + s * 7.6 * k, cy - 4.6), (cx + s * 6.0 * k, cy - 11.6), (cx + s * 2.6, cy - 6.6)])


def capucha_lado(L, cx, cy, inc):
    """Capucha del impermeable: rodea la cara, cubre la nuca y deja salir las orejas por arriba."""
    a = math.radians(inc)
    p = lambda x, y: g8.rot(cx, cy, x, y, a)
    P = lambda pts: [p(*q) for q in pts]
    L.poligono("sombrero", P([(-6.8, 6.2), (-8.4, 1.6), (-8.0, -3.0), (-5.6, -6.8), (-1.6, -8.0), (2.4, -7.2), (5.2, -4.8),
                              (5.8, -3.0), (3.6, -3.8), (0.6, -4.6), (-2.2, -3.6), (-3.6, -0.6), (-3.4, 3.2), (-2.2, 6.0)]))
    L.capsula("sombrero2", P([(5.8, -3.1), (3.4, -4.0), (0.4, -4.8), (-2.4, -3.8), (-3.8, -0.6), (-3.6, 3.2), (-2.4, 6.0)]), 0.9)
    L.capsula("sombreroh", P([(-6.0, -4.6), (-3.4, -6.6), (-0.4, -7.2)]), 0.55)      # el brillo del vinilo
    g16.orejas_encima(L, cx, cy, inc)


def capucha_frente(L, cx, cy, desv):
    dx = desv * 1.6
    L.elipse("sombrero", cx + dx, cy - 1.0, 8.0, 7.6)
    L.capsula("sombrero2", [(cx + dx - 6.6, cy + 4.6), (cx + dx, cy + 6.2), (cx + dx + 6.6, cy + 4.6)], 0.9)
    L.capsula("sombreroh", [(cx + dx - 5.0, cy - 5.0), (cx + dx - 2.0, cy - 7.2)], 0.55)
    _orejas_espalda(L, cx, cy, desv)


def chaqueta(L, ps):
    """Impermeable: cubre el pecho y el lomo hasta la grupa; cuello alto, botones y una franja reflectante en el borde."""
    Hh, C, rh, rc = ps["H"], ps["C"], ps["rh"], ps["rc"]
    ct, ht = (C[0], C[1] - rc), (Hh[0], Hh[1] - rh)
    dx, dy = ht[0] - ct[0], ht[1] - ct[1]; n = math.hypot(dx, dy) or 1
    nx, ny = -dy / n, dx / n
    lp = lambda t, up=0.0: (ct[0] + dx * t + nx * up, ct[1] + dy * t + ny * up)
    a, b, px, py = g8._cuello(C, ps["cab"])
    cu = (a[0] + (b[0] - a[0]) * 0.5, a[1] + (b[1] - a[1]) * 0.5)
    v = ps.get("viento", 0.3)
    pts = [(cu[0] - 4.6 * px, cu[1] - 4.6 * py), (cu[0] + 4.4 * px, cu[1] + 4.4 * py),
           (C[0] + rc * 0.95, C[1] - rc * 0.1), (C[0] + rc * 0.7, C[1] + rc * 0.55),
           lp(0.32, -rc * 1.55), lp(0.62, -rh * 1.45), lp(0.8 + 0.06 * v, -rh * 1.1 + 0.4 * v), lp(0.82 + 0.08 * v, 0.6 + 0.5 * v), lp(0.1, 1.2)]
    L.poligono("capa", pts)
    L.capsula("capa", [(cu[0] - 4.6 * px, cu[1] - 4.6 * py), (cu[0] + 4.4 * px, cu[1] + 4.4 * py)], 2.4)     # cuello alto
    L.capsula("broche", [lp(0.3, -rc * 1.45), lp(0.62, -rh * 1.35)], 0.8)            # franja reflectante
    for t_ in (0.25, 0.55):
        L.elipse("broche", C[0] + rc * (0.8 - t_ * 0.3), C[1] - rc * 0.1 + rc * t_ * 1.0, 0.5, 0.5)


def chaqueta_espalda(L, cx, ny, todo):
    if todo:
        L.poligono("capa", [(cx - 7.0, ny - 0.6), (cx + 7.0, ny - 0.6), (cx + 8.8, ny + 14.6), (cx - 8.8, ny + 14.6)])
        L.capsula("capa", [(cx - 5.0, ny - 0.4), (cx + 5.0, ny - 0.4)], 2.6)
        L.capsula("broche", [(cx - 8.2, ny + 13.6), (cx + 8.2, ny + 13.6)], 0.8)
    else:
        L.poligono("capa", [(cx + 4, ny), (cx - 4, ny + 0.4), (cx - 15, ny + 7.4), (cx - 17.2, ny + 14.2), (cx - 5, ny + 13.0), (cx + 5, ny + 8)])
        L.capsula("capa", [(cx - 4.4, ny - 0.4), (cx + 4.4, ny - 0.4)], 2.6)
        L.capsula("broche", [(cx - 16.2, ny + 13.2), (cx - 4.6, ny + 12.2)], 0.8)


def foulard(L, ps):
    """Pañuelo de seda al cuello, atado de lado, con las dos puntas largas que vuelan hacia atrás."""
    C, cab = ps["C"], ps["cab"]
    a, b, px, py = g8._cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.5, a[1] + (b[1] - a[1]) * 0.5)
    e1, e2 = (m[0] - 4.6 * px, m[1] - 4.6 * py), (m[0] + 4.6 * px, m[1] + 4.6 * py)
    L.capsula("capa", [e1, e2], 3.0)
    at = e1 if e1[0] < e2[0] else e2
    v = ps.get("viento", 0.3)
    s = ps.get("semilla", 0)
    ola = 0.7 * math.sin(s * 1.7)
    L.elipse("broche", at[0] + 0.2, at[1] + 0.4, 1.6, 1.4)                       # el nudo
    for k, (lx, ly, w) in enumerate(((-9.6, 1.2, 2.0), (-7.6, 4.6, 1.8))):
        f = 0.4 + 0.9 * v
        q0 = (at[0], at[1] + 0.4)
        q1 = (at[0] + lx * 0.5 * f - 0.6, at[1] + ly * 0.6 + (1 - f) * 2.4 + ola * (k + 1) * 0.5)
        q2 = (at[0] + lx * f - 1.0, at[1] + ly + (1 - f) * 4.0 - ola * (k + 0.5) * 0.6)
        L.capsula("capa", [q0, q1, q2], w)
        L.poligono("capa", [(q2[0] + 0.4, q2[1] - w * 0.7), (q2[0] - 1.4, q2[1] + 0.2), (q2[0] + 0.4, q2[1] + w * 0.7)])


def foulard_espalda(L, cx, ny, todo):
    L.capsula("capa", [(cx - 4.8, ny), (cx + 4.8, ny)], 2.4)
    if todo:
        L.elipse("broche", cx + 3.2, ny + 0.6, 1.2, 1.0)
        L.capsula("capa", [(cx + 3.2, ny + 0.6), (cx + 4.4, ny + 4.6), (cx + 4.0, ny + 8.2)], 1.5)
        L.capsula("capa", [(cx + 3.2, ny + 0.6), (cx + 1.8, ny + 4.4), (cx + 2.4, ny + 7.0)], 1.3)
    else:
        L.elipse("broche", cx - 3.6, ny + 0.6, 1.2, 1.0)
        L.capsula("capa", [(cx - 3.6, ny + 0.6), (cx - 8.0, ny + 2.4), (cx - 11.0, ny + 2.2)], 1.5)
        L.capsula("capa", [(cx - 3.6, ny + 0.6), (cx - 7.0, ny + 4.4), (cx - 9.8, ny + 5.0)], 1.3)


_lado_prev = g17._lado16
_frente_prev = g8.sombrero_frente


def sombrero_lado(L, cx, cy, inc, som):
    if som == "pañoleta":
        return pañoleta_lado(L, cx, cy, inc)
    if som == "capucha":
        ps = _ps()
        if ps: chaqueta(L, ps)
        return capucha_lado(L, cx, cy, inc)
    if som == "foulard":
        ps = _ps()
        if ps: foulard(L, ps)
        return
    return _lado_prev(L, cx, cy, inc, som)


def sombrero_frente(L, cx, cy, som, desv=0.0):
    # dibujar_giro: de espaldas, cabeza en (32, 28) y cuello en (32, 34.5); en tres cuartos, cabeza en (41.5, 28) y cuello en (40.5, 33.5).
    todo = desv == 0
    ncx, ny = (cx, cy + 6.5) if todo else (cx - 1.0, cy + 5.5)
    if som == "pañoleta":
        return pañoleta_frente(L, cx, cy, desv)
    if som == "capucha":
        chaqueta_espalda(L, ncx, ny, todo)
        return capucha_frente(L, cx, cy, desv)
    if som == "foulard":
        return foulard_espalda(L, ncx, ny, todo)
    return _frente_prev(L, cx, cy, som, desv)


g17._lado16 = sombrero_lado
g8.sombrero_frente = sombrero_frente

# =====================================================================
#  Materiales
# =====================================================================


def _ruido(forma, semilla, suave):
    rng = np.random.default_rng(semilla)
    r = Image.fromarray(((rng.normal(0, 1, forma) * 50 + 128).clip(0, 255)).astype(np.uint8))
    if suave:
        r = r.filter(ImageFilter.GaussianBlur(suave))
    return (np.asarray(r, np.float32) - 128) / 50


def pintar_carta(g, t, cel):
    """Pirata: grabado en sepia como en las cartas náuticas. Aguada sobre pergamino, contorno de tinta y las sombras en
    achurado horizontal (como el mar de las cartas); manchas de humedad aquí y allá."""
    s = cel * SS
    pap, tin = t["papel"], t["tinta_c"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    w = max(1, round(0.2 * s))
    mancha = hx("B89A5E")
    for r, c, v in g8.celdas(g):
        col = t["col"][v]; x0, y0 = c * s, r * s
        if v == "K":
            d.rectangle([x0, y0, x0 + s, y0 + s], fill=tin + (255,)); continue
        if v in "OE":
            d.rectangle([x0, y0, x0 + s, y0 + s], fill=col + (255,)); continue
        rnd = random.Random(r * 977 + c * 131)
        base = pap if v in "LHY" else mezcla(pap, col, 0.8)
        if rnd.random() < 0.07:
            base = mezcla(base, mancha, 0.22)
        d.rectangle([x0, y0, x0 + s, y0 + s], fill=base + (255,))
        tinta_suave = mezcla(tin, base, 0.35)
        if v in "SDTBZ":
            d.line([x0, y0 + 0.55 * s, x0 + s, y0 + 0.55 * s], fill=tinta_suave + (255,), width=w)
            if v in "DTZ":
                d.line([x0, y0 + 0.12 * s, x0 + s, y0 + 0.12 * s], fill=tinta_suave + (255,), width=w)
        elif v == "W":
            d.line([x0, y0 + 0.5 * s, x0 + s, y0 + 0.5 * s], fill=mezcla(tin, col, 0.45) + (255,), width=w + 1)
        elif v in "UVX" and (r % 2 == 0):
            d.line([x0, y0 + 0.5 * s, x0 + s, y0 + 0.5 * s], fill=mezcla(tin, base, 0.6) + (255,), width=max(1, w - 1))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


MAGENTA, AMBAR, SODIO = hx("FF3D9A"), hx("FFB238"), hx("8CFF5A")


def pintar_neon(g, t, cel):
    """Ciudad neón: el gato negro solo se ve por la luz que le cae. El contorno exterior toma el magenta de los letreros
    por arriba y el ámbar del puesto por abajo, el pelo junto al borde lleva un reflejo de ese color y los ojos,
    la nariz y las franjas brillan. Devuelve RGB sobre negro (se suma a la banda, como una pantalla). Se pinta a la
    escala final, con el reflejo en cuatro tonos y el resplandor en escalones, para que el atlas no pese de más."""
    s = cel
    capa = Image.new("RGB", (N * s, N * s), (0, 0, 0)); d = ImageDraw.Draw(capa)
    luz_ = Image.new("RGB", (N * s, N * s), (0, 0, 0)); dl = ImageDraw.Draw(luz_)
    vacio = lambda r, c: not (0 <= r < N and 0 <= c < N) or g[r][c] == "."
    borde = {}
    for r, c, v in g8.celdas(g):
        if v != "K":
            continue
        nx = ny = 0.0; hay = False
        for dr in (-2, -1, 0, 1, 2):
            for dc in (-2, -1, 0, 1, 2):
                if (dr or dc) and vacio(r + dr, c + dc):
                    k = 1.0 / (dr * dr + dc * dc)
                    nx += dc * k; ny += dr * k
                    hay = hay or (abs(dr) + abs(dc) == 1)
        if not hay:
            continue
        m = math.hypot(nx, ny) or 1
        nx, ny = nx / m, ny / m
        f = round(max(0.0, min(1.0, 0.5 + 0.62 * ny + 0.3 * nx)) * 3) / 3
        borde[(r, c)] = mezcla(MAGENTA, AMBAR, f)
    for r, c, v in g8.celdas(g):
        x0, y0 = c * s, r * s
        if (r, c) in borde:
            col = borde[(r, c)]
            dl.rectangle([x0, y0, x0 + s - 1, y0 + s - 1], fill=col)
        elif v == "K":
            col = t["col"]["D"]
        else:
            col = t["col"][v]
            cerca = [borde[q] for q in ((r - 1, c), (r + 1, c), (r, c - 1), (r, c + 1)) if q in borde]
            if cerca and v in "PLWSDTABMZ":
                rc_ = tuple(sum(x[i] for x in cerca) / len(cerca) for i in range(3))
                col = mezcla(col, rc_, 0.3)
            if v in "EGNH":
                dl.rectangle([x0, y0, x0 + s - 1, y0 + s - 1], fill=col)
        d.rectangle([x0, y0, x0 + s - 1, y0 + s - 1], fill=col)
    brillo = np.asarray(luz_.filter(ImageFilter.GaussianBlur(s * 0.85)), np.float32) * 0.66
    brillo = (brillo // 12 * 12).astype(np.uint8)
    return ImageChops.add(capa, Image.fromarray(brillo, "RGB"))


_textura_fresco = {}


def pintar_fresco(g, t, cel):
    """Roma: pintado al fresco sobre estuco. Colores rebajados con cal, contorno de sinopia (rojo tierra), el pigmento
    disparejo como de brocha y, en algunas celdas, se cayó la pintura y asoma el revoque ocre."""
    s = cel * SS
    if s not in _textura_fresco:
        _textura_fresco[s] = _ruido((N * s, N * s), 5, s * 0.6)
    mancha = _textura_fresco[s]
    cal, revoque = t["cal"], t["revoque"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    for r, c, v in g8.celdas(g):
        col = t["col"][v]; x0, y0 = c * s, r * s
        rnd = random.Random(r * 557 + c * 71 + 3)
        if v not in "KEO":
            col = mezcla(col, cal, 0.1)
            col = g8.esc(col, 1 + rnd.uniform(-0.035, 0.035))
            if v in "PW" and rnd.random() < 0.02:
                col = mezcla(col, revoque, 0.45)
        d.rectangle([x0, y0, x0 + s, y0 + s], fill=col + (255,))
    a = np.asarray(img, np.float32)
    k = 1 + np.round(0.075 * mancha / 0.035) * 0.035                 # manchas de pigmento, en pocos escalones
    a[..., :3] = np.clip(a[..., :3] * k[..., None], 0, 255)
    img = Image.fromarray(a.astype(np.uint8), "RGBA")
    return img.resize((N * cel, N * cel), Image.BOX)


_pintar_prev = g8.pintar


def pintar(g, t, cel, oscuro=False):
    m = t["material"]
    if m == "carta": return pintar_carta(g, t, cel)
    if m == "neon": return pintar_neon(g, t, cel)
    if m == "fresco": return pintar_fresco(g, t, cel)
    return _pintar_prev(g, t, cel, oscuro)


g8.pintar = pintar

# =====================================================================
#  Temas
# =====================================================================


def tema_pirata(o):
    sepia = hx("2A1C0F")
    papel = hx("F0E8D0") if not o else mezcla(hx("E9DEC3"), hx("D6AA4C"), 0.2)
    P = hx("DE8A3A")
    col = g8.pelaje(P, sepia, W=hx("9A4A18"), L=hx("F7EDD6"), S=mezcla(P, hx("7A3A14"), 0.4), D=mezcla(P, hx("5A2A10"), 0.55),
                    T=mezcla(P, hx("5A2A10"), 0.45), E=hx("2E7A6E"), N=hx("D98A7A"), C=hx("9A2A1C"), G=hx("C9A040"),
                    M=hx("A8301E"), Z=hx("6E1C12"), O=hx("1E140C"), J=hx("9A2A1C"))
    col["H"] = papel
    fondo = hx("E6DCBF") if not o else hx("100E0B")
    t = g8.T(col, fondo, hx("2A1C0F") if not o else hx("E9DEC3"), hx("AD9A70") if not o else hx("41382A"), "atigrado", "carta",
             ("ninguno", "pañoleta"), papel=papel, tinta_c=sepia)
    t.update(sombra_col=(60, 40, 20) if not o else (0, 0, 0), sombra_a=60 if not o else 75)
    return t


def tema_neon(o=True):
    P = hx("24162E")
    col = g8.pelaje(P, hx("0A0610"), W=hx("34203E"), L=hx("342840"), S=hx("1A1022"), D=hx("120A18"), T=hx("1C1226"),
                    E=SODIO, N=MAGENTA, R=hx("4A1838"), C=hx("6A5A80"), G=AMBAR, A=hx("16202E"), B=hx("0E141E"),
                    M=hx("16202E"), Z=hx("3A4E6A"), J=MAGENTA)
    col["H"] = hx("FFC8E4")
    t = g8.T(col, hx("0C0811"), hx("F4E7F0"), hx("4B2B47"), "mechones", "neon", ("chaqueta", "capucha"))
    t.update(pantalla=True, reflejo=MAGENTA)
    return t


def tema_urbe(o):
    sinopia = hx("5E2A1C")
    P = hx("9C978E")
    col = g8.pelaje(P, sinopia, W=hx("625C55"), L=hx("F1EADB"), S=hx("7A746C"), D=hx("5E5852"), T=hx("6E6862"), E=hx("8A9A3A"),
                    N=hx("C8746A"), A=hx("B2412A"), B=hx("7A2618"), G=hx("D9A23A"), C=hx("B2412A"), J=hx("3D5E45"))
    if o:   # de noche, la luz de sodio de las farolas lo entibia un poco
        col = g9.de_noche(col, hx("6A4428"), 0.12)
    fondo = hx("EFE6D3") if not o else hx("171412")
    t = g8.T(col, fondo, hx("2B2420") if not o else hx("F1E6D2"), hx("CDBB98") if not o else hx("433830"), "atigrado", "fresco",
             ("ninguno", "foulard"), cal=hx("F4EDDF"), revoque=hx("E0BE84"))
    t.update(sombra_col=(60, 40, 24) if not o else (0, 0, 0), sombra_a=55 if not o else 75)
    return t


_tema_prev = g8.tema


def tema(nombre, oscuro=False):
    _actual["tema"] = nombre
    if nombre not in MIOS:
        return _tema_prev(nombre, oscuro)
    t = {"pirata": tema_pirata, "neon": tema_neon, "urbe": tema_urbe}[nombre](oscuro)
    t["oscuro"] = oscuro
    t["ajustar"] = False
    col = dict(t["col"]); col.update(g17.colores(nombre, oscuro, 0, t))
    t["col"] = col
    return t


for _m in ("gato8", "gato9", "gato10", "gato12", "gato13", "gato16", "gato17", "gato18"):
    sys.modules[_m].tema = tema

# Ciudad neón no es una pantalla de terminal: sin líneas de escaneo y con una viñeta suave, sin las esquinas redondas.
_lineas_prev, _viñeta_prev, _viñeta9_prev = g8.lineas_escaneo, g8.viñeta_ciber, g9.viñeta
_vin_neon = {}


def viñeta_neon():
    if "v" not in _vin_neon:
        Wp, Hp = W * ESC, H * ESC
        yy, xx = np.mgrid[0:Hp, 0:Wp].astype(np.float32)
        nx, ny = (xx - Wp / 2) / (Wp / 2), (yy - Hp / 2) / (Hp / 2)
        v = np.clip(1.0 - 0.16 * nx ** 6 - 0.2 * np.clip(-ny, 0, 1) ** 3, 0, 1)
        m = Image.fromarray((v * 255).astype(np.uint8))
        _vin_neon["v"] = Image.merge("RGB", (m, m, m))
    return _vin_neon["v"]


def lineas_escaneo(img):
    return img if _actual["tema"] == "neon" else _lineas_prev(img)


def viñeta_ciber():
    return viñeta_neon() if _actual["tema"] == "neon" else _viñeta_prev()


def viñeta9():
    return viñeta_neon() if _actual["tema"] == "neon" else _viñeta9_prev()


g8.lineas_escaneo, g8.viñeta_ciber, g9.viñeta = lineas_escaneo, viñeta_ciber, viñeta9

# =====================================================================
#  Las dos acciones de cada tema
# =====================================================================
brazo = g17.brazo


def cofre(k):
    """Pirata, sentado: abre el cofre que tiene delante, mete la pata, saca una moneda y la mira brillar."""
    p = dict(g8.SENTADO)
    PATA = [(49.4, 52.2), (51.6, 49.8), (51.4, 53.0), (49.6, 49.4), (45.8, 41.6), (45.6, 40.8)]
    INC = [15, 13, 17, 9, -6, -9]
    CAB = [(39.8, 29.4), (39.6, 29.2), (40.0, 29.6), (39.2, 28.8), (38.4, 27.6), (38.4, 27.4)]
    px, py = PATA[k]
    p.update(inc=INC[k], cab=CAB[k], ojo="medio" if k == 5 else "abierto")
    p["fn"] = brazo((36, 43), (px, py), (1.8, -0.4) if k < 4 else (1.4, 1.8))
    p["ff"] = brazo((32.5, 43), (41.8, SUELO), (0.6, 0.4))
    return p, dict(obj=(52.4, 55.4), pata=(px, py), k=k)


def fideos(k):
    """Ciudad neón, sentado: con el tazón en una pata y los palillos en la otra, sube los fideos y los sorbe."""
    p = dict(g8.SENTADO)
    PATA = [(46.6, 40.8), (47.6, 36.6), (46.8, 37.4), (46.4, 40.2)]
    PUNTA = [(45.6, 43.4), (47.2, 31.4), (44.6, 32.8), (46.0, 42.6)]
    INC = [12, 2, 14, 8]
    p.update(inc=INC[k], cab=(38.8, 28.0), ojo="cerrado" if k == 3 else ("medio" if k == 2 else "abierto"))
    p["ff"] = brazo((32.5, 43), (43.2, 44.8), (0.6, 1.6))
    p["fn"] = brazo((36, 43), PATA[k], (1.2, 1.6))
    return p, dict(obj=(45.8, 44.6), pata=PATA[k], punta=PUNTA[k], k=k)


def helado(k):
    """Roma, sentado: sostiene el cucurucho con las dos patas y lame el helado."""
    p = dict(g8.SENTADO)
    lame = k in (1, 2)
    p.update(inc=[2, 10, 13, 0][k], cab=(38.6, 27.8), ojo="cerrado" if lame else "abierto", lengua=lame)
    p["fn"] = brazo((36, 43), (45.4, 41.4), (0.6, 1.8))
    p["ff"] = brazo((32.5, 43), (43.0, 42.2), (0.6, 1.8))
    return p, dict(obj=(45.6, 38.4), k=k)


def acecho(k):
    """Roma, de pie: se agacha y menea la grupa acechando a una paloma que picotea; salta, y la paloma se va volando."""
    pt = g8.p_tra
    if k < 4:
        w = (-0.5, 0.5, -0.4, 0.4)[k]
        baja = 0.6 if k == 3 else 0.0
        p = g8.pose(H=(21 + w, 42.4 + baja * 0.4), rh=6.8, C=(39.0, 47.6 + baja), rc=7.4, cab=(48.4, 38.8 + baja), inc=12,
                    orejas=0.0, ojo="abierto",
                    fn=g8.p_del((41.0, 50.0 + baja), (47.0, SUELO), 0.6), ff=g8.p_del((37.2, 50.0 + baja), (43.4, SUELO), 0.6),
                    hn=pt((21 + w, 44.0), (22.0, SUELO)), hf=pt((24 + w, 44.0), (25.6, SUELO)),
                    cola=[(14.0 + w, 41.4), (10.0, 43.6), (6.6, 47.4), (4.8, 51.6), (5.2 + (k % 2) * 0.8, 55.0), (7.6 + (k % 2) * 1.0, 56.4 - (k % 2) * 1.4)],
                    colaw=g8.ANCHO_COLA)
    elif k == 4:
        p = g8.pose(H=(22.0, 40.0), rh=6.6, C=(40.0, 36.0), rc=7.8, cab=(48.6, 26.8), inc=-8, ojo="abierto", viento=0.8,
                    fn=[(42.4, 39.0), (47.0, 38.2), (50.4, 36.4), (53.4, 34.8)], ff=[(38.4, 39.4), (43.0, 40.4), (46.4, 39.4), (49.4, 38.6)],
                    hn=pt((21.0, 43.0), (17.6, SUELO), 0.0), hf=pt((23.6, 43.0), (20.8, SUELO)),
                    cola=[(15.0, 39.0), (10.4, 36.4), (6.6, 33.6), (3.6, 30.6), (2.4, 27.6), (3.0, 25.6)], colaw=g8.ANCHO_COLA)
    else:
        p = g8.pose(H=(23.0, 42.0), rh=6.6, C=(41.0, 46.0), rc=7.6, cab=(50.0, 36.0), inc=14, ojo="abierto", viento=0.4,
                    fn=g8.p_del((43.0, 48.4), (51.6, SUELO), 0.4), ff=g8.p_del((39.2, 48.4), (48.0, SUELO), 0.4),
                    hn=pt((23.0, 44.0), (22.4, SUELO)), hf=pt((26.0, 44.0), (26.0, SUELO)),
                    cola=[(16.0, 40.0), (11.4, 38.4), (7.6, 37.6), (4.6, 36.0), (3.2, 33.4), (3.8, 31.2)], colaw=g8.ANCHO_COLA)
    return p, dict(obj=(55.8, 57.0), k=k)


NUEVAS = dict(cofre=(cofre, 6, "sentado"), fideos=(fideos, 4, "sentado"), helado=(helado, 4, "sentado"), acecho=(acecho, 6, "quieto"))
g17.ACCIONES.update(NUEVAS)
g17.ESCALA.update({k: 1.0 for k in NUEVAS})

# --- Los objetos


def _rotp(h, dx, dy, a):
    return (h[0] + dx * math.cos(a) - dy * math.sin(a), h[1] + dx * math.sin(a) + dy * math.cos(a))


def o_cofre(L, a):
    x, y = a["obj"]; k = a["k"]
    R = g17.rect
    R(L, "b_U", x - 4.6, y - 2.0, x + 4.6, y + 3.4)                          # la caja
    for bx in (x - 3.1, x + 3.1):
        L.capsula("b_V", [(bx, y - 1.8), (bx, y + 3.2)], 1.0)                  # flejes de latón
    L.capsula("b_X", [(x - 4.6, y + 0.8), (x + 4.6, y + 0.8)], 0.45)
    ang = (0.0, 0.5, 1.75, 1.75, 1.75, 1.75)[k]
    hinge = (x + 4.6, y - 2.0)
    tapa = [(-9.2, 0.0), (-8.8, -2.2), (-6.4, -3.4), (-2.8, -3.4), (-0.4, -2.2), (0.0, 0.0)]
    if ang == 0:
        L.poligono("b_U", [(hinge[0] + dx, hinge[1] + dy) for dx, dy in tapa])
        L.capsula("b_V", [(x - 3.1, y - 2.0), (x - 3.1, y - 4.8)], 1.0); L.capsula("b_V", [(x + 3.1, y - 2.0), (x + 3.1, y - 4.8)], 1.0)
        R(L, "b_V", x - 0.9, y - 2.6, x + 0.9, y - 0.4); L.elipse("b_X", x, y - 1.3, 0.35, 0.5)   # cerradura
    else:
        if ang > 1:
            R(L, "b_X", x - 4.2, y - 2.6, x + 4.2, y - 1.4)                   # el hueco
            for j, (cx_, cy_) in enumerate(((-2.8, -2.6), (-0.9, -3.1), (1.1, -2.8), (2.8, -2.4), (0.0, -2.2))):
                L.elipse("b_Q", x + cx_, y + cy_, 1.3, 0.9)                    # monedas
        else:
            L.capsula("b_Q", [(x - 4.2, y - 2.4), (x + 4.0, y - 2.2)], 0.7)    # el brillo que se escapa
        pts = [_rotp(hinge, dx, dy, ang) for dx, dy in tapa]
        L.poligono("b_U", pts)
        for f in (0.16, 0.84):
            q0 = _rotp(hinge, -9.2 * f, 0.0, ang); q1 = _rotp(hinge, -9.2 * f, -3.2, ang)
            L.capsula("b_V", [q0, q1], 1.0)
        R(L, "b_V", x - 0.9, y - 2.6, x + 0.9, y - 0.4)
    if k >= 3:                                                                 # la moneda en la pata
        px, py = a["pata"]
        L.elipse("f_V", px + 1.4, py - 1.6, 1.8, 1.8)
        L.elipse("f_Q", px + 1.1, py - 1.9, 1.0, 1.0)
    if k >= 4:                                                                 # el destello
        px, py = a["pata"]; e = 1.6 if k == 4 else 2.4
        L.capsula("f_Q", [(px + 3.2, py - 3.8 - e), (px + 3.2, py - 3.8 + e)], 0.45)
        L.capsula("f_Q", [(px + 3.2 - e, py - 3.8), (px + 3.2 + e, py - 3.8)], 0.45)


def m_gaviota(L, a):
    x, y = a["obj"]; k = a["k"]
    arriba = k % 2 == 0
    L.poligono("b_U", [(x - 2.4, y - 0.4), (x - 5.2, y - 0.6), (x - 4.8, y + 0.6), (x - 2.2, y + 0.8)])      # cola
    L.elipse("b_U", x, y, 2.8, 1.25)
    L.elipse("b_U", x + 2.6, y - 0.8, 1.2, 1.1)
    L.poligono("b_V", [(x + 3.5, y - 0.9), (x + 5.0, y - 0.6), (x + 3.5, y - 0.2)])                          # pico
    L.elipse("b_K", x + 2.9, y - 1.1, 0.3, 0.3)
    if arriba:
        ala = [(x - 1.8, y - 0.6), (x - 2.0, y - 3.0), (x - 4.4, y - 5.6), (x - 0.4, y - 3.4), (x + 1.4, y - 0.6)]
        L.poligono("b_X", ala); L.poligono("b_K", [(x - 4.4, y - 5.6), (x - 2.8, y - 4.8), (x - 3.4, y - 4.0)])
    else:
        ala = [(x - 1.8, y + 0.2), (x - 2.0, y + 2.4), (x - 4.2, y + 4.0), (x - 0.2, y + 2.6), (x + 1.4, y + 0.2)]
        L.poligono("b_X", ala); L.poligono("b_K", [(x - 4.2, y + 4.0), (x - 2.6, y + 3.4), (x - 3.0, y + 2.6)])


def o_fideos(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_U", [(x - 5.2, y - 1.2), (x + 5.2, y - 1.2), (x + 4.2, y + 2.2), (x + 1.8, y + 3.8), (x - 1.8, y + 3.8), (x - 4.2, y + 2.2)])
    L.capsula("b_V", [(x - 5.3, y - 1.2), (x + 5.3, y - 1.2)], 0.9)
    L.capsula("b_V", [(x - 1.6, y + 3.9), (x + 1.6, y + 3.9)], 0.7)
    L.elipse("b_Q", x, y - 1.8, 4.2, 0.9)
    for j in range(2):                                                         # vapor
        yy = y - 3.4 - ((k + j * 2) % 4) * 1.4
        L.elipse("b_C", x - 2.0 + j * 4.0 + 0.4 * math.sin(k + j), yy, 0.6, 0.85)
    px, py = a["pata"]; tx, ty = a["punta"]
    for d_ in (-0.35, 0.35):
        L.capsula("f_X", [(px + 1.4 + d_, py - 0.8), (tx + d_, ty)], 0.45)
    if k == 1:
        for j, dx in enumerate((-0.4, 0.3, 0.9)):
            L.capsula("f_Q", [(tx + dx, ty + 0.4), (tx + dx - 0.3 + 0.3 * j, ty + 4.0), (tx + dx + 0.2, ty + 7.6), (x - 0.6 + j, y - 1.6)], 0.42)
    elif k == 2:
        for j, dx in enumerate((-0.3, 0.4)):
            L.capsula("f_Q", [(tx + dx, ty + 0.2), (tx + 0.4 + dx, ty + 4.2), (x - 0.2 + j * 0.8, y - 1.6)], 0.42)


def m_dron(L, a):
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_X", [(x - 5.2, y - 1.6), (x + 5.2, y - 1.6)], 0.6)            # brazos
    for s_ in (-1, 1):
        L.capsula("b_X", [(x + s_ * 5.2, y - 1.6), (x + s_ * 5.2, y - 2.6)], 0.5)
        w = 2.2 if k % 2 else 1.0
        L.capsula("b_V", [(x + s_ * 5.2 - w, y - 2.8), (x + s_ * 5.2 + w, y - 2.8)], 0.45)   # hélices
    L.elipse("b_U", x, y - 0.8, 2.8, 1.5)
    if k % 3 != 2: L.elipse("b_C", x + 1.4, y - 0.8, 0.7, 0.6)                # la luz que parpadea
    L.capsula("b_K", [(x, y + 0.6), (x, y + 2.0)], 0.35)
    g17.rect(L, "b_Q", x - 2.2, y + 2.0, x + 2.2, y + 5.2)                     # el paquete
    L.capsula("b_G", [(x - 2.2, y + 3.6), (x + 2.2, y + 3.6)], 0.45)          # la cinta del paquete


def o_helado(L, a):
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_X", [(x - 2.2, y - 0.6), (x + 2.2, y - 0.6), (x - 0.4, y + 6.4)])           # cucurucho
    L.capsula("b_K", [(x - 1.6, y + 0.6), (x + 0.6, y + 3.8)], 0.3)
    L.capsula("b_K", [(x + 1.2, y + 0.4), (x - 0.8, y + 3.4)], 0.3)
    L.elipse("b_U", x, y - 1.4, 2.4, 1.7)                                       # fragola
    top = 1.9 if k in (1, 2) else 2.0
    L.elipse("b_V", x + 0.3, y - 3.4, top, 1.6)                                 # pistacho
    L.elipse("b_Q", x - 0.4, y - 4.0, 0.6, 0.4)
    if k == 3:
        L.elipse("b_U", x - 1.9, y + 0.4, 0.45, 0.8)                            # la gota que escurre


def o_paloma(L, a):
    x, y = a["obj"]; k = a["k"]
    if k == 4:
        x, y = x + 0.4, y - 7.0
    elif k == 5:
        x, y = x + 2.6, y - 21.0
    vuela = k >= 4
    L.poligono("b_X", [(x - 2.4, y - 0.2), (x - 5.0, y - 0.8 + (1.4 if vuela else 0)), (x - 4.6, y + 0.6 + (1.2 if vuela else 0)), (x - 2.2, y + 0.8)])
    L.elipse("b_U", x, y, 3.1, 2.0)
    pica = k in (0, 2)
    hx_, hy_ = (x + 2.8, y + 0.6) if pica else (x + 2.4, y - 1.8)
    L.capsula("b_V", [(x + 1.2, y - 0.4), (hx_ - 0.4, hy_ + 0.2)], 1.6)        # cuello tornasol
    L.elipse("b_U", hx_, hy_, 1.2, 1.1)
    L.poligono("b_K", [(hx_ + 0.8, hy_ - 0.2), (hx_ + 1.9, hy_ + (0.6 if pica else 0.2)), (hx_ + 0.8, hy_ + 0.4)])
    L.elipse("b_Q", hx_ + 0.2, hy_ - 0.2, 0.35, 0.35)
    if not vuela:
        L.elipse("b_X", x - 0.4, y - 0.2, 1.8, 1.0, -8)                         # ala plegada
        L.capsula("b_K", [(x - 1.6, y + 0.2), (x + 0.4, y + 0.0)], 0.3)
        for fx in (-0.4, 0.8):
            L.capsula("b_G", [(x + fx, y + 1.4), (x + fx + 0.2, y + 2.6)], 0.45)
    else:
        up = k == 4
        L.poligono("b_X", [(x - 1.4, y - 0.4), (x - 2.0, y + (-5.6 if up else 4.6)), (x + 0.2, y + (-4.4 if up else 3.4)), (x + 1.2, y - 0.4)])
        L.capsula("b_K", [(x - 1.2, y + (-3.0 if up else 2.2)), (x + 0.2, y + (-2.6 if up else 1.8))], 0.3)


g17.ESP.update({
    "pirata": [("cofre", o_cofre, "abre un cofre y saca una moneda"), ("mirar", m_gaviota, "sigue a una gaviota")],
    "neon": [("fideos", o_fideos, "sorbe fideos con palillos"), ("mirar", m_dron, "sigue al dron de reparto")],
    "urbe": [("helado", o_helado, "come un helado"), ("acecho", o_paloma, "persigue una paloma")],
})
C_ = lambda *hs: tuple(hx(h) for h in hs)
g17.COL.update({
    "pirata": C_("7A4A22", "C9A040", "2A1C0F", "F2C94C"),
    "neon": C_("C8284A", "FFB238", "D8B07A", "FFE59A"),
    "urbe": C_("E7899A", "A8C886", "D9A85A", "F6EBD0"),
})
g17.EXTRA_COL.update({
    ("pirata", 1): dict(U=hx("FBF8F0"), V=hx("E8A83A"), X=hx("9AA3A6"), Q=hx("FFFFFF")),
    ("neon", 1): dict(U=hx("4A3A66"), V=hx("A898D0"), X=hx("6A5A88"), Q=hx("D8A060"), C=hx("FF3D9A")),
    ("urbe", 1): dict(U=hx("8E97A8"), V=hx("6E9A86"), X=hx("6A7284"), Q=hx("E8A23A"), G=hx("D46A62")),
})
ESCALA_ESP = {("pirata", 0): 1.3, ("pirata", 1): 1.9, ("neon", 0): 1.0, ("neon", 1): 1.75, ("urbe", 0): 1.3, ("urbe", 1): 1.4}
g18.ESCALA_ESP.update(ESCALA_ESP)
for _n in MIOS:
    for _j, (_acc, _f, _) in enumerate(g17.ESP[_n]):
        _fn, _k, _ = g17.ACCIONES[_acc]
        _cs = []
        for _i in range(_k):
            _p, _a = _fn(_i)
            _a["accion"] = _acc
            _a["escala"] = ESCALA_ESP[(_n, _j)]
            _cs.append(dict(_p, objeto=(_f, _a), semilla=_i))
        g8.ANIMS[f"esp_{_n}_{_j}"] = _cs

# =====================================================================
#  Fondos (a 1 pixel por dp, ampliados ×3; lo que se mueve se pinta encima a la escala de la banda). Todo lo que se
#  mueve repite cada BUCLE cuadros, para que la app lo pase en un bucle corto.
# =====================================================================
BUCLE = 96


def _igual(im, antes):
    """Dónde la imagen sigue como estaba (para pintar las olas solo sobre el mar que quedó a la vista)."""
    return (np.asarray(im) == antes).all(2)


CALAVERA = ["..###..", ".#####.", ".#.#.#.", ".#####.", "..#.#..", "#.....#", ".#...#.", "..#.#..", "...#...", "..#.#..", ".#...#.",
            "#.....#"]


def barril(d, x, yb, madera, oscura, aro, tapa):
    w, h = 11, 15
    d.rounded_rectangle([x, yb - h, x + w, yb], radius=3, fill=madera)
    for k in (3, 6, 9):
        d.line([x + k, yb - h + 1, x + k, yb - 1], fill=oscura)
    d.line([x + w, yb - h + 2, x + w, yb - 2], fill=oscura)
    for yy in (yb - h + 2, yb - h + 7, yb - 3):
        d.line([x, yy, x + w, yy], fill=aro)
    d.ellipse([x + 1, yb - h - 1, x + w - 1, yb - h + 1], fill=tapa)


def fondo_pirata(t):
    """La cubierta: la borda con balaustres, el palo mayor con la vela aferrada y sus obenques, el farol, barriles, el
    timón, un cañón y la bandera a popa; detrás, el mar grabado en líneas como en las cartas, una isla y un barco.
    De día pasan gaviotas; de noche hay luna y estrellas y el farol está encendido. Las olas y la bandera se mueven."""
    o = t["oscuro"]
    noche = hx("0E1218")
    c = (lambda col: mezcla(col, noche, 0.58)) if o else (lambda col: col)
    im, d = lienzo(hx("C4D8D0"))
    estrellas_ = []
    if not o:
        degradado(im, 0, 40, [hx("B0CCC8"), hx("BED5CE"), hx("CCDDD2"), hx("DCE3D4"), hx("E8E4CE")])
        for x, y, w in ((20, 14, 30), (238, 9, 40), (330, 20, 26)):
            g9.nube(d, x, y, w, hx("F4EEDC"), hx("E4DCC4"), hx("CDC4A6"))
        mar = [hx("6A9C94"), hx("5C9089"), hx("4A8280"), hx("3E7676"), hx("336A6C"), hx("2A6064"), hx("22575D"), hx("1E5157")]
        cresta = hx("E8EEE2")
    else:
        degradado(im, 0, 40, [hx("080E15"), hx("0C1520"), hx("101C27"), hx("15242E"), hx("1C2E36")])
        estrellas_ = g9.estrellas(im, 31, 120, 0, 36, [hx("E9DEC3"), hx("AFA386"), hx("7DBFB6"), hx("5A6A74")],
                                  evitar=lambda x, y: abs(x - 150) < 10 and y < 22)
        cielo = im.getpixel((160, 6))
        d.ellipse([144, 6, 156, 18], fill=hx("E9DEC3"))
        d.ellipse([148, 4, 160, 16], fill=cielo)                     # la luna en cuarto
        luz(im, 150, 12, 22, hx("7DBFB6"), 0.12, 3)
        for x, y, w in ((40, 16, 26), (250, 10, 34)):
            g9.nube(d, x, y, w, hx("2E3E46"), hx("202E36"), hx("18242C"))
        mar = [hx("1E3A40"), hx("1A343A"), hx("163036"), hx("142B32"), hx("12272E"), hx("10232A"), hx("0E2026"), hx("0C1C22")]
        cresta = hx("6A9096")
    # El mar, grabado en líneas horizontales como en las cartas.
    px = im.load()
    for y in range(40, 64):
        f = (y - 40) / 23 * (len(mar) - 1)
        i = min(int(f), len(mar) - 2)
        for x in range(W):
            col = mar[i + 1] if tramar(f - i, x, y) else mar[i]
            px[x, y] = g8.esc(col, 0.93) if y % 2 else col
    d.line([0, 40, W, 40], fill=mezcla(mar[0], hx("FFFFFF") if not o else hx("7DBFB6"), 0.35))
    rnd = random.Random(8)
    for _ in range(70):                                              # crestas quietas
        x, y = rnd.randrange(W), rnd.randrange(42, 63)
        d.line([x, y, x + rnd.randint(1, 3), y], fill=mezcla(cresta, mar[min(7, (y - 40) // 3)], 0.5))
    # Una isla lejos con sus palmeras y un barco en el horizonte.
    isla = hx("6E8A76") if not o else hx("101C20")
    d.ellipse([166, 36, 214, 46], fill=isla)
    for (bx, by) in ((182, 37), (192, 36)):
        d.line([bx, by, bx + 1, by - 6], fill=isla)
        for dx, dy in ((-3, 1), (-2, -1), (2, -1), (3, 1), (0, -2)):
            d.line([bx + 1, by - 6, bx + 1 + dx, by - 6 + dy], fill=isla)
    mar_ = mar[0]
    d.rectangle([164, 41, 216, 46], fill=mar_)
    d.line([164, 41, 216, 41], fill=mezcla(mar_, isla, 0.5))
    casco, vela = (hx("5A4630"), hx("EFE6CE")) if not o else (hx("141A1E"), hx("3A4648"))
    d.polygon([(292, 38), (308, 38), (305, 41), (295, 41)], fill=casco)
    for mx, h_ in ((297, 11), (303, 9)):
        d.line([mx, 38, mx, 38 - h_], fill=casco)
        d.polygon([(mx - 3, 39 - h_), (mx + 3, 39 - h_), (mx + 3, 36), (mx - 3, 36)], fill=vela)
    antes = np.asarray(im).copy()
    madera, madera_c, madera_o = c(hx("8A5A2E")), c(hx("B07A44")), c(hx("5A3A1E"))
    sepia = c(hx("4A3422"))
    laton = hx("C9A040") if not o else hx("D6AA4C")
    # El palo mayor, la verga con la vela aferrada y los obenques con sus flechates.
    for xa, xbs in ((84, (60, 52, 44)), (89, (110, 118, 126))):
        for xb in xbs:
            d.line([xa, 7, xb, 49], fill=sepia)
    for y in range(13, 49, 4):
        f = (y - 7) / 42
        d.line([84 + (44 - 84) * f, y, 84 + (60 - 84) * f, y], fill=sepia)
        d.line([89 + (110 - 89) * f, y, 89 + (126 - 89) * f, y], fill=sepia)
    d.rectangle([84, 0, 89, 63], fill=madera); d.line([84, 0, 84, 63], fill=madera_c); d.line([89, 0, 89, 63], fill=madera_o)
    for yy in (18, 42):
        d.rectangle([83, yy, 90, yy + 1], fill=c(hx("3A2A1A")))
    d.rectangle([48, 2, 126, 3], fill=madera); d.line([48, 3, 126, 3], fill=madera_o)
    lona, lona_s = c(hx("F2E8CC")), c(hx("D8C8A0"))
    d.rounded_rectangle([52, 4, 122, 8], radius=2, fill=lona); d.line([54, 8, 120, 8], fill=lona_s)
    for x in range(58, 120, 10):
        d.line([x, 4, x, 8], fill=sepia)
    # El farol colgado del palo.
    hierro_f = c(hx("3A2A1A")) if not o else hx("4A3418")
    d.line([90, 26, 93, 26], fill=hierro_f); d.line([93, 26, 93, 28], fill=hierro_f)
    d.polygon([(90, 30), (96, 30), (93, 27)], fill=c(laton) if not o else laton)
    d.rectangle([90, 30, 96, 37], fill=hierro_f); d.rectangle([91, 31, 95, 36], fill=hx("E8DFC0") if not o else hx("FFE8A0"))
    d.line([93, 31, 93, 36], fill=hierro_f if not o else hx("C08A30"))
    d.rectangle([89, 37, 97, 38], fill=c(laton) if not o else laton)
    # La borda: pasamanos, balaustres torneados y la cubierta de tablones con juntas de brea.
    for x in range(1, W, 6):
        d.rectangle([x, 52, x + 1, 61], fill=madera)
        d.rectangle([x - 1, 55, x + 2, 57], fill=madera); d.point((x - 1, 55), fill=madera_c)
        d.line([x + 1, 52, x + 1, 61], fill=madera_o)
    d.rectangle([0, 49, W, 51], fill=madera); d.line([0, 49, W, 49], fill=madera_c); d.line([0, 51, W, 51], fill=madera_o)
    d.rectangle([0, 61, W, 63], fill=madera_o)
    tablas = [c(hx("B4A07E")), c(hx("A89272")), c(hx("BCA886")), c(hx("A08A6A"))]      # teca gastada por el sol y la sal
    brea = c(hx("3A2C1C"))
    for k, y0 in enumerate(range(64, 80, 3)):
        d.rectangle([0, y0, W, y0 + 2], fill=tablas[k % 4])
        d.line([0, y0, W, y0], fill=brea)
        for xj in range((k * 17) % 40, W, 40):
            d.line([xj, y0, xj, y0 + 2], fill=brea)
        for xc in range((k * 23 + 9) % 40, W, 40):
            d.point((xc, y0 + 1), fill=c(hx("6A5A44")))
    d.line([0, 64, W, 64], fill=c(hx("2A1A0E")))
    # Barriles, un rollo de cabo, el timón y el cañón con sus balas.
    for bx in (6, 19):
        barril(d, bx, 66, c(hx("9A6434")), c(hx("6E4420")), c(hx("3A2E26")), c(hx("B88050")))
    cabo, cabo_o = c(hx("C8A878")), c(hx("8A6E48"))
    for r_ in (6, 4, 2):
        d.ellipse([40 - r_, 65 - r_ * 0.45, 40 + r_, 65 + r_ * 0.45], fill=cabo, outline=cabo_o)
    tx, ty = 236, 50
    d.rectangle([tx - 2, ty, tx + 2, 66], fill=madera); d.line([tx - 2, ty, tx - 2, 66], fill=madera_c)
    d.rectangle([tx - 4, 63, tx + 4, 66], fill=madera_o)
    for k in range(8):
        a_ = k * math.pi / 4 + 0.2
        ex, ey = tx + 12 * math.cos(a_), ty + 12 * math.sin(a_)
        d.line([tx, ty, ex, ey], fill=madera_o, width=1)
        d.ellipse([ex - 1, ey - 1, ex + 1, ey + 1], fill=madera)
    d.ellipse([tx - 9, ty - 9, tx + 9, ty + 9], outline=madera, width=2)
    d.ellipse([tx - 7, ty - 7, tx + 7, ty + 7], outline=madera_o)
    d.ellipse([tx - 2, ty - 2, tx + 2, ty + 2], fill=c(laton))
    hierro, hierro_c = c(hx("2E2C2A")), c(hx("5A5652"))
    d.rectangle([302, 60, 324, 64], fill=madera_o); d.rectangle([304, 58, 322, 60], fill=madera)
    for wx in (306, 320):
        d.ellipse([wx - 3, 62, wx + 3, 68], fill=c(hx("3A2614"))); d.point((wx, 65), fill=c(hx("8A6A44")))
    d.polygon([(300, 54), (330, 55), (330, 58), (300, 60)], fill=hierro)
    d.line([301, 55, 329, 56], fill=hierro_c)
    d.rectangle([328, 54, 331, 59], fill=hierro); d.ellipse([297, 55, 301, 59], fill=hierro)
    for bx, by in ((338, 64), (341, 64), (344, 64), (339.5, 62), (342.5, 62), (341, 60)):
        d.ellipse([bx - 1.5, by - 1.5, bx + 1.5, by + 1.5], fill=hierro); d.point((bx - 0.5, by - 0.5), fill=hierro_c)
    # El asta de la bandera, a popa.
    d.rectangle([376, 4, 377, 63], fill=madera_o); d.point((376, 3), fill=c(laton))
    d.rectangle([373, 60, 380, 63], fill=madera)
    visible = _igual(im, antes)
    if o:   # la luz del farol: un halo cálido y un charco de luz en la cubierta
        luz(im, 93, 34, 34, hx("FFB45A"), 0.26, 5)
        luz(im, 93, 71, 52, hx("D6AA4C"), 0.22, 4, ry=8)
    big = subir(im)
    rnd = random.Random(12)
    crestas = []
    for _ in range(600):
        x, y = rnd.randrange(2, W - 4), rnd.randrange(41, 63)
        if visible[y, x:x + 3].all():
            crestas.append((x, y, rnd.randrange(8)))
        if len(crestas) >= 70:
            break
    titilan = estrellas_[::4]
    llamas = [random.Random(5 + k).random() for k in range(24)]
    negro = hx("1A1410")
    hueso = hx("F0E8D0") if not o else hx("B8B0A0")

    def bandera(dd, tick):
        for fx in range(24):
            u = fx / 23
            dy = 1.3 * math.sin(fx * 0.42 - (tick % 12) * 2 * math.pi / 12) * (0.25 + 0.75 * u)
            for fy in range(14):
                ex, ey = fx - 9, fy - 1
                blanco = 0 <= ey < len(CALAVERA) and 0 <= ex < 7 and CALAVERA[ey][ex] == "#"
                cuad(dd, 378 + fx, 5 + fy + dy, hueso if blanco else (negro if fy and fy < 13 else mezcla(negro, hueso, 0.15)))

    def gaviota(dd, x, y, ala):
        cuad(dd, x - 0.5, y, hx("FBF8F0"), 2, 1)
        for s_ in (-1, 1):
            for j in (1, 2, 3):
                cuad(dd, x + s_ * (j + 0.5) - 0.5, y - ala * j * 0.5, hx("9AA3A6") if j < 3 else hx("2A2420"))

    def vivo(img, tick, delante):
        if delante:
            return
        dd = ImageDraw.Draw(img)
        for x, y, ph in crestas:
            w_ = (1, 2, 3, 2, 0, 0, 0, 0)[((tick // 4) + ph) % 8]
            if w_:
                cuad(dd, x + (3 - w_) / 2, y, cresta, w_, 1)
        bandera(dd, tick)
        if not o:
            for cx_, cy_, rx, ry, sent, fase, paso in ((196, 16, 62, 6, 1, 0.0, 3), (300, 24, 38, 4, -1, 2.4, 4)):
                a_ = sent * (tick % BUCLE) * 2 * math.pi / BUCLE + fase
                ala = (1, 0, -1, 0)[(tick // paso) % 4]
                gaviota(dd, cx_ + rx * math.cos(a_), cy_ + ry * math.sin(a_), ala)
        else:
            for j, (sx, sy) in enumerate(titilan):
                if (j * 5 + tick // 4) % 8 == 0:
                    cuad(dd, sx - 1, sy, hx("FFF6DC"), 3, 1); cuad(dd, sx, sy - 1, hx("FFF6DC"), 1, 3)
            for k in range(8):                      # el reflejo de la luna en el agua
                on = ((tick // 3) + k * 3) % 4
                if on < 3 and visible[42 + k * 2, 150]:
                    cuad(dd, 146 + (k * 5 + on * 2) % 7, 42 + k * 2, hx("C8D8C8"), 2, 1)
            f = llamas[tick % 24]
            cuad(dd, 92.3, 32.6 - f * 0.6, hx("FFF6C8"), 1.4, 2.4 + f)
            cuad(dd, 92.6, 34.6, hx("FFB040"), 0.8, 1.2)
    g9.ANIM[("pirata", o)] = [vivo]
    return big


# ---------------------------------------------------------------------
#  Ciudad neón
# ---------------------------------------------------------------------
# Letras de los letreros (5×7 o 7×7), escritas a mano para que se lean a 1 pixel por dp: latín, cirílico, coreano,
# katakana y un kanji.
GLIFOS = {
    "H": ["#...#", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"],
    "O": [".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
    "T": ["#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."],
    "E": ["#####", "#....", "#....", "####.", "#....", "#....", "#####"],
    "L": ["#....", "#....", "#....", "#....", "#....", "#....", "#####"],
    "Б": ["#####", "#....", "#....", "####.", "#...#", "#...#", "####."],
    "А": [".###.", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"],
    "Р": ["####.", "#...#", "#...#", "####.", "#....", "#....", "#...."],
    "커": ["####..#", "...#..#", "####..#", "...#.##", "...#..#", "..#...#", ".#....#"],
    "피": ["#####.#", ".#.#..#", ".#.#..#", ".#.#..#", ".#.#..#", "#####.#", "......#"],
    "ラ": [".#####.", ".......", "#######", "......#", ".....#.", "....#..", "..##..."],
    "ー": ["...#...", "...#...", "...#...", "...#...", "...#...", "...#...", "...#..."],
    "メ": ["......#", ".....#.", ".#..#..", "..##...", "..#.#..", ".#...#.", "#......"],
    "ン": ["##.....", ".......", "......#", ".....#.", "....#..", "..##...", "##....."],
    "下": ["#######", "...#...", "...##..", "...#.#.", "...#...", "...#...", "...#..."],
    "-": [".....", ".....", ".....", "####.", ".....", ".....", "....."],
    "3": ["####.", "....#", "....#", ".###.", "....#", "....#", "####."],
    "▸": ["#....", "##...", "###..", "####.", "###..", "##...", "#...."],
}


def glifo(d, x, y, letra, col):
    for j, fila in enumerate(GLIFOS[letra]):
        for i, ch in enumerate(fila):
            if ch == "#":
                d.point((x + i, y + j), fill=col)


BUCLE_N = 48


def fondo_neon(t):
    """Los niveles bajos: una pasarela arriba, muros de ladrillo con aparatos de aire, el fondo del callejón con su
    escalera de incendios y, a la derecha, el puesto de fideos con su toldo, sus faroles de papel y la olla humeante.
    Letreros de neón en latín, cirílico, coreano, katakana y kanji; el piso mojado los refleja. Llueve, sale vapor de
    la olla y de una rejilla y algunos letreros parpadean."""
    im, d = lienzo(hx("0C0811"))
    degradado(im, 0, 16, [hx("2A1436"), hx("22102E"), hx("1A0C24"), hx("140A1C")])
    rnd = random.Random(21)
    # Torres lejanas entre la neblina, con ventanitas.
    for x0 in range(0, W, 26):
        w_ = rnd.randint(14, 24); h_ = rnd.randint(4, 12)
        d.rectangle([x0, 14 - h_, x0 + w_, 14], fill=hx("1C0E26"))
        for _ in range(3):
            d.point((x0 + rnd.randint(1, w_ - 1), 14 - rnd.randint(1, h_)), fill=rnd.choice([hx("6A3A50"), hx("7A5A30"), hx("4A3A6A")]))
    # La pasarela, con su baranda y sus luces.
    d.rectangle([0, 12, W, 16], fill=hx("0A0610")); d.line([0, 12, W, 12], fill=hx("2E2040"))
    for x in range(0, W, 5):
        d.line([x, 9, x, 12], fill=hx("1E1428"))
    d.line([0, 9, W, 9], fill=hx("2A1C38"))
    for x in range(10, W, 34):
        d.point((x, 16), fill=hx("FFB238")); d.point((x, 17), fill=hx("6A4A20"))
    # Muros. Izquierda: ladrillo; derecha: paneles de concreto.
    lad, mortero = hx("1E1426"), hx("140C1A")
    d.rectangle([0, 17, 138, 66], fill=lad)
    for y in range(17, 66, 3):
        d.line([0, y, 138, y], fill=mortero)
        for x in range((y // 3 % 2) * 4, 139, 8):
            d.line([x, y, x, y + 2], fill=mortero)
    pan, junta = hx("1A1524"), hx("110D18")
    d.rectangle([230, 17, W, 66], fill=pan)
    for x in range(230, W, 22):
        d.line([x, 17, x, 66], fill=junta)
    for y in (30, 46):
        d.line([230, y, W, y], fill=junta)
    for x in range(232, W, 22):
        for y in (19, 32, 48):
            d.point((x + 1, y), fill=hx("2A2236"))
    # El fondo del callejón: muro lejano más oscuro, una puerta encendida y la escalera de incendios en zigzag.
    d.rectangle([139, 17, 229, 66], fill=hx("120C1A"))
    d.polygon([(139, 17), (152, 22), (152, 66), (139, 66)], fill=hx("180F20"))
    d.polygon([(229, 17), (216, 22), (216, 66), (229, 66)], fill=hx("161220"))
    d.rectangle([152, 22, 216, 60], fill=hx("0E0A14"))
    d.rectangle([176, 44, 186, 60], fill=hx("3A2414")); d.rectangle([178, 46, 184, 60], fill=hx("6A4420"))
    luz(im, 181, 53, 16, hx("FFB238"), 0.16, 3)
    esc_ = hx("2A2036")
    for k, (y0, y1) in enumerate(((24, 34), (34, 44), (44, 54))):
        xa, xb = (192, 212) if k % 2 == 0 else (212, 192)
        d.line([xa, y0, xb, y1], fill=esc_); d.line([190, y1, 214, y1], fill=esc_)
        for s_ in range(5):
            f = s_ / 4
            d.point((round(xa + (xb - xa) * f), round(y0 + (y1 - y0) * f) + 1), fill=hx("3A2E4A"))
    d.line([152, 60, 216, 60], fill=hx("1E1628"))
    # Aparatos de aire y tubos en la pared de ladrillo; una puerta con su letrero.
    for ax, ay in ((40, 22), (66, 34), (118, 24)):
        d.rectangle([ax, ay, ax + 10, ay + 7], fill=hx("2E2638")); d.rectangle([ax + 1, ay + 1, ax + 9, ay + 6], fill=hx("221C2C"))
        for gx in range(ax + 2, ax + 9, 2):
            d.line([gx, ay + 2, gx, ay + 5], fill=hx("3A3248"))
        d.line([ax + 5, ay + 7, ax + 5, 66], fill=hx("241C2E"))
    d.line([132, 17, 132, 66], fill=hx("2A2236")); d.line([133, 17, 133, 66], fill=hx("161020"))
    d.rectangle([96, 46, 114, 66], fill=hx("120A16")); d.rectangle([98, 48, 112, 66], fill=hx("1A1220"))
    d.line([105, 48, 105, 66], fill=hx("120A16"))
    # Cables cruzados con foquitos.
    for (xa, ya, xb, yb, caida) in ((0, 19, 140, 22, 6), (140, 22, 300, 18, 8), (220, 19, 412, 24, 5)):
        pts = [(xa + (xb - xa) * f, ya + (yb - ya) * f + caida * math.sin(math.pi * f)) for f in np.linspace(0, 1, 60)]
        d.line(pts, fill=hx("06040A"))
        for f in (0.2, 0.45, 0.7, 0.9):
            x_, y_ = xa + (xb - xa) * f, ya + (yb - ya) * f + caida * math.sin(math.pi * f)
            if not 228 < x_ < 266:
                d.point((round(x_), round(y_) + 1), fill=hx("FFD9A0"))
    # El puesto de fideos.
    d.rectangle([286, 36, 390, 54], fill=hx("2A180A"))
    degradado(im, 36, 54, [hx("2E1A0A"), hx("38220C"), hx("442A0E")], 288, 389)
    for bx in range(296, 330, 5):                                            # botellas en la repisa
        d.rectangle([bx, 40, bx + 1, 44], fill=rnd.choice([hx("2A4A2A"), hx("5A2A1A"), hx("7A5A2A")]))
    d.line([292, 45, 330, 45], fill=hx("2A1A0C"))
    d.rectangle([336, 44, 350, 54], fill=hx("4A4A52")); d.rectangle([335, 43, 351, 44], fill=hx("6A6A72"))   # la olla
    d.rectangle([358, 46, 380, 54], fill=hx("2A1A0C"))
    for lx in (362, 368, 374):
        d.line([lx, 38, lx, 44], fill=hx("8A8A92")); d.ellipse([lx - 1, 44, lx + 1, 46], fill=hx("8A8A92"))   # cucharones
    d.rectangle([284, 54, 392, 55], fill=hx("9A7444")); d.line([284, 56, 392, 56], fill=hx("4A3018"))
    d.rectangle([286, 57, 390, 66], fill=hx("24140C"))
    for x in range(288, 390, 4):
        d.line([x, 57, x, 66], fill=hx("1A0E08"))
    for sx in (300, 324, 348, 372):                                           # bancos
        d.rectangle([sx - 3, 59, sx + 3, 60], fill=hx("5A3A6A")); d.line([sx, 61, sx, 66], fill=hx("2A1E30"))
    for k, x in enumerate(range(284, 392, 6)):                                # toldo a rayas
        d.polygon([(x, 29), (x + 6, 29), (x + 7, 35), (x + 1, 35)], fill=hx("6A1A40") if k % 2 else hx("2A1022"))
    d.line([284, 35, 392, 35], fill=hx("FF3D9A"))
    for k, x in enumerate(range(288, 388, 17)):                               # noren
        d.rectangle([x, 36, x + 15, 42], fill=hx("4A1428")); d.line([x, 41, x + 15, 41], fill=hx("D8C0C8"))
    d.rectangle([392, 6, 393, 66], fill=hx("1E1628"))                         # el poste del letrero
    # Piso mojado, con charcos.
    d.rectangle([0, 66, W, H], fill=hx("0E0A14")); d.line([0, 66, W, 66], fill=hx("2A2236"))
    for (cx_, cy_, rx) in ((64, 73, 26), (204, 76, 34), (330, 72, 30)):
        d.ellipse([cx_ - rx, cy_ - 2.5, cx_ + rx, cy_ + 2.5], fill=hx("160F20"))
    d.rectangle([178, 69, 194, 71], fill=hx("1A1622"))                        # rejilla
    for gx in range(179, 194, 2):
        d.point((gx, 70), fill=hx("050308"))
    # Reflejos de los letreros y del puesto en el piso (verticales, tramados).
    px = im.load()
    rr = random.Random(5)
    for (x0, x1, col, k) in ((10, 21, MAGENTA, 0.5), (233, 261, SODIO, 0.36), (394, 406, AMBAR, 0.45), (288, 390, hx("C07A30"), 0.2),
                             (92, 118, AMBAR, 0.32), (176, 186, AMBAR, 0.25)):
        for x in range(x0, x1):
            if rr.random() < 0.3:
                continue
            largo = rr.randint(3, 13)
            for y in range(67, min(H, 67 + largo)):
                if (y + x) % 3 == 0 and y > 69:
                    continue                                              # el agua corta el reflejo en rayitas
                f = k * (1 - (y - 67) / largo)
                px[x, y] = mezcla(px[x, y], col, f)
    # Halos de los letreros en la pared.
    for (cx_, cy_, r_, col, f_) in ((15, 36, 18, MAGENTA, 0.16), (247, 25, 18, SODIO, 0.12), (399, 24, 16, AMBAR, 0.16), (106, 41, 14, AMBAR, 0.14),
                                    (286, 33, 9, hx("FF6A3A"), 0.2), (390, 33, 9, hx("FF6A3A"), 0.2), (338, 48, 30, hx("FFB238"), 0.08)):
        luz(im, cx_, cy_, r_, col, f_, 4)
    for lx in (286, 390):                                                     # faroles de papel
        d.ellipse([lx - 3, 28, lx + 3, 36], fill=hx("E0482A")); d.line([lx - 3, 32, lx + 3, 32], fill=hx("A82A1A"))
        d.point((lx - 1, 30), fill=hx("FFC890"))
    # Los letreros (tubos de neón), en su propia capa: se amplían y se les pone el resplandor.
    letreros = {}

    def capa_letrero(nombre, dibujar):
        m = Image.new("RGB", (W, H), (0, 0, 0)); dibujar(ImageDraw.Draw(m)); letreros[nombre] = m

    def hotel(dl):
        dl.rectangle([9, 18, 21, 61], outline=hx("6A1A40"))
        for k, ch in enumerate("HOTEL"):
            if ch != "T":
                glifo(dl, 13, 20 + k * 8, ch, MAGENTA)

    capa_letrero("hotel", hotel)
    capa_letrero("hotel_t", lambda dl: glifo(dl, 13, 36, "T", MAGENTA))

    def bar(dl):
        for k, ch in enumerate("БАР"):
            glifo(dl, 92 + k * 7, 36, ch, AMBAR)

    capa_letrero("bar", bar)
    capa_letrero("bar_flecha", lambda dl: glifo(dl, 113, 36, "▸", AMBAR))

    def cafe(dl):
        dl.rectangle([234, 19, 260, 31], outline=SODIO)
        glifo(dl, 237, 22, "커", SODIO); glifo(dl, 249, 22, "피", SODIO)

    capa_letrero("cafe", cafe)

    def ramen(dl):
        dl.rectangle([394, 6, 405, 42], outline=hx("8A5A1A"))
        for k, ch in enumerate("ラーメン"):
            glifo(dl, 396, 8 + k * 8, ch, AMBAR if ch != "ー" else MAGENTA)

    capa_letrero("ramen", ramen)

    def nivel(dl):
        glifo(dl, 158, 26, "下", hx("C8B8E8")); glifo(dl, 167, 26, "-", hx("C8B8E8")); glifo(dl, 172, 26, "3", hx("C8B8E8"))

    capa_letrero("nivel", nivel)
    base = subir(im)

    def con_tubos(apagados=()):
        tub = Image.new("RGB", (W, H), (0, 0, 0))
        for nombre, m in letreros.items():
            if nombre not in apagados:
                tub = ImageChops.add(tub, m)
        t3 = tub.resize((W * ESC, H * ESC), Image.NEAREST)
        a = np.asarray(t3, np.float32)
        nucleo = np.zeros_like(a)
        nucleo[1::ESC, 1::ESC] = a[1::ESC, 1::ESC]                            # el centro del tubo, casi blanco
        brillo = np.asarray(t3.filter(ImageFilter.GaussianBlur(5)), np.float32) * 1.6
        out = np.asarray(base, np.float32) + a * 0.85 + nucleo * 0.6 + brillo
        apag = [m for nombre, m in letreros.items() if nombre in apagados]
        if apag:                                                              # el tubo apagado se ve como vidrio oscuro
            off = np.asarray(ImageChops.add(*apag) if len(apag) > 1 else apag[0], np.float32)
            off = np.repeat(np.repeat(off, ESC, 0), ESC, 1)
            out = np.where(off.sum(2, keepdims=True) > 0, out * 0.4 + off * 0.18, out)
        return Image.fromarray(np.clip(out, 0, 255).astype(np.uint8))

    big = con_tubos()
    cajas = {"hotel_t": (8 * ESC, 30 * ESC, 26 * ESC, 50 * ESC), "bar_flecha": (106 * ESC, 30 * ESC, 124 * ESC, 50 * ESC)}
    parches = {k: con_tubos((k,)).crop(cajas[k]) for k in cajas}
    APAGA_T = {7, 8, 11, 30, 31, 33, 40}
    rnd = random.Random(30)
    gotas = [(rnd.uniform(0, W + 20), rnd.uniform(0, 84), rnd.choice((0.8, 1.0, 1.0, 1.3))) for _ in range(30)]
    ondas_ = [(x, y, rnd.randrange(8)) for x, y in ((48, 73), (70, 72), (82, 74), (186, 76), (210, 75), (226, 77), (312, 72), (334, 71),
                                                     (350, 73))]                # gotas que caen en los charcos
    v = 84 * 3 / BUCLE_N

    def vivo(img, tick, delante):
        if delante:
            return
        if tick % BUCLE_N in APAGA_T:
            img.paste(parches["hotel_t"], cajas["hotel_t"][:2])
        if (tick // 6) % 2:
            img.paste(parches["bar_flecha"], cajas["bar_flecha"][:2])
        dd = ImageDraw.Draw(img)
        g9.humo(img, tick % BUCLE_N, 343, 42, hx("8A7A9A"), hx("2A1C30"), n=6, sube=0.5, ancho=1.2, semilla=1, alto=24)
        g9.humo(img, tick % BUCLE_N, 186, 69, hx("6A5A80"), hx("140E1C"), n=8, sube=0.5, ancho=2.0, semilla=4, alto=24)
        for x0, y0, k in gotas:
            y = (y0 + (tick % BUCLE_N) * v) % 84 - 4
            x = x0 - (y + 4) * 0.25
            col = hx("8A7AAA") if k < 1.2 else hx("B8A8D8")
            dd.line([x * ESC, y * ESC, (x - 0.8) * ESC, (y + 3.2 * k) * ESC], fill=col, width=1)
        for x, y, ph in ondas_:
            s_ = ((tick // 3) + ph) % 8
            if s_ < 4:
                r_ = 1 + s_ * 1.2
                dd.ellipse([(x - r_) * ESC, (y - r_ * 0.3) * ESC, (x + r_) * ESC, (y + r_ * 0.3) * ESC], outline=hx("5A4A7A") if s_ < 2 else hx("3A2E4E"))
    g9.ANIM[("neon", True)] = [vivo]
    return big


# ---------------------------------------------------------------------
#  Roma de hoy
# ---------------------------------------------------------------------

def ventana(d, x, y, w, h, marco, vidrio, persiana, luz_=None, maceta=None):
    """Ventana con marco de travertino, persianas verdes abiertas a los lados y, si hay, una maceta de geranios."""
    d.rectangle([x - 1, y - 1, x + w, y + h], fill=marco)
    d.rectangle([x, y, x + w - 1, y + h - 1], fill=luz_ or vidrio)
    d.line([x + w // 2, y, x + w // 2, y + h - 1], fill=marco)
    for sx in (x - 4, x + w + 1):
        d.rectangle([sx, y, sx + 2, y + h - 1], fill=persiana)
        for yy in range(y + 1, y + h - 1, 2):
            d.line([sx, yy, sx + 2, yy], fill=g8.esc(persiana, 0.8))
    d.rectangle([x - 2, y + h, x + w + 1, y + h + 1], fill=marco)
    if maceta:
        d.rectangle([x + 1, y + h - 2, x + w - 2, y + h - 1], fill=maceta[0])
        for k in range(x + 1, x + w - 1, 2):
            d.point((k, y + h - 3), fill=maceta[1]); d.point((k + 1, y + h - 4), fill=maceta[2])


def fondo_urbe(t):
    """Un vicolo del centro: a la izquierda un palazzo ocre con persianas verdes, la placa de mármol de la calle y el
    nasone; al centro, la vista abierta a los tejados, los pinos y la cúpula, y abajo las ruinas con sus columnas
    rotas y sus gatos, tras la reja; a la derecha, una fachada rojo pompeyano con balcón, farola y una motoneta.
    Ropa tendida de lado a lado. Arriba vuela una paloma y otras picotean el empedrado. De noche, las farolas de
    sodio, las ventanas encendidas y la cúpula iluminada."""
    o = t["oscuro"]
    noche = hx("141224")
    c = (lambda col: mezcla(col, noche, 0.52)) if o else (lambda col: col)
    im, d = lienzo(hx("C9DDEA"))
    if not o:
        degradado(im, 0, 44, [hx("A9CBE6"), hx("B9D5EA"), hx("CBDFEC"), hx("DCE7EC"), hx("EDE9DC")])
        for x, y, w in ((140, 10, 30), (226, 4, 22)):
            g9.nube(d, x, y, w, hx("FFFFFF"), hx("EEF2F4"), hx("D2DCE4"))
    else:
        degradado(im, 0, 44, [hx("0C0F20"), hx("12142A"), hx("1A1A34"), hx("24203C"), hx("3A2A3A")])
        g9.estrellas(im, 7, 40, 0, 26, [hx("F1E6D2"), hx("B5A794"), hx("8A8AA8")], x0=120, x1=262)
    # Tejados lejanos, pinos y la cúpula.
    teja, teja2 = (hx("D8A88A"), hx("C9967A")) if not o else (hx("2A2030"), hx("231A2A"))
    rnd = random.Random(41)
    x = 118
    while x < 266:
        w_ = rnd.randint(10, 20); h_ = rnd.randint(42, 47)
        d.rectangle([x, h_, x + w_, 52], fill=teja if rnd.random() < 0.5 else teja2)
        d.polygon([(x - 1, h_), (x + w_ // 2, h_ - 3), (x + w_ + 1, h_)], fill=g8.esc(teja, 0.92))
        x += w_
    cx_, cb = 192, 34                                                      # la cúpula
    plomo, plomo_c, plomo_o = (hx("9AAAB4"), hx("C8D2D6"), hx("6E7E8A")) if not o else (hx("E8B868"), hx("F8DC98"), hx("B88440"))
    trav = hx("E6DCC4") if not o else hx("E8C890")
    d.rectangle([cx_ - 17, cb, cx_ + 17, cb + 9], fill=trav)
    for k in range(-15, 16, 4):
        d.line([cx_ + k, cb + 1, cx_ + k, cb + 8], fill=g8.esc(trav, 0.82))
    d.rectangle([cx_ - 19, cb + 9, cx_ + 19, cb + 10], fill=g8.esc(trav, 0.9))
    d.pieslice([cx_ - 16, cb - 17, cx_ + 16, cb + 17], 180, 360, fill=plomo)
    d.pieslice([cx_ - 16, cb - 17, cx_ + 16, cb + 17], 180, 230, fill=plomo_c)
    for a_ in (200, 225, 250, 270, 290, 315, 340):
        r_ = math.radians(a_)
        d.line([cx_ + 4 * math.cos(r_), cb - 15, cx_ + 16 * math.cos(r_), cb + 16 * math.sin(r_) * 0.06 - 0.5], fill=plomo_o)
    d.rectangle([cx_ - 3, cb - 22, cx_ + 3, cb - 16], fill=trav); d.line([cx_ - 1, cb - 21, cx_ - 1, cb - 17], fill=g8.esc(trav, 0.7))
    d.polygon([(cx_ - 3, cb - 22), (cx_ + 3, cb - 22), (cx_, cb - 26)], fill=plomo)
    d.line([cx_, cb - 30, cx_, cb - 26], fill=hx("C9A040")); d.line([cx_ - 1, cb - 28, cx_ + 1, cb - 28], fill=hx("C9A040"))
    if o:
        luz(im, cx_, cb - 2, 34, hx("E8955A"), 0.18, 4)
    pino, pino2, tronco = (hx("4A6A40"), hx("3A5634"), hx("6A4A34")) if not o else (hx("0E1814"), hx("0A1210"), hx("120E10"))
    for px_, py_, r_ in ((134, 34, 11), (250, 36, 10)):
        d.line([px_, py_, px_ - 2, 52], fill=tronco); d.line([px_ - 1, py_ + 4, px_ + 4, py_ + 1], fill=tronco)
        d.ellipse([px_ - r_, py_ - 5, px_ + r_, py_ + 3], fill=pino)
        d.ellipse([px_ - r_ + 2, py_ - 2, px_ + r_ - 1, py_ + 3], fill=pino2)
    # Las ruinas: columnas rotas, muro de ladrillo y dos gatos, tras la reja.
    hundido = hx("C9B494") if not o else hx("1E1A22")
    d.rectangle([118, 52, 266, 64], fill=hundido)
    lad = c(hx("A86A4A"))
    for (x0, x1, y0) in ((140, 162, 54), (214, 238, 55)):
        d.rectangle([x0, y0, x1, 64], fill=lad)
        for y in range(y0 + 1, 64, 2):
            d.line([x0, y, x1, y], fill=g8.esc(lad, 0.8))
    col_c, col_m, col_s = c(hx("EEE6D4")), c(hx("D2C7AE")), c(hx("A89C82"))
    for (x0, y0, w_) in ((126, 42, 5), (146, 47, 5), (164, 38, 6), (222, 40, 6), (240, 46, 5), (256, 50, 5)):
        d.rectangle([x0, y0, x0 + w_, 64], fill=col_m)
        d.line([x0, y0, x0, 64], fill=col_c); d.line([x0 + 1, y0, x0 + 1, 64], fill=col_c)
        d.line([x0 + w_, y0, x0 + w_, 64], fill=col_s)
        d.line([x0 + 3, y0 + 1, x0 + 3, 64], fill=g8.esc(col_m, 0.92))
        d.polygon([(x0, y0), (x0 + 2, y0 - 1), (x0 + w_, y0 + 1)], fill=col_c)
    d.rectangle([162, 36, 172, 38], fill=col_c); d.line([162, 38, 172, 38], fill=col_s)     # un capitel
    d.rectangle([220, 38, 230, 40], fill=col_c); d.line([220, 40, 230, 40], fill=col_s)
    for (x0, y0) in ((186, 59), (196, 60)):                                               # tambores caídos
        d.ellipse([x0, y0, x0 + 8, y0 + 4], fill=col_m); d.ellipse([x0 + 6, y0, x0 + 9, y0 + 4], fill=col_c)
    for (gx, gy, cuerpo, cara) in ((167, 35, c(hx("D88A3A")), c(hx("F0C890"))), (226, 37, c(hx("2A2420")), c(hx("F0EAE0"))),
                                   (150, 46, c(hx("8A8478")), c(hx("D8D2C8")))):
        d.rectangle([gx - 2, gy - 4, gx + 1, gy], fill=cuerpo)                             # gatito sentado
        d.rectangle([gx - 1, gy - 6, gx + 1, gy - 4], fill=cuerpo)
        d.point((gx - 1, gy - 7), fill=cuerpo); d.point((gx + 1, gy - 7), fill=cuerpo)
        d.point((gx, gy - 3), fill=cara); d.point((gx, gy - 2), fill=cara)
        d.line([gx - 3, gy, gx - 4, gy - 3], fill=cuerpo)
    tapia = c(hx("E8DCC2"))
    d.rectangle([118, 60, 266, 64], fill=tapia); d.line([118, 60, 266, 60], fill=c(hx("F6EEDC"))); d.line([118, 64, 266, 64], fill=c(hx("B8A888")))
    for x in range(119, 266, 4):
        d.line([x, 53, x, 59], fill=c(hx("2E302C")))
    d.line([118, 53, 266, 53], fill=c(hx("2E302C")))
    # Palazzo ocre, a la izquierda.
    ocre = c(hx("D9A65A"))
    d.rectangle([0, 0, 118, 64], fill=ocre)
    rr = random.Random(3)
    for _ in range(140):
        x, y = rr.randrange(0, 118), rr.randrange(0, 64)
        d.point((x, y), fill=g8.esc(ocre, rr.choice((0.94, 1.05))))
    trav_c = c(hx("EDE2CA"))
    d.rectangle([0, 3, 119, 5], fill=trav_c); d.line([0, 6, 119, 6], fill=c(hx("B8A27A")))
    d.rectangle([0, 42, 119, 43], fill=trav_c)
    vid, pers = c(hx("4A3A30")), c(hx("3D5E45"))
    enc = hx("F0C070")
    geranio = (c(hx("A8432A")), c(hx("D8283A")), c(hx("4A7A3A")))
    for k, x in enumerate((12, 42, 72, 100)):
        ventana(d, x, 10, 8, 11, trav_c, vid, pers, enc if (o and k in (1, 3)) else None, geranio if k % 2 == 0 else None)
        ventana(d, x, 27, 8, 11, trav_c, vid, pers, enc if (o and k == 0) else None, geranio if k == 2 else None)
    d.rectangle([52, 47, 70, 64], fill=trav_c); d.pieslice([52, 41, 70, 53], 180, 360, fill=trav_c)    # portone
    d.rectangle([54, 49, 68, 64], fill=c(hx("5A3A22"))); d.pieslice([54, 43, 68, 55], 180, 360, fill=c(hx("5A3A22")))
    d.line([61, 46, 61, 64], fill=c(hx("3A2414")))
    for yy in (52, 57, 62):
        d.line([55, yy, 67, yy], fill=c(hx("4A2E1A")))
    d.rectangle([96, 46, 116, 52], fill=c(hx("F4F0E6"))); d.rectangle([96, 46, 116, 52], outline=c(hx("B8B0A0")))    # la placa
    # Nasone.
    hierro = c(hx("4A5A50"))
    d.rectangle([24, 52, 30, 66], fill=hierro); d.ellipse([23, 50, 31, 54], fill=hierro); d.line([24, 52, 24, 66], fill=c(hx("6A7A70")))
    d.line([31, 56, 33, 56], fill=hierro); d.point((33, 57), fill=hierro)
    d.rectangle([31, 65, 37, 66], fill=c(hx("2A2A28")))
    # Fachada rojo pompeyano, a la derecha.
    rojo = c(hx("B5553A"))
    d.rectangle([266, 0, W, 64], fill=rojo)
    for _ in range(140):
        x, y = rr.randrange(266, W), rr.randrange(0, 64)
        d.point((x, y), fill=g8.esc(rojo, rr.choice((0.93, 1.05))))
    d.line([266, 0, 266, 64], fill=c(hx("8A3A24")))
    d.rectangle([266, 2, W, 4], fill=trav_c)
    for k, x in enumerate((282, 316, 350, 384)):
        ventana(d, x, 8, 8, 12, trav_c, vid, pers, enc if (o and k in (0, 2)) else None, geranio if k == 1 else None)
    d.rectangle([306, 36, 336, 37], fill=trav_c)                              # balcón
    for x in range(307, 336, 2):
        d.line([x, 30, x, 35], fill=c(hx("2E302C")))
    d.line([306, 30, 336, 30], fill=c(hx("2E302C")))
    ventana(d, 316, 24, 10, 12, trav_c, vid, pers, enc if o else None)
    d.rectangle([360, 44, 390, 64], fill=c(hx("3D5E45")))                     # la serranda de la tienda
    for y in range(45, 64, 2):
        d.line([360, y, 390, y], fill=c(hx("2E4A36")))
    d.rectangle([358, 42, 392, 43], fill=trav_c)
    # La farola en su ménsula.
    fx, fy = 300, 30
    d.line([fx - 6, fy - 4, fx, fy - 4], fill=c(hx("1E1E1C"))); d.line([fx - 6, fy - 4, fx - 6, fy + 1], fill=c(hx("1E1E1C")))
    d.polygon([(fx - 3, fy - 3), (fx + 3, fy - 3), (fx + 2, fy + 4), (fx - 2, fy + 4)], fill=c(hx("1E1E1C")))
    d.rectangle([fx - 2, fy - 2, fx + 2, fy + 3], fill=hx("FFD890") if o else hx("D8D4C8"))
    # Acera de travertino y sampietrini.
    d.rectangle([0, 64, W, 66], fill=c(hx("D8CCB2"))); d.line([0, 66, W, 66], fill=c(hx("9A8E78")))
    junta, piedras = c(hx("4A443E")), [c(hx("7A726A")), c(hx("6E665E")), c(hx("847C72")), c(hx("686058"))]
    d.rectangle([0, 67, W, H], fill=junta)
    for fila, y in enumerate(range(67, H, 3)):
        off = (fila * 2) % 4
        for x in range(-off, W, 4):
            arco = round(1.2 * math.sin((x + fila * 9) / 30))
            d.rectangle([x, y + (arco > 0), x + 2, y + 1 + (arco > 0)], fill=piedras[(x * 7 + fila * 3) % 4])
    # La motoneta.
    vx, vy = 330, 66
    verde, crema, cromo, llanta = c(hx("9CCFB8")), c(hx("F2EAD8")), c(hx("C8CCD0")), c(hx("1E1E1E"))
    for wx in (vx + 3, vx + 21):
        d.ellipse([wx - 3, vy - 6, wx + 3, vy], fill=llanta); d.ellipse([wx - 1, vy - 4, wx + 1, vy - 2], fill=cromo)
    d.polygon([(vx - 1, vy - 7), (vx + 9, vy - 8), (vx + 11, vy - 4), (vx + 3, vy - 3), (vx - 1, vy - 4)], fill=verde)   # el guardabarros trasero
    d.rectangle([vx + 8, vy - 4, vx + 17, vy - 3], fill=verde)
    d.polygon([(vx + 17, vy - 3), (vx + 19, vy - 15), (vx + 22, vy - 15), (vx + 21, vy - 6), (vx + 24, vy - 4), (vx + 17, vy - 2)], fill=verde)
    d.rounded_rectangle([vx + 1, vy - 11, vx + 11, vy - 8], radius=1, fill=crema)                      # el asiento
    d.line([vx + 20, vy - 15, vx + 18, vy - 19], fill=cromo); d.line([vx + 16, vy - 19, vx + 22, vy - 19], fill=cromo)
    d.ellipse([vx + 21, vy - 18, vx + 24, vy - 15], fill=cromo if not o else hx("FFF0C0"))
    d.line([vx + 2, vy - 7, vx + 9, vy - 7], fill=g8.esc(verde, 0.8))
    if o:
        luz(im, fx, fy, 30, hx("E8955A"), 0.3, 5)
        luz(im, fx, 71, 54, hx("E8955A"), 0.26, 4, ry=9)
        luz(im, 106, 49, 12, hx("E8955A"), 0.1, 3)
    big = subir(im)
    # Texto de la placa (a la escala de la banda).
    dd = ImageDraw.Draw(big)
    dd.text((106 * ESC, 49 * ESC + 1), "VICOLO", font=fuente("DejaVuSans-Bold.ttf", 9), fill=c(hx("3A3530")), anchor="mm")
    # Lo que se mueve: la ropa tendida, las palomas y el agua del nasone.
    y_cuerda = lambda x: 10 + 3 * (x - 118) / 148 + 6 * math.sin(math.pi * (x - 118) / 148)
    dd.line([(x * ESC, y_cuerda(x) * ESC) for x in np.linspace(118, 266, 80)], fill=c(hx("3A3530")) if not o else hx("3A3646"), width=1)
    tela = lambda h: c(hx(h)) if not o else mezcla(hx(h), hx("2A2A48"), 0.5)
    ropa = [(126, "camisa", tela("F6F4EE")), (140, "calcetin", tela("C8283A")), (152, "sabana", tela("F2E6C8")),
            (224, "pantalon", tela("4A6AA8")), (238, "vestido", tela("E8C040")), (252, "toalla", tela("D86A50")), (262, "calcetin", tela("F6F4EE"))]

    def prenda(dd, x, tipo, col, s_):
        y = y_cuerda(x)
        sw = (0, 0.6, 1.0, 0.6)[s_]                                   # cuánto se inclina con el viento
        osc = g8.esc(col, 0.82)
        forma = {"camisa": (9, 9), "calcetin": (3, 6), "sabana": (18, 13), "pantalon": (8, 12), "vestido": (8, 13), "toalla": (8, 10)}[tipo]
        w_, h_ = forma
        for j in range(h_):
            dx = sw * (j / h_) * 2.2
            if tipo == "camisa":
                ww = w_ if j < 3 else w_ - 4
                x0 = x - ww / 2
            elif tipo == "pantalon":
                if j > 4:
                    for lx in (x - w_ / 2, x + 1):
                        cuad(dd, lx + dx, y + j, col if j % 4 else osc, w_ / 2 - 1, 1)
                    continue
                ww, x0 = w_, x - w_ / 2
            elif tipo == "vestido":
                ww = 4 + j * 0.45; x0 = x - ww / 2
            else:
                ww, x0 = w_, x - w_ / 2
            cuad(dd, x0 + dx, y + j, osc if (tipo == "toalla" and j % 4 == 2) or (tipo == "sabana" and j == h_ - 1) else col, ww, 1)
        cuad(dd, x - 1, y - 0.5, c(hx("C8B890")), 1, 1); cuad(dd, x + w_ / 2 - 1.5, y - 0.5, c(hx("C8B890")), 1, 1)

    pal_c, pal_v, pal_o = (hx("8E97A8"), hx("6E9A86"), hx("3A3E48")) if not o else (hx("4A4E5E"), hx("3E5A50"), hx("1A1C24"))

    def paloma(dd, x, y, pica, mira=1):
        cuad(dd, x - 2, y - 2, pal_c, 4, 2); cuad(dd, x - 3 * mira if mira > 0 else x + 2, y - 2, pal_o, 1, 1)
        hx_, hy_ = (x + 2 * mira, y - 1) if pica else (x + 2 * mira, y - 4)
        cuad(dd, min(x + 1 * mira, hx_), hy_ + (0 if pica else 1), pal_v, 1, 2)
        cuad(dd, hx_ - (0 if mira > 0 else 1), hy_, pal_c, 2, 1)
        cuad(dd, hx_ + (2 if mira > 0 else -1), hy_ + 0.33, hx("2A2A2A"), 0.67, 0.34)
        cuad(dd, x - 1, y, hx("C86A5A"), 0.34, 1); cuad(dd, x + 0.67, y, hx("C86A5A"), 0.34, 1)

    suelo = [(150, 74, 1, 0), (166, 76, -1, 3), (378, 73, 1, 5)] if not o else []
    cornisa = [] if not o else [(312, 29, 1, 0), (327, 29, -1, 4)]

    def vivo(img, tick, delante):
        if delante:
            return
        dd = ImageDraw.Draw(img)
        for j, (x, tipo, col) in enumerate(ropa):
            prenda(dd, x, tipo, col, ((tick // 3) + j * 2) % 4)
        for x, y, mira, ph in suelo:
            paloma(dd, x, y, ((tick // 4) + ph) % 6 < 2, mira)
        for x, y, mira, ph in cornisa:
            paloma(dd, x, y, False if ((tick // 6) + ph) % 8 else True, mira)
        if not o:                                                   # una paloma que da vueltas sobre los tejados
            a_ = (tick % BUCLE) * 2 * math.pi / BUCLE
            px_, py_ = 196 + 54 * math.cos(a_), 12 + 5 * math.sin(a_)
            ala = (1, -1)[(tick // 2) % 2]
            cuad(dd, px_ - 1.5, py_, pal_c, 3, 1); cuad(dd, px_ + (1.5 if math.sin(a_) < 0 else -2.5), py_ - 0.6, pal_c, 1, 1)
            cuad(dd, px_ - 0.5, py_ - ala, pal_o, 1, 1)
        for k in range(3):                                          # el chorrito del nasone
            yy = 57 + ((tick + k * 4) % 12) * 0.7
            cuad(dd, 33.3, yy, hx("BFE0EC") if not o else hx("8A9AB0"), 0.67, 1)
    g9.ANIM[("urbe", o)] = [vivo]
    return big


# =====================================================================
#  Registro en la cadena
# =====================================================================
FONDOS20A = {"pirata": fondo_pirata, "neon": fondo_neon, "urbe": fondo_urbe}
ETIQ = {"pirata": "Pirata · en la cubierta", "neon": "Ciudad neón · en el callejón del puesto", "urbe": "Roma · en el vicolo"}
NOCHE = {"pirata": "el farol y las estrellas", "neon": "letreros, lluvia y vapor", "urbe": "farolas y la cúpula iluminada"}
for _n in MIOS:
    if _n not in g9.NUEVOS:
        g9.NUEVOS.append(_n)
    if _n not in [b[0] for b in g8.BANDAS]:
        g8.BANDAS.append((_n, _n == "neon", ETIQ[_n]))
g8.FONDOS.update(FONDOS20A)
g9.FONDOS9.update(FONDOS20A)
g9.ETIQ.update(ETIQ)
g9.NOCHE.update(NOCHE)


def bandas(nombres):
    """Una banda en GIF por tema y modo (camina, hace sus dos acciones y vuelve), en salida/."""
    for n in nombres:
        for o in ((True,) if n == "neon" else (False, True)):
            et = ETIQ[n] if not o else ETIQ[n].split(" · ")[0] + " · de noche: " + NOCHE[n]
            g8.gif_banda(g17.secuencia(n), [(n, o, et)], "_oscuro" if o else "")


if __name__ == "__main__":
    bandas(sys.argv[1:] or MIOS)
    print("listo")
