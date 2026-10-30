"""
Prueba 18: arreglos de la 17 antes de pasarla a la app.
- La espada de la Orden tenía la hoja de 2 celdas y el contorno se la comía: ahora es ancha, de acero claro, con guarda de oro.
- El ornitóptero se leía como murciélago: armazón de madera con alas de lona y costillas, más grande.
- La gema, el planeta y el águila salían chicos: cada animación puede tener su propia escala.
"""
import math
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato17 as g17      # noqa: E402
from gato8 import hx      # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
for nombre in ("gato8", "gato9", "gato10", "gato11", "gato12", "gato13", "gato15", "gato16", "gato17"):
    sys.modules[nombre].AQUI = AQUI


def l_espada(L, a):
    x, y = a["obj"]; k = a["k"]
    L.capsula("b_X", [(x, y + 2.6), (x, y - 6.2)], 3.0)                     # hoja ancha de acero
    L.poligono("b_X", [(x - 1.5, y - 6.0), (x + 1.5, y - 6.0), (x, y - 8.6)])  # punta
    L.capsula("b_Q", [(x - 0.5, y + 1.6), (x - 0.5, y - 6.0)], 0.6)          # filo que brilla
    L.capsula("b_V", [(x - 3.4, y + 3.4), (x + 3.4, y + 3.4)], 1.4)          # guarda de oro
    L.capsula("b_U", [(x, y + 3.8), (x, y + 6.8)], 1.4)                      # empuñadura
    L.elipse("b_V", x, y + 7.4, 1.0, 1.0)                                     # pomo
    if k in (1, 2): L.elipse("b_Q", x + 1.6, y - 4.6 + 2 * (k - 1), 0.7, 1.3)


def m_maquina(L, a):          # el ornitóptero de Leonardo: armazón de madera y alas de lona con costillas
    x, y = a["obj"]; k = a["k"]
    ala = -2.4 if k % 2 == 0 else 0.8
    for s in (-1, 1):
        borde = [(x + s * 0.8, y - 0.4), (x + s * 3.4, y - 1.2 + ala * 0.5), (x + s * 6.2, y - 0.6 + ala)]
        L.poligono("b_V", borde + [(x + s * 5.2, y + 1.4 + ala), (x + s * 3.0, y + 1.6), (x + s * 0.8, y + 1.0)])
        L.capsula("b_U", borde, 0.45)
        for f in (0.45, 0.8):
            px_, py_ = x + s * (0.8 + 5.4 * f), y - 0.4 + (ala + 0.2) * f
            L.capsula("b_U", [(px_, py_), (px_ - s * 0.8, py_ + 1.8)], 0.3)
    L.capsula("b_U", [(x - 1.6, y + 0.4), (x + 1.6, y + 0.4)], 0.9)        # cuerpo
    L.poligono("b_V", [(x - 1.6, y + 0.4), (x - 4.2, y - 0.8), (x - 4.2, y + 1.8)])   # cola en abanico


g17.ESP["orden"][0] = ("levantar", l_espada, "alza la espada")
g17.ESP["renacimiento"][1] = ("mirar", m_maquina, "mira volar el ornitóptero")
g17.EXTRA_COL[("orden", 0)] = dict(U=hx("5A3A22"), V=hx("D9AE48"), X=hx("D6DCE2"), Q=hx("FFFFFF"))
g17.EXTRA_COL[("renacimiento", 1)] = dict(U=hx("6A4A2A"), V=hx("EFE2C2"))

# Escala por animación (sobre la de su acción).
ESCALA_ESP = {("antiguas", 1): 2.1, ("espacio", 1): 2.3, ("mongol", 0): 2.2, ("renacimiento", 1): 2.1,
              ("gotico", 1): 2.0, ("ciber", 1): 1.9, ("distopia", 1): 1.8, ("botanico", 1): 2.0, ("navidad", 1): 1.9,
              ("heleno", 1): 1.75}

# Volver a registrar las animaciones con el objeto nuevo y su escala.
for nombre, lista in g17.ESP.items():
    for j, (accion, f, _) in enumerate(lista):
        fn, n, _base = g17.ACCIONES[accion]
        cuadros = []
        for k in range(n):
            p, a = fn(k)
            a["accion"] = accion
            a["escala"] = ESCALA_ESP.get((nombre, j), g17.ESCALA[accion])
            cuadros.append(dict(p, objeto=(f, a), semilla=k))
        g8.ANIMS[f"esp_{nombre}_{j}"] = cuadros


def sombrero_lado(L, cx, cy, inc, som):
    g17._lado16(L, cx, cy, inc, som)
    ps = g17._actual.get("ps")
    if ps and ps.get("objeto"):
        f, a = ps["objeto"]
        f(g17.Escala(L, a["obj"], a.get("pata", a["obj"]), a["escala"]), a)


g8.sombrero_lado = sombrero_lado
g8._spr.clear()

# ---------------------------------------------------------------------
#  De noche, los temas que aún tenían el fondo de día: el mismo dibujo con la luz invertida (el papel se vuelve el fondo
#  oscuro del tema y la tinta se aclara), como hace la app con sus esquemas oscuros. Los colores conservan su tono.
# ---------------------------------------------------------------------
import colorsys            # noqa: E402
import numpy as np         # noqa: E402
from PIL import Image      # noqa: E402

NOCHE_FONDO = {"romano": "0E1113", "medieval": "15100A", "botanico": "14120D", "orden": "101215", "seda": "0F1426",
               "andalus": "0B1716", "renacimiento": "1A130C"}


def a_noche(img, claro, oscuro):
    a = np.asarray(img.convert("RGB")).astype(np.float32) / 255
    mx, mn = a.max(2), a.min(2)
    L = (mx + mn) / 2
    Lb = sum(claro) / 3 / 255
    Ld = sum(oscuro) / 3 / 255
    L2 = np.clip(Ld + (Lb - L) * (0.92 - Ld) / max(Lb, 1e-3), 0, 1)
    # Cambiar la luz conservando tono y saturación: escalar la distancia al gris.
    gris = L[..., None]
    sat = (a - gris)
    k = np.clip(np.minimum(L2, 1 - L2) / np.maximum(np.minimum(L, 1 - L), 1e-3), 0, 1.6)[..., None]
    out = L2[..., None] + sat * k
    # El papel toma el tinte del fondo oscuro del tema.
    papel = np.clip(1 - np.abs(L - Lb) * 6, 0, 1)[..., None]
    tinte = np.array(oscuro, np.float32) / 255 - Ld
    out = out + tinte * papel
    return Image.fromarray((np.clip(out, 0, 1) * 255).astype(np.uint8))


def _de_noche(nombre, f):
    def fondo(t):
        img = f(t)
        if not t["oscuro"]:
            return img
        claro = g8.tema(nombre, False)
        base = f(claro)
        fondo_claro = Image.Image.getpixel(base.resize((1, 1), Image.BOX), (0, 0)) if nombre != "andalus" else base.getpixel((40, 20))
        return a_noche(base, fondo_claro, hx(NOCHE_FONDO[nombre]))
    return fondo


_tema17 = g8.tema


def tema(nombre, oscuro=False):
    """En estos temas el gato de noche conserva sus colores de día: sobre el fondo oscuro se lee mejor."""
    t = _tema17(nombre, oscuro)
    if oscuro and nombre in NOCHE_FONDO:
        t = dict(t, col=dict(_tema17(nombre, False)["col"]))
    return t


for _m in ("gato8", "gato9", "gato10", "gato12", "gato13", "gato16", "gato17"):
    sys.modules[_m].tema = tema

for _n in NOCHE_FONDO:
    g8.FONDOS[_n] = _de_noche(_n, g8.FONDOS[_n])
    if _n in sys.modules["gato9"].FONDOS9:
        sys.modules["gato9"].FONDOS9[_n] = g8.FONDOS[_n]

if __name__ == "__main__":
    que = sys.argv[1:] or ["hojas", "resumen"]
    if "hojas" in que:
        g17.hojas("animaciones_tema")
    if "oscuro" in que:
        # Las mismas hojas en modo oscuro.
        claro = list(g8.BANDAS)
        g8.BANDAS[:] = [(n, True, e) for n, _, e in claro]
        g17.hojas("animaciones_tema_oscuro")
        g8.BANDAS[:] = claro
    if "resumen" in que:
        g17.resumen(AQUI / "todas_las_bandas_especiales.webp")
    print("listo")
