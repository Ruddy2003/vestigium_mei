"""Crea plantilla.docx, la plantilla de estilos que pandoc usa para el .docx del paper (módulo 12).
Parte de la plantilla por omisión de pandoc (plantilla_base.docx) y ajusta: tamaño carta con márgenes de 2.5 cm,
Cambria para el texto y Calibri para los títulos (en este entorno, LibreOffice los sustituye por Caladea y Carlito,
que tienen las mismas medidas), tablas con bordes finos y encabezado sombreado, pies de figura y de tabla, la
lista de referencias con sangría francesa (APA 7) y el número de página al pie.
Uso (desde docs/paper/ensamblado): python3 plantilla.py"""
from docx import Document
from docx.enum.style import WD_STYLE_TYPE
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Pt, RGBColor

TINTA = RGBColor(0x1F, 0x29, 0x37)
GRIS = RGBColor(0x4B, 0x55, 0x63)
AZUL = RGBColor(0x25, 0x63, 0xA8)
TEXTO, TITULOS, CODIGO = "Cambria", "Calibri", "Consolas"

d = Document("plantilla_base.docx")
st = d.styles


def fuente(s, nombre=TEXTO, tam=None, color=None, negrita=None, cursiva=None):
    f = s.font
    f.name = nombre
    rpr = s.element.get_or_add_rPr()
    fonts = rpr.find(qn("w:rFonts"))
    if fonts is None:
        fonts = OxmlElement("w:rFonts"); rpr.append(fonts)
    for a in ("w:ascii", "w:hAnsi", "w:cs", "w:eastAsia"):
        fonts.set(qn(a), nombre)
    for a in ("w:asciiTheme", "w:hAnsiTheme", "w:cstheme", "w:eastAsiaTheme"):
        if fonts.get(qn(a)) is not None:
            del fonts.attrib[qn(a)]
    if tam: f.size = Pt(tam)
    if color is not None: f.color.rgb = color
    if negrita is not None: f.bold = negrita
    if cursiva is not None: f.italic = cursiva


def estilo(nombre, tipo=WD_STYLE_TYPE.PARAGRAPH, base="Normal"):
    # Primero por identificador («Heading 1» → «Heading1»): python-docx no encuentra por nombre algunos de pandoc
    ident = nombre.replace(" ", "")
    for s in st:
        if s.style_id == ident:
            return s
    try:
        return st[nombre]
    except KeyError:
        s = st.add_style(nombre, tipo)
        s.base_style = st[base]
        return s


# Texto
for n in ("Normal", "Body Text", "First Paragraph"):
    s = estilo(n)
    fuente(s, TEXTO, 10.5, TINTA)
    s.paragraph_format.space_after = Pt(6)
    s.paragraph_format.line_spacing = 1.15
s = estilo("Compact"); fuente(s, TEXTO, 9, TINTA); s.paragraph_format.space_after = Pt(1); s.paragraph_format.line_spacing = 1.0
s = estilo("Block Text"); fuente(s, TEXTO, 9.5, GRIS)
s.paragraph_format.left_indent = Cm(0.8); s.paragraph_format.right_indent = Cm(0.4)

# Títulos
tam = {1: 20, 2: 14, 3: 12, 4: 11, 5: 10.5}
for i, t in tam.items():
    s = estilo(f"Heading {i}")
    fuente(s, TITULOS, t, TINTA if i > 1 else AZUL, negrita=True, cursiva=False)
    s.paragraph_format.space_before = Pt(18 if i <= 2 else 10)
    s.paragraph_format.space_after = Pt(6)
    s.paragraph_format.keep_with_next = True
estilo("Heading 1").paragraph_format.page_break_before = True
for n, t in (("Title", 30), ("Subtitle", 15), ("Date", 11), ("TOC Heading", 20)):
    s = estilo(n); fuente(s, TITULOS, t, AZUL if n in ("Title", "TOC Heading") else GRIS, negrita=n in ("Title", "TOC Heading"))
estilo("Title").paragraph_format.space_before = Pt(160)

# Pies de figura y de tabla
for n in ("Image Caption", "Table Caption"):
    s = estilo(n); fuente(s, TITULOS, 9, GRIS, cursiva=n == "Image Caption")
    s.paragraph_format.space_after = Pt(10)
estilo("Table Caption").paragraph_format.keep_with_next = True
s = estilo("Captioned Figure"); s.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER; s.paragraph_format.keep_with_next = True

# Código
for n, tipo in (("Source Code", WD_STYLE_TYPE.PARAGRAPH), ("Verbatim Char", WD_STYLE_TYPE.CHARACTER)):
    try:
        fuente(estilo(n, tipo), CODIGO, 8.5)
    except (KeyError, ValueError):
        pass

# Referencias: sangría francesa (APA 7)
s = estilo("Bibliografía"); fuente(s, TEXTO, 10, TINTA)
s.paragraph_format.left_indent = Cm(1.27); s.paragraph_format.first_line_indent = Cm(-1.27)
s.paragraph_format.space_after = Pt(6); s.paragraph_format.line_spacing = 1.1

# Tablas: bordes finos y encabezado sombreado
tabla = estilo("Table", WD_STYLE_TYPE.TABLE).element
tblpr = tabla.find(qn("w:tblPr"))
if tblpr is None:
    tblpr = OxmlElement("w:tblPr"); tabla.append(tblpr)
bordes = OxmlElement("w:tblBorders")
for lado in ("top", "left", "bottom", "right", "insideH", "insideV"):
    b = OxmlElement(f"w:{lado}")
    b.set(qn("w:val"), "single"); b.set(qn("w:sz"), "4"); b.set(qn("w:space"), "0"); b.set(qn("w:color"), "D1D5DB")
    bordes.append(b)
viejo = tblpr.find(qn("w:tblBorders"))
if viejo is not None:
    tblpr.remove(viejo)
tblpr.append(bordes)
mar = OxmlElement("w:tblCellMar")
for lado, v in (("top", "30"), ("bottom", "30"), ("left", "70"), ("right", "70")):
    m = OxmlElement(f"w:{lado}"); m.set(qn("w:w"), v); m.set(qn("w:type"), "dxa"); mar.append(m)
tblpr.append(mar)
for c in tabla.findall(qn("w:tblStylePr")):
    if c.get(qn("w:type")) == "firstRow":
        tcpr = c.find(qn("w:tcPr"))
        if tcpr is None:
            tcpr = OxmlElement("w:tcPr"); c.append(tcpr)
        sh = OxmlElement("w:shd"); sh.set(qn("w:val"), "clear"); sh.set(qn("w:color"), "auto"); sh.set(qn("w:fill"), "EEF2F7")
        tcpr.append(sh)

# Página: carta, márgenes de 2.5 cm y número de página al pie
sec = d.sections[0]
sec.page_width, sec.page_height = Cm(21.59), Cm(27.94)
for lado in ("left_margin", "right_margin", "top_margin", "bottom_margin"):
    setattr(sec, lado, Cm(2.5))
pie = sec.footer.paragraphs[0] if sec.footer.paragraphs else sec.footer.add_paragraph()
pie.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = pie.add_run()
for tipo, texto in (("begin", None), (None, " PAGE "), ("end", None)):
    if tipo:
        e = OxmlElement("w:fldChar"); e.set(qn("w:fldCharType"), tipo)
    else:
        e = OxmlElement("w:instrText"); e.set(qn("xml:space"), "preserve"); e.text = texto
    r._r.append(e)
r.font.size = Pt(9); r.font.color.rgb = GRIS
d.save("plantilla.docx")
print("plantilla.docx lista")
