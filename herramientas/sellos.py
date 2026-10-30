"""
Los sellos del día (huella_<id>.png + huella_fondo_<id>.jpg, 720 × 720): el fondo de cada tema y, encima, la huella
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
    img_huella.quantize(256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE).save(os.path.join(OUT, f'huella_{tema}.png'), optimize=True)


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


# ------------------------------------------------------------------ los otros catorce: extraer (una vez) y componer

CATORCE = ['romano', 'ciber', 'heleno', 'medieval', 'distopia', 'refugio', 'orden', 'laboratorio', 'colonos', 'napoleon',
           'navidad', 'seda', 'urss', 'revolucion']


def extraer(res, temas=CATORCE):
    """Una sola vez, con los PNG viejos: separa fondo, material y ornamento de cada sello y los guarda en fuentes/."""
    vieja = np.asarray(Image.open(os.path.join(res, 'huella_romano.png')).convert('RGBA'), np.float32)[..., 3] / 255   # el sello romano era solo la huella
    cerca = desenfocar(vieja, 4) > 0.04
    dentro = desenfocar(vieja, 25) > 0.02     # todo el óvalo de la huella vieja, con sus valles
    for t in temas:
        fg = np.asarray(Image.open(os.path.join(res, f'huella_{t}.png')).convert('RGBA'), np.float32)
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
    'botanico',
    'gotico',
    'arcano',
    'espacio',
    'andalus',
    'mongol',
    'renacimiento',
    'antiguas',
]
ORDEN = [
    'romano',
    'ciber',
    'heleno',
    'medieval',
    'distopia',
    'refugio',
    'orden',
    'laboratorio',
    'colonos',
    'napoleon',
    'botanico',
    'navidad',
    'seda',
    'gotico',
    'arcano',
    'espacio',
    'andalus',
    'mongol',
    'renacimiento',
    'urss',
    'revolucion',
    'antiguas',
]


def hoja_contacto(archivo, temas=ORDEN):
    """Hoja de contacto de los sellos, recortados en círculo como en la app."""
    from iconos import hoja_de_contacto
    circulo = Image.new('L', (TAM, TAM), 0); ImageDraw.Draw(circulo).ellipse((0, 0, TAM, TAM), fill=255)
    imgs = []
    for t in temas:
        f = Image.open(os.path.join(OUT, f'huella_fondo_{t}.jpg')).convert('RGBA')
        f.alpha_composite(Image.open(os.path.join(OUT, f'huella_{t}.png')).convert('RGBA'))
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
