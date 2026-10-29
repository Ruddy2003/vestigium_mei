"""
La huella maestra (fuentes/huella_icono.jpg): la única huella de la app. La usan el ícono (iconos.py), el sello del día
(sellos.py), el ícono monocromo y el de las notificaciones.
"""
import os
import numpy as np
from PIL import Image

AQUI = os.path.dirname(os.path.abspath(__file__))
_recorte = None


def recorte():
    """La huella recortada a su contorno, como imagen L (255 = tinta), a la resolución de la fuente."""
    global _recorte
    if _recorte is None:
        im = Image.open(os.path.join(AQUI, 'fuentes', 'huella_icono.jpg')).convert('L')
        a = 1.0 - np.asarray(im, dtype=np.float32) / 255.0
        a = np.clip((a - 0.25) / 0.45, 0, 1)
        ys, xs = np.where(a > 0.5)
        _recorte = Image.fromarray((a[ys.min():ys.max() + 1, xs.min():xs.max() + 1] * 255).astype(np.uint8))
    return _recorte


def mascara(tam, alto, centro=None):
    """Máscara 0–1 de tam × tam con la huella de [alto] px, centrada en [centro] (x, y); por defecto, en el centro."""
    m = recorte()
    ancho = round(m.width * alto / m.height)
    m = m.resize((ancho, round(alto)), Image.LANCZOS)
    cx, cy = centro if centro else (tam / 2, tam / 2)
    lienzo = Image.new('L', (tam, tam), 0)
    lienzo.paste(m, (round(cx - ancho / 2), round(cy - alto / 2)))
    return np.asarray(lienzo, dtype=np.float32) / 255.0
