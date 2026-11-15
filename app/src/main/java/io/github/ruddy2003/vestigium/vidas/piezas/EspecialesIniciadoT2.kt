package io.github.ruddy2003.vestigium.vidas.piezas

/*
 * Los párrafos especiales de la temporada 2 del cuento de Eleazar, el iniciado de Arcontes ([PiezasIniciado]): los días
 * especiales de la temporada 2 de su calendario (`vidas/anios/AnioG9.kt`, `INICIADO`), con el id tal como está allí (cada
 * id lleva el año). Este archivo es solo de esta temporada: así se pueden escribir varias a la vez sin tocar el mismo
 * archivo. Siguen la gramática, el tono y las reglas de la temporada 1 (`PiezasIniciado.kt`, comentario de arriba):
 * - 3 o 4 párrafos el primer día; los especiales largos, con un texto por día o con `sigue` (los de 5 días o más tienen
 *   que cambiar al menos 4 párrafos de un día al siguiente);
 * - los días de camino a Jerusalén (`sube_*`, `baja_*`) cambian la mañana, el trabajo y el pueblo cada día, y un día
 *   especial que caiga en uno de ellos también los cambia;
 * - Jesús nunca de frente, sus palabras en estilo indirecto con la cita al lado, la lectura gnóstica como lo que él cree,
 *   nada de más de 470 letras, ningún texto repetido, ningún año escrito y ningún hueco que no sea de `Piezas.HUECOS`.
 * Cuando estén todos sus días, `escrita = true`: entonces la temporada entra en `temporadasEscritas`.
 *
 * Ejemplo:
 *     textos = mapOf(
 *         "panes_29" to EspecialesIniciado.unDia(Parrafo.MANANA to "…", Parrafo.PUEBLO to "…", Parrafo.PENSAMIENTO to "…"),
 *     )
 * (con `import io.github.ruddy2003.vestigium.vidas.Parrafo` y, para los largos, `import io.github.ruddy2003.vestigium.vidas.TextoEspecial`).
 */
internal val ESPECIALES_INICIADO_T2 = EspecialesIniciado(temporada = 2, escrita = false, textos = emptyMap())
