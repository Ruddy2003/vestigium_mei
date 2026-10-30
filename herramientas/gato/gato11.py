"""
Prueba 11: el gato con las patas traseras más atrás (de pie y caminando ya no parece sentado) y la grupa menos abultada.
Todo lo demás (fondos, temas) viene de gato10.py, gato9.py y gato8.py.
"""
import inspect
import sys
from pathlib import Path

from PIL import Image, ImageDraw

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8      # noqa: E402
import gato9 as g9      # noqa: E402
import gato10 as g10    # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
g8.AQUI = g9.AQUI = g10.AQUI = AQUI

# Pata trasera nueva: el pie queda detrás de la cadera (antes caía justo debajo, como de gato sentado),
# la rodilla no se adelanta tanto y el corvejón queda más atrás y más alto.
P_TRA = '''def p_tra(top, paw, lift=0.0):
    """Pata trasera de gato de pie: cadera, rodilla (apenas delante), corvejón (bien atrás) y pie detrás de la cadera."""
    paw = (paw[0] - ATRAS, paw[1])
    rod = (top[0] + 0.8 + 0.25 * (paw[0] - top[0]), top[1] + 5.6)
    cor = (paw[0] - 2.0 + 0.3 * lift, paw[1] - 5.6 + 0.4 * lift)
    # Se acerca cada articulación a la recta cadera-pie: la pierna queda en una curva suave, no en zigzag.
    recta = lambda f: (top[0] + (paw[0] - top[0]) * f, top[1] + (paw[1] - top[1]) * f)
    rod = tuple(a + (b - a) * RECTA for a, b in zip(rod, recta(0.36)))
    cor = tuple(a + (b - a) * RECTA for a, b in zip(cor, recta(0.72)))
    return [top, rod, cor, paw]
'''
RECTA = 0.55                                             # 0 = zigzag de antes, 1 = pierna recta
ATRAS = 5.5                                              # dp que el pie trasero se va hacia atrás
GRUPA = dict(rh=6.6, alto=0.8, muslo=(3.2, 3.8))         # grupa más chica y un poco más alta


def rehacer_poses():
    src = Path(g8.__file__).read_text().split("\n")
    a = next(i for i, l in enumerate(src) if l.startswith("def p_tra("))
    b = next(i for i, l in enumerate(src) if l.startswith('for p in ANIMS["dormir"]:')) + 2
    trozo = "\n".join(src[a:b])
    fin_vieja = trozo.index("def pose(")
    trozo = P_TRA + "\n\n" + trozo[fin_vieja:]
    g8.ATRAS = ATRAS; g8.RECTA = RECTA
    exec(trozo, g8.__dict__)
    # Muslo: la masa que une la pierna al cuerpo, más chica y más arriba (menos grupa).
    s = inspect.getsource(g8.dibujar)
    viejo = 'L.elipse("cuerpo", (hip[0] + rod[0]) / 2, (hip[1] + rod[1]) / 2 - 0.8, 4.0, 4.8,'
    assert viejo in s
    mx, my = GRUPA["muslo"]
    exec(s.replace(viejo, f'L.elipse("cuerpo", (hip[0] + rod[0]) / 2 - 0.4, (hip[1] + rod[1]) / 2 - 1.4, {mx}, {my},'), g8.__dict__)
    for k in ("caminar", "quieto", "caminar_triste", "quieto_triste", "enojado", "saltar", "jugar"):
        for p in g8.ANIMS[k]:
            p["rh"] = GRUPA["rh"] + (p["rh"] - 7.4)
            if k in ("caminar", "quieto", "caminar_triste", "quieto_triste"):
                p["H"] = (p["H"][0], p["H"][1] - GRUPA["alto"])
    g8._rej.clear(); g8._spr.clear()


def comparar(archivo):
    t = g8.tema("romano", False)
    filas = [("caminar", 8), ("quieto", 1), ("caminar_triste", 2), ("enojado", 1), ("jugar", 1)]
    cel = 4; lado = g8.N * cel
    sprites = []
    for etapa in ("antes", "después"):
        if etapa == "después":
            rehacer_poses()
        fila = []
        for anim, n in filas:
            for i in range(n):
                fila.append(g8.pintar_plano(g8.rejilla(anim, i, t), t, cel))
        sprites.append(fila)
    n = len(sprites[0])
    img = Image.new("RGB", (130 + n * (lado + 4), 2 * (lado + 10) + 10), (236, 237, 234))
    d = ImageDraw.Draw(img)
    for r, (etapa, fila) in enumerate(zip(("antes", "después"), sprites)):
        y = 10 + r * (lado + 10)
        d.text((10, y + lado // 2 - 8), etapa, font=g8.fuente("DejaVuSans-Bold.ttf", 18), fill=(23, 25, 28))
        for k, s in enumerate(fila):
            x = 130 + k * (lado + 4)
            d.line([x, y + int(g8.SUELO * cel) + cel, x + lado, y + int(g8.SUELO * cel) + cel], fill=(150, 150, 150))
            img.paste(s, (x, y), s)
    img.save(AQUI / archivo)


if __name__ == "__main__":
    que = sys.argv[1:] or ["comparar"]
    if "comparar" in que:
        comparar("antes_despues.png")
    else:
        rehacer_poses()
    if "banda" in que or "todo" in que:
        s_, cs = g8.buscar_semilla()
        for n, o in (("romano", False), ("colonos", False), ("laboratorio", True)):
            et = g9.ETIQ[n] if not o else g9.ETIQ[n].split(" · ")[0] + " · de noche: " + g9.NOCHE[n]
            g8.gif_banda(cs, [(n, o, et)], "_oscuro" if o else "")
    if "hoja" in que or "todo" in que:
        g8.hoja("romano", False, "hoja_sprites_romano.png")
    print("listo")
