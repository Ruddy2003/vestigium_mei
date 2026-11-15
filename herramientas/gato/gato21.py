"""
Lote 10: los gatos de los cinco temas nuevos. Primera tanda: Frente exterior.

- Frente exterior (`frente`): gato gris pizarra pintado como un cuadro de concepto de los años 70 (gouache sobre cartón:
  manchas de color opaco con la huella del pincel, luz cálida que entra por arriba y rebote frío abajo, grano del
  cartón). Chaqueta de vuelo de cuero con cuello de borrego, puños y cintura de punto y un parche en la manga; en la
  oreja, un auricular de baquelita con su micrófono y un piloto rojo. En el collar, la cápsula donde esconde el
  microfilm. Fondo: la base escondida en ruinas (muro de sillares con raíces que cuelgan, losa con humedad y una
  rendija, tubos de cobre, paneles de vidrio grabado con trazos turquesa iluminados por el canto, consolas oscuras con
  pilotos y la pantalla redonda del radar, la consola blanca curva con sus molduras, lámparas de cúpula, cajas de
  equipo, cables por el piso y la franja de peligro); de noche, la misma base en penumbra verdosa, con el vidrio, los
  pilotos y las lámparas encendidos. Lo que se mueve: el pulso de luz que corre por el vidrio, los pilotos, el radar,
  el texto de las pantallas, las raíces que mece la ventilación y, una vez por bucle, el escáner de una patrulla que
  entra por la rendija. Esconde un microfilm en el collar y se agacha cuando el escáner entra por la rendija del techo.

Los otros cuatro temas del lote 10 (`arcontes`, `galia`, `nouveau`, `psicodelia`) se agregan igual: su rama en
`tema_mio`, sus colores en `colores_obj`, sus dos acciones en `ESP_MIOS`, su fondo en `FONDOS_MIOS` (y `ETIQ`, `NOCHE`)
y su nombre en `MIOS`; o en un archivo `gato21b.py` que importe este y registre los suyos de la misma forma.

Colores de la guía del tema (docs/diseno/temas/frente.md, «La base escondida en ruinas», y `TrazosFrente` en
ui/FrenteTrazos.kt): piedra verdosa, consola blanca y oscura, el turquesa del vidrio, el cobre, y el ámbar, el óxido y el
rojo de los pilotos como lo único cálido. Se toma la estética, no los diseños de ninguna película.
Importa la cadena (gato18, gato19 y los gato20*) y agrega sus temas sin tocar los demás.

Uso: python3 gato21.py hoja|fondos|bandas [tema…]   (lo deja en salida/, que no se sube)
"""
import importlib
import math
import random
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw, ImageFilter

sys.path.insert(0, str(Path(__file__).parent))
import gato18 as g18      # noqa: E402,F401  (carga toda la cadena)
for _m in ["gato19"] + sorted(p.stem for p in Path(__file__).parent.glob("gato20*.py")):
    importlib.import_module(_m)
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato17 as g17      # noqa: E402
from gato8 import hx, mezcla, esc, fuente, ESC   # noqa: E402
from gato9 import W, H, lienzo, degradado, luz, estrellas, cuad, subir, tramar   # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
N, SS, SUELO = g8.N, g8.SS, g8.SUELO
MIOS = ("frente",)
BUCLE = 160      # cuadros en que se repite todo lo que se mueve en estos fondos (12,8 s)
ANIM = g9.ANIM


def fase(tick, periodo=BUCLE, k=1.0):
    """Ángulo que da k vueltas exactas en `periodo` cuadros (para que el bucle del fondo cierre sin salto)."""
    return 2 * math.pi * k * (tick % periodo) / periodo


# =====================================================================
#  Capas nuevas: la manga de la pata lejana (detrás del cuerpo), el cuello de borrego y el punto de puños y cintura
# =====================================================================
_nombres = [c[0] for c in g8.CAPAS]
if "manga21" not in _nombres:
    g8.CAPAS.insert(_nombres.index("lejos") + 1, ("manga21", "B", "lejos", 0.45))
    _nombres = [c[0] for c in g8.CAPAS]
    i_ = _nombres.index("broche") + 1
    # El borrego es «collar» (rango 4): la chaqueta que lo toca se sombrea, y él no se sombrea contra el pelo.
    g8.CAPAS[i_:i_] = [("punto21", "F", "malla", 0.4), ("borrego21", "Y", "collar", 0.35)]
g8.RANGO.setdefault("malla", 4.6)

# =====================================================================
#  Accesorio: chaqueta de vuelo (cuero, borrego, punto y parche), collar con la cápsula y el auricular
# =====================================================================
g8.CON_COLLAR = tuple(g8.CON_COLLAR) + ("vuelo",)
_collar_prev = g8.collar


def collar(L, C, cab, acc):
    if acc != "vuelo":
        return _collar_prev(L, C, cab, acc)
    a, b, px, py = g8._cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.58, a[1] + (b[1] - a[1]) * 0.58)
    L.capsula("collar", [(m[0] - 4.4 * px, m[1] - 4.4 * py), (m[0] + 4.4 * px, m[1] + 4.4 * py)], 1.3)


g8.collar = collar


def capsula_pos(ps):
    """Dónde cuelga la cápsula del collar (la del microfilm), en la cuadrícula de 64."""
    a, b, px, py = g8._cuello(ps["C"], ps["cab"])
    m = (a[0] + (b[0] - a[0]) * 0.58, a[1] + (b[1] - a[1]) * 0.58)
    e1, e2 = (m[0] - 4.4 * px, m[1] - 4.4 * py), (m[0] + 4.4 * px, m[1] + 4.4 * py)
    j = max((e1, e2), key=lambda q: q[0] + q[1])
    return (j[0] - 0.4 * abs(px), j[1] + 1.9)


def capsula(L, x, y, abierta=False):
    """La cápsula de cromo del collar: un cilindro chico con su anillo de baquelita."""
    L.elipse("sombrero", x, y, 1.55, 1.95)
    L.elipse("sombrero3", x, y + 0.1, 1.0, 1.4)
    if not abierta:
        L.capsula("sombrero", [(x - 1.0, y - 0.5), (x + 1.0, y - 0.5)], 0.45)


def chaqueta_vuelo(L, ps):
    """Chaqueta de vuelo: cuero que cubre el pecho y el lomo hasta la cintura, cuello de borrego, cierre de cromo,
    cintura y puños de punto, mangas en las dos patas delanteras (la lejana, detrás del cuerpo) y un parche en el hombro."""
    Hh, C, rh, rc = ps["H"], ps["C"], ps["rh"], ps["rc"]
    ct, ht = (C[0], C[1] - rc), (Hh[0], Hh[1] - rh)
    dx, dy = ht[0] - ct[0], ht[1] - ct[1]; n = math.hypot(dx, dy) or 1
    nx, ny = -dy / n, dx / n
    lp = lambda t, up=0.0: (ct[0] + dx * t + nx * up, ct[1] + dy * t + ny * up)
    a, b, px, py = g8._cuello(C, ps["cab"])
    cu = (a[0] + (b[0] - a[0]) * 0.18, a[1] + (b[1] - a[1]) * 0.18)
    c1, c2 = (cu[0] - 5.0 * px, cu[1] - 5.0 * py), (cu[0] + 4.8 * px, cu[1] + 4.8 * py)
    fr = max((c1, c2), key=lambda q: q[0] + q[1])
    L.poligono("capa", [c1, c2, (C[0] + rc * 0.98, C[1] - rc * 0.05), (C[0] + rc * 0.72, C[1] + rc * 0.62),
                        lp(0.3, -rc * 1.6), lp(0.6, -rh * 1.42), lp(0.64, 0.8), lp(0.06, 1.3)])
    L.capsula("capa", [c1, c2], 2.6)
    L.capsula("punto21", [lp(0.6, 0.6), lp(0.585, -rh * 0.7), lp(0.58, -rh * 1.36)], 1.7)        # cintura de punto
    L.capsula("broche", [fr, (C[0] + rc * 0.9, C[1] + rc * 0.05), (C[0] + rc * 0.66, C[1] + rc * 0.56)], 0.5)   # cierre
    L.capsula("borrego21", [c1, c2], 2.7)                                                   # cuello de borrego
    L.elipse("borrego21", fr[0] + 0.2, fr[1] + 1.0, 1.4, 1.3)                               # la solapa
    # Mangas: del hombro al codo, con el puño de punto; el parche, en el hombro.
    for k, capa_, punto in (("fn", "capa", "punto21"), ("ff", "manga21", "manga21")):
        q = ps[k]
        fin = (q[1][0] + (q[2][0] - q[1][0]) * 0.2, q[1][1] + (q[2][1] - q[1][1]) * 0.2)
        L.capsula(capa_, [q[0], q[1], fin], 4.4)
        ux, uy = fin[0] - q[1][0], fin[1] - q[1][1]; nn = math.hypot(ux, uy) or 1
        if punto != capa_:
            L.capsula(punto, [(fin[0] - ux / nn * 0.3, fin[1] - uy / nn * 0.3), (fin[0] + ux / nn * 0.3, fin[1] + uy / nn * 0.3)], 4.2)
        if k == "fn":                                                                         # parche de misión
            pc = (q[0][0] + (q[1][0] - q[0][0]) * 0.15, q[0][1] + (q[1][1] - q[0][1]) * 0.15 - 1.4)
            L.elipse("cruz20b", pc[0], pc[1], 1.05, 1.05)


_cabeza_prev = g8.cabeza


def cabeza(L, cx, cy, inc, orejas, ojo, lengua, acc):
    ps = g17._actual.get("ps")
    if acc == "vuelo" and ps is not None and abs(ps["cab"][0] - cx) < 1e-6 and abs(ps["cab"][1] - cy) < 1e-6:
        chaqueta_vuelo(L, ps)
        obj = ps.get("objeto")
        capsula(L, *capsula_pos(ps), abierta=bool(obj and obj[1].get("abierta")))
    return _cabeza_prev(L, cx, cy, inc, orejas, ojo, lengua, acc)


g8.cabeza = cabeza
_espalda_prev = g8.acc_espalda


def acc_espalda(L, cu, cx, ny, todo):
    if cu != "vuelo":
        return _espalda_prev(L, cu, cx, ny, todo)
    if todo:
        L.poligono("capa", [(cx - 7.2, ny - 0.4), (cx + 7.2, ny - 0.4), (cx + 8.4, ny + 11.4), (cx - 8.4, ny + 11.4)])
        L.capsula("punto21", [(cx - 8.0, ny + 11.2), (cx + 8.0, ny + 11.2)], 1.7)
        L.capsula("borrego21", [(cx - 5.2, ny - 0.2), (cx + 5.2, ny - 0.2)], 3.2)
        for s in (-1, 1):                                                       # costuras de los hombros
            L.capsula("broche", [(cx + s * 3.0, ny + 2.4), (cx + s * 4.4, ny + 9.6)], 0.4)
    else:
        L.poligono("capa", [(cx + 4, ny), (cx - 4, ny + 0.4), (cx - 13, ny + 6.4), (cx - 14.4, ny + 11.4), (cx - 3, ny + 11.0), (cx + 5, ny + 7.4)])
        L.capsula("punto21", [(cx - 14.0, ny + 11.0), (cx - 3.0, ny + 10.6)], 1.7)
        L.capsula("borrego21", [(cx - 4.6, ny - 0.2), (cx + 4.4, ny - 0.4)], 3.2)
        L.elipse("cruz20b", cx + 2.6, ny + 4.4, 1.3, 1.3)


g8.acc_espalda = acc_espalda
_lado_prev, _frente_prev = g8.sombrero_lado, g8.sombrero_frente


def sombrero_lado(L, cx, cy, inc, som):
    if som != "auricular":
        return _lado_prev(L, cx, cy, inc, som)
    a = math.radians(inc)
    p = lambda x, y: g8.rot(cx, cy, x, y, a)
    P = lambda pts: [p(*q) for q in pts]
    L.capsula("sombrero2", P([(-3.6, -3.0), (-2.8, -6.0), (-0.6, -7.2), (1.0, -6.0)]), 0.9)         # diadema de cromo
    L.elipse("sombrero", *p(-4.0, -1.3), 2.0, 2.4, inc)                                          # copa de baquelita
    L.capsula("sombrero2", P([(-5.5, -0.2), (-5.3, -2.3), (-4.1, -3.5)]), 0.6)                       # su filo de cromo
    L.elipse("vidrio", *p(-4.3, -1.0), 0.65, 0.65)                                                 # piloto rojo
    L.capsula("sombrero2", P([(-3.0, 0.8), (0.0, 3.4), (3.6, 4.4)]), 0.8)                          # brazo del micrófono
    L.elipse("sombrero", *p(4.3, 4.3), 0.9, 0.8)                                                   # micrófono
    _lado_prev(L, cx, cy, inc, None)          # el objeto de la animación propia (si lo hay) se dibuja ahí


def sombrero_frente(L, cx, cy, som, desv=0.0):
    if som != "auricular":
        return _frente_prev(L, cx, cy, som, desv)
    dx = desv * 1.6
    L.capsula("sombrero2", [(cx - 6.2 + dx, cy - 1.0), (cx - 4.2 + dx, cy - 5.4), (cx + dx, cy - 6.6), (cx + 4.2 + dx, cy - 5.4), (cx + 6.0 + dx, cy - 1.6)], 0.9)
    L.elipse("sombrero", cx - 6.4 + dx * 0.4, cy + 0.2, 1.6, 2.6)
    L.elipse("vidrio", cx - 6.8 + dx * 0.4, cy + 0.8, 0.7, 0.7)


g8.sombrero_lado, g8.sombrero_frente = sombrero_lado, sombrero_frente
g8._spr.clear(); g8._rej.clear()

# =====================================================================
#  Material: gouache sobre cartón (pintura de concepto de los 70)
# =====================================================================
_ruido = {}


def ruido(forma, semilla, escala):
    """Ruido suave en [0, 1] del tamaño `forma` (alto, ancho), hecho de `escala` = (pasos en y, pasos en x)."""
    k = (forma, semilla, escala)
    if k not in _ruido:
        rng = np.random.default_rng(semilla)
        base = (rng.random((max(2, forma[0] // escala[0]), max(2, forma[1] // escala[1]))) * 255).astype(np.uint8)
        _ruido[k] = np.asarray(Image.fromarray(base).resize((forma[1], forma[0]), Image.BICUBIC), np.float32) / 255
    return _ruido[k]


def pinceladas(lado, semilla):
    """Campo de pinceladas: manchas alargadas e inclinadas (la huella del pincel plano), en tres niveles (-1, 0, 1)."""
    k = ("pinc", lado, semilla)
    if k not in _ruido:
        v = ruido((lado, lado * 2), semilla, (lado // 22, lado // 5))
        sesgo = np.arange(lado)[:, None] // 2 + np.arange(lado)[None, :]      # inclinación de la pincelada (unos 27°)
        v = np.take_along_axis(v, (sesgo % (lado * 2)), axis=1)
        _ruido[k] = (v > 0.6).astype(np.int8) - (v < 0.38).astype(np.int8)
    return _ruido[k]


def pintar_gouache(g, t, cel):
    """Gouache sobre cartón. Cada celda es una mancha opaca; encima: la luz cálida que entra por arriba (el filo de
    arriba del cuerpo se aclara en dos pasos), el rebote frío de abajo, la huella del pincel plano (tres niveles,
    inclinada) y el grano del cartón. El contorno es pizarra oscura, no negro, y a ratos el pincel seco deja ver el
    cartón. Lo que es luz (el piloto, el haz del escáner, los brillos) no lleva textura."""
    col = t["col"]
    claro = set("PSLWDTAYBFCMZRI")
    filas = len(g)
    fuera = lambda r, c: not (0 <= r < filas and 0 <= c < N) or g[r][c] == "."
    k_luz, k_reb = t["k_luz"], t["k_reb"]
    img = Image.new("RGBA", (N, filas), (0, 0, 0, 0)); px = img.load()
    for r, c, v in g8.celdas(g):
        base = col[v]
        if v in claro:
            if g[r - 1][c] == "K" if r > 0 else False:
                if fuera(r - 2, c) or fuera(r - 1, c - 1) or fuera(r - 1, c + 1):
                    base = mezcla(base, t["luz"], k_luz)                     # el filo que da a la luz
            elif r > 1 and g[r - 2][c] == "K" and (fuera(r - 3, c)):
                base = mezcla(base, t["luz"], k_luz * 0.45)
            if r + 1 < filas and g[r + 1][c] == "K" and fuera(r + 2, c) and v in "SDTBF":
                base = mezcla(base, t["rebote"], k_reb)                      # el rebote del suelo, frío
        if v == "K" and (r * 7 + c * 13) % 11 == 0:
            base = mezcla(base, t["carton"], 0.35)                           # pincel seco: se asoma el cartón
        px[c, r] = base + (255,)
    s = cel * SS
    big = img.resize((N * s, filas * s), Image.NEAREST)
    a = np.asarray(big).astype(np.float32)
    letra = np.array([[v for v in fila] for fila in g], "<U1").repeat(s, 0).repeat(s, 1)
    pin = pinceladas(N * s, 21)[:filas * s]
    tex = np.isin(letra, list("PSLWDTAYBFCRI"))
    a[..., :3] *= np.where(tex, 1 + PINTURA["pincel"] * pin, 1.0)[..., None]
    out = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8), "RGBA").resize((N * cel, filas * cel), PINTURA["filtro"])
    if not PINTURA["grano"]:
        return out
    o = np.asarray(out).astype(np.float32)
    # Grano del cartón: puntos sueltos un poco más claros u oscuros (dos niveles, para que el atlas pese poco).
    rng = np.random.default_rng(5)
    gr = rng.random(o.shape[:2])
    d_ = PINTURA["grano"]
    o[..., :3] *= np.where(gr < d_ / 2, 0.95, np.where(gr > 1 - d_ / 2, 1.04, 1.0))[..., None]
    return Image.fromarray(np.clip(o, 0, 255).astype(np.uint8), "RGBA")


# Cuánto se nota cada cosa (el grano y el pincel cuestan peso en el atlas).
PINTURA = dict(grano=0.12, pincel=0.08, filtro=Image.BOX)
_pintar_prev = g8.pintar


def pintar(g, t, cel, oscuro=False):
    if t["material"] == "gouache":
        return pintar_gouache(g, t, cel)
    return _pintar_prev(g, t, cel, oscuro)


g8.pintar = pintar

# =====================================================================
#  El tema
# =====================================================================
HUESO, PIZARRA, AMBAR, AMBAR_C, ROJO = hx("E4DED1"), hx("23252A"), hx("B85C0F"), hx("F2A33A"), hx("D9372B")
BAQUELITA, CROMO = hx("2A2622"), hx("B9B6AE")


def colores_obj(nombre, oscuro, j):
    """U, V, X, Q de cada animación propia, de día y de noche."""
    c = {
        ("frente", 0): dict(U=hx("D08C3A"), V=hx("3A2E26"), X=CROMO, Q=hx("FFF6DC")),  # microfilm ámbar, cuadros, tapa, destello
        ("frente", 1): dict(U=hx("3A2E26"), V=AMBAR_C, X=CROMO, Q=hx("E4FFF4")),        # el haz del escáner
    }[(nombre, j)]
    if oscuro and nombre == "frente" and j == 1:
        c["Q"] = hx("BFF5E0")
    return c


_colores_prev = g17.colores


def colores(nombre, oscuro, j, t):
    if nombre in MIOS:
        return colores_obj(nombre, oscuro, j)
    return _colores_prev(nombre, oscuro, j, t)


g17.colores = colores
for _n in MIOS:
    g17.COL[_n] = tuple(colores_obj(_n, False, 0)[k] for k in "UVXQ")


def tema_mio(nombre, oscuro):
    if nombre == "frente":
        # frente (guía, «la base escondida en ruinas»): hormigón DADBD2 / tinta 1E2523, borde 979B91, óxido B5561C,
        # vidrio 8FF0D2 · oscuro: 0E1614, DCE5DF, 33433E. Del gato: ámbar F2A33A, rojo D9372B, baquelita 2A2622, cromo B9B6AE
        P = hx("6E7780")
        cuero, cuero2 = hx("8F5229"), hx("5E3318")
        col = g8.pelaje(P, PIZARRA, S=hx("525A63"), L=hx("A7AEB3"), W=hx("646D76"), D=hx("4C535B"), T=hx("5A626B"),
                        E=AMBAR_C, N=hx("B98A86"), R=hx("8E7F84"), C=BAQUELITA, G=CROMO, A=cuero, B=cuero2,
                        Y=hx("E8DFC9"), M=BAQUELITA, Z=CROMO, O=ROJO, J=AMBAR)
        col["F"] = hx("33302D")                 # punto de puños y cintura
        col["I"] = AMBAR_C                      # el parche de misión
        col["H"] = hx("FBF4E4")
        t = g8.T(col, hx("DADBD2") if not oscuro else hx("0E1614"), hx("1E2523") if not oscuro else hx("DCE5DF"),
                 hx("979B91") if not oscuro else hx("33433E"), "ninguno", "gouache", ("vuelo", "auricular"),
                 luz=hx("FBF6E4"), k_luz=0.34, rebote=hx("8FB5A8"), k_reb=0.2, carton=hx("A8906A"))
        t["sombra_col"], t["sombra_a"] = (34, 44, 40), 62
        if oscuro:
            nc = g9.de_noche(col, hx("1E3A32"), 0.12)          # la penumbra verdosa de la base
            nc.update(O=hx("FF5A4A"), I=hx("E39A3C"), F=hx("26282A"), Y=hx("C9C2B2"))
            t["col"] = nc
            t.update(luz=hx("F4E6C4"), k_luz=0.26, rebote=hx("4FA88C"), k_reb=0.26, carton=hx("4A4C3A"))
            t["sombra_col"], t["sombra_a"] = (0, 0, 0), 72
    else:
        raise ValueError(nombre)
    t["oscuro"] = oscuro
    t["ajustar"] = False
    col = dict(t["col"]); col.update(colores_obj(nombre, oscuro, 0))
    t["col"] = col
    return t


_tema_prev = g8.tema


def tema(nombre, oscuro=False):
    if nombre in MIOS:
        return tema_mio(nombre, oscuro)
    return _tema_prev(nombre, oscuro)


for _m in ("gato8", "gato9", "gato10", "gato12", "gato13", "gato16", "gato17", "gato18", "gato19"):
    if _m in sys.modules:
        sys.modules[_m].tema = tema

for _n in MIOS:
    if _n not in g9.NUEVOS:
        g9.NUEVOS.append(_n)

# =====================================================================
#  Las dos acciones: esconde el microfilm en el collar · se agacha cuando el escáner entra por la rendija del techo
# =====================================================================
brazo, rect = g17.brazo, g17.rect

# (pata, inclinación de la cabeza, ojo, orejas). Mira el microfilm, mira alrededor, lo sube al collar, lo mete, cierra.
MICRO = [((44.2, 40.4), 13, "abierto", 0.0), ((44.2, 40.6), -8, "medio", 0.5), ((43.4, 37.8), 17, "medio", 0.15),
         ((42.6, 36.8), 19, "medio", 0.0), ((43.2, 39.2), 9, "abierto", 0.0), (None, 1, "cerrado", 0.0)]


def microfilm(k):
    pata, inc, ojo, orejas = MICRO[k]
    p = dict(g8.SENTADO)
    p.update(inc=inc, ojo=ojo, orejas=orejas, cab=(38.8, 27.4))
    if pata:
        p["fn"] = brazo((36, 43), pata, (1.4, 0.9))
    caps = capsula_pos(p)
    return p, dict(obj=caps, pata=pata or (37.0, SUELO), caps=caps, k=k, abierta=k == 3)


AGAZAPADO = g8.pose(H=(21.5, 50.2), rh=7.0, C=(37.5, 51.2), rc=7.4, cab=(46.6, 47.4), inc=8, orejas=1.0,
                    fn=[(40.5, 53.6), (44.4, 55.6), (47.0, 58.0), (49.4, SUELO)], ff=[(37.2, 54.0), (41.0, 56.0), (43.4, 58.2), (45.8, SUELO)],
                    hn=[(21.5, 52.6), (26.6, 56.0), (20.6, 58.4), (23.4, SUELO)], hf=[(24.0, 52.6), (29.0, 56.0), (23.0, 58.4), (25.8, SUELO)],
                    cola=[(14.6, 51.2), (10.2, 53.6), (6.6, 56.4), (4.2, 58.2), (3.0, 59.0), (2.2, 59.4)], colaw=[3.8, 4.0, 4.0, 3.8, 3.4, 3.0])
# (cuánto se agacha, dónde cae el haz en el suelo). El haz del escáner entra por detrás y pasa por encima.
REFL = [(0.0, -9.0), (0.6, 5.0), (1.0, 18.0), (1.0, 30.0), (1.0, 42.0), (1.0, 55.0), (0.55, 74.0), (0.12, 96.0)]


def agacharse(k):
    f, bx = REFL[k]
    p = g8.interp(g8.DE_PIE, AGAZAPADO, f)
    if k == 0:
        p.update(inc=-15, ojo="abierto", orejas=0.2)          # oye la patrulla y levanta la cabeza
    elif k in (2, 3, 4):
        p.update(ojo="abierto", inc=10)
    elif k == 5:
        p.update(ojo="medio", inc=6)
    elif k == 6:
        p.update(ojo="medio", orejas=0.6)
    return p, dict(obj=(bx, SUELO), k=k)


g17.ACCIONES.update(microfilm=(microfilm, 6, "sentado"), agacharse=(agacharse, 8, "quieto"))
g17.ESCALA.update(microfilm=1.0, agacharse=1.0)


def o_microfilm(L, a):
    """La tira de microfilm en la pata (película oscura con sus cuadros ámbar); la mete en la cápsula, que se abre y se
    cierra con un destello."""
    k = a["k"]; px, py = a["pata"]; cx, cy = a["caps"]
    if k in (0, 1, 2):
        x, y = px + 0.8, py - 1.8
        L.capsula("f_U", [(x - 0.4, y + 0.8), (x + 3.6, y - 1.4)], 2.5)
        for u in (0.2, 0.55, 0.9):
            xx, yy = x - 0.4 + 4.0 * u, y + 0.8 - 2.2 * u
            rect(L, "f_V", xx - 0.3, yy - 0.3, xx + 0.3, yy + 0.3)
    elif k == 3:
        L.capsula("f_U", [(cx + 0.2, cy - 1.0), (cx + 1.6, cy - 2.6)], 2.0)                  # entra en la cápsula
        rect(L, "f_V", cx + 0.9, cy - 2.4, cx + 1.7, cy - 1.6)
        L.poligono("f_X", [(cx - 1.2, cy - 1.6), (cx + 0.2, cy - 3.6), (cx + 1.0, cy - 3.2), (cx - 0.4, cy - 1.2)])   # la tapa abierta
    elif k in (4, 5):
        r = 2.4 if k == 4 else 1.4
        x, y = cx + 1.8, cy - 1.8
        L.capsula("f_Q", [(x - r, y), (x + r, y)], 0.55)
        L.capsula("f_Q", [(x, y - r), (x, y + r)], 0.55)


def o_escaner(L, a):
    """El barrido del escáner de una patrulla, que entra por una rendija del techo: angosto arriba, se abre hacia el
    piso y pasa por encima del gato. Va tramado, como luz: sobre el gato deja filas claras, y en el piso, la mancha."""
    bx, gy = a["obj"]
    sx, sy = bx - 8.0, -2.0
    vistos = set()
    for r in range(0, 61):
        f = (r - sy) / (gy - sy)
        xc = sx + (bx - sx) * f
        w = 0.7 + 6.4 * f
        for c in range(int(xc - w) - 1, int(xc + w) + 2):
            if not (0 <= c < N): continue
            d = abs(c + 0.5 - xc) / w
            if d >= 1: continue
            if r % 2 == 0 and (d < 0.5 or c % 2 == 0):              # en filas (sin huecos cerrados: el acabado los rellenaría)
                vistos.add((r, c))
    for r in range(int(gy) - 3, int(gy) + 2):                       # la mancha de luz en el suelo
        for c in range(int(bx - 10), int(bx + 11)):
            if 0 <= c < N and 0 <= r < N and ((c + 0.5 - bx) / 9.5) ** 2 + ((r + 0.5 - gy + 0.6) / 2.4) ** 2 < 1:
                if r % 2 == 0 or (abs(c + 0.5 - bx) < 4.5 and r >= int(gy) - 1):
                    vistos.add((r, c))
    for r, c in vistos:
        rect(L, "f_Q", c, r, c + 1, r + 1)


ESP_MIOS = {
    "frente": [("microfilm", o_microfilm, "esconde un microfilm en el collar"),
               ("agacharse", o_escaner, "se agacha cuando el escáner de una patrulla entra por la rendija del techo")],
}
ESCALA_MIA = {("frente", 0): 1.35}
g17.ESP.update(ESP_MIOS)
for nombre, lista in ESP_MIOS.items():
    for j, (accion, f, _) in enumerate(lista):
        fn, n, _base = g17.ACCIONES[accion]
        cuadros = []
        for k in range(n):
            p, a = fn(k)
            a["accion"] = accion
            a["escala"] = ESCALA_MIA.get((nombre, j), g17.ESCALA[accion])
            cuadros.append(dict(p, objeto=(f, a), semilla=k))
        g8.ANIMS[f"esp_{nombre}_{j}"] = cuadros
g8._rej.clear(); g8._spr.clear()

# =====================================================================
#  Fondos (a 1 pixel por dp, ampliados ×3 sin suavizar, como los de gato9). Todo lo que se mueve repite cada BUCLE cuadros.
# =====================================================================


def punto(px, x, y, col):
    if 0 <= x < W and 0 <= y < H:
        px[int(x), int(y)] = col


def granular(im, semilla, fuerza=0.045, densidad=0.16, y0=0, y1=H, x0=0, x1=W):
    """El grano en el fondo: pixeles sueltos un poco más claros u oscuros (a 1 pixel por dp)."""
    px = im.load(); rnd = random.Random(semilla)
    for y in range(y0, y1):
        for x in range(x0, x1):
            q = rnd.random()
            if q < densidad / 2:
                px[x, y] = esc(px[x, y], 1 - fuerza)
            elif q < densidad:
                px[x, y] = esc(px[x, y], 1 + fuerza)


def humedad(im, semilla, n, y0, y1, col, k=0.3, x0=0, x1=W, r=(3, 9)):
    """Manchas de humedad: borrones de borde tramado (más fuertes al centro), un poco más largos que altos."""
    px = im.load(); rnd = random.Random(semilla)
    for _ in range(n):
        cx, cy = rnd.uniform(x0, x1), rnd.uniform(y0, y1)
        rx = rnd.uniform(*r); ry = rx * rnd.uniform(0.35, 0.7)
        for y in range(int(cy - ry), int(cy + ry) + 1):
            for x in range(int(cx - rx), int(cx + rx) + 1):
                if not (x0 <= x < x1 and y0 <= y < y1 and 0 <= x < W and 0 <= y < H): continue
                dd = ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2
                if dd < 1 and tramar(1 - dd ** 0.7, x, y):
                    px[x, y] = mezcla(px[x, y], col, k)


def sillares(im, x0, y0, x1, y1, P, semilla=0):
    """Muro de sillares viejos: hiladas de altura desigual, bloques de largo distinto y esquinas gastadas; cada bloque
    con su tono, el filo de arriba con luz, la junta honda y la sombra de abajo; desconchones y musgo en las juntas."""
    px = im.load(); rnd = random.Random(semilla)
    tonos, junta, luz_, sombra, musgo = P["piedra"], P["junta"], P["piedra_luz"], P["piedra_sombra"], P["musgo"]
    y = y0
    while y < y1:
        alto = rnd.choice((5, 6, 6, 7, 8))
        x = x0 - rnd.randint(0, 10)
        while x < x1:
            largo = rnd.randint(9, 19)
            tono = rnd.choice(tonos)
            for yy in range(y, min(y + alto, y1)):
                for xx in range(max(x, x0), min(x + largo, x1)):
                    fy, fx = yy - y, xx - x
                    borde = fy == 0 or fx == 0 or fy == alto - 1 or fx == largo - 1
                    esquina = (fy in (0, alto - 1)) and (fx in (0, largo - 1))
                    if borde and (esquina or fy == alto - 1 or fx == 0):
                        col = junta
                    elif fy == 0 or (fy == 1 and fx in (1, largo - 2)):
                        col = luz_                                   # el filo de arriba recibe la luz de las lámparas
                    elif fy == alto - 2 or fx == largo - 2:
                        col = sombra
                    else:
                        col = tono
                    punto(px, xx, yy, col)
            if rnd.random() < 0.35:                                   # desconchón
                cx, cy = x + rnd.randint(2, max(2, largo - 3)), y + rnd.randint(1, max(1, alto - 2))
                punto(px, cx, cy, sombra); punto(px, cx + 1, cy, sombra); punto(px, cx, cy - 1, luz_)
            if rnd.random() < 0.3:                                    # musgo en la junta de abajo
                for k in range(rnd.randint(2, 6)):
                    punto(px, x + 2 + k, y + alto - 1 - (k % 2), musgo)
            x += largo
        y += alto


def raiz(d, pts, col, luz_=None, ancho=1):
    """Una raíz colgante: polilínea irregular, más gruesa arriba, con su filo de luz a la izquierda."""
    for i, ((xa, ya), (xb, yb)) in enumerate(zip(pts, pts[1:])):
        w = ancho if i < len(pts) * 0.45 else 1
        d.line([xa, ya, xb, yb], fill=col, width=w)
        if luz_ is not None and i % 2 == 0:
            d.point((xa - 1, ya), fill=luz_)


def camino_raiz(x, y, largo, semilla, deriva=0.0):
    """Puntos de una raíz que baja serpenteando desde (x, y)."""
    rnd = random.Random(semilla); pts = [(x, y)]
    for k in range(largo):
        x += rnd.choice((-1, 0, 0, 1)) * 0.6 + deriva
        y += 1.6
        pts.append((x, y))
    return pts


def vidrio_grabado(im, x0, y0, x1, y1, P, trazos, semilla=0):
    """Panel de vidrio grabado: se ve el muro detrás, teñido de verde; los trazos grabados brillan en turquesa con su
    halo, y el canto de abajo, por donde entra la luz, brilla más. Encima el riel oscuro; abajo la base con su rejilla.
    Devuelve los trazos como polilíneas (para el pulso de luz)."""
    px = im.load(); d = ImageDraw.Draw(im)
    for y in range(y0, y1):
        for x in range(x0, x1):
            k = P["vidrio_k"] + 0.12 * ((y - y0) / (y1 - y0))
            px[x, y] = mezcla(px[x, y], P["vidrio"], k)
    mascara = Image.new("L", (W, H), 0); dm = ImageDraw.Draw(mascara)
    polis = []
    for t_ in trazos:
        pts = [(min(x1 - 1, max(x0, x)), min(y1 - 1, max(y0, y))) for x, y in t_]
        dm.line(pts, fill=255)
        polis.append(pts)
    halo = mascara.filter(ImageFilter.MaxFilter(3))
    m, h_ = np.asarray(mascara), np.asarray(halo)
    for y in range(y0, y1):
        for x in range(x0, x1):
            if m[y, x]:
                px[x, y] = P["trazo"]
            elif h_[y, x]:
                px[x, y] = mezcla(px[x, y], P["trazo"], P["halo"])
    rnd = random.Random(semilla)
    for _ in range(3):                                           # reflejos en diagonal
        x = rnd.randint(x0 + 4, x1 - 14)
        for j in range(10):
            if (j + x) % 3: punto(px, x + j, y0 + 4 + j * 2, mezcla(px[min(W - 1, x + j), min(H - 1, y0 + 4 + j * 2)], P["reflejo"], 0.25))
    d.line([x0, y1 - 1, x1 - 1, y1 - 1], fill=P["canto"])      # el canto iluminado
    d.line([x1 - 1, y0, x1 - 1, y1 - 1], fill=mezcla(P["vidrio"], P["trazo"], 0.5))
    d.rectangle([x0 - 2, y0 - 3, x1 + 1, y0 - 1], fill=P["oscura"][1])          # riel
    d.line([x0 - 2, y0 - 3, x1 + 1, y0 - 3], fill=P["oscura"][0])
    d.ellipse([x1, y0 - 3, x1 + 2, y0 - 1], fill=P["oscura"][0])
    d.rectangle([x0 - 2, y1, x1 + 1, 63], fill=P["oscura"][1])                 # base
    d.line([x0 - 2, y1, x1 + 1, y1], fill=P["oscura"][0])
    for xb in range(x0 + 6, x1 - 6, 26):
        d.line([xb, y1 + 1, xb, 63], fill=P["oscura"][2])
    gx = (x0 + x1) // 2 - 9                                                     # rejilla de ventilación
    d.rectangle([gx, y1 + 3, gx + 18, y1 + 6], fill=P["oscura"][2])
    for xx in range(gx + 1, gx + 18, 2): d.line([xx, y1 + 4, xx, y1 + 5], fill=P["rejilla"])
    return polis


def largo_poli(pts):
    out = [0.0]
    for (xa, ya), (xb, yb) in zip(pts, pts[1:]):
        out.append(out[-1] + math.hypot(xb - xa, yb - ya))
    return out


def en_poli(pts, acum, s):
    """El punto que está a la distancia s a lo largo de la polilínea."""
    s %= acum[-1] or 1
    for i in range(len(pts) - 1):
        if acum[i + 1] >= s:
            f = (s - acum[i]) / max(1e-6, acum[i + 1] - acum[i])
            return pts[i][0] + (pts[i + 1][0] - pts[i][0]) * f, pts[i][1] + (pts[i + 1][1] - pts[i][1]) * f
    return pts[-1]


def arco(cx, cy, r, a0, a1, n=40, ry=None):
    ry = ry or r
    return [(cx + r * math.cos(math.radians(a0 + (a1 - a0) * k / n)), cy + ry * math.sin(math.radians(a0 + (a1 - a0) * k / n))) for k in range(n + 1)]


def espiral(cx, cy, r0, r1, vueltas, n=120):
    return [(cx + (r0 + (r1 - r0) * k / n) * math.cos(2 * math.pi * vueltas * k / n),
             cy + (r0 + (r1 - r0) * k / n) * math.sin(2 * math.pi * vueltas * k / n) * 0.95) for k in range(n + 1)]


def consola_oscura(im, x0, y0, x1, y1, P, semilla=0):
    """Gabinete de consola oscura: frente biselado, una pantalla de tubo con texto, filas de botones cuadrados y
    pilotos. Devuelve (pilotos, pantalla) para animarlos."""
    d = ImageDraw.Draw(im); rnd = random.Random(semilla)
    o1, o2, o3 = P["oscura"]
    d.rectangle([x0, y0, x1, y1], fill=o2)
    d.line([x0, y0, x1, y0], fill=o1); d.line([x0, y0, x0, y1], fill=o1); d.line([x1, y0, x1, y1], fill=o3)
    pilotos = []
    # Pantalla de tubo.
    sx0, sy0 = x0 + 3, y0 + 3
    sw, sh = min(14, x1 - x0 - 6), 9
    d.rounded_rectangle([sx0 - 1, sy0 - 1, sx0 + sw + 1, sy0 + sh + 1], radius=2, fill=o3)
    d.rounded_rectangle([sx0, sy0, sx0 + sw, sy0 + sh], radius=2, fill=P["pantalla"])
    pantalla = (sx0 + 2, sy0 + 2, sw - 4, sh - 3)
    # Botones y pilotos.
    y = sy0 + sh + 4
    while y < y1 - 3:
        for x in range(x0 + 3, x1 - 2, 3):
            q = rnd.random()
            col = P["boton"][int(q * len(P["boton"]))]
            d.rectangle([x, y, x + 1, y + 1], fill=col)
        y += 4
        if rnd.random() < 0.5 and y < y1 - 3:
            for x in range(x0 + 3, x1 - 2, 4):
                pilotos.append((x, y, rnd.choice((P["piloto_r"], P["piloto_a"], P["trazo"])), rnd.choice((8, 10, 16, 20)), rnd.randint(0, 19)))
                punto(im.load(), x, y, o3)
            y += 3
    return pilotos, pantalla


def lampara(im, x, y, P, alto=10):
    """Lámpara de cúpula blanca sobre su pie; ilumina el muro y el piso a su alrededor."""
    d = ImageDraw.Draw(im)
    d.rectangle([x - 1, y + 4, x, y + alto], fill=P["oscura"][1]); d.line([x - 1, y + 4, x - 1, y + alto], fill=P["oscura"][0])
    d.rectangle([x - 3, y + alto, x + 2, y + alto + 1], fill=P["oscura"][1])
    d.pieslice([x - 5, y - 1, x + 4, y + 8], 180, 360, fill=P["cupula"][0])
    d.line([x - 5, y + 3, x + 4, y + 3], fill=P["cupula"][1])
    d.point((x - 3, y + 1), fill=P["cupula"][2])
    return (x, y + 4)


def consola_blanca(im, x0, x1, ytop, P):
    """La consola blanca curva: un mostrador redondo anillado, visto un poco desde arriba. Arriba, el borde del anillo
    (una elipse con su hueco en sombra y el tablero de pilotos); abajo, las molduras redondas apiladas que se curvan con
    el cuerpo, cada una con su luz arriba y su sombra abajo, más oscuras del lado lejos de la luz."""
    px = im.load(); d = ImageDraw.Draw(im)
    b1, b2, b3, b4 = P["blanca"]
    cx, rx = (x0 + x1) / 2, (x1 - x0) / 2
    ry = 4.0
    # El cuerpo: molduras que siguen la curva del frente (bajan al centro).
    for x in range(x0, x1 + 1):
        u = (x - cx) / rx
        if abs(u) > 1: continue
        caida = ry * math.sqrt(max(0.0, 1 - u * u))
        lado = max(0.0, u) ** 1.5 * 0.55 + max(0.0, -u - 0.85) * 1.5     # el borde derecho y la punta izquierda, en sombra
        y = ytop + caida
        for k, alto in enumerate((3, 3, 3, 3, 4)):
            for j in range(alto):
                yy = int(round(y + j))
                if yy > 63: break
                col = b1 if j == 0 else (b2 if j < alto - 1 else b3)
                punto(px, x, yy, mezcla(col, b4, min(0.8, lado)))
            y += alto
        for yy in range(int(round(y)), 64):
            punto(px, x, yy, b4)
    # El borde del anillo, arriba, con el hueco del centro en sombra.
    d.ellipse([x0, ytop - ry, x1, ytop + ry], fill=b1)
    d.ellipse([x0 + 7, ytop - ry + 1.4, x1 - 7, ytop + ry - 1.4], fill=b3)
    d.ellipse([x0 + 10, ytop - ry + 2.2, x1 - 10, ytop + ry - 1.6], fill=b4)
    d.arc([x0, ytop - ry, x1, ytop + ry], 200, 340, fill=P["blanca_luz"])
    d.arc([x0, ytop - ry, x1, ytop + ry], 10, 170, fill=b2)
    # El tablero de pilotos sobre el borde de adelante.
    tx0, tx1 = int(cx - 26), int(cx + 26)
    pilotos = [(x, int(ytop + ry - 0.5), P["piloto_a"] if (x // 5) % 3 else P["trazo"], 16, x % 13) for x in range(tx0, tx1, 5)]
    for (x, y, _, _, _) in pilotos:
        punto(px, x, y, P["oscura"][1])
    return pilotos


def cajas(im, x0, ybase, P, pila, semilla=0):
    """Cajas de equipo apiladas: verde casi negro, con bisagras, pestillos de cromo y una etiqueta ámbar."""
    d = ImageDraw.Draw(im); rnd = random.Random(semilla)
    y = ybase
    for (w, h, dx) in pila:
        x = x0 + dx
        d.rectangle([x, y - h, x + w, y], fill=P["caja"][1])
        d.line([x, y - h, x + w, y - h], fill=P["caja"][0]); d.line([x + w, y - h, x + w, y], fill=P["caja"][2])
        d.line([x, y - h + 2, x + w, y - h + 2], fill=P["caja"][2])
        for xx in (x + 2, x + w - 3): d.rectangle([xx, y - h + 1, xx + 1, y - h + 3], fill=P["cromo"])
        if rnd.random() < 0.7:
            ex = x + rnd.randint(3, max(3, w - 7)); d.rectangle([ex, y - h // 2, ex + 4, y - h // 2 + 1], fill=P["etiqueta"])
        y -= h


def cable(d, pts, col, luz_):
    """Un cable grueso que serpentea por el piso, con su brillo arriba."""
    c = g8.catmull(pts, 10)
    for (xa, ya), (xb, yb) in zip(c, c[1:]):
        d.line([xa, ya, xb, yb], fill=col, width=2)
    for (xa, ya) in c[::3]:
        d.point((round(xa), round(ya) - 1), fill=luz_)


PAL_D = dict(
    losa=(hx("B9BBB0"), hx("A6A99D"), hx("8E9286")), mancha=hx("7E8576"),
    piedra=(hx("9A9D91"), hx("8C8F84"), hx("A3A598"), hx("858A7E")), junta=hx("5E6660"), piedra_luz=hx("B6B8AB"),
    piedra_sombra=hx("6E746A"), musgo=hx("5E6B45"), raiz=hx("4A4636"), raiz_luz=hx("6E6950"),
    cobre=(hx("C9956A"), hx("8A6A4C"), hx("5C4532")),
    vidrio=hx("1F5A4E"), vidrio_k=0.74, trazo=hx("8FF0D2"), halo=0.3, canto=hx("C8FFF0"), reflejo=hx("E8F4EE"),
    oscura=(hx("3A4A44"), hx("1E2A26"), hx("141C19")), rejilla=hx("6E7C76"), pantalla=hx("0F1A16"), texto=hx("9FD8A8"),
    boton=(hx("E6E5DD"), hx("F2A33A"), hx("3E8C76"), hx("4A5A54"), hx("4A5A54"), hx("D9372B")),
    piloto_r=hx("D9372B"), piloto_a=hx("F2A33A"),
    blanca=(hx("F2F1EA"), hx("E6E5DD"), hx("C3C4BA"), hx("A3A69B")), blanca_luz=hx("FFFFFA"),
    cupula=(hx("F4F2EA"), hx("C9CAC0"), hx("FFFFFF")), luz_lampara=hx("FBF6E4"),
    caja=(hx("3E4C46"), hx("26322D"), hx("18201D")), cromo=hx("B9B6AE"), etiqueta=hx("D9A62B"),
    piso=(hx("C3C5BA"), hx("B7BAAE"), hx("ABAEA2")), piso_junta=hx("9A9E92"), cable=hx("1E2523"), cable_luz=hx("4E5A55"),
    peligro=(hx("D9A62B"), hx("1E2523")), sombra=hx("8E9388"), rendija=hx("3B4440"), escaner=hx("DDFBEF"),
)
PAL_N = dict(
    losa=(hx("1E2724"), hx("19211E"), hx("141B18")), mancha=hx("101614"),
    piedra=(hx("26302C"), hx("222B27"), hx("2A3430"), hx("1F2824")), junta=hx("121916"), piedra_luz=hx("34403B"),
    piedra_sombra=hx("18201D"), musgo=hx("2C3624"), raiz=hx("2A2A20"), raiz_luz=hx("3A3A2C"),
    cobre=(hx("7A5A3E"), hx("4E3C2C"), hx("30251B")),
    vidrio=hx("123A33"), vidrio_k=0.78, trazo=hx("8FF0D2"), halo=0.4, canto=hx("D4FFF2"), reflejo=hx("4E8A7C"),
    oscura=(hx("26332E"), hx("121A17"), hx("0B110F")), rejilla=hx("3A4A44"), pantalla=hx("0F1A16"), texto=hx("9FD8A8"),
    boton=(hx("8A918A"), hx("F2A33A"), hx("6FD1B4"), hx("2A3530"), hx("2A3530"), hx("FF5A4A")),
    piloto_r=hx("FF5A4A"), piloto_a=hx("F2A33A"),
    blanca=(hx("8E968F"), hx("737B74"), hx("545C56"), hx("363E39")), blanca_luz=hx("B8D2C6"),
    cupula=(hx("FBF6E6"), hx("D8D2BE"), hx("FFFFFF")), luz_lampara=hx("F4E6C4"),
    caja=(hx("26322D"), hx("161E1B"), hx("0D1311")), cromo=hx("7C7A72"), etiqueta=hx("8A6E24"),
    piso=(hx("1C2421"), hx("19201E"), hx("161D1A")), piso_junta=hx("111714"), cable=hx("0A0E0D"), cable_luz=hx("26302C"),
    peligro=(hx("8A6E24"), hx("101614")), sombra=hx("0F1513"), rendija=hx("0A0F0D"), escaner=hx("BFF5E0"),
)
# El barrido del escáner que entra por la rendija del techo: cuándo pasa (fracción del bucle) y en cuántas posiciones.
ESCANER = dict(desde=0.62, hasta=0.86, estados=12)


def fondo_frente(t):
    """Frente exterior: la base escondida en ruinas. Muro de sillares viejos con raíces que cuelgan, la losa del techo
    con manchas de humedad y una rendija, los tubos de cobre, dos paneles de vidrio grabado con trazos turquesa
    iluminados por el canto, consolas oscuras con su pantalla de tubo, botones y pilotos, la pantalla redonda del radar,
    la consola blanca curva con sus molduras, lámparas de cúpula, cajas de equipo, cables gruesos por el piso y la franja
    de peligro. De día, la base con las lámparas encendidas; de noche, la misma base en penumbra verdosa, alumbrada por
    el vidrio, los pilotos y las lámparas. Lo que se mueve: el pulso de luz que corre por los trazos del vidrio, los
    pilotos, el barrido del radar, el texto de las pantallas, las raíces que mece el aire de la ventilación y, una vez
    por bucle, el barrido de un escáner que entra por la rendija del techo."""
    o = t["oscuro"]
    P = PAL_N if o else PAL_D
    im, d = lienzo(P["piedra"][1])
    px = im.load()
    # --- El muro de sillares (todo el fondo, de la losa al piso).
    sillares(im, 0, 8, W, 63, P, semilla=5)
    humedad(im, 3, 22, 10, 60, P["piedra_sombra"], 0.45, r=(4, 12))
    # --- La losa del techo con manchas de humedad y la rendija.
    for y in range(0, 8):
        for x in range(W):
            px[x, y] = P["losa"][0] if y < 5 else (P["losa"][1] if y < 7 else P["losa"][2])
    humedad(im, 8, 30, 0, 8, P["mancha"], 0.5, r=(3, 10))
    d.line([0, 7, W, 7], fill=P["losa"][2])
    RENDIJA = 262
    rnd = random.Random(2)
    y_, x_ = 0, RENDIJA
    grieta = []
    while y_ < 8:                                               # la grieta zigzaguea por la losa
        grieta.append((x_, y_)); y_ += 1; x_ += rnd.choice((-1, 0, 1))
    for (x, y) in grieta:
        punto(px, x, y, P["rendija"]); punto(px, x + 1, y, P["rendija"])
    # --- Tubos de cobre bajo la losa (con codos y bridas), que bajan por la pilastra derecha.
    c1, c2, c3 = P["cobre"]
    for (yt, xa, xb) in ((9, 40, 300), (12, 60, 304)):
        d.line([xa, yt, xb, yt], fill=c2); d.line([xa, yt - 1, xb, yt - 1], fill=c1); d.line([xa, yt + 1, xb, yt + 1], fill=c3)
        for xf in range(xa + 18, xb - 4, 36):
            d.rectangle([xf, yt - 2, xf + 1, yt + 2], fill=c3); d.point((xf, yt - 2), fill=c1)
    for (xt, yt) in ((300, 9), (304, 12)):
        d.line([xt + 3, yt, xt + 3, 62], fill=c2); d.line([xt + 2, yt, xt + 2, 62], fill=c1); d.line([xt + 4, yt, xt + 4, 62], fill=c3)
    # --- Raíces que cuelgan de la losa (las que se mecen se dibujan en cada cuadro).
    raices_quietas = [(6, 26, 1), (16, 18, 21), (28, 34, 22), (58, 14, 2), (94, 24, 3), (122, 30, 4), (132, 12, 23), (164, 8, 24), (198, 12, 5), (212, 18, 25), (286, 20, 6), (296, 10, 26), (336, 26, 7), (352, 14, 27), (392, 34, 8), (400, 22, 28), (408, 16, 9)]
    for (x, l, s) in raices_quietas:
        raiz(d, camino_raiz(x, 7, l, s), P["raiz"], P["raiz_luz"], 2)
        if l > 18:
            raiz(d, camino_raiz(x + 1, 7 + l * 0.6, l // 3, s + 40, 0.3), P["raiz"])
    meciendo = [(146, 22, 11), (232, 16, 12), (372, 19, 13)]
    # --- Paneles de vidrio grabado.
    trazos1 = ([[(44, 14), (44, 54)], [(70, 14), (70, 54)], [(108, 14), (108, 54)]] +
               [[(36, 52 - k * 14), (120, 30 - k * 14)] for k in range(3)] + [[(36, 24), (120, 50)]] +
               [arco(122, 62, 50, 196, 262, 30), arco(30, 70, 62, 292, 336, 30)])
    polis1 = vidrio_grabado(im, 36, 14, 121, 54, P, trazos1, 1)
    trazos2 = ([[(312 + k * 5, 15), (312 + k * 5, 54)] for k in range(0, 13, 2)] + [espiral(332, 38, 2, 14, 2.6)] +
               [arco(340, 34, 22, 140, 380, 50), [(312, 54), (368, 18)]])
    polis2 = vidrio_grabado(im, 312, 15, 369, 54, P, trazos2, 2)
    pulsos = [(p_, largo_poli(p_)) for p_ in polis1 + polis2 if len(p_) > 1]
    for (x, l, s_) in ((50, 9, 31), (88, 13, 32), (116, 7, 33), (322, 11, 34), (360, 8, 35)):     # raíces sobre el riel
        raiz(d, camino_raiz(x, 8, l, s_), P["raiz"], P["raiz_luz"], 2)
    for x in range(0, W, 3):                                      # musgo donde la losa toca el muro
        if (x * 7) % 11 < 4: punto(px, x, 8 + (x % 2), P["musgo"])
    # --- Consolas oscuras (con la pantalla redonda del radar en la primera).
    pil_a, pant_a = consola_oscura(im, 136, 24, 168, 62, P, 3)
    pil_b, pant_b = consola_oscura(im, 170, 34, 196, 62, P, 4)
    rx, ry_, rr = 152, 30, 6                                     # radar sobre la primera consola
    d.rectangle([rx - 9, ry_ - 8, rx + 9, ry_ + 8], fill=P["oscura"][1])
    d.ellipse([rx - rr - 1, ry_ - rr - 1, rx + rr + 1, ry_ + rr + 1], fill=P["cromo"] if not o else P["oscura"][0])
    d.ellipse([rx - rr, ry_ - rr, rx + rr, ry_ + rr], fill=P["pantalla"])
    d.ellipse([rx - rr // 2, ry_ - rr // 2, rx + rr // 2, ry_ + rr // 2], outline=hx("1F3A2C"))
    d.line([rx - rr, ry_, rx + rr, ry_], fill=hx("1F3A2C")); d.line([rx, ry_ - rr, rx, ry_ + rr], fill=hx("1F3A2C"))
    # Pantallas de tubo de las consolas, encima de lo que dibujó consola_oscura (la primera queda bajo el radar).
    pant_a = (139, 44, 10, 4)
    d.rectangle([137, 42, 151, 49], fill=P["oscura"][2]); d.rectangle([138, 43, 150, 48], fill=P["pantalla"])
    # --- La consola blanca curva.
    pil_c = consola_blanca(im, 202, 296, 42, P)
    # --- Lámparas de cúpula.
    lamparas = [lampara(im, 126, 34, P, 28), lampara(im, 302, 26, P, 36), lampara(im, 380, 36, P, 26)]
    # --- Cajas de equipo apiladas (a la izquierda y a la derecha).
    cajas(im, 2, 62, P, [(24, 8, 0), (20, 7, 2), (14, 6, 6)], 1)
    cajas(im, 388, 62, P, [(22, 8, 0), (18, 7, 2), (12, 6, 5)], 2)
    # --- El piso: losas de hormigón con manchas, la franja de peligro y los cables gruesos.
    p1, p2, p3 = P["piso"]
    for y in range(63, H):
        for x in range(W):
            v = (y - 63) / (H - 63)
            px[x, y] = p3 if v < 0.15 else (p2 if v < 0.5 else p1)
    humedad(im, 21, 24, 64, H, P["mancha"], 0.35, r=(5, 16))
    for y in (68, 74):
        d.line([0, y, W, y], fill=P["piso_junta"])
    for k in range(-10, 22):
        xb = 206 + (k - 6) * 34; xt = 206 + (xb - 206) * 0.4
        d.line([xt, 63, xb, H], fill=P["piso_junta"])
    for x in range(0, W):                                       # la franja de peligro al pie de la base, gastada
        for y in (64, 65):
            if (x * 5 + y * 3) % 17 == 0: continue
            punto(px, x, y, P["peligro"][0] if ((x + y) // 3) % 2 == 0 else P["peligro"][1])
    d.line([0, 63, W, 63], fill=P["oscura"][2])
    # Sombras de lo que está de pie, sobre el piso (la luz viene de arriba, de las lámparas).
    for (xa, xb) in ((2, 27), (36, 121), (136, 196), (200, 298), (312, 369), (372, 394)):
        for y in (66, 67):
            for x in range(xa + 2, xb - 1):
                if tramar(0.55 if y == 66 else 0.3, x, y): px[x, y] = mezcla(px[x, y], P["sombra"], 0.6)
    cable(d, [(140, 62), (150, 69), (180, 71), (226, 69), (260, 72), (300, 70)], P["cable"], P["cable_luz"])
    cable(d, [(172, 62), (168, 66), (120, 73), (70, 70), (30, 76), (0, 75)], P["cable"], P["cable_luz"])
    cable(d, [(330, 62), (336, 67), (360, 70), (412, 68)], P["cable"], P["cable_luz"])
    # --- La luz: las lámparas iluminan el muro y el piso; de noche, el vidrio alumbra de verde lo que tiene cerca.
    for (lx, ly) in lamparas:
        luz(im, lx, ly, 18 if not o else 22, P["luz_lampara"], 0.22 if not o else 0.3, 4, ry=14)
        luz(im, lx, 66, 16, P["luz_lampara"], 0.18 if not o else 0.22, 3, ry=4)
    if o:
        for (x0, x1) in ((36, 121), (312, 369)):
            luz(im, (x0 + x1) / 2, 40, (x1 - x0) * 0.8, hx("2E7A66"), 0.22, 4, ry=34)
            luz(im, (x0 + x1) / 2, 66, (x1 - x0) * 0.7, hx("3E9A80"), 0.3, 3, ry=5)
        luz(im, 249, 44, 40, hx("2E5A50"), 0.18, 3, ry=8)          # el tablero de la consola blanca
    # Las cúpulas encendidas se repintan encima del halo.
    for (lx, ly) in lamparas:
        d.pieslice([lx - 5, ly - 5, lx + 4, ly + 4], 180, 360, fill=P["cupula"][0])
        d.line([lx - 5, ly - 1, lx + 4, ly - 1], fill=P["cupula"][2] if o else P["cupula"][1])
    granular(im, 31, 0.035 if not o else 0.05, 0.14)
    big = subir(im)
    # ------------------------------------------------------------------
    # Lo que se mueve.
    # ------------------------------------------------------------------
    pilotos = pil_a + pil_b + pil_c
    rnd = random.Random(17)
    textos = [[rnd.randint(2, 9) for _ in range(12)] for _ in range(2)]
    escaner = {}

    def capa_escaner(q):
        """El haz del escáner que entra por la rendija: estrecho arriba, se abre hacia el piso; tres niveles de luz."""
        if q not in escaner:
            gx_ = RENDIJA - 30 + 66 * q / (ESCANER["estados"] - 1)
            m = np.zeros((H, W), np.uint8)
            for y in range(8, 72):
                f = (y - 7) / 64
                xc = RENDIJA + (gx_ - RENDIJA) * f
                w = 0.8 + 6.0 * f
                for x in range(int(xc - w) - 1, int(xc + w) + 2):
                    if 0 <= x < W:
                        dd = abs(x + 0.5 - xc) / w
                        if dd < 1: m[y, x] = 2 if dd < 0.45 else 1
            for y in range(66, 74):                             # la mancha en el piso
                for x in range(int(gx_ - 10), int(gx_ + 11)):
                    if 0 <= x < W and ((x - gx_) / 10) ** 2 + ((y - 70) / 3.4) ** 2 < 1: m[y, x] = max(m[y, x], 3 if abs(x - gx_) < 5 else 2)
            alfa = np.array([0, 34, 62, 90], np.uint8)[m]
            capa = Image.new("RGBA", (W, H), P["escaner"] + (0,)); capa.putalpha(Image.fromarray(alfa))
            escaner[q] = capa.resize((W * ESC, H * ESC), Image.NEAREST)
        return escaner[q]

    def vivo(img, tick, delante):
        if delante: return
        tk = tick % BUCLE
        f_ = tk / BUCLE
        if ESCANER["desde"] <= f_ < ESCANER["hasta"]:           # una vez por bucle pasa el escáner de la patrulla
            q = int((f_ - ESCANER["desde"]) / (ESCANER["hasta"] - ESCANER["desde"]) * ESCANER["estados"])
            img.paste(Image.alpha_composite(img.convert("RGBA"), capa_escaner(q)).convert("RGB"))
        dd = ImageDraw.Draw(img)
        if ESCANER["desde"] <= f_ < ESCANER["hasta"]:
            cuad(dd, RENDIJA, 6, P["escaner"], 2, 2)
        # El pulso de luz que corre por cada trazo del vidrio (una o dos vueltas exactas por bucle).
        for j, (pts, acum) in enumerate(pulsos):
            vueltas = 1 + (j % 2)
            s = (tk / BUCLE * vueltas + j * 0.37) % 1 * acum[-1]
            for k_, col in ((0, hx("FFFFFF")), (1.5, P["canto"]), (3, P["trazo"])):
                x, y = en_poli(pts, acum, s - k_)
                cuad(dd, round(x), round(y), col, 1, 1)
        # Pilotos.
        for (x, y, col, per, ph) in pilotos:
            if ((tk + ph) // per) % 2 == 0:
                cuad(dd, x, y, col, 1, 1)
        # El barrido del radar: dos vueltas por bucle, con su estela y un eco que se enciende al pasar.
        for k_, col in ((0, P["texto"]), (1, hx("4E8A60")), (2, hx("2C5A40"))):
            a = 2 * math.pi * (tk / BUCLE * 2) - k_ * 0.35
            for r in range(1, rr):
                cuad(dd, rx + 0.5 + r * math.cos(a) - 0.5, ry_ + 0.5 + r * math.sin(a) - 0.5, col, 1, 1)
        if (tk // 10) % 8 in (2, 3):
            cuad(dd, rx + 3, ry_ - 2, hx("D9FFE0"), 1, 1)
        # Texto que corre en las pantallas de tubo.
        for i_, (x0, y0, w, h) in enumerate((pant_a, pant_b)):
            for fila in range(min(h, 3)):
                n = textos[i_][(fila + tk // 20) % len(textos[i_])]
                cuad(dd, x0, y0 + fila * 1, P["texto"] if fila < 2 else hx("4E8A60"), min(w, n), 0.66)
        # Raíces que mece el aire de la ventilación.
        for (x, l, s) in meciendo:
            pts = camino_raiz(x, 7, l, s)
            mec = math.sin(2 * math.pi * (tk / BUCLE * 2) + s)
            pts = [(px_ + mec * 1.6 * ((py_ - 7) / (l * 1.6)) ** 1.5, py_) for px_, py_ in pts]
            for (xa, ya), (xb, yb) in zip(pts, pts[1:]):
                cuad(dd, round(xb), round(yb), P["raiz"], 1, 1)
            for (xa, ya) in pts[:len(pts) // 2]:
                cuad(dd, round(xa) + 1, round(ya), P["raiz"], 1, 1)
    ANIM[("frente", o)] = [vivo]
    return big


FONDOS_MIOS = {"frente": fondo_frente}
ETIQ = {"frente": "Frente exterior · la base escondida en ruinas"}
NOCHE = {"frente": "la base en penumbra, con el vidrio y las lámparas encendidos"}


def registrar():
    for n, f in FONDOS_MIOS.items():
        g8.FONDOS[n] = f
        g9.FONDOS9[n] = f
        if not any(b[0] == n for b in g8.BANDAS):
            g8.BANDAS.append((n, False, ETIQ[n]))
        g9.ETIQ[n] = ETIQ[n]
        g9.NOCHE[n] = NOCHE[n]


# =====================================================================
#  Revisión: hojas del gato, fondos y bandas
# =====================================================================

def vista_fondos(archivo, temas=MIOS, ticks=(0, 40, 90)):
    """Cada fondo (claro y oscuro) con el gato caminando y sentado, en varios momentos del bucle, a ×3."""
    filas = []
    for n in temas:
        for o in (False, True):
            t = tema(n, o)
            f = FONDOS_MIOS[n](t)
            for k in ticks:
                img = g8.cuadro_banda(n, o, t, f, ("caminar", 2, 60.0 + k, 1, 0.0), k, None)
                img = g8.cuadro_banda(n, o, t, img, ("sentado", 0, 250.0, -1, 0.0), k, None)
                filas.append(img)
    hoja = Image.new("RGB", (W * ESC, len(filas) * (H * ESC + 6)), (250, 249, 246))
    for k, f in enumerate(filas):
        hoja.paste(f, (0, k * (H * ESC + 6)))
    hoja.save(AQUI / archivo)


def hoja_mia(archivo, cel=3, temas=MIOS, comparar=("distopia", "vapor", "espacio")):
    """Las poses comunes y las dos acciones propias, claro y oscuro; abajo, los temas con que se compara."""
    lado = N * cel
    filas = [(n, o, True) for n in temas for o in (False, True)] + [(n, False, False) for n in comparar]
    ancho = 130 + 10 * (lado + 4)
    alto_f = lambda propia: (3 * lado + 20) if propia else (lado + 12)
    img = Image.new("RGB", (ancho, sum(alto_f(p) for _, _, p in filas)), (250, 249, 246)); d = ImageDraw.Draw(img)
    y = 0
    for n, o, propia in filas:
        t = tema(n, o)
        d.rectangle([0, y, ancho, y + alto_f(propia) - 4], fill=t["fondo"])
        d.text((8, y + 6), f"{n} {'oscuro' if o else 'claro'}", font=fuente("DejaVuSans-Bold.ttf", 14), fill=t["texto"])
        x = 130
        for a, i in (("caminar", 1), ("caminar", 5), ("girar", 0), ("girar", 1), ("jugar", 2), ("enojado", 0), ("quieto_triste", 0), ("sentado", 0), ("dormir", 0), ("asearse", 2)):
            s = g8.sprite(n, o, t, a, i, False)
            img.paste(s, (x, y + 4), s); x += lado + 4
        if propia:
            for j in range(2):
                acc = g17.ESP[n][j][0]
                for i in range(g17.ACCIONES[acc][1]):
                    s = g8.sprite(n, o, t, f"esp_{n}_{j}", i, False)
                    img.paste(s, (130 + i * (lado + 4), y + (1 + j) * lado + 8), s)
        y += alto_f(propia)
    img.save(AQUI / archivo)


registrar()

if __name__ == "__main__":
    que = sys.argv[1:2] or ["hoja"]
    sel = [a for a in sys.argv[2:] if a in MIOS] or list(MIOS)
    if "hoja" in que:
        hoja_mia("gato21_hoja.png", temas=sel)
    if "fondos" in que:
        vista_fondos("gato21_fondos.png", temas=sel)
    if "bandas" in que:
        for n in sel:
            g8.gif_banda(g17.secuencia(n), [(n, False, ETIQ[n])], "_21")
            g8.gif_banda(g17.secuencia(n), [(n, True, ETIQ[n].split(" · ")[0] + " · de noche: " + NOCHE[n])], "_21_oscuro")
    print("listo")
