package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cuerpo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Un óvalo inscrito en [r] (para escribir los óvalos con su caja). */
internal fun DrawScope.drawOval(color: Color, r: Rect, style: androidx.compose.ui.graphics.drawscope.DrawStyle = androidx.compose.ui.graphics.drawscope.Fill) = drawOval(color, r.topLeft, r.size, style = style)

/** Lo mismo con un degradado. */
internal fun DrawScope.drawOval(brush: Brush, r: Rect) = drawOval(brush, r.topLeft, r.size)

/**
 * El color de las cosas de la cocina con sus luces: la de fondo ([l]), los focos ([a]: el cielo de la ventana, la
 * mancha de sol, la lámpara, el gas, la tele), cuánto es de día ([dia]) y de noche ([noche]), el sol que entra ([sol]) y
 * si la lámpara está encendida.
 *
 * La ventana está al fondo: de día, lo que mira a quien mira queda a contraluz ([frente]) y lo que mira arriba recibe
 * la claridad ([arriba]); lo que mira a un lado recibe los focos de ese lado ([izq], [der]). Nunca se oscurece con gris:
 * la sombra es violeta ([sombra]).
 */
internal class LuzUrss(val l: Luces, val a: Alumbrado, val dia: Float, val noche: Float, val sol: EscenaUrss.SolDeVentana?, val lampara: Boolean, val aPared: Alumbrado = a) {
    fun en(p: Offset, c: Color, mira: Float = 0f): Color = a.en(p, c, mira)
    fun frente(p: Offset, c: Color): Color { val b = a.en(p, c); return lerp(b, mult(b, ColorUrss.SOMBRA, 1f), 0.24f * dia + 0.08f) }
    fun arriba(p: Offset, c: Color): Color { val b = a.en(p, c); return lerp(b, a.en(p, lerp(c, Color(0xFFFFF4DC), 0.4f)), 0.15f + 0.25f * dia) }
    fun izq(p: Offset, c: Color): Color = a.en(p, c, -1f)
    fun der(p: Offset, c: Color): Color = a.en(p, c, 1f)
    fun sombra(p: Offset, c: Color, k: Float = 0.8f): Color = mult(a.en(p, c), ColorUrss.SOMBRA, k)
    /** Un filo de luz cálida (la lámpara, de noche) o clara (la ventana, de día) en el borde de algo oscuro. */
    val filo: Color get() = if (lampara && noche > 0.3f) lerp(ColorUrss.LAMPARA, Color.White, 0.3f) else lerp(Color(0xFFFFF2D8), ColorUrss.LUNA, noche)
}

/**
 * Las piezas de Soviético: las cosas de la cocina y las de fuera, cada una con su lado de luz y de sombra, su materia y
 * su uso. Las de la cocina se dibujan en la perspectiva de su caja ([CocinaUrss.K]); las de fuera, a su tamaño en el
 * patio (`EscenaUrssPatio.kt`).
 *
 * **[Diseño propio]**: el aspecto de cada cosa (una nevera ZIL redondeada con su tirador cromado, el samovar eléctrico
 * de latón, la lámpara de tela naranja con flecos, el radiador de hierro, el fogón de gas esmaltado, la radio de cable)
 * es el corriente en una cocina de Moscú de los años setenta, pero no copia ninguna.
 */
internal object PiezasUrss {
    private val C = ColorUrss
    private val K get() = CocinaUrss.K

    // ---------------------------------------------------------------- Ayudas de la caja

    fun p(x: Float, y: Float, t: Float): Offset = K.p(x, y, t)

    /**
     * Una caja de la cocina, de [x0] a [x1] de lado, de [y0] (arriba) a [y1] (abajo) y de [t0] a [t1] de hondo, con las
     * caras que se ven: la de delante (a contraluz de día), la de arriba o la de abajo, y la del lado que mira al centro.
     * Devuelve la cara de delante, por si hay que pintarle algo encima.
     */
    fun DrawScope.caja(lz: LuzUrss, x0: Float, x1: Float, y0: Float, y1: Float, t0: Float, t1: Float, c: Color, tapa: Color = c, lado: Color = c): Path {
        val f = K.fuga
        if (x1 < f.x) drawPath(poligono(p(x1, y0, t0), p(x1, y0, t1), p(x1, y1, t1), p(x1, y1, t0)), lz.der(p(x1, (y0 + y1) / 2f, (t0 + t1) / 2f), lado))
        if (x0 > f.x) drawPath(poligono(p(x0, y0, t0), p(x0, y0, t1), p(x0, y1, t1), p(x0, y1, t0)), lz.izq(p(x0, (y0 + y1) / 2f, (t0 + t1) / 2f), lado))
        if (y0 > f.y) drawPath(poligono(p(x0, y0, t0), p(x1, y0, t0), p(x1, y0, t1), p(x0, y0, t1)), lz.arriba(p((x0 + x1) / 2f, y0, (t0 + t1) / 2f), tapa))
        if (y1 < f.y) drawPath(poligono(p(x0, y1, t0), p(x1, y1, t0), p(x1, y1, t1), p(x0, y1, t1)), lz.sombra(p((x0 + x1) / 2f, y1, (t0 + t1) / 2f), tapa, 0.6f))
        val frente = poligono(p(x0, y0, t1), p(x1, y0, t1), p(x1, y1, t1), p(x0, y1, t1))
        drawPath(frente, lz.frente(p((x0 + x1) / 2f, (y0 + y1) / 2f, t1), c))
        return frente
    }

    /** Un palo (una pata, un tubo) de [a] a [b], con su lado de luz. */
    fun DrawScope.palo(lz: LuzUrss, a: Offset, b: Offset, grueso: Float, c: Color, mira: Float = 1f) {
        drawLine(lz.sombra(a, c, 0.6f), a, b, grueso, StrokeCap.Butt)
        val n = (b - a).let { Offset(-it.y, it.x) / it.getDistance().coerceAtLeast(0.01f) } * (grueso * 0.25f * mira)
        drawLine(lz.en(a, lerp(c, Color.White, 0.18f), mira), a + n, b + n, grueso * 0.45f, StrokeCap.Butt)
    }

    // ---------------------------------------------------------------- La nevera ZIL

    /**
     * La nevera ZIL «blanca, grande, redonda por arriba, con un tirador cromado», contra la pared de la izquierda, con la
     * puerta mirando al centro de la cocina: de ella se ve el costado (de cara a quien mira, cortado por el borde), la
     * puerta en escorzo con su tirador y su insignia sin letras, la tapa con un tapete de ganchillo y, encima, un tarro
     * con el hongo del té tapado con una gasa, una radio de pilas y una maceta de aloe.
     */
    fun DrawScope.nevera(lz: LuzUrss, x0: Float, x1: Float, t0: Float, t1: Float, yTapa: Float, az: Azar) {
        val suelo = CocinaUrss.SUELO; val r = 9f
        // El costado, de cara a quien mira, con la esquina de arriba redondeada hacia la puerta.
        val c0 = p(x0, suelo, t1); val c1 = p(x1, suelo, t1); val k = K.s(t1)
        val costado = Path().apply {
            moveTo(c0.x, c0.y); lineTo(p(x0, yTapa + r * 0.4f, t1).x, p(x0, yTapa + r * 0.4f, t1).y)
            val a = p(x0, yTapa, t1); val b = p(x1, yTapa, t1)
            cubicTo(a.x, a.y - 1f, a.x + 4f * k, a.y, a.x + 6f * k, a.y)
            lineTo(b.x - r * k, b.y)
            cubicTo(b.x - r * k * 0.4f, b.y, b.x, b.y + r * k * 0.4f, b.x, b.y + r * k)
            lineTo(c1.x, c1.y); close()
        }
        val medio = p((x0 + x1) / 2f, (yTapa + suelo) / 2f, t1)
        // El costado da la espalda a la ventana y a la lámpara: en penumbra, más claro hacia el canto redondeado de la puerta.
        val luzCostado = lz.frente(medio, C.ESMALTE); val sombraCostado = lz.sombra(medio, C.ESMALTE, 0.7f)
        drawPath(costado, Brush.horizontalGradient(0f to sombraCostado, 0.55f to lerp(sombraCostado, luzCostado, 0.45f), 0.88f to luzCostado, 1f to lz.der(medio, lerp(C.ESMALTE, Color.White, 0.2f)), startX = c0.x, endX = c1.x))
        drawPath(costado, Brush.verticalGradient(0f to Color.White.copy(alpha = 0.1f), 0.4f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.3f), startY = p(x0, yTapa, t1).y, endY = c0.y), blendMode = BlendMode.Multiply)
        // El canto redondeado brilla con la luz de la puerta (la ventana o la lámpara están a la derecha).
        val canto = Path().apply { val b = p(x1, yTapa, t1); moveTo(b.x - r * k * 1.4f, b.y + 0.6f); cubicTo(b.x - r * k * 0.4f, b.y + 0.6f, b.x - 0.6f, b.y + r * k * 0.4f, b.x - 0.6f, b.y + r * k * 1.2f) }
        drawPath(canto, lz.der(medio, lerp(C.ESMALTE, Color.White, 0.5f)).copy(alpha = 0.8f), style = Stroke(1.6f * k, cap = StrokeCap.Round))
        clipPath(costado) {
            // El esmalte no es liso: un reflejo largo de la ventana, desconchones y el roce de la cadera junto a la puerta.
            // El canto vertical redondeado, con su brillo; y el reflejo blando de la ventana en el esmalte.
            val e0 = p(x1, yTapa + r, t1); val e1 = p(x1, suelo, t1)
            drawRect(Brush.horizontalGradient(0f to Color.Transparent, 0.7f to lz.der(medio, Color.White).copy(alpha = 0.45f), 1f to Color.Transparent, startX = e0.x - 6f * k, endX = e0.x), Offset(e0.x - 6f * k, e0.y), Size(6f * k, e1.y - e0.y))
            mancha(p(x1 - 14f, yTapa + 40f, t1), 6f * k, 30f * k, lz.en(medio, Color.White), 0.1f * lz.dia + 0.03f)
            for ((u, v) in listOf(0.3f to 0.7f, 0.62f to 0.42f, 0.2f to 0.9f)) {
                val o = p(x0 + (x1 - x0) * u, yTapa + (suelo - yTapa) * v, t1)
                drawOval(lz.en(o, C.HIERRO).copy(alpha = 0.55f), o, Size(2.4f * k, 1.6f * k)); drawOval(lz.en(o, C.TIERRA).copy(alpha = 0.4f), o + Offset(0.6f, 0.4f), Size(1.2f * k, 0.8f * k))
            }
            mancha(p(x1 - 14f, yTapa + 60f, t1), 10f * k, 18f * k, lz.sombra(medio, C.ESMALTE, 0.3f), 0.18f)
            // El reflejo de la ventana en el esmalte: un rectángulo claro con la sombra de la cruz, que sube y baja con la luz.
            val rv = p(x0 + (x1 - x0) * 0.6f, yTapa + 22f, t1)
            mancha(rv + Offset(0f, 14f * k), 5f * k, 16f * k, Color.White, 0.14f * lz.dia + 0.04f)
            mancha(rv + Offset(0f, 14f * k), 1.2f * k, 13f * k, lz.sombra(medio, C.ESMALTE, 0.3f), 0.12f * lz.dia)
            // Más desconchones abajo, donde se roza con el cubo y la escoba, con su óxido.
            for ((u, v) in listOf(0.18f to 0.86f, 0.4f to 0.93f, 0.7f to 0.8f, 0.85f to 0.95f)) { val o = p(x0 + (x1 - x0) * u, yTapa + (suelo - yTapa) * v, t1); mancha(o + Offset(1.6f * k, 1f * k), 3f * k, 2f * k, lz.en(o, lerp(C.LADRILLO, C.TIERRA, 0.4f)), 0.4f); drawOval(lz.en(o, C.HIERRO), o, Size(2.8f * k, 1.8f * k)) }
            // La rejilla de la ventilación, abajo.
            for (i in 0..4) { val y = suelo - 6f - i * 2.4f; drawLine(lz.sombra(medio, C.HIERRO, 0.4f).copy(alpha = 0.55f), p(x0 + 4f, y, t1), p(x1 - 6f, y, t1), 0.9f * k) }
            // La sombra que la pared y el suelo le dejan abajo.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.32f), startY = c0.y - 22f * k, endY = c0.y), Offset(c0.x - 10f, c0.y - 22f * k), Size(c1.x - c0.x + 20f, 22f * k), blendMode = BlendMode.Multiply)
        }
        // La puerta, en escorzo: mira al centro de la cocina, donde está la ventana.
        val q0 = p(x1, yTapa + r * 0.6f, t0); val q1 = p(x1, yTapa + r, t1); val q2 = p(x1, suelo - 3f, t1); val q3 = p(x1, suelo - 3f, t0)
        val puerta = Path().apply { moveTo(q0.x, q0.y); lineTo(q1.x, q1.y); lineTo(q2.x, q2.y); lineTo(q3.x, q3.y); close() }
        val mp = p(x1, (yTapa + suelo) / 2f, (t0 + t1) / 2f)
        drawPath(puerta, Brush.horizontalGradient(0f to lz.der(mp, lerp(C.ESMALTE, Color.White, 0.25f)), 1f to lz.der(mp, C.ESMALTE), startX = q0.x, endX = q1.x))
        drawPath(puerta, lz.sombra(mp, C.CROMO, 0.5f).copy(alpha = 0.5f), style = Stroke(0.7f))
        // El burlete de goma alrededor de la puerta, y la insignia cromada sin letras.
        drawLine(lz.sombra(mp, C.TINTA, 0.5f).copy(alpha = 0.6f), p(x1, yTapa + r, t1 - 2f), p(x1, suelo - 4f, t1 - 2f), 0.8f)
        val ins = p(x1, yTapa + 18f, t0 + (t1 - t0) * 0.45f)
        // La moldura cromada a lo largo de la puerta y el emblema ovalado: cromo con su fondo rojo y una estrella, sin letras.
        drawLine(lz.der(ins, C.CROMO), p(x1 + 0.3f, yTapa + 18f, t0 + 3f), p(x1 + 0.3f, yTapa + 18f, t1 - 3f), 1.2f)
        drawOval(lz.der(ins, C.CROMO), Offset(ins.x - 3.6f, ins.y - 2.6f), Size(7.2f, 5.2f))
        drawOval(lz.der(ins, C.ROJO), Offset(ins.x - 2.6f, ins.y - 1.8f), Size(5.2f, 3.6f))
        drawCircle(lz.der(ins, C.ORO), 0.8f, ins)
        // El tirador cromado: una palanca gruesa que sobresale de la puerta, junto al canto de delante.
        val t0p = p(x1 + 3f, yTapa + 34f, t1 - 5f); val t1p = p(x1 + 3f, yTapa + 52f, t1 - 5f)
        drawLine(lz.sombra(t0p, C.TINTA, 0.5f).copy(alpha = 0.5f), t0p + Offset(1.6f, 2f), t1p + Offset(1.6f, 2f), 3f * k, StrokeCap.Round)
        drawLine(lz.der(t0p, C.CROMO), t0p, t1p, 3.2f * k, StrokeCap.Round)
        drawLine(lz.der(t0p, Color.White).copy(alpha = 0.7f), t0p + Offset(-0.6f * k, 1f), t1p + Offset(-0.6f * k, -1f), 1f * k, StrokeCap.Round)
        drawLine(lz.der(t0p, lerp(C.CROMO, Color.White, 0.3f)), p(x1, yTapa + 34f, t1 - 5f), t0p, 2f * k)
        drawLine(lz.der(t0p, lerp(C.CROMO, Color.White, 0.3f)), p(x1, yTapa + 52f, t1 - 5f), t1p, 2f * k)
        // El paño de cocina colgado del tirador, de cuadros rojos.
        val pt = p(x1 + 3f, yTapa + 40f, t1 - 5f)
        val panoT = Path().apply { moveTo(pt.x - 1f, pt.y - 1f); lineTo(pt.x + 5f * k, pt.y - 1f); cubicTo(pt.x + 6f * k, pt.y + 12f * k, pt.x + 4f * k, pt.y + 22f * k, pt.x + 5f * k, pt.y + 30f * k); lineTo(pt.x - 2f * k, pt.y + 31f * k); cubicTo(pt.x - 1f * k, pt.y + 20f * k, pt.x - 2f * k, pt.y + 10f * k, pt.x - 1f, pt.y - 1f); close() }
        drawPath(panoT, lz.der(pt, C.PAPEL))
        clipPath(panoT) { for (i in 0..4) drawLine(lz.der(pt, C.ROJO).copy(alpha = 0.6f), Offset(pt.x - 2f * k + i * 1.8f * k, pt.y), Offset(pt.x - 2f * k + i * 1.8f * k, pt.y + 32f * k), 0.7f); for (j in 0..7) drawLine(lz.der(pt, C.ROJO).copy(alpha = 0.5f), Offset(pt.x - 3f * k, pt.y + j * 4f * k), Offset(pt.x + 7f * k, pt.y + j * 4f * k), 0.6f) }
        // Las patitas y la sombra en el linóleo.
        for (x in listOf(x0 + 4f, x1 - 4f)) { val q = p(x, suelo, t1 - 3f); drawRect(lz.sombra(q, C.TINTA, 0.4f), Offset(q.x - 1.4f * k, q.y - 2f * k), Size(2.8f * k, 2f * k)) }
        // La tapa, con su tapete de ganchillo que cae por el costado.
        val tapa = poligono(p(x0, yTapa, t0), p(x1 - r * 0.3f, yTapa, t0), p(x1 - r * 0.3f, yTapa, t1 - r * 0.4f), p(x0, yTapa, t1 - r * 0.4f))
        drawPath(tapa, lz.arriba(p((x0 + x1) / 2f, yTapa, (t0 + t1) / 2f), C.ESMALTE))
        val tap0 = p(x0 + 10f, yTapa - 0.3f, t0 + 6f); val tap1 = p(x1 - 8f, yTapa - 0.3f, t0 + 6f); val tap2 = p(x1 - 8f, yTapa - 0.3f, t1 - 4f); val tap3 = p(x0 + 10f, yTapa - 0.3f, t1 - 4f)
        val tapete = poligono(tap0, tap1, tap2, tap3)
        drawPath(tapete, lz.arriba(tap2, C.PAPEL))
        // El fleco del tapete, colgando por el costado: ondas de ganchillo.
        val ondas = Path(); var u = 0f
        val a0 = p(x0 + 10f, yTapa, t1); val a1 = p(x1 - 6f, yTapa, t1)
        ondas.moveTo(a0.x, a0.y)
        while (u < 1f) { val q = lerp2(a0, a1, u); val q2 = lerp2(a0, a1, (u + 0.125f).coerceAtMost(1f)); ondas.cubicTo(q.x + 1f, q.y + 4f * k, q2.x - 1f, q2.y + 4f * k, q2.x, q2.y); u += 0.125f }
        ondas.lineTo(a0.x, a0.y); ondas.close()
        drawPath(ondas, lz.frente(a0, C.PAPEL))
        repeat(9) { i -> val q = lerp2(a0, a1, (i + 0.5f) / 9f); drawCircle(lz.sombra(q, C.PAPEL, 0.4f), 0.7f * k, q + Offset(0f, 2f * k)) }
        // Lo de encima: el tarro del hongo del té, la radio de pilas y el aloe.
        tarro(lz, p(x0 + 18f, yTapa, t0 + 16f), 14f * K.s(t0 + 16f), lerp(C.LATON, C.TIERRA, 0.3f), gasa = true)
        radioDePilas(lz, p(x0 + 34f, yTapa, t0 + 24f), K.s(t0 + 24f))
        maceta(lz, p(x1 - 14f, yTapa, t0 + 8f), K.s(t0 + 8f), aloe = true, az = az)
    }

    private fun lerp2(a: Offset, b: Offset, u: Float) = Offset(a.x + (b.x - a.x) * u, a.y + (b.y - a.y) * u)

    /**
     * La alacena de madera que había antes de la nevera, en el mismo sitio, contra la pared de la izquierda. De ella se ve
     * el costado (de cara a quien mira, cortado por el borde), con sus dos cuarterones, la veta, la cornisa y el zócalo; la
     * luz de la ventana le resbala por el canto. Colgados del costado, el paño bordado y la ristra de setas secas. Por
     * delante, en escorzo, las puertas: arriba de cristal, con los platos de pie, las tazas y los tarros; en medio, el
     * cajón; abajo, de madera con su llave. Encima, el samovar de carbón de la abuela y una cesta.
     */
    fun DrawScope.alacena(lz: LuzUrss, x0: Float, x1: Float, t0: Float, t1: Float, yTapa: Float) {
        val suelo = CocinaUrss.SUELO; val k = K.s(t1); val madera = lerp(C.MADERA, C.LATON, 0.08f)
        val a = p(x0, yTapa, t1); val b = p(x1, yTapa, t1); val c = p(x1, suelo, t1); val d = p(x0, suelo, t1)
        val m = p((x0 + x1) / 2f, (yTapa + suelo) / 2f, t1)
        // El costado: la madera en penumbra, que se aclara hacia el canto por donde le llega la ventana.
        val costado = poligono(a, b, c, d)
        drawPath(costado, Brush.horizontalGradient(0f to lz.sombra(m, madera, 0.55f), 0.6f to lz.frente(m, madera), 0.92f to lz.der(m, lerp(madera, C.PAPEL, 0.15f)), 1f to lz.der(m, lerp(madera, C.PAPEL, 0.3f)), startX = a.x, endX = b.x))
        clipPath(costado) {
            // La veta: líneas onduladas, más juntas en los largueros.
            val az = Azar(Semilla.de("urss", "veta"))
            repeat(26) { val x = x0 + az.real() * (x1 - x0); val pth = Path(); var y = yTapa; pth.moveTo(p(x, y, t1).x, p(x, y, t1).y); var dx = 0f; while (y < suelo) { y += 12f; dx += az.entre(-0.8f, 0.8f); val q = p(x + dx, y, t1); pth.lineTo(q.x, q.y) }; drawPath(pth, lz.sombra(m, madera, 0.5f).copy(alpha = 0.22f), style = Stroke(0.5f)) }
            // Los dos cuarterones, rehundidos: el bisel que mira a la ventana, claro; el otro, oscuro.
            for ((ya, yb) in listOf(yTapa + 10f to yTapa + 62f, yTapa + 72f to suelo - 14f)) {
                val xa = x0 + 8f; val xb = x1 - 7f
                val panel = poligono(p(xa, ya, t1), p(xb, ya, t1), p(xb, yb, t1), p(xa, yb, t1))
                drawPath(panel, lz.sombra(m, madera, 0.25f).copy(alpha = 0.45f))
                drawLine(lz.sombra(m, C.TINTA, 0.5f).copy(alpha = 0.8f), p(xa, ya, t1), p(xb, ya, t1), 1.6f * k)
                drawLine(lz.sombra(m, C.TINTA, 0.4f).copy(alpha = 0.5f), p(xa, ya, t1), p(xa, yb, t1), 1.2f * k)
                drawLine(lz.der(m, lerp(madera, C.PAPEL, 0.5f)).copy(alpha = 0.85f), p(xb, ya, t1), p(xb, yb, t1), 1.5f * k)
                drawLine(lz.der(m, lerp(madera, C.PAPEL, 0.35f)).copy(alpha = 0.6f), p(xa, yb, t1), p(xb, yb, t1), 1f * k)
            }
            // El roce del uso a media altura y la sombra del suelo abajo.
            mancha(p(x1 - 10f, yTapa + 70f, t1), 6f * k, 12f * k, lz.en(m, lerp(madera, C.PAPEL, 0.4f)), 0.2f)
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.3f), startY = d.y - 24f * k, endY = d.y), Offset(d.x - 10f, d.y - 24f * k), Size(c.x - d.x + 20f, 24f * k), blendMode = BlendMode.Multiply)
        }
        // La luz que resbala por el canto del costado.
        drawLine(lz.der(m, lerp(madera, C.PAPEL, 0.55f)).copy(alpha = 0.8f), b, c, 1.2f * k)
        // La cornisa que vuela arriba y el zócalo oscuro abajo.
        drawPath(poligono(p(x0, yTapa - 6f, t1 + 3f), p(x1 + 3f, yTapa - 6f, t1 + 3f), p(x1 + 3f, yTapa, t1 + 3f), p(x0, yTapa, t1 + 3f)), lz.frente(m, lerp(madera, C.TINTA, 0.1f)))
        drawLine(lz.der(m, lerp(madera, C.PAPEL, 0.4f)), p(x0, yTapa - 6f, t1 + 3f), p(x1 + 3f, yTapa - 6f, t1 + 3f), 0.8f)
        drawPath(poligono(p(x0, suelo - 8f, t1 + 1f), p(x1 + 1f, suelo - 8f, t1 + 1f), p(x1 + 1f, suelo, t1 + 1f), p(x0, suelo, t1 + 1f)), lz.sombra(m, lerp(madera, C.TINTA, 0.4f), 0.4f))
        // Por delante, en escorzo: las puertas de cristal con los platos, el cajón y las puertas de abajo.
        val med = yTapa + 70f
        val crist = poligono(p(x1 + 0.1f, yTapa + 4f, t0 + 4f), p(x1 + 0.1f, yTapa + 4f, t1 - 3f), p(x1 + 0.1f, med - 8f, t1 - 3f), p(x1 + 0.1f, med - 8f, t0 + 4f))
        drawPath(poligono(p(x1, yTapa, t0), p(x1, yTapa, t1), p(x1, suelo, t1), p(x1, suelo, t0)), lz.der(m, lerp(madera, C.PAPEL, 0.12f)))
        drawPath(crist, lz.sombra(m, lerp(C.AZUL, C.MADERA, 0.4f), 0.5f))
        clipPath(crist) {
            // Dos baldas: abajo los platos de pie, con su filete azul; arriba las tazas y un tarro de mermelada.
            for (y in listOf(yTapa + 30f, med - 8f)) drawLine(lz.der(m, lerp(madera, C.PAPEL, 0.3f)), p(x1, y, t0), p(x1, y, t1), 1f)
            for (i in 0..4) { val q = p(x1 + 0.1f, med - 18f, t0 + 7f + i * 6f); val kk = K.s(t0 + 7f + i * 6f); drawOval(lz.der(q, C.ESMALTE), Offset(q.x - 1.3f * kk, q.y - 8f * kk), Size(2.6f * kk, 16f * kk)); drawOval(lz.der(q, C.AZUL).copy(alpha = 0.6f), Offset(q.x - 0.8f * kk, q.y - 5f * kk), Size(1.6f * kk, 10f * kk), style = Stroke(0.5f)) }
            for (i in 0..2) { val q = p(x1 + 0.1f, yTapa + 30f, t0 + 9f + i * 9f); drawRect(lz.der(q, if (i == 1) C.ROJO else lerp(C.ESMALTE, C.LATON, 0.2f)), Offset(q.x - 1.4f, q.y - 6f), Size(2.8f, 6f)) }
            // El cristal devuelve la ventana.
            drawLine(lz.en(m, Color.White).copy(alpha = 0.25f), p(x1, yTapa + 6f, t0 + 12f), p(x1, med - 10f, t0 + 22f), 4f)
        }
        drawPath(crist, lz.der(m, lerp(madera, C.PAPEL, 0.25f)), style = Stroke(1.2f))
        drawLine(lz.der(m, lerp(madera, C.PAPEL, 0.25f)), p(x1 + 0.2f, yTapa + 4f, (t0 + t1) / 2f), p(x1 + 0.2f, med - 8f, (t0 + t1) / 2f), 1f)
        // El cajón, con su tirador de latón, y las dos puertas de abajo con su llave.
        val cajon = poligono(p(x1 + 0.1f, med - 4f, t0 + 4f), p(x1 + 0.1f, med - 4f, t1 - 3f), p(x1 + 0.1f, med + 8f, t1 - 3f), p(x1 + 0.1f, med + 8f, t0 + 4f))
        drawPath(cajon, lz.sombra(m, C.TINTA, 0.4f).copy(alpha = 0.5f), style = Stroke(0.8f))
        drawCircle(lz.der(m, C.LATON), 1.1f * k, p(x1 + 0.6f, med + 2f, (t0 + t1) / 2f))
        val abajo = poligono(p(x1 + 0.1f, med + 12f, t0 + 4f), p(x1 + 0.1f, med + 12f, t1 - 3f), p(x1 + 0.1f, suelo - 10f, t1 - 3f), p(x1 + 0.1f, suelo - 10f, t0 + 4f))
        drawPath(abajo, lz.sombra(m, C.TINTA, 0.4f).copy(alpha = 0.5f), style = Stroke(0.8f))
        drawLine(lz.sombra(m, C.TINTA, 0.4f).copy(alpha = 0.5f), p(x1 + 0.2f, med + 12f, (t0 + t1) / 2f), p(x1 + 0.2f, suelo - 10f, (t0 + t1) / 2f), 0.8f)
        val llave = p(x1 + 0.6f, med + 30f, (t0 + t1) / 2f + 3f); drawRect(lz.der(llave, C.LATON), Offset(llave.x - 0.6f, llave.y - 1.6f), Size(1.4f, 3.2f)); drawCircle(lz.der(llave, C.LATON), 1.2f, llave + Offset(0.2f, 3f))
        // Colgados del costado: el paño de lino bordado de punto de cruz y la ristra de setas secas.
        val clavo = p(x1 - 14f, yTapa + 8f, t1 + 0.5f)
        val pano = poligono(clavo + Offset(-7f * k, 1f), clavo + Offset(6f * k, 1f), clavo + Offset(7f * k, 44f * k), clavo + Offset(-6f * k, 46f * k))
        drawPath(pano, lz.frente(clavo, C.PAPEL))
        clipPath(pano) {
            for (j in 0..1) { val y = clavo.y + (34f + j * 5f) * k; var x = clavo.x - 6f * k; while (x < clavo.x + 6f * k) { drawLine(lz.frente(clavo, C.ROJO), Offset(x, y), Offset(x + 1.6f, y + 1.6f), 0.6f); drawLine(lz.frente(clavo, C.ROJO), Offset(x + 1.6f, y), Offset(x, y + 1.6f), 0.6f); x += 2.6f } }
            drawLine(lz.sombra(clavo, C.PAPEL, 0.3f).copy(alpha = 0.6f), clavo + Offset(-1f * k, 2f), clavo + Offset(0f, 46f * k), 0.8f)
        }
        drawCircle(lz.en(clavo, C.TINTA), 0.8f, clavo)
        val ristra = p(x0 + 30f, yTapa + 6f, t1 + 0.5f)
        drawLine(lz.frente(ristra, lerp(C.PAPEL, C.MADERA, 0.4f)), ristra, ristra + Offset(1f, 52f * k), 0.5f)
        for (i in 0..12) { val q = ristra + Offset(1f * i / 12f + (if (i % 2 == 0) -1.4f else 1.4f), (4f + i * 3.8f) * k); drawOval(lz.der(q, lerp(C.LATON, C.PAPEL, 0.25f)), Offset(q.x - 3f, q.y - 2f), Size(6f, 4f)); drawOval(lz.sombra(q, C.MADERA, 0.4f), Offset(q.x - 1.2f, q.y + 0.6f), Size(2.4f, 1.2f)) }
        // Encima, el samovar de carbón de la abuela, más oscuro, y una cesta de mimbre.
        samovar(lz, p(x0 + 30f, yTapa - 6f, t0 + 24f) + Offset(0f, 0f), K.s(t0 + 24f) * 0.85f, conTetera = false)
        val cesta = p(x0 + 14f, yTapa - 6f, t0 + 34f); val kc = K.s(t0 + 34f)
        val cf = Path().apply { moveTo(cesta.x - 9f * kc, cesta.y - 9f * kc); lineTo(cesta.x + 9f * kc, cesta.y - 9f * kc); lineTo(cesta.x + 7f * kc, cesta.y); lineTo(cesta.x - 7f * kc, cesta.y); close() }
        cuerpo(lz.a, cf, cesta.x - 9f * kc, cesta.x + 9f * kc, cesta.y - 4f * kc, lerp(C.LATON, C.MADERA, 0.4f))
        clipPath(cf) { for (j in 0..4) drawLine(lz.sombra(cesta, C.MADERA, 0.4f).copy(alpha = 0.6f), Offset(cesta.x - 9f * kc, cesta.y - 9f * kc + j * 2f * kc), Offset(cesta.x + 9f * kc, cesta.y - 9f * kc + j * 2f * kc), 0.5f) }
    }

    // ---------------------------------------------------------------- Cosas pequeñas de encima

    /** Un tarro de cristal de pie en [pie] (el centro de su base), de [alto], con lo que lleva dentro; [gasa]: tapado con una gasa atada. */
    fun DrawScope.tarro(lz: LuzUrss, pie: Offset, alto: Float, dentro: Color, gasa: Boolean = false, tapa: Color? = null) {
        val w = alto * 0.36f
        val forma = Path().apply { moveTo(pie.x - w, pie.y); lineTo(pie.x - w, pie.y - alto * 0.82f); cubicTo(pie.x - w, pie.y - alto * 0.9f, pie.x - w * 0.7f, pie.y - alto * 0.92f, pie.x - w * 0.7f, pie.y - alto); lineTo(pie.x + w * 0.7f, pie.y - alto); cubicTo(pie.x + w * 0.7f, pie.y - alto * 0.92f, pie.x + w, pie.y - alto * 0.9f, pie.x + w, pie.y - alto * 0.82f); lineTo(pie.x + w, pie.y); close() }
        drawOval(C.SOMBRA.copy(alpha = 0.25f), Offset(pie.x - w * 1.1f, pie.y - w * 0.25f), Size(w * 2.6f, w * 0.6f), blendMode = BlendMode.Multiply)
        cuerpo(lz.a, forma, pie.x - w, pie.x + w, pie.y - alto / 2f, dentro)
        // El cristal: un brillo vertical y el borde claro.
        drawLine(lz.en(pie, Color.White).copy(alpha = 0.45f), Offset(pie.x - w * 0.55f, pie.y - alto * 0.15f), Offset(pie.x - w * 0.55f, pie.y - alto * 0.78f), w * 0.22f, StrokeCap.Round)
        drawPath(forma, lz.en(pie, lerp(C.CROMO, Color.White, 0.4f)).copy(alpha = 0.35f), style = Stroke(0.5f))
        if (gasa) {
            drawOval(lz.arriba(pie, C.PAPEL), Offset(pie.x - w * 0.9f, pie.y - alto - w * 0.3f), Size(w * 1.8f, w * 0.7f))
            drawPath(Path().apply { moveTo(pie.x - w * 0.8f, pie.y - alto); lineTo(pie.x - w * 0.9f, pie.y - alto * 0.8f); lineTo(pie.x - w * 0.6f, pie.y - alto * 0.86f); close() }, lz.frente(pie, C.PAPEL))
            drawLine(lz.frente(pie, C.ROJO), Offset(pie.x - w * 0.72f, pie.y - alto * 0.94f), Offset(pie.x + w * 0.72f, pie.y - alto * 0.94f), 0.7f)
        } else if (tapa != null) {
            drawRect(lz.frente(pie, tapa), Offset(pie.x - w * 0.74f, pie.y - alto - alto * 0.08f), Size(w * 1.48f, alto * 0.1f))
        }
    }

    /** Una radio de pilas pequeña, de plástico color crema, con su rejilla y su asa. */
    fun DrawScope.radioDePilas(lz: LuzUrss, pie: Offset, k: Float) {
        val w = 16f * k; val h = 10f * k
        drawRect(C.SOMBRA.copy(alpha = 0.3f), Offset(pie.x - w / 2f + 1f, pie.y - 1f), Size(w + 2f, 2f), blendMode = BlendMode.Multiply)
        val caja = Path().apply { addRect(Rect(pie.x - w / 2f, pie.y - h, pie.x + w / 2f, pie.y)) }
        cuerpo(lz.a, caja, pie.x - w / 2f, pie.x + w / 2f, pie.y - h / 2f, lerp(C.PAPEL, C.NARANJA, 0.18f))
        drawRect(lz.frente(pie, C.HIERRO), Offset(pie.x - w * 0.42f, pie.y - h * 0.8f), Size(w * 0.5f, h * 0.62f))
        for (i in 0..3) drawLine(lz.frente(pie, lerp(C.HIERRO, C.PAPEL, 0.4f)).copy(alpha = 0.6f), Offset(pie.x - w * 0.42f, pie.y - h * (0.72f - i * 0.14f)), Offset(pie.x + w * 0.08f, pie.y - h * (0.72f - i * 0.14f)), 0.4f)
        drawCircle(lz.frente(pie, C.ROJO), h * 0.16f, Offset(pie.x + w * 0.28f, pie.y - h * 0.5f))
        drawLine(lz.arriba(pie, C.CROMO), Offset(pie.x + w * 0.42f, pie.y - h), Offset(pie.x + w * 0.42f, pie.y - h - 9f * k), 0.6f)
        drawPath(Path().apply { moveTo(pie.x - w * 0.3f, pie.y - h); cubicTo(pie.x - w * 0.3f, pie.y - h * 1.6f, pie.x + w * 0.2f, pie.y - h * 1.6f, pie.x + w * 0.2f, pie.y - h) }, lz.frente(pie, C.HIERRO), style = Stroke(0.8f * k))
    }

    /** Una maceta de barro con un aloe (hojas carnosas en abanico) o un geranio (con sus flores rojas). */
    fun DrawScope.maceta(lz: LuzUrss, pie: Offset, k: Float, aloe: Boolean, az: Azar, botella: Boolean = false) {
        val w = 6f * k; val h = 8f * k
        drawOval(C.SOMBRA.copy(alpha = 0.28f), Offset(pie.x - w * 1.3f, pie.y - 1.4f), Size(w * 2.8f, 2.6f), blendMode = BlendMode.Multiply)
        val olla = Path().apply { moveTo(pie.x - w * 0.75f, pie.y); lineTo(pie.x - w, pie.y - h); lineTo(pie.x + w, pie.y - h); lineTo(pie.x + w * 0.75f, pie.y); close() }
        cuerpo(lz.a, olla, pie.x - w, pie.x + w, pie.y - h / 2f, C.LADRILLO)
        drawRect(lz.arriba(pie, lerp(C.LADRILLO, C.PAPEL, 0.2f)), Offset(pie.x - w * 1.05f, pie.y - h - 1.4f * k), Size(w * 2.1f, 1.6f * k))
        val base = Offset(pie.x, pie.y - h - 1f)
        if (aloe) {
            for (i in 0..8) {
                val ang = (-PI / 2 + (i - 4) * 0.27).toFloat(); val largo = (13f + 4f * az.real()) * k * (1f - 0.06f * kotlin.math.abs(i - 4))
                val punta = base + Offset(cos(ang) * largo, sin(ang) * largo)
                val hoja = Path().apply { moveTo(base.x - 1.2f * k, base.y); cubicTo(base.x - 1.2f * k + (punta.x - base.x) * 0.4f, base.y + (punta.y - base.y) * 0.5f, punta.x - 0.6f, punta.y + 2f, punta.x, punta.y); cubicTo(punta.x + 0.6f, punta.y + 2f, base.x + 1.2f * k + (punta.x - base.x) * 0.5f, base.y + (punta.y - base.y) * 0.4f, base.x + 1.2f * k, base.y); close() }
                drawPath(hoja, if (i % 2 == 0) lz.der(punta, lerp(C.VERDE, C.PAPEL, 0.15f)) else lz.izq(punta, lerp(C.VERDE, C.HIERRO, 0.2f)))
            }
        } else {
            // El geranio: hojas redondas y ramilletes rojos.
            repeat(9) { val q = base + Offset(az.entre(-9f, 9f) * k, az.entre(-14f, -2f) * k); drawCircle(if (it % 3 == 0) lz.izq(q, lerp(C.VERDE, C.HIERRO, 0.3f)) else lz.der(q, C.VERDE), az.entre(2.4f, 3.6f) * k, q) }
            repeat(3) { val q = base + Offset(az.entre(-7f, 7f) * k, az.entre(-20f, -13f) * k); for (j in 0..5) drawCircle(lz.en(q, if (j % 2 == 0) C.ROJO else lerp(C.ROJO, C.NARANJA, 0.3f)), 1.2f * k, q + Offset(az.entre(-2f, 2f) * k, az.entre(-2f, 2f) * k)) }
        }
        if (botella) {
            // La botella de riego, boca abajo y clavada en la tierra, para los días que ella no está.
            val b = base + Offset(2f * k, -2f * k)
            val f = Path().apply { moveTo(b.x - 0.8f * k, b.y); lineTo(b.x - 0.8f * k, b.y - 4f * k); cubicTo(b.x - 3f * k, b.y - 6f * k, b.x - 3f * k, b.y - 7f * k, b.x - 3f * k, b.y - 9f * k); lineTo(b.x - 3f * k, b.y - 20f * k); lineTo(b.x + 3f * k, b.y - 20f * k); lineTo(b.x + 3f * k, b.y - 9f * k); cubicTo(b.x + 3f * k, b.y - 7f * k, b.x + 3f * k, b.y - 6f * k, b.x + 0.8f * k, b.y - 4f * k); lineTo(b.x + 0.8f * k, b.y); close() }
            drawPath(f, lz.en(b, lerp(C.VERDE, C.CROMO, 0.6f)).copy(alpha = 0.7f))
            drawRect(lz.en(b, lerp(C.AZUL, C.CROMO, 0.5f)).copy(alpha = 0.5f), Offset(b.x - 3f * k, b.y - 20f * k), Size(6f * k, 7f * k))
            drawLine(Color.White.copy(alpha = 0.4f), Offset(b.x - 1.8f * k, b.y - 18f * k), Offset(b.x - 1.8f * k, b.y - 9f * k), 0.7f * k)
        }
    }

    // ---------------------------------------------------------------- El fogón

    /**
     * El fogón de gas esmaltado, de cuatro fuegos, contra la pared: el horno con su puerta y su tirador, las cuatro
     * llaves, los quemadores con sus parrillas de hierro y, encima, la olla esmaltada de la sopa y el hervidor. Con el gas
     * encendido, la corona azul de la llama (la que se mueve va en la parte viva).
     */
    fun DrawScope.fogon(lz: LuzUrss, x0: Float, x1: Float, t1: Float, yTapa: Float, encendido: Boolean, az: Azar) {
        val suelo = CocinaUrss.SUELO
        // El respaldo y la tapa abatible apoyada contra la pared.
        caja(lz, x0 + 2f, x1 - 2f, yTapa - 18f, yTapa, 0f, 3f, C.ESMALTE)
        val frente = caja(lz, x0, x1, yTapa, suelo, 0f, t1, C.ESMALTE, lerp(C.ESMALTE, C.HIERRO, 0.12f))
        val m = p((x0 + x1) / 2f, (yTapa + suelo) / 2f, t1); val k = K.s(t1)
        clipPath(frente) {
            // El horno: su puerta con el canto cromado, el cristal oscuro y el tirador.
            val h0 = p(x0 + 6f, yTapa + 16f, t1); val h1 = p(x1 - 6f, suelo - 14f, t1)
            drawRect(lz.frente(m, lerp(C.ESMALTE, C.PAPEL, 0.3f)), h0, Size(h1.x - h0.x, h1.y - h0.y))
            drawRect(lz.sombra(m, C.CROMO, 0.5f), h0, Size(h1.x - h0.x, h1.y - h0.y), style = Stroke(0.8f))
            drawRect(lz.sombra(m, C.HIERRO, 0.7f), h0 + Offset(8f * k, 10f * k), Size(h1.x - h0.x - 16f * k, (h1.y - h0.y) * 0.4f))
            drawRect(lz.en(m, Color.White).copy(alpha = 0.12f), h0 + Offset(9f * k, 11f * k), Size((h1.x - h0.x) * 0.25f, (h1.y - h0.y) * 0.38f))
            drawLine(lz.frente(m, C.CROMO), h0 + Offset(6f * k, 5f * k), Offset(h1.x - 6f * k, h0.y + 5f * k), 2f * k, StrokeCap.Round)
            // El zócalo oscuro, la chorrera de leche y la mugre de años junto al suelo.
            drawRect(lz.sombra(m, C.HIERRO, 0.6f), p(x0, suelo - 8f, t1), Size(p(x1, suelo, t1).x - p(x0, suelo, t1).x, 8f * k))
            mancha(p(x0 + 30f, yTapa + 10f, t1), 5f * k, 3f * k, lz.en(m, C.TIERRA), 0.25f)
            drawLine(lz.en(m, lerp(C.LATON, C.TIERRA, 0.4f)).copy(alpha = 0.35f), p(x0 + 40f, yTapa + 3f, t1), p(x0 + 40.4f, yTapa + 14f, t1), 1.4f * k, StrokeCap.Round)
        }
        // Las cuatro llaves del gas, en el canto de arriba.
        for (i in 0..3) { val q = p(x0 + 10f + i * (x1 - x0 - 20f) / 3f, yTapa + 7f, t1 + 1f); drawCircle(lz.sombra(q, C.TINTA, 0.4f), 2.6f * k, q + Offset(0.4f, 0.6f)); drawCircle(lz.frente(q, lerp(C.TINTA, C.PAPEL, 0.25f)), 2.4f * k, q); drawLine(lz.en(q, C.PAPEL), q + Offset(0f, -1.8f * k), q + Offset(0f, 1.8f * k), 0.7f * k) }
        // Los quemadores y sus parrillas, en la tapa.
        val fuegos = listOf((x0 + 16f) to 8f, (x1 - 16f) to 8f, (x0 + 16f) to 22f, (x1 - 16f) to 22f)
        for ((i, f) in fuegos.withIndex()) {
            val (x, t) = f; val q = p(x, yTapa - 0.2f, t); val kk = K.s(t)
            drawOval(lz.sombra(q, C.HIERRO, 0.7f), Offset(q.x - 6f * kk, q.y - 1.8f * kk), Size(12f * kk, 3.6f * kk))
            drawOval(lz.en(q, C.HIERRO), Offset(q.x - 3f * kk, q.y - 0.9f * kk), Size(6f * kk, 1.8f * kk))
            for (j in 0..3) { val ang = j * PI.toFloat() / 2f + 0.4f; drawLine(lz.sombra(q, C.TINTA, 0.3f), q + Offset(cos(ang) * 2f * kk, sin(ang) * 0.6f * kk), q + Offset(cos(ang) * 7f * kk, sin(ang) * 2.1f * kk), 0.9f) }
            if (encendido && i == 3) {
                // La corona azul (la llama quieta; la que tiembla va en la parte viva).
                for (j in 0..11) { val ang = j * PI.toFloat() / 6f; drawLine(lerp(C.AZUL, Color(0xFFB8D4FF), 0.4f).copy(alpha = 0.9f), q + Offset(cos(ang) * 2.6f * kk, sin(ang) * 0.8f * kk), q + Offset(cos(ang) * 3.4f * kk, sin(ang) * 0.9f * kk - 2.2f * kk), 0.8f, StrokeCap.Round) }
            }
        }
        // Encima: la olla esmaltada de la sopa (atrás) y el hervidor (delante, en el fuego que se enciende).
        olla(lz, p(x0 + 16f, yTapa, 9f), K.s(9f), az)
        hervidor(lz, p(x1 - 16f, yTapa, 22f), K.s(22f))
    }

    /** La olla esmaltada azul con su borde negro y su tapa, con algún desconchón que enseña el hierro. */
    fun DrawScope.olla(lz: LuzUrss, pie: Offset, k: Float, az: Azar) {
        val w = 10f * k; val h = 12f * k
        val forma = Path().apply { moveTo(pie.x - w, pie.y - h); lineTo(pie.x - w * 0.94f, pie.y - 1f); cubicTo(pie.x - w * 0.9f, pie.y, pie.x + w * 0.9f, pie.y, pie.x + w * 0.94f, pie.y - 1f); lineTo(pie.x + w, pie.y - h); close() }
        cuerpo(lz.a, forma, pie.x - w, pie.x + w, pie.y - h / 2f, C.AZUL)
        // Las flores pintadas del esmalte: la imagen de la olla, que sube con la luz.
        for (i in 0..2) { val q = Offset(pie.x - w * 0.5f + i * w * 0.5f, pie.y - h * 0.5f); for (j in 0..4) { val a = j * 1.256f; drawCircle(lz.en(q, C.PAPEL), 0.9f * k, q + Offset(cos(a) * 1.6f * k, sin(a) * 1.6f * k)) }; drawCircle(lz.en(q, C.ROJO), 0.8f * k, q) }
        repeat(2) { val q = Offset(pie.x + az.entre(-w * 0.7f, w * 0.7f), pie.y - az.entre(1f, h - 2f)); drawOval(lz.en(q, C.TINTA), q, Size(1.6f * k, 1.1f * k)) }
        drawOval(lz.frente(pie, C.TINTA), Offset(pie.x - w * 1.02f, pie.y - h - 1.6f * k), Size(w * 2.04f, 3.2f * k))
        drawOval(lz.arriba(pie, C.AZUL), Offset(pie.x - w * 0.9f, pie.y - h - 2.4f * k), Size(w * 1.8f, 3.2f * k))
        drawOval(lz.arriba(pie, C.TINTA), Offset(pie.x - 1.6f * k, pie.y - h - 3.6f * k), Size(3.2f * k, 1.8f * k))
        for (s in listOf(-1f, 1f)) drawLine(lz.frente(pie, C.TINTA), Offset(pie.x + s * w, pie.y - h * 0.8f), Offset(pie.x + s * (w + 2.4f * k), pie.y - h * 0.8f), 1.4f * k, StrokeCap.Round)
    }

    /** El hervidor de aluminio, abombado, con su asa negra y su pico. */
    fun DrawScope.hervidor(lz: LuzUrss, pie: Offset, k: Float) {
        val w = 8.6f * k; val h = 10f * k
        val forma = Path().apply { moveTo(pie.x - w, pie.y); cubicTo(pie.x - w * 1.1f, pie.y - h * 0.6f, pie.x - w * 0.6f, pie.y - h, pie.x, pie.y - h); cubicTo(pie.x + w * 0.6f, pie.y - h, pie.x + w * 1.1f, pie.y - h * 0.6f, pie.x + w, pie.y); close() }
        // El pico, hacia la izquierda.
        drawPath(Path().apply { moveTo(pie.x - w * 0.7f, pie.y - h * 0.3f); lineTo(pie.x - w * 1.6f, pie.y - h * 0.95f); lineTo(pie.x - w * 1.5f, pie.y - h * 1.02f); lineTo(pie.x - w * 0.6f, pie.y - h * 0.6f); close() }, lz.izq(pie, C.CROMO))
        cuerpo(lz.a, forma, pie.x - w, pie.x + w, pie.y - h / 2f, lerp(C.CROMO, C.HORMIGON, 0.3f))
        drawLine(lz.en(pie, Color.White).copy(alpha = 0.55f), Offset(pie.x - w * 0.3f, pie.y - h * 0.82f), Offset(pie.x - w * 0.62f, pie.y - h * 0.3f), 1.2f * k, StrokeCap.Round)
        drawOval(lz.arriba(pie, C.CROMO), Offset(pie.x - w * 0.36f, pie.y - h - 1.2f * k), Size(w * 0.72f, 2f * k))
        drawCircle(lz.frente(pie, C.TINTA), 1f * k, Offset(pie.x, pie.y - h - 1.6f * k))
        drawPath(Path().apply { moveTo(pie.x - w * 0.6f, pie.y - h * 0.86f); cubicTo(pie.x - w * 0.5f, pie.y - h * 1.7f, pie.x + w * 0.5f, pie.y - h * 1.7f, pie.x + w * 0.6f, pie.y - h * 0.86f) }, lz.frente(pie, C.TINTA), style = Stroke(1.4f * k))
    }

    // ---------------------------------------------------------------- El radiador

    /**
     * El radiador de hierro colado bajo la ventana, de [x0] a [x1]: sus elementos de costillas, pintados de crema muchas
     * veces, con la pintura saltada y el hierro a la vista; los tubos que bajan al suelo y la llave. Si la calefacción
     * está puesta, un paño secándose encima.
     */
    fun DrawScope.radiador(lz: LuzUrss, x0: Float, x1: Float, caliente: Boolean, az: Azar) {
        val y0 = 162f; val y1 = 202f; val t0 = 3f; val t1 = 9f
        val pintura = lerp(C.ESMALTE, C.PARED, 0.4f)
        // El fondo entre las costillas, en sombra.
        drawPath(poligono(p(x0, y0, t0), p(x1, y0, t0), p(x1, y1, t0), p(x0, y1, t0)), lz.sombra(p((x0 + x1) / 2f, 180f, t0), pintura, 0.7f))
        val n = ((x1 - x0) / 7f).toInt()
        for (i in 0 until n) {
            val a = x0 + i * 7f; val b = a + 5.6f
            val cara = poligono(p(a, y0 + 2f, t1), p(b, y0 + 2f, t1), p(b, y1 - 2f, t1), p(a, y1 - 2f, t1))
            val q = p((a + b) / 2f, 182f, t1)
            drawPath(cara, Brush.horizontalGradient(0f to lz.izq(q, pintura), 0.5f to lz.frente(q, pintura), 1f to lz.sombra(q, pintura, 0.5f), startX = p(a, 182f, t1).x, endX = p(b, 182f, t1).x))
            // Las cabezas redondas de arriba y de abajo de cada elemento.
            for (y in listOf(y0 + 2f, y1 - 2f)) { val c = p((a + b) / 2f, y, t1); drawOval(lz.arriba(c, pintura), Offset(c.x - 2.8f, c.y - 1.6f), Size(5.6f, 3.2f)) }
            // La costilla de en medio.
            drawLine(lz.sombra(q, pintura, 0.4f).copy(alpha = 0.6f), p((a + b) / 2f, y0 + 6f, t1 + 0.4f), p((a + b) / 2f, y1 - 6f, t1 + 0.4f), 0.6f)
            if (az.real() < 0.35f) { val d = p(az.entre(a, b), az.entre(y0 + 6f, y1 - 6f), t1); drawOval(lz.en(d, C.HIERRO), d, Size(az.entre(1.2f, 2.6f), az.entre(0.8f, 1.6f))) }
        }
        // Los tubos: el que sube por la pared junto a la ventana y el que baja al suelo, y la llave.
        palo(lz, p(x0 - 6f, y0 + 6f, t0 + 2f), p(x0 + 1f, y0 + 6f, t0 + 2f), 2.6f, lerp(pintura, C.HIERRO, 0.2f))
        palo(lz, p(x1 - 1f, y1 - 4f, t0 + 2f), p(x1 + 6f, y1 - 4f, t0 + 2f), 2.6f, lerp(pintura, C.HIERRO, 0.2f))
        palo(lz, p(x1 + 6f, y1 - 4f, t0 + 2f), p(x1 + 6f, CocinaUrss.SUELO, t0 + 2f), 2.6f, lerp(pintura, C.HIERRO, 0.2f))
        val llave = p(x0 - 3f, y0 + 6f, t0 + 4f); drawCircle(lz.frente(llave, C.ROJO), 1.8f, llave)
        if (caliente) {
            // El paño de cocina secándose encima, de cuadros rojos.
            val pa = listOf(p(x0 + 14f, y0, t1 + 1f), p(x0 + 42f, y0, t1 + 1f), p(x0 + 44f, y0 + 16f, t1 + 1.6f), p(x0 + 30f, y0 + 19f, t1 + 1.8f), p(x0 + 13f, y0 + 15f, t1 + 1.6f))
            val pano = poligono(pa)
            drawPath(pano, lz.frente(pa[3], C.PAPEL))
            clipPath(pano) { for (i in 0..6) { val x = x0 + 14f + i * 5f; drawLine(lz.frente(pa[3], C.ROJO).copy(alpha = 0.6f), p(x, y0, t1 + 1f), p(x, y0 + 20f, t1 + 1.6f), 1.2f) }; for (j in 0..3) drawLine(lz.frente(pa[3], C.ROJO).copy(alpha = 0.45f), p(x0 + 12f, y0 + 3f + j * 5f, t1 + 1f), p(x0 + 46f, y0 + 3f + j * 5f, t1 + 1f), 1f) }
            drawLine(lz.arriba(pa[0], C.PAPEL), pa[0], pa[1], 1.4f)
        }
    }
    // ---------------------------------------------------------------- La mesa con su hule

    /**
     * La mesa de la cocina, de [x0] a [x1] y de [t0] a [t1], con la tapa a [y]: sus patas de madera, el faldón y el hule de
     * cuadros rojos que cuelga por delante y por el lado, con el borde ondulado.
     */
    fun DrawScope.mesa(lz: LuzUrss, x0: Float, x1: Float, t0: Float, t1: Float, y: Float) {
        val suelo = CocinaUrss.SUELO; val cuelga = 9f
        // Las patas: las de atrás primero.
        for ((x, t) in listOf(x0 + 4f to t0 + 4f, x1 - 4f to t0 + 4f, x0 + 4f to t1 - 6f, x1 - 4f to t1 - 6f)) {
            val a = p(x, y + cuelga, t); val b = p(x, suelo, t); val k = K.s(t)
            drawLine(lz.sombra(a, C.MADERA, 0.55f), a, b, 3.4f * k, StrokeCap.Butt)
            drawLine(lz.der(a, lerp(C.MADERA, C.PAPEL, 0.2f)), a + Offset(1f * k, 0f), b + Offset(0.7f * k, 0f), 1.2f * k)
            drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(b.x - 3f * k, b.y - 1f * k), Size(6f * k, 2f * k), blendMode = BlendMode.Multiply)
        }
        // Los travesaños de abajo, entre las patas.
        palo(lz, p(x0 + 4f, suelo - 22f, t1 - 6f), p(minOf(x1 - 4f, 360f), suelo - 22f, t1 - 6f), 2.2f * K.s(t1), C.MADERA)
        palo(lz, p(x0 + 4f, suelo - 22f, t0 + 4f), p(x0 + 4f, suelo - 22f, t1 - 6f), 2f, C.MADERA)
        // La tapa con el hule.
        huleColgando(lz, x0, x1, t0, t1, y, cuelga)
        drawPath(tapa(x0, x1, t0, t1, y), lz.arriba(p((x0 + x1) / 2f, y, (t0 + t1) / 2f), C.PAPEL))
        dibujoDelHule(x0, x1, t0, t1, y) { c, q -> lz.arriba(q, c) }
        // El desgaste del hule: el cerco de una taza, una quemadura de cerilla, el roce del borde.
        val o = p(x0 + 30f, y, t1 - 22f); val k = K.s(t1 - 22f)
        drawOval(lz.en(o, C.TIERRA).copy(alpha = 0.25f), Offset(o.x - 4f * k, o.y - 1.4f * k), Size(8f * k, 2.8f * k), style = Stroke(0.6f))
        val q = p(x0 + 66f, y, t1 - 10f); drawOval(lz.en(q, C.TINTA).copy(alpha = 0.4f), Offset(q.x - 1f, q.y - 0.5f), Size(2.4f, 1.1f))
        drawLine(lz.en(p(x0, y, t1), Color.White).copy(alpha = 0.3f), p(x0 + 2f, y, t1 - 0.5f), p(x1, y, t1 - 0.5f), 0.8f)
    }

    fun tapa(x0: Float, x1: Float, t0: Float, t1: Float, y: Float): Path = poligono(p(x0, y, t0), p(x1, y, t0), p(x1, y, t1), p(x0, y, t1))

    /**
     * El dibujo del hule: cuadros rojos sobre papel, con una cenefa azul y unas florecillas en los cruces. Va en
     * perspectiva, así que marca la fuga de la mesa. [color] dice cómo se ve cada color en cada sitio (para encenderlo
     * donde da el sol o la lámpara).
     */
    fun DrawScope.dibujoDelHule(x0: Float, x1: Float, t0: Float, t1: Float, y: Float, color: (Color, Offset) -> Color) {
        clipPath(tapa(x0, x1, t0, t1, y)) {
            val paso = 7f; val rojo = lerp(C.ROJO, C.PAPEL, 0.55f)
            var i = 0; var x = x0 - 2f
            while (x < x1) {
                var j = 0; var t = t0
                while (t < t1) {
                    if ((i + j) % 2 == 0) { val q = p(x + paso / 2f, y, t + paso / 2f); drawPath(poligono(p(x, y, t), p(x + paso, y, t), p(x + paso, y, t + paso), p(x, y, t + paso)), color(rojo, q).copy(alpha = 0.55f)) }
                    if (i % 2 == 0 && j % 2 == 0) { val q = p(x + paso, y, t + paso); drawCircle(color(C.ROJO, q), 0.6f * K.s(t), q) }
                    t += paso; j++
                }
                x += paso; i++
            }
            // La cenefa de los bordes.
            for ((a, b) in listOf(p(x0 + 3f, y, t0) to p(x0 + 3f, y, t1), p(x0, y, t1 - 3f) to p(x1, y, t1 - 3f))) drawLine(color(C.AZUL, a).copy(alpha = 0.6f), a, b, 1.2f)
        }
    }

    /** El hule que cuelga por delante y por el lado de la izquierda, con el borde ondulado y los pliegues de las esquinas. */
    private fun DrawScope.huleColgando(lz: LuzUrss, x0: Float, x1: Float, t0: Float, t1: Float, y: Float, cuelga: Float) {
        // Por delante, de cara a quien mira (a contraluz de día).
        val f0 = p(x0, y, t1); val f1 = p(x1 + 10f, y, t1); val k = K.s(t1)
        val frente = Path().apply {
            moveTo(f0.x, f0.y); lineTo(f1.x, f1.y); lineTo(f1.x, f1.y + cuelga * k)
            var x = x1 + 10f; var s = 1f
            while (x > x0) { val a = p(x, y + cuelga, t1); val b = p(x - 6f, y + cuelga, t1); cubicTo(a.x - 2f * k, a.y + 1.6f * s * k, b.x + 2f * k, b.y + 1.6f * s * k, b.x, b.y); x -= 6f; s = -s }
            lineTo(f0.x - 1f, f0.y + (cuelga + 2f) * k); close()
        }
        val mf = p((x0 + x1) / 2f, y + 4f, t1)
        drawPath(frente, lz.frente(mf, C.PAPEL))
        clipPath(frente) {
            var x = x0; var i = 0
            while (x < x1 + 10f) { if (i % 2 == 0) drawPath(poligono(p(x, y, t1), p(x + 7f, y, t1), p(x + 7f, y + cuelga + 2f, t1), p(x, y + cuelga + 2f, t1)), lz.frente(mf, lerp(C.ROJO, C.PAPEL, 0.55f)).copy(alpha = 0.5f)); x += 7f; i++ }
            drawLine(lz.frente(mf, C.AZUL).copy(alpha = 0.55f), p(x0, y + 2.4f, t1), p(x1 + 10f, y + 2.4f, t1), 1.1f)
            // El pliegue de la esquina, más oscuro.
            drawRect(Brush.horizontalGradient(0f to C.SOMBRA.copy(alpha = 0.35f), 1f to Color.Transparent, startX = f0.x, endX = f0.x + 8f), Offset(f0.x, f0.y), Size(8f, cuelga * k + 3f), blendMode = BlendMode.Multiply)
        }
        // Por el lado de la izquierda, en escorzo (mira a la ventana y al fogón).
        val lado = Path().apply {
            val a = p(x0, y, t0); val b = p(x0, y, t1); moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(b.x - 1f, b.y + (cuelga + 2f) * k)
            var t = t1; var s = 1f
            while (t > t0) { val c = p(x0, y + cuelga, t - 6f); cubicTo(c.x - 0.6f * s, c.y + 1.2f * s, c.x - 0.6f * s, c.y + 1.2f * s, c.x, c.y); t -= 6f; s = -s }
            close()
        }
        val ml = p(x0, y + 4f, (t0 + t1) / 2f)
        drawPath(lado, lz.izq(ml, C.PAPEL))
        clipPath(lado) { var t = t0; var i = 0; while (t < t1) { if (i % 2 == 0) drawPath(poligono(p(x0, y, t), p(x0, y, t + 7f), p(x0, y + cuelga + 2f, t + 7f), p(x0, y + cuelga + 2f, t)), lz.izq(ml, lerp(C.ROJO, C.PAPEL, 0.55f)).copy(alpha = 0.5f)); t += 7f; i++ } }
    }

    // ---------------------------------------------------------------- El samovar, la tetera y el vaso

    /**
     * El samovar eléctrico de latón: el pie cuadrado con sus patitas, el cuerpo de urna con sus aros, las asas de madera
     * negra, el grifo con su llave y el platillo debajo, la corona y, encima, la tetera de porcelana con flores pintadas.
     * El latón devuelve la ventana (un reflejo largo y claro) y, de noche, la lámpara; a contraluz, un filo de luz.
     */
    fun DrawScope.samovar(lz: LuzUrss, pie: Offset, k: Float, conTetera: Boolean = true) {
        val L = C.LATON
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(pie.x - 13f * k, pie.y - 2.6f * k), Size(26f * k, 5.6f * k), blendMode = BlendMode.Multiply)
        // El pie y sus patitas.
        for (s in listOf(-1f, 1f)) drawOval(lz.en(pie, lerp(L, C.TINTA, 0.3f)), Offset(pie.x + s * 7f * k - 1.6f * k, pie.y - 2f * k), Size(3.2f * k, 2.6f * k))
        val base = Path().apply { moveTo(pie.x - 9f * k, pie.y - 1.6f * k); lineTo(pie.x - 8f * k, pie.y - 6f * k); lineTo(pie.x + 8f * k, pie.y - 6f * k); lineTo(pie.x + 9f * k, pie.y - 1.6f * k); close() }
        laton(lz, base, pie.x - 9f * k, pie.x + 9f * k, pie.y - 4f * k)
        // El cuello y el cuerpo de urna.
        val cuello = Path().apply { addRect(Rect(pie.x - 4f * k, pie.y - 10f * k, pie.x + 4f * k, pie.y - 5.6f * k)) }
        laton(lz, cuello, pie.x - 4f * k, pie.x + 4f * k, pie.y - 8f * k)
        val y0 = pie.y - 10f * k; val y1 = pie.y - 34f * k
        val cuerpo = Path().apply {
            moveTo(pie.x - 6f * k, y0); cubicTo(pie.x - 14f * k, y0 - 2f * k, pie.x - 13.6f * k, y0 - 12f * k, pie.x - 12.4f * k, y0 - 18f * k)
            cubicTo(pie.x - 11.6f * k, y1 + 3f * k, pie.x - 10f * k, y1 + 1f * k, pie.x - 8f * k, y1); lineTo(pie.x + 8f * k, y1)
            cubicTo(pie.x + 10f * k, y1 + 1f * k, pie.x + 11.6f * k, y1 + 3f * k, pie.x + 12.4f * k, y0 - 18f * k)
            cubicTo(pie.x + 13.6f * k, y0 - 12f * k, pie.x + 14f * k, y0 - 2f * k, pie.x + 6f * k, y0); close()
        }
        laton(lz, cuerpo, pie.x - 13.6f * k, pie.x + 13.6f * k, (y0 + y1) / 2f)
        clipPath(cuerpo) {
            // Los aros y las estrías del latón.
            for (f in listOf(0.18f, 0.55f, 0.86f)) { val yy = y0 + (y1 - y0) * f; drawLine(lz.sombra(pie, L, 0.5f).copy(alpha = 0.7f), Offset(pie.x - 14f * k, yy), Offset(pie.x + 14f * k, yy), 0.9f * k); drawLine(lz.en(pie, lerp(L, Color.White, 0.5f)).copy(alpha = 0.5f), Offset(pie.x - 14f * k, yy + 0.8f * k), Offset(pie.x + 14f * k, yy + 0.8f * k), 0.5f * k) }
            for (i in -3..3) drawLine(lz.sombra(pie, L, 0.3f).copy(alpha = 0.25f), Offset(pie.x + i * 3.4f * k, y0 - 3f * k), Offset(pie.x + i * 3.1f * k, y0 - 13f * k), 0.5f * k)
            // El reflejo de la ventana: una franja clara con la cruz del marco, y el de la lámpara de noche.
            drawRect(lz.en(pie, Color.White).copy(alpha = 0.35f * lz.dia + 0.05f), Offset(pie.x + 3f * k, y1), Size(3.6f * k, y0 - y1))
            drawLine(lz.sombra(pie, L, 0.5f).copy(alpha = 0.4f * lz.dia), Offset(pie.x + 2.6f * k, (y0 + y1) / 2f), Offset(pie.x + 7f * k, (y0 + y1) / 2f), 0.7f * k)
            if (lz.lampara) {
                // La lámpara, a la derecha y por delante: una franja naranja en el lado de la luz y su punto de brillo.
                drawRect(Brush.horizontalGradient(0f to Color.Transparent, 0.6f to C.LAMPARA.copy(alpha = 0.35f * lz.noche), 1f to Color.Transparent, startX = pie.x + 4f * k, endX = pie.x + 13f * k), Offset(pie.x + 4f * k, y1), Size(9f * k, y0 - y1), blendMode = BlendMode.Plus)
                mancha(Offset(pie.x + 7.4f * k, y1 + 7f * k), 2.6f * k, 3.4f * k, lerp(C.LAMPARA, Color.White, 0.6f), 0.6f + 0.3f * lz.noche)
            }
            // De noche, el lado de la ventana recibe el azul de la nieve y de la luna.
            if (lz.noche > 0.3f) drawRect(Brush.horizontalGradient(0f to C.LUNA.copy(alpha = 0.3f * lz.noche), 1f to Color.Transparent, startX = pie.x - 13f * k, endX = pie.x - 6f * k), Offset(pie.x - 14f * k, y1), Size(8f * k, y0 - y1), blendMode = BlendMode.Plus)
            // El fondo oscuro que devuelve el latón (la cocina en penumbra).
            drawRect(lz.sombra(pie, C.TINTA, 0.4f).copy(alpha = 0.3f), Offset(pie.x - 9f * k, y1), Size(3.6f * k, y0 - y1))
        }
        // El filo de luz de la ventana, a contraluz, por el borde de arriba y el de la derecha.
        drawPath(Path().apply { moveTo(pie.x - 8f * k, y1); lineTo(pie.x + 8f * k, y1); cubicTo(pie.x + 10f * k, y1 + 1f * k, pie.x + 11.6f * k, y1 + 3f * k, pie.x + 12.4f * k, y0 - 18f * k) }, lz.filo.copy(alpha = 0.55f), style = Stroke(0.9f * k))
        // Las asas, de madera negra con su argolla.
        for (s in listOf(-1f, 1f)) {
            val a = Offset(pie.x + s * 12.4f * k, y1 + 5f * k)
            drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + s * 4f * k, a.y - 1f * k, a.x + s * 5f * k, a.y + 4f * k, a.x + s * 3.6f * k, a.y + 6f * k) }, lz.en(a, lerp(L, C.TINTA, 0.3f)), style = Stroke(0.9f * k))
            drawLine(lz.en(a, C.TINTA), a + Offset(s * 2.6f * k, 1.4f * k), a + Offset(s * 5.6f * k, 2.2f * k), 2.2f * k, StrokeCap.Round)
        }
        // El grifo con su llave, de cara a quien mira, y el platillo con las gotas.
        val g = Offset(pie.x - 2f * k, y0 - 4f * k)
        drawOval(lz.en(pie, C.ESMALTE), Offset(g.x - 4f * k, pie.y - 2.8f * k), Size(8f * k, 2.4f * k))
        drawOval(lz.sombra(pie, C.AZUL, 0.3f).copy(alpha = 0.5f), Offset(g.x - 2.4f * k, pie.y - 2.4f * k), Size(4.8f * k, 1.3f * k))
        drawLine(lz.en(g, lerp(L, C.TINTA, 0.2f)), g, g + Offset(-2.6f * k, 3.4f * k), 1.8f * k, StrokeCap.Round)
        drawLine(lz.en(g, L), g + Offset(-0.6f * k, -2.6f * k), g + Offset(1.6f * k, -2.6f * k), 1f * k, StrokeCap.Round)
        drawCircle(lz.en(g, lerp(L, Color.White, 0.5f)), 0.9f * k, g + Offset(-0.6f * k, -2.6f * k))
        // La corona y la tetera.
        val cor = Path().apply { moveTo(pie.x - 8f * k, y1); lineTo(pie.x - 5f * k, y1 - 3f * k); lineTo(pie.x + 5f * k, y1 - 3f * k); lineTo(pie.x + 8f * k, y1); close() }
        laton(lz, cor, pie.x - 8f * k, pie.x + 8f * k, y1 - 1.5f * k)
        if (conTetera) tetera(lz, Offset(pie.x, y1 - 3f * k), k * 0.95f)
    }

    /** El latón: oro viejo con su brillo y su sombra violeta; de noche, encendido por la lámpara. */
    private fun DrawScope.laton(lz: LuzUrss, forma: Path, x0: Float, x1: Float, y: Float) {
        val izq = lz.izq(Offset(x0, y), lerp(C.LATON, Color.White, 0.15f)); val der = lz.der(Offset(x1, y), lerp(C.LATON, Color.White, 0.3f)); val med = lz.en(Offset((x0 + x1) / 2f, y), lerp(C.LATON, C.TINTA, 0.28f))
        drawPath(forma, Brush.horizontalGradient(0f to izq, 0.2f to lerp(izq, med, 0.3f), 0.45f to med, 0.7f to lerp(med, der, 0.6f), 0.86f to der, 1f to lerp(der, med, 0.5f), startX = x0, endX = x1))
    }

    /** La tetera de porcelana blanca con flores rojas y azules pintadas, con su pico, su asa y su tapa. */
    fun DrawScope.tetera(lz: LuzUrss, pie: Offset, k: Float) {
        val w = 6.6f * k; val h = 7.4f * k
        val forma = Path().apply { moveTo(pie.x - w * 0.6f, pie.y); cubicTo(pie.x - w * 1.15f, pie.y - h * 0.2f, pie.x - w * 1.1f, pie.y - h * 0.9f, pie.x - w * 0.5f, pie.y - h); lineTo(pie.x + w * 0.5f, pie.y - h); cubicTo(pie.x + w * 1.1f, pie.y - h * 0.9f, pie.x + w * 1.15f, pie.y - h * 0.2f, pie.x + w * 0.6f, pie.y); close() }
        drawPath(Path().apply { moveTo(pie.x - w * 0.9f, pie.y - h * 0.45f); lineTo(pie.x - w * 1.8f, pie.y - h * 1.1f); lineTo(pie.x - w * 1.6f, pie.y - h * 1.15f); lineTo(pie.x - w * 0.8f, pie.y - h * 0.75f); close() }, lz.izq(pie, C.ESMALTE))
        drawPath(Path().apply { moveTo(pie.x + w * 0.95f, pie.y - h * 0.75f); cubicTo(pie.x + w * 1.8f, pie.y - h * 0.8f, pie.x + w * 1.7f, pie.y - h * 0.1f, pie.x + w * 0.9f, pie.y - h * 0.25f) }, lz.der(pie, C.ESMALTE), style = Stroke(1f * k))
        cuerpo(lz.a, forma, pie.x - w * 1.1f, pie.x + w * 1.1f, pie.y - h / 2f, C.ESMALTE)
        // Las flores pintadas: una rosa roja, hojas, nomeolvides azules, y el filete de oro.
        val c = Offset(pie.x - w * 0.15f, pie.y - h * 0.5f)
        drawCircle(lz.en(c, C.ROJO), 1.5f * k, c); drawCircle(lz.en(c, lerp(C.ROJO, C.PAPEL, 0.4f)), 0.7f * k, c + Offset(-0.3f * k, -0.3f * k))
        for (a in listOf(-0.8f, 2.4f)) drawOval(lz.en(c, C.VERDE), Offset(c.x + cos(a) * 2.2f * k - 1f * k, c.y + sin(a) * 1.2f * k - 0.5f * k), Size(2f * k, 1f * k))
        for (d in listOf(Offset(3.2f, -1.4f), Offset(-3.4f, 1.2f))) drawCircle(lz.en(c, C.AZUL), 0.7f * k, c + d * k)
        drawLine(lz.en(pie, C.ORO), Offset(pie.x - w * 0.5f, pie.y - h + 0.6f * k), Offset(pie.x + w * 0.5f, pie.y - h + 0.6f * k), 0.6f * k)
        drawOval(lz.arriba(pie, C.ESMALTE), Offset(pie.x - w * 0.5f, pie.y - h - 1.3f * k), Size(w, 2.2f * k))
        drawCircle(lz.arriba(pie, C.ROJO), 1f * k, Offset(pie.x, pie.y - h - 1.8f * k))
    }

    /**
     * El vaso de té en su portavasos de metal calado, con la cucharilla dentro: el té, ámbar, se enciende con la luz que
     * le llega. [lleno]: con té; si no, el vaso vacío.
     */
    fun DrawScope.vasoDeTe(lz: LuzUrss, pie: Offset, k: Float, lleno: Boolean) {
        val w = 3.6f * k; val h = 11f * k
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(pie.x - w * 1.4f, pie.y - 1.4f * k), Size(w * 3f, 2.8f * k), blendMode = BlendMode.Multiply)
        // El cristal con el té: más claro arriba, donde le da la luz.
        val vaso = Path().apply { addRect(Rect(pie.x - w, pie.y - h, pie.x + w, pie.y - 2f * k)) }
        val te = lerp(C.NARANJA, C.LADRILLO, 0.45f)
        if (lleno) drawPath(vaso, Brush.verticalGradient(0f to lz.en(pie, lerp(te, C.LATON, 0.4f)), 1f to lz.en(pie, lerp(te, C.TINTA, 0.35f)), startY = pie.y - h, endY = pie.y))
        else drawPath(vaso, lz.en(pie, C.CROMO).copy(alpha = 0.35f))
        drawLine(lz.en(pie, Color.White).copy(alpha = 0.6f), Offset(pie.x - w * 0.55f, pie.y - h + 1f), Offset(pie.x - w * 0.55f, pie.y - 3f * k), 0.8f * k)
        // La cucharilla, apoyada en el borde.
        drawLine(lz.en(pie, C.CROMO), Offset(pie.x + w * 0.3f, pie.y - h * 0.5f), Offset(pie.x + w * 1.4f, pie.y - h - 3f * k), 0.7f * k)
        // El portavasos: la base, el calado de la panza y el asa.
        val porta = Path().apply { moveTo(pie.x - w * 1.08f, pie.y - h * 0.55f); lineTo(pie.x + w * 1.08f, pie.y - h * 0.55f); lineTo(pie.x + w * 1.1f, pie.y - 1f * k); lineTo(pie.x + w * 1.4f, pie.y); lineTo(pie.x - w * 1.4f, pie.y); lineTo(pie.x - w * 1.1f, pie.y - 1f * k); close() }
        cuerpo(lz.a, porta, pie.x - w * 1.4f, pie.x + w * 1.4f, pie.y - h * 0.3f, lerp(C.CROMO, C.LATON, 0.35f))
        clipPath(porta) { for (i in -2..2) drawOval(lz.sombra(pie, C.TINTA, 0.5f).copy(alpha = 0.6f), Offset(pie.x + i * 1.6f * k - 0.5f * k, pie.y - h * 0.46f), Size(1f * k, 2.6f * k)) }
        drawPath(Path().apply { moveTo(pie.x + w * 1.08f, pie.y - h * 0.5f); cubicTo(pie.x + w * 2.4f, pie.y - h * 0.5f, pie.x + w * 2.4f, pie.y - h * 0.08f, pie.x + w * 1.1f, pie.y - h * 0.12f) }, lz.der(pie, lerp(C.CROMO, C.LATON, 0.35f)), style = Stroke(0.9f * k))
    }

    /** La azucarera de cristal tallado con su tapa. */
    fun DrawScope.azucarera(lz: LuzUrss, pie: Offset, k: Float) {
        val w = 4.4f * k; val h = 7f * k
        val forma = Path().apply { moveTo(pie.x - w * 0.6f, pie.y); lineTo(pie.x - w, pie.y - h * 0.4f); lineTo(pie.x - w, pie.y - h); lineTo(pie.x + w, pie.y - h); lineTo(pie.x + w, pie.y - h * 0.4f); lineTo(pie.x + w * 0.6f, pie.y); close() }
        drawOval(C.SOMBRA.copy(alpha = 0.25f), Offset(pie.x - w * 1.2f, pie.y - 1f * k), Size(w * 2.4f, 2f * k), blendMode = BlendMode.Multiply)
        drawPath(forma, lz.en(pie, lerp(C.CROMO, C.PAPEL, 0.4f)).copy(alpha = 0.7f))
        clipPath(forma) { drawRect(lz.en(pie, C.PAPEL), Offset(pie.x - w, pie.y - h * 0.62f), Size(w * 2f, h * 0.62f)); for (i in -3..3) drawLine(lz.en(pie, Color.White).copy(alpha = 0.35f), Offset(pie.x + i * 1.6f * k, pie.y - h), Offset(pie.x + i * 1.6f * k + 1.6f * k, pie.y - h * 0.4f), 0.5f * k) }
        drawOval(lz.arriba(pie, lerp(C.CROMO, C.PAPEL, 0.4f)), Offset(pie.x - w * 1.05f, pie.y - h - 1.2f * k), Size(w * 2.1f, 2.4f * k))
        drawCircle(lz.arriba(pie, C.CROMO), 1f * k, Offset(pie.x, pie.y - h - 1.8f * k))
    }

    /** El pan negro bajo un paño de cuadros echado a medias. */
    fun DrawScope.panConPano(lz: LuzUrss, pie: Offset, k: Float) {
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(pie.x - 13f * k, pie.y - 2f * k), Size(26f * k, 4f * k), blendMode = BlendMode.Multiply)
        val pan = Path().apply { moveTo(pie.x - 11f * k, pie.y); cubicTo(pie.x - 12f * k, pie.y - 7f * k, pie.x + 12f * k, pie.y - 8f * k, pie.x + 11f * k, pie.y); close() }
        cuerpo(lz.a, pan, pie.x - 12f * k, pie.x + 12f * k, pie.y - 3f * k, lerp(C.LATON, C.LADRILLO, 0.45f))
        for (i in 0..2) drawLine(lz.sombra(pie, C.LADRILLO, 0.5f), Offset(pie.x - 6f * k + i * 5f * k, pie.y - 6f * k), Offset(pie.x - 3f * k + i * 5f * k, pie.y - 3.6f * k), 0.8f * k)
        val pano = Path().apply { moveTo(pie.x - 2f * k, pie.y + 0.6f * k); cubicTo(pie.x - 4f * k, pie.y - 6f * k, pie.x + 6f * k, pie.y - 9f * k, pie.x + 13f * k, pie.y - 5f * k); lineTo(pie.x + 14f * k, pie.y + 1f * k); close() }
        drawPath(pano, lz.frente(pie, C.PAPEL))
        clipPath(pano) { for (i in 0..6) drawLine(lz.frente(pie, C.ROJO).copy(alpha = 0.55f), Offset(pie.x - 2f * k + i * 2.6f * k, pie.y - 9f * k), Offset(pie.x - 2f * k + i * 2.6f * k, pie.y + 1f * k), 0.7f * k); for (j in 0..3) drawLine(lz.frente(pie, C.ROJO).copy(alpha = 0.4f), Offset(pie.x - 4f * k, pie.y - 8f * k + j * 2.6f * k), Offset(pie.x + 15f * k, pie.y - 8f * k + j * 2.6f * k), 0.6f * k) }
    }

    // ---------------------------------------------------------------- La lámpara de pantalla

    /**
     * La lámpara de pantalla de tela naranja con flecos, colgada de su cordón: vista desde abajo, se le ve la boca y
     * dentro la bombilla. Encendida, la tela se transparenta y brilla; apagada, es un naranja apagado con sus pliegues.
     * [desnuda]: sin la pantalla, solo la bombilla (los días de la mudanza).
     */
    fun DrawScope.lampara(lz: LuzUrss, arriba: Offset, abajo: Offset, ancho: Float, encendida: Boolean, desnuda: Boolean) {
        val w = ancho / 2f; val wa = w * 0.26f; val h = abajo.y - arriba.y
        // El cordón trenzado, que sube y sale por el borde.
        drawLine(lz.en(arriba, C.TINTA), Offset(arriba.x + 0.6f, -2f), Offset(arriba.x, arriba.y + (if (desnuda) h * 0.6f else 0f)), 1.2f)
        if (desnuda) {
            val b = Offset(arriba.x, arriba.y + h * 0.7f)
            drawRect(lz.en(b, C.TINTA), Offset(b.x - 2f, b.y - 5f), Size(4f, 5f))
            drawCircle(if (encendida) lerp(C.LAMPARA, Color.White, 0.7f) else lz.en(b, lerp(C.CROMO, C.PAPEL, 0.4f)), 4.4f, b + Offset(0f, 4f))
            return
        }
        val conoExt = Path().apply {
            moveTo(arriba.x - wa, arriba.y); lineTo(arriba.x + wa, arriba.y)
            cubicTo(arriba.x + wa * 1.6f, arriba.y + h * 0.4f, abajo.x + w * 0.8f, abajo.y - h * 0.4f, abajo.x + w, abajo.y)
            // El borde de abajo, en ondas.
            var u = 1f; while (u > -0.99f) { val x = abajo.x + w * u; val x2 = abajo.x + w * (u - 0.2f); cubicTo(x - w * 0.04f, abajo.y + 3f, x2 + w * 0.04f, abajo.y + 3f, x2, abajo.y); u -= 0.2f }
            cubicTo(abajo.x - w * 0.8f, abajo.y - h * 0.4f, arriba.x - wa * 1.6f, arriba.y + h * 0.4f, arriba.x - wa, arriba.y); close()
        }
        // La boca, vista desde abajo: el forro de dentro, encendido o en sombra, con la bombilla.
        val boca = Rect(abajo.x - w, abajo.y - 3.6f, abajo.x + w, abajo.y + 4.4f)
        drawOval(if (encendida) lerp(C.LAMPARA, Color.White, 0.35f) else lz.sombra(abajo, C.NARANJA, 0.7f), boca.topLeft, boca.size)
        if (encendida) drawCircle(lerp(C.LAMPARA, Color.White, 0.85f), 3.4f, Offset(abajo.x, abajo.y - 1f))
        val tela = if (encendida) lerp(C.NARANJA, C.LAMPARA, 0.55f) else C.NARANJA
        val izq = if (encendida) lerp(tela, C.LAMPARA, 0.3f) else lz.izq(arriba, tela); val der = if (encendida) lerp(tela, C.NARANJA, 0.4f) else lz.der(arriba, lerp(tela, C.PAPEL, 0.1f))
        val med = if (encendida) lerp(tela, Color.White, 0.18f) else lz.en(arriba, tela)
        drawPath(conoExt, Brush.horizontalGradient(0f to izq, 0.35f to med, 0.7f to lerp(med, der, 0.5f), 1f to der, startX = abajo.x - w, endX = abajo.x + w))
        clipPath(conoExt) {
            // Los pliegues de la tela, que bajan del aro de arriba, y el brillo del raso.
            for (i in -5..5) { val u = i / 5f; drawLine((if (encendida) lerp(C.LADRILLO, C.NARANJA, 0.5f) else lz.sombra(arriba, tela, 0.6f)).copy(alpha = 0.2f), Offset(arriba.x + wa * u, arriba.y), Offset(abajo.x + w * u * 1.02f, abajo.y + 2f), 1.4f) }
            drawLine((if (encendida) lerp(C.LAMPARA, Color.White, 0.5f) else lz.en(arriba, lerp(tela, C.PAPEL, 0.5f))).copy(alpha = 0.35f), Offset(arriba.x - wa * 0.3f, arriba.y + 1f), Offset(abajo.x - w * 0.42f, abajo.y - 2f), 2.4f)
            // La costura del aro de abajo y el ribete.
            drawLine((if (encendida) lerp(C.ORO, Color.White, 0.3f) else lz.en(abajo, C.ORO)).copy(alpha = 0.8f), Offset(abajo.x - w, abajo.y - 3f), Offset(abajo.x + w, abajo.y - 3f), 1.2f)
            if (encendida) drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to lerp(C.LAMPARA, Color.White, 0.3f).copy(alpha = 0.4f), startY = arriba.y, endY = abajo.y), Offset(abajo.x - w, arriba.y), Size(ancho, h))
        }
        // El aro de arriba y el portalámparas.
        drawOval(lz.en(arriba, lerp(C.ORO, C.TINTA, 0.3f)), Offset(arriba.x - wa, arriba.y - 1.2f), Size(wa * 2f, 2.4f))
        // Los flecos, de hilo de oro, que cuelgan del borde ondulado.
        var x = abajo.x - w + 1f; var i = 0
        while (x < abajo.x + w) {
            val y = abajo.y + 1.6f + (if (i % 2 == 0) 0.6f else 0f); val largo = 7f + (i % 3)
            val c = if (encendida) lerp(C.ORO, C.LAMPARA, 0.5f) else lz.en(Offset(x, y), C.ORO)
            drawLine(c, Offset(x, y), Offset(x + 0.4f, y + largo), 0.7f)
            if (i % 4 == 0) drawCircle(c, 0.9f, Offset(x + 0.4f, y + largo + 0.6f))
            x += 1.6f; i++
        }
    }

    // ---------------------------------------------------------------- El taburete

    /** El taburete de madera de cuatro patas, con el asiento gastado y una pata reparada con alambre. */
    fun DrawScope.taburete(lz: LuzUrss, x: Float, t: Float, ancho: Float, alto: Float) {
        val suelo = CocinaUrss.SUELO; val y = suelo - alto; val w = ancho / 2f
        for ((dx, dt) in listOf(-w + 2f to -w + 2f, w - 2f to -w + 2f, -w + 2f to w - 2f, w - 2f to w - 2f)) {
            val a = p(x + dx, y + 3f, t + dt); val b = p(x + dx * 1.18f, suelo, t + dt * 1.18f)
            palo(lz, a, b, 2.6f * K.s(t + dt), C.MADERA, if (dx > 0f) 1f else -1f)
        }
        palo(lz, p(x - w + 2.5f, suelo - 14f, t + w - 1.6f), p(x + w - 2.5f, suelo - 14f, t + w - 1.6f), 1.8f * K.s(t + w), C.MADERA)
        caja(lz, x - w, x + w, y, y + 3f, t - w, t + w, lerp(C.MADERA, C.TINTA, 0.1f), lerp(C.MADERA, C.PAPEL, 0.25f), C.MADERA)
        // El asiento gastado por el centro y la veta.
        val c = p(x, y, t); val k = K.s(t)
        drawOval(lz.arriba(c, lerp(C.MADERA, C.PAPEL, 0.4f)).copy(alpha = 0.4f), Offset(c.x - 5f * k, c.y - 1.4f * k), Size(10f * k, 2.8f * k))
        for (i in -1..1) drawLine(lz.sombra(c, C.MADERA, 0.4f).copy(alpha = 0.4f), p(x - w + 2f, y, t + i * 4f), p(x + w - 2f, y, t + i * 4f + 1f), 0.5f)
        val al = p(x + w * 1.05f, suelo - 8f, t + w * 1.05f); drawLine(lz.en(al, C.CROMO), al + Offset(-1.4f * k, 0f), al + Offset(1.4f * k, 0f), 0.6f); drawLine(lz.en(al, C.CROMO), al + Offset(-1.4f * k, 1.2f), al + Offset(1.4f * k, 1.2f), 0.6f)
    }

    // ---------------------------------------------------------------- Lo de las paredes

    /** La radio de cable: una cajita de baquelita con su rejilla de tela y la rueda del volumen, colgada de un clavo, con su cable por la pared. */
    fun DrawScope.radioDeCable(lz: LuzUrss, x: Float, y: Float) {
        val a = p(x, y, 1.6f); val b = p(x + 18f, y + 15f, 1.6f)
        drawRect(C.SOMBRA.copy(alpha = 0.3f), a + Offset(1.6f, 1.8f), Size(b.x - a.x, b.y - a.y), blendMode = BlendMode.Multiply)
        val caja = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(a, b), androidx.compose.ui.geometry.CornerRadius(2.4f))) }
        cuerpo(lz.a, caja, a.x, b.x, (a.y + b.y) / 2f, lerp(C.MADERA, C.LADRILLO, 0.3f))
        val r = Rect(a.x + 2f, a.y + 2f, a.x + 11f, b.y - 2f)
        drawOval(lz.en(a, lerp(C.PAPEL, C.LATON, 0.4f)), r.topLeft, r.size)
        for (i in 0..4) drawLine(lz.sombra(a, C.LATON, 0.5f).copy(alpha = 0.6f), Offset(r.left + 1f, r.top + 2f + i * 2.2f), Offset(r.right - 1f, r.top + 2f + i * 2.2f), 0.4f)
        val rueda = Offset(b.x - 3.4f, (a.y + b.y) / 2f); drawCircle(lz.en(rueda, C.TINTA), 2.2f, rueda); drawLine(lz.en(rueda, C.PAPEL), rueda, rueda + Offset(0f, -1.8f), 0.5f)
        drawLine(lz.arriba(a, Color.White).copy(alpha = 0.3f), Offset(a.x + 1f, a.y + 0.6f), Offset(b.x - 1f, a.y + 0.6f), 0.6f)
        // El cable, sujeto con grapas, que baja por la pared.
        val c0 = Offset(a.x + 4f, b.y); var q = c0
        for (i in 1..6) { val n = Offset(c0.x - 2f - i * 0.4f, c0.y + i * 7f); drawLine(lz.en(q, C.TINTA).copy(alpha = 0.8f), q, n, 0.6f); drawLine(lz.en(n, C.PAPEL), n + Offset(-0.8f, 0f), n + Offset(0.8f, 0f), 0.5f); q = n }
    }

    /** El calendario de taco: el cartón de color con su estrella (sin cifras) y el taco de hojas, cada vez más fino según pasa el año. */
    fun DrawScope.calendario(lz: LuzUrss, x: Float, y: Float, avance: Float, fiesta: Boolean) {
        val a = p(x, y, 0.8f); val b = p(x + 15f, y + 24f, 0.8f)
        drawRect(C.SOMBRA.copy(alpha = 0.3f), a + Offset(1.2f, 1.6f), Size(b.x - a.x, b.y - a.y), blendMode = BlendMode.Multiply)
        drawRect(lz.en(a, C.ROJO), a, Size(b.x - a.x, b.y - a.y))
        val c = Offset((a.x + b.x) / 2f, a.y + 5f)
        val estrella = Path(); for (i in 0..9) { val r = if (i % 2 == 0) 3f else 1.3f; val ang = (-PI / 2 + i * PI / 5).toFloat(); val q = c + Offset(cos(ang) * r, sin(ang) * r); if (i == 0) estrella.moveTo(q.x, q.y) else estrella.lineTo(q.x, q.y) }; estrella.close()
        drawPath(estrella, lz.en(c, C.ORO))
        // El taco: hojas blancas, más gordo en enero que en diciembre; la raya de la hoja de hoy, roja si es fiesta.
        val grueso = 1f + 5f * (1f - avance)
        drawRect(lz.frente(a, C.PAPEL), Offset(a.x + 2f, a.y + 10f), Size(b.x - a.x - 4f, b.y - a.y - 12f))
        drawRect(lz.sombra(a, C.PAPEL, 0.4f), Offset(a.x + 2f, b.y - 2f), Size(b.x - a.x - 4f, grueso * 0.4f))
        for (i in 0..3) drawLine(lz.sombra(a, C.TINTA, 0.3f).copy(alpha = 0.5f), Offset(a.x + 4f, a.y + 15f + i * 2f), Offset(b.x - 4f - (i % 2) * 2f, a.y + 15f + i * 2f), 0.5f)
        drawRect(lz.frente(a, if (fiesta) C.ROJO else C.TINTA).copy(alpha = 0.8f), Offset(a.x + 4f, a.y + 12f), Size(b.x - a.x - 8f, 1.6f))
        drawLine(lz.frente(a, C.PAPEL), Offset(a.x + 3f, a.y + 9.6f), Offset(b.x - 3f, a.y + 9.6f), 1.2f)
    }

    /** La repisa de madera con sus ménsulas: los libros sin título, de lomos gastados, y los tarros de conservas (pepinos, tomates, mermelada). */
    fun DrawScope.repisa(lz: LuzUrss, x0: Float, x1: Float, y: Float, az: Azar) {
        caja(lz, x0, x1, y, y + 2.4f, 0f, 9f, C.MADERA, lerp(C.MADERA, C.PAPEL, 0.2f))
        for (x in listOf(x0 + 6f, x1 - 6f)) drawPath(poligono(p(x - 1f, y + 2.4f, 1f), p(x + 1f, y + 2.4f, 1f), p(x + 1f, y + 10f, 1f)), lz.frente(p(x, y + 6f, 1f), C.MADERA))
        drawRect(Brush.verticalGradient(0f to C.SOMBRA.copy(alpha = 0.35f), 1f to Color.Transparent, startY = p(x0, y + 2.4f, 0f).y, endY = p(x0, y + 9f, 0f).y), p(x0, y + 2.4f, 0f), Size(p(x1, y, 0f).x - p(x0, y, 0f).x, 7f), blendMode = BlendMode.Multiply)
        // Los libros, de pie, de colores de lomo, con sus filetes.
        var x = x0 + 3f; val lomos = listOf(C.ROJO, C.AZUL, lerp(C.VERDE, C.TINTA, 0.3f), C.LATON, lerp(C.MADERA, C.TINTA, 0.3f), C.ROJO, C.PAPEL)
        var i = 0
        while (x < x0 + 30f) {
            val ancho = az.entre(2.2f, 3.8f); val alto = az.entre(13f, 17f); val c = lomos[i % lomos.size]
            val a = p(x, y - alto, 4f); val b = p(x + ancho, y, 4f)
            drawRect(lz.frente(a, c), a, Size(b.x - a.x, b.y - a.y))
            drawLine(lz.en(a, lerp(c, C.ORO, 0.6f)).copy(alpha = 0.7f), Offset(a.x + 0.2f, a.y + 2.4f), Offset(b.x - 0.2f, a.y + 2.4f), 0.5f)
            drawLine(lz.en(a, lerp(c, C.ORO, 0.6f)).copy(alpha = 0.5f), Offset(a.x + 0.2f, b.y - 2.4f), Offset(b.x - 0.2f, b.y - 2.4f), 0.5f)
            drawLine(lz.izq(a, lerp(c, Color.White, 0.3f)).copy(alpha = 0.5f), Offset(a.x + 0.3f, a.y), Offset(a.x + 0.3f, b.y), 0.4f)
            x += ancho + 0.2f; i++
        }
        // Un libro tumbado, envuelto en papel de periódico (de los que circulan copiados).
        val ta = p(x + 1f, y - 3f, 5f); val tb = p(x + 18f, y, 5f)
        drawRect(lz.frente(ta, lerp(C.PAPEL, C.HORMIGON, 0.3f)), ta, Size(tb.x - ta.x, tb.y - ta.y))
        for (j in 0..2) drawLine(lz.sombra(ta, C.TINTA, 0.3f).copy(alpha = 0.4f), Offset(ta.x + 1f, ta.y + 0.8f + j * 0.7f), Offset(tb.x - 1f, ta.y + 0.8f + j * 0.7f), 0.3f)
        // Los tarros: pepinos verdes, tomates rojos, mermelada; con sus tapas de lata y uno con papel atado.
        val dentro = listOf(lerp(C.VERDE, C.LATON, 0.3f), C.ROJO, lerp(C.LADRILLO, C.NARANJA, 0.3f), lerp(C.VERDE, C.LATON, 0.45f))
        var xt = x + 22f; var j = 0
        while (xt < x1 - 6f) {
            val t = 4f + (j % 2) * 2f; val alto = if (j % 3 == 0) 15f else 11f
            tarro(lz, p(xt, y, t), alto * K.s(t), dentro[j % dentro.size], gasa = j == 2, tapa = if (j == 2) null else lerp(C.CROMO, C.LATON, 0.3f))
            xt += alto * 0.75f + 1.6f; j++
        }
    }

    // ---------------------------------------------------------------- La puerta del cuarto y la tele

    /**
     * La puerta del cuarto, en la pared de la derecha: el marco de madera y, por el hueco, el cuarto en penumbra con la
     * esquina del televisor (un mueble de madera, con su tapete de ganchillo y un jarrón encima). Encendido, la pantalla
     * echa su luz azul (el parpadeo va en la parte viva).
     */
    fun DrawScope.puertaDelCuarto(lz: LuzUrss, encendida: Boolean, conTele: Boolean) {
        val x = K.x1; val t0 = CocinaUrss.PT0; val t1 = CocinaUrss.PT1; val y0 = CocinaUrss.PY; val suelo = CocinaUrss.SUELO
        val hueco = poligono(p(x, y0, t0), p(x, y0, t1), p(x, suelo, t1), p(x, suelo, t0))
        // El cuarto: la pared del fondo con su papel pintado, el suelo de parqué, en penumbra (azul con la tele).
        val dentro = if (encendida) lerp(C.HIERRO, C.TELE, 0.32f) else lerp(lz.sombra(p(x, 120f, 30f), lerp(C.PARED, C.LATON, 0.25f), 0.7f), lz.l.tono(lerp(C.MADERA, C.PARED, 0.4f)), 0.4f * lz.dia)
        drawPath(hueco, dentro)
        clipPath(hueco) {
            for (i in 0..8) { val t = t0 + 4f + i * 5f; drawLine((if (encendida) C.TELE else lz.en(p(x, 100f, t), C.PARED)).copy(alpha = 0.12f), p(x + 30f, y0, t), p(x + 30f, 170f, t), 0.6f) }
            if (conTele) {
                // El mueble del televisor, de madera, con su pantalla redondeada y el tapete con el jarrón encima.
                // Está pegado a la pared de su cuarto, de cara a la cocina: se ve su costado de madera y la esquina de la pantalla.
                val xt = x + 4f; val m0 = p(xt, 120f, t0 + 8f)
                for (pt in listOf(t0 + 4f, t0 + 26f)) drawLine(lz.sombra(m0, C.MADERA, 0.6f), p(xt, 146f, pt), p(xt, 190f, pt + 2f), 1.6f)
                val mueble = poligono(p(xt, 96f, t0 + 2f), p(xt, 96f, t0 + 30f), p(xt, 146f, t0 + 30f), p(xt, 146f, t0 + 2f))
                drawPath(mueble, if (encendida) lerp(C.MADERA, C.TELE, 0.22f) else lz.sombra(m0, C.MADERA, 0.5f))
                drawPath(mueble, lz.en(m0, lerp(C.MADERA, C.PAPEL, 0.3f)).copy(alpha = 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(0.6f))
                val pantalla = poligono(p(xt + 0.4f, 101f, t0 + 6f), p(xt + 0.4f, 101f, t0 + 24f), p(xt + 0.4f, 132f, t0 + 24f), p(xt + 0.4f, 132f, t0 + 6f))
                drawPath(pantalla, if (encendida) lerp(C.TELE, Color.White, 0.35f) else lz.sombra(m0, lerp(C.HIERRO, C.OLEO, 0.3f), 0.5f))
                if (encendida) drawPath(pantalla, lerp(C.TELE, Color.White, 0.65f).copy(alpha = 0.55f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.4f))
                else drawLine(Color.White.copy(alpha = 0.25f), p(xt + 0.5f, 104f, t0 + 8f), p(xt + 0.5f, 114f, t0 + 12f), 1f)
                for (i in 0..2) { val q = p(xt + 0.4f, 136f + i * 3.4f, t0 + 27f); drawCircle(if (encendida) lerp(C.LATON, C.TELE, 0.3f) else lz.en(q, C.LATON), 1.1f, q) }
                // El tapete de ganchillo que cae por delante, y el jarrón con unas ramas secas.
                val tp = poligono(p(xt, 95f, t0), p(xt, 95f, t0 + 32f), p(xt, 102f, t0 + 30f), p(xt, 99f, t0 + 16f), p(xt, 102f, t0 + 2f))
                drawPath(tp, if (encendida) lerp(C.PAPEL, C.TELE, 0.5f) else lz.en(m0, C.PAPEL))
                val j = p(xt, 95f, t0 + 16f)
                drawOval(if (encendida) lerp(C.ROJO, C.TELE, 0.4f) else lz.en(j, C.ROJO), Rect(j.x - 3.4f, j.y - 11f, j.x + 3.4f, j.y))
                for (r in -1..1) drawLine(if (encendida) lerp(C.MADERA, C.TELE, 0.4f) else lz.en(j, C.MADERA), j + Offset(r * 0.8f, -10f), j + Offset(r * 4f, -22f), 0.5f)
            }
        }
        // El marco de madera de la puerta, con su moldura.
        val marco = lerp(C.ESMALTE, C.PARED, 0.35f)
        drawPath(poligono(p(x, y0 - 5f, t0 - 4f), p(x, y0 - 5f, t1 + 4f), p(x, y0, t1 + 4f), p(x, y0, t0 - 4f)), lz.izq(p(x, y0, 30f), marco))
        drawPath(poligono(p(x, y0 - 5f, t0 - 4f), p(x, y0 - 5f, t0), p(x, suelo, t0), p(x, suelo, t0 - 4f)), lz.izq(p(x, 140f, t0), marco))
        drawLine(lz.sombra(p(x, 140f, t0), marco, 0.6f), p(x, y0, t0), p(x, suelo, t0), 0.7f)
        // El canto de la hoja abierta hacia dentro del cuarto.
        drawLine(lz.sombra(p(x, 140f, t0 + 2f), C.MADERA, 0.4f), p(x + 3f, y0 + 2f, t0 + 2f), p(x + 3f, suelo, t0 + 2f), 2.4f)
    }

    // ---------------------------------------------------------------- La bolsa de red, las botas, el abrigo

    /**
     * La bolsa de red con la compra, colgada de su asa: la malla de hilo en rombos, que deja ver lo que lleva (el pan, la
     * botella de leche con su tapa de papel de plata, un paquete atado con cordel y una lata).
     */
    fun DrawScope.bolsaDeRed(lz: LuzUrss, gancho: Offset, k: Float, az: Azar) {
        val w = 12f * k; val h = 26f * k; val c = gancho + Offset(-w * 0.4f, h * 0.5f)
        // El asa.
        drawPath(Path().apply { moveTo(gancho.x - w * 0.5f, gancho.y + 6f * k); cubicTo(gancho.x - w * 0.3f, gancho.y - 2f * k, gancho.x + w * 0.1f, gancho.y - 2f * k, gancho.x + w * 0.1f, gancho.y + 6f * k) }, lz.en(gancho, C.TINTA), style = androidx.compose.ui.graphics.drawscope.Stroke(0.7f * k))
        drawCircle(lz.en(gancho, C.LATON), 1.2f * k, gancho)
        val bolsa = Path().apply { moveTo(gancho.x - w * 0.6f, gancho.y + 6f * k); cubicTo(gancho.x - w * 1.3f, gancho.y + h * 0.4f, gancho.x - w * 1.2f, gancho.y + h, gancho.x - w * 0.3f, gancho.y + h); cubicTo(gancho.x + w * 0.5f, gancho.y + h, gancho.x + w * 0.6f, gancho.y + h * 0.4f, gancho.x + w * 0.2f, gancho.y + 6f * k); close() }
        // Lo de dentro, que se ve por la malla.
        clipPath(bolsa) {
            val pan = Offset(c.x - 3f * k, c.y + 6f * k)
            drawOval(lz.en(pan, lerp(C.LATON, C.LADRILLO, 0.45f)), Rect(pan.x - 7f * k, pan.y - 4f * k, pan.x + 6f * k, pan.y + 5f * k))
            val bot = Offset(c.x + 3f * k, c.y - 2f * k)
            drawRect(lz.en(bot, C.ESMALTE), Offset(bot.x - 2.4f * k, bot.y - 7f * k), androidx.compose.ui.geometry.Size(4.8f * k, 13f * k))
            drawRect(lz.en(bot, lerp(C.CROMO, Color.White, 0.5f)), Offset(bot.x - 1.6f * k, bot.y - 9f * k), androidx.compose.ui.geometry.Size(3.2f * k, 2.4f * k))
            val paq = Offset(c.x - 5f * k, c.y - 4f * k)
            drawRect(lz.en(paq, lerp(C.PAPEL, C.LATON, 0.35f)), Offset(paq.x - 4f * k, paq.y - 3f * k), androidx.compose.ui.geometry.Size(8f * k, 6f * k))
            drawLine(lz.en(paq, C.TINTA), paq + Offset(0f, -3f * k), paq + Offset(0f, 3f * k), 0.4f)
            val lata = Offset(c.x + 2f * k, c.y + 10f * k)
            drawOval(lz.en(lata, C.CROMO), Rect(lata.x - 3f * k, lata.y - 2f * k, lata.x + 3f * k, lata.y + 2f * k))
            // La malla: rombos de hilo, más apretados abajo.
            val hilo = lz.en(c, lerp(C.PAPEL, C.HORMIGON, 0.3f)).copy(alpha = 0.85f)
            var i = -6f
            while (i < 8f) { drawLine(hilo, Offset(c.x + i * 2.4f * k - 10f * k, gancho.y), Offset(c.x + i * 2.4f * k + 10f * k, gancho.y + h + 2f), 0.35f); drawLine(hilo, Offset(c.x + i * 2.4f * k + 10f * k, gancho.y), Offset(c.x + i * 2.4f * k - 10f * k, gancho.y + h + 2f), 0.35f); i += 1f }
        }
        drawPath(bolsa, lz.en(c, lerp(C.PAPEL, C.HORMIGON, 0.3f)).copy(alpha = 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(0.5f))
    }

    /** Las botas de fieltro, grises, una de pie y otra caída contra el radiador, con la nieve derretida en un charquito. */
    fun DrawScope.botas(lz: LuzUrss, pie: Offset, k: Float) {
        val fieltro = lerp(C.HORMIGON, C.TINTA, 0.25f)
        mancha(pie + Offset(4f * k, 1f), 16f * k, 2.4f * k, lz.en(pie, lerp(C.LINOLEO, C.TINTA, 0.4f)), 0.4f)
        drawOval(lerp(lz.en(pie, Color.White), C.LUNA, 0.3f).copy(alpha = 0.3f), Rect(pie.x - 6f * k, pie.y - 1f, pie.x + 14f * k, pie.y + 2f * k))
        for ((i, dx) in listOf(0f, 9f).withIndex()) {
            val b = pie + Offset(dx * k, 0f); val ladea = if (i == 1) 5f * k else 0f
            val bota = Path().apply { moveTo(b.x - 4f * k, b.y); lineTo(b.x - 4.4f * k + ladea, b.y - 30f * k); lineTo(b.x + 3.6f * k + ladea, b.y - 31f * k); lineTo(b.x + 3.4f * k, b.y - 8f * k); cubicTo(b.x + 6f * k, b.y - 6f * k, b.x + 9f * k, b.y - 4f * k, b.x + 9f * k, b.y); close() }
            drawPath(bota, androidx.compose.ui.graphics.Brush.horizontalGradient(0f to lz.izq(b, fieltro), 0.5f to lz.frente(b, fieltro), 1f to lz.sombra(b, fieltro, 0.6f), startX = b.x - 4f * k, endX = b.x + 9f * k))
            // La boca de la caña, el fieltro gastado y la nieve que se derrite en la puntera.
            drawOval(lz.sombra(b, C.TINTA, 0.5f), Rect(b.x - 4.4f * k + ladea, b.y - 32f * k, b.x + 3.6f * k + ladea, b.y - 29.6f * k))
            for (j in 0..4) drawLine(lz.sombra(b, fieltro, 0.3f).copy(alpha = 0.4f), Offset(b.x - 3f * k + ladea * 0.6f, b.y - 26f * k + j * 4f * k), Offset(b.x + 2f * k + ladea * 0.6f, b.y - 25f * k + j * 4f * k), 0.4f)
            drawOval(lerp(lz.en(b, C.NIEVE), C.LUNA, 0.2f).copy(alpha = 0.6f), Rect(b.x + 3f * k, b.y - 4f * k, b.x + 8f * k, b.y - 1.6f * k))
        }
    }

    /**
     * El gancho del abrigo, en la pared de la izquierda: el abrigo de paño con su cuello de piel y el chal de flores
     * echado encima, cuando ella está en casa; si no, el gancho con el chal solo o vacío. Con luto, el chal negro.
     */
    fun DrawScope.gancho(lz: LuzUrss, abrigo: Boolean, chal: Boolean, luto: Boolean) {
        val g = p(K.x0, 64f, 16f); val k = K.s(16f)
        drawCircle(lz.der(g, C.LATON), 1.2f * k, g)
        if (!abrigo && !chal) { drawLine(lz.der(g, C.LATON), g, g + Offset(2f * k, 3f * k), 0.8f * k); return }
        if (abrigo) {
            // El abrigo de paño marrón, colgado, con sus hombros y su bulto, y el cuello de piel de conejo.
            val a = Path().apply { moveTo(g.x - 2f * k, g.y + 2f * k); cubicTo(g.x - 9f * k, g.y + 4f * k, g.x - 12f * k, g.y + 18f * k, g.x - 11f * k, g.y + 70f * k); lineTo(g.x + 9f * k, g.y + 72f * k); cubicTo(g.x + 10f * k, g.y + 30f * k, g.x + 8f * k, g.y + 6f * k, g.x + 2f * k, g.y + 2f * k); close() }
            val pano = lerp(C.MADERA, C.TINTA, 0.3f)
            drawPath(a, androidx.compose.ui.graphics.Brush.horizontalGradient(0f to lz.izq(g, pano), 0.6f to lz.frente(g, pano), 1f to lz.der(g, lerp(pano, C.PAPEL, 0.1f)), startX = g.x - 12f * k, endX = g.x + 10f * k))
            for (i in 0..2) drawLine(lz.sombra(g, pano, 0.5f).copy(alpha = 0.5f), Offset(g.x - 4f * k + i * 4f * k, g.y + 14f * k), Offset(g.x - 5f * k + i * 5f * k, g.y + 70f * k), 0.6f)
            for (i in 0..2) drawCircle(lz.frente(g, C.TINTA), 0.9f * k, Offset(g.x + 4f * k, g.y + 24f * k + i * 12f * k))
            val cuello = Path().apply { moveTo(g.x - 8f * k, g.y + 4f * k); cubicTo(g.x - 6f * k, g.y - 1f * k, g.x + 6f * k, g.y - 1f * k, g.x + 7f * k, g.y + 4f * k); lineTo(g.x + 4f * k, g.y + 14f * k); lineTo(g.x - 5f * k, g.y + 13f * k); close() }
            drawPath(cuello, lz.frente(g, lerp(C.PAPEL, C.MADERA, 0.45f)))
            val az = Azar(Semilla.de("urss", "piel")); repeat(26) { val q = Offset(g.x + az.entre(-7f, 6f) * k, g.y + az.entre(1f, 13f) * k); drawLine(lz.en(q, lerp(C.PAPEL, C.MADERA, 0.3f)).copy(alpha = 0.6f), q, q + Offset(az.entre(-1f, 1f) * k, 1.4f * k), 0.4f) }
        }
        if (chal) {
            // El chal de lana de Pávlovski Posad: negro con rosas rojas y flecos, echado sobre el abrigo.
            val ch = Path().apply { moveTo(g.x - 9f * k, g.y + 6f * k); lineTo(g.x + 9f * k, g.y + 6f * k); lineTo(g.x + 6f * k, g.y + 36f * k); lineTo(g.x - 1f * k, g.y + 44f * k); lineTo(g.x - 10f * k, g.y + 32f * k); close() }
            val fondo = if (luto) C.TINTA else lerp(C.TINTA, C.ROJO, 0.15f)
            drawPath(ch, lz.frente(g, fondo))
            if (!luto) clipPath(ch) {
                for ((dx, dy) in listOf(-4f to 14f, 3f to 22f, -2f to 32f, 5f to 10f)) { val q = Offset(g.x + dx * k, g.y + dy * k); drawCircle(lz.en(q, C.ROJO), 2.4f * k, q); drawCircle(lz.en(q, lerp(C.ROJO, C.NARANJA, 0.4f)), 1f * k, q + Offset(-0.4f * k, -0.4f * k)); drawOval(lz.en(q, C.VERDE), Rect(q.x + 1.6f * k, q.y, q.x + 4f * k, q.y + 1.4f * k)) }
            }
            for (i in 0..8) { val q = Offset(g.x - 10f * k + i * 1.4f * k, g.y + 32f * k + i * 1.5f * k); drawLine(lz.frente(q, fondo), q, q + Offset(0f, 4f * k), 0.4f) }
        }
    }

    // ---------------------------------------------------------------- El visillo y la hoja abierta

    /**
     * El visillo de encaje, colgado de su barra encima de la ventana y recogido a la izquierda con un cordón; mientras
     * ella está en el mar Negro, corrido a medias sobre la hoja de la izquierda.
     */
    fun DrawScope.visillo(lz: LuzUrss, corrido: Boolean) {
        val y0 = CocinaUrss.VY0 - 8f; val x0 = CocinaUrss.VX0 - 12f; val x1 = CocinaUrss.VX1 + 6f; val t = 4f
        // La barra y sus soportes.
        drawLine(lz.frente(p(250f, y0, t), C.LATON), p(x0, y0, t), p(x1, y0, t), 1.4f)
        for (x in listOf(x0 + 2f, x1 - 2f)) drawCircle(lz.en(p(x, y0, t), C.LATON), 1.6f, p(x, y0, t))
        val ancho = if (corrido) 46f else 22f
        val a = p(x0 + 2f, y0, t); val b = p(x0 + 2f + ancho, y0, t); val atado = p(x0 + 6f, 104f, t + 4f); val abajo = p(x0 + 4f, CocinaUrss.VY1 - 2f, t + 6f)
        val tela = Path().apply {
            moveTo(a.x, a.y); lineTo(b.x, b.y)
            if (corrido) { cubicTo(b.x + 4f, b.y + 50f, b.x - 6f, abajo.y - 30f, b.x - 2f, abajo.y); lineTo(abajo.x, abajo.y) }
            else { cubicTo(b.x - 4f, b.y + 40f, atado.x + 10f, atado.y - 20f, atado.x + 3f, atado.y); cubicTo(atado.x + 10f, atado.y + 10f, abajo.x + 12f, abajo.y - 8f, abajo.x + 8f, abajo.y); lineTo(abajo.x - 2f, abajo.y) }
            close()
        }
        val blanco = lerp(C.PAPEL, Color.White, 0.35f)
        // El visillo deja pasar la luz: más claro a contraluz de día; de noche, tenue.
        drawPath(tela, lz.en(p(x0 + 10f, 80f, t), blanco).copy(alpha = if (corrido) 0.42f else 0.62f))
        clipPath(tela) {
            // Los pliegues verticales, más tupidos donde se recoge; y el encaje: una malla fina con ramitos y la cenefa de abajo.
            val pliegues = if (corrido) 14 else 8
            for (i in 0..pliegues) { val x = x0 + i * ancho / pliegues; drawLine(lz.en(p(x, 80f, t), blanco).copy(alpha = 0.35f), p(x, y0, t), p(x + 1.4f, CocinaUrss.VY1, t + 6f), 0.9f); drawLine(lz.sombra(p(x, 80f, t), blanco, 0.4f).copy(alpha = 0.22f), p(x + 1.6f, y0, t), p(x + 3f, CocinaUrss.VY1, t + 6f), 1.1f) }
            var y = y0 + 8f; var fila = 0
            while (y < CocinaUrss.VY1 - 4f) {
                var x = x0 - 2f + (fila % 2) * 7f
                while (x < x0 + ancho + 6f) { val q = p(x, y, t); for (j in 0..4) { val a = j * 1.2566f; drawCircle(lz.en(q, blanco).copy(alpha = 0.5f), 0.45f, q + Offset(cos(a) * 1.3f, sin(a) * 1.3f)) }; x += 14f }
                y += 13f; fila++
            }
            for (j in 0..2) { val yy = CocinaUrss.VY1 - 10f + j * 2.4f; drawLine(lz.en(p(x0, yy, t), blanco).copy(alpha = 0.45f), p(x0 - 4f, yy, t + 4f), p(x0 + ancho + 6f, yy, t + 4f), 0.5f) }
            // El borde de abajo, de festón.
            drawLine(lz.en(abajo, blanco).copy(alpha = 0.6f), abajo + Offset(-4f, -2f), abajo + Offset(22f, -2f), 1f)
        }
        if (!corrido) { drawLine(lz.en(atado, C.ROJO), atado + Offset(-3f, -1f), atado + Offset(6f, 1f), 1f); drawCircle(lz.en(atado, C.ROJO), 1.2f, atado + Offset(6f, 1.4f)) }
    }

    /**
     * La hoja de la izquierda de la ventana, abierta hacia dentro en los días de calor: su marco de madera pintada con su
     * travesaño, y el cristal que devuelve un poco el cielo.
     */
    fun DrawScope.hojaAbierta(lz: LuzUrss, nueva: Boolean) {
        // Abierta casi del todo, pegada al derrame de la izquierda: así no tapa el patio.
        val ang = 1.38f; val largo = CocinaUrss.MONTANTE - 2f - (CocinaUrss.VX0 + 4f)
        val xb = CocinaUrss.VX0 + 4f; val xf = xb + largo * kotlin.math.cos(ang); val tf = largo * kotlin.math.sin(ang)
        fun q(u: Float, y: Float) = p(xb + (xf - xb) * u, y, tf * u)
        val y0 = CocinaUrss.VY0 + 4f; val y1 = CocinaUrss.VY1 - 4f; val tr = CocinaUrss.TRAVESANO
        val pintura = if (nueva) lerp(C.ESMALTE, C.PAPEL, 0.2f) else lerp(C.PAPEL, C.LATON, 0.22f)
        val cristal = poligono(q(0f, y0), q(1f, y0), q(1f, y1), q(0f, y1))
        drawPath(cristal, lerp(lz.en(q(0.5f, 80f), lerp(C.CROMO, Color.White, 0.3f)), lz.l.medio, 0.3f).copy(alpha = 0.28f))
        drawLine(Color.White.copy(alpha = 0.18f), q(0.3f, y1), q(0.7f, y0), 6f)
        val c = lz.der(q(0.5f, 80f), pintura)
        for ((u0, u1) in listOf(0f to 0.06f, 0.94f to 1f)) drawPath(poligono(q(u0, y0), q(u1, y0), q(u1, y1), q(u0, y1)), c)
        drawPath(poligono(q(0f, y0), q(1f, y0), q(1f, y0 + 4f), q(0f, y0 + 4f)), c)
        drawPath(poligono(q(0f, y1 - 4f), q(1f, y1 - 4f), q(1f, y1), q(0f, y1)), c)
        drawPath(poligono(q(0f, tr - 2f), q(1f, tr - 2f), q(1f, tr + 2f), q(0f, tr + 2f)), c)
        // El canto de la hoja, que mira a quien mira.
        drawLine(lz.frente(q(1f, 80f), pintura), q(1f, y0), q(1f, y1), 2.2f)
        val fa = q(0.94f, 100f); drawLine(lz.en(fa, C.LATON), fa, fa + Offset(3f, 1f), 1.4f)
    }

    // ---------------------------------------------------------------- Lo de los días señalados

    /** La mesa de fiesta: las mandarinas en un plato, la botella de champán soviético y, en año nuevo, una rama de abeto. */
    fun DrawScope.mesaDeFiesta(lz: LuzUrss, pie: Offset, k: Float, anioNuevo: Boolean) {
        drawOval(lz.en(pie, C.ESMALTE), Rect(pie.x - 12f * k, pie.y - 3f * k, pie.x + 12f * k, pie.y + 2f * k))
        drawOval(lz.en(pie, C.AZUL).copy(alpha = 0.6f), Rect(pie.x - 10f * k, pie.y - 2.4f * k, pie.x + 10f * k, pie.y + 1.4f * k), style = androidx.compose.ui.graphics.drawscope.Stroke(0.5f))
        for ((dx, dy) in listOf(-5f to -2f, 1f to -2.6f, 6f to -1.6f, -2f to -5.6f, 3.6f to -6f)) {
            val c = pie + Offset(dx * k, dy * k)
            drawCircle(androidx.compose.ui.graphics.Brush.radialGradient(listOf(lz.en(c, lerp(C.NARANJA, C.LATON, 0.3f)), lz.sombra(c, C.NARANJA, 0.4f)), c + Offset(-1f * k, -1f * k), 4f * k), 3f * k, c)
            drawCircle(lz.en(c, C.VERDE), 0.6f * k, c + Offset(0.4f * k, -2.6f * k))
        }
        // La botella, de cristal verde oscuro con su papel de plata en el cuello.
        val b = pie + Offset(-14f * k, -1f * k)
        val bot = Path().apply { moveTo(b.x - 3f * k, b.y); lineTo(b.x - 3f * k, b.y - 14f * k); cubicTo(b.x - 3f * k, b.y - 18f * k, b.x - 1.2f * k, b.y - 19f * k, b.x - 1.2f * k, b.y - 24f * k); lineTo(b.x + 1.2f * k, b.y - 24f * k); cubicTo(b.x + 1.2f * k, b.y - 19f * k, b.x + 3f * k, b.y - 18f * k, b.x + 3f * k, b.y - 14f * k); lineTo(b.x + 3f * k, b.y); close() }
        drawPath(bot, lz.en(b, lerp(C.VERDE, C.TINTA, 0.5f)))
        drawRect(lz.en(b, lerp(C.CROMO, C.LATON, 0.5f)), Offset(b.x - 1.4f * k, b.y - 25f * k), androidx.compose.ui.geometry.Size(2.8f * k, 6f * k))
        drawRect(lz.en(b, C.PAPEL), Offset(b.x - 3f * k, b.y - 10f * k), androidx.compose.ui.geometry.Size(6f * k, 5f * k))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(b.x - 1.8f * k, b.y - 13f * k), Offset(b.x - 1.8f * k, b.y - 2f * k), 0.7f * k)
        if (anioNuevo) for (i in 0..5) { val q = pie + Offset((10f + i * 1.6f) * k, (-2f - i * 1.8f) * k); drawLine(lz.en(q, lerp(C.VERDE, C.TINTA, 0.3f)), q, q + Offset(4f * k, 1.6f * k), 1f * k); drawLine(lz.en(q, lerp(C.VERDE, C.TINTA, 0.3f)), q, q + Offset(-3f * k, 2f * k), 1f * k) }
    }

    /** Un ramo en un vaso: las mimosas amarillas del 8 de marzo, o los claveles rojos de la Victoria. */
    fun DrawScope.ramo(lz: LuzUrss, pie: Offset, k: Float, mimosas: Boolean) {
        drawRect(lz.en(pie, lerp(C.CROMO, C.PAPEL, 0.3f)).copy(alpha = 0.55f), Offset(pie.x - 3f * k, pie.y - 10f * k), androidx.compose.ui.geometry.Size(6f * k, 10f * k))
        val az = Azar(Semilla.de("urss", "ramo", if (mimosas) 1 else 2))
        repeat(if (mimosas) 7 else 5) {
            val tip = pie + Offset(az.entre(-8f, 8f) * k, -az.entre(18f, 26f) * k)
            drawLine(lz.en(tip, C.VERDE), pie + Offset(0f, -9f * k), tip, 0.6f * k)
            if (mimosas) repeat(9) { val q = tip + Offset(az.entre(-3f, 3f) * k, az.entre(-2f, 4f) * k); drawCircle(lz.en(q, lerp(C.ORO, C.PAPEL, 0.2f)), 0.9f * k, q) }
            else { drawCircle(lz.en(tip, C.ROJO), 2.2f * k, tip); drawCircle(lz.en(tip, lerp(C.ROJO, C.PAPEL, 0.3f)), 1f * k, tip + Offset(-0.5f * k, -0.6f * k)) }
        }
    }

    /** La Pascua de la suegra: los huevos teñidos con piel de cebolla, color ladrillo, y el pan alto en su lata. */
    fun DrawScope.pascua(lz: LuzUrss, pie: Offset, k: Float) {
        for ((dx, dy) in listOf(-6f to 0f, -2f to -1f, 2f to 0.4f, -4f to -3f)) { val c = pie + Offset(dx * k, dy * k); drawOval(lz.en(c, lerp(C.LADRILLO, C.ROJO, 0.3f)), Rect(c.x - 2f * k, c.y - 2.8f * k, c.x + 2f * k, c.y)); drawCircle(Color.White.copy(alpha = 0.35f), 0.6f * k, c + Offset(-0.6f * k, -2f * k)) }
        val p0 = pie + Offset(9f * k, 0f)
        drawRect(lz.en(p0, lerp(C.LATON, C.LADRILLO, 0.4f)), Offset(p0.x - 4f * k, p0.y - 12f * k), androidx.compose.ui.geometry.Size(8f * k, 12f * k))
        drawOval(lz.en(p0, C.PAPEL), Rect(p0.x - 4.6f * k, p0.y - 15f * k, p0.x + 4.6f * k, p0.y - 10f * k))
    }

    /** La caja de melocotones que trae del mar Negro. */
    fun DrawScope.melocotones(lz: LuzUrss, pie: Offset, k: Float) {
        drawRect(lz.en(pie, lerp(C.MADERA, C.PAPEL, 0.4f)), Offset(pie.x - 12f * k, pie.y - 6f * k), androidx.compose.ui.geometry.Size(24f * k, 6f * k))
        for (i in 0..5) { val c = pie + Offset((-10f + i * 4f) * k, -7f * k); drawCircle(androidx.compose.ui.graphics.Brush.radialGradient(listOf(lz.en(c, lerp(C.NARANJA, C.LATON, 0.4f)), lz.en(c, lerp(C.ROJO, C.NARANJA, 0.4f))), c, 3f * k), 2.4f * k, c) }
    }

    /** La vela del luto, en un platillo, encendida. */
    fun DrawScope.vela(lz: LuzUrss, pie: Offset, k: Float) {
        drawOval(lz.en(pie, C.ESMALTE), Rect(pie.x - 4f * k, pie.y - 1.4f * k, pie.x + 4f * k, pie.y + 1f * k))
        drawRect(lz.en(pie, C.PAPEL), Offset(pie.x - 1f * k, pie.y - 14f * k), androidx.compose.ui.geometry.Size(2f * k, 14f * k))
        val ll = pie + Offset(0f, -15f * k)
        drawCircle(C.LAMPARA.copy(alpha = 0.3f), 4f * k, ll); drawOval(lerp(C.LAMPARA, Color.White, 0.6f), Rect(ll.x - 0.8f * k, ll.y - 3f * k, ll.x + 0.8f * k, ll.y + 0.6f * k))
    }

    // ---------------------------------------------------------------- Las cajas de la mudanza

    /** El cordel que ata una caja, en cruz. */
    fun DrawScope.cuerda(lz: LuzUrss, x0: Float, x1: Float, y: Float, t0: Float, t1: Float) {
        val c = lz.en(p((x0 + x1) / 2f, y, t1), lerp(C.PAPEL, C.LATON, 0.4f))
        drawLine(c, p((x0 + x1) / 2f, y, t0), p((x0 + x1) / 2f, y, t1), 0.6f)
        drawLine(c, p(x0, y, (t0 + t1) / 2f), p(x1, y, (t0 + t1) / 2f), 0.6f)
        drawLine(c, p((x0 + x1) / 2f, y, t1), p((x0 + x1) / 2f, y + 22f, t1), 0.6f)
    }

    /** Las cajas de cartón de la mudanza, atadas con cordel, apiladas en el suelo junto al fogón; [n], cuántas. */
    fun DrawScope.cajasDeLaMudanza(lz: LuzUrss, n: Int, az: Azar) {
        val carton = lerp(C.MADERA, C.PAPEL, 0.4f); val suelo = CocinaUrss.SUELO
        val sitios = listOf(Triple(100f, 44f, 0f), Triple(132f, 52f, 0f), Triple(104f, 46f, 1f), Triple(118f, 64f, 0f), Triple(140f, 40f, 1f), Triple(96f, 62f, 0f))
        for ((i, s) in sitios.take(n.coerceAtMost(6)).withIndex()) {
            val (x, t, piso) = s; val w = 22f + (i % 3) * 3f; val h = 18f + (i % 2) * 4f
            val y1 = suelo - piso * 20f; val y0 = y1 - h
            caja(lz, x, x + w, y0, y1, t - 10f, t + 8f, carton, lerp(carton, C.PAPEL, 0.25f))
            cuerda(lz, x, x + w, y0, t - 10f, t + 8f)
            // Las solapas mal cerradas y un letrero sin letras (una raya de tiza).
            val q = p(x + 4f, y0 + 6f, t + 8f); drawLine(lz.en(q, C.PAPEL).copy(alpha = 0.8f), q, q + Offset(10f * K.s(t), 0f), 1f)
        }
    }

    /**
     * El espejito redondo de la pared, con su marco de madera, que devuelve un trozo de la ventana; el día del luto,
     * tapado con un paño negro, como se hace en las casas rusas.
     */
    fun DrawScope.espejo(lz: LuzUrss, x: Float, y: Float, tapado: Boolean) {
        val c = p(x, y, 0.8f); val r = 8f
        drawCircle(C.SOMBRA.copy(alpha = 0.3f), r + 1.4f, c + Offset(1.4f, 1.8f), blendMode = BlendMode.Multiply)
        drawCircle(lz.en(c, C.MADERA), r + 1.4f, c)
        drawCircle(lz.en(c, lerp(C.MADERA, C.PAPEL, 0.3f)), r + 1.4f, c, style = Stroke(0.5f))
        // Lo que devuelve: la claridad de la ventana (de noche, la lámpara) y la pared.
        drawCircle(Brush.linearGradient(listOf(lerp(lz.l.medio, Color.White, 0.4f * lz.dia), lz.en(c, C.CROMO), lz.en(c, lerp(C.PARED, C.SOMBRA, 0.3f))), c + Offset(r, -r), c + Offset(-r, r)), r, c)
        if (lz.lampara && lz.noche > 0.3f) mancha(c + Offset(3f, -2f), 3f, 2.4f, C.LAMPARA, 0.6f * lz.noche)
        drawLine(Color.White.copy(alpha = 0.4f), c + Offset(-r * 0.5f, r * 0.2f), c + Offset(-r * 0.1f, -r * 0.5f), 0.8f)
        drawLine(lz.en(c, C.TINTA), c + Offset(0f, -r - 1.4f), c + Offset(0f, -r - 6f), 0.4f); drawCircle(lz.en(c, C.LATON), 0.8f, c + Offset(0f, -r - 6f))
        if (tapado) {
            val pano = Path().apply { moveTo(c.x - r - 3f, c.y - r - 2f); cubicTo(c.x - r * 0.3f, c.y - r - 4f, c.x + r * 0.4f, c.y - r - 4f, c.x + r + 3f, c.y - r - 2f); lineTo(c.x + r + 4f, c.y + r + 5f); cubicTo(c.x + r * 0.3f, c.y + r + 2f, c.x - r * 0.3f, c.y + r + 6f, c.x - r - 3.6f, c.y + r + 4f); close() }
            drawPath(pano, lz.frente(c, lerp(C.TINTA, C.HIERRO, 0.2f)))
            for (i in -2..2) drawLine(lz.en(c, lerp(C.TINTA, C.PAPEL, 0.15f)).copy(alpha = 0.5f), c + Offset(i * 3f, -r - 2f), c + Offset(i * 3.4f, r + 4f), 0.5f)
        }
    }

    /** La rejilla de la ventilación, arriba en la pared, con el hollín que ha ido dejando alrededor. */
    fun DrawScope.rejilla(lz: LuzUrss, x: Float, y: Float) {
        val a = p(x, y, 0.4f); val b = p(x + 16f, y + 10f, 0.4f)
        mancha(Offset((a.x + b.x) / 2f, a.y - 2f), 14f, 8f, lz.en(a, lerp(C.TINTA, C.LATON, 0.2f)), 0.16f)
        drawRect(lz.en(a, lerp(C.ESMALTE, C.PARED, 0.4f)), a, Size(b.x - a.x, b.y - a.y))
        for (i in 1..5) { val yy = a.y + i * (b.y - a.y) / 6f; drawLine(lz.sombra(a, C.TINTA, 0.5f).copy(alpha = 0.7f), Offset(a.x + 1.2f, yy), Offset(b.x - 1.2f, yy), 0.8f) }
        drawRect(lz.sombra(a, C.PARED, 0.4f), a, Size(b.x - a.x, b.y - a.y), style = Stroke(0.5f))
    }

    /**
     * El escurridor de madera colgado en la pared, con los platos de pie, dos tazas colgadas de sus clavijas y, de día,
     * el vaso del té boca abajo.
     */
    fun DrawScope.escurridor(lz: LuzUrss, x0: Float, y0: Float, conVaso: Boolean) {
        val x1 = x0 + 34f; val y1 = y0 + 22f
        caja(lz, x0, x1, y1 - 2f, y1, 0f, 7f, C.MADERA, lerp(C.MADERA, C.PAPEL, 0.2f))
        drawRect(C.SOMBRA.copy(alpha = 0.25f), p(x0, y1, 0f), Size(p(x1, y1, 0f).x - p(x0, y1, 0f).x, 4f), blendMode = BlendMode.Multiply)
        // Los platos, de pie y un poco ladeados, con su filete azul.
        for (i in 0..5) {
            val c = p(x0 + 5f + i * 3.4f, y1 - 9f, 4f); val r = 8.4f
            drawOval(lz.en(c, C.ESMALTE), Rect(c.x - 1.6f + i * 0.1f, c.y - r, c.x + 1.6f + i * 0.1f, c.y + r))
            drawOval(lz.sombra(c, C.AZUL, 0.3f).copy(alpha = 0.6f), Rect(c.x - 1.4f, c.y - r + 0.8f, c.x + 1.4f, c.y + r - 0.8f), style = Stroke(0.4f))
        }
        // Los palos del escurridor, delante de los platos.
        for (i in 0..6) { val q = p(x0 + 3f + i * 3.4f, y1 - 2f, 6.5f); drawLine(lz.frente(q, lerp(C.MADERA, C.PAPEL, 0.25f)), q, q + Offset(0f, -8f), 0.7f) }
        drawLine(lz.frente(p(x0, y1 - 8f, 6.5f), C.MADERA), p(x0, y1 - 9f, 6.5f), p(x0 + 24f, y1 - 9f, 6.5f), 0.9f)
        // Las tazas colgadas y el vaso boca abajo.
        for ((i, col) in listOf(C.ROJO, C.AZUL).withIndex()) {
            val g = p(x0 + 27f + i * 4f, y0 + 6f, 3f); drawLine(lz.en(g, C.MADERA), g, g + Offset(0f, 2f), 0.6f)
            val t = Path().apply { moveTo(g.x - 2f, g.y + 2f); lineTo(g.x + 2f, g.y + 2f); lineTo(g.x + 1.6f, g.y + 6.4f); lineTo(g.x - 1.6f, g.y + 6.4f); close() }
            cuerpo(lz.a, t, g.x - 2f, g.x + 2f, g.y + 4f, lerp(C.ESMALTE, col, 0.25f)); drawCircle(lz.en(g, col), 0.6f, g + Offset(0f, 4.4f))
        }
        if (conVaso) { val v = p(x1 - 3f, y1 - 2f, 4f); drawRect(lz.en(v, lerp(C.CROMO, Color.White, 0.3f)).copy(alpha = 0.6f), Offset(v.x - 2f, v.y - 7f), Size(4f, 7f)); drawLine(Color.White.copy(alpha = 0.6f), Offset(v.x - 1f, v.y - 6f), Offset(v.x - 1f, v.y - 1f), 0.5f) }
    }

    /** La barra de los cacharros sobre los azulejos: el cucharón, la espumadera, el rallador y la sartén colgados de sus ganchos. */
    fun DrawScope.cacharros(lz: LuzUrss, x0: Float, y: Float) {
        val x1 = x0 + 50f
        drawLine(lz.sombra(p(x0, y, 1f), C.CROMO, 0.4f), p(x0, y, 1.6f), p(x1, y, 1.6f), 1f)
        drawLine(lz.en(p(x0, y, 1f), lerp(C.CROMO, Color.White, 0.3f)), p(x0, y - 0.4f, 1.6f), p(x1, y - 0.4f, 1.6f), 0.4f)
        // El cucharón.
        var g = p(x0 + 6f, y, 2f); drawLine(lz.en(g, C.CROMO), g, g + Offset(0f, 16f), 0.9f); drawOval(lz.en(g, lerp(C.CROMO, C.HORMIGON, 0.3f)), Rect(g.x - 3.4f, g.y + 15f, g.x + 3.4f, g.y + 20f))
        // La espumadera, con sus agujeros.
        g = p(x0 + 15f, y, 2f); drawLine(lz.en(g, C.CROMO), g, g + Offset(0f, 15f), 0.8f); drawCircle(lz.en(g, C.CROMO), 3.4f, g + Offset(0f, 18f)); for (i in -1..1) for (j in -1..1) drawCircle(lz.sombra(g, C.TINTA, 0.4f), 0.4f, g + Offset(i * 1.4f, 18f + j * 1.4f))
        // El rallador de cuatro caras.
        g = p(x0 + 25f, y, 2f); drawLine(lz.en(g, C.CROMO), g, g + Offset(0f, 3f), 0.6f); val r = Path().apply { moveTo(g.x - 1.6f, g.y + 3f); lineTo(g.x + 1.6f, g.y + 3f); lineTo(g.x + 3f, g.y + 15f); lineTo(g.x - 3f, g.y + 15f); close() }
        cuerpo(lz.a, r, g.x - 3f, g.x + 3f, g.y + 9f, lerp(C.CROMO, C.HORMIGON, 0.2f)); for (i in 0..4) drawLine(lz.sombra(g, C.TINTA, 0.4f).copy(alpha = 0.6f), Offset(g.x - 1.6f, g.y + 6f + i * 1.8f), Offset(g.x + 1.6f, g.y + 6f + i * 1.8f), 0.3f)
        // La sartén de hierro, colgada del mango.
        g = p(x0 + 40f, y, 2f); drawLine(lz.en(g, C.TINTA), g, g + Offset(0f, 9f), 1.4f); drawCircle(lz.sombra(g, C.TINTA, 0.3f), 8f, g + Offset(0f, 17f)); drawCircle(lz.en(g, lerp(C.TINTA, C.HORMIGON, 0.25f)), 6.6f, g + Offset(0f, 17f))
        drawArc(lz.filo.copy(alpha = 0.4f), 200f, 70f, false, g + Offset(-7.4f, 9.6f), Size(14.8f, 14.8f), style = Stroke(0.6f))
    }

    /** El platillo de leche del gato, en el suelo junto al radiador, con su cerco. */
    fun DrawScope.platilloDelGato(lz: LuzUrss, x: Float, t: Float) {
        val c = p(x, CocinaUrss.SUELO, t); val k = K.s(t)
        drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(c.x - 6f * k, c.y - 1f * k), Size(13f * k, 3.2f * k), blendMode = BlendMode.Multiply)
        drawOval(lz.en(c, C.ESMALTE), Rect(c.x - 6f * k, c.y - 2f * k, c.x + 6f * k, c.y + 1.4f * k))
        drawOval(lz.arriba(c, lerp(C.PAPEL, Color.White, 0.4f)), Rect(c.x - 4.2f * k, c.y - 1.4f * k, c.x + 4.2f * k, c.y + 0.6f * k))
        drawOval(lz.en(c, C.AZUL).copy(alpha = 0.6f), Rect(c.x - 6f * k, c.y - 2f * k, c.x + 6f * k, c.y + 1.4f * k), style = Stroke(0.5f))
    }

    /** El periódico doblado que se dejó en el taburete: papel gris con las columnas (sin letras) y una esquina levantada. */
    fun DrawScope.periodico(lz: LuzUrss, x: Float, y: Float, t: Float) {
        val a = p(x - 11f, y - 0.4f, t - 7f); val b = p(x + 9f, y - 0.4f, t - 9f); val c = p(x + 11f, y - 0.4f, t + 6f); val d = p(x - 9f, y - 0.4f, t + 8f)
        val hoja = poligono(a, b, c, d)
        drawPath(hoja, lz.arriba(Offset((a.x + c.x) / 2f, a.y), lerp(C.PAPEL, C.HORMIGON, 0.3f)))
        clipPath(hoja) {
            for (i in 0..5) { val u = 0.12f + i * 0.15f; val q0 = Offset(a.x + (d.x - a.x) * u, a.y + (d.y - a.y) * u); val q1 = Offset(b.x + (c.x - b.x) * u, b.y + (c.y - b.y) * u); drawLine(lz.sombra(q0, C.TINTA, 0.3f).copy(alpha = 0.3f), q0, Offset(q0.x + (q1.x - q0.x) * 0.45f, q0.y + (q1.y - q0.y) * 0.45f), 0.6f); drawLine(lz.sombra(q0, C.TINTA, 0.3f).copy(alpha = 0.3f), Offset(q0.x + (q1.x - q0.x) * 0.55f, q0.y + (q1.y - q0.y) * 0.55f), q1, 0.6f) }
            drawRect(lz.en(a, C.HORMIGON).copy(alpha = 0.5f), Offset(a.x + 3f, a.y + 1f), Size(6f, 4f))
        }
        drawLine(lz.sombra(a, C.TINTA, 0.3f).copy(alpha = 0.4f), Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f), Offset((c.x + d.x) / 2f, (c.y + d.y) / 2f), 0.5f)
        drawPath(poligono(c, Offset(c.x - 4f, c.y - 2.4f), Offset(c.x - 6f, c.y + 0.4f)), lz.en(c, lerp(C.PAPEL, Color.White, 0.2f)))
    }
    // ---------------------------------------------------------------- El primer plano de abajo

    /** El cubo de zinc con las patatas, con su asa y sus abolladuras, cortado por el borde de abajo. */
    fun DrawScope.cuboDePatatas(lz: LuzUrss, x: Float, t: Float, az: Azar) {
        val pie = p(x, CocinaUrss.SUELO, t); val k = K.s(t); val w = 11f * k; val h = 26f * k
        drawOval(C.SOMBRA.copy(alpha = 0.35f), Offset(pie.x - w * 1.3f, pie.y - 2f * k), Size(w * 2.8f, 5f * k), blendMode = BlendMode.Multiply)
        val cubo = Path().apply { moveTo(pie.x - w * 0.8f, pie.y); lineTo(pie.x - w, pie.y - h); lineTo(pie.x + w, pie.y - h); lineTo(pie.x + w * 0.8f, pie.y); close() }
        cuerpo(lz.a, cubo, pie.x - w, pie.x + w, pie.y - h / 2f, lerp(C.CROMO, C.HORMIGON, 0.45f))
        clipPath(cubo) {
            for (j in 1..2) drawLine(lz.sombra(pie, C.HIERRO, 0.4f).copy(alpha = 0.6f), Offset(pie.x - w, pie.y - h * j / 3f), Offset(pie.x + w, pie.y - h * j / 3f), 0.9f * k)
            mancha(Offset(pie.x + w * 0.3f, pie.y - h * 0.45f), 3f * k, 2f * k, lz.sombra(pie, C.HIERRO, 0.5f), 0.4f)
            drawLine(Color.White.copy(alpha = 0.3f), Offset(pie.x - w * 0.55f, pie.y - h * 0.9f), Offset(pie.x - w * 0.45f, pie.y - h * 0.1f), 1.4f * k)
        }
        // Las patatas, que asoman por encima del borde, con tierra.
        drawOval(lz.sombra(pie, C.TIERRA, 0.5f), Offset(pie.x - w, pie.y - h - 2f * k), Size(w * 2f, 4f * k))
        repeat(7) { val c = Offset(pie.x + az.entre(-w * 0.8f, w * 0.8f), pie.y - h - az.entre(0f, 5f) * k); drawOval(lz.arriba(c, lerp(C.LATON, C.TIERRA, 0.45f)), Offset(c.x - 3.4f * k, c.y - 2.4f * k), Size(6.8f * k, 4.6f * k)); drawCircle(lz.sombra(c, C.TIERRA, 0.5f), 0.5f * k, c + Offset(1f * k, 0f)) }
        drawOval(lz.en(pie, lerp(C.CROMO, Color.White, 0.3f)), Offset(pie.x - w, pie.y - h - 1.4f * k), Size(w * 2f, 2.8f * k), style = Stroke(0.9f * k))
        drawPath(Path().apply { moveTo(pie.x - w, pie.y - h); cubicTo(pie.x - w * 0.8f, pie.y - h - 14f * k, pie.x + w * 0.8f, pie.y - h - 14f * k, pie.x + w, pie.y - h) }, lz.en(pie, C.HIERRO), style = Stroke(0.8f * k))
    }

    /** La cesta de mimbre con cebollas y su piel suelta, delante del fogón. */
    fun DrawScope.cestaDeCebollas(lz: LuzUrss, x: Float, t: Float, az: Azar) {
        val pie = p(x, CocinaUrss.SUELO, t); val k = K.s(t); val w = 10f * k; val h = 9f * k
        drawOval(C.SOMBRA.copy(alpha = 0.32f), Offset(pie.x - w * 1.3f, pie.y - 2f * k), Size(w * 2.7f, 4.4f * k), blendMode = BlendMode.Multiply)
        repeat(6) { val c = Offset(pie.x + az.entre(-w * 0.8f, w * 0.8f), pie.y - h - az.entre(0f, 4f) * k); drawCircle(lz.arriba(c, lerp(C.LATON, C.NARANJA, 0.35f)), 3.2f * k, c); drawLine(lz.arriba(c, lerp(C.LATON, C.TIERRA, 0.3f)), c + Offset(0f, -3f * k), c + Offset(0.6f * k, -5f * k), 0.5f * k); drawLine(lz.sombra(c, C.LADRILLO, 0.4f).copy(alpha = 0.5f), c + Offset(-1.4f * k, -2f * k), c + Offset(-1.6f * k, 2f * k), 0.4f) }
        val cesta = Path().apply { moveTo(pie.x - w, pie.y - h); lineTo(pie.x + w, pie.y - h); lineTo(pie.x + w * 0.85f, pie.y); lineTo(pie.x - w * 0.85f, pie.y); close() }
        cuerpo(lz.a, cesta, pie.x - w, pie.x + w, pie.y - h / 2f, lerp(C.LATON, C.MADERA, 0.35f))
        clipPath(cesta) { for (j in 0..4) drawLine(lz.sombra(pie, C.MADERA, 0.4f).copy(alpha = 0.6f), Offset(pie.x - w, pie.y - h + j * 2f * k), Offset(pie.x + w, pie.y - h + j * 2f * k), 0.5f); var xx = pie.x - w; while (xx < pie.x + w) { drawLine(lz.sombra(pie, C.MADERA, 0.4f).copy(alpha = 0.35f), Offset(xx, pie.y - h), Offset(xx + 1f, pie.y), 0.4f); xx += 2.2f * k } }
        drawLine(lz.arriba(pie, lerp(C.LATON, C.PAPEL, 0.3f)), Offset(pie.x - w, pie.y - h), Offset(pie.x + w, pie.y - h), 1.2f * k)
        repeat(3) { val c = pie + Offset(az.entre(w, w * 2.4f), az.entre(-1f, 2f) * k); drawOval(lz.arriba(c, lerp(C.LATON, C.PAPEL, 0.4f)).copy(alpha = 0.8f), Offset(c.x, c.y), Size(2.4f * k, 1f * k)) }
    }

    /** La escoba de ramas, apoyada en el costado del fogón: lo que cruza en diagonal el primer plano. */
    fun DrawScope.escoba(lz: LuzUrss, pie: Offset, punta: Offset, k: Float) {
        drawLine(lz.sombra(pie, C.MADERA, 0.5f), pie + Offset(1.4f, 0f), punta + Offset(1.4f, 0f), 2.2f * k)
        drawLine(lz.der(pie, lerp(C.MADERA, C.PAPEL, 0.2f)), pie, punta, 1.6f * k, StrokeCap.Round)
        // El haz de ramas, atado con alambre, gastado de un lado.
        val d = (punta - pie) / (punta - pie).getDistance(); val n = Offset(-d.y, d.x)
        val atado = pie + d * (18f * k)
        val haz = Path().apply { moveTo(atado.x + n.x * 3f * k, atado.y + n.y * 3f * k); lineTo(pie.x + n.x * 7f * k - d.x * 3f * k, pie.y + 1f); lineTo(pie.x - n.x * 5f * k - d.x * 2f * k, pie.y + 1f); lineTo(atado.x - n.x * 3f * k, atado.y - n.y * 3f * k); close() }
        drawPath(haz, lz.der(pie, lerp(C.LATON, C.MADERA, 0.45f)))
        clipPath(haz) { for (i in -5..5) drawLine(lz.sombra(pie, C.MADERA, 0.5f).copy(alpha = 0.6f), atado + n * (i * 0.5f * k), pie + n * (i * 1.2f * k) + Offset(0f, 1f), 0.4f) }
        drawLine(lz.en(atado, C.CROMO), atado + n * (3.2f * k), atado - n * (3.2f * k), 0.8f * k)
    }

    /** El paño de cuadros echado sobre el taburete, que cuelga por delante. */
    fun DrawScope.panoEnElTaburete(lz: LuzUrss, x: Float, t: Float, ancho: Float, alto: Float) {
        val y = CocinaUrss.SUELO - alto; val w = ancho / 2f
        val a = p(x - w + 4f, y - 0.6f, t - 2f); val b = p(x + w - 2f, y - 0.6f, t + w - 1f); val c = p(x + w * 0.3f, y - 0.6f, t + w + 1f)
        val k = K.s(t + w)
        val pano = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(b.x + 1f, b.y + 10f * k); cubicTo(b.x - 4f * k, b.y + 16f * k, c.x + 2f * k, c.y + 14f * k, c.x - 2f * k, c.y + 17f * k); lineTo(c.x - 6f * k, c.y); close() }
        drawPath(pano, lz.frente(c, lerp(C.AZUL, C.PAPEL, 0.55f)))
        clipPath(pano) { for (i in 0..8) drawLine(lz.frente(c, C.AZUL).copy(alpha = 0.5f), Offset(a.x + i * 3f * k, a.y - 2f), Offset(a.x + i * 3f * k + 2f, c.y + 18f * k), 0.7f); for (j in 0..5) drawLine(lz.frente(c, C.AZUL).copy(alpha = 0.4f), Offset(a.x - 4f, a.y + j * 3f * k), Offset(b.x + 4f, a.y + j * 3f * k), 0.6f) }
        drawLine(lz.arriba(a, lerp(C.PAPEL, Color.White, 0.3f)).copy(alpha = 0.6f), a, b, 0.6f)
    }

    /** Los chanclos de goma negra, junto a la puerta de la entrada, cortados por el borde: con el barro o la nieve de la calle. */
    fun DrawScope.chanclos(lz: LuzUrss, x: Float, t: Float, nieve: Boolean) {
        val k = K.s(t)
        for (i in 0..1) {
            val pie = p(x + i * 9f, CocinaUrss.SUELO, t + i * 3f)
            val forma = Path().apply { moveTo(pie.x - 9f * k, pie.y); cubicTo(pie.x - 10f * k, pie.y - 6f * k, pie.x - 4f * k, pie.y - 8f * k, pie.x + 4f * k, pie.y - 7f * k); cubicTo(pie.x + 9f * k, pie.y - 6f * k, pie.x + 11f * k, pie.y - 2f * k, pie.x + 10f * k, pie.y); close() }
            drawOval(C.SOMBRA.copy(alpha = 0.35f), Offset(pie.x - 10f * k, pie.y - 1.4f * k), Size(21f * k, 3f * k), blendMode = BlendMode.Multiply)
            drawPath(forma, Brush.horizontalGradient(0f to lz.sombra(pie, C.TINTA, 0.3f), 0.7f to lz.en(pie, lerp(C.TINTA, C.HIERRO, 0.4f)), 1f to lz.der(pie, lerp(C.TINTA, C.CROMO, 0.4f)), startX = pie.x - 9f * k, endX = pie.x + 10f * k))
            drawLine(lz.filo.copy(alpha = 0.45f), Offset(pie.x - 6f * k, pie.y - 6.8f * k), Offset(pie.x + 6f * k, pie.y - 6.4f * k), 0.7f)
            drawOval(lz.en(pie, C.ROJO).copy(alpha = 0.7f), Offset(pie.x - 6f * k, pie.y - 6f * k), Size(10f * k, 2.4f * k))
            if (nieve) drawOval(lerp(lz.en(pie, C.NIEVE), C.LUNA, 0.2f).copy(alpha = 0.7f), Offset(pie.x + 2f * k, pie.y - 3f * k), Size(5f * k, 2f * k))
        }
        mancha(p(x + 4f, CocinaUrss.SUELO, t + 4f), 16f * k, 3f * k, lz.en(p(x, CocinaUrss.SUELO, t), lerp(C.LINOLEO, C.TINTA, 0.4f)), 0.4f)
    }
}

/**
 * Una fachada vista en perspectiva por la ventana: sus cuatro esquinas en el cuadro (arriba y abajo de la esquina de
 * cerca y de la de lejos). Un punto de la fachada se da con [s] (de 0, la esquina de cerca, a 1, la de lejos, en
 * distancia de verdad) y [v] (de 0, arriba, a 1, abajo): [q] lo lleva al cuadro con su escorzo.
 */
internal class Fachada(val tl: Offset, val bl: Offset, val tr: Offset, val br: Offset) {
    /** Cuánto más alta se ve la esquina de cerca que la de lejos: el escorzo. */
    val r: Float = ((bl.y - tl.y) / (br.y - tr.y)).coerceAtLeast(0.2f)
    fun u(s: Float): Float = s / (s + r * (1f - s))
    fun q(s: Float, v: Float): Offset { val u = u(s); val a = lerpO(tl, tr, u); val b = lerpO(bl, br, u); return lerpO(a, b, v) }
    fun alto(s: Float): Float = q(s, 1f).y - q(s, 0f).y
    fun forma(): Path = poligono(tl, tr, br, bl)
    fun trozo(s0: Float, v0: Float, s1: Float, v1: Float): Path = poligono(q(s0, v0), q(s1, v0), q(s1, v1), q(s0, v1))
}

private fun lerpO(a: Offset, b: Offset, u: Float) = Offset(a.x + (b.x - a.x) * u, a.y + (b.y - a.y) * u)

/**
 * Las piezas de lo que se ve por la ventana: los bloques de enfrente (el viejo de cinco pisos y el nuevo de nueve), los
 * árboles del patio (grandes y viejos, o palos recién plantados), el columpio, el arenero, el banco, la farola, la pista
 * de hielo, la grúa, la torre de Ostánkino, la fábrica con su cartel del plan, las chimeneas con su vapor y las
 * banderas. Todo sin letras ni caras. Cada una con su lado de luz y de sombra (el [Pincel] del sol de fuera) y, de noche,
 * con lo que alumbra.
 */
internal object PiezasFueraUrss {
    private val C = ColorUrss
    private val R get() = EscenaUrss

    // ---------------------------------------------------------------- Las ventanas de enfrente

    /** Cómo es una ventana de enfrente: sus cortinas, si tiene balcón y qué hay en él. Cada una distinta, con semilla fija. */
    private class Ventana(val cortina: Color, val luz: Color, val visillo: Boolean, val maceta: Boolean, val pantalla: Boolean, val cerrada: Boolean, val ventanillo: Boolean, val ancho: Float)

    private val CORTINAS = listOf(C.PAPEL, lerp(C.NARANJA, C.PAPEL, 0.4f), lerp(C.ROJO, C.PAPEL, 0.35f), lerp(C.VERDE, C.PAPEL, 0.4f), lerp(C.AZUL, C.PAPEL, 0.45f), lerp(C.LATON, C.PAPEL, 0.3f), lerp(C.MADERA, C.PAPEL, 0.3f))
    private val LUCES = listOf(C.VENTANA_LUZ, lerp(C.VENTANA_LUZ, C.NARANJA, 0.35f), lerp(C.VENTANA_LUZ, C.PAPEL, 0.4f), lerp(C.VENTANA_LUZ, C.VERDE, 0.25f), lerp(C.VENTANA_LUZ, C.ROJO, 0.2f), lerp(C.TELE, C.PAPEL, 0.3f))

    private fun ventana(id: Int): Ventana {
        val az = Azar(Semilla.de("urss", "ventana de enfrente", id))
        return Ventana(CORTINAS[az.entero(CORTINAS.size)], LUCES[az.entero(LUCES.size)], az.real() < 0.6f, az.real() < 0.3f, az.real() < 0.35f, az.real() < 0.18f, az.real() < 0.25f, az.entre(0.12f, 0.3f))
    }

    /**
     * Una ventana de enfrente en el trozo [forma] (de esquinas [a] arriba a la izquierda y [b] abajo a la derecha):
     * de día, el cristal oscuro que devuelve el cielo, el marco blanco y las cortinas de cada casa; encendida, su luz
     * del color de su lámpara, con la cortina a contraluz (nunca una persona).
     */
    fun DrawScope.ventanaDeEnfrente(ext: Luces, p: Pincel, a: Offset, b: Offset, id: Int, encendida: Boolean, lejos: Float, marco: Color) {
        val v = ventana(id); val w = b.x - a.x; val h = b.y - a.y
        if (w < 0.6f || h < 0.6f) return
        val oscuro = (ext.noche * 1.2f).coerceIn(0f, 1f)
        val k = if (encendida) (0.25f + 0.75f * oscuro) else 0f
        val cristal = lerp(lerp(ext.tono(lerp(C.HIERRO, C.AZUL, 0.3f), lejos), lerp(ext.alto, ext.medio, 0.5f), 0.28f * (1f - oscuro)), v.luz, k)
        drawRect(cristal, a, Size(w, h))
        if (w > 2.2f) {
            // Las cortinas a los lados (de su color, o encendidas a contraluz) y el visillo.
            val cor = if (encendida && oscuro > 0.3f) lerp(v.luz, v.cortina, 0.35f).let { lerp(it, C.NARANJA, 0.15f) } else p.medio(v.cortina, lejos)
            if (v.cerrada) {
                // Las cortinas corridas: un paño de su color, con la raya donde se juntan.
                drawRect(cor, a, Size(w, h)); drawLine(lerp(cor, C.TINTA, 0.3f), Offset(a.x + w * 0.5f, a.y), Offset(a.x + w * 0.5f, b.y), 0.3f)
            } else { drawRect(cor, a, Size(w * v.ancho, h)); drawRect(cor, Offset(b.x - w * v.ancho * 0.9f, a.y), Size(w * v.ancho * 0.9f, h)) }
            if (v.ventanillo && !encendida) drawRect(p.sombra(C.TINTA, lejos).copy(alpha = 0.6f), Offset(b.x - w * 0.36f, a.y + h * 0.04f), Size(w * 0.3f, h * 0.24f))
            if (v.visillo) drawRect((if (encendida) lerp(v.luz, Color.White, 0.3f) else p.medio(C.PAPEL, lejos)).copy(alpha = 0.45f), Offset(a.x + w * 0.22f, a.y), Size(w * 0.58f, h * 0.55f))
            // La pantalla de una lámpara colgada, encendida, que se adivina dentro.
            if (encendida && v.pantalla && oscuro > 0.3f && h > 3f) drawPath(poligono(Offset(a.x + w * 0.42f, a.y + h * 0.18f), Offset(a.x + w * 0.58f, a.y + h * 0.18f), Offset(a.x + w * 0.66f, a.y + h * 0.36f), Offset(a.x + w * 0.34f, a.y + h * 0.36f)), lerp(v.luz, C.NARANJA, 0.5f))
            // El marco con su cruz, y la maceta del alféizar.
            drawLine(marco, Offset(a.x + w * 0.5f, a.y), Offset(a.x + w * 0.5f, b.y), (w * 0.08f).coerceIn(0.3f, 0.8f))
            drawLine(marco, Offset(a.x, a.y + h * 0.32f), Offset(b.x, a.y + h * 0.32f), (h * 0.06f).coerceIn(0.3f, 0.7f))
            if (v.maceta && h > 3f) { drawRect(p.medio(C.LADRILLO, lejos), Offset(a.x + w * 0.15f, b.y - h * 0.16f), Size(w * 0.18f, h * 0.16f)); drawCircle(p.medio(C.VERDE, lejos), w * 0.12f, Offset(a.x + w * 0.24f, b.y - h * 0.2f)) }
        }
        drawRect(marco, a, Size(w, h), style = Stroke((w * 0.07f).coerceIn(0.3f, 0.7f)))
        // El alféizar de fuera, que hace sombra debajo.
        drawLine(lerp(marco, Color.White, 0.2f), Offset(a.x - 0.4f, b.y + 0.3f), Offset(b.x + 0.4f, b.y + 0.3f), 0.6f)
        if (encendida && oscuro > 0.3f) mancha(Offset((a.x + b.x) / 2f, b.y + h * 0.3f), w * 0.9f, h * 0.5f, v.luz, 0.12f * k)
    }

    // ---------------------------------------------------------------- El bloque viejo

    /**
     * El bloque viejo de enfrente: cinco pisos de estuco, con su cornisa, la faja de abajo almohadillada, los chorretones
     * de la lluvia bajo las ventanas, el estuco caído que enseña el ladrillo, los balcones de hierro (uno acristalado con
     * trastos, otro con ropa tendida en verano o esquís en invierno), el portal con su tejadillo y, en el tejado, las
     * chimeneas y las antenas. Cada ventana es distinta y se enciende sola ([ventanaDeEnfrente]).
     */
    fun DrawScope.bloqueViejo(m: Momento, ext: Luces, p: Pincel, f: Fachada, lejos: Float, nieve: Float) {
        val pisos = 5; val cols = 11; val estuco = C.ESTUCO
        val oscuro = (ext.noche * 1.2f).coerceIn(0f, 1f)
        // El muro, más claro cerca y más brumoso lejos.
        val cara = p.frente(estuco, lejos); val caraLejos = p.frente(estuco, lejos + 0.15f)
        drawPath(f.forma(), Brush.horizontalGradient(0f to cara, 1f to caraLejos, startX = f.tl.x, endX = f.tr.x))
        val marco = p.medio(lerp(C.PAPEL, C.HORMIGON, 0.2f), lejos)
        clipPath(f.forma()) {
            // La faja almohadillada de la planta baja y la imposta entre pisos.
            drawPath(f.trozo(0f, 0.8f, 1f, 1f), p.frente(lerp(estuco, C.LADRILLO, 0.18f), lejos))
            for (j in 0..6) { val s = j / 6f; drawLine(p.sombra(estuco, lejos).copy(alpha = 0.4f), f.q(0f, 0.8f + j * 0.033f), f.q(1f, 0.8f + j * 0.033f), 0.4f) }
            drawLine(p.luz(estuco, lejos), f.q(0f, 0.79f), f.q(1f, 0.79f), 0.8f)
            // Los chorretones de la lluvia bajo la cornisa y bajo las ventanas, y el estuco caído.
            val az = Azar(Semilla.de("urss", "bloque viejo"))
            repeat(18) { val s = az.real(); val v = az.entre(0.06f, 0.7f); drawLine(p.sombra(lerp(estuco, C.TINTA, 0.2f), lejos).copy(alpha = 0.25f), f.q(s, v), f.q(s, v + az.entre(0.06f, 0.2f)), (f.alto(s) * 0.012f).coerceIn(0.4f, 1.4f)) }
            repeat(5) { val s = az.real() * 0.8f; val v = az.entre(0.1f, 0.75f); val c = f.q(s, v); val r = f.alto(s) * 0.03f; drawOval(p.medio(C.LADRILLO, lejos), Rect(c.x - r * 1.6f, c.y - r, c.x + r * 1.6f, c.y + r)) }
        }
        // Las ventanas, piso a piso; el portal en la columna 3 de la planta baja.
        for (piso in 0 until pisos) for (i in 0 until cols) {
            if (piso == 0 && i == 3) continue
            val s0 = (i + 0.28f) / cols; val s1 = (i + 0.72f) / cols
            val v0 = 0.06f + piso * 0.183f + 0.04f; val v1 = v0 + 0.1f
            val a = f.q(s0, v0); val b = f.q(s1, v1)
            val id = piso * 40 + i
            // La sombra del dintel y del hueco: las ventanas viejas son hondas.
            drawRect(p.sombra(estuco, lejos).copy(alpha = 0.5f), Offset(a.x - 0.4f, a.y - 0.6f), Size(b.x - a.x + 0.8f, (b.y - a.y) * 0.16f))
            ventanaDeEnfrente(ext, p, a, b, id, R.ventanaEncendida(m, id), lejos + 0.1f * s0, marco)
            drawLine(p.luz(estuco, lejos), Offset(a.x - 0.6f, a.y - 1f), Offset(b.x + 0.6f, a.y - 1f), 0.7f)
        }
        // El portal: la puerta de madera con su tejadillo de chapa y la bombilla de encima.
        val pa = f.q(3.22f / cols, 0.84f); val pb = f.q(3.78f / cols, 1f)
        drawRect(p.sombra(C.MADERA, lejos), pa, Size(pb.x - pa.x, pb.y - pa.y))
        drawLine(p.medio(C.MADERA, lejos), Offset((pa.x + pb.x) / 2f, pa.y), Offset((pa.x + pb.x) / 2f, pb.y), 0.4f)
        val tj0 = f.q(3.05f / cols, 0.82f); val tj1 = f.q(3.95f / cols, 0.82f)
        drawPath(poligono(tj0, tj1, tj1 + Offset(0.8f, 2.6f), tj0 + Offset(-0.8f, 2.6f)), p.arriba(C.HIERRO, lejos))
        if (nieve > 0.2f) drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), tj0, tj1, 1.2f)
        if (oscuro > 0.25f) { val bb = Offset((tj0.x + tj1.x) / 2f, tj0.y + 1.6f); mancha(bb, 6f, 4f, C.FAROLA, 0.5f * oscuro); drawCircle(lerp(C.FAROLA, Color.White, 0.6f), 0.7f, bb) }
        // Los balcones de hierro: en las columnas 1 y 7, de los pisos de arriba.
        for (piso in 1 until pisos) for ((n, i) in listOf(1, 7).withIndex()) {
            val v1 = 0.06f + piso * 0.183f + 0.15f
            val a = f.q((i + 0.1f) / cols, v1); val b = f.q((i + 0.9f) / cols, v1)
            val alto = f.alto((i + 0.5f) / cols) * 0.07f
            // La losa, con su canto en sombra y la nieve encima.
            drawPath(poligono(a, b, b + Offset(0.4f, 1.2f), a + Offset(-0.4f, 1.2f)), p.sombra(C.HORMIGON, lejos))
            if (nieve > 0.2f) drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), a + Offset(0f, -0.4f), b + Offset(0f, -0.4f), 0.9f)
            val acristalado = n == 1 && piso == 2
            if (acristalado) {
                // El balcón cerrado con cristales y trastos detrás: cajas, frascos, una bici colgada.
                drawPath(poligono(a + Offset(0f, -alto * 2.4f), b + Offset(0f, -alto * 2.4f), b, a), lerp(p.medio(lerp(C.HORMIGON, C.AZUL, 0.2f), lejos), ext.medio, 0.25f))
                drawRect(p.medio(lerp(C.MADERA, C.PAPEL, 0.3f), lejos), Offset(a.x + 1f, a.y - alto * 1.1f), Size((b.x - a.x) * 0.3f, alto * 1.1f))
                drawCircle(p.sombra(C.TINTA, lejos), alto * 0.5f, Offset(a.x + (b.x - a.x) * 0.65f, a.y - alto * 1.4f), style = Stroke(0.4f))
                for (j in 1..3) drawLine(p.medio(C.PAPEL, lejos), a + Offset((b.x - a.x) * j / 4f, -alto * 2.4f), a + Offset((b.x - a.x) * j / 4f, 0f), 0.35f)
            } else {
                // La barandilla de hierro y lo que hay en el balcón según la estación.
                drawLine(p.sombra(C.TINTA, lejos), a + Offset(0f, -alto), b + Offset(0f, -alto), 0.45f)
                var x = a.x; while (x < b.x) { drawLine(p.sombra(C.TINTA, lejos).copy(alpha = 0.7f), Offset(x, a.y + (b.y - a.y) * (x - a.x) / (b.x - a.x) - alto), Offset(x, a.y + (b.y - a.y) * (x - a.x) / (b.x - a.x)), 0.25f); x += 1.2f }
                val invierno = R.calefaccion(m)
                if (n == 0 && piso == 3 && !invierno) for (j in 0..3) { val c = lerpO(a, b, 0.15f + j * 0.22f) + Offset(0f, -alto * 2f); drawRect(p.medio(listOf(C.PAPEL, C.AZUL, C.ROJO, C.PAPEL)[j], lejos), c, Size((b.x - a.x) * 0.12f, alto * 1.3f)) }
                if (n == 0 && piso == 1 && invierno) for (j in 0..1) drawLine(p.medio(if (j == 0) C.ROJO else C.AZUL, lejos), lerpO(a, b, 0.2f + j * 0.1f) + Offset(0f, 0.6f), lerpO(a, b, 0.25f + j * 0.1f) + Offset(0f, -alto * 3.4f), 0.5f)
            }
        }
        // El bajante, que baja de la cornisa al suelo.
        drawLine(p.sombra(C.HIERRO, lejos).copy(alpha = 0.7f), f.q(5.5f / cols, 0.03f), f.q(5.5f / cols, 1f), (f.alto(0.5f) * 0.012f).coerceIn(0.4f, 1f))
        // La cornisa, que vuela y deja su sombra; el tejado con la nieve, las chimeneas y las antenas.
        drawPath(poligono(f.q(0f, 0f) + Offset(0f, -1.8f), f.q(1f, 0f) + Offset(0f, -1.2f), f.q(1f, 0.035f), f.q(0f, 0.035f)), p.arriba(lerp(estuco, C.PAPEL, 0.25f), lejos))
        drawPath(poligono(f.q(0f, 0.035f), f.q(1f, 0.035f), f.q(1f, 0.06f), f.q(0f, 0.06f)), p.sombra(estuco, lejos).copy(alpha = 0.55f))
        val techo = poligono(f.q(0f, 0f) + Offset(0f, -1.8f), f.q(1f, 0f) + Offset(0f, -1.2f), f.q(1f, 0f) + Offset(0f, -3.4f), f.q(0f, 0f) + Offset(0f, -5.4f))
        drawPath(techo, if (nieve > 0.2f) lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.35f * ext.noche) else p.arriba(lerp(C.HIERRO, C.VERDE, 0.2f), lejos))
        for ((s, h) in listOf(0.12f to 0.16f, 0.46f to 0.12f, 0.8f to 0.14f)) {
            val c = f.q(s, 0f); val w = f.alto(s) * 0.05f; val hh = f.alto(s) * h
            drawRect(p.frente(C.LADRILLO, lejos), Offset(c.x - w, c.y - hh - 2f), Size(w * 2f, hh))
            drawRect(p.luz(C.LADRILLO, lejos), Offset(c.x - w, c.y - hh - 2f), Size(w * 0.6f, hh))
            if (nieve > 0.2f) drawRect(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), Offset(c.x - w * 1.1f, c.y - hh - 3f), Size(w * 2.2f, 1.2f))
        }
        for ((s, h) in listOf(0.28f to 0.26f, 0.62f to 0.22f, 0.9f to 0.18f)) {
            val c = f.q(s, 0f) + Offset(0f, -3f); val hh = f.alto(s) * h
            val ant = p.sombra(C.TINTA, lejos).copy(alpha = 0.75f)
            drawLine(ant, c, c + Offset(0f, -hh), 0.4f)
            for (j in 0..2) { val y = c.y - hh * (0.6f + j * 0.15f); val w = hh * (0.3f - j * 0.06f); drawLine(ant, Offset(c.x - w, y), Offset(c.x + w, y + 0.3f), 0.35f) }
        }
    }

    // ---------------------------------------------------------------- El bloque nuevo

    /**
     * El bloque nuevo de enfrente: nueve pisos de paneles blancos «todos iguales», con sus juntas, las logias en columnas
     * (cada una con lo suyo: ropa, esquís, cajas, una acristalada), la caja de la escalera con sus ventanitas y la de la
     * máquina del ascensor en el tejado plano. Cada ventana se enciende sola.
     */
    fun DrawScope.bloqueNuevo(m: Momento, ext: Luces, p: Pincel, f: Fachada, lejos: Float, nieve: Float, pisos: Int, cols: Int, base: Int) {
        val panel = C.PANEL
        drawPath(f.forma(), Brush.horizontalGradient(0f to p.frente(panel, lejos), 1f to p.frente(panel, lejos + 0.18f), startX = f.tl.x, endX = f.tr.x))
        val marco = p.medio(C.PAPEL, lejos)
        clipPath(f.forma()) {
            // Las juntas de los paneles, más oscuras, y el agua que chorrea de ellas.
            for (j in 1 until pisos) drawLine(p.sombra(panel, lejos).copy(alpha = 0.35f), f.q(0f, j / pisos.toFloat()), f.q(1f, j / pisos.toFloat()), 0.35f)
            for (i in 1 until cols) drawLine(p.sombra(panel, lejos).copy(alpha = 0.25f), f.q(i / cols.toFloat(), 0f), f.q(i / cols.toFloat(), 1f), 0.3f)
            val az = Azar(Semilla.de("urss", "bloque nuevo", base))
            repeat(14) { val s = az.real(); drawLine(p.sombra(lerp(panel, C.TINTA, 0.25f), lejos).copy(alpha = 0.18f), f.q(s, az.real() * 0.8f), f.q(s, 1f), 0.5f) }
        }
        for (piso in 0 until pisos) for (i in 0 until cols) {
            val v0 = (piso + 0.22f) / pisos; val v1 = (piso + 0.72f) / pisos
            val escalera = i == cols / 2
            val s0 = (i + (if (escalera) 0.38f else 0.22f)) / cols; val s1 = (i + (if (escalera) 0.62f else 0.78f)) / cols
            val id = base + piso * 40 + i
            val a = f.q(s0, if (escalera) v0 + 0.1f / pisos else v0); val b = f.q(s1, v1)
            if (escalera) { drawRect(if (R.farolas(m)) lerp(C.FAROLA, C.PAPEL, 0.3f).copy(alpha = 0.4f + 0.6f * ext.noche) else p.sombra(C.HIERRO, lejos), a, Size(b.x - a.x, b.y - a.y)); continue }
            // Las logias: en las columnas 1 y cols − 2, hundidas, con su barandilla de panel y lo que guarda cada casa.
            if (i == 1 || i == 4 || i == cols - 2) {
                drawRect(p.sombra(panel, lejos), a, Size(b.x - a.x, b.y - a.y))
                ventanaDeEnfrente(ext, p, Offset(a.x + (b.x - a.x) * 0.15f, a.y), Offset(b.x - (b.x - a.x) * 0.3f, b.y - (b.y - a.y) * 0.1f), id, R.ventanaEncendida(m, id), lejos + 0.05f, marco)
                val pr = Offset(a.x - 0.4f, a.y + (b.y - a.y) * 0.55f)
                // El antepecho de la logia, con las plaquetas de color de cada columna (azul, ladrillo, ocre).
                val color = when (i) { 1 -> lerp(C.AZUL, panel, 0.35f); 4 -> lerp(C.LATON, panel, 0.35f); else -> lerp(C.LADRILLO, panel, 0.35f) }
                drawRect(p.frente(color, lejos), pr, Size(b.x - a.x + 0.8f, (b.y - a.y) * 0.5f))
                drawLine(p.luz(color, lejos), pr, pr + Offset(b.x - a.x + 0.8f, 0f), 0.4f)
                val q = (piso * 7 + i) % 5
                when (q) {
                    0 -> for (j in 0..3) drawLine(p.medio(listOf(C.PAPEL, C.ROJO, C.AZUL, C.PAPEL)[j], lejos), Offset(a.x + (b.x - a.x) * (0.2f + j * 0.2f), a.y + 0.6f), Offset(a.x + (b.x - a.x) * (0.2f + j * 0.2f), a.y + (b.y - a.y) * 0.5f), 0.8f)
                    1 -> drawRect(p.medio(lerp(C.MADERA, C.PAPEL, 0.3f), lejos), Offset(a.x + 0.6f, pr.y - (b.y - a.y) * 0.22f), Size((b.x - a.x) * 0.4f, (b.y - a.y) * 0.22f))
                    2 -> drawRect(lerp(p.medio(C.HORMIGON, lejos), ext.medio, 0.4f).copy(alpha = 0.8f), a, Size(b.x - a.x, (b.y - a.y) * 0.55f))
                    else -> Unit
                }
                if (nieve > 0.2f) drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), pr, pr + Offset(b.x - a.x + 0.8f, 0f), 0.6f)
                continue
            }
            ventanaDeEnfrente(ext, p, a, b, id, R.ventanaEncendida(m, id), lejos + 0.12f * s0, marco)
        }
        // El tejado plano con su pretil y la caseta de la máquina del ascensor.
        drawPath(poligono(f.tl + Offset(0f, -1.6f), f.tr + Offset(0f, -1f), f.tr, f.tl), p.arriba(lerp(panel, C.HORMIGON, 0.4f), lejos))
        val c0 = f.q(0.45f, 0f); val c1 = f.q(0.56f, 0f); val hh = f.alto(0.5f) * 0.06f
        drawRect(p.frente(lerp(panel, C.HORMIGON, 0.3f), lejos), Offset(c0.x, c0.y - hh), Size(c1.x - c0.x, hh))
        if (nieve > 0.2f) { drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), f.tl + Offset(0f, -1.6f), f.tr + Offset(0f, -1f), 1f); drawRect(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), Offset(c0.x, c0.y - hh - 0.8f), Size(c1.x - c0.x, 0.9f)) }
        for (s in listOf(0.2f, 0.75f)) { val c = f.q(s, 0f) + Offset(0f, -1.6f); val h = f.alto(s) * 0.12f; drawLine(p.sombra(C.TINTA, lejos).copy(alpha = 0.6f), c, c + Offset(0f, -h), 0.35f); drawLine(p.sombra(C.TINTA, lejos).copy(alpha = 0.6f), c + Offset(-h * 0.3f, -h * 0.8f), c + Offset(h * 0.3f, -h * 0.8f), 0.3f) }
    }

    /** Una fila de bloques lejanos, en la bruma: cajas blancas con su cara de luz y de sombra y, de noche, sus ventanas encendidas en puntos. */
    fun DrawScope.filaDeBloques(m: Momento, ext: Luces, p: Pincel, x0: Float, x1: Float, base: Float, alto: Float, lejos: Float, az: Azar, nuevos: Boolean) {
        var x = x0
        val oscuro = (ext.noche * 1.2f).coerceIn(0f, 1f)
        while (x < x1) {
            val w = az.entre(10f, 22f); val h = alto * az.entre(0.5f, 1f) * (if (az.real() < 0.12f) 1.6f else 1f); val lado = w * az.entre(0.15f, 0.35f)
            val c = if (nuevos) C.PANEL else lerp(C.ESTUCO, C.HORMIGON, az.real())
            drawRect(p.frente(c, lejos), Offset(x, base - h), Size(w, h))
            drawRect(if (p.solALaDerecha) p.luz(c, lejos) else p.sombra(c, lejos), Offset(x + w, base - h + 0.6f), Size(lado, h - 0.6f))
            // Las filas de ventanas, como trazos; de noche, puntos de luz.
            var y = base - h + 1.6f
            while (y < base - 1.2f) {
                if (oscuro > 0.2f) { var xx = x + 0.8f; while (xx < x + w - 0.6f) { if (az.real() < 0.32f) drawRect(lerp(C.VENTANA_LUZ, C.NARANJA, az.real() * 0.4f).copy(alpha = 0.85f * oscuro), Offset(xx, y), Size(0.7f, 0.6f)); xx += 1.6f } }
                else drawLine(p.sombra(c, lejos + 0.1f).copy(alpha = 0.35f), Offset(x + 0.6f, y), Offset(x + w - 0.6f, y), 0.4f)
                y += 1.8f
            }
            x += w + lado + az.entre(-2f, 4f)
        }
    }

    // ---------------------------------------------------------------- Los árboles del patio

    /**
     * Un árbol viejo del patio (un álamo o un tilo): tronco grueso con su corteza, ramas que se abren y se dividen hasta
     * las ramillas finas, y según la estación: pelado (con la nieve posada en las ramas, del lado de arriba), con sus
     * hojas (una masa oscura con racimos de hojas siguiendo las ramas y las claras solo en el fleco) o amarillo en otoño.
     * Nunca una bola sobre un palo.
     */
    fun DrawScope.arbolViejo(ext: Luces, p: Pincel, pie: Offset, alto: Float, hojas: Float, amarillas: Float, nieve: Float, az: Azar, lejos: Float) {
        val tronco = lerp(lerp(C.TINTA, C.MADERA, 0.3f), C.HORMIGON, 0.22f)
        val ramas = mutableListOf<Triple<Offset, Offset, Float>>()
        fun rama(a: Offset, ang: Float, largo: Float, grueso: Float, nivel: Int) {
            // Cada rama va en dos tramos, con un codo: así no es una vara recta.
            val codo = a + Offset(kotlin.math.cos(ang + az.entre(-0.18f, 0.18f)) * largo * 0.5f, kotlin.math.sin(ang + az.entre(-0.18f, 0.18f)) * largo * 0.5f)
            val b = codo + Offset(kotlin.math.cos(ang) * largo * 0.5f, kotlin.math.sin(ang) * largo * 0.5f)
            ramas += Triple(a, codo, grueso); ramas += Triple(codo, b, grueso * 0.88f)
            if (nivel >= 6 || grueso < 0.22f) return
            val n = if (nivel < 2) 2 else if (az.real() < 0.55f) 2 else 3
            for (i in 0 until n) {
                val abre = az.entre(0.22f, 0.62f) * (if (i % 2 == 0) -1f else 1f)
                // Las ramas de abajo se abren y caen un poco; las de arriba suben (como los álamos y los tilos viejos).
                val cae = if (ang > -1.2f && ang < -0.2f) 0.08f else if (ang < -1.9f) -0.08f else 0f
                rama(if (i == 2) lerpO(a, b, 0.6f) else b, ang + abre * (1f - nivel * 0.06f) + cae + az.entre(-0.12f, 0.12f), largo * az.entre(0.6f, 0.78f), grueso * az.entre(0.55f, 0.68f), nivel + 1)
            }
        }
        // El tronco sube un trecho y se abre en tres ramas madre (como los álamos de los patios, desmochados alguna vez).
        val cruz = pie + Offset(az.entre(-3f, 3f), -alto * 0.38f)
        for (i in -1..1) rama(cruz, (-PI / 2).toFloat() + i * 0.42f + az.entre(-0.1f, 0.1f), alto * 0.22f, alto * 0.026f, 1)
        // En invierno, la maraña de las ramillas finas se ve como un velo oscuro alrededor de la copa.
        if (hojas < 0.3f) for ((_, b, g) in ramas) if (g < alto * 0.006f && az.real() < 0.5f) mancha(b, alto * 0.05f, alto * 0.04f, p.sombra(tronco, lejos + 0.2f), 0.12f * (1f - hojas))
        // Primero las ramas gruesas (las finas van después, solo donde no las tapan las hojas).
        val fina = alto * 0.011f
        for ((a, b, g) in ramas) if (g >= fina || hojas < 0.05f) { drawLine(p.sombra(tronco, lejos), a, b, g, StrokeCap.Round); if (g > 0.8f) drawLine(p.luz(tronco, lejos).copy(alpha = 0.6f), a + Offset(g * 0.25f, 0f), b + Offset(g * 0.2f, 0f), g * 0.35f, StrokeCap.Round) }
        // Las hojas: una masa oscura en cada ramillete, hecha de varios bultos, y encima las hojas sueltas, chicas; las
        // claras, solo arriba y en el borde (del lado del sol). En otoño, amarillas.
        if (hojas > 0.05f) {
            val verde = lerp(lerp(C.VERDE, C.LATON, 0.55f * amarillas), C.ORO, 0.35f * amarillas)
            val masa = p.sombra(lerp(verde, C.TINTA, 0.25f), lejos); val luz = p.luz(lerp(verde, C.PAPEL, 0.1f), lejos); val medio = p.medio(verde, lejos); val honda = p.sombra(lerp(verde, C.TINTA, 0.45f), lejos)
            val puntas = ramas.filter { it.third < fina }
            for ((a, b, _) in puntas) if (az.real() < hojas) repeat(3) { val q = lerpO(a, b, az.entre(0.3f, 1f)) + Offset(az.entre(-1f, 1f), az.entre(-1f, 0.6f)) * (alto * 0.03f); drawCircle(if (it == 0) honda else masa, alto * az.entre(0.018f, 0.032f) * (0.6f + 0.4f * hojas), q) }
            for ((a, b, _) in puntas) if (az.real() < hojas) repeat(9) {
                val q = lerpO(a, b, az.entre(0.2f, 1.1f)) + Offset(az.entre(-1f, 1f), az.entre(-1f, 1f)) * (alto * 0.035f)
                val arriba = q.y < b.y - alto * 0.008f
                val c = if (arriba && az.real() < 0.55f) luz else if (az.real() < 0.3f) honda else medio
                val r = alto * az.entre(0.005f, 0.009f)
                drawOval(c, Offset(q.x - r * 1.3f, q.y - r * 0.8f), Size(r * 2.6f, r * 1.6f))
            }
        }
        // Las ramillas finas: en invierno, todas; con hojas, solo las que asoman.
        if (hojas < 0.6f) for ((a, b, g) in ramas) if (g < fina) drawLine(p.sombra(tronco, lejos).copy(alpha = 1f - hojas), a, b, g, StrokeCap.Round)
        val g0 = alto * 0.034f; val g1 = alto * 0.026f
        val tr = Path().apply { moveTo(pie.x - g0, pie.y); cubicTo(pie.x - g0 * 0.8f, pie.y - alto * 0.2f, cruz.x - g1 * 1.1f, cruz.y + alto * 0.05f, cruz.x - g1, cruz.y); lineTo(cruz.x + g1, cruz.y); cubicTo(cruz.x + g1 * 1.1f, cruz.y + alto * 0.05f, pie.x + g0 * 0.8f, pie.y - alto * 0.2f, pie.x + g0, pie.y); close() }
        drawPath(tr, Brush.horizontalGradient(0f to p.luz(tronco, lejos), 0.4f to p.medio(tronco, lejos), 1f to p.sombra(tronco, lejos), startX = pie.x - g0, endX = pie.x + g0))
        // La corteza: grietas que suben, y el liquen claro del lado del norte.
        clipPath(tr) {
            var x = pie.x - g0; while (x < pie.x + g0) { drawLine(p.sombra(tronco, lejos).copy(alpha = 0.5f), Offset(x, pie.y), Offset(x + (cruz.x - pie.x) * 0.9f + az.entre(-1f, 1f), cruz.y), 0.4f); x += g0 * 0.4f }
            var y = pie.y; while (y > cruz.y) { drawLine(p.luz(tronco, lejos).copy(alpha = 0.25f), Offset(pie.x - g0, y), Offset(pie.x - g0 * 0.2f, y - alto * 0.01f), 0.4f); y -= alto * 0.04f }
        }
        // La nieve posada en las ramas gruesas, del lado de arriba.
        if (nieve > 0.3f && hojas < 0.2f) { val blanco = lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.35f * ext.noche); for ((a, b, g) in ramas) if (g > 0.6f && kotlin.math.abs(b.x - a.x) > kotlin.math.abs(b.y - a.y) * 0.8f) drawLine(blanco, a + Offset(0f, -g * 0.42f), b + Offset(0f, -g * 0.42f), (g * 0.28f).coerceAtMost(1.6f), StrokeCap.Round) }
        // El encalado del pie del tronco (se encalan en primavera).
        drawRect(p.medio(C.PAPEL, lejos), Offset(pie.x - alto * 0.05f, pie.y - alto * 0.05f), Size(alto * 0.1f, alto * 0.05f))
    }

    /** Un árbol recién plantado, que «todavía es un palo»: el tronquito con dos estacas y su atadura, unas ramitas y, en verano, cuatro hojas. */
    fun DrawScope.arbolJoven(ext: Luces, p: Pincel, pie: Offset, alto: Float, hojas: Float, nieve: Float, lejos: Float) {
        val tr = lerp(C.MADERA, C.HORMIGON, 0.3f)
        for (s in listOf(-1f, 1f)) drawLine(p.medio(lerp(C.MADERA, C.PAPEL, 0.3f), lejos), pie + Offset(s * alto * 0.08f, 0f), pie + Offset(s * alto * 0.05f, -alto * 0.55f), 0.5f)
        drawLine(p.medio(C.PAPEL, lejos), pie + Offset(-alto * 0.06f, -alto * 0.45f), pie + Offset(alto * 0.06f, -alto * 0.45f), 0.4f)
        drawLine(p.sombra(tr, lejos), pie, pie + Offset(0f, -alto), 0.7f)
        for (i in 0..3) { val a = pie + Offset(0f, -alto * (0.6f + i * 0.1f)); val b = a + Offset((if (i % 2 == 0) -1f else 1f) * alto * 0.18f, -alto * 0.12f); drawLine(p.sombra(tr, lejos), a, b, 0.35f); if (hojas > 0.3f) { drawCircle(p.medio(C.VERDE, lejos), alto * 0.05f, b); drawCircle(p.luz(C.VERDE, lejos), alto * 0.03f, b + Offset(0.4f, -0.4f)) } }
        drawOval(if (nieve > 0.3f) lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche) else p.medio(C.TIERRA, lejos), Rect(pie.x - alto * 0.1f, pie.y - alto * 0.02f, pie.x + alto * 0.1f, pie.y + alto * 0.03f))
    }

    // ---------------------------------------------------------------- El patio

    /** El columpio de hierro pintado, con su asiento de tabla; en invierno, con nieve en el travesaño. */
    fun DrawScope.columpio(ext: Luces, p: Pincel, pie: Offset, alto: Float, nieve: Float, lejos: Float) {
        val h = p.sombra(lerp(C.AZUL, C.HIERRO, 0.3f), lejos); val w = alto * 0.7f
        for (s in listOf(-1f, 1f)) { drawLine(h, pie + Offset(s * w * 0.5f - alto * 0.18f, 0f), pie + Offset(s * w * 0.5f, -alto), 0.6f); drawLine(h, pie + Offset(s * w * 0.5f + alto * 0.18f, 0f), pie + Offset(s * w * 0.5f, -alto), 0.6f) }
        drawLine(p.luz(lerp(C.AZUL, C.HIERRO, 0.3f), lejos), pie + Offset(-w * 0.5f, -alto), pie + Offset(w * 0.5f, -alto), 0.7f)
        for (s in listOf(-0.15f, 0.15f)) drawLine(p.sombra(C.HIERRO, lejos), pie + Offset(s * w, -alto), pie + Offset(s * w, -alto * 0.3f), 0.3f)
        drawLine(p.medio(C.MADERA, lejos), pie + Offset(-w * 0.18f, -alto * 0.3f), pie + Offset(w * 0.18f, -alto * 0.3f), 1f)
        if (nieve > 0.3f) { drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), pie + Offset(-w * 0.5f, -alto - 0.5f), pie + Offset(w * 0.5f, -alto - 0.5f), 0.6f); drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), pie + Offset(-w * 0.18f, -alto * 0.3f - 0.6f), pie + Offset(w * 0.18f, -alto * 0.3f - 0.6f), 0.6f) }
    }

    /** El arenero de tablas, con su seta de chapa pintada de rojo; en invierno, lleno de nieve. */
    fun DrawScope.arenero(ext: Luces, p: Pincel, c: Offset, ancho: Float, nieve: Float, lejos: Float) {
        val d = ancho * 0.3f
        val caja = poligono(c + Offset(-ancho / 2f, 0f), c + Offset(ancho / 2f, 0f), c + Offset(ancho / 2f + d * 0.3f, d), c + Offset(-ancho / 2f - d * 0.3f, d))
        drawPath(caja, if (nieve > 0.3f) lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche) else p.arriba(lerp(C.LATON, C.PAPEL, 0.3f), lejos))
        drawPath(caja, p.medio(C.MADERA, lejos), style = Stroke(0.9f))
        drawRect(p.frente(C.MADERA, lejos), c + Offset(-ancho / 2f - d * 0.3f, d), Size(ancho + d * 0.6f, 1.2f))
        // La seta: el palo y el sombrero rojo con lunares.
        val s = c + Offset(ancho * 0.3f, d * 0.4f)
        drawLine(p.medio(C.MADERA, lejos), s, s + Offset(0f, -ancho * 0.45f), 0.7f)
        drawPath(Path().apply { moveTo(s.x - ancho * 0.22f, s.y - ancho * 0.42f); cubicTo(s.x - ancho * 0.18f, s.y - ancho * 0.66f, s.x + ancho * 0.18f, s.y - ancho * 0.66f, s.x + ancho * 0.22f, s.y - ancho * 0.42f); close() }, p.luz(C.ROJO, lejos))
        if (nieve > 0.3f) drawOval(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), Rect(s.x - ancho * 0.16f, s.y - ancho * 0.64f, s.x + ancho * 0.16f, s.y - ancho * 0.55f))
        else for (k in 0..2) drawCircle(p.luz(C.PAPEL, lejos), 0.5f, Offset(s.x - ancho * 0.1f + k * ancho * 0.1f, s.y - ancho * 0.5f))
    }

    /** El banco de tablas del patio, con sus patas de hierro (el de las viejas, junto al portal); con nieve, blanco encima. */
    fun DrawScope.banco(ext: Luces, p: Pincel, a: Offset, largo: Float, nieve: Float, lejos: Float, patines: Boolean = false) {
        val h = largo * 0.2f
        for (s in listOf(0.1f, 0.9f)) drawLine(p.sombra(C.HIERRO, lejos), a + Offset(largo * s, 0f), a + Offset(largo * s, -h), 0.6f)
        drawRect(p.arriba(lerp(C.VERDE, C.MADERA, 0.4f), lejos), a + Offset(-1f, -h - 0.8f), Size(largo + 2f, 1.2f))
        drawRect(p.frente(lerp(C.VERDE, C.MADERA, 0.4f), lejos), a + Offset(0f, -h * 1.9f), Size(largo, 1f))
        if (nieve > 0.3f) drawRect(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), a + Offset(-1f, -h - 1.8f), Size(largo + 2f, 1f))
        if (patines) {
            // Unos patines dejados en el banco: la bota blanca y la cuchilla.
            for (j in 0..1) { val q = a + Offset(largo * (0.55f + j * 0.12f), -h - 0.8f); drawRect(p.luz(C.PAPEL, lejos), Offset(q.x, q.y - 2.2f), Size(1.6f, 2.2f)); drawLine(p.luz(C.CROMO, lejos), Offset(q.x - 0.4f, q.y + 0.3f), Offset(q.x + 2.2f, q.y + 0.3f), 0.4f) }
        }
    }

    /** La farola del patio: el poste de hormigón y el brazo con su lámpara; encendida, la luz naranja y su charco en el suelo. */
    fun DrawScope.farola(ext: Luces, p: Pincel, pie: Offset, alto: Float, encendida: Boolean, nieve: Float, lejos: Float, bandera: Boolean) {
        val poste = lerp(C.HORMIGON, C.TINTA, 0.15f)
        drawLine(p.sombra(poste, lejos), pie, pie + Offset(0f, -alto), alto * 0.035f)
        drawLine(p.luz(poste, lejos).copy(alpha = 0.6f), pie + Offset(-alto * 0.008f, 0f), pie + Offset(-alto * 0.008f, -alto), alto * 0.012f)
        val brazo = pie + Offset(alto * 0.14f, -alto * 0.98f)
        drawLine(p.sombra(C.HIERRO, lejos), pie + Offset(0f, -alto * 0.94f), brazo, alto * 0.018f)
        val lamp = brazo + Offset(0f, alto * 0.02f)
        drawOval(if (encendida) lerp(C.FAROLA, Color.White, 0.5f) else p.medio(C.HORMIGON, lejos), Rect(lamp.x - alto * 0.05f, lamp.y - alto * 0.015f, lamp.x + alto * 0.05f, lamp.y + alto * 0.02f))
        if (nieve > 0.3f) drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), pie + Offset(0f, -alto * 0.95f), brazo + Offset(0f, -0.4f), 0.6f)
        if (bandera) {
            // La bandera roja de los días de fiesta, colgada del poste, sin letras ni emblema.
            val b = pie + Offset(-alto * 0.01f, -alto * 0.8f)
            drawLine(p.sombra(C.HIERRO, lejos), b, b + Offset(-alto * 0.12f, -alto * 0.08f), 0.4f)
            drawPath(Path().apply { moveTo(b.x - alto * 0.02f, b.y - alto * 0.01f); cubicTo(b.x - alto * 0.06f, b.y + alto * 0.01f, b.x - alto * 0.1f, b.y - alto * 0.04f, b.x - alto * 0.12f, b.y - alto * 0.08f); lineTo(b.x - alto * 0.12f, b.y + alto * 0.06f); cubicTo(b.x - alto * 0.08f, b.y + alto * 0.1f, b.x - alto * 0.05f, b.y + alto * 0.06f, b.x - alto * 0.02f, b.y + alto * 0.1f); close() }, lerp(p.luz(C.ROJO, lejos), C.ROJO, 0.3f))
        }
    }

    /** El resplandor de una farola encendida: el halo en el aire y el charco naranja en la nieve. */
    fun DrawScope.luzDeFarola(ext: Luces, pie: Offset, alto: Float, nieve: Float, fuerza: Float) {
        val lamp = pie + Offset(alto * 0.14f, -alto * 0.96f)
        mancha(lamp, alto * 0.2f, alto * 0.16f, C.FAROLA, 0.45f * fuerza)
        mancha(pie + Offset(alto * 0.14f, 0f), alto * 0.5f, alto * 0.12f, C.FAROLA, (0.22f + 0.18f * nieve) * fuerza)
        drawCircle(lerp(C.FAROLA, Color.White, 0.7f).copy(alpha = fuerza), alto * 0.02f, lamp)
    }

    /**
     * La pista de hielo que el conserje riega en el patio: el hielo liso, que devuelve el cielo y las farolas, con las
     * rayas de los patines, el muro de nieve alrededor, una portería de hockey y un trineo dejado. Sin niños: lo que dejan.
     */
    fun DrawScope.pista(ext: Luces, p: Pincel, forma: Path, caja: Rect, lejos: Float, az: Azar) {
        val hielo = lerp(lerp(ext.alto, ext.medio, 0.5f), lerp(C.AZUL, C.PAPEL, 0.5f), 0.4f)
        drawPath(forma, Brush.verticalGradient(0f to lerp(hielo, ext.bajo, 0.4f), 1f to ext.tono(hielo, lejos), startY = caja.top, endY = caja.bottom))
        clipPath(forma) {
            // El brillo del cielo en el hielo y las rayas de los patines, en curvas.
            drawRect(Brush.horizontalGradient(0f to Color.Transparent, 0.5f to Color.White.copy(alpha = 0.18f * (1f - ext.noche)), 1f to Color.Transparent, startX = caja.left, endX = caja.right), caja.topLeft, caja.size)
            repeat(16) {
                val c = Offset(az.entre(caja.left, caja.right), az.entre(caja.top, caja.bottom)); val r = az.entre(4f, 12f)
                drawArc(Color.White.copy(alpha = 0.35f), az.entre(0f, 360f), az.entre(60f, 160f), false, Offset(c.x - r, c.y - r * 0.25f), Size(r * 2f, r * 0.5f), style = Stroke(0.3f))
            }
        }
        // El muro de nieve apilada alrededor, con su lado de sombra.
        drawPath(forma, lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), style = Stroke(1.4f))
        // La portería de hockey, de tubo rojo con su red.
        val g = Offset(caja.right - caja.width * 0.12f, caja.top + caja.height * 0.4f)
        drawLine(p.luz(C.ROJO, lejos), g, g + Offset(0f, -2.4f), 0.4f); drawLine(p.luz(C.ROJO, lejos), g + Offset(3.6f, 0.6f), g + Offset(3.6f, -1.8f), 0.4f); drawLine(p.luz(C.ROJO, lejos), g + Offset(0f, -2.4f), g + Offset(3.6f, -1.8f), 0.4f)
        for (k in 1..3) drawLine(p.medio(C.PAPEL, lejos).copy(alpha = 0.6f), g + Offset(k * 0.9f, -2.3f + k * 0.15f), g + Offset(k * 0.9f + 0.6f, 0.3f), 0.2f)
        // El trineo de hierro con tablillas, dejado junto al muro.
        val t = Offset(caja.left + caja.width * 0.12f, caja.bottom - 1f)
        drawLine(p.luz(C.CROMO, lejos), t, t + Offset(5f, 0f), 0.35f); drawRect(p.medio(lerp(C.ROJO, C.MADERA, 0.4f), lejos), t + Offset(0.6f, -1.2f), Size(3.6f, 1f))
    }

    // ---------------------------------------------------------------- Lo lejano

    /** La torre de televisión de Ostánkino: el tronco cónico de hormigón, las plataformas, la aguja y su luz roja en la punta. */
    fun DrawScope.ostankino(ext: Luces, p: Pincel, pie: Offset, alto: Float, lejos: Float, luzRoja: Boolean) {
        val w = alto * 0.06f
        val tronco = poligono(pie + Offset(-w, 0f), pie + Offset(w, 0f), pie + Offset(w * 0.2f, -alto * 0.62f), pie + Offset(-w * 0.2f, -alto * 0.62f))
        drawPath(tronco, Brush.horizontalGradient(0f to p.luz(lerp(C.PANEL, C.HORMIGON, 0.5f), lejos * 0.6f), 1f to p.sombra(lerp(C.PANEL, C.HORMIGON, 0.6f), lejos * 0.6f), startX = pie.x - w, endX = pie.x + w))
        // Las patas de la base, abiertas.
        for (s in listOf(-1f, 1f)) drawLine(p.medio(C.PANEL, lejos), pie + Offset(s * w * 0.6f, -alto * 0.05f), pie + Offset(s * w * 2f, 0f), 0.6f)
        // Las plataformas (el restaurante) y la aguja con sus franjas.
        val pl = pie + Offset(0f, -alto * 0.6f)
        drawRect(p.frente(lerp(C.PANEL, C.HORMIGON, 0.3f), lejos), Offset(pl.x - w * 0.6f, pl.y - alto * 0.04f), Size(w * 1.2f, alto * 0.04f))
        drawLine(p.medio(lerp(C.PANEL, C.HORMIGON, 0.4f), lejos), pl + Offset(0f, -alto * 0.04f), pie + Offset(0f, -alto), w * 0.35f)
        for (k in 0..3) { val y = pl.y - alto * (0.12f + k * 0.07f); drawLine(p.medio(lerp(C.ROJO, C.PANEL, 0.4f), lejos), Offset(pl.x - w * 0.2f, y), Offset(pl.x + w * 0.2f, y), 0.5f) }
        // De noche, las ventanas del restaurante y la luz roja de la punta.
        if (ext.noche > 0.3f) for (k in 0..3) drawRect(C.VENTANA_LUZ.copy(alpha = 0.7f * ext.noche), Offset(pl.x - w * 0.5f + k * w * 0.3f, pl.y - alto * 0.03f), Size(w * 0.15f, alto * 0.015f))
        if (luzRoja) { val t = pie + Offset(0f, -alto); mancha(t, 3f, 3f, C.ROJO, 0.6f); drawCircle(lerp(C.ROJO, Color.White, 0.3f), 0.7f, t) }
    }

    /**
     * La fábrica de tejidos, lejos: las naves de tejado en dientes de sierra, la chimenea de ladrillo con su humo, y en la
     * fachada el cartel del plan: un panel rojo con su raya que sube con lo cumplido ([plan]), sin letras. De noche, un
     * foco lo alumbra.
     */
    fun DrawScope.fabrica(ext: Luces, p: Pincel, base: Offset, ancho: Float, lejos: Float, plan: Float, humo: Boolean, az: Azar) {
        val h = ancho * 0.18f
        // Las naves, con sus dientes de sierra (las caras de cristal, hacia el norte).
        val nave = Rect(base.x, base.y - h, base.x + ancho, base.y)
        drawRect(p.frente(lerp(C.LADRILLO, C.HORMIGON, 0.35f), lejos), nave.topLeft, nave.size)
        val n = 6; val d = ancho / n
        for (i in 0 until n) {
            val x = base.x + i * d
            drawPath(poligono(Offset(x, base.y - h), Offset(x + d * 0.75f, base.y - h - d * 0.5f), Offset(x + d, base.y - h)), p.arriba(lerp(C.HORMIGON, C.PAPEL, 0.2f), lejos))
            drawPath(poligono(Offset(x + d * 0.75f, base.y - h - d * 0.5f), Offset(x + d, base.y - h), Offset(x + d * 0.75f, base.y - h)), lerp(p.sombra(C.AZUL, lejos), ext.medio, 0.3f))
        }
        // Las ventanas largas de la nave; de noche, encendidas (el turno de noche).
        for (k in 0..1) drawRect(if (ext.noche > 0.3f) C.VENTANA_LUZ.copy(alpha = 0.6f * ext.noche) else p.sombra(C.HIERRO, lejos).copy(alpha = 0.6f), Offset(base.x + 2f, base.y - h * (0.7f - k * 0.35f)), Size(ancho - 4f, h * 0.14f))
        // La chimenea de ladrillo, con sus aros.
        val ch = Offset(base.x + ancho * 0.82f, base.y - h); val ah = ancho * 0.7f
        drawPath(poligono(ch + Offset(-ancho * 0.03f, 0f), ch + Offset(ancho * 0.03f, 0f), ch + Offset(ancho * 0.018f, -ah), ch + Offset(-ancho * 0.018f, -ah)), Brush.horizontalGradient(0f to p.luz(C.LADRILLO, lejos), 1f to p.sombra(C.LADRILLO, lejos), startX = ch.x - ancho * 0.03f, endX = ch.x + ancho * 0.03f))
        for (k in 1..3) drawLine(p.sombra(C.TINTA, lejos).copy(alpha = 0.5f), ch + Offset(-ancho * 0.028f + k * 0.0f, -ah * k * 0.25f), ch + Offset(ancho * 0.028f, -ah * k * 0.25f), 0.4f)
        if (ext.noche > 0.3f) drawCircle(C.ROJO.copy(alpha = ext.noche), 0.6f, ch + Offset(0f, -ah))
        if (humo) for (k in 0..7) { val u = k / 7f; mancha(ch + Offset(-ancho * 0.25f * u * u - k * 0.6f, -ah - ancho * 0.12f * u), 2f + 6f * u, 1.6f + 4f * u, lerp(ext.tono(C.PAPEL, lejos * 0.5f), C.HORMIGON, 0.3f), 0.5f * (1f - u * 0.7f)) }
        // El cartel del plan, de pie sobre la nave de la izquierda: el marco rojo, el fondo de papel y la raya roja que sube.
        val c0 = Offset(base.x + ancho * 0.1f, base.y - h - ancho * 0.36f); val cw = ancho * 0.13f; val chh = ancho * 0.34f
        drawLine(p.sombra(C.HIERRO, lejos), Offset(c0.x + cw * 0.2f, c0.y + chh), Offset(c0.x + cw * 0.2f, base.y - h), 0.4f)
        drawLine(p.sombra(C.HIERRO, lejos), Offset(c0.x + cw * 0.8f, c0.y + chh), Offset(c0.x + cw * 0.8f, base.y - h), 0.4f)
        val alumbrado = ext.noche > 0.3f
        drawRect(if (alumbrado) lerp(C.PAPEL, C.FAROLA, 0.3f) else p.frente(C.PAPEL, lejos * 0.6f), c0, Size(cw, chh))
        drawRect(if (alumbrado) C.ROJO else p.frente(C.ROJO, lejos * 0.6f), c0, Size(cw, chh), style = Stroke(0.7f))
        val alto = (chh - 1.4f) * plan.coerceIn(0f, 1f)
        drawRect(if (alumbrado) lerp(C.ROJO, C.FAROLA, 0.2f) else p.luz(C.ROJO, lejos * 0.5f), Offset(c0.x + cw * 0.36f, c0.y + chh - 0.7f - alto), Size(cw * 0.28f, alto))
        drawLine(p.sombra(C.TINTA, lejos).copy(alpha = 0.6f), Offset(c0.x + cw * 0.3f, c0.y + chh * 0.1f), Offset(c0.x + cw * 0.7f, c0.y + chh * 0.1f), 0.35f)
        if (alumbrado) mancha(Offset(c0.x + cw / 2f, c0.y + chh / 2f), cw * 1.2f, chh * 0.7f, C.FAROLA, 0.3f * ext.noche)
    }

    /** Una chimenea alta de la central térmica, de franjas rojas y blancas, con su penacho de vapor blanco que el frío hace más grande. */
    fun DrawScope.chimeneaConVapor(ext: Luces, p: Pincel, pie: Offset, alto: Float, lejos: Float, frio: Float, viento: Float) {
        val w = alto * 0.045f
        val forma = poligono(pie + Offset(-w, 0f), pie + Offset(w, 0f), pie + Offset(w * 0.6f, -alto), pie + Offset(-w * 0.6f, -alto))
        drawPath(forma, p.frente(C.PANEL, lejos))
        clipPath(forma) { for (k in 0..4) drawRect(p.frente(C.ROJO, lejos), Offset(pie.x - w, pie.y - alto * (0.08f + k * 0.2f) - alto * 0.06f), Size(w * 2f, alto * 0.06f)) }
        drawLine(p.luz(C.PANEL, lejos).copy(alpha = 0.5f), pie + Offset(-w * 0.7f, 0f), pie + Offset(-w * 0.45f, -alto), w * 0.4f)
        val boca = pie + Offset(0f, -alto)
        if (ext.noche > 0.3f) drawCircle(C.ROJO.copy(alpha = ext.noche), 0.6f, boca)
        // El vapor: borbotones que suben y se tienden con el viento, claros donde les da el sol.
        val n = (8 + 10 * frio).toInt()
        for (k in 0 until n) {
            val u = k / n.toFloat(); val r = alto * (0.05f + 0.22f * u) * (0.7f + 0.6f * frio)
            val c = boca + Offset(-(0.2f + viento) * alto * 0.9f * u * u + kotlin.math.sin(k * 1.7f) * r * 0.2f, -alto * (0.08f + 0.5f * u) * (1f - 0.4f * viento))
            mancha(c, r, r * 0.75f, lerp(lerp(ext.tono(C.NIEVE, lejos * 0.6f), ext.solColor, 0.25f * (1f - ext.noche) * ext.solFuerza), C.LUNA, 0.4f * ext.noche), 0.5f * (1f - u * 0.6f))
        }
    }

    /** La grúa torre de la obra: la torre de celosía, la pluma con su contrapluma y el contrapeso, el cable con el gancho y, de noche, sus luces. */
    fun DrawScope.grua(ext: Luces, p: Pincel, pie: Offset, alto: Float, lejos: Float, gira: Float) {
        val c = lerp(C.ORO, C.LATON, 0.4f); val w = alto * 0.03f
        val top = pie + Offset(0f, -alto)
        drawLine(p.sombra(c, lejos), pie + Offset(-w, 0f), top + Offset(-w, 0f), 0.4f); drawLine(p.sombra(c, lejos), pie + Offset(w, 0f), top + Offset(w, 0f), 0.4f)
        var y = pie.y; var lado = 1f; while (y > top.y) { drawLine(p.sombra(c, lejos).copy(alpha = 0.7f), Offset(pie.x - w * lado, y), Offset(pie.x + w * lado, y - alto * 0.04f), 0.25f); y -= alto * 0.04f; lado = -lado }
        val largo = alto * 0.75f * gira; val atras = alto * 0.25f * gira
        drawLine(p.luz(c, lejos), top + Offset(-atras, 0f), top + Offset(largo, 0f), 0.6f)
        drawLine(p.sombra(c, lejos), top + Offset(0f, -alto * 0.08f), top + Offset(largo * 0.6f, 0f), 0.3f)
        drawLine(p.sombra(c, lejos), top + Offset(0f, -alto * 0.08f), top + Offset(-atras, 0f), 0.3f)
        drawRect(p.sombra(C.HORMIGON, lejos), Offset(top.x - atras, top.y), Size(atras * 0.3f, alto * 0.03f))
        drawRect(p.frente(c, lejos), Offset(top.x - w * 1.6f, top.y), Size(w * 3.2f, alto * 0.05f))
        val g = top + Offset(largo * 0.7f, 0f); drawLine(p.sombra(C.TINTA, lejos).copy(alpha = 0.7f), g, g + Offset(0f, alto * 0.4f), 0.2f)
        if (ext.noche > 0.3f) { drawCircle(C.ROJO.copy(alpha = ext.noche), 0.6f, top + Offset(largo, 0f)); drawCircle(C.ROJO.copy(alpha = ext.noche), 0.6f, top + Offset(0f, -alto * 0.08f)) }
    }

    /**
     * La torre del Kremlin, muy lejos, recortada contra el cielo: los cuerpos escalonados, el chapitel y, arriba, la
     * estrella roja «encendida siempre», con su resplandor de noche.
     */
    fun DrawScope.torreDelKremlin(ext: Luces, p: Pincel, pie: Offset, alto: Float, lejos: Float) {
        val w = alto * 0.18f; val ladrillo = lerp(C.LADRILLO, C.ROJO, 0.25f)
        // Los cuerpos: la base ancha, el cuerpo del reloj y el templete, cada uno más estrecho.
        for ((y0, y1, ancho) in listOf(Triple(0f, 0.42f, 1f), Triple(0.42f, 0.6f, 0.78f), Triple(0.6f, 0.72f, 0.56f))) {
            val r = Rect(pie.x - w * ancho, pie.y - alto * y1, pie.x + w * ancho, pie.y - alto * y0)
            drawRect(p.frente(ladrillo, lejos), r.topLeft, r.size)
            drawRect(if (p.solALaDerecha) p.luz(ladrillo, lejos) else p.sombra(ladrillo, lejos), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            drawLine(p.luz(C.PAPEL, lejos), Offset(r.left, r.top), Offset(r.right, r.top), 0.4f)
        }
        // El reloj (sin cifras) y el chapitel verde, con la estrella.
        drawCircle(p.medio(C.PAPEL, lejos), w * 0.35f, pie + Offset(0f, -alto * 0.52f))
        val ch = poligono(pie + Offset(-w * 0.5f, -alto * 0.72f), pie + Offset(w * 0.5f, -alto * 0.72f), pie + Offset(0f, -alto * 0.95f))
        drawPath(ch, p.frente(lerp(C.VERDE, C.HORMIGON, 0.4f), lejos))
        val c = pie + Offset(0f, -alto - 1.6f)
        val k = 0.4f + 0.6f * ext.noche
        mancha(c, 4.4f, 4.4f, C.ROJO, 0.45f * k)
        val estrella = Path(); for (i in 0..9) { val r = if (i % 2 == 0) 2.4f else 1f; val ang = (-PI / 2 + i * PI / 5).toFloat(); val q = c + Offset(cos(ang) * r, sin(ang) * r); if (i == 0) estrella.moveTo(q.x, q.y) else estrella.lineTo(q.x, q.y) }; estrella.close()
        drawPath(estrella, lerp(lerp(p.medio(C.ROJO, lejos * 0.4f), C.ROJO, 0.6f), lerp(C.ROJO, Color.White, 0.3f), 0.5f * ext.noche))
    }

    /** Una torre de dieciséis pisos, lejos: más alta que las filas, con su cara de luz y de sombra y sus ventanas encendidas una a una. */
    fun DrawScope.torreDeBloques(m: Momento, ext: Luces, p: Pincel, pie: Offset, ancho: Float, alto: Float, lejos: Float) {
        val cara = Rect(pie.x - ancho / 2f, pie.y - alto, pie.x + ancho / 2f, pie.y)
        drawRect(p.frente(C.PANEL, lejos), cara.topLeft, cara.size)
        drawRect(if (p.solALaDerecha) p.luz(C.PANEL, lejos) else p.sombra(C.PANEL, lejos), Offset(cara.right, cara.top + 0.6f), Size(ancho * 0.35f, alto - 0.6f))
        val oscuro = (ext.noche * 1.2f).coerceIn(0f, 1f); val az = Azar(Semilla.de("urss", "torre"))
        for (piso in 0 until 16) for (i in 0 until 4) {
            val x = cara.left + 1.4f + i * (ancho - 2f) / 4f; val y = cara.top + 1.4f + piso * (alto - 2f) / 16f
            val luz = R.ventanaEncendida(m, 5000 + piso * 10 + i)
            drawRect(if (luz && oscuro > 0.2f) lerp(C.VENTANA_LUZ, C.NARANJA, az.real() * 0.3f).copy(alpha = 0.4f + 0.6f * oscuro) else p.sombra(lerp(C.HIERRO, C.AZUL, 0.3f), lejos), Offset(x, y), Size(ancho / 4f - 1.4f, (alto - 2f) / 16f * 0.5f))
        }
        drawRect(p.frente(lerp(C.PANEL, C.HORMIGON, 0.4f), lejos), Offset(cara.left + ancho * 0.3f, cara.top - 3f), Size(ancho * 0.4f, 3f))
    }

    /**
     * El testero del bloque nuevo, la pared ciega del extremo, en sombra: los paneles con sus juntas, los chorretones y
     * la franja de color de cada piso. Va de [a]–[b] (el canto lejano, arriba y abajo) a [c]–[d] (el canto de la fachada).
     */
    fun DrawScope.testero(ext: Luces, p: Pincel, a: Offset, b: Offset, c: Offset, d: Offset, lejos: Float, nieve: Float) {
        val forma = poligono(a, c, d, b)
        drawPath(forma, Brush.horizontalGradient(0f to p.sombra(C.PANEL, lejos), 1f to lerp(p.sombra(C.PANEL, lejos), p.frente(C.PANEL, lejos), 0.35f), startX = a.x, endX = c.x))
        clipPath(forma) {
            for (j in 1 until 9) { val u = j / 9f; drawLine(p.sombra(lerp(C.PANEL, C.TINTA, 0.25f), lejos).copy(alpha = 0.4f), lerpO(a, b, u), lerpO(c, d, u), 0.4f) }
            for (j in 1..2) { val u = j / 3f; drawLine(p.sombra(lerp(C.PANEL, C.TINTA, 0.25f), lejos).copy(alpha = 0.35f), lerpO(a, c, u), lerpO(b, d, u), 0.4f) }
            for (j in 0..2) { val u = 0.2f + j * 0.3f; drawLine(p.sombra(lerp(C.PANEL, C.TINTA, 0.3f), lejos).copy(alpha = 0.2f), lerpO(a, c, u), lerpO(b, d, u), 1.4f) }
            // La franja de azulejo de color de lo alto del testero (los bloques de aquellos años la llevaban).
            drawPath(poligono(lerpO(a, b, 0.04f), lerpO(c, d, 0.04f), lerpO(c, d, 0.1f), lerpO(a, b, 0.1f)), p.sombra(lerp(C.AZUL, C.PANEL, 0.4f), lejos))
        }
        if (nieve > 0.3f) drawLine(lerp(p.arriba(C.NIEVE, lejos), C.LUNA, 0.3f * ext.noche), a + Offset(0f, -1f), c + Offset(0f, -1.6f), 1f)
    }

    /** La estrella roja del Kremlin, muy lejos, «encendida siempre»: un punto rubí con su resplandor. */
    fun DrawScope.estrellaDelKremlin(ext: Luces, c: Offset) {
        val k = 0.35f + 0.65f * ext.noche
        mancha(c, 3f, 3f, C.ROJO, 0.4f * k)
        drawCircle(lerp(C.ROJO, Color.White, 0.25f * ext.noche).copy(alpha = 0.5f + 0.5f * k), 0.75f, c)
    }

    /** Las banderas rojas de los balcones (sin letras ni emblemas): un paño colgado de un palo inclinado. */
    fun DrawScope.banderaDeBalcon(ext: Luces, p: Pincel, a: Offset, largo: Float, lejos: Float, mojada: Boolean) {
        val b = a + Offset(largo * 0.7f, -largo * 0.7f)
        drawLine(p.sombra(C.MADERA, lejos), a, b, 0.35f)
        val caida = if (mojada) 1.4f else 0.6f
        drawPath(Path().apply { moveTo(b.x, b.y); cubicTo(b.x + largo * 0.2f, b.y + largo * 0.1f * caida, b.x + largo * 0.4f, b.y + largo * 0.05f, b.x + largo * 0.5f, b.y + largo * 0.2f * caida); lineTo(b.x + largo * 0.36f, b.y + largo * 0.7f * caida); cubicTo(b.x + largo * 0.2f, b.y + largo * 0.6f * caida, b.x + largo * 0.1f, b.y + largo * 0.7f * caida, b.x - largo * 0.05f, b.y + largo * 0.5f * caida); close() }, lerp(p.luz(C.ROJO, lejos), C.ROJO, 0.3f))
    }
}
