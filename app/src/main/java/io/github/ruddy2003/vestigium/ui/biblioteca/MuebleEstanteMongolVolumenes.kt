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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.roundToInt

// Las entradas de la alacena del lado de la dueña (MuebleEstanteMongol): el rollo de fieltro, los cofres de mimbre forrados de
// fieltro apilados para la mudanza y la bolsa de fieltro, todos teñidos del color del ánimo y con su tablilla de abedul.

/**
 * La pelusa del fieltro, pintada una sola vez (y otra si cambia la densidad) y repetida como pincel: fibras cortas claras y
 * oscuras. Así la textura de cientos de fibras es una sola operación por pieza.
 */
private object FieltroMongol {
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

internal fun DrawScope.volumenMongol(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val m = MuebleEstanteMongol
    val a = AireMongol(oscuro, v.noche)
    val fila = m.FILA / m.ANCHO_NOMINAL
    val x = m.LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * m.LADO)
    val y = m.CORONA + v.repisa * fila + (m.ALTO_REPISA.value / m.ANCHO_NOMINAL) * 0.55f
    val total = m.CORONA + v.repisas * fila + m.BASE
    val l = m.luz(a, x, y, total)
    // De dónde le llega la luz: de día, de la puerta (izquierda); de noche, del hogar (abajo a la derecha).
    val hacia = if (a.oscuro && x < m.HOGAR_X + 0.05f) 1f else -1f
    val tono = tonoDeDiaMongol(v.tono, oscuro)
    when (m.clase(v.semilla)) {
        0 -> rolloMongol(v, tono, medidor, l, hacia)
        1 -> cofresMongol(v, tono, medidor, l, hacia)
        else -> bolsaMongol(v, tono, medidor, l, hacia)
    }
}

// ---------------------------------------------------------------- ayudantes

/** El fieltro del ánimo en bandas, para un cuerpo redondo: el plano de luz del lado de [hacia], el tono y la sombra del otro. */
private fun bandasMongol(base: Color, l: LuzMongol, hacia: Float): List<Color> {
    val a = l.a
    val claro = when {
        a.oscuro -> mezcla(base, a.fuego, 0.22f * l.k + 0.04f)
        a.noche -> mezcla(base, P.CREPUSCULO, 0.16f + 0.1f * l.sol)
        else -> mezcla(base, if (l.sol > 0.2f) P.SOL else LUZ_MUEBLE, 0.2f + 0.1f * l.sol)
    }
    val sombra = mezcla(base, if (a.oscuro) a.hondo else SOMBRA_MUEBLE, if (a.oscuro) 0.5f else 0.3f)
    val honda = mezcla(base, if (a.oscuro) a.hondo else SOMBRA_MUEBLE, if (a.oscuro) 0.7f else 0.47f)
    val izqADer = listOf(mezcla(claro, base, 0.4f), claro, base, base, sombra, honda)
    return if (hacia < 0f) izqADer else izqADer.asReversed()
}

/** La luminancia aproximada de un color. */
private fun lumMongol(c: Color) = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

/** El hilo que contrasta con el fieltro [base]: crudo sobre lo oscuro, pardo sobre lo claro. */
private fun hiloMongol(base: Color, l: LuzMongol): Color =
    l.c(if (lumMongol(base) > 0.5f) mezcla(P.CUERO, P.HOLLIN, 0.35f) else mezcla(P.CREMA, P.ORO, 0.15f))

/** El fieltro que contrasta con [base] para las aplicaciones: añil sobre lo claro, crudo sobre lo rojo, oro viejo sobre el añil. */
private fun aplicacionMongol(base: Color): Color =
    if (lumMongol(base) > 0.5f) P.ANIL else if (base.red > base.blue + 0.15f) P.CREMA else mezcla(P.CREMA, P.ORO, 0.35f)

/** El fieltro del ánimo sin luz (para escoger el de la aplicación); sin ánimo, el pardo sin teñir. */
private fun fieltroDelAnimoSeguro(t: Color?): Color = if (t == null) P.SIN_ANIMO else fieltroDelAnimo(t)

/** La sombra que la entrada echa detrás, en el fondo del casillero (a la derecha de día; de noche, a la izquierda y arriba). */
private fun DrawScope.sombraDetrasMongol(forma: Path, l: LuzMongol, hacia: Float) {
    val u = density
    val dx = -hacia * 5f * u
    val dy = if (l.a.oscuro) -4f * u else 1.5f * u
    drawPath(corridoMongol(forma, dx, dy), l.s(if (l.a.oscuro) 0.5f else 0.32f))
}

/** El contacto con la tabla: la sombra oscura justo debajo y la que se tiende hacia el lado contrario a la luz. */
private fun DrawScope.contactoEntradaMongol(l: LuzMongol, hacia: Float, x0: Float, x1: Float) {
    val u = density
    val h = size.height
    val largo = (x1 - x0)
    drawOval(l.s(0.42f), Offset(x0 - hacia * largo * 0.14f, h - 2.4f * u), Size(largo * 1.08f, 4.6f * u))
    drawRect(l.s(0.6f), Offset(x0, h - 1.2f * u), Size(largo, 1.4f * u))
}

/** El mes, bordado de abajo arriba a lo largo de [r], en hilo que contrasta. */
private fun DrawScope.mesMongol(medidor: TextMeasurer, mes: String, r: Rect, color: Color) {
    if (r.height < 12f || r.width < 4f) return
    textoEn(medidor, mes, r, P.LETRA, color, 6.8f, vertical = true, minimo = 4.4f)
}

/** Dónde va la tablilla: a la izquierda (en las angostas asoma un poco por fuera), dejando a la derecha la franja del mes. */
private fun DrawScope.rectTablillaMongol(top: Float, noche: Boolean): Rect {
    val u = density
    val w = size.width
    val tw = 20f * u
    val th = (if (noche) 28.5f else 21.5f) * u
    val izq = minOf(w * 0.09f, w - 9.5f * u - tw)
    return Rect(izq, top, izq + tw, top + th)
}

/** La franja de la derecha donde va bordado el mes. */
private fun DrawScope.franjaMesMongol(top: Float, bottom: Float): Rect {
    val u = density
    val w = size.width
    return Rect(w - 9f * u, top, w - 2f * u, bottom)
}

/**
 * La tablilla de corteza de abedul con el día, colgada de su lazo de crin desde [nudo] y un poco ladeada (cuelga): el borde
 * irregular, la corteza vieja, las lenticelas lejos del número, la punta que se enrolla y su sombra en la entrada. En los
 * Sueños, una media luna de añil bajo el día.
 */
private fun DrawScope.tablillaMongol(v: Volumen, medidor: TextMeasurer, l: LuzMongol, r: Rect, nudo: Offset, hacia: Float) {
    val u = density
    val giro = (azar(v.semilla, 53) - 0.5f) * 9f
    // El lazo de crin, del nudo a los agujeros.
    val ag = Offset(r.center.x, r.top + 2.2f * u)
    rotate(giro, nudo) {
        drawLine(l.s(0.35f), nudo + Offset(-hacia * 0.8f * u, 0.8f * u), ag + Offset(-hacia * 0.8f * u, 0.8f * u), 1.1f * u)
        drawLine(l.c(P.CRIN), nudo, Offset(ag.x - 2f * u, ag.y), 1f * u)
        drawLine(l.c(P.CRIN), nudo, Offset(ag.x + 2f * u, ag.y), 1f * u)
        val forma = contornoIrregular(r, v.semilla + 31, 14, 0.03f)
        drawPath(corridoMongol(forma, -hacia * 1.8f * u, (if (l.a.oscuro) -0.8f else 2f) * u), l.s(0.45f))
        val corteza = l.etiqueta()
        drawPath(forma, corteza)
        clipPath(forma) {
            // La corteza vieja: un velo ocre desigual, las lenticelas arriba y abajo (lejos del número) y el canto ocre.
            drawOval(mezcla(corteza, P.ABEDUL_OSC, 0.55f).copy(alpha = 0.5f), Offset(r.left + r.width * (0.4f + 0.3f * azar(v.semilla, 54)), r.top - r.height * 0.2f), Size(r.width * 0.9f, r.height * 0.5f))
            for (q in 0 until 4) {
                val arriba = q % 2 == 0
                val yy = if (arriba) r.top + r.height * (0.07f + 0.04f * azar(v.semilla, 40 + q)) else r.bottom - r.height * (0.07f + 0.05f * azar(v.semilla, 40 + q))
                val xx = r.left + r.width * (0.08f + 0.55f * azar(v.semilla, 44 + q))
                drawLine(l.c(P.LENTICELA).copy(alpha = 0.55f), Offset(xx, yy), Offset(xx + r.width * (0.16f + 0.14f * azar(v.semilla, 48 + q)), yy), 0.9f * u)
            }
            drawRect(l.c(P.ABEDUL_OSC).copy(alpha = 0.6f), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.8f * u))
            if (!l.a.oscuro) drawRect(l.b(0.45f), Offset(r.left, r.top), Size(1.2f * u, r.height))
            else drawRect(l.filo(0.6f), Offset(if (hacia > 0) r.right - 1.2f * u else r.left, r.top), Size(1.2f * u, r.height))
        }
        // La punta que se enrolla en una esquina de abajo.
        val e = if (azar(v.semilla, 52) < 0.5f) r.right else r.left
        val s = if (e == r.right) -1f else 1f
        drawPath(poligonoMongol(e, r.bottom - 4.5f * u, e + s * 4f * u, r.bottom, e, r.bottom), mezcla(corteza, P.ABEDUL_OSC, 0.7f))
        drawLine(l.s(0.45f), Offset(e, r.bottom - 4.5f * u), Offset(e + s * 4f * u, r.bottom), 0.7f * u)
        drawCircle(l.c(P.HOLLIN), 0.8f * u, Offset(ag.x - 2f * u, ag.y))
        drawCircle(l.c(P.HOLLIN), 0.8f * u, Offset(ag.x + 2f * u, ag.y))
        // El día y, en los Sueños, la media luna.
        val tinta = if (l.a.oscuro) Color(0xFF0E0805) else P.TINTA
        val zonaDia = Rect(r.left + 0.3f * u, r.top + 3f * u, r.right - 0.3f * u, r.top + 3f * u + 17.5f * u)
        textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, peso = FontWeight.Bold, minimo = 9f)
        if (v.noche) {
            val c = Offset(r.center.x, zonaDia.bottom + 3.6f * u)
            val rl = 3.2f * u
            drawCircle(l.c(P.ANIL), rl, c)
            drawCircle(corteza, rl * 0.84f, c + Offset(rl * 0.42f, -rl * 0.24f))
        }
        drawPath(forma, l.s(0.4f), style = Stroke(0.8f * u))
    }
}

// ---------------------------------------------------------------- el rollo de fieltro

/**
 * El rollo de fieltro, de pie: el cilindro blando teñido del ánimo, con la luz de un lado y la sombra del otro y su pelusa;
 * arriba, la cara del rollo con la espiral de sus vueltas; el borde de la última vuelta con su pespunte; dos ataduras de crin
 * torcida con su nudo; la tablilla colgada del nudo de arriba y el mes bordado a lo largo. Más grueso cuanto más escribiste.
 */
private fun DrawScope.rolloMongol(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzMongol, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val base = l.animo(tono)
    val xl = w * 0.06f; val xr = w * 0.94f
    val ry = minOf((xr - xl) * 0.17f, 5.5f * u)
    val top = ry + 0.5f * u
    val bulto = w * 0.015f
    val cuerpo = Path().apply {
        moveTo(xl, top)
        cubicTo(xl - bulto, top + (h - top) * 0.4f, xl - bulto, h - ry * 1.4f, xl + w * 0.02f, h - ry * 0.5f)
        quadraticTo(w * 0.5f, h + ry * 0.35f, xr - w * 0.02f, h - ry * 0.5f)
        cubicTo(xr + bulto, h - ry * 1.4f, xr + bulto, top + (h - top) * 0.4f, xr, top)
        close()
    }
    sombraDetrasMongol(cuerpo, l, hacia)
    contactoEntradaMongol(l, hacia, xl, xr)
    drawPath(cuerpo, Brush.horizontalGradient(bandasMongol(base, l, hacia), xl, xr))
    drawPath(cuerpo, FieltroMongol.pincel(u), alpha = if (l.a.oscuro) 0.2f + 0.55f * l.k else 0.85f)
    clipPath(cuerpo) {
        // El borde de la última vuelta: su canto, su sombra y el pespunte que lo cierra.
        val xb = if (hacia < 0f) w * 0.68f else w * 0.32f
        val borde = Path().apply { moveTo(xb, top); cubicTo(xb + w * 0.03f, h * 0.35f, xb - w * 0.03f, h * 0.65f, xb + w * 0.01f, h) }
        drawPath(corridoMongol(borde, -hacia * 1.6f * u, 0f), l.s(0.45f), style = Stroke(1.6f * u))
        drawPath(borde, l.b(0.32f), style = Stroke(0.8f * u))
        drawPath(corridoMongol(borde, hacia * 2.4f * u, 0f), hiloMongol(base, l).copy(alpha = 0.7f),
            style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.8f * u), 0f)))
        // Lo de abajo, más oscuro: el fieltro se hunde en la tabla y ahí le llega menos luz (de noche, el hogar lo enciende).
        drawRect(l.s(0.14f), Offset(0f, h * 0.72f), Size(w, h * 0.28f))
        drawRect(l.s(0.16f), Offset(0f, h * 0.86f), Size(w, h * 0.14f))
        if (l.a.oscuro) drawRect(l.filo(0.3f), Offset(0f, h * 0.9f), Size(w, h * 0.1f))
    }
    // El filo del lado de la luz.
    if (l.a.oscuro) {
        drawPath(Path().apply { val xf = if (hacia > 0) xr else xl; moveTo(xf, top); cubicTo(xf + hacia * bulto, h * 0.4f, xf + hacia * bulto, h * 0.8f, xf - hacia * w * 0.02f, h - ry * 0.5f) },
            l.filo(0.8f), style = Stroke(1.3f * u))
    } else drawPath(Path().apply { moveTo(xl + w * 0.04f, top + 2f * u); lineTo(xl + w * 0.03f, h - ry) }, l.b(0.35f), style = Stroke(1.2f * u))
    // La cara de arriba: el fieltro visto desde arriba, con la espiral de sus vueltas.
    val cara = Rect(xl, top - ry, xr, top + ry)
    drawOval(l.c(mezcla(base, if (l.a.oscuro) P.LUNA else LUZ_MUEBLE, if (l.a.oscuro) 0.06f + 0.3f * l.f else 0.24f)), cara.topLeft, cara.size)
    val espiral = Path()
    val pasos = 36
    for (i in 0..pasos) {
        val t = i / pasos.toFloat()
        val ang = t * 3 * 6.283f + 1.2f + azar(v.semilla, 56) * 3f
        val rr = 0.12f + 0.84f * t
        val px = cara.center.x + kotlin.math.cos(ang) * cara.width / 2 * rr
        val py = cara.center.y + kotlin.math.sin(ang) * cara.height / 2 * rr
        if (i == 0) espiral.moveTo(px, py) else espiral.lineTo(px, py)
    }
    drawPath(espiral, l.s(0.6f), style = Stroke(0.9f * u))
    drawPath(corridoMongol(espiral, 0f, 0.7f * u), l.b(0.3f), style = Stroke(0.6f * u))
    drawOval(l.s(0.32f), cara.topLeft, cara.size, style = Stroke(0.8f * u))
    if (l.a.oscuro && l.f > 0.05f) drawArc(l.frio(0.8f), 190f, 160f, false, cara.topLeft, cara.size, style = Stroke(1f * u))
    // El mes, bordado a lo largo (por debajo de las ataduras).
    mesMongol(medidor, v.mes, franjaMesMongol(top + ry + 2f * u, h - 3f * u), hiloMongol(base, l))
    // Las ataduras de crin torcida.
    val ys = listOf(h * (0.26f + 0.06f * azar(v.semilla, 57)), h * 0.8f)
    ys.forEach { yy -> ataduraMongol(xl, xr, yy, l, bulto) }
    // El nudo de la atadura de arriba, con sus cabos, y la tablilla colgada.
    val rt = rectTablillaMongol(ys[0] + 3.5f * u, v.noche)
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

// ---------------------------------------------------------------- los cofres apilados

/**
 * Dos cofres de mimbre forrados de fieltro, apilados y atados para la mudanza (como los que describe Rubruck: cajones de varas
 * trenzadas forrados de fieltro untado de sebo). Cada uno con su tapa volada (la cara de arriba con el brillo del sebo), su
 * costado en escorzo, la tira de aplicación con dientes de lobo y el mimbre que asoma abajo donde se gastó el fieltro; la
 * correa de cuero que los ata con su botón de hueso, de donde cuelga la tablilla, y el mes bordado en el costado. Más altos
 * cuanto más escribiste.
 */
private fun DrawScope.cofresMongol(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzMongol, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val base = l.animo(tono)
    val corte = h * (0.47f + 0.04f * azar(v.semilla, 58))
    val abajo = Rect(w * 0.0f, corte, w * 0.97f, h)
    val arriba = Rect(w * 0.08f, h * 0.03f, w * 0.9f, corte)
    val todo = Path().apply { addRect(abajo); addRect(arriba) }
    sombraDetrasMongol(todo, l, hacia)
    contactoEntradaMongol(l, hacia, abajo.left, abajo.right)
    cofre(v, abajo, base, tono, l, hacia, 0)
    // La sombra del cofre de arriba en la tapa del de abajo.
    drawRect(l.s(0.45f), Offset(arriba.left - hacia * 2f * u, corte - 1f * u), Size(arriba.width + 3f * u, 2.4f * u))
    cofre(v, arriba, mezcla(base, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, 0.04f), tono, l, hacia, 1)
    // El mes, bordado en el costado del de abajo.
    mesMongol(medidor, v.mes, franjaMesMongol(corte + 5f * u, h - h * 0.08f), hiloMongol(base, l))
    // La correa que los ata, de arriba abajo, con su botón de hueso; la tablilla cuelga del botón.
    val cx = minOf(w * 0.34f, w - 22f * u)
    val pe = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u), 0f)
    drawRect(l.s(0.35f), Offset(cx - 1.6f * u - hacia * 0.9f * u, arriba.top), Size(3.4f * u, h - arriba.top))
    drawRect(l.c(P.CUERO), Offset(cx - 1.8f * u, arriba.top), Size(3.6f * u, h - arriba.top - 1f * u))
    drawRect(l.b(0.3f), Offset(cx - 1.8f * u, arriba.top), Size(0.8f * u, h - arriba.top))
    drawLine(l.c(P.CRUDO).copy(alpha = 0.6f), Offset(cx, arriba.top), Offset(cx, h - 1f * u), 0.6f * u, pathEffect = pe)
    val boton = Offset(cx, arriba.top + (corte - arriba.top) * 0.3f)
    drawRoundRect(l.s(0.4f), Offset(boton.x - 3f * u, boton.y - 0.6f * u), Size(6.8f * u, 3f * u), CornerRadius(1.4f * u))
    drawRoundRect(l.c(P.HUESO), Offset(boton.x - 3.4f * u, boton.y - 1.5f * u), Size(6.8f * u, 3f * u), CornerRadius(1.4f * u))
    drawRect(l.b(0.5f), Offset(boton.x - 2.6f * u, boton.y - 1.2f * u), Size(3f * u, 0.8f * u))
    val rt = rectTablillaMongol(boton.y + 3.5f * u, v.noche)
    tablillaMongol(v, medidor, l, rt, Offset(boton.x, boton.y + 1f * u), hacia)
}

/** Un cofre de la pila: la caja forrada de fieltro con su costado en escorzo, la tira, el mimbre de abajo y la tapa con el sebo. */
private fun DrawScope.cofre(v: Volumen, r: Rect, base: Color, tono: Color?, l: LuzMongol, hacia: Float, k: Int) {
    val u = density
    val tapaAlto = minOf(r.height * 0.26f, 11f * u)
    val fondoLado = minOf(r.width * 0.12f, 4f * u)
    // El costado, en escorzo, del lado contrario a la luz (de día a la derecha).
    val lado = if (hacia < 0f) r.right else r.left
    val s = if (hacia < 0f) 1f else -1f
    val costado = poligonoMongol(lado, r.top + tapaAlto * 0.6f, lado + s * fondoLado, r.top + tapaAlto * 0.2f, lado + s * fondoLado, r.bottom - 1.2f * u, lado, r.bottom)
    drawPath(costado, mezcla(base, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, if (l.a.oscuro) 0.6f else 0.42f))
    // El frente.
    val frente = Rect(r.left + (if (s < 0) 0f else 0f), r.top + tapaAlto * 0.7f, r.right, r.bottom)
    val bandas = bandasMongol(base, l, hacia)
    drawRect(Brush.horizontalGradient(listOf(bandas[1], bandas[2], bandas[3], bandas[4]), frente.left, frente.right), frente.topLeft, frente.size)
    drawRect(FieltroMongol.pincel(u), frente.topLeft, frente.size, alpha = if (l.a.oscuro) 0.2f + 0.55f * l.k else 0.85f)
    clipRect(frente.left, frente.top, frente.right, frente.bottom) {
        // El mimbre que asoma abajo, donde el fieltro se gastó.
        val mimbre = frente.bottom - minOf(frente.height * 0.2f, 6f * u)
        val borde = Path().apply {
            moveTo(frente.left, mimbre)
            var x = frente.left
            var i = 0
            while (x < frente.right) { x += 3f * u; lineTo(x, mimbre + (azar(v.semilla + k, 60 + i) - 0.3f) * 2.2f * u); i++ }
            lineTo(frente.right, frente.bottom); lineTo(frente.left, frente.bottom); close()
        }
        drawPath(borde, l.c(P.MIMBRE))
        clipPath(borde) {
            var x = frente.left - (k * 1.8f) * u
            var i = 0
            while (x < frente.right) {
                val yy = mimbre - 1.5f * u + (if (i % 2 == 0) 0f else 2f * u)
                drawRoundRect(l.c(mezcla(P.MIMBRE, P.CREMA, 0.32f)), Offset(x, yy), Size(3.2f * u, 1.8f * u), CornerRadius(0.9f * u))
                drawRoundRect(l.c(mezcla(P.MIMBRE, P.CREMA, 0.32f)), Offset(x + 1.8f * u, yy + 4f * u), Size(3.2f * u, 1.8f * u), CornerRadius(0.9f * u))
                drawLine(l.s(0.45f), Offset(x + 3.4f * u, mimbre - 2f * u), Offset(x + 3.4f * u, frente.bottom), 0.6f * u)
                x += 3.6f * u; i++
            }
            drawRect(l.s(0.4f), Offset(frente.left, mimbre), Size(frente.width, 1.4f * u))
        }
        // La tira de aplicación con sus dientes de lobo.
        val t0 = frente.top + (mimbre - frente.top) * 0.42f
        val t1 = t0 + minOf(6f * u, (mimbre - frente.top) * 0.3f)
        val ap = l.c(aplicacionMongol(fieltroDelAnimoSeguro(tono)))
        drawRect(l.s(0.3f), Offset(frente.left, t1), Size(frente.width, 1.1f * u))
        drawRect(ap, Offset(frente.left, t0), Size(frente.width, t1 - t0))
        val dientes = Path()
        var dx = frente.left + 1f * u
        val dw = 3.2f * u
        while (dx + dw < frente.right) { dientes.moveTo(dx, t1 - 0.7f * u); dientes.lineTo(dx + dw / 2, t0 + 0.8f * u); dientes.lineTo(dx + dw, t1 - 0.7f * u); dientes.close(); dx += dw * 1.3f }
        drawPath(dientes, l.c(mezcla(base, P.BERMELLON, 0.45f)))
        drawLine(hiloMongol(base, l), Offset(frente.left, t0), Offset(frente.right, t0), 0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * u, 1.4f * u), 0f))
        // La sombra de la tapa y el canto del lado de la luz.
        drawRect(Brush.verticalGradient(listOf(l.s(0.5f), Color.Transparent), frente.top, frente.top + 5f * u), frente.topLeft, Size(frente.width, 5f * u))
        if (l.a.oscuro) drawRect(l.filo(0.7f), Offset(if (hacia > 0f) frente.right - 1.3f * u else frente.left, frente.top), Size(1.3f * u, frente.height))
        else drawRect(l.b(0.42f), Offset(frente.left, frente.top), Size(1.3f * u, frente.height))
    }
    // La tapa volada: un poco más ancha que la caja; la cara de arriba (vista desde arriba) con el brillo del sebo.
    val tapa = Rect(r.left - 1f * u, r.top, r.right + 1f * u, r.top + tapaAlto)
    val cara = poligonoMongol(tapa.left + 0.5f * u, tapa.top + tapaAlto * 0.45f, tapa.left + s * 0f + fondoLado * (if (s > 0) 0f else 1f), tapa.top,
        tapa.right - fondoLado * (if (s > 0) 0f else 1f) + s * fondoLado, tapa.top, tapa.right - 0.5f * u, tapa.top + tapaAlto * 0.45f)
    val tapaCol = mezcla(base, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, 0.1f)
    drawPath(cara, l.c(mezcla(tapaCol, LUZ_MUEBLE, if (l.a.oscuro) 0f else 0.18f)).let { if (l.a.oscuro) mezcla(it, P.LUNA, 0.25f * l.f) else it })
    drawOval(l.b(0.55f), Offset(tapa.left + tapa.width * 0.18f, tapa.top + tapaAlto * 0.1f), Size(tapa.width * 0.42f, tapaAlto * 0.2f))
    val canto = Rect(tapa.left, tapa.top + tapaAlto * 0.45f, tapa.right, tapa.bottom)
    drawRect(l.s(0.42f), Offset(canto.left - hacia * 1.4f * u, canto.top + 1.6f * u), canto.size)
    drawRect(Brush.horizontalGradient(bandasMongol(tapaCol, l, hacia), canto.left, canto.right), canto.topLeft, canto.size)
    drawRect(l.b(0.4f), canto.topLeft, Size(canto.width, 1f * u))
    drawRect(l.s(0.4f), Offset(canto.left, canto.bottom - 1.2f * u), Size(canto.width, 1.2f * u))
    drawLine(hiloMongol(base, l).copy(alpha = 0.7f), Offset(canto.left + 1.5f * u, canto.center.y), Offset(canto.right - 1.5f * u, canto.center.y), 0.7f * u,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.8f * u, 1.6f * u), 0f))
    if (l.a.oscuro && l.f > 0.05f) drawLine(l.frio(0.7f), Offset(cara.getBounds().left, tapa.top + 0.6f * u), Offset(cara.getBounds().right, tapa.top + 0.6f * u), 1f * u)
}

// ---------------------------------------------------------------- la bolsa de fieltro

/**
 * La bolsa de fieltro, blanda y vencida sobre la tabla: la panza redonda que se aplasta abajo, con la luz arriba y la sombra
 * del otro lado; los frunces que bajan del cuello; el cordón de crin que la cierra, con el nudo de donde cuelga la tablilla; lo
 * que sobra arriba, rizado y caído hacia un lado; la tira de aplicación abajo y el mes bordado en el costado. Más llena (más
 * panzona y más alta) cuanto más escribiste.
 */
private fun DrawScope.bolsaMongol(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzMongol, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val base = l.animo(tono)
    val lleno = v.lleno
    val cuello = h * (0.27f + 0.03f * azar(v.semilla, 70))
    val cx = w * 0.48f
    val panza = w * (0.53f + 0.03f * lleno)
    val cae = if (azar(v.semilla, 71) < 0.5f) 1f else -1f
    val forma = Path().apply {
        moveTo(cx - w * 0.15f, cuello)
        cubicTo(cx - w * 0.2f, cuello + h * 0.06f, cx - panza, cuello + h * 0.16f, cx - panza, h * 0.66f)
        cubicTo(cx - panza, h * 0.9f, cx - panza * 0.86f, h, cx - panza * 0.5f, h)
        lineTo(cx + panza * 0.5f, h)
        cubicTo(cx + panza * 0.88f, h, cx + panza * 1.02f, h * 0.9f, cx + panza * 1.01f, h * 0.64f)
        cubicTo(cx + panza, cuello + h * 0.16f, cx + w * 0.2f, cuello + h * 0.06f, cx + w * 0.15f, cuello)
        close()
    }
    sombraDetrasMongol(forma, l, hacia)
    contactoEntradaMongol(l, hacia, cx - panza * 0.95f, cx + panza * 0.95f)
    // Lo que sobra arriba del cordón: el fieltro fruncido que se abre y cae hacia un lado, con su revés más oscuro.
    val y0 = h * 0.02f
    val boca = Path().apply {
        moveTo(cx - w * 0.14f, cuello + 1f * u)
        cubicTo(cx - w * 0.2f, cuello - h * 0.08f, cx - w * 0.26f - cae * w * 0.04f, y0 + h * 0.06f, cx - w * 0.2f, y0 + h * 0.04f)
        quadraticTo(cx - w * 0.08f, y0 - h * 0.01f, cx + cae * w * 0.04f, y0 + h * 0.03f)
        quadraticTo(cx + w * 0.14f + cae * w * 0.06f, y0 + h * 0.0f, cx + w * 0.24f + cae * w * 0.1f, y0 + h * 0.07f + (if (cae > 0) h * 0.04f else 0f))
        cubicTo(cx + w * 0.3f + cae * w * 0.08f, cuello - h * 0.1f, cx + w * 0.22f, cuello - h * 0.04f, cx + w * 0.14f, cuello + 1f * u)
        close()
    }
    val bocaCol = mezcla(base, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, 0.12f)
    drawPath(corridoMongol(boca, -hacia * 2f * u, 1.4f * u), l.s(0.3f))
    drawPath(boca, Brush.horizontalGradient(bandasMongol(bocaCol, l, hacia), cx - w * 0.3f, cx + w * 0.34f))
    clipPath(boca) {
        for (q in 0 until 4) {
            val x = cx - w * 0.15f + w * 0.1f * q
            drawLine(l.s(0.45f), Offset(x + cae * w * 0.05f, y0 + h * 0.04f), Offset(cx + (x - cx) * 0.4f, cuello), 1.1f * u)
            drawLine(l.b(0.22f), Offset(x + cae * w * 0.05f + 1.2f * u, y0 + h * 0.04f), Offset(cx + (x - cx) * 0.4f + 0.8f * u, cuello), 0.7f * u)
        }
        // El revés del fieltro, más oscuro, en el borde rizado.
        drawPath(corridoMongol(boca, 0f, 2.2f * u), mezcla(bocaCol, if (l.a.oscuro) l.a.hondo else SOMBRA_MUEBLE, 0.35f), style = Stroke(2.6f * u))
    }
    // El cuerpo.
    drawPath(forma, Brush.horizontalGradient(bandasMongol(base, l, hacia), cx - panza, cx + panza))
    drawPath(forma, FieltroMongol.pincel(u), alpha = if (l.a.oscuro) 0.2f + 0.55f * l.k else 0.85f)
    clipPath(forma) {
        // La panza: el plano de luz arriba del lado de la luz, la sombra abajo (se aplasta contra la tabla).
        drawOval(l.b(0.3f), Offset(cx + (if (hacia < 0f) -panza * 0.85f else panza * 0.05f), cuello + h * 0.12f), Size(panza * 0.8f, h * 0.32f))
        drawRect(l.s(0.16f), Offset(-w, h * 0.72f), Size(w * 3f, h * 0.28f))
        drawRect(l.s(0.2f), Offset(-w, h * 0.86f), Size(w * 3f, h * 0.14f))
        if (l.a.oscuro) drawRect(l.filo(0.3f), Offset(-w, h * 0.9f), Size(w * 3f, h * 0.1f))
        // Los frunces que bajan del cuello y se abren en la panza.
        for (q in 0 until 5) {
            val f = (q - 2) / 2f
            val p0 = Offset(cx + f * w * 0.1f, cuello + 0.5f * u)
            val p1 = Offset(cx + f * panza * 0.8f, cuello + h * (0.2f + 0.08f * azar(v.semilla, 80 + q)))
            val fr = Path().apply { moveTo(p0.x, p0.y); quadraticTo((p0.x + p1.x) / 2 + f * w * 0.05f, (p0.y + p1.y) / 2, p1.x, p1.y) }
            drawPath(fr, l.s(0.4f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            drawPath(corridoMongol(fr, -hacia * 1.1f * u, 0f), l.b(0.24f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        }
        // Un pliegue de la panza vencida, del lado que cae.
        val pv = Path().apply { moveTo(cx + cae * panza * 0.55f, h * 0.62f); quadraticTo(cx + cae * panza * 0.8f, h * 0.78f, cx + cae * panza * 0.6f, h * 0.94f) }
        drawPath(pv, l.s(0.3f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // La tira de aplicación de abajo, que sigue la panza, con sus puntadas.
        val ap = l.c(aplicacionMongol(fieltroDelAnimoSeguro(tono)))
        val ya = h * 0.8f
        val tira = Path().apply {
            moveTo(-w, ya); quadraticTo(cx, ya + h * 0.06f, w * 2f, ya)
            lineTo(w * 2f, ya + 5.2f * u); quadraticTo(cx, ya + h * 0.06f + 5.2f * u, -w, ya + 5.2f * u); close()
        }
        drawPath(corridoMongol(tira, 0f, 1.2f * u), l.s(0.32f))
        drawPath(tira, ap)
        val puntos = Path().apply { moveTo(-w, ya + 2.6f * u); quadraticTo(cx, ya + h * 0.06f + 2.6f * u, w * 2f, ya + 2.6f * u) }
        drawPath(puntos, l.c(mezcla(base, P.BERMELLON, 0.5f)), style = Stroke(1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 2.6f * u), 0f)))
    }
    // El filo de la luz en el canto de la panza.
    if (l.a.oscuro) drawPath(forma, l.filo(0.45f), style = Stroke(1.1f * u))
    else drawPath(forma, l.s(0.25f), style = Stroke(0.8f * u))
    // El mes, bordado en el costado de la panza.
    mesMongol(medidor, v.mes, franjaMesMongol(cuello + h * 0.1f, h * 0.79f), hiloMongol(base, l))
    // El cordón de crin del cuello: dos vueltas y el nudo, del que cuelga la tablilla.
    val cord = Path().apply { moveTo(cx - w * 0.17f, cuello - 1f * u); quadraticTo(cx, cuello + 2.2f * u, cx + w * 0.17f, cuello - 1f * u) }
    listOf(0f, 2.6f * u).forEach { dy ->
        drawPath(corridoMongol(cord, 0f, dy + 1.2f * u), l.s(0.4f), style = Stroke(2f * u))
        drawPath(corridoMongol(cord, 0f, dy), l.c(P.CRIN), style = Stroke(2f * u, cap = StrokeCap.Round))
    }
    val rt = rectTablillaMongol(cuello + 6.5f * u, v.noche)
    val nudo = Offset(rt.center.x + 1.5f * u, cuello + 2.4f * u)
    drawCircle(l.c(P.CRIN), 2.4f * u, nudo)
    drawCircle(l.b(0.35f), 0.9f * u, nudo - Offset(0.7f * u, 0.7f * u))
    tablillaMongol(v, medidor, l, rt, nudo, hacia)
}
