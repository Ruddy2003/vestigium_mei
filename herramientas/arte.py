"""
Papeles de página (lámina, vitela, Xàtiva, estepa, Leonardo, Tierras Antiguas) y los ayudantes de dibujo que usa sellos.py.
Los sellos de huella están en sellos.py y los íconos en iconos.py.
Uso: python3 arte.py ../app/src/main/res/drawable-nodpi
"""
import math, sys, os
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
    guardar_p(a, 'papel_lamina.jpg')
    # Vitela de libro de horas: manchada, cálida, con bordes más oscuros.
    a = mezcla(col('#F3EAD3'), col('#E2D2AE'), ruido_rect(W, H, 30, 64))
    a = mezcla(a, col('#D7C49A'), ruido_rect(W, H, 90, 65) * 0.5)
    a = mezcla(a, col('#BFA676'), np.clip(borde, 0, 1) * 0.6)
    a = a * (0.97 + 0.05 * ruido_rect(W, H, 0.9, 66)[..., None])
    guardar_p(a, 'papel_vitela.jpg')
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
    img.save(os.path.join(OUT, 'papel_xativa.jpg'), quality=86)
    # Papel de la estepa: blanco cálido con textura de fieltro muy suave.
    a = mezcla(col('#F3EDDF'), col('#E6DDCB'), ruido_rect(W, H, 2.2, 70) * 0.8)
    a = mezcla(a, col('#DCD2BC'), ruido_rect(W, H, 50, 71) * 0.4)
    guardar_p(a, 'papel_estepa.jpg')
    # Cuaderno de Leonardo: papel amarillento, manchas y bordes tostados.
    a = mezcla(col('#F0E1BC'), col('#DDC594'), ruido_rect(W, H, 35, 72) * 0.8)
    a = mezcla(a, col('#C9A86E'), np.clip((ruido_rect(W, H, 18, 73) - 0.7) * 3.5, 0, 1) * 0.45)
    a = mezcla(a, col('#A98754'), np.clip(borde, 0, 1) * 0.7)
    a = a * (0.96 + 0.06 * ruido_rect(W, H, 0.8, 74)[..., None])
    guardar_p(a, 'papel_leonardo.jpg')

if __name__ == '__main__':
    OUT = sys.argv[1]
    papeles(); papel_antiguas()
    print('papeles listos')
