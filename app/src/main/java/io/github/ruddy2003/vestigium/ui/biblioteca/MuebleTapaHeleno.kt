package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El libro del día de Helénico (lo usa `EstanteHeleno`, en `BibliotecaAntiguedad.kt`): el rollo de papiro del día, abierto
 * sobre la mesa de olivo de la misma casa ática del Ideario y del estante. Se ve desde arriba: la hoja extendida entre sus dos
 * rollos (a la izquierda, lo ya escrito, que engorda con las palabras; a la derecha, lo que queda en blanco), con sus fibras
 * y sus juntas de pegado, el umbílico asomando por los dos cabos. En la hoja, arriba, la fecha a tinta y, al final, la cuenta
 * de las palabras, como los escribas ponían al pie del rollo la cuenta de sus renglones. Alrededor, poco: la franja roja del
 * muro detrás de la mesa, la lucerna de barniz negro en la esquina de atrás, un cuenco con higos y el cálamo dejado en el
 * margen. De día, el sol entra de la izquierda (por la puerta del patio, fuera del cuadro) y deja la derecha en sombra; en
 * modo oscuro arde la lucerna y lo demás se hunde en el azul violeta del cuarto, con un poco de luna por la izquierda. El
 * sueño ([EstadoTapa.noche]) es el mismo rincón más tarde: manda la luna, la lucerna arde baja y en el cuenco hay adormideras.
 * Al abrirse, los dos rollos se apartan y la hoja se despliega.
 */
internal object MuebleTapaHeleno {
    // ---------------------------------------------------------------- la paleta (la de la casa: Ideario y estante)
    /** La franja roja del muro y el zócalo oscuro. */
    private val ROJO = Color(0xFF8E4334)
    private val ZOCALO = Color(0xFF3F3338)
    private val EGEO = Color(0xFF2E5E8E)
    /** La mesa de olivo: la cara, su veta y el canto. */
    private val OLIVO = Color(0xFFB48D57)
    private val VETA = Color(0xFF6A4528)
    private val CANTO = Color(0xFF8C6336)
    /** El papiro: la cara escrita, el dorso de fuera del rollo, sus fibras y el corte de los cabos. */
    private val PAPIRO = Color(0xFFE4D09C)
    private val DORSO = Color(0xFFC2A06A)
    private val FIBRA = Color(0xFFA98B55)
    private val CORTE = Color(0xFFEDE0B8)
    /** La tinta de hollín: la de día y la del sueño (un poco más fría). */
    private val TINTA = Color(0xFF22170F)
    private val TINTA_SUENO = Color(0xFF1D1A2B)
    /** El barniz negro de la lucerna y del cuenco, la arcilla reservada, la caña del cálamo y el hueso de los umbílicos. */
    private val BARNIZ = Color(0xFF1A1410)
    private val ARCILLA = Color(0xFFE8A46C)
    private val CANA = Color(0xFFCDB57C)
    private val HUESO = Color(0xFFE9DFC8)
    /** Los higos, su hoja y las adormideras del sueño. */
    private val HIGO = Color(0xFF5C3A56)
    private val HIGO_LUZ = Color(0xFF9A6478)
    private val HOJA = Color(0xFF6E8A4C)
    private val ADORMIDERA = Color(0xFF93A07E)
    // Las luces: la noche del cuarto, la de los sueños, el anochecer del sueño en claro, la llama y la luna.
    private val NOCHE = Color(0xFF17142A)
    private val NOCHE_SUENOS = Color(0xFF111A36)
    private val ANOCHECER = Color(0xFF2B315C)
    private val LLAMA = Color(0xFFFFD58A)
    private val LUNA = Color(0xFFC8D5F0)

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
     * El aire del momento: de día, en modo oscuro (arde la lucerna) y en el sueño ([sueno]: al anochecer en claro; en oscuro,
     * la noche más azul con la luna mandando). [k] es cuánto llega la lucerna a un punto (0 a 1) y [l], cuánto la luna.
     */
    private class Aire(val oscuro: Boolean, val sueno: Boolean) {
        val lumbre: Boolean get() = oscuro || sueno
        /** Lo que alumbra la lucerna: entera en la noche de la casa, baja en el sueño. */
        val lampara: Float get() = if (!lumbre) 0f else if (sueno) 0.62f else 1f
        /** Lo que alumbra la luna por la izquierda: poco en la noche de la casa, mucho en el sueño. */
        val luna: Float get() = if (sueno) 1f else if (oscuro) 0.4f else 0f
        val hondo: Color get() = if (sueno) NOCHE_SUENOS else NOCHE

        fun luz(c: Color, k: Float, l: Float = 0f): Color {
            val kk = k.coerceIn(0f, 1f)
            return when {
                oscuro -> mezcla(mezcla(mezcla(c, hondo, 0.86f - 0.72f * kk.pow(1.1f)), LUNA, 0.14f * l), FILO_MUEBLE, 0.2f * kk)
                sueno -> mezcla(mezcla(mezcla(c, ANOCHECER, 0.42f - 0.22f * kk), LUNA, 0.1f * l), FILO_MUEBLE, 0.14f * kk)
                else -> c
            }
        }
        /** El papiro se hunde menos: es lo que la lucerna está alumbrando, y el extracto se tiene que leer en toda la hoja. */
        fun papiro(c: Color, k: Float, l: Float): Color {
            val kk = k.coerceIn(0f, 1f)
            return when {
                oscuro -> mezcla(mezcla(mezcla(c, hondo, 0.38f - 0.3f * kk), LUNA, 0.16f * l), FILO_MUEBLE, 0.12f * kk)
                sueno -> mezcla(mezcla(mezcla(c, ANOCHECER, 0.24f - 0.12f * kk), LUNA, 0.1f * l), FILO_MUEBLE, 0.08f * kk)
                else -> c
            }
        }
        fun sombra(a: Float): Color = when {
            oscuro -> hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
            sueno -> mezcla(SOMBRA_MUEBLE, ANOCHECER, 0.4f).copy(alpha = a.coerceIn(0f, 1f))
            else -> sombraMueble(a)
        }
        fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.12f else if (sueno) a * 0.5f else a)
        fun filo(a: Float): Color = FILO_MUEBLE.copy(alpha = (if (oscuro) a else if (sueno) a * 0.6f else 0f).coerceIn(0f, 1f))
    }

    // ---------------------------------------------------------------- lo que no cambia de un dibujo a otro

    /** Los caminos que solo dependen del tamaño: la veta de la mesa, las fibras del papiro y la luz que entra. */
    private class Piezas(val w: Float, val h: Float, u: Float) {
        val vetas = List(3) { Path() }
        val vetaCanto = Path()
        val fibrasOsc = Path()
        val fibrasClaras = Path()
        val sol = Path()
        val penumbra = Path()
        val sombra = Path()

        init {
            // La veta de la mesa: líneas largas que ondulan a lo ancho, en tres fuerzas.
            var y = h * 0.07f; var j = 0
            while (y < h * 0.93f) {
                val p = vetas[j % 3]
                val amp = (0.5f + 1.6f * azar(611, j)) * u
                val fase = azar(612, j) * 6.28f
                val ondas = 1.2f + 2f * azar(613, j)
                p.moveTo(0f, y)
                for (s in 1..14) {
                    val x = w * s / 14f
                    p.lineTo(x, y + amp * sin(fase + ondas * 6.28f * s / 14f))
                }
                y += (2.6f + 3.4f * azar(614, j)) * u; j++
            }
            var yc = h * 0.94f; var jc = 0
            while (yc < h) {
                vetaCanto.moveTo(0f, yc)
                for (s in 1..10) vetaCanto.lineTo(w * s / 10f, yc + 0.7f * u * sin(azar(615, jc) * 6f + s * 1.3f))
                yc += (2.2f + 1.6f * azar(616, jc)) * u; jc++
            }
            // Las fibras del papiro: trazos a lo ancho, cortados, unos más oscuros y otros más claros.
            var yf = h * 0.14f; var k = 0
            while (yf < h * 0.9f) {
                var x = -w * 0.05f * azar(621, k)
                var m = 0
                while (x < w) {
                    val largo = w * (0.05f + 0.2f * azar(622 + k, m))
                    val p = if (azar(623 + k, m) < 0.55f) fibrasOsc else fibrasClaras
                    p.moveTo(x, yf + 0.4f * u * (azar(624 + k, m) - 0.5f)); p.lineTo(x + largo, yf + 0.4f * u * (azar(625 + k, m) - 0.5f))
                    x += largo + w * (0.01f + 0.05f * azar(626 + k, m)); m++
                }
                yf += (2f + 2.6f * azar(627, k)) * u; k++
            }
            // La luz que entra por la izquierda: el sol de día, la luna de noche; su orilla y la sombra del otro lado.
            sol.moveTo(0f, 0f); sol.lineTo(w * 0.5f, 0f); sol.lineTo(w * 0.74f, h); sol.lineTo(0f, h); sol.close()
            penumbra.moveTo(w * 0.5f, 0f); penumbra.lineTo(w * 0.57f, 0f); penumbra.lineTo(w * 0.81f, h); penumbra.lineTo(w * 0.74f, h); penumbra.close()
            sombra.moveTo(w * 0.57f, 0f); sombra.lineTo(w, 0f); sombra.lineTo(w, h); sombra.lineTo(w * 0.81f, h); sombra.close()
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
        // Lo ya escrito se enrolla a la izquierda y engorda con las palabras; lo que queda en blanco, a la derecha.
        val lleno = sqrt(e.palabras.coerceIn(0, 3000) / 3000f)
        val dIzq = h * (0.085f + 0.11f * lleno)
        val dDer = h * 0.145f
        val izq1 = w * 0.125f - ab * w * 0.12f
        val izq0 = izq1 - dIzq
        val der0 = w * 0.88f + ab * w * 0.13f
        // La lucerna: en la esquina de atrás, a la derecha; la llama sale del pico.
        val esc = 1.35f
        val lamp = Offset(der0 - h * 0.11f, h * 0.152f)
        val llama = Offset(lamp.x - h * 0.15f * esc, lamp.y - h * 0.062f * esc)
        fun cerca(p: Offset): Float = (1f - (p - llama).getDistance() / (w * 0.8f)).coerceIn(0f, 1f) * a.lampara
        fun lunaEn(p: Offset): Float = a.luna * (1f - (p.x / w) * 1.1f).coerceIn(0f, 1f)
        fun c(col: Color, p: Offset) = a.luz(col, cerca(p), lunaEn(p))
        /** Hacia dónde cae la sombra de algo en [p]: de día, a la derecha y abajo; con la lucerna, lejos de la llama. */
        fun caida(p: Offset, largo: Float): Offset {
            if (!a.lumbre) return Offset(largo * 0.9f, largo * 0.55f)
            val v = p - llama; val dd = v.getDistance().coerceAtLeast(1f)
            return v / dd * (largo * 1.4f)
        }

        // ---- el muro detrás de la mesa: la franja roja, con su orilla de abajo oscura y el hollín de la lucerna
        val lejos = 0.1f * a.lampara
        val muro = a.luz(ROJO, lejos, a.luna * 0.4f)
        drawRect(muro, Offset.Zero, Size(w, pared))
        pinceladas(Rect(0f, 0f, w, pared), a.luz(mezcla(ROJO, HUESO, 0.25f), lejos), 601, 12, u, 0.12f)
        pinceladas(Rect(0f, 0f, w, pared), a.luz(mezcla(ROJO, ZOCALO, 0.4f), lejos), 602, 8, u, 0.14f)
        drawRect(a.luz(EGEO, lejos), Offset(0f, pared * 0.18f), Size(w, h * 0.008f))
        drawRect(a.luz(ZOCALO, lejos), Offset(0f, pared - h * 0.014f), Size(w, h * 0.014f))
        for (k in 0 until 5) {
            val r = h * (0.01f + 0.008f * k)
            drawOval(a.sombra(0.06f), Offset(llama.x - r + k * 0.4f * u, llama.y - h * 0.03f - h * 0.012f * k - r), Size(r * 2f, r * 1.6f))
        }

        // ---- la mesa de olivo: la cara con su veta y el canto de delante
        val mesa = a.luz(OLIVO, lejos, a.luna * 0.4f)
        drawRect(mesa, Offset(0f, pared), Size(w, canto - pared))
        clipRect(0f, pared, w, canto) {
            val veta = a.luz(VETA, lejos)
            drawPath(pz.vetas[0], veta.copy(alpha = 0.22f), style = Stroke(0.7f * u))
            drawPath(pz.vetas[1], veta.copy(alpha = 0.38f), style = Stroke(0.9f * u))
            drawPath(pz.vetas[2], mezcla(mesa, Color.White, 0.25f).copy(alpha = 0.3f), style = Stroke(0.6f * u))
        }
        drawRect(a.sombra(0.4f), Offset(0f, pared), Size(w, 1.4f * u))
        val cantoC = a.luz(CANTO, lejos * 0.6f, a.luna * 0.3f)
        drawRect(cantoC, Offset(0f, canto), Size(w, h - canto))
        clipRect(0f, canto, w, h) { drawPath(pz.vetaCanto, a.luz(VETA, lejos * 0.6f).copy(alpha = 0.4f), style = Stroke(0.7f * u)) }
        if (a.lumbre) drawRect(a.filo(0.3f * a.lampara), Offset(w * 0.45f, canto), Size(w * 0.55f, 1.2f * u))
        else drawRect(a.claro(0.45f), Offset(0f, canto), Size(w, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(0f, h - 2f * u), Size(w, 2f * u))


        // ---- la hoja de papiro, extendida entre los dos rollos
        val x0 = izq0 + dIzq * 0.5f; val x1 = der0 + dDer * 0.5f
        val hoja = hojaPapiro(x0, x1, papSup, papInf, e.semilla)
        val sh = caida(Offset(w * 0.5f, h * 0.5f), h * 0.03f)
        drawPath(corrido(hoja, sh.x, sh.y), a.sombra(if (a.lumbre) 0.4f else 0.24f))
        val papiro = a.papiro(PAPIRO, lejos, a.luna * 0.5f)
        drawPath(hoja, papiro)
        // Se recorta a un rectángulo (la hoja casi lo es): recortar al contorno con antialias cuesta mucho más al pintar.
        clipRect(x0, papSup + h * 0.008f, x1, papInf - h * 0.02f) {
            drawPath(pz.fibrasOsc, a.papiro(FIBRA, 0.2f, 0f).copy(alpha = 0.16f), style = Stroke(0.5f * u))
            drawPath(pz.fibrasClaras, a.claro(0.5f).copy(alpha = if (a.lumbre) 0.05f else 0.22f), style = Stroke(0.5f * u))
            // Las juntas de pegado (kolléseis): donde una hoja monta sobre la siguiente, una franja apenas más oscura.
            for (f in listOf(0.31f, 0.5f, 0.69f)) {
                val xj = w * f + (azar(e.semilla, (f * 100).toInt()) - 0.5f) * w * 0.02f
                drawRect(a.sombra(0.07f), Offset(xj, papSup), Size(2.6f * u, papInf - papSup))
                drawRect(a.sombra(0.16f), Offset(xj, papSup), Size(0.6f * u, papInf - papSup))
                drawRect(a.claro(0.3f), Offset(xj + 2.6f * u, papSup), Size(0.6f * u, papInf - papSup))
            }
            // Una mancha de agua vieja y el roce de los dedos en el borde de abajo.
            drawOval(a.sombra(0.07f), Offset(w * (0.6f + 0.15f * azar(e.semilla, 3)), h * 0.76f), Size(h * 0.16f, h * 0.07f))
            drawOval(a.papiro(DORSO, 0.2f, 0f).copy(alpha = 0.25f), Offset(w * 0.3f, papInf - h * 0.05f), Size(w * 0.3f, h * 0.06f))
            // La hoja se levanta junto a cada rollo: una franja de sombra del lado de la hoja.
            drawRect(a.sombra(0.16f), Offset(izq1, papSup), Size(h * 0.035f, papInf - papSup))
            drawRect(a.sombra(0.1f), Offset(izq1, papSup), Size(h * 0.07f, papInf - papSup))
            drawRect(a.sombra(0.14f), Offset(der0 - h * 0.03f, papSup), Size(h * 0.03f, papInf - papSup))
        }
        drawPath(hoja, a.sombra(0.3f), style = Stroke(0.6f * u))

        // ---- el cuenco, detrás de la hoja, junto al rollo de la izquierda
        val cuenco = Offset(izq1 + h * 0.15f, h * 0.112f)
        cuencoFrutas(cuenco, h * 1.25f, a, cerca(cuenco), lunaEn(cuenco), e.noche, caida(cuenco, h * 0.03f))

        // ---- los dos rollos
        val cIzq = Offset(izq0 + dIzq * 0.5f, h * 0.5f)
        val cDer = Offset(der0 + dDer * 0.5f, h * 0.5f)
        rollo(izq0, dIzq, papSup - h * 0.012f, papInf + h * 0.006f, a, cerca(cIzq), lunaEn(cIzq), if (llama.x > cIzq.x) 1f else -1f, caida(cIzq, h * 0.035f))
        rollo(der0, dDer, papSup - h * 0.012f, papInf + h * 0.006f, a, cerca(cDer), lunaEn(cDer), if (llama.x > cDer.x) 1f else -1f, caida(cDer, h * 0.035f))

        // ---- la luz que entra por la izquierda (el sol de día, la luna de noche) y el charco de la lucerna
        if (!a.lumbre) {
            drawPath(pz.sol, luzMueble(0.13f))
            drawPath(pz.penumbra, luzMueble(0.05f))
            drawPath(pz.sombra, sombraMueble(0.1f))
        } else {
            val fl = if (a.sueno) (if (oscuro) 0.17f else 0.15f) else 0.07f
            drawPath(pz.sol, LUNA.copy(alpha = fl))
            drawPath(pz.penumbra, LUNA.copy(alpha = fl * 0.4f))
            mancha(llama, w * 0.95f, FILO_MUEBLE.copy(alpha = 0.26f * a.lampara))
            mancha(llama, w * 0.45f, FILO_MUEBLE.copy(alpha = 0.22f * a.lampara))
            mancha(llama, w * 0.18f, LLAMA.copy(alpha = 0.3f * a.lampara))
        }

        // ---- la lucerna, detrás de la hoja (encima de su propio charco: el barniz no se lava con su luz)
        lucerna(lamp, h * esc, a, cerca(lamp), caida(lamp, h * 0.03f))

        // ---- lo escrito a tinta: la fecha arriba y la cuenta de las palabras al pie
        val tinta = if (e.noche) TINTA_SUENO else TINTA
        fecha(medidor, e.fecha, Rect(w * 0.22f, h * 0.168f, w * 0.78f, h * 0.272f), tinta)
        drawLine(tinta.copy(alpha = 0.5f), Offset(w * 0.4f, h * 0.29f), Offset(w * 0.6f, h * 0.29f), 0.8f * u, StrokeCap.Round)
        drawCircle(tinta.copy(alpha = 0.5f), 1.1f * u, Offset(w * 0.5f, h * 0.29f))
        if (e.palabras > 0) {
            textoEn(medidor, palabrasHeleno(e.palabras), Rect(w * 0.55f, h * 0.735f, w * 0.855f, h * 0.835f), LETRA, tinta.copy(alpha = 0.78f), 11f, espaciado = 0.4f, minimo = 7f)
            // Una gota de tinta que se escapó del cálamo.
            val g = Offset(h * 0.7f, h * 0.835f)
            drawCircle(tinta.copy(alpha = 0.75f), 1.6f * u, g)
            drawCircle(tinta.copy(alpha = 0.55f), 0.8f * u, g + Offset(3.2f * u, 1.2f * u))
        }

        // ---- el cálamo, dejado en el margen de abajo
        calamo(Offset(-h * 0.02f, h * 0.965f), Offset(h * 0.66f, h * 0.81f), h, a, cerca(Offset(h * 0.4f, h * 0.88f)), lunaEn(Offset(h * 0.3f, h * 0.88f)), !e.vacio, caida(Offset(h * 0.3f, h * 0.88f), h * 0.02f))

        // ---- la llama, encima de todo, con su propio color
        if (a.lumbre) llamita(llama, h * esc * (if (a.sueno) 0.055f else 0.075f))
        granoMueble(Rect(0f, 0f, w, h), oscuro, 0.7f)
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

    /** El contorno de la hoja extendida: los bordes de arriba y de abajo no son del todo rectos (el papiro se gasta). */
    private fun hojaPapiro(x0: Float, x1: Float, y0: Float, y1: Float, semilla: Int): Path = Path().apply {
        val n = 12
        moveTo(x0, y0)
        for (i in 1..n) {
            val x = x0 + (x1 - x0) * i / n
            lineTo(x, y0 + (y1 - y0) * 0.006f * (azar(semilla, 40 + i) - 0.3f))
        }
        lineTo(x1, y1)
        for (i in n - 1 downTo 0) {
            val x = x0 + (x1 - x0) * i / n
            // Un mordisco en el borde de abajo, donde se rompió.
            val roto = if (i == 4) (y1 - y0) * 0.018f else 0f
            lineTo(x, y1 - (y1 - y0) * 0.006f * azar(semilla, 60 + i) - roto)
        }
        close()
    }

    /**
     * Un rollo tumbado, visto desde arriba: el cuerpo con su luz y su sombra a lo ancho, el cabo de delante con la espiral de
     * las vueltas, y el umbílico de hueso asomando por los dos cabos. [haciaX]: de qué lado está la lucerna (1, a la derecha).
     */
    private fun DrawScope.rollo(x0: Float, d: Float, y0: Float, y1: Float, a: Aire, k: Float, l: Float, haciaX: Float, caida: Offset) {
        val u = density
        val ry = d * 0.26f
        // Su sombra en la hoja y en la mesa.
        drawRoundRect(a.sombra(if (a.lumbre) 0.42f else 0.26f), Offset(x0 + caida.x, y0 + caida.y), Size(d, y1 - y0 + ry), CornerRadius(d * 0.5f))
        // El umbílico de atrás, que asoma por encima del cabo de arriba.
        val hueso = a.luz(HUESO, k, l)
        val cx = x0 + d / 2
        val vara = a.luz(mezcla(HUESO, BARNIZ, 0.55f), k, l)
        drawLine(vara, Offset(cx, y0 - ry * 0.3f), Offset(cx, y0 - ry * 1.7f), d * 0.14f)
        drawOval(hueso, Offset(cx - d * 0.17f, y0 - ry * 2.4f), Size(d * 0.34f, ry * 1.1f))
        drawOval(a.sombra(0.3f), Offset(cx - d * 0.17f, y0 - ry * 2.4f), Size(d * 0.34f, ry * 1.1f), style = Stroke(0.6f * u))
        // El cuerpo: el dorso del papiro, más pardo y más áspero.
        val dorso = a.luz(DORSO, k, l)
        drawRect(dorso, Offset(x0, y0), Size(d, y1 - y0))
        drawOval(dorso, Offset(x0, y0 - ry), Size(d, ry * 2))
        // La luz a lo ancho: de día, el claro a la izquierda; con la lucerna, del lado de la llama.
        val sl = if (a.lumbre) haciaX else -1f
        val oscuroLado = if (sl < 0) x0 + d * 0.62f else x0
        val claroLado = if (sl < 0) x0 + d * 0.14f else x0 + d * 0.68f
        clipRect(x0, y0 - ry, x0 + d, y1) {
            drawRect(a.sombra(0.16f), Offset(if (sl < 0) x0 + d * 0.5f else x0, y0 - ry), Size(d * 0.5f, y1 - y0 + ry))
            drawRect(a.sombra(0.22f), Offset(oscuroLado, y0 - ry), Size(d * 0.38f, y1 - y0 + ry))
            drawRect(a.sombra(0.3f), Offset(if (sl < 0) x0 + d * 0.86f else x0, y0 - ry), Size(d * 0.14f, y1 - y0 + ry))
            drawRect(if (a.lumbre) a.filo(0.55f * k) else a.claro(0.4f), Offset(claroLado, y0 - ry), Size(d * 0.16f, y1 - y0 + ry))
            drawRect(if (a.lumbre) a.filo(0.3f * k) else a.claro(0.2f), Offset(claroLado - d * 0.08f, y0 - ry), Size(d * 0.32f, y1 - y0 + ry))
            // Las vueltas que se ven por fuera: unas líneas a lo largo, muy tenues.
            for (j in 1..3) drawLine(a.sombra(0.1f), Offset(x0 + d * (0.2f + 0.22f * j), y0), Offset(x0 + d * (0.2f + 0.22f * j), y1), 0.5f * u)
        }
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
        drawOval(if (a.lumbre) a.filo(0.6f * k) else a.claro(0.6f), Offset(cx - d * 0.08f, y1 + ry * 1.15f), Size(d * 0.12f, ry * 0.35f))
        if (a.lumbre && k > 0.05f) drawLine(a.filo(0.7f * k), Offset(if (haciaX > 0) x0 + d - 0.8f * u else x0 + 0.8f * u, y0), Offset(if (haciaX > 0) x0 + d - 0.8f * u else x0 + 0.8f * u, y1), 1f * u)
    }

    /**
     * La lucerna ática de barniz negro, vista un poco desde arriba: el cuerpo bajo y redondo con su agujero de llenar, el pico
     * largo hacia la izquierda (de día, con el hollín de la mecha) y el asa de cinta a la derecha.
     */
    private fun DrawScope.lucerna(pie: Offset, s: Float, a: Aire, k: Float, caida: Offset) {
        val u = density
        val kk = maxOf(k, if (a.lumbre) 0.75f else 0f)
        val barniz = a.luz(BARNIZ, kk)
        val arriba = pie.y - s * 0.05f
        // La sombra en la mesa.
        drawOval(a.sombra(if (a.lumbre) 0.45f else 0.3f), Offset(pie.x - s * 0.17f + caida.x, pie.y - s * 0.03f + caida.y * 0.4f), Size(s * 0.3f, s * 0.05f))
        // El asa de cinta.
        drawArc(barniz, -70f, 200f, false, Offset(pie.x + s * 0.045f, arriba - s * 0.03f), Size(s * 0.06f, s * 0.05f), style = Stroke(s * 0.014f))
        // El pico.
        val pico = Path().apply {
            moveTo(pie.x - s * 0.04f, arriba - s * 0.014f)
            lineTo(pie.x - s * 0.135f, arriba - s * 0.012f)
            cubicTo(pie.x - s * 0.162f, arriba - s * 0.01f, pie.x - s * 0.162f, arriba + s * 0.016f, pie.x - s * 0.135f, arriba + s * 0.018f)
            lineTo(pie.x - s * 0.04f, arriba + s * 0.026f)
            close()
        }
        drawPath(pico, barniz)
        // El cuerpo: el costado y la cara de arriba, con el anillo del borde y el agujero.
        drawOval(barniz, Offset(pie.x - s * 0.062f, pie.y - s * 0.036f), Size(s * 0.124f, s * 0.042f))
        drawRect(barniz, Offset(pie.x - s * 0.068f, arriba), Size(s * 0.136f, pie.y - s * 0.015f - arriba))
        drawOval(a.luz(mezcla(BARNIZ, ARCILLA, 0.12f), kk), Offset(pie.x - s * 0.07f, arriba - s * 0.026f), Size(s * 0.14f, s * 0.052f))
        drawOval(a.luz(BARNIZ, kk), Offset(pie.x - s * 0.048f, arriba - s * 0.017f), Size(s * 0.096f, s * 0.034f))
        drawOval(mezcla(BARNIZ, NOCHE, 0.5f), Offset(pie.x - s * 0.014f, arriba - s * 0.006f), Size(s * 0.028f, s * 0.012f))
        // El brillo del barniz: de día, arriba a la izquierda; con la lucerna encendida, el filo del lado del pico.
        if (!a.lumbre) {
            drawArc(a.claro(0.55f), 190f, 70f, false, Offset(pie.x - s * 0.066f, arriba - s * 0.024f), Size(s * 0.132f, s * 0.048f), style = Stroke(1f * u))
            drawLine(a.claro(0.4f), Offset(pie.x - s * 0.13f, arriba - s * 0.008f), Offset(pie.x - s * 0.05f, arriba - s * 0.01f), 0.8f * u)
            drawOval(sombraMueble(0.6f), Offset(pie.x - s * 0.165f, arriba - s * 0.008f), Size(s * 0.03f, s * 0.022f))
        } else {
            drawArc(a.filo(0.85f), 150f, 90f, false, Offset(pie.x - s * 0.066f, arriba - s * 0.024f), Size(s * 0.132f, s * 0.048f), style = Stroke(1.1f * u))
            drawLine(a.filo(0.9f), Offset(pie.x - s * 0.135f, arriba - s * 0.011f), Offset(pie.x - s * 0.05f, arriba - s * 0.013f), 0.9f * u)
        }
        // La línea de arcilla reservada del costado.
        drawLine(a.luz(ARCILLA, kk).copy(alpha = 0.7f), Offset(pie.x - s * 0.066f, pie.y - s * 0.022f), Offset(pie.x + s * 0.066f, pie.y - s * 0.022f), 0.6f * u)
    }

    /** La llama de la lucerna: la lengua cálida y su corazón claro. No pasa por la noche: da luz. */
    private fun DrawScope.llamita(base: Offset, alto: Float) {
        val a = alto * 0.36f
        val lengua = Path().apply {
            moveTo(base.x, base.y)
            cubicTo(base.x - a * 0.7f, base.y - alto * 0.15f, base.x - a * 0.5f, base.y - alto * 0.6f, base.x + a * 0.05f, base.y - alto)
            cubicTo(base.x + a * 0.25f, base.y - alto * 0.6f, base.x + a * 0.7f, base.y - alto * 0.2f, base.x, base.y)
            close()
        }
        drawPath(lengua, Color(0xFFF2A24A))
        val corazon = Path().apply {
            moveTo(base.x, base.y - alto * 0.04f)
            cubicTo(base.x - a * 0.35f, base.y - alto * 0.2f, base.x - a * 0.2f, base.y - alto * 0.5f, base.x + a * 0.04f, base.y - alto * 0.68f)
            cubicTo(base.x + a * 0.15f, base.y - alto * 0.45f, base.x + a * 0.35f, base.y - alto * 0.2f, base.x, base.y - alto * 0.04f)
            close()
        }
        drawPath(corazon, Color(0xFFFFF0C4))
        drawOval(Color(0xFF6F7FB8).copy(alpha = 0.7f), Offset(base.x - a * 0.22f, base.y - alto * 0.08f), Size(a * 0.44f, alto * 0.12f))
    }

    /**
     * El cuenco de barniz negro con lo que se come al escribir: de día, higos y una hoja de higuera; en el sueño, tres cabezas
     * de adormidera (la planta de Hipnos).
     */
    private fun DrawScope.cuencoFrutas(c: Offset, s: Float, a: Aire, k: Float, l: Float, sueno: Boolean, caida: Offset) {
        val u = density
        val rx = s * 0.1f; val ry = s * 0.036f
        val barniz = a.luz(BARNIZ, k, l)
        drawOval(a.sombra(if (a.lumbre) 0.4f else 0.28f), Offset(c.x - rx + caida.x, c.y - ry * 0.2f + caida.y * 0.5f), Size(rx * 2.1f, ry * 2.4f))
        // El cuerpo bajo, el pie y la boca.
        val cuerpo = Path().apply {
            moveTo(c.x - rx, c.y)
            cubicTo(c.x - rx, c.y + ry * 1.6f, c.x + rx, c.y + ry * 1.6f, c.x + rx, c.y)
            close()
        }
        drawPath(cuerpo, barniz)
        drawOval(barniz, Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(a.luz(mezcla(BARNIZ, ARCILLA, 0.1f), k, l), Offset(c.x - rx * 0.86f, c.y - ry * 0.8f), Size(rx * 1.72f, ry * 1.6f))
        drawArc(a.luz(ARCILLA, k, l).copy(alpha = 0.7f), 20f, 140f, false, Offset(c.x - rx * 0.96f, c.y - ry * 0.9f), Size(rx * 1.92f, ry * 1.8f), style = Stroke(0.6f * u))
        if (!sueno) {
            // La hoja de higuera, de tres lóbulos, saliendo por el borde de atrás.
            val hoja = a.luz(HOJA, k, l)
            for ((ang, lado) in listOf(-30f to 0f, -60f to -1f, 0f to 1f)) {
                val p = Offset(c.x - rx * 0.2f + lado * rx * 0.25f, c.y - ry * 0.9f)
                rotate(ang, p) { drawOval(hoja, Offset(p.x - rx * 0.16f, p.y - ry * 1.6f), Size(rx * 0.32f, ry * 1.8f)) }
            }
            // Los higos: gotas moradas con su rabito; uno abierto, con la pulpa roja.
            val higos = listOf(Offset(c.x - rx * 0.35f, c.y - ry * 0.1f), Offset(c.x + rx * 0.25f, c.y - ry * 0.2f), Offset(c.x - rx * 0.02f, c.y + ry * 0.35f))
            higos.forEachIndexed { i, p ->
                val r = s * 0.026f
                drawCircle(a.sombra(0.35f), r, p + Offset(r * 0.25f, r * 0.3f))
                drawCircle(a.luz(HIGO, k, l), r, p)
                drawLine(a.luz(HIGO, k, l), p, p + Offset(r * 0.5f, -r * 1.2f), r * 0.45f, StrokeCap.Round)
                if (i == 2) {
                    drawCircle(a.luz(Color(0xFFC0485A), k, l), r * 0.62f, p)
                    drawCircle(a.luz(Color(0xFFE9C9A0), k, l), r * 0.3f, p)
                } else drawCircle(if (a.lumbre) a.filo(0.5f * k) else a.claro(0.4f), r * 0.32f, p - Offset(r * 0.35f, r * 0.35f))
                if (i < 2) drawCircle(a.luz(HIGO_LUZ, k, l).copy(alpha = 0.5f), r * 0.55f, p - Offset(r * 0.2f, r * 0.15f))
            }
        } else {
            // Las adormideras: la cápsula verde gris, con su cuello y su corona de rayos encima, y el tallo largo y seco.
            val cabezas = listOf(Offset(c.x - rx * 0.42f, c.y - ry * 0.5f), Offset(c.x + rx * 0.22f, c.y - ry * 0.75f), Offset(c.x + rx * 0.0f, c.y + ry * 0.25f))
            cabezas.forEachIndexed { i, p ->
                val r = s * (0.024f + 0.004f * i)
                val verde = a.luz(ADORMIDERA, k, l)
                val oscura = a.luz(mezcla(ADORMIDERA, BARNIZ, 0.45f), k, l)
                drawLine(a.luz(mezcla(ADORMIDERA, CANA, 0.5f), k, l), p + Offset(0f, r * 0.8f), p + Offset(rx * (0.7f - 0.45f * i), ry * 1.6f), 1.3f * u, StrokeCap.Round)
                drawOval(a.sombra(0.35f), Offset(p.x - r + r * 0.25f, p.y - r * 0.8f + r * 0.3f), Size(r * 2f, r * 1.75f))
                drawOval(verde, Offset(p.x - r, p.y - r * 0.8f), Size(r * 2f, r * 1.75f))
                // Las costillas de la cápsula.
                for (j in -1..1) drawLine(oscura.copy(alpha = 0.5f), Offset(p.x + j * r * 0.45f, p.y - r * 0.7f), Offset(p.x + j * r * 0.55f, p.y + r * 0.85f), 0.5f * u)
                // El cuello y la corona, como un sombrerito plano de rayos.
                drawRect(oscura, Offset(p.x - r * 0.22f, p.y - r * 1.05f), Size(r * 0.44f, r * 0.3f))
                drawOval(oscura, Offset(p.x - r * 0.62f, p.y - r * 1.28f), Size(r * 1.24f, r * 0.38f))
                for (j in 0 until 6) {
                    val x = p.x - r * 0.5f + r * 0.2f * j
                    drawLine(verde, Offset(p.x, p.y - r * 1.1f), Offset(x, p.y - r * 1.2f), 0.5f * u)
                }
                drawOval(if (a.lumbre) LUNA.copy(alpha = 0.45f * l + 0.1f) else a.claro(0.4f), Offset(p.x - r * 0.75f, p.y - r * 0.6f), Size(r * 0.55f, r * 0.8f))
            }
        }
    }

    /**
     * El cálamo de caña, dejado en el margen: la caña con sus nudos, la punta cortada en bisel con su hendidura y, si ya se
     * escribió, manchada de tinta.
     */
    private fun DrawScope.calamo(desde: Offset, hasta: Offset, s: Float, a: Aire, k: Float, l: Float, entintado: Boolean, caida: Offset) {
        val u = density
        val g = s * 0.018f
        val v = hasta - desde; val largo = v.getDistance().coerceAtLeast(1f); val dir = v / largo
        val normal = Offset(-dir.y, dir.x)
        val punta = hasta - dir * (s * 0.06f)
        drawLine(a.sombra(if (a.lumbre) 0.45f else 0.28f), desde + caida, hasta + caida, g, StrokeCap.Round)
        val cana = a.luz(CANA, k, l)
        drawLine(cana, desde, punta, g, StrokeCap.Round)
        // La punta en bisel, que se afina hasta la hendidura.
        val bisel = Path().apply {
            moveTo(punta.x + normal.x * g / 2, punta.y + normal.y * g / 2)
            lineTo(hasta.x, hasta.y)
            lineTo(punta.x - normal.x * g / 2, punta.y - normal.y * g / 2)
            close()
        }
        drawPath(bisel, a.luz(mezcla(CANA, Color.White, 0.2f), k, l))
        if (entintado) {
            val t0 = hasta - dir * (s * 0.035f)
            drawPath(Path().apply {
                moveTo(t0.x + normal.x * g * 0.35f, t0.y + normal.y * g * 0.35f); lineTo(hasta.x, hasta.y); lineTo(t0.x - normal.x * g * 0.35f, t0.y - normal.y * g * 0.35f); close()
            }, TINTA.copy(alpha = 0.85f))
        }
        drawLine(a.sombra(0.5f), hasta - dir * (s * 0.03f), hasta, 0.5f * u)
        // Los nudos de la caña y la luz a lo largo.
        for (f in listOf(0.3f, 0.62f)) {
            val p = desde + v * f
            drawLine(a.luz(mezcla(CANA, BARNIZ, 0.3f), k, l), p + normal * (g * 0.55f), p - normal * (g * 0.55f), 1.4f * u)
        }
        drawLine(if (a.lumbre) a.filo(0.5f * k) else a.claro(0.45f), desde - normal * (g * 0.25f), punta - normal * (g * 0.25f), 0.6f * u, StrokeCap.Round)
    }
}
