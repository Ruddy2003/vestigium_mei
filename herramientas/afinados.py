"""Vestigium Mei — todos los sonidos, afinados.

Cada tema tiene una tónica y una escala; todo lo que tiene altura (campanas, laúdes, cornetas, pitidos, órgano…) sale de esa
escala, y lo que suena junto forma acordes de ella. Lo que no tiene altura (papel, pluma, madera, máquina de escribir) queda
como ruido. Además: el «tema» (lo que suena al elegir ese tema), las teclas del diario y el rascado del mapa.

Uso: python3 afinados.py <carpeta res/raw>
"""
import os, sys, subprocess, tempfile, wave
from contextlib import contextmanager
import numpy as np
from scipy.signal import fftconvolve, butter, sosfilt

SR = 22050
OUT = sys.argv[1]
rng = np.random.default_rng(11)

# ------------------------------------------------------------------ bloques

def t_(d): return np.arange(int(d * SR)) / SR
def env(n, a=0.005, d=0.2, curva=4.0):
    t = np.arange(n) / SR
    return np.minimum(1, t / max(a, 1e-4)) * np.exp(-np.maximum(0, t - a) * curva / max(d, 1e-4))
def seno(f, d, fase=0.0): return np.sin(2 * np.pi * f * t_(d) + fase)
def barrido(f0, f1, d, forma='seno'):
    t = t_(d); f = f0 * (f1 / f0) ** (t / d)
    ph = 2 * np.pi * np.cumsum(f) / SR
    if forma == 'cuadrada': return np.tanh(4 * np.sin(ph)) * 0.6
    if forma == 'sierra': return ((ph / (2 * np.pi)) % 1) * 2 - 1
    return np.sin(ph)
def ruido(d): return rng.standard_normal(int(d * SR))
def filtro(x, tipo, f, orden=2):
    if tipo == 'banda':
        sos = butter(orden, [max(20, f[0]) / (SR / 2), min(f[1], SR / 2 * 0.98) / (SR / 2)], 'bandpass', output='sos')
    else:
        sos = butter(orden, min(f, SR / 2 * 0.98) / (SR / 2), 'lowpass' if tipo == 'bajo' else 'highpass', output='sos')
    return sosfilt(sos, x)
def mezclar(*partes):
    partes = [(0, x, 1.0) if isinstance(x, np.ndarray) else x for x in partes]
    fin = max(int(s * SR) + len(x) for s, x, g in partes)
    out = np.zeros(fin)
    for s, x, g in partes:
        i = int(s * SR); out[i:i + len(x)] += x * g
    return out
def reverb(x, cola=1.2, mezcla=0.25, brillo=4000):
    n = int(cola * SR)
    ir = rng.standard_normal(n) * np.exp(-np.arange(n) / SR * 6 / cola)
    ir = filtro(ir, 'bajo', brillo); ir /= np.sqrt(np.sum(ir ** 2))
    seco = np.concatenate([x, np.zeros(n)])
    w = fftconvolve(x, ir)[:len(seco)]
    w = np.concatenate([w, np.zeros(len(seco) - len(w))])
    return seco * (1 - mezcla) + w * mezcla
def recortar(x, umbral=0.002):
    a = np.abs(x); idx = np.nonzero(a > umbral * a.max())[0]
    if len(idx) == 0: return x
    x = x[:min(len(x), idx[-1] + int(0.02 * SR))].copy()
    k = min(len(x), int(0.01 * SR)); x[-k:] *= np.linspace(1, 0, k)
    return x
def escribir(nombre, x, pico=0.85, calidad=3, rms_db=-19.0):
    x = recortar(np.asarray(x, dtype=np.float64))
    rms = np.sqrt(np.mean(x ** 2)) + 1e-12
    g = min(10 ** (rms_db / 20) / rms, pico / max(1e-9, np.abs(x).max()))
    x = x * g
    with tempfile.NamedTemporaryFile(suffix='.wav', delete=False) as f: ruta = f.name
    with wave.open(ruta, 'wb') as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes((x * 32767).astype(np.int16).tobytes())
    subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', ruta, '-c:a', 'libvorbis', '-q:a', str(calidad), os.path.join(OUT, nombre + '.ogg')], check=True)
    os.remove(ruta)

def hz(n): return 440 * 2 ** (n / 12)          # semitonos desde La4

# ------------------------------------------------------------------ instrumentos (todos con altura exacta)

def pluck(f, d, brillo=0.5, decaimiento=0.996):
    """Cuerda pulsada (Karplus-Strong). Con el promedio hacia adelante el lazo dura p − ½ muestras; se remuestrea para que la nota quede exacta."""
    p = max(3, int(round(SR / f + 0.5)))
    r = f / (SR / (p - 0.5))
    n = int(d * SR); n_src = int(n * r) + 4
    buf = rng.uniform(-1, 1, p)
    if p > 8: buf = filtro(buf, 'bajo', 1500 + brillo * 6000)
    out = np.zeros(n_src); idx = 0
    for i in range(n_src):
        out[i] = buf[idx]; nxt = (idx + 1) % p
        buf[idx] = decaimiento * 0.5 * (buf[idx] + buf[nxt]); idx = nxt
    x = np.interp(np.arange(n) * r, np.arange(n_src), out)
    return x * env(n, 0.002, d, 2.0)

def campana(f, d, ratio=2.0, indice=2.0, decaimiento=3.0):
    t = t_(d)
    mod = indice * np.exp(-t * decaimiento) * np.sin(2 * np.pi * f * ratio * t)
    return np.sin(2 * np.pi * f * t + mod) * np.exp(-t * decaimiento * 0.6) * np.minimum(1, t / 0.002)
def vibrafono(f, d):
    t = t_(d)
    x = np.sin(2 * np.pi * f * t) + 0.25 * np.sin(2 * np.pi * f * 4 * t) * np.exp(-t * 8)
    return x * np.exp(-t * 2.2) * (1 + 0.25 * np.sin(2 * np.pi * 5.5 * t)) * np.minimum(1, t / 0.003)
def cajita(f, d=0.9):
    """Caja de música: púa de metal brillante."""
    t = t_(d)
    x = np.sin(2 * np.pi * f * t) + 0.35 * np.sin(2 * np.pi * f * 2 * t) * np.exp(-t * 6) + 0.12 * np.sin(2 * np.pi * f * 3 * t) * np.exp(-t * 10)
    return x * np.exp(-t * 3.5) * np.minimum(1, t / 0.002)
def organo(f, d, ataque=0.06):
    t = t_(d)
    x = sum(a * np.sin(2 * np.pi * f * k * t) for k, a in ((1, 1), (2, 0.5), (3, 0.25), (4, 0.18), (6, 0.08), (8, 0.06)))
    return x * np.minimum(1, t / ataque) * np.minimum(1, (d - t) / 0.15)
def coro(f, d):
    """Voz sostenida (canto llano): vocal «a» suave."""
    t = t_(d)
    x = sum(a * np.sin(2 * np.pi * f * k * t * (1 + 0.003 * np.sin(2 * np.pi * 4.8 * t))) for k, a in ((1, 1), (2, 0.6), (3, 0.35), (4, 0.15), (5, 0.08)))
    return filtro(x, 'banda', (250, 2600)) * np.minimum(1, t / 0.15) * np.minimum(1, (d - t) / 0.25)
def arco(f, d, vibrato=5.5):
    t = t_(d)
    fv = f * (1 + 0.006 * np.sin(2 * np.pi * vibrato * t) * np.minimum(1, t / 0.3))
    ph = 2 * np.pi * np.cumsum(fv) / SR
    x = filtro(((ph / (2 * np.pi)) % 1) * 2 - 1, 'bajo', f * 6)
    return x * np.minimum(1, t / 0.08) * np.minimum(1, (d - t) / 0.2)
def flauta(f, d):
    t = t_(d)
    x = np.sin(2 * np.pi * f * t * (1 + 0.004 * np.sin(2 * np.pi * 5 * t))) + 0.15 * np.sin(4 * np.pi * f * t)
    x += 0.04 * filtro(ruido(d), 'banda', (f, f * 3))
    return x * np.minimum(1, t / 0.05) * np.minimum(1, (d - t) / 0.12)
def metal(f, d, brillo=3500, ataque=0.03):
    """Corneta, cornu o trompa: armónicos llenos y ataque de labio."""
    t = t_(d)
    x = sum(a * np.sin(2 * np.pi * f * k * t) for k, a in ((1, 1), (2, 0.75), (3, 0.55), (4, 0.35), (5, 0.22), (6, 0.12)))
    return filtro(x, 'bajo', brillo) * np.minimum(1, t / ataque) * np.minimum(1, (d - t) / 0.06)
def cuadrada(f, d, decae=0.06):
    t = t_(d); x = np.tanh(3 * np.sin(2 * np.pi * f * t))
    return filtro(x, 'bajo', 6000) * env(len(t), 0.002, decae, 3)
def pip(f, d=0.06): return seno(f, d) * env(int(d * SR), 0.002, d * 0.8, 2)
def kalimba(f, d=0.8):
    t = t_(d)
    return (np.sin(2 * np.pi * f * t) + 0.25 * np.sin(2 * np.pi * f * 5.4 * t) * np.exp(-t * 20)) * np.exp(-t * 4) * np.minimum(1, t / 0.003)
def marimba(f, d=0.6):
    t = t_(d)
    return (np.sin(2 * np.pi * f * t) + 0.3 * np.sin(2 * np.pi * f * 4 * t) * np.exp(-t * 25) + 0.1 * np.sin(2 * np.pi * f * 10 * t) * np.exp(-t * 60)) * np.exp(-t * 6) * np.minimum(1, t / 0.002)
def tambor(f0, d, ruido_mix=0.2):
    t = t_(d); f = f0 * (1 + 0.5 * np.exp(-t * 60))
    ph = 2 * np.pi * np.cumsum(f) / SR
    return np.sin(ph) * np.exp(-t * 9) + ruido_mix * filtro(ruido(d), 'bajo', 2500) * np.exp(-t * 30)
def clic(d=0.02, f=(2000, 6000)): return filtro(ruido(d), 'banda', f) * env(int(d * SR), 0.0005, d * 0.5, 6)
def papel(d=0.2, f=(700, 4500)):
    return filtro(ruido(d), 'banda', f) * env(int(d * SR), d * 0.25, d * 0.6, 3) * (0.6 + 0.4 * np.sin(np.linspace(0, 8, int(d * SR))))
def susurro(d=0.3):
    x = filtro(ruido(d), 'banda', (1500, 7000))
    am = 0.5 + 0.5 * np.abs(filtro(ruido(d), 'bajo', 30))
    return x * am * np.sin(np.pi * t_(d) / d)
def pajaro(d, f0, f1):
    t = t_(d); f = f0 + (f1 - f0) * (t / d) ** 0.7
    ph = 2 * np.pi * np.cumsum(f * (1 + 0.04 * np.sin(2 * np.pi * 60 * t))) / SR
    return np.sin(ph) * np.sin(np.pi * t / d) ** 2
def sinte(f, d, corte=1400, ataque=0.12, suelta=0.35):
    """Colchón de sintetizador frío: dos sierras apenas desafinadas, filtradas, que entran y salen despacio."""
    t = t_(d)
    x = barrido(f, f, d, 'sierra') * 0.5 + barrido(f * 1.004, f * 1.004, d, 'sierra') * 0.5 + 0.3 * np.sin(2 * np.pi * f * 0.5 * t)
    return filtro(x, 'bajo', corte) * np.minimum(1, t / ataque) * np.minimum(1, np.maximum(0, d - t) / suelta)
def frio(f, d=0.5):
    """Tono de instrumento de laboratorio: seno puro con un armónico de vidrio que se apaga rápido."""
    t = t_(d)
    return (np.sin(2 * np.pi * f * t) + 0.18 * np.sin(2 * np.pi * f * 3.01 * t) * np.exp(-t * 18)) * np.exp(-t * 4.5) * np.minimum(1, t / 0.004)
def grillo(f, d=0.14, pulsos=3):
    """Un grillo: ráfagas cortas sobre una nota aguda de la escala (frota las alas unas 30 veces por segundo)."""
    t = t_(d); x = np.sin(2 * np.pi * f * t)
    am = np.clip(np.sin(2 * np.pi * 30 * t), 0, 1) ** 2 * (t < pulsos / 30.0)
    return x * am * np.minimum(1, t / 0.003)
def fuelle(f, d, ataque=0.05):
    """Concertina: dos lengüetas apenas desafinadas que baten entre sí, con el aire del fuelle."""
    t = t_(d)
    x = sum(np.tanh(2.2 * np.sin(2 * np.pi * f * k * t)) * a for k, a in ((1.0, 0.6), (1.006, 0.5)))
    x = filtro(x, 'bajo', 3200) + 0.03 * filtro(ruido(d), 'banda', (800, 3000))
    return x * np.minimum(1, t / ataque) * np.minimum(1, np.maximum(0, d - t) / 0.12)
def guitarra(f, d=1.2, brillo=0.32): return pluck(f, d, brillo, 0.996)
def acorde(inst, notas, d, g=1.0, arpegio=0.0, **kw):
    return mezclar(*[(i * arpegio, inst(hz(n), d, **kw), g / len(notas) ** 0.5) for i, n in enumerate(notas)])
def melodia(inst, notas, paso, d, g=0.5, **kw):
    return mezclar(*[(i * paso, inst(hz(n), d, **kw), g) for i, n in enumerate(notas) if n is not None])

# ------------------------------------------------------------------ error, cambio, tecla y rascado (lote 6)

X = {}   # archivo → señal: las teclas del diario y el rascado del mapa, que no llevan el prefijo s_<tema>_

@contextmanager
def azar_propio(nombre):
    """Los sonidos del lote 6 usan su propio azar, sembrado con su nombre: agregarlos no mueve el de los de antes."""
    global rng
    antes = rng; rng = np.random.default_rng(sum(ord(c) * (i + 1) for i, c in enumerate(nombre)))
    try: yield
    finally: rng = antes

def nuevos(tema, error, cambio, tecla, rascar):
    """El error (algo salió mal: siempre baja y se apaga), el cambio (encender o apagar algo), la tecla del diario y el rascado."""
    S[tema].update(error=error, cambio=cambio)
    X[f's_tecla_{tema}'] = tecla; X[f's_rascar_{tema}'] = rascar

def rascado(banda, rebaba, aspereza=90, pasadas=((0.0, 0.28), (0.33, 0.28), (0.66, 0.3))):
    """Una moneda que raspa la lámina: tres pasadas con el ruido de su metal ([banda]) y la [rebaba], el metal que resuena al final."""
    fin = pasadas[-1][0] + pasadas[-1][1]
    x = np.zeros(int((fin + 0.02) * SR))
    for ini, dur in pasadas:
        n = int(dur * SR); r = filtro(ruido(dur), 'banda', banda) * (0.5 + 0.5 * np.abs(filtro(ruido(dur), 'bajo', aspereza)))
        r *= np.sin(np.pi * np.arange(n) / n) ** 0.6
        k = int(ini * SR); x[k:k + n] += r
    return mezclar((0, x, 1.0), (fin - 0.08, rebaba, 0.22))

def trazo(banda, d=0.045, ondas=20, caida=0.025):
    """Una tecla de escritura a mano: la punta que raspa (pluma, cálamo, lápiz, pincel), con su temblor."""
    return filtro(ruido(d), 'banda', banda) * env(int(d * SR), 0.004, caida, 4) * (0.7 + 0.3 * np.sin(np.linspace(0, ondas, int(d * SR))))

# ------------------------------------------------------------------ los temas

S = {}   # tema → dict(toque, elegir, pasar, abrir, guardar, logro, tema, aviso, solemne)

def romano():
    # Re mayor: tink de mármol, cornu de bronce y tambor.
    T = -7   # Re4
    marmol = lambda n, d=0.07: mezclar((0, clic(0.012, (1500, 5000)), 0.6), (0, seno(hz(n), d) * env(int(d * SR), 0.001, d * 0.6), 0.6))
    cornu = lambda n, d: metal(hz(n), d, 3000)
    S['romano'] = dict(
        toque=marmol(T + 24), elegir=marmol(T + 31, 0.08),
        pasar=papel(0.16, (900, 3500)),
        abrir=mezclar((0, filtro(ruido(0.4), 'banda', (500, 2500)) * np.linspace(0.2, 1, int(0.4 * SR)) * env(int(0.4 * SR), 0.2, 0.25, 2), 0.6), (0.36, tambor(hz(T - 24), 0.3), 0.6)),
        guardar=mezclar((0, tambor(hz(T - 24), 0.3, 0.3), 0.7), (0.02, campana(hz(T + 12), 1.0, 2.0, 1.5, 3), 0.45)),
        logro=reverb(mezclar((0, cornu(T, 0.18), 0.5), (0.2, cornu(T + 4, 0.18), 0.5), (0.4, cornu(T + 7, 0.18), 0.5), (0.6, cornu(T + 12, 0.6), 0.6),
                             (0.6, cornu(T + 7, 0.6), 0.3), (0.6, tambor(hz(T - 24), 0.4), 0.6)), 1.4, 0.25),
        tema=reverb(mezclar((0, cornu(T + 7, 0.16), 0.5), (0.18, cornu(T + 12, 0.45), 0.55), (0.18, campana(hz(T + 24), 1.0, 2.0, 1.2, 3), 0.25)), 1.2, 0.25),
        aviso=reverb(mezclar(*[(0.22 * i, cornu(T + n, 0.2 if i < 4 else 0.7), 0.5) for i, n in enumerate([7, 12, 16, 19, 12])], (0.88, tambor(hz(T - 24), 0.4), 0.5)), 1.5, 0.28),
        solemne=reverb(mezclar(*[(0.9 * i, acorde(lambda f, d: metal(f, d, 2500), [T + a for a in ch], 0.85), 0.55) for i, ch in enumerate([[0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12]])],
                               *[(0.9 * i, tambor(hz(T - 24), 0.5), 0.5) for i in range(4)], (2.7, campana(hz(T + 24), 2.0, 2.0, 1.5, 2), 0.3)), 2.0, 0.3))

    # Error: el cornu que se quiebra medio tono abajo. Cambio: dos golpes de mármol que suben a la quinta.
    # Tecla: el estilo sobre la tablilla de cera. Rascar: oro imperial, blando y cálido.
    with azar_propio('romano'):
        nuevos('romano',
               error=mezclar((0, cornu(T + 7, 0.12), 0.45), (0.13, cornu(T + 6, 0.3), 0.45), (0.13, tambor(hz(T - 24), 0.3, 0.2), 0.4)),
               cambio=mezclar((0, marmol(T + 19), 1), (0.08, marmol(T + 26), 1)),
               tecla=mezclar((0, trazo((1200, 4200), 0.04, 10), 1), (0, filtro(ruido(0.015), 'bajo', 700) * env(int(0.015 * SR), 0.001, 0.008), 0.3)),
               rascar=rascado((1400, 6000), campana(hz(T + 24), 0.7, 2.0, 1.0, 4)))

def ciber():
    # Mi menor: blips de onda cuadrada y arpegios de sintetizador.
    T = 7    # Mi5
    S['ciber'] = dict(
        toque=cuadrada(hz(T + 12), 0.04, 0.03), elegir=cuadrada(hz(T + 19), 0.045, 0.035),
        pasar=barrido(hz(T - 12), hz(T + 12), 0.09, 'cuadrada') * env(int(0.09 * SR), 0.002, 0.08, 2),
        abrir=melodia(lambda f, d: cuadrada(f, d, 0.04), [T, T + 3, T + 7, T + 12], 0.045, 0.06),
        guardar=melodia(lambda f, d: cuadrada(f, d, 0.06), [T + 7, T + 12], 0.07, 0.09),
        logro=reverb(mezclar(melodia(lambda f, d: cuadrada(f, d, 0.08), [T, T + 3, T + 7, T + 10, T + 12, T + 14, T + 19], 0.06, 0.12, g=0.45),
                             (0.42, filtro(barrido(hz(T - 24), hz(T - 24), 0.6, 'sierra'), 'bajo', 900) * env(int(0.6 * SR), 0.01, 0.5), 0.5)), 1.0, 0.25),
        tema=mezclar((0, filtro(ruido(0.08), 'alto', 3000) * env(int(0.08 * SR), 0.001, 0.05), 0.3), (0.06, melodia(lambda f, d: cuadrada(f, d, 0.05), [T, T + 7, T + 12, T + 19], 0.04, 0.06), 1)),
        aviso=reverb(mezclar(*[(0.13 * i, cuadrada(hz(T + n), 0.12, 0.1), 0.5) for i, n in enumerate([12, 7, 3, 7, 12, 15, 19])],
                             (0.0, filtro(barrido(hz(T - 24), hz(T - 24), 1.0, 'sierra'), 'bajo', 700) * np.minimum(1, t_(1.0) / 0.02) * np.minimum(1, (1.0 - t_(1.0)) / 0.2), 0.35)), 1.2, 0.3),
        solemne=reverb(mezclar(*[(0.8 * i, acorde(lambda f, d: filtro(barrido(f, f, d, 'sierra'), 'bajo', 1800) * np.minimum(1, t_(d) / 0.2) * np.minimum(1, (d - t_(d)) / 0.3), [T - 12 + a for a in ch], 0.85), 0.5)
                                 for i, ch in enumerate([[0, 3, 7], [-4, 0, 3], [-7, -3, 0], [-5, -2, 2], [0, 3, 7, 12]])],
                               *[(0.2 * i, cuadrada(hz(T + [12, 19, 15, 19][i % 4]), 0.08, 0.06), 0.18) for i in range(20)]), 1.8, 0.35))

    # Error: el zumbido que baja un tritono. Cambio: un interruptor de bits que sube a la octava.
    # Tecla: la tecla de membrana con su blip. Rascar: la placa de consola, con chisporroteo de fósforo.
    with azar_propio('ciber'):
        nuevos('ciber',
               error=mezclar((0, cuadrada(hz(T), 0.08, 0.06), 0.5), (0.09, cuadrada(hz(T - 6), 0.2, 0.12), 0.5), (0.09, filtro(ruido(0.12), 'bajo', 1200) * env(int(0.12 * SR), 0.002, 0.08), 0.25)),
               cambio=melodia(lambda f, d: cuadrada(f, d, 0.03), [T + 12, T + 24], 0.035, 0.05),
               tecla=mezclar((0, clic(0.007, (3500, 9500)), 0.7), (0.002, cuadrada(hz(T + 31), 0.012, 0.006), 0.08)),
               rascar=rascado((2500, 9500), mezclar(*[(0.03 * i, cuadrada(hz(T + n), 0.05, 0.03), 0.4) for i, n in enumerate([24, 19, 31])]), aspereza=160))

def neon():
    # Fa sostenido menor con séptima: synth-pop de bar. Pulsos de sierra tibios, el zumbido del transformador de un
    # letrero y la llovizna de fondo.
    T = -3   # Fa#4
    ns = lambda n, d=0.18, c=2400: filtro(barrido(hz(n), hz(n), d, 'sierra'), 'bajo', c) * env(int(d * SR), 0.003, d * 0.7, 3)
    zumbido = lambda d: (filtro(barrido(120, 120, d, 'sierra'), 'bajo', 600) * 0.5 + filtro(ruido(d), 'banda', (5000, 9000)) * 0.15) * env(int(d * SR), 0.01, d * 0.6, 2)
    llovizna = lambda d: filtro(ruido(d), 'banda', (1500, 7000)) * env(int(d * SR), d * 0.3, d * 0.6, 2) * 0.25
    # Con su propio azar también los sonidos de siempre: un tema nuevo no mueve el ruido de los que van después.
    with azar_propio('neon_base'):
        S['neon'] = dict(
            toque=mezclar((0, ns(T + 12, 0.07, 3000), 0.8), (0, zumbido(0.05), 0.25)), elegir=ns(T + 19, 0.09, 2800),
            pasar=mezclar((0, llovizna(0.16), 0.9), (0.02, ns(T + 7, 0.08, 1800), 0.35)),
            abrir=mezclar((0, zumbido(0.12), 0.5), (0.1, melodia(lambda f, d: filtro(barrido(f, f, d, 'sierra'), 'bajo', 2600) * env(int(d * SR), 0.003, d * 0.7, 3), [T, T + 3, T + 7], 0.05, 0.12), 0.8)),
            guardar=mezclar((0, ns(T + 7, 0.1), 0.8), (0.09, ns(T + 15, 0.22), 0.8), (0.09, zumbido(0.2), 0.2)),
            logro=reverb(mezclar(melodia(lambda f, d: filtro(barrido(f, f, d, 'sierra'), 'bajo', 3200) * env(int(d * SR), 0.003, d * 0.7, 3), [T, T + 3, T + 7, T + 10, T + 12, T + 15], 0.08, 0.22, g=0.5),
                                 (0.5, acorde(lambda f, d: sinte(f, d, 2000, 0.05, 0.3), [T - 12 + a for a in (0, 3, 7, 10)], 0.9), 0.5), (0, zumbido(0.5), 0.15)), 1.3, 0.3),
            tema=mezclar((0, zumbido(0.3), 0.6), (0.12, zumbido(0.08), 0.0), (0.22, melodia(lambda f, d: filtro(barrido(f, f, d, 'sierra'), 'bajo', 2800) * env(int(d * SR), 0.003, d * 0.7, 3), [T + 7, T + 10, T + 15], 0.06, 0.16), 0.9)),
            aviso=reverb(mezclar(*[(0.16 * i, ns(T + n, 0.16 if i < 5 else 0.5), 0.55) for i, n in enumerate([0, 7, 10, 15, 12, 10])], (0, llovizna(1.1), 0.5)), 1.2, 0.3),
            solemne=reverb(mezclar(*[(0.85 * i, acorde(lambda f, d: sinte(f, d, 1600, 0.15, 0.4), [T - 12 + a for a in ch], 0.9), 0.55)
                                     for i, ch in enumerate([[0, 3, 7, 10], [-4, 0, 3, 7], [-7, -3, 0, 5], [-2, 2, 5, 9], [0, 3, 7, 10]])],
                                   (0, llovizna(4.2), 0.6), *[(0.85 * i + 0.4, ns(T + [15, 12, 10, 14, 19][i], 0.2), 0.25) for i in range(5)]), 1.8, 0.35))

    # Error: el letrero que hace corto y se apaga con un chasquido. Cambio: el interruptor del letrero y su zumbido al prender.
    # Tecla: la tecla de la caja registradora del puesto. Rascar: la cortina metálica que sube a jalones.
    with azar_propio('neon'):
        nuevos('neon',
               error=mezclar((0, zumbido(0.12), 0.8), (0.1, clic(0.02, (800, 4000)), 0.6), (0.13, ns(T - 6, 0.25, 900), 0.6)),
               cambio=mezclar((0, clic(0.012, (1500, 5000)), 0.6), (0.015, zumbido(0.14), 0.6), (0.03, ns(T + 12, 0.06, 2600), 0.4)),
               tecla=mezclar((0, clic(0.01, (1200, 5000)), 0.8), (0.004, filtro(barrido(hz(T + 36), hz(T + 36), 0.03, 'cuadrada'), 'bajo', 5000) * env(int(0.03 * SR), 0.001, 0.02), 0.12)),
               rascar=rascado((300, 3000), mezclar((0, tambor(hz(T - 24), 0.3, 0.6), 0.6), (0.02, clic(0.03, (600, 2500)), 0.6)), aspereza=40,
                              pasadas=((0.0, 0.18), (0.24, 0.2), (0.5, 0.22), (0.78, 0.2))))

def heleno():
    # La mayor pentatónico: lira.
    T = -12  # La3
    lira = lambda n, d=0.8, b=0.5: pluck(hz(n), d, b)
    pent = [0, 2, 4, 7, 9]
    S['heleno'] = dict(
        toque=lira(T + 24, 0.3, 0.6), elegir=lira(T + 19, 0.35, 0.6), pasar=lira(T, 0.5, 0.3),
        abrir=melodia(lambda f, d: pluck(f, d, 0.5), [T + 12, T + 16, T + 19, T + 24], 0.09, 0.8),
        guardar=mezclar((0, lira(T + 12, 0.9), 0.5), (0, lira(T + 19, 0.9), 0.4), (0.22, lira(T + 24, 1.0), 0.5)),
        logro=reverb(melodia(lambda f, d: pluck(f, d, 0.55), [T + 12, T + 16, T + 19, T + 21, T + 19, T + 24], 0.13, 0.9), 1.2, 0.25),
        tema=reverb(melodia(lambda f, d: pluck(f, d, 0.6), [T + 12 + p for p in pent] + [T + 24], 0.05, 0.9, g=0.4), 1.0, 0.25),
        aviso=reverb(mezclar(melodia(lambda f, d: pluck(f, d, 0.5), [T + 19, T + 21, T + 19, T + 16, T + 12, None, T + 16, T + 19], 0.2, 0.9), (0, lira(T - 12, 2.0, 0.2), 0.3)), 1.4, 0.28),
        solemne=reverb(mezclar(melodia(flauta, [T + 24, T + 26, T + 28, T + 31, T + 28, T + 26, T + 24, T + 21, T + 24], 0.45, 0.43, g=0.3),
                               *[(1.8 * i, acorde(lambda f, d: pluck(f, d, 0.4), [T + a for a in ch], 1.8, arpegio=0.04), 0.6) for i, ch in enumerate([[0, 7, 12, 16], [-3, 4, 9, 12], [2, 9, 14, 21], [0, 7, 12, 16, 24]])]), 2.0, 0.3))

    # Error: la lira apagada que roza el semitono. Cambio: una cuerda y su quinta. Tecla: el cálamo sobre el papiro.
    # Rascar: electro, la aleación pálida de oro y plata.
    with azar_propio('heleno'):
        nuevos('heleno',
               error=mezclar((0, lira(T + 12, 0.25, 0.2), 0.5), (0.12, lira(T + 11, 0.45, 0.15), 0.5)),
               cambio=mezclar((0, lira(T + 19, 0.3, 0.6), 0.5), (0.07, lira(T + 26, 0.35, 0.6), 0.5)),
               tecla=trazo((1800, 5600), 0.05, 14),
               rascar=rascado((1600, 6500), campana(hz(T + 31), 0.6, 2.4, 1.0, 5)))

def egipto():
    # Mi con la segunda menor (escala propia: no se sabe cómo afinaban): el arpa del templo, el sistro de Hathor, el tambor
    # de marco y la flauta de caña.
    T = -5   # Mi4
    arpa = lambda n, d=0.8, b=0.42: pluck(hz(n), d, b, 0.9965)
    def sistro(d=0.2, k=4):
        """El sistro: las plaquitas de metal que chocan en sus varillas, unas sacudidas cortas que se apagan."""
        return mezclar(*[(i * d / k, filtro(ruido(d / k + 0.05), 'banda', (3500, 9000)) * env(int((d / k + 0.05) * SR), 0.001, 0.04, 4), 0.6 - i * 0.08) for i in range(k)])
    escala = [0, 1, 5, 7, 8]
    with azar_propio('egipto_base'):
        S['egipto'] = dict(
            toque=arpa(T + 12, 0.35, 0.45), elegir=arpa(T + 19, 0.4, 0.5), pasar=sistro(0.2, 4),
            abrir=mezclar((0, sistro(0.12, 3), 0.5), (0.05, melodia(lambda f, d: pluck(f, d, 0.45, 0.9965), [T + 12, T + 13, T + 17, T + 19], 0.08, 0.8), 0.9)),
            guardar=mezclar((0, tambor(hz(T - 24), 0.3, 0.5), 0.7), (0.05, arpa(T + 12, 0.9), 0.45), (0.05, arpa(T + 19, 0.9), 0.35)),
            logro=reverb(mezclar(melodia(lambda f, d: pluck(f, d, 0.5, 0.9965), [T + 12, T + 13, T + 17, T + 19, T + 20, T + 24], 0.12, 0.9), (0.6, sistro(0.3, 5), 0.45), (0.62, tambor(hz(T - 24), 0.4, 0.4), 0.5)), 1.3, 0.25),
            tema=reverb(mezclar((0, sistro(0.18, 3), 0.5), (0.12, melodia(flauta, [T + 19, T + 20, T + 24], 0.13, 0.3, g=0.45), 1)), 1.1, 0.25),
            aviso=reverb(mezclar(melodia(flauta, [T + 24, T + 20, T + 19, T + 17, T + 19, None, T + 20, T + 24], 0.2, 0.25), (0, arpa(T - 12, 2.0, 0.2), 0.3), (1.4, sistro(0.3, 5), 0.4)), 1.4, 0.28),
            solemne=reverb(mezclar(melodia(flauta, [T + 24, T + 25, T + 24, T + 20, T + 19, T + 17, T + 19, T + 20, T + 24], 0.45, 0.43, g=0.3),
                                   *[(1.8 * i, acorde(lambda f, d: pluck(f, d, 0.4, 0.9965), [T + a for a in ch], 1.8, arpegio=0.05), 0.6) for i, ch in enumerate([[0, 7, 12], [1, 8, 13], [-4, 5, 12], [0, 7, 12, 19]])],
                                   *[(1.8 * i, tambor(hz(T - 24), 0.5), 0.4) for i in range(4)]), 2.0, 0.3))

    # Error: el arpa apagada que baja un semitono y el tambor sordo. Cambio: una sacudida del sistro y una cuerda.
    # Tecla: el cálamo de junco sobre el papiro. Rascar: el oro de Nubia, blando, con su campanilla.
    with azar_propio('egipto'):
        nuevos('egipto',
               error=mezclar((0, arpa(T + 13, 0.2, 0.2), 0.5), (0.12, arpa(T + 12, 0.45, 0.15), 0.5), (0.12, tambor(hz(T - 24), 0.25, 0.3), 0.3)),
               cambio=mezclar((0, sistro(0.06, 2), 0.6), (0.07, arpa(T + 20, 0.3, 0.55), 0.5)),
               tecla=trazo((1400, 4800), 0.045, 9),
               rascar=rascado((1000, 5200), campana(hz(T + 32), 0.6, 2.2, 1.0, 5), aspereza=70))

def medieval():
    # Re dórico: pluma y pergamino, campanitas y canto llano.
    T = -7
    camp = lambda n, d=1.2: campana(hz(n), d, 2.0, 1.6, 3.5)
    S['medieval'] = dict(
        toque=filtro(ruido(0.03), 'banda', (2500, 7000)) * env(int(0.03 * SR), 0.002, 0.02),
        elegir=filtro(ruido(0.05), 'alto', 3500) * env(int(0.05 * SR), 0.003, 0.04),
        pasar=papel(0.22, (600, 4000)),
        abrir=mezclar((0, tambor(hz(T - 24), 0.35, 0.5), 0.8), (0.03, filtro(ruido(0.3), 'banda', (300, 1500)) * env(int(0.3 * SR), 0.05, 0.2), 0.4)),
        guardar=mezclar((0, tambor(hz(T - 24), 0.25, 0.6), 0.8), (0.05, camp(T + 24, 0.9), 0.35)),
        logro=reverb(mezclar(*[(0.16 * i, camp(T + n), 0.45) for i, n in enumerate([12, 19, 24, 15, 24])], (0.1, coro(hz(T - 12), 1.2), 0.15), (0.1, coro(hz(T - 5), 1.2), 0.12)), 1.8, 0.32),
        tema=reverb(mezclar((0, camp(T + 19), 0.5), (0.2, camp(T + 24, 1.4), 0.5)), 1.6, 0.3),
        aviso=reverb(mezclar(*[(0.18 * i, camp(T + n, 1.0), 0.45) for i, n in enumerate([24, 19, 15, 12, 19, 24])]), 1.8, 0.3),
        solemne=reverb(mezclar((0, coro(hz(T - 12), 5.6), 0.22), (0, coro(hz(T - 5), 5.6), 0.15),
                               melodia(coro, [T + n for n in [0, 2, 3, 5, 7, 5, 3, 2, 0]], 0.6, 0.62, g=0.32),
                               (5.2, camp(T + 24, 2.0), 0.35), (5.2, camp(T + 19, 2.0), 0.25)), 2.6, 0.4))

    # Error: el sello que cae torcido y una campanita desafinada. Cambio: dos campanitas que suben.
    # Tecla: la pluma de ave sobre el pergamino. Rascar: pan de oro sobre bol rojo, que casi no suena.
    with azar_propio('medieval'):
        nuevos('medieval',
               error=mezclar((0, tambor(hz(T - 24), 0.25, 0.5), 0.6), (0.06, camp(T + 13, 0.5), 0.3), (0.06, camp(T + 12, 0.5), 0.3)),
               cambio=mezclar((0, camp(T + 24, 0.4), 0.4), (0.1, camp(T + 31, 0.5), 0.4)),
               tecla=trazo((2500, 8000), 0.045, 20),
               rascar=rascado((1200, 5000), camp(T + 24, 0.6), aspereza=60))

def templario():
    # Sol mixolidio, el modo de los cantos de cruzada: la cota de malla que tintinea, la espada contra el escudo, la tela
    # de la sobreveste y del estandarte al viento, el rastrillo del castillo, el olifante y la campana de la capilla.
    T = -2   # Sol4
    malla = lambda n, d=0.14: campana(hz(n), d, 3.7, 0.9, 16)
    golpe = lambda n, d=0.4: mezclar((0, metal(hz(n), d, 4200, 0.004) * env(int(d * SR), 0.002, d * 0.35, 5), 0.8), (0, clic(0.02, (2500, 7000)), 0.5))
    cuerno = lambda f, d: metal(f, d, 2200, 0.05)
    capilla = lambda n, d=1.8: campana(hz(n), d, 2.4, 1.3, 2.4)
    with azar_propio('templario_base'):
        tela = filtro(ruido(0.3), 'banda', (250, 1800)) * env(int(0.3 * SR), 0.06, 0.18, 3) * (0.55 + 0.45 * np.abs(np.sin(np.linspace(0, 9, int(0.3 * SR)))))
        cadena = mezclar(*[(0.035 * i + 0.002 * i * i, clic(0.02, (1500, 6000)), 0.7 - 0.04 * i) for i in range(9)])
        S['templario'] = dict(
            toque=mezclar((0, malla(T + 24), 0.6), (0.012, malla(T + 31, 0.1), 0.35), (0, clic(0.012, (3000, 8000)), 0.3)),
            elegir=golpe(T + 12),
            pasar=tela,
            abrir=mezclar((0, cadena, 0.8), (0.32, tambor(hz(T - 26), 0.4, 0.5), 0.9)),
            guardar=mezclar((0, tambor(hz(T - 24), 0.3, 0.7), 0.9), (0.06, capilla(T + 12, 1.1), 0.3)),
            logro=reverb(mezclar(melodia(cuerno, [T - 12, T - 5, T, T - 5, T], 0.2, 0.32, g=0.5), (0.85, cuerno(hz(T), 0.9), 0.55),
                                 *[(0.21 * i, tambor(hz(T - 29), 0.3, 0.4), 0.5) for i in range(4)]), 1.6, 0.28),
            tema=reverb(mezclar((0, tambor(hz(T - 29), 0.35, 0.5), 0.7), (0.05, cuerno(hz(T - 5), 0.4), 0.45), (0.4, cuerno(hz(T), 0.6), 0.5)), 1.4, 0.25),
            aviso=reverb(mezclar(*[(0.3 * i, capilla(T + n, 1.4), 0.45) for i, n in enumerate([12, 10, 7, 5])], (1.2, cuerno(hz(T - 5), 0.7), 0.35)), 1.8, 0.3),
            solemne=reverb(mezclar((0, coro(hz(T - 12), 6.0), 0.22), (0, coro(hz(T - 5), 6.0), 0.14),
                                   melodia(coro, [T + n for n in [0, 2, 5, 4, 2, 0, -2, 0]], 0.7, 0.72, g=0.32),
                                   (5.4, capilla(T + 12, 2.2), 0.35), (5.6, cuerno(hz(T - 12), 1.2), 0.2)), 2.8, 0.4))

    # Error: el escudo que recibe un golpe sordo y la espada que se escurre, desafinada. Cambio: la hebilla que cierra y una
    # anilla. Tecla: el cincel del cantero que marca la piedra. Rascar: el acero de la malla raspado, que chirría.
    with azar_propio('templario'):
        nuevos('templario',
               error=mezclar((0, tambor(hz(T - 27), 0.3, 0.6), 0.8), (0.07, golpe(T + 1, 0.3), 0.35), (0.12, golpe(T, 0.35), 0.3)),
               cambio=mezclar((0, clic(0.015, (1200, 4500)), 0.7), (0.02, malla(T + 19, 0.12), 0.45)),
               tecla=mezclar((0, trazo((900, 3500), 0.03, 6, 0.012), 0.8), (0.002, malla(T + 36, 0.04), 0.12)),
               rascar=rascado((2200, 8000), malla(T + 31, 0.5), aspereza=140))

def distopia():
    # Do menor: máquina de escribir, sellos; el timbre del carro y el carillón del ministerio.
    T = 3    # Do5
    ding = lambda n=T + 12, d=0.9: campana(hz(n), d, 3.0, 1.0, 5)
    tecla = lambda: mezclar((0, clic(0.02, (1000, 4000)), 1), (0.006, clic(0.012, (500, 2000)), 0.5))
    carro = filtro(ruido(0.35), 'banda', (800, 3000)) * np.linspace(0.3, 1, int(0.35 * SR)) * env(int(0.35 * SR), 0.2, 0.2, 2)
    S['distopia'] = dict(
        toque=tecla(), elegir=clic(0.018, (1500, 6000)),
        pasar=mezclar((0, clic(0.02, (1000, 4000)), 1), (0.06, clic(0.025, (700, 3000)), 0.9)),
        abrir=mezclar((0, filtro(ruido(0.35), 'bajo', 900) * np.linspace(0.3, 1, int(0.35 * SR)), 0.6), (0.34, clic(0.04, (400, 2500)), 1.2)),
        guardar=mezclar((0, tambor(hz(T - 36), 0.2, 0.8), 0.9), (0.08, ding(), 0.35)),
        logro=mezclar(*[(0.07 * i, tecla(), 0.7) for i in range(6)], (0.45, carro, 0.5), (0.8, ding(), 0.5)),
        tema=mezclar((0, tambor(hz(T - 36), 0.2, 0.8), 0.9), (0.12, ding(T + 12), 0.4), (0.3, ding(T + 7), 0.3)),
        aviso=mezclar(*[(0.06 * i, tecla(), 0.6) for i in range(9)], (0.6, ding(), 0.5), (0.85, ding(T + 7), 0.4)),
        solemne=reverb(mezclar(*[(1.0 * i, acorde(organo, [T - 12 + a for a in ch], 1.0), 0.5) for i, ch in enumerate([[0, 3, 7], [-4, 0, 3], [-5, -1, 2], [0, 3, 7]])],
                               (3.9, ding(T + 12, 1.4), 0.4)), 2.0, 0.3))

    # Error: el zumbador del ministerio, dos veces. Cambio: el interruptor de baquelita.
    # Tecla: la máquina de escribir. Rascar: la chapa gris, sin brillo.
    with azar_propio('distopia'):
        zumbador = lambda d: filtro(np.tanh(4 * np.sin(2 * np.pi * hz(T - 24) * t_(d))) + 0.4 * np.sin(2 * np.pi * hz(T - 18) * t_(d)), 'bajo', 1800) * env(int(d * SR), 0.005, d * 0.9, 1.5)
        nuevos('distopia',
               error=mezclar((0, zumbador(0.14), 0.5), (0.18, zumbador(0.22), 0.5)),
               cambio=mezclar((0, clic(0.012, (600, 2500)), 1), (0.03, clic(0.02, (300, 1500)), 0.8), (0.03, tambor(hz(T - 36), 0.05, 0.3), 0.3)),
               tecla=mezclar((0, clic(0.018, (900, 3800)), 1), (0.004, clic(0.01, (400, 1500)), 0.6), (0.01, filtro(ruido(0.02), 'bajo', 600) * env(int(0.02 * SR), 0.001, 0.01), 0.4)),
               rascar=rascado((900, 4200), ding(T + 12, 0.4), aspereza=50))

def refugio():
    # Fa mayor pentatónico: relés, contador Geiger y el vibráfono optimista de los cincuenta.
    T = 8    # Fa5
    vib = lambda n, d=0.9: vibrafono(hz(n), d)
    zumbido = lambda d: seno(hz(T - 36), d) * 0.6 + 0.3 * seno(hz(T - 24), d)
    S['refugio'] = dict(
        toque=clic(0.015, (800, 3000)), elegir=clic(0.006, (3000, 8000)),
        pasar=mezclar((0, clic(0.015, (800, 3000)), 1), (0, zumbido(0.12) * env(int(0.12 * SR), 0.005, 0.1), 0.35)),
        abrir=mezclar((0, filtro(ruido(0.35), 'alto', 4000) * env(int(0.35 * SR), 0.02, 0.3), 0.25), (0.02, zumbido(0.35) * env(int(0.35 * SR), 0.02, 0.3), 0.4), (0.2, vib(T + 12, 0.5), 0.3)),
        guardar=mezclar((0, clic(0.012), 0.6), (0.03, vib(T, 0.6), 0.45), (0.13, vib(T + 7, 0.7), 0.45)),
        logro=reverb(melodia(vibrafono, [T, T + 4, T + 7, T + 9, T + 7, T + 12], 0.12, 1.0, g=0.45), 1.2, 0.25),
        tema=mezclar((0, clic(0.015, (800, 3000)), 0.8), (0.05, vib(T + 7, 0.6), 0.45), (0.15, vib(T + 12, 0.9), 0.5)),
        aviso=reverb(mezclar(melodia(vibrafono, [T + 12, T + 9, T + 7, T + 4, T + 7, T + 12], 0.16, 1.0, g=0.45), (0, clic(0.012), 0.6), (0.04, clic(0.012), 0.6)), 1.3, 0.25),
        solemne=reverb(mezclar(*[(1.1 * i, acorde(vibrafono, [T - 12 + a for a in ch], 1.6, arpegio=0.03), 0.6) for i, ch in enumerate([[0, 4, 7, 12], [5, 9, 12, 17], [7, 11, 14, 19], [0, 4, 7, 12, 16]])],
                               *[(1.1 * i, acorde(lambda f, d: organo(f, d, 0.2), [T - 24 + a for a in ch], 1.1), 0.25) for i, ch in enumerate([[0, 7], [5, 12], [7, 14], [0, 7]])]), 1.8, 0.3))

    # Error: el contador Geiger que se dispara y el vibráfono que baja un tritono. Cambio: el relé y una nota que sube.
    # Tecla: la terminal del refugio, con su relé. Rascar: chapa galvanizada.
    with azar_propio('refugio'):
        geiger = mezclar(*[(t, clic(0.004, (2000, 7000)), 0.8) for t in (0, 0.02, 0.05, 0.06, 0.1, 0.13)])
        nuevos('refugio',
               error=mezclar((0, geiger, 0.6), (0.05, vib(T + 6, 0.5), 0.4), (0.05, vib(T, 0.6), 0.35)),
               cambio=mezclar((0, clic(0.012, (800, 3000)), 0.9), (0.04, vib(T + 12, 0.4), 0.4)),
               tecla=mezclar((0, clic(0.014, (700, 2800)), 1), (0.005, zumbido(0.02) * env(int(0.02 * SR), 0.001, 0.012), 0.25)),
               rascar=rascado((1800, 7000), vib(T + 24, 0.5), aspereza=120))

def orden():
    # Sol menor: servos, armadura y trompas.
    T = -2   # Sol4
    trompa = lambda n, d: metal(hz(n), d, 1800, 0.06)
    S['orden'] = dict(
        toque=mezclar((0, clic(0.012, (600, 2500)), 0.8), (0, tambor(hz(T - 24), 0.06, 0.3), 0.4)),
        elegir=campana(hz(T + 24), 0.25, 2.0, 1.5, 18),
        pasar=filtro(barrido(hz(T - 12), hz(T - 5), 0.14, 'sierra'), 'bajo', 1500) * env(int(0.14 * SR), 0.01, 0.12, 2),
        abrir=mezclar((0, filtro(ruido(0.3), 'alto', 2500) * env(int(0.3 * SR), 0.01, 0.25), 0.4), (0.25, tambor(hz(T - 24), 0.3, 0.6), 0.9), (0.25, campana(hz(T + 12), 0.6, 2.0, 1.5, 8), 0.3)),
        guardar=mezclar((0, filtro(barrido(hz(T - 12), hz(T - 5), 0.12, 'sierra'), 'bajo', 1500) * env(int(0.12 * SR), 0.01, 0.1), 0.4), (0.12, tambor(hz(T - 24), 0.35, 0.6), 0.9), (0.12, trompa(T - 12, 0.4), 0.35)),
        logro=reverb(mezclar((0, trompa(T - 12, 0.3), 0.5), (0.3, trompa(T - 5, 0.3), 0.5), (0.6, trompa(T, 0.8), 0.55), (0.6, trompa(T - 9, 0.8), 0.3), (0.6, tambor(hz(T - 24), 0.5), 0.7)), 1.6, 0.3),
        tema=reverb(mezclar((0, trompa(T - 5, 0.22), 0.5), (0.24, trompa(T, 0.6), 0.55), (0.24, tambor(hz(T - 24), 0.4), 0.6)), 1.4, 0.28),
        aviso=reverb(mezclar(*[(0.24 * i, trompa(T + n, 0.26 if i < 3 else 0.8), 0.5) for i, n in enumerate([-5, 0, 3, 0])], (0, tambor(hz(T - 24), 0.4), 0.5), (0.72, tambor(hz(T - 24), 0.5), 0.6)), 1.6, 0.3),
        solemne=reverb(mezclar(*[(1.0 * i, acorde(trompa, [T - 12 + a for a in ch], 1.0), 0.55) for i, ch in enumerate([[0, 3, 7], [-4, 0, 3], [-2, 2, 5], [0, 3, 7, 12]])],
                               *[(1.0 * i, tambor(hz(T - 24), 0.6), 0.6) for i in range(4)]), 2.2, 0.32))

    # Error: el servo que se traba y la trompa que cae. Cambio: el servo que sube y un clic de armadura.
    # Tecla: el teclado blindado de la consola. Rascar: acero pavonado.
    with azar_propio('orden'):
        servo = lambda a, b, d: filtro(barrido(hz(a), hz(b), d, 'sierra'), 'bajo', 1500) * env(int(d * SR), 0.01, d * 0.85, 2)
        nuevos('orden',
               error=mezclar((0, servo(T - 5, T - 12, 0.16), 0.45), (0.14, trompa(T - 13, 0.35), 0.35), (0.14, tambor(hz(T - 24), 0.3, 0.4), 0.5)),
               cambio=mezclar((0, servo(T - 12, T - 5, 0.1), 0.4), (0.1, clic(0.01, (600, 2500)), 0.8)),
               tecla=mezclar((0, clic(0.012, (500, 2200)), 1), (0, tambor(hz(T - 24), 0.03, 0.2), 0.25)),
               rascar=rascado((2200, 8000), campana(hz(T + 24), 0.5, 2.76, 2.0, 7), aspereza=140))

def laboratorio():
    # Re menor: el instituto bajo tierra. Senos fríos, colchones de sintetizador, aire filtrado y un pulso grave lejano.
    T = -7   # Re4
    sub = lambda d=0.4: seno(hz(T - 24), d) * env(int(d * SR), 0.004, d * 0.7, 3)
    aire = lambda d: filtro(ruido(d), 'banda', (900, 5000)) * np.sin(np.pi * t_(d) / d) ** 2
    sala = lambda x, c=1.8, m=0.38: reverb(x, c, m, 2600)
    S['laboratorio'] = dict(
        toque=mezclar((0, frio(hz(T + 24), 0.18), 0.6), (0, clic(0.004, (5000, 9000)), 0.12)),
        elegir=mezclar((0, frio(hz(T + 24), 0.09), 0.5), (0.05, frio(hz(T + 27), 0.14), 0.5)),
        pasar=sala(mezclar((0, aire(0.22), 0.18), (0.02, sinte(hz(T), 0.32, 900, 0.03, 0.2), 0.5), (0.02, sinte(hz(T + 7), 0.32, 900, 0.03, 0.2), 0.35), (0, sub(0.25), 0.4)), 1.0, 0.3),
        abrir=sala(mezclar((0, barrido(hz(T - 12), hz(T + 12), 0.35) * np.sin(np.pi * t_(0.35) / 0.35), 0.3), (0, aire(0.35), 0.2),
                             (0.3, campana(hz(T + 12), 1.4, 3.5, 0.8, 2.2), 0.35), (0.3, campana(hz(T + 19), 1.4, 3.5, 0.8, 2.2), 0.25), (0.3, sub(0.5), 0.5))),
        guardar=sala(mezclar(*[(0.09 * i, frio(hz(T + n), 0.3), 0.5) for i, n in enumerate([19, 15, 12])], (0.18, sub(0.5), 0.55)), 1.2, 0.3),
        logro=sala(mezclar((0, acorde(lambda f, d: sinte(f, d, 1100, 0.3, 0.6), [T - 12, T - 5, T, T + 3], 1.8), 0.5),
                           *[(0.12 * i, frio(hz(T + n), 0.7), 0.4) for i, n in enumerate([12, 15, 19, 24, 26, 27])], (0, sub(0.8), 0.5)), 2.2, 0.4),
        tema=sala(mezclar((0, acorde(lambda f, d: sinte(f, d, 1000, 0.4, 0.7), [T - 12, T - 5, T + 2, T + 3], 1.9), 0.55), (0, sub(1.0), 0.55),
                          (0.45, campana(hz(T + 26), 1.6, 3.5, 0.7, 2), 0.22), (0.75, campana(hz(T + 24), 1.6, 3.5, 0.7, 2), 0.2)), 2.4, 0.42),
        aviso=sala(mezclar(*[(0.32 * i, frio(hz(T + n), 0.6), 0.5) for i, n in enumerate([12, 15, 14, 19])], (0, acorde(lambda f, d: sinte(f, d, 900, 0.3, 0.6), [T - 12, T - 5, T + 3], 1.7), 0.3), (0, sub(1.0), 0.4)), 2.0, 0.4),
        solemne=sala(mezclar(*[(1.4 * i, acorde(lambda f, d: sinte(f, d, 1000, 0.5, 0.8), [T - 12 + a for a in ch], 1.6), 0.5) for i, ch in enumerate([[0, 7, 14, 15], [-2, 5, 9, 14], [-5, 2, 5, 10], [-3, 4, 7, 9], [0, 7, 12, 15]])],
                             *[(1.4 * i + 0.6, frio(hz(T + n), 1.0), 0.25) for i, n in enumerate([14, 17, 15, 16, 12])], *[(1.4 * i, sub(0.9), 0.4) for i in range(5)]), 3.0, 0.45))

    # Error: dos tonos fríos que bajan a la cuarta aumentada, en la sala. Cambio: el vidrio que sube.
    # Tecla: el teclado de laboratorio, suave. Rascar: aluminio de instrumento, agudo.
    with azar_propio('laboratorio'):
        nuevos('laboratorio',
               error=sala(mezclar((0, frio(hz(T + 24), 0.2), 0.5), (0.12, frio(hz(T + 18), 0.35), 0.5), (0.12, sub(0.3), 0.4)), 1.0, 0.3),
               cambio=mezclar((0, frio(hz(T + 19), 0.1), 0.5), (0.05, frio(hz(T + 26), 0.18), 0.5)),
               tecla=mezclar((0, clic(0.006, (2500, 7000)), 0.5), (0, filtro(ruido(0.012), 'banda', (900, 2400)) * env(int(0.012 * SR), 0.001, 0.006), 0.35)),
               rascar=rascado((3000, 10000), frio(hz(T + 36), 0.4), aspereza=200))

def colonos():
    # Sol mayor pentatónico: madera afinada (marimba), crujidos y violín de pueblo.
    T = -2
    madera = lambda n, d=0.1: mezclar((0, filtro(ruido(d), 'banda', (hz(n) * 0.8, hz(n) * 1.3)) * env(int(d * SR), 0.001, d * 0.5, 6), 0.6), (0, marimba(hz(n), d * 3), 0.6))
    S['colonos'] = dict(
        toque=madera(T + 12, 0.08), elegir=madera(T + 19, 0.05), pasar=madera(T, 0.1),
        abrir=mezclar((0, filtro(barrido(140, 90, 0.45, 'sierra'), 'banda', (200, 1200)) * env(int(0.45 * SR), 0.05, 0.4, 2), 0.3), (0.42, madera(T - 12, 0.15), 1)),
        guardar=mezclar((0, madera(T + 7, 0.1), 0.8), (0.11, madera(T + 12, 0.12), 0.8)),
        logro=reverb(melodia(marimba, [T, T + 4, T + 7, T + 9, T + 12], 0.11, 0.7), 1.0, 0.2),
        tema=reverb(mezclar((0, marimba(hz(T + 7), 0.6), 0.5), (0.12, marimba(hz(T + 12), 0.8), 0.55)), 1.0, 0.2),
        aviso=reverb(melodia(marimba, [T + 12, T + 9, T + 7, T + 4, T + 7, None, T + 12], 0.16, 0.7), 1.2, 0.22),
        solemne=reverb(mezclar(melodia(lambda f, d: arco(f, d, 5), [T + n for n in [0, 2, 4, 7, 4, 2, 0, -3, 0]], 0.55, 0.6, g=0.35),
                               *[(1.1 * i, acorde(marimba, [T - 12 + a for a in ch], 1.0, arpegio=0.06), 0.5) for i, ch in enumerate([[0, 7, 12], [5, 12, 17], [7, 14, 19], [0, 7, 12, 16]])]), 1.6, 0.25))

    # Error: la madera que suena sorda, dos veces y bajando. Cambio: el pestillo de la puerta y una tabla que sube.
    # Tecla: el lápiz sobre el cuaderno de la granja. Rascar: cobre.
    with azar_propio('colonos'):
        nuevos('colonos',
               error=mezclar((0, madera(T, 0.12), 0.8), (0.14, madera(T - 1, 0.16), 0.8)),
               cambio=mezclar((0, clic(0.01, (500, 2000)), 0.6), (0.03, madera(T + 7, 0.07), 0.7), (0.1, madera(T + 12, 0.08), 0.7)),
               tecla=trazo((1500, 5000), 0.06, 30, 0.035),
               rascar=rascado((1200, 5500), marimba(hz(T + 24), 0.5), aspereza=70))

def napoleon():
    # Si bemol mayor: tambor de marcha y corneta (solo notas de la serie armónica).
    T = 1    # Si♭4
    corneta = lambda n, d: metal(hz(n), d, 3500)
    def redoble(d=0.22):
        x = np.zeros(int(d * SR))
        for i in range(int(d / 0.022)):
            g = filtro(ruido(0.03), 'banda', (1500, 6000)) * env(int(0.03 * SR), 0.001, 0.02)
            k = int(i * 0.022 * SR); x[k:k + len(g)] += g[:len(x) - k] * (0.5 + 0.5 * i / (d / 0.022))
        return x
    golpe = lambda: mezclar((0, filtro(ruido(0.05), 'banda', (1500, 6000)) * env(int(0.05 * SR), 0.001, 0.03), 0.8), (0, tambor(hz(T - 24), 0.06), 0.4))
    S['napoleon'] = dict(
        toque=golpe(), elegir=mezclar((0, golpe(), 0.6), (0.03, golpe(), 0.9)), pasar=redoble(),
        abrir=mezclar((0, corneta(T - 5, 0.16), 0.6), (0.18, corneta(T, 0.32), 0.6)),
        guardar=mezclar((0, golpe(), 0.6), (0.06, corneta(T, 0.12), 0.5), (0.18, corneta(T + 4, 0.12), 0.5), (0.3, corneta(T + 7, 0.3), 0.55)),
        logro=reverb(mezclar((0, redoble(0.4), 0.6), *[(0.42 + 0.14 * i, corneta(T + n, 0.14 if i < 3 else 0.6), 0.55) for i, n in enumerate([-5, 0, 4, 7])]), 1.4, 0.25),
        tema=reverb(mezclar((0, corneta(T - 5, 0.15), 0.55), (0.17, corneta(T, 0.15), 0.55), (0.34, corneta(T + 4, 0.5), 0.6), (0.34, golpe(), 0.6)), 1.2, 0.25),
        aviso=reverb(mezclar(*[(0.18 * i, corneta(T + n, 0.17 if i < 5 else 0.7), 0.5) for i, n in enumerate([-5, 0, 4, 7, 4, 0])], (1.1, redoble(0.3), 0.5)), 1.4, 0.25),
        solemne=reverb(mezclar(*[(1.0 * i, acorde(lambda f, d: metal(f, d, 2800), [T - 12 + a for a in ch], 1.0), 0.55) for i, ch in enumerate([[0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12]])],
                               (0, redoble(0.8), 0.4), (2.9, redoble(1.0), 0.45), (3.0, filtro(ruido(1.2), 'alto', 5000) * env(int(1.2 * SR), 0.005, 0.8, 3), 0.15)), 2.0, 0.3))

    # Error: el tambor apagado y la corneta que se cae medio tono. Cambio: dos golpes y la corneta a la quinta.
    # Tecla: la plumilla de acero del despacho. Rascar: bronce dorado del Imperio.
    with azar_propio('napoleon'):
        nuevos('napoleon',
               error=mezclar((0, tambor(hz(T - 24), 0.25, 0.4), 0.6), (0.06, corneta(T + 7, 0.12), 0.4), (0.18, corneta(T + 6, 0.3), 0.4)),
               cambio=mezclar((0, golpe(), 0.6), (0.06, corneta(T, 0.1), 0.4), (0.16, corneta(T + 7, 0.16), 0.45)),
               tecla=trazo((3000, 9000), 0.035, 12, 0.02),
               rascar=rascado((1500, 6500), corneta(T + 24, 0.25), aspereza=100))

def botanico():
    # Sol dórico: el cuaderno del naturalista. Guitarra de cuerdas de tripa, madera, la pluma que raspa, grillos afinados y un violonchelo.
    T = -2   # Sol4
    madera = lambda n, d=0.1: mezclar((0, filtro(ruido(d), 'banda', (hz(n) * 0.8, hz(n) * 1.4)) * env(int(d * SR), 0.001, d * 0.4, 6), 0.4), (0, marimba(hz(n), d * 3), 0.7))
    pluma = lambda d=0.06: filtro(ruido(d), 'banda', (2500, 7500)) * env(int(d * SR), 0.004, d * 0.6, 4) * (0.7 + 0.3 * np.sin(np.linspace(0, 18, int(d * SR))))
    cello = lambda n, d: arco(hz(n), d, 5.0)
    gr = lambda n=T + 36, d=0.12, p=3: grillo(hz(n), d, p)
    S['botanico'] = dict(
        toque=madera(T + 12, 0.07),
        elegir=gr(T + 36, 0.11, 3),
        pasar=papel(0.2, (600, 4200)),
        abrir=reverb(mezclar(*[(0.05 * i, guitarra(hz(T + n), 1.3), 0.45) for i, n in enumerate([-12, -5, 0, 3, 7])]), 1.0, 0.2),
        guardar=mezclar((0, clic(0.006, (3000, 8000)), 0.4), (0.02, guitarra(hz(T + 7), 0.9), 0.5), (0.16, guitarra(hz(T + 12), 1.1), 0.55), (0.02, madera(T - 12, 0.12), 0.4)),
        logro=reverb(mezclar(*[(0.15 * i, guitarra(hz(T + n), 0.9), 0.5) for i, n in enumerate([0, 3, 5, 7, 9, 7, 12])], (0.1, cello(T - 12, 1.2), 0.2),
                             (0.5, gr(T + 36), 0.18), (0.62, gr(T + 36), 0.16), (1.0, gr(T + 39), 0.16)), 1.2, 0.22),
        tema=reverb(mezclar(*[(0.035 * i, guitarra(hz(T + n), 1.6), 0.4) for i, n in enumerate([-12, -5, 0, 2, 7])], (0.1, cello(T - 5, 1.0), 0.22),
                            (0.45, gr(T + 36), 0.18), (0.58, gr(T + 36), 0.16)), 1.2, 0.24),
        aviso=reverb(mezclar(*[(0.16 * i, gr(T + 36 + [0, 0, 3][i % 3], 0.1), 0.35) for i in range(3)], *[(0.55 + 0.22 * i, guitarra(hz(T + n), 1.0), 0.5) for i, n in enumerate([12, 10, 7, 9])]), 1.3, 0.25),
        solemne=reverb(mezclar(melodia(lambda f, d: arco(f, d, 5.0), [T - 12 + n for n in [7, 9, 10, 12, 9, 7, 5, 7]], 0.75, 0.85, g=0.3),
                               *[(1.5 * i, mezclar(*[(0.06 * k, guitarra(hz(T - 12 + a), 1.8), 0.4) for k, a in enumerate(ch)]), 1) for i, ch in enumerate([[0, 7, 12, 15], [5, 12, 16, 19], [3, 10, 15, 19], [0, 7, 12, 15, 19]])],
                               *[(0.8 + 1.1 * i, gr(T + 36 + [0, 3, 0][i % 3]), 0.12) for i in range(5)], (0, pluma(0.5), 0.1)), 2.0, 0.28))

    # Error: la cuerda de tripa que zumba medio tono abajo. Cambio: un grillo y la guitarra a la quinta.
    # Tecla: el lápiz de grafito del cuaderno de campo. Rascar: bronce con verdín.
    with azar_propio('botanico'):
        nuevos('botanico',
               error=mezclar((0, guitarra(hz(T + 7), 0.4, 0.2), 0.5), (0.13, guitarra(hz(T + 6), 0.7, 0.15), 0.5), (0.13, madera(T - 12, 0.12), 0.35)),
               cambio=mezclar((0, gr(T + 36, 0.08, 2), 0.4), (0.06, guitarra(hz(T + 7), 0.4), 0.45), (0.14, guitarra(hz(T + 14), 0.5), 0.45)),
               tecla=trazo((1100, 4200), 0.05, 26, 0.03),
               rascar=rascado((1300, 5500), cello(T + 12, 0.4), aspereza=60))

def ruinas():
    # Re mixolidio: la Ciudad verde. Una kalimba hecha con flejes, la flauta en una plaza vacía, tubos de metal que el viento
    # hace sonar, gotas que caen del techo roto, el golpe hueco del hormigón y el viento entre los edificios.
    T = -7   # Re4
    kal = lambda n, d=0.7: kalimba(hz(n), d)
    fl = lambda n, d: flauta(hz(n), d)
    tubo = lambda n, d=1.6: campana(hz(n), d, 2.76, 1.2, 2.2)
    gota = lambda n, d=0.12: barrido(hz(n + 12) * 1.25, hz(n), d) * env(int(d * SR), 0.001, d * 0.45, 5)
    viento = lambda d: filtro(ruido(d), 'banda', (300, 1400)) * env(int(d * SR), d * 0.4, d * 0.6, 2) * 0.4
    hormigon = lambda: mezclar((0, filtro(ruido(0.05), 'bajo', 700) * env(int(0.05 * SR), 0.001, 0.03, 6), 1.0), (0, tambor(hz(T - 24), 0.09, 0.3), 0.4))
    # Con su propio azar también los sonidos de siempre: un tema nuevo no mueve el ruido de los que van después.
    with azar_propio('ruinas_base'):
        S['ruinas'] = dict(
            toque=mezclar((0, gota(T + 19, 0.08), 0.7), (0, clic(0.004, (2000, 6000)), 0.2)),
            elegir=kal(T + 19, 0.35),
            pasar=mezclar((0, viento(0.22), 0.9), (0.03, papel(0.12, (900, 4000)), 0.3)),
            abrir=mezclar(*[(0.06 * i, kal(T + n, 0.8), 0.5) for i, n in enumerate([0, 4, 7, 10])]),
            guardar=mezclar((0, hormigon(), 0.6), (0.05, kal(T + 7, 0.6), 0.5), (0.17, kal(T + 12, 0.8), 0.5)),
            logro=reverb(mezclar(melodia(lambda f, d: flauta(f, d), [T + 12 + n for n in [0, 2, 4, 7, 9, 12]], 0.12, 0.3, g=0.45),
                                 *[(0.75 + 0.09 * i, tubo(T + 24 + n, 1.6), 0.25) for i, n in enumerate([0, 4, 7, 10])], (0.1, pajaro(0.25, 2400, 3600), 0.15)), 1.4, 0.3),
            tema=reverb(mezclar((0, viento(0.8), 0.6), *[(0.25 + 0.07 * i, tubo(T + 12 + n, 1.8), 0.3) for i, n in enumerate([0, 7, 10, 14])], (0.6, fl(T + 19, 0.6), 0.3)), 1.4, 0.3),
            aviso=reverb(mezclar(*[(0.18 * i, tubo(T + 24 + n, 1.2), 0.4) for i, n in enumerate([7, 4, 0, 2])], (0, viento(1.0), 0.4)), 1.3, 0.3),
            solemne=reverb(mezclar(melodia(lambda f, d: flauta(f, d), [T + n for n in [0, 2, 4, 7, 4, 2, 0, -2, 0]], 0.6, 0.7, g=0.35),
                                   *[(1.4 * i, acorde(lambda f, d: arco(f, d, 4.5), [T - 12 + a for a in ch], 1.5), 0.35) for i, ch in enumerate([[0, 7, 12], [-2, 5, 10], [5, 9, 12], [0, 7, 12, 16]])],
                                   (0, viento(5.0), 0.35), *[(0.7 + 1.2 * i, gota(T + 24 + [0, 4, 7, 2][i % 4]), 0.15) for i in range(4)]), 2.0, 0.3))

    # Error: un trozo de hormigón que se desprende y la kalimba que baja medio tono. Cambio: dos gotas a la quinta.
    # Tecla: el carboncillo sobre el muro. Rascar: hormigón áspero, y un tubo que queda sonando.
    with azar_propio('ruinas'):
        nuevos('ruinas',
               error=mezclar((0, hormigon(), 0.7), (0.05, kal(T + 7, 0.3), 0.45), (0.18, kal(T + 6, 0.5), 0.45), (0.12, filtro(ruido(0.25), 'bajo', 900) * env(int(0.25 * SR), 0.01, 0.15, 3), 0.3)),
               cambio=mezclar((0, gota(T + 19, 0.08), 0.5), (0.07, gota(T + 26, 0.08), 0.5)),
               tecla=mezclar((0, trazo((700, 2600), 0.05, 12, 0.03), 1), (0, filtro(ruido(0.03), 'alto', 5000) * env(int(0.03 * SR), 0.001, 0.02, 5), 0.3)),
               rascar=rascado((500, 3200), tubo(T + 12, 0.5), aspereza=35))

def navidad():
    # La mayor: cascabeles, caja de música y campanas.
    T = 0    # La4
    def cascabel(d=0.18):
        x = np.zeros(int(d * SR))
        for i in range(6):
            c = campana(4200 + rng.random() * 1200, 0.08, 2.3, 1.5, 40)
            k = int(rng.random() * (d - 0.08) * SR); x[k:k + len(c)] += c * 0.5
        return x
    S['navidad'] = dict(
        toque=cascabel(0.12), elegir=cajita(hz(T + 31), 0.4), pasar=cascabel(),
        abrir=melodia(cajita, [T + 12, T + 16, T + 19, T + 24], 0.11, 0.7),
        guardar=mezclar((0, cajita(hz(T + 19), 0.7), 0.5), (0.12, cajita(hz(T + 24), 0.9), 0.55), (0, cascabel(0.15), 0.25)),
        logro=reverb(mezclar(melodia(cajita, [T + 12, T + 16, T + 19, T + 21, T + 19, T + 16, T + 24], 0.13, 0.9), (0.8, cascabel(0.3), 0.3)), 1.4, 0.25),
        tema=reverb(mezclar((0, cascabel(0.25), 0.35), (0.1, cajita(hz(T + 19), 0.8), 0.5), (0.25, cajita(hz(T + 24), 1.0), 0.55)), 1.2, 0.25),
        aviso=reverb(mezclar(melodia(cajita, [T + 24, T + 21, T + 19, T + 16, T + 19, T + 24], 0.15, 0.9), (0, cascabel(0.3), 0.3), (0.75, cascabel(0.3), 0.3)), 1.4, 0.25),
        solemne=reverb(mezclar(*[(1.0 * i, acorde(lambda f, d: campana(f, d, 2.0, 1.5, 1.5), [T - 12 + a for a in ch], 1.5, arpegio=0.04), 0.5) for i, ch in enumerate([[0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12]])],
                               melodia(cajita, [T + n for n in [12, 16, 19, 21, 19, 16, 14, 16, 12]], 0.45, 0.9, g=0.25), (3.0, cascabel(0.5), 0.2)), 2.0, 0.3))

    # Error: la caja de música que se desafina al bajar. Cambio: cascabeles y una púa que sube.
    # Tecla: la tecla de madera suave. Rascar: plata escarchada, con cascabeles.
    with azar_propio('navidad'):
        nuevos('navidad',
               error=mezclar((0, cajita(hz(T + 19), 0.3), 0.45), (0.13, cajita(hz(T + 18) * 0.995, 0.6), 0.45)),
               cambio=mezclar((0, cascabel(0.1), 0.35), (0.05, cajita(hz(T + 24), 0.4), 0.45), (0.12, cajita(hz(T + 31), 0.5), 0.4)),
               tecla=mezclar((0, filtro(ruido(0.02), 'banda', (600, 2500)) * env(int(0.02 * SR), 0.001, 0.01, 6), 1), (0, filtro(ruido(0.012), 'bajo', 400) * env(int(0.012 * SR), 0.001, 0.006), 0.5)),
               rascar=rascado((3500, 9500), cascabel(0.3), aspereza=180))

def seda():
    # La menor pentatónico: seda, címbalos de dedo, gong y cítara.
    T = 0
    citara = lambda n, d=0.9: pluck(hz(n), d, 0.65, 0.995)
    cimbalo = lambda n=T + 31, d=0.6: campana(hz(n), d, 2.76, 1.2, 6)
    gong = lambda d=1.4: reverb(campana(hz(T - 24), d, 1.48, 4, 2) * np.minimum(1, t_(d) / 0.12), 1.0, 0.3)
    pent = [0, 3, 5, 7, 10, 12]
    S['seda'] = dict(
        toque=filtro(ruido(0.06), 'banda', (2500, 8000)) * env(int(0.06 * SR), 0.01, 0.04, 3),
        elegir=cimbalo(), pasar=filtro(ruido(0.2), 'banda', (2500, 8000)) * env(int(0.2 * SR), 0.06, 0.12, 3) * 0.7,
        abrir=gong(1.2),
        guardar=mezclar((0, citara(T), 0.55), (0.12, citara(T + 7), 0.5)),
        logro=reverb(mezclar(melodia(lambda f, d: pluck(f, d, 0.65, 0.995), [T + p for p in pent], 0.08, 0.9), (0.5, cimbalo(), 0.3)), 1.2, 0.25),
        tema=reverb(mezclar(melodia(lambda f, d: pluck(f, d, 0.65, 0.995), [T + 12 + p for p in reversed(pent)], 0.05, 0.9, g=0.4), (0.32, cimbalo(), 0.35)), 1.1, 0.25),
        aviso=reverb(mezclar(melodia(lambda f, d: pluck(f, d, 0.65, 0.995), [T + 12, T + 10, T + 7, T + 5, T + 7, None, T + 3, T], 0.18, 0.9), (0, cimbalo(), 0.3), (1.2, cimbalo(T + 24), 0.3)), 1.4, 0.28),
        solemne=reverb(mezclar((0, gong(3.0), 0.5), melodia(lambda f, d: pluck(f, d, 0.65, 0.995), [None if n is None else T + n for n in [0, 3, 5, 7, 10, 7, 5, 3, 0, None, 7, 12]], 0.42, 1.2, g=0.4),
                               (0, filtro(seno(hz(T - 24), 5.5) + 0.5 * seno(hz(T - 17), 5.5), 'bajo', 600) * np.minimum(1, t_(5.5) / 0.8) * np.minimum(1, (5.5 - t_(5.5)) / 1.0), 0.15)), 2.0, 0.3))

    # Error: el gong pequeño apagado y la cítara que se dobla hacia abajo. Cambio: el címbalo de dedo y su quinta.
    # Tecla: el pincel sobre el papel de arroz. Rascar: bronce de moneda.
    with azar_propio('seda'):
        doblada = lambda a, b, d: barrido(hz(a), hz(b), d) * env(int(d * SR), 0.003, d * 0.6, 3)
        nuevos('seda',
               error=mezclar((0, campana(hz(T - 12), 0.5, 1.48, 3, 6), 0.4), (0.04, doblada(T + 12, T + 10, 0.35), 0.4)),
               cambio=mezclar((0, cimbalo(T + 24, 0.4), 0.4), (0.08, cimbalo(T + 31, 0.5), 0.4)),
               tecla=filtro(ruido(0.07), 'banda', (1500, 6000)) * env(int(0.07 * SR), 0.015, 0.04, 3),
               rascar=rascado((1400, 6000), cimbalo(T + 36, 0.5), aspereza=80))

def urss():
    # Do menor: prensa, yunque, silbato de fábrica (acorde menor) y metales.
    T = 3
    bronce = lambda n, d: metal(hz(n), d, 2600)
    silbato = lambda d: filtro(sum(np.sin(2 * np.pi * hz(T + a) * t_(d)) for a in (0, 3, 7)), 'bajo', 3000) * np.minimum(1, t_(d) / 0.05) * np.minimum(1, (d - t_(d)) / 0.08)
    S['urss'] = dict(
        toque=mezclar((0, clic(0.012, (800, 3500)), 0.8), (0, tambor(hz(T - 36), 0.05, 0.4), 0.4)),
        elegir=campana(hz(T + 19), 0.15, 2.9, 2.5, 30),
        pasar=tambor(hz(T - 36), 0.2, 0.6),
        abrir=mezclar((0, silbato(0.45), 0.4)),
        guardar=mezclar((0, tambor(hz(T - 36), 0.2, 0.6), 0.9), (0.09, tambor(hz(T - 36), 0.2, 0.6), 0.9), (0.09, campana(hz(T + 19), 0.2, 2.9, 2.5, 25), 0.3)),
        logro=reverb(mezclar(*[(0.15 * i, bronce(T - 12 + n, 0.15 if i < 3 else 0.7), 0.5) for i, n in enumerate([0, 3, 7, 12])], (0.45, tambor(hz(T - 36), 0.5), 0.7)), 1.4, 0.25),
        tema=reverb(mezclar((0, bronce(T - 12, 0.18), 0.5), (0.2, bronce(T - 5, 0.18), 0.5), (0.4, bronce(T, 0.6), 0.55), (0.4, tambor(hz(T - 36), 0.4), 0.6)), 1.3, 0.25),
        aviso=reverb(mezclar(*[(0.2 * i, bronce(T + n, 0.2 if i < 3 else 0.8), 0.5) for i, n in enumerate([-5, 0, 3, 7])], (0.6, tambor(hz(T - 36), 0.5), 0.6)), 1.5, 0.28),
        solemne=reverb(mezclar(*[(1.0 * i, acorde(lambda f, d: metal(f, d, 2200), [T - 12 + a for a in ch], 1.0), 0.55) for i, ch in enumerate([[0, 3, 7], [-4, 0, 3], [3, 7, 10], [-5, -1, 2], [0, 3, 7, 12]])],
                               *[(1.0 * i, tambor(hz(T - 36), 0.6), 0.55) for i in range(5)]), 2.2, 0.32))

    # Error: la sirena de la fábrica, corta y desafinada hacia abajo. Cambio: la palanca de la prensa y un metal que sube.
    # Tecla: la máquina de escribir pesada del soviet. Rascar: acero ennegrecido.
    with azar_propio('urss'):
        sirena = lambda d: filtro(barrido(hz(T + 6), hz(T - 1), d, 'sierra'), 'bajo', 2200) * np.minimum(1, t_(d) / 0.04) * np.minimum(1, (d - t_(d)) / 0.1)
        nuevos('urss',
               error=mezclar((0, sirena(0.4), 0.35), (0, tambor(hz(T - 36), 0.2, 0.6), 0.6)),
               cambio=mezclar((0, clic(0.02, (500, 2500)), 0.8), (0.05, bronce(T + 7, 0.1), 0.35), (0.14, bronce(T + 12, 0.16), 0.4)),
               tecla=mezclar((0, clic(0.022, (600, 3000)), 1), (0.006, clic(0.014, (300, 1200)), 0.7), (0, tambor(hz(T - 36), 0.03, 0.4), 0.3)),
               rascar=rascado((1600, 7000), campana(hz(T + 19), 0.35, 2.9, 2.5, 12), aspereza=110))

def gotico():
    # La menor (con tercera de picardía al final): órgano y campanas en una nave de piedra.
    T = 0
    nave = lambda x, c=2.6, m=0.35: reverb(x, c, m, 3000)
    org = lambda notas, d: acorde(organo, notas, d)
    S['gotico'] = dict(
        toque=mezclar((0, organo(hz(T), 0.09), 0.4), (0, clic(0.01, (800, 3000)), 0.3)),
        elegir=campana(hz(T + 19), 0.6, 2.0, 1.6, 6),
        pasar=nave(org([T - 12, T - 9, T - 5], 0.35), 1.2, 0.3),
        abrir=nave(mezclar((0, tambor(hz(T - 36), 0.4, 0.4), 0.7), (0.05, campana(hz(T - 5), 1.5, 2.0, 2.5, 2.5), 0.4)), 1.8, 0.4),
        guardar=nave(mezclar((0, campana(hz(T + 7), 1.2, 2.0, 2, 3), 0.6), (0.32, campana(hz(T), 1.4, 2.0, 2, 3), 0.6)), 1.6, 0.35),
        logro=nave(mezclar((0, org([T - 12, T - 9, T - 5], 0.7), 0.6), (0.65, org([T - 7, T - 4, T], 0.7), 0.6), (1.3, org([T - 5, T - 1, T + 2], 0.7), 0.6),
                           (1.95, org([T - 12, T - 8, T - 5, T], 1.2), 0.6), (1.95, campana(hz(T + 12), 1.6, 2.0, 1.5, 2), 0.25)), 2.4, 0.4),
        tema=nave(mezclar((0, org([T - 12, T - 9, T - 5], 0.6), 0.55), (0.1, campana(hz(T + 7), 1.6, 2.0, 1.8, 2.5), 0.35)), 2.0, 0.38),
        aviso=nave(mezclar((0, campana(hz(T - 5), 2.5, 2.0, 2.5, 1.5), 0.6), (0.9, campana(hz(T - 5), 2.5, 2.0, 2.5, 1.5), 0.5), (1.8, campana(hz(T - 12), 3.0, 2.0, 2.5, 1.2), 0.6)), 3.0, 0.4),
        solemne=nave(mezclar(*[(1.4 * i, org(ch, 1.5), 0.6) for i, ch in enumerate([[T - 12, T - 9, T - 5], [T - 16, T - 12, T - 9], [T - 7, T - 4, T], [T - 5, T - 1, T + 2], [T - 12, T - 8, T - 5, T]])],
                             (5.6, campana(hz(T), 2.8, 2.0, 2, 1.5), 0.35), (6.3, campana(hz(T - 5), 2.5, 2.0, 2, 1.5), 0.3)), 3.0, 0.42))

    # Error: el órgano en una segunda menor, en la nave. Cambio: dos campanas que suben a la quinta.
    # Tecla: la pluma sobre la vitela, más lenta. Rascar: peltre de cáliz.
    with azar_propio('gotico'):
        nuevos('gotico',
               error=nave(org([T - 12, T - 11], 0.35), 1.6, 0.35),
               cambio=mezclar((0, campana(hz(T + 12), 0.5, 2.0, 1.6, 6), 0.4), (0.12, campana(hz(T + 19), 0.6, 2.0, 1.6, 6), 0.4)),
               tecla=trazo((2000, 6000), 0.06, 16, 0.04),
               rascar=rascado((1500, 6000), campana(hz(T + 24), 0.8, 2.0, 2, 4), aspereza=70))

def espacio():
    # Mi mayor: pitidos de la sala de control y una nave que respira.
    T = 7    # Mi5
    def pad(notas, d):
        t = t_(d); x = sum(np.sin(2 * np.pi * hz(n) * t + 0.3 * np.sin(2 * np.pi * 0.3 * t)) + 0.5 * np.sin(2 * np.pi * hz(n) * 1.003 * t) for n in notas)
        return x * np.sin(np.pi * t / d) ** 1.5
    tono = lambda n, d=0.25: seno(hz(n), d) * np.minimum(1, t_(d) / 0.01) * np.minimum(1, (d - t_(d)) / 0.02)
    S['espacio'] = dict(
        toque=pip(hz(T + 12), 0.04), elegir=mezclar((0, pip(hz(T + 12), 0.035), 0.6), (0.045, pip(hz(T + 19), 0.035), 0.6)),
        pasar=mezclar((0, filtro(ruido(0.25), 'banda', (600, 4000)) * np.sin(np.pi * t_(0.25) / 0.25), 0.4), (0, barrido(hz(T - 12), hz(T + 7), 0.25) * np.sin(np.pi * t_(0.25) / 0.25), 0.2)),
        abrir=mezclar((0, filtro(ruido(0.4), 'alto', 2000) * env(int(0.4 * SR), 0.01, 0.35, 3), 0.4), (0.3, pip(hz(T + 19), 0.08), 0.6), (0.4, pip(hz(T + 24), 0.1), 0.6)),
        guardar=tono(T + 24),
        logro=reverb(mezclar(*[(i * 0.1, seno(hz(T + n), 0.5) * env(int(0.5 * SR), 0.01, 0.4, 3), 0.35) for i, n in enumerate([0, 4, 7, 12, 16])],
                             (0, barrido(hz(T - 36), hz(T - 24), 1.0) * np.sin(np.pi * t_(1.0)), 0.25)), 1.5, 0.3),
        tema=reverb(mezclar(*[(i * 0.06, pip(hz(T + n), 0.07), 0.5) for i, n in enumerate([12, 16, 19, 24])], (0, pad([T - 12, T - 5], 1.0), 0.15)), 1.4, 0.3),
        aviso=reverb(mezclar((0, tono(T + 24), 0.3), (0.4, pip(hz(T + 12), 0.15), 0.5), (0.6, pip(hz(T + 16), 0.15), 0.5), (0.8, pip(hz(T + 19), 0.3), 0.5), (0.3, pad([T - 24, T - 17], 2.6), 0.12)), 1.8, 0.35),
        solemne=reverb(mezclar((0, pad([T - 24, T - 17, T - 12], 6.5), 0.18), *[(0.8 + 0.9 * i, seno(hz(T + n), 1.2) * env(int(1.2 * SR), 0.05, 1.0, 2), 0.2) for i, n in enumerate([0, 4, 7, 11, 12, 16])],
                               (6.0, tono(T + 24), 0.15)), 2.5, 0.35))

    # Error: la alarma maestra, dos pitidos graves que chocan. Cambio: el interruptor del tablero y un pitido arriba.
    # Tecla: la pantalla táctil de la cabina. Rascar: titanio anodizado.
    with azar_propio('espacio'):
        nuevos('espacio',
               error=mezclar((0, pip(hz(T - 12), 0.1), 0.5), (0, pip(hz(T - 11), 0.1), 0.5), (0.16, pip(hz(T - 12), 0.12), 0.5), (0.16, pip(hz(T - 11), 0.12), 0.5)),
               cambio=mezclar((0, clic(0.008, (1500, 5000)), 0.6), (0.03, pip(hz(T + 19), 0.05), 0.5), (0.08, pip(hz(T + 26), 0.06), 0.5)),
               tecla=mezclar((0, clic(0.005, (4000, 10000)), 0.6), (0, pip(hz(T + 36), 0.008), 0.06)),
               rascar=rascado((2800, 9500), tono(T + 31, 0.3), aspereza=170))

def abismo():
    # Si, escala de tonos enteros (sin centro, como el agua): el ping del sonar, burbujas que suben, el agua que pasa,
    # el casco del batiscafo que cruje con la presión y un vibráfono apagado, como oído a través del agua.
    T = -10  # Si3
    ping = lambda n, d=0.7: seno(hz(n), d) * env(int(d * SR), 0.002, d * 0.55, 3) + 0.25 * seno(hz(n) * 2.01, d) * env(int(d * SR), 0.002, d * 0.2, 4)
    burbuja = lambda n, d=0.05: barrido(hz(n), hz(n) * 1.9, d) * env(int(d * SR), 0.002, d * 0.6, 3)
    agua = lambda d: filtro(ruido(d), 'bajo', 650) * np.sin(np.pi * t_(d) / d) ** 1.5
    casco = lambda d: filtro(ruido(d), 'banda', (70, 280)) * env(int(d * SR), 0.02, d * 0.7, 2)
    vib = lambda f, d: filtro(vibrafono(f, d), 'bajo', 2200)
    tonos = [0, 2, 4, 6, 8, 10]
    # Con su propio azar también los sonidos de siempre: un tema nuevo no mueve el ruido de los que van después.
    with azar_propio('abismo_base'):
        S['abismo'] = dict(
            toque=mezclar((0, vib(hz(T + 24), 0.16), 0.6), (0, burbuja(T + 24, 0.04), 0.35)),
            elegir=mezclar((0, burbuja(T + 26), 0.6), (0.05, burbuja(T + 30, 0.045), 0.5)),
            pasar=mezclar((0, agua(0.24), 0.9), (0.08, burbuja(T + 28, 0.035), 0.2), (0.14, burbuja(T + 32, 0.03), 0.15)),
            abrir=mezclar((0, casco(0.22), 0.7), (0.05, filtro(ruido(0.25), 'alto', 2500) * env(int(0.25 * SR), 0.02, 0.2, 3), 0.25), (0.2, ping(T + 18, 0.5), 0.45)),
            guardar=reverb(mezclar((0, ping(T + 12, 0.6), 0.6), (0.04, vib(hz(T + 24), 0.5), 0.35)), 1.0, 0.3),
            logro=reverb(mezclar(melodia(lambda f, d: vib(f, d), [T + 12 + n for n in tonos] + [T + 24], 0.08, 0.5, g=0.45),
                                 (0, ping(T, 1.2), 0.4), *[(0.1 * i, burbuja(T + 26 + 2 * i, 0.04), 0.15) for i in range(5)]), 1.6, 0.35),
            tema=reverb(mezclar((0, ping(T + 18, 0.8), 0.55), (0.45, ping(T + 18, 0.6), 0.25), (0.15, agua(0.5), 0.5),
                                *[(0.2 + 0.07 * i, burbuja(T + 24 + 2 * i, 0.04), 0.2) for i in range(4)]), 1.4, 0.35),
            aviso=reverb(mezclar(*[(0.5 * i, ping(T + n, 0.7), 0.5) for i, n in enumerate([22, 18, 14])],
                                 (1.5, vib(hz(T + 16), 0.8), 0.4), (0, agua(1.8), 0.35)), 1.8, 0.35),
            solemne=reverb(mezclar(*[(1.1 * i, acorde(lambda f, d: organo(f, d, 0.4), [T - 12 + a for a in ch], 1.3), 0.4)
                                     for i, ch in enumerate([[0, 4, 8], [2, 6, 10], [-2, 4, 8], [0, 6, 12], [0, 4, 8, 12]])],
                                   *[(0.55 + 1.1 * i, ping(T + [16, 18, 12, 20, 24][i], 0.9), 0.25) for i in range(5)],
                                   (0.3, barrido(hz(T - 5), hz(T - 9), 2.2) * np.sin(np.pi * t_(2.2) / 2.2) ** 2, 0.12),
                                   (0, agua(5.8), 0.3)), 2.4, 0.4))

    # Error: el ping que se hunde y el golpe sordo del casco. Cambio: la válvula de lastre que se abre y suelta burbujas.
    # Tecla: el lápiz sobre la pizarra de buceo. Rascar: cobre con verdín de un pecio, blando.
    with azar_propio('abismo'):
        nuevos('abismo',
               error=mezclar((0, barrido(hz(T + 6), hz(T - 2), 0.35) * env(int(0.35 * SR), 0.003, 0.3, 3), 0.5), (0.05, casco(0.3), 0.7)),
               cambio=mezclar((0, clic(0.01, (900, 3500)), 0.5), (0.02, burbuja(T + 22, 0.05), 0.5), (0.07, burbuja(T + 28, 0.04), 0.4), (0.1, burbuja(T + 26, 0.035), 0.3)),
               tecla=mezclar((0, trazo((900, 3600), 0.04, 6), 1), (0, filtro(ruido(0.012), 'bajo', 900) * env(int(0.012 * SR), 0.001, 0.006), 0.25)),
               rascar=rascado((600, 3800), ping(T + 24, 0.4), aspereza=55))

def andalus():
    # Re hiyaz: laúd (oud), darbuka y qanún.
    T = -7
    hijaz = [0, 1, 4, 5, 7, 8, 10, 12, 13, 16]
    oud = lambda g, d=0.9: pluck(hz(T + hijaz[g]), d, 0.35, 0.994)
    qanun = lambda g, d=0.6: pluck(hz(T + 12 + hijaz[g]), d, 0.7, 0.993)
    dum = lambda: tambor(hz(T - 24), 0.35, 0.15)
    tek = lambda: mezclar((0, filtro(ruido(0.06), 'banda', (2500, 8000)) * env(int(0.06 * SR), 0.001, 0.035), 1), (0, campana(hz(T + 31), 0.08, 1.7, 1, 30), 0.3))
    S['andalus'] = dict(
        toque=oud(0, 0.35), elegir=tek(), pasar=mezclar((0, dum(), 0.8), (0.12, tek(), 0.6)),
        abrir=mezclar(*[(i * 0.06, qanun(g), 0.4) for i, g in enumerate([0, 2, 4, 5, 7])]),
        guardar=mezclar((0, dum(), 0.7), (0, oud(4), 0.6), (0.22, oud(7, 1.0), 0.6)),
        logro=reverb(mezclar((0, dum(), 0.6), (0.24, tek(), 0.4), (0.36, tek(), 0.4), (0.48, dum(), 0.6), (0.72, tek(), 0.4),
                             (0, oud(4, 0.5), 0.5), (0.24, oud(5, 0.5), 0.5), (0.36, oud(4, 0.5), 0.5), (0.48, oud(2, 0.5), 0.5), (0.72, oud(1, 0.5), 0.5), (0.96, oud(0, 1.2), 0.6)), 1.0, 0.2),
        tema=reverb(mezclar((0, dum(), 0.6), *[(0.05 * i, qanun(g, 0.8), 0.4) for i, g in enumerate([0, 1, 2, 3, 4, 7])]), 1.0, 0.22),
        aviso=reverb(mezclar(*[(0.28 * i, oud(g, 0.9), 0.55) for i, g in enumerate([4, 5, 7, 5, 4])], (0, dum(), 0.4), (0.56, dum(), 0.4), (1.12, dum(), 0.4)), 1.4, 0.25),
        solemne=reverb(mezclar(*[(0.45 * i, oud(g, 1.2), 0.55) for i, g in enumerate([7, 5, 4, 2, 1, 2, 4, 1, 0])], *[(0.9 * i, dum(), 0.35) for i in range(5)], *[(0.9 * i + 0.45, tek(), 0.25) for i in range(5)],
                               (4.2, mezclar(*[(i * 0.05, qanun(g, 1.2), 0.3) for i, g in enumerate([0, 2, 4, 7])]), 1)), 2.0, 0.3))

    # Error: el dum y el laúd que baja el semitono del hiyaz. Cambio: el tek y el qanún que sube.
    # Tecla: el cálamo de caña, que raspa dos veces. Rascar: latón de las lámparas nazaríes.
    with azar_propio('andalus'):
        nuevos('andalus',
               error=mezclar((0, dum(), 0.6), (0.04, oud(1, 0.3), 0.5), (0.18, oud(0, 0.5), 0.5)),
               cambio=mezclar((0, tek(), 0.5), (0.05, qanun(4, 0.3), 0.4), (0.12, qanun(7, 0.4), 0.4)),
               tecla=mezclar((0, trazo((2200, 7000), 0.03, 10), 1), (0.035, trazo((2200, 7000), 0.025, 8), 0.7)),
               rascar=rascado((1500, 6500), qanun(9, 0.5), aspereza=90))

def mongol():
    # Mi mayor pentatónico: cascos, morin juur y canto difónico sobre un Mi grave.
    T = -5   # Mi4
    penta = [0, 2, 4, 7, 9, 12, 14, 16]
    casco = lambda: mezclar((0, filtro(ruido(0.05), 'banda', (300, 1500)) * env(int(0.05 * SR), 0.001, 0.03, 6), 1), (0, tambor(hz(T - 24), 0.06, 0), 0.5))
    galope = lambda: mezclar((0, casco(), 0.8), (0.07, casco(), 0.7), (0.13, casco(), 1))
    def joomii(d):
        """Bordón en Mi2 y un armónico (8.º, 10.º, 12.º: Mi, Sol♯, Si) que va cambiando: siempre dentro del acorde."""
        f = hz(T - 24); t = t_(d)
        base = arco(f, d, 0) * 0.6
        k = np.array([8, 10, 12, 10])[np.minimum(3, (t / d * 4).astype(int))]
        armon = np.sin(2 * np.pi * np.cumsum(f * k) / SR) * 0.25
        return (base + armon) * np.minimum(1, t / 0.2) * np.minimum(1, (d - t) / 0.3)
    S['mongol'] = dict(
        toque=casco(), elegir=pluck(hz(T), 0.3, 0.4), pasar=galope(),
        abrir=joomii(0.6),
        guardar=mezclar((0, tambor(hz(T - 24), 0.4, 0.3), 0.8), (0.05, arco(hz(T), 0.6), 0.4)),
        logro=reverb(mezclar((0, galope(), 0.7), (0.3, galope(), 0.7), (0.6, tambor(hz(T - 24), 0.4, 0.3), 0.8),
                             (0.6, arco(hz(T + penta[2]), 0.3), 0.35), (0.9, arco(hz(T + penta[3]), 0.3), 0.35), (1.2, arco(hz(T + penta[5]), 0.8), 0.4)), 1.4, 0.25),
        tema=reverb(mezclar((0, galope(), 0.6), (0.2, arco(hz(T + 7), 0.35), 0.4), (0.55, arco(hz(T + 12), 0.7), 0.45)), 1.4, 0.25),
        aviso=reverb(mezclar(*[(0.5 * i, arco(hz(T + penta[g]), 0.6), 0.45) for i, g in enumerate([4, 3, 2, 3, 4])], (0, galope(), 0.4), (1.0, galope(), 0.4)), 1.8, 0.3),
        solemne=reverb(mezclar((0, joomii(6.8), 0.5), *[(0.9 + 0.7 * i, arco(hz(T + penta[g]), 0.9), 0.35) for i, g in enumerate([2, 3, 4, 6, 4, 3, 2, 0])]), 2.5, 0.35))

    # Error: el morin juur que resbala medio tono abajo y un casco. Cambio: dos cascos y una cuerda a la quinta.
    # Tecla: el pincel de fieltro. Rascar: plata sobredorada.
    with azar_propio('mongol'):
        nuevos('mongol',
               error=mezclar((0, casco(), 0.6), (0.04, arco(hz(T + 7), 0.15), 0.4), (0.18, arco(hz(T + 6), 0.3), 0.4)),
               cambio=mezclar((0, casco(), 0.5), (0.07, casco(), 0.5), (0.1, pluck(hz(T + 7), 0.4, 0.4), 0.45)),
               tecla=filtro(ruido(0.06), 'banda', (900, 3800)) * env(int(0.06 * SR), 0.01, 0.035, 4),
               rascar=rascado((1400, 6000), pluck(hz(T + 24), 0.5, 0.7), aspereza=85))

def renacimiento():
    # La mayor: pluma, hojas, laúd y flauta dulce.
    T = 0
    laud = lambda n, d=0.9: pluck(hz(n), d, 0.45, 0.995)
    rasgueo = lambda ns: mezclar(*[(i * 0.03, laud(n, 1.0), 0.4) for i, n in enumerate(ns)])
    S['renacimiento'] = dict(
        toque=filtro(ruido(0.035), 'banda', (3000, 9000)) * env(int(0.035 * SR), 0.002, 0.025),
        elegir=laud(7, 0.4),
        pasar=papel(0.2, (700, 4500)),
        abrir=rasgueo([-5, 0, 4, 7]),
        guardar=mezclar((0, rasgueo([-10, -5, 2, 7]), 0.8), (0.3, rasgueo([-5, 0, 4, 7]), 0.9)),
        logro=reverb(mezclar(*[(0.16 * i, laud(n, 0.8), 0.55) for i, n in enumerate([0, 4, 7, 9, 7])], (0.85, rasgueo([-12, 0, 4, 7, 12]), 0.8)), 1.2, 0.22),
        tema=reverb(mezclar((0, rasgueo([-12, -5, 0, 4, 7]), 0.8), (0.25, flauta(hz(12), 0.4), 0.25), (0.6, flauta(hz(16), 0.5), 0.25)), 1.2, 0.25),
        aviso=reverb(mezclar(*[(0.25 * i, laud(n, 0.9), 0.55) for i, n in enumerate([7, 9, 7, 4, 0])], (1.3, rasgueo([-12, 0, 4, 7]), 0.7)), 1.4, 0.25),
        solemne=reverb(mezclar(*[(0.5 * i, flauta(hz(n + 12), 0.48), 0.3) for i, n in enumerate([0, 2, 4, 5, 7, 5, 4, 2, 4, 0])],
                               *[(1.0 * i, rasgueo(c), 0.45) for i, c in enumerate([[-12, 0, 4, 7], [-7, -3, 0, 5], [-5, -1, 2, 7], [-12, -5, 0, 4], [-12, 0, 4, 7, 12]])]), 2.0, 0.3))

    # Error: el laúd apagado en una segunda menor. Cambio: un rasgueo corto que sube.
    # Tecla: la pluma de ganso del taller. Rascar: bronce de taller, pardo.
    with azar_propio('renacimiento'):
        nuevos('renacimiento',
               error=mezclar((0, pluck(hz(T + 7), 0.3, 0.2), 0.5), (0.12, pluck(hz(T + 6), 0.5, 0.15), 0.5)),
               cambio=mezclar((0, laud(T + 4, 0.4), 0.4), (0.05, laud(T + 7, 0.4), 0.4), (0.1, laud(T + 12, 0.5), 0.45)),
               tecla=trazo((3000, 9000), 0.04, 22, 0.025),
               rascar=rascado((1300, 5800), laud(T + 24, 0.5), aspereza=75))

def pirata():
    # Mi dórico, el modo de las salomas: la campana de a bordo (que da las medias horas por pares), la concertina, el
    # violín de cubierta, la madera del casco que cruje y el oleaje.
    T = -5   # Mi4
    bronce = lambda n, d=1.8: campana(hz(n), d, 2.76, 1.4, 2.2)
    def campanadas(pares, d=1.6):
        """Las campanadas de la guardia: de dos en dos, juntas, y una pausa entre pares."""
        return mezclar(*[(0.75 * p + 0.28 * k, bronce(T + 12, d), 0.5) for p in range(pares) for k in range(2)])
    madera = lambda f=(250, 1400), d=0.05: mezclar((0, filtro(ruido(d), 'banda', f) * env(int(d * SR), 0.001, d * 0.5, 4), 0.8), (0, tambor(hz(T - 17), d * 1.6, 0.1), 0.35))
    def crujido(d=0.4, f0=70):
        """El casco que trabaja: una sierra grave con tirones irregulares, como una tabla que roza otra."""
        t = t_(d); f = f0 * (1 + 0.25 * np.sin(2 * np.pi * 1.3 * t / d) + 0.1 * filtro(ruido(d), 'bajo', 12))
        ph = 2 * np.pi * np.cumsum(f) / SR
        return filtro(((ph / (2 * np.pi)) % 1) * 2 - 1, 'banda', (180, 1600)) * np.sin(np.pi * t / d) ** 1.5
    ola = lambda d: filtro(ruido(d), 'banda', (250, 2600)) * np.sin(np.pi * t_(d) / d) ** 2 * (0.6 + 0.4 * np.abs(filtro(ruido(d), 'bajo', 8)))
    violin = lambda n, d: arco(hz(n), d, 6.2)
    with azar_propio('pirata_base'):
        S['pirata'] = dict(
            toque=madera(), elegir=mezclar((0, bronce(T + 19, 0.5), 0.6), (0, madera((600, 2400), 0.02), 0.3)),
            pasar=mezclar((0, ola(0.24), 0.9), (0.04, filtro(ruido(0.08), 'banda', (1200, 4000)) * env(int(0.08 * SR), 0.004, 0.05, 3), 0.25)),
            abrir=mezclar((0, crujido(0.35), 0.45), (0.12, acorde(fuelle, [T - 12 + a for a in (0, 7, 15)], 0.45), 0.6)),
            guardar=mezclar((0, madera(), 0.6), (0.04, bronce(T + 12, 1.2), 0.45), (0.32, bronce(T + 12, 1.2), 0.45)),
            logro=reverb(mezclar(*[(0.11 * i, violin(T + n, 0.12 if i < 6 else 0.6), 0.45) for i, n in enumerate([0, 2, 3, 7, 9, 10, 12])],
                                 (0.66, acorde(fuelle, [T - 12 + a for a in (0, 7, 12, 15)], 0.8), 0.5), (0.66, bronce(T + 24, 1.4), 0.25)), 1.1, 0.22),
            tema=reverb(mezclar((0, ola(0.7), 0.5), (0.15, acorde(fuelle, [T - 12 + a for a in (0, 3, 7, 14)], 0.7, ataque=0.12), 0.55), (0.55, bronce(T + 12, 1.4), 0.35)), 1.2, 0.25),
            aviso=reverb(mezclar((0, campanadas(2), 1.0), (0, ola(1.8), 0.35)), 1.3, 0.22),
            solemne=reverb(mezclar(*[(0.95 * i, acorde(fuelle, [T - 12 + a for a in ch], 1.0, ataque=0.15), 0.5)
                                     for i, ch in enumerate([[0, 3, 7], [-2, 2, 5], [5, 9, 12], [-5, 2, 7], [0, 3, 7, 12]])],
                                   *[(0.95 * i + 0.45, violin(T + n, 0.45), 0.22) for i, n in enumerate([7, 5, 9, 10])],
                                   (0, ola(4.8), 0.4), (3.9, campanadas(2), 0.6)), 1.8, 0.32))

    # Error: el cañonazo lejano y la concertina que se queda sin aire medio tono abajo. Cambio: dos golpes en la
    # borda y una campanada. Tecla: la pluma de la bitácora, que raspa el papel de trapo. Rascar: un doblón de oro.
    with azar_propio('pirata'):
        nuevos('pirata',
               error=mezclar((0, tambor(hz(T - 31), 0.5, 0.6), 0.6), (0.12, fuelle(hz(T + 3), 0.14), 0.35), (0.26, fuelle(hz(T + 2), 0.3), 0.35)),
               cambio=mezclar((0, madera(), 0.6), (0.07, madera((400, 2000)), 0.6), (0.12, bronce(T + 19, 0.6), 0.35)),
               tecla=trazo((1800, 6500), 0.05, 14, 0.03),
               rascar=rascado((1100, 5000), bronce(T + 24, 0.6), aspereza=70))

def revolucion():
    # Sol mayor: pífano y tambor de las milicias, el papel de los carteles; en lo solemne, cuerdas de himno y bombo.
    T = -2   # Sol4
    pifano = lambda n, d: flauta(hz(n + 12), d)
    def redoble(d=0.22):
        x = np.zeros(int(d * SR))
        for i in range(int(d / 0.024)):
            g = filtro(ruido(0.03), 'banda', (1300, 5500)) * env(int(0.03 * SR), 0.001, 0.02)
            k = int(i * 0.024 * SR); x[k:k + len(g)] += g[:len(x) - k] * (0.5 + 0.5 * i / (d / 0.024))
        return x
    golpe = lambda: mezclar((0, filtro(ruido(0.05), 'banda', (1300, 5500)) * env(int(0.05 * SR), 0.001, 0.03), 0.8), (0, tambor(hz(T - 24), 0.07), 0.45))
    S['revolucion'] = dict(
        toque=golpe(), elegir=pifano(T + 7, 0.12), pasar=papel(0.2, (600, 4000)),
        abrir=mezclar((0, pifano(T, 0.12), 0.5), (0.13, pifano(T + 4, 0.12), 0.5), (0.26, pifano(T + 7, 0.25), 0.5)),
        guardar=mezclar((0, golpe(), 0.6), (0.05, pifano(T + 7, 0.12), 0.45), (0.18, pifano(T + 12, 0.3), 0.5)),
        logro=reverb(mezclar((0, redoble(0.4), 0.6), *[(0.42 + 0.13 * i, pifano(T + n, 0.13 if i < 5 else 0.6), 0.5) for i, n in enumerate([0, 4, 7, 12, 11, 12])]), 1.2, 0.22),
        tema=reverb(mezclar((0, golpe(), 0.6), (0.1, pifano(T + 7, 0.14), 0.5), (0.25, pifano(T + 4, 0.14), 0.5), (0.4, pifano(T + 7, 0.14), 0.5),
                            (0.55, pifano(T + 12, 0.5), 0.55), (0.55, golpe(), 0.6)), 1.2, 0.25),
        aviso=reverb(mezclar(*[(0.16 * i, pifano(T + n, 0.15 if i < 5 else 0.6), 0.5) for i, n in enumerate([7, 7, 9, 7, 12, 11])], (1.0, redoble(0.3), 0.5)), 1.3, 0.25),
        solemne=reverb(mezclar(*[(1.0 * i, acorde(lambda f, d: arco(f, d, 5), [T - 12 + a for a in ch], 1.0), 0.45) for i, ch in enumerate([[0, 4, 7], [5, 9, 12], [7, 11, 14], [0, 4, 7, 12]])],
                               *[(1.0 * i, tambor(hz(T - 36), 0.4, 0.2), 0.5) for i in range(4)], (0, redoble(0.8), 0.35), (2.9, redoble(1.0), 0.4)), 2.0, 0.3))

    # Error: el pífano que se cae y el tambor destemplado. Cambio: dos golpes y el pífano a la quinta.
    # Tecla: los tipos de la imprenta de los carteles. Rascar: estaño de botón de uniforme.
    with azar_propio('revolucion'):
        nuevos('revolucion',
               error=mezclar((0, pifano(T + 7, 0.1), 0.45), (0.12, pifano(T + 6, 0.25), 0.45), (0.12, tambor(hz(T - 24), 0.25, 0.5), 0.45)),
               cambio=mezclar((0, golpe(), 0.5), (0.05, pifano(T, 0.08), 0.4), (0.13, pifano(T + 7, 0.14), 0.45)),
               tecla=mezclar((0, clic(0.014, (900, 3500)), 1), (0.003, clic(0.008, (2500, 6000)), 0.5)),
               rascar=rascado((1200, 5200), campana(hz(T + 19), 0.4, 2.2, 1.2, 8), aspereza=55))

def antiguas():
    # Re dórico: arpa, cuerno grave y piedra; en lo solemne, coro grave, cuerno y tambor de salón.
    T = -7   # Re4
    arpa = lambda n, d=1.0: pluck(hz(n), d, 0.62, 0.997)
    cuerno = lambda n, d: metal(hz(n - 12), d, brillo=1300, ataque=0.09)
    piedra = lambda: mezclar((0, filtro(ruido(0.06), 'bajo', 500) * env(int(0.06 * SR), 0.001, 0.04), 1.0), (0, tambor(hz(T - 24), 0.08), 0.4))
    S['antiguas'] = dict(
        toque=piedra(), elegir=arpa(T + 7, 0.5), pasar=papel(0.22, (500, 3500)),
        abrir=mezclar(*[(0.06 * i, arpa(T + n, 0.8), 0.45) for i, n in enumerate([0, 3, 7, 10])]),
        guardar=mezclar((0, piedra(), 0.6), (0.06, arpa(T + 7, 0.6), 0.5), (0.18, arpa(T + 12, 0.9), 0.5)),
        logro=reverb(mezclar((0, cuerno(T, 0.6), 0.5), (0.6, cuerno(T + 7, 0.9), 0.5), *[(0.7 + 0.1 * i, arpa(T + n, 0.9), 0.4) for i, n in enumerate([0, 3, 7, 10, 12, 14])]), 1.6, 0.3),
        tema=reverb(mezclar((0, cuerno(T, 0.8), 0.55), (0.3, cuerno(T + 7, 1.0), 0.5), *[(0.5 + 0.08 * i, arpa(T + n, 1.0), 0.4) for i, n in enumerate([-12, -5, 0, 3, 7])]), 1.6, 0.3),
        aviso=reverb(mezclar(*[(0.2 * i, arpa(T + n, 1.0), 0.5) for i, n in enumerate([7, 5, 3, 5, 7, 12])]), 1.5, 0.3),
        solemne=reverb(mezclar(*[(1.0 * i, acorde(coro, [T - 12 + a for a in ch], 1.1), 0.45) for i, ch in enumerate([[0, 3, 7], [-2, 2, 5], [5, 9, 12], [0, 3, 7, 12]])],
                               *[(1.0 * i, tambor(hz(T - 36), 0.5, 0.15), 0.45) for i in range(4)], (0, cuerno(T, 2.0), 0.3)), 2.2, 0.35))

    # Error: la piedra que asienta mal y el arpa que baja medio tono. Cambio: dos cuerdas del arpa a la quinta.
    # Tecla: el cincel sobre la piedra. Rascar: hierro viejo de las minas.
    with azar_propio('antiguas'):
        nuevos('antiguas',
               error=mezclar((0, piedra(), 0.6), (0.06, arpa(T + 3, 0.3), 0.45), (0.2, arpa(T + 2, 0.5), 0.45)),
               cambio=mezclar((0, arpa(T + 12, 0.4), 0.45), (0.08, arpa(T + 19, 0.5), 0.45)),
               tecla=mezclar((0, clic(0.012, (400, 2000)), 1), (0, filtro(ruido(0.02), 'bajo', 500) * env(int(0.02 * SR), 0.001, 0.01), 0.5)),
               rascar=rascado((900, 4500), cuerno(T + 12, 0.4), aspereza=45))

# ------------------------------------------------------------------ el aviso de los lugares especiales

def lugar():
    """n_lugar: llegaste a un lugar especial. Tres campanas claras (Mi, Sol, Si) que se quedan sonando."""
    with azar_propio('lugar'):
        camp = lambda n, d=2.2: campana(hz(n), d, 2.76, 1.0, 2.6)
        X['n_lugar'] = reverb(mezclar((0, camp(7), 0.5), (0.18, camp(10), 0.5), (0.32, camp(14), 0.55)), 0.8, 0.18)

if __name__ == '__main__':
    # python3 afinados.py <res/raw> [temas] [claves]: [temas] separados por comas (o «todos»); con [claves] (por ejemplo
    # «error,cambio,tecla,rascar,lugar») solo se escriben esos sonidos y los demás archivos se quedan como están.
    todos = (
        romano,
        ciber,
        neon,
        heleno,
        egipto,
        medieval,
        templario,
        distopia,
        refugio,
        orden,
        laboratorio,
        colonos,
        napoleon,
        botanico,
        ruinas,
        navidad,
        seda,
        urss,
        gotico,
        espacio,
        abismo,
        andalus,
        mongol,
        renacimiento,
        pirata,
        revolucion,
        antiguas,
    )
    solo = sys.argv[2].split(',') if len(sys.argv) > 2 and sys.argv[2] != 'todos' else None
    claves = sys.argv[3].split(',') if len(sys.argv) > 3 else None
    for f in todos:
        if solo is None or f.__name__ in solo: f()
    lugar()
    for tema, d in S.items():
        for k, x in d.items():
            if claves is not None and k not in claves: continue
            if k in ('aviso', 'solemne'): escribir(f'n_{tema}_{k}', x, calidad=4, rms_db=-16.0)
            else: escribir(f's_{tema}_{k}', x)
        print('listo', tema, flush=True)
    for nombre, x in X.items():
        clase = nombre.split('_')[1]
        if claves is not None and clase not in claves: continue
        if nombre == 'n_lugar': escribir(nombre, x, calidad=4, rms_db=-20.5)
        else: escribir(nombre, x, rms_db={'tecla': -26.0, 'rascar': -21.0}[clase])
