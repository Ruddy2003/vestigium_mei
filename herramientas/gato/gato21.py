"""
Lote 10: los gatos de los cinco temas nuevos. Primera tanda: Frente exterior.

- Frente exterior (`frente`): gato gris pizarra pintado como un cuadro de concepto de los años 70 (gouache sobre cartón:
  manchas de color opaco con la huella del pincel, luz cálida que entra por arriba y rebote frío abajo, grano del
  cartón). Chaqueta de vuelo de cuero con cuello de borrego, puños y cintura de punto y un parche en la manga; en la
  oreja, un auricular de baquelita con su micrófono y un piloto ámbar. En el collar, la cápsula donde esconde el
  microfilm. Fondo: el puerto industrial de ladrillo (el desguace con su grúa, la torre de vigía, el almacén de bóvedas,
  los tanques) y en el cielo la nave patrulla del Directorio, un bloque largo con un anillo (nunca un triángulo); de
  noche, el mismo puerto con las ventanas encendidas y los reflectores que barren, el de la torre y el de la nave. Lo
  que se mueve: el humo, el reflector y las luces de la nave. Esconde un microfilm en el collar y se agacha cuando
  pasa el reflector de una patrulla.

Los otros cuatro temas del lote 10 (`arcontes`, `galia`, `nouveau`, `psicodelia`) se agregan igual: su rama en
`tema_mio`, sus colores en `colores_obj`, sus dos acciones en `ESP_MIOS`, su fondo en `FONDOS_MIOS` (y `ETIQ`, `NOCHE`)
y su nombre en `MIOS`; o en un archivo `gato21b.py` que importe este y registre los suyos de la misma forma.

Colores de la guía del tema (docs/diseno/temas/frente.md): hueso, pizarra, ámbar de piloto, rojo de botón, el verde del
radar y el mármol con bronce del Directorio. Importa la cadena (gato18, gato19 y los gato20*) y agrega sus temas sin
tocar los demás.

Uso: python3 gato21.py hoja|fondos|bandas [tema…]   (lo deja en salida/, que no se sube)
"""
import importlib
import math
import random
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw

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
    cartón. Lo que es luz (el piloto, el reflector, los brillos) no lleva textura."""
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
    a[..., :3] *= np.where(tex, 1 + 0.06 * pin, 1.0)[..., None]
    out = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8), "RGBA").resize((N * cel, filas * cel), Image.LANCZOS)
    o = np.asarray(out).astype(np.float32)
    # Grano del cartón: puntos sueltos un poco más claros u oscuros (dos niveles, para que el atlas pese poco).
    rng = np.random.default_rng(5)
    gr = rng.random(o.shape[:2])
    o[..., :3] *= np.where(gr < 0.09, 0.94, np.where(gr > 0.93, 1.05, 1.0))[..., None]
    return Image.fromarray(np.clip(o, 0, 255).astype(np.uint8), "RGBA")


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
        ("frente", 1): dict(U=hx("3A2E26"), V=AMBAR_C, X=CROMO, Q=hx("FFF4D2")),        # el haz del reflector
    }[(nombre, j)]
    if oscuro and nombre == "frente" and j == 1:
        c["Q"] = hx("F4E6B4")
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
        # frente (guía): hueso E4DED1 / pizarra 23252A, ámbar B85C0F / F2A33A, rojo D9372B, radar 9FD8A8, baquelita 2A2622,
        # cromo B9B6AE · oscuro: 121518, E7E1D4, F0A04B
        P = hx("6E7780")
        cuero, cuero2 = hx("8F5229"), hx("5E3318")
        col = g8.pelaje(P, PIZARRA, S=hx("525A63"), L=hx("A7AEB3"), W=hx("646D76"), D=hx("4C535B"), T=hx("5A626B"),
                        E=AMBAR_C, N=hx("B98A86"), R=hx("8E7F84"), C=BAQUELITA, G=CROMO, A=cuero, B=cuero2,
                        Y=hx("E8DFC9"), M=BAQUELITA, Z=CROMO, O=ROJO, J=AMBAR)
        col["F"] = hx("33302D")                 # punto de puños y cintura
        col["I"] = AMBAR_C                      # el parche de misión
        col["H"] = hx("FBF4E4")
        t = g8.T(col, HUESO if not oscuro else hx("121518"), PIZARRA if not oscuro else hx("E7E1D4"),
                 hx("A69E8E") if not oscuro else hx("3A4249"), "ninguno", "gouache", ("vuelo", "auricular"),
                 luz=hx("FFEFD2"), k_luz=0.34, rebote=hx("8FA4AA"), k_reb=0.2, carton=hx("A8906A"))
        t["sombra_col"], t["sombra_a"] = (52, 44, 40), 62
        if oscuro:
            nc = g9.de_noche(col, hx("1E3540"), 0.12)
            nc.update(O=hx("FF5A4A"), I=hx("E39A3C"), F=hx("26282A"), Y=hx("C9C2B2"))
            t["col"] = nc
            t.update(luz=hx("F0A04B"), k_luz=0.28, rebote=hx("4E8A80"), k_reb=0.24, carton=hx("5A4C3A"))
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
#  Las dos acciones: esconde el microfilm en el collar · se agacha cuando pasa el reflector
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
# (cuánto se agacha, dónde cae el haz en el suelo). El haz entra por detrás y pasa por encima.
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


def o_reflector(L, a):
    """El haz del reflector de la patrulla, que baja desde arriba (detrás) y pasa por encima del gato. Va tramado,
    como luz: sobre el gato deja puntos claros, y en el suelo, la mancha de luz."""
    bx, gy = a["obj"]
    sx, sy = bx - 16.0, -6.0
    vistos = set()
    for r in range(0, 61):
        f = (r - sy) / (gy - sy)
        xc = sx + (bx - sx) * f
        w = 1.6 + 6.2 * f
        for c in range(int(xc - w) - 1, int(xc + w) + 2):
            if not (0 <= c < N): continue
            d = abs(c + 0.5 - xc) / w
            if d >= 1: continue
            if (d < 0.5 and (r + c) % 2 == 0) or (r % 2 == 0 and c % 2 == 0):
                vistos.add((r, c))
    for r in range(int(gy) - 3, int(gy) + 2):                       # la mancha de luz en el suelo
        for c in range(int(bx - 10), int(bx + 11)):
            if 0 <= c < N and 0 <= r < N and ((c + 0.5 - bx) / 9.5) ** 2 + ((r + 0.5 - gy + 0.6) / 2.4) ** 2 < 1:
                if (r + c) % 2 == 0 or abs(c + 0.5 - bx) < 4.5:
                    vistos.add((r, c))
    for r, c in vistos:
        rect(L, "f_Q", c, r, c + 1, r + 1)


ESP_MIOS = {
    "frente": [("microfilm", o_microfilm, "esconde un microfilm en el collar"),
               ("agacharse", o_reflector, "se agacha cuando pasa el reflector de una patrulla")],
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
    """El grano del cartón en el fondo: pixeles sueltos un poco más claros u oscuros (a 1 pixel por dp)."""
    px = im.load(); rnd = random.Random(semilla)
    for y in range(y0, y1):
        for x in range(x0, x1):
            q = rnd.random()
            if q < densidad / 2:
                px[x, y] = esc(px[x, y], 1 - fuerza)
            elif q < densidad:
                px[x, y] = esc(px[x, y], 1 + fuerza)


def vetas(im, semilla, n, y0, y1, col, k=0.35, x0=0, x1=W, largo=(18, 70)):
    """Pinceladas largas y horizontales de calima en el cielo (bordes deshilachados)."""
    px = im.load(); rnd = random.Random(semilla)
    for _ in range(n):
        y = rnd.randint(y0, y1); x = rnd.randint(x0 - 20, x1); l = rnd.randint(*largo)
        for j in range(l):
            borde = min(j, l - j)
            if borde < 5 and rnd.random() > borde / 5: continue
            xx = x + j
            if x0 <= xx < x1:
                px[xx, y] = mezcla(px[xx, y], col, k)
                if j % 3 and rnd.random() < 0.4 and 0 <= y + 1 < H:
                    px[xx, y + 1] = mezcla(px[xx, y + 1], col, k * 0.5)


def cilindro(im, x0, x1, y0, y1, tonos, bandas=None, cada=7):
    """Cuerpo cilíndrico con la luz por la izquierda: tonos de claro a oscuro por columnas, con anillos."""
    px = im.load(); w = x1 - x0; n = len(tonos)
    perfil_ = [0.5, 0.15, 0.0, 0.05, 0.2, 0.35, 0.5, 0.62, 0.74, 0.85, 0.95, 1.0]
    for i in range(w):
        f = perfil_[min(len(perfil_) - 1, int(i / w * len(perfil_)))]
        col = tonos[min(n - 1, int(f * n))]
        for y in range(y0, y1):
            punto(px, x0 + i, y, col)
        if bandas:
            for y in range(y0 + 3, y1, cada):
                punto(px, x0 + i, y, bandas[min(len(bandas) - 1, int(f * len(bandas)))])


def ladrillo(im, x0, y0, x1, y1, pal, semilla=0, luz_x=None):
    """Muro de ladrillo de 1 dp por hilada (pixel art): ladrillos de 4 dp, juntas, ladrillos sueltos más oscuros o
    quemados, y la mancha de hollín que baja desde arriba."""
    px = im.load(); rnd = random.Random(semilla)
    base, oscuro, junta, quemado = pal
    for y in range(int(y0), int(y1)):
        fila = y - int(y0)
        for x in range(int(x0), int(x1)):
            if not (0 <= x < W and 0 <= y < H): continue
            if fila % 2 == 1:
                col = junta if rnd.random() < 0.7 else base
            elif (x + (fila // 2) * 2) % 4 == 0:
                col = junta
            else:
                q = ((x + (fila // 2) * 2) // 4 * 7 + fila * 5) % 13
                col = quemado if q == 0 else (oscuro if q in (3, 8) else base)
            px[x, y] = col


def chorreo(im, x, y0, largo, col, k=0.3):
    """Manchas que chorrean por la pared (hollín u óxido), más claras abajo."""
    px = im.load()
    for j in range(largo):
        f = k * (1 - j / largo)
        for dx in (0, 1) if j < largo * 0.6 else (0,):
            punto(px, x + dx, y0 + j, mezcla(px[x + dx, y0 + j] if 0 <= x + dx < W and 0 <= y0 + j < H else col, col, f))


def bocanadas(d, tk, x, y, cols, semilla, n=5, vida=40, alto=24, deriva=16, r0=1.2, r1=4.2):
    """Humo en bocanadas: nace chico y oscuro en la boca, sube, crece, se corre con el viento y se aclara. Cada
    bocanada lleva su lado de luz (arriba a la izquierda). Se repite cada `vida` cuadros (divide a BUCLE)."""
    for k in range(n):
        f = ((tk + k * vida / n + semilla * 7) % vida) / vida
        r = r0 + (r1 - r0) * f
        cx, cy = x + deriva * f ** 1.4 + math.sin(f * 5 + semilla) * 1.2, y - alto * f
        col = cols[min(len(cols) - 2, int(f * (len(cols) - 1)))]
        caja = lambda dx_, dy_, rr: [round((cx + dx_ - rr) * ESC), round((cy + dy_ - rr * 0.8) * ESC),
                                     round((cx + dx_ + rr) * ESC), round((cy + dy_ + rr * 0.8) * ESC)]
        d.ellipse(caja(0, 0, r), fill=col)
        if r > 2:
            d.ellipse(caja(-r * 0.25, -r * 0.25, r * 0.55), fill=cols[-1] if f < 0.6 else col)


PAL_D = dict(
    cielo=[hx("B7BCB6"), hx("C6C8BF"), hx("D3D2C6"), hx("DFDACA"), hx("E8DFC9"), hx("ECDDBC")],
    veta=hx("F1EADA"), veta2=hx("ABB0AB"),
    planeta=(hx("E7E4DA"), hx("D8D7CF"), hx("C9CBC6"), hx("BFC2BE")),
    lejos=(hx("B2B2AA"), hx("C2C0B5"), hx("A3A49F")), lejos2=(hx("9EA09D"), hx("B0B0A9"), hx("8E918F")),
    acero=(hx("7E858A"), hx("5E666C"), hx("444B51")), acero_l=hx("A6ACAD"),
    casco=(hx("D7CDB6"), hx("BFB49B"), hx("9A8F78"), hx("6E6656")), oxido=hx("9C5E38"), interior=hx("3E3833"),
    ladr=(hx("8E4B35"), hx("7A3F2D"), hx("A9846F"), hx("5E3426")), ladr_s=(hx("6E3A2A"), hx("5E3224"), hx("86624F"), hx("4A281E")),
    boveda=(hx("D3CBB8"), hx("BDB4A0"), hx("9C9482"), hx("7C7566")),
    hueso=(hx("DCD4C2"), hx("C4BBA7"), hx("A79E8B"), hx("8A8272")),
    tanque=(hx("DAD2BF"), hx("C8BFAB"), hx("B0A792"), hx("958C78"), hx("7A7262")), anillo=(hx("A39A86"), hx("8A8270"), hx("6E6656")),
    vidrio=hx("3C4A50"), vidrio_b=hx("8FA3A6"), puerta=(hx("6B6E6C"), hx("575A59")),
    peligro=(AMBAR_C, hx("2A2622")), piloto=AMBAR_C, rojo=ROJO, radar=hx("9FD8A8"), pantalla=hx("0F1A16"),
    contenedor=(hx("B98A44"), hx("9E7034"), hx("7E5626"), hx("5E3E1A")),
    suelo=(hx("C9C1AE"), hx("BCB4A1"), hx("ADA591"), hx("9A927F")), junta=hx("A39B88"), mancha=hx("8F887A"),
    sombra_suelo=hx("A49C8B"),
    nave=(hx("F3F1EC"), hx("DDD9D0"), hx("C3C0B9"), hx("A9A7A4")), bronce=(hx("C29C62"), hx("9A7442"), hx("6E5230")),
    nave_v=hx("5A6066"), farol=hx("4A4E50"),
)
PAL_N = dict(
    cielo=[hx("0C1013"), hx("10161A"), hx("141D22"), hx("19252B"), hx("1F2E34"), hx("25363B")],
    veta=hx("2C3E44"), veta2=hx("0E1316"),
    planeta=(hx("CBD0C6"), hx("A9B0A9"), hx("2A363B"), hx("202C31")),
    lejos=(hx("1B2529"), hx("223035"), hx("172024")), lejos2=(hx("161E22"), hx("1D282C"), hx("12191C")),
    acero=(hx("3A4146"), hx("2A3034"), hx("1C2125")), acero_l=hx("56605F"),
    casco=(hx("5E5A50"), hx("4A463E"), hx("36332D"), hx("24221F")), oxido=hx("4E3020"), interior=hx("141210"),
    ladr=(hx("4A2A20"), hx("3E231B"), hx("2E2420"), hx("2C1812")), ladr_s=(hx("3A2019"), hx("301A14"), hx("261E1A"), hx("22130E")),
    boveda=(hx("4C4B46"), hx("3C3B37"), hx("2E2D2A"), hx("22221F")),
    hueso=(hx("595650"), hx("46443F"), hx("36352F"), hx("282723")),
    tanque=(hx("54534D"), hx("46453F"), hx("3A3934"), hx("2E2D29"), hx("23221F")), anillo=(hx("34332E"), hx("2A2925"), hx("201F1C")),
    vidrio=hx("141C1F"), vidrio_b=hx("2E3E42"), puerta=(hx("2A2C2B"), hx("202221")),
    peligro=(hx("A86E24"), hx("1A1715")), piloto=AMBAR_C, rojo=hx("FF5A4A"), radar=hx("9FD8A8"), pantalla=hx("0F1A16"),
    contenedor=(hx("5E4628"), hx("4C381F"), hx("3A2A17"), hx("2A1E10")),
    suelo=(hx("2E3234"), hx("2A2E30"), hx("26292B"), hx("212426")), junta=hx("1C1F21"), mancha=hx("1A1C1D"),
    sombra_suelo=hx("1E2123"),
    nave=(hx("9BA09B"), hx("6A706E"), hx("454B4C"), hx("2C3234")), bronce=(hx("8E7448"), hx("6A5434"), hx("44361F")),
    nave_v=hx("F2D9A0"), farol=hx("2A2D2E"),
)
VENTANA_N = hx("F0A04B")


def anillo(d, cx, cy, rx, ry, ang, cols, frente):
    """Un anillo visto de sesgo (elipse inclinada `ang` grados), en tres filas de pixeles: la de afuera con la luz
    arriba, la de en medio y la de adentro en sombra. Solo la mitad de adelante (frente=True) o la de atrás."""
    a = math.radians(ang); ca, sa = math.cos(a), math.sin(a)
    for t in np.linspace(0, 2 * math.pi, 360):
        if (math.cos(t) < 0) != frente:
            continue
        for k, dr in enumerate((0.0, 1.0, 2.0)):
            x, y = (rx - dr) * math.cos(t), (ry - dr) * math.sin(t)
            col = cols[0] if (k == 0 and math.sin(t) < 0.2) else cols[min(2, k + (0 if frente else 1))]
            d.point((round(cx + x * ca - y * sa), round(cy + x * sa + y * ca)), fill=col)


def nave(im, x0, y0, P, o):
    """La nave patrulla del Directorio: un bloque largo de mármol con chaflanes, el puente arriba adelante, la fila
    de ventanillas, las toberas atrás y el anillo de bronce que lo ciñe, visto de sesgo. Devuelve sus luces."""
    d = ImageDraw.Draw(im)
    claro, medio, sombra, borde = P["nave"]
    b1, b2, b3 = P["bronce"]
    L_, A_ = 84, 11
    rcx, rcy, rx, ry, ang = x0 + 30, y0 + A_ / 2, 6, 10.5, 12
    anillo(d, rcx, rcy, rx, ry, ang, (b1, b2, b3), False)          # la mitad de atrás, detrás del casco
    for yy in (y0 + 2, y0 + 5, y0 + 8):                              # toberas
        d.rectangle([x0 - 3, yy, x0, yy + 1], fill=borde)
    casco = [(x0 + 3, y0), (x0 + L_ - 6, y0), (x0 + L_, y0 + 4), (x0 + L_, y0 + 7), (x0 + L_ - 4, y0 + A_), (x0 + 2, y0 + A_), (x0, y0 + A_ - 2), (x0, y0 + 2)]
    d.polygon(casco, fill=medio)
    d.polygon([(x0 + 3, y0), (x0 + L_ - 6, y0), (x0 + L_ - 4, y0 + 2), (x0 + 1, y0 + 2)], fill=claro)
    d.polygon([(x0, y0 + A_ - 3), (x0 + L_, y0 + A_ - 4), (x0 + L_ - 4, y0 + A_), (x0 + 2, y0 + A_)], fill=sombra)
    d.line([x0 + 2, y0 + A_, x0 + L_ - 4, y0 + A_], fill=borde)
    d.line([x0 + L_ - 6, y0, x0 + L_, y0 + 4], fill=claro)          # el chaflán de proa, con luz
    for xx in range(x0 + 7, x0 + L_ - 6, 8):                        # juntas de las placas
        d.line([xx, y0 + 3, xx, y0 + A_ - 4], fill=sombra)
    for xx in range(x0 + 40, x0 + L_ - 8, 3):                       # ventanillas
        d.point((xx, y0 + 5), fill=P["nave_v"] if (not o or (xx // 3) % 4) else P["nave"][3])
    d.line([x0 + 4, y0 + 7, x0 + L_ - 4, y0 + 7], fill=b2)          # el filete de bronce del régimen
    d.rectangle([x0 + 56, y0 - 3, x0 + 72, y0], fill=medio)         # el puente, con su ventanal
    d.line([x0 + 56, y0 - 3, x0 + 71, y0 - 3], fill=claro)
    d.line([x0 + 59, y0 - 2, x0 + 70, y0 - 2], fill=P["nave_v"] if o else P["vidrio"])
    d.line([x0 + 63, y0 - 7, x0 + 63, y0 - 4], fill=borde)          # antena
    d.rectangle([x0 + 44, y0 + A_ + 1, x0 + 50, y0 + A_ + 2], fill=borde)   # la góndola del reflector
    anillo(d, rcx, rcy, rx, ry, ang, (b1, b2, b3), True)            # la mitad de adelante, encima del casco
    luces = [(x0 + 63, y0 - 8, "rojo"), (rcx - 2.7, rcy - ry + 0.6, "ambar"), (rcx + 2.7, rcy + ry - 0.6, "ambar"),
             (x0 + L_ - 1, y0 + 5, "rojo"), (x0 - 3, y0 + 5, "blanco"), (x0 + 47, y0 + A_ + 3, "foco")]
    return luces


def grua(im, x0, x1, top, base, P):
    """El pórtico del desguace: dos patas de celosía y la viga de arriba, con su carro y el cable."""
    d = ImageDraw.Draw(im)
    a1, a2, a3 = P["acero"]
    for x in (x0, x1 - 4):
        d.rectangle([x, top, x + 3, base], fill=a2)
        d.line([x, top, x, base], fill=P["acero_l"])
        d.line([x + 3, top, x + 3, base], fill=a3)
        for y in range(top + 4, base - 2, 6):                    # celosía
            d.line([x + 1, y, x + 2, y + 3], fill=a3)
    d.rectangle([x0 - 2, top - 4, x1 + 2, top], fill=a2)
    d.line([x0 - 2, top - 4, x1 + 2, top - 4], fill=P["acero_l"])
    d.line([x0 - 2, top, x1 + 2, top], fill=a3)
    for x in range(x0, x1, 5):
        d.line([x, top - 3, x + 4, top - 1], fill=a3)
    d.rectangle([x1 - 2, top - 8, x1 + 6, top - 4], fill=a1)      # la cabina del gruista
    d.point((x1 + 4, top - 7), fill=P["vidrio_b"])
    return (x0 + (x1 - x0) * 0.58, top)


def casco_desguace(im, x0, y0, x1, y1, P, o):
    """El carguero que desarman: casco de placas hueso con óxido, la proa redonda a la derecha, una parte sin placas
    donde se ven las cuadernas, sobre sus picaderos."""
    d = ImageDraw.Draw(im); px = im.load()
    c1, c2, c3, c4 = P["casco"]
    h = y1 - y0
    for x in range(x0, x1):
        f = (x - x0) / (x1 - x0)
        nariz = max(0.0, (f - 0.82) / 0.18)
        arriba = y0 + round(h * 0.5 * nariz ** 2)
        abajo = y1 - round(h * 0.35 * nariz ** 1.5)
        for y in range(arriba, abajo):
            v = (y - y0) / h
            col = c1 if v < 0.18 else (c2 if v < 0.55 else (c3 if v < 0.85 else c4))
            punto(px, x, y, col)
    for y in range(y0 + 4, y1 - 2, 4):                           # juntas de las placas
        for x in range(x0, int(x0 + (x1 - x0) * 0.86)):
            if (x * 3 + y) % 11: punto(px, x, y, c3 if y < y0 + h * 0.55 else c4)
    rnd = random.Random(4)
    for _ in range(26):                                          # óxido
        x, y = rnd.randint(x0 + 2, x1 - 12), rnd.randint(y0 + 2, y1 - 3)
        d.rectangle([x, y, x + rnd.randint(0, 2), y + rnd.randint(0, 3)], fill=P["oxido"])
    hx0, hx1 = x0 + 28, x0 + 50                                   # sin placas: interior y cuadernas
    d.rectangle([hx0, y0 + 2, hx1, y1 - 3], fill=P["interior"])
    for x in range(hx0 + 1, hx1, 3):
        d.line([x, y0 + 2, x, y1 - 3], fill=c3)
        punto(px, x, y0 + 2, c1)
    d.line([hx0, y0 + h // 2, hx1, y0 + h // 2], fill=c4)
    d.line([x0, y0, x1 - 8, y0], fill=c1)
    for x in (x0 + 8, x0 + 30, x0 + 56, x0 + 70):               # picaderos
        d.rectangle([x, y1, x + 5, y1 + 3], fill=P["acero"][2])
    for x in range(x0 + 4, x0 + 20, 3):                          # andamio
        d.line([x, y0 - 6, x, y1], fill=P["acero"][1]) if x % 2 else None
    d.line([x0 + 4, y0 - 6, x0 + 20, y0 - 6], fill=P["acero"][1]); d.line([x0 + 4, y0 + 6, x0 + 20, y0 + 6], fill=P["acero"][1])
    return (hx1 + 1, y0 + 9)


def fondo_frente(t):
    """Frente exterior. De día: el puerto industrial bajo un cielo de calima color hueso con un planeta pálido; a la
    izquierda el desguace (el pórtico y el carguero sin placas), la torre de vigía, el almacén de ladrillo con bóvedas
    y su chimenea de escape, los tanques y la plataforma de concreto con su raya de peligro; en el cielo, la nave
    patrulla del Directorio. De noche: el mismo puerto en penumbra, ventanas ámbar, faroles, la pantalla verde de la
    torre, el reflector de la torre que barre el cielo y el de la nave que barre la plataforma."""
    o = t["oscuro"]
    P = PAL_N if o else PAL_D
    im, d = lienzo(P["cielo"][-1])
    degradado(im, 0, 54, P["cielo"])
    px = im.load()
    # --- Cielo con vida: calima en pinceladas, el planeta, (de noche) estrellas.
    if o:
        estrellas(im, 41, 70, 0, 34, [hx("5E6E70"), hx("8A9894"), hx("C9CFC6")])
    else:
        luz(im, 24, 6, 40, hx("F4ECDA"), 0.5, 4, ry=18)          # el sol, tapado por la calima, arriba a la izquierda
    vetas(im, 3, 16, 3, 44, P["veta"], 0.4 if not o else 0.5)
    vetas(im, 9, 8, 6, 40, P["veta2"], 0.25, largo=(12, 40))
    pcx, pcy, pr = 362, 17, 15
    p1, p2, p3, p4 = P["planeta"]
    for y in range(pcy - pr, pcy + pr + 1):
        for x in range(pcx - pr, pcx + pr + 1):
            dd = math.hypot(x - pcx, y - pcy) / pr
            if dd > 1: continue
            lado = (x - pcx + 0.35 * (y - pcy)) / pr               # la luz entra por la izquierda
            f = (lado + 1) / 2
            i = 0 if f < 0.32 else (1 if f < 0.55 else (2 if f < 0.78 else 3))
            if 0 < i < 3 and tramar((f * 4) % 1, x, y): i += 1
            col = (p1, p2, p3, p4)[min(3, i)]
            if not o:
                col = mezcla(col, px[x, y], 0.35)
            px[x, y] = col
    for (mx, my, mr) in ((356, 12, 3), (365, 22, 2), (352, 21, 2), (370, 10, 1)):   # mares
        for y in range(my - mr, my + mr + 1):
            for x in range(mx - mr, mx + mr + 1):
                if (x - mx) ** 2 + (y - my) ** 2 <= mr * mr and math.hypot(x - pcx, y - pcy) < pr - 1:
                    px[x, y] = esc(px[x, y], 0.94 if not o else 0.85)
    vetas(im, 17, 4, pcy - 4, pcy + 8, P["veta"], 0.45, x0=330, x1=400, largo=(20, 50))   # la calima cruza el planeta
    # --- La nave patrulla.
    nx0, ny0 = 170, 7
    luces_nave = nave(im, nx0, ny0, P, o)
    # --- Lejos: dos filas de siluetas industriales (torres, grúas, chimeneas), con su lado de luz.
    rnd = random.Random(8)
    for fila, (base, pal, alto) in enumerate(((50, P["lejos"], (6, 16)), (55, P["lejos2"], (3, 10)))):
        x = -rnd.randint(0, 8)
        while x < W:
            w_ = rnd.randint(8, 20); h_ = rnd.randint(*alto)
            d.rectangle([x, base - h_, x + w_, base + 6], fill=pal[0])
            d.line([x, base - h_, x, base + 6], fill=pal[1])
            d.line([x + w_, base - h_, x + w_, base + 6], fill=pal[2])
            q = rnd.random()
            if q < 0.3:                                          # chimenea fina
                cx_ = x + rnd.randint(2, w_ - 2); d.rectangle([cx_, base - h_ - rnd.randint(6, 14), cx_ + 1, base - h_], fill=pal[0])
            elif q < 0.45 and fila == 0:                         # grúa lejana
                d.line([x + 3, base - h_, x + 3, base - h_ - 14], fill=pal[2]); d.line([x - 6, base - h_ - 13, x + 16, base - h_ - 13], fill=pal[2])
            elif q < 0.6:                                        # tanque redondo
                d.ellipse([x + 2, base - h_ - 4, x + w_ - 2, base - h_ + 4], fill=pal[0])
            if o and rnd.random() < 0.5:
                d.point((x + rnd.randint(1, max(1, w_ - 1)), base - rnd.randint(1, max(1, h_ - 1))), fill=hx("C08040"))
            x += w_ + rnd.randint(0, 6)
    # --- Plataforma (primero, para que las cosas de pie le echen sombra encima).
    s1, s2, s3, s4 = P["suelo"]
    for y in range(62, H):
        for x in range(W):
            v = (y - 62) / (H - 62)
            col = s1 if v > 0.55 else (s2 if v > 0.2 else s3)
            px[x, y] = col
    rnd = random.Random(12)
    for _ in range(46):                                          # manchas de aceite y desgaste
        x, y = rnd.randrange(W), rnd.randrange(64, H)
        w_ = rnd.randint(2, 7)
        d.ellipse([x - w_, y - 1, x + w_, y + 1], fill=P["mancha"] if rnd.random() < 0.3 else s4)
    for y in (66, 71, 77):                                       # juntas de las losas, en perspectiva
        d.line([0, y, W, y], fill=P["junta"])
    for k in range(-12, 24):
        xb = 206 + (k - 6) * 30
        xt = 206 + (xb - 206) * 0.28
        d.line([xt, 62, xb, H], fill=P["junta"])
    for x in range(0, W, 9):                                     # la raya de peligro, gastada
        for j in range(5):
            if (x * 7 + j * 3) % 13 != 0:
                punto(px, x + j, 68, P["peligro"][0])
                if j % 2: punto(px, x + j, 69, P["peligro"][0])
    d.line([0, 62, W, 62], fill=P["acero"][2])                   # el bordillo de la plataforma
    d.line([0, 63, W, 63], fill=P["acero"][1])
    # --- El desguace: el pórtico y el carguero.
    soldadura = casco_desguace(im, 40, 36, 124, 58, P, o)
    gancho = grua(im, 34, 128, 14, 62, P)
    gx, gy = gancho
    d.rectangle([gx - 3, gy, gx + 3, gy + 2], fill=P["acero"][1])
    d.line([gx, gy + 2, gx, 30], fill=P["acero"][2])             # el cable con una placa colgando
    d.rectangle([gx - 5, 30, gx + 5, 33], fill=P["casco"][1]); d.line([gx - 5, 33, gx + 5, 33], fill=P["casco"][3])
    # --- La torre de vigía.
    tx = 142
    cilindro(im, tx - 3, tx + 4, 13, 62, P["hueso"], P["anillo"], 9)
    d.rectangle([tx - 7, 6, tx + 8, 13], fill=P["hueso"][1])
    d.line([tx - 7, 6, tx + 8, 6], fill=P["hueso"][0]); d.line([tx - 7, 6, tx - 7, 13], fill=P["hueso"][0])
    d.line([tx + 8, 6, tx + 8, 13], fill=P["hueso"][3]); d.line([tx - 7, 13, tx + 8, 13], fill=P["hueso"][3])
    d.rectangle([tx - 5, 8, tx + 6, 10], fill=P["pantalla"] if o else P["vidrio"])
    if o:
        for xx in range(tx - 4, tx + 6, 3): d.point((xx, 9), fill=P["radar"])
        luz(im, tx + 0.5, 9, 9, hx("4E8A60"), 0.25, 3, ry=5)
    else:
        d.point((tx - 4, 8), fill=P["vidrio_b"])
    d.line([tx, 6, tx, 1], fill=P["acero"][2])                    # antena
    # --- El almacén de ladrillo con bóvedas de cañón.
    ax0, ax1, atop = 156, 334, 31
    ladrillo(im, ax0, atop, ax1, 62, P["ladr"], 3)
    for x in range(ax1 - 22, ax1):                               # el lado de la sombra (la luz viene de la izquierda)
        for y in range(atop, 62):
            px[x, y] = mezcla(px[x, y], P["ladr_s"][0], 0.55)
    bahias = [(ax0, ax0 + 59), (ax0 + 59, ax0 + 118), (ax0 + 118, ax1)]
    b1_, b2_, b3_, b4_ = P["boveda"]
    for (bx0, bx1) in bahias:
        cx_, hw = (bx0 + bx1) / 2, (bx1 - bx0) / 2
        for x in range(bx0, bx1 + 1):
            u = (x - cx_) / hw
            h_ = 8 * math.sqrt(max(0.0, 1 - u * u))
            for y in range(int(atop - h_), atop + 1):
                col = b1_ if u < -0.35 else (b2_ if u < 0.25 else (b3_ if u < 0.7 else b4_))
                if (x - bx0) % 4 == 0: col = mezcla(col, b4_, 0.5)      # costillas
                punto(px, x, y, col)
            punto(px, x, int(atop - h_), b1_ if u < 0.3 else b2_)
        d.line([bx0, atop, bx1, atop], fill=b4_)
    d.line([ax0, atop + 1, ax1, atop + 1], fill=P["ladr"][3])
    ventanas = []
    for k, x in enumerate(range(ax0 + 9, ax1 - 6, 13)):          # ojos de buey
        if 226 <= x <= 268: continue
        y = 39
        d.ellipse([x - 3, y - 3, x + 3, y + 3], fill=P["hueso"][1])
        d.ellipse([x - 2, y - 2, x + 2, y + 2], fill=P["vidrio"])
        ventanas.append((x, y))
        if not o:
            d.point((x - 1, y - 1), fill=P["vidrio_b"])
        chorreo(im, x, y + 4, 9 + (k * 5) % 7, P["ladr"][3], 0.4)
    # El portón del hangar, con su marco de peligro y la lámpara.
    dx0, dx1, dy0 = 230, 266, 42
    d.rectangle([dx0 - 2, dy0 - 2, dx1 + 2, 62], fill=P["peligro"][1])
    for y in range(dy0 - 2, 62):
        for x in (dx0 - 2, dx0 - 1, dx1 + 1, dx1 + 2):
            if (y // 2) % 2: punto(px, x, y, P["peligro"][0])
    for x in range(dx0 - 2, dx1 + 3):
        if ((x + 1) // 2) % 2: punto(px, x, dy0 - 2, P["peligro"][0])
    for y in range(dy0, 62):
        d.line([dx0, y, dx1, y], fill=P["puerta"][0] if y % 3 else P["puerta"][1])
    d.rectangle([dx0 + 3, 56, dx0 + 14, 62], fill=P["interior"])             # el portón, un poco abierto
    d.rectangle([246, dy0 - 6, 250, dy0 - 4], fill=P["farol"])
    # Tubos por la pared.
    d.line([ax0, 57, dx0 - 3, 57], fill=P["hueso"][1]); d.line([ax0, 58, dx0 - 3, 58], fill=P["hueso"][3])
    for x in range(ax0 + 6, dx0 - 3, 14): d.rectangle([x, 56, x + 1, 59], fill=P["hueso"][2])
    d.line([dx1 + 6, 34, dx1 + 6, 62], fill=P["hueso"][1]); d.line([dx1 + 7, 34, dx1 + 7, 62], fill=P["hueso"][3])
    # La chimenea de escape (cilindro con anillos) y un respiradero.
    sx = 300
    cilindro(im, sx - 4, sx + 5, 8, atop - 2, P["hueso"], P["anillo"], 6)
    d.rectangle([sx - 5, 6, sx + 6, 8], fill=P["acero"][1])
    for y in (10, 11): d.line([sx - 4, y, sx + 4, y], fill=P["rojo"] if not o else hx("7A2A22"))
    vx = 322
    d.rectangle([vx - 2, 19, vx + 3, 24], fill=P["acero"][1]); d.line([vx - 3, 18, vx + 4, 18], fill=P["acero"][2])
    # --- Los tanques.
    for (x0, x1) in ((338, 361), (364, 389)):
        cilindro(im, x0, x1, 38, 62, P["tanque"], P["anillo"], 7)
        cx_ = (x0 + x1) / 2
        for x in range(x0, x1):
            u = (x - cx_) / ((x1 - x0) / 2)
            h_ = 4 * math.sqrt(max(0.0, 1 - u * u))
            for y in range(int(38 - h_), 38):
                punto(px, x, y, P["tanque"][0] if u < -0.2 else (P["tanque"][2] if u < 0.5 else P["tanque"][4]))
        d.line([x1 - 4, 34, x1 - 4, 62], fill=P["acero"][2])    # escalera
        for y in range(36, 62, 2): d.point((x1 - 3, y), fill=P["acero"][2])
    d.line([334, 50, 338, 50], fill=P["hueso"][2]); d.line([361, 46, 364, 46], fill=P["hueso"][2])
    # --- Sombras arrojadas en la plataforma (la luz viene de arriba a la izquierda: caen a la derecha).
    if not o:
        for (x0, x1, largo) in ((34, 38, 22), (124, 128, 22), (139, 146, 26), (156, 334, 10), (338, 389, 12), (6, 28, 8), (398, 401, 16)):
            for y in range(63, 67):
                k_ = (y - 62) / 4
                for x in range(x0 + round(largo * k_ * 0.5), x1 + round(largo * k_)):
                    if 0 <= x < W: px[x, y] = mezcla(px[x, y], P["sombra_suelo"], 0.6)
    # --- Primer plano: a la izquierda el contenedor; a la derecha el farol con sus barriles.
    c1, c2, c3, c4 = P["contenedor"]
    d.rectangle([0, 42, 27, 62], fill=c2)
    for x in range(0, 27, 2): d.line([x, 43, x, 61], fill=c3)
    d.line([0, 42, 27, 42], fill=c1); d.line([27, 42, 27, 62], fill=c4); d.line([0, 62, 27, 62], fill=c4)
    for x in (4, 12): d.line([x, 44, x, 60], fill=c4); d.line([x + 1, 44, x + 1, 60], fill=c1)
    chorreo(im, 18, 43, 12, P["oxido"], 0.5); chorreo(im, 7, 43, 8, P["oxido"], 0.45)
    d.rectangle([2, 34, 16, 42], fill=P["hueso"][1]); d.line([2, 34, 16, 34], fill=P["hueso"][0]); d.line([16, 34, 16, 42], fill=P["hueso"][3])
    d.rectangle([5, 37, 12, 39], fill=P["peligro"][1]); d.line([6, 38, 11, 38], fill=P["peligro"][0])       # cartucho estampado
    fx = 399
    d.rectangle([fx, 22, fx + 1, 64], fill=P["farol"]); d.line([fx, 22, fx, 64], fill=P["acero_l"] if not o else P["acero"][1])
    d.rectangle([fx - 4, 20, fx + 2, 22], fill=P["farol"])
    lampara = (fx - 2, 23)
    d.rectangle([fx - 3, 23, fx, 24], fill=P["hueso"][1] if not o else hx("FFD9A0"))
    for (bx, by) in ((402, 56), (406, 58)):
        d.rectangle([bx, by, bx + 5, 64], fill=P["acero"][1]); d.line([bx, by, bx, 64], fill=P["acero_l"])
        d.line([bx, by + 2, bx + 5, by + 2], fill=P["acero"][2]); d.line([bx, by + 5, bx + 5, by + 5], fill=P["acero"][2])
    # --- La noche: lo encendido alumbra lo que tiene cerca.
    prendidas = []
    if o:
        for j, (x, y) in enumerate(ventanas):
            if j % 3 != 1:
                d.ellipse([x - 2, y - 2, x + 2, y + 2], fill=VENTANA_N); d.point((x - 1, y - 1), fill=hx("FFE0A8"))
                luz(im, x, y, 6, VENTANA_N, 0.18, 3)
                prendidas.append((x, y))
        luz(im, 248, dy0 - 3, 14, VENTANA_N, 0.3, 4, ry=9)                      # la lámpara del portón
        d.rectangle([246, dy0 - 4, 250, dy0 - 4], fill=hx("FFD9A0"))
        luz(im, dx0 + 8, 60, 12, VENTANA_N, 0.25, 3, ry=4)                       # la luz que sale por el portón
        for (lx, ly, r, ry) in ((lampara[0], lampara[1] + 2, 16, 12), (lampara[0], 70, 26, 7), (248, 67, 22, 5)):
            luz(im, lx, ly, r, VENTANA_N, 0.28, 4, ry=ry)
        luz(im, soldadura[0] - 8, 50, 10, hx("4E6A70"), 0.2, 3)                  # una luz de trabajo en el desguace
    else:
        luz(im, 248, dy0 - 3, 4, hx("FFE7B8"), 0.4, 2)
    granular(im, 31, 0.035 if not o else 0.05, 0.14)
    big = subir(im)
    # ------------------------------------------------------------------
    # Lo que se mueve.
    # ------------------------------------------------------------------
    humo_c = ((hx("8C8A84"), hx("A3A19A"), hx("B9B6AD"), hx("C9C5BA"), hx("D8D3C6")) if not o else
              (hx("2C3033"), hx("343A3D"), hx("3C4346"), hx("454C4F"), hx("4E5558")))
    vapor_c = ((hx("D9D6CE"), hx("E2DED5"), hx("EAE6DD"), hx("F2EEE5")) if not o else
               (hx("3A4245"), hx("424A4D"), hx("4A5255"), hx("566064")))
    # El radar de la torre gira (de día); el reflector de la torre barre el cielo (de noche).
    radar = []
    for q in range(8):
        h_ = im.copy(); dd = ImageDraw.Draw(h_)
        a = 2 * math.pi * q / 8
        w_ = round(4 * math.cos(a))
        dd.line([tx, 1, tx, 4], fill=P["acero"][2])
        if w_ >= 0:
            dd.rectangle([tx - w_ - 1, 0, tx + w_, 2], fill=P["hueso"][0] if not o else P["hueso"][1])
        else:
            dd.rectangle([tx + w_, 0, tx - w_ - 1, 2], fill=P["hueso"][2] if not o else P["hueso"][3])
        radar.append(subir(h_).crop(((tx - 6) * ESC, 0, (tx + 7) * ESC, 5 * ESC)))
    focos = {}

    def capa_haz(clave, x, y, ang, largo, ancho, mascara, piso=None):
        """El haz, pintado a 1 pixel por dp en tres niveles de luz, como el resto del fondo."""
        if clave not in focos:
            m = np.zeros((H, W), np.float32)
            yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
            vx, vy = xx - x, yy - y
            ux, uy = math.cos(ang), math.sin(ang)
            a_ = vx * ux + vy * uy
            b_ = np.abs(-vx * uy + vy * ux)
            w = 0.6 + a_ * ancho
            dentro = (a_ > 0) & (a_ < largo) & (b_ < w)
            m[dentro] = np.where(b_[dentro] < w[dentro] * 0.45, 2, 1)
            if piso is not None:
                gx_, gy_, rx_, ry_ = piso
                charco = ((xx - gx_) / rx_) ** 2 + ((yy - gy_) / ry_) ** 2 < 1
                m[charco] = np.maximum(m[charco], np.where(((xx - gx_) / rx_) ** 2 + ((yy - gy_) / ry_) ** 2 < 0.35, 3, 2)[charco])
            if mascara is not None:
                m *= mascara
            alfa = np.array([0, 40, 70, 96], np.uint8)[m.astype(int)]
            capa = Image.new("RGBA", (W, H), hx("F4E6B4") + (0,))
            capa.putalpha(Image.fromarray(alfa))
            focos[clave] = capa.resize((W * ESC, H * ESC), Image.NEAREST)
        return focos[clave]

    if o:
        ref = np.asarray(Image.new("RGB", (W, H)))
        cielo_ref = Image.new("RGB", (W, H)); degradado(cielo_ref, 0, 54, P["cielo"])
        es_cielo = ((np.abs(np.asarray(im).astype(int) - np.asarray(cielo_ref).astype(int)).sum(2) < 60) &
                    (np.arange(H)[:, None] < 52)).astype(np.float32)
    TORRE, NAVE_Q = 16, 32
    foco_nave = [l for l in luces_nave if l[2] == "foco"][0]

    def vivo(img, tick, delante):
        if delante: return
        tk = tick % BUCLE
        if o:                                                   # los reflectores
            base = img.convert("RGBA")
            q = int(tk * TORRE / BUCLE) % TORRE
            ang = math.radians(-128 + 46 * math.sin(2 * math.pi * q / TORRE))
            base = Image.alpha_composite(base, capa_haz(("t", q), tx + 0.5, 3, ang, 150, 0.075, es_cielo))
            q2 = int(tk * NAVE_Q / BUCLE) % NAVE_Q
            gx_ = 150 + 118 * math.sin(2 * math.pi * q2 / NAVE_Q)
            fx_, fy_ = foco_nave[0], foco_nave[1]
            ang2 = math.atan2(71 - fy_, gx_ - fx_)
            largo = math.hypot(gx_ - fx_, 71 - fy_)
            base = Image.alpha_composite(base, capa_haz(("n", q2), fx_, fy_, ang2, largo + 2, 0.12, None, (gx_, 71, 15, 4)))
            img.paste(base.convert("RGB"))
        dd = ImageDraw.Draw(img)
        if not o:
            img.paste(radar[(tk // 5) % 8], ((tx - 6) * ESC, 0))
        # Humo de la chimenea y vapor del respiradero.
        bocanadas(dd, tk, sx + 0.5, 4, humo_c, 0, n=6, vida=40, alto=14, deriva=26, r1=4.6)
        bocanadas(dd, tk, vx + 0.5, 16, vapor_c, 3, n=4, vida=32, alto=9, deriva=12, r0=0.8, r1=2.6)
        if o and (tk // 3) % 4:                                 # la llama de la antorcha de gas, en la boca
            cuad(dd, sx - 0.5, 4 + ((tk // 3) % 2) * 0.4, hx("F0A04B"), 2, 1)
            cuad(dd, sx, 3.4, hx("FFE0A8"), 1, 1)
        # Las luces de la nave.
        for j, (lx, ly, tipo) in enumerate(luces_nave):
            if tipo == "rojo" and (tk // 10 + j) % 2 == 0:
                cuad(dd, lx - 0.5, ly - 0.5, P["rojo"], 1, 1)
            elif tipo == "ambar" and (tk // 6 + j) % 4 != 0:
                cuad(dd, lx - 0.5, ly - 0.5, AMBAR_C, 1, 1)
            elif tipo == "blanco" and tk % 20 < 3:
                cuad(dd, lx - 1, ly - 0.5, hx("FFFFFF"), 2, 1)
            elif tipo == "foco":
                cuad(dd, lx - 1, ly - 0.5, hx("FFF0C8") if o else P["nave"][3], 2, 1)
        # Los pilotos de la torre.
        if (tk // 8) % 2 == 0:
            cuad(dd, tx - 0.5, 0, P["rojo"], 1, 1)
        # La chispa de la soldadura en el desguace (de día y de noche).
        sx_, sy_ = soldadura
        if (tk // 2) % 7 < 4:
            rnd_ = random.Random(tk // 2)
            cuad(dd, sx_, sy_, hx("FFF4C8"), 1, 1)
            for _ in range(3):
                cuad(dd, sx_ + rnd_.uniform(-2.5, 2.5), sy_ + rnd_.uniform(0.5, 4), hx("F2A33A") if rnd_.random() < 0.6 else hx("FFF4C8"), 1, 1)
            if o:
                cuad(dd, sx_ - 1, sy_ - 1, hx("8FB8C0"), 3, 3); cuad(dd, sx_, sy_, hx("FFFFFF"), 1, 1)
        if o:                                                   # una ventana que se apaga y se prende
            x_, y_ = prendidas[2]
            if (tk // 40) % 2:
                cuad(dd, x_ - 2, y_ - 2, P["vidrio"], 5, 5)
    ANIM[("frente", o)] = [vivo]
    return big


FONDOS_MIOS = {"frente": fondo_frente}
ETIQ = {"frente": "Frente exterior · el puerto de ladrillo y la patrulla"}
NOCHE = {"frente": "los reflectores que barren"}


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
