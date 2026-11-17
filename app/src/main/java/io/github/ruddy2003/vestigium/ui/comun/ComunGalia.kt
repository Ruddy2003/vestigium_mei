package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TrazosGalia
import io.github.ruddy2003.vestigium.ui.biblioteca.textoEn
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
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
 * Las rayas de los renglones de un campo de varios renglones, de abajo arriba: la primera ([k] = 0) al pie del último
 * renglón, a [margenY] del canto de abajo, y luego una cada 24 sp mientras quede sitio arriba (16 dp bajo [margenY]).
 */
private inline fun DrawScope.renglonesGal(margenY: Dp, raya: (k: Int, y: Float) -> Unit) {
    val paso = maxOf(24.sp.toPx(), 1f)
    val tope = margenY.toPx() + 16.dp.toPx()
    var y = size.height - margenY.toPx() + 1.dp.toPx()
    var k = 0
    while (y > tope && k < 200) { raya(k, y); y -= paso; k++ }
}

/** La razón de contraste entre dos colores opacos (WCAG 2): de 1 (iguales) a 21 (negro sobre blanco). */
private fun contrasteGal(a: Color, b: Color): Float {
    val la = a.luminance(); val lb = b.luminance()
    return (maxOf(la, lb) + 0.05f) / (minOf(la, lb) + 0.05f)
}

/**
 * El color [c] de un texto, tal cual si se lee sobre [fondo] (contraste de al menos [minimo]:1); si no, llevado poco a poco
 * hacia el verde negro del roble (sobre un fondo claro) o hacia la cera a la luz del fuego (sobre uno oscuro) hasta que se
 * lea. Con [tenue] se apaga antes hacia el fondo, lo más que deja ese contraste.
 */
private fun legibleGal(c: Color, fondo: Color, tenue: Boolean = false, minimo: Float = 4.6f): Color {
    val opaco = c.copy(alpha = 1f)
    if (tenue) for (k in 8 downTo 1) {
        val m = mezcla(opaco, fondo, k * 0.05f)
        if (contrasteGal(m, fondo) >= minimo) return m
    }
    if (contrasteGal(opaco, fondo) >= minimo) return opaco
    val hacia = if (fondo.luminance() > 0.18f) Color(0xFF14190F) else Color(0xFFF3E4C4)
    for (k in 1..20) {
        val m = mezcla(opaco, hacia, k * 0.05f)
        if (contrasteGal(m, fondo) >= minimo) return m
    }
    return hacia
}

/** El ancho, en píxeles, de [texto] en una sola línea con el mismo estilo con que lo dibuja [textoEn]. */
private fun anchoGal(medidor: TextMeasurer, texto: String, letra: FontFamily, tam: Float, peso: FontWeight = FontWeight.Normal, espaciado: Float = 0f): Float =
    if (texto.isBlank()) 0f
    else medidor.measure(texto, TextStyle(fontFamily = letra, fontSize = tam.sp, lineHeight = (tam * 1.05f).sp, fontWeight = peso, letterSpacing = espaciado.sp),
        maxLines = 1, softWrap = false).size.width.toFloat()

/** Una curva cerrada y suave que pasa cerca de [p] (por los puntos medios de cada lado): el barro alisado a mano, el oro batido. */
private fun curvaGal(p: List<Offset>): Path = Path().apply {
    val n = p.size
    if (n < 3) return@apply
    fun medio(a: Offset, b: Offset) = Offset((a.x + b.x) / 2, (a.y + b.y) / 2)
    val m0 = medio(p[0], p[1])
    moveTo(m0.x, m0.y)
    for (k in 1..n) {
        val a = p[k % n]; val b = medio(a, p[(k + 1) % n])
        quadraticTo(a.x, a.y, b.x, b.y)
    }
    close()
}

/** Los puntos de una curva de Bézier cúbica (de [a] a [d], con los controles [b] y [c]), en [n] tramos. */
private fun bezierGal(a: Offset, b: Offset, c: Offset, d: Offset, n: Int): List<Offset> = List(n + 1) { i ->
    val t = i / n.toFloat(); val s = 1f - t
    Offset(s * s * s * a.x + 3 * s * s * t * b.x + 3 * s * t * t * c.x + t * t * t * d.x,
        s * s * s * a.y + 3 * s * s * t * b.y + 3 * s * t * t * c.y + t * t * t * d.y)
}

/** Una cinta de ancho variable a lo largo de [pts]: el ancho en cada punto lo da [ancho] (de 0 a 1 a lo largo). */
private fun cintaGal(pts: List<Offset>, ancho: (Float) -> Float): Path {
    val n = pts.size
    val izq = ArrayList<Offset>(n); val der = ArrayList<Offset>(n)
    for (i in 0 until n) {
        val a = pts[maxOf(i - 1, 0)]; val b = pts[minOf(i + 1, n - 1)]
        val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.0001f)
        val w = ancho(i / (n - 1).coerceAtLeast(1).toFloat()) / 2
        izq.add(Offset(pts[i].x - dy / l * w, pts[i].y + dx / l * w)); der.add(Offset(pts[i].x + dy / l * w, pts[i].y - dx / l * w))
    }
    return Path().apply {
        moveTo(izq[0].x, izq[0].y); for (k in 1 until n) lineTo(izq[k].x, izq[k].y)
        for (k in n - 1 downTo 0) lineTo(der[k].x, der[k].y)
        close()
    }
}

/**
 * Galia: las piezas comunes de la casa grande de un oppidum arverno en el 52 a. C. Todo es de la Edad del Hierro: la tablilla
 * de cera en su marco de roble, la pared de barro con su paja sobre el zarzo, el tablado y los postes de roble, el estátero de
 * oro con su caballo, la fíbula de bronce que cierra el manto, la lana a cuadros, la punta de lanza, el caldero, la cuenta a
 * muescas, la placa de bronce del calendario y, en la hoja rápida, la meseta del oppidum con su empalizada y su muralla. El
 * campo es la tablilla de cera: con el foco, el estilo se posa en el marco. De noche es la misma casa a oscuras, en el verde
 * negro del robledal, con la luna en los filos; la única luz cálida es la del fuego, y solo cerca de él.
 */
object ComunGalia : DisenoComun {
    private val NOCHE = Color(0xFF0F1410)
    private val ORO = TrazosGalia.ORO
    private val ORO_BRILLO = TrazosGalia.ORO_BRILLO
    private val BRONCE = TrazosGalia.BRONCE
    private val VERDIN = TrazosGalia.VERDIN
    private val HIERRO = TrazosGalia.HIERRO
    private val MADERA = TrazosGalia.MADERA
    private val BARRO = TrazosGalia.BARRO
    private val PAJA = TrazosGalia.PAJA
    private val HUMO = TrazosGalia.HUMO
    private val LUNA = TrazosGalia.LUNA
    private val FUEGO = TrazosGalia.FUEGO
    private val FUEGO_HALO = TrazosGalia.FUEGO_HALO
    private val ROMA = TrazosGalia.ROMA
    private val ROBLE = TrazosGalia.ROBLE
    private val ROBLE_SOMBRA = TrazosGalia.ROBLE_SOMBRA
    private val MUERDAGO = TrazosGalia.MUERDAGO
    private val LANA_ROJA = TrazosGalia.LANA_ROJA
    private val LANA_VERDE = TrazosGalia.LANA_VERDE
    private val LANA_OCRE = TrazosGalia.LANA_OCRE
    /** La brasa honda, debajo del naranja de la llama. */
    private val BRASA = Color(0xFFC4511F)
    /** La cera de abeja de las tablillas: el color de la miel y su orilla más oscura. */
    private val CERA = Color(0xFFD9BE82)
    private val CERA_OSC = Color(0xFFB8975A)
    /** El roble de los marcos y de los postes, su canto en luz y su veta; la haya clara de las tablas de cuenta. */
    private val TABLA = Color(0xFF8A6A40)
    private val TABLA_LUZ = Color(0xFFB4905C)
    private val VETA = Color(0xFF4A3220)
    private val HAYA = Color(0xFFD2B88A)
    /** El barro alisado de la pared, al sol, y el zarzo de varas que se ve donde se cayó. */
    private val ENLUCIDO = Color(0xFFE4D7B4)
    private val ZARZO = Color(0xFF7A5A34)
    /** El lino de los cordeles, la piedra del hogar y la ceniza fría. */
    private val LINO = Color(0xFFD8CBA6)
    private val PIEDRA = Color(0xFF7E776B)
    private val CENIZA = Color(0xFF9C978C)

    // ---------------------------------------------------------------- los colores de la casa (de día y de noche)

    /** Lo que no alumbra el fuego: de noche, hundido en el verde negro del robledal (tanto como [k]). */
    private fun n(c: Color, oscuro: Boolean, k: Float = 0.8f): Color = if (oscuro) mezcla(c, NOCHE, k) else c
    /** Un color a [cerca] (0 a 1) del fuego: de día, tal cual; de noche, menos hundido cuanto más cerca, y entibiado por la llama. */
    private fun alFuego(c: Color, oscuro: Boolean, cerca: Float): Color {
        if (!oscuro) return c
        val k = cerca.coerceIn(0f, 1f)
        return mezcla(mezcla(c, NOCHE, 0.86f - 0.5f * k), FUEGO, 0.18f * k)
    }
    private fun tinta(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1CF) else Color(0xFF20251A)
    private fun tenue(oscuro: Boolean): Color = if (oscuro) Color(0xFFA2A78F) else Color(0xFF555B44)
    /** El oro de las marcas: oro viejo de torques de día; de noche, la hoguera. */
    private fun oro(oscuro: Boolean): Color = if (oscuro) Color(0xFFEBA53F) else Color(0xFF7E590C)
    /** El verde del roble: de día, la hoja; de noche, el roble bajo la luna. */
    private fun verde(oscuro: Boolean): Color = if (oscuro) Color(0xFFA9C795) else Color(0xFF3E5F24)
    /** El ocre rojo del barro cocido y del bronce. */
    private fun ocreRojo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE58A5C) else Color(0xFF94431F)
    /** El filo que levanta la luz: el sol de día; de noche, la luna, apenas. */
    private fun filoLuz(oscuro: Boolean): Color = if (oscuro) LUNA.copy(alpha = 0.13f) else Color.White.copy(alpha = 0.55f)
    /** El trazo hundido de lo que se talla o se raya. */
    private fun hondo(oscuro: Boolean): Color = if (oscuro) Color.Black.copy(alpha = 0.6f) else Color(0xFF3E2A12).copy(alpha = 0.34f)
    /** La sombra de lo que está encima de algo. */
    private fun sombra(oscuro: Boolean): Color = Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f)
    /** El roble de los marcos: de noche, apenas visto. */
    private fun tabla(oscuro: Boolean): Color = if (oscuro) mezcla(TABLA, NOCHE, 0.76f) else mezcla(TABLA, TABLA_LUZ, 0.35f)
    /** La cera donde se escribe: de miel de día; de noche, la cera a oscuras, con la tinta clara encima. */
    private fun cera(oscuro: Boolean): Color = if (oscuro) mezcla(CERA, NOCHE, 0.8f) else CERA

    // ---------------------------------------------------------------- la madera, el barro y los cordeles

    /** Madera dentro de [r]: el color [base], la veta a lo largo (o de arriba abajo con [vertical]) y las hebras claras. */
    private fun DrawScope.madera(r: Rect, base: Color, semilla: Int, vertical: Boolean, oscuro: Boolean, ondula: Float = 1.2f) {
        if (r.width <= 0f || r.height <= 0f) return
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            vetas(r, mezcla(base, VETA, 0.6f).copy(alpha = if (oscuro) 0.4f else 0.42f), semilla, densidad = 1.1f, vertical = vertical, grosor = 0.7f, ondulacion = ondula)
            vetas(r, mezcla(base, Color.White, 0.35f).copy(alpha = if (oscuro) 0.05f else 0.16f), semilla + 9, densidad = 0.5f, vertical = vertical, grosor = 0.6f, ondulacion = ondula * 0.7f)
        }
    }

    /**
     * El marco de roble de una tablilla en [r]: cuatro listones de ancho [b] juntos a inglete, los largueros con la veta de
     * arriba abajo y los travesaños a lo largo, el canto de arriba a la luz y el de abajo en sombra, y la junta de cada esquina.
     */
    private fun DrawScope.marcoRoble(r: Rect, b: Float, radio: Float, base: Color, semilla: Int, oscuro: Boolean) {
        val u = density
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(radio))) }
        clipPath(forma) {
            madera(Rect(r.left, r.top, r.left + b, r.bottom), mezcla(base, Color.Black, 0.05f), semilla + 1, true, oscuro)
            madera(Rect(r.right - b, r.top, r.right, r.bottom), mezcla(base, Color.Black, 0.12f), semilla + 2, true, oscuro)
            val arriba = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right - b, r.top + b); lineTo(r.left + b, r.top + b); close() }
            val abajo = Path().apply { moveTo(r.left, r.bottom); lineTo(r.left + b, r.bottom - b); lineTo(r.right - b, r.bottom - b); lineTo(r.right, r.bottom); close() }
            clipPath(arriba) { madera(Rect(r.left, r.top, r.right, r.top + b), mezcla(base, Color.White, if (oscuro) 0.03f else 0.1f), semilla + 3, false, oscuro) }
            clipPath(abajo) { madera(Rect(r.left, r.bottom - b, r.right, r.bottom), mezcla(base, Color.Black, 0.16f), semilla + 4, false, oscuro) }
            val junta = Color.Black.copy(alpha = if (oscuro) 0.45f else 0.3f)
            drawLine(junta, r.topLeft, Offset(r.left + b, r.top + b), 0.5f * u)
            drawLine(junta, Offset(r.right, r.top), Offset(r.right - b, r.top + b), 0.5f * u)
            drawLine(junta, Offset(r.left, r.bottom), Offset(r.left + b, r.bottom - b), 0.5f * u)
            drawLine(junta, r.bottomRight, Offset(r.right - b, r.bottom - b), 0.5f * u)
            drawLine(filoLuz(oscuro), Offset(r.left + radio, r.top + 0.6f * u), Offset(r.right - radio, r.top + 0.6f * u), 0.8f * u)
            drawLine(filoLuz(oscuro).copy(alpha = filoLuz(oscuro).alpha * 0.6f), Offset(r.left + 0.6f * u, r.top + radio), Offset(r.left + 0.6f * u, r.bottom - radio), 0.6f * u)
        }
        drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.55f else 0.24f), style = Stroke(0.6f * u))
    }

    /**
     * La cera de abeja metida en el hueco [r]: la sombra del marco sobre ella, arriba y a la izquierda; el labio de abajo, que
     * levanta la luz; las pasadas anchas de la espátula con que se alisó y, apenas, lo que quedó de lo que se borró.
     */
    private fun DrawScope.ceraEn(r: Rect, base: Color, semilla: Int, oscuro: Boolean, fuerza: Float = 1f) {
        val u = density
        if (r.width <= 2 * u || r.height <= 2 * u) return
        drawRect(base, r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las pasadas de la espátula: franjas anchas y sesgadas, apenas más claras o más oscuras, de bordes suaves.
            val n = (r.width / (52f * u)).toInt().coerceIn(2, 10)
            val dx = cos(Math.toRadians(14.0)).toFloat(); val dy = sin(Math.toRadians(14.0)).toFloat()
            for (k in 0 until n) {
                val x = r.left + r.width * (k + azar(semilla, k + 500)) / n - 10 * u
                val ancho = (22f + 30f * azar(semilla, k + 520)) * u
                val c = if (azar(semilla, k + 540) > 0.45f) Color.White.copy(alpha = (if (oscuro) 0.025f else 0.09f) * fuerza)
                else Color.Black.copy(alpha = (if (oscuro) 0.07f else 0.035f) * fuerza)
                val a = Offset(x, r.center.y); val b = Offset(x + dx * ancho, r.center.y + dy * ancho)
                drawRect(Brush.linearGradient(0f to c.copy(alpha = 0f), 0.5f to c, 1f to c.copy(alpha = 0f), start = a, end = b), r.topLeft, r.size)
            }
            // Lo que se borró: rayas sueltas de letras viejas, finísimas, que la espátula no alcanzó a alisar.
            val viejo = Path()
            val filas = (r.height / (11f * u)).toInt().coerceIn(1, 10)
            for (f in 0 until filas) {
                val y = r.top + r.height * (f + 0.6f) / filas
                var x = r.left + (8f + 40f * azar(semilla, f + 600)) * u
                val fin = x + r.width * (0.12f + 0.3f * azar(semilla, f + 620))
                var k = 0
                while (x < minOf(fin, r.right - 6 * u) && k < 60) {
                    val q = azar(semilla, f * 97 + k + 700)
                    val alto = (2.4f + 1.2f * azar(semilla, f * 97 + k + 900)) * u
                    val sesgo = (azar(semilla, f * 97 + k + 950) - 0.5f) * 2.4f * u
                    if (q < 0.55f) { viejo.moveTo(x, y); viejo.lineTo(x + sesgo, y - alto) }
                    else if (q < 0.8f) { viejo.moveTo(x, y); viejo.quadraticTo(x + 1.6f * u, y - alto * 1.1f, x + 2.6f * u, y - alto * 0.2f) }
                    x += (2.2f + 3.4f * azar(semilla, k + 800)) * u; k++
                }
            }
            drawPath(viejo, (if (oscuro) Color.Black else Color(0xFF5A4520)).copy(alpha = (if (oscuro) 0.2f else 0.09f) * fuerza), style = Stroke(0.45f * u, cap = StrokeCap.Round))
            // La sombra del marco y el labio de abajo.
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.22f), Color.Transparent), r.top, r.top + 3.4f * u), r.topLeft, Size(r.width, 3.4f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), r.left, r.left + 2.6f * u), r.topLeft, Size(2.6f * u, r.height))
            drawLine(filoLuz(oscuro), Offset(r.left, r.bottom - 0.5f * u), Offset(r.right, r.bottom - 0.5f * u), 0.9f * u)
        }
    }

    /**
     * El estilo de bronce acostado de [x0] a [x1] a la altura [y]: la punta afilada a la izquierda, el cuerpo con su anillo
     * y su brillo, y la paleta de alisar la cera a la derecha; con su sombra corta.
     */
    private fun DrawScope.estilo(x0: Float, x1: Float, y: Float, oscuro: Boolean) {
        val u = density
        val bronce = if (oscuro) mezcla(BRONCE, NOCHE, 0.3f) else BRONCE
        val bajo = mezcla(bronce, Color.Black, 0.4f)
        val luz = if (oscuro) LUNA.copy(alpha = 0.35f) else ORO_BRILLO.copy(alpha = 0.85f)
        translate(0.5f * u, 1.1f * u) {
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(x0 + 3 * u, y), Offset(x1, y), 2 * u, StrokeCap.Round)
        }
        drawPath(Path().apply { moveTo(x0, y + 0.15f * u); lineTo(x0 + 7 * u, y - 1f * u); lineTo(x0 + 7 * u, y + 1f * u); close() }, bronce)
        drawLine(bronce, Offset(x0 + 6 * u, y), Offset(x1 - 4 * u, y), 2 * u, StrokeCap.Round)
        drawPath(Path().apply { moveTo(x1 - 5 * u, y - 0.9f * u); lineTo(x1, y - 2.3f * u); lineTo(x1, y + 2.3f * u); lineTo(x1 - 5 * u, y + 0.9f * u); close() }, bronce)
        drawLine(bajo, Offset(x1, y - 2.3f * u), Offset(x1, y + 2.3f * u), 0.5f * u)
        for (k in 0..1) drawLine(bajo, Offset(x0 + (12f + k * 1.4f) * u, y - 1f * u), Offset(x0 + (12f + k * 1.4f) * u, y + 1f * u), 0.45f * u)
        drawLine(luz, Offset(x0 + 7 * u, y - 0.55f * u), Offset(x1 - 6 * u, y - 0.55f * u), 0.5f * u, StrokeCap.Round)
        drawLine(luz, Offset(x1 - 4.6f * u, y - 1f * u), Offset(x1 - 0.6f * u, y - 2f * u), 0.45f * u, StrokeCap.Round)
    }

    /** La paja picada del barro en [r], en dos tonos, y los granos de arena. */
    private fun DrawScope.paja(r: Rect, clara: Color, oscura: Color, semilla: Int, densidad: Float = 1f) {
        val u = density
        if (r.width <= 0f || r.height <= 0f) return
        val n = (r.width * r.height / (70f * u * u) * densidad).toInt().coerceIn(0, 500)
        val a = Path(); val b = Path(); val g = Path()
        for (k in 0 until n) {
            val x = r.left + r.width * azar(semilla, k * 5); val y = r.top + r.height * azar(semilla, k * 5 + 1)
            val ang = azar(semilla, k * 5 + 2) * PI.toFloat()
            val l = (1.4f + 3.6f * azar(semilla, k * 5 + 3)) * u
            if (azar(semilla, k * 5 + 4) > 0.8f) g.addOval(Rect(Offset(x, y), 0.35f * u))
            else {
                val p = if (k % 3 == 0) b else a
                p.moveTo(x, y); p.lineTo(x + cos(ang) * l, y + sin(ang) * l)
            }
        }
        drawPath(a, clara, style = Stroke(0.5f * u, cap = StrokeCap.Round))
        drawPath(b, oscura, style = Stroke(0.5f * u, cap = StrokeCap.Round))
        drawPath(g, oscura)
    }

    /**
     * El cordón de oro torcido, como el cuerpo del torques: una vuelta tras otra, en sesgo, de [x0] a [x1] a la altura [y],
     * de grueso [g]; se pierde hacia [x1].
     */
    private fun DrawScope.cordon(x0: Float, x1: Float, y: Float, g: Float, color: Color) {
        val largo = abs(x1 - x0)
        if (largo < 2 * g) return
        val dir = if (x1 > x0) 1f else -1f
        val paso = g * 0.95f
        val n = (largo / paso).toInt()
        for (k in 0 until n) {
            val t = k / n.toFloat()
            val a = if (t < 0.65f) 1f else (1f - (t - 0.65f) / 0.35f)
            val x = x0 + dir * (k + 0.5f) * paso
            drawLine(color.copy(alpha = color.alpha * a), Offset(x - g * 0.42f, y + g * 0.48f), Offset(x + g * 0.42f, y - g * 0.48f), g * 0.46f, StrokeCap.Round)
        }
    }

    /** Un surco rayado de [x0] a [x1] a la altura [y]: el trazo hundido y, debajo, su labio de luz; se gasta hacia [x1]. */
    private fun DrawScope.surco(x0: Float, x1: Float, y: Float, oscuro: Boolean, a: Float = 1f) {
        if (abs(x1 - x0) < 2f) return
        val u = density
        val s = hondo(oscuro).let { it.copy(alpha = it.alpha * a) }
        val l = filoLuz(oscuro).let { it.copy(alpha = it.alpha * a) }
        drawLine(Brush.horizontalGradient(0f to s, 0.7f to s, 1f to s.copy(alpha = 0f), startX = x0, endX = x1), Offset(x0, y), Offset(x1, y), 0.8f * u, StrokeCap.Round)
        drawLine(Brush.horizontalGradient(0f to l, 0.7f to l, 1f to l.copy(alpha = 0f), startX = x0, endX = x1), Offset(x0, y + 0.8f * u), Offset(x1, y + 0.8f * u),
            0.6f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- las figuras de la casa

    /**
     * El estátero arverno: el oro batido en cuenco, de borde desparejo, con la gráfila de perlas en la parte del canto que
     * agarró el cuño, el caballo en relieve con su sombrita y el brillo arriba a la izquierda. [brillo] (0 a 1) es la luz del
     * fuego que le llega (de noche, sin ella, el oro se apaga); [apagado] lo deja deslucido.
     */
    private fun DrawScope.estatero(c: Offset, r: Float, oscuro: Boolean, brillo: Float, semilla: Int, apagado: Boolean = false) {
        val u = density
        var base = if (oscuro) mezcla(ORO, NOCHE, 0.5f - 0.38f * brillo) else ORO
        if (apagado) base = mezcla(base, if (oscuro) NOCHE else Color(0xFF8C8270), 0.45f)
        val claro = mezcla(base, ORO_BRILLO, if (oscuro) 0.15f + 0.45f * brillo else 0.55f)
        val bajo = mezcla(base, Color.Black, 0.45f)
        val disco = curvaGal(List(13) { k ->
            val a = 2 * PI * k / 13
            val rr = r * (0.93f + 0.11f * azar(semilla, k))
            Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
        })
        translate(0.35f * u, 0.8f * u) { drawPath(disco, sombra(oscuro)) }
        drawPath(disco, Brush.radialGradient(listOf(claro, base, bajo), Offset(c.x - r * 0.35f, c.y - r * 0.4f), r * 1.7f))
        // La cara en cuenco: el canto levantado y el fondo hundido.
        drawCircle(bajo.copy(alpha = 0.35f), r * 0.78f, c, style = Stroke(maxOf(r * 0.1f, 0.5f * u)))
        drawPath(disco, bajo.copy(alpha = 0.7f), style = Stroke(maxOf(r * 0.06f, 0.45f * u)))
        if (r >= 4.5f * u) for (k in 0 until 9) {
            val a = Math.toRadians(140.0 + k * 24.0 + 10 * azar(semilla, 40))
            drawCircle(bajo.copy(alpha = 0.6f), r * 0.065f, Offset(c.x + r * 0.84f * cos(a).toFloat(), c.y + r * 0.84f * sin(a).toFloat()))
        }
        with(TrazosGalia) {
            val largo = r * 1.32f
            val recio = if (r < 7 * u) 1.5f else 1f
            caballo(c + Offset(r * 0.06f, r * 0.1f), largo, bajo.copy(alpha = 0.85f), recio = recio)
            caballo(c, largo, mezcla(base, claro, 0.7f), recio = recio)
        }
        drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.12f + 0.3f * brillo else 0.45f), Color.White.copy(alpha = 0f)),
            Offset(c.x - r * 0.42f, c.y - r * 0.46f), r * 0.55f), r * 0.55f, Offset(c.x - r * 0.42f, c.y - r * 0.46f))
    }

    /**
     * La fíbula de La Tène, la que cierra el manto: el resorte enrollado a la izquierda, el arco que se alza y engruesa, el pie
     * con su mortaja y la punta del pie vuelta atrás hasta el arco, con su perla; y la aguja, [cerrada] dentro de la mortaja
     * o suelta, abierta hacia abajo. En una caja de 24 de lado que empieza en [o], a [s] píxeles por unidad.
     */
    private fun DrawScope.fibula(o: Offset, s: Float, metal: Color, luz: Color, cerrada: Boolean) {
        fun p(x: Float, y: Float) = Offset(o.x + x * s, o.y + y * s)
        val bajo = mezcla(metal, Color.Black, 0.42f)
        // La aguja, que sale del resorte (atrás de todo).
        val eje = p(3.6f, 15.6f)
        rotate(if (cerrada) 0f else 30f, eje) {
            drawLine(bajo, eje, p(20.6f, 15.6f), 0.8f * s, StrokeCap.Round)
            drawLine(metal, eje, p(20.6f, 15.45f), 0.45f * s, StrokeCap.Round)
        }
        // El pie, largo y bajo, con la mortaja debajo.
        drawPath(cintaGal(bezierGal(p(16.6f, 13.2f), p(18.4f, 13.7f), p(20f, 14.1f), p(21.6f, 14.3f), 8)) { 1f * s }, metal)
        drawPath(Path().apply { moveTo(p(15.8f, 14.2f).x, p(15.8f, 14.2f).y); lineTo(p(21.4f, 14.6f).x, p(21.4f, 14.6f).y); lineTo(p(20.6f, 16.6f).x, p(20.6f, 16.6f).y)
            lineTo(p(17.4f, 16.6f).x, p(17.4f, 16.6f).y); close() }, bajo)
        drawPath(Path().apply { moveTo(p(16.2f, 14.2f).x, p(16.2f, 14.2f).y); lineTo(p(21f, 14.6f).x, p(21f, 14.6f).y); lineTo(p(20.4f, 15.7f).x, p(20.4f, 15.7f).y)
            lineTo(p(17.4f, 15.7f).x, p(17.4f, 15.7f).y); close() }, metal)
        // La punta del pie, vuelta atrás y arriba hasta abrazar el arco, con su disco.
        drawPath(cintaGal(bezierGal(p(21.4f, 14.2f), p(23f, 12.6f), p(22f, 10f), p(19.2f, 10f), 12)) { f -> (0.85f - 0.25f * f) * s }, metal)
        drawPath(cintaGal(bezierGal(p(18.6f, 10.1f), p(17.6f, 10.1f), p(16.4f, 10.2f), p(15.2f, 10.8f), 6)) { 0.55f * s }, metal)
        drawOval(bajo, Offset(p(18.2f, 8.4f).x, p(18.2f, 8.4f).y), Size(1.9f * s, 3.4f * s))
        drawOval(metal, Offset(p(18.2f, 8.5f).x, p(18.2f, 8.5f).y), Size(1.5f * s, 3f * s))
        drawLine(luz, p(18.7f, 9.1f), p(18.7f, 10.4f), 0.35f * s, StrokeCap.Round)
        // El arco, bajo y tendido, que engruesa en el medio; con un collarín donde lo abraza el pie y el brillo arriba.
        val arco = bezierGal(p(4.4f, 13.2f), p(6.2f, 7.2f), p(11.8f, 6.2f), p(16.8f, 13.2f), 20)
        drawPath(cintaGal(arco) { f -> (0.9f + 1.5f * sin(PI.toFloat() * f)) * s }, bajo)
        drawPath(cintaGal(arco) { f -> (0.55f + 1.15f * sin(PI.toFloat() * f)) * s }, metal)
        drawLine(bajo, p(14.6f, 9.9f), p(15.6f, 11.4f), 0.55f * s)
        drawPath(cintaGal(bezierGal(p(6f, 10.4f), p(7.4f, 7.6f), p(10.4f, 7f), p(13.2f, 8.6f), 10)) { f -> 0.5f * s * sin(PI.toFloat() * f) }, luz)
        // El resorte: las vueltas del alambre, de lado, y la cuerda que pasa por encima.
        val cr = p(3.6f, 14.2f)
        drawCircle(bajo, 2.2f * s, cr)
        drawCircle(metal, 1.75f * s, cr, style = Stroke(0.8f * s))
        drawCircle(metal, 0.7f * s, cr)
        drawLine(metal, p(2.2f, 12.6f), p(5.2f, 12.4f), 0.55f * s, StrokeCap.Round)
        drawCircle(luz, 0.4f * s, Offset(cr.x - 1f * s, cr.y - 1.2f * s))
    }

    /**
     * La punta de lanza de hierro: la hoja de laurel con su nervio y su filo a la luz, y el cubo con su anillo y el asta que
     * asoma. [c] es su centro; [largo], de la punta al final del asta; apunta a la derecha ([hacia] = 1) o a la izquierda (-1).
     */
    private fun DrawScope.punta(c: Offset, largo: Float, hacia: Float, color: Color, luz: Color, asta: Color) {
        val k = largo / 16f
        fun p(x: Float, y: Float) = Offset(c.x + hacia * x * k, c.y + y * k)
        drawLine(asta, p(-8f, 0f), p(-5f, 0f), 1.5f * k, StrokeCap.Round)
        val cubo = Path().apply { moveTo(p(-5.4f, -1.1f).x, p(-5.4f, -1.1f).y); lineTo(p(-2.6f, -0.8f).x, p(-2.6f, -0.8f).y); lineTo(p(-2.6f, 0.8f).x, p(-2.6f, 0.8f).y)
            lineTo(p(-5.4f, 1.1f).x, p(-5.4f, 1.1f).y); close() }
        drawPath(cubo, color)
        drawLine(mezcla(color, Color.Black, 0.45f), p(-4.6f, -1.15f), p(-4.6f, 1.15f), 0.6f * k)
        val hoja = Path().apply {
            moveTo(p(-3f, -0.7f).x, p(-3f, -0.7f).y)
            cubicTo(p(-1.2f, -3.1f).x, p(-1.2f, -3.1f).y, p(3.2f, -2.5f).x, p(3.2f, -2.5f).y, p(8f, 0f).x, p(8f, 0f).y)
            cubicTo(p(3.2f, 2.5f).x, p(3.2f, 2.5f).y, p(-1.2f, 3.1f).x, p(-1.2f, 3.1f).y, p(-3f, 0.7f).x, p(-3f, 0.7f).y)
            close()
        }
        drawPath(hoja, color)
        clipPath(hoja) { drawRect(mezcla(color, Color.Black, 0.3f), Offset(minOf(p(-3f, 0f).x, p(8f, 0f).x), c.y), Size(16f * k, 4f * k)) }
        drawLine(mezcla(color, Color.Black, 0.5f), p(-2.6f, 0f), p(6.8f, 0f), 0.55f * k, StrokeCap.Round)
        drawLine(luz, p(-1f, -1.9f), p(4.6f, -1.6f), 0.45f * k, StrokeCap.Round)
    }

    /** Una bellota de pie: el fruto liso con su brillo, la cúpula escamada encima y el rabillo. [alto] de punta a punta. */
    private fun DrawScope.bellota(c: Offset, alto: Float, oscuro: Boolean, f: Float = 1f) {
        val fruto = if (oscuro) Color(0xFF9C7C46) else Color(0xFF9A7334)
        val cupula = if (oscuro) Color(0xFF5E5034) else Color(0xFF5E4628)
        val fw = alto * 0.5f
        drawOval(mezcla(fruto, Color.Black, 0.3f).copy(alpha = f), Offset(c.x - fw / 2, c.y - alto * 0.2f), Size(fw, alto * 0.7f))
        drawOval(fruto.copy(alpha = f), Offset(c.x - fw / 2, c.y - alto * 0.2f), Size(fw * 0.86f, alto * 0.66f))
        drawLine(Color.White.copy(alpha = (if (oscuro) 0.15f else 0.4f) * f), Offset(c.x - fw * 0.22f, c.y), Offset(c.x - fw * 0.2f, c.y + alto * 0.22f), alto * 0.06f, StrokeCap.Round)
        drawLine(cupula.copy(alpha = f), Offset(c.x, c.y - alto * 0.32f), Offset(c.x + alto * 0.06f, c.y - alto * 0.5f), alto * 0.07f, StrokeCap.Round)
        drawArc(cupula.copy(alpha = f), 180f, 180f, true, Offset(c.x - fw * 0.62f, c.y - alto * 0.38f), Size(fw * 1.24f, alto * 0.44f))
        drawLine(mezcla(cupula, Color.Black, 0.3f).copy(alpha = f), Offset(c.x - fw * 0.62f, c.y - alto * 0.16f), Offset(c.x + fw * 0.62f, c.y - alto * 0.16f), alto * 0.05f)
        for (k in -1..1) drawCircle(mezcla(cupula, Color.White, 0.25f).copy(alpha = f), alto * 0.03f, Offset(c.x + k * fw * 0.3f, c.y - alto * 0.25f))
    }

    /** La hoz de oro con que se corta el muérdago (Plinio 16.250): la hoja en media luna con su filo claro, el casquillo y el mango corto. */
    private fun DrawScope.hoz(c: Offset, tam: Float, giro: Float, oscuro: Boolean) {
        val k = tam / 10f
        rotate(giro, c) {
            fun o(x: Float, y: Float) = Offset(c.x + x * k, c.y + y * k)
            drawLine(n(MADERA, oscuro, 0.45f), o(0f, 0f), o(-3.2f, 4.4f), 1.5f * k, StrokeCap.Round)
            val oro = if (oscuro) mezcla(ORO, NOCHE, 0.3f) else ORO
            val hoja = Path().apply {
                moveTo(o(0f, 0f).x, o(0f, 0f).y)
                cubicTo(o(-1f, -5.6f).x, o(-1f, -5.6f).y, o(5.6f, -7.6f).x, o(5.6f, -7.6f).y, o(7.4f, -2f).x, o(7.4f, -2f).y)
                cubicTo(o(4.8f, -5.2f).x, o(4.8f, -5.2f).y, o(0.9f, -4.4f).x, o(0.9f, -4.4f).y, o(1.1f, 0.3f).x, o(1.1f, 0.3f).y)
                close()
            }
            translate(0.3f * k, 0.5f * k) { drawPath(hoja, Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f)) }
            drawPath(hoja, oro)
            drawPath(hoja, mezcla(oro, Color.Black, 0.4f), style = Stroke(0.3f * k))
            drawPath(Path().apply { moveTo(o(7.2f, -2.2f).x, o(7.2f, -2.2f).y); cubicTo(o(4.8f, -5f).x, o(4.8f, -5f).y, o(1.2f, -4.2f).x, o(1.2f, -4.2f).y, o(1.2f, -0.6f).x, o(1.2f, -0.6f).y) },
                ORO_BRILLO.copy(alpha = if (oscuro) 0.4f else 0.85f), style = Stroke(0.4f * k, cap = StrokeCap.Round))
            drawCircle(n(BRONCE, oscuro, 0.4f), 0.9f * k, o(0.2f, 0f))
        }
    }

    /**
     * Una casa del oppidum, vista de lado: la pared de barro sobre el zarzo, el techo alto de paja a cuatro aguas con sus
     * hiladas, la puerta (con el fuego de adentro si [fuego] > 0) y el humo que sale por la paja (César 5.43: techadas de
     * paja «a la manera gala»). [c] es el pie, al centro; [alto], hasta el caballete.
     */
    private fun DrawScope.casaPaja(c: Offset, alto: Float, oscuro: Boolean, fuego: Float) {
        val k = alto / 10f
        val muro = n(BARRO, oscuro, 0.74f); val techo = n(mezcla(PAJA, MADERA, 0.3f), oscuro, 0.74f)
        // El humo, que sale por la punta de la paja y se lo lleva el viento, tendido, colina arriba.
        val humo = if (oscuro) LUNA.copy(alpha = 0.18f) else mezcla(HUMO, Color(0xFF4A4536), 0.35f).copy(alpha = 0.8f)
        drawPath(Path().apply {
            moveTo(c.x, c.y - 9.6f * k)
            cubicTo(c.x + 1.5f * k, c.y - 10.6f * k, c.x + 4f * k, c.y - 9f * k, c.x + 7f * k, c.y - 9.8f * k)
            cubicTo(c.x + 10f * k, c.y - 10.6f * k, c.x + 13f * k, c.y - 9.4f * k, c.x + 19f * k, c.y - 10.6f * k)
        }, Brush.horizontalGradient(listOf(humo, humo.copy(alpha = humo.alpha * 0.6f), humo.copy(alpha = 0f)), c.x, c.x + 19f * k), style = Stroke(1.2f * k, cap = StrokeCap.Round))
        drawRect(Brush.horizontalGradient(listOf(mezcla(muro, Color.White, if (oscuro) 0.02f else 0.15f), muro, mezcla(muro, Color.Black, 0.3f)), c.x - 6f * k, c.x + 6f * k),
            Offset(c.x - 6f * k, c.y - 3.4f * k), Size(12f * k, 3.4f * k))
        val puerta = if (fuego > 0f) mezcla(BRASA, FUEGO_HALO, 0.4f * fuego) else mezcla(muro, Color.Black, 0.65f)
        if (fuego > 0f) drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = 0.35f * fuego), FUEGO_HALO.copy(alpha = 0f)), Offset(c.x - 1f * k, c.y - 1.4f * k), 5f * k),
            5f * k, Offset(c.x - 1f * k, c.y - 1.4f * k))
        drawRect(puerta, Offset(c.x - 2f * k, c.y - 2.8f * k), Size(1.9f * k, 2.8f * k))
        val t = Path().apply {
            moveTo(c.x - 7.4f * k, c.y - 2.6f * k); lineTo(c.x - 2.6f * k, c.y - 10f * k); lineTo(c.x + 2.6f * k, c.y - 10f * k); lineTo(c.x + 7.4f * k, c.y - 2.6f * k)
            quadraticTo(c.x, c.y - 3.4f * k, c.x - 7.4f * k, c.y - 2.6f * k); close()
        }
        drawPath(t, techo)
        clipPath(t) {
            // El faldón de la derecha, a la sombra, y las hiladas de la paja.
            drawPath(Path().apply { moveTo(c.x + 2.6f * k, c.y - 10f * k); lineTo(c.x + 7.4f * k, c.y - 2.6f * k); lineTo(c.x + 4.6f * k, c.y - 2.6f * k); close() }, mezcla(techo, Color.Black, 0.3f))
            for (j in 1..2) drawLine(mezcla(techo, Color.Black, 0.35f), Offset(c.x - 7.4f * k, c.y - (2.6f + 2.4f * j) * k), Offset(c.x + 7.4f * k, c.y - (2.6f + 2.4f * j) * k), 0.3f * k)
            for (j in -6..6) drawLine(mezcla(techo, Color.Black, 0.2f), Offset(c.x + j * 0.42f * k, c.y - 10f * k), Offset(c.x + j * 1.15f * k, c.y - 2.6f * k), 0.25f * k)
        }
        drawLine(filoLuz(oscuro), Offset(c.x - 7f * k, c.y - 2.9f * k), Offset(c.x - 2.5f * k, c.y - 9.8f * k), 0.4f * k, StrokeCap.Round)
        drawLine(filoLuz(oscuro), Offset(c.x - 2.5f * k, c.y - 9.9f * k), Offset(c.x + 2.5f * k, c.y - 9.9f * k), 0.4f * k, StrokeCap.Round)
    }

    /** Un roble pequeño, lejos: el tronco y la copa redonda en varios bultos, con la luz arriba a la izquierda y una bola de muérdago. */
    private fun DrawScope.roblecito(c: Offset, alto: Float, oscuro: Boolean) {
        val k = alto / 10f
        drawLine(n(mezcla(MADERA, Color.Black, 0.2f), oscuro, 0.7f), c, Offset(c.x, c.y - 4.5f * k), 1.2f * k, StrokeCap.Round)
        val hoja = n(ROBLE, oscuro, 0.62f)
        val luz = n(mezcla(ROBLE, MUERDAGO, 0.4f), oscuro, 0.62f)
        val honda = n(ROBLE_SOMBRA, oscuro, 0.62f)
        val bultos = listOf(Triple(-2.4f, -5.4f, 2.6f), Triple(2.4f, -5.6f, 2.5f), Triple(0f, -7.6f, 2.9f), Triple(0.4f, -4.8f, 2.4f))
        bultos.forEach { (x, y, r) -> drawCircle(honda, r * k, Offset(c.x + x * k + 0.3f * k, c.y + y * k + 0.4f * k)) }
        bultos.forEach { (x, y, r) -> drawCircle(hoja, r * k, Offset(c.x + x * k, c.y + y * k)) }
        bultos.forEach { (x, y, r) -> drawCircle(luz, r * 0.5f * k, Offset(c.x + x * k - r * 0.35f * k, c.y + y * k - r * 0.35f * k)) }
        drawCircle(n(MUERDAGO, oscuro, 0.45f), 0.95f * k, Offset(c.x + 1.6f * k, c.y - 6.6f * k))
        drawCircle(n(Color(0xFFF2EEDC), oscuro, 0.45f), 0.3f * k, Offset(c.x + 1.4f * k, c.y - 6.8f * k))
    }

    /** La llama de una antorcha o de una brasa alta: tres capas (la brasa honda, el naranja y el corazón claro) que suben a [alto] desde [pie]. */
    private fun DrawScope.llama(pie: Offset, alto: Float, ancho: Float) {
        fun capa(h: Float, a: Float, color: Color) {
            drawPath(Path().apply {
                moveTo(pie.x - a, pie.y)
                cubicTo(pie.x - a * 1.3f, pie.y - h * 0.45f, pie.x - a * 0.3f, pie.y - h * 0.7f, pie.x + a * 0.15f, pie.y - h)
                cubicTo(pie.x + a * 0.3f, pie.y - h * 0.6f, pie.x + a * 1.25f, pie.y - h * 0.4f, pie.x + a, pie.y)
                quadraticTo(pie.x, pie.y + a * 0.6f, pie.x - a, pie.y)
                close()
            }, color)
        }
        capa(alto, ancho, BRASA)
        capa(alto * 0.78f, ancho * 0.72f, FUEGO)
        capa(alto * 0.45f, ancho * 0.4f, FUEGO_HALO)
    }

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 17.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = tinta(oscuro)
    override fun pistaCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9C9C86) else Color(0xFF534F34)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) oro(true) else Color(0xFF2F4A1B)
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF8A7A) else Color(0xFF8E1F16)

    /**
     * La tablilla de cera en su marco de roble: los cuatro listones a inglete, con su veta, y en el hueco la cera de abeja
     * alisada a espátula, con la sombra del marco y lo que quedó de lo que se borró; el renglón rayado con el estilo (uno por
     * renglón si son varios). En el larguero de la izquierda, los dos agujeros con el cordel que la ata a la otra hoja del
     * díptico. Con el foco, el estilo de bronce se posa en el travesaño de arriba y la cera se marca; con un error, la cera
     * se marca en rojo; apagada, la madera y la cera se van al gris.
     */
    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 20 * u) return
        val gris = if (oscuro) Color(0xFF1C211C) else Color(0xFFC8C2B2)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, gris, 0.5f)
        val alto = h - 1.2f * u
        val caja = Rect(0f, 0f, w, alto)
        val radio = 2.6f * u
        val b = 5.2f * u
        drawRoundRect(sombra(oscuro), Offset(0.6f * u, 1.2f * u), caja.size, CornerRadius(radio))
        marcoRoble(caja, b, radio, a(tabla(oscuro)), e.semilla, oscuro)
        val hueco = Rect(b, b, w - b, alto - b)
        ceraEn(hueco, a(cera(oscuro)), e.semilla, oscuro, if (e.habilitado) 1f else 0.5f)
        // El renglón rayado con el estilo: el surco y su labio.
        val izq = margenCampoX.toPx() - 2 * u; val der = w - margenCampoX.toPx() + 2 * u
        val raya = (if (oscuro) Color.Black else Color(0xFF3E2A12)).copy(alpha = if (!e.habilitado) 0.15f else if (e.enfocado) (if (oscuro) 0.75f else 0.5f) else (if (oscuro) 0.55f else 0.32f))
        val labio = filoLuz(oscuro).let { it.copy(alpha = it.alpha * (if (e.habilitado) 1f else 0.5f)) }
        fun renglon(y: Float) {
            if (der - izq < 8 * u) return
            drawLine(raya, Offset(izq, y), Offset(der, y), 0.8f * u, StrokeCap.Round)
            drawLine(labio, Offset(izq + 0.5f * u, y + 0.8f * u), Offset(der, y + 0.8f * u), 0.55f * u, StrokeCap.Round)
        }
        if (e.multilinea) renglonesGal(margenCampoY) { _, y -> renglon(y) } else renglon(h - 10.5f * u)
        // Los dos agujeros del larguero y el cordel de lino que pasa por ellos.
        if (alto > 30 * u) {
            val x = b * 0.5f
            val y1 = alto * 0.3f; val y2 = alto * 0.7f
            val cordel = a(n(LINO, oscuro, 0.6f))
            listOf(y1, y2).forEach { y -> drawCircle(Color.Black.copy(alpha = if (oscuro) 0.7f else 0.55f), 1.2f * u, Offset(x, y)) }
            drawPath(Path().apply { moveTo(x, y1); cubicTo(0.2f * u, y1 + 4 * u, 0.2f * u, y2 - 4 * u, x, y2) }, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), style = Stroke(1.7f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(x, y1); cubicTo(0.2f * u, y1 + 4 * u, 0.2f * u, y2 - 4 * u, x, y2) }, cordel, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(x, y1); cubicTo(0.2f * u, y1 + 4 * u, 0.2f * u, y2 - 4 * u, x, y2) }, Color.Black.copy(alpha = 0.25f),
                style = Stroke(0.35f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1f * u))))
        }
        // Con el foco o con un error, la cera se marca en su color.
        if (e.error) drawRect(errorCampo(oscuro), hueco.topLeft, hueco.size, style = Stroke(1.5f * u))
        else if (e.enfocado && e.habilitado) drawRect(rotuloCampo(oscuro).copy(alpha = 0.9f), hueco.topLeft, hueco.size, style = Stroke(1.2f * u))
        // Con el foco, el estilo de bronce posado en el travesaño de arriba: la punta a la izquierda, la paleta de alisar a la derecha.
        if (e.enfocado && e.habilitado && w > 120 * u) estilo(w - 72 * u, w - 14 * u, b * 0.5f, oscuro)
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * El cordón de oro torcido, como el cuerpo de un torques, que se pierde hacia los dos lados; al centro, una bellota de pie
     * entre dos hojas de roble acostadas, cada una hacia su lado. Tenue, a la mitad.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val y = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val cx = w / 2
        val cordel = (if (oscuro) mezcla(ORO, NOCHE, 0.42f) else mezcla(ORO, Color(0xFF5A3F08), 0.3f)).copy(alpha = 0.8f * f)
        val hueco = 16.5f * u
        if (cx - hueco > 8 * u) {
            cordon(cx - hueco, 3 * u, y, 1.5f * u, cordel)
            cordon(cx + hueco, w - 3 * u, y, 1.5f * u, cordel)
        }
        val hoja = (if (oscuro) Color(0xFF6F8A5E) else ROBLE).copy(alpha = 0.95f * f)
        val nervio = (if (oscuro) Color(0xFF2E3B28) else ROBLE_SOMBRA).copy(alpha = f)
        with(TrazosGalia) {
            hojaRoble(Offset(cx - 9.2f * u, y), 11.2f * u, -90f, hoja, nervio)
            hojaRoble(Offset(cx + 9.2f * u, y), 11.2f * u, 90f, hoja, nervio)
        }
        bellota(Offset(cx, y + 0.4f * u), 7.6f * u, oscuro, f)
    }

    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) oro(true) else verde(false)

    /**
     * La faja de lana tejida a tablillas bajo la palabra, con sus orillas, el dibujo de ojos en zigzag y el fleco en las
     * puntas. Al presionarlo, la luz del fuego se enciende detrás de la palabra y la lana se aviva; apagado, queda solo la
     * raya punteada, sin faja.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val izq = minOf(10 * u, w * 0.2f); val der = w - izq
        val y = h / 2 + 11.5f * u
        if (der - izq < 10 * u) return
        if (!e.habilitado) {
            drawLine(hondo(oscuro).copy(alpha = 0.32f), Offset(izq, y), Offset(der, y), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2.2f * u)))
            return
        }
        if (e.presionado && h > 20 * u) {
            val c = Offset(w / 2, h * 0.52f)
            val r = maxOf(w * 0.55f, 26 * u)
            drawOval(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = if (oscuro) 0.3f else 0.45f), FUEGO.copy(alpha = if (oscuro) 0.08f else 0.1f), FUEGO_HALO.copy(alpha = 0f)), c, r),
                Offset(c.x - r, c.y - h * 0.46f), Size(2 * r, h * 0.92f))
        }
        val viva = if (e.presionado) 1f else 0f
        fun lana(c: Color) = if (oscuro) mezcla(mezcla(c, NOCHE, 0.5f), FUEGO, 0.25f * viva) else mezcla(c, Color.White, 0.08f * viva)
        val g = 2.8f * u
        val r = Rect(izq, y - g / 2, der, y + g / 2)
        // El fleco de las dos puntas.
        val hilo = lana(LANA_OCRE).copy(alpha = 0.8f)
        for (k in -1..1) {
            drawLine(hilo, Offset(r.left, y + k * 0.8f * u), Offset(r.left - 2.4f * u, y + k * 1.2f * u), 0.4f * u, StrokeCap.Round)
            drawLine(hilo, Offset(r.right, y + k * 0.8f * u), Offset(r.right + 2.4f * u, y + k * 1.2f * u), 0.4f * u, StrokeCap.Round)
        }
        drawRoundRect(lana(LANA_ROJA), r.topLeft, r.size, CornerRadius(0.5f * u))
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Los ojos del tejido: rayitas sesgadas, verdes y ocres por tramos, que cambian de sentido de un tramo al otro.
            var x = r.left + 1 * u; var k = 0
            while (x < r.right - 0.5f * u && k < 400) {
                val tramo = k / 5
                val c = if (tramo % 2 == 0) lana(LANA_OCRE) else lana(LANA_VERDE)
                val s = if (tramo % 2 == 0) 1f else -1f
                drawLine(c, Offset(x - s * 0.45f * u, r.top + 0.85f * u), Offset(x + s * 0.45f * u, r.bottom - 0.85f * u), 0.55f * u, StrokeCap.Round)
                x += 1.25f * u; k++
            }
            drawLine(lana(LANA_OCRE), Offset(r.left, r.top + 0.3f * u), Offset(r.right, r.top + 0.3f * u), 0.45f * u)
            drawLine(lana(LANA_OCRE), Offset(r.left, r.bottom - 0.3f * u), Offset(r.right, r.bottom - 0.3f * u), 0.45f * u)
        }
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.5f * u, r.bottom + 0.6f * u), Offset(r.right - 0.5f * u, r.bottom + 0.6f * u), 0.6f * u)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    override val margenFila: Dp = 19.dp

    /**
     * Una línea de la casa: el surco rayado al pie y, a la izquierda, un estátero de oro con su caballo (dos, uno encima del
     * otro, si la fila lleva detalle). Al tocarla, la luz del fuego llega desde la izquierda y el oro brilla; de noche, sin
     * ella, el oro se apaga.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        if (e.presionado) {
            drawRect(Brush.horizontalGradient(listOf(FUEGO_HALO.copy(alpha = if (oscuro) 0.08f else 0.16f), FUEGO_HALO.copy(alpha = 0f)), 0f, w))
            drawRect(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = if (oscuro) 0.24f else 0.3f), FUEGO_HALO.copy(alpha = 0f)), Offset(8 * u, cy), 80 * u))
        }
        surco(margenFila.toPx(), w - 2 * u, h - 1.4f * u, oscuro, 0.7f)
        if (h < 16 * u) return
        val brillo = if (e.presionado) 1f else 0f
        if (e.conDetalle && h >= 30 * u) {
            estatero(Offset(10.6f * u, cy - 3.6f * u), 5f * u, oscuro, brillo, e.semilla + 17, apagado = true)
            estatero(Offset(8.4f * u, cy + 2.2f * u), 5.8f * u, oscuro, brillo, e.semilla)
        } else estatero(Offset(8.4f * u, cy), 5.8f * u, oscuro, brillo, e.semilla)
    }

    /** La punta de lanza de hierro, que apunta adelante, con el asta que asoma. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val s = size.minDimension / 20f
        val hierro = if (oscuro) mezcla(HIERRO, LUNA, 0.5f) else mezcla(HIERRO, tenue(false), 0.3f)
        val luz = if (oscuro) LUNA.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.6f)
        punta(Offset(size.width / 2 + 0.5f * s, size.height / 2), 18.5f * s, 1f, hierro, luz, n(MADERA, oscuro, 0.35f))
    }

    /**
     * La fíbula de bronce de La Tène, la que prende el manto: fija, cerrada, con la aguja en su mortaja y el bronce a la luz;
     * sin fijar, abierta, con la aguja suelta hacia abajo y el bronce tenue.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val s = size.minDimension / 24f
        val o = Offset(size.width / 2 - 12 * s, size.height / 2 - 12 * s)
        if (fija) {
            val metal = if (oscuro) mezcla(mezcla(BRONCE, ORO, 0.35f), NOCHE, 0.12f) else mezcla(BRONCE, ORO, 0.25f)
            translate(0.3f * s, 0.7f * s) { fibula(o, s, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Color.Transparent, true) }
            fibula(o, s, metal, if (oscuro) ORO_BRILLO.copy(alpha = 0.55f) else ORO_BRILLO.copy(alpha = 0.9f), true)
        } else {
            val t = tenue(oscuro).copy(alpha = 0.55f)
            fibula(o, s, t, Color.Transparent, false)
        }
    }

    override val tamDibujoVacio: Dp = 46.dp
    override fun tintaVacio(oscuro: Boolean): Color = tenue(oscuro)

    /**
     * Un lienzo de la pared de la casa grande: el barro alisado con su paja entre dos postes de roble y bajo la viga, con sus
     * clavijas; arriba a la izquierda, donde el barro se cayó, asoma el zarzo de varas tejidas. De noche, la misma pared a
     * oscuras, con la luna que entra apenas por arriba a la derecha.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 40 * u) return
        val pared = if (oscuro) Color(0xFF1A2019) else ENLUCIDO
        drawRect(pared)
        paja(Rect(0f, 0f, w, h), (if (oscuro) PAJA.copy(alpha = 0.07f) else PAJA.copy(alpha = 0.9f)), (if (oscuro) Color.Black.copy(alpha = 0.3f) else BARRO.copy(alpha = 0.55f)), e.semilla, 0.7f)
        if (oscuro) drawRect(Brush.radialGradient(listOf(LUNA.copy(alpha = 0.06f), LUNA.copy(alpha = 0f)), Offset(w, 0f), w * 0.7f))
        else drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent), 0f, h * 0.5f))
        // El zarzo, donde se cayó el barro: el hueco de canto quebrado junto al poste, bajo la viga, con sus grietas.
        val poste = 8 * u; val viga = 7 * u
        val hueco = Path().apply {
            val pts = listOf(0f to 0f, 14f to 0f, 26f to 1.5f, 34f to 0f, 40f to 3f, 38f to 9f, 42f to 14f, 35f to 19f, 30f to 24f, 22f to 22f, 15f to 27f, 8f to 24f, 3f to 28f, 0f to 22f)
            pts.forEachIndexed { k, (x, y) ->
                val jx = (azar(e.semilla, k + 30) - 0.5f) * 2.4f; val jy = (azar(e.semilla, k + 50) - 0.5f) * 2.4f
                val q = Offset(poste + (x + if (x > 0f) jx else 0f) * u, viga + (y + if (y > 0f) jy else 0f) * u)
                if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
            }
            close()
        }
        drawPath(hueco, if (oscuro) Color(0xFF070A07) else Color(0xFF3E3020))
        clipPath(hueco) {
            val vara = n(ZARZO, oscuro, 0.72f)
            // Las estacas de pie y, entre ellas, las varas tejidas por delante y por detrás.
            for (k in 0 until 6) {
                val x = poste + (4f + k * 7.6f) * u
                drawLine(mezcla(vara, Color.Black, 0.35f), Offset(x + 0.4f * u, viga), Offset(x + 0.4f * u, viga + 32 * u), 2.8f * u)
                drawLine(mezcla(vara, if (oscuro) LUNA else PAJA, if (oscuro) 0.04f else 0.15f), Offset(x - 0.4f * u, viga), Offset(x - 0.4f * u, viga + 32 * u), 1.2f * u)
            }
            for (j in 0 until 5) {
                val y0 = viga + (3f + j * 5.4f) * u
                val mimbre = Path().apply {
                    moveTo(poste - 2 * u, y0)
                    for (i in 0..12) {
                        val x = poste + (i * 3.8f) * u
                        lineTo(x, y0 + (if ((i + j) % 2 == 0) 1.3f else -1.3f) * u)
                    }
                }
                drawPath(mimbre, Color.Black.copy(alpha = if (oscuro) 0.6f else 0.35f), style = Stroke(3.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(mimbre, vara, style = Stroke(2.5f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(mimbre, mezcla(vara, if (oscuro) LUNA else PAJA, if (oscuro) 0.06f else 0.35f), style = Stroke(0.7f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            // La sombra del canto del barro sobre el zarzo, arriba y a la izquierda.
            translate(1.6f * u, 1.8f * u) { drawPath(hueco, Color.Black.copy(alpha = if (oscuro) 0.6f else 0.4f), style = Stroke(3.2f * u)) }
        }
        // El canto roto del barro: su grueso, claro abajo y a la derecha, y las grietas que salen de él.
        translate(-0.7f * u, -0.9f * u) { drawPath(hueco, mezcla(pared, if (oscuro) LUNA else Color.White, if (oscuro) 0.06f else 0.45f), style = Stroke(1.6f * u, join = StrokeJoin.Round)) }
        drawPath(hueco, mezcla(pared, Color.Black, if (oscuro) 0.45f else 0.3f), style = Stroke(0.7f * u, join = StrokeJoin.Round))
        val grieta = mezcla(pared, Color.Black, if (oscuro) 0.5f else 0.32f)
        listOf(Triple(42f, 14f, 1), Triple(30f, 24f, 2), Triple(15f, 27f, 3)).forEach { (x, y, k) ->
            val g = Path().apply {
                var q = Offset(poste + x * u, viga + y * u); moveTo(q.x, q.y)
                for (i in 1..4) {
                    q = Offset(q.x + (2.2f + 2f * azar(e.semilla, k * 10 + i)) * u, q.y + (1.2f + 2.4f * azar(e.semilla, k * 10 + i + 5)) * u * (if (k == 1) 0.4f else 1f))
                    lineTo(q.x, q.y)
                }
            }
            drawPath(g, grieta, style = Stroke(0.5f * u, join = StrokeJoin.Round))
        }
        // Los postes y la viga, con sus clavijas.
        val roble = n(TABLA, oscuro, 0.78f)
        drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Color.Transparent), poste, poste + 5 * u), Offset(poste, 0f), Size(5 * u, h))
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), Color.Transparent), viga, viga + 5 * u), Offset(0f, viga), Size(w, 5 * u))
        madera(Rect(0f, 0f, poste, h), roble, e.semilla + 1, true, oscuro)
        madera(Rect(w - poste, 0f, w, h), mezcla(roble, Color.Black, 0.12f), e.semilla + 2, true, oscuro)
        madera(Rect(0f, 0f, w, viga), mezcla(roble, Color.White, if (oscuro) 0.02f else 0.08f), e.semilla + 3, false, oscuro)
        drawLine(filoLuz(oscuro), Offset(0f, 0.6f * u), Offset(w, 0.6f * u), 0.8f * u)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(0f, viga), Offset(w, viga), 0.7f * u)
        listOf(Offset(poste / 2, viga / 2), Offset(w - poste / 2, viga / 2)).forEach { p ->
            drawCircle(mezcla(roble, Color.Black, 0.4f), 1.7f * u, p)
            drawCircle(mezcla(roble, Color.White, if (oscuro) 0.05f else 0.2f), 1.1f * u, p - Offset(0.2f * u, 0.2f * u))
        }
    }

    /**
     * Lo vacío: el caldero de bronce colgado de su cadena, con la boca oscura y nada adentro, las asas de anilla y la hilera
     * de remaches del borde, con algo de verdín; debajo, el hogar frío: las piedras, la ceniza gris y dos tizones apagados.
     * De día, con el sol a la izquierda; de noche, el bronce a oscuras con el filo de la luna.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val s = size.minDimension
        fun f(v: Float) = v * s
        val cx = size.width / 2
        val bronce = n(BRONCE, oscuro, 0.55f)
        val claro = if (oscuro) mezcla(bronce, LUNA, 0.12f) else mezcla(BRONCE, ORO_BRILLO, 0.45f)
        val bajo = mezcla(bronce, Color.Black, 0.5f)
        // El hogar frío: la ceniza, los tizones y las piedras.
        drawOval(n(CENIZA, oscuro, 0.75f), Offset(cx - f(0.32f), f(0.86f)), Size(f(0.64f), f(0.1f)))
        val tizon = n(Color(0xFF2E241C), oscuro, 0.4f)
        drawLine(tizon, Offset(cx - f(0.24f), f(0.93f)), Offset(cx + f(0.08f), f(0.87f)), f(0.05f), StrokeCap.Round)
        drawLine(mezcla(tizon, Color.Black, 0.2f), Offset(cx + f(0.24f), f(0.92f)), Offset(cx - f(0.06f), f(0.875f)), f(0.05f), StrokeCap.Round)
        drawCircle(n(CENIZA, oscuro, 0.6f), f(0.022f), Offset(cx + f(0.08f), f(0.87f)))
        for ((x, y, r) in listOf(Triple(-0.33f, 0.92f, 0.06f), Triple(-0.12f, 0.96f, 0.055f), Triple(0.13f, 0.96f, 0.06f), Triple(0.33f, 0.915f, 0.055f))) {
            val p = n(PIEDRA, oscuro, 0.7f)
            drawOval(p, Offset(cx + f(x) - f(r), f(y) - f(r * 0.7f)), Size(f(r * 2), f(r * 1.4f)))
            drawOval(mezcla(p, Color.White, if (oscuro) 0.05f else 0.25f), Offset(cx + f(x) - f(r * 0.6f), f(y) - f(r * 0.65f)), Size(f(r * 1.1f), f(r * 0.6f)))
        }
        // La cadena, de eslabones, y el asa en arco.
        val hierro = if (oscuro) mezcla(HIERRO, NOCHE, 0.3f) else HIERRO
        var y = 0f; var k = 0
        while (y < f(0.2f) && k < 20) {
            if (k % 2 == 0) drawOval(hierro, Offset(cx - f(0.025f), y), Size(f(0.05f), f(0.075f)), style = Stroke(f(0.018f)))
            else drawLine(hierro, Offset(cx, y), Offset(cx, y + f(0.075f)), f(0.022f))
            y += f(0.06f); k++
        }
        drawArc(hierro, 180f, 180f, false, Offset(cx - f(0.27f), f(0.2f)), Size(f(0.54f), f(0.42f)), style = Stroke(f(0.022f)))
        // El caldero: la panza, el borde engrosado y la boca vacía.
        val panza = Path().apply {
            moveTo(cx - f(0.27f), f(0.41f))
            cubicTo(cx - f(0.33f), f(0.5f), cx - f(0.32f), f(0.7f), cx - f(0.16f), f(0.79f))
            quadraticTo(cx, f(0.84f), cx + f(0.16f), f(0.79f))
            cubicTo(cx + f(0.32f), f(0.7f), cx + f(0.33f), f(0.5f), cx + f(0.27f), f(0.41f))
            close()
        }
        drawOval(sombra(oscuro), Offset(cx - f(0.2f), f(0.8f)), Size(f(0.4f), f(0.05f)))
        drawPath(panza, Brush.horizontalGradient(listOf(claro, bronce, bajo), cx - f(0.3f), cx + f(0.3f)))
        clipPath(panza) {
            for (j in 0 until 7) drawCircle(bajo.copy(alpha = 0.7f), f(0.012f), Offset(cx - f(0.21f) + j * f(0.07f), f(0.47f) + abs(j - 3) * f(0.004f)))
            drawLine(bajo.copy(alpha = 0.5f), Offset(cx - f(0.32f), f(0.6f)), Offset(cx + f(0.32f), f(0.6f)), f(0.012f))
            drawCircle(VERDIN.copy(alpha = if (oscuro) 0.25f else 0.45f), f(0.05f), Offset(cx + f(0.12f), f(0.7f)))
            drawCircle(VERDIN.copy(alpha = if (oscuro) 0.18f else 0.35f), f(0.03f), Offset(cx - f(0.2f), f(0.55f)))
            if (oscuro) drawPath(panza, LUNA.copy(alpha = 0.18f), style = Stroke(f(0.025f)))
        }
        drawOval(mezcla(bronce, claro, 0.5f), Offset(cx - f(0.29f), f(0.375f)), Size(f(0.58f), f(0.075f)))
        drawOval(Color(0xFF120D08).copy(alpha = if (oscuro) 0.9f else 0.85f), Offset(cx - f(0.245f), f(0.387f)), Size(f(0.49f), f(0.05f)))
        // Las asas de anilla.
        listOf(-1f, 1f).forEach { l -> drawCircle(bajo, f(0.035f), Offset(cx + l * f(0.29f), f(0.43f)), style = Stroke(f(0.016f))) }
        drawLine(filoLuz(oscuro), Offset(cx - f(0.27f), f(0.42f)), Offset(cx - f(0.3f), f(0.55f)), maxOf(f(0.012f), 0.5f * u))
    }

    override val margenAviso: Dp = 34.dp
    override fun tintaAviso(oscuro: Boolean): Color = tinta(oscuro)

    /**
     * El aviso es el grito que corre de campo en campo (César 7.3): el barro alisado de la pared junto al poste de roble. En el
     * poste, según el aviso, el carnyx atado con su cordel, con su cabeza de jabalí y el bramido que sale de la boca, o la
     * antorcha en su argolla de hierro, cuya luz cálida se derrama sobre el poste y la pared.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 40 * u || h < 20 * u) return
        val forma = Path().apply { addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(4 * u))) }
        val fuego = e.semilla % 2 != 0
        val pared = if (oscuro) Color(0xFF182017) else mezcla(ENLUCIDO, Color.White, 0.25f)
        val banda = 26 * u
        drawPath(forma, pared)
        clipPath(forma) {
            paja(Rect(banda, 0f, w, h), if (oscuro) PAJA.copy(alpha = 0.06f) else PAJA.copy(alpha = 0.75f), if (oscuro) Color.Black.copy(alpha = 0.25f) else BARRO.copy(alpha = 0.4f), e.semilla + 5, 0.55f)
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.14f), Color.Transparent), banda, banda + 5 * u), Offset(banda, 0f), Size(5 * u, h))
            madera(Rect(0f, 0f, banda, h), if (fuego) alFuego(TABLA, oscuro, 0.55f) else n(TABLA, oscuro, 0.78f), e.semilla + 1, true, oscuro)
            drawLine(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(banda, 0f), Offset(banda, h), 0.8f * u)
            drawLine(filoLuz(oscuro), Offset(0.8f * u, 0f), Offset(0.8f * u, h), 0.6f * u)
            if (fuego) {
                // La antorcha en su argolla: el palo, el haz atado, la llama y su luz.
                val boca = Offset(15.5f * u, minOf(h * 0.42f, 21 * u))
                drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = if (oscuro) 0.38f else 0.42f), FUEGO.copy(alpha = 0.1f), FUEGO_HALO.copy(alpha = 0f)), boca, 64 * u), 64 * u, boca)
                val pie = Offset(9 * u, h - 3 * u)
                drawLine(n(MADERA, oscuro, 0.3f), pie, boca, 2.2f * u, StrokeCap.Round)
                val anilla = Offset(pie.x + (boca.x - pie.x) * 0.45f, pie.y + (boca.y - pie.y) * 0.45f)
                drawLine(n(HIERRO, oscuro, 0.2f), Offset(1.5f * u, anilla.y), Offset(anilla.x - 2.2f * u, anilla.y), 1f * u)
                drawOval(n(HIERRO, oscuro, 0.2f), Offset(anilla.x - 2.4f * u, anilla.y - 1.3f * u), Size(4.8f * u, 2.6f * u), style = Stroke(0.9f * u))
                drawLine(n(Color(0xFF3A2A1A), oscuro, 0.2f), Offset(boca.x - 1.4f * u, boca.y + 2.6f * u), Offset(boca.x + 0.4f * u, boca.y - 0.4f * u), 3.6f * u, StrokeCap.Round)
                for (j in 0..1) drawLine(n(LINO, oscuro, 0.4f).copy(alpha = 0.7f), Offset(boca.x - 2.8f * u + j * 1.1f * u, boca.y + 1.2f * u - j * 1.4f * u),
                    Offset(boca.x - 0.2f * u + j * 1.1f * u, boca.y + 2.6f * u - j * 1.4f * u), 0.5f * u)
                llama(Offset(boca.x + 0.2f * u, boca.y - 0.4f * u), minOf(h * 0.36f, 16 * u), 3.6f * u)
            } else {
                // El carnyx de bronce, atado al poste, y el bramido que sale de la boca del jabalí.
                val alto = minOf(h - 7 * u, 46 * u)
                val pie = Offset(8 * u, h - 3.5f * u)
                val bronce = n(BRONCE, oscuro, 0.5f)
                with(TrazosGalia) {
                    translate(0.6f * u, 1f * u) { carnyx(pie, alto, Color.Black.copy(alpha = if (oscuro) 0.4f else 0.18f)) }
                    carnyx(pie, alto, bronce, if (oscuro) NOCHE else Color(0xFF2A1A0C))
                }
                val k = alto / 100f
                listOf(0.38f, 0.66f).forEach { t ->
                    val yy = pie.y - alto * t
                    val correa = n(Color(0xFF4E3420), oscuro, 0.35f)
                    drawLine(Color.Black.copy(alpha = 0.3f), Offset(pie.x - 3.4f * u, yy + 0.5f * u), Offset(pie.x + 3.4f * u, yy + 1.3f * u), 1.5f * u, StrokeCap.Round)
                    drawLine(correa, Offset(pie.x - 3.4f * u, yy), Offset(pie.x + 3.4f * u, yy + 0.8f * u), 1.3f * u, StrokeCap.Round)
                }
                val boca = Offset(pie.x + 40f * k, pie.y - 82f * k)
                for (j in 1..3) {
                    val r = (2.2f + 2.4f * j) * u
                    drawArc(tenue(oscuro).copy(alpha = 0.6f - j * 0.15f), -35f, 70f, false, Offset(boca.x - r, boca.y - r), Size(2 * r, 2 * r), style = Stroke(0.8f * u, cap = StrokeCap.Round))
                }
            }
        }
        drawPath(forma, (if (oscuro) Color(0xFF34402F) else Color(0xFFB5A273)).copy(alpha = 0.85f), style = Stroke(0.8f * u))
    }

    override val margenCifra: Dp = 11.dp
    override fun tintaCifra(oscuro: Boolean): Color = tinta(oscuro)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) oro(true) else Color(0xFF5E4208)

    /**
     * La tabla de cuenta: una tabla de haya cepillada con su veta, el canto de arriba a la luz y el grueso de la tabla abajo,
     * y en el canto de arriba las muescas de la cuenta, de cinco en cinco (la quinta, más honda); una clavija de roble la
     * prende a la pared.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 20 * u || h < 20 * u) return
        val r = Rect(0f, 0f, w, h - 1.4f * u)
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(1.6f * u))) }
        val cara = if (oscuro) mezcla(HAYA, NOCHE, 0.8f) else HAYA
        translate(0.8f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
        clipPath(forma) {
            madera(r, cara, e.semilla, false, oscuro, 0.35f)
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.03f else 0.2f), Color.Transparent), 0f, r.height * 0.6f), r.topLeft, r.size)
            drawRect(mezcla(cara, Color.Black, if (oscuro) 0.35f else 0.22f), Offset(0f, r.bottom - 2.2f * u), Size(w, 2.2f * u))
        }
        drawLine(filoLuz(oscuro), Offset(1.6f * u, 0.6f * u), Offset(w - 1.6f * u, 0.6f * u), 0.7f * u)
        // Las muescas, de cinco en cinco.
        val cuantas = 6 + Math.floorMod(e.semilla, 9)
        var x = 6 * u
        for (k in 0 until cuantas) {
            if (x > w - 12 * u) break
            val quinta = k % 5 == 4
            val d = (if (quinta) 3.4f else 2.2f) * u; val a = (if (quinta) 1.2f else 0.85f) * u
            drawPath(Path().apply { moveTo(x - a, 0f); lineTo(x + a, 0f); lineTo(x + 0.15f * u, d); close() }, mezcla(cara, Color.Black, if (oscuro) 0.6f else 0.45f))
            drawLine(filoLuz(oscuro), Offset(x + a, 0f), Offset(x + 0.15f * u, d), 0.45f * u)
            x += if (quinta) 5.2f * u else 3.2f * u
        }
        // La clavija de roble.
        val p = Offset(w - 6 * u, (h - 1.4f * u) / 2)
        val clavija = n(TABLA, oscuro, 0.7f)
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), 2.1f * u, p + Offset(0.3f * u, 0.4f * u))
        drawCircle(clavija, 1.8f * u, p)
        drawCircle(mezcla(clavija, Color.White, if (oscuro) 0.05f else 0.25f), 1f * u, p - Offset(0.4f * u, 0.4f * u))
        drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- tarjetas de Hoy, Día y Vida

    override val margenTarjeta: Dp = 12.dp

    /**
     * El fondo donde va el texto de cada tarjeta: el barro de la pared para la cifra; el barro junto al hogar para lo que
     * falta; el cielo de la noche sobre el oppidum (oscuro también de día) para la noche; la lana cruda para Vida.
     */
    private fun fondoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> if (oscuro) Color(0xFF171D16) else Color(0xFFE6DAB8)
        TipoTarjeta.PENDIENTE -> if (oscuro) Color(0xFF1B1F17) else Color(0xFFEADFC2)
        TipoTarjeta.NOCHE -> if (oscuro) Color(0xFF0B110D) else Color(0xFF142019)
        TipoTarjeta.VIDA -> if (oscuro) Color(0xFF191E17) else Color(0xFFEEE5CD)
    }

    override fun tintaTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tinta(oscuro)
        TipoTarjeta.PENDIENTE -> tinta(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFE6E1CF)
        TipoTarjeta.VIDA -> tinta(oscuro)
    }

    override fun rotuloTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> tenue(oscuro)
        TipoTarjeta.PENDIENTE -> tenue(oscuro)
        TipoTarjeta.NOCHE -> Color(0xFFA9AE96)
        TipoTarjeta.VIDA -> tenue(oscuro)
    }

    override fun acentoTarjeta(tipo: TipoTarjeta, oscuro: Boolean): Color = when (tipo) {
        TipoTarjeta.METRICA -> verde(oscuro)
        TipoTarjeta.PENDIENTE -> if (oscuro) oro(true) else ocreRojo(false)
        TipoTarjeta.NOCHE -> oro(true)
        TipoTarjeta.VIDA -> if (oscuro) verde(true) else oro(false)
    }

    /**
     * Las tarjetas de la casa grande. La cifra de Hoy es un **lienzo de la pared**: el barro con su paja entre dos postes de
     * roble, bajo la viga con sus clavijas y sobre el umbral donde se apoyan las barras; destacada, el barro se marca y de una
     * clavija cuelga un torques de oro, el premio. «Te falta hoy» es la **pared junto al hogar**, con la hoguera encendida en
     * la esquina, porque todavía se vela; completa, quedan las brasas con su humo y la ramita de muérdago. La noche es el
     * **cielo sobre el oppidum**: la luna y las estrellas, la colina con su empalizada y sus casas de paja y, a lo lejos, en el llano,
     * las hogueras del campamento romano; sellada, se enciende la hoguera en lo alto del oppidum y las puertas se alumbran.
     * Vida es la **lana a cuadros** de un manto, con su orilla tejida y su fleco. Al tocarlas se oscurecen.
     */
    override fun DrawScope.tarjeta(e: EstadoTarjeta, oscuro: Boolean) {
        val u = density
        if (size.width < 24 * u || size.height < 24 * u) return
        val base = fondoTarjeta(e.tipo, oscuro).let { if (e.presionado) mezcla(it, if (oscuro) Color.Black else Color(0xFF7E6A44), 0.14f) else it }
        when (e.tipo) {
            TipoTarjeta.METRICA -> tarjetaPared(e, base, oscuro)
            TipoTarjeta.PENDIENTE -> tarjetaHogar(e, base, oscuro)
            TipoTarjeta.NOCHE -> tarjetaNoche(e, base)
            TipoTarjeta.VIDA -> tarjetaLana(e, base, oscuro)
        }
    }

    /** El lienzo de pared de la cifra de Hoy (ver [tarjeta]). */
    private fun DrawScope.tarjetaPared(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val b = 5 * u
        val fondo = h - 1.6f * u
        val todo = Rect(0f, 0f, w, fondo)
        drawRect(sombra(oscuro), Offset(0.8f * u, 1.6f * u), todo.size)
        val umbral = 4.6f * u
        val dentro = Rect(b, b, w - b, fondo - umbral)
        drawRect(base, dentro.topLeft, dentro.size)
        clipRect(dentro.left, dentro.top, dentro.right, dentro.bottom) {
            paja(dentro, if (oscuro) PAJA.copy(alpha = 0.06f) else PAJA.copy(alpha = 0.85f), if (oscuro) Color.Black.copy(alpha = 0.3f) else BARRO.copy(alpha = 0.45f), e.semilla + 13, 0.55f)
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Color.Transparent), dentro.top, dentro.top + 6 * u), dentro.topLeft, Size(dentro.width, 6 * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Color.Transparent), dentro.left, dentro.left + 4 * u), dentro.topLeft, Size(4 * u, dentro.height))
        }
        val roble = if (e.presionado) mezcla(n(TABLA, oscuro, 0.72f), Color.Black, 0.1f) else n(TABLA, oscuro, 0.72f)
        madera(Rect(0f, 0f, b, fondo), roble, e.semilla + 1, true, oscuro)
        madera(Rect(w - b, 0f, w, fondo), mezcla(roble, Color.Black, 0.12f), e.semilla + 2, true, oscuro)
        madera(Rect(0f, 0f, w, b), mezcla(roble, Color.White, if (oscuro) 0.02f else 0.08f), e.semilla + 3, false, oscuro)
        madera(Rect(0f, fondo - umbral, w, fondo), mezcla(roble, Color.Black, 0.06f), e.semilla + 4, false, oscuro)
        drawLine(filoLuz(oscuro), Offset(0f, 0.6f * u), Offset(w, 0.6f * u), 0.8f * u)
        drawLine(filoLuz(oscuro), Offset(b, fondo - umbral + 0.5f * u), Offset(w - b, fondo - umbral + 0.5f * u), 0.8f * u)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(0f, b), Offset(w, b), 0.6f * u)
        listOf(Offset(b / 2, b / 2), Offset(w - b / 2, b / 2)).forEach { p ->
            drawCircle(mezcla(roble, Color.Black, 0.45f), 1.4f * u, p)
            drawCircle(mezcla(roble, Color.White, if (oscuro) 0.05f else 0.2f), 0.85f * u, p - Offset(0.2f * u, 0.2f * u))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), todo.topLeft, todo.size, style = Stroke(0.6f * u))
        if (e.destacado) {
            drawRect(acentoTarjeta(e.tipo, oscuro).copy(alpha = 0.85f), dentro.topLeft, dentro.size, style = Stroke(1.2f * u))
            // El torques que cuelga de una clavija clavada en el barro.
            val clavo = Offset(w - b - 10.5f * u, maxOf(b + 9 * u, h * 0.28f))
            val r = 6.6f * u
            with(TrazosGalia) { torques(Offset(clavo.x, clavo.y + r - 0.6f * u), r, 2.4f * u, if (oscuro) mezcla(ORO, NOCHE, 0.25f) else ORO, if (oscuro) mezcla(ORO_BRILLO, NOCHE, 0.3f) else ORO_BRILLO) }
            drawCircle(mezcla(roble, Color.Black, 0.3f), 1.3f * u, clavo)
            drawCircle(mezcla(roble, Color.White, if (oscuro) 0.05f else 0.25f), 0.7f * u, clavo - Offset(0.25f * u, 0.25f * u))
        }
    }

    /** La pared junto al hogar de «Te falta hoy» (ver [tarjeta]). */
    private fun DrawScope.tarjetaHogar(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El barro alisado a mano: los cantos desparejos.
        val cuantos = (w / (9 * u)).toInt().coerceIn(4, 100)
        val pts = ArrayList<Offset>()
        for (k in 0..cuantos) pts.add(Offset(w * k / cuantos, (0.4f + 1.4f * azar(e.semilla, k)) * u))
        for (k in 1..4) pts.add(Offset(w - (0.4f + 1.2f * azar(e.semilla, k + 300)) * u, h * k / 5f))
        for (k in cuantos downTo 0) pts.add(Offset(w * k / cuantos, h - (0.6f + 1.4f * azar(e.semilla, k + 200)) * u))
        for (k in 4 downTo 1) pts.add(Offset((0.4f + 1.2f * azar(e.semilla, k + 400)) * u, h * k / 5f))
        val pared = curvaGal(pts)
        translate(0.6f * u, 1.4f * u) { drawPath(pared, sombra(oscuro)) }
        drawPath(pared, base)
        val fuego = !e.destacado
        val hogar = if (fuego) Offset(w - 30 * u, minOf(h - 10 * u, 50 * u)) else Offset(w - 30 * u, h - 12 * u)
        clipPath(pared) {
            paja(Rect(0f, 0f, w, h), if (oscuro) PAJA.copy(alpha = 0.06f) else PAJA.copy(alpha = 0.8f), if (oscuro) Color.Black.copy(alpha = 0.25f) else BARRO.copy(alpha = 0.4f), e.semilla + 31, 0.5f)
            if (fuego) {
                // La luz del fuego, que alumbra la pared alrededor (de noche, la única luz cálida).
                drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = if (oscuro) 0.3f else 0.38f), FUEGO.copy(alpha = if (oscuro) 0.08f else 0.08f), FUEGO_HALO.copy(alpha = 0f)),
                    Offset(hogar.x, hogar.y - 12 * u), 80 * u), 80 * u, Offset(hogar.x, hogar.y - 12 * u))
            } else {
                drawCircle(Brush.radialGradient(listOf(BRASA.copy(alpha = if (oscuro) 0.22f else 0.14f), BRASA.copy(alpha = 0f)), hogar, 34 * u), 34 * u, hogar)
            }
            // El hollín del humo, que ennegrece el barro encima del hogar.
            drawOval(Brush.radialGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), Offset(hogar.x, 0f), 30 * u), Offset(hogar.x - 30 * u, -20 * u), Size(60 * u, 40 * u))
        }
        drawPath(pared, (if (oscuro) Color(0xFF34402F) else Color(0xFFB5A273)).copy(alpha = 0.6f), style = Stroke(0.7f * u, join = StrokeJoin.Round))
        // La losa del hogar, de piedras.
        val piedra = alFuego(PIEDRA, oscuro, if (fuego) 0.75f else 0.35f)
        for (k in -2..2) {
            val c = Offset(hogar.x + k * 7.2f * u, hogar.y + 1.4f * u + abs(k) * 0.3f * u)
            drawOval(mezcla(piedra, Color.Black, 0.25f), Offset(c.x - 3.4f * u, c.y - 1.8f * u), Size(6.8f * u, 3.6f * u))
            drawOval(mezcla(piedra, if (fuego) FUEGO_HALO else Color.White, if (fuego) 0.35f else 0.1f), Offset(c.x - 2.4f * u, c.y - 1.8f * u), Size(4.8f * u, 1.6f * u))
        }
        if (fuego) with(TrazosGalia) { hoguera(hogar, 28 * u, 0.7f) }
        else {
            // Las brasas, con su humo que sube, y la ramita de muérdago colgada encima.
            for (k in 0 until 7) {
                val c = Offset(hogar.x + (azar(e.semilla, k + 90) - 0.5f) * 14 * u, hogar.y - 0.8f * u - azar(e.semilla, k + 95) * 2 * u)
                drawOval(mezcla(BRASA, Color.Black, 0.35f), Offset(c.x - 2 * u, c.y - 1.2f * u), Size(4 * u, 2.4f * u))
                drawOval(Brush.radialGradient(listOf(FUEGO_HALO, BRASA.copy(alpha = 0f)), c, 1.8f * u), Offset(c.x - 1.4f * u, c.y - 0.9f * u), Size(2.8f * u, 1.6f * u))
            }
            val humo = if (oscuro) LUNA.copy(alpha = 0.25f) else HUMO.copy(alpha = 0.85f)
            drawPath(Path().apply {
                moveTo(hogar.x, hogar.y - 2 * u)
                cubicTo(hogar.x - 4 * u, hogar.y - 8 * u, hogar.x + 4 * u, hogar.y - 13 * u, hogar.x, hogar.y - 19 * u)
                cubicTo(hogar.x - 2 * u, hogar.y - 23 * u, hogar.x + 3 * u, hogar.y - 26 * u, hogar.x + 1.5f * u, hogar.y - 31 * u)
            }, Brush.verticalGradient(listOf(humo.copy(alpha = 0f), humo, humo.copy(alpha = 0.2f)), hogar.y - 31 * u, hogar.y - 2 * u), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            with(TrazosGalia) { muerdago(Offset(w - 12 * u, 13 * u), 16 * u, 172f, n(MUERDAGO, oscuro, 0.4f), n(TrazosGalia.BAYA, oscuro, 0.35f)) }
            drawPath(pared, acentoTarjeta(e.tipo, oscuro).copy(alpha = 0.8f), style = Stroke(1.3f * u, join = StrokeJoin.Round))
        }
    }

    /** El cielo de la noche sobre el oppidum (ver [tarjeta]). */
    private fun DrawScope.tarjetaNoche(e: EstadoTarjeta, base: Color) {
        val u = density
        val w = size.width; val h = size.height
        val sellada = e.destacado
        val forma = Path().apply { addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(5 * u))) }
        val horizonte = mezcla(base, Color(0xFF34473A), 0.45f)
        drawPath(forma, Brush.verticalGradient(listOf(base, base, horizonte), 0f, h))
        clipPath(forma) {
            // Las estrellas.
            val cuantas = (w * h / (260f * u * u)).toInt().coerceIn(8, 80)
            for (k in 0 until cuantas) drawCircle(LUNA.copy(alpha = 0.15f + 0.5f * azar(e.semilla + 61, k)), (0.3f + 0.5f * azar(e.semilla + 62, k)) * u,
                Offset(w * azar(e.semilla + 63, k), h * 0.75f * azar(e.semilla + 64, k)))
            // La luna creciente, con su halo.
            val luna = Offset(w * 0.64f, minOf(16 * u, h * 0.2f))
            drawCircle(Brush.radialGradient(listOf(LUNA.copy(alpha = 0.16f), LUNA.copy(alpha = 0f)), luna, 22 * u), 22 * u, luna)
            val creciente = Path().apply {
                op(Path().apply { addOval(Rect(luna, 5.2f * u)) }, Path().apply { addOval(Rect(luna + Offset(2.6f * u, -1.2f * u), 4.8f * u)) }, PathOperation.Difference)
            }
            drawPath(creciente, LUNA)
            // La niebla del llano.
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, TrazosGalia.NIEBLA.copy(alpha = 0.07f), Color.Transparent), h - 26 * u, h - 6 * u), Offset(0f, h - 26 * u), Size(w, 20 * u))
            // Las lomas lejanas y, en el llano, las hogueras del campamento romano.
            val lejos = mezcla(base, Color(0xFF223026), 0.6f)
            drawPath(Path().apply {
                moveTo(0f, h)
                lineTo(0f, h - 13 * u)
                var x = 0f; var k = 0
                while (x < w && k < 80) { x += 14 * u; lineTo(x, h - (11f + 4f * azar(e.semilla + 70, k)) * u); k++ }
                lineTo(w, h); close()
            }, lejos)
            for (k in 0 until 9) {
                val p = Offset(w * (0.36f + 0.016f * k) + azar(e.semilla, k + 120) * 2 * u, h - (8.6f + 1.4f * azar(e.semilla, k + 130)) * u)
                drawCircle(Brush.radialGradient(listOf(ROMA.copy(alpha = 0.5f), ROMA.copy(alpha = 0f)), p, 3 * u), 3 * u, p)
                drawCircle(mezcla(ROMA, FUEGO_HALO, 0.45f), 0.55f * u, p)
            }
            // La colina del oppidum, con la empalizada en lo llano y las casas de techo alto de paja.
            val silueta = mezcla(base, Color.Black, 0.55f)
            val a = w * 0.47f; val b = w * 0.72f; val llano = h - 17 * u
            val colina = Path().apply {
                moveTo(w * 0.36f, h)
                cubicTo(w * 0.41f, h - 4 * u, w * 0.43f, llano, a, llano)
                lineTo(b, llano)
                cubicTo(b + 10 * u, llano, w * 0.78f, h - 9 * u, w * 0.86f, h - 7 * u)
                lineTo(w, h - 6 * u); lineTo(w, h); close()
            }
            var x = a + 1.5f * u
            while (x < b - 1.5f * u) { drawLine(silueta, Offset(x, llano + 0.5f * u), Offset(x, llano - 2.6f * u), 1.4f * u); x += 2.8f * u }
            listOf(0.54f to 1f, 0.6f to 1.25f, 0.67f to 0.9f).forEachIndexed { i, (fx, t) ->
                val c = Offset(w * fx, llano - 2 * u)
                drawPath(Path().apply { moveTo(c.x - 4.8f * u * t, c.y); lineTo(c.x - 1.6f * u * t, c.y - 5.6f * u * t); lineTo(c.x + 1.6f * u * t, c.y - 5.6f * u * t); lineTo(c.x + 4.8f * u * t, c.y); close() }, silueta)
                drawRect(silueta, Offset(c.x - 3.4f * u * t, c.y), Size(6.8f * u * t, 2.2f * u))
                if (sellada) drawRect(mezcla(FUEGO, FUEGO_HALO, 0.3f), Offset(c.x - 0.7f * u, c.y + 0.6f * u), Size(1.4f * u, 1.6f * u))
                else if (i == 1) drawRect(BRASA.copy(alpha = 0.5f), Offset(c.x - 0.7f * u, c.y + 0.6f * u), Size(1.4f * u, 1.6f * u))
            }
            drawPath(colina, silueta)
            drawLine(LUNA.copy(alpha = 0.16f), Offset(a, llano), Offset(b, llano), 0.6f * u)
            if (sellada) {
                // La hoguera en lo alto del oppidum: la noche ya contada.
                val c = Offset(w * 0.63f, llano - 0.5f * u)
                drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = 0.4f), FUEGO.copy(alpha = 0.12f), FUEGO_HALO.copy(alpha = 0f)), c, 46 * u), 46 * u, c)
                llama(c, 7 * u, 2.4f * u)
                drawLine(silueta, Offset(c.x - 2.6f * u, c.y + 0.6f * u), Offset(c.x + 2.6f * u, c.y - 0.3f * u), 1.1f * u, StrokeCap.Round)
                for (k in 0..2) drawCircle(FUEGO_HALO.copy(alpha = 0.8f - k * 0.2f), 0.45f * u, Offset(c.x + (k - 1) * 1.8f * u, c.y - (9f + k * 2.8f) * u))
            }
        }
        drawPath(forma, if (sellada) oro(true).copy(alpha = 0.85f) else Color(0xFF2E3A2C), style = Stroke((if (sellada) 1.3f else 1f) * u))
    }

    /** La lana a cuadros de Vida (ver [tarjeta]). */
    private fun DrawScope.tarjetaLana(e: EstadoTarjeta, base: Color, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val tela = h - 3.5f * u
        val forma = Path().apply {
            addRoundRect(RoundRect(0f, 0f, w, tela, CornerRadius(4 * u), CornerRadius(4 * u), CornerRadius(0.8f * u), CornerRadius(0.8f * u)))
        }
        translate(0.6f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, base)
        // El dibujo del cuadro: las mismas franjas a lo largo y a lo ancho, que se cruzan.
        val franjas = listOf(LANA_VERDE to 7f, null to 4f, LANA_ROJA to 1.6f, null to 4f, LANA_VERDE to 7f, null to 3f, LANA_OCRE to 1f, null to 3f)
        val periodo = franjas.sumOf { it.second.toDouble() }.toFloat() * u
        val fuerza = if (oscuro) 0.2f else 0.1f
        fun col(c: Color) = (if (oscuro) mezcla(c, NOCHE, 0.35f) else mezcla(c, Color(0xFF3A2E1A), 0.2f)).copy(alpha = fuerza)
        clipPath(forma) {
            var x0 = -periodo * azar(e.semilla, 1); var k = 0
            while (x0 < w && k < 60) {
                var x = x0
                franjas.forEach { (c, a) -> if (c != null) drawRect(col(c), Offset(x, 0f), Size(a * u, tela)); x += a * u }
                x0 += periodo; k++
            }
            var y0 = -periodo * azar(e.semilla, 2); k = 0
            while (y0 < tela && k < 60) {
                var y = y0
                franjas.forEach { (c, a) -> if (c != null) drawRect(col(c), Offset(0f, y), Size(w, a * u)); y += a * u }
                y0 += periodo; k++
            }
            // La sarga: el sesgo menudo del tejido.
            val sarga = Path()
            var d = -tela; k = 0
            while (d < w && k < 400) { sarga.moveTo(d, tela); sarga.lineTo(d + tela, 0f); d += 1.6f * u; k++ }
            drawPath(sarga, (if (oscuro) Color.White.copy(alpha = 0.025f) else Color(0xFF6E5A36).copy(alpha = 0.06f)), style = Stroke(0.5f * u))
            // La orilla tejida, más cerrada.
            val orilla = Rect(0f, tela - 5 * u, w, tela)
            drawRect(if (oscuro) mezcla(LANA_ROJA, NOCHE, 0.55f) else mezcla(LANA_ROJA, base, 0.15f), orilla.topLeft, orilla.size)
            var x = 0.8f * u; k = 0
            while (x < w && k < 400) {
                drawLine((if (k % 2 == 0) col(LANA_OCRE) else col(LANA_VERDE)).copy(alpha = if (oscuro) 0.45f else 0.6f), Offset(x, orilla.top + 1 * u), Offset(x + 0.8f * u, orilla.bottom - 1 * u), 0.6f * u)
                x += 1.6f * u; k++
            }
            drawLine(col(LANA_OCRE).copy(alpha = 0.7f), Offset(0f, orilla.top + 0.5f * u), Offset(w, orilla.top + 0.5f * u), 0.6f * u)
        }
        // El fleco.
        val fleco = if (oscuro) mezcla(LANA_ROJA, NOCHE, 0.45f) else mezcla(LANA_ROJA, base, 0.1f)
        val pf = Path()
        var x = 2.5f * u; var k = 0
        while (x < w - 2.5f * u && k < 300) {
            pf.moveTo(x, tela - 0.4f * u); pf.lineTo(x + (azar(e.semilla + 81, k) - 0.5f) * 0.8f * u, h - (0.3f + 1.2f * azar(e.semilla + 82, k)) * u)
            x += 1.6f * u; k++
        }
        drawPath(pf, fleco, style = Stroke(0.6f * u, cap = StrokeCap.Round))
        drawPath(forma, (if (oscuro) Color(0xFF34402F) else Color(0xFFB5A273)).copy(alpha = 0.7f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- Gastos

    /** La letra de los rótulos y de las cifras: Ysabeau, de trazo humanista. */
    private val letraRotulos: FontFamily = LetraExtra.ysabeau

    override val altoBalanza: Dp = 64.dp

    /**
     * El díptico: dos tablillas de cera en sus marcos de roble, una al lado de la otra, atadas por dos cordeles que cruzan en
     * aspa: a la izquierda el gasto, a la derecha el ingreso, cada una con su estátero. La elegida tiene la cera recién alisada
     * y a la luz del fuego, el estilo posado en su marco, el oro que brilla y la cera marcada de su color; la otra queda a la
     * sombra, con la cera vieja, el oro deslucido y la letra tenue.
     */
    override fun DrawScope.balanza(e: EstadoBalanza, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 120 * u || h < 36 * u) return
        val xm = w / 2
        val b = 4.6f * u
        for (lado in 0..1) {
            val ingreso = lado == 1
            val elegida = e.ingreso == ingreso
            val r = if (ingreso) Rect(xm + 5 * u, 1 * u, w, h - 2.2f * u) else Rect(0f, 1 * u, xm - 5 * u, h - 2.2f * u)
            drawRoundRect(sombra(oscuro), r.topLeft + Offset(0.6f * u, 1.2f * u), r.size, CornerRadius(2.4f * u))
            marcoRoble(r, b, 2.4f * u, if (elegida) tabla(oscuro) else mezcla(tabla(oscuro), Color.Black, 0.12f), 17 + lado, oscuro)
            val hueco = Rect(r.left + b, r.top + b, r.right - b, r.bottom - b)
            val ceraC = if (elegida) (if (oscuro) alFuego(CERA, true, 0.62f) else mezcla(CERA, Color.White, 0.18f))
            else (if (oscuro) mezcla(CERA, NOCHE, 0.86f) else mezcla(CERA, CERA_OSC, 0.6f))
            ceraEn(hueco, ceraC, 31 + lado, oscuro, if (elegida) 1f else 0.6f)
            val moneda = Offset(hueco.left + 12.5f * u, hueco.center.y)
            if (elegida) clipRect(hueco.left, hueco.top, hueco.right, hueco.bottom) {
                drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = if (oscuro) 0.3f else 0.4f), FUEGO_HALO.copy(alpha = 0f)), moneda, hueco.width * 0.7f), hueco.width * 0.7f, moneda)
            }
            val color = if (ingreso) e.colorIngreso else e.colorGasto
            if (elegida) drawRect(color.copy(alpha = 0.85f), hueco.topLeft, hueco.size, style = Stroke(1.3f * u))
            estatero(moneda, 9 * u, oscuro, if (elegida) 1f else 0f, 7 + lado * 5, apagado = !elegida)
            val texto = if (ingreso) e.textoIngreso else e.textoGasto
            textoEn(medidor, texto, Rect(moneda.x + 12 * u, hueco.top + 1 * u, hueco.right - 4 * u, hueco.bottom - 1 * u), letraRotulos,
                legibleGal(color, ceraC, tenue = !elegida), if (elegida) 18f else 15.5f, peso = FontWeight.SemiBold)
            // El estilo, posado en el travesaño de arriba de la elegida.
            if (elegida && r.width > 100 * u) estilo(r.right - 58 * u, r.right - 12 * u, r.top + b * 0.5f, oscuro)
        }
        // Los cordeles que atan las dos hojas, en aspa, con sus agujeros.
        val cordel = n(LINO, oscuro, 0.55f)
        listOf(h * 0.3f, h * 0.7f).forEach { y ->
            val a = Offset(xm - 5 * u - b * 0.5f, y); val c = Offset(xm + 5 * u + b * 0.5f, y)
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.7f else 0.5f), 0.9f * u, a)
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.7f else 0.5f), 0.9f * u, c)
            drawLine(cordel, a, Offset(c.x, y + 3 * u), 0.9f * u, StrokeCap.Round)
            drawLine(mezcla(cordel, Color.Black, 0.15f), c, Offset(a.x, y + 3 * u), 0.9f * u, StrokeCap.Round)
        }
    }

    override val altoMes: Dp = 52.dp

    /**
     * La placa de bronce del calendario, como la de Coligny (recurso): el bronce batido con su verdín en los cantos, la raya
     * grabada que la recorre por dentro, un remache en cada esquina y, arriba y abajo, la hilera de agujeros donde se clavaba
     * la clavija del día (uno lleva la suya). El mes va al centro; en las puntas, una punta de lanza hacia el mes anterior y
     * otra hacia el siguiente.
     */
    override fun DrawScope.encabezadoMes(e: EstadoMes, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 140 * u || h < 30 * u) return
        val placa = Rect(10 * u, 5 * u, w - 10 * u, h - 5 * u)
        val bronce = if (oscuro) mezcla(BRONCE, NOCHE, 0.7f) else mezcla(BRONCE, PAJA, 0.55f)
        val forma = Path().apply { addRoundRect(RoundRect(placa, CornerRadius(2 * u))) }
        translate(0.6f * u, 1.4f * u) { drawPath(forma, sombra(oscuro)) }
        drawPath(forma, Brush.verticalGradient(listOf(mezcla(bronce, Color.White, if (oscuro) 0.04f else 0.14f), bronce, mezcla(bronce, Color.Black, 0.1f)), placa.top, placa.bottom))
        val semilla = e.titulo.hashCode()
        clipPath(forma) {
            // El batido: los golpecitos del martillo.
            val golpes = Path()
            for (k in 0 until (placa.width * placa.height / (40f * u * u)).toInt().coerceIn(0, 500)) {
                golpes.addOval(Rect(Offset(placa.left + placa.width * azar(semilla, k * 3), placa.top + placa.height * azar(semilla, k * 3 + 1)), (0.6f + 1f * azar(semilla, k * 3 + 2)) * u))
            }
            drawPath(golpes, Color.Black.copy(alpha = if (oscuro) 0.08f else 0.05f))
            // El verdín de los cantos.
            listOf(placa.topLeft, Offset(placa.right, placa.top), Offset(placa.left, placa.bottom), placa.bottomRight).forEachIndexed { i, p ->
                val r = (10f + 10f * azar(semilla, i + 50)) * u
                drawCircle(Brush.radialGradient(listOf(VERDIN.copy(alpha = if (oscuro) 0.22f else 0.38f), VERDIN.copy(alpha = 0f)), p, r), r, p)
            }
        }
        // La raya grabada de adentro.
        val raya = placa.deflate(2.6f * u)
        translate(0.5f * u, 0.5f * u) { drawRoundRect(filoLuz(oscuro), raya.topLeft, raya.size, CornerRadius(1 * u), style = Stroke(0.6f * u)) }
        drawRoundRect(hondo(oscuro), raya.topLeft, raya.size, CornerRadius(1 * u), style = Stroke(0.7f * u))
        // Los agujeros del día, arriba y abajo, y la clavija.
        val x0 = 52 * u; val x1 = w - 52 * u
        val paso = 5.4f * u
        val cuantos = ((x1 - x0) / paso).toInt()
        val hoy = if (cuantos > 0) Math.floorMod(semilla, cuantos) else 0
        for (fila in 0..1) {
            val y = if (fila == 0) placa.top + 5.2f * u else placa.bottom - 5.2f * u
            for (k in 0..cuantos) {
                val p = Offset(x0 + k * paso, y)
                drawCircle(Color.Black.copy(alpha = if (oscuro) 0.6f else 0.42f), 0.8f * u, p)
                drawCircle(filoLuz(oscuro), 0.8f * u, p + Offset(0.3f * u, 0.4f * u), style = Stroke(0.3f * u))
            }
            if (fila == 0 && cuantos > 0) {
                val p = Offset(x0 + hoy * paso, y)
                val clavija = n(TABLA, oscuro, 0.55f)
                drawCircle(Color.Black.copy(alpha = 0.3f), 1.6f * u, p + Offset(0.3f * u, 0.5f * u))
                drawCircle(clavija, 1.4f * u, p)
                drawCircle(mezcla(clavija, Color.White, if (oscuro) 0.08f else 0.3f), 0.6f * u, p - Offset(0.3f * u, 0.3f * u))
            }
        }
        // Los remaches de las esquinas.
        listOf(Offset(raya.left + 3.6f * u, placa.center.y), Offset(raya.right - 3.6f * u, placa.center.y)).forEach { p ->
            drawCircle(Color.Black.copy(alpha = 0.3f), 1.7f * u, p + Offset(0.3f * u, 0.4f * u))
            drawCircle(mezcla(bronce, Color.Black, 0.2f), 1.5f * u, p)
            drawCircle(mezcla(bronce, Color.White, if (oscuro) 0.06f else 0.35f), 0.6f * u, p - Offset(0.4f * u, 0.4f * u))
        }
        // Las puntas de lanza que llevan al mes de antes y al de después.
        val hierro = if (oscuro) mezcla(HIERRO, LUNA, 0.5f) else mezcla(HIERRO, Color.Black, 0.15f)
        val luz = if (oscuro) LUNA.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.55f)
        punta(Offset(30 * u, h / 2), 22 * u, -1f, hierro, luz, n(MADERA, oscuro, 0.3f))
        punta(Offset(w - 30 * u, h / 2), 22 * u, 1f, hierro, luz, n(MADERA, oscuro, 0.3f))
        textoEn(medidor, e.titulo, Rect(46 * u, placa.top + 7 * u, w - 46 * u, placa.bottom - 7 * u), letraRotulos, legibleGal(tinta(oscuro), bronce, minimo = 7f), 18f,
            peso = FontWeight.SemiBold, espaciado = 0.4f)
    }

    /**
     * Un renglón de la cuenta: el surco rayado al pie y, al principio, la marca: si entra, una perla de oro, como el glóbulo de
     * las monedas; si sale, una muesca cortada en la línea, del ocre rojo.
     */
    override fun DrawScope.asiento(e: EstadoAsiento, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 30 * u || h < 10 * u) return
        surco(10 * u, w - 2 * u, h - 1.4f * u, oscuro, 0.55f)
        val p = Offset(4.2f * u, h - 2.8f * u)
        if (e.ingreso) {
            val oro = if (oscuro) mezcla(ORO, NOCHE, 0.2f) else ORO
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), 2.5f * u, p + Offset(0.3f * u, 0.5f * u))
            drawCircle(Brush.radialGradient(listOf(mezcla(oro, ORO_BRILLO, 0.6f), oro, mezcla(oro, Color.Black, 0.35f)), p - Offset(0.8f * u, 0.8f * u), 3.4f * u), 2.4f * u, p)
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.3f else 0.55f), 0.65f * u, p - Offset(0.8f * u, 0.8f * u))
        } else {
            val c = ocreRojo(oscuro).copy(alpha = 0.9f)
            drawPath(Path().apply { moveTo(p.x - 2.4f * u, p.y - 2 * u); lineTo(p.x + 2.4f * u, p.y - 2 * u); lineTo(p.x + 0.2f * u, p.y + 1.6f * u); close() }, c)
            drawLine(filoLuz(oscuro), Offset(p.x + 2.4f * u, p.y - 2 * u), Offset(p.x + 0.2f * u, p.y + 1.6f * u), 0.5f * u)
        }
    }

    override val altoRotuloDia: Dp = 22.dp

    /**
     * La fecha en un listón de haya clavado a la pared con dos clavijas de roble, con mayúscula solo al principio, y detrás el
     * cordón de oro torcido que sigue hasta el borde.
     */
    override fun DrawScope.rotuloDia(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 60 * u || h < 12 * u) return
        val fecha = texto.lowercase().replaceFirstChar { it.uppercase() }
        val tam = 12.5f; val esp = 0.5f
        val ancho = minOf(anchoGal(medidor, fecha, letraRotulos, tam, FontWeight.SemiBold, esp), w * 0.62f)
        val liston = Rect(8 * u, 3 * u, 8 * u + ancho + 22 * u, h - 3 * u)
        val cara = if (oscuro) mezcla(HAYA, NOCHE, 0.78f) else HAYA
        val forma = Path().apply { addRoundRect(RoundRect(liston, CornerRadius(1.2f * u))) }
        translate(0.5f * u, 1 * u) { drawPath(forma, sombra(oscuro)) }
        clipPath(forma) { madera(liston, cara, texto.length + 41, false, oscuro, 0.35f) }
        drawLine(filoLuz(oscuro), Offset(liston.left + 1.2f * u, liston.top + 0.5f * u), Offset(liston.right - 1.2f * u, liston.top + 0.5f * u), 0.6f * u)
        drawPath(forma, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), style = Stroke(0.6f * u))
        val clavija = n(TABLA, oscuro, 0.6f)
        listOf(liston.left + 4 * u, liston.right - 4 * u).forEach { x ->
            val p = Offset(x, h / 2)
            drawCircle(Color.Black.copy(alpha = 0.3f), 1.6f * u, p + Offset(0.3f * u, 0.4f * u))
            drawCircle(clavija, 1.4f * u, p)
            drawCircle(mezcla(clavija, Color.White, if (oscuro) 0.06f else 0.28f), 0.6f * u, p - Offset(0.3f * u, 0.3f * u))
        }
        textoEn(medidor, fecha, Rect(liston.left + 8 * u, liston.top, liston.left + 8 * u + ancho + 6 * u, liston.bottom), letraRotulos,
            legibleGal(tinta(oscuro), cara), tam, peso = FontWeight.SemiBold, espaciado = esp)
        val cordel = (if (oscuro) mezcla(ORO, NOCHE, 0.42f) else mezcla(ORO, Color(0xFF5A3F08), 0.3f)).copy(alpha = 0.7f)
        if (liston.right + 16 * u < w) cordon(liston.right + 5 * u, w - 4 * u, h / 2, 1.3f * u, cordel)
    }

    // ---------------------------------------------------------------- adornos de la hoja rápida, las relaciones y la cinta

    /**
     * La hoja rápida es la meseta del oppidum (su borde de arriba sube en loma por los lados y lleva la empalizada en lo
     * llano). Cada estaca tiene su madera, su filo y su sombra, y un cordel las ata; debajo de la empalizada, el paramento de
     * la muralla, con las piedras en hiladas y las cabezas de las vigas asomando (el *murus gallicus*). En la loma de la
     * izquierda, una casa de paja con su humo; en la de la derecha, un roble con su muérdago y, de noche, a lo lejos, las
     * hogueras del campamento romano. Una raya sigue la colina y baja por los dos cantos, con el zarzo tejido entre dos
     * líneas; al pie, la hoguera encendida, el estátero al centro y, a la derecha, la hoz de oro con el muérdago.
     */
    override fun DrawScope.adornoQuick(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 160 * u || h < 120 * u) return
        val traza = if (oscuro) e.secundario.copy(alpha = e.secundario.alpha * 0.42f) else e.primario.copy(alpha = e.primario.alpha * 0.5f)
        val fino = traza.copy(alpha = traza.alpha * 0.55f)
        // El perfil de arriba, como lo corta la hoja (`QuickActivity`, rama de Galia).
        val p = 16 * u; val alto = 7 * u; val a = w * 0.24f; val b = w * 0.76f
        val izqP = bezierGal(Offset(0f, p), Offset(a * 0.55f, p), Offset(a * 0.6f, alto), Offset(a, alto), 40)
        val derP = bezierGal(Offset(b, alto), Offset(b + (w - b) * 0.4f, alto), Offset(b + (w - b) * 0.45f, p), Offset(w, p), 40)
        fun perfil(x: Float): Float {
            val tramo = if (x <= a) izqP else if (x >= b) derP else return alto
            for (i in 1 until tramo.size) if (tramo[i].x >= x) {
                val q0 = tramo[i - 1]; val q1 = tramo[i]
                val t = if (q1.x - q0.x < 0.001f) 0f else (x - q0.x) / (q1.x - q0.x)
                return q0.y + (q1.y - q0.y) * t
            }
            return tramo.last().y
        }
        // Las estacas de la empalizada: siguen hacia abajo hasta el pie de la muralla, cada una con su madera, el filo de la
        // luz, la sombra del lado de allá y la punta; entre ellas, la sombra honda.
        val paso = 11 * u; val cuantas = ((b - a) / paso).toInt(); val ini = a + ((b - a) - cuantas * paso) / 2
        val pie = 10.6f * u
        val estaca = if (oscuro) mezcla(MADERA, NOCHE, 0.6f) else mezcla(MADERA, e.fondo, 0.35f)
        if (cuantas > 0) drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.55f else 0.3f), Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f)), alto, pie),
            Offset(ini, alto), Size(cuantas * paso, pie - alto))
        for (k in 0 until cuantas) {
            val x = ini + k * paso
            val l = x + paso * 0.14f; val r = x + paso * 0.86f
            val forma = Path().apply { moveTo(l, pie); lineTo(l, 2.2f * u); lineTo(x + paso * 0.5f, 0f); lineTo(r, 2.2f * u); lineTo(r, pie); close() }
            drawPath(forma, estaca)
            // La punta aguzada a hachazos: la madera clara del corte.
            drawPath(Path().apply { moveTo(l, 2.2f * u); lineTo(x + paso * 0.5f, 0f); lineTo(r, 2.2f * u); lineTo(x + paso * 0.5f, 3.4f * u); close() },
                if (oscuro) mezcla(TABLA_LUZ, NOCHE, 0.62f) else mezcla(TABLA_LUZ, e.fondo, 0.15f))
            clipPath(forma) {
                drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Offset(x + paso * 0.58f, 0f), Size(paso * 0.3f, pie))
                listOf(0.3f, 0.44f, 0.7f).forEachIndexed { i2, f ->
                    drawLine(mezcla(MADERA, Color.Black, 0.4f).copy(alpha = if (oscuro) 0.5f else 0.4f), Offset(x + paso * f, 2.6f * u + i2 * 0.8f * u), Offset(x + paso * (f + 0.02f), pie), 0.35f * u)
                }
            }
            drawLine(filoLuz(oscuro), Offset(l + 0.4f * u, 2.4f * u), Offset(l + 0.4f * u, pie - 0.6f * u), 0.55f * u)
            drawLine(filoLuz(oscuro), Offset(l + 0.4f * u, 2.2f * u), Offset(x + paso * 0.5f, 0.3f * u), 0.55f * u)
        }
        // El cordel que ata las estacas.
        if (cuantas > 0) cordon(ini + 1 * u, ini + cuantas * paso - 1 * u, 4.4f * u, 1.2f * u, (if (oscuro) mezcla(LINO, NOCHE, 0.45f) else mezcla(LINO, MADERA, 0.75f)).copy(alpha = 0.95f))
        // El paramento de la muralla: piedras en hiladas y, entre ellas, las cabezas de las vigas del murus gallicus.
        val muro = Rect(ini - 3 * u, pie, ini + cuantas * paso + 3 * u, alto + 9.4f * u)
        val roca = if (oscuro) mezcla(PIEDRA, NOCHE, 0.72f) else mezcla(PIEDRA, e.fondo, 0.55f)
        val junta = if (oscuro) Color.Black.copy(alpha = 0.6f) else mezcla(PIEDRA, Color.Black, 0.3f).copy(alpha = 0.55f)
        if (muro.width > 0f) {
            drawRect(junta, muro.topLeft, muro.size)
            val altoH = muro.height / 2
            for (hilada in 0..1) {
                val y0 = muro.top + hilada * altoH
                var x = muro.left - (if (hilada == 0) 0f else 2.8f * u); var k = 0
                while (x < muro.right && k < 200) {
                    val largo = (4.4f + 3.2f * azar(k * 7 + hilada, 3)) * u
                    val r = Rect(maxOf(x + 0.35f * u, muro.left), y0 + 0.3f * u, minOf(x + largo - 0.35f * u, muro.right), y0 + altoH - 0.3f * u)
                    if (r.width > 0.5f * u) {
                        val tono = mezcla(roca, if (azar(k, hilada + 9) > 0.5f) Color.White else Color.Black, 0.08f * azar(k, hilada + 11))
                        drawRoundRect(tono, r.topLeft, r.size, CornerRadius(0.7f * u))
                        drawLine(filoLuz(oscuro), Offset(r.left + 0.4f * u, r.top + 0.3f * u), Offset(r.right - 0.4f * u, r.top + 0.3f * u), 0.4f * u)
                    }
                    x += largo; k++
                }
            }
            val viga = if (oscuro) mezcla(MADERA, NOCHE, 0.45f) else MADERA
            var xv = ini + paso * 0.5f; var kv = 0
            while (xv < muro.right - 2 * u && kv < 40) {
                val c = Offset(xv, muro.top + (if (kv % 2 == 0) altoH * 0.5f else altoH * 1.5f))
                drawRect(Color.Black.copy(alpha = 0.4f), Offset(c.x - 1.3f * u, c.y - 1.3f * u), Size(2.6f * u, 2.6f * u))
                drawRect(viga, Offset(c.x - 1.1f * u, c.y - 1.1f * u), Size(2.2f * u, 2.2f * u))
                drawCircle(mezcla(viga, Color.Black, 0.35f), 0.7f * u, c, style = Stroke(0.3f * u))
                xv += paso * 1.5f; kv++
            }
        }
        // La raya que sigue la colina por debajo y baja por los dos cantos, hasta una perla.
        val m = 4.5f * u
        val bajo = 9.6f * u
        val yFin = h - 24 * u
        val linea = Path().apply {
            moveTo(m, yFin)
            lineTo(m, perfil(m) + bajo)
            var x = m
            while (x < w - m) { x = minOf(x + 3 * u, w - m); lineTo(x, perfil(x) + bajo) }
            lineTo(w - m, yFin)
        }
        translate(0.6f * u, 0.6f * u) { drawPath(linea, filoLuz(oscuro).copy(alpha = filoLuz(oscuro).alpha * 0.7f), style = Stroke(0.6f * u)) }
        drawPath(linea, traza, style = Stroke(0.8f * u, join = StrokeJoin.Round))
        drawCircle(traza, 1.3f * u, Offset(m, yFin + 2 * u)); drawCircle(traza, 1.3f * u, Offset(w - m, yFin + 2 * u))
        // En la loma de la izquierda, la casa de paja; en la de la derecha, el roble y, de noche, las hogueras de Roma.
        val xc = a * 0.48f
        casaPaja(Offset(xc, perfil(xc) + bajo - 0.4f * u), 9.2f * u, oscuro, if (oscuro) 1f else 0f)
        val xr = b + (w - b) * 0.4f
        roblecito(Offset(xr, perfil(xr) + bajo - 0.4f * u), 9.4f * u, oscuro)
        if (oscuro) for (k in 0 until 4) {
            val x = w - (8f + k * 4.2f) * u
            val q = Offset(x, perfil(x) + bajo - (1.6f + 0.6f * (k % 2)) * u)
            drawCircle(Brush.radialGradient(listOf(ROMA.copy(alpha = 0.55f), ROMA.copy(alpha = 0f)), q, 2.6f * u), 2.6f * u, q)
            drawCircle(mezcla(ocreRojo(true), FUEGO_HALO, 0.3f), 0.5f * u, q)
        }
        // Los dos cantos: el zarzo tejido entre la raya y una segunda.
        val y0 = perfil(m) + bajo + 14 * u; val y1 = h - 34 * u
        if (y1 > y0 + 20 * u) {
            listOf(m, w - m).forEach { x ->
                val dentro = if (x < w / 2) x + 4.6f * u else x - 4.6f * u
                drawLine(traza, Offset(dentro, y0), Offset(dentro, y1), 0.6f * u)
                val med = (x + dentro) / 2
                for (fase in 0..1) {
                    val tejido = Path()
                    var y = y0; var k = 0
                    tejido.moveTo(med + (if (fase == 0) -1.6f else 1.6f) * u, y)
                    while (y < y1 && k < 500) {
                        y = minOf(y + 3.5f * u, y1)
                        val lado = if ((k + fase) % 2 == 0) 1.6f else -1.6f
                        tejido.lineTo(med + lado * u, y); k++
                    }
                    drawPath(tejido, fino, style = Stroke(0.55f * u, join = StrokeJoin.Round))
                }
            }
        }
        // Al pie: la hoguera, el estátero al centro con el cordón a los lados, la hoz de oro y el muérdago.
        val ys = h - 11 * u
        val hog = Offset(25 * u, h - 6.5f * u)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(FUEGO_HALO.copy(alpha = 0.18f), FUEGO_HALO.copy(alpha = 0f)), Offset(hog.x, hog.y - 8 * u), 46 * u), 46 * u, Offset(hog.x, hog.y - 8 * u))
        with(TrazosGalia) { hoguera(hog, 17 * u, 0.6f) }
        val cx = w / 2
        estatero(Offset(cx, ys), 6.6f * u, oscuro, if (oscuro) 0.4f else 0f, 29)
        val cordel = (if (oscuro) mezcla(ORO, NOCHE, 0.42f) else mezcla(ORO, Color(0xFF5A3F08), 0.3f)).copy(alpha = 0.75f)
        if (cx - 10 * u > 52 * u) {
            cordon(cx - 10.5f * u, 46 * u, ys, 1.3f * u, cordel)
            cordon(cx + 10.5f * u, w - 54 * u, ys, 1.3f * u, cordel)
        }
        with(TrazosGalia) { muerdago(Offset(w - 34 * u, h - 13 * u), 16 * u, -24f, n(MUERDAGO, oscuro, 0.35f), n(TrazosGalia.BAYA, oscuro, 0.3f)) }
        hoz(Offset(w - 21 * u, h - 7 * u), 13 * u, 16f, oscuro)
    }

    /**
     * El recuadro de Relaciones lleva una palmeta de La Tène arriba al centro (dos volutas que se abren a los lados desde
     * una perla), con la raya que corre desde ella bajo el canto de arriba hasta una perla en cada punta; abajo, una raya
     * doble con tres perlas al centro, como la gráfila de las monedas. Tenue en el centro.
     */
    override fun DrawScope.adornoRelacion(e: EstadoAdorno, oscuro: Boolean) {
        val u = density; val w = size.width; val h = size.height
        if (w < 40 * u || h < 30 * u) return
        val base = if (oscuro) e.secundario else e.primario
        val linea = base.copy(alpha = base.alpha * 0.45f); val fino = base.copy(alpha = base.alpha * 0.22f)
        val cx = w / 2; val yt = 6.5f * u
        // La palmeta: dos volutas desde la perla del centro.
        for (lado in listOf(-1f, 1f)) {
            fun q(x: Float, y: Float) = Offset(cx + lado * x * u, yt + y * u)
            val voluta = bezierGal(q(0.8f, 0.8f), q(4f, 3.6f), q(9.5f, 2.4f), q(9.4f, -0.6f), 14)
            drawPath(cintaGal(voluta) { f -> (1.5f - 0.8f * f) * u }, linea)
            drawPath(cintaGal(bezierGal(q(9.4f, -0.6f), q(9.2f, -2.6f), q(6.6f, -2.8f), q(6.4f, -0.9f), 8)) { 0.6f * u }, linea)
            drawCircle(linea, 0.95f * u, q(6.6f, -0.6f))
            if (cx - 14 * u > 10 * u) {
                drawLine(fino, q(12.5f, 0f), Offset(if (lado < 0) 8 * u else w - 8 * u, yt), 0.7f * u)
                drawCircle(linea, 1f * u, Offset(if (lado < 0) 6 * u else w - 6 * u, yt))
            }
        }
        drawCircle(linea, 1.4f * u, Offset(cx, yt + 1.2f * u))
        // Abajo, la raya doble con sus tres perlas.
        val yb = h - 5 * u
        if (cx - 8 * u > 8 * u) {
            drawLine(fino, Offset(8 * u, yb), Offset(cx - 7 * u, yb), 0.7f * u); drawLine(fino, Offset(cx + 7 * u, yb), Offset(w - 8 * u, yb), 0.7f * u)
            drawLine(fino, Offset(14 * u, yb + 1.8f * u), Offset(cx - 7 * u, yb + 1.8f * u), 0.5f * u); drawLine(fino, Offset(cx + 7 * u, yb + 1.8f * u), Offset(w - 14 * u, yb + 1.8f * u), 0.5f * u)
        }
        for (k in -1..1) drawCircle(linea, (if (k == 0) 1.3f else 0.9f) * u, Offset(cx + k * 4 * u, yb + 0.9f * u))
    }

    /** El icono de la cinta dentro del torques: el aro torcido, abierto por abajo, con sus dos remates que se miran. */
    override fun DrawScope.marcoPista(e: EstadoAdorno, oscuro: Boolean) {
        val s = size.minDimension
        val c = Offset(size.width / 2, size.height / 2 - s * 0.02f)
        with(TrazosGalia) { torques(c, s * 0.4f, s * 0.085f, e.primario, e.secundario.copy(alpha = e.secundario.alpha * 0.7f)) }
    }
}
