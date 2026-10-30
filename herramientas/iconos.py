"""
Íconos de la app (logo_fondo_<id>.jpg + logo_<id>.png, 432 × 432, y logo_mono.png). La composición es la misma en todos
los temas: la huella maestra (maestra.py) del mismo tamaño y en la misma posición, dentro del círculo seguro del ícono
adaptativo (66 %). Cada tema añade solo tres cosas (lote 1 del plan de diseño):
1. una textura de fondo propia y tenue;
2. la tinta de la huella en el color del tema, con un acabado discreto (dorado, tinta ferrogálica, fósforo…);
3. un anillo fino con el motivo del tema en miniatura, siempre del mismo grosor y radio.
Uso: python3 iconos.py ../app/src/main/res/drawable-nodpi [hoja.png]
"""
import math, sys, os
import numpy as np
from PIL import Image, ImageDraw, ImageFilter
import maestra

TAM = 432
RADIO = 0.29 * TAM         # el anillo y su motivo caben en el círculo seguro (radio 0.333) con aire alrededor
GROSOR = 2.6               # px a 432 (unos 0.65 dp)
K = 4                      # el anillo se dibuja al cuádruple y se reduce, para bordes suaves


def mascara():
    """La huella ocupa el 46 % del alto: queda dentro del anillo, con aire entre los dos."""
    return maestra.mascara(TAM, TAM * 0.46)


def ruido(sem, escala=1.0, suave=0):
    r = np.random.default_rng(sem).standard_normal((TAM, TAM)).astype(np.float32)
    if suave:
        r = np.asarray(Image.fromarray(((r * 40) + 128).clip(0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(suave)), dtype=np.float32)
        r = (r - 128) / 40
    return r * escala

def rgb(h): h = h.lstrip('#'); return np.array([int(h[i:i + 2], 16) for i in (0, 2, 4)], dtype=np.float32)

Y, X = np.mgrid[0:TAM, 0:TAM].astype(np.float32)

# ---- texturas: devuelven un campo de -1..1 aprox. (cuánto aclarar u oscurecer cada pixel). Una por tema.
def travertino(sem):     # Romano: travertino con poros alargados en capas
    base = ruido(sem, 1.0, 8) * 0.6
    poros = np.asarray(Image.fromarray(((ruido(sem + 3, 1, 0) > 2.2) * 255).astype(np.uint8)).resize((TAM // 4, TAM)).resize((TAM, TAM)), dtype=np.float32) / 255
    capas = np.sin(Y * 0.18 + ruido(sem + 4, 3, 12)) * 0.2
    return base + capas - poros * 1.2

def pantalla(sem):       # Ciber: líneas de barrido y una retícula muy tenue
    lineas = (np.sin(Y * math.pi / 2) > 0.7) * -0.5
    reticula = ((X % 27 < 1) | (Y % 27 < 1)) * 0.6
    return lineas + reticula + ruido(sem, 0.25)

def pario(sem):          # Helénico: mármol de Paros, blanco y fino, con vetas cortas y suaves
    t = ruido(sem, 4.0, 18)
    vetas = np.abs(np.sin((X * 0.8 - Y * 1.2) / TAM * 14 + t * 2.2)) ** 30 * -0.7
    return vetas + ruido(sem + 1, 0.35, 2) + ruido(sem + 2, 0.4, 24)

def vitela(sem):         # Scriptorium: vitela con manchas cálidas y poros de folículo
    poros = (ruido(sem, 1, 0) > 2.6) * -0.9
    return ruido(sem + 1, 1.0, 22) * 0.8 + ruido(sem + 2, 0.3) + poros

def papel(sem):          # Distopía: papel con fibras y grano
    f = ruido(sem, 0.5) + ruido(sem + 1, 1.2, 6)
    fibras = np.sin(X * 0.9 + ruido(sem + 2, 6, 10)) * 0.25
    return f * 0.6 + fibras * 0.4

def hormigon(sem):       # Refugio: hormigón con burbujas y salpicado
    burbujas = np.asarray(Image.fromarray(((ruido(sem, 1, 0) > 2.4) * 255).astype(np.uint8)).filter(ImageFilter.MaxFilter(3)), np.float32) / 255 * -1.1
    return ruido(sem + 1, 0.9, 3) * 0.7 + ruido(sem + 2, 0.6, 16) + burbujas

def acero(sem):          # Orden: acero cepillado en horizontal
    rayas = np.asarray(Image.fromarray(((ruido(sem, 1, 0) * 30) + 128).clip(0, 255).astype(np.uint8)).resize((TAM // 24, TAM)).resize((TAM, TAM), Image.BILINEAR), np.float32)
    return (rayas - 128) / 30 * 0.7 + ruido(sem + 1, 0.3, 30)

def cuadricula(sem):     # Laboratorio: papel de laboratorio cuadriculado
    fino = ((X % 18 < 1) | (Y % 18 < 1)) * -0.6
    grueso = ((X % 90 < 1.5) | (Y % 90 < 1.5)) * -0.6
    return fino + grueso + ruido(sem, 0.2)

def gaceta(sem):         # Colonos: papel de periódico con fibras y columnas
    columnas = ((X % 72) < 1.2) * -0.7
    fibras = (np.abs(np.sin(X * 0.4 + Y * 0.13 + ruido(sem, 8, 6))) < 0.05) * -0.5
    return ruido(sem + 1, 0.7, 2) + columnas + fibras

def marmol(sem):         # Napoleónico: mármol con vetas suaves
    x, y = X / TAM, Y / TAM
    t = ruido(sem, 6.0, 30) + ruido(sem + 5, 2.5, 12)
    vetas = np.abs(np.sin((x * 1.3 + y * 0.7) * 4 + t * 3.2)) ** 24 * -1.1
    finas = np.abs(np.sin((x * 0.6 - y * 1.1) * 9 + t * 4.5)) ** 40 * -0.5
    return vetas + finas + ruido(sem + 1, 0.3, 3) + ruido(sem + 2, 0.5, 20)

def herbario(sem):       # Botánico: papel de herbario con fibras y nervaduras muy tenues
    nerv = (np.abs(np.sin((X - Y * 0.6) * 0.035 + ruido(sem, 1.5, 16))) < 0.04) * -0.8
    return ruido(sem + 1, 0.9, 5) * 0.7 + ruido(sem + 2, 0.35) + nerv

def fieltro_nieve(sem):  # Navidad: fieltro con copitos de nieve
    copos = (ruido(sem, 1, 0) > 2.7) * 1.4
    return ruido(sem + 1, 0.8, 1.5) + ruido(sem + 2, 0.4, 14) + copos

def seda(sem):           # Seda: tejido fino de hilos horizontales y verticales con brillo
    trama = np.sin(Y * 1.6) * 0.35 + np.sin(X * 1.6) * 0.15
    brillo = np.sin((X + Y) * 0.02 + ruido(sem, 2, 20)) * 0.5
    return trama + brillo + ruido(sem + 1, 0.2)

def piedra(sem):         # Gótico: piedra con manchas y poros
    return ruido(sem, 1.0, 14) * 0.9 + ruido(sem + 1, 0.4, 1) + (ruido(sem + 2, 1, 0) > 2.3) * -1.2

def archivo_manchado(sem):  # Horror cósmico: papel de archivo con manchas de humedad, cercos de taza y fibras
    manchas = ruido(sem, 1.0, 22) * 0.8
    cx, cy, rr = TAM * 0.68, TAM * 0.3, TAM * 0.2
    d = np.sqrt((X - cx) ** 2 + (Y - cy) ** 2)
    cerco = -np.exp(-((d - rr) / 3.0) ** 2) * 0.9
    fibras = np.sin(Y * 0.9 + ruido(sem + 1, 3, 6)) * 0.12
    return manchas + cerco + fibras + ruido(sem + 2, 0.25)

def polvo_estelar(sem):  # Espacio: polvo de estrellas y una nebulosa apenas visible
    estrellas = (ruido(sem, 1, 0) > 2.9) * 1.6
    return estrellas + ruido(sem + 1, 0.8, 40) + ruido(sem + 2, 0.15)

def lazo(sem):           # Al-Ándalus: lazo de ocho, apenas grabado
    u, v = (X % 54) - 27, (Y % 54) - 27
    a = np.maximum(np.abs(u), np.abs(v)); b = (np.abs(u) + np.abs(v)) / math.sqrt(2)
    estrella = np.maximum(a, b)
    return (np.abs(estrella - 15) < 1) * -0.7 + ruido(sem, 0.25, 2)

def fieltro(sem):        # Mongol: fieltro de yurta, grueso y moteado
    return ruido(sem, 1.0, 3) * 0.8 + ruido(sem + 1, 0.7, 10) + ruido(sem + 2, 0.4, 0.8)

def construccion(sem):   # Renacimiento: papel con los trazos de construcción de un dibujo
    d1 = np.abs(np.sqrt((X - TAM * 0.5) ** 2 + (Y - TAM * 0.5) ** 2) - TAM * 0.42) < 0.8
    d2 = np.abs(X - Y) < 0.8
    d3 = np.abs(X + Y - TAM) < 0.8
    return (d1 | d2 | d3) * -0.7 + ruido(sem, 0.8, 18) + ruido(sem + 1, 0.3)

def cartel(sem):         # Soviético: papel de cartel con una trama de medios tonos muy leve
    trama = (np.sin(X * 0.9) * np.sin(Y * 0.9) > 0.55) * -0.6
    return trama + ruido(sem, 0.7, 12) + ruido(sem + 1, 0.3)

def verjurado(sem):      # Revolución francesa: papel verjurado (corondeles y puntizones)
    puntizones = (Y % 4 < 1) * -0.25
    corondeles = ((X % 46) < 1.2) * -0.6
    return puntizones + corondeles + ruido(sem, 0.6, 10) + ruido(sem + 1, 0.25)

def pizarra(sem):        # Tierras Antiguas: pizarra en lajas
    capas = np.sin(Y * 0.09 + ruido(sem, 4, 30)) * 0.5
    lajas = (np.abs(np.sin(Y * 0.045 + X * 0.01 + ruido(sem + 1, 2, 20))) < 0.03) * -1.0
    return capas + lajas + ruido(sem + 2, 0.4, 1.2)


# ---- motivos del anillo: se dibujan sobre el anillo, en coordenadas del lienzo grande (×K).
def _pos(ang, r=RADIO): return (TAM / 2 + r * math.cos(ang)) * K, (TAM / 2 + r * math.sin(ang)) * K

def _hojitas(d, c, n=16):             # laurel: dos hojas a cada lado de la rama
    for i in range(n):
        a = 2 * math.pi * i / n
        for lado in (-1, 1):
            x, y = _pos(a, RADIO + lado * 4.2); s = 4.6 * K
            ang = a + math.pi / 2 + lado * 0.5
            p = [(x + s * math.cos(ang + t) * (1 if t == 0 else 0.35), y + s * math.sin(ang + t) * (1 if t == 0 else 0.35)) for t in (0, math.pi / 2, math.pi, 3 * math.pi / 2)]
            d.polygon(p, fill=c)

def _rayas(d, c, n=32, largo=0.6):    # segmentos sobre el anillo (como una barra de carga)
    for i in range(n):
        a0 = 2 * math.pi * i / n; a1 = a0 + 2 * math.pi / n * largo
        d.arc([(TAM / 2 - RADIO) * K, (TAM / 2 - RADIO) * K, (TAM / 2 + RADIO) * K, (TAM / 2 + RADIO) * K], math.degrees(a0), math.degrees(a1), fill=c, width=int(GROSOR * K * 1.6))

def _perlas(d, c, n=28, r=2.4):
    for i in range(n):
        x, y = _pos(2 * math.pi * i / n); d.ellipse([x - r * K, y - r * K, x + r * K, y + r * K], fill=c)

def _cuadrifolios(d, c, n=12):
    for i in range(n):
        a = 2 * math.pi * i / n; x, y = _pos(a); r = 1.9 * K
        for k in range(4):
            u = a + k * math.pi / 2; cx, cy = x + 2.1 * K * math.cos(u), y + 2.1 * K * math.sin(u)
            d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=c)

def _marcas(d, c, n=48, largo=6, cada=4, largo2=10):   # muescas radiales (las de cada [cada], más largas)
    for i in range(n):
        a = 2 * math.pi * i / n; l = largo2 if i % cada == 0 else largo
        x0, y0 = _pos(a, RADIO - l / 2); x1, y1 = _pos(a, RADIO + l / 2)
        d.line([(x0, y0), (x1, y1)], fill=c, width=int(GROSOR * K * 0.8))

def _dientes(d, c, n=24):             # engrane
    for i in range(n):
        a = 2 * math.pi * i / n
        p = [_pos(a + s * 0.045, RADIO + h) for s, h in ((-1, 0), (-0.7, 6), (0.7, 6), (1, 0))]
        d.polygon(p, fill=c)

def _remaches(d, c, n=8):
    for i in range(n):
        x, y = _pos(2 * math.pi * i / n + math.pi / 8); r = 4.2 * K
        d.ellipse([x - r, y - r, x + r, y + r], fill=c)
        d.ellipse([x - r * 0.45 - r * 0.3, y - r * 0.45 - r * 0.3, x + r * 0.45 - r * 0.3, y + r * 0.45 - r * 0.3], fill=(255, 255, 255, 110))

def _cuerda(d, c, n=56):              # cuerda trenzada
    for i in range(n):
        a = 2 * math.pi * i / n
        x0, y0 = _pos(a - 0.03, RADIO - 3.4); x1, y1 = _pos(a + 0.05, RADIO + 3.4)
        d.line([(x0, y0), (x1, y1)], fill=c, width=int(GROSOR * K * 0.9))

def _estrellas(d, c, n=12, s=5.0, puntas=5):
    for i in range(n):
        x, y = _pos(2 * math.pi * i / n); p = []
        for k in range(puntas * 2):
            r = (s if k % 2 == 0 else s * 0.42) * K; u = -math.pi / 2 + k * math.pi / puntas
            p.append((x + r * math.cos(u), y + r * math.sin(u)))
        d.polygon(p, fill=c)

def _hojas_alternas(d, c, n=18):
    for i in range(n):
        a = 2 * math.pi * i / n; lado = 1 if i % 2 else -1
        x, y = _pos(a, RADIO + lado * 4); s = 5 * K; ang = a + math.pi / 2 + lado * 0.6
        d.ellipse([x - s * 0.5, y - s * 0.5, x + s * 0.5, y + s * 0.5], fill=c)
        d.line([_pos(a), (x + s * 0.5 * math.cos(ang), y + s * 0.5 * math.sin(ang))], fill=c, width=K)

def _copos(d, c, n=12, s=5.2):
    for i in range(n):
        x, y = _pos(2 * math.pi * i / n)
        for k in range(3):
            u = k * math.pi / 3
            d.line([(x - s * K * math.cos(u), y - s * K * math.sin(u)), (x + s * K * math.cos(u), y + s * K * math.sin(u))], fill=c, width=int(K * 1.4))

def _monedas(d, c, fondo, n=10):      # monedas con agujero cuadrado
    for i in range(n):
        x, y = _pos(2 * math.pi * i / n); r = 5 * K; q = 1.6 * K
        d.ellipse([x - r, y - r, x + r, y + r], fill=c); d.rectangle([x - q, y - q, x + q, y + q], fill=fondo)

def _trebol(d, c, n=12):
    for i in range(n):
        a = 2 * math.pi * i / n; x, y = _pos(a, RADIO + 1)
        for k in (-1, 0, 1):
            u = a + k * 0.04; cx, cy = _pos(u, RADIO + 3 + (2.5 if k == 0 else 0))
            r = 2.1 * K; d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=c)

def _ganchos(d, c, n=7):                # horror cósmico: puntas que se enroscan sobre el anillo, con un punto entre una y otra
    for i in range(n):
        a = -math.pi / 2 + i * 2 * math.pi / n
        x, y = _pos(a)
        g = math.degrees(a)
        for k, rr in enumerate((10.5, 6.0)):
            s = rr * K
            d.arc([x - s, y - s, x + s, y + s], g + 40 + k * 60, g + 300, fill=c, width=int(K * 1.4))
        x2, y2 = _pos(a + math.pi / n)
        d.ellipse([x2 - 2 * K, y2 - 2 * K, x2 + 2 * K, y2 + 2 * K], fill=c)

def _orbita(d, c):                    # un planeta en su órbita y dos estrellas
    x, y = _pos(-0.6); r = 6 * K; d.ellipse([x - r, y - r, x + r, y + r], fill=c)
    for a in (1.4, 2.6):
        x, y = _pos(a, RADIO + 1)
        for u in (0, math.pi / 2):
            d.line([(x - 4 * K * math.cos(u), y - 4 * K * math.sin(u)), (x + 4 * K * math.cos(u), y + 4 * K * math.sin(u))], fill=c, width=int(K * 1.3))

def _rombos(d, c, n=16, s=4.4):
    for i in range(n):
        a = 2 * math.pi * i / n
        d.polygon([_pos(a, RADIO + s), _pos(a + 0.035, RADIO), _pos(a, RADIO - s), _pos(a - 0.035, RADIO)], fill=c)

def _compas(d, c):                    # marcas de compás: cruces en los puntos cardinales y arcos cortos
    for i in range(4):
        a = i * math.pi / 2; x, y = _pos(a)
        d.line([_pos(a, RADIO - 8), _pos(a, RADIO + 8)], fill=c, width=int(K * 1.6))
        d.line([_pos(a - 0.05), _pos(a + 0.05)], fill=c, width=int(K * 1.6))
    _marcas(d, c, 72, 3, 6, 6)

def _tricolor(d, colores, n=36):
    for i in range(n):
        a0 = 2 * math.pi * i / n; a1 = a0 + 2 * math.pi / n * 0.7
        d.arc([(TAM / 2 - RADIO) * K, (TAM / 2 - RADIO) * K, (TAM / 2 + RADIO) * K, (TAM / 2 + RADIO) * K], math.degrees(a0), math.degrees(a1),
              fill=colores[i % 3], width=int(GROSOR * K * 2))

def _runas(d, c, n=14, s=5):
    r = np.random.default_rng(23)
    for i in range(n):
        a = 2 * math.pi * i / n; x, y = _pos(a)
        u = a + math.pi / 2; dx, dy = math.cos(a), math.sin(a)      # radial
        d.line([(x - s * K * dx, y - s * K * dy), (x + s * K * dx, y + s * K * dy)], fill=c, width=int(K * 1.5))
        t = r.integers(0, 3); ex, ey = math.cos(u) * s * 0.8 * K, math.sin(u) * s * 0.8 * K
        if t == 0: d.line([(x + s * K * dx, y + s * K * dy), (x + ex, y + ey)], fill=c, width=int(K * 1.3))
        elif t == 1: d.line([(x - ex, y - ey), (x + ex, y + ey)], fill=c, width=int(K * 1.3))
        else: d.line([(x - s * K * dx, y - s * K * dy), (x - ex, y - ey)], fill=c, width=int(K * 1.3))


# tema: (textura, fondo, tinta, fuerza de la textura en el fondo y en la huella, acabado, motivo)
# Acabados: 'tinta' (se asienta en el soporte), 'dorado' (brillo metálico leve), 'ferrogalica' (sepia que se aclara
# donde la pluma tenía menos tinta), 'fosforo' (resplandor de pantalla), 'esmalte' (relieve suave).
TEMAS = {
    'romano':       (travertino,    '#E5E1D9', '#6E5424',  10, 6, 'dorado',      _hojitas),
    'ciber':        (pantalla,      '#0D1117', '#7FE3F0',   5, 4, 'fosforo',     _rayas),
    'heleno':       (pario,         '#EEEDE9', '#3D2459',   9, 5, 'tinta',       _perlas),
    'medieval':     (vitela,        '#E8E2D6', '#33261A',   9, 6, 'ferrogalica', _cuadrifolios),
    'distopia':     (papel,         '#EFE9DA', '#1A1A1A',  10, 6, 'tinta',       _marcas),
    'refugio':      (hormigon,      '#1B1C1A', '#9BE8AE',   8, 5, 'fosforo',     _dientes),
    'orden':        (acero,         '#16171A', '#C2A15E',   9, 5, 'dorado',      _remaches),
    'laboratorio':  (cuadricula,    '#F1F2F1', '#1E5A66',   7, 4, 'tinta',       _marcas),
    'colonos':      (gaceta,        '#E7E3DA', '#22324D',   9, 6, 'tinta',       _cuerda),
    'napoleon':     (marmol,        '#EDEBE6', '#1F2B5C',  12, 5, 'esmalte',     _estrellas),
    'botanico':     (herbario,      '#EAE9E1', '#3B2E1D',   9, 6, 'ferrogalica', _hojas_alternas),
    'navidad':      (fieltro_nieve, '#1C1E1D', '#E4C987',   8, 5, 'dorado',      _copos),
    'seda':         (seda,          '#F2EFE8', '#862A1D',   6, 5, 'tinta',       _monedas),
    'gotico':       (piedra,        '#1E1E21', '#D8CCAE',   9, 6, 'dorado',      _trebol),
    'arcano':       (archivo_manchado, '#E3D9C1', '#1E3329', 10, 6, 'ferrogalica', _ganchos),
    'espacio':      (polvo_estelar, '#0F1012', '#B0C8F2',   4, 4, 'fosforo',     _orbita),
    'andalus':      (lazo,          '#F0ECE4', '#1F3F5C',   6, 5, 'esmalte',     _estrellas),
    'mongol':       (fieltro,       '#EFEBE3', '#1F3D78',   6, 5, 'tinta',       _rombos),
    'renacimiento': (construccion,  '#EAE4D9', '#7C3B23',   8, 5, 'ferrogalica', _compas),
    'urss':         (cartel,        '#ECE7DE', '#8E1D1D',  10, 6, 'tinta',       _estrellas),
    'revolucion':   (verjurado,     '#F1EFEA', '#1F2A5A',  11, 5, 'tinta',       _tricolor),
    'antiguas':     (pizarra,       '#1D1F1E', '#C6A65A',   9, 6, 'dorado',      _runas),
}
# Los temas que comparten un motivo lo dibujan distinto (cuántos, de qué tamaño, cuántas puntas).
VARIANTES = {
    'distopia': dict(n=40, largo=7, cada=5, largo2=11), 'laboratorio': dict(n=96, largo=4, cada=8, largo2=9),
    'napoleon': dict(n=12, s=5.0), 'andalus': dict(n=8, s=6.4, puntas=8), 'urss': dict(n=5, s=7.5),
}


def anillo(tema, color):
    """El anillo fino y su motivo, en una capa RGBA de TAM × TAM."""
    capa = Image.new('RGBA', (TAM * K, TAM * K), (0, 0, 0, 0)); d = ImageDraw.Draw(capa, 'RGBA')
    c = tuple(int(v) for v in color) + (235,)
    caja = [(TAM / 2 - RADIO) * K, (TAM / 2 - RADIO) * K, (TAM / 2 + RADIO) * K, (TAM / 2 + RADIO) * K]
    motivo = TEMAS[tema][6]
    if motivo is _tricolor:
        _tricolor(d, [(31, 42, 110, 235), (245, 245, 245, 235), (178, 34, 52, 235)])
        d.ellipse(caja, outline=c, width=int(GROSOR * K * 0.5))
        return capa.resize((TAM, TAM), Image.LANCZOS)
    if motivo is not _rayas:
        d.ellipse(caja, outline=c, width=int(GROSOR * K))
    if motivo is _monedas: _monedas(d, c, (0, 0, 0, 0))
    else: motivo(d, c, **VARIANTES.get(tema, {}))
    return capa.resize((TAM, TAM), Image.LANCZOS)


def hacer(tema, salida):
    textura, fondo, tinta, f_fondo, f_huella, acabado, _ = TEMAS[tema]
    sem = sum(map(ord, tema))
    tex = textura(sem)
    # Fondo: el color con la textura (pocos niveles arriba y abajo).
    bg = rgb(fondo)[None, None, :] + tex[..., None] * f_fondo
    Image.fromarray(np.clip(bg, 0, 255).astype(np.uint8)).save(os.path.join(salida, f'logo_fondo_{tema}.jpg'), quality=92)
    # Huella: la forma fija, con la misma textura muy leve y el acabado del tema.
    m = mascara()
    t2 = textura(sem + 7)
    base = rgb(tinta)
    col = base[None, None, :] + t2[..., None] * f_huella
    alfa = m * np.clip(0.93 + t2 * 0.04, 0.82, 1.0)
    if acabado == 'dorado':
        # Un brillo que baja en diagonal, como luz sobre metal.
        luz = np.clip(1 - np.abs((X + Y) / TAM - 0.85) * 1.6, 0, 1) ** 2
        col = col * (0.88 + 0.32 * luz[..., None])
    elif acabado == 'ferrogalica':
        # Tinta de agallas: más clara y parda donde la pluma se quedó sin tinta.
        carga = ruido(sem + 11, 1.0, 26)
        col = col + (np.clip(carga, 0, 2)[..., None] * np.array([26, 18, 8], np.float32))
        alfa = alfa * np.clip(0.95 - np.clip(carga, 0, 2) * 0.06, 0.8, 1)
    elif acabado == 'esmalte':
        # Relieve suave, como esmalte sobre metal.
        b = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.4)), np.float32) / 255
        gy, gx = np.gradient(b)
        col = col * (1 + np.clip((gx + gy) * -5, -0.25, 0.25))[..., None]
    capa = Image.fromarray(np.dstack([np.clip(col, 0, 255), alfa * 255]).astype(np.uint8), 'RGBA')
    if acabado == 'fosforo':
        # Resplandor de pantalla alrededor de las crestas.
        g = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(5)), np.float32) / 255
        halo = Image.fromarray(np.dstack([np.broadcast_to(base, (TAM, TAM, 3)), np.clip(g * 0.55, 0, 1) * 255]).astype(np.uint8), 'RGBA')
        halo.alpha_composite(capa); capa = halo
    final = anillo(tema, base)
    final.alpha_composite(capa)
    final.save(os.path.join(salida, f'logo_{tema}.png'), optimize=True)


def mono(salida):
    """El ícono monocromo (Android lo tiñe): solo la huella maestra, sin anillo."""
    m = mascara()
    img = np.dstack([np.full((TAM, TAM, 3), 255, np.uint8), (m * 255).astype(np.uint8)])
    Image.fromarray(img, 'RGBA').save(os.path.join(salida, 'logo_mono.png'), optimize=True)


COLUMNAS = 7

def hoja_de_contacto(imagenes, archivo, cel=160, columnas=COLUMNAS, mascaras=None):
    """Junta las imágenes en una cuadrícula de `columnas` fijas; las filas salen del número de temas."""
    filas = max(1, -(-len(imagenes) // columnas))
    hoja = Image.new('RGB', (cel * columnas, cel * filas), 'white')
    for i, img in enumerate(imagenes):
        m = mascaras[i] if mascaras else None
        hoja.paste(img.convert('RGB').resize((cel - 10, cel - 10)), ((i % columnas) * cel + 5, (i // columnas) * cel + 5),
                   m.resize((cel - 10, cel - 10)) if m else None)
    hoja.save(archivo)

def muestra(salida, archivo, cel=160):
    # Recorte circular como en la pantalla de inicio.
    c = Image.new('L', (TAM, TAM), 0)
    ImageDraw.Draw(c).ellipse((TAM * 0.17, TAM * 0.17, TAM * 0.83, TAM * 0.83), fill=255)
    caja = (int(TAM * 0.17), int(TAM * 0.17), int(TAM * 0.83), int(TAM * 0.83))
    imagenes = []
    for t in TEMAS:
        f = Image.open(os.path.join(salida, f'logo_fondo_{t}.jpg')).convert('RGBA')
        f.alpha_composite(Image.open(os.path.join(salida, f'logo_{t}.png')))
        imagenes.append(f.crop(caja))
    hoja_de_contacto(imagenes, archivo, cel=cel, mascaras=[c.crop(caja)] * len(imagenes))

if __name__ == '__main__':
    salida = sys.argv[1]
    for t in TEMAS: hacer(t, salida)
    mono(salida)
    if len(sys.argv) > 2: muestra(salida, sys.argv[2])
    print('listo', len(TEMAS))
