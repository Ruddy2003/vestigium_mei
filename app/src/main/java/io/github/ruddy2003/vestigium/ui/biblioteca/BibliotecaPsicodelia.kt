package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.Psicodelia
import io.github.ruddy2003.vestigium.ui.TrazosPsicodelia
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Psicodelia
// =====================================================================

/**
 * La calcomanía de flor de las postales de Psicodelia: troquelada en forma de flor de seis pétalos redondos, con su borde
 * blanco, la flor que vibra impresa en [color] sobre su complementario y el símbolo de la paz chico en el centro, con el brillo
 * del plástico. Sella la cinta de las postales para otro verano y sale al pie de la carta y en su PDF (`selloCarta`). [alfa]
 * la hace aparecer al caer.
 */
internal fun DrawScope.calcomaniaPsicodelia(c: Offset, r: Float, color: Color, alfa: Float = 1f, giro: Float = 0.3f) {
    val a = alfa.coerceIn(0f, 1f)
    if (a <= 0f || r <= 0f) return
    val base = color.copy(alpha = 1f)
    // El troquel: seis pétalos redondos alrededor de un centro.
    val troquel = Path()
    for (i in 0 until 6) {
        val ang = 2.0 * PI * i / 6 + giro
        troquel.addOval(Rect(Offset(c.x + r * 0.5f * cos(ang).toFloat(), c.y + r * 0.5f * sin(ang).toFloat()), r * 0.5f))
    }
    troquel.addOval(Rect(c, r * 0.62f))
    translate(r * 0.07f, r * 0.11f) { drawPath(troquel, Color.Black.copy(alpha = 0.28f * a)) }
    drawPath(troquel, Color(0xFFFFFBF2).copy(alpha = a))
    drawPath(troquel, Color.Black.copy(alpha = 0.12f * a), style = Stroke(r * 0.03f))
    // La flor impresa, que vibra sobre su complementario.
    val contra = TrazosPsicodelia.complementario(base)
    with(TrazosPsicodelia) { flor(c, r * 0.8f, base.copy(alpha = a), contra.copy(alpha = a), 6, 0.4f, giro) }
    with(TrazosPsicodelia) { paz(c, r * 0.17f, Color(0xFFFFFBF2).copy(alpha = a), r * 0.04f) }
    // El brillo del plástico, en diagonal.
    drawArc(Color.White.copy(alpha = 0.45f * a), 200f, 60f, false, Offset(c.x - r * 0.78f, c.y - r * 0.78f), Size(r * 1.56f, r * 1.56f),
        style = Stroke(r * 0.08f, cap = StrokeCap.Round))
}

/**
 * Psicodelia: la repisa de discos del departamento compartido, en una casa victoriana del Haight en 1967. Tablas de pino sin
 * pintar sobre pilas de ladrillos; los discos de larga duración de canto, un mes por tabla, con su rótulo en una calcomanía
 * de arcoíris pegada en el canto. Arriba, la pared con el cartel del baile de la semana clavado con chinchetas (propio: las
 * letras de bloque que llenan su forma, los colores que vibran), el tubo de luz negra y, sobre la tabla de arriba, el
 * tocadiscos de maleta con un disco en el plato, la lámpara de cera y un frasco con margaritas. En las puntas de las tablas,
 * la radio de válvulas, una pila de sencillos con su adaptador y un pandero con cintas. Abajo, una caja de fruta en el piso
 * de duela con más discos, y la orilla de la colcha de la India. Cada entrada es un disco en su funda: la funda impresa a
 * dos colores que vibran, la funda de cartón con el número a plumón en un trozo de cinta, o la funda negra de los carteles de
 * luz negra; el ánimo es una calcomanía redonda. De día entra el sol de la tarde por la ventana del mirador, de la izquierda;
 * de noche es el mismo cuarto bajo la luz negra: todo se hunde en el negro violeta y solo brilla lo fluorescente.
 * La tapa del diario es el disco del día como un álbum: la funda con su portada y, saliendo de ella, la funda interior con el
 * disco; al abrirse, la portada se abre como un álbum doble.
 */
object EstantePsicodelia : DisenoBiblioteca {
    // ---------------------------------------------------------------- la paleta del departamento
    private val MAGENTA = TrazosPsicodelia.MAGENTA
    private val VERDE = TrazosPsicodelia.VERDE
    private val NARANJA = TrazosPsicodelia.NARANJA
    private val AZUL = TrazosPsicodelia.AZUL
    private val AMARILLO = TrazosPsicodelia.AMARILLO
    private val VIOLETA = TrazosPsicodelia.VIOLETA
    private val LIMON = TrazosPsicodelia.LIMON
    private val FMAGENTA = TrazosPsicodelia.FLUOR_MAGENTA
    private val FVIOLETA = TrazosPsicodelia.FLUOR_VIOLETA
    private val FNARANJA = TrazosPsicodelia.FLUOR_NARANJA
    private val SALA = TrazosPsicodelia.SALA
    private val VINILO = TrazosPsicodelia.VINILO
    private val NIEBLA = TrazosPsicodelia.NIEBLA
    /** Los pares que vibran: un color y su complementario, como en los carteles de las salas. */
    private val PARES = listOf(MAGENTA to VERDE, NARANJA to AZUL, AMARILLO to VIOLETA, VERDE to MAGENTA, AZUL to NARANJA, VIOLETA to AMARILLO)
    /** El arcoíris del tintero partido; bajo la luz negra, el de las tintas que brillan. */
    private val ARCOIRIS = TrazosPsicodelia.ARCOIRIS
    private val ARCOIRIS_FLUOR = listOf(FMAGENTA, FNARANJA, LIMON, Color(0xFF6CF0C8), FVIOLETA)
    /** La pared encalada del departamento, crema, y su yeso en sombra. */
    private val PARED = Color(0xFFEDE3D2)
    private val PARED_SOMBRA = Color(0xFFC9BBA4)
    /** El pino sin pintar de las tablas: su color, en sombra y en luz, y sus nudos. */
    private val PINO = Color(0xFFCFA673)
    private val PINO_OSC = Color(0xFF8C6A42)
    private val PINO_LUZ = Color(0xFFE8CB98)
    private val NUDO = Color(0xFF7A532E)
    /** Los ladrillos de las pilas. */
    private val LADRILLO = Color(0xFFA9573C)
    private val LADRILLO_OSC = Color(0xFF6E3324)
    /** La duela del piso, el cartón de las fundas y el papel. */
    private val DUELA = Color(0xFF9A6B42)
    private val CARTON = Color(0xFFC9A877)
    private val PAPEL = TrazosPsicodelia.PAPEL
    private val PAPEL_CLARO = Color(0xFFFFF8EC)
    /** La tinta de leer y la tenue, de día y bajo la luz negra (la guía del tema). */
    private val TINTA = Color(0xFF2A1C32)
    private val TENUE = Color(0xFF61546A)
    private val TINTA_NOCHE = Color(0xFFF2EAF7)
    private val TENUE_NOCHE = Color(0xFFB4A7C4)
    /** Los colores de cartel del modo claro: magenta, verde y naranja. */
    private val MAGENTA_CARTEL = Color(0xFFB0246A)
    private val VERDE_CARTEL = Color(0xFF2E7344)
    private val NARANJA_CARTEL = Color(0xFFA84B0A)
    /** La niebla del anochecer: el mismo cuarto cuando entra la niebla (los sueños en modo claro). */
    private val ANOCHECER = Color(0xFF4B3D68)
    /** La luz del sol de la tarde. */
    private val SOL = Color(0xFFFFE2A8)

    private val TITULO: FontFamily = LetraExtra.modak
    private val TEXTO: FontFamily = LetraExtra.fredoka
    private val ROTULO: FontFamily = LetraExtra.fredokaRotulo

    /**
     * El color de lo que no brilla: en modo oscuro, el mismo cuarto bajo la luz negra, hundido en el negro violeta; con [noche]
     * (los sueños en modo claro), el cuarto cuando entra la niebla del anochecer; de día, tal cual.
     */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color = when {
        oscuro -> mezcla(c, SALA, 0.84f)
        noche -> mezcla(c, ANOCHECER, 0.36f)
        else -> c
    }

    /** Lo que se imprimió en tinta fluorescente: de día, su color de cartel; bajo la luz negra, el que brilla. */
    private fun fluor(c: Color, oscuro: Boolean): Color = if (!oscuro) c else when (c) {
        MAGENTA, MAGENTA_CARTEL -> FMAGENTA
        VERDE, VERDE_CARTEL -> LIMON
        NARANJA, NARANJA_CARTEL -> FNARANJA
        AMARILLO -> LIMON
        AZUL -> Color(0xFF6CF0C8)
        VIOLETA -> FVIOLETA
        else -> mezcla(c, FVIOLETA, 0.4f)
    }

    /** El papel donde se lee: el del periódico de día; bajo la luz negra, en penumbra violeta (la tinta se aclara encima). */
    private fun papel(oscuro: Boolean, noche: Boolean): Color = when {
        oscuro -> Color(0xFF2A2035)
        noche -> mezcla(PAPEL, ANOCHECER, 0.2f)
        else -> PAPEL
    }

    private fun tinta(oscuro: Boolean): Color = if (oscuro) TINTA_NOCHE else TINTA
    private fun tenue(oscuro: Boolean): Color = if (oscuro) TENUE_NOCHE else TENUE
    /** El acento: magenta de cartel de día; bajo la luz negra, el verde limón. */
    private fun acento(oscuro: Boolean): Color = if (oscuro) LIMON else MAGENTA_CARTEL

    /** El halo de algo que brilla (la luz negra, la lámpara de cera, la tinta fluorescente). */
    private fun DrawScope.resplandor(c: Offset, r: Float, col: Color, fuerza: Float = 1f) {
        mancha(c, r, col.copy(alpha = 0.2f * fuerza))
        mancha(c, r * 0.45f, col.copy(alpha = 0.24f * fuerza))
    }

    /** Una forma que brilla bajo la luz negra: el halo de su color alrededor y la forma encima. */
    private fun DrawScope.brilla(p: Path, col: Color, g: Float) {
        drawPath(p, col.copy(alpha = 0.1f * col.alpha), style = Stroke(g * 3.2f, join = StrokeJoin.Round))
        drawPath(p, col.copy(alpha = 0.2f * col.alpha), style = Stroke(g * 1.5f, join = StrokeJoin.Round))
        drawPath(p, col)
    }

    // =================================================================
    //  Ayudantes de dibujo
    // =================================================================

    /** El ancho de un texto, para ajustar rótulos a lo que dicen. */
    private fun anchoTx(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, espaciado: Float = 0f): Float =
        medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

    /** Un texto alineado a la izquierda dentro de [r]: [textoEn] en una caja de su ancho medido. */
    private fun DrawScope.aIzquierda(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                     espaciado: Float = 0f, minimo: Float = 6f) {
        val ancho = anchoTx(medidor, texto, letra, tam, espaciado) + 2f
        textoEn(medidor, texto, Rect(r.left, r.top, minOf(r.right, r.left + ancho), r.bottom), letra, color, tam, espaciado = espaciado, minimo = minimo)
    }

    /** Un texto que brilla bajo la luz negra ([luce]): su halo, corrido en cuatro direcciones, y el texto encima. */
    private fun DrawScope.letrero(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float, luce: Boolean = false,
                                vertical: Boolean = false, espaciado: Float = 0f, minimo: Float = 6f) {
        if (luce) {
            val u = density
            listOf(Offset(-1f, 0f), Offset(1f, 0f), Offset(0f, -1f), Offset(0f, 1f)).forEach { o ->
                textoEn(medidor, texto, r.translate(o.x * 0.9f * u, o.y * 0.9f * u), letra, color.copy(alpha = 0.26f * color.alpha), tam, vertical = vertical, espaciado = espaciado, minimo = minimo)
            }
        }
        textoEn(medidor, texto, r, letra, color, tam, vertical = vertical, espaciado = espaciado, minimo = minimo)
    }

    /** Un texto impreso dos veces, como en los carteles: [contra] corrido un poco abajo a la derecha y [color] encima. */
    private fun DrawScope.vibra(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, contra: Color, tam: Float,
                                corrido: Float = 0.06f, vertical: Boolean = false, minimo: Float = 6f) {
        val d = maxOf(0.6f * density, tam * density * corrido)
        textoEn(medidor, texto, r.translate(d, d * 0.7f), letra, contra, tam, vertical = vertical, minimo = minimo)
        textoEn(medidor, texto, r, letra, color, tam, vertical = vertical, minimo = minimo)
    }

    /**
     * Un texto en tinta de arcoíris, la del tintero partido: el color corre de un extremo al otro de las letras ([colores]).
     * Si no cabe en [r], se achica. Con [borde], un contorno de ese color por debajo.
     */
    private fun DrawScope.arcoiris(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, tam: Float, colores: List<Color>,
                                   minimo: Float = 6f, borde: Color? = null, espaciado: Float = 0f) {
        if (texto.isBlank() || r.width <= 1f || r.height <= 1f) return
        var estilo = TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, letterSpacing = espaciado.sp)
        var capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false)
        val k = minOf(1f, r.width / capa.size.width.coerceAtLeast(1), r.height / capa.size.height.coerceAtLeast(1))
        if (k < 1f) {
            val nuevo = maxOf(minimo, tam * k * 0.96f)
            estilo = estilo.copy(fontSize = nuevo.sp, lineHeight = (nuevo * 1.05f).sp)
            capa = medidor.measure(texto, estilo, maxLines = 1, softWrap = false)
        }
        val o = Offset(r.center.x - capa.size.width / 2f, r.center.y - capa.size.height / 2f)
        if (borde != null) drawText(capa, color = borde, topLeft = o, drawStyle = Stroke(maxOf(1f, 0.16f * estilo.fontSize.value * density), join = StrokeJoin.Round))
        drawText(capa, brush = Brush.horizontalGradient(colores, o.x, o.x + capa.size.width), topLeft = o)
    }

    /** El mes para un lomo: solo su nombre («Álbum de noviembre» se vuelve «noviembre»). */
    private fun mesCorto(mes: String): String = mes.substringAfter("Álbum de ").substringBefore(" ").trim()

    /** Cuál de las tres fundas es una entrada: 0, la impresa a dos colores; 1, la de cartón con cinta; 2, la negra de luz negra. */
    private fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 8)
        return if (k < 4) 0 else if (k < 6) 1 else 2
    }

    /** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para la portada que se abre). */
    private fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
    }

    /** La pestaña [k] de las cinco: exactamente una quinta parte del ancho [w] (ahí se toca). */
    private fun quinta(k: Int, w: Float, arriba: Float, abajo: Float): Rect = Rect(w * k / 5f, arriba, w * (k + 1) / 5f, abajo)

    /** Una línea ondulada de [desde] a [hasta] (horizontal), de [ondas] ondas y [alto] de amplitud. */
    private fun onda(desde: Offset, hasta: Offset, ondas: Float, alto: Float, fase: Float = 0f): Path = Path().apply {
        val pasos = 48
        for (i in 0..pasos) {
            val f = i / pasos.toFloat()
            val x = desde.x + (hasta.x - desde.x) * f
            val y = desde.y + (hasta.y - desde.y) * f + alto * sin(f * ondas * 2f * PI.toFloat() + fase)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
    }

    /** Una onda en tinta de arcoíris: cada tramo de un color, del magenta al violeta. */
    private fun DrawScope.ondaArcoiris(desde: Offset, hasta: Offset, ondas: Float, alto: Float, g: Float, oscuro: Boolean, alfa: Float = 1f) {
        val p = onda(desde, hasta, ondas, alto)
        val cols = if (oscuro) ARCOIRIS_FLUOR else ARCOIRIS
        val brocha = Brush.horizontalGradient(cols.map { it.copy(alpha = alfa) }, minOf(desde.x, hasta.x), maxOf(desde.x, hasta.x))
        if (oscuro) drawPath(p, Brush.horizontalGradient(cols.map { it.copy(alpha = 0.22f * alfa) }, minOf(desde.x, hasta.x), maxOf(desde.x, hasta.x)), style = Stroke(g * 3f, cap = StrokeCap.Round))
        drawPath(p, brocha, style = Stroke(g, cap = StrokeCap.Round))
    }

    /**
     * Pino sin pintar dentro de [r]: el color [base], la veta (a lo largo, o de arriba abajo con [vertical]) y, cada tanto, un
     * nudo oscuro con sus anillos.
     */
    private fun DrawScope.pino(r: Rect, base: Color, semilla: Int, vertical: Boolean = false, nudos: Int = 2) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, mezcla(base, PINO_OSC, 0.6f).copy(alpha = 0.5f), semilla, 0.9f, vertical, 0.7f, 0.5f)
            vetas(r, mezcla(base, Color.White, 0.3f).copy(alpha = 0.22f), semilla + 5, 0.5f, vertical, 0.6f, 0.7f)
            repeat(nudos) { k ->
                val c = Offset(r.left + r.width * (0.1f + 0.8f * azar(semilla, k + 70)), r.top + r.height * (0.25f + 0.5f * azar(semilla, k + 80)))
                val rr = minOf(r.width, r.height) * (0.16f + 0.1f * azar(semilla, k + 90))
                val s = if (vertical) Size(rr * 1.2f, rr * 2f) else Size(rr * 2.2f, rr * 1.1f)
                drawOval(mezcla(base, NUDO, 0.35f).copy(alpha = 0.5f), Offset(c.x - s.width, c.y - s.height), Size(s.width * 2, s.height * 2))
                drawOval(NUDO.copy(alpha = 0.7f), Offset(c.x - s.width / 2, c.y - s.height / 2), s)
                drawOval(mezcla(NUDO, Color.Black, 0.3f).copy(alpha = 0.6f), Offset(c.x - s.width / 2, c.y - s.height / 2), s, style = Stroke(0.6f * u))
            }
        }
    }

    /**
     * Una pila de ladrillos dentro de [r], de los que sostienen las tablas: las caras de la punta de cada ladrillo, de color
     * desparejo, con su arista de luz arriba, la sombra entre uno y otro, sus desconchones y un poco de corrimiento, porque se
     * apilaron a mano.
     */
    private fun DrawScope.ladrillos(r: Rect, semilla: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        val alto = r.width * 0.56f
        var y = r.bottom; var k = 0
        clipRect(r.left - 3 * u, r.top, r.right + 3 * u, r.bottom) {
            while (y > r.top - alto) {
                val corre = (azar(semilla, k) - 0.5f) * 2.4f * u
                val b = Rect(r.left + corre, y - alto, r.right + corre, y)
                val tono = mezcla(LADRILLO, listOf(LADRILLO_OSC, Color(0xFFC07A4E), Color(0xFF8E4630), Color(0xFFB4664A))[k % 4], 0.25f + 0.35f * azar(semilla, k + 11))
                val col = t(tono, oscuro, noche)
                drawRoundRect(col, b.topLeft, b.size, CornerRadius(1.6f * u))
                // La arcilla: granos y una veta de horno.
                for (j in 0 until 7) drawCircle((if (j % 2 == 0) Color.Black else Color.White).copy(alpha = 0.1f), (0.5f + 0.9f * azar(semilla, k * 9 + j)) * u,
                    Offset(b.left + b.width * azar(semilla, k * 9 + j + 3), b.top + b.height * azar(semilla, k * 9 + j + 6)))
                drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.28f)), b.top, b.bottom), b.topLeft, b.size)
                // Un desconchón en una esquina.
                if (azar(semilla, k + 40) > 0.55f) {
                    val esq = if (azar(semilla, k + 41) > 0.5f) b.topRight else b.topLeft
                    val s = if (esq == b.topRight) -1f else 1f
                    drawPath(Path().apply { moveTo(esq.x, esq.y); lineTo(esq.x + s * 4 * u, esq.y); lineTo(esq.x, esq.y + 3 * u); close() }, t(PARED_SOMBRA, oscuro, noche))
                }
                drawLine(Color.Black.copy(alpha = 0.4f), Offset(b.left, b.bottom - 0.4f * u), Offset(b.right, b.bottom - 0.4f * u), 1f * u)
                y -= alto; k++
            }
        }
    }

    /** Una chincheta de colores que vibran, clavada en [c]. */
    private fun DrawScope.chincheta(c: Offset, r: Float, color: Color, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.35f, r * 0.5f))
        val col = fluor(color, oscuro)
        if (oscuro) resplandor(c, r * 3f, col, 0.8f)
        drawCircle(col, r, c)
        drawCircle(TrazosPsicodelia.complementario(color).let { fluor(it, oscuro) }, r * 0.45f, c)
        drawCircle(Color.White.copy(alpha = 0.55f), r * 0.28f, c - Offset(r * 0.35f, r * 0.35f))
    }

    // =================================================================
    //  El mueble: la repisa de tablas y ladrillos
    // =================================================================

    override val corona = 0.62f
    override val base = 0.19f
    override val lado = 0.085f
    override val altoRepisa: Dp = 110.dp
    override val tabla: Dp = 14.dp
    override val separa: Dp = 1.5.dp
    override val letra: FontFamily = TITULO

    override fun ancho(palabras: Int): Dp = (19 + (palabras / 45).coerceAtMost(16)).dp
    override fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.9f + 0.05f * azar(semilla, 4)
        1 -> 0.88f + 0.04f * azar(semilla, 4)
        else -> 0.93f + 0.04f * azar(semilla, 4)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        val piso = d.base.top + d.base.height * 0.66f
        // La pared, de arriba hasta el piso, con su yeso.
        drawRect(Brush.verticalGradient(listOf(c(PARED), c(mezcla(PARED, PARED_SOMBRA, 0.35f))), 0f, piso), Offset.Zero, Size(w, piso))
        for (k in 0 until 90) drawCircle((if (k % 2 == 0) Color.White else Color.Black).copy(alpha = 0.035f), (1f + 4f * azar(301, k)) * u, Offset(w * azar(303, k), piso * azar(305, k)))
        // De día, el sol de la tarde entra por la ventana del mirador y deja en la pared su mancha, cruzada por la sombra del
        // marco; con la niebla del anochecer, apenas.
        if (!oscuro) sol(Rect(0f, 0f, w, piso), if (noche) 0.25f else 1f)
        val tubo = corona(d.corona, medidor, oscuro, noche)
        // El fondo de cada hueco: la sombra que echa la tabla de arriba y, en la punta de la derecha, algo del cuarto.
        d.repisas.forEachIndexed { k, r ->
            clipRect(r.left, r.top, r.right, r.bottom) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), Color.Transparent), r.top, r.top + 26 * u), r.topLeft, Size(r.width, 26 * u))
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.16f)), r.bottom - 12 * u, r.bottom), Offset(r.left, r.bottom - 12 * u), Size(r.width, 12 * u))
                when (k % 3) {
                    0 -> radio(Offset(r.right - 30 * u, r.bottom), oscuro, noche)
                    1 -> sencillos(Offset(r.right - 26 * u, r.bottom), oscuro, noche)
                    else -> pandero(Offset(r.right - 24 * u, r.bottom), r.height, oscuro, noche)
                }
            }
        }
        // Las pilas de ladrillos que sostienen las tablas, desde la tabla de arriba hasta el piso.
        val arriba = d.corona.bottom - tabla.toPx()
        ladrillos(Rect(d.izquierda.left + 2 * u, arriba, d.izquierda.right - 1 * u, piso), 11, oscuro, noche)
        ladrillos(Rect(d.derecha.left + 1 * u, arriba, d.derecha.right - 2 * u, piso), 13, oscuro, noche)
        // Las tablas: la de arriba (en la corona, con el tocadiscos encima) y la de cada repisa.
        val tablas = listOf(Rect(0f, arriba, w, d.corona.bottom)) + d.tablas
        tablas.forEachIndexed { k, r -> tablon(Rect(1 * u, r.top, w - 1 * u, r.bottom), 71 + k * 7, oscuro, noche) }
        // Sobre la tabla de arriba: la lámpara de cera, el frasco de margaritas y el tocadiscos de maleta.
        val lampara = Offset(w * 0.17f, arriba)
        lamparaCera(lampara, d.corona.height * 0.4f, oscuro, noche)
        margaritas(Offset(w * 0.4f, arriba), d.corona.height * 0.26f, oscuro, noche)
        tocadiscos(Rect(w * 0.53f, arriba - d.corona.height * 0.36f, w * 0.93f, arriba), oscuro, noche, 0f)
        // El rótulo de cada mes: una calcomanía de arcoíris pegada en el canto de la tabla.
        d.placas.forEach { p -> calcomaniaMes(p.r, p.texto, medidor, oscuro, noche) }
        base(d.base, piso, oscuro, noche)
        // La luz: de día, la de la ventana, de la izquierda; de noche, la luz negra baña el cuarto de violeta desde el tubo, y la
        // lámpara de cera echa su luz tibia alrededor.
        if (oscuro) {
            resplandor(tubo, w * 0.9f, FVIOLETA, 0.9f)
            resplandor(Offset(lampara.x, lampara.y - d.corona.height * 0.22f), w * 0.3f, FNARANJA, 0.7f)
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.4f to Color.Transparent, 1f to SALA.copy(alpha = 0.55f), center = tubo, radius = h * 0.95f), Offset.Zero, Size(w, h))
        } else if (noche) {
            resplandor(Offset(lampara.x, lampara.y - d.corona.height * 0.22f), w * 0.4f, FNARANJA, 0.6f)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, ANOCHECER.copy(alpha = 0.18f)), 0f, w), Offset.Zero, Size(w, h))
        } else {
            drawRect(Brush.horizontalGradient(listOf(SOL.copy(alpha = 0.14f), Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, w), Offset.Zero, Size(w, h))
        }
    }

    /**
     * La mancha de sol de la tarde en la pared, dentro de [r]: entra en diagonal desde la ventana del mirador, a la izquierda,
     * con la sombra de los parteluces y del travesaño. [fuerza] la apaga con la niebla.
     */
    private fun DrawScope.sol(r: Rect, fuerza: Float) {
        val u = density
        val x0 = r.left + r.width * 0.02f; val y0 = r.top + r.height * 0.04f
        val ancho = r.width * 0.5f; val alto = r.height * 0.42f; val corre = r.width * 0.2f
        val luzVentana = Path().apply { moveTo(x0, y0); lineTo(x0 + ancho, y0); lineTo(x0 + ancho + corre, y0 + alto); lineTo(x0 + corre, y0 + alto); close() }
        clipPath(luzVentana) {
            drawRect(Brush.linearGradient(listOf(SOL.copy(alpha = 0.32f * fuerza), SOL.copy(alpha = 0.12f * fuerza)), Offset(x0, y0), Offset(x0 + ancho, y0 + alto)), r.topLeft, r.size)
            // Los parteluces de la ventana, en sombra.
            listOf(0.33f, 0.66f).forEach { f ->
                val xa = x0 + ancho * f
                drawPath(Path().apply { moveTo(xa - 3 * u, y0); lineTo(xa + 3 * u, y0); lineTo(xa + 3 * u + corre, y0 + alto); lineTo(xa - 3 * u + corre, y0 + alto); close() }, PARED_SOMBRA.copy(alpha = 0.35f * fuerza))
            }
            drawRect(PARED_SOMBRA.copy(alpha = 0.3f * fuerza), Offset(r.left, y0 + alto * 0.48f), Size(r.width, 5 * u))
        }
    }

    /** Un tablón de pino en [r]: la cara con su veta y sus nudos, el canto de arriba con luz y la sombra que echa abajo. */
    private fun DrawScope.tablon(r: Rect, semilla: Int, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.3f), Offset(r.left + 2 * u, r.bottom), Size(r.width - 2 * u, 4 * u))
        pino(r, t(PINO, oscuro, noche), semilla)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.26f), Color.Transparent, Color.Black.copy(alpha = 0.22f)), r.top, r.bottom), r.topLeft, r.size)
        drawLine(t(PINO_LUZ, oscuro, noche).copy(alpha = 0.9f), Offset(r.left, r.top + 0.6f * u), Offset(r.right, r.top + 0.6f * u), 1f * u)
        // Bajo la luz negra, el canto de la tabla apenas toma un filo violeta.
        if (oscuro) drawLine(FVIOLETA.copy(alpha = 0.3f), Offset(r.left, r.top + 0.6f * u), Offset(r.right, r.top + 0.6f * u), 0.8f * u)
        // Las puntas del tablón, cortadas a serrucho.
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.left, r.top), Offset(r.left, r.bottom), 0.8f * u)
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.right, r.top), Offset(r.right, r.bottom), 0.8f * u)
    }

    /**
     * La corona: la pared con su moldura de colgar cuadros, el tubo de luz negra atornillado arriba a la derecha y el cartel
     * del baile de la semana clavado con cuatro chinchetas. Devuelve dónde está el tubo (de noche, de ahí sale la luz).
     */
    private fun DrawScope.corona(r: Rect, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean): Offset {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val hh = r.height
        // La moldura de colgar cuadros de la casa victoriana, con su sombra.
        val ym = r.top + hh * 0.05f
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.left, ym + 4 * u), Size(r.width, 3 * u))
        drawRect(Brush.verticalGradient(listOf(c(Color(0xFFF6EFE2)), c(Color(0xFFD9CCB4)), c(Color(0xFFBFAF95))), ym - 3 * u, ym + 4 * u), Offset(r.left, ym - 3 * u), Size(r.width, 7 * u))
        drawLine(c(Color(0xFFA8977C)), Offset(r.left, ym), Offset(r.right, ym), 0.6f * u)
        // El tubo de luz negra en su regleta, arriba a la derecha: vidrio violeta oscuro de día; de noche, encendido.
        val tubo = Rect(r.left + r.width * 0.56f, ym + hh * 0.1f, r.left + r.width * 0.93f, ym + hh * 0.1f + 6 * u)
        drawRect(Color.Black.copy(alpha = 0.22f), tubo.topLeft + Offset(1.5f * u, 3 * u), tubo.size)
        drawRoundRect(c(Color(0xFFDCD6CC)), Offset(tubo.left - 4 * u, tubo.top - 1.5f * u), Size(tubo.width + 8 * u, tubo.height + 3 * u), CornerRadius(1.5f * u))
        val cristal = Rect(tubo.left, tubo.top + 1 * u, tubo.right, tubo.bottom - 1 * u)
        if (oscuro) {
            resplandor(cristal.center, r.width * 0.32f, FVIOLETA, 1.1f)
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFE6DCFF), FVIOLETA, Color(0xFF7B3FD1)), cristal.top, cristal.bottom), cristal.topLeft, cristal.size, CornerRadius(cristal.height / 2))
        } else {
            drawRoundRect(Brush.verticalGradient(listOf(c(Color(0xFF6C5AA8)), c(Color(0xFF2E2350)), c(Color(0xFF4A3C80))), cristal.top, cristal.bottom), cristal.topLeft, cristal.size, CornerRadius(cristal.height / 2))
            drawLine(Color.White.copy(alpha = 0.4f), Offset(cristal.left + 4 * u, cristal.top + 1 * u), Offset(cristal.right - 4 * u, cristal.top + 1 * u), 0.6f * u)
        }
        listOf(tubo.left - 2 * u, tubo.right + 2 * u).forEach { x -> drawRect(c(Color(0xFF8E8A84)), Offset(x - 1.5f * u, tubo.top - 2 * u), Size(3 * u, tubo.height + 4 * u)) }
        // El cartel del baile de la semana, clavado con chinchetas y un poco torcido.
        val ca = Rect(r.left + r.width * 0.1f, ym + hh * 0.12f, r.left + r.width * 0.1f + hh * 0.46f, ym + hh * 0.12f + hh * 0.62f)
        rotate(-2.5f, ca.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.22f), ca.topLeft + Offset(3 * u, 4 * u), ca.size)
            cartel(ca, medidor, oscuro, noche)
            listOf(ca.topLeft + Offset(4 * u, 4 * u), ca.topRight + Offset(-4 * u, 4 * u), ca.bottomLeft + Offset(4 * u, -4 * u), ca.bottomRight + Offset(-4 * u, -4 * u))
                .forEachIndexed { i, q -> chincheta(q, 2.3f * u, listOf(NARANJA, AZUL, VERDE, MAGENTA)[i], oscuro) }
        }
        return cristal.center
    }

    /**
     * El cartel del baile, dentro de [r] (propio, sin copiar ninguno): el fondo magenta, una flor grande que vibra con rayos de
     * colores complementarios, la palabra «Baile» en letras infladas que llenan su forma, verdes con sombra naranja, y abajo la
     * sala y el día en una franja. Se imprimió en tinta fluorescente: bajo la luz negra, brilla.
     */
    private fun DrawScope.cartel(r: Rect, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun f(x: Color) = if (oscuro) fluor(x, true) else t(x, false, noche)
        val fondo = if (oscuro) Color(0xFF1C0F26) else t(MAGENTA, false, noche)
        drawRect(fondo, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Los rayos que salen de la flor, alternando dos colores que vibran.
            val cf = Offset(r.center.x, r.top + r.height * 0.3f)
            for (i in 0 until 16) {
                val a0 = 2 * PI * i / 16; val a1 = 2 * PI * (i + 1) / 16
                val rr = r.height * 1.2f
                val rayo = Path().apply {
                    moveTo(cf.x, cf.y)
                    lineTo(cf.x + rr * cos(a0).toFloat(), cf.y + rr * sin(a0).toFloat())
                    lineTo(cf.x + rr * cos(a1).toFloat(), cf.y + rr * sin(a1).toFloat()); close()
                }
                drawPath(rayo, (if (i % 2 == 0) f(NARANJA) else f(MAGENTA)).copy(alpha = if (oscuro) 0.55f else 0.9f))
            }
            with(TrazosPsicodelia) { flor(cf, r.width * 0.26f, f(AMARILLO), f(VIOLETA), 8, 0.5f) }
            // «Baile», en letras de bloque que llenan la franja.
            val tit = Rect(r.left + r.width * 0.04f, r.top + r.height * 0.52f, r.right - r.width * 0.04f, r.top + r.height * 0.8f)
            val contra = f(NARANJA)
            textoEn(medidor, "Baile", tit.translate(1.4f * u, 1.1f * u), TITULO, contra, 40f, minimo = 8f)
            letrero(medidor, "Baile", tit, TITULO, f(VERDE), 40f, luce = oscuro, minimo = 8f)
            // La franja de abajo, con la sala y el día.
            val fr = Rect(r.left, r.bottom - r.height * 0.18f, r.right, r.bottom)
            drawRect(if (oscuro) Color(0xFF2A1638) else t(VIOLETA, false, noche), fr.topLeft, fr.size)
            letrero(medidor, "sábado · calle Sutter", fr.deflate(2 * u), ROTULO, if (oscuro) LIMON else t(PAPEL_CLARO, false, noche), 7f, luce = oscuro, minimo = 3.5f)
            // El papel: un poco de brillo y el doblez del medio.
            drawLine(Color.Black.copy(alpha = 0.12f), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 0.6f * u)
            if (!oscuro) drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent), r.topLeft, r.center), r.topLeft, r.size)
        }
        if (oscuro) resplandor(r.center, r.width * 1.1f, FMAGENTA, 0.6f)
    }

    /**
     * La lámpara de cera, de pie en [pie] y de [alto] (otra silueta que la de cohete): un globo de vidrio sobre un pie de
     * bronce cónico, con su tapa. Dentro, el líquido ámbar y la cera magenta: de día, apagada, la cera asentada en el fondo;
     * con la noche, encendida, las gotas suben y la lámpara echa su luz tibia.
     */
    private fun DrawScope.lamparaCera(pie: Offset, alto: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        val encendida = oscuro || noche
        fun c(x: Color) = t(x, oscuro, noche)
        val an = alto * 0.5f
        val bronce = c(Color(0xFFB8893E))
        // El pie cónico de bronce.
        val pieP = Path().apply {
            moveTo(pie.x - an * 0.42f, pie.y); lineTo(pie.x + an * 0.42f, pie.y)
            lineTo(pie.x + an * 0.24f, pie.y - alto * 0.3f); lineTo(pie.x - an * 0.24f, pie.y - alto * 0.3f); close()
        }
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(pie.x - an * 0.5f, pie.y - alto * 0.03f), Size(an, alto * 0.06f))
        drawPath(pieP, Brush.horizontalGradient(listOf(mezcla(bronce, Color.White, 0.35f), bronce, mezcla(bronce, Color.Black, 0.45f)), pie.x - an * 0.42f, pie.x + an * 0.42f))
        // Los agujeritos del pie, por donde sale la luz.
        for (i in -1..1) drawCircle(if (encendida) FNARANJA.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.4f), 1.1f * u, Offset(pie.x + i * an * 0.14f, pie.y - alto * 0.12f))
        // El globo de vidrio.
        val gc = Offset(pie.x, pie.y - alto * 0.3f - an * 0.42f)
        val gr = an * 0.44f
        if (encendida) resplandor(gc, alto * 1.1f, FNARANJA, if (oscuro) 1f else 0.7f)
        val liquido = if (encendida) Brush.radialGradient(listOf(Color(0xFFFFE0A0), Color(0xFFFFA24A), Color(0xFFB8501E)), center = Offset(gc.x, gc.y + gr * 0.5f), radius = gr * 1.3f)
        else Brush.verticalGradient(listOf(c(Color(0xFFF2C886)), c(Color(0xFFD9944A))), gc.y - gr, gc.y + gr)
        drawCircle(liquido, gr, gc)
        clipPath(Path().apply { addOval(Rect(gc, gr)) }) {
            val cera = if (encendida) Color(0xFFFF5CBE) else c(Color(0xFFC23A7E))
            if (encendida) {
                // Las gotas de cera que suben, blandas.
                drawPath(TrazosPsicodelia.gota(Offset(gc.x - gr * 0.2f, gc.y - gr * 0.4f), gr * 0.28f, 0.2f, 0.18f), cera)
                drawPath(TrazosPsicodelia.gota(Offset(gc.x + gr * 0.3f, gc.y + gr * 0.05f), gr * 0.2f, 0.6f, 0.2f), cera)
                drawPath(TrazosPsicodelia.gota(Offset(gc.x, gc.y + gr * 1.05f), gr * 0.62f, 0.1f, 0.08f), cera)
            } else {
                // La cera asentada en el fondo, fría.
                drawPath(TrazosPsicodelia.gota(Offset(gc.x, gc.y + gr * 1.1f), gr * 0.66f, 0.3f, 0.05f), cera)
            }
            drawCircle(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)), gc.x - gr, gc.x + gr), gr, gc)
        }
        drawCircle(Color.Black.copy(alpha = 0.25f), gr, gc, style = Stroke(0.6f * u))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.25f else 0.55f), 200f, 70f, false, Offset(gc.x - gr * 0.75f, gc.y - gr * 0.75f), Size(gr * 1.5f, gr * 1.5f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // El collar y la tapa de bronce.
        drawRoundRect(bronce, Offset(pie.x - an * 0.2f, pie.y - alto * 0.32f), Size(an * 0.4f, alto * 0.04f), CornerRadius(1 * u))
        val tope = Offset(gc.x, gc.y - gr)
        drawPath(Path().apply { moveTo(tope.x - an * 0.12f, tope.y + 1 * u); quadraticTo(tope.x, tope.y - alto * 0.08f, tope.x + an * 0.12f, tope.y + 1 * u); close() },
            Brush.horizontalGradient(listOf(mezcla(bronce, Color.White, 0.3f), mezcla(bronce, Color.Black, 0.35f)), tope.x - an * 0.12f, tope.x + an * 0.12f))
    }

    /** Un frasco de vidrio con tres margaritas, de pie en [pie]: los pétalos redondos blancos y el botón amarillo. */
    private fun DrawScope.margaritas(pie: Offset, alto: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val fr = Rect(pie.x - alto * 0.18f, pie.y - alto * 0.42f, pie.x + alto * 0.18f, pie.y)
        // Los tallos y las flores.
        val flores = listOf(Offset(-0.18f, -0.95f), Offset(0.14f, -1.02f), Offset(0.32f, -0.78f))
        flores.forEachIndexed { i, o ->
            val cf = Offset(pie.x + o.x * alto, pie.y + o.y * alto)
            drawLine(c(Color(0xFF4E7A3A)), Offset(pie.x + (i - 1) * 2 * u, fr.top + 4 * u), cf, 1.1f * u)
            if (i == 1) drawPath(Path().apply { moveTo(cf.x - 1 * u, pie.y - alto * 0.6f); quadraticTo(cf.x - alto * 0.18f, pie.y - alto * 0.72f, cf.x - alto * 0.2f, pie.y - alto * 0.62f); quadraticTo(cf.x - alto * 0.08f, pie.y - alto * 0.58f, cf.x - 1 * u, pie.y - alto * 0.6f); close() }, c(Color(0xFF4E7A3A)))
            val rp = alto * 0.07f
            for (k in 0 until 10) {
                val a = 2 * PI * k / 10
                drawOval(c(Color(0xFFFBF8F0)), Offset(cf.x + (rp * 1.2f * cos(a)).toFloat() - rp * 0.5f, cf.y + (rp * 1.2f * sin(a)).toFloat() - rp * 0.5f), Size(rp, rp))
            }
            drawCircle(if (oscuro) LIMON else c(Color(0xFFF2C14E)), rp * 0.75f, cf)
            if (oscuro) drawCircle(LIMON.copy(alpha = 0.25f), rp * 1.6f, cf)
        }
        // El frasco, con el agua y el brillo del vidrio.
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(fr.left - 1 * u, fr.bottom - 2 * u), Size(fr.width + 2 * u, 4 * u))
        drawRoundRect(c(Color(0xFFCFE3E0)).copy(alpha = 0.55f), fr.topLeft, fr.size, CornerRadius(3 * u))
        drawRect(c(Color(0xFF9CC7C2)).copy(alpha = 0.45f), Offset(fr.left + 1 * u, fr.top + fr.height * 0.35f), Size(fr.width - 2 * u, fr.height * 0.62f))
        drawRoundRect(Color.Black.copy(alpha = 0.22f), fr.topLeft, fr.size, CornerRadius(3 * u), style = Stroke(0.6f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.6f), Offset(fr.left + 2.5f * u, fr.top + 4 * u), Offset(fr.left + 2.5f * u, fr.bottom - 4 * u), 1 * u, StrokeCap.Round)
        drawRect(c(Color(0xFFBFC6C2)), Offset(fr.left - 0.5f * u, fr.top), Size(fr.width + 1 * u, 2.5f * u))
    }

    /**
     * El tocadiscos de maleta, en [r] (lo que ocupa con la tapa abierta): la caja forrada de dos colores, con la rejilla del
     * parlante y la perilla al frente; arriba, el plato en perspectiva con un disco de etiqueta naranja que gira con [giro] y el
     * brazo de metal con su cápsula; detrás, la tapa levantada con su forro. Bajo la luz negra, la etiqueta y el piloto brillan.
     */
    private fun DrawScope.tocadiscos(r: Rect, oscuro: Boolean, noche: Boolean, giro: Float) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val forro = c(Color(0xFF2B8C88))
        val crema = c(Color(0xFFEDE4CF))
        val cajaAlto = r.height * 0.3f
        val caja = Rect(r.left, r.bottom - cajaAlto, r.right, r.bottom)
        // La tapa abierta, detrás.
        val tapa = Rect(r.left + r.width * 0.03f, r.top, r.right - r.width * 0.03f, caja.top + cajaAlto * 0.3f)
        drawRoundRect(Color.Black.copy(alpha = 0.22f), tapa.topLeft + Offset(3 * u, 2 * u), tapa.size, CornerRadius(3 * u))
        drawRoundRect(forro, tapa.topLeft, tapa.size, CornerRadius(3 * u))
        val dentro = tapa.deflate(4 * u)
        drawRoundRect(c(Color(0xFF1F6663)), dentro.topLeft, dentro.size, CornerRadius(2 * u))
        // La rejilla del parlante de la tapa.
        val rej = Rect(dentro.center.x - dentro.width * 0.28f, dentro.top + dentro.height * 0.16f, dentro.center.x + dentro.width * 0.28f, dentro.bottom - dentro.height * 0.3f)
        drawRoundRect(crema, rej.topLeft, rej.size, CornerRadius(rej.height / 2))
        var yy = rej.top + 2 * u
        while (yy < rej.bottom - 1 * u) { drawLine(c(Color(0xFF9E937A)), Offset(rej.left + 4 * u, yy), Offset(rej.right - 4 * u, yy), 0.6f * u); yy += 2 * u }
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(tapa.left, tapa.top + 1 * u), Offset(tapa.right, tapa.top + 1 * u), 0.6f * u)
        // La caja, de dos colores, con su ribete.
        drawRoundRect(Color.Black.copy(alpha = 0.3f), caja.topLeft + Offset(2 * u, 3 * u), caja.size, CornerRadius(3 * u))
        drawRoundRect(crema, caja.topLeft, caja.size, CornerRadius(3 * u))
        drawRect(forro, Offset(caja.left, caja.top + cajaAlto * 0.55f), Size(caja.width, cajaAlto * 0.45f))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), caja.topLeft, caja.size, CornerRadius(3 * u), style = Stroke(0.7f * u))
        // La rejilla del frente y la perilla.
        for (i in 0 until 7) drawCircle(c(Color(0xFF15403E)), 0.9f * u, Offset(caja.left + caja.width * 0.12f + i * 3.4f * u, caja.top + cajaAlto * 0.77f))
        val pk = Offset(caja.right - caja.width * 0.14f, caja.top + cajaAlto * 0.3f)
        drawCircle(c(Color(0xFF3A3532)), 3 * u, pk)
        drawLine(crema, pk, pk + Offset(0f, -2.4f * u), 0.7f * u)
        val piloto = Offset(caja.right - caja.width * 0.26f, caja.top + cajaAlto * 0.3f)
        if (oscuro || noche) { resplandor(piloto, 8 * u, Color(0xFFFF6A3A), 0.8f); drawCircle(Color(0xFFFF6A3A), 1.2f * u, piloto) } else drawCircle(c(Color(0xFF8A3A2A)), 1.2f * u, piloto)
        // El plato con el disco, en perspectiva.
        val pc = Offset(caja.left + caja.width * 0.42f, caja.top + cajaAlto * 0.05f)
        val rx = caja.width * 0.32f; val ry = cajaAlto * 0.36f
        drawOval(c(Color(0xFF8A8478)), Offset(pc.x - rx * 1.04f, pc.y - ry * 1.04f + 1.5f * u), Size(rx * 2.08f, ry * 2.08f))
        drawOval(VINILO.let { if (oscuro) it else c(it) }, Offset(pc.x - rx, pc.y - ry), Size(rx * 2, ry * 2))
        var k = 0.92f
        while (k > 0.45f) { drawOval(Color.White.copy(alpha = 0.07f), Offset(pc.x - rx * k, pc.y - ry * k), Size(rx * 2 * k, ry * 2 * k), style = Stroke(0.4f * u)); k -= 0.08f }
        drawArc(Color.White.copy(alpha = 0.2f), 200f + giro * 360f, 40f, true, Offset(pc.x - rx * 0.95f, pc.y - ry * 0.95f), Size(rx * 1.9f, ry * 1.9f))
        val etiqueta = fluor(NARANJA, oscuro).let { if (oscuro) it else c(it) }
        if (oscuro) drawOval(etiqueta.copy(alpha = 0.3f), Offset(pc.x - rx * 0.5f, pc.y - ry * 0.5f), Size(rx, ry))
        drawOval(etiqueta, Offset(pc.x - rx * 0.33f, pc.y - ry * 0.33f), Size(rx * 0.66f, ry * 0.66f))
        drawOval(fluor(AZUL, oscuro).let { if (oscuro) it else c(it) }, Offset(pc.x - rx * 0.16f, pc.y - ry * 0.16f), Size(rx * 0.32f, ry * 0.32f))
        drawCircle(c(Color(0xFFD8D4CC)), 0.9f * u, pc)
        // El brazo, de metal, con su contrapeso y la cápsula sobre el surco.
        val eje = Offset(caja.right - caja.width * 0.12f, caja.top - cajaAlto * 0.05f)
        drawCircle(c(Color(0xFF6E6A64)), 2.6f * u, eje)
        val punta = Offset(pc.x + rx * 0.62f, pc.y + ry * 0.2f)
        val metal = c(Color(0xFFCFCAC0))
        drawLine(metal, eje, Offset(eje.x - rx * 0.12f, eje.y + ry * 0.6f), 1.4f * u, StrokeCap.Round)
        drawLine(metal, Offset(eje.x - rx * 0.12f, eje.y + ry * 0.6f), punta, 1.4f * u, StrokeCap.Round)
        drawRect(c(Color(0xFF3A3532)), Offset(punta.x - 2.5f * u, punta.y - 1.5f * u), Size(4 * u, 3 * u))
        drawRect(c(Color(0xFF6E6A64)), Offset(eje.x + 1 * u, eje.y - 2.5f * u), Size(4 * u, 4 * u))
    }

    /** En la punta de una repisa, la radio de válvulas de mesa: el mueble de madera, la tela del parlante y el dial de la FM. */
    private fun DrawScope.radio(pie: Offset, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val r = Rect(pie.x - 24 * u, pie.y - 30 * u, pie.x + 24 * u, pie.y)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(2 * u, 2 * u), r.size, CornerRadius(6 * u, 6 * u))
        drawRoundRect(Brush.horizontalGradient(listOf(c(Color(0xFF9A6438)), c(Color(0xFF7A4A28)), c(Color(0xFF5A341C))), r.left, r.right), r.topLeft, r.size, CornerRadius(6 * u, 6 * u))
        // La tela del parlante, con sus listones.
        val tela = Rect(r.left + 4 * u, r.top + 4 * u, r.left + r.width * 0.55f, r.bottom - 4 * u)
        drawRoundRect(c(Color(0xFFD6C6A0)), tela.topLeft, tela.size, CornerRadius(3 * u))
        var x = tela.left + 3 * u
        while (x < tela.right - 1 * u) { drawLine(c(Color(0xFF7A4A28)), Offset(x, tela.top), Offset(x, tela.bottom), 1.2f * u); x += 4 * u }
        // El dial de la FM y las dos perillas.
        val dial = Rect(r.left + r.width * 0.6f, r.top + 5 * u, r.right - 4 * u, r.top + 15 * u)
        val luz = oscuro || noche
        if (luz) resplandor(dial.center, 18 * u, Color(0xFFFFB24A), 0.7f)
        drawRoundRect(if (luz) Color(0xFFFFD58A) else c(Color(0xFFEFE2C0)), dial.topLeft, dial.size, CornerRadius(2 * u))
        for (i in 0..5) drawLine(c(Color(0xFF5A4630)), Offset(dial.left + 2 * u + i * (dial.width - 4 * u) / 5, dial.bottom - 3 * u), Offset(dial.left + 2 * u + i * (dial.width - 4 * u) / 5, dial.bottom - 1.5f * u), 0.4f * u)
        drawLine(Color(0xFFC0392B), Offset(dial.left + dial.width * 0.82f, dial.top + 1 * u), Offset(dial.left + dial.width * 0.82f, dial.bottom - 1 * u), 0.7f * u)
        listOf(dial.left + 4 * u, dial.right - 4 * u).forEach { kx -> drawCircle(c(Color(0xFF3A2414)), 3 * u, Offset(kx, r.bottom - 8 * u)); drawCircle(c(Color(0xFFB08A5A)), 1 * u, Offset(kx - 0.8f * u, r.bottom - 8.8f * u)) }
        drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.03f else 0.12f), Offset(r.left + 2 * u, r.top + 1 * u), Size(r.width - 4 * u, 4 * u), CornerRadius(3 * u))
    }

    /** En la punta de una repisa, una pila de sencillos en sus fundas de papel, con el adaptador amarillo encima. */
    private fun DrawScope.sencillos(pie: Offset, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val cols = listOf(Color(0xFFEFE7D6), Color(0xFFE88A3A), Color(0xFF8BC9A6), Color(0xFFEFE7D6), Color(0xFFD06A9A))
        cols.forEachIndexed { i, col ->
            val y = pie.y - (i + 1) * 3.6f * u
            val dx = (azar(17, i) - 0.5f) * 4 * u
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(pie.x - 18 * u + dx + 1 * u, y + 1 * u), Size(36 * u, 3.6f * u))
            drawRect(c(col), Offset(pie.x - 18 * u + dx, y), Size(36 * u, 3.4f * u))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(pie.x - 18 * u + dx, y + 3.4f * u), Offset(pie.x + 18 * u + dx, y + 3.4f * u), 0.4f * u)
        }
        // El adaptador de tres patas para el agujero grande del sencillo.
        val ad = Offset(pie.x + 2 * u, pie.y - cols.size * 3.6f * u - 2 * u)
        val amarillo = fluor(AMARILLO, oscuro).let { if (oscuro) it else c(it) }
        if (oscuro) resplandor(ad, 10 * u, amarillo, 0.7f)
        drawOval(amarillo, Offset(ad.x - 6 * u, ad.y - 2 * u), Size(12 * u, 4 * u))
        drawOval(c(Color(0xFFC9A030)), Offset(ad.x - 1.5f * u, ad.y - 0.8f * u), Size(3 * u, 1.6f * u))
    }

    /** En la punta de una repisa, un pandero apoyado contra la pared, con sus sonajas de lata y dos cintas de colores. */
    private fun DrawScope.pandero(pie: Offset, alto: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val rr = minOf(18 * u, alto * 0.24f)
        val cc = Offset(pie.x, pie.y - rr * 1.05f)
        drawOval(Color.Black.copy(alpha = 0.22f), Offset(cc.x - rr * 0.6f + 3 * u, cc.y - rr + 3 * u), Size(rr * 1.2f, rr * 2f))
        // Visto un poco de lado: un óvalo de parche con el aro de madera.
        drawOval(c(Color(0xFFA06A3A)), Offset(cc.x - rr * 0.62f, cc.y - rr), Size(rr * 1.24f, rr * 2f))
        drawOval(c(Color(0xFFF1E3C4)), Offset(cc.x - rr * 0.5f, cc.y - rr * 0.9f), Size(rr * 1f, rr * 1.8f))
        drawOval(Color.Black.copy(alpha = 0.12f), Offset(cc.x - rr * 0.3f, cc.y - rr * 0.5f), Size(rr * 0.6f, rr * 1f))
        // Las sonajas, en el aro.
        for (k in 0 until 5) {
            val a = -PI / 2 + PI * (k + 0.5) / 5 * 2
            val q = Offset(cc.x + (rr * 0.6f * cos(a)).toFloat(), cc.y + (rr * 0.98f * sin(a)).toFloat())
            drawOval(c(Color(0xFFD8D2C6)), Offset(q.x - 1.4f * u, q.y - 2.6f * u), Size(2.8f * u, 5.2f * u))
            drawOval(Color.Black.copy(alpha = 0.3f), Offset(q.x - 1.4f * u, q.y - 2.6f * u), Size(2.8f * u, 5.2f * u), style = Stroke(0.4f * u))
        }
        // Las cintas atadas al aro, que cuelgan.
        listOf(MAGENTA to -1f, AMARILLO to 1f).forEach { (col, s) ->
            val cinta = fluor(col, oscuro).let { if (oscuro) it else c(it) }
            val desde = Offset(cc.x + s * rr * 0.4f, cc.y + rr * 0.85f)
            drawPath(Path().apply { moveTo(desde.x, desde.y); cubicTo(desde.x + s * 4 * u, desde.y + 6 * u, desde.x - s * 3 * u, desde.y + 10 * u, desde.x + s * 2 * u, pie.y - 0.5f * u) }, cinta, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
    }

    /**
     * El rótulo de un mes, en [r]: una calcomanía alargada pegada en el canto de la tabla, con el borde en tinta de arcoíris y,
     * dentro, el nombre del álbum del mes. Bajo la luz negra, el arcoíris brilla.
     */
    private fun DrawScope.calcomaniaMes(r: Rect, texto: String, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val tam = 9.5f
        val ancho = (anchoTx(medidor, texto, ROTULO, tam) + 22 * u).coerceIn(r.width * 0.4f, r.width)
        val pl = Rect(r.center.x - ancho / 2, r.top - 1 * u, r.center.x + ancho / 2, r.bottom + 1 * u)
        val rad = CornerRadius(pl.height / 2)
        rotate(-1.2f, pl.center) {
            drawRoundRect(Color.Black.copy(alpha = 0.3f), pl.topLeft + Offset(0.8f * u, 1.2f * u), pl.size, rad)
            val cols = if (oscuro) ARCOIRIS_FLUOR else ARCOIRIS.map { t(it, false, noche) }
            if (oscuro) drawRoundRect(Brush.horizontalGradient(cols.map { it.copy(alpha = 0.25f) }, pl.left, pl.right), pl.inflate(2 * u).topLeft, pl.inflate(2 * u).size, CornerRadius(rad.x + 2 * u))
            drawRoundRect(Brush.horizontalGradient(cols, pl.left, pl.right), pl.topLeft, pl.size, rad)
            val dentro = pl.deflate(1.8f * u)
            drawRoundRect(if (oscuro) Color(0xFF2B2140) else t(PAPEL_CLARO, false, noche), dentro.topLeft, dentro.size, CornerRadius(dentro.height / 2))
            drawRoundRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), Offset(dentro.left + 4 * u, dentro.top + 0.8f * u), Size(dentro.width - 8 * u, dentro.height * 0.3f), CornerRadius(dentro.height / 4))
            letrero(medidor, texto, Rect(dentro.left + 6 * u, dentro.top, dentro.right - 6 * u, dentro.bottom), ROTULO, if (oscuro) TINTA_NOCHE else TINTA, tam, minimo = 6f)
        }
    }

    /**
     * La base, en [b]: las pilas de ladrillos bajan hasta el piso de duela ([piso]); entre ellas, una caja de fruta de tablillas
     * con más discos, que asoman por arriba, y su etiqueta de naranjas pegada en la punta; adelante, la orilla de la colcha de
     * la India con sus flores estampadas y sus flecos.
     */
    private fun DrawScope.base(b: Rect, piso: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // El piso de duela, en perspectiva.
        val suelo = Rect(b.left, piso, b.right, b.bottom)
        drawRect(c(DUELA), suelo.topLeft, suelo.size)
        clipRect(suelo.left, suelo.top, suelo.right, suelo.bottom) {
            var k = 0
            var x = -suelo.width * 0.4f
            while (x < suelo.width * 1.4f) {
                val desde = Offset(suelo.center.x + (x - suelo.center.x) * 0.6f, suelo.top)
                drawLine(Color.Black.copy(alpha = 0.25f), desde, Offset(x, suelo.bottom), 0.7f * u)
                k++; x += 26 * u
            }
            vetas(suelo, mezcla(c(DUELA), Color.Black, 0.4f).copy(alpha = 0.35f), 401, 0.8f, false, 0.6f, 0.4f)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), suelo.top, suelo.top + 8 * u), suelo.topLeft, Size(suelo.width, 8 * u))
        }
        // El zócalo de la pared, sobre el piso.
        drawRect(c(Color(0xFFE6DCC8)), Offset(b.left, piso - 7 * u), Size(b.width, 7 * u))
        drawLine(c(Color(0xFFB8A88C)), Offset(b.left, piso - 7 * u), Offset(b.right, piso - 7 * u), 0.8f * u)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(b.left, piso - 1.5f * u), Size(b.width, 1.5f * u))
        // La caja de fruta con discos.
        val caja = Rect(b.left + b.width * 0.3f, b.top + b.height * 0.2f, b.right - b.width * 0.3f, piso + 3 * u)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(caja.left - 4 * u, caja.bottom - 3 * u), Size(caja.width + 8 * u, 6 * u))
        // Los discos que asoman por arriba de la caja.
        var x = caja.left + 3 * u; var i = 0
        while (x < caja.right - 5 * u) {
            val (a, bb) = PARES[Math.floorMod(i * 5 + 1, PARES.size)]
            val col = if (i % 3 == 1) c(CARTON) else c(a)
            val alto = caja.height * (0.32f + 0.06f * azar(i, 3))
            drawRect(col, Offset(x, caja.top - alto), Size(3.2f * u, alto + 4 * u))
            drawRect(c(bb).copy(alpha = 0.7f), Offset(x, caja.top - alto), Size(3.2f * u, 2 * u))
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(x + 3.2f * u, caja.top - alto), Offset(x + 3.2f * u, caja.top), 0.5f * u)
            x += 3.6f * u; i++
        }
        // Las tablillas de la caja, con su luz y el hueco entre una y otra.
        val tab = caja.height / 3f
        for (k in 0 until 3) {
            val tr = Rect(caja.left, caja.top + k * tab + 0.8f * u, caja.right, caja.top + (k + 1) * tab - 0.8f * u)
            pino(tr, c(mezcla(PINO, Color.White, 0.1f)), 411 + k, nudos = 1)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.03f else 0.2f), Color.Black.copy(alpha = 0.18f)), tr.top, tr.bottom), tr.topLeft, tr.size)
        }
        listOf(caja.left, caja.right - 5 * u).forEach { xx -> pino(Rect(xx, caja.top, xx + 5 * u, caja.bottom), c(PINO_OSC), 421, vertical = true, nudos = 0) }
        // La etiqueta de la caja: una naranja con su hoja sobre un sol.
        val et = Rect(caja.center.x - caja.width * 0.17f, caja.top + tab * 0.9f, caja.center.x + caja.width * 0.17f, caja.top + tab * 2.1f)
        drawRect(c(Color(0xFF2F4E8F)), et.topLeft, et.size)
        drawCircle(c(Color(0xFFF2C14E)), et.height * 0.42f, Offset(et.center.x, et.bottom))
        drawCircle(c(Color(0xFFF08A24)), et.height * 0.3f, Offset(et.center.x, et.center.y + et.height * 0.08f))
        drawOval(c(Color(0xFF3E8A4A)), Offset(et.center.x + et.height * 0.08f, et.top + et.height * 0.08f), Size(et.height * 0.32f, et.height * 0.16f))
        drawRect(Color.Black.copy(alpha = 0.2f), et.topLeft, et.size, style = Stroke(0.5f * u))
        // Los clavos de la caja.
        listOf(caja.left + 2.5f * u, caja.right - 2.5f * u).forEach { xx -> for (k in 0 until 3) drawCircle(Color.Black.copy(alpha = 0.4f), 0.7f * u, Offset(xx, caja.top + (k + 0.5f) * tab)) }
        // La orilla de la colcha de la India, adelante.
        val colcha = Rect(b.left + b.width * 0.04f, b.bottom - b.height * 0.16f, b.right - b.width * 0.04f, b.bottom + 2 * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(colcha.left, colcha.top - 2 * u), Size(colcha.width, 2 * u))
        drawRect(c(Color(0xFFB0312E)), colcha.topLeft, colcha.size)
        drawRect(c(Color(0xFFE8B04A)), Offset(colcha.left, colcha.top + 2 * u), Size(colcha.width, 1.2f * u))
        var fx = colcha.left + 6 * u
        while (fx < colcha.right - 4 * u) {
            val fc = Offset(fx, colcha.center.y + 1.5f * u)
            for (j in 0 until 5) { val a = 2 * PI * j / 5; drawCircle(c(Color(0xFFF2DCA0)), 1.3f * u, Offset(fc.x + (2 * u * cos(a)).toFloat(), fc.y + (2 * u * sin(a)).toFloat())) }
            drawCircle(c(Color(0xFF2B6E5A)), 1 * u, fc)
            fx += 12 * u
        }
    }

    // =================================================================
    //  Cada entrada: un disco en su funda
    // =================================================================

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        when (clase(v.semilla)) {
            0 -> funda(v, medidor, oscuro)
            1 -> carton(v, medidor, oscuro)
            else -> negra(v, medidor, oscuro)
        }
    }

    /** La sombra de un disco en la repisa y la luz de la tarde sobre su lomo: más clara a la izquierda, en sombra a la derecha. */
    private fun DrawScope.luzLomo(r: Rect, oscuro: Boolean) {
        drawRect(Brush.horizontalGradient(0f to Color.White.copy(alpha = if (oscuro) 0.04f else 0.2f), 0.25f to Color.Transparent, 0.75f to Color.Transparent,
            1f to Color.Black.copy(alpha = 0.32f), startX = r.left, endX = r.right), r.topLeft, r.size)
    }

    /** La calcomanía redonda del ánimo, en [c]: el color del ánimo con su borde blanco y el brillo; sin ánimo, no hay. */
    private fun DrawScope.puntoAnimo(c: Offset, r: Float, tono: Color?, oscuro: Boolean) {
        if (tono == null) return
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(0.4f * density, 0.7f * density))
        drawCircle(if (oscuro) Color(0xFFD9D2E8) else Color(0xFFFFFBF2), r, c)
        drawCircle(tono, r * 0.76f, c)
        drawCircle(Color.White.copy(alpha = 0.45f), r * 0.24f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** El desgaste de una funda: las esquinas rozadas y, a veces, la costura de arriba abierta. */
    private fun DrawScope.desgaste(w: Float, h: Float, semilla: Int) {
        val u = density
        for (n in 0 until 6) drawCircle(Color.White.copy(alpha = 0.1f), (0.5f + azar(semilla, n)) * u, Offset(if (n % 2 == 0) 0.8f * u else w - 0.8f * u, h * azar(semilla, n + 30)))
        if (azar(semilla, 61) > 0.6f) {
            val x = w * (0.3f + 0.4f * azar(semilla, 62))
            drawPath(Path().apply { moveTo(x - 2 * u, 0f); lineTo(x, 3.5f * u); lineTo(x + 2.5f * u, 0f); close() }, Color.Black.copy(alpha = 0.3f))
        }
    }

    /**
     * La funda impresa a dos colores que vibran: el lomo de un color, con la franja de su complementario donde va el número
     * del día en letras infladas (crema, con la sombra del complementario), el mes de arriba abajo y, al pie, la flor o el
     * símbolo de la paz (de noche, la luna). Bajo la luz negra, las tintas se apagan y brillan el número y el adorno.
     */
    private fun DrawScope.funda(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val (a0, b0) = PARES[Math.floorMod(v.semilla, PARES.size)]
        val noche = v.noche
        val fondo = if (noche) t(mezcla(a0, Color(0xFF1E1440), 0.55f), oscuro, false) else t(a0, oscuro, false)
        val franja = if (noche) t(mezcla(b0, Color(0xFF1E1440), 0.35f), oscuro, false) else t(b0, oscuro, false)
        val r = Rect(0f, 0f, w, h)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(1 * u, 1 * u), Size(w, h))
        drawRect(fondo, r.topLeft, r.size)
        // La franja del número, con dos filetes.
        val fr = Rect(0f, h * 0.06f, w, h * 0.06f + minOf(w * 1.2f, h * 0.26f))
        drawRect(franja, fr.topLeft, fr.size)
        drawLine(t(PAPEL_CLARO, oscuro, noche).copy(alpha = 0.7f), Offset(0f, fr.top - 1.5f * u), Offset(w, fr.top - 1.5f * u), 0.6f * u)
        drawLine(t(PAPEL_CLARO, oscuro, noche).copy(alpha = 0.7f), Offset(0f, fr.bottom + 1.5f * u), Offset(w, fr.bottom + 1.5f * u), 0.6f * u)
        val numero = if (oscuro) LIMON else PAPEL_CLARO
        val sombraN = if (oscuro) FMAGENTA.copy(alpha = 0.7f) else mezcla(a0, Color.Black, 0.25f)
        textoEn(medidor, v.dia, fr.deflate(1.5f * u).translate(1.1f * u, 0.9f * u), TITULO, sombraN, 17f, minimo = 6f)
        letrero(medidor, v.dia, fr.deflate(1.5f * u), TITULO, numero, 17f, luce = oscuro, minimo = 6f)
        // El mes, de arriba abajo.
        val mes = Rect(1 * u, fr.bottom + 4 * u, w - 1 * u, h * 0.8f)
        if (mes.height > 18 * u) letrero(medidor, mesCorto(v.mes), mes, ROTULO, if (oscuro) fluor(b0, true).copy(alpha = 0.85f) else t(PAPEL_CLARO, false, noche), 8.5f,
            luce = oscuro, vertical = true, espaciado = 0.6f, minimo = 4f)
        // Al pie, la flor, la paz o, de noche, la luna.
        val pc = Offset(w / 2, h * 0.89f)
        val rr = minOf(w * 0.3f, 5.5f * u)
        when {
            noche -> lunita(pc, rr, if (oscuro) LIMON else t(Color(0xFFF6E7B0), false, false), fondo)
            v.semilla % 2 == 0 -> with(TrazosPsicodelia) { flor(pc, rr * 1.2f, fluor(b0, oscuro).let { if (oscuro) it else t(it, false, false) }, fluor(a0, oscuro).let { if (oscuro) it else mezcla(a0, Color.White, 0.3f) }, 6, 0.3f) }
            else -> with(TrazosPsicodelia) { paz(pc, rr, if (oscuro) LIMON else t(PAPEL_CLARO, false, false), rr * 0.2f) }
        }
        luzLomo(r, oscuro)
        desgaste(w, h, v.semilla)
        puntoAnimo(Offset(w * 0.5f, h * 0.06f + minOf(w * 1.2f, h * 0.26f) + 10 * u), minOf(w * 0.24f, 4 * u), v.tono, oscuro)
        drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(0.5f * u))
    }

    /** Una luna chica (la de los discos de los sueños): el creciente sobre [fondo]. */
    private fun DrawScope.lunita(c: Offset, r: Float, color: Color, fondo: Color) {
        drawCircle(color, r, c)
        drawCircle(fondo, r * 0.86f, Offset(c.x + r * 0.42f, c.y - r * 0.2f))
    }

    /**
     * La funda de cartón sin imprimir: el cartón pardo, un trozo de cinta de pintor donde se escribió el número del día con
     * plumón, el mes escrito a mano de arriba abajo y una florecita dibujada al pie. Bajo la luz negra, la cinta (papel blanco)
     * brilla un poco violeta y el número se lee encima.
     */
    private fun DrawScope.carton(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val noche = v.noche
        val carton = t(if (v.semilla % 3 == 0) Color(0xFFE9E2D2) else CARTON, oscuro, noche)
        val r = Rect(0f, 0f, w, h)
        drawRect(Color.Black.copy(alpha = 0.28f), Offset(1 * u, 1 * u), Size(w, h))
        drawRect(carton, r.topLeft, r.size)
        // La fibra del cartón.
        clipRect(0f, 0f, w, h) { for (n in 0 until 14) drawLine(Color.Black.copy(alpha = 0.05f), Offset(0f, h * azar(v.semilla, n + 3)), Offset(w, h * azar(v.semilla, n + 3) + 2 * u), 0.5f * u) }
        // La cinta de pintor, un poco torcida, con el número a plumón.
        val cinta = Rect(-1 * u, h * 0.07f, w + 1 * u, h * 0.07f + minOf(w * 1.1f, h * 0.24f))
        rotate(-3f + 6f * azar(v.semilla, 9), cinta.center) {
            val papelCinta = if (oscuro) Color(0xFF5E5486) else t(Color(0xFFF2E6C2), false, noche)
            if (oscuro) drawRect(FVIOLETA.copy(alpha = 0.18f), cinta.inflate(1.5f * u).topLeft, cinta.inflate(1.5f * u).size)
            drawRect(papelCinta, cinta.topLeft, cinta.size)
            drawLine(Color.Black.copy(alpha = 0.12f), Offset(cinta.left, cinta.top), Offset(cinta.right, cinta.top), 0.5f * u)
            letrero(medidor, v.dia, cinta.deflate(1 * u), TEXTO, if (oscuro) TINTA_NOCHE else t(TINTA, false, noche), 16f, minimo = 6f)
        }
        // El mes, a plumón.
        val mes = Rect(1 * u, cinta.bottom + 5 * u, w - 1 * u, h * 0.82f)
        if (mes.height > 18 * u) letrero(medidor, mesCorto(v.mes), mes, TEXTO, if (oscuro) TENUE_NOCHE else t(TINTA, false, noche).copy(alpha = 0.8f), 8.5f, vertical = true, minimo = 4f)
        // La florecita (o la luna) dibujada a plumón al pie.
        val pc = Offset(w / 2, h * 0.9f)
        val tinta = if (oscuro) TENUE_NOCHE else t(TINTA, false, noche).copy(alpha = 0.75f)
        val rr = minOf(w * 0.22f, 4 * u)
        if (noche) lunita(pc, rr, tinta, carton) else {
            for (k in 0 until 5) { val a = 2 * PI * k / 5 - PI / 2; drawCircle(tinta, rr * 0.5f, Offset(pc.x + (rr * 0.8f * cos(a)).toFloat(), pc.y + (rr * 0.8f * sin(a)).toFloat()), style = Stroke(0.6f * u)) }
            drawCircle(tinta, rr * 0.25f, pc)
        }
        luzLomo(r, oscuro)
        desgaste(w, h, v.semilla)
        puntoAnimo(Offset(w * 0.5f, cinta.bottom + 10 * u), minOf(w * 0.24f, 4 * u), v.tono, oscuro)
        drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft, r.size, style = Stroke(0.5f * u))
    }

    /**
     * La funda negra de los carteles de luz negra: impresa con tinta fluorescente sobre negro, con una onda de arcoíris que
     * baja por el lomo, el número en verde limón y la flor; arriba asoma el borde de la funda interior de papel. De día ya se ve
     * viva; bajo la luz negra, brilla.
     */
    private fun DrawScope.negra(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val noche = v.noche
        // La funda interior de papel, que asoma arriba.
        drawRoundRect(t(Color(0xFFF4EEE0), oscuro, noche), Offset(w * 0.12f, 0f), Size(w * 0.76f, 6 * u), CornerRadius(2 * u))
        val r = Rect(0f, 3 * u, w, h)
        drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1 * u, 1 * u), r.size)
        drawRect(if (noche) Color(0xFF140C24) else Color(0xFF17121C), r.topLeft, r.size)
        val cols = if (oscuro) ARCOIRIS_FLUOR else listOf(FMAGENTA, FNARANJA, LIMON, Color(0xFF6CF0C8), FVIOLETA)
        // La onda de arcoíris que baja por el lomo.
        clipRect(r.left, r.top, r.right, r.bottom) {
            val p = Path()
            val pasos = 40
            for (i in 0..pasos) {
                val f = i / pasos.toFloat()
                val y = r.top + r.height * (0.36f + 0.5f * f)
                val x = w / 2 + w * 0.28f * sin(f * 5f * PI.toFloat() + v.semilla % 7)
                if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            val brocha = Brush.verticalGradient(cols, r.top + r.height * 0.36f, r.bottom - r.height * 0.14f)
            drawPath(p, brocha, style = Stroke(maxOf(1.6f * u, w * 0.12f), cap = StrokeCap.Round))
            if (oscuro) drawPath(p, Brush.verticalGradient(cols.map { it.copy(alpha = 0.25f) }, r.top + r.height * 0.36f, r.bottom), style = Stroke(maxOf(4f * u, w * 0.3f), cap = StrokeCap.Round))
        }
        // El número, en verde limón con sombra magenta.
        val fr = Rect(0f, r.top + h * 0.05f, w, r.top + h * 0.05f + minOf(w * 1.2f, h * 0.24f))
        textoEn(medidor, v.dia, fr.deflate(1.5f * u).translate(1.1f * u, 0.9f * u), TITULO, FMAGENTA.copy(alpha = 0.85f), 17f, minimo = 6f)
        letrero(medidor, v.dia, fr.deflate(1.5f * u), TITULO, LIMON, 17f, luce = oscuro, minimo = 6f)
        // El mes, chico, en violeta claro, bajo el número.
        val mes = Rect(1 * u, fr.bottom + 1 * u, w - 1 * u, fr.bottom + 10 * u)
        letrero(medidor, mesCorto(v.mes).take(3) + ".", mes, ROTULO, FVIOLETA, 7f, luce = oscuro, minimo = 3.5f)
        // Al pie, la flor (o la luna) fluorescente.
        val pc = Offset(w / 2, h * 0.92f)
        val rr = minOf(w * 0.3f, 5.5f * u)
        if (noche) lunita(pc, rr, LIMON, Color(0xFF140C24))
        else with(TrazosPsicodelia) { flor(pc, rr * 1.2f, FMAGENTA, LIMON, 6, 0.4f) }
        luzLomo(r, oscuro)
        desgaste(w, h, v.semilla)
        puntoAnimo(Offset(w * 0.5f, fr.bottom + 16 * u), minOf(w * 0.24f, 4 * u), v.tono, oscuro)
        drawRect(Color.Black.copy(alpha = 0.4f), r.topLeft, r.size, style = Stroke(0.5f * u))
    }

    // =================================================================
    //  La tapa: el disco del día como un álbum
    // =================================================================

    override val altoTapa: Dp = 200.dp
    override val cajaTapa = Rect(0.575f, 0.33f, 0.93f, 0.78f)
    override val letraTapa: FontFamily = TEXTO
    override val tamTapa = 11.5f
    override val lineasTapa = 5
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = tinta(oscuro)
    override fun palabrasTapa(n: Int): String = Psicodelia.numero(n) + if (n == 1) " palabra en la funda" else " palabras en la funda"

    /** Un color del arcoíris del tintero partido, a [f] (de 0 a 1) de un extremo al otro. */
    private fun colorArcoiris(f: Float, oscuro: Boolean): Color {
        val l = if (oscuro) ARCOIRIS_FLUOR else ARCOIRIS
        val x = f.coerceIn(0f, 1f) * (l.size - 1)
        val i = x.toInt().coerceAtMost(l.size - 2)
        return mezcla(l[i], l[i + 1], x - i)
    }

    /**
     * La trama de puntos de la tinta de arcoíris, como la que deja el tintero partido en el borde de una hoja: filas de
     * puntos que se achican hacia abajo, en [r], con el color corriendo de izquierda a derecha.
     */
    private fun DrawScope.tramaArcoiris(r: Rect, oscuro: Boolean, alfa: Float = 1f) {
        val u = density
        val paso = 2.8f * u
        var fila = 0
        var y = r.top + paso / 2
        while (y < r.bottom) {
            val k = 1f - (y - r.top) / r.height
            var x = r.left + (if (fila % 2 == 0) 0f else paso / 2)
            while (x < r.right) {
                drawCircle(colorArcoiris((x - r.left) / r.width, oscuro).copy(alpha = alfa * (0.35f + 0.5f * k)), paso * 0.42f * k + 0.2f * u, Offset(x, y))
                x += paso
            }
            y += paso; fila++
        }
    }

    /**
     * El disco del día como un álbum: a la izquierda, la funda con su portada (anillos ondulados de dos colores que vibran, el
     * sol crema con el número del disco en letras infladas de arcoíris, el día en la franja de abajo y una calcomanía de flor);
     * de ella sale, a la derecha, la funda interior de papel de periódico, con la trama de arcoíris arriba, donde se lee lo
     * escrito, y asoma por arriba el disco negro. De noche, la portada es la del disco de los sueños (la luna y las estrellas
     * fluorescentes). Al abrirse, la portada se abre como un álbum doble (y deja ver la lista de las pistas) y el disco sube.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val partes = e.fecha.split(" · ")
        val ladoPista = if (partes.size > 2) partes.drop(2).joinToString(" · ") else ""
        val lado = minOf(h * 0.88f, w * 0.52f)
        val cara = Rect(w * 0.03f, (h - lado) / 2f, w * 0.03f + lado, (h + lado) / 2f)
        val interior = Rect(cara.right - 26 * u, h * 0.22f, w * 0.97f, h * 0.95f)
        // La sombra sobre la mesa.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Offset(interior.left + 4 * u, interior.top + 5 * u), interior.size)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(cara.left + 4 * u, cara.top + 6 * u), cara.size)
        // El disco, que asoma por arriba de la funda interior (y sube al abrirse).
        val dc = Offset(cara.right + (interior.right - cara.right) * 0.48f, h * 0.5f - a * h * 0.16f)
        val dr = h * 0.38f
        discoNegro(dc, dr, oscuro, e.noche, a * 0.6f + 0.1f)
        // La funda interior de papel, con la trama de arcoíris y la cuenta de palabras.
        drawRect(papel(oscuro, e.noche), interior.topLeft, interior.size)
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent), interior.left, interior.left + 40 * u), interior.topLeft, Size(40 * u, interior.height))
        tramaArcoiris(Rect(interior.left + 26 * u, interior.top + 3 * u, interior.right - 4 * u, interior.top + 18 * u), oscuro || e.noche, if (oscuro) 0.9f else 0.8f)
        drawLine(Color.Black.copy(alpha = 0.12f), Offset(interior.left, interior.top + interior.height * 0.5f), Offset(interior.right, interior.top + interior.height * 0.5f), 0.6f * u)
        drawRect(Color.Black.copy(alpha = 0.18f), interior.topLeft, interior.size, style = Stroke(0.6f * u))
        val pie = listOf(if (e.vacio) "sin estrenar" else palabrasTapa(e.palabras), ladoPista).filter { it.isNotBlank() }.joinToString(" · ")
        aIzquierda(medidor, pie, Rect(w * cajaTapa.left, h * 0.83f, interior.right - 6 * u, h * 0.91f), ROTULO, acento(oscuro), 8.5f, espaciado = 0.3f, minimo = 5f)
        // Debajo de la portada, el interior del álbum doble.
        if (a > 0.01f) pliegue(cara, e, medidor, oscuro)
        val ang = a * 1.5f
        val co = cos(ang); val si = sin(ang)
        if (a > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * si), Offset(cara.left + cara.width * co, cara.top), Size(16 * u * si, cara.height))
        afin(co, -si * 0.1f, 0f, 1f, cara.left - cara.left * co, cara.left * si * 0.1f) {
            portada(cara, e, medidor, oscuro)
            if (a > 0f) drawRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size)
        }
    }

    /** El disco de vinilo negro en [c], con sus surcos, el brillo (que gira con [giro]) y la etiqueta del día. */
    private fun DrawScope.discoNegro(c: Offset, r: Float, oscuro: Boolean, noche: Boolean, giro: Float) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(2 * u, 3 * u))
        drawCircle(VINILO, r, c)
        var k = 0.96f
        while (k > 0.4f) { drawCircle(Color.White.copy(alpha = 0.06f), r * k, c, style = Stroke(0.5f * u)); k -= 0.035f }
        val g = giro * 360f
        listOf(g - 30f, g + 150f).forEach { s -> drawArc(Color.White.copy(alpha = if (oscuro) 0.08f else 0.16f), s, 36f, true, Offset(c.x - r * 0.96f, c.y - r * 0.96f), Size(r * 1.92f, r * 1.92f)) }
        val etiqueta = if (noche) Color(0xFF3A2A6A) else NARANJA
        if (oscuro) drawCircle(fluor(if (noche) VIOLETA else NARANJA, true).copy(alpha = 0.25f), r * 0.42f, c)
        drawCircle(if (oscuro) fluor(if (noche) VIOLETA else NARANJA, true) else etiqueta, r * 0.34f, c)
        with(TrazosPsicodelia) { flor(c, r * 0.2f, if (oscuro) LIMON else AZUL, if (oscuro) FMAGENTA else NARANJA, 6, 0.2f, giro * 6f) }
        drawCircle(PAPEL, r * 0.03f, c)
    }

    /**
     * La portada del álbum, en [r]: anillos ondulados de dos colores que vibran alrededor del sol crema, el número del disco en
     * letras infladas de arcoíris con su contorno, «Disco» arriba, el día en la franja de abajo y una calcomanía de flor en la
     * esquina. De noche, la portada del disco de los sueños: anillos violeta, la luna llena y estrellas. Bajo la luz negra, solo
     * brillan las tintas fluorescentes (los filos de los anillos, el número, la calcomanía).
     */
    private fun DrawScope.portada(r: Rect, e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val partes = e.fecha.split(" · ")
        val disco = partes.getOrElse(0) { e.fecha }
        val numero = disco.substringAfter(" ", disco).trim()
        val dia = partes.getOrElse(1) { "" }
        val (a0, b0) = PARES[Math.floorMod(e.semilla, PARES.size)]
        val ca = if (e.noche) Color(0xFF2A1A52) else a0
        val cb = if (e.noche) Color(0xFF4B2E8A) else b0
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(2.5f * u))) }
        val centro = Offset(r.center.x, r.top + r.height * 0.43f)
        clipPath(forma) {
            drawRect(t(ca, oscuro, false), r.topLeft, r.size)
            // Los anillos ondulados, de afuera adentro.
            val n = 8
            for (k in 0 until n) {
                val rr = r.width * (1.05f - k * 0.1f)
                val p = TrazosPsicodelia.gota(centro, rr, 0.12f * k, 0.035f, 7)
                drawPath(p, t(if (k % 2 == 0) cb else ca, oscuro, false))
                if (oscuro) drawPath(p, fluor(if (k % 2 == 0) b0 else a0, true).copy(alpha = 0.7f), style = Stroke(0.9f * u))
            }
            // El sol crema (de noche, la luna) donde va el número.
            val sr = r.width * 0.3f
            if (e.noche) {
                drawCircle(if (oscuro) Color(0xFF3B3352) else Color(0xFFF6E7B0), sr, centro)
                listOf(Offset(-0.3f, -0.2f), Offset(0.25f, 0.3f), Offset(0.1f, -0.42f)).forEach { o ->
                    drawCircle(Color.Black.copy(alpha = 0.06f), sr * 0.14f, Offset(centro.x + o.x * sr, centro.y + o.y * sr))
                }
                for (k in 0 until 14) {
                    val q = Offset(r.left + r.width * azar(e.semilla, k), r.top + r.height * azar(e.semilla, k + 20) * 0.8f)
                    if ((q - centro).getDistance() > sr * 1.2f) drawCircle(if (oscuro) LIMON else Color(0xFFFFF4C8), (0.6f + 0.8f * azar(e.semilla, k + 40)) * u, q)
                }
            } else {
                drawCircle(t(PAPEL_CLARO, oscuro, false), sr, centro)
                drawCircle(t(AMARILLO, oscuro, false).copy(alpha = 0.5f), sr, centro, style = Stroke(2.2f * u))
            }
            // El número del disco, en letras infladas de arcoíris con su contorno.
            val cols = if (oscuro || e.noche) ARCOIRIS_FLUOR else ARCOIRIS
            arcoiris(medidor, numero, Rect(centro.x - sr * 1.15f, centro.y - sr * 0.62f, centro.x + sr * 1.15f, centro.y + sr * 0.62f), TITULO, 56f, cols,
                minimo = 14f, borde = if (oscuro) Color(0xFF120A1C) else TINTA)
            // «Disco», arriba a la izquierda.
            val et = Rect(r.left + 7 * u, r.top + 7 * u, r.left + r.width * 0.4f, r.top + 22 * u)
            drawRoundRect(t(PAPEL_CLARO, oscuro, false).copy(alpha = 0.92f), et.topLeft, et.size, CornerRadius(et.height / 2))
            letrero(medidor, disco.substringBefore(" ", disco), et.deflate(2 * u), ROTULO, if (oscuro) LIMON else MAGENTA_CARTEL, 10f, luce = oscuro, espaciado = 0.6f, minimo = 6f)
            // La franja de abajo, con el día.
            val fr = Rect(r.left, r.bottom - r.height * 0.15f, r.right, r.bottom)
            drawRect(t(PAPEL_CLARO, oscuro, false), fr.topLeft, fr.size)
            ondaArcoiris(Offset(fr.left, fr.top), Offset(fr.right, fr.top), 5f, 1.4f * u, 2.2f * u, oscuro || e.noche)
            letrero(medidor, dia, Rect(fr.left + 8 * u, fr.top + 3 * u, fr.right - 8 * u, fr.bottom - 1 * u), TEXTO, tinta(oscuro), 12f, minimo = 6f)
            // El brillo del cartón plastificado y el roce de las esquinas.
            if (!oscuro) drawRect(Brush.linearGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent, Color.Black.copy(alpha = 0.1f)), r.topLeft, r.bottomRight), r.topLeft, r.size)
            listOf(r.topRight, r.bottomRight, r.topLeft).forEach { mancha(it, 10 * u, Color.White.copy(alpha = if (oscuro) 0.06f else 0.22f)) }
        }
        drawPath(forma, Color.Black.copy(alpha = 0.35f), style = Stroke(0.7f * u))
        // La calcomanía de flor, pegada en la esquina.
        calcomaniaPsicodelia(Offset(r.right - r.width * 0.13f, r.top + r.width * 0.13f), r.width * 0.085f, fluor(if (e.noche) VIOLETA else MAGENTA, oscuro), 1f, 0.2f + e.semilla % 5 * 0.2f)
        if (oscuro) resplandor(centro, r.width * 0.7f, if (e.noche) LIMON else FMAGENTA, 0.5f)
    }

    /**
     * El interior del álbum doble, el que se ve al abrir la portada: papel de periódico con la lista de las pistas de cada
     * lado, como renglones con su número, el día y una flor al pie.
     */
    private fun DrawScope.pliegue(r: Rect, e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        drawRect(papel(oscuro, e.noche), r.topLeft, r.size)
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), r.left, r.left + 16 * u), r.topLeft, Size(16 * u, r.height))
        tramaArcoiris(Rect(r.left + 10 * u, r.top + 4 * u, r.right - 6 * u, r.top + 16 * u), oscuro || e.noche, 0.7f)
        val col = Rect(r.left + 16 * u, r.top + 20 * u, r.right - 10 * u, r.bottom - 26 * u)
        listOf("Lado A", "Lado B").forEachIndexed { i, lado ->
            val y0 = col.top + i * col.height / 2f
            aIzquierda(medidor, lado, Rect(col.left, y0, col.right, y0 + 14 * u), ROTULO, acento(oscuro), 9.5f, espaciado = 0.6f, minimo = 6f)
            for (k in 0 until 4) {
                val y = y0 + 20 * u + k * 9 * u
                if (y > y0 + col.height / 2f - 4 * u) break
                textoEn(medidor, "${k + 1 + i * 6}", Rect(col.left, y - 4 * u, col.left + 12 * u, y + 4 * u), ROTULO, tenue(oscuro), 7f, minimo = 4f)
                drawLine(tenue(oscuro).copy(alpha = 0.45f), Offset(col.left + 16 * u, y), Offset(col.left + 16 * u + (col.width - 20 * u) * (0.5f + 0.4f * azar(e.semilla, k + i * 7)), y), 1.2f * u, StrokeCap.Round)
            }
        }
        letrero(medidor, e.fecha.split(" · ").getOrElse(1) { "" }, Rect(r.left + 16 * u, r.bottom - 22 * u, r.right - 30 * u, r.bottom - 8 * u), TEXTO, tenue(oscuro), 9f, minimo = 5f)
        with(TrazosPsicodelia) { flor(Offset(r.right - 18 * u, r.bottom - 15 * u), 6 * u, fluor(MAGENTA, oscuro), fluor(VERDE, oscuro), 6, 0.3f) }
    }

    // =================================================================
    //  La página abierta: la funda interior, en papel de periódico
    // =================================================================

    override val altoEncabezado: Dp = 112.dp

    /**
     * La etiqueta de un disco en [c], de radio [r]: el papel de [color] con el anillo de su complementario, la flor arriba, el
     * agujero del centro y [numero] debajo; alrededor, un anillo de vinilo con sus surcos. De noche ([luna]), la etiqueta del
     * disco de los sueños, con la luna.
     */
    private fun DrawScope.etiqueta(c: Offset, r: Float, color: Color, contra: Color, numero: String, medidor: TextMeasurer, tintaN: Color, luna: Boolean, brilla: Boolean) {
        val u = density
        drawCircle(Color.Black.copy(alpha = 0.18f), r + 6 * u, c + Offset(0.8f * u, 1.2f * u))
        drawCircle(VINILO, r + 6 * u, c)
        var k = r + 1.5f * u
        while (k < r + 5.5f * u) { drawCircle(Color.White.copy(alpha = 0.08f), k, c, style = Stroke(0.4f * u)); k += 1.2f * u }
        drawArc(Color.White.copy(alpha = 0.18f), 210f, 40f, false, Offset(c.x - r - 3.5f * u, c.y - r - 3.5f * u), Size((r + 3.5f * u) * 2, (r + 3.5f * u) * 2), style = Stroke(3 * u))
        if (brilla) drawCircle(color.copy(alpha = 0.3f), r + 2 * u, c)
        drawCircle(color, r, c)
        drawCircle(contra, r * 0.88f, c, style = Stroke(r * 0.06f))
        if (luna) lunita(Offset(c.x, c.y - r * 0.48f), r * 0.24f, contra, color)
        else with(TrazosPsicodelia) { flor(Offset(c.x, c.y - r * 0.46f), r * 0.3f, contra, PAPEL_CLARO, 6, 0.3f) }
        drawCircle(Color(0xFFEDE6D8), r * 0.07f, c)
        textoEn(medidor, numero, Rect(c.x - r * 0.75f, c.y + r * 0.16f, c.x + r * 0.75f, c.y + r * 0.62f), ROTULO, tintaN, 9f, minimo = 5f)
    }

    /**
     * El encabezado de la funda interior: a la izquierda, la etiqueta del disco del día con su número; a la derecha, la flor
     * que vibra en su aro con el símbolo de la paz; al centro, la cabecera, el disco del día en letras infladas, el día con el
     * lado y la pista, y el detalle; al pie, una onda de tinta de arcoíris con una flor en medio. De noche, la etiqueta lleva
     * la luna y el arcoíris es el de las tintas que brillan.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val luz = p.oscura
        val partes = e.fecha.split(" · ")
        val disco = partes.getOrElse(0) { e.fecha }
        val resto = partes.drop(1).joinToString(" · ")
        // La etiqueta del disco, a la izquierda.
        val ec = Offset(32 * u, 54 * u)
        etiqueta(ec, 22 * u, fluor(if (e.noche) VIOLETA else NARANJA, luz).let { if (luz) it else mezcla(it, p.color, 0.05f) }, fluor(if (e.noche) AMARILLO else AZUL, luz),
            Psicodelia.numero(e.numero), medidor, if (luz) Color(0xFF1C1030) else PAPEL_CLARO, e.noche, luz)
        // La flor en su aro, a la derecha.
        val fc = Offset(w - 32 * u, 52 * u)
        if (luz) resplandor(fc, 40 * u, FMAGENTA, 0.6f)
        with(TrazosPsicodelia) { emblema(fc, 21 * u, fluor(NARANJA, luz), if (luz) Color(0xFF221539) else AZUL, 0.4f) }
        // El rótulo, el disco, el día y el detalle.
        val x0 = 62 * u; val x1 = w - 62 * u
        letrero(medidor, e.rotulo, Rect(x0, 10 * u, x1, 24 * u), ROTULO, p.acento, 9.5f, luce = luz, espaciado = 0.6f, minimo = 6f)
        textoEn(medidor, disco, Rect(x0, 27 * u, x1, 58 * u).translate(1.2f * u, 1 * u), TITULO, (if (luz) FMAGENTA else fluor(VERDE, false)).copy(alpha = 0.55f), 24f, minimo = 10f)
        letrero(medidor, disco, Rect(x0, 27 * u, x1, 58 * u), TITULO, p.tinta, 24f, luce = luz, minimo = 10f)
        textoEn(medidor, resto, Rect(x0, 60 * u, x1, 76 * u), TEXTO, p.detalle, 11f, minimo = 6f)
        textoEn(medidor, e.detalle, Rect(x0, 77 * u, x1, 91 * u), TEXTO, p.tenue, 10f, minimo = 6f)
        // La onda de arcoíris del pie, con su flor.
        ondaArcoiris(Offset(18 * u, 100 * u), Offset(w / 2 - 10 * u, 100 * u), 3f, 2 * u, 1.6f * u, luz, 0.85f)
        ondaArcoiris(Offset(w / 2 + 10 * u, 100 * u), Offset(w - 18 * u, 100 * u), 3f, 2 * u, 1.6f * u, luz, 0.85f)
        with(TrazosPsicodelia) { flor(Offset(w / 2, 100 * u), 6 * u, fluor(MAGENTA, luz), fluor(VERDE, luz), 6, 0.4f) }
        drawLine(p.tinta.copy(alpha = 0.14f), Offset(4 * u, h - 2 * u), Offset(w - 4 * u, h - 2 * u), 0.6f * u)
    }

    /**
     * Sobre el papel de periódico (que ya trae su trama de arcoíris arriba): el roce redondo que deja el disco en la funda,
     * abajo a la derecha, y el remate del pie (una flor entre dos ondas). Bajo la luz negra, el papel se hunde en el violeta en
     * las orillas y el centro, donde se lee, queda claro. De noche, las luces líquidas del show, tenues, y la luna.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tintaP = p.tinta
        // El roce del disco en la funda.
        val rc = Offset(w - 64 * u, h - 150 * u)
        drawCircle(tintaP.copy(alpha = 0.035f), 60 * u, rc, style = Stroke(10 * u))
        drawCircle(tintaP.copy(alpha = 0.03f), 22 * u, rc, style = Stroke(3 * u))
        // El remate del pie.
        val y = h - 44 * u
        ondaArcoiris(Offset(w / 2 - w * 0.18f, y), Offset(w / 2 - 9 * u, y), 2f, 1.6f * u, 1.3f * u, p.oscura, 0.45f)
        ondaArcoiris(Offset(w / 2 + 9 * u, y), Offset(w / 2 + w * 0.18f, y), 2f, 1.6f * u, 1.3f * u, p.oscura, 0.45f)
        with(TrazosPsicodelia) { flor(Offset(w / 2, y), 5 * u, fluor(MAGENTA, p.oscura).copy(alpha = 0.6f), fluor(VERDE, p.oscura).copy(alpha = 0.6f), 6, 0.3f) }
        if (oscuro && !p.oscura) {
            drawRect(Brush.radialGradient(0f to FVIOLETA.copy(alpha = 0.06f), 0.45f to Color.Transparent, 1f to SALA.copy(alpha = 0.62f),
                center = Offset(w * 0.5f, h * 0.3f), radius = maxOf(w, h) * 0.9f))
        }
        if (noche) {
            with(TrazosPsicodelia) { lucesLiquidas(Size(w * 0.8f, h * 0.3f), 0.35f, listOf(FMAGENTA, LIMON, FVIOLETA), 0.16f, Offset(w * 0.2f, h * 0.66f)) }
            val l = Offset(w - 40 * u, h - 96 * u)
            resplandor(l, 46 * u, LIMON, 0.35f)
            lunita(l, 12 * u, LIMON.copy(alpha = 0.6f), p.color)
        }
    }

    /** La barra de arriba: el borde de la funda interior, con la trama de arcoíris y la franja de tinta al pie. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val luz = p.oscura || oscuro
        drawRect(if (p.oscura) mezcla(p.color, Color.Black, 0.3f) else t(mezcla(p.color, PARED, 0.5f), oscuro, false))
        tramaArcoiris(Rect(0f, h - 14 * u, w, h - 4 * u), luz, if (luz) 0.5f else 0.6f)
        val banda = Rect(0f, h - 4 * u, w, h)
        if (luz) drawRect(Brush.horizontalGradient(ARCOIRIS_FLUOR.map { it.copy(alpha = 0.3f) }, 0f, w), Offset(0f, banda.top - 3 * u), Size(w, banda.height + 3 * u))
        drawRect(Brush.horizontalGradient(if (luz) ARCOIRIS_FLUOR else ARCOIRIS, 0f, w), banda.topLeft, banda.size)
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 0.6f * u)
    }

    /**
     * El botón de guardar es una burbuja de cartel impresa dos veces: el verde corrido debajo y el magenta encima (de noche,
     * el verde limón que brilla con el magenta debajo), con un filete crema por dentro, el brillo arriba y una flor en cada
     * punta.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val luz = oscuro || p.oscura
        val r = Rect(3 * u, 3 * u, w - 5 * u, h - 6 * u)
        val rad = CornerRadius(r.height / 2f)
        val frente = if (luz) LIMON else MAGENTA_CARTEL
        val debajo = if (luz) FMAGENTA else VERDE
        if (luz) drawRoundRect(LIMON.copy(alpha = 0.2f), r.inflate(3 * u).topLeft, r.inflate(3 * u).size, CornerRadius(rad.x + 3 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.25f), r.topLeft + Offset(1.5f * u, 3.5f * u), r.size, rad)
        drawRoundRect(debajo, r.topLeft + Offset(2.2f * u, 2.2f * u), r.size, rad)
        drawRoundRect(frente, r.topLeft, r.size, rad)
        val f = r.deflate(2.6f * u)
        drawRoundRect((if (luz) Color(0xFF1C2600) else PAPEL_CLARO).copy(alpha = 0.45f), f.topLeft, f.size, CornerRadius(f.height / 2f), style = Stroke(0.7f * u))
        drawRoundRect(Color.White.copy(alpha = 0.22f), Offset(r.left + 10 * u, r.top + 2 * u), Size(r.width - 20 * u, r.height * 0.28f), CornerRadius(r.height * 0.14f))
        listOf(r.left + r.height * 0.5f, r.right - r.height * 0.5f).forEach { x ->
            with(TrazosPsicodelia) { flor(Offset(x, r.center.y), minOf(6 * u, r.height * 0.26f), if (luz) FMAGENTA else AMARILLO, if (luz) Color(0xFF1C2600) else VIOLETA, 6, 0.3f) }
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = if (oscuro || p.oscura) Color(0xFF1C2600) else Color(0xFFFFF4F8)

    // =================================================================
    //  La libreta de momentos: el periódico del barrio, en la mesa de la cocina
    // =================================================================

    override val altoLibreta: Dp = 270.dp

    /**
     * La calle que se ve por la ventana, en [r]: la niebla que entra, las casas victorianas de enfrente (grises, y una pintada
     * de turquesa) y, de noche, sus ventanas encendidas (una con la luz negra de otro departamento).
     */
    private fun DrawScope.calle(r: Rect, oscuro: Boolean) {
        val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            if (oscuro) drawRect(Brush.verticalGradient(listOf(Color(0xFF1A1230), Color(0xFF2A1F44)), r.top, r.bottom), r.topLeft, r.size)
            else drawRect(Brush.verticalGradient(listOf(NIEBLA, Color(0xFFF3E6D6), TrazosPsicodelia.CIELO_TARDE), r.top, r.bottom), r.topLeft, r.size)
            val base = r.bottom + r.height * 0.06f
            val ancho = r.width * 0.36f
            val colores = listOf(TrazosPsicodelia.CASA_GRIS, TrazosPsicodelia.CASA_TURQUESA, mezcla(TrazosPsicodelia.CASA_GRIS, TrazosPsicodelia.CASA_BLANCA, 0.4f))
            colores.forEachIndexed { i, col ->
                val cx = r.left + ancho * (0.42f + i * 0.98f)
                val ventanas = if (oscuro) (if (i == 1) FVIOLETA else Color(0xFFFFC46A)) else Color(0xFF5E6B78)
                val madera = if (oscuro) mezcla(col, SALA, 0.78f) else col
                with(TrazosPsicodelia) { casaVictoriana(Offset(cx, base), ancho * 0.94f, r.height * 0.86f, madera, if (oscuro) mezcla(TrazosPsicodelia.CASA_BLANCA, SALA, 0.7f) else TrazosPsicodelia.CASA_BLANCA, ventanas) }
                if (oscuro && i == 1) resplandor(Offset(cx - ancho * 0.1f, base - r.height * 0.45f), ancho * 0.6f, FVIOLETA, 0.6f)
            }
            // La niebla que entra en bancos.
            if (!oscuro) {
                for (k in 0 until 4) {
                    val y = r.top + r.height * (0.25f + 0.2f * k)
                    drawOval(Color.White.copy(alpha = 0.32f), Offset(r.left - r.width * 0.2f + k * r.width * 0.15f, y - 8 * u), Size(r.width * 0.9f, 16 * u))
                }
                drawRect(NIEBLA.copy(alpha = 0.25f), r.topLeft, r.size)
            } else for (k in 0 until 3) drawOval(FVIOLETA.copy(alpha = 0.06f), Offset(r.left + k * r.width * 0.3f - r.width * 0.1f, r.top + r.height * (0.4f + 0.15f * k)), Size(r.width * 0.7f, 14 * u))
        }
    }

    /**
     * La portada del periódico del barrio, doblada a la mitad, en [r]: el nombre inventado, «La Niebla», en letras infladas de
     * tinta de arcoíris (la del tintero partido); las orejas de la cabecera (una flor y el número de la edición); la línea de
     * la fecha con el nombre y la cuenta de la libreta; abajo, el grabado grande de la flor en su mandala y las columnas.
     */
    private fun DrawScope.primeraPlana(r: Rect, e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val papelP = if (oscuro) Color(0xFF3A3152) else PAPEL
        val tintaP = if (oscuro) Color(0xFFD9CFEA) else TINTA
        drawRect(papelP, r.topLeft, r.size)
        // El papel barato: fibras y el amarillo de las orillas.
        for (k in 0 until 30) drawLine(tintaP.copy(alpha = 0.05f), Offset(r.left + r.width * azar(e.semilla, k), r.top + r.height * azar(e.semilla, k + 40)),
            Offset(r.left + r.width * azar(e.semilla, k) + 3 * u, r.top + r.height * azar(e.semilla, k + 40) + 1 * u), 0.5f * u)
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.7f to Color.Transparent, 1f to Color(0xFFB89A62).copy(alpha = if (oscuro) 0.1f else 0.25f),
            center = r.center, radius = r.width * 0.75f), r.topLeft, r.size)
        val m = r.deflate(6 * u)
        // La cabecera, en tinta de arcoíris.
        val cab = Rect(m.left + m.width * 0.16f, m.top, m.right - m.width * 0.16f, m.top + m.height * 0.3f)
        arcoiris(medidor, "La Niebla", cab, TITULO, 30f, if (oscuro) ARCOIRIS_FLUOR else ARCOIRIS, minimo = 10f)
        if (oscuro) resplandor(cab.center, cab.width * 0.5f, FMAGENTA, 0.4f)
        // Las orejas: la flor a la izquierda y el número de la edición a la derecha.
        val oreja = m.height * 0.24f
        with(TrazosPsicodelia) { flor(Offset(m.left + oreja * 0.55f, cab.center.y), oreja * 0.42f, fluor(MAGENTA, oscuro), fluor(VERDE, oscuro), 6, 0.4f) }
        val or2 = Rect(m.right - m.width * 0.15f, cab.top + 2 * u, m.right, cab.bottom - 2 * u)
        drawRect(tintaP.copy(alpha = 0.6f), or2.topLeft, or2.size, style = Stroke(0.6f * u))
        textoEn(medidor, "Nº", Rect(or2.left, or2.top, or2.right, or2.center.y), ROTULO, tintaP, 7f, minimo = 4f)
        textoEn(medidor, Psicodelia.numero(e.hojas.coerceAtLeast(1)), Rect(or2.left, or2.center.y - 1 * u, or2.right, or2.bottom), ROTULO, tintaP, 8f, minimo = 4f)
        // La línea de la fecha, entre dos rayas: el nombre y la cuenta.
        val yl = cab.bottom + 2 * u
        drawLine(tintaP, Offset(m.left, yl), Offset(m.right, yl), 0.9f * u)
        textoEn(medidor, e.nombre + " · " + e.cuenta, Rect(m.left, yl + 1 * u, m.right, yl + 11 * u), ROTULO, tintaP, 7.5f, minimo = 4.5f)
        drawLine(tintaP, Offset(m.left, yl + 12 * u), Offset(m.right, yl + 12 * u), 0.5f * u)
        // El grabado de la flor en su mandala, con los rayos en arcoíris, a la izquierda.
        val cuerpo = Rect(m.left, yl + 15 * u, m.right, m.bottom)
        val mc = Offset(cuerpo.left + cuerpo.width * 0.27f, cuerpo.top + cuerpo.height * 0.62f)
        val mr = cuerpo.height * 0.56f
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            for (k in 0 until 24) {
                val a = 2 * PI * k / 24
                drawLine(colorArcoiris(k / 23f, oscuro).copy(alpha = 0.75f), mc, Offset(mc.x + (mr * cos(a)).toFloat(), mc.y + (mr * sin(a)).toFloat()), 1.4f * u)
            }
            drawCircle(papelP, mr * 0.62f, mc)
            drawCircle(tintaP, mr * 0.62f, mc, style = Stroke(0.8f * u))
            with(TrazosPsicodelia) { flor(mc, mr * 0.5f, fluor(MAGENTA, oscuro), fluor(VERDE, oscuro), 8, 0.5f) }
        }
        // Las columnas de texto, a la derecha, con su titular.
        val cols = Rect(cuerpo.left + cuerpo.width * 0.56f, cuerpo.top + 2 * u, cuerpo.right, cuerpo.bottom)
        drawRect(tintaP.copy(alpha = 0.85f), Offset(cols.left, cols.top), Size(cols.width * 0.9f, 3 * u))
        drawRect(tintaP.copy(alpha = 0.85f), Offset(cols.left, cols.top + 5 * u), Size(cols.width * 0.6f, 3 * u))
        val ancho = (cols.width - 4 * u) / 2f
        for (j in 0 until 2) {
            var y = cols.top + 12 * u; var k = 0
            while (y < cols.bottom - 2 * u) {
                val largo = ancho * (if (k % 6 == 5) 0.5f else 0.92f)
                drawLine(tintaP.copy(alpha = 0.35f), Offset(cols.left + j * (ancho + 4 * u), y), Offset(cols.left + j * (ancho + 4 * u) + largo, y), 1.1f * u)
                y += 3 * u; k++
            }
        }
        drawLine(tintaP.copy(alpha = 0.4f), Offset(cols.left - 3 * u, cuerpo.top), Offset(cols.left - 3 * u, cuerpo.bottom), 0.5f * u)
    }

    /**
     * El periódico alternativo del barrio, «La Niebla» (nombre inventado: se vendía en la calle), sobre la mesa de pino de la
     * cocina del departamento: detrás, la ventana del mirador con la niebla y las casas de enfrente, la cortina teñida con
     * nudos y el tubo de luz negra encima; en la mesa, la taza de café y el lápiz. El periódico está doblado a la mitad y debajo
     * asoman las ediciones anteriores (engorda con las hojas). Al abrirse, la plana gira sobre su lomo y deja ver las columnas
     * de adentro. Bajo la luz negra, la tinta de arcoíris brilla.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val a = e.abierto.coerceIn(0f, 1f)
        val mesaY = h * 0.42f
        // La pared de la cocina.
        drawRect(Brush.verticalGradient(listOf(c(PARED), c(mezcla(PARED, PARED_SOMBRA, 0.5f))), 0f, mesaY), Offset.Zero, Size(w, mesaY))
        // La ventana del mirador, con la calle y su marco de madera pintada.
        val ven = Rect(w * 0.1f, h * 0.06f, w * 0.64f, mesaY - 6 * u)
        calle(ven, oscuro)
        val marco = c(Color(0xFFF4EFE4))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(ven.left, ven.top), Size(ven.width, 4 * u))
        drawRect(marco, Offset(ven.center.x - 2 * u, ven.top), Size(4 * u, ven.height))
        drawRect(marco, Offset(ven.left, ven.top + ven.height * 0.45f), Size(ven.width, 4 * u))
        drawRect(marco, ven.topLeft, ven.size, style = Stroke(5 * u))
        drawRect(Color.Black.copy(alpha = 0.18f), ven.deflate(2.5f * u).topLeft, ven.deflate(2.5f * u).size, style = Stroke(0.8f * u))
        // El alféizar.
        drawRect(c(Color(0xFFE9E1D2)), Offset(ven.left - 6 * u, ven.bottom), Size(ven.width + 12 * u, 5 * u))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(ven.left - 6 * u, ven.bottom + 5 * u), Size(ven.width + 12 * u, 1.5f * u))
        // La cortina teñida con nudos, recogida a la derecha de la ventana.
        val cort = Path().apply {
            moveTo(ven.right - 10 * u, ven.top - 4 * u); lineTo(ven.right + 22 * u, ven.top - 4 * u)
            cubicTo(ven.right + 18 * u, ven.top + ven.height * 0.4f, ven.right + 26 * u, ven.top + ven.height * 0.7f, ven.right + 20 * u, mesaY - 2 * u)
            lineTo(ven.right + 2 * u, mesaY - 2 * u)
            cubicTo(ven.right + 8 * u, ven.top + ven.height * 0.6f, ven.right - 8 * u, ven.top + ven.height * 0.4f, ven.right - 10 * u, ven.top - 4 * u); close()
        }
        clipPath(cort) {
            val cc = Offset(ven.right + 8 * u, ven.top + ven.height * 0.45f)
            drawRect(c(Color(0xFF7B3FD1)), Offset(ven.right - 12 * u, ven.top - 6 * u), Size(40 * u, mesaY))
            for (k in 6 downTo 1) drawCircle(c(listOf(Color(0xFFFF7A1A), Color(0xFFFFD23F), Color(0xFFE0218A), Color(0xFF7B3FD1), Color(0xFF2F6BFF), Color(0xFFFFD23F))[k % 6]).copy(alpha = 0.85f),
                k * 7 * u, cc, style = Stroke(3.5f * u))
            for (k in 0 until 6) drawLine(Color.Black.copy(alpha = 0.12f), Offset(ven.right - 6 * u + k * 5 * u, ven.top), Offset(ven.right - 2 * u + k * 5 * u, mesaY), 1.6f * u)
        }
        // El tubo de luz negra sobre la ventana.
        val tubo = Rect(ven.left + ven.width * 0.15f, ven.top - 9 * u, ven.right - ven.width * 0.15f, ven.top - 5 * u)
        drawRoundRect(if (oscuro) FVIOLETA else c(Color(0xFF3A2F64)), tubo.topLeft, tubo.size, CornerRadius(2 * u))
        if (oscuro) resplandor(tubo.center, w * 0.4f, FVIOLETA, 0.9f)
        // La mesa de pino, en perspectiva, con su canto.
        val mesa = Path().apply { moveTo(w * 0.03f, mesaY); lineTo(w * 0.97f, mesaY); lineTo(w, h * 0.9f); lineTo(0f, h * 0.9f); close() }
        clipPath(mesa) {
            pino(Rect(0f, mesaY, w, h * 0.9f), c(PINO), 501, nudos = 2)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.12f), Color.Transparent, Color.White.copy(alpha = if (oscuro) 0.02f else 0.1f)), mesaY, h * 0.9f), Offset(0f, mesaY), Size(w, h * 0.9f - mesaY))
            // La luz fría de la ventana sobre la mesa.
            if (!oscuro) drawPath(Path().apply { moveTo(ven.left, mesaY); lineTo(ven.right, mesaY); lineTo(ven.right + w * 0.1f, h * 0.9f); lineTo(ven.left - w * 0.02f, h * 0.9f); close() }, Color.White.copy(alpha = 0.12f))
        }
        val canto = Rect(0f, h * 0.9f, w, h)
        pino(canto, c(PINO_OSC), 503, nudos = 0)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.04f else 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.35f)), canto.top, canto.bottom), canto.topLeft, canto.size)
        // La taza de café y el lápiz.
        val tc = Offset(w * 0.86f, h * 0.6f)
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(tc.x - 15 * u, tc.y - 4 * u), Size(32 * u, 16 * u))
        drawOval(c(Color(0xFFE88A3A)), Offset(tc.x - 14 * u, tc.y - 10 * u), Size(28 * u, 18 * u))
        drawOval(c(Color(0xFF4A2A18)), Offset(tc.x - 11 * u, tc.y - 8 * u), Size(22 * u, 13 * u))
        drawOval(Color.White.copy(alpha = 0.18f), Offset(tc.x - 6 * u, tc.y - 6 * u), Size(8 * u, 3 * u))
        drawArc(c(Color(0xFFE88A3A)), -60f, 120f, false, Offset(tc.x + 9 * u, tc.y - 7 * u), Size(10 * u, 10 * u), style = Stroke(3 * u))
        rotate(-18f, Offset(w * 0.1f, h * 0.8f)) {
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(w * 0.04f, h * 0.82f), Offset(w * 0.2f, h * 0.82f), 4 * u, StrokeCap.Round)
            drawLine(c(Color(0xFFF2C14E)), Offset(w * 0.04f, h * 0.81f), Offset(w * 0.18f, h * 0.81f), 3.4f * u)
            drawPath(Path().apply { moveTo(w * 0.18f, h * 0.81f - 1.7f * u); lineTo(w * 0.205f, h * 0.81f); lineTo(w * 0.18f, h * 0.81f + 1.7f * u); close() }, c(Color(0xFFE2C79A)))
        }
        // El periódico.
        val cw = minOf(w * 0.56f, (h * 0.9f - mesaY) * 1.36f)
        val ch = cw / 1.36f
        val x0 = w * 0.47f - cw / 2
        val y0 = mesaY + (h * 0.9f - mesaY - ch) * 0.45f
        val grueso = (1.5f + minOf(e.hojas, 50) * 0.11f) * u
        val cara = Rect(x0, y0, x0 + cw, y0 + ch)
        rotate(-2f, cara.center) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.25f), Offset(cara.left + 4 * u, cara.top + grueso + 4 * u), cara.size)
            // Las ediciones de antes, apiladas debajo.
            val pila = Rect(cara.left + 1.5f * u, cara.top + 1.5f * u, cara.right + grueso * 0.6f, cara.bottom + grueso)
            drawRect(if (oscuro) Color(0xFF2E2742) else mezcla(PAPEL, Color(0xFFC9B48A), 0.35f), pila.topLeft, pila.size)
            var yy = cara.bottom + 1 * u
            while (yy < pila.bottom) { drawLine(Color.Black.copy(alpha = 0.14f), Offset(pila.left, yy), Offset(pila.right, yy), 0.4f * u); yy += 1.2f * u }
            // Debajo de la plana, las columnas de adentro.
            val girar = ((a - 0.15f) / 0.85f).coerceIn(0f, 1f)
            if (girar > 0.01f) {
                drawRect(if (oscuro) Color(0xFF3A3152) else PAPEL, cara.topLeft, cara.size)
                val tintaI = (if (oscuro) Color(0xFFD9CFEA) else TINTA)
                val cols = 3
                val anchoC = (cara.width - 20 * u) / cols
                for (j in 0 until cols) {
                    var y = cara.top + 14 * u; var k = 0
                    while (y < cara.bottom - 8 * u) { drawLine(tintaI.copy(alpha = 0.3f), Offset(cara.left + 8 * u + j * (anchoC + 2 * u), y), Offset(cara.left + 8 * u + j * (anchoC + 2 * u) + anchoC * (if (k % 7 == 6) 0.5f else 0.92f), y), 1f * u); y += 3 * u; k++ }
                }
                drawRect(papel(oscuro, false), Rect(cara.left + 8 * u, cara.top + 4 * u, cara.right - 8 * u, cara.top + 13 * u).topLeft, Size(cara.width - 16 * u, 9 * u))
                textoEn(medidor, e.cuenta, Rect(cara.left + 8 * u, cara.top + 3 * u, cara.right - 8 * u, cara.top + 13 * u), TEXTO, tintaI, 8.5f, minimo = 5f)
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent), cara.left, cara.left + 14 * u), cara.topLeft, Size(14 * u, cara.height))
            }
            val ang = girar * 1.5f
            val co = cos(ang); val si = sin(ang)
            afin(co, -si * 0.1f, 0f, 1f, cara.left - cara.left * co, cara.left * si * 0.1f) {
                primeraPlana(cara, e, medidor, oscuro)
                drawRect(Color.Black.copy(alpha = 0.2f), cara.topLeft, cara.size, style = Stroke(0.6f * u))
                if (girar > 0f) drawRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size)
            }
            // El doblez del medio, abajo.
            if (girar < 0.02f) drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f), Offset(cara.left, cara.bottom - 0.6f * u), Offset(cara.right, cara.bottom - 0.6f * u), 1f * u)
        }
        // La luz: de día, la fría de la niebla que entra por la ventana; de noche, la luz negra del tubo.
        if (oscuro) {
            drawRect(Brush.radialGradient(0f to FVIOLETA.copy(alpha = 0.06f), 0.35f to Color.Transparent, 1f to SALA.copy(alpha = 0.55f), center = tubo.center, radius = w * 1.05f), Offset.Zero, Size(w, h))
        } else drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, w), Offset.Zero, Size(w, h))
    }

    /**
     * La pestaña de un día: un sencillo de vinilo con su etiqueta del color del ánimo (de papel crema, si no lo hay), con el
     * número del día en letras infladas y, debajo, la letra de la semana.
     */
    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val c = Offset(w / 2, 22 * u)
        val r = minOf(w / 2 - 2 * u, 20 * u)
        rotate(-4f + 8f * azar(e.semilla, 1), c) {
            drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(1 * u, 1.6f * u))
            drawCircle(VINILO, r, c)
            var k = r * 0.95f
            while (k > r * 0.62f) { drawCircle(Color.White.copy(alpha = 0.07f), k, c, style = Stroke(0.4f * u)); k -= 1.3f * u }
            drawArc(Color.White.copy(alpha = 0.2f), 200f + 40f * azar(e.semilla, 2), 34f, true, Offset(c.x - r * 0.95f, c.y - r * 0.95f), Size(r * 1.9f, r * 1.9f))
            val etiqueta = e.tono ?: if (oscuro) Color(0xFF3A2F52) else PAPEL_CLARO
            if (oscuro && e.tono != null) drawCircle(etiqueta.copy(alpha = 0.3f), r * 0.72f, c)
            drawCircle(etiqueta, r * 0.6f, c)
            drawCircle(Color.Black.copy(alpha = 0.15f), r * 0.6f, c, style = Stroke(0.5f * u))
            val claro = (etiqueta.red * 0.299f + etiqueta.green * 0.587f + etiqueta.blue * 0.114f) > 0.55f
            textoEn(medidor, e.dia, Rect(c.x - r * 0.55f, c.y - r * 0.45f, c.x + r * 0.55f, c.y + r * 0.45f), TITULO, if (claro) TINTA else PAPEL_CLARO, 14f, minimo = 6f)
        }
        letrero(medidor, e.semana, Rect(0f, c.y + r + 1.5f * u, w, h - 1 * u), ROTULO, tenue(oscuro), 8.5f, espaciado = 0.4f, minimo = 5f)
    }

    /** El rótulo de un mes: el nombre del álbum en letras infladas, entre dos ondas de tinta de arcoíris que acaban en una flor. */
    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tam = 15f
        val largo = (anchoTx(medidor, texto, TITULO, tam) + 18 * u).coerceAtMost(w - 70 * u)
        listOf(-1f, 1f).forEach { s ->
            val desde = Offset(w / 2 + s * (largo / 2 + 4 * u), h / 2)
            val hasta = Offset(w / 2 + s * (w / 2 - 12 * u), h / 2)
            ondaArcoiris(if (s < 0) hasta else desde, if (s < 0) desde else hasta, 2.5f, 2.2f * u, 1.6f * u, oscuro, 0.9f)
            with(TrazosPsicodelia) { flor(hasta, 4.5f * u, fluor(if (s < 0) MAGENTA else NARANJA, oscuro), fluor(if (s < 0) VERDE else AZUL, oscuro), 6, 0.3f) }
        }
        letrero(medidor, texto, Rect(w / 2 - largo / 2, 0f, w / 2 + largo / 2, h), TITULO, tinta(oscuro), tam, luce = oscuro, minimo = 8f)
    }
