"""Paleta y tipografía comunes de todas las figuras del paper (módulo 12).
Cada script hace `import estilo` después de importar pyplot y usa estas constantes en lugar de colores sueltos.
La paleta categórica (AZUL, OCRE, VIOLETA, VERDE, en ese orden) pasó el validador de la guía de visualización:
banda de luminosidad, croma mínimo, separación con daltonismo (peor par 16.3 ΔE) y contraste sobre fondo blanco.
El ROJO queda reservado para alertas (lo que la app marca como malo o un hallazgo abierto), nunca para una serie más."""
import matplotlib

# Categórica, en orden fijo
AZUL = "#2563a8"
OCRE = "#c2620c"
VIOLETA = "#7353b8"
VERDE = "#178a6a"
# Variantes de la misma serie (más clara o más oscura), para rellenos, bandas y una segunda lectura de la misma cosa
AZUL_CLARO = "#a9c4e4"
AZUL_PALIDO = "#dde8f4"
OCRE_CLARO = "#eaa35c"
OCRE_PALIDO = "#fbe5cc"
OCRE_OSCURO = "#7a3d08"
VERDE_CLARO = "#b5ddd0"
# Alerta
ROJO = "#b42318"
ROJO_CLARO = "#f0a9a3"
ROJO_PALIDO = "#fde4e2"
# Neutros (texto, ejes, rejilla)
TINTA = "#1f2937"
TINTA_2 = "#4b5563"
GRIS = "#6b7280"
GRIS_MEDIO = "#9ca3af"
GRIS_CLARO = "#d1d5db"
REJILLA = "#e5e7eb"
FONDO = "#ffffff"

matplotlib.rcParams.update({
    "font.family": ["Inter", "DejaVu Sans"],
    "font.size": 9,
    "axes.titlesize": 10,
    "axes.labelsize": 9,
    "axes.edgecolor": GRIS_MEDIO,
    "axes.labelcolor": TINTA,
    "axes.titlecolor": TINTA,
    "xtick.color": TINTA_2,
    "ytick.color": TINTA_2,
    "text.color": TINTA,
    "axes.spines.top": False,
    "axes.spines.right": False,
    "grid.color": REJILLA,
    "legend.frameon": False,
    "figure.facecolor": FONDO,
    "savefig.facecolor": FONDO,
})
