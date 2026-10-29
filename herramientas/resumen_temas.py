"""
Recalcula el «Resumen por tema» de docs/diseno/TEMAS.md a partir de las filas de la matriz (cada lote cambia celdas).
Uso: python3 resumen_temas.py ../docs/diseno/TEMAS.md
"""
import sys

TEMAS = "ROM CIB HEL MED DIS REF ORD LAB COL NAP BOT NAV SED GOT ESP AND MON REN URS REV ANT".split()
ESTADOS = ['P', 'S', 'C', 'G', '✗']

ruta = sys.argv[1]
lineas = open(ruta, encoding='utf-8').read().split('\n')
cuenta = {t: dict.fromkeys(ESTADOS, 0) for t in TEMAS}
for l in lineas:
    c = [x.strip() for x in l.strip().strip('|').split('|')]
    if len(c) == len(TEMAS) + 3 and all(x in ESTADOS + ['·'] for x in c[2:-1]):
        for t, v in zip(TEMAS, c[2:-1]):
            if v in cuenta[t]: cuenta[t][v] += 1
tabla = ['| Tema | ' + ' | '.join(ESTADOS) + ' |', '|---|' + '---|' * len(ESTADOS)]
tabla += [f'| {t} | ' + ' | '.join(str(cuenta[t][e]) for e in ESTADOS) + ' |' for t in TEMAS]
i = lineas.index('| Tema | P | S | C | G | ✗ |')
j = i
while j < len(lineas) and lineas[j].startswith('|'): j += 1
lineas[i:j] = tabla
open(ruta, 'w', encoding='utf-8').write('\n'.join(lineas))
print('\n'.join(tabla))
