"""Figura 4.1 - Fuentes de datos pasivos e importados, cómo llegan y qué día usan. Esquema (no usa datos reales).
Uso: python fig04_fuentes_datos.py [salida.png]"""
import sys
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import estilo  # paleta y tipografía comunes (módulo 12)

INK, MUTED = estilo.TINTA, estilo.GRIS
PAS, IMP = estilo.VERDE, estilo.OCRE   # mismos colores que la figura 2.1
filas = [  # fuente, cómo llega, cuándo, día, capa
    ("Pantalla (uso de apps)", "API de uso del sistema", "mañana y 21:00", "log", "p"),   # día lógico desde el esquema 13
    ("Reloj: pasos", "Health Connect", "mañana y 21:00", "log", "p"),                    # día lógico desde el esquema 13
    ("Reloj: corazón, oxígeno, peso", "Health Connect", "mañana y 21:00", "cal", "p"),
    ("Reloj: sueño", "Health Connect", "mañana y 21:00", "desp", "p"),
    ("Agenda (calendario)", "Calendario del teléfono", "mañana y 21:00", "cal", "p"),
    ("Clima, aire, luz solar", "Open-Meteo (internet)", "mañana", "cal", "p"),
    ("Ubicación", "GPS/red cada 15 min + geocercas", "15 min", "log", "p"),
    ("Movimiento (a pie, vehículo…)", "Reconocimiento de actividad", "al cambiar", "log", "p"),
    ("Wifi, audio, carga, luz, presión", "Sensores del teléfono", "15 min", "log", "p"),
    ("Notificaciones y música", "Escucha de notificaciones", "al ocurrir", "log", "p"),
    ("Fotos (conteo)", "Galería", "mañana y 21:00", "log", "p"),
    ("Llamadas y SMS (conteo)", "Registros del teléfono", "mañana y 21:00", "log", "p"),
    ("Spotify", "API (últimas 50) o historial", "~30 min / archivo", "log", "i"),
    ("Google (YouTube, búsquedas)", "Archivo Takeout", "a mano", "log", "i"),
    ("WhatsApp (solo conteos)", "Chat exportado", "a mano", "log", "i"),
    ("Cold Turkey (laptop)", "CSV", "a mano", "cal", "i"),
    ("MacroFactor (nutrición, pesas)", "Excel (.xlsx)", "a mano", "cal", "i"),
]
n = len(filas)
fig, ax = plt.subplots(figsize=(11, 6.4), dpi=160)
ax.set_xlim(0, 11); ax.set_ylim(-0.9, n + 0.6); ax.invert_yaxis(); ax.axis("off")
for x, t in [(0.35, "Fuente"), (4.1, "Cómo llega"), (7.0, "Cuándo"), (9.1, "Día que usa")]:
    ax.text(x, -0.35, t, fontsize=9, weight="bold", color=INK, va="center")
ax.plot([0.2, 10.9], [0.05, 0.05], color=estilo.GRIS_CLARO, lw=1)
for i, (f, c, w, d, k) in enumerate(filas):
    y = i + 0.6
    col = PAS if k == "p" else IMP
    ax.add_patch(plt.Rectangle((0.1, y - 0.36), 0.08, 0.72, color=col, lw=0))
    ax.text(0.35, y, f, fontsize=8.6, color=INK, va="center")
    ax.text(4.1, y, c, fontsize=8.2, color=MUTED, va="center")
    ax.text(7.0, y, w, fontsize=8.2, color=MUTED, va="center")
    if d == "log":
        ax.plot(9.2, y, "o", ms=7, color=INK); ax.text(9.45, y, "lógico (05:00)", fontsize=8.2, color=INK, va="center")
    elif d == "cal":
        ax.plot(9.2, y, "o", ms=7, mfc="white", mec=INK, mew=1.4); ax.text(9.45, y, "calendario (00:00)", fontsize=8.2, color=INK, va="center")
    else:
        ax.plot(9.2, y, "s", ms=6.5, color=INK); ax.text(9.45, y, "día en que despiertas", fontsize=8.2, color=INK, va="center")
ax.text(0.1, n + 0.45, "Franja verde = pasiva · franja naranja = importada · ● día lógico (corte 05:00) · ○ día de calendario (00:00 a 24:00) · pantalla y pasos, por día lógico desde el esquema 13",
        fontsize=8, color=MUTED)
ax.set_title("Fuentes de datos pasivos e importados y el «día» que cada una usa", loc="left", fontsize=11.5, weight="bold", color=INK)
fig.tight_layout()
fig.savefig(sys.argv[1] if len(sys.argv) > 1 else "fig04_fuentes_datos.png")
