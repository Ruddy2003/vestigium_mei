package io.github.ruddy2003.vestigium.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
        TemaId.MEDIEVAL -> Fuentes(R.font.gotica_regular, R.font.cardo_regular)
        TemaId.DISTOPIA -> Fuentes(R.font.oswald_bold, R.font.barlow_regular)
        TemaId.REFUGIO -> Fuentes(R.font.vt323, R.font.courier_regular)
        TemaId.ORDEN -> Fuentes(R.font.saira_stencil, R.font.saira_regular)
        TemaId.LABORATORIO -> Fuentes(R.font.jost_medium, R.font.jost_regular)
        TemaId.COLONOS -> Fuentes(R.font.fell, R.font.oldstandard_regular)
        TemaId.NAPOLEON -> Fuentes(R.font.bodoni_titulo, R.font.bodoni_texto)
        TemaId.BOTANICO -> Fuentes(R.font.fraunces_titulo, R.font.crimson_regular)
        TemaId.NAVIDAD -> Fuentes(R.font.cinzel_deco_regular, R.font.calida_regular)
        TemaId.SEDA -> Fuentes(R.font.almendra_regular, R.font.alegreya_regular)
        TemaId.URSS -> Fuentes(R.font.russo, R.font.obrera_regular)
        TemaId.GOTICO -> Fuentes(R.font.gotica_textura, R.font.goudy_regular)
        TemaId.ESPACIO -> Fuentes(R.font.orbitron_bold, R.font.grotesk_regular)
        TemaId.ANDALUS -> Fuentes(R.font.kufi_medium, R.font.amiri_regular)
        TemaId.MONGOL -> Fuentes(R.font.philosopher_bold, R.font.fira_regular)
        TemaId.RENACIMIENTO -> Fuentes(R.font.italianno, R.font.cormorant_regular)
        // Los carteles de la Convención y la Baskerville de su siglo.
        TemaId.REVOLUCION -> Fuentes(R.font.convencion_titulo, R.font.ciudadana_regular)
        // La uncial de los caminos y el libro de crónicas.
        TemaId.ANTIGUAS -> Fuentes(R.font.camino_titulo, R.font.vollkorn_regular)
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
        """.trimIndent() + "\n" + cssTema(tema, e)
        val cuerpo = lista.sortedBy { it.fecha }.joinToString("\n") { d ->
            "<article><p class=\"fecha\">${esc(Estilo.fecha(d.fecha))}</p><div class=\"dia\">${esc(Fechas.titulo(d.fecha))}</div>" +
                (meta(d).takeIf { it.isNotBlank() }?.let { "<div class=\"meta\">${esc(it)}</div>" } ?: "") +
                "<p class=\"nota\">${esc(d.nota.trim())}</p>" +
                (if (d.mejor_momento.isNotBlank()) "<p class=\"extra\">Lo mejor: ${esc(d.mejor_momento)}</p>" else "") +
                (if (d.peor_momento.isNotBlank()) "<p class=\"extra\">Lo peor: ${esc(d.peor_momento)}</p>" else "") + "</article>"
        }
        val f = File(carpeta(ctx), nombre(lista, "html"))
        f.writeText("<!doctype html><html lang=\"es\"><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">" +
            "<title>Diario · Vestigium Mei</title><style>$css</style><body><h1>${esc(Estilo.pantalla("Biblioteca del diario", Romano.latin("Biblioteca del diario")).first)}</h1>$cuerpo</body></html>")
        return f
    }

    /** El CSS propio de cada tema para el HTML: el filete de cada entrada, el título y cómo empieza el texto. */
    private fun cssTema(t: TemaId, e: ColorScheme): String {
        val p = hex(e.primary); val s = hex(e.secondary); val v = hex(e.onSurfaceVariant); val b = hex(e.background); val tinta = hex(e.onBackground)
        fun capitular(tam: String, color: String) = ".nota::first-letter{font-family:T,serif;font-size:$tam;float:left;line-height:.8;color:$color;margin:4px 6px 0 0}"
        return when (t) {
            TemaId.ROMANO -> "h1{text-align:center;letter-spacing:.25em}article{border-top:3px double $p}.fecha{letter-spacing:.12em}.nota::first-line{font-variant:small-caps;letter-spacing:.04em}"
            TemaId.CIBER -> "article{border-top:1px dashed $p}.fecha::before{content:'> ';color:$p}.meta::before{content:'// '}.nota::first-letter{background:$p;color:$b}"
            TemaId.HELENO -> "h1{text-align:center}article{border-top:0}article::before{content:'';display:block;height:5px;margin-bottom:14px;border-top:1px solid $p;" +
                "border-bottom:1px solid $p;background:repeating-linear-gradient(90deg,$p 0 3px,transparent 3px 9px)}" + capitular("2.4em", tinta)
            TemaId.MEDIEVAL -> "article{border-top:2px solid $p;border-left:3px double $s;padding-left:14px}" + capitular("3.2em", p)
            TemaId.DISTOPIA -> "article{border-top:6px solid $v;border-bottom:2px solid $p}.fecha{text-transform:uppercase;letter-spacing:.06em}.meta{background:$p;color:$b;display:inline-block;padding:0 6px}"
            TemaId.REFUGIO -> "article{border:1px solid $p;border-radius:14px;padding:18px;margin-bottom:18px}.nota::first-letter{background:$p;color:$b;font-size:1.6em;padding:0 3px;margin-right:2px}"
            TemaId.ORDEN -> "article{border-top:3px solid $p;outline:1px solid $v;outline-offset:6px;margin:18px 0;padding:18px}.fecha{text-transform:uppercase;letter-spacing:.1em}" + capitular("2.6em", p)
            TemaId.LABORATORIO -> "article{border-top:1px solid $p;padding:18px;background-image:linear-gradient(${v}22 1px,transparent 1px),linear-gradient(90deg,${v}22 1px,transparent 1px);" +
                "background-size:100% 24px,24px 100%}.meta::before{content:'Obs. — '}"
            TemaId.COLONOS -> "article{border-top:3px solid $p}article::before{content:'';display:block;border-top:1px solid $p;margin:2px 0 14px}" + capitular("3em", tinta)
            TemaId.NAPOLEON -> "h1{text-align:center;letter-spacing:.15em}article{border-top:1px solid $p;border-bottom:1px solid $p;outline:1px solid $s;outline-offset:4px;padding:18px;margin-bottom:18px}" + capitular("3em", p)
            TemaId.BOTANICO -> "article{border-top:0}article::before{content:'';display:block;height:8px;margin-bottom:12px;background:radial-gradient(ellipse 5px 3px at 6px 4px,$p 98%,transparent) 0 0/18px 8px repeat-x}" + capitular("2.8em", p)
            TemaId.NAVIDAD -> "article{border-top:5px solid $p;border-image:repeating-linear-gradient(45deg,$p 0 8px,$b 8px 16px,$s 16px 24px) 5}" + capitular("2.8em", p)
            TemaId.SEDA -> "article{border-top:2px solid $p;border-right:6px solid $s;padding-right:14px}" + capitular("2.6em", p)
            TemaId.GOTICO -> "article{border-top:0;padding-top:24px;background:radial-gradient(circle at 9px 16px,transparent 7px,$p 7px 8px,transparent 8px) 0 0/18px 12px repeat-x}" + capitular("3.4em", p)
            TemaId.ESPACIO -> "article{border:1px solid $p;border-radius:16px;padding:18px;margin-bottom:18px}.fecha{text-transform:uppercase;letter-spacing:.12em}"
            TemaId.ANDALUS -> "article{border-top:0}article::before{content:'';display:block;height:10px;margin-bottom:12px;" +
                "background:conic-gradient(from 45deg,$p 25%,transparent 0 50%,$p 0 75%,transparent 0) 0 0/10px 10px repeat-x}" + capitular("2.6em", p)
            TemaId.MONGOL -> "article{border:2px dashed $s;border-radius:10px;padding:18px;margin-bottom:18px}" + capitular("2.4em", p)
            TemaId.RENACIMIENTO -> "article{border-top:1px solid $v}article::before{content:'';float:right;width:60px;height:60px;border:1px dashed $v;border-radius:50%;margin:0 0 6px 10px}" + capitular("3.6em", p)
            TemaId.URSS -> "article{border-top:0;border-left:10px solid $p;padding-left:16px}.fecha{text-transform:uppercase;letter-spacing:.04em}.nota::first-line{font-weight:bold}"
            TemaId.REVOLUCION -> "article{border-top:6px solid;border-image:linear-gradient(90deg,#1F3A6E 0 33%,$b 33% 66%,#B22234 66%) 6}" + capitular("3em", p)
            TemaId.ANTIGUAS -> "article{border:2px solid $p;padding:18px;margin-bottom:18px;background:linear-gradient(135deg,$s 10px,transparent 10px),linear-gradient(225deg,$s 10px,transparent 10px)," +
                "linear-gradient(315deg,$s 10px,transparent 10px),linear-gradient(45deg,$s 10px,transparent 10px)}" + capitular("2.8em", p)
        }
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
        val med = medidas(tema, carta = false)
        val pTexto = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = med.tam; color = tinta }
        val pExtra = TextPaint(pTexto).apply { textSize = pTexto.textSize * 0.88f; color = tenue; textSkewX = -0.15f }
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
            // El filete del tema.
            filete(tema, c, MARGEN, ANCHO - MARGEN, y, primario, tinta, fondo, tfTitulo)
            y += 12f
            meta(d).takeIf { it.isNotBlank() }?.let { bloque(layout(it, pMeta)); y += 8f }
            // Las frases con ♥ de ese día, como epígrafe a la derecha (no son parte del texto del diario).
            Frases.deLaHoja(d.fecha).forEach { f ->
                val l = StaticLayout.Builder.obtain("«${f.texto}»\n— ${f.autor}", 0, "«${f.texto}»\n— ${f.autor}".length, pExtra, ancho).setAlignment(Layout.Alignment.ALIGN_OPPOSITE)
                    .setLineSpacing(0f, 1.25f).setIncludePad(false).build()
                bloque(l); y += 8f
            }
            // Texto: empieza con la inicial del tema (la misma que en la carta).
            val cuerpo = conInicial(ctx, tema, d.nota.trim(), primario, fondo, tinta)
            bloque(layout(cuerpo, pTexto, med.interlinea))
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

    // ------------------------------------------------------------------ piezas de los PDF, propias de cada tema

    /** El tamaño y el interlineado del texto en los PDF de cada tema ([carta]: el de la carta, un poco más aireado). */
    private class Medidas(val tam: Float, val interlinea: Float)

    private fun medidas(t: TemaId, carta: Boolean): Medidas {
        val aire = if (carta) 1.45f else 1.42f
        return when (t) {
            TemaId.ROMANO -> Medidas(if (carta) 12.5f else 11f, aire)
            TemaId.CIBER -> Medidas(11f, 1.35f)
            TemaId.HELENO -> Medidas(12.5f, aire)
            TemaId.MEDIEVAL -> Medidas(12.5f, aire)
            TemaId.DISTOPIA -> Medidas(11f, aire)
            TemaId.REFUGIO -> Medidas(11f, 1.35f)
            TemaId.ORDEN -> Medidas(11f, aire)
            TemaId.LABORATORIO -> Medidas(11f, aire)
            TemaId.COLONOS -> Medidas(12.5f, aire)
            TemaId.NAPOLEON -> Medidas(12.5f, aire)
            TemaId.BOTANICO -> Medidas(12.5f, aire)
            TemaId.NAVIDAD -> Medidas(12.5f, aire)
            TemaId.SEDA -> Medidas(12.5f, aire)
            TemaId.GOTICO -> Medidas(12.5f, aire)
            TemaId.ESPACIO -> Medidas(11f, aire)
            TemaId.ANDALUS -> Medidas(12.5f, aire)
            TemaId.MONGOL -> Medidas(11f, aire)
            TemaId.RENACIMIENTO -> Medidas(12.5f, aire)
            TemaId.URSS -> Medidas(11f, aire)
            TemaId.REVOLUCION -> Medidas(11f, aire)
            TemaId.ANTIGUAS -> Medidas(11f, aire)
        }
    }

    /**
     * Cómo empieza el texto en los PDF de cada tema (el del diario y el de la carta, con el mismo criterio): la primera letra
     * (o la primera palabra, si [palabra]) en la letra [fuente], [escala] veces más grande, en el color de acento (o en la
     * tinta, si [tinta]); [invertida] la pone en el color del papel sobre un fondo de acento, y [prefijo] va antes, en acento.
     */
    private class Inicial(val fuente: Int, val escala: Float, val tinta: Boolean = false, val palabra: Boolean = false, val mayusculas: Boolean = false,
                          val invertida: Boolean = false, val prefijo: String = "")

    private fun inicial(t: TemaId): Inicial = when (t) {
        // La primera palabra en capitales lapidarias, como en una inscripción.
        TemaId.ROMANO -> Inicial(R.font.cinzel_semibold, 1.25f, palabra = true, mayusculas = true)
        // El indicador de la consola y la primera letra en video inverso, como el cursor.
        TemaId.CIBER -> Inicial(R.font.sharetech_mono, 1f, invertida = true, prefijo = "> ")
        // La letra que sale al margen, en negro, como en los papiros griegos.
        TemaId.HELENO -> Inicial(R.font.garamond_semibold, 2.3f, tinta = true)
        // La capitular roja del copista.
        TemaId.MEDIEVAL -> Inicial(R.font.gotica_regular, 2.2f)
        // La primera palabra en mayúsculas sobre la franja roja del formulario.
        TemaId.DISTOPIA -> Inicial(R.font.oswald_bold, 1f, palabra = true, mayusculas = true, invertida = true)
        // Una letra grande de fósforo en video inverso.
        TemaId.REFUGIO -> Inicial(R.font.vt323, 1.9f, invertida = true)
        // La letra de estarcido.
        TemaId.ORDEN -> Inicial(R.font.saira_stencil, 2.2f, mayusculas = true)
        // «Obs. —» y la primera palabra en negrita, como en un cuaderno de laboratorio.
        TemaId.LABORATORIO -> Inicial(R.font.jost_semibold, 1f, palabra = true, prefijo = "Obs. — ")
        // La letra de grabado de la gaceta, en negro de imprenta.
        TemaId.COLONOS -> Inicial(R.font.fell, 2.6f, tinta = true)
        TemaId.NAPOLEON -> Inicial(R.font.bodoni_titulo, 2.5f)
        TemaId.BOTANICO -> Inicial(R.font.fraunces_negrita, 2.3f)
        TemaId.NAVIDAD -> Inicial(R.font.cinzel_deco_bold, 2.3f)
        TemaId.SEDA -> Inicial(R.font.almendra_bold, 2.2f)
        // La capitular de los libros de horas, en letra textura.
        TemaId.GOTICO -> Inicial(R.font.gotica_textura, 2.7f)
        // La primera palabra en mayúsculas de la sala de control.
        TemaId.ESPACIO -> Inicial(R.font.orbitron_bold, 1.1f, palabra = true, mayusculas = true)
        TemaId.ANDALUS -> Inicial(R.font.kufi_bold, 2.2f)
        TemaId.MONGOL -> Inicial(R.font.philosopher_bold, 2.1f)
        // La inicial a mano, en sanguina.
        TemaId.RENACIMIENTO -> Inicial(R.font.italianno, 3.2f)
        // La primera palabra en mayúsculas del cartel.
        TemaId.URSS -> Inicial(R.font.russo, 1.15f, palabra = true, mayusculas = true)
        TemaId.REVOLUCION -> Inicial(R.font.cormorant_medium, 2.6f)
        TemaId.ANTIGUAS -> Inicial(R.font.cinzel_semibold, 2.5f)
    }

    /** El [texto] con la inicial del tema ([acento], [fondo] y [tinta] son los colores de la hoja). */
    private fun conInicial(ctx: Context, t: TemaId, texto: String, acento: Int, fondo: Int, tinta: Int): CharSequence {
        if (texto.isEmpty()) return texto
        val i = inicial(t)
        val fin = if (i.palabra) texto.indexOfFirst { it.isWhitespace() }.let { if (it < 0) texto.length else it } else Character.charCount(texto.codePointAt(0))
        val cabeza = texto.substring(0, fin).let { if (i.mayusculas) it.uppercase() else it }
        val s = SpannableStringBuilder(i.prefijo).append(cabeza).append(texto.substring(fin))
        val a = i.prefijo.length; val b = a + cabeza.length
        val modo = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        if (a > 0) s.setSpan(ForegroundColorSpan(acento), 0, a, modo)
        s.setSpan(ForegroundColorSpan(if (i.invertida) fondo else if (i.tinta) tinta else acento), a, b, modo)
        if (i.invertida) s.setSpan(BackgroundColorSpan(acento), a, b, modo)
        if (i.escala != 1f) s.setSpan(RelativeSizeSpan(i.escala), a, b, modo)
        runCatching { ResourcesCompat.getFont(ctx, i.fuente) }.getOrNull()?.let { s.setSpan(TypefaceSpan(it), a, b, modo) }
        return s
    }

    /** Un meandro griego entre [x0] y [x1], centrado en [y], de módulo [u]. */
    private fun meandro(cv: android.graphics.Canvas, x0: Float, x1: Float, y: Float, u: Float, p: Paint) {
        val ruta = android.graphics.Path(); var x = x0
        while (x + 3 * u <= x1) { ruta.moveTo(x, y + u); ruta.lineTo(x, y - u); ruta.lineTo(x + 2 * u, y - u); ruta.lineTo(x + 2 * u, y); ruta.lineTo(x + u, y); x += 3 * u }
        cv.drawPath(ruta, p)
    }

    /** Una estrella de [puntas] puntas centrada en ([cx], [cy]). */
    private fun estrellaPdf(cx: Float, cy: Float, R: Float, r: Float, puntas: Int = 5) = android.graphics.Path().apply {
        for (k in 0 until puntas * 2) {
            val a = -Math.PI / 2 + k * Math.PI / puntas; val rr = if (k % 2 == 0) R else r
            val x = cx + (rr * cos(a)).toFloat(); val y = cy + (rr * sin(a)).toFloat()
            if (k == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /** El filete bajo el encabezado de cada día del diario y de la carta: una línea con el adorno propio del tema. */
    private fun filete(t: TemaId, cv: android.graphics.Canvas, x0: Float, x1: Float, y: Float, acento: Int, tinta: Int, fondo: Int, tfTitulo: Typeface) {
        val cx = (x0 + x1) / 2
        val linea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = acento; strokeWidth = 0.9f }
        val trazo = Paint(linea).apply { style = Paint.Style.STROKE }
        val relleno = Paint(linea).apply { style = Paint.Style.FILL }
        fun lados(hueco: Float) { cv.drawLine(x0, y, cx - hueco, y, linea); cv.drawLine(cx + hueco, y, x1, y, linea) }
        fun ruta(f: android.graphics.Path.() -> Unit) = android.graphics.Path().apply(f)
        when (t) {
            TemaId.ROMANO -> {
                // La interpunción triangular de las inscripciones, entre dos puntos.
                lados(12f)
                cv.drawPath(ruta { moveTo(cx - 4f, y - 3f); lineTo(cx + 4f, y - 3f); lineTo(cx, y + 3.5f); close() }, relleno)
                cv.drawCircle(cx - 8f, y, 1.3f, relleno); cv.drawCircle(cx + 8f, y, 1.3f, relleno)
            }
            TemaId.CIBER -> {
                // Una línea discontinua y el cursor al principio.
                cv.drawPath(ruta { moveTo(x0 + 9f, y); lineTo(x1, y) }, Paint(trazo).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(4f, 3f), 0f) })
                cv.drawRect(x0, y - 3f, x0 + 5f, y + 3f, relleno)
            }
            TemaId.HELENO -> {
                // El meandro entre dos filetes.
                val u = 2.6f; val fino = Paint(linea).apply { strokeWidth = 0.6f }
                meandro(cv, x0, x1, y, u, Paint(trazo).apply { strokeWidth = 0.8f })
                cv.drawLine(x0, y - 2 * u, x1, y - 2 * u, fino); cv.drawLine(x0, y + 2 * u, x1, y + 2 * u, fino)
            }
            TemaId.MEDIEVAL -> {
                // Un cuadrifolio rojo con el botón de tinta.
                lados(10f)
                listOf(-1f to -1f, 1f to -1f, -1f to 1f, 1f to 1f).forEach { (a, b) -> cv.drawCircle(cx + a * 2.4f, y + b * 2.4f, 2.2f, relleno) }
                cv.drawCircle(cx, y, 1.2f, Paint(relleno).apply { color = tinta })
            }
            TemaId.DISTOPIA -> {
                // La barra negra del formulario y la línea roja de debajo.
                cv.drawLine(x0, y - 1.5f, x1, y - 1.5f, Paint(linea).apply { color = tinta; strokeWidth = 2.4f })
                cv.drawLine(x0, y + 2f, x1, y + 2f, Paint(linea).apply { strokeWidth = 0.8f })
            }
            TemaId.REFUGIO -> {
                // Una fila de puntos y el átomo al centro.
                var x = x0; while (x <= x1) { if (abs(x - cx) > 10f) cv.drawCircle(x, y, 0.9f, relleno); x += 5f }
                val orbita = Paint(trazo).apply { strokeWidth = 0.7f }
                for (j in 0 until 3) { cv.save(); cv.rotate(j * 60f, cx, y); cv.drawOval(cx - 7f, y - 2.4f, cx + 7f, y + 2.4f, orbita); cv.restore() }
                cv.drawCircle(cx, y, 1.3f, relleno)
            }
            TemaId.ORDEN -> {
                // El engrane.
                lados(10f)
                cv.drawCircle(cx, y, 4.2f, Paint(trazo).apply { strokeWidth = 1.2f })
                val diente = Paint(linea).apply { strokeWidth = 1.6f }
                for (j in 0 until 8) { val a = j * Math.PI / 4; cv.drawLine(cx + 4.2f * cos(a).toFloat(), y + 4.2f * sin(a).toFloat(), cx + 6.4f * cos(a).toFloat(), y + 6.4f * sin(a).toFloat(), diente) }
                cv.drawCircle(cx, y, 1.2f, relleno)
            }
            TemaId.LABORATORIO -> {
                // Una regla con sus marcas.
                cv.drawLine(x0, y + 2f, x1, y + 2f, linea)
                val marca = Paint(linea).apply { strokeWidth = 0.6f }
                var x = x0; var k = 0; while (x <= x1) { cv.drawLine(x, y + 2f, x, y + 2f - (if (k % 5 == 0) 5f else 2.5f), marca); x += 6f; k++ }
            }
            TemaId.COLONOS -> {
                // La raya de imprenta: gruesa y fina.
                cv.drawLine(x0, y - 1.5f, x1, y - 1.5f, Paint(linea).apply { strokeWidth = 2f })
                cv.drawLine(x0, y + 1.5f, x1, y + 1.5f, Paint(linea).apply { strokeWidth = 0.6f })
            }
            TemaId.NAPOLEON -> {
                // La N imperial en su círculo.
                lados(13f)
                cv.drawCircle(cx, y, 7f, Paint(trazo).apply { strokeWidth = 0.9f })
                cv.drawText("N", cx, y + 3.2f, TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTitulo; textSize = 9f; color = acento; textAlign = Paint.Align.CENTER })
            }
            TemaId.BOTANICO -> {
                // Un tallo que ondula con sus hojas.
                cv.drawPath(ruta { moveTo(x0, y); var x = x0; var s = 1f; while (x < x1) { quadTo(x + 6f, y - s * 3f, minOf(x + 12f, x1), y); x += 12f; s = -s } }, Paint(trazo).apply { strokeWidth = 0.8f })
                var x = x0 + 6f; var s = 1f
                while (x < x1) { cv.save(); cv.rotate(-35f * s, x, y - s * 2f); cv.drawOval(x - 3f, y - s * 2f - 1.2f, x + 3f, y - s * 2f + 1.2f, relleno); cv.restore(); x += 12f; s = -s }
            }
            TemaId.NAVIDAD -> {
                // Una ramita de acebo: dos hojas verdes y tres bayas.
                lados(13f)
                val verde = Paint(relleno).apply { color = 0xFF1B5E20.toInt() }
                cv.save(); cv.rotate(25f, cx, y); cv.drawOval(cx - 10f, y - 2f, cx - 1f, y + 2f, verde); cv.restore()
                cv.save(); cv.rotate(-25f, cx, y); cv.drawOval(cx + 1f, y - 2f, cx + 10f, y + 2f, verde); cv.restore()
                listOf(-2f to 0f, 2f to 0f, 0f to -2.6f).forEach { (dx, dy) -> cv.drawCircle(cx + dx, y + dy, 1.6f, relleno) }
            }
            TemaId.SEDA -> {
                // La moneda de agujero cuadrado.
                lados(9f)
                cv.drawCircle(cx, y, 5f, relleno)
                cv.drawRect(cx - 1.8f, y - 1.8f, cx + 1.8f, y + 1.8f, Paint(relleno).apply { color = fondo })
            }
            TemaId.GOTICO -> {
                // Un arco ojival.
                lados(9f)
                cv.drawPath(ruta { moveTo(cx - 5f, y + 4f); lineTo(cx - 5f, y - 1f); quadTo(cx - 5f, y - 6f, cx, y - 8.5f); quadTo(cx + 5f, y - 6f, cx + 5f, y - 1f); lineTo(cx + 5f, y + 4f) }, Paint(trazo).apply { strokeWidth = 1f })
                cv.drawCircle(cx, y - 1.5f, 1.4f, relleno)
            }
            TemaId.ESPACIO -> {
                // Un planeta con su anillo.
                lados(12f)
                cv.drawCircle(cx, y, 3.4f, relleno)
                cv.save(); cv.rotate(-20f, cx, y); cv.drawOval(cx - 8.5f, y - 2.4f, cx + 8.5f, y + 2.4f, Paint(trazo).apply { strokeWidth = 0.8f }); cv.restore()
            }
            TemaId.ANDALUS -> {
                // La estrella de ocho puntas de dos cuadrados.
                lados(10f)
                cv.drawRect(cx - 4f, y - 4f, cx + 4f, y + 4f, relleno)
                cv.save(); cv.rotate(45f, cx, y); cv.drawRect(cx - 4f, y - 4f, cx + 4f, y + 4f, relleno); cv.restore()
                cv.drawCircle(cx, y, 1.6f, Paint(relleno).apply { color = fondo })
            }
            TemaId.MONGOL -> {
                // El sol, la luna y la llama, uno sobre otro.
                lados(10f)
                cv.drawCircle(cx, y + 2f, 2.6f, relleno)
                cv.drawCircle(cx, y - 4.5f, 3f, relleno); cv.drawCircle(cx, y - 5.8f, 2.6f, Paint(relleno).apply { color = fondo })
                cv.drawCircle(cx, y - 9f, 1f, relleno)
            }
            TemaId.RENACIMIENTO -> {
                // Construcción: el círculo con su cuadrado inscrito.
                lados(10f)
                val fino = Paint(trazo).apply { strokeWidth = 0.6f }
                cv.drawCircle(cx, y, 6f, fino); cv.drawRect(cx - 4.24f, y - 4.24f, cx + 4.24f, y + 4.24f, fino); cv.drawCircle(cx, y, 0.9f, relleno)
            }
            TemaId.URSS -> {
                // La barra roja gruesa, la negra fina y la estrella donde se juntan.
                cv.drawLine(x0, y, cx - 7f, y, Paint(linea).apply { strokeWidth = 3.2f })
                cv.drawLine(cx + 7f, y, x1, y, Paint(linea).apply { color = tinta; strokeWidth = 1.1f })
                cv.drawPath(estrellaPdf(cx, y, 5.5f, 2.2f), relleno)
            }
            TemaId.REVOLUCION -> {
                // Azul, blanco (una línea fina) y rojo.
                val tercio = (x1 - x0) / 3
                cv.drawLine(x0, y, x0 + tercio - 2f, y, Paint(linea).apply { color = 0xFF1F3A6E.toInt(); strokeWidth = 2.2f })
                cv.drawLine(x0 + tercio + 2f, y, x0 + 2 * tercio - 2f, y, Paint(linea).apply { color = tinta; strokeWidth = 0.6f })
                cv.drawLine(x0 + 2 * tercio + 2f, y, x1, y, Paint(linea).apply { color = 0xFFB22234.toInt(); strokeWidth = 2.2f })
            }
            TemaId.ANTIGUAS -> {
                // Tres runas inventadas al centro.
                lados(16f)
                val runa = Paint(linea).apply { strokeWidth = 1.1f; strokeCap = Paint.Cap.ROUND }
                listOf(-9f, 0f, 9f).forEachIndexed { i, dx ->
                    val x = cx + dx
                    cv.drawLine(x, y - 4f, x, y + 4f, runa)
                    if (i == 0) cv.drawLine(x, y - 4f, x + 3f, y - 1.5f, runa)
                    if (i == 1) { cv.drawLine(x - 3f, y - 2f, x, y, runa); cv.drawLine(x, y, x + 3f, y - 2f, runa) }
                    if (i == 2) cv.drawLine(x - 3f, y + 1.5f, x + 3f, y - 1.5f, runa)
                }
            }
        }
    }

    /** El marco de cada hoja de la carta en PDF, propio de cada tema. */
    private fun marco(t: TemaId, cv: android.graphics.Canvas, acento: Int, tinta: Int) {
        val A = ANCHO.toFloat(); val B = ALTO.toFloat()
        val trazo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = acento; style = Paint.Style.STROKE; strokeWidth = 1.2f; alpha = 150 }
        val fino = Paint(trazo).apply { strokeWidth = 0.6f; alpha = 90 }
        val relleno = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = acento; style = Paint.Style.FILL; alpha = 170 }
        fun rect(m: Float, p: Paint) = cv.drawRect(m, m, A - m, B - m, p)
        val esquinas = listOf(22f to 22f, A - 22f to 22f, 22f to B - 22f, A - 22f to B - 22f)
        /** Hacia dónde queda el centro de la hoja desde una esquina: 1 o -1 en cada eje. */
        fun haciaDentro(x: Float, y: Float) = (if (x < A / 2) 1f else -1f) to (if (y < B / 2) 1f else -1f)
        when (t) {
            TemaId.ROMANO -> {
                // Doble filete con un cuadradito en cada esquina.
                rect(22f, trazo); rect(27f, fino)
                esquinas.forEach { (x, y) -> cv.drawRect(x - 3f, y - 3f, x + 3f, y + 3f, relleno) }
            }
            // Solo las escuadras de las esquinas, como en una pantalla.
            TemaId.CIBER -> esquinas.forEach { (x, y) ->
                val (sx, sy) = haciaDentro(x, y); val p = Paint(trazo).apply { strokeWidth = 1.4f }
                cv.drawLine(x, y, x + sx * 40f, y, p); cv.drawLine(x, y, x, y + sy * 40f, p)
            }
            TemaId.HELENO -> {
                // Un filete y el meandro arriba y abajo.
                rect(22f, trazo)
                listOf(30f, B - 29f).forEach { y -> meandro(cv, 34f, A - 34f, y, 3f, Paint(trazo).apply { strokeWidth = 0.8f }) }
            }
            TemaId.MEDIEVAL -> {
                // Rojo grueso por fuera y azul fino por dentro.
                rect(22f, Paint(trazo).apply { strokeWidth = 2.4f; alpha = 200 })
                rect(27f, Paint(fino).apply { color = 0xFF1F3A93.toInt(); alpha = 160 })
            }
            TemaId.DISTOPIA -> {
                // Marco negro, la franja roja de arriba y la casilla del folio.
                rect(22f, Paint(trazo).apply { color = tinta; strokeWidth = 1.6f; alpha = 220 })
                cv.drawRect(22f, 22f, A - 22f, 30f, Paint(relleno).apply { alpha = 220 })
                cv.drawRect(A - 130f, 36f, A - 34f, 54f, Paint(fino).apply { color = tinta; alpha = 160 })
            }
            TemaId.REFUGIO -> {
                // Esquinas redondas, como el cristal de una pantalla.
                cv.drawRoundRect(22f, 22f, A - 22f, B - 22f, 18f, 18f, Paint(trazo).apply { strokeWidth = 1.4f })
                cv.drawRoundRect(28f, 28f, A - 28f, B - 28f, 13f, 13f, fino)
            }
            TemaId.ORDEN -> {
                // Un marco remachado.
                rect(22f, Paint(trazo).apply { strokeWidth = 1.4f })
                var x = 22f; while (x <= A - 22f) { cv.drawCircle(x, 22f, 1.8f, relleno); cv.drawCircle(x, B - 22f, 1.8f, relleno); x += 24f }
                var y = 46f; while (y <= B - 46f) { cv.drawCircle(22f, y, 1.8f, relleno); cv.drawCircle(A - 22f, y, 1.8f, relleno); y += 24f }
            }
            TemaId.LABORATORIO -> {
                // Un filete fino con las marcas de una regla en los bordes de arriba y de la izquierda.
                rect(22f, Paint(trazo).apply { strokeWidth = 0.8f })
                var x = 22f; var k = 0; while (x <= A - 22f) { cv.drawLine(x, 22f, x, 22f + (if (k % 5 == 0) 6f else 3f), fino); x += 10f; k++ }
                var y = 22f; k = 0; while (y <= B - 22f) { cv.drawLine(22f, y, 22f + (if (k % 5 == 0) 6f else 3f), y, fino); y += 10f; k++ }
            }
            TemaId.COLONOS -> {
                // La raya de imprenta: gruesa por fuera, fina por dentro.
                rect(22f, Paint(trazo).apply { strokeWidth = 2.4f; alpha = 190 }); rect(27f, fino)
            }
            TemaId.NAPOLEON -> {
                // Doble filete y un botón en cada esquina.
                rect(24f, trazo); rect(28f, fino)
                esquinas.forEach { (x, y) -> val (sx, sy) = haciaDentro(x, y); cv.drawCircle(x + sx * 2f, y + sy * 2f, 4.5f, relleno); cv.drawCircle(x + sx * 2f, y + sy * 2f, 7.5f, fino) }
            }
            TemaId.BOTANICO -> {
                // Un filete redondeado con dos hojas en cada esquina.
                cv.drawRoundRect(24f, 24f, A - 24f, B - 24f, 10f, 10f, fino)
                esquinas.forEach { (x, y) ->
                    val (sx, sy) = haciaDentro(x, y)
                    val base = Math.toDegrees(Math.atan2(sy.toDouble(), sx.toDouble())).toFloat()
                    listOf(-20f, 20f).forEach { g -> cv.save(); cv.rotate(base + g, x, y); cv.drawOval(x + 2f, y - 2.5f, x + 16f, y + 2.5f, relleno); cv.restore() }
                }
            }
            TemaId.NAVIDAD -> {
                // Bastón de caramelo: el mismo marco a trazos rojos y verdes alternados.
                val d = 8f
                rect(22f, Paint(trazo).apply { strokeWidth = 2.2f; alpha = 220; pathEffect = android.graphics.DashPathEffect(floatArrayOf(d, d), 0f) })
                rect(22f, Paint(trazo).apply { color = 0xFF1B5E20.toInt(); strokeWidth = 2.2f; alpha = 220; pathEffect = android.graphics.DashPathEffect(floatArrayOf(d, d), d) })
            }
            TemaId.SEDA -> {
                // Esquinas escalonadas hacia dentro y un filete fino.
                val m = 24f; val n = 12f
                val ruta = android.graphics.Path().apply {
                    moveTo(m + n, m); lineTo(A - m - n, m); lineTo(A - m - n, m + n); lineTo(A - m, m + n); lineTo(A - m, B - m - n); lineTo(A - m - n, B - m - n)
                    lineTo(A - m - n, B - m); lineTo(m + n, B - m); lineTo(m + n, B - m - n); lineTo(m, B - m - n); lineTo(m, m + n); lineTo(m + n, m + n); close()
                }
                cv.drawPath(ruta, Paint(trazo).apply { strokeWidth = 1.4f }); rect(31f, fino)
            }
            TemaId.GOTICO -> {
                // Un filete y una arquería de arcos ojivales arriba.
                rect(22f, trazo)
                val ruta = android.graphics.Path(); var x = 40f; val w = 18f
                while (x + w <= A - 40f) { ruta.moveTo(x, 44f); ruta.lineTo(x, 36f); ruta.quadTo(x, 28f, x + w / 2, 26f); ruta.quadTo(x + w, 28f, x + w, 36f); ruta.lineTo(x + w, 44f); x += w + 4f }
                cv.drawPath(ruta, fino)
            }
            TemaId.ESPACIO -> {
                // Esquinas redondas y una mira en el centro de cada lado.
                cv.drawRoundRect(22f, 22f, A - 22f, B - 22f, 16f, 16f, trazo)
                listOf(A / 2 to 22f, A / 2 to B - 22f, 22f to B / 2, A - 22f to B / 2).forEach { (x, y) -> cv.drawCircle(x, y, 4f, fino); cv.drawLine(x - 8f, y, x + 8f, y, fino); cv.drawLine(x, y - 8f, x, y + 8f, fino) }
            }
            TemaId.ANDALUS -> {
                // Doble filete y una estrella de ocho puntas en cada esquina.
                rect(24f, trazo); rect(29f, fino)
                esquinas.forEach { (x, y) -> val (sx, sy) = haciaDentro(x, y); cv.drawPath(estrellaPdf(x + sx * 2f, y + sy * 2f, 8f, 5f, 8), relleno) }
            }
            TemaId.MONGOL -> {
                // Doble filete con rombos en las esquinas y al centro de arriba y de abajo.
                rect(22f, trazo); rect(26f, fino)
                (esquinas + listOf(A / 2 to 22f, A / 2 to B - 22f)).forEach { (x, y) -> cv.drawPath(android.graphics.Path().apply { moveTo(x, y - 5f); lineTo(x + 5f, y); lineTo(x, y + 5f); lineTo(x - 5f, y); close() }, relleno) }
            }
            TemaId.RENACIMIENTO -> {
                // Un filete de sepia y los arcos de compás en las esquinas.
                rect(24f, Paint(fino).apply { alpha = 140 })
                val raya = Paint(fino).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(3f, 3f), 0f) }
                esquinas.forEach { (x, y) -> cv.drawCircle(x, y, 30f, raya) }
            }
            TemaId.URSS -> {
                // La barra roja a la izquierda, el filete negro y el cuadrado negro arriba a la derecha.
                cv.drawRect(22f, 22f, 34f, B - 22f, Paint(relleno).apply { alpha = 220 })
                rect(22f, Paint(trazo).apply { color = tinta; strokeWidth = 0.8f; alpha = 200 })
                cv.drawRect(A - 46f, 22f, A - 22f, 46f, Paint(relleno).apply { color = tinta; alpha = 220 })
            }
            TemaId.REVOLUCION -> {
                // Azul por fuera, el papel en medio y rojo por dentro.
                cv.drawRect(20f, 20f, A - 20f, B - 20f, Paint(trazo).apply { color = 0xFF1F3A6E.toInt(); strokeWidth = 1.8f; alpha = 200 })
                cv.drawRect(28f, 28f, A - 28f, B - 28f, Paint(trazo).apply { strokeWidth = 1.8f; alpha = 200 })
            }
            TemaId.ANTIGUAS -> {
                // Un filete con esquineras de hierro y una gema en cada una.
                rect(24f, trazo)
                val hierro = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF4A4E54.toInt() }
                val gema = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF2E7A5A.toInt() }
                esquinas.forEach { (x, y) ->
                    val (sx, sy) = haciaDentro(x, y)
                    cv.drawPath(android.graphics.Path().apply { moveTo(x, y); lineTo(x + sx * 20f, y); lineTo(x, y + sy * 20f); close() }, hierro)
                    cv.drawCircle(x + sx * 5f, y + sy * 5f, 2.4f, gema)
                }
            }
        }
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
            marco(tema, cv, acento, tinta)
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
        val med = medidas(tema, carta = true)
        val voz = vozCarta(tema)
        val pTexto = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { typeface = tfTexto; textSize = med.tam; color = tinta }
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
        bloque(layout(if (c.alFuturo) voz.paraYo.format(Fechas.larga(c.abrir_en)) else "Para @${c.para}", pPara, 1.15f))
        if (c.alFuturo) { y += 2f; bloque(layout("${voz.sellada} el ${Fechas.larga(c.escrita)}" + (c.abierta_el?.let { " · abierta el ${Fechas.larga(it)}" } ?: ""), pNota)) }
        y += 16f
        val cx = ANCHO / 2f
        filete(tema, lienzo(), margen, ANCHO - margen, y, acento, tinta, fondo, tfTitulo)
        y += 20f
        // El texto, con la inicial del tema (la misma que en el diario).
        val cuerpo = conInicial(ctx, tema, c.texto.trim(), acento, fondo, tinta)
        bloque(layout(cuerpo, pTexto, med.interlinea))
        // Firma y lacre.
        y += 26f
        val nombre = Repo.config.perfil.nombre.ifBlank { "yo" }
        bloque(layout("— $nombre", pFirma, 1.1f, Layout.Alignment.ALIGN_OPPOSITE))
        if (y + 90f > ALTO - margen - 24) nueva()
        y += 18f
        val r = 30f
        val cv = lienzo()
        // El sello del tema, el mismo del sobre (cera, goma, chip, cápsula, escarapela…).
        val claro = e.copy(primary = Color.White.copy(alpha = 0.9f), secondary = Color.White.copy(alpha = 0.7f), tertiary = Color.White.copy(alpha = 0.8f))
        cv.save(); cv.translate(cx - 2 * r, y + r - 2 * r)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, androidx.compose.ui.graphics.Canvas(cv), Size(4 * r, 4 * r)) { selloCarta(tema, Offset(2 * r, 2 * r), r, papeleria(tema).lacre, claro) }
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
