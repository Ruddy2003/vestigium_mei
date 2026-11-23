package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.Temas
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.roundToInt

// Las entradas de la alacena del lado de la dueña (MuebleEstanteMongol): el rollo de fieltro y la bolsa de fieltro (la pila de
// cofres está en MuebleEstanteMongolCofres.kt), todos teñidos del color del ánimo y con su tablilla de corteza de abedul, donde
// van el día y el mes corto.

/**
 * La pelusa del fieltro, pintada una sola vez (y otra si cambia la densidad) y repetida como pincel: fibras cortas claras y
 * oscuras. Así la textura de cientos de fibras es una sola operación por pieza.
 */
internal object FieltroMongol {
    private var densidad = 0f
    private var pincel: ShaderBrush? = null
    fun pincel(u: Float): ShaderBrush {
        val p = pincel
        if (p != null && densidad == u) return p
        val lado = (24f * u).roundToInt().coerceAtLeast(8)
        val l = lado.toFloat()
        val img = ImageBitmap(lado, lado)
        val r = java.util.Random(41)
        val fibras = List(80) { Triple(Offset(r.nextFloat() * l, r.nextFloat() * l), (r.nextFloat() - 0.5f) * 3.1f, r.nextFloat()) }
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(l, l)) {
            for (dx in -1..1) for (dy in -1..1) {
                val o = Offset(dx * l, dy * l)
                for ((c, ang, t) in fibras) {
                    val largo = (1.4f + 2.4f * t) * u
                    val d = Offset(kotlin.math.cos(ang) * largo, kotlin.math.sin(ang) * largo)
                    val col = if (t < 0.5f) Color(0xFFFFF6E4).copy(alpha = 0.24f) else Color(0xFF1C1014).copy(alpha = 0.22f)
                    drawLine(col, o + c, o + c + d, (0.45f * u).coerceAtLeast(0.6f), StrokeCap.Round)
                }
            }
        }
        return ShaderBrush(ImageShader(img, TileMode.Repeated, TileMode.Repeated)).also { pincel = it; densidad = u }
    }
}

/**
 * La pelusa del fieltro sobre [forma]: de día entera; de noche, según la luz que le llega. Donde la noche la dejaría casi
 * invisible (lejos del fuego y de la lámpara), no se pinta: es una pasada entera de menos por entrada.
 */
internal fun DrawScope.pelusaMongol(forma: Path, l: LuzMongol) {
    val alfa = if (l.a.oscuro) 0.2f + 0.55f * l.k + 0.3f * l.f else 0.85f
    if (alfa >= 0.3f) drawPath(forma, FieltroMongol.pincel(density), alpha = alfa.coerceAtMost(0.85f))
}

/** Los meses cortos en español, para la tablilla (el nombre largo del tema ya lo dice el rótulo de la repisa). */
private val MESES_CORTOS_MONGOL = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

/** El mes corto de una entrada: se ubica el nombre del tema entre los meses mongoles; si no está, queda tal cual. */
private fun mesCortoMongol(mes: String): String {
    val i = Temas.MESES_MONGOLES.indexOf(mes)
    return if (i in MESES_CORTOS_MONGOL.indices) MESES_CORTOS_MONGOL[i] else mes
}

internal fun DrawScope.volumenMongol(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val m = MuebleEstanteMongol
    val a = AireMongol(oscuro, v.noche)
    val fila = m.FILA / m.ANCHO_NOMINAL
    val x = m.LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * m.LADO)
    val y = m.CORONA + v.repisa * fila + (m.ALTO_REPISA.value / m.ANCHO_NOMINAL) * 0.55f
    val total = m.CORONA + v.repisas * fila + m.BASE
    val l = m.luz(a, x, y, total)
    // De dónde le llega la luz: de día, de la puerta (izquierda); de noche, del hogar y de la lámpara (a la derecha).
    val hacia = if (a.oscuro && x < m.HOGAR_X + 0.05f) 1f else -1f
    val tono = tonoDeDiaMongol(v.tono, oscuro)
    when (m.clase(v.semilla)) {
        0 -> rolloMongol(v, tono, medidor, l, hacia)
        1 -> cofresMongol(v, tono, medidor, l, hacia)
        else -> bolsaMongol(v, tono, medidor, l, hacia)
    }
}

// ---------------------------------------------------------------- ayudantes (también los usa la pila de cofres)

/** El fieltro del ánimo en bandas, para un cuerpo redondo: el plano de luz del lado de [hacia], el tono y la sombra del otro. */
internal fun bandasMongol(base: Color, l: LuzMongol, hacia: Float): List<Color> {
    val a = l.a
    val claro = when {
        a.oscuro -> mezcla(base, a.fuego, 0.22f * l.k + 0.04f)
        a.noche -> mezcla(base, P.CREPUSCULO, 0.16f + 0.1f * l.sol)
        else -> mezcla(base, if (l.sol > 0.2f) P.SOL else LUZ_MUEBLE, 0.2f + 0.1f * l.sol)
    }
    val sombra = mezcla(base, if (a.oscuro) a.hondo else SOMBRA_MUEBLE, if (a.oscuro) 0.52f else 0.32f)
    val honda = mezcla(base, if (a.oscuro) a.hondo else SOMBRA_MUEBLE, if (a.oscuro) 0.72f else 0.5f)
    val izqADer = listOf(mezcla(claro, base, 0.4f), claro, base, base, sombra, honda)
    return if (hacia < 0f) izqADer else izqADer.asReversed()
}

/** La luminancia aproximada de un color. */
private fun lumMongol(c: Color) = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

/** El hilo que contrasta con el fieltro [base]: crudo sobre lo oscuro, pardo sobre lo claro. */
internal fun hiloMongol(base: Color, l: LuzMongol): Color =
    l.c(if (lumMongol(base) > 0.5f) mezcla(P.CUERO, P.HOLLIN, 0.35f) else mezcla(P.CREMA, P.ORO, 0.15f))

/** El fieltro que contrasta con [base] para las aplicaciones: añil sobre lo claro, crudo sobre lo rojo, oro viejo sobre el añil. */
internal fun aplicacionMongol(base: Color): Color =
    if (lumMongol(base) > 0.5f) P.ANIL else if (base.red > base.blue + 0.15f) P.CREMA else mezcla(P.CREMA, P.ORO, 0.35f)

/** El fieltro del ánimo sin luz (para escoger el de la aplicación); sin ánimo, el pardo sin teñir. */
internal fun fieltroSeguroMongol(t: Color?): Color = if (t == null) P.SIN_ANIMO else fieltroDelAnimo(t)

/**
 * La sombra que la entrada echa detrás, en el fondo del casillero y en la vecina (a la derecha de día; de noche, a la izquierda
 * y arriba, lejos del hogar y de la lámpara): una sola capa, honda.
 */
internal fun DrawScope.sombraDetrasMongol(forma: Path, l: LuzMongol, hacia: Float) {
    val u = density
    val dy = if (l.a.oscuro) -3.6f * u else 2f * u
    drawPath(corridoMongol(forma, -hacia * 6.5f * u, dy), l.s(if (l.a.oscuro) 0.5f else 0.42f))
}

/**
 * La misma sombra para un cuerpo recto (de [x0] a [x1] y de [top] a [pie]): solo la franja que asoma, del lado contrario a la
 * luz y, de noche, por arriba (redonda, con el semieje [ry], si el cuerpo es un cilindro). Pinta mucho menos que la forma
 * entera corrida, que el cuerpo tapa casi toda.
 */
internal fun DrawScope.sombraDetrasRectaMongol(x0: Float, x1: Float, top: Float, pie: Float, l: LuzMongol, hacia: Float, ry: Float = 0f) {
    val u = density
    val dx = -hacia * 6.5f * u
    val dy = if (l.a.oscuro) -3.6f * u else 2f * u
    val c = l.s(if (l.a.oscuro) 0.5f else 0.42f)
    if (dx > 0f) drawRect(c, Offset(x1, top + dy), Size(dx, pie - top)) else drawRect(c, Offset(x0 + dx, top + dy), Size(-dx, pie - top))
    if (dy < 0f) {
        if (ry > 0f) drawOval(c, Offset(x0 + dx, top - ry + dy), Size(x1 - x0, 2f * ry))
        else drawRect(c, Offset(x0 + dx, top + dy), Size(x1 - x0, -dy))
    }
}

/** El contacto con la tabla: la sombra honda justo debajo y la que se tiende hacia el lado contrario a la luz. */
internal fun DrawScope.contactoEntradaMongol(l: LuzMongol, hacia: Float, x0: Float, x1: Float) {
    val u = density
    val h = size.height
    val largo = (x1 - x0)
    drawOval(l.s(0.42f), Offset(x0 - hacia * largo * 0.22f, h - 2.8f * u), Size(largo * 1.15f, 5.2f * u))
    drawRect(l.s(0.65f), Offset(x0 + 1f * u, h - 1.3f * u), Size(largo - 2f * u, 1.5f * u))
}

/**
 * La sombra que echa la vecina del lado de la luz sobre esta entrada (dentro del recorte que haya): una franja en ese canto,
 * más honda abajo, donde las dos se juntan.
 */
private fun DrawScope.sombraVecinaMongol(l: LuzMongol, hacia: Float, x0: Float, x1: Float) {
    val w = x1 - x0; val h = size.height
    val k = if (l.a.oscuro) 0.75f else 1f
    drawRect(l.s(0.2f * k), Offset(if (hacia < 0f) x0 else x1 - w * 0.22f, h * 0.32f), Size(w * 0.22f, h * 0.68f))
    drawRect(l.s(0.16f * k), Offset(if (hacia < 0f) x0 else x1 - w * 0.1f, h * 0.56f), Size(w * 0.1f, h * 0.44f))
}

/** Dientes de lobo: una hilera de triángulos de [alto] entre [x0] y [x1], con la base en [y]. Ningún signo: solo triángulos. */
internal fun dientesMongol(x0: Float, x1: Float, y: Float, alto: Float, paso: Float): Path = Path().apply {
    var x = x0
    while (x + paso <= x1) { moveTo(x, y); lineTo(x + paso / 2, y - alto); lineTo(x + paso, y); close(); x += paso * 1.3f }
}

/**
 * Dónde va la tablilla: centrada en [cx] o, si no se da, a lo ancho en un sitio distinto según la semilla (en las angostas asoma
 * un poco por fuera); su alto, con el día, el mes corto y, en los Sueños, la luna.
 */
internal fun DrawScope.rectTablillaMongol(top: Float, v: Volumen, cx: Float = Float.NaN): Rect {
    val u = density
    val w = size.width
    val tw = 20f * u
    val th = (if (v.noche) 35.5f else 29.5f) * u
    val centro = if (cx.isNaN()) w * (0.38f + 0.24f * azar(v.semilla, 95)) else cx
    val izq = (centro - tw / 2).coerceIn(-3f * u, w - tw + 3f * u)
    return Rect(izq, top, izq + tw, top + th)
}

/**
 * La tablilla de corteza de abedul vieja (tostada, con sus lenticelas y el canto quemado) con el día y, debajo, el mes corto;
 * colgada de su lazo de crin desde [nudo] y ladeada según la semilla, con el borde irregular, la punta que se enrolla y su
 * sombra en la entrada. De noche se tiñe con la luz que le toca. En los Sueños, una media luna de añil bajo el mes.
 */
internal fun DrawScope.tablillaMongol(v: Volumen, medidor: TextMeasurer, l: LuzMongol, r: Rect, nudo: Offset, hacia: Float) {
    val u = density
    val giro = (azar(v.semilla, 53) - 0.5f) * 20f
    val ag = Offset(r.center.x, r.top + 2.2f * u)
    rotate(giro, nudo) {
        drawLine(l.s(0.35f), nudo + Offset(-hacia * 0.8f * u, 0.8f * u), ag + Offset(-hacia * 0.8f * u, 0.8f * u), 1.1f * u)
        drawLine(l.c(P.CRIN), nudo, Offset(ag.x - 0.9f * u, ag.y), 1f * u)
        drawLine(l.c(P.CRIN), nudo, Offset(ag.x + 0.9f * u, ag.y), 1f * u)
        val forma = contornoIrregular(r, v.semilla + 31, 14, 0.03f)
        drawPath(corridoMongol(forma, -hacia * 2.2f * u, (if (l.a.oscuro) -1f else 2.4f) * u), l.s(0.55f))
        val corteza = l.etiqueta()
        drawPath(forma, corteza)
        // La corteza vieja: el velo tostado desigual, las lenticelas en los cantos (lejos del número) y el canto de abajo quemado;
        // todo metido un poco del borde irregular (así no hace falta recortar).
        val m = 1.1f * u
        val dentro = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        drawOval(mezcla(corteza, P.ABEDUL_OSC, 0.6f).copy(alpha = 0.45f), Offset(dentro.left + dentro.width * (0.1f + 0.25f * azar(v.semilla, 54)), dentro.top), Size(dentro.width * 0.65f, r.height * 0.2f))
        val lenticela = l.c(P.LENTICELA).copy(alpha = 0.62f)
        for (q in 0 until 6) {
            val izq = q % 2 == 0
            val yy = r.top + r.height * (0.12f + 0.14f * q + 0.06f * azar(v.semilla, 40 + q))
            val largo = r.width * (0.12f + 0.1f * azar(v.semilla, 48 + q))
            val xx = if (izq) dentro.left else dentro.right - largo
            drawLine(lenticela, Offset(xx, yy), Offset(xx + largo, yy), 0.9f * u)
        }
        drawRect(l.c(P.ABEDUL_OSC).copy(alpha = 0.75f), Offset(dentro.left, dentro.bottom - 1.6f * u), Size(dentro.width, 1.6f * u))
        if (!l.a.oscuro) drawRect(l.b(0.35f), dentro.topLeft, Size(1.1f * u, dentro.height))
        else drawRect(l.filo(0.6f), Offset(if (hacia > 0) dentro.right - 1.1f * u else dentro.left, dentro.top), Size(1.1f * u, dentro.height))
        val e = if (azar(v.semilla, 52) < 0.5f) r.right else r.left
        val s = if (e == r.right) -1f else 1f
        drawPath(poligonoMongol(e, r.bottom - 4.5f * u, e + s * 4f * u, r.bottom, e, r.bottom), mezcla(corteza, P.ABEDUL_OSC, 0.75f))
        drawLine(l.s(0.45f), Offset(e, r.bottom - 4.5f * u), Offset(e + s * 4f * u, r.bottom), 0.7f * u)
        // El único agujero, con el lazo que pasa por él.
        drawCircle(l.c(P.HOLLIN), 1.1f * u, ag)
        drawLine(l.c(P.CRIN), Offset(ag.x, ag.y - 0.4f * u), Offset(ag.x, r.top - 0.4f * u), 1.2f * u)
        // El día, el mes corto debajo y, en los Sueños, la media luna.
        val tinta = if (l.a.oscuro) Color(0xFF0E0805) else P.TINTA
        val zonaDia = Rect(r.left + 0.3f * u, r.top + 3f * u, r.right - 0.3f * u, r.top + 19.5f * u)
        textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, peso = FontWeight.Bold, minimo = 9f)
        val zonaMes = Rect(r.left + 0.5f * u, zonaDia.bottom - 0.6f * u, r.right - 0.5f * u, zonaDia.bottom + 8.4f * u)
        textoEn(medidor, mesCortoMongol(v.mes), zonaMes, P.LETRA, tinta, 8.5f, peso = FontWeight.Bold, minimo = 6f)
        if (v.noche) {
            val c = Offset(r.center.x, zonaMes.bottom + 3.4f * u)
            val rl = 2.9f * u
            drawCircle(l.c(P.ANIL), rl, c)
            drawCircle(corteza, rl * 0.84f, c + Offset(rl * 0.42f, -rl * 0.24f))
        }
        drawPath(forma, l.c(mezcla(P.ABEDUL_OSC, P.HOLLIN, 0.3f)).copy(alpha = 0.7f), style = Stroke(1f * u))
    }
}

// ---------------------------------------------------------------- el rollo de fieltro

/**
 * El rollo de fieltro, de pie (más grueso o más delgado según la semilla; alguno, ladeado contra el vecino de la izquierda):
 * el cilindro blando teñido del ánimo con la luz de un lado y la sombra del otro y su pelusa; arriba, la cara del rollo con la
 * espiral de sus vueltas; la orla de dientes de lobo del canto y las hileras de pespunte del acolchado; el borde de la última
 * vuelta; dos ataduras de crin torcida; la tablilla colgada del nudo de arriba, a una altura distinta en cada rollo.
 */
private fun DrawScope.rolloMongol(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzMongol, hacia: Float) {
    val w = size.width; val h = size.height
    val ladea = v.centro > 0.12f && h > w * 2f && azar(v.semilla, 92) < 0.32f
    val ladeo = if (ladea) -(5f + 3f * azar(v.semilla, 93)) else 0f
    if (ladeo == 0f) rolloCuerpo(v, tono, medidor, l, hacia, w, h)
    else rotate(ladeo, Offset(w * 0.15f, h)) { rolloCuerpo(v, tono, medidor, l, hacia, w, h) }
}

private fun DrawScope.rolloCuerpo(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzMongol, hacia: Float, w: Float, h: Float) {
    val u = density
    val base = l.animo(tono)
    // El rollo usa una parte del ancho que le toca (más grueso o más delgado), corrido a un lado o al otro.
    val grueso = 0.64f + 0.34f * azar(v.semilla, 91)
    val xl = w * (1f - grueso) * (0.2f + 0.6f * azar(v.semilla, 90)); val xr = xl + w * grueso
    val xm = (xl + xr) / 2
    val ry = minOf((xr - xl) * 0.17f, 5.5f * u)
    val top = ry + 0.5f * u
    val bulto = w * 0.015f
    val cuerpo = Path().apply {
        moveTo(xl, top)
        cubicTo(xl - bulto, top + (h - top) * 0.4f, xl - bulto, h - ry * 1.4f, xl + w * 0.02f, h - ry * 0.5f)
        quadraticTo(xm, h + ry * 0.35f, xr - w * 0.02f, h - ry * 0.5f)
        cubicTo(xr + bulto, h - ry * 1.4f, xr + bulto, top + (h - top) * 0.4f, xr, top)
        close()
    }
    sombraDetrasRectaMongol(xl, xr, top, h, l, hacia, ry)
    contactoEntradaMongol(l, hacia, xl, xr)
    drawPath(cuerpo, Brush.horizontalGradient(bandasMongol(base, l, hacia), xl, xr))
    pelusaMongol(cuerpo, l)
    val hilo = hiloMongol(base, l)
    val pe = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.8f * u), 0f)
    clipRect(xl, top, xr, h) {
        // La orla del canto de arriba: una franja de fieltro de otro color con sus dientes de lobo, cosida.
        val ap = l.c(aplicacionMongol(fieltroSeguroMongol(tono)))
        val o0 = top + ry + h * 0.03f; val o1 = o0 + 5f * u
        drawRect(ap, Offset(0f, o0), Size(w, o1 - o0))
        drawPath(dientesMongol(-2f * u, w + 2f * u, o1 - 0.6f * u, 3.4f * u, 3.6f * u), l.c(mezcla(base, P.BERMELLON, 0.35f)))
        drawLine(l.s(0.35f), Offset(0f, o1), Offset(w, o1), 1f * u)
        drawLine(hilo, Offset(0f, o0 + 0.6f * u), Offset(w, o0 + 0.6f * u), 0.7f * u, pathEffect = pe)
        // Las hileras de pespunte del acolchado, que siguen la curva del cilindro.
        val filas = 2 + (azar(v.semilla, 94) * 2).toInt()
        val pespuntes = Path()
        for (q in 0 until filas) {
            val yy = h * (0.5f + 0.12f * q)
            pespuntes.moveTo(xl, yy - 1f * u); pespuntes.quadraticTo(xm, yy + 2f * u, xr, yy - 1f * u)
        }
        drawPath(pespuntes, hilo.copy(alpha = 0.7f), style = Stroke(0.8f * u, pathEffect = pe))
        // El borde de la última vuelta: su canto y su sombra.
        val xb = if (hacia < 0f) xl + (xr - xl) * 0.7f else xl + (xr - xl) * 0.3f
        val borde = Path().apply { moveTo(xb, top); cubicTo(xb + w * 0.03f, h * 0.35f, xb - w * 0.03f, h * 0.65f, xb + w * 0.01f, h) }
        drawPath(corridoMongol(borde, -hacia * 1.6f * u, 0f), l.s(0.45f), style = Stroke(1.6f * u))
        drawPath(borde, l.b(0.32f), style = Stroke(0.8f * u))
        // Lo de abajo, más oscuro: el fieltro se hunde en la tabla (de noche, el hogar lo enciende).
        drawRect(l.s(0.2f), Offset(xl, h * 0.74f), Size(xr - xl, h * 0.26f))
        if (l.a.oscuro) drawRect(l.filo(0.3f), Offset(xl, h * 0.9f), Size(xr - xl, h * 0.1f))
        // La sombra propia del lado contrario a la luz, honda en el canto, y la que echa la vecina del lado de la luz.
        drawRect(l.s(0.28f), Offset(if (hacia < 0f) xr - (xr - xl) * 0.16f else xl, 0f), Size((xr - xl) * 0.16f, h))
        sombraVecinaMongol(l, hacia, xl, xr)
    }
    if (l.a.oscuro) {
        drawPath(Path().apply { val xf = if (hacia > 0) xr else xl; moveTo(xf, top); cubicTo(xf + hacia * bulto, h * 0.4f, xf + hacia * bulto, h * 0.8f, xf - hacia * w * 0.02f, h - ry * 0.5f) },
            l.filo(0.8f), style = Stroke(1.3f * u))
    } else drawLine(l.b(0.35f), Offset(xl + w * 0.04f, top + 2f * u), Offset(xl + w * 0.03f, h * 0.3f), 1.2f * u)
    // La cara de arriba: el fieltro visto desde arriba, con la espiral de sus vueltas.
    val cara = Rect(xl, top - ry, xr, top + ry)
    drawOval(l.c(mezcla(base, if (l.a.oscuro) P.LUNA else LUZ_MUEBLE, if (l.a.oscuro) 0.06f + 0.3f * l.f else 0.24f)), cara.topLeft, cara.size)
    val espiral = Path()
    for (i in 0..36) {
        val t = i / 36f
        val ang = t * 3 * 6.283f + 1.2f + azar(v.semilla, 56) * 3f
        val rr = 0.12f + 0.84f * t
        val px = cara.center.x + kotlin.math.cos(ang) * cara.width / 2 * rr
        val py = cara.center.y + kotlin.math.sin(ang) * cara.height / 2 * rr
        if (i == 0) espiral.moveTo(px, py) else espiral.lineTo(px, py)
    }
    drawPath(espiral, l.s(0.6f), style = Stroke(0.9f * u))
    drawOval(l.s(0.32f), cara.topLeft, cara.size, style = Stroke(0.8f * u))
    if (l.a.oscuro && l.f > 0.05f) drawArc(l.frio(0.8f), 190f, 160f, false, cara.topLeft, cara.size, style = Stroke(1f * u))
    // Las ataduras de crin torcida: la de arriba, a una altura distinta en cada rollo.
    val ys = listOf(h * (0.17f + 0.26f * azar(v.semilla, 57)), h * (0.86f - 0.04f * azar(v.semilla, 58)))
    ys.forEach { yy -> ataduraMongol(xl, xr, yy, l, bulto) }
    // El nudo de la atadura de arriba, y la tablilla colgada de su lazo, más largo o más corto.
    val rt = rectTablillaMongol(ys[0] + (2.5f + 6f * azar(v.semilla, 96)) * u, v, xm + (xr - xl) * (azar(v.semilla, 95) - 0.5f) * 0.4f)
    val nudo = Offset(rt.center.x + 2f * u, ys[0] + 0.6f * u)
    drawCircle(l.s(0.4f), 2.3f * u, nudo + Offset(-hacia * 0.8f * u, 0.8f * u))
    drawCircle(l.c(P.CRIN), 2.1f * u, nudo)
    drawCircle(l.b(0.35f), 0.8f * u, nudo - Offset(0.6f * u, 0.6f * u))
    tablillaMongol(v, medidor, l, rt, nudo, hacia)
}

/** Una atadura de crin torcida que abraza el rollo a la altura [y]: la banda oscura, las vueltas de la torsión y su sombra. */
private fun DrawScope.ataduraMongol(xl: Float, xr: Float, y: Float, l: LuzMongol, bulto: Float) {
    val u = density
    val curva = Path().apply { moveTo(xl - bulto * 0.6f, y - 1f * u); quadraticTo((xl + xr) / 2, y + 2.4f * u, xr + bulto * 0.6f, y - 1f * u) }
    drawPath(corridoMongol(curva, 0f, 1.6f * u), l.s(0.45f), style = Stroke(2.4f * u))
    drawPath(curva, l.c(P.CRIN), style = Stroke(2.6f * u))
    val tors = Path()
    var x = xl
    while (x < xr) {
        val t = (x - xl) / (xr - xl)
        val yy = y - 1f * u + 3.4f * u * t * (1f - t)
        tors.moveTo(x, yy + 1.1f * u); tors.lineTo(x + 1.5f * u, yy - 1.1f * u)
        x += 2.2f * u
    }
    drawPath(tors, l.c(mezcla(P.CRIN, P.CRUDO, 0.45f)), style = Stroke(0.7f * u))
    if (l.a.oscuro) drawPath(curva, l.filo(0.3f), style = Stroke(0.6f * u))
}

// ---------------------------------------------------------------- la bolsa de fieltro

/**
 * La bolsa de fieltro, blanda y vencida, con el peso abajo: los hombros caídos desde el cordón, la panza que se abre y se aplasta
 * en la tabla, vencida hacia un lado; la luz arriba en la panza y la sombra abajo y del otro lado; los frunces que bajan del
 * cuello. Arriba, la boca fruncida (unos pliegues cortos y redondos, con lo hondo de la boca entre ellos) que se dobla hacia el
 * lado que cae; el cordón de crin de dos vueltas, con su nudo; de un cabo cuelga la tablilla y del otro la borla de lana, que baja
 * por la panza. La tira de aplicación abajo. Más llena cuanto más escribiste.
 */
private fun DrawScope.bolsaMongol(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzMongol, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val base = l.animo(tono)
    val cuello = h * (0.22f + 0.06f * azar(v.semilla, 70))
    val cx = w * (0.47f + 0.06f * azar(v.semilla, 69))
    val cae = if (azar(v.semilla, 71) < 0.5f) 1f else -1f
    val panza = w * (0.54f + 0.06f * v.lleno)
    val vence = w * 0.07f * cae
    val hombro = w * 0.17f
    val forma = Path().apply {
        moveTo(cx - hombro, cuello)
        cubicTo(cx - hombro - w * 0.12f, cuello + h * 0.08f, cx - panza * 0.96f + vence * 0.3f, h * 0.5f, cx - panza + vence * 0.3f, h * 0.82f)
        quadraticTo(cx - panza + vence * 0.2f, h, cx - panza * 0.55f, h)
        lineTo(cx + panza * 0.55f + vence * 0.5f, h)
        quadraticTo(cx + panza + vence, h, cx + panza + vence, h * 0.8f)
        cubicTo(cx + panza * 0.96f + vence * 0.5f, h * 0.5f, cx + hombro + w * 0.12f, cuello + h * 0.08f, cx + hombro, cuello)
        close()
    }
    sombraDetrasMongol(forma, l, hacia)
    contactoEntradaMongol(l, hacia, cx - panza * 0.95f, cx + panza * 0.95f + vence)
    // La boca fruncida, sobre el cordón, doblada hacia el lado que cae: pliegues cortos y redondos, con lo hondo entre ellos.
    val alto = minOf(h * 0.12f, 9.5f * u)
    val x0 = cx - hombro * 1.3f; val x1 = cx + hombro * 1.3f
    val boca = Path().apply {
        moveTo(cx - hombro * 0.9f, cuello + 1f * u)
        lineTo(x0, cuello - alto * 0.5f)
        for (i in 0 until 4) {
            val xa = x0 + (x1 - x0) * i / 4; val xb = x0 + (x1 - x0) * (i + 1) / 4
            quadraticTo((xa + xb) / 2, cuello - alto * (1.1f + 0.22f * azar(v.semilla, 72 + i)), xb, cuello - alto * (0.55f + 0.12f * (i % 2)))
        }
        lineTo(cx + hombro * 0.9f, cuello + 1f * u)
        close()
    }
    val bocaCol = mezcla(base, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, 0.08f)
    rotate(cae * 9f, Offset(cx, cuello)) {
        drawPath(corridoMongol(boca, -hacia * 1.6f * u, 1.2f * u), l.s(0.32f))
        drawPath(boca, Brush.horizontalGradient(bandasMongol(bocaCol, l, hacia), x0, x1))
        // Lo hondo de la boca, que asoma entre los pliegues de arriba, y los valles que bajan al cordón (dentro de la boca: sin
        // recorte).
        drawOval(l.s(0.6f), Offset(cx - hombro * 0.6f, cuello - alto * 0.95f), Size(hombro * 1.2f, alto * 0.42f))
        val valles = Path()
        for (q in 1 until 4) {
            val x = x0 + (x1 - x0) * q / 4f
            valles.moveTo(x, cuello - alto * 0.5f); valles.lineTo(cx + (x - cx) * 0.55f, cuello + 0.5f * u)
        }
        drawPath(valles, l.s(0.5f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
    }
    // El cuerpo.
    drawPath(forma, Brush.horizontalGradient(bandasMongol(base, l, hacia), cx - panza, cx + panza + vence))
    pelusaMongol(forma, l)
    clipPath(forma) {
        // La panza: el plano de luz arriba del lado de la luz; abajo, el peso y la sombra.
        drawOval(l.b(0.28f), Offset(cx + (if (hacia < 0f) -panza * 0.85f else panza * 0.05f), cuello + h * 0.12f), Size(panza * 0.78f, h * 0.32f))
        drawRect(l.s(0.24f), Offset(-w * 0.2f, h * 0.72f), Size(w * 1.4f, h * 0.28f))
        if (l.a.oscuro) drawRect(l.filo(0.3f), Offset(-w, h * 0.9f), Size(w * 3f, h * 0.1f))
        // La sombra del cordón en los hombros.
        drawRect(l.s(0.35f), Offset(-w, cuello), Size(w * 3f, 3f * u))
        // Los frunces que bajan del cuello y se abren en la panza.
        val frunces = Path(); val luces = Path()
        for (q in 0 until 5) {
            val f = (q - 2) / 2f
            val p0 = Offset(cx + f * w * 0.1f, cuello + 0.5f * u)
            val p1 = Offset(cx + f * panza * 0.82f, cuello + h * (0.2f + 0.08f * azar(v.semilla, 80 + q)))
            val mx = (p0.x + p1.x) / 2 + f * w * 0.05f; val my = (p0.y + p1.y) / 2
            frunces.moveTo(p0.x, p0.y); frunces.quadraticTo(mx, my, p1.x, p1.y)
            luces.moveTo(p0.x - hacia * 1.1f * u, p0.y); luces.quadraticTo(mx - hacia * 1.1f * u, my, p1.x - hacia * 1.1f * u, p1.y)
        }
        drawPath(frunces, l.s(0.42f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawPath(luces, l.b(0.24f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        // El pliegue de la panza vencida, del lado que cae.
        val pv = Path().apply { moveTo(cx + cae * panza * 0.5f, h * 0.58f); quadraticTo(cx + cae * panza * 0.84f, h * 0.76f, cx + cae * panza * 0.64f, h * 0.96f) }
        drawPath(pv, l.s(0.34f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // La tira de aplicación de abajo, que sigue la panza, con sus puntadas.
        val ya = h * 0.8f
        val tira = Path().apply {
            moveTo(-w, ya); quadraticTo(cx, ya + h * 0.06f, w * 2f, ya)
            lineTo(w * 2f, ya + 5.2f * u); quadraticTo(cx, ya + h * 0.06f + 5.2f * u, -w, ya + 5.2f * u); close()
        }
        drawPath(corridoMongol(tira, 0f, 1.2f * u), l.s(0.32f))
        drawPath(tira, l.c(aplicacionMongol(fieltroSeguroMongol(tono))))
        val puntos = Path().apply { moveTo(-w, ya + 2.6f * u); quadraticTo(cx, ya + h * 0.06f + 2.6f * u, w * 2f, ya + 2.6f * u) }
        drawPath(puntos, l.c(mezcla(base, P.BERMELLON, 0.5f)), style = Stroke(1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 2.6f * u), 0f)))
        sombraVecinaMongol(l, hacia, cx - panza, cx + panza + vence)
    }
    if (l.a.oscuro) drawPath(forma, l.filo(0.45f), style = Stroke(1.1f * u))
    else drawPath(forma, l.s(0.28f), style = Stroke(0.8f * u))
    // El cordón de crin del cuello, en dos vueltas, y el nudo del lado de la tablilla.
    val cord = Path().apply { moveTo(cx - hombro - 1f * u, cuello - 1f * u); quadraticTo(cx, cuello + 2.2f * u, cx + hombro + 1f * u, cuello - 1f * u) }
    listOf(0f, 2.1f * u).forEach { dy ->
        drawPath(corridoMongol(cord, 0f, dy + 1.2f * u), l.s(0.4f), style = Stroke(2f * u))
        drawPath(corridoMongol(cord, 0f, dy), l.c(P.CRIN), style = Stroke(2f * u, cap = StrokeCap.Round))
    }
    // La tablilla cuelga de un lado y la borla del otro, para que se vean las dos.
    val ladoTablilla = if (azar(v.semilla, 99) < 0.5f) -1f else 1f
    val rt = rectTablillaMongol(cuello + (7f + 6f * azar(v.semilla, 97)) * u, v, cx + ladoTablilla * w * 0.17f)
    val nudo = Offset(rt.center.x - ladoTablilla * 1.5f * u, cuello + 2.6f * u)
    // El otro cabo baja por la panza y acaba en la borla de lana: el remate apretado y los hilos sueltos, abiertos abajo.
    val lado = -ladoTablilla
    val bx = (cx + lado * w * 0.3f).coerceIn(4f * u, w - 4f * u)
    val largo = h * (0.2f + 0.12f * azar(v.semilla, 98))
    val cabo = Path().apply { moveTo(nudo.x, nudo.y); quadraticTo(bx + lado * 3f * u, cuello + largo * 0.5f, bx, cuello + largo) }
    drawPath(corridoMongol(cabo, -hacia * 1.2f * u, 1.2f * u), l.s(0.4f), style = Stroke(1.3f * u))
    drawPath(cabo, l.c(P.CRIN), style = Stroke(1.3f * u, cap = StrokeCap.Round))
    val colBorla = l.c(if (base.red > base.blue + 0.15f) mezcla(P.ORO, P.CREMA, 0.35f) else P.BERMELLON)
    val bt = cuello + largo
    val hilos = Path().apply {
        moveTo(bx - 1.6f * u, bt + 1.6f * u); lineTo(bx + 1.6f * u, bt + 1.6f * u)
        lineTo(bx + 3.2f * u, bt + 10.5f * u)
        for (q in 0 until 4) lineTo(bx + (3.2f - 1.6f * (q + 0.5f)) * u, bt + (if (q % 2 == 0) 9.4f else 11f) * u)
        lineTo(bx - 3.2f * u, bt + 10.5f * u)
        close()
    }
    drawPath(corridoMongol(hilos, -hacia * 1.4f * u, 1f * u), l.s(0.4f))
    drawPath(hilos, colBorla)
    val hebras = Path()
    for (q in -1..1) { hebras.moveTo(bx + q * 0.9f * u, bt + 3f * u); hebras.lineTo(bx + q * 2f * u, bt + 9.8f * u) }
    drawPath(hebras, l.s(0.4f), style = Stroke(0.6f * u))
    drawRect(if (l.a.oscuro) l.filo(0.6f) else l.b(0.45f), Offset(if (hacia < 0f) bx - 2.4f * u else bx + 1.6f * u, bt + 3f * u), Size(0.8f * u, 6f * u))
    drawOval(colBorla, Offset(bx - 2f * u, bt - 1f * u), Size(4f * u, 3.8f * u))
    drawOval(l.s(0.4f), Offset(bx - 2f * u, bt + 1.6f * u), Size(4f * u, 1.2f * u))
    drawLine(l.c(P.CRIN), Offset(bx - 2.1f * u, bt + 1.9f * u), Offset(bx + 2.1f * u, bt + 1.9f * u), 0.9f * u)
    drawCircle(l.c(P.CRIN), 2.3f * u, nudo)
    drawCircle(l.b(0.35f), 0.9f * u, nudo - Offset(0.7f * u, 0.7f * u))
    tablillaMongol(v, medidor, l, rt, nudo, hacia)
}
