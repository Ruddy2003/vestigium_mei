"""Ensambla el paper completo (módulo 12) a partir de los archivos de docs/paper/.

Qué hace, en orden:
1. Une resumen, capítulos 1 a 11, apéndices A a E (E, el glosario, lo genera glosario.py) y una lista única de referencias.
2. Quita las listas de referencias de cada capítulo (la lista final las reemplaza) y las líneas horizontales.
3. Junta cada imagen con su pie («*Figura N.M. …*») para que el pie quede como leyenda de la figura.
4. Numera las tablas (Tabla N.k, con el título de la sección donde está) y las ecuaciones en bloque
   ((N.k) en los capítulos; en el apéndice B, el número de su sección, B.n).
5. Arma la lista de referencias desde referencias.md: quita duplicados y notas internas de verificación,
   deja visible «[sin verificar: …]» y ordena alfabéticamente (APA 7).
6. Escribe vestigium_mei_paper.md; los .docx y .pdf los genera construir.sh.

Con --corta (módulo 14) arma la versión corta: los capítulos y el apéndice B de docs/paper/corta/, el mismo resumen
y el mismo glosario, y en la lista de referencias solo las citadas. Escribe vestigium_mei_paper_corta.md.

Uso (desde docs/paper/ensamblado): python3 ensamblar.py [--corta]"""
import re
import sys
import unicodedata
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
CORTA = "--corta" in sys.argv
SALIDA = Path(__file__).resolve().parent / ("vestigium_mei_paper_corta.md" if CORTA else "vestigium_mei_paper.md")

CAPITULOS = ["cap01_introduccion.md", "cap02_arquitectura.md", "cap03_estado_subjetivo.md", "cap04_datos_pasivos.md",
             "cap05_modelos_fisiologicos.md", "cap06_estadistica.md", "cap07_red_social.md", "cap08_experimentos.md",
             "cap09_reportes_motivacion.md", "cap10_privacidad_integridad.md", "cap11_discusion.md"]
APENDICES = ["apendiceA_variables.md", "apendiceB_formulas.md", "apendiceC_funciones_R.md", "apendiceD_cambios.md",
             "apendiceE_glosario.md"]

PORTADA = """---
title: "Vestigium Mei"
subtitle: "Qué hace la app, cómo y por qué: el paper metodológico"
date: "Versión de la app: v91 (v90 más la entrega 10e, esquema 14) · 28 de octubre de 2026"
lang: es-MX
toc-title: "Índice"
---

"""
if CORTA:
    CAPITULOS = ["corta/" + c for c in CAPITULOS]
    APENDICES = ["corta/apendiceB_formulas.md", "apendiceE_glosario.md"]
    PORTADA = PORTADA.replace('subtitle: "Qué hace la app, cómo y por qué: el paper metodológico"',
                              'subtitle: "Qué hace la app, cómo y por qué: el paper metodológico (versión corta)"')

# Notas de verificación con «sin verificar»: se reemplazan por una marca corta y visible.
SIN_VERIFICAR = {
    "Domhoff, G. W. (2003)": "subtítulo y ficha editorial",
    "Lazarus, R. S., & Folkman, S. (1984)": "ficha editorial",
    "Dejonckheere, E.": "autores después del tercero",
    "Barnett, A. G.": "coautores",
    "Fisher, A. J.": "páginas",
    "Gelman, A., & Loken, E.": "página final",
    "Granger, C. W. J. (1969)": "página final",
    "Kravitz, R. L.": "enlace no abierto",
}
# Descriptores de APA que sí se quedan entre corchetes.
CORCHETES_APA = ("[Página web]", "[Paquete de R]")


def quitar_referencias_del_capitulo(t):
    """La lista final reemplaza las de cada capítulo («Referencias citadas en este capítulo», «Referencias del capítulo»)."""
    m = re.search(r"\n#{2,3} Referencias (citadas en este capítulo|del capítulo)\b.*", t, re.S)
    return t[:m.start()].rstrip() + "\n" if m else t


def figuras(t):
    """![Figura N.M](ruta) + «*Figura N.M. pie*» → una sola figura con su pie como leyenda."""
    def junta(m):
        pie = m.group(3).strip()
        return f"\n![{pie}]({m.group(2)}){{width=100%}}\n\n"
    t = re.sub(r"!\[(Figura [\d.]+)\]\(([^)]+)\)\s*\n\s*\n\*(Figura [\d.]+\..*?)\*\s*\n", junta, t)
    sueltas = re.findall(r"!\[Figura [\d.]+\]\(", t)
    assert not sueltas, f"figura sin pie: {sueltas}"
    return t


def titulo_limpio(h):
    h = re.sub(r"^(Capítulo \d+|Apéndice [A-E])[.·]\s+", "", h.strip())   # quita «Capítulo 3. »
    h = re.sub(r"^[\dA-D]+(\.\d+[a-z]?)*\.?\s+", "", h)   # quita «2.6.1 », «B.3 »
    h = re.sub(r"`", "", h)
    return h.strip()


def tablas_y_ecuaciones(t, pref):
    """Numera tablas (Tabla pref.k) y ecuaciones en bloque. pref = «3» o «A»."""
    lineas = t.split("\n")
    out, ntab, neq, seccion, eq_en_seccion, i = [], 0, 0, "", {}, 0
    en_codigo = False
    while i < len(lineas):
        l = lineas[i]
        if l.startswith("```"):
            en_codigo = not en_codigo
        if not en_codigo and re.match(r"^#{1,4} ", l):
            seccion = l.lstrip("#").strip()
        # Tabla: una fila con | seguida de la fila separadora
        if (not en_codigo and l.lstrip().startswith("|") and i + 1 < len(lineas)
                and re.match(r"^\s*\|[\s:|-]+\|\s*$", lineas[i + 1]) and "-" in lineas[i + 1]):
            j = i
            while j < len(lineas) and lineas[j].lstrip().startswith("|"):
                out.append(lineas[j]); j += 1
            ntab += 1
            out.append("")
            out.append(f": Tabla {pref}.{ntab}. {titulo_limpio(seccion)}")
            i = j
            continue
        m = re.match(r"^(\s*)\$\$(.+)\$\$\s*$", l)
        if not en_codigo and m:
            if pref == "B":
                etiqueta = re.match(r"(B\.\d+[a-z]?)", seccion).group(1)
                eq_en_seccion[etiqueta] = eq_en_seccion.get(etiqueta, 0) + 1
                if eq_en_seccion[etiqueta] > 1:
                    etiqueta += f"·{eq_en_seccion[etiqueta]}"
            else:
                neq += 1
                etiqueta = f"{pref}.{neq}"
            out.append(f"{m.group(1)}$${m.group(2).strip()}\\qquad\\qquad ({etiqueta})$$")
            i += 1
            continue
        out.append(l)
        i += 1
    return "\n".join(out)


def sin_dolar_suelto(t):
    """«${x}» fuera de código (plantillas de Kotlin en el apéndice A) no es una fórmula: se escapa el $."""
    trozos = re.split(r"(`[^`\n]*`)", t)
    return "".join(p if p.startswith("`") else p.replace("${", "\\${") for p in trozos)


def limpiar(t):
    t = sin_dolar_suelto(t)
    t = re.sub(r"\n-{3,}\s*\n", "\n\n", t)          # líneas horizontales
    t = re.sub(r"\n{3,}", "\n\n", t)
    return t.strip() + "\n"


def norm(s):
    return "".join(c for c in unicodedata.normalize("NFD", s) if unicodedata.category(c) != "Mn").lower()


def citada(e, cuerpo):
    """¿Se cita la entrada en el cuerpo? Primer apellido (o autor institucional) seguido de su año."""
    autor = re.split(r",| \(", e, maxsplit=1)[0].strip().rstrip(".")
    anio = re.search(r"\((\d{4}|s\. f\.)", e).group(1)
    return any(anio in cuerpo[m.end():m.end() + 160] for m in re.finditer(re.escape(norm(autor)), cuerpo))


def referencias(cuerpo_paper=None):
    t = (RAIZ / "referencias.md").read_text(encoding="utf-8")
    ents = [p.strip() for p in t.split("\n\n")
            if re.match(r"^(?:[A-ZÁÉÍÓÚÑ]|(?:von|van|de|del|da) [A-Z])", p.strip()) and re.search(r"\((\d{4}|s\. f\.)[a-z]?[,)]", p[:300])]
    vistas, lista = {}, []
    for e in ents:
        e = " ".join(e.split())
        # Cohen (1988) está dos veces: se queda la del DOI comprobado.
        if e.startswith("Cohen, J. (1988)") and "sin verificar" in e:
            continue
        clave = norm(re.sub(r"\[.*?\]|\(.*?\)", "", e))[:90]
        if clave in vistas:
            continue
        vistas[clave] = True
        marca = next((v for k, v in SIN_VERIFICAR.items() if e.startswith(k)), None)
        # notas internas entre corchetes (salvo los descriptores de APA) y entre paréntesis al final
        e = re.sub(r"\s*\[(?!Página web\]|Paquete de R\])[^\]]*\]", "", e)
        e = re.sub(r"\s*\(existe una fe de erratas: 55\(12\), 2710, https://doi\.org/10\.1139/f98-201\)\.?", "", e)
        e = re.sub(r"\s*\((?:Ficha comprobada|también citada|fórmula 7\.1\.26|el manejo de días|existe una fe de erratas)[^)]*\)*\.?", "", e)
        e = e.rstrip()
        if marca:
            e += f" **[sin verificar: {marca}]**"
        if cuerpo_paper is not None and not citada(e, cuerpo_paper):
            continue
        lista.append(e)
    def orden(e):
        # APA 7: por los apellidos de los autores en orden (sin «&») y, con los mismos autores, por año
        autores = norm(re.sub(r"[^\w ,.-]", "", e.split("(")[0])).replace(" ,", ",")
        return (" ".join(autores.split()), re.search(r"\((\d{4}|s\. f\.)", e).group(1))
    lista.sort(key=orden)
    cuerpo = "\n\n".join(lista)
    nota = ("Las referencias marcadas **[sin verificar: …]** no se pudieron comprobar del todo en la sesión en que se citaron; "
            "el corchete dice qué parte falta. Todas las demás se comprobaron (DOI o enlace) en su sesión; el detalle está en "
            "`referencias.md`.")
    return f"# Referencias\n\n{nota}\n\n::: {{custom-style=\"Bibliografía\"}}\n{cuerpo}\n:::\n", len(lista)


def main():
    partes = [PORTADA]
    resumen = (RAIZ / "resumen.md").read_text(encoding="utf-8")
    partes.append(limpiar(resumen.replace("# Resumen", "# Resumen {.unnumbered}", 1)))
    for f in CAPITULOS + APENDICES:
        t = (RAIZ / f).read_text(encoding="utf-8")
        nombre = f.split("/")[-1]
        pref = re.match(r"cap(\d+)", nombre).group(1).lstrip("0") if nombre.startswith("cap") else re.match(r"apendice([A-E])", nombre).group(1)
        t = quitar_referencias_del_capitulo(t)
        t = figuras(t)
        t = tablas_y_ecuaciones(t, pref)
        partes.append(limpiar(t))
    refs, n = referencias(norm("\n".join(partes)) if CORTA else None)
    partes.append(refs)
    texto = "\n\n".join(partes)
    SALIDA.write_text(texto, encoding="utf-8")
    print(f"{SALIDA.name}: {len(texto.split())} palabras, {texto.count('![')} figuras, "
          f"{len(re.findall(r'^: Tabla', texto, re.M))} tablas, {len(re.findall(r'qquad\\qquad \(', texto))} ecuaciones, {n} referencias")


if __name__ == "__main__":
    main()
