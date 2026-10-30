"""
Exporta el gato de la banda de Día a recursos para la app (assets/gato/).

Por cada tema y modo (claro / oscuro):
- <tema>_<c|o>_gato.webp     atlas con todos los cuadros del gato ya pintados con el material del tema (recortados);
- <tema>_<c|o>_fondo.webp    el fondo de la banda (412×80 dp a 3 px por dp);
- <tema>_<c|o>_atras.webp    si el fondo se mueve: un parche por cuadro del bucle (lo que va detrás del gato);
- <tema>_<c|o>_delante.webp  si algo pasa delante del gato (nieve, humo…): parches con transparencia;
- <tema>_<c|o>_post.webp     en las pantallas (ciber, refugio de noche): líneas de escaneo y viñeta que se multiplican encima;
y gato.json con las posiciones de cada cuadro, los nombres de las animaciones y cómo se compone la banda.

Uso: python3 exportar.py <carpeta_destino> [tema ...]
"""
import json
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageChops

sys.path.insert(0, str(Path(__file__).parent))
import gato18 as g18      # noqa: E402,F401  (carga toda la cadena de pruebas con los arreglos)
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato17 as g17      # noqa: E402

ESC = g8.ESC
LADO = g8.N * ESC
BASE = ["caminar", "caminar_triste", "quieto", "quieto_triste", "sentarse", "sentado", "echarse", "dormir", "saltar",
        "asearse", "jugar", "enojado", "girar"]
MAX_BUCLE = 400     # cuadros que se miran para hallar el bucle del fondo (este dura a lo más la mitad: 16 s)


def empacar(cuadros, ancho=1024):
    """Empaca imágenes en estantes. Devuelve el atlas y la posición [x, y] de cada una."""
    x = y = alto = 0
    pos = []
    for im in cuadros:
        if x + im.width > ancho:
            x, y, alto = 0, y + alto + 1, 0
        pos.append((x, y))
        x += im.width + 1
        alto = max(alto, im.height)
    atlas = Image.new("RGBA", (ancho, y + alto), (0, 0, 0, 0))
    for im, p in zip(cuadros, pos):
        atlas.paste(im, p)
    return atlas, pos


def recortar(s):
    """Recorta el cuadro a lo que tiene pixeles. Las pantallas (RGB sobre negro, se suman) pasan a RGBA opaco."""
    if s.mode == "RGB":
        caja = s.getbbox() or (0, 0, 1, 1)
        return s.crop(caja).convert("RGBA"), caja
    caja = s.getchannel("A").getbbox() or (0, 0, 1, 1)
    return s.crop(caja), caja


T = 6     # lado de cada mosaico, en pixeles (2 dp)


def mosaicos(destino, clave, base, atras, todos):
    """Lo que se mueve en el fondo, en mosaicos de T×T px. Para cada cuadro del bucle solo se guardan los mosaicos que
    cambian respecto al fondo base (detrás del gato) y los que se dibujan encima del gato (con transparencia). Los
    mosaicos repetidos se guardan una vez. Archivo <clave>_mov.bin (enteros little-endian):
      int32 bucle, int32 T, int32 columnas de la banda en mosaicos,
      luego, por cuadro: uint16 n_atras, n_atras × (uint16 lugar, uint16 índice), uint16 n_delante, n_delante × (…),
    donde lugar = fila × columnas + columna en la banda e índice es la posición en <clave>_atras.webp / _delante.webp
    (rejillas de 64 mosaicos por fila)."""
    H, W = base.shape[:2]
    cb = -(-W // T)
    unicos = [{}, {}]
    datos = [np.array([len(atras), T, cb], "<i4").tobytes()]
    for k in range(len(atras)):
        capas = []
        cambia = (atras[k] != base).any(2)
        delante = (todos[k] != atras[k]).any(2)
        rgba = np.dstack([todos[k], (delante * 255).astype(np.uint8)])
        for capa, (mask, img) in enumerate(((cambia, atras[k]), (delante, rgba))):
            lista = []
            for ty in range(0, H, T):
                for tx in range(0, W, T):
                    if mask[ty:ty + T, tx:tx + T].any():
                        pedazo = np.zeros((T, T, img.shape[2]), np.uint8)
                        p = img[ty:ty + T, tx:tx + T]
                        pedazo[:p.shape[0], :p.shape[1]] = p
                        clave_ = pedazo.tobytes()
                        idx = unicos[capa].setdefault(clave_, len(unicos[capa]))
                        lista.append(((ty // T) * cb + tx // T, idx))
            capas.append(lista)
        for lista in capas:
            datos.append(np.array([len(lista)], "<u2").tobytes())
            if lista:
                datos.append(np.array(lista, "<u2").tobytes())
    (destino / f"{clave}_mov.bin").write_bytes(b"".join(datos))
    for capa, (sufijo, modo) in enumerate((("atras", "RGB"), ("delante", "RGBA"))):
        if not unicos[capa]:
            continue
        cols = 64
        filas = -(-len(unicos[capa]) // cols)
        im = Image.new(modo, (cols * T, filas * T))
        for b, idx in unicos[capa].items():
            arr = np.frombuffer(b, np.uint8).reshape(T, T, len(modo))
            im.paste(Image.fromarray(arr, modo), ((idx % cols) * T, (idx // cols) * T))
        guardar(im, destino / f"{clave}_{sufijo}.webp")
    return len(unicos[0]), len(unicos[1])


def huellas(cuadros):
    """Una huella por mosaico de cada cuadro, para comparar cuadros rápido."""
    out = []
    pesos = None
    for c in cuadros:
        H, W = c.shape[:2]
        a = c.astype(np.int64)
        if pesos is None:
            pesos = (np.arange(H * W * 3, dtype=np.int64) * 2654435761 % 1000003).reshape(H, W, 3)
        v = (a * pesos).reshape(H // T, T, W // T, T, 3).sum(axis=(1, 3, 4))
        out.append(v)
    return out


def periodo(atras, todos):
    """El bucle del fondo: el periodo más corto (desde 20 cuadros) en que todo cuadro k es igual al k + L; si ninguno es
    exacto, el que deja menos mosaicos distintos, con preferencia por los largos (el salto al volver se nota menos)."""
    ha, ht = huellas(atras), huellas(todos)
    n = len(ha)
    def errores(L):
        m = n - L
        return sum(int((ha[k + L] != ha[k]).sum() + (ht[k + L] != ht[k]).sum()) for k in range(m)) / m
    candidatos = range(20, n // 2 + 1)
    for L in candidatos:
        if errores(L) == 0:
            return L
    return min(range(40, n // 2 + 1), key=lambda L: errores(L) / (1 + L / 150))


def rejilla_parches(parches, modo):
    """Los parches del bucle en una rejilla (el WebP no admite más de 16383 px por lado). El parche k va en
    la columna k % cols y la fila k // cols."""
    w, h = parches[0].size
    cols = max(1, min(len(parches), 4096 // max(w, 1)))
    filas = -(-len(parches) // cols)
    while filas * h > 16383:
        cols += 1; filas = -(-len(parches) // cols)
    im = Image.new(modo, (w * cols, h * filas), (0, 0, 0, 0) if modo == "RGBA" else (0, 0, 0))
    for k, p in enumerate(parches):
        im.paste(p, ((k % cols) * w, (k // cols) * h))
    return im, cols


def guardar(im, ruta):
    im.save(ruta, "WEBP", lossless=True, quality=100, method=6)


def sin_post():
    """Quita las líneas de escaneo y la viñeta (la app las pone encima, también sobre el gato)."""
    g8.lineas_escaneo = lambda img: img
    blanco = {}

    def vin():
        if "b" not in blanco:
            blanco["b"] = Image.new("RGB", (g8.ANCHO_DP * ESC, g8.ALTO_DP * ESC), (255, 255, 255))
        return blanco["b"]
    g9.viñeta = vin
    return vin


def exportar(destino, temas=None):
    destino = Path(destino)
    destino.mkdir(parents=True, exist_ok=True)
    lineas_orig, vin_orig = g8.lineas_escaneo, g8.viñeta_ciber
    blanco = sin_post()
    meta = {"dt": g8.DT, "esc": ESC, "ancho_dp": g8.ANCHO_DP, "alto_dp": g8.ALTO_DP, "suelo_dp": g8.Y_SUELO,
            "lado_dp": g8.N, "temas": {}}
    for n, _, _ in g8.BANDAS:
        if temas and n not in temas:
            continue
        for o in ((True,) if n == "ciber" else (False, True)):
            clave = f"{n}_{'o' if o else 'c'}"
            print(clave, flush=True)
            for viejo_ in destino.glob(f"{clave}_*"):
                viejo_.unlink()
            t = g8.tema(n, o)
            nuevo = n in g9.NUEVOS
            pantalla = (t.get("pantalla", False) if nuevo else n == "ciber")
            m = {"pantalla": pantalla}
            # --- El gato.
            anims = BASE + [f"esp_{n}_0", f"esp_{n}_1"]
            cuadros, info = [], {}
            for a in anims:
                k = 2 if a == "girar" else len(g8.ANIMS[a])
                info[a] = []
                for i in range(k):
                    s = g8.sprite(n, o, t, a, i, False)
                    im, caja = recortar(s)
                    cuadros.append(im)
                    info[a].append([caja[0], caja[1], im.width, im.height])
            atlas, pos = empacar(cuadros)
            j = 0
            for a in anims:
                for c in info[a]:
                    c[0:0] = list(pos[j]); j += 1        # [atlas_x, atlas_y, dx, dy, ancho, alto]
            guardar(atlas, destino / f"{clave}_gato.webp")
            m["anims"] = info
            m["especiales"] = [{"accion": g17.ESP[n][q][0], "nombre": g17.ESP[n][q][2],
                                "base": g17.ACCIONES[g17.ESP[n][q][0]][2]} for q in range(2)]
            # --- Sombra bajo el gato.
            if pantalla:
                col = g8.mezcla(t["reflejo"], (0, 0, 0), 0.8) if nuevo else g8.mezcla(g8.hx("00E5FF"), (0, 0, 0), 0.82)
                m["sombra"] = {"color": "%02X%02X%02X" % col, "alfa": 255, "suma": True}
            elif nuevo:
                a_ = t.get("sombra_a", 55)
                m["sombra"] = {"color": "%02X%02X%02X" % t.get("sombra_col", (20, 20, 24)), "alfa": a_, "suma": False} if a_ else None
            else:
                m["sombra"] = None if n in g8.SIN_SOMBRA else {"color": "141418", "alfa": 40 if n == "navidad" else 55, "suma": False}
            m["ajustar"] = bool(t.get("ajustar")) if nuevo else n in g8.AJUSTAR
            # --- El fondo y lo que se mueve en él.
            fondo = g8.FONDOS[n](t)
            lejos = ("quieto", 0, -1000.0, 1, 0)
            extra = blanco() if n == "ciber" else None
            anim_fondo = g9.ANIM.get((n, o), [])

            def cuadro(tick, con_delante=True):
                guard = {}
                if not con_delante:
                    if nuevo:
                        g9.ANIM[(n, o)] = [(lambda f: (lambda img, tk, d: None if d else f(img, tk, d)))(f) for f in anim_fondo]
                    if n == "navidad":
                        guard["copos"] = g8.EXTRA["navidad"]["copos"]; g8.EXTRA["navidad"]["copos"] = []
                try:
                    return np.asarray(g8.cuadro_banda(n, o, t, fondo, lejos, tick, extra).convert("RGB"))
                finally:
                    if nuevo: g9.ANIM[(n, o)] = anim_fondo
                    if "copos" in guard: g8.EXTRA["navidad"]["copos"] = guard["copos"]

            # Luz de vela (colonos de noche): las máscaras que oscurecen la hoja se multiplican encima de todo.
            # Se exportan aparte (a ¼ de resolución, son degradados suaves) y en la app se elige una por cuadro.
            luz = None
            for f in anim_fondo:
                if f.__closure__ and "mascaras" in f.__code__.co_freevars:
                    celda = f.__closure__[f.__code__.co_freevars.index("mascaras")]
                    if celda.cell_contents:
                        luz = list(celda.cell_contents)
                        celda.cell_contents[:] = [Image.new("RGB", fondo.size, (255, 255, 255))] * len(luz)
            # La retícula del laboratorio sigue al gato: la dibuja la app; aquí solo se guarda su color.
            for f in anim_fondo:
                if f.__closure__ and "hud" in f.__code__.co_freevars and "POS" in f.__code__.co_names:
                    m["reticula"] = "%02X%02X%02X" % f.__closure__[f.__code__.co_freevars.index("hud")].cell_contents
            atras0 = cuadro(0, False)
            guardar(Image.fromarray(atras0), destino / f"{clave}_fondo.webp")
            se_mueve = bool(anim_fondo) or n in ("navidad", "espacio", "ciber")
            m["bucle"] = 0
            if se_mueve:
                atras = [atras0] + [cuadro(k, False) for k in range(1, MAX_BUCLE + 1)]
                todos = [cuadro(k) for k in range(MAX_BUCLE + 1)]
                L = periodo(atras, todos)
                m["bucle"] = L
                n_atras, n_delante = mosaicos(destino, clave, atras0, atras[:L], todos[:L])
                m["mosaicos"] = [n_atras, n_delante]
            if luz:
                for q, im in enumerate(luz):
                    guardar(im.resize((im.width // 4, im.height // 4), Image.BILINEAR), destino / f"{clave}_luz{q}.webp")
                # El mismo orden que en el fondo: (tick // 2 + (tick // 7) % 3) % 4, que se repite cada 168 cuadros.
                m["luz"] = [(k // 2 + (k // 7) % 3) % len(luz) for k in range(168)]
                celda.cell_contents[:] = luz
            if pantalla:
                post = ImageChops.multiply(lineas_orig(Image.new("RGB", fondo.size, (255, 255, 255))), vin_orig())
                guardar(post, destino / f"{clave}_post.webp")
                m["post"] = True
            meta["temas"][clave] = m
    # Si solo se exportaron algunos temas, los demás se quedan como estaban.
    viejo = destino / "gato.json"
    if temas and viejo.exists():
        antes = json.loads(viejo.read_text())["temas"]
        antes.update(meta["temas"])
        meta["temas"] = {k: antes[k] for k in sorted(antes, key=lambda k: list(antes).index(k))}
    viejo.write_text(json.dumps(meta, ensure_ascii=False, separators=(",", ":")))


if __name__ == "__main__":
    exportar(sys.argv[1], sys.argv[2:] or None)
