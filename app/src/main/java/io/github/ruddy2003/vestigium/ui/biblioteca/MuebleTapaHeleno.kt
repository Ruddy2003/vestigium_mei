package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin

/**
 * El libro del día de Helénico (lo usa `EstanteHeleno`, en `BibliotecaAntiguedad.kt`): el rollo de papiro del día, abierto
 * sobre la mesa de olivo de la misma casa ática del Ideario y del estante. Se ve desde arriba: la hoja extendida entre sus dos
 * rollos (a la izquierda, lo ya escrito, que engorda con las palabras; a la derecha, lo que queda en blanco), con el tejido de
 * sus tiras en los dos sentidos, sus juntas de pegado, la orilla gastada y alguna fibra suelta, y el umbílico asomando por los
 * dos cabos. En la hoja, arriba, la fecha a tinta y, al pie, la cuenta de las palabras, como los escribas ponían al final del
 * rollo la cuenta de sus renglones. Alrededor, poco: la franja roja del muro detrás de la mesa, la lucerna de barniz negro en
 * la esquina de atrás, un cuenco con higos y el cálamo dejado en el margen.
 *
 * La luz. De día, el sol entra por la izquierda (la puerta del patio, fuera del cuadro): un haz de borde blando que entibia la
 * mesa y la hoja, y la derecha en sombra. En modo oscuro arde la lucerna: su charco cálido alumbra lo cercano (la mitad derecha
 * de la hoja, el rollo de la derecha, la mesa) y cae con la distancia hacia abajo a la izquierda, donde entra la luna fría. El
 * sueño ([EstadoTapa.noche]) es el mismo rincón más tarde: manda la luna, que entra por la izquierda y echa sombras frías; la
 * lucerna arde baja, un segundo punto cálido; en el cuenco hay adormideras. Al abrirse, los dos rollos se apartan.
 */
internal object MuebleTapaHeleno {
    // ---------------------------------------------------------------- la paleta (la de la casa: Ideario y estante)
    /** La franja roja del muro, el filete azul y el zócalo oscuro. */
    internal val ROJO = Color(0xFF8E4334)
    internal val ZOCALO = Color(0xFF3F3338)
    internal val EGEO = Color(0xFF2E5E8E)
    /** La mesa de olivo: la cara, su veta y el canto. */
    internal val OLIVO = Color(0xFFB48D57)
    internal val VETA = Color(0xFF6A4528)
    internal val CANTO = Color(0xFF8C6336)
    /** El papiro: la cara escrita, el dorso de fuera del rollo, sus fibras y el corte de los cabos. */
    internal val PAPIRO = Color(0xFFE4D09C)
    internal val DORSO = Color(0xFFC2A06A)
    internal val FIBRA = Color(0xFF9C7E4A)
    internal val CORTE = Color(0xFFEDE0B8)
    /** La tinta de hollín: la de día y la del sueño (un poco más fría). */
    internal val TINTA = Color(0xFF22170F)
    internal val TINTA_SUENO = Color(0xFF1D1A2B)
    /** El barniz negro de la lucerna y del cuenco, la arcilla reservada, la caña del cálamo y el hueso de los umbílicos. */
    internal val BARNIZ = Color(0xFF1A1410)
    internal val ARCILLA = Color(0xFFE8A46C)
    internal val CANA = Color(0xFFD2BA78)
    internal val CANA_VERDE = Color(0xFF9A9A5C)
    internal val HUESO = Color(0xFFE9DFC8)
    /** Los higos, su hoja y las adormideras del sueño. */
    internal val HIGO = Color(0xFF5C3A56)
    internal val HIGO_LUZ = Color(0xFF9A6478)
    internal val HOJA = Color(0xFF6E8A4C)
    internal val ADORMIDERA = Color(0xFF8FA088)
    // Las luces: la noche del cuarto, la de los sueños, el aire frío del sueño en claro, la llama, la luna y el sol.
    internal val NOCHE = Color(0xFF17142A)
    internal val NOCHE_SUENOS = Color(0xFF111A36)
    internal val FRIO = Color(0xFF8C97B8)
    internal val SOMBRA_LUNA = Color(0xFF2A3156)
    internal val LLAMA = Color(0xFFFFD58A)
    internal val LUNA = Color(0xFFC8D5F0)
    internal val SOL = Color(0xFFFFE2A6)

    // ---------------------------------------------------------------- las medidas (las mismas de antes: la caja no se achica)

    val ALTO: Dp = 184.dp
    val CAJA = Rect(0.15f, 0.31f, 0.86f, 0.71f)
    val LETRA: FontFamily = Letra.garamond
    const val TAM = 13.5f
    const val LINEAS = 3

    /** La tinta del extracto: hollín sobre papiro, que la lucerna (o la luna) deja siempre a la vista. */
    fun tinta(oscuro: Boolean, noche: Boolean): Color = if (noche) TINTA_SUENO else TINTA

    // ---------------------------------------------------------------- la luz

    /**
     * El aire del momento: de día; en modo oscuro (arde la lucerna, entra un poco de luna); y en el sueño ([sueno]: en claro,
     * los colores del día un poco enfriados con la luna mandando; en oscuro, la noche más azul). [k] es cuánto llega la lucerna
     * a un punto (0 a 1) y [l], cuánto la luna.
     */
    internal class Aire(val oscuro: Boolean, val sueno: Boolean) {
        val lumbre: Boolean get() = oscuro || sueno
        /** Lo que alumbra la lucerna: entera en la noche de la casa, baja en el sueño. */
        val lampara: Float get() = if (!lumbre) 0f else if (sueno) 0.55f else 1f
        /** Lo que alumbra la luna por la izquierda: poco en la noche de la casa, mucho en el sueño. */
        val luna: Float get() = if (sueno) 1f else if (oscuro) 0.35f else 0f
        val hondo: Color get() = if (sueno) NOCHE_SUENOS else NOCHE

        fun luz(c: Color, k: Float, l: Float = 0f): Color {
            val kk = k.coerceIn(0f, 1f)
            return when {
                oscuro -> mezcla(mezcla(mezcla(c, hondo, 0.88f - 0.74f * kk.pow(0.9f)), LUNA, 0.2f * l), FILO_MUEBLE, 0.22f * kk)
                sueno -> mezcla(mezcla(mezcla(c, FRIO, 0.2f - 0.12f * kk), LUNA, 0.1f * l), FILO_MUEBLE, 0.12f * kk)
                else -> c
            }
        }
        fun sombra(a: Float): Color = when {
            oscuro -> hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
            sueno -> SOMBRA_LUNA.copy(alpha = (a * 1.1f).coerceIn(0f, 1f))
            else -> sombraMueble(a)
        }
        /** La luz de lo que mira a la izquierda: de día, crema; con la luna, fría. */
        fun claro(a: Float): Color = when {
            oscuro -> LUNA.copy(alpha = (a * 0.35f * luna).coerceIn(0f, 1f))
            sueno -> LUNA.copy(alpha = (a * 0.6f).coerceIn(0f, 1f))
            else -> luzMueble(a)
        }
        fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = (a * lampara).coerceIn(0f, 1f))
    }

    // ---------------------------------------------------------------- lo que no cambia de un dibujo a otro

    /** Los caminos que solo dependen del tamaño: la veta de la mesa, el tejido del papiro y la luz que entra. */
    private class Piezas(val w: Float, val h: Float, u: Float) {
        val vetas = List(2) { Path() }
        val vetaCanto = Path()
        /** Las tiras del papiro: las de abajo, verticales, y las de encima, horizontales, unas más oscuras y otras más claras. */
        val tiras = Path()
        val fibras = Path()
        /** Las fibras sueltas de la orilla, arriba y abajo. */
        val sueltas = Path()
        /**
         * Las tres franjas de la luz que entra por la izquierda, de la orilla del haz hacia la derecha: dos pasos de la orilla
         * blanda y todo lo de la derecha. Lo de la izquierda es el color de fondo; cada franja se pinta opaca encima (sin capas
         * de luz a todo el lienzo: pintar sale mucho más barato).
         */
        val franjas = List(4) { Path() }

        init {
            // La veta de la mesa, solo donde se ve: la franja de atrás y la de delante a todo lo ancho, y los dos márgenes de los
            // lados (lo de debajo de la hoja no se pinta).
            var y = h * 0.07f; var j = 0
            while (y < h * 0.93f) {
                val p = vetas[if (j % 3 == 2) 1 else 0]
                val amp = (0.5f + 1.6f * azar(611, j)) * u
                val fase = azar(612, j) * 6.28f
                val ondas = 1.2f + 2f * azar(613, j)
                val entera = y < h * 0.17f || y > h * 0.87f
                for ((xa, xb) in if (entera) listOf(0f to w) else listOf(0f to w * 0.16f, w * 0.84f to w)) {
                    val n0 = (xa / w * 9f).toInt(); val n1 = (xb / w * 9f).toInt().coerceAtLeast(n0 + 1)
                    p.moveTo(xa, y + amp * sin(fase + ondas * 6.28f * n0 / 9f))
                    for (s in n0 + 1..n1) p.lineTo(w * s / 9f, y + amp * sin(fase + ondas * 6.28f * s / 9f))
                }
                y += (2.6f + 3.4f * azar(614, j)) * u; j++
            }
            var yc = h * 0.94f; var jc = 0
            while (yc < h) {
                vetaCanto.moveTo(0f, yc)
                for (s in 1..10) vetaCanto.lineTo(w * s / 10f, yc + 0.7f * u * sin(azar(615, jc) * 6f + s * 1.3f))
                yc += (2.2f + 1.6f * azar(616, jc)) * u; jc++
            }
            // El tejido: tiras de 3 a 7 dp en los dos sentidos, unas un poco más oscuras y otras más claras.
            val y0 = h * 0.14f; val y1 = h * 0.9f
            var x = 0f; var i = 0
            while (x < w) {
                val ancho = (3f + 6f * azar(631, i)) * u
                if (azar(632, i) < 0.4f) tiras.addRect(Rect(x, y0, x + ancho * (0.4f + 0.5f * azar(635, i)), y1))
                x += ancho; i++
            }
            var yh = y0; i = 0
            while (yh < y1) {
                val alto = (3f + 5f * azar(633, i)) * u
                if (azar(634, i) < 0.5f) tiras.addRect(Rect(0f, yh, w, yh + alto * (0.4f + 0.5f * azar(636, i))))
                yh += alto; i++
            }
            // Unas pocas fibras finas, cortadas, a lo ancho.
            var yf = y0 + 2f * u; var k = 0
            while (yf < y1) {
                var xf = w * 0.05f * azar(621, k)
                var m = 0
                while (xf < w) {
                    val largo = w * (0.05f + 0.16f * azar(622 + k, m))
                    fibras.moveTo(xf, yf); fibras.lineTo(xf + largo, yf + 0.5f * u * (azar(625 + k, m) - 0.5f))
                    xf += largo + w * (0.08f + 0.2f * azar(626 + k, m)); m++
                }
                yf += (4.5f + 4f * azar(627, k)) * u; k++
            }
            // Las fibras sueltas que asoman por la orilla de arriba y la de abajo.
            for (q in 0 until 14) {
                val xs = w * (0.12f + 0.76f * azar(641, q))
                val arriba = q % 2 == 0
                val ys = if (arriba) h * 0.16f else h * 0.885f
                val largo = (2f + 4f * azar(642, q)) * u
                sueltas.moveTo(xs, ys); sueltas.lineTo(xs + largo * (azar(643, q) - 0.3f), ys + (if (arriba) -largo * 0.6f else largo * 0.6f))
            }
            // La orilla del haz va de (0.47 w, 0) a (0.69 w, h), en cuatro pasos de 0.035 w.
            val bordes = listOf(0.47f, 0.505f, 0.54f, 0.575f, 2f)
            for (n in 0 until 4) {
                val xa = w * bordes[n]; val xb = minOf(w * bordes[n + 1], w * 1.5f)
                franjas[n].moveTo(xa, 0f); franjas[n].lineTo(xb, 0f); franjas[n].lineTo(xb + w * 0.22f, h); franjas[n].lineTo(xa + w * 0.22f, h); franjas[n].close()
            }
        }
    }

    private var cache: Piezas? = null
    private fun piezas(w: Float, h: Float, u: Float): Piezas = cache?.takeIf { it.w == w && it.h == h } ?: Piezas(w, h, u).also { cache = it }

    // ---------------------------------------------------------------- el dibujo

    fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val a = Aire(oscuro, e.noche)
        val ab = e.abierto.coerceIn(0f, 1f)
        val pz = piezas(w, h, u)
        val pared = h * 0.075f
        val papSup = h * 0.16f; val papInf = h * 0.885f
        val canto = h * 0.93f
        // Lo ya escrito se enrolla a la izquierda y engorda a ojos vistas con las palabras (en escala de logaritmo: se nota ya
        // entre una entrada vacía y una corta); lo que queda en blanco, a la derecha.
        val lleno = (ln(1f + e.palabras.coerceAtLeast(0) / 20f) / ln(151f)).coerceIn(0f, 1f)
        val dIzq = h * (0.055f + 0.17f * lleno)
        val dDer = h * 0.145f
        val izq1 = maxOf(w * 0.125f, dIzq + h * 0.02f) - ab * w * 0.12f
        val izq0 = izq1 - dIzq
        val der0 = w * 0.88f + ab * w * 0.13f
        // La lucerna: en la esquina de atrás, a la derecha; la llama sale del pico.
        val esc = 1.35f
        val lamp = Offset(der0 - h * 0.11f, h * 0.152f)
        val llama = Offset(lamp.x - h * 0.15f * esc, lamp.y - h * 0.062f * esc)
        fun cerca(p: Offset): Float = (1f - (p - llama).getDistance() / (w * 0.75f)).coerceIn(0f, 1f) * a.lampara
        fun lunaEn(p: Offset): Float = a.luna * (1f - (p.x / w) * 1.2f).coerceIn(0f, 1f)
        /** Hacia dónde cae la sombra de algo en [p]: de día y con la luna, a la derecha y abajo; con la lucerna, lejos de la llama. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!oscuro || e.noche) return Offset(largo * 0.9f, largo * 0.55f)
            val v = p - llama; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 1.4f)
        }
        val lejos = 0.04f * a.lampara

        // ---- el muro detrás de la mesa: la franja roja, el filete azul, la orilla del zócalo y el hollín de la lucerna
        val tonosMuro = tonos(ROJO, a)
        drawRect(tonosMuro[0], Offset.Zero, Size(w, pared))
        clipRect(0f, 0f, w, pared) { for (n in 0 until 4) drawPath(pz.franjas[n], tonosMuro[n + 1]) }
        pinceladas(Rect(0f, 0f, w, pared), a.luz(mezcla(ROJO, HUESO, 0.25f), lejos), 601, 10, u, 0.12f)
        drawRect(a.luz(EGEO, lejos), Offset(0f, pared * 0.18f), Size(w, h * 0.008f))
        drawRect(a.luz(ZOCALO, lejos), Offset(0f, pared - h * 0.014f), Size(w, h * 0.014f))
        for (k in 0 until 4) {
            val r = h * (0.012f + 0.01f * k)
            drawOval(a.sombra(0.07f), Offset(llama.x - r, llama.y - h * 0.035f - h * 0.014f * k - r), Size(r * 2f, r * 1.6f))
        }

        // ---- la mesa de olivo: la cara con su veta y el canto de delante
        val tonosMesa = tonos(OLIVO, a)
        val mesa = tonosMesa[1]
        // La mesa solo donde se ve alrededor de la hoja (lo de debajo no se pinta).
        drawRect(tonosMesa[0], Offset(0f, pared), Size(w, papSup + h * 0.03f - pared))
        drawRect(tonosMesa[0], Offset(0f, papInf - h * 0.03f), Size(w, canto - papInf + h * 0.03f))
        drawRect(tonosMesa[0], Offset(0f, papSup), Size(w * 0.16f, papInf - papSup))
        drawRect(tonosMesa[0], Offset(w * 0.84f, papSup), Size(w * 0.16f, papInf - papSup))
        // Las franjas de la luz en la mesa, solo donde se ve (la franja de atrás, la de delante y el margen de la derecha).
        for ((ya, yb) in listOf(pared to papSup + h * 0.02f, papInf - h * 0.02f to canto)) clipRect(0f, ya, w, yb) { for (n in 0 until 4) drawPath(pz.franjas[n], tonosMesa[n + 1]) }
        drawRect(tonosMesa[4], Offset(w * 0.86f, papSup), Size(w * 0.14f, papInf - papSup))
        clipRect(0f, pared, w, canto) {
            val veta = a.luz(VETA, lejos)
            drawPath(pz.vetas[0], veta.copy(alpha = 0.3f), style = Stroke(0.8f * u))
            drawPath(pz.vetas[1], mezcla(mesa, Color.White, 0.25f).copy(alpha = 0.3f), style = Stroke(0.6f * u))
        }
        drawRect(a.sombra(0.4f), Offset(0f, pared), Size(w, 1.4f * u))
        drawRect(a.luz(CANTO, 0f, a.luna * 0.4f), Offset(0f, canto), Size(w, h - canto))
        clipRect(0f, canto, w, h) { drawPath(pz.vetaCanto, a.luz(VETA, 0f).copy(alpha = 0.4f), style = Stroke(0.7f * u)) }
        if (oscuro && !e.noche) drawRect(a.filo(0.3f), Offset(w * 0.5f, canto), Size(w * 0.5f, 1.2f * u))
        else drawRect(a.claro(0.45f), Offset(0f, canto), Size(w * 0.6f, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(0f, h - 2f * u), Size(w, 2f * u))

        // ---- la hoja de papiro, extendida entre los dos rollos
        val x0 = izq0 + dIzq * 0.5f; val x1 = der0 + dDer * 0.5f
        val hoja = hojaPapiro(x0, x1, papSup, papInf, e.semilla)
        val sh = caida(Offset(w * 0.5f, h * 0.5f), h * 0.03f)
        val sombraHoja = a.sombra(if (oscuro) 0.42f else 0.26f)
        drawRect(sombraHoja, Offset(x0 + sh.x, papInf - h * 0.02f), Size(x1 - x0, h * 0.02f + sh.y.coerceAtLeast(1f * u)))
        if (sh.y < 0f) drawRect(sombraHoja, Offset(x0 + sh.x, papSup + sh.y), Size(x1 - x0, -sh.y))
        val tonosHoja = tonosPapiro(a)
        drawPath(hoja, tonosHoja[0])
        clipRect(x0, papSup + h * 0.012f, x1, papInf - h * 0.012f) { for (n in 0 until 4) drawPath(pz.franjas[n], tonosHoja[n + 1]) }
        clipRect(x0, papSup + h * 0.006f, x1, papInf - h * 0.02f) {
            // El tejido: las tiras de abajo (verticales) y las de encima (horizontales), tenues.
            val oscura = if (oscuro) a.hondo else FIBRA
            drawPath(pz.tiras, LUZ_MUEBLE.copy(alpha = if (oscuro) 0.04f else 0.09f))
            drawPath(pz.fibras, oscura.copy(alpha = 0.16f), style = Stroke(0.5f * u))
            // Las juntas de pegado (kolléseis): donde una hoja monta sobre la siguiente, una franja apenas más oscura.
            for (f in listOf(0.31f, 0.5f, 0.69f)) {
                val xj = w * f + (azar(e.semilla, (f * 100).toInt()) - 0.5f) * w * 0.02f
                drawRect(a.sombra(0.08f), Offset(xj, papSup), Size(3f * u, papInf - papSup))
                drawRect(a.sombra(0.18f), Offset(xj, papSup), Size(0.6f * u, papInf - papSup))
                drawRect(LUZ_MUEBLE.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(xj + 3f * u, papSup), Size(0.6f * u, papInf - papSup))
            }
            // Una mancha de agua vieja, con su cerco.
            val mx = w * (0.26f + 0.08f * azar(e.semilla, 3))
            drawOval(a.sombra(0.05f), Offset(mx, h * 0.72f), Size(h * 0.17f, h * 0.075f))
            drawOval(a.sombra(0.1f), Offset(mx, h * 0.72f), Size(h * 0.17f, h * 0.075f), style = Stroke(0.8f * u))
            // La hoja se levanta junto a cada rollo: una franja de sombra del lado de la hoja.
            drawRect(a.sombra(0.12f), Offset(izq1, papSup), Size(h * 0.06f, papInf - papSup))
            drawRect(a.sombra(0.12f), Offset(der0 - h * 0.04f, papSup), Size(h * 0.04f, papInf - papSup))
        }
        drawPath(hoja, a.sombra(0.32f), style = Stroke(0.6f * u))
        clipRect(x0 + h * 0.06f, 0f, x1 - h * 0.06f, h) { drawPath(pz.sueltas, a.luz(mezcla(PAPIRO, FIBRA, 0.3f), lejos), style = Stroke(0.6f * u, cap = StrokeCap.Round)) }

        // ---- la luz que entra: el sol de día, la luna de noche; y el charco de la lucerna
        luzDeLaLucerna(a, llama, w)

        // ---- el cuenco, detrás de la hoja, junto al rollo de la izquierda
        val cuenco = Offset(izq1 + h * 0.16f, h * 0.112f)
        with(PiezasTapaHeleno) { cuencoFrutas(cuenco, h * 1.3f, a, cerca(cuenco), lunaEn(cuenco), e.noche, caida(cuenco, h * 0.035f)) }

        // ---- los dos rollos
        val cIzq = Offset(izq0 + dIzq * 0.5f, h * 0.5f)
        val cDer = Offset(der0 + dDer * 0.5f, h * 0.5f)
        val luzDeLaIzquierda = !oscuro || e.noche
        rollo(izq0, dIzq, papSup - h * 0.012f, papInf + h * 0.006f, a, cerca(cIzq), lunaEn(cIzq), if (luzDeLaIzquierda) -1f else 1f, caida(cIzq, h * 0.045f))
        rollo(der0, dDer, papSup - h * 0.012f, papInf + h * 0.006f, a, cerca(cDer), lunaEn(cDer), if (luzDeLaIzquierda) -1f else (if (llama.x > cDer.x) 1f else -1f), caida(cDer, h * 0.045f))

        // ---- la lucerna, detrás de la hoja
        with(PiezasTapaHeleno) { lucerna(lamp, h * esc, a, cerca(lamp), caida(lamp, h * 0.04f)) }

        // ---- lo escrito a tinta: la fecha arriba y la cuenta de las palabras al pie
        val tinta = if (e.noche) TINTA_SUENO else TINTA
        fecha(medidor, e.fecha, Rect(w * 0.22f, h * 0.168f, w * 0.78f, h * 0.272f), tinta)
        drawLine(tinta.copy(alpha = 0.5f), Offset(w * 0.4f, h * 0.29f), Offset(w * 0.6f, h * 0.29f), 0.8f * u, StrokeCap.Round)
        drawCircle(tinta.copy(alpha = 0.5f), 1.1f * u, Offset(w * 0.5f, h * 0.29f))
        if (e.palabras > 0) {
            textoEn(medidor, palabrasHeleno(e.palabras), Rect(w * 0.55f, h * 0.735f, w * 0.855f, h * 0.835f), LETRA, tinta.copy(alpha = 0.78f), 11f, espaciado = 0.4f, minimo = 7f)
            // Una gota de tinta que se escapó del cálamo.
            val g = Offset(h * 0.74f, h * 0.83f)
            drawCircle(tinta.copy(alpha = 0.75f), 1.6f * u, g)
            drawCircle(tinta.copy(alpha = 0.55f), 0.8f * u, g + Offset(3.2f * u, 1.2f * u))
        }

        // ---- el cálamo, dejado en el margen de abajo
        val cCal = Offset(h * 0.35f, h * 0.88f)
        with(PiezasTapaHeleno) { calamo(Offset(-h * 0.03f, h * 0.975f), Offset(h * 0.7f, h * 0.8f), h, a, cerca(cCal), lunaEn(cCal), !e.vacio, caida(cCal, h * 0.025f)) }

        // ---- la llama, encima de todo, con su propio color
        if (a.lumbre) with(PiezasTapaHeleno) { llamita(llama, h * esc * (if (e.noche) 0.055f else 0.075f)) }
    }

    /**
     * El punto caliente de la lucerna encendida, encima de la mesa y de la hoja (en el sueño, bajo). Lo demás de su luz ya va
     * en los tonos de las franjas y en los objetos. Una mancha chica: pintar un charco a todo el lienzo cuesta mucho.
     */
    private fun DrawScope.luzDeLaLucerna(a: Aire, llama: Offset, w: Float) {
        if (!a.lumbre) return
        mancha(llama, w * 0.2f, mezcla(FILO_MUEBLE, LLAMA, 0.5f).copy(alpha = 0.36f * a.lampara))
    }

    /**
     * Los cuatro tonos de una superficie según la luz, de la izquierda a la derecha (lo de la izquierda de la orilla del haz,
     * los dos pasos de la orilla y lo de la derecha). De día: el sol entibia la izquierda y la derecha queda en sombra. En el
     * sueño en claro: la luna enfría la izquierda y la derecha queda en una sombra fría. De noche: a la izquierda, lejos de la
     * lucerna, hondo y frío de luna; a la derecha, junto a ella, cálido. En el sueño de noche, la luna plateando la izquierda.
     */
    private fun tonos(c: Color, a: Aire): List<Color> {
        val extremos = when {
            !a.lumbre -> mezcla(c, SOL, 0.16f) to mezcla(c, SOMBRA_MUEBLE, 0.1f)
            !a.oscuro -> mezcla(mezcla(c, FRIO, 0.18f), LUNA, 0.2f) to mezcla(mezcla(c, FRIO, 0.18f), SOMBRA_LUNA, 0.16f)
            a.sueno -> a.luz(c, 0.05f, 1f) to a.luz(c, 0.22f, 0f)
            else -> a.luz(c, 0.02f, 0.6f) to a.luz(c, 0.55f, 0f)
        }
        return List(5) { mezcla(extremos.first, extremos.second, it / 4f) }
    }

    /**
     * Los tonos del papiro, como [tonos] pero sin bajar de lo que el extracto necesita para leerse (4.5 : 1 con la tinta):
     * es lo que la lucerna o la luna alumbran.
     */
    private fun tonosPapiro(a: Aire): List<Color> = when {
        !a.oscuro -> tonos(PAPIRO, a)
        a.sueno -> listOf(
            mezcla(mezcla(PAPIRO, NOCHE_SUENOS, 0.28f), LUNA, 0.26f),
            mezcla(mezcla(PAPIRO, NOCHE_SUENOS, 0.31f), LUNA, 0.19f),
            mezcla(mezcla(PAPIRO, NOCHE_SUENOS, 0.35f), LUNA, 0.12f),
            mezcla(mezcla(PAPIRO, NOCHE_SUENOS, 0.38f), LUNA, 0.05f),
            mezcla(mezcla(PAPIRO, NOCHE_SUENOS, 0.3f), FILO_MUEBLE, 0.1f),
        )
        else -> listOf(
            mezcla(mezcla(PAPIRO, NOCHE, 0.4f), LUNA, 0.1f),
            mezcla(mezcla(PAPIRO, NOCHE, 0.34f), FILO_MUEBLE, 0.03f),
            mezcla(mezcla(PAPIRO, NOCHE, 0.26f), FILO_MUEBLE, 0.07f),
            mezcla(mezcla(PAPIRO, NOCHE, 0.18f), FILO_MUEBLE, 0.11f),
            mezcla(mezcla(PAPIRO, NOCHE, 0.08f), FILO_MUEBLE, 0.16f),
        )
    }

    // ---------------------------------------------------------------- las piezas

    private fun corrido(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

    /** «1 palabra», «N palabras», como al pie del rollo. */
    private fun palabrasHeleno(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

    /** La fecha a tinta, en un renglón; si no cabe, en dos (partida por el « · » más cercano a la mitad). */
    private fun DrawScope.fecha(medidor: TextMeasurer, f: String, zona: Rect, tinta: Color) {
        if (f.length <= 30) {
            textoEn(medidor, f, zona, LETRA, tinta.copy(alpha = 0.9f), 16f, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 8f)
            return
        }
        val cortes = Regex(" · ").findAll(f).map { it.range.first }.toList()
        if (cortes.isEmpty()) { textoEn(medidor, f, zona, LETRA, tinta.copy(alpha = 0.9f), 14f, peso = FontWeight.SemiBold, minimo = 7f); return }
        val k = cortes.minBy { kotlin.math.abs(it - f.length / 2) }
        val corte = zona.top + zona.height * 0.56f
        textoEn(medidor, f.substring(0, k), Rect(zona.left, zona.top, zona.right, corte), LETRA, tinta.copy(alpha = 0.9f), 14f, peso = FontWeight.SemiBold, minimo = 7f)
        textoEn(medidor, f.substring(k + 3), Rect(zona.left, corte, zona.right, zona.bottom), LETRA, tinta.copy(alpha = 0.8f), 11f, minimo = 7f)
    }

    /** El contorno de la hoja extendida: la orilla de arriba y la de abajo, gastadas, con un mordisco donde se rompió. */
    private fun hojaPapiro(x0: Float, x1: Float, y0: Float, y1: Float, semilla: Int): Path = Path().apply {
        val n = 16
        val alto = y1 - y0
        moveTo(x0, y0)
        for (i in 1..n) {
            val x = x0 + (x1 - x0) * i / n
            lineTo(x, y0 + alto * 0.011f * (azar(semilla, 40 + i) - 0.35f))
        }
        lineTo(x1, y1)
        for (i in n - 1 downTo 0) {
            val x = x0 + (x1 - x0) * i / n
            val roto = if (i == 5) alto * 0.03f else if (i == 6) alto * 0.012f else 0f
            lineTo(x, y1 - alto * 0.011f * azar(semilla, 60 + i) - roto)
        }
        close()
    }

    /**
     * Un rollo tumbado, visto desde arriba, con volumen de cilindro: su sombra arrojada (con color) en la hoja y en la mesa, el
     * claro y el brillo del lado de la luz, la sombra propia del otro y la luz que rebota en su orilla; el cabo de arriba más
     * claro, el de delante con la espiral de las vueltas, y el umbílico de hueso asomando. [lado]: de dónde viene la luz (-1,
     * de la izquierda).
     */
    private fun DrawScope.rollo(x0: Float, d: Float, y0: Float, y1: Float, a: Aire, k: Float, l: Float, lado: Float, caida: Offset) {
        val u = density
        val ry = d * 0.26f
        val cx = x0 + d / 2
        // La sombra arrojada: honda junto al rollo y deshecha más lejos.
        drawRoundRect(a.sombra(if (a.oscuro) 0.42f else 0.28f), Offset(x0 + caida.x, y0 + caida.y), Size(d, y1 - y0 + ry), CornerRadius(d * 0.5f))
        // El umbílico de atrás, que asoma por encima del cabo de arriba.
        val hueso = a.luz(HUESO, k, l)
        val vara = a.luz(mezcla(HUESO, BARNIZ, 0.55f), k, l)
        drawLine(vara, Offset(cx, y0 - ry * 0.3f), Offset(cx, y0 - ry * 1.7f), d * 0.14f)
        drawOval(hueso, Offset(cx - d * 0.17f, y0 - ry * 2.4f), Size(d * 0.34f, ry * 1.1f))
        drawOval(a.sombra(0.3f), Offset(cx - d * 0.17f, y0 - ry * 2.4f), Size(d * 0.34f, ry * 1.1f), style = Stroke(0.6f * u))
        // El cuerpo: el dorso del papiro, más pardo y más áspero; el cabo de arriba recibe más luz.
        val dorso = a.luz(DORSO, k, l)
        drawRect(dorso, Offset(x0, y0), Size(d, y1 - y0))
        drawOval(a.luz(mezcla(DORSO, CORTE, 0.35f), k, l), Offset(x0, y0 - ry), Size(d, ry * 2))
        // El volumen a lo ancho, en bandas: brillo, claro, medio, sombra propia y la luz que rebota en la orilla.
        val luzC = if (lado < 0) 0f else 1f
        fun banda(f0: Float, f1: Float): Pair<Float, Float> = if (lado < 0) (x0 + d * f0) to (d * (f1 - f0)) else (x0 + d * (1f - f1)) to (d * (f1 - f0))
        val lumbreDelLado = a.oscuro && !a.sueno
        val claro = if (lumbreDelLado) a.filo(0.5f * k / a.lampara.coerceAtLeast(0.01f)) else a.claro(0.4f)
        clipRect(x0, y0 - ry, x0 + d, y1) {
            banda(0.08f, 0.38f).let { (x, ww) -> drawRect(claro.copy(alpha = claro.alpha * 0.55f), Offset(x, y0 - ry), Size(ww, y1 - y0 + ry)) }
            banda(0.16f, 0.24f).let { (x, ww) -> drawRect(claro, Offset(x, y0 - ry), Size(ww, y1 - y0 + ry)) }
            banda(0.5f, 1f).let { (x, ww) -> drawRect(a.sombra(0.14f), Offset(x, y0 - ry), Size(ww, y1 - y0 + ry)) }
            banda(0.64f, 0.9f).let { (x, ww) -> drawRect(a.sombra(0.2f), Offset(x, y0 - ry), Size(ww, y1 - y0 + ry)) }
            banda(0.92f, 1f).let { (x, ww) -> drawRect(if (a.oscuro) a.sombra(0.1f) else luzMueble(0.12f), Offset(x, y0 - ry), Size(ww, y1 - y0 + ry)) }
            // Las vueltas que se ven por fuera: unas líneas a lo largo, muy tenues.
            for (j in 1..3) drawLine(a.sombra(0.1f), Offset(x0 + d * (0.2f + 0.22f * j), y0), Offset(x0 + d * (0.2f + 0.22f * j), y1), 0.5f * u)
        }
        if (luzC > 0.5f && lumbreDelLado) drawLine(a.filo(0.8f * k / a.lampara.coerceAtLeast(0.01f)), Offset(x0 + d - 0.8f * u, y0), Offset(x0 + d - 0.8f * u, y1), 1f * u)
        drawRect(a.sombra(0.35f), Offset(x0, y0), Size(d, y1 - y0), style = Stroke(0.6f * u))
        // El cabo de delante: el corte de las vueltas, en espiral, con el umbílico en medio.
        val cabo = Rect(x0, y1 - ry, x0 + d, y1 + ry)
        drawOval(a.luz(CORTE, k, l), cabo.topLeft, cabo.size)
        val espiral = Path()
        for (j in 1..4) {
            val f = 1f - j * 0.2f
            espiral.addOval(Rect(cx - d * 0.5f * f + d * 0.02f * j, y1 - ry * f, cx + d * 0.5f * f + d * 0.02f * j, y1 + ry * f))
        }
        drawPath(espiral, a.sombra(0.3f), style = Stroke(0.6f * u))
        drawOval(a.sombra(0.3f), cabo.topLeft, cabo.size, style = Stroke(0.8f * u))
        // El umbílico de delante: la varilla que asoma y su perilla de hueso.
        drawLine(vara, Offset(cx + d * 0.04f, y1), Offset(cx + d * 0.04f, y1 + ry * 1.3f), d * 0.14f)
        drawOval(a.sombra(0.35f), Offset(cx - d * 0.13f + 1.4f * u, y1 + ry * 1.05f + 1.4f * u), Size(d * 0.34f, ry * 1.1f))
        drawOval(hueso, Offset(cx - d * 0.13f, y1 + ry * 1.05f), Size(d * 0.34f, ry * 1.1f))
        drawOval(a.sombra(0.3f), Offset(cx - d * 0.13f, y1 + ry * 1.05f), Size(d * 0.34f, ry * 1.1f), style = Stroke(0.6f * u))
        drawOval(if (lumbreDelLado) a.filo(0.6f) else a.claro(0.6f), Offset(cx - d * 0.08f, y1 + ry * 1.15f), Size(d * 0.12f, ry * 0.35f))
    }
}
