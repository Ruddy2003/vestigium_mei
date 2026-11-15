"""
Los sellos del día (huella_<id>.webp, WebP sin pérdida, + huella_fondo_<id>.jpg, 720 × 720): el fondo de cada tema y, encima, la huella
maestra (maestra.py) en el material del tema. Lote 1 del plan de diseño: una sola huella en toda la app.

- Siete temas se dibujan enteros aquí: botánico, gótico, espacio, Al-Ándalus, mongol, Renacimiento y Tierras Antiguas
  (antes en arte.py).
- Los otros catorce venían de PNG hechos a mano con otra huella. De ellos se sacó una sola vez (`extraer`) lo que no
  era la huella, y se guardó como fuente:
  - fuentes/fondo_sello_<id>.jpg: el fondo;
  - fuentes/material_<id>.jpg: el color de las crestas (oro, tinta, fósforo, tricolor…), muestreado y extendido a
    todo el lienzo;
  - fuentes/ornamento_<id>.png: lo que va con la huella y no es huella (el lacre de Napoleón, las esquinas del lector
    del laboratorio).
  Lo que va sobre las crestas (la barra y los puntos del laboratorio, las minucias de Ciber, los destellos de Navidad)
  se dibuja aquí sobre la huella nueva, porque recortado de la vieja quedaba fuera de sus crestas.
  Aquí se vuelven a componer con la huella maestra y, en Colonos, Distopía, Navidad y Refugio, con un fondo más rico.

Uso (desde herramientas/):
  python3 sellos.py ../app/src/main/res/drawable-nodpi                    los 21 sellos
  python3 sellos.py ../app/src/main/res/drawable-nodpi romano,napoleon    solo esos
  python3 sellos.py ../app/src/main/res/drawable-nodpi --hoja hoja.png    y una hoja de contacto
  python3 sellos.py extraer ../app/src/main/res/drawable-nodpi            ya se hizo (necesita los PNG viejos)
"""
import math, os, shutil, sys
import numpy as np
from PIL import Image, ImageDraw, ImageFilter
from arte import ruido, ruido_rect, col, mezcla, a_img, a_rgba, relieve
import maestra
from iconos import cinta, ouroboros, nube, tallado, suavizar, oro_relieve

AQUI = os.path.dirname(os.path.abspath(__file__))
FUENTES = os.path.join(AQUI, 'fuentes')
OUT = None
TAM = 720


def mascara_sello(tam=TAM):
    """La huella maestra con la escala y la posición que tenía la huella vieja: 575 px de alto, centrada en (362, 376)."""
    return maestra.mascara(tam, 575 * tam / 720, (362 * tam / 720, 376 * tam / 720))


def guardar(img_fondo, img_huella, tema):
    """[img_fondo] puede ser una imagen o la ruta de un JPG que se copia tal cual (sin volver a comprimirlo)."""
    if isinstance(img_fondo, str): shutil.copyfile(img_fondo, os.path.join(OUT, f'huella_fondo_{tema}.jpg'))
    else: img_fondo.convert('RGB').save(os.path.join(OUT, f'huella_fondo_{tema}.jpg'), quality=88)
    img_huella.quantize(256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE).save(os.path.join(OUT, f'huella_{tema}.webp'), 'WEBP', lossless=True, quality=100, method=6, exact=True)


def desenfocar(a, s):
    """Desenfoque gaussiano de un campo en coma flotante (2D o con canales), con bordes reflejados."""
    p = min(int(3 * s) + 1, 700)
    anchos = ((p, p), (p, p)) + (((0, 0),) if a.ndim == 3 else ())
    b = np.pad(a, anchos, mode='reflect')
    fy = np.fft.fftfreq(b.shape[0])[:, None]; fx = np.fft.fftfreq(b.shape[1])[None, :]
    g = np.exp(-2 * (math.pi * s) ** 2 * (fx ** 2 + fy ** 2))
    f = (lambda x: np.real(np.fft.ifft2(np.fft.fft2(x) * g)))
    r = f(b) if a.ndim == 2 else np.stack([f(b[..., k]) for k in range(b.shape[2])], -1)
    return r[p:p + a.shape[0], p:p + a.shape[1]].astype(np.float32)


def extender(rgb, w):
    """Lleva el color de los píxeles con peso [w] a todo el lienzo (convolución normalizada, de lo fino a lo grueso)."""
    out = np.zeros_like(rgb); falta = np.ones(w.shape, bool)
    for s in (2, 5, 12, 30, 80, 200):
        num = desenfocar(rgb * w[..., None], s); den = desenfocar(w, s)
        ok = falta & (den > 0.02)
        out[ok] = num[ok] / den[ok][:, None]; falta &= ~ok
    if falta.any(): out[falta] = (rgb * w[..., None]).sum((0, 1)) / max(w.sum(), 1)
    return out


def luminancia(rgb): return rgb[..., 0] * 0.299 + rgb[..., 1] * 0.587 + rgb[..., 2] * 0.114

# ------------------------------------------------------------------ botánico
def hoja(d, cx, cy, largo, ang, color, ancho=0.34, nervio=None):
    pts = []
    for i in range(41):
        t = i / 40
        x = t * largo; y = math.sin(math.pi * t) * largo * ancho / 2
        pts.append((x, y))
    pts += [(x, -y) for (x, y) in reversed(pts)]
    c, s = math.cos(ang), math.sin(ang)
    p = [(cx + x * c - y * s, cy + x * s + y * c) for (x, y) in pts]
    d.polygon(p, fill=color)
    if nervio:
        d.line([(cx, cy), (cx + largo * c, cy + largo * s)], fill=nervio, width=max(1, int(largo / 40)))
        for k in range(1, 6):
            t = k / 6.5; bx, by = cx + largo * t * c, cy + largo * t * s
            for lado in (-1, 1):
                a2 = ang + lado * 0.7
                d.line([(bx, by), (bx + largo * 0.16 * math.cos(a2), by + largo * 0.16 * math.sin(a2))], fill=nervio, width=max(1, int(largo / 60)))

def escarabajo(tam_px, cuerpo=('#1E4A3C', '#5E8F4A', '#B98A3A'), tinta=(32, 26, 18, 255)):
    """Un escarabajo de lámina visto desde arriba, en su propia capa (cabeza hacia arriba). Élitros con brillo metálico."""
    W = int(tam_px * 2.2); H = int(tam_px * 2.4)
    capa = Image.new('RGBA', (W, H), (0, 0, 0, 0)); d = ImageDraw.Draw(capa, 'RGBA')
    cx, cy = W / 2, H * 0.56; L = tam_px; A = tam_px * 0.66
    lw = max(1, int(tam_px / 26))
    # Patas: tres pares, con codo.
    for k, (y0, ang1, ang2) in enumerate([(-0.3, -0.7, -1.4), (-0.02, 0.05, 0.35), (0.18, 0.6, 1.2)]):
        for lado in (-1, 1):
            p0 = (cx + lado * A * 0.3, cy + L * y0)
            p1 = (p0[0] + lado * L * 0.26 * math.cos(ang1), p0[1] + L * 0.26 * math.sin(ang1))
            p2 = (p1[0] + lado * L * 0.22 * math.cos(ang2), p1[1] + L * 0.22 * math.sin(ang2))
            d.line([p0, p1, p2], fill=tinta, width=lw + 2, joint='curve')
            d.line([p2, (p2[0] + lado * L * 0.05, p2[1] + L * 0.04)], fill=tinta, width=lw)
    # Antenas segmentadas.
    for lado in (-1, 1):
        pts = [(cx + lado * A * 0.12, cy - L * 0.62)]
        for i in range(1, 6):
            pts.append((pts[0][0] + lado * L * 0.055 * i, pts[0][1] - L * 0.06 * i + (i ** 1.6) * L * 0.004))
        d.line(pts, fill=tinta, width=lw, joint='curve')
        for x, y in pts[2::2]: d.ellipse([x - lw * 1.3, y - lw * 1.3, x + lw * 1.3, y + lw * 1.3], fill=tinta)
    # Cabeza y pronoto.
    d.ellipse([cx - A * 0.2, cy - L * 0.7, cx + A * 0.2, cy - L * 0.48], fill=tinta)
    d.ellipse([cx - A * 0.42, cy - L * 0.56, cx + A * 0.42, cy - L * 0.2], fill=col_a(cuerpo[0], 255))
    # Élitros con degradado metálico: anillos concéntricos del oscuro al claro y un brillo.
    c0, c1, c2 = (col(x) for x in cuerpo)
    n = 24
    for i in range(n):
        t = i / (n - 1)
        cc = c0 * (1 - t) + c1 * t if t < 0.7 else c1 * (1 - (t - 0.7) / 0.3) + c2 * ((t - 0.7) / 0.3)
        k = 1 - t * 0.82
        d.ellipse([cx - A * 0.5 * k - A * 0.06 * t, cy - L * 0.24 * k - L * 0.18 * t, cx + A * 0.5 * k - A * 0.06 * t, cy + L * 0.52 * k - L * 0.05 * t], fill=tuple(int(v) for v in cc) + (255,))
    d.ellipse([cx - A * 0.5, cy - L * 0.24, cx + A * 0.5, cy + L * 0.52], outline=tinta, width=lw)
    d.line([(cx, cy - L * 0.22), (cx, cy + L * 0.5)], fill=tinta, width=lw)
    # Estrías finas en los élitros.
    for lado in (-1, 1):
        for j in (0.18, 0.32):
            d.line([(cx + lado * A * j, cy - L * 0.16), (cx + lado * A * (j + 0.02), cy + L * 0.42)], fill=tinta[:3] + (90,), width=max(1, lw // 2))
    d.ellipse([cx - A * 0.42, cy - L * 0.56, cx + A * 0.42, cy - L * 0.2], outline=tinta, width=lw)
    return capa

def col_a(h, a): return tuple(int(v) for v in col(h)) + (a,)

def hormiga(d, cx, cy, l, ang, color=(40, 26, 18, 235)):
    """Hormiga de perfil desde arriba: gáster, tórax y cabeza, seis patas y antenas; [ang] hacia donde camina."""
    c, s_ = math.cos(ang), math.sin(ang)
    P = lambda x, y: (cx + x * c - y * s_, cy + x * s_ + y * c)
    def ov(x, y, rx, ry):
        pts = [P(x + rx * math.cos(t), y + ry * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 18)]
        d.polygon(pts, fill=color)
    ov(-l * 0.32, 0, l * 0.24, l * 0.17); ov(0, 0, l * 0.14, l * 0.08); ov(l * 0.24, 0, l * 0.12, l * 0.1)
    w = max(1, int(l / 14))
    for x, a1 in ((-0.04, 1.0), (0.02, 0.0), (0.08, -0.9)):
        for lado in (-1, 1):
            p0 = P(l * x, 0); p1 = P(l * x + l * 0.16 * math.sin(a1 * 0.6) * -1, lado * l * 0.22); p2 = P(l * x - l * 0.12 * a1, lado * l * 0.36)
            d.line([p0, p1, p2], fill=color, width=w)
    for lado in (-1, 1):
        d.line([P(l * 0.32, 0), P(l * 0.44, lado * l * 0.08), P(l * 0.56, lado * l * 0.18)], fill=color, width=w)

def helecho(d, x0, y0, largo, ang, color, w):
    """Una fronda de helecho a lápiz: raquis curvo y pinnas que se achican hacia la punta."""
    pts = []
    for i in range(41):
        t = i / 40; a = ang + 0.5 * t
        pts.append((x0 + largo * t * math.cos(a), y0 + largo * t * math.sin(a)))
    d.line(pts, fill=color, width=w)
    for i in range(3, 38, 2):
        t = i / 40; x, y = pts[i]; a = ang + 0.5 * t; l = largo * 0.16 * (1 - t) + largo * 0.02
        for lado in (-1, 1):
            b = a + lado * 1.1
            q = [(x, y), (x + l * 0.5 * math.cos(b) + l * 0.12 * math.cos(a), y + l * 0.5 * math.sin(b) + l * 0.12 * math.sin(a)), (x + l * math.cos(b - lado * 0.25), y + l * math.sin(b - lado * 0.25))]
            d.line(q, fill=color, width=max(1, w - 1))

def botanico(tam):
    """Cuaderno de naturalista: papel viejo con cuadrícula, un helecho a lápiz, hormigas y un escarabajo de lámina sobre la huella en tinta sepia."""
    m = mascara_sello(tam)
    papel = ruido(tam, tam / 40, 1)
    base = mezcla(col('#EEE3C6'), col('#D9C79E'), papel * 0.75)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    base = mezcla(base, col('#B79B68'), np.clip(dist - 0.75, 0, 1) * 1.2)
    manchas = np.clip((ruido(tam, tam / 30, 2) - 0.74) * 4, 0, 1)
    base = mezcla(base, col('#C6A774'), manchas * 0.45)
    base = base * (0.95 + 0.07 * ruido(tam, 0.7, 3)[..., None])
    fondo = a_img(base)   # RGB: así lo que se dibuja con transparencia se mezcla con el papel
    d = ImageDraw.Draw(fondo, 'RGBA')
    # Cuadrícula muy tenue de libreta de campo.
    paso = tam / 16
    for i in range(1, 16):
        d.line([(i * paso, 0), (i * paso, tam)], fill=(110, 95, 70, 30), width=1)
        d.line([(0, i * paso), (tam, i * paso)], fill=(110, 95, 70, 30), width=1)
    # Helechos a lápiz en dos esquinas y una fila de hormigas que cruza abajo.
    helecho(d, tam * 0.02, tam * 0.98, tam * 0.42, -1.25, (70, 60, 40, 110), max(1, tam // 220))
    helecho(d, tam * 0.98, tam * 0.04, tam * 0.36, 2.0, (70, 60, 40, 90), max(1, tam // 260))
    r = np.random.default_rng(5)
    for i in range(7):
        t = i / 6
        x = tam * (0.08 + 0.84 * t); y = tam * (0.9 + 0.035 * math.sin(t * 6))
        hormiga(d, x, y, tam * 0.03, -0.08 + 0.3 * math.cos(t * 6), (46, 30, 20, 150))
    fondo = fondo.convert('RGB')
    # Huella en tinta ferrogálica: sepia oscuro con grano, más clara donde la pluma se quedó sin tinta.
    grano = ruido(tam, 0.7, 4)
    carga = ruido(tam, tam / 12, 5)
    tinta = mezcla(col('#2B2216'), col('#5A4630'), carga * 0.8)
    alfa = m * np.clip(0.7 + 0.25 * grano + 0.1 * carga, 0, 1)
    sh = relieve(m, fuerza=0.5)[..., None]
    capa = a_rgba(tinta * (1 + sh * 0.2), alfa)
    # Un escarabajo de lámina posado sobre la huella, con su alfiler, y tres hormigas que suben por una cresta.
    k = 1.15
    esc = escarabajo(int(tam * 0.2 * k)).rotate(-28, resample=Image.BICUBIC, expand=True)
    sombra = Image.new('RGBA', esc.size, (0, 0, 0, 0)); sombra.putalpha(esc.getchannel('A').point(lambda v: int(v * 0.35)))
    sombra = sombra.filter(ImageFilter.GaussianBlur(tam / 140))
    px, py = int(tam * 0.6 - esc.width / 2), int(tam * 0.6 - esc.height / 2)
    capa.alpha_composite(sombra, (px + int(tam * 0.012), py + int(tam * 0.016)))
    capa.alpha_composite(esc, (px, py))
    d2 = ImageDraw.Draw(capa, 'RGBA')
    ax, ay = tam * 0.6 + tam * 0.012, tam * 0.6 - tam * 0.01
    d2.ellipse([ax - tam * 0.012, ay - tam * 0.012, ax + tam * 0.012, ay + tam * 0.012], fill=(150, 30, 30, 255), outline=(60, 20, 15, 255))
    for i in range(3):
        hormiga(d2, tam * (0.3 + 0.085 * i), tam * (0.37 - 0.04 * i), tam * 0.05, -0.5, (128, 46, 22, 250))
    guardar(fondo, capa, 'botanico')

# ------------------------------------------------------------------ Ciudad verde
def hoja_hiedra(d, cx, cy, s, ang, color, nervio=None):
    """Una hoja de hiedra de cinco puntas, centrada en (cx, cy), de tamaño [s], con la punta hacia el ángulo [ang]."""
    pts = [(0, 0.5), (-0.55, 0.35), (-0.8, -0.05), (-0.32, -0.05), (0, -0.5), (0.32, -0.05), (0.8, -0.05), (0.55, 0.35)]
    ca, sa = math.cos(ang + math.pi / 2), math.sin(ang + math.pi / 2)
    rot = lambda px, py: (cx + (px * ca - py * sa) * s, cy + (px * sa + py * ca) * s)
    d.polygon([rot(px, py) for px, py in pts], fill=color)
    if nervio: d.line([rot(0, 0.5), rot(0, -0.35)], fill=nervio, width=max(1, int(s / 22)))

def grieta(d, x, y, ang, largo, color, w, rng, rama=True):
    """Una grieta: un camino quebrado que a veces se bifurca."""
    pts = [(x, y)]; paso = largo / 10
    for k in range(10):
        ang += (rng.random() - 0.5) * 0.9
        x += paso * math.cos(ang); y += paso * math.sin(ang); pts.append((x, y))
        if rama and k == 4: grieta(d, x, y, ang + 0.8 * (1 if rng.random() < 0.5 else -1), largo * 0.45, color, max(1, w - 1), rng, False)
    d.line(pts, fill=color, width=w)

def ruinas(tam):
    """Ciudad verde: losa de hormigón agrietada con musgo, la línea gastada de una calle y hiedra que entra por una esquina;
    la huella es musgo que creció en sus surcos, y una guía de hiedra la cruza."""
    m = mascara_sello(tam)
    base = mezcla(col('#D9D8D0'), col('#B7B6AC'), ruido(tam, tam / 50, 31) * 0.8)
    base = base * (0.93 + 0.1 * ruido(tam, 0.8, 32)[..., None])
    musgo = np.clip((ruido(tam, tam / 14, 34) - 0.6) * 3, 0, 1)
    base = mezcla(base, col('#7F8F5A'), musgo * 0.55)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    base = mezcla(base, col('#8C8C82'), np.clip(dist - 0.78, 0, 1) * 1.3)
    fondo = a_img(base)
    d = ImageDraw.Draw(fondo, 'RGBA')
    rng = np.random.default_rng(41)
    # La junta entre dos losas y la línea pintada de la calle, gastada a tramos.
    d.line([(0, tam * 0.64), (tam, tam * 0.6)], fill=(80, 80, 74, 110), width=max(2, tam // 200))
    for i in range(6):
        x0 = tam * (0.0 + i * 0.18); x1 = x0 + tam * 0.11
        y0 = tam * 0.84 - x0 * 0.05; y1 = tam * 0.84 - x1 * 0.05; g = tam * 0.022
        d.polygon([(x0, y0 - g / 2), (x1, y1 - g / 2), (x1, y1 + g / 2), (x0, y0 + g / 2)], fill=(214, 170, 60, 120 if i % 3 else 70))
    # Grietas.
    for (x, y, a, l) in [(0.0, 0.22, 0.3, 0.55), (1.0, 0.38, 2.9, 0.5), (0.58, 0.0, 1.7, 0.4), (0.3, 1.0, -1.3, 0.35)]:
        grieta(d, tam * x, tam * y, a, tam * l, (60, 62, 56, 150), max(1, tam // 260), rng)
    # Hiedra que entra por la esquina de abajo a la izquierda.
    tallo = [(tam * (0.02 + 0.3 * t + 0.04 * math.sin(t * 9)), tam * (0.99 - 0.48 * t)) for t in [i / 30 for i in range(31)]]
    d.line(tallo, fill=(58, 80, 40, 210), width=max(2, tam // 240))
    for i in range(2, 31, 3):
        x, y = tallo[i]; lado = 1 if (i // 3) % 2 else -1
        hoja_hiedra(d, x + lado * tam * 0.025, y, tam * (0.06 - 0.03 * i / 30), -math.pi / 2 + lado * 0.9, (79, 122, 51, 225), (40, 60, 28, 200))
    fondo = fondo.convert('RGB')
    # La huella: musgo en los surcos, más claro donde le da la luz y ralo en algunos tramos.
    grano = ruido(tam, 0.7, 35)
    carga = ruido(tam, tam / 14, 36)
    tinta = mezcla(col('#2F4A22'), col('#6E8F3E'), carga)
    alfa = m * np.clip(0.72 + 0.28 * grano, 0, 1) * np.clip(0.7 + carga * 0.45, 0, 1)
    sh = relieve(m, fuerza=0.5)[..., None]
    capa = a_rgba(tinta * (1 + sh * 0.25), alfa)
    d2 = ImageDraw.Draw(capa, 'RGBA')
    # Una guía de hiedra que cruza la huella en diagonal, con sus hojas.
    guia = [(tam * (0.22 + 0.55 * t), tam * (0.78 - 0.5 * t + 0.05 * math.sin(t * 8))) for t in [i / 40 for i in range(41)]]
    d2.line(guia, fill=(46, 66, 30, 255), width=max(2, tam // 200))
    for i in range(3, 41, 6):
        x, y = guia[i]; lado = 1 if (i // 6) % 2 else -1
        hoja_hiedra(d2, x + lado * tam * 0.028, y + lado * tam * 0.018, tam * 0.058, -math.pi / 2 + lado * 1.0, (98, 142, 62, 255), (40, 60, 28, 255))
    guardar(fondo, capa, 'ruinas')

# ------------------------------------------------------------------ gótico (vitral)
def gotico(tam):
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    # Fondo: bóveda azul con estrellas de oro (sin arco: en el ícono redondo quedaba cortado).
    g = ruido(tam, tam / 10, 11)
    base = mezcla(col('#0B1440'), col('#1F2F7A'), g * 0.8)
    fondo = a_img(base).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    r = np.random.default_rng(12)
    for _ in range(int(tam / 9)):
        x, y = r.random() * tam, r.random() * tam; s = tam * (0.006 + r.random() * 0.01)
        pts = []
        for i in range(16):
            rr = s if i % 2 == 0 else s * 0.4
            a = -math.pi / 2 + i * math.pi / 8
            pts.append((x + rr * math.cos(a), y + rr * math.sin(a)))
        d.polygon(pts, fill=(226, 179, 74, 200))
    fondo = fondo.convert('RGB')
    # Huella de vitral: celdas de Voronoi de colores, plomo oscuro entre celdas, luz que atraviesa.
    n = int(tam / 4)
    sx = r.random(n) * tam; sy = r.random(n) * tam
    paleta = np.array([col('#E0243F'), col('#2F5BE0'), col('#32B06A'), col('#F2C14E'), col('#A050C8'), col('#3FA0FF'), col('#FF7A3A')])
    cid = np.full((tam, tam), 0, np.int32); dmin = np.full((tam, tam), 1e9, np.float32); d2nd = np.full((tam, tam), 1e9, np.float32)
    for i in range(n):
        dd = (xx - sx[i]) ** 2 + (yy - sy[i]) ** 2
        menor = dd < dmin
        d2nd = np.where(menor, dmin, np.minimum(d2nd, dd))
        cid = np.where(menor, i, cid); dmin = np.where(menor, dd, dmin)
    borde = (np.sqrt(d2nd) - np.sqrt(dmin)) < max(1.2, tam * 0.004)
    colores = paleta[cid % len(paleta)]
    luz = ruido(tam, tam / 25, 13)[..., None]
    rgb = colores * (0.85 + 0.45 * luz)
    rgb = np.where(borde[..., None], col('#14121C'), rgb)
    # Contorno de plomo en los bordes de las crestas.
    contorno = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.FIND_EDGES)).astype(np.float32) / 255
    rgb = mezcla(rgb, col('#14121C'), np.clip(contorno * 0.9, 0, 1))
    capa = a_rgba(rgb, m)
    # Resplandor alrededor.
    glow = Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 40))
    g2 = np.array(glow).astype(np.float32) / 255 * 0.45
    halo = a_rgba(np.zeros((tam, tam, 3)) + col('#E2B34A'), g2)
    halo.alpha_composite(capa)
    guardar(fondo, halo, 'gotico')

# ------------------------------------------------------------------ horror cósmico
def arcano(tam):
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    # Fondo: la hoja del archivo, amarillenta, con manchas de humedad, un recorte de periódico pegado detrás con sus
    # columnas, el cerco de una taza y, al pie, la costa y el faro dibujados a pluma.
    g = ruido(tam, tam / 9, 31)
    base = mezcla(col('#D6CAA8'), col('#E8DFC6'), g)
    manchas = ruido(tam, tam / 14, 32)
    base = mezcla(base, col('#B8A67A'), np.clip((manchas - 0.62) * 2.2, 0, 0.45))
    fondo = a_img(base).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    recorte = Image.new('RGBA', (int(tam * 0.5), int(tam * 0.34)), (222, 214, 190, 255))
    dr = ImageDraw.Draw(recorte)
    w0, h0 = recorte.size
    dr.rectangle([w0 * 0.06, h0 * 0.08, w0 * 0.94, h0 * 0.2], fill=(70, 70, 64, 150))
    for colx in (0.06, 0.52):
        y = h0 * 0.28
        while y < h0 * 0.92:
            dr.line([(w0 * colx, y), (w0 * (colx + 0.42), y)], fill=(90, 90, 82, 120), width=max(1, int(tam / 360)))
            y += h0 * 0.055
    recorte = recorte.rotate(7, expand=True, resample=Image.BICUBIC)
    fondo.alpha_composite(recorte, (int(tam * 0.46), int(tam * 0.04)))
    for (x, y) in ((tam * 0.48, tam * 0.07), (tam * 0.9, tam * 0.13)):
        d.rectangle([x - tam * 0.05, y - tam * 0.015, x + tam * 0.05, y + tam * 0.015], fill=(236, 226, 170, 110))
    cx, cy, rr = tam * 0.24, tam * 0.78, tam * 0.13
    d.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], outline=(120, 96, 52, 70), width=max(2, int(tam / 120)))
    tinta = (30, 51, 41)
    costa = [(0, tam * 0.95)]
    for k in range(1, 21):
        x = tam * k / 20; costa.append((x, tam * (0.95 - 0.06 * math.sin(k * 0.7) - (0.08 if k > 15 else 0))))
    d.line(costa, fill=tinta + (110,), width=max(1, int(tam / 300)))
    fx, fy = tam * 0.88, tam * 0.8
    d.polygon([(fx - tam * 0.015, fy + tam * 0.07), (fx - tam * 0.009, fy), (fx + tam * 0.009, fy), (fx + tam * 0.015, fy + tam * 0.07)], fill=tinta + (130,))
    d.polygon([(fx, fy - tam * 0.005), (fx - tam * 0.2, fy - tam * 0.045), (fx - tam * 0.2, fy + tam * 0.02)], fill=(236, 214, 140, 70))
    fondo = fondo.convert('RGB')
    # La huella: un sello de goma entintado de verde-negro que no apretó parejo: la tinta se corta en unas crestas y se
    # carga en otras, con dos o tres gotas que saltaron.
    presion = ruido(tam, tam / 30, 33)
    alfa = m * np.clip(0.45 + presion * 0.75, 0, 1) * (ruido(tam, 1.2, 34) > 0.18)
    rgb = np.zeros((tam, tam, 3), np.float32) + col('#1E3329')
    rgb = mezcla(rgb, col('#2F5446'), np.clip(1 - presion, 0, 1) * 0.5)
    capa = a_rgba(rgb, alfa)
    dc = ImageDraw.Draw(capa, 'RGBA')
    r = np.random.default_rng(35)
    for _ in range(5):
        a = r.random() * math.tau; dist = tam * (0.36 + r.random() * 0.06); s = tam * (0.006 + r.random() * 0.012)
        x, y = tam / 2 + dist * math.cos(a), tam * 0.52 + dist * math.sin(a)
        dc.ellipse([x - s, y - s, x + s, y + s], fill=(30, 51, 41, 200))
    guardar(fondo, capa, 'arcano')

# ------------------------------------------------------------------ espacio
def espacio(tam):
    m = mascara_sello(tam)
    n1 = ruido(tam, tam / 8, 21); n2 = ruido(tam, tam / 16, 22); n3 = ruido(tam, tam / 30, 23)
    base = np.zeros((tam, tam, 3)) + col('#03050C')
    neb = np.clip((n1 * 0.6 + n2 * 0.4 - 0.45) * 2.2, 0, 1)
    base = mezcla(base, col('#3B1E6E'), neb * 0.8)
    base = mezcla(base, col('#1B4F9E'), np.clip((n2 - 0.55) * 3, 0, 1) * 0.6)
    base = mezcla(base, col('#C65A3A'), np.clip((n3 - 0.7) * 3, 0, 1) * 0.35)
    fondo = a_img(base).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    r = np.random.default_rng(24)
    for _ in range(int(tam * 1.1)):
        x, y = r.random() * tam, r.random() * tam; g = r.random()
        s = 0.5 if g < 0.85 else (1.2 if g < 0.98 else 2.4)
        a = int(120 + 135 * r.random())
        d.ellipse([x - s, y - s, x + s, y + s], fill=(255, 255, 255, a))
        if s > 2:
            d.line([(x - s * 3, y), (x + s * 3, y)], fill=(200, 220, 255, 120), width=1)
            d.line([(x, y - s * 3), (x, y + s * 3)], fill=(200, 220, 255, 120), width=1)
    fondo = fondo.convert('RGB')
    # Huella de luz de estrellas: núcleo blanco azulado, resplandor y polvo de estrellas.
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    tono = mezcla(col('#E8F0FF'), col('#8AB4FF'), np.clip(dist * 1.4, 0, 1))
    tono = mezcla(tono, col('#C59BFF'), np.clip((ruido(tam, tam / 20, 25) - 0.5) * 2, 0, 1) * 0.6)
    glow = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 70))).astype(np.float32) / 255
    alfa = np.clip(m * 0.95 + glow * 0.7, 0, 1)
    capa = a_rgba(tono, alfa)
    d2 = ImageDraw.Draw(capa, 'RGBA')
    ys, xs = np.nonzero(m > 0.7)
    for _ in range(int(tam / 6)):
        i = r.integers(len(xs)); s = 0.8 + r.random() * 1.6
        d2.ellipse([xs[i] - s, ys[i] - s, xs[i] + s, ys[i] + s], fill=(255, 255, 255, 255))
    guardar(fondo, capa, 'espacio')

# ------------------------------------------------------------------ Art déco
def deco(tam):
    """Un panel de laca negra de los años veinte: un sol de rayos que nace del pie, abanicos de oro en escamas y
    filetes escalonados en las esquinas; encima, la huella incrustada en pan de oro, con su brillo de metal."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    laca = mezcla(col('#0E0C0A'), col('#1E1A14'), ruido(tam, tam / 18, 71) * 0.8)
    # El brillo de la laca: una franja diagonal muy suave.
    diag = np.exp(-(((xx - yy * 0.7) - tam * 0.35) / (tam * 0.22)) ** 2)
    laca = laca + diag[..., None] * 10
    fondo = a_img(laca)
    d = ImageDraw.Draw(fondo, 'RGBA')
    oro = (212, 175, 55)
    # Escamas de abanico en filas alternas, muy tenues.
    r = tam / 9
    fila = 0; y = r * 0.6
    while y < tam + r:
        x = (r if fila % 2 else 0)
        while x < tam + r:
            for k, a in ((1.0, 34), (0.62, 26), (0.3, 20)):
                d.arc([x - r * k, y - r * k, x + r * k, y + r * k], 180, 360, fill=oro + (a,), width=max(1, int(tam / 360)))
            x += 2 * r
        y += r * 0.78; fila += 1
    # El sol de rayos que nace del pie, alternando rayos anchos y finos.
    c = (tam / 2, tam * 1.02)
    for i in range(25):
        ang = math.pi + (i + 0.5) * math.pi / 25
        a0, a1 = ang - (0.022 if i % 2 == 0 else 0.008), ang + (0.022 if i % 2 == 0 else 0.008)
        largo = tam * 1.2
        d.polygon([c, (c[0] + largo * math.cos(a0), c[1] + largo * math.sin(a0)), (c[0] + largo * math.cos(a1), c[1] + largo * math.sin(a1))],
                  fill=oro + (46 if i % 2 == 0 else 30,))
    # Tres escalones de filete en cada esquina.
    g = max(2, int(tam / 180)); paso = tam / 22; m0 = tam / 28
    for (ox, oy, sx, sy) in ((m0, m0, 1, 1), (tam - m0, m0, -1, 1), (m0, tam - m0, 1, -1), (tam - m0, tam - m0, -1, -1)):
        pts = [(ox, oy + sy * 4 * paso)]
        for k in range(1, 4):
            pts += [(ox + sx * k * paso, oy + sy * (5 - k) * paso), (ox + sx * k * paso, oy + sy * (4 - k) * paso)]
        pts += [(ox + sx * 4 * paso, oy + sy * paso), (ox + sx * 4 * paso, oy)]
        d.line(pts, fill=oro + (150,), width=g)
    # La huella en pan de oro: gradiente de metal (oscuro, claro, oscuro) cruzado, con relieve y algún hueco de la hoja.
    t = (np.sin((xx + yy) / tam * math.pi * 3.2) + 1) / 2
    metal = mezcla(col('#8A6A1E'), col('#F3D98A'), t ** 1.6)
    metal = mezcla(metal, col('#C9A23C'), ruido(tam, tam / 60, 72) * 0.4)
    luz = relieve(m, (-1, -1), 1.2)
    metal = np.clip(metal + luz[..., None] * 60, 0, 255)
    hojas = ruido(tam, tam / 90, 73)
    alfa = m * np.clip(0.96 - np.clip(hojas - 0.82, 0, 1) * 3, 0, 1)
    capa = a_rgba(metal, alfa)
    guardar(fondo, capa, 'deco')

# ------------------------------------------------------------------ Ciudad neón
def neon(tam):
    m = mascara_sello(tam)
    # Fondo: asfalto mojado violeta, con los reflejos borrosos de tres letreros y la llovizna en diagonal.
    base = np.zeros((tam, tam, 3)) + col('#0E0812')
    base = mezcla(base, col('#2A1630'), np.clip((ruido(tam, tam / 10, 61) - 0.4) * 1.6, 0, 1) * 0.7)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    for (cx, cy, rr, c, k) in ((0.18, 0.2, 0.22, '#FF3D9A', 0.55), (0.85, 0.78, 0.2, '#FFB238', 0.45), (0.78, 0.15, 0.12, '#8CFF5A', 0.3)):
        dist = np.sqrt((xx - cx * tam) ** 2 + ((yy - cy * tam) * 0.6) ** 2) / (rr * tam)
        base = mezcla(base, col(c), np.clip(1 - dist, 0, 1) ** 2 * k)
    fondo = a_img(base).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    r = np.random.default_rng(62)
    for _ in range(int(tam * 0.45)):
        x, y = r.random() * tam, r.random() * tam; l = tam * (0.02 + r.random() * 0.04)
        d.line([(x, y), (x - l * 0.25, y + l)], fill=(190, 200, 230, int(10 + 22 * r.random())), width=1)
    fondo = fondo.convert('RGB')
    # Huella de tubo de neón: núcleo casi blanco, cuerpo magenta y el halo que se abre alrededor.
    tono = mezcla(col('#FF3D9A'), col('#FFE3F1'), np.clip((m - 0.75) * 4, 0, 1) * 0.6)
    glow = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 28))).astype(np.float32) / 255
    alfa = np.clip(m * 0.95 + glow * 0.6, 0, 1)
    guardar(fondo, a_rgba(tono, alfa), 'neon')
# ------------------------------------------------------------------ abismo
def abismo(tam):
    """El agua que se oscurece hacia abajo, con los haces de la superficie arriba, nieve marina, burbujas y medusas
    lejanas; la huella brilla con luz propia (verde agua, con un velo violeta), como un animal del fondo."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    prof = yy / tam
    base = mezcla(np.zeros((tam, tam, 3)) + col('#0E5A78'), col('#02090F'), np.clip(prof * 1.25, 0, 1))
    # Haces de luz que bajan en diagonal desde la superficie y se apagan con la profundidad.
    haces = np.clip(np.sin((xx + yy * 0.35) / tam * 18) * np.sin((xx - yy * 0.2) / tam * 7 + 1.3), 0, 1) ** 3
    base = mezcla(base, col('#9FE3F0'), haces * np.clip(0.55 - prof, 0, 1) * 0.55)
    n1 = ruido(tam, tam / 12, 61)
    base = mezcla(base, col('#123A55'), np.clip((n1 - 0.5) * 2, 0, 1) * 0.35)
    fondo = a_img(base).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    r = np.random.default_rng(62)
    # Nieve marina.
    for _ in range(int(tam * 0.9)):
        x, y = r.random() * tam, r.random() * tam; s = 0.5 + r.random() * 0.9
        d.ellipse([x - s, y - s, x + s, y + s], fill=(210, 235, 235, int(60 + 100 * r.random())))
    # Burbujas que suben.
    for _ in range(26):
        x, y = r.random() * tam, r.random() * tam * 0.7; s = 2 + r.random() * 6
        d.ellipse([x - s, y - s, x + s, y + s], outline=(220, 245, 250, 150), width=1)
        d.ellipse([x - s * 0.45, y - s * 0.55, x - s * 0.15, y - s * 0.25], fill=(255, 255, 255, 140))
    # Medusas lejanas en lo hondo, con su luz.
    for (cx, cy, rr, colm) in ((tam * 0.12, tam * 0.78, tam * 0.05, (229, 155, 255)), (tam * 0.86, tam * 0.7, tam * 0.04, (92, 242, 198)),
                               (tam * 0.8, tam * 0.9, tam * 0.03, (63, 169, 255))):
        halo = Image.new('RGBA', (tam, tam), (0, 0, 0, 0)); dh = ImageDraw.Draw(halo, 'RGBA')
        dh.ellipse([cx - rr * 2.4, cy - rr * 2.4, cx + rr * 2.4, cy + rr * 2.4], fill=colm + (70,))
        fondo.alpha_composite(halo.filter(ImageFilter.GaussianBlur(rr)))
        d = ImageDraw.Draw(fondo, 'RGBA')
        d.pieslice([cx - rr, cy - rr * 0.8, cx + rr, cy + rr * 0.8], 180, 360, fill=colm + (190,))
        for k in range(4):
            tx = cx - rr * 0.7 + k * rr * 0.47
            d.line([(tx, cy), (tx + rr * 0.2 * (1 if k % 2 else -1), cy + rr * 0.9), (tx, cy + rr * 1.8)], fill=colm + (150,), width=2)
    fondo = fondo.convert('RGB')
    # La huella de luz viva: verde agua en el centro, violeta en las orillas, con halo y chispas.
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    tono = mezcla(col('#DFFFF6'), col('#5CF2C6'), np.clip(dist * 1.5, 0, 1))
    tono = mezcla(tono, col('#E29BFF'), np.clip((ruido(tam, tam / 18, 63) - 0.5) * 2.2, 0, 1) * 0.55)
    glow = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 60))).astype(np.float32) / 255
    alfa = np.clip(m * 0.95 + glow * 0.75, 0, 1)
    capa = a_rgba(tono, alfa)
    d2 = ImageDraw.Draw(capa, 'RGBA')
    ys, xs = np.nonzero(m > 0.7)
    for _ in range(int(tam / 7)):
        i = r.integers(len(xs)); s = 0.8 + r.random() * 1.4
        d2.ellipse([xs[i] - s, ys[i] - s, xs[i] + s, ys[i] + s], fill=(240, 255, 250, 255))
    guardar(fondo, capa, 'abismo')

# ------------------------------------------------------------------ Al-Ándalus
def andalus(tam):
    m = mascara_sello(tam)
    # Fondo: alicatado de estrellas de ocho puntas en verde, blanco, azul y almagre.
    fondo = Image.new('RGB', (tam, tam), (243, 235, 218))
    d = ImageDraw.Draw(fondo)
    p = tam / 4.5
    colores = [(30, 107, 92), (42, 79, 158), (181, 67, 42), (30, 107, 92)]
    k = 0
    for j in range(-1, 7):
        for i in range(-1, 7):
            cx, cy = i * p, j * p
            for giro in (0, 45):
                pts = [(cx + p * 0.42 * math.cos(math.radians(giro + 45 + 90 * q)), cy + p * 0.42 * math.sin(math.radians(giro + 45 + 90 * q))) for q in range(4)]
                d.polygon(pts, fill=colores[(i + j) % 4])
            pts = [(cx + p * 0.2 * math.cos(math.radians(22.5 + 45 * q)), cy + p * 0.2 * math.sin(math.radians(22.5 + 45 * q))) for q in range(8)]
            d.polygon(pts, fill=(243, 235, 218))
            d.ellipse([cx - p * 0.07, cy - p * 0.07, cx + p * 0.07, cy + p * 0.07], fill=(217, 178, 91))
            k += 1
    # Lazo blanco entre las estrellas.
    for j in range(-1, 7):
        for i in range(-1, 7):
            cx, cy = (i + 0.5) * p, (j + 0.5) * p
            pts = [(cx + p * 0.3 * math.cos(math.radians(90 * q)), cy + p * 0.3 * math.sin(math.radians(90 * q))) for q in range(4)]
            d.polygon(pts, outline=(250, 246, 234), width=max(2, int(tam / 150)))
    tex = ruido(tam, 1.0, 31)[..., None]
    arr = np.array(fondo).astype(np.float32) * (0.9 + 0.12 * tex)
    # Atenuar el centro para que la huella destaque.
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    arr = mezcla(arr, col('#F3EBDA'), np.clip(1.1 - dist * 1.25, 0, 1) * 0.85)
    fondo = a_img(arr).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    c = tam / 2
    k = 1.22
    for giro, color in ((0, (24, 84, 74, 255)), (45, (24, 84, 74, 255))):
        pts = [(c + tam * 0.4 * k * math.cos(math.radians(giro + 45 + 90 * q)), c + tam * 0.4 * k * math.sin(math.radians(giro + 45 + 90 * q))) for q in range(4)]
        d.polygon(pts, fill=color, outline=(217, 178, 91, 255))
    oct_ = [(c + tam * 0.36 * k * math.cos(math.radians(22.5 + 45 * q)), c + tam * 0.36 * k * math.sin(math.radians(22.5 + 45 * q))) for q in range(8)]
    d.polygon(oct_, fill=(18, 64, 56, 255), outline=(217, 178, 91, 255), width=max(2, tam // 140))
    fondo = fondo.convert('RGB')
    # Huella en pan de oro con relieve.
    sh = relieve(m, fuerza=1.2)[..., None]
    oro = mezcla(col('#B8862E'), col('#F2D27A'), ruido(tam, tam / 30, 32))
    rgb = oro * (1 + sh * 0.45)
    capa = a_rgba(rgb, m)
    sombra = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 150))).astype(np.float32) / 255
    sombra = np.roll(np.roll(sombra, 2, 0), 2, 1) * 0.5
    base = a_rgba(np.zeros((tam, tam, 3)) + col('#2A1A12'), sombra)
    base.alpha_composite(capa)
    guardar(fondo, base, 'andalus')

# ------------------------------------------------------------------ Mesoamérica
def meso(tam):
    """Mesoamérica: papel amate de fibras cruzadas, la rueda de los veinte cartuchos de días alrededor, los renglones
    rojos del códice y, en cada cartucho, un numeral de punto y barra; la huella, en jade pulido."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    a = mezcla(col('#E6D3AE'), col('#D4BC8E'), ruido(tam, tam / 16, 71) * 0.8)
    r = np.random.default_rng(72)
    fib = Image.new('L', (tam, tam), 0); df = ImageDraw.Draw(fib)
    for _ in range(int(tam * 0.9)):
        x, y = r.random() * tam, r.random() * tam
        ang = (r.random() - 0.5) * 0.9 + (0 if r.random() < 0.5 else math.pi / 2); l = tam * (0.02 + r.random() * 0.07)
        df.line([(x, y), (x + math.cos(ang) * l, y + math.sin(ang) * l)], fill=255, width=max(1, tam // 360))
    fib = np.asarray(fib.filter(ImageFilter.GaussianBlur(0.8)), np.float32) / 255
    a = a * (1 - 0.07 * fib[..., None])
    fondo = a_img(a).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    c = tam / 2
    cinabrio = (163, 48, 30, 255); tinta = (42, 24, 16, 150); azul = (62, 143, 168, 220)
    # Los renglones rojos del códice, arriba y abajo, que dejan libre el centro.
    w = max(3, tam // 160)
    for y in (tam * 0.12, tam * 0.88):
        d.line([(0, y), (tam, y)], fill=cinabrio[:3] + (170,), width=w)
    # La rueda de los veinte días: un anillo con veinte cartuchos, uno de cada cuatro en azul maya.
    r0, r1 = tam * 0.405, tam * 0.475
    d.ellipse([c - r0, c - r0, c + r0, c + r0], outline=cinabrio, width=w)
    d.ellipse([c - r1, c - r1, c + r1, c + r1], outline=cinabrio, width=w)
    for i in range(20):
        ang = 2 * math.pi * i / 20 - math.pi / 2; rm = (r0 + r1) / 2; q = tam * 0.024
        x, y = c + rm * math.cos(ang), c + rm * math.sin(ang)
        d.rounded_rectangle([x - q, y - q, x + q, y + q], radius=int(q * 0.4), outline=tinta, width=max(2, w - 1),
                            fill=azul if i % 4 == 0 else None)
        # Dentro de cada cartucho, un numeral pequeño: un punto sobre una barra (el seis).
        f = (250, 244, 228, 230) if i % 4 == 0 else tinta
        d.rectangle([x - q * 0.6, y + q * 0.12, x + q * 0.6, y + q * 0.42], fill=f)
        d.ellipse([x - q * 0.2, y - q * 0.5, x + q * 0.2, y - q * 0.1], fill=f)
    fondo = fondo.convert('RGB')
    # La huella en jade pulido: verde con vetas claras y oscuras, y el brillo del pulido.
    sh = relieve(m, fuerza=1.3)[..., None]
    vetas = np.sin(xx * 0.03 + ruido(tam, 10, 73) * 3) * 0.5 + 0.5
    piedra = mezcla(col('#1F6B48'), col('#5FB98A'), np.clip(ruido(tam, tam / 20, 74) * 0.6 + vetas * 0.5, 0, 1))
    rgb = piedra * (1 + sh * 0.5)
    brillo = np.clip(1 - np.abs((xx + yy) / tam - 0.75) * 2.4, 0, 1) ** 3
    rgb = mezcla(rgb, col('#E6FFF0'), brillo * 0.3 * m)
    capa = a_rgba(rgb, m)
    sombra = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 150))).astype(np.float32) / 255
    sombra = np.roll(np.roll(sombra, 2, 0), 2, 1) * 0.45
    base = a_rgba(np.zeros((tam, tam, 3)) + col('#2A1810'), sombra)
    base.alpha_composite(capa)
    guardar(fondo, base, 'meso')


# ------------------------------------------------------------------ egipto

def egipto(tam):
    """Egipto: papiro con sus fibras, la cenefa de rectángulos de colores alrededor, columnas de signos pintados a los
    lados y un gran cartucho rojo detrás; la huella, en fayenza azul vidriada (la cerámica de cuarzo de los amuletos)."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    a = mezcla(col('#ECDCA8'), col('#DCC68A'), ruido(tam, tam / 14, 61) * 0.7)
    tiras = np.sin(yy * 0.55 + ruido(tam, 6, 62) * 6) * 0.5 + 0.5
    a = a * (0.95 + 0.06 * tiras[..., None])
    a = a * (0.97 + 0.04 * (np.sin(xx * 0.21 + ruido(tam, 8, 63) * 4) * 0.5 + 0.5))[..., None]
    fondo = a_img(a).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    c = tam / 2
    lapis = (29, 47, 122, 255); oro = (201, 151, 28, 255); corn = (184, 71, 30, 255)
    # La cenefa de rectángulos de colores, cerca del borde del círculo.
    n = 48; r0, r1 = tam * 0.43, tam * 0.48
    colores = [lapis, oro, corn, oro]
    for i in range(n):
        a0 = 2 * math.pi * i / n; a1 = a0 + 2 * math.pi / n * 0.72
        pts = [(c + r0 * math.cos(a0), c + r0 * math.sin(a0)), (c + r1 * math.cos(a0), c + r1 * math.sin(a0)),
               (c + r1 * math.cos(a1), c + r1 * math.sin(a1)), (c + r0 * math.cos(a1), c + r0 * math.sin(a1))]
        d.polygon(pts, fill=colores[i % 4])
    w = max(2, tam // 240)
    d.ellipse([c - r0 + w * 2, c - r0 + w * 2, c + r0 - w * 2, c + r0 - w * 2], outline=(30, 22, 12, 160), width=w)
    # El cartucho grande detrás de la huella, en ocre rojo, con su barra abajo.
    cw, ch = tam * 0.27, tam * 0.36
    d.rounded_rectangle([c - cw, c - ch, c + cw, c + ch * 0.95], radius=int(cw), outline=corn[:3] + (150,), width=max(4, tam // 110))
    d.rectangle([c - cw * 1.12, c + ch, c + cw * 1.12, c + ch + tam * 0.022], fill=corn[:3] + (160,))
    # Columnas de signos a los lados: el ankh (en rojo de rúbrica), el ojo, la pluma y otra vez el ankh.
    def ankh(x, y, s, f):
        g = max(2, int(s * 0.09))
        d.ellipse([x - s * 0.22, y - s * 0.5, x + s * 0.22, y - s * 0.08], outline=f, width=g)
        d.line([(x - s * 0.32, y - s * 0.04), (x + s * 0.32, y - s * 0.04)], fill=f, width=g)
        d.line([(x, y - s * 0.06), (x, y + s * 0.5)], fill=f, width=g)
    def pluma(x, y, s, f):
        d.polygon([(x - s * 0.06, y + s * 0.5), (x - s * 0.12, y - s * 0.2), (x + s * 0.06, y - s * 0.5), (x + s * 0.24, y - s * 0.45), (x + s * 0.1, y + s * 0.5)], fill=f)
    def ojo(x, y, s, f):
        g = max(2, int(s * 0.07))
        d.ellipse([x - s * 0.42, y - s * 0.16, x + s * 0.42, y + s * 0.12], outline=f, width=g)
        d.ellipse([x - s * 0.1, y - s * 0.12, x + s * 0.1, y + s * 0.08], fill=f)
        d.arc([x - s * 0.46, y - s * 0.5, x + s * 0.5, y + s * 0.1], 200, 340, fill=f, width=g)
        d.line([(x - s * 0.06, y + s * 0.1), (x - s * 0.12, y + s * 0.45)], fill=f, width=g)
    s = tam * 0.075
    tinta = (40, 30, 18, 120)
    for x in (c - tam * 0.36, c + tam * 0.36):
        for k, fn in enumerate((ankh, ojo, pluma, ankh)):
            fn(x, c - tam * 0.22 + k * tam * 0.15, s, corn[:3] + (140,) if k == 0 else tinta)
    fondo = fondo.convert('RGB')
    # La huella en fayenza: azul turquesa vidriado, con el brillo del esmalte.
    sh = relieve(m, fuerza=1.3)[..., None]
    fay = mezcla(col('#1B6E8A'), col('#3FB0B8'), ruido(tam, tam / 25, 64))
    rgb = fay * (1 + sh * 0.5)
    brillo = np.clip(1 - np.abs((xx + yy) / tam - 0.8) * 2.2, 0, 1) ** 3
    rgb = mezcla(rgb, col('#E8FFFF'), brillo * 0.35 * m)
    capa = a_rgba(rgb, m)
    sombra = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 150))).astype(np.float32) / 255
    sombra = np.roll(np.roll(sombra, 2, 0), 2, 1) * 0.45
    base = a_rgba(np.zeros((tam, tam, 3)) + col('#3A2A12'), sombra)
    base.alpha_composite(capa)
    guardar(fondo, base, 'egipto')

# ------------------------------------------------------------------ mongol
def mongol(tam):
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    cielo = mezcla(col('#0F3E86'), col('#5FA2E6'), yy / tam)
    nubes = np.clip((ruido(tam, tam / 12, 41) - 0.55) * 2.5, 0, 1)
    cielo = mezcla(cielo, col('#EAF2FB'), nubes * 0.35)
    fieltro = ruido(tam, 0.8, 42)[..., None]
    cielo = cielo * (0.94 + 0.1 * fieltro)
    fondo = a_img(cielo).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    # Bandas rojas con grecas de martillos arriba y abajo.
    bh = tam * 0.1
    for y0 in (0, tam - bh):
        d.rectangle([0, y0, tam, y0 + bh], fill=(194, 65, 45, 255))
        u = bh / 4; x = 0; ym = y0 + bh / 2
        linea = [(0, ym)]
        while x < tam:
            linea += [(x + u, ym), (x + u, y0 + u * 0.6), (x + 3 * u, y0 + u * 0.6), (x + 3 * u, ym), (x + 2 * u, ym), (x + 2 * u, y0 + bh - u * 0.6), (x + 4 * u, y0 + bh - u * 0.6), (x + 4 * u, ym)]
            x += 4 * u
        d.line(linea, fill=(242, 193, 78, 255), width=max(2, int(tam / 160)))
    # Sol de oro.
    d.ellipse([tam * 0.76, tam * 0.15, tam * 0.9, tam * 0.29], fill=(242, 193, 78, 160))
    fondo = fondo.convert('RGB')
    # Huella de fieltro blanco con borde cosido en rojo.
    f = ruido(tam, 0.9, 43)
    rgb = mezcla(col('#F7F4EC'), col('#DCD6C8'), f)
    sh = relieve(m, fuerza=0.8)[..., None]
    rgb = rgb * (1 + sh * 0.3)
    capa = a_rgba(rgb, m)
    sombra = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 100))).astype(np.float32) / 255
    base = a_rgba(np.zeros((tam, tam, 3)) + col('#0A1A33'), np.roll(sombra, 3, 0) * 0.45)
    base.alpha_composite(capa)
    guardar(fondo, base, 'mongol')

# ------------------------------------------------------------------ Renacimiento
def renacimiento(tam):
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    papel = mezcla(col('#F0E2BE'), col('#D9C394'), ruido(tam, tam / 18, 51) * 0.8)
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    papel = mezcla(papel, col('#B89A66'), np.clip(dist - 0.7, 0, 1) * 0.9)
    manchas = np.clip((ruido(tam, tam / 40, 52) - 0.72) * 4, 0, 1)
    papel = mezcla(papel, col('#C9A86E'), manchas * 0.5)
    papel = papel * (0.95 + 0.08 * ruido(tam, 0.7, 53)[..., None])
    fondo = a_img(papel).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    tinta = (58, 38, 20, 70)
    c = tam / 2; R = tam * 0.4
    d.ellipse([c - R, c - R * 1.0 + tam * 0.02, c + R, c + R + tam * 0.02], outline=tinta, width=max(1, tam // 300))
    s = tam * 0.34
    d.rectangle([c - s, c - s * 0.86, c + s, c + s * 1.02], outline=tinta, width=max(1, tam // 300))
    d.line([(c - s, c - s * 0.86), (c + s, c + s * 1.02)], fill=(58, 38, 20, 40), width=1)
    d.line([(c + s, c - s * 0.86), (c - s, c + s * 1.02)], fill=(58, 38, 20, 40), width=1)
    # Renglones de escritura en espejo (garabatos) en un margen.
    r = np.random.default_rng(54)
    for k in range(7):
        y = tam * 0.08 + k * tam * 0.025; x = tam * 0.62
        pts = [(x, y)]
        while x < tam * 0.94:
            x += tam * (0.005 + r.random() * 0.008); pts.append((x, y + (r.random() - 0.5) * tam * 0.01))
        d.line(pts, fill=(58, 38, 20, 90), width=1)
    fondo = fondo.convert('RGB')
    # Huella en sanguina: grano de tiza y rayado diagonal.
    grano = ruido(tam, 0.6, 55)
    rayas = (np.sin((xx + yy) * (2 * math.pi / (tam / 90))) * 0.5 + 0.5)
    alfa = m * np.clip(0.55 + 0.35 * grano + 0.15 * rayas, 0, 1)
    rgb = mezcla(col('#8C3B1E'), col('#B5562E'), ruido(tam, tam / 25, 56))
    capa = a_rgba(rgb, alfa)
    guardar(fondo, capa, 'renacimiento')


# ------------------------------------------------------------------ Pirata
def pirata(tam):
    """Carta náutica gastada: pergamino con manchas de agua, las 16 líneas de rumbo de una rosa, la costa de una isla,
    la derrota punteada hasta la «X». La huella, en tinta de brea que se corre un poco con la humedad."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    papel = mezcla(col('#EADFC0'), col('#CDB88A'), ruido(tam, tam / 16, 61) * 0.8)
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    papel = mezcla(papel, col('#8E7448'), np.clip(dist - 0.72, 0, 1) * 1.1)
    agua = np.clip((ruido(tam, tam / 30, 62) - 0.66) * 5, 0, 1)
    papel = mezcla(papel, col('#B9A273'), agua * 0.45)
    papel = papel * (0.95 + 0.08 * ruido(tam, 0.7, 63)[..., None])
    fondo = a_img(papel).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    tinta = (46, 34, 20, 60)
    # Las líneas de rumbo salen de una rosa fuera de centro, como en los portulanos.
    cx, cy = tam * 0.8, tam * 0.18
    for k in range(16):
        a = k * math.pi / 8
        d.line([(cx, cy), (cx + math.cos(a) * tam * 1.6, cy + math.sin(a) * tam * 1.6)], fill=tinta if k % 2 == 0 else (46, 34, 20, 34), width=1)
    for k in range(8):
        a = -math.pi / 2 + k * math.pi / 4; l = tam * (0.07 if k % 2 == 0 else 0.045)
        b1 = a + math.pi / 2; w = tam * 0.012
        d.polygon([(cx + math.cos(a) * l, cy + math.sin(a) * l), (cx + math.cos(b1) * w, cy + math.sin(b1) * w),
                   (cx - math.cos(b1) * w, cy - math.sin(b1) * w)], fill=(46, 34, 20, 110 if k % 2 == 0 else 70))
    # La costa de una isla abajo a la izquierda, con sus rayitas de agua.
    r = np.random.default_rng(64)
    ix, iy, ir = tam * 0.22, tam * 0.8, tam * 0.11
    costa = [(ix + math.cos(t) * ir * (1 + 0.18 * math.sin(3 * t) + 0.08 * r.random()), iy + math.sin(t) * ir * 0.62 * (1 + 0.12 * math.cos(5 * t)))
             for t in np.linspace(0, 2 * math.pi, 40)]
    d.polygon(costa, fill=(150, 120, 70, 50), outline=(46, 34, 20, 110))
    for e in (1.15, 1.32):
        d.line([(ix + (x - ix) * e, iy + (y - iy) * e) for x, y in costa][::2], fill=(46, 34, 20, 40), width=1)
    # La derrota punteada hasta la «X», en rojo de almagre.
    rojo = (140, 40, 26, 120)
    pts = [(tam * 0.34, tam * 0.83), (tam * 0.5, tam * 0.94), (tam * 0.8, tam * 0.9), (tam * 0.9, tam * 0.7)]
    for i in range(60):
        t = i / 60
        x = (1 - t) ** 3 * pts[0][0] + 3 * (1 - t) ** 2 * t * pts[1][0] + 3 * (1 - t) * t ** 2 * pts[2][0] + t ** 3 * pts[3][0]
        y = (1 - t) ** 3 * pts[0][1] + 3 * (1 - t) ** 2 * t * pts[1][1] + 3 * (1 - t) * t ** 2 * pts[2][1] + t ** 3 * pts[3][1]
        if i % 2 == 0: d.ellipse([x - 2, y - 2, x + 2, y + 2], fill=rojo)
    x, y, s = tam * 0.9, tam * 0.66, tam * 0.025
    d.line([(x - s, y - s), (x + s, y + s)], fill=(140, 40, 26, 170), width=max(2, tam // 150))
    d.line([(x - s, y + s), (x + s, y - s)], fill=(140, 40, 26, 170), width=max(2, tam // 150))
    fondo = fondo.convert('RGB')
    # Huella en tinta de brea: casi negra, con el borde corrido por la humedad y poros de papel.
    corrida = np.clip(desenfocar(m.astype(np.float32), tam / 500) * 1.15, 0, 1)
    grano = ruido(tam, 0.6, 65)
    alfa = np.clip(corrida * (0.72 + 0.28 * grano), 0, 1) * np.clip(0.85 + 0.25 * ruido(tam, tam / 30, 66), 0, 1)
    rgb = mezcla(col('#241A10'), col('#4A3420'), ruido(tam, tam / 25, 67))
    capa = a_rgba(rgb, alfa)
    guardar(fondo, capa, 'pirata')

# ------------------------------------------------------------------ Roma (la de hoy)
def urbe(tam):
    """El muro ocre de una calle de Roma, con el revoque caído y el ladrillo a la vista, y encima la huella como un
    timbre de oficina en tinta roja: entintada de más en los bordes y gastada en el centro."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)

    def suave(s, semilla):   # ruido en coma flotante, sin los escalones del desenfoque en 8 bits
        a = desenfocar(np.random.default_rng(semilla).random((tam, tam)).astype(np.float32), s)
        return (a - a.min()) / max(1e-6, a.max() - a.min())
    ocre = mezcla(col('#E2B172'), col('#C98E4E'), suave(tam / 14, 61) * 0.9)
    ocre = ocre * (0.95 + 0.08 * ruido(tam, 0.8, 62)[..., None])
    # Donde se cayó el revoque: ladrillo en hiladas, con su borde de cal.
    caida = suave(tam / 26, 63)
    hueco = np.clip((caida - 0.66) * 9, 0, 1)
    borde = np.clip(1 - np.abs(caida - 0.64) * 30, 0, 1)
    fila = (yy // (tam / 34)).astype(int)
    junta = ((yy % (tam / 34)) < tam / 260) | (((xx + (fila % 2) * tam / 18) % (tam / 9)) < tam / 260)
    ladrillo = mezcla(col('#A2553A'), col('#7F3B27'), ruido(tam, 2, 64))
    ladrillo = np.where(junta[..., None], col('#D9CDB4'), ladrillo)
    muro = mezcla(ocre, ladrillo, hueco)
    muro = mezcla(muro, col('#F1E6CF'), borde * 0.45)
    # Sombra suave arriba, como el alero de un tejado.
    muro = muro * (0.86 + 0.14 * np.clip(yy / (tam * 0.25), 0, 1))[..., None]
    fondo = a_img(muro)
    # La huella: tinta de timbre, más cargada en los bordes de cada cresta y comida en el centro.
    b = desenfocar(m, tam / 260)
    filo = np.clip((m - b) * 3 + 0.0, 0, 1)
    gasto = suave(tam / 30, 65)
    alfa = m * np.clip(0.62 + 0.3 * filo + 0.25 * ruido(tam, 0.7, 66) - np.clip(gasto - 0.6, 0, 1) * 1.1, 0, 0.97)
    rgb = mezcla(col('#9B1C1C'), col('#6E1414'), suave(tam / 40, 67))
    capa = a_rgba(rgb, alfa)
    guardar(fondo, capa, 'urbe')


def vapor(tam):
    """La puerta de una caldera: chapa de hierro ennegrecida por el hollín, con sus costuras y filas de remaches de latón,
    el bisel de latón de un manómetro con su escala por el borde y, encima, la huella en latón bruñido, con relieve y algo
    de cardenillo en los surcos."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    cx = cy = tam / 2
    dist = np.sqrt((xx - cx) ** 2 + (yy - cy) ** 2) / (tam / 2)
    hierro = mezcla(col('#3A342D'), col('#24201C'), ruido(tam, tam / 18, 141))
    hierro = hierro * (0.92 + 0.12 * ruido(tam, 1.0, 142)[..., None])
    hollin = np.clip((ruido(tam, tam / 10, 143) - 0.45) * 2, 0, 1) * 0.5
    hierro = mezcla(hierro, col('#120F0C'), hollin)
    for yc in (tam * 0.2, tam * 0.8):
        hierro = mezcla(hierro, col('#14110E'), (np.abs(yy - yc) < tam / 300).astype(np.float32) * 0.8)
    fondo = a_img(hierro).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    r0 = tam * 0.475
    d.ellipse([cx - r0, cy - r0, cx + r0, cy + r0], outline=(201, 160, 74, 255), width=int(tam * 0.024))
    r1 = r0 - tam * 0.03
    for k in range(60):
        a = math.radians(k * 6); l = tam * (0.042 if k % 5 == 0 else 0.022)
        d.line([(cx + r1 * math.cos(a), cy + r1 * math.sin(a)), (cx + (r1 - l) * math.cos(a), cy + (r1 - l) * math.sin(a))],
               fill=(201, 160, 74, 210), width=max(1, int(tam * (0.006 if k % 5 == 0 else 0.003))))
    for yc in (tam * 0.2 - tam * 0.028, tam * 0.2 + tam * 0.028, tam * 0.8 - tam * 0.028, tam * 0.8 + tam * 0.028):
        for x in np.arange(tam * 0.08, tam * 0.94, tam * 0.06):
            if math.hypot(x - cx, yc - cy) < r1 - tam * 0.08:
                rr = tam * 0.011
                d.ellipse([x - rr, yc - rr, x + rr, yc + rr], fill=(176, 134, 43, 255))
                d.ellipse([x - rr * 0.85, yc - rr * 0.85, x + rr * 0.15, yc + rr * 0.15], fill=(240, 212, 144, 170))
    arr = np.array(fondo.convert('RGB')).astype(np.float32)
    arr = mezcla(arr, col('#0E0C0A'), np.clip((dist - 0.975) * 10, 0, 1))
    fondo = a_img(arr)
    sh = relieve(m, fuerza=1.3)[..., None]
    luz = np.clip(1 - np.abs((xx + yy) / tam - 0.9) * 1.4, 0, 1) ** 2
    laton = mezcla(col('#8A5A14'), col('#E8C470'), np.clip(luz * 0.8 + ruido(tam, tam / 40, 144) * 0.3, 0, 1))
    laton = mezcla(laton, col('#4E8F7E'), np.clip((ruido(tam, tam / 60, 145) - 0.72) * 3, 0, 1) * 0.4)
    rgb = laton * (1 + sh * 0.5)
    capa = a_rgba(rgb, m)
    sombra = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 120))).astype(np.float32) / 255
    base = a_rgba(np.zeros((tam, tam, 3)) + col('#0A0806'), np.roll(np.roll(sombra, 4, 0), 4, 1) * 0.6)
    base.alpha_composite(capa)
    guardar(fondo, base, 'vapor')

# ------------------------------------------------------------------ Frente exterior
def frente(tam):
    """La pantalla redonda de radar de una consola de los 70. Por fuera, el bisel de la consola, color hueso, moldeado y
    con su piel de naranja, la escala de rumbos grabada, cuatro tornillos y, abajo, un piloto ámbar encendido, uno rojo y
    la perilla de baquelita. Por dentro, el vidrio verde oscuro con la retícula, los anillos de distancia, unos ecos y el
    barrido que deja su estela. La huella es el eco que el barrido acaba de pintar: verde pálido de fósforo, casi blanco
    donde pasó la línea y apagándose hacia atrás."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    cx = cy = tam / 2
    dx, dy = xx - cx, yy - cy
    rr = np.sqrt(dx ** 2 + dy ** 2) / (tam / 2)
    ang = np.arctan2(dy, dx)
    R_VIDRIO = 0.87                                  # radio del vidrio, en fracción del radio del sello
    BARRIDO = math.radians(-38)                       # la línea de barrido, arriba a la derecha; gira en el sentido del reloj
    atras = (BARRIDO - ang) % (2 * math.pi)           # cuánto hace que pasó la línea por cada ángulo
    estela = np.exp(-atras / 0.9)

    # --- El bisel: hueso de consola, moldeado (luz arriba a la izquierda), con piel de naranja y desgaste.
    hueso = mezcla(col('#E6DFD0'), col('#CFC6B4'), ruido(tam, tam / 14, 401) * 0.7)
    hueso = hueso * (0.965 + 0.05 * ruido(tam, 1.2, 402)[..., None] + 0.02 * ruido(tam, 0.5, 403)[..., None])
    hueso = mezcla(hueso, col('#B9B0A0'), np.clip((ruido(tam, tam / 25, 404) - 0.62) * 2.5, 0, 1) * 0.35)
    t_bisel = np.clip((rr - R_VIDRIO) / (1 - R_VIDRIO), 0, 1)
    perfil = np.sin(t_bisel * math.pi)                # el lomo del bisel
    luz = (-(dx + dy) / (rr * tam / 2 + 1e-3) / math.sqrt(2))   # 1 arriba a la izquierda, -1 abajo a la derecha
    hueso = hueso * (0.97 + 0.16 * luz[..., None] * (0.35 + 0.65 * perfil[..., None]) + 0.05 * perfil[..., None])
    # El labio interior: una sombra que cae sobre el vidrio del lado de la luz y un filo claro del otro.
    hueso = mezcla(hueso, col('#8C8474'), np.clip(1 - (rr - R_VIDRIO) / 0.018, 0, 1) * 0.55)
    hueso = mezcla(hueso, col('#5C564C'), np.clip((rr - 0.985) * 70, 0, 1) * 0.8)

    # --- El vidrio: verde casi negro, más claro en el centro, con un poco de grano de fósforo.
    vidrio = mezcla(col('#18291F'), col('#0B140F'), np.clip(rr / R_VIDRIO, 0, 1) ** 1.6)
    vidrio = vidrio * (0.94 + 0.1 * ruido(tam, 0.6, 405)[..., None])
    vidrio = mezcla(vidrio, col('#2F6B45'), np.clip(np.exp(-atras / 0.75) * 0.7 * (rr < R_VIDRIO), 0, 1) * np.clip(rr / R_VIDRIO * 1.6, 0, 1))
    # Sombra del bisel sobre el vidrio, más ancha abajo a la derecha (la luz viene de arriba a la izquierda).
    borde_sombra = np.clip(1 - (R_VIDRIO - rr) / (0.06 + 0.04 * np.clip(-luz, 0, 1)), 0, 1) ** 2
    vidrio = mezcla(vidrio, col('#030605'), borde_sombra * 0.75)
    dentro = (rr < R_VIDRIO).astype(np.float32)
    dentro = np.asarray(Image.fromarray((dentro * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.8)), np.float32) / 255
    base = mezcla(hueso, vidrio, dentro)
    fondo = a_img(base).convert('RGBA')

    # --- Lo que se dibuja con líneas, al triple y reducido.
    k = 3; S = tam * k; c3 = S / 2
    capa = Image.new('RGBA', (S, S), (0, 0, 0, 0)); d = ImageDraw.Draw(capa, 'RGBA')
    rv = R_VIDRIO * S / 2
    verde = (159, 216, 168)
    def P(a, r): return (c3 + r * math.cos(a), c3 + r * math.sin(a))
    # La retícula: anillos de distancia, la cruz y las diagonales punteadas.
    for f in (0.25, 0.5, 0.75):
        d.ellipse([c3 - rv * f, c3 - rv * f, c3 + rv * f, c3 + rv * f], outline=verde + (46,), width=2 * k // 2 + 1)
    for a in (0, math.pi / 2):
        d.line([P(a, rv * 0.97), P(a + math.pi, rv * 0.97)], fill=verde + (40,), width=k)
    for a in (math.pi / 4, 3 * math.pi / 4, 5 * math.pi / 4, 7 * math.pi / 4):
        for s in np.arange(0.06, 0.95, 0.045):
            d.line([P(a, rv * s), P(a, rv * (s + 0.018))], fill=verde + (30,), width=k)
    # La escala de rumbos por dentro del vidrio: cada 5°, más larga cada 30°.
    for i in range(72):
        a = math.radians(i * 5); l = rv * (0.06 if i % 6 == 0 else 0.03)
        d.line([P(a, rv * 0.985), P(a, rv * 0.985 - l)], fill=verde + (120 if i % 6 == 0 else 70,), width=k * (2 if i % 6 == 0 else 1))
    # La línea de barrido, con un leve halo.
    for w, al in ((10, 30), (5, 70), (2, 230)):
        d.line([P(BARRIDO, rv * 0.02), P(BARRIDO, rv * 0.975)], fill=(214, 248, 218, al), width=w * k // 2)
    # Ecos sueltos: más vivos los que el barrido acaba de tocar.
    r_ = np.random.default_rng(406)
    for _ in range(9):
        a = r_.random() * 2 * math.pi; dist = rv * (0.3 + r_.random() * 0.62)
        viva = math.exp(-((BARRIDO - a) % (2 * math.pi)) / 1.1)
        s = (2.2 + r_.random() * 2.5) * k
        x, y = P(a, dist)
        d.ellipse([x - s * 2.6, y - s * 2.6, x + s * 2.6, y + s * 2.6], fill=verde + (int(30 + 50 * viva),))
        d.ellipse([x - s, y - s, x + s, y + s], fill=(200, 245, 205, int(90 + 160 * viva)))
    # Los grabados del bisel: escala de rumbos cada 10° (larga cada 30°) y triángulos en los cuatro rumbos principales.
    r_esc = S / 2 * 0.912
    for i in range(36):
        a = math.radians(i * 10 - 90); l = S / 2 * (0.032 if i % 3 == 0 else 0.016)
        d.line([P(a, r_esc - l / 2), P(a, r_esc + l / 2)], fill=(70, 64, 55, 225), width=k * (3 if i % 3 == 0 else 2))
    for i in range(4):
        a = math.radians(i * 90 - 90); q = S / 2 * 0.018
        x, y = P(a, S / 2 * 0.962)
        u, v = math.cos(a), math.sin(a)
        d.polygon([(x - u * q, y - v * q), (x + u * q - v * q, y + v * q + u * q), (x + u * q + v * q, y + v * q - u * q)], fill=(78, 72, 62, 210))
    # Cuatro tornillos de cabeza ranurada, en las diagonales.
    for i in range(4):
        a = math.radians(45 + i * 90 + 12); x, y = P(a, S / 2 * 0.937); s = S / 2 * 0.022
        d.ellipse([x - s, y - s, x + s, y + s], fill=(170, 164, 152, 255), outline=(96, 90, 80, 255), width=k)
        d.ellipse([x - s * 0.8, y - s * 0.8, x + s * 0.2, y + s * 0.2], fill=(236, 232, 222, 120))
        g = math.radians(30 + i * 47)
        d.line([(x - s * 0.75 * math.cos(g), y - s * 0.75 * math.sin(g)), (x + s * 0.75 * math.cos(g), y + s * 0.75 * math.sin(g))], fill=(70, 64, 56, 255), width=int(k * 2.2))
    fondo.alpha_composite(capa.resize((tam, tam), Image.LANCZOS))

    # --- Pilotos y perilla, abajo, sobre el bisel: el ámbar encendido (con su resplandor), el rojo apagado a medias, la perilla.
    k = 3; capa = Image.new('RGBA', (S, S), (0, 0, 0, 0)); d = ImageDraw.Draw(capa, 'RGBA')
    resplandor = Image.new('RGBA', (S, S), (0, 0, 0, 0)); dr = ImageDraw.Draw(resplandor, 'RGBA')
    for grados, luz_c, viva in ((118, (242, 163, 58), 1.0), (130, (217, 55, 43), 0.75)):
        x, y = P(math.radians(grados), S / 2 * 0.936); s = S / 2 * 0.031
        dr.ellipse([x - s * 2.4, y - s * 2.4, x + s * 2.4, y + s * 2.4], fill=luz_c + (int(110 * viva),))
        d.ellipse([x - s * 1.25, y - s * 1.25, x + s * 1.25, y + s * 1.25], fill=(185, 182, 174, 255), outline=(110, 104, 94, 255), width=k)
        d.ellipse([x - s, y - s, x + s, y + s], fill=(42, 38, 34, 255))
        d.ellipse([x - s * 0.78, y - s * 0.78, x + s * 0.78, y + s * 0.78], fill=tuple(int(v * (0.6 + 0.4 * viva)) for v in luz_c) + (255,))
        d.ellipse([x - s * 0.42, y - s * 0.42, x + s * 0.42, y + s * 0.42], fill=tuple(min(255, int(v * 1.25 + 30)) for v in luz_c) + (int(200 * viva),))
        d.ellipse([x - s * 0.55, y - s * 0.6, x - s * 0.1, y - s * 0.2], fill=(255, 250, 235, 170))
    x, y = P(math.radians(72), S / 2 * 0.936); s = S / 2 * 0.038
    d.ellipse([x - s * 1.12 + k * 3, y - s * 1.12 + k * 4, x + s * 1.12 + k * 3, y + s * 1.12 + k * 4], fill=(40, 34, 28, 110))
    for i in range(24):                                # la perilla estriada de baquelita
        a = 2 * math.pi * i / 24
        d.polygon([(x + s * math.cos(a - 0.1), y + s * math.sin(a - 0.1)), (x + s * 1.1 * math.cos(a), y + s * 1.1 * math.sin(a)),
                   (x + s * math.cos(a + 0.1), y + s * math.sin(a + 0.1))], fill=(42, 38, 34, 255))
    d.ellipse([x - s, y - s, x + s, y + s], fill=(42, 38, 34, 255))
    d.ellipse([x - s * 0.72, y - s * 0.72, x + s * 0.72, y + s * 0.72], fill=(58, 52, 46, 255))
    d.ellipse([x - s * 0.62, y - s * 0.66, x + s * 0.05, y - s * 0.05], fill=(120, 112, 100, 70))
    g = math.radians(-120)
    d.line([(x + s * 0.2 * math.cos(g), y + s * 0.2 * math.sin(g)), (x + s * 0.68 * math.cos(g), y + s * 0.68 * math.sin(g))], fill=(236, 230, 216, 230), width=int(k * 2.4))
    fondo.alpha_composite(resplandor.filter(ImageFilter.GaussianBlur(S * 0.012)).resize((tam, tam), Image.LANCZOS))
    fondo.alpha_composite(capa.resize((tam, tam), Image.LANCZOS))
    # Un reflejo de la sala en el vidrio, arriba a la izquierda: una media luna clara y muy tenue.
    refl = np.clip(1 - np.abs(np.sqrt((xx - cx + tam * 0.06) ** 2 + (yy - cy + tam * 0.07) ** 2) / (tam / 2) - 0.66) / 0.1, 0, 1)
    refl = refl * np.clip(-np.sin(ang + math.radians(45)) * 1.3 - 0.35, 0, 1) * (rr < R_VIDRIO - 0.02)
    arr = np.asarray(fondo.convert('RGB'), np.float32)
    arr = mezcla(arr, col('#E8F2EA'), desenfocar(refl.astype(np.float32), 6) * 0.13)
    fondo = a_img(arr)

    # --- La huella: el eco en fósforo. Casi blanca donde acaba de pasar la línea, verde pálido y más tenue hacia atrás.
    viva = np.exp(-atras / 1.6)
    fosforo = mezcla(col('#5FA66F'), col('#9FD8A8'), np.clip(0.35 + viva, 0, 1))
    fosforo = mezcla(fosforo, col('#EAFBEA'), np.clip(np.exp(-atras / 0.35) * 0.85, 0, 1))
    grano = ruido(tam, 0.7, 407)
    nucleo = np.clip((desenfocar(m, 2.2) - 0.55) / 0.45, 0, 1)
    rgb = fosforo * (0.88 + 0.16 * nucleo[..., None]) * (0.95 + 0.1 * grano[..., None])
    alfa = m * np.clip(0.55 + 0.45 * viva + 0.06 * (grano - 0.5), 0, 1)
    capa = a_rgba(rgb, alfa)
    halo = a_rgba(np.zeros((tam, tam, 3)) + col('#7FD08F'), np.clip(desenfocar(m, 7) * 1.25, 0, 1) * (1 - m) * (0.18 + 0.6 * viva))
    halo.alpha_composite(capa)
    guardar(fondo, halo, 'frente')

# ------------------------------------------------------------------ Arcontes
def bezier(p0, p1, p2, p3, n=40):
    return [tuple((1 - t) ** 3 * a + 3 * (1 - t) ** 2 * t * b + 3 * (1 - t) * t ** 2 * c + t ** 3 * e
                  for a, b, c, e in zip(p0, p1, p2, p3)) for t in (i / n for i in range(n + 1))]


def abraxas(d, x0, y0, u, c, hueco):
    """Abraxas como en las gemas grabadas (siglos II–IV): cabeza de gallo de perfil, torso con coraza y faldellín de tiras,
    las dos piernas de serpiente que se abren y levantan la cabeza a los lados, el látigo en alto en la mano de fuera y el
    escudo redondo en la otra. Mira a la derecha. Origen en la cintura; [u] = px por unidad (la figura mide unas 88 de alto
    y 62 de ancho). [c] es el color del grabado y [hueco] el de los cortes de detalle."""
    def P(x, y): return (x0 + x * u, y0 + y * u)
    def circ(x, y, r, f=c):
        cx, cy = P(x, y); d.ellipse([cx - r * u, cy - r * u, cx + r * u, cy + r * u], fill=f)
    def linea(pts, w, f=c):
        q = [P(*p) for p in pts]
        d.line(q, fill=f, width=max(1, int(w * u)), joint='curve')
        for (a, b) in (q[0], q[-1]):
            r = w * u / 2; d.ellipse([a - r, b - r, a + r, b + r], fill=f)
    # --- Las piernas de serpiente: salen del faldellín, bajan, se abren y suben con la cabeza hacia fuera.
    for lado in (-1, 1):
        pts = bezier((lado * 3.5, 4), (lado * 5, 22), (lado * 22, 31), (lado * 22.5, 13), 60)
        cinta(d, [P(*p) for p in pts], 6.8 * u, 4.4 * u, c)
        # escamas: cortes cortos de través
        for i in range(8, 56, 6):
            (ax, ay), (bx, by) = pts[i - 1], pts[i + 1]
            l = math.hypot(bx - ax, by - ay) or 1; nx, ny = -(by - ay) / l, (bx - ax) / l
            w = 6.8 - 2.4 * i / 60
            mx, my = pts[i]
            linea([(mx - nx * w * 0.28 - (bx - ax) / l * 0.6, my - ny * w * 0.28 - (by - ay) / l * 0.6),
                   (mx + (bx - ax) / l * 0.5, my + (by - ay) / l * 0.5),
                   (mx + nx * w * 0.28 - (bx - ax) / l * 0.6, my + ny * w * 0.28 - (by - ay) / l * 0.6)], 0.55, hueco)
        # la cabeza de la serpiente, levantada y mirando hacia fuera, con la boca abierta
        hx, hy = pts[-1]
        cab = [(hx - lado * 2.6, hy + 1.0), (hx - lado * 2.8, hy - 2.6), (hx - lado * 0.6, hy - 5.2), (hx + lado * 2.6, hy - 5.4),
               (hx + lado * 6.4, hy - 4.2), (hx + lado * 4.0, hy - 2.8), (hx + lado * 6.0, hy - 1.4), (hx + lado * 2.0, hy + 0.2)]
        d.polygon([P(*p) for p in cab], fill=c)
        circ(hx + lado * 1.4, hy - 3.4, 0.75, hueco)
        linea([(hx + lado * 6.8, hy - 2.8), (hx + lado * 8.2, hy - 3.4)], 0.5)            # la lengua
        linea([(hx + lado * 8.2, hy - 3.4), (hx + lado * 8.9, hy - 4.4)], 0.4)
        linea([(hx + lado * 8.2, hy - 3.4), (hx + lado * 9.1, hy - 2.7)], 0.4)
    # --- El faldellín de tiras (pteryges) y el torso con la coraza musculada.
    d.polygon([P(-8.5, -5), P(8.5, -5), P(10.5, 6), P(-10.5, 6)], fill=c)
    for k in range(-3, 4):
        linea([(k * 2.7, -3.6), (k * 3.1, 5.2)], 0.55, hueco)
    d.polygon([P(-12.5, -27), P(12.5, -27), P(10.5, -16), P(7.5, -5), P(-7.5, -5), P(-10.5, -16)], fill=c)
    linea([(-7.5, -6.4), (7.5, -6.4)], 0.7, hueco)                 # el cinturón
    d.arc([*P(-8.2, -26), *P(0.6, -16)], 20, 160, fill=hueco, width=max(1, int(0.6 * u)))    # pectorales
    d.arc([*P(-0.6, -26), *P(8.2, -16)], 20, 160, fill=hueco, width=max(1, int(0.6 * u)))
    for lado in (-1, 1):                                             # el vientre de la coraza: dos curvas cortas
        d.arc([*P(lado * 3.0 - 3.6, -14.5), *P(lado * 3.0 + 3.6, -9.5)], 30, 150, fill=hueco, width=max(1, int(0.45 * u)))
    circ(0, -8.8, 0.6, hueco)
    # --- El brazo de fuera, levantado, con el látigo (un mango y tres correas que caen hacia fuera).
    linea([(-11, -25), (-19, -30), (-18.5, -40)], 4.4)
    circ(-18.5, -41.5, 2.8)
    linea([(-18.2, -43), (-16.4, -52)], 1.5)
    for k, (c1, c2, fin) in enumerate((((-19.5, -57), (-25.5, -55), (-28, -47)), ((-20.5, -55), (-27, -50), (-29, -39.5)),
                                        ((-20.5, -53), (-25.5, -45.5), (-26.5, -32)))):
        linea(bezier((-16.4, -52), c1, c2, fin, 30), 1.05 - k * 0.12)
    # --- El brazo del escudo y el escudo redondo, con su borde, su anillo y el umbo.
    linea([(11, -25), (16, -21), (18, -17)], 4.4)
    circ(19.5, -17, 10.6)
    d.ellipse([*P(19.5 - 8.7, -17 - 8.7), *P(19.5 + 8.7, -17 + 8.7)], outline=hueco, width=max(1, int(0.8 * u)))
    d.ellipse([*P(19.5 - 5.1, -17 - 5.1), *P(19.5 + 5.1, -17 + 5.1)], outline=hueco, width=max(1, int(0.6 * u)))
    circ(19.5, -17, 1.9, hueco); circ(19.5, -17, 1.05)
    # --- El cuello y la cabeza de gallo, de perfil, mirando a la derecha: cresta, pico, barba y ojo.
    d.polygon([P(-4.2, -26), P(4.4, -26), P(4.0, -33), P(-3.0, -34)], fill=c)
    circ(0.8, -37.2, 5.6)
    for (cx, cy, r) in ((4.0, -42.6, 1.9), (1.6, -44.2, 2.2), (-1.2, -44.0, 2.1), (-3.6, -42.2, 1.8)):
        circ(cx, cy, r)
    d.polygon([P(4.6, -41.6), P(-4.6, -41.4), P(-2.6, -38.4), P(3.8, -38.4)], fill=c)
    d.polygon([P(5.2, -39.6), P(11.4, -37.0), P(5.4, -35.2)], fill=c)              # pico
    d.polygon([P(4.4, -35.4), P(7.0, -35.0), P(6.6, -31.0), P(5.0, -30.4), P(4.0, -32.6)], fill=c)   # barba
    circ(2.6, -38.4, 1.15, hueco)
    for k in range(4):                                                               # plumas del cuello, en escamas
        for j in range(2):
            cx, cy = -2.6 + k * 2.1 + j * 1.0, -31.6 + j * 2.6
            d.arc([*P(cx - 1.1, cy - 1.4), *P(cx + 1.1, cy + 1.0)], 20, 160, fill=hueco, width=max(1, int(0.42 * u)))


def estrella(d, x, y, r, c, puntas=8, agudo=0.36, giro=0.0):
    pts = []
    for k in range(puntas * 2):
        a = giro - math.pi / 2 + k * math.pi / puntas; rr = r if k % 2 == 0 else r * agudo
        pts.append((x + rr * math.cos(a), y + rr * math.sin(a)))
    d.polygon(pts, fill=c)


def arcontes(tam):
    """Arcontes: una gema de heliotropo (el jaspe verde oscuro con motas rojas de las gemas de Abraxas), pulida en cabujón y
    montada en un bisel de oro granulado. Grabados en hueco, como en esas gemas: el ouroboros alrededor (la serpiente que se
    muerde la cola: el borde del mundo), Abraxas a la izquierda (cabeza de gallo, coraza, piernas de serpiente, látigo y
    escudo), la luna creciente sobre él y, a la derecha, las siete estrellas de las siete esferas. Abajo a la derecha, una
    grieta finísima en la piedra deja pasar la luz blanca y violeta (la grieta del tema). La huella es un grabado más: el
    corte mate y claro en la piedra, con la luz de arriba a la izquierda."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    c = tam / 2
    rr = np.sqrt((xx - c) ** 2 + (yy - c) ** 2) / c
    # --- La piedra: nubes verdes, vetas finas, motas rojas y el abombado del cabujón (luz arriba a la izquierda).
    nubes = nube(501, 4, tam) * 0.8 + nube(502, 10, tam) * 0.45 + nube(503, 26, tam) * 0.22 + nube(504, 160, tam) * 0.12
    v = nube(505, 6, tam) + nube(506, 17, tam) * 0.3
    vetas = np.exp(-(v / 0.02) ** 2) * np.clip(nube(507, 5, tam) * 2, 0, 1)
    roja = np.clip((nube(508, 110, tam) + nube(509, 240, tam) * 0.5 - 0.78) * 4.0, 0, 1) * np.clip(nube(510, 4, tam) * 2 + 0.2, 0, 1)
    piedra = col('#2B4636')[None, None, :] + nubes[..., None] * np.array([9, 14, 11], np.float32)
    piedra = piedra + vetas[..., None] * np.array([7, 9, 8], np.float32)
    piedra = mezcla(piedra, col('#7E3428'), roja * 0.6)
    luz = np.clip(1 - np.sqrt((xx - tam * 0.34) ** 2 + (yy - tam * 0.3) ** 2) / (tam * 0.6), 0, 1) ** 2
    piedra = piedra + luz[..., None] * np.array([18, 24, 20], np.float32) - (rr ** 3)[..., None] * np.array([16, 22, 18], np.float32)
    fondo = a_img(piedra).convert('RGBA')
    # --- Los grabados (todo lo que no es la huella), al triple y reducidos.
    k = 3; S = tam * k
    g = Image.new('L', (S, S), 0); d = ImageDraw.Draw(g)
    ouroboros(d, S / 2, S / 2, 0.868 * c * k, 25 * k, 7 * k, 255, hueco=0)
    abraxas(d, 119 * k, 368 * k, 1.65 * k, 255, 0)
    # La luna creciente sobre Abraxas y las siete estrellas a la derecha, en arco.
    lx, ly, lr = 178 * k, 196 * k, 21 * k
    d.ellipse([lx - lr, ly - lr, lx + lr, ly + lr], fill=255)
    d.ellipse([lx - lr * 0.62, ly - lr * 1.08, lx + lr * 1.28, ly + lr * 0.72], fill=0)
    for i in range(7):
        a = math.radians(-36 + i * 12.3)
        estrella(d, (c + 252 * math.cos(a)) * k, (c + 252 * math.sin(a)) * k, 12.5 * k, 255, 8, 0.34, giro=i * 0.21)
    grab = np.asarray(g.resize((tam, tam), Image.LANCZOS), np.float32) / 255
    # La grieta: una raja finísima en la piedra, abajo a la derecha, por donde se filtra la luz blanca y violeta del Pleroma.
    rg = np.random.default_rng(514)
    a0 = math.radians(57); x, y = c + 306 * math.cos(a0), c + 306 * math.sin(a0); pts = [(x, y)]
    rumbo = a0 + math.pi + 0.25
    for i in range(10):
        rumbo += (rg.random() - 0.5) * 1.1; l = 5 + rg.random() * 4
        x += l * math.cos(rumbo); y += l * math.sin(rumbo); pts.append((x, y))
    halo = Image.new('RGBA', (S, S), (0, 0, 0, 0)); dh = ImageDraw.Draw(halo, 'RGBA')
    dh.line([(px * k, py * k) for px, py in pts], fill=(156, 123, 255, 230), width=int(8 * k), joint='curve')
    fondo.alpha_composite(halo.filter(ImageFilter.GaussianBlur(4.5 * k)).resize((tam, tam), Image.LANCZOS))
    fondo.alpha_composite(halo.filter(ImageFilter.GaussianBlur(10 * k)).resize((tam, tam), Image.LANCZOS))
    nucleo = Image.new('RGBA', (S, S), (0, 0, 0, 0)); dn = ImageDraw.Draw(nucleo, 'RGBA')
    for i in range(len(pts) - 1):                      # se afina hacia la punta
        w = max(1, int((1.9 - 1.4 * i / len(pts)) * k))
        dn.line([(pts[i][0] * k, pts[i][1] * k), (pts[i + 1][0] * k, pts[i + 1][1] * k)], fill=(244, 241, 255, 255), width=w)
    fondo.alpha_composite(nucleo.filter(ImageFilter.GaussianBlur(0.6 * k)).resize((tam, tam), Image.LANCZOS))
    fondo.alpha_composite(tallado(grab, '#D9D6BE', 511, suave=1.9))
    # --- El bisel de oro: una banda redonda (luz arriba a la izquierda), con su granulado por dentro y la sombra en la piedra.
    r_in, r_out = 0.952, 1.0
    t_ = np.clip((rr - r_in) / (r_out - r_in), 0, 1)
    dentro = (rr >= r_in).astype(np.float32)
    dentro = np.asarray(Image.fromarray((dentro * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.7)), np.float32) / 255
    lado = -((xx - c) + (yy - c)) / (rr * c + 1e-3) / math.sqrt(2)          # 1 arriba a la izquierda
    # La banda es redonda: la cara de dentro mira al centro y la de fuera hacia afuera; brilla la que mira a la luz.
    normal = np.cos(t_ * math.pi)                                           # 1 en el borde de dentro, -1 en el de fuera
    brillo = np.clip(-lado * normal, -1, 1)                                 # la cara de dentro de abajo a la derecha mira a la luz
    oro = mezcla(col('#6E4E0E'), col('#C99A2E'), np.clip(0.5 + 0.45 * brillo, 0, 1))
    oro = mezcla(oro, col('#FFF0B8'), np.clip(brillo - 0.55, 0, 1) ** 1.5 * 1.6)
    oro = oro * (0.95 + 0.07 * ruido(tam, 0.8, 512)[..., None])
    arr = np.asarray(fondo.convert('RGB'), np.float32)
    sombra = np.clip(1 - (r_in - rr) / 0.03, 0, 1) * (rr < r_in) * (0.45 + 0.35 * np.clip(lado, 0, 1))
    arr = mezcla(arr, col('#06100A'), sombra * 0.8)
    arr = mezcla(arr, oro, dentro)
    fondo = a_img(arr).convert('RGBA')
    gr = Image.new('RGBA', (S, S), (0, 0, 0, 0)); dg = ImageDraw.Draw(gr, 'RGBA')
    n = 132; rb = (r_in - 0.004) * c * k; q = 3.6 * k
    for i in range(n):
        a = 2 * math.pi * i / n; x, y = c * k + rb * math.cos(a), c * k + rb * math.sin(a)
        dg.ellipse([x - q * 1.1 + k, y - q * 1.1 + k, x + q * 1.1 + k, y + q * 1.1 + k], fill=(20, 16, 6, 140))
        dg.ellipse([x - q, y - q, x + q, y + q], fill=(110, 78, 16, 255))
        dg.ellipse([x - q * 0.82, y - q * 0.82, x + q * 0.62, y + q * 0.62], fill=(196, 150, 48, 255))
        dg.ellipse([x - q * 0.6, y - q * 0.6, x - q * 0.08, y - q * 0.08], fill=(255, 238, 176, 230))
    fondo.alpha_composite(gr.resize((tam, tam), Image.LANCZOS))
    # --- La huella, grabada como lo demás.
    guardar(fondo, tallado(m, '#D9D6BE', 513, suave=1.9), 'arcontes')


# ------------------------------------------------------------------ Galia
def caballo(d, x0, y0, u, c, hueco, giro=0.0):
    """El caballo de los estáteros galos, al galope hacia la izquierda: el cuerpo liso como una almendra, el cuello alto y
    arqueado con la crin de perlas, la cabeza pequeña con el ojo grande, el hombro y la grupa como dos glóbulos, las patas
    finas con la rodilla en bolita y el casco en media luna, y la cola que se abre en hebras con su perla al final."""
    cg, sg = math.cos(giro), math.sin(giro)
    def P(x, y): return (x0 + (x * cg - y * sg) * u, y0 + (x * sg + y * cg) * u)
    def circ(x, y, r, f=c):
        X, Y = P(x, y); d.ellipse([X - r * u, Y - r * u, X + r * u, Y + r * u], fill=f)
    def trazo(pts, w0, w1, f=c, bolas=True):
        q = [P(*p) for p in pts]
        cinta(d, q, w0 * u, w1 * u, f)
        if bolas:
            for (X, Y), w in ((q[0], w0), (q[-1], w1)):
                r = w * u / 2; d.ellipse([X - r, Y - r, X + r, Y + r], fill=f)
    # El cuerpo: lomo, grupa, nalga, vientre y pecho.
    cuerpo = (bezier((-30, -4), (-26, -16), (-14, -15), (2, -12))[:-1] + bezier((2, -12), (14, -10), (22, -17), (32, -13))[:-1]
              + bezier((32, -13), (40, -10), (41, 4), (33, 9))[:-1] + bezier((33, 9), (24, 15), (8, 15), (-6, 14))[:-1]
              + bezier((-6, 14), (-18, 14), (-28, 12), (-33, 4))[:-1] + bezier((-33, 4), (-34, 0), (-32, -2), (-30, -4)))
    d.polygon([P(*p) for p in cuerpo], fill=c)
    # El cuello, ancho en la cruz, alto y arqueado hacia la cabeza.
    cuello = (bezier((-12, -14), (-16, -30), (-26, -42), (-38, -45))[:-1] + bezier((-38, -45), (-43, -44), (-47, -38), (-47, -30))[:-1]
              + bezier((-47, -30), (-44, -18), (-38, -8), (-33, 3)))
    d.polygon([P(*p) for p in cuello], fill=c)
    # La cabeza, inclinada: la frente, el hueso de la nariz que baja, el belfo romo y la quijada.
    cabeza = (bezier((-37, -46), (-45, -48), (-52, -42), (-56, -34))[:-1] + bezier((-56, -34), (-59, -28), (-63, -24), (-61, -19))[:-1]
              + bezier((-61, -19), (-59, -15), (-53, -16), (-50, -19))[:-1] + bezier((-50, -19), (-46, -22), (-44, -26), (-44, -32)))
    d.polygon([P(*p) for p in cabeza], fill=c)
    d.polygon([P(-41, -43), P(-38.5, -50), P(-36.5, -54), P(-34.5, -50), P(-33, -44)], fill=c)        # oreja
    # El ojo grande, en anillo, y la boca.
    X, Y = P(-49, -35); r = 3.8 * u; d.ellipse([X - r, Y - r, X + r, Y + r], fill=hueco)
    circ(-49, -35, 2.3)
    d.line([P(-62, -20), P(-56, -20)], fill=hueco, width=max(1, int(0.9 * u)))
    # La crin: perlas a lo largo del cuello, por fuera.
    for i, (x, y) in enumerate(bezier((-14, -19), (-17, -32), (-25, -42), (-34, -47), 6)):
        circ(x - 1.8, y - 2.0, 3.0 - i * 0.2)
    # El hombro y la grupa, glóbulos marcados con un surco en media luna (por detrás y por debajo).
    for gx, gy, gr, a0, a1 in ((-21, 1, 8.0, -20, 200), (22, -1, 9.6, -40, 160)):
        X, Y = P(gx, gy); r = gr * u
        d.arc([X - r, Y - r, X + r, Y + r], a0 + math.degrees(giro), a1 + math.degrees(giro), fill=hueco, width=max(1, int(1.1 * u)))
    # Las patas: las de delante estiradas hacia delante, las de atrás hacia atrás; rodillas en bolita y cascos.
    patas = [
        ([(-28, 8), (-38, 13), (-46, 18)], [(-46, 18), (-54, 16), (-60, 11)], (-61, 10)),   # mano de este lado, estirada
        ([(-20, 11), (-24, 20), (-30, 26)], [(-30, 26), (-26, 32), (-22, 37)], (-21, 38)),  # mano de allá, doblada
        ([(28, 6), (36, 14), (40, 21)], [(40, 21), (48, 27), (56, 31)], (57, 31)),            # pata de este lado, atrás
        ([(17, 12), (21, 21), (20, 27)], [(20, 27), (15, 32), (11, 38)], (10, 39)),           # pata de allá, recogida
    ]
    for arriba, abajo, casco in patas:
        trazo(arriba, 6.5, 4.2, bolas=False)
        trazo(abajo, 3.6, 2.8, bolas=False)
        circ(*arriba[-1], 3.4)                                                # la rodilla
        X, Y = P(*casco); r = 3.0 * u
        d.ellipse([X - r, Y - r, X + r, Y + r], fill=c)                       # el casco
    # La cola: tres hebras que salen de la grupa, suben y se abren hacia atrás, cada una con su perla.
    for k, (c1, c2, fin) in enumerate((((42, -25), (52, -30), (57, -23)), ((46, -19), (55, -19), (58, -11)), ((43, -11), (50, -5), (53, 1)))):
        pts = bezier((35, -11), c1, c2, fin, 20)
        trazo(pts, 3.4 - k * 0.3, 1.8, bolas=False)
        circ(*fin, 2.4)


def rueda(d, x0, y0, u, r, c, hueco, radios=4, giro=0.3):
    """La rueda (la «rouelle»): llanta, cubo y radios. Bajo el caballo de las monedas galas suele leerse como lo que quedó
    del carro de dos caballos del estátero de Filipo, que copiaban."""
    def P(a, rr): return (x0 + rr * u * math.cos(a), y0 + rr * u * math.sin(a))
    X, Y = x0, y0
    d.ellipse([X - r * u, Y - r * u, X + r * u, Y + r * u], fill=c)
    ri = r * 0.68
    d.ellipse([X - ri * u, Y - ri * u, X + ri * u, Y + ri * u], fill=hueco)
    for k in range(radios):
        a = giro + k * math.pi / radios * 2
        d.line([P(a, 0), P(a, ri + 0.5)], fill=c, width=int(r * 0.24 * u))
    rc = r * 0.3; d.ellipse([X - rc * u, Y - rc * u, X + rc * u, Y + rc * u], fill=c)
    rh = r * 0.1; d.ellipse([X - rh * u, Y - rh * u, X + rh * u, Y + rh * u], fill=hueco)


def lira(d, x0, y0, u, c, hueco, giro=0.0):
    """La lira de los bardos (Diodoro 5.31): la caja redonda abajo, los dos brazos que se abren y vuelven a cerrarse con un
    botón en la punta, el travesaño arriba y cuatro cuerdas. Ponerla en la moneda es recurso de dibujo (que haya liras en
    monedas galas está sin comprobar)."""
    cg, sg = math.cos(giro), math.sin(giro)
    def P(x, y): return (x0 + (x * cg - y * sg) * u, y0 + (x * sg + y * cg) * u)
    def circ(x, y, r, f=c):
        X, Y = P(x, y); d.ellipse([X - r * u, Y - r * u, X + r * u, Y + r * u], fill=f)
    caja = bezier((-17, 14), (-19, 34), (19, 34), (17, 14), 30) + bezier((17, 14), (10, 10), (-10, 10), (-17, 14), 20)
    d.polygon([P(*p) for p in caja], fill=c)
    d.arc([*P(-11, 15), *P(11, 29)], 20, 160, fill=hueco, width=max(1, int(1.0 * u)))
    for lado in (-1, 1):
        pts = bezier((lado * 13, 16), (lado * 30, 2), (lado * 26, -30), (lado * 12, -40), 40)
        cinta(d, [P(*p) for p in pts], 7.5 * u, 4.2 * u, c)
        circ(lado * 12.5, -41.5, 3.6)
    d.line([P(-20, -31), P(20, -31)], fill=c, width=int(4.2 * u))
    for lado in (-1, 1): circ(lado * 21, -31, 2.6)
    for k in range(4):
        x = -7.5 + k * 5
        d.line([P(x, -29), P(x * 0.85, 13)], fill=c, width=max(1, int(1.5 * u)))


def esse(d, x0, y0, u, w, c, giro=0.0):
    """La «S» tumbada de las monedas galas: un trazo en S que engruesa en el medio, con una perla en cada punta."""
    cg, sg = math.cos(giro), math.sin(giro)
    def P(x, y): return (x0 + (x * cg - y * sg) * u, y0 + (x * sg + y * cg) * u)
    pts = bezier((-16, 4), (-14, -12), (2, -8), (0, 0), 20)[:-1] + bezier((0, 0), (-2, 8), (14, 12), (16, -4), 20)
    q = [P(*p) for p in pts]
    n = len(q)
    izq, der = [], []
    for i, (x, y) in enumerate(q):
        a = q[max(i - 1, 0)]; b = q[min(i + 1, n - 1)]
        dx, dy = b[0] - a[0], b[1] - a[1]; l = math.hypot(dx, dy) or 1
        ww = w * u * (0.55 + 0.45 * math.sin(math.pi * i / (n - 1))) / 2
        izq.append((x - dy / l * ww, y + dx / l * ww)); der.append((x + dy / l * ww, y - dx / l * ww))
    d.polygon(izq + der[::-1], fill=c)
    for (x, y) in (q[0], q[-1]):
        r = w * u * 0.62; d.ellipse([x - r, y - r, x + r, y + r], fill=c)


def astro(d, x0, y0, u, c, n=6, r=7.0, rp=2.3):
    """El «astro» de las monedas: una perla en el centro y un corro de perlas alrededor."""
    d.ellipse([x0 - rp * 1.25 * u, y0 - rp * 1.25 * u, x0 + rp * 1.25 * u, y0 + rp * 1.25 * u], fill=c)
    for k in range(n):
        a = k * 2 * math.pi / n - math.pi / 2
        x, y = x0 + r * u * math.cos(a), y0 + r * u * math.sin(a)
        d.ellipse([x - rp * u, y - rp * u, x + rp * u, y + rp * u], fill=c)


# El electro de las monedas de Vercingétorix (oro con plata): más pálido y frío que el oro de los torques.
ELECTRO = [(0.0, '#22150A'), (0.22, '#553810'), (0.42, '#8E671E'), (0.57, '#B88E34'), (0.7, '#D2AC4E'), (0.83, '#E7CB7A'),
           (0.93, '#F6E6AE'), (1.0, '#FFF8DE')]


def galia(tam):
    """Galia: un estátero arverno de electro (oro pálido, con plata: las monedas de Vercingétorix tienen «environ 40 %
    d'or») sobre un cuero curtido. El cospel, batido a mano, no es del todo redondo y tiene el canto redondeado; la gráfila
    de perlas corre por dentro. En el campo, en relieve, como en el reverso de los estáteros galos: a la izquierda el
    caballo al galope, con la crin de perlas, la perla sobre el lomo y debajo la rueda (los estáteros galos copian el de
    Filipo de Macedonia, con su carro de dos caballos, que se fue volviendo un solo caballo); a la derecha la lira de los
    bardos (Diodoro 5.31; en la moneda es recurso), la «S» tumbada y el astro de perlas. En los huecos, la pátina rojiza
    del oro; en lo alto, el brillo del roce. La huella va acuñada en el centro, en el mismo metal, con su sombra corta (la
    luz viene de arriba a la izquierda)."""
    k = 2; S = tam * k; c = S / 2
    yy, xx = np.mgrid[0:S, 0:S].astype(np.float32)
    dx, dy = xx - c, yy - c
    rr = np.sqrt(dx ** 2 + dy ** 2) / c
    ang = np.arctan2(dy, dx)
    # --- El cospel: el contorno irregular y el canto redondeado; un leve abombado.
    rf = 0.95 + 0.011 * np.sin(3 * ang + 0.7) + 0.007 * np.sin(7 * ang + 2.1) + 0.004 * np.sin(13 * ang + 0.3)
    dentro_px = (rf - rr) * c
    moneda = np.clip(dentro_px / (1.0 * k) + 0.5, 0, 1)
    # El reverso de los estáteros galos es cóncavo: el campo baja hacia el centro como un cuenco poco hondo.
    alto_cospel = np.clip(dentro_px / (19 * k), 0, 1) ** 0.5 - 1.4 * np.clip(1 - (rr / 0.93) ** 2, 0, 1) ** 1.5
    # --- Lo que va en relieve (sin la huella), dibujado a la escala del lienzo.
    g = Image.new('L', (S, S), 0); d = ImageDraw.Draw(g)
    rg = np.random.default_rng(611)
    caballo(d, 111 * k, 291 * k, 1.3 * k, 255, 0, giro=-0.13)
    rueda(d, 140 * k, 466 * k, 1.0 * k, 21, 255, 0)
    d.ellipse([(130 - 6) * k, (204 - 6) * k, (130 + 6) * k, (204 + 6) * k], fill=255)        # la perla sobre el lomo
    lira(d, 614 * k, 352 * k, 1.55 * k, 255, 0)
    esse(d, 598 * k, 204 * k, 1.5 * k, 5.0, 255, giro=-0.35)
    astro(d, 596 * k, 512 * k, 1.5 * k, 255)
    figuras = np.asarray(g, np.float32) / 255
    cerca = suavizar(figuras, 4 * k) > 0.04
    n = 62
    for i in range(n):                                       # la gráfila: perlas desparejas, alguna mal acuñada; donde
        a = 2 * math.pi * i / n + 0.02                       # pasa una figura, la figura se come la perla
        rb = 0.882 * c + rg.normal(0, 0.6 * k); rp = (0.0185 + rg.normal(0, 0.0012)) * c
        x, y = c + rb * math.cos(a), c + rb * math.sin(a)
        if cerca[min(S - 1, int(y)), min(S - 1, int(x))]: continue
        d.ellipse([x - rp, y - rp, x + rp, y + rp], fill=int(255 * (0.55 if rg.random() < 0.12 else 1.0)))
    rel = np.asarray(g, np.float32) / 255
    rel = rel * moneda
    alto_rel = (suavizar(rel, 1.6 * k) * 0.3 + suavizar(rel, 3.2 * k) * 0.5 + suavizar(rel, 9 * k) * 0.35) * (0.3 + 0.7 * rel)
    # --- El campo: rayas finas de uso, golpecitos y un grano muy fino.
    marcas = Image.new('L', (S, S), 0); dm = ImageDraw.Draw(marcas)
    for _ in range(70):
        x, y = rg.random() * S, rg.random() * S; a = rg.random() * math.pi; l = (6 + rg.random() * 40) * k
        dm.line([(x, y), (x + l * math.cos(a), y + l * math.sin(a))], fill=int(60 + rg.random() * 120), width=1)
    for _ in range(40):
        x, y = rg.random() * S, rg.random() * S; r_ = (1 + rg.random() * 3) * k
        dm.ellipse([x - r_, y - r_, x + r_, y + r_], fill=int(80 + rg.random() * 120))
    golpes = suavizar(np.asarray(marcas, np.float32) / 255, 0.7 * k)
    # Una grieta del cospel en el canto, abajo a la derecha, como las que abre el golpe del cuño en el metal frío.
    grieta = Image.new('L', (S, S), 0); dg = ImageDraw.Draw(grieta)
    a0 = math.radians(38); x, y = c + 0.97 * c * math.cos(a0), c + 0.97 * c * math.sin(a0); pts = [(x, y)]
    rumbo = a0 + math.pi
    for i in range(8):
        rumbo += rg.normal(0, 0.18); l = (5 + rg.random() * 4) * k
        x += l * math.cos(rumbo); y += l * math.sin(rumbo); pts.append((x, y))
    for i in range(len(pts) - 1):
        dg.line([pts[i], pts[i + 1]], fill=255, width=max(1, int((4.6 - 3.8 * i / len(pts)) * k)))
    grieta = suavizar(np.asarray(grieta, np.float32) / 255, 0.5 * k)
    moneda = moneda * (1 - np.clip(grieta * 1.6, 0, 1) * np.clip(1 - dentro_px / (9 * k), 0, 1))     # la boca de la grieta en el canto
    alto = alto_cospel * 0.9 + alto_rel * 0.8 - golpes * 0.025 + suavizar(rg.standard_normal((S, S)).astype(np.float32), 1.2 * k) * 0.006
    alto = (alto - grieta * 0.35) * np.clip(moneda * 1.2, 0, 1)
    color = oro_relieve(suavizar(alto, 0.5 * k), 24.0 * k, 612, tonos=ELECTRO, grano=0.02, ambiente=0.75, plano=0.34)
    # El roce: lo más alto del relieve, pulido por las manos, más claro.
    color = mezcla(color, col('#F0D684'), np.clip(alto_rel * 1.6 - 0.45, 0, 1) * 0.3)
    # El campo no es parejo: tonos más cálidos y más fríos en manchas grandes, y la luz que cae hacia abajo a la derecha.
    color = color * (0.94 + 0.1 * nube(616, 3, S)[..., None] + 0.04 * nube(617, 9, S)[..., None])
    color = color * (1.06 - 0.16 * np.clip((dx + dy) / (2 * c) + 0.5, 0, 1))[..., None]
    # La pátina: rojiza en los huecos junto al relieve y por dentro del canto; el campo, un poco más mate que lo alto.
    hueco = np.clip(suavizar(rel, 5 * k) * 1.5 - rel * 1.2, 0, 1)
    color = mezcla(color, col('#6B3A16'), hueco * 0.42)
    color = mezcla(color, col('#6B3A16'), np.clip(1 - dentro_px / (34 * k), 0, 1) * np.clip(dentro_px / (6 * k), 0, 1) * 0.18)
    color = color * (0.93 + 0.07 * np.clip(alto_rel * 2, 0, 1))[..., None]
    color = mezcla(color, col('#2E1C08'), np.clip(grieta * 1.6, 0, 1) * 0.85)
    # El campo, con la pátina de siglos: más pardo y apagado que lo alto, con manchas rojizas.
    campo = 1 - np.clip(alto_rel * 3, 0, 1)
    color = mezcla(color, col('#6E4A16'), campo * 0.22)
    color = mezcla(color, col('#8C4A22'), np.clip(nube(618, 5, S) * 1.6 - 0.2, 0, 1) * 0.18 * campo)
    # --- El cuero de fuera: pardo oscuro, con su flor (el grano) y la sombra de la moneda.
    cuero = col('#2B1D13')[None, None, :] * (0.9 + 0.12 * nube(613, 6, S)[..., None] + 0.06 * nube(614, 40, S)[..., None])
    flor = suavizar(rg.standard_normal((S, S)).astype(np.float32), 1.6 * k)
    cuero = cuero * (1 + 0.5 * np.clip(flor, -0.2, 0.2)[..., None])
    sombra = suavizar(np.roll(np.roll(moneda, 6 * k, 0), 4 * k, 1), 7 * k)
    cuero = cuero * (1 - 0.55 * sombra)[..., None]
    fondo = mezcla(cuero, color, moneda)
    fondo = a_img(fondo).resize((tam, tam), Image.LANCZOS)
    # --- La huella, acuñada: el mismo metal, más pulida en lo alto, con la pátina y la sombra alrededor.
    m = mascara_sello(S)
    alto_h = suavizar(m, 4.6 * k) * (0.25 + 0.75 * m)
    alto_h = alto_h / max(1e-3, float(alto_h.max()))        # cada cresta, un hilo redondo y no una tira plana
    color_h = oro_relieve(suavizar(alto_cospel * 0.9 + alto_h * 0.8, 0.5 * k), 24.0 * k, 615, tonos=ELECTRO, grano=0.02, ambiente=0.75, plano=0.56)
    color_h = mezcla(color_h, col('#F0D684'), np.clip(alto_h * 1.6 - 0.6, 0, 1) * 0.3)
    color_h = color_h * (0.94 + 0.1 * nube(616, 3, S)[..., None] + 0.04 * nube(617, 9, S)[..., None])
    color_h = color_h * (1.06 - 0.16 * np.clip((dx + dy) / (2 * c) + 0.5, 0, 1))[..., None]
    capa = a_rgba(color_h, m)
    halo = np.clip(suavizar(m, 4 * k) * 1.5 - m, 0, 1)
    sombra_h = np.clip(suavizar(np.roll(np.roll(m, 3 * k, 0), 2 * k, 1), 2.4 * k) - m, 0, 1)
    base = a_rgba(np.zeros((S, S, 3)) + col('#4A2A0E'), np.clip(halo * 0.22 + sombra_h * 0.5, 0, 1))
    base.alpha_composite(capa)
    huella = base.convert('RGBa').resize((tam, tam), Image.LANCZOS).convert('RGBA')
    guardar(fondo, huella, 'galia')


# ------------------------------------------------------------------ Art nouveau
def _rot(x0, y0, u, giro):
    cg, sg = math.cos(giro), math.sin(giro)
    return lambda x, y: (x0 + (x * cg - y * sg) * u, y0 + (x * sg + y * cg) * u)


def _cerrar(d, pts, w, c):
    """El contorno cerrado de [pts] como un hilo de oro de ancho [w], con las uniones redondas."""
    d.line(list(pts) + [pts[0], pts[1]], fill=c, width=max(1, int(w)), joint='curve')


def ala(P, base, ang, largo, ancho, frente, n=48):
    """El contorno de un ala de libélula, en coordenadas locales: nace en [base] hacia [ang]; el borde de delante (el
    lado [frente], +1 o −1) casi recto y el de atrás curvo y más ancho cerca de la base; la punta redondeada. Devuelve
    (el contorno, una función que da el punto a [t] del largo y a [f] del ancho: f = 1 en el borde de delante, −1 en el
    de atrás)."""
    dx, dy = math.cos(ang), math.sin(ang); nx, ny = -dy * frente, dx * frente
    def borde_del(t): return ancho * (0.16 + 0.18 * math.sin(math.pi * t ** 0.9)) * (1 - t ** 10) ** 0.5
    def borde_atr(t): return ancho * (0.22 + 0.78 * math.sin(math.pi * min(1, t ** 0.75 * 0.98))) * 0.6 * (1 - t ** 7) ** 0.5
    def punto(t, f):
        b = borde_del(t) if f >= 0 else borde_atr(t)
        x = base[0] + dx * largo * t + nx * b * f; y = base[1] + dy * largo * t + ny * b * f
        return P(x, y)
    ts = np.linspace(0, 1, n)
    contorno = [punto(t, 1) for t in ts] + [punto(t, -1) for t in ts[::-1]]
    return contorno, punto


def libelula(dd, x0, y0, u, giro=0.0):
    """La libélula de las joyas de 1900, vista desde arriba, con la cabeza hacia −y: los dos ojos grandes, el tórax, el
    abdomen largo en diez anillos y las cuatro alas de esmalte translúcido (nácar) con su nervadura de oro y la mancha del
    pterostigma cerca de la punta. [dd] es un diccionario de lápices: 'oro' (lo que va en oro: los tabiques, las nervaduras,
    los anillos), 'ala', 'cuerpo', 'ojo' y 'mancha'. Origen en el centro del tórax; [u] px por unidad (mide unas 120 de
    largo y 140 de envergadura)."""
    P = _rot(x0, y0, u, giro)
    w_hilo = 1.5 * u
    # --- Las alas: las de delante echadas hacia delante y las de atrás hacia atrás, para que quepan junto a la huella.
    for lado in (-1, 1):
        for base, a, largo, ancho in (((lado * 3.2, -6.0), (0 if lado > 0 else math.pi) - lado * 0.95, 66, 15.5),
                                      ((lado * 3.6, 2.5), (0 if lado > 0 else math.pi) + lado * 1.0, 61, 19.0)):
            contorno, punto = ala(P, base, a, largo, ancho, -lado)       # el borde de delante mira hacia la cabeza
            dd['ala'].polygon(contorno, fill=255)
            # Las nervaduras largas: tres, que salen de la base y se abren hacia la punta.
            for f in (0.55, -0.15, -0.6):
                dd['oro'].line([punto(t, f) for t in np.linspace(0.03, 0.9 - abs(f) * 0.12, 30)], fill=255, width=max(1, int(w_hilo * 0.75)), joint='curve')
            # Las de través, en tresbolillo, que dejan las celdas del esmalte.
            for i, t in enumerate(np.arange(0.12, 0.86, 0.085)):
                tramos = ((1.0, 0.55), (-0.15, -0.6)) if i % 2 else ((0.55, -0.15), (-0.6, -1.0))
                for f0, f1 in tramos:
                    dd['oro'].line([punto(t, f0), punto(t + 0.025, f1)], fill=255, width=max(1, int(w_hilo * 0.6)))
            # El pterostigma: una celda llena cerca de la punta, en el borde de delante.
            dd['mancha'].polygon([punto(0.8, 1.0), punto(0.89, 1.0), punto(0.885, 0.45), punto(0.805, 0.45)], fill=255)
            _cerrar(dd['oro'], contorno, w_hilo * 1.15, 255)
    # --- El abdomen: diez anillos que se afinan, cada uno con su tabique de oro; al final, los dos apéndices.
    y = 9.0
    for i in range(10):
        h = 8.6 - i * 0.35; w = 6.4 - i * 0.32 if i else 5.6
        if i == 0: w = 6.8
        pts = [P(-w / 2, y), P(w / 2, y), P(w / 2 * 0.86, y + h), P(-w / 2 * 0.86, y + h)]
        seg = [P(x_ * w / 2, y + (y_ + 1) / 2 * h) for x_, y_ in
               [(math.cos(t) * (0.93 if abs(math.sin(t)) < 0.9 else 0.8), math.sin(t)) for t in np.linspace(0, 2 * math.pi, 24)[:-1]]]
        dd['cuerpo'].polygon(seg, fill=255)
        _cerrar(dd['oro'], seg, w_hilo, 255)
        y += h - 0.6
    for lado in (-1, 1):
        dd['oro'].line([P(lado * 0.8, y - 0.5), P(lado * 2.6, y + 4.5), P(lado * 2.2, y + 6.5)], fill=255, width=max(1, int(w_hilo * 1.1)), joint='curve')
    # --- El tórax: un óvalo con su tabique y las bases de las alas.
    tor = [P(6.8 * math.cos(t), -1.5 + 9.5 * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 36)[:-1]]
    dd['cuerpo'].polygon(tor, fill=255); _cerrar(dd['oro'], tor, w_hilo * 1.2, 255)
    dd['oro'].line([P(0, -9.5), P(0, 7.0)], fill=255, width=max(1, int(w_hilo * 0.8)))
    # --- La cabeza: los dos ojos grandes que casi se tocan y la cara entre ellos.
    for lado in (-1, 1):
        ojo = [P(lado * 4.6 + 5.0 * math.cos(t), -15.5 + 4.8 * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 30)[:-1]]
        dd['ojo'].polygon(ojo, fill=255); _cerrar(dd['oro'], ojo, w_hilo * 1.1, 255)
    cara = [P(3.2 * math.cos(t), -11.2 + 2.4 * math.sin(t)) for t in np.linspace(0, 2 * math.pi, 20)[:-1]]
    dd['cuerpo'].polygon(cara, fill=255); _cerrar(dd['oro'], cara, w_hilo, 255)


def petalo(P, raiz, punta, ancho, curva=0.0, n=40, panza=0.55):
    """El contorno de un pétalo (o de una hoja ancha): de [raiz] a [punta], con el ancho máximo a [panza] del largo y el
    eje curvado [curva] (fracción del largo, hacia la izquierda del avance)."""
    (x0, y0), (x1, y1) = raiz, punta
    l = math.hypot(x1 - x0, y1 - y0); dx, dy = (x1 - x0) / l, (y1 - y0) / l; nx, ny = -dy, dx
    izq, der = [], []
    for t in np.linspace(0, 1, n):
        cx = x0 + dx * l * t + nx * curva * l * math.sin(math.pi * t)
        cy = y0 + dy * l * t + ny * curva * l * math.sin(math.pi * t)
        w = ancho / 2 * math.sin(math.pi * min(1, (t / panza) * 0.5 if t < panza else 0.5 + (t - panza) / (1 - panza) * 0.5)) ** 0.7
        w = w * (1 - t ** 6) ** 0.4 + 0.3
        izq.append(P(cx + nx * w, cy + ny * w)); der.append(P(cx - nx * w, cy - ny * w))
    return izq + der[::-1]


def eje_petalo(P, raiz, punta, curva, t0=0.0, t1=1.0, n=30):
    """El eje (la nervadura del medio) del pétalo de [petalo], de [t0] a [t1] del largo."""
    (x0, y0), (x1, y1) = raiz, punta
    l = math.hypot(x1 - x0, y1 - y0); dx, dy = (x1 - x0) / l, (y1 - y0) / l; nx, ny = -dy, dx
    return [P(x0 + dx * l * t + nx * curva * l * math.sin(math.pi * t), y0 + dy * l * t + ny * curva * l * math.sin(math.pi * t))
            for t in np.linspace(t0, t1, n)]


def iris_flor(dd, x0, y0, u, giro=0.0):
    """Un iris barbado de perfil, como adorno: tres pétalos que suben y se cierran arriba (el estandarte), tres que caen
    (las caídas), cada caída con su barba amarilla, y debajo la espata verde. Lápices: 'oro', 'estandarte', 'caida',
    'barba', 'verde'. Origen donde se juntan los pétalos; mide unas 70 de ancho y 100 de alto."""
    P = _rot(x0, y0, u, giro)
    w = 1.5 * u
    # El estandarte: el de atrás, al centro, y los dos de los lados, que se curvan hacia dentro.
    for raiz, punta, ancho, curva in (((0, -2), (0.5, -46), 26, 0.0), ((-3, -1), (-16, -40), 22, -0.16), ((3, -1), (16, -40), 22, 0.16)):
        pts = petalo(P, raiz, punta, ancho, curva, panza=0.62)
        dd['estandarte'].polygon(pts, fill=255); _cerrar(dd['oro'], pts, w, 255)
    # Las caídas: la de delante, que cuelga, y las de los lados, que se abren y bajan.
    for raiz, punta, ancho, curva in (((-4, 2), (-34, 30), 22, 0.22), ((4, 2), (34, 30), 22, -0.22), ((0, 3), (1, 50), 27, 0.04)):
        pts = petalo(P, raiz, punta, ancho, curva, panza=0.6)
        dd['caida'].polygon(pts, fill=255); _cerrar(dd['oro'], pts, w, 255)
        # las venas finas de la caída, que salen de la barba, y la barba (un trazo grueso, amarillo, en la mitad de arriba)
        (xa, ya), (xb, yb) = raiz, punta
        for k in (-0.22, 0.0, 0.22):
            q0 = (xa + (xb - xa) * 0.35, ya + (yb - ya) * 0.35); q1 = (xa + (xb - xa) * 0.8 + k * (yb - ya) * 0.35, ya + (yb - ya) * 0.8 - k * (xb - xa) * 0.35)
            dd['vena'].line([P(*q0), P(*q1)], fill=255, width=max(1, int(w * 0.6)))
        dd['barba'].line([P(xa + (xb - xa) * 0.06, ya + (yb - ya) * 0.06), P(xa + (xb - xa) * 0.42, ya + (yb - ya) * 0.42)], fill=255, width=max(1, int(4.2 * u)), joint='curve')
    # La espata: dos hojitas verdes que abrazan la base.
    for raiz, punta, curva in (((-1, 30), (-8, 6), 0.1), ((1, 32), (7, 9), -0.12)):
        pts = petalo(P, raiz, punta, 7.5, curva, panza=0.4)
        dd['verde'].polygon(pts, fill=255); _cerrar(dd['oro'], pts, w, 255)


def nouveau(tam):
    """Art nouveau: un medallón de orfebre de 1900, de esmalte y oro pálido. El campo es esmalte translúcido sobre un
    guilloché de ondas (las líneas talladas en el metal se ven por debajo, como en las joyas de la época), con los colores
    del vidrio de Nancy que se funden: el violeta del iris arriba y el verde del hierro de Guimard abajo. El marco es la
    línea de látigo: un tallo de oro que nace abajo a la izquierda, da la vuelta y se cierra abajo a la derecha en un rizo;
    del mismo pie suben, a la izquierda, las hojas de espada y el iris barbado (malva, violeta, con su barba amarilla); a la
    derecha vuela la libélula entera, con las alas de nácar, la nervadura de oro y el cuerpo verde azul. Todo con tabiques
    de oro, como un esmalte. Un bisel de oro cierra el medallón. La huella va en nácar, engastada en un hilo de oro, como
    en el ícono; la luz viene de arriba a la izquierda."""
    from iconos import nacar, ORO_PALIDO, latigo_puntos, cinta_var, hoja_lanza
    k = 2; S = tam * k; c = S / 2
    yy, xx = np.mgrid[0:S, 0:S].astype(np.float32)
    dx, dy = xx - c, yy - c
    rr = np.sqrt(dx ** 2 + dy ** 2) / c
    ang = np.arctan2(dy, dx)
    rg = np.random.default_rng(701)
    # --- Los lápices: cada material en su máscara, a la escala del lienzo.
    nombres = ('oro', 'tallo', 'ala', 'cuerpo', 'ojo', 'mancha', 'estandarte', 'caida', 'barba', 'vena', 'verde', 'hoja')
    capas = {n: Image.new('L', (S, S), 0) for n in nombres}
    dd = {n: ImageDraw.Draw(capas[n]) for n in nombres}
    # El marco de látigo (el tallo grueso, de oro macizo).
    R_TALLO = 0.892 * c
    pts, avance = latigo_puntos(c, c, R_TALLO, a0=math.radians(121), a1=math.radians(408), centro_rizo=None,
                                rizo=(0.14, 0.028, 1.6), onda=0.014)
    anchos = [(6.4 + 15.0 * (1 - a) ** 1.25) * k * min(1.0, 0.3 + a / 0.03) for a in avance]
    cinta_var(dd['tallo'], pts, anchos, 255)
    x, y = pts[-1]; r_ = anchos[-1] * 0.7; dd['tallo'].ellipse([x - r_, y - r_, x + r_, y + r_], fill=255)
    # La hoja que brota en el pie del tallo.
    for donde, giro, largo, ancho, curva in ((0.0, math.radians(146), 0.2, 0.06, -0.35),):
        i = min(len(pts) - 2, int(donde * (len(pts) - 1)))
        tx, ty = pts[i + 1][0] - pts[i][0], pts[i + 1][1] - pts[i][1]
        hoja_lanza(dd['tallo'], pts[i][0], pts[i][1], math.atan2(ty, tx) + giro, largo * R_TALLO, ancho * R_TALLO, 255, curva)
    # El bisel.
    dd['oro'].ellipse([c - 0.985 * c, c - 0.985 * c, c + 0.985 * c, c + 0.985 * c], outline=255, width=int(6.5 * k))
    # Del pie del tallo suben las hojas de espada del iris y su vara.
    pie = (pts[0][0] / k + 6, pts[0][1] / k - 10)
    for (x1, y1), ancho, curva in (((60, 330), 30, 0.1), ((132, 412), 24, -0.12), ((40, 470), 20, 0.05)):
        hoja = petalo(lambda x, y: (x * k, y * k), pie, (x1, y1), ancho, curva, panza=0.3)
        dd['hoja'].polygon(hoja, fill=255); _cerrar(dd['oro'], hoja, 1.5 * k, 255)
        dd['oro'].line(eje_petalo(lambda x, y: (x * k, y * k), pie, (x1, y1), curva, 0.06, 0.82), fill=255, width=int(1.1 * k), joint='curve')
    flor = (106, 296)
    vara = bezier(pie, (pie[0] - 10, pie[1] - 120), (flor[0] + 14, flor[1] + 120), (flor[0] + 2, flor[1] + 36), 40)
    q = [(x * k, y * k) for x, y in vara]
    anchos_v = [(7.0 - 2.4 * i / 40) * k for i in range(41)]
    cinta_var(dd['verde'], q, anchos_v, 255)
    n_ = len(q); izq, der = [], []
    for i, (x, y) in enumerate(q):
        a_ = q[max(i - 1, 0)]; b_ = q[min(i + 1, n_ - 1)]
        tx, ty = b_[0] - a_[0], b_[1] - a_[1]; l = math.hypot(tx, ty) or 1
        izq.append((x - ty / l * anchos_v[i] / 2, y + tx / l * anchos_v[i] / 2)); der.append((x + ty / l * anchos_v[i] / 2, y - tx / l * anchos_v[i] / 2))
    for lado_ in (izq, der): dd['oro'].line(lado_, fill=255, width=int(1.4 * k), joint='curve')
    iris_flor(dd, flor[0] * k, flor[1] * k, 1.6 * k, giro=-0.08)
    # La libélula, a la derecha, un poco ladeada, con la cabeza hacia arriba.
    libelula(dd, 610 * k, 266 * k, 1.6 * k, giro=-0.3)
    M = {n: np.asarray(capas[n], np.float32) / 255 for n in nombres}
    # --- El campo: esmalte translúcido sobre el guilloché, con los colores del vidrio de Nancy que se funden.
    v = (dx * 0.3 + dy * 0.95) / S + 0.5 + nube(702, 3, S) * 0.12 + nube(703, 8, S) * 0.04
    tonos = [(0.0, '#58437F'), (0.3, '#46366B'), (0.5, '#3A3058'), (0.64, '#30394A'), (0.78, '#283E34'), (1.0, '#1C2F25')]
    xs = [p for p, _ in tonos]; cs = np.array([col(t) for _, t in tonos])
    campo = np.stack([np.interp(v, xs, cs[:, j]) for j in range(3)], -1)
    # El guilloché: ondas concéntricas talladas en el metal, que se ven por debajo del esmalte (más claro en la cresta).
    ondas = np.sin(2 * math.pi * (rr * c / (4.6 * k) + 0.55 * np.sin(ang * 9 + rr * 7)))
    campo = campo * (0.9 + 0.1 * (0.5 + 0.5 * ondas))[..., None]
    # El esmalte es más hondo (más oscuro) junto al bisel y deja pasar más luz en el centro; el brillo de la ventana arriba.
    campo = campo * (1.08 - 0.22 * np.clip(rr / 0.98, 0, 1) ** 3)[..., None]
    brillo = np.exp(-((np.sqrt((xx - c * 0.62) ** 2 + (yy - c * 0.5) ** 2) / c - 0.62) / 0.05) ** 2) * np.clip(-(dx + dy) / (rr * c + 1) * 1.2, 0, 1)
    campo = mezcla(campo, col('#E9E2F2'), desenfocar(brillo.astype(np.float32), 6 * k) * 0.18)
    campo = campo * (0.97 + 0.04 * suavizar(rg.standard_normal((S, S)).astype(np.float32), 0.8 * k)[..., None])

    def domo(m_, s_=2.2):
        """La luz sobre un esmalte abombado de máscara [m_]: claro arriba a la izquierda, con su brillo."""
        h = suavizar(m_, s_ * k)
        gy, gx = np.gradient(h)
        f = np.clip(-(gx + gy) * 11 * k, -1, 1)
        esp = np.clip(f - 0.45, 0, 1) ** 2
        return f, esp
    def esmalte(base, m_, color, alfa=1.0, s_=2.2, fuerza=0.22):
        f, esp = domo(m_, s_)
        tinte = color * (1 + fuerza * f)[..., None] + esp[..., None] * 90
        return mezcla(base, tinte, m_ * alfa)
    # --- Sombras de todo lo que está en relieve (la luz de arriba a la izquierda).
    alto_todo = np.clip(sum(M[n] for n in nombres), 0, 1)
    sombra = suavizar(np.roll(np.roll(alto_todo, int(4 * k), 0), int(3 * k), 1), 3.2 * k) * (1 - alto_todo)
    campo = campo * (1 - 0.5 * sombra)[..., None]
    # --- El tallo de látigo, de oro macizo y redondo: va debajo de todo lo demás (el iris y la libélula lo cruzan).
    alto_t = suavizar(M['tallo'], 2.4 * k) * 0.45 + suavizar(M['tallo'], 5.5 * k) * 0.55
    alto_t = alto_t * (0.35 + 0.65 * M['tallo'])
    oro_t = oro_relieve(suavizar(alto_t, 0.5 * k), 14.0 * k, 705, tonos=ORO_PALIDO, grano=0.02, ambiente=0.65, plano=0.55)
    img = mezcla(campo, oro_t, M['tallo'])
    # La sombra que echan sobre el tallo el iris y la libélula, que van encima.
    encima = np.clip(sum(M[n] for n in nombres if n not in ('tallo',)), 0, 1)
    sombra2 = suavizar(np.roll(np.roll(encima, int(4 * k), 0), int(3 * k), 1), 3 * k) * (1 - encima) * M['tallo']
    img = img * (1 - 0.45 * sombra2)[..., None]
    # --- Los esmaltes de colores.
    yy_n = yy / S
    hoja_c = mezcla(col('#9AB287'), col('#5C7A52'), np.clip((yy_n - 0.4) / 0.5, 0, 1))
    img = esmalte(img, M['hoja'], hoja_c)
    img = esmalte(img, M['verde'], col('#6E9064'))
    caida_c = mezcla(col('#8A6BB4'), col('#5A3E86'), np.clip(suavizar(M['caida'], 4 * k) * 1.4, 0, 1))
    img = esmalte(img, M['caida'], caida_c)
    img = mezcla(img, col('#3E2766'), M['vena'] * M['caida'] * 0.75)
    est_c = mezcla(col('#D6BEE6'), col('#B08AB8'), np.clip(suavizar(M['estandarte'], 4 * k) * 1.3, 0, 1))
    img = esmalte(img, M['estandarte'], est_c)
    barba = M['barba'] * np.clip(0.75 + 0.5 * suavizar(rg.random((S, S)).astype(np.float32), 0.6 * k), 0, 1)
    img = esmalte(img, barba, col('#E8BE52'), s_=1.2)
    # Las alas: el nácar translúcido, que deja ver un poco lo de debajo.
    perla = nacar(suavizar(M['ala'], 3 * k) * 0.6, 3.0 * k, 704)
    img = mezcla(img, perla * 0.9 + img * 0.1, M['ala'] * 0.86)
    img = esmalte(img, M['mancha'], col('#D9A14A'), s_=1.0)
    cuerpo_c = mezcla(col('#4FA0A4'), col('#235A66'), np.clip(suavizar(M['cuerpo'], 3 * k) * 1.2, 0, 1))
    img = esmalte(img, M['cuerpo'], cuerpo_c, s_=2.0, fuerza=0.3)
    img = esmalte(img, M['ojo'], col('#3E8E8A'), s_=2.6, fuerza=0.45)
    # --- Los tabiques de oro, las nervaduras y el bisel, encima de los esmaltes.
    alto_o = (suavizar(M['oro'], 0.8 * k) * 0.7 + suavizar(M['oro'], 1.8 * k) * 0.3) * (0.4 + 0.6 * M['oro'])
    oro = oro_relieve(suavizar(alto_o, 0.4 * k), 9.0 * k, 709, tonos=ORO_PALIDO, grano=0.02, ambiente=0.65, plano=0.6)
    img = mezcla(img, oro, M['oro'])
    # --- Fuera del bisel, el terciopelo del estuche (casi no se ve: el sello se recorta en círculo).
    fuera = np.clip((rr - 0.995) * c / 1.0, 0, 1)
    terciopelo = col('#1A1424')[None, None, :] * (0.9 + 0.15 * nube(706, 6, S)[..., None])
    img = mezcla(img, terciopelo, fuera)
    fondo = a_img(img).resize((tam, tam), Image.LANCZOS)
    # --- La huella: nácar engastado en un hilo de oro, en relieve, con su sombra sobre el esmalte.
    m = mascara_sello(S)
    alto_h = np.clip(suavizar(m, 2.4 * k) * 1.35, 0, 1) * (0.3 + 0.7 * m)       # el nácar abombado, pero de lomo ancho
    perla = nacar(suavizar(alto_h, 0.6 * k), 7.0 * k, 707, cuanto=0.6)
    nucleo = np.clip((suavizar(m, 1.6 * k) - 0.8) / 0.1, 0, 1) * m
    hilo = oro_relieve(suavizar(alto_h, 0.6 * k), 9.0 * k, 708, tonos=ORO_PALIDO, grano=0.0, ambiente=0.5, plano=0.6)
    color_h = hilo * (1 - nucleo[..., None]) + perla * nucleo[..., None]
    capa = a_rgba(color_h, m)
    sombra_h = np.clip(suavizar(np.roll(np.roll(m, int(4 * k), 0), int(3 * k), 1), 3 * k) - m, 0, 1)
    base = a_rgba(np.zeros((S, S, 3)) + col('#0E0A18'), np.clip(sombra_h * 0.6, 0, 1))
    base.alpha_composite(capa)
    huella = base.convert('RGBa').resize((tam, tam), Image.LANCZOS).convert('RGBA')
    guardar(fondo, huella, 'nouveau')


# ------------------------------------------------------------------ Tierras Antiguas
def runas_pil(d, x, y, ancho, alto, n, color, grosor, semilla):
    """Signos inventados (trazos rectos y ángulos), como en TrofeosMas.runas."""
    r = np.random.default_rng(semilla)
    paso = ancho / n
    for k in range(n):
        x0 = x + k * paso + paso * 0.2; ww = paso * 0.6; cx = x0 + ww / 2
        d.line([(cx, y - alto / 2), (cx, y + alto / 2)], fill=color, width=grosor)
        t = r.integers(0, 4)
        if t == 0: d.line([(cx, y - alto / 2), (x0 + ww, y - alto / 6)], fill=color, width=grosor)
        elif t == 1: d.line([(x0, y - alto / 4), (cx, y), (x0 + ww, y - alto / 4)], fill=color, width=grosor)
        elif t == 2: d.line([(x0, y + alto / 6), (x0 + ww, y - alto / 6)], fill=color, width=grosor)
        else: d.line([(x0, y + alto / 6), (cx, y - alto / 6), (x0 + ww, y + alto / 6)], fill=color, width=grosor)

def antiguas(tam):
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    # Muro de sillares de piedra gris, con grano y vetas.
    piedra = mezcla(col('#3E4046'), col('#6E6A62'), ruido(tam, tam / 20, 81))
    piedra = piedra * (0.9 + 0.14 * ruido(tam, 0.9, 82)[..., None])
    fondo = a_img(piedra).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    alto = tam / 6; largo = tam / 3.2
    for j in range(7):
        y = j * alto
        d.line([(0, y), (tam, y)], fill=(20, 20, 22, 150), width=max(2, tam // 160))
        x = (largo / 2) if j % 2 else 0
        while x < tam:
            d.line([(x, y), (x, y + alto)], fill=(20, 20, 22, 150), width=max(2, tam // 160)); x += largo
    # Runas inventadas talladas en la fila de arriba y la de abajo.
    for k, y in enumerate((alto * 0.5, tam - alto * 0.5)):
        runas_pil(d, tam * 0.08, y, tam * 0.84, alto * 0.5, 7, (210, 188, 120, 120), max(2, tam // 120), 90 + k)
    # Luz verde de gema que sale del centro.
    c = tam / 2
    dist = np.sqrt((xx - c) ** 2 + (yy - c) ** 2) / (tam / 2)
    fondo = fondo.convert('RGB')
    arr = np.array(fondo).astype(np.float32)
    arr = mezcla(arr, col('#2E5A3E'), np.clip(0.9 - dist, 0, 1) * 0.55)
    arr = mezcla(arr, col('#8CC79A'), np.clip(0.45 - dist, 0, 1) * 0.6)
    fondo = a_img(arr)
    # Huella en oro viejo con relieve y una pátina verde en los surcos.
    sh = relieve(m, fuerza=1.3)[..., None]
    oro = mezcla(col('#8A6A26'), col('#E8C878'), ruido(tam, tam / 28, 83))
    oro = mezcla(oro, col('#5E7A5A'), np.clip((ruido(tam, tam / 60, 84) - 0.65) * 3, 0, 1) * 0.5)
    rgb = oro * (1 + sh * 0.5)
    capa = a_rgba(rgb, m)
    sombra = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 120))).astype(np.float32) / 255
    base = a_rgba(np.zeros((tam, tam, 3)) + col('#0E0F10'), np.roll(np.roll(sombra, 3, 0), 3, 1) * 0.6)
    base.alpha_composite(capa)
    guardar(fondo, base, 'antiguas')


# ------------------------------------------------------------------ Prehistoria
def mano_pil(d, x, y, s, ang, color):
    """Una mano (palma, cuatro dedos y pulgar) de alto 2·s, con los dedos hacia [ang], como en Decoracion.piezasMano."""
    ca, sa = math.cos(ang + math.pi / 2), math.sin(ang + math.pi / 2)
    def p(px, py): return (x + (px * ca - py * sa) * s, y + (px * sa + py * ca) * s)
    d.polygon([p(-0.44, -0.04), p(-0.36, -0.12), p(0.32, -0.12), p(0.4, -0.04), p(0.4, 0.56), p(0.26, 1.0), p(-0.3, 1.0), p(-0.44, 0.56)], fill=color)
    for fx, arriba, an in ((-0.34, -0.62, 0.17), (-0.13, -0.86, 0.19), (0.1, -0.98, 0.19), (0.3, -0.8, 0.18)):
        pts = [p(fx - an / 2, 0.02)] + [p(fx + an / 2 * math.cos(t), arriba + an / 2 - an / 2 * math.sin(t)) for t in [math.pi - k * math.pi / 8 for k in range(9)]] + [p(fx + an / 2, 0.02)]
        d.polygon(pts, fill=color)
    d.polygon([p(0.3, 0.5), p(0.36, 0.08), p(0.55, -0.12), p(0.72, -0.12), p(0.8, -0.02), p(0.66, 0.22), p(0.42, 0.6)], fill=color)

def bisonte_pil(d, x, y, r, color):
    """El bisonte de las cuevas, de perfil y mirando a la derecha (como Decoracion.bisonte), de ancho 2·r."""
    def cub(p0, p1, p2, p3, n=10):
        return [((1 - t) ** 3 * p0[0] + 3 * (1 - t) ** 2 * t * p1[0] + 3 * (1 - t) * t * t * p2[0] + t ** 3 * p3[0],
                 (1 - t) ** 3 * p0[1] + 3 * (1 - t) ** 2 * t * p1[1] + 3 * (1 - t) * t * t * p2[1] + t ** 3 * p3[1]) for t in [k / n for k in range(1, n + 1)]]
    pts = [(-0.85, -0.15)] + cub((-0.85, -0.15), (-0.5, -0.3), (-0.1, -0.45), (0.15, -0.62)) + cub((0.15, -0.62), (0.4, -0.6), (0.55, -0.35), (0.62, -0.18))
    pts += [(0.7, -0.34), (0.72, -0.16), (0.9, 0.05), (0.92, 0.22), (0.8, 0.28), (0.68, 0.44), (0.58, 0.3), (0.52, 0.66), (0.44, 0.66), (0.4, 0.3)]
    pts += [((1 - t) ** 2 * 0.4 + 2 * (1 - t) * t * 0.0 + t * t * -0.38, (1 - t) ** 2 * 0.3 + 2 * (1 - t) * t * 0.42 + t * t * 0.3) for t in [k / 8 for k in range(1, 9)]]
    pts += [(-0.44, 0.66), (-0.52, 0.66), (-0.6, 0.24), (-0.78, 0.14), (-0.95, 0.3), (-0.88, 0.04)]
    d.polygon([(x + px * r, y + py * r) for px, py in pts], fill=color)

def cueva(tam):
    """Prehistoria: la pared de caliza de una cueva, con sus estratos, el hollín de las lámparas arriba, dos manos en
    negativo sopladas en ocre, un bisonte de carbón e hileras de puntos; la huella es de ocre rojo, como una mano que se
    apoyó mojada en pigmento: más cargada en el centro, con grumos y el borde moteado."""
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    caliza = mezcla(col('#D9C8A8'), col('#B69C76'), ruido(tam, tam / 26, 121))
    caliza = caliza * (0.92 + 0.1 * ruido(tam, 0.9, 122)[..., None])
    estratos = (np.sin(yy / tam * 26 + ruido(tam, tam / 40, 123) * 2.5) + 1) / 2
    caliza = mezcla(caliza, col('#A68B66'), (estratos > 0.93) * 0.35)
    hollin = np.clip(1 - yy / (tam * 0.32), 0, 1) ** 2 * np.clip(ruido(tam, tam / 18, 124) + 0.3, 0, 1)
    caliza = mezcla(caliza, col('#3A2E26'), hollin * 0.45)
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    caliza = mezcla(caliza, col('#7A6248'), np.clip(dist - 0.75, 0, 1) * 1.2)
    # Las dos manos en negativo: la nube de ocre soplado alrededor de la mano.
    manos = [(0.17, 0.3, 0.09, -0.35), (0.84, 0.66, 0.08, 0.4)]
    for k, (fx, fy, fs, g) in enumerate(manos):
        nube = np.clip(1 - np.sqrt((xx - tam * fx) ** 2 + (yy - tam * fy) ** 2) / (tam * fs * 2.1), 0, 1) ** 0.6
        nube = nube * np.clip(0.75 + 0.5 * ruido(tam, 1.2, 125 + k), 0, 1)
        caliza = mezcla(caliza, col('#9A3A1C'), np.clip(nube * 1.1, 0, 0.92))
    fondo = a_img(caliza).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    base_roca = tuple(int(v) for v in col('#C4AC86'))
    for fx, fy, fs, g in manos:
        mano_pil(d, tam * fx, tam * fy, tam * fs, g, base_roca + (235,))
    # El bisonte de carbón, abajo a la izquierda, y las hileras de puntos.
    bisonte_pil(d, tam * 0.24, tam * 0.8, tam * 0.13, (42, 34, 28, 200))
    rng = np.random.default_rng(126)
    for fila, (x0, y0) in enumerate([(0.62, 0.17), (0.66, 0.24), (0.58, 0.88)]):
        for k in range(5):
            r = tam * (0.009 + rng.random() * 0.006); x = tam * (x0 + k * 0.035); y = tam * y0 + (k % 2) * tam * 0.006
            d.ellipse([x - r, y - r, x + r, y + r], fill=(162, 64, 31, 210 if fila < 2 else 170))
    fondo = fondo.convert('RGB')
    # La huella de ocre: grumos de pigmento, más cargada al centro, y el borde moteado.
    grano = ruido(tam, 0.9, 127)
    carga = ruido(tam, tam / 16, 128)
    centro = np.clip(1.15 - dist, 0.4, 1)
    tinta = mezcla(col('#7A2A14'), col('#C0582E'), np.clip(carga * 0.6 + 0.3, 0, 1))
    alfa = m * np.clip(0.62 + 0.38 * grano, 0, 1) * np.clip(0.55 + carga * 0.35, 0, 1) * centro
    sh = relieve(m, fuerza=0.45)[..., None]
    capa = a_rgba(tinta * (1 + sh * 0.2), np.clip(alfa * 1.45, 0, 1))
    guardar(fondo, capa, 'cueva')


# ------------------------------------------------------------------ Templario
def cruz_patada_pts(cx, cy, r, centro=0.15, punta=0.42):
    """Los vértices de la cruz patada (la de ui/Decoracion.kt `cruzPatada`), sin el remate cóncavo."""
    pts = []
    for k in range(4):
        a = math.radians(90 * k - 90); dx, dy = math.cos(a), math.sin(a); nx, ny = -dy, dx
        for f_, q in ((centro, -centro), (1, -punta), (0.9, 0.0), (1, punta)):
            pts.append((cx + (dx * f_ + nx * q) * r, cy + (dy * f_ + ny * q) * r))
    return pts

def templario(tam):
    m = mascara_sello(tam)
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    # La sobreveste: lino blanco en tafetán, con polvo de arena y la gran cruz patada roja detrás de la huella.
    trama = (np.sin(xx * (2 * math.pi / 5.5)) * np.sin(yy * (2 * math.pi / 5.5))) * 0.5 + 0.5
    lino = mezcla(col('#F4EFE3'), col('#DCD2BC'), ruido(tam, tam / 22, 101) * 0.7)
    lino = lino * (0.96 + 0.05 * trama[..., None])
    arena = np.clip((ruido(tam, 1.2, 102) - 0.8) * 3, 0, 1)
    lino = mezcla(lino, col('#C8A86E'), arena * 0.35)
    fondo = a_img(lino).convert('RGBA')
    capa_cruz = Image.new('RGBA', (tam, tam), (0, 0, 0, 0))
    ImageDraw.Draw(capa_cruz).polygon(cruz_patada_pts(tam / 2, tam / 2, tam * 0.47), fill=(176, 25, 31, 255))
    roja = np.asarray(capa_cruz).astype(np.float32)
    tela = 0.88 + 0.12 * trama + 0.06 * ruido(tam, 0.8, 103)
    roja[..., :3] = roja[..., :3] * tela[..., None]
    fondo.alpha_composite(Image.fromarray(np.clip(roja, 0, 255).astype(np.uint8), 'RGBA'))
    dist = np.sqrt((xx - tam / 2) ** 2 + (yy - tam / 2) ** 2) / (tam / 2)
    arr = np.array(fondo.convert('RGB')).astype(np.float32)
    arr = mezcla(arr, col('#6E5A3A'), np.clip(dist - 0.75, 0, 1) * 0.7)
    fondo = a_img(arr)
    # La huella en hierro de cota de malla: acero con relieve, anillas que brillan y algo de óxido en los surcos.
    sh = relieve(m, fuerza=1.2)[..., None]
    anillas = np.abs(np.sin(xx * (2 * math.pi / 7)) * np.sin((yy + (np.floor(xx / 7) % 2) * 3.5) * (2 * math.pi / 7)))
    acero = mezcla(col('#3A4148'), col('#B8C2CA'), np.clip(ruido(tam, tam / 30, 104) * 0.6 + anillas * 0.5, 0, 1))
    acero = mezcla(acero, col('#7A4A2A'), np.clip((ruido(tam, tam / 50, 105) - 0.7) * 3, 0, 1) * 0.35)
    rgb = acero * (1 + sh * 0.55)
    capa = a_rgba(rgb, m)
    sombra = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(tam / 140))).astype(np.float32) / 255
    base = a_rgba(np.zeros((tam, tam, 3)) + col('#1A1714'), np.roll(np.roll(sombra, 3, 0), 3, 1) * 0.55)
    base.alpha_composite(capa)
    guardar(fondo, base, 'templario')


# ------------------------------------------------------------------ Japón Edo
def edo(tam):
    """Japón Edo: washi con sus fibras de kōzo y, detrás, una estampa ukiyo-e en índigo: el monte Fuji nevado, el sol
    rojo, dos bandas de niebla dorada (kasumi) y las olas de seigaiha abajo. La huella, en tinta sumi que se asienta en
    el papel y se aclara donde el pincel iba seco, y el sello de bermellón al pie, un poco torcido."""
    m = mascara_sello(tam)
    papel_c = col('#F2EBDB')
    papel = mezcla(papel_c, col('#E2D5BA'), ruido(tam, tam / 18, 71) * 0.6)
    papel = papel * (0.975 + 0.025 * ruido(tam, 0.8, 72)[..., None])
    fondo = a_img(papel).convert('RGBA')
    d = ImageDraw.Draw(fondo, 'RGBA')
    r = np.random.default_rng(73)
    for _ in range(520):
        x, y = r.random() * tam, r.random() * tam; ang = r.random() * math.pi; l = 8 + r.random() * 30
        d.line([(x + t * l * math.cos(ang) + 2 * math.sin(t * 7), y + t * l * math.sin(ang) + 2 * math.cos(t * 5)) for t in np.linspace(0, 1, 6)],
               fill=(165, 145, 110, 38), width=1)
    c = tam / 2; ind = (30, 58, 95)
    # El sol rojo, arriba a la derecha.
    d.ellipse([c + tam * 0.1, c - tam * 0.38, c + tam * 0.3, c - tam * 0.18], fill=(201, 68, 46, 140))
    # El Fuji, con su cumbre nevada.
    d.polygon([(c - tam * 0.44, c + tam * 0.2), (c - tam * 0.07, c - tam * 0.22), (c + tam * 0.07, c - tam * 0.22), (c + tam * 0.44, c + tam * 0.2)], fill=ind + (60,))
    d.polygon([(c - tam * 0.135, c - tam * 0.145), (c - tam * 0.07, c - tam * 0.22), (c + tam * 0.07, c - tam * 0.22), (c + tam * 0.135, c - tam * 0.145),
               (c + tam * 0.08, c - tam * 0.11), (c + tam * 0.03, c - tam * 0.15), (c - tam * 0.02, c - tam * 0.1), (c - tam * 0.07, c - tam * 0.14)], fill=(252, 250, 244, 200))
    # Bandas de niebla dorada.
    for x0, y0, w, h in ((c - tam * 0.42, c - tam * 0.04, tam * 0.44, tam * 0.036), (c + tam * 0.02, c + tam * 0.06, tam * 0.38, tam * 0.03)):
        d.rounded_rectangle([x0, y0, x0 + w, y0 + h], radius=int(h / 2), fill=(222, 190, 120, 120))
    # Seigaiha: cada hilera tapa la de atrás, como en las telas.
    R = tam * 0.05; pc = tuple(int(v) for v in papel_c) + (255,)
    for fila in range(7):
        yb = c + tam * 0.22 + fila * R * 0.5; x = -R + (R if fila % 2 else 0)
        while x < tam + R:
            d.pieslice([x - R, yb - R, x + R, yb + R], 180, 360, fill=pc)
            for k in range(3):
                q = R * (1 - k * 0.3)
                d.arc([x - q, yb - q, x + q, yb + q], 180, 360, fill=ind + (120,), width=max(2, tam // 300))
            x += 2 * R
    fondo = fondo.convert('RGB')
    # La huella en tinta sumi: grano del papel y zonas donde el pincel iba seco.
    grano = ruido(tam, 0.8, 74)
    seco = ruido(tam, tam / 25, 75)
    tinta = col('#1B1A1E') * (0.85 + 0.3 * grano[..., None])
    alfa = m * np.clip(0.95 - 0.08 * grano - np.clip(seco - 0.62, 0, 1) * 1.1, 0, 1)
    capa = a_rgba(tinta, alfa)
    # El sello de bermellón al pie, con su marco en reserva y 足 en escritura de sello, simplificado.
    k = 3; s = tam * 0.1 * k
    sello = Image.new('RGBA', (int(s * 1.4), int(s * 1.4)), (0, 0, 0, 0)); ds = ImageDraw.Draw(sello, 'RGBA')
    o = s * 0.2; bl = (243, 236, 220, 235); g = max(3, int(s * 0.07))
    ds.rounded_rectangle([o, o, o + s, o + s], radius=int(s * 0.08), fill=(201, 68, 46, 240))
    ds.rectangle([o + s * 0.12, o + s * 0.12, o + s * 0.88, o + s * 0.88], outline=bl, width=g)
    cx = o + s / 2
    ds.rectangle([cx - s * 0.17, o + s * 0.24, cx + s * 0.17, o + s * 0.42], outline=bl, width=g)
    ds.line([(cx, o + s * 0.46), (cx, o + s * 0.76)], fill=bl, width=g)
    ds.line([(cx, o + s * 0.56), (cx + s * 0.2, o + s * 0.56)], fill=bl, width=g)
    ds.line([(cx - s * 0.04, o + s * 0.62), (cx - s * 0.26, o + s * 0.78)], fill=bl, width=g)
    ds.line([(cx, o + s * 0.76), (cx + s * 0.28, o + s * 0.78)], fill=bl, width=g)
    sello = sello.rotate(-5, resample=Image.BICUBIC).resize((int(s * 1.4 / k), int(s * 1.4 / k)), Image.LANCZOS)
    capa.alpha_composite(sello, (int(c + tam * 0.17), int(c + tam * 0.2)))
    guardar(fondo, capa, 'edo')


# ------------------------------------------------------------------ los otros catorce: extraer (una vez) y componer

CATORCE = ['romano', 'ciber', 'heleno', 'medieval', 'distopia', 'refugio', 'orden', 'laboratorio', 'colonos', 'napoleon',
           'navidad', 'seda', 'urss', 'revolucion']


def extraer(res, temas=CATORCE):
    """Una sola vez, con los PNG viejos: separa fondo, material y ornamento de cada sello y los guarda en fuentes/."""
    vieja = np.asarray(Image.open(os.path.join(res, 'huella_romano.webp')).convert('RGBA'), np.float32)[..., 3] / 255   # el sello romano era solo la huella
    cerca = desenfocar(vieja, 4) > 0.04
    dentro = desenfocar(vieja, 25) > 0.02     # todo el óvalo de la huella vieja, con sus valles
    for t in temas:
        fg = np.asarray(Image.open(os.path.join(res, f'huella_{t}.webp')).convert('RGBA'), np.float32)
        rgb, a = fg[..., :3], fg[..., 3] / 255
        crestas = (vieja > 0.6) & (a > 0.6)
        if t == 'laboratorio': crestas &= rgb[..., 0] < rgb[..., 1]    # sin las líneas rosas de las minucias
        crestas = crestas.astype(np.float32)
        mat = extender(rgb, crestas)
        a_img(mat).save(os.path.join(FUENTES, f'material_{t}.jpg'), quality=90)
        shutil.copyfile(os.path.join(res, f'huella_fondo_{t}.jpg'), os.path.join(FUENTES, f'fondo_sello_{t}.jpg'))
        orn = None
        if t == 'napoleon':
            # El lacre sin la huella: la cera del borde de la gota, extendida con suavidad hacia el centro.
            cera = ((a > 0.5) & ~dentro).astype(np.float32)
            liso = desenfocar(rgb * cera[..., None], 90) / np.maximum(desenfocar(cera, 90), 1e-4)[..., None]
            orn = a_rgba(mezcla(liso, rgb, np.clip(desenfocar(cera, 6) * 1.5, 0, 1) * cera), a)
        elif t == 'laboratorio':
            # Las esquinas del lector: lo que queda lejos de la huella vieja, salvo la barra de escaneo (se dibuja aquí).
            yy = np.mgrid[0:TAM, 0:TAM][0]
            sel = ~cerca & (a > 0.08) & ((yy < TAM * 0.3) | (yy > TAM * 0.41))
            orn = a_rgba(rgb, a * sel)
        if orn is not None: orn.save(os.path.join(FUENTES, f'ornamento_{t}.png'), optimize=True)
        print('extraído', t)


def fondo_sello(t):
    """El fondo de cada tema; los demasiado simples se enriquecen sin salirse de su línea (y sin huella)."""
    ruta = os.path.join(FUENTES, f'fondo_sello_{t}.jpg')
    if t not in ('colonos', 'distopia', 'navidad', 'refugio'): return ruta
    a = np.asarray(Image.open(ruta).convert('RGB'), np.float32)
    r = np.random.default_rng(sum(map(ord, t)))
    if t == 'colonos':
        # Papel de gaceta: fibras, grano y la retícula de las columnas del periódico.
        a = a * (0.95 + 0.07 * ruido(TAM, 0.8, 301)[..., None]) - 10 * np.clip((ruido(TAM, 30, 302) - 0.7) * 3, 0, 1)[..., None]
        img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
        for x in np.linspace(TAM * 0.06, TAM * 0.94, 5):
            d.line([(x, TAM * 0.05), (x, TAM * 0.95)], fill=(90, 70, 45, 34), width=2)
        for y in (TAM * 0.05, TAM * 0.12, TAM * 0.95):
            d.line([(TAM * 0.06, y), (TAM * 0.94, y)], fill=(90, 70, 45, 50), width=3 if y < TAM * 0.1 else 2)
        for _ in range(420):
            x, y = r.random() * TAM, r.random() * TAM; ang = r.random() * math.pi; l = 4 + r.random() * 14
            d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=(120, 95, 60, 26), width=1)
        return img
    if t == 'distopia':
        # Papel de oficio: grano y las marcas tenues de otros sellos de goma.
        a = a * (0.95 + 0.07 * ruido(TAM, 0.7, 311)[..., None])
        img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
        for (cx, cy, rr, ang) in ((TAM * 0.2, TAM * 0.72, TAM * 0.1, 0.3), (TAM * 0.78, TAM * 0.3, TAM * 0.085, -0.5)):
            d.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], outline=(150, 40, 40, 34), width=4)
            d.ellipse([cx - rr * 0.78, cy - rr * 0.78, cx + rr * 0.78, cy + rr * 0.78], outline=(150, 40, 40, 26), width=2)
        d.rectangle([TAM * 0.56, TAM * 0.62, TAM * 0.86, TAM * 0.7], outline=(40, 40, 45, 26), width=3)
        return img
    if t == 'navidad':
        # Las luces de siempre, más nieve fina que cae.
        img = a_img(a).convert('RGBA'); capa = Image.new('RGBA', img.size, (0, 0, 0, 0)); d = ImageDraw.Draw(capa, 'RGBA')
        for _ in range(520):
            x, y = r.random() * TAM, r.random() * TAM; s = 0.8 + r.random() ** 3 * 4
            d.ellipse([x - s, y - s, x + s, y + s], fill=(255, 255, 255, int(70 + r.random() * 150)))
        img.alpha_composite(capa.filter(ImageFilter.GaussianBlur(0.6)))
        return img.convert('RGB')
    if t == 'refugio':
        # Era un verde casi plano: líneas de barrido de pantalla vieja y un viñeteado suave.
        yy, xx = np.mgrid[0:TAM, 0:TAM].astype(np.float32)
        lineas = 1 - 0.08 * (np.sin(yy * math.pi / 2.5) > 0.6)
        dist = np.sqrt((xx - TAM / 2) ** 2 + (yy - TAM / 2) ** 2) / (TAM / 2)
        a = a * lineas[..., None] * (1.05 - 0.25 * np.clip(dist - 0.4, 0, 1))[..., None] * (0.97 + 0.05 * ruido(TAM, 0.7, 321)[..., None])
        return a_img(a)


# Por tema: relieve (metal, cera), grano de tinta, halo (pantallas), trama de medios tonos y dónde va el ornamento.
ESTILO = {
    'romano': dict(relieve=0.35, grano=0.05), 'ciber': dict(halo=(9, 0.6), marcas='minucias'), 'heleno': dict(grano=0.06),
    'medieval': dict(grano=0.12), 'distopia': dict(grano=0.15), 'refugio': dict(halo=(11, 0.65)), 'orden': dict(relieve=0.35, grano=0.06),
    'laboratorio': dict(orn='encima', marcas='lector'), 'colonos': dict(grano=0.1), 'napoleon': dict(orn='cera'),
    'navidad': dict(marcas='destellos', grano=0.04),
    'seda': dict(relieve=0.2), 'urss': dict(trama=True), 'revolucion': dict(grano=0.05, contorno='#1F1F24'),
}


def componer(t):
    m = mascara_sello()
    e = ESTILO[t]
    mat = np.asarray(Image.open(os.path.join(FUENTES, f'material_{t}.jpg')).convert('RGB'), np.float32)
    s = sum(map(ord, t))
    grano = ruido(TAM, 0.8, s)
    rgb = mat * (1 - e.get('grano', 0) / 2 + e.get('grano', 0) * grano[..., None])
    alfa = m * np.clip(1 - e.get('grano', 0) * 0.8 + e.get('grano', 0) * grano, 0, 1)
    if e.get('relieve'): rgb = rgb * (1 + relieve(m, fuerza=0.8)[..., None] * e['relieve'])
    if e.get('trama'):
        # Cartel constructivista: la huella impresa en puntos de medios tonos, más finos donde el material es claro.
        yy, xx = np.mgrid[0:TAM, 0:TAM].astype(np.float32)
        claro = np.clip((luminancia(mat) - 120) / 60, 0, 1)
        trama = np.sin((xx + yy) * 0.62) * np.sin((xx - yy) * 0.62)
        alfa = alfa * np.clip((trama + 0.55 - 0.35 * claro) * 4, 0, 1)
    capa = a_rgba(rgb, alfa)
    if e.get('contorno'):
        # Un filete oscuro alrededor de cada cresta (la franja blanca del tricolor no se pierde en el papel).
        filete = a_rgba(np.zeros((TAM, TAM, 3)) + col(e['contorno']), np.clip(desenfocar(m, 1.6) * 3.2, 0, 1))
        filete.alpha_composite(capa); capa = filete
    if e.get('halo'):
        radio, fuerza = e['halo']
        g = desenfocar(m, radio) * fuerza * 1.6
        halo = a_rgba(mat * 1.1, np.clip(g, 0, 1) * (1 - m))
        halo.alpha_composite(capa); capa = halo
    orn = os.path.join(FUENTES, f'ornamento_{t}.png')
    if e.get('orn') == 'cera':
        cera = Image.open(orn).convert('RGBA')
        c = np.asarray(cera, np.float32); ca = c[..., 3] / 255
        sh = relieve(m, fuerza=1.1)[..., None]
        surco = c[..., :3] * (1 - 0.22 * m[..., None]) * (1 + sh * 0.45)
        capa = a_rgba(surco, ca)
    elif e.get('orn') == 'encima':
        capa.alpha_composite(Image.open(orn).convert('RGBA'))
    if e.get('marcas'): marcas(capa, m, e['marcas'], s)
    guardar(fondo_sello(t), capa, t)


def puntos_en_crestas(m, n, semilla, margen=0.0):
    """[n] puntos al azar sobre las crestas de la huella (en su parte central si hay [margen])."""
    r = np.random.default_rng(semilla)
    ys, xs = np.where(desenfocar(m, 1.5) > 0.8)
    if margen:
        cx, cy = xs.mean(), ys.mean(); rx, ry = (xs.max() - xs.min()) / 2, (ys.max() - ys.min()) / 2
        ok = ((xs - cx) / rx) ** 2 + ((ys - cy) / ry) ** 2 < (1 - margen) ** 2
        xs, ys = xs[ok], ys[ok]
    i = r.choice(len(xs), n, replace=False)
    return list(zip(xs[i].tolist(), ys[i].tolist()))


def marcas(capa, m, tipo, semilla):
    """Lo que va sobre las crestas: el lector del laboratorio, las minucias de Ciber, los destellos de Navidad."""
    k = 3   # se dibuja al triple y se reduce, para bordes suaves
    lienzo = Image.new('RGBA', (TAM * k, TAM * k), (0, 0, 0, 0)); d = ImageDraw.Draw(lienzo, 'RGBA')
    if tipo == 'lector':
        # La barra de escaneo, de lado a lado, y las minucias unidas por líneas finas.
        y0, y1 = TAM * 0.335 * k, TAM * 0.375 * k
        d.rectangle([TAM * 0.04 * k, y0, TAM * 0.96 * k, y1], fill=(120, 225, 240, 95))
        d.line([(TAM * 0.04 * k, (y0 + y1) / 2), (TAM * 0.96 * k, (y0 + y1) / 2)], fill=(255, 255, 255, 220), width=2 * k)
        p = puntos_en_crestas(m, 11, semilla, margen=0.1)
        p.sort(key=lambda q: q[1])
        d.line([(x * k, y * k) for x, y in p], fill=(232, 90, 140, 190), width=2 * k)
        for x, y in p:
            rr = 11 * k
            d.ellipse([x * k - rr, y * k - rr, x * k + rr, y * k + rr], outline=(232, 90, 140, 255), width=2 * k)
    elif tipo == 'minucias':
        # Cuadritos claros donde el lector marca una minucia.
        for x, y in puntos_en_crestas(m, 34, semilla):
            s = 4.5 * k
            d.rectangle([x * k - s, y * k - s, x * k + s, y * k + s], fill=(235, 255, 255, 235))
    elif tipo == 'destellos':
        # Destellos de cuatro puntas, dorados, como luces sobre la nieve.
        r = np.random.default_rng(semilla)
        for x, y in puntos_en_crestas(m, 12, semilla):
            s = (10 + r.random() * 12) * k; a = int(170 + r.random() * 80)
            cx, cy = x * k, y * k
            d.polygon([(cx, cy - s), (cx + s * 0.16, cy - s * 0.16), (cx + s, cy), (cx + s * 0.16, cy + s * 0.16),
                       (cx, cy + s), (cx - s * 0.16, cy + s * 0.16), (cx - s, cy), (cx - s * 0.16, cy - s * 0.16)], fill=(255, 240, 190, a))
    capa.alpha_composite(lienzo.resize((TAM, TAM), Image.LANCZOS))


# ------------------------------------------------------------------ uso

# Los sellos que se dibujan con código (los demás se componen desde `fuentes/`). Los temas nuevos van aquí.
SIETE = [
    'neon',
    'botanico',
    'ruinas',
    'gotico',
    'arcano',
    'deco',
    'espacio',
    'abismo',
    'urbe',
    'andalus',
    'meso',
    'egipto',
    'mongol',
    'edo',
    'renacimiento',
    'pirata',
    'antiguas',
    'cueva',
    'templario',
    'vapor',
    'frente',
    'arcontes',
    'galia',
    'nouveau',
]
ORDEN = [
    'romano',
    'urbe',
    'ciber',
    'neon',
    'heleno',
    'egipto',
    'medieval',
    'templario',
    'distopia',
    'galia',
    'arcontes',
    'frente',
    'nouveau',
    'refugio',
    'orden',
    'vapor',
    'laboratorio',
    'colonos',
    'napoleon',
    'deco',
    'botanico',
    'ruinas',
    'navidad',
    'seda',
    'edo',
    'gotico',
    'arcano',
    'espacio',
    'abismo',
    'andalus',
    'meso',
    'mongol',
    'renacimiento',
    'pirata',
    'urss',
    'revolucion',
    'antiguas',
    'cueva',
]


def hoja_contacto(archivo, temas=ORDEN):
    """Hoja de contacto de los sellos, recortados en círculo como en la app."""
    from iconos import hoja_de_contacto
    circulo = Image.new('L', (TAM, TAM), 0); ImageDraw.Draw(circulo).ellipse((0, 0, TAM, TAM), fill=255)
    imgs = []
    for t in temas:
        f = Image.open(os.path.join(OUT, f'huella_fondo_{t}.jpg')).convert('RGBA')
        f.alpha_composite(Image.open(os.path.join(OUT, f'huella_{t}.webp')).convert('RGBA'))
        imgs.append(f)
    hoja_de_contacto(imgs, archivo, mascaras=[circulo] * len(imgs))


if __name__ == '__main__':
    args = sys.argv[1:]
    if args and args[0] == 'extraer':
        extraer(args[1]); sys.exit()
    OUT = args[0]
    archivo_hoja = args[args.index('--hoja') + 1] if '--hoja' in args else None
    pedidos = [a for a in args[1:] if not a.startswith('--') and a != archivo_hoja]
    temas = pedidos[0].split(',') if pedidos else ORDEN
    for t in temas:
        if t in SIETE: globals()[t](TAM)
        else: componer(t)
        print('listo', t)
    if archivo_hoja: hoja_contacto(archivo_hoja)
