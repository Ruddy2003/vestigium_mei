package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.ciencia.Campanas
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.Contenedor
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Frases
import io.github.ruddy2003.vestigium.ui.MapasCampana
import io.github.ruddy2003.vestigium.ui.NombresNuevos
import io.github.ruddy2003.vestigium.ui.SalonesTema
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrofeosTema
import io.github.ruddy2003.vestigium.ui.contenedorDe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Test

/**
 * Plan de diseño, principio 1b: cada tema tiene todo lo suyo (entradas en los mapas, recursos, sonidos, ícono, gato…).
 * Lo que falta hoy está en [PENDIENTES]; ver [Pendientes] para cómo se usa la lista.
 */
class CoberturaTemas {
    @Test fun cadaTemaTieneLoSuyo() = Pendientes.revisar("cobertura", huecos(), PENDIENTES)

    /**
     * Despachos de diseño que el compilador no vigila: ramas que juntan temas (`TemaId.A, TemaId.B ->`), comparaciones
     * (`== TemaId.X`, `in setOf(TemaId.…)`) y `else ->` en un `when (tema)`. Ninguna cuenta puede subir; al final (lote 7)
     * quedan en cero. Es una búsqueda en el texto, aproximada: un `when (t)` que no sea de temas también cuenta.
     */
    @Test fun despachosPorTema() = Pendientes.revisarCuentas("despachos", Despachos.contar(), DESPACHOS)

    private fun huecos(): Map<String, Set<String>> {
        val h = linkedMapOf<String, Set<String>>()
        fun superficie(nombre: String, falta: (TemaId) -> Boolean) { h[nombre] = TemaId.entries.filter(falta).map { it.id }.toSet() }
        fun Id(t: TemaId) = t.id.replaceFirstChar { it.uppercase() }

        // Mapas que todavía van por texto (hasta que pasen a `when (tema)` exhaustivos).
        superficie("sonidos_mapa") { it.id !in Sentidos.SONIDOS }
        superficie("avisos_mapa") { it.id !in Avisos.SONIDOS }
        superficie("campana_titulos") { it.id !in Campanas.TITULOS }
        superficie("campana_etapas") { Campanas.ETAPAS[it.id]?.size != 7 }
        superficie("nombres") { NombresNuevos.de(it, "La cinta") == null }
        superficie("widget_rotulos") { t -> Pendientes.conTema(t) { CLAVES_WIDGET.all { Estilo.widget(it) == it.uppercase() } } }
        superficie("marcas_calendario") { t ->
            val dias = (0 until 366).map { java.time.LocalDate.of(2024, 1, 1).plusDays(it.toLong()).toString() }
            Pendientes.conTema(t) { dias.none { Estilo.marca(it) != null } }
        }
        superficie("vidas") { t -> OtrasVidas.DE_TEMAS.none { it.tema == t.id } }
        superficie("frases_20") { Frases.de(it).size < 20 }

        // Lotes de diseño: trofeos, salón, campaña y libreta propios.
        superficie("trofeos") { it !in TrofeosTema.TEMAS }
        superficie("salon") { it !in SalonesTema.TEMAS }
        superficie("campana_mapa") { it !in MapasCampana.TEMAS }
        superficie("libreta") { contenedorDe(it) == Contenedor.ESPIRAL }
        superficie("escala_toque") { Sentidos.acordeToque(it) == null }

        // Recursos.
        val manifiesto = Pendientes.main("AndroidManifest.xml").readText()
        superficie("icono_alias") { "android:name=\".Icono${Id(it)}\"" !in manifiesto }
        superficie("ic_launcher") { !Pendientes.main("res/mipmap-anydpi-v26/ic_launcher_${it.id}.xml").exists() }
        listOf("logo", "logo_fondo", "huella", "huella_fondo", "papel").forEach { r -> superficie(r) { !recurso("drawable-nodpi", "${r}_${it.id}") } }
        superficie("widget_drawables") { t ->
            val suf = if (t == TemaId.ROMANO) "" else "_${t.id}"
            listOf("widget_fondo", "widget_boton", "widget_boton_activo", "widget_boton_ambar").any { !recurso("drawable", it + suf) } ||
                !recurso("drawable-v31", "widget_fondo$suf")
        }
        superficie("sonidos") { t -> SONIDOS_TEMA.any { !recurso("raw", it.replace("<id>", t.id)) } }
        superficie("sonido_tecla") { !recurso("raw", "s_tecla_${it.id}") }
        superficie("sonido_error") { !recurso("raw", "s_${it.id}_error") }
        superficie("sonido_cambio") { !recurso("raw", "s_${it.id}_cambio") }
        val temas = Pendientes.main("res/values/themes.xml").readText()
        superficie("arranque") { "Theme.Vestigium.Arranque.${Id(it)}\"" !in temas }

        // El gato de la banda de Día: claro y oscuro (solo oscuro si el tema no tiene claro), cada uno con sus 2 acciones propias.
        val gato = Pendientes.main("assets/gato/gato.json").takeIf { it.exists() }
            ?.let { runCatching { Json.parseToJsonElement(it.readText()).jsonObject["temas"]?.jsonObject }.getOrNull() }
        superficie("gato") { t ->
            val claves = (if (t.soloOscuro) listOf("o") else listOf("c", "o")).map { "${t.id}_$it" }
            gato == null || claves.any { k -> ((gato[k] as? kotlinx.serialization.json.JsonObject)?.get("especiales") as? JsonArray)?.size?.let { it < 2 } ?: true }
        }
        return h
    }

    private fun recurso(carpeta: String, nombre: String) = Pendientes.main("res/$carpeta").listFiles().orEmpty().any { it.nameWithoutExtension == nombre }

    companion object {
        val CLAVES_WIDGET = listOf("animo", "momento", "gasto", "sueno", "habitos", "hoy", "cifra", "foto", "enfoque")

        /** Los 13 sonidos de cada tema (`<id>` se cambia por el id). */
        val SONIDOS_TEMA = listOf("toque", "guardar", "logro", "pasar", "elegir", "abrir", "tema", "presentacion", "anual").map { "s_<id>_$it" } +
            listOf("n_<id>_aviso", "n_<id>_solemne", "t_<id>_llamada", "t_<id>_notificacion")

        /** Lo que falta hoy, por superficie. Cada lote borra lo que completa. */
        val PENDIENTES: Map<String, Set<String>> = mapOf(
            "arranque" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "urss", "gotico", "espacio", "andalus", "mongol", "renacimiento", "revolucion", "antiguas"),
            "campana_mapa" to setOf("laboratorio", "urss", "espacio"),
            "escala_toque" to setOf("medieval", "distopia", "refugio", "orden", "napoleon", "navidad", "seda", "urss", "mongol", "renacimiento", "revolucion", "antiguas"),
            "frases_20" to setOf("refugio", "orden", "laboratorio", "napoleon", "navidad", "seda", "urss", "gotico", "espacio", "andalus", "mongol", "renacimiento", "revolucion"),
            "gato" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "urss", "gotico", "espacio", "andalus", "mongol", "renacimiento", "revolucion", "antiguas"),
            "libreta" to setOf("laboratorio", "urss", "espacio"),
            "marcas_calendario" to setOf("ciber", "heleno", "distopia", "refugio", "orden", "laboratorio", "colonos"),
            "nombres" to setOf("ciber"),
            "papel" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "urss", "gotico", "espacio", "andalus", "mongol", "renacimiento"),
            "salon" to setOf("laboratorio", "urss", "espacio"),
            "sonido_cambio" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "urss", "gotico", "espacio", "andalus", "mongol", "renacimiento", "revolucion", "antiguas"),
            "sonido_error" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "urss", "gotico", "espacio", "andalus", "mongol", "renacimiento", "revolucion", "antiguas"),
            "sonido_tecla" to setOf("romano", "ciber", "heleno", "medieval", "distopia", "refugio", "orden", "laboratorio", "colonos", "napoleon", "botanico", "navidad", "seda", "urss", "gotico", "espacio", "andalus", "mongol", "renacimiento", "revolucion", "antiguas"),
            "trofeos" to setOf("laboratorio", "urss", "espacio"),
            "vidas" to setOf("antiguas"),
        )

        /** Despachos que el compilador no vigila, por archivo y tipo. Cada lote baja los suyos. */
        val DESPACHOS: Map<String, Int> = mapOf(
            "reportes/Reportes.kt · agrupadas" to 3,
            "reportes/Reportes.kt · comparaciones" to 3,
            "ui/Animaciones.kt · else" to 1,
            "ui/Bases.kt · agrupadas" to 6,
            "ui/Cartas.kt · comparaciones" to 4,
            "ui/Componentes.kt · agrupadas" to 1,
            "ui/Componentes.kt · comparaciones" to 7,
            "ui/Componentes.kt · else" to 2,
            "ui/Contenedores.kt · else" to 1,
            "ui/Decoracion.kt · agrupadas" to 3,
            "ui/Decoracion.kt · comparaciones" to 1,
            "ui/Decoracion.kt · else" to 2,
            "ui/Estilo.kt · agrupadas" to 10,
            "ui/Estilo.kt · comparaciones" to 3,
            "ui/Estilo.kt · else" to 7,
            "ui/FondosTelefono.kt · agrupadas" to 3,
            "ui/FondosTelefono.kt · comparaciones" to 1,
            "ui/FondosTelefono.kt · else" to 1,
            "ui/Hoja.kt · comparaciones" to 1,
            "ui/IconoApp.kt · comparaciones" to 2,
            "ui/IconosPestana.kt · agrupadas" to 1,
            "ui/LibroDiario.kt · comparaciones" to 6,
            "ui/Libros.kt · agrupadas" to 5,
            "ui/Libros.kt · comparaciones" to 30,
            "ui/Libros.kt · else" to 7,
            "ui/MapasCampana.kt · agrupadas" to 2,
            "ui/MapasCampana.kt · else" to 4,
            "ui/MapasMas.kt · comparaciones" to 1,
            "ui/MapasMas.kt · else" to 6,
            "ui/Relacion.kt · agrupadas" to 7,
            "ui/Relacion.kt · comparaciones" to 1,
            "ui/Relacion.kt · else" to 3,
            "ui/SalonesMas.kt · else" to 2,
            "ui/SalonesTema.kt · else" to 2,
            "ui/Sentidos.kt · agrupadas" to 9,
            "ui/Sentidos.kt · comparaciones" to 2,
            "ui/Sentidos.kt · else" to 5,
            "ui/Temas.kt · comparaciones" to 1,
            "ui/Temas.kt · else" to 2,
            "ui/TrofeosMas.kt · else" to 2,
            "ui/TrofeosTema.kt · agrupadas" to 1,
            "ui/TrofeosTema.kt · comparaciones" to 1,
            "ui/TrofeosTema.kt · else" to 2,
            "ui/pantallas/Hall.kt · agrupadas" to 10,
            "ui/pantallas/Hall.kt · else" to 2,
            "ui/pantallas/HallImagen.kt · comparaciones" to 1,
            "ui/pantallas/Hoy.kt · agrupadas" to 2,
            "ui/pantallas/Hoy.kt · comparaciones" to 5,
            "ui/pantallas/Hoy.kt · else" to 2,
            "ui/pantallas/Mapa.kt · agrupadas" to 4,
            "ui/pantallas/Mapa.kt · comparaciones" to 1,
            "ui/pantallas/Mapa.kt · else" to 2,
            "ui/pantallas/Mundo.kt · agrupadas" to 3,
            "ui/pantallas/Mundo.kt · else" to 1,
        )
    }
}

/** Cuenta, en el código de diseño, los despachos por tema que el compilador no vigila. */
object Despachos {
    private val CARPETAS = listOf("ui", "reportes", "widget", "avisos")
    private val ARCHIVOS = listOf("ciencia/Campanas.kt", "data/OtrasVidas.kt", "data/Enfoque.kt")
    private val AGRUPADA = Regex("""TemaId\.\w+\s*,\s*TemaId\.\w+.*->""")
    private val COMPARACION = Regex("""(==|!=)\s*TemaId\.|\bin\s+(setOf|listOf)\(\s*TemaId\.""")
    private val WHEN_TEMA = Regex("""when\s*\(\s*(tema|t|Estilo\.tema|tema\.id|t\.id)\s*\)\s*\{""")

    fun contar(): Map<String, Int> {
        val base = Pendientes.main("java/io/github/ruddy2003/vestigium")
        val archivos = CARPETAS.flatMap { c -> base.resolve(c).walkTopDown().filter { it.isFile && it.extension == "kt" }.toList() } +
            ARCHIVOS.map { base.resolve(it) }.filter { it.exists() }
        val cuentas = sortedMapOf<String, Int>()
        archivos.forEach { f ->
            val nombre = f.relativeTo(base).invariantSeparatorsPath
            val texto = f.readText()
            val lineas = texto.lines()
            cuentas["$nombre · agrupadas"] = lineas.count { AGRUPADA.containsMatchIn(it) }
            cuentas["$nombre · comparaciones"] = lineas.count { COMPARACION.containsMatchIn(it) }
            cuentas["$nombre · else"] = elsesEnWhen(texto)
        }
        return cuentas.filterValues { it > 0 }
    }

    /** Cuántos `when (tema) { … }` tienen un `else ->` en su primer nivel. */
    fun elsesEnWhen(texto: String): Int = WHEN_TEMA.findAll(texto).count { m ->
        var i = m.range.last + 1; var nivel = 1; var conElse = false
        while (i < texto.length && nivel > 0) {
            val c = texto[i]
            if (c == '{') nivel++
            else if (c == '}') nivel--
            else if (nivel == 1 && texto.startsWith("else", i) && (texto[i - 1] in " \t\n;{") && texto.substring(i + 4).trimStart().startsWith("->")) conElse = true
            i++
        }
        conElse
    }
}
