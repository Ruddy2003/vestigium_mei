"""
Prueba 10: se rehacen laboratorio (estética del Instituto), soviético (sin el sol con rayos), Tierras Antiguas (un mapa con
runas lunares) y colonos (asentamiento de los Minutemen). El resto viene de gato9.py y gato8.py.
"""
import math
import random
import sys
from pathlib import Path

from PIL import Image, ImageDraw

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8                      # noqa: E402
import gato9 as g9                      # noqa: E402
from gato8 import hx, mezcla, fuente, ESC                                   # noqa: E402
from gato9 import (W, H, ANIM, lienzo, degradado, perfil, ondas, luz, estrellas, nube, humo, cuad, llama, subir, a_tintas,  # noqa: E402
                   trama_viva, tramar)

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
g8.AQUI = AQUI; g9.AQUI = AQUI
REHECHOS = ["laboratorio", "urss", "antiguas", "colonos"]
POS = {}          # dónde va el gato en el cuadro actual (para la retícula del laboratorio)

_cuadro9 = g9.cuadro_banda


def cuadro_banda(nombre, oscuro, t, fondo, c, tick, extra):
    POS["c"] = c
    return _cuadro9(nombre, oscuro, t, fondo, c, tick, extra)


g8.cuadro_banda = cuadro_banda; g9.cuadro_banda = cuadro_banda

# ---------------------------------------------------------------------
#  Tricornio para el gato de colonos (de lado y de frente/espaldas)
# ---------------------------------------------------------------------
_lado8, _frente8 = g8.sombrero_lado, g8.sombrero_frente


def sombrero_lado(L, cx, cy, inc, som):
    if som != "tricornio":
        return _lado8(L, cx, cy, inc, som)
    a = math.radians(inc)
    P = lambda pts: [g8.rot(cx, cy, x, y, a) for x, y in pts]
    L.elipse("sombrero", *g8.rot(cx, cy, -0.3, -7.2, a), 4.4, 2.8, inc)
    L.poligono("sombrero", P([(-8.4, -3.8), (-7.6, -8.8), (-3.6, -6.8), (0.4, -6.4), (4.4, -7.0), (8.0, -9.4), (8.6, -4.0)]))
    L.capsula("sombrero2", P([(-7.6, -8.6), (-3.6, -6.6), (0.4, -6.2), (4.4, -6.8), (8.0, -9.2)]), 0.6)


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if som != "tricornio":
        return _frente8(L, cx, cy, som, desv)
    dx = desv * 1.6
    L.elipse("sombrero", cx + dx, cy - 9.2, 4.6, 2.6)
    L.poligono("sombrero", [(cx - 9.0 + dx, cy - 8.0), (cx + dx, cy - 3.6), (cx + 9.0 + dx, cy - 8.0), (cx + 5.0 + dx, cy - 10.0), (cx + dx, cy - 8.6), (cx - 5.0 + dx, cy - 10.0)])
    L.capsula("sombrero2", [(cx - 8.6 + dx, cy - 8.0), (cx + dx, cy - 3.9), (cx + 8.6 + dx, cy - 8.0)], 0.6)


g8.sombrero_lado, g8.sombrero_frente = sombrero_lado, sombrero_frente

_tema9 = g9.tema


def tema(nombre, oscuro=False):
    t = _tema9(nombre, oscuro)
    if nombre == "colonos":
        col = dict(t["col"])
        col.update(M=hx("1F3A5F") if not oscuro else hx("2E4E7E"), Z=hx("F8F3E8") if not oscuro else hx("E3D9C2"))
        t = dict(t, col=col, acc=("panuelo", "tricornio"))
    return t


g8.tema = tema; g9.tema = tema


# =====================================================================
#  Laboratorio: cámara de observación del Instituto. Paneles blancos curvos, luz fría en franjas, un ventanal al
#  laboratorio (brazo de ensamblado, cápsula, árbol de interior) y una retícula que sigue y mide al gato.
#  De noche, el ciclo nocturno: paneles en penumbra y solo brillan las franjas, la cápsula y la retícula.
# =====================================================================

def fondo_laboratorio(t):
    o = t["oscuro"]
    if not o:
        panel = [hx("C9D3DC"), hx("DFE6EC"), hx("EEF2F5"), hx("FAFCFD")]
        junta, acero, acero_l = hx("AEBBC6"), hx("6E7C88"), hx("A9B6C1")
        franja, franja_l = hx("8FDDEB"), hx("D6F5FA")
        interior, interior_s = hx("DCE8EE"), hx("C3D5DE")
        cian, verde, verde_l, viol, tinta = hx("0B8FA1"), hx("2E8A5A"), hx("6CB884"), hx("5E4FD1"), hx("12202B")
        piso = [hx("D2DBE2"), hx("E4EAEF"), hx("F2F5F8")]
    else:
        panel = [hx("0C121A"), hx("121A24"), hx("18222F"), hx("1E2A38")]
        junta, acero, acero_l = hx("0A0F15"), hx("263447"), hx("3A4C62")
        franja, franja_l = hx("3CD2E5"), hx("A8F0F8")
        interior, interior_s = hx("0A141E"), hx("07101A")
        cian, verde, verde_l, viol, tinta = hx("3CD2E5"), hx("2A6E4E"), hx("5FD49C"), hx("9D93FF"), hx("E3ECF3")
        piso = [hx("0E151E"), hx("131C27"), hx("18222F")]
    im, d = lienzo(panel[2])
    # Techo con luces redondas empotradas.
    d.rectangle([0, 0, W, 6], fill=panel[0])
    for x in range(26, W, 52):
        d.ellipse([x - 6, 2, x + 6, 5], fill=franja_l if not o else acero_l); d.line([x - 5, 5, x + 5, 5], fill=panel[1])
    # Paneles curvos: cada uno se oscurece arriba y abajo (como un muro que se curva).
    for x0 in range(0, W, 30):
        degradado(im, 8, 34, [panel[1], panel[2], panel[3]], x0 + 1, x0 + 29)
        degradado(im, 34, 62, [panel[3], panel[2], panel[1]], x0 + 1, x0 + 29)
        d.line([x0, 8, x0, 62], fill=junta)
        d.line([x0 + 1, 8, x0 + 1, 62], fill=panel[3])
    for y, w_ in ((8, 2), (58, 2)):                 # franjas de luz fría
        d.rectangle([0, y, W, y + w_ - 1], fill=franja); d.line([0, y + w_, W, y + w_], fill=franja_l if not o else panel[2])
    # Ventanal redondeado hacia el laboratorio.
    vx0, vy0, vx1, vy1 = 128, 15, 288, 53
    d.rounded_rectangle([vx0 - 3, vy0 - 3, vx1 + 3, vy1 + 3], radius=9, fill=acero_l)
    d.rounded_rectangle([vx0, vy0, vx1, vy1], radius=7, fill=interior)
    degradado(im, vy0 + 2, vy1 - 6, [interior_s, interior, interior], vx0 + 4, vx1 - 3)
    d.rectangle([vx0 + 3, vy1 - 8, vx1 - 3, vy1 - 2], fill=interior_s)
    for x in range(vx0 + 10, vx1 - 4, 16):
        d.line([x, vy0 + 3, x, vy1 - 9], fill=mezcla(interior, junta, 0.35))
    # Dentro: cápsula con un retoño, árbol de interior en maceta blanca y el riel del brazo de ensamblado.
    for (px_, liq) in ((166, cian), (238, viol)):
        d.rounded_rectangle([px_ - 7, vy0 + 6, px_ + 7, vy1 - 6], radius=6, fill=mezcla(interior, liq, 0.35 if not o else 0.45), outline=acero)
        d.rectangle([px_ - 8, vy0 + 4, px_ + 8, vy0 + 7], fill=acero); d.rectangle([px_ - 8, vy1 - 7, px_ + 8, vy1 - 4], fill=acero)
        d.line([px_, vy1 - 9, px_, vy0 + 20], fill=verde)
        for k in range(4):
            yy = vy0 + 21 + k * 4
            d.line([px_, yy, px_ - 3 + (k % 2) * 6, yy - 2], fill=verde_l)
        d.line([px_ - 4, vy0 + 10, px_ - 4, vy1 - 10], fill=mezcla(hx("FFFFFF"), interior, 0.3 if not o else 0.7))
    tx = 270
    d.rectangle([tx - 1, 36, tx + 1, vy1 - 8], fill=acero)
    for k, (dx, dy, r) in enumerate(((-6, 28, 6), (5, 27, 6), (0, 22, 7), (-3, 33, 5), (4, 33, 5))):
        d.ellipse([tx + dx - r, dy - r, tx + dx + r, dy + r], fill=verde if k % 2 else mezcla(verde, verde_l, 0.5))
    d.ellipse([tx - 5, 21, tx, 25], fill=verde_l)
    d.rounded_rectangle([tx - 6, vy1 - 12, tx + 6, vy1 - 3], radius=3, fill=panel[3] if not o else acero_l)
    d.rectangle([vx0 + 4, vy0 + 3, vx1 - 4, vy0 + 4], fill=acero)
    # Izquierda: tanque con líquido y terminal holográfica.
    d.rounded_rectangle([70, 20, 92, 66], radius=5, fill=mezcla(panel[2], cian, 0.18), outline=acero)
    d.rectangle([72, 30, 90, 64], fill=mezcla(panel[2], cian, 0.55 if not o else 0.7))
    d.rectangle([68, 18, 94, 22], fill=acero_l); d.rectangle([68, 64, 94, 68], fill=acero_l)
    d.line([74, 30, 74, 63], fill=mezcla(hx("FFFFFF"), cian, 0.4))
    d.rounded_rectangle([30, 48, 46, 68], radius=3, fill=panel[3], outline=junta)
    d.polygon([(26, 46), (50, 46), (54, 30), (22, 30)], fill=mezcla(panel[2], cian, 0.25 if not o else 0.35))
    d.line([(22, 30), (54, 30)], fill=cian); d.line([(26, 46), (50, 46)], fill=cian)
    # Derecha: puerta corrediza con luz de estado y un arbolito en maceta.
    d.rounded_rectangle([322, 14, 356, 66], radius=6, fill=panel[3], outline=junta)
    d.line([339, 15, 339, 65], fill=junta); d.rectangle([324, 12, 354, 13], fill=franja)
    d.ellipse([359, 36, 362, 39], fill=hx("5FD49C"))
    rx = 386
    d.rectangle([rx - 1, 42, rx + 1, 58], fill=acero)
    for k, (dx, dy, r) in enumerate(((-5, 38, 5), (4, 37, 5), (0, 32, 6))):
        d.ellipse([rx + dx - r, dy - r, rx + dx + r, dy + r], fill=verde if k % 2 else mezcla(verde, verde_l, 0.5))
    d.rounded_rectangle([rx - 8, 56, rx + 8, 67], radius=4, fill=panel[3] if not o else acero_l, outline=junta)
    # Piso pulido con el reflejo de la franja de luz.
    degradado(im, 67, H, piso)
    d.line([0, 67, W, 67], fill=junta)
    for x in range(0, W, 2): d.point((x, 71), fill=mezcla(piso[1], franja, 0.4))
    if o:
        for (x, y, c) in ((166, 34, cian), (238, 34, viol), (81, 46, cian), (38, 38, cian), (360, 37, hx("5FD49C"))):
            luz(im, x, y, 12, c, 0.22, 3)
        luz(im, 81, 72, 14, cian, 0.18, 2, ry=3)
    big = subir(im)
    # Reflejos diagonales en el vidrio (a escala de la banda, finos).
    dd = ImageDraw.Draw(big)
    for (a, w_) in ((140, 10), (156, 4), (214, 8)):
        dd.polygon([(a * ESC, (vy0 + 1) * ESC), ((a + w_) * ESC, (vy0 + 1) * ESC), ((a + w_ - 20) * ESC, (vy1 - 1) * ESC), ((a - 20) * ESC, (vy1 - 1) * ESC)],
                   fill=mezcla(interior, hx("FFFFFF"), 0.35 if not o else 0.08))
    hud = cian if not o else hx("7FEAF5")
    rnd = random.Random(4)
    burb = [(rnd.uniform(74, 88), rnd.random()) for _ in range(7)]

    def vivo(img, tick, delante):
        dd = ImageDraw.Draw(img)
        if not delante:
            for x, f in burb:                       # burbujas del tanque
                ff = (f + tick * 0.03) % 1
                cuad(dd, x, 63 - ff * 32, mezcla(cian, hx("FFFFFF"), 0.6))
            a = math.sin(tick * 0.07) * 0.5         # brazo de ensamblado colgado del riel
            bx = 200 + math.sin(tick * 0.035) * 14
            j1 = (bx + 8 * math.sin(a), vy0 + 5 + 8 * math.cos(a))
            j2 = (j1[0] + 7 * math.sin(-a * 1.6 + 0.6), j1[1] + 7 * math.cos(-a * 1.6 + 0.6))
            for p, q in (((bx, vy0 + 5), j1), (j1, j2)):
                dd.line([p[0] * ESC, p[1] * ESC, q[0] * ESC, q[1] * ESC], fill=acero, width=ESC + 1)
            for p in ((bx, vy0 + 5), j1, j2):
                cuad(dd, p[0] - 1, p[1] - 1, acero_l, 2, 2)
            if (tick // 4) % 3 == 0: cuad(dd, j2[0], j2[1] + 1, franja_l)
            for k in range(4):                      # holograma de la terminal
                y = 33 + k * 3
                ancho = 8 + 14 * (0.5 + 0.5 * math.sin(tick * 0.2 + k))
                cuad(dd, 27 + k, y, cian, ancho, 1)
            if (tick // 10) % 2: cuad(dd, 359, 36, franja_l, 3, 3)
            return
        # Retícula que sigue al gato y lo mide (todo se mide).
        c = POS.get("c")
        if not c: return
        _, _, x, dirn, dy = c
        x0, x1 = x + 6, x + 58
        y0, y1 = g8.Y_SUELO - 60 + dy + 13, g8.Y_SUELO + 1
        L_ = 4
        for (cx_, cy_, sx, sy) in ((x0, y0, 1, 1), (x1, y0, -1, 1), (x0, y1, 1, -1), (x1, y1, -1, -1)):
            dd.line([cx_ * ESC, cy_ * ESC, (cx_ + sx * L_) * ESC, cy_ * ESC], fill=hud, width=2)
            dd.line([cx_ * ESC, cy_ * ESC, cx_ * ESC, (cy_ + sy * L_) * ESC], fill=hud, width=2)
        etiqueta = f"SUJETO 07 · x = {(x + 32) / 100:.2f} m".replace(".", ",")
        tx_ = x0 * ESC if x0 < W - 90 else (x1 - 86) * ESC
        dd.text((tx_, (y0 - 4.6) * ESC), etiqueta, font=fuente("DejaVuSansMono-Bold.ttf", 10), fill=hud)
        for xx in range(0, W, 4):                  # regla en el piso
            largo = 3 if xx % 40 == 0 else (2 if xx % 20 == 0 else 1)
            dd.line([xx * ESC, (H - 1) * ESC, xx * ESC, (H - 1 - largo) * ESC], fill=mezcla(hud, piso[1], 0.35), width=1)
    ANIM[("laboratorio", o)] = [vivo]
    return big


# =====================================================================
#  Soviético: cartel constructivista. Diagonal negra con el lema, estrella roja (no un círculo), torre de Shújov,
#  altavoz que grita «¡PLAN!» y la fábrica con humo. De noche: el mismo cartel en negativo, con reflectores.
# =====================================================================

def estrella5(cx, cy, r, ri):
    return [(cx + (r if k % 2 == 0 else ri) * math.cos(-math.pi / 2 + k * math.pi / 5),
             cy + (r if k % 2 == 0 else ri) * math.sin(-math.pi / 2 + k * math.pi / 5)) for k in range(10)]


def fondo_urss(t):
    o = t["oscuro"]
    g_ = lambda v: (v, v, v)
    R = hx("C1121F")
    tinta_, papel_ = (g_(14), g_(240)) if not o else (g_(235), g_(14))
    m, d = lienzo(papel_)
    for y in range(46, 75):                         # trama suave abajo (el cartel «pesa» hacia el suelo)
        d.line([0, y, W, y], fill=g_(int(240 - (y - 46) * 1.6)) if not o else g_(int(14 + (y - 46) * 2.0)))
    # Estrella roja grande y torre de Shújov delante.
    d.polygon(estrella5(342, 38, 36, 14), fill=R)
    cx = 342
    for (yb, yt, rb, rt) in ((74, 52, 17, 13), (52, 34, 13, 10), (34, 19, 10, 7.2), (19, 7, 7.2, 4.8)):
        for k in range(16):
            a = 2 * math.pi * k / 16
            for tw in (0.9, -0.9):
                if math.sin(a) < 0 and math.sin(a + tw) < 0: continue
                d.line([cx + rb * math.cos(a), yb, cx + rt * math.cos(a + tw), yt], fill=tinta_)
        d.line([cx - rb, yb, cx + rb, yb], fill=tinta_); d.line([cx - rt, yt, cx + rt, yt], fill=tinta_)
    d.line([cx, 7, cx, 1], fill=tinta_)
    # Diagonal negra (lleva el lema) y una franja roja paralela.
    d.polygon([(58, 80), (112, 80), (262, -2), (208, -2)], fill=tinta_)
    d.polygon([(118, 80), (128, 80), (278, -2), (268, -2)], fill=R)
    # Altavoz que grita, con ondas en cuña.
    d.polygon([(6, 30), (14, 28), (44, 16), (44, 46), (14, 34), (6, 34)], fill=tinta_)
    d.rectangle([12, 34, 15, 44], fill=tinta_)
    for k in range(3):
        d.line([48 + k * 5, 18 - k * 3, 52 + k * 5, 12 - k * 4], fill=R, width=2)
        d.line([50 + k * 5, 31, 56 + k * 5, 31], fill=R, width=2)
        d.line([48 + k * 5, 44 + k * 3, 52 + k * 5, 50 + k * 4], fill=R, width=2)
    # Fábrica con techo de dientes de sierra y chimeneas, entre la diagonal y la torre.
    x0f, x1f = 168, 300
    d.rectangle([x0f, 54, x1f, 74], fill=g_(120) if not o else g_(10))
    for x in range(x0f, x1f, 12):
        d.polygon([(x, 54), (x + 9, 46), (x + 9, 54)], fill=tinta_ if not o else g_(10)); d.line([x + 9, 46, x + 12, 54], fill=tinta_)
    for y in (58, 65):
        for x in range(x0f + 4, x1f - 3, 6):
            d.rectangle([x, y, x + 3, y + 4], fill=papel_ if not o else (g_(225) if (x * 3 + y) % 5 < 2 else g_(10)))
    for x, h in ((186, 14), (226, 8), (270, 18)):
        d.rectangle([x, h, x + 6, 54], fill=tinta_ if not o else g_(10))
        if o: d.line([x, h, x, 54], fill=g_(140))
        for yy in (h + 3, h + 8): d.rectangle([x, yy, x + 6, yy + 1], fill=R)
    if o:                                            # reflectores (haces finos)
        for (sx, an) in ((150, -70), (395, -112)):
            a = math.radians(an)
            for y in range(0, 74):
                for x in range(W):
                    dx, dy = x - sx, y - 74
                    if dy < 0 and abs((math.atan2(dy, dx) - a + math.pi) % (2 * math.pi) - math.pi) < 0.045:
                        v0 = m.getpixel((x, y))
                        if v0[0] == v0[1] and v0[0] < 60: m.putpixel((x, y), g_(120))
    # Suelo: franja roja con rayas.
    d.line([0, 74, W, 74], fill=tinta_)
    d.rectangle([0, 75, W, H], fill=R)
    for x in range(-10, W, 7): d.line([x, H, x + 5, 75], fill=tinta_)
    big = a_tintas(t, m, o)
    # Lema a lo largo de la diagonal y «¡ПЛАН!» bajo el altavoz.
    capa = Image.new("RGBA", (360 * ESC // 2, 40), (0, 0, 0, 0))
    ImageDraw.Draw(capa).text((capa.width // 2, 20), "ВСЕ НА СТРОЙКУ!", font=fuente("DejaVuSans-Bold.ttf", 30), fill=t["tintas"]["papel"] + (255,), anchor="mm")
    ang = math.degrees(math.atan2(82, 150))
    capa = capa.rotate(ang, expand=True, resample=Image.BICUBIC)
    big.paste(capa, (int(160 * ESC - capa.width / 2), int(39 * ESC - capa.height / 2)), capa)
    ImageDraw.Draw(big).text((26 * ESC, 60 * ESC), "ПЛАН!", font=fuente("DejaVuSans-Bold.ttf", 34), fill=t["tintas"]["rojo"], anchor="mm")
    chim = ((186, 14), (226, 8), (270, 18))

    def vivo(img, tick, delante):
        if delante: return
        cel = []
        for j, (x, h) in enumerate(chim):
            for k in range(6):
                f = ((tick * 0.05 + k / 6 + j * 0.3) % 1)
                yy = h - 1 - f * 10; xx = x + 3 - f * 16
                for dx in range(-1 - int(f * 3), 2 + int(f * 3)):
                    for dy_ in (-1, 0, 1):
                        cel.append((round(xx + dx), round(yy + dy_), 120 + int(f * 100) if not o else 140 - int(f * 90)))
        trama_viva(img, t, [(x, y, gr) for x, y, gr in cel if 0 <= y < H and 0 <= x < W], negativo=o)
    ANIM[("urss", o)] = [vivo]
    return big


# =====================================================================
#  Tierras Antiguas: el gato camina sobre un mapa viejo (lago, río, bosque, aldea, montañas con la mina y un dragón
#  en tinta roja). De noche, el mapa a la luz de la luna: aparecen las runas lunares, que de día no se ven.
# =====================================================================

def runa(d, x, y, semilla, col, h=5):
    """Runa inventada: un trazo vertical (o ninguno) con ramas a distintas alturas, o una figura cerrada."""
    rnd = random.Random(semilla * 7919 + 13)
    tipo = rnd.random()
    if tipo < 0.15:
        d.line([x, y, x + 3, y + h], fill=col); d.line([x + 3, y, x, y + h], fill=col)
        return
    if tipo < 0.27:
        d.polygon([(x + 1.5, y), (x + 3, y + h / 2), (x + 1.5, y + h), (x, y + h / 2)], outline=col)
        return
    sx = x + rnd.choice((0, 1))
    d.line([sx, y, sx, y + h], fill=col)
    for _ in range(rnd.randint(1, 3)):
        y0 = y + rnd.randint(0, h - 1); lado = rnd.choice((-1, 1)); sube = rnd.choice((-2, -1, 0, 1, 2))
        d.line([sx, y0, sx + lado * 2, y0 + sube], fill=col)
    if rnd.random() < 0.3:
        d.line([sx + 2, y, sx + 2, y + h], fill=col)


def montaña(d, x, yb, h, w, tinta, relleno, sombra):
    d.polygon([(x - w, yb), (x - w * 0.15, yb - h), (x + w * 0.1, yb - h + 1), (x + w, yb)], fill=relleno)
    d.line([(x - w, yb), (x - w * 0.15, yb - h), (x + w * 0.1, yb - h + 1), (x + w, yb)], fill=tinta)
    for k in range(1, int(h * 0.8), 2):             # tramado de la ladera en sombra
        y = yb - h + 1 + k
        xa = x + w * 0.1 + (w * 0.9) * k / h
        d.line([xa - 1, y, xa - 1 - min(3, k * 0.5), y + 1], fill=sombra)


def arbolito(d, x, y, tinta, relleno):
    d.ellipse([x - 2, y - 5, x + 2, y - 1], fill=relleno, outline=tinta); d.line([x, y - 1, x, y + 1], fill=tinta)


def fondo_antiguas(t):
    o = t["oscuro"]
    if not o:
        perg, perg_s, mancha, quem = hx("E8DCBC"), hx("D9C9A2"), hx("CDB98C"), hx("8A6A3E")
        tinta, rojo, oro, agua, sombra = hx("3A2E1E"), hx("8E1B24"), hx("9A7B2E"), hx("B5C2B6"), hx("6E5E44")
    else:
        perg, perg_s, mancha, quem = hx("4A5162"), hx("3E4455"), hx("353A4A"), hx("15161A")
        tinta, rojo, oro, agua, sombra = hx("14151A"), hx("5E1E2A"), hx("6A6040"), hx("3C485C"), hx("23262F")
    im, d = lienzo(perg)
    rnd = random.Random(7)
    px = im.load()
    for _ in range(14):                              # manchas del pergamino
        cx_, cy_, r = rnd.uniform(0, W), rnd.uniform(0, H), rnd.uniform(6, 22)
        for y in range(max(0, int(cy_ - r)), min(H, int(cy_ + r))):
            for x in range(max(0, int(cx_ - r * 1.6)), min(W, int(cx_ + r * 1.6))):
                dd_ = ((x - cx_) / (r * 1.6)) ** 2 + ((y - cy_) / r) ** 2
                if dd_ < 1 and tramar((1 - dd_) * 0.7, x, y): px[x, y] = perg_s
    for x in range(W):                               # orillas quemadas
        for y in range(0, 2 + rnd.randint(0, 2)): px[x, y] = quem
        for y in range(H - 2 - rnd.randint(0, 1), H): px[x, y] = quem
    d.line([0, 5, W, 5], fill=tinta); d.line([0, 7, W, 7], fill=tinta)
    # Rosa de los vientos.
    cx_, cy_ = 26, 30
    for k in range(8):
        an = -math.pi / 2 + k * math.pi / 4; r = 12 if k % 2 == 0 else 7
        q1 = (cx_ + 2 * math.cos(an + math.pi / 2), cy_ + 2 * math.sin(an + math.pi / 2))
        q2 = (cx_ + 2 * math.cos(an - math.pi / 2), cy_ + 2 * math.sin(an - math.pi / 2))
        d.polygon([q1, (cx_ + r * math.cos(an), cy_ + r * math.sin(an)), q2], fill=rojo if k == 0 else (tinta if k % 2 == 0 else perg), outline=tinta)
    d.ellipse([cx_ - 8, cy_ - 8, cx_ + 8, cy_ + 8], outline=tinta)
    # Cartela con el título en runas.
    d.rectangle([150, 10, 262, 21], fill=perg, outline=tinta)
    for xe in (150, 262):
        d.ellipse([xe - 3, 9, xe + 3, 22], fill=perg_s, outline=tinta)
    for k in range(17): runa(d, 158 + k * 6, 13, 200 + k, rojo if k in (0, 9) else tinta, 5)
    # Lago con olas, y el río que baja de las montañas.
    lx, ly = 78, 52
    d.ellipse([lx - 26, ly - 7, lx + 26, ly + 7], fill=agua, outline=tinta)
    for k in range(-4, 5, 2):
        for x in range(lx - 20 + abs(k) * 2, lx + 20 - abs(k) * 2, 5): d.line([x, ly + k, x + 2, ly + k - 1], fill=sombra)
    pts = [(lx + 26, ly), (122, 47), (150, 53), (180, 46), (214, 50), (246, 44), (282, 47), (300, 40)]
    curva = g8.catmull(pts, 10)
    d.line(curva, fill=tinta, width=1)
    d.line([(x, y + 2) for x, y in curva], fill=tinta, width=1)
    # Bosque, aldea, montañas con la mina, y el dragón en tinta roja.
    for k in range(30):
        x, y = 108 + (k % 10) * 7 + (k // 10) * 3 + rnd.randint(-1, 1), 31 + (k // 10) * 5 + rnd.randint(-1, 1)
        arbolito(d, x, y, tinta, perg_s if k % 3 else mancha)
    for k, (x, y) in enumerate(((210, 60), (219, 58), (228, 61))):
        d.rectangle([x - 2, y - 3, x + 2, y], fill=perg, outline=tinta); d.polygon([(x - 3, y - 3), (x, y - 6), (x + 3, y - 3)], fill=tinta)
    for (x, yb, h, w) in ((292, 40, 12, 12), (316, 38, 18, 14), (372, 40, 14, 12), (396, 42, 10, 10), (304, 44, 9, 10), (384, 46, 9, 9)):
        montaña(d, x, yb, h, w, tinta, perg, sombra)
    montaña(d, 345, 44, 26, 18, tinta, perg_s, sombra)
    d.arc([339, 37, 349, 47], 180, 360, fill=tinta); d.line([339, 42, 339, 44], fill=tinta); d.line([349, 42, 349, 44], fill=tinta)
    dx_, dy_ = 352, 10                                # dragón: cuerpo, alas, cola y cabeza
    d.line([(dx_ - 10, dy_ + 4), (dx_ - 6, dy_ + 1), (dx_, dy_ + 2), (dx_ + 6, dy_), (dx_ + 9, dy_ - 2)], fill=rojo, width=2)
    d.polygon([(dx_ - 3, dy_ + 1), (dx_ - 1, dy_ - 7), (dx_ + 3, dy_ - 4), (dx_ + 4, dy_ + 1)], fill=rojo)
    d.polygon([(dx_ + 9, dy_ - 2), (dx_ + 13, dy_ - 3), (dx_ + 10, dy_ + 0)], fill=rojo)
    d.line([(dx_ - 10, dy_ + 4), (dx_ - 14, dy_ + 3), (dx_ - 15, dy_ + 1)], fill=rojo)
    # Leyendas en runas.
    for (x, y, n, col) in ((112, 22, 5, tinta), (60, 62, 4, tinta), (206, 66, 4, tinta), (330, 50, 5, rojo), (292, 24, 3, tinta)):
        for k in range(n): runa(d, x + k * 5, y, x * 3 + k, col, 4)
    # Camino de la compañía (línea de puntos roja) por donde va el gato, con una X en la mina.
    for x in range(0, W):
        if x % 5 < 3:
            y = round(72 + 1.5 * math.sin(x / 23))
            d.point((x, y), fill=rojo); d.point((x, y + 1), fill=rojo)
    d.line([342, 46, 348, 52], fill=rojo); d.line([348, 46, 342, 52], fill=rojo)
    lunares = []
    if o:                                            # la luz de la luna sobre el mapa
        luz(im, 300, 18, 120, hx("9FB0D0"), 0.22, 5, ry=50)
        rnd2 = random.Random(3)
        for k in range(18): lunares.append((64 + k * 5, 64, rnd2.randrange(1000)))
        for k in range(10): lunares.append((288 + k * 5, 56, rnd2.randrange(1000)))
    big = g8.papel(subir(im), 0.05 if not o else 0.03)

    def vivo(img, tick, delante):
        if delante: return
        dd = ImageDraw.Draw(img)
        if not o:
            return
        for j, (x, y, s) in enumerate(lunares):      # runas lunares que aparecen y brillan
            k = 0.5 + 0.5 * math.sin(tick * 0.09 - j * 0.35)
            col = mezcla(hx("8FA4C8"), hx("F2F6FF"), k)
            capa = Image.new("RGB", (5, 7), (0, 0, 0)); runa(ImageDraw.Draw(capa), 1, 1, s, (255, 255, 255), 4)
            for yy in range(7):
                for xx in range(5):
                    if capa.getpixel((xx, yy))[0]: cuad(dd, x + xx, y + yy - 1, col)
    ANIM[("antiguas", o)] = [vivo]
    return big


# =====================================================================
#  Colonos: el asentamiento de los Minutemen. El fuerte de piedra con su cañón, la torre de radio, una casa de chapa que
#  se reconstruye, la bandera azul de estrellas, el huerto y la torre de guardia. De noche: foquitos, tambo encendido y reflector.
# =====================================================================

def fondo_colonos(t):
    o = t["oscuro"]
    if not o:
        cielo = [hx("8EA9C2"), hx("ADC0CC"), hx("CFD5CF"), hx("E9E2CC")]
        lejos, lejos_s = hx("A8B2B4"), hx("97A2A6")
        piedra, piedra_l, piedra_s = hx("A49A86"), hx("C2B9A4"), hx("7E7564")
        madera, madera_s, chapa, chapa_s, oxido, oxido_s = hx("8A6A3E"), hx("5E4422"), hx("9AA0A2"), hx("767C80"), hx("B0703A"), hx("85512A")
        marino, ladrillo, crema, tinta, verde, verde_l = hx("1F3A5F"), hx("A23B2A"), hx("F2EBDC"), hx("1F2A3A"), hx("5E7A3A"), hx("8AA64E")
        tierra, tierra_s, ventana = hx("BCA27A"), hx("9E8660"), hx("6F8EA8")
    else:
        cielo = [hx("070B14"), hx("0E1420"), hx("151D2B"), hx("1E2838")]
        lejos, lejos_s = hx("18202E"), hx("141B27")
        piedra, piedra_l, piedra_s = hx("2E3442"), hx("3A4152"), hx("222733")
        madera, madera_s, chapa, chapa_s, oxido, oxido_s = hx("3A3026"), hx("261E16"), hx("343A46"), hx("282D38"), hx("4A3022"), hx("36241A")
        marino, ladrillo, crema, tinta, verde, verde_l = hx("1E3050"), hx("4A2426"), hx("8C8676"), hx("070A10"), hx("1C2A1C"), hx("2A3C24")
        tierra, tierra_s, ventana = hx("2A2C30"), hx("222428"), hx("F2C46A")
    im, d = lienzo(cielo[-1])
    degradado(im, 0, 52, cielo)
    if not o:
        for x, y, w in ((150, 8, 34), (300, 12, 28)): nube(d, x, y, w, hx("F6F4EE"), hx("E4E4DE"), hx("BFC6C8"))
    else:
        estrellas(im, 5, 70, 0, 38, [hx("EEE7D7"), hx("A9B0BC"), hx("8FB3E0")])
        d.ellipse([272, 6, 282, 16], fill=hx("EEE7D7")); d.ellipse([275, 5, 285, 15], fill=cielo[0])
    # Ciudad en ruinas a lo lejos.
    rnd = random.Random(11); x = 0
    while x < W:
        w = rnd.randint(7, 16); h = rnd.randint(6, 22)
        top = 50 - h
        d.polygon([(x, 52), (x, top + rnd.randint(0, 3)), (x + w // 2, top), (x + w, top + rnd.randint(2, 6)), (x + w, 52)], fill=lejos)
        for yy in range(top + 4, 50, 4):
            for xx in range(x + 2, x + w - 1, 3):
                if rnd.random() < 0.5: d.point((xx, yy), fill=lejos_s if not o else (hx("6A5A3A") if rnd.random() < 0.15 else lejos_s))
        x += w + rnd.randint(0, 4)
    perfil(im, ondas(5, 54, ((1.5, 120),)), mezcla(tierra, lejos, 0.5))
    # El fuerte: muro de piedra con almenas y un cañón.
    d.rectangle([0, 42, 92, 68], fill=piedra)
    for yy in range(44, 68, 4):
        d.line([0, yy, 92, yy], fill=piedra_s)
        for xx in range((yy // 4 % 2) * 4, 92, 8): d.line([xx, yy - 3, xx, yy], fill=piedra_s)
    for xx in range(0, 92, 10): d.rectangle([xx, 38, xx + 5, 42], fill=piedra); d.line([xx, 38, xx + 5, 38], fill=piedra_l)
    d.line([0, 42, 92, 42], fill=piedra_l); d.line([92, 38, 92, 68], fill=piedra_s)
    d.polygon([(38, 34), (60, 27), (61, 30), (40, 37)], fill=hx("2A2A2E") if not o else hx("101216"))
    d.ellipse([34, 32, 44, 42], fill=madera, outline=madera_s); d.line([39, 32, 39, 42], fill=madera_s); d.line([34, 37, 44, 37], fill=madera_s)
    for k in range(3): d.ellipse([66 + k * 4, 35 - (k % 2) * 3, 69 + k * 4, 38 - (k % 2) * 3], fill=hx("2A2A2E") if not o else hx("101216"))
    # Torre de radio (celosía) con su luz roja.
    rx = 114
    d.line([rx - 8, 68, rx, 6], fill=chapa_s); d.line([rx + 8, 68, rx, 6], fill=chapa_s)
    for k in range(10):
        y = 66 - k * 6; w = 8 * (y - 6) / 62
        d.line([rx - w, y, rx + w * 0.8, y - 6], fill=chapa_s); d.line([rx + w, y, rx - w * 0.8, y - 6], fill=chapa_s)
    d.rectangle([rx - 9, 58, rx + 9, 68], fill=oxido, outline=oxido_s); d.rectangle([rx - 2, 62, rx + 2, 68], fill=madera_s)
    d.line([rx + 4, 18, rx + 12, 14], fill=chapa_s); d.ellipse([rx + 10, 10, rx + 16, 18], outline=chapa)
    # Casa de chapa en reconstrucción: paneles de colores, tablas, andamio y escalera.
    x0, base = 134, 68
    cols = [chapa, oxido, marino, chapa_s, ladrillo, madera]
    for k in range(6):
        c = cols[(k * 4) % 6]
        d.rectangle([x0 + k * 10, base - 24, x0 + k * 10 + 9, base], fill=c)
        for xx in range(x0 + k * 10 + 1, x0 + k * 10 + 9, 2): d.line([xx, base - 24, xx, base], fill=mezcla(c, tinta, 0.18))
    d.polygon([(x0 - 4, base - 24), (x0 + 30, base - 34), (x0 + 64, base - 24)], fill=chapa_s)
    for xx in range(x0, x0 + 62, 3): d.line([xx, base - 24, x0 + 30 + (xx - x0 - 30) * 0.7, base - 33], fill=chapa)
    d.rectangle([x0 + 12, base - 18, x0 + 22, base - 10], fill=madera_s); d.rectangle([x0 + 13, base - 17, x0 + 21, base - 11], fill=ventana)
    d.line([x0 + 17, base - 17, x0 + 17, base - 11], fill=madera_s)
    d.rectangle([x0 + 36, base - 14, x0 + 44, base], fill=madera); [d.line([x0 + 36, yy, x0 + 44, yy], fill=madera_s) for yy in range(base - 12, base, 3)]
    for k in range(3): d.line([x0 + 66 + k * 0, base, x0 + 66, base - 26], fill=madera)
    d.line([x0 + 60, base - 13, x0 + 72, base - 13], fill=madera); d.line([x0 + 60, base - 26, x0 + 72, base - 26], fill=madera)
    d.line([x0 + 72, base, x0 + 72, base - 26], fill=madera)
    d.line([x0 + 74, base, x0 + 80, base - 24], fill=madera_s); d.line([x0 + 78, base, x0 + 84, base - 24], fill=madera_s)
    for k in range(1, 8): d.line([x0 + 74 + k * 0.75, base - k * 3, x0 + 78 + k * 0.75, base - k * 3], fill=madera_s)
    # Huerto en camas de madera, con su purificador de agua.
    for k, xh in enumerate((236, 262, 288)):
        d.rectangle([xh, 62, xh + 20, 68], fill=madera, outline=madera_s)
        for j in range(4):
            x_ = xh + 3 + j * 5
            if k == 1:
                d.line([x_, 62, x_, 50], fill=verde); d.line([x_, 56, x_ + 2, 54], fill=verde_l); d.line([x_, 53, x_ - 2, 51], fill=verde_l)
                d.rectangle([x_ + 1, 55, x_ + 1, 57], fill=hx("E3C27A") if not o else hx("5A4A2A"))
            else:
                d.ellipse([x_ - 2, 58, x_ + 2, 62], fill=verde); d.point((x_ - 1, 58), fill=verde_l)
    d.rectangle([314, 50, 324, 68], fill=chapa, outline=chapa_s); d.ellipse([314, 47, 324, 53], fill=chapa_s)
    d.line([324, 60, 330, 60], fill=chapa_s); d.line([330, 60, 330, 68], fill=chapa_s)
    d.rectangle([316, 56, 322, 58], fill=marino)
    # Torre de guardia con costales y reflector.
    gx = 364
    for xx in (gx - 12, gx + 12): d.line([xx, 68, xx + (3 if xx < gx else -3), 32], fill=madera)
    d.line([gx - 11, 60, gx + 11, 44], fill=madera_s); d.line([gx + 11, 60, gx - 11, 44], fill=madera_s)
    d.rectangle([gx - 14, 30, gx + 14, 33], fill=madera); d.line([gx - 14, 33, gx + 14, 33], fill=madera_s)
    for k in range(6): d.ellipse([gx - 14 + k * 5, 26, gx - 9 + k * 5, 30], fill=hx("B7A27A") if not o else hx("3A3426"), outline=madera_s)
    d.polygon([(gx - 16, 18), (gx, 12), (gx + 16, 18)], fill=chapa_s); d.line([gx - 13, 18, gx - 13, 26], fill=madera); d.line([gx + 13, 18, gx + 13, 26], fill=madera)
    d.rectangle([gx + 4, 21, gx + 9, 25], fill=chapa); d.ellipse([gx + 8, 21, gx + 11, 25], fill=hx("FFF3C8") if o else chapa_s)
    # Suelo de tierra con grava y matas.
    degradado(im, 68, H, [tierra_s, tierra, tierra])
    for _ in range(200):
        x, y = rnd.randrange(W), rnd.randrange(69, H)
        d.point((x, y), fill=tierra_s if rnd.random() < 0.7 else mezcla(tierra, hx("FFFFFF"), 0.25 if not o else 0.04))
    for x in range(3, W, 13):
        d.line([x, 69, x - 1, 67], fill=verde); d.line([x + 1, 69, x + 2, 67], fill=verde_l)
    tambo = (226, 68)
    d.rectangle([tambo[0] - 3, 62, tambo[0] + 3, 68], fill=oxido, outline=oxido_s); d.line([tambo[0] - 3, 65, tambo[0] + 3, 65], fill=oxido_s)
    if o:
        for (cx_, cy_) in ((x0 + 17, base - 14), (rx, 64)): luz(im, cx_, cy_, 8, hx("F2C46A"), 0.22, 2)
        luz(im, tambo[0], 60, 12, hx("FF9A4A"), 0.3, 3)
        luz(im, tambo[0], 70, 30, hx("FF9A4A"), 0.22, 3, ry=5)
    big = subir(im)
    foquitos = []
    if o:
        for (a, b) in (((rx, 30), (x0 + 30, base - 34)), ((x0 + 64, base - 24), (gx - 14, 30))):
            for k in range(1, 12):
                f = k / 12
                foquitos.append((a[0] + (b[0] - a[0]) * f, a[1] + (b[1] - a[1]) * f + 4 * math.sin(math.pi * f)))

    def vivo(img, tick, delante):
        dd = ImageDraw.Draw(img)
        if delante: return
        # Bandera: azul, con un anillo de trece estrellas y un tricornio blanco al centro.
        ax, ay = 210, 22
        cuad(dd, ax, ay, tinta if not o else hx("2A2F3A"), 1, 46)
        for i in range(22):
            off = math.sin(tick * 0.33 - i * 0.42) * (0.3 + i * 0.06)
            som = 0.16 if math.cos(tick * 0.33 - i * 0.42) > 0.3 else 0
            cuad(dd, ax + 1 + i, ay + 1 + off, mezcla(hx("2C5AA0") if not o else hx("22406E"), hx("000000"), som), 1, 13)
            for k in range(13):
                an = 2 * math.pi * k / 13
                sx, sy = 11 + 5.2 * math.cos(an), 6.5 + 5.2 * math.sin(an)
                if round(sx) == i: cuad(dd, ax + 1 + i, ay + 1 + off + round(sy), crema if not o else hx("C9C3B0"))
            if 8 <= i <= 14:
                rows = [5, 6] if i in (8, 14) else ([5, 6, 7] if i in (9, 13) else [4, 5, 6, 7])
                for r_ in rows: cuad(dd, ax + 1 + i, ay + 1 + off + r_, crema if not o else hx("C9C3B0"))
        if (tick // 8) % 2 == 0: cuad(dd, rx - 0.5, 4, hx("FF3B30"), 2, 2)
        if not o:
            humo(img, tick, tambo[0], 61, hx("E9E6DE"), cielo[2], semilla=3)
            return
        llama(dd, tambo[0], 62, tick, 0.9)
        for j, (x, y) in enumerate(foquitos):
            col = (hx("F2C46A"), hx("FFE3A0"), hx("E07A62"))[j % 3]
            cuad(dd, x, y, col if (j + tick // 5) % 4 else mezcla(col, hx("000000"), 0.5))
        a = math.radians(-160 + 50 * (0.5 + 0.5 * math.sin(tick * 0.04)))   # reflector que barre
        sx, sy = gx + 10, 23
        for k in range(4, 70, 2):
            w_ = 0.6 + k * 0.08
            x, y = sx + k * math.cos(a), sy + k * math.sin(a)
            if 0 <= x < W and 0 <= y < H:
                dd.ellipse([(x - w_) * ESC, (y - w_) * ESC, (x + w_) * ESC, (y + w_) * ESC], fill=mezcla(hx("2A3448"), hx("FFF3C8"), max(0.08, 0.35 - k / 220)))
    ANIM[("colonos", o)] = [vivo]
    return big


g9.FONDOS9.update(laboratorio=fondo_laboratorio, urss=fondo_urss, antiguas=fondo_antiguas, colonos=fondo_colonos)
g8.FONDOS.update(g9.FONDOS9)
g9.ETIQ.update(laboratorio="Laboratorio · cámara de observación del Instituto", urss="Soviético · cartel constructivista",
               antiguas="Tierras Antiguas · sobre el mapa de la compañía", colonos="Colonos · el asentamiento de los Minutemen")
g9.NOCHE.update(laboratorio="ciclo nocturno de la cámara", urss="el cartel en negativo, con reflectores", antiguas="el mapa a la luz de la luna: runas lunares",
                colonos="foquitos, tambo encendido y reflector")


def vista(archivo):
    filas = []
    for n in REHECHOS:
        for o in (False, True):
            t = tema(n, o); f = g9.FONDOS9[n](t)
            img = cuadro_banda(n, o, t, f, ("caminar", 2, 120.0, 1, 0.0), 5, None)
            img = cuadro_banda(n, o, t, img, ("sentado", 0, 300.0, -1, 0.0), 5, None)
            filas.append(img)
    W3, H3 = filas[0].size
    hoja = Image.new("RGB", (W3 * 2 + 10, 4 * (H3 + 6)), (250, 249, 246))
    for k, f in enumerate(filas):
        hoja.paste(f, ((k % 2) * (W3 + 10), (k // 2) * (H3 + 6)))
    hoja.save(AQUI / archivo)


def resumen(cuadros, ruta, escala=1.5):
    temas_ = [(n, o, tema(n, o)) for n in REHECHOS for o in (False, True)]
    fondos = {(n, o): g9.FONDOS9[n](t) for n, o, t in temas_}
    bw, bh = int(W * escala), int(H * escala)
    cw, ch_ = bw + 12, bh + 20
    out = []
    for k, c in enumerate(cuadros):
        lam = Image.new("RGB", (2 * cw + 10, 4 * ch_ + 8), (250, 249, 246)); dl = ImageDraw.Draw(lam)
        for j, (n, o, t) in enumerate(temas_):
            b = cuadro_banda(n, o, t, fondos[(n, o)], c, k, None).resize((bw, bh), Image.LANCZOS)
            x, y = 10 + (j % 2) * cw, 6 + (j // 2) * ch_
            txt = g9.ETIQ[n] if not o else g9.ETIQ[n].split(" · ")[0] + " · de noche: " + g9.NOCHE[n]
            dl.text((x, y), txt, font=fuente("DejaVuSans.ttf", 12), fill=(40, 40, 46))
            lam.paste(b, (x, y + 15))
        out.append(lam)
    out[0].save(ruta, save_all=True, append_images=out[1:], duration=int(g8.DT * 1000), loop=0, quality=80, method=4)


if __name__ == "__main__":
    que = sys.argv[1:] or ["banda", "resumen"]
    if "vista" in que:
        vista("_vista.png"); sys.exit()
    s_, cs = g8.buscar_semilla()
    if "banda" in que:
        for n in REHECHOS:
            g8.gif_banda(cs, [(n, False, g9.ETIQ[n])])
            g8.gif_banda(cs, [(n, True, g9.ETIQ[n].split(" · ")[0] + " · de noche: " + g9.NOCHE[n])], "_oscuro")
    if "resumen" in que:
        resumen(cs, AQUI / "fondos_rehechos.webp")
    print("listo")
