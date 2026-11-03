"""
Lote 9b (d): los gatos de Japón Edo, Prehistoria, Vapor victoriano y Art déco.

- Japón Edo (`edo`): bobtail blanco con manchas negras y anaranjadas (mi-ke) y la cola en pompón, en ukiyo-e: la línea
  de sumi de la plancha y el degradado bokashi. Collar rojo con cascabel. Fondo: el puente, el monte y las olas, con
  pétalos de cerezo que caen; de noche, farolillos sobre el puente y nieve. Hace el gato de la suerte y mira saltar un koi.
- Prehistoria (`cueva`): pintura rupestre en ocre y carbón sobre la roca. Collar de conchas. Fondo: la boca de la cueva
  con el valle afuera y el fuego del campamento; de noche, lo hondo de la cueva con manos y bisontes en la pared y una
  lámpara de grasa que titila. Sopla ocre y deja la silueta de su pata, y sigue una chispa.
- Vapor victoriano (`vapor`): atigrado gris, plano técnico (tinta y achurado de patente) con piezas de latón bruñido.
  Gafas de aviador y chaleco con cadena de reloj. Fondo: la fábrica de ladrillo con chimeneas que humean y un engrane
  que gira; de noche, las farolas de gas. Baja una palanca de vapor y sigue a un dirigible.
- Art déco (`deco`): negro, laca con líneas de oro. Pajarita y collar de perlas. Fondo: rascacielos escalonados y un
  trasatlántico en el muelle; de noche, los reflectores que barren el cielo. Toca la trompeta y se abanica con plumas.

Colores: `edo` y `cueva` de sus esquemas en ui/Disenios.kt (edoClaro/edoOscuro, cuevaClaro/cuevaOscuro) y de
juegoTonos; `vapor` y `deco`, de los suyos (vaporClaro/vaporOscuro, decoClaro/decoOscuro) en sus ramas.
Importa la cadena hasta gato18 (y gato19 si existe) y agrega sus cuatro temas sin tocar los demás.
"""
import importlib
import math
import random
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw, ImageFilter

sys.path.insert(0, str(Path(__file__).parent))
import gato18 as g18      # noqa: E402,F401  (carga toda la cadena)
if (Path(__file__).parent / "gato19.py").exists():
    importlib.import_module("gato19")
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato17 as g17      # noqa: E402
from gato8 import hx, mezcla, esc, fuente, ESC   # noqa: E402
from gato9 import W, H, lienzo, degradado, ondas, perfil, luz, estrellas, nube, humo, cuad, llama, subir, tramar   # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
N, SS = g8.N, g8.SS
MIOS = ("edo", "cueva", "vapor", "deco")
BUCLE = 160      # cuadros en que se repite todo lo que se mueve en estos fondos (12,8 s)


def fase(tick, periodo=BUCLE, k=1.0):
    """Ángulo que da k vueltas exactas en `periodo` cuadros (para que el bucle del fondo cierre sin salto)."""
    return 2 * math.pi * k * (tick % periodo) / periodo


# =====================================================================
#  Accesorios nuevos: collar con cascabel, collar de conchas, perlas con pajarita, chaleco con cadena y gafas de aviador
# =====================================================================
COLLARES = ("cascabel", "conchas", "perlas", "chaleco_v")
g8.CON_COLLAR = tuple(g8.CON_COLLAR) + COLLARES
g8.CON_CHALECO = tuple(g8.CON_CHALECO) + ("chaleco_v",)
_collar_prev = g8.collar


def collar(L, C, cab, acc):
    if acc not in COLLARES:
        return _collar_prev(L, C, cab, acc)
    a, b, px, py = g8._cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.55, a[1] + (b[1] - a[1]) * 0.55)
    e1, e2 = (m[0] - 4.4 * px, m[1] - 4.4 * py), (m[0] + 4.4 * px, m[1] + 4.4 * py)
    j = max((e1, e2), key=lambda q: q[0] + q[1])
    at = e1 if j is e2 else e2
    en = lambda u: (at[0] + (j[0] - at[0]) * u, at[1] + (j[1] - at[1]) * u)
    if acc == "cascabel":            # Edo: collar rojo ancho y un cascabel de latón con su ranura
        L.capsula("collar", [e1, e2], 1.9)
        bx, by = j[0] - 0.3 * px, j[1] + 2.5
        L.elipse("joya", bx, by, 1.9, 1.9)
        L.capsula("linea", [(bx - 1.2, by + 0.5), (bx + 1.2, by + 0.5)], 0.45)
        L.elipse("linea", bx, by + 1.2, 0.4, 0.5)
        L.elipse("brillo", bx - 0.7, by - 0.8, 0.45, 0.4)
    elif acc == "conchas":           # Prehistoria: cordel de fibra con conchas perforadas y una caracola colgando
        L.capsula("collar", [e1, e2], 1.0)
        for u in (0.12, 0.42, 0.72):
            x, y = en(u)
            L.elipse("joya", x, y + 0.4, 1.05, 0.9)
        x, y = j[0] - 0.2 * px, j[1] + 2.4
        L.elipse("joya", x, y, 1.3, 1.8)
        L.capsula("linea", [(x, y - 1.0), (x, y + 1.1)], 0.4)
    elif acc == "perlas":            # Art déco: dos vueltas de perlas y una pajarita de coral bajo la barbilla
        for k in range(8):
            x, y = en(k / 7)
            L.elipse("joya", x, y, 0.62, 0.62)
        for k in range(7):          # la segunda vuelta cuelga más abajo, en curva
            u = (k + 0.5) / 7
            x, y = en(u)
            L.elipse("joya", x + 0.6 * px, y + 0.6 * py + 1.6 * math.sin(math.pi * u), 0.55, 0.55)
        x, y = j[0] + 0.3, j[1] - 0.2
        L.poligono("capa", [(x, y), (x - 2.4, y - 1.8), (x - 2.6, y + 1.7)])
        L.poligono("capa", [(x, y), (x + 2.4, y - 1.6), (x + 2.6, y + 1.9)])
        L.elipse("broche", x, y, 0.75, 0.75)
    elif acc == "chaleco_v":         # Vapor: el cuello blanco de la camisa asoma sobre el chaleco
        L.capsula("collar", [e1, e2], 1.5)


g8.collar = collar


def chaleco_v(L, ps):
    """Vapor: chaleco de paño con solapa, dos botones de latón y la cadena del reloj de bolsillo cruzada."""
    Hh, C, rh, rc = ps["H"], ps["C"], ps["rh"], ps["rc"]
    ct, ht = (C[0], C[1] - rc), (Hh[0], Hh[1] - rh)
    dx, dy = ht[0] - ct[0], ht[1] - ct[1]; n = math.hypot(dx, dy) or 1
    nx, ny = -dy / n, dx / n
    lp = lambda t, up=0.0: (ct[0] + dx * t + nx * up, ct[1] + dy * t + ny * up)
    # Envuelve todo el pecho (de la cruz al vientre), no solo el lomo: así se lee como chaleco y no como capa.
    L.poligono("capa", [lp(-0.06, 1.0), lp(0.46, 1.0), lp(0.52, -rc * 0.9), lp(0.46, -rc * 2.0), lp(0.12, -rc * 2.08),
                        (C[0] + rc * 0.7, C[1] + rc * 0.62), (C[0] + rc * 0.98, C[1] - rc * 0.1), (C[0] + rc * 0.62, C[1] - rc * 0.85)])
    L.capsula("broche", [(C[0] + rc * 0.72, C[1] + rc * 0.1), lp(0.24, -rc * 1.2), lp(0.38, -rc * 1.05)], 0.55)    # cadena
    L.elipse("broche", *lp(0.39, -rc * 1.05), 0.85, 0.85)                                                          # leontina
    for u in (0.0, 0.5, 1.0):                                                                                       # botones
        L.elipse("broche", C[0] + rc * (0.86 - 0.12 * u), C[1] - rc * (0.55 - 0.9 * u), 0.5, 0.5)


_chaleco_prev = g8.chaleco


def chaleco(L, ps):
    if _cu_actual.get("cu") == "chaleco_v":
        return chaleco_v(L, ps)
    return _chaleco_prev(L, ps)


g8.chaleco = chaleco

_lado_prev, _frente_prev = g8.sombrero_lado, g8.sombrero_frente


def sombrero_lado(L, cx, cy, inc, som):
    if som != "gafas":
        return _lado_prev(L, cx, cy, inc, som)
    a = math.radians(inc)
    p = lambda x, y: g8.rot(cx, cy, x, y, a)
    P = lambda pts: [p(*q) for q in pts]
    L.capsula("sombrero", P([(-6.6, -1.6), (-4.6, -4.8), (-1.0, -6.0), (3.0, -5.0)]), 1.3)     # correa de cuero
    for (x, y, r) in ((-2.4, -6.4, 1.9), (1.8, -4.9, 2.2)):                                      # dos lentes de latón
        L.elipse("sombrero2", *p(x, y), r, r)
        L.elipse("vidrio", *p(x + 0.1, y), r * 0.55, r * 0.55)
    L.elipse("sombreroh", *p(1.4, -5.5), 0.45, 0.45)
    _lado_prev(L, cx, cy, inc, None)          # el objeto de la animación propia (si lo hay) se dibuja ahí


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if som != "gafas":
        return _frente_prev(L, cx, cy, som, desv)
    dx = desv * 1.6
    L.capsula("sombrero", [(cx - 6.4 + dx, cy - 2.4), (cx - 3 + dx, cy - 4.4), (cx + 3 + dx, cy - 4.4), (cx + 6.4 + dx, cy - 2.4)], 1.3)
    if desv > 0:
        L.elipse("sombrero2", cx + 5.6 + dx, cy - 4.2, 1.8, 1.8)
        L.elipse("vidrio", cx + 5.8 + dx, cy - 4.2, 0.9, 0.9)


g8.sombrero_lado, g8.sombrero_frente = sombrero_lado, sombrero_frente

# ---------------------------------------------------------------------
#  El bobtail de Edo: la cola corta en pompón. Mientras se dibuja su pose, el lienzo recorta la cola.
#  Y la pose de cada cuadro se guarda para poner las manchas del pelaje en su lugar (lomo, cadera, orejas).
# ---------------------------------------------------------------------
_LienzoBase = g8.Lienzo
_cu_actual = {}
_pose = {}


class LienzoBob(_LienzoBase):
    def curva(self, capa, pts, anchos):
        if capa not in ("cola", "colafrente"):
            return super().curva(capa, pts, anchos)
        c = g8.catmull(pts)
        sub, largo = [c[0]], 0.0
        for (x0, y0), (x1, y1) in zip(c, c[1:]):
            largo += math.hypot(x1 - x0, y1 - y0); sub.append((x1, y1))
            if largo >= 3.6: break
        dd = self._d(capa)
        for x, y in sub:
            dd.ellipse([(x - 2.3) * g8.R, (y - 2.3) * g8.R, (x + 2.3) * g8.R, (y + 2.3) * g8.R], fill=255)
        ex, ey = sub[-1]
        self.elipse(capa, ex, ey, 2.9, 2.7)
        return sub


BOBTAIL = ("cascabel",)
_dibujar_prev, _giro_prev = g8.dibujar, g8.dibujar_giro


def dibujar(ps, acc):
    _pose["ps"], _pose["giro"] = ps, None
    _cu_actual["cu"] = acc[0]
    if acc[0] in BOBTAIL:
        g8.Lienzo = LienzoBob
    try:
        return _dibujar_prev(ps, acc)
    finally:
        g8.Lienzo = _LienzoBase


def dibujar_giro(i, acc):
    _pose["ps"], _pose["giro"] = None, i
    _cu_actual["cu"] = acc[0]
    if acc[0] in BOBTAIL:
        g8.Lienzo = LienzoBob
    try:
        return _giro_prev(i, acc)
    finally:
        g8.Lienzo = _LienzoBase


g8.dibujar, g8.dibujar_giro = dibujar, dibujar_giro

# ---------------------------------------------------------------------
#  Manchas de gato tricolor (mi-ke): negra en el lomo y la cola, anaranjada en el hombro; en la cabeza, una oreja de cada
#  color. Siguen a la pose: se calculan con el cuerpo, la cabeza y su inclinación de cada cuadro.
# ---------------------------------------------------------------------
_acabar_prev = g8.acabar


def _en_elipse(x, y, cx, cy, rx, ry, ang=0.0):
    ca, sa = math.cos(ang), math.sin(ang)
    u, v = (x - cx) * ca + (y - cy) * sa, -(x - cx) * sa + (y - cy) * ca
    return (u / rx) ** 2 + (v / ry) ** 2 <= 1


def mancha(x, y, parte):
    ps, giro = _pose.get("ps"), _pose.get("giro")
    if parte in ("cola", "colafrente"):
        return "W"
    if ps is None:
        if giro == 1:
            if parte == "cabeza":
                return "W" if _en_elipse(x, y, 29.0, 23.5, 4.0, 3.4) else ("Y" if _en_elipse(x, y, 36.0, 21.5, 2.8, 2.8) else None)
            return "W" if _en_elipse(x, y, 29.5, 43.5, 4.6, 4.0) else ("Y" if _en_elipse(x, y, 35.5, 38.5, 2.6, 2.4) else None)
        if parte == "cabeza":
            return "W" if _en_elipse(x, y, 38.0, 23.5, 3.8, 3.4) else ("Y" if _en_elipse(x, y, 44.5, 21.5, 2.4, 2.4) else None)
        return "W" if _en_elipse(x, y, 26.0, 42.0, 5.0, 4.4) else ("Y" if _en_elipse(x, y, 35.5, 37.5, 2.8, 2.6) else None)
    Hh, C, cab = ps["H"], ps["C"], ps["cab"]
    dx, dy = C[0] - Hh[0], C[1] - Hh[1]; n = math.hypot(dx, dy) or 1
    ux, uy = dx / n, dy / n; nx, ny = uy, -ux              # hacia la cabeza / hacia el lomo
    ang = math.atan2(dy, dx)
    if parte == "cabeza":
        a = math.radians(ps["inc"])
        bx, by = g8.rot(cab[0], cab[1], -3.6, -3.6, a)
        if _en_elipse(x, y, bx, by, 3.6, 3.2, a): return "W"
        ox, oy = g8.rot(cab[0], cab[1], 1.8, -6.6, a)
        if _en_elipse(x, y, ox, oy, 2.6, 2.8, a): return "Y"
        return None
    if parte != "cuerpo":
        return None
    rh, rc = ps["rh"], ps["rc"]
    if _en_elipse(x, y, Hh[0] + nx * rh * 0.45 + ux * 1.0, Hh[1] + ny * rh * 0.45 + uy * 1.0, 5.8, 4.6, ang): return "W"
    mx, my = Hh[0] + dx * 0.66 + nx * rc * 0.72, Hh[1] + dy * 0.66 + ny * rc * 0.72
    if _en_elipse(x, y, mx, my, 3.6, 2.9, ang): return "Y"
    return None


def acabar(ch, parte, estilo):
    if estilo[0] != "manchas":
        return _acabar_prev(ch, parte, estilo)
    out = _acabar_prev(ch, parte, ("ninguno",) + tuple(estilo[1:]))
    for r in range(N):
        for c in range(N):
            if out[r][c] in "PS" and parte[r][c] in ("cuerpo", "cola", "colafrente", "cabeza"):
                m = mancha(c + 0.5, r + 0.5, parte[r][c])
                if m:
                    out[r][c] = m
    return out


g8.acabar = acabar

# =====================================================================
#  Materiales (pintores nuevos)
# =====================================================================

def _vecinos_fuera(g, r, c):
    return any(not (0 <= r + dr < N and 0 <= c + dc < N) or g[r + dr][c + dc] == "." for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)))


def _caja(g):
    filas = [r for r in range(N) if any(v != "." for v in g[r])]
    return (min(filas), max(filas)) if filas else (0, N - 1)


def pintar_ukiyoe(g, t, cel):
    """Japón Edo, ukiyo-e: cada color es una plancha plana sobre washi, la línea de sumi de la plancha de contorno y el
    bokashi (el degradado que el impresor frota a mano en la plancha): el blanco se vuelve índigo hacia el vientre y
    las manchas negras se aclaran hacia arriba. Un poco de veta de madera en las tintas oscuras."""
    s = cel * SS
    col = t["col"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    for r, c, v in g8.celdas(g):
        d.rectangle([c * s, r * s, (c + 1) * s - 1, (r + 1) * s - 1], fill=col[v] + (255,))
    a = np.asarray(img).astype(np.float32)
    r0, r1 = _caja(g)
    yy = (np.arange(N * s)[:, None] / s - r0) / max(1, r1 - r0)
    f = np.clip(yy, 0, 1)
    letra = np.full((N * s, N * s), ".", "<U1")
    for r, c, v in g8.celdas(g):
        letra[r * s:(r + 1) * s, c * s:(c + 1) * s] = v
    bok = np.array(t["bokashi"], np.float32)
    claro = np.isin(letra, list("PSL"))
    k = np.clip((f - 0.35) / 0.65, 0, 1) ** 1.3 * 0.55
    a[..., :3] = np.where(claro[..., None], a[..., :3] * (1 - k[..., None]) + bok * k[..., None], a[..., :3])
    neg = letra == "W"
    k2 = np.clip(1 - f * 1.6, 0, 1)[..., None] * 0.5
    a[..., :3] = np.where(neg[..., None], a[..., :3] * (1 - k2) + np.array(t["sumi_claro"], np.float32) * k2, a[..., :3])
    # Veta de la madera en la tinta: rayas finas casi horizontales, apenas más claras.
    veta = (np.sin(np.arange(N * s)[:, None] * 0.9 + np.sin(np.arange(N * s)[None, :] * 0.05) * 3) > 0.82)
    oscuro_ = np.isin(letra, list("KW"))
    a[..., :3] = np.where((oscuro_ & veta)[..., None], a[..., :3] * 0.8 + 255 * 0.2 * np.array(t["sumi_claro"], np.float32) / 255, a[..., :3])
    img = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8), "RGBA")
    return img.resize((N * cel, N * cel), Image.LANCZOS)


_ruido = {}


def ruido(n, semilla, escala):
    """Ruido suave (n×n) en [0, 1]: manchas grandes como las de la roca o el pigmento."""
    k = (n, semilla, escala)
    if k not in _ruido:
        rng = np.random.default_rng(semilla)
        m = max(2, n // escala)
        base = Image.fromarray((rng.random((m, m)) * 255).astype(np.uint8)).resize((n, n), Image.BICUBIC)
        _ruido[k] = np.asarray(base, np.float32) / 255
    return _ruido[k]


def pintar_rupestre(g, t, cel):
    """Prehistoria: pintura rupestre. El ocre se aplica con los dedos y soplado: denso en manchas, más ralo en otras
    (deja ver la roca); el contorno es un trazo de carbón irregular; lo claro es caolín. Sin brillos de pintura."""
    s = cel * SS
    col = t["col"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    for r, c, v in g8.celdas(g):
        if v == "K": continue
        x0, y0 = c * s, r * s
        d.rectangle([x0, y0, x0 + s - 1, y0 + s - 1], fill=col[v] + (255,))
    for r, c, v in g8.celdas(g):     # el carbón, con el borde tembloroso
        if v != "K": continue
        rnd = random.Random(r * 131 + c * 7)
        j = lambda: rnd.uniform(-0.18, 0.18) * s
        x0, y0 = c * s, r * s
        d.polygon([(x0 + j(), y0 + j()), (x0 + s + j(), y0 + j()), (x0 + s + j(), y0 + s + j()), (x0 + j(), y0 + s + j())], fill=col["K"] + (255,))
    # Se baja sin suavizar (el borde queda áspero, como el trazo sobre la roca) y la textura va en pocos niveles para que
    # el atlas pese poco: tres densidades de pigmento y tres tonos.
    img = img.resize((N * cel, N * cel), Image.NEAREST)
    a = np.asarray(img).astype(np.float32)
    n1 = ruido(N * cel, 5, 10)
    n2 = ruido(N * cel, 9, 2)
    v = 0.7 * n1 + 0.3 * n2
    nivel = (v > 0.42).astype(int) + (v > 0.6).astype(int)
    a[..., :3] = a[..., :3] * np.array([0.9, 1.0, 1.06])[nivel][..., None]
    a[..., 3] = np.minimum(a[..., 3], np.array([205, 232, 255])[nivel])
    return Image.fromarray(np.clip(a, 0, 255).astype(np.uint8), "RGBA")


def pintar_laton(g, t, cel):
    """Vapor victoriano: plano técnico de patente. El pelaje es tinta y achurado a 45° (más denso en la sombra), sobre el
    papel de la lámina; las piezas de metal (latón de gafas, botones, cadena, objetos) van bruñidas: luz arriba y
    reflejo oscuro abajo, con un destello."""
    s = cel * SS
    col = t["col"]
    tinta = col["K"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    metal = t["metal"]
    w = max(1, round(0.13 * s))
    for r, c, v in g8.celdas(g):
        x0, y0 = c * s, r * s
        base = col[v]
        if v in metal:
            for k in range(s):     # bruñido: degradado vertical dentro de la celda y según la fila (luz desde arriba)
                f = (k / s + (r % 3) / 3) / 2
                cc = mezcla(esc(base, 1.35), esc(base, 0.7), f)
                d.line([x0, y0 + k, x0 + s - 1, y0 + k], fill=cc + (255,))
            if (r * 7 + c * 3) % 9 == 0:
                d.rectangle([x0 + 0.2 * s, y0 + 0.15 * s, x0 + 0.45 * s, y0 + 0.4 * s], fill=(255, 250, 230, 255))
            continue
        d.rectangle([x0, y0, x0 + s - 1, y0 + s - 1], fill=base + (255,))
        if v in "SDT":
            for q in (0.0, 0.5):
                d.line([x0 + q * s, y0 + s, x0 + q * s + 0.5 * s, y0 + 0.5 * s], fill=tinta + (200,), width=w)
                d.line([x0 + q * s + 0.5 * s, y0 + 0.5 * s, x0 + q * s + s, y0], fill=tinta + (200,), width=w)
        elif v == "W":
            d.line([x0, y0 + s, x0 + s, y0], fill=tinta + (170,), width=w)
        elif v in "AB" and (r + c) % 2 == 0:
            d.line([x0, y0 + s, x0 + s, y0], fill=esc(base, 0.6) + (255,), width=w)
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def pintar_laca(g, t, cel):
    """Art déco: laca negra con incrustación de oro. El contorno de afuera es un filete de oro (los trazos de adentro,
    ojo y boca, quedan en negro); el pelaje negro tiene el brillo largo de la laca y, sobre el lomo, líneas de oro en
    escalón (zigzag) como en un biombo de Jean Dunand."""
    s = cel * SS
    col = t["col"]
    oro, oro2 = t["oro"], t["oro_claro"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    w = max(1, round(0.16 * s))
    r0, r1 = _caja(g)
    for r, c, v in g8.celdas(g):
        x0, y0 = c * s, r * s
        if v == "K":
            d.rectangle([x0, y0, x0 + s - 1, y0 + s - 1], fill=(oro if _vecinos_fuera(g, r, c) else col["K"]) + (255,))
            continue
        d.rectangle([x0, y0, x0 + s - 1, y0 + s - 1], fill=col[v] + (255,))
        if v == "P" and (r + abs((c % 8) - 4)) % 6 == 0:
            d.line([x0, y0 + s * 0.5, x0 + s, y0 + s * 0.5], fill=oro + (255,), width=w)
    img = img.resize((N * cel, N * cel), Image.LANCZOS)
    a = np.asarray(img).astype(np.float32)
    # Brillo de la laca: una franja diagonal clara sobre lo negro, más fuerte arriba; en tres escalones (pesa menos).
    yy, xx = np.mgrid[0:N * cel, 0:N * cel] / cel
    franja = np.exp(-((xx * 0.55 - yy + 8) % 22 - 11) ** 2 / 6) * np.clip(1 - (yy - r0) / max(1, r1 - r0) * 0.8, 0.2, 1)
    franja = np.round(franja * 3) / 3
    lum = a[..., :3].mean(2)
    negro = (lum < 70) & (a[..., 3] > 0)
    a[..., :3] = np.where(negro[..., None], a[..., :3] + (np.array(t["brillo_laca"], np.float32) - a[..., :3]) * (franja * 0.45)[..., None], a[..., :3])
    return Image.fromarray(np.clip(a, 0, 255).astype(np.uint8), "RGBA")


PINTORES = {"ukiyoe": pintar_ukiyoe, "rupestre": pintar_rupestre, "laton": pintar_laton, "laca": pintar_laca}
_pintar_prev = g8.pintar


def pintar(g, t, cel, oscuro=False):
    if t["material"] in PINTORES:
        return PINTORES[t["material"]](g, t, cel)
    return _pintar_prev(g, t, cel, oscuro)


g8.pintar = pintar

# =====================================================================
#  Los cuatro temas
# =====================================================================

def colores_obj(nombre, oscuro, j):
    """U, V, X, Q de cada animación propia, de día y de noche."""
    c = {
        ("edo", 0): dict(U=hx("E3B341"), V=hx("F6E39A"), X=hx("8A6A1E"), Q=hx("FFF6D0")),        # koban de oro
        ("edo", 1): dict(U=hx("FAF7EF"), V=hx("D9532E"), X=hx("1E3A5F"), Q=hx("BFD6EA")),        # koi kohaku, estanque
        ("cueva", 0): dict(U=hx("B07418"), V=hx("A2401F"), X=hx("6E5A48"), Q=hx("B4471F")),      # ocre soplado
        ("cueva", 1): dict(U=hx("F4C76E"), V=hx("E8804F"), X=hx("A2401F"), Q=hx("FFE7A0")),      # chispa
        ("vapor", 0): dict(U=hx("B8862B"), V=hx("F1E6CC"), X=hx("3A3430"), Q=hx("FBF7EE")),      # caldera, manómetro, vapor
        ("vapor", 1): dict(U=hx("D8C6A0"), V=hx("9C3D24"), X=hx("5A3A22"), Q=hx("F6E39A")),      # dirigible
        ("deco", 0): dict(U=hx("D9B44A"), V=hx("8A6A1E"), X=hx("2A2010"), Q=hx("1F6B57")),       # trompeta y notas
        ("deco", 1): dict(U=hx("1F6B57"), V=hx("8CC7AE"), X=hx("D9B44A"), Q=hx("F3ECDC")),       # abanico de plumas
    }[(nombre, j)]
    if oscuro:
        if nombre == "deco":
            c["Q"] = hx("5FB59A") if j == 0 else c["Q"]
            if j == 1: c.update(U=hx("5FB59A"), V=hx("BFE6D4"))
        if nombre == "cueva" and j == 0:
            c.update(U=hx("D9AE62"), V=hx("E8804F"), Q=hx("E8804F"))
        if nombre == "vapor" and j == 0:
            c.update(U=hx("DBA641"))
    return c


_colores_prev = g17.colores


def colores(nombre, oscuro, j, t):
    if nombre in MIOS:
        return colores_obj(nombre, oscuro, j)
    return _colores_prev(nombre, oscuro, j, t)


g17.colores = colores
for _n in MIOS:
    g17.COL[_n] = tuple(colores_obj(_n, False, 0)[k] for k in "UVXQ")


def tema_mio(nombre, oscuro):
    if nombre == "edo":
        # edoClaro: washi F2ECDF, sumi 1B1A1E, índigo 1E3A5F, bermellón C9442E, pino 4F7146 · edoOscuro: 111215, EEE7D7, oro E3C77A
        sumi = hx("1B1A1E")
        P = hx("FBF8F1")
        col = g8.pelaje(P, sumi, S=hx("D9D6CF"), L=hx("FFFFFF"), W=hx("2A282C"), Y=mezcla(hx("C9442E"), hx("EBA75A"), 0.55),
                        D=hx("C2BEB6"), T=hx("BDB8AE"), E=mezcla(hx("E3C77A"), hx("4F7146"), 0.35), N=hx("E89A9A"),
                        C=hx("C9442E"), G=hx("E3B341"), A=hx("C9442E"), B=hx("8A1A1A"), J=hx("C9442E"))
        fondo = hx("F2ECDF") if not oscuro else hx("111215")
        t = g8.T(col, fondo, hx("1B1A1E") if not oscuro else hx("EEE7D7"), hx("C8BCA2") if not oscuro else hx("3A3D47"),
                 "manchas", "ukiyoe", ("cascabel", None), bokashi=hx("9DB0C8"), sumi_claro=hx("5B5750"))
        if oscuro:
            t["col"] = g9.de_noche(col, hx("23304A"), 0.1)
            t["bokashi"] = hx("7F93B5")
    elif nombre == "cueva":
        # cuevaClaro: caliza EADCC6, carbón 2A211B / 3B302A, ocre rojo A2401F, ocre amarillo B07418 · cuevaOscuro: 16100C, E8804F, D9AE62
        if not oscuro:
            amar, rojo, carbon, caliza = hx("C08A34"), hx("A2401F"), hx("2E2620"), hx("EFE4CF")
        else:
            amar, rojo, carbon, caliza = hx("D9A050"), hx("D0663A"), hx("1A130F"), hx("EEDFC8")
        col = g8.pelaje(amar, carbon, S=mezcla(amar, rojo, 0.55), L=caliza, W=mezcla(carbon, rojo, 0.3), D=mezcla(rojo, carbon, 0.25), T=mezcla(amar, rojo, 0.7),
                        E=carbon, N=rojo, R=mezcla(amar, rojo, 0.4), C=hx("4D3A2A") if not oscuro else hx("6E5440"),
                        G=hx("F4EADB"), A=rojo, B=mezcla(rojo, carbon, 0.4), J=rojo)
        col["H"] = caliza
        t = g8.T(col, hx("EADCC6") if not oscuro else hx("16100C"), hx("2A211B") if not oscuro else hx("EEDFC8"),
                 hx("C1AB8C") if not oscuro else hx("4B3A2B"), "mechones", "rupestre", ("conchas", None))
        t["sombra_a"] = 0       # es pintura sobre la roca: no proyecta sombra
    elif nombre == "vapor":
        # vaporClaro: papel E9DFC8, hollín 231B14, latón 8A5A14 / B8862B, cardenillo 2F6B5E, ladrillo 9C3D24
        # vaporOscuro: 14110E, EADCC0, latón DBA641, cardenillo 74B8A4, ladrillo E27A55
        P = hx("A7A49E")
        laton = hx("C8952F") if not oscuro else hx("DBA641")
        col = g8.pelaje(P, hx("231B14"), S=hx("837F78"), W=hx("5E5A54"), L=hx("E9E4DA"), D=hx("77736C"), T=hx("6E6A63"),
                        E=mezcla(hx("74B8A4"), hx("2F6B5E"), 0.4), N=hx("D99A90"), C=hx("F4EEE0"), G=laton,
                        A=hx("7A2E22") if not oscuro else hx("8E3A2A"), B=hx("4E1C14") if not oscuro else hx("5E2418"),
                        M=hx("6A4428"), Z=laton, O=mezcla(hx("74B8A4"), hx("FFFFFF"), 0.35), J=hx("9C3D24"))
        t = g8.T(col, hx("E9DFC8") if not oscuro else hx("14110E"), hx("231B14") if not oscuro else hx("EADCC0"),
                 hx("B9A47C") if not oscuro else hx("453A2B"), "atigrado", "laton", ("chaleco_v", "gafas"), metal="GZU")
        if oscuro:
            t["col"] = g9.de_noche(col, hx("3A2A18"), 0.08)
            t["col"].update(G=laton, Z=laton)
    elif nombre == "deco":
        # decoClaro: marfil F3ECDC, laca 16130E, oro 84610F / A8801E, jade 1F6B57, coral B5523B · decoOscuro: 0D0C0A, oro D9B44A, jade 5FB59A, coral E48A6E
        oro = hx("A8801E") if not oscuro else hx("D9B44A")
        col = g8.pelaje(hx("1C1A17"), hx("0B0A08"), S=hx("100F0D"), L=hx("34302A"), W=hx("1C1A17"), D=hx("121110"), T=hx("191714"),
                        E=hx("5FB59A") if oscuro else hx("3E9C7E"), N=oro, R=hx("1C1A17"), C=hx("F6F0E2"), G=hx("F6F0E2"),
                        A=hx("B5523B") if not oscuro else hx("E48A6E"), B=hx("7A2E20") if not oscuro else hx("B5523B"), J=hx("B5523B"))
        t = g8.T(col, hx("F3ECDC") if not oscuro else hx("0D0C0A"), hx("16130E") if not oscuro else hx("F0E6CC"),
                 hx("C7B48A") if not oscuro else hx("4A3E24"), "ninguno", "laca", ("perlas", None),
                 oro=oro, oro_claro=hx("F2D27A"), brillo_laca=hx("8C8270") if not oscuro else hx("9A8E78"))
    else:
        raise ValueError(nombre)
    t["oscuro"] = oscuro
    t["ajustar"] = False
    if oscuro and t.get("sombra_a", 55):
        t["sombra_col"], t["sombra_a"] = (0, 0, 0), 70
    col = dict(t["col"]); col.update(colores_obj(nombre, oscuro, 0))
    t["col"] = col
    return t


_tema_prev = g8.tema


def tema(nombre, oscuro=False):
    if nombre in MIOS:
        return tema_mio(nombre, oscuro)
    return _tema_prev(nombre, oscuro)


for _m in ("gato8", "gato9", "gato10", "gato12", "gato13", "gato16", "gato17", "gato18", "gato19"):
    if _m in sys.modules:
        sys.modules[_m].tema = tema

for _n in MIOS:
    if _n not in g9.NUEVOS:
        g9.NUEVOS.append(_n)


# =====================================================================
#  Las dos acciones de cada tema: poses nuevas (gato de la suerte, koi, soplar ocre, chispa, palanca, trompeta, abanico)
#  y sus objetos. Coordenadas de la cuadrícula de 64, mirando a la derecha.
# =====================================================================
brazo, rect = g17.brazo, g17.rect


def suerte(k):
    """Maneki-neko: sentado, la pata cercana arriba junto a la oreja, que se dobla y se estira para llamar; la otra
    sostiene un koban contra el pecho."""
    dobla = (0, 1, 2, 1)[k]
    p = dict(g8.SENTADO)
    p.update(inc=-3 + 0.8 * dobla, cab=(37.4, 27.6), ojo="abierto" if k != 2 else "medio")
    # El brazo sube por delante de la cara (fuera de la cabeza, para que se vea) y la mano se dobla al llamar.
    p["fn"] = [(37.0, 41.5), (42.6, 36.4), (46.4, 29.6), (47.0 + 1.0 * dobla, 25.4 + 1.2 * dobla)]
    p["ff"] = brazo((32.5, 43), (40.6, 49.0), (0.4, 1.6))
    p["cola"] = [(x + 0.5 * math.sin(k * math.pi / 2) * (i / 5), y) for i, (x, y) in enumerate(g8.SENTADO["cola"])]
    return p, dict(obj=(42.4, 50.0), k=k)


KOI = [(-0.10, 0.0), (0.0, 0.0), (0.18, 0.5), (0.38, 0.92), (0.55, 1.0), (0.72, 0.8), (0.88, 0.35), (1.0, 0.0)]


def koi(k):
    """Sentado frente a un charco: el koi salta en arco y la cabeza lo sigue (de abajo hacia arriba y abajo otra vez)."""
    u, h = KOI[k]
    p = dict(g8.SENTADO)
    p.update(inc=14 - 34 * h, cab=(38.6, 27.6 - 0.6 * h), ojo="abierto")
    p["cola"] = [(x + (0.7 * math.sin(k * 0.8) if i >= 4 else 0), y) for i, (x, y) in enumerate(g8.SENTADO["cola"])]
    x = 49.0 + 9.0 * u
    y = 57.0 - 27.0 * h
    return p, dict(obj=(x, y), ang=(-60 if k <= 2 else (0 if k == 4 else 60)) if 0 < k < 7 else 0, k=k, charco=(53.5, 59.0))


def soplar(k):
    """Pone la pata en la pared, sopla ocre alrededor y la quita: queda la silueta de la pata en negativo."""
    p = dict(g8.SENTADO)
    en_pared = k in (1, 2, 3)
    p.update(cab=(39.6, 27.0), inc=6 if en_pared else -6, lengua=k in (2, 3), ojo="medio" if en_pared else ("cerrado" if k == 5 else "abierto"))
    if k == 0:
        p["fn"] = brazo((36, 43), (44.0, 41.0), (1.2, 0.6))
    elif en_pared:
        p["fn"] = brazo((36, 43), (50.8, 36.6), (1.4, 0.4))
    elif k == 4:
        p["fn"] = brazo((36, 43), (41.0, 50.0), (1.0, 0.8))
    return p, dict(obj=(51.4, 35.0), k=k)


def chispa(k):
    """Sentado, sigue con la cabeza una chispa que sube y baja en ocho."""
    t = 2 * math.pi * k / 6
    x, y = 50 + 5.5 * math.sin(t), 16 + 4.0 * math.sin(2 * t)
    p = dict(g8.SENTADO)
    p.update(inc=-30 + (y - 12) * 1.4, cab=(38.4 + 0.3 * math.sin(t), 27.2))
    p["cola"] = [(xx + (0.8 * math.sin(t) if i >= 4 else 0), yy) for i, (xx, yy) in enumerate(g8.SENTADO["cola"])]
    return p, dict(obj=(x, y), k=k)


PALANCA = [(45.6, 40.6), (47.2, 44.4), (48.6, 48.6), (48.6, 48.6), (48.6, 48.6), (47.2, 44.4)]


def palanca(k):
    """Sentado junto a una caldera: baja la palanca con la pata, el manómetro sube y sale el vapor."""
    px, py = PALANCA[k]
    p = dict(g8.SENTADO)
    p.update(inc=8 + 2 * (py - 40) / 8, cab=(39.4, 28.0), ojo="medio" if k in (3, 4) else "abierto")
    p["fn"] = brazo((36, 43), (px, py), (1.4, -0.2))
    return p, dict(obj=(53.8, 55.0), pata=(px, py), k=k)


def trompeta(k):
    """Sentado, la cabeza alta y la trompeta con las dos patas; la campana se mueve con la frase y salen notas."""
    s = (0, 1, 0, -1)[k]
    p = dict(g8.SENTADO)
    p.update(inc=-14 + 1.5 * s, cab=(38.4, 27.0 + 0.2 * s), ojo="cerrado", lengua=False)
    p["fn"] = brazo((36, 43), (45.4, 31.6 + 0.3 * s), (1.4, 0.4))
    p["ff"] = brazo((32.5, 43), (42.6, 32.6 + 0.3 * s), (1.4, 0.4))
    return p, dict(obj=(45.0, 28.4), k=k, s=s)


def abanico(k):
    """Sentado, se abanica con un abanico de plumas: la pata lo lleva de un lado a otro frente al pecho."""
    a = (-26, 0, 22, 0)[k]
    p = dict(g8.SENTADO)
    p.update(inc=-2 + 0.1 * a, cab=(38.4, 27.6), ojo="medio")
    hx_, hy_ = 43.2 + 0.05 * a, 42.0
    p["fn"] = brazo((36, 43), (hx_, hy_), (1.0, 1.0))
    return p, dict(obj=(hx_ + 0.6, hy_ - 0.6), ang=a, k=k)


g17.ACCIONES.update(suerte=(suerte, 4, "sentado"), koi=(koi, 8, "sentado"), soplar=(soplar, 6, "sentado"),
                    chispa=(chispa, 6, "sentado"), palanca=(palanca, 6, "sentado"), trompeta=(trompeta, 4, "sentado"),
                    abanico=(abanico, 4, "sentado"))
g17.ESCALA.update(suerte=1.0, koi=1.0, soplar=1.0, chispa=1.0, palanca=1.0, trompeta=1.0, abanico=1.0)


# --- objetos
def o_koban(L, a):
    """Koban: moneda ovalada de oro con las franjas del martillo y el sello."""
    x, y = a["obj"]
    L.elipse("f_U", x, y, 2.3, 3.4)
    for j in (-1.6, -0.5, 0.6, 1.7):
        L.capsula("f_X", [(x - 1.2, y + j), (x + 1.2, y + j)], 0.35)
    L.elipse("f_V", x - 0.6, y - 2.0, 0.6, 0.5)


def o_koi(L, a):
    x, y = a["obj"]; k = a["k"]; ang = math.radians(a["ang"])
    cx, cy = a["charco"]
    L.elipse("b_X", cx, cy, 8.4, 1.9)                      # el charco (estanque) en el suelo
    L.capsula("b_Q", [(cx - 5.0, cy - 0.5), (cx - 1.0, cy - 0.5)], 0.5)
    if k in (0, 1, 7):                                      # ondas y salpicadura
        L.elipse("b_Q", cx - 2.6, cy - 0.3, 2.2, 0.5)
        L.elipse("b_Q", cx + 3.4, cy - 0.2, 1.4, 0.4)
    if k in (1, 2, 6, 7):
        for dx_, h_ in ((-3, 2.6), (-1, 4.2), (1.6, 3.4), (3.6, 2.0)):
            L.elipse("b_Q", x + dx_ * 0.9, cy - h_ - (1.2 if k in (2, 6) else 0), 0.55, 0.65)
    if k in (0, 7):
        return
    s = 1.55                                                                       # el koi, más grande que el resto
    P = lambda pts: [g8.rot(x, y, px_ * s, py_ * s, ang) for px_, py_ in pts]
    L.elipse("b_U", x, y, 1.8 * s, 3.6 * s, a["ang"])                               # cuerpo
    L.poligono("b_U", P([(-1.0, 3.0), (0, 5.4), (-2.6, 6.6)])); L.poligono("b_U", P([(1.0, 3.0), (0, 5.4), (2.6, 6.6)]))   # cola
    L.elipse("b_V", *g8.rot(x, y, 0.2 * s, -1.2 * s, ang), 1.3 * s, 1.5 * s, a["ang"])     # manchas rojas
    L.elipse("b_V", *g8.rot(x, y, -0.3 * s, 2.0 * s, ang), 0.9 * s, 0.9 * s, a["ang"])
    L.poligono("b_U", P([(1.4, 0.2), (3.0, 1.6), (1.4, 1.6)]))                     # aleta
    L.elipse("b_K", *g8.rot(x, y, 0.8 * s, -2.5 * s, ang), 0.45, 0.45)              # ojo


def o_ocre(L, a):
    """Mano en negativo: puntos de ocre soplado en una retícula (sin cerrar huecos) alrededor de la silueta de la pata."""
    x, y = a["obj"]; k = a["k"]
    if k == 0:
        return
    # La silueta de la pata: almohadilla y cuatro dedos pegados a ella (un solo hueco grande; si quedaran huecos chicos
    # sueltos, el acabado los rellenaría de pelaje).
    huella = [(x, y + 1.0, 2.5, 2.1)] + [(x + dx_, y + dy_, 1.15, 1.3) for dx_, dy_ in ((-2.5, -1.3), (-1.0, -2.7), (1.0, -2.7), (2.5, -1.3))]
    dentro = lambda px_, py_, m=0.15: any(((px_ - hx_) / (rx + m)) ** 2 + ((py_ - hy_) / (ry + m)) ** 2 <= 1 for hx_, hy_, rx, ry in huella)
    rnd = random.Random(5)
    nucleo = {1: 0.0, 2: 0.45, 3: 0.68, 4: 0.68, 5: 0.68}[k]       # el centro del soplo, macizo
    alcance = {1: 0.7, 2: 0.9, 3: 1.0, 4: 1.0, 5: 1.0}[k]
    for r in range(int(y - 9), int(y + 9)):
        for c in range(int(x - 9), int(x + 9)):
            px_, py_ = c + 0.5, r + 0.5
            dd = math.hypot((px_ - x) / 6.6, (py_ - y) / 7.0)
            if dd >= alcance or dentro(px_, py_): continue
            q = rnd.random()
            if dd < nucleo or (r % 2 == 0 and c % 2 == 0 and q < 1.2 - dd * 0.7):
                L.elipse("b_Q", px_, py_, 0.55, 0.55)
    if k in (2, 3):                                            # el soplo que sale de la boca
        for j in range(4):
            L.elipse("b_Q", x - 6.0 + j * 1.6 + 0.5 * (j % 2), y - 4.8 + j * 0.9, 0.5, 0.5)


def o_chispa(L, a):
    x, y = a["obj"]; k = a["k"]
    if k % 2 == 0:
        L.capsula("b_Q", [(x - 1.6, y), (x + 1.6, y)], 0.6); L.capsula("b_Q", [(x, y - 1.6), (x, y + 1.6)], 0.6)
    L.elipse("b_Q", x, y, 0.9, 0.9)
    t = 2 * math.pi * k / 6
    for j in (1, 2):                                       # estela
        tt = t - 0.5 * j
        L.elipse("b_Q", 50 + 5.5 * math.sin(tt), 16 + 4.0 * math.sin(2 * tt), 0.5 / j + 0.15, 0.5 / j + 0.15)


def o_palanca(L, a):
    x, y = a["obj"]; k = a["k"]; px, py = a["pata"]
    L.poligono("b_U", [(x - 5.4, y + 4.6), (x + 5.4, y + 4.6), (x + 5.0, y - 3.4), (x - 5.0, y - 3.4)])     # caja de latón
    L.capsula("b_X", [(x - 5.4, y + 4.4), (x + 5.4, y + 4.4)], 0.9)
    for xx in (x - 4.0, x + 4.0):
        L.elipse("b_X", xx, y - 2.4, 0.45, 0.45)                                                         # remaches
    L.elipse("b_V", x + 1.0, y - 0.2, 2.8, 2.8)
    L.capsula("b_X", [(x + 1.0 + 2.8 * math.cos(u), y - 0.2 + 2.8 * math.sin(u)) for u in np.linspace(0, 2 * math.pi, 24)], 0.6)             # manómetro
    ag = math.radians(200 + 70 * (py - 40.6) / 8)
    L.capsula("b_K", [(x + 1.0, y - 0.2), (x + 1.0 + 1.9 * math.cos(ag), y - 0.2 + 1.9 * math.sin(ag))], 0.45)
    L.capsula("b_X", [(x + 3.6, y - 3.4), (x + 3.6, y - 7.0)], 1.4)                                      # tubo de escape
    if k in (2, 3, 4):
        for j in range(3):
            yy = y - 8.6 - j * 1.8 - (k - 2) * 1.0
            L.elipse("b_Q", x + 3.6 + 0.8 * math.sin(j + k), yy, 1.0 + 0.5 * j + 0.3 * (k - 2), 0.8 + 0.3 * j)
    piv = (x - 4.2, y - 2.0)                                                                             # palanca
    L.capsula("f_U", [piv, (px + 0.6, py + 0.4)], 1.0)
    L.elipse("f_A", px + 0.9, py + 0.2, 1.3, 1.3)
    L.elipse("b_U", *piv, 1.0, 1.0)


def o_dirigible(L, a):
    x, y = a["obj"]; k = a["k"]
    L.elipse("b_U", x, y, 5.6, 2.3)                                                     # globo
    for j in (-2.6, 0, 2.6):
        L.capsula("b_X", [(x + j, y - 2.0), (x + j, y + 2.0)], 0.3)                    # costillas
    L.poligono("b_V", [(x - 5.0, y - 0.6), (x - 7.4, y - 2.6), (x - 7.0, y - 0.2)])       # timón
    L.poligono("b_V", [(x - 5.0, y + 0.6), (x - 7.4, y + 2.4), (x - 7.0, y + 0.2)])
    L.capsula("b_X", [(x - 1.6, y + 2.2), (x - 1.0, y + 3.4)], 0.3); L.capsula("b_X", [(x + 1.6, y + 2.2), (x + 1.0, y + 3.4)], 0.3)
    rect(L, "b_V", x - 1.6, y + 3.2, x + 1.6, y + 4.4)                                    # barquilla
    L.capsula("b_Q", [(x + 6.0, y - (1.2 if k % 2 else 0.2)), (x + 6.0, y + (1.2 if k % 2 == 0 else 0.2))], 0.45)   # hélice


def o_trompeta(L, a):
    x, y = a["obj"]; k = a["k"]; s = a["s"]
    ang = math.radians(-16 + 2 * s)
    P = lambda px_, py_: g8.rot(x, y, px_, py_, ang)
    L.capsula("f_U", [P(0.6, 0.6), P(9.0, 0.6)], 1.1)                                   # tubo
    L.capsula("f_U", [P(2.4, 0.6), P(3.4, -1.4), P(6.6, -1.4), P(7.4, 0.6)], 0.95)        # vuelta del tubo
    for j in (3.8, 4.8, 5.8):
        L.capsula("f_V", [P(j, -1.2), P(j, -2.6)], 0.55)                                 # pistones
    L.poligono("f_U", [P(9.0, 0.0), P(12.6, -2.6), P(12.6, 3.8), P(9.0, 1.2)])           # campana
    L.capsula("f_X", [P(12.6, -2.4), P(12.6, 3.6)], 0.5)
    for j in range(2):                                                                    # notas que suben
        f = ((k + j * 2) % 4) / 4
        nx_, ny_ = P(13.0 + f * 3.0 + j * 1.6, -1.6 - f * 3.6 - j * 0.8)
        L.elipse("b_Q", nx_, ny_, 0.9, 0.7, -20)
        L.capsula("b_Q", [(nx_ + 0.7, ny_), (nx_ + 0.7, ny_ - 2.6)], 0.4)
        if j == 0: L.capsula("b_Q", [(nx_ + 0.7, ny_ - 2.6), (nx_ + 1.8, ny_ - 1.8)], 0.4)


def o_abanico(L, a):
    x, y = a["obj"]; ang = a["ang"]
    for j in range(6):                                                                  # plumas de avestruz
        an = math.radians(-118 + j * 19 + ang)
        cx_, cy_ = x + 6.0 * math.cos(an), y + 6.0 * math.sin(an)
        L.elipse("b_U" if j % 2 == 0 else "b_V", cx_, cy_, 3.8, 1.25, math.degrees(an))
        L.elipse("b_U" if j % 2 == 0 else "b_V", x + 8.6 * math.cos(an), y + 8.6 * math.sin(an), 1.3, 1.5, math.degrees(an))
        L.capsula("b_X", [(x, y), (x + 8.0 * math.cos(an), y + 8.0 * math.sin(an))], 0.35)
    L.capsula("b_X", [(x, y), (x + 1.4, y + 2.4)], 0.9)


ESP_MIOS = {
    "edo": [("suerte", o_koban, "llama la suerte como maneki-neko"), ("koi", o_koi, "mira saltar un koi")],
    "cueva": [("soplar", o_ocre, "sopla ocre y deja la silueta de su pata"), ("chispa", o_chispa, "sigue una chispa del fuego")],
    "vapor": [("palanca", o_palanca, "baja una palanca de vapor"), ("mirar", o_dirigible, "sigue a un dirigible")],
    "deco": [("trompeta", o_trompeta, "toca la trompeta"), ("abanico", o_abanico, "se abanica con plumas")],
}
ESCALA_MIA = {("vapor", 1): 1.9, ("deco", 0): 1.35, ("deco", 1): 1.45, ("cueva", 1): 1.5}
g17.ESP.update(ESP_MIOS)
for nombre, lista in ESP_MIOS.items():
    for j, (accion, f, _) in enumerate(lista):
        fn, n, _base = g17.ACCIONES[accion]
        cuadros = []
        for k in range(n):
            p, a = fn(k)
            a["accion"] = accion
            a["escala"] = ESCALA_MIA.get((nombre, j), g17.ESCALA[accion])
            cuadros.append(dict(p, objeto=(f, a), semilla=k))
        g8.ANIMS[f"esp_{nombre}_{j}"] = cuadros
g8._rej.clear(); g8._spr.clear()


# =====================================================================
#  Fondos (a 1 pixel por dp, ampliados ×3 sin suavizar, como los de gato9). Todo lo que se mueve repite cada BUCLE cuadros.
# =====================================================================

def punto(px, x, y, col):
    if 0 <= x < W and 0 <= y < H:
        px[int(x), int(y)] = col


def banda_kasumi(im, x0, x1, y, alto, col, borde):
    """Kasumi: la franja de bruma de los grabados, larga y de puntas redondas, con el borde de abajo más marcado."""
    d = ImageDraw.Draw(im)
    d.rounded_rectangle([x0, y, x1, y + alto], radius=alto // 2, fill=col)
    d.line([x0 + alto // 2, y + alto, x1 - alto // 2, y + alto], fill=borde)


def ola_grande(im, x0, pal):
    """La gran ola, a la manera de Hokusai: lomo de azul de Prusia, vetas claras, la cresta que se enrosca y sus garras de espuma."""
    azul, azul2, azul3, espuma, sombra = pal
    d = ImageDraw.Draw(im)
    lomo = [(x0 + 0, 66), (x0 + 6, 54), (x0 + 16, 42), (x0 + 28, 31), (x0 + 40, 24), (x0 + 52, 21), (x0 + 62, 23), (x0 + 69, 28),
            (x0 + 70, 33), (x0 + 65, 31), (x0 + 59, 30), (x0 + 55, 33), (x0 + 56, 40), (x0 + 62, 48), (x0 + 72, 56), (x0 + 86, 62), (x0 + 104, 66)]
    d.polygon(lomo, fill=azul)
    for k, col in ((4, azul2), (8, azul3)):          # vetas: el mismo lomo, corrido hacia adentro
        pts = [(x + k * 0.55, y + k * 0.9) for x, y in lomo[1:9]]
        d.line(pts, fill=col)
    d.line(lomo[1:10], fill=espuma)                   # la cresta
    for k in range(9):                                # garras de espuma que cuelgan del rizo
        x = x0 + 46 + k * 2.6
        y = 21 + abs(k - 4.5) * 0.8
        d.polygon([(x, y), (x + 2.2, y + 0.4), (x + 1.4, y + 3.2 + (k % 3))], fill=espuma)
    d.line([(x0 + 62, 30), (x0 + 58, 31), (x0 + 56, 35), (x0 + 58, 41)], fill=sombra)
    rnd = random.Random(x0)
    for _ in range(26):                               # rocío
        punto(im.load(), x0 + 40 + rnd.randint(0, 34), 12 + rnd.randint(0, 9), espuma)
    return [(x0 + 46 + k * 2.6, 21 + abs(k - 4.5) * 0.8) for k in range(9)]


def fondo_edo(t):
    """Japón Edo: una lámina de ukiyo-e. De día, el cielo con bokashi de índigo, el monte nevado, la gran ola, el puente
    de madera sobre el río y el camino real, con un cerezo que suelta pétalos. De noche, los farolillos encendidos sobre
    el puente, la luna llena y la nieve."""
    o = t["oscuro"]
    if not o:
        washi = hx("F2ECDF")
        cielo = [hx("1E3A5F"), hx("2F527C"), hx("5D7FA6"), hx("A9BCD0"), washi, washi]
        monte, monte2, nieve, nieve2 = hx("566F93"), hx("41587A"), hx("FAF7EF"), hx("D8DEE6")
        bruma, bruma_b = hx("F7EBD8"), hx("E2CDB0")
        pal_ola = (hx("1E3A5F"), hx("3E6A98"), hx("7FA0C4"), hx("FAF7EF"), hx("0E1B2E"))
        rio, rio2 = hx("3E6A98"), hx("6F93BC")
        madera, madera2, baranda = hx("8A5A34"), hx("5E3A20"), hx("C9442E")
        pino, pino2, tronco = hx("4F7146"), hx("33502F"), hx("5E3A20")
        camino, camino2, borde_c = hx("E2D2B0"), hx("CDB88E"), hx("A88E62")
        flor, flor2 = hx("F4C6CF"), hx("E899AA")
    else:
        washi = hx("1A1F2E")
        cielo = [hx("070A12"), hx("0E1424"), hx("162038"), hx("1E2A44"), hx("27365A"), hx("2E3F66")]
        monte, monte2, nieve, nieve2 = hx("1B2742"), hx("141D33"), hx("C9D2E2"), hx("8C99B4")
        bruma, bruma_b = hx("2A3756"), hx("22304C")
        pal_ola = (hx("0E1B2E"), hx("1F3656"), hx("3A5680"), hx("C9D2E2"), hx("05080F"))
        rio, rio2 = hx("142238"), hx("24375A")
        madera, madera2, baranda = hx("3E2A1E"), hx("281A12"), hx("7A2A1E")
        pino, pino2, tronco = hx("1C2A24"), hx("131E1A"), hx("2A1C14")
        camino, camino2, borde_c = hx("D8DEE8"), hx("B8C2D2"), hx("8C99B4")       # el camino, nevado
        flor, flor2 = hx("E6EAF2"), hx("B8C2D2")
    im, d = lienzo(washi)
    degradado(im, 0, 34, cielo)
    px = im.load()
    if o:
        estrellas(im, 31, 40, 0, 26, [hx("C9D2E2"), hx("8C99B4"), hx("E3C77A")], evitar=lambda x, y: abs(x - 96) < 14 and y < 26)
        luz(im, 96, 13, 16, hx("3A4A70"), 0.5, 3)
        d.ellipse([90, 7, 102, 19], fill=hx("F1EBDA")); d.ellipse([93, 9, 97, 13], fill=hx("DCD3BC")); d.point((99, 15), fill=hx("DCD3BC"))
    else:
        d.ellipse([84, 8, 96, 20], fill=hx("D9532E"))          # el sol rojo, sin borde, como en la estampa
    # El monte (Fuji): cono ancho, nieve con las vetas que bajan por las cañadas, falda que se pierde en la bruma.
    cx, cima = 300, 15
    for x in range(200, 400):
        top = cima + abs(x - cx) * (0.36 if abs(x - cx) < 18 else 0.42) - (1 if abs(x - cx) < 4 else 0)
        for y in range(int(top), 50):
            nev = y < cima + 9 + (3 if (x // 3) % 2 == 0 else 0) + (2 if abs(x - cx) < 6 else 0)
            punto(px, x, y, (nieve if x <= cx + 2 else nieve2) if nev else (monte if x <= cx else monte2))
    d.line([(cx - 3, cima), (cx + 3, cima)], fill=nieve)
    # Bruma en franjas que cruzan el monte y la ciudad.
    banda_kasumi(im, 186, 412, 40, 6, bruma, bruma_b)
    banda_kasumi(im, 140, 260, 33, 4, bruma, bruma_b)
    # El agua: mar a la izquierda (donde rompe la ola) y el río bajo el puente.
    for y in range(50, 66):
        for x in range(W):
            col = rio if (x + 2 * y) % 9 else rio2
            punto(px, x, y, col)
    for y in range(52, 66, 3):                                 # olitas en escamas (seigaiha), del mar
        for x in range((y * 5) % 8, 150, 8):
            d.arc([x, y, x + 7, y + 6], 180, 360, fill=pal_ola[2] if not o else pal_ola[1])
    garras = ola_grande(im, 2, pal_ola)
    # El puente de madera en arco (taiko-bashi), con baranda bermellón y pilotes.
    bx0, bx1, by = 176, 268, 52
    arco = lambda x: by - 9 * math.sin(math.pi * (x - bx0) / (bx1 - bx0))
    for x in range(bx0, bx1 + 1):
        y = arco(x)
        d.line([x, y, x, y + 2], fill=madera)
        punto(px, x, y + 3, madera2)
        punto(px, x, y - 4, baranda)
        if (x - bx0) % 8 == 0:
            d.line([x, y - 4, x, y], fill=baranda)
    for x in range(bx0 + 10, bx1 - 5, 14):                     # pilotes
        d.line([x, arco(x) + 3, x, 64], fill=madera2); d.line([x + 1, arco(x) + 3, x + 1, 64], fill=madera)
    faroles = []
    if o:                                                      # farolillos colgados de la baranda
        for k, x in enumerate(range(bx0 + 6, bx1 - 4, 11)):
            y = arco(x) - 8
            d.line([x, y - 2, x, y], fill=madera2)
            faroles.append((x, y + 2))
    # El camino real: tierra apisonada (de noche, nieve), con su borde y piedras.
    for y in range(64, H):
        for x in range(W):
            punto(px, x, y, camino if ((x * 7 + y * 13) % 23) else camino2)
    d.line([0, 64, W, 64], fill=borde_c)
    rnd = random.Random(12)
    for _ in range(40):
        x, y = rnd.randrange(W), rnd.randrange(67, H)
        d.point((x, y), fill=borde_c)
    # Linterna de piedra (tōrō) junto al camino.
    lx = 150
    piedra, piedra2 = (hx("9A968C"), hx("6E6A62")) if not o else (hx("4A5168"), hx("343A50"))
    d.rectangle([lx - 1, 54, lx + 1, 65], fill=piedra); d.rectangle([lx - 4, 64, lx + 4, 66], fill=piedra2)
    d.rectangle([lx - 3, 50, lx + 3, 54], fill=piedra2 if not o else hx("E37A4A"))
    d.rectangle([lx - 2, 51, lx + 2, 53], fill=hx("2A2420") if not o else hx("F6C177"))
    d.polygon([(lx - 6, 50), (lx, 45), (lx + 6, 50)], fill=piedra); d.point((lx, 44), fill=piedra2)
    # Pino retorcido a la derecha, con sus nubes de agujas.
    d.line([(392, 66), (390, 56), (394, 46), (388, 36), (380, 30)], fill=tronco, width=3)
    d.line([(392, 50), (404, 44)], fill=tronco, width=2)
    for (x, y, w_) in ((376, 28, 14), (392, 40, 16), (402, 42, 12), (384, 34, 10), (396, 30, 12)):
        d.ellipse([x - w_ // 2, y - 3, x + w_ // 2, y + 2], fill=pino)
        d.line([x - w_ // 2 + 2, y + 2, x + w_ // 2 - 2, y + 2], fill=pino2)
        if o: d.line([x - w_ // 2 + 2, y - 3, x + w_ // 2 - 2, y - 3], fill=nieve)
    # La rama del cerezo que entra por arriba, de donde caen los pétalos (de noche, cargada de nieve).
    d.line([(W, 2), (380, 6), (356, 4), (340, 9)], fill=tronco, width=2)
    d.line([(380, 6), (372, 12)], fill=tronco); d.line([(356, 4), (350, -1)], fill=tronco)
    rnd = random.Random(3)
    for _ in range(46):
        x, y = rnd.randint(336, W - 1), rnd.randint(0, 13)
        if abs(y - (2 + (W - x) * 0.07)) < 6:
            d.point((x, y), fill=flor if rnd.random() < 0.6 else flor2)
    # El sello del grabador (bermellón) arriba a la izquierda, sobre el cielo.
    if not o:
        d.rectangle([4, 3, 12, 13], fill=hx("C9442E"))
        d.rectangle([6, 5, 10, 11], outline=hx("F2ECDF"))
        d.line([8, 5, 8, 11], fill=hx("F2ECDF"))
    if o:
        for x, y in faroles:                                  # luz de los farolillos en el agua y alrededor
            luz(im, x, y, 9, hx("E37A4A"), 0.35, 3)
            for k in range(3):
                d.line([x - 1, 56 + k * 3, x + 1, 56 + k * 3], fill=mezcla(rio2, hx("E37A4A"), 0.6))
    big = subir(im)
    # Lo que se mueve.
    rnd = random.Random(44)
    caidas = [(rnd.uniform(300, 420), rnd.uniform(0, 80), rnd.uniform(0.18, 0.32), rnd.uniform(0, 6.3)) for _ in range(14 if not o else 34)]
    brillos = [(rnd.randrange(0, 420), rnd.randrange(52, 64)) for _ in range(40)]

    def agua(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img); tk = tick % BUCLE
        for j, (x, y) in enumerate(brillos):                  # brillos del agua
            if 2 + 0 <= x and (j * 5 + tk // 4) % 8 == 0 and not (x < 106 and y < 60):
                cuad(dd, x, y, rio2 if not o else hx("8C99B4"), 2, 1)
        for j, (x, y) in enumerate(garras):                   # la espuma de la cresta se deshace y vuelve
            if (j + tk // 5) % 4 == 0:
                cuad(dd, x + 1, y + 4, pal_ola[3])
        if o:
            for j, (x, y) in enumerate(faroles):              # farolillos: cuerpo rojo que titila
                c = hx("F08A5A") if (j * 3 + tk // 6) % 5 else hx("FFB070")
                cuad(dd, x - 1.5, y - 2, c, 3, 4); cuad(dd, x - 1.5, y - 2.5, hx("2A1C14"), 3, 0.6); cuad(dd, x - 1.5, y + 2, hx("2A1C14"), 3, 0.6)
                cuad(dd, x - 0.5, y - 1, hx("FFE2A8"), 1, 2)
                for k in range(3):
                    if (j + k + tk // 4) % 3:
                        cuad(dd, x - 1 + ((tk // 8 + k) % 3) * 0.7, 56 + k * 3, mezcla(hx("E37A4A"), hx("FFE2A8"), 0.3), 2, 1)

    def cae(img, tick, delante):
        if not delante: return
        dd = ImageDraw.Draw(img); tk = tick % BUCLE
        for (x0, y0, v, ph) in caidas:
            f = tk / BUCLE
            yy = (y0 + f * (H + 4) * (2 if o else 1)) % (H + 4)
            y = yy - 2
            x = (x0 - yy * (0.9 if not o else 0.25) + 3 * math.sin(fase(tk, BUCLE, 4) + ph)) % 430 - 9
            if not o:
                cuad(dd, x, y, flor if (tk // 5 + int(ph * 3)) % 3 else flor2, 1, 1)
            else:
                cuad(dd, x, y, hx("EEF1F6"), 1, 1)
    ANIM[("edo", o)] = [agua, cae]
    return big


ANIM = g9.ANIM


# ---------------------------------------------------------------------
#  Prehistoria
# ---------------------------------------------------------------------

def roca(im, tonos, semilla, x0=0, x1=W, y0=0, y1=H, mascara=None, luz_=None):
    """Roca de pixel art: lajas de pocos tonos (ruido suave cuantizado, tramado solo en el filo entre tonos), con la
    sombra bajo cada saliente y un filo de luz encima. `luz_(x, y)` en [0, 1] corre el tono hacia los claros (la luz
    de la lámpara)."""
    px = im.load()
    n = len(tonos) - 1
    nz = ruido(512, semilla, 22)[:H, :W]
    nz2 = ruido(512, semilla + 1, 3)[:H, :W]
    v = 0.85 * nz + 0.15 * nz2
    v = (v - v.min()) / max(1e-6, v.max() - v.min())
    if luz_:
        l = np.array([[luz_(x, y) for x in range(W)] for y in range(H)])
        v = np.clip(0.45 * v + 0.75 * l - 0.12, 0, 0.999)
    else:
        v = np.clip(v * 1.1 - 0.05, 0, 0.999)
    vv = v * (n + 1) - 0.5
    idx = np.floor(vv).astype(int)
    f = vv - idx
    for y in range(H):
        for x in range(W):
            if f[y, x] > 0.72 and tramar((f[y, x] - 0.72) / 0.28, x, y):
                idx[y, x] += 1
    idx = np.clip(idx, 0, n)
    out = idx.copy()
    out[1:][idx[:-1] > idx[1:]] -= 1            # sombra bajo la laja más clara de arriba
    sube = np.zeros_like(idx, bool); sube[1:] = idx[1:] > idx[:-1]
    out[sube] += 1                              # filo de luz en el borde de arriba de cada laja
    out = np.clip(out, 0, n)
    for y in range(max(0, y0), min(H, y1)):
        for x in range(max(0, x0), min(W, x1)):
            if mascara and not mascara(x, y): continue
            px[x, y] = tonos[out[y, x]]


def mano_negativa(im, x, y, col, fondo_, s=1.0, semilla=0, ang=0.0, fuerza=0.85):
    """Mano en negativo: pigmento soplado alrededor de la mano apoyada en la pared; la mano queda del color de la roca."""
    px = im.load(); rnd = random.Random(semilla)
    a = math.radians(ang)

    lado = 1 if ang >= 0 else -1                 # de qué lado sale el pulgar (mano izquierda o derecha)
    largos = [round(v * s) for v in (3.0, 4.2, 4.6, 3.6)]
    inclina = math.tan(math.radians(ang) * 0.5)

    def en_mano(u, v):
        """A la escala de la pared (1 pixel por dp): dedos de 1 pixel con 1 pixel de separación, palma y pulgar."""
        u = u - inclina * v
        if (u / 3.7) ** 2 + ((v - 2.6) / 3.1) ** 2 <= 1: return True                            # palma
        for c, l in zip((-3, -1, 1, 3), largos):
            if abs(u - c) < 0.5 and -l <= v <= 0: return True                                     # dedos
        w = u * lado
        return 3.5 <= w <= 3.5 + 2.4 * s and abs(v - (2.2 - (w - 3.5) * 1.1)) < 0.75            # pulgar
    R = 7.5 * s
    for yy in range(int(y - R), int(y + R) + 1):
        for xx in range(int(x - R), int(x + R) + 1):
            if not (0 <= xx < W and 0 <= yy < H): continue
            dd = math.hypot(xx - x, (yy - y + 0.5 * s) * 0.9) / R
            if dd >= 1: continue
            if en_mano(xx - x, yy - y):
                if fondo_: px[xx, yy] = fondo_
                continue
            k = fuerza * (1 - dd ** 1.6)
            if rnd.random() < k + 0.15:
                px[xx, yy] = mezcla(px[xx, yy], col, min(1, k + 0.25))


def bisonte(d, x, y, cuerpo, linea, s=1.0, mira=1):
    """Bisonte a la manera de Altamira (mirando a la derecha si mira=1): giba alta sobre los hombros, cabeza baja y
    oscura con barba y cuerno curvo, cuarto trasero más bajo, patas finas y cola con borla. Ocre con carbón encima."""
    m = mira
    P = lambda pts: [(x + m * u * s, y + v * s) for u, v in pts]
    cont = [(-13, -4), (-8, -6), (-3, -7), (0, -9), (3, -12), (5, -13), (7, -12), (9, -9), (12, -7), (15, -5), (17, -2),
            (17, 1), (14, 2), (12, 5), (9, 4), (4, 4), (-2, 3), (-8, 3), (-12, 2), (-14, -1)]
    d.polygon(P(cont), fill=cuerpo)
    d.polygon(P([(8, -10), (12, -7), (15, -5), (17, -2), (17, 1), (14, 2), (12, 5), (10, 3), (9, -1)]), fill=linea)    # cabeza y barba
    d.line(P([(13, -7), (14, -10), (16, -10)]), fill=linea)                                                            # cuerno
    d.line(P(cont + [cont[0]]), fill=linea)
    for k in range(3):                                                                                                 # crin del hombro
        d.line(P([(2 + k * 2, -11 + k * 0.5), (4 + k * 2, -5)]), fill=linea)
    for u, du in ((-11, -1), (-8, 1), (6, 0), (9, 1)):                                                                 # patas y pezuñas
        d.line(P([(u, 2), (u + du, 8)]), fill=linea, width=max(1, round(s)))
        d.line(P([(u + du - 1, 8), (u + du + 1, 8)]), fill=linea)
    d.line(P([(-13, -3), (-15, 1), (-15, 5)]), fill=linea); d.line(P([(-16, 5), (-14, 5)]), fill=linea)              # cola
    d.point(P([(14, -4)])[0], fill=cuerpo)                                                                             # ojo


def caballo_rupestre(d, x, y, cuerpo, linea, s=1.0, mira=1):
    """Caballo de Lascaux: vientre redondo, cuello grueso y arqueado, cabeza chica, crin y hocico negros, patas cortas."""
    m = mira
    P = lambda pts: [(x + m * u * s, y + v * s) for u, v in pts]
    cont = [(-12, -5), (-4, -6), (3, -7), (6, -10), (9, -13), (11, -13), (14, -10), (14, -8), (12, -7), (9, -7), (8, -2),
            (4, 2), (-4, 3), (-9, 1), (-12, -2)]
    d.polygon(P(cont), fill=cuerpo)
    d.line(P(cont + [cont[0]]), fill=linea)
    d.line(P([(3, -8), (6, -11), (9, -14)]), fill=linea, width=max(1, round(s * 1.5)))                              # crin
    d.polygon(P([(12, -11), (14, -10), (14, -8), (12, -8)]), fill=linea)                                             # hocico
    for u, dx in ((-9, -1), (-6, 1), (4, 1), (7, 2)):
        d.line(P([(u, 1), (u + dx, 8)]), fill=linea)
    d.line(P([(-12, -4), (-15, 0), (-15, 5)]), fill=linea, width=max(1, round(s)))


def fondo_cueva(t):
    """Prehistoria. De día, la boca de la cueva vista desde adentro: la roca enmarca el valle (estepa, río, mamuts) y en el
    piso arde el fuego del campamento; en la pared, unas manos y puntos de ocre. De noche, lo hondo de la cueva: la pared
    con manos en negativo, bisontes y caballos, a la luz de una lámpara de grasa que titila."""
    o = t["oscuro"]
    ocre, ocre2, carbon = hx("A2401F"), hx("B07418"), hx("2E2620")
    if not o:
        tonos = [hx("4D4037"), hx("6E5C4C"), hx("8E7A63"), hx("A9947A"), hx("C1AB8C")]
        im, d = lienzo(hx("C1AB8C"))
        borde_iz = lambda y: 158 - 10 * (1 - y / 62) ** 2 + 3 * math.sin(y / 3.1)
        borde_de = lambda y: 364 + 6 * (1 - y / 62) ** 2 - 3 * math.sin(y / 2.7 + 1)
        techo = lambda x: 9 + 0.0034 * (x - 262) ** 2 - 2.4 * math.sin(x / 7) - 1.6 * math.sin(x / 17 + 2)
        boca = lambda x, y: y < 62 and borde_iz(y) < x < borde_de(y) and y > techo(x)
        # Afuera: el cielo, la estepa, el río y los mamuts.
        degradado(im, 0, 44, [hx("BFD3D6"), hx("D3E0DC"), hx("E6E6D6"), hx("EFE6D2")], 150, 372)
        for x, y, w_ in ((190, 20, 22), (300, 16, 30)):
            nube(d, x, y, w_, hx("F6F2E8"), hx("E6E6D6"), hx("CFD6CE"))
        perfil(im, ondas(71, 40, ((3, 70), (1.5, 25))), hx("A7B3A0"), 62, x0=150, x1=372)
        perfil(im, ondas(72, 47, ((2, 90), (1, 30))), hx("B8B07A"), 62, borde=hx("C8C08A"), x0=150, x1=372)
        for y in range(53, 57):
            for x in range(150, 372):
                if abs(y - 55 - 1.2 * math.sin(x / 19)) < 1.3: punto(im.load(), x, y, hx("8FB3BF"))
        perfil(im, ondas(73, 57, ((2, 60), (0.8, 20))), hx("9C9A5E"), 62, borde=hx("B4B06C"), x0=150, x1=372)
        for k, x in enumerate((222, 236, 300)):                  # mamuts a lo lejos
            y = 47 + (1 if k == 2 else 0)
            col = hx("5A4636")
            d.ellipse([x - 4, y - 4, x + 3, y], fill=col); d.ellipse([x + 1, y - 5, x + 5, y - 1], fill=col)
            d.line([x + 5, y - 2, x + 6, y + 1], fill=col); d.line([x + 4, y - 1, x + 6, y - 1], fill=hx("EFE6D2"))
            for lx in (x - 3, x - 1, x + 1, x + 2): d.line([lx, y, lx, y + 2], fill=col)
        # La roca alrededor de la boca.
        roca(im, tonos, 21, mascara=lambda x, y: not boca(x, y))
        px = im.load()
        for x in range(150, 373):                                # borde de la boca: un filo de luz
            for y in range(0, 62):
                if not boca(x, y) and (boca(x, y + 1) or boca(x - 1, y) or boca(x + 1, y)):
                    px[x, y] = hx("D8C6A6")
        # Estalactitas en el techo de la boca.
        for x in range(160, 368, 9):
            top = techo(x)
            if not (borde_iz(top + 2) < x < borde_de(top + 2)): continue
            largo = 3 + (x * 7) % 5
            d.polygon([(x - 1.5, top), (x + 1.5, top), (x, top + largo)], fill=tonos[1])
        # Piso de la cueva: tierra apisonada y lajas.
        degradado(im, 62, H, [hx("A08A6E"), hx("8E7A63"), hx("7A6652")])
        d.line([0, 62, W, 62], fill=hx("6E5C4C"))
        rnd = random.Random(9)
        for _ in range(26):
            x, y = rnd.randrange(W), rnd.randrange(66, H)
            d.ellipse([x - 2, y - 1, x + 2, y], fill=hx("6E5C4C")); d.point((x - 1, y - 1), fill=hx("B8A284"))
        # En la pared de la izquierda: manos y puntos de ocre (cerca de la luz de la boca).
        mano_negativa(im, 46, 28, ocre, hx("B8A284"), 1.5, 1, -12, 1.0)
        mano_negativa(im, 74, 20, ocre, hx("B8A284"), 1.3, 2, 10, 1.0)
        mano_negativa(im, 392, 28, ocre, hx("A9947A"), 1.35, 3, 6, 1.0)
        for k in range(7):
            d.point((100 + k * 4, 40 + (k % 2)), fill=ocre); d.point((101 + k * 4, 40 + (k % 2)), fill=ocre)
        caballo_rupestre(d, 116, 30, ocre2, carbon, 0.9)
        # El fuego del campamento: piedras en círculo y leña.
        fx, fy = 112, 68
        for k in range(7):
            an = math.pi * k / 6
            d.ellipse([fx - 7 + k * 2.3 - 1, fy - 1, fx - 7 + k * 2.3 + 1, fy + 1], fill=hx("5A4A3C"))
        d.line([fx - 4, fy - 1, fx + 4, fy - 3], fill=hx("3A2A1E")); d.line([fx - 4, fy - 3, fx + 4, fy - 1], fill=hx("3A2A1E"))
        luz(im, fx, fy - 2, 18, hx("F0B060"), 0.25, 3, ry=8)
        # Herramientas: un bifaz y unas lascas de sílex junto al fuego.
        d.polygon([(140, 70), (143, 66), (146, 70), (143, 72)], fill=hx("6E6A70")); d.point((143, 68), fill=hx("A8A4AA"))
        big = subir(im)
        rnd = random.Random(5)
        aves = [(rnd.uniform(170, 350), rnd.uniform(14, 30), rnd.uniform(0, 6.3)) for _ in range(3)]

        def vivo(img, tick, delante):
            if delante: return
            dd = ImageDraw.Draw(img); tk = tick % BUCLE
            llama(dd, fx, fy - 2, tk, 1.0)
            humo(img, tk, fx + 1, fy - 9, hx("8E8478"), hx("A9947A"), n=8, sube=0.25, alto=20, semilla=3)
            for j, (x, y, ph) in enumerate(aves):           # aves que planean sobre el valle
                a_ = fase(tk, BUCLE, 1) + ph
                ex, ey = x + 18 * math.cos(a_), y + 4 * math.sin(a_)
                ala = 1 if (tk // 5 + j) % 2 else 0
                for dx in range(-2, 3):
                    cuad(dd, ex + dx, ey - (abs(dx) * 0.5 if ala else -abs(dx) * 0.3), hx("3A3430"))
        ANIM[("cueva", False)] = [vivo]
        return big
    # De noche: lo hondo de la cueva a la luz de la lámpara.
    lx, ly = 206, 58
    tonos = [hx("140E0A"), hx("21170F"), hx("332418"), hx("4B3A2B"), hx("6A5240"), hx("8A6E54")]
    im, d = lienzo(tonos[0])
    luz_ = lambda x, y: max(0.0, 1 - math.hypot((x - lx) / 190, (y - ly) / 70)) ** 1.2
    roca(im, tonos, 33, luz_=luz_)
    # Las pinturas: más vivas cerca de la luz.
    k_ = lambda x, y: 0.35 + 0.65 * luz_(x, y)
    for (x, y, col, s, sem, an) in ((34, 26, hx("C0603A"), 1.15, 11, -8), (58, 20, hx("C0603A"), 1.0, 12, 6), (48, 46, hx("D9A050"), 1.0, 13, -18),
                                    (360, 22, hx("C0603A"), 1.1, 14, 4), (384, 34, hx("C0603A"), 1.0, 15, -10), (392, 14, hx("D9A050"), 0.9, 16, 12)):
        mano_negativa(im, x, y, mezcla(tonos[2], col, k_(x, y)), mezcla(tonos[2], tonos[4], k_(x, y)), s * 1.25, sem, an, 1.0)
    for (x, y, s, m) in ((128, 30, 1.25, 1), (276, 26, 1.2, -1), (330, 48, 0.95, -1)):
        kk = k_(x, y)
        bisonte(d, x, y, mezcla(tonos[2], hx("C0603A"), kk), mezcla(tonos[1], hx("0C0806"), kk), s, m)
    for (x, y, s, m) in ((182, 22, 1.0, 1), (92, 54, 0.85, 1)):
        kk = k_(x, y)
        caballo_rupestre(d, x, y, mezcla(tonos[2], hx("D9A050"), kk), mezcla(tonos[1], hx("0C0806"), kk), s, m)
    for k in range(9):                                            # hilera de puntos rojos
        kk = k_(220 + k * 5, 14)
        d.rectangle([220 + k * 5, 14 + (k % 2), 221 + k * 5, 15 + (k % 2)], fill=mezcla(tonos[2], hx("C0603A"), kk))
    # Estalactitas y el piso.
    for x in range(4, W, 11):
        largo = 3 + (x * 13) % 6
        kk = luz_(x, 2)
        c = mezcla(tonos[1], tonos[4], kk)
        d.polygon([(x - 2, 0), (x + 2, 0), (x, largo)], fill=c)
    for y in range(64, H):
        for x in range(W):
            v = luz_(x, y) * 0.9 + 0.08 * ((x * 7 + y * 3) % 5) / 5
            v = min(0.999, v) * 3
            i = int(v); f = v - i
            punto(im.load(), x, y, tonos[min(5, i + 1 + (1 if tramar(f, x, y) else 0))])
    d.line([0, 64, W, 64], fill=tonos[1])
    # La lámpara de grasa: una piedra ahuecada sobre una laja, con su mecha.
    d.ellipse([lx - 5, ly + 2, lx + 5, ly + 6], fill=hx("6A5240"))
    d.polygon([(lx - 4, ly + 2), (lx + 4, ly + 2), (lx + 3, ly - 1), (lx - 3, ly - 1)], fill=hx("8A6E54"))
    d.line([lx - 3, ly - 1, lx + 3, ly - 1], fill=hx("2A1C14"))
    d.line([lx + 4, ly + 1, lx + 9, ly + 2], fill=hx("6A5240"))       # el mango
    luz(im, lx, ly - 3, 20, hx("F4C76E"), 0.22, 3, ry=10)
    big = subir(im)
    halos, mascaras_h = [], []
    caja = (round((lx - 14) * ESC), round((ly - 13) * ESC), round((lx + 14) * ESC), round((ly + 5) * ESC))
    for r in (11, 13, 9.5, 12):
        h_ = Image.new("RGB", (W, H)); h_.paste(im)
        luz(h_, lx, ly - 3, r, hx("F4C76E"), 0.2, 3, ry=r * 0.55)
        h_ = subir(h_).crop(caja)
        halos.append(h_); mascaras_h.append(None)

    def lampara(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img); tk = tick % BUCLE
        estado = (tk // 3 + (tk // 11) % 3) % 4                   # el halo cambia de tamaño (titila) en cuatro pasos
        img.paste(halos[estado], (round((lx - 14) * ESC), round((ly - 13) * ESC)), mascaras_h[estado])
        llama(dd, lx, ly - 1, tk, 0.8, cols=(hx("FFF6C8"), hx("FFD27A"), hx("F4A04A"), hx("C0603A")))
    ANIM[("cueva", True)] = [lampara]
    return big


# ---------------------------------------------------------------------
#  Vapor victoriano
# ---------------------------------------------------------------------

def engrane(d, cx, cy, r, dientes, ang, cuerpo, borde, centro, rayos=5, hueco=None):
    """Engrane de pixel art: corona con dientes trapezoidales, rayos y cubo."""
    pts = []
    for k in range(dientes):
        a0 = ang + 2 * math.pi * k / dientes
        paso = 2 * math.pi / dientes
        for f, rr in ((0.0, r - 1.6), (0.18, r + 0.6), (0.5, r + 0.6), (0.68, r - 1.6)):
            pts.append((cx + rr * math.cos(a0 + f * paso), cy + rr * math.sin(a0 + f * paso)))
    d.polygon(pts, fill=cuerpo, outline=borde)
    if hueco is not None:
        d.ellipse([cx - r + 3.4, cy - r + 3.4, cx + r - 3.4, cy + r - 3.4], fill=hueco)
        for k in range(rayos):
            a0 = ang + 2 * math.pi * k / rayos
            d.line([cx, cy, cx + (r - 3) * math.cos(a0), cy + (r - 3) * math.sin(a0)], fill=cuerpo, width=2)
    d.ellipse([cx - 2.2, cy - 2.2, cx + 2.2, cy + 2.2], fill=centro, outline=borde)


def ladrillos(im, x0, y0, x1, y1, pal, semilla=0):
    """Muro de ladrillo en aparejo: hiladas de 2 dp con juntas y algún ladrillo más oscuro o tiznado."""
    px = im.load(); rnd = random.Random(semilla)
    base, oscuro, junta = pal
    for y in range(int(y0), int(y1)):
        hilada = (y - int(y0)) // 2
        for x in range(int(x0), int(x1)):
            if not (0 <= x < W and 0 <= y < H): continue
            if (y - int(y0)) % 2 == 1 and False: continue
            if (y - int(y0)) % 2 == 0 and (x + hilada * 3) % 6 == 0:
                px[x, y] = junta
            elif (y - int(y0)) % 2 == 1 and rnd.random() < 0.0:
                px[x, y] = junta
            else:
                px[x, y] = oscuro if ((x + hilada * 3) // 6 * 7 + hilada * 5) % 9 == 0 else base
        if (y - int(y0)) % 2 == 1:
            for x in range(int(x0), int(x1)):
                if 0 <= x < W and rnd.random() < 0.85: px[x, y] = junta


def farola_gas(d, x, ysuelo, hierro, vidrio, alto=26):
    """Farola de gas: poste de hierro con base, travesaño para la escalera del farolero y el farol de cuatro vidrios."""
    top = ysuelo - alto
    d.rectangle([x - 2, ysuelo - 3, x + 2, ysuelo], fill=hierro)
    d.line([x, ysuelo - 3, x, top + 6], fill=hierro); d.line([x + 1, ysuelo - 3, x + 1, top + 6], fill=hierro)
    d.line([x - 3, top + 9, x + 4, top + 9], fill=hierro)
    d.polygon([(x - 3, top + 6), (x + 4, top + 6), (x + 3, top + 1), (x - 2, top + 1)], fill=vidrio, outline=hierro)
    d.line([x + 0.5, top + 1, x + 0.5, top + 6], fill=hierro)
    d.polygon([(x - 3, top + 1), (x + 0.5, top - 2), (x + 4, top + 1)], fill=hierro)
    return (x + 0.5, top + 3)


def bocanadas(d, tk, x, y, cols, semilla, n=5, vida=40, alto=24, deriva=16):
    """Humo de chimenea en bocanadas: cada una nace chica y oscura en la boca, sube, crece, se corre con el viento y se
    aclara. Se repite cada `vida` cuadros (divide a BUCLE)."""
    for k in range(n):
        f = ((tk + k * vida / n + semilla * 7) % vida) / vida
        r = 1.4 + 3.4 * f
        cx, cy = x + deriva * f ** 1.4 + math.sin(f * 5 + semilla) * 1.2, y - alto * f
        col = cols[min(2, int(f * 3))]
        d.ellipse([round((cx - r) * ESC), round((cy - r * 0.8) * ESC), round((cx + r) * ESC), round((cy + r * 0.8) * ESC)], fill=col)


def fondo_vapor(t):
    """Vapor victoriano. De día, la fábrica de ladrillo bajo el cielo de hollín: chimeneas que humean, ventanales en arco,
    una máquina de balancín con su engrane grande que gira y la calle empedrada. De noche, las farolas de gas encendidas,
    los ventanales con la luz de los hornos y la boca de las chimeneas al rojo."""
    o = t["oscuro"]
    if not o:
        cielo = [hx("B9AB8E"), hx("CDBF9F"), hx("DCCEAF"), hx("E9DFC8"), hx("EFE6D2")]
        lejos, lejos2 = hx("BCAE90"), hx("AE9F80")
        ladr = (hx("9C3D24"), hx("7E2E1A"), hx("C9A98A"))
        piedra, piedra2, junta_p = hx("8E8578"), hx("A39A8C"), hx("5F574C")
        hierro, vidrio_f = hx("2E3A34"), hx("C9D8CE")
        cristal, marco = hx("8FB0A4"), hx("2F6B5E")
        laton, laton2, laton3 = hx("B8862B"), hx("8A5A14"), hx("E0B860")
        hollin = hx("231B14")
    else:
        cielo = [hx("0E0C0A"), hx("14110E"), hx("1D1914"), hx("29231B"), hx("33291C")]
        lejos, lejos2 = hx("1F1A15"), hx("1A1612")
        ladr = (hx("4A2216"), hx("3A1A10"), hx("2A221B"))
        piedra, piedra2, junta_p = hx("2E2924"), hx("3A332B"), hx("1A1612")
        hierro, vidrio_f = hx("0E0D0C"), hx("FFE3A0")
        cristal, marco = hx("DBA641"), hx("1D3630")
        laton, laton2, laton3 = hx("A8802E"), hx("6A4A14"), hx("DBA641")
        hollin = hx("0A0806")
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 46, cielo)
    if not o:
        luz(im, 352, 14, 11, hx("F2E8CC"), 0.6, 3)
        d.ellipse([347, 9, 357, 19], fill=hx("F4ECD6"))
    else:
        luz(im, 60, 12, 14, hx("3A3226"), 0.6, 3)
        d.ellipse([56, 8, 64, 16], fill=hx("D8CCAE")); d.ellipse([58, 9, 61, 12], fill=hx("C2B492"))
    # La ciudad a lo lejos: tejados, chimeneas y un gasómetro.
    rnd = random.Random(8)
    x = 0
    while x < W:
        w_ = rnd.randint(10, 22); h_ = rnd.randint(8, 16)
        d.rectangle([x, 48 - h_, x + w_, 50], fill=lejos)
        if rnd.random() < 0.6:
            cx_ = x + rnd.randint(2, w_ - 2); d.rectangle([cx_, 48 - h_ - rnd.randint(5, 12), cx_ + 1, 48 - h_], fill=lejos2)
        x += w_
    d.ellipse([338, 30, 372, 52], outline=lejos2); d.rectangle([338, 41, 372, 50], fill=lejos)
    for xx in range(338, 373, 6): d.line([xx, 32, xx, 50], fill=lejos2)
    # La fábrica: nave de ladrillo con techo en sierra y ventanales en arco.
    fx0, fx1, ftop = 150, 336, 30
    ladrillos(im, fx0, ftop, fx1, 64, ladr, 3)
    for k, xs in enumerate(range(fx0, fx1, 31)):                              # techo en dientes de sierra
        d.polygon([(xs, ftop), (xs + 22, ftop - 9), (xs + 22, ftop), ], fill=hx("4A4440") if not o else hx("2A2520"))
        d.rectangle([xs + 22, ftop - 9, xs + 24, ftop], fill=hx("9FB8AE") if not o else hx("6A5426"))
        d.line([xs, ftop, xs + 22, ftop - 9], fill=hx("6E665E") if not o else hx("3A332B"))
    d.line([fx0, ftop, fx1, ftop], fill=ladr[1]); d.line([fx0, ftop + 1, fx1, ftop + 1], fill=ladr[2])
    ventanas = []
    for xs in range(fx0 + 10, fx1 - 10, 22):                                  # ventanales en arco con parteluces
        d.rectangle([xs, 40, xs + 11, 56], fill=cristal)
        d.ellipse([xs, 35, xs + 11, 46], fill=cristal)
        d.arc([xs - 1, 34, xs + 12, 47], 180, 360, fill=ladr[2] if not o else hx("5A4632"))
        for xx in (xs + 3, xs + 7): d.line([xx, 37, xx, 56], fill=marco)
        for yy in (44, 50): d.line([xs, yy, xs + 11, yy], fill=marco)
        d.rectangle([xs - 1, 56, xs + 12, 57], fill=hx("C9B79A") if not o else hx("3A332B"))
        ventanas.append(xs)
    d.rectangle([236, 50, 250, 64], fill=hollin); d.arc([236, 44, 250, 57], 180, 360, fill=ladr[2])       # portón
    # El letrero de la fábrica, en una placa de hierro sobre el portón.
    d.rectangle([222, 33, 264, 38], fill=hierro); d.line([223, 35, 263, 35], fill=laton if not o else hx("8A6A2A"))
    # Chimeneas de ladrillo con su corona de hierro.
    chimeneas = [(176, 11), (300, 13), (326, 18)]
    for cx_, top in chimeneas:
        w_ = 7 if cx_ < 320 else 5
        ladrillos(im, cx_ - w_ // 2, top, cx_ + w_ // 2 + 1, ftop - 2, ladr, cx_)
        d.rectangle([cx_ - w_ // 2 - 1, top - 1, cx_ + w_ // 2 + 1, top + 1], fill=hierro)
        for yy in range(top + 6, ftop - 2, 8): d.line([cx_ - w_ // 2, yy, cx_ + w_ // 2, yy], fill=hierro)
        if o: d.line([cx_ - w_ // 2, top - 2, cx_ + w_ // 2, top - 2], fill=hx("E27A55"))
    # La máquina de balancín: bastidor de hierro, el gran volante dentado y un piñón.
    gx, gy, gr = 70, 38, 15
    d.rectangle([44, 22, 50, 64], fill=hierro); d.rectangle([90, 22, 96, 64], fill=hierro)
    d.rectangle([40, 18, 100, 22], fill=hierro); d.line([40, 18, 100, 18], fill=laton2)
    for xx in range(42, 100, 6): d.point((xx, 20), fill=laton3)                   # remaches
    d.line([gx, gy, 100, 20], fill=hierro, width=2)                                  # biela
    d.rectangle([100, 46, 128, 64], fill=hx("3E4A44") if not o else hx("141A18"))   # caldera
    d.ellipse([98, 46, 104, 64], fill=hx("4E5A54") if not o else hx("1A2220"))
    for yy in (50, 56, 62): d.line([102, yy, 128, yy], fill=laton2)
    d.ellipse([112, 49, 119, 56], fill=hx("F1E6CC") if not o else hx("E8D6A0"), outline=laton)   # manómetro
    d.line([115.5, 52.5, 117, 50.5], fill=hollin)
    d.rectangle([122, 38, 125, 46], fill=hierro)                                       # válvula
    # Calle empedrada y la acera de granito.
    for y in range(64, H):
        for x in range(W):
            fila = (y - 64) // 3
            borde = (y - 64) % 3 == 2 or (x + fila * 2) % 5 == 0
            punto(im.load(), x, y, junta_p if borde else (piedra2 if (x // 5 + fila) % 3 == 0 else piedra))
    d.rectangle([0, 63, W, 64], fill=hx("6E665E") if not o else hx("26221D"))
    farolas = [farola_gas(d, x, 66, hierro, vidrio_f) for x in (140, 356)]
    if o:
        for x in ventanas:                                                          # luz de hornos en el empedrado
            luz(im, x + 5.5, 66, 10, hx("DBA641"), 0.18, 3, ry=3)
        for fx, fy in farolas:
            luz(im, fx, fy, 22, hx("F4C76E"), 0.28, 4)
            luz(im, fx, 70, 26, hx("F4C76E"), 0.22, 4, ry=6)
    big = subir(im)
    # Los engranes giran por pasos (8 por diente): se precalcula cada paso sobre el fondo ya pintado.
    caja = (gx - gr - 3, gy - gr - 3, gx + gr + 14, gy + gr + 3)
    pasos = []
    for q in range(8):
        h_ = im.copy(); dd = ImageDraw.Draw(h_)
        a = 2 * math.pi / 12 * q / 8
        engrane(dd, gx + gr + 6.4, gy + 8, 6, 6, -a * 2 + math.pi / 6, laton2, hollin, laton3)
        engrane(dd, gx, gy, gr, 12, a, laton, hollin, laton3, 6, hueco=(hierro if not o else hx("120F0C")))
        pasos.append(subir(h_).crop(tuple(v * ESC for v in caja)))
    humo_c = (hx("7A7068"), hx("948A80"), hx("ADA394")) if not o else (hx("2A241E"), hx("3A332B"), hx("4A4136"))

    def vivo(img, tick, delante):
        if delante: return
        tk = tick % BUCLE
        img.paste(pasos[(tk // 5) % 8], (caja[0] * ESC, caja[1] * ESC))
        dd = ImageDraw.Draw(img)
        for j, (cx_, top) in enumerate(chimeneas):
            bocanadas(dd, tk, cx_, top - 2, humo_c, j, n=6, alto=12, deriva=30)
        if (tk // 40) % 2 == 1:                                    # la válvula suelta vapor a ratos
            f = (tk % 40) / 40
            for k in range(4):
                cuad(dd, 123 + k * 1.2 + f * 6, 36 - k * 1.6 - f * 4, hx("F4F0E6") if not o else hx("8A8274"), 2, 1)
        if o:
            for j, (fx, fy) in enumerate(farolas):                  # la llama del gas tiembla
                c = hx("FFF3C8") if (tk // 4 + j) % 5 else hx("FFD27A")
                cuad(dd, fx - 1, fy - 1, c, 2, 2)
    ANIM[("vapor", o)] = [vivo]
    return big


# ---------------------------------------------------------------------
#  Art déco
# ---------------------------------------------------------------------

def rascacielos(d, x, base, pisos, pal, ventanas_luz=None, rnd=None, corona=None):
    """Torre escalonada: cada tramo (ancho, alto) más angosto que el de abajo; pilastras verticales y ventanas en
    columnas. `ventanas_luz` (de noche) enciende algunas. Devuelve la punta (x, y)."""
    fachada, sombra, ventana, oro = pal
    y = base
    for w_, h_ in pisos:
        d.rectangle([x - w_ / 2, y - h_, x + w_ / 2, y], fill=fachada)
        d.rectangle([x + w_ / 2 - max(1, w_ // 4), y - h_, x + w_ / 2, y], fill=sombra)
        for xx in range(int(x - w_ / 2) + 2, int(x + w_ / 2) - 1, 3):
            for yy in range(int(y - h_) + 2, int(y) - 1, 3):
                c = ventana
                if ventanas_luz is not None and rnd.random() < ventanas_luz:
                    c = oro
                d.point((xx, yy), fill=c)
        d.line([x - w_ / 2, y - h_, x + w_ / 2, y - h_], fill=oro)
        y -= h_
    if corona == "abanico":                 # corona de arcos escalonados con ventanas triangulares (a lo Chrysler)
        w_ = pisos[-1][0]
        for k in range(4):
            r = w_ / 2 - k * 1.4
            d.pieslice([x - r, y - r - k * 2.4, x + r, y + r - k * 2.4], 180, 360, fill=fachada if k % 2 == 0 else sombra)
            d.arc([x - r, y - r - k * 2.4, x + r, y + r - k * 2.4], 180, 360, fill=oro)
        top = y - w_ / 2 - 9.6
        d.line([x, top + 4, x, top - 9], fill=oro)
        return (x, top - 9)
    if corona == "aguja":
        d.polygon([(x - 2, y), (x, y - 10), (x + 2, y)], fill=fachada); d.line([x, y - 10, x, y - 15], fill=oro)
        return (x, y - 15)
    return (x, y)


def fondo_deco(t):
    """Art déco. De día, la ciudad de rascacielos escalonados sobre un sol en abanico, el trasatlántico en el muelle y
    la terraza de mármol con su baranda de oro. De noche, el cielo negro, las ventanas encendidas, las luces del barco
    y tres reflectores que barren el cielo detrás de las torres."""
    o = t["oscuro"]
    if not o:
        cielo, rayo = hx("F3ECDC"), hx("EADBB4")
        pal_t = (hx("E3D8BE"), hx("C9BA98"), hx("8A7E66"), hx("A8801E"))
        pal_t2 = (hx("D6CDB6"), hx("BBAE90"), hx("7E735D"), hx("A8801E"))
        agua, agua2, agua3 = hx("1F6B57"), hx("2E8068"), hx("8CC7AE")
        casco, linea_f, blanco_b, chime = hx("16130E"), hx("B5523B"), hx("FBF7EE"), hx("B5523B")
        marmol, marmol2, oro = hx("F3ECDC"), hx("D9CBA6"), hx("A8801E")
    else:
        cielo, rayo = hx("0D0C0A"), hx("15130F")
        pal_t = (hx("201C15"), hx("16130F"), hx("2A2418"), hx("D9B44A"))
        pal_t2 = (hx("1A1712"), hx("13110D"), hx("241F16"), hx("B8962E"))
        agua, agua2, agua3 = hx("0B1A16"), hx("123028"), hx("5FB59A")
        casco, linea_f, blanco_b, chime = hx("08070A"), hx("7A2E20"), hx("3A3428"), hx("8A3A28")
        marmol, marmol2, oro = hx("2A251C"), hx("0D0C0A"), hx("D9B44A")
    im, d = lienzo(cielo)
    sx, sy = 206, 50
    if not o:                                          # el sol en abanico: rayos alternos desde el horizonte
        for k in range(18):
            a0 = math.pi + math.pi * k / 18; a1 = a0 + math.pi / 36
            if k % 2 == 0:
                d.polygon([(sx, sy), (sx + 400 * math.cos(a0), sy + 400 * math.sin(a0)), (sx + 400 * math.cos(a1), sy + 400 * math.sin(a1))], fill=rayo)
        d.pieslice([sx - 22, sy - 22, sx + 22, sy + 22], 180, 360, fill=hx("F2D27A"))
        for r in (16, 10):
            d.arc([sx - r, sy - r, sx + r, sy + r], 180, 360, fill=hx("D9B44A"))
    else:
        degradado(im, 0, 52, [hx("0B0A08"), hx("0D0C0A"), hx("12100D"), hx("1A1611"), hx("221D16")])
        estrellas(im, 61, 30, 0, 22, [hx("6E6656"), hx("B4A888"), hx("D9B44A")])
    rnd = random.Random(19)
    # Rascacielos de fondo (más bajos y pálidos) y los del frente.
    for x, pisos in ((240, [(14, 18), (10, 6)]), (292, [(18, 14), (12, 8), (8, 6)]), (330, [(12, 22)]), (368, [(16, 16), (10, 10)]),
                     (398, [(14, 26)]), (164, [(14, 20), (10, 6)]), (136, [(12, 14)])):
        rascacielos(d, x, 54, pisos, pal_t2, 0.18 if o else None, rnd)
    puntas = []
    puntas.append(rascacielos(d, 206, 54, [(22, 14), (18, 10), (14, 8), (10, 6)], pal_t, 0.3 if o else None, rnd, "abanico"))
    puntas.append(rascacielos(d, 262, 54, [(18, 18), (14, 10), (10, 8)], pal_t, 0.3 if o else None, rnd, "aguja"))
    puntas.append(rascacielos(d, 348, 54, [(20, 12), (14, 12), (8, 8)], pal_t, 0.3 if o else None, rnd, "aguja"))
    # El mar y su oleaje en escamas.
    for y in range(52, 62):
        for x in range(W):
            punto(im.load(), x, y, agua if (y + (x // 6)) % 4 else agua2)
    for y in (54, 57, 60):
        for x in range((y * 3) % 10, W, 10):
            d.arc([x, y, x + 8, y + 4], 200, 340, fill=agua3 if not o else hx("2E6656"))
    # El trasatlántico: proa afilada, casco negro con la línea de flotación roja, cubiertas blancas y tres chimeneas.
    bx = 22
    d.polygon([(bx, 46), (bx + 130, 46), (bx + 134, 52), (bx + 128, 58), (bx + 10, 58)], fill=casco)
    d.line([bx + 10, 57, bx + 128, 57], fill=linea_f); d.line([bx + 6, 56, bx + 130, 56], fill=linea_f)
    d.line([bx + 2, 47, bx + 132, 47], fill=blanco_b if not o else hx("5A4E36"))
    ojos = []
    for xx in range(bx + 16, bx + 126, 4):
        ojos.append((xx, 50))
        d.point((xx, 50), fill=hx("F2D27A") if o else hx("3A3428"))
    for x0, x1, y0 in ((bx + 30, bx + 120, 41), (bx + 44, bx + 112, 37), (bx + 58, bx + 100, 34)):
        d.rectangle([x0, y0, x1, 46 if y0 == 41 else y0 + 4], fill=blanco_b)
        for xx in range(x0 + 2, x1 - 1, 3):
            ojos.append((xx, y0 + 2)); d.point((xx, y0 + 2), fill=hx("F2D27A") if o else hx("8A7E66"))
    chimeneas = []
    for k, xx in enumerate((bx + 62, bx + 78, bx + 94)):
        d.polygon([(xx - 4, 34), (xx + 3, 34), (xx + 5, 20), (xx - 2, 20)], fill=chime)
        d.polygon([(xx - 2, 20), (xx + 5, 20), (xx + 5.4, 17), (xx - 1.6, 17)], fill=casco)
        chimeneas.append((xx + 1.5, 16))
    d.line([bx + 36, 41, bx + 36, 24], fill=casco); d.line([bx + 112, 41, bx + 112, 26], fill=casco)
    d.line([bx + 36, 24, bx + 112, 26], fill=casco if not o else hx("3A3428"))
    # La baranda de oro del muelle y la terraza de mármol en zigzag (chevrones negros y marfil).
    for x in range(0, W, 6):
        d.line([x, 58, x, 63], fill=oro)
        d.arc([x, 58, x + 6, 64], 180, 360, fill=oro)
    d.line([0, 58, W, 58], fill=oro)
    for y in range(64, H):
        for x in range(W):
            z = (x + abs(((y - 64) % 8) - 4) * 2) // 6 % 2
            punto(im.load(), x, y, marmol if z else marmol2)
    d.line([0, 64, W, 64], fill=oro); d.line([0, 65, W, 65], fill=marmol2 if not o else oro)
    if o:
        for x, y in puntas:
            luz(im, x, y + 4, 9, hx("D9B44A"), 0.25, 3)
        for x in range(30, 160, 12):                          # reflejo de las luces del barco en el agua
            for k in range(3):
                d.point((x + (k % 2), 59 + k), fill=hx("8A6A2A"))
    big = subir(im)
    # Lo que se mueve.
    brillos = [(rnd.randrange(160, W), rnd.randrange(52, 58)) for _ in range(30)]
    humo_c = (hx("7E7666"), hx("A8A090"), hx("CFC7B6")) if not o else (hx("2A2620"), hx("3A3428"), hx("4A4232"))
    capas = {}
    if o:
        cielo_m = Image.new("L", (W, H), 0)
        a_ = np.asarray(im)
        fondo_ = np.asarray(Image.new("RGB", (W, H)))
        es_cielo = np.zeros((H, W), bool)
        cielo_ref = Image.new("RGB", (W, H)); degradado(cielo_ref, 0, 52, [hx("0B0A08"), hx("0D0C0A"), hx("12100D"), hx("1A1611"), hx("221D16")])
        es_cielo = (np.abs(a_.astype(int) - np.asarray(cielo_ref).astype(int)).sum(2) < 40) & (np.arange(H)[:, None] < 52)
        cielo_m = Image.fromarray((es_cielo * 255).astype(np.uint8)).resize((W * ESC, H * ESC), Image.NEAREST)
        focos = [(206 - 28, 52, -100, 0.0), (318, 52, -75, 3.1)]

    def haz(j, q):
        if (j, q) not in capas:
            x, y, base, ph = focos[j]
            ang = math.radians(base + 32 * math.sin(2 * math.pi * q / 16))
            capa = Image.new("RGBA", (W * ESC, H * ESC), (0, 0, 0, 0)); dd = ImageDraw.Draw(capa)
            L = 110
            for k, (anch, a_) in enumerate(((0.05, 28), (0.025, 36))):
                pts = [(x * ESC, y * ESC), ((x + L * math.cos(ang - anch)) * ESC, (y + L * math.sin(ang - anch)) * ESC),
                       ((x + L * math.cos(ang + anch)) * ESC, (y + L * math.sin(ang + anch)) * ESC)]
                dd.polygon(pts, fill=(236, 214, 150, a_))
            al = np.asarray(capa.getchannel("A")).astype(np.uint16) * (np.asarray(cielo_m) > 0)
            capa.putalpha(Image.fromarray(al.astype(np.uint8)))
            capas[(j, q)] = capa
        return capas[(j, q)]

    def vivo(img, tick, delante):
        if delante: return
        tk = tick % BUCLE
        if o:
            base = img.convert("RGBA")
            for j, (_, _, _, ph) in enumerate(focos):
                q = int((tk / BUCLE * 16 + ph * 16 / (2 * math.pi)) % 16)
                base = Image.alpha_composite(base, haz(j, q))
            img.paste(base.convert("RGB"))
        dd = ImageDraw.Draw(img)
        for j, (cx_, top) in enumerate(chimeneas):
            bocanadas(dd, tk, cx_, top, humo_c, j + 2, n=5, alto=12, deriva=-24)
        for j, (x, y) in enumerate(brillos):
            if (j * 7 + tk // 4) % 10 == 0:
                cuad(dd, x, y, agua3 if not o else hx("D9B44A"), 2, 1)
        if o:
            for j, (x, y) in enumerate(ojos[::5]):
                if (j * 3 + tk // 8) % 11 == 0:
                    cuad(dd, x, y, hx("4A3E24"))
    ANIM[("deco", o)] = [vivo]
    return big


FONDOS_MIOS = {"edo": fondo_edo, "cueva": fondo_cueva, "vapor": fondo_vapor, "deco": fondo_deco}
ETIQ = {"edo": "Japón Edo · el puente, el monte y la ola", "cueva": "Prehistoria · la boca de la cueva",
        "vapor": "Vapor victoriano · la fábrica", "deco": "Art déco · rascacielos y trasatlántico"}
NOCHE = {"edo": "farolillos y nieve", "cueva": "manos y bisontes a la luz de la lámpara", "vapor": "las farolas de gas",
         "deco": "los reflectores"}


def registrar():
    for n, f in FONDOS_MIOS.items():
        g8.FONDOS[n] = f
        g9.FONDOS9[n] = f
        if not any(b[0] == n for b in g8.BANDAS):
            g8.BANDAS.append((n, False, ETIQ[n]))
        g9.ETIQ[n] = ETIQ[n]
        g9.NOCHE[n] = NOCHE[n]


def vista_fondos(archivo, temas=MIOS, ticks=(0, 40)):
    """Cada fondo (claro y oscuro) con el gato caminando y sentado, en dos momentos del bucle, a ×3."""
    filas = []
    for n in temas:
        if n not in FONDOS_MIOS: continue
        for o in (False, True):
            t = tema(n, o)
            f = FONDOS_MIOS[n](t)
            for k in ticks:
                img = g8.cuadro_banda(n, o, t, f, ("caminar", 2, 60.0 + k, 1, 0.0), k, None)
                img = g8.cuadro_banda(n, o, t, img, ("sentado", 0, 250.0, -1, 0.0), k, None)
                filas.append(img)
    hoja = Image.new("RGB", (W * ESC, len(filas) * (H * ESC + 6)), (250, 249, 246))
    for k, f in enumerate(filas):
        hoja.paste(f, (0, k * (H * ESC + 6)))
    hoja.save(AQUI / archivo)


# =====================================================================
#  Revisión: hojas del gato (claro y oscuro) con las animaciones comunes y las propias
# =====================================================================
FILAS = [("caminar", 8), ("girar", 2), ("jugar", 6), ("enojado", 3), ("quieto_triste", 4), ("sentado", 2), ("dormir", 3),
         ("saltar", 6), ("asearse", 6)]


def hoja_mia(archivo, cel=3, temas=MIOS):
    lado = N * cel
    filas = [(n, o) for n in temas for o in (False, True)]
    ancho = 130 + 10 * (lado + 4)
    img = Image.new("RGB", (ancho, len(filas) * (3 * lado + 20)), (250, 249, 246)); d = ImageDraw.Draw(img)
    y = 0
    for n, o in filas:
        t = tema(n, o)
        d.rectangle([0, y, ancho, y + 3 * lado + 16], fill=t["fondo"])
        d.text((8, y + 6), f"{n} {'oscuro' if o else 'claro'}", font=fuente("DejaVuSans-Bold.ttf", 14), fill=t["texto"])
        x = 130
        for a, i in (("caminar", 1), ("caminar", 5), ("girar", 0), ("girar", 1), ("jugar", 2), ("enojado", 0), ("quieto_triste", 0), ("sentado", 0), ("dormir", 0), ("asearse", 2)):
            s = g8.sprite(n, o, t, a, i, False)
            img.paste(s, (x, y + 4), s); x += lado + 4
        x = 130
        for j in range(2):
            acc = g17.ESP[n][j][0]
            nk = g17.ACCIONES[acc][1]
            for i in range(nk):
                fila = 1 if j == 0 else 2
                s = g8.sprite(n, o, t, f"esp_{n}_{j}", i, False)
                img.paste(s, (130 + i * (lado + 4), y + fila * lado + 8), s)
        y += 3 * lado + 20
    img.save(AQUI / archivo)


registrar()

if __name__ == "__main__":
    que = sys.argv[1:2] or ["hoja"]
    sel = [a for a in sys.argv[2:] if a in MIOS] or list(MIOS)
    if "hoja" in que:
        hoja_mia("gato20d_hoja.png", temas=sel)
    if "fondos" in que:
        vista_fondos("gato20d_fondos.png", temas=sel)
    if "bandas" in que:
        for n in sel:
            g8.gif_banda(g17.secuencia(n), [(n, False, ETIQ[n])], "_20d")
            g8.gif_banda(g17.secuencia(n), [(n, True, ETIQ[n].split(" · ")[0] + " · de noche: " + NOCHE[n])], "_20d_oscuro")
    print("listo")
