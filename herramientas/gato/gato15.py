"""
Prueba 15: colonos con el fondo simplificado de la prueba 13 (menos cosas) y el gato grabado completo de la prueba 12.
Romano (como antes) y soviético (simple) también vienen de gato13.py.
"""
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import gato8 as g8        # noqa: E402
import gato9 as g9        # noqa: E402
import gato12 as g12      # noqa: E402
import gato13 as g13      # noqa: E402

AQUI = Path(__file__).parent / "salida"   # hojas, bandas y GIF de revisión (no van al repositorio)
AQUI.mkdir(exist_ok=True)
for nombre in ("gato8", "gato9", "gato10", "gato11", "gato12", "gato13"):
    sys.modules[nombre].AQUI = AQUI

# El gato vuelve al grabado completo de gato12.py (gato13 lo había simplificado); el fondo se queda como en gato13.
texto = (Path(__file__).parent / "gato12.py").read_text()
i = texto.index("\ndef pintar_grabado(") + 1
exec(texto[i:texto.find("\n\n\n", i)] + "\n", g12.__dict__)

if __name__ == "__main__":
    s_, cs = g8.buscar_semilla()
    g8.gif_banda(cs, [("colonos", False, g9.ETIQ["colonos"])])
    g8.gif_banda(cs, [("colonos", True, "Colonos · de noche: " + g9.NOCHE["colonos"])], "_oscuro")
    g8.hoja("colonos", False, "hoja_sprites_colonos.png", material=True)
    print("listo")
