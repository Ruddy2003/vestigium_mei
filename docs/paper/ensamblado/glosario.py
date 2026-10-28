"""Arma el Apéndice E (glosario) juntando los mini-glosarios de los capítulos (módulo 13).
Cada capítulo explica sus términos en su sección «N.1 Mini-glosario». Este script los junta en orden alfabético,
dice en qué capítulo se explica cada uno y agrega, en EXTRA, los términos que el paper usa mucho y que no tenían
entrada (su definición se copia de donde el capítulo los explica). No cambia las definiciones.
Uso (desde docs/paper/ensamblado): python3 glosario.py  → escribe ../apendiceE_glosario.md"""
import re
import unicodedata
from collections import defaultdict
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
SALIDA = RAIZ / "apendiceE_glosario.md"

# Términos que se usan en varios capítulos y no tenían entrada en ningún mini-glosario.
# (término, definición, capítulo donde se explica)
EXTRA = [
    ("**AR(1)** (autocorrelación de orden 1)",
     "Supuesto de que el parecido entre días decae de un día a otro de forma fija: lo que importa es cuánto se parece "
     "cada día al anterior. La app lo usa para corregir los intervalos de las correlaciones (n efectivo) y de las "
     "diferencias de medias (sección 6.6 y 6.15).", 6),
    ("**Confusor**",
     "Una tercera cosa que mueve a las dos variables que se comparan, como el día de la semana o estar enfermo. "
     "Las protecciones de la app no lo eliminan.", 6),
    ("**Covariable que cambia con el tiempo** (*time-varying covariate*)",
     "Un dato de contexto que no es fijo, sino que tiene un valor por periodo; por ejemplo, la relación que tenías "
     "con una persona cada día.", 7),
    ("**Health Connect**",
     "El sistema de salud de Android, donde otras apps (por ejemplo, la del reloj) escriben sus datos. La app solo "
     "pide permisos de lectura.", 4),
    ("**Maldición del ganador**",
     "La relación más fuerte de una lista larga casi siempre exagera su tamaño, aunque sea real: al escoger la que dio "
     "la *r* más grande, se escoge también la que tuvo más suerte (Ioannidis, 2008).", 1),
    ("**Modelo circumplejo**",
     "Propuesta de Russell (1980): casi todas las emociones se ubican en un plano con dos ejes independientes, "
     "valencia y activación.", 3),
    ("**Modelo multinivel** (jerárquico, de efectos mixtos)",
     "Modelo que trata de forma explícita que los días están anidados en semanas, meses y etapas: estima, por "
     "ejemplo, una relación en cada año y cuánto varía de un año a otro. La app no lo usa; es trabajo futuro.", 11),
    ("**MSSD**",
     "Promedio de los cuadrados de las diferencias entre días consecutivos de calendario con ambos datos: mide la "
     "inestabilidad de un día al siguiente (von Neumann et al., 1941).", 6),
    ("**PSS-4**",
     "Cuestionario de estrés percibido: cuatro preguntas de 0 (nunca) a 4 (muy a menudo), dos de ellas inversas; "
     "total de 0 a 16. En la app, cada 7 días.", 3),
    ("**t de Student**",
     "La curva que reemplaza a la normal cuando la variabilidad se estima con pocos datos: tiene colas más gruesas, y "
     "más cuanto menos grados de libertad.", 6),
    ("**t de Welch**",
     "La prueba t para comparar dos promedios sin suponer que los dos grupos varían lo mismo; admite grados de "
     "libertad no enteros (Welch, 1947).", 6),
    ("**Transformación z de Fisher**",
     "z = atanh(*r*): convierte una correlación en una escala donde su error es aproximadamente normal, para "
     "calcular su intervalo y su *p* (Fisher, 1915).", 6),
    ("**WHO-5**",
     "Cuestionario de bienestar: cinco frases, cada una de 0 (nunca) a 5 (todo el tiempo). En la app, cada 7 días.", 3),
    ("**z robusto**",
     "Qué tan lejos está un día de lo habitual, medido con la mediana y la MAD en lugar del promedio y la desviación "
     "estándar, para que los valores raros no muevan la regla. La app marca como atípico un día con |z| > 3.", 6),
]
# Términos que el mismo glosario tiene con dos nombres: se juntan en una entrada.
ALIAS = {"intervalo de confianza de 95 %": "intervalo de confianza del 95 %",
         "diseno de caso unico / n-de-1": "experimento n-de-1"}


def norm(s):
    return "".join(c for c in unicodedata.normalize("NFD", s) if unicodedata.category(c) != "Mn").lower()


def entradas():
    por_termino = defaultdict(list)
    for f in sorted(RAIZ.glob("cap*.md")):
        cap = int(re.match(r"cap(\d+)", f.name).group(1))
        t = f.read_text(encoding="utf-8")
        m = re.search(r"^## \d+\.1 Mini-glosario.*?\n(.*?)(?=^## )", t, re.S | re.M)
        if not m:
            continue
        for fila in m.group(1).splitlines():
            if not fila.startswith("|") or re.match(r"^\|\s*(Término|---)", fila):
                continue
            celdas = [c.strip() for c in fila.strip().strip("|").split("|")]
            if len(celdas) < 2:
                continue
            termino, definicion = celdas[0], " | ".join(celdas[1:])
            clave = norm(re.sub(r"[*`]", "", re.split(r"\s+\(", termino)[0]))
            clave = ALIAS.get(clave, clave)
            por_termino[clave].append((termino, definicion, cap))
    for termino, definicion, cap in EXTRA:
        clave = norm(re.sub(r"[*`]", "", re.split(r"\s+\(", termino)[0]))
        clave = ALIAS.get(clave, clave)
        assert clave not in por_termino, f"«{termino}» ya tiene entrada"
        por_termino[clave].append((termino, definicion, cap))
    return por_termino


def main():
    por_termino = entradas()
    lineas = ["# Apéndice E. Glosario", "",
              "_Reúne, en orden alfabético, los términos que cada capítulo explica en su mini-glosario, con el capítulo "
              "donde se explican. Cuando un término aparece en dos capítulos, se dan las dos explicaciones, porque cada "
              "una lo dice desde su tema. Se genera con `ensamblado/glosario.py`; no se edita a mano._", "",
              "| Término | Qué significa | Cap. |", "|---|---|---|"]
    for clave in sorted(por_termino, key=lambda k: re.sub(r"[^a-z0-9ñ ]", "", k.replace("β", "beta").replace("ñ", "n~"))):
        filas = por_termino[clave]
        termino = filas[0][0]
        if len(filas) == 1:
            lineas.append(f"| {termino} | {filas[0][1]} | {filas[0][2]} |")
        else:
            definicion = " ".join(f"(cap. {c}) {d}" for _, d, c in filas)
            caps = ", ".join(str(c) for _, _, c in filas)
            lineas.append(f"| {termino} | {definicion} | {caps} |")
    SALIDA.write_text("\n".join(lineas) + "\n", encoding="utf-8")
    print(f"{SALIDA.name}: {len(por_termino)} términos")


if __name__ == "__main__":
    main()
