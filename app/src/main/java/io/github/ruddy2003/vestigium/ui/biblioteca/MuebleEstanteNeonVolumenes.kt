package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HUESO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.PAPEL
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.pow

// ---------------------------------------------------------------- el color del ánimo

/** La escala del ánimo del tema (la misma de día y de noche: ámbar, el casi negro de en medio y magenta). */
private val ESCALA_NEON: List<Color> by lazy { juegoTonos(TemaId.NEON, true).take(3) }

/**
 * Los colores con que se dice el ánimo en lo guardado, en el orden de la escala del tema (de lo peor a lo mejor): ámbar,
 * mandarina, el hueso de en medio, orquídea y magenta. En la interfaz el ánimo de en medio es un casi negro (el fondo); en un
 * objeto del cuarto se leería como un agujero, así que aquí es el hueso del plástico o la hojalata sin teñir. Los extremos son
 * los del tema.
 */
private val TINTES_NEON = listOf(Color(0xFFFFB238), Color(0xFFF08A3C), Color(0xFFE9E1D6), Color(0xFFD466B8), Color(0xFFFF3D9A))

/** Dónde cae [c] en la escala del tema (de 0 a 1), o null si no está en ella. */
private fun enEscalaNeon(c: Color): Float? {
    val e = ESCALA_NEON
    if (e.size < 3) return null
    var mejor = -1f; var dist = Float.MAX_VALUE
    for (i in 0..60) {
        val x = i / 60f
        val p = if (x < 0.5f) mezcla(e[0], e[1], x * 2) else mezcla(e[1], e[2], (x - 0.5f) * 2)
        val d = (p.red - c.red).pow(2) + (p.green - c.green).pow(2) + (p.blue - c.blue).pow(2)
        if (d < dist) { dist = d; mejor = x }
    }
    return if (dist < 0.02f) mejor else null
}

/** El tinte que corresponde a un color del ánimo: su punto en la escala del tema, llevado a [TINTES_NEON]. */
internal fun tenidoNeon(t: Color?): Color? {
    if (t == null) return null
    val x = enEscalaNeon(t) ?: return t
    val q = x.coerceIn(0f, 1f) * (TINTES_NEON.size - 1)
    val i = q.toInt().coerceAtMost(TINTES_NEON.size - 2)
    return mezcla(TINTES_NEON[i], TINTES_NEON[i + 1], q - i)
}

/**
 * Un color del ánimo en la penumbra: su mismo tono, hondo. Los de color se oscurecen hacia un violeta casi negro (el magenta y la
 * orquídea, ciruela; el ámbar, pardo); los apagados (el hueso, la hojalata) no se vuelven gris, sino ocre hondo.
 */
private fun hundidoNeon(c: Color): Color {
    val sat = maxOf(c.red, c.green, c.blue) - minOf(c.red, c.green, c.blue)
    val color = Color(c.red * 0.22f + 0.05f, c.green * 0.22f + 0.016f, c.blue * 0.22f + 0.07f, c.alpha)
    val ocre = Color(c.red * 0.28f, c.green * 0.21f, c.blue * 0.12f, c.alpha)
    return mezcla(ocre, color, (sat * 2.6f).coerceIn(0f, 1f))
}

// ---------------------------------------------------------------- la luz de un volumen

/** La luz que le toca a un volumen: el tono de su centro y el de su parte de arriba, y el aire. */
private class LuzVolumenNeon(val a: AireNeon, val t: TonoNeon, val arriba: TonoNeon) {
    fun c(col: Color, mas: Float = 1f) = t.c(col, mas)
    fun s(al: Float) = t.s(al)
    /** El filo de luz: lejos de la luz, apenas (que no aclare lo que está en penumbra). */
    fun b(al: Float, mas: Float = 1f) = t.b(al * (0.4f + 0.6f * (t.k * mas / 0.6f).coerceIn(0f, 1f)), mas)
    val izq: Boolean get() = t.izq
    /**
     * El ánimo: su color hundido por la distancia a la luz sin perder el tono (lejos, el magenta es ciruela y el hueso, ocre
     * hondo), que solo aclara cerca de una luz.
     */
    fun animo(col: Color, mas: Float = 1f): Color {
        val k = (t.k * mas).coerceIn(0f, 1.3f)
        val apagado = hundidoNeon(col)
        val lit = t.c(col, mas)
        val vivo = mezcla(col, lit, 0.5f)
        return mezcla(apagado, vivo, (1.3f * k.pow(1.8f)).coerceAtMost(0.97f))
    }
    fun etiqueta(base: Color = PAPEL): Color = a.etiqueta(t.k, t.l, base)
}

private const val TAM_DIA_NEON = 16.5f
private const val TAM_MES_NEON = 6.6f

/** El mes corto en español («nov.»), de lo que trae el volumen (el tema lo nombra «11月 NOV»): solo las letras latinas. */
private fun mesCortoNeon(mes: String): String {
    val letras = mes.filter { it in 'A'..'Z' || it in 'a'..'z' || it in "ÁÉÍÓÚÑáéíóúñ" }
    return if (letras.length >= 3) letras.take(3).lowercase() + "." else letras.lowercase()
}

/** El día, grande, y el mes, chico, dentro de [r], en [tinta]. */
private fun DrawScope.fechaNeon(r: Rect, v: Volumen, medidor: TextMeasurer, tinta: Color) {
    val corte = r.top + r.height * 0.68f
    textoEn(medidor, v.dia, Rect(r.left, r.top, r.right, corte), PaletaNeon.LETRA, tinta, TAM_DIA_NEON, peso = FontWeight.Bold)
    textoEn(medidor, mesCortoNeon(v.mes), Rect(r.left, corte - r.height * 0.05f, r.right, r.bottom), PaletaNeon.LETRA, tinta.copy(alpha = 0.88f), TAM_MES_NEON, peso = FontWeight.SemiBold, minimo = 4f)
}

/** La sombra del volumen en el fondo del casillero (del lado contrario a la luz) y la de contacto en la tabla. */
private fun DrawScope.asientoNeon(l: LuzVolumenNeon, x0: Float, x1: Float, arriba: Float) {
    val u = density; val w = size.width; val h = size.height
    val ancho = x1 - x0
    if (l.izq) drawRect(l.s(0.45f), Offset(x1, arriba + h * 0.06f), Size(w - x1, h - arriba - h * 0.06f))
    else drawRect(l.s(0.45f), Offset(0f, arriba + h * 0.06f), Size(x0, h - arriba - h * 0.06f))
    drawOval(l.s(0.55f), Offset(x0 + (if (l.izq) ancho * 0.1f else -ancho * 0.08f), h - 3f * u), Size(ancho, 4.4f * u))
}

/** Una media luna recortada, con su sombra y su brillo, sobre un fondo de color [fondo]. */
private fun DrawScope.lunaNeon(c: Offset, r: Float, fondo: Color, l: LuzVolumenNeon, plata: Color = Color(0xFFE6ECF2)) {
    val p = Path().apply {
        addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r))
    }
    val hueco = Path().apply { addOval(Rect(c.x - r * 0.35f, c.y - r * 1.15f, c.x + r * 1.45f, c.y + r * 0.65f)) }
    val luna = Path().apply { op(p, hueco, androidx.compose.ui.graphics.PathOperation.Difference) }
    drawPath(Path().apply { addPath(luna, Offset(r * 0.14f, r * 0.18f)) }, l.s(0.4f))
    drawPath(luna, l.c(plata, 1.25f))
    drawPath(luna, l.b(0.6f, 1.3f), style = Stroke(0.6f * density))
}

/**
 * Las tres clases de entrada del cuarto de Ciudad neón, elegidas con la semilla del día (la clase sale de la semilla, nunca de
 * su sitio; el sitio solo dice qué luz le llega):
 * - el cartucho de datos: rígido, alto y delgado, de plástico translúcido del ánimo, con la empuñadura de estrías, la etiqueta
 *   del día, la ventanita con la bobina (más gruesa cuanto más escribiste), los tornillos y la persiana de metal;
 * - la lata de té: hojalata litografiada del ánimo con un dibujo inventado (ondas, lunares o franjas), los hombros, la tapa
 *   redonda estriada, abolladuras y óxido, y la tarjeta del día atada con cordel al cuello;
 * - el saco de cordón del repartidor: tela técnica del ánimo, blanda y sentada, con la boca fruncida caída a un lado, la jareta,
 *   el cordón con su tanca, la cinta reflectante, el refuerzo de la base y la tira de cinta de carrocero con el día en la panza;
 *   engorda con las palabras.
 * Sin ánimo: el plástico ahumado, la hojalata sin imprimir y el nailon pardo. En los Sueños, una media luna en cada una.
 */
internal fun DrawScope.volumenNeon(v: Volumen, medidor: TextMeasurer) {
    if (size.width < 4f || size.height < 4f) return
    val a = AireNeon(v.noche)
    val p = lugarVolumenNeon(v.repisa, v.centro)
    val lampY = lampNominalNeon(v.repisas); val estufaY = estufaNominalNeon(v.repisas)
    val t = tonoNeon(p.x, p.y, a, lampY, estufaY)
    val arriba = tonoNeon(p.x, p.y - 0.11f, a, lampY, estufaY)
    val l = LuzVolumenNeon(a, t, arriba)
    val tinte = tenidoNeon(v.tono)
    when (MuebleEstanteNeon.clase(v.semilla)) {
        0 -> cartuchoNeon(v, medidor, l, tinte)
        1 -> lataTeNeon(v, medidor, l, tinte)
        else -> sacoCordonNeon(v, medidor, l, tinte)
    }
}

// ---------------------------------------------------------------- el cartucho de datos

/**
 * El cartucho de datos, de pie: la carcasa de plástico translúcido del ánimo con la esquina de arriba achaflanada, su canto de
 * sombra (el grueso) y su filo; la empuñadura de estrías arriba; la etiqueta de papel hundida con el día y el mes; la ventanita
 * ahumada con la bobina de cinta (más gruesa cuanto más escribiste) y su cubo dentado; los dos tornillos; la persiana de metal
 * abajo, con su ranura; una pegatina de color en algunos y rayones.
 */
private fun DrawScope.cartuchoNeon(v: Volumen, medidor: TextMeasurer, l: LuzVolumenNeon, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val x0 = (1.4f + 2.4f * azar(s, 77)) * u; val x1 = w - (2.4f + 2.6f * azar(s, 78)) * u
    val y0 = h * 0.01f; val y1 = h - 1f * u
    val ancho = x1 - x0
    val giro = if (azar(s, 79) > 0.72f) (if (azar(s, 80) > 0.5f) 1.6f else -1.6f) else 0f
    asientoNeon(l, x0, x1, y0)
    val plastico = if (tinte != null) l.animo(tinte) else l.c(Color(0xFF4A4856))
    val claro = if (tinte != null) l.animo(mezcla(tinte, Color.White, 0.3f), 1.1f) else l.c(Color(0xFF6E6C7C))
    val hondo = if (tinte != null) l.animo(mezcla(tinte, Color(0xFF14101C), 0.45f), 0.8f) else l.c(Color(0xFF26242E))
    rotate(giro, Offset((x0 + x1) / 2, y1)) {
        val chaflan = ancho * 0.22f
        val cuerpo = poligonoNeon(x0 + chaflan, y0, x1 - 2f * u, y0, x1, y0 + 2f * u, x1, y1, x0, y1, x0, y0 + chaflan)
        drawPath(cuerpo, plastico)
        clipPath(cuerpo) {
            // El canto (el grueso de la carcasa) del lado de la sombra, y el filo del lado de la luz.
            val canto = ancho * 0.14f
            drawRect(hondo, Offset(if (l.izq) x1 - canto else x0, y0), Size(canto, y1 - y0))
            drawRect(l.s(0.25f), Offset(if (l.izq) x1 - canto * 1.6f else x0 + canto, y0), Size(canto * 0.6f, y1 - y0))
            drawRect(l.b(0.45f), Offset(if (l.izq) x0 + 1f * u else x1 - 2.4f * u, y0), Size(1.4f * u, y1 - y0))
            // La empuñadura de estrías.
            val e0 = y0 + h * 0.035f; val e1 = y0 + h * 0.15f
            for (j in 0 until 6) {
                val y = e0 + (e1 - e0) * j / 6f
                drawRect(l.s(0.35f), Offset(x0 + ancho * 0.12f, y), Size(ancho * 0.74f, 1.3f * u))
                drawRect(claro, Offset(x0 + ancho * 0.12f, y + 1.3f * u), Size(ancho * 0.74f, 0.9f * u))
            }
            // La ventanita ahumada con la bobina.
            val vy0 = y0 + h * (0.55f + 0.04f * azar(s, 81)); val vy1 = vy0 + h * 0.2f
            val ven = Rect(x0 + ancho * 0.16f, vy0, x1 - ancho * 0.2f, vy1)
            drawRoundRect(l.c(Color(0xFF14121A)), ven.topLeft, ven.size, CornerRadius(3f * u))
            clipRect(ven.left, ven.top, ven.right, ven.bottom) {
                val cc = Offset(ven.center.x, ven.center.y)
                val rr = ven.width * (0.16f + 0.3f * v.lleno)
                drawCircle(l.c(Color(0xFF4A2E22), 1.1f), rr, cc)
                for (j in 1..3) drawCircle(l.c(Color(0xFF6A4632), 1.1f), rr * (0.4f + 0.18f * j), cc, style = Stroke(0.6f * u))
                drawCircle(l.c(HUESO, 0.9f), ven.width * 0.12f, cc)
                for (j in 0 until 6) {
                    val an = j * 1.047f
                    drawLine(l.c(Color(0xFF2A2830)), cc, Offset(cc.x + ven.width * 0.12f * kotlin.math.cos(an), cc.y + ven.width * 0.12f * kotlin.math.sin(an)), 1f * u)
                }
                drawCircle(l.c(Color(0xFF2A2830)), ven.width * 0.04f, cc)
                // El reflejo en el plástico de la ventana.
                drawPath(poligonoNeon(ven.left + ven.width * 0.2f, ven.top, ven.left + ven.width * 0.38f, ven.top, ven.left + ven.width * 0.1f, ven.bottom, ven.left - ven.width * 0.08f, ven.bottom), Color.White.copy(alpha = 0.14f))
            }
            drawRoundRect(l.b(0.4f), ven.topLeft, ven.size, CornerRadius(3f * u), style = Stroke(0.9f * u))
            // La persiana de metal y su ranura.
            val p0 = y1 - h * 0.13f
            val pers = Rect(x0 + ancho * 0.08f, p0, x1 - ancho * 0.12f, y1 - h * 0.025f)
            val metal = if (azar(s, 82) > 0.6f) LATON else ALUMINIO
            drawRect(l.s(0.4f), pers.topLeft + Offset(1f * u, 1f * u), pers.size)
            drawRect(l.c(metal, 1.05f), pers.topLeft, pers.size)
            drawRect(l.b(0.55f, 1.2f), pers.topLeft, Size(pers.width, 1f * u))
            drawRect(l.s(0.45f), Offset(pers.left + pers.width * 0.12f, pers.center.y - 1f * u), Size(pers.width * 0.5f, 2f * u))
            // Los tornillos.
            listOf(x0 + ancho * 0.16f, x1 - ancho * 0.22f).forEach { x ->
                val c = Offset(x, vy1 + (p0 - vy1) * 0.5f)
                drawCircle(l.s(0.4f), 1.6f * u, c + Offset(0.6f * u, 0.6f * u))
                drawCircle(l.c(ALUMINIO, 0.9f), 1.5f * u, c)
                drawLine(l.s(0.6f), Offset(c.x - 1f * u, c.y), Offset(c.x + 1f * u, c.y), 0.6f * u)
            }
            // Los rayones.
            for (j in 0 until 2) {
                val y = y0 + h * (0.2f + 0.6f * azar(s, 83 + j))
                drawLine(l.b(0.3f), Offset(x0 + ancho * azar(s, 85 + j), y), Offset(x0 + ancho * azar(s, 87 + j), y + h * 0.03f), 0.6f * u)
            }
            // La luna de los Sueños, en la carcasa bajo la ventana; o, de día, una pegatina de color en algunos.
            if (v.noche) lunaNeon(Offset(x0 + ancho * 0.36f, (vy1 + p0) / 2 - h * 0.005f), ancho * 0.14f, plastico, l)
            else if (azar(s, 89) > 0.6f) drawCircle(l.c(if (azar(s, 90) > 0.5f) Color(0xFF52C8D8) else Color(0xFF8CE85A)), ancho * 0.07f, Offset(x1 - ancho * 0.3f, (vy1 + p0) / 2))
        }
        // El filo del chaflán y del canto de arriba.
        drawLine(l.b(0.7f, 1.2f), Offset(x0, y0 + chaflan), Offset(x0 + chaflan, y0), 1f * u)
        drawLine(l.b(0.55f, 1.2f), Offset(x0 + chaflan, y0 + 0.5f * u), Offset(x1 - 2f * u, y0 + 0.5f * u), 1f * u)
        // La etiqueta de papel, hundida: el día y el mes.
        val ey0 = y0 + h * (0.19f + 0.03f * azar(s, 91)); val ey1 = ey0 + h * 0.31f
        val et = Rect(x0 + ancho * 0.1f, ey0, x1 - ancho * 0.16f, ey1)
        drawRect(l.s(0.5f), Offset(et.left - 1f * u, et.top - 1f * u), Size(et.width + 2f * u, et.height + 2f * u))
        val papel = l.etiqueta(if (azar(s, 92) > 0.5f) PAPEL else Color(0xFFD9CFA8))
        drawRect(papel, et.topLeft, et.size)
        drawRect(l.s(0.18f), Offset(et.left, et.bottom - et.height * 0.12f), Size(et.width, et.height * 0.12f))
        drawLine(l.c(Color(0xFFC8402E), 0.9f), Offset(et.left + 1.5f * u, et.top + 2f * u), Offset(et.right - 1.5f * u, et.top + 2f * u), 1f * u)
        fechaNeon(Rect(et.left, et.top + et.height * 0.08f, et.right, et.bottom - et.height * 0.04f), v, medidor, l.a.tinta)
    }
}

// ---------------------------------------------------------------- la lata de té

/**
 * La lata de té, de pie: el cuerpo cilíndrico de hojalata litografiado del ánimo con su dibujo (ondas, lunares o franjas, según
 * la semilla) y sus filetes dorados; la tapa a todo el ancho, un pelo más ancha, con su canto estriado y su pomo redondo; el
 * fondo de metal con el óxido; una abolladura; los planos de luz y de sombra del cilindro y el brillo largo del metal; y la
 * tarjeta del día atada al pomo con su cordel. Muy llena, la tapa queda un poco levantada y asoman las hojas.
 */
private fun DrawScope.lataTeNeon(v: Volumen, medidor: TextMeasurer, l: LuzVolumenNeon, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val x0 = (1.6f + 2f * azar(s, 77)) * u; val x1 = w - (2.6f + 2.4f * azar(s, 78)) * u
    val ancho = x1 - x0
    val y1 = h - 1f * u
    val cx = (x0 + x1) / 2
    val alza = if (v.lleno > 0.65f) h * 0.02f else 0f
    val t0 = h * 0.045f + 0f; val t1 = h * (0.15f + 0.02f * azar(s, 79))
    asientoNeon(l, x0, x1, t0)
    val impreso = if (tinte != null) l.animo(tinte) else l.c(ALUMINIO, 0.95f)
    val laton = tinte != null && azar(s, 80) > 0.45f
    val metal = l.c(if (laton) LATON else ALUMINIO)
    val metalArriba = l.arriba.c(if (laton) LATON else ALUMINIO, 1.1f)
    // Las hojas que asoman bajo la tapa levantada.
    if (alza > 0f) for (j in 0 until 3) drawRect(l.c(HUESO, 0.95f), Offset(x0 + ancho * (0.15f + 0.25f * j), t1 - h * 0.01f), Size(ancho * 0.18f, h * 0.035f))
    // El cuerpo.
    val cuerpo = Path().apply {
        moveTo(x0, t1); lineTo(x1, t1); lineTo(x1, y1 - 3f * u)
        quadraticTo(x1, y1, x1 - 3f * u, y1); lineTo(x0 + 3f * u, y1); quadraticTo(x0, y1, x0, y1 - 3f * u); close()
    }
    drawPath(cuerpo, impreso)
    clipPath(cuerpo) {
        val d0 = t1 + h * 0.03f; val d1 = y1 - h * 0.07f
        if (tinte != null) {
            val dibujo = l.animo(if (tinte.luminanciaNeon() > 0.55f) mezcla(tinte, Color(0xFF2A1A30), 0.38f) else mezcla(tinte, Color.White, 0.45f))
            when ((azar(s, 82) * 3).toInt()) {
                0 -> { // Ondas.
                    var y = d0 + h * 0.02f
                    while (y < d1) {
                        val p = Path().apply {
                            moveTo(x0, y)
                            var x = x0
                            val paso = ancho / 2.5f
                            while (x < x1) { quadraticTo(x + paso / 4, y - h * 0.016f, x + paso / 2, y); quadraticTo(x + paso * 3 / 4, y + h * 0.016f, x + paso, y); x += paso }
                        }
                        drawPath(p, dibujo, style = Stroke(1.3f * u))
                        y += h * 0.06f
                    }
                }
                1 -> { // Lunares en tresbolillo.
                    var f = 0; var y = d0 + h * 0.02f
                    while (y < d1) {
                        var x = x0 + (if (f % 2 == 0) ancho * 0.14f else ancho * 0.39f)
                        while (x < x1) { drawCircle(dibujo, ancho * 0.07f, Offset(x, y)); x += ancho * 0.5f }
                        y += h * 0.055f; f++
                    }
                }
                else -> { // Franjas finas a lo alto, más juntas hacia los cantos (el cilindro).
                    listOf(0.06f, 0.2f, 0.4f, 0.6f, 0.8f, 0.94f).forEach { fx -> drawRect(dibujo, Offset(x0 + ancho * fx - ancho * 0.03f, d0), Size(ancho * 0.06f, d1 - d0)) }
                }
            }
            drawRect(l.c(LATON, 1.15f), Offset(x0, d0), Size(ancho, 1.4f * u))
            drawRect(l.c(LATON, 1.15f), Offset(x0, d1), Size(ancho, 1.4f * u))
        } else {
            // La hojalata sin imprimir: las vetas del laminado.
            for (j in 0 until 5) drawLine(l.b(0.18f), Offset(x0, t1 + (y1 - t1) * (0.15f + 0.17f * j)), Offset(x1, t1 + (y1 - t1) * (0.15f + 0.17f * j)), 0.6f * u)
        }
        // El fondo de metal con su canto y el óxido.
        drawRect(metal, Offset(x0, y1 - h * 0.045f), Size(ancho, h * 0.045f))
        drawRect(l.s(0.4f), Offset(x0, y1 - h * 0.045f), Size(ancho, 1f * u))
        for (j in 0 until 4) drawCircle(l.c(Color(0xFF8A4A26)), (0.8f + 1.4f * azar(s, 83 + j)) * u, Offset(x0 + ancho * azar(s, 87 + j), y1 - h * (0.01f + 0.035f * azar(s, 91 + j))))
        // El cilindro: el lado de luz, el de sombra, los dos cantos oscuros y el brillo largo del metal.
        planosNeon(Rect(x0, t1, x1, y1), l.t, 1.1f)
        drawRect(l.s(0.3f), Offset(x0, t1), Size(ancho * 0.07f, y1 - t1))
        drawRect(l.s(0.35f), Offset(x1 - ancho * 0.07f, t1), Size(ancho * 0.07f, y1 - t1))
        drawRect(l.b(0.8f, 1.35f), Offset(if (l.izq) x0 + ancho * 0.18f else x1 - ancho * 0.24f, t1 + h * 0.02f), Size(maxOf(1.4f * u, ancho * 0.055f), (y1 - t1) * 0.86f))
        // Una abolladura.
        val ab = Offset(x0 + ancho * (0.3f + 0.4f * azar(s, 95)), t1 + (y1 - t1) * (0.62f + 0.25f * azar(s, 96)))
        drawArc(l.s(0.45f), 200f, 140f, false, Offset(ab.x - ancho * 0.13f, ab.y - h * 0.02f), Size(ancho * 0.26f, h * 0.04f), style = Stroke(1.3f * u))
        drawArc(l.b(0.45f), 20f, 140f, false, Offset(ab.x - ancho * 0.13f, ab.y - h * 0.02f), Size(ancho * 0.26f, h * 0.04f), style = Stroke(0.9f * u))
        // La luna impresa de los Sueños, abajo.
        if (v.noche) lunaNeon(Offset(x0 + ancho * 0.6f, y1 - h * 0.15f), ancho * 0.15f, impreso, l, Color(0xFFF2EAD8))
    }
    // La tapa, a todo el ancho y un pelo más ancha, con su canto estriado; encima, el pomo.
    val tapa = Rect(x0 - 0.8f * u, t0 - alza, x1 + 0.8f * u, t1 + 0.6f * u - alza)
    drawRect(l.s(0.5f), Offset(x0, t1), Size(ancho, 2f * u))
    drawRoundRect(metalArriba, tapa.topLeft, tapa.size, CornerRadius(1.6f * u))
    clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
        planosNeon(tapa, l.arriba, 1.1f)
        var x = tapa.left + 1.8f * u
        while (x < tapa.right - 1f * u) { drawLine(l.s(0.28f), Offset(x, tapa.top + tapa.height * 0.35f), Offset(x, tapa.bottom - 1f * u), 0.7f * u); x += 2.6f * u }
        drawRect(l.s(0.4f), Offset(tapa.left, tapa.bottom - 1.2f * u), Size(tapa.width, 1.2f * u))
    }
    drawOval(l.arriba.c(mezcla(if (laton) LATON else ALUMINIO, Color.White, 0.25f), 1.2f), Offset(tapa.left, tapa.top - h * 0.012f), Size(tapa.width, h * 0.024f))
    drawOval(l.arriba.b(0.75f, 1.3f), Offset(tapa.left, tapa.top - h * 0.012f), Size(tapa.width, h * 0.024f), style = Stroke(0.8f * u))
    val pomo = Offset(cx, tapa.top - h * 0.01f)
    drawOval(l.s(0.4f), Offset(pomo.x - ancho * 0.1f, pomo.y + 0.6f * u), Size(ancho * 0.24f, h * 0.014f))
    drawOval(l.arriba.c(HIERRO, 1.3f), Offset(pomo.x - ancho * 0.11f, pomo.y - h * 0.014f), Size(ancho * 0.22f, h * 0.022f))
    drawOval(l.arriba.b(0.7f, 1.3f), Offset(pomo.x - ancho * 0.07f, pomo.y - h * 0.013f), Size(ancho * 0.1f, h * 0.008f))
    // La tarjeta del día, atada al pomo con su cordel, cae sobre el cuerpo.
    val tw = (ancho * 0.84f).coerceAtLeast(22f * u).coerceAtMost(w - 2f * u)
    val tx = (cx - tw / 2 + ancho * 0.05f * (azar(s, 97) - 0.5f)).coerceIn(0.5f * u, w - tw - 0.5f * u)
    val ty0 = t1 + h * (0.05f + 0.03f * azar(s, 98)); val ty1 = ty0 + h * 0.29f
    val tarjeta = Rect(tx, ty0, tx + tw, ty1)
    val agujero = Offset(tarjeta.center.x, tarjeta.top + h * 0.022f)
    val cordel = l.c(Color(0xFFC9A86A), 1.1f)
    drawLine(l.s(0.4f), pomo + Offset(1f * u, 1f * u), agujero + Offset(1f * u, 1f * u), 1f * u)
    drawLine(cordel, pomo, Offset(cx - ancho * 0.18f, tapa.top + 1f * u), 0.9f * u)
    drawLine(cordel, Offset(cx - ancho * 0.18f, tapa.top + 1f * u), Offset(x0 + ancho * 0.1f, tapa.bottom), 0.9f * u)
    drawLine(cordel, pomo, agujero, 0.9f * u)
    rotate(if (azar(s, 99) > 0.5f) 2.2f else -2.2f, agujero) {
        val forma = poligonoNeon(tarjeta.left + tw * 0.14f, tarjeta.top, tarjeta.right - tw * 0.14f, tarjeta.top, tarjeta.right, tarjeta.top + tw * 0.14f, tarjeta.right, tarjeta.bottom, tarjeta.left, tarjeta.bottom, tarjeta.left, tarjeta.top + tw * 0.14f)
        drawPath(Path().apply { addPath(forma, Offset(if (l.izq) 1.6f * u else -1.6f * u, 1.8f * u)) }, l.s(0.5f))
        drawPath(forma, l.etiqueta(Color(0xFFD8C8A2)))
        drawCircle(l.s(0.6f), 1.4f * u, agujero)
        drawCircle(l.c(LATON), 1.5f * u, agujero, style = Stroke(0.7f * u))
        drawLine(l.s(0.15f), Offset(tarjeta.left, tarjeta.bottom - 1f * u), Offset(tarjeta.right, tarjeta.bottom - 1f * u), 1f * u)
        fechaNeon(Rect(tarjeta.left, tarjeta.top + h * 0.045f, tarjeta.right, tarjeta.bottom - h * 0.01f), v, medidor, l.a.tinta)
    }
}

/** La luminancia aproximada de un color, para elegir el tono del dibujo. */
private fun Color.luminanciaNeon(): Float = 0.3f * red + 0.59f * green + 0.11f * blue

// ---------------------------------------------------------------- el saco de cordón

/** Una recta, una curva de segundo y una de tercer grado, como función de t (de 0 a 1). */
private fun recta(a: Offset, b: Offset): (Float) -> Offset = { t -> a + (b - a) * t }
private fun curva(a: Offset, c: Offset, b: Offset): (Float) -> Offset = { t -> val m = 1f - t; a * (m * m) + c * (2f * m * t) + b * (t * t) }
private fun curva3(a: Offset, c1: Offset, c2: Offset, b: Offset): (Float) -> Offset =
    { t -> val m = 1f - t; a * (m * m * m) + c1 * (3f * m * m * t) + c2 * (3f * m * t * t) + b * (t * t * t) }

/**
 * Un pespunte a lo largo de [en] (t de 0 a 1): puntadas de unos 1.7 dp con su hueco, en un solo camino de tramos (sin efecto de
 * trazo, que se recalcula en cada dibujo y pesa).
 */
private fun DrawScope.pespunteNeon(color: Color, ancho: Float, en: (Float) -> Offset) {
    val u = density
    var largo = 0f; var antes = en(0f)
    for (i in 1..8) { val p = en(i / 8f); largo += (p - antes).getDistance(); antes = p }
    val n = (largo / (3f * u)).toInt().coerceIn(1, 60)
    val camino = Path()
    for (i in 0 until n) {
        val a = en(i / n.toFloat()); val b = en((i + 0.57f) / n)
        camino.moveTo(a.x, a.y); camino.lineTo(b.x, b.y)
    }
    drawPath(camino, color, style = Stroke(ancho))
}

/**
 * El saco de cordón del repartidor, de tela técnica (ripstop) del ánimo, blando y sentado: la boca fruncida, corta, con el
 * sobrante de tela doblado y caído hacia un lado (no se abre en abanico) y su dobladillo cosido; la jareta del cuello con sus
 * pespuntes y su ojal de metal; el cordón que sale del ojal, entra en la tanca de plástico y cae en un solo cabo torcido con su
 * nudo, sin lazo; el cuerpo que se abre en hombros y se asienta (más panzudo cuanto más escribiste), con la cuadrícula del ripstop, la
 * costura del costado pespunteada y la arruga de asiento; el refuerzo de la base, de otra tela más oscura, con su costura; la
 * cinta reflectante a lo ancho; y la tira de cinta de carrocero con el día, en la panza. En los Sueños, una media luna. Alguno,
 * recostado.
 */
private fun DrawScope.sacoCordonNeon(v: Volumen, medidor: TextMeasurer, l: LuzVolumenNeon, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val x0 = (0.8f + 1.2f * azar(s, 77)) * u; val x1 = w - (1.4f + 1.6f * azar(s, 78)) * u
    val ancho = x1 - x0
    val cx = (x0 + x1) / 2
    val y1 = h - 1f * u
    val panza = ancho * (0.02f + 0.05f * v.lleno)
    val lado = if (azar(s, 76) > 0.5f) 1f else -1f
    val giro = if (azar(s, 79) > 0.7f) (if (l.izq) 2.5f else -2.5f) else 0f
    val tela = if (tinte != null) l.animo(tinte) else l.c(Color(0xFF5C5A4A))
    val telaLuz = if (tinte != null) l.animo(mezcla(tinte, Color.White, 0.3f), 1.15f) else l.c(Color(0xFF7A7862))
    val telaOsc = if (tinte != null) l.animo(mezcla(tinte, Color(0xFF14101C), 0.5f), 0.8f) else l.c(Color(0xFF34322A))
    val telaArriba = if (tinte != null) mezcla(tela, l.arriba.c(tinte), 0.3f) else l.arriba.c(Color(0xFF66644F))
    val refuerzo = if (tinte != null) l.animo(mezcla(tinte, Color(0xFF16121C), 0.62f), 0.8f) else l.c(Color(0xFF26252A))
    val hilo = l.c(Color(0xFFD8D0BC), 0.9f)
    val cuello = h * (0.165f + 0.02f * azar(s, 81))
    val cuelloAncho = ancho * 0.46f
    val hombro = cuello + h * 0.1f
    asientoNeon(l, x0, x1, cuello)
    rotate(giro, Offset(cx, y1)) {
        // ---- el cuerpo: del cuello se abre en hombros, baja casi recto y se asienta, más ancho y aplastado abajo.
        val cuerpo = Path().apply {
            moveTo(cx - cuelloAncho / 2, cuello)
            cubicTo(x0 + ancho * 0.1f, cuello + h * 0.012f, x0 + ancho * 0.02f, hombro - h * 0.04f, x0 + ancho * 0.015f, hombro + h * 0.02f)
            cubicTo(x0 - panza * 0.3f, h * 0.55f, x0 - panza, y1 - h * 0.16f, x0 - panza, y1 - h * 0.06f)
            quadraticTo(x0 - panza, y1, x0 + ancho * 0.12f, y1)
            lineTo(x1 - ancho * 0.12f, y1)
            quadraticTo(x1 + panza, y1, x1 + panza, y1 - h * 0.06f)
            cubicTo(x1 + panza, y1 - h * 0.16f, x1 + panza * 0.3f, h * 0.55f, x1 - ancho * 0.015f, hombro + h * 0.02f)
            cubicTo(x1 - ancho * 0.02f, hombro - h * 0.04f, x1 - ancho * 0.1f, cuello + h * 0.012f, cx + cuelloAncho / 2, cuello)
            close()
        }
        drawPath(cuerpo, tela)
        clipPath(cuerpo) {
            // El lado de sombra y el de luz.
            drawRect(telaOsc, Offset(if (l.izq) cx + ancho * 0.18f else x0 - panza, cuello), Size(ancho * 0.34f + panza, y1 - cuello))
            drawRect(l.s(0.22f), Offset(if (l.izq) x1 - ancho * 0.12f else x0 - panza, cuello), Size(ancho * 0.12f + panza, y1 - cuello))
            drawOval(telaLuz, Offset(if (l.izq) x0 + ancho * 0.06f else x1 - ancho * 0.34f, h * 0.36f), Size(ancho * 0.28f, h * 0.44f))
            // La cuadrícula del ripstop, apenas.
            val rejilla = Path()
            var gx = x0 + 2f * u
            while (gx < x1) { rejilla.moveTo(gx, cuello); rejilla.lineTo(gx, y1); gx += 5.6f * u }
            var gy = cuello + 2f * u
            while (gy < y1) { rejilla.moveTo(x0 - panza, gy); rejilla.lineTo(x1 + panza, gy); gy += 5.6f * u }
            drawPath(rejilla, l.s(0.12f), style = Stroke(0.5f * u))
            // Los frunces cortos que bajan del cuello a los hombros.
            for (j in 0 until 4) {
                val f = (j + 0.5f) / 4f
                val xa = cx - cuelloAncho / 2 + cuelloAncho * f
                val xb = x0 + ancho * (0.1f + 0.8f * f)
                val p = Path().apply { moveTo(xa, cuello + 1f * u); quadraticTo((xa + xb) / 2, cuello + h * 0.04f, xb, hombro + h * 0.01f * azar(s, 83 + j)) }
                drawPath(p, l.s(0.4f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(p, Offset(if (l.izq) -1.1f * u else 1.1f * u, 0f)) }, l.b(0.35f, 1.15f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            }
            // La costura del costado (del lado de la sombra): el pliegue y el pespunte.
            val xc = if (l.izq) x1 - ancho * 0.14f else x0 + ancho * 0.14f
            val dc = (if (l.izq) 1f else -1f) * panza * 0.6f
            drawLine(l.s(0.38f), Offset(xc, hombro), Offset(xc + dc, y1 - h * 0.12f), 1.1f * u)
            pespunteNeon(hilo.copy(alpha = 0.55f), 0.6f * u, recta(Offset(xc + 1.3f * u, hombro + 1f * u), Offset(xc + 1.3f * u + dc, y1 - h * 0.12f)))
            // La arruga de asiento, sobre el refuerzo.
            val ya = y1 - h * (0.16f + 0.03f * azar(s, 88))
            val ar = Path().apply { moveTo(x0 + ancho * 0.12f, ya); quadraticTo(cx, ya + h * 0.025f, x1 - ancho * 0.14f, ya - h * 0.01f) }
            drawPath(ar, l.s(0.38f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(ar, Offset(0f, -1.2f * u)) }, l.b(0.28f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            // El brillo de la tela.
            drawRect(l.b(0.6f, 1.3f), Offset(if (l.izq) x0 + ancho * 0.12f else x1 - ancho * 0.17f, h * 0.44f), Size(maxOf(1.3f * u, ancho * 0.045f), h * 0.22f))
            // La cinta reflectante, a lo ancho: el gris plata y, en su canto, la luz que devuelve.
            val c0 = h * (0.745f + 0.015f * azar(s, 89)); val c1 = c0 + h * 0.038f
            drawRect(l.c(Color(0xFFA6ACB4), 1.15f), Offset(x0 - panza, c0), Size(ancho + panza * 2, c1 - c0))
            drawRect(l.b(0.85f, 1.5f), Offset(x0 - panza, c0 + (c1 - c0) * 0.3f), Size(ancho + panza * 2, maxOf(1f, (c1 - c0) * 0.22f)))
            drawRect(l.s(0.4f), Offset(x0 - panza, c1), Size(ancho + panza * 2, 1f * u))
            // El refuerzo de la base, de otra tela, que sube un poco por los lados, con su costura.
            val yr = y1 - h * 0.1f
            val base = Path().apply {
                moveTo(x0 - panza * 1.2f, yr - h * 0.02f); quadraticTo(cx, yr + h * 0.012f, x1 + panza * 1.2f, yr - h * 0.02f)
                lineTo(x1 + panza * 1.2f, y1 + 1f * u); lineTo(x0 - panza * 1.2f, y1 + 1f * u); close()
            }
            drawPath(base, refuerzo)
            val costura = Path().apply { moveTo(x0 - panza, yr - h * 0.012f); quadraticTo(cx, yr + h * 0.02f, x1 + panza, yr - h * 0.012f) }
            drawPath(costura, l.s(0.45f), style = Stroke(1f * u))
            pespunteNeon(hilo.copy(alpha = 0.6f), 0.6f * u, curva(Offset(x0 - panza, yr - h * 0.012f + 1.8f * u), Offset(cx, yr + h * 0.02f + 1.8f * u), Offset(x1 + panza, yr - h * 0.012f + 1.8f * u)))
            if (v.noche) lunaNeon(Offset(cx + lado * ancho * 0.2f, h * 0.35f), ancho * 0.12f, tela, l)
        }
        drawPath(cuerpo, l.b(0.28f), style = Stroke(0.7f * u))
        // ---- la jareta del cuello, con su frunce y sus dos pespuntes.
        val jar = Rect(cx - cuelloAncho * 0.6f, cuello - h * 0.012f, cx + cuelloAncho * 0.6f, cuello + h * 0.022f)
        drawRoundRect(l.s(0.45f), jar.topLeft + Offset(0f, 1.2f * u), jar.size, CornerRadius(2f * u))
        drawRoundRect(mezcla(telaOsc, tela, 0.5f), jar.topLeft, jar.size, CornerRadius(2f * u))
        clipRect(jar.left, jar.top, jar.right, jar.bottom) {
            var fx = jar.left + 1.5f * u
            while (fx < jar.right) { drawLine(l.s(0.35f), Offset(fx, jar.top), Offset(fx + 0.6f * u, jar.bottom), 0.8f * u); fx += 2.6f * u }
            listOf(jar.top + 1.2f * u, jar.bottom - 1.4f * u).forEach { yy ->
                pespunteNeon(hilo.copy(alpha = 0.5f), 0.55f * u, recta(Offset(jar.left + 1f * u, yy), Offset(jar.right - 1f * u, yy)))
            }
        }
        drawRect(l.arriba.b(0.45f, 1.2f), Offset(jar.left + 1.5f * u, jar.top), Size(jar.width - 3f * u, 0.8f * u))
        // ---- la boca: el sobrante de tela sobre el frunce, corto; sube un poco de un lado y se dobla y cae del otro ([lado]) por
        // encima de la jareta, hasta el hombro, como tela blanda. Los pliegues siguen la caída; el dobladillo, cosido.
        val tope = cuello - h * (0.06f + 0.015f * azar(s, 82))
        val a = cx - lado * cuelloAncho * 0.44f; val b = cx + lado * cuelloAncho * 0.3f
        val cima = Offset(cx - lado * ancho * 0.04f, tope)
        val punta = Offset(cx + lado * ancho * 0.47f, cuello + h * 0.045f)
        val labio = Path().apply {
            moveTo(cima.x, cima.y)
            cubicTo(cx + lado * ancho * 0.2f, tope, cx + lado * ancho * 0.43f, cuello - h * 0.03f, punta.x, punta.y)
        }
        val boca = Path().apply {
            moveTo(a, cuello + 1f * u)
            cubicTo(a - lado * ancho * 0.03f, cuello - h * 0.03f, cx - lado * ancho * 0.18f, tope + h * 0.004f, cima.x, cima.y)
            cubicTo(cx + lado * ancho * 0.2f, tope, cx + lado * ancho * 0.43f, cuello - h * 0.03f, punta.x, punta.y)
            quadraticTo(punta.x - lado * ancho * 0.02f, punta.y + h * 0.014f, punta.x - lado * ancho * 0.08f, punta.y + h * 0.006f)
            cubicTo(cx + lado * ancho * 0.32f, cuello + h * 0.018f, b + lado * ancho * 0.04f, cuello + h * 0.004f, b, cuello)
            close()
        }
        // La sombra que echa lo caído en el hombro.
        drawOval(l.s(0.4f), Offset(cx + lado * ancho * 0.3f - ancho * 0.19f, cuello + h * 0.035f), Size(ancho * 0.38f, h * 0.04f))
        drawPath(boca, telaArriba)
        clipPath(boca) {
            // Lo de debajo de la caída, en sombra, y dos pliegues blandos que siguen el doblez.
            drawRect(l.s(0.34f), Offset(if (lado > 0f) cx + ancho * 0.18f else cx - ancho * 0.5f, cuello - h * 0.005f), Size(ancho * 0.32f, h * 0.06f))
            listOf(0.022f to 0.4f, 0.045f to 0.3f).forEach { (dy, al) ->
                val pl = Path().apply { addPath(labio, Offset(-lado * ancho * 0.07f, h * dy)) }
                drawPath(pl, l.s(al), style = Stroke(1.1f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(pl, Offset(-lado * 1f * u, -1f * u)) }, l.arriba.b(0.3f, 1.15f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
            }
            val dl = Offset(-lado * 0.6f * u, 1.8f * u)
            pespunteNeon(hilo.copy(alpha = 0.5f), 0.6f * u, curva3(cima + dl, Offset(cx + lado * ancho * 0.2f, tope) + dl, Offset(cx + lado * ancho * 0.43f, cuello - h * 0.03f) + dl, punta + dl))
        }
        drawPath(labio, l.arriba.b(0.6f, 1.2f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // ---- el ojal de metal por donde sale el cordón, del lado que no tapa la caída.
        val ojal = Offset(cx - lado * ancho * 0.1f, jar.center.y)
        drawCircle(Color(0xFF0E0C12), 1.5f * u, ojal)
        drawCircle(l.c(ALUMINIO, 1.2f), 1.5f * u, ojal, style = Stroke(0.8f * u))
        // ---- el cordón: sale del ojal, entra enseguida en la tanca (apretada contra la jareta, ladeada) y sigue en un solo cabo de
        // dos hebras torcidas que cae en diagonal hacia fuera y acaba en un nudo con el pelo deshilachado. Sin lazo ni dos patas.
        val cord = l.arriba.c(Color(0xFF1E1C24), 1.2f)
        val tw = maxOf(5f * u, ancho * 0.15f); val th = maxOf(3.4f * u, h * 0.03f)
        val tc = Offset(ojal.x - lado * ancho * 0.07f, ojal.y + h * 0.028f)
        val angulo = -lado * 28f
        drawLine(l.s(0.4f), ojal + Offset(0.8f * u, 0.8f * u), tc + Offset(0.8f * u, 0.8f * u), 1.6f * u)
        drawLine(cord, ojal, tc, 1.4f * u, StrokeCap.Round)
        val salida = Offset(tc.x - lado * tw * 0.45f, tc.y + tw * 0.25f)
        val nudo = Offset(salida.x - lado * ancho * 0.12f, salida.y + h * 0.075f)
        val cabo = Path().apply { moveTo(salida.x, salida.y); quadraticTo(salida.x - lado * ancho * 0.02f, (salida.y + nudo.y) / 2, nudo.x, nudo.y) }
        drawPath(Path().apply { addPath(cabo, Offset(0.9f * u, 0.9f * u)) }, l.s(0.4f), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawPath(cabo, cord, style = Stroke(1.8f * u, cap = StrokeCap.Round))
        // La torsión de las dos hebras: rayitas oblicuas a lo largo del cabo.
        for (j in 1 until 5) {
            val f = j / 5f
            val p = Offset(salida.x + (nudo.x - salida.x) * f - lado * ancho * 0.01f * (1f - f), salida.y + (nudo.y - salida.y) * f)
            drawLine(l.arriba.b(0.45f, 1.2f), Offset(p.x - 0.7f * u, p.y - 0.5f * u), Offset(p.x + 0.7f * u, p.y + 0.5f * u), 0.5f * u)
        }
        drawCircle(l.s(0.4f), 1.9f * u, nudo + Offset(0.7f * u, 0.7f * u))
        drawCircle(cord, 1.9f * u, nudo)
        drawCircle(l.arriba.b(0.5f, 1.2f), 0.7f * u, nudo + Offset(-0.6f * u, -0.6f * u))
        drawLine(cord, nudo, nudo + Offset(-lado * 1.2f * u, 2.4f * u), 0.7f * u)
        drawLine(cord, nudo, nudo + Offset(lado * 0.4f * u, 2.6f * u), 0.7f * u)
        // La tanca: el barril de plástico ladeado, su botón de muelle y la luz en el canto.
        rotate(angulo, tc) {
            val tanca = Rect(tc.x - tw / 2, tc.y - th / 2, tc.x + tw / 2, tc.y + th / 2)
            drawRoundRect(l.s(0.45f), tanca.topLeft + Offset(1f * u, 1.2f * u), tanca.size, CornerRadius(th * 0.45f))
            drawRoundRect(l.arriba.c(Color(0xFF2A2830), 1.3f), tanca.topLeft, tanca.size, CornerRadius(th * 0.45f))
            drawRoundRect(l.arriba.c(Color(0xFF55535E), 1.3f), Offset(tanca.left + tw * 0.62f, tanca.top + th * 0.18f), Size(tw * 0.3f, th * 0.64f), CornerRadius(0.8f * u))
            drawRect(l.arriba.b(0.6f, 1.3f), Offset(tanca.left + th * 0.4f, tanca.top + 0.5f * u), Size(tw - th * 0.8f, 0.7f * u))
        }
        // ---- la tira de cinta de carrocero con el día, en la panza, un poco torcida, con los cabos rasgados.
        val ty0 = h * (0.43f + 0.035f * azar(s, 93)); val ty1 = ty0 + h * 0.27f
        val cw = minOf(w - 1f * u, maxOf(ancho * 0.84f, 22f * u))
        val tx = (cx - cw / 2).coerceIn(0.5f * u, w - cw - 0.5f * u)
        val cinta = Rect(tx, ty0, tx + cw, ty1)
        rotate(if (azar(s, 94) > 0.5f) 3f else -3f, cinta.center) {
            val dientes = 5
            val forma = Path().apply {
                moveTo(cinta.left, cinta.top)
                for (k in 1..dientes) lineTo(cinta.left + cinta.width * k / dientes, cinta.top + (if (k % 2 == 0) 0f else 1.2f * u))
                lineTo(cinta.right, cinta.bottom)
                for (k in dientes - 1 downTo 0) lineTo(cinta.left + cinta.width * k / dientes, cinta.bottom - (if (k % 2 == 0) 0f else 1.2f * u))
                close()
            }
            drawPath(Path().apply { addPath(forma, Offset(if (l.izq) 1.2f * u else -1.2f * u, 1.4f * u)) }, l.s(0.45f))
            drawPath(forma, l.etiqueta(Color(0xFFDCCB98)))
            drawRect(l.b(0.2f), Offset(cinta.left, cinta.top + cinta.height * 0.1f), Size(cinta.width, cinta.height * 0.12f))
            fechaNeon(Rect(cinta.left, cinta.top + cinta.height * 0.05f, cinta.right, cinta.bottom - cinta.height * 0.02f), v, medidor, l.a.tinta)
        }
    }
}
