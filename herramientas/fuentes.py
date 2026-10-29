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

# archivo de origen, ejes (None si es estática), nombre en res/font, nombre nuevo (si la licencia reserva el suyo), letras
FUENTES = [
    # REF: máquina de escribir del refugio
    ("courierprime/CourierPrime-Regular.ttf", None, "courier_regular", None, LATINO),
    ("courierprime/CourierPrime-Bold.ttf", None, "courier_bold", None, LATINO),
    # MED: la letra de los medievalistas
    ("cardo/Cardo-Regular.ttf", None, "cardo_regular", None, LATINO),
    ("cardo/Cardo-Bold.ttf", None, "cardo_bold", None, LATINO),
    # DIS: la grotesca de los rótulos oficiales
    ("barlow/Barlow-Regular.ttf", None, "barlow_regular", None, LATINO),
    ("barlow/Barlow-SemiBold.ttf", None, "barlow_semibold", None, LATINO),
    # ORD: la hermana de texto de su estarcido
    ("saira/Saira[wdth,wght].ttf", {"wdth": 100, "wght": 400}, "saira_regular", None, LATINO),
    ("saira/Saira[wdth,wght].ttf", {"wdth": 100, "wght": 600}, "saira_semibold", None, LATINO),
    # COL: la letra de los periódicos del siglo XIX
    ("oldstandardtt/OldStandard-Regular.ttf", None, "oldstandard_regular", None, LATINO),
    ("oldstandardtt/OldStandard-Bold.ttf", None, "oldstandard_bold", None, LATINO),
    # BOT: libro de naturalista
    ("crimsonpro/CrimsonPro[wght].ttf", {"wght": 400}, "crimson_regular", None, LATINO),
    ("crimsonpro/CrimsonPro[wght].ttf", {"wght": 600}, "crimson_semibold", None, LATINO),
    # NAV: cálida, de cuento
    ("lora/Lora[wght].ttf", {"wght": 400}, "calida_regular", "Calida Vestigium", LATINO),
    ("lora/Lora[wght].ttf", {"wght": 600}, "calida_semibold", "Calida Vestigium", LATINO),
    # SED: humanista caligráfica
    ("alegreya/Alegreya[wght].ttf", {"wght": 400}, "alegreya_regular", None, LATINO),
    ("alegreya/Alegreya[wght].ttf", {"wght": 600}, "alegreya_semibold", None, LATINO),
    # URS: hecha para el ruso
    ("ptsans/PT_Sans-Web-Regular.ttf", None, "obrera_regular", "Obrera Vestigium", CIRILICO),
    ("ptsans/PT_Sans-Web-Bold.ttf", None, "obrera_bold", "Obrera Vestigium", CIRILICO),
    # MON: sans con cirílico
    ("firasans/FiraSans-Regular.ttf", None, "fira_regular", None, CIRILICO),
    ("firasans/FiraSans-SemiBold.ttf", None, "fira_semibold", None, CIRILICO),
    # GOT: Goudy de libro antiguo
    ("sortsmillgoudy/SortsMillGoudy-Regular.ttf", None, "goudy_regular", None, LATINO),
    # REV: Baskerville (1757), la letra del siglo de la Revolución
    ("librebaskerville/LibreBaskerville[wght].ttf", {"wght": 400}, "ciudadana_regular", "Ciudadana Vestigium", LATINO),
    ("librebaskerville/LibreBaskerville[wght].ttf", {"wght": 600}, "ciudadana_semibold", "Ciudadana Vestigium", LATINO),
    # ANT: libro de crónicas
    ("vollkorn/Vollkorn[wght].ttf", {"wght": 400}, "vollkorn_regular", None, LATINO),
    ("vollkorn/Vollkorn[wght].ttf", {"wght": 600}, "vollkorn_semibold", None, LATINO),
    # Títulos: ANT (uncial de los caminos) y REV (los carteles de la Convención)
    ("uncialantiqua/UncialAntiqua-Regular.ttf", None, "camino_titulo", "Camino Vestigium", LATINO),
    ("playfairdisplay/PlayfairDisplay[wght].ttf", {"wght": 400}, "convencion_titulo", "Convencion Vestigium", LATINO),
    ("playfairdisplay/PlayfairDisplay[wght].ttf", {"wght": 700}, "convencion_negrita", "Convencion Vestigium", LATINO),
]


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
