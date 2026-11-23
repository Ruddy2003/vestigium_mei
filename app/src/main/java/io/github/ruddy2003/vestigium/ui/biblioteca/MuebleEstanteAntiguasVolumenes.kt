package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Temas
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las entradas del estante de Tierras Antiguas ([MuebleEstanteAntiguas]), tres objetos de la crónica según la semilla: el tomo
 * (alto, de pie, el lomo de cuero con sus nervios y su cofia, las cantoneras de latón, un cabujón engastado y la cinta de marcar
 * que asoma más cuanto más escribiste; alguno recostado contra el vecino), el rollo de varillas (la vitela enrollada de pie, con
 * su camisa de cuero ceñida por dos cordones, la espiral y la hoja que se levanta arriba, y las perillas torneadas) y el
 * envoltorio de cartas (un paño de lana teñido, blando, con la solapa en pico y su botón, atado con cordel). El ánimo es el cuero
 * o el paño, en todo el cuerpo ([materialAntiguas]); sin ánimo, cuero pardo sin teñir. El día y el mes van en pergamino. En los
 * Sueños, una media luna de plata.
 */
internal object VolumenEstanteAntiguas {
    private val P = PaletaAntiguas
    private val GEMAS = listOf(P.RUBI, P.ESMERALDA, P.ZAFIRO, P.AMATISTA)
    private val CINTAS = listOf(Color(0xFFA8302A), Color(0xFF2E6A4A), Color(0xFFC9A04A), Color(0xFF3A4A8A))

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra: dónde empieza cada franja y hacia qué va. */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.45f, 0.85f, 0.5f, 0.12f, -0.22f, -0.55f, -0.85f)

    /** Los meses cortos en español, para la etiqueta (el nombre de la luna ya lo dice la placa de la repisa). */
    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

    /** El mes corto de una entrada: se ubica el nombre de la luna entre las del tema; si no está, queda tal cual. */
    private fun mesCorto(mes: String): String {
        val i = Temas.LUNAS.indexOf(mes.uppercase())
        return if (i in MESES_CORTOS.indices) MESES_CORTOS[i] else mes
    }

    /** Los colores de un material con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(tinte: Color?, a: AireAntiguas, k: Float): Tinte {
        val m = tinte ?: P.CUERO
        val claro = mezcla(m, luzMueble(1f), if (tinte == null) 0.24f else 0.32f)
        val hondo = mezcla(m, sombraMueble(1f), 0.55f)
        return if (tinte != null) Tinte(a.animo(m, k), a.animo(claro, k), a.animo(hondo, k))
        else Tinte(a.luz(m, k), a.luz(claro, k), a.luz(hondo, k))
    }

    /** Un cuerpo redondo en [r] por franjas opacas (sin degradados): el filo, el claro, la cara, el hondo, hacia [hacia]. */
    private fun DrawScope.redondo(r: Rect, t: Tinte, hacia: Float) {
        for (i in 0 until 7) {
            val f0 = FRANJAS[i]; val f1 = FRANJAS[i + 1]
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(t.cara, t.claro, m) else mezcla(t.cara, t.hondo, -m)
            val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1
            drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + 0.6f, r.height))
        }
    }

    /**
     * El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp sin achicarse) y el mes debajo, en una de
     * 9.5 dp; así el día sale del mismo tamaño en las tres entradas. Si [r] es más bajo, se reparten en proporción.
     */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left, y0, r.right, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.Bold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left, y0 + hd - 1f * u, r.right, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, minimo = 5f)
    }

    /** La etiqueta de pergamino donde va el día: el papel con su luz, el borde tostado, el filete a pluma y el texto. */
    private fun DrawScope.etiqueta(v: Volumen, medidor: TextMeasurer, r: Rect, a: AireAntiguas, k: Float) {
        val u = density
        drawRect(a.sombra(0.4f), Offset(r.left + 0.9f * u, r.top + 1.2f * u), r.size)
        drawRect(a.etiqueta(P.PERGAMINO, k), r.topLeft, r.size)
        drawRect(a.etiqueta(P.VITELA_OSC, k).copy(alpha = 0.5f), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.12f))
        drawRect(a.etiqueta(P.VITELA, k).copy(alpha = 0.5f), r.topLeft, Size(r.width, r.height * 0.1f))
        drawRect(a.etiqueta(P.VITELA_OSC, k).copy(alpha = 0.4f), r.topLeft, Size(r.width * 0.08f, r.height))
        val dentro = r.deflate(1.2f * u)
        drawRect(a.luz(P.TINTA, k).copy(alpha = 0.45f), dentro.topLeft, dentro.size, style = Stroke(0.5f * u))
        diaYMes(v, medidor, r.deflate(1.6f * u), if (a.oscuro) mezcla(P.TINTA_NOCHE, P.TINTA, k) else a.luz(P.TINTA, maxOf(k, 0.75f)))
        if (a.oscuro && k > 0.2f) drawRect(P.LLAMA.copy(alpha = (0.3f * k).coerceAtMost(0.25f)), r.topLeft, Size(r.width, 1f * u))
    }

    /**
     * La luna de los Sueños: una media luna de plata recortada, sin disco alrededor (un círculo claro con su hueco oscuro se lee
     * como un ojo), con su canto oscuro para que se vea sobre lo claro y su sombra.
     */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireAntiguas) {
        val u = density
        val m = crecienteAntiguas(Offset.Zero, r)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, a.sombra(0.55f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDFE6F2))
            drawPath(m, Color(0xFF34405A), style = Stroke(0.9f * u))
            drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(-r * 0.6f, -r * 0.1f))
        }
    }

    /** El paño de la ventana (el ocaso o la luna de los Sueños) en el canto de arriba de una entrada: aclara y tiñe, sin velar. */
    private fun DrawScope.cantoPano(forma: Path, y0: Float, y1: Float, pano: PanoAntiguas?, fz: Float) {
        if (pano == null || fz <= 0.02f) return
        clipRect(0f, y0, size.width, y1) {
            clipPath(forma) { drawRect(pano.color.copy(alpha = (0.5f * fz * pano.fuerza).coerceAtMost(0.3f)), Offset.Zero, size, blendMode = BlendMode.Screen) }
        }
    }

    /** Lo de todas: la sombra que dejan en el fondo por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireAntiguas, hacia: Float, desde: Float, ancho: Float = size.width) {
        val u = density
        val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.45f), Offset(ancho, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.45f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.5f), Offset(-1f * u, h - 0.4f * u), Size(ancho + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira y el roce frío de la luna en el de la izquierda (de noche). */
    private fun DrawScope.filos(cuerpo: Rect, a: AireAntiguas, k: Float, f: Float, hacia: Float) {
        val u = density
        if (!a.lumbre) return
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        drawRect(a.filo(0.75f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (f > 0.03f) drawRect(a.lunar(0.55f * f), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    // ---------------------------------------------------------------- el tomo

    fun DrawScope.tomo(v: Volumen, medidor: TextMeasurer, a: AireAntiguas, k: Float, f: Float, hacia: Float, tinte: Color?, fz: Float, pano: PanoAntiguas?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        // Alguno, recostado contra el de la izquierda.
        val ladeo = if (azar(s, 70) > 0.84f) -2.6f else 0f
        rotate(ladeo, Offset(w, h)) { tomoDePie(v, medidor, a, k, f, hacia, tinte, fz, pano) }
    }

    private fun DrawScope.tomoDePie(v: Volumen, medidor: TextMeasurer, a: AireAntiguas, k: Float, f: Float, hacia: Float, tinte: Color?, fz: Float, pano: PanoAntiguas?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val top = h * 0.03f
        alrededor(a, hacia, top)
        val t = tinte(tinte, a, k)
        val lomo = Rect(0.5f * u, top, w - 0.5f * u, h)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(lomo, CornerRadius(w * 0.08f))) }
        // Las tapas de madera asoman un poco a los lados, arriba y abajo (el lomo es redondo).
        drawRect(a.luz(P.ROBLE_OSC, k), Offset(0f, top + h * 0.01f), Size(w, h - top - h * 0.01f))
        redondo(lomo, t, hacia)
        // El grano y el roce del cuero.
        for (j in 0 until 4) {
            val y = lomo.top + lomo.height * (0.1f + 0.85f * azar(s, 50 + j))
            drawLine(a.claro(0.18f), Offset(lomo.left + w * 0.2f * azar(s, 54 + j), y), Offset(lomo.left + w * (0.3f + 0.5f * azar(s, 58 + j)), y + 0.6f * u), 0.8f * u)
        }
        // La cofia de arriba y la de abajo.
        drawRoundRect(t.claro, Offset(lomo.left, lomo.top - 0.6f * u), Size(lomo.width, h * 0.026f), CornerRadius(w * 0.14f))
        drawRect(a.sombra(0.35f), Offset(lomo.left, lomo.top + h * 0.026f - 0.6f * u), Size(lomo.width, 1.2f * u))
        drawRect(t.hondo, Offset(lomo.left, h - h * 0.022f), Size(lomo.width, h * 0.022f))
        // Los nervios: cinco, con su luz y su sombra; los filetes dorados al lado.
        val nervios = floatArrayOf(0.11f, 0.45f, 0.58f, 0.74f, 0.88f)
        nervios.forEach { fy ->
            val y = h * fy
            drawRect(a.sombra(0.42f), Offset(lomo.left, y + 1.8f * u), Size(lomo.width, 1.3f * u))
            drawRect(t.hondo, Offset(lomo.left, y), Size(lomo.width, 2.4f * u))
            drawRect(t.claro, Offset(lomo.left, y - 0.6f * u), Size(lomo.width, 1.5f * u))
            drawRect(a.brillo(0.35f, k), Offset(if (hacia < 0f) lomo.left + lomo.width * 0.1f else lomo.right - lomo.width * 0.4f, y - 0.4f * u), Size(lomo.width * 0.3f, 0.8f * u))
            drawRect(a.luz(P.ORO, k).copy(alpha = 0.75f), Offset(lomo.left + lomo.width * 0.12f, y + 3.6f * u), Size(lomo.width * 0.76f, 0.6f * u))
        }
        // El tejuelo de pergamino con el día y el mes, entre el primer y el segundo nervio.
        val et = Rect(w * 0.1f, h * 0.145f, w * 0.9f, h * 0.425f)
        etiqueta(v, medidor, et, a, k)
        // El cabujón engastado (en los Sueños, la media luna), entre el segundo y el tercer nervio.
        val gc = Offset(w / 2, h * 0.515f + 1.2f * u)
        val gr = minOf(w * 0.17f, 4.4f * u)
        if (v.noche) luna(gc, minOf(w * 0.22f, 5.6f * u), a)
        else with(ObraEstanteAntiguas) { cabujon(gc, gr, GEMAS[Math.floorMod(s ushr 3, GEMAS.size)], a, k) }
        // Un rombo dorado entre los nervios de abajo (en los Sueños no: junto a la media luna podría leerse como un emblema).
        if (!v.noche) {
            val fc = Offset(w / 2, h * 0.66f + 1.4f * u)
            val fr = minOf(w * 0.13f, 3.4f * u)
            drawPath(poligonoAntiguas(fc.x, fc.y - fr, fc.x + fr * 0.75f, fc.y, fc.x, fc.y + fr, fc.x - fr * 0.75f, fc.y), a.luz(P.ORO, k).copy(alpha = 0.85f), style = Stroke(0.8f * u))
            drawCircle(a.luz(P.ORO, k).copy(alpha = 0.85f), 0.9f * u, fc)
        }
        // Las cantoneras de latón, arriba y abajo.
        val c = minOf(w * 0.24f, 6f * u)
        listOf(Offset(0f, top) to Offset(1f, 1f), Offset(w, top) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, d) ->
            val esq = poligonoAntiguas(o.x, o.y, o.x + d.x * c, o.y, o.x, o.y + d.y * c * 1.3f)
            drawPath(esq, a.luz(P.LATON, k))
            drawLine(a.luz(P.LATON_OSC, k), Offset(o.x + d.x * c, o.y), Offset(o.x, o.y + d.y * c * 1.3f), 0.7f * u)
            drawCircle(a.brillo(0.6f, k), 0.7f * u, Offset(o.x + d.x * c * 0.3f, o.y + d.y * c * 0.35f))
        }
        // El desgaste de las esquinas del cuero.
        drawOval(a.claro(0.16f), Offset(lomo.left, lomo.top + h * 0.02f), Size(w * 0.3f, h * 0.05f))
        drawOval(a.sombra(0.14f), Offset(lomo.left + w * 0.15f, lomo.top + lomo.height * (0.78f + 0.08f * azar(s, 62))), Size(w * 0.55f, h * 0.05f))
        cantoPano(forma, top, top + h * 0.12f, pano, fz)
        filos(lomo, a, k, f, hacia)
        // La cinta de marcar, que asoma por abajo y cuelga sobre el canto de la tabla: más larga cuanto más escribiste.
        val cinta = CINTAS[Math.floorMod(s ushr 5, CINTAS.size)]
        val cx = w * (0.3f + 0.4f * azar(s, 9))
        val largo = (3f + 10f * v.lleno) * u
        val lazo = Path().apply {
            moveTo(cx - 1.4f * u, h - 1f * u); lineTo(cx + 1.4f * u, h - 1f * u); lineTo(cx + 1.6f * u, h + largo)
            lineTo(cx, h + largo - 1.6f * u); lineTo(cx - 1.2f * u, h + largo); close()
        }
        drawPath(lazo, a.luz(cinta, k))
        drawLine(a.sombra(0.3f), Offset(cx + 1.2f * u, h), Offset(cx + 1.4f * u, h + largo - 1f * u), 0.6f * u)
    }

    // ---------------------------------------------------------------- el rollo

    fun DrawScope.rollo(v: Volumen, medidor: TextMeasurer, a: AireAntiguas, k: Float, f: Float, hacia: Float, tinte: Color?, fz: Float, pano: PanoAntiguas?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val arriba = h * 0.13f; val abajo = h * 0.93f
        val cil = Rect(w * 0.08f, arriba, w * 0.92f, abajo)
        val elip = minOf(cil.width * 0.17f, h * 0.045f)
        alrededor(a, hacia, arriba, w)
        val t = tinte(tinte, a, k)
        // La vitela se apaga como lo demás claro: crema hundido lejos de la luz, no pardo gris.
        val vit = Tinte(a.animo(P.VITELA, k), a.animo(mezcla(P.VITELA, Color.White, 0.4f), k), a.animo(P.VITELA_OSC, k))
        val madera = a.luz(P.ROBLE_LUZ, k)
        val maderaO = a.luz(P.ROBLE, k)
        // La perilla de abajo: el rollo se apoya en ella y no toca la tabla con la vitela.
        val pieR = Rect(w * 0.36f, abajo, w * 0.64f, h)
        drawRoundRect(maderaO, pieR.topLeft, pieR.size, CornerRadius(w * 0.1f))
        drawRect(a.brillo(0.3f, k), Offset(pieR.left + pieR.width * 0.18f, pieR.top), Size(pieR.width * 0.18f, pieR.height))
        // La vitela enrollada y su canto de abajo, que se ve un poco.
        drawOval(vit.hondo, Offset(cil.left, abajo - elip), Size(cil.width, elip * 2))
        redondo(cil, vit, hacia)
        val cam0 = cil.top + cil.height * (0.12f + 0.05f * azar(s, 11))
        val cam1 = cil.bottom - cil.height * (0.1f + 0.05f * azar(s, 12))
        val camisa = Rect(cil.left - 0.6f * u, cam0, cil.right + 0.6f * u, cam1)
        redondo(camisa, t, hacia)
        // El borde de la camisa, donde acaba la vuelta: una solapa con su sombra.
        val solapa = cil.left + cil.width * (if (hacia < 0f) 0.7f else 0.22f)
        drawRect(a.sombra(0.35f), Offset(solapa + 1f * u, cam0), Size(1.6f * u, cam1 - cam0))
        drawLine(t.claro, Offset(solapa, cam0), Offset(solapa, cam1), 1f * u)
        drawRect(t.hondo, Offset(camisa.left, cam0), Size(camisa.width, 1.4f * u))
        drawRect(t.hondo, Offset(camisa.left, cam1 - 1.4f * u), Size(camisa.width, 1.4f * u))
        val pespunte = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u))
        drawLine(a.luz(mezcla(P.VITELA, P.CUERO, 0.3f), k).copy(alpha = 0.7f), Offset(camisa.left + 2f * u, cam0 + 3f * u), Offset(camisa.right - 2f * u, cam0 + 3f * u), 0.7f * u, pathEffect = pespunte)
        drawLine(a.luz(mezcla(P.VITELA, P.CUERO, 0.3f), k).copy(alpha = 0.7f), Offset(camisa.left + 2f * u, cam1 - 3f * u), Offset(camisa.right - 2f * u, cam1 - 3f * u), 0.7f * u, pathEffect = pespunte)
        // El canto de la vitela que da la vuelta, en el tramo de arriba: la línea de la última hoja.
        val borde = cil.left + cil.width * (if (hacia < 0f) 0.82f else 0.12f)
        drawLine(vit.hondo, Offset(borde, cil.top), Offset(borde, cam0), 1f * u)
        // La boca de arriba: la espiral de la vitela, bien marcada, y la hoja de fuera que se levanta.
        val boca = Rect(cil.left, cil.top - elip, cil.right, cil.top + elip)
        drawOval(a.animo(P.VITELA, k), boca.topLeft, boca.size)
        val espiral = Path()
        val vueltas = 3
        val pasos = 60
        for (i in 0..pasos) {
            val tt = i / pasos.toFloat()
            val ang = tt * vueltas * 6.2832f
            val rad = 0.12f + 0.86f * tt
            val x = boca.center.x + kotlin.math.cos(ang) * boca.width * 0.5f * rad
            val y = boca.center.y + kotlin.math.sin(ang) * boca.height * 0.5f * rad
            if (i == 0) espiral.moveTo(x, y) else espiral.lineTo(x, y)
        }
        drawPath(espiral, a.luz(mezcla(P.VITELA_OSC, P.TINTA, 0.25f), k), style = Stroke(0.9f * u))
        val labio = Path().apply {
            val x0 = if (hacia < 0f) boca.right - boca.width * 0.22f else boca.left + boca.width * 0.22f
            val d = if (hacia < 0f) 1f else -1f
            moveTo(x0, boca.center.y + elip * 0.5f)
            quadraticTo(x0 + d * boca.width * 0.18f, boca.top - elip * 0.6f, x0 + d * boca.width * 0.26f, boca.top - elip * 0.2f)
            lineTo(x0 + d * boca.width * 0.2f, boca.center.y + elip * 0.3f)
            close()
        }
        drawPath(labio, a.animo(mezcla(P.VITELA, Color.White, 0.25f), k))
        drawPath(labio, vit.hondo, style = Stroke(0.6f * u))
        // La varilla y su perilla torneada, con el aro de latón.
        val vx = boca.center.x
        drawRect(madera, Offset(vx - w * 0.045f, h * 0.045f), Size(w * 0.09f, boca.center.y - h * 0.045f))
        drawRect(a.sombra(0.3f), Offset(vx + w * 0.015f, h * 0.045f), Size(w * 0.03f, boca.center.y - h * 0.045f))
        drawRoundRect(a.luz(P.LATON, k), Offset(vx - w * 0.11f, h * 0.06f), Size(w * 0.22f, h * 0.018f), CornerRadius(w * 0.03f))
        val bola = minOf(w * 0.16f, h * 0.035f)
        drawCircle(madera, bola, Offset(vx, bola + 0.5f * u))
        drawCircle(a.brillo(0.5f, k), bola * 0.35f, Offset(vx - bola * 0.35f, bola * 0.65f))
        drawCircle(a.sombra(0.3f), bola, Offset(vx, bola + 0.5f * u), style = Stroke(0.7f * u))
        // Los dos cordones atados, con sus cabos colgando.
        val cordon = a.luz(if (Math.floorMod(s, 3) == 0) P.LATON_OSC else P.CUERDA, k)
        listOf(cam0 + (cam1 - cam0) * 0.1f, cam1 - (cam1 - cam0) * 0.1f).forEachIndexed { i, y ->
            drawRect(a.sombra(0.35f), Offset(camisa.left, y + 1.6f * u), Size(camisa.width, 1.2f * u))
            drawRect(cordon, Offset(camisa.left, y - 0.9f * u), Size(camisa.width, 1.9f * u))
            drawRect(a.brillo(0.35f, k), Offset(camisa.left, y - 0.9f * u), Size(camisa.width, 0.6f * u))
            if (i == 1) {
                val nx = w * (0.35f + 0.3f * azar(s, 13))
                drawCircle(cordon, 1.8f * u, Offset(nx, y))
                drawLine(cordon, Offset(nx, y), Offset(nx - 2f * u, y + 7f * u + 5f * u * azar(s, 14)), 1.1f * u, StrokeCap.Round)
                drawLine(cordon, Offset(nx, y), Offset(nx + 2.4f * u, y + 6f * u), 1.1f * u, StrokeCap.Round)
                if (v.noche) luna(Offset(nx - 1f * u, y + 12f * u + 4f * u * azar(s, 14)), minOf(w * 0.2f, 5f * u), a)
            }
        }
        // La tira de pergamino con el día, metida bajo el cordón de arriba.
        val altoEt = minOf(27f * u + 3.2f * u, cil.height * 0.38f)
        val ancho = minOf(w * 0.78f, maxOf(w * 0.6f, 24f * u))
        val ey = cam0 + (cam1 - cam0) * 0.1f - 2f * u
        val et = Rect(w / 2 - ancho / 2, ey, w / 2 + ancho / 2, ey + altoEt)
        etiqueta(v, medidor, et, a, k)
        drawRect(cordon, Offset(et.left - 0.5f * u, ey + 1.2f * u), Size(et.width + 1f * u, 1.9f * u))
        val forma = Path().apply { addRect(Rect(boca.left, boca.top, boca.right, cil.bottom)) }
        cantoPano(forma, boca.top, cil.top + cil.height * 0.12f, pano, fz)
        filos(cil, a, k, f, hacia)
    }

    // ---------------------------------------------------------------- el envoltorio de cartas

    /**
     * Las cartas de la compañía envueltas en un paño de lana teñido: un bulto blando de pie, sin talle (el paño envuelve el fajo de
     * cartas), con el paño doblado y vencido arriba, por donde asoman las cartas por un lado; la solapa blanda que cruza el frente
     * desde un costado, con su dobladillo y un botón de hueso en la punta; el cordón que da dos vueltas y apenas frunce el paño; los
     * pliegues de la caída, el paño que se derrama sobre la tabla y la tablilla de pergamino metida bajo el cordón.
     */
    fun DrawScope.envoltorio(v: Volumen, medidor: TextMeasurer, a: AireAntiguas, k: Float, f: Float, hacia: Float, tinte: Color?, fz: Float, pano: PanoAntiguas?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val lado = if (Math.floorMod(s, 2) == 0) 1f else -1f
        fun x(f0: Float) = if (lado > 0f) w * f0 else w * (1f - f0)
        val top = h * (0.1f + 0.03f * azar(s, 20))
        alrededor(a, hacia, top, w)
        val t = tinte(tinte, a, k)
        // Las cartas que asoman por arriba, del lado de la abertura: hojas plegadas, una más alta que otra.
        for (i in 0 until 3) {
            val bx = x(0.5f + 0.13f * i); val alto = h * (0.07f + 0.035f * i) * (0.8f + 0.4f * azar(s, 40 + i))
            val gir = (-6f + 6f * i) * lado
            rotate(gir, Offset(bx, top + h * 0.04f)) {
                val hoja = Rect(bx - w * 0.14f, top - alto, bx + w * 0.14f, top + h * 0.05f)
                drawRect(a.sombra(0.3f), Offset(hoja.left + 1f * u, hoja.top + 1.2f * u), hoja.size)
                drawRect(a.etiqueta(if (i == 1) P.VITELA else mezcla(P.VITELA, P.PERGAMINO, 0.55f), k), hoja.topLeft, hoja.size)
                drawLine(a.luz(P.TINTA, k).copy(alpha = 0.35f), Offset(hoja.left + hoja.width * 0.2f, hoja.top + alto * 0.35f), Offset(hoja.right - hoja.width * 0.2f, hoja.top + alto * 0.35f), 0.6f * u)
                if (i == 2) drawCircle(a.luz(P.RUBIA, k), 1.6f * u, Offset(hoja.center.x, hoja.top + alto * 0.62f))
            }
        }
        // El cuerpo: un bloque blando, de costados apenas abombados (sin talle); el paño se derrama abajo.
        val yc = h * (0.58f + 0.05f * azar(s, 24))
        val cuerpo = Path().apply {
            moveTo(x(0.06f), top + h * 0.03f)
            cubicTo(x(0.25f), top - h * 0.01f, x(0.6f), top + h * 0.02f, x(0.95f), top + h * 0.005f)
            cubicTo(x(1.0f), h * 0.35f, x(0.97f), yc, x(0.97f), yc + h * 0.04f)
            cubicTo(x(0.98f), h * 0.8f, x(1.04f), h * 0.92f, x(1.02f), h - h * 0.01f)
            lineTo(x(-0.02f), h - h * 0.01f)
            cubicTo(x(-0.04f), h * 0.92f, x(0.03f), h * 0.8f, x(0.03f), yc + h * 0.04f)
            cubicTo(x(0.02f), yc, x(-0.01f), h * 0.35f, x(0.06f), top + h * 0.03f)
            close()
        }
        drawPath(cuerpo, t.cara)
        clipPath(cuerpo) {
            redondo(Rect(0f, top, w, h), Tinte(t.cara, mezcla(t.cara, t.claro, 0.55f), mezcla(t.cara, t.hondo, 0.7f)), hacia)
            // El paño doblado arriba: una vuelta blanda que cae sobre el frente.
            val doblez = Path().apply {
                moveTo(0f, top); lineTo(w, top)
                lineTo(w, top + h * 0.07f)
                cubicTo(w * 0.7f, top + h * 0.1f, w * 0.35f, top + h * 0.055f, 0f, top + h * 0.09f)
                close()
            }
            drawPath(doblez, t.claro.copy(alpha = 0.55f))
            drawPath(Path().apply { moveTo(0f, top + h * 0.09f); cubicTo(w * 0.35f, top + h * 0.055f, w * 0.7f, top + h * 0.1f, w, top + h * 0.07f) }, t.hondo, style = Stroke(1.6f * u))
            // Los pliegues de la caída, bajo el cordón: blandos, que se abren hacia abajo.
            for (i in 0 until 4) {
                val fx = 0.15f + 0.24f * i + 0.05f * (azar(s, 30 + i) - 0.5f)
                val pl = Path().apply {
                    moveTo(w * fx, yc + h * 0.04f)
                    quadraticTo(w * (fx + 0.04f * (azar(s, 34 + i) - 0.5f)), h * 0.8f, w * (fx + (fx - 0.5f) * 0.15f), h)
                }
                drawPath(pl, t.hondo, style = Stroke(2f * u))
                translate(-1.4f * u * hacia, 0f) { drawPath(pl, t.claro.copy(alpha = 0.6f), style = Stroke(1.1f * u)) }
            }
            // Las arrugas de arriba: cortas, en abanico desde el cordón.
            for (i in 0 until 3) {
                val q = Offset(w * (0.25f + 0.25f * i), yc - h * 0.02f)
                drawLine(t.hondo, q, q + Offset(w * (0.06f * (i - 1)), -h * (0.08f + 0.03f * azar(s, 50 + i))), 1.2f * u)
            }
            // La lana: una trama fina, y la sombra del bulto donde pesa sobre la tabla.
            val trama = Path()
            var y = top
            while (y < h) { trama.moveTo(0f, y); trama.lineTo(w, y + 1f * u); y += 2.4f * u }
            drawPath(trama, a.sombra(0.07f), style = Stroke(0.5f * u))
            drawOval(a.sombra(0.22f), Offset(w * 0.04f, h * 0.9f), Size(w * 0.92f, h * 0.14f))
        }
        drawPath(cuerpo, t.hondo, style = Stroke(0.9f * u))
        // El paño que se derrama sobre la tabla, por delante.
        val vuelo = Path().apply {
            moveTo(x(0.2f), h - h * 0.05f)
            quadraticTo(x(0.6f), h - h * 0.08f, x(1.05f), h - h * 0.015f)
            lineTo(x(1.05f), h + h * 0.012f)
            quadraticTo(x(0.6f), h + h * 0.02f, x(0.15f), h + h * 0.008f)
            close()
        }
        drawPath(vuelo, t.hondo)
        drawPath(vuelo, t.claro.copy(alpha = 0.45f), style = Stroke(0.8f * u))
        // La solapa blanda que cruza el frente desde un costado, con el borde ondeado y su dobladillo.
        val punta = Offset(x(0.68f + 0.1f * azar(s, 22)), top + h * (0.3f + 0.06f * azar(s, 23)))
        val solapa = Path().apply {
            moveTo(x(0.01f), top + h * 0.06f)
            cubicTo(x(0.25f), top + h * 0.08f, x(0.5f), punta.y - h * 0.12f, punta.x, punta.y - h * 0.02f)
            quadraticTo(punta.x + w * 0.04f * lado, punta.y + h * 0.03f, punta.x - w * 0.05f * lado, punta.y + h * 0.05f)
            cubicTo(x(0.4f), punta.y + h * 0.1f, x(0.18f), punta.y + h * 0.16f, x(0.01f), punta.y + h * 0.2f)
            close()
        }
        translate(1.4f * u * lado, 2.2f * u) { drawPath(solapa, a.sombra(0.4f)) }
        drawPath(solapa, mezcla(t.cara, t.claro, if (hacia < 0f) 0.35f else 0.15f))
        clipPath(solapa) {
            listOf(0.2f, 0.42f).forEach { fx ->
                val pl = Path().apply { moveTo(x(0.02f), top + h * (0.1f + fx * 0.3f)); quadraticTo(x(fx + 0.15f), punta.y - h * 0.02f + h * fx * 0.1f, punta.x - w * 0.1f * lado, punta.y + h * 0.03f) }
                drawPath(pl, t.hondo.copy(alpha = 0.8f), style = Stroke(1.4f * u))
            }
            val trama = Path()
            var y = top
            while (y < h * 0.7f) { trama.moveTo(0f, y); trama.lineTo(w, y + 1f * u); y += 2.4f * u }
            drawPath(trama, a.sombra(0.07f), style = Stroke(0.5f * u))
        }
        drawPath(solapa, t.hondo, style = Stroke(1.3f * u))
        // El cordón: dos vueltas algo sesgadas, que apenas fruncen el paño.
        val cord = a.luz(P.CUERDA, k)
        listOf(yc - h * 0.02f to yc + h * 0.01f, yc + h * 0.035f to yc + h * 0.02f).forEach { (y0, y1) ->
            val v0 = Offset(x(-0.02f), y0); val v1 = Offset(x(1.02f), y1)
            drawLine(a.sombra(0.4f), v0 + Offset(0f, 1.8f * u), v1 + Offset(0f, 1.8f * u), 1.2f * u)
            drawLine(cord, v0, v1, 1.9f * u)
            drawLine(a.brillo(0.3f, k), v0 - Offset(0f, 0.6f * u), v1 - Offset(0f, 0.6f * u), 0.5f * u)
        }
        // La tablilla de pergamino con el día, metida bajo el cordón.
        val altoEt = minOf(27f * u + 3.2f * u, h * 0.32f)
        val ancho = minOf(w * 0.78f, maxOf(w * 0.6f, 24f * u))
        val ey = yc - h * 0.02f - 2.2f * u
        val dx = w * 0.04f * (azar(s, 27) - 0.5f)
        val et = Rect(w / 2 - ancho / 2 + dx, ey, w / 2 + ancho / 2 + dx, ey + altoEt)
        etiqueta(v, medidor, et, a, k)
        val yIz = if (lado > 0f) yc - h * 0.02f else yc + h * 0.01f
        val yDe = if (lado > 0f) yc + h * 0.01f else yc - h * 0.02f
        drawLine(cord, Offset(et.left - 0.5f * u, yIz + (yDe - yIz) * (et.left / w)), Offset(et.right + 0.5f * u, yIz + (yDe - yIz) * (et.right / w)), 1.9f * u)
        // El botón de hueso en la punta de la solapa (en los Sueños, una media luna de plata) y el cordón que baja de él.
        if (v.noche) luna(punta + Offset(-w * 0.03f * lado, h * 0.01f), minOf(w * 0.2f, 5.2f * u), a)
        else {
            val bl = minOf(w * 0.16f, 4.4f * u); val bg = bl * 0.42f
            val b = punta + Offset(-w * 0.03f * lado, h * 0.012f)
            drawLine(cord, b, Offset(b.x + w * 0.02f * lado, yc - h * 0.02f), 1.3f * u)
            rotate(-30f * lado, b) {
                drawRoundRect(a.sombra(0.45f), Offset(b.x - bl + 0.8f * u, b.y - bg + 1.2f * u), Size(bl * 2, bg * 2), CornerRadius(bg))
                drawRoundRect(a.animo(Color(0xFFE8DEC6), k), Offset(b.x - bl, b.y - bg), Size(bl * 2, bg * 2), CornerRadius(bg))
                drawRect(a.brillo(0.5f, k), Offset(b.x - bl * 0.7f, b.y - bg * 0.6f), Size(bl * 1.2f, 0.8f * u))
                drawLine(a.sombra(0.35f), Offset(b.x - bl * 0.3f, b.y - bg), Offset(b.x - bl * 0.3f, b.y + bg), 0.7f * u)
                drawLine(a.sombra(0.35f), Offset(b.x + bl * 0.3f, b.y - bg), Offset(b.x + bl * 0.3f, b.y + bg), 0.7f * u)
            }
        }
        cantoPano(cuerpo, 0f, top + h * 0.12f, pano, fz)
        filos(Rect(w * 0.03f, top + h * 0.05f, w * 0.97f, h), a, k, f, hacia)
    }
}
