// Sustituto de data/Repo.kt (la base de datos del teléfono): solo lo que leen los archivos enlazados, en memoria.
package io.github.ruddy2003.vestigium.data

object Repo {
    /** La configuración: el arnés pone aquí el tema y el modo antes de dibujar. */
    var config: Config = Config()
    /** Los días guardados (las entradas de ejemplo de la biblioteca). */
    var dias: Map<String, Dia> = emptyMap()
}
