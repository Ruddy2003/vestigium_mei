package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil

/**
 * Las entradas del estante de Arcontes ([MuebleEstanteArcontes]), tres cosas del círculo según la semilla, de tres siluetas, cada
 * una con su etiqueta en su sitio y a su manera: el códice (rígido: la tapa de cuero teñido como los de Nag Hammadi, con su grano y
 * sus roces, la tira del día pegada a media altura, la solapa en punta que viene de atrás y la correa enrollada con su pasador de
 * hueso), el rollo de pie (redondo: el papiro con su espiral, el umbilicus que asoma por los dos cabos, la camisa de tela teñida
 * con su punta caída, el cordel y la etiqueta colgada de un hilo, más abajo y ladeada) y el envoltorio (blando: un fardo de lana
 * teñida de hombros redondos con las dos franjas oscuras del manto del Ideario, el cordel con la bula de barro y la etiqueta atada
 * al nudo). El ánimo es el tinte del cuero, de la tela y de la lana ([tonoDeDiaArcontes]); sin ánimo, sin teñir. En los Sueños,
 * una media luna de plata con su disco apagado en la etiqueta.
 */
internal object VolumenEstanteArcontes {
    private val P = PaletaArcontes

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.34f, 0.7f, 0.38f, 0.06f, -0.22f, -0.48f, -0.72f)

    /** Los colores de un tinte con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(ti: Color?, sin: Color, t: TonoArcontes, brillo: Float = 0.3f, sombra: Float = 0.55f): Tinte {
        val m = ti ?: sin
        val claro = mezclaRapida(m, luzMueble(1f), brillo)
        val hondo = mezclaRapida(m, sombraMueble(1f), sombra)
        return if (ti != null) Tinte(t.animo(m), t.animo(claro), t.animo(hondo)) else Tinte(t.c(m), t.c(claro), t.c(hondo))
    }

    /**
     * La luz de un cuerpo redondo en un solo degradado blando (cada tono en el medio de su franja), para pintar de una vez una
     * forma cualquiera: el claro y el brillo del lado de la luz, la cara, y el hondo del otro lado.
     */
    private fun franjas(ti: Tinte, hacia: Float, x0: Float, x1: Float): Brush {
        val paradas = ArrayList<Pair<Float, Color>>()
        for (i in 0 until 7) {
            val m = TONOS[i]
            val c = if (m >= 0f) mezclaRapida(ti.cara, ti.claro, m) else mezclaRapida(ti.cara, ti.hondo, -m)
            val centro = (FRANJAS[i] + FRANJAS[i + 1]) / 2
            paradas += (if (hacia < 0f) centro else 1f - centro) to c
        }
        paradas.sortBy { it.first }
        return Brush.horizontalGradient(*paradas.toTypedArray(), startX = x0, endX = x1)
    }

    /** Una tapa casi plana: el claro del lado de la luz, la cara y el hondo del otro lado, sin cortes. */
    private fun plano(ti: Tinte, hacia: Float, x0: Float, x1: Float): Brush {
        val l = mezclaRapida(ti.cara, ti.claro, 0.35f); val o = mezclaRapida(ti.cara, ti.hondo, 0.55f)
        return Brush.horizontalGradient(if (hacia < 0f) listOf(l, ti.cara, ti.cara, o) else listOf(o, ti.cara, ti.cara, l), x0, x1)
    }

    // ---------------------------------------------------------------- el mes y el día

    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")
    private val ORDINALES = listOf("primero", "segundo", "tercero", "cuarto", "quinto", "sexto", "séptimo", "octavo", "noveno", "décimo", "undécimo", "duodécimo")

    /** El mes corto en español, como «nov.». Arcontes nombra los meses por su número («Mes undécimo»): se vuelve al número. */
    private fun mesCorto(mes: String): String {
        val m = mes.removePrefix("Mes ").substringBefore(" · ").trim().lowercase()
        val i = ORDINALES.indexOf(m)
        if (i >= 0) return MESES_CORTOS[i]
        m.toIntOrNull()?.let { n -> if (n in 1..12) return MESES_CORTOS[n - 1] }
        return if (m.length > 4) m.take(3) + "." else m
    }

    /**
     * El día y el mes, centrados en [r]. Las cajas tienen el alto real de Caudex (a 16 sp, el día ocupa 20.6 dp de alto con el aire
     * de arriba y de abajo; el mes, 11 dp): así salen a su tamaño, sin achicarse ni volver a medirse en cada dibujo. Se solapan en
     * ese aire, para que la etiqueta siga siendo de 30 dp.
     */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val hd = 21f * u; val hm = 11.5f * u; val solape = 5.6f * u
        val y0 = r.center.y - (hd + hm - solape) / 2
        texto(medidor, v.dia, Rect(r.left - 2f * u, y0, r.right + 2f * u, y0 + hd), tinta, DIA, FontWeight.Bold, 6f)
        texto(medidor, mesCorto(v.mes), Rect(r.left - 2f * u, y0 + hd - solape, r.right + 2f * u, y0 + hd - solape + hm), tinta.copy(alpha = 0.9f), MES, FontWeight.Normal, 5f)
    }

    /**
     * Un texto centrado en [r] con la tinta [tinta]: se mide siempre con el mismo color (el medidor guarda pocas medidas y el
     * color cuenta en ellas) y se pinta con el de la tinta, que de noche cambia según el papel.
     */
    private fun DrawScope.texto(medidor: TextMeasurer, s: String, r: Rect, tinta: Color, tam: Float, peso: FontWeight, minimo: Float) {
        val capa = capaEn(medidor, s, r, P.LETRA, Color.Black, tam, peso = peso, minimo = minimo) ?: return
        drawText(capa, color = tinta, topLeft = Offset(r.center.x - capa.size.width / 2f, r.center.y - capa.size.height / 2f))
    }

    // ---------------------------------------------------------------- lo compartido (armado una vez por tamaño)

    private var radioLuna = -1f
    private var caminoLuna = Path()

    private fun creciente(r: Float): Path {
        if (r != radioLuna) {
            val fuera = Path().apply { addOval(Rect(-r, -r, r, r)) }
            val dentro = Path().apply { addOval(Rect(-r * 0.8f + r * 0.48f, -r * 0.86f - r * 0.2f, r * 0.8f + r * 0.48f, r * 0.86f - r * 0.2f)) }
            caminoLuna = Path().apply { op(fuera, dentro, PathOperation.Difference) }
            radioLuna = r
        }
        return caminoLuna
    }

    private var densidadBaldosas = -1f
    private var baldosaGrano: ImageBitmap? = null
    private var baldosaSarga: ImageBitmap? = null

    private fun pintarBaldosa(ancho: Float, alto: Float, pintar: DrawScope.() -> Unit): ImageBitmap {
        val img = ImageBitmap(ceil(ancho).toInt().coerceAtLeast(1), ceil(alto).toInt().coerceAtLeast(1))
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ancho, alto)) { pintar() }
        return img
    }

    /**
     * Las texturas de las entradas, pintadas una vez por densidad en baldosas que luego solo se pegan: el grano del cuero (rayitas
     * cortas en todas direcciones) y la sarga de la lana (diagonales finas en luz y en sombra). Son neutras, para cualquier tinte.
     */
    private fun baldosas(u: Float) {
        if (u == densidadBaldosas) return
        baldosaGrano = pintarBaldosa(60f * u, 120f * u) {
            val p = Path(); val q = Path()
            for (j in 0 until 170) {
                val x = 60f * u * azar(8701, j); val y = 120f * u * azar(8702, j)
                val an = azar(8703, j) * 3.1416f
                val l = (1.1f + 1.6f * azar(8704, j)) * u
                (if (j % 4 == 0) q else p).apply { moveTo(x, y); lineTo(x + l * kotlin.math.cos(an), y + l * kotlin.math.sin(an)) }
            }
            drawPath(p, SOMBRA_MUEBLE.copy(alpha = 0.5f), style = Stroke(0.6f * u))
            drawPath(q, LUZ_MUEBLE.copy(alpha = 0.4f), style = Stroke(0.5f * u))
        }
        baldosaSarga = pintarBaldosa(48f * u, 96f * u) {
            val p = Path(); val q = Path()
            var x = -96f * u; var j = 0
            while (x < 48f * u && j < 200) {
                p.moveTo(x, 0f); p.lineTo(x + 96f * u, 96f * u)
                q.moveTo(x + 1.1f * u, 0f); q.lineTo(x + 1.1f * u + 96f * u, 96f * u)
                x += 2.4f * u; j++
            }
            drawPath(p, SOMBRA_MUEBLE.copy(alpha = 0.32f), style = Stroke(0.7f * u))
            drawPath(q, LUZ_MUEBLE.copy(alpha = 0.22f), style = Stroke(0.5f * u))
        }
        brochaGrano = baldosaGrano?.let { ShaderBrush(ImageShader(it, TileMode.Repeated, TileMode.Repeated)) }
        brochaSarga = baldosaSarga?.let { ShaderBrush(ImageShader(it, TileMode.Repeated, TileMode.Repeated)) }
        densidadBaldosas = u
    }

    private var brochaGrano: ShaderBrush? = null
    private var brochaSarga: ShaderBrush? = null

    /** El grano del cuero en [r]: la baldosa de rayitas repetida, corrida según la semilla (más barato que trazarlas una a una). */
    private fun DrawScope.grano(r: Rect, semilla: Int, fuerza: Float) {
        val b = brochaGrano ?: return
        trama(b, r, 0.55f * fuerza, 60f * density * azar(semilla, 7), 120f * density * azar(semilla, 8))
    }

    /** La sarga de la lana en [r]: la baldosa de diagonales repetida. */
    private fun DrawScope.sarga(r: Rect, fuerza: Float) {
        val b = brochaSarga ?: return
        trama(b, r, 0.65f * fuerza, 0f, 0f)
    }

    /**
     * La brocha de una baldosa repetida [b] sobre [r], corrida ([dx], [dy]): un solo rectángulo (pegar la baldosa trozo a trozo,
     * o recortarla, pesaba al grabar y al pintar).
     */
    private fun DrawScope.trama(b: ShaderBrush, r: Rect, alfa: Float, dx: Float, dy: Float) {
        if (r.width < 2f || r.height < 2f) return
        translate(-dx, -dy) { drawRect(b, Offset(r.left + dx, r.top + dy), r.size, alpha = alfa.coerceIn(0f, 1f)) }
    }

    /** La luna de los Sueños: el disco apagado y la media luna de plata encima, con su canto oscuro y su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, t: TonoArcontes) {
        val u = density
        val m = creciente(r)
        drawCircle(Color(0xFF3A4256).copy(alpha = 0.55f), r, c)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, t.s(0.5f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDDE4F0))
            drawPath(m, Color(0xFF2E3446), style = Stroke(0.8f * u))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(c.x - r * 0.6f, c.y - r * 0.16f))
    }

    /** Lo de todas: la sombra que dejan en la roca de detrás, por la rendija con la vecina, y la de contacto en la tabla. */
    private fun DrawScope.alrededor(t: TonoArcontes, hacia: Float, desde: Float, fuerza: Float = 1f) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        val fs = if (t.a.oscuro) fuerza * (0.3f + 0.9f * t.k).coerceAtMost(1f) else fuerza
        // Solo la tira que asoma por el lado de la sombra (lo demás lo tapa la propia entrada).
        drawRoundRect(t.s(0.3f * fs), Offset(if (hacia < 0f) w - 3f * u else -6f * u, desde + 4f * u), Size(9f * u, h - desde - 4f * u), CornerRadius(3f * u))
        if (hacia < 0f) drawRect(t.s(0.42f * fuerza), Offset(w, desde), Size(sep, h - desde))
        else drawRect(t.s(0.42f * fuerza), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(t.s(0.5f * fuerza), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche), el del ocaso en los Sueños claros y el roce frío de la luna. */
    private fun DrawScope.filos(cuerpo: Rect, t: TonoArcontes, hacia: Float) {
        val u = density
        if (!t.a.lumbre && t.sol <= 0.05f) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(t.filo(0.7f), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (t.a.oscuro && t.a.noche) drawRect(t.lunar(0.7f), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    /**
     * Una etiqueta de papiro (o pergamino) en [et], de borde deshilachado, con sus fibras, su sombra, el día, el mes y la luna. Va
     * derecha: el texto girado no aprovecha las letras ya pintadas y cuesta mucho más.
     */
    private fun DrawScope.etiqueta(v: Volumen, medidor: TextMeasurer, et: Rect, t: TonoArcontes, papel: Color, roido: Float = 0.03f, pegada: Boolean = false) {
        val u = density
        val forma = contornoIrregular(et, v.semilla, 20, roido)
        // Pegada, casi no deja sombra; colgada, se separa de lo de detrás.
        if (pegada) drawRect(t.s(0.25f), Offset(et.left + 0.5f * u, et.top + 0.7f * u), et.size)
        else drawRect(t.s(0.4f), Offset(et.left + 1.6f * u, et.top + 2f * u), et.size)
        val c = t.etiqueta(papel)
        drawPath(forma, c)
        val dentro = et.deflate(1.2f * u)
        val fibras = Path()
        var y = et.top + 2f * u; var j = 0
        while (y < dentro.bottom && j < 14) {
            fibras.moveTo(dentro.left, y); fibras.lineTo(dentro.right, y + (azar(v.semilla + 1, j) - 0.5f) * u)
            y += 2.6f * u; j++
        }
        // Las fibras y el canto, más hondos que el papel (de noche, lejos de la llama, el papel ya es oscuro: no lo aclaran).
        val tinta = t.tintaSobre(c)
        val fibra = if (tinta == t.tinta()) P.FIBRA else Color(c.red * 0.6f, c.green * 0.6f, c.blue * 0.6f)
        drawPath(fibras, fibra.copy(alpha = 0.22f), style = Stroke(0.45f * u))
        drawRect(fibra.copy(alpha = 0.16f), Offset(dentro.left, dentro.bottom - et.height * 0.14f), Size(dentro.width, et.height * 0.14f))
        drawPath(forma, mezclaRapida(c, fibra, 0.55f), style = Stroke(0.7f * u))
        diaYMes(v, medidor, et.deflate(1.4f * u), tinta)
        if (v.noche) luna(Offset(et.right - 3.2f * u, et.top + 3.4f * u), 3.2f * u, t)
    }

    /**
     * La bula de barro del nudo, a su tamaño (unos 10 dp): el disco con su sombra, el surco del sello en anillo abierto y el labio
     * en luz. A este tamaño el ouroboros entero no se distingue y cuesta mucho pintarlo.
     */
    private fun DrawScope.bula(c: Offset, r: Float, barro: Color, t: TonoArcontes) {
        drawCircle(t.s(0.4f), r, c + Offset(r * 0.15f, r * 0.25f))
        drawCircle(barro, r, c)
        val hondo = mezclaRapida(barro, Color.Black, 0.42f)
        drawArc(hondo, 300f, 290f, false, Offset(c.x - r * 0.55f, c.y - r * 0.55f), Size(r * 1.1f, r * 1.1f), style = Stroke(r * 0.22f))
        drawCircle(hondo, r * 0.14f, c)
        drawArc(t.b(0.45f), 200f, 70f, false, Offset(c.x - r * 0.85f, c.y - r * 0.85f), Size(r * 1.7f, r * 1.7f), style = Stroke(r * 0.12f))
    }

    // ---------------------------------------------------------------- la etiqueta de cada clase

    /** La etiqueta: alto (cabe el día a 16 sp y el mes sin achicarse) y ancho, al menos [ET_MIN] dp o tres cuartos de la entrada. */
    private const val ET_ALTO = 27f
    private const val ET_MIN = 22f

    private fun DrawScope.anchoEtiqueta(): Float = maxOf(ET_MIN * density, size.width * 0.74f).coerceAtMost(size.width * 0.96f)

    // ---------------------------------------------------------------- el códice

    /**
     * El códice: la tapa de cuero teñido, combada (más clara del lado de la luz), con su grano, sus roces y el filete grabado en
     * seco; los cantos de las hojas de papiro que asoman arriba, deshilachados; la tira del día pegada en la tapa a media altura;
     * la solapa en punta que viene de atrás por la derecha y baja por delante, más oscura, con su bisel de luz, su canto y su
     * sombra; y la correa, redonda, que da dos o tres vueltas por la punta de la solapa, con su pasador de hueso y el cabo que cuelga.
     */
    fun DrawScope.codice(v: Volumen, medidor: TextMeasurer, t: TonoArcontes, hacia: Float, tinteA: Color?) {
        val u = density
        val w = size.width; val h = size.height
        baldosas(u)
        val sv = v.semilla
        val arriba = (3.5f + 2f * azar(sv, 31)) * u
        alrededor(t, hacia, arriba)
        val ti = tinte(tinteA, P.CUERO_CRUDO, t, 0.28f, 0.55f)
        // Las hojas de papiro: el canto de arriba, desparejo y fibroso, más grueso cuanto más lleno.
        val hojas = Path().apply {
            moveTo(w * 0.05f, arriba + 2f * u)
            for (j in 0..8) lineTo(w * (0.05f + 0.1125f * j), arriba - (0.4f + 1.4f * azar(sv, 40 + j) + 1.2f * v.lleno) * u)
            lineTo(w * 0.95f, arriba + 2f * u); close()
        }
        drawPath(hojas, t.etiqueta(P.PAPIRO))
        for (j in 0 until 2 + (v.lleno * 4).toInt()) drawLine(t.c(P.FIBRA).copy(alpha = 0.55f), Offset(w * 0.06f, arriba - j * 0.7f * u + 0.6f * u), Offset(w * 0.94f, arriba - j * 0.7f * u + 0.4f * u), 0.5f * u)
        // La tapa, combada: un degradado blando de la luz a la sombra, el grano del cuero y sus roces.
        val tapa = Rect(0f, arriba, w, h)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(tapa, CornerRadius(2.8f * u))) }
        drawRoundRect(plano(ti, hacia, 0f, w), tapa.topLeft, tapa.size, CornerRadius(2.8f * u))
        grano(tapa.deflate(1.5f * u), sv, 1f)
        // Los roces: el cuero gastado y más claro en las esquinas y el canto de arriba, y unos rayones sueltos.
        drawOval(ti.claro.copy(alpha = 0.32f), Offset(-w * 0.12f, arriba - w * 0.1f), Size(w * 0.5f, w * 0.34f))
        drawOval(ti.claro.copy(alpha = 0.22f), Offset(w * 0.62f, h - w * 0.26f), Size(w * 0.5f, w * 0.34f))
        drawRect(ti.claro.copy(alpha = 0.25f), Offset(w * 0.1f, arriba + 0.6f * u), Size(w * 0.8f, 1.2f * u))
        val rayones = Path()
        for (j in 0 until 4) {
            val p = Offset(w * (0.15f + 0.7f * azar(sv, 90 + j)), h * (0.15f + 0.35f * azar(sv, 95 + j)))
            rayones.moveTo(p.x, p.y); rayones.lineTo(p.x + (2f + 3f * azar(sv, 99 + j)) * u, p.y + (azar(sv, 103 + j) - 0.5f) * 2f * u)
        }
        drawPath(rayones, ti.claro.copy(alpha = 0.55f), style = Stroke(0.5f * u))
        // El filete grabado en seco, a un dedo del borde.
        val fi = tapa.deflate(3.2f * u)
        drawRect(ti.hondo.copy(alpha = 0.7f), fi.topLeft, fi.size, style = Stroke(0.7f * u))
        drawRect(ti.claro.copy(alpha = 0.45f), fi.topLeft + Offset(0.7f * u, 0.7f * u), fi.size, style = Stroke(0.5f * u))
        // La tira del día, pegada en la tapa a media altura (encima del arranque de la solapa), con su sombra corta.
        val ew = anchoEtiqueta()
        val ycen = h * (0.37f + 0.05f * azar(sv, 36))
        val top = (ycen - ET_ALTO * u / 2).coerceAtLeast(arriba + 4f * u)
        val ex = w * 0.5f + (azar(sv, 37) - 0.5f) * (w - ew) * 0.6f
        val et = Rect(ex - ew / 2, top, ex + ew / 2, top + ET_ALTO * u)
        etiqueta(v, medidor, et, t, P.PAPIRO_ETIQUETA, pegada = true)
        // La solapa en punta: viene de atrás por la derecha, desde debajo de la tira, y baja por delante hasta la punta.
        val y0 = maxOf(et.bottom + 3f * u, h * 0.5f); val y1 = h - h * 0.03f
        val yp = (y0 + y1) / 2 + h * (0.04f + 0.04f * azar(sv, 32))
        val xp = w * (0.14f + 0.1f * azar(sv, 33))
        val sol = Path().apply { moveTo(w, y0); lineTo(xp, yp); lineTo(w, y1); close() }
        translate(-1.6f * u, 2.4f * u) { drawPath(sol, t.s(0.5f)) }
        val cs = mezclaRapida(ti.cara, ti.hondo, 0.3f)
        drawPath(sol, Brush.linearGradient(listOf(mezclaRapida(cs, ti.claro, 0.18f), cs, mezclaRapida(cs, ti.hondo, 0.35f)), Offset(w, y0), Offset(w * 0.7f, y1)))
        // El bisel de luz del filo de arriba, el canto en sombra del de abajo y el filete de la solapa.
        drawPath(Path().apply { moveTo(w, y0); lineTo(xp, yp); lineTo(xp + 4f * u, yp + 0.4f * u); lineTo(w, y0 + 3.4f * u); close() }, ti.claro.copy(alpha = 0.45f))
        drawLine(ti.claro.copy(alpha = 0.85f), Offset(w, y0), Offset(xp, yp), 0.9f * u)
        drawLine(ti.hondo.copy(alpha = 0.9f), Offset(xp, yp), Offset(w, y1), 1.2f * u)
        drawLine(t.s(0.35f), Offset(xp + 1f * u, yp + 1.2f * u), Offset(w, y1 + 1.2f * u), 1f * u)
        drawPath(Path().apply { moveTo(w - 3f * u, y0 + 4.5f * u); lineTo(xp + 7f * u, yp); lineTo(w - 3f * u, y1 - 4.5f * u) }, ti.hondo.copy(alpha = 0.55f), style = Stroke(0.6f * u))
        grano(Rect(w * 0.5f, y0 + 2f * u, w - 1f * u, y1 - 2f * u), sv + 5, 0.6f)
        // La correa: redonda (su sombra, el cuero, el brillo de arriba), dos o tres vueltas por la punta, que se comban un poco.
        val vueltas = 2 + (azar(sv, 35) * 1.99f).toInt()
        val correa = t.c(P.CORREA, 0.04f)
        for (j in 0 until vueltas) {
            val y = yp - 2.4f * u + j * 3.6f * u
            val inc = (if (j % 2 == 0) 1f else -0.8f) * u
            val vuelta = Path().apply { moveTo(-1f * u, y - inc); quadraticTo(w * 0.5f, y + 1.4f * u, w + 1f * u, y + inc) }
            translate(0f, 1.3f * u) { drawPath(vuelta, t.s(0.45f), style = Stroke(3f * u)) }
            drawPath(vuelta, correa, style = Stroke(2.6f * u))
            translate(0f, -0.8f * u) { drawPath(vuelta, t.b(0.3f), style = Stroke(0.6f * u)) }
            translate(0f, 0.9f * u) { drawPath(vuelta, t.s(0.3f), style = Stroke(0.6f * u)) }
        }
        val cabo = Path().apply {
            val a = Offset(1.5f * u, yp + vueltas * 3.6f * u - 2f * u)
            moveTo(a.x, a.y); quadraticTo(a.x - 2.5f * u, a.y + h * 0.1f, a.x + 1.5f * u, minOf(h - 2f * u, a.y + h * 0.17f))
        }
        drawPath(cabo, t.s(0.4f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawPath(cabo, correa, style = Stroke(2.1f * u, cap = StrokeCap.Round))
        // El pasador de hueso en la punta de la solapa.
        val pas = Offset(xp + 3f * u, yp + 1f * u)
        drawLine(t.s(0.4f), pas + Offset(-3.4f * u, 1.6f * u), pas + Offset(3.8f * u, 1.2f * u), 3.4f * u, StrokeCap.Round)
        drawLine(t.c(P.HUESO), pas + Offset(-3.4f * u, 0.4f * u), pas + Offset(3.8f * u, 0f), 3f * u, StrokeCap.Round)
        drawLine(t.b(0.6f), pas + Offset(-2.4f * u, -0.6f * u), pas + Offset(2.2f * u, -0.8f * u), 0.8f * u, StrokeCap.Round)
        // Los cantos: el de la luz y el de la sombra, y el filo de la noche.
        drawPath(forma, ti.hondo.copy(alpha = 0.7f), style = Stroke(0.6f * u))
        drawLine(t.b(0.45f), Offset(if (hacia < 0f) 1f * u else w - 1f * u, arriba + 3f * u), Offset(if (hacia < 0f) 1f * u else w - 1f * u, h - 3f * u), 1f * u)
        filos(tapa, t, hacia)
    }

    // ---------------------------------------------------------------- el rollo

    /**
     * El rollo de pie, redondo de verdad: el papiro enrollado con su sombreado de cilindro, las fibras, la hoja de fuera que se
     * riza, arriba la boca con su espiral y las vueltas de dentro que asoman, el umbilicus de madera que sale por los dos cabos (el
     * de abajo lo levanta un poco de la tabla, y se ve el canto redondo de abajo); la camisa de tela teñida del ánimo, que lo envuelve
     * por en medio y deja caer su punta suelta por un lado, con sus pliegues; un cordel de dos vueltas con su nudo; y la etiqueta
     * colgada de un hilo atado arriba, más abajo y ladeada.
     */
    fun DrawScope.rollo(v: Volumen, medidor: TextMeasurer, t: TonoArcontes, hacia: Float, tinteA: Color?, giro: Float = 0f) {
        val u = density
        val w = size.width; val h = size.height
        baldosas(u)
        val sv = v.semilla
        val d = w * 0.8f
        val cx = w * (0.5f + 0.03f * (azar(sv, 50) - 0.5f))
        val el = d * 0.34f
        val asoma = (1.5f + 3f * azar(sv, 51)) * u
        val pomo = 4.4f * u
        val y0 = asoma + pomo + el / 2 + 1.5f * u
        val base = h - 2.6f * u
        val lado = if (azar(sv, 54) > 0.5f) 1f else -1f
        rotate(giro, Offset(0f, h)) {
            alrededor(t, hacia, y0)
            val tp = tinte(null, P.PAPIRO, t, 0.25f, 0.5f)
            // El cabo de abajo del umbilicus, que lo levanta de la tabla.
            drawRoundRect(t.s(0.45f), Offset(cx - 2.6f * u, h - 2f * u), Size(5.6f * u, 2.4f * u), CornerRadius(1.2f * u))
            drawRoundRect(t.c(P.OLIVO_OSC), Offset(cx - 2.2f * u, base - 1.4f * u), Size(4.4f * u, 3.6f * u), CornerRadius(1.4f * u))
            // El papiro: el cuerpo redondo con su sombreado de cilindro (blando) y las fibras.
            val cuerpo = Path().apply {
                moveTo(cx - d / 2, y0); lineTo(cx - d / 2, base - el / 2)
                arcTo(Rect(cx - d / 2, base - el, cx + d / 2, base), 180f, -180f, false)
                lineTo(cx + d / 2, y0); close()
            }
            drawPath(cuerpo, franjas(tp, hacia, cx - d / 2, cx + d / 2))
            val fibras = Path()
            var yf = y0 + 2f * u; var jf = 0
            while (yf < base - el && jf < 50) { fibras.moveTo(cx - d / 2 + 0.5f * u, yf); fibras.lineTo(cx + d / 2 - 0.5f * u, yf + 0.6f * u); yf += 4f * u; jf++ }
            drawPath(fibras, t.c(P.FIBRA).copy(alpha = 0.2f), style = Stroke(0.5f * u))
            // El canto redondo de abajo: el borde de la última vuelta, que se ve al estar levantado.
            drawArc(t.s(0.45f), 10f, 160f, false, Offset(cx - d / 2, base - el), Size(d, el), style = Stroke(1.2f * u))
            drawArc(tp.claro.copy(alpha = 0.6f), 20f, 120f, false, Offset(cx - d * 0.42f, base - el * 0.95f), Size(d * 0.84f, el * 0.8f), style = Stroke(0.6f * u))
            // La camisa de tela teñida: envuelve por en medio, con el borde de arriba desparejo y la orilla de abajo.
            val c0 = h * (0.3f + 0.05f * azar(sv, 52))
            val c1 = base - el / 2 - h * (0.06f + 0.03f * azar(sv, 53))
            val dc = d * 1.05f
            val camisa = Path().apply {
                moveTo(cx - dc / 2, c0)
                quadraticTo(cx - dc * 0.2f, c0 + el * 0.85f, cx + dc * 0.05f, c0 + el * 0.5f + 1.5f * u)
                quadraticTo(cx + dc * 0.3f, c0 + el * 0.4f, cx + dc / 2, c0 - 1f * u)
                lineTo(cx + dc / 2, c1)
                quadraticTo(cx, c1 + el * 1.05f, cx - dc / 2, c1)
                close()
            }
            val ti = tinte(tinteA, P.LANA_CRUDA, t, 0.3f, 0.58f)
            translate(0f, 1.4f * u) { drawPath(camisa, t.s(0.3f)) }
            drawPath(camisa, franjas(ti, hacia, cx - dc / 2, cx + dc / 2))
            sarga(Rect(cx - dc / 2 + 1f * u, c0 + el * 0.9f, cx + dc / 2 - 1f * u, c1), 0.55f)
            // El borde de arriba de la tela, con su luz, y su orilla de abajo.
            drawPath(Path().apply {
                moveTo(cx - dc / 2, c0); quadraticTo(cx - dc * 0.2f, c0 + el * 0.85f, cx + dc * 0.05f, c0 + el * 0.5f + 1.5f * u)
                quadraticTo(cx + dc * 0.3f, c0 + el * 0.4f, cx + dc / 2, c0 - 1f * u)
            }, ti.claro.copy(alpha = 0.7f), style = Stroke(0.9f * u))
            drawPath(Path().apply { moveTo(cx - dc / 2, c1); quadraticTo(cx, c1 + el * 1.05f, cx + dc / 2, c1) }, ti.hondo.copy(alpha = 0.8f), style = Stroke(1f * u))
            // La punta suelta de la tela, que cae por un lado con su pliegue.
            val xa = cx + lado * dc * 0.5f
            val yc0 = c0 + (c1 - c0) * 0.18f
            val caida = Path().apply {
                moveTo(xa, yc0)
                quadraticTo(xa + lado * dc * 0.1f, (yc0 + c1) / 2, xa + lado * dc * 0.02f, c1 + el * 0.5f)
                lineTo(xa - lado * dc * 0.16f, c1 + el * 0.15f)
                quadraticTo(xa - lado * dc * 0.12f, (yc0 + c1) / 2, xa - lado * dc * 0.2f, yc0 + (c1 - c0) * 0.1f)
                close()
            }
            translate(lado * 1.2f * u, 1.6f * u) { drawPath(caida, t.s(0.35f)) }
            drawPath(caida, mezclaRapida(ti.cara, if (lado * hacia > 0f) ti.claro else ti.hondo, 0.3f))
            drawLine(mezclaRapida(ti.cara, ti.hondo, 0.7f), Offset(xa - lado * dc * 0.08f, yc0 + (c1 - c0) * 0.15f), Offset(xa - lado * dc * 0.05f, c1 + el * 0.3f), 0.9f * u)
            drawLine(ti.claro.copy(alpha = 0.6f), Offset(xa - lado * dc * 0.02f, yc0 + (c1 - c0) * 0.12f), Offset(xa + lado * dc * 0.01f, c1 + el * 0.35f), 0.6f * u)
            // El cordel: dos vueltas finas que siguen la curva, el nudo delante y dos cabos.
            val yc = c0 + (c1 - c0) * (0.5f + 0.1f * azar(sv, 56))
            val cord = t.c(P.CORDEL)
            for (j in 0 until 2) {
                val y = yc + j * 2.6f * u
                val arco = Path().apply { moveTo(cx - dc / 2 - 0.5f * u, y); quadraticTo(cx, y + el * 1.05f, cx + dc / 2 + 0.5f * u, y) }
                translate(0f, 1.1f * u) { drawPath(arco, t.s(0.4f), style = Stroke(1.6f * u)) }
                drawPath(arco, cord, style = Stroke(1.3f * u))
            }
            val n = Offset(cx - lado * d * 0.16f, yc + el * 0.5f + 1.3f * u)
            drawCircle(t.s(0.4f), 2.2f * u, n + Offset(0.6f * u, 0.8f * u))
            drawCircle(cord, 1.9f * u, n)
            drawLine(cord, n, n + Offset(-1.5f * u, 6f * u), 1.1f * u, StrokeCap.Round)
            drawLine(cord, n, n + Offset(2.4f * u, 7f * u), 1.1f * u, StrokeCap.Round)
            // La última hoja del papiro, que se suelta de la izquierda y se riza hacia fuera.
            val rizo = Path().apply {
                moveTo(cx - d / 2, y0 + el * 0.3f)
                cubicTo(cx - d / 2 - 4f * u, y0 + el * 0.1f, cx - d / 2 - 5f * u, y0 - el * 0.5f, cx - d / 2 - 1f * u, y0 - el * 0.55f)
                cubicTo(cx - d / 2 + 1.5f * u, y0 - el * 0.5f, cx - d / 2 + 0.5f * u, y0 - el * 0.1f, cx - d / 2 + 2.5f * u, y0 + el * 0.2f)
                close()
            }
            // La boca de arriba: el óvalo del papiro con su espiral, las vueltas de dentro que asoman y el umbilicus que sale.
            val boca = Rect(cx - d / 2, y0 - el / 2, cx + d / 2, y0 + el / 2)
            drawOval(t.etiqueta(mezclaRapida(P.PAPIRO, P.PAPIRO_OSC, 0.25f)), boca.topLeft, boca.size)
            val dentro = Rect(cx - d * 0.22f, y0 - el * 0.22f - asoma, cx + d * 0.22f, y0 + el * 0.22f - asoma)
            drawRect(t.etiqueta(P.PAPIRO_OSC), Offset(dentro.left, dentro.center.y), Size(dentro.width, asoma))
            drawOval(t.etiqueta(P.PAPIRO), dentro.topLeft, dentro.size)
            val espiral = Path()
            for (j in 0 until 3) {
                val f = 0.3f + 0.24f * j
                espiral.addArc(Rect(boca.center.x - boca.width * f / 2, boca.center.y - boca.height * f / 2, boca.center.x + boca.width * f / 2, boca.center.y + boca.height * f / 2), 20f + 40f * j, 260f)
            }
            drawPath(espiral, t.c(P.FIBRA, -0.2f).copy(alpha = 0.85f), style = Stroke(0.8f * u))
            val dd = dentro.deflate(dentro.width * 0.28f)
            drawArc(t.c(P.FIBRA, -0.2f).copy(alpha = 0.85f), 30f, 280f, false, dd.topLeft, dd.size, style = Stroke(0.8f * u))
            drawArc(t.b(0.5f), 160f, 120f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
            // El umbilicus: la varilla de madera que sale del centro, con su perilla.
            val ce = dentro.center
            drawLine(t.s(0.4f), ce + Offset(0.8f * u, 0f), ce + Offset(0.8f * u, -pomo + 1f * u), 2.2f * u)
            drawLine(t.c(P.OLIVO_OSC), ce, ce - Offset(0f, pomo - 1f * u), 2f * u)
            drawOval(t.c(P.OLIVO_HONDO), Offset(ce.x - 2.3f * u, ce.y - pomo - 1.2f * u), Size(4.6f * u, 3.2f * u))
            drawCircle(t.b(0.55f), 0.8f * u, Offset(ce.x - 0.9f * u, ce.y - pomo - 0.2f * u))
            drawPath(rizo, t.etiqueta(mezclaRapida(P.PAPIRO, P.PAPIRO_OSC, 0.4f)))
            drawPath(rizo, t.c(P.FIBRA).copy(alpha = 0.7f), style = Stroke(0.6f * u))
            filos(Rect(cx - dc / 2, y0, cx + dc / 2, base - el / 2), t, hacia)
        }
        // La etiqueta, colgada de un hilo atado arriba, junto al umbilicus: más abajo que las demás y ladeada.
        // Donde queda el nudo del hilo con el rollo recostado (gira alrededor de su esquina de abajo a la izquierda).
        val an = Math.toRadians(giro.toDouble())
        val px = cx + d * 0.18f; val py = y0 - h
        val nudo = Offset((px * kotlin.math.cos(an) - py * kotlin.math.sin(an)).toFloat(), h + (px * kotlin.math.sin(an) + py * kotlin.math.cos(an)).toFloat())
        val ew = anchoEtiqueta()
        val ycen = h * (0.6f + 0.06f * azar(sv, 57))
        val top = minOf(ycen - ET_ALTO * u / 2, h - ET_ALTO * u - 4f * u)
        val corre = nudo.x - px
        val ex = cx + corre + (azar(sv, 58) - 0.5f) * w * 0.1f
        val et = Rect(ex - ew / 2, top, ex + ew / 2, top + ET_ALTO * u)
        val colgada = Offset(et.center.x + ew * 0.18f, et.top + 1.6f * u)
        drawLine(t.s(0.3f), nudo + Offset(0.8f * u, 0.6f * u), colgada + Offset(0.8f * u, 0.6f * u), 0.7f * u)
        drawLine(t.c(P.CORDEL), nudo, colgada, 0.7f * u)
        val ladeo = (if (azar(sv, 59) > 0.5f) 1f else -1f) * (3.5f + 2.5f * azar(sv, 60))
        rotate(ladeo, colgada) { etiqueta(v, medidor, et, t, mezclaRapida(P.PAPIRO_ETIQUETA, P.PERGAMINO, 0.25f), 0.018f) }
    }

    // ---------------------------------------------------------------- el envoltorio

    /**
     * El envoltorio: las hojas sueltas envueltas en un paño de lana teñida, un fardo blando de hombros redondos (la tapa en cúpula,
     * los costados que se abomban abajo y se aprietan donde pasa el cordel); cerca de la orilla de abajo, las dos franjas oscuras
     * del manto del Ideario; los pliegues que salen del cordel; el cordel con su nudo y la bula de barro; y la etiqueta atada al nudo.
     */
    fun DrawScope.envoltorio(v: Volumen, medidor: TextMeasurer, t: TonoArcontes, hacia: Float, tinteA: Color?) {
        val u = density
        val w = size.width; val h = size.height
        baldosas(u)
        val sv = v.semilla
        val arriba = h * (0.03f + 0.03f * azar(sv, 62))
        val hombro = minOf(w * 0.36f, 13f * u)
        val yc = h * (0.31f + 0.04f * azar(sv, 65))
        alrededor(t, hacia, arriba + hombro * 0.5f, 0.9f)
        val ti = tinte(tinteA, P.LANA_CRUDA, t, 0.3f, 0.58f)
        // El cuerpo: hombros redondos, la tapa en cúpula, apretado en el cordel y abombado abajo.
        val cuerpo = Path().apply {
            moveTo(w * 0.07f, h)
            quadraticTo(w * -0.01f, (yc + h) / 2 + h * 0.06f, w * 0.05f, yc + 2f * u)
            lineTo(w * 0.07f, yc)
            lineTo(w * 0.05f, arriba + hombro)
            quadraticTo(w * 0.05f, arriba, w * 0.05f + hombro, arriba - 0.6f * u)
            quadraticTo(w * 0.5f, arriba - 2.2f * u, w * 0.95f - hombro, arriba - 0.6f * u)
            quadraticTo(w * 0.95f, arriba, w * 0.95f, arriba + hombro)
            lineTo(w * 0.93f, yc)
            lineTo(w * 0.95f, yc + 2f * u)
            quadraticTo(w * 1.01f, (yc + h) / 2 + h * 0.06f, w * 0.93f, h)
            close()
        }
        drawPath(cuerpo, franjas(ti, hacia, 0f, w))
        sarga(Rect(w * 0.1f, arriba + hombro * 0.5f, w * 0.9f, h - 2f * u), 0.9f)
        // La luz en la cúpula de arriba y la sombra que echa el cordel.
        drawPath(Path().apply { moveTo(w * 0.1f, arriba + hombro * 0.5f); quadraticTo(w * 0.12f, arriba + 0.8f * u, w * 0.05f + hombro, arriba + 0.6f * u); quadraticTo(w * 0.5f, arriba - 1f * u, w * 0.7f, arriba + 0.4f * u) },
            t.b(0.4f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawRect(t.s(0.28f), Offset(w * 0.06f, yc + 1.4f * u), Size(w * 0.88f, 2.4f * u))
        // Los pliegues que salen del cordel: arriba, cortos hacia los hombros; abajo, largos hacia la base.
        for (j in 0 until 3) {
            val x = w * (0.22f + 0.28f * j)
            val abajo = Path().apply { moveTo(x, yc + 3f * u); quadraticTo(x + (j - 1) * w * 0.06f, (yc + h) / 2, x + (j - 1) * w * 0.1f, h - h * 0.2f) }
            drawPath(abajo, t.s(0.26f), style = Stroke(1.3f * u))
            translate(1.2f * u, 0f) { drawPath(abajo, t.b(0.18f), style = Stroke(0.7f * u)) }
            val arribaP = Path().apply { moveTo(x, yc - 1f * u); quadraticTo(x + (j - 1) * w * 0.04f, (yc + arriba) / 2, x + (j - 1) * w * 0.08f, arriba + hombro * 0.6f) }
            drawPath(arribaP, t.s(0.2f), style = Stroke(1f * u))
        }
        // Las dos franjas oscuras del manto, cerca de la orilla de abajo, siguiendo la curva del paño.
        listOf(0.84f, 0.9f).forEach { f ->
            val y = h * f
            drawPath(Path().apply { moveTo(w * 0.03f, y - h * 0.012f); quadraticTo(w * 0.5f, y + h * 0.02f, w * 0.97f, y - h * 0.012f) }, t.c(P.FRANJA), style = Stroke(h * 0.022f))
        }
        drawRect(t.s(0.3f), Offset(w * 0.1f, h - h * 0.05f), Size(w * 0.8f, h * 0.05f))
        // El cordel, que aprieta el paño, y el nudo con la bula de barro.
        val lado = if (azar(sv, 61) > 0.5f) 1f else -1f
        val cord = t.c(P.CORDEL)
        val arco = Path().apply { moveTo(w * 0.03f, yc - 1f * u); quadraticTo(w / 2, yc + h * 0.03f, w * 0.97f, yc - 1f * u) }
        translate(0f, -1.4f * u) { drawPath(arco, t.b(0.3f), style = Stroke(1.4f * u)) }
        drawPath(arco, cord, style = Stroke(1.7f * u))
        val nudo = Offset(w * (0.5f + 0.2f * lado), yc + h * 0.012f)
        drawCircle(t.s(0.4f), 2.4f * u, nudo + Offset(0.6f * u, 0.8f * u))
        drawCircle(cord, 2.1f * u, nudo)
        // La etiqueta, atada al nudo por un hilo corto: cuelga debajo del cordel.
        val ew = anchoEtiqueta()
        val ex = (nudo.x - lado * ew * 0.15f).coerceIn(ew / 2 + 1f * u, w - ew / 2 - 1f * u)
        val top = nudo.y + 4f * u
        val et = Rect(ex - ew / 2, top, ex + ew / 2, top + ET_ALTO * u)
        drawLine(cord, nudo, Offset(nudo.x, top + 1.5f * u), 0.8f * u)
        etiqueta(v, medidor, et, t, P.PAPIRO_ETIQUETA)
        // La bula de barro, colgada del nudo por el otro lado.
        val pb = Offset(nudo.x + lado * minOf(w * 0.18f, 6f * u), nudo.y + 3f * u)
        drawLine(cord, nudo, pb, 0.9f * u)
        bula(pb, minOf(w * 0.15f, 4.4f * u), t.c(P.BARRO_CLARO), t)
        filos(Rect(w * 0.05f, arriba, w * 0.95f, h), t, hacia)
    }
}
