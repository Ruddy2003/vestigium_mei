package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las entradas del estante de Orden ([MuebleEstanteOrden]), tres cosas del archivo de la orden según la semilla, cada una con su
 * silueta: el manual encadenado (alto y recto, de cuero del color del ánimo, con sus nervios, las cantoneras de acero, la placa
 * de latón con el día y la cadena que baja de su grapa a la barra de la repisa), el carcaj de planos (un cilindro de cuero del
 * color del ánimo, con la boca en elipse, el pie en curva y los rollos de plano saliendo en abanico, más cuanto más escribiste) y
 * el relicario técnico (la urna de vidrio de remate apuntado sobre su zócalo esmaltado del color del ánimo, con lo que guarda
 * sobre un cojín). Sin ánimo: cuero pardo y acero pintado. En los Sueños, una media luna de plata.
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
        diaYMes(v, medidor, Rect(r.left + 1.2f * u, r.top + 0.6f * u, r.right - 1.2f * u, r.bottom - 0.5f * u), a.tintaSobre(color))
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
        val alto = 27.5f * u
        val baja = azar(s, 12) > 0.62f
        val py = if (baja) h * (0.34f + 0.06f * azar(s, 6)) else capA + h * 0.06f + h * 0.025f * azar(s, 6)
        val mx = w * 0.12f
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
     * El carcaj de planos, un cilindro: la boca en elipse con su aro de latón y su hondo, los rollos de plano que salen en abanico
     * bien por encima (azules, alguno de papel crudo; más cuanto más escribiste), el cuerpo de cuero del color del ánimo con su
     * labio y su fondo en curva, el pie de latón, la costura a pespunte, la correa con su hebilla y la chapa de latón con el día.
     */
    fun DrawScope.carcaj(v: Volumen, medidor: TextMeasurer, a: AireOrden, k: Float, kf: Float, hacia: Float, sol: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kf, sol)
        val hacia2 = if (a.oscuro && !a.dormida) hacia else -1f
        val boca = h * (0.38f + 0.05f * azar(s, 5))
        alrededor(a, hacia, boca)
        val t = tinte(material, P.CUERO_PARDO, l, (azar(s, 4) - 0.5f) * 0.12f)
        val izq = w * 0.04f; val der = w * 0.96f
        val eh = (der - izq) * 0.34f
        // La boca por dentro: el aro de atrás y el hondo.
        val bo = Rect(izq, boca - eh / 2, der, boca + eh / 2)
        drawOval(l.c(P.LATON_HONDO), bo.topLeft, bo.size)
        drawOval(l.c(P.BETUN), Offset(bo.left + 1.6f * u, bo.top + 1.2f * u), Size(bo.width - 3.2f * u, bo.height - 2.4f * u))
        // Los rollos, en abanico desde el fondo de la boca: de atrás (los más altos, al medio) adelante.
        val n = 3 + (v.lleno * 3.4f).toInt().coerceAtMost(3)
        val d = (der - izq) * (if (n <= 3) 0.36f else 0.3f)
        val pivote = Offset(w / 2, boca + eh * 0.9f)
        val orden = (0 until n).sortedBy { kotlin.math.abs(it - (n - 1) / 2f) }.reversed()
        for (i in orden) {
            val f = if (n == 1) 0f else i / (n - 1f) - 0.5f
            val giro = f * 30f + (azar(s, 75 + i) - 0.5f) * 5f
            val largo = boca + eh * 0.9f - h * (0.02f + 0.13f * azar(s, 70 + i) + 0.06f * kotlin.math.abs(f))
            withTransform({ rotate(giro, pivote) }) {
                val r = Rect(pivote.x - d / 2, pivote.y - largo, pivote.x + d / 2, pivote.y)
                val crudo = azar(s, 80 + i) > 0.74f
                val papel = if (crudo) P.PAPEL else mezcla(P.AZUL_PLANO, P.TRAZO, 0.16f * azar(s, 90 + i))
                val c = l.c(papel)
                redondoOrden(r, c, mezcla(c, if (a.oscuro) P.LLAMA else P.LUZ_FRIA, if (a.oscuro) 0.25f * k else 0.38f), mezcla(c, P.NOCHE, 0.5f), hacia2, 0.85f)
                // La orilla de la última vuelta, en espiral, el trazo del plano y la atadura.
                drawLine(a.sombra(0.4f), Offset(r.left, r.top + r.height * 0.3f), Offset(r.right, r.top + r.height * 0.22f), 0.9f * u)
                drawLine(a.brillo(0.45f, k), Offset(r.left, r.top + r.height * 0.3f - 0.9f * u), Offset(r.right, r.top + r.height * 0.22f - 0.9f * u), 0.6f * u)
                if (!crudo) drawLine(a.luz(P.TRAZO, k, kf).copy(alpha = 0.45f), Offset(r.center.x, r.top + d * 0.4f), Offset(r.center.x, r.top + r.height * 0.5f), 0.6f * u)
                drawRect(l.c(if (crudo) P.GRANA else P.PAPEL), Offset(r.left, r.top + r.height * 0.14f), Size(r.width, 1.3f * u))
                // La boca del rollo, con su espiral.
                val rb = Rect(r.left, r.top - d * 0.17f, r.right, r.top + d * 0.17f)
                drawOval(l.c(mezcla(papel, P.TRAZO, 0.5f), 0.05f), rb.topLeft, rb.size)
                drawOval(c, Offset(rb.center.x - rb.width * 0.34f, rb.center.y - rb.height * 0.34f), Size(rb.width * 0.68f, rb.height * 0.68f), style = Stroke(0.7f * u))
                drawOval(c, Offset(rb.center.x - rb.width * 0.15f, rb.center.y - rb.height * 0.15f), Size(rb.width * 0.3f, rb.height * 0.3f), style = Stroke(0.6f * u))
                drawOval(a.sombra(0.4f), rb.topLeft, rb.size, style = Stroke(0.6f * u))
            }
        }
        // El cuerpo: un cilindro con el labio de delante y el fondo en curva.
        val fondo = h - eh * 0.5f - 1f * u
        val cuerpo = Path().apply {
            moveTo(izq, boca)
            arcTo(bo, 180f, -180f, false)
            lineTo(der, fondo)
            arcTo(Rect(izq, fondo - eh / 2, der, fondo + eh / 2), 0f, 180f, false)
            close()
        }
        val caja = Rect(izq, boca, der, fondo + eh / 2)
        clipPath(cuerpo) { redondoOrden(caja, t.cara, t.claro, t.hondo, hacia2) }
        // El roce del cuero y la costura a pespunte, del lado de la sombra.
        val xc = izq + (der - izq) * (if (hacia2 < 0f) 0.76f else 0.24f)
        drawLine(t.hondo, Offset(xc, boca + eh * 0.6f), Offset(xc, fondo + eh * 0.4f), 1.4f * u)
        drawLine(t.claro.copy(alpha = 0.7f), Offset(xc + 1.2f * u, boca + eh * 0.6f), Offset(xc + 1.2f * u, fondo + eh * 0.4f), 0.7f * u,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u)))
        // El labio de latón de la boca, por delante de los rollos.
        drawArc(a.sombra(0.4f), 0f, 180f, false, Offset(bo.left, bo.top + 1.6f * u), bo.size, style = Stroke(2.4f * u))
        drawArc(l.c(P.LATON), 0f, 180f, false, bo.topLeft, bo.size, style = Stroke(2.6f * u))
        drawArc(a.brillo(0.6f, maxOf(k, 0.25f)), 110f, 60f, false, bo.topLeft, bo.size, style = Stroke(1f * u))
        // El pie de latón, en curva.
        val pie = Rect(izq - 0.6f * u, fondo - eh / 2, der + 0.6f * u, fondo + eh / 2)
        drawArc(l.c(P.LATON_HONDO), 0f, 180f, false, Offset(pie.left, pie.top - 2.2f * u), pie.size, style = Stroke(4.4f * u))
        drawArc(l.c(P.LATON), 20f, 140f, false, Offset(pie.left, pie.top - 2.8f * u), pie.size, style = Stroke(1.6f * u))
        // La chapa de latón con el día, a media altura del cuerpo.
        val alto = 27.5f * u
        val py = boca + eh * 0.55f + 2f * u + maxOf(0f, (fondo - boca - eh * 0.55f - alto - 8f * u)) * (0.2f + 0.4f * azar(s, 6))
        val mx = w * 0.12f
        val pl = Rect(mx, py, w - mx, py + alto)
        placa(v, medidor, pl, l)
        // La correa del lado de la luz, con su hebilla bajo la chapa.
        val xa = if (hacia2 < 0f) izq + (der - izq) * 0.1f else der - (der - izq) * 0.1f - 4.4f * u
        val correa = l.c(mezcla(P.CUERO, P.BETUN, 0.2f))
        listOf(Rect(xa, boca + eh * 0.5f + 1f * u, xa + 4.4f * u, pl.top - 0.5f * u), Rect(xa, pl.bottom + 0.5f * u, xa + 4.4f * u, fondo + eh * 0.42f)).forEach { cr ->
            if (cr.height <= 0f) return@forEach
            drawRect(a.sombra(0.35f), Offset(cr.right, cr.top), Size(1.2f * u, cr.height))
            drawRect(correa, cr.topLeft, cr.size)
            drawRect(a.brillo(0.2f, k), cr.topLeft, Size(1f * u, cr.height))
        }
        val hb = Rect(xa - 1.2f * u, pl.bottom + 4f * u, xa + 5.6f * u, pl.bottom + 10f * u)
        if (hb.bottom < fondo) {
            drawRect(l.c(P.LATON), hb.topLeft, hb.size, style = Stroke(1.3f * u))
            drawLine(l.c(P.LATON_HONDO), Offset(hb.center.x, hb.top), Offset(hb.center.x, hb.bottom), 0.9f * u)
        }
        if (v.noche) lunaPlataOrden(Offset(w * (if (hacia2 < 0f) 0.64f else 0.36f), minOf(fondo - 2f * u, pl.bottom + 9f * u)), minOf(w * 0.16f, 4.4f * u), a)
        filos(Rect(izq, boca, der, fondo), a, k, kf, hacia)
    }

    // ---------------------------------------------------------------- el relicario

    /** Una bobina de hilo de cobre de pie, vista de lado, en [r]: las dos alas de acero y el hilo con sus vueltas. */
    private fun DrawScope.bobinaChica(r: Rect, l: Luz) {
        val u = density
        val a = l.a
        val hilo = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.14f, r.right - r.width * 0.12f, r.bottom - r.height * 0.14f)
        val cobre = l.c(P.OXIDO, 0.15f)
        redondoOrden(hilo, cobre, mezcla(cobre, P.LATON_BRILLO, if (a.oscuro) 0.2f * l.k else 0.45f), mezcla(cobre, P.NOCHE, 0.5f), -1f)
        var y = hilo.top + 1.2f * u
        var q = 0
        while (y < hilo.bottom && q < 20) { drawLine(mezcla(cobre, P.NOCHE, 0.35f).copy(alpha = 0.5f), Offset(hilo.left, y), Offset(hilo.right, y - 0.5f * u), 0.5f * u); y += 1.4f * u; q++ }
        listOf(r.top, r.bottom - r.height * 0.14f).forEach { yy ->
            drawRect(l.c(P.ACERO_MEDIO), Offset(r.left, yy), Size(r.width, r.height * 0.14f))
            drawRect(a.destello(0.45f, l.k, l.kf), Offset(r.left, yy), Size(r.width, 0.7f * u))
        }
    }

    /**
     * El relicario técnico: el zócalo esmaltado del color del ánimo, con su moldura de latón, su pie de acero y la chapa con el día;
     * encima, la urna de vidrio de remate apuntado en su marco de acero, con la anilla arriba; dentro, sobre un cojín del color del
     * ánimo, lo que guarda (una válvula, una bobina; más piezas cuanto más escribiste). En los Sueños, la media luna en el vidrio.
     */
    fun DrawScope.relicario(v: Volumen, medidor: TextMeasurer, a: AireOrden, k: Float, kf: Float, hacia: Float, sol: Float, material: Color?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, kf, sol)
        val hacia2 = if (a.oscuro && !a.dormida) hacia else -1f
        val alto = 27.5f * u
        val zArriba = maxOf(h * 0.42f, h - alto - 12f * u)
        alrededor(a, hacia, zArriba)
        val t = tinte(material, P.PINTURA, l, (azar(s, 4) - 0.5f) * 0.1f)
        // La urna: lados rectos y remate apuntado, un poco más angosta que el zócalo.
        val mu = w * (0.04f + 0.03f * azar(s, 5))
        val urna = Rect(mu, h * (0.05f + 0.05f * azar(s, 6)), w - mu, zArriba + 1f * u)
        val hombro = urna.top + urna.width * 0.62f
        val forma = Path().apply {
            moveTo(urna.left, urna.bottom); lineTo(urna.left, hombro)
            cubicTo(urna.left, hombro - urna.width * 0.35f, urna.center.x - urna.width * 0.18f, urna.top + urna.width * 0.08f, urna.center.x, urna.top)
            cubicTo(urna.center.x + urna.width * 0.18f, urna.top + urna.width * 0.08f, urna.right, hombro - urna.width * 0.35f, urna.right, hombro)
            lineTo(urna.right, urna.bottom); close()
        }
        // Por dentro: el muro de detrás un punto más frío, el cojín y lo que guarda.
        drawPath(forma, l.c(P.PAVON_HONDO, -0.1f))
        drawPath(forma, a.luz(P.VIDRIO, k * 0.5f, kf).copy(alpha = if (a.oscuro) 0.12f else 0.22f))
        clipPath(forma) {
            val cj = Rect(urna.left + 1.4f * u, urna.bottom - urna.height * 0.24f, urna.right - 1.4f * u, urna.bottom + 3f * u)
            val cojin = Path().apply { addRoundRect(RoundRect(cj, CornerRadius(cj.height * 0.5f))) }
            drawPath(cojin, t.cara)
            drawOval(t.claro.copy(alpha = 0.8f), Offset(cj.left + cj.width * 0.1f, cj.top + cj.height * 0.08f), Size(cj.width * 0.5f, cj.height * 0.35f))
            drawRect(t.hondo.copy(alpha = 0.6f), Offset(cj.left, cj.bottom - cj.height * 0.45f), Size(cj.width, cj.height * 0.45f))
            // Lo que guarda: la pieza principal (según la semilla) y, si escribiste más, otras más chicas.
            val piezas = 1 + (v.lleno * 2.2f).toInt().coerceAtMost(2)
            val suelo = cj.top + cj.height * 0.25f
            val hueco = urna.bottom - urna.height * 0.24f - (urna.top + urna.width * 0.3f)
            val valvulaPrimero = azar(s, 7) > 0.4f
            val ancho = urna.width
            fun valvula(cx: Float, alto: Float) {
                val vw = minOf(ancho * 0.4f, alto * 0.42f)
                valvulaOrden(Rect(cx - vw / 2, suelo - alto, cx + vw / 2, suelo), { col, f -> l.c(col, f) }, a, k)
            }
            fun bobina(cx: Float, alto: Float) {
                val bw = minOf(ancho * 0.48f, alto * 0.9f)
                drawOval(a.sombra(0.35f), Offset(cx - bw * 0.55f, suelo - 1.2f * u), Size(bw * 1.1f, 2.4f * u))
                bobinaChica(Rect(cx - bw / 2, suelo - alto, cx + bw / 2, suelo), l)
            }
            when (piezas) {
                1 -> if (valvulaPrimero) valvula(urna.center.x, hueco * 0.86f) else bobina(urna.center.x, hueco * 0.5f)
                2 -> {
                    if (valvulaPrimero) { bobina(urna.left + ancho * 0.72f, hueco * 0.36f); valvula(urna.left + ancho * 0.36f, hueco * 0.84f) }
                    else { valvula(urna.left + ancho * 0.7f, hueco * 0.62f); bobina(urna.left + ancho * 0.32f, hueco * 0.46f) }
                }
                else -> {
                    valvula(urna.left + ancho * 0.26f, hueco * 0.6f)
                    valvula(urna.left + ancho * 0.74f, hueco * 0.52f)
                    bobina(urna.center.x, hueco * 0.36f)
                }
            }
            // El vidrio: el reflejo ancho de la izquierda, el canto hondo de la derecha y un reflejo en diagonal.
            val luzV = when {
                a.dormida -> P.GUARDIA.copy(alpha = 0.3f)
                a.oscuro -> P.LLAMA.copy(alpha = 0.06f + 0.2f * k)
                a.noche -> P.VIGILIA.copy(alpha = 0.38f)
                else -> P.LUZ_FRIA.copy(alpha = 0.42f)
            }
            drawRect(luzV, Offset(urna.left + urna.width * 0.1f, urna.top), Size(urna.width * 0.12f, urna.height))
            drawPath(poligonoOrden(urna.left + urna.width * 0.35f, urna.top + urna.height * 0.25f, urna.left + urna.width * 0.48f, urna.top + urna.height * 0.25f,
                urna.right, urna.top + urna.height * 0.55f, urna.right, urna.top + urna.height * 0.66f), luzV.copy(alpha = luzV.alpha * 0.45f))
            drawRect(a.sombra(0.2f), Offset(urna.right - urna.width * 0.14f, urna.top), Size(urna.width * 0.14f, urna.height))
            // De noche, el vidrio guarda un filo frío aunque esté lejos de la vela (si no, la urna se lee como una losa).
            if (a.oscuro) drawRect(a.destello(0.55f, k, maxOf(kf, 0.5f)), Offset(urna.left + urna.width * 0.13f, urna.top + urna.width * 0.3f), Size(1.1f * u, urna.height * 0.55f))
        }
        // El marco de la urna, esmaltado del color del ánimo, con su filo de acero; el remate con su anilla.
        val marco = l.c(P.ACERO_MEDIO)
        drawPath(forma, a.sombra(0.45f), style = Stroke(3.6f * u))
        drawPath(forma, t.hondo, style = Stroke(3f * u))
        drawPath(forma, t.cara, style = Stroke(1.8f * u))
        drawLine(a.destello(0.55f, k, kf), Offset(urna.left + 0.4f * u, urna.bottom), Offset(urna.left + 0.4f * u, hombro), 0.8f * u)
        val ap = Offset(urna.center.x, urna.top)
        val ar = minOf(w * 0.12f, 3.4f * u)
        drawCircle(a.sombra(0.4f), ar, ap + Offset(0.7f * u, -ar * 0.9f + 0.9f * u), style = Stroke(1.4f * u))
        drawCircle(marco, ar, ap - Offset(0f, ar * 0.9f), style = Stroke(1.4f * u))
        drawCircle(l.c(P.LATON), 1.8f * u, ap)
        drawCircle(a.destello(0.7f, k, kf), 0.6f * u, ap - Offset(0.5f * u, 0.5f * u))
        if (v.noche) lunaPlataOrden(Offset(urna.center.x, urna.top + urna.height * 0.36f), minOf(w * 0.15f, 4.2f * u), a)
        // El zócalo esmaltado: la moldura de latón arriba, el esmalte del color del ánimo y el pie de acero.
        val z = Rect(0.5f * u, zArriba, w - 0.5f * u, h)
        redondoOrden(Rect(z.left + 1.2f * u, z.top + 3f * u, z.right - 1.2f * u, z.bottom - 5.6f * u), t.cara, t.claro, t.hondo, hacia2, 0.7f)
        val mol = Rect(z.left, z.top, z.right, z.top + 3f * u)
        redondoOrden(mol, l.c(P.LATON), l.c(P.LATON_BRILLO), l.c(P.LATON_HONDO), hacia2, 0.9f)
        drawRect(a.sombra(0.4f), Offset(mol.left + 1.2f * u, mol.bottom), Size(mol.width - 2.4f * u, 1f * u))
        val pie = Rect(z.left - 0.4f * u, z.bottom - 5.6f * u, z.right + 0.4f * u, z.bottom - 2.6f * u)
        redondoOrden(pie, l.c(P.PAVON), l.c(P.PAVON_LUZ), l.c(P.PAVON_HONDO), hacia2, 0.8f)
        drawRect(a.destello(0.45f, k, kf), Offset(pie.left, pie.top), Size(pie.width, 0.7f * u))
        // Las dos patas de bola, de latón.
        listOf(z.left + 3f * u, z.right - 3f * u).forEach { x ->
            val pa = Rect(x - 2.6f * u, pie.bottom - 0.6f * u, x + 2.6f * u, h)
            drawOval(l.c(P.LATON_HONDO), pa.topLeft, pa.size)
            drawOval(l.c(P.LATON), Offset(pa.left + 0.4f * u, pa.top), Size(pa.width * 0.7f, pa.height * 0.75f))
            drawCircle(a.destello(0.6f, k, kf), 0.6f * u, Offset(pa.left + pa.width * 0.32f, pa.top + pa.height * 0.3f))
        }
        // El esmalte saltado en las aristas.
        for (q in 0 until 3) {
            val x = z.left + z.width * azar(s, 30 + q)
            drawOval(l.c(P.ACERO_MEDIO).copy(alpha = 0.7f), Offset(x, if (q % 2 == 0) z.top + 3.4f * u else pie.top - 2f * u), Size(w * 0.1f, 1.4f * u))
        }
        val mx = w * 0.16f
        val pl = Rect(mx, z.top + 4.2f * u, w - mx, minOf(z.top + 4.2f * u + alto, pie.top - 0.6f * u))
        placa(v, medidor, pl, l)
        filos(Rect(0f, urna.top, w, h), a, k, kf, hacia)
    }
}
