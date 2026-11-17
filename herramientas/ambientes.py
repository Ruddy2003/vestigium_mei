"""Vestigium Mei — el ambiente del cuadro y el ronroneo del gato (parte 8, encargo B; `docs/diseno/CUADROS_PLAN.md`).

Al abrir el cuento suena, suave y en bucle, el lugar de su cuadro: el mar y los remos del puerto de Zea, la lluvia y el
vapor del depósito, la lluvia y la olla del callejón de neón… Un ambiente por tema con escena (`a_<tema>_ambiente`) y,
donde la escena cambia mucho de noche, otro de noche (`a_<tema>_ambiente_noche`). Todo sintetizado aquí, sin muestras:
ruido filtrado, osciladores y los instrumentos de `afinados.py` (las campanas, la flauta, el coro, el yunque y el fuego
son los mismos de los sonidos del tema, en su escala).

**El bucle.** Cada ambiente dura [LARGO] s exactos y empalma consigo mismo: se sintetiza [LARGO] + [CRUCE] s y los últimos
[CRUCE] s se funden (a potencia constante) con los primeros. Así la última muestra sigue de forma natural a la primera: no
hay chasquido ni salto de volumen al volver a empezar. El guion lo **comprueba** en el archivo ya codificado y vuelto a
leer (con ffmpeg): el salto entre la última muestra y la primera, comparado con los saltos de muestra a muestra del resto;
el volumen del último medio segundo contra el del primero; y la energía aguda (lo que sería un chasquido) justo en la
costura contra la del resto del archivo. Los sonidos que se repiten a ritmo fijo (el reloj del archivo, los remos) van a
un pulso que cabe un número entero de veces en [LARGO], para que tampoco se note el compás en la costura.

**El ronroneo** (`s_gato_ronroneo`, uno común: el gato es el mismo animal en todos los temas): ruido rosa modulado a unos
25 golpes por segundo, con un bajo de 50 a 150 Hz y un cuerpo de 200 a 600 Hz para que se oiga en el altavoz del teléfono;
una espiración y una inspiración, 1,6 s.

Uso: python3 ambientes.py <carpeta res/raw> [temas separados por comas | todos] [--sin-ronroneo]
Imprime, por archivo: duración, pico y RMS (dBFS) y las tres medidas de la costura.
"""
import os, subprocess, sys, tempfile, wave
import numpy as np
from afinados import SR, filtro, campana, flauta, coro, metal, yunque, fuego, robles, reverb, clic, trazo, papel, hz  # noqa: F401
import afinados as A

LARGO = 45.0          # s: lo que dura cada bucle
CRUCE = 4.0           # s: la cola que se funde con el principio
N = int(LARGO * SR)
M = int((LARGO + CRUCE) * SR)
RMS_DB = -21.0        # el volumen de cada ambiente (el reproductor lo baja después a 0,42)
PICO = 0.7            # el techo de los picos (−3 dBFS)

# ------------------------------------------------------------------ bloques

def rosa(R, n=M):
    X = np.fft.rfft(R.standard_normal(n)); f = np.arange(len(X), dtype=float); f[0] = 1.0
    x = np.fft.irfft(X / np.sqrt(f), n); return x / (x.std() + 1e-12)
def marron(R, n=M):
    X = np.fft.rfft(R.standard_normal(n)); f = np.arange(len(X), dtype=float); f[0] = 1.0
    x = np.fft.irfft(X / f, n); return x / (x.std() + 1e-12)
def blanco(R, n=M): return R.standard_normal(n)
def banda(x, lo, hi, orden=2): return filtro(x, 'banda', (lo, hi), orden)
def bajo(x, f, orden=2): return filtro(x, 'bajo', f, orden)
def alto(x, f, orden=2): return filtro(x, 'alto', f, orden)
def rms(x): return float(np.sqrt(np.mean(np.asarray(x) ** 2)) + 1e-12)
def norm(x): return x / rms(x)
def db(g): return 10 ** (g / 20)
def t_(d): return np.arange(int(d * SR)) / SR

def curva(R, cada, lo=0.0, hi=1.0, n=M):
    """Una curva lenta al azar entre [lo] y [hi]: un valor nuevo cada [cada] s, unidos con cosenos (sin esquinas)."""
    k = R.uniform(lo, hi, int(n / SR / cada) + 3)
    t = np.arange(n) / SR / cada; i = t.astype(int); fr = t - i
    w = (1 - np.cos(np.pi * fr)) / 2
    return k[i] * (1 - w) + k[i + 1] * w

def poner(capa, t, s, g=1.0):
    """Suma [s] en [capa] desde el segundo [t] (lo que no cabe se corta)."""
    i = int(t * SR)
    if i >= len(capa) or i + len(s) <= 0: return
    a = max(0, -i); b = min(len(s), len(capa) - i)
    capa[i + a:i + b] += s[a:b] * g

def esparcir(R, por_minuto, hacer, desde=0.0, hasta=None):
    """Una capa con sucesos sueltos al azar: [hacer](R) da cada uno, ya con su volumen. Los frecuentes, al azar (Poisson);
    los raros (menos de ocho por bucle), repartidos: cada uno en su tramo del bucle, para que no salgan todos juntos ni
    ninguno, y fuera de la costura (entre CRUCE y el final), para que se oigan enteros."""
    capa = np.zeros(M); hasta = LARGO + CRUCE if hasta is None else hasta
    k = por_minuto * LARGO / 60.0
    if k < 8:
        cuantos = max(1, int(round(k))); ini = max(desde, CRUCE); tramo = (LARGO - ini) / cuantos
        for i in range(cuantos):
            s = hacer(R)
            t = ini + tramo * (i + R.uniform(0.1, 0.9)); t = min(t, LARGO - len(s) / SR) if len(s) / SR < LARGO - ini else ini
            poner(capa, t, s)
        return capa
    t = desde + R.exponential(60.0 / por_minuto)
    while t < hasta:
        s = hacer(R); poner(capa, t, s); t += R.exponential(60.0 / por_minuto) + len(s) / SR * 0.5
    return capa

def a_pulso(R, periodo, hacer, juego=0.0, desde=0.0):
    """Una capa con un suceso cada [periodo] s exactos (± [juego]); [periodo] debe caber un número entero de veces en LARGO."""
    capa = np.zeros(M); k = 0
    while desde + k * periodo < LARGO + CRUCE:
        poner(capa, desde + k * periodo + (R.uniform(-juego, juego) if juego else 0.0), hacer(R, k)); k += 1
    return capa

def env_ad(n, ataque, caida):
    t = np.arange(n) / SR
    return np.minimum(1, t / max(ataque, 1e-4)) * np.exp(-np.maximum(0, t - ataque) / max(caida, 1e-4))

def campanita(f, d, ratio=2.76, indice=1.2, cae=2.5): return campana(f, d, ratio, indice, cae)

def lejos(x, k=1.0, cola=1.4):
    """Oído a lo lejos: menos agudos y más sala."""
    y = reverb(bajo(x, 4200 / (1 + k)), cola, min(0.65, 0.3 + 0.15 * k), 2500)
    return y[:len(x)]

def sala(x, cola=1.8, mezcla=0.35, brillo=3500):
    """En una sala de piedra: la cola de la reverberación, cortada al largo de la capa."""
    return reverb(x, cola, mezcla, brillo)[:len(x)]

# ------------------------------------------------------------------ piezas del paisaje sonoro

def oleaje(R, periodo=(5.0, 9.0), espuma=0.35, grave=500):
    """Olas que rompen en la orilla: un rumor que crece y se va, con la espuma arriba."""
    e = np.zeros(M); t = R.uniform(0, 2)
    while t < LARGO + CRUCE:
        p = R.uniform(*periodo); a = R.uniform(0.5, 1.0); m = int(p * 1.5 * SR); tt = np.arange(m) / m
        poner(e, t, a * np.where(tt < 0.3, (tt / 0.3) ** 2, np.exp(-(tt - 0.3) * 4.5))); t += p
    e /= e.max() + 1e-9
    rumor = bajo(rosa(R), grave) * (0.25 + 0.75 * e)
    arriba = banda(blanco(R), 1200, 6500) * e ** 1.6
    return norm(rumor + espuma * norm(arriba) * rms(rumor) + 0.35 * bajo(marron(R), 180))

def chapoteo(R, cada=(1.2, 3.0), brillo=2600):
    """El agua que lame un muelle o un casco: golpecitos húmedos, desparejos."""
    capa = np.zeros(M); t = 0.0
    while t < LARGO + CRUCE:
        d = R.uniform(0.15, 0.45); n = int(d * SR)
        s = banda(blanco(R, n), R.uniform(200, 400), brillo) * env_ad(n, R.uniform(0.01, 0.04), d * 0.35)
        s += 0.6 * bajo(blanco(R, n), 300) * env_ad(n, 0.02, d * 0.3)
        poner(capa, t, s, R.uniform(0.4, 1.0)); t += R.uniform(*cada)
    return norm(capa + 0.25 * norm(bajo(rosa(R), 700)) * rms(capa))

def rio(R, ancho=900, burbujas=40):
    """Un río ancho: el rumor grave, el agua que corre arriba y algún borboteo."""
    x = norm(bajo(rosa(R), ancho)) + 0.25 * norm(banda(blanco(R), 1500, 5000)) * curva(R, 0.6, 0.5, 1.0)
    return norm(x + esparcir(R, burbujas, lambda R: burbuja(R.uniform(300, 900), R.uniform(0.03, 0.08)) * R.uniform(0.3, 1.2)))

def burbuja(f, d):
    t = t_(d); ph = 2 * np.pi * np.cumsum(f * (1 + 0.8 * t / d)) / SR
    return np.sin(ph) * env_ad(len(t), 0.002, d * 0.35)

def fuente(R, densidad=900):
    """El caño de una fuente en su pila: un chorro de burbujas pequeñas y el agua que cae."""
    capa = np.zeros(M)
    n = int(densidad * (LARGO + CRUCE))
    pos = (R.uniform(0, LARGO + CRUCE, n) * SR).astype(int)
    for p in pos:
        d = R.uniform(0.008, 0.03); f = R.uniform(700, 2600)
        s = burbuja(f, d) * R.uniform(0.2, 1.0)
        capa[p:p + len(s)] += s[:max(0, min(len(s), M - p))]
    return norm(capa + 0.3 * norm(banda(rosa(R), 700, 4000)) * rms(capa))

def viento(R, lo=180, hi=900, rafagas=3.0, silbido=0.2):
    """Viento: el soplo en su banda, que sube y baja a ráfagas, y un silbido arriba en las más fuertes."""
    c = curva(R, rafagas, 0.15, 1.0)
    x = norm(banda(rosa(R), lo, hi)) * c
    x += silbido * norm(banda(rosa(R), hi * 1.5, hi * 3.5)) * c ** 3
    return norm(x)

def hojas(R, fuerza=1.0):
    """Las hojas que tiemblan en las copas, a sacudidas."""
    tiembla = np.abs(bajo(blanco(R), 60)); tiembla /= tiembla.max() + 1e-9
    return norm(banda(blanco(R), 1800, 7500) * tiembla ** 1.5 * curva(R, 2.5, 0.3, 1.0) * fuerza)

def lluvia(R, densidad=500, lo=800, hi=8000):
    """Lluvia: el siseo de fondo y las gotas sueltas (golpecitos de 1 a 3 ms)."""
    imp = np.zeros(M); n = int(densidad * (LARGO + CRUCE))
    imp[(R.uniform(0, 1, n) * (M - 1)).astype(int)] = R.exponential(1.0, n) * R.choice([-1, 1], n)
    k = int(0.003 * SR); nucleo = R.standard_normal(k) * np.exp(-np.arange(k) / (0.0008 * SR))
    gotas = banda(np.convolve(imp, nucleo)[:M], 1500, 9000)
    fondo = banda(blanco(R), lo, hi) * curva(R, 4.0, 0.8, 1.0)
    return norm(norm(fondo) + 0.8 * norm(gotas))

def goteo(R, por_minuto=30, f=(1400, 3400)):
    """Gotas que caen sobre chapa o en un charco: un «plic» con su tono."""
    def gota(R):
        d = R.uniform(0.04, 0.09); t = t_(d); f0 = R.uniform(*f)
        return np.sin(2 * np.pi * np.cumsum(f0 * (1 + 0.5 * t / d)) / SR) * env_ad(len(t), 0.001, d * 0.25) * R.uniform(0.4, 1.0)
    return esparcir(R, por_minuto, gota)

def lumbre(R, chispas=10, llama=0.6):
    """El fuego (el de `afinados`: el rumor que respira y las chispas)."""
    with A.azar_propio('lumbre%d' % R.integers(1 << 30)):
        return norm(fuego(LARGO + CRUCE, chispas, llama)[:M])

def trino(R, f0, f1, d, vibra=60.0, prof=0.04):
    t = t_(d); f = f0 + (f1 - f0) * (t / d) ** 0.7
    return np.sin(2 * np.pi * np.cumsum(f * (1 + prof * np.sin(2 * np.pi * vibra * t))) / SR) * np.sin(np.pi * t / d) ** 2

def pajaros(R, por_minuto=14, lejos_k=0.6, agudo=1.0):
    """Pájaros pequeños: frases de dos a seis notas que suben o bajan, con sus trinos."""
    def frase(R):
        partes = []; t = 0.0; base = R.uniform(2600, 4800) * agudo
        for _ in range(R.integers(2, 7)):
            d = R.uniform(0.04, 0.14); f0 = base * R.uniform(0.85, 1.2); f1 = f0 * R.uniform(0.7, 1.4)
            partes.append((t, trino(R, f0, f1, d, R.uniform(40, 90), R.uniform(0.0, 0.08)), R.uniform(0.5, 1.0))); t += d + R.uniform(0.02, 0.09)
        return A.mezclar(*partes) * R.uniform(0.3, 1.0)
    return lejos(esparcir(R, por_minuto, frase), lejos_k, 0.8)

def vencejos(R, por_minuto=6):
    """Los vencejos de Roma: chillidos agudos y rasposos que pasan en bandada."""
    def bandada(R):
        partes = []
        for _ in range(R.integers(3, 8)):
            d = R.uniform(0.25, 0.6); t = t_(d); f0 = R.uniform(5200, 6800)
            f = f0 * (1 + 0.06 * np.sin(2 * np.pi * R.uniform(25, 45) * t)) * (1 - 0.12 * t / d)
            s = np.sin(2 * np.pi * np.cumsum(f) / SR) * (0.6 + 0.4 * np.sign(np.sin(2 * np.pi * 70 * t))) * np.sin(np.pi * t / d) ** 1.5
            partes.append((R.uniform(0, 1.5), s, R.uniform(0.3, 1.0)))
        return A.mezclar(*partes) * R.uniform(0.4, 1.0)
    return lejos(esparcir(R, por_minuto, bandada), 0.8, 1.0)

def gaviotas(R, por_minuto=3):
    """Gaviotas: dos a cuatro gritos que bajan («kiau»), con sus armónicos."""
    def grito(R):
        partes = []; t = 0.0
        for _ in range(R.integers(1, 5)):
            d = R.uniform(0.28, 0.55); tt = t_(d); f0 = R.uniform(1300, 1900)
            f = f0 * (1 + 0.15 * np.minimum(1, tt / 0.05)) * (1 - 0.32 * tt / d)
            ph = 2 * np.pi * np.cumsum(f) / SR
            s = sum(np.sin(k * ph) / k for k in range(1, 7))
            s = banda(s, 900, 4500) * np.sin(np.pi * tt / d) ** 0.8
            partes.append((t, s, R.uniform(0.6, 1.0))); t += d + R.uniform(0.1, 0.3)
        return A.mezclar(*partes) * R.uniform(0.4, 1.0)
    return lejos(esparcir(R, por_minuto, grito), 1.0, 1.4)

def cigarras(R, voces=3):
    """Cigarras del verano: un zumbido agudo que late a unos 180 golpes por segundo y crece y mengua."""
    x = np.zeros(M); t = np.arange(M) / SR
    for _ in range(voces):
        pulso = 0.5 + 0.5 * np.tanh(4 * np.sin(2 * np.pi * R.uniform(160, 220) * t + R.uniform(0, 6)))
        x += banda(blanco(R), R.uniform(4000, 5000), R.uniform(6500, 8500)) * pulso * curva(R, R.uniform(5, 10), 0.0, 1.0) ** 2
    return norm(x)

def grillos(R, voces=3, lejos_k=0.4):
    """Grillos: cada uno con su tono (4 a 5 kHz) y su compás, tres o cuatro pulsos por canto."""
    x = np.zeros(M)
    for _ in range(voces):
        f = R.uniform(4000, 5200); cada = R.uniform(0.35, 0.7); pulsos = R.integers(2, 5); g = R.uniform(0.4, 1.0)
        d = 0.016; tt = t_(d); p = np.sin(2 * np.pi * f * tt) * np.sin(np.pi * tt / d) ** 2
        canto = A.mezclar(*[(k * 0.03, p, 1.0) for k in range(pulsos)])
        t = R.uniform(0, cada)
        callado = curva(R, 6.0, -0.4, 1.0)
        while t < LARGO + CRUCE:
            if callado[min(M - 1, int(t * SR))] > 0: poner(x, t, canto, g)
            t += cada * R.uniform(0.96, 1.04)
    return lejos(norm(x), lejos_k, 0.7)

def ranas(R, por_minuto=30):
    """Ranas del río: un croar grave que late a unos 30 golpes por segundo."""
    def croar(R):
        d = R.uniform(0.18, 0.4); t = t_(d); f = R.uniform(180, 320)
        am = 0.5 + 0.5 * np.sin(2 * np.pi * R.uniform(22, 38) * t) ** 2
        s = sum(np.sin(2 * np.pi * f * k * t) / k for k in range(1, 6)) * am * np.sin(np.pi * t / d)
        return banda(s, 150, 2200) * R.uniform(0.4, 1.0)
    return lejos(esparcir(R, por_minuto, croar), 0.8, 1.0)

def buho(R, por_minuto=1.2, f=400):
    """Un búho o un mochuelo a lo lejos: dos o tres ululatos que bajan un poco."""
    def ulular(R):
        partes = []; t = 0.0
        for k in range(R.integers(2, 4)):
            d = R.uniform(0.25, 0.7) if k else 0.3; tt = t_(d); f0 = f * R.uniform(0.92, 1.08)
            s = np.sin(2 * np.pi * np.cumsum(f0 * (1 - 0.06 * tt / d)) / SR) + 0.2 * np.sin(2 * np.pi * np.cumsum(2 * f0 * (1 - 0.06 * tt / d)) / SR)
            partes.append((t, s * np.sin(np.pi * tt / d) ** 1.2, 1.0)); t += d + R.uniform(0.15, 0.5)
        return A.mezclar(*partes) * R.uniform(0.5, 1.0)
    return lejos(esparcir(R, por_minuto, ulular, desde=3.0), 1.2, 1.6)

def mochuelo(R, por_minuto=2.0):
    """El mochuelo de Atenea: un «kiu» agudo y quejoso."""
    def kiu(R):
        d = R.uniform(0.35, 0.55); t = t_(d); f = 1300 * (1 + 0.25 * np.sin(np.pi * t / d)) * (1 - 0.15 * t / d)
        s = np.sin(2 * np.pi * np.cumsum(f) / SR) + 0.3 * np.sin(4 * np.pi * np.cumsum(f) / SR)
        return s * np.sin(np.pi * t / d) ** 1.5 * R.uniform(0.6, 1.0)
    return lejos(esparcir(R, por_minuto, kiu, desde=2.0), 1.0, 1.4)

def palomas(R, por_minuto=4):
    """Palomas: un arrullo grave de tres o cuatro sílabas («cu-cuuu-cu»)."""
    def arrullo(R):
        partes = []; t = 0.0
        for k in range(R.integers(3, 5)):
            d = R.uniform(0.18, 0.5); tt = t_(d); f0 = R.uniform(380, 480)
            f = f0 * (1 + 0.12 * np.sin(np.pi * tt / d)) * (1 + 0.01 * np.sin(2 * np.pi * 18 * tt))
            s = np.sin(2 * np.pi * np.cumsum(f) / SR) + 0.35 * np.sin(4 * np.pi * np.cumsum(f) / SR)
            partes.append((t, banda(s, 250, 1800) * np.sin(np.pi * tt / d), 1.0)); t += d + R.uniform(0.05, 0.15)
        return A.mezclar(*partes) * R.uniform(0.4, 1.0)
    return lejos(esparcir(R, por_minuto, arrullo), 0.5, 0.8)

def ruisenor(R, por_minuto=8):
    """Un ruiseñor de noche: silbidos limpios que se repiten y trinos rapidísimos."""
    def frase(R):
        partes = []; t = 0.0
        for _ in range(R.integers(2, 5)):
            if R.uniform() < 0.5:   # el silbido que se repite
                f0 = R.uniform(1800, 3200); d = R.uniform(0.12, 0.3)
                for _ in range(R.integers(3, 7)):
                    partes.append((t, trino(R, f0, f0 * R.uniform(0.95, 1.1), d, 0, 0), 0.8)); t += d + 0.06
            else:                   # el trino
                f0 = R.uniform(2500, 4500)
                for _ in range(R.integers(6, 14)):
                    partes.append((t, trino(R, f0, f0 * 0.6, 0.03, 0, 0), 0.6)); t += 0.045
            t += R.uniform(0.1, 0.3)
        return A.mezclar(*partes) * R.uniform(0.5, 1.0)
    return lejos(esparcir(R, por_minuto, frase), 0.5, 1.0)

def crujido(R, d=None, f=None):
    """Madera que cruje bajo peso: un roce de impulsos que se aceleran y frenan, resonando en la tabla."""
    d = d or R.uniform(0.25, 0.8); n = int(d * SR); t = np.arange(n) / SR
    tasa = (f or R.uniform(40, 110)) * (1 + 0.6 * np.sin(np.pi * t / d + R.uniform(-0.5, 0.5)))
    fase = np.cumsum(tasa) / SR
    imp = np.zeros(n); idx = np.nonzero(np.diff(np.floor(fase)) > 0)[0]; imp[idx] = R.uniform(0.5, 1.0, len(idx))
    s = A.resonador(imp, R.uniform(350, 800), 120) + 0.6 * A.resonador(imp, R.uniform(1200, 2200), 300)
    return s * np.sin(np.pi * t / d) ** 0.7

def crujidos(R, por_minuto=6, g=1.0):
    return esparcir(R, por_minuto, lambda R: crujido(R) * R.uniform(0.3, 1.0) * g)

def zumbido(R, f=120.0, armonicos=8, parpadeo=0.0):
    """El zumbido eléctrico (un tubo de neón, un transformador): la red y sus armónicos, apenas inestable."""
    t = np.arange(M) / SR
    x = sum(np.sin(2 * np.pi * f * k * t + R.uniform(0, 6)) / k ** 0.8 for k in range(1, armonicos + 1))
    x = alto(x, 200) * (1 + 0.04 * curva(R, 0.3, -1, 1))
    if parpadeo: x *= 1 - parpadeo * (curva(R, 0.08, 0, 1) > 0.93)
    return norm(x)

def trafico(R, pasan=3, lo=60, hi=900):
    """La ciudad a lo lejos: el rumor de fondo y unos coches que pasan (crecen y se van)."""
    x = norm(bajo(marron(R), 350)) * curva(R, 5.0, 0.6, 1.0)
    pase = np.zeros(M)
    for _ in range(pasan):
        d = R.uniform(4, 7); n = int(d * SR); tt = np.arange(n) / n
        s = banda(rosa(R, n), lo, hi) * np.exp(-((tt - 0.5) / 0.18) ** 2)
        poner(pase, R.uniform(0, LARGO + CRUCE - d), s, R.uniform(0.5, 1.0))
    return norm(x + 0.9 * norm(pase))

def motoneta(R):
    """Una motoneta que pasa por la calle de fuera: el motor de dos tiempos que sube y baja (efecto Doppler)."""
    d = R.uniform(4.5, 6.5); n = int(d * SR); tt = np.arange(n) / n
    f = R.uniform(95, 130) * (1 + 0.12 * np.tanh((0.5 - tt) * 6))
    ph = 2 * np.pi * np.cumsum(f) / SR
    s = sum(np.sin(k * ph) / k for k in range(1, 12)) + 0.4 * banda(R.standard_normal(n), 300, 2500)
    return banda(s, 150, 2500) * np.exp(-((tt - 0.5) / 0.2) ** 2)

def pasos(R, por_minuto=50, duro=True):
    """Pasos de gente que cruza: talón y punta, a su paso."""
    def paso(R):
        d = 0.06; n = int(d * SR)
        s = banda(R.standard_normal(n), 120 if duro else 80, 1800 if duro else 900) * env_ad(n, 0.002, 0.015)
        return A.mezclar((0, s, 1.0), (R.uniform(0.07, 0.11), s * 0.5, 1.0)) * R.uniform(0.3, 1.0)
    return esparcir(R, por_minuto, paso)

def murmullo(R, voces=6):
    """Gente que habla lejos, sin que se entienda nada: ruido con sílabas (4 a 6 por segundo) y sus formantes."""
    x = np.zeros(M); t = np.arange(M) / SR
    for _ in range(voces):
        sil = 0.5 + 0.5 * np.sin(2 * np.pi * R.uniform(3.5, 6) * t + R.uniform(0, 6)) ** 2
        habla = curva(R, R.uniform(1.5, 3.0), -0.5, 1.0).clip(0, 1)
        r = banda(blanco(R), 200, 3000)
        r = A.resonador(r, R.uniform(450, 700), 160) + 0.7 * A.resonador(r, R.uniform(1100, 1800), 260)
        x += r * sil * habla
    return lejos(norm(x), 1.5, 1.6)

def campanada(f, d=6.0, golpe=1.0):
    """Una campana grande de torre: su golpe y su zumbido que dura."""
    return A.mezclar((0, campana(f, d, 2.0, 1.6, 0.9), golpe), (0, campana(f * 2.4, d * 0.6, 1.5, 0.8, 1.6), 0.4))

def remos(R, periodo, entrada=0.45):
    """Una boga a compás: el agua que entra con la pala, el tolete que cruje y el goteo al sacarla."""
    def palada(R, k):
        d = 0.9; n = int(d * SR)
        golpe = banda(R.standard_normal(n), 300, 3800) * env_ad(n, 0.015, 0.16)
        cuerpo = bajo(R.standard_normal(n), 350) * env_ad(n, 0.02, 0.12)
        gotas = A.mezclar(*[(0.45 + R.uniform(0, 0.35), burbuja(R.uniform(900, 2200), 0.05), R.uniform(0.1, 0.3)) for _ in range(5)])
        tolete = crujido(R, 0.22, 90) * 0.35
        return A.mezclar((0, golpe, 1.0), (0, cuerpo, 0.7), (0.0, gotas, 1.0), (entrada - 0.25, tolete, 1.0)) * R.uniform(0.75, 1.0)
    return a_pulso(R, periodo, palada, juego=0.03)

def cincel(R):
    """El cincel sobre la piedra: el golpe de la maceta y el chasquido del filo."""
    d = 0.12; n = int(d * SR)
    filo = banda(R.standard_normal(n), 1800, 7000) * env_ad(n, 0.0005, 0.006)
    tono = campana(R.uniform(1500, 2600), d, 1.47, 0.6, 30)
    maceta = bajo(R.standard_normal(n), 700) * env_ad(n, 0.001, 0.012)
    return A.mezclar((0, filo, 1.0), (0, tono, 0.4), (0, maceta, 0.5))

def canteros(R, cuantos=3):
    """Canteros trabajando, cada uno a su ritmo y a su distancia: rachas de golpes con sus pausas."""
    x = np.zeros(M)
    for i in range(cuantos):
        capa = np.zeros(M); t = R.uniform(0, 3); paso = R.uniform(0.32, 0.55)
        while t < LARGO + CRUCE:
            for _ in range(R.integers(3, 9)):
                poner(capa, t, cincel(R), R.uniform(0.6, 1.0)); t += paso * R.uniform(0.92, 1.08)
            t += R.uniform(3.0, 9.0)
        x += lejos(capa, 0.3 + i * 0.6, 0.9) * (1.0 - 0.25 * i)
    return x

def tictac(R, f=(2400, 1900)):
    """Un reloj de pared: tic y tac, uno por segundo (el segundo cabe 45 veces en el bucle)."""
    def golpe(R, k):
        d = 0.05; n = int(d * SR)
        return (banda(R.standard_normal(n), f[k % 2] * 0.6, f[k % 2] * 1.6) * env_ad(n, 0.0005, 0.004)
                + 0.3 * campana(f[k % 2] * 0.5, d, 2.1, 0.4, 60))
    return sala(a_pulso(R, 1.0, golpe), 0.9, 0.3)

def pitidos(R, por_minuto=3):
    """Las consolas: dos o tres notas cortas y limpias."""
    def aviso(R):
        notas = R.choice([0, 3, 5, 7, 10, 12], R.integers(1, 4))
        return A.mezclar(*[(i * 0.11, A.pip(hz(12 + n), 0.07), 1.0) for i, n in enumerate(notas)]) * R.uniform(0.3, 0.8)
    return esparcir(R, por_minuto, aviso)

def maquina(R, f=58.0):
    """La sala de máquinas de la nave: zumbidos graves que baten entre sí, y la ventilación."""
    t = np.arange(M) / SR
    x = sum(a * np.sin(2 * np.pi * f * k * t * (1 + 0.0015 * i)) for i, (k, a) in enumerate(((1, 1), (1.5, 0.5), (3, 0.4), (6, 0.25), (9, 0.12))))
    x = alto(x, 90) * (0.85 + 0.15 * curva(R, 3.0))
    return norm(norm(x) + 1.3 * norm(bajo(rosa(R), 1300)))

def telar(R, periodo=1.5):
    """El telar de lizos de Zaynab, dentro de la casa: el batán que golpea la trama y la lanzadera que corre."""
    def pasada(R, k):
        d = 0.7; n = int(d * SR)
        batan = bajo(R.standard_normal(n), 500) * env_ad(n, 0.002, 0.04) + 0.5 * campana(180, d, 1.4, 0.4, 25)
        lanzadera = banda(R.standard_normal(int(0.25 * SR)), 800, 3500) * np.sin(np.linspace(0, np.pi, int(0.25 * SR)))
        lizos = crujido(R, 0.15, 120) * 0.4
        return A.mezclar((0, batan, 1.0), (0.3, lanzadera, 0.35), (0.55, lizos, 1.0))
    # Teje a rachas: unas pasadas y una pausa.
    capa = a_pulso(R, periodo, pasada, juego=0.04)
    return capa * (curva(R, 7.0, -0.3, 1.0).clip(0, 1) ** 0.5)

def noria(R, vuelta=9.0):
    """La noria del río, lejos: el eje que cruje en cada vuelta y los cangilones que vierten."""
    def giro(R, k):
        partes = [(0.0, crujido(R, 1.2, 55), 0.8)]
        for j in range(6): partes.append((0.4 + j * 1.4 + R.uniform(0, 0.2), chapoteo_corto(R), R.uniform(0.4, 0.8)))
        return A.mezclar(*partes)
    return lejos(a_pulso(R, vuelta, giro, juego=0.1), 1.3, 1.2)

def chapoteo_corto(R):
    d = R.uniform(0.3, 0.6); n = int(d * SR)
    return banda(R.standard_normal(n), 300, 3000) * env_ad(n, 0.03, d * 0.4)

def molienda(R, vuelta=1.0):
    """La moleta sobre la losa de pórfido: el color que se muele en círculos (un círculo por segundo)."""
    t = np.arange(M) / SR
    giro = 0.55 + 0.45 * np.sin(2 * np.pi * t / vuelta) ** 2
    x = banda(rosa(R), 300, 2600) * giro * (1 + 0.3 * np.abs(bajo(blanco(R), 25)))
    racha = curva(R, 6.0, -0.4, 1.0).clip(0, 1) ** 0.6
    return norm(x * racha)

def ruedas(R, por_minuto=1.5):
    """Un carro en el empedrado, lejos: el rodar y los golpecitos de las piedras, y los cascos del caballo."""
    def carro(R):
        d = R.uniform(5, 8); n = int(d * SR); tt = np.arange(n) / n
        rodar = banda(rosa(R, n), 80, 700) * (1 + 0.6 * np.abs(bajo(R.standard_normal(n), 18)))
        golpes = np.zeros(n); k = (R.uniform(0, 1, int(d * 25)) * (n - 1)).astype(int); golpes[k] = R.uniform(0.3, 1, len(k))
        golpes = banda(golpes, 400, 3000)
        cascos = np.zeros(n); t0 = 0.0
        while t0 < d - 0.1:
            for dt in (0.0, 0.09):
                i = int((t0 + dt) * SR)
                if i + 400 < n: cascos[i:i + 400] += banda(R.standard_normal(400), 500, 2500) * env_ad(400, 0.001, 0.006)
            t0 += 0.42 * R.uniform(0.95, 1.05)
        return (norm(rodar) + 0.6 * norm(golpes) + 0.8 * norm(cascos)) * np.exp(-((tt - 0.5) / 0.22) ** 2) * R.uniform(0.6, 1.0)
    return lejos(esparcir(R, por_minuto, carro), 1.5, 1.3)

def palas_de_lavar(R, mujeres=3):
    """Las palas de las lavanderas golpeando la ropa mojada, cada una a su ritmo, y el cepillo."""
    x = np.zeros(M)
    for i in range(mujeres):
        capa = np.zeros(M); t = R.uniform(0, 2); paso = R.uniform(0.45, 0.75)
        while t < LARGO + CRUCE:
            for _ in range(R.integers(5, 16)):
                d = 0.25; n = int(d * SR)
                golpe = bajo(R.standard_normal(n), 900) * env_ad(n, 0.002, 0.03) + 0.5 * banda(R.standard_normal(n), 900, 5000) * env_ad(n, 0.004, 0.06)
                poner(capa, t, golpe, R.uniform(0.6, 1.0)); t += paso * R.uniform(0.93, 1.07)
            t += R.uniform(1.5, 4.0)
        x += lejos(capa, 0.2 + i * 0.6, 0.8) * (1 - 0.2 * i)
    cepillo = banda(blanco(R), 1500, 6000) * (0.5 + 0.5 * np.sin(2 * np.pi * 2.6 * np.arange(M) / SR) ** 2) * curva(R, 5.0, -0.5, 1.0).clip(0, 1)
    return norm(x) + 0.25 * norm(cepillo)

def cascabeles(R, por_minuto=8):
    """Las campanas de bronce de los camellos: graves, sordas, a racimos cuando el animal se mueve."""
    def racimo(R):
        f = R.uniform(320, 620)
        return A.mezclar(*[(j * R.uniform(0.18, 0.45), campana(f * R.uniform(0.98, 1.02), 1.4, 1.41, 1.2, 2.4), R.uniform(0.4, 1.0)) for j in range(R.integers(1, 5))]) * R.uniform(0.4, 1.0)
    return lejos(esparcir(R, por_minuto, racimo), 0.6, 1.0)

def toldo(R):
    """La tela de un toldo o una vela que el viento sacude: golpes graves y desparejos."""
    am = np.abs(bajo(blanco(R), 9)); am /= am.max() + 1e-9
    return norm(banda(rosa(R), 60, 500) * am ** 3 * curva(R, 3.0, 0.2, 1.0))

def vapor_siseo(R):
    """El vapor que escapa de la máquina parada: un siseo que crece y afloja, y alguna purga más fuerte."""
    x = norm(alto(blanco(R), 2500)) * curva(R, 4.0, 0.3, 0.8)
    purgas = np.zeros(M)
    for _ in range(2):
        d = R.uniform(2.0, 3.5); n = int(d * SR); tt = np.arange(n) / n
        poner(purgas, R.uniform(2, LARGO + CRUCE - d), banda(R.standard_normal(n), 1500, 9000) * np.minimum(1, tt / 0.05) * np.exp(-tt * 2.5))
    return norm(x + 1.4 * norm(purgas))

def bomba_de_aire(R, periodo=3.0):
    """La bomba de aire de la locomotora: un «chu… chu» doble, cada tres segundos (cabe quince veces en el bucle)."""
    def chu(R, k):
        d = 0.35; n = int(d * SR)
        s = banda(R.standard_normal(n), 180, 1800) * env_ad(n, 0.01, 0.1)
        return A.mezclar((0, s, 1.0), (0.42, s * 0.7, 1.0)) * R.uniform(0.7, 1.0)
    return a_pulso(R, periodo, chu, juego=0.05)

def fragua(R):
    """La fragua del depósito: el fuelle que sopla y el martillo en el yunque, a rachas."""
    capa = np.zeros(M); t = R.uniform(1, 4)
    while t < LARGO + CRUCE - 3:
        for _ in range(R.integers(3, 9)):
            with A.azar_propio('yunque%d' % R.integers(1 << 30)):
                poner(capa, t, yunque(R.uniform(800, 1100), 0.6, R.uniform(0.6, 1.0)), R.uniform(0.6, 1.0))
            t += R.uniform(0.38, 0.5)
        t += R.uniform(3, 8)
    fuelle = banda(rosa(R), 100, 800) * (0.5 + 0.5 * np.sin(2 * np.pi * np.arange(M) / SR / 2.5) ** 2) * curva(R, 6.0, 0.2, 1.0)
    return norm(capa) + 0.3 * norm(fuelle)

def topes(R, por_minuto=1.5):
    """Vagones que se tocan al maniobrar, lejos: una fila de golpes metálicos que se va apagando."""
    def fila(R):
        partes = []
        for j in range(R.integers(3, 7)):
            d = 0.8; n = int(d * SR)
            g = bajo(R.standard_normal(n), 1200) * env_ad(n, 0.002, 0.05) + 0.4 * campana(R.uniform(250, 420), d, 2.3, 1.0, 5)
            partes.append((j * R.uniform(0.25, 0.5), g, 0.9 ** j))
        return A.mezclar(*partes) * R.uniform(0.5, 1.0)
    return lejos(esparcir(R, por_minuto, fila, desde=4.0), 1.6, 1.6)

def silbato_lejos(R, T=0):
    """El silbato de tres campanas (La, Do, Fa) de una locomotora que sale, lejos."""
    d = 1.6; t = t_(d); notas = [T, T + 3, T + 8]
    x = sum(np.sin(2 * np.pi * hz(n) * t) + 0.3 * np.sin(4 * np.pi * hz(n) * t) for n in notas) / 3
    x += 0.3 * banda(R.standard_normal(len(t)), hz(T) * 0.9, hz(T + 8) * 3)
    return x * np.minimum(1, t / 0.08) * np.minimum(1, (d - t) / 0.3)

def tranvia(R):
    """El tranvía que pasa por la avenida: el rodar que crece, el chirrido de la curva y su campana."""
    d = 9.0; n = int(d * SR); tt = np.arange(n) / n
    rodar = banda(rosa(R, n), 50, 600) * np.exp(-((tt - 0.5) / 0.22) ** 2)
    chirrido = np.sin(2 * np.pi * np.cumsum(2900 + 250 * np.sin(2 * np.pi * 0.7 * np.arange(n) / SR)) / SR) * np.exp(-((tt - 0.58) / 0.05) ** 2)
    timbre = A.mezclar((0, campanita(1500, 1.2, 2.2, 1.0, 4), 1.0), (0.22, campanita(1500, 1.2, 2.2, 1.0, 4), 0.9))
    return A.mezclar((0, norm(rodar), 1.0), (0, chirrido, 0.05), (d * 0.3, timbre, 0.12))

def altavoz_lejano(R):
    """La megafonía de la plaza, lejos: tres notas de aviso y una voz que no se entiende."""
    aviso = A.mezclar((0, campanita(hz(-2), 1.4, 2.0, 0.8, 3), 1.0), (0.45, campanita(hz(2), 1.4, 2.0, 0.8, 3), 1.0), (0.9, campanita(hz(-5), 2.0, 2.0, 0.8, 2.5), 1.0))
    d = 5.0; n = int(d * SR); t = np.arange(n) / SR
    voz = banda(R.standard_normal(n), 250, 2600)
    voz = A.resonador(voz, 600, 180) + 0.7 * A.resonador(voz, 1300, 280)
    voz *= (0.5 + 0.5 * np.sin(2 * np.pi * 4.6 * t) ** 2) * np.minimum(1, t / 0.2) * np.minimum(1, (d - t) / 0.3)
    return A.mezclar((0, aviso, 0.5), (2.6, norm(voz) * 0.2, 1.0))

def bocina_bici(R):
    return A.mezclar((0, campanita(2350, 0.7, 2.76, 1.0, 6), 1.0), (0.16, campanita(2350, 0.7, 2.76, 1.0, 6), 0.9))

def olla(R):
    """La olla grande de los fideos: el hervor (burbujas graves) y su rumor."""
    b = esparcir(R, 900, lambda R: burbuja(R.uniform(140, 420), R.uniform(0.04, 0.1)) * R.uniform(0.2, 1.0))
    return norm(b + 0.5 * norm(bajo(rosa(R), 400)) * rms(b))

def dron(R, f=55.0):
    """Algo que no se ve, debajo de todo: dos tonos graves que baten despacio (el mar del pueblo de Arcano)."""
    t = np.arange(M) / SR
    x = sum(np.sin(2 * np.pi * f * k * t * (1 + 0.004 * k)) / k for k in (1, 1.5, 2, 3, 4))
    return norm(alto(x, 70) * curva(R, 8.0, 0.3, 1.0))

def boya(R, por_minuto=3):
    """La campana de una boya que el mar mece: golpes sueltos e irregulares, lejos."""
    return lejos(esparcir(R, por_minuto, lambda R: campana(hz(-14), 3.5, 2.76, 1.4, 1.1) * R.uniform(0.4, 1.0)), 1.6, 2.0)

def jarcia(R):
    """El viento en la jarcia de un balandro: silbidos que cambian de tono."""
    t = np.arange(M) / SR
    x = np.zeros(M)
    for _ in range(3):
        f = R.uniform(500, 1300) * (1 + 0.1 * curva(R, 2.0, -1, 1))
        x += np.sin(2 * np.pi * np.cumsum(f) / SR) * curva(R, 3.0, 0, 1) ** 4 * 0.3
    return norm(x + 2.0 * norm(banda(rosa(R), 300, 1500)) * curva(R, 3.0, 0.2, 1.0))

def campana_de_a_bordo(R):
    """La campana del barco que da la media hora: dos golpes."""
    c = lambda: campana(hz(9), 2.5, 2.76, 1.6, 1.8)
    return A.mezclar((0, c(), 1.0), (0.35, c(), 0.9))

def cornu(R):
    """Un toque de cornu en el campamento, lejos (la llamada de la guardia): tres notas del modo del tema."""
    T = hz(-17)   # en Romano (Re), una octava abajo
    return A.mezclar((0, metal(T, 0.7, 2500, 0.05), 1.0), (0.75, metal(T * 1.5, 0.6, 2500, 0.05), 1.0), (1.4, metal(T * 2, 1.4, 2500, 0.05), 1.0))

def aulos(R):
    """El aulós que marca la boga desde la popa, lejos: una vuelta corta en la pentatónica de Helénico."""
    T = -12
    notas = [T + 12, T + 16, T + 19, T + 16, T + 12, T + 9, T + 12]
    return A.mezclar(*[(i * 0.4, flauta(hz(n), 0.42), 0.6) for i, n in enumerate(notas)])

def completas(R):
    """El canto de Completas, lejos, desde la iglesia: una línea llana del modo de Re, a varias voces."""
    T = -19
    linea = [0, 2, 3, 2, 0, -2, 0, 3, 5, 3, 2, 0]
    return reverb(A.mezclar(*[(i * 0.9, coro(hz(T + n), 1.1), 0.5) for i, n in enumerate(linea)]), 3.0, 0.5, 1800)

# ------------------------------------------------------------------ el bucle y la escritura

def sonando(x):
    """El volumen de una capa mientras suena: el RMS de sus tramos de 50 ms que pasan del 10 % del más fuerte. Para un
    fondo continuo es su RMS; para unos gaviotas sueltas, lo fuerte que suenan cuando gritan (no su promedio, casi mudo)."""
    v = int(0.05 * SR); x = np.asarray(x)[:len(x) // v * v].reshape(-1, v)
    f = np.sqrt((x ** 2).mean(axis=1)); f = f[f > 0.1 * f.max()]
    return float(np.sqrt((f ** 2).mean())) + 1e-12 if len(f) else 1.0

def mezcla(*capas):
    """Capas (señal, dB): cada una suena (mientras suena, [sonando]) a esos dB del fondo, que es la que va a 0 dB."""
    x = np.zeros(M)
    for s, g in capas:
        if s is None: continue
        s = np.asarray(s, dtype=np.float64)[:M]
        if len(s) < M: s = np.concatenate([s, np.zeros(M - len(s))])
        if not np.any(s): continue
        x += s / sonando(s) * db(g)
    return x

def en_bucle(x):
    """Los últimos CRUCE s se funden con los primeros, a potencia constante: el final sigue al principio."""
    x = np.asarray(x[:M], dtype=np.float64)
    k = M - N
    w = np.arange(k) / k
    y = x[:N].copy()
    y[:k] = x[:k] * np.sin(0.5 * np.pi * w) + x[N:N + k] * np.cos(0.5 * np.pi * w)
    return y

def limitar(y, techo):
    """Un limitador de picos: la ganancia baja justo antes de un pico (mira 5 ms adelante) y vuelve en unos 60 ms. Trata
    la señal como un bucle (el final sigue al principio), para no abrir la costura."""
    from scipy.ndimage import maximum_filter1d, minimum_filter1d
    adelante = int(0.005 * SR); k = int(0.06 * SR)
    env = maximum_filter1d(np.abs(y), 2 * adelante + 1, mode='wrap')
    g = minimum_filter1d(np.minimum(1.0, techo / (env + 1e-12)), 2 * adelante + 1, mode='wrap')
    nucleo = np.hanning(2 * k + 1); nucleo /= nucleo.sum()
    suave = np.convolve(np.concatenate([g[-k:], g, g[:k]]), nucleo, mode='same')[k:k + len(g)]
    return y * np.minimum(g, suave)

def nivelar(y, rms_db=RMS_DB, pico=PICO):
    """Al volumen de los ambientes ([rms_db]) con los picos por debajo de [pico]: se alternan el nivel y el limitador
    hasta que los dos se cumplen (el limitador solo toca los golpes sueltos: el fondo casi no cambia)."""
    y = np.asarray(y, dtype=np.float64); y = y - y.mean()
    # Sin lo de debajo de 90 Hz: el altavoz de un teléfono no lo da, y contaría en el volumen sin oírse. Se filtra el
    # bucle puesto tres veces seguidas y se queda el del medio: el filtro también ve que el final sigue al principio.
    n = len(y); y = alto(np.concatenate([y, y, y]), 90, 2)[n:2 * n]
    for _ in range(6):
        y = y * (db(rms_db) / rms(y))
        if np.abs(y).max() <= pico * 1.02: break
        y = limitar(y, pico * 0.97)
    return np.clip(y, -0.98, 0.98)

def escribir_ogg(nombre, y, salida, calidad=1):
    with tempfile.NamedTemporaryFile(suffix='.wav', delete=False) as f: ruta = f.name
    with wave.open(ruta, 'wb') as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes((np.clip(y, -1, 1) * 32767).astype(np.int16).tobytes())
    destino = os.path.join(salida, nombre + '.ogg')
    subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', ruta, '-c:a', 'libvorbis', '-q:a', str(calidad), destino], check=True)
    os.remove(ruta)
    return destino

def leer_ogg(ruta):
    r = subprocess.run(['ffmpeg', '-loglevel', 'error', '-i', ruta, '-f', 's16le', '-ac', '1', '-ar', str(SR), '-'], check=True, capture_output=True)
    return np.frombuffer(r.stdout, dtype=np.int16).astype(np.float64) / 32768

def medir(ruta, bucle=True):
    """Duración, pico, RMS y, si es un bucle, la costura (en el archivo ya codificado y vuelto a leer)."""
    y = leer_ogg(ruta)
    d = {'s': len(y) / SR, 'pico_db': 20 * np.log10(np.abs(y).max() + 1e-12), 'rms_db': 20 * np.log10(rms(y)),
         'kb': os.path.getsize(ruta) / 1024}
    if bucle:
        saltos = np.abs(np.diff(y))
        d['salto'] = abs(y[0] - y[-1]) / (np.percentile(saltos, 99) + 1e-12)           # < 1: menor que el 1 % mayor de los saltos normales
        # El fondo de los últimos 2 s contra el de los primeros 2 s: la mediana de sus tramos de 50 ms (los sucesos
        # sueltos, una gaviota, no cuentan).
        v = int(0.05 * SR); h = 40 * v
        fondo = lambda z: np.median(np.sqrt((z.reshape(-1, v) ** 2).mean(axis=1))) + 1e-12
        d['vol_db'] = 20 * np.log10(fondo(y[-h:]) / fondo(y[:h]))
        # Lo mismo en 40 puntos al azar de dentro del archivo: cuánto cambia el fondo en 4 s cualesquiera (el 90 %).
        Rm = np.random.default_rng(7); pts = Rm.integers(h, len(y) - h, 40)
        d['vol_tipico'] = float(np.percentile([abs(20 * np.log10(fondo(y[p - h:p]) / fondo(y[p:p + h]))) for p in pts], 90))
        # Lo agudo (sobre 5 kHz) en los 10 ms alrededor de la costura contra el 95 % de los tramos de 10 ms del archivo
        # (con el archivo puesto en bucle: la costura queda en medio). Menos de 1: la costura no tiene más agudos que
        # la mayoría de los tramos; un chasquido daría varias veces más.
        w = int(0.01 * SR)
        rodeo = alto(np.concatenate([y[-SR:], y, y[:SR]]), 5000, 4)
        cos = SR + len(y)
        centro = rms(rodeo[cos - w // 2:cos + w // 2])
        tramos = rodeo[SR:SR + len(y) // w * w].reshape(-1, w)
        d['clic'] = centro / (np.percentile(np.sqrt((tramos ** 2).mean(axis=1)), 95) + 1e-12)
    return d

# ------------------------------------------------------------------ los ambientes, tema por tema

def romano(R, noche):
    # El Danubio desde el camino de ronda de Viminacium: el río ancho, el viento en la empalizada, la lumbre del puesto.
    if noche:
        return mezcla((rio(R, 700, 25), 0), (viento(R, 150, 600, 4.0, 0.1), -9), (lumbre(R, 8, 0.6), -11), (grillos(R, 4, 0.6), -14),
                      (ranas(R, 26), -14), (buho(R, 1.4, 380), -12), (crujidos(R, 2), -20))
    return mezcla((rio(R, 900, 30), 0), (viento(R, 180, 800, 3.0, 0.2), -7), (lumbre(R, 10, 0.5), -13), (pajaros(R, 12, 0.8), -10),
                  (lejos(fragua(R), 2.0, 1.4), -18), (lejos(A.mezclar((14.0, cornu(R), 1.0)), 2.2, 2.0), -16), (crujidos(R, 3), -20))

def urbe(R, noche):
    # La capilla de los frescos en Roma: la sala, el bisturí que raspa el revoque, los vencejos y la ciudad de fuera.
    def raspar(R):
        return A.mezclar(*[(j * R.uniform(0.09, 0.16), trazo((2500, 8000), R.uniform(0.05, 0.12), 18, 0.04), R.uniform(0.4, 1.0)) for j in range(R.integers(2, 7))])
    cuarto = norm(bajo(rosa(R), 300))
    if noche:
        return mezcla((cuarto, 0), (lejos(trafico(R, 2), 1.8, 1.8), -1), (lejos(esparcir(R, 1.5, lambda R: motoneta(R)), 1.6, 1.6), -6),
                      (lejos(A.mezclar((20.0, campanada(hz(-21), 7.0), 1.0)), 1.6, 2.2), -8), (crujidos(R, 1, 0.6), -18))
    return mezcla((cuarto, 0), (lejos(trafico(R, 3), 1.6, 1.6), 0), (lejos(murmullo(R, 4), 1.0, 1.2), -10), (sala(esparcir(R, 9, raspar), 1.6, 0.35), -12),
                  (vencejos(R, 6), -10), (lejos(esparcir(R, 1.2, lambda R: motoneta(R)), 1.5, 1.5), -7),
                  (lejos(A.mezclar((26.0, campanada(hz(-14), 6.0), 1.0), (28.5, campanada(hz(-14), 6.0), 0.8)), 1.6, 2.2), -9), (crujidos(R, 2, 0.6), -18))

def neon(R, noche):
    # El callejón del nivel −3: la lluvia en los toldos, las gotas sobre chapa, el zumbido de los letreros, la olla del puesto.
    return mezcla((lluvia(R, 600, 700, 7000), 0), (goteo(R, 40, (1800, 4000)), -12), (zumbido(R, 120.0, 8, 0.6), -17), (olla(R), -11),
                  (lejos(trafico(R, 4, 80, 1200), 1.2, 1.4), -5), (lejos(esparcir(R, 1.2, bocina_bici), 0.6, 1.0), -16), (murmullo(R, 4), -16))

def heleno(R, noche):
    # El puerto de Zea desde el patio de Demeas: el agua de la dársena, una trirreme que boga con su aulós, gaviotas, cigarras.
    if noche:
        return mezcla((chapoteo(R, (1.5, 3.5), 2200), 0), (oleaje(R, (7, 11), 0.25, 400), -6), (grillos(R, 4, 0.5), -10), (mochuelo(R, 2.0), -12),
                      (viento(R, 150, 600, 5.0, 0.05), -12))
    boga = lejos(remos(R, 2.5), 1.4, 1.4) + 0.25 * lejos(a_pulso(R, 15.0, lambda R, k: aulos(R), desde=2.0), 1.6, 1.8)
    return mezcla((chapoteo(R, (1.2, 3.0), 2600), 0), (oleaje(R, (6, 10), 0.3, 450), -7), (boga, -6), (gaviotas(R, 4), -9),
                  (cigarras(R, 3), -17), (viento(R, 200, 900, 3.0, 0.15), -11))

def medieval(R, noche):
    # El scriptorium sobre el Rin: la sala de piedra, la pluma que raspa el pergamino, la hoja que se pasa, los pájaros del claustro.
    cuarto = norm(bajo(rosa(R), 350))
    if noche:
        return mezcla((cuarto, 0), (lejos(viento(R, 150, 700, 5.0, 0.2), 1.0, 1.4), -1), (lejos(a_pulso(R, 45.0, lambda R, k: completas(R), desde=6.0), 2.0, 2.5), -5),
                      (grillos(R, 2, 1.2), -16), (buho(R, 1.2), -12))
    def escribir(R):
        return sala(A.mezclar(*[(j * R.uniform(0.12, 0.3), trazo((2000, 5000), R.uniform(0.08, 0.3), 26, 0.12), R.uniform(0.5, 1.0)) for j in range(R.integers(3, 10))]), 1.2, 0.25)
    pluma = esparcir(R, 14, escribir)
    hoja = sala(esparcir(R, 1.5, lambda R: papel(0.35, (500, 4000))), 1.2, 0.3)
    return mezcla((cuarto, 0), (lejos(viento(R, 200, 800, 4.0, 0.2), 1.0, 1.2), -1), (pluma, -9), (hoja, -12), (lejos(pajaros(R, 14, 0.5), 1.0, 1.0), -8),
                  (lejos(A.mezclar((18.0, campanada(hz(-19), 6.0), 1.0)), 1.4, 2.2), -7))

def distopia(R, noche):
    # La plaza al pie del ministerio: viento entre bloques, pasos, el tranvía, la megafonía lejana.
    if noche:
        return mezcla((viento(R, 140, 700, 4.0, 0.35), 0), (zumbido(R, 100.0, 6), -16), (lejos(trafico(R, 1, 50, 500), 1.6, 1.6), -6),
                      (pasos(R, 6), -16), (lejos(A.mezclar((22.0, altavoz_lejano(R), 1.0)), 2.2, 2.6), -12))
    return mezcla((viento(R, 160, 800, 3.5, 0.3), 0), (sala(pasos(R, 60), 1.2, 0.3), -8), (murmullo(R, 5), -12), (lejos(trafico(R, 2), 1.4, 1.4), -5),
                  (lejos(A.mezclar((8.0, tranvia(R), 1.0)), 1.0, 1.4), -4), (lejos(A.mezclar((30.0, altavoz_lejano(R), 1.0)), 2.0, 2.4), -10))

def vapor(R, noche):
    # El patio del depósito: el vapor que sisea, la bomba de aire, la fragua y su yunque, la lluvia fina del norte.
    if noche:
        return mezcla((lluvia(R, 500, 700, 7000), 0), (vapor_siseo(R), -9), (lejos(bomba_de_aire(R), 0.8, 1.2), -12), (goteo(R, 30, (1200, 2800)), -12),
                      (topes(R, 1.5), -10))
    return mezcla((lluvia(R, 280, 800, 6000), -4), (vapor_siseo(R), -5), (lejos(bomba_de_aire(R), 0.6, 1.0), -8), (lejos(fragua(R), 1.0, 1.2), -6),
                  (topes(R, 1.0), -10), (lejos(A.mezclar((33.0, silbato_lejos(R), 1.0)), 2.0, 2.4), -12))

def ruinas(R, noche):
    # La azotea con su huerto sobre la ciudad que el bosque recuperó: el viento en las copas, muchos pájaros, el río lejos.
    if noche:
        return mezcla((grillos(R, 6, 0.4), 0), (ranas(R, 40), -5), (lejos(rio(R, 700, 10), 2.0, 1.6), -6), (buho(R, 2.0), -5),
                      (viento(R, 150, 700, 5.0, 0.1), -9), (hojas(R, 0.6), -14))
    zumbar = esparcir(R, 6, lambda R: (lambda d: banda(np.sign(np.sin(2 * np.pi * np.cumsum(np.full(int(d * SR), R.uniform(190, 250))) / SR)), 200, 2000)
                                       * np.sin(np.linspace(0, np.pi, int(d * SR))) ** 2 * R.uniform(0.2, 0.6))(R.uniform(1.0, 2.5)))
    return mezcla((viento(R, 180, 800, 3.0, 0.15), -2), (hojas(R, 1.0), -6), (pajaros(R, 30, 0.4), 0), (pajaros(R, 12, 1.2, 0.7), -4),
                  (lejos(rio(R, 800, 10), 2.0, 1.6), -9), (zumbar, -16))

def seda(R, noche):
    # El campamento de la caravana junto al caravasar: el viento de la estepa, el toldo de fieltro, las campanas de los camellos, la lumbre.
    if noche:
        return mezcla((viento(R, 140, 700, 5.0, 0.2), 0), (lumbre(R, 12, 0.7), -4), (cascabeles(R, 3), -8), (grillos(R, 2, 0.8), -14), (toldo(R), -14))
    return mezcla((viento(R, 160, 900, 3.0, 0.3), 0), (toldo(R), -8), (cascabeles(R, 9), -5), (lumbre(R, 8, 0.5), -10),
                  (lejos(pasos(R, 20, duro=False), 1.0, 1.0), -14), (pajaros(R, 4, 1.0, 0.8), -14))

def gotico(R, noche):
    # El patio de la logia de los canteros en Amiens: los cinceles en la piedra, la grúa de rueda que cruje, las grajillas.
    if noche:
        return mezcla((viento(R, 150, 700, 4.0, 0.2), 0), (lejos(rio(R, 700, 15), 1.6, 1.4), -6), (lejos(a_pulso(R, 3.0, lambda R, k: chapoteo_corto(R)), 1.6, 1.4), -10),
                      (buho(R, 1.5), -8), (grillos(R, 3, 0.8), -12))
    def grajilla(R):
        return A.mezclar(*[(j * R.uniform(0.25, 0.5), banda(trino(R, R.uniform(900, 1300), R.uniform(700, 900), 0.12, 0, 0) + 0.5 * A.ruido(0.12) * 0.1, 500, 4000), 1.0)
                           for j in range(R.integers(1, 4))]) * R.uniform(0.4, 1.0)
    grua = esparcir(R, 3, lambda R: A.mezclar(*[(j * 0.9, crujido(R, 0.8, 45), 0.8) for j in range(R.integers(2, 5))]))
    return mezcla((viento(R, 180, 800, 3.0, 0.2), 0), (canteros(R, 2), -5), (lejos(grua, 0.6, 1.0), -7), (lejos(esparcir(R, 5, grajilla), 1.0, 1.4), -8),
                  (lejos(A.mezclar((20.0, campanada(hz(-17), 6.0), 1.0)), 1.6, 2.2), -8))

def arcano(R, noche):
    # La galería del archivo sobre la bahía: el reloj de pared, el mar verde gris, las gaviotas pocas, algo grave debajo.
    if noche:
        return mezcla((oleaje(R, (8, 12), 0.25, 350), 0), (viento(R, 120, 600, 5.0, 0.35), -6), (tictac(R), -10), (boya(R, 3), -8),
                      (dron(R, 49.0), -12), (crujidos(R, 2, 0.6), -18))
    return mezcla((oleaje(R, (7, 11), 0.3, 400), 0), (viento(R, 150, 700, 4.0, 0.25), -8), (tictac(R), -11), (gaviotas(R, 2), -12),
                  (dron(R, 55.0), -16), (crujidos(R, 1.5, 0.6), -18))

def espacio(R, noche):
    # El puesto de la ingeniera en el anillo de la nave: la ventilación, la máquina que zumba, el agua del cultivo, las consolas.
    agua = fuente(R, 250)
    if noche:   # la luz tenue de la nave: menos aviso y la máquina más baja
        return mezcla((maquina(R, 55.0), 0), (agua, -14), (pitidos(R, 0.8), -20))
    return mezcla((maquina(R, 58.0), 0), (agua, -7), (pitidos(R, 3), -9), (lejos(esparcir(R, 1.0, lambda R: banda(rosa(R, int(4 * SR)), 200, 1500) * np.sin(np.linspace(0, np.pi, int(4 * SR))) ** 2), 0.4, 0.8), -10))

def andalus(R, noche):
    # El patio de Zaynab en Córdoba: la fuente, el telar dentro de la casa, las palomas y las golondrinas, la noria del río.
    if noche:
        return mezcla((fuente(R, 900), 0), (grillos(R, 5, 0.5), -4), (ruisenor(R, 6), -6), (ranas(R, 15), -14), (lejos(noria(R), 1.0, 1.0), -14))
    golondrinas = pajaros(R, 14, 0.6, 1.25)
    return mezcla((fuente(R, 900), 0), (sala(telar(R), 1.0, 0.3), -6), (palomas(R, 5), -5), (golondrinas, -5), (lejos(noria(R), 0.8, 1.0), -8),
                  (lejos(murmullo(R, 3), 1.0, 1.0), -18))

def renacimiento(R, noche):
    # El taller de Florencia: la moleta que muele el color, la calle por la ventana (carros y gente), las palomas, la campana del Duomo.
    cuarto = norm(bajo(rosa(R), 320))
    if noche:
        return mezcla((cuarto, 0), (lejos(grillos(R, 3, 1.0), 1.0, 1.0), -4), (lejos(A.mezclar((12.0, campanada(hz(-24), 8.0), 1.0), (16.0, campanada(hz(-24), 8.0), 0.9)), 1.6, 2.6), -4),
                      (lejos(buho(R, 1.0), 1.0, 1.0), -10), (crujidos(R, 1, 0.5), -18))
    return mezcla((cuarto, 0), (lejos(murmullo(R, 4), 1.0, 1.0), -3), (sala(molienda(R), 1.0, 0.25), -6), (lejos(ruedas(R, 2.0), 1.2, 1.2), -4),
                  (lejos(pasos(R, 20), 1.2, 1.0), -12), (palomas(R, 4), -8), (lejos(A.mezclar((24.0, campanada(hz(-24), 8.0), 1.0)), 1.6, 2.6), -6))

def pirata(R, noche):
    # La cubierta del balandro en mar abierto: el agua contra el casco, la jarcia que silba, la madera que cruje, la campana de a bordo.
    casco = chapoteo(R, (0.8, 2.0), 3000) + 0.8 * oleaje(R, (4, 7), 0.4, 600)
    if noche:
        return mezcla((casco, 0), (jarcia(R), -10), (crujidos(R, 8), -9), (toldo(R), -16), (lejos(A.mezclar((30.0, campana_de_a_bordo(R), 1.0)), 0.6, 1.4), -10))
    return mezcla((casco, 0), (jarcia(R), -7), (crujidos(R, 10), -8), (toldo(R), -11), (gaviotas(R, 2), -13),
                  (lejos(A.mezclar((30.0, campana_de_a_bordo(R), 1.0)), 0.6, 1.4), -10))

def revolucion(R, noche):
    # La popa del barco-lavadero en el Sena: el agua contra el casco, las palas de las lavanderas, los carros del puente, Notre-Dame.
    agua = chapoteo(R, (1.0, 2.5), 2400)
    if noche:
        return mezcla((agua, 0), (lejos(rio(R, 600, 8), 1.6, 1.4), -6), (lejos(ruedas(R, 0.8), 1.6, 1.4), -8),
                      (lejos(A.mezclar((14.0, campanada(hz(-26), 9.0), 1.0)), 1.8, 2.8), -6), (crujidos(R, 2, 0.6), -16))
    return mezcla((agua, 0), (palas_de_lavar(R, 3), -4), (lejos(ruedas(R, 2.5), 1.4, 1.4), -6), (lejos(murmullo(R, 5), 1.2, 1.2), -10),
                  (lejos(A.mezclar((36.0, campanada(hz(-26), 9.0), 1.0)), 1.8, 2.8), -8), (crujidos(R, 2, 0.6), -16))

# Los temas con escena hoy (`escenaDe` en `EscenaVida.kt`), y si su ambiente tiene versión de noche. Ciudad neón no: su
# callejón es siempre de noche (`RegimenLuz.SIEMPRE_NOCHE`). La de Espacio es la «luz tenue» de la nave.
TEMAS = {
    'romano': (romano, True), 'urbe': (urbe, True), 'neon': (neon, False), 'heleno': (heleno, True), 'medieval': (medieval, True),
    'distopia': (distopia, True), 'vapor': (vapor, True), 'ruinas': (ruinas, True), 'seda': (seda, True), 'gotico': (gotico, True),
    'arcano': (arcano, True), 'espacio': (espacio, True), 'andalus': (andalus, True), 'renacimiento': (renacimiento, True),
    'pirata': (pirata, True), 'revolucion': (revolucion, True),
}

# ------------------------------------------------------------------ el ronroneo

def ronroneo():
    """El gato que ronronea: espiración (0,85 s) e inspiración (0,65 s), cada una con sus golpes a unos 25 por segundo."""
    R = np.random.default_rng(2508)
    partes = []
    for ini, d, f, g in ((0.0, 0.85, 25.0, 1.0), (0.92, 0.65, 27.0, 0.7)):
        n = int(d * SR); t = np.arange(n) / SR
        # Cada golpe: un impulso con su caída (la laringe que se cierra y se abre), a [f] por segundo, un poco irregular.
        fase = np.cumsum(f * (1 + 0.04 * np.sin(2 * np.pi * 1.3 * t) + 0.02 * R.standard_normal(n).cumsum() / np.sqrt(n))) / SR
        golpe = (np.sin(np.pi * (fase % 1.0)) ** 6)
        cuerpo = norm(banda(rosa(R, n), 180, 650)) * golpe
        grave = norm(banda(rosa(R, n), 50, 150, 3)) * golpe
        aire = norm(banda(R.standard_normal(n), 600, 2500)) * (0.4 + 0.6 * golpe) * 0.12
        s = (0.9 * cuerpo + 0.8 * grave + aire) * np.sin(np.pi * t / d) ** 0.6
        partes.append((ini, s, g))
    x = A.mezclar(*partes)
    return x

# ------------------------------------------------------------------

if __name__ == '__main__':
    salida = sys.argv[1]
    pedidos = sys.argv[2].split(',') if len(sys.argv) > 2 and not sys.argv[2].startswith('--') and sys.argv[2] != 'todos' else None
    filas = []
    for tema, (f, con_noche) in TEMAS.items():
        if pedidos is not None and tema not in pedidos: continue
        for noche in ((False, True) if con_noche else (False,)):
            nombre = f'a_{tema}_ambiente' + ('_noche' if noche else '')
            semilla = sum(ord(c) * (i + 1) for i, c in enumerate(nombre))
            with A.azar_propio(nombre):
                y = nivelar(en_bucle(f(np.random.default_rng(semilla), noche)))
            ruta = escribir_ogg(nombre, y, salida)
            m = medir(ruta); filas.append((nombre, m))
            print(f"{nombre:34s} {m['s']:5.2f} s  pico {m['pico_db']:6.1f} dBFS  RMS {m['rms_db']:6.1f} dBFS  {m['kb']:5.0f} KB  "
                  f"costura: salto {m['salto']:.2f}  volumen {m['vol_db']:+.1f} dB (dentro: ±{m['vol_tipico']:.1f})  agudos {m['clic']:.2f}", flush=True)
    if '--sin-ronroneo' not in sys.argv:
        A.OUT = salida
        A.escribir('s_gato_ronroneo', ronroneo(), pico=0.85, calidad=4, rms_db=-19.0)
        m = medir(os.path.join(salida, 's_gato_ronroneo.ogg'), bucle=False)
        print(f"{'s_gato_ronroneo':34s} {m['s']:5.2f} s  pico {m['pico_db']:6.1f} dBFS  RMS {m['rms_db']:6.1f} dBFS  {m['kb']:5.0f} KB", flush=True)
