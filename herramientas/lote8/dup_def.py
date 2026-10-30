"""Copia una definición de Kotlin (val, fun u object de nivel superior o de un objeto) con otro nombre, justo después.

Uso: python3 dup_def.py archivo.kt NombreFuente NombreNuevo [src dst]
Copia desde la línea de la definición (y su KDoc) hasta que cierran sus paréntesis y llaves. Con [src dst], además
renombra dentro de la copia (src→dst, Src→Dst, SRC→DST).
"""
import re, sys

ruta, a, b = sys.argv[1:4]
ren = sys.argv[4:6]
s = open(ruta).read()
ls = s.split('\n')
pat = re.compile(r'^\s*(?:(?:private|internal|public|override|inline|@Composable)\s+)*(?:val|fun|object|class)\s+(?:[A-Za-z.<>]+\.)?' + re.escape(a) + r'\b')
i = next(k for k, l in enumerate(ls) if pat.match(l))
ini = i
while ini > 0 and (ls[ini - 1].strip().startswith(('/**', '*', '*/', '@')) and not ls[ini - 1].strip() == ''):
    ini -= 1
# fin: balance de () {} [] fuera de cadenas, desde la línea i
d = 0; j = i; vio = False
while True:
    l = re.sub(r'"(\\.|[^"\\])*"', '""', ls[j])
    l = re.sub(r"'(\\.|[^'\\])'", "''", l)
    l = l.split('//')[0]
    for c in l:
        if c in '({[': d += 1; vio = True
        elif c in ')}]': d -= 1
    sig = ls[j + 1].strip() if j + 1 < len(ls) else ''
    if vio and d == 0 and not sig.startswith(('.', ')')):
        break
    if not vio and d == 0 and '=' in l and not l.rstrip().endswith(('=', '(', ',')) and not sig.startswith('.'):
        break
    j += 1
blk = ls[ini:j + 1]
txt = '\n'.join(blk)
txt = re.sub(r'\b' + re.escape(a) + r'\b', b, txt)
if ren:
    x, y = ren
    X, Y = x[0].upper() + x[1:], y[0].upper() + y[1:]
    txt = txt.replace(x.upper(), y.upper()).replace(X, Y).replace(x, y)
ls[j + 1:j + 1] = [''] + txt.split('\n') if ls[j + 1].strip() == '' or True else txt.split('\n')
open(ruta, 'w').write('\n'.join(ls))
print(f'{ruta}: {a} (líneas {ini + 1}-{j + 1}) copiado como {b}')
