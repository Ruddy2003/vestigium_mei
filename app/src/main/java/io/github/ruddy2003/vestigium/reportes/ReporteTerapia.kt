package io.github.ruddy2003.vestigium.reportes

import io.github.ruddy2003.vestigium.data.porNumero

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import io.github.ruddy2003.vestigium.ciencia.Terapia
import io.github.ruddy2003.vestigium.data.Fechas
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * El reporte para terapia en PDF: tamaño carta, varias páginas, en blanco y negro con un solo color de acento (para imprimirlo),
 * sin los colores ni las letras del tema. Lo que lleva y lo que no está en [Terapia].
 */
object ReporteTerapia {
    private const val ANCHO = 612; private const val ALTO = 792; private const val MARGEN = 48f
    private const val TINTA = 0xFF1E1E1E.toInt(); private const val TENUE = 0xFF5F6368.toInt(); private const val ACENTO = 0xFF2F5D8A.toInt()
    private const val SEGUNDO = 0xFFA3AAB2.toInt(); private const val LINEA = 0xFFDADCE0.toInt()
    private const val COL = 196f

    /** Genera el PDF en la carpeta de exportación (la que comparte el FileProvider) y lo devuelve. */
    fun pdf(ctx: Context, op: Terapia.Opciones): File {
        val d = Terapia.calcular(op)
        // Dos pasadas: la primera solo cuenta las páginas, para poner «página X de Y» en cada una.
        val total = Pdf(null, d).also { dibujar(it, d) }.terminar(null)
        val dir = File(File(ctx.cacheDir, "export"), "terapia").apply { mkdirs(); listFiles()?.forEach { it.delete() } }
        val f = File(dir, "reporte_terapia_${op.ini}_a_${op.fin}.pdf")
        Pdf(total, d).also { dibujar(it, d) }.terminar(f)
        return f
    }

    /** Las mismas páginas como imágenes (612 × 792), para las capturas de diseño. */
    fun imagenes(op: Terapia.Opciones): List<Bitmap> {
        val d = Terapia.calcular(op)
        val total = Pdf(null, d, ArrayList()).also { dibujar(it, d) }.terminar(null)
        val l = ArrayList<Bitmap>()
        Pdf(total, d, l).also { dibujar(it, d) }.terminar(null)
        return l
    }

    // ------------------------------------------------------------------ formato de las cifras

    private fun n0(x: Double) = "%,.0f".format(x)
    private fun n1(x: Double) = "%.1f".format(x)
    private fun pct(x: Double) = "${(x * 100).roundToInt()} %"
    private fun signo(x: Double, dec: Boolean = false) = (if (x > 0) "+" else if (x < 0) "−" else "") + if (dec) n1(abs(x)) else n0(abs(x))
    private fun hora(h: Double): String {
        val m = ((h * 60).roundToInt() % 1440 + 1440) % 1440
        return "%02d:%02d".format(m / 60, m % 60)
    }
    private fun horaYVariacion(p: Pair<Double, Double?>?) = p?.let { (h, s) -> hora(h) + (s?.let { " ± ${(it * 60).roundToInt()} min" } ?: "") }
    private fun dias(n: Int) = if (n == 1) "1 día" else "$n días"
    private fun fechaCorta(f: String) = Fechas.corto(f)
    private fun lista(l: List<Pair<String, Int>>) = l.joinToString(" · ") { "${it.first} (${it.second})" }

    /** La regla de los otros reportes ([Reportes]): si la diferencia cabe en un error típico, se dice «parecido». */
    private fun cambio(a: Double?, b: Double?, unidad: String, dec: Boolean, se: Double?): String? {
        if (a == null || b == null) return null
        val d = a - b
        if (abs(d) < (if (dec) 0.05 else 0.5)) return "igual que los $unidad anteriores"
        if (se != null && abs(d) < se) return "parecido a los $unidad anteriores"
        return "${signo(d, dec)} frente a los $unidad anteriores"
    }

    // ------------------------------------------------------------------ el contenido

    private fun dibujar(p: Pdf, d: Terapia.Datos) {
        val op = d.op
        val anteriores = "${porNumero(d.n, "1 día", "${d.n} días")}"
        p.encabezado(
            "Reporte para terapia",
            "Últimos ${porNumero(d.n, "1 día", "${d.n} días")}: del ${Fechas.larga(op.ini)} al ${Fechas.larga(op.fin)}",
            "Autorregistro hecho con Vestigium Mei. Describe lo que anoté y lo que midió el teléfono; no es un instrumento diagnóstico. " +
                "El ánimo y la energía van de 1 a 7 (4 = neutral). Un guion (—) es un dato que no hay.",
        )

        p.seccion("Qué tan completo está")
        p.clave("Días con diario", "${d.diasDiario} de ${d.n}")
        p.clave("Avisos de ánimo", if (d.avisosMostrados == 0) "Sin avisos mostrados en el periodo."
            else "Respondí ${d.avisosRespondidos} de ${d.avisosMostrados} mostrados (${pct(d.avisosRespondidos.toDouble() / d.avisosMostrados)}); se planearon ${d.avisosPlaneados}.")
        if (d.diasSoloAvisos > 0) p.nota("${dias(d.diasSoloAvisos)} sin ánimo del diario, solo con avisos: no cuentan en el ánimo medio.")

        // La tabla lleva solo las columnas de las secciones elegidas; sin ninguna, no sale.
        class Col(val titulo: String, val peso: Float, val valor: (Terapia.Fila) -> String)
        val cols = buildList {
            if (op.animo) {
                add(Col("Ánimo", 0.8f) { it.valencia?.toString() ?: "—" })
                add(Col("Energía", 0.8f) { f -> f.activacion?.let(::n1) ?: "—" })
            }
            if (op.sueno) {
                add(Col("Sueño (h)", 0.9f) { f -> f.suenoH?.let(::n1) ?: "—" })
                add(Col("Calidad", 0.8f) { f -> f.calidad?.let { "$it de 5" } ?: "—" })
            }
            if (op.cuerpo) {
                add(Col("kcal vs. objetivo", 1.3f) { f -> f.kcal?.let { k -> f.objetivoKcal?.let { signo(k - it) } ?: n0(k) } ?: "—" })
                add(Col("Gimnasio", 1f) { f -> if (!f.gimnasio) "—" else f.gimnasioMin?.let { "${it.roundToInt()} min" } ?: "sí" })
            }
            if (op.privado) {
                add(Col("Impulso", 0.8f) { f -> f.impulso?.let { "$it de 5" } ?: "—" })
                add(Col("Recaída", 0.8f) { f -> when (f.recaida) { true -> "sí"; false -> "no"; null -> "—" } })
            }
        }
        if (cols.isNotEmpty()) {
            p.seccion("Día por día")
            val marcarDificil = op.animo
            p.tabla(listOf("Día") + cols.map { it.titulo }, d.filas.map { f -> listOf(fechaCorta(f.fecha) + if (marcarDificil && f.dificil) " *" else "") + cols.map { it.valor(f) } },
                listOf(1.5f) + cols.map { it.peso })
            listOfNotNull(
                "* Día que marqué como difícil.".takeIf { op.animo },
                "«kcal vs. objetivo»: lo que comí menos mi objetivo de ese día (sin objetivo, lo que comí).".takeIf { op.cuerpo },
            ).joinToString(" ").takeIf { it.isNotEmpty() }?.let { p.nota(it) }
        }

        if (op.animo) {
            p.seccion("Ánimo")
            p.graficaLinea(d.fechas, listOf(d.filas.map { it.valencia?.toDouble() } to ACENTO, d.filas.map { it.activacion } to SEGUNDO), 1.0, 7.0, (1..7).map { it.toDouble() })
            p.leyenda(listOf("Ánimo del diario" to ACENTO, "Energía" to SEGUNDO))
            p.clave("Ánimo medio del diario", d.valMedia?.let { n1(it) + " de 7" + (cambio(it, d.valAnterior, anteriores, true, d.errorValencia)?.let { c -> " · $c" } ?: "") } ?: "—")
            p.clave("Energía media", d.actMedia?.let { n1(it) + " de 7" } ?: "—")
            val dificiles = d.filas.filter { it.dificil }
            p.clave("Días difíciles", if (dificiles.isEmpty()) "Ninguno marcado." else "${dificiles.size}: " + dificiles.joinToString(", ") { fechaCorta(it.fecha) })
            p.clave("Registros de ánimo en el día", "${d.registrosAnimo}")
            if (d.emociones.isNotEmpty()) p.clave("Emociones más nombradas", lista(d.emociones))
            d.negativasPct?.let { p.clave("Emociones desagradables", "${pct(it)} de las que nombré") }
            if (d.pensamientos.isNotEmpty()) p.clave("En qué pensaba", lista(d.pensamientos))
            d.acompanadoPct?.let { p.clave("Con alguien", "${pct(it)} de los avisos que respondí") }
            if (d.valSolo != null || d.valAcompanado != null)
                p.clave("Ánimo solo y acompañado", "solo ${d.valSolo?.let(::n1) ?: "—"} · acompañado ${d.valAcompanado?.let(::n1) ?: "—"} (avisos, de 1 a 7)")
            if (d.apreciacion.isNotEmpty()) p.clave("Cómo viví la exigencia del día", lista(d.apreciacion))
            d.errorPrediccion?.let {
                p.clave("Mi predicción del día siguiente", (if (abs(it) < 0.05) "En promedio, los días salieron como los predije"
                    else "En promedio, los días salieron ${signo(it, true)} puntos ${if (it > 0) "mejor" else "peor"} de lo que predije la noche anterior") + " (${dias(d.nPrediccion)}).")
            }
        }

        if (op.cuestionarios) {
            p.seccion("Cuestionarios")
            d.cuestionarios.forEach { c ->
                val ahora = if (c.enPeriodo.isEmpty()) "Sin respuesta en el periodo." else c.enPeriodo.joinToString(" · ") { "${fechaCorta(it.first)}: ${it.second}" } + (c.lectura?.let { "\n$it" } ?: "")
                val antes = if (c.anteriores.isEmpty()) "" else "\nAntes: " + c.anteriores.joinToString(" · ") { "${fechaCorta(it.first)}: ${it.second}" }
                p.clave(c.nombre, ahora + antes)
            }
            p.nota("WHO-5: de 0 a 100, más alto es más bienestar. PSS-4: de 0 a 16, más alto es más estrés. UCLA-3: de 3 a 9, más alto es más soledad. " +
                "El WHO-5 y el PSS-4 preguntan por los últimos 7 días (el original, por 2 semanas y por un mes).")
        }

        if (op.sueno) {
            p.seccion("Sueño")
            p.barras(d.fechas, d.filas.map { it.suenoH }, ::n1)
            p.clave("Horas de sueño por noche", d.suenoH?.let { n1(it) + " h" + (cambio(it, d.suenoAnterior, anteriores, true, d.errorSueno)?.let { c -> " · $c" } ?: "") } ?: "—")
            p.clave("Me dormí", horaYVariacion(d.dormir) ?: "—")
            p.clave("Desperté", horaYVariacion(d.despertar) ?: "—")
            p.clave("Calidad que anoté", d.calidad?.let { n1(it) + " de 5" } ?: "—")
            p.clave("Noches con sueño ansioso o pesadilla", "${d.pesadillas}")
            p.clave("Pantalla en la hora antes de dormir", d.camaMin?.let { "${n0(it)} min por noche" } ?: "—")
            p.nota("Horas y horarios del reloj. «±» es la desviación estándar: cuánto se movió el horario de una noche a otra.")
        }

        if (op.rutina) {
            p.seccion("Actividad y rutina")
            p.clave("Pasos al día", d.pasos?.let { n0(it) + (cambio(it, d.pasosAnterior, anteriores, false, d.errorPasos)?.let { c -> " · $c" } ?: "") } ?: "—")
            p.clave("Ejercicio (reloj)", "${d.ejercicioMin} min en total")
            p.clave("Al aire libre", d.aireLibreMin?.let { "${n0(it)} min al día (estimado)" } ?: "—")
            p.clave("En casa", d.enCasaMin?.let { "${n1(it / 60)} h al día" } ?: "—")
            p.clave("Hábitos cumplidos", d.habitosPct?.let(::pct) ?: "—")
            p.clave("Dolor (0 a 5)", d.dolorMedio?.let { "medio ${n1(it)} · máximo ${d.dolorMax}" } ?: "—")
            p.clave("Días con", d.contextos.joinToString(" · ") { "${it.first.lowercase()}: ${it.second}" })
            p.clave("Pantalla del teléfono", d.pantallaMin?.let { n1(it / 60) + " h al día" + (cambio(it / 60, d.pantallaAnterior?.div(60), anteriores, true, d.errorPantalla?.div(60))?.let { c -> " · $c" } ?: "") } ?: "—")
            p.clave("Desbloqueos", d.desbloqueos?.let { "${n0(it)} al día" } ?: "—")

            p.seccion("Contacto con otros")
            if (d.diasSociales == 0) p.nota("No hay días con personas anotadas en el periodo.")
            else {
                p.clave("Estuve en persona con", d.vio.joinToString(" · ") { "${it.first.lowercase()}: ${dias(it.second)}" })
                p.clave("Pensé en (sin verlas)", d.penso.joinToString(" · ") { "${it.first.lowercase()}: ${dias(it.second)}" })
                p.nota("De ${dias(d.diasSociales)} con personas anotadas en el diario o en los registros. Sin nombres.")
            }
            p.clave("Llamadas", "${d.llamadas}")
        }

        if (op.cuerpo) {
            p.seccion("Gimnasio y ejercicio")
            if (d.gimnasio.isEmpty()) p.clave("Días de gimnasio", "Ninguno en el periodo.")
            else {
                val mins = d.gimnasio.mapNotNull { it.second }
                p.clave("Días de gimnasio", "${d.gimnasio.size} de ${d.n}")
                p.clave("Duración", d.gimnasio.joinToString(" · ") { "${fechaCorta(it.first)}: ${it.second?.let { m -> "${m.roundToInt()} min" } ?: "sin duración"}" })
                if (mins.isNotEmpty()) p.clave("En total", "${n0(mins.sum())} min · ${n0(mins.average())} min por entreno")
            }
            if (d.otrasSesiones.isNotEmpty()) p.clave("Otras sesiones (reloj)", d.otrasSesiones.joinToString(" · ") { "${it.first}: ${it.second} (${it.third} min)" })

            p.seccion("Alimentación")
            val nu = d.nutricion
            if (nu == null) p.nota("Sin registro de comida en el periodo.")
            else {
                p.clave("Días con registro", "${nu.diasConRegistro} de ${d.n}")
                nu.kcal?.let { k ->
                    p.clave("Calorías", "${n0(k.valorMedio)} kcal al día contra un objetivo de ${n0(k.objetivoMedio)}: ${signo(k.diferenciaMedia)} kcal (${signo(k.porcentajeMedio * 100, true)} %)")
                    p.clave("Contra el objetivo", "dentro de ±10 %: ${dias(k.dentro)} · por encima: ${dias(k.encima)} · por debajo: ${dias(k.debajo)}")
                    val porDia = k.diferencias.toMap()
                    p.barras(d.fechas, d.fechas.map { porDia[it] }, { signo(it) }, conCero = true)
                }
                nu.proteina?.let { k ->
                    p.clave("Proteína", "${n0(k.valorMedio)} g al día contra ${n0(k.objetivoMedio)} g: ${signo(k.diferenciaMedia)} g (${signo(k.porcentajeMedio * 100, true)} %) · " +
                        "dentro de ±10 %: ${dias(k.dentro)}, por encima: ${k.encima}, por debajo: ${k.debajo}")
                }
                nu.carbohidratos?.let { (v, o) -> p.clave("Carbohidratos", "${n0(v)} g al día" + (o?.let { " (objetivo ${n0(it)} g)" } ?: "")) }
                nu.grasa?.let { (v, o) -> p.clave("Grasa", "${n0(v)} g al día" + (o?.let { " (objetivo ${n0(it)} g)" } ?: "")) }
                nu.nutrientes.forEach { (nombre, v, u) -> p.clave(nombre, "${if (v < 10) n1(v) else n0(v)} $u al día") }
                horaYVariacion(nu.cafeinaUltima)?.let { p.clave("Última cafeína", it) }
                nu.comidasPorDia?.let { p.clave("Alimentos anotados al día", n1(it)) }
                horaYVariacion(nu.primera)?.let { p.clave("Primera comida", it) }
                horaYVariacion(nu.ultima)?.let { p.clave("Última comida", it) }
                p.nota("Datos de MacroFactor. Los días con menos de ${Terapia.MINIMO_KCAL.toInt()} kcal cuentan como sin registro y no entran en los promedios" +
                    (if (nu.diasSinRegistro > 0) " (${dias(nu.diasSinRegistro)})." else "."))
            }
        }

        d.sustancias?.let { l ->
            p.seccion("Sustancias")
            if (l.isEmpty()) p.nota("Sin consumos anotados en el periodo.")
            l.forEach { s ->
                p.clave(s.nombre, listOfNotNull(
                    dias(s.dias), s.cantidad?.let { "${if (it % 1.0 == 0.0) n0(it) else n1(it)} ${s.unidad} en total" },
                    s.nivelMax?.let { "nivel máximo $it de 10" }, s.crudaMedia?.let { "malestar al día siguiente ${n1(it)} de 5" },
                    s.sintomas.takeIf { it.isNotEmpty() }?.let { "síntomas: " + it.joinToString(", ").lowercase() },
                ).joinToString(" · "))
            }
        }

        if (op.momentosPeriodo) {
            p.seccion("Momentos del periodo")
            if (d.mejorMomento == null && d.peorMomento == null) p.nota("No anoté lo mejor ni lo peor en el mejor ni en el peor día.")
            d.mejorMomento?.let { (f, t) -> p.clave("Lo mejor (${fechaCorta(f)}, mi mejor día)", t) }
            d.peorMomento?.let { (f, t) -> p.clave("Lo peor (${fechaCorta(f)}, mi peor día)", t) }
        }

        if (op.momentosDia) {
            p.seccion("Lo mejor y lo peor de cada día")
            val conTexto = d.filas.filter { it.mejor.isNotBlank() || it.peor.isNotBlank() }
            if (conTexto.isEmpty()) p.nota("No anoté lo mejor ni lo peor en estos días.")
            conTexto.forEach { f ->
                p.clave(Fechas.titulo(f.fecha), listOfNotNull(f.mejor.takeIf { it.isNotBlank() }?.let { "Lo mejor: $it" }, f.peor.takeIf { it.isNotBlank() }?.let { "Lo peor: $it" }).joinToString("\n"))
            }
        }

        d.privados?.let { pr ->
            p.seccion("Seguimiento privado")
            p.nota("Incluido porque lo elegí al generar este reporte.")
            p.clave("Impulso del día (0 a 5)", pr.impulsoMedio?.let { "medio ${n1(it)}" } ?: "—")
            p.clave("Impulsos registrados", "${pr.impulsos}" + if (pr.impulsos > 0) " · sin recaída en las 3 h siguientes: ${pr.superados}" else "")
            p.clave("Días con recaída", "${pr.diasConRecaida} de ${dias(pr.diasRespondidos)} con respuesta")
            if (pr.detonantes.isNotEmpty()) p.clave("Detonantes más frecuentes", lista(pr.detonantes))
            p.clave("Medicamentos anotados", if (pr.medicamentos.isEmpty()) "Ninguno." else pr.medicamentos.joinToString(" · ") { "${it.first} (${dias(it.second)})" })
        }
    }

    // ------------------------------------------------------------------ el lienzo de páginas

    /**
     * Las páginas del PDF o, con [imagenes], de imágenes. Con [total] null solo cuenta: [terminar] devuelve cuántas páginas salieron.
     */
    private class Pdf(val total: Int?, val d: Terapia.Datos, val imagenes: MutableList<Bitmap>? = null) {
        val doc = if (imagenes == null) PdfDocument() else null
        var pagina: PdfDocument.Page? = null
        var lienzo: Canvas? = null
        var num = 0
        var y = MARGEN
        val ancho = ANCHO - 2 * MARGEN
        val normal: Typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        val negrita: Typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        fun tp(tam: Float, color: Int = TINTA, tf: Typeface = normal) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = tam; this.color = color; typeface = tf }
        val pTitulo = tp(20f, TINTA, negrita); val pSub = tp(11f, TENUE); val pSeccion = tp(13f, ACENTO, negrita)
        val pClave = tp(9.5f, TENUE); val pValor = tp(9.5f); val pNota = tp(8.5f, TENUE).apply { textSkewX = -0.15f }
        val pCelda = tp(8.5f); val pCab = tp(8.5f, TENUE, negrita); val pEje = tp(7.5f, TENUE); val pPie = tp(7.5f, TENUE)
        val linea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LINEA; strokeWidth = 0.7f }

        fun c(): Canvas = lienzo!!
        val limite get() = ALTO - MARGEN - 14

        fun nueva() {
            pagina?.let { doc?.finishPage(it) }
            pagina = null
            num++
            val cv = if (doc != null) doc.startPage(PdfDocument.PageInfo.Builder(ANCHO, ALTO, num).create()).also { pagina = it }.canvas
                else Canvas(Bitmap.createBitmap(ANCHO, ALTO, Bitmap.Config.ARGB_8888).also { imagenes!!.add(it) })
            cv.drawColor(0xFFFFFFFF.toInt())
            cv.drawText("Vestigium Mei · reporte para terapia · ${d.op.ini} a ${d.op.fin}", MARGEN, ALTO - 26f, pPie)
            val n = if (total != null) "página $num de $total" else "página $num"
            cv.drawText(n, ANCHO - MARGEN - pPie.measureText(n), ALTO - 26f, pPie)
            lienzo = cv; y = MARGEN
        }

        fun cabe(alto: Float) { if (lienzo == null || y + alto > limite) nueva() }

        fun layout(t: CharSequence, paint: TextPaint, w: Float) =
            StaticLayout.Builder.obtain(t, 0, t.length, paint, w.toInt()).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(0f, 1.2f).setIncludePad(false).build()

        /** Un bloque de texto en [x], partido entre páginas si no cabe. */
        fun bloque(l: StaticLayout, x: Float) {
            var linea = 0
            while (linea < l.lineCount) {
                var fin = linea
                while (fin < l.lineCount && l.getLineBottom(fin) - l.getLineTop(linea) <= limite - y) fin++
                if (fin == linea) { nueva(); continue }
                c().save(); c().translate(x, y - l.getLineTop(linea))
                c().clipRect(0f, l.getLineTop(linea).toFloat(), l.width.toFloat(), l.getLineBottom(fin - 1).toFloat())
                l.draw(c()); c().restore()
                y += l.getLineBottom(fin - 1) - l.getLineTop(linea)
                linea = fin
                if (linea < l.lineCount) nueva()
            }
        }

        fun encabezado(titulo: String, sub: String, aviso: String) {
            nueva()
            c().drawText(titulo, MARGEN, y + 18f, pTitulo); y += 28f
            c().drawText(sub, MARGEN, y + 10f, pSub); y += 16f
            c().drawText("Generado el ${Fechas.larga(Fechas.diaLogico())}", MARGEN, y + 10f, pSub); y += 20f
            bloque(layout(aviso, pNota, ancho), MARGEN); y += 4f
        }

        fun seccion(titulo: String) {
            cabe(64f)
            y += 16f
            c().drawText(titulo, MARGEN, y + 12f, pSeccion); y += 18f
            c().drawLine(MARGEN, y, ANCHO - MARGEN, y, linea); y += 8f
        }

        /** Rótulo a la izquierda y valor a la derecha; los dos pueden ocupar varias líneas. */
        fun clave(k: String, v: String) {
            val lk = layout(k, pClave, COL - 10f); val lv = layout(v, pValor, ancho - COL)
            // Si el valor es más largo que una página, empieza donde haya lugar para el rótulo y sigue en la siguiente.
            cabe(minOf(maxOf(lk.height, lv.height).toFloat(), limite - MARGEN - 40f) + 5f)
            val y0 = y; val pag = num
            c().save(); c().translate(MARGEN, y); lk.draw(c()); c().restore()
            bloque(lv, MARGEN + COL)
            y = (if (num == pag) maxOf(y, y0 + lk.height) else y) + 5f
        }

        fun nota(t: String) { val l = layout(t, pNota, ancho); cabe(minOf(l.height.toFloat(), 40f) + 4f); bloque(l, MARGEN); y += 6f }

        fun tabla(cab: List<String>, filas: List<List<String>>, pesos: List<Float>) {
            val suma = pesos.sum(); val xs = pesos.runningFold(MARGEN) { a, p -> a + p / suma * ancho }
            val alto = 15f
            fun encabezados() {
                cab.forEachIndexed { i, t -> c().drawText(TextUtils.ellipsize(t, pCab, xs[i + 1] - xs[i] - 4f, TextUtils.TruncateAt.END).toString(), xs[i] + 2f, y + 10f, pCab) }
                y += alto; c().drawLine(MARGEN, y - 2f, ANCHO - MARGEN, y - 2f, linea)
            }
            cabe(alto * 3); encabezados()
            filas.forEach { f ->
                if (y + alto > limite) { nueva(); encabezados() }
                f.forEachIndexed { i, t -> c().drawText(TextUtils.ellipsize(t, pCelda, xs[i + 1] - xs[i] - 4f, TextUtils.TruncateAt.END).toString(), xs[i] + 2f, y + 10f, pCelda) }
                y += alto
            }
            y += 6f
        }

        /** El marco de una gráfica por día: rejilla en [marcas], el día bajo cada columna, y [dibujo] con las escalas. */
        fun grafica(fechas: List<String>, min: Double, max: Double, marcas: List<Double>, rotulo: (Double) -> String, dibujo: (Canvas, (Int) -> Float, (Double) -> Float) -> Unit) {
            val alto = 110f
            cabe(alto + 30f)
            val x0 = MARGEN + 26f; val w = ANCHO - MARGEN - x0; val paso = w / fechas.size.coerceAtLeast(1)
            val top = y + 4f
            fun xi(i: Int) = x0 + (i + 0.5f) * paso
            fun yv(v: Double) = top + ((max - v) / (max - min)).toFloat() * alto
            marcas.forEach { m ->
                c().drawLine(x0, yv(m), ANCHO - MARGEN, yv(m), linea)
                val r = rotulo(m); c().drawText(r, x0 - 4f - pEje.measureText(r), yv(m) + 3f, pEje)
            }
            fechas.forEachIndexed { i, f ->
                val dia = Fechas.parse(f)
                val r = "LMMJVSD"[dia.dayOfWeek.value - 1] + " " + dia.dayOfMonth
                c().drawText(r, xi(i) - pEje.measureText(r) / 2, top + alto + 12f, pEje)
            }
            dibujo(c(), ::xi, ::yv)
            y = top + alto + 20f
        }

        fun graficaLinea(fechas: List<String>, series: List<Pair<List<Double?>, Int>>, min: Double, max: Double, marcas: List<Double>) =
            grafica(fechas, min, max, marcas, { it.roundToInt().toString() }) { cv, xi, yv ->
                series.forEach { (vals, color) ->
                    val trazo = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; strokeWidth = 1.6f; style = Paint.Style.STROKE }
                    val punto = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
                    vals.forEachIndexed { i, v ->
                        if (v == null) return@forEachIndexed
                        vals.getOrNull(i + 1)?.let { s -> cv.drawLine(xi(i), yv(v), xi(i + 1), yv(s), trazo) }
                        cv.drawCircle(xi(i), yv(v), 2.6f, punto)
                    }
                }
            }

        fun leyenda(l: List<Pair<String, Int>>) {
            var x = MARGEN + 26f
            l.forEach { (t, color) ->
                c().drawRect(x, y - 6f, x + 10f, y - 1f, Paint().apply { this.color = color })
                c().drawText(t, x + 14f, y, pEje); x += 24f + pEje.measureText(t)
            }
            y += 10f
        }

        /** Barras por día con su número encima. Con [conCero], las negativas bajan desde una línea en cero. */
        fun barras(fechas: List<String>, valores: List<Double?>, rotulo: (Double) -> String, conCero: Boolean = false) {
            val hay = valores.filterNotNull()
            if (hay.isEmpty()) return
            val max = (hay.max().coerceAtLeast(0.0)).let { if (it == 0.0) 1.0 else it * 1.15 }
            val min = if (conCero) (hay.min().coerceAtMost(0.0)).let { if (it == 0.0) 0.0 else it * 1.15 } else 0.0
            val marcas = if (conCero) listOf(0.0) else listOf(0.0, max / 1.15 / 2, max / 1.15).map { (it * 10).roundToInt() / 10.0 }
            grafica(fechas, min, if (max == min) min + 1 else max, marcas, { if (conCero) "0" else n1(it) }) { cv, xi, yv ->
                val relleno = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ACENTO }
                val paso = if (fechas.size > 1) xi(1) - xi(0) else 40f
                valores.forEachIndexed { i, v ->
                    if (v == null) return@forEachIndexed
                    val a = yv(0.0); val b = yv(v)
                    cv.drawRect(xi(i) - paso * 0.3f, minOf(a, b), xi(i) + paso * 0.3f, maxOf(a, b), relleno)
                    val r = rotulo(v); cv.drawText(r, xi(i) - pEje.measureText(r) / 2, if (v >= 0) b - 3f else b + 9f, pEje)
                }
                if (conCero) cv.drawLine(xi(0) - paso / 2, yv(0.0), xi(valores.size - 1) + paso / 2, yv(0.0),
                    Paint().apply { color = TENUE; strokeWidth = 0.8f; pathEffect = DashPathEffect(floatArrayOf(3f, 3f), 0f) })
            }
        }

        /** Cierra el documento: lo escribe en [f] (si hay) y devuelve cuántas páginas tuvo. */
        fun terminar(f: File?): Int {
            pagina?.let { doc?.finishPage(it) }
            if (f != null && doc != null) f.outputStream().use { doc.writeTo(it) }
            doc?.close()
            return num
        }
    }
}
