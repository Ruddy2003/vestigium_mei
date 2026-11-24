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
 * misma casa grande del estante y del Ideario. Sobre el tablero de roble ahumado descansa el haz de tablillas enceradas: las
 * tablillas de haya apiladas (más cuantas más hojas), cada una con su canto, atadas por delante con dos tientos de cuero
 * crudo que pasan por los agujeros del marco y acaban en su nudo; la de arriba muestra la cera de miel con el nombre y la cuenta
 * rayados, lo de antes a medio alisar y el estilo de bronce tendido encima. A la izquierda, el cuenco de madera con el pan de
 * cera y la espátula para volver a alisar; a la derecha, la lámpara de piedra con su mecha de junco; en el poste, la madeja de
 * tientos de repuesto colgada de su clavija. Detrás, el barro blanqueado con el zócalo de almagra, el poste, la tornapunta que
 * cruza hasta la viga, el hollín de la lámpara y una grieta.
 *
 * La luz: de día entra el sol por la puerta (a la izquierda, fuera del cuadro) y deja su mancha en el barro, en la mesa y en la
 * tablilla de arriba, corrida en cada plano (cuanto más alto el plano, más hacia la puerta). De noche arde la lámpara de sebo:
 * su charco enciende la pared, el tablero, el canto derecho del haz y la cera; lo lejano (la viga, el poste, el cuenco) se
 * hunde en el pardo rojizo de la casa, y por la puerta entra un parche chico de luna, frío, en la esquina de la mesa. Lo fijo
 * (pared, poste, mesa, cuenco, lámpara y madeja) se pinta una vez por tamaño y modo; el haz, que cambia con las hojas y al
 * abrirse, en cada dibujo ([HazLibretaGalia]).
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
    val zocalo = 90f * U
    val mesaFondo = 118f * U
    val mesaFrente = 232f * U
    val canto = 243f * U
    val mesaIzq = 14f * U
    val poste = Rect(0f, 0f, 28f * U, 240f * U)

    /** El haz: el largo y el fondo de cada tablilla (el fondo, sin escorzar) y el grueso escorzado de cada una. */
    val largo = (enU * 0.58f).coerceIn(180f, 224f) * U
    val fondo = 124f * U
    val grueso = 6.3f * U
    val ex = Offset(0.99756f, 0.05232f)
    val ez = Offset(0.06976f, -0.74817f)
    /** La esquina de abajo, delante a la izquierda, de la tablilla de abajo. */
    val origen = Offset(maxOf(w * 0.5f - largo * 0.5f + 4f * U, 72f * U), 218f * U)
    /** Lo que levanta el haz entero. */
    val alto = n * grueso

    /** La lámpara de piedra (el centro de su asiento) y su llama; el cuenco; la clavija de la madeja. */
    val lampara = Offset(w - 40f * U, 152f * U)
    val llama = Offset(lampara.x - 26f * U, 131f * U)
    val cuenco = Offset(maxOf(origen.x - 37f * U, 40f * U), 198f * U)
    val clavija = Offset(poste.right + 1f * U, 46f * U)

    /** Cuánta luz de la lámpara le llega a [p] de noche (1 junto a la llama); de día, toda. */
    fun cerca(p: Offset, oscuro: Boolean): Float {
        if (!oscuro) return 1f
        val dx = (p.x - llama.x) / U; val dy = (p.y - llama.y) / U * 1.15f
        return (1.3f * exp(-sqrt(dx * dx + dy * dy) / 125f)).coerceIn(0.03f, 1f)
    }

    /** La mancha del sol en la pared (en su plano). */
    val solPared: Path = poligonoGalia(31f * U, 24f * U, w * 0.38f, 36f * U, w * 0.52f, mesaFondo, 31f * U, mesaFondo)

    /** La mancha del sol en un plano horizontal que levanta [hs] sobre el tablero: corrida hacia la puerta y hacia arriba. */
    fun solMesa(hs: Float): Path {
        val dx = -0.8f * hs; val dy = -hs
        return poligonoGalia(mesaIzq - 20f * U + dx, mesaFondo + dy, w * 0.52f + 7f * U + dx, mesaFondo + dy, w * 0.63f + dx, mesaFrente + dy, mesaIzq - 20f * U + dx, mesaFrente + dy)
    }

    /** El parche de la luna, de noche: chico, en la esquina de la mesa junto a la puerta, con el borde quebrado. */
    val luna: Path = Path().apply {
        val q = floatArrayOf(mesaIzq - 4f * U, 205f * U, 42f * U, 201f * U, 60f * U, mesaFrente, mesaIzq - 4f * U, mesaFrente)
        moveTo(q[0], q[1])
        for (lado in 0 until 4) {
            val x0 = q[2 * lado]; val y0 = q[2 * lado + 1]; val x1 = q[(2 * lado + 2) % 8]; val y1 = q[(2 * lado + 3) % 8]
            for (i in 1 until 6) {
                val t = i / 6f
                lineTo(x0 + (x1 - x0) * t + (azar(9010 + lado, i) - 0.5f) * 3f * U, y0 + (y1 - y0) * t + (azar(9020 + lado, i) - 0.5f) * 3f * U)
            }
            lineTo(x1, y1)
        }
        close()
    }

    companion object {
        /** Cuántas tablillas hay en el haz: una sola si no hay nada; con la primera hoja, tres (el haz se ve haz), y luego crecen despacio (al año, diez). */
        fun tablillas(hojas: Int): Int = if (hojas <= 0) 1 else (2f + 2.4f * kotlin.math.ln(1f + hojas / 2f)).toInt().coerceIn(3, 10)
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
        poste(s, a)
        suelo(s, ac)
        mesa(s, ac)
        cuenco(s, ac)
        lampara(s, ac)
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
        viga(s, a)
        tornapunta(s, a)
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
        if (!a.oscuro) clipRect(0f, s.canto, w, h) { clipPath(s.solMesa(0f).let { p -> Path().apply { addPath(p, Offset(-10f * U, 36f * U)) } }) { drawRect(a.sol(P.TIERRA, a.dia(P.TIERRA, -0.2f), 0.6f), Offset(0f, s.canto), Size(w, h - s.canto)) } }
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
        if (!a.oscuro) clipRect(top.left, top.top, top.right, top.bottom) { clipPath(s.solMesa(0f)) { tablero(0.8f) } }
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

    /** El cuenco de madera torneada con el pan de cera de abeja y la espátula de bronce para alisar las tablillas (visto de arriba, como el haz). */
    private fun DrawScope.cuenco(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val c = s.cuenco
        val k = s.k(c, a)
        val lit = if (a.oscuro) 0f else 0.85f
        fun col(x: Color, f: Float = 0f) = if (lit > 0f) a.sol(x, a.dia(x, f), lit) else a.dia(x, f, k)
        contactoGalia(c + Offset(5f * U, 14f * U), 52f * U, 14f * U, a, 0.4f, if (a.oscuro) -1f else 1f)
        val boca = Rect(c.x - 23f * U, c.y - 14f * U, c.x + 23f * U, c.y + 14f * U)
        val panza = Path().apply {
            moveTo(boca.left, c.y); cubicTo(boca.left + 1f * U, c.y + 22f * U, boca.right - 1f * U, c.y + 22f * U, boca.right, c.y); close()
        }
        drawPath(panza, Brush.horizontalGradient(listOf(col(P.ROBLE, 0.15f), col(P.ROBLE_OSC), col(P.ROBLE_HONDO)), boca.left, boca.right))
        if (a.oscuro) drawArc(a.lunar(0.45f), 100f, 80f, false, boca.topLeft, Size(boca.width, boca.height + 8f * U), style = Stroke(1.2f * U))
        ovaloGalia(col(P.ROBLE_OSC, -0.1f), boca)
        val dentro = boca.deflate(3f * U)
        ovaloGalia(col(P.ROBLE_HONDO), dentro)
        // La sombra del borde dentro del cuenco, del lado de la luz.
        drawArc(a.sombra(0.45f), 180f, 140f, false, dentro.topLeft + Offset(2f * U, 2.5f * U), Size(dentro.width - 3f * U, dentro.height - 2f * U), style = Stroke(3f * U))
        // El pan de cera: redondo, con las pasadas de la espátula.
        val pan = Rect(c.x - 15f * U, c.y - 9f * U, c.x + 11f * U, c.y + 8f * U)
        ovaloGalia(col(P.CERA_OSC), pan.translate(0.8f * U, 1.6f * U))
        ovaloGalia(col(P.CERA, 0.08f), pan)
        drawArc(col(P.CERA, 0.45f).copy(alpha = 0.85f), 195f, 80f, false, pan.topLeft + Offset(3f * U, 2f * U), Size(pan.width * 0.62f, pan.height * 0.7f), style = Stroke(1.3f * U, cap = StrokeCap.Round))
        drawLine(col(P.CERA_OSC).copy(alpha = 0.8f), Offset(pan.left + 5f * U, pan.center.y + 2f * U), Offset(pan.right - 4f * U, pan.center.y - 0.5f * U), 0.9f * U)
        drawLine(col(P.CERA_OSC).copy(alpha = 0.6f), Offset(pan.left + 8f * U, pan.center.y + 4.5f * U), Offset(pan.right - 6f * U, pan.center.y + 2.5f * U), 0.8f * U)
        // La espátula de bronce, apoyada en el borde, con la hoja en la cera.
        val e0 = Offset(c.x + 4f * U, c.y - 2f * U); val e1 = Offset(c.x + 31f * U, c.y - 19f * U)
        drawLine(a.sombra(0.35f), e0 + Offset(2f * U, 3f * U), e1 + Offset(4f * U, 7f * U), 1.6f * U, StrokeCap.Round)
        drawLine(col(P.BRONCE_OSC), e0, e1, 2f * U, StrokeCap.Round)
        drawLine(col(P.BRONCE_LUZ).copy(alpha = 0.8f), e0 + Offset(-0.4f * U, -0.6f * U), e1 + Offset(-0.4f * U, -0.6f * U), 0.7f * U, StrokeCap.Round)
        drawPath(poligonoGalia(e0.x - 8f * U, e0.y + 3f * U, e0.x + 1f * U, e0.y - 2.4f * U, e0.x + 2f * U, e0.y + 1.2f * U, e0.x - 7f * U, e0.y + 5f * U), col(P.BRONCE))
        drawCircle(col(P.BRONCE_OSC), 1.8f * U, e1)
        destelloGalia(e1 - Offset(0.6f * U, 0.6f * U), 0.7f * U, a, k)
        // El labio del cuenco: la luz en el canto de enfrente y el brillo del lado de la puerta.
        drawArc(col(P.ROBLE_LUZ).copy(alpha = 0.75f), 200f, 120f, false, dentro.topLeft, dentro.size, style = Stroke(1.2f * U))
        drawArc(a.brillo(0.45f, k), 120f, 80f, false, boca.topLeft, boca.size, style = Stroke(1.1f * U))
    }

    /**
     * La lámpara: una piedra baja y ancha, picada a cincel, con su pocillo de sebo y el pico a la izquierda, donde descansa la
     * mecha de junco. De noche arde en el pico: la llama con su corazón, el halo y el charco en el tablero; de día, la mecha
     * negra, apagada.
     */
    private fun DrawScope.lampara(s: SitioLibretaGalia, a: AireGalia) {
        val U = s.U; val c = s.lampara
        val k = s.k(c, a)
        if (a.oscuro) drawOval(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.32f), P.LLAMA.copy(alpha = 0f)), s.llama + Offset(0f, 20f * U), 64f * U),
            Offset(s.llama.x - 68f * U, s.llama.y - 8f * U), Size(136f * U, 56f * U))
        contactoGalia(c + Offset(0f, 1f * U), 48f * U, 9f * U, a, 0.45f, if (a.oscuro) -1f else 1f)
        val piedra = mezcla(P.PIEDRA, P.TIERRA_LUZ, 0.35f); val honda = mezcla(P.PIEDRA_OSC, P.TIERRA, 0.3f)
        fun col(x: Color, f: Float, kk: Float) = if (a.oscuro) a.luz(x, kk) else a.dia(x, f)
        val cuerpo = Path().apply {
            moveTo(c.x - 21f * U, c.y - 14f * U); lineTo(c.x - 20f * U, c.y - 3f * U); quadraticTo(c.x, c.y + 5f * U, c.x + 20f * U, c.y - 3f * U); lineTo(c.x + 21f * U, c.y - 14f * U); close()
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(col(piedra, 0.12f, (k * 1.2f).coerceAtMost(1f)), col(piedra, 0f, k), col(honda, 0f, k * 0.7f)), c.x - 21f * U, c.x + 21f * U))
        // El pico, a la izquierda, un poco más bajo que el borde.
        val pico = poligonoGalia(c.x - 18f * U, c.y - 19f * U, c.x - 29f * U, c.y - 17.5f * U, c.x - 28f * U, c.y - 12f * U, c.x - 18f * U, c.y - 9f * U)
        drawPath(pico, col(piedra, 0.18f, 1f))
        drawPath(pico, a.sombra(0.3f), style = Stroke(0.6f * U))
        // Las picaduras del cincel.
        val picos = Path()
        for (j in 0 until 10) { val p = Offset(c.x + (azar(9110, j) - 0.5f) * 34f * U, c.y - (2f + 9f * azar(9111, j)) * U); picos.addOval(Rect(p.x - 0.8f * U, p.y - 0.5f * U, p.x + 0.8f * U, p.y + 0.5f * U)) }
        drawPath(picos, a.sombra(0.35f))
        // La cara de arriba, el pocillo y el sebo.
        val cara = Rect(c.x - 21f * U, c.y - 26f * U, c.x + 21f * U, c.y - 3f * U)
        ovaloGalia(col(piedra, 0.25f, (k * 1.1f).coerceAtMost(1f)), cara)
        val pocillo = Rect(cara.left + 5f * U, cara.top + 3.5f * U, cara.right - 4.5f * U, cara.bottom - 4f * U)
        ovaloGalia(a.sombra(0.6f), pocillo)
        ovaloGalia(col(P.GRASA, -0.12f, k), Rect(pocillo.left + 1.2f * U, pocillo.top + 2.4f * U, pocillo.right - 0.6f * U, pocillo.bottom))
        // La mecha de junco, del sebo al pico.
        val punta = Offset(c.x - 25.5f * U, c.y - 14.6f * U)
        drawPath(poligonoGalia(c.x - 28f * U, c.y - 16f * U, c.x - 22f * U, c.y - 16.8f * U, c.x - 21.5f * U, c.y - 12f * U, c.x - 27f * U, c.y - 12.5f * U), col(P.HOLLIN, 0f, k).copy(alpha = 0.75f))
        drawLine(col(P.JUNCO_OSC, 0f, k), Offset(c.x - 12f * U, c.y - 14f * U), punta, 1.6f * U, StrokeCap.Round)
        drawCircle(col(P.HOLLIN, 0f, k), 1.2f * U, punta)
        if (!a.oscuro) {
            drawArc(a.brillo(0.5f, 1f), 160f, 80f, false, cara.topLeft, cara.size, style = Stroke(1.2f * U))
            drawRect(a.claro(0.25f), Offset(c.x - 20f * U, c.y - 13f * U), Size(2.4f * U, 10f * U))
            return
        }
        // La llama, en el pico: el halo, el cuerpo y el corazón blanco.
        val f = s.llama
        drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.45f), P.LLAMA.copy(alpha = 0f)), f, 26f * U), 26f * U, f)
        val llama = Path().apply {
            moveTo(f.x + 1f * U, f.y - 13f * U); cubicTo(f.x + 5.5f * U, f.y - 4f * U, f.x + 5f * U, f.y + 5f * U, f.x, f.y + 6f * U)
            cubicTo(f.x - 5f * U, f.y + 5f * U, f.x - 5f * U, f.y - 4f * U, f.x + 1f * U, f.y - 13f * U); close()
        }
        drawPath(llama, P.LLAMA)
        drawPath(Path().apply {
            moveTo(f.x, f.y - 6f * U); cubicTo(f.x + 2.6f * U, f.y - 1f * U, f.x + 2.4f * U, f.y + 4f * U, f.x, f.y + 4.4f * U)
            cubicTo(f.x - 2.4f * U, f.y + 4f * U, f.x - 2.6f * U, f.y - 1f * U, f.x, f.y - 6f * U); close()
        }, P.FUEGO)
        drawCircle(P.BRASA.copy(alpha = 0.8f), 1.2f * U, Offset(f.x, f.y + 5.4f * U))
        // El filo cálido en el borde de la piedra que mira a la llama.
        drawArc(a.filo(0.75f), 150f, 110f, false, cara.topLeft, cara.size, style = Stroke(1.3f * U))
    }
}
