"""
Lote 10: los gatos de los cinco temas nuevos. Primera tanda: Frente exterior; segunda tanda: Arcontes.

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

- Arcontes (`arcontes`): gato negro con ojos de luz (el blanco violeta de la grieta), pintado a la encáustica como los
  retratos de El Fayum (cera caliente con pigmento: toques densos que siguen la forma, una rampa corta del negro cálido
  al ocre, el brillo de la cera donde le da la luz y la tabla cálida que se asoma en el contorno; de noche, un filo
  violeta de la grieta en el lomo). Al cuello, un cordón con el amuleto de Abraxas: una gema ovalada de jaspe verde con
  su grabado. Fondo: la orilla de Magdala y el lago de Galilea de día (el sol con su dorado de más, nubes con el filo
  dorado, la otra orilla en lomas de caliza con una aldea, el lago con su hondura y su reguero de oro, barcas de pesca
  con vela, la casa del salador con su tendedero de pescado, ánforas, sal y red, la barca varada y una palmera que
  enmarca); de noche, el mismo lago bajo el cielo antiguo, con las esferas apenas, la lámpara en la puerta, la barca
  que pesca con su lámpara y, muy fina, la grieta de luz blanca y violeta con su reflejo. Lo que se mueve: el agua
  (destellos, ondas, la espuma), la vela, la palmera, el polvo dorado, las estrellas, las llamas, la grieta que late
  apenas y, una vez por bucle, algo que pasa por el cielo y tapa las estrellas. Rasga el velo con la pata y por un
  momento se ve la luz de detrás (recurso del dibujo) · eriza el lomo ante una sombra que no se ve.

Los otros tres temas del lote 10 (`galia`, `nouveau`, `psicodelia`) se agregan igual: su rama en
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
MIOS = ("frente", "arcontes", "galia")
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
# Arcontes: la sombra que pasa (acción «eriza el lomo») va debajo de todo el gato. Es «prop» con la letra Q, así que no
# lleva contorno (el pintor de la encáustica la vuelve una mancha translúcida y le pone el contorno al gato donde la toca).
if "sombra21" not in [c[0] for c in g8.CAPAS]:
    g8.CAPAS.insert(0, ("sombra21", "Q", "prop", 0.4))

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

# ---------------------------------------------------------------------
#  Arcontes: un cordón de cuero con el amuleto de Abraxas, una gema chica y ovalada de jaspe verde (el grabado lo pone
#  el pintor de la encáustica, que sabe dónde quedó la gema en cada cuadro).
# ---------------------------------------------------------------------
g8.CON_COLLAR = tuple(g8.CON_COLLAR) + ("abraxas",)
_collar_abr = g8.collar


def collar_abraxas(L, C, cab, acc):
    if acc != "abraxas":
        return _collar_abr(L, C, cab, acc)
    a, b, px, py = g8._cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.56, a[1] + (b[1] - a[1]) * 0.56)
    e1, e2 = (m[0] - 4.4 * px, m[1] - 4.4 * py), (m[0] + 4.4 * px, m[1] + 4.4 * py)
    L.capsula("collar", [e1, e2], 1.1)
    j = max((e1, e2), key=lambda q: q[0] + q[1])
    x, y = j[0] - 0.5 * px, j[1] + 3.0
    L.capsula("collar", [(j[0] - 0.3 * px, j[1] + 0.4), (x, y - 1.6)], 0.8)          # el lazo que la cuelga
    L.elipse("joya", x, y, 2.0, 2.6)                                                  # la gema ovalada


g8.collar = collar_abraxas
g8._spr.clear(); g8._rej.clear()

# ---------------------------------------------------------------------
#  Galia: el torques de oro, un collar rígido y grueso, torcido como una cuerda y abierto por delante, con sus dos
#  remates de «tope» en la garganta (como los que llevan los bustos del caldero de Gundestrup). La torsión y el brillo
#  los pone el pintor del repujado.
# ---------------------------------------------------------------------
g8.CON_COLLAR = tuple(g8.CON_COLLAR) + ("torques",)
_collar_tor = g8.collar


def collar_torques(L, C, cab, acc):
    if acc != "torques":
        return _collar_tor(L, C, cab, acc)
    a, b, px, py = g8._cuello(C, cab)
    m = (a[0] + (b[0] - a[0]) * 0.5, a[1] + (b[1] - a[1]) * 0.5)
    e1, e2 = (m[0] - 4.7 * px, m[1] - 4.7 * py), (m[0] + 4.7 * px, m[1] + 4.7 * py)
    L.capsula("collar", [e1, e2], 2.2)
    j = max((e1, e2), key=lambda q: q[0] + q[1])          # la garganta: ahí se abre
    ux, uy = (b[0] - a[0]), (b[1] - a[1]); n = math.hypot(ux, uy) or 1; ux, uy = ux / n, uy / n
    s = 1 if (j is e2) else -1
    L.elipse("joya", j[0] + s * 0.7 * px - 0.5 * ux, j[1] + s * 0.7 * py - 0.5 * uy, 1.75, 1.75)   # el remate cercano
    L.elipse("joya", j[0] + s * 0.2 * px + 1.9 * ux, j[1] + s * 0.2 * py + 1.9 * uy, 1.25, 1.25)   # el otro, asoma


g8.collar = collar_torques

# ---------------------------------------------------------------------
#  Galia: el pelaje del gato montés europeo. Raya negra del lomo que acaba en la raíz de la cola, rayas finas y enteras en
#  los flancos, barras en las patas, cuatro rayas en la frente y la nuca y una en la mejilla; la cola gruesa, del mismo
#  ancho hasta el final, con anillos negros bien separados y la punta negra y roma. Para ponerlas en su lugar se guarda la
#  pose de cada cuadro (o qué vuelta es).
# ---------------------------------------------------------------------
_pose_gal = {}
_dib_gal, _giro_gal = g8.dibujar, g8.dibujar_giro
GIRO_COLA = {1: [(32, 50), (32.4, 44), (33.2, 38), (35.4, 32.5), (38.2, 30.5), (40, 32)],       # las de gato8.dibujar_giro
             0: [(23, 45), (19, 40.5), (17.5, 34), (18.5, 28), (21.5, 25.5), (23.6, 27.2)]}


def cola_gruesa(w):
    """La cola del montés: más gruesa y sin afinarse al final (la punta roma)."""
    k = 1.22 if max(w) < 6 else 1.08
    out = [x * k for x in w]
    out[-1] = max(out[-1], out[-2] * 0.97); out[-2] = max(out[-2], out[-3] * 0.98)
    return out


def dibujar_gal(ps, acc):
    if acc[0] == "torques":
        ps = dict(ps, colaw=cola_gruesa(ps["colaw"]))
    _pose_gal["ps"], _pose_gal["giro"] = ps, None
    return _dib_gal(ps, acc)


def dibujar_giro_gal(i, acc):
    _pose_gal["ps"], _pose_gal["giro"] = None, i
    if acc[0] != "torques":
        return _giro_gal(i, acc)
    antes = g8.ANCHO_COLA
    g8.ANCHO_COLA = cola_gruesa(antes)
    try:
        return _giro_gal(i, acc)
    finally:
        g8.ANCHO_COLA = antes


g8.dibujar, g8.dibujar_giro = dibujar_gal, dibujar_giro_gal
_acabar_gal = g8.acabar
COLA_ANILLOS = ((0.5, 0.56), (0.635, 0.695), (0.77, 0.825))      # a lo largo de la cola (0 = raíz, 1 = punta)
COLA_PUNTA = 0.905


def _rayas_montes(out, ch, parte):
    ps, giro = _pose_gal.get("ps"), _pose_gal.get("giro")
    cola = g8.catmull(ps["cola"]) if ps is not None else (g8.catmull(GIRO_COLA[giro]) if giro in GIRO_COLA else None)
    if cola is not None:
        C_ = np.array(cola, np.float32)
    for r in range(N):
        for c in range(N):
            p_ = parte[r][c]
            if out[r][c] not in "PSW" or p_ not in ("cola", "colafrente"):
                continue
            d2 = (C_[:, 0] - (c + 0.5)) ** 2 + (C_[:, 1] - (r + 0.5)) ** 2
            u = int(np.argmin(d2)) / (len(C_) - 1)
            if u >= COLA_PUNTA or any(a <= u < b for a, b in COLA_ANILLOS):
                out[r][c] = "F"
    if ps is None:
        for r in range(N):
            for c in range(N):
                if out[r][c] != "P":
                    continue
                p_ = parte[r][c]
                if giro == 1:                    # de espaldas: la raya del lomo, las rayas que bajan por los flancos y la nuca
                    if (p_ == "cuerpo" and ((31 <= c <= 32 and r >= 33) or (abs(c - 32) > 3 and (r + abs(c - 32) // 3) % 4 == 0))) or \
                            (p_ == "cabeza" and c in (30, 33) and 21 <= r <= 27):
                        out[r][c] = "W"
                elif giro == 0:                  # tres cuartos de espaldas: rayas en diagonal por el flanco
                    if p_ == "cuerpo" and r < 50 and (c + 0.45 * r) % 3.7 < 0.85:
                        out[r][c] = "W"
                    elif p_ == "pierna" and r % 4 == 1 and r < SUELO - 4:
                        out[r][c] = "W"
        return
    Hh, C, cab = ps["H"], ps["C"], ps["cab"]
    dx, dy = C[0] - Hh[0], C[1] - Hh[1]; largo = math.hypot(dx, dy) or 1
    ux, uy = dx / largo, dy / largo
    nx, ny = uy, -ux                                                     # hacia el lomo
    if ny > 0:
        nx, ny = -nx, -ny
    # La raya del lomo: las dos primeras celdas del cuerpo desde arriba, entre la cadera y la cruz.
    x0, x1 = min(Hh[0], C[0]) - ps["rh"] * 0.2, max(Hh[0], C[0]) + 1.0
    for c in range(N):
        if not (x0 <= c + 0.5 <= x1):
            continue
        hondo = 0
        for r in range(N):
            if parte[r][c] == "cuerpo" and out[r][c] in "PSL":
                hondo += 1
                if hondo <= 2 and out[r][c] == "P":
                    out[r][c] = "W"
                if hondo >= 2:
                    break
            elif hondo:
                break
    a = math.radians(ps["inc"]); ca, sa = math.cos(a), math.sin(a)
    for r in range(N):
        for c in range(N):
            if out[r][c] != "P":
                continue
            p_ = parte[r][c]
            x, y = c + 0.5, r + 0.5
            if p_ == "cuerpo":
                s_ = (x - Hh[0]) * ux + (y - Hh[1]) * uy
                v_ = (x - Hh[0]) * nx + (y - Hh[1]) * ny
                if -3.0 < s_ < largo + 2.5 and v_ > -3.6 and (s_ + 0.7 * math.sin(v_ * 0.8) + 0.6) % 3.7 < 0.85:
                    out[r][c] = "W"
            elif p_ == "pierna" and r % 4 == 1 and r < SUELO - 4:
                out[r][c] = "W"
            elif p_ == "cabeza":
                lx = (x - cab[0]) * ca + (y - cab[1]) * sa
                ly = -(x - cab[0]) * sa + (y - cab[1]) * ca
                if -6.5 < ly < -2.4 and any(abs(lx - k) < 0.55 for k in (-0.3, -2.3, -4.3)):
                    out[r][c] = "W"
                elif 1.0 < ly < 1.9 and -4.6 < lx < 0.4:
                    out[r][c] = "W"


def acabar_gal(ch, parte, estilo):
    if estilo[0] != "montes":
        return _acabar_gal(ch, parte, estilo)
    out = _acabar_gal(ch, parte, ("ninguno",) + tuple(estilo[1:]))
    out = [f[:] for f in out]
    _rayas_montes(out, ch, parte)
    return out


g8.acabar = acabar_gal
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

# =====================================================================
#  Material: encáustica (Arcontes), como los retratos de El Fayum: cera caliente con pigmento, puesta en toques densos
#  que siguen la forma, con el brillo de la cera donde le da la luz y la tabla cálida que se asoma en las orillas.
# =====================================================================
_toques = {}


def _corrido(m, dy, dx):
    """La máscara m corrida (dy, dx) pixeles, sin dar la vuelta por los bordes."""
    out = np.zeros_like(m)
    h, w = m.shape
    out[max(0, dy):h + min(0, dy), max(0, dx):w + min(0, dx)] = m[max(0, -dy):h + min(0, -dy), max(0, -dx):w + min(0, -dx)]
    return out


def _difuso(m, r):
    """Máscara (bool o 0..1) desenfocada con radio r, en 0..1."""
    im = Image.fromarray((np.clip(m, 0, 1) * 255).astype(np.uint8))
    return np.asarray(im.filter(ImageFilter.GaussianBlur(r)), np.float32) / 255


def toques_cera(alto, ancho, paso, semilla):
    """Dónde cae cada toque de cera: fijos en el cuadro (no hierven de un cuadro a otro); solo su dirección sigue la forma.
    Devuelve x, y, tono (-1 más oscuro, +1 más claro), largo y un giro chico de cada uno."""
    k = (alto, ancho, paso, semilla)
    if k not in _toques:
        rng = np.random.default_rng(semilla)
        ys, xs = np.mgrid[0:alto:paso, 0:ancho:paso].astype(np.float32)
        xs += rng.uniform(0, paso, xs.shape); ys += rng.uniform(0, paso, ys.shape)
        orden = rng.permutation(xs.size)
        _toques[k] = (xs.ravel()[orden], ys.ravel()[orden], rng.choice([-1, -1, 0, 1, 1], xs.size),
                      rng.uniform(0.75, 1.25, xs.size), rng.uniform(-0.3, 0.3, xs.size))
    return _toques[k]


def lum(c):
    return 0.3 * c[0] + 0.59 * c[1] + 0.11 * c[2]


def pintar_encaustica(g, t, cel):
    """Encáustica. El pelaje negro se pinta con una rampa corta de la cera (del negro cálido al ocre del brillo) según la
    luz que le toca: el volumen sale de la silueta desenfocada (como una almohada), la luz viene de arriba a la izquierda
    (el sol del fondo) y la cera brilla donde la forma la refleja. Encima, los toques del pincel: cortos, densos y a lo
    largo de la forma. El contorno es negro cálido y a ratos deja ver la tabla. Los ojos son de luz (con su halo); de
    noche, la luz de la grieta (arriba a la derecha) deja un filo violeta en el lomo. Lo que hace la luz detrás del velo
    (la letra Q clara) brilla y alumbra al gato; la sombra que pasa (la letra Q oscura) es una mancha translúcida."""
    col = t["col"]
    s = cel
    filas = len(g)
    A = np.array([list(f) for f in g])
    q_luz = lum(col["Q"]) > 140
    qm = A == "Q"
    if qm.any() and not q_luz:
        # La sombra no lleva contorno, así que el gato no lo tenía donde la toca: se le pone.
        cerca = np.zeros_like(qm)
        for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            cerca |= _corrido(qm, dy, dx)
        A = np.where(cerca & ~qm & (A != ".") & (A != "K"), "K", A)
    L = A.repeat(s, 0).repeat(s, 1)
    h, w = L.shape
    gato = (L != ".") & (L != "Q")
    # --- Volumen: normales de la silueta desenfocada.
    B = _difuso(gato, 2.2 * s)
    gy, gx = np.gradient(B)
    k_ = 7.5 * s
    nx, ny, nz = -gx * k_, -gy * k_, np.ones_like(B)
    nn = np.sqrt(nx * nx + ny * ny + nz * nz)
    nx, ny, nz = nx / nn, ny / nn, nz / nn
    luz_ = np.array(t["enc_luz"], np.float32); luz_ /= np.linalg.norm(luz_)
    dif = np.clip(nx * luz_[0] + ny * luz_[1] + nz * luz_[2], 0, 1)
    hh = luz_ + np.array([0, 0, 1.0]); hh /= np.linalg.norm(hh)
    brillo = np.clip(nx * hh[0] + ny * hh[1] + nz * hh[2], 0, 1) ** 26
    # --- Toques de cera a lo largo de la forma (perpendiculares a la pendiente del volumen).
    tq = Image.new("L", (w, h), 0); dt = ImageDraw.Draw(tq)
    xs, ys, tonos, largos, giros = toques_cera(h, w, max(2, int(1.25 * s)), 77)
    for x, y, tono, lg, gi in zip(xs, ys, tonos, largos, giros):
        xi, yi = int(x), int(y)
        if not (0 <= xi < w and 0 <= yi < h) or not gato[yi, xi]:
            continue
        ux, uy = -gy[yi, xi], gx[yi, xi]
        m = math.hypot(ux, uy)
        if m < 1e-3:
            ux, uy = 0.9, 0.42
        else:
            ux, uy = ux / m, uy / m
        ux, uy = ux * math.cos(gi) - uy * math.sin(gi), ux * math.sin(gi) + uy * math.cos(gi)
        l = 1.15 * s * lg
        dt.line([x - ux * l, y - uy * l, x + ux * l, y + uy * l], fill=int(tono) + 2, width=max(1, int(0.7 * s)))
    O = np.asarray(tq, np.float32)
    O = np.where(O > 0, O - 2, 0)
    # --- El pelaje: la rampa de la cera.
    base = {"P": 0.38, "L": 0.45, "W": 0.36, "S": 0.2, "D": 0.23, "T": 0.32, "R": 0.38}
    pelo = np.isin(L, list(base))
    tono = np.zeros((h, w), np.float32)
    for v, b in base.items():
        tono[L == v] = b
    lejos = np.isin(L, ["D", "S"])
    tono += np.where(lejos, 0.22, 0.42) * (dif - 0.55) + 0.06 * O
    tono += np.where(lejos, 0.15, 0.62) * brillo
    rampa = np.array(t["enc_rampa"], np.float32)
    idx = np.digitize(tono, [0.13, 0.25, 0.37, 0.49, 0.61, 0.75, 0.9])
    rgb = np.zeros((h, w, 3), np.float32)
    rgb[pelo] = rampa[idx[pelo]]
    # --- Lo demás: color plano con dos tonos de luz (la cera no deja nada del todo plano).
    for v in set(np.unique(L)) - set(base) - {".", "Q", "K"}:
        m = L == v
        c = np.array(col[v], np.float32)
        f = np.where(dif[m] > 0.62, 1.12, np.where(dif[m] < 0.4, 0.82, 1.0)) * (1 + 0.04 * O[m])
        rgb[m] = np.clip(c * f[:, None], 0, 255)
    # --- Contorno: negro cálido; a ratos se asoma la tabla. De noche, el filo de la grieta.
    km = L == "K"
    rgb[km] = t["enc_tinta"]
    rr, cc = np.mgrid[0:h, 0:w]
    asoma = km & (((rr // s) * 7 + (cc // s) * 13) % 11 == 0)
    rgb[asoma] = np.array(mezcla(t["enc_tinta"], t["enc_tabla"], 0.55), np.float32)
    if t.get("enc_filo"):
        lf = np.array(t["enc_filo_dir"], np.float32); lf /= np.linalg.norm(lf)
        ox, oy = -gx, -gy
        on = np.sqrt(ox * ox + oy * oy) + 1e-6
        cara = (ox / on) * lf[0] + (oy / on) * lf[1]
        anillo = gato & ~(np.asarray(Image.fromarray(gato.astype(np.uint8) * 255).filter(ImageFilter.MinFilter(2 * s + 1))) > 0)
        f1, f2 = t["enc_filo"]
        rgb[anillo & (cara > 0.62) & (on > 0.004)] = f1
        rgb[anillo & (cara > 0.86) & (on > 0.008)] = f2
    alfa = np.where(gato, 255.0, 0.0)
    # --- El amuleto: jaspe pulido (con su brillo) y el grabado de Abraxas (cabeza, cuerpo, brazos y las dos piernas de
    #     serpiente), un poco más claro, como se ve una talla en la piedra.
    gm = A == "G"
    if gm.sum() >= 6:
        r0, c0 = np.nonzero(gm)
        cy, cx = (r0.mean() + 0.5) * s, (c0.mean() + 0.5) * s
        gp = L == "G"
        jas = t["enc_jaspe"]
        rgb[gp] = jas[1]
        rgb[gp & (dif < 0.45)] = jas[0]
        rgb[gp & (dif > 0.7)] = jas[2]
        gr_ = Image.fromarray(np.zeros((h, w), np.uint8)); dg = ImageDraw.Draw(gr_)
        u = s / 3
        dg.point([(cx, cy - 3.6 * u)], fill=1)
        dg.line([cx, cy - 2.6 * u, cx, cy + 1.2 * u], fill=1)
        dg.line([cx - 1.6 * u, cy - 1.4 * u, cx + 1.6 * u, cy - 1.4 * u], fill=1)
        dg.line([cx, cy + 1.2 * u, cx - 1.8 * u, cy + 3.4 * u], fill=1)
        dg.line([cx, cy + 1.2 * u, cx + 1.8 * u, cy + 3.4 * u], fill=1)
        gra = (np.asarray(gr_) > 0) & gp
        rgb[gra] = jas[3]
        bri = gp & (rr == int(cy - 3.4 * u)) & (cc == int(cx - 1.8 * u))
        rgb[bri] = jas[4]
    # --- Ojos de luz: el halo sale un poco de la silueta.
    em = L == "E"
    if em.any():
        ho = _difuso(em, 1.2 * s) * t["enc_halo_ojo"]
        ho = np.round(np.clip(ho * 1.6, 0, 1) * 4) / 4 * t["enc_halo_ojo"]
        hc = np.array(t["enc_halo_col"], np.float32)
        fuera = ~gato & (ho > 0.05)
        dentro_ = gato & ~em & (L != "H") & (L != "K")
        rgb[dentro_] = rgb[dentro_] * (1 - ho[dentro_, None] * 0.7) + hc * ho[dentro_, None] * 0.7
        rgb[fuera] = hc
        alfa = np.where(fuera, np.maximum(alfa, ho * 200), alfa)
    # --- La luz detrás del velo, o la sombra que pasa.
    Qp = L == "Q"
    if Qp.any():
        qc = np.array(col["Q"], np.float32)
        if q_luz:
            b1, b2 = _difuso(Qp, 1.6 * s), _difuso(Qp, 4.5 * s)
            gl = np.clip(1.5 * b1 + 1.1 * b2, 0, 1)
            gl = np.round(gl * 6) / 6
            hal = np.array(t["enc_grieta_halo"], np.float32)
            gcol = hal[None, None, :] * (1 - b1[..., None]) + qc * b1[..., None]
            gcol = np.round(gcol / 12) * 12
            sobre = gato & ~Qp
            k2 = gl * 0.75
            rgb[sobre] = rgb[sobre] * (1 - k2[sobre, None]) + gcol[sobre] * k2[sobre, None]
            fuera = ~gato & ~Qp & (gl > 0.08)
            rgb[fuera] = gcol[fuera]
            alfa = np.where(fuera, np.maximum(alfa, gl * 0.85 * 255), alfa)
            rgb[Qp] = qc
            alfa = np.where(Qp, 255, alfa)
        else:
            b = _difuso(Qp, 2.0 * s)
            a_ = np.round(np.clip(b * 1.25, 0, 1) * t["enc_sombra_a"] * 6) / 6
            zona = ~gato & (a_ > 0.02)
            rgb[zona] = qc
            if t.get("enc_sombra_filo"):
                filo = zona & (b > 0.12) & (b < 0.4)
                rgb[filo] = t["enc_sombra_filo"]
            alfa = np.where(zona, a_ * 255, alfa)
    out = np.dstack([np.clip(rgb, 0, 255), np.clip(alfa, 0, 255)]).astype(np.uint8)
    return Image.fromarray(out, "RGBA")


# =====================================================================
#  Material: repujado de plata (Galia), a la manera del caldero de Gundestrup (recurso del dibujo: el caldero se halló
#  en Dinamarca y su técnica es tracia). La lámina se empuja desde atrás: el cuerpo es una almohada de metal, las rayas
#  son surcos cincelados y el pelo del lomo lleva el punteado del punzón; la panza, pulida. Lo pardo del montés es plata
#  con un baño de oro gastado; las rayas y la punta de la cola, plata ennegrecida (la pátina se queda en lo hondo).
# =====================================================================
def _rampa(cols, tono):
    """Índice de una rampa de colores (lista de RGB) para cada tono en 0..1."""
    r = np.array(cols, np.float32)
    i = np.clip((tono * len(cols)).astype(np.int32), 0, len(cols) - 1)
    return r[i], i


def pintar_repujado(g, t, cel):
    """Repujado de plata. El relieve sale de la silueta desenfocada (la almohada de la lámina), con las patas lejanas más
    hundidas, los surcos de las rayas y las uniones; con él se sacan las normales. El metal se pinta con una rampa corta:
    luz de la dirección del tema, el reflejo del cielo arriba y del suelo abajo, la pátina oscura en lo hondo y destellos
    duros. Encima, el punteado del punzón en el pelo pardo (un hoyo oscuro con su filo de luz abajo a la derecha) y la
    torsión del torques. Los ojos son de vidrio embutido. De noche, la luna deja un filo frío arriba y la hoguera uno
    cálido abajo. Los objetos de las acciones van en su color con su volumen."""
    col = t["col"]
    s = cel
    A = np.array([list(f) for f in g])
    L = A.repeat(s, 0).repeat(s, 1)
    h, w = L.shape
    hay = L != "."
    obj = np.isin(L, list("UVXQ"))
    metal = hay & ~obj
    # --- El relieve.
    peso = np.where(metal, 1.0, 0.0).astype(np.float32)
    peso[L == "D"] = 0.5
    peso[L == "K"] = 0.55
    hm = _difuso(peso, 2.0 * s) * 1.0
    surco = np.isin(L, ["W", "F"]).astype(np.float32)
    hm -= 0.07 * _difuso(surco, 0.5 * s)
    hm -= 0.08 * _difuso((L == "S").astype(np.float32), 0.6 * s)
    hm += 0.1 * _difuso(np.isin(L, ["C", "G"]).astype(np.float32), 0.6 * s)          # el torques sobresale
    gy, gx = np.gradient(hm)
    k_ = 7.0 * s
    nx, ny, nz = -gx * k_, -gy * k_, np.ones_like(hm)
    nn = np.sqrt(nx * nx + ny * ny + nz * nz)
    nx, ny, nz = nx / nn, ny / nn, nz / nn
    lv = np.array(t["rep_luz"], np.float32); lv /= np.linalg.norm(lv)
    dif = np.clip(nx * lv[0] + ny * lv[1] + nz * lv[2], 0, 1)
    hv = lv + np.array([0, 0, 1.0], np.float32); hv /= np.linalg.norm(hv)
    esp = np.clip(nx * hv[0] + ny * hv[1] + nz * hv[2], 0, 1) ** t["rep_exp"]
    env = np.clip(0.5 - 0.62 * ny, 0, 1)                                  # mira arriba: refleja el cielo
    cav = np.clip((_difuso(np.clip(hm, 0, 1), 1.3 * s) - hm) * 5.5, 0, 1)   # lo hondo, donde se queda la pátina
    tono = t.get("rep_base", 0.17) + 0.55 * dif + 0.3 * env - 0.42 * cav
    base = {"P": 0.0, "S": -0.1, "T": -0.02, "D": -0.2, "L": 0.12, "W": -0.26, "F": -0.2}
    for v, b in base.items():
        tono[L == v] += b
    rgb = np.zeros((h, w, 3), np.float32)
    # --- El pelo pardo: plata con el oro gastado; el punteado del punzón.
    pardo = np.isin(L, list("PSTDW"))
    yy, xx = np.mgrid[0:h, 0:w]
    hoyo = pardo & (yy % 4 == 1) & ((xx + 2 * ((yy // 4) % 2)) % 4 == 1) & ~np.isin(L, ["W"])
    hoyo &= _corrido(pardo, -1, -1) & _corrido(pardo, 1, 1)
    filo = _corrido(hoyo, 1, 1) & pardo
    tono = np.where(hoyo, tono - 0.24, tono)
    tono = np.where(filo, tono + 0.12, tono)
    c_, i_ = _rampa(t["rep_pardo"], tono)
    rgb[pardo] = c_[pardo]
    pl = L == "L"
    c_, _ = _rampa(t["rep_plata"], tono)
    rgb[pl] = c_[pl]
    ng = np.isin(L, ["F", "W"])
    c_, _ = _rampa(t["rep_negro"], tono + 0.15)
    rgb[ng] = c_[ng]
    # --- Destellos duros: el metal pulido devuelve la luz entera en un punto.
    pulido = np.isin(L, list("PSTLDF"))
    d1 = pulido & (esp > t["rep_umbral"][0])
    d2 = pulido & (esp > t["rep_umbral"][1]) & ~hoyo
    rgb[d1] = np.maximum(rgb[d1], np.array(t["rep_plata"][-1], np.float32) * 0.97)
    rgb[d2] = t["rep_destello"]
    # --- El torques: oro torcido (franjas en diagonal) con su luz arriba; los remates, lisos y brillantes.
    oro = np.array(t["rep_oro"], np.float32)
    cm = L == "C"
    tor = ((xx + yy) // 2) % 3
    io = np.clip((dif * 3.2 + env * 1.2 - 1.2).astype(np.int32) + np.where(tor == 0, -1, np.where(tor == 1, 1, 0)), 0, len(oro) - 1)
    rgb[cm] = oro[io[cm]]
    gm = L == "G"
    ig = np.clip((dif * 3.4 + env * 1.4 - 1.0).astype(np.int32), 0, len(oro) - 1)
    rgb[gm] = oro[ig[gm]]
    rgb[gm & (esp > 0.5)] = oro[-1]
    # --- Ojos de vidrio embutido; nariz y orejas por dentro de cobre; el resto de letras, en su color.
    for v in ("E", "N", "R", "H"):
        m = L == v
        if not m.any():
            continue
        cc = np.array(col[v], np.float32)
        f = np.where(dif[m] > 0.62, 1.12, np.where(dif[m] < 0.4, 0.8, 1.0))
        rgb[m] = np.clip(cc * f[:, None], 0, 255) if v != "H" else cc
    # --- El contorno: la pátina negra del borde de la lámina; donde da la luz, el filo del metal asoma.
    km = L == "K"
    rgb[km] = t["rep_tinta"]
    borde_luz = km & (dif > 0.8) & (env > 0.62)
    rgb[borde_luz] = t["rep_tinta_luz"]
    # --- Los objetos de las acciones: su color con su volumen (tres tonos y un brillo).
    if obj.any():
        ob = _difuso(obj.astype(np.float32), 1.0 * s)
        oy, ox = np.gradient(ob)
        on = np.sqrt((ox * 6 * s) ** 2 + (oy * 6 * s) ** 2 + 1)
        d_o = np.clip((-ox * 6 * s * lv[0] - oy * 6 * s * lv[1] + lv[2]) / on, 0, 1)
        for v in "UVX":
            m = L == v
            if not m.any():
                continue
            cc = np.array(col[v], np.float32)
            f = np.where(d_o[m] > 0.82, 1.22, np.where(d_o[m] > 0.62, 1.08, np.where(d_o[m] < 0.42, 0.74, 0.92)))
            rgb[m] = np.clip(cc * f[:, None], 0, 255)
        rgb[L == "Q"] = col["Q"]
    # --- De noche: el filo frío de la luna (arriba) y el cálido de la hoguera (abajo, del otro lado).
    if t.get("rep_filos"):
        B = _difuso(metal.astype(np.float32), 1.6 * s)
        by, bx = np.gradient(B)
        on_ = np.sqrt(bx * bx + by * by) + 1e-6
        dentro_ = metal & ~km
        anillo = dentro_ & ~(np.asarray(Image.fromarray(dentro_.astype(np.uint8) * 255).filter(ImageFilter.MinFilter(s + 2))) > 0)
        anillo &= ~np.isin(L, list("EHNCG"))
        for (dx_, dy_), c1, c2, u1, u2 in t["rep_filos"]:
            cara = (-bx / on_) * dx_ + (-by / on_) * dy_
            rgb[anillo & (cara > u1) & (on_ > 0.004)] = c1
            rgb[anillo & (cara > u2) & (on_ > 0.008)] = c2
    alfa = np.where(hay, 255, 0).astype(np.uint8)
    out = np.dstack([np.clip(rgb, 0, 255).astype(np.uint8), alfa])
    return Image.fromarray(out, "RGBA")


_pintar_prev = g8.pintar


def pintar(g, t, cel, oscuro=False):
    if t["material"] == "gouache":
        return pintar_gouache(g, t, cel)
    if t["material"] == "encaustica":
        return pintar_encaustica(g, t, cel)
    if t["material"] == "repujado":
        return pintar_repujado(g, t, cel)
    return _pintar_prev(g, t, cel, oscuro)


g8.pintar = pintar

# =====================================================================
#  El tema
# =====================================================================
HUESO, PIZARRA, AMBAR, AMBAR_C, ROJO = hx("E4DED1"), hx("23252A"), hx("B85C0F"), hx("F2A33A"), hx("D9372B")
BAQUELITA, CROMO = hx("2A2622"), hx("B9B6AE")
# Arcontes (docs/diseno/temas/arcontes.md, `TrazosArcontes`): la grieta y su halo, la sombra de arconte, el jaspe.
GRIETA, GRIETA_H, SOMBRA_A = hx("F4F1FF"), hx("9C7BFF"), hx("1A1424")


def colores_obj(nombre, oscuro, j):
    """U, V, X, Q de cada animación propia, de día y de noche."""
    c = {
        ("frente", 0): dict(U=hx("D08C3A"), V=hx("3A2E26"), X=CROMO, Q=hx("FFF6DC")),  # microfilm ámbar, cuadros, tapa, destello
        ("frente", 1): dict(U=hx("3A2E26"), V=AMBAR_C, X=CROMO, Q=hx("E4FFF4")),        # el haz del escáner
        ("arcontes", 0): dict(U=GRIETA_H, V=GRIETA_H, X=GRIETA_H, Q=GRIETA),          # la luz detrás del velo
        ("arcontes", 1): dict(U=SOMBRA_A, V=SOMBRA_A, X=SOMBRA_A, Q=SOMBRA_A),        # la sombra que pasa
        ("galia", 0): dict(U=hx("4E6B2E"), V=hx("6B4B2A"), X=hx("9DB560"), Q=hx("F2EEDC")),   # roble, rama, muérdago, bayas
        ("galia", 1): dict(U=hx("9A6A3A"), V=hx("4F7A78"), X=hx("A33A2A"), Q=hx("E2ECE0")),   # casco de bronce, río, crin
    }[(nombre, j)]
    if oscuro and nombre == "galia":
        c = [dict(U=hx("24331C"), V=hx("3A2C1C"), X=hx("5E7444"), Q=hx("C9CDB8")),
             dict(U=hx("6E4E2E"), V=hx("263A36"), X=hx("6E2C22"), Q=hx("B8C9B4"))][j]
    if oscuro and nombre == "frente" and j == 1:
        c["Q"] = hx("BFF5E0")
    if oscuro and nombre == "arcontes" and j == 1:
        c = {k: hx("050409") for k in "UVXQ"}
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
    elif nombre == "arcontes":
        # arcontes (guía): caliza EEE3C6 / tinta 1E1A2B, borde BBA673 · oscuro: cielo antiguo 0B0D1C, lino ECE4CF, 343A5A.
        # El gato: negro, pintado a la encáustica; la rampa de la cera va del negro cálido al ocre del brillo (de día) o al
        # gris violáceo (de noche). Los ojos, de luz: el blanco violeta de la grieta.
        P = hx("2A2226")
        col = g8.pelaje(P, hx("0B0709"), S=hx("1C161A"), L=hx("33292C"), W=P, D=hx("1E181C"), T=hx("261F23"),
                        E=hx("ECE6FF"), N=hx("5E3E44"), R=P, C=hx("4A321E"), G=hx("3E6B4E"), A=hx("4A321E"))
        t = g8.T(col, hx("EEE3C6") if not oscuro else hx("0B0D1C"), hx("1E1A2B") if not oscuro else hx("ECE4CF"),
                 hx("BBA673") if not oscuro else hx("343A5A"), "ninguno", "encaustica", ("abraxas", None))
        if not oscuro:
            t.update(enc_rampa=[hx(c) for c in ("0F0B0E", "181317", "211B20", "2D2529", "3E3333", "5D4D42", "97805C", "E6D4A2")],
                     enc_luz=(-0.46, -0.72, 0.52), enc_tinta=hx("0B0709"), enc_tabla=hx("7A5634"),
                     enc_jaspe=[hx(c) for c in ("2B4C38", "3E6B4E", "57876A", "A9CDB0", "E8F4E6")],
                     enc_halo_ojo=0.5, enc_halo_col=hx("B9A3FF"), enc_grieta_halo=GRIETA_H, enc_sombra_a=0.22,
                     enc_sombra_filo=None)
            t["sombra_col"], t["sombra_a"] = (58, 44, 30), 62
        else:
            t["col"] = dict(col, N=hx("4A3540"), C=hx("3A2818"), E=hx("F4F1FF"))
            t.update(enc_rampa=[hx(c) for c in ("0A0910", "121019", "1B1824", "262230", "342E3E", "4A4256", "766A92", "D2C8F4")],
                     enc_luz=(-0.46, -0.72, 0.52), enc_tinta=hx("06050A"), enc_tabla=hx("3A2A22"),
                     enc_jaspe=[hx(c) for c in ("203A2C", "2E5240", "467458", "8DB89A", "D8E8DA")],
                     enc_halo_ojo=0.75, enc_halo_col=GRIETA_H, enc_grieta_halo=GRIETA_H, enc_sombra_a=0.7,
                     enc_sombra_filo=hx("2A2244"),
                     enc_filo=(hx("44397A"), hx("8F7BDC")), enc_filo_dir=(0.42, -0.91))
            t["sombra_col"], t["sombra_a"] = (0, 0, 0), 80
    elif nombre == "galia":
        # galia (guía): barro y paja E6DFC6 / tinta 20251A, borde A89A6E · oscuro: el robledal 0F1410, E6E1CF, 3A4636.
        # El gato: montés pardo atigrado en repujado de plata (el pardo, plata con oro gastado; las rayas, plata
        # ennegrecida), con el torques de oro (C9962B, brillo F0D27A) y los ojos de vidrio verde amarillo.
        P = hx("857057")
        col = g8.pelaje(P, hx("15110D"), S=hx("645441"), L=hx("B6BABD"), W=hx("2E2720"), D=hx("473C2F"), T=hx("645441"),
                        E=hx("BFC24E"), N=hx("B0704E"), R=hx("A06A50"), C=hx("C9962B"), G=hx("F0D27A"))
        col["F"] = hx("1A1714")
        t = g8.T(col, hx("E6DFC6") if not oscuro else hx("0F1410"), hx("20251A") if not oscuro else hx("E6E1CF"),
                 hx("A89A6E") if not oscuro else hx("3A4636"), "montes", "repujado", ("torques", None))
        if not oscuro:
            t.update(rep_pardo=[hx(c) for c in ("1B1714", "2E2720", "473C2F", "645441", "857057", "A88F70", "CDB792", "EDE0C0")],
                     rep_plata=[hx(c) for c in ("1E1F20", "323436", "4C4F51", "6D7072", "919496", "B7B9B9", "DADBD8", "F4F4EF")],
                     rep_negro=[hx(c) for c in ("0F0D0C", "1A1714", "27221E", "3A332C")],
                     rep_oro=[hx(c) for c in ("4A3410", "7E590C", "A97A1E", "C9962B", "E2B54A", "F0D27A", "FFF0BE")],
                     rep_destello=hx("FFFFF6"), rep_tinta=hx("15110D"), rep_tinta_luz=hx("5E5345"),
                     rep_luz=(-0.5, -0.75, 0.45), rep_exp=28, rep_umbral=(0.6, 0.86), rep_filos=None)
            t["sombra_col"], t["sombra_a"] = (62, 52, 34), 62
        else:
            t["col"] = dict(col, E=hx("D8D45A"), N=hx("6E4632"), R=hx("5E4232"))
            t.update(rep_pardo=[hx(c) for c in ("0B0D0B", "151815", "20241E", "2E322A", "42453A", "5B5D4E", "7D7D69", "A9A88E")],
                     rep_plata=[hx(c) for c in ("0B0E0E", "161B1B", "232A29", "343C3A", "4A5350", "66706B", "8D9790", "BAC4BA")],
                     rep_negro=[hx(c) for c in ("060706", "0D0F0D", "151814", "1F231D")],
                     rep_oro=[hx(c) for c in ("2E2008", "4A3410", "6E4E14", "946A1E", "B88A30", "D9AE55", "F2D58E")],
                     rep_destello=hx("E9E6D6"), rep_tinta=hx("050605"), rep_tinta_luz=hx("3A4636"),
                     rep_luz=(0.4, -0.8, 0.45), rep_exp=28, rep_umbral=(0.62, 0.88),
                     rep_base=0.22,
                     rep_filos=[((0.45, -0.9), hx("5E6E58"), hx("A9C795"), 0.5, 0.82),
                                ((-0.85, 0.5), hx("5E3A1E"), hx("C9803A"), 0.72, 0.93)])
            t["sombra_col"], t["sombra_a"] = (0, 0, 0), 80
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


# --- Arcontes: rasga el velo con la pata (y por un momento se ve la luz de detrás; es un recurso del dibujo) · eriza el
#     lomo ante una sombra que no se ve (pasa por encima, apenas sugerida).
# (pata, inclinación de la cabeza, ojo, orejas, ancho de la raja). Mira el aire, alza la pata, rasga de arriba abajo, la
# raja se abre y deja salir la luz, se cierra; el gato se queda mirando el lugar.
VELO = [(None, -13, "abierto", 0.0, 0.0), ((45.6, 31.6), -17, "abierto", 0.05, 0.0), ((51.4, 38.6), -9, "abierto", 0.15, 0.45),
        ((48.6, 45.6), -5, "abierto", 0.45, 1.5), (None, -8, "abierto", 0.4, 2.8), (None, -10, "abierto", 0.25, 1.7),
        (None, -11, "abierto", 0.1, 0.5), (None, -9, "medio", 0.0, 0.0)]
RAJA = dict(x=55.6, y0=13.5, y1=47.5)


def rasgar(k):
    pata, inc, ojo, orejas, ancho = VELO[k]
    p = dict(g8.SENTADO)
    atras = 0.7 if k in (4, 5) else 0.0                      # se echa un poco atrás cuando la luz sale
    p.update(inc=inc, ojo=ojo, orejas=orejas, cab=(38.8 - atras, 27.2 + 0.3 * atras))
    if pata:
        p["fn"] = brazo((36, 43), pata, (1.2, 0.6))
    s_ = math.sin(2 * math.pi * k / 8)
    p["cola"] = [(x + (0.9 * s_ if i >= 4 else 0), y) for i, (x, y) in enumerate(g8.SENTADO["cola"])]
    return p, dict(obj=(RAJA["x"], (RAJA["y0"] + RAJA["y1"]) / 2), pata=pata or (37.0, SUELO), k=k, ancho=ancho)


def borde_raja(y):
    """El filo rasgado de la raja: no es una línea recta, como una tela que se rompe."""
    return RAJA["x"] + 0.55 * math.sin(y * 1.9) + 0.35 * math.sin(y * 0.63 + 1.0)


def o_velo(L, a):
    """La raja en el aire: fina al rasgar, se abre en forma de huso por donde sale la luz blanca y violeta (el halo lo
    pone el pintor) y se cierra. Al abrirse del todo suelta unas motas de luz."""
    k, ancho = a["k"], a["ancho"]
    if ancho <= 0:
        return
    y0, y1 = RAJA["y0"], RAJA["y1"]
    if k == 2:                                                 # la raya va detrás de la pata
        y1 = 41.0
    if k == 6:
        y0, y1 = y0 + 6, y1 - 7
    ys = [y0 + (y1 - y0) * q / 24 for q in range(25)]
    if ancho < 1.0:
        L.capsula("f_Q", [(borde_raja(y), y) for y in ys], 0.95)
        return
    izq, der = [], []
    for y in ys:
        u = (y - y0) / (y1 - y0)
        hw = ancho / 2 * math.sin(math.pi * u) ** 0.65 + 0.3
        izq.append((borde_raja(y) - hw, y)); der.append((borde_raja(y) + hw * 0.85, y))
    L.poligono("f_Q", izq + der[::-1])
    if k in (4, 5):                                            # motas de luz que salen
        for j, (dx, dy) in enumerate(((-3.6, -6.0), (3.4, -2.4), (-2.6, 5.4), (4.2, 7.6))):
            sale = 1.0 if k == 4 else 1.7
            L.elipse("f_Q", RAJA["x"] + dx * sale, 30.5 + dy * sale, 0.5, 0.5)


# (cuánto se eriza, dónde va la sombra, inclinación de la cabeza, ojo, orejas, bufa). La sombra entra por delante,
# alta, pasa por encima del gato y se va por detrás; el gato la sigue con la cabeza, erizado.
SOMBRA_PASA = [(0.0, 74, -16, "abierto", 0.0, False), (0.55, 60, -26, "abierto", 0.55, False), (1.0, 47, -34, "abierto", 0.95, False),
               (1.0, 34, -36, "abierto", 0.95, True), (1.0, 21, -30, "abierto", 0.95, True), (1.0, 8, -22, "abierto", 0.9, False),
               (0.5, -6, -8, "medio", 0.6, False), (0.0, -22, 0, "medio", 0.3, False)]


def erizar_sombra(k):
    f, sx, inc, ojo, orejas, bufa = SOMBRA_PASA[k]
    p = g8.interp(g8.DE_PIE, g8.ANIMS["enojado"][k % 3], f)
    p.update(inc=inc, ojo=ojo, orejas=orejas, lengua=bufa, semilla=k)
    p["cab"] = (p["cab"][0] - 0.6 * f, p["cab"][1] - 3.4 * f)          # alza la cabeza para seguirla
    return p, dict(obj=(sx, 7.0), k=k)


def o_sombra(L, a):
    """La sombra que pasa: una mancha larga y baja, con la cabeza adelante (dos puntas, como orejas de animal; nunca
    entera) y una cola de humo atrás. El pintor la vuelve translúcida."""
    sx, sy = a["obj"]; k = a["k"]
    ond = 0.6 * math.sin(k * 1.3)
    L.elipse("sombra21", sx + 1.0, sy + ond * 0.3, 8.6, 2.9, -4)
    L.elipse("sombra21", sx - 7.6, sy - 0.6, 3.0, 2.3)                            # la cabeza, adelante
    L.poligono("sombra21", [(sx - 9.4, sy - 1.8), (sx - 9.0, sy - 5.6), (sx - 7.8, sy - 2.2)])     # una oreja
    L.poligono("sombra21", [(sx - 7.4, sy - 2.0), (sx - 6.2, sy - 5.2), (sx - 5.6, sy - 1.6)])     # la otra
    L.poligono("sombra21", [(sx - 10.0, sy - 0.4), (sx - 12.6, sy + 0.6), (sx - 9.6, sy + 1.2)])   # el hocico largo
    L.capsula("sombra21", [(sx + 8.6, sy + 0.4), (sx + 12.4, sy - 0.8 + ond), (sx + 16.0, sy + 0.6 - ond)], 1.6)  # humo


g17.ACCIONES.update(rasgar=(rasgar, 8, "sentado"), erizar_sombra=(erizar_sombra, 8, "quieto"))
g17.ESCALA.update(rasgar=1.0, erizar_sombra=1.0)


# --- Galia: se esconde entre el muérdago de un roble (una rama de roble caída con su mata de muérdago) · le bufa a un
#     casco romano tirado en el río. Las letras de los objetos se pintan en este orden: U, V, X, Q (lo último, encima).
MATA = (54.0, 49.8, 9.8, 8.8)          # la mata de muérdago: centro y radios

# Cada hoja de la mata: (ángulo, distancia al centro en fracción del radio, largo, giro). Fijas, para que la mata no hierva.
_rm = random.Random(52)
HOJAS_MATA = []
for _j in range(48):
    _a = _rm.uniform(0, 2 * math.pi)
    HOJAS_MATA.append((_a, _rm.uniform(0.25, 1.0) ** 0.6, _rm.uniform(2.0, 3.0), _rm.uniform(-0.5, 0.5)))
BAYAS_MATA = [(_rm.uniform(-0.75, 0.75), _rm.uniform(-0.7, 0.75)) for _ in range(11)]
COLA_ARRIBA = [(19.0, 48.4), (15.6, 43.6), (14.2, 38.0), (15.0, 33.0), (17.4, 30.0), (20.0, 30.2)]


def muerdago(k):
    """Mira la mata, se agacha, mete la cabeza (la grupa y la cola se quedan fuera), asoma por arriba con los ojos y
    las orejas, mira a un lado, cierra los ojos a gusto (se cae una baya) y sale con una ramita en la cabeza."""
    if k == 0:
        p = g8.respirar(g8.DE_PIE, 1, 4)
        p.update(inc=16, orejas=0.0, ojo="abierto")
    elif k == 1:
        p = g8.mover(g8.interp(g8.DE_PIE, AGAZAPADO, 0.7), 1.2, 0)
        p.update(inc=8, orejas=0.25, ojo="abierto")
    elif k in (2, 3):
        p = g8.mover(AGAZAPADO, 5.0, 0)
        s_ = 1 if k == 2 else -1
        p["cola"] = [(x + s_ * 1.4 * (i / 5) ** 1.5, y) for i, (x, y) in enumerate(COLA_ARRIBA)]
        p["colaw"] = [3.8, 4.0, 4.0, 3.8, 3.6, 3.4]
        p.update(inc=14, orejas=0.4)
    else:
        p = g8.mover(AGAZAPADO, 5.0, 0)
        p["cola"] = [(x + (0.8 if k == 5 else -0.4) * (i / 5) ** 1.5, y + 0.6) for i, (x, y) in enumerate(COLA_ARRIBA)]
        p["colaw"] = [3.8, 4.0, 4.0, 3.8, 3.6, 3.4]
        p.update(cab=(53.4, 40.8), inc={4: -4, 5: 6, 6: -2, 7: 0}[k], orejas=0.15,
                 ojo={4: "abierto", 5: "medio", 6: "cerrado"}.get(k, "abierto"))
        if k == 7:
            p = g8.mover(g8.respirar(g8.DE_PIE, 3, 4), 1.0, 0)
            p.update(inc=-6, orejas=0.0, ojo="medio")
    return p, dict(obj=(MATA[0], MATA[1]), k=k, cab=p["cab"], inc=p["inc"])


def _hoja_roble(L, capa, x, y, ang, largo=3.4, ancho=1.5):
    """Hoja de roble: un óvalo con tres pares de lóbulos redondos y su pecíolo."""
    ca, sa = math.cos(ang), math.sin(ang)
    P_ = lambda u, v: (x + u * ca - v * sa, y + u * sa + v * ca)
    L.elipse(capa, *P_(0, 0), largo, ancho * 0.75, math.degrees(ang))
    for u in (-0.55, 0.05, 0.6):
        for v in (-1, 1):
            L.elipse(capa, *P_(u * largo, v * ancho * 0.72), ancho * 0.55, ancho * 0.5)
    L.capsula(capa, [P_(-largo, 0), P_(-largo - 1.0, 0)], 0.5)


def o_muerdago(L, a):
    """La rama de roble caída en el suelo, con sus hojas lobuladas, y en ella la mata redonda de muérdago (hojas largas
    de dos en dos y bayas blancas). Se sacude cuando el gato se mete; en el último cuadro le queda una ramita en la cabeza."""
    k = a["k"]
    cx, cy, rx, ry = MATA
    sac = {2: 0.7, 3: -0.7, 4: 0.35, 5: -0.25, 6: 0.0}.get(k, 0.0)
    # Hojas del roble (U), detrás; la rama (V).
    for (x, y, an) in ((41.5, 56.2, 3.6), (44.6, 54.4, 4.2), (61.6, 44.6, 5.3), (63.2, 49.8, 0.2), (60.6, 57.0, 0.6)):
        _hoja_roble(L, "f_U", x + sac * 0.4, y, an, 2.6, 1.3)
    L.capsula("f_V", [(38.4, 59.4), (46.0, 57.6), (55.0, 55.4), (63.6, 53.0)], 1.7)
    L.capsula("f_V", [(55.0, 55.4), (60.0, 50.0), (62.4, 46.4)], 1.0)
    L.capsula("f_V", [(46.0, 57.6), (43.6, 55.2)], 0.8)
    # La mata de muérdago: el fondo de la bola, en sombra (U), y encima sus hojas oblongas y gruesas (X).
    ojo_ = g8.rot(a["cab"][0], a["cab"][1], 2.4, -0.7, math.radians(a["inc"])) if k in (4, 5, 6) else None
    L.elipse("f_U", cx + sac * 0.5, cy, rx * 0.86, ry * 0.86)
    for (an, d, lg, gi) in HOJAS_MATA:
        hx_, hy_ = cx + math.cos(an) * d * rx + sac * d, cy + math.sin(an) * d * ry
        if ojo_ is not None and math.hypot(hx_ - ojo_[0], (hy_ - ojo_[1]) * 1.3) < 2.6:
            continue                                                       # por ahí asoman los ojos
        u = an + gi
        L.capsula("f_X", [(hx_ - math.cos(u) * lg / 2, hy_ - math.sin(u) * lg / 2), (hx_ + math.cos(u) * lg / 2, hy_ + math.sin(u) * lg / 2)], 1.6)
    for (bx, by) in BAYAS_MATA:
        L.elipse("f_Q", cx + bx * rx + sac * 0.6, cy + by * ry, 0.62, 0.62)
    if k in (4, 5, 6, 7):                                                  # la ramita en la cabeza
        hx0, hy0 = a["cab"]
        ia = math.radians(a["inc"])
        P_ = lambda u, v: g8.rot(hx0, hy0, u, v, ia)
        L.capsula("f_X", [P_(-1.6, -5.6), P_(-4.4, -7.6)], 1.2)
        L.capsula("f_X", [P_(-1.4, -5.4), P_(0.8, -8.0)], 1.2)
        L.elipse("f_Q", *P_(-1.6, -6.4), 0.62, 0.62)
    if k == 6:                                                             # se cae una baya
        L.elipse("f_Q", cx - 4.0, cy + ry + 0.6, 0.6, 0.6)


CASCO = (54.8, 57.2)                    # el casco tirado en el río: centro de la calota


def bufar_casco(k):
    """Ve el casco en el agua, lo huele, se echa atrás y se eriza: lomo arqueado, cola esponjada, orejas aplastadas y
    bufa con la boca abierta; luego se le pasa y voltea la cabeza, digno."""
    if k == 0:
        p = g8.respirar(g8.DE_PIE, 0, 4)
        p.update(inc=18, orejas=0.1)
    elif k == 1:
        p = dict(g8.DE_PIE)
        p.update(cab=(49.6, 41.6), inc=32, ojo="medio", orejas=0.0)
        p["cola"] = [(x + 0.6 * (i / 5) ** 1.5, y) for i, (x, y) in enumerate(g8.DE_PIE["cola"])]
    elif k == 2:
        p = g8.mover(g8.interp(g8.DE_PIE, g8.ANIMS["enojado"][0], 0.65), -1.4, 0)
        p.update(orejas=0.9, ojo="abierto", inc=4, lengua=False)
    elif k in (3, 4, 5):
        p = g8.mover(dict(g8.ANIMS["enojado"][(k - 3) % 3]), -1.4, 0)
        p.update(orejas=1.0, lengua=True, ojo="abierto", inc=12 + (k == 4) * 3)
    elif k == 6:
        p = g8.mover(g8.interp(g8.DE_PIE, g8.ANIMS["enojado"][1], 0.45), -0.8, 0)
        p.update(orejas=0.7, ojo="medio", inc=10, lengua=False)
    else:
        p = g8.respirar(g8.DE_PIE, 1, 4)
        p.update(inc=-16, orejas=0.3, ojo="cerrado")
        p["cola"] = [(x + 1.2 * (i / 5) ** 2, y - 1.2 * (i / 5)) for i, (x, y) in enumerate(g8.DE_PIE["cola"])]
    return p, dict(obj=CASCO, k=k, cab=p["cab"], inc=p["inc"], bufa=k in (3, 4, 5))


def o_casco(L, a):
    """Un casco romano de bronce (de calota redonda con su botón de remate y el cubrenuca, como los que llevaban las
    legiones de César) con su cresta de crin roja, tirado en el agua de la orilla; las ondas se abren a su
    alrededor. Cuando el gato bufa, unas rayitas le salen de la boca."""
    k = a["k"]
    cx, cy = CASCO
    sube = {2: -0.5, 3: -0.2, 4: 0.3, 5: 0.0}.get(k, 0.0)                 # se mece cuando el gato salta atrás
    cy += sube
    ang = math.radians(14)
    P_ = lambda u, v: g8.rot(cx, cy, u, v, ang)
    cupula = [P_(7.0 * math.cos(math.radians(q)), 6.6 * math.sin(math.radians(q))) for q in range(180, 361, 10)]
    L.poligono("f_U", cupula + [P_(7.0, 1.4), P_(-7.0, 1.4)])            # la calota, con la boca hacia abajo
    L.poligono("f_U", [P_(-6.0, -0.8), P_(-10.6, 0.4), P_(-10.4, 2.6), P_(-6.0, 2.0)])  # el cubrenuca, hacia atrás
    L.poligono("f_U", [P_(3.2, 0.6), P_(6.4, 0.6), P_(6.0, 6.0), P_(3.6, 5.4)])       # la carrillera
    L.capsula("f_K", [P_(-10.0, 1.2), P_(-6.2, 1.0), P_(0.0, 1.2), P_(6.8, 1.0)], 0.6)  # el filo del borde
    L.capsula("f_K", [P_(3.2, 1.8), P_(3.6, 5.0)], 0.45)                  # la bisagra de la carrillera
    # El agua (encima de lo que se hunde): la orilla del río.
    L.elipse("f_V", 53.6, 60.6, 12.2, 3.0)
    L.poligono("f_V", [(41.6, 61.0), (43.4, 59.2), (50.0, 58.6), (50.0, 61.4)])
    # La cresta de crin roja, en abanico sobre la calota (aunque esté tirado, se reconoce de lejos), con sus cerdas.
    arco_ = lambda r, q: P_(r * math.cos(math.radians(q)), -6.0 + r * 0.72 * math.sin(math.radians(q)))
    L.poligono("f_X", [arco_(9.0, q) for q in range(204, 337, 8)] + [P_(0.0, -5.6)])
    for q in (228, 254, 286, 312):
        L.capsula("f_K", [arco_(4.4, q), arco_(7.8, q)], 0.4)
    L.elipse("f_U", *P_(0.4, -6.6), 1.4, 1.2, 14)                          # el botón del remate
    # Ondas: arcos que se abren alrededor del casco (más grandes cuando se mece).
    r = 1.0 + (k % 4) * 0.9 + (1.2 if k in (2, 3) else 0)
    for (x0, x1, y) in ((cx - 10.6 - r * 0.6, cx - 8.0 - r * 0.6, 59.6), (cx + 5.0 + r * 0.3, cx + 7.4 + r * 0.5, 60.6), (44.0 + (k % 2), 47.6 + (k % 2), 61.6)):
        L.capsula("f_Q", [(x0, y), (x1, y)], 0.6)
    if k in (1, 4):
        L.elipse("f_Q", cx - 2.4, 59.1, 0.5, 0.5)
    if a["bufa"]:                                                          # el bufido
        hx0, hy0 = a["cab"]
        ia = math.radians(a["inc"])
        bx, by = g8.rot(hx0, hy0, 7.4, 3.6, ia)
        for j, dy_ in enumerate((-1.6, 0.0, 1.6)):
            l0 = 0.8 + 0.4 * ((k + j) % 2)
            L.capsula("f_K", [(bx + l0, by + dy_ * 0.8), (bx + l0 + 2.4, by + dy_ * 1.4)], 0.62)


g17.ACCIONES.update(muerdago=(muerdago, 8, "quieto"), bufar_casco=(bufar_casco, 8, "quieto"))
g17.ESCALA.update(muerdago=1.0, bufar_casco=1.0)

ESP_MIOS = {
    "frente": [("microfilm", o_microfilm, "esconde un microfilm en el collar"),
               ("agacharse", o_escaner, "se agacha cuando el escáner de una patrulla entra por la rendija del techo")],
    "arcontes": [("rasgar", o_velo, "rasga el velo con la pata y por un momento se ve la luz de detrás"),
                 ("erizar_sombra", o_sombra, "eriza el lomo ante una sombra que no se ve")],
    "galia": [("muerdago", o_muerdago, "se esconde entre el muérdago de un roble"),
              ("bufar_casco", o_casco, "le bufa a un casco romano tirado en el río")],
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



# ---------------------------------------------------------------------
#  Arcontes: la orilla de Magdala y el lago de Galilea (docs/diseno/temas/arcontes.md, `TrazosArcontes`)
# ---------------------------------------------------------------------
HORIZONTE = 37          # donde el lago toca la otra orilla
SOL = (104, 8)          # el sol, arriba a la izquierda: de ahí viene la luz de todo el fondo (y la del gato)
GRIETA_X = 300          # la grieta de luz en el cielo de noche (arriba a la derecha: el filo del gato viene de ahí)

PAL_ARC_D = dict(
    cielo=(hx("AFC4C3"), hx("C3D0C2"), hx("D6D8BE"), hx("E6DAB2"), hx("F0DCA6")),
    sol=hx("FFF8E2"), sol_halo=hx("F5DA8E"), oro=hx("D4A62A"),
    nube=(hx("F8F1E0"), hx("E8DCC0"), hx("CFC09E")), nube_oro=hx("EDC765"),
    lejos=(hx("CBBFA8"), hx("BDB09A")), monte=(hx("CDB890"), hx("B49E78"), hx("98845F")), matorral=hx("8E8A64"),
    aldea=(hx("F2EAD6"), hx("A8987A")),
    lago=(hx("A9C6BE"), hx("86AEA8"), hx("629698"), hx("478191"), hx("366E7E"), hx("2E6272"), hx("3B7680")),
    onda_luz=hx("BFD9D0"), onda_osc=hx("2A5A68"), destello=(hx("FFF7E0"), hx("F3D47C"), hx("E2B648")),
    orilla=hx("6FA096"), arena_mojada=(hx("BCA97F"), hx("A8956C")), espuma=(hx("F6F1E2"), hx("D9E4D6")),
    arena=(hx("E0D3AE"), hx("D7C9A2"), hx("CCBC92")), guijarro=(hx("EDE3C8"), hx("C6B58E"), hx("9F8D6A")),
    caliza=(hx("E1D5B2"), hx("D9CBA6"), hx("D0C29B"), hx("DCCFAB")), caliza_luz=hx("F1E8CD"), junta=hx("A8966F"),
    caliza_sombra=hx("A9976F"), lado=(hx("B6A47F"), hx("A39169"), hx("8C7A57")), revoque=(hx("C9B48A"), hx("B39D74"), hx("8E7A56")),
    madera=(hx("A27A4C"), hx("6B4A2E"), hx("4A321E")), barro=(hx("D19060"), hx("B9774A"), hx("8A4E2B"), hx("5E3420")),
    lino=(hx("F6EEDB"), hx("E3D4AF"), hx("C4B48C")), pez=(hx("F0E4C4"), hx("C9B48A"), hx("8D7650")),
    tronco=(hx("A88052"), hx("7C5C3A"), hx("57402A")), palma=(hx("9DB262"), hx("78944A"), hx("55703A"), hx("3B5229")),
    datil=hx("C8862C"), hueco=(hx("4A382C"), hx("2E231C")), sal=(hx("FBF8F0"), hx("DCD5C4")),
    cesto=(hx("B08A58"), hx("8A6A44"), hx("624A30")), sombra=hx("B19F78"), figura=(hx("3C2E26"), hx("D9C8A2")),
    red=hx("7A6448"), polvo=(hx("FBEAB0"), hx("F3D47C")), vela_lejos=hx("EDE3CB"),
)
PAL_ARC_N = dict(
    cielo=(hx("0B0D1C"), hx("0E1122"), hx("121528"), hx("171A30"), hx("1C2038")),
    estrella=(hx("ECE4CF"), hx("A09CB0"), hx("C4B0FF"), hx("6E6A9E")),
    lejos=(hx("161A2E"), hx("1A1E34")), monte=(hx("2A2E48"), hx("1E2238"), hx("171A2C")), matorral=hx("141728"),
    aldea=(hx("E9B04F"), hx("8A6A3A")),
    lago=(hx("1C2238"), hx("182238"), hx("152236"), hx("121E30"), hx("101A2A"), hx("0E1826"), hx("142434")),
    onda_luz=hx("2E3F5E"), onda_osc=hx("09111C"), destello=(hx("ECE4CF"), hx("A09CB0"), hx("6E6A9E")),
    orilla=hx("1C2C3C"), arena_mojada=(hx("2A2A3A"), hx("222232")), espuma=(hx("5A6078"), hx("3C4258")),
    arena=(hx("34344A"), hx("2E2E44"), hx("29293D")), guijarro=(hx("4A4A62"), hx("34344A"), hx("22222F")),
    caliza=(hx("3E3F56"), hx("393A51"), hx("35364C"), hx("3B3C53")), caliza_luz=hx("4C4D67"), junta=hx("25263A"),
    caliza_sombra=hx("2A2B40"), lado=(hx("2B2C42"), hx("25263A"), hx("1F2032")), revoque=(hx("3A3850"), hx("302F44"), hx("232236")),
    madera=(hx("4E3E3A"), hx("362A2A"), hx("241C1E")), barro=(hx("6A4436"), hx("553628"), hx("40281E"), hx("2A1A16")),
    lino=(hx("4C4C64"), hx("3E3E55"), hx("303044")), pez=(hx("6A6A80"), hx("4E4E64"), hx("36364A")),
    tronco=(hx("2C2834"), hx("221F2A"), hx("1A1820")), palma=(hx("2A3040"), hx("212634"), hx("1A1E2A"), hx("13161F")),
    datil=hx("3A2A28"), hueco=(hx("3A2410"), hx("24160A")), sal=(hx("6C6C84"), hx("50506A")),
    cesto=(hx("4A3C34"), hx("3A2E28"), hx("2A201C")), sombra=hx("232338"), figura=(hx("10101A"), hx("3A3A50")),
    red=hx("2E2830"), vela_lejos=hx("3A3C54"),
    llama=(hx("FFF1B0"), hx("F0B24A"), hx("C8742A")), llama_halo=hx("FFD98A"), grieta=GRIETA, grieta_halo=GRIETA_H,
    esfera=hx("6E6A9E"),
)


def pon(px, x, y, col, k=1.0):
    """Un pixel (a 1 dp), mezclado si k < 1; fuera del cuadro no hace nada."""
    x, y = int(round(x)), int(round(y))
    if 0 <= x < W and 0 <= y < H:
        px[x, y] = col if k >= 1 else mezcla(px[x, y], col, k)


def nube_larga(im, x, y, w, P, semilla):
    """Nube alargada y baja: bultos redondos sobre una base chata; la panza en sombra, el cuerpo claro y el filo de arriba
    que mira al sol, dorado (el oro de más del mundo visible). Las orillas, deshilachadas."""
    px = im.load(); rnd = random.Random(semilla)
    pint = set()
    for k in range(0, w, 2):
        u = k / w
        r = 1.8 + 3.6 * math.sin(math.pi * u) ** 0.7 * rnd.uniform(0.7, 1.1)
        bx, by = x + k, y - r * 0.35
        for yy in range(int(by - r), int(y) + 1):
            for xx in range(int(bx - r), int(bx + r) + 1):
                if ((xx - bx) / r) ** 2 + ((yy - by) / (r * 0.7)) ** 2 < 1 and yy <= y:
                    pint.add((xx, yy))
    hacia_sol = lambda xx: (SOL[0] - xx)
    for (xx, yy) in pint:
        if (xx, yy - 1) not in pint and (xx > x + 2 or rnd.random() < 0.5):
            col = P["nube_oro"] if (hacia_sol(xx) > -60 or (xx, yy - 2) in pint) and tramar(0.7, xx, yy) else P["nube"][0]
        elif (xx, yy + 1) not in pint:
            col = P["nube"][2]
        elif (xx, yy + 2) not in pint:
            col = P["nube"][1]
        else:
            col = P["nube"][0]
        borde = (xx - 1, yy) not in pint or (xx + 1, yy) not in pint
        pon(px, xx, yy, col, 0.6 if borde else 0.95)


def montes_arc(im, P, o):
    """La otra orilla: lomas de caliza (lejos, en la bruma) y más cerca, con las laderas que dan al sol más claras,
    terrazas y matorral. De noche, siluetas; las que quedan cerca de la grieta tienen un filo violeta."""
    px = im.load()
    lejos = g9.ondas(41, 27.5, [(3.0, 150), (1.8, 55), (0.8, 21)])
    cerca = g9.ondas(43, 31.5, [(3.4, 120), (1.6, 41), (0.7, 14)])
    for x in range(W):
        for y in range(max(0, round(lejos[x])), HORIZONTE):
            px[x, y] = P["lejos"][0] if (y - lejos[x]) < 2.5 or o else P["lejos"][1]
    rnd = random.Random(7)
    for x in range(W):
        top = round(cerca[x])
        pend = cerca[min(W - 1, x + 2)] - cerca[max(0, x - 2)]
        for y in range(max(0, top), HORIZONTE):
            prof = y - top
            if o:
                col = P["monte"][1] if prof > 0 else P["monte"][0]
                if prof == 0 and abs(x - GRIETA_X) < 60:
                    col = mezcla(col, P["estrella"][3], 0.6 if abs(x - GRIETA_X) < 30 else 0.3)
            else:
                col = P["monte"][0] if pend < -0.25 else (P["monte"][2] if pend > 0.5 else P["monte"][1])
                if prof > 3 and (y + int(x * 0.1 + 2 * math.sin(x / 9.0))) % 4 == 0:
                    col = mezcla(col, P["monte"][2], 0.5)                       # terrazas
            px[x, y] = col
        if not o and rnd.random() < 0.22 and top + 2 < HORIZONTE - 1:     # matorral y olivos sueltos
            yy = rnd.randint(top + 1, HORIZONTE - 2)
            pon(px, x, yy, P["matorral"]); pon(px, x + 1, yy, P["matorral"], 0.6)
    # Una aldea en la falda de la otra orilla (casitas cúbicas de caliza; de noche, sus lámparas).
    for k, (ax, aw) in enumerate(((326, 3), (330, 2), (333, 3), (338, 2), (342, 3), (150, 2), (153, 3))):
        ay = HORIZONTE - 3 - (k % 2)
        if o:
            if k in (1, 3, 5):
                pon(px, ax + 1, ay + 1, P["aldea"][0])
        else:
            for xx in range(ax, ax + aw):
                pon(px, xx, ay, P["aldea"][0]); pon(px, xx, ay + 1, P["aldea"][0] if xx < ax + aw - 1 else P["aldea"][1])
    return cerca


def lago_arc(im, P, o):
    """El lago: claro en la lejanía (refleja el cielo), más hondo en medio, y otra vez claro y verdoso donde ya se ve
    el fondo cerca de la orilla. Ondas en rayitas, más juntas cerca; el reflejo de las lomas al pie de la otra orilla."""
    px = im.load()
    orilla = [62.2 + 1.3 * math.sin(x / 23.0 + 0.6) + 0.7 * math.sin(x / 7.3 + 1.0) for x in range(W)]
    degradado(im, HORIZONTE, 64, P["lago"][:6])
    for x in range(W):                                   # el bajo cerca de la orilla
        for y in range(int(orilla[x]) - 4, int(orilla[x]) + 1):
            if 0 <= y < H and tramar((y - orilla[x] + 4) / 4.5, x, y):
                px[x, y] = P["lago"][6]
    for x in range(W):                                   # reflejo de las lomas
        for y in range(HORIZONTE, HORIZONTE + 3):
            if tramar(0.55 - (y - HORIZONTE) * 0.18, x, y):
                px[x, y] = mezcla(px[x, y], P["monte"][2] if not o else P["monte"][2], 0.45)
    rnd = random.Random(11)
    for _ in range(330):                                 # ondas
        y = int(HORIZONTE + 2 + (rnd.random() ** 0.7) * 22)
        x = rnd.randrange(0, W)
        if y >= orilla[min(W - 1, x)] - 1: continue
        largo = 1 + int((y - HORIZONTE) / 6) + rnd.randint(0, 2)
        col = P["onda_luz"] if rnd.random() < 0.55 else P["onda_osc"]
        for k in range(largo):
            pon(px, x + k, y, col, 0.7 if o else 0.85)
    return orilla


def costa_arc(im, P, o, orilla):
    """La playa: la espuma donde rompe el agua, la arena mojada, la arena seca de caliza con su grano, y guijarros con la
    luz arriba a la izquierda y la sombra abajo a la derecha."""
    px = im.load(); rnd = random.Random(13)
    for x in range(W):
        y0 = int(orilla[x])
        for y in range(y0, H):
            v = (y - y0) / (H - y0)
            if y - y0 < 3:
                col = P["arena_mojada"][0 if (y - y0) == 2 or tramar(0.3, x, y) else 1]
            else:
                col = P["arena"][0] if v > 0.55 else (P["arena"][1] if v > 0.25 else P["arena"][2])
                if tramar(0.5, x, y) and 0.2 < v < 0.3 or tramar(0.5, x, y) and 0.5 < v < 0.6:
                    col = P["arena"][1]
            px[x, y] = col
        pon(px, x, y0, P["espuma"][0] if (x // 3) % 4 else P["espuma"][1])
        pon(px, x, y0 - 1, P["espuma"][1], 0.5)
    for _ in range(260):                                 # grano de la arena
        x, y = rnd.randrange(W), rnd.randrange(64, H)
        pon(px, x, y, P["guijarro"][rnd.choice((0, 1, 1, 2))], 0.6)
    for _ in range(46):                                  # guijarros
        x, y = rnd.randrange(W), rnd.randrange(66, H - 1)
        r = rnd.choice((1, 1, 1, 2, 2, 3))
        guijarro(px, x, y, r, P)


def guijarro(px, x, y, r, P):
    for yy in range(-r, 1):
        for xx in range(-r - 1, r + 2):
            if (xx / (r + 0.8)) ** 2 + (yy / (r * 0.75 + 0.3)) ** 2 <= 1:
                col = P["guijarro"][0] if (xx < 0 and yy < 0) else (P["guijarro"][2] if (xx > 0 and yy == 0) else P["guijarro"][1])
                pon(px, x + xx, y + yy, col)
    for xx in range(-r + 1, r + 3):
        pon(px, x + xx + 1, y + 1, P["sombra"], 0.55)


def sombra_suelo(im, x0, x1, y0, y1, P, k=0.5):
    """Sombra arrojada en el suelo (tramada para que no salga un color nuevo por cada pixel)."""
    px = im.load()
    for y in range(max(0, y0), min(H, y1)):
        for x in range(max(0, int(x0)), min(W, int(x1))):
            if tramar(k, x, y):
                px[x, y] = mezcla(px[x, y], P["sombra"], 0.6)


def casa_salador(im, P, o):
    """La casa del salador, a la izquierda: muro de sillares de caliza (cada uno con su tono, el filo de arriba con luz y
    la junta honda), el costado en sombra, el techo plano de barro con las vigas que asoman y la estera de caña, una
    tinaja en la azotea, la puerta con su dintel de madera, una ventanita, la escalera de mano, dos ánforas y el cesto de
    sal. Devuelve dónde está la puerta (de noche, de ahí sale la luz de la lámpara)."""
    px = im.load(); d = ImageDraw.Draw(im); rnd = random.Random(3)
    x0, x1, top, base = 0, 57, 21, 67
    # Sillares.
    y = top
    while y < base:
        alto = rnd.choice((4, 4, 5))
        x = x0 - rnd.randint(0, 6)
        while x < x1:
            largo = rnd.randint(6, 11); tono = rnd.choice(P["caliza"])
            for yy in range(y, min(y + alto, base)):
                for xx in range(max(x, x0), min(x + largo, x1)):
                    fy, fx = yy - y, xx - x
                    if fy == alto - 1 or fx == 0:
                        col = P["junta"]
                    elif fy == 0:
                        col = P["caliza_luz"]
                    elif fx == largo - 1 or fy == alto - 2 and tramar(0.5, xx, yy):
                        col = P["caliza_sombra"]
                    else:
                        col = tono
                    pon(px, xx, yy, col)
            if rnd.random() < 0.3:
                cx_, cy_ = x + rnd.randint(2, max(2, largo - 3)), y + rnd.randint(1, max(1, alto - 2))
                pon(px, cx_, cy_, P["caliza_sombra"]); pon(px, cx_ - 1, cy_ - 1, P["caliza_luz"])
            x += largo
        y += alto
    # Lo de arriba, más claro (le da el sol de lleno); lo de abajo, un poco en la sombra del suelo.
    for yy in range(base - 5, base):
        for xx in range(x0, x1):
            if tramar((yy - base + 5) / 6, xx, yy): px[xx, yy] = mezcla(px[xx, yy], P["caliza_sombra"], 0.45)
    # El costado, en sombra (se va hacia atrás).
    for xx in range(x1, x1 + 6):
        f = (xx - x1) / 6
        ya, yb = top + 0.4 + f * 1.6, base - f * 1.2
        for yy in range(int(ya), int(yb) + 1):
            col = P["lado"][1] if (yy - top) % 5 else P["lado"][2]
            if xx == x1: col = P["lado"][0]
            pon(px, xx, yy, col)
    # Techo de barro, vigas que asoman y la estera de caña que cuelga.
    for yy in range(top - 4, top):
        for xx in range(x0, x1 + 6):
            col = P["revoque"][0] if yy == top - 4 else (P["revoque"][1] if yy < top - 1 else P["revoque"][2])
            if xx >= x1: col = mezcla(col, P["lado"][2], 0.5)
            pon(px, xx, yy, col)
    for xx in range(x0 + 3, x1, 7):
        d.rectangle([xx, top, xx + 1, top + 1], fill=P["madera"][2]); pon(px, xx, top, P["madera"][1])
    for xx in range(x0, x1 + 5):
        if (xx * 5) % 7 < 4: pon(px, xx, top - 1 + (xx % 3 == 0), P["revoque"][2])
    # Tinaja en la azotea, recortada contra el cielo.
    tx, ty = 40, top - 4
    for yy in range(ty - 7, ty):
        u = (yy - (ty - 7)) / 7
        w_ = 1.2 + 3.0 * math.sin(math.pi * min(1, u * 1.1))
        for xx in range(int(tx - w_), int(tx + w_) + 1):
            pon(px, xx, yy, P["barro"][1] if xx < tx - w_ * 0.3 else (P["barro"][2] if xx < tx + w_ * 0.5 else P["barro"][3]))
    d.rectangle([tx - 1, ty - 9, tx + 1, ty - 8], fill=P["barro"][2]); pon(px, tx - 1, ty - 9, P["barro"][0])
    # Puerta con dintel; ventanita.
    pu = (15, 41, 26, base)
    d.rectangle([pu[0], pu[1], pu[2], pu[3]], fill=P["hueco"][0])
    for yy in range(pu[1], pu[3]):
        for xx in range(pu[0], pu[2] + 1):
            if tramar((yy - pu[1]) / (pu[3] - pu[1]) * 0.8, xx, yy): px[xx, yy] = P["hueco"][1]
    d.line([pu[0] - 1, pu[1], pu[0] - 1, pu[3]], fill=P["caliza_luz"])            # la jamba que da a la luz
    d.line([pu[2] + 1, pu[1], pu[2] + 1, pu[3]], fill=P["caliza_sombra"])
    d.rectangle([pu[0] - 3, pu[1] - 3, pu[2] + 3, pu[1] - 1], fill=P["madera"][1])
    d.line([pu[0] - 3, pu[1] - 3, pu[2] + 3, pu[1] - 3], fill=P["madera"][0])
    d.rectangle([pu[0] - 2, base - 1, pu[2] + 2, base], fill=P["caliza_luz"])       # umbral
    d.rectangle([38, 29, 44, 34], fill=P["hueco"][0]); d.line([38, 35, 44, 35], fill=P["caliza_luz"])
    d.line([41, 29, 41, 34], fill=P["madera"][1])
    # Escalera de mano al techo.
    for (xa, ya, xb, yb) in ((59, base, 61, top - 6), (63, base, 65, top - 6)):
        d.line([xa, ya, xb, yb], fill=P["madera"][1])
        pon(px, xa, ya - 1, P["madera"][0])
    for yy in range(top - 4, base, 4):
        f = (base - yy) / (base - top + 6)
        d.line([59 + 2 * f, yy, 63 + 2 * f, yy], fill=P["madera"][0 if not o else 1])
    sombra_suelo(im, 64, 70, 66, 69, P, 0.5)
    # Ánforas apoyadas en el muro y el cesto de sal.
    for (ax, inc) in ((33, 0), (39, 1)):
        anfora(im, ax, base, P, inc)
    cesto(im, 46, base, P)
    sombra_suelo(im, x1 + 6, x1 + 14, base - 1, base + 2, P, 0.5)
    return pu


def anfora(im, x, base, P, inc=0):
    """Ánfora de barro: cuerpo de huso, cuello, dos asas; la luz a la izquierda."""
    px = im.load()
    alto = 13
    for k in range(alto):
        u = k / (alto - 1)
        w_ = 0.6 + 2.6 * math.sin(math.pi * min(1.0, 0.15 + u * 0.95)) if u > 0.18 else 0.9
        cx = x + inc * u * 1.4
        yy = base - 1 - k
        for xx in range(int(round(cx - w_)), int(round(cx + w_)) + 1):
            f = (xx - (cx - w_)) / (2 * w_ + 0.01)
            col = P["barro"][0] if f < 0.25 else (P["barro"][1] if f < 0.6 else (P["barro"][2] if f < 0.85 else P["barro"][3]))
            pon(px, xx, yy, col)
    top = base - alto
    pon(px, x + inc * 1.4 - 1, top, P["barro"][1]); pon(px, x + inc * 1.4, top, P["barro"][1]); pon(px, x + inc * 1.4 + 1, top, P["barro"][2])
    for s in (-1, 1):
        pon(px, x + inc * 1.2 + 2 * s, top + 2, P["barro"][2]); pon(px, x + inc * 1.2 + 2 * s, top + 3, P["barro"][2])


def cesto(im, x, base, P):
    """Cesto de mimbre con el montón de sal."""
    px = im.load()
    for yy in range(base - 5, base):
        for xx in range(x, x + 10):
            col = P["cesto"][(xx + (yy % 2)) % 2] if 0 < xx - x < 9 else P["cesto"][2]
            if yy == base - 5: col = P["cesto"][0]
            pon(px, xx, yy, col)
    for yy in range(base - 8, base - 5):
        w_ = (yy - (base - 9)) * 1.6
        for xx in range(int(x + 5 - w_), int(x + 5 + w_) + 1):
            pon(px, xx, yy, P["sal"][0] if xx < x + 5 + w_ * 0.3 else P["sal"][1])


def tendedero(im, P, o):
    """El tendedero del salado: dos palos, la cuerda que se comba y los pescados abiertos colgados de la cola."""
    px = im.load(); d = ImageDraw.Draw(im)
    xa, xb, yt = 69, 103, 41
    for xx in (xa, xb):
        d.line([xx, yt - 1, xx, 68], fill=P["madera"][1]); d.line([xx - 1, yt, xx - 1, 68], fill=P["madera"][0])
        sombra_suelo(im, xx + 1, xx + 9, 68, 70, P, 0.5)
    cuerda = [(x, yt + 2.6 * math.sin(math.pi * (x - xa) / (xb - xa))) for x in range(xa, xb + 1)]
    for (x, y) in cuerda:
        pon(px, x, y, P["madera"][2])
    for x in range(xa + 4, xb - 2, 5):
        y = yt + 2.6 * math.sin(math.pi * (x - xa) / (xb - xa)) + 1
        pez_abierto(px, x, y, P)
    sombra_suelo(im, xa + 6, xb + 6, 69, 71, P, 0.3)


def pez_abierto(px, x, y, P):
    """Un pescado abierto y salado, colgado de la cola: la cola arriba, el cuerpo en huso, la carne clara al centro."""
    pon(px, x - 1, y, P["pez"][2]); pon(px, x + 1, y, P["pez"][2]); pon(px, x, y + 1, P["pez"][2])
    for k in range(2, 9):
        w_ = 0.5 + 1.6 * math.sin(math.pi * (k - 1) / 8)
        for xx in range(int(round(x - w_)), int(round(x + w_)) + 1):
            col = P["pez"][0] if abs(xx - x) < w_ * 0.45 else (P["pez"][1] if xx < x else P["pez"][2])
            pon(px, xx, y + k, col)


def red_y_ancla(im, P):
    """Red amontonada (malla de cuerda) y el ancla de piedra con su agujero."""
    px = im.load()
    for yy in range(63, 69):
        w_ = (yy - 62) * 1.6
        for xx in range(int(114 - w_), int(114 + w_) + 1):
            pon(px, xx, yy, P["red"] if (xx + yy) % 2 == 0 else P["cesto"][1])
    sombra_suelo(im, 116, 126, 67, 70, P, 0.5)
    for yy in range(66, 71):
        for xx in range(125, 131):
            if ((xx - 127.8) / 3.2) ** 2 + ((yy - 68.6) / 2.6) ** 2 < 1:
                pon(px, xx, yy, P["guijarro"][0] if xx < 127 and yy < 68 else P["guijarro"][1])
    pon(px, 128, 68, P["guijarro"][2])


def barca(im, x, yw, largo, P, o, vela=0, gente=True, lampara=False):
    """Barca de pesca de Galilea: casco de madera redondo con proa y popa levantadas, la traca de arriba más clara, el
    mástil y la vela cuadrada de lino en su verga (vela: cuánto la hincha el viento, 0 a 2). Mira a la derecha."""
    px = im.load(); d = ImageDraw.Draw(im)
    alto = max(2, largo // 7)
    for k in range(largo):
        u = k / (largo - 1)
        sube = 1.6 * (abs(u - 0.5) * 2) ** 3
        y_top = yw - alto - sube
        for yy in range(int(round(y_top)), yw + 1):
            col = P["madera"][0] if yy == int(round(y_top)) else (P["madera"][1] if yy < yw - 1 else P["madera"][2])
            pon(px, x + k, yy, col)
    mx = x + int(largo * 0.55)
    tope = yw - alto - int(largo * 0.75)
    d.line([mx, yw - alto, mx, tope], fill=P["madera"][2])
    if vela >= 0:
        ancho = int(largo * 0.62)
        vx0 = mx - ancho // 2
        alto_v = int(largo * 0.55)
        for j in range(alto_v):
            v = j / max(1, alto_v - 1)
            hincha = (0.6 + 0.7 * vela) * math.sin(math.pi * min(1.0, v * 1.05))   # la baluma se hincha con el viento
            xa, xb = vx0 + 0.4 * v, vx0 + ancho + hincha
            for xx in range(int(round(xa)), int(round(xb)) + 1):
                u = (xx - xa) / max(1, xb - xa)
                col = P["lino"][0] if u < 0.45 else (P["lino"][1] if u < 0.82 else P["lino"][2])
                if j == alto_v - 1 or (j == alto_v - 2 and u > 0.5): col = P["lino"][2]
                if largo > 20 and (xx - vx0) % 4 == 2 and j % 2 == 0 and 0.1 < u < 0.9:
                    col = P["lino"][1] if u < 0.45 else P["lino"][2]           # las líneas de los anillos de cargar
                pon(px, xx, tope + 2 + j, col)
        d.line([vx0 - 1, tope + 1, vx0 + ancho + 1, tope + 1], fill=P["madera"][1])        # la verga
    if gente:
        for (fx, alto_f) in ((x + 4, 4), (x + largo - 7, 3)):
            for yy in range(yw - alto - alto_f, yw - alto):
                pon(px, fx, yy, P["figura"][1] if yy > yw - alto - alto_f + 1 else P["figura"][0])
            pon(px, fx, yw - alto - alto_f - 1, P["figura"][0])
    if lampara:
        pon(px, x + largo - 2, yw - alto - 2, P["llama"][1])


def palmera(im, P, o, sway=0.0):
    """La palmera datilera a la derecha: el tronco anillado que se inclina hacia el lago y una copa de hojas que se
    arquean y cuelgan, con sus foliolos a los dos lados, y que cruzan el cielo y enmarcan la banda. La luz viene de
    arriba a la izquierda: el lado de arriba de cada foliolo es más claro. De noche, silueta, con un filo violeta del
    lado de la grieta. `sway` mece las puntas."""
    px = im.load()
    base, copa = (404, 75), (390, 13)
    n = 64
    for i in range(n + 1):
        u = i / n
        x = base[0] + (copa[0] - base[0]) * (u ** 1.9)
        y = base[1] + (copa[1] - base[1]) * u
        w_ = 2.3 - 0.8 * u
        for xx in range(int(round(x - w_)), int(round(x + w_)) + 1):
            f = (xx - (x - w_)) / (2 * w_)
            col = P["tronco"][0] if f < 0.3 else (P["tronco"][1] if f < 0.72 else P["tronco"][2])
            if (int(y) * 2 + (1 if xx > x else 0)) % 5 == 0: col = P["tronco"][2] if f > 0.15 else P["tronco"][1]
            pon(px, xx, y, col)
    for k in range(5):                                       # hojas secas que cuelgan bajo la copa
        for j in range(4 + k % 3):
            pon(px, copa[0] - 2 + k + 0.3 * j, copa[1] + 2 + j, P["tronco"][1 + (j % 2)])
    hojas = [(268, 13, 0.25), (245, 22, 0.55), (222, 27, 0.75), (200, 29, 0.9), (180, 27, 0.95), (160, 21, 0.8),
             (298, 16, 0.5), (325, 17, 0.7), (350, 15, 0.85), (15, 12, 0.7), (140, 14, 0.5)]
    for j, (ang, largo, cae) in enumerate(hojas):
        a = math.radians(ang)
        dx, dy = math.cos(a), math.sin(a)
        prev = (copa[0], copa[1])
        for k in range(1, largo + 1):
            u = k / largo
            x = copa[0] + dx * k + sway * u * u * (1.0 if j % 2 else -0.7)
            y = copa[1] + dy * k + cae * 10 * u * u
            tx, ty = x - prev[0], y - prev[1]; tn = math.hypot(tx, ty) or 1; tx, ty = tx / tn, ty / tn
            pon(px, x, y, P["palma"][2])
            if k >= 2:
                lf = 4.2 * math.sin(math.pi * min(1.0, u * 1.15)) ** 0.7 + 0.5
                for sgn in (-1, 1):
                    nx_, ny_ = -ty * sgn, tx * sgn
                    lx, ly = 0.45 * tx + 0.8 * nx_, 0.45 * ty + 0.8 * ny_ + 0.55       # cuelgan un poco
                    ln = math.hypot(lx, ly); lx, ly = lx / ln, ly / ln
                    arriba = ly < 0.15
                    for q in range(1, int(lf) + 1):
                        if o:
                            c2 = P["palma"][1] if arriba else P["palma"][3]
                            if arriba and q == 1 and dx < 0.3 and k % 3 == 0: c2 = P["estrella"][3]
                        else:
                            c2 = (P["palma"][0] if q <= 2 else P["palma"][1]) if arriba else (P["palma"][2] if q == 1 else P["palma"][3])
                        pon(px, x + lx * q, y + ly * q, c2)
            prev = (x, y)
    for (dx, dy) in ((-1, 2), (1, 3), (0, 4), (2, 2), (-2, 3), (0, 3), (1, 4)):     # racimo de dátiles
        pon(px, copa[0] + dx, copa[1] + dy, P["datil"] if (dx + dy) % 3 else P["tronco"][0])


def grieta_puntos():
    """La grieta del cielo: una raja fina, casi vertical, con el filo quebrado y más abierta al centro (como una tela que
    empieza a romperse), y una ramita corta. Es fija; lo que late es su luz."""
    quiebre = [0, 0, 1, 1, 0, 1, 1, 2, 2, 1, 2, 2, 3, 3, 2, 3]
    tronco = [(GRIETA_X + quiebre[k], 5 + k) for k in range(len(quiebre))]
    tronco += [(x + 1, y) for (x, y) in tronco[5:11]]           # al centro se abre a dos pixeles
    rama = [(GRIETA_X, 9), (GRIETA_X - 1, 9), (GRIETA_X - 2, 10)]
    return tronco, rama


def fondo_arcontes(t):
    """Arcontes: la orilla de Magdala y el lago de Galilea, el mismo lugar de día y de noche. De día: el sol arriba a la
    izquierda con un halo dorado de más (el oro del mundo visible), nubes largas con el filo dorado, la otra orilla en
    lomas de caliza con una aldea, el lago con su hondura y su reguero de destellos bajo el sol, barcas de pesca con
    vela, la casa del salador con su tendedero de pescado, ánforas, sal y la red, la playa de guijarros y una palmera que
    enmarca. De noche: el cielo antiguo con sus estrellas, la casa con la lámpara en la puerta, la barca que pesca con su
    lámpara, las luces de la aldea y, muy fina, la grieta de luz blanca y violeta, con su reflejo en el agua.
    Lo que se mueve: el agua (destellos, ondas y la espuma de la orilla), la vela, la palmera, el polvo dorado en la luz
    (de día); las estrellas, la grieta que late apenas, las llamas y, una vez por bucle, algo que pasa por el cielo y
    tapa las estrellas (de noche)."""
    o = t["oscuro"]
    P = PAL_ARC_N if o else PAL_ARC_D
    im, d = lienzo(P["cielo"][0])
    px = im.load()
    # --- El cielo.
    degradado(im, 0, HORIZONTE, P["cielo"])
    estrellas_ = []
    if not o:
        luz(im, SOL[0], SOL[1], 92, P["oro"], 0.2, 5, ry=40)              # el dorado de más
        luz(im, SOL[0], SOL[1], 22, P["sol_halo"], 0.55, 4, ry=18)
        nube_larga(im, 150, 16, 50, P, 1); nube_larga(im, 222, 10, 34, P, 2); nube_larga(im, 272, 21, 62, P, 3)
        nube_larga(im, 340, 11, 26, P, 4); nube_larga(im, 20, 33, 26, P, 5)
        d.ellipse([SOL[0] - 4, SOL[1] - 4, SOL[0] + 4, SOL[1] + 4], fill=P["sol"])
        for a in range(0, 360, 30):                                   # rayos cortos, del oro del mundo de abajo
            r0, r1 = 7, 10 if a % 60 else 12
            for r in range(r0, r1):
                pon(px, SOL[0] + r * math.cos(math.radians(a)), SOL[1] + r * math.sin(math.radians(a)), P["sol_halo"], 0.7)
    else:
        rnd = random.Random(19)
        tronco, rama = grieta_puntos()
        for _ in range(170):
            x, y = rnd.randrange(W), rnd.randrange(0, HORIZONTE - 4)
            if abs(x - GRIETA_X) < 6 and y < 26: continue
            col = P["estrella"][rnd.choice((0, 1, 1, 1, 3, 3, 2))]
            fondo_ = px[x, y]
            px[x, y] = col
            estrellas_.append((x, y, col, fondo_))
        for (x, y) in ((70, 6), (176, 12), (232, 4), (356, 9), (128, 21)):     # algunas más grandes, en cruz
            fondo_ = px[x, y]
            for (dx, dy) in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1)):
                pon(px, x + dx, y + dy, P["estrella"][0] if (dx, dy) == (0, 0) else P["estrella"][1])
        # Las esferas (recurso: el diagrama de los ofitas): arcos de puntos, apenas.
        for r in (150, 166, 182):
            hechos = set()
            for k in range(0, 1400):
                a = math.pi + math.pi * k / 1400
                x, y = round(206 + r * math.cos(a) * 1.3), round(175 + r * math.sin(a))
                if 0 <= y < HORIZONTE - 5 and (x, y) not in hechos and abs(x - GRIETA_X) > 4:
                    hechos.add((x, y)); pon(px, x, y, P["esfera"], 0.16)
        luz(im, GRIETA_X + 2, 12, 20, P["grieta_halo"], 0.16, 4, ry=13)
    # --- La otra orilla, el lago y la playa.
    montes_arc(im, P, o)
    orilla = lago_arc(im, P, o)
    if not o:                                                          # el reguero de oro bajo el sol
        rnd = random.Random(21)
        for y in range(HORIZONTE + 1, 60):
            ancho = 2 + (y - HORIZONTE) * 0.9
            for _ in range(2):
                x = SOL[0] + rnd.uniform(-ancho, ancho)
                for k in range(rnd.randint(1, 3)):
                    pon(px, x + k, y, P["destello"][rnd.choice((1, 1, 2))], 0.9)
    else:                                                              # el reflejo de la grieta, quebrado
        tronco, rama = grieta_puntos()
        for y in range(HORIZONTE + 1, 60):
            if y % 2 == 0 or y < HORIZONTE + 4:
                x = GRIETA_X + int(1.4 * math.sin(y * 1.3))
                pon(px, x, y, P["grieta_halo"], 0.35 if y > 50 else 0.5)
    if o:                                                              # las estrellas más claras, reflejadas
        for (x, y, col, _f) in estrellas_[::3]:
            yy = 2 * HORIZONTE - y + 3
            if HORIZONTE + 1 < yy < orilla[x] - 2:
                pon(px, x, yy, col, 0.35)
    agua = im.copy()                                                   # para saber después qué sigue siendo agua
    costa_arc(im, P, o, orilla)
    # --- Las barcas: una cerca, una lejos y una vela en la lejanía; la que está varada en la playa.
    for (vx, vy) in ((246, 38), (124, 39)):
        d.polygon([(vx, vy), (vx + 2, vy - 4), (vx + 3, vy)], fill=P["vela_lejos"])
        pon(px, vx, vy + 1, P["onda_osc"])
    barca_varada(im, P, o)
    # --- La casa del salador, el tendedero, la red y el ancla.
    puerta = casa_salador(im, P, o)
    tendedero(im, P, o)
    red_y_ancla(im, P)
    for (gx, gy, gr) in ((146, 75, 3), (152, 77, 2), (232, 73, 2), (290, 76, 3), (330, 74, 2)):
        guijarro(px, gx, gy, gr, P)
    # --- La luz de noche: la lámpara de la puerta alumbra el muro y la arena; la ventana, apenas.
    if o:
        lx, ly = (puerta[0] + puerta[2]) / 2, puerta[3] - 4
        luz(im, lx, ly - 2, 30, P["llama_halo"], 0.26, 5, ry=18)
        luz(im, lx + 4, 70, 26, P["llama_halo"], 0.2, 4, ry=6)
        d.rectangle([39, 30, 43, 34], fill=hx("6A4A26"))
        d.rectangle([puerta[0], puerta[1] + 2, puerta[2], puerta[3] - 1], fill=P["hueco"][0])
        luz(im, lx, ly, 9, P["llama"][2], 0.6, 3, ry=12, x0=puerta[0], x1=puerta[2] + 1, y1=puerta[3])
        lampara_barro(im, lx, puerta[3] - 1, P)
    # --- Lo que se mueve se dibuja en cada cuadro (sobre esta base, sin grano), y el grano va al final.
    estatico = im.copy()
    pe = estatico.load()
    estrellas_ = [e for e in estrellas_ if pe[e[0], e[1]] == e[2]]          # las que no quedaron detrás de las lomas
    pa = agua.load()
    es_agua = lambda x, y: 0 <= x < W and 0 <= y < H and pe[x, y] == pa[x, y] and y < orilla[x] - 1
    rnd = random.Random(5)
    gr = np.ones((H, W), np.float32)
    q = np.random.default_rng(31).random((H, W))
    gr[q < 0.07] = 0.955; gr[q > 0.93] = 1.04
    # Destellos del agua: (x, y, color, periodo, fase).
    destellos = []
    for _ in range(54 if not o else 40):
        if not o and rnd.random() < 0.55:
            y = rnd.randint(HORIZONTE + 1, 59); x = SOL[0] + rnd.uniform(-2 - (y - HORIZONTE) * 0.9, 2 + (y - HORIZONTE) * 0.9)
            col = P["destello"][0]
        else:
            y = rnd.randint(HORIZONTE + 2, 59); x = rnd.randrange(0, W)
            col = P["destello"][0 if not o else rnd.choice((1, 2))]
        if not (es_agua(int(x), y) and es_agua(int(x) + 1, y)): continue
        destellos.append((int(x), y, col, rnd.choice((10, 16, 20, 32, 40)), rnd.randrange(0, 40)))
    ondas_ = [(rnd.randrange(80, W - 30), rnd.randint(HORIZONTE + 6, 58), rnd.randint(2, 4), rnd.uniform(0, 6.3)) for _ in range(26)]
    ondas_ = [q_ for q_ in ondas_ if all(es_agua(q_[0] + k, q_[1]) for k in range(-2, q_[2] + 2))]
    tramos = [(130, 175, 0.0), (210, 262, 2.1), (296, 336, 4.2)]           # donde lame el agua
    polvo = [(rnd.uniform(4, 66), rnd.uniform(14, 60), rnd.uniform(0, 6.3)) for _ in range(9)]

    def mover(f, tk):
        px_ = f.load(); dd = ImageDraw.Draw(f)
        for (x, y, col, per, ph) in destellos:
            if (tk + ph) % per < per // 4:
                pon(px_, x, y, col)
                if per >= 32: pon(px_, x + 1, y, col, 0.6)
        for (x, y, largo, ph) in ondas_:
            dx = round(1.4 * math.sin(fase(tk, k=2) + ph))
            for k in range(largo):
                pon(px_, x + dx + k, y, P["onda_luz"], 0.75 if not o else 0.9)
        for (xa, xb, ph) in tramos:                                     # la espuma que sube y baja por la arena
            sube = math.sin(fase(tk, k=2) + ph)
            for x in range(xa, xb):
                y0 = orilla[x]
                if sube > 0.35:
                    pon(px_, x, int(y0) + 1, P["espuma"][0] if (x // 3) % 4 else P["espuma"][1])
                    pon(px_, x, int(y0), P["arena_mojada"][1] if not o else P["orilla"])
                elif sube < -0.35:
                    pon(px_, x, int(y0), P["arena_mojada"][1])
                    pon(px_, x, int(y0) - 1, P["espuma"][1])
        # La barca de cerca: la vela respira con el viento y el banderín ondea.
        v = 1 + math.sin(fase(tk, k=2))
        barca(f, 186, 50, 30, P, o, vela=v, lampara=o)
        bx_, by_ = 186 + int(30 * 0.55), 50 - 4 - int(30 * 0.75)
        for k in range(4):
            pon(px_, bx_ + 1 + k, by_ + round(0.8 * math.sin(fase(tk, k=4) + k * 1.2)), P["barro"][1] if not o else P["madera"][0])
        for k in range(12):                                             # su reflejo en el agua
            pon(px_, 188 + k * 2 + round(math.sin(fase(tk, k=2) + k)), 51 + (k % 2), P["onda_osc"], 0.6)
        # La barca de lejos: se mece.
        dy = round(0.6 * math.sin(fase(tk, k=1)))
        barca(f, 300, 43 + dy, 14, P, o, vela=0.5, gente=False, lampara=o)
        if not o:
            # Polvo dorado que flota en la luz, junto a la casa.
            for (x, y, ph) in polvo:
                xx = x + 3 * math.sin(fase(tk, k=1) + ph); yy = y + 2 * math.cos(fase(tk, k=1) + ph * 1.7)
                pon(px_, xx, yy, P["polvo"][int(ph * 3) % 2])
            # La palmera: las puntas de las hojas se mecen.
            palmera(f, P, o, sway=0.8 * math.sin(fase(tk, k=1)))
            return
        # --- Noche: estrellas que titilan, algo que pasa y tapa las estrellas, la grieta que late, las llamas.
        fsom = (tk / BUCLE - 0.28) / 0.34
        for j, (x, y, col, fondo_) in enumerate(estrellas_):
            if 0 <= fsom <= 1:
                sx = W + 70 - fsom * (W + 160)
                sy = 13 + 3 * math.sin(fsom * 6)
                if ((x - sx) / 46) ** 2 + ((y - sy) / 8) ** 2 < 1 or ((x - sx + 44) / 12) ** 2 + ((y - sy + 2) / 7) ** 2 < 1:
                    px_[x, y] = fondo_
                    continue
            if j % 5 == 0 and ((tk + j * 3) // 8) % 4 == 0:
                px_[x, y] = fondo_ if j % 2 else P["estrella"][0]
        tronco, rama = grieta_puntos()
        pulso = 0.5 + 0.5 * math.sin(fase(tk, k=2))
        for (x, y) in tronco + rama:
            for (dx, dy) in ((-1, 0), (1, 0)):
                if pulso > 0.3: pon(px_, x + dx, y + dy, P["grieta_halo"], 0.35 + 0.35 * pulso)
        for (x, y) in tronco + rama:
            pon(px_, x, y, P["grieta"] if pulso > 0.2 or (x + y) % 2 else P["grieta_halo"])
        for y in range(HORIZONTE + 2, 58, 2):                             # su reflejo tiembla
            if (y + tk // 6) % 3 == 0:
                pon(px_, GRIETA_X + int(1.4 * math.sin(y * 1.3 + fase(tk, k=4))), y, P["grieta"], 0.4 * (0.5 + pulso))
        # La llama de la puerta y la de la barca, y el reflejo de la barca.
        rnd2 = random.Random(tk // 2)
        lx, ly = (puerta[0] + puerta[2]) / 2, puerta[3] - 2
        llama_chica(px_, lx + 1, ly - 1, P, rnd2)
        llama_chica(px_, 186 + 28, 50 - 4 - 2, P, rnd2)
        for k, y in enumerate(range(52, 61)):
            if (k + tk // 3) % 3:
                pon(px_, 214 + round(1.2 * math.sin(fase(tk, k=4) + k)), y, P["llama"][1], 0.55 - k * 0.04)
        pon(px_, 312, 43 - 2 + dy, P["llama"][0])
        palmera(f, P, o, sway=0.8 * math.sin(fase(tk, k=1)))

    cache = {}

    def render(tk):
        if tk not in cache:
            f = estatico.copy()
            mover(f, tk)
            a = np.asarray(f, np.float32) * gr[..., None]
            cache[tk] = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8))
        return cache[tk]

    mascaras = {}

    def vivo(img, tick, delante):
        """Pega encima solo lo que cambió respecto del cuadro 0 (así no borra lo que ya haya en la banda)."""
        if delante: return
        tk = tick % BUCLE
        if tk not in mascaras:
            dif = (np.asarray(render(tk)) != np.asarray(render(0))).any(2)
            mascaras[tk] = Image.fromarray((dif * 255).astype(np.uint8)).resize((W * ESC, H * ESC), Image.NEAREST) if dif.any() else None
        if mascaras[tk] is not None:
            img.paste(subir(render(tk)), (0, 0), mascaras[tk])
    ANIM[("arcontes", o)] = [vivo]
    return subir(render(0))


def barca_varada(im, P, o):
    """La barca varada en la playa, de costado e inclinada, con dos remos apoyados y la red tendida en dos palos."""
    px = im.load(); d = ImageDraw.Draw(im)
    x0, x1, yb = 338, 378, 69
    for x in range(x0, x1):
        u = (x - x0) / (x1 - x0)
        quilla = yb - 1.5 * u
        borda = quilla - 6 + 2.4 * (abs(u - 0.5) * 2) ** 2.5
        for y in range(int(borda), int(quilla) + 1):
            f = (y - borda) / max(1, quilla - borda)
            col = P["madera"][0] if f < 0.25 else (P["madera"][1] if f < 0.75 else P["madera"][2])
            if int(y - borda) == 2: col = P["madera"][2]
            pon(px, x, y, col)
    sombra_suelo(im, x0 + 4, x1 + 8, yb, yb + 3, P, 0.55)
    for (xa, ya, xb, yb_) in ((346, 69, 356, 55), (350, 69, 361, 56)):
        d.line([xa, ya, xb, yb_], fill=P["madera"][1])
    for xx in (384, 396):
        d.line([xx, 50, xx, 70], fill=P["madera"][1])
    for x in range(384, 397):
        y0 = 51 + 2.2 * math.sin(math.pi * (x - 384) / 12)
        for y in range(int(y0), 64):
            if (x + y) % 2 == 0 and (y - y0) < 10 - abs(x - 390) * 0.3:
                pon(px, x, y, P["red"])


def lampara_barro(im, x, y, P):
    """La lámpara de aceite de barro, de pico, en el umbral."""
    px = im.load()
    for k, w_ in enumerate((3, 3, 2)):
        for xx in range(int(x - w_), int(x + w_) + 1):
            pon(px, xx, y - k, P["barro"][1] if xx < x else P["barro"][2])
    pon(px, x + 3, y - 2, P["barro"][2])


def llama_chica(px, x, y, P, rnd):
    """Una llama de lámpara de 3 pixeles que titila."""
    alto = rnd.choice((2, 3, 3))
    pon(px, x, y, P["llama"][1])
    pon(px, x, y - 1, P["llama"][0])
    if alto == 3:
        pon(px, x + rnd.choice((-1, 0, 0)), y - 2, P["llama"][1])


# ---------------------------------------------------------------------
#  Galia: el oppidum arverno en su colina y el robledal (docs/diseno/temas/galia.md, `TrazosGalia`)
# ---------------------------------------------------------------------
GAL_SOL = (150, 11)       # el sol de la mañana, bajo y a la izquierda: de ahí viene la luz (y la del gato)
GAL_LUNA = (318, 8)       # la luna, sobre el oppidum
GAL_RIO = 50              # donde empieza el río, al pie de la colina

PAL_GAL_D = dict(
    cielo=(hx("BCCFCB"), hx("C9D6CB"), hx("D8DDCA"), hx("E6E1C6"), hx("EEE2BE")),
    sol=hx("FFF8E4"), sol_halo=hx("F6E6B8"), nube=(hx("F6F1E2"), hx("E4E1D2"), hx("C9CABB")), nube_sol=hx("F8E4AE"),
    puy=(hx("9DA99F"), hx("8C998F"), hx("B3BCAE")), llano=(hx("A9B08A"), hx("9DA57E"), hx("B9B48A")),
    colina=(hx("98A464"), hx("7F8D52"), hx("67753F"), hx("525F33")), campo=(hx("C9B47A"), hx("B8A46A"), hx("A8955C")),
    seto=hx("4E5C30"), senda=(hx("CDB585"), hx("B49C6C")),
    piedra=(hx("ABA180"), hx("9D9373"), hx("B6AC8A"), hx("928A6A")), junta=hx("6E654C"), piedra_luz=hx("C8BF9C"),
    viga=(hx("4A3822"), hx("6B5232")), tierra=(hx("8E9A5C"), hx("737F48")), estaca=(hx("8A6A42"), hx("6B4B2A"), hx("4E361E")),
    paja=(hx("E2CE92"), hx("D0B97A"), hx("B89A62"), hx("8E7444")), muro_casa=(hx("E2D4AA"), hx("C9B98A")),
    hueco=(hx("3E3226"), hx("2A2119")), humo=(hx("ACA798"), hx("9E9A8C")), niebla=hx("E2E5DD"),
    rio=(hx("C4D2C8"), hx("AFC3BB"), hx("93ADA6"), hx("7A9890"), hx("67877F")), rio_luz=hx("E9EFE6"), rio_osc=hx("5A7870"),
    reflejo=hx("7F8C5E"), orilla=(hx("8A8456"), hx("6E6C44")), junco=(hx("9DA45E"), hx("7A8446"), hx("B9B07A")),
    copa=(hx("A6B86A"), hx("7A9248"), hx("4E6B2E"), hx("2E4219"), hx("223214")), copa_lejos=(hx("8C9C6A"), hx("74865A"), hx("62744C")),
    tronco=(hx("8E7A58"), hx("6E5C42"), hx("4E3F2E"), hx("33291E")), musgo=hx("7E9046"),
    muerdago=(hx("C2CF7E"), hx("9DB560"), hx("6F8A3C")), baya=hx("F2EEDC"),
    hierba=(hx("A8B068"), hx("8C9A50"), hx("6E7E3C"), hx("56652E")), camino=(hx("D2BC8C"), hx("C4AC7C"), hx("B39A6A"), hx("9C845A")),
    hoja_caida=(hx("B98A3A"), hx("94682C")), guijarro=(hx("D8D0B8"), hx("B4AA90"), hx("857C64")), sombra=hx("8C7A56"),
    valla=(hx("A88A5A"), hx("7E6440"), hx("5A4428")), bronce=(hx("D8A868"), hx("B98A4C"), hx("9A6A3A"), hx("6A4626")),
    verdin=hx("5F8C76"), hierro=(hx("7A7C7E"), hx("5A5C5E"), hx("3A3C3E")), ascua=(hx("E86A2A"), hx("A33A1E")),
    llama=(hx("FFF1B0"), hx("F2A040"), hx("D0602A")), llama_halo=hx("FFD28A"),
    roma=(hx("A33A2A"), hx("C9C2AC"), hx("9C9682")), figura=(hx("3A3226"), hx("8E3B2A")),
)
PAL_GAL_N = dict(
    cielo=(hx("0F1410"), hx("121813"), hx("161D17"), hx("1A221B"), hx("1F2820")),
    luna=hx("E9E6D6"), luna_halo=hx("A9C795"), estrella=(hx("E6E1CF"), hx("A2A78F"), hx("6E7666")),
    nube=(hx("2E3A30"), hx("242E26"), hx("1C241E")), nube_sol=hx("4E5C4A"),
    puy=(hx("1C251E"), hx("18201A"), hx("2A362C")), llano=(hx("18211A"), hx("151D17"), hx("1E281F")),
    colina=(hx("27321F"), hx("202A1B"), hx("1A2317"), hx("141C12")), campo=(hx("2E3324"), hx("282D20"), hx("23281C")),
    seto=hx("10170E"), senda=(hx("3A3A2C"), hx("2E2E24")),
    piedra=(hx("34372C"), hx("2E3128"), hx("3A3D31"), hx("2A2C24")), junta=hx("1A1C16"), piedra_luz=hx("4A4E3E"),
    viga=(hx("15120C"), hx("241E14")), tierra=(hx("222B1C"), hx("1C2418")), estaca=(hx("3A2E1E"), hx("2A2016"), hx("1A140E")),
    paja=(hx("4A4430"), hx("3C3726"), hx("2E2A1E"), hx("221F16")), muro_casa=(hx("3E3A2C"), hx("302D22")),
    hueco=(hx("0C0E0A"), hx("080A07")), humo=(hx("3A403A"), hx("2E342E")), niebla=hx("4A5648"),
    rio=(hx("1C2622"), hx("18221E"), hx("141E1A"), hx("111A17"), hx("0E1714")), rio_luz=hx("C9D2B8"), rio_osc=hx("0A110E"),
    reflejo=hx("1A2418"), orilla=(hx("1E2618"), hx("161D12")), junco=(hx("2E3A24"), hx("222C1A"), hx("3E4A30")),
    copa=(hx("3A4E2E"), hx("26361E"), hx("1A2614"), hx("12190E"), hx("0C120A")), copa_lejos=(hx("1C2619"), hx("172015"), hx("131A11")),
    tronco=(hx("3A3626"), hx("2A271C"), hx("1E1C14"), hx("14120D")), musgo=hx("2A3420"),
    muerdago=(hx("34422A"), hx("2A3622"), hx("1F2A19")), baya=hx("7E8672"),
    hierba=(hx("2E3A22"), hx("26301C"), hx("1E2717"), hx("172012")), camino=(hx("3E3E2E"), hx("363628"), hx("2E2E22"), hx("26261C")),
    hoja_caida=(hx("3E3220"), hx("30261A")), guijarro=(hx("4A4E40"), hx("363A2E"), hx("222620")), sombra=hx("12160E"),
    valla=(hx("3A3222"), hx("2C2619"), hx("1E1A12")), bronce=(hx("C9904A"), hx("8A5E30"), hx("5E3E20"), hx("3A2614")),
    verdin=hx("2E4A3E"), hierro=(hx("4A4C4E"), hx("2E3032"), hx("1A1C1E")), ascua=(hx("FF8A3A"), hx("C84A1E")),
    llama=(hx("FFF1B0"), hx("F2A040"), hx("D0602A")), llama_halo=hx("FFD28A"),
    roma=(hx("E58A5C"), hx("FFC59A"), hx("8A4A30")), figura=(hx("0A0C08"), hx("2A1E14")),
)


def _tono_luz(nx, ny, o):
    """Cuánta luz recibe una superficie con esa normal (en el plano): de día, el sol bajo a la izquierda; de noche, la
    luna arriba a la derecha (solo deja un filo)."""
    lx, ly = (-0.62, -0.78) if not o else (0.5, -0.86)
    return nx * lx + ny * ly


def masa_hojas(im, blobs, P, o, semilla, tonos="copa", luna_filo=True, huecos=0.0):
    """Follaje de roble como una sola masa: los bultos (x, y, r) se juntan, el borde se deshilacha con dos ruidos (los
    grumos grandes de ramas y los chicos de hojas lobuladas) y la luz sale de las normales de esa masa: el sol bajo a la
    izquierda de día; de noche casi todo en sombra con el filo de la luna arriba a la derecha. Lo de abajo de cada copa,
    a la sombra de lo de arriba. `huecos` abre claros por donde se ve el cielo (y las ramas)."""
    px = im.load()
    T_ = P[tonos]
    yy, xx = np.mgrid[0:H, 0:W].astype(np.float32)
    v = np.full((H, W), -2.0, np.float32)
    debajo = np.zeros((H, W), np.float32)
    for (bx, by, r) in blobs:
        q = 1 - np.hypot(xx - bx, (yy - by) * 1.12) / r
        mejor = q > v
        debajo = np.where(mejor, np.clip((yy - by) / r, -1, 1), debajo)
        v = np.maximum(v, q)
    n1 = ruido((H, W), semilla, (5, 5)) - 0.5
    n2 = ruido((H, W), semilla + 50, (2, 2)) - 0.5
    f = v + 0.42 * n1 + 0.26 * n2
    m = f > 0.02
    if huecos:
        m &= ~((n1 > 0.5 - huecos) & (v > 0.25) & (n2 > -0.1))
    alto = _difuso(np.clip(f * 3, 0, 1), 1.4) + 0.35 * n2 + 0.2 * n1
    gy, gx = np.gradient(alto)
    lx, ly = (-0.62, -0.78) if not o else (0.5, -0.86)
    sh = -(gx * lx + gy * ly) * 2.4 - 0.55 * debajo + 0.15
    if not o:
        cortes = (0.42, 0.02, -0.38, -0.75)
        idx = np.digitize(-sh, [-c for c in cortes])          # 0 = lo más claro
        d4 = (np.array([[0, 8, 2, 10], [12, 4, 14, 6], [3, 11, 1, 9], [15, 7, 13, 5]]) + 0.5) / 16
        cerca = np.abs(sh[..., None] - np.array(cortes)[None, None, :]).min(2) < 0.06
        idx = np.where(cerca & (d4[yy.astype(int) % 4, xx.astype(int) % 4] > 0.5), np.minimum(idx + 1, 4), idx)
    else:
        idx = np.where(sh > 0.85, 0, np.where(sh > 0.55, 1, np.where(sh > -0.3, 2, 3)))
        if not luna_filo:
            idx = np.maximum(idx, 2)
    idx = np.minimum(idx, len(T_) - 1)
    ys, xs = np.nonzero(m)
    for y, x in zip(ys, xs):
        px[int(x), int(y)] = T_[int(idx[y, x])]
    return m


def rama(d, pts, ancho, col, luz_=None):
    """Una rama: polilínea suave que se afina; el filo de arriba con luz."""
    c = g8.catmull(pts, 8)
    n = len(c)
    for i, ((xa, ya), (xb, yb)) in enumerate(zip(c, c[1:])):
        w = max(1, round(ancho * (1 - 0.75 * i / n)))
        d.line([xa, ya, xb, yb], fill=col, width=w)
        if luz_ is not None and w >= 2:
            d.point((round(xa), round(ya - w / 2)), fill=luz_)


def roble_grande(im, P, o):
    """El roble grande de la izquierda, que enmarca: tronco ancho con su corteza agrietada, raíces que muerden el suelo,
    musgo del lado de la sombra y la copa arriba, con dos ramas largas que cruzan el cielo hacia la derecha; en una de
    ellas, la mata de muérdago con sus bayas."""
    px = im.load(); d = ImageDraw.Draw(im)
    t1, t2, t3, t4 = P["tronco"]
    # Ramas (antes que la copa: se ven entre los huecos).
    rama(d, [(30, 20), (52, 12), (84, 9), (118, 8), (142, 4)], 4, t3, t2)
    rama(d, [(28, 22), (60, 20), (92, 22), (118, 19)], 3, t3, t2)
    rama(d, [(24, 18), (14, 8), (8, 0)], 4, t3, t2)
    rama(d, [(84, 9), (96, 3), (102, 0)], 2, t3)
    # El tronco: se ensancha abajo en contrafuertes; la luz de la izquierda; la corteza en costillas verticales con sus
    # grietas; el musgo del lado de la sombra.
    for y in range(12, 77):
        u = (y - 12) / 64
        cx = 27 + 1.6 * math.sin(y * 0.09)
        hw = 4.4 + 1.8 * u + (4.0 * ((u - 0.8) / 0.2) ** 1.6 if u > 0.8 else 0)
        for x in range(int(cx - hw), int(cx + hw) + 1):
            f = (x - (cx - hw)) / (2 * hw + 0.01)
            costilla = math.sin((x - cx) * 1.55 + 0.6 * math.sin(y * 0.35 + x * 0.2))
            if not o:
                col = t1 if f < 0.2 else (t2 if f < 0.52 else (t3 if f < 0.84 else t4))
                if costilla > 0.55 and f < 0.75: col = t1 if f < 0.45 else t2
                elif costilla < -0.6: col = t3 if f < 0.4 else t4
                if f > 0.62 and y > 34 and (math.sin(y * 0.7 + x) > 0.2 or f > 0.85) and costilla > -0.6:
                    col = P["musgo"] if (x + y) % 2 else mezcla(P["musgo"], t3, 0.4)
            else:
                col = t3 if f < 0.7 else t4
                if costilla < -0.6: col = t4
                if f > 0.86 and y < 60: col = P["copa"][1]                      # filo de luna
            pon(px, x, y, col)
    # Raíces.
    for (x0, x1, y1) in ((20, 6, 76), (34, 50, 77), (24, 16, 78), (31, 40, 79)):
        rama(d, [(x0, 70), ((x0 + x1) / 2, (70 + y1) / 2 + 1), (x1, y1)], 3, t3 if not o else t4, t2 if not o else None)
    # La copa: bultos a lo largo de las ramas.
    blobs = [(6, 2, 17), (28, -2, 16), (50, 4, 13), (72, 2, 12), (94, 6, 11), (116, 2, 9), (134, 0, 6),
             (14, 15, 10), (40, 15, 8), (64, 14, 7), (106, 15, 5), (2, 22, 6)]
    masa_hojas(im, blobs, P, o, 3, huecos=0.14)
    # La mata de muérdago colgada de la rama: bola de hojas amarillentas y bayas blancas.
    mx, my = 94, 13
    for y in range(my - 4, my + 5):
        for x in range(mx - 5, mx + 6):
            dd = ((x - mx) / 5.2) ** 2 + ((y - my) / 4.4) ** 2
            if dd < 1 and (dd < 0.7 or (x + y) % 2 == 0):
                pon(px, x, y, P["muerdago"][0] if (x - mx) + (y - my) < -2 else (P["muerdago"][1] if dd < 0.55 else P["muerdago"][2]))
    for (dx, dy) in ((-2, -1), (1, 0), (3, -2), (-1, 2), (2, 2), (-3, 1)):
        pon(px, mx + dx, my + dy, P["baya"])
    d.line([mx, my - 5, mx + 1, my - 4], fill=t3)


def roble_derecho(im, P, o):
    """Un roble joven en el borde derecho: el tronco y unas ramas con hojas que asoman por arriba (cierra el cuadro)."""
    px = im.load(); d = ImageDraw.Draw(im)
    t1, t2, t3, t4 = P["tronco"]
    for y in range(0, 66):
        cx = 405 + 0.8 * math.sin(y * 0.15)
        for x in range(int(cx - 2.5), int(cx + 3.5)):
            f = (x - (cx - 2.5)) / 6
            pon(px, x, y, (t2 if f < 0.35 else (t3 if f < 0.8 else t4)) if not o else (t4 if f > 0.3 else P["copa"][2]))
    rama(d, [(404, 14), (390, 8), (372, 6), (360, 2)], 3, t3)
    rama(d, [(406, 26), (396, 22), (386, 23)], 2, t3)
    masa_hojas(im, [(400, 0, 13), (378, 3, 10), (360, 0, 6), (412, 15, 9), (392, 19, 6)], P, o, 9, huecos=0.1)
    for (x0, x1) in ((398, 392), (408, 414)):
        rama(d, [(x0 + 4, 62), ((x0 + x1) / 2 + 2, 65), (x1, 67)], 2, t3 if not o else t4)


def cielo_galia(im, P, o):
    px = im.load(); d = ImageDraw.Draw(im)
    degradado(im, 0, 44, P["cielo"])
    if not o:
        luz(im, GAL_SOL[0], GAL_SOL[1] + 4, 110, P["sol_halo"], 0.32, 6, ry=34)
        luz(im, GAL_SOL[0], GAL_SOL[1], 18, P["sol"], 0.55, 4, ry=14)
        d.ellipse([GAL_SOL[0] - 4, GAL_SOL[1] - 4, GAL_SOL[0] + 4, GAL_SOL[1] + 4], fill=P["sol"])
        # Nubes bajas y largas de la mañana, con el filo que da al sol encendido.
        for (x0, y0, w, s_) in ((178, 9, 46, 1), (238, 5, 30, 2), (300, 12, 58, 3), (352, 4, 24, 4), (200, 18, 22, 5)):
            nube_galia(px, x0, y0, w, P, s_, o)
    else:
        rnd = random.Random(23)
        for _ in range(150):
            x, y = rnd.randrange(W), rnd.randrange(0, 34)
            px[x, y] = P["estrella"][rnd.choice((0, 1, 1, 2, 2, 2))]
        luz(im, GAL_LUNA[0], GAL_LUNA[1], 70, P["luna_halo"], 0.12, 5, ry=30)
        luz(im, GAL_LUNA[0], GAL_LUNA[1], 12, P["luna"], 0.25, 3)
        d.ellipse([GAL_LUNA[0] - 4, GAL_LUNA[1] - 4, GAL_LUNA[0] + 4, GAL_LUNA[1] + 4], fill=P["luna"])
        for (dx, dy) in ((1, -1), (-2, 1), (2, 2)):                          # los mares de la luna
            pon(px, GAL_LUNA[0] + dx, GAL_LUNA[1] + dy, hx("C9C6B4"))
        pon(px, GAL_LUNA[0] - 1, GAL_LUNA[1] - 2, hx("D6D3C2"))
        for (x0, y0, w, s_) in ((178, 12, 40, 1), (352, 16, 30, 4), (250, 3, 26, 2)):
            nube_galia(px, x0, y0, w, P, s_, o)


def nube_galia(px, x0, y0, w, P, semilla, o):
    """Nube baja y alargada: bultos sobre una base chata, la panza en sombra y el filo de arriba encendido (el sol de
    la izquierda, o la luna)."""
    rnd = random.Random(semilla)
    pint = set()
    for k in range(0, w, 2):
        u = k / w
        r = 1.4 + 2.6 * math.sin(math.pi * u) ** 0.8 * rnd.uniform(0.7, 1.1)
        bx, by = x0 + k, y0 - r * 0.3
        for yy in range(int(by - r), int(y0) + 1):
            for xx in range(int(bx - r), int(bx + r) + 1):
                if ((xx - bx) / r) ** 2 + ((yy - by) / (r * 0.65)) ** 2 < 1:
                    pint.add((xx, yy))
    for (xx, yy) in pint:
        if (xx, yy - 1) not in pint:
            col = P["nube_sol"] if (not o and xx < x0 + w * 0.6) or (o and xx > x0 + w * 0.4) else P["nube"][0]
        elif (xx, yy + 1) not in pint:
            col = P["nube"][2]
        else:
            col = P["nube"][1] if (xx, yy + 2) not in pint else P["nube"][0]
        borde = (xx - 1, yy) not in pint or (xx + 1, yy) not in pint
        pon(px, xx, yy, col, 0.55 if borde else (0.9 if not o else 0.8))


def lejania_galia(im, P, o):
    """Los puys en la bruma (conos y cúpulas volcánicas, la tierra de los arvernos), con la ladera que da al sol más
    clara, y el llano a la derecha: franjas de prados y campos, setos con árboles sueltos, más pálidos cuanto más lejos."""
    px = im.load()
    conos = [(332, 31, 18, 13), (360, 33, 12, 8), (392, 30, 21, 15), (150, 31, 18, 10), (176, 33, 12, 6), (282, 33, 16, 7)]
    for (cx, base, w, alto) in conos:
        for x in range(cx - w, cx + w + 1):
            u = (x - cx) / w
            top = base - alto * (1 - u * u) ** (1.3 if cx != 332 else 0.6) + 6
            for y in range(int(top), 44):
                if not o:
                    col = P["puy"][2] if u < -0.1 else (P["puy"][0] if u < 0.45 else P["puy"][1])
                    if y - top > 4: col = mezcla(col, P["llano"][2], 0.3)
                else:
                    col = P["puy"][2] if (u > 0.2 and y - top < 1.5) else P["puy"][0]
                pon(px, x, y, col)
    for x in range(390, 399):                                          # un cráter mordido (como el del Pariou)
        pon(px, x, int(22 + 0.5 * abs(x - 394)), P["puy"][1])
    # El llano.
    for y in range(37, 50):
        for x in range(300, W):
            v = (y - 37) / 13
            franja = int((y + 0.04 * x) / 2.2)
            c_ = hash((franja, int((x + franja * 13) / (18 + franja % 4 * 6)))) % 6
            if not o:
                col = (P["llano"][2], P["llano"][0], P["campo"][0], P["llano"][1], P["llano"][0], P["campo"][1])[c_]
                col = mezcla(col, P["puy"][0], max(0.0, 0.5 - v * 0.6))           # la bruma de lejos
            else:
                col = P["llano"][c_ % 3]
            px[x, y] = col
    rnd = random.Random(61)
    for (y, xa, xb) in ((40, 300, 352), (44, 300, 412), (47, 320, 412)):     # setos con árboles sueltos
        for x in range(xa, xb):
            if (x * 7 + y) % 5 < 3: pon(px, x, y, P["seto"], 0.35 if not o else 0.6)
            if rnd.random() < 0.06:
                pon(px, x, y - 1, P["seto"], 0.6 if not o else 0.8); pon(px, x + 1, y - 1, P["seto"], 0.45 if not o else 0.8)
                pon(px, x, y - 2, P["copa_lejos"][1] if not o else P["seto"], 0.6)


def campamento_romano(im, P, o):
    """El campamento romano, lejos en el llano: el terraplén de delante y las tiendas de cuero en hileras (de noche
    solo se ven sus hogueras en fila). Solo es Roma: su rojo y el gris del hierro."""
    px = im.load()
    if o:
        return
    x0, x1 = 354, 398
    for x in range(x0, x1 + 1):
        pon(px, x, 43, P["roma"][2], 0.55); pon(px, x, 44, P["seto"], 0.45)
    for y, dx in ((40, 0), (42, 1)):
        for x in range(x0 + 2 + dx, x1 - 1, 3):
            if hash((x, y)) % 6 == 0: continue
            pon(px, x, y, P["roma"][1]); pon(px, x + 1, y, P["roma"][2])
    pon(px, 376, 39, P["roma"][0]); pon(px, 376, 38, P["hierro"][1], 0.6)     # el estandarte junto a la tienda del jefe
    for x in range(300, x0, 2):                                           # el cerco que sale hacia la colina
        pon(px, x, 45 + (x0 - x) // 26, P["roma"][2], 0.3)


def colina_galia(im, P, o):
    """La colina del oppidum: una meseta de laderas con prados, campos y setos; la ladera de la izquierda, al sol. Un
    camino sube en zigzag hasta la puerta."""
    px = im.load()
    perfil_ = []
    for x in range(W):
        if x < 150:
            y = 60.0
        elif x < 186:
            u = (x - 150) / 36
            y = 52 - 26.5 * (1 - (1 - u) ** 2.2)
        elif x < 306:
            y = 25.5 + 0.6 * math.sin(x / 13.0)
        elif x < 350:
            u = (x - 306) / 44
            y = 26 + 26 * u ** 1.5
        else:
            y = 60.0
        perfil_.append(y)
    suave = [sum(perfil_[min(W - 1, max(0, x + k))] for k in range(-4, 5)) / 9 for x in range(W)]
    for x in range(148, 352):
        top = perfil_[x]
        pend = (suave[min(W - 1, x + 3)] - suave[max(0, x - 3)]) / 6
        for y in range(int(top), 56):
            prof = y - top
            luz_ = -pend * 1.4 - 0.04 * max(0, prof - 6)
            banda = int((prof + 0.6 * math.sin(x / 9.0)) / 3.6)
            trozo = int((x + banda * 7) / (13 + banda % 3 * 4))
            campo = hash((banda, trozo, 7)) % 7
            ladera = prof > 1 and (x < 186 or x > 306)
            seto_ = ladera and campo % 2 == 0 and int((prof + 0.6 * math.sin(x / 9.0)) * 10) % 36 < 4 and x % 2 == 0
            if not o:
                i_ = 0 if luz_ > 0.55 else (1 if luz_ > -0.25 else (2 if luz_ > -0.9 else 3))
                if abs(luz_ - (0.55, -0.25, -0.9)[min(i_, 2)]) < 0.12 and tramar(0.5, x, y) and i_ < 3: i_ += 1
                col = P["colina"][i_]
                if ladera and campo in (0, 3):
                    col = P["campo"][min(2, (campo == 3) + (i_ >= 2))]
                elif ladera and campo == 5:
                    col = P["colina"][max(0, i_ - 1)]
                if seto_:
                    col = P["seto"]
            else:
                col = P["colina"][0] if (prof == 0 and pend > -0.1) else (P["colina"][1] if luz_ > -0.6 else P["colina"][2])
                if ladera and campo in (0, 3) and prof > 2:
                    col = P["campo"][0]
                if seto_:
                    col = mezcla(col, P["seto"], 0.5)
            px[x, y] = col
    # Árboles sueltos y matas en las laderas: una copita con su lado al sol y su sombra a la derecha.
    for (x, dp_) in ((156, 3), (163, 6), (171, 2), (176, 9), (168, 13), (159, 16), (312, 4), (322, 8), (330, 3), (338, 12), (316, 15), (344, 6)):
        y = int(perfil_[x]) + dp_
        for (dx, dy, k) in ((0, 0, 1), (1, 0, 2), (-1, 0, 1), (0, -1, 0), (1, -1, 1), (0, 1, 2), (1, 1, 3)):
            pon(px, x + dx, y + dy, P["copa"][k] if not o else P["copa"][min(4, k + 2)])
        pon(px, x + 2, y + 1, P["colina"][3], 0.5)
    for x in range(186, 306):
        pon(px, x, 27 + (x % 3 == 0), P["colina"][3], 0.5)
    # El camino que sube a la puerta.
    camino = [(172, 52), (198, 47), (182, 40), (212, 34), (236, 29.5), (244, 27.5)]
    c = g8.catmull(camino, 8)
    for (x, y) in c:
        pon(px, x, y, P["senda"][0]); pon(px, x + 1, y, P["senda"][1])
    return perfil_


def murus_gallicus(im, P, o):
    """La muralla del oppidum, el murus gallicus: un frente de piedra seca con las cabezas de las vigas en hileras (la
    armazón de madera clavada que lleva por dentro), el terraplén de tierra detrás con su parapeto de estacas y el paso
    de ronda; la puerta con sus muros que se meten hacia dentro y la torre de madera encima."""
    px = im.load(); d = ImageDraw.Draw(im)
    x0, x1, yt, yb = 186, 306, 18, 27
    rnd = random.Random(4)
    # Frente de piedra: hiladas irregulares.
    y = yt
    while y < yb:
        alto = rnd.choice((2, 2, 3))
        x = x0 - rnd.randint(0, 3)
        while x < x1:
            largo = rnd.randint(3, 6)
            tono = rnd.choice(P["piedra"])
            for yy in range(y, min(y + alto, yb)):
                for xx in range(max(x, x0), min(x + largo, x1)):
                    fy, fx = yy - y, xx - x
                    col = P["junta"] if (fy == alto - 1 or fx == 0) else (P["piedra_luz"] if (fy == 0 and fx < largo - 1 and not o) else tono)
                    pon(px, xx, yy, col)
            x += largo
        y += alto
    # Las cabezas de las vigas (madera): en hileras, a tresbolillo; con su sombra abajo.
    for k, yy in enumerate((20, 24)):
        for xx in range(x0 + 3 + (k * 3), x1 - 2, 6):
            if 236 <= xx <= 252: continue
            pon(px, xx, yy, P["viga"][1]); pon(px, xx + 1, yy, P["viga"][0]); pon(px, xx, yy + 1, P["viga"][0])
            pon(px, xx + 1, yy + 1, P["junta"])
    # El terraplén con hierba y el parapeto de estacas encima.
    for x in range(x0, x1):
        pon(px, x, yt - 1, P["tierra"][0]); pon(px, x, yt - 2, P["tierra"][1])
        if x % 2 == 0 and not (236 <= x <= 252):
            for yy in range(yt - 6, yt - 2):
                pon(px, x, yy, P["estaca"][0] if yy > yt - 5 else P["estaca"][1])
            pon(px, x, yt - 7, P["estaca"][2])
        elif not (236 <= x <= 252):
            pon(px, x, yt - 5, P["estaca"][2]); pon(px, x, yt - 3, P["estaca"][1])
    # La puerta: el hueco con la oscuridad del pasillo, los muros que se meten y la torre de madera.
    d.rectangle([239, yt - 1, 249, yb - 1], fill=P["hueco"][0])
    for yy in range(yt, yb):
        pon(px, 238, yy, P["piedra_luz"] if not o else P["piedra"][2]); pon(px, 250, yy, P["junta"])
    for yy in range(yt + 2, yb, 2):
        for xx in range(241, 248):
            if (xx + yy) % 3 == 0: pon(px, xx, yy, P["hueco"][1])
    d.rectangle([241, yt + 4, 247, yb - 1], fill=P["estaca"][2])          # el portón de tablas
    for xx in range(242, 248, 2):
        d.line([xx, yt + 4, xx, yb - 1], fill=P["estaca"][1])
    # La torre de la puerta: postes, la plataforma, el antepecho de tablas y el tejado de paja.
    for xx in (236, 252):
        d.line([xx, 5, xx, yt + 1], fill=P["estaca"][1]); d.line([xx + 1, 5, xx + 1, yt + 1], fill=P["estaca"][2])
    d.rectangle([235, 10, 253, 15], fill=P["estaca"][1])
    for xx in range(236, 253, 2):
        d.line([xx, 10, xx, 15], fill=P["estaca"][0] if not o else P["estaca"][1])
    d.line([235, 15, 253, 15], fill=P["estaca"][2]); d.line([235, 10, 253, 10], fill=P["estaca"][0])
    for yy in range(2, 10):                                                # tejado de paja a cuatro aguas
        w = (yy - 2) * 1.6 + 2
        for xx in range(int(244 - w), int(244 + w) + 1):
            f = (xx - (244 - w)) / (2 * w + 0.01)
            col = P["paja"][0] if f < 0.35 else (P["paja"][1] if f < 0.7 else P["paja"][2])
            if yy == 9: col = P["paja"][3]
            pon(px, xx, yy, col)
    for (fx, fy) in ((222, yt - 4), (281, yt - 4)):                         # los centinelas en el paso de ronda
        pon(px, fx, fy, P["figura"][0]); pon(px, fx, fy + 1, P["figura"][1]); pon(px, fx, fy + 2, P["figura"][0])
        for yy in range(fy - 3, fy + 3):
            pon(px, fx + 1, yy, P["hierro"][1] if not o else P["figura"][0])
    pon(px, 244, 7, P["figura"][0])


def casas_galia(im, P, o):
    """Las casas del oppidum detrás de la muralla: techos de paja de dos y cuatro aguas que asoman, uno grande (la casa
    grande del jefe), con el filo del caballete. Devuelve de dónde sale el humo de cada una."""
    px = im.load()
    casas = [(197, 6, 9, 3), (213, 7, 8, 4), (228, 5, 8, 3), (263, 4, 9, 4), (283, 1, 12, 6), (300, 7, 6, 2), (205, 9, 6, 2),
             (274, 8, 6, 2)]
    humos = []
    for (cx, top, w, cumbre) in sorted(casas, key=lambda c: c[1]):
        base = 15
        for y in range(top, base):
            u = (y - top) / max(1, base - top)
            hw = cumbre + (w - cumbre) * u ** 0.85
            for x in range(int(round(cx - hw)), int(round(cx + hw)) + 1):
                f = (x - (cx - hw)) / (2 * hw + 0.01)
                lado = x - cx < -cumbre * (1 - u) - 0.5                     # el faldón de la punta, que da al sol
                col = P["paja"][0] if lado else (P["paja"][1] if f < 0.75 else P["paja"][2])
                if f > 0.9: col = P["paja"][3]
                if o:
                    col = P["paja"][2] if not (f > 0.75 and u < 0.6) else P["paja"][1]
                elif (x * 2 + y * 3) % 7 == 0:
                    col = P["paja"][min(3, (0 if lado else 1) + 1)]           # las hiladas de la paja
                pon(px, x, y, col)
        for x in range(int(cx - cumbre), int(cx + cumbre) + 1):
            pon(px, x, top, P["paja"][3])                                   # el caballete
        pon(px, int(cx - cumbre) - 1, top + 1, P["paja"][2])
        humos.append((int(cx) + 1, top - 1))
    return humos


def rio_galia(im, P, o, perfil_):
    """El río al pie de la colina: claro donde refleja el cielo lejos, más hondo y verde cerca, con el reflejo de la
    colina, ondas en rayitas y la orilla de enfrente con juncos. Devuelve (y de arriba, y de abajo) por columna."""
    px = im.load()
    arriba = [GAL_RIO + 0.8 * math.sin(x / 31.0) + 0.5 * math.sin(x / 9.0) for x in range(W)]
    abajo = [56.4 + 1.2 * math.sin(x / 27.0 + 1.0) + 0.6 * math.sin(x / 7.7) for x in range(W)]
    for x in range(0, W):
        y0, y1 = int(arriba[x]), int(abajo[x])
        for y in range(y0, y1 + 1):
            v = (y - y0) / max(1, y1 - y0)
            px[x, y] = P["rio"][min(4, int(v * 4.6))]
        # el reflejo de la colina (invertida y quebrada)
        if 150 <= x < 350 and not o:
            alto_r = max(0, (56 - perfil_[x]) * 0.18)
            for y in range(y0 + 1, min(y1, int(y0 + 1 + alto_r))):
                px[x, y] = mezcla(px[x, y], P["reflejo"], 0.3)
        pon(px, x, y0, P["orilla"][0]); pon(px, x, y0 - 1, P["orilla"][1], 0.6)
    rnd = random.Random(29)
    for _ in range(70):                                                    # ondas
        x = rnd.randrange(124, W); y0, y1 = arriba[x], abajo[x]
        y = int(y0 + 1 + rnd.random() * (y1 - y0 - 1))
        col = P["rio_luz"] if rnd.random() < 0.5 else P["rio_osc"]
        for k in range(rnd.randint(1, 3)):
            pon(px, x + k, y, col, 0.4 if not o else 0.35)
    for x in range(124, W, 2):                                             # juncos de la otra orilla
        if (x * 7) % 11 < 5:
            h_ = 1 + (x * 3) % 3
            for k in range(h_):
                pon(px, x, int(arriba[x]) - 1 - k, P["junco"][k % 2])
    return arriba, abajo


def bosque_galia(im, P, o):
    """El robledal a la izquierda, detrás del roble grande: copas lejos en la bruma y más cerca, más oscuras, hasta la
    orilla del río."""
    lejos = [(-4 + 17 * k + (k % 3) * 4, 33 + (k % 2) * 3 - (k % 3), 12 + (k % 3) * 2) for k in range(11)] + [(176, 44, 8)]
    masa_hojas(im, lejos, P, o, 11, "copa_lejos", luna_filo=False)
    cerca = [(4 + 15 * k + (k % 2) * 5, 45 + (k % 3) * 2, 10 + (k % 2) * 2) for k in range(11)] + [(160, 52, 9), (176, 56, 6)]
    masa_hojas(im, cerca, P, o, 12)
    # troncos entre las copas
    px = im.load()
    for (x, y0) in ((8, 47), (52, 46), (84, 47), (118, 48), (146, 54)):
        for y in range(y0, 60):
            pon(px, x, y, P["tronco"][2] if not o else P["tronco"][3]); pon(px, x + 1, y, P["tronco"][3])


def suelo_galia(im, P, o):
    """El primer plano: la orilla de hierba con juncos y, abajo, el camino de tierra por donde anda el gato, con sus
    rodadas, guijarros, hojas de roble caídas y bellotas; matas de hierba en las orillas."""
    px = im.load(); rnd = random.Random(41)
    borde = [58.2 + 0.9 * math.sin(x / 13.0) + 0.5 * math.sin(x / 4.1) for x in range(W)]
    camino_y = [64.5 + 0.8 * math.sin(x / 21.0 + 2) for x in range(W)]
    for x in range(W):
        for y in range(int(borde[x]), H):
            if y < camino_y[x]:
                v = (y - borde[x]) / max(1, camino_y[x] - borde[x])
                col = P["hierba"][0] if v < 0.25 else (P["hierba"][1] if v < 0.7 else P["hierba"][2])
                if tramar(0.35, x, y) and 0.2 < v < 0.3: col = P["hierba"][1]
            else:
                v = (y - camino_y[x]) / (H - camino_y[x])
                col = P["camino"][2] if v < 0.12 else (P["camino"][1] if v < 0.45 else P["camino"][0])
                if tramar(0.5, x, y) and (0.4 < v < 0.5 or 0.1 < v < 0.16): col = P["camino"][1 if v > 0.3 else 2]
                if abs(y - 70.5 - 0.5 * math.sin(x / 17.0)) < 0.6 or abs(y - 77.0 - 0.5 * math.sin(x / 23.0)) < 0.6:
                    col = P["camino"][3]                                      # las rodadas de los carros
            px[x, y] = col
        pon(px, x, int(borde[x]), P["hierba"][3] if (x % 3) else P["hierba"][2])
    for _ in range(300):                                                   # grano de la tierra
        x, y = rnd.randrange(W), rnd.randrange(66, H)
        pon(px, x, y, P["guijarro"][rnd.choice((0, 1, 2))], 0.4)
    for _ in range(36):                                                    # guijarros
        x, y = rnd.randrange(W), rnd.randrange(67, H)
        pon(px, x, y, P["guijarro"][0]); pon(px, x + 1, y, P["guijarro"][1]); pon(px, x + 1, y + 1, P["sombra"], 0.5)
    for _ in range(46):                                                    # hojas de roble caídas
        x, y = rnd.randrange(W), rnd.randrange(64, H - 1)
        c_ = P["hoja_caida"][rnd.randrange(2)]
        pon(px, x, y, c_); pon(px, x + 1, y, c_); pon(px, x + 1, y - 1 + 2 * (x % 2), c_, 0.7)
    for x in range(0, W, 3):                                               # matas de hierba en la orilla del camino
        if (x * 13) % 7 < 3:
            yb = int(camino_y[x])
            for k in range(rnd.randint(2, 4)):
                pon(px, x + (k % 2), yb - k, P["hierba"][1 + (k % 2)])
                pon(px, x - 1 + k, yb - 1 - k // 2, P["hierba"][0], 0.6)
    for x in range(2, W, 5):                                               # juncos en la orilla del río
        if (x * 5) % 9 < 4 and x > 122:
            yb = int(borde[x]) + 1
            for k in range(3 + (x % 3)):
                pon(px, x + (k // 3), yb - k, P["junco"][k % 2])
            pon(px, x + 1, yb - 4 - (x % 3), P["junco"][2] if not o else P["junco"][0])
    return borde, camino_y


def valla_galia(im, P, o):
    """Un seto de varas trenzadas (zarzo) entre postes, en la orilla, del lado derecho."""
    px = im.load()
    x0, x1 = 300, 372
    for x in range(x0, x1):
        y_top = 56 + int(1.5 * math.sin((x - x0) / 12.0))
        for y in range(y_top, 64):
            fila = (y - y_top) // 2
            delante = ((x - x0) // 9 + fila) % 2 == 0                        # la vara pasa por delante o por detrás del poste
            u = ((x - x0) % 9) / 9
            arco = abs(u - 0.5) < 0.38
            if (y - y_top) % 2 == 0:
                col = P["valla"][0] if (delante and arco) else P["valla"][1]
            else:
                col = P["valla"][1] if (delante and arco) else P["valla"][2]
            pon(px, x, y, col)
    for x in range(x0, x1 + 1, 9):
        for y in range(53, 65):
            pon(px, x, y, P["valla"][2]); pon(px, x + 1, y, P["valla"][1])
    sombra_suelo(im, x0 + 3, x1 + 4, 64, 66, P, 0.5)


def hoguera_caldero(im, P, o):
    """El fuego del camino junto al roble: un cerco de piedras, leños y, colgado de las trébedes de hierro, el caldero de
    bronce con su verdín. De día arde bajo; de noche alumbra."""
    px = im.load(); d = ImageDraw.Draw(im)
    fx, fy = 76, 69
    # trébedes
    for (xa, xb) in ((69, 74), (83, 78)):
        d.line([xa, fy + 1, xb, 55], fill=P["hierro"][1])
    d.line([76, 54, 76, 59], fill=P["hierro"][2])
    # el caldero de bronce: ancho y bajo, con el borde vuelto, las dos argollas y el verdín
    cx, cy = 76, 62
    for y in range(cy - 2, cy + 4):
        v = (y - (cy - 2)) / 6
        hw = 5.0 * math.sqrt(max(0.0, 1 - v ** 2.2)) + 0.2
        for x in range(int(round(cx - hw)), int(round(cx + hw)) + 1):
            f = (x - (cx - hw)) / (2 * hw + 0.01)
            col = P["bronce"][1] if f < 0.25 else (P["bronce"][2] if f < 0.7 else P["bronce"][3])
            if f < 0.12 and v < 0.6 and not o: col = P["bronce"][0]
            if (x * 2 + y) % 7 == 0 and f > 0.45 and not o: col = P["verdin"]
            pon(px, x, y, col)
    d.line([cx - 6, cy - 3, cx + 6, cy - 3], fill=P["bronce"][0] if not o else P["bronce"][2])
    d.line([cx - 5, cy - 2, cx + 5, cy - 2], fill=P["bronce"][3])
    pon(px, cx - 6, cy - 1, P["hierro"][1]); pon(px, cx + 6, cy - 1, P["hierro"][1])
    d.line([cx - 6, cy - 3, cx, 57], fill=P["hierro"][1]); d.line([cx + 6, cy - 3, cx, 57], fill=P["hierro"][1])
    # el cerco de piedras y los leños
    for k, x in enumerate(range(fx - 7, fx + 8, 2)):
        pon(px, x, fy + (k % 2), P["guijarro"][1]); pon(px, x + 1, fy + (k % 2), P["guijarro"][2])
        pon(px, x, fy - 1 + (k % 2), P["guijarro"][0])
    d.line([fx - 5, fy - 1, fx + 4, fy - 2], fill=P["estaca"][2]); d.line([fx - 3, fy - 2, fx + 6, fy - 1], fill=P["estaca"][1])
    sombra_suelo(im, fx - 6, fx + 10, fy + 2, fy + 4, P, 0.5)
    # leña apilada al lado
    for (y, x0, x1) in ((70, 92, 106), (68, 93, 105), (66, 95, 103)):
        for x in range(x0, x1):
            pon(px, x, y, P["estaca"][1] if x % 3 else P["estaca"][2]); pon(px, x, y - 1, P["estaca"][0] if not o else P["estaca"][1])
        pon(px, x0, y, P["paja"][1] if not o else P["estaca"][2]); pon(px, x1 - 1, y - 1, P["paja"][1] if not o else P["estaca"][2])
    sombra_suelo(im, 92, 110, 71, 73, P, 0.5)
    return fx, fy - 2


def fondo_galia(t):
    """Galia: el oppidum arverno en su colina, el mismo de día y de noche. De día, la mañana: el sol bajo a la izquierda,
    nubes largas, los puys en la bruma, la meseta con su murus gallicus (piedra con las cabezas de las vigas, terraplén y
    parapeto de estacas), la puerta con su torre de madera, los techos de paja con su humo, el camino que sube, la niebla
    al pie de la colina, el río con su reflejo, el robledal y el roble grande que enmarca (con su muérdago), un roble
    joven a la derecha, el zarzo, el fuego con el caldero de bronce y el camino de tierra. Lejos, en el llano, el
    campamento romano. De noche: la luna sobre el oppidum, las hogueras de la muralla, la puerta y los techos con la luz
    de abajo; en el llano, las hogueras del cerco romano en fila; el fuego del camino alumbra el roble.
    Lo que se mueve: el humo, la niebla, las hojas que caen, el agua y el fuego (y de noche, las hogueras lejanas)."""
    o = t["oscuro"]
    P = PAL_GAL_N if o else PAL_GAL_D
    im, d = lienzo(P["cielo"][0])
    px = im.load()
    cielo_galia(im, P, o)
    lejania_galia(im, P, o)
    campamento_romano(im, P, o)
    perfil_ = colina_galia(im, P, o)
    humos = casas_galia(im, P, o)
    murus_gallicus(im, P, o)
    # La niebla de la mañana al pie de la colina (de noche, la luna la blanquea apenas).
    for y in range(34, 52):
        for x in range(120, W):
            yc = 44 + 2.2 * math.sin(x / 37.0) + 1.0 * math.sin(x / 13.0 + 1)
            grueso = 5.5 + 2.0 * math.sin(x / 29.0 + 2)
            k_ = (1 - abs(y - yc) / grueso) * (0.6 + 0.25 * math.sin(x / 19.0) + 0.15 * math.sin(x / 7.0 + y * 0.5)) * (0.4 if x > 346 else 1.0)
            if k_ > 0.05:
                px[x, y] = mezcla(px[x, y], P["niebla"], round(min(1, k_) * 4) / 4 * (0.6 if not o else 0.4))
    arriba, abajo = rio_galia(im, P, o, perfil_)
    bosque_galia(im, P, o)
    for y in range(44, 52):                                                # la niebla entre los troncos del robledal
        for x in range(30, 170):
            k_ = (1 - abs(y - 48) / 4.5) * (0.5 + 0.3 * math.sin(x / 11.0))
            if k_ > 0.05:
                px[x, y] = mezcla(px[x, y], P["niebla"], round(min(1, k_) * 4) / 4 * (0.5 if not o else 0.32))
    borde, camino_y = suelo_galia(im, P, o)
    valla_galia(im, P, o)
    roble_derecho(im, P, o)
    if not o:                                                              # las sombras largas de la mañana, a la derecha
        for y in range(70, 78):
            for x in range(30, 74):
                f_ = (x - 30) / 44
                if abs(y - (73.5 + f_ * 1.5)) < 2.2 - f_ * 1.2 and tramar(0.75 - f_ * 0.45, x, y):
                    px[x, y] = mezcla(px[x, y], P["sombra"], 0.55)
        for x in range(372, 412):
            for y in range(65, 69):
                if tramar(0.45 - (x - 372) / 100, x, y): px[x, y] = mezcla(px[x, y], P["sombra"], 0.5)
    fuego = hoguera_caldero(im, P, o)
    roble_grande(im, P, o)
    hogueras = [(204, 11), (292, 11), (262, 12)]                           # en el paso de ronda (y la de la torre)
    if o:
        # Las hogueras del oppidum: su halo sobre la muralla y los techos; la puerta, encendida por dentro.
        for (hx_, hy_) in hogueras:
            luz(im, hx_, hy_, 16, P["llama_halo"], 0.22, 4, ry=8)
        luz(im, 244, 23, 8, P["llama"][1], 0.4, 3, ry=5)
        luz(im, 244, 13, 12, P["llama_halo"], 0.18, 3, ry=6)
        # El cerco romano: su resplandor en el llano.
        luz(im, 375, 42, 30, P["roma"][0], 0.08, 3, ry=4)
        # El fuego del camino: alumbra el roble, el caldero y el suelo.
        luz(im, fuego[0], fuego[1], 40, P["llama_halo"], 0.15, 6, ry=16)
        luz(im, fuego[0], fuego[1] + 4, 26, P["llama"][1], 0.16, 4, ry=6)
        for y in range(30, 76):                                            # el lado del tronco que da al fuego
            cx = 27 + 1.5 * math.sin(y * 0.11)
            hw = 4.0 + 1.6 * (y - 14) / 62
            k_ = round(max(0.0, 0.75 - abs(y - 64) / 26) * 3) / 3 * 0.4
            for x in range(int(cx + hw * 0.45), int(cx + hw) + 1):
                if k_ > 0: pon(px, x, y, P["llama"][2], k_ * (0.6 if x < cx + hw * 0.75 else 1.0))
    # --- Lo que se mueve: se dibuja en cada cuadro sobre la base, y el grano va al final.
    estatico = im.copy()
    pe = estatico.load()
    rnd = random.Random(7)
    gr = np.ones((H, W), np.float32)
    q = np.random.default_rng(37).random((H, W))
    gr[q < 0.07] = 0.955; gr[q > 0.93] = 1.04
    es_rio = lambda x, y: 124 <= x < W and arriba[x] + 1 < y < abajo[x] - 0.5
    destellos = []
    for _ in range(46 if not o else 30):
        x = rnd.randrange(124, W); y = rnd.randint(int(arriba[x]) + 2, int(abajo[x]) - 1)
        if es_rio(x, y) and es_rio(x + 1, y):
            destellos.append((x, y, rnd.choice((10, 16, 20, 32)), rnd.randrange(40)))
    humos_ = [h_ for h_ in humos if h_[0] in (214, 264, 284, 229)] + [(fuego[0] - 1, 57)]
    # Las hojas que caen del roble: (x de salida, y de salida, ciclos por bucle, fase, deriva).
    caen = [(70, 20, 1, 0.0, 14), (104, 22, 1, 0.45, 10), (40, 24, 2, 0.2, 9), (380, 12, 1, 0.7, -8)]
    # Jirones de niebla que pasan despacio: (x, y, largo, desfase).
    jirones = [(140, 45, 22, 0.0), (300, 43, 26, 0.5), (60, 49, 18, 0.25)]
    roma_ = [(x + (hash((x, y)) % 3) - 1, y) for y in (40, 42) for x in range(356, 396, 4)
             if not (373 <= x <= 377) and hash((x, y, 1)) % 5] + [(375, 41)] + \
            [(x, 45 + (352 - x) // 26) for x in range(304, 352, 6)]

    def humo_(f, x0, y0, tk, j, alto=15):
        """Un hilo de humo: bocanadas que suben, se ensanchan, se tuercen con el viento y se deshacen."""
        p_ = f.load()
        for k in range(6):
            ff = ((tk / (BUCLE / 2)) + k / 6 + j * 0.17) % 1.0
            y = y0 - ff * alto
            x = x0 + ff * ff * 11 + 0.9 * math.sin(ff * 5 + j)
            r = 0.7 + ff * 2.0
            a_ = (0.8 if not o else 0.55) * (1 - ff) ** 0.6
            for yy in range(int(y - r), int(y + r) + 1):
                for xx in range(int(x - r - 0.5), int(x + r + 1)):
                    dd = ((xx + 0.5 - x) / (r + 0.3)) ** 2 + ((yy + 0.5 - y) / (r * 0.8 + 0.3)) ** 2
                    if dd < 1 and 0 <= xx < W and 0 <= yy < H:
                        base_ = P["humo"][0] if (xx + 0.5 - x) + (yy + 0.5 - y) < 0 else P["humo"][1]
                        if o and ff < 0.3: base_ = mezcla(base_, P["llama"][2], 0.4)
                        p_[xx, yy] = mezcla(p_[xx, yy], base_, round(a_ * (1 - dd * 0.5) * 4) / 4)

    def mover(f, tk):
        p_ = f.load()
        # El agua.
        for (x, y, per, ph) in destellos:
            if (tk + ph) % per < per // 4:
                pon(p_, x, y, P["rio_luz"]); pon(p_, x + 1, y, P["rio_luz"], 0.5)
        # La niebla que pasa (aparece y se va: el bucle cierra sin salto).
        for (x0, y0, largo, ph) in jirones:
            ff = (tk / BUCLE + ph) % 1.0
            a_ = math.sin(math.pi * ff)
            x = x0 + 26 * ff
            for xx in range(int(x), int(x + largo)):
                for yy, kk in ((y0 - 1, 0.45), (y0, 1.0), (y0 + 1, 0.6)):
                    u = min(1.0, max(0.0, (xx - x) / largo))
                    k_ = round(a_ * math.sin(math.pi * u) ** 0.8 * kk * 3) / 3 * (0.55 if not o else 0.4)
                    if 0 <= xx < W and k_ > 0:
                        p_[xx, yy] = mezcla(p_[xx, yy], P["niebla"], k_)
        # El humo de las casas y del fuego.
        for j, (x0, y0) in enumerate(humos_):
            humo_(f, x0, y0, tk, j, 11)
        # Las hojas que caen, balanceándose.
        for (x0, y0, ciclos, ph, deriva) in caen:
            ff = (tk / BUCLE * ciclos + ph) % 1.0
            if ff > 0.85: continue
            u = ff / 0.85
            y = y0 + u * (68 - y0)
            x = x0 + deriva * u + 2.2 * math.sin(u * 9)
            col = P["hoja_caida"][0] if (int(u * 9) % 2) else P["copa"][1]
            pon(p_, x, y, col)
            pon(p_, x + (1 if int(u * 12) % 2 else 0), y + (0 if int(u * 12) % 2 else 1), col if not o else P["copa"][2])
        # El fuego del camino.
        rnd2 = random.Random(tk // 2 % (BUCLE // 2) * 13 + 1)
        fx, fy = fuego
        alto_ = 2 if not o else 4
        for k in range(-2, 3):
            h_ = rnd2.randint(0, alto_) + (alto_ - abs(k))
            for j in range(h_):
                col = P["llama"][0] if j >= h_ - 1 and h_ > 2 else (P["llama"][1] if j > 0 else P["llama"][2])
                pon(p_, fx + k, fy - j, col)
        for k in range(-4, 5, 2):
            pon(p_, fx + k, fy + 1, P["ascua"][rnd2.randrange(2)])
        if not o:
            return
        # Las hogueras del oppidum, la de la puerta y las del cerco romano.
        for j, (hx_, hy_) in enumerate(hogueras):
            for k in (-1, 0, 1):
                h_ = 1 + rnd2.randint(0, 2) - abs(k)
                for jj in range(max(0, h_)):
                    pon(p_, hx_ + k, hy_ - jj, P["llama"][0] if jj == h_ - 1 else P["llama"][1])
        pon(p_, 244, 21 + rnd2.randint(0, 1), P["llama"][1])
        for j, (x, y) in enumerate(roma_):
            v = (tk // 3 + j * 7) % 11
            pon(p_, x, y, P["roma"][1] if v == 0 else (P["roma"][0] if v < 8 else P["roma"][2]))
        # Las estrellas titilan.
        for j in range(0, 40):
            x, y = (j * 97) % W, (j * 13) % 30
            if pe[x, y] in P["estrella"] and ((tk // 6 + j) % 9) == 0:
                p_[x, y] = P["cielo"][0]

    cache = {}

    def render(tk):
        if tk not in cache:
            f = estatico.copy()
            mover(f, tk)
            a = np.asarray(f, np.float32) * gr[..., None]
            cache[tk] = Image.fromarray(np.clip(a, 0, 255).astype(np.uint8))
        return cache[tk]

    mascaras = {}

    def vivo(img, tick, delante):
        """Pega encima solo lo que cambió respecto del cuadro 0 (así no borra lo que ya haya en la banda)."""
        if delante: return
        tk = tick % BUCLE
        if tk not in mascaras:
            dif = (np.asarray(render(tk)) != np.asarray(render(0))).any(2)
            mascaras[tk] = Image.fromarray((dif * 255).astype(np.uint8)).resize((W * ESC, H * ESC), Image.NEAREST) if dif.any() else None
        if mascaras[tk] is not None:
            img.paste(subir(render(tk)), (0, 0), mascaras[tk])
    ANIM[("galia", o)] = [vivo]
    return subir(render(0))


FONDOS_MIOS = {"frente": fondo_frente, "arcontes": fondo_arcontes, "galia": fondo_galia}
ETIQ = {"frente": "Frente exterior · la base escondida en ruinas", "arcontes": "Arcontes · el lago de Galilea en Magdala",
        "galia": "Galia · el oppidum arverno y su robledal"}
NOCHE = {"frente": "la base en penumbra, con el vidrio y las lámparas encendidos",
         "arcontes": "el mismo lago con las estrellas, la lámpara y una grieta de luz",
         "galia": "el mismo oppidum con sus hogueras y, en el llano, las del cerco romano"}


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
