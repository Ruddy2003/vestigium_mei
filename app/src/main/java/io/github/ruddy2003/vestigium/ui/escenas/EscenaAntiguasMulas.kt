package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono

/**
 * Las mulas de la posada, «la Terca, la más vieja», atadas a la argolla del zaguán cuando no están en el camino: de lado,
 * mirando a la puerta, una algo más lejos que la otra. Una mula no es un caballo chico: la cabeza grande y larga, de
 * perfil algo acarnerado, las orejas largas, el cuello recto con la crin corta de cepillo, la cruz baja, la grupa caída,
 * los corvejones bien marcados, las patas finas y los cascos chicos y altos; el morro y la barriga más claros; la parda,
 * con su raya en el lomo. El aparejo de carga: la manta, el basto con sus dos arcos de haya, la cincha, el pretal y la
 * baticola; el día de llenar los refugios, la leña. Con su sombra, su lado de luz y, de noche, el filo de la luna arriba y
 * el de la puerta del lado de la cabeza (la noche a dos luces).
 */
internal object MulasAntiguas {
    private val C = ColorAntiguas

    /** Una mula de la argolla: dónde tiene el hocico a lo largo del muro del zaguán, cuánto se aparta del muro, su pelo, si descansa y cuánto baja la cabeza. */
    class Mula(val s: Float, val fuera: Float, val pelo: Color, val descansa: Boolean, val baja: Float)

    val MULAS = listOf(Mula(1.12f, 0.95f, C.MULA, false, 0f), Mula(2.3f, 0.25f, C.MULA_PARDA, true, 1f))

    /**
     * El día de un objeto grande (recostado en el muro entre la esquina del zaguán y la jamba) las mulas se corren a la
     * izquierda y se atan cortas a la argolla de la esquina, para que el objeto se vea entero **[Diseño propio]**.
     */
    val MULAS_CORRIDAS = listOf(Mula(1.3f, 0.95f, C.MULA, false, 0f), Mula(2.4f, 0.25f, C.MULA_PARDA, true, 1f))

    fun mulasDe(grande: Boolean): List<Mula> = if (grande) MULAS_CORRIDAS else MULAS

    /** Dónde pisa una mula (bajo la cincha) y su alto hasta la cruz, en el lienzo. */
    fun pieYAlto(m: Mula): Pair<Offset, Float> {
        val k = FachadaPosada.escala(m.s + 1f, m.fuera)
        val hocico = FachadaPosada.p(m.s, 0f, m.fuera)
        val alto = 1.28f * k
        return Offset(hocico.x - 0.96f * alto, FachadaPosada.p(m.s + 1f, 0f, m.fuera).y) to alto
    }

    fun DrawScope.mulas(v: VistaAntiguas, n: Int, cargadas: Boolean, grande: Boolean) {
        if (n <= 0) return
        for (mu in mulasDe(grande).take(n).reversed()) {
            val (pie, alto) = pieYAlto(mu)
            val pe = v.pincelEn(pie + Offset(0f, -alto * 0.6f), lumbre = (v.encendido.sala * v.noche * 0.5f).coerceIn(0f, 1f))
            mula(pe, v, pie, alto, mu.pelo, mu.descansa, mu.baja, cargadas)
            // El ronzal, de la quijada a la argolla, con su comba.
            val u = alto / 100f
            val cab = pie + Offset(88f * u, (-96f + 12f * mu.baja) * u)
            val ar = if (grande) PuertaAntiguas.ARGOLLA_ESQUINA else PuertaAntiguas.ARGOLLA
            drawPath(Path().apply { moveTo(cab); cubicTo(cab + Offset(4f, 10f), ar + Offset(-6f, 12f), ar) }, pe.medio(lerp(C.CUERO, C.LANA, 0.3f)), style = Stroke(0.8f, cap = StrokeCap.Round))
        }
    }

    /** La silueta de una mula (para su sombra arrojada): el cuerpo, el cuello, la cabeza y las patas. */
    fun silueta(pie: Offset, alto: Float): Path {
        val u = alto / 100f
        fun q(x: Float, y: Float) = pie + Offset(x * u, y * u)
        return poligono(listOf(q(-66f, -94f), q(-40f, -102f), q(0f, -96f), q(28f, -101f), q(64f, -128f), q(56f, -164f), q(70f, -134f), q(96f, -96f), q(84f, -88f), q(54f, -80f), q(44f, -52f), q(44f, 0f), q(34f, 0f), q(30f, -46f), q(-38f, -50f), q(-44f, 0f), q(-54f, 0f), q(-66f, -40f)))
    }

    /**
     * Una mula de lado, mirando a la derecha, con [alto] unidades hasta la cruz (u = alto / 100). [descansa]: una pata de
     * atrás doblada en la punta del casco. [baja], de 0 a 1: la cabeza más baja y las orejas caídas a los lados (la que
     * dormita). [cargada]: con dos haces de leña sobre el basto.
     */
    fun DrawScope.mula(p: Pincel, v: VistaAntiguas, pie: Offset, alto: Float, pelo: Color, descansa: Boolean, baja: Float, cargada: Boolean) {
        val u = alto / 100f
        // La grupa, más lejos que la cabeza (la mula se aleja con el muro): un poco más chica.
        fun q(x: Float, y: Float) = pie + Offset(x * u, y * u * (1f + 0.0011f * x))
        val parda = pelo == C.MULA_PARDA
        val luz = p.luz(lerp(pelo, C.LANA, 0.25f)); val medio = p.por(pelo, 0.05f); val sombra = p.sombra(lerp(pelo, C.ESCORIA, 0.25f))
        val oscura = p.sombra(lerp(pelo, C.ESCORIA, 0.5f)); val claro = p.por(lerp(pelo, C.LANA, 0.55f), 0.15f)
        fun vestir(forma: Path, y0: Float, y1: Float) {
            drawPath(forma, medio)
            clipPath(forma) {
                drawRect(Brush.verticalGradient(0f to luz, 0.38f to luz.copy(alpha = 0f), 0.6f to sombra.copy(alpha = 0f), 1f to sombra, startY = y0, endY = y1), Offset(pie.x - 100f * u, y0 - 2f), Size(210f * u, y1 - y0 + 4f))
                drawRect(Brush.horizontalGradient(if (p.solALaDerecha) listOf(sombra.copy(alpha = 0.45f), Color.Transparent, luz.copy(alpha = 0.2f)) else listOf(luz.copy(alpha = 0.2f), Color.Transparent, sombra.copy(alpha = 0.45f)), pie.x - 70f * u, pie.x + 100f * u), Offset(pie.x - 100f * u, y0 - 2f), Size(210f * u, y1 - y0 + 4f))
            }
        }
        // La sombra de contacto, bajo los cascos y la barriga.
        drawOval(p.sombra(C.TIERRA).copy(alpha = 0.5f), Offset(pie.x - 72f * u, pie.y - 3f * u), Size(126f * u, 6f * u))
        // Las patas de allá, en sombra: la de delante algo adelantada, la de atrás recta.
        pata(q(30f, -58f), q(31f, -30f), q(33f, -8f), q(35f, 0f), 8f * u, oscura, u, false)
        pata(q(-46f, -64f), q(-56f, -32f), q(-50f, -8f), q(-49f, 0f), 10f * u, oscura, u, true)
        // La cola: el maslo corto con pelo ralo y las cerdas largas de media cola para abajo.
        val cola = Path().apply { moveTo(q(-64f, -92f)); cubicTo(q(-72f, -84f), q(-73f, -66f), q(-71f, -48f)); cubicTo(q(-70f, -40f), q(-66f, -36f), q(-63f, -42f)); cubicTo(q(-65f, -60f), q(-65f, -78f), q(-60f, -90f)); close() }
        drawPath(cola, p.medio(lerp(pelo, C.ESCORIA, 0.55f)))
        for (k in 0..3) drawLine(p.luz(lerp(pelo, C.LANA, 0.2f)).copy(alpha = 0.35f), q(-67f + k * 1.2f, -70f), q(-69f + k * 1.4f, -42f), 0.5f * u, StrokeCap.Round)
        // El cuerpo: la cruz baja, el lomo recto, la grupa caída, la barriga redonda.
        val cuerpo = Path().apply {
            moveTo(q(-66f, -86f)); cubicTo(q(-62f, -100f), q(-46f, -104f), q(-30f, -100f)); cubicTo(q(-14f, -96f), q(8f, -95f), q(24f, -100f))
            cubicTo(q(38f, -104f), q(52f, -96f), q(55f, -84f)); cubicTo(q(57f, -72f), q(50f, -58f), q(38f, -54f)); cubicTo(q(16f, -46f), q(-20f, -46f), q(-40f, -54f))
            cubicTo(q(-54f, -58f), q(-68f, -66f), q(-66f, -86f)); close()
        }
        vestir(cuerpo, q(0f, -104f).y, q(0f, -46f).y)
        clipPath(cuerpo) {
            // La paletilla y el anca con su luz; el costillar y la barriga clara; el pliegue de la babilla.
            mancha(q(-44f, -84f), 16f * u, 14f * u, luz, 0.45f); mancha(q(36f, -80f), 12f * u, 16f * u, luz, 0.35f)
            mancha(q(0f, -50f), 34f * u, 6f * u, claro, 0.5f)
            drawPath(Path().apply { moveTo(q(28f, -86f)); cubicTo(q(24f, -72f), q(28f, -60f), q(32f, -54f)) }, sombra.copy(alpha = 0.45f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(q(-34f, -92f)); cubicTo(q(-30f, -80f), q(-30f, -66f), q(-34f, -56f)) }, sombra.copy(alpha = 0.35f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            if (parda) drawPath(Path().apply { moveTo(q(-58f, -96f)); cubicTo(q(-30f, -101f), q(0f, -96f), q(24f, -100f)) }, p.medio(lerp(pelo, C.ESCORIA, 0.6f)).copy(alpha = 0.7f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        }
        // Las patas de acá: el antebrazo, la rodilla, la caña fina; atrás, el muslo, el corvejón y la caña; los cascos chicos.
        pata(q(40f, -60f), q(41f, -30f), q(42f, -8f), q(44f, 0f), 8.6f * u, medio, u, false)
        if (descansa) pata(q(-40f, -66f), q(-46f, -32f), q(-38f, -10f), q(-34f, -3f), 11f * u, medio, u, true)
        else pata(q(-40f, -66f), q(-50f, -32f), q(-44f, -8f), q(-43f, 0f), 11f * u, medio, u, true)
        // El cuello recto y grueso, con la crin corta de cepillo.
        val cuello = poligono(listOf(q(20f, -102f), q(38f, -114f), q(58f, -124f), q(68f, -124f), q(70f, -108f), q(58f, -92f), q(54f, -78f), q(36f, -72f)).map { if (baja > 0f) it + Offset(0f, baja * 6f * u * ((it.x - q(20f, 0f).x) / (50f * u)).coerceIn(0f, 1f)) else it })
        vestir(cuello, q(0f, -126f).y, q(0f, -72f).y)
        clipPath(cuello) { mancha(q(46f, -96f), 8f * u, 12f * u, luz, 0.3f) }
        for (k in 0..9) { val a = q(22f + k * 4.6f, -103f - k * 2.2f) + Offset(0f, baja * 6f * u * (k / 9f)); drawLine(p.medio(lerp(pelo, C.ESCORIA, 0.6f)), a, a + Offset(-1.2f * u, -4.4f * u), 1.5f * u, StrokeCap.Round) }
        // La cabeza: grande y larga, de perfil algo acarnerado, con el morro claro; las orejas largas.
        val dy = baja * 10f
        // La cabeza, algo más chica que la del dibujo de base y estirada hacia delante: de la nuca al morro.
        fun h(x: Float, y: Float) = q(64f + (x - 64f) * 0.86f, -126f + dy + (y + 126f) * 0.86f)
        val cabeza = Path().apply {
            moveTo(h(62f, -128f)); cubicTo(h(72f, -131f), h(82f, -122f), h(90f, -110f)); cubicTo(h(97f, -100f), h(100f, -92f), h(95f, -86f))
            cubicTo(h(89f, -82f), h(82f, -85f), h(76f, -92f)); cubicTo(h(68f, -99f), h(60f, -108f), h(62f, -128f)); close()
        }
        fun oreja(dx: Float, cerca: Boolean) {
            val b = h(62f + dx, -127f)
            val punta = if (baja > 0.5f) b + Offset(-19f * u, -6f * u) else b + Offset(-6f * u, -29f * u)
            drawPath(Path().apply { moveTo(b); cubicTo(b + Offset(-7f * u, -6f * u), punta + Offset(-3f * u, 8f * u), punta); cubicTo(punta + Offset(4f * u, 6f * u), b + Offset(6f * u, -8f * u), b + Offset(6f * u, 0f)); close() }, if (cerca) medio else oscura)
            if (cerca) drawLine(sombra, b + Offset(0f, -3f * u), (b + punta) / 2f + (punta - b) * 0.3f, 1.2f * u, StrokeCap.Round)
        }
        oreja(-3f, false)
        vestir(cabeza, h(64f, -132f).y, h(64f, -84f).y)
        clipPath(cabeza) {
            mancha(h(70f, -110f), 7f * u, 8f * u, luz, 0.4f)
            mancha(h(93f, -89f), 6f * u, 5f * u, claro, 0.85f)
            drawPath(Path().apply { moveTo(h(64f, -104f)); cubicTo(h(70f, -98f), h(76f, -96f), h(82f, -94f)) }, sombra.copy(alpha = 0.45f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        oreja(3f, true)
        // El ojo grande, con su brillo; el ollar.
        val ojo = h(74f, -114f)
        drawOval(p.medio(C.ESCORIA), Offset(ojo.x - 2.2f * u, ojo.y - 1.6f * u), Size(4.4f * u, 3.2f * u))
        drawCircle(Color.White.copy(alpha = 0.45f), 0.7f * u, ojo + Offset(0.6f * u, -0.6f * u))
        drawPath(Path().apply { moveTo(h(92f, -95f)); cubicTo(h(95f, -95f), h(96f, -92f), h(94f, -90f)) }, p.medio(C.ESCORIA), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // El cabezal de cuero.
        val cab = p.medio(lerp(C.CUERO, C.ESCORIA, 0.3f))
        drawLine(cab, h(64f, -122f), h(74f, -96f), 1.5f * u); drawLine(cab, h(74f, -98f), h(92f, -96f), 1.4f * u)
        aparejo(p, u, ::q, cargada)
        // De noche: el lomo y las orejas con la luz de la luna; del lado de la puerta, la lumbre.
        if (v.luna > 0.05f) clipPath(cuerpo) { drawPath(Path().apply { moveTo(q(-62f, -98f)); cubicTo(q(-40f, -104f), q(0f, -96f), q(24f, -102f)) }, C.LUNA.copy(alpha = 0.45f * v.luna), style = Stroke(5f * u, cap = StrokeCap.Round)) }
        if (p.calida > 0.03f) {
            val formas = union(listOf(cuerpo, cuello, cabeza))
            clipPath(formas) { drawRect(Brush.horizontalGradient(listOf(Color.Transparent, p.lumbre.copy(alpha = (0.55f * p.calida).coerceIn(0f, 1f))), pie.x, pie.x + 96f * u), Offset(pie.x - 70f * u, pie.y - 170f * u), Size(170f * u, 172f * u), blendMode = BlendMode.Plus) }
        }
    }

    /**
     * Una pata: de [a] (arriba) por [b] (la rodilla o el corvejón) y [c] (el menudillo) a [d] (el casco), de [g] de ancho
     * arriba; la rodilla y el corvejón, abultados; la caña fina; el casco chico, alto y oscuro.
     */
    private fun DrawScope.pata(a: Offset, b: Offset, c: Offset, d: Offset, g: Float, col: Color, u: Float, trasera: Boolean) {
        val nudo = if (trasera) 0.42f else 0.4f
        // El antebrazo o el muslo, ancho arriba y que se estrecha; la caña, fina; el menudillo.
        val m = (a + b) / 2f
        drawPath(poligono(listOf(a + Offset(-g, 0f), a + Offset(g, 0f), m + Offset(g * 0.62f, 0f), b + Offset(g * nudo, 0f), c + Offset(g * 0.24f, 0f), d + Offset(g * 0.26f, -3f * u), d + Offset(-g * 0.24f, -3f * u), c + Offset(-g * 0.24f, 0f), b + Offset(-g * nudo, 0f), m + Offset(-g * 0.62f, 0f))), col)
        drawCircle(col, g * 0.3f, c + Offset(0f, -0.6f * u))
        drawCircle(col, g * nudo * 1.05f, b)
        if (trasera) drawPath(poligono(listOf(b + Offset(-g * 0.5f, -g * 0.2f), b + Offset(-g * 0.95f, -g * 0.6f), b + Offset(-g * 0.3f, -g * 1.1f))), col)
        drawPath(poligono(d + Offset(-g * 0.36f, -3.4f * u), d + Offset(g * 0.4f, -3.4f * u), d + Offset(g * 0.5f, 0.4f), d + Offset(-g * 0.46f, 0.4f)), lerp(col, C.ESCORIA, 0.6f))
    }

    /** El aparejo de carga: la manta de fieltro, el basto con sus dos arcos de haya y sus ganchos, la cincha, el pretal y la baticola; con leña, los dos haces atados. */
    private fun DrawScope.aparejo(p: Pincel, u: Float, q: (Float, Float) -> Offset, cargada: Boolean) {
        val cuero = p.medio(lerp(C.CUERO, C.ESCORIA, 0.25f))
        // La baticola, de la trasera del basto a la cola; el pretal, por el pecho.
        drawPath(Path().apply { moveTo(q(-24f, -96f)); cubicTo(q(-40f, -96f), q(-56f, -90f), q(-64f, -86f)) }, cuero, style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(q(14f, -88f)); cubicTo(q(30f, -78f), q(46f, -74f), q(54f, -80f)) }, cuero, style = Stroke(1.5f * u, cap = StrokeCap.Round))
        val manta = poligono(listOf(q(-28f, -102f), q(18f, -100f), q(22f, -72f), q(-30f, -72f)))
        drawPath(manta, p.por(lerp(C.RUBI, C.CUERO, 0.35f), 0.15f))
        for (k in 0..4) { val x = -28f + k * 12f; drawLine(p.por(C.LANA, 0.2f), q(x, -72f), q(x + 0.6f, -68f), 1.2f * u, StrokeCap.Round) }
        drawLine(p.luz(C.ORO).copy(alpha = 0.5f), q(-30f, -74f), q(22f, -74f), 1f * u)
        // El basto: un almohadón de paja forrado, con los dos arcos y los ganchos.
        drawPath(Path().apply { moveTo(q(-26f, -100f)); cubicTo(q(-24f, -110f), q(16f, -110f), q(16f, -100f)); close() }, p.por(lerp(C.LANA, C.CUERO, 0.4f), 0.25f))
        val haya = lerp(C.FRESNO, C.MADERA, 0.25f)
        for (x in listOf(-18f, 8f)) drawPath(Path().apply { moveTo(q(x - 6f, -100f)); cubicTo(q(x - 6f, -116f), q(x + 6f, -116f), q(x + 6f, -100f)) }, p.por(haya, 0.3f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawLine(p.por(haya, 0.2f), q(-25f, -108f), q(15f, -108f), 2f * u, StrokeCap.Round)
        for (x in listOf(-23f, 13f)) drawLine(p.medio(C.ESCORIA), q(x, -108f), q(x + 2f, -99f), 1f * u, StrokeCap.Round)
        // La cincha, que abraza la barriga.
        drawLine(cuero, q(-4f, -98f), q(-6f, -48f), 1.8f * u)
        if (cargada) for (k in 0..1) {
            val y = -114f - k * 8f
            for (j in 0..5) { val a = q(-36f + j * 1.6f, y + j * 0.6f); val b = q(28f + j * 1.2f, y - 2f + j * 0.6f); drawLine(p.por(lerp(C.MADERA, C.FRESNO, 0.3f), 0.2f - j * 0.08f), a, b, 2.2f * u, StrokeCap.Round) }
            drawCircle(p.luz(C.FRESNO), 1.4f * u, q(-36f, y + 1f)); drawLine(p.medio(C.CUERO), q(-12f, y - 3f), q(-12f, y + 4f), 1f * u)
        }
    }
}
