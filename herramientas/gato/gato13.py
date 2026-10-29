"""
Prueba 13: el romano vuelve a como estaba (sin casco, mosaico de antes); colonos (la gaceta grabada) con menos detalle;
soviético rehecho más simple: estrella con la torre de Shújov, la diagonal con el lema y la fábrica.
"""
import inspect
import math
import sys
from pathlib import Path

from PIL import Image, ImageDraw

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato10 as g10      # noqa: E402
import gato11 as g11      # noqa: E402
import gato12 as g12      # noqa: E402
from gato8 import hx, fuente, ESC          # noqa: E402
from gato9 import W, H, ANIM, a_tintas, trama_viva   # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
g8.AQUI = g9.AQUI = g10.AQUI = g11.AQUI = g12.AQUI = AQUI

# ---- Romano: como antes (sin casco, mosaico de la prueba 8).
g8.FONDOS["romano"] = g8.fondo_romano
_tema12 = g8.tema


def tema(nombre, oscuro=False):
    if nombre == "romano":
        return g12._tema10(nombre, oscuro)
    return _tema12(nombre, oscuro)


g8.tema = g9.tema = g10.tema = g12.tema = tema


# ---- Colonos: el mismo grabado con menos detalle (menos achurado, menos casas, sin cuerdas ni rayas en el piso).
def _quitar(fuente_, cambios):
    for viejo, nuevo in cambios:
        assert viejo in fuente_, viejo
        fuente_ = fuente_.replace(viejo, nuevo)
    return fuente_


src = inspect.getsource(g12.fondo_colonos)
src = _quitar(src, [
    ("for y in range(17, 40, 2 if True else 3):\n        if y > 32 and y % 4: continue", "for y in range(17, 24, 2):"),
    ('    achurar(px, lomas, 6, "d", tinta)\n', ""),
    ('        achurar(px, m, 3, "d", tinta)\n', '        achurar(px, m, 4, "d", tinta)\n'),
    ('        achurar(px, m2, 2, "h", tinta)\n', ""),
    ('    casa(100, 26, 14, 10, ag["ocre"])\n', ""),
    ('    casa(304, 30, 16, 11, mezcla(pap, AGUADA["azul"], 0.25))\n', ""),
    ("    d.line([bx + 16, 34, bx + 46, 66], fill=tinta); d.line([bx + 16, 34, bx - 14, 66], fill=tinta)\n", ""),
    ('        achurar(px, m, 2, "d", tinta)\n', ""),
    ('    achurar(px, piso, 3, "h", tinta)\n', '    achurar(px, piso, 5, "h", tinta)\n'),
    ("    for x in range(8, W - 8, 9): d.line([x, 70, x + 2, 69], fill=tinta)\n", ""),
    ('    m = Image.new("L", (W, H), 0); ImageDraw.Draw(m).rectangle([ix + 26, 51, ix + 29, 67], fill=255); achurar(px, m, 2, "h", tinta)\n', ""),
    ("for (tx, ty, r) in ((24, 58, 9), (132, 60, 7), (240, 58, 8), (396, 58, 8)):", "for (tx, ty, r) in ((24, 58, 9), (240, 58, 8), (396, 58, 8)):"),
])
exec(src, g12.__dict__)

src = inspect.getsource(g12.pintar_grabado)
src = _quitar(src, [
    ('            if v in "DTZ": d.line([x0, y0 + s / 2, x0 + s / 2, y0], fill=tin + (255,), width=w)\n', ""),
    ('        elif v == "M" and (r + c) % 2 == 0:', '        elif v == "M" and (r + c) % 4 == 0:'),
    ('        elif v == "A" and (r + c) % 3 == 0:', '        elif False:'),
    ('        if v in "SDTBZ":', '        if v in "SDT" and (r + c) % 2 == 0:'),
])
exec(src, g12.__dict__)
g8.FONDOS["colonos"] = g9.FONDOS9["colonos"] = g12.fondo_colonos


# =====================================================================
#  Soviético, más simple: tres bloques limpios sobre el papel.
#  Izquierda, la estrella roja con la torre de Shújov; al centro, la diagonal negra con el lema;
#  a la derecha, la fábrica con dos chimeneas que echan humo. Abajo, la franja roja.
#  De noche, el mismo cartel en negativo (tinta crema y roja sobre papel negro).
# =====================================================================

def fondo_urss(t):
    o = t["oscuro"]
    g_ = lambda v: (v, v, v)
    R = hx("C1121F")
    tinta_, papel_ = (g_(14), g_(240)) if not o else (g_(235), g_(14))
    m, d = g9.lienzo(papel_)
    # Estrella y torre.
    cx = 72
    d.polygon(g10.estrella5(cx, 40, 34, 13), fill=R)
    for (yb, yt, rb, rt) in ((74, 52, 15, 11.5), (52, 34, 11.5, 8.5), (34, 19, 8.5, 6), (19, 8, 6, 4)):
        for k in range(12):
            a = 2 * math.pi * k / 12
            for tw in (0.9, -0.9):
                if math.sin(a) < 0 and math.sin(a + tw) < 0: continue
                d.line([cx + rb * math.cos(a), yb, cx + rt * math.cos(a + tw), yt], fill=tinta_)
        d.line([cx - rb, yb, cx + rb, yb], fill=tinta_); d.line([cx - rt, yt, cx + rt, yt], fill=tinta_)
    d.line([cx, 8, cx, 2], fill=tinta_)
    # Diagonal negra con el lema.
    d.polygon([(140, 80), (194, 80), (344, -2), (290, -2)], fill=tinta_)
    # Fábrica: techo de dientes de sierra y dos chimeneas con bandas rojas.
    x0f, x1f = 300, 404
    d.rectangle([x0f, 54, x1f, 74], fill=tinta_)
    for x in range(x0f, x1f, 13):
        d.polygon([(x, 54), (x + 10, 45), (x + 10, 54)], fill=tinta_)
    for x in range(x0f + 6, x1f - 4, 12):
        d.rectangle([x, 61, x + 4, 66], fill=papel_ if not o else g_(110))
    chim = ((330, 14), (374, 22))
    for x, h in chim:
        d.rectangle([x, h, x + 6, 54], fill=tinta_)
        d.rectangle([x, h + 4, x + 6, h + 6], fill=R)
    # Suelo: línea y franja roja lisa.
    d.line([0, 74, W, 74], fill=tinta_)
    d.rectangle([0, 75, W, H], fill=R)
    big = a_tintas(t, m, o)
    capa = Image.new("RGBA", (360 * ESC // 2, 40), (0, 0, 0, 0))
    ImageDraw.Draw(capa).text((capa.width // 2, 20), "ВСЕ НА СТРОЙКУ!", font=fuente("DejaVuSans-Bold.ttf", 30), fill=t["tintas"]["papel"] + (255,), anchor="mm")
    capa = capa.rotate(math.degrees(math.atan2(82, 150)), expand=True, resample=Image.BICUBIC)
    big.paste(capa, (int(242 * ESC - capa.width / 2), int(39 * ESC - capa.height / 2)), capa)

    def humo(img, tick, delante):
        if delante: return
        cel = []
        for j, (x, h) in enumerate(chim):
            for k in range(5):
                f = ((tick * 0.045 + k / 5 + j * 0.4) % 1)
                yy = h - 1 - f * 9; xx = x + 3 - f * 14
                for dx in range(-1 - int(f * 2), 2 + int(f * 2)):
                    for dy_ in (-1, 0, 1):
                        cel.append((round(xx + dx), round(yy + dy_), 130 + int(f * 90) if not o else 130 - int(f * 80)))
        trama_viva(img, t, [(x, y, gr) for x, y, gr in cel if 0 <= y < H and 0 <= x < W], negativo=o)
    ANIM[("urss", o)] = [humo]
    return big


g8.FONDOS["urss"] = g9.FONDOS9["urss"] = fondo_urss
g9.ETIQ.update(urss="Soviético · cartel constructivista", colonos="Colonos · una hoja de la gaceta del pueblo")
g9.NOCHE.update(urss="el cartel en negativo", colonos="la gaceta a la luz de una vela")


def vista(archivo):
    filas = []
    for n, o in (("romano", False), ("colonos", False), ("colonos", True), ("urss", False), ("urss", True)):
        t = tema(n, o); f = g8.FONDOS[n](t)
        img = g8.cuadro_banda(n, o, t, f, ("caminar", 2, 120.0, 1, 0.0), 5, None)
        img = g8.cuadro_banda(n, o, t, img, ("sentado", 0, 300.0, -1, 0.0), 5, None)
        filas.append(img)
    W3, H3 = filas[0].size
    hoja = Image.new("RGB", (W3, len(filas) * (H3 + 6)), (250, 249, 246))
    for k, f in enumerate(filas):
        hoja.paste(f, (0, k * (H3 + 6)))
    hoja.save(AQUI / archivo)


if __name__ == "__main__":
    que = sys.argv[1:] or ["banda", "hoja"]
    if "vista" in que:
        vista("_vista.png"); sys.exit()
    s_, cs = g8.buscar_semilla()
    if "banda" in que:
        g8.gif_banda(cs, [("romano", False, g8.BANDAS[0][2])])
        for n in ("colonos", "urss"):
            g8.gif_banda(cs, [(n, False, g9.ETIQ[n])])
            g8.gif_banda(cs, [(n, True, g9.ETIQ[n].split(" · ")[0] + " · de noche: " + g9.NOCHE[n])], "_oscuro")
    if "hoja" in que:
        g8.hoja("colonos", False, "hoja_sprites_colonos.png", material=True)
    print("listo")
