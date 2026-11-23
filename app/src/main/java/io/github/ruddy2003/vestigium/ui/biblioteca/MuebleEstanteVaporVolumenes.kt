package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.CARTULINA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HOJALATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LONA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL_VIEJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PLOMO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos

/** Las escalas del ánimo de Vapor, la de día y la de noche (bajo, medio, alto). */
private val TONOS_VAPOR: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.VAPOR, false) to juegoTonos(TemaId.VAPOR, true) }

/**
 * Los tintes del cuero, la laca y la lona con que se dice el ánimo, en el orden de la escala del tema (de lo peor a lo mejor):
 * granate, teja, crema, cardenillo y verde hondo. Así los puntos de en medio son un color con cuerpo y no un pastel lavado.
 */
private val TINTES_VAPOR = listOf(Color(0xFF86281F), Color(0xFFB4532C), Color(0xFFC9A262), Color(0xFF4C8268), Color(0xFF245E55))

private fun cuadradoVapor(x: Float) = x * x

/** La luz que da en lo de arriba de las cajas. */
private val LUZ_CAJA = Color(0xFFFFF1D2)

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaVapor(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoVapor(t.red - a0.red - dr * f) + cuadradoVapor(t.green - a0.green - dg * f) + cuadradoVapor(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (pensada para la interfaz) y un
 * volumen claro se volvía negro de noche. Aquí el volumen es un objeto del cuarto: conserva su color de día y la luz del gas lo
 * apaga con la distancia. Se ubica el color en la escala de noche y se toma el mismo punto en la de día; si no está, queda igual.
 */
internal fun tonoDeDiaVapor(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_VAPOR
    val x = enEscalaVapor(t, noche) ?: return t
    return if (x < 0.5f) mezcla(dia[0], dia[1], x * 2) else mezcla(dia[1], dia[2], (x - 0.5f) * 2)
}

/** El tinte de un color de la escala de día: el mismo punto entre [TINTES_VAPOR]; si no está en la escala, el color tal cual. */
internal fun tinteVapor(t: Color?): Color? {
    if (t == null) return null
    val x = enEscalaVapor(t, TONOS_VAPOR.first) ?: return t
    val i = (x * (TINTES_VAPOR.size - 1)).coerceIn(0f, TINTES_VAPOR.size - 1.001f)
    return mezcla(TINTES_VAPOR[i.toInt()], TINTES_VAPOR[i.toInt() + 1], i - i.toInt())
}

/**
 * Las tres clases de entrada de Vapor ([MuebleEstanteVapor.clase], de la semilla del día):
 * - el libro de registro de pie, encuadernado en piel del color del ánimo: lomo redondo, cabezada, nervios con filetes dorados,
 *   las esquinas rozadas y el portaetiquetas de latón con la tarjeta del día; más nervios cuanto más escribiste;
 * - las cajas de documentos de hojalata lacada del color del ánimo, dos o tres apiladas y vistas por la testa: la tapa que monta,
 *   el filete dorado, el asa abatible, el lacado saltado donde asoma la hojalata, y el portatarjetas con el día en la de abajo;
 * - la saca de lona teñida del color del ánimo: blanda y asentada, el cuello fruncido y atado, el precinto de plomo, las costuras,
 *   un remiendo y la etiqueta de cartulina de ojal reforzado con el día.
 * Sin ánimo: piel castaña, laca negra y lona cruda. En los Sueños, una luna creciente dorada o pintada en cada una.
 */
internal object VolumenEstanteVapor {
    private const val TAM_DIA = 16f
    private const val TAM_MES = 6.5f

    fun DrawScope.volumenVapor(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireVapor(oscuro, v.noche)
        val p = lugarVolumenVapor(v.repisa, v.centro)
        val e = estufaNominalVapor(v.repisas)
        val k0 = cercaVapor(p.x, p.y, a, e)
        val f = frioVapor(p.x, p.y, a)
        val br = brasaVapor(p.x, p.y, a, e)
        // De dónde viene la luz: de día y al anochecer, de la ventana (izquierda); de noche, del gas o de la estufa.
        val hacia = when {
            !a.oscuro -> -1f
            br > 0.5f -> if (p.x > MuebleEstanteVapor.ESTUFA_X) -1f else 1f
            else -> if (p.x < MuebleEstanteVapor.GAS_X) 1f else -1f
        }
        val k = if (a.oscuro) maxOf(k0, 0.035f) else k0
        val tinte = tinteVapor(tonoDeDiaVapor(v.tono, oscuro))
        val clase = MuebleEstanteVapor.clase(v.semilla)
        giroTarjeta = 0f
        silueta = Path(); cimas = Path()
        when (clase) {
            0 -> libro(v, medidor, a, k, f, br, hacia, tinte)
            1 -> cajas(v, medidor, a, k, f, br, hacia, tinte)
            else -> saca(v, medidor, a, k, f, br, hacia, tinte)
        }
        // En los Sueños, la luna (o el ocaso) que cruza la pared pinta también la entrada, a su altura y corrida hacia la ventana
        // lo que la entrada sobresale de la pared: solo dentro de su silueta, sin cambiarle el color. El día se vuelve a entintar.
        if (a.noche) {
            val escala = MuebleEstanteVapor.ANCHO_NOMINAL * density
            val abajo = MuebleEstanteVapor.CORONA + (v.repisa * MuebleEstanteVapor.FILA + MuebleEstanteVapor.ALTO_REPISA.value) / MuebleEstanteVapor.ANCHO_NOMINAL
            val origen = Offset(p.x - size.width / 2f / escala, abajo - size.height / escala)
            if (LuzSuenosVapor.toca(a, origen, size.width / escala, size.height / escala, LuzSuenosVapor.VOLUMEN)) {
                with(LuzSuenosVapor) { luzEnVolumen(a, escala, origen, silueta, cimas) }
                rotate(giroTarjeta, pivoteTarjeta) { textoTarjeta(ultimaTarjeta, v, medidor, a) }
            }
        }
    }

    private fun mesCorto(mes: String): String {
        val m = mes.substringAfterLast(' ').trim()
        return if (m.length > 4) m.take(3) + "." else m
    }

    /** La tarjeta del día: el número grande y el mes chico, en tinta, sobre la cartulina [base] que casi no se apaga. */
    private fun DrawScope.tarjeta(r: Rect, v: Volumen, medidor: TextMeasurer, a: AireVapor, k: Float, base: Color) {
        val u = density
        drawRect(a.etiqueta(k, base), r.topLeft, r.size)
        drawRect(a.sombra(0.22f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        textoTarjeta(r, v, medidor, a)
        ultimaTarjeta = r
    }

    /** El día y el mes de la tarjeta [r], en tinta. */
    private fun DrawScope.textoTarjeta(r: Rect, v: Volumen, medidor: TextMeasurer, a: AireVapor) {
        val u = density
        val corte = r.top + r.height * 0.72f
        textoEn(medidor, v.dia, Rect(r.left, r.top + 0.5f * u, r.right, corte), PaletaVapor.LETRA, a.tinta, TAM_DIA)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left, corte - r.height * 0.06f, r.right, r.bottom - 0.6f * u), PaletaVapor.LETRA, a.tinta.copy(alpha = 0.85f), TAM_MES, minimo = 4.5f)
    }

    /**
     * Dónde quedó la tarjeta del volumen que se está pintando (y, en la saca, cuánto gira y sobre qué punto): la luz de los Sueños
     * que le pasa por encima la tiñe, y el día se vuelve a entintar encima para que se siga leyendo.
     */
    private var ultimaTarjeta = Rect.Zero
    private var giroTarjeta = 0f
    private var pivoteTarjeta = Offset.Zero
    /** La silueta del volumen que se está pintando y lo de arriba que mira a la luz (para la luz de los Sueños). */
    private var silueta = Path()
    private var cimas = Path()

    /** La luna creciente de los Sueños, en [c], de radio [r]: un círculo menos otro corrido. */
    private fun DrawScope.luna(c: Offset, r: Float, color: Color) {
        val luna = Path().apply {
            op(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) },
                Path().apply { addOval(Rect(c.x - r * 0.45f, c.y - r * 1.1f, c.x + r * 1.45f, c.y + r * 0.7f)) }, PathOperation.Difference)
        }
        drawPath(luna, color)
    }

    /** La sombra que la entrada echa en la pared del fondo y en la tabla, del lado contrario a la luz, y su línea de contacto. */
    private fun DrawScope.sombraArrojada(a: AireVapor, hacia: Float, arriba: Float) {
        val u = density; val w = size.width; val h = size.height
        val lado = -hacia
        val x = if (lado > 0) w else 0f
        val largo = (if (a.oscuro) 12f else 11f) * u
        val baja = 3.5f * u
        drawPath(poligonoVapor(x, h, x, arriba + baja, x + lado * largo, arriba + baja + largo * 0.4f, x + lado * largo, h), a.sombra(if (a.oscuro) 0.45f else 0.36f))
        drawPath(poligonoVapor(x, h, x, arriba + baja * 2f, x + lado * largo * 1.35f, arriba + baja * 2f + largo * 0.5f, x + lado * largo * 1.35f, h), a.sombra(0.12f))
        drawRect(a.sombra(0.5f), Offset(if (lado > 0) 0f else -largo * 0.6f, h - 1.6f * u), Size(w + largo * 0.6f, 1.6f * u))
    }

    /** El filo de la luz de noche en el canto que la mira y arriba; en los Sueños de noche, el roce de la luna a la izquierda. */
    private fun DrawScope.filo(l: Float, r: Float, arriba: Float, abajo: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        if (!a.oscuro) return
        val u = density
        if (k > 0.06f) {
            drawRect(a.brillo(0.8f, k, br), Offset(if (hacia > 0) r - 1.2f * u else l, arriba), Size(1.2f * u, abajo - arriba))
            drawRect(a.brillo(0.6f, k, br), Offset(l, arriba), Size(r - l, 1f * u))
        }
        if (a.lunar && f > 0.08f) drawRect(a.helado(0.4f * f), Offset(if (hacia > 0) l else r - 1f * u, arriba), Size(1f * u, abajo - arriba))
    }

    // ---------------------------------------------------------------- el libro de registro

    private fun DrawScope.libro(v: Volumen, medidor: TextMeasurer, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float, tinte: Color?) {
        val u = density; val w = size.width; val h = size.height
        val arriba = 3.6f * u
        silueta.addRect(Rect(0.4f * u, 0.3f * u, w - 0.4f * u, h)); cimas.addRect(Rect(0.4f * u, 0.3f * u, w - 0.4f * u, arriba + 1.3f * u))
        sombraArrojada(a, hacia, 0.5f * u)
        val piel = if (tinte != null) a.animo(mezcla(tinte, Color(0xFF1E140E), 0.12f), k, f, br) else a.luz(CUERO, k, f, br)
        val pielOsc = mezcla(piel, Color.Black, 0.32f)
        // Lo de arriba del libro, visto un poco desde encima: el canto de las tapas y el de las hojas, con sus pliegos.
        drawRect(pielOsc, Offset(0.5f * u, 0.3f * u), Size(w - 1f * u, arriba + 1f * u))
        drawRect(a.luz(PAPEL_VIEJO, k, f, br), Offset(1.9f * u, 0.8f * u), Size(w - 3.8f * u, arriba))
        drawRect(a.sombra(0.2f), Offset(1.9f * u, 0.8f * u + arriba * 0.45f), Size(w - 3.8f * u, 0.5f * u))
        drawRect(a.brillo(0.4f, maxOf(k, 0.3f), br), Offset(1.9f * u, 0.8f * u), Size(w - 3.8f * u, 0.6f * u))
        // El lomo redondo: arriba, la curva de la cofia.
        val forma = Path().apply {
            moveTo(0.4f * u, arriba + 2.2f * u); quadraticTo(w / 2, arriba - 2f * u, w - 0.4f * u, arriba + 2.2f * u)
            lineTo(w - 0.4f * u, h); lineTo(0.4f * u, h); close()
        }
        drawPath(forma, piel)
        clipPath(forma) {
            // Lo redondo del lomo: el claro del lado que mira a la luz, el brillo de la piel y la sombra del otro.
            val luzX = if (hacia < 0) w * 0.12f else w * 0.6f
            drawRect(a.brillo(0.22f, maxOf(k, 0.25f), br), Offset(luzX, 0f), Size(w * 0.28f, h))
            drawRect(a.brillo(0.32f, maxOf(k, 0.25f), br), Offset(luzX + w * 0.08f, 0f), Size(w * 0.06f, h))
            drawRect(a.sombra(0.18f), Offset(if (hacia < 0) w * 0.52f else w * 0.22f, 0f), Size(w * 0.2f, h))
            drawRect(a.sombra(0.36f), Offset(if (hacia < 0) w * 0.74f else 0f, 0f), Size(w * 0.26f, h))
            // La cabezada de dos colores bajo la cofia; el pie, gastado y sucio.
            val cab = Path()
            var x = w * 0.14f
            while (x < w * 0.86f) { cab.addRect(Rect(x, arriba + 1.4f * u, x + 1.2f * u, arriba + 2.8f * u)); x += 2.4f * u }
            drawPath(cab, a.luz(Color(0xFF9C2A22), k, f, br))
            drawRect(a.sombra(0.3f), Offset(0f, h - 3.2f * u), Size(w, 3.2f * u))
            drawOval(a.luz(mezcla(PAPEL_VIEJO, CUERO, 0.5f), k, f, br).copy(alpha = 0.28f), Offset(w * 0.15f, arriba + 3.4f * u), Size(w * 0.5f, 2.4f * u))
            drawOval(a.luz(mezcla(PAPEL_VIEJO, CUERO, 0.5f), k, f, br).copy(alpha = 0.22f), Offset(w * 0.3f, h - 5f * u), Size(w * 0.5f, 2.6f * u))
        }
        // Los cajos: el surco de cada lado del lomo, donde se doblan las tapas.
        drawRect(a.sombra(0.4f), Offset(w * 0.07f, arriba + 3f * u), Size(0.9f * u, h - arriba - 4f * u))
        drawRect(a.sombra(0.4f), Offset(w * 0.93f - 0.9f * u, arriba + 3f * u), Size(0.9f * u, h - arriba - 4f * u))
        // Los nervios y el tejuelo negro: uno encima de la etiqueta, el tejuelo debajo y más nervios cuanto más escribiste.
        val cw = minOf(w - 7f * u, 28f * u); val ch = 25f * u
        val card = Rect(w / 2 - cw / 2, arriba + 8.4f * u, w / 2 + cw / 2, arriba + 8.4f * u + ch)
        val oro = a.luz(LATON, maxOf(k, 0.2f), f, br)
        val nervios = mutableListOf(arriba + 5.2f * u, card.bottom + 3.4f * u)
        val tej = Rect(w * 0.14f, card.bottom + 7.4f * u, w * 0.86f, card.bottom + 7.4f * u + maxOf(8f * u, h * 0.13f))
        val y0 = tej.bottom + 3.6f * u; val y1 = h - 6f * u
        val resto = 1 + (v.lleno * 2.4f).toInt()
        if (y1 - y0 > 6f * u) for (i in 0 until resto) nervios.add(y0 + (y1 - y0) * (i + 0.35f) / resto)
        // El tejuelo: piel negra con su doble filete dorado (sin letras).
        if (tej.bottom < y1) {
            drawRect(a.luz(Color(0xFF1C1614), k, f, br), tej.topLeft, tej.size)
            drawRect(a.brillo(0.25f, maxOf(k, 0.25f), br), Offset(tej.left + tej.width * (if (hacia < 0) 0.12f else 0.62f), tej.top), Size(tej.width * 0.18f, tej.height))
            drawRect(oro, tej.deflate(1.3f * u).topLeft, tej.deflate(1.3f * u).size, style = Stroke(0.6f * u))
            drawRect(oro.copy(alpha = 0.6f), tej.deflate(2.5f * u).topLeft, tej.deflate(2.5f * u).size, style = Stroke(0.4f * u))
            if (v.noche) luna(tej.center, minOf(tej.height * 0.3f, 4f * u), a.luz(LATON_LUZ, maxOf(k, 0.5f), f, br))
            else drawPath(poligonoVapor(tej.center.x, tej.center.y - 1.6f * u, tej.center.x + 1.6f * u, tej.center.y, tej.center.x, tej.center.y + 1.6f * u, tej.center.x - 1.6f * u, tej.center.y), oro)
        }
        nervios.forEach { ny ->
            drawRect(a.sombra(0.5f), Offset(0.4f * u, ny + 1.3f * u), Size(w - 0.8f * u, 1.3f * u))
            drawRect(mezcla(piel, Color.White, 0.1f), Offset(0.4f * u, ny - 1.3f * u), Size(w - 0.8f * u, 2.6f * u))
            drawRect(a.brillo(0.5f, maxOf(k, 0.25f), br), Offset(0.4f * u, ny - 1.3f * u), Size(w - 0.8f * u, 0.8f * u))
            drawRect(oro, Offset(w * 0.1f, ny + 3.2f * u), Size(w * 0.8f, 0.5f * u))
        }
        // Un florón dorado en los entrenervios de abajo (un rombo con dos puntos).
        for (i in 2 until nervios.size - 1) {
            val c = Offset(w / 2, (nervios[i] + nervios[i + 1]) / 2 + 1.5f * u)
            if (nervios[i + 1] - nervios[i] > 9f * u) {
                val sz = minOf(w * 0.1f, 2.4f * u)
                drawPath(poligonoVapor(c.x, c.y - sz, c.x + sz * 0.7f, c.y, c.x, c.y + sz, c.x - sz * 0.7f, c.y), oro)
                drawCircle(oro, 0.6f * u, Offset(c.x - sz * 1.5f, c.y)); drawCircle(oro, 0.6f * u, Offset(c.x + sz * 1.5f, c.y))
            }
        }
        // La etiqueta de papel pegada al lomo, con su filete impreso y una esquina levantada donde asoma la piel.
        drawRect(a.sombra(0.3f), Offset(card.left + 0.6f * u, card.top + 0.8f * u), card.size)
        tarjeta(card, v, medidor, a, k, mezcla(PAPEL, PAPEL_VIEJO, 0.45f))
        drawRect(a.grano(Color(0xFF5A4A36), 0.55f), card.deflate(1.4f * u).topLeft, card.deflate(1.4f * u).size, style = Stroke(0.5f * u))
        val esq = if (azar(v.semilla, 33) < 0.5f) card.right else card.left
        val s0 = if (esq == card.right) -1f else 1f
        drawPath(poligonoVapor(esq, card.bottom, esq + s0 * 3.2f * u, card.bottom, esq, card.bottom - 3.2f * u), piel)
        drawPath(poligonoVapor(esq + s0 * 3.2f * u, card.bottom, esq, card.bottom - 3.2f * u, esq + s0 * 2.4f * u, card.bottom - 2.8f * u), a.etiqueta(k, PAPEL_VIEJO))
        filo(0.4f * u, w - 0.4f * u, arriba, h, a, k, f, br, hacia)
    }

    // ---------------------------------------------------------------- las cajas de hojalata lacada

    private fun DrawScope.cajas(v: Volumen, medidor: TextMeasurer, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float, tinte: Color?) {
        val u = density; val w = size.width; val h = size.height
        val tres = v.lleno > 0.55f
        val partes = if (tres) listOf(0.46f, 0.28f, 0.26f) else listOf(0.56f, 0.44f)
        val laca = if (tinte != null) a.animo(tinte, k, f, br) else a.luz(Color(0xFF1C1A1E), k, f, br)
        val arriba = 2.2f * u
        sombraArrojada(a, hacia, arriba + (h - arriba) * (1f - partes[0]))
        var y = h
        partes.forEachIndexed { i, fr ->
            val bh = (h - arriba) * fr
            val corre = if (i == 0) 0f else w * 0.05f * (if (azar(v.semilla, 40 + i) < 0.5f) -1f else 1f)
            val mete = if (i == 0) 0.4f * u else w * 0.06f
            val caja = Rect(mete + corre, y - bh, w - mete + corre, y)
            // La de abajo es un poco más oscura (más lejos de la luz de arriba).
            val color = if (i == 0) mezcla(laca, Color.Black, 0.06f) else laca
            caja(caja, i, i == partes.size - 1, v, medidor, a, k, f, br, hacia, color, tinte == null)
            y -= bh
        }
        filo(0.4f * u, w - 0.4f * u, y, h, a, k, f, br, hacia)
    }

    /**
     * Una caja de documentos vista de frente: lo de arriba de la tapa (un poco desde encima), la tapa que monta con su filete, el
     * cuerpo lacado con el brillo de la laca, el doble filete dorado con sus puntos en las esquinas, las asas de los costados que
     * sobresalen, el lacado saltado; la de abajo lleva el portatarjetas con el día; las de encima, la aldaba con su bocallave.
     */
    private fun DrawScope.caja(r: Rect, i: Int, ultima: Boolean, v: Volumen, medidor: TextMeasurer, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float,
                               laca: Color, negra: Boolean) {
        val u = density
        val cima = 2.2f * u
        val tapa = maxOf(4.5f * u, (r.height - cima) * 0.24f)
        val tp = Rect(r.left, r.top + cima, r.right, r.top + cima + tapa)
        val cuerpo = Rect(r.left + 0.8f * u, tp.bottom, r.right - 0.8f * u, r.bottom)
        val kk = maxOf(k, 0.25f)
        silueta.addRect(r)
        if (ultima) cimas.addRect(Rect(r.left, r.top, r.right, r.top + cima + 0.6f * u))
        // Las asas de los costados, que asoman por los lados a media altura.
        listOf(cuerpo.left - 1.4f * u, cuerpo.right).forEach { x -> drawRect(a.luz(LATON_OSC, k, f, br), Offset(x, cuerpo.top + cuerpo.height * 0.3f), Size(1.4f * u, 3f * u)) }
        // El cuerpo lacado.
        drawRect(laca, cuerpo.topLeft, cuerpo.size)
        val luzX = if (hacia < 0) cuerpo.left + cuerpo.width * 0.05f else cuerpo.right - cuerpo.width * 0.17f
        drawRect(a.brillo(if (negra) 0.2f else 0.28f, kk, br), Offset(luzX, cuerpo.top + 1f * u), Size(cuerpo.width * 0.12f, cuerpo.height - 2f * u))
        drawRect(a.brillo(0.16f, kk, br), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.12f), Size(cuerpo.width, cuerpo.height * 0.14f))
        drawRect(a.sombra(0.28f), Offset(if (hacia < 0) cuerpo.right - cuerpo.width * 0.18f else cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.18f, cuerpo.height))
        drawRect(a.sombra(0.4f), Offset(cuerpo.left, cuerpo.bottom - 1.2f * u), Size(cuerpo.width, 1.2f * u))
        // El doble filete dorado con sus puntos en las esquinas.
        val oro = a.luz(LATON_LUZ, kk, f, br)
        val fil = Rect(cuerpo.left + 2.2f * u, cuerpo.top + 2.4f * u, cuerpo.right - 2.2f * u, cuerpo.bottom - 2.2f * u)
        if (fil.width > 4f && fil.height > 4f) {
            drawRect(oro.copy(alpha = 0.9f), fil.topLeft, fil.size, style = Stroke(0.9f * u))
            val fil2 = fil.deflate(1.5f * u)
            if (fil2.width > 2f && fil2.height > 2f) drawRect(oro.copy(alpha = 0.55f), fil2.topLeft, fil2.size, style = Stroke(0.5f * u))
            listOf(fil.topLeft, Offset(fil.right, fil.top), Offset(fil.left, fil.bottom), fil.bottomRight).forEach { q -> drawCircle(oro, 1f * u, q) }
        }
        // El lacado saltado: la hojalata que asoma en los cantos.
        for (j in 0 until 3) {
            if (azar(v.semilla, 60 + i * 5 + j) < 0.45f) continue
            val enX = cuerpo.left + cuerpo.width * (if (j == 1) 0.93f else 0.05f + 0.85f * azar(v.semilla, 70 + i * 5 + j))
            val enY = if (j == 2) cuerpo.bottom - 1.4f * u else cuerpo.top + cuerpo.height * (0.3f + 0.5f * azar(v.semilla, 80 + j))
            drawOval(a.luz(HOJALATA, k, f, br), Offset(enX - 1.2f * u, enY - 0.8f * u), Size(2.6f * u, 1.6f * u))
        }
        // Lo de arriba de la tapa, visto desde encima, y la tapa que monta, con su filete y la sombra que echa en el cuerpo.
        drawRect(mezcla(laca, LUZ_CAJA, if (a.oscuro) 0.08f * kk else 0.22f), Offset(r.left + 0.6f * u, r.top), Size(r.width - 1.2f * u, cima + 0.6f * u))
        drawRect(a.sombra(0.35f), Offset(cuerpo.left, tp.bottom), Size(cuerpo.width, 1.6f * u))
        drawRect(mezcla(laca, Color.White, 0.04f), tp.topLeft, tp.size)
        drawRect(a.brillo(0.5f, kk, br), tp.topLeft, Size(tp.width, 0.9f * u))
        drawRect(a.sombra(0.3f), Offset(tp.left, tp.bottom - 1f * u), Size(tp.width, 1f * u))
        drawRect(oro.copy(alpha = 0.75f), Offset(tp.left + 2f * u, tp.center.y), Size(tp.width - 4f * u, 0.6f * u))
        val cx = cuerpo.center.x
        if (i == 0) {
            // La de abajo lleva el portatarjetas con el día.
            val cw = minOf(cuerpo.width - 6f * u, 28f * u)
            val chh = minOf(25f * u, cuerpo.height - 4f * u)
            val card = Rect(cx - cw / 2, cuerpo.center.y - chh / 2 + 0.5f * u, cx + cw / 2, cuerpo.center.y + chh / 2 + 0.5f * u)
            val marco = card.inflate(1.6f * u)
            drawRect(a.sombra(0.4f), Offset(marco.left + 0.8f * u, marco.top + 1f * u), marco.size)
            drawRect(a.luz(LATON, k, f, br), marco.topLeft, marco.size)
            drawRect(a.brillo(0.7f, maxOf(k, 0.35f), br), marco.topLeft, Size(marco.width, 0.8f * u))
            drawRect(a.luz(LATON_OSC, k, f, br), Offset(marco.left, marco.bottom - 0.8f * u), Size(marco.width, 0.8f * u))
            tarjeta(card, v, medidor, a, k, mezcla(PAPEL, PAPEL_VIEJO, 0.3f))
        } else {
            // Las de encima: la aldaba que baja de la tapa y la bocallave.
            val aw = minOf(cuerpo.width * 0.2f, 5.4f * u)
            val ald = Rect(cx - aw / 2, tp.top + tapa * 0.3f, cx + aw / 2, tp.bottom + cuerpo.height * 0.42f)
            drawRect(a.sombra(0.4f), Offset(ald.left + 0.7f * u, ald.top + 0.9f * u), ald.size)
            drawRect(a.luz(LATON, k, f, br), ald.topLeft, ald.size)
            drawRect(a.brillo(0.6f, kk, br), ald.topLeft, Size(0.8f * u, ald.height))
            drawRect(a.luz(LATON_OSC, k, f, br), Offset(ald.left, tp.bottom - 0.6f * u), Size(ald.width, 1.2f * u))
            drawCircle(a.luz(NEGRO, k, f, br), aw * 0.17f, Offset(cx, ald.bottom - aw * 0.55f))
            if (v.noche && i == 1) luna(Offset(cuerpo.left + cuerpo.width * 0.22f, cuerpo.center.y + 1f * u), minOf(cuerpo.width * 0.11f, 4f * u), a.luz(LATON_LUZ, maxOf(k, 0.5f), f, br))
        }
        if (ultima) {
            // La de arriba: el asa de llevar, tumbada sobre la tapa.
            val dx = minOf(r.width * 0.24f, 7.5f * u)
            drawArc(a.sombra(0.35f), 180f, 180f, false, Offset(cx - dx + 0.6f * u, r.top - 1.2f * u), Size(dx * 2, 4f * u), style = Stroke(1.3f * u))
            drawArc(a.luz(LATON, k, f, br), 180f, 180f, false, Offset(cx - dx, r.top - 1.8f * u), Size(dx * 2, 4f * u), style = Stroke(1.3f * u))
            listOf(cx - dx, cx + dx).forEach { x -> drawCircle(a.luz(LATON_OSC, k, f, br), 1.2f * u, Offset(x, r.top + 0.4f * u)) }
        }
    }

    // ---------------------------------------------------------------- la saca de lona

    private fun DrawScope.saca(v: Volumen, medidor: TextMeasurer, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float, tinte: Color?) {
        val u = density; val w = size.width; val h = size.height
        val lona = if (tinte != null) a.animo(mezcla(tinte, LONA, 0.12f), k, f, br) else a.luz(Color(0xFF8C7656), k, f, br)
        val cuello = h * 0.2f
        val hombro = h * (0.36f - 0.05f * v.lleno)
        sombraArrojada(a, hacia, hombro)
        val cuerpo = Path().apply {
            moveTo(w * 0.36f, cuello)
            cubicTo(w * 0.18f, cuello + h * 0.05f, 0f, hombro, w * 0.02f, h * 0.52f)
            cubicTo(w * 0.03f, h * 0.74f, -w * 0.01f, h * 0.92f, w * 0.06f, h * 0.975f)
            quadraticTo(w * 0.5f, h * 1.01f, w * 0.94f, h * 0.975f)
            cubicTo(w * 1.01f, h * 0.92f, w * 0.97f, h * 0.74f, w * 0.98f, h * 0.52f)
            cubicTo(w, hombro, w * 0.82f, cuello + h * 0.05f, w * 0.64f, cuello)
            close()
        }
        val orejas = Path().apply {
            moveTo(w * 0.38f, cuello + 1f * u)
            quadraticTo(w * 0.2f, h * 0.1f, w * 0.16f, h * 0.035f)
            quadraticTo(w * 0.3f, h * 0.05f, w * 0.38f, h * 0.085f)
            quadraticTo(w * 0.44f, 0f, w * 0.56f, h * 0.012f)
            quadraticTo(w * 0.6f, h * 0.06f, w * 0.64f, h * 0.075f)
            quadraticTo(w * 0.76f, h * 0.03f, w * 0.86f, h * 0.05f)
            quadraticTo(w * 0.76f, h * 0.11f, w * 0.62f, cuello + 1f * u)
            close()
        }
        silueta = Path().apply { op(cuerpo, orejas, PathOperation.Union) }
        drawPath(orejas, mezcla(lona, Color.Black, 0.08f))
        clipPath(orejas) {
            drawRect(a.sombra(0.3f), Offset(if (hacia < 0) w * 0.5f else 0f, 0f), Size(w * 0.5f, cuello))
            val pl = Path().apply { moveTo(w * 0.42f, cuello); lineTo(w * 0.3f, h * 0.05f); moveTo(w * 0.52f, cuello); lineTo(w * 0.5f, h * 0.02f); moveTo(w * 0.58f, cuello); lineTo(w * 0.72f, h * 0.05f) }
            drawPath(pl, a.sombra(0.35f), style = Stroke(0.8f * u))
        }
        drawPath(cuerpo, lona)
        clipPath(cuerpo) {
            // Lo panzón: la luz del lado que la mira y la sombra del otro y de abajo.
            val claro = if (hacia < 0) 0.04f else 0.46f
            drawOval(a.brillo(0.22f, maxOf(k, 0.3f), br), Offset(w * claro, h * 0.34f), Size(w * 0.5f, h * 0.46f))
            drawOval(a.brillo(0.16f, maxOf(k, 0.3f), br), Offset(w * (claro + 0.08f), h * 0.42f), Size(w * 0.26f, h * 0.26f))
            drawOval(a.sombra(0.32f), Offset(if (hacia < 0) w * 0.6f else -w * 0.25f, h * 0.24f), Size(w * 0.65f, h * 0.9f))
            drawRect(a.sombra(0.3f), Offset(0f, h * 0.88f), Size(w, h * 0.12f))
            // Los pliegues que bajan del cuello.
            val pliegues = Path().apply {
                moveTo(w * 0.42f, cuello); quadraticTo(w * 0.3f, h * 0.36f, w * 0.26f, h * 0.55f)
                moveTo(w * 0.58f, cuello); quadraticTo(w * 0.7f, h * 0.34f, w * 0.76f, h * 0.5f)
                moveTo(w * 0.5f, cuello); quadraticTo(w * 0.52f, h * 0.28f, w * 0.47f, h * 0.36f)
            }
            drawPath(pliegues, a.sombra(0.32f), style = Stroke(1.1f * u))
            drawPath(corridoVapor(pliegues, -1.1f * u * hacia), a.brillo(0.18f, maxOf(k, 0.3f), br), style = Stroke(0.7f * u))
            // La costura de un costado, en puntadas.
            val cost = Path()
            var yy = h * 0.42f
            while (yy < h * 0.95f) { cost.moveTo(w * 0.86f, yy); cost.lineTo(w * 0.865f, yy + 2f * u); yy += 3.6f * u }
            drawPath(cost, a.sombra(0.4f), style = Stroke(0.7f * u))
            // Un remiendo cosido, en algunas.
            if (azar(v.semilla, 90) < 0.5f) {
                val rm = Rect(w * 0.12f, h * 0.7f, w * 0.12f + minOf(w * 0.3f, 9f * u), h * 0.7f + minOf(w * 0.3f, 9f * u))
                drawRect(mezcla(lona, PAPEL_VIEJO, 0.22f), rm.topLeft, rm.size)
                drawRect(a.sombra(0.35f), rm.topLeft, rm.size, style = Stroke(0.6f * u))
            }
            if (tinte == null) {
                // La arpillera: la trama gruesa a la vista.
                val trama = Path()
                var ty = h * 0.3f
                while (ty < h) { trama.moveTo(0f, ty); trama.lineTo(w, ty + 1f * u); ty += 2.4f * u }
                drawPath(trama, a.sombra(0.18f), style = Stroke(0.6f * u))
            }
            if (v.noche) luna(Offset(w * 0.68f, h * 0.84f), minOf(w * 0.14f, 4.5f * u), a.luz(PAPEL, maxOf(k, 0.5f), f, br).copy(alpha = 0.85f))
        }
        // El cordel: las vueltas en el cuello, el nudo y sus dos cabos; el precinto de plomo.
        val cuerda = a.luz(Color(0xFFB89A68), k, f, br)
        val vueltas = Path()
        for (j in 0 until 3) { val yy = cuello - 2f * u + j * 1.8f * u; vueltas.moveTo(w * 0.35f, yy + 1.4f * u); vueltas.lineTo(w * 0.65f, yy - 0.4f * u) }
        drawPath(vueltas, a.sombra(0.4f), style = Stroke(1.6f * u))
        drawPath(vueltas, cuerda, style = Stroke(1.1f * u))
        val nudo = Offset(w * 0.64f, cuello)
        drawCircle(cuerda, 1.8f * u, nudo)
        val plomo = Offset(w * 0.8f, cuello + h * 0.1f)
        drawLine(cuerda, nudo, plomo, 0.9f * u)
        drawCircle(a.sombra(0.4f), 3f * u, plomo + Offset(0.6f * u, 0.8f * u))
        drawCircle(a.luz(PLOMO, k, f, br), 3f * u, plomo)
        drawCircle(a.luz(mezcla(PLOMO, NEGRO, 0.3f), k, f, br), 1.8f * u, plomo, style = Stroke(0.6f * u))
        drawCircle(a.brillo(0.5f, maxOf(k, 0.3f), br), 0.8f * u, plomo - Offset(1f * u, 1f * u))
        // La etiqueta de cartulina, colgada del nudo por su ojal reforzado, un poco ladeada.
        val cw = minOf(w - 6f * u, 27f * u); val chh = 32f * u
        val ojal = Offset(w * 0.5f, h * 0.36f)
        val giro = (azar(v.semilla, 91) - 0.5f) * 8f
        drawLine(cuerda, nudo, ojal, 0.8f * u)
        giroTarjeta = giro; pivoteTarjeta = ojal
        rotate(giro, ojal) {
            val tg = Rect(ojal.x - cw / 2, ojal.y - 3.5f * u, ojal.x + cw / 2, ojal.y - 3.5f * u + chh)
            val corte = 3f * u
            val forma = poligonoVapor(tg.left + corte, tg.top, tg.right - corte, tg.top, tg.right, tg.top + corte, tg.right, tg.bottom, tg.left, tg.bottom, tg.left, tg.top + corte)
            drawPath(corridoVapor(forma, -hacia * 1.2f * u, 1.4f * u), a.sombra(0.4f))
            drawPath(forma, a.etiqueta(k, CARTULINA))
            drawCircle(a.etiqueta(k, mezcla(CARTULINA, PAPEL, 0.5f)), 2.2f * u, ojal, style = Stroke(1.3f * u))
            drawCircle(a.sombra(0.6f), 0.9f * u, ojal)
            tarjeta(Rect(tg.left + 1f * u, ojal.y + 2.6f * u, tg.right - 1f * u, tg.bottom - 0.6f * u), v, medidor, a, k, CARTULINA)
        }
        filo(w * 0.04f, w * 0.96f, hombro, h * 0.96f, a, k, f, br, hacia)
    }
}
