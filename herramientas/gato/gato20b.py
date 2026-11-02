"""
Lote 9b: el gato de tres temas nuevos, Templario, Egipto y Mesoamérica. Sigue la cadena (importa gato18 y lo que haya
cargado antes exportar.py) y solo agrega lo suyo:
- Templario: blanco con capucha y cola grises, en esmalte heráldico (campos de color plano, filetes de oro entre campo
  y campo y un brillo vítreo). Sobreveste blanca con la cruz roja y cota de malla en el cuello, los hombros y la manga.
  De día, la muralla de frontera entre la arena (estandartes al viento); de noche, el campamento al pie de la muralla
  (hogueras, antorchas y estandartes). Alza el estandarte blanco y negro · afila la espada.
- Egipto: mau moteado, en pintura de tumba (color plano sin sombras, contorno rojizo, el estuco que se ve entre los
  granos de pigmento). Collar ancho de cuentas (usekh) y arete de oro. De día, el Nilo con papiros y una barca; de
  noche, la tumba: el techo azul con estrellas y una lámpara de aceite que titila. Toca el sistro · sigue a un ibis.
- Mesoamérica: jaguarundi rojizo, como en un códice (contorno grueso de tinta en cada campo, color plano y las fibras
  del amate). Collar de jade y una pluma de quetzal. De día, la pirámide en la selva con guacamayas y el humo del
  copal; de noche, Venus sobre la pirámide y una fogata. Bebe cacao espumoso · sigue a un colibrí.
Colores de ui/Disenios.kt (templarioClaro/Oscuro, mesoClaro/Oscuro, juegoTonos) y ui/Tema.kt (egiptoClaro/Oscuro).
"""
import math
import random
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato17 as g17      # noqa: E402
import gato18 as g18      # noqa: E402
from gato8 import hx, mezcla, esc, ESC, N   # noqa: E402

AQUI = g18.AQUI
MIOS = ["templario", "egipto", "meso"]

# =====================================================================
#  Capas nuevas del lienzo: la cruz y las bandas del collar (letra I) y la cota de malla (letra F)
# =====================================================================
_nombres = [c[0] for c in g8.CAPAS]
if "cruz20b" not in _nombres:
    i_ = _nombres.index("broche") + 1
    g8.CAPAS[i_:i_] = [("cruz20b", "I", "capa", 0.3), ("malla20b", "F", "malla", 0.4)]
g8.RANGO.setdefault("malla", 4.6)


# =====================================================================
#  Pelaje: capucha gris (templario) y motas de mau (egipto)
# =====================================================================
_acabar = g8.acabar
_rm = random.Random(31)
MOTAS = []                               # (columna, fila, radio²) de las motas del mau, sueltas como en el gato real
for _ in range(400):
    x, y = _rm.uniform(4, 60), _rm.uniform(24, 56)
    if all((x - a) ** 2 + (y - b) ** 2 > 9 for a, b, _ in MOTAS):
        MOTAS.append((x, y, _rm.choice((0.6, 1.1, 1.3))))
CUERPO = ("cuerpo", "cola", "pierna", "colafrente")


def acabar(ch, parte, estilo):
    rayas = estilo[0]
    if rayas not in ("capucha", "moteado"):
        return _acabar(ch, parte, estilo)
    out = _acabar(ch, parte, ("ninguno",) + tuple(estilo[1:]))
    out = [f[:] for f in out]
    if rayas == "capucha":
        # Blanco con gris: la cola entera, y por arriba de la cabeza (orejas y frente) y del lomo una capa gris que sigue
        # al contorno de la pose (se cuenta desde arriba en cada columna).
        for c in range(N):
            hondo = {"cabeza": 0, "cuerpo": 0}
            for r in range(N):
                p_ = parte[r][c]
                if p_ in ("cola", "colafrente") and out[r][c] in "P":
                    out[r][c] = "W"
                elif p_ in hondo and out[r][c] in "PS" and ch[r][c] == "P":
                    hondo[p_] += 1
                    if hondo[p_] <= (4 if p_ == "cabeza" else 3) and out[r][c] == "P":
                        out[r][c] = "W"
    else:
        # Mau: motas redondas sueltas en el cuerpo, barras en las patas, anillos en la cola y rayitas en la frente.
        for r in range(N):
            for c in range(N):
                if out[r][c] != "P":
                    continue
                p_ = parte[r][c]
                if p_ == "cuerpo" and any((r - y) ** 2 + (c - x) ** 2 <= rr for x, y, rr in MOTAS):
                    out[r][c] = "W"
                elif p_ == "pierna" and r % 3 == 0:
                    out[r][c] = "W"
                elif p_ in ("cola", "colafrente") and (r + c) % 5 == 0:
                    out[r][c] = "W"
                elif p_ == "cabeza" and c % 2 == 0 and r < 25 and (r + c) % 4 == 0:
                    out[r][c] = "W"
    return out


g8.acabar = acabar


# =====================================================================
#  Accesorios
# =====================================================================

def cruz_patada(L, capa, x, y, r, a=0.0):
    """Cruz patada (los brazos se abren hacia la punta), como la de la Orden del Temple."""
    for k in range(4):
        an = a + k * math.pi / 2
        u = (math.cos(an), math.sin(an)); v = (-u[1], u[0])
        L.poligono(capa, [(x + v[0] * 0.55, y + v[1] * 0.55), (x + u[0] * r + v[0] * r * 0.62, y + u[1] * r + v[1] * r * 0.62),
                          (x + u[0] * r - v[0] * r * 0.62, y + u[1] * r - v[1] * r * 0.62), (x - v[0] * 0.55, y - v[1] * 0.55)])
    L.elipse(capa, x, y, 0.8, 0.8)


def sobreveste(L, ps):
    """Templario: sobreveste de lino que cubre el lomo y los costados hasta el vientre, con la cruz roja en el costado,
    un cinturón de cuero; encima, el camal de malla en el cuello y los hombros y la manga de malla en la pata cercana."""
    H, C, rh, rc = ps["H"], ps["C"], ps["rh"], ps["rc"]
    ct, ht = (C[0], C[1] - rc), (H[0], H[1] - rh)
    dx, dy = ht[0] - ct[0], ht[1] - ct[1]; n = math.hypot(dx, dy) or 1
    nx, ny = -dy / n, dx / n
    lp = lambda t, up=0.0: (ct[0] + dx * t + nx * up, ct[1] + dy * t + ny * up)
    L.poligono("capa", [lp(-0.06, 1.0), lp(0.5, 1.1), lp(0.9, 1.0), lp(1.0, -rh * 0.7), lp(0.92, -rh * 1.5),
                        lp(0.72, -rh * 1.55), lp(0.62, -rh * 1.35), lp(0.52, -rc * 1.62), lp(0.2, -rc * 1.68),
                        (C[0] + rc * 0.62, C[1] + rc * 0.5), (C[0] + rc * 0.9, C[1] - rc * 0.1), (C[0] + rc * 0.75, C[1] - rc * 0.8)])
    L.capsula("broche", [lp(0.74, 0.9), lp(0.78, -rh * 1.42)], 0.9)                 # cinturón
    cx, cy = lp(0.38, -rc * 0.78)
    if abs(dy) > abs(dx) * 0.5:              # sentado: la cruz va en el costado, entre la cadera y el pecho
        cx, cy = (H[0] + C[0]) / 2 + 1.0, (H[1] + C[1]) / 2 - 1.0
    cruz_patada(L, "cruz20b", cx, cy, 3.4)
    # Camal de malla: cuello y hombros, y la manga en la pata delantera cercana.
    a, b, px, py = g8._cuello(C, ps["cab"])
    m = (a[0] + (b[0] - a[0]) * 0.55, a[1] + (b[1] - a[1]) * 0.55)
    L.capsula("malla20b", [a, m], 8.6)
    L.poligono("malla20b", [(a[0] - 4.6 * px, a[1] - 4.6 * py), lp(0.22, 1.2), lp(0.3, -rc * 0.35), (C[0] + rc * 0.4, C[1] - rc * 0.05),
                            (C[0] + rc * 0.95, C[1] - rc * 0.35), (a[0] + 4.8 * px, a[1] + 4.8 * py)])
    fn = ps["fn"]
    L.capsula("malla20b", [fn[0], (fn[0][0] + (fn[1][0] - fn[0][0]) * 1.05, fn[0][1] + (fn[1][1] - fn[0][1]) * 1.05)], 4.4)


def usekh(L, ps):
    """Egipto: collar ancho de cuentas (usekh) en bandas: oro arriba, fayenza turquesa en medio y cornalina abajo, con una
    fila de gotas de oro en el borde, que se abre sobre el pecho."""
    C, cab = ps["C"], ps["cab"]
    a, b, px, py = g8._cuello(C, cab)
    ux, uy = b[0] - a[0], b[1] - a[1]; n = math.hypot(ux, uy) or 1; ux, uy = ux / n, uy / n
    if px < 0: px, py = -px, -py
    m = (a[0] + (b[0] - a[0]) * 0.32, a[1] + (b[1] - a[1]) * 0.32)
    bandas = [("broche", 2.4, 4.6, 4.8, 1.5), ("capa", 0.6, 5.0, 6.2, 2.4), ("cruz20b", -1.5, 5.4, 7.4, 2.0), ("capa", -3.2, 5.6, 8.2, 1.6)]
    for capa, off, atras, frente, w in bandas:
        o = (m[0] + ux * off, m[1] + uy * off)
        L.capsula(capa, [(o[0] - px * atras, o[1] - py * atras), (o[0] + px * frente * 0.6, o[1] + py * frente * 0.6),
                         (o[0] + px * frente, o[1] + py * frente + 0.6)], w)
    for k in range(6):                      # gotas de oro
        f = 0.2 + k * 0.16
        o = (m[0] - ux * 4.4, m[1] - uy * 4.4)
        x, y = o[0] + px * 8.4 * f - px * 1.5, o[1] + py * 8.4 * f + 0.4
        L.elipse("broche", x, y + 0.6, 0.65, 0.95)


def collar_jade(L, ps):
    """Mesoamérica: sarta de cuentas de jade en un cordón, con una cuenta grande al frente."""
    C, cab = ps["C"], ps["cab"]
    a, b, px, py = g8._cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.5, a[1] + (b[1] - a[1]) * 0.5)
    e1, e2 = (m[0] - 4.6 * px, m[1] - 4.6 * py), (m[0] + 4.6 * px, m[1] + 4.6 * py)
    L.capsula("collar", [e1, e2], 1.0)
    for k in range(5):
        f = (k + 0.5) / 5
        L.elipse("joya", e1[0] + (e2[0] - e1[0]) * f, e1[1] + (e2[1] - e1[1]) * f, 1.05, 1.05)
    j = max((e1, e2), key=lambda q: q[0] + q[1])
    L.capsula("collar", [j, (j[0] - 0.2, j[1] + 1.6)], 0.6)
    L.elipse("joya", j[0] - 0.2, j[1] + 2.8, 1.7, 1.6)


_cabeza = g8.cabeza


def cabeza(L, cx, cy, inc, orejas, ojo, lengua, acc):
    ps = g17._actual.get("ps")
    if ps is not None and abs(ps["cab"][0] - cx) < 1e-6 and abs(ps["cab"][1] - cy) < 1e-6:
        if acc == "sobreveste": sobreveste(L, ps)
        elif acc == "usekh": usekh(L, ps)
        elif acc == "jade": collar_jade(L, ps)
    return _cabeza(L, cx, cy, inc, orejas, ojo, lengua, acc)


g8.cabeza = cabeza

_espalda = g8.acc_espalda


def acc_espalda(L, cu, cx, ny, todo):
    if cu == "sobreveste":
        if todo:
            L.poligono("capa", [(cx - 7.6, ny + 1), (cx + 7.6, ny + 1), (cx + 8.4, ny + 14.5), (cx + 1.0, ny + 15.5), (cx, ny + 13.2),
                                (cx - 1.0, ny + 15.5), (cx - 8.4, ny + 14.5)])
            cruz_patada(L, "cruz20b", cx, ny + 7.4, 3.6)
        else:
            L.poligono("capa", [(cx + 3, ny + 1.5), (cx - 6, ny + 2.5), (cx - 16, ny + 6.5), (cx - 17, ny + 13.4), (cx - 4, ny + 12.4), (cx + 4, ny + 7.4)])
            cruz_patada(L, "cruz20b", cx - 8.6, ny + 7.6, 3.2, 0.25)
        L.capsula("malla20b", [(cx - 5.4, ny), (cx + 5.4, ny)], 4.4)
    elif cu == "usekh":
        L.capsula("capa", [(cx - 5.0, ny), (cx + 5.0, ny)], 2.0)
        L.capsula("broche", [(cx - 4.8, ny - 1.2), (cx + 4.8, ny - 1.2)], 0.8)
        if todo:                            # el contrapeso (menkhet) que cuelga en la espalda
            L.poligono("broche", [(cx - 1.4, ny), (cx + 1.4, ny), (cx + 1.0, ny + 4.6), (cx - 1.0, ny + 4.6)])
            L.elipse("cruz20b", cx, ny + 5.4, 1.0, 1.0)
    elif cu == "jade":
        L.capsula("collar", [(cx - 4.6, ny), (cx + 4.6, ny)], 1.0)
        for k in range(5):
            L.elipse("joya", cx - 4.0 + k * 2.0, ny + 0.2, 1.0, 1.0)
    else:
        _espalda(L, cu, cx, ny, todo)


g8.acc_espalda = acc_espalda

_lado = g8.sombrero_lado
_frente = g8.sombrero_frente


def sombrero_lado(L, cx, cy, inc, som):
    if som not in ("arete", "pluma"):
        return _lado(L, cx, cy, inc, som)
    a = math.radians(inc)
    p = lambda x, y: g8.rot(cx, cy, x, y, a)
    if som == "arete":                      # aro de oro en el borde de la oreja cercana (como en los gatos de bronce)
        x, y = p(4.5, -5.0)
        L.capsula("sombrero3", [(x + 1.1 * math.cos(t), y + 1.1 * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 20)], 0.65)
    else:                                   # pluma de quetzal que sale de atrás de la cabeza, con su cuenta de jade
        pts = [p(-3.8, -4.6), p(-5.8, -9.4), p(-8.8, -13.0), p(-12.4, -14.2)]
        L.curva("sombrero", pts, [1.0, 2.0, 2.3, 1.2])
        L.capsula("sombrero2", [p(-4.0, -5.0), p(-6.0, -9.6), p(-9.0, -12.8)], 0.45)
        L.elipse("sombrero2", *p(-11.6, -13.8), 1.1, 1.0)
        L.elipse("sombrero3", *p(-3.6, -4.4), 1.2, 1.2)
    _lado(L, cx, cy, inc, None)             # el objeto de la animación propia (si lo hay)


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if som not in ("arete", "pluma"):
        return _frente(L, cx, cy, som, desv)
    dx = desv * 1.6
    if som == "arete":
        for s in (-1, 1):
            if s < 0 and desv > 0: continue
            x, y = cx + s * 6.2 + dx * 0.3, cy - 0.6
            L.capsula("sombrero3", [(x + 1.0 * math.cos(t), y + 1.1 * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 16)], 0.55)
    else:
        L.curva("sombrero", [(cx - 2.0 + dx, cy - 5.0), (cx - 3.8 + dx, cy - 10.0), (cx - 6.8 + dx, cy - 13.6), (cx - 10.0 + dx, cy - 14.6)],
                [1.0, 2.0, 2.2, 1.1])
        L.elipse("sombrero2", cx - 9.4 + dx, cy - 14.4, 1.1, 1.0)
        L.elipse("sombrero3", cx - 1.8 + dx, cy - 5.0, 1.2, 1.2)


g8.sombrero_lado, g8.sombrero_frente = sombrero_lado, sombrero_frente
g8._spr.clear(); g8._rej.clear()


# =====================================================================
#  Materiales
# =====================================================================

def _vecinos(g, r, c):
    for dr, dc in ((-1, 0), (1, 0), (0, -1), (0, 1)):
        rr, cc = r + dr, c + dc
        yield dr, dc, (g[rr][cc] if 0 <= rr < N and 0 <= cc < N else ".")


GRUPO = {v: "pelo" for v in "PSLTDHR"}
GRUPO.update(A="tela", B="tela", Y="tela")


def pintar_esmalte(g, t, cel):
    """Templario: esmalte heráldico (champlevé). Cada campo de color es plano y vidriado: más claro en el borde de arriba
    y a la izquierda (donde el vidrio se curva hacia la luz), más hondo abajo; entre campo y campo, un filete de oro, y
    algún poro del esmalte. La malla es hierro: anillos claros y oscuros en tresbolillo."""
    s = cel * g8.SS
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    oro = t["filete"]
    for r, c, v in g8.celdas(g):
        col = t["col"][v]; x0, y0 = c * s, r * s
        if v == "F":
            claro = (r + (c // 2) % 2) % 2 == 0
            d.rectangle([x0, y0, x0 + s, y0 + s], fill=esc(col, 0.62) + (255,))
            d.ellipse([x0 + 0.1 * s, y0 + 0.1 * s, x0 + 0.9 * s, y0 + 0.9 * s], outline=esc(col, 1.35 if claro else 1.05) + (255,),
                      width=max(1, int(0.24 * s)))
            continue
        d.rectangle([x0, y0, x0 + s, y0 + s], fill=col + (255,))
        if v == "K":
            continue
        luz = sombra = False
        for dr, dc, w in _vecinos(g, r, c):
            if GRUPO.get(w, w) != GRUPO.get(v, v) and w not in ".K":
                if dr < 0 or dc < 0: luz = True
                else: sombra = True
        if luz: d.rectangle([x0, y0, x0 + s, y0 + s], fill=mezcla(col, (255, 255, 255), 0.22) + (255,))
        elif sombra: d.rectangle([x0, y0, x0 + s, y0 + s], fill=esc(col, 0.9) + (255,))
        if random.Random(r * 977 + c * 31).random() < 0.05:
            d.ellipse([x0 + 0.35 * s, y0 + 0.35 * s, x0 + 0.6 * s, y0 + 0.6 * s], fill=mezcla(col, (255, 255, 255), 0.5) + (255,))
    w = max(1, round(0.2 * s))
    for r, c, v in g8.celdas(g):            # filetes de oro entre campos de distinto esmalte
        if v in "KF": continue
        for dr, dc, u in _vecinos(g, r, c):
            if u not in (".", "K", "F") and GRUPO.get(u, u) != GRUPO.get(v, v) and (dr, dc) in ((1, 0), (0, 1)):
                x0, y0 = c * s, r * s
                if dr: d.rectangle([x0, y0 + s - w / 2, x0 + s, y0 + s + w / 2], fill=oro + (255,))
                else: d.rectangle([x0 + s - w / 2, y0, x0 + s + w / 2, y0 + s], fill=oro + (255,))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def _ruido(semilla, n=N, escala=6):
    rng = np.random.default_rng(semilla)
    a = rng.normal(0, 1, (n // escala + 3, n // escala + 3))
    im = Image.fromarray(((a * 40 + 128).clip(0, 255)).astype(np.uint8)).resize((n + 3 * escala, n + 3 * escala), Image.BICUBIC)
    return (np.asarray(im, np.float32)[:n, :n] - 128) / 128


_RUIDO = {}


def pintar_tumba(g, t, cel):
    """Egipto: pintura de tumba. Color plano sin sombra (las sombras de contorno pasan a una línea rojiza fina), el
    pigmento mineral algo desigual y, donde se gastó, se ve el estuco; el contorno, en ocre rojo oscuro."""
    s = cel * g8.SS
    if "t" not in _RUIDO: _RUIDO["t"] = _ruido(11)
    ru = _RUIDO["t"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    col = t["col"]; estuco = t["estuco"]
    plano = {"S": mezcla(col["P"], col["K"], 0.55), "B": mezcla(col["A"], col["K"], 0.5), "D": mezcla(col["P"], col["K"], 0.22),
             "T": col["P"]}
    for r, c, v in g8.celdas(g):
        base = plano.get(v, col[v]); x0, y0 = c * s, r * s
        if v != "K":                        # el pigmento, desigual en manchas grandes (tres tonos)
            base = esc(base, 1 + 0.05 * max(-1, min(1, round(ru[r, c] * 2.2))))
        d.rectangle([x0, y0, x0 + s, y0 + s], fill=base + (255,))
        rnd = random.Random(r * 211 + c * 7 + 5)
        if v != "K":
            if rnd.random() < 0.045:        # pigmento gastado: asoma el estuco
                d.ellipse([x0 + 0.15 * s, y0 + 0.2 * s, x0 + 0.85 * s, y0 + 0.8 * s], fill=mezcla(base, estuco, 0.55) + (255,))
    return img.resize((N * cel, N * cel), Image.LANCZOS)


def pintar_codice(g, t, cel):
    """Mesoamérica: como en un códice. Todo campo de color va cerrado por una línea gruesa de tinta (el contorno de
    afuera es aún más grueso), el color es plano y deja ver las fibras del amate que lo cruzan."""
    s = cel * g8.SS
    if "c" not in _RUIDO:
        rnd = random.Random(29)
        fib = Image.new("L", (N * 4, N * 4), 128); df = ImageDraw.Draw(fib)
        for _ in range(170):
            x, y = rnd.uniform(0, N * 4), rnd.uniform(0, N * 4)
            an = rnd.uniform(-0.5, 0.5) + (0 if rnd.random() < 0.6 else math.pi / 2); l = rnd.uniform(10, 40)
            df.line([x, y, x + math.cos(an) * l, y + math.sin(an) * l], fill=rnd.choice((96, 160)), width=1)
        _RUIDO["c"] = fib
    col = t["col"]; tinta = col["K"]
    img = Image.new("RGBA", (N * s, N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    linea = {"S", "B", "K"}
    for r, c, v in g8.celdas(g):
        x0, y0 = c * s, r * s
        base = tinta if v in linea else col[v]
        d.rectangle([x0, y0, x0 + s, y0 + s], fill=base + (255,))
    g2 = 0.28 * s
    for r, c, v in g8.celdas(g):            # contorno de afuera más grueso (se sale un poco de la celda)
        if v != "K": continue
        x0, y0 = c * s, r * s
        for dr, dc, u in _vecinos(g, r, c):
            if u == ".":
                d.rectangle([x0 - (g2 if dc < 0 else 0), y0 - (g2 if dr < 0 else 0), x0 + s + (g2 if dc > 0 else 0), y0 + s + (g2 if dr > 0 else 0)],
                            fill=tinta + (255,))
    w = max(1, round(0.22 * s))             # línea entre campos de distinto color
    for r, c, v in g8.celdas(g):
        if v in linea: continue
        for dr, dc, u in _vecinos(g, r, c):
            if (dr, dc) in ((1, 0), (0, 1)) and u not in (".", v) and u not in linea:
                x0, y0 = c * s, r * s
                if dr: d.rectangle([x0, y0 + s - w / 2, x0 + s, y0 + s + w / 2], fill=tinta + (255,))
                else: d.rectangle([x0 + s - w / 2, y0, x0 + s + w / 2, y0 + s], fill=tinta + (255,))
    out = img.resize((N * cel, N * cel), Image.LANCZOS)
    a = np.asarray(out).astype(np.float32)
    fib = np.asarray(_RUIDO["c"].resize((N * cel, N * cel), Image.BILINEAR), np.float32)
    k = 1 + (fib - 128) / 128 * 0.09
    oscuro_ = a[..., :3].sum(2) < 150
    a[..., :3] = np.where(oscuro_[..., None], a[..., :3], np.clip(a[..., :3] * k[..., None], 0, 255))
    return Image.fromarray(a.astype(np.uint8), "RGBA")


_pintar = g8.pintar


def pintar(g, t, cel, oscuro=False):
    m = t["material"]
    if m == "esmalte": return pintar_esmalte(g, t, cel)
    if m == "tumba": return pintar_tumba(g, t, cel)
    if m == "codice": return pintar_codice(g, t, cel)
    return _pintar(g, t, cel, oscuro)


g8.pintar = pintar


# =====================================================================
#  Objetos de las dos animaciones propias
# =====================================================================

def l_beauseant(L, a):
    """El Beauséant: el estandarte del Temple, negro arriba y blanco abajo, con dos colas, en un asta con punta de hierro."""
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_X", [(x - 0.6, y + 8.6), (x - 0.6, y - 10.4)], 0.8)
    L.poligono("b_Q", [(x - 1.4, y - 10.2), (x - 0.6, y - 12.6), (x + 0.2, y - 10.2)])
    ola = lambda u: 0.9 * math.sin(k * math.pi / 2 + u * 2.2) * u
    xs = [x + 9.8 * j / 6 for j in range(7)]
    arriba = [(xx, y - 10.0 + ola(j / 6)) for j, xx in enumerate(xs)]
    medio = [(xx, y - 6.3 + ola(j / 6)) for j, xx in enumerate(xs)]
    abajo = [(xx, y - 2.6 + ola(j / 6)) for j, xx in enumerate(xs)]
    L.poligono("b_U", arriba + medio[::-1])
    L.poligono("b_V", medio + abajo[::-1])
    fx = xs[-1]; dy_ = ola(1.0)
    L.poligono("b_V", [(fx - 0.2, y - 6.3 + dy_), (fx + 2.6, y - 4.0 + dy_), (fx + 0.2, y - 4.4 + dy_), (fx + 2.4, y - 1.6 + dy_), (fx - 0.2, y - 2.6 + dy_)])
    L.poligono("b_U", [(fx - 0.2, y - 10.0 + dy_), (fx + 2.6, y - 7.8 + dy_), (fx - 0.2, y - 6.3 + dy_)])


def t_afilar(L, a):
    """Afila la espada: la espada en el suelo y la piedra de afilar en la pata, que corre por el filo y saca chispas."""
    x, y = a["obj"]; px, py = a["pata"]; k = a["k"]
    L.capsula("b_X", [(x - 7.6, y + 0.4), (x + 3.0, y + 0.4)], 2.2)                 # hoja
    L.poligono("b_X", [(x - 7.6, y - 0.7), (x - 10.0, y + 0.4), (x - 7.6, y + 1.5)])  # punta
    L.capsula("b_Q", [(x - 7.0, y - 0.2), (x + 2.4, y - 0.2)], 0.45)                # filo que brilla
    L.capsula("b_V", [(x + 3.6, y - 2.4), (x + 3.6, y + 3.2)], 1.2)                 # guarda de oro
    L.capsula("b_U", [(x + 4.0, y + 0.4), (x + 6.8, y + 0.4)], 1.4)                 # empuñadura
    L.elipse("b_V", x + 7.5, y + 0.4, 1.2, 1.2)                                      # pomo
    L.poligono("f_C", [(px - 1.4, py - 0.6), (px + 3.0, py - 1.3), (px + 3.3, py + 0.8), (px - 1.1, py + 1.4)])
    if k in (1, 2):
        for j in range(3):
            an = -2.4 + j * 0.5 + k * 0.2
            L.capsula("f_Q", [(px + 3.2, py), (px + 3.2 + 1.8 * math.cos(an), py + 1.8 * math.sin(an))], 0.35)


def _rotar(cx, cy, a):
    return lambda x, y: g8.rot(cx, cy, x - cx, y - cy, a)


def o_sistro(L, a):
    """Sistro de Hathor: mango de lapislázuli, el arco de oro y tres varillas de bronce con sus platillos, que suenan al
    sacudirlo en alto."""
    x, y = a["obj"]; k = a["k"]
    ang = math.radians(10 * math.sin(k * math.pi / 2))
    r = _rotar(x, y + 6.0, ang)
    L.capsula("b_X", [r(x, y + 6.4), r(x, y + 0.6)], 1.3)
    L.elipse("b_U", *r(x, y + 0.4), 1.7, 0.9)
    arco = [r(x + 2.8 * math.cos(t), y - 0.2 - 7.6 * math.sin(t) ** 0.7) for t in np.linspace(0, math.pi, 16)]
    L.capsula("b_U", arco, 1.05)
    for j, yy in enumerate((-2.0, -4.2, -6.2)):
        L.capsula("b_V", [r(x - 3.6, y + yy + 0.5), r(x - 2.8, y + yy), r(x + 2.8, y + yy), r(x + 3.6, y + yy + 0.5)], 0.5)
        sx = (0.55 if (k + j) % 2 else -0.55)
        L.elipse("b_Q", *r(x - 1.1 + sx, y + yy), 0.5, 0.85); L.elipse("b_Q", *r(x + 1.1 + sx, y + yy), 0.5, 0.85)


def m_ibis(L, a):
    """Ibis sagrado en vuelo: cuerpo blanco, cuello y cabeza negros, pico largo y curvo, puntas de las alas negras."""
    x, y = a["obj"]; k = a["k"]
    alas = -3.6 if k % 2 == 0 else 2.0
    L.elipse("b_U", x, y, 2.6, 1.15)
    L.capsula("b_V", [(x + 2.0, y - 0.3), (x + 4.4, y - 0.8)], 0.95)
    L.elipse("b_V", x + 4.8, y - 0.8, 0.85, 0.75)
    L.capsula("b_X", [(x + 5.4, y - 0.6), (x + 6.8, y + 0.1), (x + 7.6, y + 1.3)], 0.45)
    L.capsula("b_X", [(x - 2.2, y + 0.3), (x - 5.6, y + 0.9)], 0.4)
    L.poligono("b_U", [(x - 1.4, y - 0.4), (x + 1.6, y - 0.4), (x + 0.4, y + alas), (x - 2.6, y + alas * 0.95)])
    L.poligono("b_V", [(x + 0.4, y + alas), (x - 2.6, y + alas * 0.95), (x - 1.4, y + alas * 0.62), (x + 0.9, y + alas * 0.66)])


def o_cacao(L, a):
    """Cacao espumoso en un vaso pintado: estuco blanco con franjas de cinabrio y glifos, la espuma que sube y se
    desborda (se bate vertiéndolo de un vaso a otro)."""
    x, y = a["obj"]; k = a["k"]
    x += 0.6
    rect = lambda capa, x0, y0, x1, y1: L.poligono(capa, [(x0, y0), (x1, y0), (x1, y1), (x0, y1)])
    rect("b_U", x - 2.5, y - 2.6, x + 2.5, y + 3.6)
    rect("b_V", x - 2.5, y - 2.0, x + 2.5, y - 1.0)
    rect("b_V", x - 2.5, y + 2.6, x + 2.5, y + 3.6)
    L.elipse("b_X", x - 1.0, y + 0.9, 0.55, 0.55); L.elipse("b_X", x + 1.1, y + 0.9, 0.55, 0.55)
    L.capsula("b_X", [(x - 0.2, y + 0.0), (x - 0.2, y + 1.8)], 0.35)
    alto = 0.3 * ((k % 4) in (1, 2))
    L.elipse("b_C", x, y - 2.8, 2.5, 0.7)
    for bx, by, rx, ry in ((-1.5, -3.4, 1.2, 0.95), (0.1, -3.9 - alto, 1.4, 1.15), (1.6, -3.4, 1.1, 0.9)):
        L.elipse("b_Q", x + bx, y + by, rx, ry)
    L.elipse("b_Q", x + 2.5, y - 2.4 + 0.5 * (k % 2), 0.5, 0.8)
    if k % 2 == 0:
        L.elipse("b_Q", x - 0.6 + 0.4 * k, y - 5.6 - 0.3 * k, 0.45, 0.45)


def m_colibri(L, a):
    """Colibrí: cuerpo verde, garganta roja, pico largo y las alas que zumban (una arriba, otra adelante)."""
    x, y = a["obj"]; k = a["k"]
    L.elipse("b_U", x, y, 1.8, 1.05, -20)
    L.elipse("b_U", x + 1.6, y - 0.9, 1.0, 0.95)
    L.capsula("b_X", [(x + 2.4, y - 0.9), (x + 5.0, y - 0.4)], 0.4)
    L.elipse("b_Q", x + 1.6, y - 0.1, 0.7, 0.45)
    L.poligono("b_V", [(x - 1.4, y + 0.3), (x - 3.8, y + 1.8), (x - 3.0, y + 2.6), (x - 0.8, y + 1.0)])
    if k % 2 == 0:
        L.poligono("b_V", [(x - 0.4, y - 0.7), (x - 2.6, y - 4.4), (x + 0.6, y - 3.8)])
    else:
        L.poligono("b_V", [(x - 0.2, y - 0.4), (x + 2.8, y - 3.4), (x + 1.0, y - 0.2)])


C_ = lambda *hs: tuple(hx(h) for h in hs)
g17.ESP.update({
    "templario": [("levantar", l_beauseant, "alza el estandarte blanco y negro"), ("trabajar", t_afilar, "afila la espada")],
    "egipto": [("levantar", o_sistro, "toca el sistro"), ("mirar", m_ibis, "sigue a un ibis")],
    "meso": [("sostener", o_cacao, "bebe cacao espumoso"), ("mirar", m_colibri, "sigue a un colibrí")],
})
g17.COL.update({
    "templario": C_("201E1B", "F6F1E4", "6B4A2A", "D6DCE2"),
    "egipto": C_("F2C14E", "7A4A1E", "1D2F7A", "FFE48A"),
    "meso": C_("F4E8CF", "A3301E", "2A1810", "E3C9A0"),
})
g17.EXTRA_COL.update({
    ("templario", 1): dict(X=hx("D6DCE2"), V=hx("C9A040"), U=hx("5A3A22"), Q=hx("FFFFFF"), C=hx("B98A55")),
    ("egipto", 1): dict(U=hx("F4F0E6"), V=hx("231A0E"), X=hx("3A2A1A")),
    ("meso", 0): dict(C=hx("5A2A12")),
    ("meso", 1): dict(U=hx("2F9D67"), V=hx("2C7F9C"), X=hx("2A1810"), Q=hx("E8674D")),
})
_colores = g17.colores


def colores(nombre, oscuro, j, t):
    c = _colores(nombre, oscuro, j, t)
    if nombre == "templario" and oscuro and j == 0:
        c["U"] = hx("4A453E")             # de noche, el negro del estandarte se aclara un poco para que no se pierda
    return c


g17.colores = colores
g18.ESCALA_ESP.update({("templario", 0): 1.45, ("egipto", 0): 1.4, ("egipto", 1): 2.0, ("meso", 0): 1.55, ("meso", 1): 2.1})

for _n in MIOS:
    for _j, (_acc, _f, _) in enumerate(g17.ESP[_n]):
        _fn, _k, _base = g17.ACCIONES[_acc]
        _cs = []
        for _i in range(_k):
            _p, _a = _fn(_i)
            _a["accion"] = _acc
            _a["escala"] = g18.ESCALA_ESP.get((_n, _j), g17.ESCALA[_acc])
            _cs.append(dict(_p, objeto=(_f, _a), semilla=_i))
        g8.ANIMS[f"esp_{_n}_{_j}"] = _cs


# =====================================================================
#  Temas
# =====================================================================
_tema = g8.tema


def pelaje(P, K, **k):
    """Como g8.pelaje, pero conserva las letras nuevas (I: cruz o cornalina, F: malla)."""
    c = g8.pelaje(P, K, **k)
    c.update({x: v for x, v in k.items() if x not in c})
    c.setdefault("I", c["A"]); c.setdefault("F", c["S"])
    return c


def tema(nombre, oscuro=False):
    if nombre not in MIOS:
        return _tema(nombre, oscuro)
    if nombre == "templario":
        # templarioClaro: fondo ECE2CC, tinta 201E1B, cruz B0191F, hierro 4A5560, arena A2763A · oscuro: 12110E, EEE7D8, E2453B
        P = hx("ECEFF0")
        col = pelaje(P, hx("201E1B"), W=hx("858C93"), S=hx("C3C8CC"), L=hx("FFFFFF"), D=hx("AEB3B8"), T=hx("C3C8CC"),
                        E=hx("D9A441"), N=hx("D98E8E"), A=hx("FBF3DF"), B=hx("D9C9A2"), I=hx("B0191F"), F=hx("7A848C"),
                        G=hx("6B4A2A"), C=hx("201E1B"), J=hx("B0191F"))
        if oscuro: col = g9.de_noche(col, hx("3B352A"), 0.1)
        t = g8.T(col, hx("ECE2CC") if not oscuro else hx("12110E"), hx("201E1B") if not oscuro else hx("EEE7D8"),
                 hx("BDA984") if not oscuro else hx("3B352A"), "capucha", "esmalte", ("sobreveste", None), filete=hx("C9A040"))
    elif nombre == "egipto":
        # egiptoClaro: papiro E8D6A0, tinta 231A0E, lapislázuli 1D2F7A, cornalina B8471E, oro 9A7212, turquesa 1F7A6C
        P = hx("D9AE62")
        col = pelaje(P, hx("5A2512"), W=hx("4A2E1A"), L=hx("F4E6BE"), E=hx("9BBF3A"), N=hx("B8471E"),
                        A=hx("1F8C7C"), B=hx("16564C"), I=hx("B8471E"), G=hx("F0C24A"), M=hx("F0C24A"), Z=hx("1D2F7A"), C=hx("1D2F7A"),
                        J=hx("1D2F7A"))
        if oscuro: col = g9.de_noche(col, hx("3B2C0E"), 0.08)
        t = g8.T(col, hx("E8D6A0") if not oscuro else hx("140E0A"), hx("231A0E") if not oscuro else hx("F1E4C0"),
                 hx("C2A867") if not oscuro else hx("3F3121"), "moteado", "tumba", ("usekh", "arete"), estuco=hx("EFE2BC"))
    else:
        # mesoClaro: amate E9D8B6, tinta 2A1810, cinabrio A3301E, azul maya 2C7F9C, jade 2F7D57 · oscuro: 0F1314, 63C5DD, 6FCB8F
        P = hx("B5532E")
        col = pelaje(P, hx("2A1810"), W=hx("8E3A1E"), L=hx("E0A84A"), D=hx("8E3A1E"), T=hx("B5532E"), E=hx("F2C14E"),
                        N=hx("6B1A10"), C=hx("2A1810"), G=hx("3FA36E"), M=hx("1E8C5A"), Z=hx("2C7F9C"), J=hx("2C7F9C"))
        if oscuro: col = g9.de_noche(col, hx("1F2829"), 0.06)
        t = g8.T(col, hx("E9D8B6") if not oscuro else hx("0F1314"), hx("2A1810") if not oscuro else hx("EFE2C6"),
                 hx("B99A6B") if not oscuro else hx("374446"), "ninguno", "codice", ("jade", "pluma"))
    t["oscuro"] = oscuro
    t["ajustar"] = True
    if oscuro:
        t["sombra_col"] = (0, 0, 0); t["sombra_a"] = 70
    c = dict(t["col"]); c.update(g17.colores(nombre, oscuro, 0, t)); t["col"] = c
    return t


for _m in list(sys.modules):
    if _m.startswith("gato") and hasattr(sys.modules[_m], "tema") and _m != __name__:
        sys.modules[_m].tema = tema

# =====================================================================
#  Fondos: herramientas
# =====================================================================
W, H = g9.W, g9.H
lienzo, degradado, ondas, perfil, luz, estrellas, cuad, subir = g9.lienzo, g9.degradado, g9.ondas, g9.perfil, g9.luz, g9.estrellas, g9.cuad, g9.subir
ANIM = g9.ANIM
FUEGO = (hx("FFF1B0"), hx("FFC24A"), hx("F0762B"), hx("B5361C"))


def llama(d, x, y, tick, s=1.0, cols=FUEGO, semilla=0, periodo=12):
    """Como g9.llama, pero con forma que se repite cada 2 × periodo cuadros (el fondo tiene que cerrar el bucle)."""
    rnd = random.Random(((tick // 2) % periodo) * 31 + semilla)
    alto = (5 + rnd.randint(0, 2)) * s
    for j, col in enumerate(reversed(cols)):
        k = 1 - j * 0.24
        a = alto * k; w = 2.4 * s * k
        lean = rnd.uniform(-0.8, 0.8) * s
        pts = [(x - w, y), (x - w * 0.7, y - a * 0.5), (x + lean, y - a), (x + w * 0.7, y - a * 0.45), (x + w, y)]
        d.polygon([(px_ * ESC, py_ * ESC) for px_, py_ in pts], fill=col)


def chispas(d, x, y, tick, n=5, alto=16, cols=(hx("FFE07A"), hx("F0762B")), semilla=0, periodo=48):
    """Pavesas que suben del fuego y se apagan (el bucle cierra cada «periodo» cuadros)."""
    for k in range(n):
        f = ((tick + k * periodo / n + semilla) % periodo) / periodo
        xx = x + math.sin(f * 7 + k * 2.1 + semilla) * 2.2 + f * 3
        cuad(d, xx, y - f * alto, cols[0] if f < 0.55 else cols[1], 0.67, 0.67)


def ondear(d, x, y, tick, largo, alto, franjas, cruz=None, colas=False, periodo=24, fase=0.0, amp=1.0):
    """Estandarte que ondea en su asta (x, y: la esquina de arriba junto al asta); franjas: colores de arriba abajo;
    cruz: (dx, dy, radio, color) de una cruz patada; colas: la punta partida en dos. Se dibuja en columnas de ⅓ dp
    (un pixel), y la onda crece hacia la punta. Se repite cada «periodo» cuadros."""
    pasos = int(largo * ESC)
    n = len(franjas)
    for i in range(pasos):
        u = i / pasos
        off = amp * math.sin(2 * math.pi * (tick / periodo + fase) - u * 3.2) * u
        alto_ = alto * (1 - 0.12 * u)
        yy = y + off + 0.06 * alto * u
        X = round(x * ESC) + i
        hueco = None
        if colas and u > 0.8:                   # la punta partida: un hueco en medio que se abre hacia afuera
            h_ = alto_ * 0.6 * (u - 0.8) / 0.2
            hueco = (round((yy + alto_ / 2 - h_ / 2) * ESC), round((yy + alto_ / 2 + h_ / 2) * ESC))
        for j, c in enumerate(franjas):
            a_, b_ = round((yy + alto_ * j / n) * ESC), round((yy + alto_ * (j + 1) / n) * ESC) - 1
            tramos = [(a_, min(b_, hueco[0] - 1)), (max(a_, hueco[1] + 1), b_)] if hueco else [(a_, b_)]
            for p0, p1 in tramos:
                if p1 >= p0: d.rectangle([X, p0, X, p1], fill=c)
        if cruz is not None:
            cx_, cy_, r_, col_ = cruz
            du = abs(i / ESC - cx_)
            if du <= r_:
                ww = r_ if du <= 0.5 else 0.5 + du * 0.6
                ww = min(ww, r_)
                yc = yy + cy_ * alto_ / alto
                if du <= 0.5:
                    d.rectangle([X, round((yc - r_) * ESC), X, round((yc + r_) * ESC)], fill=col_)
                elif du > r_ * 0.45:
                    d.rectangle([X, round((yc - ww * 0.75) * ESC), X, round((yc + ww * 0.75) * ESC)], fill=col_)
                else:
                    d.rectangle([X, round((yc - 0.5) * ESC), X, round((yc + 0.5) * ESC)], fill=col_)


def patada(d, x, y, r, col):
    """Cruz patada a 1 pixel por dp."""
    for k in range(4):
        an = k * math.pi / 2
        u = (math.cos(an), math.sin(an)); v = (-u[1], u[0])
        d.polygon([(x + v[0] * 0.5, y + v[1] * 0.5), (x + u[0] * r + v[0] * r * 0.6, y + u[1] * r + v[1] * r * 0.6),
                   (x + u[0] * r - v[0] * r * 0.6, y + u[1] * r - v[1] * r * 0.6), (x - v[0] * 0.5, y - v[1] * 0.5)], fill=col)


def sillares(im, x0, y0, x1, y1, pal, semilla=0, alto=4, largo=9):
    """Muro de sillares a soga: hiladas de «alto» px, juntas que corren media piedra en cada hilada y algunas piedras más
    claras u oscuras. pal: (luz, medio, sombra, junta)."""
    d = ImageDraw.Draw(im); rnd = random.Random(semilla)
    d.rectangle([x0, y0, x1, y1], fill=pal[1])
    fila = 0
    for y in range(y0, y1 + 1, alto):
        x = x0 - (largo // 2 if fila % 2 else 0)
        while x <= x1:
            w = largo + rnd.randint(-2, 2)
            r_ = rnd.random()
            if r_ < 0.3 and max(x0, x + 1) <= min(x1, x + w - 1) and y + 1 <= min(y1, y + alto - 1):
                d.rectangle([max(x0, x + 1), y + 1, min(x1, x + w - 1), min(y1, y + alto - 1)], fill=pal[0] if r_ < 0.18 else pal[2])
            if x0 <= x <= x1: d.line([x, y, x, min(y1, y + alto - 1)], fill=pal[3])
            x += w
        d.line([x0, y, x1, y], fill=pal[3])
        fila += 1


# =====================================================================
#  Templario: la muralla de frontera entre la arena. De noche, el campamento al pie de la muralla.
# =====================================================================

def almenas(d, x0, x1, y, col, sombra, paso=7, ancho=4, alto=4):
    for x in range(x0, x1, paso):
        d.rectangle([x, y - alto, min(x1, x + ancho - 1), y], fill=col)
        d.line([min(x1, x + ancho - 1), y - alto, min(x1, x + ancho - 1), y], fill=sombra)


def palmera(d, x, y, h, tronco, hoja, hoja2):
    for k in range(h):
        xx = x + round(math.sin(k / h * 1.4) * 3)
        d.point((xx, y - k), fill=tronco if k % 2 else mezcla(tronco, (0, 0, 0), 0.25)); d.point((xx + 1, y - k), fill=tronco)
    tx, ty = x + round(math.sin(1.4) * 3), y - h
    for an, l in ((-2.8, 9), (-2.2, 10), (-1.4, 7), (-0.6, 10), (0.1, 9), (2.6, 7)):
        pts = [(tx + math.cos(an) * l * f, ty + math.sin(an) * l * f * 0.6 + 4 * f * f) for f in np.linspace(0, 1, 8)]
        d.line(pts, fill=hoja, width=2); d.line(pts[:5], fill=hoja2)


def fondo_templario(t):
    o = t["oscuro"]
    if not o:
        cielo = [hx("8FBAD6"), hx("A9CADC"), hx("C4D8DD"), hx("DCDFD2"), hx("EBE2C8")]
        piedra = (hx("E6D7B2"), hx("CDBB92"), hx("AE9870"), hx("8C7752"))
        piedra2 = (hx("D9C8A0"), hx("BCA67C"), hx("9C865E"), hx("7E6A48"))
        oscura, duna, duna2, madera = hx("3A2E20"), hx("DCC79C"), hx("CFB688"), hx("5A3A22")
        arena = [hx("D8C397"), hx("E2CFA6"), hx("E9DBB8")]
        palma = (hx("8A6A44"), hx("4E6B3A"), hx("6E8A48"))
        lino, rojo, reja = hx("F4EEDD"), hx("B0191F"), hx("5E4A30")
    else:
        cielo = [hx("07070A"), hx("0C0C0E"), hx("12110E"), hx("1B1915"), hx("262219")]
        piedra = (hx("4A4436"), hx("332E25"), hx("27231C"), hx("1C1914"))
        piedra2 = (hx("3E392E"), hx("2B2720"), hx("211E18"), hx("171511"))
        oscura, duna, duna2, madera = hx("0C0B09"), hx("2A251C"), hx("221E17"), hx("1A140E")
        arena = [hx("221E17"), hx("2A2419"), hx("30291D")]
        palma = (hx("1E1912"), hx("14180F"), hx("1C2214"))
        lino, rojo, reja = hx("5A5246"), hx("7E1A14"), hx("2A2218")
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 46, cielo)
    titilan = []
    if o:
        titilan = estrellas(im, 21, 120, 0, 40, [hx("EEE7D8"), hx("B1A891"), hx("8A8272"), hx("DAAE68")])
        lx, ly = 378, 12                                        # luna creciente
        luz(im, lx, ly, 12, hx("3B352A"), 0.35, 3)
        corte = im.getpixel((lx + 3, ly - 2))
        d.ellipse([lx - 5, ly - 5, lx + 5, ly + 5], fill=hx("EFE6CF")); d.ellipse([lx - 3, ly - 6, lx + 7, ly + 4], fill=corte)
    # Dunas lejanas.
    perfil(im, ondas(5, 44, ((2.2, 110), (1.2, 40))), duna, borde=mezcla(duna, hx("FFFFFF"), 0.25) if not o else duna)
    perfil(im, ondas(6, 49, ((1.6, 80), (0.8, 30))), duna2)
    # La muralla: cortina almenada con su talud; dos torres y la puerta con arco apuntado y rastrillo.
    tope = 27
    sillares(im, 0, tope, W - 1, 60, piedra2, semilla=1)
    almenas(d, 0, W, tope - 1, piedra2[1], piedra2[3])
    d.line([0, tope, W, tope], fill=piedra2[0])
    for x in range(W):
        d.line([x, 52, x, 63], fill=piedra2[2] if (x // 9) % 2 else piedra2[1])
    for y in range(52, 64, 3): d.line([0, y, W, y], fill=piedra2[3])
    d.line([0, 52, W, 52], fill=piedra2[0])
    torres = [(34, 30), (340, 30)]
    for x0, w in torres:
        sillares(im, x0, 11, x0 + w, 64, piedra, semilla=x0)
        px = im.load()
        for y in range(11, 65):                                 # el lado de la sombra
            for x in range(x0 + w * 2 // 3, x0 + w + 1):
                px[x, y] = mezcla(px[x, y], piedra[3], 0.4)
        almenas(d, x0, x0 + w + 1, 10, piedra[1], piedra[3], paso=6)
        d.line([x0, 11, x0 + w, 11], fill=piedra[0])
        for x in range(x0 + 1, x0 + w, 4):                      # matacanes
            d.rectangle([x, 12, x + 1, 14], fill=piedra[2])
        for yy in (22, 38):                                     # saeteras
            d.line([x0 + w // 3, yy, x0 + w // 3, yy + 6], fill=oscura)
            d.line([x0 + w // 3 - 1, yy + 3, x0 + w // 3 + 1, yy + 3], fill=oscura)
        d.line([x0 + w // 2, 0, x0 + w // 2, 10], fill=madera)
    gx, gw = 180, 52
    sillares(im, gx, 15, gx + gw, 64, piedra, semilla=7)
    almenas(d, gx, gx + gw + 1, 14, piedra[1], piedra[3], paso=6)
    d.line([gx, 15, gx + gw, 15], fill=piedra[0])
    for x in range(gx + 1, gx + gw, 4): d.rectangle([x, 16, x + 1, 18], fill=piedra[2])
    ax, aw = gx + gw // 2, 11
    arco = [(ax - aw, 64), (ax - aw, 46)] + [(ax - aw + aw * (1 - math.cos(a_)), 46 - 9 * math.sin(a_)) for a_ in np.linspace(0, math.pi / 2, 6)] \
        + [(ax + aw - aw * (1 - math.cos(a_)), 46 - 9 * math.sin(a_)) for a_ in np.linspace(math.pi / 2, 0, 6)] + [(ax + aw, 46), (ax + aw, 64)]
    d.polygon([(x + (2 if x > ax else -2), y - (2 if y < 60 else 0)) for x, y in arco], fill=piedra[0])     # dovelas
    d.polygon(arco, fill=oscura)
    for x in range(ax - aw + 2, ax + aw, 3): d.line([x, 38, x, 64], fill=reja)                             # rastrillo
    for y in range(40, 64, 3): d.line([ax - aw + 1, y, ax + aw - 1, y], fill=reja)
    patada(d, ax, 27, 4, piedra[2] if not o else piedra[0])
    d.line([ax, 0, ax, 14], fill=madera)
    # Paños colgados de la muralla: lino blanco con la cruz roja y dos colas.
    for bx in (112, 282):
        d.rectangle([bx - 5, tope + 1, bx + 5, tope + 19], fill=lino)
        d.polygon([(bx - 5, tope + 19), (bx - 5, tope + 23), (bx, tope + 20), (bx + 5, tope + 23), (bx + 5, tope + 19)], fill=lino)
        d.line([bx + 5, tope + 1, bx + 5, tope + 22], fill=mezcla(lino, piedra[3], 0.35))
        d.line([bx - 6, tope + 1, bx + 6, tope + 1], fill=madera)
        patada(d, bx, tope + 9, 4, rojo)
    palmera(d, 160, 66, 22, *palma); palmera(d, 254, 66, 18, *palma)
    # Arena, con el camino y sus surcos, y matas al pie del muro.
    degradado(im, 62, H, arena)
    rnd = random.Random(12)
    for _ in range(70):
        x, y = rnd.randrange(W), rnd.randrange(64, H)
        d.line([x, y, x + rnd.randint(2, 6), y], fill=mezcla(arena[1], piedra[3], 0.25))
    for _ in range(40):
        d.point((rnd.randrange(W), rnd.randrange(63, H)), fill=piedra[2])
    for x0 in (18, 96, 142, 238, 300, 392):
        for k in range(5):
            d.line([x0 + k, 63, x0 + k + rnd.randint(-1, 1), 63 - rnd.randint(1, 3)], fill=hx("7A7A44") if not o else hx("1A1C12"))
    fuegos, tiendas = [], []
    if o:
        # Campamento: pabellones de lona, dos hogueras y antorchas en las torres.
        lona, lona2, lona3 = hx("CDB48A"), hx("8C7656"), hx("4E4232")
        for tx, tw, th, cruz_ in ((70, 26, 15, True), (210, 20, 12, False), (262, 30, 17, True), (380, 22, 13, False)):
            yb = 70
            d.polygon([(tx - tw // 2, yb), (tx, yb - th), (tx + tw // 2, yb)], fill=lona2)
            d.polygon([(tx - tw // 2, yb), (tx, yb - th), (tx + 1, yb)], fill=lona)
            d.line([tx, yb - th - 4, tx, yb - th], fill=lona3)
            d.polygon([(tx - 2, yb), (tx, yb - 6), (tx + 2, yb)], fill=hx("1A140E"))
            if cruz_: patada(d, tx - tw // 5, yb - th // 3, 2, hx("9E2A20"))
            tiendas.append((tx, yb - th - 4))
        for fx in (128, 322):
            d.line([fx - 4, 71, fx + 4, 69], fill=hx("4A3018")); d.line([fx - 4, 69, fx + 4, 71], fill=hx("4A3018"))
            for k in range(8):
                an = 2 * math.pi * k / 8
                d.point((round(fx + 6 * math.cos(an)), round(71 + 1.5 * math.sin(an))), fill=hx("3A342A"))
            luz(im, fx, 70, 46, hx("F0A040"), 0.36, 4, ry=9)
            luz(im, fx, 62, 16, hx("FFB45A"), 0.28, 3)
            fuegos.append((fx, 70))
        for x0, w in torres:
            luz(im, x0 + w // 2 - 6, 15, 12, hx("FFB45A"), 0.3, 3)
            d.line([x0 + w // 2 - 6, 15, x0 + w // 2 - 6, 19], fill=hx("4A3018"))
        luz(im, ax, 50, 12, hx("FFB45A"), 0.2, 3)
    big = subir(im)
    antorchas = [(x0 + w // 2 - 6, 15) for x0, w in torres] if o else []
    rnd = random.Random(44)
    tit = [p for p in titilan if rnd.random() < 0.25]
    NEGRO_, BLANCO_ = (hx("1E1C19"), hx("F7F2E6")) if not o else (hx("3A3631"), hx("B9B0A0"))
    ROJO_ = hx("B0191F") if not o else hx("8E2018")

    def vivo(img, tick, delante):
        if delante:
            return
        dd = ImageDraw.Draw(img)
        # Estandartes: el Beauséant en las torres y el blanco con la cruz sobre la puerta.
        for j, (x0, w) in enumerate(torres):
            ondear(dd, x0 + w // 2 + 0.34, 0.6, tick, 15, 7, [NEGRO_, BLANCO_], colas=True, fase=j * 0.37)
        ondear(dd, ax + 0.34, 1.6, tick, 12, 8, [BLANCO_], cruz=(4.5, 4, 2.6, ROJO_), fase=0.6)
        for j, (tx, ty) in enumerate(tiendas):                  # gallardetes en los pabellones
            ondear(dd, tx + 0.34, ty, tick, 6, 2.4, [ROJO_ if j % 2 else BLANCO_], fase=j * 0.21, amp=0.6)
        for j, (fx, fy) in enumerate(fuegos):
            llama(dd, fx, fy, tick, 1.25, semilla=j * 5)
            chispas(dd, fx, fy - 6, tick, semilla=j * 13)
        for j, (ax_, ay) in enumerate(antorchas):
            llama(dd, ax_, ay, tick, 0.55, semilla=40 + j)
        for j, (sx, sy) in enumerate(tit):
            if (j * 5 + tick // 4) % 12 == 0:
                cuad(dd, sx, sy, hx("FFFFFF"))
    ANIM[("templario", o)] = [vivo]
    return big


# =====================================================================
#  Egipto: pintado como en una tumba. De día, el Nilo con papiros, una barca, las pirámides y el sol alado; de noche,
#  la cámara de la tumba: el techo azul con estrellas, el friso jeker, los registros de jeroglíficos y la barca
#  pintada en el muro, a la luz de dos lámparas de aceite.
# =====================================================================
ESTRELLA5 = ["..X..", "XXXXX", ".XXX.", ".X.X."]
GLIFOS = [   # jeroglíficos inventados a partir de signos reales, a 1 pixel por dp
    ["XXX.", "X.X.", "XXX.", "X...", "XXXX"],            # búho
    [".X..", "XXX.", ".X..", ".X..", ".X.."],            # anj
    ["X.X.X", ".X.X.", "....."],                         # agua
    [".XXX.", "XX.XX", ".XXX."],                         # ojo / boca
    [".XX.", "XXXX"],                                    # pan
    ["X...", "X...", "X..X", "XXXX"],                    # pie
    [".X.", ".X.", "XXX", ".X.", ".X."],                 # junco
    ["XXXX", "X..X", "XXXX"],                            # casa
    ["..X.", ".XX.", "XXX.", ".X.X"],                    # pájaro
]


def mapa(d, x, y, filas, col):
    for j, f in enumerate(filas):
        for i, ch in enumerate(f):
            if ch == "X": d.point((x + i, y + j), fill=col)


def bloques(d, y, alto, cols, tinta, ancho=6):
    """Cenefa de rectángulos de colores separados por tinta, como en los bordes de los registros."""
    d.rectangle([0, y, W, y + alto], fill=tinta)
    for k, x in enumerate(range(0, W, ancho + 1)):
        d.rectangle([x + 1, y + 1, x + ancho, y + alto - 1], fill=cols[k % len(cols)])


def barca(d, x, y, cols, s=1.0, remo=0.0, vela=True, cabina=False):
    """Barca de papiro con las puntas en umbela, vela y dos remos. d dibuja en pixeles del lienzo de destino (escala s)."""
    casco, atado, tinta, lino, mastil = cols
    P = lambda pts: [(x + px_ * s, y + py_ * s) for px_, py_ in pts]
    d.polygon(P([(-14, -5), (-12, -1), (-6, 1.5), (6, 1.5), (12, -1), (15, -6), (13, -2), (8, -0.6), (-8, -0.6), (-12, -2.4)]), fill=casco, outline=tinta)
    for k in range(-9, 10, 3):
        d.line(P([(k, -0.4), (k, 1.2)]), fill=atado)
    d.polygon(P([(15, -6), (17, -8), (16.5, -5.5)]), fill=hx("5E9A3E"), outline=tinta)
    d.polygon(P([(-14, -5), (-16, -7.5), (-15.5, -4.5)]), fill=hx("5E9A3E"), outline=tinta)
    if cabina:
        d.rectangle(P([(-4, -5), (4, -0.8)]), fill=hx("E0B04A"), outline=tinta)
    for k, dx in enumerate((-5, 3)):
        a = 0.9 + 0.35 * math.sin(remo + k * 0.6)
        d.line(P([(dx, -1), (dx - 7 * math.cos(a), 6 * math.sin(a))]), fill=mastil, width=max(1, round(s * 0.7)))
        bx, by = dx - 7 * math.cos(a), 6 * math.sin(a)
        d.polygon(P([(bx, by - 0.6), (bx - 1.6, by + 0.2), (bx, by + 1.2)]), fill=mastil)
    if vela:
        d.line(P([(1, -1), (1, -17)]), fill=mastil, width=max(1, round(s * 0.8)))
        d.polygon(P([(-5, -16), (7, -16), (7, -6), (-5, -6)]), fill=lino, outline=tinta)
        d.line(P([(-5, -16), (7, -16)]), fill=mastil, width=max(1, round(s)))
        d.line(P([(-5, -6), (7, -6)]), fill=mastil)


def umbela(d, x, y, col, borde, ancho=4, alto=4):
    d.polygon([(x, y), (x - ancho, y - alto), (x + ancho, y - alto)], fill=col, outline=borde)
    d.line([x - ancho + 1, y - alto - 1, x + ancho - 1, y - alto - 1], fill=borde)


def fondo_egipto(t):
    o = t["oscuro"]
    tinta = hx("231A0E")
    azul, lapis, rojo, verde, ocre, oro, blanco = hx("3C6EB4"), hx("1D2F7A"), hx("B8471E"), hx("3E8A4E"), hx("D9A84E"), hx("E0B04A"), hx("F4ECD8")
    if not o:
        estuco = hx("EFE2BC")
        im, d = lienzo(estuco)
        bloques(d, 0, 4, [lapis, rojo, verde, ocre], tinta)
        # El sol alado sobre todo, como en los dinteles.
        sx, sy = 206, 11
        for k, c in enumerate((lapis, verde, rojo)):
            for s_ in (-1, 1):
                x0, x1 = sx + s_ * 4, sx + s_ * (30 - k * 7)
                d.rectangle([min(x0, x1), sy - 3 + k * 2, max(x0, x1), sy - 2 + k * 2], fill=c)
                for xx in range(min(x0, x1), max(x0, x1), 3): d.point((xx, sy - 2 + k * 2), fill=tinta)
        d.ellipse([sx - 4, sy - 5, sx + 4, sy + 3], fill=hx("C8401E"), outline=tinta)
        for s_ in (-1, 1): d.line([sx + s_ * 5, sy, sx + s_ * 5, sy + 3], fill=verde)
        # La otra orilla: el desierto, las pirámides, un obelisco y palmeras dum.
        d.rectangle([0, 30, W, 41], fill=hx("E3C487"))
        d.line([0, 30, W, 30], fill=hx("C49A5A"))
        for px_, w, h in ((292, 36, 21), (336, 26, 15)):
            d.polygon([(px_ - w // 2, 40), (px_, 40 - h), (px_ + w // 2, 40)], fill=hx("C49A5A"), outline=tinta)
            d.polygon([(px_ - w // 2, 40), (px_, 40 - h), (px_ + 2, 40)], fill=hx("ECCB8C"))
            d.line([px_ - w // 2, 40, px_, 40 - h, px_ + w // 2, 40], fill=tinta)
            d.point((px_, 40 - h), fill=oro); d.point((px_, 41 - h), fill=oro)
        d.rectangle([148, 15, 151, 40], fill=hx("D9B478"), outline=tinta)
        d.polygon([(148, 15), (149.5, 11), (151, 15)], fill=oro, outline=tinta)
        for yy in range(18, 38, 3): d.point((149 + (yy % 2), yy), fill=lapis)
        for x0 in (34, 52, 372, 392):
            d.line([x0, 40, x0 + 1, 27], fill=hx("8A6A3A")); d.line([x0 + 1, 27, x0 - 2, 23], fill=hx("8A6A3A"))
            for xc, yc in ((x0 + 1, 27), (x0 - 2, 23)):
                for an in np.linspace(-2.8, -0.3, 6):
                    d.line([xc, yc, xc + 5 * math.cos(an), yc + 4 * math.sin(an)], fill=verde)
                d.line([xc - 5, yc, xc + 5, yc - 1], fill=tinta)
        # El Nilo: azul con las líneas de agua en zigzag, flores de loto y peces.
        d.rectangle([0, 41, W, 63], fill=azul)
        d.line([0, 41, W, 41], fill=tinta)
        for k, y in enumerate((45, 50, 55, 60)):
            for x in range(0, W):
                f = (x + k * 3) % 6
                d.point((x, y + (f if f < 3 else 6 - f) - 1), fill=hx("16306E") if k % 2 else hx("223E86"))
        for lx, ly in ((132, 57), (176, 61), (226, 58), (268, 61)):
            d.ellipse([lx - 4, ly, lx + 4, ly + 1], fill=verde)
            pet = hx("A9C8F0") if lx % 2 else blanco
            d.polygon([(lx - 1, ly), (lx - 3, ly - 5), (lx - 1, ly - 3), (lx, ly - 6), (lx + 1, ly - 3), (lx + 3, ly - 5), (lx + 1, ly)], fill=pet)
            d.point((lx, ly - 1), fill=oro)
        for fx, fy in ((150, 47), (204, 53), (286, 49)):
            d.ellipse([fx - 3, fy - 1, fx + 3, fy + 2], fill=hx("7E9AB0"), outline=tinta)
            d.polygon([(fx - 3, fy), (fx - 6, fy - 2), (fx - 6, fy + 3)], fill=hx("7E9AB0"), outline=tinta)
            d.point((fx + 2, fy), fill=tinta); d.line([fx - 1, fy + 2, fx + 2, fy + 2], fill=rojo)
        # Papiros en las dos orillas (las umbelas se mecen: las pone el fondo vivo) y patos que se alzan.
        rnd = random.Random(9)
        tallos = []
        for x0, x1 in ((0, 112), (306, W)):
            for x in range(x0 + 2, x1, 5):
                top = rnd.randint(30, 44)
                d.line([x, 66, x + rnd.choice((-1, 0, 1)), top], fill=hx("4E7A2E"))
                tallos.append((x, top))
                if rnd.random() < 0.3: d.polygon([(x, 66), (x - 2, 62), (x + 2, 62)], fill=hx("6E8A3A"))
        for bx, by in ((38, 20), (60, 15), (342, 18)):
            d.polygon([(bx - 4, by), (bx, by - 2), (bx + 4, by), (bx, by + 1)], fill=hx("7A5A3A"), outline=tinta)
            d.polygon([(bx - 1, by - 1), (bx - 4, by - 5), (bx + 1, by - 1)], fill=hx("A0603A"), outline=tinta)
            d.point((bx + 4, by - 1), fill=verde)
        # La orilla de tierra negra (Kemet), con matas, y la cenefa de abajo.
        d.rectangle([0, 63, W, 76], fill=hx("6A5236"))
        d.line([0, 63, W, 63], fill=tinta)
        d.line([0, 64, W, 64], fill=hx("5E8A3A"))
        for _ in range(90):
            x, y = rnd.randrange(W), rnd.randrange(66, 76)
            d.line([x, y, x + rnd.randint(1, 4), y], fill=hx("57422A"))
        bloques(d, 76, 4, [lapis, rojo, verde, ocre], tinta)
        big = g8.papel(subir(im), 0.035, semilla=5)
        def umbela_(dd, x, y):
            pts = [(x, y), (x - 3, y - 3.4), (x + 3, y - 3.4)]
            dd.polygon([(a_ * ESC, b_ * ESC) for a_, b_ in pts], fill=hx("5E9A3E"), outline=hx("2E4A1E"))
            cuad(dd, x - 2.4, y - 4, hx("2E4A1E"), 4.8, 0.67)
        umbelas = [(x, y) for x, y in tallos if y <= 35]      # las más altas se mecen con el viento
        db = ImageDraw.Draw(big)
        for x, y in tallos:                                  # las demás, quietas
            if y > 35: umbela_(db, x, y)
        bote = (226, 49)
        cols_barca = (hx("D9B060"), hx("8A6A2A"), tinta, blanco, hx("7A3A1E"))

        def vivo(img, tick, delante):
            if delante:
                return
            dd = ImageDraw.Draw(img)
            # La barca se mece y rema; detrás, las ondas que deja.
            fase = 2 * math.pi * tick / 48
            by = bote[1] + round(math.sin(fase) * 0.67 * ESC) / ESC
            for k in range(3):
                cuad(dd, bote[0] - 20 - k * 5 - (tick // 6) % 8 * 0.67, bote[1] + 2 + k % 2, hx("8AB4E8"), 2, 0.34)
            barca(dd, bote[0] * ESC, by * ESC, cols_barca, s=ESC, remo=2 * math.pi * tick / 24)
            # Un pez que salta cada cuatro segundos.
            f = (tick % 48) / 24
            if f < 1:
                px_, py_ = 170 + f * 12, 52 - math.sin(f * math.pi) * 7
                cuad(dd, px_ - 1.5, py_ - 0.6, hx("7E9AB0"), 3, 1.3)
                cuad(dd, px_ - 2.5, py_ - 1, hx("7E9AB0"), 1, 2)
                cuad(dd, px_ + 1, py_ - 0.3, tinta, 0.34, 0.34)
            elif f < 1.4:
                r_ = (f - 1) * 10
                cuad(dd, 182 - r_, 53, hx("D6E6F6"), 0.67, 0.34); cuad(dd, 182 + r_, 53, hx("D6E6F6"), 0.67, 0.34)
            # Las umbelas de los papiros se mecen con el viento.
            for k, (x, y) in enumerate(umbelas):
                umbela_(dd, x + round(math.sin(fase + k * 0.7) * 0.6 * ESC) / ESC, y)
        ANIM[("egipto", o)] = [vivo]
        return big
    # --- De noche: la cámara de la tumba.
    im, d = lienzo(hx("140E0A"))
    techo = hx("1C2A6E")
    d.rectangle([0, 0, W, 22], fill=techo)
    estrellas_ = []
    for fila, y in enumerate(range(2, 20, 6)):
        for x in range(4 + (fila % 2) * 7, W, 14):
            mapa(d, x - 2, y, ESTRELLA5, hx("E3B341")); estrellas_.append((x, y))
    d.line([0, 22, W, 22], fill=tinta)
    # Friso jeker: haces de juncos atados, en verde, rojo y azul sobre ocre.
    d.rectangle([0, 23, W, 29], fill=ocre)
    for k, x in enumerate(range(1, W, 5)):
        c = (verde, rojo, azul)[k % 3]
        d.rectangle([x, 25, x + 2, 29], fill=c); d.ellipse([x, 23, x + 2, 25], fill=c); d.point((x + 1, 26), fill=ocre)
    d.line([0, 30, W, 30], fill=tinta)
    # El muro: arriba, columnas de jeroglíficos; abajo, la barca del sol sobre el agua; cenefa entre registros.
    muro = hx("E9D6A4")
    d.rectangle([0, 31, W, 73], fill=muro)
    rnd = random.Random(17)
    for x in range(4, W, 8):
        d.line([x - 2, 31, x - 2, 51], fill=hx("C2A867"))
        y = 33
        while y < 49:
            gl = rnd.choice(GLIFOS)
            if y + len(gl) > 50: break
            mapa(d, x, y, gl, rnd.choice((lapis, lapis, verde, rojo, tinta)))
            y += len(gl) + 2
    bloques(d, 51, 3, [lapis, rojo, verde, ocre], tinta, ancho=5)
    d.rectangle([0, 55, W, 73], fill=muro)
    for k, y in enumerate((67, 70)):
        for x in range(0, W):
            f = (x + k * 3) % 6
            d.point((x, y + (f if f < 3 else 6 - f) - 1), fill=azul)
    barca(d, 206, 66, (hx("D9B060"), hx("8A6A2A"), tinta, blanco, hx("7A3A1E")), s=1.0, remo=0.4, vela=False, cabina=True)
    d.ellipse([202, 55, 210, 63], fill=hx("C8401E"), outline=tinta)          # el disco del sol en la barca
    for ox_ in (120, 292):                                                   # ojos udjat
        d.ellipse([ox_ - 5, 58, ox_ + 5, 63], fill=blanco, outline=tinta)
        d.ellipse([ox_ - 2, 58, ox_ + 2, 62], fill=tinta)
        d.line([ox_ - 6, 57, ox_ + 6, 57], fill=tinta)
        d.line([ox_ - 1, 63, ox_ - 3, 68], fill=tinta); d.line([ox_ + 2, 63, ox_ + 6, 67], fill=tinta)
    for cx_ in (40, 372):                                                    # cartuchos
        d.rounded_rectangle([cx_ - 6, 56, cx_ + 6, 70], 4, fill=muro, outline=tinta)
        d.line([cx_ - 5, 71, cx_ + 5, 71], fill=tinta)
        mapa(d, cx_ - 2, 58, GLIFOS[1], lapis); mapa(d, cx_ - 2, 64, GLIFOS[4], rojo)
    d.rectangle([0, 74, W, H], fill=hx("4A3A26"))
    d.line([0, 74, W, 74], fill=tinta)
    for x in range(0, W, 24): d.line([x, 75, x, H], fill=hx("33281A"))
    # Las lámparas: soporte de bronce, cuenco de aceite. La luz se reparte en anillos, y el resto queda en penumbra.
    lamparas = [(70, 52), (342, 52)]
    for lx, ly in lamparas:
        d.rectangle([lx - 1, ly + 3, lx, 72], fill=hx("8A6A2E")); d.line([lx + 1, ly + 3, lx + 1, 72], fill=hx("5A4018"))
        d.polygon([(lx - 4, 73), (lx + 4, 73), (lx + 1, 69), (lx - 2, 69)], fill=hx("8A6A2E"), outline=tinta)
        for yy_ in (56, 62): d.line([lx - 2, yy_, lx + 2, yy_], fill=hx("B98A3A"))
        d.polygon([(lx - 6, ly), (lx + 6, ly), (lx + 3, ly + 3), (lx - 3, ly + 3)], fill=hx("C99A3A"), outline=tinta)
        d.line([lx - 5, ly, lx + 5, ly], fill=hx("F2D27A"))
    a = np.asarray(im).astype(np.float32)
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    L = np.full((H, W), 0.2, np.float32)
    for lx, ly in lamparas:
        dd_ = np.sqrt(((xx - lx) / 120) ** 2 + ((yy - ly + 4) / 70) ** 2)
        L += 0.95 * np.clip(1 - dd_, 0, 1) ** 1.3
    L = np.ceil(np.clip(L, 0, 1) * 6) / 6
    tinte = np.array([1.0, 0.86, 0.66], np.float32)
    a = a * (L[..., None] * tinte + (1 - L[..., None]) * np.array([0.55, 0.6, 0.85], np.float32) * L[..., None])
    oro_ = np.asarray(im).astype(np.float32)
    es_oro = (np.abs(oro_ - np.array(hx("E3B341"))).sum(2) < 4) & (yy < 22)
    a[es_oro] = np.maximum(a[es_oro], oro_[es_oro] * 0.62)          # las estrellas doradas brillan un poco aun en la penumbra
    im = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8))
    big = g8.papel(subir(im), 0.03, semilla=6)
    rnd = random.Random(3)
    brillan = rnd.sample(estrellas_, 14)
    motas = [(rnd.uniform(-20, 20), rnd.uniform(0, 30), rnd.uniform(0, 96)) for _ in range(10)]

    def vivo(img, tick, delante):
        if delante:
            return
        dd = ImageDraw.Draw(img)
        for j, (lx, ly) in enumerate(lamparas):
            llama(dd, lx, ly, tick, 1.0, semilla=j * 3)
            for k, (mx, my, f0) in enumerate(motas):            # polvo que flota en la luz
                f = ((tick + f0) % 96) / 96
                cuad(dd, lx + mx + math.sin(f * 6.3 + k) * 2, ly - 8 - my * 0.6 + f * 6, hx("F2D9A0"), 0.34, 0.34)
        for j, (sx, sy) in enumerate(brillan):                   # la llama hace brillar el oro de alguna estrella
            if (j * 7 + tick // 4) % 24 == 0:
                for ddx, ddy in ((0, -1), (-1, 0), (1, 0), (0, 1)):
                    cuad(dd, sx + ddx, sy + 1 + ddy, hx("FFF0B0"))
    ANIM[("egipto", o)] = [vivo]
    return big


# =====================================================================
#  Mesoamérica: como en un códice sobre amate, todo con su contorno de tinta. De día, la pirámide en la selva bajo la
#  banda celeste, con guacamayas y el humo del copal; de noche, Venus sobre la pirámide y una fogata en la plaza.
# =====================================================================

def guacamaya(d, x, y, k, cols, tinta, s=1.35):
    """Guacamaya roja en vuelo (mira a la izquierda), en dp; k: cuadro del aleteo (0 a 3)."""
    rojo, azul, amarillo = cols
    P = lambda pts: [((x + a_ * s) * ESC, (y + b_ * s) * ESC) for a_, b_ in pts]
    ala = (-4.0, -1.6, 2.4, -1.6)[k % 4]
    d.polygon(P([(4, 0), (13, 1.6), (13.5, 3.0), (4, 1.4)]), fill=rojo, outline=tinta)                 # cola larga
    d.line(P([(10, 1.8), (13.4, 2.6)]), fill=azul, width=ESC)
    d.polygon(P([(-3, -0.6), (0, -1.6), (5, -0.6), (5, 1.6), (0, 1.8), (-3, 1.0)]), fill=rojo, outline=tinta)
    d.polygon(P([(-3, -0.4), (-4.6, 0.3), (-3, 1.0)]), fill=hx("F4E8CF"), outline=tinta)               # pico
    d.polygon(P([(-0.5, 0), (3.5, 0), (2.5 + ala * 0.2, ala - 0.6), (-1.5 + ala * 0.1, ala)]), fill=amarillo, outline=tinta)
    d.polygon(P([(2.5 + ala * 0.2, ala - 0.6), (-1.5 + ala * 0.1, ala), (0.5 + ala * 0.5, ala * 1.6), (3.5 + ala * 0.4, ala * 1.5)]), fill=azul, outline=tinta)
    d.rectangle([round((x - 1.6 * s) * ESC), round((y - 0.6 * s) * ESC), round((x - 1.6 * s) * ESC) + 1, round((y - 0.6 * s) * ESC) + 1], fill=tinta)


def voluta(d, x, y, r, col, tinta, giro=1):
    """Voluta de humo (así se pinta el humo y la palabra en los códices): un rizo con su contorno."""
    pts = [(x + giro * r * (1 - f) * math.cos(f * 4.2), y - r * (1 - f) * math.sin(f * 4.2) - f * r) for f in np.linspace(0, 1, 10)]
    d.line([(a_ * ESC, b_ * ESC) for a_, b_ in pts], fill=tinta, width=ESC * 3 + 2, joint="curve")
    d.line([(a_ * ESC, b_ * ESC) for a_, b_ in pts], fill=col, width=ESC * 3 - 4, joint="curve")


def greca_escalonada(d, y, cols, tinta, fondo):
    """Xicalcoliuhqui: la greca escalonada con su espiral, en una franja de 4 px."""
    d.rectangle([0, y, W, y + 4], fill=fondo)
    d.line([0, y, W, y], fill=tinta); d.line([0, y + 4, W, y + 4], fill=tinta)
    for k, x in enumerate(range(0, W, 8)):
        c = cols[k % len(cols)]
        d.polygon([(x, y + 3), (x + 2, y + 3), (x + 2, y + 2), (x + 4, y + 2), (x + 4, y + 1), (x + 6, y + 1), (x + 6, y + 3), (x + 8, y + 3), (x + 8, y + 4), (x, y + 4)], fill=c)
        d.point((x + 5, y + 2), fill=fondo)


def fondo_meso(t):
    o = t["oscuro"]
    tinta = hx("2A1810")
    cinabrio, azul, jade, ocre, estuco = hx("A3301E"), hx("2C7F9C"), hx("2F7D57"), hx("D9A23A"), hx("F4E8CF")
    if not o:
        amate = hx("E9D8B6")
        im, d = lienzo(amate)
        rnd = random.Random(5)
        for _ in range(260):                                     # las fibras del amate
            x, y = rnd.randrange(W), rnd.randrange(H)
            an = rnd.uniform(-0.4, 0.4) + (0 if rnd.random() < 0.65 else math.pi / 2); l = rnd.uniform(4, 16)
            d.line([x, y, x + math.cos(an) * l, y + math.sin(an) * l], fill=rnd.choice((hx("D6C29A"), hx("F2E6CC"))))
        # Banda celeste: tableros con los signos del cielo (estrella, Venus, sol, luna).
        d.rectangle([0, 0, W, 9], fill=cinabrio); d.line([0, 9, W, 9], fill=tinta); d.line([0, 1, W, 1], fill=tinta)
        for k, x in enumerate(range(2, W, 22)):
            d.rectangle([x, 2, x + 19, 8], fill=azul if k % 2 == 0 else estuco, outline=tinta)
            cx_, cy_ = x + 10, 5
            s_ = k % 4
            if s_ == 0:                                          # estrella: ojo con rayos
                d.ellipse([cx_ - 2, cy_ - 1, cx_ + 2, cy_ + 1], fill=estuco, outline=tinta); d.point((cx_, cy_), fill=tinta)
                for dx in (-5, 5): d.line([cx_ + dx - 1, cy_, cx_ + dx + 1, cy_], fill=estuco)
            elif s_ == 1:                                        # Venus (lamat): cruz con cuatro puntos
                d.line([cx_, cy_ - 2, cx_, cy_ + 2], fill=tinta); d.line([cx_ - 2, cy_, cx_ + 2, cy_], fill=tinta)
                for dx, dy in ((-2, -2), (2, -2), (-2, 2), (2, 2)): d.point((cx_ + dx, cy_ + dy), fill=cinabrio)
            elif s_ == 2:                                        # sol (k'in): flor de cuatro pétalos
                d.ellipse([cx_ - 2, cy_ - 2, cx_ + 2, cy_ + 2], fill=ocre, outline=tinta); d.point((cx_, cy_), fill=cinabrio)
            else:                                                # luna
                d.ellipse([cx_ - 2, cy_ - 2, cx_ + 2, cy_ + 2], fill=estuco, outline=tinta); d.ellipse([cx_ - 1, cy_ - 3, cx_ + 3, cy_ + 1], fill=estuco)
        # La pirámide: seis cuerpos en talud, la escalinata al centro y el templo con su crestería.
        px_, base, cima = 300, 67, 35
        cuerpos = 6
        for i in range(cuerpos):
            y1 = base - i * (base - cima) / cuerpos; y0 = y1 - (base - cima) / cuerpos
            w1 = 62 - i * 7.5; w0 = w1 - 2
            d.polygon([(px_ - w1, y1), (px_ - w0, y0), (px_ + w0, y0), (px_ + w1, y1)], fill=cinabrio, outline=tinta)
            d.line([px_ - w0, y0 + 1, px_ + w0, y0 + 1], fill=hx("C9573A"))
            d.line([px_ - w1 + 1, y1 - 2, px_ + w1 - 1, y1 - 2], fill=hx("8A2818"))
        d.polygon([(px_ - 9, base), (px_ - 5, cima), (px_ + 5, cima), (px_ + 9, base)], fill=estuco, outline=tinta)
        for y in range(cima + 2, base, 2):
            f = (y - cima) / (base - cima)
            d.line([px_ - 5 - 4 * f, y, px_ + 5 + 4 * f, y], fill=hx("C9B48E"))
        d.rectangle([px_ - 13, cima - 11, px_ + 13, cima], fill=estuco, outline=tinta)          # templo
        d.rectangle([px_ - 14, cima - 13, px_ + 14, cima - 10], fill=cinabrio, outline=tinta)
        d.rectangle([px_ - 4, cima - 8, px_ + 4, cima], fill=tinta)
        for dx in (-9, 9): d.rectangle([px_ + dx - 1, cima - 7, px_ + dx + 1, cima - 4], fill=jade, outline=tinta)
        d.polygon([(px_ - 9, cima - 13), (px_ - 6, cima - 22), (px_ + 6, cima - 22), (px_ + 9, cima - 13)], fill=cinabrio, outline=tinta)   # crestería
        for dx in (-4, 0, 4): d.rectangle([px_ + dx - 1, cima - 19, px_ + dx, cima - 16], fill=amate)
        bx_ = px_ + 19                                            # el incensario en la plataforma del templo
        d.polygon([(bx_ - 3, cima - 4), (bx_ + 3, cima - 4), (bx_ + 2, cima), (bx_ - 2, cima)], fill=ocre, outline=tinta)
        d.line([bx_ - 2, cima - 2, bx_ + 2, cima - 2], fill=cinabrio)
        humo_en = (bx_ - 14, cima - 2)
        # La selva: ceibas y palmas de copa redonda, cada hoja con su contorno, a los dos lados.
        def arbol(x, y, r, hojas=jade):
            d.line([x, y, x, y - r], fill=tinta, width=3); d.line([x, y, x, y - r], fill=hx("8A7A5A"))
            for dx, dy, rr in ((-r * 0.6, -r * 1.1, r * 0.55), (r * 0.6, -r * 1.1, r * 0.55), (0, -r * 1.5, r * 0.6), (-r * 0.2, -r * 0.9, r * 0.5)):
                d.ellipse([x + dx - rr, y + dy - rr, x + dx + rr, y + dy + rr], fill=hojas, outline=tinta)
                d.arc([x + dx - rr * 0.6, y + dy - rr * 0.6, x + dx + rr * 0.6, y + dy + rr * 0.6], 200, 320, fill=hx("6FB07E"))
        for x, y, r in ((22, 66, 14), (64, 68, 18), (110, 66, 11), (150, 68, 15), (196, 66, 10), (236, 68, 8), (366, 68, 9), (396, 66, 14)):
            arbol(x, y, r)
        for x0 in range(0, W, 7):                                 # monte bajo
            d.ellipse([x0 - 4, 62, x0 + 4, 70], fill=hx("4E8E5E"), outline=tinta)
        # La plaza de estuco, maíz y nopales, y la greca de abajo.
        d.rectangle([0, 68, W, 76], fill=hx("E3CFA2")); d.line([0, 68, W, 68], fill=tinta)
        for x0 in (40, 130, 214, 380):
            d.line([x0, 68, x0, 61], fill=hx("4E8E5E")); d.line([x0, 64, x0 - 3, 61], fill=hx("4E8E5E")); d.line([x0, 63, x0 + 3, 60], fill=hx("4E8E5E"))
            d.ellipse([x0 - 1, 59, x0 + 1, 63], fill=ocre, outline=tinta)
        greca_escalonada(d, 76, [cinabrio, tinta], tinta, estuco)
        big = subir(im)          # sin el ruido de papel: las fibras ya dan la textura y el bucle de las aves pesa menos
        aves = [(0, 15), (97, 23)]

        def vivo(img, tick, delante):
            if delante:
                return
            dd = ImageDraw.Draw(img)
            for j, (x0, y0) in enumerate(aves):                  # guacamayas que cruzan el cielo hacia la izquierda
                f = ((tick + x0) % 192) / 192
                x = round(W + 16 - f * (W + 40))                 # de dp en dp, como el pixel art del fondo
                guacamaya(dd, x, round(y0 + math.sin(f * 12.6 + j) * 1.5), (tick // 2 + j) % 4, (hx("C8321E"), azul, hx("E8B830")), tinta)
            for k in range(3):                                   # el copal sube en volutas desde el templo
                f = ((tick + k * 16) % 48) / 48
                voluta(dd, humo_en[0] + 14 + f * 8 + k * 3, humo_en[1] - 2 - f * 12, 2.0 + f * 1.4, mezcla(hx("B9C4C0"), hx("E9D8B6"), f * 0.7), tinta, 1 if k % 2 else -1)
        ANIM[("meso", o)] = [vivo]
        return big
    # --- De noche: obsidiana, Venus, la pirámide en sombra con el templo encendido y una fogata en la plaza.
    cielo = [hx("060909"), hx("0A0D0E"), hx("0F1314"), hx("161C1D"), hx("1F2829")]
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 60, cielo)
    px = im.load()
    for y in range(0, 50):                                      # la Vía Láctea, el árbol del mundo, cruza el cielo
        for x in range(W):
            dd_ = abs((x - 200) * 0.22 + y - 25) / 7
            if dd_ < 1 and g9.tramar((1 - dd_) * 0.5, x, y):
                px[x, y] = mezcla(px[x, y], hx("4A6A70"), 0.3)
    titilan = estrellas(im, 13, 160, 0, 50, [hx("EFE2C6"), hx("B0A58D"), hx("63C5DD"), hx("6E7C7E")])
    vx, vy = 96, 14                                             # Venus, la gran estrella
    luz(im, vx, vy, 16, hx("63C5DD"), 0.3, 4)
    d.ellipse([vx - 2, vy - 2, vx + 2, vy + 2], fill=hx("EAFBFF"))
    px_, base, cima = 300, 67, 35
    for i in range(6):
        y1 = base - i * (base - cima) / 6; y0 = y1 - (base - cima) / 6
        w1 = 62 - i * 7.5; w0 = w1 - 2
        d.polygon([(px_ - w1, y1), (px_ - w0, y0), (px_ + w0, y0), (px_ + w1, y1)], fill=hx("1F2829"))
        d.line([px_ - w0, y0, px_ + w0, y0], fill=hx("374446"))
    d.polygon([(px_ - 9, base), (px_ - 5, cima), (px_ + 5, cima), (px_ + 9, base)], fill=hx("2B3638"))
    for y in range(cima + 2, base, 2): d.line([px_ - 5 - 4 * (y - cima) / 40, y, px_ + 5 + 4 * (y - cima) / 40, y], fill=hx("374446"))
    d.rectangle([px_ - 13, cima - 11, px_ + 13, cima], fill=hx("2B3638"))
    d.rectangle([px_ - 14, cima - 13, px_ + 14, cima - 10], fill=hx("374446"))
    d.polygon([(px_ - 9, cima - 13), (px_ - 6, cima - 22), (px_ + 6, cima - 22), (px_ + 9, cima - 13)], fill=hx("2B3638"))
    luz(im, px_, cima - 4, 18, hx("E8674D"), 0.3, 3)
    d.rectangle([px_ - 4, cima - 8, px_ + 4, cima], fill=hx("E8874D"))
    d.rectangle([px_ - 3, cima - 6, px_ + 3, cima], fill=hx("FFC24A"))
    humo_en = (px_, cima - 14)
    for x, y, r in ((22, 66, 14), (64, 68, 18), (110, 66, 11), (150, 68, 15), (196, 66, 10), (236, 68, 8), (366, 68, 9), (396, 66, 14)):
        d.line([x, y, x, y - r], fill=hx("0B1210"), width=2)
        for dx, dy, rr in ((-r * 0.6, -r * 1.1, r * 0.55), (r * 0.6, -r * 1.1, r * 0.55), (0, -r * 1.5, r * 0.6), (-r * 0.2, -r * 0.9, r * 0.5)):
            d.ellipse([x + dx - rr, y + dy - rr, x + dx + rr, y + dy + rr], fill=hx("0D1715"))
            d.arc([x + dx - rr, y + dy - rr, x + dx + rr, y + dy + rr], 200, 300, fill=hx("1B2C2A"))
    for x0 in range(0, W, 7): d.ellipse([x0 - 4, 62, x0 + 4, 70], fill=hx("101C1A"))
    d.rectangle([0, 68, W, H], fill=hx("1A1F1E")); d.line([0, 68, W, 68], fill=hx("2A3332"))
    fx, fy = 170, 72                                            # la fogata, con su cerco de piedras
    for k in range(10):
        an = 2 * math.pi * k / 10
        d.point((round(fx + 7 * math.cos(an)), round(fy + 1.6 * math.sin(an))), fill=hx("3A4446"))
    d.line([fx - 5, fy + 1, fx + 5, fy - 1], fill=hx("4A2A18")); d.line([fx - 5, fy - 1, fx + 5, fy + 1], fill=hx("4A2A18"))
    luz(im, fx, fy, 52, hx("E8874D"), 0.34, 4, ry=9)
    luz(im, fx, fy - 8, 18, hx("FFB45A"), 0.24, 3)
    greca_escalonada(d, 76, [hx("63C5DD"), hx("0F1314")], hx("0A0D0E"), hx("1F2829"))
    big = subir(im)
    rnd = random.Random(19)
    tit = [p for p in titilan if rnd.random() < 0.3]
    luciernagas = [(rnd.uniform(10, 240), rnd.uniform(46, 64), rnd.randrange(96)) for _ in range(9)]

    def vivo(img, tick, delante):
        if delante:
            return
        dd = ImageDraw.Draw(img)
        b = (tick // 3) % 8                                     # Venus titila con sus cuatro rayos
        largo = (2, 3, 4, 3, 2, 3, 5, 3)[b]
        cuad(dd, vx - largo, vy, hx("CFF3FF"), 2 * largo + 1, 0.34); cuad(dd, vx, vy - largo, hx("CFF3FF"), 0.34, 2 * largo + 1)
        for k in range(4):                                      # el copal sube del templo, gris azulado
            f = ((tick + k * 12) % 48) / 48
            for j in range(3):
                g_ = min(1, f + j * 0.08)
                cuad(dd, humo_en[0] + math.sin(g_ * 5 + k) * 2.4 + g_ * 6, humo_en[1] - g_ * 22, mezcla(hx("6E8C92"), cielo[2], g_ * 0.8), 1.34, 1)
        llama(dd, fx, fy, tick, 1.35, semilla=7)
        chispas(dd, fx, fy - 7, tick, n=6, alto=18)
        for j, (lx, ly, f0) in enumerate(luciernagas):          # luciérnagas en el monte
            f = ((tick + f0) % 96) / 96
            if f < 0.4:
                cuad(dd, lx + math.sin(f * 8 + j) * 3, ly - f * 4, hx("C8F06A"), 0.67, 0.67)
        for j, (sx, sy) in enumerate(tit):
            if (j * 5 + tick // 4) % 12 == 0: cuad(dd, sx, sy, hx("FFFFFF"))
    ANIM[("meso", o)] = [vivo]
    return big


# =====================================================================
#  Registro en la cadena
# =====================================================================
ETIQUETAS = {"templario": "Templario · al pie de la muralla de frontera", "egipto": "Egipto · a orillas del Nilo",
             "meso": "Mesoamérica · la pirámide en la selva, como en un códice"}
NOCHE = {"templario": "las hogueras del campamento", "egipto": "en la tumba, bajo el techo de estrellas", "meso": "Venus y la fogata"}
for _n in MIOS:
    if _n not in [b[0] for b in g8.BANDAS]:
        g8.BANDAS.append((_n, False, ETIQUETAS[_n]))
    if _n not in g9.NUEVOS:
        g9.NUEVOS.append(_n)
g9.ETIQ.update(ETIQUETAS)
g8.FONDOS.update(templario=fondo_templario, egipto=fondo_egipto, meso=fondo_meso)
g9.NOCHE.update(NOCHE)


def especiales(archivo, cel=3):
    """Hoja con las dos animaciones propias de los tres temas, de día y de noche, cada fila sobre el fondo del tema."""
    lado = N * cel
    filas = [(n, o) for n in MIOS for o in (False, True)]
    ancho = 200 + 10 * (lado + 4) + 30
    img = Image.new("RGB", (ancho, 34 + len(filas) * (lado + 26)), (250, 249, 246)); d = ImageDraw.Draw(img)
    d.text((10, 8), "Animaciones propias (×3)", font=g8.fuente("DejaVuSans-Bold.ttf", 16), fill=(25, 25, 28))
    for r, (n, o) in enumerate(filas):
        t = tema(n, o); y = 34 + r * (lado + 26)
        d.rectangle([0, y - 2, ancho, y + lado + 20], fill=t["fondo"])
        d.text((10, y + lado // 2 - 18), ETIQUETAS[n].split(" · ")[0] + (" (noche)" if o else ""), font=g8.fuente("DejaVuSans-Bold.ttf", 14), fill=t["texto"])
        x = 200
        for j in range(2):
            d.text((x, y + lado + 4), g17.ESP[n][j][2], font=g8.fuente("DejaVuSans.ttf", 11), fill=t["texto"])
            for k in range(g17.ACCIONES[g17.ESP[n][j][0]][1]):
                sp = g8.sprite(n, o, t, f"esp_{n}_{j}", k, False)
                img.paste(sp, (x, y), sp)
                x += lado + 4
            x += 26
    img.save(AQUI / f"{archivo}.png")


if __name__ == "__main__":
    que = sys.argv[1:] or ["hojas", "especiales"]
    if "fondos" in que:
        for n in [a for a in sys.argv[2:] if a in MIOS] or MIOS:
            for o in (False, True):
                t = tema(n, o); f = g8.FONDOS[n](t)
                cs = g17.secuencia(n)
                for tick in (0, 40, 90):
                    g8.cuadro_banda(n, o, t, f, cs[tick], tick, None).save(AQUI / f"fondo_{n}_{'o' if o else 'c'}_{tick}.png")
    if "especiales" in que:
        especiales("especiales_20b")
    if "hojas" in que:
        for n in MIOS:
            for o in (False, True):
                g8.hoja(n, o, f"hoja_{n}_{'o' if o else 'c'}.png", cel=4, material=True)
