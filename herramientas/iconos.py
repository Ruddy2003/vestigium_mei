"""
Íconos de la app: la misma huella (fuentes/huella_icono.jpg) en todos los temas; solo cambian la textura y el tono del
fondo y de la huella, siempre cerca del negro, el blanco y el gris. Seis texturas, una por tema base; los temas
complementarios usan la de su base con su propio matiz.
Uso: python3 iconos.py ../app/src/main/res/drawable-nodpi
"""
import sys, os
import numpy as np
from PIL import Image, ImageFilter

TAM = 432
AQUI = os.path.dirname(os.path.abspath(__file__))

def mascara():
    im = Image.open(os.path.join(AQUI, 'fuentes', 'huella_icono.jpg')).convert('L')
    a = 1.0 - np.asarray(im, dtype=np.float32) / 255.0
    a = np.clip((a - 0.25) / 0.45, 0, 1)
    ys, xs = np.where(a > 0.5)
    y0, y1, x0, x1 = ys.min(), ys.max(), xs.min(), xs.max()
    m = Image.fromarray((a[y0:y1 + 1, x0:x1 + 1] * 255).astype(np.uint8))
    # La huella ocupa la mitad del alto: queda dentro del círculo seguro del ícono adaptativo (66 %).
    alto = int(TAM * 0.5); ancho = int(m.width * alto / m.height)
    m = m.resize((ancho, alto), Image.LANCZOS)
    lienzo = Image.new('L', (TAM, TAM), 0)
    lienzo.paste(m, ((TAM - ancho) // 2, (TAM - alto) // 2))
    return np.asarray(lienzo, dtype=np.float32) / 255.0

def ruido(sem, escala=1.0, suave=0):
    r = np.random.default_rng(sem).standard_normal((TAM, TAM)).astype(np.float32)
    if suave:
        r = np.asarray(Image.fromarray(((r * 40) + 128).clip(0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(suave)), dtype=np.float32)
        r = (r - 128) / 40
    return r * escala

def rgb(h): h = h.lstrip('#'); return np.array([int(h[i:i + 2], 16) for i in (0, 2, 4)], dtype=np.float32)

# ---- texturas: devuelven un campo de -1..1 aprox. (cuánto aclarar u oscurecer cada pixel)
def papel(sem):          # Distopía: papel con fibras y grano
    f = ruido(sem, 0.5) + ruido(sem + 1, 1.2, 6)
    y, x = np.mgrid[0:TAM, 0:TAM]
    fibras = np.sin(x * 0.9 + ruido(sem + 2, 6, 10)) * 0.25
    return f * 0.6 + fibras * 0.4

def piedra(sem):         # Gótico: piedra con manchas y poros
    return ruido(sem, 1.0, 14) * 0.9 + ruido(sem + 1, 0.4, 1) + (ruido(sem + 2, 1, 0) > 2.3) * -1.2

def marmol(sem):         # Napoleónico: mármol con vetas suaves
    y, x = np.mgrid[0:TAM, 0:TAM] / TAM
    t = ruido(sem, 6.0, 30) + ruido(sem + 5, 2.5, 12)
    vetas = np.abs(np.sin((x * 1.3 + y * 0.7) * 4 + t * 3.2)) ** 24 * -1.1
    finas = np.abs(np.sin((x * 0.6 - y * 1.1) * 9 + t * 4.5)) ** 40 * -0.5
    return vetas + finas + ruido(sem + 1, 0.3, 3) + ruido(sem + 2, 0.5, 20)

def travertino(sem):     # Romano: travertino con poros alargados en capas
    base = ruido(sem, 1.0, 8) * 0.6
    poros = np.asarray(Image.fromarray(((ruido(sem + 3, 1, 0) > 2.2) * 255).astype(np.uint8)).resize((TAM // 4, TAM)).resize((TAM, TAM)), dtype=np.float32) / 255
    y = np.mgrid[0:TAM, 0:TAM][0]
    capas = np.sin(y * 0.18 + ruido(sem + 4, 3, 12)) * 0.2
    return base + capas - poros * 1.2

def seda(sem):           # Seda: tejido fino de hilos horizontales y verticales con brillo
    y, x = np.mgrid[0:TAM, 0:TAM]
    trama = np.sin(y * 1.6) * 0.35 + np.sin(x * 1.6) * 0.15
    brillo = np.sin((x + y) * 0.02 + ruido(sem, 2, 20)) * 0.5
    return trama + brillo + ruido(sem + 1, 0.2)

def herbario(sem):       # Botánico: papel de herbario con fibras y nervaduras muy tenues
    y, x = np.mgrid[0:TAM, 0:TAM]
    nerv = (np.abs(np.sin((x - y * 0.6) * 0.035 + ruido(sem, 1.5, 16))) < 0.04) * -0.8
    return ruido(sem + 1, 0.9, 5) * 0.7 + ruido(sem + 2, 0.35) + nerv

TEXTURAS = {'distopia': papel, 'gotico': piedra, 'napoleon': marmol, 'romano': travertino, 'seda': seda, 'botanico': herbario}

# tema: (base, fondo, huella, fuerza de la textura en el fondo, en la huella)
TEMAS = {
    # ---- los seis base
    'distopia':     ('distopia', '#EFE9DA', '#1A1A1A', 10, 6),
    'gotico':       ('gotico',   '#1E1E21', '#DAD6CF', 9, 6),
    'napoleon':     ('napoleon', '#EDEBE6', '#1D2029', 12, 5),
    'romano':       ('romano',   '#E5E1D9', '#2B2A27', 10, 5),
    'seda':         ('seda',     '#F2EFE8', '#161616', 6, 5),
    'botanico':     ('botanico', '#EAE9E1', '#2D312E', 9, 6),
    # ---- complementarios: la textura de su base, con su matiz
    'ciber':        ('distopia', '#0D1117', '#C9D6E3', 5, 8),
    'espacio':      ('distopia', '#0F1012', '#E6E6E8', 4, 6),
    'laboratorio':  ('distopia', '#F1F2F1', '#25292B', 7, 5),
    'refugio':      ('distopia', '#1B1C1A', '#C8CABF', 8, 7),
    'urss':         ('distopia', '#ECE7DE', '#2A1E1E', 10, 6),
    'medieval':     ('gotico',   '#E8E2D6', '#26221D', 9, 6),
    'renacimiento': ('gotico',   '#EAE4D9', '#2A2723', 8, 5),
    'antiguas':     ('gotico',   '#1D1F1E', '#D6D3CA', 9, 6),
    'orden':        ('gotico',   '#16171A', '#CFCFCF', 8, 6),
    'revolucion':   ('napoleon', '#F1EFEA', '#1F1F24', 11, 5),
    'heleno':       ('romano',   '#EEEDE9', '#262626', 9, 5),
    'mongol':       ('seda',     '#EFEBE3', '#1E1C1A', 6, 5),
    'andalus':      ('seda',     '#F0ECE4', '#1C1D1F', 6, 5),
    'colonos':      ('botanico', '#E7E3DA', '#2E2B27', 9, 6),
    'navidad':      ('botanico', '#1C1E1D', '#E2E0DA', 8, 6),
}

def hacer(tema, salida):
    base, fondo, tinta, f_fondo, f_huella = TEMAS[tema]
    sem = sum(map(ord, tema))
    tex = TEXTURAS[base](sem)
    # Fondo: el color con la textura (pocos niveles de gris arriba y abajo).
    bg = rgb(fondo)[None, None, :] + tex[..., None] * f_fondo
    Image.fromarray(np.clip(bg, 0, 255).astype(np.uint8)).save(os.path.join(salida, f'logo_fondo_{tema}.jpg'), quality=92)
    # Huella: la forma fija, con la misma textura muy leve (tinta que se asienta en el papel, la piedra o la tela).
    m = mascara()
    t2 = TEXTURAS[base](sem + 7)
    col = rgb(tinta)[None, None, :] + t2[..., None] * f_huella
    alfa = m * np.clip(0.93 + t2 * 0.04, 0.82, 1.0)
    img = np.dstack([np.clip(col, 0, 255), (alfa * 255)]).astype(np.uint8)
    Image.fromarray(img, 'RGBA').save(os.path.join(salida, f'logo_{tema}.png'), optimize=True)

def mono(salida):
    m = mascara()
    img = np.dstack([np.full((TAM, TAM, 3), 255, np.uint8), (m * 255).astype(np.uint8)])
    Image.fromarray(img, 'RGBA').save(os.path.join(salida, 'logo_mono.png'), optimize=True)

def muestra(salida, archivo):
    cel = 160; temas = list(TEMAS)
    hoja = Image.new('RGB', (cel * 7, cel * 3), 'white')
    for i, t in enumerate(temas):
        f = Image.open(os.path.join(salida, f'logo_fondo_{t}.jpg')).convert('RGBA')
        f.alpha_composite(Image.open(os.path.join(salida, f'logo_{t}.png')))
        # Recorte circular como en la pantalla de inicio.
        c = Image.new('L', (TAM, TAM), 0)
        from PIL import ImageDraw
        ImageDraw.Draw(c).ellipse((TAM * 0.17, TAM * 0.17, TAM * 0.83, TAM * 0.83), fill=255)
        f = f.crop((int(TAM * 0.17), int(TAM * 0.17), int(TAM * 0.83), int(TAM * 0.83))).resize((cel - 10, cel - 10))
        c = c.crop((int(TAM * 0.17), int(TAM * 0.17), int(TAM * 0.83), int(TAM * 0.83))).resize((cel - 10, cel - 10))
        hoja.paste(f.convert('RGB'), ((i % 7) * cel + 5, (i // 7) * cel + 5), c)
    hoja.save(archivo)

if __name__ == '__main__':
    salida = sys.argv[1]
    for t in TEMAS: hacer(t, salida)
    mono(salida)
    if len(sys.argv) > 2: muestra(salida, sys.argv[2])
    print('listo', len(TEMAS))
