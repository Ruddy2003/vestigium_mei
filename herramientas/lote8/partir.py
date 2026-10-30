"""Parte las líneas con varias ramas `TemaId.X -> …; TemaId.Y -> …` en una rama por línea (misma sangría)."""
import re, sys
def cortes(l):
    """Índices de los ';' a profundidad 0 (fuera de cadenas) seguidos de ' TemaId.X ->'."""
    d = 0; i = 0; n = len(l); est = []; res = []
    while i < n:
        c = l[i]
        if est and est[-1] == 's':
            if c == '\\': i += 2; continue
            if c == '"': est.pop()
            elif c == '$' and l[i+1:i+2] == '{': est.append('t'); i += 2; continue
            i += 1; continue
        if l.startswith('//', i): break
        if c == '"': est.append('s')
        elif c == "'":
            k = i + 2 if l[i+1] == '\\' else i + 1
            i = l.index("'", k + 1) + 1 if l[k] != "'" else k + 1; continue
        elif c in '({[': d += 1
        elif c in ')]': d -= 1
        elif c == '}':
            if est and est[-1] == 't' and d == 0: est.pop()
            else: d -= 1
        elif c == ';' and d == 0 and not est and re.match(r';\s*TemaId\.[A-Z]+ ->', l[i:]): res.append(i)
        i += 1
    return res
for ruta in sys.argv[1:]:
    ls = open(ruta).read().split('\n'); out = []; cambio = False
    for l in ls:
        m = re.match(r'^(\s*)TemaId\.[A-Z]+ ->', l)
        cs = cortes(l) if m else []
        if cs:
            ind = m.group(1); a = 0; partes = []
            for c in cs: partes.append(l[a:c].rstrip()); a = c + 1
            partes.append(l[a:])
            out.append(partes[0]); out += [ind + p.strip() for p in partes[1:]]; cambio = True
        else: out.append(l)
    if cambio: open(ruta, 'w').write('\n'.join(out))
