package io.github.ruddy2003.vestigium.reportes

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Picture
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.res.ResourcesCompat
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.ciencia.Periodo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Grados
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.LibroDiario
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.esquemaDe
import kotlin.math.abs
import kotlin.math.roundToInt

/** Colores y letras del tema para dibujar fuera de la pantalla (reportes, imágenes para compartir). */
class Lienzo(ctx: Context, val tema: TemaId = Estilo.tema, val oscuro: Boolean = oscuroFuera(ctx, tema)) {
    private val e = esquemaDe(tema, oscuro)
    val fondo = e.background.toArgb()
    val superficie = e.surface.toArgb()
    val superficie2 = e.surfaceVariant.toArgb()
    val tinta = e.onBackground.toArgb()
    val tenue = e.onSurfaceVariant.toArgb()
    val primario = e.primary.toArgb()
    val secundario = e.secondary.toArgb()
    val terciario = e.tertiary.toArgb()
    val error = e.error.toArgb()
    val linea = e.outlineVariant.toArgb()
    private val fu = LibroDiario.fuentes(tema)
    val titulo: Typeface = runCatching { ResourcesCompat.getFont(ctx, fu.titulo) }.getOrNull() ?: Typeface.SERIF
    val texto: Typeface = runCatching { ResourcesCompat.getFont(ctx, fu.texto) }.getOrNull() ?: Typeface.SANS_SERIF
    val mono: Typeface = runCatching { ResourcesCompat.getFont(ctx, R.font.plex_mono_regular) }.getOrNull() ?: Typeface.MONOSPACE
    /** Las cifras: en temas de letra caligráfica o gótica, la del texto (se leen mejor). */
    val cifra: Typeface = if (tema in setOf(TemaId.RENACIMIENTO, TemaId.GOTICO, TemaId.MEDIEVAL, TemaId.SEDA, TemaId.COLONOS, TemaId.NAVIDAD)) texto
        else if (tema in setOf(TemaId.CIBER, TemaId.REFUGIO)) mono else titulo

    fun p(color: Int, tam: Float, tf: Typeface = texto, espacio: Float = 0f) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color; textSize = tam; typeface = tf; letterSpacing = espacio
    }
    fun relleno(color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.FILL }
    fun trazo(color: Int, ancho: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = ancho; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND }

    /** Mezcla dos colores (t de 0 a 1). */
    fun mezcla(a: Int, b: Int, t: Double): Int {
        val f = t.coerceIn(0.0, 1.0)
        fun c(sh: Int) = (((a shr sh) and 0xFF) * (1 - f) + ((b shr sh) and 0xFF) * f).roundToInt()
        return (0xFF shl 24) or (c(16) shl 16) or (c(8) shl 8) or c(0)
    }
    /** Color del ánimo (1 a 7): del color de alerta, pasando por la línea, al color del tema. */
    fun colorAnimo(v: Double): Int { val t = (v - 1) / 6; return if (t < 0.5) mezcla(error, linea, t * 2) else mezcla(linea, primario, (t - 0.5) * 2) }

    companion object {
        /** Claro u oscuro sin Compose: lo que elegiste en la app, o lo del teléfono. */
        fun oscuroFuera(ctx: Context, tema: TemaId): Boolean = tema.soloOscuro || when (Repo.config.modo) {
            "oscuro" -> true; "claro" -> false
            else -> (ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }
}

/**
 * Reportes semanal (imagen), mensual (PDF e imagen) y anual, con los colores, letras, nombres y números del tema.
 * Sin mapas ni direcciones para poder compartirlos; nunca incluyen lo del seguimiento privado ni el texto de las cartas.
 */
object Reportes {
    private const val W = 1080f
    private const val M = 64f

    /** Quien dibuja: lleva la altura y sabe poner cada pieza con el tema. */
    private class Hoja(val c: Canvas, val L: Lienzo, val discreto: Boolean) {
        var y = 0f
        var seccion = 0
        val ancho = W - 2 * M

        fun envolver(t: CharSequence, p: TextPaint, w: Float = ancho, alinear: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL, max: Int = 50): StaticLayout =
            StaticLayout.Builder.obtain(t, 0, t.length, p, w.toInt()).setAlignment(alinear).setLineSpacing(0f, 1.18f).setIncludePad(false).setMaxLines(max)
                .setEllipsize(android.text.TextUtils.TruncateAt.END).build()
        fun texto(t: CharSequence, p: TextPaint, x: Float = M, w: Float = ancho, alinear: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL, max: Int = 50): Float {
            val l = envolver(t, p, w, alinear, max)
            c.save(); c.translate(x, y); l.draw(c); c.restore()
            y += l.height
            return l.height.toFloat()
        }

        /** Adorno del tema entre secciones: la greca en los clásicos, una línea con su signo en los demás. */
        fun ornamento() {
            val yy = y + 10f
            when (L.tema) {
                TemaId.ROMANO, TemaId.HELENO -> {
                    val u = 8f; val paso = u * 4
                    val pa = L.trazo(L.primario, 2.4f).apply { strokeCap = Paint.Cap.SQUARE; strokeJoin = Paint.Join.MITER }
                    val path = Path(); var x = M
                    while (x + paso <= W - M) {
                        path.moveTo(x, yy + 3 * u); path.lineTo(x, yy); path.lineTo(x + 3 * u, yy); path.lineTo(x + 3 * u, yy + 2 * u); path.lineTo(x + u, yy + 2 * u); path.lineTo(x + u, yy + u); path.lineTo(x + 2 * u, yy + u)
                        path.moveTo(x, yy + 3 * u); path.lineTo(x + paso, yy + 3 * u)
                        x += paso
                    }
                    c.drawPath(path, pa); y += 3 * u + 34f
                }
                TemaId.CIBER, TemaId.REFUGIO -> {
                    val p = L.p(L.primario, 22f, L.mono)
                    val s = "─".repeat(64)
                    c.drawText(s, M, yy + 10f, p.apply { textSize = 22f }); y += 44f
                }
                TemaId.DISTOPIA, TemaId.URSS, TemaId.ORDEN -> {
                    c.drawRect(M, yy, W - M, yy + 8f, L.relleno(L.primario)); y += 38f
                }
                else -> {
                    val signo = Estilo.comentario.trim().ifEmpty { "◆" }
                    val ps = L.p(L.primario, 30f, L.texto)
                    val sw = ps.measureText(signo)
                    val cx = W / 2
                    c.drawLine(M, yy + 2f, cx - sw / 2 - 18f, yy + 2f, L.trazo(L.linea, 2f))
                    c.drawLine(cx + sw / 2 + 18f, yy + 2f, W - M, yy + 2f, L.trazo(L.linea, 2f))
                    c.drawText(signo, cx - sw / 2, yy + 12f, ps); y += 40f
                }
            }
        }

        fun encabezado(titulo: String, latin: String?, rango: String) {
            c.drawColor(L.fondo)
            y = 92f
            val marca = if (L.tema == TemaId.ROMANO) "VESTIGIVM MEI" else "Vestigium Mei"
            c.drawText(marca, M, y, L.p(L.tenue, 26f, L.titulo, 0.2f)); y += 30f
            val (t, com) = Estilo.pantalla(titulo, latin)
            texto(t, L.p(L.primario, if (t.length > 22) 60f else 76f, L.titulo, 0.03f), max = 2); y += 8f
            com?.let { texto(it, L.p(L.tenue, 24f, L.texto), max = 1); y += 4f }
            texto(rango, L.p(L.tinta, 26f, L.texto), max = 2); y += 18f
            ornamento()
        }

        fun cifras(l: List<Pair<String, String>>) {
            if (l.isEmpty()) return
            val col = (ancho - 2 * 18f) / 3; val alto = 128f
            l.forEachIndexed { i, (v, et) ->
                val x = M + (i % 3) * (col + 18f); val yy = y + (i / 3) * (alto + 18f)
                c.drawRoundRect(RectF(x, yy, x + col, yy + alto), 10f, 10f, L.relleno(L.superficie2))
                c.drawRect(RectF(x, yy + 14f, x + 5f, yy + alto - 14f), L.relleno(L.primario))
                val pv = L.p(L.tinta, if (v.length > 9) 30f else 40f, L.cifra)
                c.drawText(v, x + 22f, yy + 62f, pv)
                val pe = L.p(L.tenue, 19f, L.texto, 0.02f)
                val etq = envolver(et, pe, col - 34f, max = 2)
                c.save(); c.translate(x + 22f, yy + 76f); etq.draw(c); c.restore()
            }
            y += ((l.size + 2) / 3) * (alto + 18f) + 16f
        }

        fun titulo(es: String, latin: String) {
            seccion++
            y += 18f
            val (t, com) = Estilo.seccion(es, latin)
            val num = Estilo.numeral(seccion)
            val pn = L.p(L.primario, 30f, L.titulo)
            c.drawText(num, M, y + 30f, pn)
            val x = M + pn.measureText(num) + 22f
            c.drawText(t, x, y + 30f, L.p(L.tinta, 32f, L.titulo, 0.04f)); y += 42f
            com?.let { c.drawText(it, x, y + 18f, L.p(L.tenue, 20f, L.texto)); y += 28f }
            y += 14f
        }

        /** Línea «clave = valor» como en la pantalla del día (las claves se traducen al tema). */
        fun clave(k: String, v: String) {
            val pk = L.p(L.tenue, 22f, L.mono); val pv = L.p(L.tinta, 23f, L.texto)
            val kk = Estilo.clave(k)
            c.drawText(kk, M, y + 24f, pk)
            c.drawText("=", M + 206f, y + 24f, L.p(L.primario, 22f, L.mono))
            val h = envolver(v, pv, ancho - 236f, max = 3)
            c.save(); c.translate(M + 236f, y + 2f); h.draw(c); c.restore()
            y += maxOf(36f, h.height + 12f)
        }

        fun barras(l: List<Triple<String, Double, String>>, color: Int = L.primario) {
            val max = l.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: return
            val pe = L.p(L.tinta, 22f, L.texto); val pv = L.p(L.tenue, 20f, L.mono)
            l.forEach { (et, v, txt) ->
                val etq = recortar(et, pe, 300f)
                c.drawText(etq, M, y + 26f, pe)
                val bx = M + 320f; val bwMax = W - M - bx - 150f
                val bw = (bwMax * v / max).toFloat().coerceAtLeast(6f)
                c.drawRoundRect(RectF(bx, y + 8f, bx + bw, y + 30f), 6f, 6f, L.relleno(color))
                c.drawText(txt, bx + bw + 12f, y + 27f, pv)
                y += 40f
            }
            y += 8f
        }

        /** Ánimo día por día: valencia (color del tema) y activación (secundario). */
        fun graficaAnimo(d: Periodo.Datos) {
            val gx0 = M + 34f; val gx1 = W - M; val gy0 = y + 10f; val gy1 = y + 260f
            val ej = L.trazo(L.linea, 1.6f)
            for (v in 1..7) {
                val yy = gy1 - (v - 1) / 6f * (gy1 - gy0)
                c.drawLine(gx0, yy, gx1, yy, ej)
                if (v % 2 == 1) c.drawText("$v", M, yy + 8f, L.p(L.tenue, 18f, L.mono))
            }
            val n = d.fechas.size
            fun x(i: Int) = if (n == 1) (gx0 + gx1) / 2 else gx0 + i * (gx1 - gx0) / (n - 1)
            fun serie(vals: List<Double?>, color: Int, puntos: Boolean) {
                val path = Path(); var abierto = false
                vals.forEachIndexed { i, v ->
                    if (v == null) { abierto = false; return@forEachIndexed }
                    val yy = gy1 - ((v - 1) / 6.0 * (gy1 - gy0)).toFloat()
                    if (!abierto) { path.moveTo(x(i), yy); abierto = true } else path.lineTo(x(i), yy)
                    if (puntos && n <= 40) c.drawCircle(x(i), yy, 6f, L.relleno(L.colorAnimo(v)))
                }
                c.drawPath(path, L.trazo(color, if (n > 60) 2.5f else 4f))
            }
            serie(d.activacion, L.terciario, false); serie(d.valencia, L.primario, true)
            val pe = L.p(L.tenue, 18f, L.mono)
            val paso = maxOf(1, n / 7)
            d.fechas.forEachIndexed { i, f ->
                if (i % paso != 0) return@forEachIndexed
                val fd = Fechas.parse(f)
                val t = if (n <= 8) Estilo.semana[fd.dayOfWeek.value - 1] else "${fd.dayOfMonth}"
                c.drawText(t, x(i) - pe.measureText(t) / 2, gy1 + 30f, pe)
            }
            y = gy1 + 52f
            c.drawCircle(M + 8f, y - 6f, 7f, L.relleno(L.primario)); c.drawText("ánimo", M + 24f, y, L.p(L.tenue, 20f, L.texto))
            c.drawCircle(M + 160f, y - 6f, 7f, L.relleno(L.terciario)); c.drawText("energía", M + 176f, y, L.p(L.tenue, 20f, L.texto))
            y += 26f
        }

        /** Una barra por día (horas de sueño). */
        fun barrasDia(vals: List<Double?>, fechas: List<String>, unidad: String, meta: Double? = null) {
            if (vals.all { it == null }) return
            val gx0 = M; val gx1 = W - M; val alto = 150f; val base = y + alto
            val max = maxOf(vals.filterNotNull().maxOrNull() ?: 1.0, meta ?: 0.0) * 1.1
            val n = vals.size; val bw = (gx1 - gx0) / n
            meta?.let { val yy = base - (it / max * alto).toFloat(); c.drawLine(gx0, yy, gx1, yy, L.trazo(L.linea, 2f)); }
            vals.forEachIndexed { i, v ->
                v ?: return@forEachIndexed
                val h = (v / max * alto).toFloat()
                c.drawRoundRect(RectF(gx0 + i * bw + bw * 0.18f, base - h, gx0 + (i + 1) * bw - bw * 0.18f, base), 4f, 4f,
                    L.relleno(if (meta != null && v < meta * 0.9) L.mezcla(L.error, L.linea, 0.3) else L.primario))
                if (n <= 8) c.drawText("%.1f".format(v), gx0 + i * bw + bw / 2 - 20f, base - h - 8f, L.p(L.tinta, 18f, L.mono))
            }
            if (n <= 8) fechas.forEachIndexed { i, f ->
                val t = Estilo.semana[Fechas.parse(f).dayOfWeek.value - 1]
                val pe = L.p(L.tenue, 18f, L.mono); c.drawText(t, gx0 + i * bw + bw / 2 - pe.measureText(t) / 2, base + 26f, pe)
            }
            y = base + if (n <= 8) 44f else 18f
            meta?.let { c.drawText("— la línea: ${"%.0f".format(it)} $unidad recomendadas", M, y + 6f, L.p(L.tenue, 18f, L.texto)); y += 26f }
        }

        /**
         * La semana de un vistazo: una columna por día con la medalla del ánimo, la pila de páginas que escribiste,
         * la barra del sueño, las huellas de los pasos y los puntos de los momentos.
         */
        fun semanaVisual(dias: List<DiaVisual>) {
            if (dias.isEmpty()) return
            val n = dias.size; val col = ancho / n
            fun cx(i: Int) = M + col * i + col / 2
            fun rotulo(t: String) { c.drawText(t, M, y + 20f, L.p(L.tenue, 19f, L.texto, 0.04f)); y += 30f }
            c.drawRoundRect(RectF(M - 18f, y - 6f, W - M + 18f, y + 16f), 8f, 8f, L.relleno(L.superficie2))
            val pd = L.p(L.tinta, 22f, L.titulo)
            dias.forEachIndexed { i, d ->
                val fd = Fechas.parse(d.fecha)
                val t = Estilo.semana[fd.dayOfWeek.value - 1] + " " + fd.dayOfMonth
                c.drawText(t, cx(i) - pd.measureText(t) / 2, y + 12f, pd)
            }
            y += 34f
            // ---- ánimo: medallas del color del día
            rotulo("ánimo")
            val r = minOf(col * 0.32f, 40f)
            dias.forEachIndexed { i, d ->
                val o = cx(i); val yy = y + r
                if (d.animo != null) {
                    c.drawCircle(o, yy, r, L.relleno(L.colorAnimo(d.animo)))
                    c.drawCircle(o, yy, r, L.trazo(L.mezcla(L.colorAnimo(d.animo), L.tinta, 0.35), 2.5f))
                    val pv = L.p(L.fondo, 26f, L.cifra); val tv = "%.0f".format(d.animo)
                    c.drawText(tv, o - pv.measureText(tv) / 2, yy + 9f, pv)
                } else c.drawCircle(o, yy, r, L.trazo(L.linea, 2f).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(6f, 6f), 0f) })
            }
            y += 2 * r + 18f
            // ---- diario: una hoja por cada 80 palabras, apiladas como un cuaderno
            if (dias.any { it.palabras > 0 }) {
                rotulo("páginas escritas")
                val alto = 120f; val base = y + alto
                dias.forEachIndexed { i, d ->
                    val hojas = if (d.palabras == 0) 0 else ((d.palabras + 79) / 80).coerceIn(1, 12)
                    val o = cx(i); val hw = col * 0.3f
                    for (k in 0 until hojas) {
                        val yy = base - (k + 1) * 9.5f; val dx = if (k % 2 == 0) -1.5f else 1.5f
                        c.drawRoundRect(RectF(o - hw + dx, yy, o + hw + dx, yy + 8f), 2f, 2f, L.relleno(L.mezcla(L.superficie, L.fondo, 0.2)))
                        c.drawRoundRect(RectF(o - hw + dx, yy, o + hw + dx, yy + 8f), 2f, 2f, L.trazo(L.mezcla(L.linea, L.tinta, 0.3), 1.4f))
                    }
                    if (hojas > 0) c.drawRect(RectF(o - hw * 0.6f, base - hojas * 9.5f - 4f, o - hw * 0.45f, base - hojas * 9.5f + 6f), L.relleno(L.primario))
                    val tv = if (d.palabras >= 1000) "%.1fk".format(d.palabras / 1000.0) else "${d.palabras}"
                    val pv = L.p(L.tenue, 18f, L.mono)
                    if (d.palabras > 0) c.drawText(tv, o - pv.measureText(tv) / 2, base + 24f, pv)
                }
                y = base + 36f
            }
            // ---- sueño: barras con su luna
            if (dias.any { it.suenoH != null }) {
                rotulo("sueño")
                val alto = 110f; val base = y + alto
                c.drawLine(M, base - alto * 7f / 10f, W - M, base - alto * 7f / 10f, L.trazo(L.linea, 1.2f).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(8f, 8f), 0f) })
                dias.forEachIndexed { i, d ->
                    val h0 = d.suenoH ?: return@forEachIndexed
                    val o = cx(i); val bw = col * 0.24f
                    val hh = (h0.coerceIn(0.0, 10.0) / 10.0 * alto).toFloat()
                    c.drawRoundRect(RectF(o - bw, base - hh, o + bw, base), 6f, 6f, L.relleno(if (h0 < 6.3) L.mezcla(L.error, L.linea, 0.3) else L.secundario))
                    // Media luna arriba de la barra.
                    val lr = bw * 0.75f; val ly = base - hh - lr - 6f
                    c.drawCircle(o, ly, lr, L.relleno(L.terciario)); c.drawCircle(o + lr * 0.45f, ly - lr * 0.2f, lr * 0.85f, L.relleno(L.fondo))
                    val tv = "%.1f".format(h0); val pv = L.p(L.tenue, 18f, L.mono)
                    c.drawText(tv, o - pv.measureText(tv) / 2, base + 24f, pv)
                }
                y = base + 36f
            }
            // ---- pasos: una huella por cada 2 000
            if (dias.any { (it.pasos ?: 0) > 0 }) {
                rotulo("pasos")
                val filas = 5; val base = y + filas * 18f
                dias.forEachIndexed { i, d ->
                    val p0 = d.pasos ?: return@forEachIndexed
                    val k = ((p0 + 1999) / 2000).toInt().coerceIn(0, 10)
                    val o = cx(i)
                    for (j in 0 until k) {
                        val izq = j % 2 == 0; val fila = j / 2
                        val xx = o + (if (izq) -9f else 9f); val yy = base - fila * 18f - (if (izq) 0f else 7f)
                        c.drawOval(RectF(xx - 5f, yy - 8f, xx + 5f, yy + 4f), L.relleno(L.primario))
                    }
                    val tv = if (p0 >= 1000) "%.1fk".format(p0 / 1000.0) else "$p0"; val pv = L.p(L.tenue, 18f, L.mono)
                    c.drawText(tv, o - pv.measureText(tv) / 2, base + 28f, pv)
                }
                y = base + 40f
            }
            // ---- momentos: un punto por cada uno
            if (dias.any { it.momentos > 0 }) {
                rotulo("momentos")
                dias.forEachIndexed { i, d ->
                    val k = d.momentos.coerceAtMost(12); val o = cx(i)
                    for (j in 0 until k) {
                        val fila = j / 4; val pos = j % 4
                        c.drawCircle(o - 21f + pos * 14f, y + 8f + fila * 14f, 5f, L.relleno(L.terciario))
                    }
                }
                y += 14f * ((dias.maxOf { it.momentos.coerceAtMost(12) } + 3) / 4) + 16f
            }
            y += 8f
        }

        fun nota(t: String) { texto(t, L.p(L.tenue, 20f, L.texto)); y += 6f }

        fun pie(fin: String) {
            y += 24f
            ornamento()
            val t = "Vestigium Mei · " + Estilo.fecha(fin)
            val p = L.p(L.tenue, 19f, L.texto)
            c.drawText(t, W / 2 - p.measureText(t) / 2, y + 10f, p); y += 50f
        }
    }

    private fun recortar(s: String, p: Paint, ancho: Float): String {
        if (p.measureText(s) <= ancho) return s
        var t = s
        while (t.length > 3 && p.measureText("$t…") > ancho) t = t.dropLast(1)
        return "$t…"
    }

    private fun mesNombre(m: Int) = java.time.Month.of(m).getDisplayName(java.time.format.TextStyle.FULL, Fechas.ES)
    private fun n0(x: Double) = "%,.0f".format(x)
    private fun n1(x: Double) = "%.1f".format(x)
    private fun hm(min: Double) = "${(min / 60).toInt()} h ${(min % 60).toInt()} min"
    private fun hora(h: Double) = "%02d:%02d".format(((h % 24) + 24).toInt() % 24, ((h - h.toInt()) * 60).roundToInt().coerceIn(0, 59))
    private fun dinero(x: Double) = "$" + n0(x)
    private fun inicial(s: String) = s.split('_', ' ', '-').filter { it.isNotBlank() }.joinToString("") { it.take(1).uppercase() + "." }
    private fun persona(p: String, discreto: Boolean) = if (discreto) inicial(p) else "@$p"
    private fun cambio(a: Double?, b: Double?, unidad: String, dec: Boolean = true): String? {
        if (a == null || b == null) return null
        val d = a - b
        if (abs(d) < (if (dec) 0.05 else 0.5)) return "igual que el periodo anterior"
        return (if (d > 0) "+" else "−") + (if (dec) n1(abs(d)) else n0(abs(d))) + " $unidad frente al periodo anterior"
    }

    /** Lo de cada día para la semana de un vistazo (sin nada del seguimiento privado). */
    private class DiaVisual(val fecha: String, val animo: Double?, val palabras: Int, val suenoH: Double?, val pasos: Long?, val momentos: Int)

    private fun diasVisuales(d: Periodo.Datos): List<DiaVisual> {
        val porDia = Repo.momentos.filter { !it.privado && it.fecha in d.ini..d.fin }.groupingBy { it.fecha }.eachCount()
        return d.fechas.mapIndexed { i, f ->
            val dia = Repo.dias[f]
            val palabras = dia?.nota?.trim()?.takeIf { it.isNotEmpty() }?.split(Regex("\\s+"))?.size ?: 0
            val s = Repo.salud[f]
            DiaVisual(f, d.valencia.getOrNull(i), palabras, s?.sueno_min?.let { it / 60.0 }, s?.pasos, porDia[f] ?: 0)
        }
    }

    /** Todas las secciones que tengan datos, en el orden del tema. */
    private fun cuerpo(h: Hoja, d: Periodo.Datos, anual: Boolean, logros: List<io.github.ruddy2003.vestigium.ciencia.Logros.Logro>) {
        val L = h.L
        h.cifras(listOfNotNull(
            "${d.diasDiario}/${d.n}" to "días con diario",
            d.valMedia?.let { n1(it) to "ánimo medio (de 7)" },
            d.suenoH?.let { n1(it) + " h" to "sueño por noche" },
            d.pasos?.let { n0(it) to "pasos al día" },
            d.pantallaMin?.let { hm(it) to "pantalla al día" },
            (d.palabras > 0).takeIf { it }?.let { n0(d.palabras.toDouble()) to "palabras escritas" },
            (d.gasto > 0).takeIf { it }?.let { dinero(d.gasto) to "gastado" },
            (d.entrenos > 0).takeIf { it }?.let { "${d.entrenos}" to "entrenamientos" },
            (d.musicaH > 0.5).takeIf { it }?.let { n0(d.musicaH) + " h" to "de música" },
        ).take(9))

        // ---- La semana de un vistazo (solo en el reporte semanal)
        if (!anual && d.n <= 8) {
            h.titulo("La semana de un vistazo", "HEBDOMAS PICTA")
            h.semanaVisual(diasVisuales(d))
        }

        // ---- Ánimo
        if (d.valencia.any { it != null }) {
            h.titulo("Ánimo", "ANIMVS")
            if (anual) pixeles(h, d) else h.graficaAnimo(d)
            listOfNotNull(
                d.mejorDia?.let { "optimus" to Fechas.titulo(it).lowercase() + (d.mejorMomento?.let { m -> " · «${m.take(90)}»" } ?: "") },
                d.peorDia?.takeIf { it != d.mejorDia }?.let { "pessimus" to Fechas.titulo(it).lowercase() },
                cambio(d.valMedia, d.valAnterior, "de ánimo")?.let { "animus" to it },
                d.valPorSemana.maxByOrNull { it.value }?.takeIf { d.n > 13 }?.let { "dies felix" to "los " + DIAS_LARGOS[it.key - 1] + " (%.1f)".format(it.value) },
                d.habitosPct?.let { "consuetudo" to "${(it * 100).roundToInt()}% de tus hábitos cumplidos" + (d.habitos.firstOrNull()?.let { (n, k) -> " · más: $n ($k días)" } ?: "") },
                d.rachaDiario.takeIf { it >= 3 }?.let { "series" to "$it días seguidos escribiendo" },
            ).forEach { (k, v) -> h.clave(k, v) }
        }

        // ---- Cuerpo
        val hayCuerpo = d.suenoH != null || d.pasos != null || d.entrenos > 0 || d.kcal != null || d.ejercicioMin > 0
        if (hayCuerpo) {
            h.titulo("Cuerpo", "CORPVS")
            if (!anual && d.suenoH != null) h.barrasDia(d.suenoPorDia, d.fechas, "h", 7.0)
            listOfNotNull(
                d.suenoH?.let { "somnus" to "${n1(it)} h de media" + (d.dormirH?.let { a -> " · te dormías a las ${hora(a)}" } ?: "") + (d.despertarH?.let { b -> ", despertabas a las ${hora(b)}" } ?: "") },
                d.nocheLarga?.takeIf { anual }?.let { "somnus" to "la noche más larga: ${hm(it.second.toDouble())} (${Fechas.corto(it.first)}); la más corta: " + (d.nocheCorta?.let { c -> "${hm(c.second.toDouble())} (${Fechas.corto(c.first)})" } ?: "—") },
                cambio(d.suenoH, d.suenoAnterior, "h de sueño")?.let { "somnus" to it },
                d.pasos?.let { "passus" to "${n0(it)} al día · ${n0(d.pasosTotal.toDouble())} en total · unos ${n0(d.km)} km" },
                cambio(d.pasos, d.pasosAnterior, "pasos al día", false)?.let { "passus" to it },
                d.ejercicioMin.takeIf { it > 0 }?.let { "exercitatio" to hm(it.toDouble()) + " de ejercicio" },
                d.entrenos.takeIf { it > 0 }?.let { "robur" to "$it entrenos · ${d.series} series · ${n1(d.volumenKg / 1000)} t levantadas" + (d.pr?.let { p -> " · mejor marca: ${p.first} (${n0(p.second)} kg 1RM)" } ?: "") },
                d.kcal?.let { "nutritio" to "${n0(it)} kcal al día" + (d.proteina?.let { p -> " · ${n0(p)} g de proteína" } ?: "") },
                d.comidaTop.firstOrNull()?.let { "cibus" to "lo que más comiste: " + d.comidaTop.take(3).joinToString(", ") { it.first.lowercase() } },
                if (d.pesoIni != null && d.pesoFin != null && abs(d.pesoFin - d.pesoIni) >= 0.2) "corpus" to "peso: ${n1(d.pesoIni)} → ${n1(d.pesoFin)} kg" else null,
                d.fcReposo?.let { "cor" to "${n0(it)} latidos por minuto en reposo" },
            ).forEach { (k, v) -> h.clave(k, v) }
        }

        // ---- Mente
        if (d.reacciones > 0 || d.cuestionarios.isNotEmpty() || d.suenosRecordados > 0) {
            h.titulo("Mente", "MENS")
            listOfNotNull(
                d.reaccionMs?.let { "celeritas" to "${n0(it)} ms de reacción (${d.reacciones} pruebas)" },
                d.suenosRecordados.takeIf { it > 0 }?.let { "somnium" to "$it sueños recordados" },
            ).forEach { (k, v) -> h.clave(k, v) }
            d.cuestionarios.take(if (anual) 4 else 6).forEach { h.clave("quaestiones", it) }
        }

        // ---- Gente
        if (d.personas.isNotEmpty() || d.momentos.isNotEmpty() || d.waMios + d.waOtros > 0 || d.llamadas > 0) {
            h.titulo("Gente", "HOMINES")
            if (d.personas.isNotEmpty()) h.barras(d.personas.take(6).map { Triple(persona(it.first, h.discreto), it.second.toDouble(), "${it.second} días") }, L.primario)
            listOfNotNull(
                d.personaNueva?.takeIf { anual }?.let { "personae" to "alguien nuevo: ${persona(it, h.discreto)}" },
                d.temas.takeIf { it.isNotEmpty() }?.let { "themata" to it.take(5).joinToString("  ") { "#${it.first}" } },
                d.momentos.takeIf { it.isNotEmpty() }?.let { "momenta" to it.take(4).joinToString(" · ") { (t, k) -> "${t.lowercase()} $k" } },
                (d.waMios + d.waOtros).takeIf { it > 0 }?.let { "voces" to "${n0(d.waMios.toDouble())} mensajes tuyos y ${n0(d.waOtros.toDouble())} recibidos en WhatsApp" },
                d.llamadas.takeIf { it > 0 }?.let { "voces" to "$it llamadas" },
                d.cartas.takeIf { it > 0 }?.let { "epistulae" to "$it cartas escritas" },
            ).forEach { (k, v) -> h.clave(k, v) }
        }

        // ---- Mundo
        if (d.ciudades.isNotEmpty() || d.fotos > 0 || d.tempMax != null) {
            h.titulo("Mundo", "ORBIS")
            listOfNotNull(
                d.ciudades.takeIf { it.isNotEmpty() }?.let { "loca" to it.take(if (anual) 8 else 5).joinToString(" · ") { (c, p) -> "${Mundo.bandera(p)} $c" } + if (it.size > 8) " y ${it.size - 8} más" else "" },
                d.ciudadesNuevas.takeIf { it.isNotEmpty() }?.let { "nova" to "${it.size} ciudades nuevas" + if (d.paisesNuevos.isNotEmpty()) " · países nuevos: " + d.paisesNuevos.joinToString(" ") { p -> Mundo.bandera(p) } else "" },
                d.sitios.takeIf { it.isNotEmpty() }?.let { "mirabilia" to it.take(3).joinToString(" · ") },
                d.fotos.takeIf { it > 0 }?.let { "imagines" to "${n0(it.toDouble())} fotos" },
                d.tempMax?.let { "caelum" to "máximas de ${n0(it)}° de media · ${d.diasLluvia} días de lluvia" + (d.luzH?.let { l -> " · ${n1(l)} h de luz" } ?: "") },
            ).forEach { (k, v) -> h.clave(k, v) }
        }

        // ---- Cultura
        if (d.obras.isNotEmpty() || d.artistas.isNotEmpty() || d.ytVideos > 0 || d.subrayados > 0 || d.frases > 0) {
            h.titulo("Cultura", "CVLTVRA")
            if (d.artistas.isNotEmpty()) h.barras(d.artistas.take(5).map { Triple(it.first, it.second, n1(it.second / 60) + " h") }, L.secundario)
            listOfNotNull(
                d.obras.takeIf { it.isNotEmpty() }?.let { "opera" to it.take(5).joinToString(" · ") { o -> o.titulo + (o.calificacion?.let { c -> " ($c/10)" } ?: "") } },
                d.mejorObra?.takeIf { d.obras.size > 1 }?.let { "praeclarum" to "${it.titulo} · ${it.calificacion}/10" },
                d.cancionTop?.let { "musica" to "${it.first} (${it.second} veces)" },
                d.artistaNuevo?.let { "nova" to "descubriste a $it" },
                d.ytVideos.takeIf { it > 0 }?.let { "youtube" to "${n0(it.toDouble())} videos" + (d.ytCanales.firstOrNull()?.let { c -> " · más: ${c.first}" } ?: "") + if (d.ytMadrugada > 0) " · ${d.ytMadrugada} de madrugada" else "" },
                d.subrayados.takeIf { it > 0 }?.let { "lectio" to "$it subrayados" },
                d.frase?.let { "sententia" to "«${it.texto.take(110)}» — ${it.autor}" },
            ).forEach { (k, v) -> h.clave(k, v) }
        }

        // ---- Dinero
        if (d.gasto > 0 || d.ingresos > 0) {
            h.titulo("Dinero", "PECVNIA")
            if (d.gastoCategorias.isNotEmpty()) h.barras(d.gastoCategorias.take(5).map { Triple(it.first, it.second, dinero(it.second)) }, L.terciario)
            listOfNotNull(
                "svmptvs" to dinero(d.gasto) + " gastados · ${dinero(d.gasto / d.n)} al día",
                d.gastoImprevisto.takeIf { it > 0 }?.let { "svmptvs" to dinero(it) + " en imprevistos" },
                d.ingresos.takeIf { it > 0 }?.let { "reditus" to dinero(it) + " · balance ${if (it - d.gasto >= 0) "+" else "−"}${dinero(abs(it - d.gasto))}" },
            ).forEach { (k, v) -> h.clave(k, v) }
        }

        // ---- Teléfono
        if (d.pantallaMin != null) {
            h.titulo("Teléfono", "TELEPHONVM")
            if (d.apps.isNotEmpty()) h.barras(d.apps.take(5).map { Triple(it.first, it.second.toDouble(), hm(it.second.toDouble())) }, L.linea)
            listOfNotNull(
                "telephonum" to hm(d.pantallaMin) + " al día" + (cambio(d.pantallaMin, d.pantallaAnterior, "min", false)?.let { " · $it" } ?: ""),
                d.desbloqueos?.let { "usus" to "${n0(it)} desbloqueos al día" },
                d.notifs.takeIf { it > 0 }?.let { "monita" to "${n0(it.toDouble())} notificaciones" },
            ).forEach { (k, v) -> h.clave(k, v) }
        }

        // ---- Lo memorable
        if (d.hitos.isNotEmpty() || logros.isNotEmpty()) {
            h.titulo("Lo memorable", "MEMORABILIA")
            if (d.hitos.isNotEmpty()) h.clave("memorabilia", d.hitos.take(8).joinToString(", ") { it.replace('_', ' ') })
            if (logros.isNotEmpty()) h.clave("palmae", logros.take(8).joinToString(" · ") { "${it.icono} ${it.nombre}" })
        }
    }

    private val DIAS_LARGOS = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábados", "domingos")

    /** El año en cuadros: un cuadro por día, del color de tu ánimo; y la media de cada mes. */
    private fun pixeles(h: Hoja, d: Periodo.Datos) {
        val L = h.L; val c = h.c
        val anio = d.ini.take(4).toInt()
        val celda = (h.ancho - 90f) / 31f
        for (mes in 1..12) {
            val yy = h.y + (mes - 1) * (celda + 4f)
            c.drawText(Estilo.mes(mes).take(4), M, yy + celda * 0.8f, L.p(L.tenue, 18f, L.texto))
            val dm = java.time.YearMonth.of(anio, mes).lengthOfMonth()
            for (dd in 1..dm) {
                val f = "%04d-%02d-%02d".format(anio, mes, dd)
                val i = d.fechas.indexOf(f)
                val v = if (i >= 0) d.valencia[i] else null
                val x = M + 90f + (dd - 1) * celda
                c.drawRoundRect(RectF(x, yy, x + celda - 3f, yy + celda), 3f, 3f, L.relleno(if (v != null) L.colorAnimo(v) else L.superficie2))
            }
        }
        h.y += 12 * (celda + 4f) + 30f
        val altoB = 160f; val anchoM = h.ancho / 12f
        for (mes in 1..12) {
            val v = d.valPorMes[mes] ?: continue
            val x = M + (mes - 1) * anchoM
            val hh = ((v - 1) / 6 * altoB).toFloat()
            c.drawRoundRect(RectF(x + 8f, h.y + altoB - hh, x + anchoM - 8f, h.y + altoB), 5f, 5f, L.relleno(L.colorAnimo(v)))
            c.drawText(n1(v), x + 10f, h.y + altoB - hh - 8f, L.p(L.tinta, 17f, L.mono))
        }
        for (mes in 1..12) c.drawText(Estilo.mes(mes).take(3), M + (mes - 1) * anchoM + 10f, h.y + altoB + 26f, L.p(L.tenue, 16f, L.texto))
        h.y += altoB + 50f
    }

    /** Dibuja dos veces: primero para medir la altura, luego en su tamaño justo. */
    private fun dibujarMedido(ctx: Context, dibujo: (Canvas) -> Float): Pair<Picture, Int> {
        val pic = Picture()
        val alto = dibujo(pic.beginRecording(W.toInt(), 12000)).roundToInt().coerceIn(800, 12000)
        pic.endRecording()
        return pic to alto
    }

    private fun bitmap(pic: Picture, alto: Int, fondo: Int): Bitmap {
        val bmp = Bitmap.createBitmap(W.toInt(), alto, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp); c.drawColor(fondo); c.drawPicture(pic)
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

    private fun guardarPdf(ctx: Context, nombre: String, pic: Picture, alto: Int, fondo: Int): Uri? {
        val doc = PdfDocument()
        val pagina = doc.startPage(PdfDocument.PageInfo.Builder(W.toInt(), alto, 1).create())
        pagina.canvas.drawColor(fondo); pagina.canvas.drawPicture(pic)
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

    private fun reporte(ctx: Context, titulo: String, latin: String, rango: String, ini: String, fin: String, anual: Boolean): Triple<Picture, Int, Int> {
        val L = Lienzo(ctx)
        val d = Periodo.calcular(ctx, ini, fin)
        val logros = runCatching { io.github.ruddy2003.vestigium.ciencia.Logros.todos(ctx).filter { l -> l.conseguido?.let { it in ini..fin } == true } }.getOrDefault(emptyList())
        val (pic, alto) = dibujarMedido(ctx) { c ->
            val h = Hoja(c, L, Repo.config.discreto)
            h.encabezado(titulo, latin, rango)
            cuerpo(h, d, anual, logros)
            h.pie(fin)
            h.y
        }
        return Triple(pic, alto, L.fondo)
    }

    /** La imagen del reporte semanal (sin guardarla). */
    fun imagenSemana(ctx: Context, lunes: String): Bitmap {
        val fin = Fechas.sumar(lunes, 6)
        val ini = Fechas.parse(lunes); val f = Fechas.parse(fin)
        val semana = f.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val rango = "Semana ${Estilo.numeral(semana)} · del ${ini.dayOfMonth} de ${mesNombre(ini.monthValue)} al ${f.dayOfMonth} de ${mesNombre(f.monthValue)} de ${f.year}"
        val (pic, alto, fondo) = reporte(ctx, "Tu semana", "HEBDOMAS", rango, lunes, fin, false)
        return bitmap(pic, alto, fondo)
    }

    /** La imagen del reporte de un año (sin guardarla). */
    fun imagenAnio(ctx: Context, anio: Int): Bitmap {
        val fin = minOf("$anio-12-31", Fechas.diaLogico())
        val (pic, alto, fondo) = reporte(ctx, "Tu año", "ANNVS", "$anio", "$anio-01-01", fin, true)
        return bitmap(pic, alto, fondo)
    }

    fun semanal(ctx: Context, lunes: String, avisar: Boolean): Uri? {
        val uri = guardarPng(ctx, "semana_$lunes.png", imagenSemana(ctx, lunes))
        if (avisar && uri != null) Avisos.reporte(ctx, "Tu reporte semanal está listo", uri, "image/png")
        return uri
    }

    fun mensual(ctx: Context, ym: String, avisar: Boolean): Uri? {
        val ini = "$ym-01"
        val d = Fechas.parse(ini)
        val fin = Fechas.iso(d.plusMonths(1).minusDays(1))
        val (pic, alto, fondo) = reporte(ctx, "Tu mes", "MENSIS", Estilo.mes(d.monthValue, d.year), ini, fin, false)
        guardarPng(ctx, "mes_$ym.png", bitmap(pic, alto, fondo))
        val uri = guardarPdf(ctx, "mes_$ym.pdf", pic, alto, fondo)
        if (avisar && uri != null) Avisos.reporte(ctx, "Tu reporte de ${Fechas.mesTitulo(ym)} está listo", uri, "application/pdf")
        return uri
    }

    fun anual(ctx: Context, anio: Int): Uri? {
        val hoy = Fechas.diaLogico()
        val fin = minOf("$anio-12-31", hoy)
        val (pic, alto, fondo) = reporte(ctx, "Tu año", "ANNVS", "$anio" + if (fin < "$anio-12-31") " · hasta el ${Fechas.titulo(fin).lowercase()}" else "", "$anio-01-01", fin, true)
        val png = guardarPng(ctx, "anno_$anio.png", bitmap(pic, alto, fondo))
        guardarPdf(ctx, "anno_$anio.pdf", pic, alto, fondo)
        return png
    }
}
