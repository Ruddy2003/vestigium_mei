"""
El papel de página de cada tema (`papel_<id>.jpg`, 720 × 1280) y los ayudantes de dibujo que usa sellos.py.
Los papeles de Romano, Scriptorium, Seda y Revolución vienen de rasters viejos, guardados una vez en `fuentes/`.
Los sellos de huella están en sellos.py y los íconos en iconos.py.
Uso: python3 arte.py ../app/src/main/res/drawable-nodpi
     python3 arte.py ../app/src/main/res/drawable-nodpi frente     solo el papel de ese tema (o de varios, con comas)
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

def papel_meso():
    """Mesoamérica: la página de un códice. El papel amate (corteza golpeada: una red de fibras que se cruzan sin orden,
    más oscuras y torcidas) asoma por debajo del estuco blanco que lo cubre, más gastado hacia los bordes; alguna grieta
    fina del estuco y motas de corteza."""
    W, H = 720, 1280
    r = np.random.default_rng(260)
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    amate = mezcla(col('#CDB283'), col('#B99B6A'), ruido_rect(W, H, 50, 261) * 0.8)
    fib = Image.new('L', (W, H), 0); d = ImageDraw.Draw(fib)
    for _ in range(2600):
        x, y = r.random() * W, r.random() * H
        a = (r.random() - 0.5) * 0.8 + (0 if r.random() < 0.5 else math.pi / 2); l = 20 + r.random() * 90
        pts = [(x, y)]
        for k in range(4):
            a += (r.random() - 0.5) * 0.3; x += math.cos(a) * l / 4; y += math.sin(a) * l / 4; pts.append((x, y))
        d.line(pts, fill=int(120 + r.random() * 135), width=1 + int(r.random() * 2))
    fib = np.asarray(fib.filter(ImageFilter.GaussianBlur(0.9)), np.float32) / 255
    amate = amate * (1 - 0.18 * fib[..., None])
    # El estuco: cubre casi todo en el centro y se gasta hacia los bordes, dejando ver el amate.
    dist = np.sqrt(((xx - W / 2) / (W / 2)) ** 2 + ((yy - H / 2) / (H / 2)) ** 2) / math.sqrt(2)
    capa = np.clip(0.92 - np.clip((dist - 0.5) / 0.5, 0, 1) * 0.55 - (ruido_rect(W, H, 18, 262) - 0.5) * 0.35, 0.25, 0.95)
    estuco = mezcla(col('#F3EAD4'), col('#E8DCC0'), ruido_rect(W, H, 30, 263) * 0.8)
    a = mezcla(amate, estuco, capa)
    a = a * (1 - 0.05 * fib[..., None])
    # Grietas finas del estuco.
    gr = Image.new('L', (W, H), 0); dg = ImageDraw.Draw(gr)
    for _ in range(14):
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi * 2
        pts = [(x, y)]
        for k in range(10):
            ang += (r.random() - 0.5) * 1.1; x += math.cos(ang) * 9; y += math.sin(ang) * 9; pts.append((x, y))
        dg.line(pts, fill=255, width=1)
    gr = np.asarray(gr.filter(ImageFilter.GaussianBlur(0.5)), np.float32) / 255
    a = mezcla(a, col('#8C7350'), gr * 0.35)
    motas = r.random((H, W)) > 0.9988
    a[motas] = a[motas] * 0.6 + col('#5A4024') * 0.4
    a_img(a).save(os.path.join(OUT, 'papel_meso.jpg'), quality=86)


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

def papel_deco():
    """Art déco: el papel de cartas del salón de escribir de un trasatlántico, marfil y satinado, con el membrete del
    abanico solar en oro arriba, tres filetes que se acortan en escalones y un doble filete alrededor con las esquinas
    escalonadas."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#FBF6EA'), col('#F0E6CF'), ruido_rect(W, H, 60, 151) * 0.55)
    a = a * (0.985 + 0.02 * ruido_rect(W, H, 0.7, 152)[..., None])
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
    oro = (184, 149, 46)
    # El membrete: el abanico de rayos alternos sobre su base, y los filetes escalonados debajo.
    cx, cy, r = W / 2, 118, 52
    for i in range(13):
        if i % 2 == 0: d.pieslice([cx - r, cy - r, cx + r, cy + r], 180 + i * 180 / 13, 180 + (i + 1) * 180 / 13, fill=oro + (120,))
    d.pieslice([cx - r * 0.34, cy - r * 0.34, cx + r * 0.34, cy + r * 0.34], 180, 360, fill=(31, 107, 87, 110))
    d.rectangle([cx - r - 6, cy, cx + r + 6, cy + 3], fill=oro + (130,))
    for k, f in enumerate((0.62, 0.44, 0.26)):
        y = cy + 16 + k * 8
        d.line([(W * (1 - f) / 2, y), (W * (1 + f) / 2, y)], fill=oro + (110 - k * 20,), width=2)
    # El marco: doble filete y tres escalones en cada esquina.
    m, paso = 26, 12
    d.rectangle([m, m, W - m, H - m], outline=oro + (95,), width=2)
    d.rectangle([m + 7, m + 7, W - m - 7, H - m - 7], outline=oro + (60,), width=1)
    for (ox, oy, sx, sy) in ((m + 7, m + 7, 1, 1), (W - m - 7, m + 7, -1, 1), (m + 7, H - m - 7, 1, -1), (W - m - 7, H - m - 7, -1, -1)):
        pts = [(ox, oy + sy * 4 * paso)]
        for k in range(1, 4):
            pts += [(ox + sx * k * paso, oy + sy * (5 - k) * paso), (ox + sx * k * paso, oy + sy * (4 - k) * paso)]
        pts += [(ox + sx * 4 * paso, oy + sy * paso), (ox + sx * 4 * paso, oy)]
        d.line(pts, fill=oro + (110,), width=2)
    img.save(os.path.join(OUT, 'papel_deco.jpg'), quality=86)

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

def papel_edo():
    """Japón Edo: washi de kōzo, blanco cálido, con las marcas finas de la estera de bambú (su), unas nubes de pulpa
    más espesa y fibras largas y curvas, más claras y más oscuras que el papel, enredadas como en el papel hecho a mano."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#F4EEE1'), col('#E9E0CC'), ruido_rect(W, H, 45, 131) * 0.6)
    a = mezcla(a, col('#DED2B8'), np.clip((ruido_rect(W, H, 16, 132) - 0.62) * 2.5, 0, 1) * 0.3)
    for y in range(0, H, 3): a[y:y + 1, :] *= 0.992
    for x in range(20, W, 54): a[:, x:x + 1] *= 0.985
    d2 = np.sqrt(((xx - W / 2) / (W / 2)) ** 2 + ((yy - H / 2) / (H / 2)) ** 2) / math.sqrt(2)
    a = mezcla(a, col('#D8CBB0'), np.clip((d2 - 0.6) / 0.4, 0, 1) * 0.35)
    a = a * (0.98 + 0.03 * ruido_rect(W, H, 0.8, 133)[..., None])
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA'); r = np.random.default_rng(134)
    for _ in range(380):
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 14 + r.random() * 46
        f = r.random() * 6; c = (255, 252, 244, 70) if r.random() < 0.45 else (150, 128, 92, 34)
        d.line([(x + t * l * math.cos(ang) + 2.5 * math.sin(t * 5 + f), y + t * l * math.sin(ang) + 2.5 * math.cos(t * 4 + f))
                for t in np.linspace(0, 1, 9)], fill=c, width=1)
    img.save(os.path.join(OUT, 'papel_edo.jpg'), quality=86)

def papel_vapor():
    """Vapor victoriano: el papel de oficio crema de una memoria de patente, con las manchas pardas de la humedad
    (foxing), la huella de hollín de unos dedos en la esquina de abajo y, arriba a la derecha, el sello en seco de la
    oficina de patentes: un anillo con un engrane, en relieve sin tinta."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#F3EAD4'), col('#E6D8B8'), ruido_rect(W, H, 60, 151) * 0.75)
    # Las manchas de humedad: puntos pardos de borde difuso, más en los márgenes.
    rng = np.random.default_rng(152)
    for _ in range(26):
        cx, cy = rng.random() * W, rng.random() * H
        r = 3 + rng.random() * 10
        d = np.sqrt((xx - cx) ** 2 + (yy - cy) ** 2)
        borde = min(cx, W - cx, cy, H - cy) / (W / 2)
        a = mezcla(a, col('#B08A55'), np.clip(1 - d / r, 0, 1) ** 1.5 * (0.35 - 0.2 * min(borde, 1)))
    # El hollín de los dedos del fogonero, abajo a la izquierda: dos o tres manchas alargadas y grises.
    for k, (cx, cy) in enumerate(((W * 0.1, H * 0.93), (W * 0.16, H * 0.955), (W * 0.06, H * 0.9))):
        d = np.sqrt(((xx - cx) / 1.6) ** 2 + (yy - cy) ** 2)
        a = mezcla(a, col('#4A443C'), np.clip(1 - d / (24 - k * 4), 0, 1) * (0.5 + 0.5 * ruido_rect(W, H, 5, 153 + k)) * 0.4)
    # El sello en seco: un anillo con dientes de engrane, en relieve (luz arriba a la izquierda, sombra abajo a la derecha).
    sx, sy, sr = W * 0.8, H * 0.1, W * 0.085
    d = np.sqrt((xx - sx) ** 2 + (yy - sy) ** 2)
    ang = np.arctan2(yy - sy, xx - sx)
    dientes = sr * 0.62 + sr * 0.08 * (np.cos(ang * 12) > 0)
    figura = (np.abs(d - sr) < 3).astype(np.float32) + ((d < dientes) & (d > sr * 0.3)).astype(np.float32)
    figura = np.clip(figura, 0, 1)
    gy, gx = np.gradient(np.asarray(Image.fromarray((figura * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.5)), np.float32) / 255)
    a = a * (1 + np.clip(-(gx + gy) * 3.5, -0.08, 0.08))[..., None]
    a = a * (0.97 + 0.05 * ruido_rect(W, H, 0.8, 156)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_vapor.jpg'), quality=86)

def papel_frente():
    """Frente exterior: un despacho cifrado sacado del teleimpresor de la célula. Papel continuo color hueso, con las
    franjas verdes muy pálidas del papel de impresora de los 70, las dos tiras de agujeros de arrastre y su perforado,
    el doblez del medio (el despacho viajó doblado en un bolsillo), el desgaste de los bordes y, arriba a la derecha, el
    emblema de la célula en seco, sin tinta: la chispa de tres trazos en el círculo roto."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#F1ECE1'), col('#E3DDCF'), ruido_rect(W, H, 50, 161) * 0.7)
    # Las franjas verdes del papel continuo: dos renglones de cada color, con el borde apenas suavizado.
    franja = 0.5 + 0.5 * np.tanh(np.sin(yy / 64 * math.pi) * 8)
    a = mezcla(a, col('#D3DFCE'), franja * 0.17)
    # Las tiras de arrastre: un agujero cada media pulgada y la línea perforada que las separa.
    margen = 30
    for xc in (margen / 2, W - margen / 2):
        for yc in np.arange(16, H, 32):
            d = np.sqrt((xx - xc) ** 2 + (yy - yc) ** 2)
            a = mezcla(a, col('#BDB5A5'), np.clip(1 - np.abs(d - 5.6) / 1.1, 0, 1) * 0.55)
            a = mezcla(a, col('#D9D2C3'), np.clip(1 - d / 5.4, 0, 1) * 0.9)
    for xp in (margen, W - margen):
        a = mezcla(a, col('#A79F90'), ((np.abs(xx - xp) < 0.9) & ((yy % 6) < 3)).astype(np.float32) * 0.45)
    a = mezcla(a, col('#F1ECE1'), ((xx < margen) | (xx > W - margen)).astype(np.float32) * 0.5 * franja)
    # El doblez: una línea de sombra con su filo de luz y una pendiente suave a cada lado.
    yd = H * 0.5 + 6 * np.sin(xx / W * math.pi * 1.3)
    s = yy - yd
    a = a * (1 - 0.05 * np.exp(-(s / 1.4) ** 2) + 0.03 * np.exp(-((s - 2.5) / 1.6) ** 2) - 0.018 * np.tanh(s / 40) * np.exp(-np.abs(s) / 120))[..., None]
    # El desgaste: bordes un poco más oscuros y sucios, y una mancha de dedo con grasa de máquina abajo a la derecha.
    borde = np.sqrt(((xx - W / 2) / (W / 2)) ** 6 + ((yy - H / 2) / (H / 2)) ** 6) ** (1 / 6) + ruido_rect(W, H, 14, 162) * 0.08
    a = mezcla(a, col('#B9AF9C'), np.clip((borde - 0.9) * 4, 0, 1) * 0.4)
    dedo = np.sqrt(((xx - W * 0.83) / 1.3) ** 2 + (yy - H * 0.91) ** 2)
    a = mezcla(a, col('#8E8676'), np.clip(1 - dedo / 26, 0, 1) * (0.4 + 0.6 * ruido_rect(W, H, 2.5, 163)) * 0.22)
    # El emblema en seco: el círculo roto y la chispa, en relieve (luz arriba a la izquierda).
    sx, sy, sr = W * 0.8, H * 0.085, W * 0.07
    d = np.sqrt((xx - sx) ** 2 + (yy - sy) ** 2)
    ang = np.arctan2(yy - sy, xx - sx)
    hueco = np.abs((ang + 0.95 + math.pi) % (2 * math.pi) - math.pi) < 0.32
    figura = ((np.abs(d - sr) < 3.2) & ~hueco).astype(np.float32)
    ox, oy = sx + (sr - 14) * math.cos(-0.95), sy + (sr - 14) * math.sin(-0.95)
    for da, l0, l1 in ((0, 13, 40), (-0.62, 15, 34), (0.62, 15, 34)):
        u = -0.95 + da
        # Distancia a cada trazo de la chispa (un segmento).
        px, py = xx - ox, yy - oy
        t_ = np.clip(px * math.cos(u) + py * math.sin(u), l0, l1)
        dseg = np.sqrt((px - t_ * math.cos(u)) ** 2 + (py - t_ * math.sin(u)) ** 2)
        figura = np.maximum(figura, (dseg < 2.4).astype(np.float32))
    gy, gx = np.gradient(np.asarray(Image.fromarray((figura * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.6)), np.float32) / 255)
    a = a * (1 + np.clip(-(gx + gy) * 3.5, -0.08, 0.08))[..., None]
    a = a * (0.97 + 0.05 * ruido_rect(W, H, 0.8, 164)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_frente.jpg'), quality=86)

def papel_arcontes():
    """Arcontes: una hoja de un códice de papiro, como los de Nag Hammadi, en el color lino y caliza de la guía. Arriba,
    las tiras de médula horizontales (cada una con su densidad, su filo y sus fibras finas a lo largo, que no corren del
    todo derechas); debajo, las verticales, que se adivinan por transparencia; las motas oscuras de los haces de la
    planta, alguna fibra suelta más oscura, una unión de hojas (kóllesis), el doblez del cuadernillo a la izquierda con
    los agujeros del cosido, y los bordes gastados: tostados, un poco deshilachados y con algún mordisco."""
    W, H = 720, 1280
    r = np.random.default_rng(171)
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    yi, xi = np.mgrid[0:H, 0:W]
    claro, denso = col('#F3ECD9'), col('#DCCBA4')
    def estirado(semilla, sx, sy):
        """Ruido de 0 a 1 estirado sx veces a lo ancho y sy a lo alto (fibras)."""
        n = np.random.default_rng(semilla).random((max(2, H // sy), max(2, W // sx))).astype(np.float32)
        return np.asarray(Image.fromarray((n * 255).astype(np.uint8)).resize((W, H), Image.BICUBIC), np.float32) / 255
    # Las fibras no corren derechas: cada columna se corre unos píxeles arriba o abajo, despacio.
    corre = ((ruido_rect(W, H, 40, 172) - 0.5) * 22 + (ruido_rect(W, H, 9, 173) - 0.5) * 3).astype(np.int32)
    yc = np.clip(yi + corre, 0, H - 1)
    # Densidad: lo que hace la hoja más oscura y parda (más médula, menos luz que pasa).
    dens = ruido_rect(W, H, 90, 174) * 0.35 + ruido_rect(W, H, 30, 175) * 0.2
    # Las tiras horizontales de arriba: anchos desparejos, cada una con su densidad y su filo.
    tira = np.zeros(H, np.float32); filo = np.zeros(H, np.float32); y = -int(r.integers(0, 30))
    while y < H:
        alto = int(r.integers(22, 48)); y0, y1 = max(y, 0), min(y + alto, H)
        if y1 > y0: tira[y0:y1] = r.random() * 0.22
        if 0 <= y + alto < H: filo[y + alto] = 0.5 + r.random() * 0.5
        y += alto
    filo = np.convolve(filo, np.array([0.25, 1, 0.45, 0.15]), mode='same')
    dens = dens + tira[yc] * (0.6 + 0.8 * ruido_rect(W, H, 45, 176)) + filo[yc] * 0.16
    # Las fibras finas de las tiras, a lo largo (varias escalas) y, más tenues y anchas, las de la capa de abajo.
    fh = estirado(177, 70, 1) * 0.5 + estirado(178, 26, 1) * 0.3 + estirado(179, 9, 1) * 0.2
    fh = fh[yc, xi]
    dens = dens + (fh - 0.5) * 0.26
    tira_v = np.zeros(W, np.float32); x = -int(r.integers(0, 40))
    while x < W:
        ancho = int(r.integers(34, 80)); x0, x1 = max(x, 0), min(x + ancho, W)
        if x1 > x0: tira_v[x0:x1] = r.random() * 0.07
        if 0 <= x + ancho < W: tira_v[x + ancho] += 0.06
        x += ancho
    tira_v = np.convolve(tira_v, np.ones(5) / 5, mode='same')
    dens = dens + tira_v[None, :] + (estirado(180, 2, 120) - 0.5) * 0.05
    # Las motas de los haces de la planta: puntitos pardos alargados en el sentido de la fibra.
    motas = Image.new('L', (W * 2, H * 2), 0); dm = ImageDraw.Draw(motas)
    for _ in range(900):
        x0, y0 = r.random() * W * 2, r.random() * H * 2; l = 2 + r.random() * 7
        dm.line([(x0, y0), (x0 + l, y0 + (r.random() - 0.5))], fill=int(80 + r.random() * 150), width=2)
    # Fibras sueltas más oscuras, largas y finas, casi siempre horizontales.
    for _ in range(45):
        x0, y0 = r.random() * W * 2, r.random() * H * 2
        vertical = r.random() > 0.9
        ang = (r.random() - 0.5) * 0.05 + (math.pi / 2 if vertical else 0)
        largo = (80 + r.random() * (120 if vertical else 300)) * 2; fase = r.random() * 6; pts = []
        for k in range(16):
            t_ = k / 15; o = math.sin(t_ * (9 if vertical else 5) + fase) * (5 if vertical else 2.5)
            pts.append((x0 + math.cos(ang) * largo * t_ - math.sin(ang) * o, y0 + math.sin(ang) * largo * t_ + math.cos(ang) * o))
        dm.line(pts, fill=int(60 + r.random() * 110), width=2)
    motas = np.asarray(motas.filter(ImageFilter.GaussianBlur(0.9)).resize((W, H), Image.LANCZOS), np.float32) / 255
    # La kóllesis: la unión de dos hojas del rollo del que se cortó el códice, una banda vertical un poco más densa.
    xk = W * 0.64 + 5 * np.sin(yy / H * math.pi * 2.3)
    dens = dens + np.clip(1 - np.abs(xx - xk) / 14, 0, 1) ** 0.7 * 0.08 + np.exp(-((xx - xk - 14) / 1.4) ** 2) * 0.1
    a = mezcla(claro[None, None, :] + np.zeros((H, W, 3), np.float32), denso, np.clip(dens, 0, 1))
    a = mezcla(a, col('#8E6E44'), motas * 0.4)
    # El doblez del cuadernillo, a la izquierda: la hoja se curva hacia el lomo (una sombra que crece hacia el canto, con
    # el pliegue marcado) y, cerca, los agujeros del cosido de la correa de cuero.
    xd = 22 + 1.2 * np.sin(yy / H * math.pi * 1.7)
    a = a * (1 - 0.13 * np.exp(-xx / 13) - 0.04 * np.exp(-((xx - 4) / 2.5) ** 2) + 0.015 * np.exp(-((xx - 10) / 4) ** 2))[..., None]
    for yc_ in (H * 0.22, H * 0.41, H * 0.59, H * 0.78):
        yc_ += r.normal(0, 6)
        d = np.sqrt((xx - xd) ** 2 * 1.5 + (yy - yc_) ** 2)
        a = mezcla(a, col('#9A7F58'), np.clip(1 - np.abs(d - 3.0) / 1.3, 0, 1) * 0.3)
        a = mezcla(a, col('#6A5538'), np.clip(1 - d / 2.4, 0, 1) * 0.7)
    # Manchas de la edad, pocas y tenues, más hacia los bordes.
    borde = np.sqrt(((xx - W / 2) / (W / 2)) ** 4 + ((yy - H / 2) / (H / 2)) ** 4) ** 0.25
    manchas = np.clip((ruido_rect(W, H, 30, 181) - 0.66) * 3.0, 0, 1) * np.clip(borde * 1.4 - 0.5, 0, 1)
    a = mezcla(a, col('#CDB27F'), manchas * 0.3)
    # Los bordes gastados: el contorno de la hoja, irregular (las fibras horizontales se sueltan un poco en los cantos
    # de los lados), con algún mordisco; el canto, tostado; por fuera, la caliza en sombra.
    irreg = (ruido_rect(W, H, 10, 182) - 0.5) * 10 + (ruido_rect(W, H, 2.5, 183) - 0.5) * 4
    hilachas = np.clip((estirado(184, 14, 1)[yc, xi] - 0.62) * 3, 0, 1) * (ruido_rect(W, H, 6, 187) > 0.5) * 7
    dist = np.minimum(W - 1 - xx - hilachas, np.minimum(yy, H - 1 - yy)) + irreg      # a la izquierda está el lomo, sin canto
    for _ in range(4):                                           # mordiscos en el canto: faltas de bordes quebrados
        lado = r.integers(1, 4); rad = 6 + r.random() * 9
        if lado < 2: cx_, cy_ = W + rad * 0.3, 80 + r.random() * (H - 160)
        else: cx_, cy_ = 60 + r.random() * (W - 120), (-rad * 0.3 if lado == 2 else H + rad * 0.3)
        dd = np.sqrt((xx - cx_) ** 2 + ((yy - cy_) * (0.6 if lado < 2 else 1.6)) ** 2)
        dist = np.minimum(dist, dd - rad + irreg * 0.6)
    hoja = np.clip((dist - 9) / 1.8, 0, 1)
    tostado = np.clip(1 - (dist - 9) / 34, 0, 1) ** 1.8 * np.clip(xx / 40, 0, 1)   # el lomo no se tuesta como los cantos
    a = mezcla(a, col('#CBB083'), tostado * 0.55)
    fuera = col('#CDBB94')[None, None, :] * (0.95 + 0.06 * ruido_rect(W, H, 4, 185)[..., None])
    sombra = np.clip(1 - np.clip(9 - dist, 0, None) / 6, 0, 1)     # la sombra de la hoja sobre lo de fuera
    fuera = fuera * (1 - 0.1 * sombra[..., None])
    a = mezcla(fuera, a, hoja)
    a = a * (0.985 + 0.02 * ruido_rect(W, H, 0.8, 186)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_arcontes.jpg'), quality=86)

def papel_galia():
    """Galia: una tablilla de madera encerada (recurso: César no dice en qué escribían los galos), clara. Es una sola tabla
    de roble, con la veta a lo largo, vaciada por dentro y con el borde en alto, como las tablillas que se conservan; en
    el hueco, la cera de abeja sin teñir, color miel pálida, alisada con la paleta del estilo: barridas en arco, suaves,
    con su rebordecito, el rastro casi borrado de renglones escritos y vueltos a alisar, poros y alguna mota de polvo o
    de hollín. El borde de madera tiene el canto gastado y oscurecido por las manos, algún golpe y, a la izquierda, los
    dos agujeros por donde pasa la correa de cuero que une las tablillas. La luz viene de arriba a la izquierda: el borde
    de arriba y el de la izquierda echan sombra sobre la cera; las paredes del hueco de abajo y de la derecha brillan."""
    W, H = 720, 1280
    r = np.random.default_rng(191)
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    def estirado(semilla, sx, sy):
        """Ruido de 0 a 1 estirado sx veces a lo ancho y sy a lo alto."""
        n = np.random.default_rng(semilla).random((max(2, H // sy), max(2, W // sx))).astype(np.float32)
        return np.asarray(Image.fromarray(n, 'F').resize((W, H), Image.BICUBIC), np.float32)
    def emboss(campo, s=1.2):
        """Lo que se ve de un campo de alturas con la luz de arriba a la izquierda (−1 a 1 aprox.)."""
        b = np.asarray(Image.fromarray(np.clip(campo * 255, 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(s)), np.float32) / 255
        gy, gx = np.gradient(b)
        return -(gx + gy)
    # --- La cera: el color de base, con manchas grandes más mieladas y otras más pálidas.
    a = mezcla(col('#F0E7CF'), col('#E2D1A6'), ruido_rect(W, H, 70, 192) * 0.85)
    a = mezcla(a, col('#F2EBD6'), np.clip((ruido_rect(W, H, 36, 193) - 0.55) * 2.5, 0, 1) * 0.4)
    a = mezcla(a, col('#D2BF90'), np.clip((ruido_rect(W, H, 50, 206) - 0.7) * 3, 0, 1) * 0.25)
    # Las barridas de la paleta: arcos anchos (la mano gira desde el codo), cada uno con un rebordecito de cera en el
    # borde de fuera; dentro, la cera queda más lisa y un poco más brillante.
    alto = np.zeros((H, W), np.float32)
    capa = Image.new('L', (W, H), 0); filos = Image.new('L', (W, H), 0)
    dc, df = ImageDraw.Draw(capa), ImageDraw.Draw(filos)
    for _ in range(34):
        cx_, cy_ = r.uniform(-300, W + 300), r.uniform(-200, H + 200)
        rad = r.uniform(380, 900); a0 = r.uniform(0, 2 * math.pi); arco = r.uniform(0.25, 0.6); ancho = r.uniform(30, 64)
        angs = np.linspace(a0, a0 + arco, 40)
        fuera = [(cx_ + (rad + ancho / 2) * math.cos(t), cy_ + (rad + ancho / 2) * math.sin(t)) for t in angs]
        dentro = [(cx_ + (rad - ancho / 2) * math.cos(t), cy_ + (rad - ancho / 2) * math.sin(t)) for t in angs]
        dc.polygon(fuera + dentro[::-1], fill=int(r.uniform(60, 140)))
        df.line(fuera, fill=int(r.uniform(120, 220)), width=3)
        for _ in range(int(ancho / 7)):                                   # las estrías finas que deja el filo de la paleta
            rr_ = rad + r.uniform(-ancho / 2, ancho / 2); t0 = r.uniform(0, arco * 0.4); t1 = r.uniform(arco * 0.6, arco)
            df.line([(cx_ + rr_ * math.cos(a0 + t), cy_ + rr_ * math.sin(a0 + t)) for t in np.linspace(t0, t1, 30)],
                    fill=int(r.uniform(25, 60)), width=1)
    lisa = np.asarray(capa.filter(ImageFilter.GaussianBlur(9)), np.float32) / 255
    filo = np.asarray(filos.filter(ImageFilter.GaussianBlur(1.3)), np.float32) / 255
    a = a * (1 + lisa * 0.035)[..., None]
    a = a * (1 + emboss(filo * 0.5, 1.4) * 0.7)[..., None]
    # El rastro de lo que se escribió y se borró: renglones de trazos cortos, hundidos y casi alisados.
    rastro = Image.new('L', (W, H), 0); d = ImageDraw.Draw(rastro)
    for y in np.arange(150, H - 120, 46):
        x = 80 + r.uniform(0, 30)
        while x < W - 90:
            if r.random() < 0.12: x += r.uniform(14, 30); continue          # el hueco entre palabras
            h_ = r.uniform(12, 18); tipo = r.integers(0, 4)
            if tipo == 0: d.line([(x, y), (x + r.uniform(-2, 3), y - h_)], fill=255, width=2)
            elif tipo == 1: d.line([(x, y), (x + 6, y - h_), (x + 12, y)], fill=255, width=2)
            elif tipo == 2: d.arc([x, y - h_, x + 11, y], r.uniform(0, 90), r.uniform(240, 330), fill=255, width=2)
            else: d.line([(x, y - h_ / 2), (x + 10, y - h_ / 2)], fill=255, width=2)
            x += r.uniform(12, 18)
    rastro = np.asarray(rastro, np.float32) / 255
    queda = np.clip(ruido_rect(W, H, 40, 194) * 2.4 - 1.05, 0, 1) * (1 - lisa * 0.7)  # casi todo alisado; queda a ratos
    a = a * (1 - emboss(rastro * queda, 1.2) * 0.7)[..., None]          # hundido: la sombra cae al revés que en lo alto
    a = mezcla(a, col('#C8B583'), np.asarray(Image.fromarray((rastro * queda * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.4)), np.float32)[..., None] / 255 * 0.07)
    # Poros y un brillo suave de la cera hacia arriba a la izquierda.
    poro = np.clip((ruido_rect(W, H, 1.0, 195) - 0.82) * 5, 0, 1)
    a = mezcla(a, col('#C6B48A'), poro * 0.18)
    brillo = np.clip(1 - np.sqrt(((xx - W * 0.25) / W) ** 2 + ((yy - H * 0.2) / H) ** 2) / 0.75, 0, 1) ** 2
    a = a * (1 + 0.035 * brillo - 0.03 * (1 - brillo))[..., None]
    # El lustre de la cera: una franja ancha y muy suave de brillo en diagonal, donde se refleja la ventana.
    lustre = np.exp(-(((xx * 0.8 + yy * 0.45) - H * 0.42) / 150) ** 2) * (0.6 + 0.4 * ruido_rect(W, H, 30, 210))
    a = mezcla(a, col('#FBF5E4'), lustre * 0.16)
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
    for _ in range(70):                                                  # motas de polvo y de hollín, desparejas
        x, y = r.random() * W, r.random() * H; s_ = 0.6 + r.random() ** 3 * 1.4
        pts = [(x + s_ * math.cos(t) * r.uniform(0.6, 1.3), y + s_ * math.sin(t) * r.uniform(0.6, 1.3)) for t in np.linspace(0, 2 * math.pi, 7)]
        d.polygon(pts, fill=(64, 50, 32, int(50 + r.random() * 100)))
    for _ in range(12):                                                  # alguna fibra o pelo pegado a la cera
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = r.uniform(8, 22)
        d.line([(x + l * t * math.cos(ang) + 2 * math.sin(t * 5), y + l * t * math.sin(ang)) for t in np.linspace(0, 1, 6)],
               fill=(90, 72, 48, 70), width=1)
    a = np.asarray(img, np.float32)
    a = a * (0.985 + 0.025 * ruido_rect(W, H, 0.7, 196)[..., None])
    # --- La tabla de roble: veta vertical, a lo largo, con anillos desparejos, poros, radios y manchas de uso.
    MX, MY = 40, 48                                       # el borde en alto (a los lados, arriba y abajo)
    dl, dr_, dt, db = xx, W - 1 - xx, yy, H - 1 - yy
    u = xx + 9 * np.sin(yy / 300 + 0.4) + (ruido_rect(W, H, 40, 198) - 0.5) * 14
    bordes = np.cumsum(r.uniform(5, 19, 120)) - 40
    k_ = np.searchsorted(bordes, u)
    b0 = bordes[np.clip(k_ - 1, 0, len(bordes) - 1)]; b1 = bordes[np.clip(k_, 0, len(bordes) - 1)]
    f = np.clip((u - b0) / np.maximum(b1 - b0, 1e-3), 0, 1)
    tardio = np.clip((f - 0.55) / 0.3, 0, 1) ** 1.4 * np.clip((1 - f) / 0.12, 0, 1) * r.uniform(0.4, 1, len(bordes) + 1)[k_]
    fibra = estirado(200, 1, 50) * 0.6 + estirado(201, 1, 16) * 0.4
    madera = mezcla(col('#BB9A68'), col('#987447'), np.clip(fibra * 0.7 + ruido_rect(W, H, 50, 202) * 0.3, 0, 1))
    madera = mezcla(madera, col('#A57E4C'), np.clip((ruido_rect(W, H, 30, 211) - 0.5) * 2, 0, 1) * 0.5)
    madera = mezcla(madera, col('#735532'), tardio * 0.45)
    poros = np.clip((estirado(207, 1, 9) - 0.7) * 4, 0, 1) * (1 - tardio)
    madera = mezcla(madera, col('#6A5236'), poros * 0.35)
    radios = np.clip((estirado(203, 2, 24) - 0.8) * 5, 0, 1)
    madera = mezcla(madera, col('#C9B089'), radios * 0.45)
    # El borde en alto es redondeado: más claro en la cara que mira a la luz.
    fuera = np.minimum(np.minimum(dl, dr_), np.minimum(dt, db))
    ancho_b = np.where(np.minimum(dl, dr_) < np.minimum(dt, db), MX, MY).astype(np.float32)
    t_ = np.clip(fuera / ancho_b, 0, 1)
    perfil = np.where(dl == fuera, 1, 0) + np.where(dt == fuera, 1, 0) - np.where(dr_ == fuera, 1, 0) - np.where(db == fuera, 1, 0)
    madera = madera * (1 + 0.07 * np.cos(t_ * math.pi) * perfil.astype(np.float32))[..., None]
    # El canto gastado y oscuro de manos, algún golpe y la mugre en el borde de fuera.
    madera = mezcla(madera, col('#4E3820'), np.clip(1 - fuera / 10, 0, 1) ** 1.3 * 0.55)
    madera = mezcla(madera, col('#5C4428'), np.clip((ruido_rect(W, H, 12, 208) - 0.62) * 3, 0, 1) * np.clip(1 - fuera / 30, 0, 1) * 0.35)
    golpes = Image.new('L', (W, H), 0); dg = ImageDraw.Draw(golpes)
    for _ in range(9):
        lado = r.integers(0, 4)
        if lado == 0: x, y = r.uniform(4, MX - 6), r.uniform(60, H - 60)
        elif lado == 1: x, y = W - r.uniform(4, MX - 6), r.uniform(60, H - 60)
        elif lado == 2: x, y = r.uniform(60, W - 60), r.uniform(4, MY - 6)
        else: x, y = r.uniform(60, W - 60), H - r.uniform(4, MY - 6)
        l = r.uniform(3, 9); ang = r.random() * math.pi
        dg.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=255, width=2)
    golpes = np.asarray(golpes, np.float32) / 255
    madera = madera * (1 - emboss(golpes, 0.9) * 2.0)[..., None]
    madera = mezcla(madera, col('#5A4026'), np.asarray(Image.fromarray((golpes * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.8)), np.float32)[..., None] / 255 * 0.4)
    # Una raja corta a lo largo de la veta, arriba, que sale del canto.
    xr = W * 0.62 + 2 * np.sin(yy / 9)
    raja = np.exp(-((xx - xr) / 0.9) ** 2) * (yy < 30) * np.clip(1 - yy / 30, 0, 1) ** 0.4
    madera = mezcla(madera, col('#2E2012'), raja * 0.8)
    madera = madera * (0.97 + 0.05 * ruido_rect(W, H, 0.8, 205)[..., None])
    # Los dos agujeros de la correa, en el borde de la izquierda, y la correa de cuero que pasa de uno al canto.
    for yc in (H * 0.3, H * 0.7):
        dd = np.sqrt((xx - MX * 0.5) ** 2 + (yy - yc) ** 2)
        madera = mezcla(madera, col('#4A3420'), np.clip(1 - (dd - 7.0) / 1.8, 0, 1) * 0.7)
        madera = mezcla(madera, col('#1A1108'), np.clip(1 - (dd - 5.2) / 1.2, 0, 1))
        cuero = (np.abs(yy - yc - 0.06 * (MX * 0.5 - xx)) < 3.0) & (xx < MX * 0.5 - 2)
        cuero = np.asarray(Image.fromarray((cuero * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.7)), np.float32) / 255
        madera = mezcla(madera, col('#6B3E22') * (0.9 + 0.2 * ruido_rect(W, H, 1.2, 209)[..., None]), cuero)
        madera = mezcla(madera, col('#3A2212'), np.clip(1 - np.abs(np.abs(yy - yc - 0.06 * (MX * 0.5 - xx)) - 3.0) / 0.8, 0, 1) * (xx < MX * 0.5 - 2) * 0.5)
    # --- El hueco: la cera queda abajo; las paredes y las sombras dicen de dónde viene la luz.
    din = np.minimum(np.minimum(dl - MX, dr_ - MX), np.minimum(dt - MY, db - MY))    # > 0 dentro de la cera
    dentro = (din > 0).astype(np.float32)
    sombra_arriba = np.clip(1 - (dt - MY) / 18, 0, 1) ** 1.8
    sombra_izq = np.clip(1 - (dl - MX) / 15, 0, 1) ** 1.8
    a = mezcla(a, col('#6E5634'), np.maximum(sombra_arriba, sombra_izq) * 0.4 * dentro)
    a = mezcla(a, col('#FFF7E2'), np.maximum(np.clip(1 - (db - MY) / 6, 0, 1), np.clip(1 - (dr_ - MX) / 6, 0, 1)) * 0.25 * dentro)
    # Las paredes del hueco: oscuras arriba y a la izquierda (miran lejos de la luz), claras abajo y a la derecha.
    pared = np.clip(1 - np.abs(din + 1.2) / 2.0, 0, 1)
    pared_luz = ((db - MY <= np.minimum(dt - MY, np.minimum(dl - MX, dr_ - MX))) | (dr_ - MX <= np.minimum(dt - MY, dl - MX))).astype(np.float32)
    madera = mezcla(madera, col('#3A2A16'), pared * (1 - pared_luz) * 0.7)
    madera = mezcla(madera, col('#D9C29A'), pared * pared_luz * 0.55)
    marco = np.clip(-din / 1.0 + 0.5, 0, 1)
    a = mezcla(a, madera, marco)
    a_img(a).save(os.path.join(OUT, 'papel_galia.jpg'), quality=86)

def papel_nouveau():
    """Art nouveau: el papel crema de un cartel litográfico de 1900, de pasta de madera, liso y un poco amarillento, con su
    formación en nubes (más espeso aquí, más ralo allá), fibras cortas y un grano fino. A los lados, muy suave, una cenefa
    impresa como se imprimían los carteles: una piedra para cada color. La de la tinta salvia pone las manchas de los
    tallos y las hojas, y la malva las del iris y los capullos; encima, la piedra del contorno, en verde oliva, cae un poco
    corrida (el registro nunca era perfecto). Las manchas de color tienen el punteado del lápiz graso sobre la piedra
    granulada. A la izquierda, un tallo sube desde abajo, se mece y arriba se enrosca en un rizo, con hojas de lanza y un
    iris; otro más corto lo acompaña. A la derecha, otro tallo con su rizo y un capullo que se inclina. El centro queda
    libre para el texto."""
    W, H = 720, 1280
    r = np.random.default_rng(211)
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    # --- El papel: crema, con la formación en nubes, un viñeteado tibio, fibras y grano.
    a = mezcla(col('#F2EBDA'), col('#E8DDC4'), ruido_rect(W, H, 55, 212) * 0.7)
    a = mezcla(a, col('#EADFC6'), np.clip((ruido_rect(W, H, 7, 213) - 0.5) * 2.2, 0, 1) * 0.35)        # la formación
    a = mezcla(a, col('#F5F0E2'), np.clip((ruido_rect(W, H, 18, 214) - 0.55) * 2.0, 0, 1) * 0.3)
    borde = np.clip(np.maximum(np.abs(xx - W / 2) / (W / 2), np.abs(yy - H / 2) / (H / 2)) - 0.82, 0, 1) / 0.18
    a = mezcla(a, col('#DCCDAB'), borde ** 2 * 0.35)
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
    for _ in range(650):                                             # fibras cortas de la pasta, claras y oscuras
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 3 + r.random() * 8
        tono = (120, 100, 70, int(10 + r.random() * 16)) if r.random() < 0.6 else (255, 252, 240, int(30 + r.random() * 40))
        d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=tono, width=1)
    for _ in range(5):                                               # unas pocas motas de óxido, chicas
        x, y = r.random() * W, r.random() * H; s_ = 0.7 + r.random() * 1.4
        d.ellipse([x - s_, y - s_, x + s_, y + s_], fill=(150, 112, 66, int(30 + r.random() * 35)))
    a = np.asarray(img, np.float32)
    a = a * (0.988 + 0.022 * ruido_rect(W, H, 0.6, 215)[..., None])
    # --- La cenefa, dibujada al doble y reducida. Cada piedra en su capa.
    k = 2
    piedras = {n: Image.new('L', (W * k, H * k), 0) for n in ('salvia', 'malva', 'contorno')}
    pd = {n: ImageDraw.Draw(im) for n, im in piedras.items()}
    def cinta(dib, pts, anchos, c=255):
        n = len(pts); izq, der = [], []
        for i, (x, y) in enumerate(pts):
            p0 = pts[max(i - 1, 0)]; p1 = pts[min(i + 1, n - 1)]
            tx, ty = p1[0] - p0[0], p1[1] - p0[1]; l = math.hypot(tx, ty) or 1
            w = anchos[i] / 2
            izq.append((x - ty / l * w, y + tx / l * w)); der.append((x + ty / l * w, y - tx / l * w))
        dib.polygon(izq + der[::-1], fill=c)
        return izq + der[::-1]
    def tallo(x, y, rumbo, largo, curvas, n=500):
        """Un tallo como espiral de Euler: [curvas] da la curvatura (rad/px) en función de cuánto va (0–1)."""
        pts = [(x, y)]
        for i in range(n):
            t = i / n; rumbo += curvas(t) * largo / n
            x += largo / n * math.cos(rumbo); y += largo / n * math.sin(rumbo); pts.append((x, y))
        return pts
    def contorno(pts, w=1.3):
        pd['contorno'].line([(x * k + 2.4, y * k + 1.6) for x, y in pts] + [(pts[0][0] * k + 2.4, pts[0][1] * k + 1.6)], fill=255, width=int(w * k), joint='curve')
    def hoja(x, y, rumbo, largo, ancho, curva):
        eje = tallo(x, y, rumbo, largo, lambda t: curva / largo, 40)
        anchos = [ancho * math.sin(math.pi * (i / 40) ** 0.7) ** 0.85 + 0.3 for i in range(41)]
        borde_ = cinta(pd['salvia'], [(px * k, py * k) for px, py in eje], [w_ * k for w_ in anchos])
        contorno([(px / k, py / k) for px, py in borde_])
        pd['contorno'].line([(px * k + 2.4, py * k + 1.6) for px, py in eje[3:-8]], fill=150, width=int(0.8 * k))
    def dibuja_tallo(pts, w0, w1, hojas=(), rizo=True):
        anchos = [(w0 + (w1 - w0) * i / (len(pts) - 1)) for i in range(len(pts))]
        borde_ = cinta(pd['salvia'], [(x * k, y * k) for x, y in pts], [w_ * k for w_ in anchos])
        contorno([(x / k, y / k) for x, y in borde_], 1.1)
        for donde, lado, largo, ancho, curva in hojas:
            i = int(donde * (len(pts) - 1)); (x0, y0), (x1, y1) = pts[i], pts[min(i + 3, len(pts) - 1)]
            rumbo = math.atan2(y1 - y0, x1 - x0) + lado * 0.55
            hoja(x0, y0, rumbo, largo, ancho, curva * lado)
    def iris(x, y, s_):
        """Un iris de perfil en dos tintas, chico (malva los pétalos, con su contorno)."""
        for (dx0, dy0, dx1, dy1, an) in ((0, 0, 0, -26, 15), (-2, 0, -10, -22, 12), (2, 0, 10, -22, 12),
                                          (-2, 2, -20, 18, 13), (2, 2, 20, 18, 13), (0, 2, 0, 28, 15)):
            p0 = (x + dx0 * s_, y + dy0 * s_); p1 = (x + dx1 * s_, y + dy1 * s_)
            l = math.hypot(p1[0] - p0[0], p1[1] - p0[1]); tx, ty = (p1[0] - p0[0]) / l, (p1[1] - p0[1]) / l
            eje = [(p0[0] + tx * l * t, p0[1] + ty * l * t) for t in np.linspace(0, 1, 30)]
            anchos = [an * s_ * math.sin(math.pi * min(1, t * 0.85 + 0.08)) ** 0.7 * (1 - t ** 6) ** 0.4 + 0.3 for t in np.linspace(0, 1, 30)]
            borde_ = cinta(pd['malva'], [(px * k, py * k) for px, py in eje], [w_ * k for w_ in anchos])
            contorno([(px / k, py / k) for px, py in borde_], 1.1)
    def capullo(x, y, rumbo, s_):
        eje = [(x + math.cos(rumbo) * s_ * t, y + math.sin(rumbo) * s_ * t) for t in np.linspace(0, 1, 30)]
        anchos = [s_ * 0.42 * math.sin(math.pi * t ** 0.8) ** 0.8 + 0.3 for t in np.linspace(0, 1, 30)]
        borde_ = cinta(pd['malva'], [(px * k, py * k) for px, py in eje], [w_ * k for w_ in anchos])
        contorno([(px / k, py / k) for px, py in borde_], 1.1)
    def tallo_borde(x0, y_arriba, amp, fase, lado, rizo=True, n=420):
        """Un tallo que sube pegado al borde desde abajo hasta [y_arriba], meciéndose apenas ([amp] px), y arriba, si
        [rizo], se dobla hacia dentro y se enrosca (el látigo). [lado]: −1 en el borde izquierdo, 1 en el derecho."""
        pts = [(x0 + amp * math.sin((H + 10 - y) / 190 + fase) * (0.5 + 0.5 * (H - y) / H) - lado * 0.006 * (H - y), y)
               for y in np.linspace(H + 10, y_arriba, n)]
        if rizo:
            (xa, ya), (xb, yb) = pts[-2], pts[-1]
            rumbo = math.atan2(yb - ya, xb - xa); x, y = xb, yb; largo = 250
            for i in range(200):
                t = i / 200
                rumbo += (-lado) * (0.002 + 0.115 * t ** 2.0) * largo / 200
                x += largo / 200 * math.cos(rumbo); y += largo / 200 * math.sin(rumbo); pts.append((x, y))
        return pts
    # A la izquierda: el tallo largo, que arriba se dobla hacia dentro y se enrosca, y uno corto que se cruza con él y
    # acaba en el iris; abajo, las hojas de espada del iris.
    for x_, alto_, curva_ in ((18, 300, 0.25), (40, 230, -0.2), (60, 170, -0.3)):
        hoja(x_, H + 8, -math.pi / 2 + curva_ * 0.4, alto_, 17, curva_)
    t1 = tallo_borde(34, 210, 24, 0.3, -1)
    dibuja_tallo(t1, 6.0, 2.6, hojas=((0.2, 1, 92, 14, -0.35), (0.34, -1, 74, 12, 0.3), (0.5, 1, 70, 11, -0.35), (0.64, -1, 56, 9, 0.3)))
    t2 = tallo_borde(38, 770, 22, 3.4, -1, rizo=False)
    dibuja_tallo(t2, 4.6, 3.0, hojas=((0.3, 1, 80, 12, -0.3),))
    iris(max(64.0, t2[-1][0] + 6), t2[-1][1] - 28, 1.9)
    i_ = int(0.46 * len(t1)); capullo(t1[i_][0] + 2, t1[i_][1], -math.pi / 2 + 0.85, 30)
    # A la derecha: el tallo largo con su rizo, uno más corto con un capullo que se inclina, y las hojas de abajo.
    for x_, alto_, curva_ in ((W - 18, 260, -0.25), (W - 42, 190, 0.22)):
        hoja(x_, H + 8, -math.pi / 2 + curva_ * 0.4, alto_, 16, curva_)
    t3 = tallo_borde(W - 34, 260, 24, 1.4, 1)
    dibuja_tallo(t3, 6.0, 2.6, hojas=((0.24, -1, 84, 13, 0.3), (0.42, 1, 70, 11, -0.3), (0.6, -1, 58, 9, 0.3)))
    t4 = tallo_borde(W - 38, 580, 22, 4.6, 1, rizo=False)
    dibuja_tallo(t4, 4.4, 2.4, hojas=((0.3, -1, 70, 11, 0.3),))
    capullo(t4[-1][0], t4[-1][1], -math.pi / 2 - 0.6, 34)
    # --- Las tintas, con el punteado del lápiz sobre la piedra granulada, y la mezcla con el papel (como tinta: multiplica).
    def capa(n, s=1.0):
        m = np.asarray(piedras[n].filter(ImageFilter.GaussianBlur(0.7 * k)).resize((W, H), Image.LANCZOS), np.float32) / 255
        return m * s
    grano = ruido_rect(W, H, 0.55, 216)
    punteado = 0.55 + 0.45 * np.clip((grano - 0.35) * 3, 0, 1)
    salvia = capa('salvia') * punteado * (0.85 + 0.3 * ruido_rect(W, H, 30, 217))
    malva = capa('malva') * punteado * (0.85 + 0.3 * ruido_rect(W, H, 30, 218))
    linea = capa('contorno')
    a = a * (1 - salvia[..., None] * (1 - col('#B9C5A6') / 255) * 0.75)
    a = a * (1 - malva[..., None] * (1 - col('#D3B7CF') / 255) * 0.75)
    a = a * (1 - linea[..., None] * (1 - col('#7D8A68') / 255) * 0.62)
    a_img(a).save(os.path.join(OUT, 'papel_nouveau.jpg'), quality=86)

def papel_psicodelia():
    """Psicodelia, la ciudad (cara de día): el papel de una copia de foto de los 70, mate perla, ya amarillento. El
    color de la ciudad clara (#E9DFCC); la superficie perla del papel (un punteado muy fino, que no estorba al texto); el
    grano de película, cálido y menudo; la luz de la tarde, que entra por la izquierda; los bordes que amarillean más (el
    papel se tuesta por donde lo tocan) y una viñeta suave hacia el café. Alrededor, el margen blanco de la copia, con las
    esquinas redondeadas del laboratorio de revelado (el papel sin exponer, a 14 px del borde), con la leyenda menuda que
    imprimía el laboratorio en el margen de abajo. Nada en el centro."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#ECE3D1'), col('#E2D7C1'), ruido_rect(W, H, 70, 2001) * 0.8)
    # La superficie perla: un punteado fino y regular, más claro y más oscuro, casi invisible.
    perla = ruido_rect(W, H, 0.9, 2002) - 0.5
    a = a * (1 + 0.035 * perla[..., None])
    # El grano de la película: cálido (el oscuro hacia el café, el claro hacia el crema), en granitos de 1 a 2 px.
    g = ruido_rect(W, H, 0.6, 2003) - 0.5
    a = a + np.where(g[..., None] > 0, col('#FFF6E4') - a, col('#4A3528') - a) * (np.abs(g) * 0.11)[..., None]
    # La luz de la tarde: de la izquierda y un poco de arriba.
    luz = np.clip(0.55 - (xx / W - 0.5) * 0.7 - (yy / H - 0.5) * 0.25, 0, 1)
    a = a * (0.95 + 0.08 * luz[..., None])
    # El margen de la copia: el papel sin exponer, más claro, con las esquinas redondeadas.
    m, rc = 14, 26
    qx = np.clip(np.maximum(m + rc - xx, xx - (W - m - rc)), 0, None)
    qy = np.clip(np.maximum(m + rc - yy, yy - (H - m - rc)), 0, None)
    fuera_img = np.clip(np.sqrt(qx ** 2 + qy ** 2) - rc + 0.5, 0, 1)
    fuera_img = np.maximum(fuera_img, ((xx < m) | (xx > W - m) | (yy < m) | (yy > H - m)).astype(np.float32))
    a = mezcla(a, col('#F2ECDF'), fuera_img * 0.75)
    # Lo tostado de los bordes y la viñeta hacia el café de la foto.
    borde = np.sqrt(((xx - W / 2) / (W / 2)) ** 4 + ((yy - H / 2) / (H / 2)) ** 4) ** 0.25 + (ruido_rect(W, H, 24, 2004) - 0.5) * 0.1
    a = mezcla(a, col('#D2BE98'), np.clip((borde - 0.8) * 2.6, 0, 1) * 0.42)
    vin = np.clip((np.sqrt(((xx - W / 2) / (W * 0.62)) ** 2 + ((yy - H * 0.48) / (H * 0.62)) ** 2) - 0.55) * 1.6, 0, 1) ** 1.5
    a = mezcla(a, col('#3A2410'), vin * 0.16)
    # Dos o tres motas de polvo que quedaron en la ampliadora (sombras blancas pequeñas: el polvo tapa la luz al copiar).
    r = np.random.default_rng(2005)
    for _ in range(5):
        x0, y0 = r.uniform(40, W - 40), r.choice([r.uniform(30, 220), r.uniform(H - 260, H - 40)])
        d = np.sqrt((xx - x0) ** 2 + ((yy - y0) * r.uniform(0.6, 1.6)) ** 2)
        a = mezcla(a, col('#F7F1E6'), np.clip(1 - d / r.uniform(1.2, 2.6), 0, 1) * 0.55)
    # En el margen de abajo, lo que el laboratorio imprimía en la copia: el papel y el mes, en letra menuda y tenue.
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
    from iconos import letra
    d.text((W / 2, H - m / 2), 'PAPEL FOTOGRÁFICO · MATE PERLA · OCT 1967', font=letra('franklin_semibold.ttf', 8.5),
           fill=(120, 100, 76, 70), anchor='mm')
    img.save(os.path.join(OUT, 'papel_psicodelia.jpg'), quality=86)


def papel_psicodelia_cartel():
    """Psicodelia, el cartel (cara de noche): el papel crema del cartel de la semana (#F2E8D3), sin estucar, con su fibra.
    Por detrás se transparenta, apenas, lo impreso del otro lado: arriba, el disco hecho sol con sus rayos que ondulan, en
    el grabado fino de líneas de la tinta azul negra, y el marco de arco del cartel cerca del borde. Un par de puntos del
    magenta y del ultramar que saltaron de la plancha. Muy tenue: el centro queda para el texto. Se usa en las dos claridades
    de la cara del cartel (la página de leer es siempre clara)."""
    W, H = 720, 1280
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    a = mezcla(col('#F4EBD8'), col('#EADFC8'), ruido_rect(W, H, 60, 2011) * 0.75)
    a = mezcla(a, col('#E6DAC2'), np.clip((ruido_rect(W, H, 4, 2012) - 0.5) * 2.2, 0, 1) * 0.35)        # la formación
    img = a_img(a); d = ImageDraw.Draw(img, 'RGBA')
    r = np.random.default_rng(2013)
    for _ in range(700):                                                     # fibras cortas
        x, y = r.random() * W, r.random() * H; ang = r.random() * math.pi; l = 2 + r.random() * 7
        tono = (120, 104, 84, int(8 + r.random() * 14)) if r.random() < 0.6 else (255, 252, 244, int(24 + r.random() * 30))
        d.line([(x, y), (x + l * math.cos(ang), y + l * math.sin(ang))], fill=tono, width=1)
    a = np.asarray(img, np.float32)
    # Lo del otro lado: los rayos que ondulan alrededor del disco (centrado arriba, fuera del papel), en trama de líneas.
    k = 2
    Y2, X2 = np.mgrid[0:H * k, 0:W * k].astype(np.float32) / k
    cx, cy = W * 0.5, -H * 0.06
    rr = np.sqrt((X2 - cx) ** 2 + (Y2 - cy) ** 2); ang = np.arctan2(Y2 - cy, X2 - cx)
    n = 26
    onda = 0.05 * np.sin(rr / 190 * 2 * math.pi + 0.8) * (220 / np.maximum(rr, 220)) ** 0.4
    sector = ((ang + onda) * n / (2 * math.pi)) % 2
    rayo = np.clip((np.abs(sector - 0.5) < 0.5).astype(np.float32), 0, 1)
    lineas = (np.abs(((Y2 + 0.3 * X2) % 5.0) - 2.5) < 0.75).astype(np.float32)        # el grabado, inclinado
    fuera_disco = np.clip((rr - 190) / 4, 0, 1)
    caida = np.clip(1 - (Y2 - H * 0.04) / (H * 0.42), 0, 1) ** 1.3
    trama = rayo * lineas * fuera_disco * caida
    # El disco, del otro lado: dos anillos finos.
    for r0, g0 in ((176, 3.0), (150, 1.2)):
        trama = np.maximum(trama, np.clip(1 - np.abs(rr - r0) / g0, 0, 1) * 0.9)
    # El marco de arco del cartel: arriba, esquinas grandes; abajo, casi rectas.
    m, ra = 30, 120
    qx = np.maximum(m + ra - X2, X2 - (W - m - ra)); qy = m + ra - Y2
    arco = np.where((qx > 0) & (qy > 0), np.abs(np.sqrt(np.clip(qx, 0, None) ** 2 + np.clip(qy, 0, None) ** 2) - ra),
                    np.minimum(np.minimum(np.abs(X2 - m), np.abs(X2 - (W - m))), np.minimum(np.abs(Y2 - m), np.abs(Y2 - (H - m)))))
    trama = np.maximum(trama, np.clip(1 - arco / 1.4, 0, 1))
    trama = np.asarray(Image.fromarray(trama.astype(np.float32), 'F').resize((W, H), Image.LANCZOS), np.float32)
    trama = np.clip(trama, 0, 1) * (0.75 + 0.25 * ruido_rect(W, H, 1.0, 2014))
    # Del otro lado y a través del papel: muy poca, y un poco borrosa hacia el gris de la tinta vista por detrás.
    a = mezcla(a, col('#8E89A0'), trama * 0.16)
    # Dos o tres salpicaduras de las otras tintas, chicas.
    for x0, y0, c0 in ((W * 0.86, H * 0.2, '#A94870'), (W * 0.1, H * 0.83, '#3F4C9E'), (W * 0.9, H * 0.9, '#A94870')):
        dd = np.sqrt((xx - x0) ** 2 + (yy - y0) ** 2)
        a = mezcla(a, col(c0), np.clip(1 - dd / 2.2, 0, 1) * 0.5)
    # El borde, apenas tostado.
    borde = np.clip(np.maximum(np.abs(xx - W / 2) / (W / 2), np.abs(yy - H / 2) / (H / 2)) - 0.88, 0, 1) / 0.12
    a = mezcla(a, col('#DCCBA8'), np.clip(borde ** 1.6 * (0.7 + 0.5 * ruido_rect(W, H, 30, 2015)), 0, 1) * 0.35)
    a = a * (0.985 + 0.025 * ruido_rect(W, H, 0.6, 2016)[..., None])
    a_img(a).save(os.path.join(OUT, 'papel_psicodelia_cartel.jpg'), quality=86)

if __name__ == '__main__':
    OUT = sys.argv[1]
    if len(sys.argv) > 2:
        # Solo los temas pedidos (python3 arte.py <carpeta> frente,vapor): los demás papeles quedan como están.
        for t in sys.argv[2].split(','): globals()['papel_' + t]()
        print('papeles listos:', sys.argv[2]); sys.exit()
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
    papel_meso()
    papeles_fuente()
    papel_templario()
    papel_deco()
    papel_urbe()
    papel_edo()
    papel_vapor()
    papel_frente()
    papel_arcontes()
    papel_galia()
    papel_nouveau()
    papel_psicodelia()
    papel_psicodelia_cartel()
    print('papeles listos')
