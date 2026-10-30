"""
El papel de página de cada tema (`papel_<id>.jpg`, 720 × 1280) y los ayudantes de dibujo que usa sellos.py.
Los papeles de Romano, Scriptorium, Seda y Revolución vienen de rasters viejos, guardados una vez en `fuentes/`.
Los sellos de huella están en sellos.py y los íconos en iconos.py.
Uso: python3 arte.py ../app/src/main/res/drawable-nodpi
"""
import math, sys, os, shutil
import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageChops

OUT = None                   # carpeta drawable-nodpi (la pone el uso desde la línea de órdenes)
rng = np.random.default_rng(7)

def ruido(tam, escala, semilla):
    r = np.random.default_rng(semilla)
    n = r.random((tam, tam)).astype(np.float32)
    im = Image.fromarray((n * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(escala))
    a = np.array(im).astype(np.float32)
    a = (a - a.min()) / max(1e-6, a.max() - a.min())
    return a

def ruido_rect(w, h, escala, semilla):
    r = np.random.default_rng(semilla)
    n = r.random((h, w)).astype(np.float32)
    im = Image.fromarray((n * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(escala))
    a = np.array(im).astype(np.float32)
    return (a - a.min()) / max(1e-6, a.max() - a.min())

def col(h):
    h = h.lstrip('#'); return np.array([int(h[i:i + 2], 16) for i in (0, 2, 4)], dtype=np.float32)

def mezcla(a, b, t):
    t = t[..., None] if np.ndim(t) == 2 else t
    return a * (1 - t) + b * t

def a_img(rgb):
    return Image.fromarray(np.clip(rgb, 0, 255).astype(np.uint8), 'RGB')

def a_rgba(rgb, alfa):
    arr = np.dstack([np.clip(rgb, 0, 255), np.clip(alfa * 255, 0, 255)]).astype(np.uint8)
    return Image.fromarray(arr, 'RGBA')

def relieve(m, luz=(-1, -1), fuerza=1.0):
    """Sombreado simple a partir de la máscara (emboss)."""
    b = np.array(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.6))).astype(np.float32) / 255
    gy, gx = np.gradient(b)
    s = -(gx * luz[0] + gy * luz[1]) * 6 * fuerza
    return np.clip(s, -1, 1)

def papel_antiguas():
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    borde = np.clip(np.maximum(np.abs(xx - W / 2) / (W / 2), np.abs(yy - H / 2) / (H / 2)) - 0.78, 0, 1) * 5
    # Pergamino de mapa: amarillento, con manchas y bordes oscurecidos por el uso.
    a = mezcla(col('#EDE2C6'), col('#D9C79C'), ruido_rect(W, H, 32, 91) * 0.8)
    a = mezcla(a, col('#BFA676'), np.clip((ruido_rect(W, H, 14, 92) - 0.72) * 3.5, 0, 1) * 0.4)
    a = mezcla(a, col('#8E7448'), np.clip(borde, 0, 1) * 0.7)
    a = a * (0.96 + 0.06 * ruido_rect(W, H, 0.8, 93)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_antiguas.jpg'), quality=86)

def papel_cueva():
    """Prehistoria: una piel curtida, clara y cálida como el ante, con su poro, las venas de la piel, una mancha de ocre
    donde se apoyó una mano pintada y los bordes más oscuros, como de piel raspada con un raspador de sílex."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#ECDFC8'), col('#DCC8A6'), ruido_rect(W, H, 34, 151) * 0.8)
    venas = np.abs(ruido_rect(W, H, 46, 152) - 0.5)
    a = mezcla(a, col('#C9B089'), np.clip(1 - venas / 0.012, 0, 1) * 0.3)
    poro = np.clip((ruido_rect(W, H, 1.2, 154) - 0.86) * 6, 0, 1)
    a = mezcla(a, col('#A8906A'), poro * 0.3)
    ocre = np.clip(1 - np.sqrt((xx - W * 0.84) ** 2 + (yy - H * 0.9) ** 2) / 170, 0, 1) ** 1.5 * np.clip(0.6 + ruido_rect(W, H, 1.5, 155) * 0.5, 0, 1)
    a = mezcla(a, col('#B65A34'), ocre * 0.22)
    borde = np.sqrt(((xx - W / 2) / (W / 2)) ** 4 + ((yy - H / 2) / (H / 2)) ** 4) ** 0.5 + ruido_rect(W, H, 18, 157) * 0.12
    a = mezcla(a, col('#9E8058'), np.clip((borde - 0.78) * 2.2, 0, 1) * 0.5)
    a = a * (0.96 + 0.06 * ruido_rect(W, H, 0.8, 156)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_cueva.jpg'), quality=86)

def papel_abismo():
    """El papel sintético impermeable de la bitácora del batiscafo: gris verdoso y liso, con un grano plástico muy fino,
    la red de luz del agua apenas marcada como marca de agua, manchas de humedad que se secaron y los bordes un poco
    más azules, como mojados."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#EEF4F2'), col('#DDE9E7'), ruido_rect(W, H, 40, 131) * 0.7)
    causticas = np.abs(np.sin(xx / 29 + np.sin(yy / 41) * 2.3) * np.sin(yy / 23 + np.sin(xx / 35) * 1.9))
    a = mezcla(a, col('#BFD9DC'), (causticas < 0.05) * 0.2)
    a = mezcla(a, col('#C9DCD8'), np.clip((ruido_rect(W, H, 22, 132) - 0.72) * 3.2, 0, 1) * 0.45)
    d = np.sqrt(((xx - W / 2) / (W / 2)) ** 2 + ((yy - H / 2) / (H / 2)) ** 2) / math.sqrt(2)
    a = mezcla(a, col('#9DC3CC'), np.clip((d - 0.6) / 0.4, 0, 1) * 0.45)
    a = a * (0.975 + 0.035 * ruido_rect(W, H, 0.6, 133)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_abismo.jpg'), quality=86)

def papel_pirata():
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    borde = np.clip((np.sqrt(((xx - W / 2) / (W / 2)) ** 2 + ((yy - H / 2) / (H / 2)) ** 2) / math.sqrt(2) - 0.5) * 2, 0, 1)
    # Hoja de bitácora: papel de trapo grisáceo, mojado y vuelto a secar; las cercas de sal del agua dejan bordes
    # más oscuros que el centro de cada mancha, y la brea de las manos tiñe los cantos.
    a = mezcla(col('#E9E1CB'), col('#D6CBAE'), ruido_rect(W, H, 28, 141) * 0.8)
    for k, (cx, cy, rad) in enumerate(((0.28, 0.2, 150), (0.8, 0.55, 210), (0.35, 0.86, 120), (0.9, 0.08, 90))):
        ang = np.arctan2(yy - cy * H, xx - cx * W)
        dist = np.sqrt((xx - cx * W) ** 2 + (yy - cy * H) ** 2) / (rad * (1 + 0.07 * np.sin(3 * ang + k) + 0.04 * np.sin(7 * ang + 2 * k)))
        a = mezcla(a, col('#E2D8BD'), np.clip(1 - dist, 0, 1) ** 0.5 * 0.35)
        a = mezcla(a, col('#A99366'), np.exp(-((dist - 1) * 28) ** 2) * 0.28)
    a = mezcla(a, col('#6E5A3C'), borde ** 1.5 * 0.7)
    a = a * (0.95 + 0.07 * ruido_rect(W, H, 0.7, 144)[..., None])
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA'); r = np.random.default_rng(145)
    for _ in range(220):
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 4 + r.random() * 12
        d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=(90, 74, 50, 30), width=1)
    img.save(os.path.join(OUT, 'papel_pirata.jpg'), quality=86)

def papel_ruinas():
    """Ciudad verde: papel reciclado gris verdoso de un cuaderno rescatado, con fibras, manchas de agua y la sombra de dos hojas
    de hiedra que estuvieron prensadas entre sus páginas."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#E8E6DA'), col('#D6D5C6'), ruido_rect(W, H, 26, 141) * 0.7)
    agua = np.clip((ruido_rect(W, H, 40, 142) - 0.68) * 3.5, 0, 1)
    a = mezcla(a, col('#C8C1A6'), agua * 0.45)
    borde = np.clip(np.maximum(np.abs(xx - W / 2) / (W / 2), np.abs(yy - H / 2) / (H / 2)) - 0.82, 0, 1) * 5
    a = mezcla(a, col('#A9A68E'), np.clip(borde, 0, 1) * 0.5)
    a = a * (0.95 + 0.07 * ruido_rect(W, H, 0.8, 143)[..., None])
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA'); r = np.random.default_rng(144)
    for _ in range(260):
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 4 + r.random() * 12
        d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=(90, 100, 70, 28), width=1)
    pts = [(0, 0.5), (-0.55, 0.35), (-0.8, -0.05), (-0.32, -0.05), (0, -0.5), (0.32, -0.05), (0.8, -0.05), (0.55, 0.35)]
    for cx, cy, s, ang in ((W * 0.78, H * 0.82, 170, -0.5), (W * 0.2, H * 0.16, 110, 0.7)):
        ca, sa = math.cos(ang), math.sin(ang)
        d.polygon([(cx + (px * ca - py * sa) * s, cy + (px * sa + py * ca) * s) for px, py in pts], fill=(90, 110, 60, 22))
    img.save(os.path.join(OUT, 'papel_ruinas.jpg'), quality=86)

def papel_arcano():
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    borde = np.clip(np.maximum(np.abs(xx - W / 2) / (W / 2), np.abs(yy - H / 2) / (H / 2)) - 0.82, 0, 1) * 5
    # Hoja de archivo de pueblo costero: amarillenta, con manchas de humedad que dejan cerco, papel de libro de
    # registro (los renglones los pone la página), el margen rojo desvaído, un cerco de taza y el borde oscurecido por la sal.
    a = mezcla(col('#E6DCC2'), col('#D9CCA8'), ruido_rect(W, H, 36, 141) * 0.8)
    hum = ruido_rect(W, H, 70, 142)
    a = mezcla(a, col('#C7B78E'), np.clip((hum - 0.62) * 3.0, 0, 1) * 0.4)
    cerco_h = np.exp(-((hum - 0.62) / 0.008) ** 2)
    a = mezcla(a, col('#A8946A'), np.clip(cerco_h, 0, 1) * 0.2)
    a = mezcla(a, col('#8A7A55'), np.clip(borde, 0, 1) * 0.55)
    a = a * (0.965 + 0.05 * ruido_rect(W, H, 0.8, 143)[..., None])
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
    d.line([(92, 40), (92, H - 40)], fill=(150, 60, 50, 46), width=1)
    d.line([(96, 40), (96, H - 40)], fill=(150, 60, 50, 30), width=1)
    cx, cy, rr = W * 0.74, H * 0.83, 74
    d.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], outline=(110, 84, 46, 40), width=5)
    d.arc([cx - rr + 6, cy - rr + 4, cx + rr - 2, cy + rr + 6], 200, 330, fill=(110, 84, 46, 30), width=3)
    r = np.random.default_rng(144)
    for _ in range(9):
        x, y, s = r.random() * W, r.random() * H, 2 + r.random() * 4
        d.ellipse([x - s, y - s, x + s, y + s], fill=(30, 51, 41, 40))
    img.save(os.path.join(OUT, 'papel_arcano.jpg'), quality=86)

# ------------------------------------------------------------------ papeles
def papeles():
    W, H = 720, 1280
    def guardar_p(arr, nombre): a_img(arr).save(os.path.join(OUT, nombre), quality=86)
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    borde = np.clip(np.maximum(np.abs(xx - W / 2) / (W / 2), np.abs(yy - H / 2) / (H / 2)) - 0.8, 0, 1) * 5
    # Lámina de herbario: crema con grano y manchitas de óxido.
    a = mezcla(col('#F5F0DE'), col('#E8DFC3'), ruido_rect(W, H, 40, 61) * 0.7)
    a = a * (0.96 + 0.06 * ruido_rect(W, H, 0.8, 62)[..., None])
    a = mezcla(a, col('#C9A97A'), np.clip((ruido_rect(W, H, 6, 63) - 0.8) * 5, 0, 1) * 0.35)
    guardar_p(a, 'papel_botanico.jpg')
    # Vitela de libro de horas: manchada, cálida, con bordes más oscuros.
    a = mezcla(col('#F3EAD3'), col('#E2D2AE'), ruido_rect(W, H, 30, 64))
    a = mezcla(a, col('#D7C49A'), ruido_rect(W, H, 90, 65) * 0.5)
    a = mezcla(a, col('#BFA676'), np.clip(borde, 0, 1) * 0.6)
    a = a * (0.97 + 0.05 * ruido_rect(W, H, 0.9, 66)[..., None])
    guardar_p(a, 'papel_gotico.jpg')
    # Papel de Xàtiva: blanco roto con fibras y corondeles.
    a = mezcla(col('#F3E8CC'), col('#E7D9B6'), ruido_rect(W, H, 25, 67) * 0.6)
    a = a * (0.96 + 0.05 * ruido_rect(W, H, 0.7, 68)[..., None])
    for x in range(30, W, 36): a[:, x:x + 1] *= 0.975
    for y in range(0, H, 4): a[y:y + 1, :] *= 0.992
    r = np.random.default_rng(69)
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
    for _ in range(260):
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 6 + r.random() * 16
        d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=(150, 120, 80, 40), width=1)
    img.save(os.path.join(OUT, 'papel_andalus.jpg'), quality=86)
    # Papel de la estepa: blanco cálido con textura de fieltro muy suave.
    a = mezcla(col('#F3EDDF'), col('#E6DDCB'), ruido_rect(W, H, 2.2, 70) * 0.8)
    a = mezcla(a, col('#DCD2BC'), ruido_rect(W, H, 50, 71) * 0.4)
    guardar_p(a, 'papel_mongol.jpg')
    # Cuaderno de Leonardo: papel amarillento, manchas y bordes tostados.
    a = mezcla(col('#F0E1BC'), col('#DDC594'), ruido_rect(W, H, 35, 72) * 0.8)
    a = mezcla(a, col('#C9A86E'), np.clip((ruido_rect(W, H, 18, 73) - 0.7) * 3.5, 0, 1) * 0.45)
    a = mezcla(a, col('#A98754'), np.clip(borde, 0, 1) * 0.7)
    a = a * (0.96 + 0.06 * ruido_rect(W, H, 0.8, 74)[..., None])
    guardar_p(a, 'papel_renacimiento.jpg')

# ------------------------------------------------------------------ Ciudad neón (lote 8)
def papel_neon():
    """El reverso de un ticket mojado bajo los letreros: violeta muy oscuro, con el reflejo magenta arriba, el ámbar
    abajo, el grano del papel térmico y unas gotas que se secaron."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#140D1A'), col('#0C0811'), ruido_rect(W, H, 80, 201) * 0.6)
    arriba = np.clip(1 - np.sqrt(((xx - W * 0.8) / (W * 0.7)) ** 2 + (yy / (H * 0.35)) ** 2), 0, 1) ** 2
    abajo = np.clip(1 - np.sqrt((xx / (W * 0.6)) ** 2 + ((yy - H) / (H * 0.3)) ** 2), 0, 1) ** 2
    a = mezcla(a, col('#5A1A42'), arriba * 0.55)
    a = mezcla(a, col('#5A3A12'), abajo * 0.4)
    r = np.random.default_rng(202)
    for _ in range(40):
        cx, cy, rr = r.random() * W, r.random() * H, 4 + r.random() * 14
        d = np.sqrt((xx - cx) ** 2 + (yy - cy) ** 2)
        a = mezcla(a, col('#24182C'), np.clip(1 - np.abs(d - rr) / 1.5, 0, 1) * 0.5)
    a = a * (0.97 + 0.05 * ruido_rect(W, H, 0.8, 203)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_neon.jpg'), quality=86)

# ------------------------------------------------------------------ papeles del lote 7 (los temas que no tenían)
def papeles_lote7():
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    def guardar_p(arr, nombre): a_img(arr).save(os.path.join(OUT, nombre), quality=86)
    def vineta(fuerza, desde=0.55):
        d = np.sqrt(((xx - W / 2) / (W / 2)) ** 2 + ((yy - H / 2) / (H / 2)) ** 2) / math.sqrt(2)
        return np.clip((d - desde) / (1 - desde), 0, 1) * fuerza
    grano = lambda semilla, k=0.05: (1 - k / 2 + k * ruido_rect(W, H, 0.8, semilla))[..., None]

    # Terminal: vidrio oscuro de monitor, con la rejilla de píxeles apenas visible y el centro un poco más claro.
    a = mezcla(col('#070B12'), col('#03050A'), vineta(1.0, 0.2))
    a = mezcla(a, col('#0C1A24'), ruido_rect(W, H, 60, 101) * 0.35)
    a[::6, :] = a[::6, :] * 0.85 + col('#0A2730') * 0.15
    a[:, ::6] = a[:, ::6] * 0.9 + col('#0A2730') * 0.1
    guardar_p(a * grano(102, 0.06), 'papel_ciber.jpg')

    # Heleno: pergamino de Pérgamo, liso y claro, con manchas suaves de la piel y sus poros.
    a = mezcla(col('#F5ECD7'), col('#E9DCBF'), ruido_rect(W, H, 70, 103) * 0.7)
    a = mezcla(a, col('#DCC9A2'), np.clip((ruido_rect(W, H, 22, 104) - 0.65) * 2.5, 0, 1) * 0.35)
    poros = np.random.default_rng(105).random((H, W)) > 0.9975
    a[poros] *= 0.86
    a = mezcla(a, col('#CDB58C'), vineta(0.5))
    guardar_p(a * grano(106, 0.04), 'papel_heleno.jpg')

    # Distopía: papel de oficio grisáceo, con grano y las marcas tenues de sellos viejos.
    a = mezcla(col('#EEEADF'), col('#DEDACD'), ruido_rect(W, H, 18, 107) * 0.6)
    img = a_img(a * grano(108, 0.08)); d = ImageDraw.Draw(img, 'RGBA'); r = np.random.default_rng(109)
    for _ in range(5):
        x, y, rad = r.random() * W, r.random() * H, 50 + r.random() * 40
        d.ellipse([x - rad, y - rad, x + rad, y + rad], outline=(140, 40, 50, 18), width=5)
        d.ellipse([x - rad * 0.72, y - rad * 0.72, x + rad * 0.72, y + rad * 0.72], outline=(140, 40, 50, 14), width=2)
    img.save(os.path.join(OUT, 'papel_distopia.jpg'), quality=86)

    # Refugio: la pantalla de fósforo verde, con su brillo en el centro y el polvo del cristal.
    a = mezcla(col('#05200F'), col('#020D05'), vineta(1.0, 0.15))
    a = mezcla(a, col('#0B3A1C'), np.clip(1 - vineta(1.0, 0.0) * 1.6, 0, 1) * 0.35)
    polvo = np.random.default_rng(110).random((H, W)) > 0.998
    a[polvo] = a[polvo] * 0.5 + col('#2F8F4E') * 0.5
    guardar_p(a * grano(111, 0.07), 'papel_refugio.jpg')

    # Orden: placa de acero cepillado, oscura, con las rayas del cepillo en horizontal.
    rayas = ruido_rect(W, H, 0.6, 112)
    rayas = np.array(Image.fromarray((rayas * 255).astype(np.uint8)).filter(ImageFilter.BoxBlur(0)).resize((W // 24, H), Image.BILINEAR).resize((W, H), Image.BILINEAR)).astype(np.float32) / 255
    a = mezcla(col('#20262D'), col('#151A20'), rayas * 0.9)
    a = mezcla(a, col('#2C343D'), ruido_rect(W, H, 80, 113) * 0.4)
    a = mezcla(a, col('#0E1115'), vineta(0.6))
    guardar_p(a, 'papel_orden.jpg')

    # Laboratorio: papel blanco de cuaderno de laboratorio, con fibras azuladas muy finas (la cuadrícula la pone la app).
    a = mezcla(col('#F8FBFC'), col('#EDF3F6'), ruido_rect(W, H, 30, 114) * 0.5)
    img = a_img(a * grano(115, 0.03)); d = ImageDraw.Draw(img, 'RGBA'); r = np.random.default_rng(116)
    for _ in range(140):
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 4 + r.random() * 10
        d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=(90, 130, 170, 26), width=1)
    img.save(os.path.join(OUT, 'papel_laboratorio.jpg'), quality=86)

    # Colonos: papel de gaceta con fibras y la retícula tenue de las columnas.
    a = mezcla(col('#F3EBD7'), col('#E4D8BC'), ruido_rect(W, H, 12, 117) * 0.7)
    a = a * grano(118, 0.09)
    for x in (W // 3, 2 * W // 3): a[:, x - 1:x + 1] *= 0.94
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA'); r = np.random.default_rng(119)
    for _ in range(320):
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 5 + r.random() * 14
        d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=(110, 90, 60, 30), width=1)
    img.save(os.path.join(OUT, 'papel_colonos.jpg'), quality=86)

    # Napoleónico: papel verjurado del despacho: los puntizones finos en horizontal (los corondeles los pone la app).
    a = mezcla(col('#F7F1E2'), col('#EAE0C8'), ruido_rect(W, H, 40, 120) * 0.5)
    for y in range(0, H, 3): a[y:y + 1, :] *= 0.985
    a = mezcla(a, col('#D9C9A2'), vineta(0.45))
    guardar_p(a * grano(121, 0.04), 'papel_napoleon.jpg')

    # Navidad: papel de carta crema con motas de oro y de rojo, como el de las tarjetas.
    a = mezcla(col('#FCF7ED'), col('#F1E7D3'), ruido_rect(W, H, 50, 122) * 0.5)
    img = a_img(a * grano(123, 0.03)); d = ImageDraw.Draw(img, 'RGBA'); r = np.random.default_rng(124)
    for _ in range(420):
        x, y = r.random() * W, r.random() * H; rad = 0.8 + r.random() * 1.6
        c = (201, 162, 39, 90) if r.random() < 0.7 else (183, 28, 28, 60)
        d.ellipse([x - rad, y - rad, x + rad, y + rad], fill=c)
    img.save(os.path.join(OUT, 'papel_navidad.jpg'), quality=86)

    # Espacio: el azul de la cabina con nubes de nebulosa muy tenues (las estrellas las pone la app).
    a = mezcla(col('#060C1C'), col('#03060F'), vineta(1.0, 0.2))
    a = mezcla(a, col('#1A1640'), np.clip((ruido_rect(W, H, 70, 125) - 0.55) * 2.2, 0, 1) * 0.45)
    a = mezcla(a, col('#0E2A48'), np.clip((ruido_rect(W, H, 50, 126) - 0.6) * 2.2, 0, 1) * 0.35)
    guardar_p(a * grano(127, 0.05), 'papel_espacio.jpg')

    # Soviético: papel barato de cuaderno escolar, amarillento y con motas de fibra reciclada.
    a = mezcla(col('#F0E7D3'), col('#E2D6BB'), ruido_rect(W, H, 8, 128) * 0.55)
    motas = np.random.default_rng(129).random((H, W)) > 0.9985
    a[motas] = a[motas] * 0.55 + col('#6A625A') * 0.45
    guardar_p(a * grano(130, 0.1), 'papel_urss.jpg')

def papel_egipto():
    """Egipto: papiro. Las tiras de la médula en dos capas cruzadas (las horizontales, encima, más marcadas), fibras finas,
    las uniones de las hojas pegadas que forman el rollo y alguna mota oscura de la planta."""
    W, H = 720, 1280
    r = np.random.default_rng(141)
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#ECDDAA'), col('#DCC78C'), ruido_rect(W, H, 60, 142) * 0.7)
    var = 0.6 + 0.8 * ruido_rect(W, H, 40, 144)[..., None]     # las tiras no son parejas: el tono cambia a lo largo
    y = 0
    while y < H:
        alto = int(r.integers(5, 15)); t = (r.random() - 0.5) * 0.07
        a[y:y + alto] *= (1 + t * var[y:y + alto]); a[y:y + 1] *= 0.96
        y += alto
    x = 0
    while x < W:
        ancho = int(r.integers(10, 26)); t = (r.random() - 0.5) * 0.02
        a[:, x:x + ancho] *= (1 + t); x += ancho
    fib = ruido_rect(W, H, 0.7, 143)
    fib = np.array(Image.fromarray((fib * 255).astype(np.uint8)).resize((W // 30, H), Image.BILINEAR).resize((W, H), Image.BILINEAR)).astype(np.float32) / 255
    a = a * (0.95 + 0.08 * fib[..., None])
    for x0 in (250, 530):
        a[:, x0:x0 + 14] *= 0.94; a[:, x0:x0 + 2] *= 0.9
    motas = r.random((H, W)) > 0.9985
    a[motas] = a[motas] * 0.6 + col('#5A4524') * 0.4
    dist = np.sqrt(((xx - W / 2) / (W / 2)) ** 2 + ((yy - H / 2) / (H / 2)) ** 2) / math.sqrt(2)
    a = mezcla(a, col('#B99C5E'), np.clip((dist - 0.55) / 0.45, 0, 1) * 0.45)
    a_img(a).save(os.path.join(OUT, 'papel_egipto.jpg'), quality=86)

def papeles_fuente():
    """Los papeles que vienen de un raster viejo: se copian tal cual desde `fuentes/`."""
    aqui = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'fuentes')
    for t in ('romano', 'medieval', 'seda', 'revolucion'):
        shutil.copyfile(os.path.join(aqui, f'papel_{t}.jpg'), os.path.join(OUT, f'papel_{t}.jpg'))

def papel_templario():
    """La regla de la casa: pergamino blanqueado, polvo de arena del camino, la filigrana tenue de la cruz patada al centro
    y los bordes tostados por el sol de la frontera."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#F3EDDF'), col('#E4D9C0'), ruido_rect(W, H, 40, 141) * 0.7)
    a = mezcla(a, col('#D8C49A'), np.clip((ruido_rect(W, H, 20, 142) - 0.7) * 3, 0, 1) * 0.35)
    arena = np.random.default_rng(143).random((H, W)) > 0.997
    a[arena] = a[arena] * 0.6 + col('#B08A52') * 0.4
    d = np.sqrt(((xx - W / 2) / (W / 2)) ** 2 + ((yy - H / 2) / (H / 2)) ** 2) / math.sqrt(2)
    a = mezcla(a, col('#B99B6A'), np.clip((d - 0.6) / 0.4, 0, 1) * 0.55)
    filigrana = Image.new('L', (W, H), 0); dl = ImageDraw.Draw(filigrana)
    cx, cy, r = W / 2, H / 2, W * 0.28; pts = []
    for k in range(4):
        u = math.radians(90 * k - 90); dx, dy = math.cos(u), math.sin(u); nx, ny = -dy, dx
        for f_, q in ((0.15, -0.15), (1, -0.42), (1, 0.42)):
            pts.append((cx + (dx * f_ + nx * q) * r, cy + (dy * f_ + ny * q) * r))
    dl.polygon(pts, fill=255)
    fil = np.asarray(filigrana.filter(ImageFilter.GaussianBlur(3)), np.float32) / 255
    a = a * (1 - 0.035 * fil[..., None])
    a = a * (0.97 + 0.05 * ruido_rect(W, H, 0.8, 144)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_templario.jpg'), quality=86)

def papel_urbe():
    """Roma: la hoja de un cuaderno comprado en la papelería del barrio, crema y algo reciclada, con el cerco claro
    que dejó una tazzina de café y el pliegue suave de haberlo llevado en la bolsa."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#F7EFDD'), col('#ECE0C6'), ruido_rect(W, H, 50, 141) * 0.7)
    motas = np.random.default_rng(142).random((H, W)) > 0.9988
    a[motas] = a[motas] * 0.7 + col('#9C8460') * 0.3
    # El cerco de la tazzina: un anillo marrón muy tenue, más oscuro en el borde, y no del todo cerrado.
    cx, cy, rad = W * 0.74, H * 0.83, W * 0.13
    d = np.sqrt((xx - cx) ** 2 + (yy - cy) ** 2)
    ang = np.arctan2(yy - cy, xx - cx)
    anillo = np.clip(1 - np.abs(d - rad) / 4, 0, 1) * np.clip(0.3 + 0.7 * np.sin(ang + 0.8), 0, 1)
    dentro = (d < rad) * ruido_rect(W, H, 18, 144) * 0.35
    a = mezcla(a, col('#B98E5E'), np.clip(anillo * 0.32 + dentro * 0.18, 0, 1))
    # El pliegue: una franja apenas más oscura con su lado iluminado.
    py = H * 0.42 + (xx - W / 2) * 0.03
    a = a * (1 - 0.03 * np.exp(-((yy - py) / 28) ** 2) + 0.015 * np.exp(-((yy - py - 40) / 30) ** 2))[..., None]
    a = a * (0.97 + 0.05 * ruido_rect(W, H, 0.8, 143)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_urbe.jpg'), quality=86)

if __name__ == '__main__':
    OUT = sys.argv[1]
    papeles()
    papel_ruinas()
    papel_antiguas()
    papel_cueva()
    papel_pirata()
    papel_arcano()
    papeles_lote7()
    papel_neon()
    papel_abismo()
    papel_egipto()
    papeles_fuente()
    papel_templario()
    papel_urbe()
    print('papeles listos')
