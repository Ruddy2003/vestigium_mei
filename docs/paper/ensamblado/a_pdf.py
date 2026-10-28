"""Convierte vestigium_mei_paper.docx a PDF con LibreOffice (módulo 12).
Abre el .docx sin pantalla, actualiza el índice y los campos (números de página) y exporta a PDF con marcadores.
Uso (desde docs/paper/ensamblado): python3 a_pdf.py [nombre sin extensión; por omisión vestigium_mei_paper]"""
import subprocess
import sys
import time
from pathlib import Path

import uno
from com.sun.star.beans import PropertyValue

AQUI = Path(__file__).resolve().parent
BASE = sys.argv[1] if len(sys.argv) > 1 else "vestigium_mei_paper"
ENTRADA, SALIDA = AQUI / f"{BASE}.docx", AQUI / f"{BASE}.pdf"


def prop(nombre, valor):
    p = PropertyValue(); p.Name, p.Value = nombre, valor
    return p


oficina = subprocess.Popen(["soffice", "--headless", "--invisible", "--norestore",
                            "--accept=socket,host=localhost,port=2002;urp;"])
try:
    resolver = uno.getComponentContext().ServiceManager.createInstanceWithContext(
        "com.sun.star.bridge.UnoUrlResolver", uno.getComponentContext())
    for _ in range(60):
        try:
            ctx = resolver.resolve("uno:socket,host=localhost,port=2002;urp;StarOffice.ComponentContext")
            break
        except Exception:
            time.sleep(1)
    escritorio = ctx.ServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop", ctx)
    doc = escritorio.loadComponentFromURL(ENTRADA.as_uri(), "_blank", 0, (prop("Hidden", True),))
    # El índice que inserta pandoc es un campo de Word que LibreOffice no reconoce: se pone uno propio
    # justo antes del título «Resumen» (queda en la página del título «Índice»), con los dos primeros niveles.
    busca = doc.createSearchDescriptor(); busca.SearchString = "Resumen"
    titulo = doc.findFirst(busca)
    texto = doc.getText()
    cursor = texto.createTextCursorByRange(titulo.getStart())
    texto.insertControlCharacter(cursor, uno.getConstantByName("com.sun.star.text.ControlCharacter.PARAGRAPH_BREAK"), False)
    cursor.gotoPreviousParagraph(False)
    cursor.setPropertyValue("ParaStyleName", "Body Text")
    cursor.setPropertyValue("BreakType", uno.Enum("com.sun.star.style.BreakType", "NONE"))
    indice = doc.createInstance("com.sun.star.text.ContentIndex")
    indice.CreateFromOutline = True; indice.Level = 2; indice.Title = ""
    texto.insertTextContent(cursor, indice, False)
    for _ in range(2):   # dos pasadas: el índice cambia la paginación
        indices = doc.getDocumentIndexes()
        for i in range(indices.getCount()):
            indices.getByIndex(i).update()
        doc.getTextFields().refresh()
    filtro = (prop("ExportBookmarks", True), prop("UseTaggedPDF", True))
    doc.storeToURL(SALIDA.as_uri(), (prop("FilterName", "writer_pdf_Export"), prop("FilterData", uno.Any("[]com.sun.star.beans.PropertyValue", filtro))))
    doc.close(True)
    print(f"{SALIDA.name} listo")
finally:
    oficina.terminate()
