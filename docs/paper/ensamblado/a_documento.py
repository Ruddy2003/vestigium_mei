"""Prepara el paper ensamblado para el documento de Claude (módulo 12): un archivo Markdown por parte.
Baja un nivel cada título (el título del documento es el único de nivel 1), cambia las rutas de las figuras por
los blobs del documento, pasa las ecuaciones a bloques ```latex con su número, pone los pies de tabla como texto
y quita lo que solo entiende pandoc. Uso: python3 a_documento.py <carpeta de salida> [paper ensamblado .md; por omisión vestigium_mei_paper.md]"""
import re
import sys
from pathlib import Path

# figNN → blob del documento (subidas el 28 oct 2026, en orden fig01…fig30; fig02 rehecha en el módulo 14)
BLOBS = """70a3921f-e084 48b5ecd0-80ab ae34f15c-fd2e 75f8fd2d-9b48 59dd9485-1d0c 87402bc4-734c f38fd18b-8a1e f2c1ad34-e088
a8d3c701-8f22 df10f3ba-b067 e3772107-2947 e9615867-f5ab 1d5b10d6-11c5 3276340c-5c70 4318aea3-bc87 bc7418d1-703f
20159d72-905d 39da0a78-6f59 51e7526d-79d5 a6c929eb-29f8 0b7d41eb-f676 cfab8dab-2130 3f0cb652-ffb3 36bd08e7-d974
f7407a79-aadf 8a08e87f-1ed4 7b51d30c-5352 fee1be4a-0a03 45a73458-0777 591e4e3d-b900""".split()

t = (Path(__file__).parent / (sys.argv[2] if len(sys.argv) > 2 else "vestigium_mei_paper.md")).read_text(encoding="utf-8")
t = t.split("\n---\n", 2)[-1] if t.startswith("---") else t          # sin el bloque de metadatos
t = t.replace(" {.unnumbered}", "").replace("\\${", "${")
t = re.sub(r"^::: \{custom-style=\"Bibliografía\"\}\n|^:::\n", "", t, flags=re.M)
t = re.sub(r"^(#{1,5}) ", lambda m: "#" + m.group(1) + " ", t, flags=re.M)
t = re.sub(r"!\[(.*?)\]\(figuras/fig(\d\d)_[\w]+\.png\)\{width=100%\}",
           lambda m: f"![{m.group(1)}](blob/{BLOBS[int(m.group(2)) - 1]})\n\n*{m.group(1)}*", t)
t = re.sub(r"^\s*\$\$(.+?)\\qquad\\qquad \(([^)]+)\)\$\$\s*$", lambda m: f"```latex\n{m.group(1).strip()} \\tag{{{m.group(2)}}}\n```", t, flags=re.M)
t = re.sub(r"^: (Tabla [A-E\d]+\.\d+\..*)$", r"*\1*", t, flags=re.M)
# Las pocas fórmulas dentro de una frase pasan a texto (el documento muestra las fórmulas solo en bloque)
EN_LINEA = {r"$\text{nivel} \mathrel{+}= \left(1 - (1-s)^{d/\text{dosis}}\right)\cdot(100 - \text{nivel})$": "nivel += (1 − (1 − s)^(d/dosis)) · (100 − nivel)",
            r"$\text{nivel} \mathrel{\times}= (1 - b)$": "nivel ×= (1 − b)",
            r"$\binom{N}{N/2}$": "(N sobre N/2)", r"$2^{N/2}$": "2^(N/2)", r"$0 \le d - c < L$": "0 ≤ d − c < L",
            r"$\left(1 - (1-s)^{d/\text{dosis}}\right)\cdot(100 - \text{nivel})$": "(1 − (1 − s)^(d/dosis)) · (100 − nivel)",
            r"$\bar{x}$": "x̄", r"$s^2$": "s²", r"$s_p$": "sₚ", r"$s_c^2$": "s²_c", r"$s_s^2$": "s²_s"}
for k, v in EN_LINEA.items():
    t = t.replace(k, v)
# Subíndices en HTML: el documento no los muestra; se pasan a texto
t = t.replace("<sub>ij</sub>", "ᵢⱼ")
t = re.sub(r"<sub>(\w+)</sub>", r"_\1", t)
salida = Path(sys.argv[1]); salida.mkdir(parents=True, exist_ok=True)
for i, p in enumerate(re.split(r"\n(?=## )", t.strip())):
    (salida / f"parte{i:02d}.md").write_text(p.strip() + "\n", encoding="utf-8")
    print(f"parte{i:02d}.md", len(p.encode()), p.split("\n")[0][:60])
