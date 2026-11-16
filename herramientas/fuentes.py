"""
Letras de texto y de título del lote 5 de diseño (docs/diseno/PLAN_TEMAS.md): una familia propia por tema.

Baja cada fuente de Google Fonts (repositorio google/fonts, todas con la SIL Open Font License 1.1), saca las instancias
estáticas de las fuentes variables, las recorta al alfabeto latino (y al cirílico en las de los temas soviético y mongol)
y las guarda en app/src/main/res/font/. Las que reservan su nombre en la licencia se renombran, como pide la OFL.

Uso:  pip install fonttools  &&  python3 herramientas/fuentes.py [carpeta_de_descargas] [--solo salida1,salida2]
      Con --solo se hacen solo esas salidas (las de un tema nuevo) y las demás no se tocan.
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
# Un latino más corto para las letras que pesan mucho (Caudex, Literata): el español, el latín extendido A (con las
# vocales largas de las transcripciones) y las pocas letras con punto debajo de las transcripciones del hebreo
# (ḥ ṣ ṭ), sin el resto de la tabla vietnamita ni el latín extendido B.
LATINO_CORTO = "U+0000-017F,U+0259,U+02B0-02FF,U+0300-036F,U+1E24-1E25,U+1E62-1E63,U+1E6C-1E6D,U+2000-206F,U+2070-209F,U+20A0-20CF,U+2100-214F,U+2190-21FF,U+2200-22FF,U+25A0-25FF,U+FB00-FB06"
# El griego entero (Galia escribe palabras galas con letras griegas, como las inscripciones galo-griegas).
GRIEGO = "U+0370-03FF"
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
    # FRE: Frente exterior (lote 10). Títulos en Michroma, la ancha de la ciencia ficción de los años 70; texto en Chivo,
    # grotesca firme; rótulos y cifras en Chivo Mono, como lo grabado en una consola
    ("michroma/Michroma-Regular.ttf", None, "michroma_titulo", None, LATINO),
    ("chivo/Chivo[wght].ttf", {"wght": 400}, "chivo_regular", None, LATINO),
    ("chivo/Chivo[wght].ttf", {"wght": 600}, "chivo_semibold", None, LATINO),
    ("chivomono/ChivoMono[wght].ttf", {"wght": 500}, "chivo_mono_medium", None, LATINO),
    # ARC: Arcontes (lote 10). Títulos y rótulos en Caudex, la letra de los códices (su licencia reserva el nombre: va
    # renombrada); texto en Literata. Las cuatro llevan dentro el hebreo y el copto (ver CON_HEBREO_COPTO)
    ("caudex/Caudex-Bold.ttf", None, "codice_bold", "Codice Vestigium", LATINO_CORTO),
    ("caudex/Caudex-Regular.ttf", None, "codice_regular", "Codice Vestigium", LATINO_CORTO),
    ("literata/Literata[opsz,wght].ttf", {"wght": 400, "opsz": 12}, "literata_regular", None, LATINO_CORTO),
    ("literata/Literata[opsz,wght].ttf", {"wght": 600, "opsz": 12}, "literata_semibold", None, LATINO_CORTO),
    # GAL: Galia (lote 10). Títulos y texto en Piazzolla, serifa afilada como letra grabada; rótulos en Ysabeau. Las tres
    # con el griego (para las palabras galas escritas con letras griegas) y la sigma de luna (ver CON_SIGMA_LUNA)
    ("piazzolla/Piazzolla[opsz,wght].ttf", {"wght": 800, "opsz": 30}, "piazzolla_titulo", None, LATINO_CORTO + "," + GRIEGO),
    ("piazzolla/Piazzolla[opsz,wght].ttf", {"wght": 400, "opsz": 14}, "piazzolla_regular", None, LATINO_CORTO + "," + GRIEGO),
    ("ysabeau/Ysabeau[wght].ttf", {"wght": 600}, "ysabeau_semibold", None, LATINO_CORTO + "," + GRIEGO),
    # NOU: Art nouveau (lote 10). Títulos en Federant (la Feder Antiqua de 1911, letra de cartel), texto en Brygada 1918 y
    # rótulos y cifras en Della Respira (la Della Robbia de 1902). Federant y Della Respira reservan su nombre: van
    # renombradas. A las dos les faltan signos que el tema usa: se les copian de Brygada (ver CON_SIGNOS_BRYGADA)
    ("federant/Federant-Regular.ttf", None, "cartel_titulo", "Cartel Vestigium", LATINO_CORTO),
    ("brygada1918/Brygada1918[wght].ttf", {"wght": 400}, "brygada_regular", None, LATINO_CORTO),
    ("brygada1918/Brygada1918[wght].ttf", {"wght": 600}, "brygada_semibold", None, LATINO_CORTO),
    ("dellarespira/DellaRespira-Regular.ttf", None, "vitrina_regular", "Vitrina Vestigium", LATINO_CORTO),
    # PSI: Psicodelia (lote 10). Títulos en Modak, la letra burbuja de los carteles; texto y rótulos en Fredoka (normal y
    # negrita un poco ancha). A las tres se les agregan los signos del tema de Noto Sans Symbols (ver CON_SIGNOS_NOTO)
    ("modak/Modak-Regular.ttf", None, "burbuja_titulo", None, LATINO_CORTO),
    ("fredoka/Fredoka[wdth,wght].ttf", {"wght": 400, "wdth": 100}, "fredoka_regular", None, LATINO_CORTO),
    ("fredoka/Fredoka[wdth,wght].ttf", {"wght": 600, "wdth": 112}, "fredoka_semibold", None, LATINO_CORTO),
    # PSI, versión 2 (16 nov 2026, docs/diseno/temas/psicodelia.md, «Letra y forma»): Libre Franklin, el grotesco americano
    # de los periódicos y los letreros, para el texto, los rótulos y las cifras de las dos caras y para los títulos de la
    # ciudad (no reserva su nombre); Chango, letra de bloque que llena su forma, para los títulos de la psicodelia (reserva
    # «Chango»: se renombra). Modak y Fredoka siguen hasta que las otras tandas dejen de usarlas.
    ("librefranklin/LibreFranklin[wght].ttf", {"wght": 400}, "franklin_regular", None, LATINO_CORTO),
    ("librefranklin/LibreFranklin[wght].ttf", {"wght": 600}, "franklin_semibold", None, LATINO_CORTO),
    ("librefranklin/LibreFranklin[wght].ttf", {"wght": 800}, "franklin_extrabold", None, LATINO_CORTO),
    ("chango/Chango-Regular.ttf", None, "bloque_titulo", "Bloque Vestigium", LATINO_CORTO),
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


# Las salidas que llevan, además, las letras hebreas (con el geresh y el gershayim, para escribir números) y las coptas,
# copiadas de Noto Serif Hebrew y Noto Sans Coptic (OFL). Android trae esas escrituras solo en letra de palo; el tema
# Arcontes las quiere con su serifa y a la medida de la suya. Se copian ya dibujadas (sin componentes), escaladas a la
# altura de las mayúsculas de cada fuente, con el peso más cercano.
CON_HEBREO_COPTO = {"codice_bold": 700, "codice_regular": 400, "literata_regular": 400, "literata_semibold": 600}
HEBREO = "notoserifhebrew/NotoSerifHebrew[wdth,wght].ttf"
COPTO = "notosanscoptic/NotoSansCoptic-Regular.ttf"
RANGOS_HEBREO = list(range(0x05D0, 0x05EB)) + [0x05F3, 0x05F4]
RANGOS_COPTO = list(range(0x2C80, 0x2CF4)) + list(range(0x2CF9, 0x2D00)) + list(range(0x03E2, 0x03F0))
SIGNOS_ARCONTES = [ord(c) for c in "◌⁘∴∵∷※⁂▪▫▹◃"]
# Las salidas a las que les falta la sigma de luna (Ϲ ϲ, U+03F9 y U+03F2), la de las inscripciones galo-griegas: se les
# da la forma de su propia «C» y «c» latina, que es la misma letra.
# Art nouveau: los signos que Federant y Della Respira no traen (la comilla y el apóstrofo curvos, los puntos suspensivos
# y las flechas), copiados de Brygada 1918 con su peso.
CON_SIGNOS_BRYGADA = {"cartel_titulo": 500, "vitrina_regular": 400}
SIGNOS_BRYGADA = [ord(c) for c in "’”…←→↑↓"]
# Psicodelia: la flor y el sol (de Noto Sans Symbols 2) y la nota, la paz y las flechas (de Noto Sans Symbols, con el peso
# de cada salida), que Modak y Fredoka no traen.
CON_SIGNOS_NOTO = {"burbuja_titulo": 700, "fredoka_regular": 400, "fredoka_semibold": 600}
SIGNOS_NOTO_2 = [ord(c) for c in "✿☼"]
SIGNOS_NOTO_1 = [ord(c) for c in "♪♫☮←→↑↓"]
CON_SIGMA_LUNA = {"piazzolla_titulo", "piazzolla_regular", "ysabeau_semibold"}


def anadir_sigma_luna(f):
    """Apunta Ϲ y ϲ al mismo glifo que C y c (si la fuente no los trae)."""
    cm = f.getBestCmap()
    extra = {cp: cm[ord(lat)] for cp, lat in ((0x03F9, "C"), (0x03F2, "c")) if cp not in cm and ord(lat) in cm}
    for t in f["cmap"].tables:
        if t.isUnicode():
            t.cmap.update(extra)


def altura_mayus(f):
    """La altura de las mayúsculas: la de la tabla OS/2 o, si no la trae, la de la «H»."""
    os2 = f["OS/2"]
    if getattr(os2, "sCapHeight", 0):
        return os2.sCapHeight
    gs = f.getGlyphSet()
    from fontTools.pens.boundsPen import BoundsPen
    b = BoundsPen(gs)
    gs[f.getBestCmap()[ord("H")]].draw(b)
    return b.bounds[3]


def anadir_escritura(f, origen, puntos, ejes, cache, prefijo, solo_faltan=False):
    """Copia a [f] los glifos de [puntos] de la fuente [origen] (instanciada en [ejes]), dibujados de nuevo sin componentes
    y escalados para que su altura de mayúsculas quede igual a la de [f]."""
    from fontTools.pens.ttGlyphPen import TTGlyphPen
    from fontTools.pens.recordingPen import DecomposingRecordingPen
    from fontTools.pens.transformPen import TransformPen
    from fontTools.ttLib.tables._c_m_a_p import CmapSubtable
    m = TTFont(bajar(origen, cache))
    if ejes and "fvar" in m:
        validos = {a.axisTag for a in m["fvar"].axes}
        m = instancer.instantiateVariableFont(m, {k: v for k, v in ejes.items() if k in validos})
    escala = altura_mayus(f) / altura_mayus(m)
    gs = m.getGlyphSet()
    cm = m.getBestCmap()
    orden = list(f.getGlyphOrder())
    nuevos = {}
    ya = f.getBestCmap()
    for cp in puntos:
        if cp not in cm or (solo_faltan and cp in ya):
            continue
        origen_g = cm[cp]
        nombre = "%s%04X" % (prefijo, cp)
        pen = TTGlyphPen(None)
        rec = DecomposingRecordingPen(gs)  # los glifos hechos de componentes (como en Brygada) se copian ya descompuestos
        gs[origen_g].draw(rec)
        rec.replay(TransformPen(pen, (escala, 0, 0, escala, 0, 0)))
        g = pen.glyph()
        g.recalcBounds(None)
        ancho, _ = m["hmtx"][origen_g]
        orden.append(nombre)
        f["glyf"][nombre] = g
        f["hmtx"][nombre] = (round(ancho * escala), getattr(g, "xMin", 0))
        if "vmtx" in f:  # Literata trae métricas verticales: cada glifo nuevo necesita las suyas
            f["vmtx"][nombre] = (f["head"].unitsPerEm, 0)
        nuevos[cp] = nombre
    f.setGlyphOrder(orden)
    f["glyf"].glyphOrder = orden
    base = dict(f.getBestCmap())
    base.update(nuevos)
    for t in f["cmap"].tables:
        if t.isUnicode():
            if t.format == 4 and any(cp > 0xFFFF for cp in base):
                continue
            t.cmap = {cp: n for cp, n in base.items() if t.format != 4 or cp <= 0xFFFF}
    if not any(t.platformID == 3 and t.platEncID in (1, 10) for t in f["cmap"].tables):
        t4 = CmapSubtable.newSubtable(4)
        t4.platformID, t4.platEncID, t4.language, t4.cmap = 3, 1, 0, {cp: n for cp, n in base.items() if cp <= 0xFFFF}
        f["cmap"].tables.append(t4)
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
    if salida in CON_SIGMA_LUNA:
        anadir_sigma_luna(f)
    if salida in CON_SIGNOS_NOTO:
        anadir_escritura(f, "notosanssymbols2/NotoSansSymbols2-Regular.ttf", SIGNOS_NOTO_2, None, cache, "ns2", solo_faltan=True)
        anadir_escritura(f, "notosanssymbols/NotoSansSymbols[wght].ttf", SIGNOS_NOTO_1, {"wght": CON_SIGNOS_NOTO[salida]}, cache, "ns1", solo_faltan=True)
        if salida == "burbuja_titulo":
            # Las cifras de Modak se confunden en tamaño chico (el 8 parece una S): las de Fredoka en negrita y ancha.
            anadir_escritura(f, "fredoka/Fredoka[wdth,wght].ttf", [ord(c) for c in "0123456789"], {"wght": 700, "wdth": 118}, cache, "fre")
    if salida in CON_SIGNOS_BRYGADA:
        anadir_escritura(f, "brygada1918/Brygada1918[wght].ttf", SIGNOS_BRYGADA, {"wght": CON_SIGNOS_BRYGADA[salida]}, cache, "bry", solo_faltan=True)
    if salida in CON_HEBREO_COPTO:
        peso = CON_HEBREO_COPTO[salida]
        anadir_escritura(f, HEBREO, RANGOS_HEBREO, {"wght": peso, "wdth": 100}, cache, "heb")
        anadir_escritura(f, COPTO, RANGOS_COPTO, None, cache, "cop")
        if salida.startswith("literata"):
            # Los signos que Arcontes usa en sus textos (◌ ⁘ ∴ ∵ ∷ ※ ⁂ ▪ ▫ ▹ ◃) y que Literata no trae: de Caudex.
            anadir_escritura(f, "caudex/Caudex-Bold.ttf" if peso >= 600 else "caudex/Caudex-Regular.ttf", SIGNOS_ARCONTES, None, cache, "sig", solo_faltan=True)
    if nombre:
        peso = salida.split("_")[-1]
        renombrar(f, nombre, {"regular": "Regular", "titulo": "Regular", "medium": "Medium", "semibold": "SemiBold", "bold": "Bold", "negrita": "Bold"}[peso])
    destino = os.path.join(DESTINO, salida + ".ttf")
    f.save(destino)
    return os.path.getsize(destino)


if __name__ == "__main__":
    args = [a for a in sys.argv[1:] if not a.startswith("--solo")]
    solo = next((a.split("=", 1)[1] if "=" in a else sys.argv[sys.argv.index(a) + 1] for a in sys.argv[1:] if a.startswith("--solo")), None)
    if solo and solo in args:
        args.remove(solo)
    solo = set(solo.split(",")) if solo else None
    cache = args[0] if args else os.path.join(RAIZ, "build", "fuentes")
    os.makedirs(cache, exist_ok=True)
    total = 0
    for origen, ejes, salida, nombre, letras in FUENTES:
        if solo is not None and salida not in solo:
            continue
        n = hacer(origen, ejes, salida, nombre, letras, cache)
        total += n
        print(f"{salida:22s} {n // 1024:4d} KB")
    print(f"total {total // 1024} KB")
