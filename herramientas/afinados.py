"""Vestigium Mei — todos los sonidos, afinados.

Cada tema tiene una tónica y una escala; todo lo que tiene altura (campanas, laúdes, cornetas, pitidos, órgano…) sale de esa
escala, y lo que suena junto forma acordes de ella. Lo que no tiene altura (papel, pluma, madera, máquina de escribir) queda
como ruido. Además: el «tema» (lo que suena al elegir ese tema), las teclas del diario y el rascado del mapa.

Uso: python3 afinados.py <carpeta res/raw>
"""
import os, sys, subprocess, tempfile, wave
from contextlib import contextmanager
import numpy as np
from scipy.signal import fftconvolve, butter, sosfilt, sosfiltfilt
from scipy.ndimage import maximum_filter1d

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
def sierra_limpia(f, d, fase=0.0):
    """Sierra sin el «aliasing» de la sierra ingenua (polyBLEP): redondea cada salto. [f] es una nota o una curva de notas."""
    n = int(d * SR)
    dt = np.full(n, float(f)) / SR if np.ndim(f) == 0 else np.asarray(f, dtype=np.float64)[:n] / SR
    p = (fase + np.cumsum(dt) - dt) % 1.0
    y = 2 * p - 1
    a = p < dt; x = p[a] / dt[a]; y[a] -= x + x - x * x - 1
    b = p > 1 - dt; x = (p[b] - 1) / dt[b]; y[b] -= x * x + x + x + 1
    return y
def paso_bajo_movil(x, cortes, reso=1.3, bloque=32):
    """Filtro paso bajo de 24 dB por octava (dos biquads, uno con resonancia [reso]) cuyo corte sigue la curva [cortes]."""
    y = np.zeros(len(x)); zi = np.zeros((2, 2))
    for i in range(0, len(x), bloque):
        w = 2 * np.pi * min(max(float(cortes[i]), 30.0), SR * 0.45) / SR; c = np.cos(w); s = np.sin(w)
        filas = []
        for q in (reso, 0.54):
            al = s / (2 * q); a0 = 1 + al
            filas.append([(1 - c) / 2 / a0, (1 - c) / a0, (1 - c) / 2 / a0, 1.0, -2 * c / a0, (1 - al) / a0])
        y[i:i + bloque], zi = sosfilt(np.array(filas), x[i:i + bloque], zi=zi)
    return y
def analogico(f, d, corte=(250, 2600, 700), t_pico=0.01, caida=0.25, reso=2.2, ataque=0.006, sosten=1.0, decae=0.3, suelta=0.06,
              desafina=0.005, cae=0.0):
    """Sintetizador analógico de los años setenta: dos sierras apenas desafinadas que derivan un poco, por un filtro resonante
    de 24 dB cuyo corte sube de corte[0] a corte[1] en [t_pico] s y vuelve a corte[2] con la constante [caida]. El volumen
    baja a [sosten] con la constante [decae]; [cae] son los semitonos que la nota se hunde de principio a fin."""
    t = t_(d)
    fb = f * (1 + 0.0018 * np.sin(2 * np.pi * 0.35 * t + rng.uniform(0, 2 * np.pi))) * 2 ** (-cae * t / d / 12)
    x = 0.55 * sierra_limpia(fb, d) + 0.45 * sierra_limpia(fb * (1 + desafina), d, 0.37)
    c0, c1, c2 = corte
    sube = c0 * (c1 / c0) ** np.minimum(1, t / max(t_pico, 1e-3))
    baja = c2 + (c1 - c2) * np.exp(-np.maximum(0, t - t_pico) / max(caida, 1e-3))
    x = paso_bajo_movil(x, np.where(t < t_pico, sube, baja), reso)
    return x * np.minimum(1, t / max(ataque, 1e-4)) * (sosten + (1 - sosten) * np.exp(-t / max(decae, 1e-4))) * np.minimum(1, np.maximum(0, d - t) / max(suelta, 1e-4))
def consola(f, d=0.06, ancho=0.35, cae=0.0):
    """Pitido de consola de los setenta: un pulso estrecho por un altavoz pequeño, con la nota que se asienta al encender y
    una compuerta (prende y apaga de golpe, no se apaga sola). [cae]: semitonos que se hunde, como cuando falla la corriente."""
    t = t_(d)
    fb = f * (1 + 0.02 * np.exp(-t / 0.005)) * 2 ** (-cae * t / d / 12)
    x = filtro(sierra_limpia(fb, d) - sierra_limpia(fb, d, ancho), 'banda', (350, min(4000, f * 4)))
    return x * np.minimum(1, t / 0.002) * np.minimum(1, np.maximum(0, d - t) / 0.006)
def rele(fuerza=1.0, resorte=2650):
    """Un relé que cierra: el contacto, el golpe de la armadura, el resorte que suena un instante (en [resorte] Hz) y dos rebotes."""
    golpe = filtro(ruido(0.025), 'banda', (220, 1500)) * env(int(0.025 * SR), 0.0005, 0.008, 4)
    return mezclar((0, clic(0.004, (2500, 8500)), 0.8 * fuerza), (0.0008, golpe, 0.9 * fuerza),
                   (0.001, seno(resorte, 0.03) * env(int(0.03 * SR), 0.0005, 0.012, 4), 0.12 * fuerza),
                   (0.0065, clic(0.003, (1800, 6000)), 0.35 * fuerza), (0.011, clic(0.0025, (1800, 6000)), 0.15 * fuerza))
def teletipo(letras=6, cps=10, motor=None):
    """El teletipo que imprime [letras] letras a [cps] por segundo: el tipo que pega, el carro y el selector que cascabelea.
    Con [motor] (una frecuencia), debajo zumba el motor."""
    partes = []
    for i in range(letras):
        s = max(0.0, i / cps + rng.uniform(-0.006, 0.006)); g = rng.uniform(0.7, 1.0)
        partes += [(s, clic(0.005, (1800, 7500)), 0.9 * g),
                   (s + 0.002, filtro(ruido(0.03), 'banda', (300, 1600)) * env(int(0.03 * SR), 0.001, 0.012, 4), 0.8 * g),
                   (s + 0.035, filtro(ruido(0.04), 'banda', (3000, 8000)) * env(int(0.04 * SR), 0.004, 0.02, 3), 0.12)]
    if motor:
        dm = letras / cps + 0.1; t = t_(dm)
        m = filtro(sierra_limpia(motor, dm) + 0.5 * np.sin(4 * np.pi * motor * t), 'bajo', 600)
        partes.append((0, m * np.minimum(1, t / 0.04) * np.minimum(1, (dm - t) / 0.06), 0.18))
    return mezclar(*partes)
def resonador(x, f, ancho):
    """Una resonancia (un formante de la voz, la caja de un instrumento): un polo doble en [f] Hz, de [ancho] Hz, con
    ganancia 1 en su centro."""
    r = np.exp(-np.pi * ancho / SR); w = 2 * np.pi * f / SR
    g = (1 - r) * np.sqrt(1 - 2 * r * np.cos(2 * w) + r * r)
    return sosfilt(np.array([[g, 0.0, 0.0, 1.0, -2 * r * np.cos(w), r * r]]), x)
def domar(x, umbral=0.4, razon=3.0):
    """Un compresor suave para una nota pulsada: por encima de [umbral] (del pico) baja la ganancia con razón [razon],
    siguiendo una envolvente que mira un poco adelante y no tiembla (no recorta la onda: no ensucia el timbre). La nota
    pierde el golpe de más del principio y la música puede sonar al mismo volumen que las de los otros temas."""
    a = np.abs(x); p = a.max() + 1e-12
    e = maximum_filter1d(a, int(0.02 * SR)) / p
    e = np.maximum(sosfiltfilt(butter(2, 40 / (SR / 2), output='sos'), e), 1e-4)
    return x * np.where(e > umbral, (umbral / e) ** (1 - 1 / razon), 1.0)
def kinnor(f, d=0.8, brillo=0.5, decae=0.9935):
    """El kinnor, la lira de mano de Galilea: cuerda de tripa pulsada con púa (el golpe seco de la púa al empezar), que
    arranca apenas alta y se asienta, y la caja de madera pequeña que le da un color hueco (dos resonancias graves)."""
    x = pluck(f, d, brillo, decae)
    t = t_(d)
    lee = np.minimum(np.cumsum(1 + 0.007 * np.exp(-t / 0.035)) - 1, len(x) - 1)
    x = np.interp(lee, np.arange(len(x)), x)
    caja = 0.55 * resonador(x, 290, 90) + 0.35 * resonador(x, 610, 150)
    pua = clic(0.004, (2200, 7500))
    # Sin el desplazamiento de la cuerda al soltarla, que el oído no oye y el altavoz no da: un paso alto suave.
    return filtro(mezclar((0, 0.8 * x + caja, 1.0), (0, pua, 0.35 * brillo)), 'alto', min(60.0, f * 0.5))
def crotalos(f, d=1.0, apagado=False):
    """Crótalos (címbalos de dedo): dos discos pequeños de bronce que chocan. Cada disco da sus parciales de placa
    (no armónicos: 1, 2.71, 5.14, 8.33) y los dos nunca están afinados igual, así que baten despacio. [apagado]: el
    choque que la mano detiene enseguida."""
    t = t_(d); k = 3.5 if apagado else 1.0
    def disco(fd, fase):
        return sum(a * np.sin(2 * np.pi * fd * r * t + fase * (i + 1)) * np.exp(-t * c * k)
                   for i, (r, a, c) in enumerate(((1.0, 1.0, 1.5), (2.71, 0.5, 3.0), (5.14, 0.28, 6.0), (8.33, 0.15, 10.0)))
                   if fd * r < SR * 0.45)
    x = 0.65 * disco(f, 0.0) + 0.35 * disco(f * 1.0042, 1.3)
    choque = clic(0.004, (4500, 10000))
    return 0.3 * mezclar((0, x * np.minimum(1, t / 0.0012) * np.minimum(1, np.maximum(0, d - t) / 0.04), 1.0), (0, choque, 0.5))
VOCALES = {   # formantes (centro, ancho, ganancia) de una voz grave de hombre
    'm': ((250, 60, 1.0), (1050, 160, 0.10), (2300, 220, 0.04)),
    'o': ((430, 70, 1.0), (770, 85, 0.8), (2450, 130, 0.25), (3200, 200, 0.1)),
    'u': ((320, 60, 1.0), (730, 80, 0.38), (2300, 130, 0.07)),
}
def salmodia(f, d, vocal='o', abre=0.3, subida=0.6):
    """Una sola voz grave de hombre que salmodia en voz baja (no un coro): pulso de la glotis por los formantes de la
    boca, que empieza cerrada en «m» y se abre a la vocal en [abre] s. La nota entra desde [subida] semitonos abajo,
    como al cantar a media voz, y se queda recta, sin vibrato, con el temblor leve de una voz real y algo de aliento."""
    t = t_(d); n = len(t)
    temblor = filtro(rng.standard_normal(n), 'bajo', 5); temblor /= np.abs(temblor).max() + 1e-9
    fb = f * 2 ** (-subida * np.exp(-t / 0.06) / 12) * (1 + 0.0035 * temblor)
    src = filtro(sierra_limpia(fb, d), 'bajo', 1100, 1) + 0.035 * filtro(ruido(d), 'banda', (900, 4200))
    def boca(v):
        y = sum(g * resonador(src, F, B) for F, B, g in VOCALES[v])
        return y / (np.sqrt(np.mean(y ** 2)) + 1e-12)
    w = 0.5 - 0.5 * np.cos(np.pi * np.minimum(1, t / max(abre, 1e-3)))
    x = filtro(boca('m') * (1 - w) + boca(vocal) * w, 'alto', 130)   # la fundamental grave sobra en un teléfono: se oye por sus armónicos
    return 0.075 * x * np.minimum(1, t / 0.09) * np.minimum(1, np.maximum(0, d - t) / 0.2)
def zumbido_hondo(f, d, cae=0.0, entra=0.35, sale=0.35, abre=(300, 900)):
    """El zumbido de los arcontes: tres sierras graves que baten entre sí, por un filtro resonante que se abre y se cierra
    despacio, con un temblor áspero. Se oye por sus armónicos aunque el altavoz no dé la fundamental. [cae]: semitonos que
    se hunde; [entra] y [sale]: qué parte de la duración tarda en crecer y en apagarse."""
    t = t_(d)
    fb = f * 2 ** (-cae * t / d / 12)
    x = 0.5 * sierra_limpia(fb, d) + 0.4 * sierra_limpia(fb * 1.0085, d, 0.3) + 0.25 * sierra_limpia(fb * 2.003, d, 0.6)
    x = paso_bajo_movil(x, abre[0] + (abre[1] - abre[0]) * (0.5 - 0.5 * np.cos(2 * np.pi * 0.23 * t)), 2.4)
    x = filtro(x, 'alto', f * 1.6)   # sin la fundamental, que el altavoz no da y solo resta volumen a lo que sí se oye
    aspero = 0.78 + 0.22 * np.sin(2 * np.pi * 9.1 * t + 0.8 * np.sin(2 * np.pi * 1.3 * t))
    return 0.35 * x * aspero * np.minimum(1, t / max(entra * d, 1e-3)) ** 1.5 * np.minimum(1, np.maximum(0, d - t) / max(sale * d, 1e-3))
def pleroma(f, d=2.0, ataque=0.12):
    """La nota limpia y alta de la luz de arriba: un seno casi puro, sin vibrato ni batido, que entra suave y se queda."""
    t = t_(d)
    x = np.sin(2 * np.pi * f * t) + 0.05 * np.sin(4 * np.pi * f * t) * np.exp(-t * 3)
    return 0.3 * x * np.minimum(1, t / ataque) * np.exp(-t * 1.6 / d) * np.minimum(1, np.maximum(0, d - t) / 0.3)
def simpatia(x, f, g=0.996):
    """Una cuerda al aire que resuena sola cuando otra suena: un lazo de [f] Hz (con el promedio de dos muestras que le
    apaga los agudos, como a una cuerda) excitado por [x]. Solo le llegan los armónicos de [x] que caen en los suyos. El
    retraso es de P + ½ muestras; como P es mayor que 2, cada tramo de P muestras se calcula de una vez con el anterior."""
    P = max(2, int(round(SR / f - 0.5)))
    y = np.zeros(len(x) + P + 1)
    for s in range(0, len(x), P):
        e = min(s + P, len(x)); i = s + P + 1; m = e - s
        y[i:i + m] = x[s:e] + 0.5 * g * (y[i - P:i - P + m] + y[i - P - 1:i - P - 1 + m])
    return y[P + 1:]
def lira_bardo(f, d=1.0, brillo=0.35, decae=0.9965, al_aire=None, halo=0.18, apagar=None):
    """La lira de los bardos (Diodoro 5.31: «instrumentos parecidos a liras»): grande, de tripa y pulsada con la yema del
    dedo, no con púa (sin el golpe seco del kinnor: solo el roce blando de la yema), cerca de un quinto de la cuerda (le
    faltan el quinto armónico y sus múltiplos: suena hueca). Su caja honda le da un color oscuro (dos resonancias graves y
    anchas) y las cuerdas [al_aire] (frecuencias) resuenan solas por simpatía detrás de cada nota que comparte sus
    armónicos: el [halo]. Ni la lira seca de Helénico ni el kinnor de Arcontes. [apagar]: el segundo en que la palma la calla."""
    x = pluck(f, d, brillo, decae)
    k = max(1, int(round(SR / f * 0.2)))
    x[k:] -= 0.8 * x[:-k]
    t = t_(d)
    x = x + 0.5 * resonador(x, 205, 110) + 0.3 * resonador(x, 455, 190)
    if al_aire:
        h = sum(simpatia(x, fa) for fa in al_aire)
        x = x + halo * h * np.sqrt(np.mean(x ** 2)) / (np.sqrt(np.mean(h ** 2)) + 1e-12)
    yema = filtro(ruido(0.012), 'bajo', 700) * env(int(0.012 * SR), 0.002, 0.004, 4)
    x[:len(yema)] += 0.25 * brillo * yema * np.abs(x).max()
    if apagar is not None: x *= np.where(t < apagar, 1.0, np.exp(-(t - apagar) / 0.035))
    return filtro(x, 'alto', min(70.0, f * 0.5)) * np.minimum(1, np.maximum(0, d - t) / 0.03)
def carnyx(f, d, sube=2.5, crece=0.5, bramido=0.3, brillo=1.0, cae=0.0, suelo=330.0):
    """El carnyx: la trompeta de guerra de bronce, de casi dos metros, que se toca derecha, con la boca en cabeza de jabalí
    por encima de la gente (Diodoro 5.30: trompetas que «al sonar hacen un ruido horrible»; las de Tintignac). Es un tubo
    largo de labio: la nota entra desde abajo ([sube] semitonos) y se asienta; el bronce se abre (más armónicos y la onda
    más empinada) a medida que crece el soplo ([crece]: qué parte de la nota tarda en llegar al todo); la cabeza de jabalí
    le da una resonancia hueca y otra nasal, y el [bramido] es el temblor áspero (unas 27 veces por segundo) que la hace
    rugir. Ni la corneta quieta de Romano ni el olifante de Templario ni el cuerno de Tierras Antiguas. [cae]: semitonos
    que se hunde al final, cuando se acaba el aire. Por debajo de [suelo] Hz se aligera: el altavoz del teléfono no da esa
    fundamental y solo le quitaría volumen a lo que sí se oye (el bramido se oye por sus armónicos)."""
    t = t_(d); n = len(t)
    tr = filtro(rng.standard_normal(n), 'bajo', 6); tr /= np.abs(tr).max() + 1e-9
    caida = cae * np.clip((t - 0.4 * d) / (0.6 * d), 0, 1) ** 1.5
    fb = f * 2 ** ((-sube * np.exp(-t / 0.07) - caida) / 12) * (1 + 0.004 * tr)
    soplo = np.minimum(1, t / 0.035) * (0.5 + 0.5 * np.minimum(1, t / max(crece * d, 1e-3)) ** 1.2)
    x = paso_bajo_movil(sierra_limpia(fb, d), f * (2.2 + 9.0 * brillo * soplo ** 1.5), 1.3)
    x = np.tanh(1.6 * x * (0.5 + soplo)) / np.tanh(2.4)
    boca = x + 0.9 * resonador(x, 720, 180) + 0.5 * resonador(x, 1650, 260)
    ruge = 1 - bramido * soplo * (0.5 + 0.5 * np.sin(2 * np.pi * 27 * t + 2 * np.sin(2 * np.pi * 3.1 * t)))
    aire = 0.05 * filtro(ruido(d), 'banda', (1200, 5000)) * np.exp(-t / 0.05)
    return 0.4 * (filtro(boca, 'alto', max(f * 0.7, suelo)) * ruge * soplo + aire) * np.minimum(1, np.maximum(0, d - t) / 0.1)
VOCALES_GALIA = {   # formantes (centro, ancho, ganancia) de una voz de hombre que habla fuerte, al aire libre
    'a': ((700, 90, 1.0), (1180, 110, 0.6), (2550, 170, 0.25), (3350, 250, 0.1)),
    'o': ((480, 80, 1.0), (820, 100, 0.55), (2500, 160, 0.14)),
    'm': ((260, 60, 1.0), (1100, 160, 0.08), (2300, 220, 0.03)),
}
def voz_hombre(f, d, vocales=('o', 'a'), cambia=0.15, pasos=((0.0, 0),), cae=0.0, esfuerzo=0.5, aliento=0.03, h=0.0, temblor=0.005):
    """Una voz de hombre, sin vibrato (no canta: llama, o murmura con otros): el pulso de la glotis por los formantes de la
    boca, que pasa de la primera vocal a la segunda en [cambia] s. [pasos]: (segundo, semitonos) por donde va la voz, con
    el deslizamiento de una voz real; [cae]: semitonos que se le cae al final, al quedarse sin aire; [esfuerzo]: más fuerte,
    más brillante; [h]: la «h» aspirada con que empieza un grito."""
    t = t_(d); n = len(t)
    sem = np.zeros(n)
    for s, v in pasos: sem[int(s * SR):] = v
    sem = sosfiltfilt(butter(1, 9 / (SR / 2), output='sos'), np.concatenate([np.full(SR // 4, sem[0]), sem]))[SR // 4:]
    tr = filtro(rng.standard_normal(n), 'bajo', 7); tr /= np.abs(tr).max() + 1e-9
    caida = cae * np.clip((t - (d - 0.3)) / 0.3, 0, 1) ** 2
    fb = f * 2 ** ((sem - caida) / 12) * (1 + temblor * tr)
    voz = np.minimum(1, np.maximum(0, t - 0.6 * h * 0.06) / 0.04)
    src = filtro(sierra_limpia(fb, d), 'bajo', 700 + 2600 * esfuerzo, 1) * voz + (aliento + h * np.exp(-t / 0.05)) * filtro(ruido(d), 'banda', (600, 5000))
    def boca(v):
        y = sum(g * resonador(src, F, B) for F, B, g in VOCALES_GALIA[v])
        return y / (np.sqrt(np.mean(y ** 2)) + 1e-12)
    w = 0.5 - 0.5 * np.cos(np.pi * np.minimum(1, t / max(cambia, 1e-3)))
    x = filtro(boca(vocales[0]) * (1 - w) + boca(vocales[-1]) * w, 'alto', 120)
    return 0.08 * x * np.minimum(1, t / 0.03) * np.minimum(1, np.maximum(0, d - t) / 0.15)
def asamblea(f, d, vocal='o', voces=4, esfuerzo=0.3):
    """La asamblea que murmura a la vez: [voces] hombres en la misma nota, ninguno afinado igual ni entrando a la vez,
    que empiezan con la boca cerrada y la abren despacio."""
    partes = []
    for _ in range(voces):
        r = rng.uniform(0, 0.12)
        partes.append((r, voz_hombre(f * 2 ** (rng.uniform(-12, 12) / 1200), max(0.2, d - r), ('m', vocal), rng.uniform(0.3, 0.55),
                                     esfuerzo=esfuerzo, aliento=0.02, temblor=0.007), 1 / np.sqrt(voces)))
    return mezclar(*partes)
def escudo(fuerza=1.0, hierro=0.4):
    """Una lanza que golpea un escudo: la tabla de madera que retumba (tres modos graves de la tabla, cada escudo con los
    suyos), el chasquido de la madera y el umbo de hierro que suena un instante."""
    d = 0.3; e = np.zeros(int(d * SR))
    g = filtro(ruido(0.008), 'banda', (120, 3000)) * env(int(0.008 * SR), 0.0003, 0.003, 4); e[:len(g)] = g
    f1 = rng.uniform(175, 215); f2 = f1 * rng.uniform(2.1, 2.5)
    tabla = resonador(e, f1, 14) + 0.6 * resonador(e, f2, 30) + 0.25 * resonador(e, f2 * 1.9, 60)
    tabla /= np.abs(tabla).max() + 1e-9
    fu = rng.uniform(1100, 1400); t = t_(0.2)
    umbo = sum(a * np.sin(2 * np.pi * fu * r * t) * np.exp(-t * c) for r, a, c in ((1.0, 1.0, 14), (1.59, 0.6, 20), (2.37, 0.35, 30)))
    return fuerza * mezclar((0, tabla, 1.0), (0, clic(0.006, (700, 3200)), 0.5), (0.001, umbo * np.minimum(1, t / 0.0008), 0.3 * hierro))
def choque_armas(d=0.8, golpes=12, fuerza=1.0):
    """La asamblea que aprueba golpeando las armas contra los escudos, cada uno a su tiempo."""
    ts = np.sort(rng.uniform(0, d, golpes)) ** 1.15
    return mezclar(*[(s, escudo(rng.uniform(0.45, 1.0) * fuerza, rng.uniform(0.2, 0.9)), 0.5) for s in ts])
def yunque(f, d=0.8, fuerza=1.0):
    """El yunque de la fragua: el martillo que pega (el golpe seco de hierro contra hierro) y el yunque que canta en [f],
    con sus modos de bloque de hierro (no armónicos: 1, 2.32, 4.25, 6.63). Más [fuerza], más golpe y más brillo."""
    t = t_(d)
    canto = sum(a * np.sin(2 * np.pi * f * r * t + 0.7 * i) * np.exp(-t * c * (1.3 - 0.3 * fuerza))
                for i, (r, a, c) in enumerate(((1.0, 1.0, 2.6), (2.32, 0.5, 4.5), (4.25, 0.28 * fuerza, 8.0), (6.63, 0.14 * fuerza, 13.0))) if f * r < SR * 0.45)
    martillo = filtro(ruido(0.02), 'banda', (400, 1800)) * env(int(0.02 * SR), 0.0005, 0.006, 4)
    return 0.6 * mezclar((0, canto * np.minimum(1, t / 0.0008) * np.minimum(1, np.maximum(0, d - t) / 0.05), 0.5),
                         (0, clic(0.003, (2500, 9000)), 0.6 * fuerza), (0, martillo, 0.3 * fuerza))
def hierro_al_rojo(f):
    """El martillazo sobre el hierro al rojo: un golpe sordo (el hierro caliente se aplasta y no suena) y el yunque, debajo,
    apagado."""
    thud = filtro(ruido(0.07), 'banda', (90, 1200)) * env(int(0.07 * SR), 0.0008, 0.022, 4)
    bajo = seno(95, 0.12) * env(int(0.12 * SR), 0.001, 0.04, 4)
    canto = yunque(f, 0.4, 0.4) * np.exp(-t_(0.4) * 9)
    return mezclar((0, thud, 0.9), (0, bajo, 0.5), (0.001, canto, 0.35))
def fuego(d, chispas=14, llama=0.5):
    """El fuego del hogar que crepita: el rumor grave de la llama, que respira, y chispas sueltas (la madera que estalla)."""
    t = t_(d)
    resp = np.abs(filtro(rng.standard_normal(len(t)), 'bajo', 3)); resp /= resp.max() + 1e-9
    x = llama * filtro(ruido(d), 'bajo', 320) * (0.5 + 0.5 * resp) * 0.4
    for _ in range(int(chispas * d)):
        dc = rng.uniform(0.002, 0.007); k = int(rng.uniform(0, max(0.01, d - dc)) * SR)
        c = filtro(ruido(dc), 'banda', (rng.uniform(900, 2000), rng.uniform(3500, 7500))) * env(int(dc * SR), 0.0007, dc * 0.4, 5)
        x[k:k + len(c)] += c * rng.exponential(0.5)
    return x * np.minimum(1, t / 0.08) * np.minimum(1, np.maximum(0, d - t) / 0.12)
def robles(d, hojas=1.0):
    """El viento que pasa entre los robles: la ráfaga grave en los troncos y las hojas que tiemblan arriba, a sacudidas."""
    t = t_(d); s = np.sin(np.pi * t / d) ** 1.5
    lento = np.abs(filtro(rng.standard_normal(len(t)), 'bajo', 4)); lento /= lento.max() + 1e-9
    tiembla = np.abs(filtro(rng.standard_normal(len(t)), 'bajo', 70)); tiembla /= tiembla.max() + 1e-9
    viento = filtro(ruido(d), 'banda', (180, 900)) * (0.6 + 0.4 * lento)
    arriba = filtro(ruido(d), 'banda', (1800, 7000)) * tiembla ** 1.5
    return (0.6 * viento + 0.5 * hojas * arriba) * s
def resoplido(d=0.45):
    """El caballo que resopla: el aire por los ollares y el aleteo de los labios (unos 19 golpes por segundo), que se apaga."""
    t = t_(d)
    x = filtro(ruido(d), 'banda', (250, 2600)); x = 0.3 * x + 0.7 * resonador(x, 420, 150) + 0.5 * resonador(x, 1100, 250)
    aleteo = 0.45 + 0.55 * np.sin(2 * np.pi * 19 * t * (1 - 0.25 * t / d) + 0.5) ** 2
    return x * aleteo * np.minimum(1, t / 0.02) * np.exp(-t / (0.35 * d)) * np.minimum(1, np.maximum(0, d - t) / 0.05)
def lejos(x, k=1.0):
    """Lo mismo, oído a [k] campos de distancia: sin los agudos y con más aire alrededor."""
    return reverb(filtro(x, 'bajo', 3800 / (1 + k)), 1.1 + 0.3 * k, min(0.6, 0.25 + 0.15 * k), 2600)
def piano_salon(f, d=1.2, fuerza=0.6, apagar=None):
    """El piano de salón de 1900: el macillo de fieltro golpea tres cuerdas (dos en el grave medio, una en el bajo) que
    nunca están afinadas igual, así que la nota late despacio. Cada cuerda da sus parciales un poco más altos que los
    armónicos (la rigidez del acero: f·k·√(1 + B·k²), con B que crece hacia el agudo) y le faltan los que caen en el
    octavo de la cuerda, donde pega el macillo. Cada parcial se apaga en dos tiempos (el golpe y el sonido que queda),
    los agudos antes que los graves; con más [fuerza], más brillo. Debajo, el golpe del macillo y la tabla armónica que
    responde. [apagar]: el segundo en que el apagador vuelve a la cuerda (sin él, la nota sigue con el pedal). Ni la
    cuerda pulsada del piano «stride» de Art déco ni el laúd: aquí hay macillo, cuerdas que laten y apagador."""
    t = t_(d); n = len(t)
    B = max(1.5e-4, 3.5e-4 * (f / 261.6) ** 0.9)
    cuerdas = (0.0,) if f < 110 else (-0.7, 0.8) if f < 220 else (-0.9, 0.15, 1.0)
    base = 1.7 * (261.6 / f) ** 0.5
    corte = 1100 + 3800 * fuerza * (f / 261.6) ** 0.25
    x = np.zeros(n)
    for k in range(1, 41):
        fk = f * k * np.sqrt(1 + B * k * k)
        if fk > min(SR * 0.45, 9500): break
        a = abs(np.sin(np.pi * k / 8 + 0.04)) / k ** (1.15 - 0.45 * fuerza) / (1 + (fk / corte) ** 2)
        if a < 1e-4: continue
        t1 = min(4.0, max(0.12, base / (1 + fk / 1500)))
        caida = 0.72 * np.exp(-t / t1) + 0.28 * np.exp(-t / (4.5 * t1))
        for c in cuerdas:
            x += a * caida * np.sin(2 * np.pi * fk * 2 ** (c * (1 + 0.15 * k / 10) / 1200) * t + rng.uniform(-0.15, 0.15)) / len(cuerdas)
    x *= np.minimum(1, t / 0.0012)
    macillo = filtro(ruido(0.012), 'bajo', 900 + 1800 * fuerza) * env(int(0.012 * SR), 0.0005, 0.004, 4)
    golpe = np.zeros(int(0.12 * SR)); golpe[:len(macillo)] = macillo
    tabla = 0.6 * resonador(golpe, 140, 60) + 0.4 * resonador(golpe, 330, 110) + 0.15 * golpe
    x = mezclar((0, x, 1.0), (0, tabla / (np.abs(tabla).max() + 1e-9), 0.05 + 0.08 * fuerza))[:n]
    if apagar is not None: x *= np.where(t < apagar, 1.0, np.exp(-(t - apagar) / 0.07))
    # Al final, el pedal que se levanta despacio: la nota se apaga en una curva suave, no de golpe.
    return 0.35 * (0.45 + 0.55 * fuerza) * x * np.sin(0.5 * np.pi * np.minimum(1, np.maximum(0, d - t) / min(0.6, 0.35 * d))) ** 2
def peine(f, d=1.1, fuerza=1.0, cae=0.0):
    """La cajita de música de cilindro: una púa del cilindro levanta una lámina del peine de acero y la suelta. La lámina
    es una viga empotrada en un extremo, así que sus parciales no son armónicos (1, 6.27, 17.55): suena a metal fino,
    no a campana. Cada nota tiene dos láminas casi iguales que laten muy despacio, la púa da un tic al soltarla y la
    caja de madera le da cuerpo. Ni la cajita de Navidad (de parciales armónicos) ni la celesta. [cae]: semitonos que
    la nota se hunde al sonar, como cuando a la cajita se le acaba la cuerda."""
    t = t_(d)
    tau = min(2.2, max(0.35, 1.3 * (523.0 / f) ** 0.6))
    fb = f * 2 ** (-cae * np.minimum(1, t / max(d, 1e-3)) ** 0.7 / 12)
    fase = 2 * np.pi * np.cumsum(fb) / SR
    x = np.zeros(len(t))
    for r, a, c in ((1.0, 1.0, 1.0 / tau), (6.267, 0.2 * fuerza, 9.0), (17.55, 0.06 * fuerza, 26.0)):
        if f * r >= SR * 0.45: continue
        x += a * (0.75 * np.sin(r * fase) + 0.25 * np.sin(r * fase * 1.0018 + 0.6)) * np.exp(-t * c)
    x *= np.minimum(1, t / 0.0015)
    x = x + 0.35 * resonador(x, 480, 160) + 0.2 * resonador(x, 1250, 260)
    pua = clic(0.003, (3500, 9500))
    return 0.4 * mezclar((0, x * np.minimum(1, np.maximum(0, d - t) / min(0.3, 0.3 * d)) ** 2, 1.0), (0, pua, 0.18 * fuerza))
def cristal(f, d=1.6, fuerza=0.7):
    """Una campanilla de cristal (un colgante de la lámpara, una copa fina): el vidrio suena casi puro y largo; sus
    modos de copa (aproximados: 1, 2.61, 4.85) son débiles y cada uno es doble, porque ningún cristal es redondo del todo, así que
    la nota tiembla despacio (unas dos veces por segundo). Al empezar, el «tin» del golpe, muy agudo y muy breve. Ni
    el crótalo de bronce de Arcontes (parciales fuertes y choque de discos) ni las campanas de Navidad."""
    t = t_(d)
    tau = min(2.4, max(0.5, 1.4 * (900.0 / f) ** 0.5))
    x = np.zeros(len(t))
    for r, a, c, sep in ((1.0, 1.0, 1.0 / tau, 1.9), (2.61, 0.14 * (0.5 + fuerza), 3.2, 3.1), (4.85, 0.05 * (0.4 + fuerza), 7.0, 4.3)):
        fr = f * r
        if fr >= SR * 0.45: continue
        x += a * (0.55 * np.sin(2 * np.pi * fr * t) + 0.45 * np.sin(2 * np.pi * (fr + sep) * t + 0.9)) * np.exp(-t * c)
    if f * 7.9 < SR * 0.45: x += 0.08 * fuerza * np.sin(2 * np.pi * f * 7.9 * t) * np.exp(-t * 90)
    x *= np.minimum(1, t / 0.0008)
    tin = filtro(clic(0.0025, (5500, 10500)), 'alto', 5000)
    return 0.42 * mezclar((0, x * np.minimum(1, np.maximum(0, d - t) / min(0.35, 0.3 * d)) ** 2, 1.0), (0, tin, 0.25 * fuerza))
def prensa(fuerza=1.0, rueda=0.22):
    """La prensa de litografía a mano: el carro con la piedra rueda bajo el «râteau» (la regla de madera forrada de cuero
    que aprieta el pliego), con el roce del cuero en el tímpano, y al final el golpe sordo del carro contra el tope y el
    bastidor de hierro que retumba apenas. [rueda]: lo que tarda el carro en pasar (0 para el golpe solo)."""
    partes = []
    if rueda > 0:
        t = t_(rueda); s = np.minimum(1, t / (0.4 * rueda)) ** 1.5
        rumor = filtro(ruido(rueda), 'banda', (140, 900)) * (0.6 + 0.4 * np.abs(filtro(ruido(rueda), 'bajo', 40)))
        cuero = filtro(ruido(rueda), 'banda', (900, 3200)) * 0.35
        partes.append((0, (rumor + cuero) * s, 0.5 * fuerza))
    e = np.zeros(int(0.35 * SR))
    g = filtro(ruido(0.02), 'bajo', 700) * env(int(0.02 * SR), 0.0008, 0.007, 4); e[:len(g)] = g
    golpe = resonador(e, 95, 30) + 0.8 * resonador(e, 210, 55) + 0.45 * resonador(e, 470, 120)
    golpe = filtro(golpe / (np.abs(golpe).max() + 1e-9), 'alto', 120)
    partes += [(rueda, golpe, 0.9 * fuerza), (rueda, filtro(clic(0.006, (400, 2200)), 'bajo', 2400), 0.35 * fuerza)]
    return mezclar(*partes)
def pliego(d=0.3, tinta=1.0):
    """El pliego que se despega de la piedra entintada: el papel que roza y, a medida que se levanta, la tinta grasa
    que suelta el papel en chasquidos diminutos, cada vez más ralos."""
    t = t_(d); n = len(t)
    roce = filtro(ruido(d), 'banda', (700, 5200)) * np.sin(np.pi * t / d) ** 1.2 * (0.6 + 0.4 * np.abs(filtro(ruido(d), 'bajo', 25)))
    x = 0.55 * roce
    for _ in range(int(40 * d * tinta)):
        u = rng.uniform(0, 1) ** 1.6; k = int(u * (n - 200))
        c = clic(0.0015, (1800, 8000)) * rng.uniform(0.3, 1.0) * (1 - 0.6 * u)
        x[k:k + len(c)] += c
    return x * np.minimum(1, np.maximum(0, d - t) / 0.02)
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

def urbe():
    # Si bemol mayor: la Roma de hoy. Mandolina en trémolo, la campanilla del tranvía, el claxon de dos tonos de la
    # motoneta y, en lo solemne, las campanas de las iglesias del centro que no tocan nunca a la vez.
    T = 1    # Si bemol 4
    pua = lambda n, d=0.22: pluck(hz(n), d, 0.58, 0.994)
    mandolina = lambda n, d=0.5, veces=5: mezclar(*[(0.055 * i, pua(n, 0.2 if i < veces - 1 else d), 0.55 * (0.85 ** i)) for i in range(veces)])
    tranvia = lambda n, d=0.5: campana(hz(n), d, 3.01, 1.3, 5.0)
    claxon = lambda n, d: filtro(barrido(hz(n), hz(n), d, 'cuadrada') + 0.5 * barrido(hz(n + 4), hz(n + 4), d, 'cuadrada'), 'bajo', 2200) * env(int(d * SR), 0.008, d * 0.9, 1.5)
    campanas = lambda n, d=2.4: campana(hz(n), d, 2.4, 1.8, 1.7)
    # Con su propio azar también los sonidos de siempre: un tema nuevo no mueve el ruido de los que van después.
    with azar_propio('urbe_base'):
        S['urbe'] = dict(
            toque=mezclar((0, pua(T + 12, 0.1), 0.9), (0, clic(0.006, (2500, 7000)), 0.25)), elegir=tranvia(T + 19, 0.35),
            pasar=mezclar((0, papel(0.14, (1100, 5200)), 0.9), (0.05, pua(T + 7, 0.08), 0.15)),
            abrir=mezclar(*[(0.09 * i, mandolina(T + n, 0.35, 3), 0.5) for i, n in enumerate([0, 4, 7])]),
            guardar=mezclar((0, tranvia(T + 12, 0.35), 0.55), (0.15, tranvia(T + 12, 0.6), 0.6)),
            logro=reverb(mezclar(*[(0.22 * i, mandolina(T + n, 0.3 if i < 3 else 0.8, 4 if i < 3 else 8), 0.55) for i, n in enumerate([0, 4, 7, 12])],
                                 (0.66, tranvia(T + 24, 0.9), 0.3), (0.66, pua(T - 12, 0.8), 0.4)), 1.1, 0.22),
            tema=reverb(mezclar((0, claxon(T + 7, 0.11), 0.4), (0.14, claxon(T + 4, 0.16), 0.4), (0.38, mandolina(T + 12, 0.6, 7), 0.6)), 0.9, 0.2),
            aviso=reverb(mezclar((0, tranvia(T + 19, 0.4), 0.5), (0.16, tranvia(T + 19, 0.7), 0.5),
                                 *[(0.5 + 0.2 * i, mandolina(T + n, 0.25 if i < 3 else 0.7, 3 if i < 3 else 7), 0.45) for i, n in enumerate([12, 11, 9, 7])]), 1.2, 0.25),
            solemne=reverb(mezclar(*[(0.75 * i, acorde(lambda f, d: organo(f, d, 0.1), [T - 12 + a for a in ch], 0.8), 0.4) for i, ch in enumerate([[0, 4, 7], [-3, 0, 4], [5, 9, 12], [0, 4, 7, 12]])],
                                   *[(0.37 * k + 0.05 * (k % 3), campanas([T - 12, T - 5, T, T + 4, T - 8, T + 7][k % 6], 2.2), 0.28) for k in range(8)]), 2.4, 0.33))

    # Error: el claxon que se ahoga medio tono abajo. Cambio: la campanilla del tranvía, de la tónica a la quinta.
    # Tecla: el bolígrafo en el cuaderno a rayas. Rascar: la moneda de un euro, bimetálica, sobre la lámina.
    with azar_propio('urbe'):
        nuevos('urbe',
               error=mezclar((0, claxon(T + 7, 0.1), 0.45), (0.13, claxon(T + 6, 0.32), 0.45)),
               cambio=mezclar((0, tranvia(T + 12, 0.25), 0.5), (0.07, tranvia(T + 19, 0.3), 0.5)),
               tecla=mezclar((0, trazo((2200, 6500), 0.035, 14, 0.02), 1), (0, filtro(ruido(0.01), 'bajo', 1200) * env(int(0.01 * SR), 0.001, 0.005), 0.25)),
               rascar=rascado((1700, 7600), campana(hz(T + 28), 0.5, 2.41, 1.1, 6), aspereza=110))

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

def vapor():
    # La menor con la sexta: el silbato de tres campanas de las locomotoras (La, Do y Fa), el «chu» del escape de la
    # chimenea, la picadora del revisor, el tintineo del latón y, en lo solemne, el armonio de una estación con eco.
    T = 0    # La4
    def silbato(notas, d, aire=0.35):
        t = t_(d)
        x = sum(np.sin(2 * np.pi * hz(n) * t) + 0.3 * np.sin(2 * np.pi * hz(n) * 2 * t) for n in notas) / len(notas)
        soplo = filtro(ruido(d), 'banda', (hz(notas[0]) * 0.9, hz(notas[-1]) * 3.2)) * aire
        return (x + soplo) * np.minimum(1, t / 0.04) * np.minimum(1, (d - t) / 0.08)
    def chu(d=0.16, brillo=1800):
        return filtro(ruido(d), 'banda', (180, brillo)) * env(int(d * SR), 0.004, d * 0.5, 3)
    tink = lambda n, d=0.2: mezclar((0, clic(0.008, (1800, 6000)), 0.45), (0, campana(hz(n), d, 3.0, 1.0, 14), 0.6))
    armonio = lambda f, d: organo(f, d, 0.12)
    with azar_propio('vapor_base'):
        S['vapor'] = dict(
            toque=tink(T + 12, 0.14),
            elegir=silbato([T + 19], 0.12, 0.25),
            pasar=mezclar((0, chu(0.12), 0.8), (0.1, chu(0.08, 1400), 0.4)),
            abrir=mezclar((0, filtro(ruido(0.38), 'alto', 2200) * env(int(0.38 * SR), 0.12, 0.25, 2), 0.45), (0.3, tambor(hz(T - 24), 0.3, 0.5), 0.8),
                          (0.3, tink(T + 7, 0.4), 0.4)),
            guardar=mezclar((0, clic(0.012, (900, 3500)), 0.9), (0.01, tambor(hz(T - 24), 0.2, 0.4), 0.6), (0.09, tink(T + 12, 0.5), 0.6)),
            logro=reverb(mezclar(*[(s, chu(0.16), 0.75) for s in (0.0, 0.36, 0.64, 0.86, 1.02)],
                                 (1.12, silbato([T, T + 3, T + 8], 0.9), 0.6)), 1.3, 0.22),
            tema=reverb(mezclar((0, chu(0.14), 0.6), (0.18, silbato([T, T + 3, T + 8], 0.55), 0.6)), 1.2, 0.24),
            aviso=reverb(mezclar((0, silbato([T, T + 3, T + 8], 0.18), 0.55), (0.28, silbato([T, T + 3, T + 8], 0.6), 0.55),
                                 (0.95, campana(hz(T + 12), 1.2, 2.76, 1.4, 2.5), 0.35)), 1.4, 0.26),
            solemne=reverb(mezclar(*[(0.85 * i, acorde(armonio, [T - 12 + a for a in ch], 0.8), 0.45) for i, ch in enumerate([[0, 3, 7], [-4, 0, 3], [-7, -3, 0], [0, 3, 8, 12]])],
                                   (2.55, silbato([T, T + 3, T + 8], 1.0, 0.25), 0.45),
                                   *[(0.85 * i, chu(0.2, 1200), 0.35) for i in range(3)]), 2.6, 0.34))

    # Error: el silbato que se ahoga medio tono abajo y el vapor que escapa. Cambio: la palanca del regulador y la
    # válvula que tintinea a la quinta. Tecla: el manipulador del telégrafo (golpe y rebote). Rascar: latón de caldera.
    with azar_propio('vapor'):
        nuevos('vapor',
               error=mezclar((0, silbato([T + 7], 0.12), 0.45), (0.13, silbato([T + 6], 0.3), 0.45), (0.13, filtro(ruido(0.35), 'alto', 2500) * env(int(0.35 * SR), 0.02, 0.2), 0.25)),
               cambio=mezclar((0, tambor(hz(T - 19), 0.08, 0.5), 0.6), (0.07, tink(T + 19, 0.25), 0.6)),
               tecla=mezclar((0, clic(0.01, (900, 3200)), 1), (0.035, clic(0.008, (1500, 4500)), 0.55), (0, tambor(hz(T - 12), 0.03, 0.2), 0.2)),
               rascar=rascado((1500, 6800), campana(hz(T + 26), 0.6, 3.0, 1.2, 5), aspereza=120))

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

def deco():
    # Do mayor con sexta y novena: una orquesta de jazz de los años veinte. El piano «stride», el vibráfono, la trompeta
    # con sordina, la escobilla sobre el platillo y el contrabajo pulsado; en lo solemne, el órgano de un cine de estreno.
    T = 3    # Do5
    piano = lambda n, d=0.4: mezclar((0, pluck(hz(n), d, 0.7, 0.995), 0.8), (0, pluck(hz(n + 12), d * 0.6, 0.5, 0.993), 0.25))
    vibra = lambda n, d=0.6: vibrafono(hz(n), d)
    trompeta = lambda n, d: filtro(metal(hz(n), d, 2200, 0.02), 'bajo', 2600)
    escobilla = lambda d=0.12: filtro(ruido(d), 'banda', (2500, 8000)) * env(int(d * SR), 0.01, d * 0.8, 2)
    platillo = lambda d=0.6: filtro(ruido(d), 'alto', 6000) * env(int(d * SR), 0.002, d, 4)
    contrabajo = lambda n, d=0.4: pluck(hz(n), d, 0.3, 0.996)
    # Con su propio azar: un tema nuevo no mueve el ruido de los que van después.
    with azar_propio('deco_base'):
        S['deco'] = dict(
            toque=mezclar((0, piano(T + 12, 0.12), 0.8), (0, escobilla(0.05), 0.2)), elegir=vibra(T + 16, 0.35),
            pasar=mezclar((0, escobilla(0.16), 0.8), (0.04, piano(T + 19, 0.07), 0.2)),
            abrir=mezclar(*[(0.06 * i, piano(T + n, 0.3), 0.45) for i, n in enumerate([0, 4, 7, 9])]),
            guardar=mezclar((0, piano(T + 7, 0.15), 0.5), (0.12, piano(T + 12, 0.45), 0.55), (0.12, piano(T + 16, 0.45), 0.4)),
            logro=reverb(mezclar(*[(0.12 * i, trompeta(T + n, 0.12 if i < 4 else 0.7), 0.5) for i, n in enumerate([0, 4, 7, 9, 14])],
                                 (0.48, platillo(0.9), 0.35), (0.48, contrabajo(T - 24, 0.8), 0.5)), 1.2, 0.22),
            tema=reverb(mezclar(*[(0.07 * i, piano(T + n, 0.5), 0.4) for i, n in enumerate([-12, -5, 0, 4, 9, 14])], (0.42, vibra(T + 21, 0.8), 0.35)), 1.0, 0.22),
            aviso=reverb(mezclar((0, vibra(T + 16, 0.4), 0.5), (0.15, vibra(T + 19, 0.4), 0.5), (0.3, vibra(T + 21, 0.7), 0.5), (0.3, escobilla(0.2), 0.3)), 1.1, 0.25),
            solemne=reverb(mezclar(*[(0.9 * i, acorde(lambda f, d: organo(f, d, 0.08), [T - 12 + a for a in ch], 0.9), 0.4) for i, ch in enumerate([[0, 4, 9, 14], [2, 5, 9, 12], [-5, -1, 2, 5], [0, 4, 7, 9, 14]])],
                                   *[(0.9 * i, contrabajo(T - 24 + b, 0.8), 0.45) for i, b in enumerate([0, 2, -5, 0])],
                                   (2.7, platillo(1.4), 0.3), (2.7, trompeta(T + 16, 1.2), 0.35)), 2.0, 0.3))

    # Error: la trompeta con sordina que baja medio tono, el «uá-uá» de los cómicos. Cambio: dos notas de piano, sexta y
    # novena. Tecla: la estilográfica sobre el papel satinado. Rascar: la laca con pan de oro, lisa y dura.
    with azar_propio('deco'):
        nuevos('deco',
               error=mezclar((0, trompeta(T + 4, 0.12), 0.45), (0.12, trompeta(T + 3, 0.35), 0.45)),
               cambio=mezclar((0, piano(T + 9, 0.2), 0.5), (0.08, piano(T + 14, 0.3), 0.5)),
               tecla=mezclar((0, trazo((2500, 7500), 0.03, 16, 0.015), 1), (0, clic(0.006, (3000, 8000)), 0.2)),
               rascar=rascado((1800, 7000), campana(hz(T + 31), 0.4, 2.76, 1.2, 6), aspereza=120))

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

def edo():
    # Mi hirajōshi (Mi, Fa#, Sol, Si, Do): koto, shamisen, las tablillas de madera (hyōshigi), la campana del templo
    # (bonshō), el shakuhachi y la campanita de viento (fūrin). La escala tiene semitonos (Fa#–Sol, Si–Do): suena japonesa
    # y no a la pentatónica sin semitonos del tema de la seda.
    T = -5   # Mi4
    hira = [0, 2, 3, 7, 8, 12]
    koto = lambda n, d=1.0: mezclar((0, pluck(hz(n), d, 0.8, 0.994), 1.0), (0, clic(0.006, (3000, 8000)), 0.25))
    shami = lambda n, d=0.45: mezclar((0, pluck(hz(n), d, 0.95, 0.985), 0.9),
                                      (0, filtro(ruido(d), 'banda', (hz(n) * 4, hz(n) * 9)) * env(int(d * SR), 0.002, d * 0.3, 4), 0.25))
    hyoshigi = lambda d=0.09: mezclar((0, filtro(ruido(d), 'banda', (1200, 5000)) * env(int(d * SR), 0.0005, 0.015, 6), 0.7),
                                      (0, seno(hz(T + 31), d) * env(int(d * SR), 0.0005, 0.03, 5), 0.5))
    bonsho = lambda d=3.0: reverb(mezclar((0, campana(hz(T - 24), d, 2.76, 1.2, 1.2), 0.6), (0, campana(hz(T - 24) * 1.007, d, 2.76, 1.0, 1.2), 0.5)), 1.4, 0.3)
    shaku = lambda f, d: flauta(f, d) + 0.12 * filtro(ruido(d), 'banda', (f, f * 4)) * np.minimum(1, t_(d) / 0.05)
    furin = lambda n=T + 36, d=0.7: campana(hz(n), d, 3.9, 0.8, 5)
    # Con su propio azar también los sonidos de siempre: un tema nuevo no mueve el ruido de los que van después.
    with azar_propio('edo_base'):
        S['edo'] = dict(
            toque=mezclar((0, clic(0.008, (2500, 7000)), 0.6), (0, pluck(hz(T + 24), 0.12, 0.8, 0.99), 0.5)),
            elegir=koto(T + 19, 0.4), pasar=papel(0.22, (600, 3000)),
            abrir=mezclar((0, hyoshigi(), 0.8), (0.16, hyoshigi(), 0.9)),
            guardar=mezclar((0, koto(T + 7, 0.8), 0.55), (0.1, koto(T + 12, 0.9), 0.5)),
            logro=reverb(mezclar(melodia(lambda f, d: pluck(f, d, 0.8, 0.994), [T + p for p in hira], 0.07, 0.9), (0.45, furin(), 0.3)), 1.2, 0.25),
            tema=reverb(mezclar((0, shami(T + 12, 0.35), 0.6), (0.14, shami(T + 19, 0.35), 0.55), (0.28, koto(T + 24, 0.9), 0.5)), 1.0, 0.22),
            aviso=reverb(mezclar(melodia(shaku, [T + 12, T + 8, T + 7, None, T + 3, T + 2, T], 0.22, 0.4, g=0.5), (0, hyoshigi(), 0.5), (0.12, hyoshigi(), 0.6)), 1.4, 0.3),
            solemne=reverb(mezclar((0, bonsho(4.0), 0.6),
                                   (1.0, melodia(lambda f, d: pluck(f, d, 0.8, 0.994), [T + n for n in [0, 2, 3, 7, 8, 7, 3, 2, 0]], 0.45, 1.2, g=0.4), 0.9),
                                   (1.0, shaku(hz(T - 5), 4.2), 0.12)), 1.6, 0.22))

    # Error: el shamisen que cae medio tono y la cuerda que se afloja. Cambio: las tablillas y la campanita de viento.
    # Tecla: el pincel sobre el washi, más blando que el de la seda. Rascar: la moneda de cobre con su agujero cuadrado.
    with azar_propio('edo'):
        nuevos('edo',
               error=mezclar((0, shami(T + 8, 0.14), 0.5), (0.14, shami(T + 7, 0.4), 0.5),
                             (0.14, filtro(barrido(hz(T + 7), hz(T + 3), 0.35), 'bajo', 2000) * env(int(0.35 * SR), 0.003, 0.2, 3), 0.25)),
               cambio=mezclar((0, hyoshigi(0.06), 0.6), (0.09, furin(T + 31, 0.4), 0.35)),
               tecla=trazo((700, 3600), 0.06, 8, 0.035),
               rascar=rascado((1100, 5200), furin(T + 36, 0.5), aspereza=70))

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

def arcano():
    # Re frigio (la segunda menor, Mi bemol) con el tritono (La bemol) que no resuelve: la sirena de niebla del faro, la
    # boya con su campana desafinada, las olas contra el muelle, la armónica de cristal y la madera que cruje en el archivo.
    T = -7   # Re4
    def sirena(n, d):
        """La sirena de niebla: dos sierras graves que bajan apenas al final, muy filtradas, que entran y salen despacio."""
        t = t_(d)
        x = barrido(hz(n), hz(n) * 0.985, d, 'sierra') * 0.6 + barrido(hz(n) * 1.004, hz(n) * 0.988, d, 'sierra') * 0.5
        return filtro(x, 'bajo', 520) * np.minimum(1, t / 0.25) * np.minimum(1, np.maximum(0, d - t) / 0.4)
    def cristal(f, d=0.6):
        """Armónica de cristal: seno con su octava, ataque lento de dedo mojado y un temblor que no se va."""
        t = t_(d)
        x = np.sin(2 * np.pi * f * t) + 0.3 * np.sin(2 * np.pi * f * 2 * t) + 0.08 * np.sin(2 * np.pi * f * 3.02 * t)
        return x * np.minimum(1, t / 0.06) * np.exp(-t * 2.2) * (1 + 0.15 * np.sin(2 * np.pi * 6.3 * t)) * np.minimum(1, np.maximum(0, d - t) / 0.05)
    boya = lambda n, d=1.6: campana(hz(n), d, 3.17, 1.4, 2.2)
    def ola(d):
        """Una ola que sube y se retira por la grava."""
        t = t_(d); s = np.sin(np.pi * t / d)
        return filtro(ruido(d), 'bajo', 900) * s ** 2 + 0.25 * filtro(ruido(d), 'banda', (2000, 6000)) * s ** 6
    def crujido(d=0.35, k=9):
        """La madera del cajón o de la tarima: chasquidos graves, cada vez más juntos."""
        return mezclar(*[(d * (1 - (1 - i / k) ** 1.6), clic(0.018, (300, 1600)), 0.5 + 0.05 * (i % 3)) for i in range(k)])
    with azar_propio('arcano_base'):
        S['arcano'] = dict(
            toque=mezclar((0, clic(0.012, (900, 3200)), 0.5), (0, cristal(hz(T + 12), 0.12), 0.5)),
            elegir=cristal(hz(T + 13), 0.35),
            pasar=mezclar((0, papel(0.18, (600, 3800)), 0.9), (0.04, cristal(hz(T + 7), 0.15), 0.2)),
            abrir=reverb(mezclar((0, crujido(0.35), 0.8), (0.3, boya(T - 5, 1.2), 0.35)), 1.4, 0.3),
            guardar=mezclar((0, tambor(hz(T - 24), 0.25, 0.6), 0.7), (0.06, cristal(hz(T + 7), 0.5), 0.4), (0.2, cristal(hz(T + 12), 0.6), 0.4)),
            logro=reverb(mezclar((0, sirena(T - 24, 1.4), 0.5), (0.2, melodia(cristal, [T, T + 1, T + 5, T + 7, T + 8, T + 12], 0.16, 0.7, g=0.45), 1),
                                 (1.1, boya(T + 12, 1.8), 0.3)), 2.0, 0.35),
            tema=reverb(mezclar((0, ola(0.9), 0.5), (0.2, melodia(cristal, [T + 12, T + 13, T + 7], 0.14, 0.6, g=0.5), 1)), 1.6, 0.3),
            aviso=reverb(mezclar((0, sirena(T - 19, 1.1), 0.6), (1.4, sirena(T - 19, 1.6), 0.6), (0.3, boya(T + 5, 2.0), 0.3), (0, ola(3.2), 0.4)), 2.2, 0.35),
            solemne=reverb(mezclar(*[(1.3 * i, acorde(cristal, [T - 12 + a for a in ch], 1.6), 0.6)
                                     for i, ch in enumerate([[0, 3, 7], [1, 5, 8], [-2, 2, 5], [-5, -1, 2, 6], [0, 3, 7, 12]])],
                                   (0, ola(3.0), 0.35), (3.0, ola(3.4), 0.35), (2.6, sirena(T - 24, 2.4), 0.35), (5.2, boya(T, 2.0), 0.3)), 2.6, 0.4))

    # Error: el cristal que cae un tritono y la sirena que se ahoga. Cambio: el interruptor de la linterna y la boya que
    # sube a la cuarta. Tecla: la plumilla de acero del investigador, rápida y seca. Rascar: un botón de latón de uniforme
    # que el mar dejó verde.
    with azar_propio('arcano'):
        nuevos('arcano',
               error=mezclar((0, cristal(hz(T + 6), 0.18), 0.5), (0.14, cristal(hz(T), 0.45), 0.5), (0.12, sirena(T - 24, 0.4), 0.3)),
               cambio=mezclar((0, clic(0.014, (1200, 4200)), 0.6), (0.03, boya(T + 12, 0.5), 0.35), (0.12, boya(T + 17, 0.6), 0.35)),
               tecla=mezclar((0, trazo((2600, 8000), 0.035, 24, 0.02), 1), (0, clic(0.006, (4000, 9000)), 0.3)),
               rascar=rascado((800, 4200), boya(T + 19, 0.9), aspereza=55, pasadas=((0.0, 0.22), (0.3, 0.26), (0.62, 0.3))))

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

def meso():
    # Sol pentatónico de flauta de barro (sin semitonos; no se sabe cómo afinaban: la escala es propia), el tunkul (el
    # tambor de tronco hendido, con sus dos lengüetas de madera), la sonaja de semillas, el caparazón de tortuga golpeado
    # con un asta de venado y la caracola de mar que se sopla como trompeta.
    T = -2   # Sol4
    tunkul = lambda n, d=0.45: mezclar((0, marimba(hz(T + n - 12), d), 0.9), (0, tambor(hz(T + n - 24), d * 0.5, 0.08), 0.35))
    def sonaja(d=0.18, k=5):
        """La sonaja: semillas secas dentro de un guaje, unas sacudidas cortas y ásperas."""
        return mezclar(*[(i * d / k, filtro(ruido(0.05), 'banda', (2200, 6500)) * env(int(0.05 * SR), 0.001, 0.025, 5), 0.7 - i * 0.07) for i in range(k)])
    tortuga = lambda: mezclar((0, clic(0.03, (900, 3200)), 0.8), (0, tambor(hz(T - 7), 0.12, 0.3), 0.5))
    caracola = lambda n, d: metal(hz(T + n - 12), d, 1500, 0.09)
    with azar_propio('meso_base'):
        S['meso'] = dict(
            toque=tunkul(0, 0.3), elegir=tortuga(), pasar=sonaja(0.16, 4),
            abrir=mezclar((0, sonaja(0.1, 3), 0.5), (0.04, melodia(flauta, [T + 12, T + 14, T + 17, T + 19], 0.07, 0.25, g=0.5), 0.8)),
            guardar=mezclar((0, tunkul(-5, 0.5), 0.8), (0.16, tunkul(0, 0.6), 0.7), (0.16, tortuga(), 0.35)),
            logro=reverb(mezclar((0, caracola(0, 0.9), 0.55), (0.3, melodia(flauta, [T + 12, T + 14, T + 17, T + 19, T + 21, T + 24], 0.12, 0.3, g=0.5), 0.8),
                                 *[(0.12 * i, tunkul([0, 2, 5, 7, 9, 12][i], 0.4), 0.4) for i in range(6)], (0.75, sonaja(0.3, 6), 0.4)), 1.3, 0.25),
            tema=reverb(mezclar((0, sonaja(0.18, 4), 0.5), (0.1, melodia(flauta, [T + 17, T + 19, T + 24], 0.13, 0.3, g=0.45), 1), (0.1, tunkul(0, 0.6), 0.5)), 1.1, 0.25),
            aviso=reverb(mezclar((0, caracola(0, 1.1), 0.5), (0.9, melodia(flauta, [T + 24, T + 21, T + 19, T + 17, T + 19], 0.2, 0.25), 0.8),
                                 (0.9, tunkul(-5, 0.5), 0.4), (1.3, tunkul(0, 0.5), 0.4)), 1.4, 0.28),
            solemne=reverb(mezclar((0, caracola(-5, 1.6), 0.55), (1.2, caracola(0, 1.6), 0.5),
                                   (2.4, melodia(flauta, [T + 12, T + 14, T + 17, T + 14, T + 12, T + 9, T + 12, None, T + 7, T + 12], 0.42, 0.4, g=0.35), 1),
                                   *[(2.4 + 0.84 * i, tunkul(-5 if i % 2 == 0 else 0, 0.6), 0.45) for i in range(5)],
                                   *[(2.82 + 0.84 * i, sonaja(0.12, 3), 0.3) for i in range(4)]), 2.0, 0.3))

    # Error: el tunkul que baja y la tortuga sorda. Cambio: una sacudida de la sonaja y la lengüeta aguda del tunkul.
    # Tecla: el pincel de pelo sobre el estuco. Rascar: el jade pulido, con su eco de piedra.
    with azar_propio('meso'):
        nuevos('meso',
               error=mezclar((0, tunkul(0, 0.2), 0.5), (0.12, tunkul(-5, 0.45), 0.55), (0.12, tortuga(), 0.3)),
               cambio=mezclar((0, sonaja(0.06, 2), 0.6), (0.06, tunkul(7, 0.25), 0.5)),
               tecla=trazo((900, 3600), 0.05, 6, 0.032),
               rascar=rascado((1800, 7200), marimba(hz(T + 29), 0.5), aspereza=60))

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

def cueva():
    # Mi pentatónico menor, en cuartas y quintas: la Prehistoria. Una flauta de hueso con mucho soplo, un litófono (lajas de
    # piedra que suenan al golpearlas), un tambor de piel, piedras que chocan, la bramadera que zumba al girar y el eco largo
    # de la cueva. No se sabe cómo se afinaban las flautas del Paleolítico: la escala es una elección, no una reconstrucción.
    T = -5   # Mi4
    def hueso_f(f, d):
        return flauta(f, d) * 0.85 + 0.25 * filtro(ruido(d), 'banda', (f * 1.5, f * 4)) * env(int(d * SR), 0.03, d * 0.7, 2)
    hueso = lambda n, d: hueso_f(hz(n), d)
    laja = lambda n, d=0.5: campana(hz(n), d, 2.41, 0.8, 9.0)
    piel = lambda d=0.35: tambor(hz(T - 24), d, 0.35)
    def piedras():
        return mezclar((0, filtro(ruido(0.035), 'banda', (900, 3800)) * env(int(0.035 * SR), 0.0005, 0.02, 6), 1.0), (0.002, tambor(hz(T - 12), 0.06, 0.4), 0.35))
    def bramadera(d, f0=180, f1=420):
        t = t_(d)
        return filtro(ruido(d), 'banda', (f0, f1)) * (0.55 + 0.45 * np.sin(2 * np.pi * 2.6 * t)) * env(int(d * SR), d * 0.3, d * 0.6, 2)
    eco = lambda x, cola=2.2, mezcla_=0.38: reverb(x, cola, mezcla_, 3000)
    with azar_propio('cueva_base'):
        S['cueva'] = dict(
            toque=mezclar((0, laja(T + 12, 0.25), 0.7), (0, piedras(), 0.25)),
            elegir=hueso(T + 7, 0.22),
            pasar=mezclar((0, filtro(ruido(0.2), 'banda', (250, 1600)) * env(int(0.2 * SR), 0.02, 0.12, 3), 0.9), (0.09, filtro(ruido(0.08), 'banda', (300, 1400)) * env(int(0.08 * SR), 0.01, 0.05, 3), 0.5)),
            abrir=eco(mezclar(*[(0.07 * i, laja(T + n, 0.7), 0.5) for i, n in enumerate([0, 5, 7, 12])]), 1.2, 0.25),
            guardar=mezclar((0, piedras(), 0.6), (0.06, laja(T + 7, 0.5), 0.5), (0.18, laja(T + 12, 0.7), 0.5)),
            logro=eco(mezclar(*[(0.18 * i, piel(0.35), 0.5) for i in range(3)], melodia(hueso_f, [T + 12 + n for n in [0, 3, 5, 7, 10, 12]], 0.13, 0.35, g=0.45))),
            tema=eco(mezclar((0, bramadera(1.1), 0.5), (0.35, hueso(T + 12, 0.9), 0.4), (0.1, piel(0.4), 0.45), (0.55, piel(0.4), 0.35))),
            aviso=eco(mezclar(melodia(hueso_f, [T + 12 + n for n in [7, 5, 3, 0]], 0.2, 0.4, g=0.5), (0, piel(0.3), 0.35), (0.6, laja(T + 24, 0.8), 0.3)), 1.6, 0.3),
            solemne=eco(mezclar(melodia(hueso_f, [T + n for n in [0, 3, 5, 7, 5, 3, 0, -2, 0]], 0.55, 0.7, g=0.35), (0, bramadera(5.5, 120, 300), 0.35),
                                *[(1.1 * i, piel(0.5), 0.45) for i in range(5)], *[(0.8 + 1.4 * i, laja(T + [12, 19, 15, 24][i], 1.0), 0.18) for i in range(4)]), 2.6, 0.4))

    # Error: las piedras que chocan mal y la flauta que cae medio tono. Cambio: dos lajas a la quinta.
    # Tecla: el carbón que raspa la caliza. Rascar: ocre que se raspa de la pared, y una laja que queda sonando.
    with azar_propio('cueva'):
        nuevos('cueva',
               error=mezclar((0, piedras(), 0.6), (0.06, hueso(T + 3, 0.18), 0.45), (0.2, hueso(T + 2, 0.4) * np.linspace(1, 0.4, int(0.4 * SR)), 0.45)),
               cambio=mezclar((0, laja(T + 12, 0.35), 0.5), (0.08, laja(T + 19, 0.45), 0.5)),
               tecla=mezclar((0, trazo((450, 2000), 0.06, 9, 0.04), 1), (0, filtro(ruido(0.015), 'bajo', 600) * env(int(0.015 * SR), 0.001, 0.008), 0.4)),
               rascar=rascado((700, 3800), laja(T + 12, 0.5), aspereza=40))

def frente():
    # Fa menor con la séptima mayor (Fa, La bemol, Do, Mi): la red clandestina, en una ciencia ficción de los setenta hecha
    # a mano. Relés que cierran, el pitido corto de la consola (un pulso estrecho por un altavoz pequeño), el teletipo con
    # su motor afinado en Fa, el chasquido del interruptor de baquelita y un sintetizador analógico de sierras con filtro
    # resonante que se abre y se cierra. Ni senos limpios (Espacio), ni blips cuadrados (Terminal), ni sierras quietas
    # (Ciudad neón, Laboratorio).
    T = -4   # Fa4
    interruptor = lambda: mezclar((0, clic(0.003, (3000, 9500)), 1.0), (0.0006, filtro(ruido(0.03), 'banda', (140, 800)) * env(int(0.03 * SR), 0.0005, 0.012, 4), 0.9),
                                  (0.005, clic(0.005, (900, 3200)), 0.35))
    rel = lambda g=1.0: rele(g, hz(T + 36))
    pit = lambda n, d=0.07, **kw: consola(hz(n), d, **kw)
    # El sintetizador: pulsado (el filtro se abre de golpe y se cierra) y en colchón (el filtro se abre despacio).
    pulsado = lambda f, d, brillo=3000: analogico(f, d, (300, brillo, 650), 0.008, 0.16, 3.0, 0.004, 0.0, 0.28, 0.08)
    colchon = lambda f, d, brillo=1700: analogico(f, d, (220, brillo, brillo * 0.6), d * 0.45, d * 0.4, 1.8, 0.09, 1.0, 1.0, min(0.5, d * 0.4), 0.006)
    motor = hz(T - 24)
    with azar_propio('frente_base'):
        S['frente'] = dict(
            toque=mezclar((0, pit(T + 12, 0.055), 0.85), (0, clic(0.003, (3500, 9000)), 0.15)),
            elegir=mezclar((0, interruptor(), 0.7), (0.014, pit(T + 19, 0.06), 0.6)),
            pasar=teletipo(3, 13, motor),
            abrir=mezclar(*[(0.045 * i, rel(1 - 0.2 * i), 0.7) for i in range(3)],
                          (0.1, acorde(lambda f, d: analogico(f, d, (160, 2600, 900), 0.32, 0.25, 3.0, 0.03, 1.0, 0.3, 0.14), [T - 12, T - 5], 0.55), 0.55),
                          (0.52, pit(T + 19, 0.07), 0.3)),
            guardar=mezclar((0, rel(), 0.8), (0.03, analogico(hz(T - 24), 0.3, (120, 900, 140), 0.008, 0.08, 2.0, 0.004, 0.0, 0.12, 0.05), 0.5),
                            (0.03, pit(T + 7, 0.06), 0.55), (0.12, pit(T + 12, 0.13), 0.55)),
            logro=reverb(mezclar(*[(0.13 * i, pulsado(hz(T + n), 0.5), 0.42) for i, n in enumerate([-12, -9, -5, -1, 0, 3, 7])],
                                 (0.9, acorde(colchon, [T - 12, T - 5, T, T + 2, T + 3], 1.3), 0.55), (0.9, pulsado(hz(T - 24), 0.6, 900), 0.4),
                                 (0.9, rel(), 0.35), (1.5, pit(T + 24, 0.07), 0.18), (1.62, pit(T + 24, 0.07), 0.18)), 1.4, 0.26),
            tema=reverb(mezclar((0, interruptor(), 0.4), (0.06, rel(), 0.4), (0.1, acorde(colchon, [T - 12, T - 9, T - 5, T - 1], 1.15, brillo=2400), 0.6),
                                (0.1, pulsado(hz(T - 24), 0.7, 1100), 0.45), (0.62, pit(T + 24, 0.07), 0.25), (0.72, pit(T + 19, 0.11), 0.25)), 1.2, 0.24),
            aviso=reverb(mezclar((0, teletipo(7, 11, motor), 0.7), (0.75, colchon(hz(T - 12), 1.2), 0.35), (0.75, colchon(hz(T - 5), 1.2), 0.25),
                                 (0.78, pit(T + 19, 0.09), 0.4), (0.9, pit(T + 19, 0.09), 0.4), (1.04, pit(T + 24, 0.24), 0.4)), 1.4, 0.26),
            solemne=reverb(mezclar(*[(0.95 * i, acorde(colchon, [T - 12 + a for a in ch], 1.6 if i == 4 else 1.05), 0.5)
                                     for i, ch in enumerate([[0, 3, 7, 11], [-4, 0, 3, 7], [1, 5, 8, 12], [-5, -1, 2, 8], [0, 3, 7, 12]])],
                                   *[(0.95 * i, pulsado(hz(T - 24 + r), 0.9, 700), 0.4) for i, r in enumerate([0, -4, 1, -5, 0])],
                                   *[(0.95 * i + 0.24 * k, pulsado(hz(T + ch[k]), 0.45, 2400), 0.16)
                                     for i, ch in enumerate([[0, 3, 7, 11], [-4, 0, 3, 7], [1, 5, 8, 12], [-5, -1, 2, 8]]) for k in range(4)],
                                   (0, rel(), 0.4), (4.4, pit(T + 24, 0.08), 0.2), (4.55, pit(T + 24, 0.08), 0.2)), 2.4, 0.34))

    # Error: el pitido que cae a la sensible y se hunde al fallar la corriente, el relé que zumba y el sintetizador que se
    # cierra. Cambio: el interruptor y el piloto que sube una cuarta. Tecla: el teclado del teletipo (el tipo, el carro y el
    # selector). Rascar: la chapa esmaltada color hueso de la consola, con su eco de metal.
    with azar_propio('frente'):
        nuevos('frente',
               error=mezclar((0, pit(T + 12, 0.08), 0.5), (0.1, pit(T + 11, 0.32, cae=1.5), 0.5), (0.1, mezclar(*[(k / 34, rel(0.6), 1.0) for k in range(5)]), 0.45),
                             (0.1, analogico(hz(T - 13), 0.35, (1400, 1400, 120), 0.001, 0.1, 2.5, 0.004, 0.0, 0.2, 0.06, cae=2.0), 0.35)),
               cambio=mezclar((0, interruptor(), 0.8), (0.025, pit(T + 15, 0.05), 0.5), (0.085, pit(T + 20, 0.07), 0.5)),
               tecla=mezclar((0, clic(0.005, (1600, 6500)), 1), (0.0012, filtro(ruido(0.02), 'banda', (280, 1300)) * env(int(0.02 * SR), 0.0006, 0.007, 4), 0.6),
                             (0.009, clic(0.0035, (2400, 7500)), 0.3)),
               rascar=rascado((1100, 5200), mezclar((0, campana(hz(T + 24), 0.5, 1.41, 1.2, 6), 1.0), (0.02, pit(T + 24, 0.05), 0.2)), aspereza=75))

def arcontes():
    # La bemol mixolidio con la sexta menor (La bemol, Si bemol, Do, Re bemol, Mi bemol, Fa bemol, Sol bemol): la tercera
    # mayor es el dorado del mundo de abajo y la sexta menor, que cae a la quinta, lo que hay debajo. Galilea en el siglo I
    # vista por un iniciado: el kinnor (lira de mano de tripa, con púa y caja de madera), los crótalos de bronce que baten,
    # una sola voz grave que salmodia a media voz (no un coro), un zumbido hondo para los arcontes y una nota limpia y alta
    # para el Pleroma. Ni la lira de dedos en pentatónica mayor (Helénico), ni la segunda menor del arpa (Egipto), ni el
    # hiyaz del laúd (Al-Ándalus), ni el frigio con tritono (Horror cósmico), ni órgano ni coro (Gótico, Scriptorium).
    T = -1   # La bemol 4
    kin = lambda n, d=0.8, b=0.5: kinnor(hz(n), d, b)
    crot = lambda n=T + 24, d=1.1, apagado=False: crotalos(hz(n), d, apagado)
    voz = lambda n, d, v='o': salmodia(hz(n), d, v)
    hondo = lambda d, **kw: zumbido_hondo(hz(T - 36), d, **kw)
    luz = lambda n, d=2.0: pleroma(hz(n), d)
    rasgueo = lambda notas, d=1.0, paso=0.03, b=0.45: mezclar(*[(paso * i, kin(n, d, b), 0.5) for i, n in enumerate(notas)])
    def velo(d=0.3):
        """El velo de lino que se corre: un roce suave y grave, con un hilo de aire arriba."""
        t = t_(d); s = np.sin(np.pi * t / d) ** 2
        tela = np.abs(filtro(ruido(d), 'bajo', 25)); tela /= tela.max() + 1e-9
        return filtro(ruido(d), 'banda', (380, 2600)) * s * (0.6 + 0.4 * tela) + 0.12 * filtro(ruido(d), 'alto', 5000) * s ** 3
    def jarra():
        """La tapa de barro que asienta en la boca de la jarra: el golpe de barro y el hueco de la jarra que responde."""
        hueco = resonador(filtro(ruido(0.35), 'bajo', 900), hz(T - 12), 18) * env(int(0.35 * SR), 0.002, 0.12, 3)
        return mezclar((0, clic(0.012, (700, 3200)), 0.8), (0.001, hueco / (np.abs(hueco).max() + 1e-9), 0.6),
                       (0.002, seno(hz(T - 12), 0.25) * env(int(0.25 * SR), 0.002, 0.08, 3), 0.3))
    with azar_propio('arcontes_base'):
        S['arcontes'] = dict(
            toque=kin(T + 12, 0.28, 0.6),
            elegir=crot(T + 24, 0.4, apagado=True),
            pasar=velo(0.3),
            abrir=mezclar((0, velo(0.22), 0.55), (0.12, rasgueo([T - 12, T - 5, T, T + 4, T + 7], 0.9), 1.0)),
            guardar=mezclar((0, jarra(), 0.7), (0.06, kin(T + 7, 0.7, 0.45), 0.45), (0.22, kin(T + 12, 0.9, 0.5), 0.45)),
            logro=reverb(mezclar((0, voz(T - 24, 2.6), 0.4), (0, voz(T - 17, 2.6), 0.22),
                                 *[(0.11 * i, kin(T + n, 0.9, 0.55), 0.42) for i, n in enumerate([0, 4, 7, 12, 16])],
                                 (0.6, crot(T + 24, 1.4), 0.12), (0.6, luz(T + 24, 2.6), 0.2), (0.6, luz(T + 36, 2.0), 0.05)), 1.6, 0.3),
            tema=reverb(mezclar((0, hondo(0.9, entra=0.4, sale=0.6), 0.35), (0.25, crot(T + 24, 1.0), 0.2), (0.3, voz(T - 24, 1.5), 0.45),
                                *[(0.35 + 0.22 * i, kin(T + n, 0.8 if i < 2 else 1.1, 0.5), 0.45) for i, n in enumerate([12, 8, 7])]), 1.3, 0.28),
            aviso=reverb(mezclar((0, crot(T + 24, 1.0), 0.28), (0.24, crot(T + 24, 1.2), 0.24), (0.5, voz(T - 24, 2.0), 0.45), (0.5, voz(T - 17, 2.0), 0.25),
                                 *[(0.5 + 0.2 * i, kin(T + n, 0.7 if i < 4 else 1.2, 0.5), 0.45) for i, n in enumerate([12, 8, 7, 4, 7])]), 1.4, 0.28),
            # Lo solemne: abre el zumbido de los arcontes; una voz salmodia sobre el bordón de otra (sube a su nota de recitar,
            # la repite y cae a la tónica), el kinnor marca los cambios y, al final, queda la nota limpia del Pleroma.
            solemne=reverb(mezclar(
                (0, hondo(2.6, entra=0.45, sale=0.5), 0.4),
                (0.4, voz(T - 24, 6.2, 'u'), 0.35),
                *[(0.6 + 0.42 * i, voz(T - 12 + n, 0.46 if i < 11 else 1.4), 0.5) for i, n in enumerate([0, 2, 4, 4, 4, 4, 5, 4, 2, 4, 2, 0])],
                *[(0.6 + 0.42 * i, domar(rasgueo([T - 12 + a for a in ch], 1.6, 0.035, 0.4)), 0.5) for i, ch in ((0, [0, 7, 12]), (4, [-4, 0, 4]), (8, [-7, 0, 5]), (11, [0, 4, 7, 12]))],
                (5.75, crot(T + 24, 1.6), 0.18), (5.75, luz(T + 24, 2.6), 0.25)), 2.4, 0.34))

    # Error: los arcontes: el zumbido hondo que crece de golpe y se hunde un semitono, con dos cuerdas apagadas que bajan
    # de la sexta menor a la sexta menor grave. Cambio: dos crótalos apagados, de la quinta a la octava. Tecla: la caña con
    # tinta de hollín sobre el papiro, que salta en sus fibras. Rascar: una moneda de bronce de Herodes Antipas (acuñada en
    # Tiberíades) sobre la lámina, y un crótalo que queda sonando.
    with azar_propio('arcontes'):
        nuevos('arcontes',
               error=mezclar((0, kin(T + 8, 0.14, 0.2), 0.4), (0.05, hondo(0.5, cae=1.0, entra=0.12, sale=0.6, abre=(450, 1300)), 0.75), (0.15, kin(T - 4, 0.35, 0.15), 0.45)),
               cambio=mezclar((0, crot(T + 19, 0.3, apagado=True), 0.5), (0.08, crot(T + 24, 0.35, apagado=True), 0.5)),
               tecla=mezclar((0, trazo((900, 4200), 0.045, 6, 0.03), 1), *[(0.011 * k, clic(0.002, (2500, 7000)), 0.25 - 0.06 * k) for k in range(3)]),
               rascar=rascado((750, 4300), 3.0 * crot(T + 31, 0.8), aspereza=45, pasadas=((0.0, 0.26), (0.31, 0.3), (0.66, 0.28))))

def galia():
    # Do sostenido eolio, el menor natural (Do#, Re#, Mi, Fa#, Sol#, La, Si): sin sensible, la séptima menor sube a la
    # tónica (Si–Do#) y la sexta menor (La) le da su sombra. La Galia del 52 a. C., desde un oppidum arverno: el carnyx de
    # bronce con su bramido para los logros (Diodoro 5.30), la lira de los bardos pulsada con la yema y con sus cuerdas al
    # aire que resuenan solas (Diodoro 5.31), el yunque y el martillo de la fragua, las lanzas contra los escudos de madera,
    # el viento entre los robles, el fuego del hogar, el caballo y las voces de hombre: el grito que corre de campo en campo
    # (César 7.3) y la asamblea. Nada de gaitas, arpas irlandesas ni flautas «celtas»: ni el dórico ni el mixolidio que las
    # evocan (Tierras Antiguas, Templario), ni la lira seca en pentatónica (Helénico), ni el kinnor con púa (Arcontes), ni
    # la corneta quieta (Romano), ni tambores (Romano, Templario, Prehistoria) ni cascos de caballo (Mongol).
    T = -8   # Do sostenido 4
    al_aire = (hz(T - 12), hz(T - 17))   # las cuerdas graves de la lira que resuenan solas: Do#3 y Sol#2
    lir = lambda n, d=1.0, b=0.35, **kw: lira_bardo(hz(n), d, b, al_aire=al_aire, **kw)
    rueda = lambda notas, d=1.2, paso=0.05, b=0.35: mezclar(*[(paso * i, lir(n, d, b), 0.5) for i, n in enumerate(notas)])
    cx = lambda n, d, **kw: carnyx(hz(n), d, **kw)
    yun = lambda n, d=0.8, fuerza=1.0: yunque(hz(n), d, fuerza)
    def grito(n, d=0.8):
        """Un hombre que llama a los del campo de al lado: «¡ho-a!», de la quinta de abajo a la tónica, y la voz que se le cae al final."""
        return filtro(voz_hombre(hz(n), d, ('o', 'a'), 0.18, ((0.0, -5), (0.14, 0)), cae=3.0, esfuerzo=0.85, aliento=0.05, h=0.5), 'alto', 200)
    with azar_propio('galia_base'):
        S['galia'] = dict(
            toque=lir(T + 12, 0.3, 0.45),
            elegir=mezclar((0, lir(T + 7, 0.3, 0.4, apagar=0.07), 1.0), (0.07, clic(0.006, (500, 2200)), 0.12)),
            pasar=robles(0.32),
            abrir=rueda([T - 12, T - 5, T, T + 3, T + 7], 1.1),
            guardar=mezclar((0, hierro_al_rojo(hz(T + 12)), 0.8), (0.16, yun(T + 12, 0.7, 0.7), 0.55), (0.3, yun(T + 19, 0.55, 0.45), 0.35)),
            # El logro: la asamblea golpea las armas y el carnyx sube por sus armónicos (Do#, Sol#, Do#) hasta el bramido.
            logro=reverb(mezclar((0, choque_armas(0.7, 10), 0.55),
                                 (0.15, cx(T - 12, 0.42, sube=3.0, crece=0.3), 0.5), (0.55, cx(T - 5, 0.42, sube=2.0, crece=0.3), 0.5),
                                 (0.95, cx(T, 1.7, sube=2.5, crece=0.55, bramido=0.38), 0.55), (0.95, escudo(1.0, 0.8), 0.6),
                                 (1.0, rueda([T - 12, T - 5, T, T + 3, T + 7, T + 12], 1.5, 0.045), 0.35)), 1.8, 0.3),
            # El tema: el caballo resopla, pasa el viento por los robles, suena un carnyx a lo lejos y responde la lira.
            tema=reverb(mezclar((0, resoplido(0.5), 0.35), (0.1, robles(1.5, 0.8), 0.22), (0.3, lejos(cx(T - 12, 1.1, crece=0.5), 0.6), 0.5),
                                (0.75, rueda([T, T + 3, T + 7], 1.2, 0.07), 0.55)), 1.4, 0.28),
            # El aviso: el grito que corre de campo en campo (César 7.3): uno llama cerca y otros, cada vez más lejos, lo pasan.
            aviso=reverb(mezclar((0, rueda([T - 5, T + 7], 1.0, 0.04), 0.3), (0.1, grito(T, 0.85), 0.7),
                                 (0.75, lejos(grito(T, 0.8), 1.0), 0.45), (1.4, lejos(grito(T, 0.8), 2.0), 0.28)), 1.3, 0.24),
            # Lo solemne: la asamblea alrededor del hogar murmura la tónica y la quinta, el bardo canta con la lira y, al
            # final, la asamblea aprueba golpeando las armas (César 7.21, de memoria: no está en fuentes_galia.md, [sin verificar])
            # y un carnyx responde a lo lejos.
            solemne=reverb(mezclar(
                (0, asamblea(hz(T - 12), 7.2, 'o', 5), 0.4), (0.3, asamblea(hz(T - 5), 6.6, 'o', 4), 0.24), (0, fuego(7.6, 5), 0.1),
                *[(0.5 + 0.48 * i, domar(lir(T + n, 1.3 if i < 8 else 2.4, 0.4)), 0.5) for i, n in enumerate([7, 8, 7, 3, 5, 3, 2, -2, 0])],
                *[(0.5 + 0.48 * i, domar(rueda([T + a for a in ch], 1.8, 0.06, 0.3)), 0.4) for i, ch in ((0, [-12, -5, 0]), (3, [-16, -12, -9]), (6, [-14, -10, -7]), (8, [-12, -5, 0, 3]))],
                (5.3, choque_armas(0.9, 12, 0.9), 0.4), (5.4, lejos(cx(T - 12, 1.8, crece=0.6), 1.2), 0.4)), 2.4, 0.34))

    # Error: la lanza que da sordo en el escudo y el carnyx que se ahoga medio tono abajo y se hunde al quedarse sin aire.
    # Cambio: el martillo que toca dos veces el yunque, de la quinta a la octava. Tecla: el estilo que escribe letras griegas
    # en la tablilla (que fuera de cera es un recurso) y el marco de madera. Rascar: un estátero arverno del caballo, de oro
    # con plata y cobre, que suena más claro que el oro puro.
    with azar_propio('galia'):
        tablilla = resonador(np.concatenate([clic(0.004, (300, 2500)), np.zeros(int(0.04 * SR))]), 520, 60)
        t = t_(0.6); fe = hz(T + 31)
        estatero = sum(a * np.sin(2 * np.pi * fe * r * t) * np.exp(-t * c) for r, a, c in ((1.0, 1.0, 5.0), (2.31, 0.45, 8.0), (3.93, 0.2, 13.0))) * np.minimum(1, t / 0.001)
        nuevos('galia',
               error=mezclar((0, escudo(0.9, 0.1), 0.5), (0.03, cx(T, 0.13, sube=1.0, crece=0.2), 0.4),
                             (0.15, cx(T - 1, 0.42, sube=0.0, crece=0.15, cae=1.5, bramido=0.45, brillo=0.6) * np.exp(-t_(0.42) / 0.16), 0.55)),
               cambio=mezclar((0, yun(T + 19, 0.3, 0.5), 0.6), (0.085, yun(T + 24, 0.35, 0.6), 0.6)),
               tecla=mezclar((0, trazo((650, 3000), 0.05, 5, 0.03), 1), (0, tablilla / (np.abs(tablilla).max() + 1e-9), 0.25)),
               rascar=rascado((1300, 5800), estatero, aspereza=60))

def nouveau():
    # Mi bemol mayor con la séptima mayor y la novena (Mi bemol, Sol, Si bemol, Re, Fa): el color de los salones de 1900,
    # el acorde de séptima mayor que se queda sin resolver, y la cadencia plagal con el cuarto grado menor (La bemol menor
    # con sexta) que suspira antes de volver a casa. París hacia 1900, desde un taller de litografía: el piano de salón
    # (macillo de fieltro y tres cuerdas que laten), la cajita de música de cilindro con su peine de acero, las campanillas
    # de cristal de la lámpara, el pliego que se despega de la piedra entintada y el golpe sordo de la prensa a mano. Ni
    # la sexta y la novena del jazz ni el piano pulsado (Art déco), ni la cajita armónica y los cascabeles (Navidad), ni
    # el silbato y el latón (Vapor victoriano), ni corneta y tambor (Napoleónico, Revolución), ni laúd (Renacimiento), ni
    # guitarra y grillos (Botánico), ni el crótalo de bronce (Arcontes).
    T = -6   # Mi bemol 4
    pia = lambda n, d=1.2, fuerza=0.6, apagar=None: piano_salon(hz(n), d, fuerza, apagar)
    caja = lambda n, d=1.0, fuerza=1.0, cae=0.0: peine(hz(n), d, fuerza, cae)
    cri = lambda n, d=1.6, fuerza=0.7: cristal(hz(n), d, fuerza)
    def rodado(notas, d=1.6, paso=0.035, fuerza=0.55):
        """Un acorde de piano rodado de abajo arriba, con el pedal puesto."""
        return mezclar(*[(paso * i, pia(n, d - paso * i, fuerza), 0.5) for i, n in enumerate(notas)])
    def interruptor():
        """El interruptor de llave de 1900: la llave de latón que gira y encaja en la base de porcelana."""
        return mezclar((0, filtro(clic(0.004, (1800, 6500)), 'bajo', 6000), 0.6), (0.0008, filtro(ruido(0.018), 'banda', (500, 2600)) * env(int(0.018 * SR), 0.0006, 0.006, 4), 0.7),
                       (0.012, clic(0.002, (3000, 8500)), 0.2))
    def cuerda(clics=5, cps=11):
        """La llave que da cuerda a la cajita de música: el trinquete que salta diente a diente, dentro de la caja de madera."""
        partes = []
        for i in range(clics):
            e = np.concatenate([clic(0.0025, (2500, 8000)), np.zeros(int(0.03 * SR))])
            caja_m = resonador(e, 900, 140)
            partes += [(i / cps + rng.uniform(-0.004, 0.004), e, 0.6), (i / cps, caja_m / (np.abs(caja_m).max() + 1e-9), 0.25)]
        return mezclar(*partes)
    with azar_propio('nouveau_base'):
        S['nouveau'] = dict(
            # El toque: un colgante de cristal de la lámpara.
            toque=cri(T + 12, 0.34, 0.6) * np.exp(-t_(0.34) * 6),
            # Elegir: una sola lámina de la cajita de música.
            elegir=caja(T + 19, 0.32, 0.9) * np.exp(-t_(0.32) / 0.09),
            # Pasar: el pliego que se despega de la piedra.
            pasar=pliego(0.3),
            # Abrir: un acorde de piano rodado (Mi bemol con séptima mayor y novena) y el cristal arriba.
            abrir=mezclar((0, rodado([T - 12, T - 5, T + 2, T + 4, T + 11], 1.25, 0.04, 0.5), 1.0), (0.2, cri(T + 26, 1.0, 0.5), 0.3)),
            # Guardar: la prensa baja sobre el pliego, y el piano da la quinta y la tónica.
            guardar=mezclar((0, domar(prensa(1.0, 0.16), 0.35), 0.75), (0.2, pia(T + 7, 0.6, 0.5, apagar=0.45), 0.5), (0.31, pia(T + 12, 0.55, 0.55), 0.55)),
            # El logro: un barrido de piano de salón por el acorde, un poco teatral, que cae en el acorde con pedal; un compás
            # de vals por debajo (el «um» en el uno, el «pa-pa» en el dos y el tres), las campanillas de cristal que bajan y
            # la cajita que contesta arriba.
            logro=reverb(mezclar(
                *[(0.052 * i, pia(T - 12 + n, 0.5, 0.45 + 0.03 * i, apagar=0.3), 0.36) for i, n in enumerate([0, 7, 11, 14, 16, 19, 23, 26, 28, 31])],
                (0.56, rodado([T - 24, T - 12, T - 5, T + 4, T + 11, T + 14], 2.9, 0.025, 0.6), 0.9), (0.56, pia(T + 23, 2.7, 0.65), 0.35),
                (0.98, acorde(lambda f, d: piano_salon(f, d, 0.35, 0.26), [T - 1, T + 4, T + 7], 0.5), 0.35),
                (1.4, acorde(lambda f, d: piano_salon(f, d, 0.32, 0.26), [T - 1, T + 4, T + 7], 0.5), 0.3),
                *[(0.62 + 0.16 * i, cri(T + n, 1.8, 0.7), 0.26) for i, n in enumerate([31, 26, 23, 19])],
                (1.55, caja(T + 26, 1.2), 0.22), (1.7, caja(T + 31, 1.2), 0.18)), 1.6, 0.28),
            # El tema: se le da cuerda a la cajita y suena la vuelta del vals (Re, Fa, Mi bemol); el cristal cierra.
            tema=reverb(mezclar((0, cuerda(4, 13), 0.35),
                                (0.36, caja(T + 23, 1.0), 0.5), (0.36 + 0.32 * 2, caja(T + 26, 0.8), 0.45), (0.36 + 0.32 * 3, caja(T + 24, 1.1), 0.5),
                                (0.36, pia(T - 12, 1.3, 0.35), 0.3), (0.36 + 0.32 * 3, rodado([T - 12, T - 5, T - 1, T + 4], 1.1, 0.03, 0.35), 0.35),
                                (0.36 + 0.32 * 3, cri(T + 31, 1.1, 0.5), 0.2)), 1.1, 0.24),
            # El aviso: las campanillas de cristal bajan en un compás de vals sobre el piano (Si bemol con novena: el «um» y
            # el «pa-pa») y se posan en la tónica con la séptima mayor.
            aviso=reverb(mezclar(
                (0, pia(T - 17, 1.2, 0.5, apagar=0.36), 0.42), (0.42, acorde(lambda f, d: piano_salon(f, d, 0.35, 0.3), [T - 1, T + 5, T + 9], 0.5), 0.3),
                (0.84, acorde(lambda f, d: piano_salon(f, d, 0.32, 0.3), [T - 1, T + 5, T + 9], 0.5), 0.26),
                *[(0.42 * i, cri(T + n, 1.6, 0.7), 0.4) for i, n in enumerate([19, 14, 11])],
                (1.26, rodado([T - 24, T - 12, T - 5, T + 4, T + 11], 2.3, 0.03, 0.5), 0.75), (1.26, cri(T + 12, 2.2, 0.75), 0.45),
                (1.5, caja(T + 26, 1.2, 0.8), 0.16)), 1.4, 0.26),
            # Lo solemne: un vals lento de piano (Mi bemol con séptima mayor, Do menor con novena, La bemol menor con sexta y
            # vuelta a casa), con el «um-pa-pa» y la melodía, que se posa en la quinta; las campanillas cierran y la cajita,
            # lejos, repite la última vuelta.
            solemne=reverb(mezclar(*(lambda pz: [
                *[(3 * pz * i, pia(T + b, 3 * pz + 0.4, 0.5, apagar=3 * pz * 0.9), 0.4) for i, b in enumerate([-12, -15, -19])],
                *[(3 * pz * i + pz * k, acorde(lambda f, d: piano_salon(f, d, 0.32, pz * 0.8), [T - 12 + a for a in ch], pz * 1.2), 0.26 - 0.03 * (k - 1))
                  for i, ch in enumerate([[4, 7, 11], [0, 4, 9], [5, 8, 14]]) for k in (1, 2)],
                *[(pz * b, pia(T + n, (dd * pz + 0.6) if b < 9 else 3.6, 0.55, apagar=None if b >= 9 else dd * pz + 0.1), 0.5)
                  for b, n, dd in [(0, 14, 2), (2, 12, 1), (3, 11, 2), (5, 7, 1), (6, 8, 2), (8, 5, 1), (9, 7, 3)]],
                (9 * pz, rodado([T - 24, T - 12, T - 5, T + 4, T + 11, T + 14], 3.6, 0.04, 0.5), 0.7),
                *[(9 * pz + 0.35 + 0.22 * i, cri(T + n, 1.8, 0.6), 0.25) for i, n in enumerate([26, 23, 19])],
                *[(9 * pz + 0.9 + 0.8 * pz * b, lejos(caja(T + 24 + n, 0.9, 0.8), 0.7), 0.3) for b, n in [(0, 2), (2, 0), (3, -1), (5, -5)]]])(0.56)), 2.0, 0.32))

    # Error: la cajita a la que se le acaba la cuerda (la nota que se arrastra medio tono abajo y se hunde) sobre el piano
    # apagado en el grave. Cambio: el interruptor de llave y la lámpara que se enciende: dos cristales, de la quinta a la
    # octava. Tecla: el lápiz graso de litógrafo sobre la piedra graneada (sale a saltitos, por el grano). Rascar: una
    # moneda de plata de un franco (de 1900; cuál, [sin verificar]: no está en fuentes_nouveau.md), que suena clara.
    with azar_propio('nouveau'):
        t = t_(0.7); fp = hz(T + 33)
        plata = sum(a * np.sin(2 * np.pi * fp * r * t) * np.exp(-t * c) for r, a, c in ((1.0, 1.0, 4.0), (1.73, 0.35, 6.0), (2.33, 0.3, 8.0), (3.91, 0.12, 14.0))
                    if fp * r < SR * 0.45) * np.minimum(1, t / 0.001)
        grano = lambda: mezclar((0, trazo((700, 3400), 0.045, 4, 0.028), 1.0),
                                *[(rng.uniform(0.002, 0.038), clic(0.0012, (1500, 5500)), rng.uniform(0.15, 0.4)) for _ in range(6)])
        nuevos('nouveau',
               error=mezclar((0, caja(T + 19, 0.2, 0.9), 0.5), (0.15, caja(T + 18, 0.45, 0.7, cae=0.8) * np.exp(-t_(0.45) / 0.2), 0.55),
                             (0.13, pia(T - 18, 0.4, 0.35, apagar=0.16), 0.55)),
               cambio=mezclar((0, interruptor(), 0.3), (0.03, cri(T + 19, 0.4, 0.55) * np.exp(-t_(0.4) * 5), 0.45), (0.11, cri(T + 24, 0.45, 0.55) * np.exp(-t_(0.45) * 4), 0.45)),
               tecla=grano(),
               rascar=rascado((1400, 6400), plata, aspereza=85, pasadas=((0.0, 0.27), (0.32, 0.27), (0.65, 0.29))))

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
        urbe,
        ciber,
        neon,
        heleno,
        egipto,
        medieval,
        templario,
        distopia,
        refugio,
        orden,
        vapor,
        laboratorio,
        colonos,
        napoleon,
        deco,
        botanico,
        ruinas,
        navidad,
        seda,
        edo,
        urss,
        gotico,
        arcano,
        espacio,
        abismo,
        andalus,
        meso,
        mongol,
        renacimiento,
        pirata,
        revolucion,
        antiguas,
        cueva,
        frente,
        arcontes,
        galia,
        nouveau,
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
