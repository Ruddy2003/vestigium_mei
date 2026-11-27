package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El cartucho de datos de la libreta de Frente, de pie en la ranura de la grabadora: la caja de aluminio con sus remaches y sus
 * estrías, el canto de arriba (más grueso cuantos más bloques), la pestaña roja de escritura, la etiqueta de cinta de papel con
 * el nombre y la cuenta, y la ventana ahumada con sus dos bobinas de cinta parda (la de recogida, más llena cuantos más bloques).
 * Y los cartuchos ya llenos, apilados sobre el osciloscopio.
 */
internal object CartuchoLibretaFrente {
    private val P = PaletaFrente
    private val L = MuebleLibretaFrente
    private val NOMBRE: FontFamily = LetraExtra.chivo
    private val CUENTA: FontFamily = LetraExtra.chivoMono

    private fun en(c: Color, f: Float, p: Offset, s: SitioLibretaFrente, a: AireFrente) = L.en(c, f, p, s, a)

    /** El grueso del cartucho según sus bloques: crece rápido con los primeros y luego despacio, de 4 a 18 dp. */
    private fun grueso(hojas: Int): Float = (4f + 1.05f * sqrt(hojas.coerceAtLeast(0).toFloat())).coerceAtMost(18f)

    /** Cuánto lleva grabado (de 0 a 1): la cinta que pasó a la bobina de recogida. */
    private fun grabado(hojas: Int): Float = (sqrt(hojas.coerceAtLeast(0).toFloat()) / 12f).coerceIn(0f, 1f)

    /**
     * El cartucho, de pie en la ranura; con [ab] (al tocarlo) la grabadora lo suelta y sube un poco. Su sombra en el muro, el canto
     * de arriba, la caja de aluminio, la etiqueta con el nombre y la cuenta, la ventana con las bobinas y los filos de la noche.
     */
    fun DrawScope.cartucho(e: EstadoLibreta, medidor: TextMeasurer, s: SitioLibretaFrente, ab: Float, a: AireFrente) {
        val u = density
        val h = s.h
        val g = grueso(e.hojas) * u
        val f = s.cart.translate(0f, -ab * h * 0.07f)
        val prof = g * 0.55f
        val rad = 6f * u
        // Su sombra en el muro (el flexo está a la izquierda), solo donde asoma por encima de la grabadora.
        clipRect(0f, 0f, s.w, s.deck.top) {
            drawRoundRect(a.sombra(0.3f), Offset(f.left + h * 0.06f, f.top - prof + h * 0.012f), Size(f.width, f.height + prof), CornerRadius(rad))
        }
        // El canto de arriba: mira a la luz; su filo de delante.
        val canto = Rect(f.left + 0.6f * u, f.top - prof, f.right - 0.6f * u, f.top + rad)
        drawRoundRect(Brush.horizontalGradient(listOf(en(P.ALUMINIO, 0.02f, Offset(f.left, f.top), s, a), en(P.ALUMINIO, -0.14f, Offset(f.right, f.top), s, a)), canto.left, canto.right),
            canto.topLeft, canto.size, CornerRadius(rad * 0.8f))
        drawLine(a.sombra(0.25f), Offset(canto.left + rad, canto.top + prof * 0.5f), Offset(canto.right - rad, canto.top + prof * 0.5f), 0.6f * u)
        // La pestaña roja de escritura, que asoma por el canto, a la derecha.
        val pe = Rect(f.right - f.width * 0.2f, f.top - prof - 3.2f * u, f.right - f.width * 0.11f, f.top - prof + 2.4f * u)
        drawRoundRect(a.sombra(0.35f), pe.topLeft + Offset(1.2f * u, 1.2f * u), pe.size, CornerRadius(1.2f * u))
        drawRoundRect(en(P.BOTON, 0f, pe.center, s, a), pe.topLeft, pe.size, CornerRadius(1.2f * u))
        drawRect(a.claro(0.4f), Offset(pe.left + 1f * u, pe.top + 0.6f * u), Size(pe.width - 2f * u, 0.9f * u))
        // La caja de aluminio: la luz del flexo de la izquierda a la derecha, el cepillado, el borde.
        drawRoundRect(Brush.horizontalGradient(0f to en(P.ALUMINIO, 0.12f, Offset(f.left, f.center.y), s, a), 0.45f to en(P.ALUMINIO, 0f, f.center, s, a),
            1f to en(P.ALUMINIO, -0.14f, Offset(f.right, f.center.y), s, a), startX = f.left, endX = f.right), f.topLeft, f.size, CornerRadius(rad))
        for (j in 0 until 5) {
            val y = f.top + f.height * (0.1f + 0.19f * j + 0.05f * azar(e.semilla, 40 + j))
            drawLine(a.sombra(0.06f), Offset(f.left + rad, y), Offset(f.right - rad, y), 0.6f * u)
        }
        // El pie, que entra en la ranura: la sombra de la boca sube por la caja.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.sombra(0.4f)), f.bottom - f.height * 0.14f, f.bottom), Offset(f.left + 1f * u, f.bottom - f.height * 0.14f), Size(f.width - 2f * u, f.height * 0.14f))
        // El uso: una abolladura con su luz, rayones y el aluminio más oscuro donde se agarra.
        val ab0 = Offset(f.left + f.width * (0.18f + 0.1f * azar(e.semilla, 50)), f.top + f.height * 0.405f)
        drawOval(a.sombra(0.14f), Offset(ab0.x - 6f * u, ab0.y - 1.6f * u), Size(12f * u, 3.2f * u))
        drawOval(a.claro(0.4f), Offset(ab0.x - 5f * u, ab0.y - 2.2f * u), Size(8f * u, 1.4f * u))
        for (j in 0 until 4) {
            val x = f.left + f.width * (0.55f + 0.35f * azar(e.semilla, 80 + j)); val y = f.top + f.height * (0.04f + 0.36f * azar(e.semilla, 84 + j))
            drawLine(a.sombra(0.16f), Offset(x, y), Offset(x + (5f + 9f * azar(e.semilla, 88 + j)) * u, y + 1.4f * u), 0.6f * u)
            drawLine(a.claro(0.4f), Offset(x, y + 0.7f * u), Offset(x + (5f + 9f * azar(e.semilla, 88 + j)) * u, y + 2.1f * u), 0.5f * u)
        }
        drawRoundRect(a.sombra(0.5f), f.topLeft, f.size, CornerRadius(rad), style = Stroke(0.8f * u))
        drawLine(if (a.oscuro) a.filo(0.5f * L.cerca(s, f.topLeft)) else a.claro(0.85f), Offset(f.left + rad, f.top + 0.7f * u), Offset(f.right - rad, f.top + 0.7f * u), 1f * u)
        // Las estrías de agarre a los dos lados, abajo.
        listOf(f.left + f.width * 0.025f, f.right - f.width * 0.065f).forEach { x0 ->
            for (j in 0 until 4) {
                val x = x0 + j * 2.4f * u
                drawLine(a.sombra(0.4f), Offset(x, f.top + f.height * 0.48f), Offset(x, f.bottom - 3f * u), 0.8f * u)
                drawLine(a.claro(0.35f), Offset(x + 0.8f * u, f.top + f.height * 0.48f), Offset(x + 0.8f * u, f.bottom - 3f * u), 0.5f * u)
            }
        }
        listOf(Offset(f.left + 6f * u, f.top + 6.5f * u), Offset(f.right - 6f * u, f.top + 6.5f * u)).forEachIndexed { i, o ->
            tornilloFrente(o, 1.6f * u, a, L.cerca(s, o), 30f + 60f * i, L.frio(s, o, a.oscuro))
        }
        ventana(e, f, s, a)
        etiqueta(e, medidor, f, s, a)
        // De noche: el filo tibio del flexo en el canto izquierdo; el frío del osciloscopio en el derecho y en el canto de arriba.
        if (a.oscuro) {
            val kl = L.cerca(s, Offset(f.left, f.center.y))
            val vr = L.frio(s, Offset(f.right, f.center.y), true)
            drawRect(a.filo(0.75f * kl), Offset(f.left, f.top + rad), Size(1.4f * u, f.height - rad))
            drawRect(a.filoVidrio((1.1f * vr).coerceAtMost(0.85f)), Offset(f.right - 1.4f * u, f.top + rad), Size(1.4f * u, f.height - rad))
            val vc = L.frio(s, Offset(f.right, f.top), true)
            drawRect(a.filoVidrio((0.9f * vc).coerceAtMost(0.7f)), Offset(f.center.x, canto.top + 0.4f * u), Size(f.width / 2 - rad, 1f * u))
        }
    }

    /**
     * La ventana ahumada, con su marco de baquelita: las dos bobinas de cinta parda (la de la izquierda se vacía y la de la derecha
     * se llena con los bloques), los cubos de cromo con sus tres agujeros, los rodillos y la cinta que pasa por abajo, y el reflejo.
     */
    private fun DrawScope.ventana(e: EstadoLibreta, f: Rect, s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val marco = Rect(f.left + f.width * 0.1f, f.top + f.height * 0.43f, f.right - f.width * 0.1f, f.top + f.height * 0.92f)
        drawRoundRect(a.sombra(0.4f), marco.topLeft + Offset(0.8f * u, 1.2f * u), marco.size, CornerRadius(4f * u))
        drawRoundRect(en(P.BAQUELITA, 0f, marco.center, s, a), marco.topLeft, marco.size, CornerRadius(4f * u))
        drawLine(a.claro(0.3f), Offset(marco.left + 4f * u, marco.top + 0.7f * u), Offset(marco.right - 4f * u, marco.top + 0.7f * u), 0.8f * u)
        val v = marco.deflate(3f * u)
        val humo = if (a.oscuro) Color(0xFF0C0D0B) else Color(0xFF2A2620)
        drawRoundRect(humo, v.topLeft, v.size, CornerRadius(2.5f * u))
        val lleno = grabado(e.hojas)
        val rMax = v.height * 0.44f; val rCubo = v.height * 0.15f
        val cinta = en(P.PELICULA, 0f, v.center, s, a)
        val borde = en(P.MIEL, 0f, v.center, s, a)
        listOf(0.27f to (1f - 0.85f * lleno), 0.73f to (0.15f + 0.85f * lleno)).forEachIndexed { i, (fx, carga) ->
            val c = Offset(v.left + v.width * fx, v.center.y)
            val r = rCubo * 1.25f + (rMax - rCubo * 1.25f) * carga
            drawCircle(cinta, r, c)
            drawCircle(borde.copy(alpha = 0.55f), r, c, style = Stroke(0.8f * u))
            drawCircle(borde.copy(alpha = 0.25f), r * 0.72f, c, style = Stroke(0.6f * u))
            // El cubo de cromo, con sus tres agujeros, girado distinto en cada una.
            drawCircle(en(P.CROMO, 0.05f, c, s, a), rCubo, c)
            drawCircle(a.sombra(0.3f), rCubo, c, style = Stroke(0.6f * u))
            val giro = 0.6f + 1.7f * i + 2f * azar(e.semilla, 60 + i)
            for (j in 0 until 3) {
                val an = giro + j * 2.094f
                drawCircle(humo, rCubo * 0.24f, Offset(c.x + cos(an) * rCubo * 0.55f, c.y + sin(an) * rCubo * 0.55f))
            }
            drawCircle(en(P.CROMO_OSC, 0f, c, s, a), rCubo * 0.2f, c)
        }
        // Los rodillos de las esquinas de abajo y la cinta que pasa entre ellos.
        val yb = v.bottom - 2.6f * u
        val g0 = Offset(v.left + 4f * u, yb); val g1 = Offset(v.right - 4f * u, yb)
        drawLine(cinta, g0, g1, 1.4f * u)
        listOf(g0, g1).forEach { drawCircle(en(P.CROMO, 0f, it, s, a), 2f * u, it) }
        // El reflejo del vidrio: dos rayas finas, de lado.
        drawLine(Color.White.copy(alpha = if (a.oscuro) 0.08f else 0.2f), Offset(v.left + v.width * 0.08f, v.bottom - 3f * u), Offset(v.left + v.width * 0.3f, v.top + 2f * u), 1.4f * u, StrokeCap.Round)
        drawLine(Color.White.copy(alpha = if (a.oscuro) 0.05f else 0.12f), Offset(v.left + v.width * 0.16f, v.bottom - 3f * u), Offset(v.left + v.width * 0.36f, v.top + 2f * u), 0.7f * u, StrokeCap.Round)
    }

    /**
     * La etiqueta de cinta de papel, un poco torcida, pegada arriba: la franja ámbar, el nombre y, debajo de la raya, la cuenta.
     * Es de la cinta de las etiquetas del Ideario: de noche se tiñe con la luz que le llega y la tinta sigue más honda.
     */
    private fun DrawScope.etiqueta(e: EstadoLibreta, medidor: TextMeasurer, f: Rect, s: SitioLibretaFrente, a: AireFrente) {
        val u = density
        val r = Rect(f.left + f.width * 0.07f, f.top + f.height * 0.075f, f.right - f.width * 0.07f, f.top + f.height * 0.385f)
        val k = L.cerca(s, r.center); val v = L.frio(s, r.center, a.oscuro)
        rotate(-0.8f + 1.2f * (azar(e.semilla, 70) - 0.5f), r.center) {
            drawRect(a.sombra(0.3f), r.topLeft + Offset(1f * u, 1.4f * u), r.size)
            drawRect(a.etiqueta(P.CINTA, k, v), r.topLeft, r.size)
            drawRect(a.etiqueta(P.CINTA_OSC, k, v), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            drawRect(a.etiqueta(P.QUEMADO_TINTA, k, v), r.topLeft, Size(r.width, 2.2f * u))
            // Las puntas cortadas a mano.
            listOf(r.left, r.right).forEach { x ->
                for (j in 0 until 3) {
                    val y = r.top + r.height * (0.2f + 0.3f * j)
                    drawLine(a.sombra(0.12f), Offset(x, y), Offset(x + (if (x == r.left) 1f else -1f) * 1.6f * u, y + 2f * u), 0.6f * u)
                }
            }
            val tinta = a.tinta(k)
            val corte = r.top + r.height * 0.6f
            val cn = Rect(r.left + 6f * u, r.top + 3f * u, r.right - 6f * u, corte - 1f * u)
            val cc = Rect(r.left + 5f * u, corte + 1f * u, r.right - 5f * u, r.bottom - 2f * u)
            val llave = "${e.nombre}|${e.cuenta}|${a.oscuro}|${r.width}|$density|$fontScale"
            if (llave != claveTextos) {
                nombreMedido = capaEn(medidor, e.nombre, cn, NOMBRE, tinta, 15f, peso = FontWeight.SemiBold, minimo = 8f)
                cuentaMedida = capaEn(medidor, e.cuenta, cc, CUENTA, tinta.copy(alpha = 0.9f), 9.5f, minimo = 6f)
                claveTextos = llave
            }
            nombreMedido?.let { drawText(it, topLeft = Offset(cn.center.x - it.size.width / 2f, cn.center.y - it.size.height / 2f)) }
            drawLine(tinta.copy(alpha = 0.35f), Offset(r.left + r.width * 0.12f, corte), Offset(r.right - r.width * 0.12f, corte), 0.7f * u)
            cuentaMedida?.let { drawText(it, topLeft = Offset(cc.center.x - it.size.width / 2f, cc.center.y - it.size.height / 2f)) }
        }
    }

    /** El nombre y la cuenta ya medidos: cambian solo con lo que dicen, el modo y el tamaño (no en cada dibujo). */
    private var claveTextos = ""
    private var nombreMedido: TextLayoutResult? = null
    private var cuentaMedida: TextLayoutResult? = null

    /**
     * Los cartuchos ya llenos, acostados sobre el osciloscopio (uno con los primeros bloques, hasta tres): de canto, con su
     * franja de cinta sin rótulo y el pie de la etiqueta.
     */
    fun DrawScope.llenos(hojas: Int, s: SitioLibretaFrente, a: AireFrente) {
        val n = if (hojas <= 0) 0 else (1 + hojas / 40).coerceAtMost(3)
        if (n == 0 || s.scope.left > s.w) return
        val u = density
        val h = s.h
        val alto = h * 0.055f
        val ancho = h * 0.3f
        for (i in 0 until n) {
            val dx = (azar(1501, i) - 0.5f) * h * 0.04f
            val r = Rect(s.scope.left + h * 0.04f + dx, s.scope.top - h * 0.03f - (i + 1) * alto, s.scope.left + h * 0.04f + dx + ancho, s.scope.top - h * 0.03f - i * alto)
            if (i == 0) contactoFrente(Offset(r.center.x, r.bottom), ancho * 1.05f, 4f * u, a, 0.4f)
            drawRoundRect(a.sombra(0.3f), r.topLeft + Offset(2f * u, 1.6f * u), r.size, CornerRadius(2f * u))
            drawRoundRect(en(P.ALUMINIO, -0.04f, r.center, s, a), r.topLeft, r.size, CornerRadius(2f * u))
            drawRect(en(P.ALUMINIO, 0.18f, r.center, s, a), Offset(r.left + 2f * u, r.top), Size(r.width - 4f * u, 1.4f * u))
            drawRect(a.sombra(0.35f), Offset(r.left + 2f * u, r.bottom - 1.6f * u), Size(r.width - 4f * u, 1.6f * u))
            val et = Rect(r.left + r.width * 0.22f, r.top + r.height * 0.28f, r.left + r.width * 0.7f, r.bottom - r.height * 0.28f)
            drawRect(a.etiqueta(P.CINTA, L.cerca(s, et.center), L.frio(s, et.center, a.oscuro)), et.topLeft, et.size)
            drawRect(a.etiqueta(P.QUEMADO_TINTA, L.cerca(s, et.center), L.frio(s, et.center, a.oscuro)), et.topLeft, Size(et.width * 0.12f, et.height))
            if (a.oscuro) drawRect(a.filoVidrio(0.6f * L.frio(s, r.topLeft, true)), Offset(r.left + 2f * u, r.top), Size(r.width - 4f * u, 1f * u))
        }
    }
}
