package io.github.ruddy2003.vestigium.reportes

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.provider.MediaStore
import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import java.time.temporal.ChronoUnit

data class Resumen(
    val titulo: String,
    val subtitulo: String,
    val fechas: List<String>,
    val valencia: List<Double?>,
    val activacion: List<Double?>,
    val diasConRegistro: Int,
    val sueno: Double?,
    val pasos: Double?,
    val pantalla: Double?,
    val desbloqueos: Double?,
    val gasto: Double,
    val gastoImprevisto: Double,
    val momentos: List<Pair<String, Int>>,
    val personas: List<Pair<String, Int>>,
    val temas: List<Pair<String, Int>>,
    val escalas: List<String>,
    val mejorDia: String?,
    val peorDia: String?,
    val comparacion: String?,
)

object Reportes {
    private const val FONDO = 0xFFECEEEF.toInt()
    private const val TINTA = 0xFF18212A.toInt()
    private const val TENUE = 0xFF58646F.toInt()
    private const val LINEA = 0xFFCDD4D7.toInt()
    private const val ACENTO = 0xFF1E6E68.toInt()
    private const val ACENTO2 = 0xFF9A5B12.toInt()
    private const val SUPERFICIE = 0xFFF8F9F9.toInt()

    fun resumen(ini: String, fin: String, titulo: String, subtitulo: String, previo: Pair<String, String>? = null): Resumen {
        val n = ChronoUnit.DAYS.between(Fechas.parse(ini), Fechas.parse(fin)).toInt() + 1
        val fechas = (0 until n).map { Fechas.sumar(ini, it.toLong()) }
        val dias = fechas.mapNotNull { Repo.dias[it] }
        val mu = Repo.momentos.filter { it.fecha in fechas }
        fun valDia(f: String): Double? = Repo.dias[f]?.valencia?.toDouble()
            ?: mu.filter { it.fecha == f && it.tipo == "muestreo" }.mapNotNull { it.valencia?.toDouble() }.takeIf { it.isNotEmpty() }?.average()
        fun actDia(f: String): Double? = Repo.dias[f]?.activacion?.toDouble()
            ?: mu.filter { it.fecha == f && it.tipo == "muestreo" }.mapNotNull { it.activacion?.toDouble() }.takeIf { it.isNotEmpty() }?.average()
        val eq = Repo.equivalencias()
        fun top(lista: List<String>) = lista.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(6).map { it.key to it.value }
        val personas = dias.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct() } + mu.flatMap { Texto.personas(it.texto) }
        val temas = dias.flatMap { d -> Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct() } + mu.flatMap { Texto.temas(it.texto) }
        val salud = fechas.mapNotNull { Repo.salud[it] }
        val pant = fechas.mapNotNull { Repo.pantalla[it] }
        val gastos = Repo.gastos.filter { it.fecha in fechas }
        val escalas = Repo.respuestas.filter { it.fecha in fechas }.map { r ->
            val inst = io.github.ruddy2003.vestigium.ciencia.Escalas.por(r.instrumento)
            (inst?.nombre ?: r.instrumento) + ": " + r.puntajes.entries.joinToString(" · ") { "${it.key} ${"%.0f".format(it.value)}" }
        }
        val vals = fechas.map { valDia(it) }
        val mejor = fechas.zip(vals).filter { it.second != null }.maxByOrNull { it.second!! }?.first
        val peor = fechas.zip(vals).filter { it.second != null }.minByOrNull { it.second!! }?.first
        val comp = previo?.let { (pi, pf) ->
            val pv = Repo.dias.values.filter { it.fecha in pi..pf }.mapNotNull { it.valencia?.toDouble() }
            val av = vals.filterNotNull()
            if (pv.size >= 3 && av.size >= 3) {
                val d = av.average() - pv.average()
                "Valencia media ${"%.1f".format(av.average())} contra ${"%.1f".format(pv.average())} del periodo anterior (" + (if (d >= 0) "+" else "") + "%.1f".format(d) + ")."
            } else null
        }
        return Resumen(
            titulo, subtitulo, fechas, vals, fechas.map { actDia(it) },
            diasConRegistro = dias.size,
            sueno = salud.mapNotNull { it.sueno_min?.toDouble() }.takeIf { it.isNotEmpty() }?.average()?.div(60),
            pasos = salud.mapNotNull { it.pasos?.toDouble() }.takeIf { it.isNotEmpty() }?.average(),
            pantalla = pant.map { it.total_min.toDouble() }.takeIf { it.isNotEmpty() }?.average(),
            desbloqueos = pant.map { it.desbloqueos.toDouble() }.takeIf { it.isNotEmpty() }?.average(),
            gasto = gastos.sumOf { it.monto }, gastoImprevisto = gastos.filter { it.imprevisto }.sumOf { it.monto },
            momentos = mu.filter { it.tipo != "muestreo" }.groupingBy { it.tipo }.eachCount().entries.sortedByDescending { it.value }.map { Catalogo.nombreTipo(it.key) to it.value },
            personas = top(personas), temas = top(temas), escalas = escalas,
            mejorDia = mejor, peorDia = peor, comparacion = comp,
        )
    }

    private fun pincel(color: Int, tam: Float, negrita: Boolean = false, serif: Boolean = false) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color; textSize = tam
        typeface = Typeface.create(if (serif) Typeface.SERIF else Typeface.SANS_SERIF, if (negrita) Typeface.BOLD else Typeface.NORMAL)
    }

    fun dibujar(c: Canvas, w: Float, r: Resumen, discreto: Boolean) {
        val m = 64f
        c.drawColor(FONDO)
        var y = 110f
        c.drawText("Vestigium Mei", m, y, pincel(TENUE, 30f, serif = true)); y += 70f
        c.drawText(r.titulo, m, y, pincel(TINTA, 54f, negrita = false, serif = true)); y += 46f
        c.drawText(r.subtitulo, m, y, pincel(TENUE, 28f)); y += 60f

        // Cifras
        val cifras = listOf(
            "${r.diasConRegistro}/${r.fechas.size}" to "días con diario",
            (r.valencia.filterNotNull().takeIf { it.isNotEmpty() }?.average()?.let { "%.1f".format(it) } ?: "—") to "valencia media",
            (r.sueno?.let { "%.1f h".format(it) } ?: "—") to "sueño promedio",
            (r.pasos?.let { "%,.0f".format(it) } ?: "—") to "pasos al día",
            (r.pantalla?.let { "${(it / 60).toInt()} h ${(it % 60).toInt()} m" } ?: "—") to "pantalla al día",
            ("$" + "%,.0f".format(r.gasto)) to "gasto total",
        )
        val col = (w - 2 * m - 2 * 24f) / 3
        cifras.forEachIndexed { i, (v, l) ->
            val x = m + (i % 3) * (col + 24f); val yy = y + (i / 3) * 150f
            c.drawRoundRect(RectF(x, yy, x + col, yy + 128f), 18f, 18f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = SUPERFICIE })
            c.drawText(v, x + 24f, yy + 66f, pincel(TINTA, 44f, negrita = true))
            c.drawText(l, x + 24f, yy + 106f, pincel(TENUE, 24f))
        }
        y += 320f

        // Gráfica de valencia y activación
        c.drawText("Ánimo por día", m, y, pincel(TINTA, 32f, negrita = true)); y += 24f
        val gx0 = m + 40f; val gx1 = w - m; val gy0 = y + 20f; val gy1 = y + 320f
        val ejes = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = LINEA; strokeWidth = 2f }
        for (v in 1..7) {
            val yy = gy1 - (v - 1) / 6f * (gy1 - gy0)
            c.drawLine(gx0, yy, gx1, yy, ejes)
            if (v % 2 == 1) c.drawText("$v", m, yy + 8f, pincel(TENUE, 22f))
        }
        fun serie(vals: List<Double?>, color: Int) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; strokeWidth = 6f; style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
            val path = Path(); var abierto = false
            val n = vals.size
            vals.forEachIndexed { i, v ->
                val x = if (n == 1) (gx0 + gx1) / 2 else gx0 + i * (gx1 - gx0) / (n - 1)
                if (v == null) { abierto = false; return@forEachIndexed }
                val yy = gy1 - ((v - 1) / 6.0 * (gy1 - gy0)).toFloat()
                if (!abierto) { path.moveTo(x, yy); abierto = true } else path.lineTo(x, yy)
                c.drawCircle(x, yy, 8f, Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color })
            }
            c.drawPath(path, p)
        }
        serie(r.activacion, ACENTO2); serie(r.valencia, ACENTO)
        val etq = pincel(TENUE, 20f)
        val paso = maxOf(1, r.fechas.size / 7)
        r.fechas.forEachIndexed { i, f ->
            if (i % paso != 0) return@forEachIndexed
            val x = if (r.fechas.size == 1) (gx0 + gx1) / 2 else gx0 + i * (gx1 - gx0) / (r.fechas.size - 1)
            val t = if (r.fechas.size <= 8) Fechas.corto(f).substringBefore(" ") else Fechas.parse(f).dayOfMonth.toString()
            c.drawText(t, x - etq.measureText(t) / 2, gy1 + 34f, etq)
        }
        y = gy1 + 70f
        c.drawCircle(m + 10f, y - 8f, 9f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ACENTO })
        c.drawText("Valencia", m + 30f, y, pincel(TENUE, 24f))
        c.drawCircle(m + 190f, y - 8f, 9f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ACENTO2 })
        c.drawText("Activación", m + 210f, y, pincel(TENUE, 24f))
        y += 70f

        // Momentos
        c.drawText("Momentos registrados", m, y, pincel(TINTA, 32f, negrita = true)); y += 20f
        if (r.momentos.isEmpty()) { y += 40f; c.drawText("Ninguno en este periodo.", m, y, pincel(TENUE, 26f)); y += 30f }
        val maxM = r.momentos.maxOfOrNull { it.second } ?: 1
        r.momentos.take(6).forEach { (t, n) ->
            y += 50f
            val nombre = if (discreto) t.take(1) else t
            c.drawText(nombre, m, y, pincel(TINTA, 26f))
            val bx = m + 260f; val bw = (w - m - bx - 80f) * n / maxM
            c.drawRoundRect(RectF(bx, y - 22f, bx + maxOf(bw, 8f), y), 8f, 8f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ACENTO })
            c.drawText("$n", bx + bw + 16f, y, pincel(TENUE, 24f))
        }
        y += 70f

        // Personas y temas
        val mitad = (w - 2 * m) / 2
        c.drawText("Personas", m, y, pincel(TINTA, 32f, negrita = true))
        c.drawText("Temas", m + mitad, y, pincel(TINTA, 32f, negrita = true))
        val filas = maxOf(r.personas.size, r.temas.size).coerceAtMost(6)
        for (i in 0 until filas) {
            val yy = y + 48f + i * 42f
            r.personas.getOrNull(i)?.let { c.drawText("@${it.first}  ${it.second}", m, yy, pincel(TENUE, 26f)) }
            r.temas.getOrNull(i)?.let { c.drawText("#${it.first}  ${it.second}", m + mitad, yy, pincel(TENUE, 26f)) }
        }
        if (filas == 0) c.drawText("Sin menciones.", m, y + 48f, pincel(TENUE, 26f))
        y += 48f + maxOf(filas, 1) * 42f + 40f

        // Notas finales
        val lineas = mutableListOf<String>()
        r.mejorDia?.let { lineas += "Mejor día: ${Fechas.titulo(it)}" }
        r.peorDia?.let { if (it != r.mejorDia) lineas += "Día más difícil: ${Fechas.titulo(it)}" }
        if (r.gastoImprevisto > 0) lineas += "Gastos imprevistos: $" + "%,.0f".format(r.gastoImprevisto)
        r.desbloqueos?.let { lineas += "Desbloqueos del teléfono: ${it.toInt()} al día" }
        r.comparacion?.let { lineas += it }
        lineas += r.escalas
        lineas.forEach { c.drawText(it, m, y, pincel(TINTA, 26f)); y += 42f }
    }

    private fun bitmap(r: Resumen): Bitmap {
        val w = 1080; val h = 1920
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        dibujar(Canvas(bmp), w.toFloat(), r, Repo.config.discreto)
        return bmp
    }

    private fun guardarPng(ctx: Context, nombre: String, bmp: Bitmap): Uri? {
        val v = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/VestigiumMei")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v) ?: return null
        ctx.contentResolver.openOutputStream(uri)?.use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return uri
    }

    private fun guardarPdf(ctx: Context, nombre: String, r: Resumen): Uri? {
        val doc = PdfDocument()
        val pagina = doc.startPage(PdfDocument.PageInfo.Builder(1080, 1920, 1).create())
        dibujar(pagina.canvas, 1080f, r, Repo.config.discreto)
        doc.finishPage(pagina)
        val v = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, nombre)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/VestigiumMei")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v)
        if (uri != null) ctx.contentResolver.openOutputStream(uri)?.use { doc.writeTo(it) }
        doc.close()
        return uri
    }

    fun semanal(ctx: Context, lunes: String, avisar: Boolean): Uri? {
        val fin = Fechas.sumar(lunes, 6)
        val ini = Fechas.parse(lunes); val f = Fechas.parse(fin)
        val sub = "Del ${ini.dayOfMonth} de ${ini.month.getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)} al ${f.dayOfMonth} de ${f.month.getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)} de ${f.year}"
        val r = resumen(lunes, fin, "Tu semana", sub, Fechas.sumar(lunes, -7) to Fechas.sumar(lunes, -1))
        val uri = guardarPng(ctx, "semana_$lunes.png", bitmap(r))
        if (avisar && uri != null) Avisos.reporte(ctx, "Tu reporte semanal está listo", uri, "image/png")
        return uri
    }

    fun mensual(ctx: Context, ym: String, avisar: Boolean): Uri? {
        val ini = "$ym-01"
        val fin = Fechas.iso(Fechas.parse(ini).plusMonths(1).minusDays(1))
        val pIni = Fechas.iso(Fechas.parse(ini).minusMonths(1))
        val r = resumen(ini, fin, Fechas.mesTitulo(ym), "Resumen del mes", pIni to Fechas.sumar(ini, -1))
        guardarPng(ctx, "mes_$ym.png", bitmap(r))
        val uri = guardarPdf(ctx, "mes_$ym.pdf", r)
        if (avisar && uri != null) Avisos.reporte(ctx, "Tu reporte de ${Fechas.mesTitulo(ym)} está listo", uri, "application/pdf")
        return uri
    }
}
