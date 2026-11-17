"""Vestigium Mei — el sonido del día especial de quien sigues: `n_<tema>_historia` (parte 8, encargo A; `docs/diseno/CUADROS_PLAN.md`).

Un aviso más por tema, distinto del corto (`aviso`) y del solemne (`solemne`) de ese mismo tema: **tres notas que suben, sin
prisa, y una campanilla** (o lo que en el tema hace de campanilla: el sistro, el cascabel, el grillo, la campana del barco,
el yunque) que se queda sonando sobre la tercera, con su eco una octava más arriba. Dice «hoy pasa algo», no «haz algo».
Las notas, en la tónica y el modo del tema (mayor o menor), con sus mismos instrumentos y su misma sala.

Los instrumentos de cada tema viven **dentro** de su función en `afinados.py` (`romano()`, `heleno()`…). Para no copiarlos
(y que no se separen si alguien los afina), este guion corre cada función y guarda lo que deja definido (su `T` y sus
instrumentos) al terminar, con `sys.setprofile`; si un nombre deja de existir, falla con su nombre.

Duración: unos 2 s de motivo y su cola (3,5 a 4,5 s en total; el corto de los temas dura de 1,6 a 6,8 s, mediana 2,8, y el
solemne de 5,3 a 12,2, mediana 7). Volumen: el mismo de los avisos (`escribir(…, calidad=4, rms_db=-16)`, como el corto y
el solemne en `afinados.py`). Cada tema con su azar propio (`azar_propio`): agregar uno no mueve los demás.

Uso: python3 sonidos_historia.py <carpeta res/raw> [temas separados por comas]
"""
import sys
import afinados as A   # lee la carpeta de salida de sys.argv[1], como musica.py

# ------------------------------------------------------------------ los instrumentos de cada tema

def locales(tema):
    """Corre `afinados.<tema>()` y devuelve lo que dejó definido al terminar (su T, sus instrumentos y sus escalas)."""
    f = getattr(A, tema)
    vistos = {}
    def mirar(frame, evento, arg):
        if evento == 'return' and frame.f_code is f.__code__: vistos.update(frame.f_locals)
    sys.setprofile(mirar)
    try: f()
    finally: sys.setprofile(None)
    if 'T' not in vistos: raise SystemExit(f'{tema}: no encontré su T en afinados.py')
    return vistos

MAYOR, MENOR = [0, 4, 7], [0, 3, 7]

def motivo(voz, campanilla, notas, final, debajo=(), paso=0.3, d=0.5, d_final=1.25, g=0.5, gc=0.36, d_camp=1.6, sala=None):
    """
    Las tres notas [notas] (semitonos absolutos, desde La4) en [voz](n, d), una cada [paso] s, la última larga; sobre ella,
    a 0,1 s, la [campanilla](n, d) en [final] y su eco una octava más arriba, más suave. [debajo]: (tiempo, señal, ganancia)
    que acompañan (un tambor, el mar). [sala]: la reverberación del tema (por omisión, la de sus avisos).
    """
    t3 = paso * (len(notas) - 1)
    partes = [(paso * i, voz(n, d if i < len(notas) - 1 else d_final), g) for i, n in enumerate(notas)]
    partes += [(t3 + 0.1, campanilla(final, d_camp), gc), (t3 + 0.42, campanilla(final + 12, d_camp * 0.8), gc * 0.55)]
    partes += list(debajo)
    x = A.mezclar(*partes)
    return sala(x) if sala else A.reverb(x, 1.6, 0.3)

def campana_clara(n, d):
    """La campanilla de los temas que no tienen una propia: clara, de cobre, que se apaga despacio."""
    return A.campana(A.hz(n), d, 2.76, 1.1, 2.6)

def recetas(tema, L):
    """El motivo de [tema] con lo suyo ([L], lo que dejó `locales`)."""
    T = L['T']; hz = A.hz
    sube = lambda modo, octava=12: [T + octava + n for n in modo]
    if tema == 'romano':     # el cornu de bronce y, al final, la campana y el tambor del foro
        return motivo(L['cornu'], lambda n, d: A.campana(hz(n), d, 2.0, 1.5, 3), sube(MAYOR, 0), T + 24, [(0.6, A.tambor(hz(T - 24), 0.45), 0.5)])
    if tema == 'urbe':       # la mandolina en trémolo y la campanilla del tranvía
        return motivo(lambda n, d: L['mandolina'](n, d, 4), L['tranvia'], sube(MAYOR), T + 24)
    if tema == 'ciber':      # pitidos cuadrados de consola y un doble «bip» arriba
        bip = lambda n, d: A.mezclar((0, A.cuadrada(hz(n), 0.08, 0.1), 1), (0.12, A.cuadrada(hz(n), 0.08, 0.1), 0.7))
        return motivo(lambda n, d: A.cuadrada(hz(n), min(d, 0.3), 0.12), bip, sube(MENOR, 0), T + 24)
    if tema == 'neon':       # el sintetizador de sierra de los letreros y una campanita (sin la llovizna del corto)
        return motivo(L['ns'], lambda n, d: A.campana(hz(n), d, 3.0, 1.0, 5), sube(MENOR, 0), T + 24, gc=0.42)
    if tema == 'heleno':     # la lira y, en lugar de campanilla, el aulos que se queda
        return motivo(L['lira'], lambda n, d: A.flauta(hz(n), d * 0.8), sube(MAYOR), T + 24, gc=0.3)
    if tema == 'egipto':     # el arpa y el sistro
        return motivo(L['arpa'], lambda n, d: L['sistro'](0.45, 5), sube([0, 5, 7]), T + 24, gc=0.45)
    if tema == 'medieval':   # el canto llano y la campana del monasterio
        return motivo(lambda n, d: A.coro(hz(n), d), L['camp'], sube(MENOR), T + 24, d=0.55)
    if tema == 'templario':  # el cuerno y la campana de la capilla
        return motivo(lambda n, d: L['cuerno'](hz(n), d), L['capilla'], sube(MENOR, 0), T + 24)
    if tema == 'distopia':   # el órgano del ministerio, la máquina de escribir y su timbre
        teclas = [(0.08 * i, L['tecla'](), 0.4) for i in range(4)]
        return motivo(lambda n, d: A.organo(hz(n), d), L['ding'], sube(MENOR, 0), T + 12, teclas, g=0.45)
    if tema == 'refugio':    # el vibráfono de los cincuenta y una campanita
        return motivo(L['vib'], campana_clara, sube(MAYOR, 0), T + 12, [(0, A.clic(0.012), 0.5)])
    if tema == 'orden':      # la trompa, una octava más arriba que en el corto, y una señal metálica (sin el tambor del corto)
        return motivo(L['trompa'], lambda n, d: A.campana(hz(n), d, 3.0, 1.0, 5), sube(MENOR), T + 24, gc=0.42)
    if tema == 'vapor':      # el armonio de la estación y el tintineo del latón
        return motivo(lambda n, d: L['armonio'](hz(n), d), L['tink'], sube(MENOR, 0), T + 24, gc=0.5, d_camp=0.5)
    if tema == 'laboratorio':  # tonos fríos y una señal de vidrio, en su sala
        return motivo(lambda n, d: A.frio(hz(n), d), lambda n, d: A.campana(hz(n), d, 3.0, 1.0, 4), sube(MENOR, 0), T + 24, [(0, L['sub'](0.6), 0.35)], sala=L['sala'])
    if tema == 'colonos':    # la marimba de la junta y la campana de la escuela
        return motivo(lambda n, d: A.marimba(hz(n), d), lambda n, d: A.campana(hz(n), d, 2.0, 1.5, 3), sube(MAYOR, 0), T + 24)
    if tema == 'napoleon':   # el pífano de la banda (el corto es de corneta), el redoble y una campana
        return motivo(lambda n, d: A.flauta(hz(n), d), campana_clara, sube(MAYOR), T + 24, [(0.6, L['redoble'](0.3), 0.4)])
    if tema == 'deco':       # el piano del salón y el vibráfono
        return motivo(L['piano'], L['vibra'], sube(MAYOR), T + 19, [(0.6, L['escobilla'](0.2), 0.3)], gc=0.42)
    if tema == 'botanico':   # la marimba de madera y el grillo
        return motivo(L['madera'], lambda n, d: L['gr'](T + 36, 0.12, 3), sube(MAYOR), T + 24, gc=0.32)
    if tema == 'ruinas':     # la kalimba y el tubo que cuelga como campanilla de viento (sin el viento del corto)
        return motivo(L['kal'], L['tubo'], sube(MAYOR), T + 24, gc=0.3)
    if tema == 'navidad':    # la cajita de música y los cascabeles
        return motivo(lambda n, d: A.cajita(hz(n), d), lambda n, d: L['cascabel'](0.4), sube(MAYOR), T + 24, gc=0.4)
    if tema == 'seda':       # la cítara y el címbalo
        return motivo(L['citara'], L['cimbalo'], sube(MENOR), T + 24)
    if tema == 'edo':        # el koto y la campanilla de viento (fūrin)
        return motivo(L['koto'], L['furin'], sube(MENOR), T + 24)
    if tema == 'urss':       # las campanas del reloj de la torre (el corto es de bronce) y, al final, el bronce grave
        return motivo(lambda n, d: A.campana(hz(n), d * 1.4, 2.76, 1.2, 2.5), lambda n, d: L['bronce'](n, d), sube(MENOR), T, gc=0.3)
    if tema == 'gotico':     # el órgano y la campana, en la nave
        return motivo(lambda n, d: A.organo(hz(n), d), lambda n, d: A.campana(hz(n), d, 2.0, 2.5, 1.5), sube(MENOR, 0), T + 12, sala=lambda x: L['nave'](x, 2.4, 0.38))
    if tema == 'arcano':     # el cristal y su eco (la boya y el mar son del corto)
        return motivo(lambda n, d: L['cristal'](hz(n), d), lambda n, d: L['cristal'](hz(n), d), sube(MENOR, 0), T + 24, gc=0.4)
    if tema == 'espacio':    # pitidos de control y un tono puro
        return motivo(lambda n, d: A.pip(hz(n), min(d, 0.3)), L['tono'], sube(MAYOR), T + 24, gc=0.3)
    if tema == 'abismo':     # el sonar, el vibráfono bajo el agua y las burbujas
        return motivo(L['ping'], lambda n, d: L['vib'](hz(n), d), sube(MENOR), T + 24, [(0.7, L['burbuja'](T + 12), 0.3), (0.85, L['burbuja'](T + 16), 0.25)])
    if tema == 'andalus':    # el laúd (oud) en su modo hijaz, el qanún y el darbuka
        hj = L['hijaz']
        return motivo(lambda n, d: A.pluck(hz(n), d, 0.35, 0.994), lambda n, d: A.pluck(hz(n), d, 0.7, 0.993), [T + hj[0], T + hj[2], T + hj[4]], T + 24,
                      [(0, L['dum'](), 0.4), (0.6, L['dum'](), 0.4), (0.7, L['tek'](), 0.3)], gc=0.45)
    if tema == 'meso':       # la flauta de barro y la sonaja
        return motivo(lambda n, d: A.flauta(hz(n), d), lambda n, d: L['sonaja'](0.3, 5), sube(MAYOR), T + 24, [(0.6, L['tortuga'](), 0.4)], gc=0.45)
    if tema == 'mongol':     # el morin juur (arco) en pentatónica, las campanillas del arnés y el galope
        pe = L['penta']
        return motivo(lambda n, d: A.arco(hz(n), d), campana_clara, [T + pe[0], T + pe[2], T + pe[3]], T + 24, [(0, L['galope'](), 0.3)])
    if tema == 'renacimiento':  # el laúd y una campanita
        return motivo(L['laud'], lambda n, d: A.campana(hz(n), d, 2.0, 1.4, 3), sube(MAYOR), T + 24)
    if tema == 'pirata':     # el violín de a bordo y una campanilla clara (la campana del barco y el mar son del corto)
        return motivo(L['violin'], campana_clara, sube(MENOR, 0), T + 24, gc=0.3)
    if tema == 'revolucion':  # el pífano, el redoble y una campana
        return motivo(L['pifano'], campana_clara, sube(MAYOR, 0), T + 24, [(0.6, L['redoble'](0.3), 0.45)])
    if tema == 'antiguas':   # el arpa y una campana de piedra
        return motivo(L['arpa'], campana_clara, sube(MENOR, 0), T + 24)
    if tema == 'cueva':      # la flauta de hueso y la laja que suena como campana, con el eco de la cueva
        return motivo(L['hueso'], L['laja'], sube(MENOR), T + 24, [(0.6, L['piel'](0.3), 0.3)], sala=lambda x: L['eco'](x, 2.0, 0.34))
    if tema == 'frente':     # el sintetizador pulsado y el pitido de la consola
        return motivo(lambda n, d: L['pulsado'](hz(n), d), lambda n, d: L['pit'](n, 0.12), sube(MENOR, 0), T + 24, gc=0.42)
    if tema == 'arcontes':   # el kinnor y los crótalos
        return motivo(L['kin'], L['crot'], sube(MENOR, 0), T + 24, gc=0.3)
    if tema == 'galia':      # la lira del bardo y el yunque
        return motivo(L['lir'], L['yun'], sube(MAYOR, 0), T + 24, gc=0.3)
    if tema == 'nouveau':    # el piano de salón y el cristal
        return motivo(L['pia'], L['cri'], sube(MAYOR, 0), T + 24, gc=0.32)
    if tema == 'psicodelia':  # el órgano de transistores (la ciudad) y una campanita, con su eco corto
        x = motivo(L['org'], campana_clara, sube(MAYOR, 0), T + 24, sala=lambda x: x, g=0.42)
        return A.reverb(L['corto'](x), 1.4, 0.28)
    raise SystemExit(f'{tema}: no tiene receta')

TEMAS = ['romano', 'urbe', 'ciber', 'neon', 'heleno', 'egipto', 'medieval', 'templario', 'distopia', 'refugio', 'orden', 'vapor',
         'laboratorio', 'colonos', 'napoleon', 'deco', 'botanico', 'ruinas', 'navidad', 'seda', 'edo', 'urss', 'gotico', 'arcano',
         'espacio', 'abismo', 'andalus', 'meso', 'mongol', 'renacimiento', 'pirata', 'revolucion', 'antiguas', 'cueva', 'frente',
         'arcontes', 'galia', 'nouveau', 'psicodelia']

def parejo(x, rms_db=-16.0, pico=0.85, holgura=1.5):
    """
    Los motivos de cuerda pulsada o de sonaja tienen picos tan altos que, al escribirlos con su pico en [pico], quedan más
    bajos que los demás avisos. Si les faltan más de [holgura] dB para [rms_db], se les baja el golpe con el compresor suave
    de `afinados` (`domar`, que no recorta la onda), cada vez un poco más, hasta tres veces.
    """
    import numpy as np
    def falta(y):
        y = y / (np.abs(y).max() + 1e-12) * pico
        return rms_db - 20 * np.log10(np.sqrt(np.mean(y ** 2)) + 1e-12)
    for umbral in (0.45, 0.32, 0.22):
        if falta(x) <= holgura: break
        x = A.domar(x, umbral, 3.0)
    return x

if __name__ == '__main__':
    solo = sys.argv[2].split(',') if len(sys.argv) > 2 else TEMAS
    for tema in solo:
        L = locales(tema)
        with A.azar_propio('historia_' + tema):
            x = parejo(recetas(tema, L))
        A.escribir(f'n_{tema}_historia', x, calidad=4, rms_db=-16.0)
        print('listo', tema, flush=True)
