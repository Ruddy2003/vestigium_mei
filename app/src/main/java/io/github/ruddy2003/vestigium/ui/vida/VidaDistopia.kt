package io.github.ruddy2003.vestigium.ui.vida

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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

/**
 * Distopía: Vida en el edificio del Ministerio, con el gris del hormigón y del acero, el rojo de cartel y el ojo que todo lo
 * ve (los mismos materiales que su archivo de logros y su Ideario).
 * - Social: la sala del comité. La mesa de acero bajo la lámpara colgante y su cono de luz, dos sillas de oficina, el cartel
 *   rojo con el ojo en el muro de bloques, la cámara de vigilancia y el interfono.
 * - Mapas: la mesa de control con su cuadrícula de sectores numerados (números en barras de estarcido), un sector vedado en
 *   rojo, el sello del Ministerio, el cuadrante y la luz de alarma en las esquinas.
 * - Otras vidas: archiveros de acero, cada medalla en una gaveta abierta con sus carpetas de manila al fondo, la tarjeta del
 *   portaetiquetas y el tirador; uno lleva la franja roja de «clasificado».
 * - Íconos: el Ministerio monolítico con el ojo, el sello «aprobado» que raspa la capa, las chinchetas rojas con su hilo.
 * - Notas: el corcho de expedientes en su marco de acero, con el hilo rojo de la investigación; las notas son fichas
 *   grapadas (o clavadas, si están fijadas) con su margen rojo y el detalle de su tipo.
 */
internal object VidaDistopia : DisenoVida {
    private val GRIS = Color(0xFF6E7378)
    private val GRIS_LUZ = Color(0xFF9AA0A6)
    private val GRIS_OSC = Color(0xFF3E4246)
    private val NEGRO = Color(0xFF141414)
    private val MANILA = Color(0xFFD9C48E)
    private val PAPEL = Color(0xFFECE8DC)
    private val HORMIGON = Color(0xFFB8B6AE)
    private val HORMIGON_OSC = Color(0xFF6C6A64)
    private val CORCHO = Color(0xFF9A8466)

    /** El papel de la ficha: el de oficio de día; de noche, el papel carbón de la copia. */
    private val FICHA = Color(0xFFF3F0E6)
    private val FICHA_NOCHE = Color(0xFF2A2C2F)

    /** La unidad de dibujo: la centésima parte del lado menor del lienzo. */
    private fun DrawScope.u() = size.minDimension / 100f

    /** El color en oscuro: hundido hacia el negro del archivo, como en sus logros. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0C0D), 0.4f) else c

    /** El rojo de cartel del esquema, apenas mezclado con el fondo. */
    private fun rojoDe(e: EstadoMueble, oscuro: Boolean): Color = mezcla(e.fondo, e.acento, if (oscuro) 0.78f else 0.88f)

    /** Los centros de las cinco medallas de Otras vidas, como los reparte la fila de la pantalla (10 dp de margen, ≈ 50 dp cada una). */
    private fun DrawScope.centrosMedallas(): List<Float> {
        val pad = 10f * density; val iw = 50f * density
        val gap = ((size.width - 2 * pad - 5 * iw) / 6f).coerceAtLeast(0f)
        return List(5) { k -> pad + gap * (k + 1) + iw * (k + 0.5f) }
    }

    /** El ojo que todo lo ve: la almendra, el iris, la pupila y el contorno. */
    private fun DrawScope.ojo(c: Offset, a: Float, blanco: Color, iris: Color, pupila: Color, contorno: Color) {
        val almendra = Path().apply {
            moveTo(c.x - a, c.y); quadraticTo(c.x, c.y - a * 0.9f, c.x + a, c.y); quadraticTo(c.x, c.y + a * 0.9f, c.x - a, c.y); close()
        }
        drawPath(almendra, blanco)
        clipPath(almendra) { drawCircle(iris, a * 0.4f, c); drawCircle(pupila, a * 0.18f, c) }
        drawPath(almendra, contorno, style = Stroke(a * 0.1f))
    }

    /** El muro de bloques de hormigón trabados en [r], en un solo trazo: hiladas de [alto] y bloques de [largo]. */
    private fun DrawScope.muroBloques(r: Rect, alto: Float, largo: Float, color: Color, grosor: Float) {
        if (alto < 1f || largo < 1f) return
        val p = Path()
        var y = r.top; var k = 0
        while (y < r.bottom && k < 40) {
            p.moveTo(r.left, y); p.lineTo(r.right, y)
            var x = r.left + (if (k % 2 == 0) 0f else largo / 2); var j = 0
            while (x < r.right && j < 40) { p.moveTo(x, y); p.lineTo(x, minOf(y + alto, r.bottom)); x += largo; j++ }
            y += alto; k++
        }
        drawPath(p, color, style = Stroke(grosor))
    }

    /** Una silla de acero de oficina, de lado: el respaldo en [x], el asiento hacia la mesa ([s] = 1 a la derecha, −1 a la izquierda). */
    private fun DrawScope.silla(x: Float, suelo: Float, alto: Float, s: Float, color: Color, luz: Color, u: Float) {
        val asiento = suelo - alto * 0.45f
        val fondoAsiento = x + s * alto * 0.42f
        drawLine(color, Offset(x, suelo), Offset(x, suelo - alto), 1.1f * u, StrokeCap.Round)
        drawLine(color, Offset(x, suelo - alto), Offset(x, suelo - alto * 0.62f), 2.6f * u, StrokeCap.Round)
        drawLine(color, Offset(x, asiento), Offset(fondoAsiento, asiento), 1.7f * u, StrokeCap.Round)
        drawLine(luz, Offset(x + s * 0.6f * u, asiento - 0.6f * u), Offset(fondoAsiento, asiento - 0.6f * u), 0.4f * u)
        drawLine(color, Offset(fondoAsiento, asiento), Offset(fondoAsiento, suelo), 1.1f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val rojo = rojoDe(e, oscuro)
        // El muro de la sala: una veladura de hormigón y los bloques trabados, apenas marcados.
        drawRect(m(HORMIGON, if (oscuro) 0.10f else 0.14f), Offset.Zero, size)
        muroBloques(Rect(0f, 0f, w, h), 12f * u, 26f * u, m(HORMIGON_OSC, if (oscuro) 0.22f else 0.18f), 0.5f * u)

        // La lámpara colgante y su cono de luz sobre la mesa (un solo degradado, tenue para que se lea el grafo).
        val lx = w / 2; val pantallaY = 7f * u; val ym = h - 9f * u
        val luz = if (oscuro) Color(0xFFE8F0E4) else Color(0xFFFFFBEA)
        val cono = Path().apply { moveTo(lx - 7f * u, pantallaY); lineTo(lx + 7f * u, pantallaY); lineTo(lx + w * 0.3f, ym); lineTo(lx - w * 0.3f, ym); close() }
        drawPath(cono, Brush.verticalGradient(listOf(luz.copy(alpha = if (oscuro) 0.10f else 0.35f), luz.copy(alpha = 0f)), pantallaY, ym))
        drawLine(m(NEGRO, 0.6f), Offset(lx, 0f), Offset(lx, 2.5f * u), 0.6f * u)
        val pantalla = Path().apply { moveTo(lx - 2.5f * u, 2.5f * u); lineTo(lx + 2.5f * u, 2.5f * u); lineTo(lx + 7f * u, pantallaY); lineTo(lx - 7f * u, pantallaY); close() }
        drawPath(pantalla, m(GRIS_OSC, if (oscuro) 0.85f else 0.8f))
        drawLine(m(GRIS_LUZ, 0.7f), Offset(lx - 2f * u, 3f * u), Offset(lx - 6f * u, pantallaY - 0.4f * u), 0.5f * u)
        drawOval(luz.copy(alpha = if (oscuro) 0.85f else 0.95f), Offset(lx - 3f * u, pantallaY - 0.8f * u), Size(6f * u, 1.8f * u))

        // El cartel del muro izquierdo: el rojo de propaganda, sus rayos, el ojo que todo lo ve y el lema en barras.
        val cartel = Rect(3f * u, 9f * u, 18f * u, 35f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(cartel.left + 0.8f * u, cartel.top + 0.8f * u), cartel.size)
        drawRect(rojo, cartel.topLeft, cartel.size)
        val oc = Offset(cartel.center.x, cartel.top + 9f * u)
        for (k in 0 until 12) {
            val a = k * Math.PI / 6
            drawLine(m(NEGRO, 0.55f), oc, Offset(oc.x + 6.8f * u * cos(a).toFloat(), oc.y + 6.8f * u * sin(a).toFloat()), 0.5f * u)
        }
        ojo(oc, 5.2f * u, m(PAPEL, 0.95f), m(GRIS, 0.9f), m(NEGRO, 1f), m(NEGRO, 0.9f))
        drawRect(m(PAPEL, 0.85f), Offset(cartel.left + 2.5f * u, cartel.top + 17.5f * u), Size(cartel.width - 5f * u, 1.6f * u))
        drawRect(m(NEGRO, 0.9f), Offset(cartel.left, cartel.bottom - 5.5f * u), Size(cartel.width, 5.5f * u))
        drawRect(m(PAPEL, 0.8f), Offset(cartel.left + 2.5f * u, cartel.bottom - 3.4f * u), Size(cartel.width - 5f * u, 1.2f * u))

        // La cámara de vigilancia en la esquina derecha, con su piloto rojo; debajo, el interfono con su rejilla.
        val cam = Offset(w - 9f * u, 7f * u)
        drawLine(m(GRIS_OSC, 0.8f), Offset(w - 1.5f * u, 3f * u), Offset(cam.x + 3f * u, cam.y), 1.2f * u)
        rotate(24f, cam) {
            drawRect(m(GRIS_LUZ, 0.9f), Offset(cam.x - 7f * u, cam.y - 2.4f * u), Size(10f * u, 4.8f * u))
            drawRect(m(NEGRO, 0.9f), Offset(cam.x - 8.6f * u, cam.y - 1.6f * u), Size(1.8f * u, 3.2f * u))
            drawCircle(e.acento.copy(alpha = if (oscuro) 0.3f else 0.18f), 2.4f * u, Offset(cam.x + 1.4f * u, cam.y - 0.8f * u))
            drawCircle(e.acento, 0.7f * u, Offset(cam.x + 1.4f * u, cam.y - 0.8f * u))
        }
        val inter = Rect(w - 15f * u, h * 0.36f, w - 5f * u, h * 0.36f + 14f * u)
        drawRect(m(GRIS, if (oscuro) 0.6f else 0.55f), inter.topLeft, inter.size)
        drawRect(m(GRIS_OSC, 0.7f), inter.topLeft, inter.size, style = Stroke(0.6f * u))
        for (k in 0 until 4) drawLine(m(NEGRO, 0.7f), Offset(inter.left + 2f * u, inter.top + (2.5f + k * 1.8f) * u), Offset(inter.right - 2f * u, inter.top + (2.5f + k * 1.8f) * u), 0.6f * u)
        drawCircle(rojo, 1.3f * u, Offset(inter.center.x, inter.bottom - 2.8f * u))

        // Las dos sillas de acero, cara a la mesa (en oscuro, el acero claro, para que no se pierda en la penumbra).
        val acero = if (oscuro) m(GRIS_LUZ, 0.5f) else m(GRIS_OSC, 0.7f)
        val aceroLuz = if (oscuro) m(GRIS_LUZ, 0.75f) else m(GRIS_LUZ, 0.6f)
        silla(6f * u, h - 1.2f * u, 22f * u, 1f, acero, aceroLuz, u)
        silla(w - 6f * u, h - 1.2f * u, 22f * u, -1f, acero, aceroLuz, u)

        // La mesa de acero del comité, de frente: el tablero, su canto, las patas y el travesaño.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.10f), Offset(w * 0.12f, ym + 2.6f * u), Size(w * 0.76f, 1.2f * u))
        drawRect(if (oscuro) m(GRIS_LUZ, 0.55f) else m(GRIS, 0.7f), Offset(w * 0.1f, ym), Size(w * 0.8f, 2.6f * u))
        drawLine(aceroLuz, Offset(w * 0.1f, ym + 0.3f * u), Offset(w * 0.9f, ym + 0.3f * u), 0.5f * u)
        listOf(0.13f, 0.87f).forEach { f -> drawRect(acero, Offset(w * f - 1f * u, ym + 2.6f * u), Size(2f * u, h - ym - 2.6f * u)) }
        drawLine(acero, Offset(w * 0.13f, h - 2.5f * u), Offset(w * 0.87f, h - 2.5f * u), 0.8f * u)
        // Sobre la mesa: el expediente de manila con su franja roja y el sello de goma.
        val ex = Rect(w * 0.62f, ym - 1.6f * u, w * 0.62f + 16f * u, ym)
        drawRect(m(MANILA, if (oscuro) 0.6f else 0.9f), ex.topLeft, ex.size)
        drawRect(rojo, Offset(ex.left + 2f * u, ex.top), Size(3f * u, ex.height))
        val sx = w * 0.36f
        drawRect(m(NEGRO, 0.8f), Offset(sx - 2f * u, ym - 1.2f * u), Size(4f * u, 1.2f * u))
        drawLine(m(GRIS_OSC, 0.9f), Offset(sx, ym - 1.2f * u), Offset(sx, ym - 3.4f * u), 1f * u)
        drawCircle(rojo, 1.2f * u, Offset(sx, ym - 4f * u))
        // El piso.
        drawRect(m(NEGRO, if (oscuro) 0.5f else 0.25f), Offset(0f, h - 1.2f * u), Size(w, 1.2f * u))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val rojo = rojoDe(e, oscuro)
        val mesa = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 6f * u)
        // Las patas de acero que asoman abajo, con su travesaño.
        listOf(0.06f, 0.94f).forEach { f -> drawRect(m(GRIS_OSC, 0.8f), Offset(w * f - 1.2f * u, mesa.bottom), Size(2.4f * u, h - mesa.bottom)) }
        drawLine(m(GRIS_OSC, 0.55f), Offset(w * 0.06f, h - 2f * u), Offset(w * 0.94f, h - 2f * u), 0.8f * u)
        // El tablero de la mesa de control: la chapa gris, tenue, bajo el mapa.
        drawRect(m(GRIS, if (oscuro) 0.16f else 0.12f), mesa.topLeft, mesa.size)
        // La cuadrícula de sectores.
        val paso = 14f * u
        val columnas = if (paso > 0f) (mesa.width / paso).toInt() else 0
        val rejilla = Path()
        var x = mesa.left + paso; var k = 0
        while (x < mesa.right && k < 60) { rejilla.moveTo(x, mesa.top); rejilla.lineTo(x, mesa.bottom); x += paso; k++ }
        var y = mesa.top + paso; k = 0
        while (y < mesa.bottom && k < 30) { rejilla.moveTo(mesa.left, y); rejilla.lineTo(mesa.right, y); y += paso; k++ }
        drawPath(rejilla, m(NEGRO, if (oscuro) 0.16f else 0.10f), style = Stroke(0.5f * u))
        // El sector vedado, rayado en rojo, en la esquina de arriba a la derecha.
        if (columnas >= 2) {
            val zona = Rect(mesa.left + paso * (columnas - 1), mesa.top, minOf(mesa.left + paso * columnas, mesa.right), mesa.top + paso)
            clipRect(zona.left, zona.top, zona.right, zona.bottom) {
                var d = -zona.height; var j = 0
                while (d < zona.width && j < 40) { drawLine(rojo.copy(alpha = 0.45f), Offset(zona.left + d, zona.bottom), Offset(zona.left + d + zona.height, zona.top), 0.7f * u); d += 2.5f * u; j++ }
            }
        }
        // Los números de los sectores, estarcidos en barras, en el margen de arriba y en el de la izquierda.
        val tinta = m(NEGRO, if (oscuro) 0.42f else 0.35f)
        x = mesa.left + paso * 1.5f; k = 0
        while (x < mesa.right - paso * 1.5f && k < 60) {
            val n = 1 + (k % 3)
            for (j in 0 until n) drawRect(tinta, Offset(x - n * 1.1f * u + j * 2.2f * u, mesa.top + 2.4f * u), Size(1.4f * u, 2.6f * u))
            x += paso; k++
        }
        y = mesa.top + paso * 1.5f; k = 0
        while (y < mesa.bottom - paso && k < 30) {
            drawRect(tinta, Offset(mesa.left + 2.4f * u, y - 1.3f * u), Size(2.6f * u + (k % 2) * 1.6f * u, 2.6f * u))
            y += paso; k++
        }
        // El marco de acero de la mesa y su filo de luz.
        drawRect(m(GRIS, if (oscuro) 0.7f else 0.65f), mesa.topLeft, mesa.size, style = Stroke(2.4f * u))
        drawRect(m(GRIS_LUZ, if (oscuro) 0.45f else 0.6f), Offset(mesa.left + 1.6f * u, mesa.top + 1.6f * u), Size(mesa.width - 3.2f * u, mesa.height - 3.2f * u), style = Stroke(0.5f * u))
        // El sello del Ministerio en la esquina de arriba a la izquierda: el anillo rojo y la estrella de cinco puntas.
        val sello = Offset(mesa.left + 6f * u, mesa.top + 5f * u)
        drawCircle(rojo.copy(alpha = 0.8f), 3f * u, sello, style = Stroke(0.7f * u))
        drawPath(Path().apply {
            for (j in 0 until 10) {
                val a = Math.toRadians(-90.0 + j * 36.0); val rr = if (j % 2 == 0) 2f * u else 0.85f * u
                val p = Offset(sello.x + rr * cos(a).toFloat(), sello.y + rr * sin(a).toFloat())
                if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
            }
            close()
        }, rojo.copy(alpha = 0.8f))
        // El cuadrante de la consola, abajo a la izquierda.
        val dial = Offset(mesa.left + 6f * u, mesa.bottom - 5f * u)
        drawCircle(m(GRIS_OSC, 0.85f), 3.6f * u, dial)
        drawCircle(m(PAPEL, if (oscuro) 0.4f else 0.9f), 2.8f * u, dial)
        drawLine(rojo, dial, Offset(dial.x + 2f * u, dial.y - 1.4f * u), 0.5f * u, StrokeCap.Round)
        // La luz de alarma y dos interruptores, abajo a la derecha.
        val alarma = Offset(mesa.right - 5.5f * u, mesa.bottom - 5f * u)
        drawCircle(e.acento.copy(alpha = if (oscuro) 0.28f else 0.16f), 4.5f * u, alarma)
        drawCircle(m(GRIS_OSC, 0.9f), 2.6f * u, alarma)
        drawCircle(rojo, 1.9f * u, alarma)
        drawCircle(Color.White.copy(alpha = 0.5f), 0.6f * u, Offset(alarma.x - 0.6f * u, alarma.y - 0.6f * u))
        for (j in 0 until 2) {
            val ix = mesa.right - 11f * u - j * 3f * u
            drawRect(m(GRIS_OSC, 0.85f), Offset(ix - 1f * u, mesa.bottom - 6.4f * u), Size(2f * u, 3f * u))
            drawLine(m(GRIS_LUZ, 0.9f), Offset(ix, mesa.bottom - 5f * u), Offset(ix + (if (j == 0) 0.8f else -0.8f) * u, mesa.bottom - 7.6f * u), 0.6f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u(); val d = density
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val rojo = rojoDe(e, oscuro)
        val centros = centrosMedallas()
        val paso = (w - 20f * d) / 5f
        val my = h / 2 - 10f * d
        val arriba = maxOf(my - 33f * d, 9f * u)
        val estante = minOf(my + 46f * d, h - 12f * u)
        val cuerpo = Rect(1.5f * u, maxOf(arriba - 9f * u, 0.5f * u), w - 1.5f * u, h - 4f * u)
        // El cuerpo de los archiveros: acero gris con su bisel; las juntas entre un archivero y otro.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Offset(cuerpo.left + 1f * u, cuerpo.top + 1f * u), cuerpo.size)
        drawRect(m(GRIS, if (oscuro) 0.34f else 0.3f), cuerpo.topLeft, cuerpo.size)
        drawLine(m(GRIS_LUZ, if (oscuro) 0.4f else 0.55f), Offset(cuerpo.left, cuerpo.top + 0.4f * u), Offset(cuerpo.right, cuerpo.top + 0.4f * u), 0.8f * u)
        for (k in 1 until 5) {
            val xj = (centros[k - 1] + centros[k]) / 2
            drawLine(m(GRIS_OSC, if (oscuro) 0.7f else 0.5f), Offset(xj, cuerpo.top), Offset(xj, cuerpo.bottom), 0.8f * u)
            drawLine(m(GRIS_LUZ, if (oscuro) 0.25f else 0.4f), Offset(xj + 0.8f * u, cuerpo.top), Offset(xj + 0.8f * u, cuerpo.bottom), 0.4f * u)
        }
        val clasificado = Random(e.semilla.toLong()).nextInt(5)
        centros.forEachIndexed { k, cx ->
            val nw = minOf(paso * 0.82f, 60f * d)
            // El portaetiquetas con su tarjeta mecanografiada, arriba de la gaveta.
            val tw = minOf(nw * 0.5f, 26f * d); val th = minOf(5f * u, arriba - cuerpo.top - 2f * u).coerceAtLeast(1f)
            val ty = cuerpo.top + (arriba - cuerpo.top - th) / 2
            drawRect(m(GRIS_OSC, 0.8f), Offset(cx - tw / 2 - 0.6f * u, ty - 0.6f * u), Size(tw + 1.2f * u, th + 1.2f * u))
            drawRect(m(PAPEL, if (oscuro) 0.45f else 0.95f), Offset(cx - tw / 2, ty), Size(tw, th))
            drawLine(m(NEGRO, 0.45f), Offset(cx - tw * 0.32f, ty + th * 0.55f), Offset(cx + tw * 0.2f, ty + th * 0.55f), 0.5f * u)
            // La gaveta abierta: el hueco hundido donde va la medalla, tenue, con las carpetas de manila al fondo.
            val nicho = Rect(cx - nw / 2, arriba, cx + nw / 2, estante)
            drawRect(m(NEGRO, if (oscuro) 0.3f else 0.10f), nicho.topLeft, nicho.size)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.07f), nicho.topLeft, Size(nicho.width, 2f * u))
            for (j in 0 until 4) {
                val px = nicho.left + nicho.width * (0.1f + 0.2f * j)
                drawRect(m(MANILA, if (oscuro) 0.3f else 0.45f), Offset(px, nicho.top + 2.2f * u), Size(nicho.width * 0.14f, 2.4f * u))
            }
            drawLine(m(GRIS_LUZ, if (oscuro) 0.3f else 0.45f), Offset(nicho.left + 1f * u, nicho.top + 4.8f * u), Offset(nicho.right - 1f * u, nicho.top + 4.8f * u), 0.5f * u)
            drawRect(m(GRIS_OSC, if (oscuro) 0.7f else 0.55f), nicho.topLeft, nicho.size, style = Stroke(0.9f * u))
            // El frente de la gaveta, salido, con su tarjetero y el tirador.
            val frente = Rect(nicho.left - 1.5f * u, estante, nicho.right + 1.5f * u, minOf(estante + 7f * u, cuerpo.bottom - 1f * u))
            if (frente.height > 2f * u) {
                drawRect(m(GRIS, if (oscuro) 0.55f else 0.55f), frente.topLeft, frente.size)
                drawLine(m(GRIS_LUZ, if (oscuro) 0.4f else 0.7f), Offset(frente.left, frente.top + 0.3f * u), Offset(frente.right, frente.top + 0.3f * u), 0.6f * u)
                drawLine(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Offset(frente.left, frente.bottom), Offset(frente.right, frente.bottom), 0.8f * u)
                drawRoundRect(m(GRIS_OSC, 0.9f), Offset(cx - nw * 0.18f, frente.center.y - 0.9f * u), Size(nw * 0.36f, 1.8f * u), CornerRadius(0.9f * u))
            }
            // La franja roja de «clasificado» en la esquina de un archivero.
            if (k == clasificado) clipRect(nicho.left, cuerpo.top, nicho.right + 1.5f * u, arriba) {
                rotate(35f, Offset(nicho.right, cuerpo.top)) {
                    drawRect(rojo.copy(alpha = 0.85f), Offset(nicho.right - 9f * u, cuerpo.top + 1.4f * u), Size(18f * u, 2.4f * u))
                }
            }
        }
        // El zócalo con su rejilla.
        drawRect(m(GRIS_OSC, if (oscuro) 0.7f else 0.6f), Offset(0f, cuerpo.bottom), Size(w, h - cuerpo.bottom))
        var gx = 6f * u; var j = 0
        while (gx < w - 6f * u && j < 80) {
            drawRoundRect(m(NEGRO, 0.8f), Offset(gx, cuerpo.bottom + (h - cuerpo.bottom) * 0.3f), Size(1.6f * u, (h - cuerpo.bottom) * 0.4f), CornerRadius(0.8f * u))
            gx += 4.5f * u; j++
        }
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.4f
        val g = 1.6f * density
        when (id) {
            "unesco" -> {
                // El Ministerio: tres cuerpos de hormigón con sus ventanas en ranura, los estandartes rojos, el ojo en la
                // torre y la antena.
                drawLine(e.c2, Offset(c.x - r * 1.35f, c.y + r * 0.86f), Offset(c.x + r * 1.35f, c.y + r * 0.86f), g * 1.3f, StrokeCap.Round)
                val base = Rect(c.x - r * 1.1f, c.y + r * 0.22f, c.x + r * 1.1f, c.y + r * 0.86f)
                val medio = Rect(c.x - r * 0.75f, c.y - r * 0.3f, c.x + r * 0.75f, c.y + r * 0.22f)
                val torre = Rect(c.x - r * 0.38f, c.y - r * 0.8f, c.x + r * 0.38f, c.y - r * 0.3f)
                listOf(base, medio, torre).forEach { b -> drawRect(e.c2, b.topLeft, b.size) }
                fun ranuras(b: Rect, n: Int) {
                    for (k in 0 until n) {
                        val x = b.left + b.width * (k + 1) / (n + 1)
                        drawLine(e.fondo, Offset(x, b.top + b.height * 0.2f), Offset(x, b.bottom - b.height * 0.2f), g * 0.6f)
                    }
                }
                ranuras(base, 9); ranuras(medio, 6)
                listOf(-0.86f, 0.86f).forEach { f -> drawRect(e.c1, Offset(c.x + r * f - r * 0.09f, base.top + r * 0.04f), Size(r * 0.18f, base.height * 0.62f)) }
                ojo(Offset(c.x, torre.center.y), r * 0.3f, e.fondo, e.c1, e.c2, e.fondo)
                drawLine(e.c2, Offset(c.x, torre.top), Offset(c.x, c.y - r), g * 0.8f)
                drawCircle(e.c1, r * 0.07f, Offset(c.x, c.y - r))
            }
            "rascar" -> {
                // La hoja con su capa gris para rascar: un tramo ya raspado deja ver el sello rojo de «aprobado», y el sello
                // de goma sigue raspando.
                val hoja = Rect(c.x - r * 1.4f, c.y - r * 0.75f, c.x + r * 0.95f, c.y + r * 0.8f)
                drawRect(e.c2.copy(alpha = 0.22f), hoja.topLeft, hoja.size)
                val capa = hoja.deflate(r * 0.1f)
                drawRect(e.c2.copy(alpha = 0.6f), capa.topLeft, capa.size)
                val raspado = Path().apply {
                    moveTo(capa.left, capa.top + capa.height * 0.18f)
                    lineTo(capa.left + capa.width * 0.62f, capa.top + capa.height * 0.12f)
                    lineTo(capa.left + capa.width * 0.7f, capa.top + capa.height * 0.32f)
                    lineTo(capa.left + capa.width * 0.64f, capa.top + capa.height * 0.5f)
                    lineTo(capa.left + capa.width * 0.72f, capa.top + capa.height * 0.7f)
                    lineTo(capa.left + capa.width * 0.6f, capa.bottom - capa.height * 0.12f)
                    lineTo(capa.left, capa.bottom - capa.height * 0.18f)
                    close()
                }
                drawPath(raspado, e.fondo)
                val sello = Rect(capa.left + capa.width * 0.08f, capa.top + capa.height * 0.3f, capa.left + capa.width * 0.56f, capa.bottom - capa.height * 0.3f)
                drawRect(e.c1, sello.topLeft, sello.size, style = Stroke(g))
                drawRect(e.c1, sello.deflate(g * 1.4f).topLeft, sello.deflate(g * 1.4f).size, style = Stroke(g * 0.5f))
                drawLine(e.c1, Offset(sello.left + sello.width * 0.18f, sello.center.y), Offset(sello.right - sello.width * 0.18f, sello.center.y), g * 1.2f)
                drawRect(e.c2, hoja.topLeft, hoja.size, style = Stroke(g * 0.8f))
                // El sello de goma, inclinado: la base, el cuello y el pomo rojo.
                val s = Offset(c.x + r * 0.95f, c.y + r * 0.05f)
                rotate(-22f, s) {
                    drawRect(e.c2, Offset(s.x - r * 0.3f, s.y + r * 0.1f), Size(r * 0.6f, r * 0.2f))
                    drawRect(e.c1, Offset(s.x - r * 0.32f, s.y + r * 0.3f), Size(r * 0.64f, r * 0.08f))
                    drawRect(e.c2, Offset(s.x - r * 0.08f, s.y - r * 0.32f), Size(r * 0.16f, r * 0.42f))
                    drawCircle(e.c1, r * 0.2f, Offset(s.x, s.y - r * 0.45f))
                }
            }
            else -> {
                // Tus lugares: la cuadrícula de sectores con tres chinchetas rojas unidas por el hilo de la investigación.
                val plano = Rect(c.x - r * 1.4f, c.y - r * 0.8f, c.x + r * 1.4f, c.y + r * 0.85f)
                for (k in 1..3) { val x = plano.left + plano.width * k / 4; drawLine(e.c2.copy(alpha = 0.4f), Offset(x, plano.top), Offset(x, plano.bottom), g * 0.5f) }
                for (k in 1..2) { val y = plano.top + plano.height * k / 3; drawLine(e.c2.copy(alpha = 0.4f), Offset(plano.left, y), Offset(plano.right, y), g * 0.5f) }
                drawRect(e.c2.copy(alpha = 0.7f), plano.topLeft, plano.size, style = Stroke(g * 0.7f))
                val pins = listOf(Offset(c.x - r * 0.75f, c.y + r * 0.15f), Offset(c.x + r * 0.15f, c.y - r * 0.35f), Offset(c.x + r * 0.85f, c.y + r * 0.35f))
                drawPath(Path().apply { moveTo(pins[0].x, pins[0].y); lineTo(pins[1].x, pins[1].y); lineTo(pins[2].x, pins[2].y) }, e.c1.copy(alpha = 0.7f), style = Stroke(g * 0.6f))
                pins.forEach { p ->
                    drawLine(e.tinta.copy(alpha = 0.6f), p, Offset(p.x - r * 0.12f, p.y + r * 0.3f), g * 0.6f, StrokeCap.Round)
                    drawCircle(e.c1, r * 0.2f, p)
                    drawCircle(Color.Black.copy(alpha = 0.25f), r * 0.2f, p, style = Stroke(g * 0.5f))
                    drawCircle(e.fondo.copy(alpha = 0.7f), r * 0.06f, Offset(p.x - r * 0.06f, p.y - r * 0.06f))
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val rojo = rojoDe(e, oscuro)
        val marco = 3f * u
        // El marco de acero, con su filo de luz arriba y su sombra abajo.
        drawRect(m(GRIS, if (oscuro) 0.7f else 0.65f), Offset.Zero, size)
        drawLine(m(GRIS_LUZ, 0.8f), Offset(0f, 0.4f * u), Offset(w, 0.4f * u), 0.8f * u)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.2f), Offset(0f, h - 0.4f * u), Offset(w, h - 0.4f * u), 0.8f * u)
        // El corcho, con sus poros claros y oscuros (dos trazos).
        val corcho = Rect(marco, marco, w - marco, h - marco)
        drawRect(m(CORCHO, if (oscuro) 0.38f else 0.42f), corcho.topLeft, corcho.size)
        val az = Random(e.semilla.toLong())
        val poros = Path(); val brillos = Path()
        repeat(160) { k ->
            val x = corcho.left + corcho.width * az.nextFloat(); val y = corcho.top + corcho.height * az.nextFloat()
            val rr = (0.3f + 0.5f * az.nextFloat()) * u
            (if (k % 3 == 0) brillos else poros).addOval(Rect(x - rr, y - rr, x + rr, y + rr))
        }
        drawPath(poros, m(Color(0xFF4A3A28), if (oscuro) 0.4f else 0.35f))
        drawPath(brillos, m(Color(0xFFE2CFA8), if (oscuro) 0.25f else 0.4f))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), corcho.topLeft, Size(corcho.width, 1.2f * u))
        // El hilo rojo de la investigación, de chincheta en chincheta (por donde irán las notas).
        val mx = w * 0.06f; val myy = h * 0.08f; val hueco = w * 0.025f
        val cw = (w - 2 * mx - 2 * hueco) / 3f; val ch = (h - 2 * myy - hueco) / 2f
        fun punta(i: Int) = Offset(mx + (i % 3) * (cw + hueco) + cw / 2, myy + (i / 3) * (ch + hueco) + ch * 0.08f)
        val hilo = Path().apply {
            moveTo(punta(0).x, punta(0).y); lineTo(punta(4).x, punta(4).y); lineTo(punta(2).x, punta(2).y)
            moveTo(punta(1).x, punta(1).y); lineTo(punta(3).x, punta(3).y); moveTo(punta(4).x, punta(4).y); lineTo(punta(5).x, punta(5).y)
        }
        drawPath(hilo, rojo.copy(alpha = 0.75f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
        // La placa del Ministerio en el marco de arriba: el rojo de cartel y el ojo.
        val placa = Rect(w / 2 - 9f * u, 0f, w / 2 + 9f * u, 5f * u)
        drawRect(rojo, placa.topLeft, placa.size)
        drawRect(m(NEGRO, 0.7f), placa.topLeft, placa.size, style = Stroke(0.5f * u))
        ojo(placa.center, 3.2f * u, m(PAPEL, 0.95f), m(GRIS, 0.9f), m(NEGRO, 1f), m(NEGRO, 0.9f))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val u = minOf(r.width, r.height) / 100f
        val rojo = if (oscuro) mezcla(e.acento, Color.White, 0.1f) else e.acento
        val papel = if (oscuro) FICHA_NOCHE else FICHA
        // La pestaña de la carpeta de manila, que asoma detrás de los pensamientos.
        if (e.tipo == "pensamiento") drawRect(t(MANILA, oscuro), Offset(r.left + r.width * 0.06f, r.top - 5f * u), Size(r.width * 0.3f, 6f * u))
        // La sombra y la ficha.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.18f), Offset(r.left + 2f * u, r.top + 2.5f * u), r.size)
        drawRect(papel, r.topLeft, r.size)
        // El margen rojo y la raya del encabezado, por fuera del texto.
        drawLine(rojo.copy(alpha = 0.4f), Offset(r.left + r.width * 0.08f, r.top), Offset(r.left + r.width * 0.08f, r.bottom), 0.8f * u)
        drawLine((if (oscuro) Color.White else NEGRO).copy(alpha = 0.14f), Offset(r.left, r.top + r.height * 0.12f), Offset(r.right, r.top + r.height * 0.12f), 0.7f * u)
        when (e.tipo) {
            // La franja roja de «clasificado» en la esquina.
            "idea" -> clipRect(r.left, r.top, r.right, r.bottom) {
                rotate(40f, Offset(r.right, r.top)) { drawRect(rojo.copy(alpha = 0.85f), Offset(r.right - 16f * u, r.top + 5f * u), Size(32f * u, 5f * u)) }
            }
            // El sello de «aprobado», tenue, en la esquina de abajo.
            "proposito" -> {
                val sc = Offset(r.right - 14f * u, r.bottom - 11f * u)
                rotate(-14f, sc) {
                    drawCircle(rojo.copy(alpha = 0.3f), 8.5f * u, sc, style = Stroke(1.2f * u))
                    drawCircle(rojo.copy(alpha = 0.22f), 6.2f * u, sc, style = Stroke(0.6f * u))
                }
            }
            // El clip en el borde izquierdo.
            "recordatorio" -> drawRoundRect(t(GRIS_LUZ, oscuro), Offset(r.left - 1.5f * u, r.top + r.height * 0.3f), Size(5f * u, r.height * 0.34f), CornerRadius(2.5f * u), style = Stroke(1f * u))
            else -> {}
        }
        if (e.fijada) {
            // Clavada con una chincheta roja, con el filo en el rojo de cartel.
            drawRect(rojo, r.topLeft, r.size, style = Stroke(1.4f * u))
            val p = Offset(r.center.x, r.top + 6.5f * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), 6f * u, Offset(p.x + 1f * u, p.y + 1.4f * u))
            drawCircle(rojo, 5.8f * u, p)
            drawCircle(Color.White.copy(alpha = 0.55f), 1.7f * u, Offset(p.x - 1.7f * u, p.y - 1.7f * u))
        } else {
            // Grapada: la grapa de acero arriba a la izquierda.
            drawRect(if (oscuro) Color.White.copy(alpha = 0.10f) else GRIS_OSC.copy(alpha = 0.25f), r.topLeft, r.size, style = Stroke(0.6f * u))
            val g = Offset(r.left + r.width * 0.16f, r.top + 4.5f * u)
            drawLine(t(GRIS, oscuro), Offset(g.x, g.y), Offset(g.x + 11f * u, g.y), 1.2f * u, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(g.x + 1f * u, g.y - 0.4f * u), Offset(g.x + 10f * u, g.y - 0.4f * u), 0.4f * u)
        }
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO
}
