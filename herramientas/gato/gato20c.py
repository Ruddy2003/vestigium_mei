"""
Lote 9b, tanda c: el gato de tres temas nuevos.
- Ciudad verde (ruinas): gato tricolor pintado a la acuarela, con granulado de musgo en las sombras; collar de cuerda con una
  hoja de hiedra. Fondo: una calle que el bosque recuperó (autos con hiedra, semáforo apagado); de noche, luciérnagas.
- Horror cósmico (arcano): gato negro de ojos que brillan, en aguatinta verde-negra (grano de resina); amuleto con un signo
  inventado. Fondo: la costa con el faro en la niebla, impreso en aguatinta; de noche, la luna, el haz del faro y, a ratos,
  un tentáculo.
- Abismo (abismo): gato gris azulado con casco de buzo de latón (ojos de buey, pernos, peto); de noche es bioluminiscente y
  sus pixeles se suman como luz. Fondo: la zona iluminada con peces y algas; de noche, la zona abisal con medusas que laten.
Cada tema trae sus 2 acciones. Todo lo demás viene de la cadena (gato8 … gato18).
"""
import math
import random
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter

sys.path.insert(0, str(Path(__file__).parent))
import gato18 as g18      # noqa: E402,F401  (carga toda la cadena)
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato17 as g17      # noqa: E402
from gato8 import hx, mezcla, fuente, ESC                     # noqa: E402
from gato9 import W, H, lienzo, degradado, luz, cuad, subir    # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
N, SUELO = g8.N, g8.SUELO
MIOS = ["ruinas", "arcano", "abismo"]

# =====================================================================
#  Registro en la cadena
# =====================================================================
g8.BANDAS.extend([("ruinas", False, "Ciudad verde · la calle que el bosque recuperó"),
                  ("arcano", False, "Horror cósmico · el faro en la niebla"),
                  ("abismo", False, "Abismo · la zona iluminada")])
for _n in MIOS:
    if _n not in g9.NUEVOS:
        g9.NUEVOS.append(_n)
g9.ETIQ.update(ruinas="Ciudad verde · la calle que el bosque recuperó", arcano="Horror cósmico · el faro en la niebla",
               abismo="Abismo · la zona iluminada")
g9.NOCHE.update(ruinas="luciérnagas entre los autos", arcano="la luna, el haz del faro y lo que sale del agua",
                abismo="la zona abisal: medusas que laten")
_ULTIMO = {"n": None}     # el último tema pedido (para saber qué banda se está componiendo)

# =====================================================================
#  Accesorios
# =====================================================================
g8.CON_COLLAR = tuple(g8.CON_COLLAR) + ("cuerda", "amuleto", "buzo")
_collar = g8.collar


def collar(L, C, cab, acc):
    if acc not in ("cuerda", "amuleto", "buzo"):
        return _collar(L, C, cab, acc)
    a, b, px, py = g8._cuello(C, cab)
    if acc == "buzo":
        # Peto (corselete) de latón: una placa ancha al pie del cuello, con pernos; el casco se apoya encima.
        m = (a[0] + (b[0] - a[0]) * 0.05, a[1] + (b[1] - a[1]) * 0.05)
        e1, e2 = (m[0] - 6.0 * px, m[1] - 6.0 * py), (m[0] + 6.0 * px, m[1] + 6.0 * py)
        L.capsula("capa", [e1, e2], 4.6)
        for f in (0.12, 0.5, 0.88):
            L.elipse("broche", e1[0] + (e2[0] - e1[0]) * f + 0.6 * (b[0] - a[0]) / 10, e1[1] + (e2[1] - e1[1]) * f + 1.0, 0.65, 0.65)
        return
    m = (a[0] + (b[0] - a[0]) * 0.55, a[1] + (b[1] - a[1]) * 0.55)
    e1, e2 = (m[0] - 4.4 * px, m[1] - 4.4 * py), (m[0] + 4.4 * px, m[1] + 4.4 * py)
    j = max((e1, e2), key=lambda q: q[0] + q[1])
    if acc == "cuerda":
        # Cuerda torcida (se trenza al acabar) con una hoja de hiedra de cinco puntas colgando.
        L.capsula("collar", [e1, e2], 1.7)
        x, y = j[0] - 0.4 * px, j[1] + 0.9
        L.poligono("joya", [(x, y), (x - 1.5, y + 1.1), (x - 1.9, y + 2.4), (x - 0.8, y + 2.6), (x, y + 4.2), (x + 0.8, y + 2.6),
                            (x + 1.9, y + 2.4), (x + 1.5, y + 1.1)])
    else:
        # Cordón de cuero y un disco de bronce verdoso con un signo que no es de ningún alfabeto.
        L.capsula("collar", [e1, e2], 1.1)
        x, y = j[0] - 0.4 * px, j[1] + 2.7
        L.elipse("joya", x, y, 2.5, 2.5)
        L.capsula("linea", [(x - 0.2, y - 1.6), (x + 0.1, y + 1.5)], 0.55)
        L.capsula("linea", [(x - 1.5, y - 0.4), (x - 0.4, y + 0.3), (x + 0.6, y - 0.6), (x + 1.5, y + 0.2)], 0.5)


g8.collar = collar
_acc_espalda = g8.acc_espalda


def acc_espalda(L, cu, cx, ny, todo):
    if cu != "buzo":
        return _acc_espalda(L, cu, cx, ny, todo)
    L.capsula("capa", [(cx - 5.8, ny + 1.2), (cx + 5.8, ny + 1.2)], 4.6)
    for dx in ((-4.2, 0, 4.2) if todo else (-3.6, 2.4)):
        L.elipse("broche", cx + dx, ny + 2.2, 0.65, 0.65)


g8.acc_espalda = acc_espalda

# ---------------------------------------------------------------------
#  Casco de buzo de latón (no es la burbuja de vidrio del Espacio): cúpula opaca que tapa las orejas, ojo de buey al frente
#  por el que se ve la cara, otro arriba, pernos en la base y la válvula del aire atrás.
# ---------------------------------------------------------------------
_lado, _frente = g8.sombrero_lado, g8.sombrero_frente


def _borrar(L, capa, cx, cy, r):
    ImageDraw.Draw(L.img[capa]).ellipse([(cx - r) * g8.R, (cy - r) * g8.R, (cx + r) * g8.R, (cy + r) * g8.R], fill=0)


def _aro(L, capa, cx, cy, r, w):
    L.capsula(capa, [(cx + r * math.cos(u), cy + r * math.sin(u)) for u in np.linspace(0, 2 * math.pi, 28)], w)


def sombrero_lado(L, cx, cy, inc, som):
    if som == "buzo":
        a = math.radians(inc)
        p = lambda x, y: g8.rot(cx, cy, x, y, a)
        L.elipse("sombrero", *p(0.3, -1.7), 10.2, 10.0, inc)
        L.capsula("sombrero2", [p(-9.0, -4.6), p(-11.4, -6.8)], 2.4)                 # válvula del aire
        L.capsula("sombrero3", [p(-10.6, -6.0), p(-12.2, -7.8)], 1.2)
        fx, fy = p(4.1, 0.4)                                                            # ojo de buey del frente
        _borrar(L, "sombrero", fx, fy, 4.3)
        _aro(L, "sombrero3", fx, fy, 4.6, 1.15)
        L.capsula("sombreroh", [p(5.3, -2.7), p(6.6, -1.6)], 0.55)                      # reflejo en el vidrio
        sx, sy = p(-2.6, -6.6)                                                          # ojo de buey de arriba
        L.elipse("vidrio", sx, sy, 1.5, 1.5)
        _aro(L, "sombrero3", sx, sy, 2.0, 0.9)
        for u in (100, 125, 150):                                                       # pernos de la base
            q = p(0.3 + 9.0 * math.cos(math.radians(u)), -1.7 + 8.8 * math.sin(math.radians(u)))
            L.elipse("sombrero3", *q, 0.6, 0.6)
        L.elipse("sombreroh", *p(-5.4, -4.0), 1.2, 0.6, inc - 40)                       # brillo del latón
        som = None
    _lado(L, cx, cy, inc, som)


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if som != "buzo":
        return _frente(L, cx, cy, som, desv)
    dx = desv * 1.4
    L.elipse("sombrero", cx + dx * 0.3, cy - 1.8, 10.4, 10.0)
    for s in ((1,) if desv > 0 else (-1, 1)):                                           # ojos de buey de los lados
        ox = cx + dx * 0.3 + s * (7.6 if desv == 0 else 7.8)
        L.elipse("vidrio", ox, cy - 0.6, 1.5, 1.8)
        _aro(L, "sombrero3", ox, cy - 0.6, 2.0, 0.9)
    L.elipse("sombrero2", cx - dx * 0.8, cy - 3.6, 2.4, 2.4)                            # válvula del aire, atrás
    L.elipse("sombrero3", cx - dx * 0.8, cy - 3.6, 1.0, 1.0)
    for u in (35, 70, 110, 145):
        L.elipse("sombrero3", cx + dx * 0.3 + 9.2 * math.cos(math.radians(u)), cy - 1.8 + 8.8 * math.sin(math.radians(u)), 0.6, 0.6)
    L.elipse("sombreroh", cx + dx * 0.3 - 4.8, cy - 6.4, 1.4, 0.7, -25)


g8.sombrero_lado, g8.sombrero_frente = sombrero_lado, sombrero_frente

# =====================================================================
#  Pelaje tricolor (Ciudad verde): manchas naranja y negras que siguen al cuerpo (se ponen según la pose, no la rejilla)
# =====================================================================
_rejilla = g8.rejilla
_rej_mia = {}
NAR, NAR_S, NEG, NEG_S = "1", "2", "3", "4"      # letras nuevas: naranja, su sombra; negro, su sombra


def _manchas_pose(ps):
    H_, C_, cab, inc = ps["H"], ps["C"], ps["cab"], ps["inc"]
    ang = math.atan2(C_[1] - H_[1], C_[0] - H_[0])
    a = math.radians(inc)
    pc = lambda x, y: g8.rot(cab[0], cab[1], x, y, a)
    mx, my = (H_[0] + C_[0]) / 2, (H_[1] + C_[1]) / 2
    negras = [(H_[0] + 1.0, H_[1] - 3.2, 5.6, 4.4, ang), (*pc(1.6, -5.6), 2.8, 2.4, a)]
    naranjas = [(mx + 5.0, my - 4.2, 4.6, 3.2, ang), (*pc(-3.4, -3.2), 3.4, 3.0, a), (C_[0] - 1.0, C_[1] + 4.0, 2.4, 2.0, 0)]
    cola = ps["cola"]
    return negras, naranjas, [(cola[i], NAR) for i in (2, 3)] + [(cola[i], NEG) for i in (4, 5)]


def _manchas_giro(i):
    if i == 1:
        cola = [(32, 50), (32.4, 44), (33.2, 38), (35.4, 32.5), (38.2, 30.5), (40, 32)]
        return ([(28.5, 46.5, 5.0, 5.0, 0), (35.6, 23.6, 2.6, 2.4, 0)], [(35.4, 38.2, 4.0, 3.0, 0), (29.6, 25.6, 3.4, 2.8, 0)],
                [(cola[i_], NAR) for i_ in (1, 2)] + [(cola[i_], NEG) for i_ in (3, 4, 5)])
    cola = [(23, 45), (19, 40.5), (17.5, 34), (18.5, 28), (21.5, 25.5), (23.6, 27.2)]
    return ([(25.5, 44.5, 5.2, 4.4, 0), (44.0, 24.0, 2.6, 2.4, 0)], [(36.4, 38.8, 4.0, 3.0, 0), (39.6, 25.2, 3.4, 2.8, 0)],
            [(cola[i_], NAR) for i_ in (2, 3)] + [(cola[i_], NEG) for i_ in (4, 5)])


def _en(e, x, y):
    cx, cy, rx, ry, a = e          # el ángulo, en radianes
    dx, dy = x - cx, y - cy
    u = dx * math.cos(a) + dy * math.sin(a); v = -dx * math.sin(a) + dy * math.cos(a)
    return (u / rx) ** 2 + (v / ry) ** 2 <= 1


def rejilla(anim, i, t, espejo=False):
    tri, cuerda = t.get("rayas") == "tricolor", t.get("acc", ("", None))[0] == "cuerda"
    if not (tri or cuerda):
        return _rejilla(anim, i, t, espejo)
    k = (anim, i, t["rayas"], t["acc"], espejo)
    if k not in _rej_mia:
        g = [f[:] for f in _rejilla(anim, i, t, False)]
        if tri:
            negras, naranjas, cola = _manchas_giro(i) if anim == "girar" else _manchas_pose(g8.ANIMS[anim][i])
            for r in range(N):
                for c in range(N):
                    v = g[r][c]
                    if v not in "PSW":
                        continue
                    x, y = c + 0.5, r + 0.5
                    m = None
                    for (qx, qy), l in cola:
                        if (x - qx) ** 2 + (y - qy) ** 2 < 2.6 ** 2:
                            m = l
                    if m is None and any(_en(e, x, y) for e in negras): m = NEG
                    if m is None and any(_en(e, x, y) for e in naranjas): m = NAR
                    if m:
                        g[r][c] = m if v != "S" else (NAR_S if m == NAR else NEG_S)
        if cuerda:
            for r in range(N):
                for c in range(N):
                    if g[r][c] == "C" and (r + c) % 2 == 0:
                        g[r][c] = "Z"
        _rej_mia[k] = [f[::-1] for f in g] if espejo else g
    return _rej_mia[k]


g8.rejilla = rejilla

# =====================================================================
#  Materiales nuevos
# =====================================================================
_RNG = np.random.default_rng(20)
_GRANO = _RNG.random((N * 8, N * 8))              # grano fijo (no cambia entre cuadros)
_RUIDO = np.asarray(Image.fromarray((_RNG.random((9, 9)) * 255).astype(np.uint8)).resize((N, N), Image.BICUBIC), np.float32) / 255 - 0.5


def _arreglos(g, t):
    col = t["col"]
    ids = np.full((N, N), -1, np.int32); rgb = np.zeros((N, N, 3), np.float32)
    for r, c, v in g8.celdas(g):
        ids[r, c] = ord(v); rgb[r, c] = col[v]
    return ids, rgb


def _posterizar(a, paso):
    return np.round(a / paso) * paso


def pintar_acuarela(g, t, cel):
    """Ciudad verde: acuarela. Las aguadas se funden entre sí, el pigmento se junta en las orillas de cada mancha, el papel
    granula y en las sombras se asienta un verde musgo. La línea es de tinta."""
    s = cel * 4
    ids, rgb = _arreglos(g, t)
    mask = ids >= 0
    k_ = ids == ord("K")
    rgb = rgb * (1 + 0.09 * _RUIDO)[..., None]                       # la aguada no es pareja
    big = np.kron(rgb, np.ones((s, s, 1), np.float32)); mb = np.kron(mask, np.ones((s, s), bool)).astype(np.float32)
    ib = np.kron(ids, np.ones((s, s), np.int32))
    # Mojado sobre mojado: se mezcla con lo vecino (solo dentro del gato).
    num = np.asarray(Image.fromarray(np.clip(big * mb[..., None], 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.5 * s)), np.float32)
    den = np.asarray(Image.fromarray((mb * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.5 * s)), np.float32)[..., None] / 255
    suave = num / np.maximum(den, 1e-3)
    out = big * 0.45 + suave * 0.55
    # Pigmento acumulado en la orilla de cada mancha.
    e = max(1, int(0.22 * s))
    orilla = np.zeros(ib.shape, bool)
    for dy, dx in ((e, 0), (-e, 0), (0, e), (0, -e)):
        orilla |= np.roll(ib, (dy, dx), (0, 1)) != ib
    out[orilla & mb.astype(bool)] *= 0.86
    kb = np.kron(k_, np.ones((s, s), bool))
    out[kb] = np.array(t["col"]["K"], np.float32)
    img = Image.fromarray(np.dstack([np.clip(out, 0, 255), mb * 255]).astype(np.uint8), "RGBA").resize((N * cel, N * cel), Image.LANCZOS)
    # Musgo que se asienta en las sombras, pixel a pixel. Los tonos se redondean (pocos colores: el atlas pesa menos).
    a = np.asarray(img).astype(np.float32)
    gr = _GRANO[:N * cel, :N * cel]
    a[..., :3] = _posterizar(a[..., :3], 9)
    sombra = np.kron(np.isin(ids, [ord(c) for c in "SDT24"]), np.ones((cel, cel), bool))
    musgo = sombra & (gr > 0.8)
    a[musgo, :3] = a[musgo, :3] * 0.45 + np.array(t["musgo"], np.float32) * 0.55
    a[..., 3] = np.where(a[..., 3] > 127, 255, 0)
    return Image.fromarray(np.clip(a, 0, 255).astype(np.uint8), "RGBA")


def pintar_aguatinta(g, t, cel):
    """Horror cósmico: aguatinta. El tono es un grano de resina mordido en la plancha (tinta verde-negra, un medio tono y el
    papel); la línea es tinta pura, y solo el color de los ojos y del amuleto va «a la muñeca». Los ojos brillan."""
    ids, rgb = _arreglos(g, t)
    mask = ids >= 0
    tin, med, pap = (np.array(t[k], np.float32) for k in ("ag_tinta", "ag_medio", "ag_papel"))
    lum = rgb @ np.array([0.3, 0.59, 0.11], np.float32)
    f = np.clip((235 - lum) / 205, 0, 1)
    mx, mn = rgb.max(2), rgb.min(2)
    sat = (mx - mn) / np.maximum(mx, 1)
    acento = mask & (sat > 0.32) & (mx > 70) & (ids != ord("K"))
    acento |= np.isin(ids, [ord(c) for c in t.get("ag_acento", "EG")])
    K = lambda a_: np.kron(a_, np.ones((cel, cel) + (1,) * (a_.ndim - 2), a_.dtype))
    fb, ab, kb, mb = K(f), K(acento), K(ids == ord("K")), K(mask)
    rgbb = K(rgb)
    gr = _GRANO[:N * cel, :N * cel]
    q = fb + (gr - 0.5) * 0.6
    out = np.where((q > 0.66)[..., None], tin, np.where((q > 0.3)[..., None], med, pap))
    out = np.where(ab[..., None], np.where((gr < 0.18)[..., None], rgbb * 0.7 + tin * 0.3, rgbb), out)
    out[kb] = tin
    alfa = mb.astype(np.float32) * 255
    # Ojos que brillan: un halo que sale un poco del contorno.
    ojo = Image.fromarray((K(ids == ord("E")) * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(cel * 1.3))
    h = np.asarray(ojo, np.float32) / 255 * t.get("ag_halo", 0.8)
    luzc = np.array(t["col"]["E"], np.float32)
    out = out * (1 - h[..., None]) + luzc * h[..., None]
    alfa = np.maximum(alfa, np.clip(h * 2.2, 0, 1) * 255 * (t.get("ag_halo", 0.8) > 0.9))
    return Image.fromarray(np.dstack([np.clip(out, 0, 255), alfa]).astype(np.uint8), "RGBA")


def pintar_bio(g, t, cel):
    """Abismo de noche: bioluminiscencia. El cuerpo apenas se ve; brillan el contorno, una hilera de fotóforos en el costado,
    los ojos y los ojos de buey del casco. Devuelve RGB sobre negro: se suma a la banda como luz."""
    s = cel * g8.SS
    ids, rgb = _arreglos(g, t)
    fot = np.zeros((N, N), bool)
    for r in range(N):
        for c in range(N):
            if ids[r, c] == ord("P") and (c * 3 + r * 5) % 13 == 0 and r % 2 == 0:
                fot[r, c] = True
                rgb[r, c] = t["fotoforos"][(r + c) % 2]
    capa = Image.new("RGB", (N * s, N * s), (0, 0, 0)); d = ImageDraw.Draw(capa)
    for r in range(N):
        for c in range(N):
            if ids[r, c] < 0:
                continue
            col = tuple(int(v) for v in rgb[r, c])
            if fot[r, c]:
                d.rectangle([c * s, r * s, (c + 1) * s, (r + 1) * s], fill=t["col"]["P"])
                d.ellipse([c * s + 0.15 * s, r * s + 0.15 * s, (c + 1) * s - 0.15 * s, (r + 1) * s - 0.15 * s], fill=col)
            else:
                d.rectangle([c * s, r * s, (c + 1) * s, (r + 1) * s], fill=col)
    a = np.asarray(capa, np.float32)
    claro = a * (a.max(2, keepdims=True) > 70)
    brillo = np.asarray(Image.fromarray(claro.astype(np.uint8)).filter(ImageFilter.GaussianBlur(s * 0.9)), np.float32)
    img = Image.fromarray(np.clip(a + brillo * 1.2, 0, 255).astype(np.uint8)).resize((N * cel, N * cel), Image.LANCZOS)
    b = np.asarray(img, np.float32)
    b = np.where(b.max(2, keepdims=True) < 10, 0, _posterizar(b, 14))         # el halo en escalones, como pixel art
    return Image.fromarray(np.clip(b, 0, 255).astype(np.uint8))


_pintar = g8.pintar


def pintar(g, t, cel, oscuro=False):
    m = t.get("material")
    if m == "acuarela": return pintar_acuarela(g, t, cel)
    if m == "aguatinta": return pintar_aguatinta(g, t, cel)
    if m == "bio": return pintar_bio(g, t, cel)
    return _pintar(g, t, cel, oscuro)


g8.pintar = pintar

# =====================================================================
#  Temas: colores de ui/Disenios.kt (ruinasClaro/Oscuro, arcanoClaro/Oscuro, abismoClaro/Oscuro)
# =====================================================================
_tema_prev = g8.tema


def tema_mio(nombre, o):
    if nombre == "ruinas":
        # ruinasClaro: hormigón E4E3DC, tinta 1F2420, hiedra 3E6B2A, óxido A34A1E, ámbar B57A0E
        # ruinasOscuro: 0F1411, tinta E3E6DC, hiedra 8FCB6A, óxido E08A55, ámbar E9C25A
        P = hx("F1EBDD")
        col = g8.pelaje(P, hx("262D27"), S=hx("C9C3B2"), L=hx("FCFAF4"), W=P, D=hx("A9A595"), T=hx("E4DDCB"),
                        E=hx("9DAE3C"), N=hx("D99A92"), C=hx("B59A68"), Z=hx("7E6844"), G=hx("3E6B2A"), A=hx("3E6B2A"))
        col.update({NAR: hx("DE8B48"), NAR_S: hx("A85A2C"), NEG: hx("343A35"), NEG_S: hx("222823")})
        if o:
            col = g9.de_noche(col, hx("22302A"), 0.14)
        t = g8.T(col, hx("0F1411") if o else hx("E4E3DC"), hx("E3E6DC") if o else hx("1F2420"), hx("3A443B") if o else hx("A9AA9C"),
                 "tricolor", "acuarela", ("cuerda", None), musgo=hx("5E8A3A") if not o else hx("4E7A34"))
        t.update(sombra_col=(31, 36, 32) if not o else (0, 0, 0), sombra_a=55 if not o else 75)
    elif nombre == "arcano":
        # arcanoClaro: legajo E3D9C1, tinta 18231D, verde 2D5446, sepia 6A4B26, lacre 7A2C24
        # arcanoOscuro: 0A1210, niebla CDD6C7, faro E6D58C, agua que brilla 7DBBA4
        col = g8.pelaje(hx("2C332E"), hx("101813"), S=hx("1B221D"), L=hx("4A544C"), W=hx("2C332E"), D=hx("1A211C"), T=hx("252C27"),
                        E=hx("D9E67A") if not o else hx("E6EE8C"), N=hx("4A3A38"), R=hx("2C332E"), C=hx("6A4B26"),
                        G=hx("6FA88E") if not o else hx("7DBBA4"))
        if not o:
            t = g8.T(col, hx("E3D9C1"), hx("18231D"), hx("B3A683"), "ninguno", "aguatinta", ("amuleto", None),
                     ag_tinta=hx("17211B"), ag_medio=hx("6D7466"), ag_papel=hx("E6DCC4"), ag_halo=0.75, ag_acento="EGQ")
            t.update(sombra_col=(24, 35, 29), sombra_a=60)
        else:
            t = g8.T(col, hx("0A1210"), hx("CDD6C7"), hx("2B3D37"), "ninguno", "aguatinta", ("amuleto", None),
                     ag_tinta=hx("040806"), ag_medio=hx("1C2A25"), ag_papel=hx("51665C"), ag_halo=1.0, ag_acento="EGQ")
            t.update(sombra_col=(0, 0, 0), sombra_a=90)
    else:
        # abismoClaro: E6F2F6, tinta 05263A, azul 0A5C8C, latón 8C6A1F, verde agua 0F8A86, coral C0563F
        # abismoOscuro: 01060B, luz 5CF2C6, violeta E29BFF, luciferina 3FA9FF
        if not o:
            P = hx("8C9FAE")
            col = g8.pelaje(P, hx("0B2536"), S=hx("667C8D"), L=hx("D3DEE5"), W=hx("7D91A1"), E=hx("E8B84A"), N=hx("C9868A"),
                            A=hx("B8913E"), B=hx("7A5A1E"), M=hx("C9A24A"), Z=hx("8C6A1F"), G=hx("EBD27E"), O=hx("A8DCE6"),
                            J=hx("C0563F"))
            t = g8.T(col, hx("E6F2F6"), hx("05263A"), hx("A6C8D5"), "ninguno", "plano", ("buzo", "buzo"))
            t.update(sombra_col=(5, 38, 58), sombra_a=50)
        else:
            v = lambda h_, k: mezcla((0, 0, 0), hx(h_), k)
            col = g8.pelaje(v("3A6E86", 0.42), v("5CF2C6", 0.78), S=v("3A6E86", 0.26), L=v("5C9AB0", 0.5), W=v("3A6E86", 0.42),
                            D=v("3A6E86", 0.22), T=v("3A6E86", 0.36), E=hx("C8FFF2"), N=v("E29BFF", 0.7), R=v("3A6E86", 0.42),
                            A=v("B8913E", 0.32), B=v("B8913E", 0.2), M=v("B8913E", 0.34), Z=v("B8913E", 0.22), G=v("3FA9FF", 0.75),
                            O=v("5CF2C6", 0.7), C=v("5CF2C6", 0.6), J=v("3FA9FF", 0.8), H=hx("E6FFF8"))
            col["K"] = v("5CF2C6", 0.78)
            t = g8.T(col, hx("01060B"), hx("D6F0F2"), hx("163347"), "ninguno", "bio", ("buzo", "buzo"),
                     fotoforos=(hx("E29BFF"), hx("3FA9FF")))
            t.update(pantalla=True, reflejo=hx("5CF2C6"))
    t["oscuro"] = o
    t["ajustar"] = False
    t["col"] = dict(t["col"]); t["col"].update(colores(nombre, o, 0, t))
    return t


def tema(nombre, oscuro=False):
    _ULTIMO["n"] = nombre
    if nombre in MIOS:
        return tema_mio(nombre, oscuro)
    return _tema_prev(nombre, oscuro)


for _m in list(sys.modules):
    if _m.startswith("gato") and hasattr(sys.modules[_m], "tema") and _m != __name__:
        sys.modules[_m].tema = tema

# Sin líneas de escaneo en el abismo (no es una pantalla): solo queda la viñeta, como mirar por la escotilla.
_lineas = g8.lineas_escaneo


def lineas_escaneo(img):
    if _ULTIMO["n"] == "abismo":
        return img
    return _lineas(img)


g8.lineas_escaneo = lineas_escaneo

# =====================================================================
#  Las dos acciones de cada tema
# =====================================================================
brazo = g17.brazo


def erizar(k):
    """Ve algo en el agua: baja la cabeza hacia el charco, arquea el lomo, eriza la cola y bufa."""
    p = dict(g8.ANIMS["enojado"][k % 3])
    p.update(cab=(46.6, 34.6 + 0.4 * (k % 2)), inc=24, orejas=0.9 + 0.1 * (k % 2), lengua=k in (1, 2), ojo="abierto", semilla=k)
    p["fn"] = g8.p_del((40, 45), (43.5, SUELO)); p["ff"] = g8.p_del((36, 45), (37.5, SUELO))
    return p, dict(obj=(57.6, 57.4), k=k)


def nadar(k):
    """Nada: el cuerpo se despega del fondo; las patas de adelante bracean y las de atrás patalean, la cola va estirada."""
    u = 2 * math.pi * k / 4
    s, c = math.sin(u), math.cos(u)
    dy = -6.0 - 0.9 * s
    p = g8.mover(g8.ANIMS["quieto"][0], 0, dy)
    p["fn"] = brazo((40, 45 + dy), (47.0 + 2.6 * c, 50.5 + dy - 2.0 * s), (1.0, 0.6))
    p["ff"] = brazo((36, 45 + dy), (42.4 - 2.4 * c, 51.6 + dy + 2.0 * s), (1.0, 0.6))
    p["hn"] = g8.p_tra((21, 43 + dy), (14.0 - 1.6 * c, 52.0 + dy + 1.6 * s))
    p["hf"] = g8.p_tra((23.5, 43 + dy), (17.4 + 1.6 * c, 53.0 + dy - 1.6 * s))
    p["cola"] = [(14, 39 + dy), (9.6, 37.6 + dy + 0.6 * s), (5.8, 37.2 + dy + 1.2 * s), (3.0, 38.4 + dy + 1.8 * s),
                 (1.8, 40.6 + dy + 1.6 * c), (2.6, 42.4 + dy + 1.2 * c)]
    p["inc"] = -8 + 2 * s
    p["viento"] = 0.4
    cab = p["cab"]
    return p, dict(obj=(cab[0] - 11.0, cab[1] - 9.0), k=k)


g17.ACCIONES.update(erizar=(erizar, 4, "quieto"), nadar=(nadar, 4, "quieto"))
g17.ESCALA.update(erizar=1.25, nadar=1.0)
rect = g17.rect


def s_hongo(L, a):                # ruinas: un hongo que rompió el asfalto
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_K", [(x - 5.2, y + 4.2), (x - 2.4, y + 3.4), (x + 0.2, y + 4.0), (x + 3.4, y + 3.2)], 0.4)   # grieta
    L.elipse("b_X", x + 2.8, y + 3.2, 1.6, 0.6, -20); L.elipse("b_X", x - 3.0, y + 3.6, 1.4, 0.5, 15)       # pasto
    L.capsula("b_U", [(x, y + 3.6), (x + 0.2, y - 0.6)], 1.3)                                                 # tallo
    w = 0.15 * (k % 2)
    L.poligono("b_V", [(x - 3.2 - w, y - 0.2), (x - 2.4, y - 2.4), (x + 0.2, y - 3.4), (x + 2.8, y - 2.4), (x + 3.6 + w, y - 0.2)])
    L.elipse("b_Q", x - 1.2, y - 1.8, 0.5, 0.5); L.elipse("b_Q", x + 1.4, y - 2.2, 0.55, 0.55)
    if k in (2, 3):                                                                                            # esporas
        for j in range(2 + k - 2):
            L.elipse("b_Q", x - 3.4 - j * 1.2, y - 3.6 - (j + k) % 3 * 1.0, 0.35, 0.35)


def t_brote(L, a):                # ruinas: el brote en una lata
    x, y = a["obj"]; k = a["k"]
    rect(L, "b_U", x - 2.6, y - 3.4, x + 2.8, y + 2.8)
    L.capsula("b_V", [(x - 2.6, y - 0.4), (x + 2.8, y - 0.4)], 1.5)                                           # etiqueta oxidada
    L.capsula("b_X", [(x - 2.4, y - 3.4), (x + 2.6, y - 3.4)], 0.9)                                           # tierra
    sw = 0.5 * math.sin(k * math.pi / 2)
    L.capsula("b_G", [(x + 0.2, y - 3.6), (x + 0.4 + sw, y - 7.4)], 0.6)
    L.elipse("b_G", x - 1.0 + sw, y - 6.6, 1.4, 0.7, 25); L.elipse("b_G", x + 1.8 + sw, y - 7.6, 1.5, 0.75, -30)
    if k == 1:
        L.elipse("b_Q", x + 1.8, y - 9.6, 0.5, 0.7)                                                          # gota de rocío


def e_charco(L, a):               # arcano: algo en el charco de la marea
    x, y = a["obj"]; k = a["k"]
    L.elipse("b_U", x, y + 1.7, 5.4, 1.5)
    r = 2.0 + 1.1 * k
    L.capsula("b_Q", [(x - r, y + 1.8), (x - r * 0.6, y + 1.1), (x + r * 0.6, y + 1.1), (x + r, y + 1.8)], 0.4)
    alto = (1.4, 4.2, 7.0, 3.6)[k]
    if alto:
        base = (x + 1.2, y + 1.2)
        pts = [base, (base[0] + 0.6, base[1] - alto * 0.5), (base[0] - 0.4 + (0.6 if k == 2 else 0), base[1] - alto),
               (base[0] - 1.8, base[1] - alto + (0.8 if k == 2 else 0.2))]
        L.capsula("b_V", pts[:3], 1.7); L.capsula("b_V", pts[2:], 1.1)
        for f in (0.35, 0.7):
            L.elipse("b_X", base[0] + 0.9, base[1] - alto * f, 0.35, 0.35)


def s_signo(L, a):                # arcano: un signo de tiza en una piedra
    x, y = a["obj"]; k = a["k"]
    L.poligono("b_U", [(x - 2.0, y + 4.4), (x - 2.6, y - 1.6), (x - 1.4, y - 4.6), (x + 1.8, y - 5.2), (x + 3.6, y - 2.4),
                       (x + 3.8, y + 4.4)])
    L.elipse("b_X", x - 1.6, y + 3.6, 1.2, 0.8)                                                                # musgo
    c_ = "b_Q" if k % 2 == 0 else "b_V"
    L.capsula(c_, [(x + 0.6, y - 3.4), (x + 0.6, y + 2.6)], 0.45)
    L.capsula(c_, [(x - 1.0, y - 1.6), (x + 0.6, y - 0.4), (x + 2.2, y - 1.8)], 0.45)
    L.capsula(c_, [(x - 0.8, y + 1.6), (x + 2.4, y + 1.2)], 0.45)


def n_burbujas(L, a):             # abismo: burbujas que suben desde la válvula del casco
    x, y = a["obj"]; k = a["k"]
    for j in range(3):
        f = ((k + j * 4 / 3) % 4) / 4
        bx, by, r = x + 1.6 * math.sin(f * 5 + j), y + 4.0 - f * 13.0, 0.8 + 0.6 * f + 0.2 * (j == 1)
        L.elipse("b_V", bx, by, r, r)
        L.elipse("b_Q", bx - 0.25 * r, by - 0.25 * r, r * 0.45, r * 0.45)


def m_pez_abisal(L, a):           # abismo: el rape con su señuelo de luz
    x, y = a["obj"]; k = a["k"]
    L.elipse("b_U", x, y, 3.2, 2.4)
    L.poligono("b_U", [(x - 2.6, y), (x - 5.2, y - 1.8), (x - 4.6, y), (x - 5.2, y + 1.8)])
    L.poligono("b_X", [(x + 1.4, y + 0.6), (x + 3.4, y + 0.2), (x + 2.6, y + 1.6)])                             # boca y dientes
    L.elipse("b_V", x + 1.2, y - 0.9, 0.6, 0.6)
    L.capsula("b_X", [(x + 0.6, y - 2.2), (x + 1.8, y - 4.6), (x + 3.8, y - 4.8)], 0.35)                      # la caña
    r = 0.9 if k % 2 == 0 else 1.25
    L.elipse("b_Q", x + 4.2, y - 4.4, r, r)


g17.ESP.update(
    ruinas=[("olfatear", s_hongo, "huele un hongo en el asfalto"), ("trabajar", t_brote, "cuida un brote en una lata")],
    arcano=[("erizar", e_charco, "ve algo en el agua y eriza el lomo"), ("olfatear", s_signo, "olfatea un símbolo de tiza")],
    abismo=[("nadar", n_burbujas, "nada entre burbujas"), ("mirar", m_pez_abisal, "sigue la luz de un pez abisal")],
)
ESCALA_MIA = {("ruinas", 0): 1.35, ("ruinas", 1): 1.25, ("arcano", 0): 1.45, ("arcano", 1): 1.45, ("abismo", 0): 1.0, ("abismo", 1): 1.7}
for _n in MIOS:
    for _j, (_acc, _f, _) in enumerate(g17.ESP[_n]):
        _fn, _k, _base = g17.ACCIONES[_acc]
        _cs = []
        for _q in range(_k):
            _p, _a = _fn(_q)
            _a["accion"] = _acc; _a["escala"] = ESCALA_MIA[(_n, _j)]
            _cs.append(dict(_p, objeto=(_f, _a), semilla=_q))
        g8.ANIMS[f"esp_{_n}_{_j}"] = _cs

C_ = lambda *hs: tuple(hx(h) for h in hs)
g17.COL.update(ruinas=C_("F0E6D0", "B4552A", "6E9A44", "F6EEDC"), arcano=C_("2D4A40", "6E8A70", "D6D0B8", "E8E2CC"),
               abismo=C_("E6F6FA", "0A5C8C", "C9E6EE", "F2FBFD"))
COLORES = {   # (tema, oscuro, acción) -> U, V, X, Q (y G para el brote)
    ("ruinas", False, 0): dict(U=hx("F0E6D0"), V=hx("B4552A"), X=hx("6E9A44"), Q=hx("F6EEDC")),
    ("ruinas", False, 1): dict(U=hx("B9BDB4"), V=hx("A34A1E"), X=hx("5A4632"), Q=hx("CDEBF0"), G=hx("5E9A36")),
    ("arcano", False, 0): dict(U=hx("4E6E62"), V=hx("33463C"), X=hx("D6D0B8"), Q=hx("EEE8D4")),
    ("arcano", False, 1): dict(U=hx("8E8A78"), V=hx("A8C9B8"), X=hx("4C564C"), Q=hx("F4EFDF")),
    ("abismo", False, 0): dict(U=hx("E6F6FA"), V=hx("6FB3CF"), X=hx("C9E6EE"), Q=hx("F6FDFF")),
    ("abismo", False, 1): dict(U=hx("2E4456"), V=hx("D6F0F2"), X=hx("D6F0F2"), Q=hx("5CF2C6")),
}
COLORES[("ruinas", True, 0)] = {k: mezcla(v, hx("22302A"), 0.14) for k, v in COLORES[("ruinas", False, 0)].items()}
COLORES[("ruinas", True, 1)] = {k: mezcla(v, hx("22302A"), 0.14) for k, v in COLORES[("ruinas", False, 1)].items()}
COLORES[("arcano", True, 0)] = dict(U=hx("2E5A4E"), V=hx("0E1814"), X=hx("9DBFA8"), Q=hx("7DBBA4"))
COLORES[("arcano", True, 1)] = dict(U=hx("4A5550"), V=hx("7DBBA4"), X=hx("2B3D37"), Q=hx("D8F0E2"))
_v = lambda h_, k: mezcla((0, 0, 0), hx(h_), k)
COLORES[("abismo", True, 0)] = dict(U=_v("3FA9FF", 0.4), V=_v("5CF2C6", 0.5), X=_v("3FA9FF", 0.5), Q=_v("8FD8FF", 0.62))
COLORES[("abismo", True, 1)] = dict(U=_v("3A6E86", 0.3), V=_v("E29BFF", 0.9), X=_v("5CF2C6", 0.55), Q=hx("D8FFF4"))
_colores = g17.colores


def colores(nombre, oscuro, j, t):
    if nombre in MIOS:
        return dict(COLORES[(nombre, bool(oscuro), j)])
    return _colores(nombre, oscuro, j, t)


g17.colores = colores
g8._spr.clear()


# =====================================================================
#  Fondos (a 1 pixel por dp, como el gato; se amplían ×3 sin suavizar)
# =====================================================================
def _mezclar(px, x, y, col, k):
    if 0 <= x < W and 0 <= y < H:
        px[x, y] = mezcla(px[x, y], col, k)


def _pon(px, x, y, col):
    if 0 <= x < W and 0 <= y < H:
        px[int(x), int(y)] = col


# ---------------------------------------------------------------------
#  Ciudad verde: la calle que el bosque recuperó. Edificios de hormigón con cortinas de hiedra, autos oxidados cubiertos de
#  hojas, un semáforo apagado con una enredadera y un abedul que rompió el asfalto. De noche: estrellas (ya no hay luz en
#  la ciudad), una sola ventana con fuego y luciérnagas.
# ---------------------------------------------------------------------
PAL_RUINAS = {
    False: dict(cielo=[hx("BFD2CE"), hx("CCDAD5"), hx("D9E3DB"), hx("E4E9E1")], nube=(hx("F3F5EF"), hx("E4E9E2"), hx("D0DAD4")),
                lejos=hx("B4BEB8"), lejos2=hx("A8B2AC"), lejos_v=hx("9AA59F"), lejos_verde=hx("9FB396"),
                muro=hx("C4C2B6"), muro2=hx("ADACA0"), muro3=hx("989789"), ventana=hx("454C46"), ventana2=hx("5E6B57"), marco=hx("8A8A7E"),
                hiedra=(hx("27461C"), hx("3E6B2A"), hx("5E8A3A"), hx("88B05A")), oxido=hx("A34A1E"), oxido2=hx("C46E3E"), ambar=hx("B57A0E"),
                asfalto=hx("7C7F77"), asfalto2=hx("6E716A"), grieta=hx("4F534D"), raya=hx("CFC69A"), acera=hx("BAB8AC"), acera2=hx("A8A69A"),
                junta=hx("8F8D81"), musgo=hx("6E9A44"), tronco=hx("E2DED2"), corteza=hx("3A3F3A"), vidrio=hx("6E8C8A"), llanta=hx("2E322E"),
                pinturas=(hx("6F8F8A"), hx("A35A4A"), hx("C9B27A")), semaforo=(hx("4E2A24"), hx("5A4A22"), hx("2A3E2A")), poste=hx("5C625C"),
                flor=(hx("F2D23A"), hx("FFFFFF"))),
    True: dict(cielo=[hx("060A08"), hx("0A100D"), hx("0F1612"), hx("152019")], nube=None,
               lejos=hx("161F1A"), lejos2=hx("121A16"), lejos_v=hx("0C1210"), lejos_verde=hx("13221A"),
               muro=hx("27302A"), muro2=hx("1E2621"), muro3=hx("171E1A"), ventana=hx("0A0F0C"), ventana2=hx("101A12"), marco=hx("1A211D"),
               hiedra=(hx("0B170D"), hx("122416"), hx("1A331C"), hx("274A26")), oxido=hx("43231A"), oxido2=hx("5A3020"), ambar=hx("3A3014"),
               asfalto=hx("1E2420"), asfalto2=hx("19201C"), grieta=hx("0E1311"), raya=hx("3A3E33"), acera=hx("2C342E"), acera2=hx("252C27"),
               junta=hx("171D19"), musgo=hx("26401F"), tronco=hx("5C625A"), corteza=hx("1A1F1C"), vidrio=hx("1E2C2C"), llanta=hx("0C100E"),
               pinturas=(hx("22302E"), hx("3A2522"), hx("3A3628")), semaforo=(hx("2A1612"), hx("2A2412"), hx("142014")), poste=hx("2A302C"),
               flor=(hx("5A5420"), hx("6A7068"))),
}


def cortina_hiedra(px, x0, x1, ytop, largo, verdes, rnd, ybaja=H):
    """Hiedra que cuelga de una cornisa: cada columna baja un largo distinto, con huecos por donde se ve el muro."""
    fase = rnd.uniform(0, 6)
    for x in range(x0, x1):
        l = largo * (0.55 + 0.45 * math.sin((x - x0) / max(1, x1 - x0) * math.pi)) + 3 * math.sin(x * 0.7 + fase) + rnd.uniform(-2, 2)
        for y in range(ytop, min(ybaja, ytop + int(l))):
            if rnd.random() < 0.13:
                continue
            k = (y - ytop) / max(1, l)
            j = 3 if rnd.random() < 0.18 - 0.1 * k else (2 if rnd.random() < 0.5 - 0.3 * k else (1 if rnd.random() < 0.75 else 0))
            _pon(px, x, y, verdes[j])
        if rnd.random() < 0.35:
            _pon(px, x, ytop + int(l) + 1, verdes[1])


def mata(px, x, y, r, verdes, rnd, n=None):
    """Mata de hojas (copa de árbol, arbusto, hiedra sobre un auto): puntos al azar, más claros arriba."""
    for _ in range(n or int(r * r * 2.6)):
        a, q = rnd.uniform(0, 2 * math.pi), r * math.sqrt(rnd.random())
        xx, yy = x + q * math.cos(a), y + q * math.sin(a) * 0.75
        j = 3 if (yy < y - r * 0.3 and rnd.random() < 0.45) else rnd.choice((0, 1, 1, 2, 2))
        _pon(px, round(xx), round(yy), verdes[j])


def edificio(im, d, x0, w, top, p, rnd, base=61, hiedra=(), arbol=False, roto=0):
    px = im.load()
    # Muro con el lado de la luz y el de la sombra; la cornisa rota.
    d.rectangle([x0, top, x0 + w - 1, base], fill=p["muro"])
    d.rectangle([x0 + w - 4, top, x0 + w - 1, base], fill=p["muro2"])
    for x in range(x0, x0 + w):
        mella = int(max(0, roto * math.sin((x - x0) * 0.9) + rnd.uniform(-1, 1) * roto * 0.6))
        for y in range(top, top + mella):
            _pon(px, x, y, p["cielo"][2] if top > 20 else p["cielo"][1])
    for y in range(top + 6, base - 3, 8):                                                   # losas de cada piso
        d.line([x0, y, x0 + w - 1, y], fill=p["muro3"])
    for y in range(top + 2, base - 6, 8):                                                   # ventanas
        for x in range(x0 + 3, x0 + w - 6, 6):
            c = p["ventana"] if rnd.random() < 0.8 else p["ventana2"]
            d.rectangle([x, y, x + 3, y + 3], fill=c)
            d.line([x, y + 4, x + 3, y + 4], fill=p["marco"])
            if rnd.random() < 0.18:
                mata(px, x + 2, y + 1, 2, p["hiedra"], rnd, 6)
    for x in range(x0, x0 + w, 3):                                                          # manchas de humedad
        if rnd.random() < 0.4:
            for y in range(top + rnd.randint(2, 12), top + rnd.randint(14, 30)):
                if 0 <= y < base: _mezclar(px, x, y, p["muro3"], 0.35)
    for xa, largo in hiedra:
        cortina_hiedra(px, x0 + xa, x0 + xa + rnd.randint(8, 16), top, largo, p["hiedra"], rnd, base)
    if arbol:
        xt = x0 + w // 2
        d.line([xt, top, xt - 1, top - 6], fill=p["corteza"])
        mata(px, xt - 1, top - 9, 7, p["hiedra"], rnd)
    for k in range(2):                                                                      # varillas que asoman
        xv = x0 + rnd.randint(2, w - 3)
        d.line([xv, top - 1, xv + rnd.choice((-1, 1)), top - 3], fill=p["corteza"])


def auto(im, d, x, yb, p, pintura, rnd, largo=44):
    """Auto abandonado de perfil: llantas ponchadas, vidrios rotos, óxido y hiedra encima."""
    px = im.load()
    k = largo / 32
    q = lambda u, v: (round(x + u * k), round(yb - v * k))
    d.polygon([q(0, 3), q(1, 8), q(8, 9), q(11, 14), q(22, 14), q(26, 9), q(31, 8), q(32, 3)], fill=pintura)
    d.line([q(1, 8), q(31, 8)], fill=mezcla(pintura, (255, 255, 255), 0.15))
    d.polygon([q(9.2, 9), q(12, 13), q(16, 13), q(16, 9)], fill=p["vidrio"])
    d.polygon([q(18, 9), q(18, 13), q(21, 13), q(24.4, 9)], fill=p["ventana"])
    d.line([q(17, 13), q(17, 9)], fill=mezcla(pintura, (0, 0, 0), 0.3))
    d.line([q(0.5, 2.6), q(32, 2.6)], fill=mezcla(pintura, (0, 0, 0), 0.35))
    d.line([q(0.5, 3.4), q(32, 3.4)], fill=mezcla(pintura, (0, 0, 0), 0.25))
    d.point(q(31, 6.5), fill=p["ventana"]); d.point(q(1.2, 6.5), fill=p["oxido"])
    for u in (6, 26):                                                                       # llantas ponchadas
        cx, cy = q(u, 1.2)
        d.ellipse([cx - 4, cy - 3, cx + 4, cy + 2], fill=p["llanta"])
        d.point((cx, cy - 1), fill=p["poste"]); d.point((cx + 1, cy - 1), fill=p["poste"])
    for _ in range(26):                                                                     # óxido
        xx, yy = q(rnd.uniform(1, 31), rnd.uniform(3.5, 8))
        _pon(px, xx, yy, p["oxido"] if rnd.random() < 0.6 else p["oxido2"])
    mx, my = q(16, 14.5)
    mata(px, mx, my, 8, p["hiedra"], rnd, 120)                                               # hiedra sobre el techo
    mata(px, *q(4, 8), 4, p["hiedra"], rnd, 26)
    cortina_hiedra(px, q(12, 0)[0], q(22, 0)[0], my, 7, p["hiedra"], rnd)
    for u in range(0, 33, 5):                                                               # pasto bajo el auto
        _pon(px, *q(u, 0.3), p["hiedra"][2]); _pon(px, q(u, 0.3)[0] + 1, q(u, 0.3)[1] - 1, p["hiedra"][1])


def semaforo(im, d, x, p, rnd):
    px = im.load()
    d.rectangle([x, 16, x + 1, 74], fill=p["poste"])
    d.line([x + 1, 18, x - 26, 18], fill=p["poste"]); d.line([x, 21, x - 6, 18], fill=p["poste"])
    d.rectangle([x - 27, 19, x - 22, 33], fill=p["llanta"])                                 # la caja, apagada
    for j, c in enumerate(p["semaforo"]):
        d.ellipse([x - 26, 20 + j * 4, x - 23, 23 + j * 4], fill=c)
        d.line([x - 27, 20 + j * 4, x - 22, 20 + j * 4], fill=p["llanta"])
    for y in range(22, 74, 3):                                                              # enredadera que sube en espiral
        lado = (y // 3) % 2
        _pon(px, x - 1 + 3 * lado, y, p["hiedra"][1 + (y % 2)])
        _pon(px, x - 1 + 3 * lado, y + 1, p["hiedra"][2])
        if (y // 3) % 3 == 0: _pon(px, x + (-2 if lado == 0 else 3), y, p["hiedra"][3])
    mata(px, x - 14, 18, 3, p["hiedra"], rnd, 14)


def abedul(im, d, x, p, rnd):
    """Un abedul joven que salió por una grieta del asfalto."""
    px = im.load()
    for y in range(22, 72):
        xx = x + round(1.2 * math.sin(y * 0.09))
        d.line([xx, y, xx + 1, y], fill=p["tronco"])
        if rnd.random() < 0.25: _pon(px, xx + rnd.randint(0, 1), y, p["corteza"])
    d.line([x, 40, x - 7, 30], fill=p["tronco"]); d.line([x + 1, 34, x + 8, 26], fill=p["tronco"])
    for cx, cy, r in ((x - 6, 26, 8), (x + 7, 21, 9), (x, 16, 9), (x + 2, 30, 6)):
        mata(px, cx, cy, r, p["hiedra"], rnd)


def fondo_ruinas(t):
    o = t["oscuro"]
    p = PAL_RUINAS[o]
    rnd = random.Random(14)
    im, d = lienzo(p["cielo"][-1]); px = im.load()
    degradado(im, 0, 56, p["cielo"])
    titilan = []
    if o:
        for _ in range(120):
            x, y = rnd.randrange(W), rnd.randrange(0, 40)
            px[x, y] = rnd.choice([hx("E3E6DC"), hx("8E9A90"), hx("5C665E"), hx("C9D2C0")])
            titilan.append((x, y))
        luz(im, 236, 11, 13, hx("2A3A30"), 0.6, 3)
        for yy in range(5, 18):                                                            # luna menguante
            for xx in range(229, 244):
                if (xx - 236) ** 2 + (yy - 11) ** 2 <= 30 and (xx - 239) ** 2 + (yy - 10) ** 2 > 26:
                    px[xx, yy] = hx("E3E6DC") if xx < 234 or yy > 13 else hx("C9D2C0")
    else:
        for x, y, w in ((20, 10, 30), (120, 6, 40), (250, 12, 26), (330, 7, 34)):
            g9.nube(d, x, y, w, *p["nube"])
    # Lejos: torres rotas en la bruma, algunas con árboles en la azotea.
    x = -6
    while x < W:
        w = rnd.randint(14, 28); top = rnd.randint(16, 36)
        pts = [(x, 62), (x, top + rnd.randint(0, 3))]
        for k in range(1, 4):
            pts.append((x + w * k / 4, top + rnd.randint(-2, 5)))
        pts += [(x + w, top + rnd.randint(0, 4)), (x + w, 62)]
        d.polygon(pts, fill=p["lejos"])
        d.rectangle([x + w - 3, top + 4, x + w, 62], fill=p["lejos2"])
        for yy in range(top + 5, 60, 3):
            for xx in range(x + 2, x + w - 3, 3):
                if rnd.random() < 0.55: _pon(px, xx, yy, p["lejos_v"])
        if rnd.random() < 0.5:
            mata(px, x + w // 2, top + 1, 4, (p["lejos_verde"],) * 2 + (p["lejos2"], p["lejos_verde"]), rnd, 22)
        x += w + rnd.randint(-3, 4)
    d.line([300, 30, 300, 6], fill=p["lejos2"]); d.line([296, 30, 300, 10], fill=p["lejos2"]); d.line([304, 30, 300, 10], fill=p["lejos2"])
    # Edificios cercanos.
    edificio(im, d, -2, 70, 4, p, rnd, hiedra=((6, 34), (34, 50), (52, 22)), roto=2)
    edificio(im, d, 150, 62, 24, p, rnd, hiedra=((4, 18), (40, 30)), arbol=True, roto=1)
    edificio(im, d, 342, 72, 10, p, rnd, hiedra=((8, 40), (30, 22), (50, 44)), roto=3)
    ventana_fuego = (164, 32)
    # La acera de enfrente, la calle con grietas y rayas borradas.
    d.rectangle([0, 60, W, 62], fill=p["acera2"]); d.line([0, 60, W, 60], fill=p["acera"])
    degradado(im, 63, 74, [p["asfalto2"], p["asfalto"], p["asfalto"]])
    for x in range(8, W, 26):
        if rnd.random() < 0.7: d.line([x, 67, x + 10, 67], fill=p["raya"])
    for _ in range(9):                                                                     # grietas del asfalto con pasto
        x, y = rnd.randrange(W), rnd.randint(64, 72)
        for k in range(rnd.randint(5, 12)):
            x += rnd.choice((1, 1, 2)); y = max(63, min(73, y + rnd.choice((-1, 0, 0, 1))))
            _pon(px, x, y, p["grieta"])
            if rnd.random() < 0.3: _pon(px, x, y - 1, p["hiedra"][2])
    for x in range(0, W, 2):                                                               # pasto al pie de la acera de enfrente
        if rnd.random() < 0.5: _pon(px, x, 62, p["hiedra"][rnd.choice((1, 2))])
    # Autos, el semáforo y el abedul.
    auto(im, d, 66, 72, p, p["pinturas"][0], rnd, 46)
    auto(im, d, 226, 71, p, p["pinturas"][1], rnd, 42)
    semaforo(im, d, 318, p, rnd)
    abedul(im, d, 128, p, rnd)
    # La acera de este lado: losas, juntas con musgo, dientes de león.
    d.rectangle([0, 74, W, H], fill=p["acera"])
    d.line([0, 74, W, 74], fill=p["junta"])
    for x in range(0, W, 22):
        d.line([x, 75, x - 2, H], fill=p["junta"])
    for x in range(0, W):
        if rnd.random() < 0.3: _pon(px, x, 75 + rnd.randint(0, 4), p["acera2"])
        if rnd.random() < 0.18: _pon(px, x, 74, p["musgo"])
    for x in range(10, W, 22):
        for k in range(rnd.randint(2, 5)):
            _pon(px, x - 2 + k % 3 - 1, 75 + k, p["musgo"])
    for x in (40, 104, 196, 286, 378):
        for k in range(4):
            _pon(px, x + k - 2, 73 - (k % 2), p["hiedra"][2]); _pon(px, x + k - 1, 72, p["hiedra"][1])
        _pon(px, x, 70, p["flor"][0]); _pon(px, x + 1, 71, p["hiedra"][2])
    if o:
        xv, yv = ventana_fuego                                                              # una ventana con fuego
        d.rectangle([xv, yv, xv + 3, yv + 3], fill=hx("E08A55")); d.point((xv + 1, yv + 3), fill=hx("E9C25A"))
        luz(im, xv + 1.5, yv + 1.5, 8, hx("E08A55"), 0.22, 3)
        for x0, w_ in ((60, 18), (190, 24), (300, 14)):                                    # charcos con la luna
            d.ellipse([x0, 69, x0 + w_, 72], fill=hx("152420"))
            d.line([x0 + w_ // 2 - 2, 70, x0 + w_ // 2 + 2, 70], fill=hx("5C6A60"))
    big = subir(im)

    # Lo que se mueve: de día, hojas que caen y vilanos que suben; de noche, luciérnagas.
    rnd2 = random.Random(3)
    hojas = [(rnd2.uniform(0, W), rnd2.random(), rnd2.choice((p["ambar"], p["hiedra"][2], p["oxido2"], p["hiedra"][3])), j % 3 == 0)
             for j in range(10)]
    vilanos = [(rnd2.uniform(0, W), rnd2.random()) for _ in range(5)]
    luces = [(rnd2.uniform(6, W - 6), rnd2.uniform(28, 72), rnd2.random(), j % 4 == 0) for j in range(16)]
    L = 160

    def de_dia(img, tick, delante):
        dd = ImageDraw.Draw(img)
        for x0, f0, col, adelante in hojas:
            if adelante != delante: continue
            f = (f0 + tick / L) % 1
            y = -4 + f * 88
            x = x0 + 5 * math.sin(2 * math.pi * (tick / 80 + f0)) + f * 10
            if (tick // 5 + int(f0 * 7)) % 4 < 2:
                cuad(dd, x, y, col, 2, 1)
            else:
                cuad(dd, x, y, col, 1, 1); cuad(dd, x + 1, y + 1, col, 1, 1)
        if delante: return
        for x0, f0 in vilanos:
            f = (f0 + tick / L) % 1
            y = 84 - f * 90
            x = x0 + 3 * math.sin(2 * math.pi * (tick / 40 + f0))
            cuad(dd, x, y, p["corteza"]); cuad(dd, x, y - 1, hx("FFFFFF")); cuad(dd, x - 1, y - 2, hx("F3F5EF"), 3, 1)

    def de_noche(img, tick, delante):
        dd = ImageDraw.Draw(img)
        for x0, y0, f0, adelante in luces:
            if adelante != delante: continue
            u = 2 * math.pi * (tick / L + f0)
            x, y = x0 + 6 * math.sin(u), y0 + 3 * math.sin(2 * u + f0 * 3)
            b = 0.5 + 0.5 * math.sin(2 * math.pi * (tick / 40 + f0 * 2))
            if b < 0.25: continue
            halo = mezcla(hx("1A2414"), hx("6A7A2A"), b)
            cuad(dd, x - 1, y, halo, 3, 1); cuad(dd, x, y - 1, halo, 1, 3)
            cuad(dd, x, y, mezcla(hx("9AAA3A"), hx("F2FFB0"), b))
        if delante: return
        for j, (sx, sy) in enumerate(titilan[:30]):
            if (j * 5 + tick // 4) % 8 == 0: cuad(dd, sx, sy, hx("FFFFFF"))
        if (tick // 4) % 5 != 0:                                                          # el fuego de la ventana
            xv_, yv_ = ventana_fuego
            cuad(dd, xv_ + 1 + (tick // 4) % 2, yv_ + 1, hx("F7C46A"), 1, 2)
    g9.ANIM[("ruinas", o)] = [de_noche if o else de_dia]
    return big


g8.FONDOS["ruinas"] = fondo_ruinas


# ---------------------------------------------------------------------
#  Horror cósmico: la costa del pueblo impresa en aguatinta. Cada zona es una «densidad» de tinta (0 = papel, 1 = negro) y
#  el grano de resina decide, pixel a pixel, entre cinco tonos de la tinta verde-negra (verde en el agua, sepia en la madera).
#  De día, papel de legajo con niebla; de noche, la plancha entintada: la luna, el faro que gira y lo que sale del agua.
# ---------------------------------------------------------------------
TONOS_ARCANO = {
    False: [[hx("ECE2CA"), hx("CFC6AC"), hx("9C9D88"), hx("5B6658"), hx("1B261F")],      # neutro
            [hx("E2E3CC"), hx("BCCBB8"), hx("7F9C8E"), hx("3E5E51"), hx("16302A")],      # agua
            [hx("EEE0C2"), hx("D5C29C"), hx("A88E66"), hx("6A4B26"), hx("2E2214")]],     # madera, piedra
    True: [[hx("8C9E93"), hx("4F645A"), hx("2C3E37"), hx("17231F"), hx("070D0B")],
           [hx("A2D4C1"), hx("5E9483"), hx("2E5A4E"), hx("143229"), hx("06120E")],
           [hx("9A8C74"), hx("5E5444"), hx("3A3226"), hx("201A13"), hx("0B0806")]],
}
_GRANO_F = np.random.default_rng(1926).random((H, W)).astype(np.float32)


class Plancha:
    """La plancha de aguatinta: densidad y tinte por pixel (a 1 pixel por dp), y acentos de color que van encima."""
    def __init__(self, o):
        self.o = o
        self.den = Image.new("L", (W, H), 0); self.tin = Image.new("L", (W, H), 0)
        self.acento = Image.new("RGBA", (W, H), (0, 0, 0, 0))
        self.dd, self.dt, self.da = ImageDraw.Draw(self.den), ImageDraw.Draw(self.tin), ImageDraw.Draw(self.acento)

    def v(self, dia, noche):
        return round(255 * (noche if self.o else dia))

    def poli(self, pts, dia, noche, tinte=0):
        self.dd.polygon(pts, fill=self.v(dia, noche)); self.dt.polygon(pts, fill=tinte)

    def rect(self, caja, dia, noche, tinte=0):
        self.dd.rectangle(caja, fill=self.v(dia, noche)); self.dt.rectangle(caja, fill=tinte)

    def elipse(self, caja, dia, noche, tinte=0):
        self.dd.ellipse(caja, fill=self.v(dia, noche)); self.dt.ellipse(caja, fill=tinte)

    def linea(self, pts, dia, noche, tinte=0, w=1):
        self.dd.line(pts, fill=self.v(dia, noche), width=w); self.dt.line(pts, fill=tinte, width=w)

    def arrays(self):
        return (np.asarray(self.den, np.float32) / 255, np.asarray(self.tin, np.uint8).copy(), np.asarray(self.acento).copy())


def imprimir(den, tin, ac, o, y0=0, y1=H, x0=0, x1=W):
    """Densidad → tono con el grano de la resina (el grano fijo decide entre los dos tonos vecinos)."""
    tonos = np.array(TONOS_ARCANO[o], np.uint8)
    g = _GRANO_F[y0:y1, x0:x1]
    idx = np.clip(np.floor(den * 4 + g * 0.999), 0, 4).astype(np.int32)
    out = tonos[np.clip(tin, 0, 2), idx]
    m = ac[..., 3] > 0
    out[m] = ac[..., :3][m]
    return out


def niebla(den, y0, y1, fuerza, hacia):
    """Bancos de niebla: acercan la densidad a la de la niebla, más en el centro de la franja."""
    ys = np.arange(den.shape[0], dtype=np.float32)[:, None]
    f = np.clip(1 - np.abs((ys - (y0 + y1) / 2) / ((y1 - y0) / 2)), 0, 1) ** 1.5 * fuerza
    return den * (1 - f) + hacia * f


def fondo_arcano(t):
    o = t["oscuro"]
    pl = Plancha(o)
    tinta = TONOS_ARCANO[o][0][4]
    # Cielo: de día casi papel; de noche, plancha entintada que se aclara hacia el horizonte.
    for y in range(0, 45):
        f = y / 44
        pl.rect([0, y, W, y], 0.04 + 0.10 * f, 0.86 - 0.26 * f)
    # Mancha de humedad y cerco de taza en el papel del legajo.
    if not o:
        pl.da.ellipse([28, 6, 62, 30], outline=TONOS_ARCANO[False][2][1] + (255,))
        pl.da.arc([30, 8, 60, 28], 200, 330, fill=TONOS_ARCANO[False][2][2] + (255,))
    # El cabo del faro (derecha) y unos riscos lejanos (izquierda).
    pl.poli([(250, 45), (268, 40), (290, 36), (312, 28), (330, 26), (412, 25), (412, 45)], 0.42, 0.9, 2)
    pl.poli([(300, 45), (318, 34), (334, 33), (348, 30), (412, 30), (412, 45)], 0.55, 0.94, 2)
    pl.poli([(0, 45), (0, 34), (14, 32), (30, 36), (52, 38), (70, 45)], 0.3, 0.84, 2)
    # Casita del farero.
    pl.rect([320, 18, 340, 26], 0.1, 0.78); pl.poli([(318, 18), (330, 12), (342, 18)], 0.75, 0.97)
    pl.rect([325, 21, 327, 23], 0.85, 0.97); pl.rect([333, 21, 335, 23], 0.85, 0.97)
    # El faro: torre blanca con franjas de lacre, galería, linterna y cúpula.
    fx = 364
    for y in range(9, 27):
        a = 3.6 + (y - 9) * 0.12
        pl.rect([fx - a, y, fx + a, y], 0.05, 0.62)
        pl.rect([fx + a * 0.3, y, fx + a, y], 0.2, 0.8)
    for y0 in (13, 20):
        pl.da.rectangle([fx - 4, y0, fx + 4, y0 + 2], fill=(hx("7A2C24") if not o else hx("3A1E1A")) + (255,))
        pl.da.line([fx + 2, y0, fx + 4, y0 + 2], fill=(hx("55201A") if not o else hx("26130F")) + (255,))
    pl.rect([fx - 5, 8, fx + 5, 8], 0.95, 0.97); pl.linea([(fx - 5, 6), (fx + 5, 6)], 0.9, 0.95)
    for x in range(fx - 5, fx + 6, 2): pl.rect([x, 6, x, 8], 0.9, 0.95)
    pl.rect([fx - 3, 3, fx + 3, 5], 0.25, 0.3)
    pl.poli([(fx - 3, 3), (fx, 0), (fx + 3, 3)], 0.95, 0.97)
    if o:
        pl.da.rectangle([fx - 2, 3, fx + 2, 5], fill=hx("E6D58C") + (255,))
    # La luna (de noche), velada por la niebla.
    if o:
        for r, dv in ((12, 0.42), (9, 0.3), (6, 0.0)):
            pl.elipse([96 - r, 15 - r, 96 + r, 15 + r], 0, dv)
        pl.elipse([93, 12, 96, 14], 0, 0.15)
    # El mar, con oleaje en rayas cortas y el camino de luz (la luna de noche, el sol pálido de día).
    for y in range(45, 66):
        f = (y - 45) / 20
        pl.rect([0, y, W, y], 0.3 + 0.2 * f, 0.66 + 0.16 * f, 1)
    rnd = random.Random(1890)
    for _ in range(170):
        x, y = rnd.randrange(W), rnd.randrange(46, 65)
        l = rnd.randint(2, 6) + (y - 46) // 4
        claro = (o and abs(x - 96) < 4 + (y - 45) * 0.8) or (not o and rnd.random() < 0.3)
        pl.linea([(x, y), (x + l, y)], 0.05 if claro else 0.62, 0.2 if claro else 0.95, 1)
    for x0, w_, h_ in ((150, 22, 9), (176, 9, 5), (228, 14, 7)):                       # rocas en el agua
        pl.poli([(x0, 60), (x0 + 3, 60 - h_), (x0 + w_ * 0.4, 58 - h_), (x0 + w_ * 0.7, 61 - h_), (x0 + w_, 60)], 0.78, 0.97, 2)
        pl.linea([(x0 - 2, 60), (x0 + w_ + 2, 60)], 0.05, 0.55, 1)
    # Muelle viejo con la caseta de redes (izquierda).
    pl.rect([0, 53, 92, 55], 0.65, 0.95, 2)
    for x in range(4, 92, 11): pl.rect([x, 55, x + 1, 66], 0.85, 0.98, 2)
    pl.rect([8, 34, 40, 53], 0.38, 0.86, 2)
    for x in range(10, 40, 3): pl.linea([(x, 35), (x, 52)], 0.55, 0.92, 2)
    pl.poli([(5, 35), (24, 26), (43, 35)], 0.82, 0.97, 2)
    pl.rect([28, 40, 34, 46], 0.95, 0.97, 2)
    if o:
        pl.da.rectangle([29, 41, 33, 45], fill=hx("C9B36A") + (255,))
        pl.da.line([31, 41, 31, 45], fill=hx("2C3524") + (255,)); pl.da.line([29, 43, 33, 43], fill=hx("2C3524") + (255,))
    pl.rect([14, 41, 20, 52], 0.92, 0.97, 2)
    # La orilla: guijarros, algas, una barca boca abajo, postes con la red tendida.
    for y in range(66, H):
        pl.rect([0, y, W, y], 0.4 + 0.15 * (y - 66) / 14, 0.84 + 0.08 * (y - 66) / 14, 2)
    pl.linea([(0, 66), (W, 66)], 0.08, 0.5, 1)
    for _ in range(140):
        x, y = rnd.randrange(W), rnd.randrange(68, H)
        r = rnd.choice((1, 1, 2))
        pl.elipse([x - r, y - r * 0.6, x + r, y + r * 0.6], rnd.choice((0.18, 0.62, 0.8)), rnd.choice((0.55, 0.97, 0.92)), 2)
    pl.poli([(112, 72), (116, 65), (148, 64), (156, 69), (150, 72)], 0.75, 0.96, 2)            # barca volteada
    pl.linea([(116, 66), (150, 65)], 0.3, 0.7, 2)
    for k in range(4): pl.linea([(120 + k * 8, 65), (121 + k * 8, 71)], 0.92, 0.98, 2)
    for x in (262, 284, 306):                                                                  # postes y red
        pl.rect([x, 52, x + 2, 73], 0.88, 0.98, 2)
    for x in range(264, 306):
        for y in range(54, 68):
            if (x + y) % 4 == 0 or (x - y) % 4 == 0:
                if y < 56 + 3 * math.sin((x - 264) / 42 * math.pi * 2) + 9: pl.rect([x, y, x, y], 0.7, 0.96, 2)
    for x in range(0, W, 7):                                                                   # algas al borde del agua
        pl.linea([(x, 67), (x + 2, 69)], 0.9, 0.98, 1)
    # El camino del gato y la niebla.
    den, tin, ac = pl.arrays()
    den = niebla(den, 30, 60, 0.75 if not o else 0.55, 0.02 if not o else 0.46)
    if o:                                       # de noche, una niebla baja a ras de la orilla (el gato negro se recorta en ella)
        den[50:H] = niebla(den[50:H], 4, 28, 0.75, 0.36)
    base = imprimir(den, tin, ac, o)
    big = subir(Image.fromarray(base))

    # Lo que se mueve. Todo usa la misma plancha: se modifica la densidad de una franja y se vuelve a imprimir.
    L = 128
    lampara = (fx, 4)

    def haz(img, tick, delante):
        """El faro gira: el haz se alarga hacia un lado, se acorta, destella de frente y se va al otro."""
        if delante: return
        ang = 2 * math.pi * tick / L
        s = math.sin(ang)
        y0, y1 = 0, 22
        d_ = den[y0:y1].copy()
        largo = (150 if s < 0 else 60) * abs(s) ** 0.8
        lado = -1 if s < 0 else 1
        if largo > 4:
            for x in range(W):
                u = (x - lampara[0]) * lado
                if 2 < u < largo:
                    h_ = 1.2 + u * 0.07
                    for y in range(max(y0, int(lampara[1] - h_)), min(y1, int(lampara[1] + h_) + 1)):
                        k = (1 - u / largo) ** 0.6 * (0.55 if o else 0.22)
                        d_[y - y0, x] = d_[y - y0, x] * (1 - k) + 0.0 * k
        a_ = ac[y0:y1].copy()
        if math.cos(ang) > 0.7 or o:
            col = hx("FFF6C8") if math.cos(ang) > 0.7 else hx("E6D58C")
            a_[lampara[1] - 1:lampara[1] + 2, lampara[0] - 2:lampara[0] + 3] = col + (255,)
        pedazo = imprimir(d_, tin[y0:y1], a_, o, y0, y1)
        img.paste(Image.fromarray(pedazo).resize((W * ESC, (y1 - y0) * ESC), Image.NEAREST), (0, y0 * ESC))

    bancos = [(70, 140, 38, 44), (190, 300, 46, 52)]

    def bruma(img, tick, delante):
        """Dos bancos de niebla que se espesan y se abren (cuatro grados de espesor que van y vuelven)."""
        if delante: return
        paso = (tick // (L // 8)) % 8
        grado = (0, 1, 2, 3, 3, 2, 1, 0)[paso]
        for j, (xa, xb, ya, yb) in enumerate(bancos):
            g_ = (grado + 2 * j) % 4
            d_ = den[ya:yb, xa:xb].copy()
            xs = np.arange(xb - xa, dtype=np.float32)[None, :]
            ys = np.arange(yb - ya, dtype=np.float32)[:, None]
            f = (np.clip(1 - np.abs(xs / (xb - xa) * 2 - 1), 0, 1) * np.clip(1 - np.abs(ys / (yb - ya) * 2 - 1), 0, 1)) ** 0.7
            d_ = d_ * (1 - f * 0.18 * g_) + (0.0 if not o else 0.35) * f * 0.18 * g_
            pedazo = imprimir(d_, tin[ya:yb, xa:xb], ac[ya:yb, xa:xb], o, ya, yb, xa, xb)
            img.paste(Image.fromarray(pedazo).resize(((xb - xa) * ESC, (yb - ya) * ESC), Image.NEAREST), (xa * ESC, ya * ESC))

    def tentaculo(img, tick, delante):
        """A ratos, un tentáculo sale del agua entre las rocas, se enrosca y se hunde."""
        if delante: return
        f = (tick % L) / L
        if not (0.45 < f < 0.85): return
        u = (f - 0.45) / 0.4
        alto = 22 * math.sin(math.pi * u) ** 0.7
        xa, xb, ya, yb = 196, 226, 30, 64
        capa = Image.new("L", (xb - xa, yb - ya), 0); dc = ImageDraw.Draw(capa)
        bx, by = 210 - xa, 62 - ya
        pts = []
        for k in range(14):
            q = k / 13
            yy = by - alto * q
            xx = bx + 3 * math.sin(q * 3 + u * 4) * q + (6 * q ** 3 * math.cos(u * 3))
            pts.append((xx, yy))
        for k, (xx, yy) in enumerate(pts):
            r = 2.6 * (1 - k / 16)
            dc.ellipse([xx - r, yy - r, xx + r, yy + r], fill=255)
        if alto > 10:
            xx, yy = pts[-1]
            dc.arc([xx - 4, yy - 5, xx + 1, yy], 270, 120, fill=255, width=1)
        m = np.asarray(capa, np.float32) / 255
        d_ = den[ya:yb, xa:xb].copy()
        d_ = np.where(m > 0, 0.97, d_)
        t_ = tin[ya:yb, xa:xb].copy(); t_[m > 0] = 1
        a_ = ac[ya:yb, xa:xb].copy()
        for k, (xx, yy) in enumerate(pts[2:-3:2]):
            xi, yi = int(xx + 1), int(yy)
            if 0 <= yi < yb - ya and 0 <= xi < xb - xa and m[yi, xi] > 0:
                a_[yi, xi] = hx("7DBBA4") + (255,)
        pedazo = imprimir(d_, t_, a_, o, ya, yb, xa, xb)
        img.paste(Image.fromarray(pedazo).resize(((xb - xa) * ESC, (yb - ya) * ESC), Image.NEAREST), (xa * ESC, ya * ESC))

    rnd2 = random.Random(5)
    gaviotas = [(rnd2.uniform(0, W), rnd2.uniform(10, 26), rnd2.random()) for _ in range(2)]

    def aves(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        for x0, y0, f0 in gaviotas:
            x = round((x0 + tick * (W + 20) / L) % (W + 20) - 10)
            y = round(y0 + 2 * math.sin(2 * math.pi * (tick / 64 + f0)))
            ala = (tick // 4) % 2
            for dx in (-3, -2, -1, 1, 2, 3):
                cuad(dd, x + dx, y - (1 if abs(dx) == 2 else 0) - (ala if abs(dx) == 3 else 0), tinta)
            cuad(dd, x, y, tinta)

    g9.ANIM[("arcano", o)] = [haz, bruma] + ([tentaculo] if o else [aves])
    return big


g8.FONDOS["arcano"] = fondo_arcano


# ---------------------------------------------------------------------
#  Abismo. De día, la zona iluminada: haces de sol, un arrecife con corales, algas que se mecen, un ancla medio enterrada,
#  peces en cardumen y burbujas. De noche, la zona abisal: casi negro, nieve marina, y la única luz es la de los animales
#  (medusas que laten, gusanos y plumas de mar). Todo lo que se mueve va en pasos de 1 dp (pocos mosaicos distintos).
# ---------------------------------------------------------------------

def alga(px, x, ytop, ybase, verdes, fase=0.0, mueve=0.0):
    """Un tallo de kelp con hojas alternas; la punta se mece «mueve» dp."""
    for y in range(ytop, ybase):
        f = (ybase - y) / (ybase - ytop)
        xx = round(x + 1.5 * math.sin(y * 0.15 + fase) + mueve * f ** 2)
        _pon(px, xx, y, verdes[1])
        if y % 4 == 0:
            lado = 1 if (y // 4) % 2 else -1
            for k in range(1, 4):
                _pon(px, xx + lado * k, y - (k // 2), verdes[2 if k < 3 else 0])
        if y % 4 == 2:
            _pon(px, xx + 1, y, verdes[0])
    _pon(px, round(x + 1.5 * math.sin(ytop * 0.15 + fase) + mueve), ytop - 1, verdes[2])


def coral_rama(d, x, y, alto, col, col2, rnd, n=3):
    def rama(x0, y0, ang, l, w):
        if l < 2: return
        x1, y1 = x0 + l * math.cos(ang), y0 - l * math.sin(ang)
        d.line([x0, y0, x1, y1], fill=col, width=w)
        d.point((round(x1), round(y1)), fill=col2)
        for da in (-0.5, 0.45):
            if rnd.random() < 0.85: rama(x1, y1, ang + da + rnd.uniform(-0.15, 0.15), l * 0.66, max(1, w - 1))
    for k in range(n):
        rama(x + k * 3 - n, y, math.pi / 2 + rnd.uniform(-0.4, 0.4), alto, 2)


def medusa_parche(col, estado, fondo):
    """Una medusa de 23×30 dp en uno de 4 estados del latido: la campana se cierra y se abre, los tentáculos ondulan."""
    w_, h_ = 23, 30
    im = Image.new("RGB", (w_, h_), fondo); px = im.load()
    late = (0.0, 0.5, 1.0, 0.5)[estado]
    cx, cy = 11, 9
    ancho, alto = 8.6 - 2.2 * late, 5.6 + 1.3 * late
    luz(im, cx, cy + 3, 11, col, 0.18, 3)
    for y in range(h_):
        for x in range(w_):
            u, v = (x + 0.5 - cx) / ancho, (y + 0.5 - cy) / alto
            if v <= 0.35 and u * u + v * v <= 1 and not (v > 0.05 and abs(u) < 0.6):
                orilla = u * u + v * v > 0.6
                px[x, y] = mezcla(fondo, col, 0.95 if orilla else 0.42)
    for k in range(5):
        tx = cx - ancho * 0.7 + ancho * 0.35 * k
        for j in range(16):
            y = round(cy + 2 + j * (1.15 + 0.25 * late))
            x = round(tx + 1.4 * math.sin(j * 0.6 + estado * 1.57 + k))
            if 0 <= x < w_ and 0 <= y < h_:
                px[x, y] = mezcla(fondo, col, 0.62 * (1 - j / 18))
    px[cx, cy - 2] = mezcla(col, (255, 255, 255), 0.55)
    return im


def fondo_abismo(t):
    o = t["oscuro"]
    rnd = random.Random(1934)
    if not o:
        agua = [hx("CBE9F3"), hx("A8D8EA"), hx("83C3DE"), hx("62AECF"), hx("4A9BC2")]
        im, d = lienzo(agua[-1]); px = im.load()
        degradado(im, 3, 72, agua)
        for x in range(W):                                                               # la superficie, vista desde abajo
            y = round(1.2 + math.sin(x * 0.21) + 0.6 * math.sin(x * 0.53))
            for yy in range(0, max(0, y) + 1): px[x, yy] = hx("EAF7FB")
            _pon(px, x, y + 1, hx("FFFFFF") if (x // 3) % 4 == 0 else hx("D8F0F7"))
        for k, x0 in enumerate((30, 120, 214, 300, 380)):                                # haces de sol
            for y in range(3, 70):
                a_ = x0 + (y - 3) * 0.45; w_ = 6 + k % 2 * 4 + (y - 3) * 0.12
                fuerza = 0.2 * (1 - y / 75)
                for x in range(int(a_), int(a_ + w_)):
                    if 0 <= x < W and g9.tramar(0.6, x, y):
                        px[x, y] = mezcla(px[x, y], hx("FFFFFF"), fuerza)
        lejos, lejos2 = hx("6FB1CD"), hx("5DA2C2")                                       # arrecife lejano en la bruma azul
        for x in range(W):
            y = 52 + 6 * math.sin(x * 0.03) + 3 * math.sin(x * 0.11 + 1)
            for yy in range(int(y), 72): px[x, yy] = lejos if yy > y + 1 or (x % 5) else lejos2
        for x in range(10, W, 23):
            alga(px, x, 44 + rnd.randint(0, 6), 60, (lejos2, hx("5A9EBE"), hx("6AA9C6")), rnd.uniform(0, 6))
        # La roca del arrecife, a la izquierda, con corales.
        d.polygon([(0, 74), (0, 40), (10, 34), (26, 38), (38, 48), (52, 60), (60, 74)], fill=hx("4F7E8E"))
        d.polygon([(0, 74), (0, 52), (16, 48), (30, 54), (48, 66), (54, 74)], fill=hx("3F6B7A"))
        for _ in range(60):
            x, y = rnd.randint(0, 50), rnd.randint(38, 72)
            if px[x, y] in (hx("4F7E8E"), hx("3F6B7A")): px[x, y] = rnd.choice((hx("6A9BA8"), hx("35606E"), hx("C0563F")))
        coral_rama(d, 20, 40, 7, hx("C0563F"), hx("E58A6E"), rnd)
        coral_rama(d, 40, 54, 6, hx("E8A0B4"), hx("FFD2DC"), rnd, 2)
        # Corales al centro: cerebro, ramas, abanico de mar y una anémona.
        coral_rama(d, 168, 72, 9, hx("C0563F"), hx("F0A07E"), rnd, 4)
        d.ellipse([178, 64, 194, 74], fill=hx("D9A25E")); d.arc([180, 66, 192, 74], 180, 360, fill=hx("B07A3A"))
        for k in range(3): d.arc([181 + k * 3, 65, 186 + k * 3, 70], 200, 340, fill=hx("B07A3A"))
        for k in range(9):                                                               # abanico de mar
            a_ = math.pi * (0.2 + 0.6 * k / 8)
            d.line([200, 73, 200 + 12 * math.cos(a_), 73 - 13 * math.sin(a_)], fill=hx("8E4A86"))
        d.arc([188, 60, 212, 86], 200, 340, fill=hx("B0609E"))
        d.rectangle([150, 70, 156, 73], fill=hx("7A3A5A"))
        for k in range(7): d.line([151 + k, 70, 150 + k + (k - 3) * 0.6, 65 - abs(k - 3) % 2], fill=hx("F07AA0"))
        # Ancla medio enterrada, con la cadena.
        ax = 262
        d.line([ax, 52, ax, 72], fill=hx("5E4A2A"), width=2)
        d.ellipse([ax - 2, 48, ax + 3, 53], outline=hx("5E4A2A"))
        d.line([ax - 6, 56, ax + 7, 56], fill=hx("5E4A2A"), width=2)
        d.arc([ax - 10, 62, ax + 11, 76], 20, 160, fill=hx("5E4A2A"), width=2)
        for k in range(10):
            d.ellipse([ax + 3 + k * 4, 70 + (k % 2), ax + 6 + k * 4, 72 + (k % 2)], outline=hx("6E5A3A"))
        for _ in range(16): _pon(px, ax + rnd.randint(-8, 8), rnd.randint(50, 72), hx("8C6A1F"))
        # El fondo de arena: ondas, conchas, una estrella de mar y manchas de luz.
        degradado(im, 70, H, [hx("D8CEA6"), hx("CFC398"), hx("C2B68A")])
        for y in range(71, H, 3):
            for x in range(W):
                if math.sin(x * 0.35 + y) > 0.6: _pon(px, x, y, hx("B8AC80"))
        for _ in range(90):
            x, y = rnd.randrange(W), rnd.randint(70, 79)
            _pon(px, x, y, rnd.choice((hx("EAE2C2"), hx("F2ECD6"))))
        for x in (64, 214, 340):
            d.polygon([(x, 77), (x + 2, 75), (x + 4, 77), (x + 2, 78)], fill=hx("F0E6D0")); _pon(px, x + 2, 76, hx("C9B8A0"))
        sx = 120
        for k in range(5):
            a_ = -math.pi / 2 + 2 * math.pi * k / 5
            d.line([sx, 76, sx + 3 * math.cos(a_), 76 + 2 * math.sin(a_)], fill=hx("E07A4A"))
        d.line([0, 70, W, 70], fill=hx("E2D9B4"))
        # Algas cercanas (se mecen) y su lista para animarlas.
        algas = [(372, 10, 74, hx("4E6E2E"), 0.4), (392, 18, 74, hx("5A7A3A"), 1.6), (78, 22, 74, hx("4E6E2E"), 2.4), (334, 30, 74, hx("5A7A3A"), 3.3)]
        verdes = lambda c: (mezcla(c, (0, 0, 0), 0.25), c, mezcla(c, hx("C9E06A"), 0.35))
        base = im.copy()
        for x, yt, yb, c, f in algas:
            alga(px, x, yt, yb, verdes(c), f)
        big = subir(im)
        bp = base.load()
        L = 160
        peces = [(j, rnd.uniform(-1, 1), rnd.uniform(-1, 1)) for j in range(8)]

        def vivo(img, tick, delante):
            if delante: return
            dd = ImageDraw.Draw(img)
            # Algas: la punta se mece de -2 a 2 dp (5 posiciones).
            m = round(2 * math.sin(2 * math.pi * tick / 80))
            for x, yt, yb, c, f in algas:
                x0_, x1_, y0_ = x - 8, x + 12, yt - 2
                parche = base.crop((x0_, y0_, x1_, yb))
                pp = parche.load()
                for y in range(yt, yb):
                    ff = (yb - y) / (yb - yt)
                    xx = round(x + 1.5 * math.sin(y * 0.15 + f) + m * ff ** 2) - x0_
                    v = verdes(c)
                    if 0 <= xx < x1_ - x0_: pp[xx, y - y0_] = v[1]
                    if y % 4 == 0:
                        lado = 1 if (y // 4) % 2 else -1
                        for k in range(1, 4):
                            if 0 <= xx + lado * k < x1_ - x0_: pp[xx + lado * k, y - (k // 2) - y0_] = v[2 if k < 3 else 0]
                    if y % 4 == 2 and xx + 1 < x1_ - x0_: pp[xx + 1, y - y0_] = v[0]
                img.paste(parche.resize(((x1_ - x0_) * ESC, (yb - y0_) * ESC), Image.NEAREST), (x0_ * ESC, y0_ * ESC))
            # Cardumen: da vueltas en una elipse, todos miran hacia donde nadan.
            for j, ex, ey in peces:
                a_ = 2 * math.pi * (tick / L + j * 0.035)
                x = round(150 + 70 * math.cos(a_) + 5 * ex); y = round(24 + 6 * math.sin(a_) + 3 * ey)
                lado = -1 if math.sin(a_) > 0 else 1
                cuerpo, panza, aleta = hx("2E6E92"), hx("D6EEF6"), hx("F2C94C")
                cuad(dd, x, y, cuerpo, 5, 1); cuad(dd, x + 1, y - 1, cuerpo, 3, 1); cuad(dd, x, y + 1, panza, 5, 1)
                cola = x - 1 if lado > 0 else x + 5
                cuad(dd, cola, y - 1, aleta, 1, 3)
                cuad(dd, x + (3 if lado > 0 else 1), y, hx("0B2536"))
            # Burbujas que suben desde la arena, junto al ancla.
            for k in range(4):
                f = ((tick + k * 10) % 40) / 40
                y = round(70 - f * 66); x = 284 + round(math.sin(f * 9 + k))
                cuad(dd, x, y, hx("F2FBFD")); cuad(dd, x + 1, y + 1, hx("A8D8EA"))
                if k % 2: cuad(dd, x - 1, y, hx("A8D8EA"))
        g9.ANIM[("abismo", False)] = [vivo]
        return big
    # --- De noche: la zona abisal.
    fondo = hx("02080E")
    im, d = lienzo(fondo); px = im.load()
    for x in range(W):                                                                    # dorsal lejana, apenas
        y = 54 + 5 * math.sin(x * 0.025 + 2) + 2 * math.sin(x * 0.09)
        for yy in range(int(y), 74): px[x, yy] = hx("061320")
    for _ in range(140):                                                                  # nieve marina quieta
        x, y = rnd.randrange(W), rnd.randrange(0, 70)
        px[x, y] = rnd.choice((hx("0E2230"), hx("163347"), hx("0A1A26")))
    # La chimenea hidrotermal (a la derecha): roca negra, un brillo naranja apagado en la boca.
    d.polygon([(318, 74), (322, 52), (326, 46), (332, 46), (336, 54), (342, 74)], fill=hx("0A1620"))
    d.line([(326, 46), (322, 56), (321, 74)], fill=hx("102434"))
    luz(im, 329, 46, 5, hx("8A3A1A"), 0.35, 2)
    # Fondo de limo con gusanos de tubo y plumas de mar que brillan.
    d.rectangle([0, 72, W, H], fill=hx("071521"))
    d.line([0, 72, W, 72], fill=hx("163347"))
    for _ in range(80): _pon(px, rnd.randrange(W), rnd.randint(73, 79), hx("0C2030"))
    for x in (40, 96, 150, 212, 288, 360, 398):
        for k in range(3):
            xx = x + k * 3
            d.line([xx, 72, xx, 68 - k % 2], fill=hx("123040"))
            _pon(px, xx, 67 - k % 2, hx("2E9C88")); _pon(px, xx, 66 - k % 2, hx("5CF2C6"))
    for x in (122, 250, 306):                                                             # plumas de mar
        d.line([x, 72, x, 62], fill=hx("1A2A50"))
        for y in range(62, 70, 2):
            _pon(px, x - 1, y, hx("3A6AB0")); _pon(px, x + 1, y + 1, hx("3A6AB0"))
        _pon(px, x, 61, hx("8FC8FF"))
    big = subir(im)
    medusas = [(48, 8, hx("5CF2C6"), 0), (166, 1, hx("E29BFF"), 1), (258, 14, hx("3FA9FF"), 2), (368, 4, hx("5CF2C6"), 3)]
    parches = {(j, e): medusa_parche(c, e, fondo).resize((23 * ESC, 30 * ESC), Image.NEAREST)
               for j, (_, _, c, _) in enumerate(medusas) for e in range(4)}
    rnd2 = random.Random(7)
    nieve = [(rnd2.randrange(W), rnd2.randrange(80), rnd2.choice((hx("163347"), hx("1E4458")))) for _ in range(26)]
    destellos = [(rnd2.randrange(W), rnd2.randrange(4, 66), rnd2.choice((hx("5CF2C6"), hx("3FA9FF"), hx("E29BFF")))) for _ in range(18)]
    L = 160

    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        for x, y0, c in nieve:                                                            # nieve marina que baja
            y = (y0 + tick // 2) % 80
            cuad(dd, x, y, c)
        for j, (x, y, c, f) in enumerate(medusas):                                       # medusas que laten
            e = (tick // 5 + f) % 4
            img.paste(parches[(j, e)], (x * ESC, y * ESC))
        for j, (x, y, c) in enumerate(destellos):                                         # plancton que destella
            if (j * 7 + tick // 4) % 10 == 0:
                cuad(dd, x, y, c)
            elif (j * 7 + tick // 4) % 10 == 1:
                cuad(dd, x, y, mezcla(c, fondo, 0.6))
    g9.ANIM[("abismo", True)] = [vivo]
    return big


g8.FONDOS["abismo"] = fondo_abismo


# =====================================================================
#  Revisión: hojas de las acciones, hojas completas (claro y oscuro) y bandas en GIF; todo queda en salida/
# =====================================================================

def hoja_mia(nombre, oscuro, cel=3):
    """Como g8.hoja, pero también sabe pegar los sprites que se suman como luz (Abismo de noche)."""
    t = g8.tema(nombre, oscuro)
    lado = N * cel; sep = 10; izq = 170
    filas = g8.FILAS_HOJA
    img = Image.new("RGB", (izq + 8 * (lado + sep) + 10, 50 + len(filas) * (lado + 12)), t["fondo"])
    d = ImageDraw.Draw(img)
    d.text((12, 12), f"{g9.ETIQ[nombre].split(' · ')[0]} · {'oscuro' if oscuro else 'claro'} · ×{cel}", font=fuente("DejaVuSans-Bold.ttf", 18),
           fill=t["texto"])
    for fi, (et, n) in enumerate(filas):
        y = 50 + fi * (lado + 12)
        d.text((12, y + lado // 2 - 8), et, font=fuente("DejaVuSans.ttf", 14), fill=t["texto"])
        for k in range(n):
            x = izq + k * (lado + sep)
            s_ = g8.sprite(nombre, oscuro, t, g8.CLAVE[et], k, False)
            if s_.mode == "RGB":
                img.paste(ImageChops.add(img.crop((x, y, x + lado, y + lado)), s_.resize((lado, lado))), (x, y))
            else:
                s_ = s_.resize((lado, lado)); img.paste(s_, (x, y), s_)
    img.save(AQUI / f"hoja_{nombre}_{'oscuro' if oscuro else 'claro'}.png")


if __name__ == "__main__":
    que = sys.argv[1:] or ["hojas", "bandas"]
    if "hojas" in que:
        viejas = list(g8.BANDAS)
        for o in (False, True):
            g8.BANDAS[:] = [(n, o, e) for n, _, e in viejas if n in MIOS]
            g17.hojas("animaciones_9b_c" + ("_oscuro" if o else ""), por_hoja=3)
            for n in MIOS:
                hoja_mia(n, o)
        g8.BANDAS[:] = viejas
    if "bandas" in que:
        for n in MIOS:
            cs = g17.secuencia(n, 14.0)
            g8.gif_banda(cs, [(n, False, g9.ETIQ[n])], "_especiales")
            g8.gif_banda(cs, [(n, True, g9.ETIQ[n].split(" · ")[0] + " · de noche: " + g9.NOCHE[n])], "_especiales_oscuro")
    print("listo")
