package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Templario: la encomienda de frontera, con los materiales de su cámara del tesoro (el Ideario) y de su biblioteca:
 * sillares de caliza, roble oscuro con flejes y candados de hierro, la sobreveste de lino blanco con la cruz patada roja,
 * el baucent negro y blanco, el zócalo de almagra, el lacre y el fuego de las antorchas.
 * - Social: la sala capitular. El muro de sillería con su cornisa y sus canecillos, el escudo de la cruz entre dos
 *   baucents, dos columnas con capitel y basa, una antorcha en cada una, el banco corrido de piedra y el piso ajedrezado.
 *   Al centro cuelga de su barra de hierro el gran paño de la orden (el nicho): de lana blanca de día y negra de noche (el
 *   manto de los sargentos), con la franja del baucent arriba, la orla roja, la cruz apenas y las puntas con borlas.
 * - Mapas: la mesa de campaña sobre caballetes: tablones con flejes y cantoneras de hierro, el pergamino del mapa (el nicho)
 *   clavado en sus esquinas; alrededor, los rollos de cartas atados con cordón rojo, los besantes de oro, la matriz del
 *   sello, la lucerna encendida, el tintero, la daga, el astrolabio, dos torres de ajedrez y la espada tendida abajo.
 * - Otras vidas: el adarve: almenas contra el cielo, la cornisa, un escudo de la orden sobre cada hornacina, cinco
 *   hornacinas de arco apuntado con sus dovelas (encaladas, el nicho), la repisa, el zócalo de almagra y el piso ajedrezado.
 * - Íconos: el castillo con su estandarte, el mapa que raspa un besante y tres banderolas de la cruz sobre la arena.
 * - Notas: el tablón de la regla: un dintel de piedra con tres escudos y dos lanzas cruzadas, el marco de roble con sus
 *   bisagras de punta de lanza, el fondo encalado (el nicho) y, al pie, la espada sobre el poyo. Cada nota es una carta
 *   doblada en tercios con su sello de cera pendiente (rojo, negro, verde o amarillo según el tipo), clavada con un clavo
 *   de hierro o, si está fijada, atravesada por una daga.
 */
internal object VidaTemplario : DisenoVida {
    private val PIEDRA = Color(0xFFC9BC9E)
    private val PIEDRA_LUZ = Color(0xFFE0D5BC)
    private val PIEDRA_OSC = Color(0xFF8E8168)
    private val JUNTA = Color(0xFF6A604E)
    private val ROBLE = Color(0xFF5A3D25)
    private val ROBLE_LUZ = Color(0xFF7A5634)
    private val ROBLE_OSC = Color(0xFF2C1C10)
    private val HIERRO = Color(0xFF1F1E1D)
    private val HIERRO_LUZ = Color(0xFF6E6A63)
    private val ACERO = Color(0xFFC3C7CC)
    private val ROJO = Color(0xFFB0191F)
    private val BLANCO = Color(0xFFF4EFE4)
    private val NEGRO = Color(0xFF1C1A18)
    private val LINO = Color(0xFFECE4D0)
    private val CERA = Color(0xFF8E1A16)
    private val CERA_NEGRA = Color(0xFF2E2622)
    private val CERA_VERDE = Color(0xFF3E6B3A)
    private val CERA_AMARILLA = Color(0xFFC9962E)
    private val ALMAGRA = Color(0xFFA04A2A)
    private val ORO = Color(0xFFD1A54A)
    private val ORO_LUZ = Color(0xFFF0D58A)
    private val LATON = Color(0xFFC29A48)
    private val CUERO = Color(0xFF6B4226)
    private val ARENA = Color(0xFFDCC69A)
    private val CIELO = Color(0xFFC9DCEA)
    private val CIELO_NOCHE = Color(0xFF1B2433)
    private val TINTA = Color(0xFF201E1B)
    private val FUEGO = Color(0xFFF2B65A)
    private val NOCHE = Color(0xFF0D0C0A)

    // ---------------------------------------------------------------- Ayudas

    /** Un número estable de 0 a 1: cada piedra con su tono, siempre el mismo. */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun mezcla(a: Color, b: Color, f: Float): Color = lerp(a, b, f.coerceIn(0f, 1f))

    /** El material de día o en la vigilia: de noche, la piedra y la madera en penumbra de verdad. */
    private fun t(c: Color, oscuro: Boolean, cuanto: Float = 0.5f): Color = if (oscuro) mezcla(c, NOCHE, cuanto) else c

    /** El color sólido de un nicho: el material, llevado hasta la luminancia de la tarjeta. */
    private fun nicho(e: EstadoMueble, oscuro: Boolean, material: Color): Color =
        if (oscuro) mezcla(e.fondo, material, 0.1f) else mezcla(e.fondo, material, 0.4f)

    private fun DrawScope.caja(r: Rect) = Rect(size.width * r.left, size.height * r.top, size.width * r.right, size.height * r.bottom)

    private fun Rect.crece(d: Float) = Rect(left - d, top - d, right + d, bottom + d)

    /** La cruz patada: cuatro brazos que se abren hacia la punta. */
    private fun cruzPatada(c: Offset, r: Float): Path = Path().apply {
        val a = r * 0.13f; val b = r * 0.42f
        moveTo(c.x - a, c.y - a)
        lineTo(c.x - b, c.y - r); lineTo(c.x + b, c.y - r); lineTo(c.x + a, c.y - a)
        lineTo(c.x + r, c.y - b); lineTo(c.x + r, c.y + b); lineTo(c.x + a, c.y + a)
        lineTo(c.x + b, c.y + r); lineTo(c.x - b, c.y + r); lineTo(c.x - a, c.y + a)
        lineTo(c.x - r, c.y + b); lineTo(c.x - r, c.y - b); close()
    }

    /** Un escudo de punta: arriba recto, abajo en punta; [a] de ancho y [h] de alto. */
    private fun escudo(c: Offset, a: Float, h: Float) = Path().apply {
        moveTo(c.x - a / 2, c.y - h / 2); lineTo(c.x + a / 2, c.y - h / 2); lineTo(c.x + a / 2, c.y)
        quadraticTo(c.x + a / 2, c.y + h * 0.35f, c.x, c.y + h / 2); quadraticTo(c.x - a / 2, c.y + h * 0.35f, c.x - a / 2, c.y); close()
    }

    /**
     * Un escudo de la orden con su sombra y su borde: [tipo] 0 es el blanco de la cruz roja, 1 el baucent (negro arriba,
     * blanco abajo) y 2 el rojo de la cruz blanca.
     */
    private fun DrawScope.blason(c: Offset, a: Float, h: Float, tipo: Int, oscuro: Boolean, u: Float) {
        val e = escudo(c, a, h)
        translate(0.6f * u, 0.8f * u) { drawPath(e, Color.Black.copy(alpha = 0.35f)) }
        when (tipo) {
            0 -> {
                drawPath(e, t(BLANCO, oscuro, 0.3f))
                drawRect(t(ROJO, oscuro, 0.25f), Offset(c.x - a * 0.08f, c.y - h / 2), Size(a * 0.16f, h * 0.92f))
                drawRect(t(ROJO, oscuro, 0.25f), Offset(c.x - a / 2, c.y - h * 0.2f), Size(a, h * 0.15f))
            }
            1 -> clipPath(e) {
                drawRect(t(BLANCO, oscuro, 0.3f), Offset(c.x - a / 2, c.y - h / 2), Size(a, h))
                drawRect(t(NEGRO, oscuro, 0.2f), Offset(c.x - a / 2, c.y - h / 2), Size(a, h * 0.42f))
            }
            else -> {
                drawPath(e, t(ROJO, oscuro, 0.25f))
                drawRect(t(BLANCO, oscuro, 0.3f), Offset(c.x - a * 0.07f, c.y - h * 0.38f), Size(a * 0.14f, h * 0.7f))
                drawRect(t(BLANCO, oscuro, 0.3f), Offset(c.x - a * 0.36f, c.y - h * 0.12f), Size(a * 0.72f, h * 0.13f))
            }
        }
        clipPath(e) { drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent, Color.Black.copy(alpha = 0.18f)), c.x - a / 2, c.x + a / 2), Offset(c.x - a / 2, c.y - h / 2), Size(a, h)) }
        drawPath(e, t(HIERRO, oscuro, 0.2f), style = Stroke(0.45f * u))
    }

    /** Sillares en [r]: hiladas de [alto] con juntas alternadas, cada piedra con su tono. */
    private fun DrawScope.sillares(r: Rect, alto: Float, claro: Color, osc: Color, semilla: Int, oscuro: Boolean, u: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(t(JUNTA, oscuro), r.topLeft, r.size)
            var y = r.top; var hilada = 0
            val j = 0.35f * u
            while (y < r.bottom) {
                var x = r.left - (if (hilada % 2 == 0) 0f else alto * 1.1f)
                var k = 0
                while (x < r.right) {
                    val largo = alto * (1.8f + 0.9f * azar(semilla + hilada, k))
                    val c = mezcla(osc, claro, 0.45f + 0.4f * azar(semilla + 100 + hilada, k))
                    drawRect(t(c, oscuro), Offset(x + j, y + j), Size(largo - 2 * j, alto - 2 * j))
                    drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.14f), Offset(x + j, y + j), Size(largo - 2 * j, 0.3f * u))
                    drawRect(Color.Black.copy(alpha = 0.12f), Offset(x + j, y + alto - j - 0.3f * u), Size(largo - 2 * j, 0.3f * u))
                    x += largo; k++
                }
                y += alto; hilada++
            }
        }
    }

    /** El piso ajedrezado de losas blancas y negras, como el baucent. */
    private fun DrawScope.ajedrez(r: Rect, lado: Float, oscuro: Boolean) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top; var f = 0
            while (y < r.bottom) {
                var x = r.left; var k = 0
                while (x < r.right) {
                    drawRect(t(if ((k + f) % 2 == 0) mezcla(BLANCO, PIEDRA, 0.35f) else mezcla(NEGRO, JUNTA, 0.25f), oscuro, 0.55f), Offset(x, y), Size(lado + 0.5f, lado + 0.5f))
                    x += lado; k++
                }
                y += lado; f++
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent), r.top, r.bottom), r.topLeft, r.size)
        }
    }

    /** Vetas de madera: líneas onduladas a lo largo, separadas ≈ [paso]. */
    private fun DrawScope.vetas(r: Rect, color: Color, semilla: Int, paso: Float, vertical: Boolean = false, grosor: Float = 1f) {
        val largo = if (vertical) r.height else r.width
        val ancho = if (vertical) r.width else r.height
        val n = (ancho / paso).toInt().coerceIn(2, 60)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val base = ancho * (k + azar(semilla, k)) / n
                val amp = paso * (0.15f + 0.45f * azar(semilla, k + 100))
                val fase = azar(semilla, k + 200) * 6.28f
                val ondas = 1.2f + 2.2f * azar(semilla, k + 300)
                val p = Path()
                for (s in 0..16) {
                    val a = largo * s / 16
                    val v = base + amp * sin(fase + ondas * 6.28f * s / 16)
                    if (s == 0) p.moveTo(if (vertical) r.left + v else r.left + a, if (vertical) r.top + a else r.top + v)
                    else p.lineTo(if (vertical) r.left + v else r.left + a, if (vertical) r.top + a else r.top + v)
                }
                drawPath(p, color.copy(alpha = color.alpha * (0.3f + 0.7f * azar(semilla, k + 400))), style = Stroke(grosor * (0.5f + azar(semilla, k + 500))))
            }
        }
    }

    /** Una tabla de roble con veta, luz arriba y sombra abajo. */
    private fun DrawScope.tabla(r: Rect, base: Color, oscuro: Boolean, semilla: Int, u: Float, vertical: Boolean = false) {
        drawRect(t(base, oscuro), r.topLeft, r.size)
        vetas(r, t(ROBLE_OSC, oscuro).copy(alpha = 0.5f), semilla, 1.3f * u, vertical, 0.35f * u)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), r.topLeft, Size(r.width, 0.35f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 0.35f * u), Size(r.width, 0.35f * u))
    }

    private fun DrawScope.halo(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Un remache de hierro con su brillo. */
    private fun DrawScope.remache(c: Offset, r: Float, oscuro: Boolean, metal: Color = HIERRO, luz: Color = HIERRO_LUZ) {
        drawCircle(Color.Black.copy(alpha = 0.35f), r, c + Offset(r * 0.35f, r * 0.45f))
        drawCircle(t(metal, oscuro, 0.25f), r, c)
        drawCircle(t(luz, oscuro, 0.25f), r * 0.42f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** Un clavo de cabeza cuadrada, forjado. */
    private fun DrawScope.clavoCuadrado(c: Offset, r: Float, oscuro: Boolean) {
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(c.x - r + r * 0.4f, c.y - r + r * 0.5f), Size(2 * r, 2 * r))
        drawRect(t(HIERRO, oscuro, 0.2f), Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        drawPath(Path().apply { moveTo(c.x - r, c.y - r); lineTo(c.x + r, c.y - r); lineTo(c.x, c.y); close() }, t(HIERRO_LUZ, oscuro, 0.2f))
        drawPath(Path().apply { moveTo(c.x - r, c.y - r); lineTo(c.x - r, c.y + r); lineTo(c.x, c.y); close() }, t(mezcla(HIERRO_LUZ, HIERRO, 0.5f), oscuro, 0.2f))
    }

    /** Una llama (de antorcha o de lámpara) con su halo. */
    private fun DrawScope.llama(base: Offset, alto: Float, oscuro: Boolean, halo: Float = 3.2f) {
        halo(base - Offset(0f, alto * 0.5f), alto * halo, FUEGO.copy(alpha = if (oscuro) 0.45f else 0.3f))
        val p = Path().apply {
            moveTo(base.x, base.y - alto)
            quadraticTo(base.x + alto * 0.45f, base.y - alto * 0.3f, base.x, base.y)
            quadraticTo(base.x - alto * 0.45f, base.y - alto * 0.3f, base.x, base.y - alto)
            close()
        }
        drawPath(p, Color(0xFFF0922E))
        drawOval(Color(0xFFFFF0C0), Offset(base.x - alto * 0.13f, base.y - alto * 0.55f), Size(alto * 0.26f, alto * 0.45f))
    }

    /** Un arco apuntado (ojival): [r] con la punta arriba al centro; [flecha] es el alto de la parte curva. */
    private fun apuntado(r: Rect, flecha: Float): Path = Path().apply {
        val cx = r.center.x
        moveTo(r.left, r.bottom); lineTo(r.left, r.top + flecha)
        cubicTo(r.left, r.top + flecha * 0.45f, cx - r.width * 0.22f, r.top + flecha * 0.1f, cx, r.top)
        cubicTo(cx + r.width * 0.22f, r.top + flecha * 0.1f, r.right, r.top + flecha * 0.45f, r.right, r.top + flecha)
        lineTo(r.right, r.bottom); close()
    }

    /** Una espada tendida de [a] (el pomo) a [b] (la punta), con su cruz, su puño de cuero y la acanaladura. */
    private fun DrawScope.espada(a: Offset, b: Offset, ancho: Float, oscuro: Boolean, u: Float) {
        val d = b - a; val largo = d.getDistance(); val dir = d / largo; val nor = Offset(-dir.y, dir.x)
        val cruz = a + dir * (largo * 0.2f)
        fun q(p: Offset, n: Float) = p + nor * n
        val hoja = Path().apply {
            val p1 = q(cruz, -ancho * 0.5f); val p2 = q(b - dir * (ancho * 1.6f), -ancho * 0.42f); val p3 = q(b - dir * (ancho * 1.6f), ancho * 0.42f); val p4 = q(cruz, ancho * 0.5f)
            moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(b.x, b.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close()
        }
        translate(0.5f * u, 0.8f * u) { drawPath(hoja, Color.Black.copy(alpha = 0.35f)) }
        drawPath(hoja, t(ACERO, oscuro, 0.3f))
        drawLine(Color.White.copy(alpha = 0.55f), q(cruz, -ancho * 0.22f), q(b - dir * (ancho * 2f), -ancho * 0.18f), ancho * 0.18f)
        drawLine(t(mezcla(ACERO, Color.Black, 0.35f), oscuro, 0.3f), cruz + dir * (ancho * 0.5f), b - dir * (largo * 0.25f), ancho * 0.16f)
        drawPath(hoja, t(Color(0xFF6E7277), oscuro, 0.3f), style = Stroke(0.2f * u))
        // El puño de cuero, la cruz y el pomo.
        drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(0.5f * u, 0.8f * u), cruz + Offset(0.5f * u, 0.8f * u), ancho * 0.75f)
        drawLine(t(CUERO, oscuro, 0.3f), a, cruz, ancho * 0.7f)
        var k = 0.15f
        while (k < 0.95f) { val p = a + (cruz - a) * k; drawLine(t(mezcla(CUERO, Color.Black, 0.4f), oscuro, 0.3f), q(p, -ancho * 0.35f), q(p + dir * (ancho * 0.3f), ancho * 0.35f), 0.2f * u); k += 0.16f }
        drawLine(Color.Black.copy(alpha = 0.3f), q(cruz, -ancho * 2.2f) + Offset(0.5f * u, 0.8f * u), q(cruz, ancho * 2.2f) + Offset(0.5f * u, 0.8f * u), ancho * 0.6f, StrokeCap.Round)
        drawLine(t(HIERRO_LUZ, oscuro, 0.3f), q(cruz, -ancho * 2.2f), q(cruz, ancho * 2.2f), ancho * 0.55f, StrokeCap.Round)
        drawLine(t(ACERO, oscuro, 0.3f), q(cruz, -ancho * 2.1f), q(cruz, ancho * 2.1f), ancho * 0.18f, StrokeCap.Round)
        drawCircle(t(HIERRO_LUZ, oscuro, 0.3f), ancho * 0.75f, a)
        drawCircle(t(ACERO, oscuro, 0.3f), ancho * 0.3f, a - nor * (ancho * 0.2f))
    }

    // ---------------------------------------------------------------- Nichos

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.12f, 0.18f, 0.88f, 0.80f)
        MuebleVida.MAPAS -> Rect(0.12f, 0.14f, 0.88f, 0.76f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.035f, 0.25f, 0.965f, 0.80f)
        MuebleVida.NOTAS -> Rect(0.07f, 0.15f, 0.93f, 0.89f)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.SOCIAL))
        // El muro de sillería de la sala capitular.
        sillares(Rect(0f, 0f, w, h * 0.86f), 5.4f * u, PIEDRA_LUZ, PIEDRA_OSC, 3, oscuro, u)
        // La cornisa con su línea de almagra y los canecillos debajo.
        val cor = Rect(0f, 0f, w, h * 0.07f)
        drawRect(t(PIEDRA_LUZ, oscuro), cor.topLeft, cor.size)
        drawRect(t(ALMAGRA, oscuro, 0.4f), Offset(0f, cor.bottom - 1.6f * u), Size(w, 0.9f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, cor.bottom), Size(w, 0.7f * u))
        var x = 3f * u
        while (x < w - 2f * u) {
            val r = Rect(x, cor.bottom, x + 2.4f * u, cor.bottom + 3.2f * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.5f * u, r.top), Size(r.width, r.height + 0.6f * u))
            drawRoundRect(t(PIEDRA, oscuro), r.topLeft, r.size, CornerRadius(0f, 0f))
            drawOval(t(PIEDRA, oscuro), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 2.4f * u))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.right - 0.7f * u, r.top), Size(0.7f * u, r.height + 0.6f * u))
            x += 7f * u
        }
        // Las dos columnas, con basa, fuste redondo, capitel de crochets y ábaco, y una antorcha en cada una.
        listOf(w * 0.018f to w * 0.072f, w * 0.928f to w * 0.982f).forEachIndexed { i, (a, z) ->
            val cx = (a + z) / 2; val fw = z - a
            val abaco = Rect(a - fw * 0.2f, h * 0.105f, z + fw * 0.2f, h * 0.13f)
            val capitel = Rect(a - fw * 0.1f, abaco.bottom, z + fw * 0.1f, h * 0.2f)
            val fuste = Rect(a + fw * 0.12f, capitel.bottom, z - fw * 0.12f, h * 0.8f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(fuste.right, fuste.top), Size(1.2f * u, fuste.height))
            drawRect(Brush.horizontalGradient(listOf(t(PIEDRA, oscuro), t(PIEDRA_LUZ, oscuro), t(PIEDRA, oscuro), t(PIEDRA_OSC, oscuro)), fuste.left, fuste.right), fuste.topLeft, fuste.size)
            drawRect(t(PIEDRA_LUZ, oscuro), Offset(fuste.left - fw * 0.06f, fuste.top), Size(fuste.width + fw * 0.12f, 0.9f * u))
            drawPath(Path().apply { moveTo(capitel.left, capitel.top); lineTo(capitel.right, capitel.top); lineTo(fuste.right, capitel.bottom); lineTo(fuste.left, capitel.bottom); close() }, t(PIEDRA_LUZ, oscuro))
            drawPath(Path().apply { moveTo(cx, capitel.top); lineTo(capitel.right, capitel.top); lineTo(fuste.right, capitel.bottom); lineTo(cx, capitel.bottom); close() }, Color.Black.copy(alpha = 0.12f))
            listOf(capitel.left + 0.8f * u, capitel.right - 0.8f * u).forEach { vx -> drawCircle(t(PIEDRA_LUZ, oscuro), 1f * u, Offset(vx, capitel.top + 1.2f * u)); drawCircle(t(JUNTA, oscuro), 1f * u, Offset(vx, capitel.top + 1.2f * u), style = Stroke(0.3f * u)) }
            drawPath(Path().apply { moveTo(cx - fw * 0.22f, capitel.bottom); quadraticTo(cx, capitel.top + 1f * u, cx + fw * 0.22f, capitel.bottom) }, t(JUNTA, oscuro), style = Stroke(0.35f * u))
            drawRect(t(PIEDRA_LUZ, oscuro), abaco.topLeft, abaco.size)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(abaco.left, abaco.bottom - 0.4f * u), Size(abaco.width, 0.4f * u))
            val toro = Rect(a - fw * 0.02f, fuste.bottom, z + fw * 0.02f, fuste.bottom + 1.6f * u)
            drawRoundRect(t(PIEDRA_LUZ, oscuro), toro.topLeft, toro.size, CornerRadius(0.8f * u))
            drawRect(t(PIEDRA, oscuro), Offset(a - fw * 0.15f, toro.bottom), Size(fw * 1.3f, h * 0.86f - toro.bottom))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(a - fw * 0.15f, toro.bottom), Size(fw * 1.3f, 0.4f * u))
            // La antorcha en su argolla, del lado del paño.
            val tx = if (i == 0) (z + n.left - 2f * u) / 2 else (n.right + 2f * u + a) / 2
            val ty = h * 0.4f
            drawLine(t(HIERRO, oscuro, 0.2f), Offset(if (i == 0) z else a, ty + 2.4f * u), Offset(tx, ty + 2.4f * u), 0.6f * u)
            drawPath(Path().apply { moveTo(tx - 1.6f * u, ty); lineTo(tx + 1.6f * u, ty); lineTo(tx + 0.6f * u, ty + 7f * u); lineTo(tx - 0.6f * u, ty + 7f * u); close() }, t(ROBLE, oscuro, 0.3f))
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(tx - 1.8f * u, ty - 0.2f * u), Size(3.6f * u, 1f * u))
            drawRect(t(HIERRO, oscuro, 0.2f), Offset(tx - 1.2f * u, ty + 2f * u), Size(2.4f * u, 0.8f * u))
            llama(Offset(tx, ty), 4.2f * u, oscuro, halo = 3.4f)
        }
        // El banco corrido de piedra y el piso ajedrezado.
        val banco = Rect(0f, h * 0.86f, w, h * 0.93f)
        drawRect(t(PIEDRA_LUZ, oscuro), banco.topLeft, Size(w, 1.6f * u))
        drawRect(t(PIEDRA, oscuro), Offset(0f, banco.top + 1.6f * u), Size(w, banco.height - 1.6f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, banco.top + 1.6f * u), Size(w, 0.5f * u))
        var bx = 4f * u; var k = 0
        while (bx < w) { drawLine(t(JUNTA, oscuro), Offset(bx, banco.top + 1.6f * u), Offset(bx, banco.bottom), 0.35f * u); bx += (11f + 4f * azar(5, k)) * u; k++ }
        ajedrez(Rect(0f, banco.bottom, w, h), (h - banco.bottom) / 2f, oscuro)
        // El paño de la orden colgado de su barra: la sombra, la tela, la franja del baucent, la orla roja y las puntas.
        val blanco = !oscuro
        val tela = nicho(e, oscuro, if (blanco) LINO else NEGRO)
        val pano = Rect(n.left - 2f * u, h * 0.15f, n.right + 2f * u, n.bottom + 2f * u)
        val puntas = 7
        val forma = Path().apply {
            moveTo(pano.left, pano.top); lineTo(pano.right, pano.top); lineTo(pano.right, pano.bottom)
            for (j in puntas downTo 1) {
                val x1 = pano.left + pano.width * (j - 0.5f) / puntas; val x0 = pano.left + pano.width * (j - 1) / puntas
                lineTo(x1, pano.bottom + 3.4f * u); lineTo(x0, pano.bottom)
            }
            close()
        }
        translate(1f * u, 1.4f * u) { drawPath(forma, Color.Black.copy(alpha = 0.35f)) }
        drawPath(forma, tela)
        clipPath(forma) {
            // Los pliegues, muy suaves, y la caída de la luz.
            for (j in 0..10) {
                val fx = pano.left + pano.width * j / 10f
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.06f else 0.035f), Color.Transparent), fx - 3f * u, fx + 3f * u), Offset(fx - 3f * u, pano.top), Size(6f * u, pano.height + 4f * u))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.08f), Color.Transparent), pano.top, pano.top + 6f * u), pano.topLeft, Size(pano.width, 6f * u))
        }
        val franja = Rect(pano.left, pano.top, pano.right, n.top - 0.6f * u)
        drawRect(if (blanco) NEGRO else mezcla(BLANCO, NOCHE, 0.55f), franja.topLeft, franja.size)
        drawRect(t(ROJO, oscuro, 0.25f), Offset(franja.left, franja.bottom - 0.7f * u), Size(franja.width, 0.7f * u))
        drawRect(t(ROJO, oscuro, 0.25f), n.crece(1.1f * u).let { Offset(it.left, n.top - 0.6f * u) }, Size(0.7f * u, n.height + 1.7f * u))
        drawRect(t(ROJO, oscuro, 0.25f), Offset(n.right + 0.4f * u, n.top - 0.6f * u), Size(0.7f * u, n.height + 1.7f * u))
        drawRect(t(ROJO, oscuro, 0.25f), Offset(n.left - 1.1f * u, n.bottom + 0.4f * u), Size(n.width + 2.2f * u, 0.7f * u))
        for (j in 0 until puntas) {
            val px = pano.left + pano.width * (j + 0.5f) / puntas
            drawLine(t(ROJO, oscuro, 0.25f), Offset(px, pano.bottom + 3.2f * u), Offset(px, pano.bottom + 5f * u), 0.5f * u)
            drawOval(t(ROJO, oscuro, 0.25f), Offset(px - 0.8f * u, pano.bottom + 4.6f * u), Size(1.6f * u, 2.4f * u))
        }
        // La barra de hierro con sus anillas y sus remates de punta de lanza.
        val by = pano.top - 0.6f * u
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(pano.left - 2.5f * u, by + 0.8f * u), Offset(pano.right + 2.5f * u, by + 0.8f * u), 0.9f * u)
        drawLine(t(HIERRO, oscuro, 0.2f), Offset(pano.left - 2.5f * u, by), Offset(pano.right + 2.5f * u, by), 0.9f * u)
        listOf(-1f, 1f).forEach { s ->
            val ex = if (s < 0) pano.left - 2.5f * u else pano.right + 2.5f * u
            drawPath(Path().apply { moveTo(ex, by - 1.1f * u); lineTo(ex + s * 2.6f * u, by); lineTo(ex, by + 1.1f * u); close() }, t(HIERRO, oscuro, 0.2f))
        }
        val anillas = 9
        for (j in 0..anillas) {
            val ax = pano.left + 1f * u + (pano.width - 2f * u) * j / anillas
            drawCircle(t(HIERRO_LUZ, oscuro, 0.2f), 0.9f * u, Offset(ax, by + 0.3f * u), style = Stroke(0.35f * u))
        }
        // El nicho, liso, y la cruz encima apenas; los escudos en la cornisa.
        drawRect(tela, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (j in 0..10) {
                val fx = pano.left + pano.width * j / 10f
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = if (oscuro) 0.05f else 0.03f), Color.Transparent), fx - 3f * u, fx + 3f * u), Offset(fx - 3f * u, n.top), Size(6f * u, n.height))
            }
        }
        drawPath(cruzPatada(n.center, n.height * 0.4f), t(ROJO, oscuro, 0.2f).copy(alpha = if (oscuro) 0.1f else 0.045f))
        blason(Offset(w / 2, h * 0.075f), 7.4f * u, 9f * u, 0, oscuro, u)
        blason(Offset(w * 0.32f, h * 0.075f), 5.6f * u, 7f * u, 1, oscuro, u)
        blason(Offset(w * 0.68f, h * 0.075f), 5.6f * u, 7f * u, 1, oscuro, u)
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.MAPAS))
        // El piso de losas de la tienda de campaña, en penumbra.
        sillares(Rect(0f, 0f, w, h), 7f * u, PIEDRA, PIEDRA_OSC, 11, oscuro, u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.3f))
        // Los caballetes en tijera.
        val tope = h * 0.86f; val canto = h * 0.905f
        listOf(w * 0.09f, w * 0.91f).forEach { cx ->
            drawOval(Color.Black.copy(alpha = 0.4f), Offset(cx - 7f * u, h - 1.8f * u), Size(14f * u, 2.6f * u))
            listOf(-1f, 1f).forEach { s ->
                drawLine(t(ROBLE, oscuro), Offset(cx - s * 1f * u, canto), Offset(cx + s * 5.5f * u, h), 1.8f * u)
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(cx - s * 1f * u + 0.6f * u, canto), Offset(cx + s * 5.5f * u + 0.6f * u, h), 0.5f * u)
            }
            drawRect(t(ROBLE_LUZ, oscuro), Offset(cx - 4f * u, canto + (h - canto) * 0.55f), Size(8f * u, 1.2f * u))
            remache(Offset(cx, canto + (h - canto) * 0.3f), 0.6f * u, oscuro)
        }
        // El canto grueso de los tablones, con los flejes que lo abrazan.
        val frente = Rect(w * 0.015f, tope, w * 0.985f, canto)
        tabla(frente, ROBLE_OSC, oscuro, 41, u)
        // El tablero: tres tablones a lo largo, con su canto.
        val top = Rect(w * 0.015f, h * 0.02f, w * 0.985f, tope)
        drawRect(t(ROBLE_OSC, oscuro), top.topLeft, top.size)
        val campo = top.crece(-0.7f * u)
        for (k in 0 until 3) {
            val r = Rect(campo.left, campo.top + campo.height * k / 3, campo.right, campo.top + campo.height * (k + 1) / 3)
            drawRect(t(mezcla(ROBLE, ROBLE_LUZ, 0.2f + 0.4f * azar(e.semilla, k)), oscuro), r.topLeft, r.size)
            vetas(r, t(ROBLE_OSC, oscuro).copy(alpha = 0.55f), e.semilla * 3 + k, 1.3f * u, grosor = 0.35f * u)
            drawLine(Color.Black.copy(alpha = 0.55f), Offset(r.left, r.top), Offset(r.right, r.top), 0.4f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), Offset(r.left, r.top + 0.5f * u), Offset(r.right, r.top + 0.5f * u), 0.3f * u)
        }
        // Los flejes de hierro que cruzan los tablones cerca de cada extremo, remachados, y las cantoneras.
        listOf(campo.left + 1.4f * u, campo.right - 3.6f * u).forEach { fx ->
            drawRect(t(HIERRO, oscuro, 0.25f), Offset(fx, top.top), Size(2.2f * u, canto - top.top))
            drawRect(t(HIERRO_LUZ, oscuro, 0.25f).copy(alpha = 0.4f), Offset(fx, top.top), Size(0.5f * u, canto - top.top))
            var y = top.top + 2f * u
            while (y < canto - 1f * u) { remache(Offset(fx + 1.1f * u, y), 0.45f * u, oscuro); y += 5f * u }
        }
        val ca = 5.5f * u
        listOf(campo.topLeft to Offset(1f, 1f), Offset(campo.right, campo.top) to Offset(-1f, 1f)).forEach { (c, d) ->
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + d.x * ca, c.y); lineTo(c.x, c.y + d.y * ca); close() }, t(HIERRO, oscuro, 0.25f))
            remache(c + Offset(d.x * ca * 0.3f, d.y * ca * 0.3f), 0.5f * u, oscuro)
        }
        // El pergamino del mapa, con su filete negro y rojo y un clavo forjado en cada esquina; el campo es el nicho.
        val perg = n.crece(2.6f * u)
        val colorPerg = nicho(e, oscuro, LINO)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(perg.left + 0.7f * u, perg.top + 1f * u), perg.size)
        drawRect(colorPerg, perg.topLeft, perg.size)
        drawRect(t(ARENA, oscuro, 0.3f).copy(alpha = 0.6f), perg.topLeft, perg.size, style = Stroke(0.7f * u))
        drawRect(t(NEGRO, oscuro, 0.1f), n.crece(1.5f * u).topLeft, n.crece(1.5f * u).size, style = Stroke(0.4f * u))
        drawRect(t(ROJO, oscuro, 0.25f), n.crece(0.9f * u).topLeft, n.crece(0.9f * u).size, style = Stroke(0.3f * u))
        drawRect(colorPerg, n.topLeft, n.size)
        listOf(perg.topLeft, Offset(perg.right, perg.top), Offset(perg.left, perg.bottom), perg.bottomRight).forEach { c ->
            clavoCuadrado(c + Offset(if (c.x < w / 2) 1.1f * u else -1.1f * u, if (c.y < h / 2) 1.1f * u else -1.1f * u), 0.9f * u, oscuro)
        }
        // Arriba: los rollos de cartas atados con cordón rojo, la matriz del sello y los besantes de oro.
        val ys = (campo.top + perg.top) / 2; val alto = perg.top - campo.top
        listOf(Triple(w * 0.15f, w * 0.36f, 0f), Triple(w * 0.19f, w * 0.41f, 1f)).forEach { (a, z, k) ->
            val r = Rect(a, ys - alto * 0.3f + k * alto * 0.12f - alto * 0.06f, z, ys + alto * 0.06f + k * alto * 0.12f)
            drawRoundRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 0.5f * u, r.top + 0.7f * u), r.size, CornerRadius(r.height / 2))
            drawRoundRect(Brush.verticalGradient(listOf(t(mezcla(LINO, ARENA, 0.5f), oscuro, 0.35f), t(LINO, oscuro, 0.35f), t(ARENA, oscuro, 0.35f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(r.height / 2))
            drawOval(t(mezcla(ARENA, JUNTA, 0.3f), oscuro, 0.35f), Offset(r.right - r.height * 0.6f, r.top), Size(r.height * 0.6f, r.height))
            drawCircle(t(JUNTA, oscuro, 0.3f), r.height * 0.12f, Offset(r.right - r.height * 0.3f, r.center.y))
            val cx = r.left + r.width * (0.45f + 0.1f * k)
            drawRect(t(ROJO, oscuro, 0.25f), Offset(cx, r.top), Size(0.7f * u, r.height))
            drawLine(t(ROJO, oscuro, 0.25f), Offset(cx + 0.35f * u, r.bottom), Offset(cx + 1.6f * u, r.bottom + 1.2f * u), 0.4f * u, StrokeCap.Round)
        }
        val sello = Offset(w * 0.5f, ys)
        val rs = minOf(alto * 0.32f, 2.4f * u)
        drawCircle(Color.Black.copy(alpha = 0.3f), rs, sello + Offset(0.5f * u, 0.7f * u))
        drawCircle(t(LATON, oscuro, 0.25f), rs, sello)
        drawCircle(t(mezcla(LATON, Color.Black, 0.35f), oscuro, 0.25f), rs * 0.78f, sello, style = Stroke(0.3f * u))
        drawPath(cruzPatada(sello, rs * 0.55f), t(mezcla(LATON, Color.Black, 0.35f), oscuro, 0.25f))
        listOf(Offset(0.62f, -0.15f), Offset(0.655f, 0.12f), Offset(0.69f, -0.1f), Offset(0.66f, -0.02f), Offset(0.72f, 0.14f)).forEachIndexed { i, p ->
            val c = Offset(w * p.x, ys + alto * p.y); val rb = minOf(alto * 0.22f, 1.7f * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), rb, c + Offset(0.3f * u, 0.5f * u))
            drawCircle(t(ORO, oscuro, 0.2f), rb, c)
            drawCircle(t(mezcla(ORO, Color.Black, 0.3f), oscuro, 0.2f), rb * 0.75f, c, style = Stroke(0.2f * u))
            if (i % 2 == 0) drawPath(cruzPatada(c, rb * 0.45f), t(mezcla(ORO, Color.Black, 0.3f), oscuro, 0.2f))
            drawCircle(t(ORO_LUZ, oscuro, 0.2f), rb * 0.25f, c - Offset(rb * 0.35f, rb * 0.35f))
        }
        // A la izquierda: la lucerna encendida, el tintero y la daga.
        val xl = (campo.left + 3.6f * u + perg.left) / 2
        val anchoL = perg.left - campo.left - 3.6f * u
        val lu = Offset(xl, h * 0.32f); val rl = minOf(2.6f * u, anchoL * 0.4f)
        drawOval(Color.Black.copy(alpha = 0.35f), Offset(lu.x - rl + 0.5f * u, lu.y - rl * 0.8f + 0.7f * u), Size(rl * 2, rl * 1.6f))
        drawPath(Path().apply { moveTo(lu.x - rl * 0.5f, lu.y - rl * 0.5f); lineTo(lu.x, lu.y - rl * 1.9f); lineTo(lu.x + rl * 0.5f, lu.y - rl * 0.5f); close() }, t(LATON, oscuro, 0.25f))
        drawOval(t(LATON, oscuro, 0.25f), Offset(lu.x - rl, lu.y - rl * 0.8f), Size(rl * 2, rl * 1.6f))
        drawOval(t(mezcla(LATON, Color.Black, 0.45f), oscuro, 0.25f), Offset(lu.x - rl * 0.4f, lu.y - rl * 0.3f), Size(rl * 0.8f, rl * 0.6f))
        drawArc(t(mezcla(LATON, Color.Black, 0.3f), oscuro, 0.25f), 30f, 120f, false, Offset(lu.x - rl * 0.9f, lu.y + rl * 0.2f), Size(rl * 1.8f, rl * 1.2f), style = Stroke(0.4f * u))
        llama(Offset(lu.x, lu.y - rl * 1.8f), rl * 1.1f, oscuro)
        val tin = Offset(xl, h * 0.54f); val rt = minOf(2.2f * u, anchoL * 0.35f)
        drawCircle(Color.Black.copy(alpha = 0.35f), rt, tin + Offset(0.5f * u, 0.7f * u))
        drawCircle(t(Color(0xFF3B3633), oscuro, 0.2f), rt, tin)
        drawCircle(t(HIERRO_LUZ, oscuro, 0.2f), rt, tin, style = Stroke(0.35f * u))
        drawCircle(Color.Black, rt * 0.5f, tin)
        drawCircle(Color.White.copy(alpha = 0.3f), rt * 0.2f, tin - Offset(rt * 0.4f, rt * 0.4f))
        espada(Offset(xl, h * 0.64f), Offset(xl, h * 0.82f), minOf(1f * u, anchoL * 0.16f), oscuro, u)
        // A la derecha: el astrolabio de latón y dos torres de ajedrez, blanca y negra.
        val xr = (perg.right + campo.right - 3.6f * u) / 2
        val anchoR = campo.right - 3.6f * u - perg.right
        val as0 = Offset(xr, h * 0.36f); val ra = minOf(3.4f * u, anchoR * 0.42f)
        drawCircle(Color.Black.copy(alpha = 0.35f), ra, as0 + Offset(0.6f * u, 0.8f * u))
        drawCircle(t(LATON, oscuro, 0.25f), ra * 0.25f, as0 - Offset(0f, ra * 1.12f), style = Stroke(0.4f * u))
        drawCircle(t(LATON, oscuro, 0.25f), ra, as0)
        drawCircle(t(mezcla(LATON, Color.Black, 0.35f), oscuro, 0.25f), ra * 0.82f, as0, style = Stroke(0.3f * u))
        for (k in 0 until 12) { val a = k * Math.PI / 6; drawLine(t(mezcla(LATON, Color.Black, 0.35f), oscuro, 0.25f), as0 + Offset(cos(a).toFloat(), sin(a).toFloat()) * (ra * 0.82f), as0 + Offset(cos(a).toFloat(), sin(a).toFloat()) * ra, 0.2f * u) }
        drawCircle(t(mezcla(LATON, Color.Black, 0.35f), oscuro, 0.25f), ra * 0.45f, as0 + Offset(0f, -ra * 0.15f), style = Stroke(0.25f * u))
        drawLine(t(mezcla(LATON, Color.Black, 0.45f), oscuro, 0.25f), as0 - Offset(ra * 0.8f, -ra * 0.3f), as0 + Offset(ra * 0.8f, -ra * 0.3f), 0.5f * u)
        drawCircle(t(HIERRO, oscuro, 0.2f), 0.4f * u, as0)
        listOf(Pair(h * 0.6f, true), Pair(h * 0.72f, false)).forEach { (y, blanca) ->
            val c = Offset(xr, y); val rr = minOf(2f * u, anchoR * 0.3f)
            val col = if (blanca) mezcla(BLANCO, ARENA, 0.25f) else NEGRO
            drawCircle(Color.Black.copy(alpha = 0.35f), rr * 1.2f, c + Offset(0.5f * u, 0.7f * u))
            drawCircle(t(mezcla(col, Color.Black, 0.2f), oscuro, 0.35f), rr * 1.2f, c)
            drawCircle(t(col, oscuro, 0.35f), rr, c)
            for (k in 0 until 4) { val a = k * Math.PI / 2 + Math.PI / 4; drawRect(t(col, oscuro, 0.35f), Offset(c.x + cos(a).toFloat() * rr - rr * 0.25f, c.y + sin(a).toFloat() * rr - rr * 0.25f), Size(rr * 0.5f, rr * 0.5f)) }
            drawCircle(t(mezcla(col, Color.Black, 0.35f), oscuro, 0.35f), rr * 0.5f, c)
            drawCircle(Color.White.copy(alpha = if (blanca) 0.4f else 0.15f), rr * 0.2f, c - Offset(rr * 0.4f, rr * 0.4f))
        }
        // Abajo, la espada tendida a lo largo del tablero.
        val ye = (perg.bottom + campo.bottom) / 2
        val ae = minOf(1.6f * u, (campo.bottom - perg.bottom) * 0.2f)
        espada(Offset(w * 0.22f, ye), Offset(w * 0.8f, ye), ae, oscuro, u)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.OTRAS_VIDAS))
        // El muro del adarve, entero, y el cielo entre las almenas.
        val adarve = h * 0.11f
        drawRect(if (oscuro) mezcla(e.fondo, CIELO_NOCHE, 0.7f) else mezcla(e.fondo, CIELO, 0.6f), Offset.Zero, Size(w, adarve))
        if (oscuro) listOf(0.13f to 0.3f, 0.47f to 0.18f, 0.81f to 0.4f).forEach { (fx, fy) -> drawCircle(Color(0xFFF2EBD8).copy(alpha = 0.7f), 0.35f * u, Offset(w * fx, adarve * fy)) }
        sillares(Rect(0f, adarve, w, h * 0.93f), 4.6f * u, PIEDRA_LUZ, PIEDRA_OSC, 5, oscuro, u)
        val merlon = 7.5f * u; val vano = 4.5f * u
        val nm = ((w + vano) / (merlon + vano)).toInt()
        val x0 = (w - (nm * merlon + (nm - 1) * vano)) / 2
        for (k in 0 until nm) {
            val r = Rect(x0 + k * (merlon + vano), 0.8f * u, x0 + k * (merlon + vano) + merlon, adarve)
            drawRect(t(JUNTA, oscuro), r.topLeft, r.size)
            listOf(0f, 0.5f).forEachIndexed { j, f ->
                val q = Rect(r.left + 0.35f * u, r.top + r.height * f + 0.35f * u, r.right - 0.35f * u, r.top + r.height * (f + 0.5f) - 0.35f * u)
                drawRect(t(mezcla(PIEDRA, PIEDRA_LUZ, 0.4f + 0.5f * azar(40 + k, j)), oscuro), q.topLeft, q.size)
            }
            drawRect(t(PIEDRA_LUZ, oscuro), Offset(r.left - 0.4f * u, r.top), Size(r.width + 0.8f * u, 1f * u))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.right - 0.8f * u, r.top + 1f * u), Size(0.8f * u, r.height - 1f * u))
            if (k % 2 == 1) drawRect(t(NEGRO, oscuro, 0.1f), Offset(r.center.x - 0.4f * u, r.top + r.height * 0.3f), Size(0.8f * u, r.height * 0.5f))
        }
        // La cornisa de piedra, con su sombra, y el friso de escudos sobre las hornacinas.
        val cor = Rect(0f, adarve, w, adarve + 2.4f * u)
        drawRect(t(PIEDRA_LUZ, oscuro), cor.topLeft, cor.size)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.25f), cor.topLeft, Size(w, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, cor.bottom), Size(w, 0.9f * u))
        val col = n.width / 5f
        val ph = 1.7f * u
        for (i in 0 until 5) {
            val cx = n.left + col * (i + 0.5f)
            val ey = (cor.bottom + n.top) / 2
            val eh = minOf((n.top - cor.bottom) * 0.82f, 9f * u)
            blason(Offset(cx, ey), eh * 0.8f, eh, if (i % 2 == 0) 0 else if (i == 1) 1 else 2, oscuro, u)
        }
        // Las cinco hornacinas de arco apuntado: el anillo de dovelas y el fondo encalado (el nicho).
        val cal = nicho(e, oscuro, if (oscuro) PIEDRA_OSC else LINO)
        for (i in 0 until 5) {
            val r = Rect(n.left + col * i + ph, n.top + 1.8f * u, n.left + col * (i + 1) - ph, n.bottom)
            val fl = r.width * 0.55f
            val ext = apuntado(r.crece(1.6f * u), fl + 1.6f * u)
            val int = apuntado(r, fl)
            drawPath(ext, t(PIEDRA_LUZ, oscuro))
            clipPath(ext) {
                val centro = Offset(r.center.x, r.top + fl)
                for (k in 0..8) {
                    val a = Math.toRadians(180.0 + k * 22.5)
                    drawLine(t(JUNTA, oscuro), centro, centro + Offset(cos(a).toFloat(), sin(a).toFloat()) * (r.width * 1.5f), 0.35f * u)
                }
                var y = r.top + fl + 3.6f * u
                while (y < r.bottom) { drawLine(t(JUNTA, oscuro), Offset(r.left - 2f * u, y), Offset(r.right + 2f * u, y), 0.35f * u); y += 3.6f * u }
            }
            drawPath(ext, Color.Black.copy(alpha = 0.25f), style = Stroke(0.35f * u))
            drawPath(int, cal)
            clipPath(int) {
                drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), r.top, r.top + fl * 1.3f), r.topLeft, r.size)
                drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.1f), Color.Transparent), r.left, r.left + 3f * u), r.topLeft, r.size)
            }
            drawPath(int, Color.Black.copy(alpha = 0.3f), style = Stroke(0.4f * u))
        }
        // La repisa, el zócalo de almagra y el piso ajedrezado.
        val repisa = Rect(0f, n.bottom, w, n.bottom + 2f * u)
        drawRect(t(PIEDRA_LUZ, oscuro), repisa.topLeft, repisa.size)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.3f), repisa.topLeft, Size(w, 0.4f * u))
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, repisa.bottom), Size(w, 0.9f * u))
        val zoc = Rect(0f, repisa.bottom + 0.9f * u, w, h * 0.93f)
        drawRect(t(ALMAGRA, oscuro, 0.45f), zoc.topLeft, zoc.size)
        drawRect(t(BLANCO, oscuro, 0.4f), Offset(0f, zoc.top + 1f * u), Size(w, 0.4f * u))
        drawRect(t(BLANCO, oscuro, 0.4f), Offset(0f, zoc.bottom - 1.4f * u), Size(w, 0.4f * u))
        ajedrez(Rect(0f, h * 0.93f, w, h), h * 0.07f / 1.5f, oscuro)
    }

    // ---------------------------------------------------------------- Íconos de mapa

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 108f
        val c = Offset(w / 2, h / 2)
        val piedra = t(Color(0xFFD8CBAE), oscuro, 0.3f); val sombra = t(Color(0xFFAE9F80), oscuro, 0.3f); val linea = t(Color(0xFF4E4536), oscuro, 0.25f)
        when (id) {
            "unesco" -> {
                // El castillo de frontera: dos torres almenadas, el muro con su puerta y el estandarte de la cruz.
                val suelo = c.y + 40 * u
                drawOval(t(ARENA, oscuro, 0.4f), Offset(c.x - 50 * u, suelo - 4 * u), Size(100 * u, 8 * u))
                fun almenas(r: Rect) {
                    val m = r.width / 5
                    for (k in 0 until 3) drawRect(piedra, Offset(r.left + k * 2 * m, r.top - 5 * u), Size(m, 5 * u))
                    for (k in 0 until 3) drawRect(linea, Offset(r.left + k * 2 * m, r.top - 5 * u), Size(m, 5 * u), style = Stroke(0.9f * u))
                }
                val muro = Rect(c.x - 24 * u, c.y - 6 * u, c.x + 24 * u, suelo)
                drawRect(piedra, muro.topLeft, muro.size); almenas(muro)
                drawRect(linea, muro.topLeft, muro.size, style = Stroke(1.1f * u))
                listOf(-1f, 1f).forEach { s ->
                    val tr = Rect(c.x + s * 30 * u - 9 * u, c.y - 22 * u, c.x + s * 30 * u + 9 * u, suelo)
                    drawRect(piedra, tr.topLeft, tr.size)
                    drawRect(sombra, Offset(if (s > 0) tr.right - 5 * u else tr.left, tr.top), Size(5 * u, tr.height))
                    almenas(tr)
                    drawRect(linea, tr.topLeft, tr.size, style = Stroke(1.1f * u))
                    drawRect(t(NEGRO, oscuro, 0.1f), Offset(tr.center.x - 1.2f * u, tr.top + 8 * u), Size(2.4f * u, 9 * u))
                    var y = tr.top + 6 * u
                    while (y < tr.bottom - 2 * u) { drawLine(linea.copy(alpha = 0.35f), Offset(tr.left, y), Offset(tr.right, y), 0.5f * u); y += 6 * u }
                }
                val pu = Rect(c.x - 7 * u, c.y + 14 * u, c.x + 7 * u, suelo)
                drawPath(apuntado(pu, 8 * u), t(Color(0xFF2A221A), oscuro, 0.1f))
                drawLine(t(HIERRO_LUZ, oscuro, 0.2f), Offset(pu.left + 3 * u, pu.top + 8 * u), Offset(pu.left + 3 * u, pu.bottom), 0.6f * u)
                drawLine(t(HIERRO_LUZ, oscuro, 0.2f), Offset(pu.right - 3 * u, pu.top + 8 * u), Offset(pu.right - 3 * u, pu.bottom), 0.6f * u)
                // El estandarte blanco de la cruz en la torre de la derecha, con el acento.
                val mx = c.x + 30 * u
                drawLine(linea, Offset(mx, c.y - 27 * u), Offset(mx, c.y - 46 * u), 1.1f * u)
                val ban = Path().apply { moveTo(mx, c.y - 46 * u); lineTo(mx + 18 * u, c.y - 43 * u); lineTo(mx + 14 * u, c.y - 39.5f * u); lineTo(mx + 18 * u, c.y - 36 * u); lineTo(mx, c.y - 34 * u); close() }
                drawPath(ban, t(BLANCO, oscuro, 0.2f))
                drawPath(ban, linea, style = Stroke(0.6f * u))
                drawPath(cruzPatada(Offset(mx + 6.5f * u, c.y - 40 * u), 4.2f * u), t(e.c1, oscuro, 0.1f))
                blason(Offset(c.x, c.y + 3 * u), 10 * u, 12 * u, 0, oscuro, u)
            }
            "rascar" -> {
                // El mapa de la costa con una franja raspada, y el besante de oro que la raspa.
                val r = Rect(c.x - 52 * u, c.y - 34 * u, c.x + 30 * u, c.y + 36 * u)
                translate(1.5f * u, 2f * u) { drawRect(Color.Black.copy(alpha = 0.25f), r.topLeft, r.size) }
                drawRect(t(LINO, oscuro, 0.3f), r.topLeft, r.size)
                drawRect(t(ARENA, oscuro, 0.3f), r.topLeft, r.size, style = Stroke(1.4f * u))
                drawRect(t(NEGRO, oscuro, 0.1f), r.crece(-4 * u).topLeft, r.crece(-4 * u).size, style = Stroke(0.8f * u))
                val costa = Path().apply { moveTo(r.left + 8 * u, r.bottom - 20 * u); cubicTo(r.left + 24 * u, r.bottom - 34 * u, r.left + 34 * u, r.bottom - 14 * u, r.left + 48 * u, r.bottom - 26 * u); cubicTo(r.left + 60 * u, r.bottom - 36 * u, r.left + 66 * u, r.bottom - 18 * u, r.right - 8 * u, r.bottom - 22 * u) }
                drawPath(costa, t(TINTA, oscuro, 0.1f).copy(alpha = 0.55f), style = Stroke(1f * u))
                val raspa = Rect(r.left + 10 * u, r.top + 9 * u, r.right - 14 * u, r.top + 30 * u)
                clipRect(raspa.left, raspa.top, raspa.right, raspa.bottom) {
                    drawRect(t(e.c1, oscuro, 0.15f), raspa.topLeft, raspa.size)
                    drawOval(t(e.c2, oscuro, 0.15f), Offset(raspa.left + 18 * u, raspa.top + 4 * u), Size(24 * u, 18 * u))
                    var x = raspa.left - 10 * u
                    while (x < raspa.right) { drawLine(Color.White.copy(alpha = 0.35f), Offset(x, raspa.bottom), Offset(x + 10 * u, raspa.top), 0.8f * u); x += 5 * u }
                }
                drawPath(Path().apply { moveTo(raspa.right, raspa.top); lineTo(raspa.right + 4 * u, raspa.top + 3 * u); lineTo(raspa.right, raspa.top + 7 * u); lineTo(raspa.right + 3 * u, raspa.top + 12 * u); lineTo(raspa.right, raspa.bottom) }, t(ARENA, oscuro, 0.3f), style = Stroke(1f * u))
                val mo = Offset(r.right + 6 * u, r.top + 20 * u); val rm = 15 * u
                drawCircle(Color.Black.copy(alpha = 0.3f), rm, mo + Offset(1.5f * u, 2f * u))
                drawCircle(t(ORO, oscuro, 0.15f), rm, mo)
                drawCircle(t(mezcla(ORO, Color.Black, 0.3f), oscuro, 0.15f), rm * 0.8f, mo, style = Stroke(1f * u))
                drawPath(cruzPatada(mo, rm * 0.5f), t(mezcla(ORO, Color.Black, 0.3f), oscuro, 0.15f))
                for (k in 0 until 24) { val a = k * Math.PI / 12; drawCircle(t(mezcla(ORO, Color.Black, 0.25f), oscuro, 0.15f), 0.6f * u, mo + Offset(cos(a).toFloat(), sin(a).toFloat()) * (rm * 0.9f)) }
                drawArc(t(ORO_LUZ, oscuro, 0.15f), 200f, 70f, false, Offset(mo.x - rm * 0.7f, mo.y - rm * 0.7f), Size(rm * 1.4f, rm * 1.4f), style = Stroke(1.4f * u))
            }
            else -> {
                // Tus lugares: tres banderolas de la cruz clavadas en la arena, unidas por un camino.
                drawOval(t(ARENA, oscuro, 0.35f), Offset(c.x - 66 * u, c.y + 22 * u), Size(132 * u, 18 * u))
                val ps = listOf(Offset(c.x - 44 * u, c.y + 30 * u), Offset(c.x, c.y + 26 * u), Offset(c.x + 44 * u, c.y + 31 * u))
                val camino = Path().apply { moveTo(ps[0].x, ps[0].y); quadraticTo(c.x - 22 * u, c.y + 38 * u, ps[1].x, ps[1].y); quadraticTo(c.x + 22 * u, c.y + 16 * u, ps[2].x, ps[2].y) }
                drawPath(camino, t(JUNTA, oscuro, 0.2f), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u))))
                listOf(e.c1, e.c2, ROJO).forEachIndexed { i, cr ->
                    val p = ps[i]; val alto = 54 * u
                    drawOval(Color.Black.copy(alpha = 0.25f), Offset(p.x - 4 * u, p.y - 1.5f * u), Size(8 * u, 3 * u))
                    drawLine(t(ROBLE, oscuro, 0.2f), p, Offset(p.x, p.y - alto), 1.6f * u)
                    drawPath(Path().apply { moveTo(p.x, p.y - alto - 4 * u); lineTo(p.x + 2 * u, p.y - alto); lineTo(p.x - 2 * u, p.y - alto); close() }, t(ACERO, oscuro, 0.2f))
                    val y0 = p.y - alto + 1 * u
                    val ban = Path().apply { moveTo(p.x, y0); lineTo(p.x + 22 * u, y0 + 1 * u); lineTo(p.x + 17 * u, y0 + 7 * u); lineTo(p.x + 22 * u, y0 + 13 * u); lineTo(p.x, y0 + 14 * u); close() }
                    translate(1f * u, 1.4f * u) { drawPath(ban, Color.Black.copy(alpha = 0.2f)) }
                    drawPath(ban, t(BLANCO, oscuro, 0.2f))
                    drawPath(ban, linea, style = Stroke(0.6f * u))
                    drawPath(cruzPatada(Offset(p.x + 8.5f * u, y0 + 7 * u), 5 * u), t(cr, oscuro, 0.1f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val n = caja(area(MuebleVida.NOTAS))
        // El muro de sillares y el poyo de piedra al pie, con la espada tendida.
        sillares(Rect(0f, 0f, w, h), 5f * u, PIEDRA_LUZ, PIEDRA_OSC, 7, oscuro, u)
        val poyo = Rect(0f, h * 0.935f, w, h)
        drawRect(t(PIEDRA_LUZ, oscuro), poyo.topLeft, Size(w, 1.2f * u))
        drawRect(t(PIEDRA, oscuro), Offset(0f, poyo.top + 1.2f * u), Size(w, poyo.height - 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, poyo.top + 1.2f * u), Size(w, 0.4f * u))
        // El marco de roble, de tablas verticales, con su sombra en el muro.
        val marco = Rect(w * 0.022f, h * 0.105f, w * 0.978f, h * 0.925f)
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(marco.left + 0.8f * u, marco.top + 1f * u), marco.size)
        drawRect(t(ROBLE, oscuro), marco.topLeft, marco.size)
        vetas(marco, t(ROBLE_OSC, oscuro).copy(alpha = 0.5f), 9, 1.3f * u, vertical = true, grosor = 0.35f * u)
        drawRect(t(ROBLE_OSC, oscuro), marco.topLeft, marco.size, style = Stroke(0.6f * u))
        // El fondo encalado: el nicho, hundido en el marco.
        val cal = nicho(e, oscuro, if (oscuro) PIEDRA_OSC else LINO)
        drawRect(t(ROBLE_OSC, oscuro), n.crece(0.8f * u).topLeft, n.crece(0.8f * u).size)
        drawRect(cal, n.topLeft, n.size)
        clipRect(n.left, n.top, n.right, n.bottom) {
            for (k in 0 until 9) {
                val c = Offset(n.left + n.width * azar(91, k), n.top + n.height * azar(92, k))
                halo(c, n.width * (0.08f + 0.1f * azar(93, k)), (if (oscuro) Color.White else PIEDRA_OSC).copy(alpha = 0.035f))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), Color.Transparent), n.top, n.top + 4f * u), n.topLeft, Size(n.width, 4f * u))
            drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.1f), Color.Transparent), n.left, n.left + 3f * u), n.topLeft, Size(3f * u, n.height))
        }
        // Las bisagras de punta de lanza en los travesaños de arriba y de abajo.
        listOf((marco.top + n.top) / 2, (n.bottom + marco.bottom) / 2).forEach { y ->
            listOf(-1f, 1f).forEach { s ->
                val x0 = if (s < 0) marco.left else marco.right
                val x1 = if (s < 0) w * 0.3f else w * 0.7f
                val g = 1.3f * u
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(minOf(x0, x1), y - g / 2 + 0.5f * u), Size(abs(x1 - x0), g))
                drawRect(t(HIERRO, oscuro, 0.25f), Offset(minOf(x0, x1), y - g / 2), Size(abs(x1 - x0), g))
                drawPath(Path().apply { moveTo(x1, y - g * 1.3f); lineTo(x1 - s * 3.2f * u, y); lineTo(x1, y + g * 1.3f); close() }, t(HIERRO, oscuro, 0.25f))
                drawCircle(t(HIERRO, oscuro, 0.25f), g * 0.9f, Offset(x1 + s * 0.4f * u, y))
                listOf(0.25f, 0.55f, 0.85f).forEach { f -> remache(Offset(x0 + (x1 - x0) * f, y), 0.4f * u, oscuro) }
            }
        }
        // El dintel de piedra con su línea de almagra, las dos lanzas cruzadas y los tres escudos.
        val dintel = Rect(0f, h * 0.015f, w, h * 0.1f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, dintel.bottom), Size(w, 0.8f * u))
        drawRect(t(PIEDRA_LUZ, oscuro), dintel.topLeft, dintel.size)
        drawRect(t(ALMAGRA, oscuro, 0.4f), Offset(0f, dintel.bottom - 1.6f * u), Size(w, 0.8f * u))
        var dx = 7f * u; var k = 0
        while (dx < w) { drawLine(t(JUNTA, oscuro), Offset(dx, dintel.top), Offset(dx, dintel.bottom - 1.6f * u), 0.3f * u); dx += (18f + 8f * azar(3, k)) * u; k++ }
        val cx = w / 2
        listOf(-1f, 1f).forEach { s ->
            val a = Offset(cx - s * 14f * u, h * 0.135f); val b = Offset(cx + s * 12f * u, h * 0.005f)
            drawLine(Color.Black.copy(alpha = 0.3f), a + Offset(0.5f * u, 0.7f * u), b + Offset(0.5f * u, 0.7f * u), 0.8f * u)
            drawLine(t(ROBLE_LUZ, oscuro, 0.3f), a, b, 0.75f * u, StrokeCap.Round)
            val d = (b - a) / (b - a).getDistance(); val nn = Offset(-d.y, d.x)
            drawPath(Path().apply { val p0 = b + d * (2.6f * u); moveTo(p0.x, p0.y); lineTo((b + nn * (0.9f * u)).x, (b + nn * (0.9f * u)).y); lineTo((b - nn * (0.9f * u)).x, (b - nn * (0.9f * u)).y); close() }, t(ACERO, oscuro, 0.3f))
        }
        blason(Offset(cx, h * 0.07f), 7.5f * u, 9.2f * u, 0, oscuro, u)
        blason(Offset(w * 0.24f, h * 0.06f), 5.4f * u, 6.6f * u, 1, oscuro, u)
        blason(Offset(w * 0.76f, h * 0.06f), 5.4f * u, 6.6f * u, 2, oscuro, u)
        // La espada envainada sobre el poyo.
        espada(Offset(w * 0.32f, (poyo.top + h) / 2 + 0.4f * u), Offset(w * 0.7f, (poyo.top + h) / 2 + 0.4f * u), 0.9f * u, oscuro, u)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val w = r.width; val h = r.height; val s = minOf(w, h) / 100f
        // La carta: un pliego de lino blanco con sus dobleces en tercios y la sombra en la cal.
        val hoja = Rect(r.left + w * 0.025f, r.top + h * 0.03f, r.right - w * 0.025f, r.bottom - h * 0.03f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.28f), Offset(hoja.left + 1.2f * s, hoja.top + 1.8f * s), hoja.size)
        val papel = if (oscuro) mezcla(LINO, NOCHE, 0.4f) else BLANCO
        drawRect(papel, hoja.topLeft, hoja.size)
        listOf(hoja.top + hoja.height / 3, hoja.top + hoja.height * 2 / 3).forEach { y ->
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.07f)), y - 3f * s, y), Offset(hoja.left, y - 3f * s), Size(hoja.width, 3f * s))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.6f), Offset(hoja.left, y + 0.4f * s), Offset(hoja.right, y + 0.4f * s), 0.7f * s)
        }
        drawRect(mezcla(ARENA, Color.Black, if (oscuro) 0.5f else 0.1f), hoja.topLeft, hoja.size, style = Stroke(0.7f * s))
        // El filete del baucent arriba: una raya negra y una roja.
        drawLine(mezcla(NEGRO, NOCHE, 0f).copy(alpha = 0.55f), Offset(hoja.left + w * 0.08f, r.top + h * 0.13f), Offset(hoja.right - w * 0.08f, r.top + h * 0.13f), 0.7f * s)
        drawLine(ROJO.copy(alpha = 0.55f), Offset(hoja.left + w * 0.08f, r.top + h * 0.145f), Offset(hoja.right - w * 0.08f, r.top + h * 0.145f), 0.5f * s)
        // El sello de cera pendiente, con su cordón y la cruz: el color de la cera dice el tipo.
        val cera = when (e.tipo) { "idea" -> CERA; "pensamiento" -> CERA_NEGRA; "proposito" -> CERA_VERDE; else -> CERA_AMARILLA }
        val sc = Offset(r.center.x, r.bottom - h * 0.06f)
        val rs = 5.6f * s
        val ranura = hoja.bottom - h * 0.13f
        drawLine(Color.Black.copy(alpha = 0.45f), Offset(sc.x - 3f * s, ranura), Offset(sc.x + 3f * s, ranura), 0.9f * s)
        listOf(-1f, 1f).forEach { k -> drawLine(mezcla(ROJO, NOCHE, if (oscuro) 0.25f else 0f), Offset(sc.x + k * 1.6f * s, ranura), Offset(sc.x + k * 1.2f * s, sc.y), 0.9f * s) }
        drawCircle(Color.Black.copy(alpha = 0.35f), rs, sc + Offset(0.7f * s, 1f * s))
        drawCircle(cera, rs, sc)
        drawCircle(mezcla(cera, Color.Black, 0.4f), rs * 0.78f, sc, style = Stroke(0.7f * s))
        drawPath(cruzPatada(sc, rs * 0.52f), mezcla(cera, Color.Black, 0.4f))
        drawCircle(Color.White.copy(alpha = 0.3f), rs * 0.22f, sc - Offset(rs * 0.42f, rs * 0.42f))
        // Arriba: un clavo de hierro forjado o, si está fijada, una daga que atraviesa la carta.
        if (e.fijada) {
            val y = r.top + h * 0.065f
            val a = Offset(r.left + w * 0.22f, y); val b = Offset(r.right - w * 0.2f, y)
            espada(a, b, 1.6f * s, oscuro = false, u = s)
            drawLine(Color.Black.copy(alpha = 0.35f), Offset(r.left + w * 0.52f, y - 1.2f * s), Offset(r.left + w * 0.6f, y - 1.2f * s), 0.6f * s)
        } else clavoCuadrado(Offset(r.center.x, r.top + h * 0.07f), 2.1f * s, oscuro = false)
    }

    /** La tinta de hierro de las cartas: el lino es blanco de día y, en la vigilia, se ve a la luz de la antorcha. */
    override fun tintaNota(oscuro: Boolean): Color = TINTA
}
