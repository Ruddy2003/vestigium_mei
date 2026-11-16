package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.ciencia.Campanas
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.Contenedor
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Frases
import io.github.ruddy2003.vestigium.ui.MapasCampana
import io.github.ruddy2003.vestigium.ui.NombresNuevos
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Textos
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
        // Básico (TemaId.sinDiseno) queda fuera a propósito: es el tema sin diseño, para probar la app, y no tiene lo suyo
        // (ni sonidos, ni ícono, ni sello, ni gato, ni frases, ni nombres propios). Sus `when (tema)` sí los vigila el compilador.
        fun superficie(nombre: String, falta: (TemaId) -> Boolean) { h[nombre] = TemaId.entries.filterNot { it.sinDiseno }.filter(falta).map { it.id }.toSet() }
        fun Id(t: TemaId) = t.id.replaceFirstChar { it.uppercase() }

        // Mapas que todavía van por texto (hasta que pasen a `when (tema)` exhaustivos).
        superficie("sonidos_mapa") { it.id !in Sentidos.SONIDOS }
        // Los temas con caras (Psicodelia) tienen además el juego de su cara intensa, con los 11 sonidos.
        superficie("sonidos_mapa_intensa") { it.conCaras && Sentidos.SONIDOS_INTENSA[it.id]?.size != Sentidos.SONIDOS[it.id]?.size }
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
        superficie("textos") { t -> Textos.CLAVES.any { Textos.de(t, it) == null } }

        // Trofeos, salón, campaña y libreta ya no tienen respaldo: sus `when (tema)` exhaustivos los vigila el compilador.
        superficie("escala_toque") { Sentidos.acordeToque(it).isEmpty() }

        // Recursos.
        val manifiesto = Pendientes.main("AndroidManifest.xml").readText()
        superficie("icono_alias") { "android:name=\".Icono${Id(it)}\"" !in manifiesto }
        superficie("ic_launcher") { !Pendientes.main("res/mipmap-anydpi-v26/ic_launcher_${it.id}.xml").exists() }
        listOf("logo", "logo_fondo", "huella", "huella_fondo", "papel").forEach { r -> superficie(r) { !recurso("drawable-nodpi", "${r}_${it.id}") } }
        // Un tema con caras (Psicodelia) lleva los de su cara intensa con «_tinta» al final (`docs/diseno/temas/psicodelia_base.md`).
        superficie("widget_drawables") { t ->
            val base = if (t == TemaId.ROMANO) "" else "_${t.id}"
            (if (t.conCaras) listOf(base, base + "_tinta") else listOf(base)).any { suf ->
                listOf("widget_fondo", "widget_boton", "widget_boton_activo", "widget_boton_ambar").any { !recurso("drawable", it + suf) } ||
                    !recurso("drawable-v31", "widget_fondo$suf")
            }
        }
        // Y su papel de la cara intensa: `papel_<id>_cartel` (el de la cara base es `papel_<id>`, arriba).
        superficie("papel_cara") { t -> t.conCaras && !recurso("drawable-nodpi", "papel_${t.id}_cartel") }
        superficie("sonidos") { t -> SONIDOS_TEMA.any { !recurso("raw", it.replace("<id>", t.id)) } }
        superficie("sonido_tecla") { !recurso("raw", "s_tecla_${it.id}") }
        superficie("sonido_error") { !recurso("raw", "s_${it.id}_error") }
        superficie("sonido_cambio") { !recurso("raw", "s_${it.id}_cambio") }
        superficie("sonido_rascar") { !recurso("raw", "s_rascar_${it.id}") }
        superficie("sonidos_intensa") { t -> t.conCaras && SONIDOS_INTENSA.any { !recurso("raw", it.replace("<id>", t.id)) } }
        val temas = Pendientes.main("res/values/themes.xml").readText()
        superficie("arranque") { "Theme.Vestigium.Arranque.${Id(it)}\"" !in temas }

        // El gato de la banda de Día: claro y oscuro (solo oscuro si el tema no tiene claro), cada uno con sus 2 acciones propias.
        // Los temas con caras (Psicodelia) tienen además la banda de su cara intensa en sus tres escalones (`Gato.bandas`).
        val gato = Pendientes.main("assets/gato/gato.json").takeIf { it.exists() }
            ?.let { runCatching { Json.parseToJsonElement(it.readText()).jsonObject["temas"]?.jsonObject }.getOrNull() }
        superficie("gato") { t ->
            val claves = io.github.ruddy2003.vestigium.ui.Gato.bandas(t).flatMap { b -> (if (t.soloOscuro) listOf("o") else listOf("c", "o")).map { "${b}_$it" } }
            gato == null || claves.any { k -> ((gato[k] as? kotlinx.serialization.json.JsonObject)?.get("especiales") as? JsonArray)?.size?.let { it < 2 } ?: true }
        }

        // El cuadro de Hoy (plan de historia y escenas): la escena de su personaje y su banderita. Terminal y Navidad no
        // lo llevan (`sinCuadro`); a los demás les va llegando por tandas, y cada tanda borra los suyos de los pendientes.
        superficie("cuadro_escena") { !io.github.ruddy2003.vestigium.ui.escenas.sinCuadro(it) && io.github.ruddy2003.vestigium.ui.escenas.escenaDe(it) == null }
        superficie("cuadro_banderita") { !io.github.ruddy2003.vestigium.ui.escenas.sinCuadro(it) && io.github.ruddy2003.vestigium.ui.escenas.banderitaHistoria(it) == null }
        superficie("cuadro_gato") { !io.github.ruddy2003.vestigium.ui.escenas.sinCuadro(it) && io.github.ruddy2003.vestigium.ui.escenas.pelajeDe(it) == null }
        return h
    }

    private fun recurso(carpeta: String, nombre: String) = Pendientes.main("res/$carpeta").listFiles().orEmpty().any { it.nameWithoutExtension == nombre }

    companion object {
        val CLAVES_WIDGET = listOf("animo", "momento", "gasto", "sueno", "habitos", "hoy", "cifra", "foto", "enfoque")

        /** Los 13 sonidos de cada tema (`<id>` se cambia por el id). */
        val SONIDOS_TEMA = listOf("toque", "guardar", "logro", "pasar", "elegir", "abrir", "tema", "presentacion", "anual").map { "s_<id>_$it" } +
            listOf("n_<id>_aviso", "n_<id>_solemne", "t_<id>_llamada", "t_<id>_notificacion")

        /** Los del lote 6: error, cambio, la tecla del diario y el rascado de la lámina. */
        val SONIDOS_LOTE6 = listOf("s_<id>_error", "s_<id>_cambio", "s_tecla_<id>", "s_rascar_<id>")

        /**
         * El juego de la cara intensa de los temas con caras (Psicodelia v2, tanda 7): lo que suena dentro de la app. Sin
         * avisos ni tonos del teléfono (se quedan los de la cara de base) y sin rascado (la misma moneda en las dos caras).
         */
        val SONIDOS_INTENSA = listOf("toque", "guardar", "logro", "pasar", "elegir", "abrir", "tema", "presentacion", "anual", "error", "cambio").map { "s_<id>_tinta_$it" } +
            listOf("s_tecla_<id>_tinta")

        /** Los 16 temas que todavía no tienen su cuadro de Hoy: Helénico lo tiene desde la tanda P4a; Gótico y Ciudad neón, desde la P4b; Romano y Al-Ándalus, desde la S1; Pirata, desde la S2; Espacio y Distopía, desde la S3; Horror cósmico y Ciudad verde, desde la S4; Roma, desde la S5; Scriptorium, desde la S6; Renacimiento, desde la S6; Ruta de la Seda, desde la S7; Revolución francesa, desde la S8; Vapor victoriano, desde la S8; siguen las demás tandas S. */
        private val SIN_CUADRO = setOf("egipto", "templario", "refugio", "orden", "laboratorio", "colonos", "napoleon", "deco", "botanico", "edo", "abismo", "meso", "mongol", "urss", "antiguas", "cueva")

        /** Los temas nuevos del lote 10 (`docs/diseno/PLAN_TEMAS.md`): sus cuadros van después de la tanda S19. */
        private val SIN_CUADRO_LOTE10 = setOf("frente", "arcontes", "galia", "nouveau", "psicodelia")

        /**
         * Lo que falta hoy, por superficie. Del plan de diseño no falta nada desde el lote 9. Lo que queda es del plan de
         * historia y escenas: el cuadro de Hoy (la escena, la banderita y el pelaje del gato) de cada tema, que va llegando por tandas.
         */
        val PENDIENTES: Map<String, Set<String>> = mapOf("cuadro_escena" to SIN_CUADRO + SIN_CUADRO_LOTE10, "cuadro_banderita" to SIN_CUADRO + SIN_CUADRO_LOTE10, "cuadro_gato" to SIN_CUADRO + SIN_CUADRO_LOTE10)

        /** Despachos que el compilador no vigila, por archivo y tipo. Cada lote baja los suyos. */
        val DESPACHOS: Map<String, Int> = emptyMap()
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
