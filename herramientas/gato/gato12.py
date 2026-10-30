"""
Prueba 12: casco de legionario para el gato romano, mosaico romano con más detalle (paneles en teselas finas)
y colonos con concepto nuevo: una hoja de gaceta con un grabado en madera iluminado a mano (el gato, también grabado).
El gato es el de gato11.py (patas traseras en curva suave).
"""
import math
import random
import sys
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato10 as g10      # noqa: E402
import gato11 as g11      # noqa: E402
from gato8 import hx, mezcla, fuente, ESC, SS   # noqa: E402
from gato9 import W, H, ANIM, cuad, llama       # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
g8.AQUI = g9.AQUI = g10.AQUI = g11.AQUI = AQUI
g11.rehacer_poses()

# =====================================================================
#  Casco de legionario (galea): cúpula de hierro con filo de oro, carrillera, guardanuca y un penacho rojo
#  entre las orejas, que asoman por arriba (así se ve tierno).
# =====================================================================
_lado10, _frente10 = g8.sombrero_lado, g8.sombrero_frente
# Las orejas se vuelven a dibujar encima del casco (como si salieran por unos agujeros): capa nueva por arriba del sombrero.
g8.CAPAS.extend([("oreja2l", "D", "orejafrente", 0.45), ("oreja2", "P", "orejafrente", 0.45), ("oreja2i", "N", "orejafrente", 0.4)])
g8.RANGO["orejafrente"] = 7.5
_ultimas = {"orejas": 0.0}
_cabeza8 = g8.cabeza


def cabeza(L, cx, cy, inc, orejas, ojo, lengua, acc):
    _ultimas["orejas"] = orejas
    return _cabeza8(L, cx, cy, inc, orejas, ojo, lengua, acc)


g8.cabeza = cabeza


def orejas_encima(L, cx, cy, inc):
    a = math.radians(inc); p = lambda x, y: g8.rot(cx, cy, x, y, a)
    o = _ultimas["orejas"]
    pt = lambda u, v: (u[0] + (v[0] - u[0]) * o, u[1] + (v[1] - u[1]) * o)
    L.poligono("oreja2l", [p(*q) for q in [(-6.4, -4.4), pt((-5.4, -10.4), (-9.6, -5.0)), (-2.6, -5.6)]])
    L.poligono("oreja2", [p(*q) for q in [(-0.2, -5.8), pt((2.4, -11.0), (-5.8, -7.6)), (5.0, -4.2)]])
    L.poligono("oreja2i", [p(*q) for q in [(1.2, -5.8), pt((2.4, -9.2), (-4.4, -6.8)), (3.8, -4.8)]])


def sombrero_lado(L, cx, cy, inc, som):
    if som != "galea":
        return _lado10(L, cx, cy, inc, som)
    a = math.radians(inc)
    p = lambda x, y: g8.rot(cx, cy, x, y, a)
    P = lambda pts: [p(*q) for q in pts]
    cupula = [(-0.6 + 7.3 * math.cos(t), -2.9 + 3.8 * math.sin(t)) for t in [math.pi + k * math.pi / 16 for k in range(17)]]
    L.poligono("sombrero", P(cupula))
    L.poligono("sombrero", P([(-6.6, -3.4), (-9.0, -1.2), (-8.4, -0.2), (-5.6, -1.6)]))          # guardanuca
    L.poligono("sombrero", P([(-2.6, -3.0), (0.9, -3.0), (0.5, 1.4), (-1.9, 1.8)]))              # carrillera
    L.capsula("sombrero3", P([(-7.6, -2.8), (6.6, -2.8)]), 0.55)                                  # filo de oro
    L.elipse("sombrero3", *p(-0.8, -0.6), 0.55, 0.55)                                              # remache
    L.elipse("sombrero3", *p(-1.6, -6.8), 0.9, 0.7)                                                # portapenacho
    L.poligono("sombrero2", P([(-1.6 + 2.4 * math.cos(t), -7.0 + 3.2 * math.sin(t)) for t in [math.pi + k * math.pi / 12 for k in range(13)]]))
    orejas_encima(L, cx, cy, inc)


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if som != "galea":
        return _frente10(L, cx, cy, som, desv)
    dx = desv * 1.6
    L.poligono("sombrero", [(cx + dx + 7.6 * math.cos(t), cy - 3.0 + 3.9 * math.sin(t)) for t in [math.pi + k * math.pi / 16 for k in range(17)]])
    for s in (-1, 1):
        L.poligono("sombrero", [(cx + dx + s * 7.4, cy - 3.0), (cx + dx + s * 4.6, cy - 3.0), (cx + dx + s * 5.0, cy + 1.6), (cx + dx + s * 7.0, cy + 1.2)])
    L.capsula("sombrero3", [(cx + dx - 7.6, cy - 2.9), (cx + dx + 7.6, cy - 2.9)], 0.55)
    L.elipse("sombrero2", cx + dx, cy - 8.4, 1.6, 2.6)
    L.elipse("sombrero3", cx + dx, cy - 6.8, 1.0, 0.7)
    for sx in (-1, 1):                               # de espaldas: las dos orejas salen por encima del casco
        k = 0.88 if (sx > 0 and desv > 0) else 1.0
        L.poligono("oreja2", [(cx + sx * 6.0 * k, cy - 3.4), (cx + sx * 4.8 * k, cy - 10.0), (cx + sx * 1.4, cy - 5.6)])


g8.sombrero_lado, g8.sombrero_frente = sombrero_lado, sombrero_frente

# =====================================================================
#  Colonos: grabado en madera. Tinta azul-negra, papel de gaceta y aguadas puestas a mano.
# =====================================================================
PAPEL, PAPEL_N, TINTA = hx("F2EBDC"), hx("DCCBA8"), hx("1F2A3A")
AGUADA = dict(azul=hx("6C88AE"), ladrillo=hx("C0604E"), ocre=hx("D8B46A"), verde=hx("8AA06A"), cielo=hx("A9C1D8"))


def pintar_grabado(g, t, cel):
    """Cada celda: la aguada del color (rebajada con el papel), la tinta en contornos y achurado diagonal en las sombras."""
    s = cel * SS
    pap, tin = t["papel"], t["tinta_g"]
    img = Image.new("RGBA", (g8.N * s, g8.N * s), (0, 0, 0, 0)); d = ImageDraw.Draw(img)
    w = max(1, round(0.2 * s))
    for r, c, v in g8.celdas(g):
        col = t["col"][v]; x0, y0 = c * s, r * s
        if v in "KE" or (v == "C"):
            d.rectangle([x0, y0, x0 + s, y0 + s], fill=tin + (255,)); continue
        base = pap if v in "LHY" else mezcla(pap, col, 0.75 if v == "M" else 0.5)
        d.rectangle([x0, y0, x0 + s, y0 + s], fill=base + (255,))
        if v in "SDTBZ":
            d.line([x0, y0 + s, x0 + s, y0], fill=tin + (255,), width=w)
            if v in "DTZ": d.line([x0, y0 + s / 2, x0 + s / 2, y0], fill=tin + (255,), width=w)
        elif v == "W":
            d.line([x0, y0 + s, x0 + s, y0], fill=tin + (255,), width=w + 1)
        elif v == "M" and (r + c) % 2 == 0:
            d.line([x0, y0 + s, x0 + s, y0], fill=tin + (255,), width=w)
        elif v == "A" and (r + c) % 3 == 0:
            d.line([x0, y0 + s, x0 + s, y0], fill=tin + (255,), width=w)
    return img.resize((g8.N * cel, g8.N * cel), Image.LANCZOS)


_pintar11 = g8.pintar


def pintar(g, t, cel, oscuro=False):
    if t["material"] == "grabado":
        return pintar_grabado(g, t, cel)
    return _pintar11(g, t, cel, oscuro)


g8.pintar = pintar

_tema10 = g8.tema


def tema(nombre, oscuro=False):
    t = _tema10(nombre, oscuro)
    if nombre == "romano":
        col = dict(t["col"])
        col.update(M=hx("C9CBC4") if not oscuro else hx("B7BBC0"), Z=hx("9E3445") if not oscuro else hx("A63A4D"),
                   G=hx("D2AE5A") if not oscuro else hx("F2D27A"))
        t = dict(t, col=col, acc=("capa", "galea"))
    if nombre == "colonos":
        col = dict(t["col"])
        col.update(P=hx("D6B48A"), W=hx("9A6A3E"), S=hx("A8865E"), D=hx("A8865E"), T=hx("A8865E"), L=hx("FFFFFF"), K=TINTA, E=TINTA,
                   A=hx("C0604E"), B=hx("8A3A2E"), Y=hx("F8F3E8"), M=hx("2E4E86"), Z=hx("F8F3E8"), N=hx("D98A8A"), R=hx("E8B0A0"), C=TINTA)
        papel = PAPEL if not oscuro else PAPEL_N
        t = dict(t, col=col, material="grabado", material_banda="grabado", papel=papel, tinta_g=TINTA, sombra_a=0)
    return t


g8.tema = g9.tema = g10.tema = tema


def achurar(px, mask, paso, modo, tinta):
    """Achurado de grabado: líneas diagonales (modo «d»), horizontales («h») o cruzadas («x») donde la máscara está puesta."""
    m = mask.load()
    for y in range(H):
        for x in range(W):
            if not m[x, y]: continue
            if (modo in "dx" and (x + y) % paso == 0) or (modo == "h" and y % paso == 0) or (modo == "x" and (x - y) % paso == 0):
                px[x, y] = tinta


def fondo_colonos(t):
    """Una hoja de la gaceta del pueblo: cabecera y un grabado del pueblo que se levanta (iluminado a mano).
    De noche, la misma hoja leída a la luz de una vela."""
    o = t["oscuro"]
    pap = t["papel"]
    tinta = TINTA
    ag = {k: mezcla(pap, v, 0.5) for k, v in AGUADA.items()}
    im = Image.new("RGB", (W, H), pap); d = ImageDraw.Draw(im); px = im.load()
    # Marco del grabado.
    d.rectangle([2, 14, W - 3, H - 2], outline=tinta)
    d.rectangle([4, 16, W - 5, H - 4], outline=tinta)
    # Cielo: aguada azul arriba y líneas horizontales de grabado que se abren hacia el horizonte.
    for y in range(17, 44):
        for x in range(5, W - 5):
            if y < 30 and (x * 7 + y * 13) % 5 < 3: px[x, y] = ag["cielo"]
    for y in range(17, 40, 2 if True else 3):
        if y > 32 and y % 4: continue
        for x in range(5, W - 5):
            if (x // (3 + (y - 17) // 6)) % 2 == 0 or y < 24: px[x, y] = tinta if y % 2 == 1 else px[x, y]
    # Sol con cara (a la izquierda) y nubes recortadas en papel.
    sx, sy = 34, 30
    d.ellipse([sx - 12, sy - 12, sx + 12, sy + 12], fill=pap)
    for k in range(16):
        an = 2 * math.pi * k / 16
        d.line([sx + 8 * math.cos(an), sy + 8 * math.sin(an), sx + (12 if k % 2 else 10) * math.cos(an), sy + (12 if k % 2 else 10) * math.sin(an)], fill=tinta)
    d.ellipse([sx - 7, sy - 7, sx + 7, sy + 7], fill=ag["ocre"], outline=tinta)
    d.point((sx - 2, sy - 1), fill=tinta); d.point((sx + 2, sy - 1), fill=tinta); d.arc([sx - 3, sy - 1, sx + 3, sy + 3], 20, 160, fill=tinta)
    for (cx, cy, w) in ((120, 24, 30), (260, 20, 36)):
        for k in range(0, w, 6):
            d.ellipse([cx + k - 5, cy - 4 - (2 if 0 < k < w - 6 else 0), cx + k + 5, cy + 4], fill=pap, outline=tinta)
        d.rectangle([cx - 4, cy, cx + w + 4, cy + 4], fill=pap); d.line([cx - 5, cy + 4, cx + w + 5, cy + 4], fill=tinta)
    # La serpiente de «Únete o muere», en segmentos, con su leyenda.
    sx0, sy0 = 318, 26
    for k in range(8):
        x = sx0 + k * 10
        y = sy0 + 4 * math.sin(k * 0.9)
        d.ellipse([x - 4, y - 2.5, x + 3, y + 2.5], fill=ag["verde"], outline=tinta)
        d.line([x - 2, y - 1, x + 1, y + 1], fill=tinta)
    d.polygon([(sx0 + 76, sy0 - 2), (sx0 + 84, sy0 - 3), (sx0 + 84, sy0 + 2), (sx0 + 76, sy0 + 3)], fill=ag["verde"], outline=tinta)
    d.point((sx0 + 81, sy0 - 1), fill=tinta); d.line([sx0 + 84, sy0, sx0 + 87, sy0 - 1], fill=hx("A23B2A"))
    d.line([sx0 - 6, sy0 + 6, sx0 - 3, sy0], fill=tinta)
    d.rectangle([sx0 - 6, sy0 + 9, sx0 + 84, sy0 + 15], fill=pap)
    # Lomas lejanas con achurado ralo.
    lomas = Image.new("L", (W, H), 0); dl = ImageDraw.Draw(lomas)
    perf = [(x, 46 + 3 * math.sin(x / 31) + 2 * math.sin(x / 13)) for x in range(5, W - 5)]
    dl.polygon(perf + [(W - 5, 60), (5, 60)], fill=255)
    for x, y in perf: d.point((x, y), fill=tinta)
    achurar(px, lomas, 6, "d", tinta)
    # El pueblo: casas, iglesia, un armazón de granero que los vecinos levantan con cuerdas, y el asta con la bandera.
    def casa(x, w, h, techo_h, muro):
        base = 68
        d.rectangle([x, base - h, x + w, base], fill=muro, outline=tinta)
        d.polygon([(x - 2, base - h), (x + w / 2, base - h - techo_h), (x + w + 2, base - h)], fill=ag["ladrillo"], outline=tinta)
        m = Image.new("L", (W, H), 0); ImageDraw.Draw(m).polygon([(x - 1, base - h), (x + w / 2, base - h - techo_h + 1), (x + w + 1, base - h)], fill=255)
        achurar(px, m, 3, "d", tinta)
        for vx in range(x + 3, x + w - 3, 7):
            d.rectangle([vx, base - h + 3, vx + 3, base - h + 7], fill=tinta)
            d.line([vx + 1, base - h + 3, vx + 1, base - h + 7], fill=ag["ocre"] if o else muro)
        d.rectangle([x + w // 2 - 2, base - 7, x + w // 2 + 2, base], fill=tinta)
        m2 = Image.new("L", (W, H), 0); ImageDraw.Draw(m2).rectangle([x + w - 4, base - h + 1, x + w - 1, base - 1], fill=255)
        achurar(px, m2, 2, "h", tinta)
    casa(60, 34, 18, 12, pap)
    casa(100, 26, 14, 10, ag["ocre"])
    # Armazón del granero con cuerdas y escalera.
    bx = 150
    for k in range(5): d.line([bx + k * 8, 68, bx + k * 8, 46], fill=tinta)
    d.line([bx, 46, bx + 32, 46], fill=tinta); d.line([bx, 57, bx + 32, 57], fill=tinta)
    d.line([bx - 2, 46, bx + 16, 34], fill=tinta); d.line([bx + 16, 34, bx + 34, 46], fill=tinta)
    for k in range(4): d.line([bx + k * 8, 57, bx + k * 8 + 8, 46], fill=tinta)
    d.line([bx + 16, 34, bx + 46, 66], fill=tinta); d.line([bx + 16, 34, bx - 14, 66], fill=tinta)
    d.line([bx + 38, 68, bx + 44, 50], fill=tinta); d.line([bx + 42, 68, bx + 48, 50], fill=tinta)
    for k in range(1, 6): d.line([bx + 38 + k, 68 - k * 3, bx + 42 + k, 68 - k * 3], fill=tinta)
    # Asta con la bandera (aguada azul, anillo de trece estrellas en papel).
    fx = 216
    d.line([fx, 68, fx, 30], fill=tinta); d.ellipse([fx - 1, 28, fx + 1, 30], fill=tinta)
    # Iglesia con campanario.
    ix = 262
    d.rectangle([ix, 50, ix + 30, 68], fill=pap, outline=tinta)
    d.polygon([(ix - 2, 50), (ix + 15, 40), (ix + 32, 50)], fill=ag["azul"], outline=tinta)
    d.rectangle([ix + 10, 30, ix + 20, 50], fill=pap, outline=tinta); d.polygon([(ix + 10, 30), (ix + 15, 18), (ix + 20, 30)], fill=ag["azul"], outline=tinta)
    d.line([ix + 15, 18, ix + 15, 14], fill=tinta)
    d.arc([ix + 12, 33, ix + 18, 41], 180, 360, fill=tinta); d.rectangle([ix + 12, 37, ix + 18, 41], fill=tinta)
    for vx in (ix + 4, ix + 22): d.rectangle([vx, 54, vx + 3, 60], fill=tinta)
    d.rectangle([ix + 13, 60, ix + 17, 68], fill=tinta)
    m = Image.new("L", (W, H), 0); ImageDraw.Draw(m).rectangle([ix + 26, 51, ix + 29, 67], fill=255); achurar(px, m, 2, "h", tinta)
    casa(304, 30, 16, 11, mezcla(pap, AGUADA["azul"], 0.25))
    casa(350, 40, 20, 14, pap)
    # Árboles de grabado: copa festoneada, la mitad de abajo achurada.
    for (tx, ty, r) in ((24, 58, 9), (132, 60, 7), (240, 58, 8), (396, 58, 8)):
        d.line([tx, ty + r - 2, tx, 68], fill=tinta)
        d.ellipse([tx - r, ty - r, tx + r, ty + r], fill=ag["verde"], outline=tinta)
        m = Image.new("L", (W, H), 0); ImageDraw.Draw(m).chord([tx - r + 1, ty - r + 1, tx + r - 1, ty + r - 1], 20, 160, fill=255)
        achurar(px, m, 2, "d", tinta)
        for k in range(8):
            an = 2 * math.pi * k / 8
            d.arc([tx + r * 0.8 * math.cos(an) - 3, ty + r * 0.8 * math.sin(an) - 3, tx + r * 0.8 * math.cos(an) + 3, ty + r * 0.8 * math.sin(an) + 3],
                  math.degrees(an) - 70, math.degrees(an) + 70, fill=tinta)
    # Suelo: línea y achurado cruzado denso.
    d.line([5, 68, W - 5, 68], fill=tinta)
    piso = Image.new("L", (W, H), 0); ImageDraw.Draw(piso).rectangle([5, 69, W - 6, H - 5], fill=255)
    achurar(px, piso, 3, "h", tinta)
    for x in range(8, W - 8, 9): d.line([x, 70, x + 2, 69], fill=tinta)
    big = im.resize((W * ESC, H * ESC), Image.NEAREST)
    big = g8.papel(big, 0.05 if not o else 0.04)
    dd = ImageDraw.Draw(big)
    # Cabecera de la gaceta.
    dd.line([2 * ESC, 1 * ESC, (W - 2) * ESC, 1 * ESC], fill=tinta, width=2); dd.line([2 * ESC, 2.4 * ESC, (W - 2) * ESC, 2.4 * ESC], fill=tinta, width=1)
    dd.text((W * ESC / 2, 7.5 * ESC), "LA GACETA DEL PUEBLO", font=fuente("DejaVuSerif-Bold.ttf", 22), fill=tinta, anchor="mm")
    dd.text((4 * ESC, 7.5 * ESC), "Núm. 76 · Año II", font=fuente("DejaVuSerif.ttf", 13), fill=tinta, anchor="lm")
    dd.text(((W - 4) * ESC, 7.5 * ESC), "Precio: 2 chapas", font=fuente("DejaVuSerif.ttf", 13), fill=tinta, anchor="rm")
    dd.line([2 * ESC, 12.2 * ESC, (W - 2) * ESC, 12.2 * ESC], fill=tinta, width=3)
    dd.text(((sx0 + 39) * ESC, (sy0 + 12.3) * ESC), "ÚNETE  O  MUERE", font=fuente("DejaVuSerif-Bold.ttf", 12), fill=tinta, anchor="mm")
    vela = (W - 22, 66)
    mascaras = []
    if o:
        import numpy as np
        yy, xx = np.mgrid[0:H * ESC, 0:W * ESC].astype(np.float32)
        for k, r in enumerate((430, 450, 420, 440)):
            dist = np.hypot(xx - vela[0] * ESC, (yy - (vela[1] - 12) * ESC) * 1.6)
            f = np.clip(1.1 - (dist / (r * ESC / 2.2)) ** 1.6, 0.5, 1.0)
            mascaras.append(Image.fromarray(np.stack([f * 255, f * 236, f * 205], -1).astype(np.uint8)))

    def bandera(img, tick, delante):
        dd = ImageDraw.Draw(img)
        if not delante:
            for i in range(18):
                off = math.sin(tick * 0.3 - i * 0.45) * (0.2 + i * 0.05)
                for j in range(11):
                    x, y = fx + 1 + i, 29 + j + off
                    ring = math.hypot(i - 8.5, j - 5)
                    col = pap if 3.6 < ring < 4.6 and (round(math.degrees(math.atan2(j - 5, i - 8.5))) % 28 < 10) else ag["azul"]
                    if j in (0, 10) or i == 17: col = tinta
                    cuad(dd, x, y, col)
            return
        if o:
            k = mascaras[(tick // 2 + (tick // 7) % 3) % len(mascaras)]
            img.paste(ImageChops.multiply(img, k))
            vx, vy = vela
            cuad(dd, vx - 3, vy - 10, hx("EFE6CF"), 6, 12); cuad(dd, vx - 3, vy - 10, hx("D9CDB0"), 2, 12)
            cuad(dd, vx - 6, vy + 2, hx("8A6A3E"), 12, 2)
            llama(dd, vx, vy - 10, tick, 0.7)
    ANIM[("colonos", o)] = [bandera]
    return big


# =====================================================================
#  Mosaico romano con más detalle: campo de mármol con crucecitas, filetes y tres paneles (emblemas) en teselas finas:
#  dos peces, una corona de laurel con «SALVE» (como los mosaicos de bienvenida) y una perdiz con cerezas.
# =====================================================================
LETRAS = {"S": ["0111", "1000", "0110", "0001", "1110"], "A": ["0110", "1001", "1111", "1001", "1001"], "L": ["1000", "1000", "1000", "1000", "1111"],
          "V": ["1001", "1001", "1001", "0110", "0110"], "E": ["1111", "1000", "1110", "1000", "1111"]}


def fondo_romano(t):
    tinta, porf, oro, bron, crema = hx("17191C"), hx("7C2433"), hx("B8892B"), hx("2D7A6F"), hx("F2EEE2")
    rojo, verde, verde_l, azul, ocre, gris = hx("9E3445"), hx("3F6B3A"), hx("6E9A4A"), hx("3E6E9A"), hx("C9934A"), hx("8A8C88")
    marmol = [hx("ECEDEA"), hx("E4E2DA"), hx("F2F1EC"), hx("E8E6DF"), hx("ECEDEA"), hx("DEDBD2")]
    # Capa gruesa (1 tesela = 1 dp).
    G_ = Image.new("RGB", (W, H), marmol[0]); dg = ImageDraw.Draw(G_); pg = G_.load()
    rnd = random.Random(5)
    for y in range(H):
        for x in range(W):
            pg[x, y] = rnd.choice(marmol)
    greca = ["XXXXXXX.", "X.....X.", "X.XXX.X.", "X.X...X.", "X.XXXXXX"]
    for x in range(W):
        for y in (0, 6, 74):
            pg[x, y] = porf
        for k in range(5):
            pg[x, 1 + k] = tinta if greca[k][x % 8] == "X" else crema
            pg[x, 75 + k] = tinta if greca[k][(x + 4) % 8] == "X" else crema
    # Trenza (guilloche) bajo la greca de arriba.
    for x in range(W):
        u = x % 8
        pg[x, 7] = crema
        pg[x, 8] = rojo if u in (1, 2, 5, 6) else tinta
        pg[x, 9] = oro if u in (0, 3, 4, 7) else crema
        pg[x, 10] = tinta if u in (1, 2, 5, 6) else rojo
        pg[x, 11] = crema
    # Crucecitas negras en el campo.
    for y in range(18, 72, 8):
        for x in range(4 + (y // 8 % 2) * 4, W, 8):
            for dx, dy in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1)):
                pg[x + dx, y + dy] = tinta if (dx, dy) != (0, 0) else porf
    paneles = [(26, 16, 112, 70), (163, 14, 249, 72), (300, 16, 386, 70)]
    for (x0, y0, x1, y1) in paneles:
        dg.rectangle([x0 - 3, y0 - 3, x1 + 3, y1 + 3], fill=tinta)
        dg.rectangle([x0 - 2, y0 - 2, x1 + 2, y1 + 2], fill=porf)
        dg.rectangle([x0 - 1, y0 - 1, x1 + 1, y1 + 1], fill=tinta)
        for x in range(x0 - 2, x1 + 3, 2): pg[x, y0 - 2] = oro; pg[x, y1 + 2] = oro
    # Capa fina (2×2 teselas por dp) para el interior de los paneles: «opus vermiculatum».
    F2 = Image.new("RGB", (W * 2, H * 2), crema); df = ImageDraw.Draw(F2); pf = F2.load()
    rnd2 = random.Random(9)
    for y in range(H * 2):
        for x in range(W * 2):
            pf[x, y] = rnd2.choice([hx("F4F2EA"), hx("EEEBE2"), hx("F7F5EF"), hx("E9E5DA")])
    def ojo(x, y): df.rectangle([x, y, x + 1, y + 1], fill=tinta); pf[x, y] = hx("FFFFFF")
    # Panel 1: dos peces sobre olas.
    (x0, y0, x1, y1) = [v * 2 for v in paneles[0]]
    for k in range(4):
        yy = y1 - 10 - k * 5
        for x in range(x0 + 4, x1 - 4):
            if (x + k * 3) % 12 < 6: pf[x, yy + (1 if (x // 3) % 2 else 0)] = azul if k % 2 == 0 else mezcla(azul, crema, 0.5)
    for (cx, cy, dir_, col, col2) in ((x0 + 60, y0 + 34, 1, bron, hx("7FC0B2")), (x0 + 108, y0 + 66, -1, rojo, hx("E0A0A8"))):
        df.ellipse([cx - 22, cy - 11, cx + 22, cy + 11], fill=col, outline=tinta)
        df.chord([cx - 21, cy - 10, cx + 21, cy + 10], 20, 160, fill=col2)
        df.polygon([(cx - 21 * dir_, cy), (cx - 34 * dir_, cy - 11), (cx - 32 * dir_, cy), (cx - 34 * dir_, cy + 11)], fill=col, outline=tinta)
        df.polygon([(cx - 4, cy - 10), (cx + 4 * dir_, cy - 17), (cx + 8 * dir_, cy - 10)], fill=col, outline=tinta)
        for k in range(-12, 13, 5): df.arc([cx + k - 3, cy - 6, cx + k + 3, cy], 0, 180, fill=mezcla(col, tinta, 0.4))
        ojo(cx + 15 * dir_ - 1, cy - 4)
        df.line([cx + 20 * dir_, cy + 2, cx + 17 * dir_, cy + 3], fill=tinta)
    # Panel 2: corona de laurel con «SALVE».
    (x0, y0, x1, y1) = [v * 2 for v in paneles[1]]
    cx, cy = (x0 + x1) // 2, (y0 + y1) // 2
    for k in range(34):
        an = math.radians(110 + k * 9.7)
        if 255 < math.degrees(an) % 360 < 285: continue
        x, y = cx + 34 * math.cos(an), cy + 30 * math.sin(an)
        rot_ = math.degrees(an) + 90
        hoja = Image.new("RGBA", (12, 6), (0, 0, 0, 0)); ImageDraw.Draw(hoja).ellipse([0, 0, 11, 5], fill=(verde if k % 2 else verde_l) + (255,), outline=tinta + (255,))
        hoja = hoja.rotate(-rot_ + (25 if k % 2 else -25), expand=True)
        F2.paste(hoja, (int(x - hoja.width / 2), int(y - hoja.height / 2)), hoja)
        if k % 4 == 1: df.ellipse([x - 2, y - 2, x + 2, y + 2], fill=oro, outline=tinta)
    df.polygon([(cx - 4, cy + 30), (cx - 14, cy + 44), (cx - 8, cy + 44), (cx, cy + 34), (cx + 8, cy + 44), (cx + 14, cy + 44), (cx + 4, cy + 30)], fill=rojo, outline=tinta)
    df.ellipse([cx - 4, cy + 27, cx + 4, cy + 33], fill=rojo, outline=tinta)
    for j, ch in enumerate("SALVE"):
        for r_, fila in enumerate(LETRAS[ch]):
            for c_, b in enumerate(fila):
                if b == "1":
                    df.rectangle([cx - 27 + j * 11 + c_ * 2, cy - 6 + r_ * 2, cx - 27 + j * 11 + c_ * 2 + 1, cy - 6 + r_ * 2 + 1], fill=tinta)
    # Panel 3: una perdiz con una rama de cerezas.
    (x0, y0, x1, y1) = [v * 2 for v in paneles[2]]
    bx, by = x0 + 70, y0 + 64
    df.line([x0 + 8, y1 - 14, x1 - 8, y1 - 18], fill=hx("6A4A2A"), width=2)
    for k, (cx_, cy_) in enumerate(((x0 + 30, y1 - 26), (x0 + 38, y1 - 22), (x1 - 34, y1 - 30), (x1 - 26, y1 - 26))):
        df.line([cx_, cy_, cx_ + 2, y1 - 16], fill=verde)
        df.ellipse([cx_ - 4, cy_ - 4, cx_ + 4, cy_ + 4], fill=rojo, outline=tinta); pf[cx_ - 1, cy_ - 2] = hx("FFFFFF")
    for (hx_, hy_) in ((x0 + 20, y1 - 22), (x1 - 18, y1 - 30)):
        df.ellipse([hx_ - 6, hy_ - 3, hx_ + 6, hy_ + 3], fill=verde_l, outline=tinta)
    df.ellipse([bx - 24, by - 26, bx + 18, by + 6], fill=ocre, outline=tinta)
    df.chord([bx - 22, by - 24, bx + 16, by + 4], 10, 170, fill=hx("E8D2A8"))
    for k in range(6): df.arc([bx - 18 + k * 6, by - 18, bx - 12 + k * 6, by - 12], 0, 180, fill=hx("7A4A22"))
    df.ellipse([bx + 8, by - 38, bx + 26, by - 20], fill=ocre, outline=tinta)
    df.polygon([(bx + 25, by - 31), (bx + 33, by - 28), (bx + 25, by - 26)], fill=rojo, outline=tinta)
    ojo(bx + 18, by - 32); df.arc([bx + 15, by - 35, bx + 23, by - 27], 200, 340, fill=rojo)
    df.polygon([(bx - 24, by - 14), (bx - 38, by - 22), (bx - 36, by - 8)], fill=hx("7A4A22"), outline=tinta)
    for lx in (bx - 6, bx + 4):
        df.line([lx, by + 4, lx, y1 - 16], fill=rojo, width=2)
    # Pintar: teselas gruesas fuera de los paneles, finas dentro (con algo de tono y forma irregular por tesela).
    s = ESC * SS
    img = Image.new("RGB", (W * s, H * s), t["junta"]); d = ImageDraw.Draw(img)
    def tesela(x0, y0, lado, c, semilla):
        r_ = random.Random(semilla)
        k = 1 + r_.uniform(-0.06, 0.06)
        c = tuple(max(0, min(255, round(ch * k + r_.uniform(-5, 5)))) for ch in c)
        j = lambda: r_.uniform(-0.08, 0.08) * lado
        gp = 0.15 * lado
        a, b, c2, d2 = x0 + gp / 2, y0 + gp / 2, x0 + lado - gp / 2, y0 + lado - gp / 2
        d.polygon([(a + j(), b + j()), (c2 + j(), b + j()), (c2 + j(), d2 + j()), (a + j(), d2 + j())], fill=c)
    dentro = lambda x, y: any(p[0] <= x <= p[2] and p[1] <= y <= p[3] for p in paneles)
    for y in range(H):
        for x in range(W):
            if dentro(x, y):
                for sy in range(2):
                    for sx in range(2):
                        tesela(x * s + sx * s / 2, y * s + sy * s / 2, s / 2, pf[x * 2 + sx, y * 2 + sy], x * 7919 + y * 104729 + sx * 31 + sy * 17)
            else:
                tesela(x * s, y * s, s, pg[x, y], x * 7919 + y * 104729)
    return img.resize((W * ESC, H * ESC), Image.LANCZOS)


g8.FONDOS.update(romano=fondo_romano, colonos=fondo_colonos)
g9.FONDOS9["colonos"] = fondo_colonos
g9.ETIQ.update(colonos="Colonos · una hoja de la gaceta del pueblo", romano="Romano · un mosaico que cobra vida")
g9.NOCHE.update(colonos="la gaceta a la luz de una vela")


if __name__ == "__main__":
    que = sys.argv[1:] or ["banda", "hoja"]
    s_, cs = g8.buscar_semilla()
    if "banda" in que:
        if "colonos" not in que: g8.gif_banda(cs, [("romano", False, g9.ETIQ["romano"])])
        g8.gif_banda(cs, [("colonos", False, g9.ETIQ["colonos"])])
        g8.gif_banda(cs, [("colonos", True, "Colonos · de noche: " + g9.NOCHE["colonos"])], "_oscuro")
    if "hoja" in que:
        if "colonos" not in que: g8.hoja("romano", False, "hoja_sprites_romano.png", material=True)
        g8.hoja("colonos", False, "hoja_sprites_colonos.png", material=True)
    print("listo")
