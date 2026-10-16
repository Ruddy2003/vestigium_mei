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
import androidx.core.content.res.ResourcesCompat
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Grados
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Romano
import java.time.temporal.ChronoUnit

data class Resumen(
    val titulo: String,
    val latin: String,
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
    val tempMedia: Double?,
    val luzMedia: Double?,
    val lluviaDias: Int,
    val fotos: Int,
    val escalada: Int,
    val obras: List<String>,
    val hitos: List<String>,
)

/** Reportes como inscripciones: basalto, oro y bronce. Sin mapas ni direcciones, para poder compartirlos. */
object Reportes {
    private const val FONDO = 0xFF0E1113.toInt()
    private const val SUPERFICIE = 0xFF161A1D.toInt()
    private const val LINEA = 0xFF2B3137.toInt()
    private const val TINTA = 0xFFE7E3DA.toInt()
    private const val TENUE = 0xFF9AA0A6.toInt()
    private const val ORO = 0xFFD2AE5A.toInt()
    private const val BRONCE = 0xFF67BBAE.toInt()
    private const val PORFIDO = 0xFFC2566A.toInt()

    private class Letras(val ins: Typeface, val mono: Typeface, val sans: Typeface)
    private fun letras(ctx: Context) = Letras(
        ResourcesCompat.getFont(ctx, R.font.cinzel_regular) ?: Typeface.SERIF,
        ResourcesCompat.getFont(ctx, R.font.plex_mono_regular) ?: Typeface.MONOSPACE,
        ResourcesCompat.getFont(ctx, R.font.plex_sans_regular) ?: Typeface.SANS_SERIF,
    )

    fun resumen(ini: String, fin: String, titulo: String, latin: String, subtitulo: String, previo: Pair<String, String>? = null): Resumen {
        val n = ChronoUnit.DAYS.between(Fechas.parse(ini), Fechas.parse(fin)).toInt() + 1
        val fechas = (0 until n).map { Fechas.sumar(ini, it.toLong()) }
        val conjunto = fechas.toHashSet()
        val dias = fechas.mapNotNull { Repo.dias[it] }
        val mu = Repo.momentos.filter { it.fecha in conjunto }
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
        val ent = fechas.mapNotNull { Repo.entorno[it] }
        val gastos = Repo.gastos.filter { it.fecha in conjunto }
        val escalas = Repo.respuestas.filter { it.fecha in conjunto }.map { r ->
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
                "valencia %.1f contra %.1f del periodo anterior (%s%.1f)".format(av.average(), pv.average(), if (d >= 0) "+" else "", d)
            } else null
        }
        val asc = Repo.ascensos.filter { it.fecha in conjunto }
        val obras = Repo.obras.filter { it.estado == "terminado" && it.fin != null && it.fin in conjunto }.map { "${it.titulo}" + (it.calificacion?.let { c -> " · $c/10" } ?: "") }
        return Resumen(
            titulo, latin, subtitulo, fechas, vals, fechas.map { actDia(it) },
            diasConRegistro = dias.size,
            sueno = salud.mapNotNull { it.sueno_min?.toDouble() }.takeIf { it.isNotEmpty() }?.average()?.div(60),
            pasos = salud.mapNotNull { it.pasos?.toDouble() }.takeIf { it.isNotEmpty() }?.average(),
            pantalla = pant.map { it.total_min.toDouble() }.takeIf { it.isNotEmpty() }?.average(),
            desbloqueos = pant.map { it.desbloqueos.toDouble() }.takeIf { it.isNotEmpty() }?.average(),
            gasto = gastos.sumOf { it.monto }, gastoImprevisto = gastos.filter { it.imprevisto }.sumOf { it.monto },
            momentos = mu.filter { it.tipo != "muestreo" }.groupingBy { it.tipo }.eachCount().entries.sortedByDescending { it.value }.map { Catalogo.nombreTipo(it.key) to it.value },
            personas = top(personas), temas = top(temas), escalas = escalas,
            mejorDia = mejor, peorDia = peor, comparacion = comp,
            tempMedia = ent.mapNotNull { it.t_max }.takeIf { it.isNotEmpty() }?.average(),
            luzMedia = ent.mapNotNull { it.luz_min?.toDouble() }.takeIf { it.isNotEmpty() }?.average()?.div(60),
            lluviaDias = ent.count { (it.precip_mm ?: 0.0) >= 1.0 },
            fotos = fechas.sumOf { Repo.fotos[it]?.fotos ?: 0 },
            escalada = asc.size, obras = obras, hitos = dias.flatMap { it.hitos }.distinct(),
        )
    }

    private fun pincel(color: Int, tam: Float, tf: Typeface, espacio: Float = 0f) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color; textSize = tam; typeface = tf; letterSpacing = espacio
    }

    private fun meandro(c: Canvas, x0: Float, x1: Float, y: Float, color: Int) {
        val u = 9f; val paso = u * 4
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 2.5f }
        val path = Path(); var x = x0
        path.moveTo(x0, y + 3 * u)
        while (x + paso <= x1) {
            path.lineTo(x, y); path.lineTo(x + 3 * u, y); path.lineTo(x + 3 * u, y + 2 * u); path.lineTo(x + u, y + 2 * u); path.lineTo(x + u, y + u); path.lineTo(x + 2 * u, y + u)
            path.moveTo(x, y + 3 * u); path.lineTo(x + paso, y + 3 * u); path.moveTo(x + paso, y + 3 * u)
            x += paso
        }
        c.drawPath(path, p)
    }

    private fun encabezado(c: Canvas, w: Float, r: Resumen, L: Letras): Float {
        val m = 64f
        c.drawColor(FONDO)
        var y = 100f
        c.drawText("VESTIGIVM MEI", m, y, pincel(TENUE, 26f, L.ins, 0.25f)); y += 78f
        c.drawText(r.latin, m, y, pincel(ORO, 62f, L.ins, 0.08f)); y += 46f
        c.drawText("// " + r.titulo.lowercase() + " · " + r.subtitulo.lowercase(), m, y, pincel(TENUE, 24f, L.mono)); y += 28f
        meandro(c, m, w - m, y, ORO); y += 70f
        return y
    }

    private fun cifras(c: Canvas, w: Float, y0: Float, lista: List<Pair<String, String>>, L: Letras): Float {
        val m = 64f; val col = (w - 2 * m - 2 * 20f) / 3
        lista.forEachIndexed { i, (v, l) ->
            val x = m + (i % 3) * (col + 20f); val yy = y0 + (i / 3) * 140f
            c.drawRect(RectF(x, yy, x + col, yy + 120f), Paint().apply { color = SUPERFICIE })
            c.drawRect(RectF(x, yy, x + 4f, yy + 120f), Paint().apply { color = ORO })
            c.drawText(v, x + 22f, yy + 62f, pincel(TINTA, 40f, L.mono))
            c.drawText(l.uppercase(), x + 22f, yy + 98f, pincel(TENUE, 19f, L.mono, 0.05f))
        }
        return y0 + ((lista.size + 2) / 3) * 140f
    }

    private fun titulo(c: Canvas, x: Float, y: Float, numero: Int, latin: String, es: String, L: Letras) {
        c.drawText("${Romano.de(numero)} · $latin", x, y, pincel(TINTA, 30f, L.ins, 0.1f))
        c.drawText("// $es", x, y + 30f, pincel(TENUE, 20f, L.mono))
    }

    private fun dibujar(c: Canvas, w: Float, r: Resumen, discreto: Boolean, L: Letras) {
        val m = 64f
        var y = encabezado(c, w, r, L)
        y = cifras(c, w, y, listOf(
            "${r.diasConRegistro}/${r.fechas.size}" to "días con diario",
            (r.valencia.filterNotNull().takeIf { it.isNotEmpty() }?.average()?.let { "%.1f".format(it) } ?: "—") to "valencia media",
            (r.sueno?.let { "%.1f h".format(it) } ?: "—") to "sueño promedio",
            (r.pasos?.let { "%,.0f".format(it) } ?: "—") to "pasos al día",
            (r.pantalla?.let { "${(it / 60).toInt()}h ${(it % 60).toInt()}m" } ?: "—") to "pantalla al día",
            ("$" + "%,.0f".format(r.gasto)) to "gasto total",
        ), L) + 30f

        // I · Ánimo
        titulo(c, m, y, 1, "ANIMVS", "ánimo por día", L); y += 50f
        val gx0 = m + 40f; val gx1 = w - m; val gy0 = y + 20f; val gy1 = y + 300f
        val ejes = Paint().apply { color = LINEA; strokeWidth = 2f }
        for (v in 1..7) {
            val yy = gy1 - (v - 1) / 6f * (gy1 - gy0)
            c.drawLine(gx0, yy, gx1, yy, ejes)
            if (v % 2 == 1) c.drawText("$v", m, yy + 8f, pincel(TENUE, 20f, L.mono))
        }
        fun serie(vals: List<Double?>, color: Int) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; strokeWidth = 5f; style = Paint.Style.STROKE; strokeCap = Paint.Cap.SQUARE }
            val path = Path(); var abierto = false
            val n = vals.size
            vals.forEachIndexed { i, v ->
                val x = if (n == 1) (gx0 + gx1) / 2 else gx0 + i * (gx1 - gx0) / (n - 1)
                if (v == null) { abierto = false; return@forEachIndexed }
                val yy = gy1 - ((v - 1) / 6.0 * (gy1 - gy0)).toFloat()
                if (!abierto) { path.moveTo(x, yy); abierto = true } else path.lineTo(x, yy)
                c.drawRect(x - 6f, yy - 6f, x + 6f, yy + 6f, Paint().apply { this.color = color })
            }
            c.drawPath(path, p)
        }
        serie(r.activacion, BRONCE); serie(r.valencia, ORO)
        val etq = pincel(TENUE, 18f, L.mono)
        val paso = maxOf(1, r.fechas.size / 7)
        r.fechas.forEachIndexed { i, f ->
            if (i % paso != 0) return@forEachIndexed
            val x = if (r.fechas.size == 1) (gx0 + gx1) / 2 else gx0 + i * (gx1 - gx0) / (r.fechas.size - 1)
            val t = if (r.fechas.size <= 8) Romano.SEMANA[Fechas.parse(f).dayOfWeek.value - 1] else Fechas.parse(f).dayOfMonth.toString()
            c.drawText(t, x - etq.measureText(t) / 2, gy1 + 32f, etq)
        }
        y = gy1 + 66f
        c.drawRect(m, y - 16f, m + 16f, y, Paint().apply { color = ORO }); c.drawText("valencia", m + 26f, y, pincel(TENUE, 20f, L.mono))
        c.drawRect(m + 180f, y - 16f, m + 196f, y, Paint().apply { color = BRONCE }); c.drawText("activación", m + 206f, y, pincel(TENUE, 20f, L.mono))
        y += 60f

        // II · Momentos
        titulo(c, m, y, 2, "MOMENTA", "momentos registrados", L); y += 40f
        if (r.momentos.isEmpty()) { y += 34f; c.drawText("ninguno en este periodo", m, y, pincel(TENUE, 22f, L.mono)); y += 20f }
        val maxM = r.momentos.maxOfOrNull { it.second } ?: 1
        r.momentos.take(5).forEach { (t, n) ->
            y += 44f
            c.drawText(if (discreto) t.take(1) else t.lowercase(), m, y, pincel(TINTA, 22f, L.sans))
            val bx = m + 250f; val bw = (w - m - bx - 80f) * n / maxM
            c.drawRect(RectF(bx, y - 20f, bx + maxOf(bw, 6f), y), Paint().apply { color = PORFIDO })
            c.drawText("$n", bx + bw + 14f, y, pincel(TENUE, 20f, L.mono))
        }
        y += 60f

        // III · Personas y temas
        val mitad = (w - 2 * m) / 2
        titulo(c, m, y, 3, "PERSONAE", "personas y temas", L)
        val filas = maxOf(r.personas.size, r.temas.size).coerceAtMost(5)
        for (i in 0 until filas) {
            val yy = y + 74f + i * 36f
            r.personas.getOrNull(i)?.let { c.drawText("@${it.first}  ${it.second}", m, yy, pincel(TINTA, 22f, L.mono)) }
            r.temas.getOrNull(i)?.let { c.drawText("#${it.first}  ${it.second}", m + mitad, yy, pincel(TINTA, 22f, L.mono)) }
        }
        if (filas == 0) c.drawText("sin menciones", m, y + 74f, pincel(TENUE, 22f, L.mono))
        y += 74f + maxOf(filas, 1) * 36f + 30f

        // IV · Notas
        titulo(c, m, y, 4, "NOTAE", "lo demás", L); y += 76f
        val lineas = mutableListOf<Pair<String, String>>()
        r.mejorDia?.let { lineas += "optimus" to Fechas.titulo(it).lowercase() }
        r.peorDia?.let { if (it != r.mejorDia) lineas += "difficilis" to Fechas.titulo(it).lowercase() }
        r.comparacion?.let { lineas += "comparatio" to it }
        r.tempMedia?.let { lineas += "caelum" to "máx. media %.0f°".format(it) + (r.luzMedia?.let { l -> " · %.1f h de luz".format(l) } ?: "") + " · ${r.lluviaDias} días de lluvia" }
        if (r.fotos > 0) lineas += "imagines" to "${r.fotos} fotos"
        if (r.escalada > 0) lineas += "ascensvs" to "${r.escalada} sesiones de escalada"
        if (r.obras.isNotEmpty()) lineas += "perfecta" to r.obras.take(3).joinToString(", ")
        if (r.hitos.isNotEmpty()) lineas += "memorabilia" to r.hitos.take(6).joinToString(", ")
        if (r.gastoImprevisto > 0) lineas += "imprevisa" to "$" + "%,.0f".format(r.gastoImprevisto)
        r.desbloqueos?.let { lineas += "reserationes" to "${it.toInt()} desbloqueos al día" }
        r.escalas.forEach { lineas += "quaestio" to it }
        val k = pincel(TENUE, 21f, L.mono); val v = pincel(TINTA, 21f, L.mono)
        for ((a, b) in lineas) {
            if (y > 1880f) break
            c.drawText(a.padEnd(12), m, y, k); c.drawText("= ", m + 190f, y, pincel(ORO, 21f, L.mono))
            c.drawText(recortar(b, v, w - m - (m + 222f)), m + 222f, y, v); y += 38f
        }
    }

    private fun recortar(s: String, p: Paint, ancho: Float): String {
        if (p.measureText(s) <= ancho) return s
        var t = s
        while (t.length > 3 && p.measureText("$t…") > ancho) t = t.dropLast(1)
        return "$t…"
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

    private fun guardarPdf(ctx: Context, nombre: String, alto: Int, dibujo: (Canvas) -> Unit): Uri? {
        val doc = PdfDocument()
        val pagina = doc.startPage(PdfDocument.PageInfo.Builder(1080, alto, 1).create())
        dibujo(pagina.canvas)
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

    private fun bitmap(alto: Int, dibujo: (Canvas) -> Unit): Bitmap {
        val bmp = Bitmap.createBitmap(1080, alto, Bitmap.Config.ARGB_8888)
        dibujo(Canvas(bmp))
        return bmp
    }

    fun semanal(ctx: Context, lunes: String, avisar: Boolean): Uri? {
        val fin = Fechas.sumar(lunes, 6)
        val ini = Fechas.parse(lunes); val f = Fechas.parse(fin)
        val sub = "del ${ini.dayOfMonth} de ${ini.month.getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)} al ${f.dayOfMonth} de ${f.month.getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)} de ${f.year}"
        val semana = f.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val r = resumen(lunes, fin, "Tu semana", "HEBDOMAS ${Romano.de(semana)}", sub, Fechas.sumar(lunes, -7) to Fechas.sumar(lunes, -1))
        val L = letras(ctx)
        val uri = guardarPng(ctx, "semana_$lunes.png", bitmap(1920) { dibujar(it, 1080f, r, Repo.config.discreto, L) })
        if (avisar && uri != null) Avisos.reporte(ctx, "Tu reporte semanal está listo", uri, "image/png")
        return uri
    }

    fun mensual(ctx: Context, ym: String, avisar: Boolean): Uri? {
        val ini = "$ym-01"
        val d = Fechas.parse(ini)
        val fin = Fechas.iso(d.plusMonths(1).minusDays(1))
        val pIni = Fechas.iso(d.minusMonths(1))
        val r = resumen(ini, fin, Fechas.mesTitulo(ym), "${Romano.mes(d.monthValue)} ${Romano.de(d.year)}", "resumen del mes", pIni to Fechas.sumar(ini, -1))
        val L = letras(ctx)
        guardarPng(ctx, "mes_$ym.png", bitmap(1920) { dibujar(it, 1080f, r, Repo.config.discreto, L) })
        val uri = guardarPdf(ctx, "mes_$ym.pdf", 1920) { dibujar(it, 1080f, r, Repo.config.discreto, L) }
        if (avisar && uri != null) Avisos.reporte(ctx, "Tu reporte de ${Fechas.mesTitulo(ym)} está listo", uri, "application/pdf")
        return uri
    }

    // ---------------- Año ----------------
    private fun colorValencia(v: Double): Int {
        val t = ((v - 1) / 6).coerceIn(0.0, 1.0)
        val (a, b, f) = if (t < 0.5) Triple(PORFIDO, 0xFF3A3F44.toInt(), t * 2) else Triple(0xFF3A3F44.toInt(), BRONCE, (t - 0.5) * 2)
        fun mezcla(sh: Int) = (((a shr sh) and 0xFF) * (1 - f) + ((b shr sh) and 0xFF) * f).toInt()
        return (0xFF shl 24) or (mezcla(16) shl 16) or (mezcla(8) shl 8) or mezcla(0)
    }

    private fun dibujarAnual(c: Canvas, w: Float, anio: Int, L: Letras) {
        val m = 64f
        val ini = "$anio-01-01"; val fin = "$anio-12-31"
        val r = resumen(ini, fin, "Tu año", "ANNVS ${Romano.de(anio)}", "$anio", "${anio - 1}-01-01" to "${anio - 1}-12-31")
        var y = encabezado(c, w, r, L)
        val palabras = r.fechas.sumOf { f -> Repo.dias[f]?.let { Texto.palabras(it.nota) } ?: 0 }
        val terminadas = Repo.obras.count { it.estado == "terminado" && it.fin?.startsWith("$anio") == true }
        y = cifras(c, w, y, listOf(
            "${r.diasConRegistro}" to "días escritos",
            "%,d".format(palabras) to "palabras",
            (r.valencia.filterNotNull().takeIf { it.isNotEmpty() }?.average()?.let { "%.2f".format(it) } ?: "—") to "valencia media",
            "${Repo.momentos.count { it.fecha.startsWith("$anio") && it.tipo != "muestreo" }}" to "momentos",
            (r.sueno?.let { "%.1f h".format(it) } ?: "—") to "sueño promedio",
            "$terminadas" to "obras terminadas",
        ), L) + 30f

        // I · El año en píxeles
        titulo(c, m, y, 1, "ANNVS IN PVNCTIS", "un cuadro por día, color por ánimo", L); y += 66f
        val celda = (w - 2 * m - 80f) / 31f
        for (mes in 1..12) {
            val yy = y + (mes - 1) * (celda + 4f)
            c.drawText(Romano.de(mes), m, yy + celda * 0.8f, pincel(TENUE, 18f, L.ins))
            val dm = java.time.YearMonth.of(anio, mes).lengthOfMonth()
            for (d in 1..dm) {
                val f = "%04d-%02d-%02d".format(anio, mes, d)
                val v = r.valencia.getOrNull(r.fechas.indexOf(f))
                val x = m + 80f + (d - 1) * celda
                c.drawRect(x, yy, x + celda - 3f, yy + celda, Paint().apply { color = if (v != null) colorValencia(v) else SUPERFICIE })
            }
        }
        y += 12 * (celda + 4f) + 50f

        // II · Mes por mes
        titulo(c, m, y, 2, "MENSES", "valencia media de cada mes", L); y += 60f
        val altoB = 200f
        val ancho = (w - 2 * m) / 12f
        for (mes in 1..12) {
            val vs = r.fechas.zip(r.valencia).filter { it.first.substring(5, 7).toInt() == mes }.mapNotNull { it.second }
            val x = m + (mes - 1) * ancho
            if (vs.isNotEmpty()) {
                val h = ((vs.average() - 1) / 6 * altoB).toFloat()
                c.drawRect(x + 8f, y + altoB - h, x + ancho - 8f, y + altoB, Paint().apply { color = colorValencia(vs.average()) })
                c.drawText("%.1f".format(vs.average()), x + 10f, y + altoB - h - 10f, pincel(TINTA, 18f, L.mono))
            }
            c.drawText(Romano.de(mes), x + 10f, y + altoB + 30f, pincel(TENUE, 18f, L.ins))
        }
        y += altoB + 80f

        // III · Lo más frecuente
        titulo(c, m, y, 3, "FREQVENTISSIMA", "lo más frecuente del año", L); y += 76f
        val k = pincel(TENUE, 21f, L.mono); val v = pincel(TINTA, 21f, L.mono)
        val lineas = mutableListOf<Pair<String, String>>()
        if (r.personas.isNotEmpty()) lineas += "personae" to r.personas.take(5).joinToString(" ") { "@${it.first}" }
        if (r.temas.isNotEmpty()) lineas += "themata" to r.temas.take(5).joinToString(" ") { "#${it.first}" }
        if (r.momentos.isNotEmpty()) lineas += "momenta" to r.momentos.take(3).joinToString(" · ") { "${it.first.lowercase()} ${it.second}" }
        val artistas = runCatching { Repo.escuchas(ini, fin).filter { it.artista.isNotBlank() }.groupingBy { it.artista }.eachCount().entries.sortedByDescending { it.value }.take(3) }.getOrDefault(emptyList())
        if (artistas.isNotEmpty()) lineas += "musica" to artistas.joinToString(" · ") { "${it.key} ${it.value}" }
        val asc = Repo.ascensos.filter { it.fecha.startsWith("$anio") }
        if (asc.isNotEmpty()) {
            val max = asc.flatMap { it.bloques }.filter { Grados.encadenado(it.estilo) }.mapNotNull { b -> Grados.valor(b.grado)?.let { it to b.grado } }.maxByOrNull { it.first }?.second
            lineas += "ascensvs" to "${asc.size} sesiones" + (max?.let { " · máximo $it" } ?: "")
        }
        val porTipo = Repo.obras.filter { it.estado == "terminado" && it.fin?.startsWith("$anio") == true }.groupingBy { it.tipo }.eachCount()
        if (porTipo.isNotEmpty()) lineas += "bibliotheca" to porTipo.entries.joinToString(" · ") { "${it.value} ${it.key}" }
        r.mejorDia?.let { lineas += "optimus" to Fechas.titulo(it).lowercase() }
        lineas += "svmptvs" to "$" + "%,.0f".format(r.gasto)
        r.pasos?.let { lineas += "passus" to "%,.0f al día".format(it) }
        r.tempMedia?.let { lineas += "caelum" to "máx. media %.0f° · ${r.lluviaDias} días de lluvia".format(it) }
        if (r.fotos > 0) lineas += "imagines" to "%,d fotos".format(r.fotos)
        if (r.hitos.isNotEmpty()) lineas += "memorabilia" to r.hitos.take(8).joinToString(", ")
        for ((a, b) in lineas) {
            c.drawText(a.padEnd(12), m, y, k); c.drawText("= ", m + 190f, y, pincel(ORO, 21f, L.mono))
            c.drawText(recortar(b, v, w - m - (m + 222f)), m + 222f, y, v); y += 38f
        }
    }

    fun anual(ctx: Context, anio: Int): Uri? {
        val L = letras(ctx)
        val alto = 2400
        val png = guardarPng(ctx, "anno_$anio.png", bitmap(alto) { dibujarAnual(it, 1080f, anio, L) })
        guardarPdf(ctx, "anno_$anio.pdf", alto) { dibujarAnual(it, 1080f, anio, L) }
        return png
    }
}
