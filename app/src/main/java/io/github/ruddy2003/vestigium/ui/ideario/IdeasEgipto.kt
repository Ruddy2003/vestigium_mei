package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.pow

// =====================================================================
//  Egipto
// =====================================================================

/**
 * Egipto: el rincón de trabajo de Pay, escriba de la tumba, en su casa del poblado de los artesanos (Deir el-Medina, Reino
 * Nuevo, hacia 1200 a. C.). Es un interior sin vano a la vista: un muro de adobe enlucido y encalado, con el enlucido
 * agrietado, desconchones donde asoman el adobe y su paja, y la luz dura y cálida de un vano alto que queda fuera del cuadro,
 * arriba a la izquierda. El mueble es una alacena empotrada en el muro: una viga gruesa de sicomoro hace de dintel y de balda
 * alta, y dentro, entre la jamba y un machón enlucido, las baldas de tablón de sicomoro (con sus espigas y algún remiendo);
 * el fondo de cada hueco es más hondo, con la sombra de la balda de arriba y la jamba en sombra. Debajo, el banco de adobe
 * pintado de ocre con su franja roja y azul (la losa de caliza de su frente es la placa de «y N más»). Encima, el punto
 * focal: un rollo de papiro a medio desenrollar, de pie contra el muro, con sus columnas de signos y rúbricas en rojo; delante
 * y ladeada, la paleta de escriba con sus pastillas roja y negra y los cálamos en su ranura, y el cuenco del agua; en medio,
 * un montón de óstraka de caliza y uno apoyado con un gato a medio dibujar; a la derecha, la lámpara de cuenco con su mecha y
 * el hollín que sube hasta el nicho arqueado del dios de la casa (un ibis de Thot de fayenza azul); la jarra de cerveza con
 * su tapón de barro, y colgados de una estaca, junto al borde, el codo de medir y la plomada. Al pie, sobre el piso de tierra,
 * la estera de junco, el taburete de tres patas y el cántaro del agua en su soporte, que rezuma y queda cortado por el borde.
 * Cada tema es, según la semilla, un cofre con tapa de capilla (de cedro, de sicomoro o pintado) cerrado con un hilo entre
 * dos perillas y un sello de barro, con el tema en un cartucho de yeso; un cesto de palma en espiral (natural o con franjas
 * teñidas) con su tapa cónica y una etiqueta de madera atada; o una vasija (barro rojo bruñido, marga con su banda azul o
 * encalada) con hierático inventado en el hombro, el tapón de barro ladeado y un óstrakon atado al cuello como etiqueta. En
 * los tres asoman las tablillas (más con más fichas). Lo que no tiene tema es un cuenco ancho con óstraka sueltos; un hueco
 * libre guarda un óstrakon con un dibujo a medio hacer o un escarabajo de fayenza. Al abrir uno, sube y se le alza la tapa.
 * De noche todo se hunde en un azul hondo y cálido: alumbra solo la lámpara (el halo ámbar en el encalado, el nicho encendido
 * desde abajo, el brillo en la fayenza, el filo en los cantos que la miran); las dos primeras filas se leen y las últimas casi
 * se pierden. Por dentro, el cofre forrado de lino. Las fichas son tablillas de escriba: madera con una cara de estuco blanco.
 */
object IdeasEgipto : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la casa
    /** El encalado y su sombra; el fondo hondo de los huecos. */
    private val CAL = Color(0xFFEFE5CF)
    private val CAL_OSC = Color(0xFFC6B699)
    /** El adobe, con su paja; el piso de tierra. */
    private val ADOBE = Color(0xFF8C6A48)
    private val PALMA = Color(0xFFC9A45C)
    /** El ocre del zócalo y del banco, el rojo óxido, el negro de carbón y el azul egipcio. */
    private val OCRE = Color(0xFFC38A3E)
    private val ROJO = Color(0xFFA2452B)
    private val CARBON = Color(0xFF2B2422)
    private val AZUL = Color(0xFF2F6E9C)
    /** El sicomoro de las baldas, la viga y los muebles, su luz y su sombra. */
    private val SICOMORO = Color(0xFFAA8455)
    private val SICOMORO_LUZ = Color(0xFFCCA672)
    private val SICOMORO_OSC = Color(0xFF6C4C2E)
    /** La caliza de los óstraka, el papiro, el barro rojo y la marga clara. */
    private val CALIZA = Color(0xFFE9DFC8)
    private val PAPIRO = Color(0xFFDFCA94)
    private val BARRO = Color(0xFFA65C38)
    private val MARGA = Color(0xFFD5B78A)
    /** La llama de la lámpara y la noche de la casa: azul hondo y cálido. */
    private val LLAMA = Color(0xFFFFC56E)
    private val NOCHE = Color(0xFF17152F)
    /** La sombra de noche: lo que la llama no alcanza, más hondo que el aire. */
    private val NOCHE_SOMBRA = Color(0xFF0D0C1E)
    /**
     * La caliza de los rótulos de noche lejos de la lámpara: con [TINTA] da unos 6:1 en el título y algo más de 4.5:1 en la
     * cuenta (al 78 %); cerca de la lámpara sube hacia [CALIZA] con un poco de llama.
     */
    private val CALIZA_LEJOS = Color(0xFFA59C86)

    /** Las del interior y las fichas (sin cambios). */
    private val CEDRO = Color(0xFF8E4E2C)
    private val CEDRO_LUZ = Color(0xFFB4703F)
    private val CEDRO_OSC = Color(0xFF55301A)
    private val LAPIS = Color(0xFF1D2F7A)
    private val FAYENZA = Color(0xFF2C8C9C)
    private val ORO = Color(0xFFD4AF37)
    private val CORNALINA = Color(0xFFB8471E)
    private val PIEDRA_OSC = Color(0xFF9A835A)
    private val EBANO = Color(0xFF1E1610)
    private val LINO = Color(0xFFE6D8B4)
    private val LINO_OSC = Color(0xFFBFAD84)
    private val ESTUCO = Color(0xFFF6F0E0)
    private val TINTA = Color(0xFF1E160C)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.5f
    override val corona = 0.6f
    override val base = 0.3f
    override val lado = 0.06f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la cara de arriba de la viga y su canto de frente, en fracción de su alto. */
    private const val SUP = 0.8f
    private const val FRENTE = 0.87f
    /** En la base: lo alto del banco (su cara de arriba), la franja del zócalo y el rincón del muro con el piso. */
    private const val BANCO = 0.03f
    private const val FRENTE_BANCO = 0.1f
    private const val SUELO = 0.5f
    /** Dónde acaba el banco, en fracción del ancho. */
    private const val FIN_BANCO = 0.8f
    /** El piso de cada hueco (la cara de arriba de la balda) y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val SALIDA = 0.06f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la lámpara, en fracciones del ancho. */
    private val LUZ = Offset(0.178f, 0.468f)

    /** Cuánto le llega de la lámpara a un punto [p] (en fracciones del ancho): cae con la distancia; abajo, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.4f * kotlin.math.exp(-3.3f * kotlin.math.sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que la lámpara echa hacia abajo sobre la alacena: cae de fila en fila (la primera, encendida; la segunda, a medias;
     * la tercera, tenue; la cuarta y la quinta, casi perdidas), un poco menos en la columna de la izquierda, más lejos de la
     * llama. Los puntos son los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.9f, 0.78f to 0.84f, 1.1f to 0.56f, 1.42f to 0.28f, 1.74f to 0.1f, 2.06f to 0.045f, 2.5f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.55f * (abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.7f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire azul de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.08f + NOCHE.red * 0.86f, c.green * 0.08f + NOCHE.green * 0.86f, c.blue * 0.08f + NOCHE.blue * 0.86f, c.alpha)

    /** Cómo se pinta algo según dónde está: de día tal cual (con [dia], un poco más claro); de noche, según [k], lo que le llega de la llama. */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.92f))
        return mezcla(v, LLAMA, 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche de la casa. */
    private fun Tono.s(a: Float) = if (o) NOCHE_SOMBRA.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)

    /** De noche la luz viene de la llama: lo que está a su izquierda la recibe por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** La sombra de algo en el muro: de día, corrida a la derecha y abajo; de noche, hacia fuera de la llama. No baja de [hasta]. */
    private fun DrawScope.sombraEnMuro(p: Path, desde: Offset, hasta: Float, a: Float = 0.2f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.024f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.045f * dx, -w * 0.01f) { drawPath(p, NOCHE_SOMBRA.copy(alpha = (0.9f * k).coerceAtMost(0.55f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la viga, la balda o el piso, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE_SOMBRA.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.22f * fuerza), Offset(x(0f, 0.32f), r.top), Size(r.width * 0.32f, r.height))
        drawRect(t.b(0.32f * fuerza), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
        drawRect(t.s(0.22f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** El filo cálido de la llama en el canto de arriba y en el costado que la mira, de noche. De día no pinta nada. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** Un brillo puntual: de día crema; de noche, el de la llama. */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.28f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /**
     * Signos inventados, a pincel, que parecen hierático (no copian ningún texto): trazos, ganchos, puntos y lazos, en una
     * columna (o en una línea, si [horizontal]) de [desde] a [hasta], a [paso].
     */
    private fun DrawScope.signos(fijo: Float, desde: Float, hasta: Float, paso: Float, semilla: Int, col: Color, g: Float, horizontal: Boolean = false) {
        var v = desde; var k = 0
        val s = paso * 0.72f
        fun p(a: Float, b: Float) = if (horizontal) Offset(v + a, fijo + b) else Offset(fijo + a, v + b)
        while (v < hasta) {
            when ((azar(semilla, k) * 7).toInt()) {
                0 -> drawLine(col, p(0f, -s * 0.42f), p(0f, s * 0.42f), g, StrokeCap.Round)
                1 -> drawArc(col, 200f, 190f, false, p(-s * 0.4f, -s * 0.3f), Size(s * 0.8f, s * 0.6f), style = Stroke(g, cap = StrokeCap.Round))
                2 -> { drawCircle(col, g * 0.95f, p(-s * 0.15f, 0f)); drawCircle(col, g * 0.8f, p(s * 0.2f, s * 0.1f)) }
                3 -> drawLine(col, p(-s * 0.38f, s * 0.32f), p(s * 0.38f, -s * 0.3f), g, StrokeCap.Round)
                4 -> { drawLine(col, p(-s * 0.45f, 0f), p(s * 0.45f, 0f), g, StrokeCap.Round); drawLine(col, p(s * 0.2f, 0f), p(s * 0.32f, -s * 0.38f), g, StrokeCap.Round) }
                5 -> drawArc(col, 30f, 280f, false, p(-s * 0.26f, -s * 0.26f), Size(s * 0.52f, s * 0.52f), style = Stroke(g, cap = StrokeCap.Round))
                else -> { drawLine(col, p(-s * 0.3f, -s * 0.35f), p(-s * 0.3f, s * 0.3f), g, StrokeCap.Round); drawLine(col, p(-s * 0.3f, s * 0.3f), p(s * 0.35f, s * 0.2f), g, StrokeCap.Round) }
            }
            v += paso * (0.85f + 0.45f * azar(semilla, k + 50)); k++
        }
    }

    /** Una tablilla de escriba de canto, de pie en [r]: la madera con su cara de estuco y el filo de arriba. */
    private fun DrawScope.tablilla(r: Rect, t: Tono, i: Int) {
        val u = density
        drawRect(t.s(0.25f), Offset(r.left + 1.4f * u, r.top + 1.4f * u), r.size)
        drawRect(t.c(if (i % 3 == 1) mezcla(SICOMORO_LUZ, CALIZA, 0.3f) else ESTUCO), r.topLeft, r.size)
        drawRect(t.c(SICOMORO), r.topLeft, Size(r.width, 1.6f * u))
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 0.7f * u))
        drawRect(t.s(0.18f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
        if (i % 2 == 0) drawLine(t.c(ROJO).copy(alpha = 0.6f), Offset(r.left + r.width * 0.25f, r.top + 4f * u), Offset(r.right - r.width * 0.3f, r.top + 4f * u), 0.6f * u)
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE
        val pieA = sup + (frente - sup) * 0.3f
        val pieF = sup + (frente - sup) * 0.85f
        val suelo = b.top + hb * SUELO

        // ---- el muro, el zócalo ocre y el piso de tierra
        muro(w, ch, b, suelo)
        piso(w, suelo, alto)

        // ---- la alacena: el fondo de los huecos, las baldas y la viga
        alacena(d, w, sup, frente)

        // ---- el banco de adobe, la estera, el taburete y el cántaro
        banco(b, w, suelo)
        estera(w, suelo, alto)
        taburete(Offset(w * 0.2f, alto - hb * 0.07f), hb)
        cantaro(Offset(w * 0.925f, alto - hb * 0.05f), hb)

        // ---- en el muro: el nicho del dios, el hollín de la lámpara, la estaca con el codo y la plomada
        nicho(Rect(w * 0.066f, ch * 0.29f, w * 0.196f, ch * 0.66f))
        hollin(Offset(LUZ.x * w, LUZ.y * w), ch)
        codo(Offset(w * 0.962f, ch * 0.1f), ch, sup)

        // ---- lo de encima de la viga, de atrás adelante
        papiro(w, sup, pieA, pieF, ch)
        ostraka(Offset(w * 0.695f, pieA), sup)
        jarra(Offset(w * 0.862f, pieA), sup)
        paleta(Offset(w * 0.505f, pieF), sup)
        cuenco(Offset(w * 0.605f, pieF))
        lampara(Offset(w * 0.13f, pieF))

        // ---- la placa de «y N más»: la losa de caliza del frente del banco
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRect(if (o) mezcla(CALIZA_LEJOS, CALIZA, 0.15f) else CALIZA, r.topLeft, r.size)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            drawRect(t.s(0.15f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            drawRect(t.c(ROJO).copy(alpha = 0.55f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5f * u, r.height - 5f * u), style = Stroke(0.8f * u))
        }

        // ---- la luz de la lámpara, por encima de lo que tiene cerca
        val llama = LUZ * w
        if (o) {
            mancha(llama + Offset(0f, -w * 0.02f), w * 0.46f, LLAMA.copy(alpha = 0.1f))
            mancha(llama + Offset(0f, -w * 0.03f), w * 0.2f, LLAMA.copy(alpha = 0.2f))
            mancha(llama, w * 0.06f, LLAMA.copy(alpha = 0.35f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * El muro de adobe encalado: la mancha de luz arriba a la izquierda (de noche, el halo de la lámpara), pinceladas de cal,
     * la paja que asoma, grietas, dos desconchones con el adobe a la vista y, abajo, el zócalo ocre con su franja roja y azul.
     */
    private fun DrawScope.muro(w: Float, ch: Float, b: Rect, suelo: Float) {
        val u = density
        val o = esNoche
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(CAL, LUZ_MUEBLE, 0.4f), CAL, mezcla(CAL, CAL_OSC, 0.55f)), 0f, w), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * 0.06f, -w * 0.04f), w * 0.8f, luzMueble(0.45f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.12f)), ch * 0.4f, suelo), Offset(0f, ch * 0.4f), Size(w, suelo - ch * 0.4f))
        } else {
            fun k(dd: Float) = Tono(true, luz(LUZ + Offset(dd, 0f)).pow(1.6f)).c(CAL)
            drawRect(Brush.radialGradient(0f to k(0.03f), 0.1f to k(0.11f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.8f), 1f to k(1.3f),
                center = LUZ * w, radius = w * 1.3f), Offset.Zero, Size(w, suelo))
        }
        pinceladas(Rect(0f, 0f, w, suelo), if (o) NOCHE else CAL_OSC, 5001, 46, u, if (o) 0.05f else 0.11f)
        if (!o) pinceladas(Rect(0f, 0f, w, suelo), mezcla(CAL, LUZ_MUEBLE, 0.6f), 5002, 20, u, 0.12f)
        // La paja del adobe, que asoma en el enlucido.
        for (k in 0 until 34) {
            val p = Offset(w * azar(5010, k), suelo * azar(5011, k))
            val t = tn(p)
            val l = w * (0.006f + 0.008f * azar(5012, k))
            val a = (azar(5013, k) - 0.5f) * 1.6f
            drawLine(t.c(mezcla(PALMA, ADOBE, 0.3f)).copy(alpha = 0.55f), p, p + Offset(l * kotlin.math.cos(a), l * kotlin.math.sin(a)), 0.7f * u)
        }
        // Grietas del enlucido.
        listOf(Offset(0.2f, 0.05f), Offset(0.52f, 0.2f), Offset(0.985f, 0.35f), Offset(0.015f, 0.95f)).forEachIndexed { i, f ->
            val p0 = Offset(w * f.x, ch * f.y)
            val t = tn(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 5) { q += Offset(w * (0.007f + 0.012f * azar(5020 + i, j)) * (if (j % 2 == 0) 1f else -0.5f), w * (0.012f + 0.012f * azar(5030 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.3f), style = Stroke(0.8f * u))
            translate(0.9f * u, 0.9f * u) { drawPath(g, t.b(0.3f), style = Stroke(0.5f * u)) }
        }
        // Los desconchones: el enlucido caído y el adobe con su paja a la vista.
        desconchon(Rect(w * 0.57f, ch * 0.07f, w * 0.66f, ch * 0.17f), 5040)
        desconchon(Rect(w * 0.73f, ch * 0.42f, w * 0.775f, ch * 0.5f), 5041)
        desconchon(Rect(w * 0.955f, b.top - w * 0.2f, w * 1.02f, b.top - w * 0.12f), 5042)
        // El zócalo ocre y su franja de color, a lo largo del muro (el banco lo tapa casi todo).
        val dado = b.top + b.height * FRENTE_BANCO
        val t = tn(Offset(w * 0.9f, dado + w * 0.03f))
        drawRect(t.c(OCRE), Offset(0f, dado), Size(w, suelo - dado))
        pinceladas(Rect(0f, dado, w, suelo), if (o) NOCHE else mezcla(OCRE, ADOBE, 0.4f), 5050, 10, u, 0.14f)
        franja(0f, w, dado, t)
    }

    /** La franja de color que remata el zócalo: un filete negro, la banda roja y una línea de azul egipcio. */
    private fun DrawScope.franja(x0: Float, x1: Float, y: Float, t: Tono) {
        val u = density
        val w = anchoW
        drawRect(t.c(AZUL), Offset(x0, y - w * 0.006f), Size(x1 - x0, w * 0.004f))
        drawRect(t.c(CARBON), Offset(x0, y - w * 0.002f), Size(x1 - x0, 0.9f * u))
        drawRect(t.c(ROJO), Offset(x0, y), Size(x1 - x0, w * 0.011f))
        drawRect(t.b(0.25f), Offset(x0, y), Size(x1 - x0, 0.7f * u))
        drawRect(t.c(CARBON), Offset(x0, y + w * 0.011f), Size(x1 - x0, 0.9f * u))
    }

    /** Un desconchón en [r]: el hueco irregular, las hiladas de adobe con su paja, la sombra del borde de arriba y el canto del enlucido. */
    private fun DrawScope.desconchon(r: Rect, semilla: Int) {
        val u = density
        val t = tn(r.center)
        val hoyo = contornoIrregular(r, semilla, 16, 0.16f)
        // Alrededor, el enlucido que se desprende: una aureola un poco más oscura y gastada.
        val aureola = contornoIrregular(Rect(r.left - r.width * 0.18f, r.top - r.height * 0.2f, r.right + r.width * 0.2f, r.bottom + r.height * 0.22f), semilla + 1, 16, 0.12f)
        drawPath(aureola, t.c(CAL_OSC).copy(alpha = 0.35f))
        drawPath(hoyo, t.c(mezcla(ADOBE, CAL_OSC, 0.3f)))
        clipPath(hoyo) {
            // Las hiladas de adobe y sus juntas.
            val hilada = anchoW * 0.022f
            var y = r.top; var k = 0
            while (y < r.bottom) {
                drawRect(t.s(0.25f), Offset(r.left, y), Size(r.width, 1.1f * u))
                var x = r.left - (if (k % 2 == 0) 0f else hilada)
                while (x < r.right) { drawRect(t.s(0.22f), Offset(x, y), Size(1f * u, hilada)); x += hilada * 2.2f }
                y += hilada; k++
            }
            for (j in 0 until 10) {
                val p = Offset(r.left + r.width * azar(semilla, j), r.top + r.height * azar(semilla, j + 20))
                drawLine(t.c(PALMA).copy(alpha = 0.7f), p, p + Offset(r.width * 0.12f, (azar(semilla, j + 40) - 0.5f) * r.height * 0.15f), 0.7f * u)
            }
            // La sombra del canto de arriba a la izquierda del enlucido.
            translate(2.4f * u, 2.8f * u) { drawPath(hoyo, t.s(0.4f), style = Stroke(4f * u)) }
        }
        // El canto del enlucido: claro abajo a la derecha, donde le da la luz.
        clipRect(r.center.x - r.width * 0.1f, r.center.y, r.right + 4f * u, r.bottom + 4f * u) { translate(0.8f * u, 0.8f * u) { drawPath(hoyo, t.b(0.6f), style = Stroke(1.2f * u)) } }
    }

    /** El piso de tierra apisonada, con su rincón en sombra y algunas piedrecillas. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        drawRect(t.c(mezcla(ADOBE, PALMA, 0.3f)), Offset(0f, suelo), Size(w, alto - suelo))
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else ADOBE, 5060, 22, u, 0.16f)
        drawRect(t.s(0.3f), Offset(0f, suelo), Size(w, w * 0.012f))
        drawRect(t.s(0.15f), Offset(0f, suelo + w * 0.012f), Size(w, w * 0.02f))
        for (k in 0 until 9) {
            val p = Offset(w * azar(5061, k), suelo + (alto - suelo) * (0.2f + 0.75f * azar(5062, k)))
            val r = w * (0.002f + 0.003f * azar(5063, k))
            drawOval(t.s(0.3f), Offset(p.x - r * 0.5f, p.y), Size(r * 2.4f, r))
            drawCircle(t.c(CALIZA), r, p)
        }
    }

    // ---------------------------------------------------------------- la alacena

    /**
     * La alacena empotrada: el hueco hondo de cada columna (el fondo lo termina cada compartimento), los cantos de la jamba y del
     * machón, las baldas de tablón de sicomoro con sus espigas y su sombra, y la viga gruesa de arriba que hace de balda alta.
     */
    private fun DrawScope.alacena(d: Disposicion, w: Float, sup: Float, frente: Float) {
        val u = density
        val sep = d.separacion
        val viga = d.corona.bottom + sep * 0.45f
        val col0 = d.huecos.take(cols)
        val e = w * 0.009f
        // El hueco hondo de cada columna, de la viga al banco.
        col0.forEach { h0 ->
            val x0 = h0.left - e; val x1 = h0.right + e
            val r = Rect(x0, d.corona.bottom, x1, d.base.top + d.base.height * BANCO)
            if (!esNoche) drawRect(mezcla(CAL_OSC, ADOBE, 0.22f), r.topLeft, r.size)
            else drawRect(Brush.verticalGradient(List(6) { i -> tn(Offset(r.center.x, r.top + r.height * i / 5f)).c(mezcla(CAL_OSC, ADOBE, 0.22f)) }, r.top, r.bottom), r.topLeft, r.size)
            // La jamba de la izquierda deja su sombra; el costado de la derecha recibe la luz.
            d.filas.forEach { f ->
                val t = tn(Offset(r.center.x, f.center.y))
                drawRect(t.s(0.35f), Offset(x0, f.top - sep), Size(e * 1.4f, f.height + sep))
                drawRect(if (luzDerecha(x0)) t.b(0.45f) else t.b(0.3f), Offset(x1 - e, f.top - sep), Size(e, f.height + sep))
                drawRect(t.s(0.25f), Offset(x1 - e, f.top - sep), Size(0.8f * u, f.height + sep))
            }
            // El canto del enlucido alrededor de la boca, un poco irregular.
            val tb = tn(Offset(r.center.x, r.top + w * 0.3f))
            drawRect(tb.b(0.5f), Offset(x0 - 1.2f * u, r.top), Size(1.2f * u, r.height))
            drawRect(tb.s(0.3f), Offset(x1, r.top), Size(1.4f * u, r.height))
        }
        // Las baldas de tablón, de jamba a jamba, con su sombra en el fondo y en el machón.
        d.filas.forEachIndexed { i, f ->
            val y = f.bottom
            val th = sep * 0.55f
            val t = tn(Offset(w * 0.55f, y))
            val r = Rect(w * 0.028f, y, w * 0.972f, y + th)
            drawPath(poligono(r.left + w * 0.01f, r.bottom, r.right + w * 0.004f, r.bottom, r.right + w * 0.004f, r.bottom + sep * 0.42f, r.left + w * 0.03f, r.bottom + sep * 0.42f), t.s(0.32f))
            tablon(r, t, 5100 + i * 7, i)
        }
        // La viga de arriba: su cara de arriba vista un poco desde arriba y su frente labrado a azuela.
        viga(Rect(w * 0.012f, sup, w * 0.988f, frente), Rect(w * 0.006f, frente, w * 0.994f, viga), sep)
    }

    /** Un tablón de sicomoro de frente: la veta, el canto con luz, la sombra de abajo, las cabezas y, según [i], una junta con espigas o un remiendo. */
    private fun DrawScope.tablon(r: Rect, t: Tono, semilla: Int, i: Int) {
        val u = density
        val w = anchoW
        drawRect(t.c(SICOMORO), r.topLeft, r.size)
        vetas(r, (if (esNoche) NOCHE else SICOMORO_OSC).copy(alpha = 0.35f), semilla, 0.8f, grosor = 0.6f, ondulacion = 0.5f)
        drawRect(t.c(SICOMORO_LUZ), r.topLeft, Size(r.width, r.height * 0.18f))
        drawRect(t.b(0.55f), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.3f * u), Size(r.width, 1.3f * u))
        // Las cabezas, metidas en el enlucido.
        listOf(r.left, r.right - w * 0.008f).forEach { x -> drawRect(t.c(SICOMORO_OSC), Offset(x, r.top), Size(w * 0.008f, r.height)) }
        if (i % 2 == 0) {
            // Dos tablas ensambladas: la junta y sus espigas.
            val x = r.left + r.width * (0.3f + 0.35f * azar(semilla, 1))
            drawRect(t.s(0.4f), Offset(x, r.top), Size(1.1f * u, r.height))
            drawRect(t.b(0.25f), Offset(x + 1.1f * u, r.top), Size(0.7f * u, r.height))
            listOf(0.35f, 0.7f).forEach { fy -> drawCircle(t.c(SICOMORO_OSC), 1.3f * u, Offset(x - 3.5f * u, r.top + r.height * fy)); drawCircle(t.c(SICOMORO_OSC), 1.3f * u, Offset(x + 4.5f * u, r.top + r.height * fy)) }
        } else {
            // Un remiendo: un trozo de otra madera, clavado con cuatro espigas.
            val x = r.left + r.width * (0.12f + 0.6f * azar(semilla, 2))
            val p = Rect(x, r.top + r.height * 0.2f, x + w * 0.07f, r.bottom - r.height * 0.15f)
            drawRect(t.s(0.3f), Offset(p.left + 1f * u, p.top + 1.2f * u), p.size)
            drawRect(t.c(mezcla(SICOMORO, CEDRO, 0.45f)), p.topLeft, p.size)
            drawRect(t.b(0.4f), p.topLeft, Size(p.width, 0.8f * u))
            listOf(p.topLeft, Offset(p.right, p.top), Offset(p.left, p.bottom), p.bottomRight).forEach { q ->
                drawCircle(t.c(SICOMORO_OSC), 1.1f * u, Offset(q.x + (if (q.x == p.left) 2.4f else -2.4f) * u, q.y + (if (q.y == p.top) 2.2f else -2.2f) * u))
            }
        }
    }

    /** La viga: la cara de arriba ([sup]) y el frente ([fr]) con la veta, las marcas de la azuela, una raja y la sombra que echa abajo. */
    private fun DrawScope.viga(sup: Rect, fr: Rect, sep: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(w * 0.6f, fr.center.y))
        // La sombra de la viga en el muro y en la boca de la alacena.
        drawRect(t.s(0.38f), Offset(fr.left + w * 0.012f, fr.bottom), Size(fr.width, sep * 0.55f))
        drawRect(t.s(0.18f), Offset(fr.left + w * 0.02f, fr.bottom + sep * 0.55f), Size(fr.width, sep * 0.3f))
        drawPath(poligono(fr.right, sup.top + w * 0.004f, fr.right + w * 0.022f, sup.top + w * 0.016f, fr.right + w * 0.022f, fr.bottom + w * 0.014f, fr.right, fr.bottom), t.s(0.25f))
        // La cara de arriba.
        if (!esNoche) drawRect(SICOMORO_LUZ, sup.topLeft, sup.size)
        else drawRect(Brush.horizontalGradient(List(5) { i -> tn(Offset(sup.left + sup.width * i / 4f, sup.center.y)).c(SICOMORO_LUZ) }, sup.left, sup.right), sup.topLeft, sup.size)
        vetas(sup, (if (esNoche) NOCHE else SICOMORO_OSC).copy(alpha = 0.3f), 5200, 0.6f, grosor = 0.6f, ondulacion = 0.6f)
        if (!esNoche) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.22f), Color.Transparent, sombraMueble(0.12f)), sup.left, sup.right), sup.topLeft, sup.size)
        drawRect(if (esNoche) NOCHE.copy(alpha = 0.35f) else sombraMueble(0.18f), sup.topLeft, Size(sup.width, 1.6f * u))
        // Un cerco de agua y unas manchas de tinta, del uso.
        val tc = tn(Offset(w * 0.47f, sup.center.y))
        drawOval(tc.s(0.18f), Offset(w * 0.47f, sup.top + sup.height * 0.25f), Size(w * 0.06f, sup.height * 0.5f), style = Stroke(1.2f * u))
        drawCircle(tc.c(CARBON).copy(alpha = 0.5f), 1.4f * u, Offset(w * 0.4f, sup.top + sup.height * 0.7f))
        drawCircle(tc.c(ROJO).copy(alpha = 0.5f), 1.1f * u, Offset(w * 0.415f, sup.top + sup.height * 0.55f))
        // El frente.
        if (!esNoche) drawRect(SICOMORO, fr.topLeft, fr.size)
        else drawRect(Brush.horizontalGradient(List(5) { i -> tn(Offset(fr.left + fr.width * i / 4f, fr.center.y)).c(SICOMORO) }, fr.left, fr.right), fr.topLeft, fr.size)
        vetas(fr, (if (esNoche) NOCHE else SICOMORO_OSC).copy(alpha = 0.4f), 5201, 0.9f, grosor = 0.7f, ondulacion = 0.8f)
        if (!esNoche) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.16f), Color.Transparent, sombraMueble(0.18f)), fr.left, fr.right), fr.topLeft, fr.size)
        // Las marcas de la azuela: cortes cortos en arco, en hileras.
        for (k in 0 until 26) {
            val x = fr.left + fr.width * azar(5210, k)
            val y = fr.top + fr.height * (0.2f + 0.65f * azar(5211, k))
            val tt = tn(Offset(x, y))
            val l = w * (0.014f + 0.012f * azar(5212, k))
            drawArc(tt.s(0.28f), 200f, 110f, false, Offset(x, y), Size(l, l * 0.5f), style = Stroke(0.8f * u))
            drawArc(tt.b(0.3f), 200f, 110f, false, Offset(x, y + 1f * u), Size(l, l * 0.5f), style = Stroke(0.6f * u))
        }
        // La raja a lo largo y un nudo.
        val tr = tn(Offset(w * 0.25f, fr.center.y))
        drawPath(Path().apply { moveTo(w * 0.08f, fr.top + fr.height * 0.55f); cubicTo(w * 0.16f, fr.top + fr.height * 0.5f, w * 0.24f, fr.top + fr.height * 0.62f, w * 0.31f, fr.top + fr.height * 0.58f) }, tr.s(0.5f), style = Stroke(1.2f * u))
        val nudo = Offset(w * 0.79f, fr.top + fr.height * 0.45f)
        val tk = tn(nudo)
        drawOval(tk.c(SICOMORO_OSC), Offset(nudo.x - w * 0.012f, nudo.y - w * 0.005f), Size(w * 0.024f, w * 0.01f))
        drawOval(tk.s(0.3f), Offset(nudo.x - w * 0.018f, nudo.y - w * 0.009f), Size(w * 0.036f, w * 0.018f), style = Stroke(0.7f * u))
        // Los cantos: el de arriba con luz, la cabeza derecha en sombra.
        drawRect(t.c(SICOMORO_LUZ), fr.topLeft, Size(fr.width, 1.6f * u))
        drawRect(tn(Offset(w * 0.3f, fr.top)).b(0.7f), fr.topLeft, Size(fr.width, 1f * u))
        drawRect(t.s(0.35f), Offset(fr.left, fr.bottom - 1.6f * u), Size(fr.width, 1.6f * u))
        drawRect(t.s(0.3f), Offset(fr.right - w * 0.01f, fr.top), Size(w * 0.01f, fr.height))
        drawRect(t.b(0.3f), Offset(fr.left, fr.top), Size(w * 0.006f, fr.height))
    }

    // ---------------------------------------------------------------- el banco y lo del pie

    /** El banco de adobe enlucido: la cara de arriba gastada, el frente ocre con su franja, una esquina comida y su sombra. */
    private fun DrawScope.banco(b: Rect, w: Float, suelo: Float) {
        val u = density
        val hb = b.height
        val top = b.top + hb * BANCO
        val fr = b.top + hb * FRENTE_BANCO
        val xR = w * FIN_BANCO
        val t = tn(Offset(w * 0.5f, fr + hb * 0.15f))
        // Su sombra en el zócalo, a la derecha, y en el piso.
        drawPath(poligono(xR, top, xR + w * 0.05f, top + w * 0.02f, xR + w * 0.05f, suelo, xR, suelo), t.s(0.3f))
        drawRect(t.s(0.3f), Offset(0f, suelo), Size(xR + w * 0.03f, hb * 0.07f))
        // La cara de arriba.
        drawRect(t.c(mezcla(CAL, OCRE, 0.25f)), Offset(-w * 0.01f, top), Size(xR + w * 0.01f, fr - top))
        drawRect(t.b(0.4f), Offset(0f, top), Size(xR, 1f * u))
        // El frente ocre, con pinceladas, la franja y el desgaste de abajo.
        val frente = Rect(-w * 0.01f, fr, xR, suelo)
        drawRect(t.c(OCRE), frente.topLeft, frente.size)
        if (!esNoche) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.18f), Color.Transparent, sombraMueble(0.14f)), 0f, xR), frente.topLeft, frente.size)
        pinceladas(frente, if (esNoche) NOCHE else mezcla(OCRE, ADOBE, 0.5f), 5300, 18, u, 0.16f)
        franja(-w * 0.01f, xR, fr + hb * 0.04f, t)
        drawRect(t.c(mezcla(OCRE, ADOBE, 0.5f)), Offset(0f, suelo - hb * 0.06f), Size(xR, hb * 0.06f))
        // La cabeza del banco, en sombra.
        drawPath(poligono(xR, top, xR + w * 0.018f, top + w * 0.01f, xR + w * 0.018f, suelo, xR, suelo), t.c(mezcla(OCRE, ADOBE, 0.55f)))
        drawPath(poligono(xR, top, xR + w * 0.018f, top + w * 0.01f, xR + w * 0.018f, suelo, xR, suelo), t.s(0.25f))
        drawRect(t.b(0.35f), Offset(xR - 1f * u, top), Size(1f * u, suelo - top))
        // Las pasadas de la mano al enlucir, un desconchón chico y la suciedad del pie.
        for (k in 0 until 9) {
            val c = Offset(w * (0.04f + 0.085f * k + 0.03f * azar(5302, k)), fr + (suelo - fr) * (0.4f + 0.35f * azar(5303, k)))
            drawArc(t.s(0.1f), 200f, 120f, false, Offset(c.x - w * 0.04f, c.y - hb * 0.12f), Size(w * 0.08f, hb * 0.25f), style = Stroke(1.4f * u))
            drawArc(t.b(0.14f), 200f, 120f, false, Offset(c.x - w * 0.04f, c.y - hb * 0.12f + 1.6f * u), Size(w * 0.08f, hb * 0.25f), style = Stroke(1f * u))
        }
        desconchon(Rect(w * 0.68f, fr + hb * 0.12f, w * 0.74f, fr + hb * 0.26f), 5301)
        drawRect(t.s(0.12f), Offset(0f, suelo - hb * 0.12f), Size(xR, hb * 0.06f))
    }

    /** La estera de junco en el piso, vista un poco desde arriba: los juncos a lo largo, las ataduras y los cabos sueltos. */
    private fun DrawScope.estera(w: Float, suelo: Float, alto: Float) {
        val u = density
        val y0 = suelo + (alto - suelo) * 0.22f; val y1 = alto - (alto - suelo) * 0.06f
        val x0 = -w * 0.02f; val x1 = w * 0.58f
        val t = tn(Offset(w * 0.3f, y1))
        val forma = poligono(x0 + w * 0.02f, y0, x1, y0, x1 + w * 0.03f, y1, x0, y1)
        drawPath(forma, t.s(0.3f))
        translate(0f, -w * 0.004f) {
            drawPath(forma, t.c(mezcla(PALMA, Color(0xFF8E9A5A), 0.22f)))
            clipPath(forma) {
                var y = y0; var k = 0
                while (y < y1) {
                    drawRect(if (k % 2 == 0) t.s(0.16f) else t.b(0.22f), Offset(x0, y), Size(x1 - x0 + w * 0.03f, 1f * u))
                    y += 2.6f * u; k++
                }
                listOf(0.12f, 0.33f, 0.55f, 0.78f).forEach { f ->
                    val x = x0 + (x1 - x0) * f
                    drawLine(t.c(mezcla(ADOBE, ROJO, 0.3f)), Offset(x, y0), Offset(x + w * 0.006f, y1), 1.6f * u)
                    drawLine(t.b(0.3f), Offset(x - 1f * u, y0), Offset(x - 1f * u + w * 0.006f, y1), 0.6f * u)
                }
            }
            // Los cabos sueltos del borde de la derecha.
            for (k in 0 until 8) {
                val y = y0 + (y1 - y0) * (k + 0.5f) / 8f
                val x = x1 + w * 0.03f * (y - y0) / (y1 - y0)
                drawLine(t.c(PALMA), Offset(x, y), Offset(x + w * (0.008f + 0.01f * azar(5310, k)), y + (azar(5311, k) - 0.5f) * 4f * u), 0.8f * u)
            }
        }
    }

    /** El taburete de tres patas, delante del banco: el asiento cóncavo de madera y las patas abiertas, con su sombra en la estera. */
    private fun DrawScope.taburete(pie: Offset, hb: Float) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        val asiento = pie.y - hb * 0.4f
        val an = w * 0.085f
        contacto(Offset(pie.x + w * 0.015f, pie.y), an * 2.4f, hb * 0.07f, 0.35f)
        val madera = t.c(SICOMORO_OSC)
        // La pata de atrás, más corta y en sombra.
        drawLine(t.c(mezcla(SICOMORO_OSC, NOCHE, 0.3f)), Offset(pie.x + an * 0.1f, asiento + hb * 0.04f), Offset(pie.x + an * 0.2f, pie.y - hb * 0.06f), w * 0.011f, StrokeCap.Round)
        // Las dos de delante, abiertas.
        listOf(-1f, 1f).forEach { s ->
            val a = Offset(pie.x + s * an * 0.45f, asiento + hb * 0.04f)
            val z = Offset(pie.x + s * an * 0.95f, pie.y)
            drawLine(madera, a, z, w * 0.013f, StrokeCap.Round)
            drawLine(if (s < 0) t.b(0.35f) else t.s(0.25f), a + Offset(-w * 0.003f, 0f), z + Offset(-w * 0.003f, 0f), w * 0.004f, StrokeCap.Round)
        }
        // El asiento: la tabla con su cara de arriba hundida.
        val tabla = Path().apply {
            moveTo(pie.x - an, asiento - hb * 0.05f)
            cubicTo(pie.x - an * 0.4f, asiento + hb * 0.02f, pie.x + an * 0.4f, asiento + hb * 0.02f, pie.x + an, asiento - hb * 0.05f)
            lineTo(pie.x + an * 0.92f, asiento + hb * 0.06f)
            cubicTo(pie.x + an * 0.4f, asiento + hb * 0.1f, pie.x - an * 0.4f, asiento + hb * 0.1f, pie.x - an * 0.92f, asiento + hb * 0.06f)
            close()
        }
        drawPath(tabla, t.c(SICOMORO))
        clipPath(tabla) {
            drawRect(t.c(SICOMORO_LUZ), Offset(pie.x - an, asiento - hb * 0.06f), Size(an * 2f, hb * 0.07f))
            drawRect(t.s(0.25f), Offset(pie.x + an * 0.4f, asiento - hb * 0.06f), Size(an, hb * 0.2f))
        }
        drawPath(Path().apply { moveTo(pie.x - an, asiento - hb * 0.05f); cubicTo(pie.x - an * 0.4f, asiento + hb * 0.02f, pie.x + an * 0.4f, asiento + hb * 0.02f, pie.x + an, asiento - hb * 0.05f) }, t.b(0.55f), style = Stroke(1f * u))
    }

    /** El cántaro del agua en su soporte de madera, cortado por el borde derecho: la marga clara, la mancha húmeda y una gota. */
    private fun DrawScope.cantaro(pie: Offset, hb: Float) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        val alto = hb * 0.92f
        val an = w * 0.13f
        val top = pie.y - alto
        contacto(Offset(pie.x + w * 0.01f, pie.y), an * 1.4f, hb * 0.06f, 0.35f)
        // El soporte: el aro y tres patas.
        val aro = pie.y - alto * 0.32f
        listOf(-1f, 0.1f, 1f).forEach { s -> drawLine(t.c(if (s == 0.1f) mezcla(SICOMORO_OSC, NOCHE, 0.3f) else SICOMORO_OSC), Offset(pie.x + s * an * 0.38f, aro), Offset(pie.x + s * an * 0.5f, pie.y), w * 0.009f, StrokeCap.Round) }
        // El cántaro: cuerpo de huevo con el fondo en punta, cuello corto y labio.
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.18f, top + alto * 0.08f)
            cubicTo(pie.x - an * 0.6f, top + alto * 0.18f, pie.x - an * 0.62f, top + alto * 0.55f, pie.x - an * 0.1f, aro + alto * 0.14f)
            lineTo(pie.x + an * 0.1f, aro + alto * 0.14f)
            cubicTo(pie.x + an * 0.62f, top + alto * 0.55f, pie.x + an * 0.6f, top + alto * 0.18f, pie.x + an * 0.18f, top + alto * 0.08f)
            close()
        }
        drawPath(cuerpo, t.c(MARGA))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - an * 0.6f, top, pie.x + an * 0.6f, aro + alto * 0.2f), t, 1.2f)
            // Lo húmedo: la marga rezuma por abajo.
            drawOval(t.c(mezcla(MARGA, ADOBE, 0.45f)).copy(alpha = 0.7f), Offset(pie.x - an * 0.7f, top + alto * 0.5f), Size(an * 1.4f, alto * 0.5f))
        }
        drawRect(t.c(mezcla(MARGA, CALIZA, 0.2f)), Offset(pie.x - an * 0.2f, top), Size(an * 0.4f, alto * 0.09f))
        drawRect(t.b(0.5f), Offset(pie.x - an * 0.2f, top), Size(an * 0.4f, 1f * u))
        drawOval(t.s(0.5f), Offset(pie.x - an * 0.16f, top - alto * 0.012f), Size(an * 0.32f, alto * 0.03f))
        // El aro del soporte, por delante.
        drawRect(t.c(SICOMORO), Offset(pie.x - an * 0.42f, aro - hb * 0.02f), Size(an * 0.84f, hb * 0.045f))
        drawRect(t.b(0.4f), Offset(pie.x - an * 0.42f, aro - hb * 0.02f), Size(an * 0.84f, 1f * u))
        // La gota que cae al piso y su charquito.
        drawCircle(t.c(mezcla(MARGA, AZUL, 0.25f)), 1.5f * u, Offset(pie.x, aro + alto * 0.2f))
        drawOval(t.c(mezcla(ADOBE, NOCHE, 0.35f)).copy(alpha = 0.6f), Offset(pie.x - an * 0.25f, pie.y - hb * 0.03f), Size(an * 0.5f, hb * 0.035f))
    }

    // ---------------------------------------------------------------- en el muro

    /** El nicho del dios de la casa: el arco hondo, el borde del enlucido, el ibis de Thot de fayenza azul en su peana. */
    private fun DrawScope.nicho(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(Offset(r.center.x, r.bottom))
        val o = esNoche
        val arco = arcoMedioPunto(r)
        // El borde del enlucido, un poco más grueso, con su sombra.
        val borde = arcoMedioPunto(Rect(r.left - w * 0.008f, r.top - w * 0.008f, r.right + w * 0.008f, r.bottom + w * 0.006f))
        drawPath(borde, t.c(mezcla(CAL, LUZ_MUEBLE, 0.3f)))
        drawPath(arco, t.c(mezcla(CAL_OSC, ADOBE, 0.3f)))
        clipPath(arco) {
            // De día la sombra del arco arriba a la izquierda; de noche, encendido desde abajo por la lámpara.
            if (!o) {
                drawRect(sombraMueble(0.32f), r.topLeft, Size(r.width, r.height * 0.3f))
                drawRect(sombraMueble(0.25f), r.topLeft, Size(r.width * 0.28f, r.height))
                drawPath(poligono(r.left + r.width * 0.28f, r.top + r.height * 0.3f, r.right, r.top + r.height * 0.55f, r.right, r.bottom, r.left + r.width * 0.28f, r.bottom), luzMueble(0.12f))
            } else {
                drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.45f), Color.Transparent, LLAMA.copy(alpha = 0.35f)), r.top, r.bottom), r.topLeft, r.size)
            }
        }
        drawPath(arco, t.s(0.35f), style = Stroke(1.2f * u))
        val pb = Offset(r.center.x, r.bottom - w * 0.004f)
        scale(1.2f, pb) {
            // El ibis: la peana, el cuerpo echado, la cola, el cuello y el pico curvo.
            val peana = Rect(pb.x - w * 0.042f, pb.y - w * 0.012f, pb.x + w * 0.042f, pb.y)
            contacto(Offset(pb.x + w * 0.01f, pb.y), w * 0.09f, w * 0.006f, 0.35f)
            drawRect(t.c(SICOMORO_OSC), peana.topLeft, peana.size)
            drawRect(t.b(0.45f), peana.topLeft, Size(peana.width, 1f * u))
            val fay = if (o) mezcla(t.c(FAYENZA), FAYENZA, 0.25f) else FAYENZA
            val pt = peana.top
            // El cuerpo echado: el pecho a la derecha, el lomo que baja hacia la cola, a la izquierda.
            val cuerpo = Path().apply {
                moveTo(pb.x - w * 0.03f, pt - w * 0.012f)
                cubicTo(pb.x - w * 0.022f, pt - w * 0.03f, pb.x + w * 0.004f, pt - w * 0.034f, pb.x + w * 0.016f, pt - w * 0.02f)
                cubicTo(pb.x + w * 0.022f, pt - w * 0.01f, pb.x + w * 0.016f, pt, pb.x + w * 0.006f, pt)
                lineTo(pb.x - w * 0.024f, pt)
                close()
            }
            val caja = Rect(pb.x - w * 0.034f, pt - w * 0.034f, pb.x + w * 0.022f, pt)
            drawPath(cuerpo, fay)
            clipPath(cuerpo) {
                drawRect(t.b(0.4f), caja.topLeft, Size(caja.width, caja.height * 0.32f))
                drawRect(t.s(0.3f), Offset(caja.left, caja.top + caja.height * 0.62f), Size(caja.width, caja.height))
                // El ala plegada, en un azul más hondo.
                drawArc(t.c(mezcla(FAYENZA, LAPIS, 0.55f)), 160f, 120f, false, Offset(caja.left + caja.width * 0.12f, caja.top + caja.height * 0.3f), Size(caja.width * 0.7f, caja.height * 0.9f), style = Stroke(1.1f * u))
            }
            // La cola negra, caída sobre la peana.
            drawPath(poligono(pb.x - w * 0.026f, pt - w * 0.014f, pb.x - w * 0.044f, pt - w * 0.002f, pb.x - w * 0.036f, pt, pb.x - w * 0.02f, pt - w * 0.006f), t.c(CARBON))
            // El cuello corto que se adelanta, la cabeza chica y el pico largo y curvo que baja hasta la peana.
            val cabeza = Offset(pb.x + w * 0.024f, pt - w * 0.036f)
            drawPath(Path().apply {
                moveTo(pb.x + w * 0.008f, pt - w * 0.026f)
                cubicTo(pb.x + w * 0.012f, pt - w * 0.036f, pb.x + w * 0.016f, pt - w * 0.04f, cabeza.x - w * 0.003f, cabeza.y)
            }, t.c(CARBON), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
            drawOval(t.c(CARBON), Offset(cabeza.x - w * 0.006f, cabeza.y - w * 0.005f), Size(w * 0.013f, w * 0.009f))
            drawPath(Path().apply {
                moveTo(cabeza.x + w * 0.005f, cabeza.y - w * 0.001f)
                cubicTo(cabeza.x + w * 0.02f, cabeza.y - w * 0.001f, cabeza.x + w * 0.034f, cabeza.y + w * 0.01f, cabeza.x + w * 0.036f, pt - w * 0.002f)
            }, t.c(CARBON), style = Stroke(w * 0.0025f, cap = StrokeCap.Round))
            drawCircle(t.c(CALIZA), w * 0.0014f, Offset(cabeza.x + w * 0.001f, cabeza.y - w * 0.001f))
            destello(Offset(caja.left + caja.width * 0.45f, caja.top + caja.height * 0.3f), 1.3f * u, t)
            if (o) drawCircle(LLAMA.copy(alpha = 0.5f * t.k), 1.2f * u, Offset(caja.center.x, caja.bottom - 1.5f * u))
        }
    }

    /** El hollín de la lámpara: sube en una lengua oscura hasta el poyo del nicho, y lo mancha. */
    private fun DrawScope.hollin(llama: Offset, ch: Float) {
        val w = anchoW
        val col = if (esNoche) NOCHE else CARBON
        // Una lengua que se ensancha hacia arriba, con el centro más negro.
        val lengua = Path().apply {
            moveTo(llama.x - w * 0.006f, llama.y - w * 0.02f)
            cubicTo(llama.x - w * 0.02f, llama.y - w * 0.05f, llama.x - w * 0.05f, ch * 0.7f, llama.x - w * 0.06f, ch * 0.66f)
            lineTo(llama.x + w * 0.0f, ch * 0.66f)
            cubicTo(llama.x + w * 0.005f, ch * 0.7f, llama.x + w * 0.008f, llama.y - w * 0.05f, llama.x + w * 0.004f, llama.y - w * 0.02f)
            close()
        }
        drawPath(lengua, col.copy(alpha = 0.08f))
        translate(-w * 0.012f, 0f) { scale(0.45f, 1f, Offset(llama.x, llama.y)) { drawPath(lengua, col.copy(alpha = 0.09f)) } }
        // La mancha en el poyo del nicho, encima.
        drawOval(col.copy(alpha = 0.18f), Offset(llama.x - w * 0.06f, ch * 0.64f), Size(w * 0.09f, w * 0.022f))
    }

    /** La estaca clavada en el muro, con el codo de medir y la plomada colgados, cada uno con su sombra. */
    private fun DrawScope.codo(estaca: Offset, ch: Float, hasta: Float) {
        val u = density
        val w = anchoW
        val t = tn(estaca)
        val largo = w * 0.3f
        val vara = Rect(estaca.x - w * 0.011f, estaca.y + w * 0.02f, estaca.x + w * 0.011f, estaca.y + w * 0.02f + largo)
        val bob = Offset(estaca.x - w * 0.034f, estaca.y + w * 0.2f)
        val sombra = Path().apply {
            addRect(vara)
            addOval(Rect(bob.x - w * 0.009f, bob.y - w * 0.012f, bob.x + w * 0.009f, bob.y + w * 0.014f))
        }
        sombraEnMuro(sombra, estaca, hasta, 0.2f)
        if (!esNoche) drawLine(sombraMueble(0.18f), estaca + Offset(w * 0.024f, w * 0.012f), bob + Offset(w * 0.024f, w * 0.012f), 0.8f * u)
        // La plomada: el hilo y la pesa de caliza en gota.
        drawLine(t.c(mezcla(PALMA, CARBON, 0.3f)), estaca, bob - Offset(0f, w * 0.01f), 0.9f * u)
        val pesa = Path().apply {
            moveTo(bob.x, bob.y - w * 0.014f)
            cubicTo(bob.x + w * 0.012f, bob.y - w * 0.004f, bob.x + w * 0.011f, bob.y + w * 0.012f, bob.x, bob.y + w * 0.016f)
            cubicTo(bob.x - w * 0.011f, bob.y + w * 0.012f, bob.x - w * 0.012f, bob.y - w * 0.004f, bob.x, bob.y - w * 0.014f)
            close()
        }
        drawPath(pesa, t.c(CALIZA))
        clipPath(pesa) { drawRect(t.s(0.3f), Offset(bob.x + w * 0.002f, bob.y - w * 0.02f), Size(w * 0.02f, w * 0.04f)) }
        destello(Offset(bob.x - w * 0.004f, bob.y - w * 0.002f), 1f * u, t)
        // El cordel del codo y la vara, con sus marcas de palmos y dedos.
        drawLine(t.c(mezcla(PALMA, CARBON, 0.3f)), estaca, Offset(vara.center.x - w * 0.004f, vara.top + w * 0.008f), 0.9f * u)
        drawLine(t.c(mezcla(PALMA, CARBON, 0.3f)), estaca, Offset(vara.center.x + w * 0.004f, vara.top + w * 0.008f), 0.9f * u)
        drawRect(t.c(SICOMORO_OSC), vara.topLeft, vara.size)
        clipRect(vara.left, vara.top, vara.right, vara.bottom) {
            drawRect(t.c(mezcla(SICOMORO_OSC, CARBON, 0.25f)), Offset(vara.center.x, vara.top), Size(vara.width / 2, vara.height))
            drawRect(t.b(0.35f), vara.topLeft, Size(vara.width * 0.25f, vara.height))
            for (k in 1 until 28) {
                val y = vara.top + vara.height * k / 28f
                drawLine(t.c(CALIZA).copy(alpha = 0.55f), Offset(vara.left, y), Offset(vara.left + vara.width * (if (k % 4 == 0) 0.7f else 0.35f), y), 0.6f * u)
            }
        }
        drawCircle(t.c(CARBON), 1.3f * u, Offset(vara.center.x, vara.top + w * 0.008f))
        // La estaca.
        drawCircle(t.s(0.4f), w * 0.007f, estaca + Offset(w * 0.004f, w * 0.004f))
        drawCircle(t.c(SICOMORO), w * 0.006f, estaca)
        drawCircle(t.b(0.5f), w * 0.0025f, estaca - Offset(w * 0.002f, w * 0.002f))
    }

    // ---------------------------------------------------------------- lo de encima de la viga

    /**
     * El rollo de papiro a medio desenrollar: el rollo tumbado en la viga y la hoja que sube contra el muro, un poco ladeada,
     * con sus fibras, columnas de signos y rúbricas en rojo; arriba, la punta suelta se enrolla hacia delante.
     */
    private fun DrawScope.papiro(w: Float, sup: Float, pieA: Float, pieF: Float, ch: Float) {
        val u = density
        val x0 = w * 0.235f; val x1 = w * 0.46f
        val arriba = ch * 0.17f
        val t = tn(Offset(w * 0.3f, ch * 0.62f))
        val hoja = Path().apply {
            moveTo(x0 + w * 0.012f, sup + w * 0.004f)
            lineTo(x0 + w * 0.026f, arriba + w * 0.012f)
            lineTo(x1 + w * 0.006f, arriba)
            lineTo(x1 - w * 0.004f, sup + w * 0.004f)
            close()
        }
        // La sombra de la hoja en el muro.
        sombraEnMuro(Path().apply { addPath(hoja); addOval(Rect(x0 + w * 0.02f, arriba - w * 0.022f, x1 + w * 0.01f, arriba + w * 0.012f)) }, Offset(w * 0.35f, ch * 0.5f), sup, 0.22f)
        drawPath(hoja, t.c(PAPIRO))
        clipPath(hoja) {
            // Las fibras: las de la cara a lo ancho y las de atrás a lo alto, muy tenues; una franja más clara donde se unen las hojas.
            var y = arriba; var k = 0
            while (y < sup) { drawRect(if (k % 3 == 0) t.s(0.07f) else t.b(0.1f), Offset(x0, y), Size(x1 - x0 + w * 0.02f, 0.8f * u)); y += 2.2f * u; k++ }
            drawRect(t.b(0.15f), Offset(x0 + (x1 - x0) * 0.52f, arriba), Size(w * 0.01f, sup - arriba))
            drawRect(t.s(0.1f), Offset(x0 + (x1 - x0) * 0.52f + w * 0.01f, arriba), Size(1f * u, sup - arriba))
            // La luz de arriba a la izquierda y el lado de la derecha más apagado.
            drawRect(t.s(0.12f), Offset(x1 - w * 0.04f, arriba), Size(w * 0.06f, sup - arriba))
            // Las columnas de signos, de derecha a izquierda, con su rúbrica roja arriba en algunas.
            val ink = t.c(CARBON).copy(alpha = 0.85f)
            for (cI in 0 until 5) {
                val cx = x1 - w * 0.026f - cI * w * 0.038f
                val ladeo = (cx - x0) / (x1 - x0) * w * 0.004f
                val ini = arriba + w * 0.035f - ladeo
                if (cI % 3 == 0) signos(cx, ini, ini + w * 0.03f, w * 0.014f, 5400 + cI, t.c(ROJO), 1.1f * u)
                signos(cx, ini + (if (cI % 3 == 0) w * 0.034f else 0f), sup - w * 0.03f - (if (cI == 4) w * 0.12f else cI * w * 0.012f), w * 0.0175f, 5410 + cI, ink, 1.15f * u)
            }
            // Una columna a medio escribir, que acaba antes, y una mancha de tinta.
            drawCircle(t.c(CARBON).copy(alpha = 0.3f), 2f * u, Offset(x0 + w * 0.05f, sup - w * 0.06f))
        }
        // El borde roto de la izquierda.
        drawPath(Path().apply {
            moveTo(x0 + w * 0.012f, sup)
            for (k in 1..8) lineTo(x0 + w * 0.012f + w * 0.014f * k / 8f + (if (k % 2 == 0) 1.5f else -1f) * u, sup - (sup - arriba - w * 0.012f) * k / 8f)
        }, t.s(0.3f), style = Stroke(1f * u))
        // La punta suelta, enrollada hacia delante.
        val curl = Rect(x0 + w * 0.02f, arriba - w * 0.022f, x1 + w * 0.012f, arriba + w * 0.012f)
        drawRoundRect(t.c(mezcla(PAPIRO, SICOMORO, 0.25f)), curl.topLeft, curl.size, CornerRadius(curl.height / 2))
        clipPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(curl, CornerRadius(curl.height / 2))) }) {
            drawRect(t.b(0.4f), curl.topLeft, Size(curl.width, curl.height * 0.32f))
            drawRect(t.s(0.25f), Offset(curl.left, curl.top + curl.height * 0.62f), Size(curl.width, curl.height))
        }
        drawOval(t.c(mezcla(PAPIRO, SICOMORO_OSC, 0.45f)), Offset(curl.right - curl.height * 0.7f, curl.top + curl.height * 0.08f), Size(curl.height * 0.5f, curl.height * 0.84f))
        drawArc(t.c(mezcla(PAPIRO, CARBON, 0.35f)), 0f, 300f, false, Offset(curl.right - curl.height * 0.6f, curl.top + curl.height * 0.25f), Size(curl.height * 0.3f, curl.height * 0.5f), style = Stroke(0.7f * u))
        // El tramo que cruza la viga, del muro al rollo.
        val tf = tn(Offset(w * 0.3f, pieF))
        drawPath(poligono(x0 + w * 0.012f, sup, x1 - w * 0.004f, sup, x1 + w * 0.004f, pieF - w * 0.016f, x0 + w * 0.006f, pieF - w * 0.016f), tf.c(mezcla(PAPIRO, SICOMORO, 0.15f)))
        // El rollo tumbado, delante.
        val rollo = Rect(x0 - w * 0.012f, pieF - w * 0.032f, x1 + w * 0.012f, pieF)
        contacto(Offset(rollo.center.x + w * 0.02f, pieF), rollo.width * 1.05f, w * 0.01f, 0.4f)
        drawRoundRect(tf.c(PAPIRO), rollo.topLeft, rollo.size, CornerRadius(rollo.height * 0.3f))
        clipRect(rollo.left, rollo.top, rollo.right, rollo.bottom) {
            drawRect(tf.b(0.35f), rollo.topLeft, Size(rollo.width, rollo.height * 0.3f))
            drawRect(tf.s(0.22f), Offset(rollo.left, rollo.top + rollo.height * 0.6f), Size(rollo.width, rollo.height * 0.4f))
            drawLine(tf.s(0.3f), Offset(rollo.left, rollo.top + rollo.height * 0.22f), Offset(rollo.right, rollo.top + rollo.height * 0.22f), 0.6f * u)
        }
        // La boca del rollo, con sus vueltas, y la cinta que lo ataba, suelta.
        val boca = Rect(rollo.left - rollo.height * 0.2f, rollo.top, rollo.left + rollo.height * 0.3f, rollo.bottom)
        drawOval(tf.c(mezcla(PAPIRO, SICOMORO_OSC, 0.3f)), boca.topLeft, boca.size)
        drawOval(tf.s(0.3f), Offset(boca.left + boca.width * 0.25f, boca.top + boca.height * 0.25f), Size(boca.width * 0.5f, boca.height * 0.5f), style = Stroke(0.7f * u))
        drawCircle(tf.s(0.45f), boca.height * 0.08f, boca.center)
        drawPath(Path().apply {
            moveTo(rollo.left + rollo.width * 0.3f, rollo.top)
            cubicTo(rollo.left + rollo.width * 0.32f, rollo.bottom, rollo.left + rollo.width * 0.4f, rollo.bottom + w * 0.008f, rollo.left + rollo.width * 0.5f, rollo.bottom + w * 0.006f)
        }, tf.c(ROJO), style = Stroke(1.3f * u))
        filo(rollo, tf, luzDerecha(rollo.right))
    }

    /**
     * La paleta de escriba, ladeada contra el papiro: la tablilla larga con sus dos pastillas (roja y negra), la ranura con los
     * cálamos (uno asoma), y su sombra en el muro.
     */
    private fun DrawScope.paleta(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.1f))
        val largo = w * 0.25f; val an = w * 0.036f
        val giro = 11f
        val r = Rect(pie.x - an / 2, pie.y - largo, pie.x + an / 2, pie.y)
        contacto(Offset(pie.x + w * 0.01f, pie.y), an * 1.8f, w * 0.008f, 0.4f)
        rotate(giro, pie) {
            sombraEnMuro(Path().apply { addRect(r) }, Offset(pie.x, pie.y - largo / 2), sup, 0.22f)
            drawRect(t.c(SICOMORO_LUZ), r.topLeft, r.size)
            vetas(r, (if (esNoche) NOCHE else SICOMORO_OSC).copy(alpha = 0.3f), 5500, 0.6f, vertical = true, grosor = 0.5f, ondulacion = 0.3f)
            drawRect(t.b(0.35f), r.topLeft, Size(r.width * 0.22f, r.height))
            drawRect(t.s(0.25f), Offset(r.right - r.width * 0.2f, r.top), Size(r.width * 0.2f, r.height))
            // Las dos pastillas de tinta, gastadas en el centro.
            listOf(ROJO to 0.07f, CARBON to 0.17f).forEach { (col, fy) ->
                val c = Offset(r.center.x, r.top + largo * fy)
                val rr = an * 0.3f
                drawCircle(t.s(0.35f), rr * 1.1f, c + Offset(0.6f * u, 0.6f * u))
                drawCircle(t.c(col), rr, c)
                drawCircle(t.c(mezcla(col, CALIZA, 0.18f)), rr * 0.5f, c + Offset(-rr * 0.15f, -rr * 0.15f))
                drawArc(t.s(0.4f), 200f, 140f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(0.8f * u))
            }
            // La ranura de los cálamos, con dos dentro y uno que asoma por arriba.
            val ranura = Rect(r.center.x - an * 0.12f, r.top + largo * 0.27f, r.center.x + an * 0.12f, r.bottom - largo * 0.08f)
            drawRect(t.s(0.55f), ranura.topLeft, ranura.size)
            drawLine(t.c(mezcla(PALMA, ROJO, 0.2f)), Offset(ranura.center.x, ranura.top + 2f * u), Offset(ranura.center.x, ranura.bottom - 2f * u), 1.2f * u)
            drawLine(t.c(PALMA), Offset(r.center.x + an * 0.15f, r.top + largo * 0.3f), Offset(r.center.x + an * 0.35f, r.top - largo * 0.12f), 1.3f * u, StrokeCap.Round)
            drawLine(t.c(CARBON), Offset(r.center.x + an * 0.33f, r.top - largo * 0.09f), Offset(r.center.x + an * 0.35f, r.top - largo * 0.12f), 1.3f * u, StrokeCap.Round)
            // El nombre del dueño, en signos chicos al pie, y el canto con luz.
            signos(r.center.x, r.bottom - largo * 0.065f, r.bottom - 2f * u, an * 0.32f, 5501, t.c(CARBON).copy(alpha = 0.6f), 0.6f * u)
            drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1f * u))
            filo(r, t, luzDerecha(r.right))
        }
    }

    /** El cuenco del agua de diluir la tinta: el barro con su labio, el agua dentro y su brillo. */
    private fun DrawScope.cuenco(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        val an = w * 0.04f; val al = w * 0.024f
        contacto(Offset(pie.x + w * 0.006f, pie.y), an * 2.4f, w * 0.009f, 0.4f)
        val cuerpo = Path().apply {
            moveTo(pie.x - an, pie.y - al)
            cubicTo(pie.x - an, pie.y - al * 0.2f, pie.x - an * 0.5f, pie.y, pie.x, pie.y)
            cubicTo(pie.x + an * 0.5f, pie.y, pie.x + an, pie.y - al * 0.2f, pie.x + an, pie.y - al)
            close()
        }
        drawPath(cuerpo, t.c(BARRO))
        clipPath(cuerpo) { cilindro(Rect(pie.x - an, pie.y - al, pie.x + an, pie.y), t, 1.2f) }
        val boca = Rect(pie.x - an, pie.y - al - w * 0.008f, pie.x + an, pie.y - al + w * 0.008f)
        drawOval(t.c(mezcla(BARRO, CALIZA, 0.2f)), boca.topLeft, boca.size)
        drawOval(t.c(mezcla(AZUL, CARBON, 0.55f)), Offset(boca.left + 1.6f * u, boca.top + 1.3f * u), Size(boca.width - 3.2f * u, boca.height - 2.6f * u))
        drawOval(t.b(0.5f), Offset(boca.left + boca.width * 0.25f, boca.top + boca.height * 0.3f), Size(boca.width * 0.25f, boca.height * 0.2f))
        clipPath(cuerpo) { filo(Rect(boca.left, pie.y - al, boca.right, pie.y), t, luzDerecha(pie.x)) }
    }

    /** Los óstraka: un montón de lascas de caliza con cuentas y, de pie contra el muro, una con un gato a medio dibujar. */
    private fun DrawScope.ostraka(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.04f))
        // El de pie, contra el muro.
        val grande = Rect(pie.x - w * 0.05f, pie.y - w * 0.13f, pie.x + w * 0.055f, pie.y - w * 0.006f)
        val g = contornoIrregular(grande, 5600, 14, 0.12f)
        sombraEnMuro(g, grande.center, sup, 0.22f)
        drawPath(g, t.c(CALIZA))
        clipPath(g) {
            drawRect(t.s(0.18f), Offset(grande.right - grande.width * 0.25f, grande.top), Size(grande.width * 0.25f, grande.height))
            drawRect(t.b(0.25f), grande.topLeft, Size(grande.width * 0.3f, grande.height))
            // El gato, sentado, de perfil: el boceto en rojo y la línea negra que lo corrige, a medias.
            val c = Offset(grande.center.x, grande.center.y + grande.height * 0.08f)
            val s = grande.height * 0.36f
            val boceto = Path().apply {
                moveTo(c.x - s * 0.5f, c.y + s * 0.8f)
                cubicTo(c.x - s * 0.8f, c.y + s * 0.2f, c.x - s * 0.3f, c.y - s * 0.4f, c.x + s * 0.05f, c.y - s * 0.45f)
                lineTo(c.x + s * 0.0f, c.y - s * 0.85f); lineTo(c.x + s * 0.2f, c.y - s * 0.62f)
                lineTo(c.x + s * 0.38f, c.y - s * 0.85f); lineTo(c.x + s * 0.4f, c.y - s * 0.45f)
                cubicTo(c.x + s * 0.5f, c.y - s * 0.2f, c.x + s * 0.3f, c.y + s * 0.1f, c.x + s * 0.25f, c.y + s * 0.8f)
            }
            drawPath(boceto, t.c(ROJO).copy(alpha = 0.75f), style = Stroke(0.9f * u))
            clipRect(grande.left, grande.top, grande.right, c.y + s * 0.05f) { drawPath(boceto, t.c(CARBON), style = Stroke(1.2f * u)) }
            drawPath(Path().apply { moveTo(c.x + s * 0.25f, c.y + s * 0.8f); cubicTo(c.x + s * 0.7f, c.y + s * 0.8f, c.x + s * 0.8f, c.y + s * 0.4f, c.x + s * 0.6f, c.y + s * 0.2f) }, t.c(ROJO).copy(alpha = 0.7f), style = Stroke(0.9f * u))
            drawCircle(t.c(CARBON), 0.9f * u, Offset(c.x + s * 0.28f, c.y - s * 0.5f))
        }
        drawPath(g, t.s(0.25f), style = Stroke(0.7f * u))
        // El montón, por delante: lascas tumbadas, unas sobre otras, con renglones de cuentas.
        listOf(Triple(-0.055f, 0.012f, 0.06f), Triple(-0.03f, 0.004f, 0.065f), Triple(0.005f, 0.014f, 0.055f), Triple(-0.045f, -0.008f, 0.05f)).forEachIndexed { i, (dx, dy, an) ->
            val r = Rect(pie.x + w * dx, pie.y + w * dy - w * 0.022f, pie.x + w * (dx + an), pie.y + w * dy)
            val p = contornoIrregular(r, 5610 + i, 12, 0.14f)
            contacto(Offset(r.center.x + w * 0.004f, r.bottom), r.width * 1.05f, w * 0.006f, 0.35f)
            drawPath(p, t.c(if (i % 2 == 0) CALIZA else mezcla(CALIZA, OCRE, 0.18f)))
            clipPath(p) {
                drawRect(t.b(0.35f), r.topLeft, Size(r.width, r.height * 0.4f))
                drawRect(t.s(0.25f), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height))
                signos(r.top + r.height * 0.35f, r.left + r.width * 0.15f, r.right - r.width * 0.2f, w * 0.007f, 5620 + i, t.c(CARBON).copy(alpha = 0.7f), 0.6f * u, horizontal = true)
            }
        }
    }

    /** La jarra de cerveza, alta: el barro basto, el tapón de barro con la marca de los dedos y el cordel atado al cuello. */
    private fun DrawScope.jarra(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.08f))
        val an = w * 0.052f; val al = w * 0.19f
        val top = pie.y - al
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.36f, top + al * 0.1f)
            cubicTo(pie.x - an * 1.05f, top + al * 0.25f, pie.x - an * 1.1f, top + al * 0.7f, pie.x - an * 0.4f, pie.y)
            lineTo(pie.x + an * 0.4f, pie.y)
            cubicTo(pie.x + an * 1.1f, top + al * 0.7f, pie.x + an * 1.05f, top + al * 0.25f, pie.x + an * 0.36f, top + al * 0.1f)
            close()
        }
        val tapon = Path().apply {
            moveTo(pie.x - an * 0.46f, top + al * 0.06f)
            cubicTo(pie.x - an * 0.5f, top - al * 0.08f, pie.x + an * 0.5f, top - al * 0.1f, pie.x + an * 0.48f, top + al * 0.06f)
            close()
        }
        sombraEnMuro(Path().apply { addPath(cuerpo); addPath(tapon) }, Offset(pie.x, pie.y - al / 2), sup, 0.22f)
        contacto(Offset(pie.x + w * 0.01f, pie.y), an * 2.2f, w * 0.01f, 0.42f)
        drawPath(cuerpo, t.c(mezcla(BARRO, ADOBE, 0.35f)))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - an * 1.1f, top, pie.x + an * 1.1f, pie.y), t, 1.3f, luzDerecha(pie.x))
            // Las marcas del torno y una chorrera vieja.
            for (k in 0 until 6) drawLine(t.s(0.12f), Offset(pie.x - an * 1.1f, top + al * (0.3f + 0.1f * k)), Offset(pie.x + an * 1.1f, top + al * (0.3f + 0.1f * k)), 0.7f * u)
            drawRect(t.c(mezcla(BARRO, NOCHE, 0.3f)).copy(alpha = 0.4f), Offset(pie.x + an * 0.15f, top + al * 0.12f), Size(an * 0.18f, al * 0.4f))
        }
        // El cordel del cuello.
        listOf(0.11f, 0.14f).forEach { fy -> drawLine(t.c(mezcla(PALMA, ADOBE, 0.3f)), Offset(pie.x - an * 0.45f, top + al * fy), Offset(pie.x + an * 0.45f, top + al * (fy + 0.01f)), 1.2f * u) }
        drawLine(t.c(mezcla(PALMA, ADOBE, 0.3f)), Offset(pie.x + an * 0.4f, top + al * 0.13f), Offset(pie.x + an * 0.55f, top + al * 0.24f), 1f * u)
        // El tapón de barro gris, con las huellas de los dedos.
        drawPath(tapon, t.c(mezcla(ADOBE, CAL_OSC, 0.35f)))
        clipPath(tapon) {
            drawRect(t.b(0.3f), Offset(pie.x - an * 0.5f, top - al * 0.1f), Size(an * 0.4f, al * 0.2f))
            drawRect(t.s(0.25f), Offset(pie.x + an * 0.15f, top - al * 0.1f), Size(an * 0.4f, al * 0.2f))
            listOf(-0.18f, 0.12f).forEach { fx -> drawOval(t.s(0.3f), Offset(pie.x + an * fx, top - al * 0.02f), Size(an * 0.22f, al * 0.04f)) }
        }
        clipPath(cuerpo) { filo(Rect(pie.x - an * 1.1f, top + al * 0.1f, pie.x + an * 1.1f, pie.y), t, luzDerecha(pie.x)) }
    }

    /** La lámpara de cuenco: el soporte de barro, el cuenco con el pico pellizcado, la mecha y, de noche, su llama. */
    private fun DrawScope.lampara(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        val o = esNoche
        contacto(Offset(pie.x + w * 0.008f, pie.y), w * 0.07f, w * 0.009f, 0.42f)
        // El soporte: un anillo de barro.
        val so = Rect(pie.x - w * 0.022f, pie.y - w * 0.016f, pie.x + w * 0.022f, pie.y)
        drawPath(poligono(so.left, so.bottom, so.left + w * 0.006f, so.top, so.right - w * 0.006f, so.top, so.right, so.bottom), t.c(BARRO))
        clipRect(so.left, so.top, so.right, so.bottom) { cilindro(so, t, 1.3f, luzDerecha(pie.x)) }
        // El cuenco, con el pico a la izquierda.
        val cy = so.top
        val an = w * 0.04f; val al = w * 0.016f
        val cuenco = Path().apply {
            moveTo(pie.x + an + w * 0.012f, cy - al - w * 0.002f)
            lineTo(pie.x - an, cy - al)
            cubicTo(pie.x - an, cy - al * 0.2f, pie.x - an * 0.5f, cy, pie.x, cy)
            cubicTo(pie.x + an * 0.5f, cy, pie.x + an, cy - al * 0.3f, pie.x + an + w * 0.012f, cy - al - w * 0.002f)
            close()
        }
        drawPath(cuenco, t.c(BARRO))
        clipPath(cuenco) { cilindro(Rect(pie.x - an, cy - al, pie.x + an, cy), t, 1.3f) }
        drawOval(t.c(mezcla(BARRO, CARBON, 0.45f)), Offset(pie.x - an, cy - al - w * 0.005f), Size(an * 2, w * 0.01f))
        drawOval(t.c(mezcla(OCRE, CARBON, 0.4f)), Offset(pie.x - an * 0.8f, cy - al - w * 0.003f), Size(an * 1.6f, w * 0.006f))
        drawArc(t.b(0.6f), 180f, 120f, false, Offset(pie.x - an, cy - al - w * 0.005f), Size(an * 2, w * 0.01f), style = Stroke(0.9f * u))
        // La mecha en el pico, quemada en la punta.
        val m = Offset(pie.x + an + w * 0.008f, cy - al - w * 0.004f)
        drawLine(t.c(CALIZA), Offset(m.x - w * 0.01f, m.y + w * 0.002f), m, 1.6f * u, StrokeCap.Round)
        drawCircle(t.c(CARBON), 1.2f * u, m)
        // La llama, de noche, pequeña: el halo, el cuerpo y el corazón blanco.
        if (o) {
            val lc = Offset(LUZ.x * w, LUZ.y * w)
            drawCircle(LLAMA.copy(alpha = 0.3f), w * 0.024f, lc)
            drawPath(Path().apply {
                moveTo(lc.x, lc.y - w * 0.022f)
                cubicTo(lc.x + w * 0.009f, lc.y - w * 0.006f, lc.x + w * 0.008f, lc.y + w * 0.01f, lc.x, lc.y + w * 0.012f)
                cubicTo(lc.x - w * 0.008f, lc.y + w * 0.01f, lc.x - w * 0.009f, lc.y - w * 0.006f, lc.x, lc.y - w * 0.022f)
                close()
            }, LLAMA)
            drawOval(Color(0xFFFFF4D8), Offset(lc.x - w * 0.0035f, lc.y - w * 0.004f), Size(w * 0.007f, w * 0.013f))
        }
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Un número que mezcla bien los bits de la semilla (los `hashCode` de temas parecidos se parecen). */
    private fun mezclar(s: Int): Int {
        var x = s * -0x61c88647
        x = x xor (x ushr 15)
        x *= 0x2c1b3c6d
        return x xor (x ushr 12)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega de la lámpara.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.08f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        fondoHueco(w, h, t, der)
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> cuencoOstraka(w, h, c, t, der)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> cofre(w, h, c, m, t, der, ab)
                    1 -> cesto(w, h, c, m, t, der, ab)
                    else -> vasija(w, h, c, m, t, der, ab)
                }
            }
        }
    }

    /**
     * El fondo de un hueco: el enlucido hondo del fondo de la alacena, la sombra de la balda de arriba (en diagonal: la luz
     * baja de la izquierda), la jamba de la izquierda en sombra, el costado de la derecha con luz y la balda con su cara de
     * arriba. De día, la fila de arriba un poco más clara; de noche, la lámpara según lo cerca que esté.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono, der: Boolean) {
        val u = density
        drawRect(t.c(mezcla(CAL_OSC, ADOBE, 0.22f)), Offset.Zero, Size(w, h))
        pinceladas(Rect(0f, 0f, w, h), if (t.o) NOCHE else mezcla(CAL_OSC, ADOBE, 0.5f), 5700, 7, u, 0.14f)
        // La mancha de luz que entra en diagonal y llega al fondo, abajo a la derecha.
        if (!t.o) drawPath(poligono(w * 0.3f, h * 0.36f, w, h * 0.22f, w, h, w * 0.14f, h), luzMueble(0.14f))
        else drawRect(LLAMA.copy(alpha = (0.12f * t.k).coerceAtMost(0.1f)), if (der) Offset(w * 0.5f, 0f) else Offset.Zero, Size(w * 0.5f, h))
        // La sombra de la balda de arriba, más honda a la izquierda, en escalones.
        drawPath(poligono(0f, 0f, w, 0f, w, h * 0.16f, 0f, h * 0.34f), t.s(0.2f))
        drawPath(poligono(0f, 0f, w, 0f, w, h * 0.07f, 0f, h * 0.16f), t.s(0.2f))
        // La jamba de la izquierda en sombra y el costado de la derecha con luz.
        drawPath(poligono(0f, 0f, w * 0.12f, 0f, w * 0.07f, h, 0f, h), t.s(0.24f))
        drawRect(t.s(0.15f), Offset.Zero, Size(w * 0.025f, h))
        drawRect(t.b(0.25f), Offset(w - w * 0.018f, 0f), Size(w * 0.018f, h))
        // Una grieta del enlucido del fondo.
        drawPath(Path().apply { moveTo(w * 0.62f, h * 0.18f); lineTo(w * 0.66f, h * 0.3f); lineTo(w * 0.64f, h * 0.38f); lineTo(w * 0.69f, h * 0.5f) }, t.s(0.25f), style = Stroke(0.7f * u))
        // La balda: su cara de arriba, vista un poco desde arriba, con el rincón del fondo en sombra.
        val piso = h * PISO
        drawRect(t.c(mezcla(SICOMORO, SICOMORO_LUZ, 0.45f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.s(0.35f), Offset(0f, piso), Size(w, 1.4f * u))
        drawRect(t.b(0.2f), Offset(0f, piso + (h - piso) * 0.5f), Size(w, 0.8f * u))
    }

    /** El rótulo de cada recipiente: el fondo claro (cerca de la lámpara, encendido; lejos, hundido sin perder contraste). */
    private fun claroRotulo(t: Tono, base: Color): Color =
        if (t.o) mezcla(mezcla(base, CALIZA_LEJOS, 0.9f), mezcla(base, LLAMA, 0.12f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(base)

    private fun rectRotulo(w: Float, h: Float) = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)

    /** Las tablillas que asoman por la boca de [x0]..[x1], desde [boca]: más y más altas cuanto más guarda. */
    private fun DrawScope.tablillas(x0: Float, x1: Float, boca: Float, h: Float, c: EstadoCajon, t: Tono, alzado: Float = 0f) {
        val n = c.n.coerceIn(0, 10)
        val cuantas = (1 + (n + 1) / 2).coerceIn(1, 6)
        val sube = h * (0.05f + 0.016f * n) + alzado
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val tw = (x1 - x0) * (0.12f + 0.06f * azar(c.semilla, i + 20))
            val x = x0 + (x1 - x0 - tw) * (f * 0.92f + 0.04f * azar(c.semilla, i + 30))
            val y = boca - sube * (0.5f + 0.5f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 10f
            rotate(g, Offset(x + tw / 2, boca)) { tablilla(Rect(x, y, x + tw, boca + h * 0.02f), t, i + c.semilla) }
        }
    }

    /**
     * El cofre con tapa de capilla: la caja (de cedro, de sicomoro o pintada de blanco con sus fajas) sobre cuatro patas cortas,
     * la tapa abombada con su cornisa alzada sobre las tablillas, las dos perillas con el hilo y el sello de barro, y el
     * cartucho de yeso con el tema.
     */
    private fun DrawScope.cofre(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 3, 3)
        val madera = when (clase) { 0 -> CEDRO; 1 -> SICOMORO; else -> mezcla(CAL, OCRE, 0.12f) }
        val canto = when (clase) { 0 -> CEDRO_OSC; 1 -> SICOMORO_OSC; else -> ROJO }
        val caja = Rect(w * 0.04f, h * 0.4f, w * 0.96f, piso - h * 0.035f)
        // La sombra en el fondo y las patas.
        drawRect(t.s(0.3f), Offset(caja.left + w * 0.035f, caja.top + h * 0.03f), Size(caja.width, caja.height + h * 0.03f))
        contacto(Offset(caja.center.x + w * 0.04f, piso + h * 0.03f), caja.width * 1.05f, h * 0.05f, 0.42f)
        listOf(caja.left + w * 0.02f, caja.right - w * 0.07f).forEach { x ->
            drawRect(t.c(if (clase == 2) SICOMORO_OSC else canto), Offset(x, caja.bottom), Size(w * 0.05f, piso + h * 0.03f - caja.bottom))
            drawRect(t.b(0.3f), Offset(x, caja.bottom), Size(w * 0.012f, piso + h * 0.03f - caja.bottom))
        }
        // Las tablillas, entre la caja y la tapa alzada.
        val alza = h * (0.025f + 0.013f * c.n.coerceIn(0, 8)) + ab * h * 0.08f
        val lb = caja.top - alza
        drawRect(t.c(CARBON).copy(alpha = 0.85f), Offset(caja.left + w * 0.02f, lb), Size(caja.width - w * 0.04f, alza + 2f * u))
        tablillas(caja.left + w * 0.05f, caja.right - w * 0.05f, caja.top + h * 0.01f, h, c, t, alza * 0.4f)
        // La caja.
        drawRect(t.c(madera), caja.topLeft, caja.size)
        if (clase < 2) vetas(caja, (if (t.o) NOCHE else CEDRO_OSC).copy(alpha = 0.3f), c.semilla, 0.8f, grosor = 0.6f, ondulacion = 0.4f)
        else {
            // Pintado: las fajas de rojo y azul en el borde y una línea de signos en la de arriba.
            pinceladas(caja, if (t.o) NOCHE else CAL_OSC, c.semilla, 6, u, 0.12f)
            drawRect(t.c(ROJO), Offset(caja.left, caja.top + h * 0.03f), Size(caja.width, h * 0.022f))
            drawRect(t.c(AZUL), Offset(caja.left, caja.bottom - h * 0.05f), Size(caja.width, h * 0.022f))
            listOf(caja.left + w * 0.015f, caja.right - w * 0.035f).forEach { x -> drawRect(t.c(ROJO), Offset(x, caja.top), Size(w * 0.02f, caja.height)) }
        }
        if (der) drawRect(t.b(0.2f), Offset(caja.right - caja.width * 0.18f, caja.top), Size(caja.width * 0.18f, caja.height))
        else { drawRect(t.b(0.18f), caja.topLeft, Size(caja.width * 0.2f, caja.height)); drawRect(t.s(0.22f), Offset(caja.right - caja.width * 0.14f, caja.top), Size(caja.width * 0.14f, caja.height)) }
        drawRect(t.s(0.3f), Offset(caja.left, caja.bottom - 1.6f * u), Size(caja.width, 1.6f * u))
        drawRect(t.c(canto), caja.topLeft, Size(caja.width, h * 0.022f))
        drawRect(t.b(0.45f), caja.topLeft, Size(caja.width, 1f * u))
        // Un golpe en la esquina, donde asoma la madera clara.
        drawPath(poligono(caja.left, caja.bottom - h * 0.12f, caja.left + w * 0.03f, caja.bottom, caja.left, caja.bottom), t.c(mezcla(madera, CALIZA, 0.35f)))
        // La tapa de capilla: la cornisa y el lomo abombado que sube hacia atrás.
        val ancho = caja.width + w * 0.03f
        val tapa = Path().apply {
            moveTo(caja.left - w * 0.015f, lb)
            lineTo(caja.right + w * 0.015f, lb)
            lineTo(caja.right + w * 0.01f, lb - h * 0.045f)
            cubicTo(caja.right - ancho * 0.08f, lb - h * 0.17f, caja.left + ancho * 0.08f, lb - h * 0.17f, caja.left - w * 0.01f, lb - h * 0.045f)
            close()
        }
        drawPath(tapa, t.c(madera))
        clipPath(tapa) {
            if (clase < 2) vetas(Rect(caja.left, lb - h * 0.2f, caja.right, lb), (if (t.o) NOCHE else CEDRO_OSC).copy(alpha = 0.3f), c.semilla + 3, 0.7f, grosor = 0.5f)
            drawRect(t.b(0.3f), Offset(caja.left - w * 0.02f, lb - h * 0.2f), Size(caja.width + w * 0.04f, h * 0.075f))
            drawRect(t.s(0.25f), Offset(caja.right - caja.width * 0.18f, lb - h * 0.2f), Size(caja.width * 0.2f, h * 0.2f))
            drawRect(t.c(canto), Offset(caja.left - w * 0.02f, lb - h * 0.045f), Size(caja.width + w * 0.04f, h * 0.045f))
            drawRect(t.b(0.45f), Offset(caja.left - w * 0.02f, lb - h * 0.045f), Size(caja.width + w * 0.04f, 1f * u))
        }
        drawRect(t.s(0.35f), Offset(caja.left - w * 0.015f, lb), Size(caja.width + w * 0.03f, 1.6f * u))
        // Las perillas, el hilo enrollado entre ellas y el sello de barro.
        val p1 = Offset(w * 0.5f, lb - h * 0.075f)
        val p2 = Offset(w * 0.5f, caja.top + h * 0.065f)
        listOf(p1, p2).forEach { p ->
            drawCircle(t.s(0.35f), w * 0.017f, p + Offset(1.2f * u, 1.4f * u))
            drawCircle(t.c(if (clase == 2) SICOMORO_OSC else mezcla(canto, CARBON, 0.3f)), w * 0.016f, p)
            drawCircle(t.b(0.5f), w * 0.006f, p - Offset(w * 0.005f, w * 0.005f))
        }
        val hilo = t.c(mezcla(LINO, PALMA, 0.2f))
        listOf(-1f, 1f).forEach { s -> drawLine(hilo, Offset(p1.x + s * w * 0.012f, p1.y), Offset(p2.x - s * w * 0.012f, p2.y), 1.1f * u) }
        drawLine(hilo, Offset(p1.x - w * 0.014f, p1.y + 1f * u), Offset(p1.x + w * 0.014f, p1.y + 1f * u), 1.1f * u)
        val sello = Rect(p2.x + w * 0.02f, p2.y - h * 0.035f, p2.x + w * 0.075f, p2.y + h * 0.03f)
        drawOval(t.s(0.3f), sello.topLeft + Offset(1f * u, 1.4f * u), sello.size)
        drawOval(t.c(mezcla(ADOBE, CAL_OSC, 0.2f)), sello.topLeft, sello.size)
        drawOval(t.s(0.3f), Offset(sello.left + sello.width * 0.25f, sello.top + sello.height * 0.25f), Size(sello.width * 0.5f, sello.height * 0.5f), style = Stroke(0.7f * u))
        drawLine(hilo, Offset(p2.x + w * 0.012f, p2.y), Offset(sello.left + 1f * u, sello.center.y), 1f * u)
        drawOval(t.b(0.4f), Offset(sello.left + sello.width * 0.15f, sello.top + sello.height * 0.1f), Size(sello.width * 0.3f, sello.height * 0.2f))
        filo(caja, t, der)
        // El cartucho: el óvalo de yeso con su cuerda roja y la barra al final.
        val r = rectRotulo(w, h)
        val cr = CornerRadius(r.height / 2)
        drawRoundRect(t.s(0.28f), r.topLeft + Offset(1.2f * u, 1.6f * u), r.size, cr)
        drawRoundRect(claroRotulo(t, CALIZA), r.topLeft, r.size, cr)
        drawRoundRect(t.c(if (clase == 1) ROJO else CARBON).copy(alpha = 0.8f), r.topLeft, r.size, cr, style = Stroke(1.6f * u))
        drawRoundRect(t.b(0.4f), r.topLeft + Offset(2f * u, 1.5f * u), Size(r.width - 4f * u, r.height * 0.3f), CornerRadius(r.height * 0.15f))
        drawLine(t.c(if (clase == 1) ROJO else CARBON), Offset(r.right - r.height * 0.18f, r.top + r.height * 0.2f), Offset(r.right - r.height * 0.18f, r.bottom - r.height * 0.2f), 1.8f * u)
    }

    /**
     * El cesto de palma cosido en espiral: el cuerpo un poco más angosto abajo, las vueltas con sus puntadas, una o dos franjas
     * teñidas (según la semilla), la tapa cónica alzada sobre las tablillas, y la etiqueta de madera atada con su cordel.
     */
    private fun DrawScope.cesto(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 3, 4)
        val palma = when (clase) { 0 -> PALMA; 1 -> mezcla(PALMA, ADOBE, 0.35f); 2 -> mezcla(PALMA, CALIZA, 0.3f); else -> mezcla(PALMA, Color(0xFF8E9A5A), 0.35f) }
        val tinte = if (clase % 2 == 0) ROJO else CARBON
        val top = h * 0.4f
        val forma = poligono(w * 0.05f, top, w * 0.95f, top, w * 0.9f, piso, w * 0.1f, piso)
        translate(w * 0.035f, h * 0.03f) { drawPath(forma, t.s(0.26f)) }
        contacto(Offset(w * 0.54f, piso), w * 0.9f, h * 0.05f, 0.42f)
        // Las tablillas, bajo la tapa alzada.
        val alza = h * (0.02f + 0.012f * c.n.coerceIn(0, 8)) + ab * h * 0.08f
        val lb = top - alza
        drawRect(t.c(mezcla(CARBON, ADOBE, 0.3f)), Offset(w * 0.07f, lb), Size(w * 0.86f, alza + 2f * u))
        tablillas(w * 0.12f, w * 0.88f, top + h * 0.01f, h, c, t, alza * 0.4f)
        // El cuerpo y sus vueltas.
        drawPath(forma, t.c(palma))
        clipPath(forma) {
            val paso = h * 0.042f
            var y = top; var k = 0
            while (y < piso) {
                val franja = (k == 3 || (clase >= 2 && k == 5))
                if (franja) drawRect(t.c(tinte), Offset(0f, y), Size(w, paso))
                drawRect(t.b(0.22f), Offset(0f, y), Size(w, paso * 0.3f))
                drawRect(t.s(0.22f), Offset(0f, y + paso - 1.3f * u), Size(w, 1.3f * u))
                // Las puntadas, cada tanto y alternadas.
                var x = w * (0.06f + (if (k % 2 == 0) 0f else 0.03f))
                while (x < w * 0.94f) { drawLine(t.s(0.2f), Offset(x, y + paso * 0.15f), Offset(x + w * 0.012f, y + paso * 0.85f), 0.8f * u); x += w * 0.06f }
                y += paso; k++
            }
            if (der) drawRect(t.b(0.2f), Offset(w * 0.78f, top), Size(w * 0.22f, piso - top))
            else { drawRect(t.b(0.16f), Offset(0f, top), Size(w * 0.22f, piso - top)); drawRect(t.s(0.25f), Offset(w * 0.78f, top), Size(w * 0.22f, piso - top)) }
        }
        // Un roto en el borde, con una hebra suelta.
        drawLine(t.c(palma), Offset(w * 0.86f, top + h * 0.1f), Offset(w * 0.93f, top + h * 0.06f), 1f * u)
        // La tapa cónica, de vueltas más chicas, con su asa de cordel arriba.
        val tapa = Path().apply {
            moveTo(w * 0.03f, lb)
            lineTo(w * 0.97f, lb)
            cubicTo(w * 0.94f, lb - h * 0.1f, w * 0.68f, lb - h * 0.2f, w * 0.53f, lb - h * 0.21f)
            lineTo(w * 0.47f, lb - h * 0.21f)
            cubicTo(w * 0.32f, lb - h * 0.2f, w * 0.06f, lb - h * 0.1f, w * 0.03f, lb)
            close()
        }
        drawPath(tapa, t.c(mezcla(palma, LUZ_MUEBLE, if (t.o) 0f else 0.08f)))
        clipPath(tapa) {
            for (k in 0 until 6) {
                val y = lb - h * 0.2f * (k / 6f)
                drawRect(t.s(0.2f), Offset(0f, y - 1.2f * u), Size(w, 1.2f * u))
                if (k == 2) drawRect(t.c(tinte), Offset(0f, y - h * 0.032f), Size(w, h * 0.024f))
            }
            drawRect(t.b(0.25f), Offset(0f, lb - h * 0.2f), Size(w * 0.45f, h * 0.2f))
            drawRect(t.s(0.22f), Offset(w * 0.62f, lb - h * 0.2f), Size(w * 0.38f, h * 0.2f))
        }
        drawRect(t.s(0.35f), Offset(w * 0.03f, lb), Size(w * 0.94f, 1.5f * u))
        drawArc(t.c(mezcla(ADOBE, PALMA, 0.4f)), 180f, 180f, false, Offset(w * 0.47f, lb - h * 0.25f), Size(w * 0.06f, h * 0.1f), style = Stroke(1.6f * u))
        filo(Rect(w * 0.05f, top, w * 0.95f, piso), t, der)
        // La etiqueta de madera, atada con un cordel a la tapa.
        val r = rectRotulo(w, h)
        val cordel = t.c(mezcla(ADOBE, PALMA, 0.3f))
        drawLine(cordel, Offset(r.left + r.width * 0.12f, r.top + 3f * u), Offset(w * 0.3f, lb - h * 0.02f), 1f * u)
        drawLine(cordel, Offset(r.right - r.width * 0.12f, r.top + 3f * u), Offset(w * 0.7f, lb - h * 0.02f), 1f * u)
        drawRect(t.s(0.3f), r.topLeft + Offset(1.4f * u, 1.8f * u), r.size)
        drawRect(claroRotulo(t, mezcla(SICOMORO_LUZ, CALIZA, 0.62f)), r.topLeft, r.size)
        vetas(r, (if (t.o) NOCHE else SICOMORO).copy(alpha = 0.18f), c.semilla + 9, 0.4f, grosor = 0.5f, ondulacion = 0.3f)
        drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.s(0.25f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        drawRect(t.s(0.12f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        listOf(r.left + r.width * 0.12f, r.right - r.width * 0.12f).forEach { x -> drawCircle(t.s(0.6f), 1.5f * u, Offset(x, r.top + 3.5f * u)) }
    }

    /**
     * La vasija ancha: el barro (rojo bruñido, marga con su banda azul o encalada, según la semilla) con el lado de luz y su
     * brillo, el hierático inventado en el hombro, el tapón de barro ladeado en la boca, las tablillas que asoman y el
     * óstrakon atado al cuello como etiqueta.
     */
    private fun DrawScope.vasija(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 3, 3)
        val barro = when (clase) { 0 -> BARRO; 1 -> MARGA; else -> mezcla(CAL, MARGA, 0.2f) }
        val cx = w * 0.5f
        val boca = h * 0.24f
        val cuerpo = Path().apply {
            moveTo(cx - w * 0.16f, boca)
            lineTo(cx - w * 0.15f, h * 0.31f)
            cubicTo(cx - w * 0.47f, h * 0.36f, cx - w * 0.5f, h * 0.7f, cx - w * 0.22f, piso - h * 0.01f)
            lineTo(cx + w * 0.22f, piso - h * 0.01f)
            cubicTo(cx + w * 0.5f, h * 0.7f, cx + w * 0.47f, h * 0.36f, cx + w * 0.15f, h * 0.31f)
            lineTo(cx + w * 0.16f, boca)
            close()
        }
        translate(w * 0.035f, h * 0.03f) { drawPath(cuerpo, t.s(0.26f)) }
        contacto(Offset(cx + w * 0.04f, piso), w * 0.7f, h * 0.05f, 0.45f)
        // Las tablillas que asoman de la boca, detrás del tapón.
        tablillas(cx - w * 0.15f, cx + w * 0.15f, boca + h * 0.02f, h, c, t, ab * h * 0.04f)
        drawPath(cuerpo, t.c(barro))
        clipPath(cuerpo) {
            cilindro(Rect(cx - w * 0.47f, boca, cx + w * 0.47f, piso), t, 1.25f, der)
            drawRect(t.s(0.18f), Offset(0f, h * 0.76f), Size(w, h * 0.2f))
            if (clase == 1) {
                // La banda azul con pétalos, pintada a pincel.
                drawRect(t.c(AZUL), Offset(0f, h * 0.43f), Size(w, h * 0.035f))
                var x = cx - w * 0.44f
                while (x < cx + w * 0.44f) { drawOval(t.c(AZUL), Offset(x, h * 0.465f), Size(w * 0.03f, h * 0.06f)); x += w * 0.05f }
                drawRect(t.c(ROJO), Offset(0f, h * 0.425f), Size(w, h * 0.008f))
            }
            if (clase == 2) pinceladas(Rect(0f, boca, w, piso), if (t.o) NOCHE else CAL_OSC, c.semilla, 8, u, 0.14f)
            // El hierático del hombro: el tema escrito a pincel, inventado.
            signos(h * 0.375f, cx - w * 0.27f, cx + w * 0.27f, w * 0.032f, c.semilla, t.c(CARBON).copy(alpha = 0.8f), 1.1f * u, horizontal = true)
            if (clase == 0) drawOval(t.b(0.5f), Offset(cx - w * 0.33f, h * 0.48f), Size(w * 0.05f, h * 0.18f))
        }
        // El cuello con su labio.
        drawRect(t.c(mezcla(barro, CALIZA, 0.15f)), Offset(cx - w * 0.17f, boca - h * 0.015f), Size(w * 0.34f, h * 0.03f))
        drawRect(t.b(0.45f), Offset(cx - w * 0.17f, boca - h * 0.015f), Size(w * 0.34f, 1f * u))
        // El tapón de barro, ladeado sobre el labio (y más alzado al abrir).
        val tp = Offset(cx - w * 0.07f, boca - h * 0.03f - ab * h * 0.05f)
        rotate(-14f, tp) {
            val tapon = Path().apply {
                moveTo(tp.x - w * 0.12f, tp.y)
                cubicTo(tp.x - w * 0.12f, tp.y - h * 0.13f, tp.x + w * 0.12f, tp.y - h * 0.13f, tp.x + w * 0.12f, tp.y)
                close()
            }
            drawPath(tapon, t.s(0.3f))
            translate(-1.5f * u, -1.5f * u) {
                drawPath(tapon, t.c(mezcla(ADOBE, CAL_OSC, 0.3f)))
                clipPath(tapon) {
                    drawRect(t.b(0.3f), Offset(tp.x - w * 0.12f, tp.y - h * 0.13f), Size(w * 0.09f, h * 0.13f))
                    drawRect(t.s(0.22f), Offset(tp.x + w * 0.04f, tp.y - h * 0.13f), Size(w * 0.08f, h * 0.13f))
                    drawOval(t.s(0.3f), Offset(tp.x - w * 0.04f, tp.y - h * 0.08f), Size(w * 0.06f, h * 0.04f))
                }
            }
        }
        filo(Rect(cx - w * 0.45f, h * 0.33f, cx + w * 0.45f, piso), t, der)
        // El óstrakon atado al cuello, colgado sobre la panza.
        val r = rectRotulo(w, h)
        val cordel = t.c(mezcla(ADOBE, PALMA, 0.3f))
        drawLine(cordel, Offset(cx - w * 0.15f, h * 0.3f), Offset(r.left + r.width * 0.2f, r.top + 2f * u), 1f * u)
        drawLine(cordel, Offset(cx + w * 0.15f, h * 0.3f), Offset(r.right - r.width * 0.2f, r.top + 2f * u), 1f * u)
        drawLine(cordel, Offset(cx - w * 0.16f, h * 0.3f), Offset(cx + w * 0.16f, h * 0.31f), 1.2f * u)
        val lasca = contornoIrregular(r, c.semilla + 77, 16, 0.05f)
        translate(1.4f * u, 1.8f * u) { drawPath(lasca, t.s(0.3f)) }
        drawPath(lasca, claroRotulo(t, CALIZA))
        drawPath(lasca, t.s(0.25f), style = Stroke(0.8f * u))
        clipPath(lasca) { drawRect(t.b(0.35f), r.topLeft, Size(r.width, 1.2f * u)) }
    }

    /** Lo que no tiene tema: un cuenco ancho de barro lleno de óstraka sueltos, y uno grande, en blanco, apoyado delante. */
    private fun DrawScope.cuencoOstraka(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val boca = h * 0.46f
        val cuerpo = Path().apply {
            moveTo(w * 0.04f, boca)
            cubicTo(w * 0.05f, h * 0.75f, w * 0.25f, piso, w * 0.5f, piso)
            cubicTo(w * 0.75f, piso, w * 0.95f, h * 0.75f, w * 0.96f, boca)
            close()
        }
        translate(w * 0.035f, h * 0.025f) { drawPath(cuerpo, t.s(0.26f)) }
        contacto(Offset(w * 0.54f, piso), w * 0.6f, h * 0.05f, 0.45f)
        // Los óstraka amontonados, que asoman por encima del labio.
        for (i in 0 until 7) {
            val cx = w * (0.14f + 0.72f * i / 6f + 0.04f * (azar(c.semilla, i) - 0.5f))
            val cy = boca - h * (0.05f + 0.16f * azar(c.semilla, i + 10))
            val r = Rect(cx - w * 0.08f, cy - h * 0.08f, cx + w * 0.08f, cy + h * 0.1f)
            rotate((azar(c.semilla, i + 20) - 0.5f) * 50f, r.center) {
                val p = contornoIrregular(r, c.semilla + i, 12, 0.14f)
                drawPath(p, t.c(if (i % 3 == 0) mezcla(CALIZA, OCRE, 0.2f) else CALIZA))
                clipPath(p) {
                    drawRect(t.s(0.22f), Offset(r.center.x + r.width * 0.15f, r.top), Size(r.width, r.height))
                    signos(r.top + r.height * 0.35f, r.left + r.width * 0.2f, r.right - r.width * 0.2f, w * 0.022f, c.semilla + i, t.c(if (i % 2 == 0) CARBON else ROJO).copy(alpha = 0.7f), 0.8f * u, horizontal = true)
                }
                drawPath(p, t.s(0.25f), style = Stroke(0.7f * u))
            }
        }
        drawPath(cuerpo, t.c(BARRO))
        clipPath(cuerpo) {
            cilindro(Rect(w * 0.04f, boca, w * 0.96f, piso), t, 1.2f, der)
            drawRect(t.c(CARBON).copy(alpha = 0.6f), Offset(0f, boca + h * 0.03f), Size(w, h * 0.012f))
        }
        drawOval(t.c(mezcla(BARRO, CALIZA, 0.2f)), Offset(w * 0.04f, boca - h * 0.02f), Size(w * 0.92f, h * 0.04f), style = Stroke(1.8f * u))
        filo(Rect(w * 0.04f, boca, w * 0.96f, piso), t, der)
        // La lasca grande, apoyada delante (el rótulo).
        val r = rectRotulo(w, h)
        val lasca = contornoIrregular(r, c.semilla + 91, 16, 0.05f)
        translate(1.4f * u, 1.8f * u) { drawPath(lasca, t.s(0.3f)) }
        drawPath(lasca, claroRotulo(t, CALIZA))
        drawPath(lasca, t.s(0.25f), style = Stroke(0.8f * u))
        clipPath(lasca) { drawRect(t.b(0.35f), r.topLeft, Size(r.width, 1.2f * u)) }
    }

    /** Un hueco libre: un óstrakon apoyado con un dibujo a medio hacer (la cuadrícula roja y una cabeza a medias) o un escarabajo de fayenza junto a dos lascas en blanco y un cálamo. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        if (Math.floorMod(mezclar(c.semilla), 2) == 0) {
            val r = Rect(w * 0.26f, h * 0.24f, w * 0.72f, piso + h * 0.02f)
            val p = contornoIrregular(r, c.semilla, 16, 0.08f)
            rotate(-6f, Offset(r.center.x, r.bottom)) {
                translate(w * 0.03f, h * 0.02f) { drawPath(p, t.s(0.28f)) }
                drawPath(p, t.c(CALIZA))
                clipPath(p) {
                    drawRect(t.b(0.25f), r.topLeft, Size(r.width * 0.3f, r.height))
                    drawRect(t.s(0.2f), Offset(r.right - r.width * 0.22f, r.top), Size(r.width * 0.22f, r.height))
                    // La cuadrícula roja del dibujante.
                    for (k in 1 until 6) {
                        drawLine(t.c(ROJO).copy(alpha = 0.45f), Offset(r.left, r.top + r.height * k / 6f), Offset(r.right, r.top + r.height * k / 6f), 0.6f * u)
                        drawLine(t.c(ROJO).copy(alpha = 0.45f), Offset(r.left + r.width * k / 6f, r.top), Offset(r.left + r.width * k / 6f, r.bottom), 0.6f * u)
                    }
                    // Una cabeza de perfil, a medias: el boceto rojo y la línea negra que solo llega a la frente.
                    val cc = Offset(r.center.x, r.center.y - r.height * 0.05f)
                    val s = r.height * 0.3f
                    val perfil = Path().apply {
                        moveTo(cc.x - s * 0.6f, cc.y - s * 0.6f)
                        cubicTo(cc.x, cc.y - s * 1.1f, cc.x + s * 0.6f, cc.y - s * 0.7f, cc.x + s * 0.55f, cc.y - s * 0.1f)
                        lineTo(cc.x + s * 0.75f, cc.y + s * 0.15f); lineTo(cc.x + s * 0.5f, cc.y + s * 0.3f)
                        cubicTo(cc.x + s * 0.5f, cc.y + s * 0.6f, cc.x + s * 0.2f, cc.y + s * 0.8f, cc.x - s * 0.2f, cc.y + s * 1f)
                    }
                    drawPath(perfil, t.c(ROJO).copy(alpha = 0.8f), style = Stroke(1f * u))
                    clipRect(r.left, r.top, r.right, cc.y - s * 0.2f) { drawPath(perfil, t.c(CARBON), style = Stroke(1.4f * u)) }
                    drawOval(t.c(CARBON), Offset(cc.x + s * 0.2f, cc.y - s * 0.25f), Size(s * 0.22f, s * 0.1f))
                }
                drawPath(p, t.s(0.25f), style = Stroke(0.8f * u))
            }
            // El pincel de junco, tumbado delante.
            drawLine(t.s(0.3f), Offset(w * 0.18f, piso + h * 0.06f), Offset(w * 0.6f, piso + h * 0.04f), 2f * u)
            drawLine(t.c(PALMA), Offset(w * 0.16f, piso + h * 0.045f), Offset(w * 0.58f, piso + h * 0.025f), 1.6f * u, StrokeCap.Round)
            drawLine(t.c(CARBON), Offset(w * 0.16f, piso + h * 0.045f), Offset(w * 0.19f, piso + h * 0.044f), 1.6f * u, StrokeCap.Round)
        } else {
            // Dos lascas en blanco, una sobre otra, y el escarabajo de fayenza delante.
            listOf(Rect(w * 0.12f, h * 0.68f, w * 0.48f, piso + h * 0.02f), Rect(w * 0.16f, h * 0.6f, w * 0.44f, h * 0.76f)).forEachIndexed { i, r ->
                val p = contornoIrregular(r, c.semilla + i, 14, 0.12f)
                translate(w * 0.02f, h * 0.015f) { drawPath(p, t.s(0.25f)) }
                drawPath(p, t.c(if (i == 0) mezcla(CALIZA, OCRE, 0.15f) else CALIZA))
                clipPath(p) { drawRect(t.b(0.3f), r.topLeft, Size(r.width, r.height * 0.35f)); drawRect(t.s(0.2f), Offset(r.left, r.top + r.height * 0.65f), Size(r.width, r.height)) }
            }
            val e = Offset(w * 0.66f, piso - h * 0.01f)
            val es = w * 0.07f
            contacto(Offset(e.x + w * 0.01f, e.y + h * 0.02f), es * 2.4f, h * 0.04f, 0.4f)
            val fay = t.c(mezcla(FAYENZA, AZUL, 0.3f))
            val cuerpo = Rect(e.x - es, e.y - es * 1.1f, e.x + es, e.y + h * 0.02f)
            drawOval(fay, cuerpo.topLeft, cuerpo.size)
            clipPath(Path().apply { addOval(cuerpo) }) {
                drawRect(t.b(0.4f), cuerpo.topLeft, Size(cuerpo.width * 0.4f, cuerpo.height))
                drawRect(t.s(0.3f), Offset(cuerpo.left + cuerpo.width * 0.65f, cuerpo.top), Size(cuerpo.width, cuerpo.height))
            }
            // La cabeza, el corselete y la raya de los élitros.
            drawRect(t.c(mezcla(FAYENZA, CARBON, 0.4f)), Offset(cuerpo.left + es * 0.3f, cuerpo.top + cuerpo.height * 0.32f), Size(es * 1.4f, 1.2f * u))
            drawLine(t.c(mezcla(FAYENZA, CARBON, 0.4f)), Offset(e.x, cuerpo.top + cuerpo.height * 0.33f), Offset(e.x, cuerpo.bottom - 2f * u), 1.1f * u)
            drawOval(fay, Offset(e.x - es * 0.35f, cuerpo.top - es * 0.25f), Size(es * 0.7f, es * 0.45f))
            destello(Offset(e.x - es * 0.45f, cuerpo.top + es * 0.35f), 1.6f * u, t)
            destello(Offset(e.x - es * 0.1f, cuerpo.top - es * 0.12f), 1f * u, t)
        }
    }

    override val rotulo = Rect(0.06f, 0.52f, 0.94f, 0.82f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.messiri
    override val salida = -SALIDA
    override val placa = Rect(0.33f, 0.24f, 0.62f, 0.44f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo del cofre, forrado de lino: pliegues suaves en diagonal.
        drawRect(t(LINO, oscuro))
        for (k in 0 until 14) {
            val x = w * (k / 10f) - w * 0.3f
            val pliegue = Path().apply { moveTo(x, 0f); cubicTo(x + w * 0.1f, h * 0.3f, x - w * 0.05f, h * 0.6f, x + w * 0.2f, h) }
            drawPath(pliegue, t(LINO_OSC, oscuro).copy(alpha = 0.35f), style = Stroke((6f + 10f * azar(71, k)) * u, cap = StrokeCap.Round))
            drawPath(pliegue, Color.White.copy(alpha = 0.18f), style = Stroke(1.5f * u))
        }
        // El tejido: hilos finos en las dos direcciones.
        var y = 0f; while (y < h) { drawLine(t(LINO_OSC, oscuro).copy(alpha = 0.12f), Offset(0f, y), Offset(w, y), 0.5f * u); y += 2.5f * u }
        // Las paredes de cedro y la sombra que echan sobre el lino.
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl * 1.6f, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(CEDRO, oscuro), r.topLeft, r.size)
            vetas(r, CEDRO_OSC.copy(alpha = 0.4f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.4f)
        }
        sombraInterior(Rect(pl, pl, w - pl, h - pl * 1.6f), 18 * u, 0.3f)
        // El borde de fayenza en el canto de la pared de enfrente.
        drawRect(t(FAYENZA, oscuro), Offset(0f, h - pl * 1.6f), Size(w, 2.5f * u))
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) Color(0xFFF2E6CC) else TINTA
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 3

    override fun forma(semilla: Int): Shape = RoundedCornerShape(4.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        val m = 5 * u * g
        // La cara de estuco, más blanca que el papel, con su craquelado.
        drawRect(ESTUCO.copy(alpha = 0.55f), Offset(m, m), Size(w - 2 * m, h - 2 * m))
        for (k in 0 until (if (grande) 9 else 4)) {
            val p0 = Offset(m + (w - 2 * m) * azar(semilla, k), m + (h - 2 * m) * azar(semilla, k + 20))
            val q = Path().apply { moveTo(p0.x, p0.y); var o = p0; repeat(4) { j -> o = Offset(o.x + (azar(semilla, k * 7 + j + 40) - 0.5f) * 20 * u, o.y + (azar(semilla, k * 7 + j + 60) - 0.3f) * 14 * u); lineTo(o.x, o.y) } }
            drawPath(q, PIEDRA_OSC.copy(alpha = 0.25f), style = Stroke(0.5f * u))
        }
        // El marco de madera de la tablilla.
        drawRect(Brush.linearGradient(listOf(CEDRO_LUZ, CEDRO, CEDRO_OSC), Offset.Zero, Offset(w, h)), Offset.Zero, Size(w, h), style = Stroke(m * 2))
        drawRect(CEDRO_OSC, Offset(m, m), Size(w - 2 * m, h - 2 * m), style = Stroke(0.8f * u))
        // El agujero para colgarla, con su cordel de lino, arriba a la derecha.
        val hoyo = Offset(w - m / 2, m * 1.2f)
        drawCircle(Color.Black.copy(alpha = 0.6f), m * 0.28f, hoyo)
        drawLine(LINO, hoyo, Offset(w, m * 0.2f), 1f * u * g)
        // Las dos pastillas de tinta, roja y negra, en la esquina de abajo a la izquierda.
        drawCircle(CORNALINA, 2.6f * u * g, Offset(m * 2.4f, h - m * 2.2f))
        drawCircle(EBANO, 2.6f * u * g, Offset(m * 2.4f + 6.5f * u * g, h - m * 2.2f))
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 16.dp, 22.dp, 28.dp, 32.dp)
    override val proporcionFicha = 1.25f
    override val anchoGrande = 1f
    override val proporcionGrande = 1f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El pilar djed: lo que se mantiene firme.
            "hecho" -> {
                drawRect(LAPIS, Offset(c.x - s * 0.1f, c.y - s * 0.24f), Size(s * 0.2f, s * 0.66f))
                for (k in 0..3) drawRect(LAPIS, Offset(c.x - s * 0.3f, c.y - s * 0.44f + k * s * 0.1f), Size(s * 0.6f, s * 0.06f))
            }
            // El ankh: la vida.
            "convence" -> {
                drawOval(LAPIS, Offset(c.x - s * 0.18f, c.y - s * 0.46f), Size(s * 0.36f, s * 0.4f), style = Stroke(s * 0.1f))
                drawLine(LAPIS, Offset(c.x, c.y - s * 0.06f), Offset(c.x, c.y + s * 0.46f), s * 0.12f)
                drawLine(LAPIS, Offset(c.x - s * 0.34f, c.y + 0.02f * s), Offset(c.x + s * 0.34f, c.y + 0.02f * s), s * 0.11f)
            }
            // El ojo: lo estás mirando.
            "piensa" -> {
                drawOval(EBANO, Offset(c.x - s * 0.42f, c.y - s * 0.18f), Size(s * 0.84f, s * 0.36f), style = Stroke(s * 0.08f))
                drawCircle(EBANO, s * 0.13f, c)
                drawLine(EBANO, Offset(c.x - s * 0.08f, c.y + s * 0.16f), Offset(c.x - s * 0.2f, c.y + s * 0.42f), s * 0.07f)
            }
            // El anillo shen, sin cerrar del todo.
            "duda" -> { drawCircle(p.tenue, s * 0.3f, Offset(c.x, c.y - s * 0.08f), style = Stroke(s * 0.1f)); drawLine(p.tenue, Offset(c.x - s * 0.34f, c.y + s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.1f) }
            "desacuerdo" -> { drawLine(CORNALINA, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Offset(c.x + s * 0.34f, c.y + s * 0.34f), s * 0.13f); drawLine(CORNALINA, Offset(c.x + s * 0.34f, c.y - s * 0.34f), Offset(c.x - s * 0.34f, c.y + s * 0.34f), s * 0.13f) }
            else -> {}
        }
    }

    /** Un escarabajo de lapislázuli con las alas de oro. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        listOf(-1f, 1f).forEach { k -> for (j in 0 until 3) drawLine(EBANO, Offset(c.x, c.y - s * 0.1f + j * s * 0.18f), Offset(c.x + k * s * 0.42f, c.y - s * 0.2f + j * s * 0.24f), s * 0.05f) }
        drawOval(LAPIS, Offset(c.x - s * 0.26f, c.y - s * 0.24f), Size(s * 0.52f, s * 0.62f))
        drawOval(LAPIS, Offset(c.x - s * 0.15f, c.y - s * 0.44f), Size(s * 0.3f, s * 0.22f))
        drawLine(ORO, Offset(c.x, c.y - s * 0.22f), Offset(c.x, c.y + s * 0.36f), s * 0.05f)
        drawLine(ORO, Offset(c.x - s * 0.24f, c.y - s * 0.12f), Offset(c.x + s * 0.24f, c.y - s * 0.12f), s * 0.05f)
    }

    override val desorden = 0f
}
