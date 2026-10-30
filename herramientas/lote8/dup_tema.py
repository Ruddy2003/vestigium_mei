"""Duplica cada rama `TemaId.<SRC> -> ...` y cada entrada `TemaId.<SRC> to ...` como rama del tema nuevo, justo después.

Uso: python3 dup_tema.py SRC DST src_id dst_id archivo.kt...
El texto copiado se renombra (SRC→DST, src_id→dst_id, Src→Dst). Lo que no sabe copiar lo lista como «manual».
"""
import re, sys

SRC, DST, sid, did = sys.argv[1:5]
Sid, Did = sid[0].upper() + sid[1:], did[0].upper() + did[1:]


def renombrar(t):
    return t.replace(SRC, DST).replace(Sid, Did).replace(sid, did)


def escanear(s, i, modo):
    """Desde s[i] (justo después de '->' o 'to'), devuelve el índice donde acaba la expresión."""
    depth = 0
    n = len(s)
    j = i
    pila = []  # estados: 'code', 'str', 'rawstr', 'tmpl'
    estado = ['code']
    ultimo = ''  # último carácter significativo en código
    empezo = False
    while j < n:
        c = s[j]
        st = estado[-1]
        if st == 'str':
            if c == '\\':
                j += 2; continue
            if c == '"':
                estado.pop(); j += 1; ultimo = '"'; continue
            if c == '$' and j + 1 < n and s[j + 1] == '{':
                estado.append('tmpl'); pila.append(depth); depth = 0; j += 2; continue
            j += 1; continue
        if st == 'rawstr':
            if s.startswith('"""', j):
                estado.pop(); j += 3; ultimo = '"'; continue
            if c == '$' and j + 1 < n and s[j + 1] == '{':
                estado.append('tmpl'); pila.append(depth); depth = 0; j += 2; continue
            j += 1; continue
        # código (o plantilla)
        if s.startswith('//', j):
            k = s.find('\n', j); j = n if k < 0 else k; continue
        if s.startswith('/*', j):
            k = s.find('*/', j); j = n if k < 0 else k + 2; continue
        if s.startswith('"""', j):
            estado.append('rawstr'); j += 3; empezo = True; continue
        if c == '"':
            estado.append('str'); j += 1; empezo = True; continue
        if c == "'":
            # carácter
            k = j + 1
            if s[k] == '\\': k += 1
            k = s.find("'", k + 1) if s[k] != "'" else k
            j = k + 1; ultimo = "'"; continue
        if st == 'tmpl':
            if c in '({[': depth += 1
            elif c in ')]': depth -= 1
            elif c == '}':
                if depth == 0:
                    estado.pop(); depth = pila.pop(); j += 1; continue
                depth -= 1
            j += 1; continue
        if c in '({[':
            depth += 1; empezo = True
        elif c in ')}]':
            if depth == 0:
                return j
            depth -= 1
        elif depth == 0 and c == ';':
            return j
        elif depth == 0 and modo == 'to' and c == ',':
            return j
        elif depth == 0 and c == '\n' and empezo:
            # ¿sigue la expresión en la línea siguiente?
            resto = s[j + 1:]
            sig = resto.lstrip(' \t')
            prev = s[i:j].rstrip()
            # quitar comentario final de línea
            linea = prev.split('\n')[-1]
            sin_com = re.sub(r'//.*$', '', linea).rstrip()
            cont_prev = bool(sin_com) and (sin_com[-1] in '+-*/%=&|<>?:.,(' or sin_com.endswith('->'))
            cont_sig = sig.startswith(('.', '?.', '?:', '&&', '||', 'else', ')'))  # ')' no debería
            if sig.startswith(')'):
                cont_sig = False
            if not cont_prev and not cont_sig:
                return j
        elif not c.isspace():
            empezo = True
        j += 1
    return n


manual = []
for ruta in sys.argv[5:]:
    s = open(ruta).read()
    out = []
    pos = 0
    for m in re.finditer(r'TemaId\.' + SRC + r'\b', s):
        a = m.start()
        if a < pos:
            continue
        resto = s[m.end():]
        mf = re.match(r'\s*(->|to\b)', resto)
        linea_ini = s.rfind('\n', 0, a) + 1
        prefijo = s[linea_ini:a]
        if not mf:
            manual.append(f'{ruta}:{s.count(chr(10), 0, a) + 1}: {s[linea_ini:s.find(chr(10), a)].strip()[:140]}')
            continue
        modo = mf.group(1)
        inicio_expr = m.end() + mf.end()
        fin = escanear(s, inicio_expr, '->' if modo == '->' else 'to')
        cuerpo = s[a:fin]
        # recortar espacios finales del cuerpo
        cuerpo_r = cuerpo.rstrip()
        fin = a + len(cuerpo_r)
        copia = renombrar(cuerpo_r)
        if copia == cuerpo_r:
            manual.append(f'{ruta}:{s.count(chr(10), 0, a) + 1}: sin cambio al copiar')
        al_inicio = prefijo.strip() == ''
        # lo que sigue tras el fin en la misma línea
        fin_linea = s.find('\n', fin)
        if fin_linea < 0: fin_linea = len(s)
        cola = s[fin:fin_linea]
        out.append(s[pos:fin])
        sep = ';' if modo == '->' else ','
        if al_inicio and (cola.strip() in ('', sep, sep + ' ') or re.match(r'^\s*' + re.escape(sep) + r'?\s*//', cola)):
            # rama en sus propias líneas: la copia va en la línea siguiente, con la misma sangría
            if modo == 'to' and not cola.strip().startswith(','):
                cola = ',' + cola
                tenia_coma = False
            else:
                tenia_coma = True
            out.append(cola)
            ind = prefijo
            lineas = copia.split('\n')
            out.append('\n' + ind + lineas[0] + ('\n' + '\n'.join(lineas[1:]) if len(lineas) > 1 else '') + (',' if modo == 'to' and tenia_coma else ''))
            pos = fin_linea
        else:
            if '\n' in copia:
                manual.append(f'{ruta}:{s.count(chr(10), 0, a) + 1}: rama de varias líneas en línea compartida')
            out.append(sep + ' ' + copia)
            pos = fin
    out.append(s[pos:])
    nuevo = ''.join(out)
    if nuevo != s:
        open(ruta, 'w').write(nuevo)
print('\n'.join(manual))
