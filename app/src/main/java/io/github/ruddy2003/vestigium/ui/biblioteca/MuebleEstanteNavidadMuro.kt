package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * La pared del estante de Navidad ([MuebleEstanteNavidad]): la de la cocina del Ideario. Arriba, la pintura verdosa apagada con
 * las manos de pintura, el vaho y el tizne de la vela; la cenefa de azulejos verdes con su raya clara; debajo, el azulejo crema
 * cuadrado con sus juntas, su vidriado y alguno cuarteado. Arriba a la izquierda, la ventana de la cocina en su hueco (el derrame
 * enlucido, el marco de dos hojas pintado de crema y gastado, la falleba, el alféizar), con la escarcha en las esquinas de los
 * vidrios, la nieve amontonada afuera y la rama de un abeto nevado: de día, el sol de la tarde deja en la pared la mancha de sus
 * vidrios; de noche, la nieve azul de afuera; en los Sueños, el ocaso o la luna. Y lo colgado: las cadenetas de papel en festón y
 * la naranja con clavos colgada de la falleba.
 */
internal object MuroEstanteNavidad {
    private val P = PaletaNavidad

    /** La cenefa: su borde de arriba y de abajo, en anchos (encima, la pintura; debajo, el azulejo). */
    const val CENEFA0 = 0.215f
    const val CENEFA1 = 0.238f
    /** La ventana: el hueco en la pared (izquierda, arriba, derecha, abajo), en anchos; la izquierda queda fuera del cuadro. */
    const val V_IZQ = -0.03f
    const val V_ARR = 0.05f
    const val V_DER = 0.255f
    const val V_ABA = 0.405f
    /** El derrame de la derecha, el dintel que se ve y el ancho del marco, en anchos. */
    private const val DERRAME = 0.026f
    private const val DINTEL = 0.014f
    private const val MARCO = 0.015f
    /** El lado del azulejo, en anchos (el del Ideario). */
    private const val AZ = 0.062f

    // ---------------------------------------------------------------- la pared entera (arriba)

    fun DrawScope.pared(a: AireNavidad, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        val fondo = Rect(0f, 0f, w, apoyo + w * 0.02f)
        muro(fondo, a, cerca, null)
        // La mancha de la ventana: la misma pared, alumbrada, dentro de los vidrios (en la penumbra, a medias).
        if (m != null) {
            clipRect(w * V_DER, fondo.top, fondo.right, fondo.bottom) {
                val medio = ManchaNavidad(m.q, m.color, m.fuerza * 0.42f)
                clipPath(m.forma(w, 0f, 0.012f)) { muro(fondo, a, cerca, medio) }
                clipPath(m.forma(w)) { muro(fondo, a, cerca, m); sombrasEnMancha(a, m) }
            }
        }
        ventana(a, cerca, m)
    }

    /** La pintura, la cenefa y el azulejo de [r], a la luz del momento (y, con [m], dentro de la mancha de la ventana). */
    private fun DrawScope.muro(r: Rect, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        val c0 = w * CENEFA0; val c1 = w * CENEFA1
        pintado(Rect(r.left, r.top, r.right, c0 + 1f), a, cerca, m)
        manos(Rect(r.left, r.top, r.right, c0), a)
        // La cenefa: una hilera de azulejos verdes con su raya clara y su brillo.
        val lc = w * 0.074f
        var x = r.left - w * 0.02f
        var j = 0
        while (x < r.right && j < 60) {
            val k = cerca(Offset(x + lc / 2, (c0 + c1) / 2))
            drawRect(a.en(mezcla(P.FILETE, P.SALVIA, 0.12f * (j % 3)), 0f, k, lit, m), Offset(x, c0), Size(lc - 1.2f * u, c1 - c0))
            drawRect(a.en(P.PAPEL, 0f, k, lit, m).copy(alpha = 0.5f), Offset(x, c0 + (c1 - c0) * 0.46f), Size(lc - 1.2f * u, 1f * u))
            if (!a.oscuro) drawRect(a.claro(0.45f), Offset(x + 1.5f * u, c0 + 1.5f * u), Size(lc * 0.4f, 1f * u))
            x += lc; j++
        }
        drawRect(a.sombra(0.3f), Offset(r.left, c1), Size(r.width, 1.3f * u))
        // Las juntas, debajo de todo el azulejo; después cada azulejo con su tono.
        val kJ = cerca(Offset(w * 0.5f, (c1 + r.bottom) / 2))
        drawRect(a.en(P.JUNTA, 0f, kJ, lit, m), Offset(r.left, c1 + 1.3f * u), Size(r.width, r.bottom - c1))
        val lt = w * AZ
        var yy = c1 + 1.3f * u
        var fy = 0
        while (yy < r.bottom && fy < 40) {
            var xx = r.left - lt * 0.3f
            var fx = 0
            while (xx < r.right && fx < 40) {
                val c = Offset(xx + lt / 2, yy + lt / 2)
                val k = cerca(c)
                val tono = mezcla(P.AZULEJO, if ((fx + fy) % 3 == 0) P.PINTURA else P.PAPEL, 0.22f * azar(9010 + fy, fx))
                val alto = minOf(lt, r.bottom - yy) - 1f * u
                if (alto > 0f) {
                    drawRect(a.en(tono, 0f, k, lit, m), Offset(xx, yy), Size(lt - 1f * u, alto))
                    // El vidriado: un brillo corto arriba a la izquierda, y la sombra de la junta de arriba.
                    if (!a.oscuro) drawRect(a.claro(if (lit > 0f) 0.5f else 0.3f), Offset(xx + lt * 0.12f, yy + lt * 0.12f), Size(lt * 0.34f, 1.1f * u))
                    else if (k > 0.3f) drawRect(a.brillo(0.35f, k), Offset(xx + lt * 0.12f, yy + lt * 0.12f), Size(lt * 0.34f, 1f * u))
                    if (azar(9011, fx * 31 + fy) < 0.06f) drawLine(a.sombra(0.14f), Offset(xx + lt * 0.2f, yy + lt * 0.55f), Offset(xx + lt * 0.62f, yy + lt * 0.72f), 0.6f * u)
                }
                xx += lt; fx++
            }
            yy += lt; fy++
        }
        // El azulejo de abajo, ennegrecido junto a la cornisa, y alguna gota de grasa.
        drawRect(a.sombra(0.1f), Offset(r.left, r.bottom - w * 0.03f), Size(r.width, w * 0.03f))
        drawOval(Color(0xFFC8A060).copy(alpha = 0.14f), Offset(w * 0.33f, r.bottom - w * 0.1f), Size(w * 0.03f, w * 0.018f), blendMode = BlendMode.Multiply)
    }

    /**
     * La pintura de arriba, lisa a la luz del momento: de día, algo más honda bajo el techo y más clara hacia la ventana; de noche
     * (y al ocaso), en franjas con su degradado de arriba abajo según la luz que llega a cada punto (en la imagen guardada).
     */
    private fun DrawScope.pintado(r: Rect, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        val lit = if (m != null) 1f else 0f
        if (!a.lumbre) {
            val c0 = a.en(mezcla(P.PINTURA, P.PINTURA_SOMBRA, 0.3f), 0f, 1f, lit, m)
            val c1 = a.en(P.PINTURA, 0f, 1f, lit, m)
            drawRect(Brush.verticalGradient(listOf(c0, c1, c1), r.top, r.bottom), r.topLeft, r.size)
            if (m == null && !a.noche) mancha(Offset(w * 0.18f, -w * 0.02f), w * 0.75f, a.claro(0.26f))
            return
        }
        val n = 48
        val ys = FloatArray(14) { i -> r.top + r.height * i / 13f }
        for (t in 0 until n) {
            val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1f
            val cols = Array(ys.size) { i -> (i / 13f) to a.en(mezcla(P.PINTURA, P.PINTURA_SOMBRA, 0.25f * (1f - i / 13f)), 0f, cerca(Offset((x0 + x1) / 2, ys[i])), lit, m) }
            drawRect(Brush.verticalGradient(*cols, startY = r.top, endY = r.bottom), Offset(x0, r.top), Size(x1 - x0, r.height))
        }
    }

    /**
     * Lo que tiene la pintura (con multiplicar, así sigue la luz que tiene debajo): las manos que no quedaron parejas, los brochazos,
     * el vaho del puchero que subió en una nube y el tizne de la vela sobre la palmatoria.
     */
    private fun DrawScope.manos(r: Rect, a: AireNavidad) {
        val w = size.width
        val motas = Path()
        for (j in 0 until 14) {
            val mx = r.left + r.width * azar(9050, j); val my = r.top + r.height * azar(9051, j)
            val mw = w * (0.07f + 0.12f * azar(9052, j)); val mh = mw * (0.3f + 0.3f * azar(9053, j))
            motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
        }
        drawPath(motas, Color(0xFFE6E2C8).copy(alpha = 0.22f), blendMode = BlendMode.Multiply)
        val brochazos = Path()
        for (j in 0 until 34) {
            val bx = r.left + r.width * azar(9060, j); val by = r.top + r.height * (0.08f + 0.9f * azar(9061, j))
            val bl = w * (0.05f + 0.09f * azar(9062, j))
            brochazos.moveTo(bx, by); brochazos.quadraticTo(bx + bl * 0.5f, by + w * 0.004f, bx + bl * 0.9f, by + w * 0.014f)
        }
        drawPath(brochazos, Color(0xFFDCD8BC).copy(alpha = 0.34f), style = Stroke(w * 0.005f, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        // El cerco de la humedad en la esquina de arriba a la derecha.
        drawOval(Color(0xFFC9C3A0).copy(alpha = 0.35f), Offset(w * 0.84f, -w * 0.05f), Size(w * 0.22f, w * 0.11f), blendMode = BlendMode.Multiply)
        drawOval(Color(0xFFB9B28E).copy(alpha = 0.35f), Offset(w * 0.84f, -w * 0.05f), Size(w * 0.22f, w * 0.11f), style = Stroke(1.2f * density), blendMode = BlendMode.Multiply)
        // El tizne de la vela: una lengua parda que sube de la palmatoria.
        val lx = w * MuebleEstanteNavidad.VELA_X
        val humo = Path().apply {
            moveTo(lx - w * 0.008f, w * 0.33f)
            cubicTo(lx - w * 0.03f, w * 0.26f, lx - w * 0.05f, w * 0.2f, lx - w * 0.035f, w * 0.13f)
            lineTo(lx + w * 0.055f, w * 0.13f)
            cubicTo(lx + w * 0.05f, w * 0.2f, lx + w * 0.03f, w * 0.27f, lx + w * 0.008f, w * 0.33f); close()
        }
        clipRect(r.left, r.top, r.right, w * 0.34f) {
            drawPath(humo, Color(0xFF8A8070).copy(alpha = 0.12f), blendMode = BlendMode.Multiply)
        }
    }

    // ---------------------------------------------------------------- la ventana

    private fun DrawScope.ventana(a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val o = Rect(w * V_IZQ, w * V_ARR, w * V_DER, w * V_ABA)
        val k = cerca(o.center)
        val d = w * DERRAME; val dt = w * DINTEL
        // El hueco: el fondo del derrame, el dintel en sombra y el lado de la derecha, que mira a la luz (de día, al sol).
        drawRect(a.dia(P.PINTURA_SOMBRA, -0.15f, k), o.topLeft, o.size)
        val ladoLuz = if (m != null && !a.oscuro) a.sol(P.PINTURA, a.dia(P.PINTURA, 0f, k), m.fuerza * 0.85f) else a.dia(P.PINTURA, -0.06f, k)
        drawPath(poligonoNavidad(o.right, o.top, o.right, o.bottom, o.right - d, o.bottom - dt * 0.5f, o.right - d, o.top + dt), ladoLuz)
        drawPath(poligonoNavidad(o.left, o.top, o.right, o.top, o.right - d, o.top + dt, o.left, o.top + dt), a.dia(P.PINTURA_SOMBRA, -0.3f, k))
        drawRect(a.sombra(0.3f), Offset(o.right - d, o.top + dt), Size(1.2f * u, o.height - dt * 1.5f))
        // El marco: el cerco y las dos hojas, pintados de crema, con su canto de luz y su sombra.
        val g = Rect(o.left, o.top + dt, o.right - d, o.bottom - dt * 0.5f)
        val crema = a.dia(P.CREMA, 0f, k)
        drawRect(crema, g.topLeft, g.size)
        val mf = w * MARCO
        val vid = Rect(g.left + mf, g.top + mf, g.right - mf, g.bottom - mf * 1.3f)
        val xm = vid.left + vid.width * 0.48f
        val ym = vid.top + vid.height * 0.5f
        val bar = mf * 0.55f
        val vidrios = listOf(
            Rect(vid.left, vid.top, xm - bar, ym - bar * 0.7f), Rect(xm + bar, vid.top, vid.right, ym - bar * 0.7f),
            Rect(vid.left, ym + bar * 0.7f, xm - bar, vid.bottom), Rect(xm + bar, ym + bar * 0.7f, vid.right, vid.bottom),
        )
        val cristal = Path().apply { vidrios.forEach { addRect(it) } }
        clipPath(cristal) { afuera(vid, a) }
        vidrios.forEachIndexed { i, v -> escarcha(v, a, 9100 + i) }
        // Las sombras del marco en el vidrio (el canto de dentro de cada hoja) y los filetes de luz.
        vidrios.forEach { v ->
            drawRect(a.sombra(0.3f), v.topLeft, Size(v.width, 1.6f * u))
            drawRect(a.sombra(0.22f), Offset(v.right - 1.4f * u, v.top), Size(1.4f * u, v.height))
            drawRect(a.brillo(0.5f, k), Offset(v.left - 1.2f * u, v.top), Size(1.2f * u, v.height))
            drawRect(a.brillo(0.35f, k), Offset(v.left, v.bottom), Size(v.width, 1.1f * u))
        }
        drawRect(a.brillo(0.45f, k), g.topLeft, Size(g.width, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(g.left, g.bottom - 1.4f * u), Size(g.width, 1.4f * u))
        drawLine(a.sombra(0.35f), Offset(xm, vid.top), Offset(xm, vid.bottom), 0.9f * u)
        // La pintura saltada en el marco: asoma el pino.
        listOf(Offset(xm + bar * 0.6f, ym + w * 0.06f), Offset(g.right - mf * 0.5f, g.bottom - w * 0.02f), Offset(xm - bar * 0.4f, vid.top + w * 0.03f)).forEachIndexed { i, p ->
            saltado(p, w * (0.006f + 0.003f * i), 9120 + i, a, k)
        }
        // La falleba: la barra de latón en la junta de las hojas y su manilla.
        val kl = maxOf(k, if (a.oscuro) 0f else 0.5f)
        drawRect(a.sombra(0.35f), Offset(xm + 1.2f * u, vid.top + w * 0.01f), Size(w * 0.006f, vid.height - w * 0.02f))
        drawRect(a.luz(P.LATON, kl), Offset(xm - w * 0.003f, vid.top + w * 0.01f), Size(w * 0.006f, vid.height - w * 0.02f))
        drawRect(a.brillo(0.6f, kl), Offset(xm - w * 0.003f, vid.top + w * 0.01f), Size(1f * u, vid.height - w * 0.02f))
        val manilla = Offset(xm, ym)
        drawCircle(a.luz(P.LATON_OSC, kl), w * 0.008f, manilla)
        drawLine(a.luz(P.LATON, kl), manilla, manilla + Offset(w * 0.006f, w * 0.03f), w * 0.006f, StrokeCap.Round)
        drawCircle(a.brillo(0.7f, kl), w * 0.003f, manilla - Offset(w * 0.002f, w * 0.002f))
        // El alféizar de dentro: la tabla pintada, su canto y su sombra en la pared.
        val al = Rect(o.left - w * 0.01f, o.bottom - w * 0.008f, o.right + w * 0.014f, o.bottom + w * 0.012f)
        drawRect(a.sombra(0.3f), Offset(al.left, al.bottom), Size(al.width + w * 0.006f, w * 0.014f))
        drawRect(a.dia(P.CREMA, 0.08f, k), al.topLeft, Size(al.width, al.height * 0.4f))
        drawRect(a.dia(P.CREMA, -0.08f, k), Offset(al.left, al.top + al.height * 0.4f), Size(al.width, al.height * 0.6f))
        drawRect(a.brillo(0.5f, k), al.topLeft, Size(al.width, 1f * u))
        saltado(Offset(al.right - w * 0.02f, al.bottom - w * 0.004f), w * 0.007f, 9130, a, k)
    }

    /** La pintura saltada en un canto: asoma el pino, con el borde de la pintura en sombra. */
    fun DrawScope.saltado(p: Offset, r: Float, semilla: Int, a: AireNavidad, k: Float) {
        val pr = Path()
        for (j in 0 until 7) {
            val an = 6.283f * j / 7 + azar(semilla, j) * 0.6f
            val rr = r * (0.55f + 0.5f * azar(semilla, j + 9))
            val q = Offset(p.x + rr * cos(an) * 1.6f, p.y + rr * sin(an) * 0.5f)
            if (j == 0) pr.moveTo(q.x, q.y) else pr.lineTo(q.x, q.y)
        }
        pr.close()
        drawPath(pr, a.dia(P.PINO, 0f, k))
        drawPath(Path().apply { op(pr, corridoNavidad(pr, r * 0.25f, r * 0.25f), PathOperation.Difference) }, a.sombra(0.35f))
    }

    /**
     * Lo de afuera, detrás de los vidrios ([v]: todo el vidrio): el cielo de la tarde de invierno, la nieve amontonada en el
     * alféizar de afuera, la rama nevada de un abeto que entra por arriba y los copos. De noche, la nieve azul; en los Sueños, el
     * ocaso melocotón o la luna.
     */
    private fun DrawScope.afuera(v: Rect, a: AireNavidad) {
        val w = size.width; val u = density
        val ocaso = a.noche && !a.oscuro
        val luna = a.noche && a.oscuro
        val alto: Color; val bajo: Color; val nieve: Color; val nieveSombra: Color
        when {
            ocaso -> { alto = Color(0xFF7A6F9E); bajo = Color(0xFFF4BE96); nieve = Color(0xFFF6DCCF); nieveSombra = Color(0xFFB59CB8) }
            luna -> { alto = Color(0xFF0B1636); bajo = Color(0xFF223A62); nieve = Color(0xFF9AB2CE); nieveSombra = Color(0xFF4A5F84) }
            a.oscuro -> { alto = P.CIELO_NOCHE_ALTO; bajo = P.CIELO_NOCHE; nieve = P.NIEVE_NOCHE; nieveSombra = Color(0xFF2C3E58) }
            else -> { alto = P.CIELO_ALTO; bajo = P.CIELO; nieve = P.NIEVE; nieveSombra = P.NIEVE_SOMBRA }
        }
        drawRect(Brush.verticalGradient(listOf(alto, bajo), v.top, v.bottom), v.topLeft, v.size)
        // La loma nevada de lejos y, de noche, una ventana encendida en la casa de enfrente.
        val loma = Path().apply {
            moveTo(v.left, v.top + v.height * 0.7f)
            cubicTo(v.left + v.width * 0.3f, v.top + v.height * 0.6f, v.left + v.width * 0.6f, v.top + v.height * 0.74f, v.right, v.top + v.height * 0.64f)
            lineTo(v.right, v.bottom); lineTo(v.left, v.bottom); close()
        }
        drawPath(loma, mezcla(nieve, alto, 0.3f))
        if (luna) {
            // La luna en el vidrio de arriba a la derecha: la media luna con su halo frío.
            val c = Offset(v.left + v.width * 0.8f, v.top + v.height * 0.2f)
            mancha(c, w * 0.06f, P.LUNA.copy(alpha = 0.25f))
            val r = w * 0.018f
            drawPath(Path().apply {
                op(Path().apply { addOval(Rect(c, r)) }, Path().apply { addOval(Rect(c + Offset(r * 0.5f, -r * 0.25f), r * 0.86f)) }, PathOperation.Difference)
            }, P.LUNA)
        }
        // La nieve amontonada en el alféizar de afuera: un lomo blando con su sombra azul.
        val y0 = v.bottom - v.height * 0.16f
        val monton = Path().apply {
            moveTo(v.left, v.bottom)
            lineTo(v.left, y0 + v.height * 0.05f)
            cubicTo(v.left + v.width * 0.25f, y0 - v.height * 0.05f, v.left + v.width * 0.45f, y0 + v.height * 0.03f, v.left + v.width * 0.62f, y0 - v.height * 0.02f)
            cubicTo(v.left + v.width * 0.78f, y0 - v.height * 0.06f, v.left + v.width * 0.92f, y0 + v.height * 0.02f, v.right, y0)
            lineTo(v.right, v.bottom); close()
        }
        drawPath(monton, nieve)
        drawPath(Path().apply { op(monton, corridoNavidad(monton, -w * 0.006f, -w * 0.012f), PathOperation.Difference) }, nieveSombra.copy(alpha = 0.6f))
        // La rama del abeto nevado, que entra por arriba a la derecha.
        val ramaA = Offset(v.right + w * 0.02f, v.top + v.height * 0.06f)
        val ramaB = Offset(v.left + v.width * 0.38f, v.top + v.height * 0.32f)
        val agujas = Path()
        val nieveR = Path()
        val n = 14
        for (i in 0 until n) {
            val t = i / (n - 1f)
            val p = Offset(ramaA.x + (ramaB.x - ramaA.x) * t, ramaA.y + (ramaB.y - ramaA.y) * t + v.height * 0.06f * t * t)
            val l = w * (0.03f - 0.014f * t)
            for (s in listOf(-1f, 1f)) {
                agujas.moveTo(p.x, p.y); agujas.lineTo(p.x - l * 0.5f, p.y + s * l * 0.8f + l * 0.3f)
            }
            if (i % 2 == 0) nieveR.addOval(Rect(p.x - l * 0.7f, p.y - l * 0.45f, p.x + l * 0.5f, p.y + l * 0.05f))
        }
        val verde = if (a.oscuro) mezcla(P.ABETO_HONDO, alto, 0.2f) else if (ocaso) Color(0xFF3A3E46) else mezcla(P.ABETO_OSC, bajo, 0.2f)
        drawLine(verde, ramaA, ramaB, 1.6f * u)
        drawPath(agujas, verde, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(nieveR, nieve)
        // Los copos que caen.
        val copos = Path()
        val cuantos = if (a.oscuro) 26 else 18
        for (j in 0 until cuantos) {
            val p = Offset(v.left + v.width * azar(9140, j), v.top + v.height * 0.8f * azar(9141, j))
            val r = (0.7f + 0.9f * azar(9142, j)) * u
            copos.addOval(Rect(p, r))
        }
        drawPath(copos, mezcla(nieve, Color.White, 0.4f).copy(alpha = if (a.oscuro) 0.7f else 0.9f))
    }

    /** La escarcha de un vidrio [v]: los helechos blancos que crecen desde las esquinas de abajo. */
    private fun DrawScope.escarcha(v: Rect, a: AireNavidad, semilla: Int) {
        val u = density; val w = size.width
        val p = Path()
        listOf(Offset(v.left, v.bottom) to 1f, Offset(v.right, v.bottom) to -1f).forEachIndexed { e, (c, lado) ->
            for (j in 0 until 6) {
                val an = (-0.2f - 1.2f * azar(semilla + e, j)) * lado
                val dir = Offset(cos(an) * lado, -kotlin.math.abs(sin(an)))
                val l = w * (0.02f + 0.03f * azar(semilla + e, j + 10))
                val q = c + dir * l
                p.moveTo(c.x, c.y); p.lineTo(q.x, q.y)
                // Las barbas del helecho.
                for (b in 1..4) {
                    val f = b / 5f
                    val s = c + dir * (l * f)
                    val bl = l * 0.28f * (1f - f * 0.6f)
                    p.moveTo(s.x, s.y); p.lineTo(s.x + (dir.x * 0.5f - dir.y * 0.8f) * bl, s.y + (dir.y * 0.5f + dir.x * 0.8f) * bl)
                    p.moveTo(s.x, s.y); p.lineTo(s.x + (dir.x * 0.5f + dir.y * 0.8f) * bl, s.y + (dir.y * 0.5f - dir.x * 0.8f) * bl)
                }
            }
        }
        val blanco = if (a.oscuro) Color(0xFFB8CCE4).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.62f)
        clipRect(v.left, v.top, v.right, v.bottom) {
            drawPath(p, blanco, style = Stroke(0.8f * u, cap = StrokeCap.Round))
            drawOval(blanco.copy(alpha = blanco.alpha * 0.45f), Offset(v.left - w * 0.03f, v.bottom - w * 0.025f), Size(w * 0.06f, w * 0.05f))
            drawOval(blanco.copy(alpha = blanco.alpha * 0.45f), Offset(v.right - w * 0.03f, v.bottom - w * 0.025f), Size(w * 0.06f, w * 0.05f))
        }
    }

    /** Dentro de la mancha: la sombra de la rama nevada de afuera y la de la naranja colgada de la falleba. */
    private fun DrawScope.sombrasEnMancha(a: AireNavidad, m: ManchaNavidad) {
        val w = size.width; val u = density
        val agujas = Path()
        for (i in 0 until 12) {
            val t = i / 11f
            val p = m.en(1f - 0.55f * t, 0.04f + 0.24f * t + 0.05f * t * t)
            val l = 0.022f - 0.008f * t
            for (s in listOf(-1f, 1f)) { agujas.moveTo(p.x * w, p.y * w); agujas.lineTo((p.x - l * 0.6f) * w, (p.y + s * l * 0.7f + l * 0.4f) * w) }
        }
        drawPath(agujas, a.sombra(0.16f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        val n = m.en(0.52f, 0.7f)
        drawOval(a.sombra(0.2f), Offset(n.x * w - w * 0.018f, n.y * w - w * 0.016f), Size(w * 0.038f, w * 0.03f))
        drawLine(a.sombra(0.16f), Offset(n.x * w, n.y * w - w * 0.016f), m.en(0.5f, 0.5f).let { Offset(it.x * w, it.y * w) }, 1.4f * u)
    }

    // ---------------------------------------------------------------- lo colgado

    /** Las cadenetas de papel en festón por lo alto de la pared y la naranja con clavos colgada de la falleba. */
    fun DrawScope.colgado(a: AireNavidad, apoyo: Float, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        cadeneta(Offset(w * 0.27f, w * 0.022f), Offset(w * 0.62f, w * 0.03f), w * 0.07f, a, cerca, m, 0)
        cadeneta(Offset(w * 0.62f, w * 0.03f), Offset(w * 1.03f, w * 0.018f), w * 0.085f, a, cerca, m, 1)
        naranjaColgada(a, cerca)
    }

    /**
     * Una cadeneta de papel de [p0] a [p1], colgada con [caida]: los eslabones alternos, uno de frente (el aro) y otro de canto (la
     * tira), en rojo, verde y crema, con su sombra en la pared y la chincheta de cada punta.
     */
    private fun DrawScope.cadeneta(p0: Offset, p1: Offset, caida: Float, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?, semilla: Int) {
        val w = size.width; val u = density
        fun punto(t: Float) = Offset(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t + caida * 4f * t * (1f - t))
        val largo = w * 0.024f
        val n = (((p1.x - p0.x) * 1.08f) / largo).toInt().coerceIn(2, 80)
        val colores = listOf(P.CARMIN, P.FILETE_LUZ, P.CREMA)
        val sombra = Offset(w * 0.006f, w * 0.009f)
        for (pasada in 0 until 2) {
            for (i in 0 until n) {
                val t = (i + 0.5f) / n
                val c = punto(t) + (if (pasada == 0) sombra else Offset.Zero)
                val dir = punto(minOf(1f, t + 0.01f)) - punto(maxOf(0f, t - 0.01f))
                val ang = Math.toDegrees(atan2(dir.y, dir.x).toDouble()).toFloat()
                val k = cerca(c)
                val lit = if (m != null && m.toca(c.x / w, c.y / w, ManchaNavidad.FRENTE * 0.3f) > 0f) 1f else 0f
                val col = colores[(i + semilla) % 3]
                if (pasada == 0) {
                    rotate(ang, c) {
                        if (i % 2 == 0) drawOval(a.sombra(0.2f), Offset(c.x - largo * 0.5f, c.y - largo * 0.32f), Size(largo, largo * 0.64f), style = Stroke(2.4f * u))
                        else drawLine(a.sombra(0.2f), Offset(c.x - largo * 0.46f, c.y), Offset(c.x + largo * 0.46f, c.y), 2.6f * u)
                    }
                    continue
                }
                val cara = a.en(col, 0f, k, lit, m)
                val hondo = a.en(mezcla(col, P.TINTA, 0.35f), 0f, k, lit, m)
                rotate(ang, c) {
                    if (i % 2 == 0) {
                        // El aro de frente: la cara de fuera y, dentro, la de la vuelta en sombra.
                        drawOval(cara, Offset(c.x - largo * 0.5f, c.y - largo * 0.32f), Size(largo, largo * 0.64f), style = Stroke(2.4f * u))
                        drawArc(hondo, 20f, 140f, false, Offset(c.x - largo * 0.42f, c.y - largo * 0.24f), Size(largo * 0.84f, largo * 0.48f), style = Stroke(1f * u))
                        drawArc(a.brillo(0.45f, k), 200f, 90f, false, Offset(c.x - largo * 0.5f, c.y - largo * 0.32f), Size(largo, largo * 0.64f), style = Stroke(0.8f * u))
                    } else {
                        // La tira de canto, que cruza el aro de antes y el de después.
                        drawLine(hondo, Offset(c.x - largo * 0.48f, c.y + 0.5f * u), Offset(c.x + largo * 0.48f, c.y + 0.5f * u), 2.8f * u)
                        drawLine(cara, Offset(c.x - largo * 0.48f, c.y - 0.4f * u), Offset(c.x + largo * 0.48f, c.y - 0.4f * u), 1.4f * u)
                    }
                }
            }
        }
        // Las chinchetas de las puntas.
        listOf(p0, p1).forEach { p ->
            val k = cerca(p)
            drawCircle(a.sombra(0.35f), w * 0.007f, p + Offset(1.2f * u, 1.4f * u))
            drawCircle(a.luz(P.CARMIN, k), w * 0.0065f, p)
            drawCircle(a.brillo(0.6f, k), w * 0.0025f, p - Offset(w * 0.002f, w * 0.002f))
        }
    }

    /** La naranja con clavos, atada con su cinta roja y colgada de la manilla de la falleba. */
    private fun DrawScope.naranjaColgada(a: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val g0 = w * (V_IZQ + 0.0f) + w * MARCO
        val xm = g0 + ((w * (V_DER - DERRAME) - w * MARCO) - g0) * 0.48f
        val ym = w * (V_ARR + DINTEL) + w * MARCO + (w * (V_ABA - DINTEL * 0.5f - MARCO * 1.3f) - w * (V_ARR + DINTEL + MARCO)) * 0.5f
        val gancho = Offset(xm + w * 0.006f, ym + w * 0.03f)
        val c = Offset(gancho.x + w * 0.002f, gancho.y + w * 0.05f)
        val r = w * 0.021f
        val k = cerca(c)
        val lit = !a.oscuro
        drawLine(a.dia(P.CARMIN, 0f, k), gancho, c - Offset(0f, r), 1.3f * u)
        drawOval(a.sombra(0.28f), Offset(c.x - r * 0.7f, c.y - r * 0.6f), Size(r * 2.1f, r * 2f))
        drawCircle(a.dia(P.NARANJA, 0f, k), r, c)
        // La cinta que la rodea de arriba abajo, ladeada (una sola vuelta), y el lazo de arriba.
        drawArc(a.dia(P.CARMIN, 0f, k), -90f, 180f, false, Offset(c.x - r * 0.35f, c.y - r), Size(r * 0.7f, r * 2f), style = Stroke(r * 0.24f))
        listOf(-1f, 1f).forEach { l -> drawOval(a.dia(P.CARMIN, 0f, k), Offset(c.x + l * r * 0.4f - r * 0.25f, c.y - r * 1.3f), Size(r * 0.5f, r * 0.34f)) }
        // Los clavos de especia, en hileras.
        for (fila in -2..2) for (j in -2..2) {
            val p = Offset(c.x + j * r * 0.34f - r * 0.12f, c.y + fila * r * 0.34f)
            if ((p - c).getDistance() < r * 0.85f) drawCircle(a.dia(P.PINO_OSC, -0.2f, k), r * 0.07f, p)
        }
        // El claro y la sombra de la naranja.
        drawCircle(a.sombra(0.22f), r, c + Offset(r * 0.32f, r * 0.3f), style = Stroke(r * 0.4f))
        if (lit) drawCircle(a.claro(0.5f), r * 0.2f, c - Offset(r * 0.38f, r * 0.42f))
    }

    // ---------------------------------------------------------------- la noche

    /** De noche, el halo de la vela de la cornisa en la pared (en la imagen guardada; se pinta una vez). */
    fun DrawScope.luzDeNoche(a: AireNavidad, ch: Float) {
        if (!a.oscuro) return
        val w = size.width
        val c = Offset(w * MuebleEstanteNavidad.VELA_X, w * MuebleEstanteNavidad.VELA_Y)
        val f = if (a.noche) 0.5f else 1f
        clipRect(0f, 0f, w, ch) {
            mancha(c, w * 0.3f, P.LLAMA.copy(alpha = 0.16f * f))
            mancha(c, w * 0.12f, P.LLAMA.copy(alpha = 0.26f * f))
        }
        mancha(c, w * 0.045f, P.FUEGO.copy(alpha = 0.42f * f))
    }
}
