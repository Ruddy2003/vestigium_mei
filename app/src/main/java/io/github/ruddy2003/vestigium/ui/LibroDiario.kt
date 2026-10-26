package io.github.ruddy2003.vestigium.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.alFuturo
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * El diario como libro: todas las entradas, y cómo sacarlas de la app (PDF con el diseño del tema,
 * HTML, Markdown, una nota por día para Obsidian, o texto plano). Todo se genera en el teléfono.
 */
object LibroDiario {
    /** Entradas con texto, de la más reciente a la más antigua. */
    fun entradas(): List<Dia> = Repo.dias.values.filter { it.nota.isNotBlank() }.sortedByDescending { it.fecha }

    /** Noches con un sueño escrito, de la más reciente a la más antigua. */
    fun suenos(): List<Dia> = Repo.dias.values.filter { it.sueno_texto.isNotBlank() }.sortedByDescending { it.fecha }

    /** Una línea con lo que se anotó del sueño: qué tan agradable, lucidez, vividez, tipo, temas y cómo dormiste. */
    fun metaSueno(d: Dia): String = listOfNotNull(
        d.sueno_valencia?.let { "agradable $it/7" },
        d.sueno_lucidez?.let { id -> Catalogo.SUENO_LUCIDEZ.firstOrNull { it.id == id }?.let { "lucidez " + it.nombre.lowercase() } },
        d.sueno_vividez?.let { id -> Catalogo.SUENO_VIVIDEZ.firstOrNull { it.id == id }?.nombre?.lowercase() },
        d.sueno_tipo?.let { id -> Catalogo.SUENO_TIPO.firstOrNull { it.id == id }?.nombre?.lowercase() },
        d.sueno_temas.takeIf { it.isNotEmpty() }?.joinToString(", "),
        d.sueno_calidad?.let { "dormiste $it/5" },
        Repo.salud[d.fecha]?.sueno_min?.let { "${it / 60} h ${"%02d".format(it % 60)} según el reloj" },
    ).joinToString(" · ")

    /** Una línea con lo medido ese día: ánimo, energía, sueño, contexto, hitos. */
    fun meta(d: Dia): String = listOfNotNull(
        d.valencia?.let { "ánimo $it/7" }, d.activacion?.let { "energía $it/7" }, d.dolor?.takeIf { it > 0 }?.let { "dolor $it/5" },
        Repo.salud[d.fecha]?.sueno_min?.let { "sueño ${it / 60} h ${"%02d".format(it % 60)}" },
        Catalogo.CONTEXTOS.filter { d.ctx[it.id] == true }.takeIf { it.isNotEmpty() }?.joinToString(", ") { it.nombre.lowercase() },
        d.hitos.takeIf { it.isNotEmpty() }?.joinToString(", ") { "★ " + it.replace('_', ' ') },
    ).joinToString(" · ")

    private fun carpeta(ctx: Context) = File(File(ctx.cacheDir, "export"), "diario").apply { mkdirs(); listFiles()?.forEach { it.delete() } }

    private fun nombre(lista: List<Dia>, ext: String) =
        if (lista.size == 1) "diario_${lista[0].fecha}.$ext" else "diario_${lista.minOf { it.fecha }}_a_${lista.maxOf { it.fecha }}.$ext"

    fun compartir(ctx: Context, f: File, mime: String) {
        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".archivos", f)
        val i = Intent(Intent.ACTION_SEND).setType(mime).putExtra(Intent.EXTRA_STREAM, uri).putExtra(Intent.EXTRA_SUBJECT, f.name)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(i, "Guardar o enviar ${f.name}").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    // ------------------------------------------------------------------ texto, Markdown y Obsidian

    fun texto(ctx: Context, lista: List<Dia>): File {
        val f = File(carpeta(ctx), nombre(lista, "txt"))
        f.writeText(lista.sortedBy { it.fecha }.joinToString("\n\n\n") { d ->
            Fechas.titulo(d.fecha) + " (" + d.fecha + ")\n" + meta(d).let { if (it.isBlank()) "" else "$it\n" } + "\n" + d.nota.trim() +
                (if (d.mejor_momento.isNotBlank()) "\n\nLo mejor: ${d.mejor_momento}" else "") + (if (d.peor_momento.isNotBlank()) "\nLo peor: ${d.peor_momento}" else "")
        })
        return f
    }

    fun markdown(ctx: Context, lista: List<Dia>): File {
        val f = File(carpeta(ctx), nombre(lista, "md"))
        f.writeText("# Diario · Vestigium Mei\n\n" + lista.sortedBy { it.fecha }.joinToString("\n\n---\n\n") { d ->
            "## ${Fechas.titulo(d.fecha)}\n\n" + meta(d).let { if (it.isBlank()) "" else "*$it*\n\n" } + d.nota.trim() +
                (if (d.mejor_momento.isNotBlank()) "\n\n**Lo mejor:** ${d.mejor_momento}" else "") + (if (d.peor_momento.isNotBlank()) "\n\n**Lo peor:** ${d.peor_momento}" else "")
        } + "\n")
        return f
    }

    private fun yaml(s: String) = "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

    /** Un .md por día con sus datos arriba (propiedades de Obsidian), dentro de un .zip para soltar en la bóveda. */
    fun obsidian(ctx: Context, lista: List<Dia>): File {
        val f = File(carpeta(ctx), nombre(lista, "zip").replace("diario_", "obsidian_"))
        val eq = Repo.equivalencias()
        ZipOutputStream(f.outputStream()).use { z ->
            lista.forEach { d ->
                val personas = Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct()
                val temas = Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct()
                val prop = buildList {
                    add("fecha: ${d.fecha}")
                    d.valencia?.let { add("valencia: $it") }; d.activacion?.let { add("activacion: $it") }; d.dolor?.let { add("dolor: $it") }
                    d.sueno_calidad?.let { add("sueno_calidad: $it") }; d.prediccion_manana?.let { add("prediccion_manana: $it") }
                    Repo.salud[d.fecha]?.sueno_min?.let { add("sueno_min: $it") }
                    Catalogo.CONTEXTOS.filter { d.ctx[it.id] == true }.takeIf { it.isNotEmpty() }?.let { l -> add("contexto: [" + l.joinToString(", ") { it.id } + "]") }
                    if (d.hitos.isNotEmpty()) add("hitos: [" + d.hitos.joinToString(", ") + "]")
                    if (personas.isNotEmpty()) add("personas: [" + personas.joinToString(", ") { yaml(it) } + "]")
                    if (temas.isNotEmpty()) add("tags: [" + temas.joinToString(", ") { it.replace(' ', '_') } + "]")
                    if (d.mejor_momento.isNotBlank()) add("mejor: " + yaml(d.mejor_momento))
                    if (d.peor_momento.isNotBlank()) add("peor: " + yaml(d.peor_momento))
                }
                val md = "---\n" + prop.joinToString("\n") + "\n---\n\n# ${Fechas.titulo(d.fecha)}\n\n" + d.nota.trim() + "\n" +
                    (if (d.sueno_texto.isNotBlank()) "\n## Sueño\n\n${d.sueno_texto.trim()}\n" else "")
                z.putNextEntry(ZipEntry("Diario/${d.fecha.take(4)}/${d.fecha}.md")); z.write(md.toByteArray()); z.closeEntry()
            }
        }
        return f
    }

    // ------------------------------------------------------------------ fuentes y colores del tema

    internal class Fuentes(val titulo: Int, val texto: Int)
    internal fun fuentes(t: TemaId) = when (t) {
        TemaId.ROMANO -> Fuentes(R.font.cinzel_regular, R.font.plex_sans_regular)
        TemaId.CIBER -> Fuentes(R.font.sharetech_mono, R.font.plex_mono_regular)
        TemaId.HELENO -> Fuentes(R.font.garamond_medium, R.font.garamond_regular)
        TemaId.MEDIEVAL -> Fuentes(R.font.gotica_regular, R.font.garamond_regular)
        TemaId.DISTOPIA -> Fuentes(R.font.oswald_bold, R.font.plex_sans_regular)
        TemaId.REFUGIO -> Fuentes(R.font.vt323, R.font.plex_mono_regular)
        TemaId.ORDEN -> Fuentes(R.font.saira_stencil, R.font.plex_sans_regular)
        TemaId.LABORATORIO -> Fuentes(R.font.jost_medium, R.font.jost_regular)
        TemaId.COLONOS -> Fuentes(R.font.fell, R.font.garamond_regular)
        TemaId.NAPOLEON -> Fuentes(R.font.bodoni_titulo, R.font.bodoni_texto)
        TemaId.BOTANICO -> Fuentes(R.font.fraunces_titulo, R.font.garamond_regular)
        TemaId.NAVIDAD -> Fuentes(R.font.cinzel_deco_regular, R.font.garamond_regular)
        TemaId.SEDA -> Fuentes(R.font.almendra_regular, R.font.garamond_regular)
        TemaId.URSS -> Fuentes(R.font.russo, R.font.plex_sans_regular)
        TemaId.GOTICO -> Fuentes(R.font.gotica_textura, R.font.garamond_regular)
        TemaId.ESPACIO -> Fuentes(R.font.orbitron_bold, R.font.grotesk_regular)
        TemaId.ANDALUS -> Fuentes(R.font.kufi_medium, R.font.amiri_regular)
        TemaId.MONGOL -> Fuentes(R.font.philosopher_bold, R.font.plex_sans_regular)
        TemaId.RENACIMIENTO -> Fuentes(R.font.italianno, R.font.cormorant_regular)
        TemaId.REVOLUCION -> Fuentes(R.font.fell, R.font.cormorant_regular)
        TemaId.ANTIGUAS -> Fuentes(R.font.cinzel_semibold, R.font.garamond_regular)
    }

    private fun hex(c: Color) = "#%06X".format(c.toArgb() and 0xFFFFFF)

    // ------------------------------------------------------------------ HTML con el diseño del tema

    fun html(ctx: Context, lista: List<Dia>): File {
        val tema = Estilo.tema
        val e = esquemaDe(tema, tema.soloOscuro)
        val fu = fuentes(tema)
        fun b64(id: Int) = android.util.Base64.encodeToString(ctx.resources.openRawResource(id).use { it.readBytes() }, android.util.Base64.NO_WRAP)
        fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        val mayus = if (Estilo.mayusculas) "text-transform:uppercase;letter-spacing:.08em;" else ""
        val css = """
            @font-face{font-family:T;src:url(data:font/ttf;base64,${b64(fu.titulo)})}
            @font-face{font-family:B;src:url(data:font/ttf;base64,${b64(fu.texto)})}
            body{background:${hex(e.background)};color:${hex(e.onBackground)};font-family:B,serif;max-width:720px;margin:40px auto;padding:0 20px;line-height:1.6}
            h1{font-family:T,serif;color:${hex(e.primary)};font-weight:normal;$mayus}
            article{border-top:2px solid ${hex(e.primary)};padding:18px 0 28px}
            .fecha{font-family:T,serif;font-size:1.5em;margin:0}
            .dia{color:${hex(e.onSurfaceVariant)};font-size:.9em;margin:2px 0 8px}
            .meta{color:${hex(e.primary)};font-size:.85em;margin-bottom:12px}
            .extra{color:${hex(e.onSurfaceVariant)};font-style:italic}
            p{white-space:pre-wrap;margin:0 0 10px}
        """.trimIndent()
        val cuerpo = lista.sortedBy { it.fecha }.joinToString("\n") { d ->
            "<article><p class=\"fecha\">${esc(Estilo.fecha(d.fecha))}</p><div class=\"dia\">${esc(Fechas.titulo(d.fecha))}</div>" +
                (meta(d).takeIf { it.isNotBlank() }?.let { "<div class=\"meta\">${esc(it)}</div>" } ?: "") +
                "<p>${esc(d.nota.trim())}</p>" +
                (if (d.mejor_momento.isNotBlank()) "<p class=\"extra\">Lo mejor: ${esc(d.mejor_momento)}</p>" else "") +
                (if (d.peor_momento.isNotBlank()) "<p class=\"extra\">Lo peor: ${esc(d.peor_momento)}</p>" else "") + "</article>"
        }
        val f = File(carpeta(ctx), nombre(lista, "html"))
        f.writeText("<!doctype html><html lang=\"es\"><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">" +
            "<title>Diario · Vestigium Mei</title><style>$css</style><body><h1>${esc(Estilo.pantalla("Biblioteca del diario", Romano.latin("Biblioteca del diario")).first)}</h1>$cuerpo</body></html>")
        return f
    }

    // ------------------------------------------------------------------ PDF con el diseño del tema

    private const val ANCHO = 612; private const val ALTO = 792; private const val MARGEN = 60f

    /** PDF tamaño carta. Con una sola entrada agrega al final la frase de ese día. */
    fun pdf(ctx: Context, lista: List<Dia>): File {
        val tema = Estilo.tema
        val oscuro = tema.soloOscuro
        val e = esquemaDe(tema, oscuro)
        val fu = fuentes(tema)
        val tfTitulo = runCatching { ResourcesCompat.getFont(ctx, fu.titulo) }.getOrNull() ?: Typeface.SERIF
        val tfTexto = runCatching { ResourcesCompat.getFont(ctx, fu.texto) }.getOrNull() ?: Typeface.SERIF
        val fondo = e.background.toArgb(); val tinta = e.onBackground.toArgb(); val primario = e.primary.toArgb(); val tenue = e.onSurfaceVariant.toArgb()
        val doc = PdfDocument()
        val ancho = (ANCHO - 2 * MARGEN).toInt()
        var pagina: PdfDocument.Page? = null
        var num = 0
        var y = MARGEN
        val pie = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = 8f; color = tenue }

        fun nueva() {
            pagina?.let { doc.finishPage(it) }
            num++
            val p = doc.startPage(PdfDocument.PageInfo.Builder(ANCHO, ALTO, num).create())
            p.canvas.drawColor(fondo)
            p.canvas.drawText("Vestigium Mei", MARGEN, ALTO - 30f, pie)
            val n = Estilo.numeral(num)
            p.canvas.drawText(n, ANCHO - MARGEN - pie.measureText(n), ALTO - 30f, pie)
            pagina = p; y = MARGEN
        }
        fun lienzo() = pagina!!.canvas
        fun cabe(alto: Float) { if (y + alto > ALTO - MARGEN - 20) nueva() }
        fun layout(t: CharSequence, paint: TextPaint, interlineado: Float = 1.3f) =
            StaticLayout.Builder.obtain(t, 0, t.length, paint, ancho).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(0f, interlineado).setIncludePad(false).build()
        /** Dibuja un bloque de texto partiéndolo entre páginas si no cabe. */
        fun bloque(l: StaticLayout) {
            var linea = 0
            while (linea < l.lineCount) {
                val disponible = ALTO - MARGEN - 20 - y
                var fin = linea
                while (fin < l.lineCount && l.getLineBottom(fin) - l.getLineTop(linea) <= disponible) fin++
                if (fin == linea) { nueva(); continue }
                val c = lienzo()
                c.save(); c.translate(MARGEN, y - l.getLineTop(linea))
                c.clipRect(0f, l.getLineTop(linea).toFloat(), ancho.toFloat(), l.getLineBottom(fin - 1).toFloat())
                l.draw(c); c.restore()
                y += l.getLineBottom(fin - 1) - l.getLineTop(linea)
                linea = fin
                if (linea < l.lineCount) nueva()
            }
        }
        val pTitulo = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTitulo; textSize = 21f; color = tinta }
        val pEtiqueta = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTitulo; textSize = 9f; color = primario; letterSpacing = 0.12f }
        val pDia = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = 9.5f; color = tenue }
        val pMeta = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = 9f; color = primario }
        val pTexto = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = if (tema in setOf(TemaId.HELENO, TemaId.MEDIEVAL, TemaId.COLONOS, TemaId.NAPOLEON, TemaId.NAVIDAD, TemaId.SEDA, TemaId.BOTANICO, TemaId.GOTICO, TemaId.ANDALUS, TemaId.RENACIMIENTO)) 12.5f else 11f; color = tinta }
        val pExtra = TextPaint(pTexto).apply { textSize = pTexto.textSize * 0.88f; color = tenue; textSkewX = -0.15f }
        val linea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = primario; strokeWidth = 0.9f }
        val etiqueta = Estilo.seccion("Diario", Romano.latin("Diario")).first.let { if (Estilo.mayusculas) it.uppercase() else it }

        nueva()
        lista.sortedBy { it.fecha }.forEachIndexed { i, d ->
            if (i > 0) { cabe(170f); if (y > MARGEN) y += 26f }
            val c = lienzo()
            // Encabezado: rótulo del tema, fecha solemne, fecha en español y el emblema.
            c.drawText(etiqueta, MARGEN, y + 9f, pEtiqueta)
            val titulo = layout(Estilo.fecha(d.fecha), pTitulo, 1.1f)
            c.save(); c.translate(MARGEN, y + 16f); titulo.draw(c); c.restore()
            val emb = 38f
            c.save(); c.translate(ANCHO - MARGEN - emb, y)
            CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, androidx.compose.ui.graphics.Canvas(c), Size(emb, emb)) { dibujarEmblema(tema, 0.3f, e, oscuro) }
            c.restore()
            y += 16f + titulo.height + 4f
            c.drawText(Fechas.titulo(d.fecha), MARGEN, y + 9f, pDia); y += 18f
            // Filete con un rombo al centro.
            val cx = ANCHO / 2f
            c.drawLine(MARGEN, y, cx - 8f, y, linea); c.drawLine(cx + 8f, y, ANCHO - MARGEN, y, linea)
            val rombo = android.graphics.Path().apply { moveTo(cx, y - 4f); lineTo(cx + 4f, y); lineTo(cx, y + 4f); lineTo(cx - 4f, y); close() }
            c.drawPath(rombo, Paint(linea).apply { style = Paint.Style.FILL })
            y += 12f
            meta(d).takeIf { it.isNotBlank() }?.let { bloque(layout(it, pMeta)); y += 8f }
            // Las frases con ♥ de ese día, como epígrafe a la derecha (no son parte del texto del diario).
            Frases.deLaHoja(d.fecha).forEach { f ->
                val l = StaticLayout.Builder.obtain("«${f.texto}»\n— ${f.autor}", 0, "«${f.texto}»\n— ${f.autor}".length, pExtra, ancho).setAlignment(Layout.Alignment.ALIGN_OPPOSITE)
                    .setLineSpacing(0f, 1.25f).setIncludePad(false).build()
                bloque(l); y += 8f
            }
            // Texto: en el scriptorium, la primera letra va como capitular roja.
            val t = d.nota.trim()
            val cuerpo: CharSequence = if (tema == TemaId.MEDIEVAL && t.isNotEmpty()) SpannableString(t).apply {
                setSpan(ForegroundColorSpan(primario), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(RelativeSizeSpan(2.2f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(TypefaceSpan(tfTitulo), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else t
            bloque(layout(cuerpo, pTexto, if (tema == TemaId.CIBER || tema == TemaId.REFUGIO) 1.35f else 1.42f))
            if (d.mejor_momento.isNotBlank()) { y += 8f; bloque(layout("Lo mejor: " + d.mejor_momento, pExtra)) }
            if (d.peor_momento.isNotBlank()) { y += 4f; bloque(layout("Lo peor: " + d.peor_momento, pExtra)) }
            if (lista.size == 1) {
                val f = Frases.del(d.fecha, tema)
                y += 22f
                bloque(layout(Frases.rotulo(tema) + "\n" + (f.original?.let { "$it\n" } ?: "") + "«${f.texto}» — ${f.autor}", pExtra))
            }
        }
        pagina?.let { doc.finishPage(it) }
        val f = File(carpeta(ctx), nombre(lista, "pdf"))
        f.outputStream().use { doc.writeTo(it) }
        doc.close()
        return f
    }

    // ------------------------------------------------------------------ una carta en PDF

    /**
     * Una carta en PDF con el papel del tema: el lugar, la fecha y la hora arriba, a quién va, el texto, la firma
     * y el lacre con el emblema al final.
     */
    fun cartaPdf(ctx: Context, c: io.github.ruddy2003.vestigium.data.Carta): File {
        val tema = Estilo.tema
        val p = pagina(tema)
        val e = esquemaDe(tema, tema.soloOscuro)
        val fu = fuentes(tema)
        val tfTitulo = runCatching { ResourcesCompat.getFont(ctx, fu.titulo) }.getOrNull() ?: Typeface.SERIF
        val tfTexto = runCatching { ResourcesCompat.getFont(ctx, fu.texto) }.getOrNull() ?: Typeface.SERIF
        val fondo = p.color.toArgb(); val tinta = p.tinta.toArgb(); val tenue = p.tenue.toArgb(); val acento = p.acento.toArgb()
        val papel = p.fondo?.let { runCatching { android.graphics.BitmapFactory.decodeResource(ctx.resources, it) }.getOrNull() }
        val doc = PdfDocument()
        val margen = 64f
        val ancho = (ANCHO - 2 * margen).toInt()
        var pagina: PdfDocument.Page? = null
        var num = 0
        var y = margen
        val pie = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = 8f; color = tenue }
        val marco = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = acento; style = Paint.Style.STROKE; strokeWidth = 1.2f; alpha = 150 }
        fun nueva() {
            pagina?.let { doc.finishPage(it) }
            num++
            val pg = doc.startPage(PdfDocument.PageInfo.Builder(ANCHO, ALTO, num).create())
            val cv = pg.canvas
            cv.drawColor(fondo)
            // El papel del tema recortado para cubrir la hoja.
            papel?.let { b ->
                val k = maxOf(ANCHO.toFloat() / b.width, ALTO.toFloat() / b.height)
                val w = b.width * k; val h = b.height * k
                cv.drawBitmap(b, null, android.graphics.RectF((ANCHO - w) / 2, (ALTO - h) / 2, (ANCHO + w) / 2, (ALTO + h) / 2), Paint(Paint.FILTER_BITMAP_FLAG))
            }
            cv.drawRect(22f, 22f, ANCHO - 22f, ALTO - 22f, marco)
            cv.drawRect(27f, 27f, ANCHO - 27f, ALTO - 27f, Paint(marco).apply { strokeWidth = 0.6f; alpha = 90 })
            cv.drawText("Vestigium Mei · carta", margen, ALTO - 36f, pie)
            val n = Estilo.numeral(num)
            cv.drawText(n, ANCHO - margen - pie.measureText(n), ALTO - 36f, pie)
            pagina = pg; y = margen
        }
        fun lienzo() = pagina!!.canvas
        fun layout(t: CharSequence, paint: TextPaint, interlineado: Float = 1.3f, alineado: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL) =
            StaticLayout.Builder.obtain(t, 0, t.length, paint, ancho).setAlignment(alineado).setLineSpacing(0f, interlineado).setIncludePad(false).build()
        fun bloque(l: StaticLayout) {
            var linea = 0
            while (linea < l.lineCount) {
                val disponible = ALTO - margen - 24 - y
                var fin = linea
                while (fin < l.lineCount && l.getLineBottom(fin) - l.getLineTop(linea) <= disponible) fin++
                if (fin == linea) { nueva(); continue }
                val cv = lienzo()
                cv.save(); cv.translate(margen, y - l.getLineTop(linea))
                cv.clipRect(0f, l.getLineTop(linea).toFloat(), ancho.toFloat(), l.getLineBottom(fin - 1).toFloat())
                l.draw(cv); cv.restore()
                y += l.getLineBottom(fin - 1) - l.getLineTop(linea)
                linea = fin
                if (linea < l.lineCount) nueva()
            }
        }
        val pFecha = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = 10.5f; color = tinta; textSkewX = -0.12f }
        val pSolemne = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTitulo; textSize = 9f; color = acento; letterSpacing = 0.08f }
        val pPara = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTitulo; textSize = 17f; color = acento }
        val pNota = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = 9f; color = tenue }
        val clasica = tema in setOf(TemaId.HELENO, TemaId.MEDIEVAL, TemaId.COLONOS, TemaId.NAPOLEON, TemaId.NAVIDAD, TemaId.SEDA, TemaId.BOTANICO, TemaId.GOTICO, TemaId.ANDALUS, TemaId.RENACIMIENTO, TemaId.ROMANO)
        val pTexto = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = if (clasica) 12.5f else 11f; color = tinta }
        val pFirma = TextPaint(pTexto).apply { typeface = tfTitulo; textSize = pTexto.textSize * 1.35f; color = tinta }

        nueva()
        // Emblema a la izquierda; lugar, fecha y hora a la derecha.
        val emb = 40f
        lienzo().save(); lienzo().translate(margen, y)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, androidx.compose.ui.graphics.Canvas(lienzo()), Size(emb, emb)) { dibujarEmblema(tema, 0.3f, e, tema.soloOscuro) }
        lienzo().restore()
        val lugarFecha = listOfNotNull(c.lugar.ifBlank { null }, Fechas.larga(c.escrita)).joinToString(", ")
        val l1 = layout(lugarFecha + (if (c.hora.isNotBlank()) " · ${c.hora}" else ""), pFecha, 1.2f, Layout.Alignment.ALIGN_OPPOSITE)
        lienzo().save(); lienzo().translate(margen, y + 4f); l1.draw(lienzo()); lienzo().restore()
        val l2 = layout(Estilo.fecha(c.escrita), pSolemne, 1.2f, Layout.Alignment.ALIGN_OPPOSITE)
        lienzo().save(); lienzo().translate(margen, y + 8f + l1.height); l2.draw(lienzo()); lienzo().restore()
        y += maxOf(emb, 8f + l1.height + l2.height) + 28f
        // A quién va.
        bloque(layout(if (c.alFuturo) "Para mi yo del ${Fechas.larga(c.abrir_en)}" else "Para @${c.para}", pPara, 1.15f))
        if (c.alFuturo) { y += 2f; bloque(layout("Sellada el ${Fechas.larga(c.escrita)}" + (c.abierta_el?.let { " · abierta el ${Fechas.larga(it)}" } ?: ""), pNota)) }
        y += 16f
        val cx = ANCHO / 2f
        val linea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = acento; strokeWidth = 0.9f }
        lienzo().drawLine(margen, y, cx - 8f, y, linea); lienzo().drawLine(cx + 8f, y, ANCHO - margen, y, linea)
        lienzo().drawPath(android.graphics.Path().apply { moveTo(cx, y - 4f); lineTo(cx + 4f, y); lineTo(cx, y + 4f); lineTo(cx - 4f, y); close() }, Paint(linea).apply { style = Paint.Style.FILL })
        y += 20f
        // El texto, con capitular en el scriptorium y el gótico.
        val t = c.texto.trim()
        val cuerpo: CharSequence = if ((tema == TemaId.MEDIEVAL || tema == TemaId.GOTICO) && t.isNotEmpty()) SpannableString(t).apply {
            setSpan(ForegroundColorSpan(acento), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(2.2f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(TypefaceSpan(tfTitulo), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else t
        bloque(layout(cuerpo, pTexto, if (tema == TemaId.CIBER || tema == TemaId.REFUGIO) 1.35f else 1.45f))
        // Firma y lacre.
        y += 26f
        val nombre = Repo.config.perfil.nombre.ifBlank { "yo" }
        bloque(layout("— $nombre", pFirma, 1.1f, Layout.Alignment.ALIGN_OPPOSITE))
        if (y + 90f > ALTO - margen - 24) nueva()
        y += 18f
        val r = 30f
        val cv = lienzo()
        val lacre = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = acento }
        val borde = android.graphics.Path()
        for (i in 0..36) { val a = 2 * Math.PI * i / 36; val rr = r * (1f + 0.07f * kotlin.math.sin(a * 5 + 1).toFloat()); val px = cx + rr * kotlin.math.cos(a).toFloat(); val py = y + r + rr * kotlin.math.sin(a).toFloat(); if (i == 0) borde.moveTo(px, py) else borde.lineTo(px, py) }
        borde.close()
        cv.drawPath(borde, lacre)
        cv.drawCircle(cx, y + r, r * 0.78f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.BLACK; alpha = 45; style = Paint.Style.STROKE; strokeWidth = r * 0.08f })
        val k = r * 0.62f
        cv.save(); cv.translate(cx - k, y + r - k)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, androidx.compose.ui.graphics.Canvas(cv), Size(2 * k, 2 * k)) { dibujarEmblema(tema, 0.3f, e.copy(primary = Color.White.copy(alpha = 0.9f), secondary = Color.White.copy(alpha = 0.7f)), true) }
        cv.restore()
        y += 2 * r + 14f
        bloque(layout(colofon(tema), pNota.apply { color = acento }, 1.2f, Layout.Alignment.ALIGN_CENTER))
        pagina?.let { doc.finishPage(it) }
        val quien = if (c.alFuturo) "futuro" else Texto.slug(c.para)
        val f = File(carpeta(ctx), "carta_${c.escrita}_$quien.pdf")
        f.outputStream().use { doc.writeTo(it) }
        doc.close()
        return f
    }
}
