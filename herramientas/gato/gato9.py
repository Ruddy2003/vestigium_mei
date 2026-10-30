"""
Prueba 9: fondos nuevos (claro y oscuro) para distopía, laboratorio, refugio, colonos, revolución, napoleónico,
soviético, mongol, Tierras Antiguas y helénico. El gato y los demás temas vienen de la prueba 8 (gato8.py).
Colores de ui/Disenios.kt (esquemas claro/oscuro y juegoTonos) y ui/Tema.kt (heleno).
Los fondos se pintan a 1 pixel por dp (igual que el gato) y se amplían ×3 sin suavizar.
"""
import math
import random
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageChops, ImageDraw, ImageFilter

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8                                     # noqa: E402
from gato8 import hx, mezcla, esc, fuente, ESC          # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
g8.AQUI = AQUI
W, H = g8.ANCHO_DP, g8.ALTO_DP
NUEVOS = ["heleno", "mongol", "colonos", "revolucion", "napoleon", "laboratorio", "refugio", "distopia", "urss", "antiguas"]
ANIM = {}        # (tema, oscuro) -> [f(img, tick, delante)]: lo que se mueve en el fondo

# =====================================================================
#  Herramientas de pixel art (a 1 pixel por dp)
# =====================================================================
B4 = (np.array([[0, 8, 2, 10], [12, 4, 14, 6], [3, 11, 1, 9], [15, 7, 13, 5]]) + 0.5) / 16


def lienzo(col):
    im = Image.new("RGB", (W, H), col)
    return im, ImageDraw.Draw(im)


def tramar(f, x, y):
    return f > B4[y % 4][x % 4]


def degradado(im, y0, y1, cols, x0=0, x1=W):
    """Degradado vertical con tramado ordenado (sin colores intermedios, como en pixel art)."""
    px = im.load(); n = len(cols) - 1
    for y in range(max(0, y0), min(H, y1)):
        t = (y - y0) / max(1, y1 - y0 - 1) * n
        i = min(int(t), n - 1); f = t - i
        for x in range(max(0, x0), min(W, x1)):
            px[x, y] = cols[i + 1] if tramar(f, x, y) else cols[i]


def ondas(semilla, base, comps):
    """Perfil de colinas: suma de senos (amplitud, largo de onda) con fase al azar."""
    rnd = random.Random(semilla)
    cs = [(a, 2 * math.pi / l * rnd.uniform(0.8, 1.25), rnd.uniform(0, 6.3)) for a, l in comps]
    return [base + sum(a * math.sin(w * x + p) for a, w, p in cs) for x in range(W)]


def perfil(im, ys, col, hasta=H, borde=None, x0=0, x1=W):
    px = im.load()
    for x in range(max(0, x0), min(W, x1)):
        top = round(ys[x])
        for y in range(max(0, top), hasta):
            px[x, y] = col
        if borde is not None and 0 <= top < H:
            px[x, top] = borde


def luz(im, cx, cy, r, col, fuerza=0.5, pasos=4, ry=None, x0=0, x1=W, y1=H):
    """Halo en anillos (sin degradado continuo): mezcla cada pixel hacia el color de la luz."""
    px = im.load(); ry = ry or r
    for y in range(max(0, int(cy - ry)), min(y1, int(cy + ry) + 1)):
        for x in range(max(x0, int(cx - r)), min(x1, int(cx + r) + 1)):
            dd = ((x - cx) / r) ** 2 + ((y - cy) / ry) ** 2
            if dd < 1:
                k = math.ceil((1 - math.sqrt(dd)) * pasos) / pasos * fuerza
                px[x, y] = mezcla(px[x, y], col, k)


def estrellas(im, semilla, n, y0, y1, cols, x0=0, x1=W, evitar=None):
    rnd = random.Random(semilla); px = im.load(); out = []
    for _ in range(n):
        x, y = rnd.randrange(x0, x1), rnd.randrange(y0, y1)
        if evitar and evitar(x, y): continue
        px[x, y] = rnd.choice(cols); out.append((x, y))
    return out


def nube(d, x, y, w, claro, medio, sombra):
    """Nube de pixel art: bultos redondos con la base plana y sombreada."""
    rnd = random.Random(x * 7 + y)
    bultos = [(x + k, y - rnd.randint(1, 3) - (2 if 0.25 < k / w < 0.7 else 0), rnd.randint(3, 5)) for k in range(2, w - 2, 4)]
    for bx, by, r in bultos:
        d.ellipse([bx - r, by - r, bx + r, by + r], fill=medio)
    for bx, by, r in bultos:
        d.ellipse([bx - r + 1, by - r, bx + r - 2, by + r - 2], fill=claro)
    d.rectangle([x, y, x + w, y + 2], fill=medio)
    d.line([x + 1, y + 2, x + w - 1, y + 2], fill=sombra)


def humo(img, tick, x, y, col, fondo, n=7, sube=0.16, ancho=1.0, semilla=0, alto=22):
    """Hilo de humo que sube, se ensancha y se deshace en el fondo (se dibuja a la escala de la banda)."""
    d = ImageDraw.Draw(img)
    for k in range(n):
        f = ((tick * sube + k * alto / n + semilla) % alto) / alto
        if f > 0.85: continue
        yy = y - f * alto; xx = x + math.sin(f * 4 + semilla) * 1.6 * ancho + f * 6
        w = 1 + round(f * 2.5)
        cuad(d, xx - w / 2, yy, mezcla(col, fondo, min(0.8, f * 0.9)), w, 1)


def cuad(d, x, y, col, w=1, h=1):
    d.rectangle([round(x * ESC), round(y * ESC), round((x + w) * ESC) - 1, round((y + h) * ESC) - 1], fill=col)


def llama(d, x, y, tick, s=1.0, cols=(hx("FFF1B0"), hx("FFC24A"), hx("F0762B"), hx("B5361C")), semilla=0):
    """Fuego de pixel art en 4 tonos; cambia de forma cada cuadro."""
    rnd = random.Random(tick // 2 * 31 + semilla)
    alto = (5 + rnd.randint(0, 2)) * s
    for j, col in enumerate(reversed(cols)):
        k = 1 - j * 0.24
        a = alto * k; w = 2.4 * s * k
        lean = rnd.uniform(-0.8, 0.8) * s
        pts = [(x - w, y), (x - w * 0.7, y - a * 0.5), (x + lean, y - a), (x + w * 0.7, y - a * 0.45), (x + w, y)]
        d.polygon([(px_ * ESC, py_ * ESC) for px_, py_ in pts], fill=col)


def subir(im):
    return im.resize((W * ESC, H * ESC), Image.NEAREST)


def columna(im, x, y0, y1, w, tonos, estrias=True):
    """Fuste cilíndrico sombreado (luz desde la izquierda) con estrías."""
    px = im.load(); n = len(tonos)
    perfil_ = [0.55, 0.8, 0.95, 1.0, 0.92, 0.8, 0.66, 0.5, 0.36, 0.24, 0.14, 0.06]
    for i in range(w):
        f = perfil_[min(len(perfil_) - 1, int(i / w * len(perfil_)))]
        col = tonos[min(n - 1, int(f * n))]
        if estrias and i % 3 == 2 and 0 < i < w - 1:
            col = tonos[max(0, min(n - 1, int(f * n)) - 1)]
        for y in range(y0, y1):
            if 0 <= x + i < W and 0 <= y < H:
                px[x + i, y] = col


# =====================================================================
#  Banda: el fondo, lo que se mueve, la sombra y el gato
# =====================================================================
_cuadro8 = g8.cuadro_banda
_vin = {}


def viñeta():
    if "v" not in _vin:
        _vin["v"] = g8.viñeta_ciber()
    return _vin["v"]


def cuadro_banda(nombre, oscuro, t, fondo, c, tick, extra):
    if nombre not in NUEVOS:
        return _cuadro8(nombre, oscuro, t, fondo, c, tick, extra)
    anim, i, x, dirn, dy = c
    if t.get("ajustar"):
        x, dy = round(x), round(dy)
    img = fondo.copy()
    for f in ANIM.get((nombre, oscuro), []):
        f(img, tick, False)
    cx = (x + (31 if dirn > 0 else 33)) * ESC
    k = 1 + dy / 14
    caja = [cx - 19 * ESC * k, (g8.Y_SUELO + 0.4) * ESC - 1.3 * ESC * k, cx + 19 * ESC * k, (g8.Y_SUELO + 0.4) * ESC + 1.3 * ESC * k]
    spr = g8.sprite(nombre, oscuro, t, anim, i, dirn < 0)
    px_, py_ = int(round(x * ESC)), int(round((g8.Y_SUELO - 60 + dy) * ESC))
    if t.get("pantalla"):
        capa = Image.new("RGB", img.size, (0, 0, 0)); capa.paste(spr, (px_, py_))
        ImageDraw.Draw(capa).ellipse(caja, fill=mezcla(t["reflejo"], (0, 0, 0), 0.8))
        img = ImageChops.add(img, capa)
    else:
        if t.get("sombra_a", 55):
            capa = Image.new("RGBA", img.size, (0, 0, 0, 0))
            ImageDraw.Draw(capa).ellipse(caja, fill=t.get("sombra_col", (20, 20, 24)) + (t.get("sombra_a", 55),))
            img = Image.alpha_composite(img.convert("RGBA"), capa).convert("RGB")
        img.paste(spr, (px_, py_), spr)
    for f in ANIM.get((nombre, oscuro), []):
        f(img, tick, True)
    if t.get("pantalla"):
        img = ImageChops.multiply(g8.lineas_escaneo(img), viñeta())
    return img


g8.cuadro_banda = cuadro_banda


# =====================================================================
#  Temas: el gato de cada tema, de día y de noche
# =====================================================================
_tema8 = g8.tema


def de_noche(col, tinte, k=0.12):
    """De noche el gato se queda claro (es el protagonista), pero toma un poco del color de la escena."""
    return {c: (v if c in "EHNRG" else mezcla(v, tinte, k)) for c, v in col.items()}


def tema(nombre, oscuro=False):
    if nombre not in NUEVOS:
        t = _tema8(nombre, oscuro)
        return dict(t, oscuro=oscuro)
    if nombre == "heleno":
        # helenoClaro: púrpura 5A2A82, azul 1E5F8C, oro A27B22, tinta 1E1A2B · helenoOscuro: oro D8B45C, lila B992EA, azul 73B4E2
        P = mezcla(hx("F3EFE6"), hx("C9A35A"), 0.45)
        pur = hx("5A2A82") if not oscuro else mezcla(hx("5A2A82"), hx("B992EA"), 0.3)
        col = g8.pelaje(P, hx("1E1A2B"), W=mezcla(P, hx("8A6A2E"), 0.45), L=hx("FBF8F1"), E=hx("1E5F8C") if not oscuro else hx("73B4E2"),
                        A=pur, B=mezcla(pur, hx("1E1A2B"), 0.45), G=hx("D8B45C"), M=mezcla(hx("A27B22"), hx("F2D27A"), 0.45 if not oscuro else 0.6),
                        C=pur, J=pur)
        if oscuro: col = de_noche(col, hx("2B2245"))
        t = g8.T(col, hx("F3EFE6") if not oscuro else hx("0E0B17"), hx("1E1A2B"), hx("D3C8B3"), "mechones", "plano", ("capa", "laurel"))
    else:
        t = dict(_tema8(nombre, False))
        col = dict(t["col"])
        noche = dict(mongol=hx("18263F"), colonos=hx("1E2838"), revolucion=hx("25282F"), napoleon=hx("1B2550"), laboratorio=hx("18222F"),
                     antiguas=hx("2A2B2F"))
        if oscuro and nombre in noche:
            t["col"] = de_noche(col, noche[nombre])
        if nombre == "distopia" and oscuro:
            # Serigrafía en negativo: papel negro, tinta crema y rojo (distopiaOscuro: 0F1011, E8E6E0, E0262F).
            # Los colores del gato son «cuánta tinta lleva» (gris = trama), así que lo claro del gato es lo oscuro aquí.
            g_ = lambda v: (v, v, v)
            t["col"] = g8.pelaje(g_(150), g_(10), S=g_(182), W=g_(150), L=g_(112), D=g_(186), T=g_(120), E=hx("E0262F"), N=hx("E0262F"),
                                 R=g_(150), C=g_(10), G=hx("E0262F"), J=hx("E0262F"))
            t["tintas"] = dict(papel=hx("141516"), negro=hx("E8E6E0"), rojo=hx("E0262F"))
        if nombre == "urss" and oscuro:
            g_ = lambda v: (v, v, v)
            t["col"] = g8.pelaje(g_(140), g_(10), S=g_(180), W=g_(140), L=g_(100), D=g_(186), T=g_(118), E=g_(10), N=hx("E5252F"),
                                 R=g_(140), A=hx("E5252F"), C=hx("E5252F"), J=hx("E5252F"))
            t["tintas"] = dict(papel=hx("161212"), negro=hx("E8D9B5"), rojo=hx("E5252F"))
        if nombre == "refugio" and oscuro:
            # Pantalla de fósforo verde (refugioOscuro: 070D08, 3DFF7F, 9CF5B0, 1E8A45): un solo color en varias intensidades.
            v = lambda k: mezcla((0, 0, 0), hx("3DFF7F"), k)
            t["col"] = g8.pelaje(v(0.62), v(0.16), S=v(0.4), W=v(0.5), L=mezcla(hx("3DFF7F"), hx("D8FFE2"), 0.5), D=v(0.3), T=v(0.42),
                                 E=hx("D8FFE2"), N=v(0.85), R=v(0.62), C=v(0.95), A=v(0.3), B=v(0.95), G=v(1.0), J=v(0.9))
            t.update(material="fosforo", material_banda="fosforo", pantalla=True, reflejo=hx("3DFF7F"))
        t["fondo"] = {False: t["fondo"], True: noche.get(nombre, hx("101010"))}[oscuro]
    t["oscuro"] = oscuro
    t["ajustar"] = nombre in ("distopia", "urss")
    if t["material"] == "dos_tintas":
        t["sombra_a"] = 0
    if oscuro:
        t["sombra_col"] = (0, 0, 0); t["sombra_a"] = t.get("sombra_a", 70) and 70
    return t


g8.tema = tema


def pintar_fosforo(g, t, cel):
    """Refugio de noche: pixeles de fósforo verde redondeados que brillan (se suman a la pantalla)."""
    s = cel * g8.SS
    capa = Image.new("RGB", (g8.N * s, g8.N * s), (0, 0, 0)); d = ImageDraw.Draw(capa)
    for r, c, v in g8.celdas(g):
        d.rounded_rectangle([c * s + 0.08 * s, r * s + 0.08 * s, (c + 1) * s - 0.08 * s, (r + 1) * s - 0.08 * s], radius=0.3 * s, fill=t["col"][v])
    brillo = capa.filter(ImageFilter.GaussianBlur(s * 0.9))
    img = ImageChops.add(capa, ImageChops.multiply(brillo, Image.new("RGB", capa.size, (130, 130, 130))))
    return img.resize((g8.N * cel, g8.N * cel), Image.LANCZOS)


_pintar8 = g8.pintar


def pintar(g, t, cel, oscuro=False):
    if t["material"] == "fosforo":
        return pintar_fosforo(g, t, cel)
    return _pintar8(g, t, cel, oscuro)


g8.pintar = pintar


# =====================================================================
#  Helénico: un pórtico de columnas frente al Egeo. De noche, la luna sobre el mar y braseros encendidos.
# =====================================================================

def fondo_heleno(t):
    o = t["oscuro"]
    if not o:
        cielo = [hx("8FBCDD"), hx("B5D3E8"), hx("D9E8EF"), hx("EEF1EC")]
        mar, mar2, mar3 = hx("1E5F8C"), hx("2F7DB0"), hx("5FA3CF")
        islas, islas2 = hx("9DB0B8"), hx("B7C5C6")
        m = [hx("8E8473"), hx("B3A891"), hx("D3C8B3"), hx("EAE3D4"), hx("F7F3EA")]      # mármol, de sombra a luz
        pur, oro, tinta = hx("5A2A82"), hx("C9A04A"), hx("1E1A2B")
        piso, piso2 = hx("E4DBC8"), hx("CFC3AA")
    else:
        cielo = [hx("0E0B17"), hx("161222"), hx("211B33"), hx("2B2245")]
        mar, mar2, mar3 = hx("0B1B2C"), hx("13324A"), hx("2A5878")
        islas, islas2 = hx("120F1E"), hx("1A1529")
        m = [hx("1B1726"), hx("2A2440"), hx("3B3456"), hx("4E476B"), hx("625A80")]
        pur, oro, tinta = mezcla(hx("5A2A82"), hx("B992EA"), 0.25), hx("D8B45C"), hx("0A0812")
        piso, piso2 = hx("2A2440"), hx("211B33")
    im, d = lienzo(cielo[-1])
    hor = 42
    degradado(im, 12, hor, cielo)
    if o:
        estrellas(im, 3, 50, 13, hor - 6, [hx("EFE9DC"), hx("AAA2BF"), hx("D8B45C")])
        lx, ly = 300, 24                                      # luna
        luz(im, lx, ly, 14, hx("3B3456"), 0.6, 3)
        d.ellipse([lx - 5, ly - 5, lx + 5, ly + 5], fill=hx("EFE6CF")); d.ellipse([lx - 2, ly - 3, lx + 1, ly], fill=hx("D9CDB0"))
    # Islas lejanas con pueblos blancos.
    for x0, w, h in ((30, 70, 6), (190, 46, 4), (250, 90, 8)):
        d.polygon([(x0, hor), (x0 + w * 0.3, hor - h), (x0 + w * 0.6, hor - h + 2), (x0 + w, hor)], fill=islas)
        d.polygon([(x0 + w * 0.3, hor - h), (x0 + w * 0.6, hor - h + 2), (x0 + w * 0.55, hor)], fill=islas2)
        if not o:
            for k in range(int(w * 0.15)):
                xx = x0 + w * 0.35 + k * 2.2
                d.rectangle([xx, hor - 3, xx + 1, hor - 2], fill=hx("FBFAF5"))
                if k % 4 == 1: d.point((xx, hor - 4), fill=hx("2F7DB0"))
        else:
            for k in range(0, int(w * 0.15), 3):
                d.point((x0 + w * 0.35 + k * 2.2, hor - 2), fill=hx("F2C14E"))
    degradado(im, hor, 58, [mar3, mar2, mar2, mar])
    d.line([0, hor, W, hor], fill=mezcla(mar3, cielo[-1], 0.5))
    # Parapeto, piso de losas en perspectiva.
    d.rectangle([0, 56, W, 63], fill=m[3]); d.line([0, 56, W, 56], fill=m[4]); d.line([0, 63, W, 63], fill=m[1])
    for x in range(0, W, 24): d.line([x, 57, x, 62], fill=m[2])
    degradado(im, 64, H, [piso2, piso, piso])
    for k in range(-14, 16):
        d.line([206 + k * 9, 64, 206 + k * 40, H], fill=mezcla(piso, m[1], 0.45))
    for y in (66, 69, 73, 78):
        d.line([0, y, W, y], fill=mezcla(piso, m[1], 0.45))
    # Columnas dóricas.
    xs = [6, 74, 142, 258, 326, 394]
    for x in xs:
        columna(im, x, 15, 62, 12, m)
        d.rectangle([x - 2, 13, x + 13, 14], fill=m[3]); d.line([x - 2, 14, x + 13, 14], fill=m[1])
        d.rectangle([x - 1, 15, x + 12, 16], fill=m[2]); d.line([x - 1, 16, x + 12, 16], fill=m[0])
        d.rectangle([x - 2, 62, x + 13, 64], fill=m[3]); d.line([x - 2, 64, x + 13, 64], fill=m[1])
    # Entablamento: arquitrave, friso con greca de oro sobre púrpura, cornisa y dentículos.
    d.rectangle([0, 0, W, 12], fill=m[3])
    d.line([0, 0, W, 0], fill=m[4]); d.line([0, 3, W, 3], fill=m[2])
    d.rectangle([0, 4, W, 9], fill=pur)
    g8.greca_negra(d, 4, oro, pur)
    d.line([0, 10, W, 10], fill=m[4]); d.line([0, 11, W, 11], fill=m[1])
    for x in range(0, W, 4): d.rectangle([x, 12, x + 1, 12], fill=m[1])
    # Estandarte púrpura con el sol de Vergina, entre las columnas del centro.
    bx = 200
    d.rectangle([bx - 9, 13, bx + 9, 40], fill=pur)
    d.polygon([(bx - 9, 40), (bx, 45), (bx + 9, 40)], fill=pur)
    d.line([bx - 9, 13, bx - 9, 40], fill=mezcla(pur, tinta, 0.4)); d.line([bx + 9, 13, bx + 9, 40], fill=mezcla(pur, tinta, 0.4))
    d.line([bx - 10, 13, bx + 10, 13], fill=oro)
    for k in range(16):
        an = 2 * math.pi * k / 16; r = 8 if k % 2 == 0 else 6
        d.line([bx, 27, bx + r * math.cos(an), 27 + r * math.sin(an)], fill=oro)
    d.ellipse([bx - 2, 25, bx + 2, 29], fill=oro)
    for x in range(bx - 8, bx + 9, 2): d.point((x, 41 + (1 if abs(x - bx) < 5 else 0)), fill=oro)
    # Ánfora junto a una columna.
    ax = 104
    terr = hx("B5612E") if not o else hx("5A3424")
    d.ellipse([ax - 5, 49, ax + 5, 61], fill=terr); d.rectangle([ax - 2, 44, ax + 2, 50], fill=terr)
    d.rectangle([ax - 3, 43, ax + 3, 44], fill=terr); d.arc([ax - 6, 44, ax - 1, 52], 90, 270, fill=terr); d.arc([ax + 1, 44, ax + 6, 52], 270, 90, fill=terr)
    d.line([ax - 5, 54, ax + 5, 54], fill=hx("1E1410")); d.line([ax - 4, 56, ax + 4, 56], fill=hx("1E1410"))
    d.rectangle([ax - 1, 61, ax + 1, 63], fill=terr)
    if not o:
        d.line([ax - 3, 50, ax - 3, 53], fill=mezcla(terr, hx("FFFFFF"), 0.35))
    braseros = []
    if o:
        for bxx in (42, 364):
            d.polygon([(bxx - 5, 52), (bxx + 5, 52), (bxx + 3, 55), (bxx - 3, 55)], fill=hx("6B4A22"))
            d.line([bxx - 5, 52, bxx + 5, 52], fill=oro)
            for lx_ in (-3, 0, 3): d.line([bxx + lx_ * 0.6, 55, bxx + lx_, 63], fill=hx("4A3418"))
            luz(im, bxx, 66, 34, hx("F0A040"), 0.3, 4, ry=7)
            luz(im, bxx, 47, 14, hx("FFB45A"), 0.25, 3)
            braseros.append((bxx, 52))
    big = subir(im)
    # Lo que se mueve: brillos del mar (de noche, el camino de la luna) y el fuego de los braseros.
    rnd = random.Random(8)
    destellos = [(rnd.randrange(W), rnd.randrange(hor + 1, 56)) for _ in range(60)]
    def mar_vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        for j, (x, y) in enumerate(destellos):
            if any(xc <= x <= xc + 12 for xc in xs): continue
            if o and abs(x - 300) > 8 + (y - hor) * 0.9: continue
            if (j * 5 + tick // 3) % 7 < (3 if o else 2):
                cuad(dd, x, y, hx("FFFFFF") if not o else hx("EFE6CF"), 2 if o else 1)
    def fuego(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        for j, (x, y) in enumerate(braseros):
            llama(dd, x, y, tick, 1.0, semilla=j)
    ANIM[("heleno", o)] = [mar_vivo] + ([fuego] if o else [])
    return big


# =====================================================================
#  Mongol: la estepa bajo el Cielo Azul Eterno. De noche, la Vía Láctea, la fogata y la ger encendida.
# =====================================================================

def ger(d, x, y, w, pal, puerta_luz=False):
    """Ger (yurta) de fieltro: muro, techo cónico, corona, cuerdas y puerta roja."""
    blanco, sombra, rojo, oro, tinta, cuerda = pal
    h = w * 0.38
    d.rectangle([x - w / 2, y - h, x + w / 2, y], fill=blanco)
    d.rectangle([x + w * 0.18, y - h, x + w / 2, y], fill=sombra)
    d.polygon([(x - w / 2 - 1, y - h), (x - 2, y - h - w * 0.32), (x + 2, y - h - w * 0.32), (x + w / 2 + 1, y - h)], fill=blanco)
    d.polygon([(x + 2, y - h - w * 0.32), (x + w / 2 + 1, y - h), (x + w * 0.18, y - h)], fill=sombra)
    d.line([x - w / 2, y - h, x + w / 2, y - h], fill=rojo)
    d.line([x - w / 2, y - h + 2, x + w / 2, y - h + 2], fill=cuerda)
    d.line([x - w / 2, y - 2, x + w / 2, y - 2], fill=cuerda)
    d.rectangle([x - 2, y - h - w * 0.32 - 1, x + 2, y - h - w * 0.32], fill=oro)
    pw = max(3, w * 0.16)
    d.rectangle([x - pw / 2, y - h * 0.75, x + pw / 2, y], fill=oro if puerta_luz else rojo, outline=oro if not puerta_luz else rojo)
    d.line([x - w / 2, y, x + w / 2, y], fill=tinta)


def caballo(d, x, y, col, crin, mira=1, pastando=False):
    m = mira
    d.rectangle([x - 3, y - 5, x + 3, y - 3], fill=col)
    for lx in (-3, -1, 2, 3):
        d.line([x + lx * m, y - 3, x + lx * m, y], fill=col)
    if pastando:
        d.line([x + 3 * m, y - 4, x + 5 * m, y - 1], fill=col); d.point((x + 5 * m, y), fill=col)
    else:
        d.line([x + 3 * m, y - 5, x + 5 * m, y - 8], fill=col); d.line([x + 4 * m, y - 8, x + 6 * m, y - 7], fill=col)
        d.line([x + 3 * m, y - 6, x + 4 * m, y - 8], fill=crin)
    d.line([x - 4 * m, y - 5, x - 5 * m, y - 2], fill=crin)


def fondo_mongol(t):
    o = t["oscuro"]
    im, d = lienzo(hx("1F5FA8"))
    if not o:
        degradado(im, 0, 46, [hx("1A4F92"), hx("1F5FA8"), hx("3D7DC4"), hx("74A6DA"), hx("B4D0EC"), hx("E3ECF2")])
        for x, y, w in ((40, 14, 34), (150, 8, 46), (300, 18, 28), (360, 7, 30)):
            nube(d, x, y, w, hx("FFFFFF"), hx("E3ECF4"), hx("B9CCE2"))
        mont, mont2, nieve, nieve2 = hx("6E86B8"), hx("5B70A3"), hx("F4F6FA"), hx("C8D3EA")
        colinas = [(hx("9DB08A"), hx("B4C59C")), (hx("C6AE62"), hx("D8C27A")), (hx("8FA24C"), hx("A6B85E"))]
        pal_ger = (hx("F6F2E8"), hx("D9D2C1"), hx("C2412D"), hx("C48A1E"), hx("18202E"), hx("8A7A62"))
        pasto, pasto2 = hx("7D9440"), hx("6A7F36")
    else:
        degradado(im, 0, 50, [hx("05090F"), hx("0A1220"), hx("101B2E"), hx("18263F"), hx("22324F")])
        # Vía Láctea: una franja diagonal más clara con muchas estrellas.
        px = im.load()
        for y in range(0, 44):
            for x in range(W):
                dd = abs((x - 30) * 0.18 - y + 4) / 9
                if dd < 1 and tramar((1 - dd) * 0.55, x, y):
                    px[x, y] = mezcla(px[x, y], hx("6F84B8"), 0.35)
        rnd = random.Random(4)
        for _ in range(260):
            x, y = rnd.randrange(W), rnd.randrange(0, 44)
            cerca = abs((x - 30) * 0.18 - y + 4) < 9
            if cerca or rnd.random() < 0.4:
                px[x, y] = rnd.choice([hx("F0EBDD"), hx("AAB0BD"), hx("6FB1FF"), hx("F2C14E")]) if rnd.random() < 0.5 else hx("5A6A8A")
        mont, mont2, nieve, nieve2 = hx("14203A"), hx("101A30"), hx("4A5A7E"), hx("2B3E5E")
        colinas = [(hx("0F1A2A"), hx("15233A")), (hx("0E1824"), hx("142034")), (hx("0B131E"), hx("101B2A"))]
        pal_ger = (hx("4A5168"), hx("343A50"), hx("8A2E22"), hx("F2C14E"), hx("05080E"), hx("2A2F40"))
        pasto, pasto2 = hx("0D1520"), hx("0A111A")
    # Montañas del Altái con nieve.
    picos = [(10, 26), (60, 22), (118, 30), (176, 20), (238, 27), (300, 19), (352, 25), (408, 23)]
    ys = []
    for x in range(W):
        y = min(p[1] + abs(x - p[0]) * 0.55 for p in picos)
        ys.append(min(46, y))
    px = im.load()
    for x in range(W):
        top = round(ys[x]); izq = ys[x] < ys[x - 1] if x > 0 else False
        pico = min(picos, key=lambda p: p[1] + abs(x - p[0]) * 0.55)
        for y in range(top, 47):
            sol = x <= pico[0]
            if y - pico[1] < 5 + (2 if sol else 0) and y < 34:
                px[x, y] = nieve if sol else nieve2
            else:
                px[x, y] = mont if sol else mont2
    # Colinas de la estepa en tres capas.
    for k, (base, amps) in enumerate(((44, ((2, 90), (1.5, 40))), (52, ((3, 120), (1.2, 50))), (62, ((2.5, 150), (1, 60))))):
        perfil(im, ondas(11 + k, base, amps), colinas[k][0], borde=colinas[k][1])
    # Gers (dos cerca, una lejos), con su humo.
    gers = [(268, 51, 14), (322, 61, 24), (366, 62, 20)]
    for x, y, w in gers:
        ger(d, x, y, w, pal_ger, puerta_luz=o and w > 15)
    # Caballos.
    if not o:
        cols = [hx("7A4A2A"), hx("F1EDE3"), hx("2A2420"), hx("A0603A"), hx("C9B59A")]
    else:
        cols = [hx("1A2232")] * 5
    for j, (x, pasta) in enumerate(((64, True), (80, False), (95, True), (110, True), (126, False))):
        caballo(d, x, round(ondas(13, 62, ((2.5, 150), (1, 60)))[x]) + 3, cols[j], hx("1E1814") if not o else hx("0A0F18"), 1 if j % 2 else -1, pasta)
    # Ovoo: montículo de piedras con una vara y jadag azules.
    ox = 196; oy = 70
    for k, (dx, dy, r) in enumerate(((-6, -1, 3), (-2, -2, 3), (3, -1, 3), (6, 0, 2), (-1, -5, 3), (2, -6, 2), (0, -8, 2))):
        c_ = (hx("8A8478"), hx("A39C8E"), hx("6E695F"))[k % 3] if not o else (hx("1E2638"), hx("26304A"), hx("1A2030"))[k % 3]
        d.ellipse([ox + dx - r, oy + dy - r, ox + dx + r, oy + dy + r], fill=c_)
    d.line([ox, oy - 10, ox, oy - 26], fill=hx("6B4A2A") if not o else hx("2A2030"))
    # Pasto y suelo.
    if not o:
        degradado(im, 70, H, [hx("8FA24C"), pasto, pasto2])
    else:
        degradado(im, 70, H, [colinas[2][0], pasto, pasto2])
    rnd = random.Random(2)
    for x in range(0, W, 3):
        y = 72 + rnd.randint(0, 6)
        d.line([x, y, x + rnd.choice((-1, 1)), y - 2], fill=hx("B4C064") if not o else hx("18263F"))
        if not o and rnd.random() < 0.12: d.point((x + 1, y - 1), fill=rnd.choice([hx("C2412D"), hx("F2C14E"), hx("FFFFFF")]))
    fog = (130, 70)
    if o:
        d.ellipse([fog[0] - 4, fog[1] - 1, fog[0] + 4, fog[1] + 1], fill=hx("2A1C14"))
        luz(im, fog[0], fog[1] + 1, 34, hx("FF9A4A"), 0.32, 4, ry=6)
        luz(im, fog[0], fog[1] - 4, 11, hx("FFB45A"), 0.28, 3)
        for x, y, w in gers:
            if w > 15: luz(im, x, y + 1, 16, hx("F2C14E"), 0.25, 3, ry=4)
    big = subir(im)
    jad = [hx("1F5FA8"), hx("6FB1FF"), hx("F1EDE3")] if not o else [hx("2B5A9A"), hx("3D6FB0"), hx("8A93A8")]
    def vivo(img, tick, delante):
        dd = ImageDraw.Draw(img)
        if delante:
            return
        for x, y, w in gers:                       # humo de las coronas
            humo(img, tick, x, y - w * 0.38 - w * 0.32 - 1, hx("F4F6FA") if not o else hx("4A5368"), hx("74A6DA") if not o else hx("0A1220"), semilla=x)
        for k in range(3):                         # jadag que ondean
            for s_ in range(6):
                yy = oy - 25 + k * 2 + math.sin(tick * 0.5 + s_ * 0.9 + k) * 0.9
                cuad(dd, ox + 1 + s_, yy, jad[k])
        if not o:                                  # águila que planea en círculos
            a = tick * 0.06
            ex, ey = 236 + 34 * math.cos(a), 12 + 5 * math.sin(a)
            ala = 1 if (tick // 4) % 2 else 0
            for dx in range(-4, 5):
                cuad(dd, ex + dx, ey - (abs(dx) * 0.4 if ala else -abs(dx) * 0.25), hx("2A2420"))
            cuad(dd, ex - 0.5, ey - 1, hx("F1EDE3"))
        else:
            llama(dd, fog[0], fog[1], tick, 1.1)
            for k in range(4):                     # chispas
                f = ((tick * 0.35 + k * 3.1) % 10) / 10
                cuad(dd, fog[0] + math.sin(k * 2 + tick * 0.2) * 3, fog[1] - 6 - f * 14, hx("FFC24A") if f < 0.6 else hx("B5361C"))
            for j, (sx, sy) in enumerate(titilan):  # estrellas que titilan
                if (j * 3 + tick // 4) % 6 == 0: cuad(dd, sx, sy, hx("FFFFFF"))
    rnd = random.Random(77)
    titilan = [(rnd.randrange(W), rnd.randrange(0, 40)) for _ in range(30)]
    ANIM[("mongol", o)] = [vivo]
    return big



# =====================================================================
#  Colonos: el pueblo que se reconstruye (una casa en obra, la tienda, el granero). De noche, faroles y luciérnagas.
# =====================================================================

def casa(d, x, y, w, h, pal, muro="tabla", ventanas=2, techo=None, chimenea=True, luces=()):
    """Casa de frente con el hastial al frente: muro, techo a dos aguas con alero, ventanas con marco y puerta."""
    techo = techo or pal["tejado"]
    if muro == "ladrillo":
        d.rectangle([x, y - h, x + w, y], fill=pal["ladrillo"])
        for yy in range(y - h + 1, y, 2):
            d.line([x, yy, x + w, yy], fill=pal["mortero"])
            for xx in range(x + (0 if (yy // 2) % 2 else 2), x + w, 4): d.point((xx, yy - 1), fill=pal["mortero"])
    else:
        d.rectangle([x, y - h, x + w, y], fill=pal["tabla"])
        for yy in range(y - h + 1, y, 2): d.line([x, yy, x + w, yy], fill=pal["linea"])
    d.rectangle([x + w - 3, y - h, x + w, y], fill=pal["tabla_s"] if muro != "ladrillo" else pal["ladrillo_s"])
    alto_t = w * 0.42
    d.polygon([(x - 2, y - h), (x + w / 2, y - h - alto_t), (x + w + 2, y - h)], fill=techo)
    d.polygon([(x + w / 2, y - h - alto_t), (x + w + 2, y - h), (x + w / 2, y - h)], fill=pal["tejado_s"])
    d.line([(x - 2, y - h), (x + w / 2, y - h - alto_t), (x + w + 2, y - h)], fill=pal["borde_techo"])
    d.polygon([(x + w * 0.3, y - h), (x + w / 2, y - h - alto_t * 0.6), (x + w * 0.7, y - h)], fill=pal["tabla"] if muro != "ladrillo" else pal["ladrillo"])
    d.rectangle([x + w / 2 - 1, y - h - alto_t * 0.35, x + w / 2 + 1, y - h - alto_t * 0.18], fill=pal["vidrio"])
    if chimenea:
        d.rectangle([x + w * 0.72, y - h - alto_t * 0.75, x + w * 0.72 + 3, y - h - alto_t * 0.35], fill=pal["ladrillo_s"])
        d.line([x + w * 0.72 - 1, y - h - alto_t * 0.75, x + w * 0.72 + 4, y - h - alto_t * 0.75], fill=pal["tinta"])
    xs = [x + 3 + k * ((w - 10) / max(1, ventanas - 1)) for k in range(ventanas)] if ventanas > 1 else [x + w / 2 - 2]
    for j, vx in enumerate(xs):
        for fila, vy in enumerate((y - h + 3, y - h * 0.48)):
            if fila == 1 and abs(vx + 2 - (x + w / 2)) < 5: continue
            encendida = (j, fila) in luces
            d.rectangle([vx - 1, vy - 1, vx + 5, vy + 6], fill=pal["marco"])
            d.rectangle([vx, vy, vx + 4, vy + 5], fill=pal["luz"] if encendida else pal["vidrio"])
            if not encendida: d.rectangle([vx, vy, vx + 1, vy + 1], fill=pal["vidrio2"])
            d.line([vx + 2, vy, vx + 2, vy + 5], fill=pal["marco"]); d.line([vx, vy + 2, vx + 4, vy + 2], fill=pal["marco"])
            d.rectangle([vx - 3, vy - 1, vx - 2, vy + 6], fill=pal["postigo"]); d.rectangle([vx + 6, vy - 1, vx + 7, vy + 6], fill=pal["postigo"])
    d.rectangle([x + w / 2 - 3, y - 9, x + w / 2 + 3, y], fill=pal["puerta"])
    d.line([x + w / 2 - 4, y - 10, x + w / 2 + 4, y - 10], fill=pal["marco"])
    d.point((x + w / 2 + 2, y - 4), fill=pal["oro"])


def arbol(im, d, x, y, r, verdes, tronco, semilla=0):
    """Árbol de copa redonda con luz desde arriba a la izquierda."""
    d.rectangle([x - 1, y - r, x + 1, y], fill=tronco); d.line([x, y - 4, x + 3, y - 8], fill=tronco)
    rnd = random.Random(semilla)
    bultos = [(x + rnd.uniform(-r * 0.7, r * 0.7), y - r * 1.2 + rnd.uniform(-r * 0.6, r * 0.4), rnd.uniform(r * 0.4, r * 0.6)) for _ in range(9)]
    for bx, by, br in bultos: d.ellipse([bx - br, by - br, bx + br, by + br], fill=verdes[0])
    for bx, by, br in bultos: d.ellipse([bx - br, by - br, bx + br * 0.6, by + br * 0.5], fill=verdes[1])
    for bx, by, br in bultos: d.ellipse([bx - br * 0.7, by - br * 0.8, bx + br * 0.1, by - br * 0.1], fill=verdes[2])
    px = im.load()
    for _ in range(int(r * r * 0.5)):
        xx, yy = int(x + rnd.uniform(-r, r)), int(y - r * 1.2 + rnd.uniform(-r, r))
        if 0 <= xx < W and 0 <= yy < H and px[xx, yy] in verdes[1:]: px[xx, yy] = verdes[0] if rnd.random() < 0.5 else verdes[2]


def fondo_colonos(t):
    o = t["oscuro"]
    if not o:
        pal = dict(tabla=hx("F8F3E8"), tabla_s=hx("D9CFBB"), linea=hx("E1D7C3"), ladrillo=hx("A23B2A"), ladrillo_s=hx("7E2C20"), mortero=hx("B9705E"),
                   tejado=hx("4A5568"), tejado_s=hx("353D4E"), borde_techo=hx("1F2A3A"), vidrio=hx("6F93B5"), vidrio2=hx("C8DCEA"), marco=hx("FBF8F0"),
                   postigo=hx("1F3A5F"), puerta=hx("1F3A5F"), oro=hx("E3C27A"), tinta=hx("1F2A3A"), luz=hx("F2D27A"), madera=hx("8A6A3E"),
                   madera_s=hx("5E4422"), granero=hx("A23B2A"), granero_s=hx("7E2C20"))
        cielo = [hx("7FA6D0"), hx("A6C2DE"), hx("CCDCE6"), hx("EDEDE4")]
        lomas, lomas2, verdes, tronco = hx("97AE9E"), hx("B4C5B2"), [hx("3E5A34"), hx("56743F"), hx("7E9A56")], hx("5A4024")
        camino, camino2, pasto = hx("CDB88E"), hx("B29C70"), hx("8DA45A")
    else:
        pal = dict(tabla=hx("3A4660"), tabla_s=hx("2A3348"), linea=hx("334058"), ladrillo=hx("4E2A2C"), ladrillo_s=hx("3A1E22"), mortero=hx("5E3A38"),
                   tejado=hx("1A2232"), tejado_s=hx("121826"), borde_techo=hx("0A0E16"), vidrio=hx("1E2838"), vidrio2=hx("2E3B50"), marco=hx("4E5A74"),
                   postigo=hx("151D2B"), puerta=hx("151D2B"), oro=hx("E3C27A"), tinta=hx("0A0E16"), luz=hx("F2C46A"), madera=hx("4A3A2A"),
                   madera_s=hx("2E2418"), granero=hx("4A2426"), granero_s=hx("341A1C"))
        cielo = [hx("070B14"), hx("0E1420"), hx("151D2B"), hx("1E2838")]
        lomas, lomas2, verdes, tronco = hx("141C2A"), hx("1A2434"), [hx("0E1612"), hx("152019"), hx("1C2A20")], hx("1A140E")
        camino, camino2, pasto = hx("2A2C30"), hx("222428"), hx("141C1A")
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 54, cielo)
    if not o:
        for x, y, w in ((24, 12, 30), (170, 6, 40), (330, 14, 34)):
            nube(d, x, y, w, hx("FFFFFF"), hx("EEF2F4"), hx("C7D3DE"))
    else:
        estrellas(im, 5, 70, 0, 40, [hx("EEE7D7"), hx("A9B0BC"), hx("8FB3E0")])
        d.ellipse([370, 8, 380, 18], fill=hx("EEE7D7")); d.ellipse([373, 7, 383, 17], fill=cielo[0])
    perfil(im, ondas(21, 47, ((3, 160), (1.5, 50))), lomas, borde=lomas2)
    # Iglesia lejana.
    ix, iy = 300, 46
    d.rectangle([ix - 6, iy - 8, ix + 6, iy], fill=lomas2 if o else hx("E9ECEC"))
    d.polygon([(ix - 7, iy - 8), (ix, iy - 12), (ix + 7, iy - 8)], fill=pal["tejado"])
    d.rectangle([ix - 2, iy - 18, ix + 2, iy - 8], fill=lomas2 if o else hx("E9ECEC")); d.polygon([(ix - 2, iy - 18), (ix, iy - 26), (ix + 2, iy - 18)], fill=pal["tejado"])
    if o: d.point((ix, iy - 14), fill=pal["luz"])
    for k in range(5): arbol(im, d, 250 + k * 9, 47, 4, [mezcla(v, lomas, 0.4) for v in verdes], tronco, k)
    base = 68
    luces = {(0, 0), (1, 1)} if o else set()
    # Casa de ladrillo.
    casa(d, 6, base, 46, 24, pal, "ladrillo", 3, luces=luces)
    # Casa en obra: armazón de madera, andamio y escalera.
    x0 = 66
    for k in range(5): d.line([x0 + k * 9, base, x0 + k * 9, base - 22], fill=pal["madera"])
    for yy in (base - 11, base - 22): d.line([x0, yy, x0 + 36, yy], fill=pal["madera"])
    for k in range(4): d.line([x0 + k * 9, base - 11, x0 + k * 9 + 9, base - 22], fill=pal["madera_s"])
    for k in range(0, 5): d.line([x0 + k * 4.5, base - 22 - k * 2.7 if k < 5 else 0, x0 + 18, base - 34], fill=pal["madera"]) if False else None
    d.line([x0 - 2, base - 22, x0 + 18, base - 34], fill=pal["madera"]); d.line([x0 + 18, base - 34, x0 + 38, base - 22], fill=pal["madera"])
    for k in range(1, 4): d.line([x0 + 18 - k * 4.5, base - 34 + k * 2.7, x0 + 18 - k * 4.5, base - 22], fill=pal["madera_s"])
    d.rectangle([x0 + 2, base - 10, x0 + 34, base - 1], fill=pal["tabla"]); [d.line([x0 + 2, yy, x0 + 34, yy], fill=pal["linea"]) for yy in range(base - 9, base, 2)]
    d.line([x0 + 40, base, x0 + 46, base - 24], fill=pal["madera_s"]); d.line([x0 + 44, base, x0 + 50, base - 24], fill=pal["madera_s"])
    for k in range(1, 8): d.line([x0 + 40 + k * 0.75, base - k * 3, x0 + 44 + k * 0.75, base - k * 3], fill=pal["madera_s"])
    d.rectangle([x0 + 4, base - 3, x0 + 16, base - 1], fill=pal["madera"]); d.line([x0 + 4, base - 3, x0 + 16, base - 3], fill=pal["madera_s"])
    # Casa de tablas con postigos.
    casa(d, 122, base, 44, 26, pal, "tabla", 3, luces={(2, 0), (0, 1)} if o else set())
    arbol(im, d, 196, base, 13, verdes, tronco, 3)
    # Tienda con fachada falsa y toldo a rayas.
    x0 = 222
    d.rectangle([x0, base - 30, x0 + 52, base], fill=pal["tabla"]); [d.line([x0, yy, x0 + 52, yy], fill=pal["linea"]) for yy in range(base - 29, base, 2)]
    d.rectangle([x0 + 49, base - 30, x0 + 52, base], fill=pal["tabla_s"])
    d.rectangle([x0 + 6, base - 27, x0 + 46, base - 20], fill=pal["postigo"]); d.rectangle([x0 + 7, base - 26, x0 + 45, base - 21], fill=pal["tabla"] if not o else pal["tabla_s"])
    for k in range(9): d.rectangle([x0 + 9 + k * 4, base - 25, x0 + 10 + k * 4, base - 22], fill=pal["postigo"] if k % 3 else pal["ladrillo"])
    for k in range(13): d.polygon([(x0 + k * 4, base - 17), (x0 + k * 4 + 4, base - 17), (x0 + k * 4 + 5, base - 13), (x0 + k * 4 - 1, base - 13)], fill=pal["ladrillo"] if k % 2 else pal["marco"])
    d.rectangle([x0 + 4, base - 12, x0 + 20, base - 2], fill=pal["luz"] if o else pal["vidrio"]); d.rectangle([x0 + 32, base - 12, x0 + 48, base - 2], fill=pal["luz"] if o else pal["vidrio"])
    d.rectangle([x0 + 23, base - 11, x0 + 29, base], fill=pal["puerta"])
    for xx in (x0 + 8, x0 + 40): d.ellipse([xx - 3, base - 4, xx + 3, base + 1], fill=pal["madera"]); d.line([xx - 3, base - 2, xx + 3, base - 2], fill=pal["madera_s"])
    # Granero rojo con techo de dos pendientes.
    x0 = 300
    d.rectangle([x0, base - 20, x0 + 40, base], fill=pal["granero"]); [d.line([xx, base - 20, xx, base], fill=pal["granero_s"]) for xx in range(x0 + 2, x0 + 40, 3)]
    d.polygon([(x0 - 2, base - 20), (x0 + 6, base - 30), (x0 + 20, base - 35), (x0 + 34, base - 30), (x0 + 42, base - 20)], fill=pal["tejado"])
    d.polygon([(x0 + 20, base - 35), (x0 + 34, base - 30), (x0 + 42, base - 20), (x0 + 20, base - 20)], fill=pal["tejado_s"])
    d.rectangle([x0 + 12, base - 14, x0 + 28, base], fill=pal["granero_s"], outline=pal["marco"]); d.line([x0 + 12, base - 14, x0 + 28, base], fill=pal["marco"]); d.line([x0 + 28, base - 14, x0 + 12, base], fill=pal["marco"])
    d.rectangle([x0 + 17, base - 26, x0 + 23, base - 21], fill=pal["marco"]); d.rectangle([x0 + 18, base - 25, x0 + 22, base - 22], fill=pal["luz"] if o else pal["granero_s"])
    casa(d, 356, base, 40, 22, pal, "tabla", 2, luces={(1, 0)} if o else set())
    # Banderines entre la casa de ladrillo y la de tablas.
    pts = [(x, 40 + 5 * math.sin(math.pi * (x - 52) / 70)) for x in range(52, 123)]
    d.line(pts, fill=pal["tinta"])
    for k, x in enumerate(range(55, 121, 6)):
        y = 40 + 5 * math.sin(math.pi * (x - 52) / 70)
        d.polygon([(x - 2, y), (x + 2, y), (x, y + 4)], fill=(pal["postigo"], pal["ladrillo"], pal["marco"])[k % 3])
    # Cerca de estacas, camino y pasto.
    for (a, b) in ((0, 60), (118, 186), (344, 412)):
        d.line([a, 64, b, 64], fill=pal["marco"]); d.line([a, 67, b, 67], fill=pal["marco"])
        for x in range(a + 1, b, 4):
            d.rectangle([x, 62, x + 1, 69], fill=pal["marco"]); d.point((x, 61), fill=pal["marco"]); d.point((x + 1, 69), fill=pal["tabla_s"])
    d.rectangle([0, 69, W, 71], fill=pasto)
    degradado(im, 71, H, [camino2, camino, camino])
    rnd = random.Random(12)
    for _ in range(140):
        x, y = rnd.randrange(W), rnd.randrange(72, H)
        d.point((x, y), fill=camino2 if rnd.random() < 0.7 else mezcla(camino, hx("FFFFFF"), 0.3 if not o else 0.05))
    for y in (75, 78): d.line([0, y, W, y], fill=mezcla(camino, camino2, 0.6))
    faroles = [(108, 70), (284, 70), (404, 70)] if o else []
    for x, y in faroles:
        d.line([x, y, x, y - 20], fill=hx("0A0E16")); d.rectangle([x - 2, y - 26, x + 2, y - 20], fill=hx("0A0E16"))
        d.rectangle([x - 1, y - 25, x + 1, y - 21], fill=hx("FFE3A0"))
        luz(im, x, y - 23, 16, hx("F2C46A"), 0.3, 4)
        luz(im, x, y + 3, 22, hx("F2C46A"), 0.22, 3, ry=5)
    if o:
        for (cx, cy) in ((32, 52), (36, 44), (138, 50), (156, 41), (380, 53)): luz(im, cx, cy, 7, hx("F2C46A"), 0.18, 2)
    big = subir(im)
    rnd = random.Random(31)
    lucis = [(rnd.uniform(0, W), rnd.uniform(46, 70), rnd.uniform(0, 6.3)) for _ in range(16)]
    def vivo(img, tick, delante):
        dd = ImageDraw.Draw(img)
        if not delante:
            for (x, y, w_) in ((6 + 46 * 0.72 + 1.5, base - 24 - 46 * 0.42 * 0.75, 1), (122 + 44 * 0.72 + 1.5, base - 26 - 44 * 0.42 * 0.75, 1),
                               (356 + 40 * 0.72 + 1.5, base - 22 - 40 * 0.42 * 0.75, 1)):
                humo(img, tick, x, y, hx("F4F2EC") if not o else hx("3A4458"), cielo[2], semilla=int(x))
            for x, y in faroles:
                if (tick // 3 + x) % 9 == 0: cuad(dd, x - 1, y - 25, hx("F2C46A"), 3, 5)
        elif o:
            for j, (x, y, f) in enumerate(lucis):     # luciérnagas
                xx = (x + math.sin(tick * 0.07 + f) * 6) % W; yy = y + math.sin(tick * 0.11 + f * 2) * 3
                if (tick // 5 + j) % 4: cuad(dd, xx, yy, hx("D9F27A"))
    ANIM[("colonos", o)] = [vivo]
    return big


# =====================================================================
#  Revolución: la plaza de París con la bandera, el lema, el árbol de la libertad y una barricada.
#  De noche, la plaza a la luz de las antorchas.
# =====================================================================

def fondo_revolucion(t):
    o = t["oscuro"]
    if not o:
        cielo = [hx("9DB6D2"), hx("C1D0DC"), hx("E2E3DC"), hx("F2ECDD")]
        piedra, piedra_l, piedra_s, junta = hx("E3D8C0"), hx("F0E8D6"), hx("C4B796"), hx("CDBF9F")
        pizarra, pizarra_s, ventana, postigo = hx("5E6672"), hx("464D59"), hx("2E3440"), hx("3F6B3A")
        azul, blanco, rojo, tinta = hx("1F3A6E"), hx("FAF6EC"), hx("B22234"), hx("1C1A17")
        verdes, tronco = [hx("2F5530"), hx("3F6B3A"), hx("5E8A4E")], hx("5A4024")
        ad, ad_l, ad_s = hx("9A9384"), hx("B3AC9C"), hx("6E695E")
        tienda = hx("4A3A2E")
    else:
        cielo = [hx("08090C"), hx("0E0F13"), hx("131417"), hx("1B1D22")]
        piedra, piedra_l, piedra_s, junta = hx("3A3F4A"), hx("4A505C"), hx("2A2D35"), hx("30343D")
        pizarra, pizarra_s, ventana, postigo = hx("1B1D22"), hx("131417"), hx("101115"), hx("1E2A20")
        azul, blanco, rojo, tinta = hx("2A3E6E"), hx("B2AC9E"), hx("8A2232"), hx("08090B")
        verdes, tronco = [hx("0E1712"), hx("142019"), hx("1C2C22")], hx("1A140E")
        ad, ad_l, ad_s = hx("2E3036"), hx("3A3D44"), hx("1E2024")
        tienda = hx("15161A")
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 50, cielo)
    if o: estrellas(im, 9, 50, 0, 30, [hx("F0EADB"), hx("B2AC9E")])
    else: nube(d, 286, 10, 30, hx("FFFFFF"), hx("EEF0EE"), hx("C9D3DC"))
    # Torres lejanas (siluetas) en el hueco de la plaza.
    lej = mezcla(cielo[-2], piedra_s, 0.45)
    d.rectangle([290, 26, 300, 60], fill=lej); d.rectangle([318, 26, 328, 60], fill=lej); d.rectangle([300, 34, 318, 60], fill=lej)
    for xx in (290, 318): d.rectangle([xx + 2, 30, xx + 3, 36], fill=mezcla(lej, tinta, 0.3)); d.rectangle([xx + 6, 30, xx + 7, 36], fill=mezcla(lej, tinta, 0.3))
    d.ellipse([305, 38, 313, 46], outline=mezcla(lej, tinta, 0.3))
    edificios = [(0, 50, 10), (50, 62, 6), (112, 70, 12), (182, 84, 8), (352, 60, 9)]
    lit = {(0, 1), (2, 0), (3, 2), (5, 1), (8, 0), (11, 1), (14, 0)} if o else set()
    nv = 0
    for (x0, w, top) in edificios:
        d.rectangle([x0, top + 8, x0 + w - 1, 68], fill=piedra)
        for yy in range(top + 11, 68, 4): d.line([x0, yy, x0 + w - 1, yy], fill=junta)
        d.line([x0, top + 8, x0, 68], fill=piedra_l); d.line([x0 + w - 1, top + 8, x0 + w - 1, 68], fill=piedra_s)
        # Mansarda de pizarra con buhardillas y chimeneas.
        d.polygon([(x0 - 1, top + 8), (x0 + 3, top), (x0 + w - 4, top), (x0 + w, top + 8)], fill=pizarra)
        d.line([x0 - 1, top + 8, x0 + w, top + 8], fill=piedra_l)
        d.polygon([(x0 + w - 4, top), (x0 + w, top + 8), (x0 + w - 4, top + 8)], fill=pizarra_s)
        for bx in range(x0 + 7, x0 + w - 6, 13):
            d.rectangle([bx, top + 3, bx + 4, top + 8], fill=piedra_l); d.rectangle([bx + 1, top + 4, bx + 3, top + 7], fill=ventana)
        d.rectangle([x0 + w - 12, top - 4, x0 + w - 8, top], fill=piedra_s)
        for k in range(2): d.rectangle([x0 + w - 12 + k * 2, top - 6, x0 + w - 12 + k * 2, top - 4], fill=hx("A0522D") if not o else hx("2A1A14"))
        # Ventanas con postigos en dos pisos y un balcón.
        for fila, vy in enumerate((top + 13, top + 25)):
            for vx in range(x0 + 6, x0 + w - 6, 11):
                encendida = (nv % 17, fila) in lit or (nv, fila) in lit
                nv += 1
                d.rectangle([vx, vy, vx + 3, vy + 7], fill=hx("E8B860") if encendida else ventana)
                if encendida: d.line([vx + 1, vy + 1, vx + 1, vy + 6], fill=hx("FFE0A0"))
                d.rectangle([vx - 2, vy, vx - 1, vy + 7], fill=postigo); d.rectangle([vx + 4, vy, vx + 5, vy + 7], fill=postigo)
                d.line([vx - 1, vy - 1, vx + 4, vy - 1], fill=piedra_l)
                if fila == 0: d.line([vx - 2, vy + 8, vx + 5, vy + 8], fill=tinta); [d.point((bx_, vy + 9), fill=tinta) for bx_ in range(vx - 2, vx + 6, 2)]
        # Planta baja: arcos de tiendas y carteles pegados.
        for ax in range(x0 + 4, x0 + w - 10, 16):
            d.rectangle([ax, 56, ax + 9, 68], fill=tienda); d.pieslice([ax, 52, ax + 9, 60], 180, 360, fill=tienda)
            d.line([ax, 56, ax + 9, 56], fill=mezcla(tienda, piedra, 0.3))
        for px_ in range(x0 + 15, x0 + w - 4, 16):
            d.rectangle([px_, 55, px_ + 4, 61], fill=hx("F7F2E4") if not o else hx("5A5850"))
            for k in range(3): d.line([px_ + 1, 56 + k * 2, px_ + 3, 56 + k * 2], fill=tinta if not o else hx("2A2A2A"))
    # Banda del lema sobre la planta baja del edificio del centro.
    d.rectangle([186, 47, 262, 52], fill=piedra_l)
    # Árbol de la libertad: cintas tricolor en el tronco y el gorro frigio arriba.
    tx = 309
    d.rectangle([tx - 2, 40, tx + 2, 68], fill=tronco)
    for y in range(41, 67):
        d.point((tx - 2 + (y % 5), y), fill=(azul, blanco, rojo)[(y // 2) % 3])
    d.line([tx, 30, tx, 6], fill=tronco if not o else hx("2A2018"))
    arbol(im, d, tx, 42, 15, verdes, tronco, 5)
    d.polygon([(tx - 4, 8), (tx - 3, 3), (tx + 1, 1), (tx + 4, 3), (tx + 2, 4), (tx + 4, 8)], fill=rojo); d.point((tx - 2, 6), fill=blanco)
    for x in range(tx - 10, tx + 11, 3): d.line([x, 63, x, 68], fill=tronco)
    d.line([tx - 10, 64, tx + 10, 64], fill=tronco)
    # Barricada: toneles, una rueda de carreta, adoquines y una bandera.
    bx = 384
    for (cx, cy, r) in ((372, 64, 4), (380, 65, 4), (376, 58, 4), (398, 64, 4)):
        d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=hx("7A5A2E") if not o else hx("2A2018")); d.line([cx - r, cy, cx + r, cy], fill=hx("4A3418") if not o else hx("120E0A"))
    d.ellipse([384, 52, 400, 68], outline=hx("5A4024") if not o else hx("1A140E"))
    for k in range(6):
        an = k * math.pi / 3; d.line([392, 60, 392 + 8 * math.cos(an), 60 + 8 * math.sin(an)], fill=hx("5A4024") if not o else hx("1A140E"))
    for k in range(9): d.rectangle([362 + k * 5, 66, 365 + k * 5, 68], fill=ad_s)
    d.line([406, 68, 404, 40], fill=tronco)
    big0 = subir(im)
    # Adoquines (se dibujan en la banda para que se vean las piedras de 1 dp con su brillo).
    for row, (y0, h) in enumerate(((68, 2), (70, 2), (72, 3), (75, 3), (78, 3))):
        x = -(row % 2) * 2
        rnd = random.Random(row)
        while x < W:
            w = rnd.randint(3, 4) + (1 if h == 3 else 0)
            d.rectangle([x, y0, x + w - 2, y0 + h - 1], fill=rnd.choice((ad, ad, ad_l)))
            d.line([x, y0, x + w - 3, y0], fill=ad_l if not o else ad_l)
            d.rectangle([x + w - 1, y0, x + w - 1, y0 + h - 1], fill=ad_s); d.line([x, y0 + h - 1, x + w - 1, y0 + h - 1], fill=ad_s)
            x += w
    antorchas = [(62, 46), (176, 44), (272, 44)] if o else []
    for x, y in antorchas:
        d.line([x - 2, y + 6, x, y + 1], fill=hx("4A3A2A")); d.rectangle([x - 1, y, x + 1, y + 2], fill=hx("4A3A2A"))
        luz(im, x, y - 2, 14, hx("F0A040"), 0.3, 3)
        luz(im, x, 72, 30, hx("F0A040"), 0.22, 3, ry=5)
    big = subir(im)
    dd = ImageDraw.Draw(big)
    dd.text((224 * ESC, 49.6 * ESC), "LIBERTÉ · ÉGALITÉ · FRATERNITÉ", font=fuente("DejaVuSerif-Bold.ttf", 11), fill=tinta if not o else hx("C9C1AE"), anchor="mm")
    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        # Bandera tricolor que ondea en el asta del tejado.
        ax, ay = 236, 2
        cuad(dd, ax, ay, hx("1C1A17") if not o else hx("08090B"), 1, 22)
        for i in range(20):
            off = math.sin(tick * 0.35 - i * 0.45) * (0.3 + i * 0.06)
            col = (azul, blanco, rojo)[min(2, i * 3 // 20)]
            som = 0.12 if math.cos(tick * 0.35 - i * 0.45) > 0.3 else 0
            cuad(dd, ax + 1 + i, ay + 1 + off, mezcla(col, tinta, som), 1, 11)
        for j, (x, y) in enumerate(antorchas):
            llama(dd, x, y, tick, 0.9, semilla=j)
    ANIM[("revolucion", o)] = [vivo]
    return big


# =====================================================================
#  Napoleónico: el salón del trono (muro azul con abejas de oro, ventanales con cortinas carmesí, la N con laurel).
#  De noche, terciopelo azul a la luz de las arañas.
# =====================================================================

def fondo_napoleon(t):
    o = t["oscuro"]
    if not o:
        muro, muro_l, muro_s = hx("1E3A8A"), hx("2C4C9E"), hx("172E6E")
        oro, oro_l, oro_s = hx("C9A040"), hx("EED58A"), hx("8A6418")
        crema, crema_s = hx("F4EEDF"), hx("D9CCAA")
        carm, carm_l, carm_s = hx("A3172E"), hx("C8384A"), hx("74101F")
        vidrio, vidrio_l = hx("BBD0E8"), hx("E6EEF6")
        piso_a, piso_b = hx("EFE8D8"), hx("BDB7C9")
    else:
        muro, muro_l, muro_s = hx("121A3D"), hx("1B2550"), hx("0B1230")
        oro, oro_l, oro_s = hx("B8923E"), hx("E8CB7A"), hx("6A5020")
        crema, crema_s = hx("3A3F5E"), hx("2C3150")
        carm, carm_l, carm_s = hx("5A1424"), hx("7A2030"), hx("3A0C18")
        vidrio, vidrio_l = hx("1F2E5E"), hx("5E78C0")
        piso_a, piso_b = hx("2C3970"), hx("121A3D")
    im, d = lienzo(muro)
    degradado(im, 6, 52, [muro_s, muro, muro, muro_l] if not o else [muro_s, muro_s, muro, muro])
    # Abejas imperiales sembradas en el muro.
    for fila, y in enumerate(range(10, 50, 8)):
        for x in range(4 + (fila % 2) * 6, W, 12):
            d.point((x, y), fill=oro); d.point((x, y + 1), fill=oro); d.point((x, y + 2), fill=oro_s)
            d.point((x - 1, y), fill=mezcla(oro_l, muro, 0.4)); d.point((x + 1, y), fill=mezcla(oro_l, muro, 0.4))
    # Cornisa con ovas de oro.
    d.rectangle([0, 0, W, 5], fill=crema); d.line([0, 5, W, 5], fill=oro)
    for x in range(1, W, 4): d.ellipse([x, 2, x + 2, 4], fill=oro if (x // 4) % 2 else oro_s)
    d.line([0, 1, W, 1], fill=oro_l)
    # Lambrín: barandal de oro y paneles enmarcados.
    d.rectangle([0, 52, W, 64], fill=crema); d.line([0, 52, W, 52], fill=oro_l); d.line([0, 53, W, 53], fill=oro)
    for x in range(4, W, 30): d.rectangle([x, 55, x + 24, 62], outline=oro_s)
    d.line([0, 64, W, 64], fill=oro_s)
    # Piso de mármol en damero con perspectiva.
    px = im.load(); fx, fy = 206, 30
    for y in range(65, H):
        for x in range(W):
            u = (x - fx) / (y - fy) * 3.2; v = 260 / (y - fy)
            px[x, y] = piso_a if (math.floor(u) + math.floor(v)) % 2 else piso_b
    # Ventanales con cortinas, cenefa y haz de luz.
    for vx in (22, 354):
        if not o:
            luzpol = Image.new("L", (W, H), 0); ImageDraw.Draw(luzpol).polygon([(vx + 2, 52), (vx + 34, 52), (vx + 70, H), (vx + 30, H)], fill=255)
            m_ = luzpol.load()
            for y in range(52, H):
                for x in range(W):
                    if m_[x, y] and tramar(0.5, x, y): px[x, y] = mezcla(px[x, y], hx("FFF8E0"), 0.35)
        d.rectangle([vx, 10, vx + 36, 51], fill=crema)
        d.pieslice([vx, 4, vx + 36, 24], 180, 360, fill=crema)
        d.rectangle([vx + 3, 14, vx + 33, 50], fill=vidrio); d.pieslice([vx + 3, 7, vx + 33, 21], 180, 360, fill=vidrio)
        degradado(im, 8, 50, [vidrio_l, vidrio, vidrio], vx + 4, vx + 33)
        for k in range(1, 4): d.line([vx + 3 + k * 7.5, 8, vx + 3 + k * 7.5, 50], fill=crema)
        for yy in range(20, 50, 8): d.line([vx + 3, yy, vx + 33, yy], fill=crema)
        if o:
            d.ellipse([vx + 22, 18, vx + 28, 24], fill=hx("F1E9D2"))
        for lado in (0, 1):
            xa = vx - 4 if lado == 0 else vx + 30
            d.rectangle([xa, 6, xa + 10, 54], fill=carm)
            for k in range(0, 10, 3): d.line([xa + k, 6, xa + k, 54], fill=carm_s); d.line([xa + k + 1, 6, xa + k + 1, 54], fill=carm_l)
            d.polygon([(xa, 36), (xa + 10, 36), (xa + 10 - (6 if lado == 0 else 0), 40), (xa + (6 if lado == 1 else 0), 40)], fill=muro if not o else muro_s)
            d.ellipse([xa + 3, 36, xa + 7, 40], fill=oro)
        d.rectangle([vx - 6, 4, vx + 42, 9], fill=carm); d.line([vx - 6, 9, vx + 42, 9], fill=oro)
        for x in range(vx - 6, vx + 43, 2): d.point((x, 10), fill=oro)
        for k in range(4): d.arc([vx - 6 + k * 12, 4, vx + 6 + k * 12, 14], 0, 180, fill=oro)
    # Pilastras con capitel de oro.
    for x in (112, 292):
        d.rectangle([x, 6, x + 8, 52], fill=crema)
        for k in (2, 4, 6): d.line([x + k, 12, x + k, 48], fill=crema_s)
        d.rectangle([x - 1, 6, x + 9, 10], fill=oro); d.line([x - 1, 10, x + 9, 10], fill=oro_s); d.rectangle([x - 1, 49, x + 9, 52], fill=oro)
    # Medallón: corona de laurel de oro con la N y la corona imperial.
    cx, cy = 206, 30
    d.ellipse([cx - 12, cy - 12, cx + 12, cy + 12], fill=muro_s)
    for k in range(26):
        an = math.radians(100 + k * 13.5)
        if 250 < math.degrees(an) % 360 < 290: continue
        x, y = cx + 12 * math.cos(an), cy + 12 * math.sin(an)
        d.ellipse([x - 1.5, y - 1, x + 1.5, y + 1], fill=oro if k % 2 else oro_l)
    d.line([cx - 5, cy + 6, cx - 5, cy - 6], fill=oro_l, width=2); d.line([cx - 5, cy - 6, cx + 4, cy + 6], fill=oro_l, width=2)
    d.line([cx + 4, cy + 6, cx + 4, cy - 6], fill=oro_l, width=2)
    d.polygon([(cx - 7, cy - 13), (cx - 7, cy - 18), (cx - 4, cy - 15), (cx, cy - 20), (cx + 4, cy - 15), (cx + 7, cy - 18), (cx + 7, cy - 13)], fill=oro)
    d.line([cx - 7, cy - 13, cx + 7, cy - 13], fill=oro_s); d.point((cx, cy - 21), fill=oro_l)
    for k in (-1, 1):
        d.line([cx + k * 13, cy + 10, cx + k * 22, cy + 16], fill=oro); d.line([cx + k * 22, cy + 16, cx + k * 26, cy + 14], fill=oro)
    # Arañas (candelabros).
    velas = []
    for ax in (160, 252):
        d.line([ax, 5, ax, 13], fill=oro_s)
        d.ellipse([ax - 2, 12, ax + 2, 16], fill=oro)
        d.arc([ax - 10, 10, ax + 10, 20], 0, 180, fill=oro)
        for k in (-10, -5, 0, 5, 10):
            d.rectangle([ax + k, 12, ax + k, 15 - (1 if k == 0 else 0)], fill=hx("FBF6EA") if not o else hx("E8E0C8"))
            velas.append((ax + k, 12))
        for k in range(-8, 9, 4): d.point((ax + k, 21), fill=vidrio_l if not o else hx("9DB4FF")); d.point((ax + k, 22), fill=hx("FFFFFF") if not o else hx("5E78C0"))
        if o:
            luz(im, ax, 14, 18, hx("F2C46A"), 0.26, 3, ry=12)
            luz(im, ax, 72, 40, hx("F2C46A"), 0.18, 3, ry=6)
    if o:
        luz(im, cx, cy, 16, oro_l, 0.15, 2)
    big = subir(im)
    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        rnd = random.Random(tick // 2)
        for j, (x, y) in enumerate(velas):
            if o or True:
                alto = 2 if rnd.random() < 0.7 else 1
                if o or j % 3 == 0:
                    cuad(dd, x, y - alto, hx("FFE7A0") if o else hx("FFD27A"), 1, alto)
        if not o:
            for j in range(10):                       # motas de polvo en el haz de luz
                f = ((tick * 0.02 + j * 0.1) % 1)
                cuad(dd, 40 + j * 6 + f * 20, 54 + f * 22, hx("FFF8E0"))
    ANIM[("napoleon", o)] = [vivo]
    return big



# =====================================================================
#  Laboratorio: repisas con material de vidrio, pizarrón con una gráfica, reloj y la mesa de trabajo.
#  De noche, los líquidos y las pantallas brillan en la oscuridad.
# =====================================================================

def frasco(d, x, yb, tipo, liq, vidrio, borde, tapa):
    if tipo == "matraz":
        d.polygon([(x - 1, yb - 9), (x + 1, yb - 9), (x + 1, yb - 6), (x + 4, yb), (x - 4, yb), (x - 1, yb - 6)], fill=vidrio, outline=borde)
        d.polygon([(x - 3, yb - 2), (x + 3, yb - 2), (x + 4, yb), (x - 4, yb)], fill=liq)
    elif tipo == "probeta":
        d.rectangle([x - 1, yb - 12, x + 1, yb], fill=vidrio, outline=borde); d.rectangle([x, yb - 7, x, yb - 1], fill=liq)
        d.line([x - 2, yb, x + 2, yb], fill=borde)
    elif tipo == "botella":
        d.rectangle([x - 3, yb - 7, x + 3, yb], fill=liq); d.rectangle([x - 1, yb - 9, x + 1, yb - 8], fill=tapa)
        d.rectangle([x - 2, yb - 5, x + 2, yb - 3], fill=hx("F4F7F9") if sum(vidrio) > 400 else hx("97A8B9"))
    elif tipo == "balon":
        d.ellipse([x - 4, yb - 8, x + 4, yb], fill=vidrio, outline=borde); d.rectangle([x - 1, yb - 12, x + 1, yb - 7], fill=vidrio, outline=borde)
        d.chord([x - 4, yb - 8, x + 4, yb], 20, 160, fill=liq)
    elif tipo == "vaso":
        d.rectangle([x - 3, yb - 7, x + 3, yb], fill=vidrio, outline=borde); d.rectangle([x - 2, yb - 4, x + 2, yb - 1], fill=liq)


def fondo_laboratorio(t):
    o = t["oscuro"]
    if not o:
        pared, junta, tinta = hx("F4F7F9"), hx("E2EAF0"), hx("12202B")
        cian, viol, verde, rosa, ambar = hx("0B8FA1"), hx("5E4FD1"), hx("1E8E5A"), hx("C0466A"), hx("B5752A")
        acero, acero_l, acero_s, gris = hx("4D5D6B"), hx("97A8B9"), hx("33414D"), hx("CBD6DF")
        vidrio, borde = hx("E6F3F6"), hx("8FB5C2")
        mesa, frente, piso, piso_s = hx("DDE5EB"), hx("B9C6D1"), hx("E6EDF2"), hx("CBD6DF")
        pizarra, pizarra_m = hx("FFFFFF"), hx("97A8B9")
    else:
        pared, junta, tinta = hx("101823"), hx("0C131C"), hx("E3ECF3")
        cian, viol, verde, rosa, ambar = hx("3CD2E5"), hx("9D93FF"), hx("5FD49C"), hx("FF7A9A"), hx("E0A050")
        acero, acero_l, acero_s, gris = hx("263447"), hx("3A4C62"), hx("18222F"), hx("263447")
        vidrio, borde = hx("1A2836"), hx("3A5466")
        mesa, frente, piso, piso_s = hx("1E2A38"), hx("151E29"), hx("121A24"), hx("0E141C")
        pizarra, pizarra_m = hx("152230"), hx("263447")
    im, d = lienzo(pared)
    for y in range(8, 58, 6): d.line([0, y, W, y], fill=junta)
    for y in range(8, 58, 6):
        for x in range((y // 6 % 2) * 4, W, 8): d.line([x, y, x, y + 5], fill=junta)
    # Lámparas de techo.
    for lx in (40, 330):
        d.rectangle([lx, 0, lx + 50, 2], fill=acero_l); d.line([lx + 2, 2, lx + 48, 2], fill=hx("FFFFFF") if not o else acero_s)
    if not o:
        for lx in (40, 330):
            pol = Image.new("L", (W, H), 0); ImageDraw.Draw(pol).polygon([(lx, 3), (lx + 50, 3), (lx + 70, 56), (lx - 20, 56)], fill=255)
            m_ = pol.load(); px = im.load()
            for y in range(3, 56):
                for x in range(max(0, lx - 20), min(W, lx + 71)):
                    if m_[x, y] and tramar(0.35 * (1 - y / 56), x, y): px[x, y] = mezcla(px[x, y], hx("FFFFFF"), 0.6)
    # Repisas con frascos (izquierda y derecha).
    rnd = random.Random(3)
    liqs = [cian, viol, verde, rosa, ambar]
    for (x0, x1, ys) in ((8, 112, (26, 44)), (330, 404, (24, 42))):
        for yb in ys:
            d.rectangle([x0, yb + 1, x1, yb + 2], fill=acero_l); d.line([x0, yb + 2, x1, yb + 2], fill=acero_s)
            for bx in (x0 + 4, x1 - 4): d.line([bx, yb + 3, bx + 3, yb + 3], fill=acero_s); d.line([bx + 1, yb + 3, bx + 1, yb + 5], fill=acero_s)
            x = x0 + 6
            while x < x1 - 5:
                tipo = rnd.choice(["matraz", "probeta", "botella", "botella", "balon", "vaso"])
                frasco(d, x, yb, tipo, rnd.choice(liqs), vidrio, borde, acero)
                x += rnd.randint(8, 11)
    # Reloj de pared.
    rx, ry = 136, 18
    d.ellipse([rx - 8, ry - 8, rx + 8, ry + 8], fill=acero); d.ellipse([rx - 7, ry - 7, rx + 7, ry + 7], fill=hx("FFFFFF") if not o else hx("1A2836"))
    for k in range(12):
        an = k * math.pi / 6; d.point((rx + 6 * math.cos(an), ry + 6 * math.sin(an)), fill=tinta if not o else cian)
    d.line([rx, ry, rx, ry - 5], fill=tinta if not o else tinta); d.line([rx, ry, rx + 3, ry + 1], fill=tinta if not o else tinta)
    # Pizarrón: una curva normal con datos y barras de error.
    bx0, by0, bx1, by1 = 160, 8, 262, 46
    d.rectangle([bx0, by0, bx1, by1], fill=pizarra, outline=pizarra_m); d.rectangle([bx0 - 1, by0 - 1, bx1 + 1, by1 + 1], outline=acero_l)
    d.rectangle([bx0 + 30, by1 + 1, bx1 - 30, by1 + 2], fill=acero_l)
    ax0, ay0 = bx0 + 8, by1 - 6
    d.line([ax0, by0 + 6, ax0, ay0], fill=tinta if not o else acero_l); d.line([ax0, ay0, bx1 - 6, ay0], fill=tinta if not o else acero_l)
    pts = [(ax0 + i, ay0 - 26 * math.exp(-((i - 44) / 15) ** 2)) for i in range(0, 86)]
    d.line(pts, fill=viol)
    rnd2 = random.Random(6)
    for i in range(6, 86, 8):
        y = ay0 - 26 * math.exp(-((i - 44) / 15) ** 2) + rnd2.uniform(-2.5, 2.5)
        e = rnd2.uniform(1.5, 3)
        d.line([ax0 + i, y - e, ax0 + i, y + e], fill=cian); d.point((ax0 + i, y), fill=rosa); d.point((ax0 + i - 1, y), fill=rosa)
    d.line([ax0 + 44, by0 + 6, ax0 + 44, ay0], fill=mezcla(pizarra, viol, 0.4))
    # Tabla periódica.
    tx0 = 280
    for r in range(5):
        for c in range(9):
            if r == 0 and 0 < c < 8: continue
            if r in (1, 2) and 1 < c < 4: continue
            col = (cian, viol, verde, rosa, ambar)[(c * 3 + r) % 5]
            d.rectangle([tx0 + c * 4, 10 + r * 4, tx0 + c * 4 + 2, 10 + r * 4 + 2], fill=mezcla(col, pared, 0.35 if not o else 0.55))
    # Mesa de trabajo.
    d.rectangle([0, 58, W, 60], fill=mesa); d.line([0, 58, W, 58], fill=hx("FFFFFF") if not o else acero_l)
    d.rectangle([0, 61, W, 71], fill=frente)
    for x in range(0, W, 34):
        d.rectangle([x + 2, 62, x + 31, 70], outline=mezcla(frente, acero, 0.4)); d.line([x + 13, 66, x + 20, 66], fill=acero)
    d.line([0, 61, W, 61], fill=acero_s)
    # Sobre la mesa: microscopio, destilación, gradilla, balanza, osciloscopio y vaso con agitador.
    mx = 34
    d.rectangle([mx - 6, 56, mx + 6, 57], fill=acero); d.line([mx + 3, 56, mx + 3, 44], fill=acero, width=2)
    d.line([mx + 3, 44, mx - 3, 40], fill=acero, width=3); d.rectangle([mx - 5, 38, mx - 2, 40], fill=acero_s)
    d.line([mx - 3, 41, mx - 3, 49], fill=acero_s); d.rectangle([mx - 5, 50, mx + 1, 51], fill=acero_l)
    sx = 112
    d.line([sx - 8, 57, sx - 8, 34], fill=acero); d.rectangle([sx - 11, 56, sx - 3, 57], fill=acero)
    d.line([sx - 8, 44, sx - 2, 44], fill=acero)
    frasco(d, sx, 50, "balon", rosa, vidrio, borde, acero)
    d.line([sx, 38, sx + 22, 46], fill=borde); d.line([sx + 6, 40, sx + 20, 45], fill=vidrio, width=2)
    d.line([sx + 22, 46, sx + 22, 50], fill=borde)
    frasco(d, sx + 22, 57, "vaso", rosa, vidrio, borde, acero)
    d.rectangle([sx - 2, 53, sx + 2, 57], fill=acero_s)
    for k in range(5):
        x = 176 + k * 5
        d.rectangle([x, 47, x + 2, 57], fill=vidrio, outline=borde); d.rectangle([x + 1, 52 - k, x + 1, 56], fill=liqs[k])
    d.rectangle([173, 51, 199, 53], fill=acero_l); d.rectangle([173, 56, 199, 57], fill=acero_l)
    # Balanza analítica.
    d.rectangle([214, 50, 236, 57], fill=gris if not o else acero, outline=acero); d.rectangle([217, 52, 229, 55], fill=hx("0C1A14"))
    d.rectangle([218, 46, 232, 49], fill=acero_l); d.line([225, 49, 225, 50], fill=acero)
    # Osciloscopio.
    d.rectangle([252, 40, 290, 57], fill=acero, outline=acero_s); d.rectangle([255, 43, 279, 55], fill=hx("08140F") if not o else hx("040A08"))
    for k in range(3): d.ellipse([282, 44 + k * 4, 286, 47 + k * 4], fill=acero_l)
    # Vaso con agitador magnético.
    d.rectangle([344, 54, 362, 57], fill=acero_l if not o else acero); d.rectangle([347, 42, 359, 54], fill=vidrio, outline=borde)
    d.rectangle([348, 46, 358, 53], fill=cian); d.line([350, 52, 356, 52], fill=hx("FFFFFF"))
    if o:
        for (x, y, c) in ((112, 46, rosa), (353, 48, cian), (189, 52, verde), (267, 49, verde), (136, 18, cian)):
            luz(im, x, y, 10, c, 0.25, 3)
    # Piso de losetas.
    degradado(im, 72, H, [piso_s, piso, piso])
    for x in range(0, W, 12): d.line([x, 72, x - 2, H], fill=piso_s)
    d.line([0, 75, W, 75], fill=piso_s)
    big = subir(im)
    dd = ImageDraw.Draw(big)
    dd.text((236 * ESC, 13 * ESC), "μ = 7,2 ± 0,3", font=fuente("DejaVuSans.ttf", 12), fill=tinta if not o else cian)
    rnd3 = random.Random(9)
    burbujas = [(rnd3.uniform(349, 357), rnd3.uniform(0, 1)) for _ in range(5)]
    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        # Segundero.
        an = tick * DT_ * 2 * math.pi / 60 - math.pi / 2
        dd.line([rx * ESC + 1, ry * ESC + 1, (rx + 6 * math.cos(an)) * ESC + 1, (ry + 6 * math.sin(an)) * ESC + 1], fill=rosa, width=2)
        # Traza del osciloscopio que corre.
        prev = None
        for i in range(24):
            yy = 49 + 3.2 * math.sin((i + tick * 0.8) * 0.55) * math.exp(-((i + tick * 0.8) % 24 - 12) ** 2 / 60)
            p_ = ((255 + i) * ESC + 1, yy * ESC)
            if prev: dd.line([prev, p_], fill=verde if not o else hx("7CFFB0"), width=2)
            prev = p_
        # Burbujas del vaso y del matraz que hierve; llama azul del mechero.
        for j, (x, f) in enumerate(burbujas):
            ff = (f + tick * 0.05) % 1
            cuad(dd, x, 53 - ff * 7, hx("FFFFFF") if not o else hx("CFF6FB"))
            cuad(dd, sx - 2 + j, 49 - ((f + tick * 0.08) % 1) * 4, hx("FFFFFF") if not o else hx("FFD0DC"))
        h_ = 2 + (tick // 2) % 2
        cuad(dd, sx - 1, 53 - h_, hx("5E8BFF"), 2, h_); cuad(dd, sx - 0.5, 53 - h_ + 1, hx("CFE0FF"), 1, h_ - 1)
        # Dígitos de la balanza (cambian el último decimal) y lucecitas.
        dd.text((223 * ESC, 53.6 * ESC), f"12.0{(tick // 6) % 3 + 3}", font=fuente("DejaVuSansMono-Bold.ttf", 7), fill=hx("5FD49C"), anchor="mm")
        if (tick // 8) % 2: cuad(dd, 287, 54, hx("FF7A9A"))
    ANIM[("laboratorio", o)] = [vivo]
    return big


DT_ = g8.DT


# =====================================================================
#  Refugio: el atrio del refugio, optimismo atómico de los cincuenta (tubos, lámparas de jaula, cartel del átomo,
#  la puerta en forma de engrane). De noche: la misma sala dibujada en una terminal de fósforo verde.
# =====================================================================

def puerta_engrane(d, cx, cy, r, cuerpo, sombra, borde, dientes, centro):
    for k in range(20):
        an = 2 * math.pi * k / 20
        pts = [(cx + (r - 1) * math.cos(an - 0.1), cy + (r - 1) * math.sin(an - 0.1)), (cx + (r + 3) * math.cos(an - 0.07), cy + (r + 3) * math.sin(an - 0.07)),
               (cx + (r + 3) * math.cos(an + 0.07), cy + (r + 3) * math.sin(an + 0.07)), (cx + (r - 1) * math.cos(an + 0.1), cy + (r - 1) * math.sin(an + 0.1))]
        d.polygon(pts, fill=dientes)
    d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=cuerpo, outline=borde)
    d.pieslice([cx - r, cy - r, cx + r, cy + r], 300, 120, fill=sombra)
    d.ellipse([cx - r + 4, cy - r + 4, cx + r - 4, cy + r - 4], outline=borde)
    d.ellipse([cx - 5, cy - 5, cx + 5, cy + 5], fill=centro, outline=borde)


def fondo_refugio(t):
    o = t["oscuro"]
    if o:
        return fondo_refugio_noche(t)
    pared, panel, panel_s, borde_ = hx("EDE2C6"), hx("F8F1DE"), hx("E2D5B2"), hx("C9B98E")
    tinta, teal, teal_l, teal_s = hx("1F2A2A"), hx("1C6E6A"), hx("4FA59E"), hx("134E4B")
    nar, nar_l, oro, oro_l, rojo = hx("D9572B"), hx("F08A5E"), hx("B98A15"), hx("E8C25A"), hx("A3312F")
    metal, metal_l, metal_s = hx("8A928F"), hx("B8BFBC"), hx("5E6664")
    im, d = lienzo(pared)
    # Techo con tubos, abrazaderas y una válvula.
    d.rectangle([0, 0, W, 8], fill=metal_s)
    d.rectangle([0, 1, W, 3], fill=teal); d.line([0, 1, W, 1], fill=teal_l); d.line([0, 3, W, 3], fill=teal_s)
    d.rectangle([0, 5, W, 6], fill=nar); d.line([0, 5, W, 5], fill=nar_l)
    for x in range(14, W, 44): d.rectangle([x, 0, x + 2, 7], fill=metal); d.line([x, 0, x, 7], fill=metal_l)
    d.ellipse([146, -1, 156, 9], outline=rojo); d.line([151, -1, 151, 9], fill=rojo); d.line([146, 4, 156, 4], fill=rojo)
    # Muro: costillas de la bóveda y paneles redondeados.
    costillas = list(range(0, W, 64))
    for x0 in costillas:
        for k, y0 in enumerate((11, 31)):
            d.rounded_rectangle([x0 + 9, y0, x0 + 58, y0 + 17], radius=3, fill=panel, outline=borde_)
            d.line([x0 + 11, y0 + 16, x0 + 56, y0 + 16], fill=panel_s)
        d.rectangle([x0, 8, x0 + 6, 63], fill=metal); d.line([x0, 8, x0, 63], fill=metal_l); d.line([x0 + 5, 8, x0 + 5, 63], fill=metal_s)
        for y in range(12, 62, 7): d.point((x0 + 2, y), fill=metal_s); d.point((x0 + 4, y), fill=metal_l)
    d.rectangle([0, 50, W, 55], fill=teal); d.line([0, 50, W, 50], fill=teal_l); d.line([0, 55, W, 55], fill=teal_s)
    # Cartel del átomo.
    px0, py0 = 86, 13
    d.rectangle([px0, py0, px0 + 32, py0 + 33], fill=teal, outline=tinta)
    d.rectangle([px0 + 2, py0 + 2, px0 + 30, py0 + 24], fill=oro_l)
    for k in range(12):
        an = 2 * math.pi * k / 12
        d.line([px0 + 16, py0 + 13, px0 + 16 + 16 * math.cos(an), py0 + 13 + 16 * math.sin(an)], fill=oro if k % 2 else oro_l)
    d.rectangle([px0 + 2, py0 + 2, px0 + 30, py0 + 24], outline=oro)
    for a in (0, 60, 120):
        e = Image.new("L", (W, H), 0); ImageDraw.Draw(e).ellipse([px0 + 6, py0 + 10, px0 + 26, py0 + 16], outline=255)
        e = e.rotate(a, center=(px0 + 16, py0 + 13))
        m_ = e.load(); pxx = im.load()
        for y in range(py0 + 2, py0 + 25):
            for x in range(px0 + 2, px0 + 31):
                if m_[x, y] > 100: pxx[x, y] = nar
    d.ellipse([px0 + 14, py0 + 11, px0 + 18, py0 + 15], fill=rojo)
    d.rectangle([px0 + 2, py0 + 26, px0 + 30, py0 + 31], fill=tinta)
    # Placa «TODO EN ORDEN» y panel de control junto a la puerta.
    d.rectangle([186, 16, 250, 26], fill=teal, outline=tinta)
    d.rectangle([292, 22, 316, 46], fill=metal, outline=metal_s); d.rectangle([294, 24, 314, 30], fill=hx("1E2A20"))
    for k in range(4): d.ellipse([294 + k * 5, 33, 297 + k * 5, 36], fill=(rojo, oro_l, teal_l, nar)[k])
    for k in range(3): d.rectangle([295 + k * 6, 39, 298 + k * 6, 44], fill=metal_l); d.line([295 + k * 6, 41 + k, 298 + k * 6, 41 + k], fill=tinta)
    # La puerta del refugio: engrane con el número 21 y franja de peligro.
    cx, cy = 368, 34
    d.ellipse([cx - 32, cy - 32, cx + 32, cy + 32], fill=metal_s)
    for k in range(24):
        an = 2 * math.pi * k / 24
        d.pieslice([cx - 31, cy - 31, cx + 31, cy + 31], math.degrees(an), math.degrees(an) + 7.5, fill=oro_l)
    d.ellipse([cx - 28, cy - 28, cx + 28, cy + 28], fill=hx("2A3332"))
    puerta_engrane(d, cx, cy, 23, metal_l, metal, metal_s, metal, oro)
    d.rectangle([cx + 26, cy - 3, cx + 40, cy + 3], fill=metal); d.line([cx + 26, cy - 3, cx + 40, cy - 3], fill=metal_l)
    # Planta en su maceta, bajo la lámpara.
    for k, (dx, h, c) in enumerate(((-4, 9, teal_s), (-1, 13, hx("3C7A3A")), (2, 11, hx("2E5E2E")), (5, 8, hx("4C8A46")))):
        d.line([30 + dx, 57, 30 + dx + (k - 1.5), 57 - h], fill=c); d.line([30 + dx + (k - 1.5), 57 - h, 30 + dx + (k - 1.5) + 2, 57 - h + 2], fill=c)
    d.polygon([(23, 57), (37, 57), (35, 64), (25, 64)], fill=nar); d.line([23, 57, 37, 57], fill=nar_l)
    # Lámparas de jaula.
    lamparas = [(54, 9), (200, 9), (272, 9)]
    for x, y in lamparas:
        d.line([x, 7, x, y], fill=metal_s); d.rectangle([x - 2, y, x + 2, y + 4], fill=oro_l)
        d.line([x - 2, y, x - 2, y + 4], fill=tinta); d.line([x + 2, y, x + 2, y + 4], fill=tinta); d.line([x - 2, y + 2, x + 2, y + 2], fill=tinta)
        luz(im, x, y + 3, 9, hx("FFF0C0"), 0.22, 2)
    # Zoclo con franja de peligro y piso de placa con rombos.
    for x in range(W):
        for y in (64, 65):
            d.point((x, y), fill=oro_l if ((x + y) // 3) % 2 else tinta)
    degradado(im, 66, H, [metal_s, metal, metal])
    for y in range(67, H, 3):
        for x in range((y // 3 % 2) * 3, W, 6): d.point((x, y), fill=metal_l); d.point((x + 1, y + 1), fill=metal_s)
    big = subir(im)
    dd = ImageDraw.Draw(big)
    dd.text((218 * ESC, 21.5 * ESC), "TODO EN ORDEN", font=fuente("DejaVuSansMono-Bold.ttf", 15), fill=oro_l, anchor="mm")
    dd.text((102 * ESC, 41.2 * ESC), "¡A SALVO!", font=fuente("DejaVuSansMono-Bold.ttf", 11), fill=oro_l, anchor="mm")
    dd.text((cx * ESC, cy * ESC + 1), "21", font=fuente("DejaVuSans-Bold.ttf", 30), fill=tinta, anchor="mm")
    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        for k in range(4):
            if (tick // 5 + k * 3) % 7 < 3: cuad(dd, 294 + k * 5 + 1, 34, hx("FFFFFF"), 2, 1)
        n = 3 + int(2.5 + 2.5 * math.sin(tick * 0.2))
        for k in range(n): cuad(dd, 295 + k * 3, 26, hx("3DFF7F"), 2, 2)
    ANIM[("refugio", False)] = [vivo]
    return big


def fondo_refugio_noche(t):
    """La sala del refugio en una terminal de fósforo verde: líneas que brillan sobre negro, texto y cursor."""
    Wb, Hb = W * ESC, H * ESC
    v = lambda k: mezcla((0, 0, 0), hx("3DFF7F"), k)
    im, d = lienzo((0, 0, 0))
    # Perspectiva del pasillo: piso en rejilla y costillas de la bóveda.
    hor = 40
    for k in range(1, 8):
        y = hor + (H - hor) * (k / 7) ** 1.7
        d.line([0, y, W, y], fill=v(0.18))
    for xb in range(-600, 1012, 34):
        d.line([206 + (xb - 206) * 0.08, hor, xb, H], fill=v(0.14))
    for k, x in enumerate((10, 74, 138, 202, 266)):
        d.arc([x - 4, 8, x + 64, 112], 180, 360, fill=v(0.35))
        d.line([x - 4, 60, x - 4, 64], fill=v(0.35))
    d.line([0, 8, W, 8], fill=v(0.5)); d.line([0, 10, W, 10], fill=v(0.25))
    # Puerta en engrane, dibujada en líneas.
    cx, cy = 368, 36
    for k in range(20):
        an = 2 * math.pi * k / 20
        d.line([cx + 22 * math.cos(an), cy + 22 * math.sin(an), cx + 26 * math.cos(an), cy + 26 * math.sin(an)], fill=v(0.7))
    d.ellipse([cx - 22, cy - 22, cx + 22, cy + 22], outline=v(0.85)); d.ellipse([cx - 16, cy - 16, cx + 16, cy + 16], outline=v(0.4))
    d.ellipse([cx - 4, cy - 4, cx + 4, cy + 4], outline=v(0.85))
    # Cartel del átomo en líneas.
    ax, ay = 102, 28
    for a in (0, 60, 120):
        e = Image.new("L", (W, H), 0); ImageDraw.Draw(e).ellipse([ax - 10, ay - 3, ax + 10, ay + 3], outline=255)
        e = e.rotate(a, center=(ax, ay)); m_ = e.load(); pxx = im.load()
        for y in range(ay - 11, ay + 12):
            for x in range(ax - 11, ax + 12):
                if m_[x, y] > 100: pxx[x, y] = v(0.75)
    d.ellipse([ax - 1, ay - 1, ax + 1, ay + 1], fill=v(1.0))
    d.rectangle([ax - 14, ay - 14, ax + 14, ay + 14], outline=v(0.35))
    # Panel lateral con barras.
    d.rectangle([290, 20, 318, 50], outline=v(0.45))
    big = im.resize((Wb, Hb), Image.NEAREST)
    # Brillo del fósforo y pixeles visibles.
    glow = big.filter(ImageFilter.GaussianBlur(3))
    big = ImageChops.add(big, ImageChops.multiply(glow, Image.new("RGB", big.size, (150, 150, 150))))
    a = np.asarray(big, np.float32)
    yy, xx = np.mgrid[0:Hb, 0:Wb]
    rej = np.where((xx % ESC == ESC - 1) | (yy % ESC == ESC - 1), 0.6, 1.0)
    big = Image.fromarray(np.clip(a * rej[..., None] + np.array(hx("061009"), np.float32), 0, 255).astype(np.uint8))
    capa = Image.new("RGB", big.size, (0, 0, 0)); dd = ImageDraw.Draw(capa)
    f = fuente("DejaVuSansMono-Bold.ttf", 15)
    dd.text((12, 4), "REFUGIO 21 :: SECTOR C :: TODO EN ORDEN", font=f, fill=v(1.0))
    dd.text((Wb - 12, 4), "O2 98%  RAD 0.02", font=f, fill=v(0.8), anchor="ra")
    dd.text((12, Hb - 21), "> estado del habitante: TRANQUILO", font=fuente("DejaVuSansMono-Bold.ttf", 14), fill=v(0.75))
    capa = ImageChops.add(capa, ImageChops.multiply(capa.filter(ImageFilter.GaussianBlur(3)), Image.new("RGB", capa.size, (160, 160, 160))))
    big = ImageChops.lighter(big, capa)
    ancho_txt = ImageDraw.Draw(capa).textlength("> estado del habitante: TRANQUILO", font=fuente("DejaVuSansMono-Bold.ttf", 14))
    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        if (tick // 6) % 2 == 0:
            dd.rectangle([12 + ancho_txt + 4, Hb - 20, 12 + ancho_txt + 12, Hb - 6], fill=v(0.9))
        for k in range(5):
            h_ = 4 + 20 * (0.5 + 0.5 * math.sin(tick * 0.15 + k * 1.3))
            dd.rectangle([(293 + k * 5) * ESC, (48 - h_ * 0.9) * ESC, (296 + k * 5) * ESC - 2, 48 * ESC], fill=v(0.55 + 0.08 * k))
        an = tick * 0.08                           # la puerta gira despacio, como si se abriera
        for k in range(4):
            a_ = an + k * math.pi / 2
            dd.line([cx * ESC, cy * ESC, (cx + 15 * math.cos(a_)) * ESC, (cy + 15 * math.sin(a_)) * ESC], fill=v(0.6), width=2)
    ANIM[("refugio", True)] = [vivo]
    return big


# =====================================================================
#  Dos tintas (distopía y soviético). De noche: serigrafía en negativo, con tinta crema y roja sobre papel negro.
# =====================================================================

def a_tintas(t, m, negativo):
    """Pinta una imagen de «cuánta tinta» con el pintor de dos tintas; en negativo, lo claro se vuelve tinta."""
    a = np.asarray(m).copy()
    if negativo:
        r, g_, b = a[..., 0].astype(int), a[..., 1].astype(int), a[..., 2].astype(int)
        rojo = (r > g_ + 50) & (r > b + 40)
        inv = 255 - a
        a = np.where(rojo[..., None], a, inv)
    items = [(y, x, tuple(int(v) for v in a[y, x])) for y in range(H) for x in range(W)]
    capa = g8.pintar_dos_tintas(None, t, ESC, celdas_iter=items, ancho=W, alto=H, semilla=2)
    base = g8.papel(Image.new("RGB", capa.size, t["tintas"]["papel"]), 0.05)
    base.paste(capa, (0, 0), capa)
    return base


def trama_viva(img, t, celdas_, negativo=False):
    """Dibuja unas celdas (x, y, gris o 'rojo') con la trama de puntos directamente en la banda (para lo que se mueve)."""
    d = ImageDraw.Draw(img); s = ESC
    for x, y, gr in celdas_:
        if gr == "rojo":
            d.rectangle([x * s + 0.4, y * s - 0.3, x * s + s, y * s + s - 0.3], fill=t["tintas"]["rojo"]); continue
        k = gr / 255 if negativo else 1 - gr / 255
        if k <= 0.05: continue
        rr = s * 0.62 * math.sqrt(min(1.0, k * 1.2))
        d.ellipse([x * s + s / 2 - rr, y * s + s / 2 - rr, x * s + s / 2 + rr, y * s + s / 2 + rr], fill=t["tintas"]["negro"])


def fondo_distopia(t):
    o = t["oscuro"]
    g_ = lambda v: (v, v, v)
    R = hx("B3121B")
    m, d = lienzo(g_(236))
    # Cielo: de día, trama que se aclara hacia el horizonte; de noche, negro con resplandor de la ciudad abajo.
    for y in range(0, 66):
        v = (176 + y * 0.9) if not o else (40 + y * 0.9)
        d.line([0, y, W, y], fill=g_(int(v)))
    # Bloques de vivienda iguales a los lados.
    rnd = random.Random(2)
    for x0 in list(range(0, 118, 24)) + list(range(300, W, 24)):
        h = rnd.randint(26, 40)
        d.rectangle([x0, 66 - h, x0 + 20, 66], fill=g_(150 if not o else 12))
        for yy in range(66 - h + 3, 64, 4):
            for xx in range(x0 + 2, x0 + 19, 3):
                lit = o and rnd.random() < 0.18
                d.point((xx, yy), fill=g_(236) if not o else (g_(230) if lit else g_(20)))
    # El Ministerio: pirámide escalonada con ventanas en hilera.
    tiers = [(118, 300, 34, 66), (138, 280, 22, 34), (158, 260, 12, 22), (180, 238, 4, 12)]
    for k, (a, b, y0, y1) in enumerate(tiers):
        d.rectangle([a, y0, b, y1], fill=g_(150 if not o else 10))
        d.rectangle([a, y0, b, y0], fill=g_(236 if not o else 120))
        for yy in range(y0 + 3, y1 - 1, 3):
            for xx in range(a + 3, b - 2, 4):
                lit = o and (xx * 7 + yy * 3) % 11 == 0
                d.rectangle([xx, yy, xx + 1, yy], fill=g_(14) if not o else (g_(225) if lit else g_(10)))
    # Estandartes rojos con el ojo, y el gran ojo arriba.
    for bx in (150, 268):
        d.rectangle([bx - 8, 22, bx + 8, 58], fill=R)
        d.polygon([(bx - 6, 36), (bx, 32), (bx + 6, 36), (bx, 40)], fill=g_(236) if not o else g_(20))
        d.ellipse([bx - 2, 34, bx + 2, 38], fill=g_(14) if not o else g_(230)); d.point((bx - 1, 35), fill=g_(236) if not o else g_(20))
        d.rectangle([bx - 8, 54, bx + 8, 58], fill=g_(14) if not o else g_(220))
    ex, ey = 209, 8
    d.polygon([(ex - 16, ey), (ex, ey - 6), (ex + 16, ey), (ex, ey + 6)], fill=g_(236) if not o else g_(20))
    d.ellipse([ex - 5, ey - 5, ex + 5, ey + 5], fill=R); d.ellipse([ex - 2, ey - 2, ex + 2, ey + 2], fill=g_(14) if not o else g_(230))
    for k in range(9):
        an = math.pi + math.pi * k / 8
        d.line([ex + 19 * math.cos(an), ey + 9 * math.sin(an), ex + 25 * math.cos(an), ey + 13 * math.sin(an)], fill=g_(14) if not o else g_(200))
    # Franja roja con el lema, bajo el Ministerio.
    d.rectangle([118, 60, 300, 66], fill=R)
    # Poste con altavoces y poste con cámara.
    for x in (52,):
        d.line([x, 72, x, 18], fill=g_(14) if not o else g_(150))
        d.polygon([(x, 20), (x - 8, 16), (x - 8, 26)], fill=g_(14) if not o else g_(150)); d.polygon([(x, 20), (x + 8, 16), (x + 8, 26)], fill=g_(14) if not o else g_(150))
    d.line([360, 72, 360, 24], fill=g_(14) if not o else g_(150))
    # Piso de losas con juntas y una línea roja de fila.
    for y in range(66, H):
        d.line([0, y, W, y], fill=g_(196 if not o else 40))
    for y in (66, 70, 75): d.line([0, y, W, y], fill=g_(14) if not o else g_(110))
    for x in range(0, W, 16): d.line([x, 66, x - 3, H], fill=g_(14) if not o else g_(110))
    d.line([0, 78, W, 78], fill=R)
    if o:
        for (x, y) in ((150, 40), (268, 40)):
            pass
    big = a_tintas(t, m, o)
    dd = ImageDraw.Draw(big)
    papel_ = t["tintas"]["papel"] if not o else t["tintas"]["negro"]
    dd.text((209 * ESC, 63.3 * ESC), "EL OJO TE CUIDA", font=fuente("DejaVuSans-Bold.ttf", 15), fill=papel_ if not o else hx("E8E6E0"), anchor="mm")
    dd.text((209 * ESC, 63.3 * ESC), "EL OJO TE CUIDA", font=fuente("DejaVuSans-Bold.ttf", 15), fill=hx("ECE9E1") if not o else hx("E8E6E0"), anchor="mm")
    # Lo que se mueve: la cámara que voltea y su luz roja; de noche, un reflector que barre el cielo.
    haces = {}
    if o:
        for k in range(12):
            an = math.radians(-150 + 40 * math.sin(k / 12 * 2 * math.pi))
            cel = []
            for y in range(0, 60):
                for x in range(W):
                    dx, dy = x - 330, y - 62
                    if dy >= 0: continue
                    a_ = math.atan2(dy, dx); dist = math.hypot(dx, dy)
                    diff = abs((a_ - an + math.pi) % (2 * math.pi) - math.pi)
                    if diff < 0.05 + 0.0 * dist and dist < 120:
                        cel.append((x, y, int(255 * (0.55 - dist / 120 * 0.4))))
            haces[k] = cel
    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        ink = t["tintas"]["negro"]
        if haces:
            for x, y, gr in haces[(tick // 3) % 12]:
                k = gr / 255; rr = ESC * 0.55 * math.sqrt(k)
                dd.ellipse([x * ESC + 1.5 - rr, y * ESC + 1.5 - rr, x * ESC + 1.5 + rr, y * ESC + 1.5 + rr], fill=ink)
        mira = 1 if (tick // 40) % 2 else -1
        cuad(dd, 357, 22, ink, 7, 4) if mira > 0 else cuad(dd, 357 - 4, 22, ink, 7, 4)
        cuad(dd, 362 if mira > 0 else 351, 22.5, ink, 2, 3)
        if (tick // 5) % 2: cuad(dd, 358 if mira > 0 else 357, 22.5, t["tintas"]["rojo"], 1, 1)
    ANIM[("distopia", o)] = [vivo]
    return big


def fondo_urss(t):
    o = t["oscuro"]
    g_ = lambda v: (v, v, v)
    R = hx("C1121F")
    m, d = lienzo(g_(240) if not o else g_(14))
    tinta_ = g_(14) if not o else g_(235)          # el color «de la tinta» (claro de noche porque se invierte)
    cx, cy = 300, 74
    # Rayos de sol constructivistas.
    for k in range(22):
        a0 = math.pi + k * math.pi / 22
        if k % 2 == 0:
            d.polygon([(cx, cy), (cx + 500 * math.cos(a0), cy + 500 * math.sin(a0)),
                       (cx + 500 * math.cos(a0 + math.pi / 22), cy + 500 * math.sin(a0 + math.pi / 22))], fill=g_(182) if not o else g_(78))
    d.ellipse([cx - 28, 18, cx + 28, 74], fill=R)
    # Torre de Shújov: secciones de hiperboloide con celosía.
    secciones = [(74, 52, 18, 14), (52, 34, 14, 10.5), (34, 19, 10.5, 7.5), (19, 7, 7.5, 5)]
    for (yb, yt, rb, rt) in secciones:
        for k in range(16):
            a = 2 * math.pi * k / 16
            for tw in (0.9, -0.9):
                x0 = cx + rb * math.cos(a); x1 = cx + rt * math.cos(a + tw)
                if math.sin(a) < 0 and math.sin(a + tw) < 0: continue
                d.line([x0, yb, x1, yt], fill=tinta_)
        d.line([cx - rb, yb, cx + rb, yb], fill=tinta_); d.line([cx - rt, yt, cx + rt, yt], fill=tinta_)
    d.line([cx, 7, cx, 1], fill=tinta_)
    star = [(cx + (4 if k % 2 == 0 else 1.7) * math.cos(-math.pi / 2 + k * math.pi / 5), 3 + (4 if k % 2 == 0 else 1.7) * math.sin(-math.pi / 2 + k * math.pi / 5)) for k in range(10)]
    # Fábrica con techo de diente de sierra y chimeneas.
    fab = g_(30) if not o else g_(30)
    d.rectangle([0, 46, 196, 74], fill=g_(110) if not o else g_(24))
    for x in range(0, 196, 14):
        d.polygon([(x, 46), (x + 10, 36), (x + 10, 46)], fill=tinta_ if not o else g_(24)); d.line([x + 10, 36, x + 14, 46], fill=tinta_)
        d.rectangle([x + 9, 38, x + 9, 45], fill=g_(236) if not o else g_(200))
    for y in (52, 60):
        for x in range(4, 192, 6):
            d.rectangle([x, y, x + 3, y + 4], fill=g_(236) if not o else (g_(220) if (x * 3 + y) % 5 < 2 else g_(30)))
    for x, h in ((26, 6), (58, 2), (150, 10)):
        d.rectangle([x, h, x + 6, 46], fill=tinta_ if not o else g_(28))
        if o: d.line([x, h, x, 46], fill=g_(120))
        for yy in (h + 3, h + 8): d.rectangle([x, yy, x + 6, yy + 1], fill=R)
    # Cuña roja con el lema.
    d.polygon([(0, 8), (176, 2), (176, 15), (0, 21)], fill=R)
    # Engrane.
    gx, gy = 380, 62
    for k in range(12):
        a = 2 * math.pi * k / 12
        d.rectangle([gx + 15 * math.cos(a) - 2, gy + 15 * math.sin(a) - 2, gx + 15 * math.cos(a) + 2, gy + 15 * math.sin(a) + 2], fill=tinta_)
    d.ellipse([gx - 14, gy - 14, gx + 14, gy + 14], fill=tinta_); d.ellipse([gx - 6, gy - 6, gx + 6, gy + 6], fill=g_(240) if not o else g_(14))
    # Reflectores (de noche) cruzados.
    if o:
        for (sx, an) in ((110, -60), (250, -118)):
            a = math.radians(an)
            for y in range(0, 74):
                for x in range(W):
                    dx, dy = x - sx, y - 74
                    if dy >= 0: continue
                    if abs((math.atan2(dy, dx) - a + math.pi) % (2 * math.pi) - math.pi) < 0.06:
                        v0 = m.getpixel((x, y))
                        if v0[0] == v0[1] and v0[0] < 60: m.putpixel((x, y), g_(110))
    # Vía del tren y franja roja con rayas.
    d.line([0, 74, W, 74], fill=tinta_)
    d.rectangle([0, 75, W, H], fill=R)
    for x in range(-10, W, 7): d.line([x, H, x + 5, 75], fill=tinta_)
    d.polygon(star, fill=R)
    big = a_tintas(t, m, o)
    dd = ImageDraw.Draw(big)
    claro_ = t["tintas"]["papel"] if not o else hx("E8D9B5")
    dd.text((86 * ESC, 11.3 * ESC), "ПЯТИЛЕТКА · ПЛАН", font=fuente("DejaVuSans-Bold.ttf", 21), fill=claro_ if not o else hx("161212"), anchor="mm")
    def vivo(img, tick, delante):
        if delante: return
        cel = []
        for j, (x, h) in enumerate(((26, 6), (58, 2), (150, 10))):
            for k in range(6):
                f = ((tick * 0.05 + k / 6 + j * 0.3) % 1)
                yy = h - 1 - f * 10; xx = x + 3 + f * 18
                for dx in range(-1 - int(f * 3), 2 + int(f * 3)):
                    for dy_ in (-1, 0, 1):
                        cel.append((round(xx + dx), round(yy + dy_), 120 + int(f * 100) if not o else 140 - int(f * 90)))
        trama_viva(img, t, [(x, y, gr) for x, y, gr in cel if 0 <= y < H and 0 <= x < W], negativo=o)
    ANIM[("urss", o)] = [vivo]
    return big


# =====================================================================
#  Tierras Antiguas: de día, el camino de la compañía (aldea, bosque, montañas con la boca de una mina y una piedra rúnica
#  que cuenta las lunas). De noche, el salón de piedra bajo la montaña: runas que brillan y gemas en los muros.
# =====================================================================

def runa(d, x, y, semilla, col, h=5):
    rnd = random.Random(semilla)
    d.line([x, y, x, y + h], fill=col)
    for _ in range(rnd.randint(1, 2)):
        a = rnd.choice([(0, 0, 2, 2), (0, 2, 2, 0), (0, 1, 2, 3), (0, 3, 2, 1), (0, 0, 2, 1)])
        d.line([x + a[0], y + a[1], x + a[2], y + a[3]], fill=col)


def fondo_antiguas(t):
    o = t["oscuro"]
    if o:
        return fondo_antiguas_noche(t)
    im, d = lienzo(hx("E9E3D4"))
    degradado(im, 0, 44, [hx("7FA2C0"), hx("9DB8CC"), hx("C3D2D8"), hx("E2E3DA")])
    for x, y, w in ((60, 10, 30), (230, 6, 26)): nube(d, x, y, w, hx("FFFFFF"), hx("EEF0EE"), hx("C9D3DC"))
    # Montañas: la lejana azulada y la cercana de roca, con la boca de la mina.
    perfil(im, [min(44, 18 + abs(((x * 0.9) % 60) - 30) * 0.9 + 6 * math.sin(x / 33)) for x in range(W)], hx("A7B5C2"), borde=hx("C9D3DC"))
    roca, roca_l, roca_s = hx("8C887C"), hx("B9B09A"), hx("5E5A50")
    ys = [min(50, 22 + abs(x - 330) * 0.42 + 3 * math.sin(x / 7)) for x in range(W)]
    px = im.load()
    for x in range(250, W):
        for y in range(round(ys[x]), 52):
            px[x, y] = roca_l if x < 330 and (y - ys[x]) < 6 else (roca if x < 330 else roca_s)
            if y - ys[x] < 3 and ys[x] < 30: px[x, y] = hx("F4F6F8")
    mx, my = 352, 51
    d.rectangle([mx - 8, my - 13, mx + 8, my], fill=hx("1E1D1A"))
    d.line([mx - 9, my, mx - 9, my - 14], fill=hx("7A5A2E"), width=2); d.line([mx + 9, my, mx + 9, my - 14], fill=hx("7A5A2E"), width=2)
    d.line([mx - 10, my - 14, mx + 10, my - 14], fill=hx("7A5A2E"), width=2)
    luz(im, mx, my - 4, 7, hx("F0A040"), 0.35, 2)
    d.line([mx - 30, 62, mx - 6, 51], fill=hx("57534A")); d.line([mx - 26, 62, mx - 2, 51], fill=hx("57534A"))
    d.rectangle([mx - 22, 52, mx - 12, 57], fill=hx("57534A")); d.rectangle([mx - 21, 50, mx - 13, 52], fill=hx("9A7B2E"))
    # Lomas y bosque, con una atalaya en ruinas.
    perfil(im, ondas(31, 52, ((3, 140), (1.5, 50))), hx("7E9A6A"), borde=hx("9DB585"))
    tx_ = 18
    d.rectangle([tx_ - 4, 30, tx_ + 4, 52], fill=hx("A9A08A")); d.rectangle([tx_ + 1, 30, tx_ + 4, 52], fill=hx("8C887C"))
    for k in range(-4, 5, 2): d.rectangle([tx_ + k, 28 - (k % 4 == 0) - (2 if k == 2 else 0), tx_ + k, 30], fill=hx("A9A08A"))
    d.rectangle([tx_ - 1, 36, tx_, 39], fill=hx("3E3A32")); d.rectangle([tx_ - 1, 44, tx_, 47], fill=hx("3E3A32"))
    for k in range(6):
        d.ellipse([60 + k * 9 - 4, 47 - (k % 2) * 2 - 4, 60 + k * 9 + 4, 47 - (k % 2) * 2 + 4], fill=hx("4F7A55") if k % 2 else hx("3E6A4A"))
        d.ellipse([60 + k * 9 - 3, 47 - (k % 2) * 2 - 4, 60 + k * 9 + 1, 47 - (k % 2) * 2 - 1], fill=hx("6E9A6A"))
    rnd = random.Random(8)
    for k in range(40):                         # pinos del bosque
        x = 130 + k * 3 + rnd.randint(-2, 2); h = rnd.randint(8, 15); y = 60 + rnd.randint(-3, 2)
        d.polygon([(x, y - h), (x - 4, y), (x + 4, y)], fill=hx("1F3A28") if k % 3 else hx("2E5A3E"))
        d.line([x, y - h, x - 3, y - 1], fill=hx("3E6A4A"))
    # Aldea: casitas redondas de piedra con techo de paja y humo.
    casas = [(40, 62, 14), (66, 63, 18), (96, 61, 12)]
    for x, y, w in casas:
        d.rectangle([x - w / 2, y - w * 0.45, x + w / 2, y], fill=hx("B9B09A"))
        for yy in range(int(y - w * 0.45) + 1, int(y), 2):
            for xx in range(int(x - w / 2) + (yy % 4) // 2, int(x + w / 2), 3): d.point((xx, yy), fill=hx("8C887C"))
        d.polygon([(x - w / 2 - 2, y - w * 0.45), (x, y - w * 1.05), (x + w / 2 + 2, y - w * 0.45)], fill=hx("C2A35A"))
        d.polygon([(x, y - w * 1.05), (x + w / 2 + 2, y - w * 0.45), (x + 1, y - w * 0.45)], fill=hx("9A7B2E"))
        for k in range(3): d.line([x - w / 2 + k * w / 3, y - w * 0.5, x - w / 2 + k * w / 3 + 3, y - w * 0.62], fill=hx("9A7B2E"))
        d.rectangle([x - 2, y - 6, x + 2, y], fill=hx("57341E")); d.point((x - 4, y - 5), fill=hx("F2D27A"))
    # Camino de lajas.
    d.rectangle([0, 66, W, H], fill=hx("8DA06A"))
    for y in range(66, H): d.line([0, y, W, y], fill=mezcla(hx("8DA06A"), hx("6E8450"), (y - 66) / 14))
    rnd = random.Random(4)
    for y0, hh in ((70, 3), (73, 3), (76, 4)):
        x = rnd.randint(-4, 0)
        while x < W:
            w = rnd.randint(5, 9)
            d.rounded_rectangle([x, y0, x + w - 2, y0 + hh - 1], radius=1, fill=rnd.choice((hx("B9B09A"), hx("C9C1AA"), hx("A9A08A"))))
            d.line([x + 1, y0, x + w - 3, y0], fill=hx("DCD5C3")); d.line([x + 1, y0 + hh - 1, x + w - 3, y0 + hh - 1], fill=hx("7E786A"))
            x += w
    # Piedra rúnica con las fases de la luna.
    sx, sy = 196, 69
    d.polygon([(sx - 7, sy), (sx - 8, sy - 22), (sx - 4, sy - 30), (sx + 4, sy - 31), (sx + 8, sy - 24), (sx + 7, sy)], fill=hx("8C887C"))
    d.polygon([(sx + 2, sy - 31), (sx + 4, sy - 31), (sx + 8, sy - 24), (sx + 7, sy), (sx + 3, sy)], fill=hx("6E695E"))
    d.line([sx - 7, sy - 22, sx - 4, sy - 30], fill=hx("B9B09A"))
    for k in range(5):
        yy = sy - 27 + k * 5
        d.ellipse([sx - 3, yy, sx + 1, yy + 4], fill=hx("DCD5C3"))
        f = k / 4
        d.ellipse([sx - 3 + f * 4 - 2, yy, sx + 1 + f * 4 - 2, yy + 4], fill=hx("57534A")) if k not in (2,) else None
    for k in range(4): runa(d, sx + 3, sy - 26 + k * 6, k + 40, hx("3E3A32"), 4)
    for k in range(6): d.point((sx - 6 + k * 2, sy - 1 - (k % 2)), fill=hx("6E8450"))
    big = subir(im)
    def vivo(img, tick, delante):
        if delante: return
        for x, y, w in casas:
            humo(img, tick, x + 1, y - w * 1.05, hx("F4F2EC"), hx("C3D2D8"), semilla=x)
        dd = ImageDraw.Draw(img)
        a = tick * 0.05
        for k in range(3):                           # cuervos a lo lejos
            bx, by = (80 + k * 14 + tick * 0.6) % W, 20 + k * 3 + math.sin(a + k) * 2
            ala = 1 if (tick // 3 + k) % 2 else 0
            cuad(dd, bx - 1, by - ala, hx("2A2622")); cuad(dd, bx, by, hx("2A2622")); cuad(dd, bx + 1, by - ala, hx("2A2622"))
    ANIM[("antiguas", False)] = [vivo]
    return big


def fondo_antiguas_noche(t):
    piedra = [hx("101113"), hx("151618"), hx("1F2023"), hx("2A2B2F"), hx("3A3B41"), hx("4A4B52")]
    oro, oro_l, rubi, verde = hx("9A7B2E"), hx("D9BC6A"), hx("E06A72"), hx("8CC79A")
    im, d = lienzo(piedra[1])
    # Muro de sillares.
    rnd = random.Random(5)
    for y0 in range(10, 66, 6):
        x = -((y0 // 6) % 2) * 6
        while x < W:
            w = rnd.randint(10, 14)
            d.rectangle([x, y0, x + w - 1, y0 + 5], fill=rnd.choice(piedra[2:4]))
            d.line([x, y0, x + w - 2, y0], fill=piedra[4]); d.line([x + w - 1, y0, x + w - 1, y0 + 5], fill=piedra[0]); d.line([x, y0 + 5, x + w - 1, y0 + 5], fill=piedra[0])
            x += w
    # Arco del fondo con el resplandor de la fragua.
    ax = 206
    d.rectangle([ax - 20, 24, ax + 20, 66], fill=piedra[0]); d.pieslice([ax - 20, 4, ax + 20, 44], 180, 360, fill=piedra[0])
    for k in range(7): d.line([ax - 14 + k * 1, 66 - k * 3, ax + 14 - k * 1, 66 - k * 3], fill=mezcla(piedra[0], hx("C2552A"), 0.5 - k * 0.06))
    luz(im, ax, 60, 22, hx("FF8A3A"), 0.35, 4, ry=14)
    d.arc([ax - 22, 2, ax + 22, 46], 180, 360, fill=piedra[5])
    # Friso con runas y las fases de la luna, incrustadas en plata.
    d.rectangle([0, 0, W, 9], fill=piedra[3]); d.line([0, 9, W, 9], fill=piedra[0]); d.line([0, 0, W, 0], fill=piedra[5])
    lunas = []
    for k in range(13):
        x = 12 + k * 32
        f = (k % 8) / 8
        d.ellipse([x - 3, 1, x + 3, 7], fill=hx("C9CCD6"))
        if f != 0.5:
            sh = (f - 0.5) * 12
            d.ellipse([x - 3 + sh, 1, x + 3 + sh, 7], fill=piedra[3])
        lunas.append(x)
    runas = []
    for k in range(W // 6):
        x = 4 + k * 6
        if any(abs(x - l) < 6 for l in lunas): continue
        runa(d, x, 2, k, oro, 4); runas.append(x)
    # Columnas talladas con nudos.
    for x in (40, 120, 292, 372):
        d.rectangle([x - 7, 10, x + 7, 66], fill=piedra[3])
        d.line([x - 7, 10, x - 7, 66], fill=piedra[5]); d.line([x - 6, 10, x - 6, 66], fill=piedra[4]); d.line([x + 7, 10, x + 7, 66], fill=piedra[0])
        for y in (12, 60):
            d.rectangle([x - 9, y, x + 9, y + 4], fill=piedra[4]); d.line([x - 9, y, x + 9, y], fill=piedra[5])
        for y in range(20, 58, 6):
            d.line([x - 5, y, x + 5, y + 4], fill=piedra[1]); d.line([x - 5, y + 4, x + 5, y], fill=piedra[1])
        d.rectangle([x - 2, 34, x + 2, 38], fill=rubi if x in (40, 292) else verde)
    # Linternas colgantes.
    linternas = [(80, 18), (164, 20), (250, 20), (334, 18)]
    for x, y in linternas:
        d.line([x, 10, x, y], fill=piedra[0]); d.rectangle([x - 2, y, x + 2, y + 5], fill=hx("F2D27A"), outline=hx("2A2014"))
        luz(im, x, y + 3, 13, hx("F2B45A"), 0.28, 3)
    # Gemas en el muro.
    gemas = []
    rnd2 = random.Random(12)
    for _ in range(26):
        x, y = rnd2.randrange(4, W - 4), rnd2.randrange(14, 62)
        col = rnd2.choice([rubi, verde, oro_l, hx("8FB3E0")])
        d.point((x, y), fill=col); d.point((x + 1, y), fill=mezcla(col, piedra[0], 0.4)); gemas.append((x, y, col))
    # Piso de losas pulidas con el reflejo de las linternas.
    d.rectangle([0, 66, W, H], fill=piedra[2])
    for y in (66, 70, 75): d.line([0, y, W, y], fill=piedra[0])
    for x in range(0, W, 14): d.line([x + (4 if x % 28 else 0), 66, x + (4 if x % 28 else 0) - 2, H], fill=piedra[0])
    for x, y in linternas: luz(im, x, 72, 16, hx("F2B45A"), 0.2, 3, ry=4)
    big = subir(im)
    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        for j, x in enumerate(runas):                # las runas se encienden en ola
            k = 0.5 + 0.5 * math.sin(tick * 0.12 - j * 0.25)
            if k > 0.75:
                c = mezcla(oro, hx("FFE8A0"), (k - 0.75) * 4)
                rnd_ = random.Random(j)
                cuad(dd, x, 2, c, 1, 5)
        for j, (x, y, col) in enumerate(gemas):     # destellos de las gemas
            if (j * 7 + tick // 2) % 23 == 0:
                cuad(dd, x, y, hx("FFFFFF")); cuad(dd, x - 1, y, mezcla(col, hx("FFFFFF"), 0.5)); cuad(dd, x + 1, y, mezcla(col, hx("FFFFFF"), 0.5))
                cuad(dd, x, y - 1, mezcla(col, hx("FFFFFF"), 0.5)); cuad(dd, x, y + 1, mezcla(col, hx("FFFFFF"), 0.5))
        rnd_ = random.Random(tick // 3)
        for x, y in linternas:
            if rnd_.random() < 0.3: cuad(dd, x - 1, y + 1, hx("FFF0B0"), 3, 3)
    ANIM[("antiguas", True)] = [vivo]
    return big


FONDOS9 = {"heleno": fondo_heleno, "mongol": fondo_mongol, "colonos": fondo_colonos, "revolucion": fondo_revolucion, "napoleon": fondo_napoleon,
           "laboratorio": fondo_laboratorio, "refugio": fondo_refugio, "distopia": fondo_distopia, "urss": fondo_urss, "antiguas": fondo_antiguas}


def vista(nombres, archivo):
    """Un cuadro fijo de cada fondo (claro y oscuro) con el gato, ×3, para revisar."""
    filas = []
    for n in nombres:
        for o in (False, True):
            t = tema(n, o)
            f = FONDOS9[n](t)
            c = ("caminar", 2, 120.0, 1, 0.0)
            img = cuadro_banda(n, o, t, f, c, 5, None)
            img = cuadro_banda(n, o, t, img, ("sentado", 0, 300.0, -1, 0.0), 5, None)
            filas.append(img)
    hoja = Image.new("RGB", (W * ESC, len(filas) * (H * ESC + 6)), (250, 249, 246))
    for k, f in enumerate(filas):
        hoja.paste(f, (0, k * (H * ESC + 6)))
    hoja.save(AQUI / archivo)


g8.FONDOS.update(FONDOS9)
ETIQ = {n: e for n, _, e in g8.BANDAS}
ETIQ.update(heleno="Helénico · pórtico frente al Egeo", mongol="Mongol · en la estepa", colonos="Colonos · el pueblo que se reconstruye",
            revolucion="Revolución · la plaza de la libertad", napoleon="Napoleónico · el salón del trono", laboratorio="Laboratorio · la mesa de trabajo",
            refugio="Refugio · el atrio del refugio", distopia="Distopía · frente al Ministerio", urss="Soviético · cartel constructivista",
            antiguas="Tierras Antiguas · el camino de la compañía")
NOCHE = dict(heleno="la luna sobre el mar", mongol="la Vía Láctea y la fogata", colonos="faroles y luciérnagas", revolucion="a la luz de las antorchas",
             napoleon="a la luz de las arañas", laboratorio="lo que brilla de noche", refugio="terminal de fósforo verde",
             distopia="serigrafía en negativo, reflector", urss="serigrafía en negativo, reflectores", antiguas="el salón bajo la montaña")


def resumen(cuadros, oscuro, ruta, escala=1.5, columnas=2):
    temas_ = [(n, tema(n, oscuro)) for n in NUEVOS]
    fondos = {n: FONDOS9[n](t) for n, t in temas_}
    bw, bh = int(W * escala), int(H * escala)
    cw, ch_ = bw + 12, bh + 20
    filas = math.ceil(len(temas_) / columnas)
    out = []
    for k, c in enumerate(cuadros):
        lam = Image.new("RGB", (columnas * cw + 10, filas * ch_ + 8), (250, 249, 246) if not oscuro else (24, 24, 28)); dl = ImageDraw.Draw(lam)
        for j, (n, t) in enumerate(temas_):
            b = cuadro_banda(n, oscuro, t, fondos[n], c, k, None).resize((bw, bh), Image.LANCZOS)
            x, y = 10 + (j % columnas) * cw, 6 + (j // columnas) * ch_
            txt = ETIQ[n].split(" · ")[0] + " · " + (ETIQ[n].split(" · ")[1] if not oscuro else NOCHE[n])
            dl.text((x, y), txt, font=fuente("DejaVuSans.ttf", 12), fill=(40, 40, 46) if not oscuro else (220, 220, 214))
            lam.paste(b, (x, y + 15))
        out.append(lam)
    out[0].save(ruta, save_all=True, append_images=out[1:], duration=int(g8.DT * 1000), loop=0, quality=80, method=4)


if __name__ == "__main__":
    que = sys.argv[1:] or ["banda", "resumen"]
    s_, cs = g8.buscar_semilla()
    if "banda" in que:
        for n in NUEVOS:
            g8.gif_banda(cs, [(n, False, ETIQ[n])])
            g8.gif_banda(cs, [(n, True, ETIQ[n].split(" · ")[0] + " · de noche: " + NOCHE[n])], "_oscuro")
    if "resumen" in que:
        resumen(cs, False, AQUI / "fondos_nuevos_claro.webp")
        resumen(cs, True, AQUI / "fondos_nuevos_oscuro.webp")
    print("listo")
