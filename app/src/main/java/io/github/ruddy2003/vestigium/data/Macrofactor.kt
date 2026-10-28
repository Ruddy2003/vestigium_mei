package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.net.Uri
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.zip.ZipInputStream

/** Lector mínimo de .xlsx (un .zip con XML): nombres de hoja, cadenas compartidas y celdas. Sin dependencias. */
object Xlsx {
    class Hoja(val nombre: String, val filas: List<List<Any?>>)

    private fun parser(b: ByteArray): XmlPullParser = Xml.newPullParser().apply {
        setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false); setInput(ByteArrayInputStream(b), "UTF-8")
    }

    private fun cadenas(b: ByteArray): List<String> {
        val out = ArrayList<String>(); val p = parser(b); var actual: StringBuilder? = null; var enT = false
        while (p.next() != XmlPullParser.END_DOCUMENT) when (p.eventType) {
            XmlPullParser.START_TAG -> when (p.name) { "si" -> actual = StringBuilder(); "t" -> enT = true }
            XmlPullParser.TEXT -> if (enT) actual?.append(p.text)
            XmlPullParser.END_TAG -> when (p.name) { "t" -> enT = false; "si" -> { out.add(actual?.toString() ?: ""); actual = null } }
        }
        return out
    }

    private fun columna(ref: String): Int { var n = 0; for (ch in ref) { if (!ch.isLetter()) break; n = n * 26 + (ch.uppercaseChar() - 'A' + 1) }; return n - 1 }

    private fun hoja(b: ByteArray, compartidas: List<String>): List<List<Any?>> {
        val filas = ArrayList<List<Any?>>(); val p = parser(b)
        var fila: ArrayList<Any?>? = null; var col = -1; var tipo: String? = null; var valor: StringBuilder? = null; var enValor = false
        while (p.next() != XmlPullParser.END_DOCUMENT) when (p.eventType) {
            XmlPullParser.START_TAG -> when (p.name) {
                "row" -> { fila = ArrayList(); col = -1 }
                "c" -> { col = p.getAttributeValue(null, "r")?.let { columna(it) } ?: (col + 1); tipo = p.getAttributeValue(null, "t"); valor = StringBuilder() }
                "v", "t" -> enValor = true
            }
            XmlPullParser.TEXT -> if (enValor) valor?.append(p.text)
            XmlPullParser.END_TAG -> when (p.name) {
                "v", "t" -> enValor = false
                "c" -> {
                    val f = fila; val txt = valor?.toString()
                    if (f != null && !txt.isNullOrEmpty()) {
                        val v: Any? = when (tipo) {
                            "s" -> txt.toIntOrNull()?.let { compartidas.getOrNull(it) }
                            "str", "inlineStr" -> txt
                            "b" -> if (txt == "1") 1.0 else 0.0
                            "e" -> null
                            else -> txt.toDoubleOrNull() ?: txt
                        }
                        while (f.size < col) f.add(null)
                        if (f.size == col) f.add(v) else f[col] = v
                    }
                    valor = null
                }
                "row" -> { fila?.let { filas.add(it) }; fila = null }
            }
        }
        return filas
    }

    fun leer(ins: InputStream): List<Hoja> {
        val a = HashMap<String, ByteArray>()
        ZipInputStream(ins).use { z -> while (true) { val e = z.nextEntry ?: break; if (!e.isDirectory && (e.name.endsWith(".xml") || e.name.endsWith(".rels"))) a[e.name.removePrefix("/")] = z.readBytes() } }
        val comp = a["xl/sharedStrings.xml"]?.let { cadenas(it) } ?: emptyList()
        val rels = HashMap<String, String>()
        a["xl/_rels/workbook.xml.rels"]?.let { b ->
            val p = parser(b)
            while (p.next() != XmlPullParser.END_DOCUMENT) if (p.eventType == XmlPullParser.START_TAG && p.name == "Relationship") {
                val t = p.getAttributeValue(null, "Target") ?: continue
                rels[p.getAttributeValue(null, "Id") ?: continue] = if (t.startsWith("/")) t.removePrefix("/") else "xl/$t"
            }
        }
        val out = ArrayList<Hoja>()
        a["xl/workbook.xml"]?.let { b ->
            val p = parser(b)
            while (p.next() != XmlPullParser.END_DOCUMENT) if (p.eventType == XmlPullParser.START_TAG && p.name == "sheet") {
                val nombre = p.getAttributeValue(null, "name") ?: "hoja"
                val ruta = rels[p.getAttributeValue(null, "r:id") ?: ""] ?: continue
                a[ruta]?.let { out.add(Hoja(nombre, hoja(it, comp))) }
            }
        }
        return out
    }
}

/**
 * Importa la exportación de MacroFactor (nutrición y Workouts) en .xlsx: resumen diario, alimentos, series y grupos musculares.
 * Reimportar un periodo reemplaza lo de esas fechas, así que se puede exportar de nuevo cuando quieras.
 */
object Macrofactor {
    private val NOMBRES = mapOf(
        "expenditure" to "gasto_kcal", "trend_weight_kg" to "peso_tendencia_kg", "weight_kg" to "peso_mf_kg", "calories_kcal" to "kcal",
        "protein_g" to "proteina_g", "fat_g" to "grasa_g", "carbs_g" to "carbohidratos_g", "target_calories_kcal" to "objetivo_kcal",
        "target_protein_g" to "objetivo_proteina_g", "target_fat_g" to "objetivo_grasa_g", "target_carbs_g" to "objetivo_carbohidratos_g",
        "steps" to "pasos_mf", "alcohol_g" to "alcohol_g", "caffeine_mg" to "cafeina_mg", "fiber_g" to "fibra_g", "sugars_g" to "azucares_g",
        "sugars_added_g" to "azucares_anadidos_g", "sodium_mg" to "sodio_mg", "water_g" to "agua_g", "saturated_fat_g" to "grasa_saturada_g",
        "cholesterol_mg" to "colesterol_mg", "potassium_mg" to "potasio_mg", "calcium_mg" to "calcio_mg", "iron_mg" to "hierro_mg",
        "magnesium_mg" to "magnesio_mg", "omega_3_g" to "omega_3_g",
    )
    private val MUSCULOS = mapOf(
        "chest" to "pecho", "quads" to "cuadriceps", "upper_back" to "espalda_alta", "glutes" to "gluteos", "lats" to "dorsales",
        "hamstrings" to "isquiotibiales", "biceps" to "biceps", "triceps" to "triceps", "front_delts" to "deltoide_anterior",
        "side_delts" to "deltoide_lateral", "lower_back" to "espalda_baja", "abs" to "abdomen", "calves" to "pantorrillas",
        "upper_traps" to "trapecio", "rear_delts" to "deltoide_posterior", "forearms" to "antebrazos", "obliques" to "oblicuos",
        "abductors" to "abductores", "adductors" to "aductores", "neck" to "cuello", "tibialis" to "tibial", "serratus" to "serrato",
    )
    private const val LB = 0.45359237

    /** Nombre de columna → clave y factor (las libras se pasan a kilos). */
    private fun clave(h: String): Pair<String, Double> {
        val lbs = h.contains("(lbs)", true) || h.contains("(lb)", true)
        val base = Texto.slug(h.replace("(lbs)", "(kg)", true).replace("(lb)", "(kg)", true))
        return (NOMBRES[base] ?: base) to if (lbs) LB else 1.0
    }

    private fun num(v: Any?): Double? = when (v) { is Double -> v; is String -> v.replace(",", "").toDoubleOrNull(); else -> null }

    private val F_US = DateTimeFormatter.ofPattern("M/d/yyyy")
    private fun fecha(v: Any?): String? = when (v) {
        is Double -> Fechas.iso(LocalDate.of(1899, 12, 30).plusDays(v.toLong()))
        is String -> v.trim().let { t -> runCatching { Fechas.iso(LocalDate.parse(t.take(10))) }.getOrNull() ?: runCatching { Fechas.iso(LocalDate.parse(t, F_US)) }.getOrNull() }
        else -> null
    }

    private val H12 = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
    private fun hora(v: Any?): String = when (v) {
        is Double -> { val m = Math.round((v % 1.0) * 1440).toInt(); "%02d:%02d".format(m / 60 % 24, m % 60) }
        is String -> v.trim().uppercase().let { t -> runCatching { LocalTime.parse(t, H12).toString().take(5) }.getOrNull() ?: runCatching { LocalTime.parse(t).toString().take(5) }.getOrNull() ?: t }
        else -> ""
    }

    private fun Xlsx.Hoja.cabeza(): List<String> = filas.firstOrNull()?.map { (it as? String)?.trim() ?: "" } ?: emptyList()

    fun importar(ctx: Context, uri: Uri): String {
        val hojas = ctx.contentResolver.openInputStream(uri)?.use { Xlsx.leer(it) } ?: return "No pude leer el archivo."
        if (hojas.isEmpty()) return "El archivo no parece un .xlsx de MacroFactor."
        val dias = LinkedHashMap<String, NutricionDia>()
        val comidas = ArrayList<Comida>(); val series = ArrayList<Serie>()
        val musSeries = HashMap<String, Map<String, Double>>(); val musVol = HashMap<String, Map<String, Double>>()

        for (h in hojas) {
            val cab = h.cabeza(); if (cab.isEmpty()) continue
            val iFecha = cab.indexOfFirst { it.equals("Date", true) }.takeIf { it >= 0 } ?: continue
            val cuerpo = h.filas.drop(1)
            when {
                // Alimentos
                "Food Name" in cab -> {
                    val iHora = cab.indexOf("Time"); val iNom = cab.indexOf("Food Name"); val iPor = cab.indexOf("Serving Size")
                    val iCant = cab.indexOf("Serving Qty"); val iPeso = cab.indexOfFirst { it.startsWith("Serving Weight") }
                    val nutr = cab.indices.filter { it !in setOf(iFecha, iHora, iNom, iPor, iCant, iPeso) && cab[it].isNotBlank() }
                    val porDia = HashMap<String, Int>()
                    for (r in cuerpo) {
                        val f = fecha(r.getOrNull(iFecha)) ?: continue
                        val nombre = (r.getOrNull(iNom) as? String)?.trim().orEmpty()
                        if (nombre.isEmpty()) continue
                        val k = porDia.merge(f, 1, Int::plus)!!
                        val valores = nutr.mapNotNull { i -> num(r.getOrNull(i))?.let { v -> val (c, x) = clave(cab[i]); c to Math.round(v * x * 100) / 100.0 } }.toMap()
                        comidas.add(Comida("mf_${f}_$k", f, hora(r.getOrNull(iHora)), nombre.take(160), (r.getOrNull(iPor) as? String) ?: "",
                            num(r.getOrNull(iCant)), num(r.getOrNull(iPeso)), valores))
                    }
                }
                // Series de Workouts
                "Exercise" in cab && cab.any { it.equals("Set Type", true) } -> {
                    fun i(n: String) = cab.indexOfFirst { it.startsWith(n, true) }
                    val iDur = i("Workout Duration"); val iW = cab.indexOf("Workout"); val iEj = cab.indexOf("Exercise"); val iBase = i("Exercise Base Weight")
                    val iTipo = i("Set Type"); val iPeso = cab.indexOfFirst { it.startsWith("Weight (") }; val iReps = cab.indexOf("Reps"); val iRir = cab.indexOf("RIR")
                    val iDuracion = cab.indexOf("Duration"); val iCorta = i("Distance short"); val iLarga = i("Distance long")
                    val lbs = iPeso >= 0 && cab[iPeso].contains("lb", true)
                    val porDia = HashMap<String, Int>()
                    for (r in cuerpo) {
                        val f = fecha(r.getOrNull(iFecha)) ?: continue
                        val ej = (r.getOrNull(iEj) as? String)?.trim().orEmpty()
                        if (ej.isEmpty()) continue
                        val k = porDia.merge(f, 1, Int::plus)!!
                        val dist = num(r.getOrNull(iCorta)) ?: num(r.getOrNull(iLarga))?.let { it * 1000 }
                        series.add(Serie("mfs_${f}_$k", f, (r.getOrNull(iW) as? String) ?: "", num(r.getOrNull(iDur)), ej.take(160), (r.getOrNull(iTipo) as? String) ?: "",
                            num(r.getOrNull(iPeso))?.let { Math.round((if (lbs) it * LB else it) * 100) / 100.0 }, num(r.getOrNull(iReps))?.toInt(), num(r.getOrNull(iRir)),
                            num(r.getOrNull(iDuracion)), dist, num(r.getOrNull(iBase))?.let { if (lbs) it * LB else it }, k))
                    }
                }
                // Series o volumen por grupo muscular
                cab.drop(1).any { it.endsWith("(sets)", true) } || h.nombre.contains("Muscle", true) -> {
                    val esVol = cab.drop(1).any { it.endsWith("(kg)", true) || it.endsWith("(lbs)", true) }
                    for (r in cuerpo) {
                        val f = fecha(r.getOrNull(iFecha)) ?: continue
                        val m = cab.indices.filter { it != iFecha }.mapNotNull { i ->
                            val v = num(r.getOrNull(i)) ?: return@mapNotNull null
                            val base = Texto.slug(cab[i].substringBefore("("))
                            (MUSCULOS[base] ?: base) to if (cab[i].contains("lb", true)) v * LB else v
                        }.toMap()
                        if (esVol) musVol[f] = m else musSeries[f] = m
                    }
                }
                // Resumen diario de nutrición
                cab.any { it.startsWith("Calories", true) } || cab.contains("Expenditure") -> {
                    for (r in cuerpo) {
                        val f = fecha(r.getOrNull(iFecha)) ?: continue
                        val valores = cab.indices.filter { it != iFecha && cab[it].isNotBlank() }.mapNotNull { i -> num(r.getOrNull(i))?.let { v -> val (c, x) = clave(cab[i]); c to Math.round(v * x * 100) / 100.0 } }.toMap()
                        dias[f] = NutricionDia(f, valores)
                    }
                }
            }
        }
        if (dias.isEmpty() && comidas.isEmpty() && series.isEmpty()) return "No reconocí hojas de MacroFactor. Exporta desde Más → Data Management → Data Export."

        // Horario de comidas y de la última cafeína (esto último importa mucho para el sueño).
        comidas.groupBy { it.fecha }.forEach { (f, l) ->
            val horas = l.map { it.hora }.filter { it.matches(Regex("\\d{2}:\\d{2}")) }.sorted()
            val caf = l.filter { (it.valores["cafeina_mg"] ?: 0.0) > 0 }.map { it.hora }.filter { it.matches(Regex("\\d{2}:\\d{2}")) }.maxOrNull()
            val previo = dias[f] ?: NutricionDia(f, emptyMap())
            dias[f] = previo.copy(comidas = l.size, primera = horas.firstOrNull(), ultima = horas.lastOrNull(), cafeina_ultima = caf)
        }
        val fechasEntreno = (series.map { it.fecha } + musSeries.keys + musVol.keys).toSortedSet()
        val entrenos = fechasEntreno.map { f ->
            val l = series.filter { it.fecha == f }
            val efectivas = l.filter { !it.tipo.contains("Warm", true) }
            EntrenoDia(f, l.map { it.entreno }.filter { it.isNotBlank() }.distinct(),
                l.groupBy { it.entreno }.values.mapNotNull { g -> g.firstOrNull()?.entreno_s }.takeIf { it.isNotEmpty() }?.sum()?.let { Math.round(it / 6.0) / 10.0 },
                l.size, efectivas.size, efectivas.sumOf { it.reps ?: 0 },
                Math.round(efectivas.sumOf { (it.peso_kg ?: 0.0) * (it.reps ?: 0) } * 10) / 10.0,
                musSeries[f] ?: emptyMap(), musVol[f]?.mapValues { Math.round(it.value * 10) / 10.0 } ?: emptyMap(), fuerza_ref_kg = fuerzaReferencia(l))
        }
        val todas = (dias.keys + comidas.map { it.fecha } + fechasEntreno)
        val desde = todas.min(); val hasta = todas.max()
        Repo.importarMacrofactor(desde, hasta, dias.values.toList(), comidas, series, entrenos)
        return "Importé del $desde al $hasta: ${dias.size} días de nutrición, ${comidas.size} alimentos, ${entrenos.count { it.series > 0 }} entrenamientos y ${series.size} series."
    }

    /**
     * El ejercicio fijo de referencia para medir la fuerza (H-7-14): el press inclinado con mancuernas, escrito en inglés o en
     * español («Incline Dumbbell Press», «Press inclinado con mancuernas»). Uno solo, para que la cifra no dependa de qué
     * entrenaste ese día: el volumen total sube o baja según la sesión.
     */
    fun esReferencia(ejercicio: String): Boolean {
        val s = Texto.slug(ejercicio)
        return "inclin" in s && ("mancuern" in s || "dumbbell" in s || Regex("(^|_)db(_|$)").containsMatchIn(s)) &&
            ("press" in s || "banc" in s || "bench" in s)
    }

    /** La mejor 1RM estimada ([e1rm]) del día en el ejercicio de referencia; null si ese día no lo hiciste. */
    fun fuerzaReferencia(series: List<Serie>): Double? = series.filter { esReferencia(it.ejercicio) }.mapNotNull { e1rm(it) }.maxOrNull()

    /** 1RM estimado (Epley) de una serie efectiva de hasta 12 repeticiones. */
    fun e1rm(s: Serie): Double? {
        val p = s.peso_kg ?: return null; val r = s.reps ?: return null
        if (r <= 0 || r > 12 || s.tipo.contains("Warm", true) || p <= 0) return null
        return Math.round(p * (1 + r / 30.0) * 10) / 10.0
    }
}
