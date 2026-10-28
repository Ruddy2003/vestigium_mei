#!/bin/sh
# Construye el paper completo (módulo 12): Markdown ensamblado, .docx y .pdf.
# Requiere python3 con python-docx, pandoc y LibreOffice. Uso: sh construir.sh (desde docs/paper/ensamblado)
set -e
cd "$(dirname "$0")"
python3 ensamblar.py
[ -f plantilla_base.docx ] || pandoc -o plantilla_base.docx --print-default-data-file reference.docx
python3 plantilla.py
pandoc vestigium_mei_paper.md -o vestigium_mei_paper.docx --reference-doc=plantilla.docx --toc --toc-depth=2 --resource-path=..
# Que Word actualice el índice y los números de página al abrir el archivo
python3 - <<'PY'
import zipfile, shutil
src, tmp = "vestigium_mei_paper.docx", "vestigium_mei_paper.tmp.docx"
with zipfile.ZipFile(src) as zin, zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zout:
    for item in zin.infolist():
        data = zin.read(item.filename)
        if item.filename == "word/settings.xml" and b"updateFields" not in data:
            data = data.replace(b"</w:settings>", b'<w:updateFields w:val="true"/></w:settings>')
        zout.writestr(item, data)
shutil.move(tmp, src)
PY
python3 a_pdf.py
