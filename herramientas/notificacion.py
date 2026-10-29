"""
El ícono de las notificaciones y de los mosaicos de ajustes rápidos (res/drawable/ic_stat.xml): la huella maestra
(maestra.py) en vector, simplificada para que se lea a 24 dp. Se adelgaza la huella hasta su esqueleto, se sigue
cada trazo, se simplifica y se dejan solo los de [TRAZOS] (uno sí y otro no), más gruesos.
Uso: python3 notificacion.py ../app/src/main/res/drawable/ic_stat.xml [muestra.png]
"""
import sys
import numpy as np
from PIL import Image, ImageDraw
import maestra

ALTO = 220                 # resolución a la que se adelgaza
VISTA = 24                 # viewport del vector
MARGEN = 1.4               # la huella va de MARGEN a VISTA - MARGEN de alto
GROSOR = 1.5               # grosor del trazo en el vector (dp a 24 dp)
# Los trazos que se dejan, contados de arriba abajo: el segundo y el cuarto arco, el núcleo y dos ondas de abajo.
# Con todos (10), a 24 dp los trazos vecinos se tocan.
TRAZOS = (1, 3, 5, 7, 9)


def esqueleto(b):
    """Adelgazamiento de Zhang-Suen sobre una imagen binaria (True = tinta)."""
    b = np.pad(b.astype(np.uint8), 1)
    while True:
        cambio = False
        for paso in (0, 1):
            p = [np.roll(np.roll(b, -dy, 0), -dx, 1) for dy, dx in ((-1, 0), (-1, 1), (0, 1), (1, 1), (1, 0), (1, -1), (0, -1), (-1, -1))]
            n = sum(p)
            a = sum(((p[i] == 0) & (p[(i + 1) % 8] == 1)).astype(np.uint8) for i in range(8))
            if paso == 0: c = (p[0] * p[2] * p[4] == 0) & (p[2] * p[4] * p[6] == 0)
            else: c = (p[0] * p[2] * p[6] == 0) & (p[0] * p[4] * p[6] == 0)
            quitar = (b == 1) & (n >= 2) & (n <= 6) & (a == 1) & c
            if quitar.any(): b[quitar] = 0; cambio = True
        if not cambio: break
    return b[1:-1, 1:-1].astype(bool)


VECINOS = [(-1, 0), (0, 1), (1, 0), (0, -1), (-1, 1), (1, 1), (1, -1), (-1, -1)]   # primero los de lado

def trazos(s):
    """Sigue el esqueleto de punta a punta: una lista de trazos, cada uno una lista de (x, y)."""
    h, w = s.shape
    def vecinos(y, x): return [(y + dy, x + dx) for dy, dx in VECINOS if 0 <= y + dy < h and 0 <= x + dx < w and s[y + dy, x + dx]]
    puntas = [(y, x) for y, x in zip(*np.where(s)) if len(vecinos(y, x)) == 1]
    visto = set(); salida = []
    for p in puntas:
        if p in visto: continue
        camino = [p]; visto.add(p)
        while True:
            sig = [q for q in vecinos(*camino[-1]) if q not in visto]
            if not sig: break
            camino.append(sig[0]); visto.add(sig[0])
        if len(camino) > 6: salida.append([(x, y) for y, x in camino])
    return salida


def suavizar(p, r=4):
    """Promedio móvil de ±[r] puntos: quita la escalera de los píxeles sin mover las puntas."""
    a = np.array(p, float); out = a.copy()
    for i in range(1, len(a) - 1):
        k = min(r, i, len(a) - 1 - i); out[i] = a[i - k:i + k + 1].mean(0)
    return [tuple(q) for q in out]


def simplificar(p, eps):
    """Ramer-Douglas-Peucker."""
    if len(p) < 3: return p
    a, b = np.array(p[0], float), np.array(p[-1], float)
    d = b - a; L = np.hypot(*d) or 1.0
    dist = [abs(d[0] * (q[1] - a[1]) - d[1] * (q[0] - a[0])) / L for q in p[1:-1]]
    i = int(np.argmax(dist)) + 1
    if dist[i - 1] > eps: return simplificar(p[:i + 1], eps)[:-1] + simplificar(p[i:], eps)
    return [p[0], p[-1]]


def vector():
    m = maestra.recorte()
    ancho = round(m.width * ALTO / m.height)
    b = np.asarray(m.resize((ancho, ALTO), Image.LANCZOS)) > 128
    escala = (VISTA - 2 * MARGEN) / ALTO
    dx = (VISTA - ancho * escala) / 2
    lista = []
    for t in trazos(esqueleto(b)):
        q = suavizar([(dx + x * escala, MARGEN + y * escala) for x, y in t])
        lista.append(simplificar(q, 0.06))
    # Orden estable: de arriba abajo por el punto más alto.
    lista.sort(key=lambda t: (round(min(y for _, y in t), 1), min(x for x, _ in t)))
    return lista


def xml(lista):
    camino = lambda t: 'M' + ' L'.join(f'{x:.2f},{y:.2f}' for x, y in t)
    trazos_xml = '\n'.join(
        f'    <path android:fillColor="#00000000" android:strokeColor="#FFFFFFFF" android:strokeWidth="{GROSOR}" android:strokeLineCap="round"\n'
        f'        android:strokeLineJoin="round" android:pathData="{camino(t)}" />' for t in lista)
    return ('<?xml version="1.0" encoding="utf-8"?>\n'
            '<!-- La huella maestra, simplificada para la barra de estado. Lo genera herramientas/notificacion.py. -->\n'
            '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
            f'    android:width="24dp" android:height="24dp" android:viewportWidth="{VISTA}" android:viewportHeight="{VISTA}">\n'
            + trazos_xml + '\n</vector>\n')


def muestra(lista, archivo, todos=None):
    """La versión a 24 dp en 1×, 2× y 3× y, si se da, la de todos los trazos numerados, para elegir."""
    tamanos = (24, 48, 72, 480)
    hoja = Image.new('RGB', (sum(tamanos) + 50, 480), (40, 40, 44)); x0 = 10
    for t in tamanos:
        k = t / VISTA * 4; im = Image.new('L', (t * 4, t * 4), 0); d = ImageDraw.Draw(im)
        for tr in lista:
            pts = [(x * k, y * k) for x, y in tr]; w = max(1, round(GROSOR * k))
            d.line(pts, fill=255, width=w, joint='curve')
            for p in (pts[0], pts[-1]): d.ellipse([p[0] - w / 2, p[1] - w / 2, p[0] + w / 2, p[1] + w / 2], fill=255)
        hoja.paste(im.resize((t, t), Image.LANCZOS).convert('RGB'), (x0, 10)); x0 += t + 10
    if todos:
        d = ImageDraw.Draw(hoja); k = 480 / VISTA; x1 = x0 - 490
        for i, tr in enumerate(todos):
            d.text((x1 + tr[0][0] * k + 4, 10 + tr[0][1] * k - 6), str(i), fill=(255, 120, 120))
    hoja.save(archivo)


if __name__ == '__main__':
    todos = vector()
    elegidos = [t for i, t in enumerate(todos) if TRAZOS is None or i in TRAZOS]
    open(sys.argv[1], 'w').write(xml(elegidos))
    if len(sys.argv) > 2: muestra(elegidos, sys.argv[2], todos)
    print('trazos:', len(todos), 'en el vector:', len(elegidos))
