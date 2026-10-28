#!/bin/sh
# Construye el paper (módulos 12 a 14): completo y versión corta, en Markdown ensamblado, .docx y .pdf.
# Requiere python3 con python-docx, pandoc y LibreOffice. Uso: sh construir.sh (desde docs/paper/ensamblado)
set -e
cd "$(dirname "$0")"
python3 glosario.py
[ -f plantilla_base.docx ] || pandoc -o plantilla_base.docx --print-default-data-file reference.docx
python3 plantilla.py
# Completa y, si existe docs/paper/corta/, también la versión corta (módulo 14)
for v in completa corta; do
  if [ "$v" = corta ]; then [ -d ../corta ] || continue; base=vestigium_mei_paper_corta; python3 ensamblar.py --corta
  else base=vestigium_mei_paper; python3 ensamblar.py; fi
  pandoc $base.md -o $base.docx --reference-doc=plantilla.docx --toc --toc-depth=2 --resource-path=..
  # Que Word actualice el índice y los números de página al abrir el archivo
  BASE=$base python3 - <<'PY'
import os, zipfile, shutil
src = os.environ["BASE"] + ".docx"; tmp = os.environ["BASE"] + ".tmp.docx"
with zipfile.ZipFile(src) as zin, zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        data = zin.read(item.filename)
        if item.filename == "word/settings.xml" and b"updateFields" not in data:
            data = data.replace(b"</w:settings>", b'<w:updateFields w:val="true"/></w:settings>')
        zout.writestr(item, data)
shutil.move(tmp, src)
PY
  python3 a_pdf.py $base
done
