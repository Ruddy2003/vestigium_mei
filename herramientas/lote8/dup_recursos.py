"""Copia los recursos de un tema (src) como punto de partida de otro (dst), en app/src/main.

Uso: python3 dup_recursos.py <raíz del repo> src dst [--binarios]
- AndroidManifest.xml: el activity-alias .Icono<Src> se copia como .Icono<Dst>, justo después.
- values/themes.xml, values-v31/themes.xml: el estilo Theme.Vestigium.Arranque.<Src> se copia, justo después.
- values/colores_arranque.xml: los colores arranque_<src>_* se copian, justo después.
- mipmap-anydpi-v26/ic_launcher_<src>.xml y los xml de widget de drawable/ y drawable-v31/: se copian renombrando.
- Con --binarios, también se copian los .png/.jpg/.ogg del tema (solo como relleno: luego se generan los propios).
"""
import os, re, shutil, sys

raiz, src, dst = sys.argv[1:4]
binarios = '--binarios' in sys.argv
Src, Dst = src[0].upper() + src[1:], dst[0].upper() + dst[1:]
M = os.path.join(raiz, 'app/src/main')


def ren(t):
    return t.replace(src, dst).replace(Src, Dst)


def bloque_tras(ruta, patron_inicio, fin):
    """Copia el primer bloque que empieza en la línea que cumple patron_inicio y acaba en la línea que contiene fin."""
    ls = open(ruta).read().split('\n')
    for i, l in enumerate(ls):
        if re.search(patron_inicio, l):
            j = i
            while fin not in ls[j]:
                j += 1
            blk = ls[i:j + 1]
            ls[j + 1:j + 1] = [ren(x) for x in blk]
            open(ruta, 'w').write('\n'.join(ls))
            return True
    print('no se encontró', patron_inicio, 'en', ruta)
    return False


# Manifiesto: el alias empieza en la línea «<activity-alias» anterior a android:name=".Icono<Src>".
ruta = os.path.join(M, 'AndroidManifest.xml')
ls = open(ruta).read().split('\n')
k = next(i for i, l in enumerate(ls) if f'android:name=".Icono{Src}"' in l)
i = k
while '<activity-alias' not in ls[i]:
    i -= 1
j = k
while '</activity-alias>' not in ls[j]:
    j += 1
ls[j + 1:j + 1] = [ren(x) for x in ls[i:j + 1]]
open(ruta, 'w').write('\n'.join(ls))

for v in ('values', 'values-v31'):
    r = os.path.join(M, 'res', v, 'themes.xml')
    if os.path.exists(r):
        bloque_tras(r, rf'name="Theme\.Vestigium\.Arranque\.{Src}"', '</style>')

r = os.path.join(M, 'res/values/colores_arranque.xml')
ls = open(r).read().split('\n')
idx = [i for i, l in enumerate(ls) if f'arranque_{src}_' in l]
ls[idx[-1] + 1:idx[-1] + 1] = [ren(ls[i]) for i in idx]
open(r, 'w').write('\n'.join(ls))

for carpeta in os.listdir(os.path.join(M, 'res')):
    d = os.path.join(M, 'res', carpeta)
    for f in sorted(os.listdir(d)):
        base, ext = os.path.splitext(f)
        if not re.search(rf'(^|_){src}(_|$)', base):
            continue
        nuevo = os.path.join(d, ren(f))
        if os.path.exists(nuevo):
            continue
        if ext == '.xml':
            open(nuevo, 'w').write(ren(open(os.path.join(d, f)).read()))
        elif binarios:
            shutil.copyfile(os.path.join(d, f), nuevo)
print('listo')
