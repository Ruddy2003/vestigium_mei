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
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Grados
import io.github.ruddy2003.vestigium.data.Lugares
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.LibroDiario
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.esquemaDe
import io.github.ruddy2003.vestigium.ui.EstiloGrafica
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.lineaMeta
import io.github.ruddy2003.vestigium.ui.pincelCifras
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import io.github.ruddy2003.vestigium.ui.rejillaGrafica
import kotlin.math.abs
import kotlin.math.roundToInt

/** Colores y letras del tema para dibujar fuera de la pantalla (reportes, imágenes para compartir). */
class Lienzo(private val ctx: Context, val tema: TemaId = Estilo.tema, val oscuro: Boolean = oscuroFuera(ctx, tema)) {
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
    /** Las cifras grandes: cada tema con su letra ([letraCifra]); si no carga, la del título. */
    val cifra: Typeface = runCatching { ResourcesCompat.getFont(ctx, letraCifra(tema)) }.getOrNull() ?: titulo
    /** El estilo de las gráficas del tema: punto, trazo, rejilla, eje, barra y letra de las cifras chicas. */
    val grafica: EstiloGrafica = estiloGrafica(tema)

    fun p(color: Int, tam: Float, tf: Typeface = texto, espacio: Float = 0f) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color; textSize = tam; typeface = tf; letterSpacing = espacio
    }
    /** Pincel de las cifras chicas de las gráficas (ejes y valores), con la letra de cifras del tema. */
    fun pc(color: Int, tam: Float): TextPaint = p(color, tam).also { grafica.pincelCifras(it, ctx) }
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
 * La letra de las cifras de cada tema. Ninguna se repite entre temas; en los de letra gótica o caligráfica se escoge
 * el corte cuyos números se leen bien (la gótica seminegrita, la textura del título gótico, la Almendra negrita, la Cormorant).
 */
private fun letraCifra(tema: TemaId): Int = when (tema) {
    TemaId.ROMANO -> R.font.cinzel_regular          // capitales romanas, como sus títulos
    TemaId.URBE -> R.font.titillium_bold          // las cifras de los letreros y de los números de autobús
    TemaId.CIBER -> R.font.sharetech_mono           // la consola: monoespaciada de terminal
    TemaId.NEON -> R.font.chakra_semibold          // la de los letreros
    TemaId.HELENO -> R.font.garamond_medium
    TemaId.EGIPTO -> R.font.messiri_bold            // la de los títulos, redonda y firme
    TemaId.MEDIEVAL -> R.font.gotica_semibold       // la gótica del copista, en su corte más claro
    TemaId.TEMPLARIO -> R.font.regla_bold           // la letra de la regla, negrita: cifras firmes
    TemaId.DISTOPIA -> R.font.oswald_bold           // números de expediente, estrechos y negros
    TemaId.REFUGIO -> R.font.plex_mono_semibold     // la máquina del refugio: mono, gruesa
    TemaId.ORDEN -> R.font.saira_stencil            // estarcido
    TemaId.LABORATORIO -> R.font.jost_medium
    TemaId.COLONOS -> R.font.garamond_semibold      // el tipo de la gaceta
    TemaId.NAPOLEON -> R.font.bodoni_titulo         // Didot
    TemaId.BOTANICO -> R.font.fraunces_titulo
    TemaId.RUINAS -> R.font.letrero_titulo        // el letrero pintado a mano
    TemaId.NAVIDAD -> R.font.cinzel_deco_bold
    TemaId.SEDA -> R.font.almendra_bold
    TemaId.EDO -> R.font.shippori_titulo            // cifras de mincho, como en los libros impresos en madera
    TemaId.GOTICO -> R.font.gotica_textura          // la textura del vitral; sus números se leen bien
    TemaId.ARCANO -> R.font.spectral_medium         // la serifa del archivo, de cifras claras
    TemaId.ESPACIO -> R.font.orbitron_bold
    TemaId.ABISMO -> R.font.batiscafo_semibold      // cifras estrechas de manómetro y profundímetro
    TemaId.ANDALUS -> R.font.kufi_medium
    TemaId.MESO -> R.font.alegreya_sc_bold         // las versalitas del códice (y sus numerales de puntos y barras)
    TemaId.MONGOL -> R.font.philosopher_bold
    TemaId.RENACIMIENTO -> R.font.cormorant_medium  // números de cuaderno, con trazos de pluma
    TemaId.PIRATA -> R.font.caslon_semibold      // números de libro de a bordo, en Caslon
    TemaId.URSS -> R.font.russo
    TemaId.REVOLUCION -> R.font.fell                // tipos de imprenta de cartel
    TemaId.ANTIGUAS -> R.font.cinzel_semibold       // números tallados en piedra
}

/** El nombre de la app como lo firma cada tema arriba del reporte. */
private fun marcaReporte(tema: TemaId): String = when (tema) {
    TemaId.ROMANO -> "VESTIGIVM MEI"
    TemaId.URBE -> "ROMA · VESTIGIUM MEI"
    TemaId.CIBER -> "vestigium@mei:~\$"
    TemaId.NEON -> "屋台 · VESTIGIUM MEI · NIVEL −3"
    TemaId.HELENO -> "ΙΧΝΟΣ ΕΜΟΝ · Vestigium Mei"
    TemaId.EGIPTO -> "☥ Vestigium Mei · papiro del escriba"
    TemaId.MEDIEVAL -> "Liber vestigii mei"
    TemaId.TEMPLARIO -> "Pauperes commilitones · Vestigium Mei"
    TemaId.DISTOPIA -> "EXPEDIENTE · VESTIGIUM MEI"
    TemaId.REFUGIO -> "REFUGIO · BITÁCORA VESTIGIUM-MEI"
    TemaId.ORDEN -> "ARCHIVUM ORDINIS · VESTIGIUM MEI"
    TemaId.LABORATORIO -> "Vestigium Mei — cuaderno de laboratorio"
    TemaId.COLONOS -> "La Gaceta de Vestigium Mei"
    TemaId.NAPOLEON -> "BULLETIN DE VESTIGIUM MEI"
    TemaId.BOTANICO -> "Herbarium · Vestigium Mei"
    TemaId.RUINAS -> "Cuaderno del superviviente · Vestigium Mei"
    TemaId.NAVIDAD -> "Cartas al fuego · Vestigium Mei"
    TemaId.SEDA -> "Vestigium Mei · diario de caravana"
    TemaId.EDO -> "Vestigium Mei · 道中日記 · diario del camino"
    TemaId.GOTICO -> "Liber horarum · Vestigium mei"
    TemaId.ARCANO -> "Vestigium Mei · archivo del caso, caja reservada"
    TemaId.ESPACIO -> "VESTIGIUM MEI / MISIÓN"
    TemaId.ABISMO -> "BITÁCORA DE INMERSIÓN · VESTIGIUM MEI"
    TemaId.ANDALUS -> "Vestigium Mei · أثري"
    TemaId.MESO -> "Códice de Vestigium Mei"
    TemaId.MONGOL -> "МИНИЙ МӨР · Vestigium Mei"
    TemaId.RENACIMIENTO -> "Il taccuino di Vestigium Mei"
    TemaId.PIRATA -> "Bitácora de a bordo de Vestigium Mei"
    TemaId.URSS -> "ДНЕВНИК · VESTIGIUM MEI"
    TemaId.REVOLUCION -> "Cahier de Vestigium Mei"
    TemaId.ANTIGUAS -> "Crónica de Vestigium Mei"
}

/**
 * Reportes semanal (imagen), mensual (PDF e imagen) y anual, con los colores, letras, nombres y números del tema.
 * Sin calles, direcciones ni nombres de lugares para poder compartirlos (el mapa del mes es solo puntos sobre una cuadrícula);
 * nunca incluyen lo del seguimiento privado ni el texto de las cartas.
 */
object Reportes {
    private const val W = 1080f
    private const val M = 64f

    /** Quien dibuja: lleva la altura y sabe poner cada pieza con el tema. */
    private class Hoja(val c: Canvas, val L: Lienzo, val discreto: Boolean, val sinFrases: Boolean = false) {
        var y = 0f
        var seccion = 0
        val ancho = W - 2 * M
        val eg = L.grafica

        fun envolver(t: CharSequence, p: TextPaint, w: Float = ancho, alinear: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL, max: Int = 50): StaticLayout =
            StaticLayout.Builder.obtain(t, 0, t.length, p, w.toInt()).setAlignment(alinear).setLineSpacing(0f, 1.18f).setIncludePad(false).setMaxLines(max)
                .setEllipsize(android.text.TextUtils.TruncateAt.END).build()
        fun texto(t: CharSequence, p: TextPaint, x: Float = M, w: Float = ancho, alinear: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL, max: Int = 50): Float {
            val l = envolver(t, p, w, alinear, max)
            c.save(); c.translate(x, y); l.draw(c); c.restore()
            y += l.height
            return l.height.toFloat()
        }

        /** Adorno del tema entre secciones: cada tema tiene su figura (greca, ola, cenefa, cursor, ojo, átomo, engranaje…). */
        fun ornamento() {
            val yy = y + 10f
            val cx = W / 2
            // Las dos líneas a los lados de una figura central de medio ancho [medio].
            fun flancos(yc: Float, medio: Float, pa: Paint) {
                c.drawLine(M, yc, cx - medio, yc, pa); c.drawLine(cx + medio, yc, W - M, yc, pa)
            }
            val tema = L.tema
            when (tema) {
                TemaId.ROMANO -> {
                    // La greca (meandro) de los mosaicos, de orilla a orilla.
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
                TemaId.URBE -> {
                    // El perfil de los tejados: una cúpula grande con su linterna entre dos campanarios y dos cúpulas chicas,
                    // sobre la línea de las cornisas.
                    val base = yy + 44f
                    val pa = L.relleno(L.primario)
                    val sombra = L.relleno(L.mezcla(L.primario, L.fondo, 0.45))
                    flancos(base, 150f, L.trazo(L.tenue, 1.6f))
                    c.drawRect(cx - 150f, base - 6f, cx + 150f, base, sombra)
                    // Cúpula mayor: tambor, casquete y linterna.
                    c.drawRect(cx - 46f, base - 30f, cx + 46f, base - 6f, pa)
                    c.drawArc(RectF(cx - 44f, base - 74f, cx + 44f, base + 14f), 180f, 180f, true, pa)
                    c.drawRect(cx - 7f, base - 88f, cx + 7f, base - 70f, pa)
                    c.drawCircle(cx, base - 92f, 4f, pa)
                    val gajo = L.trazo(L.fondo, 1.4f)
                    listOf(-26f, 0f, 26f).forEach { dx -> c.drawLine(cx + dx * 0.4f, base - 72f, cx + dx, base - 32f, gajo) }
                    // Campanarios y cúpulas chicas a los lados.
                    listOf(-1f, 1f).forEach { s ->
                        val xt = cx + s * 82f
                        c.drawRect(xt - 9f, base - 62f, xt + 9f, base - 6f, sombra)
                        c.drawRect(xt - 4f, base - 54f, xt + 4f, base - 44f, L.relleno(L.fondo))
                        val path = Path().apply { moveTo(xt - 11f, base - 62f); lineTo(xt, base - 74f); lineTo(xt + 11f, base - 62f); close() }
                        c.drawPath(path, sombra)
                        val xc = cx + s * 124f
                        c.drawArc(RectF(xc - 18f, base - 36f, xc + 18f, base), 180f, 180f, true, sombra)
                        c.drawRect(xc - 2.5f, base - 44f, xc + 2.5f, base - 34f, sombra)
                    }
                    y += 44f + 34f
                }
                TemaId.CIBER -> {
                    // La línea de órdenes: el indicador, una fila de puntos de fósforo y el cursor en bloque.
                    c.drawText(">", M, yy + 24f, L.p(L.primario, 28f, L.mono))
                    val punto = L.relleno(L.mezcla(L.primario, L.fondo, 0.45))
                    var x = M + 34f
                    while (x + 6f < W - M - 34f) { c.drawRect(x, yy + 14f, x + 6f, yy + 20f, punto); x += 16f }
                    c.drawRect(W - M - 16f, yy + 2f, W - M, yy + 26f, L.relleno(L.primario))
                    y += 48f
                }
                TemaId.NEON -> {
                    // Un tubo de neón de orilla a orilla, con su halo, partido al centro por un rombo ámbar.
                    val halo = L.trazo(L.mezcla(L.primario, L.fondo, 0.7), 14f)
                    val nucleo = L.trazo(L.primario, 4f)
                    flancos(yy + 14f, 22f, halo); flancos(yy + 14f, 22f, nucleo)
                    val r = Path().apply { moveTo(cx, yy); lineTo(cx + 14f, yy + 14f); lineTo(cx, yy + 28f); lineTo(cx - 14f, yy + 14f); close() }
                    c.drawPath(r, L.relleno(L.secundario))
                    y += 48f
                }
                TemaId.HELENO -> {
                    // La ola que corre de los vasos griegos: cada onda sube y se enrolla, con un punto en el ojo.
                    val r = 9f; val paso = 40f; val base = yy + 26f
                    val pa = L.trazo(L.primario, 2.4f)
                    val ojo = L.relleno(L.terciario)
                    val path = Path(); var x = M
                    path.moveTo(x, base)
                    while (x + paso <= W - M) {
                        val ox = x + paso * 0.62f; val oy = base - r
                        path.quadTo(x + paso * 0.3f, base, ox - r, oy)
                        path.arcTo(RectF(ox - r, oy - r, ox + r, oy + r), 180f, 270f)
                        path.lineTo(x + paso, base)
                        c.drawCircle(ox, oy, 2.6f, ojo)
                        x += paso
                    }
                    c.drawPath(path, pa)
                    c.drawLine(M, base + 7f, x, base + 7f, L.trazo(L.primario, 1.2f))
                    y += 62f
                }
                TemaId.EGIPTO -> {
                    // Friso de lotos abiertos y capullos, como en las tumbas tebanas, sobre una cenefa de rectángulos de colores.
                    val base = yy + 30f; val paso = 44f
                    val pa = L.trazo(L.primario, 2f); val flor = L.relleno(L.primario); val capullo = L.relleno(L.secundario)
                    var x = M + paso / 2
                    while (x + paso / 2 <= W - M) {
                        c.drawLine(x, base, x, base - 10f, pa)
                        val p = Path()
                        p.moveTo(x, base - 10f); p.lineTo(x - 11f, base - 26f); p.lineTo(x - 4f, base - 22f); p.lineTo(x, base - 31f)
                        p.lineTo(x + 4f, base - 22f); p.lineTo(x + 11f, base - 26f); p.close()
                        c.drawPath(p, flor)
                        val xb = x + paso / 2
                        if (xb + 6f < W - M) {
                            c.drawLine(xb, base, xb, base - 8f, pa)
                            val q = Path()
                            q.moveTo(xb, base - 8f); q.quadTo(xb - 6f, base - 14f, xb, base - 22f); q.quadTo(xb + 6f, base - 14f, xb, base - 8f); q.close()
                            c.drawPath(q, capullo)
                        }
                        x += paso
                    }
                    val colores = intArrayOf(L.primario, L.terciario, L.secundario, L.terciario)
                    var xr = M; var k = 0
                    while (xr + 12f <= W - M) { c.drawRect(xr, base + 4f, xr + 12f, base + 12f, L.relleno(colores[k % 4])); xr += 14f; k++ }
                    y += 62f
                }
                TemaId.MEDIEVAL -> {
                    // Cenefa de copista: dos filetes de tinta y, entre ellos, rombos de rúbrica que alternan con puntos de oro.
                    val a = yy + 2f; val b = yy + 26f; val mid = (a + b) / 2
                    val fil = L.trazo(L.tinta, 1.4f)
                    c.drawLine(M, a, W - M, a, fil); c.drawLine(M, b, W - M, b, fil)
                    val rubrica = L.relleno(L.primario); val oro = L.relleno(L.terciario)
                    var x = M + 14f; var i = 0
                    while (x + 7f <= W - M) {
                        if (i % 2 == 0) {
                            val rombo = Path().apply { moveTo(x, mid - 8f); lineTo(x + 7f, mid); lineTo(x, mid + 8f); lineTo(x - 7f, mid); close() }
                            c.drawPath(rombo, rubrica)
                        } else c.drawCircle(x, mid, 3f, oro)
                        x += 22f; i++
                    }
                    y += 50f
                }
                TemaId.TEMPLARIO -> {
                    // El adarve de la muralla: una hilera de almenas en tinta de hierro y, en medio, el escudo con la cruz patada.
                    val base = yy + 34f; val alto = 12f; val merlon = 14f; val cx = W / 2
                    val hierro = L.relleno(L.tinta)
                    c.drawRect(M, base - 4f, W - M, base, hierro)
                    var x = M
                    while (x + merlon <= W - M) {
                        if (x + merlon < cx - 30f || x > cx + 30f) c.drawRect(x, base - 4f - alto, x + merlon, base - 4f, hierro)
                        x += merlon * 2
                    }
                    val escudo = Path().apply {
                        moveTo(cx - 20f, yy); lineTo(cx + 20f, yy); quadTo(cx + 21f, yy + 28f, cx, yy + 42f); quadTo(cx - 21f, yy + 28f, cx - 20f, yy); close()
                    }
                    c.drawPath(escudo, L.relleno(L.fondo)); c.drawPath(escudo, L.trazo(L.tinta, 1.6f))
                    val rojo = L.relleno(L.primario); val my = yy + 17f
                    val cruz = Path().apply {
                        moveTo(cx - 3f, my - 3f); lineTo(cx - 7f, my - 15f); lineTo(cx + 7f, my - 15f); lineTo(cx + 3f, my - 3f)
                        lineTo(cx + 15f, my - 7f); lineTo(cx + 15f, my + 7f); lineTo(cx + 3f, my + 3f); lineTo(cx + 7f, my + 15f)
                        lineTo(cx - 7f, my + 15f); lineTo(cx - 3f, my + 3f); lineTo(cx - 15f, my + 7f); lineTo(cx - 15f, my - 7f); close()
                    }
                    c.drawPath(cruz, rojo)
                    y += 58f
                }
                TemaId.DISTOPIA -> {
                    // La barra del expediente se abre en el centro para el ojo que lo mira todo.
                    val yc = yy + 16f
                    val barra = L.relleno(L.primario)
                    c.drawRect(M, yc - 4f, cx - 70f, yc + 4f, barra); c.drawRect(cx + 70f, yc - 4f, W - M, yc + 4f, barra)
                    val ojo = Path().apply { moveTo(cx - 54f, yc); quadTo(cx, yc - 34f, cx + 54f, yc); quadTo(cx, yc + 34f, cx - 54f, yc); close() }
                    c.drawPath(ojo, L.relleno(L.superficie2))
                    c.drawPath(ojo, L.trazo(L.tinta, 3f))
                    c.drawCircle(cx, yc, 10f, barra); c.drawCircle(cx, yc, 4f, L.relleno(L.tinta))
                    y += 54f
                }
                TemaId.REFUGIO -> {
                    // Optimismo atómico: el átomo al centro de una línea discontinua, como las marcas del pasillo del refugio.
                    val yc = yy + 20f
                    flancos(yc, 46f, L.trazo(L.linea, 2.4f).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(14f, 8f), 0f) })
                    val orbita = L.trazo(L.primario, 2.2f)
                    for (k in 0 until 3) {
                        c.save(); c.rotate(k * 60f, cx, yc)
                        c.drawOval(RectF(cx - 28f, yc - 9f, cx + 28f, yc + 9f), orbita)
                        c.restore()
                    }
                    c.drawCircle(cx, yc, 5f, L.relleno(L.secundario))
                    y += 60f
                }
                TemaId.ORDEN -> {
                    // Viga de acero remachada y, al centro, el engranaje de la orden que custodia el saber técnico.
                    val yc = yy + 18f
                    val viga = L.relleno(L.mezcla(L.linea, L.tinta, 0.35))
                    c.drawRect(M, yc - 5f, cx - 34f, yc + 5f, viga); c.drawRect(cx + 34f, yc - 5f, W - M, yc + 5f, viga)
                    val remache = L.relleno(L.terciario)
                    var x = M + 16f
                    while (x < W - M - 10f) { if (abs(x - cx) > 48f) c.drawCircle(x, yc, 2.6f, remache); x += 36f }
                    val acero = L.relleno(L.primario)
                    for (k in 0 until 8) {
                        c.save(); c.rotate(k * 45f, cx, yc)
                        c.drawRect(cx - 5f, yc - 24f, cx + 5f, yc - 14f, acero)
                        c.restore()
                    }
                    c.drawCircle(cx, yc, 17f, acero); c.drawCircle(cx, yc, 6f, L.relleno(L.fondo))
                    y += 58f
                }
                TemaId.LABORATORIO -> {
                    // Una regla graduada: una marca cada 12 px, más larga cada cinco y numerada cada diez, porque aquí todo se mide.
                    val base = yy + 4f
                    val fina = L.trazo(L.primario, 1.6f); val gruesa = L.trazo(L.primario, 2.4f)
                    val pn = L.p(L.tenue, 13f, L.mono)
                    c.drawLine(M, base, W - M, base, gruesa)
                    var x = M; var i = 0
                    while (x <= W - M + 0.5f) {
                        val largo = if (i % 10 == 0) 20f else if (i % 5 == 0) 13f else 7f
                        c.drawLine(x, base, x, base + largo, if (i % 5 == 0) gruesa else fina)
                        if (i % 10 == 0 && i > 0) c.drawText("${i / 10}", x + 3f, base + 22f, pn)
                        x += 12f; i++
                    }
                    y += 44f
                }
                TemaId.COLONOS -> {
                    // Filete de gaceta: el filete inglés, grueso al centro y afilado en las puntas, sobre un filete fino.
                    val yc = yy + 8f
                    val filete = Path().apply { moveTo(M, yc); quadTo(cx, yc - 10f, W - M, yc); quadTo(cx, yc + 10f, M, yc); close() }
                    c.drawPath(filete, L.relleno(L.tinta))
                    c.drawLine(M + 40f, yc + 13f, W - M - 40f, yc + 13f, L.trazo(L.primario, 1.4f))
                    y += 42f
                }
                TemaId.NAPOLEON -> {
                    // Las abejas del manto imperial: tres al centro, entre dos filetes.
                    val yc = yy + 14f
                    val oro = if (L.oscuro) L.primario else L.terciario
                    flancos(yc, 92f, L.trazo(oro, 2f))
                    for (k in -1..1) {
                        val x = cx + k * 58f
                        val ala = L.trazo(oro, 1.8f)
                        c.drawOval(RectF(x - 15f, yc - 12f, x - 2f, yc - 3f), ala)
                        c.drawOval(RectF(x + 2f, yc - 12f, x + 15f, yc - 3f), ala)
                        val cuerpo = L.relleno(oro)
                        c.drawCircle(x, yc - 10f, 3.4f, cuerpo)
                        c.drawCircle(x, yc - 4f, 4f, cuerpo)
                        c.drawOval(RectF(x - 5f, yc - 1f, x + 5f, yc + 16f), cuerpo)
                        val raya = L.trazo(L.fondo, 1.4f)
                        c.drawLine(x - 4f, yc + 5f, x + 4f, yc + 5f, raya); c.drawLine(x - 4f, yc + 10f, x + 4f, yc + 10f, raya)
                    }
                    y += 50f
                }
                TemaId.BOTANICO -> {
                    // Una fronda a tinta: el tallo cruza la hoja con hojitas alternas, más grandes al centro, como en un herbario.
                    val yc = yy + 14f
                    c.drawLine(M, yc, W - M, yc, L.trazo(L.tinta, 1.6f))
                    val hoja = L.relleno(L.primario)
                    var x = M + 10f; var i = 0
                    while (x < W - M - 24f) {
                        val s = 1f - 0.45f * abs(x - cx) / (cx - M)
                        c.save(); c.rotate(if (i % 2 == 0) -35f else 35f, x, yc); c.scale(s, s, x, yc)
                        val p = Path().apply { moveTo(x, yc); quadTo(x + 9f, yc - 8f, x + 22f, yc); quadTo(x + 9f, yc + 8f, x, yc); close() }
                        c.drawPath(p, hoja); c.restore()
                        x += 26f; i++
                    }
                    y += 46f
                }
                TemaId.RUINAS -> {
                    // Una losa de hormigón partida por una grieta en zigzag; encima trepa una enredadera de hiedra con hojas de tres puntas.
                    val yc = yy + 18f
                    val losa = L.relleno(L.mezcla(L.linea, L.tinta, 0.25))
                    c.drawRect(M, yc - 4f, cx - 12f, yc + 4f, losa); c.drawRect(cx + 8f, yc - 4f, W - M, yc + 4f, losa)
                    val grieta = Path().apply { moveTo(cx - 12f, yc - 4f); lineTo(cx - 4f, yc - 1f); lineTo(cx - 10f, yc + 1.5f); lineTo(cx + 8f, yc + 4f) }
                    c.drawPath(grieta, L.trazo(L.tinta, 1.4f))
                    val tallo = Path(); tallo.moveTo(M, yc - 6f)
                    val hojas = mutableListOf<Pair<Float, Float>>()
                    var x = M; var i = 0
                    while (x < W - M - 8f) {
                        val alto = if (i % 2 == 0) yc - 20f else yc - 2f
                        tallo.quadTo(x + 18f, alto, x + 36f, yc - 6f)
                        hojas += (x + 18f) to (if (i % 2 == 0) yc - 13f else yc - 4f)
                        x += 36f; i++
                    }
                    c.drawPath(tallo, L.trazo(L.mezcla(L.primario, L.tinta, 0.4), 1.3f))
                    val verde = L.relleno(L.primario)
                    hojas.forEachIndexed { k, (hx, hy) ->
                        val s = 0.7f + 0.3f * (1f - abs(hx - cx) / (cx - M))
                        c.save(); c.translate(hx, hy); c.rotate(if (k % 2 == 0) -25f else 155f); c.scale(s, s)
                        val hoja = Path().apply { moveTo(0f, 0f); lineTo(-7f, -4f); lineTo(-9f, -12f); lineTo(-3f, -10f); lineTo(0f, -17f); lineTo(3f, -10f); lineTo(9f, -12f); lineTo(7f, -4f); close() }
                        c.drawPath(hoja, verde); c.restore()
                    }
                    y += 46f
                }
                TemaId.NAVIDAD -> {
                    // Guirnalda de pino colgada en festones, con una esfera en cada nudo.
                    val top = yy + 2f; val paso = 68f
                    val n = ((W - 2 * M) / paso).toInt(); val x0 = M + (W - 2 * M - n * paso) / 2
                    val path = Path(); path.moveTo(x0, top)
                    for (k in 0 until n) path.quadTo(x0 + k * paso + paso / 2, top + 30f, x0 + (k + 1) * paso, top)
                    c.drawPath(path, L.trazo(L.secundario, 3.2f))
                    for (k in 0..n) {
                        val bx = x0 + k * paso
                        c.drawRect(bx - 2.5f, top + 1f, bx + 2.5f, top + 5f, L.relleno(L.terciario))
                        c.drawCircle(bx, top + 12f, 7f, L.relleno(if (k % 2 == 0) L.primario else L.terciario))
                    }
                    y += 48f
                }
                TemaId.SEDA -> {
                    // Una sarta de monedas de bronce con agujero cuadrado, ensartadas en un cordón de seda.
                    val yc = yy + 16f
                    val cordon = L.trazo(L.secundario, 2f)
                    c.drawLine(M, yc, W - M, yc, cordon)
                    c.drawCircle(M + 4f, yc, 4f, L.relleno(L.secundario)); c.drawCircle(W - M - 4f, yc, 4f, L.relleno(L.secundario))
                    val bronce = 0xFFB3863A.toInt()
                    for (k in -2..2) {
                        val mx = cx + k * 44f; val r = 16f
                        c.drawCircle(mx, yc, r, L.relleno(bronce))
                        c.drawCircle(mx, yc, r - 3.5f, L.trazo(L.mezcla(bronce, L.tinta, 0.4), 1.4f))
                        c.drawRect(mx - 5f, yc - 5f, mx + 5f, yc + 5f, L.relleno(L.fondo))
                        c.drawLine(mx - 5f, yc, mx + 5f, yc, cordon)
                    }
                    y += 52f
                }
                TemaId.EDO -> {
                    // Una franja de seigaiha (las olas en escamas de las telas y los grabados) y, al centro, el sello de bermellón.
                    val r = 13f; val base = yy + 36f
                    val ola = L.trazo(L.primario, 1.3f); val fondo = L.relleno(L.fondo)
                    for (fila in 1 downTo 0) {
                        val yb = base - fila * r * 0.5f; val dx = if (fila == 1) r else 0f
                        var x = M + dx
                        while (x <= W - M) {
                            val caja = RectF(x - r, yb - r, x + r, yb + r)
                            c.drawArc(caja, 180f, 180f, true, fondo)
                            for (k in 0..2) { val q = r * (1f - k * 0.3f); c.drawArc(RectF(x - q, yb - q, x + q, yb + q), 180f, 180f, false, ola) }
                            x += 2 * r
                        }
                    }
                    c.drawRect(cx - 14f, base - 34f, cx + 14f, base - 6f, L.relleno(L.secundario))
                    c.drawRect(cx - 10.5f, base - 30.5f, cx + 10.5f, base - 9.5f, L.trazo(L.fondo, 1.6f))
                    c.drawLine(cx, base - 28f, cx, base - 12f, L.trazo(L.fondo, 2f)); c.drawLine(cx - 7f, base - 22f, cx + 7f, base - 22f, L.trazo(L.fondo, 2f))
                    y += 50f
                }
                TemaId.GOTICO -> {
                    // Una arquería de ojivas, como el triforio de una capilla, con un punto de oro entre arco y arco.
                    val vano = 36f; val alto = 26f; val base = yy + 30f; val arranque = base - alto * 0.45f
                    val n = ((W - 2 * M) / vano).toInt(); val x0 = M + (W - 2 * M - n * vano) / 2
                    val pa = L.trazo(L.primario, 2f)
                    val path = Path()
                    for (k in 0 until n) {
                        val a = x0 + k * vano; val b = a + vano; val m = (a + b) / 2; val cima = base - alto
                        path.moveTo(a, base); path.lineTo(a, arranque)
                        path.cubicTo(a, arranque - alto * 0.35f, m - vano * 0.15f, cima + alto * 0.15f, m, cima)
                        path.cubicTo(m + vano * 0.15f, cima + alto * 0.15f, b, arranque - alto * 0.35f, b, arranque)
                        path.lineTo(b, base)
                        if (k < n - 1) c.drawCircle(b, cima + 2f, 2.4f, L.relleno(L.terciario))
                    }
                    c.drawPath(path, pa)
                    c.drawLine(x0, base, x0 + n * vano, base, L.trazo(L.primario, 2.6f))
                    y += 56f
                }
                TemaId.ARCANO -> {
                    // La costa vista desde el mar: la línea de las olas a tinta, el acantilado y el faro al centro con sus dos haces.
                    val base = yy + 32f
                    val ola = Path(); var x = M; ola.moveTo(M, base)
                    while (x < W - M) { ola.quadTo(x + 6f, base - 4f, x + 12f, base); ola.quadTo(x + 18f, base + 4f, x + 24f, base); x += 24f }
                    c.drawPath(ola, L.trazo(L.tinta, 1.6f))
                    val haz = L.relleno(L.mezcla(L.primario, L.fondo, 0.6))
                    listOf(-1f, 1f).forEach { s -> c.drawPath(Path().apply { moveTo(cx, yy + 4f); lineTo(cx + s * (W / 2 - M), yy - 6f); lineTo(cx + s * (W / 2 - M), yy + 14f); close() }, haz) }
                    c.drawPath(Path().apply { moveTo(cx - 40f, base); quadTo(cx - 30f, base - 16f, cx - 10f, base - 18f); lineTo(cx + 14f, base - 18f); quadTo(cx + 34f, base - 10f, cx + 44f, base); close() }, L.relleno(L.tinta))
                    c.drawPath(Path().apply { moveTo(cx - 6f, base - 18f); lineTo(cx - 4f, yy + 8f); lineTo(cx + 4f, yy + 8f); lineTo(cx + 6f, base - 18f); close() }, L.relleno(L.tinta))
                    c.drawRect(cx - 5f, yy + 2f, cx + 5f, yy + 8f, L.relleno(L.primario))
                    c.drawLine(cx - 6f, yy + 2f, cx + 6f, yy + 2f, L.trazo(L.tinta, 1.6f))
                    y += 56f
                }
                TemaId.ESPACIO -> {
                    // Un planeta con su anillo al centro y, a los lados, estrellas de varios brillos.
                    val yc = yy + 16f
                    val estrella = L.relleno(L.mezcla(L.primario, L.fondo, 0.25))
                    var x = M; var i = 0
                    while (x < W - M) {
                        if (abs(x - cx) > 60f) c.drawCircle(x, yc + ((i * 7) % 5 - 2) * 2.4f, 1.2f + (i * 13 % 4) * 0.7f, estrella)
                        x += 19f; i++
                    }
                    c.drawCircle(cx, yc, 13f, L.relleno(L.primario))
                    c.save(); c.rotate(-18f, cx, yc)
                    c.drawOval(RectF(cx - 30f, yc - 7f, cx + 30f, yc + 7f), L.trazo(L.terciario, 2.4f))
                    c.restore()
                    y += 50f
                }
                TemaId.ABISMO -> {
                    // La traza del sonar: el perfil del fondo con sus sondeos verticales y, al centro, una medusa.
                    val yc = yy + 14f
                    fun fondo(x: Float) = yc + 12f + 5f * kotlin.math.sin(x / 37f) + 3f * kotlin.math.sin(x / 13f)
                    val perfil = L.trazo(L.mezcla(L.tinta, L.fondo, 0.35), 1.6f)
                    val sondeo = L.trazo(L.mezcla(L.primario, L.fondo, 0.4), 1f)
                    for (lado in listOf(-1f, 1f)) {
                        val ruta = Path(); var x = cx + lado * 46f; var primero = true
                        while (if (lado < 0) x >= M else x <= W - M) {
                            if (primero) { ruta.moveTo(x, fondo(x)); primero = false } else ruta.lineTo(x, fondo(x))
                            x += lado * 6f
                        }
                        c.drawPath(ruta, perfil)
                        var s = cx + lado * 70f
                        while (if (lado < 0) s >= M else s <= W - M) {
                            c.drawLine(s, yc - 12f, s, fondo(s) - 3f, sondeo)
                            c.drawCircle(s, yc - 12f, 2f, L.relleno(L.primario))
                            s += lado * 48f
                        }
                    }
                    c.drawArc(RectF(cx - 18f, yc - 20f, cx + 18f, yc + 8f), 180f, 180f, true, L.relleno(L.primario))
                    val tentaculo = L.trazo(L.terciario, 1.6f)
                    for (k in 0 until 4) {
                        val tx = cx - 12f + k * 8f
                        c.drawPath(Path().apply { moveTo(tx, yc - 6f); quadTo(tx + (if (k % 2 == 0) 6f else -6f), yc + 6f, tx, yc + 18f) }, tentaculo)
                    }
                    y += 50f
                }
                TemaId.ANDALUS -> {
                    // Lazo de azulejo: estrellas de ocho puntas (dos cuadrados girados) unidas por una cinta doble.
                    val yc = yy + 18f; val paso = 96f
                    val n = ((W - 2 * M) / paso).toInt(); val x0 = M + (W - 2 * M - (n - 1) * paso) / 2
                    val cinta = L.trazo(L.secundario, 1.6f)
                    c.drawLine(M, yc - 4f, W - M, yc - 4f, cinta); c.drawLine(M, yc + 4f, W - M, yc + 4f, cinta)
                    val estrella = L.relleno(L.primario)
                    for (k in 0 until n) {
                        val sx = x0 + k * paso
                        for (g in 0..1) {
                            c.save(); c.rotate(g * 45f, sx, yc)
                            c.drawRect(sx - 11f, yc - 11f, sx + 11f, yc + 11f, estrella)
                            c.restore()
                        }
                        c.drawCircle(sx, yc, 5f, L.relleno(L.fondo))
                    }
                    y += 52f
                }
                TemaId.MESO -> {
                    // Greca escalonada (xicalcoliuhqui): un escalón que sube tres gradas y remata en un gancho en espiral,
                    // repetido entre dos cenefas, como en los frisos de piedra de Mitla y Uxmal; puntos de jade en los huecos.
                    val a = yy + 4f; val b = yy + 34f; val paso = 40f
                    val greca = L.trazo(L.primario, 2.6f).apply { strokeCap = Paint.Cap.SQUARE; strokeJoin = Paint.Join.MITER }
                    c.drawLine(M, a - 2f, W - M, a - 2f, L.trazo(L.secundario, 1.4f)); c.drawLine(M, b + 2f, W - M, b + 2f, L.trazo(L.secundario, 1.4f))
                    val jade = L.relleno(L.terciario)
                    var x = M
                    while (x + paso <= W - M) {
                        val g = (b - a) / 4f; val p = Path()
                        p.moveTo(x, b); p.lineTo(x + 4f, b); p.lineTo(x + 4f, b - g); p.lineTo(x + 9f, b - g); p.lineTo(x + 9f, b - 2 * g)
                        p.lineTo(x + 14f, b - 2 * g); p.lineTo(x + 14f, a); p.lineTo(x + 30f, a); p.lineTo(x + 30f, a + 2.4f * g)
                        p.lineTo(x + 20f, a + 2.4f * g); p.lineTo(x + 20f, a + 1.2f * g); p.lineTo(x + 25f, a + 1.2f * g)
                        c.drawPath(p, greca)
                        c.drawCircle(x + 32f, b - 5f, 2.6f, jade)
                        x += paso
                    }
                    y += 50f
                }
                TemaId.MONGOL -> {
                    // El enrejado de la pared de una ger: listones cruzados en diagonal entre dos cintas de fieltro.
                    val a = yy + 2f; val b = yy + 26f; val paso = 24f
                    val liston = L.trazo(L.terciario, 1.8f)
                    val nudo = L.relleno(L.primario)
                    var x = M
                    while (x + paso <= W - M) {
                        c.drawLine(x, a, x + paso, b, liston); c.drawLine(x, b, x + paso, a, liston)
                        c.drawCircle(x + paso / 2, (a + b) / 2, 2.2f, nudo)
                        x += paso
                    }
                    val cinta = L.trazo(L.primario, 2.6f)
                    c.drawLine(M, a, W - M, a, cinta); c.drawLine(M, b, W - M, b, cinta)
                    y += 50f
                }
                TemaId.RENACIMIENTO -> {
                    // Trazos de construcción del cuaderno: círculo y cuadrado a compás, sus ejes y cruces de medida en la línea.
                    val yc = yy + 22f; val r = 18f
                    val fino = L.trazo(L.mezcla(L.primario, L.fondo, 0.35), 1.1f)
                    c.drawLine(M, yc, W - M, yc, fino)
                    c.drawLine(cx, yc - r - 8f, cx, yc + r + 8f, fino)
                    c.drawCircle(cx, yc, r, L.trazo(L.primario, 1.8f))
                    val l = r * 0.82f
                    c.drawRect(cx - l, yc + r - 2 * l, cx + l, yc + r, L.trazo(L.secundario, 1.4f))
                    for (k in 0..8) {
                        val xx = M + k * (W - 2 * M) / 8
                        if (abs(xx - cx) > r + 16f) { c.drawLine(xx - 6f, yc - 6f, xx + 6f, yc + 6f, fino); c.drawLine(xx - 6f, yc + 6f, xx + 6f, yc - 6f, fino) }
                    }
                    y += 62f
                }
                TemaId.PIRATA -> {
                    // Una ola de tinta de orilla a orilla y, al centro, una rosa de ocho rumbos con la flor de lis al norte.
                    val yc = yy + 22f; val r = 18f
                    val fino = L.trazo(L.mezcla(L.primario, L.fondo, 0.35), 1.1f)
                    val ola = android.graphics.Path(); var x = M; ola.moveTo(M, yc)
                    while (x < W - M) { ola.quadTo(x + 8f, yc - 4f, x + 16f, yc); ola.quadTo(x + 24f, yc + 4f, x + 32f, yc); x += 32f }
                    c.drawPath(ola, fino)
                    c.drawRect(cx - r - 10f, yc - r - 4f, cx + r + 10f, yc + r + 4f, L.relleno(L.fondo))
                    for (k in 0 until 8) {
                        val a = Math.toRadians(-90.0 + 45.0 * k); val l = if (k % 2 == 0) r else r * 0.6f
                        c.drawLine(cx, yc, cx + (l * kotlin.math.cos(a)).toFloat(), yc + (l * kotlin.math.sin(a)).toFloat(), L.trazo(if (k == 0) L.primario else L.secundario, if (k % 2 == 0) 1.8f else 1.1f))
                    }
                    c.drawCircle(cx, yc, r * 0.62f, fino)
                    c.drawCircle(cx, yc - r - 1f, 2.6f, L.relleno(L.primario))
                    y += 62f
                }
                TemaId.URSS -> {
                    // Constructivismo: una cuña roja que entra desde la orilla, un círculo negro y una línea que sigue sola.
                    val yc = yy + 14f
                    val rojo = L.relleno(L.primario)
                    val cuna = Path().apply { moveTo(M, yc - 9f); lineTo(cx + 40f, yc - 9f); lineTo(cx + 110f, yc); lineTo(cx + 40f, yc + 9f); lineTo(M, yc + 9f); close() }
                    c.drawPath(cuna, rojo)
                    c.drawCircle(cx + 150f, yc, 12f, L.relleno(L.tinta))
                    c.drawLine(cx + 176f, yc, W - M, yc, L.trazo(L.tinta, 3f))
                    c.drawRect(W - M - 60f, yc + 8f, W - M, yc + 14f, rojo)
                    y += 46f
                }
                TemaId.REVOLUCION -> {
                    // La escarapela plisada (rojo, blanco y azul) al centro de un filete doble de cartel.
                    val yc = yy + 18f
                    val fil = L.trazo(L.tinta, 1.6f)
                    flancos(yc - 3f, 32f, fil); flancos(yc + 3f, 32f, fil)
                    c.drawCircle(cx, yc, 20f, L.relleno(L.primario))
                    c.drawCircle(cx, yc, 13.5f, L.relleno(0xFFF8F5EE.toInt()))
                    c.drawCircle(cx, yc, 7f, L.relleno(L.secundario))
                    val pliegue = L.trazo(L.mezcla(L.primario, L.tinta, 0.35), 1f)
                    for (k in 0 until 16) {
                        c.save(); c.rotate(k * 22.5f, cx, yc)
                        c.drawLine(cx, yc - 20f, cx, yc - 14f, pliegue)
                        c.restore()
                    }
                    y += 56f
                }
                TemaId.ANTIGUAS -> {
                    // Una fila de runas inventadas, talladas entre dos líneas: un palo con ramas en distinta posición.
                    val a = yy; val b = yy + 28f
                    val pa = L.trazo(L.primario, 2.2f).apply { strokeCap = Paint.Cap.SQUARE }
                    val runas = 7; val paso = 30f; val x0 = cx - (runas - 1) * paso / 2
                    flancos((a + b) / 2, (runas - 1) * paso / 2 + 26f, L.trazo(L.linea, 2f))
                    for (k in 0 until runas) {
                        val x = x0 + k * paso
                        c.drawLine(x, a, x, b, pa)
                        when (k % 4) {
                            0 -> { c.drawLine(x, a, x + 9f, a + 9f, pa); c.drawLine(x, a + 12f, x + 9f, a + 21f, pa) }
                            1 -> c.drawLine(x - 8f, a + 6f, x + 8f, a + 18f, pa)
                            2 -> { c.drawLine(x, a + 8f, x - 9f, a + 16f, pa); c.drawLine(x, a + 8f, x + 9f, a + 16f, pa) }
                            3 -> { c.drawLine(x, b - 10f, x + 9f, b - 19f, pa); c.drawLine(x, a, x - 8f, a + 8f, pa) }
                        }
                    }
                    y += 50f
                }
            }
        }

        fun encabezado(titulo: String, latin: String?, rango: String) {
            c.drawColor(L.fondo)
            y = 92f
            val pm = L.p(L.tenue, 26f, L.titulo, 0.2f)
            c.drawText(recortar(marcaReporte(L.tema), pm, ancho), M, y, pm); y += 30f
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
            val pe = L.p(L.tinta, 22f, L.texto); val pv = L.pc(L.tenue, 20f)
            l.forEach { (et, v, txt) ->
                val etq = recortar(et, pe, 300f)
                c.drawText(etq, M, y + 26f, pe)
                val bx = M + 320f; val bwMax = W - M - bx - 150f
                val bw = (bwMax * v / max).toFloat().coerceAtLeast(6f)
                c.barraGrafica(eg, RectF(bx, y + 8f, bx + bw, y + 30f), L.relleno(color), horizontal = true)
                c.drawText(txt, bx + bw + 12f, y + 27f, pv)
                y += 40f
            }
            y += 8f
        }

        /** Ánimo día por día: valencia (color del tema) y activación (secundario). */
        fun graficaAnimo(d: Periodo.Datos) {
            val gx0 = M + 34f; val gx1 = W - M; val gy0 = y + 10f; val gy1 = y + 260f
            val ej = L.trazo(L.linea, 1.6f)
            c.rejillaGrafica(eg, (1..7).map { v -> gy1 - (v - 1) / 6f * (gy1 - gy0) }, ej, RectF(gx0, gy0, gx1, gy1))
            val pcEje = L.pc(L.tenue, 18f)
            for (v in 1..7) {
                val yy = gy1 - (v - 1) / 6f * (gy1 - gy0)
                if (v % 2 == 1) c.drawText("$v", M, yy + 8f, pcEje)
            }
            val n = d.fechas.size
            fun x(i: Int) = if (n == 1) (gx0 + gx1) / 2 else gx0 + i * (gx1 - gx0) / (n - 1)
            fun serie(vals: List<Double?>, color: Int, puntos: Boolean) {
                val path = Path(); var abierto = false
                vals.forEachIndexed { i, v ->
                    if (v == null) { abierto = false; return@forEachIndexed }
                    val yy = gy1 - ((v - 1) / 6.0 * (gy1 - gy0)).toFloat()
                    if (!abierto) { path.moveTo(x(i), yy); abierto = true } else path.lineTo(x(i), yy)
                    if (puntos && n <= 40) c.puntoGrafica(eg, x(i), yy, 6f, L.relleno(L.colorAnimo(v)))
                }
                c.lineaGrafica(eg, path, L.trazo(color, if (n > 60) 2.5f else 4f))
            }
            serie(d.activacion, L.terciario, false); serie(d.valencia, L.primario, true)
            // Los nombres de los días siguen con su letra; los números de día, con la de las cifras.
            val pe = if (n <= 8) L.p(L.tenue, 18f, L.mono) else L.pc(L.tenue, 18f)
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
            meta?.let { val yy = base - (it / max * alto).toFloat(); c.lineaMeta(eg, gx0, yy, gx1, yy, L.trazo(L.linea, 2f)) }
            vals.forEachIndexed { i, v ->
                v ?: return@forEachIndexed
                val h = (v / max * alto).toFloat()
                c.barraGrafica(eg, RectF(gx0 + i * bw + bw * 0.18f, base - h, gx0 + (i + 1) * bw - bw * 0.18f, base),
                    L.relleno(if (meta != null && v < meta * 0.9) L.mezcla(L.error, L.linea, 0.3) else L.primario))
                if (n <= 8) c.drawText("%.1f".format(v), gx0 + i * bw + bw / 2 - 20f, base - h - 8f, L.pc(L.tinta, 18f))
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
            // ---- diario: hojas apiladas como un cuaderno; el día que más escribiste llega a diez hojas
            if (dias.any { it.palabras > 0 }) {
                val maxPal = dias.maxOf { it.palabras }
                val porHoja = maxOf(10, (maxPal + 9) / 10)
                rotulo("páginas escritas · una hoja = $porHoja palabras")
                val maxHojas = ((maxPal + porHoja - 1) / porHoja).coerceIn(1, 10)
                val alto = maxHojas * 9.5f + 14f; val base = y + alto
                dias.forEachIndexed { i, d ->
                    val hojas = if (d.palabras == 0) 0 else ((d.palabras + porHoja - 1) / porHoja).coerceIn(1, 10)
                    val o = cx(i); val hw = col * 0.3f
                    for (k in 0 until hojas) {
                        val yy = base - (k + 1) * 9.5f; val dx = if (k % 2 == 0) -1.5f else 1.5f
                        c.drawRoundRect(RectF(o - hw + dx, yy, o + hw + dx, yy + 8f), 2f, 2f, L.relleno(L.mezcla(L.superficie, L.fondo, 0.2)))
                        c.drawRoundRect(RectF(o - hw + dx, yy, o + hw + dx, yy + 8f), 2f, 2f, L.trazo(L.mezcla(L.linea, L.tinta, 0.3), 1.4f))
                    }
                    if (hojas > 0) c.drawRect(RectF(o - hw * 0.6f, base - hojas * 9.5f - 4f, o - hw * 0.45f, base - hojas * 9.5f + 6f), L.relleno(L.primario))
                    val tv = if (d.palabras >= 1000) "%.1fk".format(d.palabras / 1000.0) else "${d.palabras}"
                    val pv = L.pc(L.tenue, 18f)
                    if (d.palabras > 0) c.drawText(tv, o - pv.measureText(tv) / 2, base + 24f, pv)
                }
                y = base + 36f
            }
            // ---- sueño: barras con su luna
            if (dias.any { it.suenoH != null }) {
                rotulo("sueño")
                val alto = 110f; val base = y + alto
                c.lineaMeta(eg, M, base - alto * 7f / 10f, W - M, base - alto * 7f / 10f, L.trazo(L.linea, 1.2f))
                dias.forEachIndexed { i, d ->
                    val h0 = d.suenoH ?: return@forEachIndexed
                    val o = cx(i); val bw = col * 0.24f
                    val hh = (h0.coerceIn(0.0, 10.0) / 10.0 * alto).toFloat()
                    c.barraGrafica(eg, RectF(o - bw, base - hh, o + bw, base), L.relleno(if (h0 < 6.3) L.mezcla(L.error, L.linea, 0.3) else L.secundario))
                    // Media luna arriba de la barra.
                    val lr = bw * 0.75f; val ly = base - hh - lr - 6f
                    c.drawCircle(o, ly, lr, L.relleno(L.terciario)); c.drawCircle(o + lr * 0.45f, ly - lr * 0.2f, lr * 0.85f, L.relleno(L.fondo))
                    val tv = "%.1f".format(h0); val pv = L.pc(L.tenue, 18f)
                    c.drawText(tv, o - pv.measureText(tv) / 2, base + 24f, pv)
                }
                y = base + 36f
            }
            // ---- pasos: una huella por cada 2 000, que van subiendo
            if (dias.any { (it.pasos ?: 0) > 0 }) {
                rotulo("pasos · una huella = 2,000")
                val filas = 5; val base = y + filas * 26f + 6f
                dias.forEachIndexed { i, d ->
                    val p0 = d.pasos ?: return@forEachIndexed
                    val k = ((p0 + 1999) / 2000).toInt().coerceIn(0, 10)
                    val o = cx(i)
                    for (j in 0 until k) {
                        val izq = j % 2 == 0; val fila = j / 2
                        val xx = o + (if (izq) -11f else 11f); val yy = base - fila * 26f - (if (izq) 0f else 12f)
                        c.drawOval(RectF(xx - 6f, yy - 11f, xx + 6f, yy + 5f), L.relleno(L.primario))
                        c.drawOval(RectF(xx - 4.5f, yy + 7f, xx + 4.5f, yy + 13f), L.relleno(L.primario))
                    }
                    val tv = if (p0 >= 1000) "%.1fk".format(p0 / 1000.0) else "$p0"; val pv = L.pc(L.tenue, 18f)
                    c.drawText(tv, o - pv.measureText(tv) / 2, base + 40f, pv)
                }
                y = base + 54f
            }
            // ---- momentos: un punto por cada uno
            if (dias.any { it.momentos > 0 }) {
                rotulo("momentos")
                dias.forEachIndexed { i, d ->
                    val k = d.momentos.coerceAtMost(12); val o = cx(i)
                    for (j in 0 until k) {
                        val fila = j / 4; val pos = j % 4
                        c.puntoGrafica(eg, o - 21f + pos * 14f, y + 8f + fila * 14f, 5f, L.relleno(L.terciario))
                    }
                }
                y += 14f * ((dias.maxOf { it.momentos.coerceAtMost(12) } + 3) / 4) + 16f
            }
            y += 8f
        }

        fun rotulo(t: String) { c.drawText(t, M, y + 20f, L.p(L.tenue, 19f, L.texto, 0.04f)); y += 32f }

        /** El mes como calendario: cada día del color de su ánimo (el hueco punteado es un día sin registro). */
        fun calendarioMes(d: Periodo.Datos) {
            val ini = Fechas.parse(d.ini); val dm = ini.lengthOfMonth()
            val desfase = ini.dayOfWeek.value - 1
            val col = ancho / 7f; val alto = col * 0.74f; val g = 8f
            val pd = L.p(L.tenue, 19f, L.texto)
            for (i in 0 until 7) { val t = Estilo.semana[i]; c.drawText(t, M + col * i + col / 2 - pd.measureText(t) / 2, y + 18f, pd) }
            y += 32f
            val filas = (desfase + dm + 6) / 7
            for (dd in 1..dm) {
                val k = desfase + dd - 1; val fx = M + (k % 7) * col; val fy = y + (k / 7) * (alto + g)
                val f = "%s-%02d".format(d.ini.take(7), dd)
                val i = d.fechas.indexOf(f); val v = if (i >= 0) d.valencia[i] else null
                val r = RectF(fx + g / 2, fy, fx + col - g / 2, fy + alto)
                val pn: TextPaint
                if (v != null) {
                    c.drawRoundRect(r, 10f, 10f, L.relleno(L.colorAnimo(v)))
                    pn = L.p(L.fondo, 26f, L.cifra)
                    val tv = n1(v); val pv = L.pc(L.fondo, 18f)
                    c.drawText(tv, r.right - pv.measureText(tv) - 10f, r.bottom - 10f, pv)
                } else {
                    c.drawRoundRect(r, 10f, 10f, L.trazo(L.linea, 2f).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(6f, 6f), 0f) })
                    pn = L.p(L.tenue, 26f, L.cifra)
                }
                c.drawText("$dd", r.left + 10f, r.top + 30f, pn)
            }
            y += filas * (alto + g) + 6f
            // Leyenda: la escala de colores del ánimo.
            val lw = 260f; val lx = W - M - lw
            for (k in 0 until 26) c.drawRect(lx + k * lw / 26, y, lx + (k + 1) * lw / 26 + 1, y + 12f, L.relleno(L.colorAnimo(1 + 6.0 * k / 25)))
            val pl = L.pc(L.tenue, 17f)
            c.drawText("1", lx - 18f, y + 12f, pl); c.drawText("7", lx + lw + 8f, y + 12f, pl)
            c.drawText("ánimo del día", M, y + 12f, L.p(L.tenue, 18f, L.texto))
            y += 34f
        }

        /** La curva del sueño del mes, con la línea de las 7 h y la media del mes anterior. */
        fun curvaSueno(d: Periodo.Datos) {
            val vals = d.suenoPorDia; if (vals.count { it != null } < 2) return
            val gx0 = M + 34f; val gx1 = W - M; val gy0 = y + 6f; val gy1 = y + 210f
            val lo = 3.0; val hi = maxOf(10.0, vals.filterNotNull().max() + 0.5)
            fun yy(v: Double) = gy1 - ((v.coerceIn(lo, hi) - lo) / (hi - lo) * (gy1 - gy0)).toFloat()
            val n = vals.size
            fun x(i: Int) = if (n == 1) (gx0 + gx1) / 2 else gx0 + i * (gx1 - gx0) / (n - 1)
            val pe = L.pc(L.tenue, 17f)
            val guias = listOf(4, 6, 8, 10).filter { it <= hi }
            c.rejillaGrafica(eg, guias.map { yy(it.toDouble()) }, L.trazo(L.mezcla(L.linea, L.fondo, 0.4), 1.2f), RectF(gx0, gy0, gx1, gy1))
            guias.forEach { h0 -> c.drawText("$h0", M, yy(h0.toDouble()) + 6f, pe) }
            // Las dos referencias con el guion de referencia del tema; se distinguen por el color (y la leyenda de abajo).
            c.lineaMeta(eg, gx0, yy(7.0), gx1, yy(7.0), L.trazo(L.linea, 2f))
            // El mes anterior conserva su punteado propio: así no se confunde con la línea de 7 h aunque el tema la puntee.
            d.suenoAnterior?.let { a -> c.drawLine(gx0, yy(a), gx1, yy(a), L.trazo(L.mezcla(L.terciario, L.fondo, 0.35), 2f).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(3f, 7f), 0f) }) }
            // Relleno bajo la curva y la curva.
            val linea = Path(); val area = Path(); var abierto = false; var primero = 0f; var ultimo = 0f
            fun cerrar() { if (abierto) { area.lineTo(ultimo, gy1); area.lineTo(primero, gy1); area.close() }; abierto = false }
            vals.forEachIndexed { i, v ->
                if (v == null) { cerrar(); return@forEachIndexed }
                val px = x(i); val py = yy(v)
                if (!abierto) { linea.moveTo(px, py); area.moveTo(px, py); primero = px; abierto = true } else { linea.lineTo(px, py); area.lineTo(px, py) }
                ultimo = px
            }
            cerrar()
            c.drawPath(area, L.relleno(L.mezcla(L.secundario, L.fondo, 0.78)))
            c.lineaGrafica(eg, linea, L.trazo(L.secundario, 3.5f))
            vals.forEachIndexed { i, v -> if (v != null && v < 6.3) c.puntoGrafica(eg, x(i), yy(v), 5f, L.relleno(L.mezcla(L.error, L.linea, 0.3))) }
            for (i in vals.indices step 5) { val t = "${i + 1}"; c.drawText(t, x(i) - pe.measureText(t) / 2, gy1 + 26f, pe) }
            y = gy1 + 46f
            c.lineaMeta(eg, M, y - 6f, M + 34f, y - 6f, L.trazo(L.linea, 2f))
            c.drawText("7 h", M + 44f, y, L.p(L.tenue, 18f, L.texto))
            if (d.suenoAnterior != null) {
                c.drawLine(M + 140f, y - 6f, M + 174f, y - 6f, L.trazo(L.mezcla(L.terciario, L.fondo, 0.35), 2f).apply { pathEffect = android.graphics.DashPathEffect(floatArrayOf(3f, 7f), 0f) })
                c.drawText("media del mes anterior (${n1(d.suenoAnterior)} h)", M + 184f, y, L.p(L.tenue, 18f, L.texto))
            }
            y += 26f
        }

        /**
         * Este mes frente al anterior: el punto hueco (el mes anterior) queda al centro y el lleno se corre según el cambio,
         * hasta ±50 % en los bordes; así un cambio pequeño se ve pequeño en todas las medidas.
         */
        fun comparacion(filas: List<Triple<String, Double, Double>>, formato: List<(Double) -> String>) {
            if (filas.isEmpty()) return
            val pe = L.p(L.tinta, 22f, L.texto); val pv = L.pc(L.tenue, 19f)
            val textos = filas.mapIndexed { i, (_, ahora, antes) -> "${formato[i](antes)} → ${formato[i](ahora)}" }
            val bx = M + 150f; val bw = W - M - bx - (textos.maxOf { pv.measureText(it) } + 30f)
            val cx = bx + bw / 2
            val pm = L.p(L.tenue, 16f, L.mono); val pmc = L.pc(L.tenue, 16f)
            c.drawText("−50 %", bx - 4f, y + 12f, pmc); c.drawText("+50 %", bx + bw - pmc.measureText("+50 %"), y + 12f, pmc)
            val t0 = "igual"; c.drawText(t0, cx - pm.measureText(t0) / 2, y + 12f, pm)
            y += 20f
            filas.forEachIndexed { i, (et, ahora, antes) ->
                val pct = if (antes > 0) ((ahora - antes) / antes).coerceIn(-0.5, 0.5) else 0.0
                val xn = cx + (pct / 0.5 * bw / 2).toFloat()
                val yy = y + 20f
                c.drawText(et, M, yy + 7f, pe)
                c.ejeGrafica(eg, bx, yy, bx + bw, yy, L.trazo(L.mezcla(L.linea, L.fondo, 0.4), 2f))
                c.drawLine(cx, yy - 10f, cx, yy + 10f, L.trazo(L.linea, 1.5f))
                if (xn != cx) c.lineaGrafica(eg, Path().apply { moveTo(cx, yy); lineTo(xn, yy) }, L.trazo(L.primario, 6f))
                c.puntoGrafica(eg, cx, yy, 9f, L.relleno(L.fondo)); c.puntoGrafica(eg, cx, yy, 9f, L.trazo(L.tenue, 2.5f))
                c.puntoGrafica(eg, xn, yy, 11f, L.relleno(L.primario))
                c.drawText(textos[i], W - M - pv.measureText(textos[i]), yy + 7f, pv)
                y += 46f
            }
            // La leyenda repite la forma de los puntos (hueco: mes anterior; lleno: este mes).
            c.puntoGrafica(eg, M + 8f, y + 4f, 7f, L.trazo(L.tenue, 2f)); c.drawText("mes anterior", M + 24f, y + 10f, L.p(L.tenue, 18f, L.texto))
            c.puntoGrafica(eg, M + 190f, y + 4f, 8f, L.relleno(L.primario)); c.drawText("este mes", M + 206f, y + 10f, L.p(L.tenue, 18f, L.texto))
            y += 34f
        }

        /** Etiquetas que fluyen en renglones; más grandes cuanto más aparecen. */
        fun nube(items: List<Pair<String, Int>>, color: Int) {
            if (items.isEmpty()) return
            val max = items.maxOf { it.second }.toFloat(); val min = items.minOf { it.second }.toFloat()
            var x = M; var alto = 0f; val g = 12f
            items.forEach { (t, k) ->
                val tam = if (max == min) 28f else 22f + 18f * (k - min) / (max - min)
                val p = L.p(color, tam, L.titulo)
                val et = "$t · $k"; val w = p.measureText(et) + 28f; val h = tam + 18f
                if (x + w > W - M && x > M) { x = M; y += alto + g; alto = 0f }
                c.drawRoundRect(RectF(x, y, x + w, y + h), h / 2, h / 2, L.relleno(L.superficie2))
                c.drawText(et, x + 14f, y + h / 2 + tam * 0.34f, p)
                x += w + g; alto = maxOf(alto, h)
            }
            y += alto + 22f
        }

        /** Los lugares del mes como puntos sobre una cuadrícula (sin calles ni nombres): tamaño = horas, color = tipo. */
        fun mapaLugares(pts: List<Triple<Double, Double, Pair<String, Double>>>, horasTipo: Map<String, Double>) {
            if (pts.isEmpty()) return
            val alto = 440f; val x0 = M; val x1 = W - M; val y0 = y; val y1 = y + alto
            c.drawRoundRect(RectF(x0, y0, x1, y1), 14f, 14f, L.relleno(L.superficie2))
            val cuad = L.trazo(L.mezcla(L.linea, L.superficie2, 0.5), 1.2f)
            c.rejillaGrafica(eg, (1 until 4).map { k -> y0 + alto * k / 4 }, cuad, RectF(x0 + 10f, y0 + 10f, x1 - 10f, y1 - 10f),
                xs = (1 until 8).map { k -> x0 + (x1 - x0) * k / 8 })
            val lat0 = pts.minOf { it.first }; val lat1 = pts.maxOf { it.first }
            val lon0 = pts.minOf { it.second }; val lon1 = pts.maxOf { it.second }
            val k = kotlin.math.cos(Math.toRadians((lat0 + lat1) / 2))
            val sx = (lon1 - lon0) * k; val sy = lat1 - lat0
            val pad = 60f; val aw = x1 - x0 - 2 * pad; val ah = alto - 2 * pad
            val esc = if (sx <= 0 && sy <= 0) 0.0 else minOf(if (sx > 0) aw / sx else Double.MAX_VALUE, if (sy > 0) ah / sy else Double.MAX_VALUE)
            val cx = (x0 + x1) / 2; val cy = (y0 + y1) / 2
            val maxH = pts.maxOf { it.third.second }.coerceAtLeast(0.1)
            pts.sortedByDescending { it.third.second }.forEach { (la, lo, th) ->
                val px = cx + (((lo - (lon0 + lon1) / 2) * k) * esc).toFloat()
                val py = cy - (((la - (lat0 + lat1) / 2)) * esc).toFloat()
                val r = 8f + 34f * kotlin.math.sqrt(th.second / maxH).toFloat()
                val col = colorTipo(th.first)
                c.drawCircle(px, py, r, L.relleno(L.mezcla(col, L.superficie2, 0.45)))
                c.drawCircle(px, py, r, L.trazo(col, 3f))
                // Las burbujas siguen redondas (su área es la medida: horas); el centro lleva el punto del tema.
                c.puntoGrafica(eg, px, py, 4f, L.relleno(col))
            }
            y = y1 + 20f
            // Leyenda: horas por tipo de lugar.
            horasTipo.entries.take(8).forEachIndexed { i, (t, hh) ->
                val lx = M + (i % 2) * (ancho / 2); val ly = y + (i / 2) * 34f
                c.drawCircle(lx + 9f, ly + 12f, 9f, L.relleno(colorTipo(t)))
                c.drawText("${Catalogo.nombreTipoLugar(t)} · ${n1(hh)} h", lx + 28f, ly + 19f, L.p(L.tinta, 21f, L.texto))
            }
            y += ((minOf(horasTipo.size, 8) + 1) / 2) * 34f + 14f
        }

        fun colorTipo(t: String): Int {
            val base = listOf(L.primario, L.secundario, L.terciario, L.mezcla(L.primario, L.tinta, 0.45), L.mezcla(L.secundario, L.tinta, 0.45),
                L.mezcla(L.terciario, L.tinta, 0.45), L.mezcla(L.primario, L.fondo, 0.45), L.mezcla(L.secundario, L.fondo, 0.45), L.mezcla(L.terciario, L.fondo, 0.45), L.tenue)
            val i = Catalogo.TIPOS_LUGAR.indexOfFirst { it.id == t }.let { if (it < 0) Catalogo.TIPOS_LUGAR.size - 1 else it }
            return base[i % base.size]
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

    /** El nombre del mes en la voz del tema. */
    private fun mesNombre(m: Int) = Estilo.mes(m)
    private fun n0(x: Double) = "%,.0f".format(x)
    private fun n1(x: Double) = "%.1f".format(x)
    private fun hm(min: Double) = "${(min / 60).toInt()} h ${(min % 60).toInt()} min"
    private fun hora(h: Double) = "%02d:%02d".format(((h % 24) + 24).toInt() % 24, ((h - h.toInt()) * 60).roundToInt().coerceIn(0, 59))
    private fun dinero(x: Double) = "$" + n0(x)
    private fun inicial(s: String) = s.split('_', ' ', '-').filter { it.isNotBlank() }.joinToString("") { it.take(1).uppercase() + "." }
    private fun persona(p: String, discreto: Boolean) = if (discreto) inicial(p) else "@$p"
    /** [se]: error típico de la diferencia ([Periodo.errorDif]); si la diferencia es menor, se dice «parecido» (H-8-11). */
    private fun cambio(a: Double?, b: Double?, unidad: String, dec: Boolean = true, se: Double? = null): String? {
        if (a == null || b == null) return null
        val d = a - b
        if (abs(d) < (if (dec) 0.05 else 0.5)) return "igual que el periodo anterior"
        if (se != null && abs(d) < se) return "parecido al periodo anterior (la diferencia cabe en lo que varían tus días)"
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
    private fun cuerpo(h: Hoja, d: Periodo.Datos, anual: Boolean, logros: List<io.github.ruddy2003.vestigium.ciencia.Logros.Logro>, mensual: Boolean = false) {
        val L = h.L
        // Los lugares guardados: horas por tipo y el ánimo en cada tipo (nunca lo privado).
        val horasTipo = runCatching { Lugares.horasPorTipo(d.ini, d.fin) }.getOrDefault(emptyMap()).filterValues { it >= 0.5 }
        val valTipo = runCatching { Lugares.valenciaPorTipo(d.ini, d.fin) }.getOrDefault(emptyMap()).filterValues { it.second >= 2 }
        h.cifras(listOfNotNull(
            "${d.diasDiario}/${d.n}" to "días con diario",
            d.valMedia?.let { n1(it) to ("ánimo medio del diario (de 7)" + if (d.diasSoloAvisos > 0) "; ${d.diasSoloAvisos} días solo con avisos no cuentan" else "") },
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

        // ---- El mes de un vistazo (solo en el reporte mensual)
        if (mensual) {
            h.titulo("El mes de un vistazo", "MENSIS PICTVS")
            if (d.valencia.any { it != null }) { h.rotulo("el calendario de tu ánimo"); h.calendarioMes(d) }
            if (d.suenoPorDia.count { it != null } >= 2) { h.rotulo("las horas de sueño, noche a noche"); h.curvaSueno(d) }
            val comp = listOfNotNull(
                if (d.valMedia != null && d.valAnterior != null) Triple("ánimo", d.valMedia!!, d.valAnterior!!) to { v: Double -> n1(v) } else null,
                if (d.suenoH != null && d.suenoAnterior != null) Triple("sueño", d.suenoH!!, d.suenoAnterior!!) to { v: Double -> n1(v) + " h" } else null,
                if (d.pasos != null && d.pasosAnterior != null) Triple("pasos", d.pasos!!, d.pasosAnterior!!) to { v: Double -> n0(v) } else null,
                if (d.pantallaMin != null && d.pantallaAnterior != null) Triple("pantalla", d.pantallaMin!!, d.pantallaAnterior!!) to { v: Double -> hm(v) } else null,
            )
            if (comp.isNotEmpty()) { h.rotulo("frente al mes anterior"); h.comparacion(comp.map { it.first }, comp.map { it.second }) }
            if (d.personas.isNotEmpty()) { h.rotulo("con quién"); h.nube(d.personas.take(10).map { persona(it.first, h.discreto) to it.second }, L.primario) }
            if (d.temas.isNotEmpty()) { h.rotulo("de qué"); h.nube(d.temas.take(10).map { "#" + it.first to it.second }, L.secundario) }
            val pts = lugaresDelMes(d.ini, d.fin)
            if (pts.isNotEmpty()) { h.rotulo("dónde pasaste el mes · cada punto es un lugar guardado"); h.mapaLugares(pts, horasTipo) }
        }

        // ---- Ánimo
        if (d.valencia.any { it != null }) {
            h.titulo("Ánimo", "ANIMVS")
            if (anual) pixeles(h, d) else h.graficaAnimo(d)
            listOfNotNull(
                d.mejorDia?.let { "optimus" to Fechas.titulo(it).lowercase() + (d.mejorMomento?.takeIf { !h.sinFrases }?.let { m -> " · «${m.take(90)}»" } ?: "") },
                d.peorDia?.takeIf { it != d.mejorDia }?.let { "pessimus" to Fechas.titulo(it).lowercase() },
                cambio(d.valMedia, d.valAnterior, "de ánimo", se = d.errorAnterior["valencia"])?.let { "animus" to it },
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
                cambio(d.suenoH, d.suenoAnterior, "h de sueño", se = d.errorAnterior["sueno"])?.let { "somnus" to it },
                d.pasos?.let { "passus" to "${n0(it)} al día · ${n0(d.pasosTotal.toDouble())} en total · unos ${n0(d.km)} km" },
                cambio(d.pasos, d.pasosAnterior, "pasos al día", false, d.errorAnterior["pasos"])?.let { "passus" to it },
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
        if (d.ciudades.isNotEmpty() || d.fotos > 0 || d.tempMax != null || horasTipo.isNotEmpty()) {
            h.titulo("Mundo", "ORBIS")
            if (horasTipo.isNotEmpty() && !mensual) h.barras(horasTipo.entries.take(6).map { (t, hh) -> Triple(Catalogo.nombreTipoLugar(t), hh, n1(hh) + " h") }, L.secundario)
            valTipo.entries.sortedByDescending { it.value.first }.takeIf { it.isNotEmpty() }?.let { l ->
                h.clave("genius loci", "ánimo por tipo de lugar: " + l.take(5).joinToString(" · ") { (t, v) -> "${Catalogo.nombreTipoLugar(t).lowercase()} ${n1(v.first)}" })
            }
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
                "telephonum" to hm(d.pantallaMin) + " al día" + (cambio(d.pantallaMin, d.pantallaAnterior, "min", false, d.errorAnterior["pantalla"])?.let { " · $it" } ?: ""),
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

    /** Cada lugar guardado con visitas en el periodo: lat, lon, tipo y horas (solo para el mapa abstracto del mes). */
    private fun lugaresDelMes(ini: String, fin: String): List<Triple<Double, Double, Pair<String, Double>>> {
        val ahora = System.currentTimeMillis()
        val a0 = Fechas.inicioDia(ini) + 5 * 3_600_000L; val z0 = Fechas.finDia(fin) + 5 * 3_600_000L
        val horas = HashMap<String, Double>()
        Repo.visitas.forEach { v ->
            val a = maxOf(Fechas.millis(v.entrada), a0); val z = minOf(v.salida?.let { Fechas.millis(it) } ?: ahora, z0)
            if (a > 0 && z > a) horas[v.lugar] = (horas[v.lugar] ?: 0.0) + (z - a) / 3_600_000.0
        }
        return Repo.lugares.mapNotNull { l -> horas[l.id]?.takeIf { it >= 0.25 }?.let { Triple(l.lat, l.lon, l.tipo to it) } }
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
            c.barraGrafica(h.eg, RectF(x + 8f, h.y + altoB - hh, x + anchoM - 8f, h.y + altoB), L.relleno(L.colorAnimo(v)))
            c.drawText(n1(v), x + 10f, h.y + altoB - hh - 8f, L.pc(L.tinta, 17f))
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

    /**
     * [automatico]: el que se guarda solo (lunes y día 1) en Imágenes y Descargas, al alcance de la galería y de su copia a la nube.
     * Ese va siempre en modo discreto (iniciales en vez de nombres) y sin la frase del mejor momento.
     */
    private fun reporte(ctx: Context, titulo: String, latin: String, rango: String, ini: String, fin: String, anual: Boolean, mensual: Boolean = false,
                        automatico: Boolean = false): Triple<Picture, Int, Int> {
        val L = Lienzo(ctx)
        val d = Periodo.calcular(ctx, ini, fin)
        val logros = runCatching { io.github.ruddy2003.vestigium.ciencia.Logros.todos(ctx).filter { l -> l.conseguido?.let { it in ini..fin } == true } }.getOrDefault(emptyList())
        val (pic, alto) = dibujarMedido(ctx) { c ->
            val h = Hoja(c, L, Repo.config.discreto || automatico, sinFrases = automatico)
            h.encabezado(titulo, latin, rango)
            cuerpo(h, d, anual, logros, mensual)
            h.pie(fin)
            h.y
        }
        return Triple(pic, alto, L.fondo)
    }

    /** La imagen del reporte semanal (sin guardarla). */
    fun imagenSemana(ctx: Context, lunes: String, automatico: Boolean = false): Bitmap {
        val fin = Fechas.sumar(lunes, 6)
        val ini = Fechas.parse(lunes); val f = Fechas.parse(fin)
        val semana = f.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val rango = "Semana ${Estilo.numeral(semana)} · del ${ini.dayOfMonth} de ${mesNombre(ini.monthValue)} al ${f.dayOfMonth} de ${mesNombre(f.monthValue)} de ${f.year}"
        val (pic, alto, fondo) = reporte(ctx, "Tu semana", "HEBDOMAS", rango, lunes, fin, false, automatico = automatico)
        return bitmap(pic, alto, fondo)
    }

    /** La imagen del reporte de un mes (sin guardarla); «ym» es «AAAA-MM». */
    fun imagenMes(ctx: Context, ym: String): Bitmap {
        val ini = "$ym-01"; val d = Fechas.parse(ini)
        val fin = minOf(Fechas.iso(d.plusMonths(1).minusDays(1)), Fechas.diaLogico())
        val (pic, alto, fondo) = reporte(ctx, "Tu mes", "MENSIS", Estilo.mes(d.monthValue, d.year), ini, fin, false, mensual = true)
        return bitmap(pic, alto, fondo)
    }

    /** La imagen del reporte de un año (sin guardarla). */
    fun imagenAnio(ctx: Context, anio: Int): Bitmap {
        val fin = minOf("$anio-12-31", Fechas.diaLogico())
        val (pic, alto, fondo) = reporte(ctx, "Tu año", "ANNVS", "$anio", "$anio-01-01", fin, true)
        return bitmap(pic, alto, fondo)
    }

    fun semanal(ctx: Context, lunes: String, avisar: Boolean): Uri? {
        val uri = guardarPng(ctx, "semana_$lunes.png", imagenSemana(ctx, lunes, automatico = avisar))
        if (avisar && uri != null) Avisos.reporte(ctx, "Tu reporte semanal está listo", uri, "image/png")
        return uri
    }

    fun mensual(ctx: Context, ym: String, avisar: Boolean): Uri? {
        val ini = "$ym-01"
        val d = Fechas.parse(ini)
        val fin = Fechas.iso(d.plusMonths(1).minusDays(1))
        val (pic, alto, fondo) = reporte(ctx, "Tu mes", "MENSIS", Estilo.mes(d.monthValue, d.year), ini, fin, false, mensual = true, automatico = avisar)
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
