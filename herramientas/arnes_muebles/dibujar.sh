#!/usr/bin/env bash
# Dibuja un mueble de la pestaña Diario en PNG con el código real de la app. Ver LEEME.md.
#
#   dibujar.sh ideario <tema> <claro|oscuro> <salida.png>
#   dibujar.sh biblioteca <tema> <claro|oscuro> <salida.png>
#   dibujar.sh hoja <ideario|biblioteca> <claro|oscuro> <salida.png> <tema1,tema2,...>
#
# Primero compila lo que haya cambiado (Gradle, con su daemon y compilación incremental) bajo un candado, para que varias
# llamadas a la vez no compilen encima unas de otras; luego suelta el candado y dibuja con una copia fija del jar, así cada
# llamada dibuja en paralelo con su propio archivo de salida.
#
# Variables: ARNES_APP=/ruta/a/app/src/main (otro árbol de la app; por omisión el de este repositorio),
#            ARNES_HOY=AAAA-MM-DD (el «hoy» de los ejemplos), GRADLE=/ruta/a/gradle.
set -euo pipefail

aqui="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
gradle="${GRADLE:-$(command -v gradle || echo /opt/gradle/bin/gradle)}"
if [[ $# -lt 4 ]]; then sed -n '2,7p' "$0" | sed 's/^# \{0,1\}//'; exit 2; fi

# Con otra app, otra carpeta de compilación (así dos árboles de trabajo no se pisan los .class).
extra=()
build="$aqui/build"
if [[ -n "${ARNES_APP:-}" ]]; then
  app="$(cd "$ARNES_APP" && pwd)"
  clave="$(printf '%s' "$app" | sha1sum | cut -c1-10)"
  build="$aqui/build-otras/$clave"
  extra=(-Papp="$app" -PdirCompilacion="$build")
fi
mkdir -p "$build"

# El candado: solo una compilación a la vez por carpeta de compilación. El classpath se lee dentro del candado.
cp=""
{
  flock 9
  if ! salida_gradle="$("$gradle" -q -p "$aqui" "${extra[@]}" instantanea --console=plain 2>&1)"; then
    printf '%s\n' "$salida_gradle" | grep -v '^Picked up JAVA_TOOL_OPTIONS' | sed 's#file://##' >&2
    echo "dibujar.sh: no compiló (arriba, los errores con su archivo y línea)." >&2
    exit 1
  fi
  cp="$(cat "$build/instantaneas/classpath.txt")"
} 9>"$build/.candado"

exec java -Djava.awt.headless=true -Xss8m -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -cp "$cp" arnes.DibujarKt "$@" 2> >(grep -v '^Picked up JAVA_TOOL_OPTIONS' >&2)
