package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Campanas
import io.github.ruddy2003.vestigium.ui.Contenedor
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrofeosTema
import io.github.ruddy2003.vestigium.ui.contenedorDe
import io.github.ruddy2003.vestigium.ui.radioDe
import io.github.ruddy2003.vestigium.ui.tipoDe
import org.junit.Test
import java.security.MessageDigest

/**
 * Plan de diseño, principio 1c: lo que se puede comparar como valor no se repite entre dos temas (textos, números y fechas,
 * letras, esquinas, sonido, vibración, nombres de trofeos y de etapas). Por superficie, se anotan los temas que comparten
 * su valor con otro. Lo de hoy está en [PENDIENTES]; ver [Pendientes] para cómo se usa la lista. Los dibujos se revisan
 * en las capturas.
 */
class UnicidadTemas {
    @Test fun cadaTemaEsDistinto() = Pendientes.revisar("unicidad", repetidos(), PENDIENTES)

    private fun repetidos(): Map<String, Set<String>> {
        val r = linkedMapOf<String, Set<String>>()
        fun conEstilo(nombre: String, valor: () -> Any?) { r[nombre] = iguales { t -> Pendientes.conTema(t) { valor() } } }

        // Textos del tema.
        conEstilo("comentario") { Estilo.comentario }
        conEstilo("atras") { Estilo.atras }
        conEstilo("prefijo") { Estilo.prefijo }
        conEstilo("clave") { Estilo.clave("somnus") }
        conEstilo("widget_rotulos") { CoberturaTemas.CLAVES_WIDGET.joinToString(" | ") { Estilo.widget(it) } }
        r["pantallas"] = PANTALLAS.flatMap { p -> iguales { t -> Pendientes.conTema(t) { Estilo.pantalla(p, null).first } } }.toSet()

        // Números y fechas.
        conEstilo("numeral_17") { Estilo.numeral(17) }
        conEstilo("mes_10") { Estilo.mes(10) }
        conEstilo("semana") { Estilo.semana.joinToString(" ") }
        conEstilo("semana_corta") { Estilo.semanaCorta.joinToString(" ") }

        // Letra y forma (la del modo claro, o la única si el tema solo existe en oscuro).
        r["letra_titulo"] = iguales { tipoDe(it, it.soloOscuro).headlineMedium.fontFamily }
        r["letra_texto"] = iguales { tipoDe(it, it.soloOscuro).bodyLarge.fontFamily }
        r["radio"] = iguales { radioDe(it) }

        // Sonido y vibración. La escala que falta se cuenta en la cobertura, no aquí.
        r["escala_toque"] = iguales { Sentidos.acordeToque(it)?.contentToString() }
        r["vibracion"] = iguales { t -> Sentidos.Evento.entries.joinToString(" | ") { Sentidos.patron(t, it).toString() } }
        r["sonido_archivos"] = CoberturaTemas.SONIDOS_TEMA.flatMap { s -> iguales { t -> huellaDe("raw", s.replace("<id>", t.id)) } }.toSet()

        // Nombres de trofeos, de etapas y de campañas; la libreta (la de respaldo se cuenta en la cobertura).
        r["trofeos_nombres"] = comparten { t -> CATEGORIAS.mapNotNull { TrofeosTema.nombre(t, it) } }
        r["campana_etapas"] = comparten { Campanas.ETAPAS[it.id].orEmpty() }
        r["campana_titulos"] = comparten { Campanas.TITULOS[it.id].orEmpty() }
        r["libreta"] = iguales { contenedorDe(it).takeIf { c -> c != Contenedor.ESPIRAL } }
        return r
    }

    /** Los temas cuyo valor (si no es null) es igual al de otro tema. */
    private fun iguales(valor: (TemaId) -> Any?): Set<String> =
        TemaId.entries.mapNotNull { t -> valor(t)?.let { t.id to it } }.groupBy({ it.second }, { it.first }).values.filter { it.size > 1 }.flatten().toSet()

    /** Los temas que comparten al menos un nombre de su lista con otro tema. */
    private fun comparten(nombres: (TemaId) -> Collection<String>): Set<String> {
        val n = TemaId.entries.associate { it.id to nombres(it).map { s -> s.lowercase() }.toSet() }
        return n.filter { (id, s) -> n.any { (otro, s2) -> otro != id && s.any { it in s2 } } }.keys
    }

    /** Huella SHA-256 de un recurso, para comparar sonidos byte a byte (null si no existe). */
    private fun huellaDe(carpeta: String, nombre: String): String? =
        Pendientes.main("res/$carpeta").listFiles().orEmpty().firstOrNull { it.nameWithoutExtension == nombre }
            ?.let { f -> MessageDigest.getInstance("SHA-256").digest(f.readBytes()).joinToString("") { "%02x".format(it) } }

    companion object {
        /** Las nueve categorías de logros (las de `Logros.CATEGORIAS`). */
        val CATEGORIAS = listOf("Diario", "Mundo", "Cuerpo", "Mente", "Cultura", "Gente", "Constancia", "Curiosos", "Tuyos")

        /** Títulos de pantalla que todos los temas muestran. */
        val PANTALLAS = listOf("Hoy", "Diario", "Momentos", "Gastos", "Logros", "Cartas", "Salud", "Mapas", "Campañas", "Línea de tiempo", "Tu gente", "Más")

        /** Lo repetido hoy, por superficie. Cada lote borra lo que vuelve propio. */
        val PENDIENTES: Map<String, Set<String>> = mapOf(
            "atras" to setOf("laboratorio", "colonos", "botanico", "navidad", "seda"),
            "campana_etapas" to setOf("refugio", "orden", "colonos", "napoleon", "botanico", "navidad", "urss"),
            "clave" to setOf("romano", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "gotico", "espacio", "andalus", "mongol", "urss", "revolucion", "antiguas"),
            "comentario" to setOf("romano", "ciber", "heleno", "orden", "napoleon"),
            "escala_toque" to setOf("romano", "ciber", "heleno", "colonos", "gotico", "espacio"),
            "letra_texto" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "colonos", "botanico", "navidad", "seda", "gotico", "mongol", "renacimiento", "urss", "revolucion", "antiguas"),
            "letra_titulo" to setOf("romano", "colonos", "revolucion", "antiguas"),
            "mes_10" to setOf("ciber", "refugio", "orden", "colonos", "napoleon", "botanico", "navidad", "espacio", "revolucion"),
            "numeral_17" to setOf("romano", "orden", "laboratorio", "napoleon", "botanico", "navidad", "mongol", "renacimiento", "antiguas"),
            "pantallas" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "gotico", "espacio", "andalus", "mongol", "renacimiento", "urss", "revolucion", "antiguas"),
            "radio" to setOf("ciber", "heleno", "medieval", "distopia", "refugio", "orden", "napoleon", "navidad", "seda", "gotico", "andalus", "mongol", "renacimiento", "urss", "revolucion", "antiguas"),
            "semana" to setOf("ciber", "distopia", "refugio", "orden", "colonos", "napoleon", "botanico", "navidad", "espacio", "revolucion", "antiguas"),
            "semana_corta" to setOf("ciber", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "gotico", "espacio", "andalus", "mongol", "renacimiento", "urss", "revolucion", "antiguas"),
            "trofeos_nombres" to setOf("romano", "napoleon", "andalus", "renacimiento"),
        )
    }
}
