package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Las entradas del estante de Horror cósmico ([MuebleEstanteArcano]), tres objetos del archivo según la semilla: el libro de
 * registro (de pie, en tela entera del color del ánimo, con sus cofias de piel, sus nervios, la marca del agua que lo mojó y el
 * tejuelo de papel), la botella lacrada (el mensaje que el mar devolvió: vidrio del color del ánimo, el rollo de papel dentro, el
 * corcho con su lacre que chorrea y la etiqueta pegada) y el fardo envuelto en un trozo de vela (lona curtida del color del ánimo,
 * blanda, con su costura y su ollao, atada con piola alquitranada y la tarjeta metida debajo). En los Sueños, una media luna de
 * plata con su disco apagado.
 */
internal object VolumenEstanteArcano {
    private val P = PaletaArcano

    /** El tamaño del día, el mismo en las tres (unos 16 sp), y el del mes. */
    private const val DIA = 16f
    private const val MES = 8.5f

    /** Las telas del libro, de lo bajo a lo alto: sangre de buey, granate, el lino crudo, verde gris, verde de fondo de mar. */
    private val TELAS = listOf(0f to Color(0xFF5A1218), 0.28f to Color(0xFF902E28), 0.44f to Color(0xFFB98A6A), 0.5f to Color(0xFFD2C4A0),
        0.56f to Color(0xFFA2B190), 0.72f to Color(0xFF3C7860), 1f to Color(0xFF15463A))
    /** Los vidrios de la botella: rubí, ámbar rojizo, el vidrio claro, verde agua, verde botella. */
    private val VIDRIOS = listOf(0f to Color(0xFF5C0E1C), 0.28f to Color(0xFF9A2C2A), 0.44f to Color(0xFFBC8C68), 0.5f to Color(0xFFC6D2C2),
        0.56f to Color(0xFFA2C8AC), 0.72f to Color(0xFF368A5C), 1f to Color(0xFF125032))
    /** Las lonas del fardo: vela curtida roja, teja, la lona cruda, verde gris, verde de barca. */
    private val LONAS = listOf(0f to Color(0xFF6A2418), 0.28f to Color(0xFFA2482C), 0.44f to Color(0xFFC6986E), 0.5f to Color(0xFFD8CAA4),
        0.56f to Color(0xFFAEB490), 0.72f to Color(0xFF567C54), 1f to Color(0xFF2A4E3A))

    /** Sin ánimo: la tela negra de los registros, el vidrio negro de las botellas de vino, la lona alquitranada. */
    private val TELA_SIN = Color(0xFF2E3230)
    private val VIDRIO_SIN = Color(0xFF2A2C22)
    private val LONA_SIN = Color(0xFF423C30)

    private fun material(x: Float?, t: Color?, rampa: List<Pair<Float, Color>>): Color? =
        if (x != null) rampaArcano(x, rampa) else t

    /** La luz que le toca a una entrada: la del momento ([k]), de qué lado ([hacia]) y el frío de la ventana ([frio]). */
    private class Luz(val a: AireArcano, val k: Float, val hacia: Float, val frio: Float) {
        fun c(col: Color, f: Float = 0f): Color = a.dia(col, f, k)
        fun animo(t: Color, f: Float = 0f): Color = a.animo(if (f > 0f) mezcla(t, luzMueble(1f), f) else if (f < 0f) mezcla(t, sombraMueble(1f), -f) else t, k)
    }

    /** Los tonos de un cuerpo con la luz: su cara, su claro y su hondo (si [animo], con el color del ánimo). */
    private class Tinte(val cara: Color, val claro: Color, val hondo: Color)

    private fun tinte(m: Color, animo: Boolean, l: Luz): Tinte =
        if (animo) Tinte(l.animo(m), l.animo(m, 0.3f), l.animo(m, -0.5f)) else Tinte(l.c(m), l.c(m, 0.22f), l.c(m, -0.45f))

    /** Cómo se reparte la luz en un cuerpo redondo, de la luz a la sombra, por franjas opacas (sin degradados). */
    private val FRANJAS = floatArrayOf(0f, 0.08f, 0.2f, 0.38f, 0.58f, 0.76f, 0.9f, 1f)
    private val TONOS = floatArrayOf(0.4f, 0.8f, 0.45f, 0.1f, -0.22f, -0.52f, -0.8f)

    private fun DrawScope.redondo(r: Rect, t: Tinte, hacia: Float) {
        for (i in 0 until 7) {
            val f0 = FRANJAS[i]; val f1 = FRANJAS[i + 1]
            val m = TONOS[i]
            val c = if (m >= 0f) mezcla(t.cara, t.claro, m) else mezcla(t.cara, t.hondo, -m)
            val mas = if (i < 6) 1.2f else 0f
            val x0 = if (hacia < 0f) r.left + r.width * f0 else r.right - r.width * f1 - mas
            drawRect(c, Offset(x0, r.top), Size(r.width * (f1 - f0) + mas, r.height))
        }
    }

    /** El mes corto en español, como «nov.». */
    private fun mesCorto(mes: String): String {
        val m = mes.substringAfterLast(' ').trim()
        return if (m.length > 4) m.take(3).lowercase() + "." else m.lowercase()
    }

    /** El día (en una franja donde cabe a 16 sp sin achicarse) y el mes debajo, centrados en [r]. */
    private fun DrawScope.diaYMes(v: Volumen, medidor: TextMeasurer, r: Rect, tinta: Color) {
        val u = density
        val k = minOf(1f, r.height / (27f * u))
        val hd = 17.5f * u * k; val hm = 9.5f * u * k
        val y0 = r.center.y - (hd + hm) / 2
        textoEn(medidor, v.dia, Rect(r.left - 1f * u, y0, r.right + 1f * u, y0 + hd), P.LETRA, tinta, DIA, peso = FontWeight.SemiBold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left - 1f * u, y0 + hd - 1.2f * u, r.right + 1f * u, y0 + hd + hm), P.LETRA, tinta.copy(alpha = 0.85f), MES, minimo = 5f)
    }

    /** Una etiqueta de papel manila en [et] con el día y el mes, su sombra, su mancha y su borde gastado. */
    private fun DrawScope.etiqueta(v: Volumen, medidor: TextMeasurer, et: Rect, l: Luz, giro: Float = 0f) {
        val u = density
        val a = l.a
        rotate(giro, et.center) {
            drawRect(a.sombra(0.4f), Offset(et.left + 0.9f * u, et.top + 1.1f * u), et.size)
            drawRect(a.etiqueta(P.MANILA, l.k), et.topLeft, et.size)
            drawRect(a.etiqueta(P.PAPEL_OSC, l.k).copy(alpha = 0.45f), Offset(et.left, et.bottom - et.height * 0.14f), Size(et.width, et.height * 0.14f))
            drawPath(irregularArcano(Offset(et.right - et.width * 0.2f, et.top + et.height * 0.3f), et.width * 0.22f, et.height * 0.16f, v.semilla, 8, 0.35f), a.etiqueta(P.PAPEL_OSC, l.k).copy(alpha = 0.3f))
            drawRect(a.luz(P.TINTA, l.k).copy(alpha = 0.4f), et.deflate(1.3f * u).topLeft, et.deflate(1.3f * u).size, style = Stroke(0.5f * u))
            diaYMes(v, medidor, et.deflate(1.5f * u), if (a.oscuro) Color(0xFF120E08) else P.TINTA)
        }
    }

    /** La media luna de plata, de radio [r], armada una vez por tamaño. */
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

    /** La luna de los Sueños: el disco apagado (la luz cenicienta) y la media luna de plata encima, con su canto y su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, a: AireArcano) {
        val u = density
        val m = creciente(r)
        drawCircle(Color(0xFF3A4458).copy(alpha = 0.55f), r, c)
        translate(c.x + 0.8f * u, c.y + 1.1f * u) { drawPath(m, a.sombra(0.5f)) }
        translate(c.x, c.y) {
            drawPath(m, Color(0xFFDDE6EC))
            drawPath(m, Color(0xFF2E3648), style = Stroke(0.8f * u))
        }
        drawCircle(Color.White.copy(alpha = 0.85f), r * 0.14f, Offset(c.x - r * 0.6f, c.y - r * 0.16f))
    }

    /** Lo de todas: la sombra en el paño por la rendija con la vecina y la de contacto en la tabla. */
    private fun DrawScope.alrededor(a: AireArcano, hacia: Float, desde: Float) {
        val u = density
        val w = size.width; val h = size.height
        val sep = 3f * u
        if (hacia < 0f) drawRect(a.sombra(0.45f), Offset(w, desde), Size(sep, h - desde))
        else drawRect(a.sombra(0.45f), Offset(-sep, desde), Size(sep, h - desde))
        drawRect(a.sombra(0.55f), Offset(-1f * u, h - 0.4f * u), Size(w + 2f * u, 2.4f * u))
    }

    /** El filo de la llama en el canto que la mira (de noche) y el roce frío de la ventana en el de la izquierda. */
    private fun DrawScope.filos(cuerpo: Rect, l: Luz) {
        val u = density
        val a = l.a
        if (a.lumbre) {
            val x = if (l.hacia > 0f) cuerpo.right - 1.4f * u else cuerpo.left
            drawRect(a.filo(0.75f * l.k), Offset(x, cuerpo.top), Size(1.4f * u, cuerpo.height))
        }
        if (a.oscuro && l.frio > 0.08f) drawRect(a.frio(0.55f * l.frio), Offset(cuerpo.left, cuerpo.top), Size(1.2f * u, cuerpo.height))
    }

    // ---------------------------------------------------------------- el libro de registro

    fun DrawScope.libro(v: Volumen, medidor: TextMeasurer, a: AireArcano, k: Float, hacia: Float, frio: Float, x: Float?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, hacia, frio)
        val m = material(x, v.tono, TELAS)
        val t = tinte(m ?: TELA_SIN, m != null, l)
        // Las papeletas que asoman por la cabeza: más cuanto más escribiste.
        val papeletas = (v.lleno * 3.4f).toInt() + (if (azar(s, 3) > 0.6f) 1 else 0)
        val cab = if (papeletas > 0) 5f * u else 1.5f * u
        alrededor(a, hacia, cab)
        for (j in 0 until papeletas) {
            val px = w * (0.16f + 0.55f * azar(s, 10 + j)); val pw = w * (0.16f + 0.12f * azar(s, 20 + j))
            rotate((azar(s, 30 + j) - 0.5f) * 12f, Offset(px + pw / 2, cab + 3f * u)) {
                drawRect(l.c(if (j % 2 == 0) P.PAPEL else P.MANILA), Offset(px, 0f), Size(pw, cab + 6f * u))
                drawRect(a.sombra(0.25f), Offset(px + pw * 0.7f, 0f), Size(pw * 0.3f, cab + 6f * u))
            }
        }
        // Las tapas que asoman a los lados del lomo (abiertas un poco arriba por la humedad) y el lomo redondo de tela.
        val tapa = w * 0.08f
        drawRoundRect(t.hondo, Offset(0f, cab), Size(w, h - cab), CornerRadius(1.4f * u))
        val lomo = Rect(tapa, cab + 0.6f * u, w - tapa, h)
        redondo(lomo, t, hacia)
        // El roce de la tela, su trama y el lamparón del agua: una franja más clara (lavada por la sal) bajo una raya ondulada oscura.
        val marea = h * (0.66f + 0.14f * azar(s, 5))
        val ola = Path().apply {
            moveTo(lomo.left, marea)
            var xx = lomo.left; var j = 0
            while (xx < lomo.right) { xx += lomo.width / 5f; lineTo(minOf(xx, lomo.right), marea + (if (j % 2 == 0) 1.4f else -1.2f) * u); j++ }
        }
        val mojado = azar(s, 19) < 0.65f
        clipRect(lomo.left, lomo.top, lomo.right, lomo.bottom) {
            if (mojado) {
                drawRect(l.c(P.SALOBRE).copy(alpha = 0.26f), Offset(lomo.left, marea), Size(lomo.width, h - marea))
                drawRect(luzMueble(if (a.oscuro) 0.04f else 0.16f), Offset(lomo.left, marea), Size(lomo.width, h - marea))
                drawPath(ola, l.c(mezcla(P.SALOBRE, P.TECA_HONDA, 0.4f)).copy(alpha = 0.75f), style = Stroke(1.5f * u))
            }
            for (j in 0 until 4) {
                val y = lomo.top + lomo.height * (0.2f + 0.6f * azar(s, 50 + j))
                drawLine(a.claro(0.12f), Offset(lomo.left + lomo.width * 0.15f, y), Offset(lomo.left + lomo.width * (0.4f + 0.4f * azar(s, 54 + j)), y + 0.6f * u), 0.7f * u)
            }
        }
        // Las cofias de piel arriba y abajo, con su borde gastado, y los nervios.
        val piel = mezcla(P.TECA_OSC, P.MINIO, 0.2f)
        val tc = Tinte(l.c(piel), l.c(piel, 0.25f), l.c(piel, -0.5f))
        listOf(Rect(lomo.left, lomo.top, lomo.right, lomo.top + h * 0.09f), Rect(lomo.left, h - h * 0.075f, lomo.right, h)).forEach { cr ->
            redondo(cr, tc, hacia)
            drawRect(a.sombra(0.35f), Offset(cr.left, if (cr.top < h / 2) cr.bottom else cr.top - 1f * u), Size(cr.width, 1f * u))
        }
        val nervios = if (azar(s, 7) > 0.45f) listOf(0.27f, 0.55f) else listOf(0.24f, 0.44f, 0.6f)
        nervios.forEach { fy ->
            val y = h * fy
            drawRect(a.sombra(0.42f), Offset(lomo.left, y + 1.4f * u), Size(lomo.width, 1.1f * u))
            drawRect(t.hondo, Offset(lomo.left, y), Size(lomo.width, 2.2f * u))
            drawRect(t.claro, Offset(lomo.left, y - 0.5f * u), Size(lomo.width, 1.2f * u))
        }
        // El tejuelo de papel con el día, debajo del último nervio.
        val et0 = h * nervios.last() + 3f * u
        etiqueta(v, medidor, Rect(lomo.left + 1.2f * u, et0, lomo.right - 1.2f * u, minOf(et0 + 30f * u, h - h * 0.08f - 1f * u)), l)
        // El registro de cinta que cuelga por el pie, en algunos.
        if (azar(s, 9) > 0.5f) {
            val cx = lomo.left + lomo.width * (0.3f + 0.4f * azar(s, 11))
            drawLine(l.c(if ((s and 2) == 0) P.LACRE else P.VERDIN_OSC), Offset(cx, h - 2f * u), Offset(cx + (azar(s, 12) - 0.5f) * 3f * u, h + 2.4f * u), 1.6f * u)
        }
        // La luna de los Sueños, entre la cofia de arriba y el primer nervio.
        if (v.noche) luna(Offset(w / 2, (h * 0.09f + h * nervios[0]) / 2), minOf(lomo.width * 0.24f, 5.4f * u, (h * nervios[0] - h * 0.09f) * 0.42f), a)
        filos(lomo, l)
    }

    // ---------------------------------------------------------------- la botella lacrada

    fun DrawScope.botella(v: Volumen, medidor: TextMeasurer, a: AireArcano, k: Float, hacia: Float, frio: Float, x: Float?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, hacia, frio)
        val m = material(x, v.tono, VIDRIOS)
        val vidrio = m ?: VIDRIO_SIN
        val t = tinte(vidrio, m != null, l)
        // La forma: de vino (hombros redondos y cuello largo) o de ginebra (hombros rectos, cuerpo que se angosta, cuello corto).
        val ginebra = azar(s, 4) > 0.6f
        val cuello = maxOf(w * 0.32f, 8.5f * u).coerceAtMost(w * 0.5f)
        val boca = h * 0.02f
        val hombro = h * (if (ginebra) 0.2f else 0.36f + 0.06f * azar(s, 6))
        val cuelloAbajo = h * (if (ginebra) 0.13f else 0.2f)
        val cx = w / 2
        val cuerpo = Path().apply {
            moveTo(cx - cuello / 2, boca + 5f * u)
            lineTo(cx - cuello / 2, cuelloAbajo)
            if (ginebra) { lineTo(0.6f * u, hombro); lineTo(w * 0.06f, h - 1f * u) }
            else { cubicTo(cx - cuello / 2, hombro - h * 0.05f, 0.6f * u, hombro - h * 0.08f, 0.6f * u, hombro + h * 0.02f); lineTo(0.6f * u, h - 3f * u) }
            quadraticTo(0.6f * u, h, 3f * u, h)
            lineTo(w - 3f * u, h)
            quadraticTo(w - 0.6f * u, h, w - 0.6f * u, h - 3f * u)
            if (ginebra) { lineTo(w * 0.94f, h - 1f * u); lineTo(w - 0.6f * u, hombro); lineTo(cx + cuello / 2, cuelloAbajo) }
            else { lineTo(w - 0.6f * u, hombro + h * 0.02f); cubicTo(w - 0.6f * u, hombro - h * 0.08f, cx + cuello / 2, hombro - h * 0.05f, cx + cuello / 2, cuelloAbajo) }
            lineTo(cx + cuello / 2, boca + 5f * u)
            close()
        }
        alrededor(a, hacia, hombro)
        // Lo que el vidrio de color echa en la tabla, del lado contrario a la luz.
        if (m != null && a.lumbre && k > 0.25f) drawOval(t.claro.copy(alpha = 0.3f * k), Offset(if (hacia < 0f) w * 0.4f else -w * 0.5f, h - 3f * u), Size(w * 1.1f, 4f * u))
        // El vidrio: algo transparente (deja ver el paño), por franjas, y lo de dentro.
        clipPath(cuerpo) {
            drawRect(t.hondo.copy(alpha = 0.92f), Offset(0f, 0f), Size(w, h))
            redondo(Rect(0f, cuelloAbajo, w, h), Tinte(t.cara.copy(alpha = 0.9f), t.claro.copy(alpha = 0.9f), t.hondo.copy(alpha = 0.95f)), hacia)
            // El rollo de papel dentro, atado con su hilo: visto a través del vidrio, teñido de su color; más grueso si va lleno.
            val rw = w * (0.32f + 0.36f * v.lleno)
            val rollo = Rect(cx - rw / 2 + w * 0.04f * (azar(s, 8) - 0.5f), hombro - h * 0.08f, cx + rw / 2, h - 3f * u)
            val papel = mezcla(l.c(P.PAPEL), t.cara, if (m == null) 0.55f else 0.42f)
            drawRect(papel, rollo.topLeft, rollo.size)
            drawRect(mezcla(papel, t.hondo, 0.4f), Offset(if (hacia < 0f) rollo.right - rollo.width * 0.3f else rollo.left, rollo.top), Size(rollo.width * 0.3f, rollo.height))
            drawOval(mezcla(papel, t.claro, 0.3f), Offset(rollo.left, rollo.top - 2f * u), Size(rollo.width, 4f * u))
            drawOval(mezcla(papel, t.hondo, 0.5f), Offset(rollo.left + rollo.width * 0.3f, rollo.top - 1f * u), Size(rollo.width * 0.4f, 2f * u), style = Stroke(0.7f * u))
            drawLine(mezcla(l.c(P.LACRE), t.cara, 0.4f), Offset(rollo.left, rollo.top + rollo.height * 0.4f), Offset(rollo.right, rollo.top + rollo.height * 0.42f), 1.4f * u)
            // El grueso del vidrio en los cantos y el culo hundido.
            drawRect(t.hondo.copy(alpha = 0.5f), Offset(0f, 0f), Size(w * 0.08f, h))
            drawRect(t.hondo.copy(alpha = 0.6f), Offset(w * 0.92f, 0f), Size(w * 0.08f, h))
            drawOval(t.hondo.copy(alpha = 0.7f), Offset(w * 0.2f, h - 5f * u), Size(w * 0.6f, 5f * u))
            drawOval(t.claro.copy(alpha = 0.5f), Offset(w * 0.25f, h - 4.6f * u), Size(w * 0.5f, 1.2f * u))
        }
        // Los brillos del vidrio: la ventana (o la llama) en una raya larga del lado de la luz, y otra más fina.
        val claro = if (a.oscuro) mezcla(P.FUEGO, Color.White, 0.3f).copy(alpha = (0.15f + 0.8f * k).coerceAtMost(0.9f)) else if (a.noche) mezcla(P.OCASO, Color.White, 0.5f).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f)
        val xb = if (hacia < 0f) w * 0.2f else w * 0.72f
        drawRoundRect(claro, Offset(xb, hombro + h * 0.02f), Size(maxOf(2f * u, w * 0.08f), h * 0.4f), CornerRadius(2f * u))
        drawRect(claro.copy(alpha = claro.alpha * 0.5f), Offset(xb + w * 0.14f * (if (hacia < 0f) 1f else -1f), hombro + h * 0.1f), Size(1f * u, h * 0.25f))
        drawLine(claro.copy(alpha = claro.alpha * 0.7f), Offset(cx - cuello * 0.25f * (if (hacia < 0f) 1f else -1f), cuelloAbajo), Offset(cx - cuello * 0.25f * (if (hacia < 0f) 1f else -1f), boca + 7f * u), 1.2f * u)
        drawPath(cuerpo, t.hondo.copy(alpha = 0.8f), style = Stroke(0.9f * u))
        // El anillo de la boca, el corcho con su lacre que chorrea por el cuello y el hilo atado debajo.
        drawRect(t.claro, Offset(cx - cuello / 2 - 1f * u, boca + 4f * u), Size(cuello + 2f * u, 3f * u))
        val lacre = if ((s shr 3) and 1 == 0) P.LACRE else Color(0xFF26201C)
        val cap = Path().apply {
            moveTo(cx - cuello / 2 - 1.4f * u, boca + 8f * u)
            lineTo(cx - cuello / 2 - 1f * u, boca); quadraticTo(cx, boca - 2.4f * u, cx + cuello / 2 + 1f * u, boca)
            lineTo(cx + cuello / 2 + 1.4f * u, boca + 8f * u)
            // Los chorreones.
            lineTo(cx + cuello * 0.3f, boca + 8f * u); quadraticTo(cx + cuello * 0.22f, boca + 15f * u, cx + cuello * 0.12f, boca + 8f * u)
            lineTo(cx - cuello * 0.1f, boca + 8f * u); quadraticTo(cx - cuello * 0.2f, boca + (11f + 6f * azar(s, 14)) * u, cx - cuello * 0.32f, boca + 8f * u)
            close()
        }
        translate(0.8f * u, 1f * u) { drawPath(cap, a.sombra(0.4f)) }
        drawPath(cap, l.c(lacre))
        drawRect(l.c(lacre, 0.3f), Offset(cx - cuello / 2 + 1f * u, boca + 0.4f * u), Size(cuello * 0.35f, 1.2f * u))
        val hilo = l.c(P.LONA)
        drawLine(hilo, Offset(cx - cuello / 2, cuelloAbajo - 4f * u), Offset(cx + cuello / 2, cuelloAbajo - 3f * u), 1.1f * u)
        drawLine(hilo, Offset(cx - cuello / 2, cuelloAbajo - 6f * u), Offset(cx + cuello / 2, cuelloAbajo - 5.4f * u), 1f * u)
        // La etiqueta pegada en la panza, con el día; en algunas, la esquina despegada.
        val etH = minOf(31f * u, h * 0.4f)
        val etY = h * (0.56f + 0.08f * azar(s, 15)) - etH / 2
        val et = Rect(w * 0.1f, maxOf(hombro + 3f * u, etY), w * 0.9f, maxOf(hombro + 3f * u, etY) + etH)
        etiqueta(v, medidor, et, l, (azar(s, 16) - 0.5f) * 3f)
        if (azar(s, 17) > 0.6f) drawPath(poligonoArcano(et.right - 5f * u, et.bottom, et.right, et.bottom, et.right, et.bottom - 5f * u), a.etiqueta(mezcla(P.MANILA, Color.White, 0.25f), k))
        if (v.noche) luna(Offset(cx, maxOf(hombro - h * 0.02f, cuelloAbajo + 6f * u)), minOf(w * 0.17f, 4.6f * u), a)
        filos(Rect(0f, hombro, w, h), l)
    }

    // ---------------------------------------------------------------- el fardo en un trozo de vela

    fun DrawScope.fardo(v: Volumen, medidor: TextMeasurer, a: AireArcano, k: Float, hacia: Float, frio: Float, x: Float?) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla
        val l = Luz(a, k, hacia, frio)
        val m = material(x, v.tono, LONAS)
        val t = tinte(m ?: LONA_SIN, m != null, l)
        val lado = if (azar(s, 5) > 0.5f) 1f else -1f
        val panza = w * (0.03f + 0.05f * v.lleno)
        val solapa = h * (0.2f + 0.05f * azar(s, 6))
        alrededor(a, hacia, h * 0.1f)
        // Los papeles que asoman por debajo de la solapa: más cuanto más escribiste.
        val hojas = 1 + (v.lleno * 2.5f).toInt()
        for (j in 0 until hojas) {
            val px = w * (0.15f + 0.5f * azar(s, 40 + j))
            rotate((azar(s, 41 + j) - 0.5f) * 16f, Offset(px, h * 0.12f)) {
                drawRect(l.c(if (j % 2 == 0) P.PAPEL else P.MANILA), Offset(px, h * 0.03f), Size(w * 0.3f, h * 0.12f))
            }
        }
        // El bulto: blando, con la panza que se cae hacia un lado y el pie aplastado en la tabla.
        val bulto = Path().apply {
            moveTo(w * 0.1f, h * 0.08f)
            cubicTo(w * 0.35f, h * 0.05f, w * 0.65f, h * 0.06f, w * 0.9f, h * 0.09f)
            cubicTo(w + panza * (if (lado > 0f) 1.2f else 0.6f), h * 0.35f, w + panza, h * 0.8f, w - 0.6f * u, h - 1f * u)
            quadraticTo(w * 0.5f, h + 1f * u, 0.6f * u, h - 1f * u)
            cubicTo(-panza, h * 0.8f, -panza * (if (lado < 0f) 1.2f else 0.6f), h * 0.35f, w * 0.1f, h * 0.08f)
            close()
        }
        clipPath(bulto) {
            redondo(Rect(-panza, 0f, w + panza, h), t, hacia)
            // Las costuras de la vela (dos pespuntes paralelos), en diagonal.
            val cx0 = w * (0.2f + 0.3f * azar(s, 7))
            listOf(0f, 2.6f * u).forEach { d ->
                val p = Path()
                var y = h * 0.1f
                while (y < h) { p.moveTo(cx0 + d + (y / h) * w * 0.25f * lado, y); p.lineTo(cx0 + d + ((y + 2f * u) / h) * w * 0.25f * lado, y + 2f * u); y += 3.6f * u }
                drawPath(p, t.hondo.copy(alpha = 0.8f), style = Stroke(0.7f * u))
            }
            drawLine(t.claro.copy(alpha = 0.6f), Offset(cx0 + 1.3f * u, h * 0.1f), Offset(cx0 + 1.3f * u + w * 0.25f * lado, h), 0.7f * u)
            // Los pliegues blandos que bajan, torcidos.
            for (j in 0 until 3) {
                val x = w * (0.22f + 0.28f * j)
                val p = Path().apply { moveTo(x, h * 0.2f); cubicTo(x + lado * 3f * u, h * 0.45f, x - lado * 2f * u, h * 0.7f, x + lado * 4f * u, h * (0.88f + 0.08f * azar(s, 20 + j))) }
                drawPath(p, a.sombra(0.3f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(p, Offset(2.2f * u, 0f)) }, t.claro.copy(alpha = 0.5f), style = Stroke(1f * u, cap = StrokeCap.Round))
            }
            // El pie aplastado, con su sombra, y el roce de la lona.
            drawOval(a.sombra(0.3f), Offset(w * 0.04f, h * 0.9f), Size(w * 0.92f, h * 0.12f))
            for (j in 0 until 3) drawLine(a.claro(0.14f), Offset(w * azar(s, 30 + j), h * (0.4f + 0.3f * azar(s, 33 + j))), Offset(w * azar(s, 36 + j), h * (0.42f + 0.3f * azar(s, 33 + j))), 0.8f * u)
        }
        // La solapa doblada por arriba: la vuelta de la lona que cae sobre el frente, con su orilla y la relinga.
        val sol = Path().apply {
            moveTo(w * 0.06f, h * 0.09f)
            cubicTo(w * 0.35f, h * 0.04f, w * 0.7f, h * 0.05f, w * 0.94f, h * 0.1f)
            lineTo(w * (0.9f - 0.1f * lado), solapa + h * 0.03f)
            quadraticTo(w * 0.5f, solapa + h * (0.07f + 0.03f * lado), w * (0.12f - 0.06f * lado), solapa)
            close()
        }
        translate(0f, 1.6f * u) { drawPath(sol, a.sombra(0.4f)) }
        drawPath(sol, t.claro)
        clipPath(sol) { drawRect(t.cara, Offset(if (hacia < 0f) w * 0.6f else 0f, 0f), Size(w * 0.4f, h)) }
        val orilla = Path().apply { moveTo(w * (0.9f - 0.1f * lado), solapa + h * 0.03f); quadraticTo(w * 0.5f, solapa + h * (0.07f + 0.03f * lado), w * (0.12f - 0.06f * lado), solapa) }
        drawPath(orilla, l.c(P.LONA_OSC, -0.1f), style = Stroke(2.2f * u))
        drawPath(orilla, t.claro, style = Stroke(0.7f * u))
        // El ollao de latón de la orilla, con su hueco.
        val ox = if (lado > 0f) w * 0.72f else w * 0.3f
        val oc = Offset(ox, solapa + h * 0.035f)
        drawCircle(a.sombra(0.4f), 2.8f * u, oc + Offset(0.6f * u, 0.8f * u))
        drawCircle(l.c(P.LATON), 2.7f * u, oc)
        drawCircle(t.hondo, 1.3f * u, oc)
        drawCircle(a.brillo(0.6f, k), 0.7f * u, oc - Offset(1.2f * u, 1.2f * u))
        // Las vueltas de piola alquitranada, de lado a lado (sin cruzarse), con el nudo delante y sus dos cabos.
        val vueltas = if (azar(s, 8) > 0.5f) listOf(0.44f, 0.74f) else listOf(0.4f, 0.6f, 0.8f)
        val piola = l.c(P.PIOLA)
        vueltas.forEach { fy ->
            val y = h * fy
            val p = Path().apply { moveTo(-panza, y + 1.5f * u); quadraticTo(w * 0.5f, y - 2.5f * u, w + panza, y + 1.5f * u) }
            drawPath(p, a.sombra(0.4f), style = Stroke(2.6f * u))
            drawPath(p, piola, style = Stroke(1.8f * u))
            drawPath(Path().apply { addPath(p, Offset(0f, -0.6f * u)) }, a.brillo(0.3f, k), style = Stroke(0.5f * u))
        }
        val ny = h * vueltas.first() - 1f * u
        val nudo = Offset(if (lado > 0f) w * 0.32f else w * 0.68f, ny)
        drawCircle(piola, 2.4f * u, nudo)
        drawCircle(a.brillo(0.35f, k), 0.8f * u, nudo - Offset(0.8f * u, 0.8f * u))
        listOf(-1f, 1f).forEach { q -> drawLine(piola, nudo, Offset(nudo.x + q * 2.6f * u, nudo.y + h * (0.1f + 0.03f * q)), 1.4f * u, StrokeCap.Round) }
        // La tarjeta del día, metida bajo la vuelta de arriba, un poco ladeada.
        val etH = minOf(30f * u, h * 0.36f)
        val et = Rect(w * 0.1f, h * vueltas.first() - etH * 0.3f, w * 0.9f, h * vueltas.first() + etH * 0.7f)
        etiqueta(v, medidor, et, l, (azar(s, 12) - 0.5f) * 5f)
        val p = Path().apply { moveTo(-panza, h * vueltas.first() + 1.5f * u); quadraticTo(w * 0.5f, h * vueltas.first() - 2.5f * u, w + panza, h * vueltas.first() + 1.5f * u) }
        clipRect(et.left, et.top, et.right, et.top + etH * 0.32f) { drawPath(p, piola, style = Stroke(1.8f * u)) }
        if (v.noche) luna(Offset(w * 0.5f, h * 0.88f - 3f * u), minOf(w * 0.18f, 4.8f * u), a)
        filos(Rect(0f, h * 0.08f, w, h), l)
    }
}
