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
    caballoGalia(c + Offset(r * 0.04f, r * 0.05f), r * 0.56f, labio)
    caballoGalia(c, r * 0.56f, hondo)
}

/**
 * El caballo de los estáteros arvernos, mirando a la derecha, en un cuadrado de lado 2·[r] centrado en [c]: el cuerpo en
 * media luna, el cuello arqueado y la cabeza chica, las patas de trazo con las perlas de las rodillas, la cola que se
 * enrosca, la crin de perlas y, debajo, la rueda. Se dibuja de un solo color (el oro de la moneda, el hueco del sello).
 */
internal fun DrawScope.caballoGalia(c: Offset, r: Float, color: Color) {
    val x = c.x; val y = c.y - r * 0.08f
    val g = r * 0.085f
    // El cuerpo: la grupa redonda, el lomo hundido y el pecho alto.
    val cuerpo = Path().apply {
        moveTo(x - r * 0.62f, y - r * 0.02f)
        cubicTo(x - r * 0.62f, y - r * 0.3f, x - r * 0.3f, y - r * 0.3f, x - r * 0.05f, y - r * 0.2f)
        cubicTo(x + r * 0.15f, y - r * 0.14f, x + r * 0.3f, y - r * 0.3f, x + r * 0.4f, y - r * 0.18f)
        cubicTo(x + r * 0.46f, y - r * 0.02f, x + r * 0.32f, y + r * 0.16f, x + r * 0.05f, y + r * 0.15f)
        cubicTo(x - r * 0.25f, y + r * 0.16f, x - r * 0.55f, y + r * 0.2f, x - r * 0.62f, y - r * 0.02f)
        close()
    }
    drawPath(cuerpo, color)
    // El cuello que se arquea y la cabeza, con la boca abierta hacia la derecha.
    val cuello = Path().apply {
        moveTo(x + r * 0.2f, y - r * 0.2f)
        cubicTo(x + r * 0.26f, y - r * 0.5f, x + r * 0.44f, y - r * 0.68f, x + r * 0.62f, y - r * 0.62f)
        lineTo(x + r * 0.82f, y - r * 0.44f)
        lineTo(x + r * 0.7f, y - r * 0.4f)
        lineTo(x + r * 0.62f, y - r * 0.46f)
        cubicTo(x + r * 0.52f, y - r * 0.4f, x + r * 0.46f, y - r * 0.2f, x + r * 0.4f, y - r * 0.08f)
        close()
    }
    drawPath(cuello, color)
    drawCircle(color, r * 0.06f, Offset(x + r * 0.6f, y - r * 0.7f))
    // La crin: una fila de perlas por el cuello.
    for (k in 0 until 4) drawCircle(color, r * 0.045f, Offset(x + r * (0.18f + 0.1f * k), y - r * (0.44f + 0.07f * k - 0.012f * k * k)))
    // Las patas: dos delante y dos detrás, de trazo, con su perla en la rodilla y el casco.
    listOf(
        Triple(Offset(x + r * 0.28f, y + r * 0.08f), Offset(x + r * 0.42f, y + r * 0.3f), Offset(x + r * 0.5f, y + r * 0.56f)),
        Triple(Offset(x + r * 0.16f, y + r * 0.12f), Offset(x + r * 0.2f, y + r * 0.34f), Offset(x + r * 0.14f, y + r * 0.58f)),
        Triple(Offset(x - r * 0.42f, y + r * 0.1f), Offset(x - r * 0.3f, y + r * 0.32f), Offset(x - r * 0.38f, y + r * 0.58f)),
        Triple(Offset(x - r * 0.54f, y + r * 0.06f), Offset(x - r * 0.62f, y + r * 0.3f), Offset(x - r * 0.56f, y + r * 0.56f)),
    ).forEach { (a, b, p) ->
        drawLine(color, a, b, g, StrokeCap.Round)
        drawLine(color, b, p, g * 0.85f, StrokeCap.Round)
        drawCircle(color, g * 0.85f, b)
        drawCircle(color, g * 0.75f, p)
    }
    // La cola, que se enrosca hacia arriba.
    drawPath(Path().apply {
        moveTo(x - r * 0.6f, y - r * 0.12f)
        cubicTo(x - r * 0.86f, y - r * 0.1f, x - r * 0.92f, y - r * 0.42f, x - r * 0.74f, y - r * 0.48f)
        quadraticTo(x - r * 0.62f, y - r * 0.5f, x - r * 0.66f, y - r * 0.38f)
    }, color, style = Stroke(g * 0.85f, cap = StrokeCap.Round))
    // La rueda debajo, y una perla arriba, sobre el lomo.
    val rueda = Offset(x - r * 0.08f, y + r * 0.66f)
    drawCircle(color, r * 0.17f, rueda, style = Stroke(g * 0.6f))
    for (k in 0 until 4) {
        val a = PI / 4 + k * PI / 2
        drawLine(color, rueda, Offset(rueda.x + r * 0.17f * cos(a).toFloat(), rueda.y + r * 0.17f * sin(a).toFloat()), g * 0.45f)
    }
    drawCircle(color, r * 0.07f, Offset(x - r * 0.1f, y - r * 0.5f))
}

/**
 * Galia: la biblioteca de la casa grande del oppidum, por dentro. Arriba, el techo de paja sobre sus cabrios, que bajan
 * hasta la viga tirante; al fondo, la pared de zarzo y barro con el hogar en medio, el caldero de bronce colgado de su
 * cadena y el humo que sube a la paja; a un lado, el escudo y la espada colgados; al otro, el manto a cuadros y un ramo de
 * muérdago. Los postes de roble sostienen las tablas, clavadas a la pared, y en cada una el mes va tallado en una tablilla
 * de haya clavada con dos clavos de bronce. En las repisas, donde no llegan los haces, lo de la casa: la olla pintada de
 * franjas, el muérdago que se seca colgado y la tea de pino en su soporte de hierro (encendida de noche). Abajo, el umbral
 * de roble y el suelo de tierra con la piel de lobo y la cesta de mimbre. Cada entrada es un haz de tablillas enceradas
 * (recurso: César no dice el material): atado con correa y la borla de lana del color del ánimo, el díptico de roble con su
 * ventanita de cera, o envuelto en un retazo de manto a cuadros. De noche es la misma casa a oscuras, en el verde negro del
 * robledal, y la luz cálida solo llega cerca del fuego.
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
    private val HUMO = TrazosGalia.HUMO
    private val LUNA = TrazosGalia.LUNA
    private val FUEGO = TrazosGalia.FUEGO
    private val FUEGO_HALO = TrazosGalia.FUEGO_HALO
    /** El barro de la pared en sombra, el zarzo que se ve donde se cayó el barro y el fondo de la paja, ennegrecido de humo. */
    private val BARRO_SOMBRA = Color(0xFF8E7448)
    private val ZARZO = Color(0xFF7A5A34)
    private val PAJA_HUMO = Color(0xFF6E5A36)
    /** El roble de las tablas y de las tablillas: su color, su canto en luz y su veta; la haya clara de los rótulos. */
    private val TABLA = Color(0xFF8A6A40)
    private val TABLA_LUZ = Color(0xFFB4905C)
    private val VETA = Color(0xFF4A3220)
    private val HAYA = Color(0xFFD2B88A)
    /** La cera de abeja de las tablillas: el color de la miel, más oscura en los bordes, y el surco que deja el estilo. */
    private val CERA = Color(0xFFD9BE82)
    private val CERA_OSC = Color(0xFFB8975A)
    /** El cuero de las correas y de los estuches, y el lino de los cordeles. */
    private val CUERO = Color(0xFF7A5232)
    private val CUERO_OSC = Color(0xFF4E3420)
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

    /** Un texto grabado: el surco oscuro y, corrido hacia abajo, el labio que levanta la luz. */
    private fun DrawScope.grabado(medidor: TextMeasurer, texto: String, r: Rect, letra: FontFamily, hondo: Color, labio: Color, tam: Float,
                                  peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f, vertical: Boolean = false, minimo: Float = 5f) {
        val u = density
        textoEn(medidor, texto, r.translate(0.45f * u, 0.55f * u), letra, labio, tam, vertical = vertical, peso = peso, espaciado = espaciado, minimo = minimo)
        textoEn(medidor, texto, r, letra, hondo, tam, vertical = vertical, peso = peso, espaciado = espaciado, minimo = minimo)
    }

    /** El mes para un sitio angosto: solo su nombre («Samonios MAT» se vuelve «Samonios»). */
    private fun mesCorto(mes: String): String = mes.substringBefore(" ").substringBefore("·").trim()

    /** El número de un día como lo cuenta el tema: los ordinales galos hasta el 10, números romanos por encima. */
    private fun numeroGalo(dia: String): String = dia.trim().toIntOrNull()?.let { Galia.numero(it) } ?: ""

    /** Cuál de los tres haces es una entrada: 0, atado con correa; 1, el díptico de roble; 2, envuelto en el manto a cuadros. */
    private fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 6)
        return if (k < 3) 0 else if (k < 5) 1 else 2
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
    private fun DrawScope.zarzo(r: Rect, barro: Color, semilla: Int, fuerza: Float = 1f) {
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
        // Donde se cayó el barro: el zarzo de debajo.
        val huecos = (r.width * r.height / (u * u * 4200f)).toInt().coerceIn(1, 7)
        for (k in 0 until huecos) {
            val c = Offset(r.left + r.width * (0.1f + 0.8f * azar(semilla + 8, k)), r.top + r.height * (0.15f + 0.7f * azar(semilla + 9, k)))
            val s = (9f + 9f * azar(semilla + 10, k)) * u
            val q = Rect(c.x - s, c.y - s * 0.55f, c.x + s, c.y + s * 0.55f)
            val hueco = contornoIrregular(q, semilla + 11 + k, 22, 0.2f)
            clipPath(hueco) {
                drawRect(mezcla(ZARZO, Color.Black, 0.55f), q.topLeft, q.size)
                // Las estacas, de arriba abajo, y las varas que pasan por delante y por detrás.
                val paso = 7f * u
                var x = q.left + paso * azar(semilla + 12, k)
                while (x < q.right) { drawLine(mezcla(ZARZO, Color.Black, 0.3f), Offset(x, q.top), Offset(x, q.bottom), 1.8f * u); x += paso }
                var y = q.top + 1f * u; var j = 0
                while (y < q.bottom) {
                    var xx = q.left - paso * (j % 2) * 0.5f; var i = 0
                    while (xx < q.right) {
                        val delante = (i + j) % 2 == 0
                        drawLine(if (delante) ZARZO else mezcla(ZARZO, Color.Black, 0.4f), Offset(xx, y), Offset(xx + paso, y + 0.3f * u), 2.2f * u, StrokeCap.Round)
                        if (delante) drawLine(mezcla(ZARZO, Color.White, 0.25f).copy(alpha = 0.5f), Offset(xx + 1 * u, y - 0.6f * u), Offset(xx + paso - 1 * u, y - 0.4f * u), 0.5f * u)
                        xx += paso; i++
                    }
                    y += 2.6f * u; j++
                }
                drawRect(Color.Black.copy(alpha = 0.22f * fuerza), q.topLeft, q.size)
            }
            translate(0.6f * u, 0.9f * u) { drawPath(hueco, claro.copy(alpha = 0.35f * fuerza), style = Stroke(0.9f * u)) }
            drawPath(hueco, oscuro.copy(alpha = 0.6f), style = Stroke(0.7f * u))
        }
    }

    /** La paja del techo vista por debajo, dentro de [r]: haces cortos, cada fila un poco corrida, más oscura cuanto más arriba. */
    private fun DrawScope.paja(r: Rect, base: Color, semilla: Int) {
        val u = density
        drawRect(base, r.topLeft, r.size)
        val fila = 3.2f * u
        var y = r.top; var j = 0
        var n = 0
        while (y < r.bottom && n < 1400) {
            var x = r.left - 4 * u + 3 * u * azar(semilla, j)
            var i = 0
            while (x < r.right && n < 1400) {
                val l = (4f + 5f * azar(semilla + j, i)) * u
                val col = when (Math.floorMod(i + j * 3, 4)) { 0 -> mezcla(base, Color.White, 0.25f); 1 -> mezcla(base, Color.Black, 0.3f); 2 -> mezcla(base, PAJA, 0.3f); else -> base }
                drawLine(col.copy(alpha = 0.8f), Offset(x, y), Offset(x + l * 0.35f, y + l), 0.7f * u, StrokeCap.Round)
                x += 1.6f * u; i++; n++
            }
            y += fila; j++
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

    /** La onda grabada: el surco y su labio de luz corrido hacia abajo. */
    private fun DrawScope.ondaGrabada(x0: Float, x1: Float, y: Float, alto: Float, hondo: Color, labio: Color, grosor: Float, hueco: Float = 0f) {
        val u = density
        onda(x0 + 0.4f * u, x1 + 0.4f * u, y + 0.5f * u, alto, labio, grosor, hueco)
        onda(x0, x1, y, alto, hondo, grosor, hueco)
    }

    /**
     * Una ramita de muérdago que nace en [c] y crece hacia [angulo] grados (0 = abajo), de largo [largo]: los tallos que se
     * parten de dos en dos, las hojas largas por pares y las bayas blancas en las horquillas.
     */
    private fun DrawScope.muerdago(c: Offset, largo: Float, angulo: Float, hoja: Color = MUERDAGO, baya: Color = BAYA) {
        val u = density
        val tallo = mezcla(hoja, Color(0xFF6E7A3A), 0.5f)
        val bayas = mutableListOf<Offset>()
        fun punta(o: Offset, a: Double, l: Float): Offset = Offset(o.x + l * sin(a).toFloat(), o.y + l * cos(a).toFloat())
        fun hojaEn(o: Offset, a: Double, l: Float) {
            val q = punta(o, a, l)
            val m = punta(o, a, l * 0.5f)
            val n = Offset(cos(a).toFloat(), -sin(a).toFloat()) * (l * 0.2f)
            val p = Path().apply { moveTo(o.x, o.y); quadraticTo(m.x + n.x, m.y + n.y, q.x, q.y); quadraticTo(m.x - n.x, m.y - n.y, o.x, o.y); close() }
            drawPath(p, hoja)
            drawLine(mezcla(hoja, Color.Black, 0.25f).copy(alpha = 0.5f), o, punta(o, a, l * 0.8f), maxOf(0.3f * u, l * 0.03f))
        }
        fun rama(o: Offset, a: Double, l: Float, nivel: Int) {
            val f = punta(o, a, l)
            drawLine(tallo, o, f, maxOf(0.6f * u, l * 0.09f), StrokeCap.Round)
            if (nivel == 0) {
                hojaEn(f, a - 0.4, l * 1.05f); hojaEn(f, a + 0.4, l * 1.05f)
                bayas.add(f)
            } else {
                rama(f, a - 0.55, l * 0.8f, nivel - 1); rama(f, a + 0.55, l * 0.8f, nivel - 1)
                bayas.add(f)
            }
        }
        val a0 = Math.toRadians(angulo.toDouble())
        rama(c, a0, largo * 0.32f, 2)
        bayas.forEach { b ->
            drawCircle(Color.Black.copy(alpha = 0.18f), largo * 0.04f, b + Offset(largo * 0.008f, largo * 0.012f))
            drawCircle(baya, largo * 0.038f, b)
            drawCircle(Color.White.copy(alpha = 0.6f), largo * 0.013f, b - Offset(largo * 0.012f, largo * 0.012f))
        }
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
        caballoGalia(c + Offset(r * 0.03f, r * 0.04f), r * 0.66f, ORO_BRILLO.copy(alpha = 0.7f))
        caballoGalia(c, r * 0.66f, mezcla(oro, Color.Black, 0.45f))
    }

    /** El torques de las figuras compartidas ([TrazosGalia.torques]). */
    private fun DrawScope.torq(c: Offset, r: Float, grueso: Float, oro: Color = ORO, brillo: Color = ORO_BRILLO) {
        with(TrazosGalia) { torques(c, r, grueso, oro, brillo) }
    }

    /** La hoja de roble de las figuras compartidas ([TrazosGalia.hojaRoble]). */
    private fun DrawScope.hoja(c: Offset, largo: Float, angulo: Float, color: Color = ROBLE, nervio: Color = ROBLE_SOMBRA) {
        with(TrazosGalia) { hojaRoble(c, largo, angulo, color, nervio) }
    }

    /** La rueda de bronce (la rouelle de los amuletos), de radio [r], con sus seis rayos y el cubo. */
    private fun DrawScope.rueda(c: Offset, r: Float, color: Color) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.12f, r * 0.16f), style = Stroke(r * 0.22f))
        drawCircle(color, r, c, style = Stroke(r * 0.22f))
        for (k in 0 until 6) {
            val a = k * PI / 3
            drawLine(color, c, Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()), r * 0.14f)
        }
        drawCircle(color, r * 0.26f, c)
        drawCircle(mezcla(color, Color.White, 0.45f).copy(alpha = 0.6f), r, c, style = Stroke(r * 0.06f))
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

    /** Renglones escritos con el estilo en la cera, dentro de [r]: trazos cortos, de grueso [g], cada [paso]. */
    private fun DrawScope.renglones(r: Rect, color: Color, semilla: Int, paso: Float, g: Float, margen: Float = 0f) {
        val u = density
        var y = r.top + paso * 0.7f; var k = 0
        while (y < r.bottom - paso * 0.2f) {
            var x = r.left + margen
            val fin = r.right - margen - r.width * 0.2f * azar(semilla, k)
            var j = 0
            while (x < fin) {
                val l = (2.5f + 9f * azar(semilla + k, j)) * u
                val x2 = minOf(x + l, fin)
                drawLine(color, Offset(x, y + (azar(semilla + 3, j + k) - 0.5f) * 0.5f * u), Offset(x2, y), g, StrokeCap.Round)
                x = x2 + 2.4f * u; j++
            }
            y += paso; k++
        }
    }

    /** El hogar: la solera de barro cocido, el anillo de piedras y la hoguera ([TrazosGalia.hoguera]) con su [llama]. */
    private fun DrawScope.hogar(c: Offset, w: Float, llama: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(c.x - w * 0.95f, c.y - w * 0.12f), Size(w * 1.9f, w * 0.34f))
        drawOval(luz(mezcla(BARRO, Color(0xFF8E4A2A), 0.35f), oscuro, noche, 0.9f), Offset(c.x - w * 0.9f, c.y - w * 0.16f), Size(w * 1.8f, w * 0.3f))
        with(TrazosGalia) { hoguera(Offset(c.x, c.y - w * 0.02f), w * 0.8f, llama, luz(MADERA, oscuro, noche, 1f)) }
        // Las piedras del anillo, delante del fuego.
        for (k in 0 until 7) {
            val a = PI * (0.08 + 0.84 * k / 6.0)
            val p = Offset(c.x - (w * 0.82f * cos(a)).toFloat(), c.y + (w * 0.1f * sin(a)).toFloat())
            val s = (3.4f + 1.6f * azar(17, k)) * u
            drawOval(luz(Color(0xFF8A8478), oscuro, noche, 0.95f), p - Offset(s, s * 0.6f), Size(s * 2, s * 1.2f))
            drawOval(Color.White.copy(alpha = 0.18f), p - Offset(s * 0.7f, s * 0.6f), Size(s * 1.1f, s * 0.5f))
            drawOval(Color.Black.copy(alpha = 0.25f), p - Offset(s, s * 0.6f), Size(s * 2, s * 1.2f), style = Stroke(0.5f * u))
        }
    }

    /**
     * El caldero de bronce, con la boca en [c] y de ancho [ancho]: la panza redonda, el borde vuelto, la franja de remaches,
     * las dos argollas y el verdín; [metal] ya lleva la luz que le toca.
     */
    private fun DrawScope.caldero(c: Offset, ancho: Float, metal: Color) {
        val u = density
        val a = ancho
        val cuerpo = Path().apply {
            moveTo(c.x - a * 0.5f, c.y)
            cubicTo(c.x - a * 0.56f, c.y + a * 0.5f, c.x + a * 0.56f, c.y + a * 0.5f, c.x + a * 0.5f, c.y)
            close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(mezcla(metal, Color.Black, 0.35f), mezcla(metal, Color.White, 0.2f), metal, mezcla(metal, Color.Black, 0.5f)), c.x - a * 0.5f, c.x + a * 0.5f))
        clipPath(cuerpo) {
            mancha(Offset(c.x + a * 0.15f, c.y + a * 0.3f), a * 0.3f, VERDIN.copy(alpha = 0.35f))
            mancha(Offset(c.x - a * 0.3f, c.y + a * 0.15f), a * 0.18f, VERDIN.copy(alpha = 0.3f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)), c.y + a * 0.2f, c.y + a * 0.4f), Offset(c.x - a, c.y), Size(a * 2, a))
        }
        val banda = c.y + a * 0.09f
        drawLine(mezcla(metal, Color.Black, 0.4f), Offset(c.x - a * 0.52f, banda), Offset(c.x + a * 0.52f, banda), 0.8f * u)
        for (k in 0 until 9) drawCircle(mezcla(metal, Color.White, 0.3f).copy(alpha = 0.7f), 0.55f * u, Offset(c.x - a * 0.44f + k * a * 0.11f, banda + 1.2f * u))
        drawOval(mezcla(metal, Color.Black, 0.6f), Offset(c.x - a * 0.5f, c.y - a * 0.06f), Size(a, a * 0.12f))
        drawOval(mezcla(metal, Color.White, 0.25f), Offset(c.x - a * 0.53f, c.y - a * 0.07f), Size(a * 1.06f, a * 0.14f), style = Stroke(1.2f * u))
        listOf(-1f, 1f).forEach { s -> drawCircle(mezcla(metal, Color.Black, 0.3f), a * 0.07f, Offset(c.x + s * a * 0.5f, c.y + a * 0.05f), style = Stroke(1.1f * u)) }
    }

    /** El humo que sube de [desde] hasta [hasta]: velos que ondulan y se abren al subir. */
    private fun DrawScope.humo(desde: Offset, hasta: Float, ancho: Float, color: Color, semilla: Int) {
        val u = density
        val alto = desde.y - hasta
        if (alto <= 0f) return
        for (k in 0 until 4) {
            val p = Path()
            val s = if (k % 2 == 0) 1f else -1f
            p.moveTo(desde.x + (k - 1.5f) * 2 * u, desde.y)
            val pasos = 6
            for (i in 1..pasos) {
                val f = i / pasos.toFloat()
                val x = desde.x + s * ancho * f * (0.3f + 0.4f * azar(semilla + k, i)) * sin(f * 5f + k).toFloat()
                p.lineTo(x, desde.y - alto * f)
            }
            drawPath(p, color.copy(alpha = color.alpha * (0.5f - 0.1f * k)), style = Stroke((6f + 6f * k) * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }

    /** El escudo galo, alto y ovalado, dentro de [r]: las tablas pintadas, el filo de cuero, la espina de madera y el umbo de hierro. */
    private fun DrawScope.escudo(r: Rect, pintura: Color, hierro: Color) {
        val u = density
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(r.width * 0.48f, r.height * 0.3f))) }
        translate(1.5f * u, 2.2f * u) { drawPath(forma, Color.Black.copy(alpha = 0.35f)) }
        drawPath(forma, Brush.horizontalGradient(listOf(mezcla(pintura, Color.White, 0.12f), pintura, mezcla(pintura, Color.Black, 0.3f)), r.left, r.right))
        clipPath(forma) {
            var x = r.left + r.width / 6
            while (x < r.right) { drawLine(Color.Black.copy(alpha = 0.12f), Offset(x, r.top), Offset(x, r.bottom), 0.6f * u); x += r.width / 6 }
            // Una espiral pintada en cada mitad, gastada.
            listOf(0.28f, 0.72f).forEach { f ->
                drawArc(mezcla(pintura, Color(0xFFE8DCC0), 0.5f).copy(alpha = 0.45f), 30f, 280f, false, Offset(r.center.x - r.width * 0.18f, r.top + r.height * f - r.width * 0.18f),
                    Size(r.width * 0.36f, r.width * 0.36f), style = Stroke(1.2f * u))
            }
        }
        drawPath(forma, mezcla(CUERO_OSC, pintura, 0.2f), style = Stroke(2.2f * u))
        // La espina y el umbo.
        val esp = Rect(r.center.x - r.width * 0.07f, r.top + r.height * 0.06f, r.center.x + r.width * 0.07f, r.bottom - r.height * 0.06f)
        drawRoundRect(mezcla(TABLA, pintura, 0.25f), esp.topLeft, esp.size, CornerRadius(esp.width / 2))
        drawLine(Color.White.copy(alpha = 0.2f), Offset(esp.left + 1 * u, esp.top + 2 * u), Offset(esp.left + 1 * u, esp.bottom - 2 * u), 0.6f * u)
        val um = Rect(r.center.x - r.width * 0.2f, r.center.y - r.height * 0.1f, r.center.x + r.width * 0.2f, r.center.y + r.height * 0.1f)
        drawOval(Color.Black.copy(alpha = 0.3f), um.topLeft + Offset(1 * u, 1.5f * u), um.size)
        drawOval(Brush.radialGradient(listOf(mezcla(hierro, Color.White, 0.35f), hierro, mezcla(hierro, Color.Black, 0.4f)), center = Offset(um.center.x - um.width * 0.2f, um.center.y - um.height * 0.2f), radius = um.width), um.topLeft, um.size)
        listOf(um.left + 1.5f * u, um.right - 1.5f * u).forEach { x -> drawCircle(mezcla(hierro, Color.White, 0.3f), 0.8f * u, Offset(x, um.center.y)) }
    }

    /** La espada en su vaina, de [a] (el pomo) a [b] (la contera): la empuñadura de puño curvo, la vaina de hierro y la contera de bronce. */
    private fun DrawScope.espada(a: Offset, b: Offset, hierro: Color, bronce: Color, g: Float) {
        val u = density
        val dx = b.x - a.x; val dy = b.y - a.y
        val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.01f)
        val d = Offset(dx / l, dy / l)
        val n = Offset(-d.y, d.x)
        val guarda = a + d * (l * 0.16f)
        drawLine(Color.Black.copy(alpha = 0.3f), guarda + Offset(1 * u, 1.5f * u), b + Offset(1 * u, 1.5f * u), g * 1.1f, StrokeCap.Round)
        drawLine(mezcla(hierro, Color.Black, 0.2f), guarda, b, g, StrokeCap.Round)
        drawLine(mezcla(hierro, Color.White, 0.35f).copy(alpha = 0.5f), guarda + n * (g * 0.25f), b + n * (g * 0.25f), 0.5f * u)
        drawLine(CUERO_OSC, a, guarda, g * 0.6f, StrokeCap.Round)
        drawLine(bronce, guarda - n * (g * 1.1f), guarda + n * (g * 1.1f), g * 0.45f, StrokeCap.Round)
        drawCircle(bronce, g * 0.6f, a)
        drawLine(bronce, b - d * (g * 2.4f), b, g * 1.05f, StrokeCap.Round)
    }

    /** El manto de lana a cuadros colgado de un clavo en [arriba], de ancho [ancho] y largo [largo]: la caída, los pliegues y el fleco. */
    private fun DrawScope.manto(arriba: Offset, ancho: Float, largo: Float, oscuro: Boolean, noche: Boolean, cerca: Float) {
        val u = density
        val base = luz(LANAS[0], oscuro, noche, cerca)
        val forma = Path().apply {
            moveTo(arriba.x - ancho * 0.12f, arriba.y)
            lineTo(arriba.x + ancho * 0.12f, arriba.y)
            cubicTo(arriba.x + ancho * 0.45f, arriba.y + largo * 0.3f, arriba.x + ancho * 0.5f, arriba.y + largo * 0.7f, arriba.x + ancho * 0.46f, arriba.y + largo)
            lineTo(arriba.x - ancho * 0.48f, arriba.y + largo * 0.94f)
            cubicTo(arriba.x - ancho * 0.52f, arriba.y + largo * 0.6f, arriba.x - ancho * 0.4f, arriba.y + largo * 0.25f, arriba.x - ancho * 0.12f, arriba.y)
            close()
        }
        translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, base)
        clipPath(forma) {
            val paso = 7f * u
            var x = arriba.x - ancho; var k = 0
            while (x < arriba.x + ancho) {
                drawLine(luz(LANAS[1 + k % 2], oscuro, noche, cerca).copy(alpha = 0.7f), Offset(x, arriba.y), Offset(x + ancho * 0.08f, arriba.y + largo), if (k % 2 == 0) 2.2f * u else 1f * u)
                x += paso; k++
            }
            var y = arriba.y + paso * 0.6f; k = 0
            while (y < arriba.y + largo) {
                drawLine(luz(LANAS[1 + k % 2], oscuro, noche, cerca).copy(alpha = 0.55f), Offset(arriba.x - ancho, y), Offset(arriba.x + ancho, y), if (k % 2 == 0) 2.2f * u else 1f * u)
                y += paso; k++
            }
            // Los pliegues: bandas de sombra que bajan del clavo.
            listOf(-0.28f, 0.02f, 0.3f).forEach { f ->
                drawLine(Color.Black.copy(alpha = 0.22f), Offset(arriba.x + ancho * f * 0.3f, arriba.y), Offset(arriba.x + ancho * f, arriba.y + largo), 3.5f * u)
                drawLine(Color.White.copy(alpha = 0.1f), Offset(arriba.x + ancho * f * 0.3f + 3 * u, arriba.y), Offset(arriba.x + ancho * f + 3 * u, arriba.y + largo), 1.6f * u)
            }
        }
        // El fleco.
        var x = arriba.x - ancho * 0.46f
        while (x < arriba.x + ancho * 0.44f) {
            val y0 = arriba.y + largo * (0.94f + 0.06f * (x - (arriba.x - ancho * 0.48f)) / (ancho * 0.94f))
            drawLine(base, Offset(x, y0), Offset(x - 0.5f * u, y0 + 4 * u), 0.8f * u, StrokeCap.Round)
            x += 2.2f * u
        }
        drawCircle(luz(HIERRO, oscuro, noche, cerca), 1.8f * u, arriba)
    }

    /** La olla de barro pintada a franjas rojas y blancas (la cerámica pintada de los arvernos), parada en [pie], de alto [alto]. */
    private fun DrawScope.olla(pie: Offset, alto: Float, barro: Color, oscuro: Boolean) {
        val u = density
        val a = alto; val x = pie.x; val y = pie.y
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(x - a * 0.4f, y - a * 0.05f), Size(a * 0.8f, a * 0.1f))
        val cuerpo = Path().apply {
            moveTo(x - a * 0.16f, y)
            lineTo(x - a * 0.2f, y - a * 0.08f)
            cubicTo(x - a * 0.48f, y - a * 0.3f, x - a * 0.46f, y - a * 0.7f, x - a * 0.22f, y - a * 0.84f)
            lineTo(x - a * 0.26f, y - a * 0.96f)
            lineTo(x + a * 0.26f, y - a * 0.96f)
            lineTo(x + a * 0.22f, y - a * 0.84f)
            cubicTo(x + a * 0.46f, y - a * 0.7f, x + a * 0.48f, y - a * 0.3f, x + a * 0.2f, y - a * 0.08f)
            lineTo(x + a * 0.16f, y)
            close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(mezcla(barro, Color.Black, 0.3f), mezcla(barro, Color.White, 0.15f), barro, mezcla(barro, Color.Black, 0.4f)), x - a * 0.47f, x + a * 0.47f))
        clipPath(cuerpo) {
            val rojo = if (oscuro) mezcla(Color(0xFF9A3A22), NOCHE, 0.6f) else Color(0xFF9A3A22)
            val blanco = if (oscuro) mezcla(Color(0xFFEDE3CC), NOCHE, 0.6f) else Color(0xFFEDE3CC)
            drawRect(rojo.copy(alpha = 0.8f), Offset(x - a, y - a * 0.62f), Size(a * 2, a * 0.16f))
            drawRect(blanco.copy(alpha = 0.7f), Offset(x - a, y - a * 0.46f), Size(a * 2, a * 0.05f))
            // La franja de arriba, con su zigzag pintado.
            val zz = Path().apply {
                var xx = x - a * 0.5f; moveTo(xx, y - a * 0.66f); var arriba = true
                while (xx < x + a * 0.5f) { xx += a * 0.06f; lineTo(xx, if (arriba) y - a * 0.74f else y - a * 0.66f); arriba = !arriba }
            }
            drawPath(zz, blanco.copy(alpha = 0.75f), style = Stroke(0.8f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)), x, x + a * 0.5f), Offset(x, y - a), Size(a * 0.5f, a))
        }
        drawOval(mezcla(barro, Color.Black, 0.55f), Offset(x - a * 0.25f, y - a * 0.99f), Size(a * 0.5f, a * 0.07f))
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

    /** La piel de lobo tendida en el suelo, dentro de [r]: el contorno irregular, el pelo en mechones y la cabeza a un lado. */
    private fun DrawScope.piel(r: Rect, color: Color, semilla: Int) {
        val u = density
        val forma = contornoIrregular(r, semilla, 30, 0.18f)
        translate(1 * u, 1.5f * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, Brush.radialGradient(listOf(mezcla(color, Color.White, 0.15f), color, mezcla(color, Color.Black, 0.3f)), center = r.center, radius = r.width * 0.6f))
        clipPath(forma) {
            for (k in 0 until 90) {
                val o = Offset(r.left + r.width * azar(semilla, k), r.top + r.height * azar(semilla + 1, k))
                drawLine((if (k % 3 == 0) Color.White else Color.Black).copy(alpha = 0.16f), o, o + Offset(2.5f * u, 1.5f * u * (azar(semilla + 2, k) - 0.3f)), 0.6f * u, StrokeCap.Round)
            }
            drawLine(mezcla(color, Color.Black, 0.4f).copy(alpha = 0.5f), Offset(r.left + r.width * 0.15f, r.center.y), Offset(r.right - r.width * 0.12f, r.center.y), 2.6f * u, StrokeCap.Round)
        }
    }

    /** La cesta de mimbre parada en [pie], de ancho [ancho] y alto [alto]: el tejido, el borde trenzado y las manzanas dentro. */
    private fun DrawScope.cesta(pie: Offset, ancho: Float, alto: Float, mimbre: Color, dentro: Color?) {
        val u = density
        val forma = Path().apply {
            moveTo(pie.x - ancho * 0.4f, pie.y); lineTo(pie.x - ancho * 0.5f, pie.y - alto); lineTo(pie.x + ancho * 0.5f, pie.y - alto); lineTo(pie.x + ancho * 0.4f, pie.y); close()
        }
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(pie.x - ancho * 0.48f, pie.y - 2 * u), Size(ancho * 0.96f, 4 * u))
        if (dentro != null) for (k in 0 until 3) {
            val c = Offset(pie.x + ancho * (-0.24f + 0.24f * k), pie.y - alto - ancho * 0.06f + (if (k == 1) -ancho * 0.05f else 0f))
            drawCircle(dentro, ancho * 0.13f, c)
            drawCircle(Color.White.copy(alpha = 0.25f), ancho * 0.04f, c - Offset(ancho * 0.04f, ancho * 0.04f))
        }
        drawPath(forma, Brush.horizontalGradient(listOf(mezcla(mimbre, Color.Black, 0.2f), mezcla(mimbre, Color.White, 0.12f), mezcla(mimbre, Color.Black, 0.35f)), pie.x - ancho / 2, pie.x + ancho / 2))
        clipPath(forma) {
            var y = pie.y - alto + 2 * u; var j = 0
            while (y < pie.y) {
                var x = pie.x - ancho * 0.5f + (j % 2) * 2.5f * u
                while (x < pie.x + ancho * 0.5f) { drawOval(Color.Black.copy(alpha = 0.18f), Offset(x, y - 1 * u), Size(4 * u, 2.2f * u), style = Stroke(0.5f * u)); x += 5 * u }
                y += 2.6f * u; j++
            }
        }
        drawLine(mezcla(mimbre, Color.Black, 0.3f), Offset(pie.x - ancho * 0.52f, pie.y - alto), Offset(pie.x + ancho * 0.52f, pie.y - alto), 2.4f * u, StrokeCap.Round)
        drawLine(mezcla(mimbre, Color.White, 0.25f), Offset(pie.x - ancho * 0.5f, pie.y - alto - 0.6f * u), Offset(pie.x + ancho * 0.5f, pie.y - alto - 0.6f * u), 0.6f * u)
    }

    // =================================================================
    //  El mueble: la casa grande del oppidum
    // =================================================================

    override val corona = 0.48f
    override val base = 0.14f
    override val lado = 0.06f
    override val altoRepisa: Dp = 108.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = TITULO

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 45).coerceAtMost(16)).dp
    override fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.8f + 0.1f * azar(semilla, 4)
        1 -> 0.86f + 0.1f * azar(semilla, 4)
        else -> 0.7f + 0.1f * azar(semilla, 4)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        val lumbre = oscuro || noche
        val teas = mutableListOf<Offset>()
        // La pared del fondo, de zarzo y barro, de arriba abajo, y los dos postes de roble que sostienen la casa.
        zarzo(Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, h - d.base.height), c(BARRO_SOMBRA), 31, 0.8f)
        // Cada repisa: la pared en sombra bajo la tabla de arriba y, en la punta de la derecha, lo que se guarda en la casa.
        d.repisas.forEachIndexed { k, r ->
            clipRect(r.left, r.top, r.right, r.bottom) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (lumbre) 0.55f else 0.38f), Color.Transparent), r.top, r.top + 30 * u), r.topLeft, Size(r.width, 30 * u))
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), r.left, r.left + 12 * u), r.topLeft, Size(12 * u, r.height))
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f)), r.right - 12 * u, r.right), Offset(r.right - 12 * u, r.top), Size(12 * u, r.height))
                when (k % 3) {
                    0 -> olla(Offset(r.right - 22 * u, r.bottom), 34 * u, luz(Color(0xFFB98A5E), oscuro, noche, 0.3f), lumbre)
                    1 -> {
                        // El muérdago que se seca, colgado de la tabla de arriba.
                        val o = Offset(r.right - 26 * u, r.top + 2 * u)
                        cordel(o, o + Offset(0f, 12 * u), c(LINO), 0.9f * u)
                        muerdago(o + Offset(0f, 12 * u), 40 * u, 0f, c(mezcla(MUERDAGO, PAJA, 0.35f)), c(BAYA))
                    }
                    else -> {
                        val p = Offset(r.right - 18 * u, r.top + r.height * 0.42f)
                        tea(p, 30 * u, lumbre, oscuro, noche)
                        if (lumbre) teas.add(p + Offset(-3 * u, -14 * u))
                    }
                }
            }
        }
        // Los postes de roble, de arriba abajo, con la corteza en las orillas.
        listOf(Rect(0f, 0f, d.izquierda.right, h - d.base.height), Rect(d.derecha.left, 0f, w, h - d.base.height)).forEachIndexed { j, p ->
            madera(p, luz(MADERA, oscuro, noche, 0.25f), 61 + j * 5, vertical = true, nudos = 3)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.White.copy(alpha = 0.1f), Color.Transparent, Color.Black.copy(alpha = 0.4f)), p.left, p.right), p.topLeft, p.size)
        }
        // En el poste de la izquierda, un gancho de hierro por nivel y, en uno sí y otro no, la rueda de bronce colgada; en el de
        // la derecha, las muescas de las noches contadas, de cinco en cinco.
        d.repisas.forEachIndexed { k, rep ->
            val p = Offset(d.izquierda.center.x, rep.top + rep.height * 0.28f)
            drawCircle(Color.Black.copy(alpha = 0.35f), 1.8f * u, p + Offset(0.6f * u, 0.8f * u))
            drawCircle(c(HIERRO), 1.6f * u, p)
            if (k % 2 == 1) {
                cordel(Path().apply { moveTo(p.x, p.y); quadraticTo(p.x - 2 * u, p.y + 6 * u, p.x, p.y + 11 * u) }, c(CORREA), 0.9f * u)
                rueda(Offset(p.x, p.y + 16 * u), minOf(d.izquierda.width * 0.32f, 5 * u), luz(BRONCE, oscuro, noche, 0.2f))
            }
            val x0 = d.derecha.left + d.derecha.width * 0.3f
            val y0 = rep.top + rep.height * 0.6f
            for (g in 0 until 1 + k % 3) {
                for (i in 0 until 4) drawLine(Color.Black.copy(alpha = 0.35f), Offset(x0 + i * 1.6f * u, y0 + g * 12 * u), Offset(x0 + i * 1.6f * u, y0 + g * 12 * u + 7 * u), 0.7f * u)
                drawLine(Color.Black.copy(alpha = 0.35f), Offset(x0 - 1 * u, y0 + g * 12 * u + 6 * u), Offset(x0 + 6.5f * u, y0 + g * 12 * u + 1 * u), 0.7f * u)
            }
        }
        // Las tablas: roble, con su veta y sus nudos, clavadas a los postes con clavijas.
        d.tablas.forEachIndexed { k, r ->
            val q = Rect(r.left - 3 * u, r.top, r.right + 3 * u, r.bottom)
            madera(q, luz(TABLA, oscuro, noche, 0.35f), 71 + k * 5, nudos = 2)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (lumbre) 0.06f else 0.22f), Color.Transparent, Color.Black.copy(alpha = 0.25f)), q.top, q.bottom), q.topLeft, q.size)
            drawRect(Color.Black.copy(alpha = 0.36f), Offset(q.left, q.bottom), Size(q.width, 3.5f * u))
            listOf(r.left + 5 * u, r.right - 5 * u).forEach { x ->
                drawCircle(c(VETA), 2 * u, Offset(x, r.center.y))
                drawCircle(Color.White.copy(alpha = 0.15f), 0.7f * u, Offset(x - 0.6f * u, r.center.y - 0.6f * u))
            }
        }
        // El rótulo de cada mes: una tablilla de haya clavada a la tabla con dos clavos de bronce, el nombre tallado.
        d.placas.forEach { p ->
            val r = p.r
            val ancho = (anchoTx(medidor, p.texto, TITULO, 9.5f, 0.3f, GRUESO) + 26 * u).coerceIn(r.width * 0.45f, r.width)
            val pl = Rect(r.center.x - ancho / 2, r.top - 1 * u, r.center.x + ancho / 2, r.bottom + 1 * u)
            drawRect(Color.Black.copy(alpha = 0.32f), pl.topLeft + Offset(0.8f * u, 1.2f * u), pl.size)
            madera(pl, luz(HAYA, oscuro, noche, 0.4f), p.texto.length * 7 + 3, nudos = 0)
            drawRect(Color.White.copy(alpha = 0.2f), pl.topLeft, Size(pl.width, 0.8f * u))
            drawRect(Color.Black.copy(alpha = 0.3f), pl.topLeft, pl.size, style = Stroke(0.5f * u))
            listOf(pl.left + 5 * u, pl.right - 5 * u).forEach { x ->
                drawCircle(Color.Black.copy(alpha = 0.35f), 1.8f * u, Offset(x + 0.4f * u, pl.center.y + 0.5f * u))
                drawCircle(luz(BRONCE, oscuro, noche, 0.5f), 1.6f * u, Offset(x, pl.center.y))
                drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(x - 0.5f * u, pl.center.y - 0.5f * u))
            }
            val madera = luz(HAYA, oscuro, noche, 0.4f)
            grabado(medidor, p.texto, Rect(pl.left + 10 * u, pl.top, pl.right - 10 * u, pl.bottom), TITULO, mezcla(madera, Color.Black, 0.62f),
                mezcla(madera, Color.White, 0.4f).copy(alpha = 0.6f), 9.5f, peso = GRUESO, espaciado = 0.3f)
        }
        val fuego = corona(d.corona, w * lado, oscuro, noche)
        base(d.base, oscuro, noche)
        // La luz: de día entra por la puerta, a la izquierda, y el hogar entibia el fondo; de noche, solo el fuego del hogar y
        // las teas alumbran, y lo demás se queda en el verde negro del robledal.
        if (!lumbre) {
            drawRect(Brush.horizontalGradient(listOf(Color(0xFFFFF2D0).copy(alpha = 0.16f), Color.Transparent, Color.Black.copy(alpha = 0.08f)), 0f, w), Offset.Zero, Size(w, h))
            mancha(fuego, w * 0.5f, FUEGO_HALO.copy(alpha = 0.18f))
        } else {
            resplandor(fuego, w * 0.62f, if (oscuro) 1f else 0.65f)
            teas.forEach { resplandor(it, w * 0.36f, if (oscuro) 0.8f else 0.5f) }
        }
    }

    /**
     * La corona: el fondo de la casa grande. Arriba, el techo de paja visto por debajo, con los cabrios que bajan hacia la viga
     * tirante y las varas atadas que los cruzan, ennegrecido de humo en lo alto; abajo, la pared de zarzo con el hogar en medio,
     * el caldero colgado de su cadena sobre el fuego y el humo que sube; a la izquierda, el escudo y la espada; a la derecha, el
     * manto a cuadros y el ramo de muérdago colgado de la viga. Devuelve dónde está el fuego.
     */
    private fun DrawScope.corona(r: Rect, lado: Float, oscuro: Boolean, noche: Boolean): Offset {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val lumbre = oscuro || noche
        val hh = r.height
        val izq = r.left + lado; val der = r.right - lado
        val cx = r.center.x
        val yViga = r.top + hh * 0.4f
        val gViga = 10 * u
        // El techo: la paja, los cabrios y las varas.
        val techo = Rect(izq, r.top, der, yViga)
        clipRect(techo.left, techo.top, techo.right, techo.bottom) {
            paja(techo, luz(mezcla(PAJA, PAJA_HUMO, 0.45f), oscuro, noche, 0.3f), 101)
            val cumbre = Offset(cx, r.top - hh * 0.55f)
            var y = r.top + 5 * u; var k = 0
            while (y < yViga) {
                val p = Path().apply { moveTo(izq, y + 2 * u); quadraticTo(cx, y + 6 * u, der, y + 2 * u) }
                drawPath(p, c(mezcla(ZARZO, Color.Black, 0.2f)), style = Stroke(1.6f * u))
                drawPath(p, Color.White.copy(alpha = 0.08f), style = Stroke(0.5f * u))
                y += 11 * u; k++
            }
            for (k2 in 0..8) {
                val x = izq - (der - izq) * 0.15f + (der - izq) * 1.3f * k2 / 8f
                drawLine(Color.Black.copy(alpha = 0.35f), cumbre + Offset(1.5f * u, 0f), Offset(x + 2 * u, yViga), 5.5f * u)
                drawLine(luz(MADERA, oscuro, noche, 0.3f), cumbre, Offset(x, yViga), 4.5f * u)
                drawLine(Color.White.copy(alpha = if (lumbre) 0.05f else 0.14f), cumbre + Offset(-1.2f * u, 0f), Offset(x - 1.2f * u, yViga), 0.8f * u)
            }
            // El hollín de tantos inviernos, más negro arriba.
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (lumbre) 0.6f else 0.5f), Color.Black.copy(alpha = 0.08f)), r.top, yViga), techo.topLeft, techo.size)
        }
        // La pared del fondo.
        val pared = Rect(izq, yViga + gViga, der, r.bottom)
        clipRect(pared.left, pared.top, pared.right, pared.bottom) {
            zarzo(pared, luz(BARRO, oscuro, noche, 0.4f), 113, 0.9f)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), pared.top, pared.top + 18 * u), pared.topLeft, Size(pared.width, 18 * u))
        }
        val fuego = Offset(cx, r.bottom - 6 * u)
        val ancho = minOf((der - izq) * 0.11f, 30 * u)
        // El humo que sube del caldero hasta la paja.
        humo(Offset(cx, fuego.y - ancho * 1.8f), r.top + hh * 0.05f, ancho * 0.9f, if (lumbre) mezcla(HUMO, NOCHE, 0.55f).copy(alpha = 0.35f) else HUMO.copy(alpha = 0.32f), 7)
        // El escudo y la espada, colgados a la izquierda.
        val altoPared = pared.height
        val escR = Rect(izq + (cx - izq) * 0.3f, pared.top + altoPared * 0.12f, izq + (cx - izq) * 0.3f + altoPared * 0.42f, pared.top + altoPared * 0.88f)
        espada(Offset(escR.left - altoPared * 0.05f, escR.top + altoPared * 0.02f), Offset(escR.right + altoPared * 0.2f, escR.bottom + altoPared * 0.04f), luz(HIERRO, oscuro, noche, 0.45f), luz(BRONCE, oscuro, noche, 0.5f), 3.2f * u)
        escudo(escR, luz(mezcla(LANAS[0], BARRO, 0.35f), oscuro, noche, 0.5f), luz(HIERRO, oscuro, noche, 0.55f))
        // El manto a cuadros y el ramo de muérdago, a la derecha.
        manto(Offset(cx + (der - cx) * 0.62f, pared.top + altoPared * 0.1f), (der - cx) * 0.36f, altoPared * 0.8f, oscuro, noche, 0.45f)
        val mu = Offset(cx + (der - cx) * 0.3f, yViga + gViga)
        cordel(mu, mu + Offset(0f, altoPared * 0.18f), c(LINO), 0.9f * u)
        muerdago(mu + Offset(0f, altoPared * 0.18f), altoPared * 0.42f, 0f, luz(MUERDAGO, oscuro, noche, 0.55f), luz(BAYA, oscuro, noche, 0.6f))
        // La viga tirante, de poste a poste.
        val viga = Rect(r.left, yViga, r.right, yViga + gViga)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(viga.left, viga.bottom), Size(viga.width, 4 * u))
        madera(viga, luz(MADERA, oscuro, noche, 0.35f), 127, nudos = 3)
        drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (lumbre) 0.05f else 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.3f)), viga.top, viga.bottom), viga.topLeft, viga.size)
        // El hogar, el caldero sobre el fuego y la cadena que baja de la viga.
        hogar(fuego, ancho, if (lumbre) 1f else 0.55f, oscuro, noche)
        val boca = Offset(cx, fuego.y - ancho * 1.25f)
        val cadena = luz(HIERRO, oscuro, noche, 0.6f)
        var y = viga.bottom; var k = 0
        while (y < boca.y - ancho * 0.1f) {
            if (k % 2 == 0) drawOval(cadena, Offset(cx - 1.4f * u, y), Size(2.8f * u, 4.2f * u), style = Stroke(0.8f * u))
            else drawLine(cadena, Offset(cx, y), Offset(cx, y + 4.2f * u), 1f * u)
            y += 3.6f * u; k++
        }
        listOf(-1f, 1f).forEach { s -> drawLine(cadena, Offset(cx, boca.y - ancho * 0.18f), Offset(cx + s * ancho * 0.5f, boca.y + ancho * 0.05f), 0.8f * u) }
        caldero(boca, ancho * 1.1f, luz(BRONCE, oscuro, noche, 0.95f))
        // Las lenguas de fuego que asoman por los lados del caldero.
        if (lumbre) {
            mancha(Offset(cx, fuego.y - ancho * 0.6f), ancho * 1.1f, FUEGO.copy(alpha = 0.35f))
        }
        return Offset(cx, fuego.y - ancho * 0.6f)
    }

    /** La base: el umbral de roble y, abajo, el suelo de tierra apisonada con paja, la piel de lobo, la cesta de mimbre y un leño. */
    private fun DrawScope.base(b: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val lumbre = oscuro || noche
        val umbral = b.top + b.height * 0.32f
        madera(Rect(b.left, b.top, b.right, umbral), luz(MADERA, oscuro, noche, 0.25f), 141, nudos = 2)
        drawRect(Color.White.copy(alpha = if (lumbre) 0.05f else 0.2f), b.topLeft, Size(b.width, 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(b.left, umbral), Size(b.width, 2.4f * u))
        val suelo = Rect(b.left, umbral, b.right, b.bottom)
        val tierra = c(Color(0xFF7A6446))
        drawRect(Brush.verticalGradient(listOf(mezcla(tierra, Color.Black, 0.3f), tierra), suelo.top, suelo.bottom), suelo.topLeft, suelo.size)
        for (k in 0 until 70) {
            val o = Offset(suelo.left + suelo.width * azar(143, k), suelo.top + suelo.height * azar(145, k))
            if (k % 3 == 0) drawLine(c(PAJA).copy(alpha = 0.45f), o, o + Offset((3f + 4f * azar(147, k)) * u, (azar(149, k) - 0.5f) * 2 * u), 0.6f * u)
            else drawCircle(mezcla(tierra, if (k % 2 == 0) Color.White else Color.Black, 0.3f).copy(alpha = 0.4f), (0.3f + 0.6f * azar(151, k)) * u, o)
        }
        piel(Rect(b.left + b.width * 0.08f, suelo.top + suelo.height * 0.18f, b.left + b.width * 0.5f, b.bottom + 3 * u), c(Color(0xFF7D7366)), 153)
        val leño = Offset(b.left + b.width * 0.58f, suelo.top + suelo.height * 0.62f)
        drawLine(Color.Black.copy(alpha = 0.3f), leño + Offset(1 * u, 2 * u), leño + Offset(30 * u, 0f), 7 * u, StrokeCap.Round)
        drawLine(c(MADERA), leño, leño + Offset(29 * u, -2 * u), 6.5f * u, StrokeCap.Round)
        drawCircle(c(mezcla(TABLA, PAJA, 0.4f)), 3.1f * u, leño + Offset(29 * u, -2 * u))
        drawCircle(c(VETA), 3.1f * u, leño + Offset(29 * u, -2 * u), style = Stroke(0.5f * u))
        cesta(Offset(b.left + b.width * 0.84f, b.bottom - 3 * u), 34 * u, suelo.height * 0.62f, c(Color(0xFFB08A50)), c(Color(0xFF9A3A22)))
    }
