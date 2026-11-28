package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las entradas del estante de Orden ([MuebleEstanteOrden]), tres cosas del archivo de la orden según la semilla: el manual
 * encadenado (alto, de cuero del color del ánimo, con sus nervios, las cantoneras de acero, la placa de latón con el día y la
 * cadena que baja de su grapa a la barra de la repisa), el carcaj de planos (un cilindro de cuero del color del ánimo con su aro
 * y su pie de latón, la correa con hebilla y los rollos de plano que asoman, más cuanto más escribiste) y la carpeta de campaña
 * (de lona encerada del color del ánimo, blanda y panzona, con los pliegos que asoman arriba, la correa con su hebilla y las
 * puntas de latón). Sin ánimo: cuero pardo y lona cruda. En los Sueños, una media luna de plata.
 */
internal object VolumenEstanteOrden {
    private val P = PaletaOrden

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Los meses cortos en español, para la placa (el año ya lo dice el rótulo de la repisa). */
    private val MESES_CORTOS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

    /** El mes corto de una entrada: de «NOV-11» del tema sale «nov.»; si no se entiende, el nombre tal cual, acortado. */
    private fun mesCorto(mes: String): String {
        val n = mes.filter { it.isDigit() }.take(2).toIntOrNull()
        if (n != null && n in 1..12) return MESES_CORTOS[n - 1]
        return if (mes.length > 4) mes.take(3).lowercase() + "." else mes.lowercase()
    }

    /** La luz que le toca a una entrada: la de vela ([k]), la de guardia ([kf]) y la mancha de la luminaria de día ([sol]). */
    private class Luz(val a: AireOrden, val k: Float, val kf: Float, val sol: Float) {
        fun c(col: Color, f: Float = 0f): Color = a.luminaria(a.dia(col, f, k, kf), sol * 0.4f)
        fun animo(t: Color): Color = a.luminaria(a.animo(t, k, kf), sol * 0.4f)
    }

    /** Los colores de un material con la luz del momento: su cara, su claro y su hondo. */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(material: Color?, defecto: Color, l: Luz, varia: Float = 0f): Tinte {
        val m0 = material ?: defecto
        val m = if (varia > 0f) mezcla(m0, P.LUZ_FRIA, varia) else if (varia < 0f) mezcla(m0, P.SOMBRA_FRIA, -varia) else m0
        val claro = mezcla(m, P.LUZ_FRIA, 0.3f)
        val hondo = mezcla(m, P.SOMBRA_FRIA, 0.5f)
        return if (material != null) Tinte(l.animo(m), l.animo(claro), l.animo(hondo)) else Tinte(l.c(m), l.c(claro), l.c(hondo))
    }

    /** El día y el mes, centrados en [r]: el día en una franja de 17.5 dp (cabe a 16 sp) y el mes debajo, en una de 9.5 dp. */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left - 1f * u, y0, r.right + 1f * u, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.Bold, minimo = 11f)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left - 1f * u, y0 + hd - 1.4f * u, r.right + 1f * u, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, peso = FontWeight.Medium, minimo = 5f)
    }

    /** La placa de latón del día en [r]: el marco en relieve, el filete hundido, los cuatro tornillos, su sombra y el día y el mes. */
    private fun DrawScope.placa(v: Volumen, medidor: TextMeasurer, r: Rect, l: Luz) {
        val u = density
        val a = l.a
        val color = a.luminaria(a.placa(l.k, l.kf), l.sol * 0.3f)
        val hondo = mezcla(color, P.LATON_HONDO, 0.55f)
        drawRect(a.sombra(0.5f), r.topLeft + Offset(1f * u, 1.4f * u), r.size)
        drawRect(color, r.topLeft, r.size)
        drawRect(if (a.oscuro) P.LLAMA.copy(alpha = 0.35f * l.k + 0.06f) else P.LATON_BRILLO.copy(alpha = 0.8f), r.topLeft, Size(r.width, 1.1f * u))
        drawRect(hondo, Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        drawRect(mezcla(color, P.LATON_HONDO, 0.4f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        drawRect(mezcla(color, P.LATON_HONDO, 0.35f), Offset(r.left + 2.2f * u, r.top + 2.2f * u), Size(r.width - 4.4f * u, r.height - 4.4f * u), style = Stroke(0.6f * u))
        listOf(Offset(r.left + 1.6f * u, r.top + 1.6f * u), Offset(r.right - 1.6f * u, r.top + 1.6f * u), Offset(r.left + 1.6f * u, r.bottom - 1.6f * u), Offset(r.right - 1.6f * u, r.bottom - 1.6f * u))
            .forEach { drawCircle(hondo, 0.8f * u, it) }
        diaYMes(v, medidor, Rect(r.left + 1.4f * u, r.top + 1.2f * u, r.right - 1.4f * u, r.bottom - 1f * u), a.tinta())
    }

    /** Lo de todas: la sombra que dejan en el muro por la rendija con la vecina y la de contacto en la repisa. */
    private fun DrawScope.alrededor(a: AireOrden, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.45f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.45f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.55f), Offset(-1f * u, h - 0.5f * u), Size(w + 2f * u, 2.6f * u))
    }

    /**
     * Cuánto rojo del piloto del pilar le llega a la entrada que se está dibujando (lo pone el estante antes de cada una: cada
     * volumen se dibuja en su propio lienzo y no sabe dónde está el piloto).
     */
    var rojo = 0f

    /**
     * El filo de la vela en el canto que la mira (de noche y en la vigilia), el frío de la guardia en el otro y, cerca del piloto,
     * su rojo en el canto de la izquierda.
     */
    private fun DrawScope.filos(cuerpo: Rect, a: AireOrden, k: Float, kf: Float, hacia: Float) {
        val u = density
        if (!a.lumbre) return
        if (rojo > 0.05f) {
            drawRect(P.PILOTO.copy(alpha = (0.85f * rojo).coerceAtMost(0.8f)), Offset(cuerpo.left, cuerpo.top), Size(1.4f * u, cuerpo.height))
            drawRect(P.PILOTO.copy(alpha = (0.18f * rojo).coerceAtMost(0.16f)), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.45f, cuerpo.height))
        }
        val x = if (hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
        if (!a.dormida) drawRect(a.filo(0.7f * k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        if (kf > 0.08f) {
            drawRect(a.filoFrio(0.6f * kf), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
            drawRect(a.filoFrio(0.7f * kf), Offset(cuerpo.left + 1f * u, cuerpo.top), Size(cuerpo.width - 2f * u, 1.2f * u))
        }
    }

    /** La cadena que baja de [desde] a [hasta], con su comba hacia [lado] (en px): eslabones de canto y de frente alternados. */
    private fun DrawScope.cadena(desde: Offset, hasta: Offset, lado: Float, l: Luz) {
        val u = density
        val a = l.a
        val paso = 4.6f * u
        val d = (hasta - desde).getDistance()
        val n = maxOf(2, (d / paso).toInt())
        val acero = l.c(P.ACERO_CLARO, -0.1f)
        val hondo = l.c(P.ACERO_HONDO)
        val luz = a.destello(0.75f, l.k, l.kf)
        for (j in 0 until n) {
            val t = (j + 0.5f) / n
            val comba = lado * 4f * t * (1f - t)
            val p = Offset(desde.x + (hasta.x - desde.x) * t + comba, desde.y + (hasta.y - desde.y) * t)
            val dx = (hasta.x - desde.x) + lado * 4f * (1f - 2f * t); val dy = hasta.y - desde.y
            val ang = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
            rotate(ang, p) {
                if (j % 2 == 0) {
                    drawOval(a.sombra(0.45f), Offset(p.x - paso * 0.72f + 0.9f * u, p.y - paso * 0.42f + 1f * u), Size(paso * 1.44f, paso * 0.84f), style = Stroke(1.6f * u))
                    drawOval(acero, Offset(p.x - paso * 0.72f, p.y - paso * 0.42f), Size(paso * 1.44f, paso * 0.84f), style = Stroke(1.6f * u))
                    drawArc(luz, 190f, 90f, false, Offset(p.x - paso * 0.72f, p.y - paso * 0.42f), Size(paso * 1.44f, paso * 0.84f), style = Stroke(0.7f * u))
                } else {
                    drawLine(a.sombra(0.4f), Offset(p.x - paso * 0.62f + 0.8f * u, p.y + 1f * u), Offset(p.x + paso * 0.62f + 0.8f * u, p.y + 1f * u), 2f * u, StrokeCap.Round)
                    drawLine(hondo, Offset(p.x - paso * 0.66f, p.y), Offset(p.x + paso * 0.66f, p.y), 2.1f * u, StrokeCap.Round)
                    drawLine(acero, Offset(p.x - paso * 0.5f, p.y - 0.5f * u), Offset(p.x + paso * 0.5f, p.y - 0.5f * u), 0.9f * u, StrokeCap.Round)
                }
            }
        }
    }

    /** Una cantonera de acero de punta apuntada, arriba ([arriba]) o abajo del lomo, en [r] (su punta llega a [punta]). */
    private fun DrawScope.cantonera(r: Rect, punta: Float, arriba: Boolean, l: Luz, hacia: Float) {
        val u = density
        val a = l.a
        val w = r.width
        val forma = Path().apply {
            if (arriba) {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom)
                cubicTo(r.right - w * 0.25f, r.bottom, r.center.x + w * 0.12f, punta - (punta - r.bottom) * 0.3f, r.center.x, punta)
                cubicTo(r.center.x - w * 0.12f, punta - (punta - r.bottom) * 0.3f, r.left + w * 0.25f, r.bottom, r.left, r.bottom)
            } else {
                moveTo(r.left, r.bottom); lineTo(r.right, r.bottom); lineTo(r.right, r.top)
                cubicTo(r.right - w * 0.25f, r.top, r.center.x + w * 0.12f, punta + (r.top - punta) * 0.3f, r.center.x, punta)
                cubicTo(r.center.x - w * 0.12f, punta + (r.top - punta) * 0.3f, r.left + w * 0.25f, r.top, r.left, r.top)
            }
            close()
        }
        translate(0.8f * u, 1.2f * u) { drawPath(forma, a.sombra(0.45f)) }
        drawPath(forma, l.c(P.ACERO_MEDIO))
        clipPath(forma) {
            redondoOrden(Rect(r.left, minOf(r.top, punta), r.right, maxOf(r.bottom, punta)), l.c(P.ACERO_MEDIO), l.c(P.ACERO_CLARO, 0.1f), l.c(P.ACERO_HONDO), hacia, 0.9f)
        }
        drawPath(forma, a.sombra(0.35f), style = Stroke(0.7f * u))
        drawRect(a.destello(0.55f, l.k, l.kf), Offset(r.left, if (arriba) r.top else r.bottom - 1f * u), Size(r.width, 0.9f * u))
        val ry = if (arriba) r.top + (punta - r.top) * 0.45f else r.bottom - (r.bottom - punta) * 0.45f
        drawCircle(l.c(P.ACERO_HONDO), 1.1f * u, Offset(r.center.x, ry))
        drawCircle(a.destello(0.6f, l.k, l.kf), 0.45f * u, Offset(r.center.x - 0.4f * u, ry - 0.4f * u))
    }

    // ---------------------------------------------------------------- el manual encadenado

    /**
     * El manual encadenado, de pie: el lomo redondo de cuero del color del ánimo con su luz y su sombra, las cantoneras de acero
     * de punta apuntada arriba y abajo, los nervios en relieve, los hierros en seco, la placa de latón con el día y el mes y, más
     * abajo, la argolla con la cadena que baja a la barra de latón de la repisa.
     */
    fun DrawScope.manual(v: Volumen, medidor: TextMeasurer, a: AireOrden, k: Float, kf: Float, hacia: Float, sol: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kf, sol)
        val hacia2 = if (a.oscuro && !a.dormida) hacia else -1f
        alrededor(a, hacia, 6f * u)
        val t = tinte(material, P.CUERO_PARDO, l, (azar(s, 4) - 0.5f) * 0.12f)
        val cuerpo = Rect(0.5f * u, 0f, w - 0.5f * u, h)
        redondoOrden(cuerpo, t.cara, t.claro, t.hondo, hacia2)
        // El grano y el roce del cuero.
        for (j in 0 until 5) {
            val y = h * (0.2f + 0.7f * azar(s, 40 + j)); val x = w * (0.12f + 0.5f * azar(s, 50 + j))
            drawLine(a.brillo(0.18f, k), Offset(x, y), Offset(x + w * 0.22f, y + 0.5f * u), 0.6f * u)
        }
        // La cantonera de arriba, de punta apuntada.
        val capA = h * (0.05f + 0.012f * azar(s, 5)); val capB = h * 0.05f
        cantonera(Rect(0f, 0f, w, capA), capA + h * 0.045f, true, l, hacia2)
        // La placa de latón del día: en unos, bajo la cantonera; en otros, a media altura entre los nervios (así la repisa no
        // hace una fila de placas). Su alto, el justo para el día a 16 sp y el mes.
        val alto = 29f * u
        val baja = azar(s, 12) > 0.62f
        val py = if (baja) h * (0.34f + 0.06f * azar(s, 6)) else capA + h * 0.06f + h * 0.025f * azar(s, 6)
        val mx = w * 0.08f
        val pl = Rect(mx, py, w - mx, py + alto)
        // Los nervios en relieve: con la placa arriba, uno sobre ella y dos o tres debajo; con la placa en medio, dos arriba y
        // uno debajo.
        val zonaA = if (baja) capA + h * 0.08f else pl.bottom + 6f * u
        val zonaB = if (baja) py - 7f * u else h * 0.66f
        val nN = if (baja) 2 else 2 + (azar(s, 7) * 1.99f).toInt()
        val nerv = mutableListOf(if (baja) minOf(pl.bottom + 7f * u, h * 0.7f) else py - 3.4f * u)
        for (q in 0 until nN) nerv.add(zonaA + (zonaB - zonaA) * q / maxOf(1, nN - 1))
        nerv.forEach { y ->
            drawRect(a.sombra(0.45f), Offset(0f, y + 2.6f * u), Size(w, 1.4f * u))
            drawRect(t.hondo, Offset(0f, y + 1.4f * u), Size(w, 1.4f * u))
            drawRect(t.claro, Offset(0f, y - 0.6f * u), Size(w, 2f * u))
            drawRect(a.brillo(0.4f, k), Offset(w * (if (hacia2 < 0f) 0.1f else 0.6f), y - 0.6f * u), Size(w * 0.3f, 0.9f * u))
        }
        // Los hierros en seco: un filete doble arriba y abajo.
        listOf(capA + h * 0.05f, h - capB - h * 0.06f).forEach { y ->
            drawLine(t.hondo.copy(alpha = 0.7f), Offset(w * 0.1f, y), Offset(w * 0.9f, y), 0.6f * u)
            drawLine(t.claro.copy(alpha = 0.5f), Offset(w * 0.1f, y + 0.8f * u), Offset(w * 0.9f, y + 0.8f * u), 0.5f * u)
        }
        placa(v, medidor, pl, l)
        cantonera(Rect(0f, h - capB, w, h), h - capB - h * 0.04f, false, l, hacia2)
        // En los Sueños, la media luna de plata, bajo la placa.
        if (v.noche) lunaPlataOrden(Offset(w * 0.5f, if (baja) (capA + h * 0.045f + zonaA) / 2 + 2f * u else (pl.bottom + zonaA) / 2 + 1.5f * u), minOf(w * 0.17f, 4.6f * u), a)
        // La argolla de la cadena, con su grapa, y la cadena que baja a la barra de la repisa (un poco más abajo del lienzo).
        val gy = h * (0.74f + 0.05f * azar(s, 8))
        val gx = w * (0.38f + 0.24f * azar(s, 9))
        drawRect(a.sombra(0.4f), Offset(gx - 2.4f * u + 0.8f * u, gy - 1.6f * u + 1f * u), Size(4.8f * u, 3.2f * u))
        drawRect(l.c(P.ACERO_HONDO), Offset(gx - 2.4f * u, gy - 1.6f * u), Size(4.8f * u, 3.2f * u))
        drawCircle(a.sombra(0.4f), 2.8f * u, Offset(gx + 0.7f * u, gy + 2.6f * u), style = Stroke(1.5f * u))
        drawCircle(l.c(P.ACERO_CLARO, -0.1f), 2.8f * u, Offset(gx, gy + 2f * u), style = Stroke(1.5f * u))
        drawArc(a.destello(0.7f, k, kf), 180f, 90f, false, Offset(gx - 2.8f * u, gy - 0.8f * u), Size(5.6f * u, 5.6f * u), style = Stroke(0.7f * u))
        val barra = h + MuebleEstanteOrden.TABLA.value * ObraEstanteOrden.BARRA * u
        val lado = (if (azar(s, 10) > 0.5f) 1f else -1f) * w * (0.12f + 0.14f * azar(s, 11))
        cadena(Offset(gx, gy + 4.6f * u), Offset(gx + lado * 0.4f, barra), lado, l)
        filos(cuerpo, a, k, kf, hacia)
    }

    // ---------------------------------------------------------------- el carcaj de planos

    /**
     * El carcaj de planos: el cilindro de cuero del color del ánimo, su pie y su aro de latón, la costura a pespunte, la correa con
     * su hebilla, la banda de latón con el día y, por la boca, los rollos de plano (azules, alguno de papel crudo) abiertos en
     * abanico, más cuanto más escribiste.
     */
    fun DrawScope.carcaj(v: Volumen, medidor: TextMeasurer, a: AireOrden, k: Float, kf: Float, hacia: Float, sol: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kf, sol)
        val hacia2 = if (a.oscuro && !a.dormida) hacia else -1f
        val boca = h * (0.27f + 0.05f * azar(s, 5))
        alrededor(a, hacia, boca)
        val t = tinte(material, P.CUERO_PARDO, l, (azar(s, 4) - 0.5f) * 0.12f)
        val cuerpo = Rect(w * 0.05f, boca, w * 0.95f, h - 1f * u)
        // Los rollos de plano que asoman, abiertos en abanico: de atrás (los más altos) adelante.
        val n = 2 + (v.lleno * 3.6f).toInt().coerceAtMost(3)
        val d = cuerpo.width * (if (n <= 2) 0.46f else 0.4f)
        for (i in 0 until n) {
            val f = if (n == 1) 0.5f else i / (n - 1f)
            val cx = cuerpo.left + d * 0.5f + (cuerpo.width - d) * f
            val top = boca - h * (0.14f + 0.12f * azar(s, 70 + i))
            val giro = (f - 0.5f) * 14f + (azar(s, 75 + i) - 0.5f) * 4f
            rotate(giro, Offset(cx, boca + 4f * u)) {
                val r = Rect(cx - d / 2, top, cx + d / 2, boca + 4f * u)
                val crudo = azar(s, 80 + i) > 0.72f
                val papel = if (crudo) P.PAPEL else mezcla(P.AZUL_PLANO, P.TRAZO, 0.14f * azar(s, 90 + i))
                val c = l.c(papel)
                redondoOrden(r, c, mezcla(c, if (a.oscuro) P.LLAMA else P.LUZ_FRIA, if (a.oscuro) 0.25f * k else 0.35f), mezcla(c, P.NOCHE, 0.5f), hacia2, 0.85f)
                // La orilla de la última vuelta, en espiral, y la atadura.
                drawLine(a.sombra(0.4f), Offset(r.left, r.top + r.height * 0.55f), Offset(r.right, r.top + r.height * 0.42f), 0.9f * u)
                drawLine(a.brillo(0.45f, k), Offset(r.left, r.top + r.height * 0.55f - 0.9f * u), Offset(r.right, r.top + r.height * 0.42f - 0.9f * u), 0.6f * u)
                drawRect(l.c(if (crudo) P.GRANA else P.PAPEL), Offset(r.left, r.top + r.height * 0.24f), Size(r.width, 1.3f * u))
                val bo = Rect(r.left, top - d * 0.17f, r.right, top + d * 0.17f)
                drawOval(l.c(mezcla(papel, P.TRAZO, 0.5f), 0.05f), bo.topLeft, bo.size)
                drawOval(c, Offset(bo.center.x - bo.width * 0.34f, bo.center.y - bo.height * 0.34f), Size(bo.width * 0.68f, bo.height * 0.68f), style = Stroke(0.7f * u))
                drawOval(c, Offset(bo.center.x - bo.width * 0.16f, bo.center.y - bo.height * 0.16f), Size(bo.width * 0.32f, bo.height * 0.32f), style = Stroke(0.6f * u))
                drawOval(a.sombra(0.4f), bo.topLeft, bo.size, style = Stroke(0.6f * u))
            }
        }
        // El cilindro de cuero.
        redondoOrden(cuerpo, t.cara, t.claro, t.hondo, hacia2)
        // La costura a pespunte, del lado de la sombra.
        val xc = cuerpo.left + cuerpo.width * (if (hacia2 < 0f) 0.76f else 0.24f)
        drawLine(t.hondo, Offset(xc, cuerpo.top + 4f * u), Offset(xc, cuerpo.bottom - 4f * u), 1.4f * u)
        drawLine(t.claro.copy(alpha = 0.7f), Offset(xc + 1.2f * u, cuerpo.top + 4f * u), Offset(xc + 1.2f * u, cuerpo.bottom - 4f * u), 0.7f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u)))
        // El aro de latón de la boca, en escorzo, por delante de los rollos.
        val aro = Rect(cuerpo.left - 0.6f * u, boca - 3.4f * u, cuerpo.right + 0.6f * u, boca + 3.4f * u)
        redondoOrden(aro, l.c(P.LATON), l.c(P.LATON_BRILLO), l.c(P.LATON_HONDO), hacia2, 0.9f)
        drawRect(a.sombra(0.45f), Offset(cuerpo.left, aro.bottom), Size(cuerpo.width, 1.4f * u))
        drawRect(a.destello(0.55f, k, kf), Offset(aro.left, aro.top), Size(aro.width, 0.8f * u))
        // El pie de latón.
        val pie = Rect(cuerpo.left - 0.6f * u, h - h * 0.06f, cuerpo.right + 0.6f * u, h)
        redondoOrden(pie, l.c(P.LATON), l.c(P.LATON_BRILLO), l.c(P.LATON_HONDO), hacia2, 0.9f)
        drawRect(a.sombra(0.4f), Offset(cuerpo.left, pie.top - 1f * u), Size(cuerpo.width, 1f * u))
        // La banda de latón con el día, a media altura (otro sitio que en el manual).
        val alto = 29f * u
        val py = boca + 7f * u + (h - boca - alto - 30f * u) * (0.15f + 0.35f * azar(s, 6))
        val mx = w * 0.08f
        val pl = Rect(mx, py, w - mx, py + alto)
        placa(v, medidor, pl, l)
        // La correa del lado de la luz, con su hebilla bajo la placa.
        val xa = if (hacia2 < 0f) cuerpo.left + cuerpo.width * 0.16f else cuerpo.right - cuerpo.width * 0.16f - 4.4f * u
        val correa = l.c(mezcla(P.CUERO, P.BETUN, 0.2f))
        listOf(Rect(xa, aro.bottom, xa + 4.4f * u, pl.top - 0.5f * u), Rect(xa, pl.bottom + 0.5f * u, xa + 4.4f * u, pie.top)).forEach { cr ->
            if (cr.height <= 0f) return@forEach
            drawRect(a.sombra(0.35f), Offset(cr.right, cr.top), Size(1.2f * u, cr.height))
            drawRect(correa, cr.topLeft, cr.size)
            drawRect(a.brillo(0.2f, k), cr.topLeft, Size(1f * u, cr.height))
        }
        val hb = Rect(xa - 1.2f * u, pl.bottom + 5f * u, xa + 5.6f * u, pl.bottom + 11f * u)
        drawRect(l.c(P.LATON), hb.topLeft, hb.size, style = Stroke(1.3f * u))
        drawLine(l.c(P.LATON_HONDO), Offset(hb.center.x, hb.top), Offset(hb.center.x, hb.bottom), 0.9f * u)
        if (v.noche) lunaPlataOrden(Offset(w * (if (hacia2 < 0f) 0.64f else 0.36f), (pl.bottom + pie.top) / 2 + 3f * u), minOf(w * 0.17f, 4.6f * u), a)
        filos(cuerpo, a, k, kf, hacia)
    }

    // ---------------------------------------------------------------- la carpeta de campaña

    /**
     * La carpeta de campaña: blanda y panzona, de lona encerada del color del ánimo, con el doblez del lomo a la izquierda, la
     * trama de la lona y sus arrugas, los pliegos de plano y de papel que asoman arriba (más cuanto más escribiste), las dos cintas
     * atadas, el portaetiquetas de latón con el día y las dos puntas de latón de abajo.
     */
    fun DrawScope.carpeta(v: Volumen, medidor: TextMeasurer, a: AireOrden, k: Float, kf: Float, hacia: Float, sol: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kf, sol)
        val hacia2 = if (a.oscuro && !a.dormida) hacia else -1f
        val arriba = h * (0.2f + 0.05f * azar(s, 5))
        alrededor(a, hacia, arriba)
        val t = tinte(material, P.LONA, l, (azar(s, 4) - 0.5f) * 0.12f)
        val panza = w * 0.07f
        // Los pliegos que asoman por arriba, en abanico, de atrás adelante: planos azules con su trazo y papel crudo.
        val n = 3 + (v.lleno * 4f).toInt().coerceAtMost(4)
        for (i in 0 until n) {
            val x0 = w * (0.08f + 0.08f * azar(s, 60 + i)); val x1 = w * (0.92f - 0.1f * azar(s, 61 + i))
            val top = arriba - h * (0.04f + 0.13f * azar(s, 62 + i))
            val gir = (azar(s, 63 + i) - 0.5f) * 8f
            rotate(gir, Offset(w / 2, arriba + 4f * u)) {
                val pr = Rect(x0, top, x1, arriba + 6f * u)
                val azul = (i + s) % 3 != 0
                val papel = if (azul) P.AZUL_PLANO else P.PAPEL
                drawRect(a.sombra(0.4f), pr.topLeft + Offset(1f * u, 1f * u), pr.size)
                drawRect(l.c(papel), pr.topLeft, pr.size)
                if (azul) {
                    val tr = a.luz(P.TRAZO, k, kf).copy(alpha = 0.55f)
                    drawLine(tr, Offset(pr.left + pr.width * 0.15f, pr.top + 3f * u), Offset(pr.right - pr.width * 0.2f, pr.top + 3f * u), 0.6f * u)
                    drawRect(tr, Offset(pr.left + pr.width * 0.25f, pr.top + 5f * u), Size(pr.width * 0.3f, 3f * u), style = Stroke(0.5f * u))
                } else drawLine(a.sombra(0.25f), Offset(pr.left + 2f * u, pr.top + 3.5f * u), Offset(pr.right - 3f * u, pr.top + 3.5f * u), 0.5f * u)
                drawRect(a.brillo(0.45f, k), pr.topLeft, Size(pr.width, 0.8f * u))
            }
        }
        // La lona, panzona: el borde de arriba hundido y los costados abombados.
        val lona = Path().apply {
            moveTo(0.8f * u, arriba)
            quadraticTo(w * 0.5f, arriba + h * 0.04f, w - 0.8f * u, arriba)
            quadraticTo(w + panza * 0.35f, h * 0.55f, w - 1.4f * u, h - 3f * u)
            quadraticTo(w - 1.4f * u, h, w - 5f * u, h)
            lineTo(5f * u, h)
            quadraticTo(1.4f * u, h, 1.4f * u, h - 3f * u)
            quadraticTo(-panza * 0.35f, h * 0.55f, 0.8f * u, arriba)
            close()
        }
        drawPath(lona, t.cara)
        clipPath(lona) {
            // Su volumen: la luz del lado de la luminaria (o de la vela), la sombra del otro y el hondo abajo.
            val claroX = if (hacia2 < 0f) 0f else w * 0.6f
            drawRect(t.claro.copy(alpha = 0.75f), Offset(claroX, 0f), Size(w * 0.4f, h))
            drawRect(t.hondo.copy(alpha = 0.8f), Offset(if (hacia2 < 0f) w * 0.74f else 0f, 0f), Size(w * 0.26f, h))
            drawRect(t.hondo.copy(alpha = 0.45f), Offset(0f, h * 0.84f), Size(w, h * 0.16f))
            drawRect(t.hondo.copy(alpha = 0.5f), Offset(0f, arriba), Size(w, 3f * u))
            // La trama de la lona, en un solo camino.
            val trama = Path()
            var y = arriba + 2f * u
            var q = 0
            while (y < h && q < 200) { trama.moveTo(0f, y); trama.lineTo(w, y); y += 2.2f * u; q++ }
            drawPath(trama, t.hondo.copy(alpha = 0.2f), style = Stroke(0.5f * u))
            // El doblez del lomo, a la izquierda, y las arrugas de la panza, que bajan en diagonal.
            drawRect(t.hondo.copy(alpha = 0.65f), Offset(w * 0.1f, 0f), Size(1.6f * u, h))
            drawRect(t.claro.copy(alpha = 0.7f), Offset(w * 0.1f + 1.6f * u, 0f), Size(0.9f * u, h))
            listOf(0.4f, 0.66f).forEachIndexed { i, f ->
                val x = w * (f + 0.06f * azar(s, 70 + i))
                drawLine(t.hondo.copy(alpha = 0.5f), Offset(x, arriba + h * 0.03f), Offset(x + w * 0.08f, h * 0.42f), 1.4f * u, StrokeCap.Round)
                drawLine(t.claro.copy(alpha = 0.5f), Offset(x - 1.2f * u, arriba + h * 0.03f), Offset(x + w * 0.08f - 1.2f * u, h * 0.42f), 0.8f * u, StrokeCap.Round)
            }
        }
        drawPath(lona, a.sombra(0.45f), style = Stroke(0.8f * u))
        // Las dos cintas de cierre, atadas en el canto de arriba, con sus cabos que cuelgan.
        listOf(w * 0.3f, w * 0.7f).forEachIndexed { i, x ->
            val c0 = Offset(x, arriba + h * 0.02f)
            val cinta = l.c(mezcla(P.GRANA_HONDA, P.CUERO, 0.4f))
            val c1 = Offset(x + (if (i == 0) -1f else 1f) * w * 0.06f, c0.y + h * 0.09f)
            drawLine(a.sombra(0.35f), c0 + Offset(0.8f * u, 1f * u), c1 + Offset(0.8f * u, 1f * u), 1.4f * u, StrokeCap.Round)
            drawLine(cinta, c0, c1, 1.4f * u, StrokeCap.Round)
            drawCircle(cinta, 1.5f * u, c0)
        }
        // El portaetiquetas de latón con el día, a media altura.
        val alto = 29f * u
        val py = arriba + h * 0.13f + h * 0.08f * azar(s, 6)
        val mx = w * 0.09f
        val pl = Rect(mx, py, w - mx, py + alto)
        placa(v, medidor, pl, l)
        // Las dos puntas de latón de abajo.
        listOf(true, false).forEach { izq ->
            val x = if (izq) 1.4f * u else w - 1.4f * u
            val sg = if (izq) 1f else -1f
            val punta = poligonoOrden(x, h - 9f * u, x, h - 0.5f * u, x + sg * 9f * u, h - 0.5f * u)
            drawPath(punta, l.c(P.LATON))
            drawLine(a.destello(0.5f, k, kf), Offset(x, h - 9f * u), Offset(x + sg * 9f * u, h - 0.5f * u), 0.7f * u)
        }
        if (v.noche) lunaPlataOrden(Offset(w * 0.5f, minOf(h - 12f * u, pl.bottom + 9f * u)), minOf(w * 0.17f, 4.6f * u), a)
        filos(Rect(0f, arriba, w, h), a, k, kf, hacia)
    }
}
