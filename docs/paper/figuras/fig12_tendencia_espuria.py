"""Figura 6.2 - Correlación espuria por tendencia, antes y después de quitarla (datos SINTÉTICOS).
Dos variables SIN ninguna relación día a día comparten solo un cambio lento (una sube y otra baja con los meses).
Se aplica lo mismo que Estadistica.series(): a cada día se le resta el promedio de los días con dato que están a
<= 10 días de calendario de él (incluido él mismo; se exige que haya al menos 3). Luego se calcula Pearson sobre esos
restos, como Estadistica.relaciones(sinTendencia = true). Uso: python fig12_tendencia_espuria.py [salida.png]"""
import sys
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

rng = np.random.default_rng(12)
N, VENTANA = 150, 10
t = np.arange(N)
pantalla = 180 + 0.9 * t + rng.normal(0, 35, N)            # minutos de pantalla: suben poco a poco
valencia = 5.0 - 0.012 * t + rng.normal(0, 0.8, N)          # valencia: baja poco a poco
falta = rng.random(N) < 0.15
pantalla[falta] = np.nan
valencia[rng.random(N) < 0.15] = np.nan

def resto(x):
    out = np.full(len(x), np.nan); ok = np.where(~np.isnan(x))[0]
    for i in ok:
        w = ok[np.abs(ok - i) <= VENTANA]
        if len(w) >= 3: out[i] = x[i] - x[w].mean()
    return out

def r(a, b):
    ok = ~np.isnan(a) & ~np.isnan(b); return np.corrcoef(a[ok], b[ok])[0, 1], ok.sum()

ra, rb = resto(pantalla), resto(valencia)
r_bruta, n = r(pantalla, valencia); r_sin, _ = r(ra, rb)
fig, ax = plt.subplots(2, 2, figsize=(12, 7.2), dpi=160, gridspec_kw={"width_ratios": [2.1, 1]})
a = ax[0, 0]; a2 = a.twinx()
a.plot(t, pantalla, ".", color="#2a6f97", ms=4); a2.plot(t, valencia, ".", color="#b45309", ms=4)
a.set_ylabel("pantalla (min)", color="#2a6f97"); a2.set_ylabel("valencia", color="#b45309")
a.set_title("Valores tal cual: las dos solo comparten una tendencia lenta")
b = ax[1, 0]; b2 = b.twinx()
b.plot(t, ra, ".", color="#2a6f97", ms=4); b2.plot(t, rb, ".", color="#b45309", ms=4)
b.axhline(0, color="#9ca3af", lw=.8)
b.set_ylabel("pantalla − su promedio ±10 d", color="#2a6f97"); b2.set_ylabel("valencia − su promedio ±10 d", color="#b45309")
b.set_title("Sin tendencia: lo que cada día tuvo de distinto a sus ~3 semanas vecinas"); b.set_xlabel("día")
okb = ~np.isnan(pantalla) & ~np.isnan(valencia)
ax[0, 1].scatter(pantalla[okb], valencia[okb], s=10, color="#6b7280")
ax[0, 1].set_title(f"con tendencia: r = {r_bruta:+.2f} (n = {n})"); ax[0, 1].set_xlabel("pantalla"); ax[0, 1].set_ylabel("valencia")
oks = ~np.isnan(ra) & ~np.isnan(rb)
ax[1, 1].scatter(ra[oks], rb[oks], s=10, color="#6b7280")
ax[1, 1].set_title(f"sin tendencia: r = {r_sin:+.2f} (n = {oks.sum()})"); ax[1, 1].set_xlabel("resto de pantalla"); ax[1, 1].set_ylabel("resto de valencia")
for x in ax.flat: x.spines[["top"]].set_visible(False)
fig.suptitle("Dos cosas que solo cambian con los meses parecen relacionadas; al compararlas día contra sus vecinos, la relación desaparece", fontsize=10)
fig.tight_layout(); fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig12_tendencia_espuria.png")
print(f"r bruta = {r_bruta:.3f}, r sin tendencia = {r_sin:.3f}")
