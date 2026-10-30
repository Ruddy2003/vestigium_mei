"""Lista las ramas de un tema nuevo que siguen iguales (salvo el renombrado) a las del tema fuente.
Uso: python3 iguales.py FUENTE NUEVO fuente nuevo archivos..."""
import re, sys
sys.path.insert(0, '/tmp/claude-0/-home-user-vestigium-mei/b6f44007-b28e-53d1-9d32-f75967fcb140/scratchpad')
SRC, DST, sid, did = sys.argv[1:5]
Sid, Did = sid[0].upper() + sid[1:], did[0].upper() + did[1:]
import importlib.util
spec = importlib.util.spec_from_file_location('d', '/home/user/vestigium_mei/herramientas/lote8/dup_tema.py')
src = open('/home/user/vestigium_mei/herramientas/lote8/dup_tema.py').read()
# reutiliza escanear()
ns = {}
exec(src.split('manual = []')[0].replace('SRC, DST, sid, did = sys.argv[1:5]', 'SRC, DST, sid, did = "A","B","a","b"'), ns)
escanear = ns['escanear']
def cuerpo(s, m):
    resto = s[m.end():]
    mf = re.match(r'\s*(->|to\b)', resto)
    if not mf: return None
    ini = m.end() + mf.end()
    fin = escanear(s, ini, '->' if mf.group(1) == '->' else 'to')
    return s[ini:fin].strip()
for ruta in sys.argv[5:]:
    s = open(ruta).read()
    a = [(m.start(), cuerpo(s, m)) for m in re.finditer(r'TemaId\.' + SRC + r'\b', s)]
    b = [(m.start(), cuerpo(s, m)) for m in re.finditer(r'TemaId\.' + DST + r'\b', s)]
    for (pa, ca), (pb, cb) in zip(a, b):
        if ca is None or cb is None: continue
        ren = ca.replace(SRC, DST).replace(Sid, Did).replace(sid, did)
        if ren == cb:
            print(f'{ruta}:{s.count(chr(10), 0, pb) + 1}: {cb[:110]}')
