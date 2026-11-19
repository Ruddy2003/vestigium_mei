#!/usr/bin/env bash
# Mide el «peso» del dibujo de los muebles y de las piezas comunes (arnes/Medir.kt). Ver LEEME.md, «Medir».
#
#   medir.sh [grupo,grupo,...] [tema,tema,...]
#
# Grupos: biblioteca, ideario, vida, comun, decoracion, fondo (por omisión, todos). Temas: por omisión, psicodelia (en sus
# ocho variantes), heleno, laboratorio, nouveau, galia, arcontes y frente. Sale una tabla separada por tabuladores.
set -euo pipefail
aqui="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
gradle="${GRADLE:-$(command -v gradle || echo /opt/gradle/bin/gradle)}"
build="$aqui/build"
mkdir -p "$build"
cp=""
{
  flock 9
  if ! salida_gradle="$("$gradle" -q -p "$aqui" instantanea --console=plain 2>&1)"; then
    printf '%s\n' "$salida_gradle" | grep -v '^Picked up JAVA_TOOL_OPTIONS' | sed 's#file://##' >&2
    echo "medir.sh: no compiló (arriba, los errores con su archivo y línea)." >&2
    exit 1
  fi
  cp="$(cat "$build/instantaneas/classpath.txt")"
} 9>"$build/.candado"
# Sin -XX:TieredStopAtLevel=1: para medir hace falta el compilador completo de la JVM, como en un dibujo que se repite.
exec java -Djava.awt.headless=true -Dstdout.encoding=UTF-8 -Dfile.encoding=UTF-8 -Xss8m -Xmx2g -cp "$cp" arnes.MedirKt "$@" 2> >(grep -v '^Picked up JAVA_TOOL_OPTIONS' >&2)
