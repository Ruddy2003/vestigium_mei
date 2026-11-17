"""
Lámina de muestra del tema Psicodelia rehecho (16 nov 2026): las dos caras (la ciudad de día y la psicodelia de noche),
cada una en claro y en oscuro, más la psicodelia oscura con intensidad 0.5 y 1. Es una propuesta de dirección de arte
para enseñarle al autor; no es código de la app. Los valores salen de `docs/diseno/temas/psicodelia.md`.

Cada variante lleva: su fondo, su motivo de dibujo (hecho aquí con formas), un título y un texto con las letras
propuestas, una tarjeta con tres fichas de cifras, un botón principal y uno secundario, y su paleta con el contraste.

Letras: Libre Franklin (OFL) y Chango (OFL; reserva su nombre, en la app irá renombrada). Se bajan de google/fonts a
una carpeta de caché fuera del repositorio. Sin red, se usan dos letras que ya están en `res/font/` y se avisa.

Uso:  pip install fonttools pillow
      python3 herramientas/muestras/psicodelia_caras.py [--cache CARPETA] [--salida RUTA.png]
      python3 herramientas/muestras/psicodelia_caras.py --contraste      (solo la tabla de contrastes)
"""
import argparse
import math
import os
import random
import urllib.request

from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont

RAIZ = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SALIDA = os.path.join(RAIZ, "docs", "diseno", "temas", "muestras", "psicodelia_caras.png")
GF = "https://raw.githubusercontent.com/google/fonts/main/ofl/"
S = 2  # se dibuja al doble y se reduce al final, para que los bordes salgan suaves

# =====================================================================================================================
#  Las cuatro paletas (las de la guía) y las tintas de dibujo
# =====================================================================================================================

PALETAS = {
    "ciudad_claro": dict(
        fondo="#E9DFCC", superficie="#F3ECDD", variante="#DCD0B9", borde="#A39478", tinta="#2A2119", tenue="#5C4F41",
        primario="#9C4219", sobre_primario="#FFF4E8", contenedor="#F1D3BD", sobre_contenedor="#3A1906",
        secundario="#3D5838", contenedor_secundario="#D3DCC4", terciario="#7A5713", error="#A01E2C"),
    "ciudad_oscuro": dict(
        fondo="#1B1511", superficie="#241C16", variante="#2F251D", borde="#54463A", tinta="#EEE3D0", tenue="#B9AA94",
        primario="#E8975A", sobre_primario="#2A1103", contenedor="#5A2E12", sobre_contenedor="#FBDDC6",
        secundario="#A3BB93", contenedor_secundario="#2E3B29", terciario="#D9B865", error="#FF8E84"),
    "psico_claro": dict(
        fondo="#F2E8D3", superficie="#F9F3E6", variante="#E7DBC2", borde="#3A3458", tinta="#1E1A3A", tenue="#4E4868",
        primario="#974164", sobre_primario="#FFF2F7", contenedor="#F3D3DF", sobre_contenedor="#3F0520",
        secundario="#404D99", contenedor_secundario="#D6DAF0", terciario="#6A3E8E", error="#A8381E"),
    "psico_oscuro": dict(
        fondo="#0F0C17", superficie="#17131F", variante="#211B2B", borde="#6E6488", tinta="#F3E9D6", tenue="#B9AFC4",
        primario="#DD80A4", sobre_primario="#2B0716", contenedor="#5C1438", sobre_contenedor="#FFD9EA",
        secundario="#94A1DB", contenedor_secundario="#232C66", terciario="#B79BE0", error="#FF8E70"),
}

# Con intensidad, en la psicodelia cambian solo el primario, el secundario y el terciario (a luminosidad fija, así que
# el contraste no baja) y las tintas de dibujo. Lo demás (fondo, superficies, tinta, tenue, bordes) no se mueve nunca.
ROLES_INTENSOS = {
    "psico_claro": {0.5: dict(primario="#A43164", secundario="#3B47AD", terciario="#6F3399"),
                    1.0: dict(primario="#B11265", secundario="#383EC1", terciario="#7428A6")},
    "psico_oscuro": {0.5: dict(primario="#EC73A5", secundario="#909EEB", terciario="#BE95F0"),
                     1.0: dict(primario="#FA63A5", secundario="#8C9CFB", terciario="#C58CFF")},
}
TINTAS = {  # magenta, bermellón, ultramar: de reposo (i = 0) a plenas (i = 1)
    "psico_claro": {0.0: ("#A94870", "#C56851", "#3F4C9E"), 0.5: ("#B73570", "#D55A3C", "#3A44B3"), 1.0: ("#C50E70", "#E5481E", "#3739C9")},
    "psico_oscuro": {0.0: ("#DC7BA1", "#E0907B", "#8693D7"), 0.5: ("#EC6DA2", "#EF876B", "#8190EA"), 1.0: ("#FB5BA2", "#FD7C5A", "#7C8CFC")},
}
CREMA_TINTA = "#F3E9D6"  # el papel, usado como cuarta tinta sobre el negro


def rgb(h, a=255):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)


def mezcla(a, b, t):
    a, b = rgb(a), rgb(b)
    return "#%02X%02X%02X" % tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def luminancia(h):
    def f(c):
        c /= 255
        return c / 12.92 if c <= 0.04045 else ((c + 0.055) / 1.055) ** 2.4
    r, g, b, _ = rgb(h)
    return 0.2126 * f(r) + 0.7152 * f(g) + 0.0722 * f(b)


def contraste(a, b):
    x, y = sorted([luminancia(a), luminancia(b)], reverse=True)
    return (x + 0.05) / (y + 0.05)


# =====================================================================================================================
#  Letras
# =====================================================================================================================

def bajar(url, destino):
    if not os.path.exists(destino):
        urllib.request.urlretrieve(url, destino)
    return destino


def letras(cache):
    """Las letras propuestas; si no hay red, dos de `res/font/` (y lo dice)."""
    os.makedirs(cache, exist_ok=True)
    try:
        from fontTools.ttLib import TTFont
        from fontTools.varLib import instancer
        franklin = bajar(GF + "librefranklin/LibreFranklin%5Bwght%5D.ttf", os.path.join(cache, "LibreFranklin.ttf"))
        chango = bajar(GF + "chango/Chango-Regular.ttf", os.path.join(cache, "Chango.ttf"))
        res = {"bloque": chango}
        for peso in (400, 600, 800):
            p = os.path.join(cache, f"franklin_{peso}.ttf")
            if not os.path.exists(p):
                f = TTFont(franklin)
                instancer.instantiateVariableFont(f, {"wght": peso}, inplace=True)
                f.save(p)
            res[peso] = p
        res["aviso"] = None
        return res
    except Exception as e:  # sin red o sin fontTools
        fuente = os.path.join(RAIZ, "app", "src", "main", "res", "font")
        print("Aviso: no se pudieron bajar las letras propuestas (", e, "); se usan las de res/font.")
        return {400: os.path.join(fuente, "overpass_regular.ttf"), 600: os.path.join(fuente, "overpass_semibold.ttf"),
                800: os.path.join(fuente, "overpass_semibold.ttf"), "bloque": os.path.join(fuente, "russo.ttf"),
                "aviso": "Letras de sustitución (sin red): Overpass y Russo One, no las propuestas."}


F = {}


def fuente(clave, px):
    k = (clave, px)
    if k not in F:
        F[k] = ImageFont.truetype(LETRAS[clave], int(px * S))
    return F[k]


def texto(d, xy, s, clave, px, color, espacio=0.0, ancla="la"):
    """Texto con espaciado entre letras (en fracción del cuerpo). Devuelve el ancho."""
    f = fuente(clave, px)
    x, y = xy[0] * S, xy[1] * S
    if espacio == 0:
        d.text((x, y), s, font=f, fill=color, anchor=ancla)
        return d.textlength(s, font=f) / S
    ancho = sum(d.textlength(c, font=f) for c in s) + espacio * px * S * (len(s) - 1)
    if ancla[0] == "m":
        x -= ancho / 2
    elif ancla[0] == "r":
        x -= ancho
    for c in s:
        d.text((x, y), c, font=f, fill=color, anchor="l" + ancla[1])
        x += d.textlength(c, font=f) + espacio * px * S
    return ancho / S


# =====================================================================================================================
#  Formas
# =====================================================================================================================

def caja(d, x0, y0, x1, y1, r, relleno=None, linea=None, ancho=1, forma="redonda"):
    """Rectángulo con las esquinas del tema: «redonda» (las cuatro) o «arco» (arriba redondas, abajo casi rectas)."""
    x0, y0, x1, y1, r = x0 * S, y0 * S, x1 * S, y1 * S, r * S
    if forma == "redonda":
        d.rounded_rectangle((x0, y0, x1, y1), r, fill=relleno, outline=linea, width=int(ancho * S))
    else:  # arco: la esquina de arriba es un cuarto de círculo grande; la de abajo, de 2 dp
        poco = 2 * S
        pts = []
        for i in range(19):
            a = math.pi + i * (math.pi / 2) / 18
            pts.append((x0 + r + r * math.cos(a), y0 + r + r * math.sin(a)))
        for i in range(19):
            a = 1.5 * math.pi + i * (math.pi / 2) / 18
            pts.append((x1 - r + r * math.cos(a), y0 + r + r * math.sin(a)))
        pts += [(x1, y1 - poco), (x1 - poco, y1), (x0 + poco, y1), (x0, y1 - poco)]
        d.polygon(pts, fill=relleno, outline=linea, width=int(ancho * S) if linea else 0)


def mascara(w, h, r, forma):
    m = Image.new("L", (w * S, h * S), 0)
    caja(ImageDraw.Draw(m), 0, 0, w, h, r, relleno=255, forma=forma)
    return m


def grano(img, fuerza, semilla, color=False):
    rnd = random.Random(semilla)
    w, h = img.size
    ruido = Image.effect_noise((w // 2, h // 2), 64).resize((w, h), Image.BILINEAR)
    capa = Image.merge("RGB", (ruido, ruido, ruido))
    if color:  # el grano de color de la película: un poco cálido en las sombras
        r, g, b = capa.split()
        capa = Image.merge("RGB", (r, g.point(lambda v: v * 0.97), b.point(lambda v: v * 0.9)))
    base = img.convert("RGB")
    mezclada = ImageChops.overlay(base, capa)
    out = Image.blend(base, mezclada, fuerza)
    _ = rnd
    return out.convert("RGBA")


def vineta(img, fuerza, color="#000000"):
    w, h = img.size
    m = Image.new("L", (w, h), 0)
    ImageDraw.Draw(m).ellipse((-w * 0.25, -h * 0.2, w * 1.25, h * 1.2), fill=255)
    m = m.filter(ImageFilter.GaussianBlur(w * 0.12))
    oscuro = Image.new("RGBA", (w, h), rgb(color))
    return Image.composite(img, Image.blend(img, oscuro, fuerza), m)


# =====================================================================================================================
#  Los motivos
# =====================================================================================================================

def motivo_ventanilla(w, h, p):
    """Ciudad · claro: la ventanilla del trolebús por la tarde, con las victorianas grises (una pintada), los postes, los
    cables del trolebús y el pasamanos que cruza por delante. Luz de la izquierda; color de foto vieja."""
    W, H = w * S, h * S
    img = Image.new("RGBA", (W, H), rgb("#CDBFA6"))
    d = ImageDraw.Draw(img)
    # El cielo de la tarde: bruma arriba, luz cálida abajo, con la niebla que entra a ras.
    for y in range(H):
        t = y / H
        c = mezcla("#D9CDB6", "#EDBE8C", min(1, t * 1.25)) if t < 0.8 else "#EDBE8C"
        d.line((0, y, W, y), fill=c)
    d.ellipse((W * 0.08, H * 0.42, W * 0.2, H * 0.42 + W * 0.12), fill=rgb("#F6DDB4"))
    niebla = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    dn = ImageDraw.Draw(niebla)
    for i in range(4):
        y = H * (0.5 + i * 0.03)
        dn.ellipse((W * (0.3 + i * 0.12), y, W * (1.1 + i * 0.1), y + H * 0.08), fill=rgb("#F1E6D2", 90))
    img.alpha_composite(niebla.filter(ImageFilter.GaussianBlur(10 * S)))
    # Las casas victorianas: miradores, cornisas; casi todas grises, una de colores apagados por la foto.
    rnd = random.Random(7)
    x = -10 * S
    suelo = H * 0.86
    colores = ["#A0968A", "#958B7E", "#A89C8C", "#7E958E", "#9B9081", "#AEA293"]
    i = 0
    while x < W:
        ancho = rnd.randint(52, 64) * S
        alto = rnd.randint(98, 124) * S
        col = colores[i % len(colores)]
        claro = mezcla(col, "#F4E6CC", 0.35)  # lo que mira al sol de la tarde (a la izquierda)
        sombra = mezcla(col, "#4A3528", 0.42)
        top = suelo - alto
        gablete = i % 3 == 1
        d.rectangle((x, top, x + ancho, suelo), fill=col)
        if gablete:  # el frontón en punta de las casas de estilo Stick, con su moldura
            d.polygon([(x - 2 * S, top), (x + ancho / 2, top - 22 * S), (x + ancho + 2 * S, top)], fill=claro)
            d.polygon([(x + ancho / 2, top - 22 * S), (x + ancho + 2 * S, top), (x + ancho / 2, top)], fill=col)
            d.line((x - 2 * S, top, x + ancho / 2, top - 22 * S, x + ancho + 2 * S, top), fill=sombra, width=2 * S)
        else:  # la cornisa italianizante, volada, con sus ménsulas
            d.rectangle((x - 4 * S, top - 7 * S, x + ancho + 4 * S, top), fill=claro)
            d.rectangle((x - 4 * S, top, x + ancho + 4 * S, top + 3 * S), fill=sombra)
            for j in range(6):
                bx = x + j * ancho / 5.4
                d.rectangle((bx, top + 3 * S, bx + 2.4 * S, top + 8 * S), fill=sombra)
        # El mirador de dos pisos en saliente: tres paños, el de la izquierda al sol y el de la derecha en sombra.
        mx0, mx1 = x + ancho * 0.08, x + ancho * 0.6
        tercio = (mx1 - mx0) / 3
        d.rectangle((mx0, top + 12 * S, mx0 + tercio, suelo - 8 * S), fill=claro)
        d.rectangle((mx0 + tercio, top + 12 * S, mx1 - tercio, suelo - 8 * S), fill=mezcla(col, "#F4E6CC", 0.12))
        d.rectangle((mx1 - tercio, top + 12 * S, mx1, suelo - 8 * S), fill=sombra)
        for k in range(2):
            yy = top + 20 * S + k * 42 * S
            for j in range(3):
                vx = mx0 + j * tercio
                d.rectangle((vx + 3 * S, yy, vx + tercio - 3 * S, yy + 28 * S), fill=rgb(mezcla("#4A3D33", "#E8C9A0", 0.4 if j == 0 else 0.12)))
            d.rectangle((mx0 - 1 * S, yy + 30 * S, mx1 + 1 * S, yy + 33 * S), fill=sombra)
        # La ventana alta del otro lado, de guillotina, y la sombra que la casa echa sobre la de su derecha.
        wx = x + ancho * 0.7
        for k in range(2):
            yy = top + 22 * S + k * 42 * S
            d.rectangle((wx, yy, wx + ancho * 0.18, yy + 28 * S), fill=rgb("#4A3D33"))
            d.rectangle((wx, yy + 13 * S, wx + ancho * 0.18, yy + 15 * S), fill=col)
        d.rectangle((x + ancho - 4 * S, top, x + ancho, suelo), fill=sombra)
        x += ancho + rnd.randint(0, 3) * S
        i += 1
    # La acera y un coche en sombra.
    d.rectangle((0, suelo, W, H), fill=rgb("#6B5D4E"))
    d.rounded_rectangle((W * 0.55, suelo - 14 * S, W * 0.92, suelo + 6 * S), 7 * S, fill=rgb("#7A3424"))
    d.rounded_rectangle((W * 0.62, suelo - 24 * S, W * 0.82, suelo - 10 * S), 5 * S, fill=rgb("#6A2E22"))
    # Los postes y los cables del trolebús: dos cables paralelos que cuelgan, y los tirantes.
    for px_ in (W * 0.33, W * 0.9):
        d.rectangle((px_, H * 0.12, px_ + 5 * S, suelo), fill=rgb("#3E3128"))
        d.rectangle((px_ - 16 * S, H * 0.2, px_ + 21 * S, H * 0.2 + 3 * S), fill=rgb("#3E3128"))
    for k, y0 in enumerate((0.26, 0.29)):
        pts = [(xx, H * y0 + 10 * S * math.sin(math.pi * xx / W) + k * 2 * S) for xx in range(0, W + 1, 8)]
        d.line(pts, fill=rgb("#3A2E26"), width=int(1.4 * S))
    d.line((0, H * 0.18, W, H * 0.24), fill=rgb("#3A2E26"), width=int(1.2 * S))
    # El pasamanos que cruza por delante (con su brillo a la izquierda), y el respaldo del asiento abajo.
    hx = W * 0.7
    d.rectangle((hx, 0, hx + 11 * S, H), fill=rgb("#8C8270"))
    d.rectangle((hx + 2 * S, 0, hx + 4 * S, H), fill=rgb("#D8CDB5"))
    d.rectangle((hx + 8 * S, 0, hx + 11 * S, H), fill=rgb("#5E5547"))
    d.rounded_rectangle((-20 * S, H * 0.9, W * 0.58, H + 30 * S), 14 * S, fill=rgb("#6B4A33"))
    d.rounded_rectangle((-20 * S, H * 0.9, W * 0.58, H * 0.9 + 5 * S), 3 * S, fill=rgb("#8A6447"))
    img = vineta(img, 0.28, "#3A2410")
    img = grano(img, 0.22, 1, color=True)
    # El marco de la ventanilla, pintado de crema, con su junta de goma.
    marco = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    dm = ImageDraw.Draw(marco)
    dm.rounded_rectangle((0, 0, W - 1, H - 1), 22 * S, outline=rgb("#B9AB92"), width=10 * S)
    dm.rounded_rectangle((10 * S, 10 * S, W - 10 * S, H - 10 * S), 14 * S, outline=rgb("#3A2E26"), width=3 * S)
    img.alpha_composite(marco)
    return img


def motivo_tocadiscos(w, h, p):
    """Ciudad · oscuro: el tocadiscos de maleta bajo la lámpara de pantalla, con el disco del día girando. La luz sale
    de la lámpara (arriba a la izquierda) y cae sobre la pared y el canto del disco."""
    W, H = w * S, h * S
    img = Image.new("RGBA", (W, H), rgb("#1F1712"))
    d = ImageDraw.Draw(img)
    # La pared con la mancha de la lámpara.
    luz = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    dl = ImageDraw.Draw(luz)
    for k in range(10, 0, -1):
        r = k * 26 * S
        dl.ellipse((W * 0.22 - r, H * 0.22 - r * 0.8, W * 0.22 + r, H * 0.22 + r * 0.8), fill=rgb("#C97B3A", 16))
    img.alpha_composite(luz.filter(ImageFilter.GaussianBlur(14 * S)))
    # El cono de luz hacia abajo.
    cono = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(cono).polygon([(W * 0.12, H * 0.3), (W * 0.33, H * 0.3), (W * 0.62, H), (-W * 0.15, H)], fill=rgb("#F0A35A", 40))
    img.alpha_composite(cono.filter(ImageFilter.GaussianBlur(16 * S)))
    # La lámpara: pantalla plisada encendida, pie torneado.
    d.polygon([(W * 0.13, H * 0.06), (W * 0.31, H * 0.06), (W * 0.36, H * 0.3), (W * 0.08, H * 0.3)], fill=rgb("#F2C996"))
    for k in range(9):
        x0 = W * 0.13 + k * (W * 0.18) / 8
        x1 = W * 0.08 + k * (W * 0.28) / 8
        d.line((x0, H * 0.06, x1, H * 0.3), fill=rgb("#D9A06A"), width=S)
    d.rectangle((W * 0.08, H * 0.29, W * 0.36, H * 0.31), fill=rgb("#B9733E"))
    d.rectangle((W * 0.215, H * 0.31, W * 0.235, H * 0.62), fill=rgb("#8A5A34"))
    # La mesa.
    d.rectangle((0, H * 0.7, W, H), fill=rgb("#3B2A1E"))
    d.rectangle((0, H * 0.7, W, H * 0.7 + 3 * S), fill=rgb("#6A4A30"))
    # Los discos apoyados contra la pared, a la derecha (lomos).
    for k, c in enumerate(["#5A3B26", "#2F3B2A", "#7A3A1E", "#4A3A2E", "#6B5A3A"]):
        x0 = W * 0.86 + k * 5 * S
        d.polygon([(x0, H * 0.7), (x0 + 4 * S, H * 0.7), (x0 + 18 * S, H * 0.22), (x0 + 14 * S, H * 0.22)], fill=rgb(c))
    # La maleta del tocadiscos: la tapa abierta detrás, la caja delante.
    d.polygon([(W * 0.36, H * 0.34), (W * 0.84, H * 0.34), (W * 0.88, H * 0.58), (W * 0.32, H * 0.58)], fill=rgb("#4A2F1F"))
    d.polygon([(W * 0.38, H * 0.37), (W * 0.82, H * 0.37), (W * 0.85, H * 0.56), (W * 0.35, H * 0.56)], fill=rgb("#5B3A27"))
    d.rounded_rectangle((W * 0.3, H * 0.58, W * 0.9, H * 0.76), 6 * S, fill=rgb("#6E4630"))
    d.rectangle((W * 0.3, H * 0.58, W * 0.36, H * 0.76), fill=rgb("#8C5E3E"))  # el canto que mira a la lámpara
    d.rectangle((W * 0.3, H * 0.74, W * 0.9, H * 0.76), fill=rgb("#3A2418"))
    # El plato con el disco, visto en escorzo (elipse).
    cx, cy, rx, ry = W * 0.58, H * 0.6, W * 0.22, H * 0.075
    d.ellipse((cx - rx, cy - ry, cx + rx, cy + ry), fill=rgb("#141010"))
    for k in range(1, 12):
        f = k / 12
        d.ellipse((cx - rx * f, cy - ry * f, cx + rx * f, cy + ry * f), outline=rgb("#2A2220"), width=S)
    d.arc((cx - rx, cy - ry, cx + rx, cy + ry), 160, 250, fill=rgb("#E39A58"), width=2 * S)  # el canto que brilla
    d.ellipse((cx - rx * 0.32, cy - ry * 0.32, cx + rx * 0.32, cy + ry * 0.32), fill=rgb("#C8662A"))
    d.ellipse((cx - 2 * S, cy - 1 * S, cx + 2 * S, cy + 1 * S), fill=rgb("#EEE3D0"))
    # El brazo.
    d.line((W * 0.84, H * 0.5, W * 0.8, H * 0.6, W * 0.7, H * 0.62), fill=rgb("#B8AE9C"), width=3 * S, joint="curve")
    d.ellipse((W * 0.83, H * 0.48, W * 0.86, H * 0.52), fill=rgb("#8C8270"))
    img = vineta(img, 0.35, "#000000")
    img = grano(img, 0.3, 2, color=True)
    return img


def rayos(d, cx, cy, r0, r1, n, amp, ondas, fase, colores, lleno=1.0):
    """Rayos que ondulan desde un centro: cuñas cuyos bordes son ondas de [amp] píxeles. [lleno]: qué parte del sector ocupa."""
    for k in range(n):
        if k % 2:
            continue
        a0 = 2 * math.pi * k / n
        a1 = a0 + 2 * math.pi / n * lleno
        pts = []
        pasos = 40
        for s_ in range(pasos + 1):
            r = r0 + (r1 - r0) * s_ / pasos
            da = amp * math.sin(ondas * s_ / pasos * math.pi * 2 + fase) / max(r, 1)
            pts.append((cx + r * math.cos(a0 + da), cy + r * math.sin(a0 + da)))
        for s_ in range(pasos, -1, -1):
            r = r0 + (r1 - r0) * s_ / pasos
            da = amp * math.sin(ondas * s_ / pasos * math.pi * 2 + fase) / max(r, 1)
            pts.append((cx + r * math.cos(a1 + da), cy + r * math.sin(a1 + da)))
        d.polygon(pts, fill=colores[(k // 2) % len(colores)])


def motivo_grabado(w, h, p, tintas):
    """Psicodelia · claro: el disco del día como un sol, grabado en una sola tinta azul negra sobre el papel: rayos que
    ondulan, uno de cada tres lleno de trama en lugar de tinta plena. Las otras dos tintas, con medida: un anillo de
    ultramar alrededor del disco y la etiqueta magenta."""
    W, H = w * S, h * S
    mag, ber, ult = tintas
    tinta = p["tinta"]
    img = Image.new("RGBA", (W, H), rgb(p["fondo"]))
    cx, cy = W / 2, H * 0.56
    n = 44
    r0 = 86 * S
    plena = Image.new("L", (W, H), 0)
    tramada = Image.new("L", (W, H), 0)
    for k in range(0, n, 2):
        destino = tramada if (k // 2) % 3 == 1 else plena
        sub = Image.new("L", (W, H), 0)
        dsub = ImageDraw.Draw(sub)
        a0 = 2 * math.pi * k / n
        a1 = 2 * math.pi * (k + 1) / n
        pts = []
        for s_ in range(41):
            r = r0 + (W - r0) * s_ / 40
            da = 0.09 * math.sin(2.4 * s_ / 40 * math.pi * 2) * (r0 / r) ** 0.3
            pts.append((cx + r * math.cos(a0 + da), cy + r * math.sin(a0 + da)))
        for s_ in range(40, -1, -1):
            r = r0 + (W - r0) * s_ / 40
            da = 0.09 * math.sin(2.4 * s_ / 40 * math.pi * 2) * (r0 / r) ** 0.3
            pts.append((cx + r * math.cos(a1 + da), cy + r * math.sin(a1 + da)))
        dsub.polygon(pts, fill=255)
        destino.paste(255, (0, 0), sub)
    img.paste(Image.new("RGBA", (W, H), rgb(tinta)), (0, 0), plena)
    trama = Image.new("L", (W, H), 0)
    dt = ImageDraw.Draw(trama)
    for y in range(0, H, int(3.0 * S)):
        dt.line((0, y, W, y), fill=255, width=max(1, int(1.1 * S)))
    img.paste(Image.new("RGBA", (W, H), rgb(tinta)), (0, 0), ImageChops.multiply(tramada, trama))
    dentro = Image.new("L", (W, H), 0)
    caja(ImageDraw.Draw(dentro), 14, 14, w - 14, h - 14, 54, relleno=255, forma="arco")
    papel = Image.new("RGBA", (W, H), rgb(p["fondo"]))
    img = Image.composite(img, papel, dentro)
    d = ImageDraw.Draw(img)
    # El anillo de ultramar y el disco: surcos en tinta, etiqueta magenta, agujero.
    d.ellipse((cx - 84 * S, cy - 84 * S, cx + 84 * S, cy + 84 * S), fill=rgb(p["fondo"]))
    d.ellipse((cx - 80 * S, cy - 80 * S, cx + 80 * S, cy + 80 * S), outline=rgb(ult), width=7 * S)
    d.ellipse((cx - 62 * S, cy - 62 * S, cx + 62 * S, cy + 62 * S), fill=rgb(p["fondo"]), outline=rgb(tinta), width=3 * S)
    for k in range(5):
        r = (54 - k * 6) * S
        d.ellipse((cx - r, cy - r, cx + r, cy + r), outline=rgb(tinta), width=max(1, int(0.9 * S)))
    d.ellipse((cx - 22 * S, cy - 22 * S, cx + 22 * S, cy + 22 * S), fill=rgb(mag))
    d.ellipse((cx - 3 * S, cy - 3 * S, cx + 3 * S, cy + 3 * S), fill=rgb(p["fondo"]))
    # El marco de arco del cartel, en tinta, doble, con el papel entre los dos filetes.
    caja(d, 6, 6, w - 6, h - 6, 60, linea=rgb(p["fondo"]), ancho=8, forma="arco")
    caja(d, 8, 8, w - 8, h - 8, 60, linea=rgb(tinta), ancho=2.4, forma="arco")
    caja(d, 14, 14, w - 14, h - 14, 54, linea=rgb(tinta), ancho=0.8, forma="arco")
    img = grano(img, 0.12, 3)
    return img


def bandas(d, W, H, cx, n, sep, amp, frec, fase, colores, grosor):
    """Bandas que fluyen de arriba abajo, en espejo desde el centro (líneas que se abren alrededor de la figura)."""
    for lado in (-1, 1):
        for k in range(n):
            pts = []
            for y in range(0, H + 1, 4 * S):
                t = y / H
                abre = math.sin(math.pi * t) ** 1.6  # se abren alrededor del disco, se juntan arriba y abajo
                x = cx + lado * (sep * (k + 0.6) * (0.35 + 0.9 * abre)) + lado * amp * math.sin(frec * t * math.pi * 2 + fase + k * 0.12) * abre
                pts.append((x, y))
            d.line(pts, fill=rgb(colores[k % len(colores)]), width=int(grosor), joint="curve")


def motivo_bandas(w, h, p, tintas, i):
    """Psicodelia · oscuro: tres tintas sobre negro de terciopelo. Bandas que fluyen y se abren alrededor del disco crema.
    Con intensidad: más croma, más amplitud y grosor, la tercera tinta, el registro se corre, salen los anillos y, con i = 1,
    las bandas se salen del marco."""
    W, H = w * S, h * S
    mag, ber, ult = tintas
    img = Image.new("RGBA", (W, H), rgb(p["fondo"]))
    cx, cy = W / 2, H * 0.52
    amp = (6 + 22 * i) * S
    corrido = int((0 + 4 * i) * S)
    n = 7 + int(round(3 * i))
    # Capa por tinta, como en la serigrafía: cada una con su corrimiento de registro.
    for idx, (col, dx, dy) in enumerate(((ult, 0, 0), (mag, corrido, -corrido // 2), (ber, -corrido, corrido // 2))):
        capa = Image.new("RGBA", (W, H), (0, 0, 0, 0))
        dc = ImageDraw.Draw(capa)
        sep = (12 + 2 * i) * S
        if idx == 0:
            bandas(dc, W, H, cx + dx, n, sep, amp, 1.4, 0.4, [col], (3.2 + 3.0 * i) * S)
        elif idx == 1:
            bandas(dc, W, H, cx + dx + sep / 2, n, sep, amp, 1.4, 0.4, [col], (2.2 + 2.6 * i) * S)
        elif i >= 0.45:  # desde i = 0.5, la tercera tinta: una familia de bandas en bermellón entre las otras dos
            bandas(dc, W, H, cx + dx - sep / 3, n, sep, amp, 1.4, 0.4, [col], (1.2 + 2.4 * i) * S)
        img.alpha_composite(capa)
    if i < 0.9:  # hasta i = 1 todo queda dentro del marco del cartel; con i = 1, las bandas se salen
        dentro = Image.new("L", (W, H), 0)
        caja(ImageDraw.Draw(dentro), 8, 8, w - 8, h - 8, 60, relleno=255, forma="arco")
        img = Image.composite(img, Image.new("RGBA", (W, H), rgb(p["fondo"])), dentro)
    d = ImageDraw.Draw(img)
    # Halo de anillos alternos (desde i = 0.5).
    if i >= 0.45:
        for k in range(int(3 + 4 * i), 0, -1):
            r = (54 + k * 9) * S
            col = (mag, ult, ber)[k % 3]
            d.ellipse((cx - r, cy - r, cx + r, cy + r), outline=rgb(col), width=int(3 * S))
    # El negro alrededor del disco, para que respire, y el disco crema con su etiqueta.
    d.ellipse((cx - 56 * S, cy - 56 * S, cx + 56 * S, cy + 56 * S), fill=rgb(p["fondo"]))
    d.ellipse((cx - 48 * S, cy - 48 * S, cx + 48 * S, cy + 48 * S), fill=rgb(CREMA_TINTA))
    for k in range(4):
        r = (42 - k * 5) * S
        d.ellipse((cx - r, cy - r, cx + r, cy + r), outline=rgb(mezcla(CREMA_TINTA, p["fondo"], 0.25)), width=max(1, S))
    d.ellipse((cx - 17 * S, cy - 17 * S, cx + 17 * S, cy + 17 * S), fill=rgb(mag))
    if corrido:
        d.ellipse((cx - 17 * S + corrido, cy - 17 * S, cx + 17 * S + corrido, cy + 17 * S), outline=rgb(ult), width=max(1, int(1.5 * S)))
    d.ellipse((cx - 3 * S, cy - 3 * S, cx + 3 * S, cy + 3 * S), fill=rgb(p["fondo"]))
    caja(d, 8, 8, w - 8, h - 8, 60, linea=rgb(mezcla(CREMA_TINTA, p["fondo"], 0.35)), ancho=1.6, forma="arco")
    img = grano(img, 0.1, 4)
    return img


# =====================================================================================================================
#  Una variante entera
# =====================================================================================================================

def titulo_doblado(s, clave, px, color, fondo, amp, frec):
    """El título grande en bloque, doblado por una onda (solo en la psicodelia con intensidad)."""
    f = fuente(clave, px)
    tmp = ImageDraw.Draw(Image.new("RGBA", (1, 1)))
    ancho = int(tmp.textlength(s, font=f)) + 8 * S
    alto = int(px * S * 1.5) + int(2 * amp * S)
    img = Image.new("RGBA", (ancho, alto), (0, 0, 0, 0))
    ImageDraw.Draw(img).text((4 * S, amp * S), s, font=f, fill=color)
    if amp <= 0:
        return img
    out = Image.new("RGBA", img.size, (0, 0, 0, 0))
    for x in range(ancho):
        dy = int(amp * S * math.sin(x / ancho * math.pi * 2 * frec))
        col = img.crop((x, 0, x + 1, alto))
        out.paste(col, (x, dy))
    return out


def variante(w, h, clave, nombre, cuando, i=None):
    p = dict(PALETAS[clave])
    psico = clave.startswith("psico")
    if psico and i and clave in ROLES_INTENSOS:
        p.update(ROLES_INTENSOS[clave][i])
    W, H = w * S, h * S
    img = Image.new("RGBA", (W, H), rgb(p["fondo"]))
    d = ImageDraw.Draw(img)
    forma = "arco" if psico else "redonda"
    radio = 13 if psico else 11

    # La línea de la fecha (rótulo).
    if psico:
        texto(d, (18, 22), "DISCO 290 · LADO B · PISTA 10", 600, 10.5, rgb(p["tenue"]), espacio=0.16)
    else:
        texto(d, (18, 22), "SÁBADO 17 DE OCTUBRE · LADO B, PISTA 4", 600, 10.5, rgb(p["tenue"]), espacio=0.12)

    # El motivo.
    mw, mh = w - 28, 236
    if clave == "ciudad_claro":
        m = motivo_ventanilla(mw, mh, p)
    elif clave == "ciudad_oscuro":
        m = motivo_tocadiscos(mw, mh, p)
    elif clave == "psico_claro":
        m = motivo_grabado(mw, mh, p, TINTAS["psico_claro"][0.0])
    else:
        m = motivo_bandas(mw, mh, p, TINTAS["psico_oscuro"][i or 0.0], i or 0.0)
    img.paste(m, (14 * S, 42 * S), mascara(mw, mh, radio * 1.5, forma))
    d = ImageDraw.Draw(img)

    # El título y el texto.
    y = 296
    if psico:
        amp = {None: 0, 0.5: 2.0, 1.0: 5.0}[i]
        t = titulo_doblado("LADO B", "bloque", 46, rgb(p["tinta"]), p["fondo"], amp, 1.0)
        if i:  # el registro corrido: una segunda pasada en magenta, debajo
            sombra = titulo_doblado("LADO B", "bloque", 46, rgb(p["primario"]), p["fondo"], amp, 1.0)
            img.alpha_composite(sombra, (int(16 * S + 3 * S * i), int((y - amp - 2) * S + 2 * S * i)))
        img.alpha_composite(t, (16 * S, int((y - amp - 2) * S)))
        d = ImageDraw.Draw(img)
        y += 64
        texto(d, (18, y), "La sala abre a las nueve, y el 106.9", 400, 14.5, rgb(p["tinta"]))
        texto(d, (18, y + 21), "pone discos enteros hasta las seis.", 400, 14.5, rgb(p["tinta"]))
    else:
        texto(d, (17, y), "Disco 290", 800, 40, rgb(p["tinta"]), espacio=-0.035)
        y += 56
        texto(d, (18, y), "La niebla se retira hacia el mar a", 400, 14.5, rgb(p["tinta"]))
        texto(d, (18, y + 21), "mediodía. Hoy dibujas hasta tarde.", 400, 14.5, rgb(p["tinta"]))
    y += 52

    # La tarjeta con tres fichas de cifras.
    caja(d, 14, y, w - 14, y + 104, radio * 1.5, relleno=rgb(p["superficie"]), linea=rgb(p["borde"]), ancho=1 if not psico else 1.4, forma=forma)
    rot = "LO QUE REGISTRÓ EL TELÉFONO"
    texto(d, (28, y + 18), rot, 600, 9.5, rgb(p["tenue"]), espacio=0.14)
    fichas = [("SUEÑO", "7 h 10"), ("PASOS", "8 412"), ("ÁNIMO", "5,2")]
    fw = (w - 28 - 28 - 16) / 3
    for k, (r, v) in enumerate(fichas):
        fx = 28 + k * (fw + 8)
        caja(d, fx, y + 30, fx + fw, y + 92, radio, relleno=rgb(p["variante"]), forma=forma)
        texto(d, (fx + 10, y + 46), r, 600, 9, rgb(p["tenue"]), espacio=0.14)
        col = [p["primario"], p["secundario"], p["terciario"]][k]
        texto(d, (fx + 10, y + 84), v, 800 if not psico else 600, 21 if not psico else 20, rgb(col), espacio=-0.02, ancla="ls")
    y += 120

    # Los botones: principal lleno y secundario tonal.
    bw = (w - 28 - 10) / 2
    caja(d, 14, y, 14 + bw, y + 42, radio * 1.6 if not psico else radio * 1.4, relleno=rgb(p["primario"]), forma=forma)
    texto(d, (14 + bw / 2, y + 21), "Registrar", 600, 14.5, rgb(p["sobre_primario"]), ancla="mm")
    caja(d, 24 + bw, y, w - 14, y + 42, radio * 1.6 if not psico else radio * 1.4, relleno=rgb(p["contenedor_secundario"]), forma=forma)
    texto(d, (24 + bw + bw / 2, y + 21), "Ver el día", 600, 14.5, rgb(p["secundario"] if psico else p["tinta"]), ancla="mm")
    y += 60

    # La paleta, con el contraste sobre el fondo (o sobre su color, en «sobre primario»).
    claves = [("fondo", None), ("tinta", "fondo"), ("tenue", "fondo"), ("primario", "fondo"), ("sobre_primario", "primario"),
              ("secundario", "fondo"), ("terciario", "fondo"), ("error", "fondo")]
    sw = (w - 28) / len(claves)
    for k, (n, sobre) in enumerate(claves):
        x0 = 14 + k * sw
        d.rectangle((x0 * S, y * S, (x0 + sw - 3) * S, (y + 22) * S), fill=rgb(p[n]), outline=rgb(p["borde"]), width=S)
        if sobre:
            texto(d, (x0 + sw / 2 - 1.5, y + 33), f"{contraste(p[n], p[sobre]):.1f}", 600, 9, rgb(p["tenue"]), ancla="mm")
    if psico:
        tintas = TINTAS[clave][i or 0.0]
        y2 = y + 46
        texto(d, (14, y2), "TINTAS", 600, 8.5, rgb(p["tenue"]), espacio=0.14)
        for k, c in enumerate(tintas + (p["tinta"] if clave == "psico_claro" else CREMA_TINTA,)):
            x0 = 64 + k * 30
            d.ellipse((x0 * S, (y2 - 7) * S, (x0 + 14) * S, (y2 + 7) * S), fill=rgb(c), outline=rgb(p["borde"]), width=S)
    else:
        y2 = y + 46
        texto(d, (14, y2), "GRANO DE PELÍCULA · LUZ DE LA IZQUIERDA", 600, 8.5, rgb(p["tenue"]), espacio=0.1)
    return img


# =====================================================================================================================
#  La lámina
# =====================================================================================================================

def lamina(salida):
    ancho, margen, hueco = 2400, 40, 24
    cols = 6
    w = int((ancho - 2 * margen - (cols - 1) * hueco) / cols)
    h = 690
    alto = 1040
    L = Image.new("RGBA", (ancho * S, alto * S), rgb("#D8D2C6"))
    d = ImageDraw.Draw(L)
    texto(d, (margen, 34), "Psicodelia · dos caras", 800, 34, rgb("#1E1A16"), espacio=-0.02)
    texto(d, (margen, 80), "De día, la ciudad: sobria, cálida y gastada, como una foto de los 70. De noche, la psicodelia: tres tintas planas "
          "sobre papel o sobre negro, usadas con disciplina. Un consumo de cannabis en curso sube la intensidad (i) de la cara de noche.",
          400, 15, rgb("#3A342C"))
    texto(d, (margen, 104), "Las cuatro primeras son las variantes del tema; las dos últimas, la psicodelia oscura con i = 0.5 y con i = 1. "
          "El texto, los datos, las superficies y los bordes no cambian con i: solo los colores de acento y el dibujo.",
          400, 15, rgb("#3A342C"))
    variantes = [
        ("ciudad_claro", "1 · Ciudad · claro", "De día (5:00 a 21:00) con el modo «claro», «hora» o el teléfono en claro", None),
        ("ciudad_oscuro", "2 · Ciudad · oscuro", "De día con el modo «oscuro» o el teléfono en oscuro", None),
        ("psico_claro", "3 · Psicodelia · claro", "De noche (21:00 a 5:00) con el modo «claro» o el teléfono en claro", None),
        ("psico_oscuro", "4 · Psicodelia · oscuro", "De noche con el modo «oscuro», «hora» o el teléfono en oscuro · i = 0", None),
        ("psico_oscuro", "5 · Psicodelia · oscuro · i = 0.5", "Un consumo en curso de nivel 5 (o sin nivel anotado)", 0.5),
        ("psico_oscuro", "6 · Psicodelia · oscuro · i = 1", "Un consumo en curso de nivel 10", 1.0),
    ]
    y0 = 186
    for k, (clave, nombre, cuando, i) in enumerate(variantes):
        x = margen + k * (w + hueco)
        texto(d, (x, y0 - 40), nombre, 800, 16.5, rgb("#1E1A16"), espacio=-0.01)
        texto(d, (x, y0 - 18), cuando, 400, 11, rgb("#4A433A"))
        v = variante(w, h, clave, nombre, cuando, i)
        sombra = Image.new("RGBA", (w * S + 40 * S, h * S + 40 * S), (0, 0, 0, 0))
        ImageDraw.Draw(sombra).rounded_rectangle((20 * S, 24 * S, w * S + 20 * S, h * S + 20 * S), 26 * S, fill=(40, 30, 20, 90))
        L.alpha_composite(sombra.filter(ImageFilter.GaussianBlur(10 * S)), ((x - 20) * S, (y0 - 20) * S))
        L.paste(v, (x * S, y0 * S), mascara(w, h, 24, "redonda"))
    pie = y0 + h + 34
    notas = [
        "Letras: títulos de la ciudad en Libre Franklin ExtraBold con el espaciado apretado de los titulares de los 70; texto y cifras en Libre Franklin "
        "en las dos caras (no cambian de noche: la legibilidad no depende de la hora). Títulos de la psicodelia en Chango, letra de bloque que llena su forma.",
        "Esquinas: la ciudad, redondas de 11 dp (la ventanilla del trolebús); la psicodelia, de arco de 13 dp (el marco del cartel). Con i, el título de la "
        "psicodelia se dobla y su segunda pasada se corre de registro; las tarjetas, las cifras y los botones no se mueven.",
    ]
    if LETRAS.get("aviso"):
        notas.append(LETRAS["aviso"])
    for k, n in enumerate(notas):
        texto(d, (margen, pie + k * 24), n, 400, 13.5, rgb("#3A342C"))
    L = L.resize((ancho, alto), Image.LANCZOS).convert("RGB")
    os.makedirs(os.path.dirname(salida), exist_ok=True)
    L.save(salida, optimize=True)
    print("Guardada:", salida)


def imprimir_contrastes():
    """El contraste WCAG de cada papel, el que cita la guía (y los mínimos que pide)."""
    pares = [("tinta", "fondo", 7), ("tenue", "fondo", 4.5), ("primario", "fondo", 4.5), ("secundario", "fondo", 4.5),
             ("terciario", "fondo", 4.5), ("error", "fondo", 4.5), ("sobre_primario", "primario", 4.5),
             ("sobre_contenedor", "contenedor", 4.5), ("tinta", "superficie", 7), ("tenue", "variante", 4.5),
             ("secundario", "contenedor_secundario", 4.5)]
    for clave, p in PALETAS.items():
        for i in [None] + sorted(ROLES_INTENSOS.get(clave, {})):
            q = dict(p)
            if i:
                q.update(ROLES_INTENSOS[clave][i])
            fila = []
            for a, b, minimo in pares:
                c = contraste(q[a], q[b])
                fila.append(f"{a}/{b} {c:.1f}{'' if c >= minimo else ' (¡bajo!)'}")
            print(clave + ("" if i is None else f" i={i}"), " | ".join(fila))


if __name__ == "__main__":
    ap = argparse.ArgumentParser()
    ap.add_argument("--contraste", action="store_true", help="solo imprime la tabla de contrastes")
    ap.add_argument("--cache", default=os.path.join(os.path.expanduser("~"), ".cache", "vestigium_fuentes"))
    ap.add_argument("--salida", default=SALIDA)
    a = ap.parse_args()
    if a.contraste:
        imprimir_contrastes()
        raise SystemExit
    LETRAS = letras(a.cache)
    lamina(a.salida)
