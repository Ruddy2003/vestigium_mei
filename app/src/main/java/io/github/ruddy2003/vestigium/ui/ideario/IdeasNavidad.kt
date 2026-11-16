package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

// =====================================================================
//  Navidad
// =====================================================================

/**
 * Navidad: la alacena de la cocina de una casa en diciembre, a media tarde de hornear. Contra la pared de la cocina (arriba
 * pintada, con el vaho y alguna mancha de uso; detrás del sobre, medio azulejo crema con su cenefa verde) está el aparador de
 * madera pintada de crema con filetes verdes, gastado en los cantos donde asoma el pino. Su sobre de trabajo es de pino fregado,
 * con el canto moldurado (la cornisa) del que cuelga en festones una tira de bombillas de colores, y un mantel de cuadros rojos
 * que cae por el canto con sus pliegues. Encima, lo de hornear: la casita de jengibre a medio glasear (lo primero que se ve),
 * con la manga pastelera tumbada al lado y caramelos sueltos; el cuenco con su cuchara de palo, el rodillo con harina, el tarro
 * de palitos de canela con dos naranjas con clavos, un plato de galletas (una mordida), una taza humeante y la palmatoria de
 * latón con su vela. En la pared, el calendario de adviento con algunas casillas abiertas, los cortadores de galletas (estrella,
 * corazón y abeto) colgados de un listón y la corona de abeto con su lazo rojo. Debajo del sobre, las baldas abiertas, con su
 * puntilla de papel recortado; abajo, un cajón largo con tiradores de porcelana y su tarjetero (la placa), el zócalo con sus
 * patas y el suelo de barro; al pie, un cesto de mimbre con piñas y ramas y un regalo cortado por el borde.
 * Cada tema es, según la semilla, una lata redonda de galletas (roja, azul o verde, con copos, filetes dorados y una abolladura),
 * una lata rectangular de tapa con bisagra con cuadros escoceses, o un tarro de cristal con tapa de tela a cuadros atada con
 * cordel; en las tres, la etiqueta alargada con el tema, y asoman las tarjetas (más cuantas más fichas). Lo que no tiene tema va
 * en una caja de cartón de pastelería atada con cordel, con la etiqueta en blanco; un hueco sin tema es la balda con migas y un
 * adorno suelto (una bola de cristal o una piña). De noche la cocina queda a oscuras, en verde azulado hondo: alumbran la tira
 * de bombillas (cada una con su halo de color en el canto y destellos en el azulejo y en las latas de la primera fila) y la vela
 * (halo cálido en la pared, la corona y la casita); cada fila de baldas recibe menos luz que la de arriba y las de abajo quedan
 * casi perdidas, con algún destello del metal. Por dentro, la lata con su blonda de papel. Las fichas son tarjetas de
 * felicitación con filete rojo y verde y una ramita de acebo.
 */
object IdeasNavidad : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta
    /** La pintura del aparador y la de sus filetes verdes; el fondo pintado de las baldas. */
    private val CREMA = Color(0xFFEDE0BF)
    private val FILETE = Color(0xFF2F5B40)
    private val SALVIA = Color(0xFFA2B08E)
    /** El rojo de las latas, el lazo y los cuadros; el azul de lata. */
    private val CARMIN = Color(0xFFAC2E27)
    private val AZUL_LATA = Color(0xFF2E4D76)
    /** El oro viejo de los filetes y el latón. */
    private val ORO_VIEJO = Color(0xFFC39A4A)
    /** El pino del sobre, el rodillo y la cuchara, y su sombra. */
    private val PINO = Color(0xFFB9874F)
    private val PINO_OSC = Color(0xFF6A4528)
    /** La masa de jengibre y el glaseado. */
    private val JENGIBRE = Color(0xFFB06A33)
    private val GLASEADO = Color(0xFFFBF6EA)
    /** El azulejo crema y la pared pintada, verdosa y apagada. */
    private val AZULEJO = Color(0xFFEFE6CF)
    private val PINTURA = Color(0xFFD5D0B0)
    private val NARANJA = Color(0xFFDD8730)
    /** El papel de etiquetas, tarjetas, puntillas y calendario. */
    private val PAPEL = Color(0xFFF5ECD8)
    /** La noche de la cocina: verde azulado hondo. */
    private val NOCHE = Color(0xFF0D1C20)
    private val LLAMA = Color(0xFFFFC66E)
    /** Las bombillas encendidas: roja, verde, azul y ámbar. */
    private val BOMBILLAS = listOf(Color(0xFFFF6A5A), Color(0xFF7FE08A), Color(0xFF8DB8FF), Color(0xFFFFCF5A))

    // Lo de las fichas y el interior, como antes.
    private val ABETO = Color(0xFF1B5E20)
    private val ROJO = Color(0xFFB71C1C)
    private val ROJO_OSC = Color(0xFF7A1010)
    private val ORO = Color(0xFFD4A84A)
    private val BLANCO = Color(0xFFFBF6EC)
    private val LATA_AZUL = Color(0xFF1E3E7A)
    private val PLATA = Color(0xFFC8CCD0)
    private val TINTA = Color(0xFF1E2A22)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.68f
    override val base = 0.34f
    override val lado = 0.085f
    override val hueco = 0.05f

    // ---------------------------------------------------------------- dónde está cada cosa (fracciones del ancho)
    /** El sobre: la junta con la pared, el borde de delante y el canto moldurado (la cornisa). */
    private const val SOBRE_ATRAS = 0.54f
    private const val SOBRE_DELANTE = 0.59f
    private const val CANTO = 0.625f
    /** La tira de bombillas cuelga del canto. */
    private const val TIRA_Y = 0.645f
    /** La llama de la vela de la palmatoria. */
    private const val VELA_X = 0.915f
    private const val VELA_Y = 0.405f

    /**
     * Cuánta luz llega de noche a un punto (en fracciones del ancho), de 0 (perdido) a 1: la tira alumbra hacia abajo y cae
     * despacio fila a fila; hacia arriba cae rápido (las bombillas miran al suelo). La vela alumbra alrededor de ella.
     */
    private fun calor(x: Float, y: Float): Float {
        val dy = y - TIRA_Y
        val tira = if (dy >= 0f) 1.05f - dy / 0.95f else 1f + dy / 0.2f
        val vela = 1.1f * Math.pow((1f - hypot(x - VELA_X, y - VELA_Y) / 0.78f).coerceAtLeast(0f).toDouble(), 1.6).toFloat()
        return maxOf(tira.coerceIn(0f, 1f) * 0.9f, vela).coerceIn(0f, 1f)
    }

    /** Si en ese punto manda la vela (true) o la tira (false): de dónde viene la luz de noche. */
    private fun mandaVela(x: Float, y: Float): Boolean {
        val dy = y - TIRA_Y
        val tira = if (dy >= 0f) 1.05f - dy / 0.95f else 1f + dy / 0.2f
        return 1.1f * Math.pow((1f - hypot(x - VELA_X, y - VELA_Y) / 0.78f).coerceAtLeast(0f).toDouble(), 1.6).toFloat() > tira.coerceIn(0f, 1f) * 0.9f
    }

    /** Hacia dónde está la luz, como vector unitario: de día, arriba a la izquierda; de noche, la vela o la tira. */
    private fun haciaLuz(x: Float, y: Float, oscuro: Boolean): Offset {
        if (!oscuro) return Offset(-0.8f, -0.6f)
        if (mandaVela(x, y)) {
            val v = Offset(VELA_X - x, VELA_Y - y); val d = v.getDistance().coerceAtLeast(0.001f)
            return v / d
        }
        return Offset(0f, if (y > TIRA_Y) -1f else 1f)
    }

    /** El centro de un compartimento en fracciones del ancho (como lo reparte [MuebleIdeas]); null fuera del mueble. */
    private fun centroHueco(c: EstadoCajon): Offset? {
        if (c.cols != cols) return null
        val aw = (1f - 2 * lado - (cols - 1) * hueco) / cols
        val ah = aw / proporcion
        return Offset(lado + c.col * (aw + hueco) + aw * 0.5f, corona + hueco + c.fila * (ah + hueco) + ah * 0.6f)
    }

    /** Un color a [k] de luz: de día tal cual; de noche hundido en el verde azulado y calentado cerca de la luz. */
    private fun luz(c: Color, oscuro: Boolean, k: Float): Color {
        if (!oscuro) return c
        val kk = k.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.87f - 0.66f * kk), FILO_MUEBLE, 0.16f * kk)
    }

    /** La sombra: de día, violeta hondo; de noche, el verde azulado de la cocina. */
    private fun sombra(oscuro: Boolean, a: Float) = if (oscuro) NOCHE.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** La luz que da en lo que mira a la luz: crema de día; de noche, el filo cálido según [k]. */
    private fun claro(oscuro: Boolean, a: Float, k: Float = 0f) = if (oscuro) FILO_MUEBLE.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f)) else luzMueble(a)

    // ---------------------------------------------------------------- ayudantes

    private fun corrido(p: Path, dx: Float, dy: Float = 0f) = Path().apply { addPath(p, Offset(dx, dy)) }
    private fun borde(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corrido(p, dx, dy), PathOperation.Difference) }

    /** Un polígono con las coordenadas en pares x, y. */
    private fun poli(vararg v: Float): Path = Path().apply {
        moveTo(v[0], v[1]); var i = 2
        while (i < v.size - 1) { lineTo(v[i], v[i + 1]); i += 2 }
        close()
    }

    /**
     * El volumen de gouache sobre una forma ya pintada: la media luna de sombra del lado contrario a la luz ([hacia], vector
     * unitario), y el canto que mira a la luz más claro. De noche el canto es el filo cálido y crece con [k].
     */
    private fun DrawScope.volumen(forma: Path, oscuro: Boolean, k: Float, hacia: Offset, fuerza: Float = 1f, brillo: Float = 0f) {
        val r = forma.getBounds(); val a = r.width; val hh = r.height
        drawPath(borde(forma, hacia.x * a * 0.3f, hacia.y * hh * 0.3f), sombra(oscuro, (if (oscuro) 0.28f else 0.16f) * fuerza))
        drawPath(borde(forma, hacia.x * a * 0.12f, hacia.y * hh * 0.12f), sombra(oscuro, (if (oscuro) 0.26f else 0.16f) * fuerza))
        drawPath(borde(forma, -hacia.x * a * 0.05f, -hacia.y * hh * 0.05f), claro(oscuro, 0.35f * fuerza, k))
        if (brillo > 0f) {
            val p = Offset(r.center.x + hacia.x * a * 0.26f, r.center.y + hacia.y * hh * 0.26f)
            drawOval(if (oscuro) FILO_MUEBLE.copy(alpha = (brillo * 1.4f * k).coerceAtMost(0.8f)) else luzMueble(brillo), Offset(p.x - a * 0.05f, p.y - hh * 0.1f), Size(a * 0.1f, hh * 0.2f))
        }
    }

    /** Un copo de nieve de seis brazos. */
    private fun DrawScope.copo(c: Offset, r: Float, color: Color, grosor: Float) {
        for (k in 0 until 6) {
            val a = k * Math.PI / 3
            val e = Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat())
            drawLine(color, c, e, grosor, StrokeCap.Round)
            val m = Offset(c.x + r * 0.6f * cos(a).toFloat(), c.y + r * 0.6f * sin(a).toFloat())
            listOf(0.6, -0.6).forEach { da -> drawLine(color, m, Offset(m.x + r * 0.3f * cos(a + da).toFloat(), m.y + r * 0.3f * sin(a + da).toFloat()), grosor, StrokeCap.Round) }
        }
    }

    /** Una ramita de acebo: dos hojas dentadas y tres bayas. */
    private fun DrawScope.acebo(c: Offset, s: Float, hoja: Color, baya: Color) {
        listOf(-1f, 1f).forEach { d ->
            val p = Path().apply {
                moveTo(c.x, c.y)
                for (k in 1..4) { val f = k / 4f; lineTo(c.x + d * s * 0.5f * f, c.y - s * (0.16f + (if (k % 2 == 0) 0.04f else 0.14f)) * (1 - f * 0.4f) - s * 0.05f * f) }
                lineTo(c.x + d * s * 0.56f, c.y - s * 0.02f)
                for (k in 4 downTo 1) { val f = k / 4f; lineTo(c.x + d * s * 0.5f * f, c.y + s * (if (k % 2 == 0) 0.04f else 0.12f) * (1 - f * 0.4f)) }
                close()
            }
            drawPath(p, hoja)
        }
        listOf(Offset(-0.06f, 0.02f), Offset(0.07f, 0.04f), Offset(0f, 0.13f)).forEach { o -> drawCircle(baya, s * 0.08f, Offset(c.x + o.x * s, c.y + o.y * s)) }
    }

    /** Cuadros de mantel (vichy) dentro de lo que ya está recortado: franjas de [color] que se cruzan sobre el blanco. */
    private fun DrawScope.vichy(r: Rect, fondo: Color, color: Color, paso: Float) {
        drawRect(fondo, r.topLeft, r.size)
        var x = r.left
        while (x < r.right) { drawRect(color.copy(alpha = color.alpha * 0.55f), Offset(x, r.top), Size(paso, r.height)); x += paso * 2 }
        var y = r.top
        while (y < r.bottom) { drawRect(color.copy(alpha = color.alpha * 0.55f), Offset(r.left, y), Size(r.width, paso)); y += paso * 2 }
    }

    /**
     * Tapa el aura en reposo de `Modifier.halo()` alrededor de una silueta, de noche (la misma solución que Helénico): un aro
     * oscuro pintado con `SrcAtop`, que oscurece el fondo junto al borde y no cuenta en la capa vacía donde se calca el aura.
     */
    private fun DrawScope.cubreAura(oscuro: Boolean, rellenos: List<Path>) {
        if (!oscuro) return
        val ancho = 2.9f * density; val junto = 1.7f * density
        drawIntoCanvas { it.saveLayer(Rect(-ancho * 4, -ancho * 4, size.width + ancho * 4, size.height + ancho * 4), Paint().apply { blendMode = BlendMode.SrcAtop }) }
        for ((color, a) in listOf(NOCHE.copy(alpha = 0.32f) to ancho, NOCHE.copy(alpha = 0.6f) to junto))
            rellenos.forEach { drawPath(it, color, style = Stroke(a * 2, join = StrokeJoin.Round), blendMode = BlendMode.Src) }
        drawIntoCanvas { it.restore() }
    }

    /** Una sombra que solo cae sobre lo ya pintado (no cuenta para el aura de `Modifier.halo()`, ver Helénico). */
    private fun DrawScope.sombraEn(p: Path, color: Color) = drawPath(p, color, blendMode = BlendMode.SrcAtop)

    // ---------------------------------------------------------------- el mueble

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val w = size.width; val alto = size.height
        val o = oscuro
        fun k(x: Float, y: Float) = calor(x / w, y / w)
        fun c(col: Color, x: Float, y: Float) = luz(col, o, k(x, y))
        fun hacia(x: Float, y: Float) = haciaLuz(x / w, y / w, o)
        /** Hacia dónde cae la sombra de algo en la pared: de día a la derecha y abajo; de noche, lejos de su luz. */
        fun caida(x: Float, y: Float, largo: Float): Offset {
            val h = hacia(x, y)
            return if (!o) Offset(largo * 0.8f, largo * 0.6f) else Offset(-h.x * largo * 2.2f, -h.y * largo * 2.2f)
        }
        val sb = w * SOBRE_ATRAS; val sf = w * SOBRE_DELANTE; val canto = w * CANTO
        val ladoIzq = w * 0.022f; val ladoDer = w * 0.978f
        val primero = d.filas.first(); val ultima = d.filas.last()
        val b = d.base
        val cajonArriba = b.top + w * 0.018f; val cajonAbajo = b.top + w * 0.135f
        val zocalo = b.top + w * 0.19f
        val suelo = b.top + w * 0.205f

        // ---- la pared: pintada arriba, medio azulejo con su cenefa verde detrás del sobre
        val cenefa = w * 0.3f
        val azul0 = cenefa + w * 0.022f
        // La pintura, en franjas para que de noche se apague hacia arriba sin degradados.
        for (j in 0 until 6) {
            val y0 = cenefa * j / 6f
            drawRect(c(PINTURA, w * 0.3f, y0 + cenefa / 12f), Offset(0f, y0), Size(w, cenefa / 6f + 1f))
        }
        val pintada = Rect(0f, 0f, w, cenefa)
        pinceladas(pintada, luz(mezcla(PINTURA, SOMBRA_MUEBLE, 0.35f), o, 0.1f), 1501, 70, u, 0.08f)
        pinceladas(pintada, luz(mezcla(PINTURA, LUZ_MUEBLE, 0.5f), o, 0.1f), 1502, 50, u, 0.1f)
        pinceladas(pintada, luz(mezcla(PINTURA, SALVIA, 0.6f), o, 0.1f), 1503, 26, u, 0.08f)
        if (!o) mancha(Offset(w * 0.05f, -w * 0.02f), w * 0.95f, luzMueble(0.4f))
        // El vaho del horno: una nube pálida que subió por la pared, y el cerco gris de la humedad en la esquina.
        listOf(Offset(w * 0.56f, w * 0.2f), Offset(w * 0.62f, w * 0.12f), Offset(w * 0.5f, w * 0.07f)).forEachIndexed { i, p ->
            drawOval(luz(mezcla(PINTURA, LUZ_MUEBLE, 0.7f), o, k(p.x, p.y)).copy(alpha = if (o) 0.07f else 0.22f), Offset(p.x - w * 0.09f, p.y - w * 0.035f), Size(w * (0.18f - 0.03f * i), w * 0.07f))
        }
        drawOval(sombra(o, 0.07f), Offset(w * 0.86f, -w * 0.04f), Size(w * 0.2f, w * 0.1f))
        // La cenefa: una hilera de azulejos verdes con su raya clara.
        for (j in 0 until 14) {
            val x0 = w * j / 13.5f - w * 0.02f
            val col = c(mezcla(FILETE, SALVIA, 0.12f * (j % 3)), x0, cenefa)
            drawRect(col, Offset(x0, cenefa), Size(w / 13.5f - 1.2f * u, azul0 - cenefa))
            drawRect(claro(o, 0.4f, k(x0, cenefa)), Offset(x0 + 1.5f * u, cenefa + 1.5f * u), Size(w / 13.5f * 0.4f, 1f * u))
            drawRect(luz(GLASEADO, o, k(x0, cenefa)).copy(alpha = 0.55f), Offset(x0, cenefa + (azul0 - cenefa) * 0.48f), Size(w / 13.5f - 1.2f * u, 1f * u))
        }
        drawRect(sombra(o, 0.25f), Offset(0f, azul0), Size(w, 1.2f * u))
        // El azulejo crema, cuadrado, con sus juntas, el vidriado que brilla y alguno cuarteado.
        val lt = w * 0.062f
        var fy = 0
        var yy = azul0
        while (yy < sb) {
            var x = -lt * 0.3f; var fx = 0
            while (x < w) {
                val p = Offset(x + lt / 2, yy + lt / 2)
                val tono = mezcla(AZULEJO, if ((fx + fy) % 3 == 0) PINTURA else LUZ_MUEBLE, 0.18f * azar(1510 + fy, fx))
                drawRect(c(tono, p.x, p.y), Offset(x, yy), Size(lt - 1f * u, minOf(lt, sb - yy) - 1f * u))
                if (!o) drawRect(luzMueble(0.32f), Offset(x + lt * 0.12f, yy + lt * 0.12f), Size(lt * 0.36f, 1.1f * u))
                if (azar(1511, fx * 31 + fy) < 0.05f) drawLine(sombra(o, 0.12f), Offset(x + lt * 0.2f, yy + lt * 0.55f), Offset(x + lt * 0.6f, yy + lt * 0.7f), 0.5f * u)
                x += lt; fx++
            }
            drawRect(sombra(o, 0.14f), Offset(0f, yy + lt - 1f * u), Size(w, 1f * u))
            yy += lt; fy++
        }
        // Una mancha de grasa en el azulejo, junto al cuenco, y la junta ennegrecida al pie.
        drawOval(luz(mezcla(PINO, ORO_VIEJO, 0.5f), o, 0.2f).copy(alpha = 0.12f), Offset(w * 0.205f, w * 0.4f), Size(w * 0.035f, w * 0.022f))
        pinceladas(Rect(0f, sb - w * 0.035f, w, sb), sombra(o, 1f), 1512, 16, u, 0.1f)
        // De noche, la vela: su halo en la pared, en pocas manchas.
        val llama = Offset(w * VELA_X, w * VELA_Y)
        if (o) {
            mancha(llama, w * 0.75f, FILO_MUEBLE.copy(alpha = 0.18f))
            mancha(llama, w * 0.34f, FILO_MUEBLE.copy(alpha = 0.26f))
            mancha(llama, w * 0.12f, LLAMA.copy(alpha = 0.4f))
        }

        // ---- lo colgado en la pared
        calendario(Rect(w * 0.045f, w * 0.045f, w * 0.225f, w * 0.255f), o, ::k, caida(w * 0.13f, w * 0.15f, w * 0.01f))
        cortadores(w * 0.36f, w * 0.64f, w * 0.1f, w, o, ::k, caida(w * 0.5f, w * 0.17f, w * 0.008f))
        coronaAbeto(Offset(w * 0.775f, w * 0.145f), w * 0.088f, o, ::k, caida(w * 0.775f, w * 0.145f, w * 0.012f))

        // ---- el aparador: el cuerpo pintado, por franjas de luz
        val cuerpo = Rect(ladoIzq, canto, ladoDer, zocalo)
        // Su sombra en la pared, a la derecha (de día) o hundida (de noche).
        drawRect(sombra(o, if (o) 0.5f else 0.24f), Offset(ladoDer, sf), Size(w - ladoDer, suelo - sf))
        drawRect(sombra(o, if (o) 0.4f else 0.18f), Offset(0f, sf), Size(ladoIzq, suelo - sf))
        // El azulejo que asoma a los lados, bajo el sobre.
        drawRect(luz(mezcla(AZULEJO, SOMBRA_MUEBLE, 0.25f), o, 0f), Offset(0f, canto), Size(ladoIzq * 0.6f, suelo - canto))
        drawRect(luz(mezcla(AZULEJO, SOMBRA_MUEBLE, 0.4f), o, 0f), Offset(ladoDer + ladoIzq * 0.4f, canto), Size(ladoIzq * 0.6f, suelo - canto))
        val bandas = 14
        for (j in 0 until bandas) {
            val y0 = cuerpo.top + cuerpo.height * j / bandas
            drawRect(c(CREMA, w * 0.4f, y0 + cuerpo.height / bandas / 2), Offset(cuerpo.left, y0), Size(cuerpo.width, cuerpo.height / bandas + 1f))
        }
        pinceladas(cuerpo, luz(mezcla(CREMA, SOMBRA_MUEBLE, 0.3f), o, 0.3f), 1520, 60, u, 0.07f)
        pinceladas(cuerpo, luz(mezcla(CREMA, LUZ_MUEBLE, 0.6f), o, 0.3f), 1521, 40, u, 0.1f)
        // El costado de la luz y el de la sombra.
        if (!o) {
            drawRect(luzMueble(0.25f), Offset(cuerpo.left, cuerpo.top), Size(w * 0.012f, cuerpo.height))
            drawRect(sombraMueble(0.16f), Offset(cuerpo.right - w * 0.03f, cuerpo.top), Size(w * 0.03f, cuerpo.height))
            drawRect(sombraMueble(0.14f), Offset(cuerpo.right - w * 0.01f, cuerpo.top), Size(w * 0.01f, cuerpo.height))
        } else {
            drawRect(NOCHE.copy(alpha = 0.3f), Offset(cuerpo.right - w * 0.03f, cuerpo.top), Size(w * 0.03f, cuerpo.height))
        }

        // ---- el fondo de cada balda (lo pinta el mueble, que sabe en qué fila está) y su puntilla
        d.huecos.forEachIndexed { i, r -> fondoHueco(r, i / cols, d.filas.size, o) }
        // El filete verde alrededor de cada hueco, y el pino que asoma donde se gastó la pintura.
        d.huecos.forEachIndexed { i, r ->
            val kk = k(r.center.x, r.top)
            val m = w * 0.012f
            drawRect(luz(FILETE, o, kk), Offset(r.left - m, r.top - m * 0.4f), Size(r.width + 2 * m, r.height + m * 1.4f), style = Stroke(1.3f * u))
            drawRect(claro(o, 0.3f, kk), Offset(r.left - m - 1.4f * u, r.top - m * 0.4f), Size(1f * u, r.height + m * 1.4f))
            if (azar(1530, i) < 0.6f) {
                val izq = azar(1531, i) < 0.5f
                desgaste(Offset(if (izq) r.left - m * 1.5f else r.right + m * 1.5f, r.bottom + m * 0.6f), w * 0.008f, 1530 + i * 7, o, kk)
            }
        }
        // Los cantos de las baldas, entre fila y fila, con su filete y su roce.
        d.filas.forEachIndexed { i, f ->
            val y0 = f.bottom; val y1 = d.filas.getOrNull(i + 1)?.top ?: (b.top + w * 0.004f)
            if (y1 > y0 + 1f) {
                val kk = k(w * 0.5f, y0)
                drawRect(luz(mezcla(CREMA, LUZ_MUEBLE, 0.15f), o, kk), Offset(cuerpo.left + w * 0.04f, y0), Size(cuerpo.width - w * 0.08f, (y1 - y0) * 0.55f))
                drawRect(claro(o, 0.45f, kk), Offset(cuerpo.left + w * 0.04f, y0), Size(cuerpo.width - w * 0.08f, 1f * u))
                drawRect(luz(FILETE, o, kk), Offset(cuerpo.left + w * 0.06f, y0 + (y1 - y0) * 0.3f), Size(cuerpo.width - w * 0.12f, 1f * u))
                drawRect(sombra(o, 0.2f), Offset(cuerpo.left + w * 0.04f, y0 + (y1 - y0) * 0.55f), Size(cuerpo.width - w * 0.08f, 1.2f * u))
            }
        }
        // El montante del centro, con su luz a la izquierda.
        val mx0 = d.huecos[0].right + w * 0.012f; val mx1 = d.huecos[1].left - w * 0.012f
        if (mx1 > mx0) d.filas.forEach { f ->
            drawRect(claro(o, 0.3f, k(mx0, f.center.y)), Offset(mx0, f.top - w * 0.01f), Size(1.2f * u, f.height + w * 0.02f))
            drawRect(sombra(o, 0.14f), Offset(mx1 - 2f * u, f.top - w * 0.01f), Size(2f * u, f.height + w * 0.02f))
        }

        // ---- abajo: el cajón largo, el zócalo con sus patas y el suelo de barro
        pisoBarro(Rect(0f, suelo, w, alto), o)
        val cajon = Rect(cuerpo.left + w * 0.035f, cajonArriba, cuerpo.right - w * 0.035f, cajonAbajo)
        drawRect(sombra(o, 0.3f), Offset(cajon.left - 1.4f * u, cajon.top - 1.4f * u), Size(cajon.width + 2.8f * u, cajon.height + 2.8f * u))
        drawRect(luz(mezcla(CREMA, LUZ_MUEBLE, 0.1f), o, 0f), cajon.topLeft, cajon.size)
        pinceladas(cajon, luz(mezcla(CREMA, SOMBRA_MUEBLE, 0.3f), o, 0f), 1540, 14, u, 0.08f)
        drawRect(luz(FILETE, o, 0f), Offset(cajon.left + w * 0.012f, cajon.top + w * 0.012f), Size(cajon.width - w * 0.024f, cajon.height - w * 0.024f), style = Stroke(1.2f * u))
        drawRect(claro(o, 0.4f, 0f), cajon.topLeft, Size(cajon.width, 1.2f * u))
        drawRect(sombra(o, 0.25f), Offset(cajon.left, cajon.bottom - 1.6f * u), Size(cajon.width, 1.6f * u))
        desgaste(Offset(cajon.left + w * 0.01f, cajon.bottom - w * 0.006f), w * 0.012f, 1541, o, 0f)
        desgaste(Offset(cajon.right - w * 0.2f, cajon.top + w * 0.004f), w * 0.009f, 1542, o, 0f)
        // Los tiradores de porcelana.
        listOf(0.22f, 0.78f).forEach { fx -> tirador(Offset(cajon.left + cajon.width * fx, cajon.center.y), w * 0.017f, o) }
        // El tarjetero de latón en medio, con su tarjeta (la placa de «y N más»).
        tarjetero(d.enBase(placa), o, d.mas > 0)
        // El zócalo y sus patas.
        drawRect(luz(mezcla(CREMA, SOMBRA_MUEBLE, 0.18f), o, 0f), Offset(cuerpo.left, cajonAbajo + w * 0.01f), Size(cuerpo.width, zocalo - cajonAbajo - w * 0.01f))
        drawRect(luz(FILETE, o, 0f), Offset(cuerpo.left, cajonAbajo + w * 0.01f), Size(cuerpo.width, 1.2f * u))
        drawRect(sombra(o, 0.2f), Offset(cuerpo.left, zocalo - 2f * u), Size(cuerpo.width, 2f * u))
        listOf(cuerpo.left, cuerpo.right - w * 0.06f).forEachIndexed { i, x ->
            val pata = poli(x, zocalo, x + w * 0.06f, zocalo, x + w * (if (i == 0) 0.05f else 0.06f), suelo + w * 0.008f, x + w * (if (i == 0) 0f else 0.01f), suelo + w * 0.008f)
            drawOval(sombra(o, 0.4f), Offset(x - w * 0.005f, suelo), Size(w * 0.08f, w * 0.014f))
            drawPath(pata, luz(mezcla(CREMA, SOMBRA_MUEBLE, if (i == 0) 0.1f else 0.3f), o, 0f))
            desgaste(Offset(x + w * 0.03f, suelo), w * 0.01f, 1550 + i, o, 0f)
        }
        // Debajo del aparador, la oscuridad entre las patas.
        drawRect(sombra(o, 0.55f), Offset(cuerpo.left + w * 0.06f, zocalo), Size(cuerpo.width - w * 0.12f, suelo - zocalo + w * 0.008f))
        // ---- al pie: el cesto con piñas y el regalo, cortado por el borde
        cesto(Offset(w * 0.15f, suelo + w * 0.105f), w * 0.24f, o)
        regalo(Rect(w * 0.79f, suelo - w * 0.03f, w * 1.06f, suelo + w * 0.115f), o)

        // ---- el sobre de pino, su canto moldurado y el mantel
        drawRect(c(mezcla(PINO, LUZ_MUEBLE, 0.32f), w * 0.5f, sb), Offset(0f, sb), Size(w, sf - sb))
        clipRect(0f, sb, w, sf) { vetas(Rect(0f, sb, w, sf), luz(PINO_OSC, o, 0.4f).copy(alpha = 0.26f), 1560, densidad = 0.9f, grosor = 0.6f, ondulacion = 0.35f) }
        drawRect(sombra(o, 0.4f), Offset(0f, sb), Size(w, 1.4f * u))
        drawRect(sombra(o, 0.12f), Offset(0f, sb + 1.4f * u), Size(w, 3f * u))
        // El canto: la moldura pintada de crema con su filete verde, gastada en las esquinas.
        for (j in 0 until 3) {
            val y0 = sf + (canto - sf) * j / 3f
            drawRect(c(mezcla(CREMA, if (j == 0) LUZ_MUEBLE else SOMBRA_MUEBLE, if (j == 0) 0.25f else 0.12f * j), w * 0.5f, y0), Offset(0f, y0), Size(w, (canto - sf) / 3f + 1f))
        }
        drawRect(c(FILETE, w * 0.5f, sf), Offset(0f, sf + (canto - sf) * 0.62f), Size(w, 1.3f * u))
        drawRect(claro(o, 0.5f, k(w * 0.5f, sf)), Offset(0f, sf), Size(w, 1.1f * u))
        drawRect(sombra(o, 0.35f), Offset(0f, canto - 1.2f * u), Size(w, 1.2f * u))
        desgaste(Offset(w * 0.955f, canto - w * 0.006f), w * 0.011f, 1561, o, k(w, sf))
        // La sombra del sobre en el frente del aparador.
        drawRect(sombra(o, 0.22f), Offset(cuerpo.left, canto), Size(cuerpo.width, w * 0.008f))
        mantel(sb, sf, canto, w, o, ::k)

        // ---- lo de hornear, sobre el sobre, de atrás adelante
        val apoyo = (sb + sf) / 2
        tarroCanela(Offset(w * 0.61f, apoyo - w * 0.006f), w * 0.075f, w * 0.12f, o, ::k, ::hacia, caida(w * 0.6f, apoyo, w * 0.01f))
        cuenco(Offset(w * 0.09f, apoyo + w * 0.004f), w * 0.17f, o, ::k, ::hacia, caida(w * 0.08f, apoyo, w * 0.01f))
        casita(Offset(w * 0.345f, apoyo + w * 0.006f), w * 0.34f, o, ::k, ::hacia, caida(w * 0.33f, apoyo - w * 0.1f, w * 0.012f))
        palmatoria(Offset(w * VELA_X, apoyo + w * 0.004f), w, o, ::k, caida(w * VELA_X, apoyo - w * 0.08f, w * 0.01f))
        taza(Offset(w * 0.8f, apoyo + w * 0.006f), w * 0.08f, o, ::k, ::hacia, caida(w * 0.8f, apoyo, w * 0.01f))
        plato(Offset(w * 0.705f, apoyo + w * 0.014f), w * 0.11f, o, ::k)
        naranja(Offset(w * 0.575f, apoyo + w * 0.014f), w * 0.027f, 1571, o, ::k, ::hacia)
        naranja(Offset(w * 0.632f, apoyo + w * 0.02f), w * 0.024f, 1572, o, ::k, ::hacia)
        manga(Offset(w * 0.47f, apoyo + w * 0.022f), w, o, ::k)
        rodillo(Offset(w * 0.05f, sf - w * 0.004f), Offset(w * 0.27f, sf - w * 0.014f), w * 0.019f, o, ::k)

        // ---- la tira de bombillas, colgada en festones del canto
        tira(w, o)
        // ---- la llama, encima de todo
        if (o) llama(llama, w * 0.03f)
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /** La pintura saltada en un canto: asoma el pino, con el borde de la pintura en sombra. */
    private fun DrawScope.desgaste(p: Offset, r: Float, semilla: Int, o: Boolean, k: Float) {
        val pr = Path()
        for (j in 0 until 7) {
            val a = 6.283f * j / 7 + azar(semilla, j) * 0.6f
            val rr = r * (0.55f + 0.5f * azar(semilla, j + 9))
            val q = Offset(p.x + rr * cos(a) * 1.3f, p.y + rr * sin(a) * 0.75f)
            if (j == 0) pr.moveTo(q.x, q.y) else pr.lineTo(q.x, q.y)
        }
        pr.close()
        drawPath(pr, luz(PINO, o, k))
        drawPath(borde(pr, r * 0.25f, r * 0.25f), sombra(o, 0.35f))
    }

    /**
     * El fondo de una balda en [r]: las tablas de machihembrado pintadas de salvia, la sombra de la balda de arriba, la balda
     * vista desde arriba al pie y la puntilla de papel colgando de arriba. De día, la fila de arriba algo más clara y la luz a la
     * izquierda; de noche, la luz de la tira, que cae fila a fila.
     */
    private fun DrawScope.fondoHueco(r: Rect, fila: Int, filas: Int, o: Boolean) {
        val u = density
        val w = r.width; val h = r.height
        val W = size.width
        val kk = calor(r.center.x / W, (r.top + h * 0.5f) / W)
        val kArriba = calor(r.center.x / W, r.top / W)
        val piso = r.top + h * 0.86f
        if (!o) {
            val f = 1f - fila / filas.coerceAtLeast(1).toFloat()
            drawRect(mezcla(mezcla(SALVIA, SOMBRA_MUEBLE, 0.12f), LUZ_MUEBLE, 0.12f * f), r.topLeft, Size(w, h))
            drawRect(luzMueble(0.12f + 0.06f * f), r.topLeft, Size(w * 0.28f, h))
            drawRect(sombraMueble(0.12f), Offset(r.right - w * 0.26f, r.top), Size(w * 0.26f, h))
            drawRect(sombraMueble(0.12f), Offset(r.right - w * 0.07f, r.top), Size(w * 0.07f, h))
            listOf(0.34f to 0.08f, 0.2f to 0.1f, 0.1f to 0.14f).forEach { (a, al) -> drawRect(sombraMueble(al), r.topLeft, Size(w, h * a)) }
        } else {
            // La luz baja de la tira: en franjas, más fuerte arriba en la primera fila.
            for (j in 0 until 5) {
                val kj = calor(r.center.x / W, (r.top + h * (0.1f + 0.2f * j)) / W)
                drawRect(luz(SALVIA, true, kj * 0.9f), Offset(r.left, r.top + h * 0.2f * j), Size(w, h * 0.2f + 1f))
            }
            drawRect(NOCHE.copy(alpha = 0.28f), Offset(r.right - w * 0.1f, r.top), Size(w * 0.1f, h))
            drawRect(NOCHE.copy(alpha = 0.2f), r.topLeft, Size(w * 0.05f, h))
            if (fila == 0) {
                // El reflejo de color de las bombillas en lo alto del fondo.
                for (j in 0 until 6) {
                    val x = r.left + w * (0.08f + 0.17f * j)
                    drawOval(BOMBILLAS[(j + (r.left / W * 10).toInt()) % 4].copy(alpha = 0.1f), Offset(x - w * 0.09f, r.top + h * 0.02f), Size(w * 0.18f, h * 0.24f))
                }
            }
        }
        // Las tablas del fondo: la junta de cada una y su canto claro.
        for (j in 1 until 6) {
            val x = r.left + w * j / 6f + w * 0.01f * (azar(1580, j + fila * 7) - 0.5f)
            drawLine(sombra(o, if (o) 0.3f else 0.18f), Offset(x, r.top), Offset(x, piso), 1f * u)
            drawLine(claro(o, 0.25f, kk), Offset(x + 1.2f * u, r.top), Offset(x + 1.2f * u, piso), 0.6f * u)
        }
        // La balda vista desde arriba, al pie, con su borde de luz.
        drawRect(luz(mezcla(CREMA, SOMBRA_MUEBLE, 0.1f), o, kk * 0.9f), Offset(r.left, piso), Size(w, r.bottom - piso))
        drawRect(sombra(o, 0.3f), Offset(r.left, piso), Size(w, 1.2f * u))
        drawRect(sombra(o, 0.12f), Offset(r.left, piso + 1.2f * u), Size(w, (r.bottom - piso) * 0.35f))
        drawRect(claro(o, 0.3f, kk), Offset(r.left, r.bottom - 1f * u), Size(w, 1f * u))
        // La puntilla de papel recortado, colgando de la balda de arriba, con su sombra en el fondo.
        val alto = h * 0.085f
        val festones = 9
        val pt = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + alto * 0.55f)
            for (j in festones downTo 1) {
                val x1 = r.left + w * (j - 1) / festones; val x0 = r.left + w * j / festones
                cubicTo(x0 - w * 0.01f, r.top + alto * 1.15f, x1 + w * 0.01f, r.top + alto * 1.15f, x1, r.top + alto * 0.55f)
            }
            close()
        }
        drawPath(corrido(pt, if (o) 0f else w * 0.012f, if (o) alto * 0.5f else alto * 0.35f), sombra(o, if (o) 0.3f else 0.16f))
        drawPath(pt, luz(PAPEL, o, kArriba))
        for (j in 0 until festones) {
            val x = r.left + w * (j + 0.5f) / festones
            drawCircle(luz(mezcla(SALVIA, SOMBRA_MUEBLE, 0.3f), o, kArriba * 0.8f), alto * 0.12f, Offset(x, r.top + alto * 0.68f))
            drawCircle(luz(mezcla(SALVIA, SOMBRA_MUEBLE, 0.3f), o, kArriba * 0.8f), alto * 0.07f, Offset(x - w / festones * 0.5f, r.top + alto * 0.36f))
        }
        drawRect(sombra(o, 0.12f), Offset(r.left, r.top), Size(w, alto * 0.18f))
    }

    // ---------------------------------------------------------------- lo de la pared

    /** El calendario de adviento clavado con una chincheta: un abeto pintado, 24 casillas y algunas abiertas. */
    private fun DrawScope.calendario(r: Rect, o: Boolean, k: (Float, Float) -> Float, caida: Offset) {
        val u = density
        val kk = k(r.center.x, r.center.y)
        rotate(-2.5f, r.center) {
            drawRect(sombra(o, if (o) 0.35f else 0.2f), r.topLeft + caida, r.size)
            drawRect(luz(PAPEL, o, kk), r.topLeft, r.size)
            drawRect(luz(CARMIN, o, kk), Offset(r.left + r.width * 0.04f, r.top + r.width * 0.04f), Size(r.width * 0.92f, r.height - r.width * 0.08f), style = Stroke(1.4f * u))
            // El dibujo de fondo: un abeto verde con su estrella.
            val cx = r.center.x
            drawPath(poli(cx, r.top + r.height * 0.1f, r.right - r.width * 0.12f, r.bottom - r.height * 0.12f, r.left + r.width * 0.12f, r.bottom - r.height * 0.12f), luz(mezcla(FILETE, SALVIA, 0.3f), o, kk).copy(alpha = 0.55f))
            drawPath(estrella(Offset(cx, r.top + r.height * 0.1f), r.width * 0.07f, 5, 0.45f), luz(ORO_VIEJO, o, kk))
            // Las casillas: 4 por 6; las primeras abiertas (la puertecita doblada a un lado, el hueco oscuro con su dulce).
            val cw = r.width * 0.17f; val chh = r.height * 0.1f
            for (j in 0 until 24) {
                val fx = j % 4; val fy = j / 4
                val x = r.left + r.width * (0.1f + 0.21f * fx) + r.width * 0.03f * azar(1590, j)
                val y = r.top + r.height * (0.22f + 0.12f * fy)
                val abierta = azar(1591, j) < 0.32f
                if (abierta) {
                    drawRect(luz(mezcla(PINO_OSC, NOCHE, 0.3f), o, kk), Offset(x, y), Size(cw, chh))
                    drawCircle(luz(listOf(CARMIN, ORO_VIEJO, FILETE)[j % 3], o, kk), chh * 0.22f, Offset(x + cw * 0.5f, y + chh * 0.55f))
                    drawRect(luz(mezcla(PAPEL, SOMBRA_MUEBLE, 0.15f), o, kk), Offset(x - cw * 0.55f, y), Size(cw * 0.55f, chh))
                } else {
                    drawRect(luz(mezcla(PAPEL, CARMIN, 0.06f), o, kk).copy(alpha = 0.7f), Offset(x, y), Size(cw, chh))
                    drawRect(luz(mezcla(PINO_OSC, PAPEL, 0.5f), o, kk), Offset(x, y), Size(cw, chh), style = Stroke(0.6f * u))
                    drawCircle(luz(CARMIN, o, kk).copy(alpha = 0.6f), chh * 0.1f, Offset(x + cw * 0.5f, y + chh * 0.5f))
                }
            }
            // La esquina de abajo, que se enrolla.
            drawPath(poli(r.right, r.bottom - r.width * 0.16f, r.right - r.width * 0.16f, r.bottom, r.right - r.width * 0.12f, r.bottom - r.width * 0.12f), luz(mezcla(PAPEL, SOMBRA_MUEBLE, 0.2f), o, kk))
        }
        // La chincheta.
        val p = Offset(r.center.x, r.top + r.height * 0.03f)
        drawCircle(sombra(o, 0.4f), r.width * 0.045f, p + Offset(1.2f * u, 1.4f * u))
        drawCircle(luz(CARMIN, o, kk), r.width * 0.04f, p)
        drawCircle(claro(o, 0.6f, kk), r.width * 0.014f, p - Offset(r.width * 0.012f, r.width * 0.012f))
    }

    /** Los cortadores de galletas (estrella, corazón y abeto) colgados de un listón con ganchos, cada uno de su cordel. */
    private fun DrawScope.cortadores(x0: Float, x1: Float, y: Float, w: Float, o: Boolean, k: (Float, Float) -> Float, caida: Offset) {
        val u = density
        val kk = k((x0 + x1) / 2, y + w * 0.06f)
        // El listón de pino con sus dos clavos.
        drawRect(sombra(o, if (o) 0.35f else 0.22f), Offset(x0 + caida.x * 0.5f, y + caida.y * 0.5f), Size(x1 - x0, w * 0.014f))
        drawRect(luz(PINO, o, kk), Offset(x0, y), Size(x1 - x0, w * 0.014f))
        drawRect(claro(o, 0.4f, kk), Offset(x0, y), Size(x1 - x0, 1f * u))
        drawRect(sombra(o, 0.3f), Offset(x0, y + w * 0.014f - 1f * u), Size(x1 - x0, 1f * u))
        listOf(x0 + w * 0.01f, x1 - w * 0.01f).forEach { drawCircle(luz(PINO_OSC, o, kk), 1.3f * u, Offset(it, y + w * 0.007f)) }
        val formas = listOf(
            // La estrella.
            { c: Offset, s: Float -> estrella(c, s, 5, 0.48f) },
            // El corazón.
            { c: Offset, s: Float -> Path().apply {
                moveTo(c.x, c.y + s * 0.9f)
                cubicTo(c.x - s * 1.25f, c.y + s * 0.05f, c.x - s * 0.7f, c.y - s * 0.95f, c.x, c.y - s * 0.38f)
                cubicTo(c.x + s * 0.7f, c.y - s * 0.95f, c.x + s * 1.25f, c.y + s * 0.05f, c.x, c.y + s * 0.9f); close()
            } },
            // El abeto, en tres pisos.
            { c: Offset, s: Float -> poli(c.x, c.y - s, c.x + s * 0.45f, c.y - s * 0.35f, c.x + s * 0.25f, c.y - s * 0.35f, c.x + s * 0.7f, c.y + s * 0.4f,
                c.x + s * 0.15f, c.y + s * 0.4f, c.x + s * 0.15f, c.y + s * 0.95f, c.x - s * 0.15f, c.y + s * 0.95f, c.x - s * 0.15f, c.y + s * 0.4f,
                c.x - s * 0.7f, c.y + s * 0.4f, c.x - s * 0.25f, c.y - s * 0.35f, c.x - s * 0.45f, c.y - s * 0.35f) },
        )
        val lata = luz(mezcla(PLATA, PINO, 0.15f), o, kk)
        listOf(0.17f to 0.058f, 0.5f to 0.045f, 0.83f to 0.054f).forEachIndexed { i, (fx, fs) ->
            val gancho = Offset(x0 + (x1 - x0) * fx, y + w * 0.014f)
            val s = w * fs
            val c = Offset(gancho.x + (i - 1) * w * 0.004f, gancho.y + w * (0.03f + 0.012f * i) + s)
            val giro = (i - 1) * 7f
            // El gancho y el cordel.
            drawArc(luz(PINO_OSC, o, kk), 0f, 180f, false, Offset(gancho.x - 2f * u, gancho.y - 1f * u), Size(4f * u, 4f * u), style = Stroke(1f * u))
            drawLine(luz(mezcla(PAPEL, CARMIN, 0.4f), o, kk), gancho + Offset(0f, 2f * u), Offset(c.x, c.y - s * 0.95f), 0.8f * u)
            rotate(giro, c) {
                val f = formas[i](c, s)
                drawPath(corrido(f, caida.x, caida.y), sombra(o, if (o) 0.35f else 0.2f), style = Stroke(2.2f * u))
                drawPath(f, lata, style = Stroke(2.4f * u, join = StrokeJoin.Round))
                drawPath(corrido(f, -0.8f * u, -0.8f * u), claro(o, 0.6f, kk), style = Stroke(0.7f * u, join = StrokeJoin.Round))
                drawPath(corrido(f, 0.9f * u, 0.9f * u), sombra(o, 0.35f), style = Stroke(0.6f * u, join = StrokeJoin.Round))
            }
        }
    }

    /** La corona de abeto colgada de un clavo con su lazo rojo: agujas en tres verdes, piñitas y bayas. */
    private fun DrawScope.coronaAbeto(c: Offset, r: Float, o: Boolean, k: (Float, Float) -> Float, caida: Offset) {
        val u = density
        val kk = k(c.x, c.y)
        val hacia = haciaLuz(c.x / size.width, c.y / size.width, o)
        drawCircle(sombra(o, if (o) 0.4f else 0.2f), r, c + caida, style = Stroke(r * 0.5f))
        drawCircle(luz(mezcla(FILETE, NOCHE, 0.35f), o, kk), r, c, style = Stroke(r * 0.42f))
        val tonos = listOf(FILETE, mezcla(FILETE, SALVIA, 0.45f), mezcla(FILETE, NOCHE, 0.3f))
        for (j in 0 until 84) {
            val a = 6.283f * j / 84 + azar(1600, j) * 0.1f
            val rr = r * (0.78f + 0.44f * azar(1601, j))
            val p = Offset(c.x + rr * cos(a), c.y + rr * sin(a))
            val g = a + 1.2f + 0.8f * (azar(1602, j) - 0.5f)
            val largo = r * (0.2f + 0.1f * azar(1603, j))
            // Las del lado de la luz, más claras.
            val dl = cos(a) * hacia.x + sin(a) * hacia.y
            val tono = if (dl > 0.35f) tonos[1] else if (dl < -0.4f) tonos[2] else tonos[j % 3]
            drawLine(luz(tono, o, kk), p, Offset(p.x + largo * cos(g), p.y + largo * sin(g)), 1.6f * u, StrokeCap.Round)
        }
        // Las piñitas y las bayas, sobre todo abajo.
        listOf(0.5f, 2.2f, 3.6f).forEachIndexed { i, a ->
            val p = Offset(c.x + r * cos(a), c.y + r * sin(a))
            drawOval(luz(PINO_OSC, o, kk), Offset(p.x - r * 0.1f, p.y - r * 0.15f), Size(r * 0.2f, r * 0.3f))
            for (j in 0 until 3) drawLine(luz(PINO, o, kk), Offset(p.x - r * 0.08f, p.y - r * 0.08f + j * r * 0.08f), Offset(p.x + r * 0.08f, p.y - r * 0.08f + j * r * 0.08f), 0.7f * u)
            for (j in 0 until 3) {
                val q = Offset(p.x + r * (0.14f + 0.06f * j) * (if (i == 1) -1f else 1f), p.y + r * (0.05f * j - 0.05f))
                drawCircle(luz(CARMIN, o, kk), r * 0.055f, q)
                drawCircle(claro(o, 0.6f, kk), r * 0.018f, q - Offset(r * 0.02f, r * 0.02f))
            }
        }
        // El clavo, la cinta que sube a él y el lazo arriba.
        val clavo = Offset(c.x, c.y - r * 1.5f)
        val nudo = Offset(c.x, c.y - r * 0.92f)
        drawLine(luz(CARMIN, o, kk), clavo, nudo, r * 0.08f)
        drawCircle(luz(PINO_OSC, o, kk), 1.4f * u, clavo)
        val lazo = luz(CARMIN, o, kk)
        for (l in listOf(-1f, 1f)) {
            val ala = Path().apply {
                moveTo(nudo.x, nudo.y)
                cubicTo(nudo.x + l * r * 0.3f, nudo.y - r * 0.32f, nudo.x + l * r * 0.55f, nudo.y - r * 0.1f, nudo.x + l * r * 0.42f, nudo.y + r * 0.1f)
                close()
            }
            drawPath(ala, lazo)
            drawPath(borde(ala, l * r * 0.05f, -r * 0.05f), sombra(o, 0.3f))
            val cola = Path().apply {
                moveTo(nudo.x, nudo.y)
                cubicTo(nudo.x + l * r * 0.12f, nudo.y + r * 0.25f, nudo.x + l * r * 0.05f, nudo.y + r * 0.4f, nudo.x + l * r * 0.2f, nudo.y + r * 0.55f)
            }
            drawPath(cola, lazo, style = Stroke(r * 0.1f))
        }
        drawCircle(lazo, r * 0.07f, nudo)
        drawCircle(claro(o, 0.4f, kk), r * 0.03f, nudo - Offset(r * 0.02f, r * 0.02f))
    }

    // ---------------------------------------------------------------- el sobre: mantel, tira y lo de hornear

    /**
     * El mantel de cuadros rojos sobre la mitad izquierda del sobre: liso encima y, por el canto, cae con sus pliegues hasta
     * una punta, con su dobladillo.
     */
    private fun DrawScope.mantel(sb: Float, sf: Float, canto: Float, w: Float, o: Boolean, k: (Float, Float) -> Float) {
        val u = density
        val x1 = w * 0.5f
        val kk = k(w * 0.25f, sf)
        val paso = w * 0.012f
        // Encima del sobre.
        val arriba = poli(-w * 0.01f, sb + w * 0.004f, x1 - w * 0.02f, sb + w * 0.006f, x1, sf, -w * 0.01f, sf)
        clipPath(arriba) { vichy(Rect(-w * 0.01f, sb, x1, sf), luz(GLASEADO, o, kk), luz(CARMIN, o, kk), paso) }
        // La caída, con el dobladillo en ondas y los pliegues.
        val bajo = canto + w * 0.05f
        val caida = Path().apply {
            moveTo(-w * 0.01f, sf)
            lineTo(x1, sf)
            lineTo(x1 + w * 0.008f, sf + w * 0.02f)
            cubicTo(x1 + w * 0.01f, sf + w * 0.06f, x1 - w * 0.01f, bajo + w * 0.02f, x1 - w * 0.03f, bajo + w * 0.035f)
            val pl = listOf(0.44f, 0.36f, 0.27f, 0.18f, 0.09f, -0.01f)
            var xa = x1 - w * 0.03f
            pl.forEachIndexed { i, fx ->
                val xb = w * fx
                val yb = bajo + w * (if (i % 2 == 0) 0.004f else 0.016f) - w * 0.01f * i / pl.size
                cubicTo(xa - (xa - xb) * 0.3f, bajo + w * 0.03f, xb + (xa - xb) * 0.3f, yb + w * 0.01f, xb, yb)
                xa = xb
            }
            close()
        }
        drawPath(corrido(caida, w * 0.01f, w * 0.008f), sombra(o, if (o) 0.4f else 0.2f))
        clipPath(caida) {
            vichy(Rect(-w * 0.01f, sf, x1 + w * 0.02f, bajo + w * 0.06f), luz(GLASEADO, o, kk), luz(CARMIN, o, kk), paso)
            // Los pliegues: el lado de cada uno a la sombra y el otro a la luz.
            listOf(0.08f, 0.2f, 0.31f, 0.41f).forEachIndexed { i, fx ->
                val x = w * fx
                drawPath(poli(x, sf + w * 0.012f, x + w * 0.03f, sf + w * 0.012f, x + w * 0.045f, bajo + w * 0.04f, x + w * 0.012f, bajo + w * 0.04f), sombra(o, if (o) 0.28f else 0.16f))
                drawPath(poli(x - w * 0.02f, sf + w * 0.012f, x, sf + w * 0.012f, x + w * 0.012f, bajo + w * 0.04f, x - w * 0.008f, bajo + w * 0.04f), claro(o, 0.22f, kk))
            }
            // El borde del sobre que se marca debajo de la tela.
            drawRect(sombra(o, 0.25f), Offset(-w * 0.01f, sf), Size(x1 + w * 0.02f, w * 0.006f))
            drawRect(claro(o, 0.3f, kk), Offset(-w * 0.01f, sf + w * 0.006f), Size(x1 + w * 0.02f, 1f * u))
            drawPath(caida, luz(mezcla(CARMIN, PINO_OSC, 0.4f), o, kk).copy(alpha = 0.6f), style = Stroke(2.4f * u))
        }
        // La harina sobre el mantel, junto al rodillo.
        listOf(Offset(w * 0.12f, sf - w * 0.012f), Offset(w * 0.2f, sf - w * 0.006f), Offset(w * 0.27f, sf - w * 0.016f)).forEachIndexed { i, p ->
            drawOval(luz(GLASEADO, o, kk).copy(alpha = 0.7f), Offset(p.x - w * 0.04f, p.y - w * 0.006f), Size(w * (0.08f - 0.015f * i), w * 0.012f))
            for (j in 0 until 6) drawCircle(luz(GLASEADO, o, kk).copy(alpha = 0.8f), 0.8f * u, Offset(p.x + w * 0.05f * (azar(1610 + i, j) - 0.5f) * 2f, p.y + w * 0.008f * (azar(1611 + i, j) - 0.5f)))
        }
    }

    /**
     * La tira de bombillas, colgada del canto en festones con sus pinzas: de día se ven las bombillas apagadas, de vidrio de
     * color; de noche están encendidas, cada una con su halo chico de su color en el canto y en el mantel.
     */
    private fun DrawScope.tira(w: Float, o: Boolean) {
        val u = density
        val yc = w * TIRA_Y - w * 0.012f
        val sag = w * 0.024f
        val pinzas = 8
        val cable = luz(mezcla(FILETE, NOCHE, 0.4f), o, 0.6f)
        // De noche, primero el resplandor sobre el canto y la primera fila: unas pocas manchas grandes, de colores.
        if (o) for (j in 0 until 4) mancha(Offset(w * (0.12f + 0.25f * j), yc + w * 0.04f), w * 0.2f, BOMBILLAS[j].copy(alpha = 0.16f))
        var n = 0
        for (s in 0 until pinzas) {
            val xa = w * (s / (pinzas - 1f)) * 1.04f - w * 0.02f
            val xb = w * ((s + 1) / (pinzas - 1f)) * 1.04f - w * 0.02f
            if (xa > w) break
            val p = Path().apply { moveTo(xa, yc); cubicTo(xa + (xb - xa) * 0.3f, yc + sag * 1.3f, xa + (xb - xa) * 0.7f, yc + sag * 1.3f, xb, yc) }
            drawPath(corrido(p, 1.2f * u, 2f * u), sombra(o, 0.25f), style = Stroke(1f * u))
            drawPath(p, cable, style = Stroke(1.1f * u))
            // La pinza que la sujeta al canto.
            drawRect(luz(mezcla(FILETE, NOCHE, 0.3f), o, 0.6f), Offset(xa - 1.6f * u, yc - w * 0.012f), Size(3.2f * u, w * 0.014f))
            for (f in listOf(0.22f, 0.5f, 0.78f)) {
                val x = xa + (xb - xa) * f
                val y = yc + sag * 4f * f * (1f - f) * 0.97f
                val giro = (f - 0.5f) * 50f
                val col = n % 4
                n++
                rotate(giro, Offset(x, y)) {
                    // El casquillo y la bombilla, en forma de gota que mira abajo.
                    drawRect(luz(mezcla(FILETE, NOCHE, 0.2f), o, 0.7f), Offset(x - 1.6f * u, y), Size(3.2f * u, 3.4f * u))
                    val b = Path().apply {
                        moveTo(x - 1.8f * u, y + 3.2f * u)
                        cubicTo(x - 3.6f * u, y + 6f * u, x - 2f * u, y + 10.5f * u, x, y + 11f * u)
                        cubicTo(x + 2f * u, y + 10.5f * u, x + 3.6f * u, y + 6f * u, x + 1.8f * u, y + 3.2f * u)
                        close()
                    }
                    if (!o) {
                        val vidrio = listOf(CARMIN, FILETE, AZUL_LATA, ORO_VIEJO)[col]
                        drawPath(corrido(b, 1.4f * u, 1.4f * u), sombraMueble(0.2f))
                        drawPath(b, mezcla(vidrio, LUZ_MUEBLE, 0.25f))
                        drawPath(borde(b, -1.4f * u, 0f), sombraMueble(0.2f))
                        drawCircle(luzMueble(0.8f), 0.8f * u, Offset(x - 1.2f * u, y + 5.5f * u))
                    } else {
                        val lc = BOMBILLAS[col]
                        drawCircle(lc.copy(alpha = 0.12f), 11f * u, Offset(x, y + 8f * u))
                        drawCircle(lc.copy(alpha = 0.2f), 6f * u, Offset(x, y + 8f * u))
                        drawPath(b, mezcla(lc, Color.White, 0.25f))
                        drawCircle(mezcla(lc, Color.White, 0.7f), 1.4f * u, Offset(x, y + 7.5f * u))
                    }
                }
            }
        }
    }

    /** La llama de la vela: la gota amarilla con su corazón claro y el pabilo. */
    private fun DrawScope.llama(p: Offset, s: Float) {
        mancha(p, s * 1.4f, LLAMA.copy(alpha = 0.55f))
        val f = Path().apply {
            moveTo(p.x, p.y - s * 0.75f)
            cubicTo(p.x + s * 0.32f, p.y - s * 0.2f, p.x + s * 0.28f, p.y + s * 0.3f, p.x, p.y + s * 0.32f)
            cubicTo(p.x - s * 0.28f, p.y + s * 0.3f, p.x - s * 0.32f, p.y - s * 0.2f, p.x, p.y - s * 0.75f)
            close()
        }
        drawPath(f, LLAMA)
        drawOval(Color(0xFFFFF4D6), Offset(p.x - s * 0.1f, p.y - s * 0.15f), Size(s * 0.2f, s * 0.4f))
    }

    /** La palmatoria de latón: el platillo con su asa de anillo, la vela de cera crema con sus chorreras y el pabilo. */
    private fun DrawScope.palmatoria(pie: Offset, w: Float, o: Boolean, k: (Float, Float) -> Float, caida: Offset) {
        val u = density
        val kk = maxOf(k(pie.x, pie.y - w * 0.06f), if (o) 0.85f else 0f)
        val laton = luz(ORO_VIEJO, o, kk)
        val rx = w * 0.05f; val ry = w * 0.011f
        val velaW = w * 0.022f
        val arriba = w * VELA_Y + w * 0.022f
        // Las sombras en la pared y en el sobre.
        drawRect(sombra(o, if (o) 0.4f else 0.18f), Offset(pie.x - velaW / 2 + caida.x, arriba + caida.y), Size(velaW, pie.y - arriba))
        drawOval(sombra(o, if (o) 0.5f else 0.32f), Offset(pie.x - rx * 0.9f, pie.y - ry * 0.4f), Size(rx * 2.2f, ry * 1.8f))
        // El asa de anillo, a la izquierda.
        drawCircle(laton, w * 0.014f, Offset(pie.x - rx * 1.05f, pie.y - ry * 1.3f), style = Stroke(w * 0.005f))
        // El platillo: el borde vuelto, el fondo y el cañón.
        drawOval(luz(mezcla(ORO_VIEJO, PINO_OSC, 0.4f), o, kk), Offset(pie.x - rx, pie.y - ry * 2f), Size(rx * 2, ry * 2.2f))
        drawOval(laton, Offset(pie.x - rx, pie.y - ry * 2.6f), Size(rx * 2, ry * 2f))
        drawOval(luz(mezcla(ORO_VIEJO, PINO_OSC, 0.25f), o, kk), Offset(pie.x - rx * 0.8f, pie.y - ry * 2.3f), Size(rx * 1.6f, ry * 1.4f))
        drawArc(claro(o, 0.7f, kk), 190f, 70f, false, Offset(pie.x - rx, pie.y - ry * 2.6f), Size(rx * 2, ry * 2f), style = Stroke(1.2f * u))
        val canon = Rect(pie.x - velaW * 0.75f, pie.y - ry * 1.6f - w * 0.016f, pie.x + velaW * 0.75f, pie.y - ry * 1.4f)
        drawRect(laton, canon.topLeft, canon.size)
        drawRect(claro(o, 0.5f, kk), canon.topLeft, Size(canon.width * 0.25f, canon.height))
        drawRect(sombra(o, 0.3f), Offset(canon.right - canon.width * 0.3f, canon.top), Size(canon.width * 0.3f, canon.height))
        // La vela: cera crema, un poco consumida, con chorreras.
        val vela = Path().apply {
            moveTo(pie.x - velaW / 2, canon.top)
            lineTo(pie.x - velaW / 2, arriba + w * 0.004f)
            cubicTo(pie.x - velaW * 0.2f, arriba - w * 0.002f, pie.x + velaW * 0.2f, arriba + w * 0.004f, pie.x + velaW / 2, arriba)
            lineTo(pie.x + velaW / 2, canon.top); close()
        }
        drawPath(vela, luz(mezcla(PAPEL, ORO_VIEJO, 0.12f), o, kk))
        drawPath(Path().apply {
            moveTo(pie.x - velaW / 2, arriba + w * 0.004f); lineTo(pie.x - velaW * 0.1f, arriba + w * 0.002f)
            cubicTo(pie.x - velaW * 0.1f, arriba + w * 0.03f, pie.x - velaW * 0.32f, arriba + w * 0.04f, pie.x - velaW * 0.32f, arriba + w * 0.02f)
            lineTo(pie.x - velaW / 2, arriba + w * 0.03f); close()
        }, luz(mezcla(PAPEL, LUZ_MUEBLE, 0.4f), o, kk))
        volumen(vela, o, kk, if (o) Offset(0f, -1f) else Offset(-0.8f, -0.6f), 0.8f)
        if (o) drawRect(LLAMA.copy(alpha = 0.35f), Offset(pie.x - velaW / 2, arriba), Size(velaW, w * 0.012f))
        // El pabilo, negro.
        drawLine(luz(PINO_OSC, o, 0f).copy(alpha = 1f), Offset(pie.x, arriba + w * 0.002f), Offset(pie.x + w * 0.002f, arriba - w * 0.01f), 1.2f * u)
    }

    /**
     * La casita de jengibre a medio decorar, vista de tres cuartos: el hastial a la luz con su puerta y su ventanita redonda, el
     * costado en sombra con dos ventanas (una con su marco de glaseado, la otra todavía no), el tejado con el glaseado y los
     * carámbanos solo en la mitad de delante, caramelos pegados y una chimenea sin decorar. Sobre su cartón, con «nieve».
     */
    private fun DrawScope.casita(pie: Offset, s: Float, o: Boolean, k: (Float, Float) -> Float, hacia: (Float, Float) -> Offset, caida: Offset) {
        val u = density
        val kk = maxOf(k(pie.x, pie.y - s * 0.3f), if (o) 0.45f else 0f)
        val hl = hacia(pie.x, pie.y - s * 0.3f)
        val x0 = pie.x - s * 0.5f
        val gw = s * 0.4f
        val x1 = x0 + gw; val xm = x0 + gw / 2
        val yb = pie.y - s * 0.03f
        val yw = yb - s * 0.32f
        val yp = yw - s * 0.27f
        val D = Offset(s * 0.5f, -s * 0.07f)
        val masa = luz(JENGIBRE, o, kk)
        val masaLuz = luz(mezcla(JENGIBRE, LUZ_MUEBLE, 0.18f), o, kk)
        val masaSombra = luz(mezcla(JENGIBRE, SOMBRA_MUEBLE, 0.38f), o, kk)
        val glas = luz(GLASEADO, o, maxOf(kk, 0.3f))
        // El cartón con su «nieve» de glaseado, y la sombra de la casita en la pared y en el sobre.
        val carton = Rect(x0 - s * 0.06f, yb - s * 0.07f, x1 + D.x + s * 0.07f, pie.y + s * 0.02f)
        drawOval(sombra(o, if (o) 0.5f else 0.3f), Offset(carton.left + s * 0.04f, carton.top + s * 0.03f), carton.size)
        val silueta = poli(x0, yb, x0, yw, xm, yp, xm + D.x, yp + D.y, x1 + D.x + s * 0.03f, yw + D.y, x1 + D.x, yb + D.y)
        drawPath(corrido(silueta, caida.x, caida.y), sombra(o, if (o) 0.35f else 0.16f))
        drawOval(luz(mezcla(PINO, LUZ_MUEBLE, 0.4f), o, kk), carton.topLeft, carton.size)
        val nieve = Path().apply {
            moveTo(carton.left + s * 0.04f, carton.center.y)
            for (j in 0..10) {
                val f = j / 10f
                val ang = 3.1416f + 3.1416f * f
                val rr = 0.9f + 0.1f * azar(1620, j)
                lineTo(carton.center.x + carton.width * 0.48f * rr * cos(ang), carton.center.y + carton.height * 0.45f * rr * sin(ang) * -1f + carton.height * 0.05f)
            }
            for (j in 0..10) {
                val f = j / 10f
                val ang = 3.1416f * f
                val rr = 0.88f + 0.12f * azar(1621, j)
                lineTo(carton.center.x + carton.width * 0.48f * rr * cos(ang), carton.center.y + carton.height * 0.42f * rr * sin(ang))
            }
            close()
        }
        drawPath(nieve, glas)
        drawPath(borde(nieve, -s * 0.03f, -s * 0.02f), sombra(o, 0.12f))
        // El costado, en sombra, con su veta de horno.
        val costado = poli(x1, yb, x1 + D.x, yb + D.y, x1 + D.x, yw + D.y, x1, yw)
        drawPath(costado, masaSombra)
        clipPath(costado) {
            for (j in 0 until 18) drawCircle(luz(mezcla(JENGIBRE, PINO_OSC, 0.5f), o, kk).copy(alpha = 0.5f), 0.9f * u, Offset(x1 + D.x * azar(1622, j), yw + (yb - yw) * azar(1623, j) + D.y * azar(1622, j)))
            drawRect(sombra(o, 0.15f), Offset(x1 + D.x * 0.75f, yw + D.y), Size(D.x * 0.3f, yb - yw + s * 0.1f))
        }
        // Las dos ventanas del costado: la de delante con su marco de glaseado y su cruz; la de atrás, solo la masa.
        listOf(0.25f, 0.65f).forEachIndexed { i, f ->
            val wx = x1 + D.x * f; val wy = yw + (yb - yw) * 0.3f + D.y * f
            val v = poli(wx, wy, wx + s * 0.1f, wy + D.y * 0.2f, wx + s * 0.1f, wy + s * 0.11f + D.y * 0.2f, wx, wy + s * 0.11f)
            drawPath(v, luz(if (o) Color(0xFFE8A04A) else mezcla(PINO_OSC, NOCHE, 0.25f), o, if (o) maxOf(kk, 0.7f) else kk))
            if (i == 0) {
                drawPath(v, glas, style = Stroke(1.8f * u, join = StrokeJoin.Round))
                drawLine(glas, Offset(wx + s * 0.05f, wy + D.y * 0.1f), Offset(wx + s * 0.05f, wy + s * 0.11f + D.y * 0.1f), 1.2f * u)
                drawLine(glas, Offset(wx, wy + s * 0.055f), Offset(wx + s * 0.1f, wy + s * 0.055f + D.y * 0.2f), 1.2f * u)
            } else drawPath(v, masa, style = Stroke(1.2f * u))
        }
        // El hastial, a la luz.
        val hastial = poli(x0, yb, x1, yb, x1, yw, xm, yp, x0, yw)
        drawPath(hastial, masaLuz)
        clipPath(hastial) {
            for (j in 0 until 14) drawCircle(luz(mezcla(JENGIBRE, PINO_OSC, 0.45f), o, kk).copy(alpha = 0.45f), 0.9f * u, Offset(x0 + gw * azar(1624, j), yp + (yb - yp) * azar(1625, j)))
            drawRect(claro(o, 0.2f, kk), Offset(x0, yp), Size(gw * 0.2f, yb - yp))
        }
        // La puerta en arco, con su contorno de puntitos de glaseado; la ventanita redonda del hastial.
        val pw = gw * 0.34f
        val puerta = Path().apply {
            moveTo(xm - pw / 2, yb); lineTo(xm - pw / 2, yb - s * 0.12f)
            arcTo(Rect(xm - pw / 2, yb - s * 0.12f - pw / 2, xm + pw / 2, yb - s * 0.12f + pw / 2), 180f, 180f, false)
            lineTo(xm + pw / 2, yb); close()
        }
        drawPath(puerta, luz(mezcla(JENGIBRE, PINO_OSC, 0.55f), o, kk))
        for (j in 0 until 11) {
            val a = 3.1416f * j / 10f
            val p = if (j in 2..8) Offset(xm - (pw / 2 + 1.5f * u) * cos(a), yb - s * 0.12f - (pw / 2 + 1.5f * u) * sin(a)) else Offset(if (j < 2) xm - pw / 2 - 1.5f * u else xm + pw / 2 + 1.5f * u, yb - s * 0.12f + (j % 2) * s * 0.05f + s * 0.03f)
            drawCircle(glas, 1.3f * u, p)
        }
        drawCircle(luz(GLASEADO, o, kk), 1.1f * u, Offset(xm + pw * 0.25f, yb - s * 0.07f))
        val ov = Offset(xm, yw - s * 0.1f)
        drawCircle(luz(if (o) Color(0xFFE8A04A) else mezcla(PINO_OSC, NOCHE, 0.25f), o, if (o) maxOf(kk, 0.7f) else kk), s * 0.04f, ov)
        drawCircle(glas, s * 0.04f, ov, style = Stroke(1.6f * u))
        drawLine(glas, ov - Offset(s * 0.04f, 0f), ov + Offset(s * 0.04f, 0f), 1f * u)
        // El tejado: el faldón de delante sobre el costado.
        val P = Offset(xm, yp)
        val E = Offset(x1 + s * 0.04f, yw + s * 0.03f)
        fun en(a: Float, bb: Float) = Offset(P.x + D.x * a + (E.x - P.x) * bb, P.y + D.y * a + (E.y - P.y) * bb)
        val faldon = Path().apply { val q = listOf(en(0f, 0f), en(1.04f, 0f), en(1.04f, 1f), en(0f, 1f)); moveTo(q[0].x, q[0].y); q.drop(1).forEach { lineTo(it.x, it.y) }; close() }
        drawPath(faldon, luz(mezcla(JENGIBRE, SOMBRA_MUEBLE, 0.12f), o, kk))
        clipPath(faldon) {
            // La mitad de delante, glaseada: tejas de festón en hileras y la nieve gorda del caballete.
            for (fila in 0 until 4) {
                val bb = 0.18f + 0.22f * fila
                for (j in 0 until 7) {
                    val a = 0.04f + 0.085f * j + (if (fila % 2 == 0) 0f else 0.04f)
                    val p = en(a, bb)
                    drawArc(glas, 0f, 180f, false, Offset(p.x - s * 0.025f, p.y - s * 0.02f), Size(s * 0.05f, s * 0.04f), style = Stroke(1.2f * u))
                }
            }
            // La otra mitad, sin glasear: solo las marcas de la masa y unos puntitos de guía.
            for (j in 0 until 5) drawCircle(glas.copy(alpha = 0.8f), 0.9f * u, en(0.68f + 0.07f * j, 0.4f + 0.1f * (j % 2)))
            drawPath(borde(faldon, 0f, -s * 0.03f), sombra(o, 0.2f))
        }
        drawPath(Path().apply { val a = en(0f, 0f); val bb = en(1.04f, 0f); moveTo(a.x, a.y); lineTo(bb.x, bb.y) }, masaLuz, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        // La chimenea, sin decorar, atrás.
        val ch = en(0.82f, 0.25f)
        drawPath(poli(ch.x, ch.y, ch.x + s * 0.06f, ch.y - s * 0.01f, ch.x + s * 0.06f, ch.y - s * 0.1f, ch.x, ch.y - s * 0.09f), masaSombra)
        drawPath(poli(ch.x - s * 0.02f, ch.y - s * 0.09f, ch.x, ch.y - s * 0.09f, ch.x, ch.y + s * 0.01f, ch.x - s * 0.02f, ch.y + s * 0.012f), masaLuz)
        // La nieve del caballete y del alero, con sus carámbanos, solo en la mitad de delante.
        val nieveCab = Path().apply {
            val a = en(-0.02f, -0.06f); moveTo(a.x, a.y)
            for (j in 1..8) { val p = en(0.6f * j / 8f, -0.04f - 0.03f * azar(1626, j)); lineTo(p.x, p.y) }
            for (j in 8 downTo 0) { val p = en(0.6f * j / 8f, 0.1f + 0.05f * azar(1627, j)); lineTo(p.x, p.y) }
            close()
        }
        drawPath(nieveCab, glas)
        drawPath(borde(nieveCab, 0f, -s * 0.012f), sombra(o, 0.15f))
        val alero = Path().apply {
            val a = en(-0.02f, 0.9f); moveTo(a.x, a.y)
            for (j in 1..8) { val p = en(0.56f * j / 8f, 0.88f); lineTo(p.x, p.y) }
            for (j in 8 downTo 0) {
                val p = en(0.56f * j / 8f, 1.04f)
                val largo = if (j % 2 == 0) s * (0.03f + 0.03f * azar(1628, j)) else 0f
                lineTo(p.x + s * 0.008f, p.y + largo); lineTo(p.x, p.y)
            }
            close()
        }
        drawPath(alero, glas)
        // Las tablas del hastial (los bordes del tejado de delante), glaseadas, con sus chorreras.
        val tl = Offset(x0 - s * 0.035f, yw + s * 0.03f)
        drawLine(masa, tl, P, s * 0.032f, StrokeCap.Round)
        drawLine(masa, P, E, s * 0.032f, StrokeCap.Round)
        drawLine(glas, tl + Offset(0f, -s * 0.006f), P + Offset(0f, -s * 0.008f), s * 0.016f, StrokeCap.Round)
        drawLine(glas, P + Offset(0f, -s * 0.008f), E + Offset(0f, -s * 0.006f), s * 0.016f, StrokeCap.Round)
        for (j in 0 until 6) {
            val f = 0.12f + 0.15f * j
            val p = Offset(tl.x + (P.x - tl.x) * f, tl.y + (P.y - tl.y) * f)
            drawLine(glas, p, p + Offset(0f, s * (0.015f + 0.02f * azar(1629, j))), 1.6f * u, StrokeCap.Round)
        }
        // Los caramelos pegados: gotas de colores en el caballete y junto a la puerta.
        listOf(0.06f, 0.18f, 0.3f, 0.42f, 0.54f).forEachIndexed { i, a ->
            val p = en(a, 0.03f)
            drawCircle(luz(listOf(CARMIN, FILETE, ORO_VIEJO, CARMIN, AZUL_LATA)[i], o, kk), s * 0.017f, p)
            drawCircle(claro(o, 0.55f, kk), s * 0.006f, p - Offset(s * 0.005f, s * 0.005f))
        }
        listOf(-1f, 1f).forEach { l ->
            val p = Offset(xm + l * pw * 0.95f, yb - s * 0.03f)
            drawCircle(luz(if (l < 0) CARMIN else FILETE, o, kk), s * 0.016f, p)
            drawCircle(claro(o, 0.5f, kk), s * 0.006f, p - Offset(s * 0.005f, s * 0.005f))
        }
        volumen(hastial, o, kk, hl, 0.5f)
        // Caramelos sueltos en el cartón: un bastón a rayas y dos pastillas.
        val bc = Offset(x0 + s * 0.02f, yb + s * 0.035f)
        drawLine(luz(GLASEADO, o, kk), bc, bc + Offset(s * 0.1f, -s * 0.012f), s * 0.016f, StrokeCap.Round)
        for (j in 0 until 4) drawLine(luz(CARMIN, o, kk), bc + Offset(s * (0.012f + 0.024f * j), -s * 0.006f), bc + Offset(s * (0.022f + 0.024f * j), -s * 0.0f), s * 0.006f)
        drawArc(luz(GLASEADO, o, kk), 180f, 200f, false, Offset(bc.x - s * 0.03f, bc.y - s * 0.04f), Size(s * 0.035f, s * 0.04f), style = Stroke(s * 0.014f, cap = StrokeCap.Round))
        listOf(Offset(x1 + D.x * 0.6f, yb + s * 0.03f), Offset(x1 + D.x * 0.85f, yb + s * 0.005f)).forEachIndexed { i, p ->
            drawOval(sombra(o, 0.3f), Offset(p.x - s * 0.012f, p.y + s * 0.004f), Size(s * 0.034f, s * 0.012f))
            drawCircle(luz(if (i == 0) FILETE else CARMIN, o, kk), s * 0.016f, p)
            drawCircle(claro(o, 0.5f, kk), s * 0.006f, p - Offset(s * 0.006f, s * 0.006f))
        }
    }

    /** La manga pastelera de papel, tumbada, con el glaseado que se le salió por la punta. */
    private fun DrawScope.manga(punta: Offset, w: Float, o: Boolean, k: (Float, Float) -> Float) {
        val u = density
        val kk = k(punta.x, punta.y)
        val cola = punta + Offset(w * 0.095f, -w * 0.022f)
        val cono = poli(punta.x, punta.y, cola.x - w * 0.002f, cola.y - w * 0.018f, cola.x + w * 0.006f, cola.y + w * 0.014f)
        drawPath(corrido(cono, w * 0.004f, w * 0.006f), sombra(o, if (o) 0.4f else 0.25f))
        drawPath(cono, luz(PAPEL, o, kk))
        clipPath(cono) { drawRect(sombra(o, 0.18f), Offset(punta.x, punta.y - w * 0.004f), Size(w * 0.1f, w * 0.03f)) }
        // El papel retorcido de atrás.
        drawPath(poli(cola.x - w * 0.002f, cola.y - w * 0.018f, cola.x + w * 0.02f, cola.y - w * 0.012f, cola.x + w * 0.024f, cola.y + w * 0.006f, cola.x + w * 0.006f, cola.y + w * 0.014f), luz(mezcla(PAPEL, SOMBRA_MUEBLE, 0.15f), o, kk))
        drawLine(sombra(o, 0.3f), Offset(cola.x + w * 0.006f, cola.y - w * 0.014f), Offset(cola.x + w * 0.016f, cola.y + w * 0.008f), 0.8f * u)
        // El glaseado de la punta: un cordón que se enrosca en el sobre.
        val hilo = Path().apply {
            moveTo(punta.x, punta.y)
            cubicTo(punta.x - w * 0.02f, punta.y + w * 0.004f, punta.x - w * 0.012f, punta.y + w * 0.014f, punta.x - w * 0.03f, punta.y + w * 0.012f)
            cubicTo(punta.x - w * 0.045f, punta.y + w * 0.01f, punta.x - w * 0.035f, punta.y + w * 0.002f, punta.x - w * 0.026f, punta.y + w * 0.004f)
        }
        drawPath(hilo, luz(GLASEADO, o, maxOf(kk, 0.3f)), style = Stroke(2.2f * u, cap = StrokeCap.Round))
    }

    /** El cuenco de loza crema con filete verde, la masa dentro y la cuchara de palo que sale hacia la pared. */
    private fun DrawScope.cuenco(pie: Offset, ancho: Float, o: Boolean, k: (Float, Float) -> Float, hacia: (Float, Float) -> Offset, caida: Offset) {
        val u = density
        val kk = k(pie.x, pie.y - ancho * 0.3f)
        val rx = ancho / 2; val ry = ancho * 0.12f
        val alto = ancho * 0.42f
        val boca = Rect(pie.x - rx, pie.y - alto - ry, pie.x + rx, pie.y - alto + ry)
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y)
            cubicTo(boca.left + rx * 0.02f, pie.y - alto * 0.2f, pie.x - rx * 0.5f, pie.y, pie.x, pie.y)
            cubicTo(pie.x + rx * 0.5f, pie.y, boca.right - rx * 0.02f, pie.y - alto * 0.2f, boca.right, boca.center.y)
            arcTo(boca, 0f, 180f, false); close()
        }
        drawPath(corrido(cuerpo, caida.x, caida.y), sombra(o, if (o) 0.35f else 0.16f))
        drawOval(sombra(o, if (o) 0.5f else 0.3f), Offset(pie.x - rx * 0.6f, pie.y - ry * 0.4f), Size(rx * 1.5f, ry * 0.9f))
        // La cuchara de palo, atrás: el mango sale de la masa hacia arriba a la izquierda.
        val c0 = Offset(pie.x + rx * 0.15f, boca.center.y)
        val c1 = Offset(pie.x - rx * 0.95f, boca.top - ancho * 0.75f)
        drawLine(sombra(o, if (o) 0.35f else 0.18f), c0 + caida, c1 + caida, ancho * 0.07f, StrokeCap.Round)
        drawLine(luz(PINO, o, kk), c0, c1, ancho * 0.065f, StrokeCap.Round)
        drawLine(claro(o, 0.4f, kk), c0 + Offset(-ancho * 0.012f, -ancho * 0.01f), c1 + Offset(-ancho * 0.012f, -ancho * 0.01f), ancho * 0.015f, StrokeCap.Round)
        drawLine(luz(mezcla(PINO, PINO_OSC, 0.5f), o, kk), c0 + (c1 - c0) * 0.2f, c0 + (c1 - c0) * 0.45f, ancho * 0.03f, StrokeCap.Round)
        // La boca: el borde y la masa.
        drawOval(luz(mezcla(CREMA, SOMBRA_MUEBLE, 0.3f), o, kk), boca.topLeft, boca.size)
        drawOval(luz(mezcla(GLASEADO, JENGIBRE, 0.35f), o, kk), Offset(boca.left + rx * 0.1f, boca.top + ry * 0.35f), Size(rx * 1.8f, ry * 1.4f))
        drawOval(claro(o, 0.3f, kk), Offset(boca.left + rx * 0.4f, boca.top + ry * 0.5f), Size(rx * 0.6f, ry * 0.5f))
        // La cuchara asoma de la masa.
        drawOval(luz(PINO, o, kk), Offset(c0.x - ancho * 0.05f, c0.y - ancho * 0.03f), Size(ancho * 0.1f, ancho * 0.05f))
        // El cuerpo de loza: crema, con el filete verde y el desconchón.
        drawPath(cuerpo, luz(CREMA, o, kk))
        clipPath(cuerpo) {
            drawRect(luz(FILETE, o, kk), Offset(boca.left, boca.center.y + alto * 0.18f), Size(ancho, alto * 0.12f))
            drawRect(luz(FILETE, o, kk), Offset(boca.left, boca.center.y + alto * 0.36f), Size(ancho, alto * 0.04f))
        }
        drawOval(luz(PINO_OSC, o, kk).copy(alpha = 0.7f), Offset(pie.x + rx * 0.45f, boca.center.y + alto * 0.05f), Size(ancho * 0.05f, ancho * 0.025f))
        volumen(cuerpo, o, kk, hacia(pie.x, pie.y), 1f, 0.35f)
        drawArc(claro(o, 0.6f, kk), 180f, 120f, false, boca.topLeft, boca.size, style = Stroke(1.2f * u))
    }

    /** El rodillo de pino tumbado en el mantel, con harina pegada y sus dos mangos. */
    private fun DrawScope.rodillo(a: Offset, b: Offset, r: Float, o: Boolean, k: (Float, Float) -> Float) {
        val u = density
        val kk = k((a.x + b.x) / 2, a.y)
        val v = b - a; val lg = v.getDistance(); val dir = v / lg
        val m0 = a + dir * (lg * 0.14f); val m1 = b - dir * (lg * 0.14f)
        drawLine(sombra(o, if (o) 0.45f else 0.28f), a + Offset(r * 0.4f, r * 1.1f), b + Offset(r * 0.4f, r * 1.1f), r * 1.6f, StrokeCap.Round)
        drawLine(luz(mezcla(PINO, PINO_OSC, 0.25f), o, kk), a, m0, r * 0.75f, StrokeCap.Round)
        drawLine(luz(mezcla(PINO, PINO_OSC, 0.25f), o, kk), m1, b, r * 0.75f, StrokeCap.Round)
        drawLine(luz(mezcla(PINO, PINO_OSC, 0.15f), o, kk), m0, m1, r * 2f, StrokeCap.Butt)
        drawLine(luz(PINO_OSC, o, kk).copy(alpha = 0.5f), m0 + Offset(0f, r * 0.95f), m1 + Offset(0f, r * 0.95f), 1f * u, StrokeCap.Butt)
        drawLine(sombra(o, 0.3f), m0 + Offset(0f, r * 0.6f), m1 + Offset(0f, r * 0.6f), r * 0.8f, StrokeCap.Butt)
        drawLine(claro(o, 0.45f, kk), m0 + Offset(0f, -r * 0.5f), m1 + Offset(0f, -r * 0.5f), r * 0.4f, StrokeCap.Butt)
        for (j in 0 until 7) {
            val p = m0 + (m1 - m0) * azar(1640, j) + Offset(0f, r * (azar(1641, j) - 0.6f))
            drawOval(luz(GLASEADO, o, kk).copy(alpha = 0.75f), Offset(p.x - r * 0.6f, p.y - r * 0.25f), Size(r * 1.2f, r * 0.5f))
        }
        drawLine(sombra(o, 0.4f), m0 + Offset(0f, -r), m0 + Offset(0f, r), 0.8f * u)
        drawLine(sombra(o, 0.4f), m1 + Offset(0f, -r), m1 + Offset(0f, r), 0.8f * u)
    }

    /** El tarro de vidrio con los palitos de canela que asoman. */
    private fun DrawScope.tarroCanela(pie: Offset, ancho: Float, alto: Float, o: Boolean, k: (Float, Float) -> Float, hacia: (Float, Float) -> Offset, caida: Offset) {
        val u = density
        val kk = k(pie.x, pie.y - alto / 2)
        val r = Rect(pie.x - ancho / 2, pie.y - alto, pie.x + ancho / 2, pie.y)
        drawRoundRect(sombra(o, if (o) 0.3f else 0.14f), r.topLeft + caida, r.size, CornerRadius(ancho * 0.18f))
        // Los palitos: canela enrollada, de alturas distintas, ladeados.
        for (j in 0 until 6) {
            val x = r.left + ancho * (0.15f + 0.14f * j)
            val top = r.top - alto * (0.15f + 0.4f * azar(1650, j))
            val inc = (azar(1651, j) - 0.5f) * ancho * 0.3f
            val col = luz(mezcla(JENGIBRE, PINO_OSC, 0.25f + 0.25f * azar(1652, j)), o, kk)
            drawLine(col, Offset(x, r.bottom - alto * 0.05f), Offset(x + inc, top), ancho * 0.12f, StrokeCap.Butt)
            drawLine(luz(mezcla(JENGIBRE, PINO_OSC, 0.7f), o, kk), Offset(x + ancho * 0.03f, r.bottom - alto * 0.05f), Offset(x + inc + ancho * 0.03f, top), ancho * 0.025f)
            drawOval(luz(mezcla(JENGIBRE, PINO_OSC, 0.6f), o, kk), Offset(x + inc - ancho * 0.06f, top - ancho * 0.03f), Size(ancho * 0.12f, ancho * 0.06f))
        }
        // El vidrio: un velo verdoso, el reflejo largo a la luz, el canto oscuro del otro lado y el borde grueso de la boca.
        drawRoundRect(luz(mezcla(SALVIA, PAPEL, 0.4f), o, kk).copy(alpha = 0.3f), r.topLeft, r.size, CornerRadius(ancho * 0.18f))
        drawRect(claro(o, 0.6f, kk), Offset(r.left + ancho * 0.12f, r.top + alto * 0.12f), Size(ancho * 0.08f, alto * 0.72f))
        drawRect(sombra(o, 0.25f), Offset(r.right - ancho * 0.14f, r.top + alto * 0.1f), Size(ancho * 0.1f, alto * 0.8f))
        drawRoundRect(luz(mezcla(SALVIA, PAPEL, 0.3f), o, kk), Offset(r.left - ancho * 0.03f, r.top - alto * 0.04f), Size(ancho * 1.06f, alto * 0.08f), CornerRadius(ancho * 0.04f))
        drawRoundRect(luz(mezcla(SALVIA, PAPEL, 0.6f), o, kk).copy(alpha = 0.5f), Offset(r.left, r.bottom - alto * 0.08f), Size(ancho, alto * 0.08f), CornerRadius(ancho * 0.1f))
        if (!o) drawCircle(luzMueble(0.8f), ancho * 0.03f, Offset(r.left + ancho * 0.2f, r.top + alto * 0.18f))
        else drawRect(FILO_MUEBLE.copy(alpha = 0.6f * kk), Offset(r.left + ancho * (if (hacia(pie.x, pie.y).x > 0) 0.8f else 0.1f), r.top + alto * 0.1f), Size(ancho * 0.06f, alto * 0.7f))
        drawLine(luz(CARMIN, o, kk), Offset(r.left, r.top + alto * 0.12f), Offset(r.right, r.top + alto * 0.12f), 1.4f * u)
    }

    /** Una naranja con clavos de olor clavados en hileras, con su volumen y su sombra. */
    private fun DrawScope.naranja(c: Offset, r: Float, semilla: Int, o: Boolean, k: (Float, Float) -> Float, hacia: (Float, Float) -> Offset) {
        val u = density
        val kk = k(c.x, c.y)
        drawOval(sombra(o, if (o) 0.5f else 0.32f), Offset(c.x - r * 0.6f, c.y + r * 0.6f), Size(r * 1.9f, r * 0.6f))
        val f = Path().apply { addOval(Rect(c, r)) }
        drawPath(f, luz(NARANJA, o, kk))
        clipPath(f) {
            for (fila in -2..2) for (j in -3..3) {
                val a = j * 0.42f + azar(semilla, fila) * 0.2f
                val x = c.x + r * sin(a) * 0.95f
                val y = c.y + fila * r * 0.36f + abs(sin(a)) * r * 0.04f
                if (hypot(x - c.x, y - c.y) < r * 0.88f) {
                    drawCircle(luz(PINO_OSC, o, kk), r * 0.07f, Offset(x, y))
                    drawCircle(luz(mezcla(PINO_OSC, JENGIBRE, 0.5f), o, kk), r * 0.035f, Offset(x - r * 0.02f, y - r * 0.02f))
                }
            }
        }
        volumen(f, o, kk, hacia(c.x, c.y), 1f, 0.3f)
    }

    /** El plato con galletas: una estrella, una redonda glaseada, un hombrecillo y otra mordida. */
    private fun DrawScope.plato(c: Offset, ancho: Float, o: Boolean, k: (Float, Float) -> Float) {
        val u = density
        val kk = k(c.x, c.y)
        val rx = ancho / 2; val ry = ancho * 0.16f
        drawOval(sombra(o, if (o) 0.45f else 0.28f), Offset(c.x - rx * 0.9f, c.y - ry * 0.7f), Size(rx * 2.05f, ry * 2.1f))
        drawOval(luz(PAPEL, o, kk), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(luz(FILETE, o, kk), Offset(c.x - rx * 0.92f, c.y - ry * 0.88f), Size(rx * 1.84f, ry * 1.76f), style = Stroke(1.2f * u))
        drawOval(luz(mezcla(PAPEL, SOMBRA_MUEBLE, 0.15f), o, kk), Offset(c.x - rx * 0.66f, c.y - ry * 0.6f), Size(rx * 1.32f, ry * 1.2f))
        val galleta = luz(mezcla(JENGIBRE, PINO, 0.4f), o, kk)
        // La redonda mordida, de canto contra el borde de atrás.
        val g1 = Offset(c.x + rx * 0.35f, c.y - ry * 0.7f)
        val redonda = Path().apply { op(Path().apply { addOval(Rect(g1, ancho * 0.16f)) }, Path().apply { addOval(Rect(g1 + Offset(ancho * 0.13f, -ancho * 0.1f), ancho * 0.07f)) }, PathOperation.Difference) }
        drawPath(corrido(redonda, 1.4f * u, 1.4f * u), sombra(o, 0.3f))
        drawPath(redonda, galleta)
        drawCircle(luz(GLASEADO, o, kk), ancho * 0.09f, g1 + Offset(-ancho * 0.02f, ancho * 0.02f))
        listOf(CARMIN, FILETE).forEachIndexed { i, cc -> drawCircle(luz(cc, o, kk), ancho * 0.015f, g1 + Offset(ancho * (0.02f * i - 0.04f), ancho * 0.01f * i)) }
        drawPath(borde(redonda, -ancho * 0.02f, -ancho * 0.02f), sombra(o, 0.25f))
        // La estrella, tumbada.
        val g2 = Offset(c.x - rx * 0.4f, c.y - ry * 0.1f)
        val est = estrella(g2, ancho * 0.16f, 5, 0.5f, -80f)
        drawPath(corrido(est, 1.2f * u, 1.4f * u), sombra(o, 0.3f))
        drawPath(est, luz(mezcla(JENGIBRE, PINO, 0.25f), o, kk))
        drawPath(est, luz(GLASEADO, o, kk), style = Stroke(1f * u))
        drawPath(borde(est, -ancho * 0.015f, -ancho * 0.015f), sombra(o, 0.2f))
        // El hombrecillo, a la derecha, encima de la estrella.
        val g3 = Offset(c.x + rx * 0.3f, c.y + ry * 0.1f)
        val s = ancho * 0.25f
        val cuerpo = Path().apply {
            addOval(Rect(g3 + Offset(0f, -s * 0.38f), s * 0.17f))
            addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(g3.x - s * 0.17f, g3.y - s * 0.24f, g3.x + s * 0.17f, g3.y + s * 0.2f), CornerRadius(s * 0.1f)))
        }
        drawPath(corrido(cuerpo, 1.4f * u, 1.4f * u), sombra(o, 0.3f))
        drawPath(cuerpo, galleta)
        drawLine(galleta, g3 + Offset(-s * 0.12f, -s * 0.16f), g3 + Offset(-s * 0.36f, -s * 0.02f), s * 0.13f, StrokeCap.Round)
        drawLine(galleta, g3 + Offset(s * 0.12f, -s * 0.16f), g3 + Offset(s * 0.36f, -s * 0.06f), s * 0.13f, StrokeCap.Round)
        drawLine(galleta, g3 + Offset(-s * 0.08f, s * 0.14f), g3 + Offset(-s * 0.16f, s * 0.38f), s * 0.14f, StrokeCap.Round)
        drawLine(galleta, g3 + Offset(s * 0.08f, s * 0.14f), g3 + Offset(s * 0.18f, s * 0.36f), s * 0.14f, StrokeCap.Round)
        listOf(-0.1f, 0.02f).forEach { f -> drawCircle(luz(GLASEADO, o, kk), s * 0.03f, g3 + Offset(0f, s * f)) }
        drawLine(luz(GLASEADO, o, kk), g3 + Offset(-s * 0.08f, -s * 0.34f), g3 + Offset(s * 0.08f, -s * 0.34f), 0.8f * u)
        drawPath(borde(cuerpo, -s * 0.06f, -s * 0.05f), sombra(o, 0.2f))
        // Migas.
        for (j in 0 until 5) drawCircle(galleta, 0.9f * u, Offset(c.x + rx * (azar(1660, j) - 0.5f) * 1.4f, c.y + ry * (azar(1661, j) - 0.2f) * 0.8f))
    }

    /** La taza de loza roja con lunares, su asa a la derecha, y el vapor que sube. */
    private fun DrawScope.taza(pie: Offset, ancho: Float, o: Boolean, k: (Float, Float) -> Float, hacia: (Float, Float) -> Offset, caida: Offset) {
        val u = density
        val kk = k(pie.x, pie.y - ancho * 0.5f)
        val alto = ancho * 0.85f
        val r = Rect(pie.x - ancho / 2, pie.y - alto, pie.x + ancho / 2, pie.y)
        val ry = ancho * 0.12f
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.left + ancho * 0.03f, r.bottom - ry)
            arcTo(Rect(r.left + ancho * 0.03f, r.bottom - ry * 2, r.right - ancho * 0.03f, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top); close()
        }
        drawPath(corrido(cuerpo, caida.x, caida.y), sombra(o, if (o) 0.35f else 0.16f))
        drawOval(sombra(o, if (o) 0.5f else 0.3f), Offset(r.left + ancho * 0.1f, r.bottom - ry * 0.6f), Size(ancho * 1.05f, ry * 1.2f))
        // El asa.
        drawArc(luz(CARMIN, o, kk), -80f, 170f, false, Offset(r.right - ancho * 0.2f, r.top + alto * 0.18f), Size(ancho * 0.42f, alto * 0.5f), style = Stroke(ancho * 0.08f))
        drawPath(cuerpo, luz(CARMIN, o, kk))
        clipPath(cuerpo) {
            for (j in 0 until 9) drawCircle(luz(GLASEADO, o, kk), ancho * 0.035f, Offset(r.left + ancho * (0.12f + 0.25f * (j % 4) + (if (j / 4 % 2 == 0) 0f else 0.12f)), r.top + alto * (0.25f + 0.25f * (j / 4))))
        }
        volumen(cuerpo, o, kk, hacia(pie.x, pie.y), 1f, 0.3f)
        // La boca y el té.
        drawOval(luz(mezcla(CARMIN, PINO_OSC, 0.3f), o, kk), Offset(r.left, r.top - ry), Size(ancho, ry * 2))
        drawOval(luz(mezcla(JENGIBRE, PINO_OSC, 0.55f), o, kk), Offset(r.left + ancho * 0.08f, r.top - ry * 0.6f), Size(ancho * 0.84f, ry * 1.4f))
        drawArc(claro(o, 0.6f, kk), 190f, 90f, false, Offset(r.left, r.top - ry), Size(ancho, ry * 2), style = Stroke(1f * u))
        // El vapor: tres hilos curvos, pálidos.
        for (j in 0 until 3) {
            val x = r.left + ancho * (0.3f + 0.2f * j)
            val v = Path().apply {
                moveTo(x, r.top - ry)
                cubicTo(x - ancho * 0.15f, r.top - alto * 0.4f, x + ancho * 0.18f, r.top - alto * 0.6f, x + ancho * (0.02f * j), r.top - alto * (0.95f + 0.15f * j))
            }
            drawPath(v, luz(GLASEADO, o, maxOf(kk, 0.2f)).copy(alpha = if (o) 0.18f else 0.45f), style = Stroke(ancho * 0.05f, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- abajo

    /** El suelo de barro: baldosas en dos hileras que se acercan, con sus juntas, el roce y la sombra del aparador. */
    private fun DrawScope.pisoBarro(r: Rect, o: Boolean) {
        val u = density
        val w = size.width
        val barro = Color(0xFFB4664A)
        drawRect(luz(mezcla(barro, CARMIN, 0.1f), o, 0f), r.topLeft, r.size)
        val vx = w * 0.5f; val vy = r.top - w * 1.2f
        val filas = listOf(r.top, r.top + r.height * 0.4f, r.bottom)
        for (f in 0 until 2) {
            val y0 = filas[f]; val y1 = filas[f + 1]
            for (j in -6..6) {
                val xb = vx + j * w * 0.12f * (y1 - vy) / (r.bottom - vy)
                val xa = vx + (xb - vx) * (y0 - vy) / (y1 - vy)
                val xb2 = vx + (j + 1) * w * 0.12f * (y1 - vy) / (r.bottom - vy)
                val xa2 = vx + (xb2 - vx) * (y0 - vy) / (y1 - vy)
                val tono = mezcla(barro, if ((j + f) % 2 == 0) PINO else SOMBRA_MUEBLE, 0.12f * azar(1670 + f, j + 6))
                drawPath(poli(xa, y0, xa2, y0, xb2, y1, xb, y1), luz(tono, o, 0f))
                drawLine(luz(mezcla(barro, SOMBRA_MUEBLE, 0.4f), o, 0f), Offset(xa, y0), Offset(xb, y1), 1f * u)
            }
            drawLine(luz(mezcla(barro, SOMBRA_MUEBLE, 0.4f), o, 0f), Offset(0f, y0), Offset(w, y0), 1f * u)
        }
        pinceladas(r, luz(mezcla(barro, LUZ_MUEBLE, 0.4f), o, 0f), 1672, 14, u, 0.1f)
        drawRect(sombra(o, if (o) 0.4f else 0.3f), r.topLeft, Size(r.width, r.height * 0.3f))
        drawRect(sombra(o, 0.15f), Offset(r.left, r.top + r.height * 0.3f), Size(r.width, r.height * 0.25f))
    }

    /** Un tirador de porcelana blanca con su vástago de latón y su sombra. */
    private fun DrawScope.tirador(c: Offset, r: Float, o: Boolean) {
        drawCircle(sombra(o, 0.4f), r * 1.05f, c + Offset(r * 0.4f, r * 0.45f))
        drawCircle(luz(ORO_VIEJO, o, 0f), r * 0.55f, c + Offset(r * 0.1f, r * 0.1f))
        drawCircle(luz(GLASEADO, o, 0.05f), r * 0.9f, c)
        drawCircle(luz(mezcla(GLASEADO, SOMBRA_MUEBLE, 0.25f), o, 0.05f), r * 0.9f, c, style = Stroke(r * 0.15f))
        drawCircle(if (o) FILO_MUEBLE.copy(alpha = 0.25f) else luzMueble(0.9f), r * 0.25f, c - Offset(r * 0.3f, r * 0.3f))
        drawCircle(luz(FILETE, o, 0.05f), r * 0.38f, c, style = Stroke(r * 0.1f))
    }

    /** El tarjetero de latón del cajón, con su tarjeta: de día crema; de noche algo apagada, pero el texto se lee. */
    private fun DrawScope.tarjetero(r: Rect, o: Boolean, conTexto: Boolean) {
        val u = density
        drawRect(sombra(o, 0.35f), Offset(r.left + 1.4f * u, r.top + 1.8f * u), r.size)
        drawRect(luz(ORO_VIEJO, o, 0.25f), r.topLeft, r.size)
        drawRect(claro(o, 0.5f, 0.25f), r.topLeft, Size(r.width, 1f * u))
        val m = r.height * 0.14f
        drawRect(if (o) mezcla(PAPEL, NOCHE, 0.34f) else PAPEL, Offset(r.left + m, r.top + m), Size(r.width - 2 * m, r.height - 2 * m))
        if (!conTexto) drawLine(luz(PINO_OSC, o, 0.3f).copy(alpha = 0.3f), Offset(r.left + r.width * 0.3f, r.center.y), Offset(r.right - r.width * 0.3f, r.center.y), 0.8f * u)
        listOf(r.left + m * 0.5f, r.right - m * 0.5f).forEach { drawCircle(luz(mezcla(ORO_VIEJO, PINO_OSC, 0.4f), o, 0.2f), 1.2f * u, Offset(it, r.center.y)) }
    }

    /** El cesto de mimbre con piñas y ramas de abeto, con su asa. */
    private fun DrawScope.cesto(pie: Offset, ancho: Float, o: Boolean) {
        val u = density
        val kk = 0f
        val alto = ancho * 0.42f
        val boca = pie.y - alto
        val forma = Path().apply {
            moveTo(pie.x - ancho * 0.4f, pie.y); lineTo(pie.x + ancho * 0.4f, pie.y)
            cubicTo(pie.x + ancho * 0.48f, pie.y - alto * 0.3f, pie.x + ancho * 0.52f, boca + alto * 0.2f, pie.x + ancho * 0.5f, boca)
            lineTo(pie.x - ancho * 0.5f, boca)
            cubicTo(pie.x - ancho * 0.52f, boca + alto * 0.2f, pie.x - ancho * 0.48f, pie.y - alto * 0.3f, pie.x - ancho * 0.4f, pie.y)
            close()
        }
        drawOval(sombra(o, if (o) 0.55f else 0.35f), Offset(pie.x - ancho * 0.4f, pie.y - ancho * 0.03f), Size(ancho * 1.05f, ancho * 0.07f))
        // El asa, detrás.
        drawArc(luz(mezcla(PINO, PINO_OSC, 0.3f), o, kk), 180f, 180f, false, Offset(pie.x - ancho * 0.36f, boca - alto * 0.9f), Size(ancho * 0.72f, alto * 1.8f), style = Stroke(ancho * 0.035f))
        drawArc(claro(o, 0.3f, kk), 190f, 60f, false, Offset(pie.x - ancho * 0.36f, boca - alto * 0.9f), Size(ancho * 0.72f, alto * 1.8f), style = Stroke(ancho * 0.01f))
        // Las ramas de abeto, que asoman.
        for (j in 0 until 4) {
            val b0 = Offset(pie.x - ancho * 0.3f + ancho * 0.2f * j, boca + alto * 0.1f)
            val b1 = b0 + Offset(ancho * (0.1f * (j - 1.5f)), -alto * (0.5f + 0.3f * azar(1680, j)))
            drawLine(luz(PINO_OSC, o, kk), b0, b1, 1.2f * u)
            for (m in 0 until 7) {
                val p = b0 + (b1 - b0) * (m / 7f)
                listOf(-1f, 1f).forEach { l -> drawLine(luz(if (m % 2 == 0) FILETE else mezcla(FILETE, SALVIA, 0.35f), o, kk), p, p + Offset(l * ancho * 0.04f, -ancho * 0.015f), 1.3f * u, StrokeCap.Round) }
            }
        }
        // Las piñas, encima, con sus escamas.
        listOf(Offset(-0.18f, -0.05f), Offset(0.06f, -0.1f), Offset(0.26f, -0.02f)).forEachIndexed { i, f ->
            val c = Offset(pie.x + ancho * f.x, boca + alto * f.y)
            val rx = ancho * 0.09f; val ry = ancho * 0.12f
            rotate(-30f + 30f * i, c) {
                val pina = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }
                drawPath(pina, luz(mezcla(PINO_OSC, JENGIBRE, 0.3f), o, kk))
                clipPath(pina) {
                    for (fy in 0 until 5) for (fx in 0 until 3) {
                        val q = Offset(c.x - rx * 0.66f + rx * 0.66f * fx + (if (fy % 2 == 0) 0f else rx * 0.33f), c.y - ry * 0.8f + ry * 0.4f * fy)
                        drawArc(luz(mezcla(PINO, JENGIBRE, 0.3f), o, kk), 20f, 140f, false, Offset(q.x - rx * 0.3f, q.y - ry * 0.18f), Size(rx * 0.6f, ry * 0.36f), style = Stroke(1.4f * u))
                    }
                }
                volumen(pina, o, kk, if (o) Offset(0f, -1f) else Offset(-0.8f, -0.6f), 0.8f)
            }
        }
        // El cesto: mimbre trenzado en hileras, con su borde.
        drawPath(forma, luz(mezcla(PINO, GLASEADO, 0.25f), o, kk))
        clipPath(forma) {
            var y = boca + 2f * u; var j = 0
            while (y < pie.y) {
                val paso = ancho * 0.045f
                var x = pie.x - ancho * 0.55f + (if (j % 2 == 0) 0f else paso / 2)
                while (x < pie.x + ancho * 0.55f) { drawOval(luz(mezcla(PINO, PINO_OSC, 0.2f), o, kk), Offset(x, y - 1.2f * u), Size(paso * 0.7f, 2.4f * u)); x += paso }
                y += 3.2f * u; j++
            }
        }
        volumen(forma, o, kk, if (o) Offset(0f, -1f) else Offset(-0.8f, -0.6f))
        drawLine(luz(mezcla(PINO, PINO_OSC, 0.35f), o, kk), Offset(pie.x - ancho * 0.5f, boca), Offset(pie.x + ancho * 0.5f, boca), 3f * u, StrokeCap.Round)
        drawLine(claro(o, 0.35f, kk), Offset(pie.x - ancho * 0.5f, boca - 1f * u), Offset(pie.x, boca - 1f * u), 1f * u, StrokeCap.Round)
    }

    /** El regalo envuelto en papel verde con puntos dorados, con su cinta roja y su lazo, cortado por el borde. */
    private fun DrawScope.regalo(r: Rect, o: Boolean) {
        val u = density
        val kk = 0f
        val tapa = r.height * 0.22f
        drawOval(sombra(o, if (o) 0.55f else 0.35f), Offset(r.left - r.width * 0.05f, r.bottom - r.height * 0.1f), Size(r.width * 1.1f, r.height * 0.2f))
        val frente = Rect(r.left, r.top + tapa, r.right, r.bottom)
        drawRect(luz(FILETE, o, kk), frente.topLeft, frente.size)
        drawPath(poli(r.left, r.top + tapa, r.left + r.width * 0.12f, r.top, r.right + r.width * 0.12f, r.top, r.right, r.top + tapa), luz(mezcla(FILETE, LUZ_MUEBLE, 0.2f), o, kk))
        for (j in 0 until 18) {
            val p = Offset(frente.left + frente.width * azar(1690, j), frente.top + frente.height * azar(1691, j))
            drawCircle(luz(ORO_VIEJO, o, kk), 1.4f * u, p)
        }
        drawRect(claro(o, 0.25f, kk), frente.topLeft, Size(frente.width * 0.08f, frente.height))
        // La cinta, que cruza el frente y la tapa.
        val cx = r.left + r.width * 0.38f
        drawRect(luz(CARMIN, o, kk), Offset(cx - r.width * 0.05f, frente.top), Size(r.width * 0.1f, frente.height))
        drawPath(poli(cx - r.width * 0.05f, frente.top, cx + r.width * 0.05f, frente.top, cx + r.width * 0.15f, r.top, cx + r.width * 0.05f, r.top), luz(CARMIN, o, kk))
        drawPath(poli(r.left + r.width * 0.06f, r.top + tapa * 0.5f, r.right + r.width * 0.06f, r.top + tapa * 0.5f, r.right + r.width * 0.06f, r.top + tapa * 0.75f, r.left + r.width * 0.04f, r.top + tapa * 0.75f), luz(CARMIN, o, kk))
        // El lazo, encima.
        val n = Offset(cx + r.width * 0.08f, r.top + tapa * 0.55f)
        for (l in listOf(-1f, 1f)) {
            val ala = Path().apply {
                moveTo(n.x, n.y)
                cubicTo(n.x + l * r.width * 0.12f, n.y - r.height * 0.32f, n.x + l * r.width * 0.3f, n.y - r.height * 0.18f, n.x + l * r.width * 0.2f, n.y + r.height * 0.02f)
                close()
            }
            drawPath(ala, luz(CARMIN, o, kk))
            drawPath(borde(ala, l * r.width * 0.03f, -r.height * 0.04f), sombra(o, 0.3f))
        }
        drawCircle(luz(mezcla(CARMIN, PINO_OSC, 0.3f), o, kk), r.width * 0.035f, n)
        drawRect(sombra(o, 0.2f), Offset(r.right - r.width * 0.25f, frente.top), Size(r.width * 0.25f, frente.height))
        if (o) drawLine(FILO_MUEBLE.copy(alpha = 0.3f), Offset(cx - r.width * 0.04f, frente.top + 2f * u), Offset(cx - r.width * 0.04f, frente.bottom - 2f * u), 1f * u)
    }

    // ---------------------------------------------------------------- los compartimentos

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val o = oscuro
        val ab = c.abierto.coerceIn(0f, 1f)
        val centro = centroHueco(c)
        val k = if (centro != null) calor(centro.x, centro.y) else 0.45f
        val hacia = if (centro != null) haciaLuz(centro.x, centro.y, o) else if (o) Offset(0f, -1f) else Offset(-0.8f, -0.6f)
        val pie = h * 0.95f
        if (c.vacio) { vacio(c.semilla, pie, o, k, hacia); return }
        val m = (c.semilla xor (c.semilla ushr 3)) ushr 1
        val tipo = if (c.sinTema) 3 else Math.floorMod(m, 3)
        val corre = w * 0.02f * ((m ushr 2) % 3 - 1)
        // Cuántas tarjetas asoman: más cuantas más fichas.
        val tarjetas = c.n.coerceIn(0, 9)
        when (tipo) {
            0 -> lataRedonda(c, corre, pie, tarjetas, ab, o, k, hacia, m)
            1 -> lataCuadros(c, corre, pie, tarjetas, ab, o, k, hacia, m)
            2 -> tarro(c, corre, pie, tarjetas, ab, o, k, hacia, m)
            else -> cajaPasteleria(c, corre, pie, tarjetas, ab, o, k, hacia)
        }
        etiqueta(c.sinTema, o, k)
        // De noche, en las primeras filas, los destellos de colores de las bombillas en el metal de arriba.
        if (o && centro != null && c.fila <= 1 && tipo != 3) {
            val f = if (c.fila == 0) 1f else 0.45f
            for (j in 0 until 4) {
                val x = w * (0.16f + 0.2f * j + 0.04f * azar(c.semilla, j + 300))
                val y = h * (0.37f + 0.03f * azar(c.semilla, j + 301))
                drawCircle(BOMBILLAS[(j + c.col) % 4].copy(alpha = 0.18f * f), w * 0.03f, Offset(x, y))
                drawCircle(mezcla(BOMBILLAS[(j + c.col) % 4], Color.White, 0.4f).copy(alpha = 0.85f * f), 1.3f * u, Offset(x, y))
            }
        } else if (o && tipo != 3) {
            // Las de abajo: casi perdidas, con un destello del metal.
            drawCircle(FILO_MUEBLE.copy(alpha = 0.35f), 1.1f * u, Offset(w * (0.2f + 0.1f * azar(c.semilla, 310)), h * 0.5f))
        }
    }

    /** Las tarjetas que asoman de un recipiente: papeles de felicitación con su filete, en abanico, entre [x0] y [x1]. */
    private fun DrawScope.tarjetasAsoman(x0: Float, x1: Float, boca: Float, cuantas: Int, sube: Float, semilla: Int, o: Boolean, k: Float) {
        val u = density
        val h = size.height
        if (cuantas <= 0) return
        val visibles = cuantas.coerceAtMost(7)
        for (i in 0 until visibles) {
            val f = if (visibles == 1) 0.5f else i / (visibles - 1f)
            val x = x0 + (x1 - x0) * (0.1f + 0.8f * f)
            val alto = h * (0.06f + 0.012f * cuantas + 0.04f * azar(semilla, i + 200)) + sube
            val ancho = (x1 - x0) * 0.2f
            val giro = (f - 0.5f) * 24f + (azar(semilla, i + 201) - 0.5f) * 8f
            rotate(giro, Offset(x, boca)) {
                val r = Rect(x - ancho / 2, boca - alto, x + ancho / 2, boca + h * 0.02f)
                drawRect(sombra(o, 0.3f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
                drawRect(luz(if (i % 3 == 1) mezcla(PAPEL, CARMIN, 0.08f) else PAPEL, o, k), r.topLeft, r.size)
                drawRect(luz(if (i % 2 == 0) CARMIN else FILETE, o, k), Offset(r.left + ancho * 0.12f, r.top + ancho * 0.12f), Size(ancho * 0.76f, alto * 0.7f), style = Stroke(0.7f * u))
                if (i % 3 == 0) drawCircle(luz(CARMIN, o, k), ancho * 0.08f, Offset(r.center.x, r.top + ancho * 0.35f))
                drawRect(claro(o, 0.3f, k), r.topLeft, Size(1f * u, r.height))
            }
        }
    }

    /** La sombra de un recipiente en el fondo de la balda (solo sobre lo pintado: no entra en el aura). */
    private fun DrawScope.sombraFondo(forma: Path, o: Boolean, k: Float, hacia: Offset) {
        val w = size.width
        if (!o) sombraEn(corrido(forma, w * 0.05f, w * 0.012f), sombraMueble(0.16f))
        else if (k > 0.06f) sombraEn(corrido(forma, -hacia.x * w * 0.04f, -hacia.y * w * 0.05f), NOCHE.copy(alpha = (0.7f * k).coerceAtMost(0.5f)))
        sombraEn(Path().apply { addOval(Rect(w * 0.08f, size.height * 0.92f, w * 0.98f, size.height * 0.99f)) }, sombra(o, if (o) 0.35f + 0.2f * k else 0.3f))
    }

    /** La lata redonda de galletas: roja, azul o verde, con copos, filetes dorados, una abolladura y la tapa entreabierta. */
    private fun DrawScope.lataRedonda(c: EstadoCajon, corre: Float, pie: Float, tarjetas: Int, ab: Float, o: Boolean, k: Float, hacia: Offset, m: Int) {
        val u = density
        val w = size.width; val h = size.height
        val color = listOf(CARMIN, AZUL_LATA, FILETE)[Math.floorMod(m ushr 3, 3)]
        val x0 = w * 0.05f + corre; val x1 = w * 0.95f + corre
        val arriba = h * (0.4f + 0.04f * azar(c.semilla, 1))
        val ry = h * 0.05f
        val cuerpo = Path().apply {
            moveTo(x0, arriba); lineTo(x1, arriba); lineTo(x1, pie - ry)
            arcTo(Rect(x0, pie - ry * 2, x1, pie), 0f, 180f, false); close()
        }
        sombraFondo(cuerpo, o, k, hacia)
        cubreAura(o, listOf(cuerpo, Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(x0 - w * 0.015f, arriba - h * 0.125f - h * 0.012f * tarjetas.coerceAtMost(6) - ab * h * 0.18f, x1 + w * 0.015f, arriba + h * 0.02f), CornerRadius(w * 0.2f, h * 0.05f))) }))
        // La tapa sube con las fichas y al abrir: las tarjetas asoman por el hueco.
        val sube = h * 0.012f * tarjetas.coerceAtMost(6) + ab * h * 0.18f
        tarjetasAsoman(x0 + w * 0.06f, x1 - w * 0.06f, arriba + h * 0.01f, tarjetas, ab * h * 0.12f, c.semilla, o, k)
        val metal = luz(color, o, k)
        drawPath(cuerpo, metal)
        clipPath(cuerpo) {
            // Los filetes dorados, el friso de copos en los extremos (lejos de la etiqueta) y el brillo del metal.
            drawRect(luz(ORO_VIEJO, o, k), Offset(x0, arriba + h * 0.05f), Size(x1 - x0, h * 0.018f))
            drawRect(luz(ORO_VIEJO, o, k), Offset(x0, pie - ry - h * 0.06f), Size(x1 - x0, h * 0.012f))
            for (j in 0 until 6) {
                val lado = if (j % 2 == 0) 0.1f else 0.9f
                val p = Offset(x0 + (x1 - x0) * (lado + 0.05f * (azar(c.semilla, j + 10) - 0.5f)), arriba + h * (0.13f + 0.12f * (j / 2)))
                copo(p, w * (0.025f + 0.012f * azar(c.semilla, j + 11)), luz(GLASEADO, o, k).copy(alpha = 0.55f), 0.8f * u)
            }
            drawRect(claro(o, 0.3f, k), Offset(x0 + w * 0.08f, arriba), Size(w * 0.05f, pie - arriba))
            drawRect(claro(o, 0.55f, k), Offset(x0 + w * 0.15f, arriba), Size(w * 0.012f, pie - arriba))
            drawRect(sombra(o, 0.18f), Offset(x1 - w * 0.16f, arriba), Size(w * 0.16f, pie - arriba))
            drawRect(sombra(o, 0.2f), Offset(x1 - w * 0.06f, arriba), Size(w * 0.06f, pie - arriba))
            // La abolladura: una hondonada con su luz del otro lado.
            val dx = if (azar(c.semilla, 20) < 0.5f) x0 + w * 0.2f else x1 - w * 0.22f
            val dy = pie - h * (0.12f + 0.06f * azar(c.semilla, 21))
            drawOval(sombra(o, 0.3f), Offset(dx - w * 0.03f, dy - h * 0.025f), Size(w * 0.06f, h * 0.05f))
            drawArc(claro(o, 0.5f, k), 0f, 120f, false, Offset(dx - w * 0.03f, dy - h * 0.025f), Size(w * 0.06f, h * 0.05f), style = Stroke(1f * u))
            // Un roce donde la pintura se gastó y asoma la hojalata.
            drawLine(luz(PLATA, o, k).copy(alpha = 0.6f), Offset(x0 + w * 0.3f, pie - ry * 1.4f), Offset(x0 + w * 0.42f, pie - ry * 1.2f), 1f * u, StrokeCap.Round)
        }
        drawPath(borde(cuerpo, 0f, -h * 0.02f), luz(mezcla(color, NOCHE, 0.4f), o, k))
        // La tapa: la banda con su filete y el óvalo de arriba, ladeada si va entreabierta.
        val giro = -(tarjetas.coerceAtMost(6) * 0.8f + ab * 8f)
        rotate(giro, Offset(x1, arriba)) {
            translate(0f, -sube) {
                val tx0 = x0 - w * 0.015f; val tx1 = x1 + w * 0.015f
                val tb = Rect(tx0, arriba - h * 0.075f, tx1, arriba + h * 0.012f)
                drawRect(luz(mezcla(color, NOCHE, 0.18f), o, k), tb.topLeft, tb.size)
                drawOval(luz(mezcla(color, NOCHE, 0.18f), o, k), Offset(tx0, tb.bottom - ry), Size(tx1 - tx0, ry * 2))
                drawOval(luz(mezcla(color, LUZ_MUEBLE, 0.12f), o, k), Offset(tx0, tb.top - ry), Size(tx1 - tx0, ry * 2))
                drawOval(luz(ORO_VIEJO, o, k), Offset(tx0 + w * 0.06f, tb.top - ry * 0.7f), Size(tx1 - tx0 - w * 0.12f, ry * 1.4f), style = Stroke(1.2f * u))
                drawLine(luz(ORO_VIEJO, o, k), Offset(tx0, tb.center.y), Offset(tx1, tb.center.y), 1.2f * u)
                drawRect(claro(o, 0.4f, k), Offset(tx0 + w * 0.08f, tb.top), Size(w * 0.04f, tb.height))
                drawRect(sombra(o, 0.2f), Offset(tx1 - w * 0.1f, tb.top), Size(w * 0.1f, tb.height))
                drawArc(claro(o, 0.7f, k), 190f, 60f, false, Offset(tx0, tb.top - ry), Size(tx1 - tx0, ry * 2), style = Stroke(1.2f * u))
            }
        }
    }

    /** La lata rectangular de tapa con bisagra, de cuadros escoceses: la tapa se abre hacia atrás y asoman las tarjetas. */
    private fun DrawScope.lataCuadros(c: EstadoCajon, corre: Float, pie: Float, tarjetas: Int, ab: Float, o: Boolean, k: Float, hacia: Offset, m: Int) {
        val u = density
        val w = size.width; val h = size.height
        val base = if ((m ushr 3) % 2 == 0) CARMIN else FILETE
        val cruce = if (base == CARMIN) FILETE else CARMIN
        val x0 = w * 0.07f + corre; val x1 = w * 0.93f + corre
        val arriba = h * (0.44f + 0.03f * azar(c.semilla, 2))
        val fondo = h * 0.07f
        val frente = Rect(x0, arriba, x1, pie)
        val silueta = poli(x0, pie, x0, arriba, x0 + w * 0.03f, arriba - fondo, x1 + w * 0.03f, arriba - fondo, x1, arriba, x1, pie)
        sombraFondo(silueta, o, k, hacia)
        cubreAura(o, listOf(silueta))
        // La tapa: de cerrada (plana) a abierta (de pie, detrás), según las fichas y al abrir.
        val abre = (0.12f + 0.06f * tarjetas.coerceAtMost(6) + ab * 0.6f).coerceAtMost(1f)
        val altoTapa = fondo + (h * 0.3f - fondo) * abre
        val boca = poli(x0, arriba, x0 + w * 0.03f, arriba - fondo, x1 + w * 0.03f, arriba - fondo, x1, arriba)
        // La tapa de pie, por dentro (hojalata), con su borde de cuadros.
        val tapa = poli(x0 + w * 0.03f, arriba - fondo, x1 + w * 0.03f, arriba - fondo, x1 + w * 0.03f - w * 0.02f * abre, arriba - fondo - altoTapa * abre, x0 + w * 0.03f + w * 0.02f * abre, arriba - fondo - altoTapa * abre)
        if (abre > 0.15f) {
            drawPath(tapa, luz(mezcla(PLATA, ORO_VIEJO, 0.25f), o, k))
            drawPath(borde(tapa, 0f, w * 0.015f), luz(base, o, k))
            drawPath(borde(tapa, w * 0.06f, 0f), sombra(o, 0.2f))
        }
        drawPath(boca, luz(mezcla(PLATA, NOCHE, 0.5f), o, k))
        tarjetasAsoman(x0 + w * 0.04f, x1 - w * 0.02f, arriba - fondo * 0.3f, tarjetas, ab * h * 0.1f, c.semilla, o, k)
        if (abre <= 0.15f) {
            drawPath(boca, luz(mezcla(base, LUZ_MUEBLE, 0.12f), o, k))
            drawPath(boca, luz(cruce, o, k).copy(alpha = 0.4f), style = Stroke(1.4f * u))
        }
        // El frente de cuadros escoceses: franjas anchas y finas que se cruzan, y una línea dorada.
        drawRect(luz(base, o, k), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            val paso = w * 0.12f
            var x = frente.left - paso * 0.3f
            while (x < frente.right) {
                drawRect(luz(cruce, o, k).copy(alpha = 0.45f), Offset(x, frente.top), Size(paso * 0.35f, frente.height))
                drawRect(luz(ORO_VIEJO, o, k).copy(alpha = 0.7f), Offset(x + paso * 0.62f, frente.top), Size(1f * u, frente.height))
                x += paso
            }
            var y = frente.top + h * 0.02f
            while (y < frente.bottom) {
                drawRect(luz(cruce, o, k).copy(alpha = 0.4f), Offset(frente.left, y), Size(frente.width, paso * 0.3f))
                drawRect(luz(NOCHE, o, k).copy(alpha = 0.2f), Offset(frente.left, y + paso * 0.45f), Size(frente.width, 1.2f * u))
                y += paso * 0.8f
            }
            drawRect(claro(o, 0.3f, k), frente.topLeft, Size(w * 0.05f, frente.height))
            drawRect(sombra(o, 0.2f), Offset(frente.right - w * 0.12f, frente.top), Size(w * 0.12f, frente.height))
        }
        // Los bordes enrollados de la hojalata, arriba y abajo, con su brillo; la bisagra asoma atrás.
        drawRect(luz(mezcla(PLATA, ORO_VIEJO, 0.3f), o, k), Offset(x0, arriba - 1.5f * u), Size(x1 - x0, 3f * u))
        drawRect(claro(o, 0.7f, k), Offset(x0, arriba - 1.5f * u), Size((x1 - x0) * 0.6f, 1f * u))
        drawRect(luz(mezcla(PLATA, ORO_VIEJO, 0.3f), o, k), Offset(x0, pie - 3f * u), Size(x1 - x0, 3f * u))
        drawRect(luz(mezcla(base, NOCHE, 0.4f), o, k), Offset(x1 - 1f * u, arriba), Size(w * 0.03f, pie - arriba))
        drawPath(poli(x1, arriba, x1 + w * 0.03f, arriba - fondo, x1 + w * 0.03f, pie - fondo, x1, pie), luz(mezcla(base, NOCHE, 0.35f), o, k))
        // Un golpe en la esquina, donde saltó la pintura.
        drawPath(poli(x0, pie - h * 0.08f, x0 + w * 0.04f, pie - h * 0.05f, x0 + w * 0.02f, pie, x0, pie), luz(PLATA, o, k).copy(alpha = 0.7f))
    }

    /** El tarro de cristal ancho con tapa de tela a cuadros atada con cordel; las tarjetas se ven dentro. */
    private fun DrawScope.tarro(c: EstadoCajon, corre: Float, pie: Float, tarjetas: Int, ab: Float, o: Boolean, k: Float, hacia: Offset, m: Int) {
        val u = density
        val w = size.width; val h = size.height
        val tela = if ((m ushr 3) % 2 == 0) CARMIN else FILETE
        val x0 = w * 0.08f + corre; val x1 = w * 0.92f + corre
        val hombro = h * (0.4f + 0.03f * azar(c.semilla, 3))
        val cuello = hombro - h * 0.07f
        val cx = (x0 + x1) / 2
        val vidrio = Path().apply {
            moveTo(cx - w * 0.3f, cuello)
            lineTo(cx - w * 0.3f, hombro - h * 0.02f)
            cubicTo(x0, hombro - h * 0.01f, x0, hombro + h * 0.02f, x0, hombro + h * 0.08f)
            lineTo(x0, pie - h * 0.05f)
            cubicTo(x0, pie, x0 + w * 0.02f, pie, x0 + w * 0.06f, pie)
            lineTo(x1 - w * 0.06f, pie)
            cubicTo(x1 - w * 0.02f, pie, x1, pie, x1, pie - h * 0.05f)
            lineTo(x1, hombro + h * 0.08f)
            cubicTo(x1, hombro + h * 0.02f, x1, hombro - h * 0.01f, cx + w * 0.3f, hombro - h * 0.02f)
            lineTo(cx + w * 0.3f, cuello); close()
        }
        sombraFondo(vidrio, o, k, hacia)
        cubreAura(o, listOf(vidrio, Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(cx - w * 0.36f, cuello - h * 0.06f - ab * h * 0.12f, cx + w * 0.36f, cuello + h * 0.07f), CornerRadius(w * 0.2f, h * 0.05f))) }))
        // El fondo del tarro (lo de detrás, visto por el vidrio, más apagado) y las tarjetas dentro, de pie.
        drawPath(vidrio, luz(mezcla(mezcla(SALVIA, SOMBRA_MUEBLE, 0.12f), PAPEL, 0.25f), o, k * 0.85f))
        clipPath(vidrio) {
            val n = tarjetas.coerceAtMost(9)
            val lleno = hombro + h * (0.1f - 0.012f * n) - ab * h * 0.1f
            for (i in 0 until maxOf(n, 1)) {
                val f = if (n <= 1) 0.5f else i / (n - 1f)
                val x = x0 + (x1 - x0) * (0.15f + 0.7f * f)
                val giro = (f - 0.5f) * 30f + (azar(c.semilla, i + 220) - 0.5f) * 10f
                if (n == 0) break
                rotate(giro, Offset(x, pie)) {
                    val r = Rect(x - w * 0.07f, lleno - h * 0.05f * azar(c.semilla, i + 221), x + w * 0.07f, pie)
                    drawRect(luz(if (i % 2 == 0) PAPEL else mezcla(PAPEL, CARMIN, 0.1f), o, k * 0.9f), r.topLeft, r.size)
                    drawRect(luz(if (i % 2 == 0) FILETE else CARMIN, o, k * 0.9f), Offset(r.left + w * 0.012f, r.top + w * 0.012f), Size(r.width - w * 0.024f, r.height * 0.5f), style = Stroke(0.7f * u))
                }
            }
            // El vidrio: velo verdoso, el reflejo largo, el canto de sombra y el fondo grueso.
            drawRect(luz(SALVIA, o, k).copy(alpha = 0.18f), Offset(x0, cuello), Size(x1 - x0, pie - cuello))
            // El hombro del vidrio, que recoge la luz en una curva.
            drawArc(claro(o, 0.7f, k), 185f, 50f, false, Offset(x0 + w * 0.03f, hombro - h * 0.03f), Size(w * 0.5f, h * 0.2f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawRect(claro(o, 0.55f, k), Offset(x0 + w * 0.06f, hombro + h * 0.06f), Size(w * 0.03f, pie - hombro - h * 0.14f))
            drawRect(claro(o, 0.3f, k), Offset(x0 + w * 0.11f, hombro + h * 0.06f), Size(w * 0.012f, pie - hombro - h * 0.2f))
            drawRect(sombra(o, 0.2f), Offset(x1 - w * 0.06f, hombro), Size(w * 0.06f, pie - hombro))
            drawRect(luz(mezcla(SALVIA, PAPEL, 0.5f), o, k).copy(alpha = 0.5f), Offset(x0, pie - h * 0.035f), Size(x1 - x0, h * 0.035f))
        }
        drawPath(vidrio, luz(mezcla(SALVIA, NOCHE, 0.3f), o, k).copy(alpha = 0.6f), style = Stroke(1.2f * u))
        // La tapa de tela: cubre la boca y cae en ondas por los lados, atada con cordel y su lazo. Al abrir se levanta.
        translate(0f, -ab * h * 0.12f) {
            val tx0 = cx - w * 0.36f; val tx1 = cx + w * 0.36f
            val pano = Path().apply {
                moveTo(cx - w * 0.31f, cuello - h * 0.035f)
                cubicTo(cx - w * 0.15f, cuello - h * 0.07f, cx + w * 0.15f, cuello - h * 0.07f, cx + w * 0.31f, cuello - h * 0.035f)
                lineTo(tx1, cuello + h * 0.05f)
                for (j in 7 downTo 0) {
                    val x = tx0 + (tx1 - tx0) * j / 7f
                    lineTo(x + (tx1 - tx0) / 14f, cuello + h * (0.07f + 0.012f * (j % 2)))
                    lineTo(x, cuello + h * 0.05f)
                }
                close()
            }
            drawPath(corrido(pano, w * 0.01f, h * 0.012f), sombra(o, 0.3f))
            clipPath(pano) {
                vichy(Rect(tx0, cuello - h * 0.08f, tx1, cuello + h * 0.1f), luz(GLASEADO, o, k), luz(tela, o, k), w * 0.03f)
                drawRect(sombra(o, 0.2f), Offset(tx1 - w * 0.14f, cuello - h * 0.08f), Size(w * 0.14f, h * 0.18f))
                drawRect(claro(o, 0.2f, k), Offset(tx0, cuello - h * 0.08f), Size(w * 0.08f, h * 0.18f))
            }
            // El cordel, apretado en el cuello, con su lazo y sus cabos.
            val cy = cuello + h * 0.012f
            drawLine(luz(mezcla(PINO, PAPEL, 0.4f), o, k), Offset(cx - w * 0.3f, cy), Offset(cx + w * 0.3f, cy), 1.4f * u)
            val nudo = Offset(cx - w * 0.16f, cy)
            listOf(-1f, 1f).forEach { l ->
                drawOval(luz(mezcla(PINO, PAPEL, 0.4f), o, k), Offset(nudo.x + (if (l < 0) -w * 0.07f else 0f), nudo.y - h * 0.035f), Size(w * 0.07f, h * 0.04f), style = Stroke(1.2f * u))
                drawLine(luz(mezcla(PINO, PAPEL, 0.4f), o, k), nudo, nudo + Offset(l * w * 0.03f, h * 0.07f), 1.2f * u)
            }
        }
    }

    /** La caja de cartón de pastelería atada con cordel rojo y blanco: lo que no tiene tema. */
    private fun DrawScope.cajaPasteleria(c: EstadoCajon, corre: Float, pie: Float, tarjetas: Int, ab: Float, o: Boolean, k: Float, hacia: Offset) {
        val u = density
        val w = size.width; val h = size.height
        val carton = mezcla(PAPEL, PINO, 0.12f)
        val x0 = w * 0.08f + corre; val x1 = w * 0.92f + corre
        val arriba = h * 0.42f
        val fondo = h * 0.06f
        val silueta = poli(x0, pie, x0, arriba, x0 + w * 0.03f, arriba - fondo, x1 + w * 0.03f, arriba - fondo, x1, arriba, x1, pie)
        sombraFondo(silueta, o, k, hacia)
        cubreAura(o, listOf(silueta))
        // Al abrir, la solapa se levanta y asoman las tarjetas.
        if (ab > 0.02f || tarjetas > 4) tarjetasAsoman(x0 + w * 0.05f, x1 - w * 0.05f, arriba - fondo * 0.5f, tarjetas, ab * h * 0.12f, c.semilla, o, k)
        val tapa = poli(x0, arriba, x0 + w * 0.03f, arriba - fondo - ab * h * 0.1f, x1 + w * 0.03f, arriba - fondo - ab * h * 0.1f, x1, arriba)
        drawPath(tapa, luz(mezcla(carton, LUZ_MUEBLE, 0.25f), o, k))
        drawRect(luz(carton, o, k), Offset(x0, arriba), Size(x1 - x0, pie - arriba))
        drawPath(poli(x1, arriba, x1 + w * 0.03f, arriba - fondo, x1 + w * 0.03f, pie - fondo, x1, pie), luz(mezcla(carton, SOMBRA_MUEBLE, 0.35f), o, k))
        drawRect(claro(o, 0.3f, k), Offset(x0, arriba), Size(w * 0.04f, pie - arriba))
        drawRect(sombra(o, 0.12f), Offset(x1 - w * 0.12f, arriba), Size(w * 0.12f, pie - arriba))
        // La solapa del frente, el pliegue y una mancha de mantequilla.
        drawLine(sombra(o, 0.25f), Offset(x0, arriba + h * 0.08f), Offset(x1, arriba + h * 0.08f), 1f * u)
        drawOval(luz(mezcla(carton, JENGIBRE, 0.3f), o, k).copy(alpha = 0.35f), Offset(x1 - w * 0.25f, pie - h * 0.12f), Size(w * 0.1f, h * 0.06f))
        // El cordel rojo y blanco que la ata en cruz, con su lazo arriba.
        val cx = (x0 + x1) / 2 + w * 0.12f
        fun cordel(a: Offset, b: Offset) {
            drawLine(luz(GLASEADO, o, k), a, b, 1.6f * u)
            drawLine(luz(CARMIN, o, k), a, b, 1.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f * u, 3f * u)))
        }
        cordel(Offset(cx, arriba - fondo * 0.5f), Offset(cx, pie))
        cordel(Offset(x0, arriba + h * 0.03f), Offset(x1, arriba + h * 0.03f))
        val nudo = Offset(cx + w * 0.015f, arriba - fondo * 0.5f)
        listOf(-1f, 1f).forEach { l ->
            drawOval(luz(CARMIN, o, k), Offset(nudo.x + (if (l < 0) -w * 0.08f else 0f), nudo.y - h * 0.045f), Size(w * 0.08f, h * 0.05f), style = Stroke(1.4f * u))
        }
    }

    /** La etiqueta alargada (óvalo de puntas redondas) donde va el tema: papel con filete dorado y rojo; en blanco sin tema. */
    private fun DrawScope.etiqueta(enBlanco: Boolean, o: Boolean, k: Float) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(w * rotulo.left - w * 0.035f, h * rotulo.top - h * 0.03f, w * rotulo.right + w * 0.035f, h * rotulo.bottom + h * 0.025f)
        val rad = CornerRadius(r.height / 2)
        drawRoundRect(sombra(o, if (o) 0.4f else 0.25f), r.topLeft + Offset(1.4f * u, 1.8f * u), r.size, rad)
        val papel = if (o) mezcla(mezcla(PAPEL, NOCHE, 0.06f + 0.28f * (1f - k)), FILO_MUEBLE, 0.12f * k) else PAPEL
        drawRoundRect(papel, r.topLeft, r.size, rad)
        if (!enBlanco) {
            drawRoundRect(luz(ORO_VIEJO, o, maxOf(k, 0.4f)), r.topLeft + Offset(2f * u, 2f * u), Size(r.width - 4f * u, r.height - 4f * u), CornerRadius(r.height / 2 - 2f * u), style = Stroke(1f * u))
            drawRoundRect(luz(CARMIN, o, maxOf(k, 0.4f)).copy(alpha = 0.6f), r.topLeft + Offset(3.6f * u, 3.6f * u), Size(r.width - 7.2f * u, r.height - 7.2f * u), CornerRadius(r.height / 2 - 3.6f * u), style = Stroke(0.6f * u))
        }
        if (!o) drawRoundRect(luzMueble(0.35f), r.topLeft + Offset(r.height * 0.3f, 1.2f * u), Size(r.width * 0.4f, 1f * u), CornerRadius(1f))
    }

    /** Un hueco sin tema: la balda con su puntilla, unas migas y un adorno suelto (una bola de cristal o una piña). */
    private fun DrawScope.vacio(semilla: Int, pie: Float, o: Boolean, k: Float, hacia: Offset) {
        val u = density
        val w = size.width; val h = size.height
        for (j in 0 until 8) drawCircle(luz(mezcla(JENGIBRE, PINO, 0.4f), o, k), (0.8f + 0.6f * azar(semilla, j)) * u, Offset(w * (0.15f + 0.7f * azar(semilla, j + 20)), pie - h * 0.02f * azar(semilla, j + 40)))
        // El cerco de una lata que estuvo aquí.
        drawOval(luz(mezcla(PINO_OSC, CREMA, 0.4f), o, k).copy(alpha = 0.3f), Offset(w * 0.1f, pie - h * 0.035f), Size(w * 0.55f, h * 0.06f), style = Stroke(1.4f * u))
        if (Math.floorMod(semilla / 7919, 2) == 0) {
            // La bola de cristal roja, tumbada, con su casquillo y la anilla.
            val c = Offset(w * 0.62f, pie - h * 0.12f)
            val r = h * 0.12f
            sombraEn(Path().apply { addOval(Rect(c.x - r * 0.6f, pie - h * 0.035f, c.x + r * 1.5f, pie + h * 0.01f)) }, sombra(o, if (o) 0.5f else 0.3f))
            val bola = Path().apply { addOval(Rect(c, r)) }
            drawPath(bola, luz(CARMIN, o, k))
            clipPath(bola) {
                drawRect(luz(ORO_VIEJO, o, k), Offset(c.x - r, c.y - r * 0.1f), Size(r * 2, r * 0.12f))
                copo(Offset(c.x + r * 0.1f, c.y + r * 0.45f), r * 0.28f, luz(GLASEADO, o, k).copy(alpha = 0.6f), 0.8f * u)
            }
            volumen(bola, o, k, hacia, 1f, 0.6f)
            if (!o) drawCircle(luzMueble(0.9f), r * 0.12f, c + Offset(-r * 0.4f, -r * 0.4f))
            rotate(-70f, c) {
                drawRect(luz(mezcla(ORO_VIEJO, PLATA, 0.4f), o, k), Offset(c.x - r * 0.22f, c.y - r * 1.18f), Size(r * 0.44f, r * 0.22f))
                drawCircle(luz(ORO_VIEJO, o, k), r * 0.12f, Offset(c.x, c.y - r * 1.3f), style = Stroke(1f * u))
            }
            // Un trocito de cinta roja.
            drawPath(Path().apply { moveTo(w * 0.22f, pie - h * 0.01f); cubicTo(w * 0.28f, pie - h * 0.06f, w * 0.34f, pie + h * 0.01f, w * 0.4f, pie - h * 0.03f) }, luz(CARMIN, o, k), style = Stroke(2f * u, cap = StrokeCap.Round))
        } else {
            // La piña con una ramita de abeto.
            val c = Offset(w * 0.4f, pie - h * 0.11f)
            val rx = h * 0.09f; val ryy = h * 0.13f
            rotate(-75f, c) {
                val pina = Path().apply { addOval(Rect(c.x - rx, c.y - ryy, c.x + rx, c.y + ryy)) }
                sombraEn(corrido(pina, w * 0.03f, h * 0.02f), sombra(o, if (o) 0.4f else 0.25f))
                drawPath(pina, luz(mezcla(PINO_OSC, JENGIBRE, 0.3f), o, k))
                clipPath(pina) {
                    for (fy in 0 until 6) for (fx in 0 until 3) {
                        val q = Offset(c.x - rx * 0.66f + rx * 0.66f * fx + (if (fy % 2 == 0) 0f else rx * 0.33f), c.y - ryy * 0.85f + ryy * 0.34f * fy)
                        drawArc(luz(mezcla(PINO, JENGIBRE, 0.3f), o, k), 20f, 140f, false, Offset(q.x - rx * 0.3f, q.y - ryy * 0.15f), Size(rx * 0.6f, ryy * 0.3f), style = Stroke(1.4f * u))
                    }
                }
                volumen(pina, o, k, if (o) Offset(0f, -1f) else Offset(-0.6f, 0.8f), 0.9f)
            }
            val b0 = Offset(w * 0.55f, pie - h * 0.02f); val b1 = Offset(w * 0.85f, pie - h * 0.1f)
            drawLine(luz(PINO_OSC, o, k), b0, b1, 1.2f * u)
            for (j in 0 until 8) {
                val p = b0 + (b1 - b0) * (j / 8f)
                listOf(-1f, 1f).forEach { l -> drawLine(luz(if (j % 2 == 0) FILETE else mezcla(FILETE, SALVIA, 0.35f), o, k), p, p + Offset(w * 0.02f, l * h * 0.05f), 1.3f * u, StrokeCap.Round) }
            }
        }
    }

    /** El tema va en la etiqueta alargada: ancha para que quepa casi entero «#historia de la ciencia». */
    override val rotulo = Rect(0.1f, 0.6f, 0.9f, 0.835f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.calida
    override val salida = 0f
    /** La tarjeta del tarjetero de latón, en medio del cajón. */
    override val placa = Rect(0.36f, 0.1f, 0.64f, 0.36f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (no cambian)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de la lata, de hojalata con sus círculos, y una blonda de papel calado al centro.
        val pl = if (oscuro) mezcla(PLATA, Color.Black, 0.55f) else PLATA
        drawRect(pl)
        val c = Offset(w / 2, h * 0.45f)
        var r = 20 * u
        while (r < h) { drawCircle(Color.White.copy(alpha = 0.12f), r, c, style = Stroke(1f * u)); r += 14 * u }
        val rb = w * 0.42f
        val blonda = if (oscuro) mezcla(BLANCO, Color.Black, 0.5f) else BLANCO
        for (k in 0 until 40) {
            val a = k * 2 * Math.PI / 40
            drawCircle(blonda, rb * 0.09f, Offset(c.x + rb * cos(a).toFloat(), c.y + rb * sin(a).toFloat()))
        }
        drawCircle(blonda, rb, c)
        for (anillo in listOf(0.85f, 0.65f, 0.45f)) {
            val n = (40 * anillo).toInt()
            for (k in 0 until n) {
                val a = k * 2 * Math.PI / n
                drawCircle(pl.copy(alpha = 0.6f), rb * 0.025f, Offset(c.x + rb * anillo * cos(a).toFloat(), c.y + rb * anillo * sin(a).toFloat()))
            }
        }
        val pared = 14 * u
        listOf(Rect(0f, 0f, w, pared), Rect(0f, h - pared, w, h), Rect(0f, 0f, pared, h), Rect(w - pared, 0f, w, h)).forEach { rr -> drawRect(t(ROJO, oscuro), rr.topLeft, rr.size) }
        drawRect(t(ORO, oscuro), Offset(pared, pared), Size(w - 2 * pared, h - 2 * pared), style = Stroke(1.6f * u))
        sombraInterior(Rect(pared, pared, w - pared, h - pared), 16 * u, 0.25f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) BLANCO else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 4

    override fun forma(semilla: Int): Shape = RoundedCornerShape(3.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        // El doble filete rojo y verde, unos copos tenues y una ramita de acebo en la esquina de abajo.
        val m = 4 * u * g
        drawRoundRect(ROJO.copy(alpha = 0.6f), Offset(m, m), Size(w - 2 * m, h - 2 * m), CornerRadius(2 * u), style = Stroke(1.2f * u * g))
        drawRoundRect(ABETO.copy(alpha = 0.5f), Offset(m + 2.6f * u * g, m + 2.6f * u * g), Size(w - 2 * m - 5.2f * u * g, h - 2 * m - 5.2f * u * g), CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        for (k in 0 until (if (grande) 14 else 6)) copo(Offset(w * azar(semilla, k), h * azar(semilla, k + 20)), (2.5f + 2.5f * azar(semilla, k + 40)) * u * g, ABETO.copy(alpha = 0.1f), 0.6f * u)
        acebo(Offset(m + 9 * u * g, h - m - 7 * u * g), 16 * u * g, ABETO.copy(alpha = 0.9f), ROJO)
    }

    override val margenes = MargenesFicha(14.dp, 16.dp, 16.dp, 26.dp, 28.dp, 32.dp)
    override val proporcionFicha = 0.85f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El lacre rojo de una carta.
            "hecho" -> selloHecho(ROJO, p.color)
            "convence" -> acebo(Offset(c.x, c.y + s * 0.06f), s * 0.9f, ABETO, ROJO)
            "piensa" -> copo(c, s * 0.44f, LATA_AZUL, s * 0.08f)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un moño rojo. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x - s * 0.46f, c.y - s * 0.3f); lineTo(c.x - s * 0.46f, c.y + s * 0.2f); close() }, ROJO)
        drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + s * 0.46f, c.y - s * 0.3f); lineTo(c.x + s * 0.46f, c.y + s * 0.2f); close() }, ROJO)
        drawLine(ROJO_OSC, c, Offset(c.x - s * 0.2f, c.y + s * 0.46f), s * 0.1f)
        drawLine(ROJO_OSC, c, Offset(c.x + s * 0.2f, c.y + s * 0.46f), s * 0.1f)
        drawCircle(ROJO_OSC, s * 0.12f, c)
    }

    override val desorden = 2f
}
