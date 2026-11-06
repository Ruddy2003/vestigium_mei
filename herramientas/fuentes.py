"""
Letras de texto y de título del lote 5 de diseño (docs/diseno/PLAN_TEMAS.md): una familia propia por tema.

Baja cada fuente de Google Fonts (repositorio google/fonts, todas con la SIL Open Font License 1.1), saca las instancias
estáticas de las fuentes variables, las recorta al alfabeto latino (y al cirílico en las de los temas soviético y mongol)
y las guarda en app/src/main/res/font/. Las que reservan su nombre en la licencia se renombran, como pide la OFL.

Uso:  pip install fonttools  &&  python3 herramientas/fuentes.py [carpeta_de_descargas]
"""
import io
import os
import sys
import urllib.request

from fontTools import subset
from fontTools.ttLib import TTFont
from fontTools.varLib import instancer

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DESTINO = os.path.join(RAIZ, "app", "src", "main", "res", "font")
GF = "https://raw.githubusercontent.com/google/fonts/main/ofl/"

LATINO = "U+0000-024F,U+0259,U+02B0-02FF,U+0300-036F,U+1D00-1DBF,U+1E00-1EFF,U+2000-206F,U+2070-209F,U+20A0-20CF,U+2100-214F,U+2150-218F,U+2190-21FF,U+2200-22FF,U+2460-24FF,U+25A0-25FF,U+2600-27BF,U+FB00-FB06"
CIRILICO = LATINO + ",U+0400-052F,U+1C80-1C8F,U+2DE0-2DFF,U+A640-A69F"
# Japón Edo: solo los kanji, kana y signos que usa el tema (las fuentes japonesas completas pesan varios megas).
KANJI_EDO = ("※、。々〆「」『』〔〕あいえおかがきぎくけこさしじすずせそただちつづてでとどなにのはひびふまみむめもやゆよらりるれわをんコマ・一七万世中也予事五人代令会体作使便元光入八兵具出分"
             "動勝医十千卯印又反取口古吉同名和品問土場声夏夢大如始定客宿封尺岩師平年庫廻引弥後御心応悔悪成我所手持数文新旅日明昭是時暮曜曲書最月有朝期木朱東枯栖様模正歩残段水求池"
             "河治流海滋火無瓶生用画番病登百皐盗目眠睦知硯神秋稼稽空窓第筋紙終絵絶続網習者脈腕腹良花茶草菜葉蛙蝉行西言計記評読費走起足跡転連過道野金釣錬鍛長開閑間階雪電霜露面音"
             "題顔風飛食養騒齢＋－：〇二三四六九")
JAPONES_EDO = LATINO + "," + ",".join(f"U+{ord(c):04X}" for c in KANJI_EDO)
# La negrita del texto lleva las mismas letras que la regular: cualquier texto en negrita del tema (títulos de ficha,
# la capitular del diario, rótulos) puede llevar kana o kanji, y la letra que falta sale con la fuente del sistema,
# de palo seco, en medio de la palabra.
JAPONES_EDO_NEGRITA = JAPONES_EDO

# archivo de origen, ejes (None si es estática), nombre en res/font, nombre nuevo (si la licencia reserva el suyo), letras
FUENTES = [
    # REF: máquina de escribir del refugio
    ("courierprime/CourierPrime-Regular.ttf", None, "courier_regular", None, LATINO),
    ("courierprime/CourierPrime-Bold.ttf", None, "courier_bold", None, LATINO),
    # NEON: la letra técnica y cuadrada de los letreros de la megaciudad (Chakra Petch)
    ("chakrapetch/ChakraPetch-Regular.ttf", None, "chakra_regular", None, LATINO),
    ("chakrapetch/ChakraPetch-SemiBold.ttf", None, "chakra_semibold", None, LATINO),
    # MED: la letra de los medievalistas
    ("cardo/Cardo-Regular.ttf", None, "cardo_regular", None, LATINO),
    ("cardo/Cardo-Bold.ttf", None, "cardo_bold", None, LATINO),
    # Templario: el libro de la regla (Gentium, renombrada) y la letra de los estandartes (MedievalSharp, renombrada)
    ("gentiumbookplus/GentiumBookPlus-Regular.ttf", None, "regla_regular", "Regla Vestigium", LATINO),
    ("gentiumbookplus/GentiumBookPlus-Bold.ttf", None, "regla_bold", "Regla Vestigium", LATINO),
    ("medievalsharp/MedievalSharp.ttf", None, "cruzada_titulo", "Cruzada Vestigium", LATINO),
    # DIS: la grotesca de los rótulos oficiales
    ("barlow/Barlow-Regular.ttf", None, "barlow_regular", None, LATINO),
    ("barlow/Barlow-SemiBold.ttf", None, "barlow_semibold", None, LATINO),
    # ORD: la hermana de texto de su estarcido
    ("saira/Saira[wdth,wght].ttf", {"wdth": 100, "wght": 400}, "saira_regular", None, LATINO),
    ("saira/Saira[wdth,wght].ttf", {"wdth": 100, "wght": 600}, "saira_semibold", None, LATINO),
    # VAP: Vapor victoriano. Títulos en Abril Fatface, una «fat face» como las de los carteles del XIX (reserva «Abril»: se
    # renombra), y texto en Bitter, egipcia de remates cuadrados como las de los horarios de ferrocarril
    ("abrilfatface/AbrilFatface-Regular.ttf", None, "caldera_titulo", "Caldera Vestigium", LATINO),
    ("bitter/Bitter[wght].ttf", {"wght": 400}, "bitter_regular", None, LATINO),
    ("bitter/Bitter[wght].ttf", {"wght": 600}, "bitter_semibold", None, LATINO),
    # COL: la letra de los periódicos del siglo XIX
    ("oldstandardtt/OldStandard-Regular.ttf", None, "oldstandard_regular", None, LATINO),
    ("oldstandardtt/OldStandard-Bold.ttf", None, "oldstandard_bold", None, LATINO),
    # DECO: los rótulos de línea fina de los carteles de los años veinte (Poiret One) y una geométrica de esa época para
    # leer (Josefin Sans, que reserva su nombre: se renombra)
    ("poiretone/PoiretOne-Regular.ttf", None, "poiret_titulo", None, LATINO),
    ("josefinsans/JosefinSans[wght].ttf", {"wght": 400}, "pasaje_regular", "Pasaje Vestigium", LATINO),
    ("josefinsans/JosefinSans[wght].ttf", {"wght": 600}, "pasaje_semibold", "Pasaje Vestigium", LATINO),
    # BOT: libro de naturalista
    ("crimsonpro/CrimsonPro[wght].ttf", {"wght": 400}, "crimson_regular", None, LATINO),
    ("crimsonpro/CrimsonPro[wght].ttf", {"wght": 600}, "crimson_semibold", None, LATINO),
    # RUI: Ciudad verde. Títulos de letrero pintado a mano sobre una tabla y texto de señal de carretera
    ("londrinasolid/LondrinaSolid-Regular.ttf", None, "letrero_titulo", "Letrero Vestigium", LATINO),
    ("overpass/Overpass[wght].ttf", {"wght": 400}, "overpass_regular", None, LATINO),
    ("overpass/Overpass[wght].ttf", {"wght": 600}, "overpass_semibold", None, LATINO),
    # NAV: cálida, de cuento
    ("lora/Lora[wght].ttf", {"wght": 400}, "calida_regular", "Calida Vestigium", LATINO),
    ("lora/Lora[wght].ttf", {"wght": 600}, "calida_semibold", "Calida Vestigium", LATINO),
    # SED: humanista caligráfica
    ("alegreya/Alegreya[wght].ttf", {"wght": 400}, "alegreya_regular", None, LATINO),
    ("alegreya/Alegreya[wght].ttf", {"wght": 600}, "alegreya_semibold", None, LATINO),
    # EDO: Shippori Mincho para los títulos (de pincel grabado en madera) y Zen Old Mincho para el texto; recortadas al tema
    ("shipporimincho/ShipporiMincho-SemiBold.ttf", None, "shippori_titulo", None, JAPONES_EDO),
    ("zenoldmincho/ZenOldMincho-Regular.ttf", None, "zenmincho_regular", None, JAPONES_EDO),
    ("zenoldmincho/ZenOldMincho-SemiBold.ttf", None, "zenmincho_semibold", None, JAPONES_EDO_NEGRITA),
    # MESO: Alegreya Sans SC (títulos y rótulos) y Alegreya Sans (texto), de Huerta Tipográfica; las dos llevan además los
    # numerales mayas de puntos y barras (U+1D2E0-1D2F3) de Noto Sans Mayan Numerals (ver CON_MAYAS)
    ("alegreyasanssc/AlegreyaSansSC-Medium.ttf", None, "alegreya_sc_medium", None, LATINO),
    ("alegreyasanssc/AlegreyaSansSC-Bold.ttf", None, "alegreya_sc_bold", None, LATINO),
    ("alegreyasans/AlegreyaSans-Regular.ttf", None, "alegreya_sans_regular", None, LATINO),
    ("alegreyasans/AlegreyaSans-Bold.ttf", None, "alegreya_sans_bold", None, LATINO),
    # URS: hecha para el ruso
    ("ptsans/PT_Sans-Web-Regular.ttf", None, "obrera_regular", "Obrera Vestigium", CIRILICO),
    ("ptsans/PT_Sans-Web-Bold.ttf", None, "obrera_bold", "Obrera Vestigium", CIRILICO),
    # MON: sans con cirílico
    ("firasans/FiraSans-Regular.ttf", None, "fira_regular", None, CIRILICO),
    ("firasans/FiraSans-SemiBold.ttf", None, "fira_semibold", None, CIRILICO),
    # GOT: Goudy de libro antiguo
    ("sortsmillgoudy/SortsMillGoudy-Regular.ttf", None, "goudy_regular", None, LATINO),
    # ABI: rótulos estrechos de instrumento (título) y una letra hecha para leerse con poca luz (texto)
    ("barlowcondensed/BarlowCondensed-Medium.ttf", None, "batiscafo_medium", None, LATINO),
    ("barlowcondensed/BarlowCondensed-SemiBold.ttf", None, "batiscafo_semibold", None, LATINO),
    ("atkinsonhyperlegiblenext/AtkinsonHyperlegibleNext[wght].ttf", {"wght": 400}, "atkinson_regular", None, LATINO),
    ("atkinsonhyperlegiblenext/AtkinsonHyperlegibleNext[wght].ttf", {"wght": 600}, "atkinson_semibold", None, LATINO),
    # PIR: Caslon (h. 1725), la letra de los libros de a bordo; títulos en Pirata One (reserva «Pirata»: se renombra)
    ("librecaslontext/LibreCaslonText[wght].ttf", {"wght": 400}, "caslon_regular", None, LATINO),
    ("librecaslontext/LibreCaslonText[wght].ttf", {"wght": 600}, "caslon_semibold", None, LATINO),
    ("pirataone/PirataOne-Regular.ttf", None, "corsario_titulo", "Corsario Vestigium", LATINO),
    # ARC (horror cósmico): Spectral para los títulos, en su cursiva de mano de archivista, y Castoro para leer los legajos
    ("spectral/Spectral-Medium.ttf", None, "spectral_medium", None, LATINO),
    ("spectral/Spectral-MediumItalic.ttf", None, "spectral_cursiva", None, LATINO),
    ("castoro/Castoro[wght].ttf", {"wght": 400}, "castoro_regular", None, LATINO),
    ("castoro/Castoro[wght].ttf", {"wght": 600}, "castoro_semibold", None, LATINO),
    # REV: Baskerville (1757), la letra del siglo de la Revolución
    ("librebaskerville/LibreBaskerville[wght].ttf", {"wght": 400}, "ciudadana_regular", "Ciudadana Vestigium", LATINO),
    ("librebaskerville/LibreBaskerville[wght].ttf", {"wght": 600}, "ciudadana_semibold", "Ciudadana Vestigium", LATINO),
    # URBE: Titillium Web (Accademia di Belle Arti di Urbino), sans de placa moderna; Newsreader, letra de periódico para el texto
    ("titilliumweb/TitilliumWeb-Regular.ttf", None, "titillium_regular", None, LATINO),
    ("titilliumweb/TitilliumWeb-SemiBold.ttf", None, "titillium_semibold", None, LATINO),
    ("titilliumweb/TitilliumWeb-Bold.ttf", None, "titillium_bold", None, LATINO),
    ("newsreader/Newsreader[opsz,wght].ttf", {"opsz": 16, "wght": 400}, "newsreader_regular", None, LATINO),
    ("newsreader/Newsreader[opsz,wght].ttf", {"opsz": 16, "wght": 600}, "newsreader_semibold", None, LATINO),
    # ANT: libro de crónicas
    ("vollkorn/Vollkorn[wght].ttf", {"wght": 400}, "vollkorn_regular", None, LATINO),
    ("vollkorn/Vollkorn[wght].ttf", {"wght": 600}, "vollkorn_semibold", None, LATINO),
    # CUE: Prehistoria. Títulos a mano alzada, como trazos de carbón en la pared (Amatic SC), y texto de Andika, hecha para
    # quien aprende a leer (reserva «Andika» y «SIL»: se renombra)
    ("amaticsc/AmaticSC-Regular.ttf", None, "amatic_regular", None, LATINO),
    ("amaticsc/AmaticSC-Bold.ttf", None, "amatic_bold", None, LATINO),
    ("andika/Andika-Regular.ttf", None, "abrigo_regular", "Abrigo Vestigium", LATINO),
    ("andika/Andika-Bold.ttf", None, "abrigo_bold", "Abrigo Vestigium", LATINO),
    # EGI: Gentium (SIL), con las letras de la transliteración egiptológica (ꜣ, ꜥ, ḥ, ḫ, š…); títulos en El Messiri, de un diseñador egipcio
    ("gentiumbookplus/GentiumBookPlus-Regular.ttf", None, "escriba_regular", "Escriba Vestigium", LATINO + ",U+A722-A725"),
    ("gentiumbookplus/GentiumBookPlus-Bold.ttf", None, "escriba_bold", "Escriba Vestigium", LATINO + ",U+A722-A725"),
    ("elmessiri/ElMessiri[wght].ttf", {"wght": 500}, "messiri_medium", None, LATINO),
    ("elmessiri/ElMessiri[wght].ttf", {"wght": 700}, "messiri_bold", None, LATINO),
    # Títulos: ANT (uncial de los caminos) y REV (los carteles de la Convención)
    ("uncialantiqua/UncialAntiqua-Regular.ttf", None, "camino_titulo", "Camino Vestigium", LATINO),
    ("playfairdisplay/PlayfairDisplay[wght].ttf", {"wght": 400}, "convencion_titulo", "Convencion Vestigium", LATINO),
    ("playfairdisplay/PlayfairDisplay[wght].ttf", {"wght": 700}, "convencion_negrita", "Convencion Vestigium", LATINO),
]


# Las salidas que llevan, además, los 20 numerales mayas (0 a 19, de puntos y barras) de Noto Sans Mayan Numerals: Android
# no trae esa letra, así que el tema Mesoamérica los lleva dentro de las suyas.
CON_MAYAS = {"alegreya_sc_medium", "alegreya_sc_bold", "alegreya_sans_regular", "alegreya_sans_bold"}
MAYAS = "notosansmayannumerals/NotoSansMayanNumerals-Regular.ttf"


def anadir_mayas(f, cache):
    """Copia a [f] los glifos de U+1D2E0-1D2F3 (contornos y anchos) y los agrega a su tabla de caracteres."""
    import copy
    from fontTools.ttLib.tables._c_m_a_p import CmapSubtable
    m = TTFont(bajar(MAYAS, cache))
    escala = f["head"].unitsPerEm / m["head"].unitsPerEm
    cm = m.getBestCmap()
    orden = list(f.getGlyphOrder())
    nuevos = {}
    for cp in range(0x1D2E0, 0x1D2F4):
        origen = cm[cp]
        nombre = "maya%02d" % (cp - 0x1D2E0)
        g = copy.deepcopy(m["glyf"][origen])
        if escala != 1:
            g.coordinates.scale((escala, escala))
        ancho, izq = m["hmtx"][origen]
        orden.append(nombre)
        f["glyf"][nombre] = g
        f["hmtx"][nombre] = (round(ancho * escala), round(izq * escala))
        nuevos[cp] = nombre
    f.setGlyphOrder(orden)
    f["glyf"].glyphOrder = orden
    base = dict(f.getBestCmap())
    base.update(nuevos)
    t12 = CmapSubtable.newSubtable(12)
    t12.platformID, t12.platEncID, t12.language, t12.cmap = 3, 10, 0, base
    f["cmap"].tables = [t for t in f["cmap"].tables if not (t.platformID == 3 and t.platEncID == 10)] + [t12]
    f["maxp"].numGlyphs = len(orden)


# Las salidas que llevan, además, los adornos del tema Art déco (≡ ◊ ‖ ▷ ▽ y las flechas), que Poiret One y Josefin Sans
# no traen: sin ellos Android los toma de la fuente del sistema, con otro grosor y otra altura. Se dibujan aquí con el
# grosor del palo de cada fuente (ver anadir_adornos).
CON_ADORNOS = {"poiret_titulo", "pasaje_regular", "pasaje_semibold"}


def anadir_adornos(f):
    """Dibuja en [f] los adornos de Art déco con sus propias medidas: el grosor de «|», la altura de «-» y «=», la de las mayúsculas."""
    import math
    from fontTools.pens.ttGlyphPen import TTGlyphPen
    cm = f.getBestCmap()
    glyf, hmtx = f["glyf"], f["hmtx"]

    def caja(c):
        g = glyf[cm[ord(c)]]
        g.recalcBounds(glyf)
        return g.xMin, g.yMin, g.xMax, g.yMax, hmtx[cm[ord(c)]][0]

    bx0, by0, bx1, by1, badv = caja("|")
    hx0, hy0, hx1, hy1, _ = caja("-")
    ex0, ey0, ex1, ey1, eadv = caja("=")
    _, _, _, mayus, _ = caja("H")
    g = bx1 - bx0                       # grosor del palo
    h = hy1 - hy0                       # grosor de la raya
    c = (hy0 + hy1) / 2                 # el eje de los signos (la altura de la raya)
    em = f["head"].unitsPerEm

    def area(p):
        return sum(p[i][0] * p[(i + 1) % len(p)][1] - p[(i + 1) % len(p)][0] * p[i][1] for i in range(len(p))) / 2

    def horario(p, si=True):
        """TrueType rellena los contornos en sentido horario; los huecos van al revés."""
        return p if (area(p) < 0) == si else p[::-1]

    def barra(a, b, grueso):
        (x0, y0), (x1, y1) = a, b
        l = math.hypot(x1 - x0, y1 - y0)
        nx, ny = -(y1 - y0) / l * grueso / 2, (x1 - x0) / l * grueso / 2
        return horario([(x0 + nx, y0 + ny), (x1 + nx, y1 + ny), (x1 - nx, y1 - ny), (x0 - nx, y0 - ny)])

    def hueco(p, grueso):
        """El contorno de adentro de un polígono convexo de trazo [grueso]: cada lado se recorre [grueso] hacia el centro."""
        cx, cy = sum(x for x, _ in p) / len(p), sum(y for _, y in p) / len(p)
        lineas = []
        for i in range(len(p)):
            (x0, y0), (x1, y1) = p[i], p[(i + 1) % len(p)]
            l = math.hypot(x1 - x0, y1 - y0)
            nx, ny = -(y1 - y0) / l, (x1 - x0) / l
            if (cx - x0) * nx + (cy - y0) * ny < 0:
                nx, ny = -nx, -ny
            lineas.append(((x0 + nx * grueso, y0 + ny * grueso), (x1 - x0, y1 - y0)))
        out = []
        for i in range(len(lineas)):
            (p0, d0), (p1, d1) = lineas[i - 1], lineas[i]
            det = d0[0] * d1[1] - d0[1] * d1[0]
            t = ((p1[0] - p0[0]) * d1[1] - (p1[1] - p0[1]) * d1[0]) / det
            out.append((p0[0] + d0[0] * t, p0[1] + d0[1] * t))
        return out

    def contorno(p, grueso):
        return [horario(p), horario(hueco(p, grueso), si=False)]

    def glifo(nombre, contornos, ancho):
        pen = TTGlyphPen(None)
        for p in contornos:
            pen.moveTo(tuple(round(v) for v in p[0]))
            for q in p[1:]:
                pen.lineTo(tuple(round(v) for v in q))
            pen.closePath()
        gl = pen.glyph()
        gl.recalcBounds(None)
        return nombre, gl, (round(ancho), gl.xMin)

    lado = em * 0.08                                        # aire a cada lado de los signos
    alto = mayus * 0.8                                      # alto de los triángulos y del rombo
    cm_ = mayus / 2                                         # ≡ ‖ ▷ ▽ ◊ van junto a rótulos en mayúsculas: a media altura de ellas
    largo = max(hx1 - hx0, em * 0.3) * 1.7                  # largo de las flechas
    punta = largo * 0.4
    t = min(max(g, h), em * 0.07)                           # grosor de las flechas
    trazo = min(g, alto * 0.12)                             # grosor del contorno de ▷ ▽ ◊
    nuevos = {}
    # ≡: tres rayas del grosor de las de «=», con aire suficiente para que no se junten en la negrita.
    hr = min(h, em * 0.06)
    d = max((ey1 - ey0 - h) / 2, hr * 2.4, em * 0.09)
    an = max(ex1 - ex0, em * 0.5)
    nuevos[0x2261] = glifo("uni2261", [barra((lado + hr / 2, cm_ + k * d), (lado + an - hr / 2, cm_ + k * d), hr) for k in (-1, 0, 1)], an + 2 * lado)
    # ‖: dos palos de «|».
    sep = max(2 * g, em * 0.06)
    nuevos[0x2016] = glifo("uni2016", [barra((bx0 + g / 2 + k * (g + sep), by0), (bx0 + g / 2 + k * (g + sep), by1), g) for k in (0, 1)], badv + g + sep)
    # ▷ ▽ ◊: el contorno con el grosor del palo.
    w = alto * 0.866
    nuevos[0x25B7] = glifo("uni25B7", contorno([(lado, cm_ - alto / 2), (lado, cm_ + alto / 2), (lado + w, cm_)], trazo), w + 2 * lado)
    wv = alto / 0.866
    nuevos[0x25BD] = glifo("uni25BD", contorno([(lado, cm_ + alto / 2), (lado + wv, cm_ + alto / 2), (lado + wv / 2, cm_ - alto / 2)], trazo), wv + 2 * lado)
    wr = alto * 0.62
    nuevos[0x25CA] = glifo("uni25CA", contorno([(lado + wr / 2, cm_ - alto / 2), (lado, cm_), (lado + wr / 2, cm_ + alto / 2), (lado + wr, cm_)], trazo), wr + 2 * lado)

    # Flechas: el asta y los dos brazos de la punta, del grosor de la raya.
    def flecha(punta_en, desde):
        (px, py), (qx, qy) = punta_en, desde
        l = math.hypot(px - qx, py - qy)
        ux, uy = (px - qx) / l, (py - qy) / l
        brazos = [((px - ux * punta - uy * punta * s), (py - uy * punta + ux * punta * s)) for s in (1, -1)]
        return [barra((qx, qy), (px - ux * t * 0.35, py - uy * t * 0.35), t)] + [barra((px, py), b, t) for b in brazos]

    ancho = largo + 2 * lado
    nuevos[0x2192] = glifo("arrowright", flecha((lado + largo, c), (lado, c)), ancho)
    nuevos[0x2190] = glifo("arrowleft", flecha((lado, c), (lado + largo, c)), ancho)
    av = max(eadv, punta * 2 + 2 * lado)
    nuevos[0x2191] = glifo("arrowup", flecha((av / 2, mayus), (av / 2, 0)), av)
    nuevos[0x2193] = glifo("arrowdown", flecha((av / 2, 0), (av / 2, mayus)), av)

    orden = list(f.getGlyphOrder())
    para_cmap = {}
    for cp, (nombre, gl, metrica) in nuevos.items():
        if cp in cm:
            continue
        while nombre in glyf.glyphs:
            nombre += ".vm"
        orden.append(nombre)
        glyf[nombre] = gl
        hmtx[nombre] = metrica
        para_cmap[cp] = nombre
    f.setGlyphOrder(orden)
    glyf.glyphOrder = orden
    for tabla in f["cmap"].tables:
        if tabla.isUnicode():
            tabla.cmap.update(para_cmap)
    f["maxp"].numGlyphs = len(orden)


def bajar(ruta, cache):
    local = os.path.join(cache, ruta.replace("/", "_"))
    if not os.path.exists(local):
        url = GF + ruta.replace("[", "%5B").replace("]", "%5D")
        with urllib.request.urlopen(url) as r, open(local, "wb") as f:
            f.write(r.read())
    return local


def renombrar(f, nombre, sufijo):
    """Cambia la familia (y lo que la repite) en la tabla de nombres."""
    completo = f"{nombre} {sufijo}".strip()
    f["name"].names = [r for r in f["name"].names if r.nameID != 25]  # prefijo de variaciones: ya no es variable
    for rec in f["name"].names:
        if rec.nameID in (1, 16):
            rec.string = nombre
        elif rec.nameID == 4:
            rec.string = completo
        elif rec.nameID in (3, 6):
            rec.string = completo.replace(" ", "")
        elif rec.nameID in (17, 2):
            rec.string = sufijo or "Regular"


def hacer(origen, ejes, salida, nombre, letras, cache):
    f = TTFont(bajar(origen, cache))
    if ejes:
        try:
            f = instancer.instantiateVariableFont(f, ejes, updateFontNames=True)
        except ValueError:  # la tabla STAT no nombra ese peso: los nombres quedan como estaban (Android elige por el recurso)
            f = instancer.instantiateVariableFont(TTFont(bajar(origen, cache)), ejes)
    op = subset.Options()
    op.layout_features = ["kern", "liga", "clig", "calt", "ccmp", "locl", "mark", "mkmk", "lnum", "tnum", "pnum", "onum"]
    op.name_IDs = ["*"]
    op.name_languages = ["*"]
    op.notdef_outline = True
    op.hinting = False  # Android suaviza sin las instrucciones de hinting; así pesan la mitad
    op.drop_tables += ["DSIG"]
    s = subset.Subsetter(op)
    s.populate(unicodes=subset.parse_unicodes(letras))
    s.subset(f)
    if salida in CON_MAYAS:
        anadir_mayas(f, cache)
    if salida in CON_ADORNOS:
        anadir_adornos(f)
    if nombre:
        peso = salida.split("_")[-1]
        renombrar(f, nombre, {"regular": "Regular", "titulo": "Regular", "medium": "Medium", "semibold": "SemiBold", "bold": "Bold", "negrita": "Bold"}[peso])
    destino = os.path.join(DESTINO, salida + ".ttf")
    f.save(destino)
    return os.path.getsize(destino)


if __name__ == "__main__":
    cache = sys.argv[1] if len(sys.argv) > 1 else os.path.join(RAIZ, "build", "fuentes")
    os.makedirs(cache, exist_ok=True)
    total = 0
    for origen, ejes, salida, nombre, letras in FUENTES:
        n = hacer(origen, ejes, salida, nombre, letras, cache)
        total += n
        print(f"{salida:22s} {n // 1024:4d} KB")
    print(f"total {total // 1024} KB")
