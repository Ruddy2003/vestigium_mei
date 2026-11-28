package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil

/**
 * Las entradas del estante de Arcontes ([MuebleEstanteArcontes]), tres cosas del círculo según la semilla, de tres siluetas: el
 * códice (rígido: la tapa de cuero teñido como los de Nag Hammadi, la solapa en punta que viene de atrás, la correa enrollada con
 * su pasador de hueso, los cantos deshilachados de las hojas de papiro y la tira del día metida bajo la correa), el rollo de pie
 * (redondo: el papiro con su espiral, la camisa de pergamino teñido atada con dos correas rojas y la etiqueta de pergamino
 * colgada de lo alto) y el envoltorio (blando: hojas sueltas en un paño de lana teñida con las dos franjas oscuras del manto del
 * Ideario, atado con cordel, la boca anudada y caída a un lado con la bula de barro, y la etiqueta colgada del nudo). El ánimo es
 * el tinte del cuero, del pergamino y de la lana ([tonoDeDiaArcontes]); sin ánimo, sin teñir. En los Sueños, una media luna de
 * plata con su disco apagado en la etiqueta.
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
        val claro = mezcla(m, luzMueble(1f), brillo)
        val hondo = mezcla(m, sombraMueble(1f), sombra)
        return if (ti != null) Tinte(t.animo(m), t.animo(claro), t.animo(hondo)) else Tinte(t.c(m), t.c(claro), t.c(hondo))
    }

    /** Las franjas de un cuerpo redondo en un solo degradado de cortes francos, para pintar de una vez una forma cualquiera. */
    private fun franjas(ti: Tinte, hacia: Float, x0: Float, x1: Float): Brush {
        val paradas = ArrayList<Pair<Float, Color>>()
        for (i in 0 until 7) {
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(ti.cara, ti.claro, m) else mezcla(ti.cara, ti.hondo, -m)
            val a0 = if (hacia < 0f) FRANJAS[i] else 1f - FRANJAS[i + 1]
            val a1 = if (hacia < 0f) FRANJAS[i + 1] else 1f - FRANJAS[i]
            paradas += a0 to c; paradas += a1 to c
        }
        paradas.sortBy { it.first }
        return Brush.horizontalGradient(*paradas.toTypedArray(), startX = x0, endX = x1)
    }

    /** Una tapa casi plana: el claro del lado de la luz, la cara y el hondo del otro lado, sin cortes. */
    private fun plano(ti: Tinte, hacia: Float, x0: Float, x1: Float): Brush {
        val l = mezcla(ti.cara, ti.claro, 0.35f); val o = mezcla(ti.cara, ti.hondo, 0.55f)
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

    /** El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp sin achicarse) y el mes debajo, en una de 9.5 dp. */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left - 2f * u, y0, r.right + 2f * u, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.Bold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left - 2f * u, y0 + hd - 1.6f * u, r.right + 2f * u, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.88f), MES, minimo = 5f)
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
        densidadBaldosas = u
    }

    /** El grano del cuero en [r]: rayitas cortas en todas direcciones, en un camino (más barato que pegar una baldosa entera). */
    private fun DrawScope.grano(r: Rect, semilla: Int, fuerza: Float) {
        val u = density
        if (r.width < 2f || r.height < 2f) return
        val hondo = Path(); val claro = Path()
        val n = (r.width * r.height / (u * u * 26f)).toInt().coerceIn(6, 70)
        for (j in 0 until n) {
            val x = r.left + r.width * azar(semilla + 8701, j); val y = r.top + r.height * azar(semilla + 8702, j)
            val an = azar(semilla + 8703, j) * 3.1416f
            val l = (1.1f + 1.5f * azar(semilla + 8704, j)) * u
            (if (j % 4 == 0) claro else hondo).apply { moveTo(x, y); lineTo(x + l * kotlin.math.cos(an), y + l * kotlin.math.sin(an)) }
        }
        drawPath(hondo, SOMBRA_MUEBLE.copy(alpha = 0.42f * fuerza), style = Stroke(0.6f * u))
        drawPath(claro, LUZ_MUEBLE.copy(alpha = 0.34f * fuerza), style = Stroke(0.5f * u))
    }

    /** La sarga de la lana en [r]: diagonales finas en sombra y en luz, en un camino. */
    private fun DrawScope.sarga(r: Rect, fuerza: Float) {
        val u = density
        if (r.width < 2f || r.height < 2f) return
        val hondo = Path(); val claro = Path()
        var x = r.left - r.height; var j = 0
        while (x < r.right && j < 120) {
            hondo.moveTo(x, r.top); hondo.lineTo(x + r.height, r.bottom)
            claro.moveTo(x + 1.2f * u, r.top); claro.lineTo(x + 1.2f * u + r.height, r.bottom)
            x += 3f * u; j++
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawPath(hondo, SOMBRA_MUEBLE.copy(alpha = 0.26f * fuerza), style = Stroke(0.7f * u))
            drawPath(claro, LUZ_MUEBLE.copy(alpha = 0.18f * fuerza), style = Stroke(0.5f * u))
        }
    }

    private fun DrawScope.mosaico(b: ImageBitmap?, r: Rect, alfa: Float) {
        if (b == null) return
        var y = r.top
        while (y < r.bottom) {
            var x = r.left
            while (x < r.right) { drawImage(b, Offset(x, y), alpha = alfa); x += b.width }
            y += b.height
        }
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
        drawRoundRect(t.s(0.3f * fs), Offset(-hacia * 6f * u, desde + 4f * u), Size(w, h - desde - 4f * u), CornerRadius(3f * u))
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

    /** Una etiqueta de papiro (o pergamino) en [et], de borde deshilachado, con sus fibras, su sombra, el día, el mes y la luna. */
    private fun DrawScope.etiqueta(v: Volumen, medidor: TextMeasurer, et: Rect, t: TonoArcontes, papel: Color, giro: Float, roido: Float = 0.03f) {
        val u = density
        rotate(giro, et.center) {
            val forma = contornoIrregular(et, v.semilla, 28, roido)
            translate(1f * u, 1.4f * u) { drawPath(forma, t.s(0.42f)) }
            val c = t.etiqueta(papel)
            drawPath(forma, c)
            val dentro = et.deflate(1.2f * u)
            val fuertes = Path(); val debiles = Path()
            var y = et.top + 1.6f * u; var j = 0
            while (y < dentro.bottom && j < 30) {
                (if (azar(v.semilla, j) > 0.5f) fuertes else debiles).apply { moveTo(dentro.left, y); lineTo(dentro.right, y + (azar(v.semilla + 1, j) - 0.5f) * u) }
                y += 1.9f * u; j++
            }
            drawPath(fuertes, P.FIBRA.copy(alpha = 0.26f), style = Stroke(0.45f * u))
            drawPath(debiles, P.FIBRA.copy(alpha = 0.16f), style = Stroke(0.45f * u))
            drawRect(P.FIBRA.copy(alpha = 0.16f), Offset(dentro.left, dentro.bottom - et.height * 0.14f), Size(dentro.width, et.height * 0.14f))
            drawPath(forma, mezcla(c, P.FIBRA, 0.55f), style = Stroke(0.7f * u))
            diaYMes(v, medidor, et.deflate(1.4f * u), t.tinta())
            if (v.noche) luna(Offset(et.right - 3.2f * u, et.top + 3.4f * u), 3.2f * u, t)
        }
    }

    // ---------------------------------------------------------------- el códice

    /**
     * El códice: la tapa de cuero teñido, con su grano y el filete grabado en seco; los cantos de las hojas de papiro que asoman
     * arriba, deshilachados; la solapa grande en punta que viene de atrás por la derecha, más oscura, con su filo de luz y su
     * sombra; la tira del día pegada arriba; y la correa que da dos o tres vueltas por la punta de la solapa, con su pasador de
     * hueso y el cabo que cuelga.
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
        // La tapa.
        val tapa = Rect(0f, arriba, w, h)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(tapa, CornerRadius(2.8f * u))) }
        drawPath(forma, plano(ti, hacia, 0f, w))
        val yp = h * (0.5f + 0.08f * azar(sv, 32))
        val xp = w * (0.12f + 0.1f * azar(sv, 33))
        clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
            grano(tapa, sv, 1f)
            // El filete grabado en seco, a un dedo del borde.
            val fi = tapa.deflate(3.2f * u)
            drawRect(ti.hondo.copy(alpha = 0.7f), fi.topLeft, fi.size, style = Stroke(0.7f * u))
            drawRect(ti.claro.copy(alpha = 0.5f), fi.topLeft + Offset(0.7f * u, 0.7f * u), fi.size, style = Stroke(0.5f * u))
            // La solapa en punta, que viene de atrás por la derecha: más oscura, con su sombra en la tapa.
            val y0 = arriba + h * 0.05f; val y1 = h - h * 0.035f
            val sol = Path().apply { moveTo(w + 1f, y0); lineTo(xp, yp); lineTo(w + 1f, y1); close() }
            translate(-1.8f * u, 2.2f * u) { drawPath(sol, t.s(0.5f)) }
            val ts = Tinte(mezcla(ti.cara, ti.hondo, 0.32f), mezcla(ti.claro, ti.cara, 0.4f), ti.hondo)
            drawPath(sol, plano(ts, hacia, xp, w))
            // El filo de arriba de la solapa mira a la luz; el de abajo queda en sombra; y su propio filete grabado.
            drawLine(ti.claro.copy(alpha = 0.8f), Offset(w, y0), Offset(xp, yp), 1.1f * u)
            drawLine(ti.hondo.copy(alpha = 0.85f), Offset(xp, yp), Offset(w, y1), 1f * u)
            drawPath(Path().apply { moveTo(w - 3f * u, y0 + 4.5f * u); lineTo(xp + 7f * u, yp); lineTo(w - 3f * u, y1 - 4.5f * u) }, ti.hondo.copy(alpha = 0.6f), style = Stroke(0.6f * u))
            // El desgaste: la esquina de abajo a la izquierda, más clara.
            drawOval(ti.claro.copy(alpha = 0.3f), Offset(-w * 0.2f, h - w * 0.2f), Size(w * 0.5f, w * 0.38f))
        }
        // La tira del día, pegada en lo alto de la tapa.
        val et = Rect(w * 0.1f, arriba + h * 0.07f + 3f * u * azar(sv, 36), w * 0.9f, arriba + h * 0.07f + 3f * u * azar(sv, 36) + 28f * u)
        etiqueta(v, medidor, et, t, P.PAPIRO_ETIQUETA, -2f + 4f * azar(sv, 34))
        // La correa: dos o tres vueltas casi horizontales por la punta de la solapa, y el cabo que cuelga por la izquierda.
        val vueltas = 2 + (azar(sv, 35) * 1.99f).toInt()
        val correa = t.c(P.CORREA, 0.04f)
        for (j in 0 until vueltas) {
            val y = yp - 2f * u + j * 3.4f * u
            val inc = (if (j % 2 == 0) 1f else -0.8f) * u
            drawLine(t.s(0.45f), Offset(-1f * u, y + 1.2f * u - inc), Offset(w + 1f * u, y + 1.2f * u + inc), 2.8f * u)
            drawLine(correa, Offset(-1f * u, y - inc), Offset(w + 1f * u, y + inc), 2.4f * u)
            drawLine(t.b(0.22f), Offset(-1f * u, y - inc - 0.8f * u), Offset(w + 1f * u, y + inc - 0.8f * u), 0.5f * u)
        }
        val cabo = Path().apply {
            val a = Offset(1.5f * u, yp + vueltas * 3.4f * u - 2f * u)
            moveTo(a.x, a.y); quadraticTo(a.x - 2.5f * u, a.y + h * 0.12f, a.x + 1.5f * u, a.y + h * 0.2f)
        }
        drawPath(cabo, t.s(0.4f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(cabo, correa, style = Stroke(2f * u, cap = StrokeCap.Round))
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
     * El rollo de pie: el papiro enrollado, con su espiral, las vueltas de dentro que asoman arriba y la última hoja que se suelta y
     * se riza por fuera; la camisa de pergamino teñido del ánimo envuelta encima, con el canto de la hoja que la cierra; un
     * cordel que la ciñe dos vueltas, con su nudo y sus cabos; y la etiqueta de pergamino que cuelga de lo alto por un hilo.
     */
    fun DrawScope.rollo(v: Volumen, medidor: TextMeasurer, t: TonoArcontes, hacia: Float, tinteA: Color?) {
        val u = density
        val w = size.width; val h = size.height
        baldosas(u)
        val sv = v.semilla
        val d = w * 0.82f
        val cx = w * (0.5f + 0.03f * (azar(sv, 50) - 0.5f))
        val el = d * 0.34f
        val asoma = (1.5f + 3.5f * azar(sv, 51)) * u
        val y0 = asoma + el / 2 + 1.5f * u
        alrededor(t, hacia, y0)
        val tp = tinte(null, P.PAPIRO, t, 0.25f, 0.45f)
        // El papiro: el cuerpo redondo con su fondo en curva y las fibras a lo ancho.
        val cuerpo = Path().apply {
            moveTo(cx - d / 2, y0); lineTo(cx - d / 2, h - el / 2)
            arcTo(Rect(cx - d / 2, h - el, cx + d / 2, h), 180f, -180f, false)
            lineTo(cx + d / 2, y0); close()
        }
        drawPath(cuerpo, franjas(tp, hacia, cx - d / 2, cx + d / 2))
        val fibras = Path()
        var yf = y0 + 2f * u; var jf = 0
        while (yf < h - el && jf < 50) { fibras.moveTo(cx - d / 2 + 0.5f * u, yf); fibras.lineTo(cx + d / 2 - 0.5f * u, yf + 0.6f * u); yf += 2.3f * u; jf++ }
        drawPath(fibras, t.c(P.FIBRA).copy(alpha = 0.22f), style = Stroke(0.5f * u))
        // La camisa de pergamino teñido: envuelve de arriba a casi abajo, con el borde de arriba desparejo.
        val c0 = h * (0.3f + 0.06f * azar(sv, 52))
        val c1 = h - el / 2 - h * (0.03f + 0.03f * azar(sv, 53))
        val dc = d * 1.05f
        val camisa = Path().apply {
            moveTo(cx - dc / 2, c0)
            quadraticTo(cx - dc * 0.2f, c0 + el * 0.85f, cx + dc * 0.05f, c0 + el * 0.5f + 1.5f * u)
            quadraticTo(cx + dc * 0.3f, c0 + el * 0.4f, cx + dc / 2, c0 - 1f * u)
            lineTo(cx + dc / 2, c1)
            quadraticTo(cx, c1 + el * 1.05f, cx - dc / 2, c1)
            close()
        }
        val ti = tinte(tinteA, P.PERGAMINO, t, 0.28f, 0.56f)
        translate(0f, 1.4f * u) { drawPath(camisa, t.s(0.3f)) }
        drawPath(camisa, franjas(ti, hacia, cx - dc / 2, cx + dc / 2))
        grano(Rect(cx - dc / 2 + 1f * u, c0 + el * 0.9f, cx + dc / 2 - 1f * u, c1), sv + 3, 0.5f)
        // El canto de la hoja que cierra la camisa: una raya que baja, con su luz y la sombra que echa.
        val xb = cx + dc * (0.18f + 0.1f * azar(sv, 55))
        val yb0 = c0 + el * 0.55f; val yb1 = c1 + el * 0.75f
        drawLine(t.s(0.4f), Offset(xb + 1.2f * u, yb0), Offset(xb + 1.8f * u, yb1), 2f * u)
        drawLine(ti.claro.copy(alpha = 0.7f), Offset(xb, yb0), Offset(xb + 0.6f * u, yb1), 0.9f * u)
        drawPath(Path().apply {
            moveTo(cx - dc / 2, c0); quadraticTo(cx - dc * 0.2f, c0 + el * 0.85f, cx + dc * 0.05f, c0 + el * 0.5f + 1.5f * u)
            quadraticTo(cx + dc * 0.3f, c0 + el * 0.4f, cx + dc / 2, c0 - 1f * u)
        }, ti.claro.copy(alpha = 0.75f), style = Stroke(0.9f * u))
        // El cordel: dos vueltas finas que siguen la curva, el nudo delante y dos cabos.
        val yc = c0 + (c1 - c0) * (0.4f + 0.12f * azar(sv, 56))
        val cord = t.c(P.CORDEL)
        for (j in 0 until 2) {
            val y = yc + j * 2.6f * u
            val arco = Path().apply { moveTo(cx - dc / 2 - 0.5f * u, y); quadraticTo(cx, y + el * 1.05f, cx + dc / 2 + 0.5f * u, y) }
            translate(0f, 1.1f * u) { drawPath(arco, t.s(0.4f), style = Stroke(1.6f * u)) }
            drawPath(arco, cord, style = Stroke(1.3f * u))
        }
        val n = Offset(cx - d * 0.16f, yc + el * 0.5f + 1.3f * u)
        drawCircle(t.s(0.4f), 2.2f * u, n + Offset(0.6f * u, 0.8f * u))
        drawCircle(cord, 1.9f * u, n)
        drawLine(cord, n, n + Offset(-1.5f * u, 7f * u), 1.1f * u, StrokeCap.Round)
        drawLine(cord, n, n + Offset(2.4f * u, 8.5f * u), 1.1f * u, StrokeCap.Round)
        // La última hoja del papiro, que se suelta de la izquierda y se riza hacia fuera.
        val rizo = Path().apply {
            moveTo(cx - d / 2, y0 + el * 0.3f)
            cubicTo(cx - d / 2 - 4f * u, y0 + el * 0.1f, cx - d / 2 - 5f * u, y0 - el * 0.5f, cx - d / 2 - 1f * u, y0 - el * 0.55f)
            cubicTo(cx - d / 2 + 1.5f * u, y0 - el * 0.5f, cx - d / 2 + 0.5f * u, y0 - el * 0.1f, cx - d / 2 + 2.5f * u, y0 + el * 0.2f)
            close()
        }
        // La boca de arriba: el óvalo del papiro con su espiral y las vueltas de dentro, que asoman.
        val boca = Rect(cx - d / 2, y0 - el / 2, cx + d / 2, y0 + el / 2)
        drawOval(t.etiqueta(mezcla(P.PAPIRO, P.PAPIRO_OSC, 0.25f)), boca.topLeft, boca.size)
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
        drawCircle(t.c(P.FIBRA, -0.4f), 1f * u, dentro.center)
        drawArc(t.b(0.5f), 160f, 120f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
        drawPath(rizo, t.etiqueta(mezcla(P.PAPIRO, P.PAPIRO_OSC, 0.4f)))
        drawPath(rizo, t.c(P.FIBRA).copy(alpha = 0.7f), style = Stroke(0.6f * u))
        // La etiqueta de pergamino, colgada de un hilo atado a lo alto.
        val top = y0 + el * 0.55f + (1f + 4f * azar(sv, 57)) * u
        val et = Rect(cx - w * 0.42f, top, cx + w * 0.42f, top + 28f * u)
        val giro = -4f + 8f * azar(sv, 54)
        drawLine(t.c(P.CORDEL), Offset(cx + d * 0.2f, y0 + el * 0.25f), Offset(et.center.x + w * 0.12f, et.top + 2f * u), 0.7f * u)
        etiqueta(v, medidor, et, t, mezcla(P.PAPIRO_ETIQUETA, P.PERGAMINO, 0.4f), giro, 0.018f)
        filos(Rect(cx - dc / 2, y0, cx + dc / 2, h - el / 2), t, hacia)
    }

    // ---------------------------------------------------------------- el envoltorio

    /**
     * El envoltorio: las hojas sueltas envueltas en un paño de lana teñida, un fardo blando con sus esquinas (más ancho abajo, la
     * tapa un poco hundida en medio y los costados apretados por el cordel); la punta del paño doblada por delante en una solapa
     * con las dos franjas oscuras y los flecos del manto del Ideario; la etiqueta en la solapa; y la vuelta de cordel con la bula
     * de barro en el nudo.
     */
    fun DrawScope.envoltorio(v: Volumen, medidor: TextMeasurer, t: TonoArcontes, hacia: Float, tinteA: Color?) {
        val u = density
        val w = size.width; val h = size.height
        baldosas(u)
        val sv = v.semilla
        val arriba = h * (0.035f + 0.03f * azar(sv, 62))
        val yc = h * (0.74f + 0.04f * azar(sv, 65))
        val esq = minOf(w * 0.2f, 7f * u)
        alrededor(t, hacia, arriba, 0.9f)
        val ti = tinte(tinteA, P.LANA_CRUDA, t, 0.3f, 0.55f)
        // El cuerpo: blando, con las esquinas redondeadas, la tapa hundida y los costados apretados por el cordel.
        val cuerpo = Path().apply {
            moveTo(w * 0.05f, arriba + esq)
            quadraticTo(w * 0.05f, arriba, w * 0.05f + esq, arriba)
            quadraticTo(w * 0.5f, arriba + h * 0.035f, w * 0.95f - esq, arriba)
            quadraticTo(w * 0.95f, arriba, w * 0.95f, arriba + esq)
            quadraticTo(w * 1.0f, (arriba + yc) / 2, w * 0.93f, yc)
            quadraticTo(w * 1.02f, (yc + h) / 2 + h * 0.08f, w * 0.88f, h)
            lineTo(w * 0.12f, h)
            quadraticTo(w * -0.02f, (yc + h) / 2 + h * 0.08f, w * 0.07f, yc)
            quadraticTo(w * 0.0f, (arriba + yc) / 2, w * 0.05f, arriba + esq)
            close()
        }
        drawPath(cuerpo, franjas(ti, hacia, 0f, w))
        clipRect(w * 0.08f, arriba + esq, w * 0.92f, h) {
            sarga(Rect(w * 0.08f, arriba + esq, w * 0.92f, h), 1f)
            // Las esquinas de las hojas que se marcan en el paño, abajo, y los pliegues que bajan del cordel.
            for (j in 0 until 3) {
                val x = w * (0.2f + 0.3f * j)
                val p = Path().apply { moveTo(x, yc + 1f * u); quadraticTo(x + (j - 1) * w * 0.12f, (yc + h) / 2, x + (j - 1) * w * 0.18f, h) }
                drawPath(p, t.s(0.28f), style = Stroke(1.3f * u))
                translate(1.2f * u, 0f) { drawPath(p, t.b(0.2f), style = Stroke(0.7f * u)) }
            }
            drawRect(t.s(0.25f), Offset(w * 0.12f, h - h * 0.06f), Size(w * 0.76f, h * 0.06f))
        }
        // La solapa: la punta del paño doblada por delante, con su borde de abajo ladeado, sus franjas y sus flecos.
        val lado = if (azar(sv, 61) > 0.5f) 1f else -1f
        val ys = h * (0.53f + 0.04f * azar(sv, 63))
        val inc = lado * h * 0.035f
        val solapa = Path().apply {
            moveTo(w * 0.04f, arriba + esq * 0.8f)
            quadraticTo(w * 0.04f, arriba - 1f * u, w * 0.05f + esq, arriba - 1f * u)
            quadraticTo(w * 0.5f, arriba + h * 0.03f, w * 0.95f - esq, arriba - 1f * u)
            quadraticTo(w * 0.96f, arriba - 1f * u, w * 0.96f, arriba + esq * 0.8f)
            quadraticTo(w * 0.99f, (arriba + ys) / 2, w * 0.95f, ys + inc)
            quadraticTo(w * 0.5f, ys + h * 0.02f, w * 0.05f, ys - inc)
            quadraticTo(w * 0.01f, (arriba + ys) / 2, w * 0.04f, arriba + esq * 0.8f)
            close()
        }
        translate(0f, 2f * u) { drawPath(solapa, t.s(0.42f)) }
        drawPath(solapa, franjas(Tinte(mezcla(ti.cara, ti.claro, 0.15f), ti.claro, ti.hondo), hacia, 0f, w))
        sarga(Rect(w * 0.07f, arriba + esq * 0.6f, w * 0.93f, ys - h * 0.04f), 0.9f)
        // Las dos franjas oscuras del manto, cerca del orillo, siguiendo su borde.
        listOf(0.07f, 0.12f).forEach { f ->
            val y = ys - h * f
            drawPath(Path().apply { moveTo(w * 0.04f, y - inc * 0.92f); quadraticTo(w * 0.5f, y + h * 0.02f, w * 0.96f, y + inc * 0.92f) }, t.c(P.FRANJA), style = Stroke(h * 0.022f))
        }
        drawPath(Path().apply { moveTo(w * 0.05f, ys - inc); quadraticTo(w * 0.5f, ys + h * 0.02f, w * 0.95f, ys + inc) }, t.s(0.4f), style = Stroke(1f * u))
        // Los flecos del orillo.
        val flecos = Path()
        for (j in 0 until 9) {
            val f = (j + 0.5f) / 9f
            val x = w * (0.07f + 0.86f * f)
            val y = ys + (inc * (2f * f - 1f)) + h * 0.02f * 4f * f * (1f - f)
            flecos.moveTo(x, y); flecos.lineTo(x + (azar(sv, 66 + j) - 0.5f) * 1.2f * u, y + (2.2f + 1.6f * azar(sv, 75 + j)) * u)
        }
        drawPath(flecos, t.c(mezcla(ti.cara, P.LANA_CRUDA, 0.25f)), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        // La etiqueta, en la solapa.
        val top = arriba + h * 0.07f + 3f * u * azar(sv, 67)
        val et = Rect(w * 0.1f, top, w * 0.9f, top + 28f * u)
        etiqueta(v, medidor, et, t, P.PAPIRO_ETIQUETA, -3f + 6f * azar(sv, 64))
        // La vuelta de cordel, que hunde un poco el paño, y la bula de barro en el nudo.
        val cord = t.c(P.CORDEL)
        val arco = Path().apply { moveTo(w * 0.03f, yc - 1f * u); quadraticTo(w / 2, yc + h * 0.035f, w * 0.97f, yc - 1f * u) }
        translate(0f, -1.6f * u) { drawPath(arco, t.b(0.3f), style = Stroke(1.6f * u)) }
        translate(0f, 1.6f * u) { drawPath(arco, t.s(0.45f), style = Stroke(2.2f * u)) }
        drawPath(arco, cord, style = Stroke(1.7f * u))
        val nudo = Offset(w * (0.5f + 0.18f * lado), yc + h * 0.012f)
        drawLine(cord, nudo, nudo + Offset(lado * 1.5f * u, 7f * u), 1.1f * u, StrokeCap.Round)
        bullaArcontes(nudo, minOf(w * 0.16f, 4.6f * u), t.c(P.BARRO_CLARO), 1f, sv)
        filos(Rect(w * 0.05f, arriba, w * 0.95f, h), t, hacia)
    }
}
