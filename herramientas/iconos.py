"""
Íconos de la app (logo_fondo_<id>.jpg + logo_<id>.webp, WebP sin pérdida, 432 × 432, y logo_mono.webp). La composición es la misma en todos
los temas: la huella maestra (maestra.py) del mismo tamaño y en la misma posición, dentro del círculo seguro del ícono
adaptativo (66 %). Cada tema añade solo tres cosas (lote 1 del plan de diseño):
1. una textura de fondo propia y tenue;
2. la tinta de la huella en el color del tema, con un acabado discreto (dorado, tinta ferrogálica, fósforo…);
3. un anillo fino con el motivo del tema en miniatura, siempre del mismo grosor y radio.
Uso: python3 iconos.py ../app/src/main/res/drawable-nodpi [hoja.png]
     python3 iconos.py ../app/src/main/res/drawable-nodpi --temas frente,vapor    solo esos (sin logo_mono ni hoja)
"""
import math, sys, os
import numpy as np
from PIL import Image, ImageDraw, ImageFilter
import maestra

TAM = 432
RADIO = 0.29 * TAM         # el anillo y su motivo caben en el círculo seguro (radio 0.333) con aire alrededor
GROSOR = 2.6               # px a 432 (unos 0.65 dp)
K = 4                      # el anillo se dibuja al cuádruple y se reduce, para bordes suaves
RADIO_CHAPA = 0.292        # el botón de Psicodelia, en fracción de TAM: deja ver la mezclilla alrededor


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

def revoque(sem):        # Roma: revoque ocre de fachada, con manchas donde se descascara y alguna hilada de ladrillo
    base = ruido(sem, 1.0, 5) * 0.5
    manchas = (ruido(sem + 5, 1, 22) > 0.9).astype(np.float32)
    manchas = np.asarray(Image.fromarray((manchas * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(3)), np.float32) / 255
    hiladas = ((Y % 18) < 1.6).astype(np.float32) * manchas
    return base - manchas * 0.5 - hiladas * 0.9

def pantalla(sem):       # Ciber: líneas de barrido y una retícula muy tenue
    lineas = (np.sin(Y * math.pi / 2) > 0.7) * -0.5
    reticula = ((X % 27 < 1) | (Y % 27 < 1)) * 0.6
    return lineas + reticula + ruido(sem, 0.25)

def callejon(sem):       # Ciudad neón: llovizna en diagonal sobre el asfalto mojado, con manchas de luz de letrero
    lluvia = (np.sin((X * 0.35 + Y * 1.2) * 0.9 + ruido(sem, 8, 0)) > 0.985) * 0.9
    charcos = ruido(sem + 1, 1.0, 26) * 0.7
    return lluvia + charcos + ruido(sem + 2, 0.2)

def pario(sem):          # Helénico: mármol de Paros, blanco y fino, con vetas cortas y suaves
    t = ruido(sem, 4.0, 18)
    vetas = np.abs(np.sin((X * 0.8 - Y * 1.2) / TAM * 14 + t * 2.2)) ** 30 * -0.7
    return vetas + ruido(sem + 1, 0.35, 2) + ruido(sem + 2, 0.4, 24)

def papiro_nilo(sem):    # Egipto: papiro, las tiras en dos capas que se cruzan (las horizontales más marcadas) y una unión de hojas
    t = ruido(sem, 3.0, 20)
    horiz = np.sin(Y * 1.7 + t * 1.5) * 0.35
    vert = np.sin(X * 0.9 + ruido(sem + 2, 2.0, 16)) * 0.18
    union = (np.abs(X - TAM * 0.68) < 5) * -0.5
    return horiz + vert + union + ruido(sem + 3, 0.3, 1)

def vitela(sem):         # Scriptorium: vitela con manchas cálidas y poros de folículo
    poros = (ruido(sem, 1, 0) > 2.6) * -0.9
    return ruido(sem + 1, 1.0, 22) * 0.8 + ruido(sem + 2, 0.3) + poros

def lino(sem):           # Templario: el lino de la sobreveste, en tafetán (trama y urdimbre), con polvo de arena
    trama = np.sin(X * 1.9 + ruido(sem, 0.8, 3)) * 0.22 + np.sin(Y * 1.9 + ruido(sem + 1, 0.8, 3)) * 0.22
    arena = (ruido(sem + 2, 1, 0) > 2.4) * -0.6
    return trama + arena + ruido(sem + 3, 0.6, 20)

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

def hollin_plano(sem):   # Vapor victoriano: papel de patente con nubes de hollín, motas de carbón y la doble regla del margen
    nubes = np.clip(ruido(sem, 1.0, 22), -2, 0) * 0.9
    motas = np.asarray(Image.fromarray(((ruido(sem + 5, 1, 0) > 2.6) * 255).astype(np.uint8)).filter(ImageFilter.MaxFilter(3)), np.float32) / 255 * -0.9
    regla = ((np.abs(X - 40) < 1) | (np.abs(X - 46) < 0.8) | (np.abs(Y - 40) < 1) | (np.abs(Y - 46) < 0.8)) * -0.6
    return nubes + motas + regla + ruido(sem + 2, 0.25, 2)

def consola(sem):        # Frente exterior: el panel de una consola de los 70, color hueso, con la piel de naranja de la pintura
    # al horno, el desgaste de las manos en manchas grandes, las juntas de los paneles arriba y abajo y sus tornillos.
    piel = ruido(sem, 0.55, 1.4) + ruido(sem + 1, 0.3, 0.5)
    uso = np.clip(ruido(sem + 2, 1.0, 34), -2, 0.6) * 0.75
    junta = np.zeros((TAM, TAM), np.float32)
    for y0 in (TAM * 0.135, TAM * 0.865):
        junta += (np.abs(Y - y0) < 1.1) * -1.6 + (np.abs(Y - y0 - 2.2) < 0.9) * 0.9
    for x0, y0 in ((TAM * 0.2, TAM * 0.09), (TAM * 0.8, TAM * 0.09), (TAM * 0.2, TAM * 0.91), (TAM * 0.8, TAM * 0.91)):
        d = np.sqrt((X - x0) ** 2 + (Y - y0) ** 2)
        junta += (d < 5.5) * -0.9 + (np.abs(d - 5.5) < 0.9) * -1.2 + ((np.abs(X - x0) < 0.8) & (d < 4.5)) * -1.4
    return piel + uso + junta

def nube(sem, celdas, tam=TAM):
    """Un campo suave de -1 a 1 aprox.: ruido de [celdas] × [celdas] puntos, ampliado con interpolación bicúbica."""
    r = np.random.default_rng(sem).random((celdas, celdas)).astype(np.float32)
    im = Image.fromarray(r, 'F').resize((tam, tam), Image.BICUBIC)
    a = np.asarray(im, np.float32)
    return (a - a.mean()) / (a.std() + 1e-6) * 0.5

def heliotropo(sem):     # Arcontes: jaspe verde oscuro de las gemas de Abraxas (heliotropo), con nubes más claras y más oscuras,
    # alguna veta fina, motas rojas de hierro y el brillo del pulido arriba a la izquierda. Es la única textura en color:
    # devuelve el desvío en RGB.
    nubes = nube(sem, 4) * 0.8 + nube(sem + 1, 10) * 0.45 + nube(sem + 2, 26) * 0.22 + ruido(sem + 3, 0.1)
    # Vetas: líneas finas y quebradas donde un campo cruza el cero (no rayas parejas), solo en algunas zonas.
    v = nube(sem + 4, 6) + nube(sem + 5, 17) * 0.3
    vetas = np.exp(-(v / 0.02) ** 2) * np.clip(nube(sem + 6, 5) * 2, 0, 1)
    # Las motas rojas del heliotropo: chicas, sueltas y agrupadas en una o dos zonas.
    roja = np.clip((nube(sem + 7, 70) + nube(sem + 8, 150) * 0.5 - 0.78) * 4.0, 0, 1) * np.clip(nube(sem + 9, 4) * 2 + 0.2, 0, 1)
    brillo = np.clip(1 - np.sqrt((X - TAM * 0.3) ** 2 + (Y - TAM * 0.26) ** 2) / (TAM * 0.62), 0, 1) ** 2
    caida = np.clip(np.sqrt((X - TAM * 0.5) ** 2 + (Y - TAM * 0.5) ** 2) / (TAM * 0.72), 0, 1) ** 2
    rgb_ = nubes[..., None] * np.array([9, 14, 11], np.float32)
    rgb_ += vetas[..., None] * np.array([7, 9, 8], np.float32)
    rgb_ += roja[..., None] * (rgb('#8E3A2E') - rgb('#2B4636')) * 0.6
    rgb_ += brillo[..., None] * np.array([14, 19, 16], np.float32) - caida[..., None] * np.array([10, 14, 12], np.float32)
    return rgb_

def roble(sem, tam=TAM):  # Galia: una tabla de roble aserrada al cuarto, oscura y aceitada, con la veta horizontal (anillos de
    # anchos desparejos, el leño de verano más oscuro, que ondula apenas), las vetas largas de color, los poros del leño de
    # primavera en rayitas, los radios de la madera (las «flores» del roble: manchas claras y satinadas en forma de lente, a
    # lo largo de la veta) y el brillo del aceite arriba a la izquierda. Devuelve el desvío en RGB, como el jaspe de Arcontes.
    s = tam / TAM
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32) / s
    rng = np.random.default_rng(sem + 3)
    def estirada(semilla, celdas_x, celdas_y):
        n = np.random.default_rng(semilla).random((celdas_y, celdas_x)).astype(np.float32)
        a = np.asarray(Image.fromarray(n, 'F').resize((tam, tam), Image.BICUBIC), np.float32)
        return (a - a.mean()) / (a.std() + 1e-6)
    # Los anillos: fronteras con anchos al azar (de 4 a 15 px), recorridas por una onda suave.
    bordes = np.cumsum(rng.uniform(4, 15, 120)) - 60
    onda = nube(sem, 3, tam) * 9 + nube(sem + 1, 8, tam) * 2.5
    v = yy + onda
    k = np.searchsorted(bordes, v)
    b0 = bordes[np.clip(k - 1, 0, len(bordes) - 1)]; b1 = bordes[np.clip(k, 0, len(bordes) - 1)]
    f = np.clip((v - b0) / np.maximum(b1 - b0, 1e-3), 0, 1)
    fuerza_anillo = rng.uniform(0.35, 1.0, len(bordes) + 1)[k]
    tardio = np.clip((f - 0.55) / 0.3, 0, 1) ** 1.4 * np.clip((1 - f) / 0.12, 0, 1) * fuerza_anillo
    vetas = estirada(sem + 8, 3, 60) * 0.6 + estirada(sem + 9, 6, 140) * 0.4
    # Los poros: rayitas oscuras, cortas, a lo largo de la veta, sobre todo en la madera de primavera.
    poros = np.clip((nube(sem + 4, 260, tam) - 0.55) * 4, 0, 1)
    poros = np.asarray(Image.fromarray((poros * 255).astype(np.uint8)).resize((max(2, tam // 8), tam), Image.BILINEAR).resize((tam, tam), Image.BILINEAR), np.float32) / 255
    poros = poros * np.clip(1 - tardio * 1.6, 0, 1)
    # Los radios: lentes claras, alargadas en la veta, de varios tamaños, con un filo un poco más oscuro.
    capa = Image.new('L', (tam, tam), 0); d = ImageDraw.Draw(capa)
    for _ in range(46):
        x, y = rng.uniform(-30, TAM + 30), rng.uniform(0, TAM)
        l = rng.uniform(10, 62); a = rng.uniform(1.4, 4.2) * (0.6 + l / 90)
        pts = [((x + l * math.cos(t)) * s, (y + a * math.sin(t) * (0.55 + 0.45 * abs(math.cos(t)))) * s) for t in np.linspace(0, 2 * math.pi, 28)]
        d.polygon(pts, fill=int(rng.uniform(120, 255)))
    radios = np.asarray(capa.filter(ImageFilter.GaussianBlur(0.8 * s)), np.float32) / 255
    filo = np.clip(np.asarray(capa.filter(ImageFilter.GaussianBlur(2.4 * s)), np.float32) / 255 - radios, 0, 1)
    brillo = np.clip(1 - np.sqrt((xx - TAM * 0.28) ** 2 + (yy - TAM * 0.22) ** 2) / (TAM * 0.8), 0, 1) ** 2
    caida = np.clip(np.sqrt((xx - TAM * 0.5) ** 2 + (yy - TAM * 0.5) ** 2) / (TAM * 0.72), 0, 1) ** 2
    rgb_ = -tardio[..., None] * np.array([16, 13, 9], np.float32)
    rgb_ += vetas[..., None] * np.array([8, 6, 4], np.float32) + nube(sem + 5, 4, tam)[..., None] * np.array([10, 8, 5], np.float32)
    rgb_ -= poros[..., None] * np.array([11, 9, 6], np.float32)
    rgb_ += radios[..., None] * np.array([24, 19, 12], np.float32) - filo[..., None] * np.array([7, 6, 4], np.float32)
    rgb_ += brillo[..., None] * np.array([22, 17, 10], np.float32) - caida[..., None] * np.array([14, 11, 8], np.float32)
    rgb_ += np.random.default_rng(sem + 7).standard_normal((tam, tam)).astype(np.float32)[..., None] * 1.4
    return rgb_

def vidrio_nancy(sem, tam=TAM):  # Art nouveau: vidrio de Nancy, de varias capas, grabado al ácido (mate y satinado). Los
    # colores se funden sin frontera, en diagonal: el violeta del iris arriba a la izquierda, más hondo en el medio, y
    # abajo a la derecha el verde del hierro de Guimard, en nubes; donde se encuentran, el polvo de vidrio de color que se
    # mete entre las capas deja motas de uno dentro del otro. Unas burbujas finas y la luz que pasa a través, ámbar,
    # arriba a la izquierda. Devuelve el color entero menos el del fondo de TEMAS (como el jaspe de Arcontes, en RGB).
    s = tam / TAM
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32) / s
    # Dónde está cada pixel en el degradado: una diagonal, torcida en nubes por la masa del vidrio al soplarla.
    v = (xx * 0.5 + yy * 0.86) / TAM / 1.36 + 0.04 + nube(sem, 3, tam) * 0.16 + nube(sem + 1, 7, tam) * 0.09 + nube(sem + 8, 18, tam) * 0.035
    tonos = [(-0.1, '#7759A3'), (0.2, '#634A8A'), (0.4, '#4E3C6E'), (0.54, '#3F3A5A'), (0.64, '#324540'), (0.8, '#294436'), (1.1, '#1C3026')]
    xs = [p for p, _ in tonos]; cs = np.array([rgb(c) for _, c in tonos])
    col = np.stack([np.interp(v, xs, cs[:, k]) for k in range(3)], -1)
    # El polvo de vidrio entre las capas: motas del verde dentro del violeta y del violeta dentro del verde, sobre todo
    # donde los dos se encuentran, de varios tamaños.
    junta = np.exp(-((v - 0.58) / 0.2) ** 2)
    polvo = nube(sem + 3, 110, tam) + nube(sem + 4, 40, tam) * 0.7 + nube(sem + 11, 14, tam) * 0.5
    verdes = np.clip((polvo - 0.5) * 1.8, 0, 1) * junta * np.clip((0.62 - v) * 8, 0, 1)
    violetas = np.clip((-polvo - 0.5) * 1.8, 0, 1) * junta * np.clip((v - 0.54) * 8, 0, 1)
    col = col * (1 - verdes[..., None] * 0.45) + rgb('#3C6650') * verdes[..., None] * 0.45
    col = col * (1 - violetas[..., None] * 0.4) + rgb('#5E4A84') * violetas[..., None] * 0.4
    # Nubes más claras dentro de la masa (el vidrio es más delgado ahí y pasa más luz).
    claro = np.clip(nube(sem + 12, 5, tam) * 1.4, 0, 1)
    col = col * (1 + claro[..., None] * 0.1)
    # La luz que pasa a través, arriba a la izquierda: ámbar, muy suave; y la caída hacia abajo a la derecha.
    luz = np.clip(1 - np.sqrt((xx - TAM * 0.24) ** 2 + (yy - TAM * 0.2) ** 2) / (TAM * 0.5), 0, 1) ** 2
    col = col * (1 - luz[..., None] * 0.22) + rgb('#C49A5E') * luz[..., None] * 0.22
    caida = np.clip(np.sqrt((xx - TAM * 0.5) ** 2 + (yy - TAM * 0.5) ** 2) / (TAM * 0.72), 0, 1) ** 2
    col = col * (1 - 0.2 * caida[..., None])
    # El martelado de Daum: la superficie tallada en facetas chicas y poco hondas, como golpes de martillo, que la luz
    # vuelve un poco más claras de un lado y más oscuras del otro.
    from scipy.spatial import cKDTree
    rp = np.random.default_rng(sem + 13)
    centros = rp.uniform(-10, TAM + 10, (1100, 2)).astype(np.float32)
    dist, cual = cKDTree(centros).query(np.stack([xx.ravel(), yy.ravel()], -1))
    dx_ = (xx.ravel() - centros[cual, 0]).reshape(tam, tam); dy_ = (yy.ravel() - centros[cual, 1]).reshape(tam, tam)
    faceta = np.clip((dx_ * -0.6 + dy_ * -0.75) / 9.0, -1, 1)          # la pendiente del hoyuelo, de cara a la luz o no
    col = col + (faceta * 5.5)[..., None] * np.array([1.0, 1.0, 1.1], np.float32)
    # El grabado al ácido: grano fino y mate, con un satinado más grueso encima.
    grano = np.random.default_rng(sem + 6).standard_normal((tam, tam)).astype(np.float32)
    grano = np.asarray(Image.fromarray(np.clip(grano * 40 + 128, 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.6 * s)), np.float32)
    col = col + ((grano - 128) / 40 * 2.6)[..., None] + nube(sem + 7, 70, tam)[..., None] * 2.5
    # Burbujas finas: un punto claro con su borde oscuro.
    img = Image.fromarray(np.clip(col, 0, 255).astype(np.uint8)).convert('RGBA')
    K2 = 3; S = tam * K2
    capa = Image.new('RGBA', (S, S), (0, 0, 0, 0)); d = ImageDraw.Draw(capa, 'RGBA')
    rng = np.random.default_rng(sem + 5)
    def P(x, y): return (x * s * K2, y * s * K2)
    for _ in range(18):
        x, y = rng.uniform(0, TAM, 2); r = rng.uniform(0.8, 2.2)
        d.ellipse([*P(x - r, y - r), *P(x + r, y + r)], outline=(18, 16, 26, 70), width=K2)
        d.ellipse([*P(x - r * 0.55 - 0.3, y - r * 0.55 - 0.3), *P(x + r * 0.1 - 0.3, y + r * 0.1 - 0.3)], fill=(236, 232, 246, 90))
    img.alpha_composite(capa.resize((tam, tam), Image.LANCZOS))
    return np.asarray(img.convert('RGB'), np.float32) - rgb('#40384F')[None, None, :]

def mezclilla(sem, tam=TAM):  # Psicodelia: la solapa de una chaqueta de mezclilla, donde va prendido el botón. Sarga 3/1: la
    # urdimbre teñida de índigo (vertical) pasa sobre tres pasadas de la trama cruda y deja ver una, y así se forman las
    # diagonales que suben hacia la derecha. Cada hilo de urdimbre tiene su tinte (el índigo se queda en la superficie del
    # hilo y se gasta desparejo: rayas verticales y nudos), la trama asoma en puntos crudos y el uso aclara la tela en nubes
    # grandes. Devuelve el color menos el del fondo de TEMAS (como el jaspe de Arcontes, en RGB).
    s = tam / TAM
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32) / s
    rng = np.random.default_rng(sem)
    pw, pt = 3.3, 4.4                                       # el paso de la urdimbre y el de la trama, en px a 432
    xw = xx + nube(sem + 1, 5, tam) * 2.4 + nube(sem + 9, 19, tam) * 0.5      # los hilos no van del todo rectos
    yw = yy + nube(sem + 2, 6, tam) * 2.0
    i = np.floor(xw / pw); fx = xw / pw - i
    j = np.floor(yw / pt); fy = yw / pt - j
    fase = (j + i) % 4
    trama = fase == 0                                       # donde asoma la trama: una de cada cuatro, corrida un hilo
    a_lo_largo = np.clip((fase - 1 + fy) / 3.0, 0, 1)       # dentro del flotante de urdimbre (tres pasadas)
    cil = np.sin(np.pi * np.clip(fx, 0, 1)) ** 0.6          # el hilo es redondo: claro al medio
    puntas = np.sin(np.pi * a_lo_largo) ** 0.45             # y se hunde donde entra bajo la trama
    ncol = int(TAM / pw) + 8
    hilo = rng.normal(0, 1, ncol)[np.clip(i.astype(int) + 4, 0, ncol - 1)]
    nudos = np.asarray(Image.fromarray(nube(sem + 3, 90, tam), 'F').resize((max(2, tam // 5), tam), Image.BILINEAR)
                       .resize((tam, tam), Image.BILINEAR), np.float32)
    gasto = np.clip(hilo * 0.35 + nudos * 1.1 + nube(sem + 4, 4, tam) * 0.7, -1, 2)
    indigo, lavado, crudo = rgb('#1F2D4E'), rgb('#4C6491'), rgb('#8E97A8')
    urd = indigo + (lavado - indigo) * np.clip(0.18 + gasto * 0.3, 0, 1)[..., None]
    urd = urd * (0.72 + 0.36 * (cil * puntas)[..., None])
    tra = crudo * (0.5 + 0.3 * np.sin(np.pi * np.clip(fy, 0, 1))[..., None] ** 0.8) * (0.85 + 0.15 * cil[..., None])
    col = np.where(trama[..., None], tra * 0.55 + urd * 0.45, urd)
    brillo = np.clip(1 - np.sqrt((xx - TAM * 0.28) ** 2 + (yy - TAM * 0.22) ** 2) / (TAM * 0.8), 0, 1) ** 2
    caida = np.clip(np.sqrt((xx - TAM * 0.5) ** 2 + (yy - TAM * 0.5) ** 2) / (TAM * 0.72), 0, 1) ** 2
    col = col * (0.88 + 0.2 * brillo[..., None] - 0.14 * caida[..., None])
    col = np.asarray(Image.fromarray(np.clip(col, 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.45 * s)), np.float32)
    return col - rgb('#24324F')[None, None, :]

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

def laca(sem):           # Art déco: laca negra con escamas de abanico apenas grabadas y el brillo en diagonal
    r = 27
    fila = (Y // (r * 0.78)).astype(int)
    cx = (X + (fila % 2) * r) % (2 * r) - r; cy = Y % (r * 0.78) - r * 0.78
    d = np.sqrt(cx ** 2 + cy ** 2)
    escamas = ((np.abs(d - r) < 0.9) | (np.abs(d - r * 0.6) < 0.7)) & (cy < 0)
    brillo = np.exp(-(((X - Y * 0.7) - TAM * 0.3) / (TAM * 0.2)) ** 2) * 0.6
    return escamas * 0.8 + brillo + ruido(sem, 0.2, 2)

def herbario(sem):       # Botánico: papel de herbario con fibras y nervaduras muy tenues
    nerv = (np.abs(np.sin((X - Y * 0.6) * 0.035 + ruido(sem, 1.5, 16))) < 0.04) * -0.8
    return ruido(sem + 1, 0.9, 5) * 0.7 + ruido(sem + 2, 0.35) + nerv

def hormigon_agrietado(sem):   # Ciudad verde: hormigón con grietas finas que se cruzan y manchas de musgo
    t = ruido(sem, 6, 12)
    grietas = (np.abs(np.sin(X * 0.03 + Y * 0.017 + t)) < 0.025) * -1.0 + (np.abs(np.sin(X * 0.011 - Y * 0.04 + t * 1.3)) < 0.02) * -0.8
    musgo = np.clip(ruido(sem + 3, 1.0, 22) - 0.5, 0, 2) * -0.6
    return ruido(sem + 1, 0.7, 2) * 0.6 + ruido(sem + 2, 0.4, 14) + grietas + musgo

def fieltro_nieve(sem):  # Navidad: fieltro con copitos de nieve
    copos = (ruido(sem, 1, 0) > 2.7) * 1.4
    return ruido(sem + 1, 0.8, 1.5) + ruido(sem + 2, 0.4, 14) + copos

def seda(sem):           # Seda: tejido fino de hilos horizontales y verticales con brillo
    trama = np.sin(Y * 1.6) * 0.35 + np.sin(X * 1.6) * 0.15
    brillo = np.sin((X + Y) * 0.02 + ruido(sem, 2, 20)) * 0.5
    return trama + brillo + ruido(sem + 1, 0.2)

def washi(sem):          # Japón Edo: papel washi con sus fibras largas de kōzo, enredadas, sobre un grano suave
    rng = np.random.default_rng(sem)
    capa = Image.new('L', (TAM, TAM), 128); d = ImageDraw.Draw(capa)
    for _ in range(260):
        x, y = rng.uniform(0, TAM, 2); a = rng.uniform(0, math.pi); l = rng.uniform(14, 60)
        pts = [(x + t * l * math.cos(a) + 3 * math.sin(t * 6 + x), y + t * l * math.sin(a) + 3 * math.cos(t * 5 + y)) for t in np.linspace(0, 1, 8)]
        d.line(pts, fill=int(rng.choice([150, 165, 105])), width=1)
    fibras = (np.asarray(capa.filter(ImageFilter.GaussianBlur(0.6)), np.float32) - 128) / 22
    return fibras * 0.8 + ruido(sem + 1, 0.35, 3) + ruido(sem + 2, 0.25, 18)

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

def agua_abisal(sem):    # Abismo: la red de luz del oleaje (cáusticas), muy tenue, y nieve marina que cae
    yy, xx = np.mgrid[0:TAM, 0:TAM].astype(np.float32)
    causticas = np.abs(np.sin(xx / 23 + np.sin(yy / 31) * 2.1) * np.sin(yy / 19 + np.sin(xx / 27) * 1.7))
    nieve = (ruido(sem, 1, 0) > 2.8) * 1.2
    return (causticas < 0.08) * 1.1 + nieve + ruido(sem + 1, 0.6, 30)

def lazo(sem):           # Al-Ándalus: lazo de ocho, apenas grabado
    u, v = (X % 54) - 27, (Y % 54) - 27
    a = np.maximum(np.abs(u), np.abs(v)); b = (np.abs(u) + np.abs(v)) / math.sqrt(2)
    estrella = np.maximum(a, b)
    return (np.abs(estrella - 15) < 1) * -0.7 + ruido(sem, 0.25, 2)

def amate(sem):          # Mesoamérica: papel amate, la corteza golpeada en una red de fibras que se cruzan sin orden
    r = np.random.default_rng(sem)
    campo = np.zeros((TAM, TAM), np.float32)
    img = Image.new('L', (TAM, TAM), 0); d = ImageDraw.Draw(img)
    for _ in range(260):
        x, y = r.random() * TAM, r.random() * TAM
        a = (r.random() - 0.5) * 0.9 + (0 if r.random() < 0.5 else math.pi / 2); l = 12 + r.random() * 40
        d.line([(x, y), (x + math.cos(a) * l, y + math.sin(a) * l)], fill=255, width=1)
    campo = np.asarray(img.filter(ImageFilter.GaussianBlur(0.6)), np.float32) / 255
    return ruido(sem + 1, 0.5, 6) - campo * 0.9 + ruido(sem + 2, 0.25, 1)

def fieltro(sem):        # Mongol: fieltro de yurta, grueso y moteado
    return ruido(sem, 1.0, 3) * 0.8 + ruido(sem + 1, 0.7, 10) + ruido(sem + 2, 0.4, 0.8)

def construccion(sem):   # Renacimiento: papel con los trazos de construcción de un dibujo
    d1 = np.abs(np.sqrt((X - TAM * 0.5) ** 2 + (Y - TAM * 0.5) ** 2) - TAM * 0.42) < 0.8
    d2 = np.abs(X - Y) < 0.8
    d3 = np.abs(X + Y - TAM) < 0.8
    return (d1 | d2 | d3) * -0.7 + ruido(sem, 0.8, 18) + ruido(sem + 1, 0.3)

def carta_nautica(sem):  # Pirata: pergamino húmedo de carta náutica, con líneas de rumbo y manchas de agua
    cx, cy = TAM * 0.78, TAM * 0.2
    ang = np.arctan2(Y - cy, X - cx)
    rumbos = (np.abs(((ang / (2 * math.pi / 16)) + 0.5) % 1 - 0.5) < 0.012) * -0.55
    manchas = np.clip(ruido(sem + 2, 1.0, 30) - 0.6, 0, 2) * -0.9
    return rumbos + manchas + ruido(sem, 0.7, 14) + ruido(sem + 1, 0.3)

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

def caliza(sem):         # Prehistoria: caliza de la pared de la cueva, con estratos que ondulan y motas de ocre
    estratos = np.sin(Y * 0.06 + np.sin(X * 0.02) * 2 + ruido(sem, 3, 24)) * 0.4
    motas = (ruido(sem + 1, 1.0, 2) > 2.1) * -1.2
    return estratos + motas + ruido(sem + 2, 0.45, 1.0)


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

def _cupolas(d, c, n=8, r=8.5):       # cúpulas: medio disco hacia afuera con su linterna, como el perfil de los tejados
    for i in range(n):
        a = 2 * math.pi * i / n + math.pi / n
        x, y = _pos(a); g = math.degrees(a)
        d.pieslice([x - r * K, y - r * K, x + r * K, y + r * K], g - 90, g + 90, fill=c)
        lx, ly = _pos(a, RADIO + r + 1.6)
        d.ellipse([lx - 1.4 * K, ly - 1.4 * K, lx + 1.4 * K, ly + 1.4 * K], fill=c)

def _abanicos(d, c, n=10, r=10.5):     # Art déco: abanicos solares hacia afuera, de tres rayos llenos, y un punto entre uno y otro
    for i in range(n):
        a = 2 * math.pi * i / n
        x, y = _pos(a); g = math.degrees(a)
        caja = [x - r * K, y - r * K, x + r * K, y + r * K]
        for k in range(5):
            if k % 2 == 0: d.pieslice(caja, g - 90 + k * 36, g - 90 + (k + 1) * 36, fill=c)
        px, py = _pos(a + math.pi / n)
        d.ellipse([px - 1.5 * K, py - 1.5 * K, px + 1.5 * K, py + 1.5 * K], fill=c)

def _rayas(d, c, n=32, largo=0.6):    # segmentos sobre el anillo (como una barra de carga)
    for i in range(n):
        a0 = 2 * math.pi * i / n; a1 = a0 + 2 * math.pi / n * largo
        d.arc([(TAM / 2 - RADIO) * K, (TAM / 2 - RADIO) * K, (TAM / 2 + RADIO) * K, (TAM / 2 + RADIO) * K], math.degrees(a0), math.degrees(a1), fill=c, width=int(GROSOR * K * 1.6))

def _tubos(d, c, n=3, hueco=0.2):    # el anillo como un tubo de neón partido en tramos, con sus grapas
    caja = [(TAM / 2 - RADIO) * K, (TAM / 2 - RADIO) * K, (TAM / 2 + RADIO) * K, (TAM / 2 + RADIO) * K]
    for i in range(n):
        a0 = 2 * math.pi * i / n + hueco; a1 = 2 * math.pi * (i + 1) / n - hueco
        d.arc(caja, math.degrees(a0), math.degrees(a1), fill=c, width=int(GROSOR * K * 1.5))
        for a in (a0 + 0.25, a1 - 0.25):
            x0, y0 = _pos(a, RADIO - 5); x1, y1 = _pos(a, RADIO + 5)
            d.line([(x0, y0), (x1, y1)], fill=c, width=int(GROSOR * K * 0.7))

def _perlas(d, c, n=28, r=2.4):
    for i in range(n):
        x, y = _pos(2 * math.pi * i / n); d.ellipse([x - r * K, y - r * K, x + r * K, y + r * K], fill=c)

def _cenefa(d, c, n=36, s=3.6):       # Egipto: la cenefa de rectángulos de las pinturas, uno lleno y uno hueco
    for i in range(n):
        a = 2 * math.pi * i / n; da = math.pi / n * 0.62
        p = [_pos(a - da, RADIO - s), _pos(a + da, RADIO - s), _pos(a + da, RADIO + s), _pos(a - da, RADIO + s)]
        if i % 2 == 0: d.polygon(p, fill=c)
        else: d.polygon(p, outline=c, width=int(K * 1.2))

def _cuadrifolios(d, c, n=12):
    for i in range(n):
        a = 2 * math.pi * i / n; x, y = _pos(a); r = 1.9 * K
        for k in range(4):
            u = a + k * math.pi / 2; cx, cy = x + 2.1 * K * math.cos(u), y + 2.1 * K * math.sin(u)
            d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=c)

def _cruces_patadas(d, c, n=10, s=4.6):   # cruces patadas del Temple sobre el anillo
    for i in range(n):
        a = 2 * math.pi * i / n; x, y = _pos(a); pts = []
        for k in range(4):
            u = math.radians(90 * k - 90); dx, dy = math.cos(u), math.sin(u); nx, ny = -dy, dx
            for f_, q in ((0.15, -0.15), (1, -0.42), (1, 0.42)):
                pts.append((x + (dx * f_ + nx * q) * s * K, y + (dy * f_ + ny * q) * s * K))
        d.polygon(pts, fill=c)

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

def _engrane_manometro(d, c, n=16):   # Vapor victoriano: dientes de engrane por fuera y cuatro manómetros sobre el anillo
    for i in range(n):
        a = 2 * math.pi * i / n
        p = [_pos(a + s * 0.07, RADIO + h) for s, h in ((-1, 0), (-0.6, 9), (0.6, 9), (1, 0))]
        d.polygon(p, fill=c)
    for i in range(4):
        a = math.pi / 4 + i * math.pi / 2
        x, y = _pos(a); r = 11 * K
        d.ellipse([x - r, y - r, x + r, y + r], fill=c)
        d.ellipse([x - r * 0.72, y - r * 0.72, x + r * 0.72, y + r * 0.72], fill=(239, 228, 203, 255))
        for k in range(5):
            b = math.radians(135 + 67.5 * k)
            d.line([(x + r * 0.6 * math.cos(b), y + r * 0.6 * math.sin(b)), (x + r * 0.42 * math.cos(b), y + r * 0.42 * math.sin(b))], fill=c, width=int(K * 1.4))
        g = math.radians(300)
        d.line([(x, y), (x + r * 0.55 * math.cos(g), y + r * 0.55 * math.sin(g))], fill=(156, 61, 36, 255), width=int(K * 2))

def _canal(d, c, hueco=0.52, centro=-0.95):   # Frente exterior: el emblema del tema, el canal (un círculo roto) y la chispa
    # de tres trazos que se escapa por el hueco. El aro engruesa hacia el corte para que se lea roto; por dentro, solo los
    # rumbos de 30° de una pantalla de radar, y abajo a la izquierda, dos pilotos.
    caja = [(TAM / 2 - RADIO) * K, (TAM / 2 - RADIO) * K, (TAM / 2 + RADIO) * K, (TAM / 2 + RADIO) * K]
    a0, a1 = centro + hueco / 2, centro - hueco / 2 + 2 * math.pi
    d.arc(caja, math.degrees(a0), math.degrees(a1), fill=c, width=int(GROSOR * K * 1.35))
    for fin, sentido in ((a0, -1), (a1, 1)):     # los cabos: engruesan hacia el corte y acaban redondos
        tramo = 0.34
        pts_fuera, pts_dentro = [], []
        for k in range(25):
            s = k / 24
            a = fin - sentido * tramo * (1 - s)
            w = GROSOR / 2 * (1.35 + 1.75 * s ** 1.6)
            pts_fuera.append(_pos(a, RADIO + w)); pts_dentro.append(_pos(a, RADIO - w))
        d.polygon(pts_fuera + pts_dentro[::-1], fill=c)
        x, y = _pos(fin); r = GROSOR / 2 * 3.1 * K
        d.ellipse([x - r, y - r, x + r, y + r], fill=c)
    for i in range(12):                      # rumbos cada 30°, solo donde hay canal
        a = 2 * math.pi * i / 12
        if abs((a - centro + math.pi) % (2 * math.pi) - math.pi) < hueco / 2 + 0.4: continue
        d.line([_pos(a, RADIO - 2), _pos(a, RADIO - 8)], fill=c, width=int(K * 1.7))
    # La chispa: tres trazos gruesos que se abren hacia fuera desde el hueco, el del centro más largo.
    ox, oy = _pos(centro, RADIO - 24)        # el foco de la chispa queda dentro del canal; los trazos nacen ya separados
    for da, l0, l1, g in ((0, 13.0, 38.5, 3.0), (-0.52, 16.0, 35.0, 2.7), (0.52, 16.0, 35.0, 2.7)):
        u = centro + da
        p0 = (ox + l0 * K * math.cos(u), oy + l0 * K * math.sin(u)); p1 = (ox + l1 * K * math.cos(u), oy + l1 * K * math.sin(u))
        d.line([p0, p1], fill=c, width=int(GROSOR * K * g))
        for (x, y) in (p0, p1):
            r = GROSOR * K * g / 2; d.ellipse([x - r, y - r, x + r, y + r], fill=c)
    # Dos pilotos sobre el canal, en su aro de baquelita: el ámbar encendido y el rojo.
    for a, luz in ((math.radians(124), (242, 163, 58, 255)), (math.radians(146), (217, 55, 43, 255))):
        x, y = _pos(a); r = 8.4 * K
        d.ellipse([x - r, y - r, x + r, y + r], fill=(42, 38, 34, 255))
        r2 = 6.0 * K; d.ellipse([x - r2, y - r2, x + r2, y + r2], fill=luz)
        r3 = 1.8 * K; d.ellipse([x - r2 * 0.42 - r3, y - r2 * 0.42 - r3, x - r2 * 0.42 + r3, y - r2 * 0.42 + r3], fill=(255, 248, 230, 200))

def cinta(d, pts, w0, w1, c):
    """Un trazo de ancho variable (de w0 a w1, en px del lienzo) a lo largo de los puntos [pts], con los extremos lisos."""
    n = len(pts); izq, der = [], []
    for i, (x, y) in enumerate(pts):
        a = pts[max(i - 1, 0)]; b = pts[min(i + 1, n - 1)]
        dx, dy = b[0] - a[0], b[1] - a[1]; l = math.hypot(dx, dy) or 1
        w = (w0 + (w1 - w0) * i / (n - 1)) / 2
        izq.append((x - dy / l * w, y + dx / l * w)); der.append((x + dy / l * w, y - dx / l * w))
    d.polygon(izq + der[::-1], fill=c)


def cinta_var(d, pts, anchos, c):
    """Como [cinta], pero con un ancho por punto (en px del lienzo)."""
    n = len(pts); izq, der = [], []
    for i, (x, y) in enumerate(pts):
        a = pts[max(i - 1, 0)]; b = pts[min(i + 1, n - 1)]
        dx, dy = b[0] - a[0], b[1] - a[1]; l = math.hypot(dx, dy) or 1
        w = anchos[i] / 2
        izq.append((x - dy / l * w, y + dx / l * w)); der.append((x + dy / l * w, y - dx / l * w))
    d.polygon(izq + der[::-1], fill=c)


def ouroboros(d, cx, cy, radio, w_cabeza, w_cola, c, hueco=(0, 0, 0, 0), cabeza=-math.pi / 2, escamas=True):
    """La serpiente que se muerde la cola, en coordenadas del lienzo: el cuerpo sigue el círculo de [radio] en el sentido
    del reloj, de la cola (fina, ancho w_cola) al cuello (w_cabeza). La cabeza va de perfil en el ángulo [cabeza], con la
    mandíbula de arriba por fuera y la de abajo por dentro, cerradas sobre la cola, que le entra por la boca. Las escamas
    son medias lunas en hileras alternas, con el borde libre hacia la cola. [hueco] es el color con que se recortan las
    escamas, la boca y el ojo (transparente en una capa RGBA, 0 en una máscara)."""
    L = w_cabeza * 2.0; W = w_cabeza * 1.12
    cola0 = cabeza + L * 0.3 / radio                  # la punta de la cola, metida en la boca
    cuello = cabeza - L * 0.5 / radio + 2 * math.pi   # donde el cuerpo se vuelve cabeza
    n = 720
    angs = [cola0 + (cuello - cola0) * i / n for i in range(n + 1)]
    pts = [(cx + radio * math.cos(a), cy + radio * math.sin(a)) for a in angs]
    # El cuerpo engruesa rápido al salir de la boca y luego despacio hasta el cuello.
    anchos = [w_cola + (w_cabeza - w_cola) * (i / n) ** 0.55 for i in range(n + 1)]
    izq, der = [], []
    for (x, y), a, w in zip(pts, angs, anchos):
        nx, ny = math.cos(a), math.sin(a)
        izq.append((x + nx * w / 2, y + ny * w / 2)); der.append((x - nx * w / 2, y - ny * w / 2))
    d.polygon(izq + der[::-1], fill=c)
    largo = (cuello - cola0) * radio
    if escamas:
        # Hileras de escamas: una media luna cada [paso], alternando entre dos y tres por el ancho del cuerpo.
        paso = w_cabeza * 0.34
        k = 0; t = L * 0.75
        while t < largo - paso:
            i = int(t / largo * n); a = angs[i]; w = anchos[i]
            nx, ny = math.cos(a), math.sin(a); tx, ty = -ny, nx          # t: hacia la cabeza
            x, y = pts[i]
            r = w * 0.2
            # dos escamas por hilera, corridas media escama de una hilera a la otra (como tejas)
            for g in ((-0.22, 0.22) if k % 2 == 0 else (0.0,)):
                bx, by = x + nx * g * w, y + ny * g * w
                arco = [(bx - (math.cos(u) * tx - math.sin(u) * nx) * r, by - (math.cos(u) * ty - math.sin(u) * ny) * r)
                        for u in np.linspace(-1.2, 1.2, 9)]
                d.line(arco, fill=hueco, width=max(1, int(w * 0.07)), joint='curve')
            t += paso; k += 1
    # La cabeza, en el marco del cuello: t hacia donde avanza, n hacia fuera del círculo.
    hx, hy = cx + radio * math.cos(cabeza), cy + radio * math.sin(cabeza)
    tx, ty = -math.sin(cabeza), math.cos(cabeza); nx, ny = math.cos(cabeza), math.sin(cabeza)
    def Q(f, g): return (hx + tx * f * L + nx * g * W, hy + ty * f * L + ny * g * W)
    lab = w_cola / W / 2 + 0.07                       # dónde cierran los labios: apenas por fuera de la cola
    # Cráneo y mandíbula de arriba (por fuera): sube del cuello, se redondea sobre el ojo y baja al hocico romo.
    d.polygon([Q(-0.62, 0.46), Q(-0.4, 0.6), Q(-0.1, 0.68), Q(0.22, 0.64), Q(0.5, 0.55), Q(0.7, 0.46), Q(0.82, 0.38),
               Q(0.89, 0.29), Q(0.9, 0.2), Q(0.87, lab + 0.03), Q(0.8, lab), Q(0.3, lab), Q(0.3, -lab), Q(-0.62, -0.46)], fill=c)
    # Mandíbula de abajo (por dentro), un poco más corta, cerrada bajo la cola.
    d.polygon([Q(-0.62, -0.46), Q(-0.3, -0.58), Q(0.1, -0.56), Q(0.44, -0.46), Q(0.66, -0.34), Q(0.78, -0.22),
               Q(0.76, -lab), Q(0.3, -lab), Q(0.3, lab), Q(-0.62, 0.46)], fill=c)
    # Las comisuras: un corte fino que sigue los labios por encima y por debajo de la cola y se abre hacia atrás.
    for s_ in (1, -1):
        d.line([Q(0.84 if s_ > 0 else 0.72, s_ * (lab - 0.02)), Q(0.12, s_ * (lab - 0.02)), Q(-0.16, s_ * (lab + 0.14))],
               fill=hueco, width=max(1, int(W * 0.055)), joint='curve')
    # Ojo con su pupila, y dos cortes de las escamas grandes de la cabeza.
    ex, ey = Q(0.12, 0.38); r = W * 0.15
    d.ellipse([ex - r, ey - r, ex + r, ey + r], fill=hueco)
    r2 = r * 0.5; d.ellipse([ex - r2 + r * 0.25, ey - r2, ex + r2 + r * 0.25, ey + r2], fill=c)
    d.line([Q(-0.52, 0.3), Q(-0.24, 0.5)], fill=hueco, width=max(1, int(W * 0.06)))
    d.line([Q(-0.52, -0.3), Q(-0.24, -0.45)], fill=hueco, width=max(1, int(W * 0.06)))
    d.line([Q(0.62, 0.36), Q(0.74, 0.3)], fill=hueco, width=max(1, int(W * 0.05)))      # la fosa de la nariz


def _ouroboros(d, c):                 # Arcontes: el anillo es el ouroboros, la serpiente que se muerde la cola, grabado
    ouroboros(d, TAM / 2 * K, TAM / 2 * K, RADIO * K, 15.0 * K, 4.6 * K, c)

def torques(d, cx, cy, radio, w, c, hueco=(0, 0, 0, 0), abre=0.27, paso=None):
    """El torques galo visto de frente, en coordenadas del lienzo: el aro de oro torcido (una barra retorcida, con los surcos
    en diagonal de la torsión) que se abre abajo, y en cada punta su remate de tope (el tipo «buffer» de los torques de la
    Edad del Hierro): un collarín, una trompeta que se ensancha y el tope, un disco que se ve de canto, con los dos discos
    mirándose a través de la abertura. El aro es más delgado atrás (arriba) y engruesa hacia los remates. [w] es el ancho
    del aro junto a los remates; [abre], la mitad de la abertura en radianes; [hueco], el color de los surcos."""
    paso = paso or w * 0.62
    a0, a1 = math.pi / 2 + abre, math.pi / 2 + 2 * math.pi - abre      # el aro va de un remate al otro pasando por arriba
    largo_rem = w * 1.7                                                 # lo que ocupan collarín y trompeta, a lo largo del aro
    b0, b1 = a0 + largo_rem / radio, a1 - largo_rem / radio
    n = 900
    angs = [b0 + (b1 - b0) * i / n for i in range(n + 1)]
    def ancho(a):
        lejos = min(a - a0, a1 - a) / math.pi                            # 0 en los remates, 1 arriba
        return w * (1 - 0.3 * min(1, lejos) ** 1.2)
    fuera = [(cx + (radio + ancho(a) / 2) * math.cos(a), cy + (radio + ancho(a) / 2) * math.sin(a)) for a in angs]
    dentro = [(cx + (radio - ancho(a) / 2) * math.cos(a), cy + (radio - ancho(a) / 2) * math.sin(a)) for a in angs]
    d.polygon(fuera + dentro[::-1], fill=c)
    # La torsión: surcos en diagonal, un poco curvos (la hélice de la barra vista de frente).
    s = 0.0; total = (b1 - b0) * radio
    while s < total:
        a = b0 + s / radio; wa = ancho(a)
        pts = []
        for k in range(7):
            t = k / 6 - 0.5                                              # de dentro (-0.5) a fuera (0.5)
            desliz = (t + 0.5) * wa * 0.95 - 0.12 * wa * math.sin((t + 0.5) * math.pi)
            aa = a + desliz / radio
            pts.append((cx + (radio + t * wa * 1.04) * math.cos(aa), cy + (radio + t * wa * 1.04) * math.sin(aa)))
        d.line(pts, fill=hueco, width=max(1, int(wa * 0.14)), joint='curve')
        s += paso
    # Los remates.
    for fin, sentido in ((a0, -1), (a1, 1)):         # sentido: hacia dónde queda el aro desde el remate
        tx, ty = -math.sin(fin) * sentido, math.cos(fin) * sentido       # tangente hacia la abertura
        nx, ny = math.cos(fin), math.sin(fin)                            # hacia fuera
        bx, by = cx + radio * math.cos(b0 if sentido < 0 else b1), cy + radio * math.sin(b0 if sentido < 0 else b1)
        def P(f, g): return (bx + tx * f * w + nx * g * w, by + ty * f * w + ny * g * w)
        # collarín
        d.polygon([P(-0.08, -0.7), P(0.32, -0.7), P(0.32, 0.7), P(-0.08, 0.7)], fill=c)
        d.line([P(-0.08, -0.66), P(-0.08, 0.66)], fill=hueco, width=max(1, int(w * 0.13)))
        d.line([P(0.32, -0.64), P(0.32, 0.64)], fill=hueco, width=max(1, int(w * 0.11)))
        # trompeta que se abre hasta el tope
        pts_a = [P(0.32 + 1.0 * u, 0.48 + 0.75 * u ** 2.2) for u in np.linspace(0, 1, 14)]
        pts_b = [P(0.32 + 1.0 * u, -0.48 - 0.75 * u ** 2.2) for u in np.linspace(0, 1, 14)]
        d.polygon(pts_a + pts_b[::-1], fill=c)
        # el tope: un disco de canto, más alto que la trompeta, con el borde redondeado y una moldura en la cara
        g0, g1, h_in, h_out = 1.3, 2.1, 1.55, 0.82      # el disco asoma más hacia dentro (por fuera queda el borde del ícono)
        d.polygon([P(g0, -h_in), P(g1, -h_in), P(g1, h_out), P(g0, h_out)], fill=c)
        for g_ in (h_out, -h_in):
            x_, y_ = P((g0 + g1) / 2, g_); r_ = (g1 - g0) / 2 * w
            d.ellipse([x_ - r_, y_ - r_, x_ + r_, y_ + r_], fill=c)
        d.line([P(g0, -h_in * 0.97), P(g0, h_out * 0.96)], fill=hueco, width=max(1, int(w * 0.12)))


def _torques(d, c, hueco=(0, 0, 0, 0)):   # Galia: el anillo es el torques de oro, abierto abajo, con sus dos remates
    torques(d, TAM / 2 * K, TAM / 2 * K, RADIO * K, 11.0 * K, c, hueco)


def latigo_puntos(cx, cy, radio, a0=math.radians(116), a1=math.radians(366), centro_rizo=(math.radians(46), 0.9),
                  rizo=(0.24, 0.05, 1.45), onda=0.022):
    """La línea de látigo como marco: un tallo que nace abajo a la izquierda (en [a0]), da la vuelta al círculo de
    [radio] en el sentido del reloj, ondulando apenas ([onda]), y al llegar abajo a la derecha ([a1]) no cierra: se
    dobla sobre sí mismo y termina en un rizo, una espiral que se cierra alrededor de [centro_rizo] (ángulo, radio en
    fracción de [radio]). [rizo] = (radio inicial y final de la espiral, en fracción de [radio], y vueltas). Devuelve
    los puntos del eje y, para cada uno, cuánto va del tallo (0 en la base, 1 en la punta del rizo)."""
    pts = []
    n = 700
    for i in range(n + 1):
        u = i / n; a = a0 + (a1 - a0) * u
        r = radio * (1 + onda * math.sin(u * 2 * math.pi * 1.5 + 0.6))
        pts.append((cx + r * math.cos(a), cy + r * math.sin(a)))
    # El rizo: una espiral que se cierra en el sentido del reloj alrededor de su centro. Sin [centro_rizo], el centro
    # queda en el mismo radio que la punta del arco, por dentro, y el tallo entra en la espiral sin quiebre: solo se
    # curva de golpe más cerrado (el chasquido del látigo).
    r0, r1, vueltas = rizo
    if centro_rizo is None:
        ccx, ccy = pts[-1][0] - radio * r0 * math.cos(a1), pts[-1][1] - radio * r0 * math.sin(a1); f0 = a1
    else:
        ac, rc = centro_rizo
        ccx, ccy = cx + radio * rc * math.cos(ac), cy + radio * rc * math.sin(ac)
        f0 = ac - math.radians(40)                # la espiral empieza por fuera del centro, del lado de donde viene el tallo
    espiral = []
    m = 360
    for i in range(m + 1):
        t = i / m; f = f0 + vueltas * 2 * math.pi * t
        rr = radio * (r0 + (r1 - r0) * (1 - (1 - t) ** 1.6))
        espiral.append((ccx + rr * math.cos(f), ccy + rr * math.sin(f)))
    if centro_rizo is None:
        pts = pts + espiral[1:]
        largos = [0.0]
        for i in range(1, len(pts)):
            largos.append(largos[-1] + math.hypot(pts[i][0] - pts[i - 1][0], pts[i][1] - pts[i - 1][1]))
        return pts, [x / largos[-1] for x in largos]
    # El paso del arco a la espiral: una curva de Bézier que sigue la tangente de los dos.
    p0 = pts[-1]; t0 = (-math.sin(a1), math.cos(a1))
    p3 = espiral[0]; t3 = (-math.sin(f0), math.cos(f0))
    l = math.hypot(p3[0] - p0[0], p3[1] - p0[1])
    p1 = (p0[0] + t0[0] * l * 0.55, p0[1] + t0[1] * l * 0.55)
    p2 = (p3[0] - t3[0] * l * 0.55, p3[1] - t3[1] * l * 0.55)
    paso = [tuple((1 - t) ** 3 * a + 3 * (1 - t) ** 2 * t * b + 3 * (1 - t) * t ** 2 * c_ + t ** 3 * e
                  for a, b, c_, e in zip(p0, p1, p2, p3)) for t in np.linspace(0, 1, 40)[1:-1]]
    pts = pts + paso + espiral
    # Cuánto va del tallo, por largo recorrido.
    largos = [0.0]
    for i in range(1, len(pts)):
        largos.append(largos[-1] + math.hypot(pts[i][0] - pts[i - 1][0], pts[i][1] - pts[i - 1][1]))
    return pts, [x / largos[-1] for x in largos]


def hoja_lanza(d, x, y, ang, largo, ancho, c, curva=0.0, n=40):
    """Una hoja lanceolada (como la del iris o la del lirio) que nace en (x, y) hacia [ang] y se curva [curva] (radianes
    de giro acumulado hasta la punta, + en el sentido del reloj)."""
    eje = [(x, y)]; a = ang; paso = largo / n
    for i in range(n):
        a += curva / n
        x += paso * math.cos(a); y += paso * math.sin(a); eje.append((x, y))
    anchos = [ancho * math.sin(math.pi * (i / n) ** 0.7) ** 0.85 + 0.4 for i in range(n + 1)]
    cinta_var(d, eje, anchos, c)
    return eje


def latigo(d, cx, cy, radio, w0, w1, c, hojas=((0.0, math.radians(152), 0.44, 0.13, -0.3),), **kw):
    """Dibuja la línea de látigo de [latigo_puntos]: el tallo engruesa en la base (w0) y se afina hasta la punta del rizo
    (w1), con la punta redonda. [hojas]: (dónde nace, en fracción del tallo; giro respecto del tallo; largo y ancho, en
    fracción de [radio]; curva)."""
    pts, avance = latigo_puntos(cx, cy, radio, **kw)
    anchos = [(w1 + (w0 - w1) * (1 - a) ** 1.15) * min(1.0, 0.25 + a / 0.035) for a in avance]   # brota fino junto a la hoja
    cinta_var(d, pts, anchos, c)
    x, y = pts[-1]; r = anchos[-1] * 0.7; d.ellipse([x - r, y - r, x + r, y + r], fill=c)
    for donde, giro, largo, ancho, curva in hojas:
        i = min(len(pts) - 2, int(donde * (len(pts) - 1)))
        tx, ty = pts[i + 1][0] - pts[i][0], pts[i + 1][1] - pts[i][1]
        hoja_lanza(d, pts[i][0], pts[i][1], math.atan2(ty, tx) + giro, largo * radio, ancho * radio, c, curva)
    return pts


def _latigo(d, c, hueco=(0, 0, 0, 0)):   # Art nouveau: el anillo es la línea de látigo, un tallo que se cierra en un rizo
    latigo(d, TAM / 2 * K, TAM / 2 * K, RADIO * 0.975 * K, 12.5 * K, 5.6 * K, c)


def petalo_redondo(P, ang, r0, r1, ancho_punta, ancho_raiz, n=40):
    """Un pétalo redondo de botón de 1967, de la raíz (a [r0] del centro) a la punta (a [r1]), en la dirección [ang]: se
    ensancha hacia fuera y termina en medio círculo. [P] lleva del plano de la flor (x, y) a pixeles."""
    ca, sa = math.cos(ang), math.sin(ang)
    rc = r1 - ancho_punta / 2
    lado = [(r0 + (rc - r0) * t, ancho_raiz / 2 + (ancho_punta - ancho_raiz) / 2 * math.sin(t * math.pi / 2) ** 0.75)
            for t in np.linspace(0, 1, n)]
    arco = [(rc + ancho_punta / 2 * math.cos(a), ancho_punta / 2 * math.sin(a)) for a in np.linspace(math.pi / 2, -math.pi / 2, 36)]
    q = lambda u, v: P(u * ca - v * sa, u * sa + v * ca)
    return [q(u, w) for u, w in lado] + [q(u, v) for u, v in arco] + [q(u, -w) for u, w in lado[::-1]]


def margarita(d, c, cx, cy, escala, n=8, r0=0.035, r1=0.305, punta=0.15, raiz=0.035, fase=-math.pi / 2 + math.pi / 8, encoge=0.0):
    """La margarita de los botones y los carteles de 1967: [n] pétalos redondos alrededor del corazón, medidos en
    fracciones de [escala] (px) desde (cx, cy). [encoge] la achica por dentro de su contorno: dos margaritas encogidas,
    una menos la otra, dan el eco (la línea que repite la forma por dentro, como en los carteles)."""
    P = lambda x, y: (cx + x * escala, cy + y * escala)
    for k in range(n):
        a = 2 * math.pi * k / n + fase
        d.polygon(petalo_redondo(P, a, r0, r1 - encoge, punta - 2 * encoge, max(0.004, raiz - 2 * encoge)), fill=c)


def _margarita(d, c, hueco=(0, 0, 0, 0), encoge=0.0):   # Psicodelia: el anillo se vuelve la flor; la huella es su corazón
    margarita(d, c, TAM / 2 * K, TAM / 2 * K, TAM * K, r1=RADIO_CHAPA, encoge=encoge)


def oro_relieve(alto, fuerza, sem, luz=(-0.55, -0.7, 1.0), brillo=40, tonos=None, grano=0.035, ambiente=0.0, plano=None):
    """Metal en relieve a partir de un campo de alturas [alto] (0–1, ya suavizado): la luz viene de arriba a la izquierda;
    lo que mira a la luz brilla, lo plano queda en el oro medio y lo que le da la espalda se hunde en el pardo. [fuerza]
    escala las pendientes (más, relieve más marcado). [ambiente] mezcla el reflejo del cuarto (claro arriba a la
    izquierda, oscuro abajo a la derecha), que es lo que hace que el metal parezca metal; [plano], si se da, es el tono
    (0–1) de lo que mira de frente. Devuelve el RGB (float)."""
    gy, gx = np.gradient(alto)
    nx, ny, nz = -gx * fuerza, -gy * fuerza, np.ones_like(alto)
    nn = np.sqrt(nx ** 2 + ny ** 2 + nz ** 2); nx, ny, nz = nx / nn, ny / nn, nz / nn
    lx, ly, lz = luz; ll = math.sqrt(lx * lx + ly * ly + lz * lz); lx, ly, lz = lx / ll, ly / ll, lz / ll
    dif = np.clip(nx * lx + ny * ly + nz * lz, 0, 1)
    hx, hy, hz = lx, ly, lz + 1; hh = math.sqrt(hx * hx + hy * hy + hz * hz)
    esp = np.clip((nx * hx + ny * hy + nz * hz) / hh, 0, 1) ** brillo
    tonos = tonos or [(0.0, '#2A1A05'), (0.3, '#6E4A10'), (0.52, '#A87A22'), (0.68, '#C9962B'), (0.82, '#E2B850'),
                      (0.93, '#F0D27A'), (1.0, '#FBEBB4')]
    t = dif
    if ambiente:
        # El reflejo de la mirada (de frente) en la superficie, buscado en un cuarto con una ventana grande arriba a la
        # izquierda, una luz de relleno tenue a la derecha y lo demás en penumbra; lo que mira de frente refleja [plano].
        rx, ry = 2 * nz * nx, 2 * nz * ny
        ventana = np.clip(1 - np.sqrt((rx + 0.55) ** 2 + (ry + 0.62) ** 2) / 0.62, 0, 1) ** 0.8
        relleno = np.clip(1 - np.sqrt((rx - 0.75) ** 2 + (ry - 0.1) ** 2) / 0.45, 0, 1) ** 1.5
        sombra = np.clip(np.sqrt(rx ** 2 + ry ** 2) * 1.4, 0, 1) * np.clip((rx + ry) * 1.5 + 0.3, 0, 1)
        refl = np.clip((0.5 if plano is None else plano) + 0.55 * ventana + 0.18 * relleno - 0.45 * sombra, 0, 1)
        t = (1 - ambiente) * dif + ambiente * refl
    if grano:
        g = np.random.default_rng(sem).standard_normal(alto.shape).astype(np.float32)
        g = np.asarray(Image.fromarray(np.clip(g * 40 + 128, 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.0)), np.float32)
        t = np.clip(t + (g - 128) / 40 * grano, 0, 1)
    xs = [p for p, _ in tonos]; cs = np.array([rgb(c) for _, c in tonos])
    col = np.stack([np.interp(t, xs, cs[:, k]) for k in range(3)], -1)
    return col + esp[..., None] * np.array([70, 62, 40], np.float32)


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

def _puntos_barras(d, c, n=14):        # Mesoamérica: numerales mayas en el anillo, el siete: la barra por fuera y dos puntos por dentro
    for i in range(n):
        a = 2 * math.pi * i / n
        ca, sa = math.cos(a + math.pi / 2), math.sin(a + math.pi / 2)
        x, y = _pos(a, RADIO + 4.6); l = 4.4 * K; g = 1.3 * K
        cx, cy = math.cos(a), math.sin(a)
        d.polygon([(x - ca * l - cx * g, y - sa * l - cy * g), (x + ca * l - cx * g, y + sa * l - cy * g),
                   (x + ca * l + cx * g, y + sa * l + cy * g), (x - ca * l + cx * g, y - sa * l + cy * g)], fill=c)
        for lado in (-1, 1):
            px, py = _pos(a + lado * 0.045, RADIO - 4.4); r = 1.6 * K
            d.ellipse([px - r, py - r, px + r, py + r], fill=c)

def _hojas_alternas(d, c, n=18):
    for i in range(n):
        a = 2 * math.pi * i / n; lado = 1 if i % 2 else -1
        x, y = _pos(a, RADIO + lado * 4); s = 5 * K; ang = a + math.pi / 2 + lado * 0.6
        d.ellipse([x - s * 0.5, y - s * 0.5, x + s * 0.5, y + s * 0.5], fill=c)
        d.line([_pos(a), (x + s * 0.5 * math.cos(ang), y + s * 0.5 * math.sin(ang))], fill=c, width=K)

def _hiedra(d, c, n=14):               # hojas de hiedra de cinco puntas, alternas hacia fuera y hacia dentro del anillo
    pts = [(0, 0.5), (-0.55, 0.35), (-0.8, -0.05), (-0.32, -0.05), (0, -0.5), (0.32, -0.05), (0.8, -0.05), (0.55, 0.35)]
    for i in range(n):
        a = 2 * math.pi * i / n; lado = 1 if i % 2 else -1
        x, y = _pos(a, RADIO + lado * 6); s = 8.5 * K
        ang = a + (0 if lado > 0 else math.pi)             # la punta mira hacia fuera o hacia dentro
        ca, sa = math.cos(ang + math.pi / 2), math.sin(ang + math.pi / 2)
        d.polygon([(x + (px * ca - py * sa) * s, y + (px * sa + py * ca) * s) for px, py in pts], fill=c)

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

def _sellos(d, c, n=12):              # sellos hanko cuadrados, con su marco en reserva y un trazo
    for i in range(n):
        ang = 2 * math.pi * i / n; x, y = _pos(ang); s = 5.4 * K; q = 3.3 * K
        cs, sn = math.cos(math.radians(-4)), math.sin(math.radians(-4))   # derechos, apenas torcidos, como estampados a mano
        def rot(px, py): return (x + px * cs - py * sn, y + px * sn + py * cs)
        d.polygon([rot(-s, -s), rot(s, -s), rot(s, s), rot(-s, s)], fill=c)
        d.polygon([rot(-q, -q), rot(q, -q), rot(q, q), rot(-q, q)], outline=(0, 0, 0, 0), width=int(K * 0.9))
        d.line([rot(0, -q * 0.7), rot(0, q * 0.7)], fill=(0, 0, 0, 0), width=int(K * 0.9))

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

def _burbujas(d, c, n=22):            # burbujas de varios tamaños, unas por dentro y otras por fuera del anillo
    for i in range(n):
        a = 2 * math.pi * i / n + 0.07 * (i % 3)
        r = (2.2 + 1.6 * ((i * 7) % 3)) * K
        x, y = _pos(a, RADIO + (3.5 if i % 2 else -3.5))
        d.ellipse([x - r, y - r, x + r, y + r], outline=c, width=int(K * 1.1))

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

def _rosa(d, c):                      # las 32 cuartas de la rosa (las cardinales, largas), la flor de lis al norte y la «X»
    for i in range(32):
        a = -math.pi / 2 + 2 * math.pi * i / 32
        largo = 9 if i % 8 == 0 else (5 if i % 2 == 0 else 3)
        d.line([_pos(a, RADIO - largo), _pos(a, RADIO + (largo if i % 8 == 0 else 0))], fill=c, width=int(K * (1.8 if i % 8 == 0 else 1.1)))
    x, y = _pos(-math.pi / 2, RADIO + 9); s = 4.5 * K
    d.polygon([(x, y - s), (x + s * 0.6, y + s * 0.3), (x, y), (x - s * 0.6, y + s * 0.3)], fill=c)
    x, y = _pos(math.pi * 0.75, RADIO); s = 4.2 * K
    d.line([(x - s, y - s), (x + s, y + s)], fill=c, width=int(K * 1.8)); d.line([(x - s, y + s), (x + s, y - s)], fill=c, width=int(K * 1.8))

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

def _mano(d, c, x, y, s, ang):
    """Una mano pequeña (palma, cuatro dedos y pulgar) de alto 2·s, con los dedos hacia [ang]."""
    ca, sa = math.cos(ang + math.pi / 2), math.sin(ang + math.pi / 2)
    def p(px, py): return (x + (px * ca - py * sa) * s, y + (px * sa + py * ca) * s)
    d.polygon([p(-0.44, -0.1), p(0.4, -0.1), p(0.4, 0.6), p(0.26, 1.0), p(-0.3, 1.0), p(-0.44, 0.6)], fill=c)
    for fx, arriba, an in ((-0.34, -0.62, 0.17), (-0.13, -0.86, 0.19), (0.1, -0.98, 0.19), (0.3, -0.8, 0.18)):
        d.polygon([p(fx - an / 2, 0), p(fx - an / 2, arriba + an / 2), p(fx, arriba), p(fx + an / 2, arriba + an / 2), p(fx + an / 2, 0)], fill=c)
    d.polygon([p(0.3, 0.5), p(0.36, 0.08), p(0.7, -0.14), p(0.8, -0.02), p(0.42, 0.6)], fill=c)

def _manos(d, c, n=24):                 # Prehistoria: puntos de ocre de tamaño desparejo y, cada cuarto, una mano hacia fuera
    for i in range(n):
        a = 2 * math.pi * i / n
        if i % 6 == 0:
            x, y = _pos(a, RADIO + 2); _mano(d, c, x, y, 12 * K, a)
        else:
            x, y = _pos(a); r = (2.4 + (i * 7 % 4) * 0.5) * K
            d.ellipse([x - r, y - r, x + r, y + r], fill=c)


# tema: (textura, fondo, tinta, fuerza de la textura en el fondo y en la huella, acabado, motivo)
# Acabados: 'tinta' (se asienta en el soporte), 'dorado' (brillo metálico leve), 'ferrogalica' (sepia que se aclara
# donde la pluma tenía menos tinta), 'fosforo' (resplandor de pantalla), 'esmalte' (relieve suave), 'grabado' (talla en
# hueco sobre piedra pulida, con el anillo: la huella y el motivo son un solo grabado), 'oro' (oro fundido en relieve, con
# su sombra: el anillo y la huella son una sola pieza), 'nacar' (nácar engastado en oro pálido) y 'chapa' (un botón de
# solapa: la huella impresa en tintas planas bajo la cúpula de celuloide, con su brillo).
TEMAS = {
    'romano':       (travertino,    '#E5E1D9', '#6E5424',  10, 6, 'dorado',      _hojitas),
    'urbe':         (revoque,       '#E7C793', '#7A2E1C',  13, 5, 'tinta',       _cupolas),
    'ciber':        (pantalla,      '#0D1117', '#7FE3F0',   5, 4, 'fosforo',     _rayas),
    'neon':         (callejon,      '#150C19', '#FF5FAE',   6, 4, 'fosforo',     _tubos),
    'heleno':       (pario,         '#EEEDE9', '#3D2459',   9, 5, 'tinta',       _perlas),
    'egipto':       (papiro_nilo,   '#E6D39E', '#1D2F7A',   8, 5, 'esmalte',     _cenefa),
    'medieval':     (vitela,        '#E8E2D6', '#33261A',   9, 6, 'ferrogalica', _cuadrifolios),
    'templario':    (lino,          '#EDE6D6', '#9A1A1F',   8, 5, 'esmalte',     _cruces_patadas),
    'distopia':     (papel,         '#EFE9DA', '#1A1A1A',  10, 6, 'tinta',       _marcas),
    'frente':       (consola,       '#E4DED1', '#B85C0F',  10, 4, 'piloto',      _canal),
    'arcontes':     (heliotropo,    '#2B4636', '#D9D6BE',   1, 0, 'grabado',     _ouroboros),
    'galia':        (roble,         '#3A2A1B', '#C9962B',   1, 0, 'oro',         _torques),
    'nouveau':      (vidrio_nancy,  '#40384F', '#D9BE7A',   1, 0, 'nacar',       _latigo),
    'psicodelia':   (mezclilla,     '#24324F', '#FFF6E6',   1, 0, 'chapa',       _margarita),
    'refugio':      (hormigon,      '#1B1C1A', '#9BE8AE',   8, 5, 'fosforo',     _dientes),
    'orden':        (acero,         '#16171A', '#C2A15E',   9, 5, 'dorado',      _remaches),
    'vapor':        (hollin_plano,  '#E6D9BD', '#7A4E12',  10, 5, 'dorado',      _engrane_manometro),
    'laboratorio':  (cuadricula,    '#F1F2F1', '#1E5A66',   7, 4, 'tinta',       _marcas),
    'colonos':      (gaceta,        '#E7E3DA', '#22324D',   9, 6, 'tinta',       _cuerda),
    'napoleon':     (marmol,        '#EDEBE6', '#1F2B5C',  12, 5, 'esmalte',     _estrellas),
    'deco':         (laca,          '#15120E', '#D9B44A',   9, 5, 'dorado',      _abanicos),
    'botanico':     (herbario,      '#EAE9E1', '#3B2E1D',   9, 6, 'ferrogalica', _hojas_alternas),
    'ruinas':       (hormigon_agrietado, '#E2E2DA', '#3E5A2A', 9, 5, 'tinta',    _hiedra),
    'navidad':      (fieltro_nieve, '#1C1E1D', '#E4C987',   8, 5, 'dorado',      _copos),
    'seda':         (seda,          '#F2EFE8', '#862A1D',   6, 5, 'tinta',       _monedas),
    'edo':          (washi,         '#F1EBDD', '#1E3A5F',   7, 5, 'tinta',       _sellos),
    'gotico':       (piedra,        '#1E1E21', '#D8CCAE',   9, 6, 'dorado',      _trebol),
    'arcano':       (archivo_manchado, '#E3D9C1', '#1E3329', 10, 6, 'ferrogalica', _ganchos),
    'espacio':      (polvo_estelar, '#0F1012', '#B0C8F2',   4, 4, 'fosforo',     _orbita),
    'abismo':       (agua_abisal,   '#06141D', '#7FE9D2',   6, 4, 'fosforo',     _burbujas),
    'andalus':      (lazo,          '#F0ECE4', '#1F3F5C',   6, 5, 'esmalte',     _estrellas),
    'meso':         (amate,         '#E6D3AE', '#A3301E',  10, 5, 'tinta',       _puntos_barras),
    'mongol':       (fieltro,       '#EFEBE3', '#1F3D78',   6, 5, 'tinta',       _rombos),
    'renacimiento': (construccion,  '#EAE4D9', '#7C3B23',   8, 5, 'ferrogalica', _compas),
    'pirata':       (carta_nautica, '#E3D8BB', '#2E2214',   9, 5, 'tinta',       _rosa),
    'urss':         (cartel,        '#ECE7DE', '#8E1D1D',  10, 6, 'tinta',       _estrellas),
    'revolucion':   (verjurado,     '#F1EFEA', '#1F2A5A',  11, 5, 'tinta',       _tricolor),
    'antiguas':     (pizarra,       '#1D1F1E', '#C6A65A',   9, 6, 'dorado',      _runas),
    'cueva':        (caliza,        '#E6D6BC', '#A2401F',  10, 5, 'tinta',       _manos),
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
    if motivo not in (_rayas, _canal, _ouroboros, _torques, _latigo):
        d.ellipse(caja, outline=c, width=int(GROSOR * K))
    if motivo is _monedas: _monedas(d, c, (0, 0, 0, 0))
    else: motivo(d, c, **VARIANTES.get(tema, {}))
    return capa.resize((TAM, TAM), Image.LANCZOS)


def tallado(m, tinta, sem, sombra='#0E1A13', suave=1.1):
    """Un grabado en hueco sobre piedra pulida (máscara [m], 0–1, de cualquier tamaño): el fondo del corte, mate y más
    claro que la piedra (la piedra cortada se ve blanquecina); la pared de arriba a la izquierda, en sombra, y la de abajo a
    la derecha, con luz (la luz viene de arriba a la izquierda); y un filo oscuro alrededor, donde el pulido se rompe.
    [suave] es el ancho de las paredes en px (1.1 a 432; más en lienzos más grandes)."""
    alto, ancho = m.shape
    b = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(suave)), np.float32) / 255
    gy, gx = np.gradient(b)
    pend = (gx + gy) * suave / 1.1                    # > 0 en la pared de arriba a la izquierda (por dentro del corte)
    grano = np.random.default_rng(sem + 21).standard_normal((alto, ancho)).astype(np.float32)
    # El grano del corte, apenas desenfocado: más fino que un píxel suelto (y el WebP sin pérdida pesa menos).
    grano = np.asarray(Image.fromarray(np.clip(grano * 40 + 128, 0, 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(0.8 if suave > 1.5 else 0.6)), np.float32)
    grano = (grano - 128) / 16
    col = rgb(tinta)[None, None, :] * (0.93 + 0.05 * grano[..., None])
    col = col * (1 - np.clip(pend * 2.4, 0, 0.55))[..., None]
    col = col + (np.clip(-pend * 2.0, 0, 0.3) * 60)[..., None]
    capa = Image.fromarray(np.dstack([np.clip(col, 0, 255), m * 255]).astype(np.uint8), 'RGBA')
    filo = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(suave * 1.45)), np.float32) / 255
    base = Image.fromarray(np.dstack([np.broadcast_to(rgb(sombra), (alto, ancho, 3)), np.clip(filo * 1.5, 0, 1) * (1 - m) * 0.55 * 255]).astype(np.uint8), 'RGBA')
    base.alpha_composite(capa)
    return base


def suavizar(a, s):
    """Desenfoque gaussiano de un campo en coma flotante (sin pasar por 8 bits, para que las pendientes no se escalonen)."""
    p = int(3 * s) + 1
    b = np.pad(a, p, mode='edge')
    fy = np.fft.fftfreq(b.shape[0])[:, None]; fx = np.fft.rfftfreq(b.shape[1])[None, :]
    g = np.exp(-2 * (math.pi * s) ** 2 * (fx ** 2 + fy ** 2))
    return np.fft.irfft2(np.fft.rfft2(b) * g, s=b.shape)[p:p + a.shape[0], p:p + a.shape[1]].astype(np.float32)


def sombra_de(alfa, dx, dy, radio, fuerza, color='#0A0603'):
    """La sombra que deja una pieza con alfa [alfa] (0–1) cuando la luz viene de arriba a la izquierda."""
    a = np.roll(np.roll(suavizar(alfa, radio), int(round(dy)), 0), int(round(dx)), 1)
    return Image.fromarray(np.dstack([np.broadcast_to(rgb(color), alfa.shape + (3,)), np.clip(a * fuerza, 0, 1) * 255]).astype(np.uint8), 'RGBA')


def oro_icono(tema, sem):
    """Galia: el torques y la huella en oro fundido, en relieve (el aro torcido, los remates, cada cresta como un hilo de
    oro redondo), sobre la tabla de roble, con su sombra. Se calcula al cuádruple y se reduce."""
    S = TAM * K
    lienzo = Image.new('L', (S, S), 0)
    TEMAS[tema][6](ImageDraw.Draw(lienzo), 255, 0)
    aro = np.asarray(lienzo, np.float32) / 255
    huella = maestra.mascara(S, S * 0.46)
    m = np.maximum(aro, huella)
    alto = suavizar(m, 1.3 * K) * 0.55 + suavizar(m, 3.0 * K) * 0.45
    alto = alto * (0.35 + 0.65 * m)                        # los surcos de la torsión y los bordes, bien hundidos
    color = oro_relieve(suavizar(alto, 0.6 * K), 9.0 * K, sem)
    capa = Image.fromarray(np.dstack([np.clip(color, 0, 255), m * 255]).astype(np.uint8), 'RGBA')
    capa = capa.convert('RGBa').resize((TAM, TAM), Image.LANCZOS).convert('RGBA')
    a = np.asarray(capa, np.float32)[..., 3] / 255
    final = sombra_de(a, 3.2, 4.2, 3.4, 0.62)
    final.alpha_composite(sombra_de(a, 1.0, 1.4, 1.0, 0.5))
    final.alpha_composite(capa)
    return final


# El oro pálido del tema (#D9BE7A, con su brillo #F2E2B0): más claro y más frío que el oro de Galia.
ORO_PALIDO = [(0.0, '#2E2410'), (0.25, '#6A5628'), (0.45, '#A08850'), (0.6, '#C2A86A'), (0.74, '#D9BE7A'), (0.86, '#E8D49C'),
              (0.94, '#F2E2B0'), (1.0, '#FFF7DE')]


def nacar(alto, fuerza, sem, base='#E6E3EC', luz=(-0.55, -0.7, 1.0), cuanto=0.78):
    """El nácar en relieve a partir de un campo de alturas: blanco perla, con los colores del arco que cambian según hacia
    dónde mira la superficie (rosa, lila, verde de agua, crema) y en manchas grandes; brillo suave arriba a la izquierda."""
    gy, gx = np.gradient(alto)
    nx, ny, nz = -gx * fuerza, -gy * fuerza, np.ones_like(alto)
    nn = np.sqrt(nx ** 2 + ny ** 2 + nz ** 2); nx, ny, nz = nx / nn, ny / nn, nz / nn
    lx, ly, lz = luz; ll = math.sqrt(lx * lx + ly * ly + lz * lz); lx, ly, lz = lx / ll, ly / ll, lz / ll
    dif = np.clip(nx * lx + ny * ly + nz * lz, 0, 1)
    hx, hy, hz = lx, ly, lz + 1; hh = math.sqrt(hx * hx + hy * hy + hz * hz)
    esp = np.clip((nx * hx + ny * hy + nz * hz) / hh, 0, 1) ** 26
    tam = alto.shape[0]
    fase = (nx * 2.6 + ny * 1.8) + nube(sem, 5, tam) * 1.6 + nube(sem + 1, 13, tam) * 0.5
    arco = [rgb('#F7C4DA'), rgb('#CEC2F8'), rgb('#B6E8D5'), rgb('#F7DFB0')]
    t = (fase % 4.0)
    i0 = np.floor(t).astype(int) % 4; f = (t - np.floor(t))[..., None]
    f = f * f * (3 - 2 * f)
    tinte = np.stack(arco)[i0] * (1 - f) + np.stack(arco)[(i0 + 1) % 4] * f
    col = rgb(base)[None, None, :] * (1 - cuanto) + tinte * cuanto
    col = col * (0.66 + 0.4 * dif[..., None]) + esp[..., None] * np.array([70, 68, 72], np.float32)
    return col


def nacar_icono(tema, sem):
    """Art nouveau: la huella de nácar, cada cresta engastada en un hilo de oro (como un esmalte con sus tabiques), y el
    marco de látigo de oro pálido en relieve, sobre el vidrio, con su sombra. Se calcula al cuádruple y se reduce."""
    S = TAM * K
    lienzo = Image.new('L', (S, S), 0)
    TEMAS[tema][6](ImageDraw.Draw(lienzo), 255, 0)
    aro = np.asarray(lienzo, np.float32) / 255
    huella = maestra.mascara(S, S * 0.46)
    # El marco: un tallo redondo de oro (alto en el centro del trazo), con la luz de arriba a la izquierda.
    alto = suavizar(aro, 1.6 * K) * 0.5 + suavizar(aro, 3.4 * K) * 0.5
    alto = alto * (0.35 + 0.65 * aro)
    oro = oro_relieve(suavizar(alto, 0.6 * K), 9.0 * K, sem, tonos=ORO_PALIDO, grano=0.02, ambiente=0.55, plano=0.62)
    # La huella: el nácar abombado en cada cresta y, en el borde, el hilo de oro que la engasta.
    alto_h = suavizar(huella, 1.7 * K) * (0.3 + 0.7 * huella)
    alto_h = alto_h / max(1e-3, float(alto_h.max()))
    perla = nacar(suavizar(alto_h, 0.5 * K), 7.0 * K, sem + 3)
    nucleo = np.clip((suavizar(huella, 1.0 * K) - 0.74) / 0.12, 0, 1) * huella
    hilo = oro_relieve(suavizar(alto_h, 0.5 * K), 7.0 * K, sem + 4, tonos=ORO_PALIDO, grano=0.0, ambiente=0.5, plano=0.6)
    color_h = hilo * (1 - nucleo[..., None]) + perla * nucleo[..., None]
    m = np.maximum(aro, huella)
    color = np.where((aro >= huella)[..., None], oro, color_h)
    capa = Image.fromarray(np.dstack([np.clip(color, 0, 255), m * 255]).astype(np.uint8), 'RGBA')
    capa = capa.convert('RGBa').resize((TAM, TAM), Image.LANCZOS).convert('RGBA')
    a = np.asarray(capa, np.float32)[..., 3] / 255
    final = sombra_de(a, 3.0, 4.0, 3.6, 0.55, '#0C0A14')
    final.alpha_composite(sombra_de(a, 0.9, 1.3, 1.0, 0.45, '#0C0A14'))
    final.alpha_composite(capa)
    return final


# Las tintas planas del botón de Psicodelia (los colores de dibujo de la guía: los complementarios que vibran).
TINTAS_CHAPA = dict(fondo='#2FBF71', petalo='#E0218A', eco='#2FBF71', corazon='#FFD23F', huella='#FFF6E6', filete='#3A1A5C')


def mezclar(a, b, t):
    """Pone el color [b] sobre [a] con la cobertura [t] (0–1, un campo)."""
    return a * (1 - t[..., None]) + b * t[..., None]


def celuloide(tam, radio, alto=15.0, dentro=0.80, reflejo=1.0):
    """La luz sobre un botón de solapa de [radio] px en un lienzo de [tam] px (centrado): el papel impreso va bajo una
    lámina de celuloide abombada, casi plana en el centro y que se curva fuerte donde se enrolla en el borde. Devuelve
    (sombreado, brillo): el sombreado (0–1, cuánto mira a la luz, que viene de arriba a la izquierda) multiplica las
    tintas; el brillo (0–1) se suma en blanco: la franja curva del reflejo de la ventana, que sigue la cúpula a
    [dentro] del radio, con un punto nítido, el halo ancho y los dos filos del borde (el de la luz y el de la contraluz)."""
    yy, xx = np.mgrid[0:tam, 0:tam].astype(np.float32)
    dx, dy = xx - tam / 2, yy - tam / 2
    r = np.sqrt(dx ** 2 + dy ** 2)
    rn = np.clip(r / radio, 0, 1)
    h = np.sqrt(np.clip(1 - rn ** 8, 0, 1)) * 0.8 + (1 - rn ** 2) * 0.2
    gy, gx = np.gradient(h * alto * tam / TAM)
    nx, ny, nz = -gx, -gy, np.ones_like(h)
    nn = np.sqrt(nx * nx + ny * ny + nz * nz)
    L = np.array([-0.5, -0.65, 0.75]); L = L / np.linalg.norm(L)
    sombreado = np.clip((nx * L[0] + ny * L[1] + nz * L[2]) / nn, 0, 1)
    u, v = dx / radio, dy / radio
    ang = np.arctan2(dy, dx)
    da = np.angle(np.exp(1j * (ang - math.radians(-128))))
    franja = np.clip(np.exp(-((rn - dentro) / 0.055) ** 2) * np.clip(1 - (da / 0.62) ** 2, 0, 1) ** 1.3 * 1.25, 0, 1)
    punto = np.exp(-((u + 0.47) ** 2 + (v + 0.6) ** 2) / 0.0035)
    halo = np.clip(1 - np.sqrt((u + 0.35) ** 2 + (v + 0.42) ** 2) / 0.8, 0, 1) ** 2
    diag = (dx + dy) / (r + 1)
    filo_luz = np.exp(-((rn - 0.962) / 0.016) ** 2) * np.clip(-diag * 1.2 + 0.1, 0, 1)
    filo_contra = np.exp(-((rn - 0.958) / 0.03) ** 2) * np.clip(diag * 1.2 - 0.2, 0, 1)
    brillo = (franja * 0.62 + punto * 0.5) * reflejo + halo * 0.1 + filo_luz * 0.45 + filo_contra * 0.22
    return sombreado.astype(np.float32), brillo.astype(np.float32)


def chapa_icono(tema, sem):
    """Psicodelia: un botón de solapa de 1967 prendido en la mezclilla. Bajo el celuloide, el papel impreso en tintas
    planas: la margarita de pétalos redondos en magenta sobre el verde (dos complementarios del mismo peso, que vibran),
    cada pétalo con su eco verde por dentro, el corazón amarillo, y la huella en tinta clara con su filete violeta oscuro.
    Las planchas no casan del todo (el magenta va corrido medio pixel). Encima, la cúpula con su brillo y, en la tela, la
    sombra del botón. Se calcula al cuádruple y se reduce."""
    S = TAM * K
    radio = RADIO_CHAPA * TAM * K
    yy, xx = np.mgrid[0:S, 0:S].astype(np.float32)
    r = np.sqrt((xx - S / 2) ** 2 + (yy - S / 2) ** 2)
    disco = np.clip((radio - r) / (1.4 * K), 0, 1)
    def lapiz(dibujo):
        im = Image.new('L', (S, S), 0); dibujo(ImageDraw.Draw(im)); return np.asarray(im, np.float32) / 255
    pet = lapiz(lambda d: _margarita(d, 255))
    eco = np.clip(lapiz(lambda d: _margarita(d, 255, encoge=0.024)) - lapiz(lambda d: _margarita(d, 255, encoge=0.031)), 0, 1)
    eco = eco * np.clip((r - 0.155 * TAM * K) / (3 * K), 0, 1)       # el eco no llega al corazón, donde se juntan los pétalos
    corazon = np.clip((0.083 * TAM * K - r) / (1.2 * K), 0, 1)
    huella = maestra.mascara(S, S * 0.46)
    T = {k: rgb(v) for k, v in TINTAS_CHAPA.items()}
    corrido = lambda m, ox, oy: np.roll(np.roll(m, int(oy * K), 0), int(ox * K), 1)
    img = np.zeros((S, S, 3), np.float32) + T['fondo']
    img = mezclar(img, T['petalo'], corrido(pet, 0.6, -0.4))
    img = mezclar(img, T['eco'], corrido(eco, 0.6, -0.4) * 0.95)
    img = mezclar(img, T['corazon'], corazon)
    img = mezclar(img, T['filete'], np.clip(suavizar(huella, 1.7 * K) * 3.6, 0, 1))
    img = mezclar(img, T['huella'], corrido(huella, -0.3, 0.3) * huella)
    grano = suavizar(np.random.default_rng(sem).standard_normal((S, S)).astype(np.float32), 0.7 * K)
    img = img * (1 + grano[..., None] * 0.035)                         # el grano del papel bajo las tintas
    sombreado, brillo = celuloide(S, radio, reflejo=0.75)
    img = img * (0.42 + 0.62 * sombreado[..., None]) + brillo[..., None] * 255
    capa = Image.fromarray(np.dstack([np.clip(img, 0, 255), disco * 255]).astype(np.uint8), 'RGBA')
    capa = capa.convert('RGBa').resize((TAM, TAM), Image.LANCZOS).convert('RGBA')
    a = np.asarray(capa, np.float32)[..., 3] / 255
    final = sombra_de(a, 3.5, 5.5, 5.0, 0.8, '#070A16')
    final.alpha_composite(sombra_de(a, 1.0, 1.6, 1.2, 0.6, '#070A16'))
    final.alpha_composite(capa)
    return final


def hacer(tema, salida):
    textura, fondo, tinta, f_fondo, f_huella, acabado, _ = TEMAS[tema]
    sem = sum(map(ord, tema))
    tex = textura(sem)
    # Fondo: el color con la textura (pocos niveles arriba y abajo). Una textura en color (la del jaspe) trae su desvío en RGB.
    bg = rgb(fondo)[None, None, :] + (tex if tex.ndim == 3 else tex[..., None]) * f_fondo
    Image.fromarray(np.clip(bg, 0, 255).astype(np.uint8)).save(os.path.join(salida, f'logo_fondo_{tema}.jpg'), quality=92)
    # Huella: la forma fija, con la misma textura muy leve y el acabado del tema.
    if acabado == 'oro':
        # Oro en relieve sobre la madera: el anillo (el torques) y la huella son una sola pieza de orfebre.
        final = oro_icono(tema, sem)
        # Como el grabado: con 256 colores se ve igual y pesa la cuarta parte.
        final = final.quantize(256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE)
        final.save(os.path.join(salida, f'logo_{tema}.webp'), 'WEBP', lossless=True, quality=100, method=6, exact=True)
        return
    if acabado == 'nacar':
        # Nácar y oro pálido sobre el vidrio de Nancy: el marco de látigo y la huella son una sola joya.
        final = nacar_icono(tema, sem)
        final = final.quantize(256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE)
        final.save(os.path.join(salida, f'logo_{tema}.webp'), 'WEBP', lossless=True, quality=100, method=6, exact=True)
        return
    if acabado == 'chapa':
        # Un botón de solapa de celuloide: la flor y la huella impresas en tintas planas, bajo la cúpula con su brillo.
        final = chapa_icono(tema, sem)
        final = final.quantize(256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE)
        final.save(os.path.join(salida, f'logo_{tema}.webp'), 'WEBP', lossless=True, quality=100, method=6, exact=True)
        return
    m = mascara()
    if acabado == 'grabado':
        # Talla en hueco sobre la piedra, como las gemas: el anillo y la huella son un solo grabado.
        anillo_m = np.asarray(anillo(tema, rgb(tinta)), np.float32)[..., 3] / 255
        final = tallado(np.maximum(m, anillo_m), tinta, sem)
        # El grabado tiene pocos colores de verdad (el corte claro y su sombra): con 256 se ve igual y pesa un tercio.
        final = final.quantize(256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.NONE)
        final.save(os.path.join(salida, f'logo_{tema}.webp'), 'WEBP', lossless=True, quality=100, method=6, exact=True)
        return
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
    if acabado == 'piloto':
        # El cristal ámbar de un piloto encendido: el centro de cada cresta más claro, como luz que pasa, y un halo cálido.
        b = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(1.8)), np.float32) / 255
        nucleo = np.clip((b - 0.62) / 0.38, 0, 1) ** 1.5 * 0.55
        luz = col * (1 - nucleo[..., None]) + rgb('#F2A33A')[None, None, :] * nucleo[..., None]
        capa = Image.fromarray(np.dstack([np.clip(luz, 0, 255), alfa * 255]).astype(np.uint8), 'RGBA')
        g = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(4)), np.float32) / 255
        halo = Image.fromarray(np.dstack([np.broadcast_to(rgb('#E8862A'), (TAM, TAM, 3)), np.clip(g * 0.18, 0, 1) * 255]).astype(np.uint8), 'RGBA')
        halo.alpha_composite(capa); capa = halo
    if acabado == 'fosforo':
        # Resplandor de pantalla alrededor de las crestas.
        g = np.asarray(Image.fromarray((m * 255).astype(np.uint8)).filter(ImageFilter.GaussianBlur(5)), np.float32) / 255
        halo = Image.fromarray(np.dstack([np.broadcast_to(base, (TAM, TAM, 3)), np.clip(g * 0.55, 0, 1) * 255]).astype(np.uint8), 'RGBA')
        halo.alpha_composite(capa); capa = halo
    final = anillo(tema, base)
    final.alpha_composite(capa)
    final.save(os.path.join(salida, f'logo_{tema}.webp'), 'WEBP', lossless=True, quality=100, method=6, exact=True)


def mono(salida):
    """El ícono monocromo (Android lo tiñe): solo la huella maestra, sin anillo."""
    m = mascara()
    img = np.dstack([np.full((TAM, TAM, 3), 255, np.uint8), (m * 255).astype(np.uint8)])
    Image.fromarray(img, 'RGBA').save(os.path.join(salida, 'logo_mono.webp'), 'WEBP', lossless=True, quality=100, method=6, exact=True)


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
        f.alpha_composite(Image.open(os.path.join(salida, f'logo_{t}.webp')))
        imagenes.append(f.crop(caja))
    hoja_de_contacto(imagenes, archivo, cel=cel, mascaras=[c.crop(caja)] * len(imagenes))

if __name__ == '__main__':
    args = sys.argv[1:]
    salida = args[0]
    if '--temas' in args:
        # Solo los temas pedidos: los demás íconos y logo_mono quedan como están.
        pedidos = args[args.index('--temas') + 1].split(',')
        for t in pedidos: hacer(t, salida)
        print('listo', ', '.join(pedidos))
        sys.exit()
    for t in TEMAS: hacer(t, salida)
    mono(salida)
    if len(args) > 1: muestra(salida, args[1])
    print('listo', len(TEMAS))
