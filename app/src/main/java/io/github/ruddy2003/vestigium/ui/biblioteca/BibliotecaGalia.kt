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
import androidx.compose.ui.graphics.PathEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.Galia
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.TrazosGalia
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.sombraSuave
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Galia
// =====================================================================

/**
 * El sello de barro de las cartas de Galia: un disco de barro apretado sobre el nudo del cordel, con la marca de quien lo
 * manda hundida, el caballo de las monedas. Lo usan el sobre de la biblioteca y `selloCarta` (al pie de la carta y en su
 * PDF). [alfa] lo hace aparecer al caer; [semilla] cambia las ondas del borde.
 */
internal fun DrawScope.selloGalia(c: Offset, r: Float, barro: Color, alfa: Float = 1f, semilla: Int = 5) {
    val a = alfa.coerceIn(0f, 1f)
    if (a <= 0f || r <= 0f) return
    val base = barro.copy(alpha = 1f)
    val borde = Path()
    for (i in 0..44) {
        val ang = 2 * PI * i / 44
        val rr = r * (1f + 0.05f * sin(ang * 4 + semilla).toFloat() + 0.04f * sin(ang * 9 + semilla * 3).toFloat())
        val p = Offset(c.x + rr * cos(ang).toFloat(), c.y + rr * 0.94f * sin(ang).toFloat())
        if (i == 0) borde.moveTo(p.x, p.y) else borde.lineTo(p.x, p.y)
    }
    borde.close()
    translate(r * 0.08f, r * 0.12f) { drawPath(borde, Color.Black.copy(alpha = 0.28f * a)) }
    drawPath(borde, base.copy(alpha = a))
    drawPath(borde, Brush.radialGradient(listOf(Color.White.copy(alpha = 0.24f * a), Color.Transparent), center = Offset(c.x - r * 0.4f, c.y - r * 0.42f), radius = r * 1.2f))
    // El labio que levantó el sello al apretarlo, y el hueco de la marca.
    drawCircle(mezcla(base, Color.Black, 0.3f).copy(alpha = 0.5f * a), r * 0.74f, c, style = Stroke(r * 0.07f))
    drawCircle(mezcla(base, Color.White, 0.25f).copy(alpha = 0.4f * a), r * 0.8f, c + Offset(r * 0.03f, r * 0.04f), style = Stroke(r * 0.04f))
    val hondo = mezcla(base, Color.Black, 0.48f).copy(alpha = a)
    val labio = mezcla(base, Color.White, 0.3f).copy(alpha = 0.7f * a)
    caballoGalia(c + Offset(r * 0.04f, r * 0.05f), r * 0.66f, labio)
    caballoGalia(c, r * 0.66f, hondo)
}

/**
 * El estátero en pequeño, en un cuadrado de lado 2·[r] centrado en [c] y de un solo color (el oro de la moneda, el hueco del
 * sello): el caballo de las monedas ([TrazosGalia.caballo]), mirando a la derecha, con la perla sobre el lomo y la rueda debajo.
 */
internal fun DrawScope.caballoGalia(c: Offset, r: Float, color: Color) {
    with(TrazosGalia) {
        caballo(Offset(c.x, c.y - r * 0.12f), r * 1.78f, color, mira = 1f, recio = if (r < 12f * density) 1.6f else 1f)
        rueda(Offset(c.x - r * 0.04f, c.y + r * 0.7f), r * 0.24f, color, Color.Transparent)
    }
    drawCircle(color, r * 0.08f, Offset(c.x + r * 0.02f, c.y - r * 0.62f))
}

/**
 * Galia: la biblioteca de la casa grande del oppidum, por dentro. El estante del diario es el estante de las cuentas de la
 * casa, un bastidor de roble labrado a azuela contra la pared de barro, con la despensa encima y el hogar al pie; cada entrada
 * es la funda de lana a cuadros con sus tablillas, el cofre de mimbre o el zurrón de cuero. Su dibujo está en
 * `MuebleEstanteGalia.kt`.
 * La tapa del diario es la tablilla del día, cerrada: el marco de roble, la cera donde se lee lo escrito, las correas de las
 * bisagras y la correa que la ciñe, cerrada con una fíbula de bronce; al abrirse, la fíbula suelta la correa y la tablilla
 * gira sobre sus bisagras hasta dejar ver la de adentro, escrita en una columna y lisa en la otra: lo sagrado no se escribe.
 */
object EstanteGalia : DisenoBiblioteca {
    // ---------------------------------------------------------------- la paleta de la casa grande
    private val ROBLE = TrazosGalia.ROBLE
    private val ROBLE_SOMBRA = TrazosGalia.ROBLE_SOMBRA
    private val MUERDAGO = TrazosGalia.MUERDAGO
    private val BAYA = TrazosGalia.BAYA
    private val BARRO = TrazosGalia.BARRO
    private val PAJA = TrazosGalia.PAJA
    private val MADERA = TrazosGalia.MADERA
    private val ORO = TrazosGalia.ORO
    private val ORO_BRILLO = TrazosGalia.ORO_BRILLO
    private val BRONCE = TrazosGalia.BRONCE
    private val VERDIN = TrazosGalia.VERDIN
    private val HIERRO = TrazosGalia.HIERRO
    private val LANAS = listOf(TrazosGalia.LANA_ROJA, TrazosGalia.LANA_VERDE, TrazosGalia.LANA_OCRE)
    private val LUNA = TrazosGalia.LUNA
    private val FUEGO = TrazosGalia.FUEGO
    private val FUEGO_HALO = TrazosGalia.FUEGO_HALO
    /** El barro de la pared en sombra, el zarzo que se ve donde se cayó el barro y el fondo de la paja, ennegrecido de humo. */
    private val BARRO_SOMBRA = Color(0xFF8E7448)
    private val ZARZO = Color(0xFF7A5A34)
    /** El roble de las tablas y de las tablillas: su color, su canto en luz y su veta; la haya clara de los rótulos. */
    private val TABLA = Color(0xFF8A6A40)
    private val TABLA_LUZ = Color(0xFFB4905C)
    private val VETA = Color(0xFF4A3220)
    /** La cera de abeja de las tablillas: el color de la miel, más oscura en los bordes, y el surco que deja el estilo. */
    private val CERA = Color(0xFFD9BE82)
    private val CERA_OSC = Color(0xFFB8975A)
    /** El cuero de las correas y de los estuches, y el lino de los cordeles. */
    private val CUERO = Color(0xFF7A5232)
    private val CORREA = Color(0xFF5A3A22)
    private val LINO = Color(0xFFD8CBA6)
    /** La tinta del estilo sobre la cera, la tinta gastada y el oro viejo de las marcas. */
    private val TINTA = Color(0xFF20251A)
    private val TENUE = Color(0xFF555B44)
    private val OCRE = Color(0xFF7E590C)
    /** El robledal de noche (modo oscuro): el verde negro donde se hunde la casa lejos del fuego. */
    private val NOCHE = Color(0xFF0F1410)
    /** El cielo de los sueños en modo claro: la misma casa al caer la noche. */
    private val ANOCHECER = Color(0xFF1E2A22)
    /** La tinta de noche: la cera a la luz del fuego. */
    private val TINTA_NOCHE = Color(0xFFF3E4C4)
    private val TENUE_NOCHE = Color(0xFFB9AE90)

    private val TITULO: FontFamily = LetraExtra.piazzolla
    private val TEXTO: FontFamily = LetraExtra.piazzolla
    private val ROTULO: FontFamily = LetraExtra.ysabeau
    private val GRUESO = FontWeight.ExtraBold

    /**
     * El color de lo que no alumbra el fuego: en modo oscuro, la misma casa de noche, hundida en el verde negro del robledal;
     * de día, tal cual (con [noche], la de los sueños, la casa al anochecer).
     */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color = when {
        oscuro -> mezcla(c, NOCHE, 0.84f)
        noche -> mezcla(c, ANOCHECER, 0.42f)
        else -> c
    }

    /** Un color a [cerca] (0 a 1) del fuego: de día, como [t]; de noche, menos hundido cuanto más cerca, y entibiado por la llama. */
    private fun luz(c: Color, oscuro: Boolean, noche: Boolean, cerca: Float): Color {
        if (!oscuro) return t(c, false, noche)
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.9f - 0.52f * Math.pow(k.toDouble(), 1.4).toFloat()), FUEGO, 0.2f * k)
    }

    /** La cera donde se lee: de día, de miel; de noche, la cera a oscuras que alcanza el fuego, con la tinta clara encima. */
    private fun cera(oscuro: Boolean, noche: Boolean, cerca: Float = 0.45f): Color = when {
        oscuro -> luz(CERA, true, noche, cerca)
        noche -> mezcla(CERA, ANOCHECER, 0.24f)
        else -> CERA
    }

    private fun tinta(oscuro: Boolean): Color = if (oscuro) TINTA_NOCHE else TINTA
    private fun tenue(oscuro: Boolean): Color = if (oscuro) TENUE_NOCHE else TENUE
    /** El oro de las marcas: oro viejo de día; de noche, la hoguera. */
    private fun oro(oscuro: Boolean): Color = if (oscuro) FUEGO else OCRE

    /** La luz del fuego sobre lo que lo rodea, en modo oscuro: el halo cálido y, más cerca, la llama. */
    private fun DrawScope.resplandor(c: Offset, r: Float, fuerza: Float = 1f) {
        mancha(c, r, FUEGO_HALO.copy(alpha = 0.22f * fuerza))
        mancha(c, r * 0.42f, FUEGO_HALO.copy(alpha = 0.22f * fuerza))
    }

    // =================================================================
    //  Ayudantes de dibujo de la casa grande
    // =================================================================

    /** El ancho de un texto, para ajustar rótulos a lo que dicen. */
    private fun anchoTx(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal): Float =
        medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, fontWeight = peso, letterSpacing = espaciado.sp), maxLines = 1, softWrap = false).size.width.toFloat()

    /** Un texto alineado a la izquierda dentro de [r]: [textoEn] en una caja de su ancho medido. */
    private fun DrawScope.aIzquierda(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                     espaciado: Float = 0f, peso: FontWeight = FontWeight.Normal, minimo: Float = 6f) {
        val ancho = anchoTx(medidor, texto, letra, tam, espaciado, peso) + 2f
        textoEn(medidor, texto, Rect(r.left, r.top, minOf(r.right, r.left + ancho), r.bottom), letra, color, tam, peso = peso, espaciado = espaciado, minimo = minimo)
    }

    /**
     * Un texto en [r]: en un renglón si cabe a [tam]; si no cabe y tiene un « · », se parte ahí en dos renglones, el segundo a
     * [k] veces el tamaño («Noche 319» arriba, el mes de Coligny abajo). [r] debe tener alto para los dos.
     */
    private fun DrawScope.enDos(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, color: Color, tam: Float,
                                peso: FontWeight = FontWeight.Normal, k: Float = 0.78f, color2: Color = color) {
        val i = texto.indexOf(" · ")
        if (i <= 0 || anchoTx(medidor, texto, letra, tam, peso = peso) <= r.width) {
            textoEn(medidor, texto, r, letra, color, tam, peso = peso, minimo = tam * 0.5f)
            return
        }
        val mitad = r.top + r.height * 0.54f
        textoEn(medidor, texto.substring(0, i).trim(), Rect(r.left, r.top, r.right, mitad), letra, color, tam, peso = peso, minimo = tam * 0.5f)
        textoEn(medidor, texto.substring(i + 3).trim(), Rect(r.left, mitad, r.right, r.bottom), ROTULO, color2, tam * k, peso = FontWeight.SemiBold, espaciado = 0.3f, minimo = tam * 0.42f)
    }

    /** Dibuja [bloque] con la transformación afín x' = a·x + c·y + tx, y' = b·x + d·y + ty (para las tablillas que giran). */
    private fun DrawScope.afin(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float, bloque: DrawScope.() -> Unit) {
        withTransform({ transform(Matrix(floatArrayOf(a, b, 0f, 0f, c, d, 0f, 0f, 0f, 0f, 1f, 0f, tx, ty, 0f, 1f))) }) { bloque() }
    }

    /** La pestaña [k] de las cinco: exactamente una quinta parte del ancho [w] (ahí se toca). */
    private fun quinta(k: Int, w: Float, arriba: Float, abajo: Float): Rect = Rect(w * k / 5f, arriba, w * (k + 1) / 5f, abajo)

    /**
     * Madera de roble dentro de [r]: el color [base], la veta (a lo largo, o de arriba abajo con [vertical]), las hebras claras
     * y [nudos] nudos con sus anillos.
     */
    private fun DrawScope.madera(r: Rect, base: Color, semilla: Int, vertical: Boolean = false, nudos: Int = 1) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(base, r.topLeft, r.size)
        vetas(r, mezcla(base, Color.Black, 0.5f).copy(alpha = 0.42f), semilla, densidad = 1.15f, vertical = vertical, grosor = 0.8f, ondulacion = 1.3f)
        vetas(r, mezcla(base, Color.White, 0.35f).copy(alpha = 0.16f), semilla + 7, densidad = 0.6f, vertical = vertical, grosor = 0.6f, ondulacion = 1.1f)
        for (k in 0 until nudos) {
            val o = Offset(r.left + r.width * (0.15f + 0.7f * azar(semilla, k + 30)), r.top + r.height * (0.2f + 0.6f * azar(semilla, k + 31)))
            val largo = (5f + 4f * azar(semilla, k + 32)) * u
            val ancho = minOf(if (vertical) r.width else r.height, 5 * u) * 0.5f
            val s = if (vertical) Size(ancho, largo) else Size(largo, ancho)
            drawOval(mezcla(base, Color.Black, 0.55f).copy(alpha = 0.6f), o - Offset(s.width / 2, s.height / 2), s)
            drawOval(mezcla(base, Color.Black, 0.4f).copy(alpha = 0.35f), o - Offset(s.width, s.height), Size(s.width * 2, s.height * 2), style = Stroke(0.5f * u))
        }
    }

    /**
     * La pared de zarzo y barro dentro de [r]: el barro [barro] con las marcas de las manos que lo apretaron y, donde se cayó,
     * el zarzo de varas entretejidas en las estacas. [fuerza] baja el dibujo (para la pared en sombra).
     */
    private fun DrawScope.zarzo(r: Rect, barro: Color, semilla: Int, fuerza: Float = 1f, caidos: Float = 1f) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(barro, r.topLeft, r.size)
        val oscuro = mezcla(barro, Color.Black, 0.4f)
        val claro = mezcla(barro, Color.White, 0.3f)
        // Las manchas del barro y la paja picada que lleva dentro.
        val n = (r.width * r.height / (u * u * 60f)).toInt().coerceIn(8, 260)
        for (k in 0 until n) {
            val o = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k))
            val s = (2f + 6f * azar(semilla + 2, k)) * u
            if (k % 5 == 0) drawLine(mezcla(barro, PAJA, 0.6f).copy(alpha = 0.35f * fuerza), o, o + Offset(s * 0.8f, s * (azar(semilla + 3, k) - 0.5f) * 0.6f), 0.5f * u)
            else drawOval((if (k % 3 == 0) claro else oscuro).copy(alpha = 0.09f * fuerza), o, Size(s, s * 0.6f))
        }
        // Las pasadas de la mano: arcos anchos y bajos.
        val m = (r.width * r.height / (u * u * 700f)).toInt().coerceIn(2, 60)
        for (k in 0 until m) {
            val o = Offset(r.left + r.width * azar(semilla + 4, k), r.top + r.height * azar(semilla + 5, k))
            val s = (8f + 10f * azar(semilla + 6, k)) * u
            drawArc(claro.copy(alpha = 0.14f * fuerza), 200f, 120f, false, o, Size(s, s * 0.5f), style = Stroke(0.7f * u))
            drawArc(oscuro.copy(alpha = 0.12f * fuerza), 200f, 120f, false, o + Offset(0f, 1f * u), Size(s, s * 0.5f), style = Stroke(0.6f * u))
        }
        // Donde se cayó el barro: el zarzo de debajo, las estacas y las varas que pasan una por delante y otra por detrás.
        val huecos = (r.width * r.height * caidos / (u * u * 7000f)).toInt().coerceIn(1, 5)
        for (k in 0 until huecos) {
            val c = Offset(r.left + r.width * (0.1f + 0.8f * azar(semilla + 8, k)), r.top + r.height * (0.18f + 0.64f * azar(semilla + 9, k)))
            val s = (13f + 9f * azar(semilla + 10, k)) * u
            val q = Rect(c.x - s, c.y - s * 0.6f, c.x + s, c.y + s * 0.6f)
            val hueco = contornoIrregular(q, semilla + 11 + k, 26, 0.22f)
            // El labio del barro: lo que queda alrededor del hueco, más claro abajo, donde le da la luz.
            translate(0.8f * u, 1.2f * u) { drawPath(hueco, claro.copy(alpha = 0.45f * fuerza), style = Stroke(2.4f * u)) }
            clipPath(hueco) {
                drawRect(mezcla(ZARZO, Color.Black, 0.72f), q.topLeft, q.size)
                val paso = 8f * u
                val x0 = q.left + paso * azar(semilla + 12, k) - paso
                var x = x0
                while (x < q.right + paso) {
                    drawLine(mezcla(ZARZO, Color.Black, 0.15f), Offset(x, q.top), Offset(x, q.bottom), 2.6f * u)
                    drawLine(mezcla(ZARZO, Color.White, 0.2f).copy(alpha = 0.5f), Offset(x - 0.7f * u, q.top), Offset(x - 0.7f * u, q.bottom), 0.5f * u)
                    x += paso
                }
                var y = q.top + 1.5f * u; var j = 0
                while (y < q.bottom + 2 * u) {
                    var xx = x0; var i = 0
                    while (xx < q.right + paso) {
                        val delante = (i + j) % 2 == 0
                        val curva = Path().apply { moveTo(xx, y); quadraticTo(xx + paso / 2, y + (if (delante) -0.9f else 0.9f) * u, xx + paso, y) }
                        if (delante) {
                            drawPath(curva, Color.Black.copy(alpha = 0.45f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                            drawPath(curva, mezcla(ZARZO, PAJA, 0.25f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
                            drawPath(curva, Color.White.copy(alpha = 0.25f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
                        } else drawPath(curva, mezcla(ZARZO, Color.Black, 0.35f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
                        xx += paso; i++
                    }
                    y += 2.8f * u; j++
                }
                // La sombra que echa el canto del barro sobre el zarzo.
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent), q.top, q.top + q.height * 0.55f), q.topLeft, q.size)
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), q.left, q.left + q.width * 0.3f), q.topLeft, q.size)
            }
            drawPath(hueco, oscuro.copy(alpha = 0.7f), style = Stroke(0.8f * u))
        }
    }

    /** Un cordel torcido a lo largo de [p]: el cuerpo, su sombra y las vueltas de la torsión. */
    private fun DrawScope.cordel(p: Path, color: Color, g: Float) {
        drawPath(p, mezcla(color, Color.Black, 0.45f), style = Stroke(g * 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, color, style = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(p, mezcla(color, Color.White, 0.35f), style = Stroke(g * 0.36f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.6f, g * 0.9f), 0f)))
    }

    private fun DrawScope.cordel(a: Offset, b: Offset, color: Color, g: Float) = cordel(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y) }, color, g)

    /** Una correa de cuero de [a] a [b], de ancho [g]: la sombra que echa, el cuero y sus dos cantos, el de arriba en luz. */
    private fun DrawScope.correa(a: Offset, b: Offset, color: Color, g: Float, sombra: Float = 0.35f) {
        val u = density
        val dx = b.x - a.x; val dy = b.y - a.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.01f)
        val n = Offset(-dy / l, dx / l)
        drawLine(Color.Black.copy(alpha = sombra), a + Offset(0.6f * u, 1.1f * u), b + Offset(0.6f * u, 1.1f * u), g)
        drawLine(color, a, b, g)
        drawLine(mezcla(color, Color.White, 0.28f).copy(alpha = 0.6f), a - n * (g * 0.38f), b - n * (g * 0.38f), 0.45f * u)
        drawLine(mezcla(color, Color.Black, 0.4f), a + n * (g * 0.42f), b + n * (g * 0.42f), 0.45f * u)
    }

    /**
     * La borla de lana colgando de [c]: el nudo y los hilos sueltos, del [color] del ánimo (si no lo hay, de lana cruda), de
     * largo [largo].
     */
    private fun DrawScope.borla(c: Offset, largo: Float, color: Color?, oscuro: Boolean, semilla: Int = 3) {
        val u = density
        val lana = (color ?: LINO).let { if (oscuro) mezcla(it, NOCHE, 0.35f) else it }
        val hondo = mezcla(lana, Color.Black, 0.35f)
        for (k in 0 until 7) {
            val dx = (k - 3) * largo * 0.07f
            val fin = Offset(c.x + dx * 1.6f + (azar(semilla, k) - 0.5f) * largo * 0.1f, c.y + largo * (0.82f + 0.18f * azar(semilla + 1, k)))
            drawLine(if (k % 2 == 0) lana else hondo, Offset(c.x + dx * 0.4f, c.y + largo * 0.18f), fin, maxOf(0.7f * u, largo * 0.07f), StrokeCap.Round)
        }
        drawOval(Color.Black.copy(alpha = 0.25f), Offset(c.x - largo * 0.16f, c.y + largo * 0.02f), Size(largo * 0.34f, largo * 0.26f))
        drawOval(lana, Offset(c.x - largo * 0.17f, c.y - largo * 0.04f), Size(largo * 0.34f, largo * 0.26f))
        drawLine(hondo, Offset(c.x - largo * 0.15f, c.y + largo * 0.12f), Offset(c.x + largo * 0.15f, c.y + largo * 0.12f), maxOf(0.6f * u, largo * 0.05f))
        drawCircle(Color.White.copy(alpha = 0.3f), largo * 0.05f, Offset(c.x - largo * 0.05f, c.y + largo * 0.02f))
    }

    /**
     * La onda de La Tène: un tallo que ondula de [x0] a [x1] a la altura [y], y en cada cresta un zarcillo que se enrosca hacia
     * adentro y acaba en botón. Con [hueco], deja libre ese ancho a cada lado del centro (para un rótulo o un emblema).
     */
    private fun DrawScope.onda(x0: Float, x1: Float, y: Float, alto: Float, color: Color, grosor: Float, hueco: Float = 0f) {
        if (x1 - x0 < alto * 2) return
        val paso = alto * 2.2f
        val cx = (x0 + x1) / 2
        val tallo = Path()
        val zarcillos = Path()
        var x = x0
        var arriba = true
        var empezado = false
        while (x + paso / 2 <= x1 + 0.5f) {
            val xm = x + paso / 2
            val libre = hueco <= 0f || (xm < cx - hueco || x > cx + hueco)
            if (libre) {
                val s = if (arriba) -1f else 1f
                if (!empezado) { tallo.moveTo(x, y); empezado = true }
                tallo.cubicTo(x + paso * 0.15f, y + s * alto * 0.5f, x + paso * 0.35f, y + s * alto * 0.5f, xm, y)
                // El zarcillo: sale de la cresta y se enrosca hacia la bahía.
                val p0 = Offset(x + paso * 0.25f, y + s * alto * 0.37f)
                zarcillos.moveTo(p0.x, p0.y)
                zarcillos.quadraticTo(p0.x + paso * 0.2f, p0.y + s * alto * 0.12f, p0.x + paso * 0.16f, p0.y - s * alto * 0.12f)
                zarcillos.quadraticTo(p0.x + paso * 0.1f, p0.y - s * alto * 0.26f, p0.x + paso * 0.03f, p0.y - s * alto * 0.14f)
                drawCircle(color, grosor * 0.95f, Offset(p0.x + paso * 0.03f, p0.y - s * alto * 0.14f))
            } else empezado = false
            x = xm; arriba = !arriba
        }
        drawPath(tallo, color, style = Stroke(grosor, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(zarcillos, color, style = Stroke(grosor * 0.75f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    /** Una ramita de muérdago ([TrazosGalia.muerdago]): [c] es la horquilla, [tam] su alto y [giro] en grados (0 = de pie). */
    private fun DrawScope.ramita(c: Offset, tam: Float, giro: Float, hoja: Color = MUERDAGO, baya: Color = BAYA) {
        with(TrazosGalia) { muerdago(c, tam, giro, hoja, baya) }
    }

    /** El ramo de muérdago colgado a secar de su cordel en [c]: tres ramitas cabeza abajo, abiertas en abanico. */
    private fun DrawScope.ramo(c: Offset, tam: Float, hoja: Color = MUERDAGO, baya: Color = BAYA) {
        listOf(-24f to -0.12f, 24f to 0.12f, 0f to 0f).forEach { (g, dx) ->
            ramita(Offset(c.x + dx * tam, c.y + tam * 0.48f), tam * 0.85f, 180f + g, hoja, baya)
        }
        drawCircle(mezcla(LINO, Color.Black, 0.2f), tam * 0.05f, c)
    }

    /**
     * La fíbula de bronce de La Tène, tendida de [a] (el resorte) hacia la derecha, de largo [largo]: el arco, el pie que vuelve
     * sobre el arco con su botón, el resorte en espiral y la aguja, que con [abierta] (0 a 1) se suelta y cae.
     */
    private fun DrawScope.fibula(a: Offset, largo: Float, color: Color, abierta: Float = 0f) {
        val L = largo
        val hondo = mezcla(color, Color.Black, 0.35f)
        rotate(38f * abierta.coerceIn(0f, 1f), a) {
            drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(L * 0.02f, L * 0.05f), a + Offset(L * 0.96f, L * 0.05f), L * 0.04f, StrokeCap.Round)
            drawLine(hondo, a, a + Offset(L * 0.95f, 0f), L * 0.035f, StrokeCap.Round)
        }
        val arco = Path().apply { moveTo(a.x, a.y); cubicTo(a.x + L * 0.18f, a.y - L * 0.42f, a.x + L * 0.6f, a.y - L * 0.4f, a.x + L * 0.86f, a.y - L * 0.03f) }
        translate(L * 0.02f, L * 0.05f) { drawPath(arco, Color.Black.copy(alpha = 0.3f), style = Stroke(L * 0.08f, cap = StrokeCap.Round)) }
        drawPath(arco, color, style = Stroke(L * 0.075f, cap = StrokeCap.Round))
        drawPath(arco, mezcla(color, Color.White, 0.45f).copy(alpha = 0.6f), style = Stroke(L * 0.02f, cap = StrokeCap.Round))
        // El pie que vuelve sobre el arco y el botón donde se apoya.
        val pie = Path().apply {
            moveTo(a.x + L * 0.86f, a.y - L * 0.03f)
            quadraticTo(a.x + L * 0.98f, a.y + L * 0.06f, a.x + L * 0.9f, a.y + L * 0.05f)
            quadraticTo(a.x + L * 0.76f, a.y + L * 0.02f, a.x + L * 0.64f, a.y - L * 0.2f)
        }
        drawPath(pie, color, style = Stroke(L * 0.045f, cap = StrokeCap.Round))
        drawCircle(color, L * 0.06f, Offset(a.x + L * 0.63f, a.y - L * 0.23f))
        drawCircle(mezcla(color, Color.White, 0.5f).copy(alpha = 0.6f), L * 0.02f, Offset(a.x + L * 0.615f, a.y - L * 0.245f))
        // El resorte: cuatro vueltas de alambre.
        for (k in 0 until 4) {
            val o = Offset(a.x - L * 0.02f + k * L * 0.025f, a.y)
            drawCircle(hondo, L * 0.06f, o, style = Stroke(L * 0.022f))
        }
        drawCircle(color, L * 0.045f, Offset(a.x + L * 0.04f, a.y - L * 0.01f), style = Stroke(L * 0.02f))
    }

    /** El estátero de oro con el caballo, de radio [r]: el disco grueso y abombado, su canto oscuro y el caballo en relieve. */
    private fun DrawScope.moneda(c: Offset, r: Float, oscuro: Boolean = false) {
        val oro = if (oscuro) mezcla(ORO, FUEGO, 0.25f) else ORO
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.1f, r * 0.14f))
        drawCircle(Brush.radialGradient(listOf(ORO_BRILLO, oro, mezcla(oro, Color.Black, 0.35f)), center = c - Offset(r * 0.35f, r * 0.4f), radius = r * 1.5f), r, c)
        drawCircle(mezcla(oro, Color.Black, 0.4f), r, c, style = Stroke(r * 0.07f))
        caballoGalia(c + Offset(r * 0.03f, r * 0.04f), r * 0.74f, ORO_BRILLO.copy(alpha = 0.7f))
        caballoGalia(c, r * 0.74f, mezcla(oro, Color.Black, 0.45f))
    }

    /** El torques de las figuras compartidas ([TrazosGalia.torques]). */
    private fun DrawScope.torq(c: Offset, r: Float, grueso: Float, oro: Color = ORO, brillo: Color = ORO_BRILLO) {
        with(TrazosGalia) { torques(c, r, grueso, oro, brillo) }
    }

    /** La hoja de roble de las figuras compartidas ([TrazosGalia.hojaRoble]). */
    private fun DrawScope.hoja(c: Offset, largo: Float, angulo: Float, color: Color = ROBLE, nervio: Color = ROBLE_SOMBRA) {
        with(TrazosGalia) { hojaRoble(c, largo, angulo, color, nervio) }
    }

    /** La rueda de bronce de los amuletos y de las monedas ([TrazosGalia.rueda]), con su sombra y el brillo de la llanta. */
    private fun DrawScope.ruedita(c: Offset, r: Float, color: Color) {
        with(TrazosGalia) {
            rueda(c + Offset(r * 0.12f, r * 0.16f), r, Color.Black.copy(alpha = 0.3f), Color.Transparent)
            rueda(c, r, color, Color.Transparent)
        }
        drawCircle(mezcla(color, Color.White, 0.45f).copy(alpha = 0.5f), r * 0.84f, c, style = Stroke(r * 0.06f))
    }

    /**
     * Una tablilla encerada dentro de [r]: el marco de roble ([marco], de ancho [borde]) con su veta y, hundida en él, la cera
     * ([cera]) con el filo en sombra arriba y a la izquierda y las marcas que dejó la espátula al alisarla. Devuelve la cera.
     */
    private fun DrawScope.tablilla(r: Rect, marco: Color, cera: Color, semilla: Int, borde: Float, sombra: Float = 0.3f): Rect {
        val u = density
        val rad = minOf(2.2f * u, borde * 0.6f)
        if (sombra > 0f) drawRoundRect(Color.Black.copy(alpha = sombra), r.topLeft + Offset(0.8f * u, 1.4f * u), r.size, CornerRadius(rad))
        clipPath(Path().apply { addRoundRect(RoundRect(r, CornerRadius(rad))) }) { madera(r, marco, semilla, vertical = r.height > r.width, nudos = 0) }
        drawRoundRect(mezcla(marco, Color.White, 0.25f).copy(alpha = 0.5f), r.topLeft + Offset(0.4f * u, 0.4f * u), Size(r.width - 0.8f * u, r.height - 0.8f * u), CornerRadius(rad), style = Stroke(0.6f * u))
        drawRoundRect(Color.Black.copy(alpha = 0.35f), r.topLeft, r.size, CornerRadius(rad), style = Stroke(0.5f * u))
        val q = r.deflate(borde)
        if (q.width <= 0f || q.height <= 0f) return q
        drawRect(Brush.verticalGradient(listOf(mezcla(cera, CERA_OSC, 0.25f), cera, mezcla(cera, CERA_OSC, 0.3f)), q.top, q.bottom), q.topLeft, q.size)
        clipRect(q.left, q.top, q.right, q.bottom) {
            // Las pasadas de la espátula y el brillo de la cera.
            for (k in 0 until 3) {
                val y = q.top + q.height * (0.2f + 0.3f * k + 0.1f * azar(semilla, k))
                drawLine(Color.White.copy(alpha = 0.1f), Offset(q.left, y), Offset(q.right, y + 2 * u * (azar(semilla + 1, k) - 0.5f)), 1.6f * u)
            }
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), q.left, q.left + 3 * u), q.topLeft, Size(3 * u, q.height))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.26f), Color.Transparent), q.top, q.top + 3 * u), q.topLeft, Size(q.width, 3 * u))
        }
        drawLine(Color.White.copy(alpha = 0.22f), Offset(q.left, q.bottom), Offset(q.right, q.bottom), 0.6f * u)
        drawLine(Color.White.copy(alpha = 0.16f), Offset(q.right, q.top), Offset(q.right, q.bottom), 0.6f * u)
        return q
    }

    /**
     * Lo de todos los días, rayado en la cera con letras griegas, como escribían los galos (César 6.14): renglones de
     * mayúsculas sin acentos, de alto [alto], con sus espacios. Es la textura de lo escrito, no se lee.
     */
    private fun DrawScope.letrasGriegas(r: Rect, color: Color, semilla: Int, alto: Float, g: Float) {
        val ancho = alto * 0.9f
        var y = r.top + alto * 0.3f; var k = 0
        while (y + alto <= r.bottom) {
            var x = r.left
            val fin = r.right - r.width * 0.25f * azar(semilla, k)
            var j = 0
            while (x + ancho <= fin) {
                val q = Rect(x, y + alto * 0.1f * (azar(semilla + 3, j + k) - 0.5f), x + ancho * 0.7f, y + alto)
                letraGriega(Math.floorMod((azar(semilla + k * 7, j) * 10).toInt(), 10), q, color, g)
                x += ancho
                if (azar(semilla + 11 + k, j) > 0.8f) x += ancho * 0.9f
                j++
            }
            y += alto * 1.9f; k++
        }
    }

    /** Una mayúscula griega rayada dentro de [q], según [tipo]: Λ, Δ, Π, Θ, Ξ, Γ, Φ, Ϲ (la sigma de luna), Ο o Η. */
    private fun DrawScope.letraGriega(tipo: Int, q: Rect, color: Color, g: Float) {
        val l = q.left; val t = q.top; val d = q.right; val b = q.bottom; val cx = q.center.x; val cy = q.center.y
        fun ln(x0: Float, y0: Float, x1: Float, y1: Float) = drawLine(color, Offset(x0, y0), Offset(x1, y1), g, StrokeCap.Round)
        when (tipo) {
            0 -> { ln(l, b, cx, t); ln(cx, t, d, b) }
            1 -> { ln(l, b, cx, t); ln(cx, t, d, b); ln(l, b, d, b) }
            2 -> { ln(l, t, d, t); ln(l + q.width * 0.12f, t, l + q.width * 0.12f, b); ln(d - q.width * 0.12f, t, d - q.width * 0.12f, b) }
            3 -> { drawOval(color, q.topLeft, q.size, style = Stroke(g)); ln(l + q.width * 0.25f, cy, d - q.width * 0.25f, cy) }
            4 -> { ln(l, t, d, t); ln(l + q.width * 0.15f, cy, d - q.width * 0.15f, cy); ln(l, b, d, b) }
            5 -> { ln(l + q.width * 0.2f, b, l + q.width * 0.2f, t); ln(l + q.width * 0.2f, t, d, t) }
            6 -> { drawOval(color, Offset(l, t + q.height * 0.2f), Size(q.width, q.height * 0.6f), style = Stroke(g)); ln(cx, t, cx, b) }
            7 -> drawArc(color, 50f, 260f, false, q.topLeft, q.size, style = Stroke(g, cap = StrokeCap.Round))
            8 -> drawOval(color, q.topLeft, q.size, style = Stroke(g))
            else -> { ln(l, t, l, b); ln(d, t, d, b); ln(l, cy, d, cy) }
        }
    }

    /** La tea de pino en su soporte de hierro clavado en la pared, en [c] (la boca del soporte): encendida, con su llama y el humo. */
    private fun DrawScope.tea(c: Offset, largo: Float, encendida: Boolean, oscuro: Boolean, noche: Boolean, llama: Float = 1f) {
        val u = density
        val hierro = luz(HIERRO, oscuro, noche, if (encendida) 0.9f else 0.2f)
        drawLine(hierro, Offset(c.x + largo * 0.32f, c.y + largo * 0.1f), Offset(c.x, c.y), 1.6f * u, StrokeCap.Round)
        drawRect(hierro, Offset(c.x + largo * 0.3f, c.y + largo * 0.02f), Size(2.4f * u, largo * 0.18f))
        val palo0 = Offset(c.x + largo * 0.08f, c.y + largo * 0.5f)
        val palo1 = Offset(c.x - largo * 0.08f, c.y - largo * 0.32f)
        drawLine(luz(MADERA, oscuro, noche, if (encendida) 0.85f else 0.2f), palo0, palo1, 2.6f * u, StrokeCap.Round)
        drawCircle(hierro, 2.2f * u, c, style = Stroke(1.2f * u))
        if (encendida) {
            val f = palo1 + Offset(-0.5f * u, -1 * u)
            mancha(f, largo * 1.2f, FUEGO_HALO.copy(alpha = 0.32f))
            val alto = largo * (0.45f + 0.1f * llama)
            val p = Path().apply {
                moveTo(f.x - 3.2f * u, f.y + 1 * u)
                quadraticTo(f.x - 3.6f * u, f.y - alto * 0.5f, f.x + 0.4f * u, f.y - alto)
                quadraticTo(f.x + 3.8f * u, f.y - alto * 0.45f, f.x + 3 * u, f.y + 1 * u)
                close()
            }
            drawPath(p, FUEGO)
            drawPath(Path().apply { moveTo(f.x - 1.4f * u, f.y); quadraticTo(f.x - 1.4f * u, f.y - alto * 0.35f, f.x + 0.2f * u, f.y - alto * 0.55f); quadraticTo(f.x + 1.6f * u, f.y - alto * 0.3f, f.x + 1.3f * u, f.y); close() }, FUEGO_HALO)
        } else {
            drawCircle(Color.Black.copy(alpha = 0.55f), 1.8f * u, palo1)
        }
    }

    /** La cesta de mimbre parada en [pie], de ancho [ancho] y alto [alto]: el tejido de varas, el borde trenzado y, si hay, las manzanas dentro. */
    private fun DrawScope.cesta(pie: Offset, ancho: Float, alto: Float, mimbre: Color, dentro: Color?) {
        val u = density
        val forma = Path().apply {
            moveTo(pie.x - ancho * 0.4f, pie.y); lineTo(pie.x - ancho * 0.5f, pie.y - alto); lineTo(pie.x + ancho * 0.5f, pie.y - alto); lineTo(pie.x + ancho * 0.4f, pie.y); close()
        }
        drawOval(Color.Black.copy(alpha = 0.32f), Offset(pie.x - ancho * 0.5f, pie.y - 2.5f * u), Size(ancho, 5 * u))
        if (dentro != null) for (k in 0 until 3) {
            val c = Offset(pie.x + ancho * (-0.24f + 0.24f * k), pie.y - alto - ancho * 0.06f + (if (k == 1) -ancho * 0.05f else 0f))
            drawCircle(dentro, ancho * 0.13f, c)
            drawCircle(Color.White.copy(alpha = 0.25f), ancho * 0.04f, c - Offset(ancho * 0.04f, ancho * 0.04f))
        }
        drawPath(forma, mezcla(mimbre, Color.Black, 0.55f))
        clipPath(forma) {
            val fila = 3.4f * u
            val paso = 5.5f * u
            var y = pie.y - alto + 3 * u; var j = 0
            while (y < pie.y + fila) {
                var x = pie.x - ancho * 0.55f + (j % 2) * paso / 2
                while (x < pie.x + ancho * 0.55f) {
                    drawOval(mezcla(mimbre, Color.Black, 0.12f), Offset(x, y - fila * 0.5f), Size(paso * 0.92f, fila * 0.9f))
                    drawOval(Color.White.copy(alpha = 0.2f), Offset(x + paso * 0.15f, y - fila * 0.42f), Size(paso * 0.6f, fila * 0.3f))
                    x += paso
                }
                y += fila; j++
            }
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.White.copy(alpha = 0.08f), Color.Transparent, Color.Black.copy(alpha = 0.4f)), pie.x - ancho / 2, pie.x + ancho / 2),
                Offset(pie.x - ancho / 2, pie.y - alto), Size(ancho, alto))
        }
        // El borde trenzado.
        val y0 = pie.y - alto
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(pie.x - ancho * 0.53f, y0 + 1.4f * u), Offset(pie.x + ancho * 0.53f, y0 + 1.4f * u), 3.6f * u, StrokeCap.Round)
        drawLine(mezcla(mimbre, Color.Black, 0.1f), Offset(pie.x - ancho * 0.53f, y0), Offset(pie.x + ancho * 0.53f, y0), 3.4f * u, StrokeCap.Round)
        var x = pie.x - ancho * 0.52f
        while (x < pie.x + ancho * 0.52f) { drawLine(mezcla(mimbre, Color.White, 0.25f), Offset(x, y0 + 1.4f * u), Offset(x + 2 * u, y0 - 1.4f * u), 1f * u, StrokeCap.Round); x += 2.6f * u }
    }

    // =================================================================
    //  El estante y las entradas: el estante de las cuentas de la casa grande (`MuebleEstanteGalia.kt`)
    // =================================================================

    override val corona = MuebleEstanteGalia.CORONA
    override val base = MuebleEstanteGalia.BASE
    override val lado = MuebleEstanteGalia.LADO
    override val altoRepisa: Dp = MuebleEstanteGalia.ALTO_REPISA
    override val tabla: Dp = MuebleEstanteGalia.TABLA
    override val separa: Dp = MuebleEstanteGalia.SEPARA
    override val letra: FontFamily = TITULO
    /** Sin aura en reposo: el estante rehecho tiene su propia luz (MUEBLES.md, versión 2). */
    override val auraEnReposo = false

    override fun ancho(palabras: Int): Dp = MuebleEstanteGalia.ancho(palabras)
    override fun alto(semilla: Int, palabras: Int): Float = MuebleEstanteGalia.alto(semilla, palabras)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) =
        with(MuebleEstanteGalia) { estante(d, medidor, oscuro, noche) }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleEstanteGalia) { volumen(v, medidor, oscuro) }

    // =================================================================
    //  La tapa: la tablilla del día, cerrada sobre la mesa
    // =================================================================

    override val altoTapa: Dp = 200.dp
    override val cajaTapa = Rect(0.1f, 0.34f, 0.64f, 0.71f)
    override val letraTapa: FontFamily = TEXTO
    override val tamTapa = 12.5f
    override val lineasTapa = 4
    override val centradoTapa = false
    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = tinta(oscuro)
    override fun palabrasTapa(n: Int): String = if (n == 1) "1 palabra en la cera" else "$n palabras en la cera"

    /**
     * La tablilla del día, cerrada: detrás, los cantos de las otras tablillas del haz; delante, el marco de roble con la cera
     * hundida, donde va la fecha del tema, una onda de La Tène y lo escrito; abajo, cuántas palabras hay. A la izquierda, las
     * correas de las bisagras; a la derecha, la correa que ciñe el haz de arriba abajo, cerrada con una fíbula de bronce, y el
     * estátero de oro con el caballo, metido en la cera. Al abrirse, la fíbula suelta la aguja y la correa se va; luego la
     * tablilla gira sobre sus bisagras y deja ver la de adentro.
     *
     * Descansa en la mesa de roble (su tablero detrás y su canto abajo), con la sombra con color del lado contrario a la luz. De
     * día la luz viene de la puerta, arriba a la izquierda: filete en los cantos que la miran y sombra propia teñida. De noche
     * arde la lamparilla de piedra a la derecha: ese lado cálido, el otro con el filo frío de la luna y la mesa lejos de la
     * llama hundida en el pardo de la casa. En el sueño manda la luna: la lamparilla arde baja y la sombra se va, azul, abajo a
     * la derecha.
     */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = luz(x, oscuro, e.noche, 0.5f)
        val a = e.abierto.coerceIn(0f, 1f)
        val aSol = (a / 0.35f).coerceIn(0f, 1f)
        val aTapa = ((a - 0.3f) / 0.7f).coerceIn(0f, 1f)
        val cuerpo = Rect(w * 0.02f, h * 0.05f, w * 0.92f, h * 0.94f)
        val x0 = cuerpo.left + w * 0.03f
        val cara = Rect(x0, cuerpo.top, cuerpo.right, cuerpo.bottom)
        val rad = 3 * u
        val marco = c(mezcla(TABLA, VETA, 0.12f))
        val fondo = cera(oscuro, e.noche, 0.55f)
        val lumbre = oscuro && !e.noche
        mesaTapa(w, h, oscuro, e.noche)
        // La sombra arrojada, con color: de día y en el sueño, abajo a la derecha (violeta; azul con la luna); de noche, a la
        // izquierda, lejos de la lamparilla.
        val sombraTapa = when { e.noche -> Color(0xFF16203A).copy(alpha = if (oscuro) 0.6f else 0.42f); lumbre -> Color(0xFF140805).copy(alpha = 0.62f); else -> Color(0xFF3A2438).copy(alpha = 0.32f) }
        val corrida = if (lumbre) Offset(-7 * u, 4 * u) else Offset(5 * u, 6 * u)
        drawRoundRect(sombraTapa, cara.topLeft + corrida, cara.size, CornerRadius(rad))

        for (j in 3 downTo 1) {
            val col = c(mezcla(TABLA, VETA, 0.15f + 0.1f * j))
            drawRoundRect(col, Offset(cara.left + j * 1.1f * u, cara.top + j * 1.4f * u), cara.size, CornerRadius(rad))
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(cara.left + j * 1.1f * u, cara.top + j * 1.4f * u), cara.size, CornerRadius(rad), style = Stroke(0.5f * u))
        }
        // Debajo: la tablilla de adentro.
        if (aTapa > 0.01f) interior(cara, e, medidor, oscuro)
        val ang = aTapa * 1.45f
        val co = cos(ang); val si = sin(ang)
        if (aTapa > 0.01f) drawRect(Color.Black.copy(alpha = 0.3f * si), Offset(x0 + cara.width * co, cara.top), Size(14 * u * si, cara.height))
        val xs = cara.right - cara.width * 0.2f
        afin(co, -si * 0.1f, 0f, 1f, x0 - x0 * co, x0 * si * 0.1f) {
            val q = tablilla(cara, marco, fondo, e.semilla, 11 * u, 0f)
            // La fecha del tema, arriba, y la onda que la subraya.
            enDos(medidor, e.fecha, Rect(q.left + 8 * u, q.top + 3 * u, xs - 16 * u, h * 0.255f), TITULO, tinta(oscuro), 14f, peso = GRUESO, k = 0.72f, color2 = oro(oscuro))
            val yd = h * 0.285f
            onda(q.left + 8 * u, xs - 16 * u, yd, 7 * u, oro(oscuro).copy(alpha = 0.85f), 1f * u)
            // De noche, la media luna rayada en la cera, junto a la fecha.
            if (e.noche) {
                val l = Offset(xs - 22 * u, q.top + 12 * u)
                drawCircle(tinta(oscuro).copy(alpha = 0.6f), 5 * u, l, style = Stroke(0.9f * u))
                drawCircle(fondo, 5 * u, l + Offset(2.4f * u, -1.2f * u))
            }
            // Las palabras, rayadas al pie.
            val palabras = if (e.vacio) "cera lisa" else palabrasTapa(e.palabras)
            aIzquierda(medidor, palabras, Rect(q.left + 8 * u, h * 0.76f, xs - 16 * u, h * 0.86f), ROTULO, tenue(oscuro), 10f, peso = FontWeight.SemiBold, minimo = 6f)
            // El estátero metido en la cera, abajo a la derecha.
            moneda(Offset(xs + (q.right - xs) * 0.5f + 2 * u, q.bottom - 14 * u), 9.5f * u, oscuro)
            // Las bisagras, a la izquierda: un tiento de cuero que pasa por dos agujeros y da la vuelta al canto.
            listOf(0.27f, 0.73f).forEach { f ->
                val y = cara.top + cara.height * f
                val a1 = Offset(x0 + 5.5f * u, y - 4 * u); val a2 = Offset(x0 + 5.5f * u, y + 4 * u)
                listOf(a1, a2).forEach { o -> drawCircle(Color.Black.copy(alpha = 0.55f), 1.7f * u, o) }
                val lazo = Path().apply { moveTo(a1.x, a1.y); quadraticTo(x0 - 7 * u, y - 6 * u, x0 - 7 * u, y); quadraticTo(x0 - 7 * u, y + 6 * u, a2.x, a2.y) }
                drawPath(lazo, Color.Black.copy(alpha = 0.3f), style = Stroke(2.8f * u, cap = StrokeCap.Round))
                drawPath(lazo, c(CORREA), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                drawPath(lazo, mezcla(c(CORREA), Color.White, 0.3f).copy(alpha = 0.5f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
            }
            if (aTapa > 0f) drawRoundRect(Color.Black.copy(alpha = 0.45f * (1f - co)), cara.topLeft, cara.size, CornerRadius(rad))
            if (aTapa < 0.05f) cantosTapa(cara, oscuro, e.noche)
        }
        // La correa que ciñe el haz de arriba abajo, con la fíbula; al soltarse, se va hacia la derecha.
        if (aTapa < 0.05f) {
            val alfa = (1f - aSol * 1.1f).coerceIn(0f, 1f)
            val dx = 26 * u * aSol
            if (alfa > 0f) {
                val cuero = c(CORREA).copy(alpha = alfa)
                val xc = xs + dx
                correa(Offset(xc, cuerpo.top - 4 * u), Offset(xc + 0.6f * u, cuerpo.bottom + 4 * u), cuero, 9 * u, 0.35f * alfa)
                // Los pespuntes del canto de la correa.
                listOf(-3.2f, 3.2f).forEach { s ->
                    drawLine(mezcla(cuero, Color.White, 0.3f).copy(alpha = 0.5f * alfa), Offset(xc + s * u, cuerpo.top - 2 * u), Offset(xc + s * u + 0.6f * u, cuerpo.bottom + 2 * u), 0.5f * u,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u), 0f))
                }
                val fc = Offset(xc - 14 * u, cuerpo.center.y + 12 * u)
                rotate(-32f, fc) { fibula(fc, 34 * u, c(BRONCE).copy(alpha = alfa), aSol) }
            }
        }
        // La lamparilla de piedra en la mesa, a la derecha: de día apagada; de noche, encendida; en el sueño, ardiendo baja.
        lamparillaTapa(Offset(w * 0.958f, h * 0.9f), h * 0.055f, oscuro, e.noche)
    }

    /**
     * La mesa donde descansa la tablilla: la franja del tablero de roble que asoma abajo y el canto de delante con su arista, que
     * se pierden hacia los lados. De día, más vivo del lado de la puerta; de noche, encendido junto a la lamparilla y hundido
     * lejos; con la luna, frío.
     */
    private fun DrawScope.mesaTapa(w: Float, h: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        val top = Rect(0f, h * 0.82f, w, h * 0.96f)
        val roble = mezcla(TABLA, VETA, 0.45f)
        val (izq, der) = when {
            oscuro && !noche -> luz(roble, true, false, 0.08f) to luz(roble, true, false, 0.95f)
            noche -> mezcla(t(roble, oscuro, true), Color(0xFF2A3A5C), if (oscuro) 0.3f else 0.22f) to t(roble, oscuro, true)
            else -> mezcla(roble, Color(0xFFF0D29C), 0.18f) to mezcla(roble, Color(0xFF3A2438), 0.18f)
        }
        // Los dos extremos se pierden (el tablero sigue fuera del cuadro, en la sombra del cuarto).
        fun tramo(a: Color, b: Color) = Brush.horizontalGradient(0f to a.copy(alpha = 0f), 0.07f to a, 0.93f to b, 1f to b.copy(alpha = 0f), startX = 0f, endX = w)
        drawRect(tramo(izq, der), top.topLeft, top.size)
        drawRect(tramo(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.2f), Color.Black.copy(alpha = if (oscuro) 0.35f else 0.2f)), top.topLeft, Size(w, 4 * u))
        val canto = Rect(0f, h * 0.96f, w, h)
        drawRect(tramo(mezcla(izq, Color.Black, 0.35f), mezcla(der, Color.Black, 0.35f)), canto.topLeft, canto.size)
        val arista = when { oscuro && !noche -> FUEGO.copy(alpha = 0.5f); noche -> LUNA.copy(alpha = 0.4f); else -> Color(0xFFFFF0C8).copy(alpha = 0.55f) }
        drawRect(if (oscuro && !noche) tramo(arista.copy(alpha = 0.05f), arista) else tramo(arista, arista.copy(alpha = 0.1f)), canto.topLeft, Size(w, 1.2f * u))
    }

    /**
     * Los cantos de la tablilla según la luz: de día, el filete claro arriba y a la izquierda y la sombra propia teñida abajo y a
     * la derecha; de noche, el filo de la lamparilla a la derecha y el de la luna, frío, a la izquierda; en el sueño, la luna
     * arriba y a la izquierda.
     */
    private fun DrawScope.cantosTapa(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        val g = 1.3f * u
        when {
            oscuro && !noche -> {
                drawRect(Brush.verticalGradient(listOf(FUEGO.copy(alpha = 0.25f), FUEGO.copy(alpha = 0.7f)), r.top, r.bottom), Offset(r.right - g, r.top + 2 * u), Size(g, r.height - 4 * u))
                drawRect(Brush.horizontalGradient(listOf(FUEGO.copy(alpha = 0f), FUEGO.copy(alpha = 0.5f)), r.left + r.width * 0.5f, r.right), Offset(r.left + r.width * 0.5f, r.bottom - g), Size(r.width * 0.5f, g))
                drawRect(LUNA.copy(alpha = 0.3f), Offset(r.left, r.top + 3 * u), Size(1f * u, r.height - 6 * u))
            }
            noche -> {
                val fria = LUNA.copy(alpha = if (oscuro) 0.6f else 0.5f)
                drawRect(fria, Offset(r.left + 2 * u, r.top), Size(r.width - 4 * u, g))
                drawRect(fria, Offset(r.left, r.top + 2 * u), Size(g, r.height - 4 * u))
                drawRect(Color(0xFF16203A).copy(alpha = 0.35f), Offset(r.right - 2.2f * u, r.top + 2 * u), Size(2.2f * u, r.height - 4 * u))
            }
            else -> {
                drawRect(Color(0xFFFFF0C8).copy(alpha = 0.55f), Offset(r.left + 2 * u, r.top), Size(r.width - 4 * u, g))
                drawRect(Color(0xFFFFF0C8).copy(alpha = 0.4f), Offset(r.left, r.top + 2 * u), Size(g, r.height - 4 * u))
                drawRect(Color(0xFF3A2438).copy(alpha = 0.3f), Offset(r.right - 2.2f * u, r.top + 2 * u), Size(2.2f * u, r.height - 4 * u))
                drawRect(Color(0xFF3A2438).copy(alpha = 0.26f), Offset(r.left + 2 * u, r.bottom - 2.2f * u), Size(r.width - 4 * u, 2.2f * u))
            }
        }
    }

    /** La lamparilla de piedra con su pocillo de sebo y la mecha en el pico; [noche] (el sueño), ardiendo baja. */
    private fun DrawScope.lamparillaTapa(c: Offset, r: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        val arde = oscuro || noche
        val piedra = if (oscuro && !noche) luz(Color(0xFF8E8070), true, false, 0.85f) else t(Color(0xFF8E8070), oscuro, noche)
        if (arde) mancha(c + Offset(-r * 0.6f, -r * 0.6f), r * (if (noche) 3.2f else 5f), FUEGO_HALO.copy(alpha = if (noche) 0.16f else 0.26f))
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - r * 1.05f + (if (arde) -1.5f else 1.5f) * u, c.y - r * 0.05f), Size(r * 2.1f, r * 0.7f))
        drawOval(mezcla(piedra, Color.Black, 0.3f), Offset(c.x - r, c.y - r * 0.55f), Size(r * 2f, r * 0.95f))
        drawOval(piedra, Offset(c.x - r, c.y - r * 0.8f), Size(r * 2f, r * 0.8f))
        drawOval(mezcla(piedra, Color.Black, 0.45f), Offset(c.x - r * 0.72f, c.y - r * 0.66f), Size(r * 1.44f, r * 0.52f))
        drawOval(if (arde) mezcla(Color(0xFFE6D4AE), FUEGO, 0.4f) else Color(0xFFCFC0A0), Offset(c.x - r * 0.62f, c.y - r * 0.56f), Size(r * 1.24f, r * 0.4f))
        val pico = Offset(c.x - r * 1.05f, c.y - r * 0.45f)
        drawLine(Color(0xFF3A2E22), Offset(c.x - r * 0.4f, c.y - r * 0.4f), pico, 1.2f * u, StrokeCap.Round)
        if (!arde) return
        val alto = r * (if (noche) 0.8f else 1.4f)
        val llama = Path().apply {
            moveTo(pico.x, pico.y - alto); cubicTo(pico.x + r * 0.35f, pico.y - alto * 0.35f, pico.x + r * 0.3f, pico.y, pico.x, pico.y + 0.5f * u)
            cubicTo(pico.x - r * 0.3f, pico.y, pico.x - r * 0.35f, pico.y - alto * 0.35f, pico.x, pico.y - alto); close()
        }
        drawPath(llama, Color(0xFFFFB04A))
        drawCircle(FUEGO, r * 0.16f, Offset(pico.x, pico.y - alto * 0.25f))
    }

    /**
     * La tablilla de adentro, la que se ve al abrir: en la columna de la izquierda, lo escrito con el estilo; la de la derecha
     * se queda lisa a propósito, con una ramita de muérdago y una línea al pie: lo sagrado no se escribe (César 6.14).
     */
    private fun DrawScope.interior(r: Rect, e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = luz(x, oscuro, e.noche, 0.55f)
        val fondo = cera(oscuro, e.noche, 0.6f)
        val q = tablilla(r, c(mezcla(TABLA, VETA, 0.3f)), fondo, e.semilla + 9, 11 * u, 0f)
        val medio = q.left + q.width * 0.55f
        letrasGriegas(Rect(q.left + 8 * u, q.top + 5 * u, medio - 8 * u, q.bottom - 4 * u), tinta(oscuro).copy(alpha = 0.42f), e.semilla, 4.6f * u, 0.7f * u)
        drawLine(Color.Black.copy(alpha = 0.18f), Offset(medio, q.top + 6 * u), Offset(medio, q.bottom - 6 * u), 0.7f * u)
        drawLine(Color.White.copy(alpha = 0.18f), Offset(medio + 0.8f * u, q.top + 6 * u), Offset(medio + 0.8f * u, q.bottom - 6 * u), 0.5f * u)
        val lisa = Rect(medio + 8 * u, q.top + 4 * u, q.right - 8 * u, q.bottom - 4 * u)
        val tm = minOf(lisa.height * 0.5f, lisa.width * 0.6f)
        ramita(Offset(lisa.center.x, lisa.top + tm * 0.62f), tm, 0f, c(MUERDAGO), c(BAYA))
        textoEn(medidor, "lo que no se escribe,", Rect(lisa.left, lisa.bottom - 26 * u, lisa.right, lisa.bottom - 14 * u), TEXTO, tenue(oscuro), 9f, minimo = 5f)
        textoEn(medidor, "se recuerda", Rect(lisa.left, lisa.bottom - 14 * u, lisa.right, lisa.bottom - 2 * u), TEXTO, tenue(oscuro), 9f, minimo = 5f)
    }

    // =================================================================
    //  La página abierta y el escritor
    // =================================================================

    override val altoEncabezado: Dp = 118.dp

    /**
     * La cabecera de la tablilla: arriba y abajo, la onda de La Tène; a la izquierda, el torques de oro con la noche dentro; a la
     * derecha, el estátero con el caballo y, debajo, la marca del mes (MAT o ANM, como en el bronce). Al centro, el rótulo, la
     * fecha del tema (la noche arriba, en grande; el mes de Coligny abajo) y el detalle.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta; val acento = p.acento
        onda(4 * u, w - 4 * u, 8 * u, 6 * u, acento.copy(alpha = 0.7f), 0.9f * u, hueco = 12 * u)
        drawCircle(acento, 2.2f * u, Offset(w / 2, 8 * u))
        drawCircle(acento, 2.2f * u, Offset(w / 2, 8 * u), style = Stroke(0.6f * u))
        // El torques con la noche dentro.
        val tc = Offset(32 * u, 60 * u)
        val oroT = if (p.oscura) mezcla(ORO, FUEGO, 0.3f) else ORO
        torq(tc, 21 * u, 4.4f * u, oroT, ORO_BRILLO)
        textoEn(medidor, "${e.numero}", Rect(tc.x - 15 * u, tc.y - 10 * u, tc.x + 15 * u, tc.y + 10 * u), ROTULO, tinta, 13f, peso = FontWeight.SemiBold, minimo = 6f)
        // El estátero con el caballo y la marca del mes.
        val mc = Offset(w - 32 * u, 54 * u)
        moneda(mc, 20 * u, p.oscura)
        val marca = when { e.fecha.contains("ANM") -> "ANM"; e.fecha.contains("MAT") -> "MAT"; else -> "" }
        if (marca.isNotEmpty()) textoEn(medidor, marca, Rect(mc.x - 20 * u, mc.y + 23 * u, mc.x + 20 * u, mc.y + 35 * u), ROTULO, acento, 9f, peso = FontWeight.SemiBold, espaciado = 1.2f, minimo = 5f)
        // El rótulo, la fecha y el detalle.
        val x0 = 64 * u; val x1 = w - 64 * u
        textoEn(medidor, e.rotulo, Rect(x0, 17 * u, x1, 32 * u), ROTULO, acento, 10f, peso = FontWeight.SemiBold, espaciado = 0.5f, minimo = 6f)
        enDos(medidor, e.fecha, Rect(x0, 34 * u, x1, 78 * u), TITULO, tinta, 18f, peso = GRUESO, k = 0.62f, color2 = acento)
        textoEn(medidor, e.detalle, Rect(x0, 80 * u, x1, 95 * u), TEXTO, p.tenue, 10.5f, minimo = 6f)
        onda(x0, x1, 104 * u, 4.4f * u, acento.copy(alpha = 0.5f), 0.7f * u, hueco = 8 * u)
        hoja(Offset(w / 2, 104 * u), 9 * u, 90f, acento.copy(alpha = 0.7f), acento.copy(alpha = 0.3f))
        drawLine(tinta.copy(alpha = 0.18f), Offset(4 * u, h - 4 * u), Offset(w - 4 * u, h - 4 * u), 0.6f * u)
    }

    /**
     * Sobre la cera de la tablilla: el rastro de lo que se escribió antes y se alisó con la espátula, las muescas de las noches
     * contadas en la esquina, la hoja de roble como marca de agua y, al pie, la onda de La Tène con un torques chico al centro.
     * En modo oscuro, el fuego del hogar alumbra desde abajo a la izquierda y las orillas se van al verde negro del robledal. De
     * noche, la luna del sexto día (la de Plinio) asoma en la esquina y una ramita de muérdago cuelga del margen.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tinta = p.tinta
        // El rastro de lo escrito y alisado: trazos tenues, a medio borrar.
        for (k in 0 until 9) {
            val y = h * (0.18f + 0.09f * k) + 6 * u * azar(71, k)
            val x0 = w * (0.12f + 0.3f * azar(72, k))
            val l = w * (0.18f + 0.3f * azar(73, k))
            drawLine(tinta.copy(alpha = 0.035f), Offset(x0, y), Offset(x0 + l, y + 2 * u * (azar(74, k) - 0.5f)), 1.4f * u, StrokeCap.Round)
        }
        // Las muescas de las noches, de cinco en cinco, arriba a la derecha.
        for (g in 0 until 3) {
            val x = w - 58 * u + g * 14 * u; val y = 18 * u
            for (i in 0 until 4) drawLine(tinta.copy(alpha = 0.16f), Offset(x + i * 2.2f * u, y), Offset(x + i * 2.2f * u, y + 9 * u), 0.8f * u, StrokeCap.Round)
            drawLine(tinta.copy(alpha = 0.16f), Offset(x - 1.2f * u, y + 8 * u), Offset(x + 8 * u, y + 1 * u), 0.8f * u, StrokeCap.Round)
        }
        caballoGalia(Offset(w / 2, h * 0.52f), w * 0.3f, tinta.copy(alpha = 0.028f))
        onda(w * 0.16f, w * 0.84f, h - 46 * u, 5 * u, p.acento.copy(alpha = 0.32f), 0.8f * u, hueco = 14 * u)
        torq(Offset(w / 2, h - 46 * u), 6 * u, 1.6f * u, p.acento.copy(alpha = 0.55f), p.acento.copy(alpha = 0.25f))
        // En modo oscuro, la tablilla a la luz del fuego: alumbra desde abajo a la izquierda, donde está el hogar.
        if (oscuro && !p.oscura) {
            drawRect(Brush.radialGradient(0f to FUEGO_HALO.copy(alpha = 0.12f), 0.45f to Color.Transparent, 1f to NOCHE.copy(alpha = 0.62f),
                center = Offset(w * 0.3f, h * 0.7f), radius = maxOf(w, h) * 0.95f))
            drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.35f), Color.Transparent), 0f, 18 * u), Offset.Zero, Size(18 * u, h))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.35f)), w - 18 * u, w), Offset(w - 18 * u, 0f), Size(18 * u, h))
        }
        if (noche) {
            val l = Offset(w - 30 * u, h - 84 * u)
            mancha(l, 40 * u, LUNA.copy(alpha = 0.1f))
            clipPath(Path().apply { addOval(Rect(l, 11 * u)) }) {
                drawCircle(LUNA.copy(alpha = 0.4f), 11 * u, l)
                drawCircle(p.color.copy(alpha = 0.85f), 11 * u, l + Offset(-7.5f * u, -1 * u))
            }
            ramita(Offset(18 * u, h * 0.22f), 36 * u, 165f, p.detalle.copy(alpha = 0.35f), LUNA.copy(alpha = 0.5f))
        }
    }

    /** La barra de arriba es el canto de roble del marco: la veta, una fila de clavos de bronce y la onda tallada debajo. */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (p.oscura) mezcla(p.color, Color.Black, 0.3f) else mezcla(mezcla(p.color, CERA_OSC, 0.4f), Color.Black, if (oscuro) 0.3f else 0f))
        val banda = Rect(0f, h - 9 * u, w, h)
        val roble = t(TABLA, oscuro, noche)
        madera(banda, roble, 211, nudos = 2)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), banda.top, banda.bottom), banda.topLeft, banda.size)
        var x = 18 * u
        while (x < w - 6 * u) {
            drawCircle(Color.Black.copy(alpha = 0.35f), 1.9f * u, Offset(x + 0.4f * u, banda.center.y + 0.6f * u))
            drawCircle(t(BRONCE, oscuro, noche), 1.7f * u, Offset(x, banda.center.y))
            drawCircle(Color.White.copy(alpha = 0.35f), 0.55f * u, Offset(x - 0.5f * u, banda.center.y - 0.5f * u))
            x += 46 * u
        }
        drawLine(p.acento.copy(alpha = 0.6f), Offset(0f, banda.top - 1.4f * u), Offset(w, banda.top - 1.4f * u), 0.8f * u)
    }

    /**
     * El botón de guardar es una placa de bronce con su verdín en las orillas, dos remaches y, en cada punta, el disco de un
     * remate de torques.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(5 * u, 3 * u, w - 6 * u, h - 4 * u)
        val rad = minOf(r.height / 2f, 10 * u)
        if (oscuro || noche) drawRoundRect(FUEGO_HALO.copy(alpha = 0.2f), r.inflate(3 * u).topLeft, r.inflate(3 * u).size, CornerRadius(rad + 3 * u))
        drawRoundRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1.2f * u, 2.2f * u), r.size, CornerRadius(rad))
        val bronce = Color(0xFF7E5430)
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(bronce, ORO_BRILLO, 0.3f), bronce, mezcla(bronce, Color.Black, 0.3f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(rad))
        clipPath(Path().apply { addRoundRect(RoundRect(r, CornerRadius(rad))) }) {
            mancha(Offset(r.left + 6 * u, r.bottom), r.height * 0.9f, VERDIN.copy(alpha = 0.45f))
            mancha(Offset(r.right - 10 * u, r.top), r.height * 0.7f, VERDIN.copy(alpha = 0.35f))
            for (k in 0 until 24) drawCircle(Color.Black.copy(alpha = 0.1f), (0.3f + 0.5f * azar(17, k)) * u, Offset(r.left + r.width * azar(19, k), r.top + r.height * azar(23, k)))
        }
        val dentro = r.deflate(3 * u)
        val rd = CornerRadius((rad - 3 * u).coerceAtLeast(0f))
        drawRoundRect(Color.White.copy(alpha = 0.18f), dentro.topLeft + Offset(0.6f * u, 0.6f * u), dentro.size, rd, style = Stroke(0.5f * u))
        drawRoundRect(mezcla(bronce, Color.Black, 0.45f).copy(alpha = 0.7f), dentro.topLeft, dentro.size, rd, style = Stroke(0.8f * u))
        listOf(r.left, r.right).forEach { x ->
            drawCircle(Color.Black.copy(alpha = 0.3f), r.height * 0.32f, Offset(x + 0.8f * u, r.center.y + 1.2f * u))
            drawCircle(Brush.radialGradient(listOf(ORO_BRILLO, ORO, mezcla(ORO, Color.Black, 0.35f)), center = Offset(x - r.height * 0.1f, r.center.y - r.height * 0.12f), radius = r.height * 0.4f), r.height * 0.3f, Offset(x, r.center.y))
        }
        drawRoundRect(Color.White.copy(alpha = 0.15f), Offset(r.left + 6 * u, r.top + 1.5f * u), Size(r.width - 12 * u, r.height * 0.28f), CornerRadius(rad))
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = Color(0xFFFFF2D6)

    // =================================================================
    //  La libreta de momentos: el haz de tablillas en la mesa de las cuentas
    // =================================================================

    override val altoLibreta: Dp = MuebleLibretaGalia.ALTO

    /** Una solapa que gira sobre su bisagra horizontal en [bisagra]: con [abrir] en 0 cuelga, en 1 apunta arriba y se ve por dentro. */
    private fun DrawScope.solapa(bisagra: Float, abrir: Float, bloque: DrawScope.(Boolean) -> Unit) {
        val k = cos(PI * abrir.coerceIn(0f, 1f)).toFloat()
        val esc = if (abs(k) < 0.02f) 0.02f * (if (k < 0f) -1f else 1f) else k
        withTransform({ scale(1f, esc, Offset(size.width / 2f, bisagra)) }) { bloque(k < 0f) }
    }

    /** Media pieza de un sello que se parte por una línea quebrada a la altura [y]: la de arriba ([arriba]) o la de abajo. */
    private fun mitad(r: Rect, y: Float, arriba: Boolean, semilla: Int): Path = Path().apply {
        val dientes = 5
        val amp = r.height * 0.12f
        moveTo(r.left - 2f, if (arriba) r.top - 2f else r.bottom + 2f)
        lineTo(r.left - 2f, y)
        for (i in 1..dientes) {
            val x = r.left + r.width * i / dientes
            lineTo(x - r.width / dientes * 0.5f, y + (if (i % 2 == 0) amp else -amp) * (0.5f + 0.9f * azar(semilla, i)))
            lineTo(x + if (i == dientes) 2f else 0f, y)
        }
        lineTo(r.right + 2f, if (arriba) r.top - 2f else r.bottom + 2f)
        close()
    }

    /** La mesa de las cuentas con el haz de tablillas: en `MuebleLibretaGalia*.kt`. */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) =
        with(MuebleLibretaGalia) { libreta(e, medidor, oscuro) }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) =
        with(IndiceLibretaGalia) { pestana(e, medidor, oscuro) }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) =
        with(IndiceLibretaGalia) { rotuloMes(texto, medidor, oscuro) }

    // =================================================================
    //  Las cartas: mensajes que corren de oppidum en oppidum
    // =================================================================

    /**
     * El mensaje como corría de oppidum en oppidum: la tablilla dentro de su funda de cuero, con la solapa cosida; por fuera,
     * la tablillita de cera cosida a la funda, donde se raya a quién va. La ata un cordel de lana roja, que da dos vueltas, y
     * sobre el nudo, a caballo en el borde de la solapa, va el sello de barro con la marca de quien lo manda: el caballo de las
     * monedas. Cuando la carta ya se puede abrir, por la juntura asoma la luz de una brasa; al abrirla, el sello se parte y sale
     * la tablilla escrita.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, false)
        val cu = e.cuerpo
        val s = Rect(cu.left + 4 * u, cu.top + 2 * u, cu.right - 4 * u, cu.bottom - 5 * u)
        val w = s.width; val h = s.height
        val k = (w / (164f * u)).coerceIn(0.8f, 2.2f)
        val bis = s.top
        val borde = bis + h * 0.34f
        val ab = e.abrir.coerceIn(0f, 1f)
        val cerrada = ab < 0.5f
        // De noche, el cuero a oscuras que alcanza el fuego.
        val piel = if (oscuro) luz(Color(0xFF9A7048), true, false, 0.5f) else Color(0xFF9A7048)
        val pielOsc = mezcla(piel, Color.Black, 0.3f)
        val lana = luz(LANAS[0], oscuro, false, 0.55f)
        val ro = minOf(w, h) * 0.13f
        val pc = Offset(s.center.x, borde)
        val pr = Rect(pc.x - ro * 1.15f, pc.y - ro * 1.15f, pc.x + ro * 1.15f, pc.y + ro * 1.15f)
        fun DrawScope.sello(alfa: Float) = selloGalia(pc, ro, mezcla(luz(Color(0xFFA4704A), oscuro, false, 0.6f), FUEGO_HALO, e.brillo * 0.25f), alfa, e.semilla)

        sombraSuave(s, 3 * u, 6 * u, 0.28f)
        drawRoundRect(pielOsc, s.topLeft, s.size, CornerRadius(3 * u))
        // La solapa: por fuera el cuero cosido; por dentro, la gamuza.
        fun DrawScope.tapaSolapa(adentro: Boolean) {
            val p = Path().apply {
                moveTo(s.left, bis); lineTo(s.right, bis); lineTo(s.right, borde - h * 0.05f)
                quadraticTo(s.center.x, borde + h * 0.06f, s.left, borde - h * 0.05f); close()
            }
            if (adentro) {
                drawPath(p, mezcla(piel, Color(0xFFD8B88E), 0.55f))
                drawPath(p, Color.Black.copy(alpha = 0.25f), style = Stroke(0.6f * u))
                return
            }
            translate(0f, 1.6f * u) { drawPath(p, Color.Black.copy(alpha = 0.3f)) }
            drawPath(p, Brush.verticalGradient(listOf(mezcla(piel, Color.White, 0.12f), piel), bis, borde))
            clipPath(p) {
                for (n in 0 until 120) drawCircle((if (n % 3 == 0) Color.White else Color.Black).copy(alpha = 0.06f), (0.3f + 0.5f * azar(e.semilla + 9, n)) * u,
                    Offset(s.left + w * azar(e.semilla + 10, n), bis + (borde - bis) * azar(e.semilla + 11, n)))
            }
            val cos0 = Path().apply {
                moveTo(s.left + 4 * u, bis + 3 * u); lineTo(s.right - 4 * u, bis + 3 * u); lineTo(s.right - 4 * u, borde - h * 0.07f)
                quadraticTo(s.center.x, borde + h * 0.02f, s.left + 4 * u, borde - h * 0.07f); close()
            }
            drawPath(cos0, mezcla(piel, Color(0xFFF2E2C0), 0.5f).copy(alpha = 0.6f), style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2 * u), 0f)))
            drawPath(p, Color.Black.copy(alpha = 0.4f), style = Stroke(0.6f * u))
            if (e.romper > 0f) withTransform({ translate(-1.2f * u * e.romper, -2.5f * u * e.romper); rotate(-7f * e.romper, pc) }) {
                clipPath(mitad(pr, pc.y, true, e.semilla)) { sello(1f) }
            }
        }
        if (!cerrada) solapa(bis, ab) { tapaSolapa(it) }
        // La tablilla que sale de la funda, con lo escrito.
        if (e.subir > 0f) {
            val arriba0 = s.top + h * 0.05f
            val alza = minOf(e.subir.coerceIn(0f, 1f) * h * 0.9f, (arriba0 - 4 * u).coerceAtLeast(0f))
            val hj = Rect(s.left + w * 0.07f, arriba0 - alza, s.right - w * 0.07f, arriba0 - alza + h * 0.9f)
            val ceraCarta = if (oscuro) mezcla(mezcla(e.hoja, NOCHE, 0.2f), FUEGO, 0.06f) else mezcla(e.hoja, CERA, 0.35f)
            val q = tablilla(hj, luz(TABLA, oscuro, false, 0.55f), ceraCarta, e.semilla + 1, 5 * u, 0.25f)
            onda(q.left + 6 * u, q.right - 6 * u, q.top + 4 * u, 3 * u, oro(oscuro).copy(alpha = 0.6f), 0.6f * u, hueco = 5 * u)
            var y = q.top + 6 * u * k + 9 * u
            while (y < q.bottom - 3 * u) { drawLine(e.renglones, Offset(q.left + 5 * u, y), Offset(q.right - 5 * u, y), 0.7f * u); y += 11 * u }
        }
        // La funda: el cuero con su grano, los cantos cosidos y la tablillita de la dirección.
        val forma = Path().apply { addRoundRect(RoundRect(s, CornerRadius(3 * u))) }
        drawPath(forma, Brush.verticalGradient(listOf(piel, mezcla(piel, pielOsc, 0.5f)), s.top, s.bottom))
        clipPath(forma) {
            for (n in 0 until 260) drawCircle((if (n % 3 == 0) Color.White else Color.Black).copy(alpha = 0.06f + 0.04f * azar(e.semilla, n)), (0.3f + 0.5f * azar(e.semilla + 1, n)) * u,
                Offset(s.left + w * azar(e.semilla + 2, n), s.top + h * azar(e.semilla + 3, n)))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.12f)), s.left, s.right), s.topLeft, s.size)
        }
        val costura = mezcla(piel, Color(0xFFF2E2C0), 0.5f).copy(alpha = 0.55f)
        listOf(s.left + 3 * u, s.right - 3 * u).forEach { x -> drawLine(costura, Offset(x, s.top + 4 * u), Offset(x, s.bottom - 3 * u), 0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2 * u), 0f)) }
        drawLine(costura, Offset(s.left + 4 * u, s.bottom - 3 * u), Offset(s.right - 4 * u, s.bottom - 3 * u), 0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2 * u), 0f))
        drawPath(forma, Color.Black.copy(alpha = 0.4f), style = Stroke(0.7f * u))
        if (!cerrada) drawRect(Color.Black.copy(alpha = 0.3f), Offset(s.left, s.top), Size(w, 1.6f * u))
        if (oscuro) clipPath(forma) { mancha(Offset(s.left, s.top), w * 0.7f, FUEGO_HALO.copy(alpha = 0.12f)) }
        // La tablillita de la dirección, cosida a la funda.
        val dir = Rect(s.left + w * 0.1f, s.top + h * 0.45f, s.right - w * 0.1f, s.bottom - h * 0.08f)
        if (e.linea1.isNotBlank() || e.linea2.isNotBlank()) {
            val q = tablilla(dir, luz(TABLA_LUZ, oscuro, false, 0.5f), cera(oscuro, false, 0.5f), e.semilla + 4, 3.5f * u, 0.3f)
            listOf(dir.left - 1.5f * u, dir.right + 1.5f * u).forEach { x -> listOf(0.3f, 0.7f).forEach { f -> drawLine(c(LINO), Offset(x - 1.6f * u, dir.top + dir.height * f), Offset(x + 1.6f * u, dir.top + dir.height * f + 1 * u), 0.9f * u, StrokeCap.Round) } }
            textoEn(medidor, e.linea1, Rect(q.left + 3 * u, q.top, q.right - 3 * u, q.top + q.height * 0.5f), TITULO, tinta(oscuro), 11.5f, peso = GRUESO, minimo = 6.5f)
            onda(q.left + q.width * 0.2f, q.right - q.width * 0.2f, q.top + q.height * 0.55f, 2.6f * u, oro(oscuro).copy(alpha = 0.6f), 0.5f * u, hueco = 3 * u)
            textoEn(medidor, e.linea2, Rect(q.left + 3 * u, q.top + q.height * 0.6f, q.right - 3 * u, q.bottom), TEXTO, tinta(oscuro).copy(alpha = 0.85f), 9.5f, minimo = 5.5f)
        }
        if (cerrada) solapa(bis, ab) { tapaSolapa(it) }
        // La luz de la brasa que asoma por la juntura de la carta que ya se puede abrir.
        if (e.brillo > 0f && cerrada) {
            val a0 = Offset(s.left + w * 0.05f, borde - h * 0.04f); val a1 = Offset(s.right - w * 0.05f, borde - h * 0.04f)
            val junta = Path().apply { moveTo(a0.x, a0.y); quadraticTo(s.center.x, borde + h * 0.07f, a1.x, a1.y) }
            drawPath(junta, FUEGO_HALO.copy(alpha = 0.2f * e.brillo), style = Stroke(8 * u, cap = StrokeCap.Round))
            drawPath(junta, FUEGO.copy(alpha = 0.4f * e.brillo), style = Stroke(3 * u, cap = StrokeCap.Round))
            drawPath(junta, Color(0xFFFFE9B8).copy(alpha = 0.9f * e.brillo), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        // El cordel de lana roja: se va atando en dos vueltas; abierta la carta, queda suelto al pie.
        if (e.cordon > 0f && cerrada) {
            val f = e.cordon.coerceIn(0f, 1f)
            cordel(Offset(s.left - 1 * u, borde + 1 * u), Offset(s.left - 1 * u + (w + 2 * u) * (f * 2f).coerceAtMost(1f), borde - 0.5f * u), lana, 1.6f * u * k)
            if (f > 0.5f) cordel(Offset(s.left - 1 * u, borde + 4.5f * u), Offset(s.left - 1 * u + (w + 2 * u) * ((f - 0.5f) * 2f), borde + 3.5f * u), lana, 1.4f * u * k)
            if (f >= 1f) {
                cordel(Path().apply { moveTo(pc.x, pc.y); quadraticTo(pc.x - 6 * u, pc.y + 6 * u, pc.x - 9 * u, pc.y + 15 * u) }, lana, 1.2f * u * k)
                cordel(Path().apply { moveTo(pc.x, pc.y); quadraticTo(pc.x + 4 * u, pc.y + 8 * u, pc.x + 5 * u, pc.y + 17 * u) }, lana, 1.2f * u * k)
            }
        } else if (e.romper > 0.5f) {
            cordel(Path().apply { moveTo(s.left + w * 0.05f, s.bottom + 1 * u); cubicTo(s.left + w * 0.3f, s.bottom - 6 * u, s.left + w * 0.6f, s.bottom + 5 * u, s.right - w * 0.08f, s.bottom - 2 * u) }, lana, 1.3f * u * k)
        }
        // El sello: entero (y, al sellar, cayendo desde grande), o partido por el borde de la solapa.
        if (e.romper <= 0f) {
            if (e.brillo > 0f) drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = 0.45f * e.brillo), Color.Transparent), center = pc, radius = ro * 2.4f), ro * 2.4f, pc)
            if (e.alfaSello > 0f) withTransform({ scale(e.escalaSello, e.escalaSello, pc) }) { sello(e.alfaSello) }
        } else {
            withTransform({ translate(1f * u * e.romper, 3f * u * e.romper); rotate(-4f + 8f * e.romper, pc) }) {
                clipPath(mitad(pr, pc.y, false, e.semilla)) { sello(1f) }
            }
        }
    }

    // La tablilla que sale queda arriba, entre su onda y la boca de la funda.
    override val cajaPrevia = Rect(0.13f, 0.16f, 0.87f, 0.31f)

    override val altoBuzon: Dp = 210.dp

    /**
     * Donde se juntan los mensajes en la casa grande: arriba, la viga con la tablilla colgada que dice «Mensajes del oppidum»,
     * la bolsa de cuero del mensajero colgada de un clavo y, de noche, el fuego del hogar que alumbra desde la derecha. Abajo,
     * tres cestas de mimbre, cada una con su tablillita: las que ya se pueden abrir (con la brasa en la juntura), las atadas
     * (con su cordel rojo y el sello) y las leídas (abiertas). Caben siete por cesta; si hay más, se dice cuántas más.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val suelo = h - 12 * u
        zarzo(Rect(0f, 0f, w, suelo), luz(mezcla(BARRO, BARRO_SOMBRA, 0.4f), oscuro, false, 0.3f), 251, 1f, 0.35f)
        drawRect(c(Color(0xFF4E4030)), Offset(0f, suelo), Size(w, h - suelo))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, suelo), Size(w, 2 * u))
        val viga = Rect(0f, 0f, w, 10 * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, viga.bottom), Size(w, 4 * u))
        madera(viga, luz(MADERA, oscuro, false, 0.3f), 253, nudos = 3)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.05f else 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), 0f, viga.bottom), viga.topLeft, viga.size)
        // La bolsa del mensajero, colgada de un clavo.
        val cl = Offset(w * 0.1f, viga.bottom + 4 * u)
        drawCircle(c(HIERRO), 1.8f * u, cl)
        val bolsa = Path().apply {
            moveTo(cl.x - 9 * u, cl.y + 14 * u); quadraticTo(cl.x - 12 * u, cl.y + 34 * u, cl.x, cl.y + 36 * u); quadraticTo(cl.x + 12 * u, cl.y + 34 * u, cl.x + 9 * u, cl.y + 14 * u); close()
        }
        correa(cl, Offset(cl.x - 8 * u, cl.y + 15 * u), c(CORREA), 1.8f * u, 0.2f)
        correa(cl, Offset(cl.x + 8 * u, cl.y + 15 * u), c(CORREA), 1.8f * u, 0.2f)
        translate(1 * u, 1.5f * u) { drawPath(bolsa, Color.Black.copy(alpha = 0.3f)) }
        drawPath(bolsa, Brush.horizontalGradient(listOf(mezcla(c(CUERO), Color.White, 0.1f), c(CUERO), mezcla(c(CUERO), Color.Black, 0.3f)), cl.x - 12 * u, cl.x + 12 * u))
        drawLine(mezcla(c(CUERO), Color.Black, 0.4f), Offset(cl.x - 9 * u, cl.y + 20 * u), Offset(cl.x + 9 * u, cl.y + 20 * u), 0.7f * u)
        ruedita(Offset(cl.x, cl.y + 27 * u), 3.4f * u, luz(BRONCE, oscuro, false, 0.4f))
        // La tablilla del rótulo, colgada de la viga.
        val et = Rect(w * 0.26f, 16 * u, w * 0.68f, 38 * u)
        listOf(et.left + 7 * u, et.right - 7 * u).forEach { x -> correa(Offset(x, viga.bottom), Offset(x, et.top + 2 * u), c(CORREA), 1.4f * u, 0.2f) }
        val qe = tablilla(et, luz(TABLA_LUZ, oscuro, false, 0.4f), cera(oscuro, false, 0.4f), 255, 3 * u, 0.3f)
        textoEn(medidor, "Mensajes del oppidum", Rect(qe.left + 3 * u, qe.top, qe.right - 3 * u, qe.bottom), TITULO, tinta(oscuro), 10.5f, peso = GRUESO, minimo = 6f)
        // Las tres cestas.
        val grupos = listOf(Triple("Para abrir", e.listas, 0), Triple("Atadas", e.selladas, 1), Triple("Leídas", e.abiertas, 2))
        val anchoB = (w - 32 * u) / 3f
        grupos.forEach { (nombre, cuantas, tipo) ->
            val bx = 12 * u + tipo * (anchoB + 4 * u)
            val cesto = Rect(bx + anchoB * 0.12f, h * 0.66f, bx + anchoB * 0.88f, suelo)
            val altoCesta = cesto.height * 0.78f
            val borde = cesto.bottom - altoCesta
            // Lo que asoma de la cesta: las tablillas paradas, cada una un poco distinta.
            val vis = cuantas.coerceAtMost(7)
            for (jj in 0 until vis) {
                val gx = cesto.left + 2 * u + (cesto.width - 20 * u) * (jj / 6f)
                val alto = (26f + 12f * azar(tipo * 13 + 5, jj)) * u
                val r = Rect(gx, borde - alto + 8 * u, gx + 15 * u, borde + 10 * u)
                rotate(-6f + 12f * azar(tipo * 7 + 1, jj), Offset(r.center.x, r.bottom)) {
                    when (tipo) {
                        0 -> {
                            val q = tablilla(r, luz(TABLA, oscuro, false, 0.5f), cera(oscuro, false, 0.5f), jj, 2 * u, 0.3f)
                            drawLine(FUEGO.copy(alpha = 0.8f), Offset(q.left, q.top + 0.6f * u), Offset(q.right, q.top + 0.6f * u), 0.9f * u)
                            mancha(Offset(r.center.x, r.top), 8 * u, FUEGO_HALO.copy(alpha = 0.35f))
                            selloGalia(Offset(r.center.x, r.top + 7 * u), 3 * u, luz(Color(0xFFA4704A), oscuro, false, 0.5f), 1f, jj)
                        }
                        1 -> {
                            drawRoundRect(luz(Color(0xFF9A7048), oscuro, false, 0.4f), r.topLeft, r.size, CornerRadius(1.5f * u))
                            drawRoundRect(Color.Black.copy(alpha = 0.35f), r.topLeft, r.size, CornerRadius(1.5f * u), style = Stroke(0.5f * u))
                            drawLine(luz(LANAS[0], oscuro, false, 0.5f), Offset(r.left - 0.5f * u, r.top + 7 * u), Offset(r.right + 0.5f * u, r.top + 7 * u), 1.2f * u)
                            selloGalia(Offset(r.center.x, r.top + 7 * u), 2.8f * u, luz(Color(0xFF8E5E3C), oscuro, false, 0.4f), 1f, jj + 7)
                        }
                        else -> {
                            val q = tablilla(r, luz(TABLA_LUZ, oscuro, false, 0.4f), cera(oscuro, false, 0.4f), jj + 3, 2 * u, 0.3f)
                            letrasGriegas(q.deflate(1 * u), tinta(oscuro).copy(alpha = 0.45f), jj, 2.4f * u, 0.45f * u)
                        }
                    }
                }
            }
            cesta(Offset(cesto.center.x, cesto.bottom), cesto.width, altoCesta, luz(Color(0xFFB08A50), oscuro, false, 0.3f), null)
            if (tipo == 0 && cuantas > 0) mancha(Offset(cesto.center.x, borde), cesto.width * 0.6f, FUEGO_HALO.copy(alpha = 0.22f))
            if (cuantas > 7) textoEn(medidor, "+${cuantas - 7}", Rect(cesto.left, borde + altoCesta * 0.3f, cesto.right, cesto.bottom - 4 * u), TITULO,
                if (oscuro) FUEGO else Color(0xFFFFF2D6), 13f, peso = GRUESO)
            if (cuantas == 0) hoja(Offset(cesto.center.x, borde - 3 * u), 16 * u, 80f, c(mezcla(ROBLE, PAJA, 0.4f)), c(ROBLE_SOMBRA))
            // La tablillita de la cesta, colgada de la viga sobre su cesta.
            val ti = Rect(bx + anchoB * 0.12f, 50 * u, bx + anchoB * 0.88f, 68 * u)
            listOf(ti.left + 6 * u, ti.right - 6 * u).forEach { x -> drawLine(c(CORREA), Offset(x, viga.bottom), Offset(x, ti.top + 2 * u), 1f * u) }
            val qt = tablilla(ti, luz(TABLA_LUZ, oscuro, false, 0.4f), cera(oscuro, false, 0.4f), 261 + tipo, 2.5f * u, 0.3f)
            textoEn(medidor, "$nombre · $cuantas", Rect(qt.left + 2 * u, qt.top, qt.right - 2 * u, qt.bottom), TITULO, tinta(oscuro), 9.5f, peso = GRUESO, minimo = 5f)
        }
        if (oscuro) resplandor(Offset(w * 1.02f, h * 0.55f), w * 0.6f)
        else drawRect(Brush.horizontalGradient(listOf(Color(0xFFFFF2D0).copy(alpha = 0.14f), Color.Transparent), 0f, w * 0.6f), Offset.Zero, Size(w * 0.6f, h))
    }

    // =================================================================
    //  Lo de alrededor: las pestañas, el buscador, las obras y los recuerdos
    // =================================================================

    override val altoPestanas: Dp = 78.dp

    /**
     * El emblema de cada biblioteca, como trazo dentro de [r]: 0, el diario (el haz con su correa); 1, los momentos (el estuche
     * con su solapa); 2, los sueños (la luna del sexto día); 3, el ideario (la cesta de mimbre); 4, las cartas (la funda atada
     * con su sello).
     */
    private fun iconoBiblioteca(k: Int, r: Rect): Path = Path().apply {
        val w = r.width; val h = r.height; val x = r.left; val y = r.top
        val c = r.center
        when (k) {
            0 -> {
                addRect(Rect(x + w * 0.16f, y + h * 0.16f, x + w * 0.84f, y + h * 0.94f))
                addRect(Rect(x + w * 0.3f, y + h * 0.3f, x + w * 0.7f, y + h * 0.5f))
                moveTo(x + w * 0.16f, y + h * 0.66f); lineTo(x + w * 0.84f, y + h * 0.66f)
                moveTo(x + w * 0.22f, y + h * 0.08f); lineTo(x + w * 0.9f, y + h * 0.08f); lineTo(x + w * 0.9f, y + h * 0.86f)
            }
            1 -> {
                addRect(Rect(x + w * 0.18f, y + h * 0.28f, x + w * 0.82f, y + h * 0.96f))
                moveTo(x + w * 0.18f, y + h * 0.28f); lineTo(x + w * 0.18f, y + h * 0.48f); quadraticTo(c.x, y + h * 0.62f, x + w * 0.82f, y + h * 0.48f); lineTo(x + w * 0.82f, y + h * 0.28f)
                moveTo(x + w * 0.2f, y + h * 0.28f); lineTo(c.x, y + h * 0.02f); lineTo(x + w * 0.8f, y + h * 0.28f)
            }
            2 -> {
                addArc(Rect(c.x - w * 0.42f, c.y - w * 0.42f, c.x + w * 0.42f, c.y + w * 0.42f), 300f, 240f)
                addArc(Rect(c.x - w * 0.18f, c.y - w * 0.4f, c.x + w * 0.32f, c.y + w * 0.4f), 270f, 180f)
                moveTo(c.x + w * 0.38f, c.y - h * 0.38f); lineTo(c.x + w * 0.38f, c.y - h * 0.3f)
            }
            3 -> {
                moveTo(x + w * 0.1f, y + h * 0.36f); lineTo(x + w * 0.9f, y + h * 0.36f); lineTo(x + w * 0.76f, y + h * 0.96f); lineTo(x + w * 0.24f, y + h * 0.96f); close()
                moveTo(x + w * 0.16f, y + h * 0.56f); lineTo(x + w * 0.84f, y + h * 0.56f)
                moveTo(x + w * 0.2f, y + h * 0.76f); lineTo(x + w * 0.8f, y + h * 0.76f)
                addArc(Rect(x + w * 0.24f, y + h * 0.02f, x + w * 0.76f, y + h * 0.7f), 180f, 180f)
            }
            else -> {
                addRect(Rect(x + w * 0.04f, y + h * 0.22f, x + w * 0.96f, y + h * 0.84f))
                moveTo(x + w * 0.04f, y + h * 0.22f); quadraticTo(c.x, y + h * 0.62f, x + w * 0.96f, y + h * 0.22f)
                addOval(Rect(c.x - w * 0.12f, y + h * 0.42f - w * 0.12f, c.x + w * 0.12f, y + h * 0.42f + w * 0.12f))
            }
        }
    }

    /**
     * Las pestañas son cinco tablillas colgadas de una vara de roble por sus tientos, delante de la pared de zarzo: en cada una,
     * el emblema rayado en la cera, el nombre y la cuenta. La elegida cuelga más baja y la alumbra el fuego, que dora su emblema.
     */
    override fun DrawScope.pestanas(e: EstadoPestanas, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val banda = Rect(0f, 1 * u, w, h - 1 * u)
        val forma = Path().apply { addRoundRect(RoundRect(banda, CornerRadius(5 * u))) }
        clipPath(forma) {
            zarzo(banda, luz(BARRO, oscuro, false, 0.3f), 271, 0.8f)
            // La vara de roble de la que cuelgan.
            val vara = Rect(0f, 3 * u, w, 9 * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, vara.bottom), Size(w, 2.4f * u))
            drawRect(Brush.verticalGradient(listOf(mezcla(c(MADERA), Color.White, 0.2f), c(MADERA), mezcla(c(MADERA), Color.Black, 0.35f)), vara.top, vara.bottom), vara.topLeft, vara.size)
        }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(0.8f * u))
        for (k in 0 until 5) {
            val q0 = quinta(k, w, 0f, h)
            val sel = k == e.actual
            val baja = if (sel) 3.5f * u else 0f
            val r = Rect(q0.left + 5 * u, 12 * u + baja, q0.right - 5 * u, h - 5 * u + baja * 0.4f)
            listOf(r.left + 6 * u, r.right - 6 * u).forEach { x -> drawLine(c(CORREA), Offset(x, 6 * u), Offset(x, r.top + 2 * u), 1.1f * u, StrokeCap.Round) }
            val q = tablilla(r, luz(mezcla(TABLA, TABLA_LUZ, if (sel) 0.4f else 0.15f), oscuro, false, if (sel) 0.75f else 0.35f), cera(oscuro, false, if (sel) 0.8f else 0.4f), 281 + k, 3 * u, 0.3f)
            if (sel) clipRect(q.left, q.top, q.right, q.bottom) { mancha(Offset(q.center.x, q.bottom), q.width * 0.9f, FUEGO_HALO.copy(alpha = if (oscuro) 0.4f else 0.38f)) }
            val texto = if (sel) tinta(oscuro) else tinta(oscuro).copy(alpha = 0.72f)
            val icono = if (sel) oro(oscuro) else texto
            val ic = Rect(q.center.x - 8 * u, q.top + 3 * u, q.center.x + 8 * u, q.top + 19 * u)
            drawPath(iconoBiblioteca(k, ic), icono, style = Stroke(1.2f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            textoEn(medidor, e.nombres.getOrElse(k) { "" }, Rect(q.left + 1 * u, q.top + 21 * u, q.right - 1 * u, q.top + 33 * u), TITULO, texto, 9f,
                peso = if (sel) GRUESO else FontWeight.Normal, minimo = 5f)
            textoEn(medidor, "${e.cuentas.getOrElse(k) { 0 }}", Rect(q.left + 1 * u, q.top + 33 * u, q.right - 1 * u, q.bottom - 1 * u), ROTULO, if (sel) icono else texto, 11f,
                peso = FontWeight.SemiBold, minimo = 6f)
        }
    }

    // ---------------------------------------------------------------- el buscador

    override val altoCampo: Dp = 52.dp
    override val cajaCampo = Rect(0.17f, 0.24f, 0.83f, 0.76f)
    override val letraCampo: FontFamily = TEXTO
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El buscador es una tablilla larga, de cera, en su marco de roble: a la izquierda, el estilo de hueso, que se levanta con el
     * foco; a la derecha, el torques, que se dora y brilla.
     */
    override fun DrawScope.campo(e: EstadoCampo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(0f, 0f, w, h).deflate(1 * u)
        val q = tablilla(r, luz(TABLA, oscuro, false, if (e.enfocado) 0.55f else 0.35f), cera(oscuro, false, if (e.enfocado) 0.65f else 0.45f), 291, 6 * u, 0.25f)
        if (e.enfocado) clipRect(q.left, q.top, q.right, q.bottom) { mancha(Offset(w * 0.1f, q.bottom), w * 0.4f, FUEGO_HALO.copy(alpha = if (oscuro) 0.3f else 0.2f)) }
        // El estilo de hueso.
        val giro = if (e.enfocado) -24f else 0f
        val es = Offset(w * 0.035f, h * 0.7f)
        rotate(giro, Offset(w * 0.13f, h * 0.5f)) {
            drawLine(Color.Black.copy(alpha = 0.3f), es + Offset(1 * u, 1.5f * u), es + Offset(w * 0.09f + 1 * u, -h * 0.4f + 1.5f * u), 2.6f * u, StrokeCap.Round)
            drawLine(luz(Color(0xFFE6DCC4), oscuro, false, 0.6f), es, es + Offset(w * 0.09f, -h * 0.4f), 2.4f * u, StrokeCap.Round)
            drawCircle(luz(Color(0xFFD8CCB0), oscuro, false, 0.6f), 2.4f * u, es)
        }
        // El torques.
        val tc = Offset(w * 0.925f, h / 2)
        val tr = minOf(h * 0.24f, w * 0.04f)
        if (e.enfocado) mancha(tc, tr * 2.2f, FUEGO_HALO.copy(alpha = 0.45f))
        torq(tc, tr, tr * 0.32f, if (e.enfocado) ORO else luz(mezcla(ORO, BRONCE, 0.5f), oscuro, false, 0.3f), if (e.enfocado) ORO_BRILLO else luz(ORO, oscuro, false, 0.3f))
        if (e.vacio) aIzquierda(medidor, e.pista, Rect(w * cajaCampo.left, h * cajaCampo.top, w * cajaCampo.right, h * cajaCampo.bottom), TEXTO, tinta(oscuro).copy(alpha = if (oscuro) 0.6f else 0.45f), 14f, minimo = 9f)
    }

    // ---------------------------------------------------------------- las obras

    /**
     * Las obras se apoyan en una tabla de roble clavada a la pared de zarzo: arriba, la viga con el ramo de muérdago colgado y,
     * a la izquierda, la tea en su soporte (encendida de noche); de día, la luz entra por la puerta, de la izquierda.
     */
    override fun DrawScope.repisaObras(medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, false)
        val y0 = h - 24 * u
        zarzo(Rect(0f, 0f, w, y0), luz(mezcla(BARRO, BARRO_SOMBRA, 0.4f), oscuro, false, 0.3f), 301, 1f, 0.5f)
        val viga = Rect(0f, 0f, w, 9 * u)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, viga.bottom), Size(w, 3 * u))
        madera(viga, luz(MADERA, oscuro, false, 0.3f), 303, nudos = 2)
        val mu = Offset(w * 0.88f, viga.bottom)
        cordel(mu, mu + Offset(0f, 8 * u), c(LINO), 0.9f * u)
        ramo(mu + Offset(0f, 8 * u), 36 * u, luz(MUERDAGO, oscuro, false, 0.4f), luz(BAYA, oscuro, false, 0.4f))
        val p = Offset(w * 0.07f, h * 0.36f)
        tea(p, 32 * u, oscuro, oscuro, false)
        val tabla = Rect(0f, y0, w, h - 9 * u)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(0f, y0 - 4 * u), Size(w, 4 * u))
        madera(tabla, luz(TABLA, oscuro, false, 0.4f), 307, nudos = 2)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.06f else 0.22f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), tabla.top, tabla.bottom), tabla.topLeft, tabla.size)
        drawRect(Color.Black.copy(alpha = 0.36f), Offset(0f, tabla.bottom), Size(w, 3 * u))
        drawRect(c(Color(0xFF4E4030)), Offset(0f, tabla.bottom + 3 * u), Size(w, h - tabla.bottom - 3 * u))
        if (oscuro) resplandor(p + Offset(-3 * u, -15 * u), w * 0.5f)
        else drawRect(Brush.horizontalGradient(listOf(Color(0xFFFFF2D0).copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, w), Offset.Zero, Size(w, h))
    }

    /** Un título partido por palabras en a lo más tres renglones que caben en [r]. */
    private fun DrawScope.tituloEn(medidor: TextMeasurer, titulo: String, r: Rect, letra: FontFamily, color: Color, tam: Float, peso: FontWeight) {
        val palabras = titulo.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (palabras.isEmpty()) return
        val lineas = mutableListOf<String>()
        var actual = ""
        palabras.forEach { p ->
            val prueba = if (actual.isEmpty()) p else "$actual $p"
            if (actual.isNotEmpty() && anchoTx(medidor, prueba, letra, tam, peso = peso) > r.width && lineas.size < 2) { lineas.add(actual); actual = p }
            else { actual = prueba }
        }
        lineas.add(actual)
        val alto = r.height / 3f
        var y = r.center.y - alto * lineas.size / 2f
        lineas.forEach { s ->
            textoEn(medidor, s, Rect(r.left, y, r.right, y + alto), letra, color, tam, peso = peso, minimo = tam * 0.5f)
            y += alto
        }
    }

    /** Cuál de [n] variantes le toca a una obra según su [semilla], que puede venir negativa. */
    private fun variante(semilla: Int, n: Int): Int = Math.floorMod(semilla, n.coerceAtLeast(1))

    /**
     * La portada de una obra es la tapa de roble de un díptico: el filete tallado, las correas de la bisagra a la izquierda y,
     * hundida arriba, la ventana de cera con el tipo, una onda y el título; abajo, otra con el autor y el año, y el estátero con
     * el caballo al pie. En miniatura, solo la inicial en su ventanita.
     */
    override fun DrawScope.portadaObra(e: EstadoPortada, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = w / u
        fun c(x: Color) = luz(x, oscuro, false, 0.5f)
        val roble = c(listOf(TABLA, mezcla(TABLA, VETA, 0.25f), mezcla(TABLA, TABLA_LUZ, 0.3f))[variante(e.semilla, 3)])
        val tapa = Rect(0f, 0f, w, h)
        val rad = CornerRadius(w * 0.03f)
        clipPath(Path().apply { addRoundRect(RoundRect(tapa, rad)) }) {
            madera(tapa, roble, e.semilla, vertical = true, nudos = 1)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.White.copy(alpha = 0.06f), Color.Transparent, Color.Black.copy(alpha = 0.28f)), 0f, w), tapa.topLeft, tapa.size)
        }
        drawRoundRect(Color.Black.copy(alpha = 0.4f), tapa.topLeft, tapa.size, rad, style = Stroke(maxOf(0.5f * u, w * 0.006f)))
        val fondo = cera(oscuro, false, 0.5f)
        fun ventana(r: Rect) {
            drawRect(Brush.verticalGradient(listOf(mezcla(fondo, CERA_OSC, 0.3f), fondo), r.top, r.bottom), r.topLeft, r.size)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), r.top, r.top + w * 0.03f), r.topLeft, Size(r.width, w * 0.03f))
            drawRect(Color.Black.copy(alpha = 0.35f), r.topLeft, r.size, style = Stroke(maxOf(0.5f * u, w * 0.005f)))
        }
        if (e.mini) {
            val r = Rect(w * 0.2f, h * 0.3f, w * 0.8f, h * 0.7f)
            ventana(r)
            textoEn(medidor, e.titulo.trim().take(1).uppercase().ifEmpty { "·" }, r, TITULO, tinta(oscuro), s * 0.36f, peso = GRUESO, minimo = 5f)
            return
        }
        val hondo = mezcla(roble, Color.Black, 0.55f)
        val labio = mezcla(roble, Color.White, 0.3f).copy(alpha = 0.5f)
        val filete = tapa.deflate(w * 0.045f)
        drawRect(labio, filete.topLeft + Offset(w * 0.005f, w * 0.005f), filete.size, style = Stroke(maxOf(0.5f * u, w * 0.005f)))
        drawRect(hondo, filete.topLeft, filete.size, style = Stroke(maxOf(0.6f * u, w * 0.006f)))
        val cuero = c(CORREA)
        listOf(0.22f, 0.78f).forEach { f -> correa(Offset(-w * 0.01f, h * f), Offset(w * 0.06f, h * f), cuero, maxOf(1.6f * u, w * 0.035f), 0.25f) }
        val ti = Rect(w * 0.14f, h * 0.08f, w * 0.88f, h * 0.48f)
        ventana(ti)
        textoEn(medidor, e.tipo, Rect(ti.left + w * 0.04f, ti.top + h * 0.012f, ti.right - w * 0.04f, ti.top + h * 0.07f), ROTULO, oro(oscuro), s * 0.07f, peso = FontWeight.SemiBold, espaciado = s * 0.01f, minimo = 3.5f)
        onda(ti.left + w * 0.08f, ti.right - w * 0.08f, ti.top + h * 0.09f, w * 0.035f, oro(oscuro).copy(alpha = 0.7f), maxOf(0.5f * u, w * 0.006f), hueco = w * 0.03f)
        tituloEn(medidor, e.titulo, Rect(ti.left + w * 0.04f, ti.top + h * 0.11f, ti.right - w * 0.04f, ti.bottom - h * 0.01f), TITULO, tinta(oscuro), s * 0.1f, GRUESO)
        val pie = listOf(e.autor, e.anio).filter { it.isNotBlank() }.joinToString(" · ")
        if (pie.isNotEmpty()) {
            val ta = Rect(w * 0.16f, h * 0.58f, w * 0.86f, h * 0.71f)
            ventana(ta)
            textoEn(medidor, pie, ta.deflate(w * 0.03f), TEXTO, tinta(oscuro).copy(alpha = 0.85f), s * 0.065f, minimo = 3.5f)
        }
        moneda(Offset(w * 0.52f, h * 0.85f), w * 0.08f, oscuro)
    }

    // ---------------------------------------------------------------- los recuerdos

    override val margenRecuerdo: Dp = 14.dp
    override val altoRotuloRecuerdo: Dp = 48.dp
    override fun tintaRecuerdo(oscuro: Boolean): Color = tinta(oscuro)

    /** «Hace un año» o «Hace N años», como lo dice la tablilla del recuerdo. */
    private fun haceAnios(n: Int): String = if (n == 1) "Hace un año" else "Hace $n años"

    /** El clima sin su signo (☀ ☂ ❄), que las letras del tema no traen. */
    private fun sinSigno(clima: String): String = clima.dropWhile { !it.isLetterOrDigit() }.trim()

    /**
     * El recuerdo es una tablilla vieja: el marco de roble, la cera con el rastro de lo alisado y, como marca de agua, la hoja
     * de roble. En la cabecera, el torques con los años dentro, «Hace N años» en oro y la fecha del tema con el clima; a la
     * derecha, la borla de lana del color del ánimo colgada de su tiento. Bajo la cabecera, la onda de La Tène.
     */
    override fun DrawScope.recuerdo(e: EstadoRecuerdo, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val fondo = cera(oscuro, false, 0.35f)
        val tinta = tintaRecuerdo(oscuro)
        val acento = oro(oscuro)
        val cab = altoRotuloRecuerdo.toPx()
        val forma = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(8 * u))) }
        clipPath(forma) {
            madera(Rect(0f, 0f, w, h), luz(TABLA, oscuro, false, 0.35f), e.semilla, nudos = 2)
            val q = Rect(5 * u, 5 * u, w - 5 * u, h - 5 * u)
            drawRoundRect(fondo, q.topLeft, q.size, CornerRadius(4 * u))
            clipRect(q.left, q.top, q.right, q.bottom) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), q.top, q.top + 4 * u), q.topLeft, Size(q.width, 4 * u))
                for (k in 0 until 5) {
                    val y = q.top + cab + (q.height - cab) * (0.15f + 0.18f * k)
                    drawLine(tinta.copy(alpha = 0.03f), Offset(q.left + q.width * 0.1f, y), Offset(q.right - q.width * (0.1f + 0.3f * azar(e.semilla, k)), y), 1.4f * u)
                }
                if (oscuro) mancha(Offset(0f, 0f), w * 0.6f, FUEGO_HALO.copy(alpha = 0.1f))
                if (h > cab + 40 * u) caballoGalia(Offset(w - 44 * u, h - 30 * u), 26 * u, mezcla(fondo, tinta, 0.07f))
            }
        }
        drawPath(forma, Color.Black.copy(alpha = 0.25f), style = Stroke(1f * u))
        onda(14 * u, w - 30 * u, cab - 1 * u, 4.4f * u, acento.copy(alpha = 0.55f), 0.7f * u)
        val tc = Offset(27 * u, cab / 2f + 2 * u)
        torq(tc, 14 * u, 3 * u, if (oscuro) mezcla(ORO, FUEGO, 0.3f) else ORO, ORO_BRILLO)
        textoEn(medidor, "${e.anios.coerceAtLeast(1)}", Rect(tc.x - 9 * u, tc.y - 7 * u, tc.x + 9 * u, tc.y + 7 * u), ROTULO, tinta, 11f, peso = FontWeight.SemiBold, minimo = 5f)
        val col = Rect(50 * u, 7 * u, w - 32 * u, cab - 7 * u)
        aIzquierda(medidor, haceAnios(e.anios), Rect(col.left, col.top, col.right, col.top + col.height * 0.5f), TITULO, acento, 11.5f, peso = GRUESO, minimo = 6f)
        val clima = sinSigno(e.clima)
        aIzquierda(medidor, if (clima.isEmpty()) e.fecha else e.fecha + " · " + clima, Rect(col.left, col.top + col.height * 0.5f, col.right, col.bottom), TEXTO, tinta, 10.5f, minimo = 6.5f)
        drawLine(t(CORREA, oscuro, false), Offset(w - 17 * u, 2 * u), Offset(w - 17 * u, cab / 2f - 7 * u), 1.1f * u, StrokeCap.Round)
        borla(Offset(w - 17 * u, cab / 2f - 7 * u), 16 * u, e.animo, oscuro, e.semilla)
    }
}
