package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * La libreta de momentos de Galia (la usa `EstanteGalia`, en `BibliotecaGalia.kt`): la mesa de las cuentas, en un rincón de la
 * misma casa grande del estante y del Ideario. Sobre el tablero de roble ahumado descansa el haz de tablillas enceradas
 * ([HazLibretaGalia]): tablillas gruesas de haya apiladas, desiguales y algo giradas (más cuantas más hojas), atadas por un
 * tiento de cuero crudo que da dos vueltas por encima; en la de arriba, la cera rehundida en su marco con el nombre y la cuenta
 * rayados y el estilo metido bajo el tiento. Alrededor, lo que se usa ([MesaLibretaGalia]): dos tablillas sueltas apoyadas
 * contra el zócalo, el cuenco con el pan de cera y la espátula, unas avellanas, la lámpara de piedra, el vaso de madera y el
 * ovillo de lana teñida. Detrás, el barro blanqueado con la cenefa del friso bajo la viga y el zócalo de almagra; el poste con
 * sus cuñas y la madeja de tientos; la tornapunta que cruza hasta la viga; el manojo de hierbas a secar y, a la derecha, la
 * manta a cuadros colgada de su vara.
 *
 * La luz: de día entra el sol por la puerta (a la izquierda, fuera del cuadro) y pinta cada plano por separado, con su propio
 * sesgo y el borde quebrado: la cal, el tablero (más bajo y más a la izquierda) y, en lo alto del haz, el marco, mientras la
 * cera solo brilla; las cosas echan su sombra dentro de la mancha. De noche arde la lámpara de sebo: su charco enciende la
 * pared, la manta, el tablero, el canto derecho del haz y la cera; lo lejano (la viga, el poste, el cuenco) se hunde en el
 * pardo rojizo de la casa, y por la puerta entra un parche chico de luna, frío, en la esquina de la mesa. Lo fijo se pinta una
 * vez por tamaño y modo; el haz, que cambia con las hojas y al abrirse, en cada dibujo.
 */
internal object MuebleLibretaGalia {
    val ALTO: Dp = 268.dp

    private var clave = ""
    private var imagen: ImageBitmap? = null
    private var sitio: SitioLibretaGalia? = null

    private fun sitio(w: Float, h: Float, n: Int): SitioLibretaGalia =
        sitio?.takeIf { it.w == w && it.h == h && it.n == n } ?: SitioLibretaGalia(w, h, n).also { sitio = it }

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val s = sitio(w, h, SitioLibretaGalia.tablillas(e.hojas))
        val a = AireGalia(oscuro, false)
        val ac = AireGalia(oscuro, false, cuerpo = true)
        clipRect(0f, 0f, w, h) {
            val k = "${w.toInt()}:${h.toInt()}:$oscuro:$density"
            var img = imagen
            if (img == null || clave != k) {
                val nueva = ImageBitmap(ceil(w).toInt().coerceAtLeast(1), ceil(h).toInt().coerceAtLeast(1))
                CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(nueva.width.toFloat(), nueva.height.toFloat())) {
                    with(RinconLibretaGalia) { rincon(s, a, ac) }
                }
                imagen = nueva; clave = k; img = nueva
            }
            drawImage(img)
            with(HazLibretaGalia) { haz(e, s, ac, medidor) }
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.6f)
        }
    }
}

/**
 * Dónde va cada cosa del rincón, en U (el alto entre 268: un dp a la altura de diseño). El haz se mira desde arriba y de
 * frente, un poco girado: [ex] es el largo de la tablilla en la pantalla, [ez] su fondo (hacia la pared) y cada tablilla
 * levanta [grueso].
 */
internal class SitioLibretaGalia(val w: Float, val h: Float, val n: Int) {
    val U = h / 268f
    val enU = w / U
    val viga = 16f * U
    /** La cenefa pintada bajo la viga (la misma del friso del estante). */
    val friso0 = 19f * U
    val friso1 = 31f * U
    val zocalo = 92f * U
    val mesaFondo = 118f * U
    val mesaFrente = 236f * U
    val canto = 248f * U
    val mesaIzq = 12f * U
    val poste = Rect(0f, 0f, 28f * U, 250f * U)

    /** El haz: el largo y el fondo de cada tablilla (sin escorzar), el grueso escorzado de cada una y el centro de su pie. */
    val largo = (enU * 0.5f).coerceIn(160f, 196f) * U
    val fondo = 136f * U
    val grueso = 11.5f * U
    val centro = Offset(w * 0.5f + 6f * U, 180f * U)
    /** Lo que levanta el haz entero. */
    val alto = n * grueso
    /** El borde izquierdo y el derecho del haz en la pantalla (para poner lo de los lados). */
    val izqHaz = centro.x - largo * 0.52f
    val derHaz = centro.x + largo * 0.56f

    /** La lámpara de piedra (el centro de su asiento) y su llama, en el pico; lo de la mesa; la clavija de la madeja. */
    val lampara = Offset(maxOf(w - 40f * U, derHaz + 30f * U), 156f * U)
    val llama = Offset(lampara.x - 26f * U, 133f * U)
    val cuenco = Offset(maxOf(izqHaz - 34f * U, 38f * U), 206f * U)
    val vaso = Offset(w - 22f * U, 202f * U)
    val ovillo = Offset(derHaz + 14f * U, 226f * U)
    val clavija = Offset(poste.right + 1f * U, 48f * U)

    /** Cuánta luz de la lámpara le llega a [p] de noche (1 junto a la llama); de día, toda. */
    fun cerca(p: Offset, oscuro: Boolean): Float {
        if (!oscuro) return 1f
        val dx = (p.x - llama.x) / U; val dy = (p.y - llama.y) / U * 1.15f
        return (1.3f * exp(-sqrt(dx * dx + dy * dy) / 125f)).coerceIn(0.03f, 1f)
    }

    /** Un cuadrilátero de luz (x, y de sus cuatro esquinas) con los bordes quebrados: la luz entra por las rendijas del zarzo. */
    private fun quebrado(q: FloatArray, sem: Int, amp: Float): Path = Path().apply {
        moveTo(q[0], q[1])
        for (lado in 0 until 4) {
            val x0 = q[2 * lado]; val y0 = q[2 * lado + 1]; val x1 = q[(2 * lado + 2) % 8]; val y1 = q[(2 * lado + 3) % 8]
            for (i in 1 until 7) {
                val t = i / 7f
                lineTo(x0 + (x1 - x0) * t + (azar(sem + lado, i) - 0.5f) * amp, y0 + (y1 - y0) * t + (azar(sem + 10 + lado, i) - 0.5f) * amp)
            }
            lineTo(x1, y1)
        }
        close()
    }

    /**
     * La mancha del sol en cada plano, por separado: en la pared (la cal y el zócalo), corrida hacia arriba y a la derecha; en el
     * tablero, más baja y más a la izquierda, con otro sesgo (el borde de arriba es la sombra del dintel; el de la derecha, la de
     * la jamba). En lo alto del haz solo prende el marco y deja un brillo en la cera.
     */
    val solPared: Path = quebrado(floatArrayOf(31f * U, 40f * U, w * 0.31f, 50f * U, w * 0.45f, mesaFondo, 31f * U, mesaFondo), 9600, 2.6f * U)
    val solMesa: Path = quebrado(floatArrayOf(mesaIzq - 12f * U, 134f * U, w * 0.32f, mesaFondo + 2f * U, w * 0.42f, mesaFrente, mesaIzq - 12f * U, mesaFrente), 9620, 2.6f * U)
    /** La mancha en un plano que levanta [hs] sobre el tablero (la cara del haz): corrida hacia la puerta y hacia arriba. */
    fun solAlto(hs: Float): Path = Path().apply { addPath(solMesa, Offset(-0.55f * hs, -hs)) }

    /** El parche de la luna, de noche: chico, en la esquina de la mesa junto a la puerta, con el borde quebrado. */
    val luna: Path = quebrado(floatArrayOf(mesaIzq - 4f * U, 210f * U, 40f * U, 206f * U, 58f * U, mesaFrente, mesaIzq - 4f * U, mesaFrente), 9010, 3f * U)

    companion object {
        /** Cuántas tablillas hay en el haz: dos gruesas si no hay nada; tres con la primera hoja, y luego crecen despacio (siete al año). */
        fun tablillas(hojas: Int): Int = if (hojas <= 0) 2 else (3f + 1.5f * kotlin.math.ln(1f + hojas / 4f)).toInt().coerceIn(3, 7)
    }
}

/**
 * Lo fijo del rincón de la libreta de Galia ([MuebleLibretaGalia]): la pared de barro con su zócalo, la viga, el poste con la
 * tornapunta y la madeja, la mesa de roble con su canto y sus patas, el suelo, el cuenco de cera y la lámpara de piedra.
 */
internal object RinconLibretaGalia {
    private val P = PaletaGalia

    fun DrawScope.rincon(s: SitioLibretaGalia, a: AireGalia, ac: AireGalia) {
        pared(s, a)
        manta(s, a)
        poste(s, a)
        suelo(s, ac)
        mesa(s, ac)
        with(MesaLibretaGalia) { objetos(s, ac) }
    }

    private fun SitioLibretaGalia.k(p: Offset, a: AireGalia) = cerca(p, a.oscuro)

    // ---------------------------------------------------------------- la pared

    private fun DrawScope.pared(s: SitioLibretaGalia, a: AireGalia) {
        val w = s.w; val U = s.U
        val cara = mezcla(P.BLANCO, P.BLANCO_SOMBRA, 0.22f)
        val muro = Rect(0f, 0f, w, s.canto + 2f)
        val zoc = Rect(0f, s.zocalo, w, s.canto + 2f)
        // El borde de arriba del zócalo, a mano: ondula un poco.
        val borde = Path().apply {
            moveTo(0f, s.canto + 2f); lineTo(0f, s.zocalo)
            var x = 0f; var j = 0
            while (x < w) { x += 18f * U; lineTo(minOf(x, w), s.zocalo + (azar(9030, j) - 0.5f) * 2.4f * U); j++ }
            lineTo(w, s.canto + 2f); close()
        }
        fun fondo(sol: Float) {
            if (a.oscuro) {
                val radios = floatArrayOf(0f, 30f, 70f, 120f, 190f, 280f, 420f)
                val c = s.llama
                drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 420f to a.luz(cara, s.cerca(Offset(c.x - radios[i] * U, c.y), true)) },
                    center = c, radius = 420f * U), muro.topLeft, muro.size)
                clipPath(borde) {
                    drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 420f to a.luz(P.ALMAGRA, s.cerca(Offset(c.x - radios[i] * U, c.y), true)) },
                        center = c, radius = 420f * U), zoc.topLeft, zoc.size)
                }
                return
            }
            val claro = a.dia(cara, 0f); val hondo = a.dia(mezcla(P.BLANCO_SOMBRA, P.BARRO, 0.25f), 0f)
            val c0 = if (sol > 0f) a.sol(cara, claro, sol) else claro
            val c1 = if (sol > 0f) a.sol(cara, mezcla(claro, hondo, 0.5f), sol) else hondo
            drawRect(Brush.horizontalGradient(listOf(c0, c1), 0f, w), muro.topLeft, muro.size)
            val z0 = a.dia(P.ALMAGRA, 0f); val z1 = a.dia(P.ALMAGRA_OSC, 0f)
            clipPath(borde) {
                drawRect(Brush.horizontalGradient(listOf(if (sol > 0f) a.sol(P.ALMAGRA, z0, sol) else z0, if (sol > 0f) a.sol(P.ALMAGRA, mezcla(z0, z1, 0.5f), sol) else z1), 0f, w), zoc.topLeft, zoc.size)
            }
        }
        fondo(0f)
        // La mancha de la puerta: la misma pared al sol (opaca), con su penumbra.
        if (!a.oscuro) {
            val pen = Path().apply { addPath(s.solPared, Offset(4f * U, 0f)) }
            clipRect(muro.left, muro.top, muro.right, muro.bottom) {
                clipPath(pen) { fondo(0.4f) }
                clipPath(s.solPared) { fondo(0.85f) }
            }
        }
        clipRect(muro.left, muro.top, muro.right, muro.bottom) {
            // Las pasadas de la mano al alisar el barro y las rozaduras del zócalo.
            val arcos = Path()
            for (k in 0 until 18) {
                val c = Offset(w * azar(9040, k), s.viga + (s.zocalo - s.viga) * azar(9041, k))
                val rr = (14f + 22f * azar(9042, k)) * U
                arcos.addArc(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr), 200f + 70f * azar(9043, k), 70f)
            }
            drawPath(arcos, Color(0xFFB09A78).copy(alpha = if (a.oscuro) 0.25f else 0.45f), style = Stroke(1.8f * U), blendMode = BlendMode.Multiply)
            val roces = Path()
            for (k in 0 until 7) {
                val x = w * azar(9044, k); val y = s.zocalo + (4f + 18f * azar(9045, k)) * U
                roces.moveTo(x, y); roces.lineTo(x + (10f + 16f * azar(9046, k)) * U, y + (azar(9047, k) - 0.5f) * 3f * U)
            }
            drawPath(roces, a.dia(P.ALMAGRA_LUZ, 0f, s.k(Offset(w * 0.5f, s.zocalo), a)).copy(alpha = 0.45f), style = Stroke(1.2f * U, cap = StrokeCap.Round))
            // Una grieta del barro seco.
            val g = Path(); var q = Offset(w - 66f * U, 64f * U); g.moveTo(q.x, q.y)
            for (i in 0 until 7) { q += Offset((azar(9050, i) - 0.6f) * 6f * U, 4.5f * U); g.lineTo(q.x, q.y) }
            drawPath(g, Color(0xFF6E5E4C).copy(alpha = if (a.oscuro) 0.5f else 0.8f), style = Stroke(0.9f * U, cap = StrokeCap.Round, join = StrokeJoin.Round), blendMode = BlendMode.Multiply)
        }
        // El hollín de la lámpara, que sube por el barro.
        val l = s.llama
        drawPath(parcheGalia(Rect(l.x - 11f * U, l.y - 44f * U, l.x + 9f * U, l.y - 12f * U), 9055),
            Brush.verticalGradient(listOf(Color(0xFF6A5A4C).copy(alpha = 0f), Color(0xFF6A5A4C).copy(alpha = if (a.oscuro) 0.25f else 0.4f)), l.y - 44f * U, l.y - 12f * U), blendMode = BlendMode.Multiply)
        friso(s, a)
        viga(s, a)
        tornapunta(s, a)
        hierbas(s, a)
    }

    /** La viga de arriba, labrada a azuela, con la sombra que echa en el barro. */
    private fun DrawScope.viga(s: SitioLibretaGalia, a: AireGalia) {
        val w = s.w; val U = s.U
        if (!a.oscuro) drawRect(a.sombra(0.22f), Offset(0f, s.viga), Size(w, 5f * U))
        val k0 = s.k(Offset(0f, s.viga), a); val k1 = s.k(Offset(s.llama.x, s.viga), a)
        drawRect(Brush.horizontalGradient(listOf(a.dia(P.ROBLE, 0f, k0), a.dia(P.ROBLE_OSC, 0f, k1)), 0f, w), Offset.Zero, Size(w, s.viga))
        drawRect(a.dia(P.ROBLE_LUZ, 0f, k1).copy(alpha = 0.55f), Offset(0f, s.viga - 2.6f * U), Size(w, 1.2f * U))
        drawRect(a.sombra(0.5f), Offset(0f, s.viga - 1.2f * U), Size(w, 1.2f * U))
        val facetas = Path()
        var x = 6f * U; var j = 0
        while (x < w) { facetas.moveTo(x, 2f * U + azar(9070, j) * 3f * U); facetas.lineTo(x + (12f + 10f * azar(9071, j)) * U, 2.5f * U + azar(9072, j) * 6f * U); x += (20f + 14f * azar(9073, j)) * U; j++ }
        drawPath(facetas, a.sombra(0.4f), style = Stroke(1f * U, cap = StrokeCap.Round))
        if (a.oscuro) drawRect(a.filo(0.5f * k1), Offset(w * 0.6f, s.viga - 1.6f * U), Size(w * 0.4f, 1.2f * U))
    }

    /** La tornapunta: el puntal que sube en diagonal del poste a la viga y la cruza en el barro, con su clavija de madera. */
    private fun DrawScope.tornapunta(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U
        val x0 = s.poste.right - 2f * U; val y0 = 86f * U; val x1 = 104f * U; val y1 = s.viga - 1f
        val g = 11f * U
        val cuerpo = poligonoGalia(x0, y0, x1, y1, x1 + g * 1.3f, y1, x0, y0 + g * 1.4f)
        if (!a.oscuro) drawPath(Path().apply { addPath(cuerpo, Offset(5f * U, 4f * U)) }, a.sombra(0.22f))
        val k = s.k(Offset((x0 + x1) / 2, (y0 + y1) / 2), a)
        drawPath(cuerpo, a.dia(P.ROBLE, 0.04f, k))
        clipPath(cuerpo) {
            if (!a.oscuro) clipPath(s.solPared) { drawPath(cuerpo, a.sol(P.ROBLE, a.dia(P.ROBLE, 0.04f), 0.85f)) }
            drawLine(a.dia(P.ROBLE_LUZ, 0f, k).copy(alpha = 0.8f), Offset(x0, y0 + 1f * U), Offset(x1, y1 + 1f * U), 1.4f * U)
            drawLine(a.sombra(0.45f), Offset(x0, y0 + g * 1.4f - 1f * U), Offset(x1 + g * 1.3f, y1 - 1f * U), 1.6f * U)
            drawLine(a.sombra(0.25f), Offset(x0 + 8f * U, y0 + 4f * U), Offset(x1 - 2f * U, y1 + 5f * U), 0.8f * U)
        }
        drawCircle(a.dia(P.ROBLE_OSC, 0f, k), 2f * U, Offset(x1 + 6f * U, y1 + 4f * U))
        drawCircle(a.brillo(0.35f, k), 0.8f * U, Offset(x1 + 5.4f * U, y1 + 3.4f * U))
    }

    // ---------------------------------------------------------------- el poste y la madeja

    /** El poste de roble labrado, con su chaflán que mira a la puerta; la clavija y la madeja de tientos de repuesto. */
    private fun DrawScope.poste(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val r = s.poste
        val k = s.k(r.center, a)
        if (!a.oscuro) drawRect(a.sombra(0.25f), Offset(r.right, r.top), Size(7f * U, r.height))
        drawRect(Brush.horizontalGradient(listOf(a.dia(P.ROBLE, 0.1f, k), a.dia(P.ROBLE, 0f, k), a.dia(P.ROBLE_OSC, 0f, k)), r.left, r.right), r.topLeft, r.size)
        // El chaflán de la izquierda: al sol de día; de noche, el filo de la luna.
        val chaflan = Rect(r.left, r.top, r.left + 5f * U, r.bottom)
        drawRect(if (a.oscuro) a.dia(P.ROBLE, 0f, k) else a.sol(P.ROBLE_LUZ, a.dia(P.ROBLE_LUZ, 0f), 0.6f), chaflan.topLeft, chaflan.size)
        if (a.oscuro) drawRect(a.lunar(0.25f), Offset(chaflan.left, 150f * U), Size(1.2f * U, chaflan.bottom - 150f * U))
        drawRect(a.sombra(0.3f), Offset(chaflan.right, r.top), Size(1f * U, r.height))
        val veta = Path()
        for (j in 0 until 5) {
            val x = r.left + (8f + 3.6f * j + azar(9080, j) * 2f) * U
            veta.moveTo(x, r.top); veta.cubicTo(x + 1.5f * U, r.height * 0.3f, x - 1.5f * U, r.height * 0.6f, x + 0.5f * U, r.bottom)
        }
        drawPath(veta, a.sombra(0.28f), style = Stroke(0.7f * U))
        drawRect(a.sombra(0.4f), Offset(r.right - 1.4f * U, r.top), Size(1.4f * U, r.height))
        cunas(s, a)
        madeja(s, a)
    }

    /** Los tientos de repuesto: unas vueltas de cuero crudo colgadas de una clavija, con dos cabos sueltos. */
    private fun DrawScope.madeja(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val c = s.clavija
        val k = s.k(c, a)
        val sol = if (a.oscuro) 0f else 0.75f
        val cuero = mezcla(P.CUERO, P.CUERNO, 0.38f)
        fun col(f: Float) = if (sol > 0f) a.sol(cuero, a.dia(cuero, f), sol) else a.dia(cuero, f, k)
        // La clavija.
        drawRect(a.dia(P.ROBLE_OSC, 0f, k), Rect(c.x - 2f * U, c.y - 2.2f * U, c.x + 6f * U, c.y + 2.2f * U).topLeft, Size(8f * U, 4.4f * U))
        val vueltas = Path(); val sombras = Path()
        for (i in 0 until 4) {
            val dx = (i - 1.5f) * 1.6f * U; val largo = (30f + 3f * i) * U; val ancho = (9f + 1.2f * i) * U
            vueltas.moveTo(c.x + 2f * U, c.y)
            vueltas.cubicTo(c.x + 2f * U + dx - ancho, c.y + largo * 0.4f, c.x + dx - ancho * 0.5f, c.y + largo, c.x + 2f * U + dx, c.y + largo)
            vueltas.cubicTo(c.x + 2f * U + dx + ancho * 0.5f, c.y + largo, c.x + 2f * U + dx + ancho, c.y + largo * 0.4f, c.x + 2f * U, c.y)
            sombras.addPath(vueltas)
        }
        vueltas.moveTo(c.x + 3f * U, c.y + 2f * U); vueltas.quadraticTo(c.x + 7f * U, c.y + 24f * U, c.x + 4f * U, c.y + 44f * U)
        if (!a.oscuro) drawPath(Path().apply { addPath(vueltas, Offset(3f * U, 2.5f * U)) }, a.sombra(0.3f), style = Stroke(1.6f * U, cap = StrokeCap.Round))
        drawPath(vueltas, col(-0.2f), style = Stroke(1.7f * U, cap = StrokeCap.Round))
        drawPath(vueltas, col(0.15f).copy(alpha = 0.7f), style = Stroke(0.6f * U, cap = StrokeCap.Round))
        // El nudo que las junta debajo de la clavija.
        drawOval(col(-0.1f), Offset(c.x - 1f * U, c.y + 2f * U), Size(6f * U, 4.4f * U))
        drawOval(a.sombra(0.35f), Offset(c.x - 1f * U, c.y + 2f * U), Size(6f * U, 4.4f * U), style = Stroke(0.6f * U))
    }

    // ---------------------------------------------------------------- lo pintado y lo colgado

    /**
     * La cenefa bajo la viga, la misma del friso del estante: dos filetes de hollín y dos de glasto, la hilera de rombos de
     * almagra apartados con un punto de hollín entre cada dos, alguno gastado (con mezcla de multiplicar: sigue la luz de debajo).
     */
    private fun DrawScope.friso(s: SitioLibretaGalia, a: AireGalia) {
        val w = s.w; val U = s.U
        val y0 = s.friso0; val y1 = s.friso1
        val hollin = Color(0xFF5A4E44); val glasto = Color(0xFF8AA0BC); val almagra = Color(0xFFC0705A)
        drawRect(hollin, Offset(0f, y0), Size(w, 1.6f * U), blendMode = BlendMode.Multiply)
        drawRect(hollin, Offset(0f, y1 - 1.6f * U), Size(w, 1.6f * U), blendMode = BlendMode.Multiply)
        drawRect(glasto, Offset(0f, y0 + 2.4f * U), Size(w, 1f * U), blendMode = BlendMode.Multiply)
        drawRect(glasto, Offset(0f, y1 - 3.4f * U), Size(w, 1f * U), blendMode = BlendMode.Multiply)
        val rombos = Path(); val puntos = Path()
        val paso = 19f * U; val ym = (y0 + y1) / 2; val ra = (y1 - y0) * 0.26f
        var x = 4f * U; var j = 0
        while (x < w) {
            val gasto = azar(9640, j)
            if (gasto > 0.1f) {
                val e = if (gasto < 0.3f) 0.6f else 1f
                val cx = x + paso * 0.5f
                rombos.moveTo(cx - paso * 0.3f * e, ym); rombos.lineTo(cx, ym - ra * e); rombos.lineTo(cx + paso * 0.3f * e, ym); rombos.lineTo(cx, ym + ra * e); rombos.close()
            }
            puntos.addOval(Rect(x - 1.1f * U, ym - 1.1f * U, x + 1.1f * U, ym + 1.1f * U))
            x += paso; j++
        }
        drawPath(rombos, almagra, blendMode = BlendMode.Multiply)
        drawPath(puntos, hollin, blendMode = BlendMode.Multiply)
        for (q in 0 until 5) {
            val xq = w * azar(9641, q)
            drawLine(Color(0xFFE8DCC0).copy(alpha = if (a.oscuro) 0.08f else 0.5f), Offset(xq, y0 + 1.2f * U), Offset(xq + 7f * U, y1 - 1.6f * U), 1.1f * U)
        }
    }

    /**
     * El manojo de hierbas puesto a secar, colgado cabeza abajo de un clavo del friso: el cordel, los tallos que se abren y las
     * hojitas y las flores de milenrama. De día echa su sombra en la cal, a la derecha.
     */
    private fun DrawScope.hierbas(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U
        val clavo = Offset(maxOf(s.w * 0.33f, 120f * U), s.friso1 + 2f * U)
        val k = s.k(clavo, a)
        val tallos = Path(); val hojas = Path(); val flores = Path()
        val atado = clavo + Offset(0f, 9f * U)
        for (j in 0 until 7) {
            val ang = (-0.55f + 1.1f * j / 6f) + (azar(9650, j) - 0.5f) * 0.15f
            val largo = (30f + 10f * azar(9651, j)) * U
            val fin = atado + Offset(kotlin.math.sin(ang) * largo, kotlin.math.cos(ang) * largo)
            tallos.moveTo(atado.x, atado.y); tallos.quadraticTo(atado.x + (fin.x - atado.x) * 0.3f, atado.y + largo * 0.5f, fin.x, fin.y)
            for (q in 1 until 5) {
                val t = q / 5f
                val p = Offset(atado.x + (fin.x - atado.x) * t * t, atado.y + (fin.y - atado.y) * t)
                val lado = if (q % 2 == 0) 1f else -1f
                hojas.addOval(Rect(p.x + lado * 0.5f * U - 2.2f * U, p.y - 1f * U, p.x + lado * 0.5f * U + 2.2f * U, p.y + 1.4f * U))
            }
            if (j % 2 == 0) for (q in 0 until 3) { val c = fin + Offset((q - 1) * 1.8f * U, 1f * U + (q % 2) * 1.2f * U); flores.addOval(Rect(c.x - 1.3f * U, c.y - 1.3f * U, c.x + 1.3f * U, c.y + 1.3f * U)) }
        }
        if (!a.oscuro) translate(7f * U, 4f * U) { drawPath(hojas, a.sombra(0.28f)); drawPath(tallos, a.sombra(0.28f), style = Stroke(1f * U)) }
        drawLine(a.dia(P.LANA_CRUDA, -0.2f, k), clavo, atado, 1f * U)
        drawCircle(a.dia(P.HIERRO, 0f, k), 1.4f * U, clavo)
        drawPath(tallos, a.dia(P.JUNCO_OSC, 0f, k), style = Stroke(1f * U, cap = StrokeCap.Round))
        drawPath(hojas, a.dia(mezcla(P.LANA_VERDE, P.JUNCO, 0.45f), 0f, k))
        drawPath(hojas, a.brillo(0.25f, k), style = Stroke(0.5f * U))
        drawPath(flores, a.dia(Color(0xFFE4DAB8), 0f, k))
        drawOval(a.dia(P.LANA_CRUDA, -0.1f, k), Offset(atado.x - 3f * U, atado.y - 2f * U), Size(6f * U, 4f * U))
    }

    /**
     * La manta de lana a cuadros, colgada a la derecha de una vara sobre dos clavijas: la vuelta de arriba, los cuadros (el rojo
     * de rubia con las listas de verde y de gualda), los pliegues y los flecos. Enmarca el rincón por ese lado; de noche la alumbra
     * la lámpara.
     */
    private fun DrawScope.manta(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U
        val x0 = s.w - 84f * U; val x1 = s.w + 2f * U
        val yv = 40f * U; val yb = 106f * U
        val k = s.k(Offset((x0 + x1) / 2, (yv + yb) / 2), a)
        // La vara y sus clavijas.
        listOf(x0 + 8f * U, x1 - 12f * U).forEach { x -> drawRect(a.dia(P.ROBLE_OSC, 0f, k), Offset(x - 2f * U, yv - 1f * U), Size(4f * U, 6f * U)) }
        // La sombra de la manta en la pared (de noche, hacia la izquierda, lejos de la lámpara).
        val cuerpo = Path().apply {
            moveTo(x0, yv + 3f * U); lineTo(x1, yv + 3f * U); lineTo(x1, yb + 2f * U)
            var x = x1; var j = 0
            while (x > x0) { x -= 7f * U; lineTo(maxOf(x, x0), yb + (azar(9660, j) - 0.3f) * 3f * U); j++ }
            close()
        }
        drawPath(Path().apply { addPath(cuerpo, if (a.oscuro) Offset(-6f * U, 3f * U) else Offset(5f * U, 3f * U)) }, a.sombra(0.3f))
        drawPath(cuerpo, a.dia(P.LANA_ROJA, 0f, k))
        clipPath(cuerpo) {
            val listas = Path(); val claras = Path()
            var x = x0 + 6f * U
            while (x < x1) { listas.addRect(Rect(x, yv, x + 5f * U, yb + 6f * U)); claras.addRect(Rect(x + 9f * U, yv, x + 10.4f * U, yb + 6f * U)); x += 22f * U }
            var y = yv + 10f * U
            while (y < yb) { listas.addRect(Rect(x0, y, x1, y + 5f * U)); claras.addRect(Rect(x0, y + 9f * U, x1, y + 10.4f * U)); y += 22f * U }
            drawPath(listas, a.dia(P.LANA_VERDE, 0f, k).copy(alpha = 0.75f))
            drawPath(claras, a.dia(P.LANA_OCRE, 0f, k).copy(alpha = 0.85f))
            // Los pliegues: la caída de la lana, más honda en cada valle y con su loma de luz.
            var xp = x0 + 12f * U; var q = 0
            while (xp < x1) {
                drawRect(a.sombra(0.22f), Offset(xp, yv), Size(5f * U, yb - yv + 6f * U))
                drawRect(if (a.oscuro) a.filo(0.25f * k) else a.claro(0.18f), Offset(xp - 5f * U, yv), Size(2f * U, yb - yv + 6f * U))
                xp += (17f + 5f * azar(9661, q)) * U; q++
            }
            if (!a.oscuro) drawRect(a.claro(0.12f), Offset(x0, yv), Size(12f * U, yb - yv + 6f * U))
        }
        // La vuelta de arriba, sobre la vara, y los flecos.
        val vuelta = Rect(x0 - 1f * U, yv - 2f * U, x1, yv + 7f * U)
        drawRect(a.dia(P.LANA_ROJA, -0.15f, k), vuelta.topLeft, vuelta.size)
        drawRect(a.dia(P.LANA_VERDE, -0.1f, k).copy(alpha = 0.7f), Offset(vuelta.left, vuelta.top + 3f * U), Size(vuelta.width, 2f * U))
        drawRect(a.sombra(0.35f), Offset(vuelta.left, vuelta.bottom - 1.2f * U), Size(vuelta.width, 1.2f * U))
        val flecos = Path()
        var xf = x0 + 2f * U
        while (xf < x1) { flecos.moveTo(xf, yb); flecos.lineTo(xf + 0.6f * U, yb + (4f + 2f * azar(9662, xf.toInt())) * U); xf += 2.6f * U }
        drawPath(flecos, a.dia(P.LANA_ROJA, -0.1f, k), style = Stroke(0.9f * U, cap = StrokeCap.Round))
    }

    /** Las cuñas del poste: la que aprieta la viga sobre su espiga y la del pie de la tornapunta, de roble más claro. */
    private fun DrawScope.cunas(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val r = s.poste
        val k = s.k(r.center, a)
        listOf(Offset(r.right, s.viga + 1f * U), Offset(r.right, 92f * U)).forEachIndexed { i, o ->
            val cuna = poligonoGalia(o.x - 1f * U, o.y, o.x + 7f * U, o.y + 1.5f * U, o.x + 7f * U, o.y + 4f * U, o.x - 1f * U, o.y + 7f * U)
            drawPath(Path().apply { addPath(cuna, Offset(1.5f * U, 1.5f * U)) }, a.sombra(0.4f))
            drawPath(cuna, a.dia(P.ROBLE_LUZ, -0.05f, k))
            drawLine(a.brillo(0.4f, k), Offset(o.x, o.y + 0.5f * U), Offset(o.x + 6.5f * U, o.y + 1.8f * U), 0.7f * U)
            drawLine(a.sombra(0.5f), Offset(o.x + 7f * U, o.y + 1.5f * U), Offset(o.x + 7f * U, o.y + 4f * U), 0.8f * U)
            if (i == 1) { drawCircle(a.dia(P.ROBLE_OSC, 0f, k), 1.8f * U, Offset(r.right - 8f * U, o.y + 3.5f * U)); drawCircle(a.brillo(0.3f, k), 0.7f * U, Offset(r.right - 8.6f * U, o.y + 2.9f * U)) }
        }
    }

    // ---------------------------------------------------------------- la mesa y el suelo

    /** El suelo de tierra pisada bajo la mesa, en su sombra, con alguna paja; las dos patas que se ven. */
    private fun DrawScope.suelo(s: SitioLibretaGalia, a: AireGalia) {
        val w = s.w; val h = s.h; val U = s.U
        val k = s.k(Offset(w * 0.8f, h), a)
        drawRect(Brush.verticalGradient(listOf(a.dia(P.TIERRA, -0.4f, k * 0.4f), a.dia(P.TIERRA, 0f, k * 0.7f)), s.canto, h), Offset(0f, s.canto - 1f), Size(w, h - s.canto + 1f))
        val pajas = Path()
        for (j in 0 until 9) {
            val x = w * azar(9090, j); val y = s.canto + (h - s.canto) * (0.45f + 0.5f * azar(9091, j))
            pajas.moveTo(x, y); pajas.lineTo(x + (8f + 8f * azar(9092, j)) * U, y + (azar(9093, j) - 0.5f) * 3f * U)
        }
        drawPath(pajas, a.dia(P.PAJA, -0.2f, k).copy(alpha = 0.6f), style = Stroke(0.9f * U, cap = StrokeCap.Round))
        listOf(s.mesaIzq + 10f * U, w - 34f * U).forEachIndexed { i, x ->
            val kk = s.k(Offset(x, s.canto), a)
            val pata = poligonoGalia(x, s.canto - 2f, x + 13f * U, s.canto - 2f, x + 12f * U + (if (i == 0) -2f else 2f) * U, h, x + (if (i == 0) -3f else 1f) * U, h)
            drawPath(pata, a.dia(P.ROBLE_OSC, 0f, kk))
            drawRect(a.dia(P.ROBLE, 0f, kk).copy(alpha = 0.7f), Offset(x + (if (i == 0) 0f else 9f) * U, s.canto), Size(2.4f * U, h - s.canto))
        }
        if (!a.oscuro) clipRect(0f, s.canto, w, h) { clipPath(s.solMesa.let { p -> Path().apply { addPath(p, Offset(-10f * U, 36f * U)) } }) { drawRect(a.sol(P.TIERRA, a.dia(P.TIERRA, -0.2f), 0.6f), Offset(0f, s.canto), Size(w, h - s.canto)) } }
    }

    /** El tablero de roble ahumado (tres tablas con sus juntas, su veta, gotas de cera) y su canto, con la mancha del sol. */
    private fun DrawScope.mesa(s: SitioLibretaGalia, a: AireGalia) {
        val w = s.w; val U = s.U
        val top = Rect(s.mesaIzq, s.mesaFondo, w, s.mesaFrente)
        fun tablero(sol: Float) {
            if (a.oscuro) {
                val radios = floatArrayOf(0f, 30f, 70f, 130f, 210f, 320f)
                val c = Offset(s.lampara.x, s.lampara.y)
                drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 320f to a.luz(P.ROBLE, s.cerca(Offset(c.x - radios[i] * U, c.y), true)) }, center = c, radius = 320f * U), top.topLeft, top.size)
                return
            }
            val c0 = a.dia(P.ROBLE, -0.08f); val c1 = a.dia(P.ROBLE, 0.06f)
            drawRect(Brush.verticalGradient(listOf(if (sol > 0f) a.sol(P.ROBLE, c0, sol) else c0, if (sol > 0f) a.sol(P.ROBLE, c1, sol) else c1), top.top, top.bottom), top.topLeft, top.size)
        }
        tablero(0f)
        if (!a.oscuro) clipRect(top.left, top.top, top.right, top.bottom) { clipPath(s.solMesa) { tablero(0.8f) } }
        else clipRect(top.left, top.top, top.right, top.bottom) {
            // La luna: el roble virado a un azul hondo, un paso más claro que lo que lo rodea.
            drawPath(s.luna, mezcla(a.luz(P.ROBLE, 0.05f), Color(0xFF34486E), 0.45f))
        }
        val k = s.k(top.center, a)
        clipRect(top.left, top.top, top.right, top.bottom) {
            // Las juntas de las tablas y la veta, a lo largo.
            val juntas = Path(); val veta = Path()
            listOf(0.36f, 0.7f).forEach { f -> val y = top.top + top.height * f; juntas.moveTo(top.left, y); juntas.lineTo(top.right, y + 1.2f * U) }
            for (j in 0 until 14) {
                val y = top.top + top.height * azar(9100, j); val x = top.left + top.width * azar(9101, j) * 0.7f
                veta.moveTo(x, y); veta.cubicTo(x + 40f * U, y - 2f * U, x + 80f * U, y + 2f * U, x + (110f + 60f * azar(9102, j)) * U, y + (azar(9103, j) - 0.5f) * 3f * U)
            }
            drawPath(veta, a.sombra(0.22f), style = Stroke(0.8f * U, cap = StrokeCap.Round))
            drawPath(juntas, a.sombra(0.55f), style = Stroke(1.4f * U))
            drawPath(Path().apply { addPath(juntas, Offset(0f, 1.4f * U)) }, a.brillo(0.25f, k), style = Stroke(0.7f * U))
            // Gotas de cera caídas y los cortes del cuchillo.
            val gotas = Path()
            for (j in 0 until 6) {
                val x = top.left + top.width * azar(9104, j); val y = top.top + top.height * (0.55f + 0.42f * azar(9105, j)); val r = (1.2f + 1.6f * azar(9106, j)) * U
                gotas.addOval(Rect(x - r, y - r * 0.6f, x + r, y + r * 0.6f))
            }
            drawPath(gotas, a.dia(P.CERA, 0f, k))
            drawPath(gotas, a.sombra(0.3f), style = Stroke(0.5f * U))
            // Por detrás, la sombra que el tablero recibe del zócalo (su junta con la pared).
            drawRect(Brush.verticalGradient(listOf(a.sombra(0.4f), a.sombra(0f)), top.top, top.top + 10f * U), top.topLeft, Size(top.width, 10f * U))
        }
        // El canto de delante: más hondo, con la arista que levanta la luz.
        val canto = Rect(s.mesaIzq, s.mesaFrente, w, s.canto)
        val kc = s.k(Offset(s.lampara.x, canto.top), a)
        drawRect(Brush.horizontalGradient(listOf(a.dia(P.ROBLE_OSC, 0f, s.k(canto.topLeft, a)), a.dia(P.ROBLE_OSC, 0f, kc)), canto.left, canto.right), canto.topLeft, canto.size)
        drawRect(a.dia(P.ROBLE_LUZ, 0f, kc).copy(alpha = 0.75f), canto.topLeft, Size(canto.width, 1.3f * U))
        if (a.oscuro) drawRect(a.filo(0.5f * kc), Offset(w * 0.55f, canto.top), Size(w * 0.45f, 1f * U))
        drawRect(a.sombra(0.45f), Offset(canto.left, canto.bottom - 2f * U), Size(canto.width, 2f * U))
        // La esquina de la izquierda: el canto de la cabeza del tablero.
        drawRect(a.dia(P.ROBLE_LUZ, 0f, s.k(top.topLeft, a)).copy(alpha = 0.6f), Offset(top.left, top.top), Size(1.3f * U, canto.bottom - top.top))
        if (a.oscuro) drawRect(a.lunar(0.3f), Offset(top.left, 204f * U), Size(1f * U, canto.top - 204f * U))
    }

    // ---------------------------------------------------------------- lo que hay en la mesa
}
