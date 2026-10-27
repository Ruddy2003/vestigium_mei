"""Figura 7.3 - Relación según la fecha con datos SINTÉTICOS: replica Personas.guardar (historial) y
Personas.relacionEn / cercaniaEn (data/Personas.kt). Uso: python fig13_relacion_fecha.py [salida.png]"""
import sys
from datetime import date, timedelta
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.dates as md

# --- Lo que hace la app: cada guardado con cambio deja (hasta = día del cambio, relación y cercanía anteriores).
persona = {"relacion": None, "cercania": None, "historial": []}
def guardar(rel, cerca, hoy):
    if persona["relacion"] is not None:
        cambio = persona["relacion"] != rel or (persona["cercania"] is not None and cerca is not None and persona["cercania"] != cerca)
        if cambio: persona["historial"].append((hoy, persona["relacion"], persona["cercania"]))
    persona["relacion"] = rel; persona["cercania"] = cerca if cerca is not None else persona["cercania"]
def relacion_en(f):
    for hasta, rel, _ in sorted(persona["historial"]):
        if f < hasta: return rel
    return persona["relacion"]
def cercania_en(f):
    for hasta, _, c in sorted(persona["historial"]):
        if f < hasta: return c if c is not None else persona["cercania"]
    return persona["cercania"]

guardar("amistad", 4, "2025-02-01")        # primera vez que la clasificas
guardar("pareja", 6, "2025-09-15")
guardar("expareja", 3, "2026-04-20")       # lo anotas 23 días después de la ruptura real (2026-03-28)
REAL = "2026-03-28"

COL = {"amistad": "#e09f3e", "pareja": "#c1121f", "expareja": "#e8a5a9"}
d0, d1 = date(2024, 10, 1), date(2026, 10, 15)
dias = [d0 + timedelta(k) for k in range((d1 - d0).days + 1)]

fig = plt.figure(figsize=(11.5, 5.4), dpi=160)
a = fig.add_axes([0.06, 0.58, 0.9, 0.25])
prev, ini = None, d0
for d in dias + [None]:
    r = relacion_en(d.isoformat()) if d else None
    if r != prev and prev is not None:
        a.axvspan(ini, d or d1, ymin=.15, ymax=.85, color=COL[prev], alpha=.85)
        mid = ini + (((d or d1) - ini) / 2)
        a.text(mid, .5, f"{prev} · cercanía {cercania_en(ini.isoformat())}", ha="center", va="center", fontsize=8.5, color="white" if prev != "expareja" else "#1f2937")
    if r != prev: ini = d
    prev = r
a.axvspan(d0, date(2025, 2, 1), ymin=.0, ymax=1, facecolor="none", hatch="////", edgecolor="#6b7280", lw=0)
a.text(date(2024, 12, 15), 1.07, "antes de clasificarla:\nla app proyecta «amistad»", ha="center", va="bottom", fontsize=7.5, color="#374151", transform=a.get_xaxis_transform())
a.axvspan(date.fromisoformat(REAL), date(2026, 4, 20), ymin=0, ymax=1, facecolor="none", hatch="xxxx", edgecolor="#1f2937", lw=0)
a.annotate("ruptura real (28 mar) → la anotas el 20 abr:\n23 días cuentan como «pareja»", (date(2026, 4, 8), 1.0), xytext=(date(2025, 11, 1), 1.12),
           textcoords="data", xycoords=("data", "axes fraction"), fontsize=7.5, color="#1f2937", arrowprops=dict(arrowstyle="-", color="#6b7280", lw=.8))
for f in ("2025-02-01", "2025-09-15", "2026-04-20"):
    a.axvline(date.fromisoformat(f), color="#1f2937", lw=.8); a.text(date.fromisoformat(f), -.08, f"guardado\n{f}", ha="center", va="top", fontsize=7, transform=a.get_xaxis_transform())
a.set_yticks([]); a.set_xlim(d0, d1); a.xaxis.set_major_locator(md.MonthLocator(bymonth=[1, 4, 7, 10])); MES = "ene feb mar abr may jun jul ago sep oct nov dic".split()
a.xaxis.set_major_formatter(matplotlib.ticker.FuncFormatter(lambda x, _: f"{MES[md.num2date(x).month - 1]} {md.num2date(x).year}"))
a.tick_params(labelsize=7.5, pad=24)
for s in ("top", "right", "left"): a.spines[s].set_visible(False)

t = fig.add_axes([0.06, 0.02, 0.9, 0.36]); t.axis("off")
filas = []
for f in ("2024-12-24", "2025-05-10", "2025-09-14", "2025-09-15", "2026-04-02", "2026-04-20", "2026-06-01"):
    r = relacion_en(f)
    vi = {"pareja": "vi_pareja", "amistad": "vi_amistades", "expareja": "vi_expareja"}[r]
    filas.append([f, r, str(cercania_en(f)), f"{vi} = 1 (si estuviste con ella)"])
tab = t.table(cellText=filas, colLabels=["fecha del día", "relacionEn()", "cercaniaEn()", "variable del panel que se enciende"],
              loc="center", cellLoc="center", colWidths=[.16, .16, .14, .38])
tab.auto_set_font_size(False); tab.set_fontsize(8); tab.scale(1, 1.25)
for (r, c), cell in tab.get_celld().items():
    cell.set_edgecolor("#d1d5db")
    if r == 0: cell.set_facecolor("#f3f4f6"); cell.set_text_props(weight="bold")
    elif filas[r - 1][0] == "2026-04-02": cell.set_facecolor("#fde2e4")
fig.suptitle("Figura 7.3  La relación que había cada día: historial de una persona inventada", x=0.01, ha="left", fontsize=11, weight="bold", color="#1f2937")
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig13_relacion_fecha.png")
for f in filas: print(f)
