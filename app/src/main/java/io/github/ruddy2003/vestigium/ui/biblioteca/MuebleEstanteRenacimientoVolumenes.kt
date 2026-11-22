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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE

/**
 * Las tres entradas del estante de Renacimiento, todas de la tienda del cartolaio, cada una con su silueta y modelada como un
 * objeto (su luz, su sombra, su brillo y sus detalles): el libro nuevo encuadernado en piel teñida, alto y tieso, con el lomo
 * redondo, los nervios que sobresalen con sus filetes dorados, el tejuelo de papel y los registros que asoman arriba; los
 * pliegos impresos sin encuadernar, más bajos, envueltos en papel de color y atados con bramante, con la cédula metida bajo el
 * cordel y el lacre en el nudo; y el librito en vitela floja, vencido, con la cubierta que se dobla en la cabeza y las
 * correíllas de piel blanca que pasan por el lomo. El color del ánimo es la piel, el papel de la envoltura o la vitela.
 */
internal object VolumenEstanteRenacimiento {
    private val P = PaletaRenacimiento

    /** Qué tan claro es un color (para escribir encima en tinta o en oro). */
    private fun claridad(c: Color): Float = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

    /** El color del ánimo hecho materia (piel, papel teñido): un poco hacia lo hondo, con cuerpo. */
    private fun materia(tono: Color?, a: AireRenacimiento, k: Float, hondo: Float): Color =
        a.animo(tono?.let { mezcla(it, P.TINTE_HONDO, hondo) } ?: P.SIN_ANIMO, k)

    /** De día, lo que está más lejos de la ventana ([dia]) se apaga un poco hacia la sombra violeta. */
    private fun lejos(c: Color, dia: Float): Color = if (dia > 0.005f) mezcla(c, SOMBRA_MUEBLE, dia) else c

    /**
     * El modelado de un cuerpo de pie entre [x0] y [x1] (ir dentro de su recorte): el plano de luz hacia donde le llega
     * ([luzDe]: -1, de la izquierda), el brillo, el plano de sombra al otro lado, el canto oscuro y, de noche, el filo de la
     * lucerna.
     */
    private fun DrawScope.cilindro(x0: Float, x1: Float, y0: Float, y1: Float, t: TonoRenacimiento, luzDe: Float, brillo: Float = 1f) {
        val u = density
        val an = x1 - x0
        fun x(f: Float, ancho: Float) = if (luzDe < 0) x0 + an * f else x1 - an * (f + ancho)
        val alto = y1 - y0
        drawRect(t.b(0.16f * brillo), Offset(x(0f, 0.32f), y0), Size(an * 0.32f, alto))
        drawRect(t.b(0.34f * brillo), Offset(x(0.14f, 0.09f), y0), Size(an * 0.09f, alto))
        drawRect(t.s(0.16f), Offset(x(0.58f, 0.42f), y0), Size(an * 0.42f, alto))
        drawRect(t.s(0.24f), Offset(x(0.82f, 0.18f), y0), Size(an * 0.18f, alto))
        drawRect(t.s(0.4f), Offset(if (luzDe < 0) x1 - 0.9f * u else x0, y0), Size(0.9f * u, alto))
        if (t.a.lumbre) drawRect(t.f(0.7f), Offset(if (luzDe > 0) x1 - 1.1f * u else x0, y0), Size(1.1f * u, alto))
    }

    /** La marca del pie: de día una florecilla de hierro de dorar; en los Sueños, la luna creciente. */
    private fun DrawScope.marca(c: Offset, r: Float, luna: Boolean, color: Color, fondo: Color) {
        if (luna) {
            drawCircle(color, r, c)
            drawCircle(fondo, r * 0.84f, Offset(c.x + r * 0.42f, c.y - r * 0.24f))
        } else {
            val p = Path()
            for (j in 0 until 4) {
                val an = j * 1.5708f
                val q = Offset(c.x + r * 0.55f * kotlin.math.cos(an), c.y + r * 0.55f * kotlin.math.sin(an))
                p.addOval(Rect(q.x - r * 0.38f, q.y - r * 0.38f, q.x + r * 0.38f, q.y + r * 0.38f))
            }
            drawPath(p, color)
            drawCircle(fondo, r * 0.22f, c)
        }
    }

    /** Un papel pegado o metido donde va el día: su comba (luz a un lado, sombra al otro) y la sombra que deja. */
    private fun DrawScope.papelito(r: Rect, t: TonoRenacimiento, a: AireRenacimiento, k: Float, luzDe: Float) {
        val u = density
        drawRect(a.sombra(0.35f), Offset(r.left + 0.9f * u, r.top + 1.1f * u), r.size)
        drawRect(a.etiqueta(k), r.topLeft, r.size)
        drawRect(t.s(0.1f), Offset(if (luzDe < 0) r.right - r.width * 0.22f else r.left, r.top), Size(r.width * 0.22f, r.height))
        drawRect(t.b(0.3f), Offset(if (luzDe < 0) r.left else r.right - 0.8f * u, r.top), Size(0.8f * u, r.height))
    }

    // ---------------------------------------------------------------- 0: el libro en piel teñida

    fun DrawScope.libroPiel(v: Volumen, tono: Color?, medidor: TextMeasurer, a: AireRenacimiento, k: Float, luzDe: Float, dia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val t = a.en(k)
        // Los registros: tiras de papel y una cinta de seda que asoman por la cabeza, más cuanto más escribiste.
        val cabeza = h * 0.05f
        val registros = (v.lleno * 3.2f).toInt()
        for (j in 0 until registros) {
            val x = w * (0.2f + 0.24f * j + 0.08f * azar(s, 20 + j))
            val alto = cabeza * (0.7f + 0.5f * azar(s, 24 + j))
            val col = if (j == 1) t.c(P.VERDE_FONDO) else a.etiqueta(k * 0.9f)
            drawRect(col, Offset(x, cabeza - alto), Size(2.8f * u, alto + 2f * u))
            drawRect(t.s(0.3f), Offset(x + 2.1f * u, cabeza - alto), Size(0.7f * u, alto + 2f * u))
        }
        val piel = lejos(materia(tono, a, k, 0.26f), dia)
        val x0 = 0.9f * u; val x1 = w - 0.9f * u
        val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(x0, cabeza, x1, h), CornerRadius(2.4f * u))) }
        drawPath(cuerpo, piel)
        clipPath(cuerpo) {
            cilindro(x0, x1, cabeza, h, t, luzDe)
            // El roce de la piel: la cabeza y el pie gastados, un par de rayas.
            drawRect(mezcla(piel, P.PAPEL_OSC, 0.3f).copy(alpha = 0.55f), Offset(x0, cabeza), Size(x1 - x0, 1.6f * u))
            drawRect(mezcla(piel, P.PAPEL_OSC, 0.3f).copy(alpha = 0.5f), Offset(x0, h - 2.2f * u), Size(x1 - x0, 1.2f * u))
            for (j in 0 until 2) {
                val y = h * (0.44f + 0.28f * azar(s, 30 + j))
                drawLine(t.s(0.2f), Offset(x0 + w * 0.22f, y), Offset(x0 + w * (0.42f + 0.2f * azar(s, 33 + j)), y + 1.2f * u), 0.5f * u)
            }
        }
        // Los nervios, que sobresalen del lomo, con su luz, su sombra y sus filetes dorados.
        val oro = t.c(P.ORO)
        val nervios = floatArrayOf(0.1f, 0.3f, 0.78f, 0.9f)
        val filetes = Path()
        nervios.forEach { f ->
            val y = cabeza + (h - cabeza) * f
            drawRoundRect(t.s(0.4f), Offset(0f, y + 1.3f * u), Size(w, 2.8f * u), CornerRadius(1.4f * u))
            drawRoundRect(mezcla(piel, a.hondo, 0.12f), Offset(0f, y - 1.5f * u), Size(w, 3f * u), CornerRadius(1.5f * u))
            drawRect(t.b(0.45f), Offset(w * 0.1f, y - 1.3f * u), Size(w * 0.7f, 0.8f * u))
            filetes.addRect(Rect(x0 + 1.5f * u, y - 2.9f * u, x1 - 1.5f * u, y - 2.4f * u))
            filetes.addRect(Rect(x0 + 1.5f * u, y + 2.4f * u, x1 - 1.5f * u, y + 2.9f * u))
        }
        drawPath(filetes, oro.copy(alpha = 0.85f))
        drawRect(t.f(0.8f), Offset(0f, cabeza), Size(w, 1f * u))
        val yn = { j: Int -> cabeza + (h - cabeza) * nervios[j] }
        // El tejuelo de papel con el día, entre los dos nervios de arriba.
        val tej = Rect(w * 0.14f, yn(0) + 4.2f * u, w * 0.86f, yn(1) - 4.2f * u)
        if (tej.height > 4f * u) {
            papelito(tej, t, a, k, luzDe)
            textoEn(medidor, v.dia, tej, P.LETRA, a.tinta, 15f, peso = FontWeight.Bold, minimo = 7f)
        }
        // El mes, a lo largo del lomo, en su cartela de filetes: en oro sobre la piel oscura, en tinta sobre la clara.
        val mesR = Rect(x0 + 2.2f * u, yn(1) + 4.4f * u, x1 - 2.2f * u, yn(2) - 4.4f * u)
        if (mesR.height > 8f * u) {
            drawRect(oro.copy(alpha = 0.6f), mesR.topLeft, mesR.size, style = Stroke(0.5f * u))
            // De noche el oro del rótulo coge algo de la lucerna aunque esté lejos, para que el mes se siga leyendo.
            val sobreClaro = claridad(piel) > 0.42f
            val letraMes = if (sobreClaro) mezcla(P.IMPRENTA, P.SEPIA, 0.5f) else a.en(maxOf(k, 0.5f)).c(mezcla(P.ORO_LUZ, P.ORO, 0.3f))
            textoEn(medidor, v.mes, mesR.deflate(1.2f * u), P.LETRA, letraMes, 10f, vertical = true, peso = FontWeight.SemiBold, minimo = 5f)
        }
        // Al pie, la florecilla dorada (en los Sueños, la luna).
        val mc = Offset(w / 2, (yn(2) + yn(3)) / 2)
        marca(mc, minOf(w * 0.17f, (yn(3) - yn(2)) * 0.3f), v.noche, if (v.noche) mezcla(oro, P.LUNA, 0.55f) else oro, piel)
        drawRect(t.s(0.45f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    }

    // ---------------------------------------------------------------- 1: los pliegos atados

    fun DrawScope.pliegos(v: Volumen, tono: Color?, medidor: TextMeasurer, a: AireRenacimiento, k: Float, luzDe: Float, dia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val t = a.en(k)
        // Los cantos de los pliegos, que asoman por arriba de la envoltura (más cuanto más escribiste).
        val asoma = h * 0.1f
        val hojas = 3 + (v.lleno * 5).toInt()
        drawRect(t.c(P.PAPEL_OSC), Offset(w * 0.04f, 0f), Size(w * 0.92f, asoma + 2f * u))
        val cantos = Path()
        for (j in 0 until hojas) {
            val y = asoma * (j + 0.5f) / hojas
            val dx = (azar(s, 40 + j) - 0.5f) * 2.4f * u
            cantos.addRect(Rect(w * 0.05f + dx, y - 0.45f * u, w * 0.95f + dx, y + 0.45f * u))
        }
        drawPath(cantos, a.etiqueta(k * 0.95f))
        // La envoltura de papel de color: el borde de arriba irregular, la cara de arriba, los pliegues de las esquinas.
        val papel = lejos(materia(tono, a, k, 0.18f), dia)
        val env = Path().apply {
            moveTo(0f, h)
            lineTo(0f, asoma + 1.4f * u)
            var x = 0f
            var j = 0
            while (x < w && j < 12) {
                x = minOf(w, x + maxOf(2f * u, w * 0.2f))
                lineTo(x, asoma + (azar(s, 50 + j) - 0.3f) * 2.4f * u)
                j++
            }
            lineTo(w, h); close()
        }
        drawPath(env, papel)
        clipPath(env) {
            drawRect(t.b(0.3f), Offset(0f, asoma - 1f * u), Size(w, h * 0.05f))
            drawPath(poligonoRenacimiento(0f, h, w * 0.3f, h, 0f, h - w * 0.38f), t.s(0.16f))
            drawPath(poligonoRenacimiento(w, h, w * 0.7f, h, w, h - w * 0.38f), t.s(0.24f))
            drawLine(t.b(0.3f), Offset(0f, h - w * 0.38f), Offset(w * 0.3f, h), 0.7f * u)
            val fibras = Path()
            for (j in 0 until 4) { val y = asoma + (h - asoma) * (0.2f + 0.2f * j); fibras.addRect(Rect(w * 0.05f, y, w * (0.3f + 0.5f * azar(s, 60 + j)), y + 0.4f * u)) }
            drawPath(fibras, t.s(0.1f))
            cilindro(0f, w, asoma, h, t, luzDe, 0.7f)
        }
        // La cédula de papel, metida bajo el cordel, con el día y el mes y la esquina doblada.
        val ced = Rect(w * 0.25f, h * 0.19f, w * 0.75f, h * 0.76f)
        rotate((azar(s, 5) - 0.5f) * 3f, ced.center) {
            papelito(ced, t, a, k, luzDe)
            drawPath(poligonoRenacimiento(ced.right, ced.bottom - ced.width * 0.28f, ced.right, ced.bottom, ced.right - ced.width * 0.28f, ced.bottom), papel)
            drawPath(poligonoRenacimiento(ced.right, ced.bottom - ced.width * 0.28f, ced.right - ced.width * 0.28f, ced.bottom, ced.right - ced.width * 0.24f, ced.bottom - ced.width * 0.24f), mezcla(a.etiqueta(k), P.PAPEL_OSC, 0.5f))
            val diaR = Rect(ced.left, ced.top + ced.height * 0.12f, ced.right, ced.top + ced.height * 0.44f)
            textoEn(medidor, v.dia, diaR, P.LETRA, a.tinta, 14f, peso = FontWeight.Bold, minimo = 7f)
            drawLine(t.c(P.RUBRICA).copy(alpha = 0.75f), Offset(ced.left + ced.width * 0.2f, diaR.bottom), Offset(ced.right - ced.width * 0.2f, diaR.bottom), 0.7f * u)
            val mesR = Rect(ced.left + 0.5f * u, diaR.bottom + 1.5f * u, ced.right - 0.5f * u, ced.bottom - ced.width * 0.2f)
            if (mesR.height > 6f * u) textoEn(medidor, v.mes, mesR, P.LETRA, a.tinta.copy(alpha = 0.9f), 9.5f, vertical = true, minimo = 4.5f)
        }
        // El bramante: una vuelta a lo ancho, por encima de la cédula, y otra a lo alto cerca del canto; el nudo y su lacre.
        val cana = t.c(P.CANAMO)
        val yc = h * 0.25f
        val xv = w * 0.88f
        drawLine(t.s(0.35f), Offset(0f, yc + 1.3f * u), Offset(w, yc + 1.5f * u), 1.4f * u)
        drawLine(cana, Offset(0f, yc), Offset(w, yc + 0.4f * u), 1.4f * u)
        drawLine(t.b(0.4f), Offset(0f, yc - 0.4f * u), Offset(w, yc), 0.5f * u)
        drawLine(cana, Offset(xv, 0f), Offset(xv + 0.4f * u, h), 1.3f * u)
        val nudo = Offset(w * 0.3f, yc)
        val rs = minOf(w * 0.16f, h * 0.08f)
        drawLine(cana, nudo, nudo + Offset(-1.6f * u, rs * 2.6f), 1.1f * u, StrokeCap.Round)
        drawLine(cana, nudo, nudo + Offset(2.2f * u, rs * 2.2f), 1.1f * u, StrokeCap.Round)
        val lacre = t.c(P.LACRE)
        drawCircle(t.s(0.45f), rs * 1.05f, nudo + Offset(0.9f * u, 1.1f * u))
        drawCircle(mezcla(lacre, a.hondo, 0.25f), rs * 1.08f, nudo)
        drawCircle(lacre, rs * 0.86f, nudo)
        if (v.noche) marca(nudo, rs * 0.55f, true, mezcla(lacre, P.LUNA, 0.6f), lacre)
        else {
            val sello = mezcla(lacre, a.hondo, 0.4f)
            for (j in 0 until 3) {
                val an = j * 1.047f
                val d = Offset(rs * 0.55f * kotlin.math.cos(an), rs * 0.55f * kotlin.math.sin(an))
                drawLine(sello, nudo - d, nudo + d, 0.8f * u)
            }
        }
        destelloRenacimiento(nudo - Offset(rs * 0.38f, rs * 0.38f), rs * 0.22f, t)
        drawRect(t.s(0.45f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    }

    // ---------------------------------------------------------------- 2: el librito en vitela floja

    fun DrawScope.librito(v: Volumen, tono: Color?, medidor: TextMeasurer, a: AireRenacimiento, k: Float, luzDe: Float, dia: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val t = a.en(k)
        val ladeo = if (azar(s, 7) < 0.5f) -1f else 1f
        val cabeza = h * 0.07f
        // Las hojas sueltas metidas que asoman por la cabeza (más cuanto más escribiste).
        val sueltas = (v.lleno * 3.4f).toInt()
        for (j in 0 until sueltas) {
            val x = w * (0.18f + 0.22f * j)
            rotate((azar(s, 60 + j) - 0.5f) * 16f, Offset(x, cabeza * 2f)) {
                drawRect(a.etiqueta(k * 0.92f), Offset(x, cabeza * (0.1f + 0.4f * azar(s, 64 + j))), Size(w * 0.26f, cabeza * 2f))
                drawRect(t.s(0.25f), Offset(x + w * 0.22f, cabeza * 0.4f), Size(w * 0.04f, cabeza * 1.6f))
            }
        }
        // La cubierta: vencida hacia un lado, con la cabeza hundida en el medio y los bordes blandos.
        val inc = ladeo * minOf(w * 0.12f, 3.5f * u)
        val izq = if (ladeo > 0) 0.6f * u else -inc + 0.6f * u
        val der = if (ladeo > 0) w - inc - 0.6f * u else w - 0.6f * u
        val cuerpo = Path().apply {
            moveTo(izq, h)
            quadraticTo(izq - 0.8f * u, h * 0.55f, izq + inc, cabeza + 1.2f * u)
            quadraticTo((izq + der) / 2 + inc, cabeza + h * 0.04f, der + inc, cabeza)
            quadraticTo(der + 0.9f * u, h * 0.5f, der, h)
            close()
        }
        val vitela = lejos(materia(tono, a, k, 0.12f), dia)
        drawPath(Path().apply { addPath(cuerpo, Offset(1.1f * u, 0.7f * u)) }, t.s(0.35f))
        drawPath(cuerpo, vitela)
        clipPath(cuerpo) {
            cilindro(0f, w, cabeza, h, t, luzDe, 0.8f)
            // Los pliegues blandos de la vitela: cada uno con su luz y su sombra.
            val sombras = Path(); val luces = Path()
            for (j in 0 until 3) {
                val x = w * (0.25f + 0.25f * j) + (azar(s, 70 + j) - 0.5f) * 2f * u
                sombras.moveTo(x + inc, cabeza); sombras.lineTo(x, h)
                luces.moveTo(x + inc + 0.9f * u, cabeza); luces.lineTo(x + 0.9f * u, h)
            }
            drawPath(sombras, t.s(0.18f), style = Stroke(0.8f * u))
            drawPath(luces, t.b(0.22f), style = Stroke(0.6f * u))
            // La cubierta que se dobla en la cabeza: el labio claro y su sombra.
            drawRect(mezcla(vitela, P.PAPEL, 0.32f), Offset(0f, cabeza - 1f * u), Size(w, h * 0.035f))
            drawRect(t.s(0.25f), Offset(0f, cabeza + h * 0.03f), Size(w, 1f * u))
            // Las correíllas de piel blanca que pasan por el lomo, con los ojales por donde entran.
            listOf(0.38f, 0.62f, 0.86f).forEach { f ->
                val y = h * f
                val corr = t.c(mezcla(P.PAPEL, P.CANAMO, 0.22f))
                drawRect(t.s(0.4f), Offset(0f, y + 1.5f * u), Size(w, 1f * u))
                drawRect(corr, Offset(w * 0.1f, y - 1.6f * u), Size(w * 0.8f, 3.1f * u))
                drawRect(t.b(0.4f), Offset(w * 0.12f, y - 1.6f * u), Size(w * 0.76f, 0.7f * u))
                drawRect(t.s(0.55f), Offset(w * 0.08f, y - 1.8f * u), Size(1.3f * u, 3.5f * u))
                drawRect(t.s(0.55f), Offset(w * 0.92f - 1.3f * u, y - 1.8f * u), Size(1.3f * u, 3.5f * u))
            }
        }
        // El papelito pegado con el día, arriba, y el mes escrito a pluma sobre la vitela.
        val pap = Rect(w * 0.15f + inc * 0.6f, h * 0.12f, w * 0.85f + inc * 0.6f, h * 0.33f)
        papelito(pap, t, a, k, luzDe)
        textoEn(medidor, v.dia, pap, P.LETRA, a.tinta, 14.5f, peso = FontWeight.Bold, minimo = 7f)
        val sobreClaro = claridad(vitela) > 0.42f
        val pluma = if (sobreClaro) mezcla(P.IMPRENTA, P.SEPIA, 0.5f) else a.etiqueta(maxOf(k, 0.3f)).copy(alpha = 0.95f)
        val franja = Rect(w * 0.1f + inc * 0.4f, h * 0.405f, w * 0.9f + inc * 0.4f, h * 0.595f)
        if (franja.height > 6f * u) textoEn(medidor, v.mes, franja, P.LETRA, pluma, 9f, vertical = true, minimo = 4.5f)
        // Entre la segunda y la tercera correílla: la luna de los Sueños o una prueba de pluma.
        val pc = Offset(w * 0.5f + inc * 0.2f, h * 0.74f)
        if (v.noche) marca(pc, minOf(w * 0.16f, h * 0.04f), true, pluma, vitela)
        else drawArc(pluma.copy(alpha = 0.6f), 20f, 290f, false, Offset(pc.x - w * 0.14f, pc.y - h * 0.022f), Size(w * 0.28f, h * 0.044f), style = Stroke(0.7f * u))
        drawRect(t.s(0.45f), Offset(0f, h - 1.3f * u), Size(w, 1.3f * u))
    }
}
