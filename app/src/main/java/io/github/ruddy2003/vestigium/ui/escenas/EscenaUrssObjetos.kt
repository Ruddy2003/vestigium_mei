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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los doce objetos del día de Liudmila, en su cocina.
 *
 * - **Chicos** (sobre el hule, en la esquina de delante de la mesa, donde da la mancha de sol por la mañana y el cono de
 *   la lámpara de noche): la bolsa de red llena, con lo que se ve por la malla; el monedero de boquilla abierto con la
 *   moneda de cinco kopeks de canto y otras dos en el hule; el libro de tapa de tela con su filete, sobre el periódico en
 *   que venía envuelto; el termo chino con flores pintadas; el pase de la fábrica, abierto; la postal de las mimosas
 *   sobre su sobre; la radio de cable, descolgada de la pared, con su cable y un destornillador; y el samovar, que ese
 *   día deja su sitio del fondo y viene delante, en su bandeja pintada, con su tetera encima y el vaso al lado.
 * - **Grandes** ([GRANDES]): el televisor, traído a la cocina y puesto en el taburete, con su antena de cuernos y su
 *   tapete (de noche, encendido con nieve, sin imagen); el abrigo de paño con cuello de piel, echado en el taburete con
 *   una manga colgando; las botas de fieltro, una de pie con su chanclo puesto y otra recostada en la pata de la mesa,
 *   con el otro chanclo caído; y el chal de flores echado en la esquina de la mesa, colgando por los dos cantos con sus
 *   flecos. Cada uno pinta su sombra (en el suelo, de la lámpara de noche o del sol dentro de su mancha de día).
 *
 * Todos se alumbran como la mesa en la que están ([Tono]): con las luces de la cocina (`CocinaUrss.luz`), lo de arriba
 * encendido dentro de la mancha de sol (`LuzCocinaUrss.encendido`) y dentro del cono de la lámpara
 * (`LuzCocinaUrss.alaLampara`); de noche, el lado de la lámpara naranja y el de la ventana azul.
 *
 * Seis objetos son también piezas de la cocina (`EscenaUrss.DE_LA_COCINA`, y el chal del gancho). Este contrato los dibuja
 * todos ([DIBUJADOS]), así que el día que uno es el objeto la cocina no pinta el suyo (`EscenaUrss.laCocinaPinta`).
 *
 * **[Diseño propio]**: cómo es cada uno y dónde va. Los chicos van más grandes de lo que serían (la moneda, unas cinco
 * veces; el pase y la postal, el doble) para que se lean a tamaño de teléfono. Sin letras, sin cifras y sin caras: la
 * moneda lleva su relieve (el canto, la corona de espigas, un botón en medio y una estrella) sin el número; el pase, un
 * recuadro en blanco donde iría la foto, un sello y rayas; el periódico y el sobre, rayas.
 */
internal object ObjetosUrss {
    private val C = ColorUrss
    private val K get() = CocinaUrss.K

    /** Los doce objetos del calendario de Liudmila (`AnioG6.kt`). */
    val TODOS = listOf("bolsa", "moneda", "libro", "abrigo", "botas", "radio", "televisor", "samovar", "termo", "chal", "pase", "postal")

    /** Los que no caben sobre la mesa: van en el taburete, de pie en el linóleo o echados en la esquina de la mesa. */
    val GRANDES = setOf("televisor", "abrigo", "botas", "chal")

    /** Los objetos de la cocina que este contrato dibuja él mismo el día que tocan (todos: la cocina deja de pintar el suyo). */
    val DIBUJADOS: Set<String> = setOf("samovar", "televisor", "radio", "abrigo", "chal", "botas", "bolsa")

    // ---- Los pocos colores propios: mezclas de la paleta del tema.
    private val PANO = lerp(C.MADERA, C.TINTA, 0.3f)                            // el paño del abrigo (el mismo del gancho)
    private val PIEL = lerp(C.PAPEL, C.MADERA, 0.45f)                           // el cuello de conejo
    private val FIELTRO = lerp(C.HORMIGON, C.TINTA, 0.25f)                      // las botas de fieltro (las del radiador)
    private val GOMA = lerp(C.TINTA, C.HIERRO, 0.4f)                            // los chanclos
    private val BAQUELITA = lerp(C.MADERA, C.LADRILLO, 0.3f)                    // la radio de cable (la de la pared)
    private val TELA = lerp(C.PAPEL, C.LATON, 0.4f)                             // la rejilla de tela
    private val CHAPA = lerp(C.ROJO, C.LADRILLO, 0.2f)                          // la chapa pintada del termo
    private val ROSA = lerp(C.ROJO, C.PAPEL, 0.55f)                             // las peonías del termo
    private val MIMOSA = lerp(lerp(C.ORO, C.LATON, 0.5f), Color(0xFFF2D64A), 0.55f)  // las bolitas de la mimosa, amarillo de imprenta
    private val CIELO = lerp(C.AZUL, C.PAPEL, 0.55f)                            // el cielo de la postal
    private val TAPA_LIBRO = lerp(C.VERDE, C.TINTA, 0.35f)                      // la tela del libro
    private val TINTA_AZUL = lerp(C.AZUL, C.TINTA, 0.35f)                       // lo escrito a mano y los sellos
    private val SELLO = lerp(C.SOMBRA, C.AZUL, 0.3f)                            // el sello violeta
    private val NOGAL = lerp(C.MADERA, C.LATON, 0.18f)                          // la chapa de madera del televisor
    private val CRISTAL = lerp(C.HIERRO, C.OLEO, 0.3f)                          // la pantalla apagada
    private val CHAL = lerp(C.TINTA, C.ROJO, 0.12f)                             // el fondo negro del chal
    private val PAN = lerp(C.LATON, C.LADRILLO, 0.45f)                          // el pan

    /** Si el objeto de hoy es de los grandes (no cabe sobre la mesa). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    // =================================================================
    //  El contrato
    // =================================================================

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {
        val id = m.ficha.objeto ?: return
        if (id in GRANDES || id !in TODOS) return
        val lz = CocinaUrss.luz(m)
        val M = marcoSobre(sobre, CocinaUrss.MY, escala)
        val T = Tono.en(m, lz, sobre, M.x, CocinaUrss.MY, M.t)
        // El soporte es la esquina de delante del hule: cada uno se corre un poco hacia dentro, para no salirse del canto.
        when (id) {
            "bolsa" -> bolsa(T, M.mas(7f, 0f))
            "moneda" -> moneda(T, M.mas(5f, 0f))
            "libro" -> libro(T, M.mas(6f, 0f))
            "radio" -> radio(T, M.mas(3f, -1f))
            "samovar" -> samovar(T, M.mas(9f, -3f), m)
            "termo" -> termo(T, M.mas(4f, -2f))
            "pase" -> pase(T, M.mas(5f, 0f))
            "postal" -> postal(T, M.mas(5f, 0f))
        }
    }

    /**
     * La silueta del objeto chico de hoy sobre [sobre], para que la cocina le tumbe su sombra en el hule como a lo demás
     * de la mesa (la del sol, dentro de la mancha; la de la lámpara, de noche). Vacía si hoy no hay chico.
     */
    fun silueta(m: Momento, sobre: Offset, escala: Float = 1f): Path {
        val id = m.ficha.objeto ?: return Path()
        if (id in GRANDES || id !in TODOS) return Path()
        val k = escala
        val (ancho, alto) = when (id) {
            "samovar" -> 30f to 58f
            "termo" -> 12f to 44f
            "radio" -> 30f to 22f
            "bolsa" -> 44f to 14f
            "moneda" -> 22f to 16f
            "libro" -> 28f to 6f
            else -> 30f to 3f
        }
        val w = ancho * k / 2f; val h = alto * k
        return Path().apply { moveTo(sobre.x - w, sobre.y); lineTo(sobre.x - w * 0.8f, sobre.y - h); lineTo(sobre.x + w * 0.8f, sobre.y - h); lineTo(sobre.x + w, sobre.y); close() }
    }

    /**
     * El objeto grande de hoy. Devuelve null si hoy no hay objeto o es de los chicos. Dónde va cada uno:
     * - las botas pisan en [pie] (el linóleo, junto a la mesa) y la segunda se recuesta en la pata de la mesa;
     * - el chal se echa en la esquina de delante de la mesa, cerca de [apoyo] (su canto);
     * - el televisor y el abrigo van en el taburete de la cocina (`CosasUrss.TABURETE`), al lado de [pie].
     * Cada uno pinta su sombra; la silueta de la [Cosa] es la de su bulto.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        val id = m.ficha.objeto ?: return null
        if (id !in GRANDES) return null
        val lz = CocinaUrss.luz(m)
        val e = escala / K.s(70f)
        return when (id) {
            "botas" -> {
                val M = marcoSobre(pie, CocinaUrss.SUELO, escala)
                val forma = poligono(M.q(-14f, 0f), M.q(-12f, 0f, 44f), M.q(20f, 0f, 44f), M.q(22f, 0f))
                Cosa(forma, pie) { sombraEnElSuelo(m, lz, forma, pie); botas(Tono.en(m, lz, pie, M.x, CocinaUrss.SUELO, M.t), M, m) }
            }
            "chal" -> {
                val M = Marco(CocinaUrss.MX0, CocinaUrss.MY, CocinaUrss.MT1, e)
                val forma = poligono(M.q(-1f, -34f), M.q(44f, -20f), M.q(30f, 1f, -38f), M.q(-1f, 0f, -34f))
                Cosa(forma, pie) { chal(Tono.en(m, lz, M.q(14f, -6f), M.x, CocinaUrss.MY, M.t), M, m) }
            }
            "televisor", "abrigo" -> {
                val tab = CosasUrss.TABURETE; val (x, t) = enElPlano(tab, CocinaUrss.SUELO)
                val M = Marco(x, CocinaUrss.SUELO - 36f * e, t, e)
                val tele = id == "televisor"
                val forma = if (tele) poligono(M.q(-17f, 11f, -36f), M.q(-15f, 11f, 26f), M.q(15f, 11f, 26f), M.q(17f, 11f, -36f))
                else poligono(M.q(-21f, 18f, -36f), M.q(-19f, 0f, 12f), M.q(17f, 0f, 12f), M.q(18f, 18f, -36f))
                val T = Tono.en(m, lz, M.q(0f, 0f, 10f), x, CocinaUrss.SUELO - 36f * e, t)
                Cosa(forma, pie) { sombraEnElSuelo(m, lz, forma, tab); if (tele) televisor(T, M, m) else abrigo(T, M) }
            }
            else -> null
        }
    }

    // =================================================================
    //  Ayudas: dónde está cada punto y cómo le da la luz
    // =================================================================

    /** El punto de la cocina (x, t) que cae en [o] del cuadro, sabiendo que está a la altura [y] (la mesa, el suelo). */
    private fun enElPlano(o: Offset, y: Float): Pair<Float, Float> {
        val f = K.fuga
        val s = ((o.y - f.y) / (y - f.y)).coerceIn(0.2f, 6f)
        return (f.x + (o.x - f.x) / s) to K.ojo * (1f - 1f / s)
    }

    /** El marco de una cosa que se apoya en [o], a la altura [y], dibujada a [escala] (la escala de la cocina en ese punto da 1). */
    private fun marcoSobre(o: Offset, y: Float, escala: Float): Marco {
        val (x, t) = enElPlano(o, y)
        return Marco(x, y, t, escala / K.s(t))
    }

    /**
     * Los ejes de una cosa de la cocina, puestos en (x, y, t) de su caja: [Marco.q] da un punto a u a la derecha, v hacia
     * quien mira y h hacia arriba, en unidades de la cocina por [e], girados [ang] en el plano del suelo.
     */
    private class Marco(val x: Float, val y: Float, val t: Float, val e: Float, val ang: Float = 0f) {
        private val ca = cos(ang); private val sa = sin(ang)
        fun q(u: Float, v: Float, h: Float = 0f): Offset {
            val uu = u * ca - v * sa; val vv = u * sa + v * ca
            return CocinaUrss.K.p(x + uu * e, y - h * e, t + vv * e)
        }
        /** Lo que mide una unidad en el cuadro, a la profundidad del marco. */
        val k: Float get() = CocinaUrss.K.s(t) * e
        fun mas(u: Float, v: Float, dAng: Float = 0f): Marco { val uu = u * ca - v * sa; val vv = u * sa + v * ca; return Marco(x + uu * e, y, t + vv * e, e, ang + dAng) }
    }

    /**
     * El color de las caras de una cosa con las luces de la cocina, como la mesa: [kl] cuánto le da el cono de la lámpara
     * (0 a 1) y [ks] cuánto la mancha de sol (o de luna). Lo de arriba se enciende con las dos; de día, el lado del sol
     * sube; de noche, el lado de la lámpara es naranja y el de la ventana, azul.
     */
    private class Tono(val lz: LuzUrss, val kl: Float, val ks: Float) {
        val s = lz.sol
        val noche = lz.noche
        /** El sol entra desde la derecha (por la mañana): le da al lado derecho de las cosas. */
        private val solDer = s != null && s.corre < 0f

        private fun conSol(q: Offset, c: Color, base: Color, k: Float): Color =
            if (s == null || ks <= 0.01f || k <= 0f) base else lerp(base, LuzCocinaUrss.encendido(lz, s, c, q, ks), k.coerceIn(0f, 1f))

        private fun conLampara(q: Offset, c: Color, base: Color, k: Float): Color =
            if (kl <= 0.01f || k <= 0f) base else lerp(base, LuzCocinaUrss.alaLampara(lz, c, q, 1f), (kl * k).coerceIn(0f, 1f))

        private fun conLuna(c: Color, base: Color, k: Float): Color =
            if (noche < 0.3f) base else lerp(base, mult(lerp(c, Color.White, 0.25f), C.LUNA, 0.85f), (k * noche).coerceIn(0f, 1f))

        fun en(q: Offset, c: Color): Color = conLampara(q, c, conSol(q, c, lz.en(q, c), 0.55f), 0.55f)
        fun arriba(q: Offset, c: Color): Color = conLampara(q, c, conSol(q, c, lz.arriba(q, c), 1f), 0.9f)
        fun izq(q: Offset, c: Color): Color = conLuna(c, conLampara(q, c, conSol(q, c, lz.izq(q, c), if (solDer) 0.15f else 0.75f), 0.12f), 0.3f)
        fun der(q: Offset, c: Color): Color = conLampara(q, c, conSol(q, c, lz.der(q, c), if (solDer) 0.75f else 0.15f), 0.7f)
        fun frente(q: Offset, c: Color): Color = conLampara(q, c, conSol(q, c, lz.frente(q, c), 0.2f), 0.4f)
        fun sombra(q: Offset, c: Color, k: Float = 0.8f): Color = conLampara(q, c, lz.sombra(q, c, k), 0.15f)
        /** Un filo de luz en el borde de algo oscuro: la ventana de día; de noche, la lámpara. */
        val filo: Color get() = lz.filo

        /** Un cuerpo redondo entre [x0] y [x1]: su lado izquierdo, la ventana (azul de noche); el derecho, la lámpara. */
        fun DrawScope.cuerpo(forma: Path, x0: Float, x1: Float, y: Float, c: Color) {
            val q0 = Offset(x0, y); val q1 = Offset(x1, y); val qm = Offset((x0 + x1) / 2f, y)
            val iz = izq(q0, lerp(c, Color.White, 0.12f)); val de = der(q1, lerp(c, Color.White, 0.12f)); val me = frente(qm, c)
            drawPath(forma, Brush.horizontalGradient(0f to iz, 0.25f to lerp(iz, me, 0.65f), 0.55f to me, 0.82f to lerp(de, me, 0.45f), 1f to de, startX = x0, endX = x1))
        }

        companion object {
            /**
             * El tono de una cosa que está en [q] del cuadro y pisa el punto (x, y, t) de la cocina: ahí se mira si le da
             * la mancha de sol (siguiendo el rayo hasta los cristales de la ventana) y el cono de la lámpara.
             */
            fun en(m: Momento, lz: LuzUrss, q: Offset, x: Float, y: Float, t: Float): Tono {
                val s = lz.sol
                var ks = 0f
                if (s != null && s.fuerza > 0.05f) {
                    // El rayo que llega a (x, y, t) entró por la ventana en (x − corre·t, y − baja·t).
                    val xv = x - s.corre * t; val yv = y - s.baja * t
                    val dentro = CocinaUrss.cristales(EscenaUrss.ventanaAbierta(m)).any { c -> xv in c.minOf { it.first }..c.maxOf { it.first } && yv in c.minOf { it.second }..c.maxOf { it.second } }
                    if (dentro) ks = (if (s.deLuna) 0.7f else 1f) * s.fuerza
                }
                var kl = 0f
                if (lz.lampara) {
                    val fuerza = 0.45f + 0.55f * lz.noche
                    // En la mesa, el círculo del cono (`LuzCocinaUrss.conoDeLaLampara`); en el suelo, su charco.
                    val e = if (y < CocinaUrss.SUELO - 20f) {
                        val c = CocinaUrss.BAJO_LA_LAMPARA + Offset(-6f, -2f); val dx = (q.x - c.x) / 58f; val dy = (q.y - c.y) / 16f; sqrt(dx * dx + dy * dy)
                    } else {
                        val c = CocinaUrss.K.p(CocinaUrss.LX - 40f, CocinaUrss.SUELO, CocinaUrss.LT + 12f); val dx = (q.x - c.x) / 110f; val dy = (q.y - c.y) / 40f; sqrt(dx * dx + dy * dy) * 1.2f
                    }
                    kl = fuerza * (1.45f - 0.75f * e).coerceIn(0f, 1f) * (if (y < CocinaUrss.SUELO - 20f) 0.85f else 0.45f)
                }
                return Tono(lz, kl, ks)
            }
        }
    }

    /** La sombra blanda donde algo toca lo que tiene debajo. */
    private fun DrawScope.contacto(c: Offset, rx: Float, ry: Float, a: Float = 0.32f) =
        drawOval(C.SOMBRA.copy(alpha = a), Offset(c.x - rx, c.y - ry), Size(rx * 2f, ry * 2f), blendMode = BlendMode.Multiply)

    /** Un óvalo en el plano de [M], de centro (u, v), con sus radios, a la altura [h]. */
    private fun ovalo(M: Marco, u: Float, v: Float, ru: Float, rv: Float, h: Float = 0f, n: Int = 24): Path = Path().apply {
        for (i in 0 until n) {
            val a = 2f * PI.toFloat() * i / n; val o = M.q(u + cos(a) * ru, v + sin(a) * rv, h)
            if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y)
        }
        close()
    }

    /** Un rectángulo de esquinas redondas (de cara a quien mira) de [a] a [b], con radio [r]. */
    private fun redondo(a: Offset, b: Offset, r: Float): Path = Path().apply {
        moveTo(a.x + r, a.y); lineTo(b.x - r, a.y); cubicTo(b.x - r * 0.45f, a.y, b.x, a.y + r * 0.45f, b.x, a.y + r)
        lineTo(b.x, b.y - r); cubicTo(b.x, b.y - r * 0.45f, b.x - r * 0.45f, b.y, b.x - r, b.y)
        lineTo(a.x + r, b.y); cubicTo(a.x + r * 0.45f, b.y, a.x, b.y - r * 0.45f, a.x, b.y - r)
        lineTo(a.x, a.y + r); cubicTo(a.x, a.y + r * 0.45f, a.x + r * 0.45f, a.y, a.x + r, a.y); close()
    }

    /**
     * Una caja en el marco [M] (de u0 a u1, de v0 a v1, de h0 a h1), con las caras que se ven: la de arriba, la del lado
     * que mira al centro y la de delante. Devuelve la de delante.
     */
    private fun DrawScope.caja(T: Tono, M: Marco, u0: Float, u1: Float, v0: Float, v1: Float, h0: Float, h1: Float, c: Color, tapa: Color = c, lado: Color = c): Path {
        val f = K.fuga
        if (M.q(u0, v1, h1).x > f.x) { val q = M.q(u0, (v0 + v1) / 2f, (h0 + h1) / 2f); drawPath(poligono(M.q(u0, v0, h1), M.q(u0, v1, h1), M.q(u0, v1, h0), M.q(u0, v0, h0)), T.izq(q, lado)) }
        if (M.q(u1, v1, h1).x < f.x) { val q = M.q(u1, (v0 + v1) / 2f, (h0 + h1) / 2f); drawPath(poligono(M.q(u1, v0, h1), M.q(u1, v1, h1), M.q(u1, v1, h0), M.q(u1, v0, h0)), T.der(q, lado)) }
        drawPath(poligono(M.q(u0, v0, h1), M.q(u1, v0, h1), M.q(u1, v1, h1), M.q(u0, v1, h1)), T.arriba(M.q((u0 + u1) / 2f, (v0 + v1) / 2f, h1), tapa))
        val frente = poligono(M.q(u0, v1, h1), M.q(u1, v1, h1), M.q(u1, v1, h0), M.q(u0, v1, h0))
        drawPath(frente, T.frente(M.q((u0 + u1) / 2f, v1, (h0 + h1) / 2f), c))
        // El canto de arriba, con su filo de luz.
        drawLine(T.arriba(M.q(0f, v1, h1), lerp(c, Color.White, 0.35f)).copy(alpha = 0.7f), M.q(u0, v1, h1), M.q(u1, v1, h1), 0.6f * M.k)
        return frente
    }

    /** La sombra que tumba en el suelo un grande que pisa en [pie] con la silueta [forma]: la de la lámpara de noche; de día, la del sol dentro de su mancha. */
    private fun DrawScope.sombraEnElSuelo(m: Momento, lz: LuzUrss, forma: Path, pie: Offset) {
        val s = lz.sol; val lampara = lz.a.focos.firstOrNull { it.p == CocinaUrss.BOMBILLA }
        if (lampara != null && lz.noche > 0.3f) capaSombra(rayoDeFoco(lampara, pie, 0.55f, C.SOMBRA)) { c ->
            clipPath(K.formaDelSuelo()) { sombraArrojada(rayoDeFoco(lampara, pie, 1f, c), forma, pie, c, CocinaUrss.SUELO, CocinaUrss.SUELO) }
        }
        else if (s != null && s.fuerza > 0.1f) capaSombra(LuzCocinaUrss.rayoDelSol(s, 1f)) { c ->
            clipPath(K.formaDelSuelo()) { clipPath(LuzCocinaUrss.manchaEnElSuelo(m, s)) { sombraArrojada(LuzCocinaUrss.rayoDelSol(s, 1f, pie), forma, pie, c, CocinaUrss.SUELO, CocinaUrss.SUELO) } }
        }
    }

    // =================================================================
    //  Los chicos
    // =================================================================

    /**
     * La bolsa de red llena, echada en el hule: por la malla de hilo se ve lo que lleva (el pan negro, la botella de
     * leche con su tapa de papel de plata, una lata, dos naranjas y un paquete atado con cordel); las asas, sueltas.
     */
    private fun DrawScope.bolsa(T: Tono, M: Marco) {
        val k = M.k
        contacto(M.q(-1f, 1f), 24f * k, 5f * k, 0.36f)
        // El bulto: de un cabo a otro, más alto por detrás.
        val borde = listOf(M.q(-19f, 1f, 2f), M.q(-15f, -6f, 9f), M.q(-4f, -9f, 12f), M.q(8f, -8f, 11f), M.q(15f, -3f, 7f), M.q(16f, 4f, 2f), M.q(8f, 8f, 0.5f), M.q(-6f, 8f, 0.5f), M.q(-16f, 6f, 1f))
        val bulto = Path().apply {
            val n = borde.size
            val mid = { i: Int -> val a = borde[i % n]; val b = borde[(i + 1) % n]; Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f) }
            val i0 = mid(n - 1); moveTo(i0.x, i0.y)
            for (i in 0 until n) { val c = borde[i]; val e = mid(i); cubicTo(c.x, c.y, c.x, c.y, e.x, e.y) }
            close()
        }
        // Lo de dentro, de atrás adelante: entre una cosa y otra se ve el hule, a la sombra del bulto.
        val hiloC = lerp(C.AZUL, C.TINTA, 0.35f)
        clipPath(bulto) {
            drawPath(bulto, T.sombra(M.q(0f, 0f, 4f), C.PAPEL, 0.55f))
            // El paquete de papel de estraza, atado con cordel.
            val pa = poligono(M.q(-1f, -9f, 12f), M.q(12f, -8f, 11f), M.q(12f, -2f, 5f), M.q(-1f, -3f, 6f))
            drawPath(pa, T.arriba(M.q(5f, -6f, 9f), lerp(C.PAPEL, C.LATON, 0.45f)))
            drawLine(T.en(M.q(5f, -6f, 9f), C.TINTA), M.q(5f, -8.6f, 11.6f), M.q(5f, -2.6f, 5.5f), 0.5f * k)
            drawLine(T.en(M.q(5f, -6f, 9f), C.TINTA), M.q(-1f, -6f, 9f), M.q(12f, -5f, 8f), 0.5f * k)
            // El pan negro, a la izquierda: un ladrillo con su corteza tostada y sus cortes.
            val pan = poligono(M.q(-18f, -4f, 9f), M.q(-6f, -6f, 10.5f), M.q(-5f, 3f, 5f), M.q(-18f, 4f, 3.5f))
            drawPath(pan, T.arriba(M.q(-11f, -1f, 8f), lerp(PAN, C.TINTA, 0.3f)))
            drawPath(poligono(M.q(-18f, 4f, 3.5f), M.q(-5f, 3f, 5f), M.q(-5f, 5f, 0f), M.q(-18f, 6f, 0f)), T.frente(M.q(-11f, 5f, 2f), lerp(PAN, C.TINTA, 0.45f)))
            for (i in 0..2) drawLine(T.arriba(M.q(-11f, 0f, 8f), lerp(PAN, C.PAPEL, 0.3f)).copy(alpha = 0.8f), M.q(-16f + i * 4f, -4f, 9.5f), M.q(-15f + i * 4f, 3f, 4.5f), 0.5f * k)
            // La botella de leche, echada, con su tapa de papel de plata hacia la derecha.
            val b0 = M.q(-4f, 4f, 4.5f); val b1 = M.q(9f, 0f, 5.5f)
            drawLine(T.en(b0, lerp(C.ESMALTE, C.HORMIGON, 0.15f)), b0, b1, 6.4f * k, StrokeCap.Round)
            drawLine(T.arriba(b0, lerp(C.ESMALTE, Color.White, 0.6f)), b0 + Offset(0f, -2f * k), b1 + Offset(0f, -2f * k), 1.4f * k, StrokeCap.Round)
            val cuello = b1 + (b1 - b0) * 0.24f
            drawLine(T.en(b1, lerp(C.ESMALTE, C.HORMIGON, 0.15f)), b1, cuello, 3.8f * k, StrokeCap.Round)
            drawCircle(T.arriba(cuello, lerp(C.CROMO, Color.White, 0.6f)), 2.2f * k, cuello)
            drawCircle(T.der(cuello, C.CROMO), 1.2f * k, cuello + Offset(0.6f * k, 0.3f * k))
            // Dos naranjas delante y la lata de pescado a la derecha.
            for ((u, v) in listOf(-10f to 6f, -3.5f to 7.5f)) { val o = M.q(u, v, 3f); drawCircle(T.en(o, C.NARANJA), 3.5f * k, o); drawCircle(T.arriba(o, lerp(C.NARANJA, C.PAPEL, 0.35f)), 1.4f * k, o + Offset(-0.8f * k, -1.2f * k)) }
            val lo = M.q(11f, 4f, 2.5f)
            drawRect(T.en(lo, C.CROMO), Offset(lo.x - 3.6f * k, lo.y - 2.6f * k), Size(7.2f * k, 4.6f * k))
            drawOval(T.arriba(lo, lerp(C.CROMO, Color.White, 0.3f)), Offset(lo.x - 3.6f * k, lo.y - 4f * k), Size(7.2f * k, 2.8f * k))
            drawRect(T.en(lo, C.ROJO), Offset(lo.x - 3.6f * k, lo.y - 1.4f * k), Size(7.2f * k, 1.6f * k))
            // La malla: rombos de hilo, ceñidos al bulto (más apretados en los costados), con sus nudos.
            val b = bulto.getBounds(); val paso = 3.2f * k; val cx = b.center.x
            val hilo = T.en(M.q(0f, 0f, 8f), hiloC)
            var x = b.left - b.height
            while (x < b.right + b.height) {
                fun junto(xx: Float) = cx + (xx - cx) * 0.92f
                drawLine(hilo.copy(alpha = 0.85f), Offset(junto(x), b.top), Offset(junto(x + b.height * 0.9f), b.bottom), 0.4f * k)
                drawLine(hilo.copy(alpha = 0.85f), Offset(junto(x + b.height * 0.9f), b.top), Offset(junto(x), b.bottom), 0.4f * k)
                x += paso
            }
            // El bulto se redondea: más oscuro abajo y delante.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.4f), startY = b.top, endY = b.bottom), b.topLeft, b.size, blendMode = BlendMode.Multiply)
        }
        drawPath(bulto, T.en(M.q(0f, 0f, 8f), hiloC).copy(alpha = 0.8f), style = Stroke(0.6f * k))
        // El fruncido de la boca, a la derecha, y las asas de cordel, sueltas sobre el hule, con su sombrita.
        val boca = M.q(15f, 0f, 5f)
        drawCircle(T.en(boca, hiloC), 1.6f * k, boca)
        for ((i, d) in listOf(0f, 3.5f).withIndex()) {
            val a = M.q(15f, -2f + d * 0.5f, 6f - i); val c = M.q(25f, 5f + d, 0f); val z = M.q(15.5f, 2f + d * 0.5f, 3f)
            val asa = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + 6f * k, a.y - 2f * k, c.x + 2f * k, c.y - 3f * k, c.x, c.y); cubicTo(c.x - 3f * k, c.y + 1f * k, z.x + 4f * k, z.y + 2f * k, z.x, z.y) }
            drawPath(asa, C.SOMBRA.copy(alpha = 0.25f), style = Stroke(0.9f * k), blendMode = BlendMode.Multiply)
            drawPath(asa, T.arriba(c, hiloC), style = Stroke(0.75f * k))
        }
    }

    /**
     * El monedero de boquilla, de cuero, abierto, con dos bolas de latón en el cierre; la moneda de cinco kopeks de canto,
     * apoyada en él, con su relieve (sin el número), y otras dos echadas en el hule.
     */
    private fun DrawScope.moneda(T: Tono, M: Marco) {
        val k = M.k
        val cuero = lerp(C.MADERA, C.LADRILLO, 0.35f)
        // Las monedas echadas: una de cobre y otra de plata, de lado en el hule.
        for ((u, v, c) in listOf(Triple(6f, 6f, C.CROMO), Triple(12f, 2f, C.LATON))) {
            contacto(M.q(u, v), 4.2f * k, 1.4f * k, 0.3f)
            drawPath(ovalo(M, u, v, 3.6f, 3.6f, 0.4f), T.arriba(M.q(u, v), lerp(c, C.TINTA, 0.1f)))
            drawPath(ovalo(M, u, v, 2.8f, 2.8f, 0.5f), T.arriba(M.q(u, v), lerp(c, Color.White, 0.2f)).copy(alpha = 0.8f), style = Stroke(0.4f * k))
        }
        // El monedero de boquilla, de pie: la bolsa de cuero, panzuda, y la boquilla de latón en arco con sus dos bolas cruzadas.
        val o = M.q(-6f, -1f)
        contacto(o, 12f * k, 3f * k, 0.38f)
        val w = 8.6f * k; val h = 14f * k
        val ya = o.y - h * 0.72f                                                      // los cabos de la boquilla
        val bolsa = Path().apply {
            moveTo(o.x - w * 0.92f, ya); cubicTo(o.x - w * 1.3f, o.y - h * 0.3f, o.x - w * 0.95f, o.y, o.x, o.y)
            cubicTo(o.x + w * 0.95f, o.y, o.x + w * 1.3f, o.y - h * 0.3f, o.x + w * 0.92f, ya)
            cubicTo(o.x + w * 0.6f, o.y - h * 1.02f, o.x - w * 0.6f, o.y - h * 1.02f, o.x - w * 0.92f, ya); close()
        }
        with(T) { cuerpo(bolsa, o.x - w * 1.25f, o.x + w * 1.25f, o.y - h * 0.45f, cuero) }
        clipPath(bolsa) {
            // El cuero gastado: los frunces bajo la boquilla, el brillo del roce y el pespunte.
            for (i in -3..3) drawLine(T.sombra(o, cuero, 0.6f).copy(alpha = 0.55f), Offset(o.x + i * w * 0.26f, o.y - h * 0.8f), Offset(o.x + i * w * 0.32f, o.y - h * 0.55f), 0.45f * k)
            mancha(Offset(o.x + w * 0.4f, o.y - h * 0.4f), w * 0.35f, h * 0.22f, T.arriba(o, lerp(cuero, C.PAPEL, 0.45f)), 0.5f)
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.35f), startY = o.y - h * 0.3f, endY = o.y), Offset(o.x - w * 1.3f, o.y - h * 0.3f), Size(w * 2.6f, h * 0.3f), blendMode = BlendMode.Multiply)
        }
        // La boquilla: un arco de latón con su filo de luz.
        val arco = Path().apply { moveTo(o.x - w * 0.92f, ya); cubicTo(o.x - w * 0.6f, o.y - h * 1.02f, o.x + w * 0.6f, o.y - h * 1.02f, o.x + w * 0.92f, ya) }
        drawPath(arco, T.frente(o, lerp(C.LATON, C.TINTA, 0.2f)), style = Stroke(1.7f * k))
        drawPath(arco, T.arriba(o, lerp(C.LATON, Color.White, 0.35f)), style = Stroke(0.6f * k))
        for (s in listOf(-1f, 1f)) drawCircle(T.en(o, C.LATON), 0.8f * k, Offset(o.x + s * w * 0.92f, ya))
        // Las dos bolas del cierre, cruzadas en lo alto.
        val tope = o.y - h * 0.94f
        for ((dx, d) in listOf(-1.3f to -1f, 1.3f to 1f)) {
            val c = Offset(o.x + dx * k, tope - 2f * k)
            drawLine(T.en(c, C.LATON), Offset(o.x - dx * 0.4f * k, tope), c, 0.7f * k)
            drawCircle(T.sombra(c, C.LATON, 0.4f), 1.7f * k, c + Offset(0.3f * k, 0.3f * k))
            drawCircle(if (d < 0f) T.izq(c, C.LATON) else T.der(c, C.LATON), 1.6f * k, c)
            drawCircle(T.arriba(c, lerp(C.LATON, Color.White, 0.6f)), 0.6f * k, c + Offset(-0.5f * k, -0.5f * k))
        }
        // La moneda de cinco kopeks, de canto, apoyada en el monedero: el canto, la corona de espigas, el botón y la estrella.
        val c = M.q(5.5f, 0.5f, 5.6f); val r = 5.4f * k
        drawOval(C.SOMBRA.copy(alpha = 0.35f), Offset(c.x - r * 0.9f, c.y + r * 0.85f), Size(r * 2.2f, r * 0.5f), blendMode = BlendMode.Multiply)
        drawOval(T.der(c, lerp(C.LATON, C.TINTA, 0.35f)), Offset(c.x - r + 0.8f * k, c.y - r), Size(r * 2f, r * 2f))
        val cara = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        drawPath(cara, Brush.linearGradient(listOf(T.arriba(c, lerp(C.LATON, Color.White, 0.2f)), T.en(c, C.LATON), T.sombra(c, C.LATON, 0.45f)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)))
        drawCircle(T.sombra(c, C.LATON, 0.6f).copy(alpha = 0.8f), r * 0.86f, c, style = Stroke(0.5f * k))
        drawCircle(T.arriba(c, lerp(C.LATON, Color.White, 0.5f)).copy(alpha = 0.7f), r * 0.8f, c, style = Stroke(0.3f * k))
        for (lado in listOf(-1f, 1f)) for (i in 0..5) {
            val a = (PI / 2 + lado * (0.5 + i * 0.36)).toFloat(); val q = c + Offset(cos(a) * r * 0.62f, sin(a) * r * 0.62f)
            drawOval(T.sombra(c, C.LATON, 0.55f).copy(alpha = 0.8f), Offset(q.x - 0.7f * k, q.y - 0.4f * k), Size(1.4f * k, 0.8f * k))
            drawOval(T.arriba(c, lerp(C.LATON, Color.White, 0.45f)).copy(alpha = 0.8f), Offset(q.x - 0.6f * k, q.y - 0.55f * k), Size(1.1f * k, 0.5f * k))
        }
        drawCircle(T.sombra(c, C.LATON, 0.5f), 1.9f * k, c + Offset(0.3f * k, 0.6f * k)); drawCircle(T.arriba(c, lerp(C.LATON, Color.White, 0.3f)), 1.7f * k, c + Offset(0f, 0.4f * k))
        val es = c + Offset(0f, -r * 0.55f)
        drawPath(estrella(es, 1.3f * k), T.arriba(c, lerp(C.LATON, Color.White, 0.35f)))
        drawPath(cara, T.filo.copy(alpha = 0.35f), style = Stroke(0.5f * k))
    }

    private fun estrella(c: Offset, r: Float): Path = Path().apply {
        for (i in 0..9) { val rr = if (i % 2 == 0) r else r * 0.42f; val a = (-PI / 2 + i * PI / 5).toFloat(); val q = c + Offset(cos(a) * rr, sin(a) * rr); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
        close()
    }

    /**
     * El libro de tapa de tela verde, con su filete dorado, su lomo con dos nervios y la cinta de señal, sobre el periódico
     * en que venía envuelto (rayas, sin letras), ya abierto.
     */
    private fun DrawScope.libro(T: Tono, M: Marco) {
        val k = M.k
        // El periódico, abierto debajo, un poco girado: sus columnas de rayas y el pliegue.
        val P = M.mas(1f, 1f, 0.14f)
        contacto(P.q(0f, 1f), 22f * k, 4f * k, 0.25f)
        val hoja = poligono(P.q(-17f, -11f, 0.2f), P.q(16f, -12f, 0.2f), P.q(17f, 11f, 0.2f), P.q(-16f, 12f, 0.2f))
        drawPath(hoja, T.arriba(P.q(0f, 0f), lerp(C.PAPEL, C.HORMIGON, 0.3f)))
        clipPath(hoja) {
            val gris = T.en(P.q(0f, 0f), lerp(C.HORMIGON, C.TINTA, 0.35f))
            drawLine(gris.copy(alpha = 0.7f), P.q(-15f, -9f, 0.2f), P.q(14f, -10f, 0.2f), 1.4f * k)
            for (col in 0..3) for (fila in 0..9) { val u = -15f + col * 8f; val v = -6f + fila * 1.8f; drawLine(gris.copy(alpha = 0.45f), P.q(u, v, 0.2f), P.q(u + 6.4f, v, 0.2f), 0.35f * k) }
            drawLine(T.sombra(P.q(0f, 0f), C.PAPEL, 0.5f).copy(alpha = 0.5f), P.q(0.5f, -12f, 0.2f), P.q(0.5f, 12f, 0.2f), 0.6f * k)
        }
        // El libro: girado al revés que el periódico; se ven su tapa, su lomo (a la izquierda) y el canto de las hojas.
        val L = M.mas(-1f, 0f, -0.22f); val g = 3.6f
        contacto(L.q(0f, 1f), 13f * k, 4f * k, 0.4f)
        val tela = TAPA_LIBRO
        drawPath(poligono(L.q(-9f, -12f, g), L.q(-9f, 12f, g), L.q(-9f, 12f, 0f), L.q(-9f, -12f, 0f)), T.izq(L.q(-9f, 0f, 2f), lerp(tela, C.TINTA, 0.15f)))
        for (vv in listOf(-8f, 8f)) drawLine(T.izq(L.q(-9f, vv, 2f), C.ORO), L.q(-9.1f, vv, g), L.q(-9.1f, vv, 0f), 0.6f * k)
        val canto = poligono(L.q(-9f, 12f, g), L.q(9f, 12f, g), L.q(9f, 12f, 0f), L.q(-9f, 12f, 0f))
        drawPath(canto, T.frente(L.q(0f, 12f, 2f), C.PAPEL))
        clipPath(canto) { for (i in 1..4) drawLine(T.sombra(L.q(0f, 12f, 2f), C.PAPEL, 0.4f).copy(alpha = 0.5f), L.q(-8.5f, 12f, i * 0.7f), L.q(8.5f, 12f, i * 0.7f), 0.25f * k) }
        drawPath(poligono(L.q(9f, -12f, g), L.q(9f, 12f, g), L.q(9f, 12f, 0f), L.q(9f, -12f, 0f)), T.der(L.q(9f, 0f, 2f), C.PAPEL))
        // La cinta de señal, que sale por abajo y cae en el periódico.
        val c0 = L.q(3f, 12f, 1.5f); val c1 = L.q(5f, 17f, 0.2f)
        drawPath(Path().apply { moveTo(c0.x, c0.y); cubicTo(c0.x, c0.y + 2f * k, c1.x - 2f * k, c1.y - 1f * k, c1.x, c1.y) }, T.en(c1, C.ROJO), style = Stroke(1f * k))
        // La tapa, con el desgaste de las esquinas, el filete y un rombo en seco en medio.
        val tapa = poligono(L.q(-9f, -12f, g), L.q(9.3f, -12f, g), L.q(9.3f, 12.2f, g), L.q(-9f, 12.2f, g))
        drawPath(tapa, T.arriba(L.q(0f, 0f, g), tela))
        val filete = poligono(L.q(-6.5f, -9.5f, g), L.q(7.5f, -9.5f, g), L.q(7.5f, 9.8f, g), L.q(-6.5f, 9.8f, g))
        drawPath(filete, T.arriba(L.q(0f, 0f, g), C.ORO), style = Stroke(0.55f * k))
        drawPath(poligono(L.q(0.5f, -3.5f, g), L.q(3.5f, 0f, g), L.q(0.5f, 3.5f, g), L.q(-2.5f, 0f, g)), T.sombra(L.q(0f, 0f, g), tela, 0.6f))
        drawPath(poligono(L.q(0.5f, -2f, g), L.q(2f, 0f, g), L.q(0.5f, 2f, g), L.q(-1f, 0f, g)), T.arriba(L.q(0f, 0f, g), C.ORO).copy(alpha = 0.7f))
        for (q in listOf(L.q(9f, -12f, g), L.q(9f, 12f, g))) drawCircle(T.arriba(q, lerp(tela, C.PAPEL, 0.4f)), 1f * k, q)
        drawLine(T.filo.copy(alpha = 0.45f), L.q(-9f, -12f, g), L.q(9.3f, -12f, g), 0.5f * k)
    }

    /**
     * El termo chino: la chapa pintada de rojo con peonías y hojas, el cinturón y el pie de aluminio, el hombro, el vaso
     * que hace de tapón con sus estrías, y el asa de alambre al costado.
     */
    private fun DrawScope.termo(T: Tono, M: Marco) {
        val k = M.k; val o = M.q(0f, 0f)
        val r = 6f * k; val ry = r * 0.32f
        contacto(o, r * 1.5f, ry * 1.4f, 0.4f)
        fun tramo(y0: Float, y1: Float, r0: Float, r1: Float): Path = Path().apply {
            moveTo(o.x - r0, o.y - y0); lineTo(o.x - r1, o.y - y1); cubicTo(o.x - r1, o.y - y1 + ry * 1.3f, o.x + r1, o.y - y1 + ry * 1.3f, o.x + r1, o.y - y1)
            lineTo(o.x + r0, o.y - y0); cubicTo(o.x + r0, o.y - y0 + ry * 1.3f, o.x - r0, o.y - y0 + ry * 1.3f, o.x - r0, o.y - y0); close()
        }
        // El pie de aluminio y la chapa.
        val pieT = tramo(0f, 4f * k, r, r)
        with(T) { cuerpo(pieT, o.x - r, o.x + r, o.y - 2f * k, C.CROMO) }
        for (i in 1..2) drawLine(T.sombra(o, C.CROMO, 0.5f).copy(alpha = 0.6f), Offset(o.x - r, o.y - i * 1.4f * k), Offset(o.x + r, o.y - i * 1.4f * k), 0.35f * k)
        val chapa = tramo(4f * k, 29f * k, r, r)
        with(T) { cuerpo(chapa, o.x - r, o.x + r, o.y - 16f * k, CHAPA) }
        clipPath(chapa) {
            // Las peonías, más estrechas hacia los costados (la chapa es redonda), con sus hojas.
            val flores = listOf(Triple(-1.5f, 19f, 3.4f), Triple(3.2f, 11f, 2.8f), Triple(-3.8f, 9f, 2.2f))
            for ((u, h, rr) in flores) {
                val c = Offset(o.x + u * k, o.y - h * k); val ap = 1f - 0.5f * (abs(u) / 6f)
                for (a in listOf(-2.4f, -0.6f, 0.9f, 2.6f)) drawOval(T.en(c, C.VERDE), Offset(c.x + cos(a) * rr * 1.25f * k * ap - 1.6f * k * ap, c.y + sin(a) * rr * 0.9f * k - 0.7f * k), Size(3.2f * k * ap, 1.4f * k))
                drawOval(T.en(c, ROSA), Offset(c.x - rr * k * ap, c.y - rr * k * 0.85f), Size(rr * 2f * k * ap, rr * 1.7f * k))
                drawOval(T.arriba(c, lerp(ROSA, C.PAPEL, 0.55f)), Offset(c.x - rr * 0.65f * k * ap, c.y - rr * k * 0.75f), Size(rr * 1.3f * k * ap, rr * 0.9f * k))
                drawOval(T.sombra(c, ROSA, 0.4f), Offset(c.x - rr * 0.5f * k * ap, c.y), Size(rr * 1f * k * ap, rr * 0.55f * k))
                drawCircle(T.en(c, C.ORO), 0.6f * k, c + Offset(0f, -0.2f * k))
            }
            // Un ramito y un pájaro chico en lo alto.
            drawLine(T.sombra(o, C.VERDE, 0.4f), Offset(o.x - 4f * k, o.y - 6f * k), Offset(o.x + 1f * k, o.y - 24f * k), 0.5f * k)
            val pj = Offset(o.x + 2.6f * k, o.y - 25f * k)
            drawOval(T.en(pj, C.AZUL), Offset(pj.x - 1.6f * k, pj.y - 0.8f * k), Size(3.2f * k, 1.6f * k)); drawLine(T.en(pj, C.AZUL), pj + Offset(1.4f * k, 0f), pj + Offset(2.8f * k, -0.8f * k), 0.6f * k)
            // El brillo de la ventana en la chapa y, de noche, el de la lámpara a la derecha.
            drawRect(T.arriba(o, Color.White).copy(alpha = 0.18f + 0.18f * (1f - T.noche)), Offset(o.x - r * 0.55f, o.y - 29f * k), Size(1.3f * k, 25f * k))
            if (T.kl > 0.05f) drawRect(C.LAMPARA.copy(alpha = (0.3f * T.kl).coerceIn(0f, 1f)), Offset(o.x + r * 0.45f, o.y - 29f * k), Size(1.1f * k, 25f * k), blendMode = BlendMode.Plus)
        }
        // El cinturón de aluminio, el hombro y el vaso-tapón con sus estrías.
        val cint = tramo(28f * k, 30f * k, r, r)
        with(T) { cuerpo(cint, o.x - r, o.x + r, o.y - 29f * k, C.CROMO) }
        val hombro = tramo(30f * k, 33f * k, r, r * 0.72f)
        with(T) { cuerpo(hombro, o.x - r, o.x + r, o.y - 31f * k, CHAPA) }
        val vaso = tramo(33f * k, 40f * k, r * 0.76f, r * 0.8f)
        with(T) { cuerpo(vaso, o.x - r * 0.8f, o.x + r * 0.8f, o.y - 36f * k, C.CROMO) }
        clipPath(vaso) { for (i in -4..4) drawLine(T.sombra(o, C.CROMO, 0.5f).copy(alpha = 0.5f), Offset(o.x + i * r * 0.18f, o.y - 40f * k), Offset(o.x + i * r * 0.18f, o.y - 33f * k), 0.3f * k) }
        drawOval(T.arriba(o, lerp(C.CROMO, Color.White, 0.3f)), Offset(o.x - r * 0.8f, o.y - 40f * k - ry), Size(r * 1.6f, ry * 2f))
        // El asa de alambre al costado derecho.
        val a0 = Offset(o.x + r, o.y - 25f * k); val a1 = Offset(o.x + r, o.y - 9f * k)
        drawPath(Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x + 4.5f * k, a0.y + 1f * k, a1.x + 4.5f * k, a1.y - 1f * k, a1.x, a1.y) }, T.der(a0, C.CROMO), style = Stroke(0.7f * k))
        drawPath(chapa, T.filo.copy(alpha = 0.3f), style = Stroke(0.4f * k))
    }

    /**
     * El pase de la fábrica, abierto en el hule: las tapas rojas, y en las hojas un recuadro en blanco donde iría la foto,
     * el sello redondo violeta que lo pisa, el sello cuadrado y las rayas escritas a mano (sin letras).
     */
    private fun DrawScope.pase(T: Tono, M: Marco) {
        val P = M.mas(0f, 0f, 0.16f); val k = M.k
        contacto(P.q(0f, 1f), 20f * k, 5f * k, 0.35f)
        // Las tapas, un poco más grandes que las hojas.
        val tapas = poligono(P.q(-13.6f, -9.4f, 0.3f), P.q(13.6f, -9.4f, 0.3f), P.q(13.6f, 9.4f, 0.3f), P.q(-13.6f, 9.4f, 0.3f))
        drawPath(tapas, T.arriba(P.q(0f, 0f), lerp(C.ROJO, C.TINTA, 0.25f)))
        drawPath(poligono(P.q(-13.6f, 9.4f, 0.3f), P.q(13.6f, 9.4f, 0.3f), P.q(13.6f, 9.4f, 0f), P.q(-13.6f, 9.4f, 0f)), T.frente(P.q(0f, 9.4f), lerp(C.ROJO, C.TINTA, 0.4f)))
        // Las hojas, que se levantan un poco del lomo: cada una, una tira de trozos.
        fun alza(u: Float): Float = 0.6f + 1.4f * sin((PI * (abs(u) / 12.6f)).toFloat()).coerceAtLeast(0f) * 0.8f
        fun hoja(desde: Float, hasta: Float): Path { val n = 8; val pts = ArrayList<Offset>(); for (i in 0..n) { val u = desde + (hasta - desde) * i / n; pts += P.q(u, -8.6f, alza(u)) }; for (i in n downTo 0) { val u = desde + (hasta - desde) * i / n; pts += P.q(u, 8.6f, alza(u)) }; return poligono(pts) }
        val izq = hoja(-12.6f, -0.2f); val der = hoja(0.2f, 12.6f)
        drawPath(izq, T.izq(P.q(-6f, 0f, 1f), lerp(C.PAPEL, C.LATON, 0.12f)))
        drawPath(der, T.arriba(P.q(6f, 0f, 1f), lerp(C.PAPEL, C.LATON, 0.08f)))
        drawLine(T.sombra(P.q(0f, 0f), C.PAPEL, 0.6f), P.q(0f, -8.6f, 0.5f), P.q(0f, 8.6f, 0.5f), 0.7f * k)
        // La hoja de la izquierda: el recuadro de la foto, vacío, y el sello redondo que lo pisa.
        val foto = poligono(P.q(-10.5f, -6.8f, alza(-10.5f)), P.q(-4.5f, -6.8f, alza(-4.5f)), P.q(-4.5f, 2.6f, alza(-4.5f)), P.q(-10.5f, 2.6f, alza(-10.5f)))
        drawPath(foto, T.en(P.q(-7.5f, -2f, 1f), lerp(C.PAPEL, C.HORMIGON, 0.5f)))
        drawPath(foto, T.sombra(P.q(-7.5f, -2f, 1f), C.PAPEL, 0.6f), style = Stroke(0.4f * k))
        val sc = P.q(-4.6f, 2.4f, alza(-4.6f) + 0.05f)
        drawOval(T.en(sc, SELLO).copy(alpha = 0.8f), Offset(sc.x - 3.4f * k, sc.y - 1.6f * k), Size(6.8f * k, 3.2f * k), style = Stroke(0.45f * k))
        drawOval(T.en(sc, SELLO).copy(alpha = 0.6f), Offset(sc.x - 2.2f * k, sc.y - 1f * k), Size(4.4f * k, 2f * k), style = Stroke(0.3f * k))
        drawPath(estrella(sc, 0.8f * k), T.en(sc, SELLO).copy(alpha = 0.75f))
        for (i in 0..1) drawLine(T.en(P.q(-8f, 5f, 1f), TINTA_AZUL).copy(alpha = 0.7f), P.q(-11f, 4.8f + i * 1.8f, alza(-11f)), P.q(-3f + i * -2f, 4.8f + i * 1.8f, alza(-3f)), 0.35f * k)
        // La hoja de la derecha: las rayas a mano, ondeadas, el sello cuadrado y la firma.
        for (i in 0..4) {
            val v = -6.4f + i * 2.4f; val pth = Path(); var u = 1.8f; val a0 = P.q(u, v, alza(u)); pth.moveTo(a0.x, a0.y)
            val hasta = if (i == 4) 7f else 11.4f
            while (u < hasta) { val u1 = u + 1.2f; val c = P.q(u + 0.6f, v - 0.35f * (if (((u * 3f).toInt() % 2) == 0) 1f else -1f), alza(u)); val e = P.q(u1, v, alza(u1)); pth.cubicTo(c.x, c.y, c.x, c.y, e.x, e.y); u = u1 }
            drawPath(pth, T.en(P.q(6f, v, 1f), TINTA_AZUL).copy(alpha = 0.75f), style = Stroke(0.32f * k))
        }
        drawPath(poligono(P.q(6f, 4.4f, alza(6f)), P.q(11.4f, 4.4f, alza(11.4f)), P.q(11.4f, 7.6f, alza(11.4f)), P.q(6f, 7.6f, alza(6f))), T.en(P.q(9f, 6f, 1f), lerp(C.ROJO, SELLO, 0.4f)).copy(alpha = 0.7f), style = Stroke(0.4f * k))
        val f0 = P.q(2f, 6.6f, alza(2f)); val f1 = P.q(5.4f, 5.6f, alza(5.4f))
        drawPath(Path().apply { moveTo(f0.x, f0.y); cubicTo(f0.x + 1.6f * k, f0.y - 2.6f * k, f1.x - 2f * k, f1.y + 2.4f * k, f1.x, f1.y) }, T.en(f1, TINTA_AZUL), style = Stroke(0.35f * k))
        drawPath(der, T.filo.copy(alpha = 0.3f), style = Stroke(0.4f * k))
    }

    /**
     * La postal de las mimosas sobre su sobre: el cielo, la rama con sus bolitas amarillas y sus hojas de pluma, el lazo
     * rojo y el borde blanco; el sobre, con su sello sin letras y las rayas de la dirección.
     */
    private fun DrawScope.postal(T: Tono, M: Marco) {
        val k = M.k
        // El sobre, echado delante.
        val S = M.mas(2f, 4f, -0.12f)
        contacto(S.q(0f, 1f), 20f * k, 5f * k, 0.3f)
        drawPath(poligono(S.q(-14f, -7f, 0.2f), S.q(14f, -7f, 0.2f), S.q(14f, 7f, 0.2f), S.q(-14f, 7f, 0.2f)), T.arriba(S.q(0f, 0f), lerp(C.PAPEL, C.AZUL, 0.12f)))
        drawPath(poligono(S.q(9f, 1f, 0.25f), S.q(12.5f, 1f, 0.25f), S.q(12.5f, 5.5f, 0.25f), S.q(9f, 5.5f, 0.25f)), T.en(S.q(11f, 3f), C.ROJO))
        drawPath(poligono(S.q(9.7f, 1.8f, 0.3f), S.q(11.8f, 1.8f, 0.3f), S.q(11.8f, 4.7f, 0.3f), S.q(9.7f, 4.7f, 0.3f)), T.en(S.q(11f, 3f), C.PAPEL))
        for (i in 0..2) drawLine(T.en(S.q(0f, 0f), TINTA_AZUL).copy(alpha = 0.55f), S.q(-6f, 1.5f + i * 1.6f, 0.25f), S.q(5f - i * 2f, 1.5f + i * 1.6f, 0.25f), 0.3f * k)
        // El tarro de miel en que se apoya, que asoma por la derecha.
        with(PiezasUrss) { tarro(T.lz, M.q(9f, -8f), 14f * k, lerp(C.ORO, C.LADRILLO, 0.25f), gasa = true) }
        // La postal, de pie y recostada en el tarro: abajo pisa el hule, arriba se apoya.
        val Q = M.mas(-2f, -1f, 0.08f)
        fun p(a: Float, b: Float): Offset = Q.q(-12f + 24f * a, -6f + 7f * b, 15f * (1f - b))
        drawPath(poligono(Q.q(-12f, 1f), Q.q(12f, 1f), Q.q(14f, -8f), Q.q(-10f, -8f)), C.SOMBRA.copy(alpha = 0.18f), blendMode = BlendMode.Multiply)
        drawLine(T.sombra(p(0.5f, 1f), C.PAPEL, 0.6f), p(0f, 1f), p(1f, 1f), 0.8f * k)
        val carta = poligono(p(0f, 0f), p(1f, 0f), p(1f, 1f), p(0f, 1f))
        drawPath(carta, T.frente(p(0.5f, 0.5f), C.PAPEL))
        val dibujo = poligono(p(0.05f, 0.08f), p(0.95f, 0.08f), p(0.95f, 0.92f), p(0.05f, 0.92f))
        val ctr = p(0.5f, 0.5f)
        drawPath(dibujo, Brush.verticalGradient(0f to T.en(ctr, CIELO), 1f to T.en(ctr, lerp(CIELO, C.PAPEL, 0.6f)), startY = p(0.5f, 0.08f).y, endY = p(0.5f, 0.92f).y))
        clipPath(dibujo) {
            // La rama, de abajo a la izquierda hacia arriba a la derecha, con sus ramillas.
            val rama = listOf(0.12f to 0.95f, 0.38f to 0.62f, 0.62f to 0.42f, 0.9f to 0.18f)
            for (i in 0 until rama.size - 1) drawLine(T.en(ctr, lerp(C.MADERA, C.VERDE, 0.4f)), p(rama[i].first, rama[i].second), p(rama[i + 1].first, rama[i + 1].second), 0.8f * k, StrokeCap.Round)
            // Las hojas de pluma: hileras de rayitas verdes.
            for ((a, b, dir) in listOf(Triple(0.3f, 0.72f, -1f), Triple(0.55f, 0.48f, 1f), Triple(0.72f, 0.36f, -1f), Triple(0.45f, 0.6f, 1f))) {
                val c0 = p(a, b); val c1 = p(a + 0.14f * dir, b - 0.22f)
                drawLine(T.en(ctr, C.VERDE), c0, c1, 0.4f * k)
                for (j in 1..5) { val q = c0 + (c1 - c0) * (j / 6f); drawLine(T.en(ctr, C.VERDE), q, q + Offset(1.4f * k, 1f * k), 0.45f * k); drawLine(T.en(ctr, C.VERDE), q, q + Offset(-1.4f * k, 1f * k), 0.45f * k) }
            }
            // Las bolitas de la mimosa, en racimos.
            val az = Azar(Semilla.de("urss", "mimosa"))
            for ((a, b) in listOf(0.42f to 0.5f, 0.62f to 0.3f, 0.78f to 0.36f, 0.3f to 0.55f, 0.86f to 0.12f, 0.5f to 0.38f, 0.7f to 0.52f)) repeat(7) {
                val q = p(a + az.entre(-0.06f, 0.06f), b + az.entre(-0.09f, 0.09f)); val rr = az.entre(0.8f, 1.3f) * k
                drawCircle(T.en(q, MIMOSA), rr, q); drawCircle(T.en(q, lerp(MIMOSA, Color.White, 0.45f)), rr * 0.5f, q + Offset(-rr * 0.3f, -rr * 0.3f))
            }
            // El lazo rojo en el arranque de la rama.
            val l = p(0.22f, 0.82f)
            drawOval(T.en(l, C.ROJO), Offset(l.x - 3f * k, l.y - 1.2f * k), Size(2.8f * k, 2f * k)); drawOval(T.en(l, C.ROJO), Offset(l.x + 0.2f * k, l.y - 1.2f * k), Size(2.8f * k, 2f * k))
            drawLine(T.en(l, C.ROJO), l, l + Offset(-1.6f * k, 2.4f * k), 0.6f * k); drawLine(T.en(l, C.ROJO), l, l + Offset(1.2f * k, 2.6f * k), 0.6f * k)
        }
        drawPath(carta, T.filo.copy(alpha = 0.4f), style = Stroke(0.4f * k))
    }

    /**
     * La radio de cable, descolgada de la pared y puesta en el hule: la cajita de baquelita con su rejilla de tela y la
     * rueda del volumen, el cable que se va hacia la pared y un destornillador al lado (la están arreglando).
     */
    private fun DrawScope.radio(T: Tono, M: Marco) {
        val k = M.k
        // El cable, que sale por detrás y se va por el hule hacia la pared.
        val c0 = M.q(-6f, -4f, 3f); val c1 = M.q(-14f, -14f, 0.2f); val c2 = M.q(-4f, -26f, 0.2f); val c3 = M.q(-12f, -44f, 0.2f)
        val cable = Path().apply { moveTo(c0.x, c0.y); cubicTo(c0.x - 3f * k, c0.y + 2f * k, c1.x - 2f * k, c1.y + 1f * k, c1.x, c1.y); cubicTo(c1.x + 3f * k, c1.y - 1f * k, c2.x - 1f * k, c2.y + 1f * k, c2.x, c2.y); cubicTo(c2.x + 1f * k, c2.y - 1f * k, c3.x + 2f * k, c3.y + 0.5f * k, c3.x, c3.y) }
        drawPath(cable, C.SOMBRA.copy(alpha = 0.25f), style = Stroke(1.2f * k), blendMode = BlendMode.Multiply)
        drawPath(cable, T.en(c1, C.TINTA), style = Stroke(0.7f * k))
        // El destornillador, con su mango de madera roja.
        val d0 = M.q(9f, 9f, 0.8f); val d1 = M.q(18f, 4f, 0.8f); val d2 = M.q(25f, 1f, 0.6f)
        contacto(M.q(16f, 6.5f), 9f * k, 1.2f * k, 0.28f)
        drawLine(T.en(d1, C.CROMO), d1, d2, 0.6f * k, StrokeCap.Round)
        drawLine(T.en(d0, lerp(C.ROJO, C.MADERA, 0.3f)), d0, d1, 2.2f * k, StrokeCap.Round)
        drawLine(T.arriba(d0, lerp(C.ROJO, C.PAPEL, 0.4f)).copy(alpha = 0.8f), d0 + Offset(0f, -0.6f * k), d1 + Offset(0f, -0.6f * k), 0.6f * k, StrokeCap.Round)
        // La caja.
        contacto(M.q(0f, 1.5f), 15f * k, 3.4f * k, 0.42f)
        val frente = caja(T, M, -11f, 11f, -4f, 4f, 0f, 16f, BAQUELITA, lerp(BAQUELITA, C.PAPEL, 0.12f), lerp(BAQUELITA, C.TINTA, 0.15f))
        val a = M.q(-11f, 4f, 16f); val b = M.q(11f, 4f, 0f); val ancho = b.x - a.x; val alto = b.y - a.y
        clipPath(frente) {
            // La rejilla de tela, con sus listones de baquelita.
            val rj = redondo(Offset(a.x + ancho * 0.08f, a.y + alto * 0.14f), Offset(a.x + ancho * 0.62f, b.y - alto * 0.14f), 2f * k)
            drawPath(rj, T.frente(M.q(-3f, 4f, 8f), TELA))
            clipPath(rj) { for (i in 0..5) { val y = a.y + alto * (0.2f + i * 0.13f); drawLine(T.frente(M.q(-3f, 4f, 8f), lerp(BAQUELITA, C.TINTA, 0.2f)), Offset(a.x, y), Offset(b.x, y), 1f * k); drawLine(T.arriba(M.q(-3f, 4f, 8f), lerp(BAQUELITA, C.PAPEL, 0.3f)).copy(alpha = 0.6f), Offset(a.x, y - 0.6f * k), Offset(b.x, y - 0.6f * k), 0.3f * k) } }
            drawPath(rj, T.sombra(M.q(-3f, 4f, 8f), BAQUELITA, 0.6f), style = Stroke(0.5f * k))
            // La rueda del volumen, con su raya clara, y el brillo de la baquelita.
            val rueda = Offset(a.x + ancho * 0.8f, a.y + alto * 0.5f)
            drawCircle(T.sombra(rueda, C.TINTA, 0.4f), 3.4f * k, rueda + Offset(0.4f * k, 0.5f * k))
            drawCircle(T.en(rueda, C.TINTA), 3.1f * k, rueda)
            drawCircle(T.arriba(rueda, lerp(C.TINTA, C.PAPEL, 0.35f)).copy(alpha = 0.7f), 3.1f * k, rueda, style = Stroke(0.4f * k))
            drawLine(T.arriba(rueda, C.PAPEL), rueda, rueda + Offset(1.2f * k, -2.4f * k), 0.5f * k)
            drawLine(T.arriba(rueda, Color.White).copy(alpha = 0.3f), Offset(a.x + 1f * k, a.y + 1f * k), Offset(b.x - 1f * k, a.y + 1f * k), 0.6f * k)
        }
        // El ojal de colgar, en lo alto de atrás.
        val oj = M.q(0f, -3f, 16.6f)
        drawOval(T.arriba(oj, C.CROMO), Offset(oj.x - 1.4f * k, oj.y - 1.2f * k), Size(2.8f * k, 1.6f * k), style = Stroke(0.4f * k))
    }

    /**
     * El samovar, que ese día viene delante: en su bandeja negra de flores pintadas (con su filo dorado), con la tetera
     * encima, el platillo de la mermelada y la cucharilla; si está enchufado, con su vaho quieto sobre la tetera.
     */
    private fun DrawScope.samovar(T: Tono, M: Marco, m: Momento) {
        val k = M.k
        // La bandeja: el borde levantado, el fondo negro y las flores pintadas.
        contacto(M.q(0f, 1f), 24f * k, 5f * k, 0.32f)
        val borde = ovalo(M, 0f, 0f, 21f, 14f, 0.8f)
        drawPath(borde, T.arriba(M.q(0f, 0f), lerp(C.TINTA, C.LATON, 0.2f)))
        drawPath(ovalo(M, 0f, 0.6f, 21f, 14f, 0f), T.frente(M.q(0f, 14f), C.TINTA))
        drawPath(borde, T.arriba(M.q(0f, 0f), lerp(C.TINTA, C.LATON, 0.2f)))
        val fondo = ovalo(M, 0f, 0f, 19f, 12.4f, 0.8f)
        drawPath(fondo, T.arriba(M.q(0f, 0f), C.TINTA))
        drawPath(ovalo(M, 0f, 0f, 17.6f, 11.3f, 0.8f), T.arriba(M.q(0f, 0f), C.ORO).copy(alpha = 0.8f), style = Stroke(0.35f * k))
        // Las rosas de la bandeja, por delante (lo de atrás lo tapa el samovar).
        for ((u, v, r) in listOf(Triple(-11f, 5f, 2.6f), Triple(-5f, 8.5f, 2f), Triple(8f, 8f, 2.4f), Triple(13f, 3.5f, 1.8f))) {
            val q = M.q(u, v, 0.8f)
            for (a in listOf(-0.4f, 2.2f, 3.6f)) drawOval(T.arriba(q, C.VERDE), Offset(q.x + cos(a) * r * 1.3f * k - 1.4f * k, q.y + sin(a) * r * 0.5f * k - 0.5f * k), Size(2.8f * k, 1f * k))
            drawOval(T.arriba(q, C.ROJO), Offset(q.x - r * k, q.y - r * 0.5f * k), Size(r * 2f * k, r * k))
            drawOval(T.arriba(q, lerp(C.ROJO, C.PAPEL, 0.45f)), Offset(q.x - r * 0.55f * k, q.y - r * 0.4f * k), Size(r * 1.1f * k, r * 0.5f * k))
            drawCircle(T.arriba(q, C.PAPEL), 0.5f * k, q + Offset(r * 1.4f * k, -r * 0.3f * k))
        }
        drawPath(borde, T.filo.copy(alpha = 0.4f), style = Stroke(0.5f * k))
        // El platillo de la mermelada, de cristal, con su cucharilla.
        val pm = M.q(-13f, 3f, 0.8f)
        drawPath(ovalo(M, -13f, 3f, 3.6f, 3.6f, 1.6f), T.arriba(pm, lerp(C.CROMO, C.PAPEL, 0.4f)).copy(alpha = 0.8f))
        drawPath(ovalo(M, -13f, 3f, 2.6f, 2.6f, 1.7f), T.en(pm, lerp(C.ROJO, C.LADRILLO, 0.3f)))
        drawLine(T.arriba(pm, C.CROMO), M.q(-13f, 3f, 2f), M.q(-6f, 7f, 1f), 0.5f * k)
        // El samovar y su tetera: la pieza de la cocina, más cerca y más grande.
        val ps = M.q(1f, -2f, 0.8f)
        with(PiezasUrss) { samovar(T.lz, ps, k * 0.98f) }
        // Lo que no lleva la pieza: el lado de la lámpara, más naranja, y el de la ventana, azul de noche.
        val y0 = ps.y - 10f * k; val y1 = ps.y - 34f * k
        if (T.kl > 0.05f) mancha(Offset(ps.x + 8f * k, (y0 + y1) / 2f), 5f * k, 11f * k, C.LAMPARA, 0.22f * T.kl)
        // Su vaho, quieto, sobre la tapa de la tetera (el que se mueve es el del samovar de la mesa, que hoy no está).
        if (EscenaUrss.samovarEnchufado(m)) {
            val v0 = Offset(ps.x, y1 - 12f * k); val tinte = if (T.kl > 0.1f) lerp(C.LAMPARA, Color.White, 0.55f) else lerp(C.PAPEL, C.LUNA, 0.3f * T.noche)
            for (i in 0..4) mancha(v0 + Offset(sin(i * 1.7f) * 3f * k + i * 1.2f * k, -i * 6f * k), (3f + i * 1.8f) * k, (2.6f + i * 1.4f) * k, tinte, 0.34f * (1f - i / 6f))
        }
    }

    // =================================================================
    //  Los grandes
    // =================================================================

    /**
     * El televisor, traído del cuarto y puesto en el taburete: el mueble de chapa de madera, la pantalla redondeada (de
     * día, apagada, con la ventana reflejada; de noche, encendida con nieve: puntos y rayas, sin imagen), las dos ruedas,
     * el altavoz de tela, el tapete de ganchillo encima y la antena de cuernos.
     */
    private fun DrawScope.televisor(T: Tono, M: Marco, m: Momento) {
        val k = M.k; val on = EscenaUrss.televisor(m)
        contacto(M.q(0f, 2f), 18f * k, 4f * k, 0.4f)
        val frente = caja(T, M, -15f, 15f, -11f, 11f, 0f, 26f, NOGAL, lerp(NOGAL, C.TINTA, 0.1f), lerp(NOGAL, C.TINTA, 0.2f))
        val a = M.q(-15f, 11f, 26f); val b = M.q(15f, 11f, 0f)
        val ancho = b.x - a.x; val alto = b.y - a.y
        // La veta de la chapa.
        clipPath(frente) { for (i in 0..6) drawLine(T.sombra(a, NOGAL, 0.5f).copy(alpha = 0.3f), Offset(a.x, a.y + alto * (0.1f + i * 0.14f)), Offset(b.x, a.y + alto * (0.13f + i * 0.13f)), 0.35f * k) }
        // La pantalla: el marco hundido, el cristal abombado.
        val pa = Offset(a.x + ancho * 0.06f, a.y + alto * 0.1f); val pb = Offset(a.x + ancho * 0.72f, b.y - alto * 0.12f)
        drawPath(redondo(pa - Offset(1f * k, 1f * k), pb + Offset(1f * k, 1f * k), 4f * k), T.sombra(a, lerp(C.TINTA, NOGAL, 0.3f), 0.6f))
        val pantalla = redondo(pa, pb, 3.6f * k)
        val cp = Offset((pa.x + pb.x) / 2f, (pa.y + pb.y) / 2f)
        if (on) {
            // Encendida, sin señal: la nieve. Es luz: su color es el suyo.
            drawPath(pantalla, Brush.radialGradient(listOf(lerp(C.TELE, Color.White, 0.55f), lerp(C.TELE, C.HIERRO, 0.25f)), cp, (pb.x - pa.x) * 0.7f))
            clipPath(pantalla) {
                val az = Azar(Semilla.de("urss", "nieve de la tele"))
                repeat(260) { val q = Offset(pa.x + az.real() * (pb.x - pa.x), pa.y + az.real() * (pb.y - pa.y)); val c = if (az.real() < 0.5f) Color.White else lerp(C.HIERRO, C.TELE, 0.3f); drawRect(c.copy(alpha = az.entre(0.3f, 0.8f)), q, Size(az.entre(0.5f, 1.6f) * k, 0.45f * k)) }
                for (i in 0..5) { val y = pa.y + (pb.y - pa.y) * (0.1f + i * 0.17f); drawRect(Color.White.copy(alpha = 0.12f), Offset(pa.x, y), Size(pb.x - pa.x, 0.8f * k)) }
            }
        } else {
            drawPath(pantalla, Brush.radialGradient(listOf(T.en(cp, lerp(CRISTAL, C.PAPEL, 0.12f)), T.sombra(cp, CRISTAL, 0.7f)), cp, (pb.x - pa.x) * 0.7f))
            clipPath(pantalla) {
                // La ventana reflejada, con su cruz, y el brillo curvo del cristal.
                val r0 = Offset(pa.x + (pb.x - pa.x) * 0.12f, pa.y + (pb.y - pa.y) * 0.1f); val r1 = Offset(pa.x + (pb.x - pa.x) * 0.55f, pa.y + (pb.y - pa.y) * 0.55f)
                drawRect(T.arriba(cp, Color.White).copy(alpha = 0.16f + 0.1f * (1f - T.noche)), r0, Size(r1.x - r0.x, r1.y - r0.y))
                drawLine(T.sombra(cp, CRISTAL, 0.5f).copy(alpha = 0.5f), Offset((r0.x + r1.x) / 2f, r0.y), Offset((r0.x + r1.x) / 2f, r1.y), 0.8f * k)
                drawLine(T.sombra(cp, CRISTAL, 0.5f).copy(alpha = 0.5f), Offset(r0.x, r0.y + (r1.y - r0.y) * 0.35f), Offset(r1.x, r0.y + (r1.y - r0.y) * 0.35f), 0.8f * k)
                drawPath(Path().apply { moveTo(pb.x - 3f * k, pa.y + 3f * k); cubicTo(pb.x - 1f * k, pa.y + 8f * k, pb.x - 1f * k, pb.y - 8f * k, pb.x - 3f * k, pb.y - 3f * k) }, T.filo.copy(alpha = 0.35f), style = Stroke(0.8f * k))
            }
        }
        drawPath(pantalla, T.filo.copy(alpha = 0.3f), style = Stroke(0.4f * k))
        // Las dos ruedas y el altavoz de tela, a la derecha.
        for (i in 0..1) {
            val c = Offset(a.x + ancho * 0.86f, a.y + alto * (0.2f + i * 0.2f))
            drawCircle(T.sombra(c, C.TINTA, 0.4f), 2.4f * k, c + Offset(0.3f * k, 0.4f * k)); drawCircle(T.der(c, lerp(C.TINTA, C.CROMO, 0.25f)), 2.1f * k, c)
            drawCircle(T.arriba(c, C.CROMO).copy(alpha = 0.8f), 2.1f * k, c, style = Stroke(0.4f * k)); drawLine(T.arriba(c, C.PAPEL), c, c + Offset(0f, -1.6f * k), 0.4f * k)
        }
        val al = Rect(a.x + ancho * 0.78f, a.y + alto * 0.58f, a.x + ancho * 0.95f, b.y - alto * 0.12f)
        drawRect(T.frente(Offset(al.center.x, al.center.y), TELA), al.topLeft, al.size)
        for (i in 0..5) drawLine(T.sombra(Offset(al.center.x, al.center.y), TELA, 0.4f).copy(alpha = 0.6f), Offset(al.left, al.top + al.height * i / 6f), Offset(al.right, al.top + al.height * i / 6f), 0.3f * k)
        // Las patitas.
        for (u in listOf(-12f, 12f)) drawRect(T.sombra(M.q(u, 11f, 0f), C.TINTA, 0.5f), M.q(u, 11f, 0f) + Offset(-1.2f * k, -0.6f * k), Size(2.4f * k, 1.2f * k))
        // El tapete de ganchillo, que cuelga un poco por delante con sus picos.
        val tp = Path().apply {
            val p0 = M.q(-10f, -8f, 26.1f); moveTo(p0.x, p0.y)
            for (q in listOf(M.q(10f, -8f, 26.1f), M.q(11f, 11f, 26.1f))) lineTo(q.x, q.y)
            var u = 11f
            while (u > -11f) { val e = M.q(u - 2.2f, 11f, 26.1f); val c = M.q(u - 1.1f, 11f, 23.6f); cubicTo(c.x, c.y, c.x, c.y, e.x, e.y); u -= 2.2f }
            close()
        }
        drawPath(tp, T.arriba(M.q(0f, 0f, 26f), C.PAPEL))
        clipPath(tp) { for (i in 0..5) for (j in 0..3) { val q = M.q(-8f + i * 3.2f, -5f + j * 4.4f, 26.1f); drawCircle(T.sombra(q, C.PAPEL, 0.4f).copy(alpha = 0.7f), 0.5f * k, q) } }
        // La antena de cuernos: la base negra y las dos varillas abiertas, con sus bolitas.
        val ba = M.q(3f, -3f, 26f)
        drawOval(T.en(ba, C.TINTA), Offset(ba.x - 3.4f * k, ba.y - 2.6f * k), Size(6.8f * k, 3.4f * k))
        drawOval(T.arriba(ba, lerp(C.TINTA, C.PAPEL, 0.3f)).copy(alpha = 0.6f), Offset(ba.x - 2.4f * k, ba.y - 2.4f * k), Size(4.4f * k, 1.4f * k))
        for ((dx, dy) in listOf(-15f to -34f, 13f to -36f)) {
            val tip = ba + Offset(dx * k, dy * k)
            drawLine(T.izq(tip, C.CROMO), ba + Offset(0f, -2f * k), tip, 0.7f * k, StrokeCap.Round)
            drawLine(T.der(tip, lerp(C.CROMO, Color.White, 0.5f)).copy(alpha = 0.7f), ba + Offset(0.3f * k, -2f * k), tip + Offset(0.3f * k, 0f), 0.3f * k)
            drawCircle(T.en(tip, C.CROMO), 0.8f * k, tip)
        }
        if (on) {
            // La luz de la pantalla: en el aire delante, en el tapete y en el suelo, al pie del taburete.
            mancha(cp, (pb.x - pa.x) * 0.9f, (pb.y - pa.y) * 0.9f, C.TELE, 0.18f)
            drawCircle(Brush.radialGradient(listOf(C.TELE.copy(alpha = 0.14f), Color.Transparent), M.q(0f, 30f, -36f), 40f * k), 40f * k, M.q(0f, 30f, -36f), blendMode = BlendMode.Plus)
        }
    }

    /**
     * El abrigo de paño, echado en el taburete por los hombros: los hombros doblados en el asiento, el delantero que
     * cuelga por delante, abierto en pico, con el forro, el cuello de piel de conejo alrededor, los botones y el bolsillo,
     * y las dos mangas que caen a los lados con sus puños.
     */
    private fun DrawScope.abrigo(T: Tono, M: Marco) {
        val k = M.k; val vF = 18f; val top = 1f; val baja = -32f
        contacto(M.q(0f, 2f, 0f), 22f * k, 5f * k, 0.35f)
        val forro = lerp(C.ROJO, C.MADERA, 0.55f)
        // Los hombros, doblados en el asiento (detrás de todo).
        val hom = Path().apply {
            val p0 = M.q(-20f, vF, top); moveTo(p0.x, p0.y)
            val q1 = M.q(-19f, -4f, 7f); val q2 = M.q(0f, -12f, 8f); val q3 = M.q(19f, -4f, 7f); val p1 = M.q(20f, vF, top)
            cubicTo(p0.x - 2f * k, p0.y - 6f * k, q1.x - 2f * k, q1.y + 2f * k, q1.x, q1.y); cubicTo(q1.x + 4f * k, q1.y - 3f * k, q2.x - 8f * k, q2.y, q2.x, q2.y)
            cubicTo(q2.x + 8f * k, q2.y, q3.x - 4f * k, q3.y - 3f * k, q3.x, q3.y); cubicTo(q3.x + 2f * k, q3.y + 2f * k, p1.x + 2f * k, p1.y - 6f * k, p1.x, p1.y); close()
        }
        drawPath(hom, Brush.verticalGradient(0f to T.arriba(M.q(0f, -8f, 8f), lerp(PANO, C.PAPEL, 0.08f)), 1f to T.frente(M.q(0f, 12f, 2f), PANO), startY = hom.getBounds().top, endY = hom.getBounds().bottom))
        // Las mangas, que caen a los lados, un poco hacia fuera, con su puño y la boca oscura.
        for (lado in listOf(-1f, 1f)) {
            val m0 = M.q(lado * 15.5f, vF - 2f, 3f); val m1 = M.q(lado * 18.5f, vF + 1f, if (lado < 0f) -27f else -23f)
            val manga = Path().apply {
                moveTo(m0.x - 4.6f * k, m0.y); cubicTo(m0.x - 5.4f * k, m0.y + 10f * k, m1.x - 5f * k, m1.y - 10f * k, m1.x - 4.6f * k, m1.y)
                lineTo(m1.x + 4.6f * k, m1.y + lado * 0.8f * k); cubicTo(m1.x + 4.6f * k, m1.y - 10f * k, m0.x + 5f * k, m0.y + 8f * k, m0.x + 4.6f * k, m0.y); close()
            }
            with(T) { cuerpo(manga, m1.x - 5f * k, m1.x + 5f * k, (m0.y + m1.y) / 2f, PANO) }
            clipPath(manga) { drawLine(T.sombra(m0, PANO, 0.6f).copy(alpha = 0.6f), m0 + Offset(-lado * 1.4f * k, 4f * k), m1 + Offset(-lado * 1.4f * k, -4f * k), 0.8f * k) }
            drawLine(T.arriba(m1, lerp(PANO, C.PAPEL, 0.12f)), m1 + Offset(-4.6f * k, -3.4f * k), m1 + Offset(4.6f * k, -3.4f * k + lado * 0.8f * k), 1.4f * k)
            drawOval(T.sombra(m1, C.TINTA, 0.7f), Offset(m1.x - 4f * k, m1.y - 1.2f * k), Size(8f * k, 2.6f * k))
        }
        // El cuerpo que cuelga por delante, con su vuelo y su bajo.
        val del = Path().apply {
            val a0 = M.q(-11.5f, vF, top); moveTo(a0.x, a0.y)
            val a1 = M.q(11.5f, vF, top); lineTo(a1.x, a1.y)
            val b1 = M.q(13.5f, vF, baja); cubicTo(a1.x + 0.6f * k, a1.y + 14f * k, b1.x, b1.y - 14f * k, b1.x, b1.y)
            var u = 13.5f; var sg = 1f
            while (u > -13.5f) { val e = M.q(u - 4f, vF, baja + (if (sg > 0f) 1f else 0f)); val c = M.q(u - 2f, vF, baja - 1.4f * sg); cubicTo(c.x, c.y, c.x, c.y, e.x, e.y); u -= 4.5f; sg = -sg }
            val b0 = M.q(-13.5f, vF, baja); cubicTo(b0.x, b0.y - 14f * k, a0.x - 0.6f * k, a0.y + 14f * k, a0.x, a0.y); close()
        }
        // La sombra que el cuerpo echa en las mangas, a cada lado.
        for (lado in listOf(-1f, 1f)) drawLine(C.SOMBRA.copy(alpha = 0.45f), M.q(lado * 12.4f, vF, 0f), M.q(lado * 14.6f, vF, baja + 6f), 2.4f * k, blendMode = BlendMode.Multiply)
        with(T) { cuerpo(del, M.q(-14f, vF, 0f).x, M.q(14f, vF, 0f).x, M.q(0f, vF, -14f).y, PANO) }
        clipPath(del) {
            // Los pliegues, con su lomo de luz.
            for (u in listOf(-10f, 9f)) { val pa = M.q(u, vF, -2f); val pb = M.q(u * 1.12f, vF, baja); drawLine(T.sombra(pa, PANO, 0.65f).copy(alpha = 0.7f), pa, pb, 1.2f * k); drawLine(T.arriba(pa, lerp(PANO, C.PAPEL, 0.15f)).copy(alpha = 0.55f), pa + Offset(1.4f * k, 0f), pb + Offset(1.6f * k, 0f), 0.6f * k) }
            // El pico abierto: el forro de raso.
            val pico = poligono(M.q(-7f, vF, top), M.q(7f, vF, top), M.q(0.6f, vF, -13f))
            drawPath(pico, Brush.verticalGradient(0f to T.sombra(M.q(0f, vF, 0f), forro, 0.7f), 1f to T.frente(M.q(0f, vF, -10f), forro), startY = M.q(0f, vF, top).y, endY = M.q(0f, vF, -13f).y))
            drawLine(T.arriba(M.q(0f, vF, -6f), lerp(forro, C.PAPEL, 0.4f)).copy(alpha = 0.6f), M.q(-2f, vF, -2f), M.q(0f, vF, -10f), 0.5f * k)
            // El cruce de los delanteros, los botones y el bolsillo con su cartera.
            drawLine(T.sombra(M.q(1f, vF, -20f), PANO, 0.55f), M.q(0.6f, vF, -13f), M.q(1.4f, vF, baja), 0.8f * k)
            for (i in 0..2) { val q = M.q(4f, vF, -16f - i * 6.5f); drawCircle(T.sombra(q, C.TINTA, 0.4f), 1.5f * k, q + Offset(0.3f * k, 0.4f * k)); drawCircle(T.en(q, lerp(C.TINTA, C.MADERA, 0.35f)), 1.3f * k, q); drawCircle(T.arriba(q, C.PAPEL).copy(alpha = 0.5f), 0.4f * k, q + Offset(-0.4f * k, -0.4f * k)) }
            drawPath(poligono(M.q(-11f, vF, -19f), M.q(-4f, vF, -19f), M.q(-4f, vF, -22f), M.q(-11f, vF, -22f)), T.frente(M.q(-7f, vF, -20f), lerp(PANO, C.TINTA, 0.18f)))
            drawLine(T.arriba(M.q(-7f, vF, -19f), lerp(PANO, C.PAPEL, 0.2f)), M.q(-11f, vF, -19f), M.q(-4f, vF, -19f), 0.5f * k)
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.35f), startY = M.q(0f, vF, -22f).y, endY = M.q(0f, vF, baja).y), M.q(-20f, vF, -22f), Size(40f * k, M.q(0f, vF, baja - 2f).y - M.q(0f, vF, -22f).y), blendMode = BlendMode.Multiply)
        }
        // El cuello de piel: alrededor del pico y por encima del canto del asiento, con su pelo.
        val cu = Path().apply {
            val a0 = M.q(-9.5f, vF - 3f, 4.5f); moveTo(a0.x, a0.y)
            val a1 = M.q(9.5f, vF - 3f, 4.5f); val c0 = M.q(0f, vF - 6f, 6.5f); cubicTo(c0.x - 8f * k, c0.y, c0.x + 8f * k, c0.y, a1.x, a1.y)
            val t1 = M.q(6.5f, vF, 0f); val pk = M.q(0.6f, vF, -14.5f); cubicTo(a1.x + 1f * k, a1.y + 4f * k, t1.x + 1f * k, t1.y, t1.x, t1.y); lineTo(pk.x, pk.y)
            val t0 = M.q(-6.5f, vF, 0f); lineTo(t0.x, t0.y); cubicTo(t0.x - 1f * k, t0.y, a0.x - 1f * k, a0.y + 4f * k, a0.x, a0.y); close()
            val pi = M.q(0.6f, vF, -9f); val ti = M.q(4.5f, vF, top); val tj = M.q(-4.5f, vF, top)
            moveTo(tj.x, tj.y); lineTo(ti.x, ti.y); lineTo(pi.x, pi.y); close()
        }
        cu.fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        val cb = cu.getBounds()
        drawPath(cu, Brush.verticalGradient(0f to T.arriba(M.q(0f, vF - 4f, 6f), lerp(PIEL, C.PAPEL, 0.2f)), 1f to T.frente(M.q(0f, vF, -8f), PIEL), startY = cb.top, endY = cb.bottom))
        val az = Azar(Semilla.de("urss", "piel del abrigo"))
        clipPath(cu) { repeat(110) { val q = Offset(az.entre(cb.left, cb.right), az.entre(cb.top, cb.bottom)); drawLine(T.en(q, if (az.real() < 0.5f) lerp(PIEL, C.PAPEL, 0.45f) else lerp(PIEL, C.TINTA, 0.3f)).copy(alpha = 0.7f), q, q + Offset(az.entre(-1f, 1f) * k, 1.6f * k), 0.4f * k) } }
        drawPath(cu, T.filo.copy(alpha = 0.3f), style = Stroke(0.5f * k))
    }

    /**
     * Las botas de fieltro: una de pie con su chanclo de goma puesto (negro, brillante, forrado de rojo), la otra
     * recostada en la pata de la mesa, y el otro chanclo caído delante, de lado, enseñando el forro. En invierno, con la
     * nieve que se derrite en un charquito.
     */
    private fun DrawScope.botas(T: Tono, M0: Marco, m: Momento) {
        val M = M0.mas(5f, -9f)
        val k = M.k; val nieve = EscenaUrss.nieve(m) > 0.3f || EscenaUrss.frio(m) > 0.4f
        // El charquito y la sombra blanda.
        if (nieve) mancha(M.q(4f, 4f), 26f * k, 4f * k, T.en(M.q(4f, 4f), lerp(C.LINOLEO, C.TINTA, 0.45f)), 0.45f)
        contacto(M.q(2f, 2f), 22f * k, 4f * k, 0.38f)
        // La segunda bota, recostada en la pata de la mesa (detrás), de perfil hacia la izquierda.
        bota(T, M.q(13f, -3f), k * 0.97f, 0.3f, false, nieve)
        // La primera, de pie, con su chanclo puesto.
        bota(T, M.q(-3f, 1f), k, 0f, true, nieve)
        // El otro chanclo, caído de lado delante: la suela, la goma brillante y el forro rojo.
        val ch = M.q(22f, 3f)
        contacto(ch, 8f * k, 2f * k, 0.4f)
        val s = Path().apply { moveTo(ch.x - 9f * k, ch.y); cubicTo(ch.x - 10f * k, ch.y - 5f * k, ch.x - 3f * k, ch.y - 7f * k, ch.x + 4f * k, ch.y - 6f * k); cubicTo(ch.x + 9f * k, ch.y - 5f * k, ch.x + 10f * k, ch.y - 1f * k, ch.x + 9f * k, ch.y); close() }
        drawPath(s, Brush.horizontalGradient(0f to T.izq(ch, GOMA), 0.6f to T.frente(ch, GOMA), 1f to T.der(ch, lerp(GOMA, C.CROMO, 0.4f)), startX = ch.x - 9f * k, endX = ch.x + 9f * k))
        drawOval(T.en(ch, lerp(C.ROJO, C.TINTA, 0.15f)), Offset(ch.x - 5f * k, ch.y - 6.6f * k), Size(10f * k, 3.4f * k))
        drawOval(T.sombra(ch, C.ROJO, 0.6f), Offset(ch.x - 3.6f * k, ch.y - 5.6f * k), Size(7f * k, 2f * k))
        drawPath(Path().apply { moveTo(ch.x - 7f * k, ch.y - 3f * k); cubicTo(ch.x - 4f * k, ch.y - 6.2f * k, ch.x + 4f * k, ch.y - 6.6f * k, ch.x + 8f * k, ch.y - 3.6f * k) }, T.filo.copy(alpha = 0.55f), style = Stroke(0.6f * k))
        drawLine(T.sombra(ch, C.TINTA, 0.5f), Offset(ch.x - 9f * k, ch.y - 0.4f * k), Offset(ch.x + 9f * k, ch.y - 0.4f * k), 0.9f * k)
        if (nieve) drawOval(lerp(T.en(ch, C.NIEVE), C.LUNA, 0.2f).copy(alpha = 0.7f), Offset(ch.x + 3f * k, ch.y - 4f * k), Size(5f * k, 2f * k))
    }

    /** Una bota de fieltro de perfil, la puntera a la izquierda, que pisa en [pie] y se ladea [ang] (positivo, la caña hacia la derecha). */
    private fun DrawScope.bota(T: Tono, pie: Offset, k: Float, ang: Float, chanclo: Boolean, nieve: Boolean) {
        val ca = cos(ang); val sa = sin(ang)
        fun q(x: Float, y: Float): Offset = Offset(pie.x + (x * ca - y * sa) * k, pie.y + (x * sa + y * ca) * k)
        // La caña (de x −3 a 9) y el pie, con la puntera redonda a la izquierda.
        val bota = Path().apply {
            val p0 = q(9.5f, 0f); moveTo(p0.x, p0.y)
            val p1 = q(9f, -34f); lineTo(p1.x, p1.y)
            val p2 = q(-2.6f, -35f); cubicTo(q(6f, -36f).x, q(6f, -36f).y, q(0f, -36f).x, q(0f, -36f).y, p2.x, p2.y)
            val p3 = q(-2f, -11f); cubicTo(q(-2.4f, -26f).x, q(-2.4f, -26f).y, q(-1.6f, -15f).x, q(-1.6f, -15f).y, p3.x, p3.y)
            val p4 = q(-11f, -1f); cubicTo(q(-6f, -9f).x, q(-6f, -9f).y, q(-12f, -7f).x, q(-12f, -7f).y, p4.x, p4.y)
            val p5 = q(-9f, 0f); lineTo(p5.x, p5.y); close()
        }
        val c = q(3f, -16f)
        drawPath(bota, Brush.horizontalGradient(0f to T.izq(c, lerp(FIELTRO, Color.White, 0.08f)), 0.45f to T.frente(c, FIELTRO), 0.8f to T.der(c, FIELTRO), 1f to T.der(c, lerp(FIELTRO, Color.White, 0.15f)), startX = q(-11f, 0f).x, endX = q(10f, 0f).x))
        clipPath(bota) {
            // El fieltro: motas y el desgaste, y el doblez del tobillo.
            val az = Azar(Semilla.de("urss", "fieltro", (ang * 100f).toInt()))
            repeat(60) { val o = q(az.entre(-10f, 9f), az.entre(-34f, 0f)); drawCircle((if (az.real() < 0.5f) T.sombra(o, FIELTRO, 0.5f) else T.arriba(o, lerp(FIELTRO, C.PAPEL, 0.3f))).copy(alpha = 0.45f), az.entre(0.3f, 0.6f) * k, o) }
            for (j in 0..2) drawLine(T.sombra(c, FIELTRO, 0.55f).copy(alpha = 0.6f), q(-2f, -12f - j * 2.2f), q(8f, -11f - j * 2.4f), 0.5f * k)
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to C.SOMBRA.copy(alpha = 0.3f), startY = q(0f, -8f).y, endY = q(0f, 0f).y), Offset(q(-12f, 0f).x - 4f * k, q(0f, -8f).y), Size(26f * k, 9f * k), blendMode = BlendMode.Multiply)
        }
        // La boca de la caña, con el fieltro grueso y claro del borde.
        drawOval(T.sombra(q(3.4f, -34.6f), C.TINTA, 0.5f), Rect(q(-2.6f, -36f).x, q(-2.6f, -36f).y - 1.2f * k, q(9.2f, -34f).x, q(9.2f, -34f).y + 0.6f * k))
        drawLine(T.arriba(q(3f, -35f), lerp(FIELTRO, C.PAPEL, 0.35f)), q(-2.6f, -35f), q(9.2f, -34f), 1f * k)
        drawPath(bota, T.filo.copy(alpha = 0.3f), style = Stroke(0.4f * k))
        if (chanclo) {
            // El chanclo puesto: la goma negra hasta el empeine, brillante, con el filo rojo del forro.
            val ch = Path().apply {
                val p0 = q(10f, 0.4f); moveTo(p0.x, p0.y); val p1 = q(9.8f, -6f); lineTo(p1.x, p1.y)
                cubicTo(q(6f, -5f).x, q(6f, -5f).y, q(0f, -5f).x, q(0f, -5f).y, q(-3f, -6.4f).x, q(-3f, -6.4f).y)
                cubicTo(q(-7f, -7.6f).x, q(-7f, -7.6f).y, q(-12.6f, -5f).x, q(-12.6f, -5f).y, q(-11.6f, 0.4f).x, q(-11.6f, 0.4f).y); close()
            }
            drawPath(ch, Brush.horizontalGradient(0f to T.izq(q(-6f, -3f), GOMA), 0.55f to T.frente(q(0f, -3f), GOMA), 1f to T.der(q(8f, -3f), lerp(GOMA, C.CROMO, 0.35f)), startX = q(-12f, 0f).x, endX = q(10f, 0f).x))
            drawPath(Path().apply { moveTo(q(9.8f, -6f).x, q(9.8f, -6f).y); cubicTo(q(6f, -5f).x, q(6f, -5f).y, q(0f, -5f).x, q(0f, -5f).y, q(-3f, -6.4f).x, q(-3f, -6.4f).y); cubicTo(q(-7f, -7.6f).x, q(-7f, -7.6f).y, q(-12.6f, -5f).x, q(-12.6f, -5f).y, q(-11.8f, -1f).x, q(-11.8f, -1f).y) }, T.en(q(0f, -5f), C.ROJO), style = Stroke(0.6f * k))
            drawPath(Path().apply { moveTo(q(-9f, -5.4f).x, q(-9f, -5.4f).y); cubicTo(q(-5f, -6f).x, q(-5f, -6f).y, q(-1f, -4.6f).x, q(-1f, -4.6f).y, q(6f, -4f).x, q(6f, -4f).y) }, T.filo.copy(alpha = 0.6f), style = Stroke(0.6f * k))
            drawLine(T.sombra(pie, C.TINTA, 0.5f), q(-11f, -0.3f), q(10f, -0.3f), 0.9f * k)
            if (nieve) drawOval(lerp(T.en(pie, C.NIEVE), C.LUNA, 0.2f).copy(alpha = 0.65f), Offset(q(-8f, -4f).x - 2f * k, q(-8f, -4f).y - 1f * k), Size(5f * k, 2f * k))
        } else if (nieve) drawOval(lerp(T.en(pie, C.NIEVE), C.LUNA, 0.2f).copy(alpha = 0.55f), Offset(q(-8f, -3f).x - 2f * k, q(-8f, -3f).y - 1f * k), Size(5f * k, 2f * k))
    }

    /**
     * El chal de lana de Pávlovski Posad, negro con rosas rojas, doblado en pico y echado en la esquina de delante de la
     * mesa: la mitad sobre el hule y el pico colgando por delante, otro trozo colgando por el costado, con sus flecos.
     * Con luto, sin flores.
     */
    private fun DrawScope.chal(T: Tono, M: Marco, m: Momento) {
        val k = M.k; val luto = EscenaUrss.luto(m)
        val fondo = if (luto) C.TINTA else CHAL
        // Lo que cuelga por el costado (la cara de la izquierda de la mesa).
        val ladoP = listOf(M.q(-0.6f, -30f, 0.4f), M.q(-0.6f, -2f, 0.4f), M.q(-0.6f, -1f, -20f), M.q(-0.6f, -16f, -16f))
        val lado = poligono(ladoP)
        drawPath(lado, T.izq(M.q(-0.6f, -14f, -10f), fondo))
        // Sobre el hule: el triángulo doblado.
        val arriba = poligono(M.q(-0.8f, -32f, 0.5f), M.q(42f, -22f, 0.5f), M.q(34f, 0.6f, 0.5f), M.q(-0.8f, 0.6f, 0.5f))
        // Lo que cuelga por delante: el pico, hasta media pata.
        val frente = Path().apply {
            val a0 = M.q(-0.8f, 0.6f, 0.5f); moveTo(a0.x, a0.y)
            val a1 = M.q(34f, 0.6f, 0.5f); lineTo(a1.x, a1.y)
            val tip = M.q(6f, 0.8f, -36f); cubicTo(M.q(30f, 0.7f, -10f).x, M.q(30f, 0.7f, -10f).y, M.q(14f, 0.8f, -28f).x, M.q(14f, 0.8f, -28f).y, tip.x, tip.y)
            val b0 = M.q(-0.8f, 0.6f, -20f); cubicTo(M.q(2f, 0.8f, -30f).x, M.q(2f, 0.8f, -30f).y, M.q(-1f, 0.7f, -24f).x, M.q(-1f, 0.7f, -24f).y, b0.x, b0.y); close()
        }
        // La sombra del chal en el hule y en el borde.
        drawPath(poligono(M.q(-0.8f, -32f, 0f), M.q(44f, -22f, 0f), M.q(36f, 1f, 0f), M.q(-0.8f, 1f, 0f)), C.SOMBRA.copy(alpha = 0.22f), blendMode = BlendMode.Multiply)
        drawPath(arriba, Brush.horizontalGradient(0f to T.arriba(M.q(4f, -12f), fondo), 1f to T.arriba(M.q(34f, -12f), lerp(fondo, C.PAPEL, 0.04f)), startX = M.q(0f, 0f).x, endX = M.q(40f, 0f).x))
        with(T) { cuerpo(frente, M.q(-1f, 0.6f, -10f).x, M.q(34f, 0.6f, -10f).x, M.q(10f, 0.6f, -16f).y, fondo) }
        // Los pliegues del pico.
        clipPath(frente) { for ((u0, u1) in listOf(8f to 5f, 18f to 9f, 26f to 10f)) { val a = M.q(u0, 0.6f, 0f); val b = M.q(u1, 0.7f, -32f); drawLine(T.sombra(a, fondo, 0.6f).copy(alpha = 0.8f), a, b, 1.4f * k); drawLine(T.filo.copy(alpha = 0.22f), a + Offset(1.3f * k, 0f), b + Offset(1.3f * k, 0f), 0.5f * k) } }
        if (!luto) {
            // Las rosas grandes, con su remolino claro y sus hojas, y la cenefa del borde.
            fun rosa(q: Offset, r: Float, color: (Offset, Color) -> Color) {
                for (a in listOf(-2.6f, -0.5f, 1.2f, 2.8f)) drawOval(color(q, C.VERDE), Offset(q.x + cos(a) * r * 1.3f - r * 0.5f, q.y + sin(a) * r * 0.75f - r * 0.22f), Size(r, r * 0.44f))
                drawCircle(color(q, C.ROJO), r, q)
                drawCircle(color(q, lerp(C.ROJO, C.LADRILLO, 0.5f)), r * 0.62f, q + Offset(r * 0.12f, r * 0.12f))
                drawArc(color(q, lerp(C.ROJO, C.PAPEL, 0.45f)), 200f, 220f, false, q - Offset(r * 0.55f, r * 0.55f), Size(r * 1.1f, r * 1.1f), style = Stroke(r * 0.18f))
                drawArc(color(q, lerp(C.ROJO, C.PAPEL, 0.35f)), 20f, 200f, false, q - Offset(r * 0.3f, r * 0.3f), Size(r * 0.6f, r * 0.6f), style = Stroke(r * 0.14f))
                drawCircle(color(q, C.ORO), r * 0.14f, q)
            }
            clipPath(arriba) {
                for ((u, v, r) in listOf(Triple(14f, -14f, 4.6f), Triple(28f, -8f, 3.6f), Triple(6f, -4f, 3.2f), Triple(24f, -20f, 2.6f))) rosa(M.q(u, v, 0.5f), r * k) { q, c -> T.arriba(q, c) }
                for (i in 0..12) { val q = M.q(2f + i * 2.6f, -1.6f, 0.5f); drawCircle(T.arriba(q, if (i % 2 == 0) C.ORO else C.ROJO), 0.55f * k, q) }
            }
            clipPath(frente) {
                rosa(M.q(14f, 0.7f, -14f), 4.2f * k) { q, c -> T.frente(q, c) }
                rosa(M.q(7f, 0.7f, -25f), 2.6f * k) { q, c -> T.frente(q, c) }
                for (i in 0..9) { val q = M.q(2f + i * 3f, 0.7f, -1.6f); drawCircle(T.frente(q, if (i % 2 == 0) C.ORO else C.ROJO), 0.5f * k, q) }
            }
            clipPath(lado) { rosa(M.q(-0.6f, -14f, -9f), 3f * k) { q, c -> T.izq(q, c) } }
        }
        // El canto doblado, con su filo de luz.
        drawLine(T.arriba(M.q(14f, 0.6f, 0.5f), lerp(fondo, C.PAPEL, 0.25f)), M.q(-0.8f, 0.6f, 0.5f), M.q(34f, 0.6f, 0.5f), 0.9f * k)
        // Los flecos: por los dos bordes del pico y por el del costado.
        val fleco = T.frente(M.q(10f, 0.8f, -30f), lerp(fondo, C.ROJO, if (luto) 0f else 0.25f))
        for (i in 0..13) { val f = i / 13f; val q = lerp2(M.q(34f, 0.7f, 0f), M.q(6f, 0.8f, -36f), f) + Offset(0f, 0.5f * k * f); drawLine(fleco, q, q + Offset(-1f * k, 4.6f * k), 0.4f * k) }
        for (i in 0..8) { val f = i / 8f; val q = lerp2(M.q(-0.8f, 0.6f, -20f), M.q(6f, 0.8f, -36f), f); drawLine(fleco, q, q + Offset(0.6f * k, 4.6f * k), 0.4f * k) }
        for (i in 0..7) { val f = i / 7f; val q = lerp2(ladoP[2], ladoP[3], f); drawLine(T.izq(q, fondo), q, q + Offset(0.2f * k, 4f * k), 0.4f * k) }
    }

    private fun lerp2(a: Offset, b: Offset, f: Float) = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
}
