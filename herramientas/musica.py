"""Vestigium Mei — música de cada tema: la presentación al elegirlo (~8–10 s), un tono de llamada y un tono de notificación
para el teléfono (distinto de los avisos de la app). Todo en la tonalidad del tema, con sus mismos instrumentos.

Uso: python3 musica.py <carpeta res/raw> [temas] [piezas]
"""
import sys
from afinados import *   # noqa: F401,F403  (bloques e instrumentos; OUT viene de sys.argv[1])
import afinados as A

def frase(inicio, texto):
    """«0:1 4:1 7:2 r:1 …» → [(tiempo, semitono, duración)] seguidos, empezando en el tiempo [inicio]."""
    out = []; b = inicio
    for tok in texto.split():
        if tok == '|': continue
        n, d = tok.split(':'); d = float(d)
        if n != 'r': out.append((b, int(n), d))
        b += d
    return out

def obra(T, pulso, acordes, mel, inst_mel, inst_ac, arpegio=False, bajo=None, perc=(), extra=(), g_mel=0.55, g_ac=0.4, g_bajo=0.35,
         oct_ac=-12, oct_mel=0, oct_bajo=-24, legato=1.0, cola=1.6, rev=0.28):
    """acordes: [(tiempo, [semitonos], duración)]; mel: [(tiempo, semitono, duración)] relativo a T; perc: [(tiempo, señal, ganancia)]."""
    p = []
    for (b, notas, dur) in acordes:
        if arpegio:
            paso = 0.5
            k = 0; t = 0.0
            while t < dur - 1e-6:
                n = notas[k % len(notas)] + (12 if (k // len(notas)) % 2 else 0)
                p.append(((b + t) * pulso, inst_ac(hz(T + oct_ac + n), paso * pulso * 2.2), g_ac * 0.8))
                k += 1; t += paso
        else:
            p.append((b * pulso, acorde(inst_ac, [T + oct_ac + n for n in notas], dur * pulso), g_ac))
        if bajo is not None: p.append((b * pulso, bajo(hz(T + oct_bajo + notas[0]), dur * pulso), g_bajo))
    for (b, n, d) in mel: p.append((b * pulso, inst_mel(hz(T + oct_mel + n), d * pulso * legato), g_mel))
    for (b, x, g) in perc: p.append((b * pulso, x, g))
    for x in extra: p.append(x)
    return reverb(mezclar(*p), cola, rev)

def compases(*cs, largo=4):
    """Acordes de un compás cada uno: ([semitonos], …) → [(tiempo, notas, largo)]."""
    return [(i * largo, c, largo) for i, c in enumerate(cs)]

def cada(pulsos, señal, g=0.5, desde=0.0, paso=1.0):
    return [(desde + i * paso, señal() if callable(señal) else señal, g) for i in range(int(pulsos / paso))]

def llamada(T, pulso, acordes, mel, *a, **kw):
    """Tono de llamada: los dos primeros compases y el cierre, dos veces, un poco más vivo."""
    pr = pulso * 0.85
    ac = [c for c in acordes if c[0] < 8] + [(8, acordes[-1][1], 4)]
    m = [x for x in mel if x[0] < 8] + [(8 + (x[0] - acordes[-1][0]), x[1], x[2]) for x in mel if x[0] >= acordes[-1][0]]
    una = obra(T, pr, ac, m, *a, **kw)
    return mezclar((0, una, 1), (12 * pr + 0.5, una, 1))

def notificacion(T, inst, notas, paso=0.12, d=0.7, cola=1.0):
    return reverb(mezclar(*[(i * paso, inst(hz(T + n), d), 0.55) for i, n in enumerate(notas)]), cola, 0.25)

def parejo(x, rms_db=-15.0, techo=0.86, maximo_db=5.0):
    """Tonos del teléfono parejos: los de cuerda pulsada o campana tienen picos tan altos que, al normalizar, quedan más
    bajos que los demás. Si les falta volumen, suben hasta [maximo_db] y sus picos se redondean con un limitador suave."""
    x = x / np.abs(x).max() * techo
    falta = min(maximo_db, rms_db - 20 * np.log10(np.sqrt(np.mean(x ** 2)) + 1e-12))
    if falta <= 0.5: return x
    return np.tanh(x * 10 ** (falta / 20) / techo) * techo

P = {}   # tema → (presentación, llamada, notificación)
ARGS = {}   # tema → lo necesario para la música larga de «Tu año»

def hacer():
    # --- Romano: Re mayor, cornu, lira y tambor
    T = -7; pz = 0.5
    cornu = lambda f, d: metal(f, d, 3200)
    ac = compases([0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "0:1 4:1 7:1 12:1 | 9:1 7:1 5:1 9:1 | 11:1 7:1 11:1 14:1 | 12:4")
    perc = cada(16, lambda: tambor(hz(T - 24), 0.35, 0.3), 0.5, paso=2) + [(15, tambor(hz(T - 24), 0.6), 0.7)]
    kw = dict(inst_mel=cornu, inst_ac=lambda f, d: metal(f, d, 2000, 0.08), perc=perc, g_ac=0.3)
    P['romano'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, cornu, [7, 12, 16], 0.14, 0.5))
    ARGS['romano'] = (T, pz, ac, mel, kw)

    # --- Cyberpunk: Mi menor, arpegios cuadrados, bajo de sierra y hi-hat
    T = 7; pz = 0.3
    ac = compases([0, 3, 7], [-4, 0, 3], [3, 7, 10], [-2, 2, 5], [0, 3, 7], [-4, 0, 3], [-2, 2, 5], [0, 3, 7])
    mel = frase(0, "12:1 15:1 19:1 17:1 | 15:2 12:1 14:1 | 10:1 14:1 19:1 17:1 | 14:4 | 12:1 15:1 19:1 22:1 | 20:2 19:1 15:1 | 17:1 14:1 17:1 19:1 | 12:4")
    sierra = lambda f, d: filtro(barrido(f, f, d, 'sierra'), 'bajo', 900) * np.minimum(1, t_(d) / 0.01) * np.minimum(1, (d - t_(d)) / 0.05)
    hats = cada(32, lambda: filtro(ruido(0.04), 'alto', 6000) * env(int(0.04 * SR), 0.001, 0.02), 0.25, paso=0.5)
    kw = dict(inst_mel=lambda f, d: cuadrada(f, d, 0.12), inst_ac=lambda f, d: cuadrada(f, min(d, 0.2), 0.05), arpegio=True, bajo=sierra, perc=hats, g_ac=0.25, oct_mel=-12)
    P['ciber'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: cuadrada(f, d, 0.1), [12, 19, 24], 0.08, 0.3))
    ARGS['ciber'] = (T, pz, ac, mel, kw)

    # --- Helénico: La mayor pentatónico, lira y aulos
    T = -12; pz = 0.42
    lira = lambda f, d: pluck(f, d, 0.5)
    ac = compases([0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "0:1 2:1 4:2 | 7:1 9:1 7:2 | 4:1 7:1 2:2 | 0:4")
    kw = dict(inst_mel=flauta, inst_ac=lambda f, d: pluck(f, 1.2, 0.45), arpegio=True, oct_ac=12, oct_mel=24, g_mel=0.35, legato=0.95)
    P['heleno'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: pluck(f, d, 0.6), [19, 24, 28], 0.1, 0.8))
    ARGS['heleno'] = (T, pz, ac, mel, kw)

    # --- Scriptorium: Re dórico, canto llano sobre bordón y campanas
    T = -7; pz = 0.55
    mel = frase(0, "0:2 2:1 3:1 | 5:2 7:2 | 5:1 3:1 2:2 | 0:4")
    camp = lambda n, d=1.4: campana(hz(T + n), d, 2.0, 1.6, 3)
    extra = [(0, coro(hz(T - 12), 16 * pz + 0.6), 0.2), (0, coro(hz(T - 5), 16 * pz + 0.6), 0.14), (0, camp(24), 0.3), (0.3, camp(19), 0.25),
             (15 * pz, camp(12, 2.4), 0.35), (15 * pz + 0.25, camp(24, 2.4), 0.3)]
    kw = dict(inst_mel=coro, inst_ac=organo, extra=extra, g_mel=0.45, cola=2.4, rev=0.38)
    P['medieval'] = (obra(T, pz, [], mel, **kw), llamada(T, pz, [(0, [0], 4), (4, [0], 4), (8, [0], 4), (12, [0], 4)], mel, inst_mel=lambda f, d: campana(f, 1.2, 2.0, 1.6, 3), inst_ac=lambda f, d: coro(f, d), g_ac=0.15, cola=2.0, rev=0.35),
                     notificacion(T, lambda f, d: campana(f, 1.2, 2.0, 1.6, 3), [24, 19, 22], 0.16, 1.0, 1.6))
    ARGS['medieval'] = (T, pz, [], mel, kw)

    # --- Distopía: Do menor, órgano del ministerio, máquina de escribir y timbre
    T = 3; pz = 0.5
    ac = compases([0, 3, 7], [-4, 0, 3], [-7, -4, 0], [-5, -1, 2], [0, 3, 7])
    mel = frase(0, "7:2 8:2 | 7:1 5:1 3:2 | 0:1 3:1 5:2 | 2:2 -1:2 | 0:4")
    teclas = cada(8, lambda: mezclar((0, clic(0.02, (1000, 4000)), 1), (0.006, clic(0.012, (500, 2000)), 0.5)), 0.35, paso=0.5)
    ding = lambda: campana(hz(T + 12), 1.2, 3.0, 1.0, 5)
    kw = dict(inst_mel=lambda f, d: metal(f, d, 1800, 0.06), inst_ac=organo, bajo=lambda f, d: organo(f, d), perc=teclas + [(16, ding(), 0.45)], g_mel=0.45, oct_mel=-12)
    P['distopia'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: campana(f, 1.0, 3.0, 1.0, 5), [12, 12, 7], 0.18, 0.8))
    ARGS['distopia'] = (T, pz, ac, mel, kw)

    # --- Refugio: Fa mayor, vibráfono y órgano de los cincuenta
    T = 8; pz = 0.36
    ac = compases([0, 4, 7], [-3, 0, 4], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "12:1 9:1 7:1 9:1 | 12:2 14:2 | 17:1 16:1 14:1 12:1 | 11:2 14:2 | 12:4")
    kw = dict(inst_mel=vibrafono, inst_ac=lambda f, d: organo(f, d, 0.15), bajo=lambda f, d: vibrafono(f, d), g_ac=0.22, oct_mel=-12, oct_ac=-12)
    P['refugio'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, vibrafono, [0, 4, 12], 0.12, 0.9))
    ARGS['refugio'] = (T, pz, ac, mel, kw)

    # --- Orden: Sol menor, trompas y timbales
    T = -2; pz = 0.5
    ac = compases([0, 3, 7], [-4, 0, 3], [-2, 2, 5], [0, 3, 7, 12])
    mel = frase(0, "0:1 3:1 7:2 | 8:1 7:1 3:2 | 5:1 2:1 5:1 7:1 | 12:4")
    timbal = lambda n: tambor(hz(T - 24 + n), 0.6, 0.15)
    perc = [(b, timbal(0 if b % 4 == 0 else 7), 0.55) for b in range(0, 16, 2)] + [(15, timbal(0), 0.8)]
    trompa = lambda f, d: metal(f, d, 1800, 0.06)
    kw = dict(inst_mel=trompa, inst_ac=trompa, perc=perc, g_ac=0.28, oct_mel=-12, oct_ac=-24)
    P['orden'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T - 12, trompa, [7, 12], 0.2, 0.6))
    ARGS['orden'] = (T, pz, ac, mel, kw)

    # --- Laboratorio: Re menor, el instituto bajo tierra. Arpegio de senos fríos, colchón de sintetizador, pulso grave y vidrio lejano.
    T = -7; pz = 0.42
    ac = compases([0, 3, 7, 14], [-2, 2, 5, 9], [-5, -2, 2, 5], [-3, 1, 4, 7], [0, 3, 7, 14], [-2, 2, 5, 9], [-7, -3, 0, 5], [0, 3, 7, 12])
    mel = frase(0, "14:2 15:2 | 14:1 12:1 9:2 | 10:2 9:1 7:1 | 9:4 | 14:2 15:1 17:1 | 19:2 17:1 14:1 | 12:2 14:2 | 12:4")
    pulsos = cada(32, lambda: seno(hz(T - 24), 0.35) * env(int(0.35 * SR), 0.004, 0.25, 3), 0.4, paso=1)
    kw = dict(inst_mel=lambda f, d: campana(f, d + 0.8, 3.5, 0.7, 1.8), inst_ac=lambda f, d: frio(f, 0.5), arpegio=True,
              bajo=lambda f, d: sinte(f, d, 900, 0.4, 0.6), perc=pulsos, g_mel=0.3, g_ac=0.3, g_bajo=0.3, oct_mel=0, oct_ac=0, cola=2.4, rev=0.4)
    P['laboratorio'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: frio(f, 0.6), [12, 15, 19, 14], 0.16, 0.6, 1.8))
    ARGS['laboratorio'] = (T, pz, ac, mel, kw)

    # --- Colonos: Sol mayor pentatónico, marimba y violín de pueblo
    T = -2; pz = 0.4
    ac = compases([0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "0:1 2:1 4:1 7:1 | 9:2 7:2 | 4:1 2:1 4:1 7:1 | 12:4")
    kw = dict(inst_mel=lambda f, d: arco(f, d, 5.0), inst_ac=lambda f, d: marimba(f, 0.6), arpegio=True, g_mel=0.4, legato=0.95)
    P['colonos'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: marimba(f, 0.6), [7, 12, 16], 0.11, 0.6))
    ARGS['colonos'] = (T, pz, ac, mel, kw)

    # --- Napoleónico: Si bemol mayor, marcha con redoble y corneta
    T = 1; pz = 0.42
    ac = compases([0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "-5:1 -5:0.5 -5:0.5 0:1 4:1 | 7:2 4:1 0:1 | 7:1 7:1 4:1 7:1 | 12:3 r:1")
    golpe = lambda: mezclar((0, filtro(ruido(0.05), 'banda', (1500, 6000)) * env(int(0.05 * SR), 0.001, 0.03), 0.8), (0, tambor(hz(T - 24), 0.06), 0.4))
    perc = cada(16, golpe, 0.35, paso=0.5) + cada(16, lambda: tambor(hz(T - 36), 0.3, 0.2), 0.5, paso=2) + [(12, filtro(ruido(1.2), 'alto', 5000) * env(int(1.2 * SR), 0.005, 0.8, 3), 0.2)]
    corneta = lambda f, d: metal(f, d, 3500)
    kw = dict(inst_mel=corneta, inst_ac=lambda f, d: metal(f, d, 2200, 0.06), perc=perc, g_ac=0.25)
    P['napoleon'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, corneta, [-5, 0, 4], 0.13, 0.4))
    ARGS['napoleon'] = (T, pz, ac, mel, kw)

    # --- Botánico: Sol dórico, el cuaderno del naturalista. Violonchelo sobre guitarra arpegiada y grillos afinados de fondo.
    T = -2; pz = 0.48
    ac = compases([0, 3, 7], [5, 9, 12], [0, 3, 7], [-2, 2, 5], [0, 3, 7], [5, 9, 12], [2, 5, 9], [0, 3, 7, 12])
    mel = frase(0, "7:2 9:1 10:1 | 12:2 9:2 | 10:1 9:1 7:1 5:1 | 7:4 | 10:2 12:1 14:1 | 16:2 14:2 | 12:1 10:1 9:1 10:1 | 7:4")
    grillos = [(1.0 + 2.6 * i, grillo(hz(T + 36 + [0, 3, 0, 7][i % 4]), 0.12, 3), 0.1) for i in range(12)]
    kw = dict(inst_mel=lambda f, d: arco(f, d, 5.0), inst_ac=lambda f, d: guitarra(f, 1.2), arpegio=True, perc=grillos, g_mel=0.36, g_ac=0.32, oct_mel=-12, oct_ac=-12, legato=0.95)
    P['botanico'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: guitarra(f, 1.0), [12, 15, 19], 0.12, 1.0))
    ARGS['botanico'] = (T, pz, ac, mel, kw)

    # --- Navidad: La mayor, caja de música, cascabeles y campanas
    T = 0; pz = 0.38
    def cascabel(d=0.18):
        x = np.zeros(int(d * SR))
        for i in range(6):
            c = campana(4200 + A.rng.random() * 1200, 0.08, 2.3, 1.5, 40)
            k = int(A.rng.random() * (d - 0.08) * SR); x[k:k + len(c)] += c * 0.5
        return x
    ac = compases([0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "7:1 9:1 7:1 4:1 | 5:2 9:2 | 7:1 4:1 2:1 4:1 | 0:4")
    perc = cada(16, cascabel, 0.18, paso=1) + [(12, campana(hz(T - 12), 2.5, 2.0, 1.5, 1.5), 0.35), (12.5, campana(hz(T - 5), 2.5, 2.0, 1.5, 1.5), 0.3)]
    kw = dict(inst_mel=cajita, inst_ac=lambda f, d: cajita(f, 0.8), arpegio=True, perc=perc, g_ac=0.22, oct_mel=12)
    P['navidad'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, cajita, [19, 16, 24], 0.12, 0.9))
    ARGS['navidad'] = (T, pz, ac, mel, kw)

    # --- Ruta de la Seda: La menor pentatónico, cítara, gong y bordón
    T = 0; pz = 0.5
    citara = lambda f, d: pluck(f, max(d, 0.8), 0.65, 0.995)
    mel = frase(0, "0:1 3:1 5:1 7:1 | 10:2 7:2 | 5:1 3:1 5:1 0:1 | -2:2 0:2")
    gong = reverb(campana(hz(T - 24), 3.0, 1.48, 4, 2) * np.minimum(1, t_(3.0) / 0.12), 1.0, 0.3)
    bordon = filtro(seno(hz(T - 24), 16 * pz + 0.5) + 0.5 * seno(hz(T - 17), 16 * pz + 0.5), 'bajo', 600) * np.minimum(1, t_(16 * pz + 0.5) / 0.8) * np.minimum(1, (16 * pz + 0.5 - t_(16 * pz + 0.5)) / 1.0)
    extra = [(0, gong, 0.35), (0, bordon, 0.15), (4 * pz, campana(hz(T + 31), 0.6, 2.76, 1.2, 6), 0.2), (12 * pz, campana(hz(T + 31), 0.6, 2.76, 1.2, 6), 0.2)]
    kw = dict(inst_mel=citara, inst_ac=citara, extra=extra, g_mel=0.5)
    P['seda'] = (obra(T, pz, [], mel, **kw), llamada(T, pz, [(0, [0], 4), (4, [7], 4), (8, [0], 4), (12, [0], 4)], mel, inst_mel=citara, inst_ac=citara, g_ac=0.2),
                 notificacion(T, citara, [12, 7, 15], 0.1, 0.8))
    ARGS['seda'] = (T, pz, [], mel, kw)

    # --- Soviético: Do menor, metales y timbales
    T = 3; pz = 0.5
    ac = compases([0, 3, 7], [-4, 0, 3], [3, 7, 10], [-5, -1, 2], [0, 3, 7, 12])
    mel = frase(0, "0:1 3:1 7:2 | 8:1 7:1 3:2 | 10:1 7:1 3:1 7:1 | 2:2 -1:2 | 0:4")
    perc = [(b, tambor(hz(T - 36 + (0 if b % 8 == 0 else 7)), 0.5, 0.2), 0.55) for b in range(0, 20, 2)]
    bronce = lambda f, d: metal(f, d, 2600)
    kw = dict(inst_mel=bronce, inst_ac=lambda f, d: metal(f, d, 2000, 0.06), perc=perc, g_ac=0.28, oct_mel=-12, oct_ac=-24)
    P['urss'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T - 12, bronce, [0, 7, 12], 0.14, 0.45))
    ARGS['urss'] = (T, pz, ac, mel, kw)

    # --- Gótico: La menor con tercera de picardía, órgano y campanas
    T = 0; pz = 0.6
    ac = [(0, [-12, -9, -5], 4), (4, [-16, -12, -9], 4), (8, [-7, -4, 0], 4), (12, [-5, -1, 2], 4), (16, [-12, -8, -5, 0], 4)]
    mel = frase(0, "0:2 3:2 | 5:2 3:1 0:1 | 2:2 5:2 | -1:2 2:2 | 0:2 4:2")
    extra = [(0, campana(hz(T + 7), 2.0, 2.0, 2, 2.5), 0.3), (16 * pz, campana(hz(T + 12), 2.8, 2.0, 2, 1.5), 0.35), (16 * pz + 0.3, campana(hz(T + 7), 2.5, 2.0, 2, 1.5), 0.25)]
    kw = dict(inst_mel=coro, inst_ac=organo, extra=extra, g_ac=0.45, g_mel=0.35, oct_ac=0, cola=2.8, rev=0.42)
    P['gotico'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac[:4] + [ac[4]], mel, **kw), notificacion(T, lambda f, d: campana(f, 1.6, 2.0, 2, 2.5), [7, 0, 12], 0.22, 1.4, 2.0))
    ARGS['gotico'] = (T, pz, ac, mel, kw)

    # --- Espacio: Mi mayor, pad que respira, tonos limpios y el pitido de la transmisión
    T = 7; pz = 0.55
    def pad(f, d):
        t = t_(d); x = np.sin(2 * np.pi * f * t + 0.3 * np.sin(2 * np.pi * 0.3 * t)) + 0.5 * np.sin(2 * np.pi * f * 1.003 * t)
        return x * np.sin(np.pi * t / d) ** 1.2
    ac = compases([0, 4, 7], [-3, 0, 4], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "0:1 4:1 7:2 | 11:2 9:2 | 12:1 9:1 7:2 | 14:2 11:2 | 12:4")
    extra = [(20 * pz - 0.4, seno(hz(T + 24), 0.25) * np.minimum(1, t_(0.25) / 0.01) * np.minimum(1, (0.25 - t_(0.25)) / 0.02), 0.2)]
    kw = dict(inst_mel=lambda f, d: seno(f, d) * env(int(d * SR), 0.03, d * 0.9, 2), inst_ac=pad, extra=extra, g_ac=0.3, g_mel=0.35, oct_ac=-24, cola=2.4, rev=0.35)
    P['espacio'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: pip(f, 0.12), [12, 16, 19, 24], 0.07, 0.12, 1.4))
    ARGS['espacio'] = (T, pz, ac, mel, kw)

    # --- Al-Ándalus: Re hiyaz, laúd, darbuka (maqsum) y qanún
    T = -7; pz = 0.4
    oud = lambda f, d: pluck(f, max(d, 0.6), 0.35, 0.994)
    dum = lambda: tambor(hz(T - 24), 0.35, 0.15)
    tek = lambda: mezclar((0, filtro(ruido(0.06), 'banda', (2500, 8000)) * env(int(0.06 * SR), 0.001, 0.035), 1), (0, campana(hz(T + 31), 0.08, 1.7, 1, 30), 0.3))
    maqsum = []
    for c in range(4):
        b = 4 * c
        maqsum += [(b, dum(), 0.55), (b + 0.5, tek(), 0.35), (b + 1.5, tek(), 0.35), (b + 2, dum(), 0.55), (b + 3, tek(), 0.35)]
    mel = frase(0, "7:1 8:1 7:1 5:1 | 4:2 5:1 4:1 | 1:1 4:1 1:1 0:1 | 0:4")
    hijaz = [0, 1, 4, 5, 7, 8, 10, 12]
    qanun = [(12 + i * 0.25, pluck(hz(T + 12 + g), 1.0, 0.7, 0.993), 0.3) for i, g in enumerate(hijaz)]
    kw = dict(inst_mel=oud, inst_ac=oud, bajo=None, perc=maqsum + qanun, extra=[(0, pluck(hz(T - 12), 6.5, 0.2, 0.998), 0.25)], g_mel=0.55)
    P['andalus'] = (obra(T, pz, [], mel, **kw), llamada(T, pz, [(0, [0], 4), (4, [0], 4), (8, [0], 4), (12, [0], 4)], mel, inst_mel=oud, inst_ac=oud, perc=maqsum[:10], g_ac=0.2),
                    notificacion(T, oud, [7, 8, 7, 12], 0.11, 0.7))
    ARGS['andalus'] = (T, pz, [], mel, kw)

    # --- Mongol: Mi mayor pentatónico, canto difónico, morin juur y galope
    T = -5; pz = 0.5
    casco = lambda: mezclar((0, filtro(ruido(0.05), 'banda', (300, 1500)) * env(int(0.05 * SR), 0.001, 0.03, 6), 1), (0, tambor(hz(T - 24), 0.06, 0), 0.5))
    galope = lambda: mezclar((0, casco(), 0.8), (0.07, casco(), 0.7), (0.13, casco(), 1))
    d0 = 16 * pz + 0.8
    f = hz(T - 24); t = t_(d0)
    k = np.array([8, 10, 12, 10])[np.minimum(3, (t / d0 * 4).astype(int))]
    joomii = (arco(f, d0, 0) * 0.6 + np.sin(2 * np.pi * np.cumsum(f * k) / SR) * 0.25) * np.minimum(1, t / 0.3) * np.minimum(1, (d0 - t) / 0.4)
    mel = frase(0, "0:2 2:1 4:1 | 7:2 9:2 | 7:1 4:1 2:1 4:1 | 0:4")
    kw = dict(inst_mel=lambda f, d: arco(f, d, 5.5), inst_ac=lambda f, d: arco(f, d), perc=cada(16, galope, 0.35, paso=1), extra=[(0, joomii, 0.35)], g_mel=0.45)
    P['mongol'] = (obra(T, pz, [], mel, **kw), llamada(T, pz, [(0, [0], 4), (4, [0], 4), (8, [0], 4), (12, [0], 4)], mel, inst_mel=lambda f, d: arco(f, d, 5.5), inst_ac=lambda f, d: arco(f, d), perc=cada(16, galope, 0.35, paso=1), g_ac=0.1),
                   notificacion(T, lambda f, d: arco(f, 0.45), [0, 7, 12], 0.16, 0.45))
    ARGS['mongol'] = (T, pz, [], mel, kw)

    # --- Renacimiento: La mayor, laúd rasgueado y flauta dulce
    T = 0; pz = 0.45
    laud = lambda f, d: pluck(f, 1.2, 0.45, 0.995)
    ac = [(0, [-12, -5, 0, 4], 4), (4, [-15, -8, -3, 1], 4), (8, [-7, -3, 0, 5], 4), (12, [-5, -1, 2, 7], 4), (16, [-12, -5, 0, 4, 12], 4)]
    mel = frase(0, "0:1 2:1 4:1 5:1 | 4:2 0:2 | 2:1 5:1 4:1 2:1 | -1:2 2:2 | 0:4")
    kw = dict(inst_mel=flauta, inst_ac=lambda f, d: pluck(f, 1.4, 0.45, 0.995), g_ac=0.45, g_mel=0.33, oct_ac=0, oct_mel=12, legato=0.92)
    P['renacimiento'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, laud, [4, 7, 12], 0.09, 1.0))
    ARGS['renacimiento'] = (T, pz, ac, mel, kw)

def hacer_pirata():
    # --- Pirata: Mi dórico, una saloma propia a buen paso. Violín de cubierta, concertina, un bajo pulsado, el pisotón
    # en las tablas y, al cerrar, dos campanadas sobre el oleaje. Con su propio azar.
    with A.azar_propio('musica_pirata'):
        T = -5; pz = 0.3
        ac = compases([0, 3, 7], [-2, 2, 5], [0, 3, 7], [5, 9, 12], [0, 3, 7], [-2, 2, 5], [5, 9, 12], [0, 3, 7])
        mel = frase(0, "0:0.5 3:0.5 7:1 7:0.5 5:0.5 3:1 | 2:1 5:1 2:1 -2:1 | 0:0.5 3:0.5 7:1 10:1 9:1 | 7:1.5 5:0.5 9:2 | "
                       "12:1 10:0.5 9:0.5 7:1 3:1 | 5:1 2:1 5:0.5 7:0.5 5:1 | 9:1 12:1 9:0.5 7:0.5 5:1 | 3:1 2:1 0:2")
        pisoton = lambda: mezclar((0, filtro(ruido(0.06), 'banda', (200, 1200)) * env(int(0.06 * SR), 0.001, 0.035, 4), 0.8), (0, tambor(hz(T - 29), 0.12, 0.1), 0.5))
        palmada = lambda: filtro(ruido(0.03), 'banda', (900, 3500)) * env(int(0.03 * SR), 0.001, 0.015, 5)
        perc = cada(32, pisoton, 0.4, paso=2) + cada(32, palmada, 0.12, desde=1, paso=2)
        bronce = lambda d: campana(hz(T + 12), d, 2.76, 1.4, 2.2)
        ola = lambda d: filtro(ruido(d), 'banda', (250, 2600)) * np.sin(np.pi * t_(d) / d) ** 2
        extra = ((0, ola(3.0), 0.12), (28 * pz, bronce(2.0), 0.3), (28 * pz + 0.28, bronce(2.0), 0.3), (26 * pz, ola(4.0), 0.14))
        kw = dict(inst_mel=lambda f, d: arco(f, d, 6.2), inst_ac=lambda f, d: fuelle(f, d, 0.08), bajo=lambda f, d: pluck(f, min(d, 1.0), 0.3, 0.994),
                  perc=perc, extra=extra, g_mel=0.4, g_ac=0.3, g_bajo=0.3, oct_mel=12, oct_ac=-12, legato=0.9, rev=0.24)
        P['pirata'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw),
                       notificacion(T, lambda f, d: campana(f, 1.6, 2.76, 1.4, 2.2), [12, 12, 19], 0.28, 1.6, 1.4))
        ARGS['pirata'] = (T, pz, ac, mel, kw)

def hacer_revolucion():
    # --- Revolución: Sol mayor, marcha de pífano y tambor (melodía propia) sobre cuerdas
    T = -2; pz = 0.36
    ac = compases([0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12])
    mel = frase(0, "7:0.5 7:0.5 12:1 11:0.5 9:0.5 7:1 | 4:1 7:1 5:1 2:1 | 0:0.5 2:0.5 4:1 5:0.5 7:0.5 9:1 | 7:1 11:1 12:2")
    golpe = lambda: mezclar((0, filtro(ruido(0.05), 'banda', (1300, 5500)) * env(int(0.05 * SR), 0.001, 0.03), 0.8), (0, tambor(hz(T - 24), 0.07), 0.4))
    perc = cada(16, golpe, 0.3, paso=0.5) + cada(16, lambda: tambor(hz(T - 36), 0.3, 0.2), 0.5, paso=2)
    kw = dict(inst_mel=flauta, inst_ac=lambda f, d: arco(f, d, 4.5), perc=perc, g_mel=0.42, g_ac=0.22, oct_mel=12, oct_ac=-12, legato=0.85)
    P['revolucion'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T + 12, flauta, [7, 12, 16], 0.12, 0.45))
    ARGS['revolucion'] = (T, pz, ac, mel, kw)

def hacer_antiguas():
    # --- Tierras Antiguas: Re dórico, una balada de arpa con cuerno y coro grave (melodía propia)
    T = -7; pz = 0.5
    ac = compases([0, 3, 7], [-2, 2, 5], [5, 9, 12], [0, 3, 7, 12])
    mel = frase(0, "7:1 5:0.5 3:0.5 5:1 7:1 | 10:1.5 9:0.5 7:2 | 3:1 5:1 7:1 9:1 | 7:1 5:1 7:2")
    golpe = lambda: tambor(hz(T - 36), 0.5, 0.12)
    perc = cada(16, golpe, 0.35, paso=2)
    kw = dict(inst_mel=lambda f, d: pluck(f, max(0.8, d), 0.6, 0.997), inst_ac=coro, perc=perc, g_mel=0.5, g_ac=0.28, oct_mel=0, oct_ac=-12, legato=1.0,
              bajo=lambda f, d: metal(f, d, brillo=900, ataque=0.12), g_bajo=0.18)
    P['antiguas'] = (obra(T, pz, ac, mel, **kw), llamada(T, pz, ac, mel, **kw), notificacion(T, lambda f, d: pluck(f, 1.0, 0.62, 0.997), [7, 10, 14], 0.12, 1.0))
    ARGS['antiguas'] = (T, pz, ac, mel, kw)

def hacer_enriquecidos():
    """Lote 6: la presentación y la música anual de Refugio, Orden, Soviético y Espacio, más ricas. Ocho compases en lugar
    de cuatro o cinco, con más voces (bajo que camina, coro, cuerdas, arpegios) y su percusión. Corre después de [hacer]
    con su propio azar, para que las demás músicas no cambien; el tono de llamada y la notificación siguen iguales."""
    with A.azar_propio('musica_enriquecida'):
        # --- Refugio: Fa mayor. Combo de los cincuenta: vibráfono, órgano, contrabajo que camina, escobillas y celesta.
        T = 8; pz = 0.34
        ac = compases([0, 4, 7, 11], [-3, 0, 4, 7], [5, 9, 12, 16], [7, 11, 14, 17], [0, 4, 7, 11], [-3, 0, 4, 7], [2, 5, 9, 12], [0, 4, 7, 12])
        mel = frase(0, "12:1 9:1 7:1 9:1 | 12:2 14:2 | 17:1 16:1 14:1 12:1 | 11:2 14:2 | 12:1 16:1 19:1 16:1 | 14:2 12:2 | 14:1 12:1 9:1 7:1 | 12:4")
        bajo = lambda n, d: pluck(hz(T - 24 + n), d, 0.25, 0.995)
        camina = [(b + k, bajo(n, 0.5 * pz * 2), 0.4) for (b, c, _) in ac for k, n in enumerate([c[0], c[1], c[2], c[1] + (1 if c[1] + 1 not in c else 0)])]
        escobilla = lambda: filtro(ruido(0.14), 'banda', (2000, 7500)) * env(int(0.14 * SR), 0.03, 0.08, 3)
        ride = lambda: campana(5200, 0.25, 2.3, 0.6, 14) * 0.5
        perc = camina + [(b, escobilla(), 0.18) for b in range(1, 32, 2)] + [(b, ride(), 0.08) for b in range(0, 32)]
        celesta = [(16 + b, cajita(hz(T + n), 0.8), 0.18) for b, n in [(0, 24), (1.5, 28), (3, 31), (4, 26), (6, 24), (8, 26), (9.5, 24), (11, 19), (12, 24)]]
        kw = dict(inst_mel=vibrafono, inst_ac=lambda f, d: organo(f, d, 0.15), perc=perc + celesta, g_ac=0.18, oct_mel=-12, oct_ac=-12)
        P['refugio'] = (obra(T, pz, ac, mel, **kw), P['refugio'][1], P['refugio'][2])
        ARGS['refugio'] = (T, pz, ac, mel, kw)

        # --- Orden: Sol menor. Trompas, coro grave, cuerdas en corcheas, timbales y un redoble al final.
        T = -2; pz = 0.38
        ac = compases([0, 3, 7], [-4, 0, 3], [-2, 2, 5], [0, 3, 7], [3, 7, 10], [-4, 0, 3], [-5, -1, 2], [0, 3, 7, 12])
        mel = frase(0, "0:1 3:1 7:2 | 8:1 7:1 3:2 | 5:1 2:1 5:1 7:1 | 12:4 | 10:1 12:1 14:2 | 15:1 14:1 12:2 | 11:1 7:1 11:1 14:1 | 12:4")
        trompa = lambda f, d: metal(f, d, 1800, 0.06)
        timbal = lambda n: tambor(hz(T - 24 + n), 0.6, 0.15)
        cuerdas = [(b + 0.5 * k, arco(hz(T - 12 + (c[0] if k % 2 == 0 else c[2])), 0.5 * pz * 0.95, 0), 0.12) for (b, c, _) in ac for k in range(8)]
        coro_ac = [(b * pz, acorde(coro, [T - 12 + n for n in c], d * pz), 0.22) for (b, c, d) in ac]
        def redoble(d):
            x = np.zeros(int(d * SR))
            for i in range(int(d / 0.03)):
                g = filtro(ruido(0.04), 'banda', (1200, 5000)) * env(int(0.04 * SR), 0.001, 0.025)
                k = int(i * 0.03 * SR); x[k:k + len(g)] += g[:len(x) - k] * (0.3 + 0.7 * i / (d / 0.03))
            return x
        perc = [(b, timbal(0 if b % 4 == 0 else 7), 0.5) for b in range(0, 32, 2)] + [(28, redoble(4 * pz - 0.05), 0.3), (31, timbal(0), 0.8)] + cuerdas
        kw = dict(inst_mel=trompa, inst_ac=trompa, perc=perc, extra=coro_ac, g_ac=0.22, oct_mel=-12, oct_ac=-24)
        P['orden'] = (obra(T, pz, ac, mel, **kw), P['orden'][1], P['orden'][2])
        ARGS['orden'] = (T, pz, ac, mel, kw)

        # --- Soviético: Do menor. Himno de metales sobre coro de hombres, bombo y caja de marcha, yunque y un bajo de tuba.
        T = 3; pz = 0.4
        ac = compases([0, 3, 7], [-4, 0, 3], [3, 7, 10], [-5, -1, 2], [0, 3, 7], [5, 8, 12], [-5, -1, 2], [0, 3, 7, 12])
        mel = frase(0, "0:1 3:1 7:2 | 8:1 7:1 3:2 | 10:1 7:1 3:1 7:1 | 2:2 -1:2 | 0:1 3:1 7:1 12:1 | 8:2 12:2 | 11:1 7:1 2:1 -1:1 | 0:4")
        bronce = lambda f, d: metal(f, d, 2600)
        caja = lambda: mezclar((0, filtro(ruido(0.08), 'banda', (1500, 6000)) * env(int(0.08 * SR), 0.001, 0.05), 0.8), (0, tambor(hz(T - 12), 0.05, 0.2), 0.3))
        yunque = lambda: campana(hz(T + 19), 0.6, 2.9, 2.5, 7)
        coro_ac = [(b * pz, acorde(coro, [T - 24 + n for n in c], d * pz), 0.25) for (b, c, d) in ac]
        perc = [(b, tambor(hz(T - 36), 0.5, 0.2), 0.55) for b in range(0, 32, 2)] + [(b + 0.5, caja(), 0.22) for b in range(1, 32, 2)] + \
               [(b + 0.75, caja(), 0.12) for b in range(1, 32, 2)] + [(12, yunque(), 0.25), (28, yunque(), 0.3)]
        kw = dict(inst_mel=bronce, inst_ac=lambda f, d: metal(f, d, 2000, 0.06), bajo=lambda f, d: metal(f, d, 700, 0.05), perc=perc, extra=coro_ac,
                  g_ac=0.22, g_bajo=0.3, oct_mel=-12, oct_ac=-24, oct_bajo=-36)
        P['urss'] = (obra(T, pz, ac, mel, **kw), P['urss'][1], P['urss'][2])
        ARGS['urss'] = (T, pz, ac, mel, kw)

        # --- Espacio: Mi lidio. El pad que respira, un arpegiador de senos, sub grave, campanas de vidrio y la transmisión.
        T = 7; pz = 0.4
        def pad(f, d):
            t = t_(d); x = np.sin(2 * np.pi * f * t + 0.3 * np.sin(2 * np.pi * 0.3 * t)) + 0.5 * np.sin(2 * np.pi * f * 1.003 * t)
            return x * np.sin(np.pi * t / d) ** 1.2
        ac = compases([0, 4, 7], [-3, 0, 4], [5, 9, 12], [7, 11, 14], [0, 4, 7], [2, 6, 9], [-3, 0, 4], [0, 4, 7, 12])
        mel = frase(0, "0:1 4:1 7:2 | 11:2 9:2 | 12:1 9:1 7:2 | 14:2 11:2 | 12:1 11:1 7:1 4:1 | 6:2 9:2 | 7:1 4:1 2:1 4:1 | 0:4")
        nota_arp = lambda n: seno(hz(T + n), 0.18) * env(int(0.18 * SR), 0.004, 0.12, 3)
        arp = [(b + 0.25 * k, nota_arp(c[k % len(c)] + 12 * ((k // len(c)) % 2)), 0.1) for (b, c, _) in ac for k in range(16)]
        sub = [(b, seno(hz(T - 36 + c[0]), 4 * pz) * np.sin(np.pi * t_(4 * pz) / (4 * pz)), 0.3) for (b, c, _) in ac]
        vidrio = [(b, vibrafono(hz(T + 24 + c[-1] - c[0]), 1.6), 0.12) for (b, c, _) in ac[::2]]
        aliento = [(b, filtro(ruido(4 * pz), 'banda', (500, 3000)) * np.sin(np.pi * t_(4 * pz) / (4 * pz)) ** 2, 0.06) for b in (8, 24)]
        bip = lambda: seno(hz(T + 24), 0.12) * np.minimum(1, t_(0.12) / 0.005) * np.minimum(1, (0.12 - t_(0.12)) / 0.01)
        trans = [(31.0, bip(), 0.15), (31.4, bip(), 0.15)]
        kw = dict(inst_mel=lambda f, d: seno(f, d) * env(int(d * SR), 0.03, d * 0.9, 2), inst_ac=pad, perc=arp + sub + vidrio + aliento + trans,
                  g_ac=0.26, g_mel=0.35, oct_ac=-24, cola=2.4, rev=0.35)
        P['espacio'] = (obra(T, pz, ac, mel, **kw), P['espacio'][1], P['espacio'][2])
        ARGS['espacio'] = (T, pz, ac, mel, kw)

def anual(T, pz, ac, mel, kw, ciclos=10):
    """La música de «Tu año» (~1 min y medio): la frase del tema, que va creciendo. Primero sola y suave; luego entran los acordes,
    el bajo y la percusión; en el clímax la melodía se dobla a la octava y todo sube un tono; al final vuelve a casa y se apaga."""
    largo = max([b + d for b, _, d in mel] + [b + d for b, _, d in ac] + [16])
    partes = []
    for c in range(ciclos):
        k = dict(kw)
        sube = 2 if c in (ciclos - 3, ciclos - 2) else 0
        ac_c = [(b, [n + sube for n in ns], d) for b, ns, d in ac]
        mel_c = [(b, n + sube, d) for b, n, d in mel]
        if c < 2:
            ac_c = []; k['perc'] = (); k['bajo'] = None; k['extra'] = ()
            k['g_mel'] = kw.get('g_mel', 0.55) * 0.8
        elif c < 4:
            k['perc'] = (); k['bajo'] = None
        if c >= ciclos - 4 and c < ciclos - 1:
            mel_c = mel_c + [(b, n + 12, d) for b, n, d in mel_c]
            k['g_mel'] = kw.get('g_mel', 0.55) * 0.75
        if c == ciclos - 1:
            k['perc'] = (); mel_c = [x for x in mel_c if x[0] < largo / 2] + [(largo / 2, 0, largo / 2)]
        if c % 2 == 1 and c < ciclos - 1:
            # Respira: la segunda vuelta de cada par cambia el final de la frase una octava abajo.
            mel_c = [(b, n - 12 if b >= largo * 0.75 else n, d) for b, n, d in mel_c]
        seg = obra(T, pz, ac_c, mel_c, **k)
        g = 0.55 + 0.45 * min(1.0, c / (ciclos - 3))
        partes.append((c * largo * pz, seg, g))
    x = mezclar(*partes)
    n = int(4.0 * SR); x[-n:] *= np.linspace(1, 0, n) ** 1.5
    return x

if __name__ == '__main__':
    hacer()
    hacer_pirata()
    hacer_revolucion()
    hacer_antiguas()
    hacer_enriquecidos()
    solo = sys.argv[2].split(',') if len(sys.argv) > 2 else None
    # Con un tercer argumento («presentacion,anual», por ejemplo) solo se escriben esas piezas.
    piezas = sys.argv[3].split(',') if len(sys.argv) > 3 else None
    for tema, (pres, llam, notif) in P.items():
        if solo is not None and tema not in solo: continue
        for clave, nombre, x, rms in (('presentacion', f's_{tema}_presentacion', pres, -17.0), ('llamada', f't_{tema}_llamada', parejo(llam), -15.0), ('notificacion', f't_{tema}_notificacion', parejo(notif), -15.0)):
            if piezas is None or clave in piezas: escribir(nombre, x, calidad=4, rms_db=rms)
        print(tema, '%.1f s' % (len(pres) / SR), '%.1f s' % (len(llam) / SR), flush=True)
    for tema, (T, pz, ac, mel, kw) in ARGS.items():
        if solo is not None and tema not in solo: continue
        if piezas is not None and 'anual' not in piezas: continue
        x = anual(T, pz, ac, mel, kw, ciclos=max(6, min(14, int(85 / (max([b + d for b, _, d in mel] + [16]) * pz)))))
        escribir(f's_{tema}_anual', x, calidad=1, rms_db=-19.0)
        print(tema, 'anual %.1f s' % (len(x) / SR), flush=True)
