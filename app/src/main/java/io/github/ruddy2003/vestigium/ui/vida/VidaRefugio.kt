package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import java.util.Random
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Refugio: Vida bajo tierra, con el optimismo atómico de día (crema, verde azulado, amarillo y naranja) y el fósforo verde de
 * noche, con los mismos materiales que su sala común de logros y su Ideario: paneles de chapa remachados, tubos de cobre,
 * cinta amarilla y negra, el engrane de la puerta de la bóveda y el átomo.
 * - Social: el comedor común. La mesa larga de fórmica verde azulado con sus taburetes, la máquina de bebidas con su átomo,
 *   el tubo de ventilación con la cinta de aviso, la lámpara de cúpula, y el cobre con su manómetro y su llave de paso.
 * - Mapas: la terminal. La caja de la pantalla con sus remaches, la pantalla con el plano del refugio (pasillos, el pozo
 *   central, las salas) y sus líneas de barrido; abajo, el teclado, las perillas y los pilotos.
 * - Otras vidas: las cápsulas del dormitorio, cada medalla tras su ojo de buey con pernos, con la rejilla de ventilación, el
 *   piloto y la plaquita de su año.
 * - Íconos: la bóveda de cúpula con la puerta engranada, la tarjeta perforada que se raspa, las chinchetas de estrella.
 * - Notas: el tablón de avisos con su sol de propaganda y la cinta de aviso; las notas son avisos con su franja de color y
 *   su chincheta de estrella.
 */
internal object VidaRefugio : DisenoVida {
    private val MURO = Color(0xFFD9CFAF)
    private val CREMA = Color(0xFFF2E8CC)
    private val TUBO = Color(0xFF7A8088)
    private val TUBO_OSC = Color(0xFF4E5557)
    private val AMARILLO = Color(0xFFE5B436)
    private val NARANJA = Color(0xFFD9572B)
    private val COBRE = Color(0xFFB8774A)
    private val CHAPA = Color(0xFF1C6E6A)
    private val CHAPA_LUZ = Color(0xFF3C928B)
    private val CHAPA_OSC = Color(0xFF124A47)
    private val PANTALLA = Color(0xFF041208)
    private val CROMO = Color(0xFFD5DADD)
    private val NEGRO = Color(0xFF15191A)

    /** El papel de los avisos: crema de día; de noche, la pantalla verde oscura. */
    private val AVISO = Color(0xFFF6EEDA)
    private val AVISO_NOCHE = Color(0xFF0F1F14)

    private fun DrawScope.u() = size.minDimension / 100f

    /** El color en modo oscuro, más hundido, como en sus logros. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0F0F), 0.36f) else c

    /** La luz de las lámparas: cálida de día; de noche, el fósforo del esquema. */
    private fun luzDe(e: EstadoMueble, oscuro: Boolean): Color = if (oscuro) e.acento else Color(0xFFFFD98A)

    /** Los centros de las cinco medallas de Otras vidas, como los reparte la fila de la pantalla (10 dp de margen, ≈ 50 dp cada una). */
    private fun DrawScope.centrosMedallas(): List<Float> {
        val pad = 10f * density; val iw = 50f * density
        val gap = ((size.width - 2 * pad - 5 * iw) / 6f).coerceAtLeast(0f)
        return List(5) { k -> pad + gap * (k + 1) + iw * (k + 0.5f) }
    }

    /** La cinta amarilla y negra de aviso en [r]: el amarillo y las rayas inclinadas (un solo trazo). */
    private fun DrawScope.cintaAviso(r: Rect, amarillo: Color, negro: Color) {
        drawRect(amarillo, r.topLeft, r.size)
        val paso = r.height * 2f
        if (paso < 0.5f) return
        val rayas = Path()
        var x = r.left - r.height; var k = 0
        while (x < r.right && k < 300) {
            rayas.moveTo(x, r.bottom); rayas.lineTo(x + r.height, r.top); rayas.lineTo(x + r.height * 2, r.top); rayas.lineTo(x + r.height, r.bottom); rayas.close()
            x += paso * 1.5f; k++
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(rayas, negro) }
    }

    /** El átomo del refugio: el núcleo y sus tres órbitas. */
    private fun DrawScope.atomo(c: Offset, r: Float, color: Color, grosor: Float) {
        for (k in 0 until 3) rotate(k * 60f, c) { drawOval(color, Offset(c.x - r, c.y - r * 0.36f), Size(2 * r, r * 0.72f), style = Stroke(grosor)) }
        drawCircle(color, r * 0.16f, c)
    }

    // ---------------------------------------------------------------- Social

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val luz = luzDe(e, oscuro)
        // El muro de paneles crema, con sus juntas y sus remaches (tenues).
        drawRect(m(MURO, if (oscuro) 0.12f else 0.3f), Offset.Zero, size)
        val junta = if (oscuro) m(CHAPA_LUZ, 0.22f) else m(TUBO_OSC, 0.16f)
        val juntas = Path(); val remaches = Path()
        val altoP = 24f * u; val anchoP = 32f * u
        var y = 6f * u; var k = 0
        while (y < h && k < 12) {
            juntas.moveTo(0f, y); juntas.lineTo(w, y)
            var x = if (k % 2 == 0) 0f else anchoP / 2; var j = 0
            while (x < w && j < 40) {
                juntas.moveTo(x, y); juntas.lineTo(x, minOf(y + altoP, h))
                remaches.addOval(Rect(x + 1.2f * u, y + 1.2f * u, x + 2.1f * u, y + 2.1f * u))
                x += anchoP; j++
            }
            y += altoP; k++
        }
        drawPath(juntas, junta, style = Stroke(0.5f * u))
        drawPath(remaches, junta)
        // El zócalo verde azulado con su filete amarillo.
        val zocalo = h - 12f * u
        drawRect(m(CHAPA, if (oscuro) 0.3f else 0.3f), Offset(0f, zocalo), Size(w, h - zocalo))
        drawRect(m(AMARILLO, if (oscuro) 0.4f else 0.55f), Offset(0f, zocalo - 0.8f * u), Size(w, 0.8f * u))

        // El tubo de ventilación del techo, con sus abrazaderas, y la cinta de aviso debajo.
        drawRect(m(TUBO, if (oscuro) 0.45f else 0.6f), Offset.Zero, Size(w, 4f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), Offset(0f, 1f * u), Offset(w, 1f * u), 0.5f * u)
        var xa = 10f * u; k = 0
        while (xa < w && k < 40) { drawRect(m(NARANJA, if (oscuro) 0.45f else 0.7f), Offset(xa, 0f), Size(1.6f * u, 4f * u)); xa += 30f * u; k++ }
        cintaAviso(Rect(0f, 4f * u, w, 5.6f * u), m(AMARILLO, if (oscuro) 0.5f else 0.75f), m(NEGRO, if (oscuro) 0.8f else 0.7f))
        // La lámpara de cúpula del comedor, con su halo (capas planas).
        val lc = Offset(w / 2, 6.6f * u)
        for (j in 0 until 3) drawCircle(luz.copy(alpha = if (oscuro) 0.05f else 0.12f), (26f - j * 8f) * u, Offset(lc.x, lc.y + 2f * u))
        drawArc(m(CHAPA, if (oscuro) 0.7f else 0.85f), 180f, 180f, true, Offset(lc.x - 5f * u, lc.y - 1.5f * u), Size(10f * u, 7f * u))
        drawRect(m(CROMO, if (oscuro) 0.6f else 0.9f), Offset(lc.x - 5.4f * u, lc.y + 1.8f * u), Size(10.8f * u, 0.8f * u))
        drawOval(luz.copy(alpha = if (oscuro) 0.85f else 0.95f), Offset(lc.x - 3f * u, lc.y + 2.2f * u), Size(6f * u, 1.6f * u))

        // La máquina de bebidas a la izquierda: el cuerpo naranja de esquinas redondas, la vitrina con las botellas, el
        // átomo en su medallón, la ranura de las monedas y la bandeja.
        val maq = Rect(2.5f * u, h * 0.2f, 19.5f * u, h - 2f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(maq.left + 1f * u, maq.top + 1f * u), maq.size, CornerRadius(4f * u))
        drawRoundRect(m(NARANJA, if (oscuro) 0.6f else 0.85f), maq.topLeft, maq.size, CornerRadius(4f * u))
        drawRoundRect(m(CROMO, if (oscuro) 0.4f else 0.7f), maq.topLeft, maq.size, CornerRadius(4f * u), style = Stroke(0.7f * u))
        val medallon = Offset(maq.center.x, maq.top + 6f * u)
        drawCircle(m(CREMA, if (oscuro) 0.5f else 0.95f), 4f * u, medallon)
        atomo(medallon, 3.2f * u, m(CHAPA, 0.9f), 0.45f * u)
        val vitrina = Rect(maq.left + 2f * u, maq.top + 11.5f * u, maq.right - 5f * u, maq.top + 11.5f * u + maq.height * 0.42f)
        drawRect(if (oscuro) mezcla(e.fondo, PANTALLA, 0.9f) else m(CREMA, 0.95f), vitrina.topLeft, vitrina.size)
        if (oscuro) drawRect(luz.copy(alpha = 0.08f), vitrina.topLeft, vitrina.size)
        for (f in 0 until 3) {
            val fy = vitrina.top + vitrina.height * (f + 1) / 3.2f
            drawLine(m(CROMO, if (oscuro) 0.5f else 0.8f), Offset(vitrina.left, fy), Offset(vitrina.right, fy), 0.5f * u)
            for (b in 0 until 3) {
                val bx = vitrina.left + vitrina.width * (b + 0.5f) / 3f
                drawRoundRect(m(if ((b + f) % 2 == 0) CHAPA_LUZ else NARANJA, if (oscuro) 0.6f else 0.85f), Offset(bx - 0.9f * u, fy - 4.2f * u), Size(1.8f * u, 4.2f * u), CornerRadius(0.6f * u))
                drawRect(m(NEGRO, 0.6f), Offset(bx - 0.4f * u, fy - 5f * u), Size(0.8f * u, 0.9f * u))
            }
        }
        drawRect(m(CHAPA_OSC, 0.6f), vitrina.topLeft, vitrina.size, style = Stroke(0.5f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.5f), Offset(vitrina.left + 1f * u, vitrina.bottom - 2f * u), Offset(vitrina.left + 4f * u, vitrina.top + 2f * u), 0.5f * u)
        // La columna de la ranura y los botones, y la bandeja de abajo.
        drawRect(m(CROMO, if (oscuro) 0.45f else 0.8f), Offset(maq.right - 4f * u, vitrina.top), Size(2.6f * u, vitrina.height))
        drawRect(m(NEGRO, 0.8f), Offset(maq.right - 3f * u, vitrina.top + 2f * u), Size(0.6f * u, 2.4f * u))
        for (b in 0 until 3) drawCircle(m(if (b == 0) AMARILLO else CHAPA, 0.85f), 0.6f * u, Offset(maq.right - 2.7f * u, vitrina.top + (7f + b * 2.4f) * u))
        drawRoundRect(m(NEGRO, if (oscuro) 0.85f else 0.7f), Offset(maq.left + 3f * u, maq.bottom - 9f * u), Size(maq.width - 6f * u, 4f * u), CornerRadius(1f * u))
        drawRect(m(CROMO, if (oscuro) 0.4f else 0.7f), Offset(maq.left + 1f * u, maq.bottom - 2.5f * u), Size(maq.width - 2f * u, 1.2f * u))

        // A la derecha, el tubo de cobre con su manómetro y la llave de paso, y el tubo de chapa.
        val xt = w - 4f * u
        drawLine(m(COBRE, if (oscuro) 0.5f else 0.75f), Offset(xt, 5.6f * u), Offset(xt, zocalo), 2f * u)
        drawLine(m(TUBO_OSC, if (oscuro) 0.5f else 0.55f), Offset(xt - 3f * u, 5.6f * u), Offset(xt - 3f * u, zocalo), 1.6f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.35f), Offset(xt - 0.5f * u, 5.6f * u), Offset(xt - 0.5f * u, zocalo), 0.4f * u)
        var ya = 14f * u; k = 0
        while (ya < zocalo && k < 10) { drawRect(m(TUBO_OSC, 0.7f), Offset(xt - 4.5f * u, ya), Size(6f * u, 1f * u)); ya += 20f * u; k++ }
        val mano = Offset(w - 13f * u, h * 0.36f)
        drawLine(m(COBRE, if (oscuro) 0.5f else 0.75f), Offset(mano.x, mano.y), Offset(xt, mano.y), 1.2f * u)
        manometro(mano, 5f * u, m(TUBO_OSC, 0.9f), if (oscuro) Color(0xFF0B1A0F) else m(CREMA, 0.98f), if (oscuro) e.acento else NARANJA, 0.4f)
        val llave = Offset(w - 13f * u, h * 0.6f)
        val nar = m(NARANJA, if (oscuro) 0.6f else 0.85f)
        drawLine(m(COBRE, if (oscuro) 0.5f else 0.75f), Offset(llave.x, llave.y), Offset(xt, llave.y), 1.2f * u)
        drawCircle(nar, 3.4f * u, llave, style = Stroke(0.9f * u))
        drawLine(nar, Offset(llave.x - 3.4f * u, llave.y), Offset(llave.x + 3.4f * u, llave.y), 0.6f * u)
        drawLine(nar, Offset(llave.x, llave.y - 3.4f * u), Offset(llave.x, llave.y + 3.4f * u), 0.6f * u)
        drawCircle(nar, 0.9f * u, llave)
        // El piloto ámbar junto a la llave.
        drawCircle(e.secundario.copy(alpha = if (oscuro) 0.3f else 0.2f), 2.6f * u, Offset(w - 13f * u, h * 0.6f + 7f * u))
        drawCircle(e.secundario, 0.8f * u, Offset(w - 13f * u, h * 0.6f + 7f * u))

        // La mesa larga del comedor: el tablero de fórmica con su canto cromado, las patas de tubo y los taburetes.
        val ym = h - 9f * u
        listOf(0.3f, 0.43f, 0.57f, 0.7f).forEach { f ->
            val sx = w * f
            drawLine(m(CROMO, if (oscuro) 0.4f else 0.7f), Offset(sx, ym + 4f * u), Offset(sx, h - 1.5f * u), 0.7f * u)
            drawRoundRect(m(NARANJA, if (oscuro) 0.55f else 0.8f), Offset(sx - 2.6f * u, ym + 3f * u), Size(5.2f * u, 1.6f * u), CornerRadius(0.8f * u))
            drawLine(m(CROMO, if (oscuro) 0.4f else 0.7f), Offset(sx - 2f * u, h - 1.2f * u), Offset(sx + 2f * u, h - 1.2f * u), 0.6f * u)
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(w * 0.17f, ym + 2.4f * u), Size(w * 0.66f, 0.8f * u))
        drawRect(m(CHAPA, if (oscuro) 0.65f else 0.8f), Offset(w * 0.15f, ym), Size(w * 0.7f, 2.4f * u))
        drawLine(m(CROMO, if (oscuro) 0.55f else 0.9f), Offset(w * 0.15f, ym + 2.2f * u), Offset(w * 0.85f, ym + 2.2f * u), 0.5f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(w * 0.15f, ym + 0.3f * u), Offset(w * 0.85f, ym + 0.3f * u), 0.4f * u)
        listOf(0.17f, 0.83f).forEach { f -> drawLine(m(CROMO, if (oscuro) 0.45f else 0.75f), Offset(w * f, ym + 2.4f * u), Offset(w * f, h - 1f * u), 0.9f * u) }
        // Sobre la mesa: la botella y el vaso.
        drawRoundRect(m(NARANJA, if (oscuro) 0.6f else 0.85f), Offset(w * 0.4f - 0.9f * u, ym - 4f * u), Size(1.8f * u, 4f * u), CornerRadius(0.6f * u))
        drawRect(m(NEGRO, 0.6f), Offset(w * 0.4f - 0.4f * u, ym - 4.8f * u), Size(0.8f * u, 0.9f * u))
        drawRect(m(CROMO, if (oscuro) 0.45f else 0.8f), Offset(w * 0.6f - 1f * u, ym - 2.6f * u), Size(2f * u, 2.6f * u))
        // La rejilla del piso.
        drawRect(m(TUBO_OSC, if (oscuro) 0.5f else 0.45f), Offset(0f, h - 1f * u), Size(w, 1f * u))
    }

    // ---------------------------------------------------------------- Mapas

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        // La caja de la terminal: el plástico crema (de noche, la chapa oscura), su contorno y los remaches.
        val caja = Rect(1f * u, 1f * u, w - 1f * u, h - 1f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(caja.left + 0.8f * u, caja.top + 1f * u), caja.size, CornerRadius(6f * u))
        drawRoundRect(if (oscuro) m(TUBO_OSC, 0.55f) else m(MURO, 0.95f), caja.topLeft, caja.size, CornerRadius(6f * u))
        drawRoundRect(m(TUBO_OSC, if (oscuro) 0.8f else 0.5f), caja.topLeft, caja.size, CornerRadius(6f * u), style = Stroke(0.7f * u))
        listOf(Offset(caja.left + 3f * u, caja.top + 3f * u), Offset(caja.right - 3f * u, caja.top + 3f * u), Offset(caja.left + 3f * u, caja.bottom - 3f * u), Offset(caja.right - 3f * u, caja.bottom - 3f * u)).forEach { p ->
            drawCircle(m(TUBO_OSC, 0.7f), 0.9f * u, p); drawCircle(Color.White.copy(alpha = if (oscuro) 0.1f else 0.5f), 0.35f * u, Offset(p.x - 0.3f * u, p.y - 0.3f * u))
        }
        // La pantalla: el bisel hundido y el vidrio, tenue (el mapa va encima).
        val pant = Rect(7f * u, 5f * u, w - 7f * u, h - 9f * u)
        drawRoundRect(m(NEGRO, if (oscuro) 0.9f else 0.55f), Offset(pant.left - 1.4f * u, pant.top - 1.4f * u), Size(pant.width + 2.8f * u, pant.height + 2.8f * u), CornerRadius(5f * u))
        drawRoundRect(if (oscuro) mezcla(e.fondo, PANTALLA, 0.8f) else mezcla(e.fondo, CHAPA, 0.08f), pant.topLeft, pant.size, CornerRadius(4f * u))
        val pantalla = Path().apply { addRoundRect(RoundRect(pant, CornerRadius(4f * u))) }
        clipPath(pantalla) {
            // El plano del refugio: los tres pasillos, el pozo central, las salas y la puerta engranada.
            val trazo = if (oscuro) e.acento.copy(alpha = 0.22f) else mezcla(e.fondo, CHAPA, 0.3f)
            val plano = Path()
            val pasillos = listOf(0.3f, 0.55f, 0.8f)
            pasillos.forEach { f -> val py = pant.top + pant.height * f; plano.moveTo(pant.left + pant.width * 0.08f, py); plano.lineTo(pant.right - pant.width * 0.06f, py) }
            plano.moveTo(pant.center.x, pant.top + pant.height * 0.12f); plano.lineTo(pant.center.x, pant.top + pant.height * 0.8f)
            val az = Random(e.semilla.toLong())
            pasillos.forEachIndexed { i, f ->
                val py = pant.top + pant.height * f
                var x = pant.left + pant.width * (0.1f + 0.05f * az.nextFloat()); var j = 0
                while (x < pant.right - pant.width * 0.12f && j < 12) {
                    val sw = (8f + 8f * az.nextFloat()) * u; val sh = (5f + 3f * az.nextFloat()) * u
                    val arriba = (j + i) % 2 == 0
                    if (abs(x + sw / 2 - pant.center.x) > sw / 2 + 2f * u)
                        plano.addRect(Rect(x, if (arriba) py - sh else py, x + sw, if (arriba) py else py + sh))
                    x += sw + (3f + 6f * az.nextFloat()) * u; j++
                }
            }
            drawPath(plano, trazo, style = Stroke(0.5f * u))
            val puerta = Offset(pant.left + pant.width * 0.05f, pant.top + pant.height * 0.3f)
            drawPath(engrane(puerta, 3.4f * u, 10), trazo, style = Stroke(0.5f * u))
            // Las líneas de barrido y el reflejo del vidrio.
            val barrido = Path()
            var y = pant.top; var k = 0
            while (y < pant.bottom && k < 120) { barrido.moveTo(pant.left, y); barrido.lineTo(pant.right, y); y += 2.4f * u; k++ }
            drawPath(barrido, (if (oscuro) e.acento else Color.Black).copy(alpha = if (oscuro) 0.035f else 0.025f), style = Stroke(0.4f * u))
            drawPath(Path().apply { moveTo(pant.left, pant.top + 10f * u); lineTo(pant.left + 10f * u, pant.top); lineTo(pant.left + 16f * u, pant.top); lineTo(pant.left, pant.top + 16f * u); close() },
                Color.White.copy(alpha = if (oscuro) 0.04f else 0.25f))
        }
        // Abajo: la rejilla del altavoz a la izquierda, el teclado, la placa con el engrane y las perillas con sus pilotos.
        val yb = h - 5f * u
        for (j in 0 until 3) drawLine(m(NEGRO, if (oscuro) 0.8f else 0.4f), Offset(caja.left + 3.5f * u, pant.top + (8f + j * 2.2f) * u), Offset(caja.left + 5.5f * u, pant.top + (8f + j * 2.2f) * u), 0.6f * u, StrokeCap.Round)
        for (j in 0 until 8) drawRoundRect(m(if (j == 7) NARANJA else CREMA, if (oscuro) 0.4f else 0.95f), Offset(10f * u + j * 3.4f * u, yb - 1.4f * u), Size(2.8f * u, 2.6f * u), CornerRadius(0.5f * u))
        for (j in 0 until 8) drawRoundRect(m(TUBO_OSC, 0.6f), Offset(10f * u + j * 3.4f * u, yb - 1.4f * u), Size(2.8f * u, 2.6f * u), CornerRadius(0.5f * u), style = Stroke(0.3f * u))
        val placa = Rect(w / 2 - 7f * u, yb - 1.6f * u, w / 2 + 7f * u, yb + 1.8f * u)
        drawRect(m(AMARILLO, if (oscuro) 0.6f else 0.85f), placa.topLeft, placa.size)
        drawPath(engrane(Offset(placa.left + 2f * u, placa.center.y), 1.3f * u, 8), m(NEGRO, 0.8f))
        drawLine(m(NEGRO, 0.6f), Offset(placa.left + 4.5f * u, placa.center.y), Offset(placa.right - 1.5f * u, placa.center.y), 0.6f * u)
        listOf(w - 22f * u, w - 15f * u).forEachIndexed { j, x ->
            drawCircle(m(TUBO_OSC, 0.9f), 2.2f * u, Offset(x, yb))
            drawCircle(m(CROMO, if (oscuro) 0.5f else 0.85f), 1.5f * u, Offset(x, yb))
            drawLine(m(NEGRO, 0.8f), Offset(x, yb), Offset(x + (if (j == 0) 1f else -0.6f) * u, yb - 1.2f * u), 0.4f * u)
        }
        val piloto = Offset(w - 9f * u, yb)
        drawCircle(e.secundario.copy(alpha = if (oscuro) 0.3f else 0.2f), 2.6f * u, piloto)
        drawCircle(e.secundario, 0.9f * u, piloto)
        // El piloto de encendido, en el costado derecho de la caja.
        val encendido = Offset(caja.right - 3f * u, pant.top + 8f * u)
        drawCircle((if (oscuro) e.acento else CHAPA_LUZ).copy(alpha = 0.3f), 2f * u, encendido)
        drawCircle(if (oscuro) e.acento else CHAPA_LUZ, 0.7f * u, encendido)
    }

    // ---------------------------------------------------------------- Otras vidas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u(); val d = density
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val centros = centrosMedallas()
        val paso = (w - 20f * d) / 5f
        val my = h / 2 - 10f * d
        // El muro del dormitorio y el tubo de arriba con su cinta de aviso.
        drawRect(m(MURO, if (oscuro) 0.1f else 0.25f), Offset.Zero, size)
        drawRect(m(TUBO, if (oscuro) 0.45f else 0.55f), Offset.Zero, Size(w, 2.6f * u))
        cintaAviso(Rect(0f, 2.6f * u, w, 3.8f * u), m(AMARILLO, if (oscuro) 0.5f else 0.75f), m(NEGRO, if (oscuro) 0.8f else 0.7f))
        val piso = h - 5f * u
        centros.forEach { cx ->
            val pw = minOf(paso * 0.92f, 62f * d)
            val cap = Rect(cx - pw / 2, 5.5f * u, cx + pw / 2, piso)
            // La cápsula: la chapa verde azulada de esquinas redondas, con su bisel.
            drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(cap.left + 0.8f * u, cap.top + 0.8f * u), cap.size, CornerRadius(4f * u))
            drawRoundRect(m(CHAPA, if (oscuro) 0.4f else 0.5f), cap.topLeft, cap.size, CornerRadius(4f * u))
            drawRoundRect(m(CHAPA_LUZ, if (oscuro) 0.35f else 0.6f), Offset(cap.left + 0.8f * u, cap.top + 0.8f * u), Size(cap.width - 1.6f * u, cap.height - 1.6f * u), CornerRadius(3.4f * u), style = Stroke(0.5f * u))
            // La rejilla de ventilación arriba y el piloto ámbar.
            val anilloR = minOf(26f * d, pw / 2 - 2f * d)
            val rejTop = cap.top + 2.5f * u
            var ry = rejTop; var k = 0
            while (ry < my - anilloR - 2f * u && k < 4) { drawLine(m(CHAPA_OSC, if (oscuro) 0.8f else 0.7f), Offset(cx - pw * 0.22f, ry), Offset(cx + pw * 0.14f, ry), 0.8f * u, StrokeCap.Round); ry += 2.2f * u; k++ }
            val led = Offset(cap.right - 4f * u, cap.top + 3.6f * u)
            drawCircle(e.secundario.copy(alpha = if (oscuro) 0.3f else 0.2f), 2.4f * u, led)
            drawCircle(e.secundario, 0.8f * u, led)
            // El ojo de buey: el vidrio oscuro (tenue), el aro de acero y sus ocho pernos.
            val oc = Offset(cx, my)
            drawCircle(if (oscuro) mezcla(e.fondo, PANTALLA, 0.8f) else mezcla(e.fondo, CHAPA_OSC, 0.12f), anilloR, oc)
            if (oscuro) drawCircle(e.acento.copy(alpha = 0.06f), anilloR, oc)
            drawCircle(m(TUBO, if (oscuro) 0.6f else 0.8f), anilloR, oc, style = Stroke(2.6f * d))
            drawCircle(Color.White.copy(alpha = if (oscuro) 0.06f else 0.3f), anilloR + 0.6f * d, oc, style = Stroke(0.6f * d))
            for (j in 0 until 8) {
                val a = j * Math.PI / 4 + Math.PI / 8
                drawCircle(m(TUBO_OSC, 0.9f), 0.9f * d, Offset(oc.x + anilloR * cos(a).toFloat(), oc.y + anilloR * sin(a).toFloat()))
            }
            // La plaquita del año, con un fondo casi del color de la tarjeta para que el año se lea.
            val py = my + 27f * d
            if (py + 16f * d < piso) {
                val pr = Rect(cx - minOf(18f * d, pw * 0.4f), py, cx + minOf(18f * d, pw * 0.4f), py + 16f * d)
                drawRoundRect(if (oscuro) mezcla(e.fondo, PANTALLA, 0.9f) else mezcla(e.fondo, CREMA, 0.9f), pr.topLeft, pr.size, CornerRadius(2f * d))
                drawRoundRect(m(AMARILLO, if (oscuro) 0.45f else 0.8f), pr.topLeft, pr.size, CornerRadius(2f * d), style = Stroke(0.8f * d))
            }
        }
        // El piso de rejilla con su franja amarilla.
        drawRect(m(TUBO_OSC, if (oscuro) 0.5f else 0.45f), Offset(0f, piso), Size(w, h - piso))
        drawRect(m(AMARILLO, if (oscuro) 0.45f else 0.7f), Offset(0f, piso), Size(w, 0.8f * u))
        val rejilla = Path()
        var gx = 0f; var j = 0
        while (gx < w && j < 200) { rejilla.moveTo(gx, piso + 1f * u); rejilla.lineTo(gx, h); gx += 3f * u; j++ }
        drawPath(rejilla, m(NEGRO, if (oscuro) 0.7f else 0.4f), style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.4f
        val g = 1.6f * density
        when (id) {
            "unesco" -> {
                // La bóveda: la cúpula sobre su basamento, la puerta engranada al centro y la antena con su estrella.
                drawLine(e.c1, Offset(c.x - r * 1.4f, c.y + r * 0.88f), Offset(c.x + r * 1.4f, c.y + r * 0.88f), g * 1.3f, StrokeCap.Round)
                val base = Rect(c.x - r * 1.15f, c.y + r * 0.42f, c.x + r * 1.15f, c.y + r * 0.88f)
                drawRect(e.c1, base.topLeft, base.size)
                for (k in 1..4) { val x = base.left + base.width * k / 5; drawLine(e.fondo, Offset(x, base.top + base.height * 0.3f), Offset(x, base.bottom - base.height * 0.25f), g * 0.6f) }
                val cupula = Rect(c.x - r * 0.95f, c.y - r * 0.5f, c.x + r * 0.95f, c.y + r * 1.34f)
                drawArc(e.c1.copy(alpha = 0.25f), 180f, 180f, true, cupula.topLeft, cupula.size)
                drawArc(e.c1, 180f, 180f, false, cupula.topLeft, cupula.size, style = Stroke(g * 1.2f))
                drawArc(e.c1, 200f, 140f, false, Offset(cupula.left + r * 0.3f, cupula.top + r * 0.25f), Size(cupula.width - r * 0.6f, cupula.height - r * 0.5f), style = Stroke(g * 0.6f))
                val pc = Offset(c.x, c.y + r * 0.12f)
                drawPath(engrane(pc, r * 0.32f, 10), e.c2)
                drawCircle(e.fondo, r * 0.14f, pc)
                drawLine(e.c1, Offset(c.x, cupula.top), Offset(c.x, c.y - r * 0.82f), g * 0.8f)
                drawPath(estrella(Offset(c.x, c.y - r * 0.88f), r * 0.16f, 5, 0.45f), e.c2)
            }
            "rascar" -> {
                // La tarjeta perforada con la esquina cortada: la capa para rascar y, donde ya se raspó, las perforaciones.
                val t0 = Rect(c.x - r * 1.4f, c.y - r * 0.78f, c.x + r * 1.4f, c.y + r * 0.78f)
                val corte = r * 0.35f
                val tarjeta = Path().apply { moveTo(t0.left + corte, t0.top); lineTo(t0.right, t0.top); lineTo(t0.right, t0.bottom); lineTo(t0.left, t0.bottom); lineTo(t0.left, t0.top + corte); close() }
                drawPath(tarjeta, e.c2.copy(alpha = 0.55f))
                val raspado = Path().apply {
                    moveTo(t0.left + t0.width * 0.42f, t0.top); lineTo(t0.right, t0.top); lineTo(t0.right, t0.bottom)
                    lineTo(t0.left + t0.width * 0.3f, t0.bottom); lineTo(t0.left + t0.width * 0.4f, t0.top + t0.height * 0.7f)
                    lineTo(t0.left + t0.width * 0.33f, t0.top + t0.height * 0.45f); lineTo(t0.left + t0.width * 0.44f, t0.top + t0.height * 0.22f); close()
                }
                drawPath(raspado, e.fondo)
                clipPath(raspado) {
                    for (f in 0 until 3) for (k in 0 until 10) {
                        if ((f * 3 + k * 7) % 4 == 0) continue
                        val x = t0.left + t0.width * (0.08f + k * 0.088f); val y = t0.top + t0.height * (0.25f + f * 0.25f)
                        drawRect(e.c1, Offset(x - r * 0.05f, y - r * 0.09f), Size(r * 0.1f, r * 0.18f))
                    }
                }
                drawPath(tarjeta, e.c1, style = Stroke(g))
            }
            else -> {
                // Tus lugares: tres chinchetas de estrella con su aguja y su sombra.
                val pins = listOf(Offset(c.x - r * 0.8f, c.y + r * 0.2f), Offset(c.x + r * 0.1f, c.y - r * 0.35f), Offset(c.x + r * 0.9f, c.y + r * 0.3f))
                pins.forEachIndexed { k, p ->
                    val rr = r * (if (k == 1) 0.36f else 0.3f)
                    drawLine(e.c2, p, Offset(p.x - rr * 0.5f, p.y + rr * 1.3f), g * 0.7f, StrokeCap.Round)
                    drawOval(Color.Black.copy(alpha = 0.18f), Offset(p.x - rr * 0.9f, p.y + rr * 1.15f), Size(rr * 0.8f, rr * 0.3f))
                    drawPath(estrella(p, rr, 5, 0.45f), e.c1)
                    drawCircle(e.fondo, rr * 0.2f, p)
                }
            }
        }
    }

    // ---------------------------------------------------------------- Notas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        fun m(c: Color, k: Float) = mezcla(e.fondo, t(c, oscuro), k)
        val marco = 3f * u
        // El marco de chapa verde azulado con sus remaches.
        drawRoundRect(m(CHAPA, if (oscuro) 0.55f else 0.8f), Offset.Zero, size, CornerRadius(3f * u))
        val remaches = Path()
        var x = 6f * u; var k = 0
        while (x < w - 3f * u && k < 80) { listOf(marco / 2, h - marco / 2).forEach { y -> remaches.addOval(Rect(x - 0.5f * u, y - 0.5f * u, x + 0.5f * u, y + 0.5f * u)) }; x += 10f * u; k++ }
        drawPath(remaches, m(CHAPA_LUZ, if (oscuro) 0.7f else 0.9f))
        // El tablón: crema de día; de noche, el verde oscuro de la pantalla. Encima, el sol de la propaganda optimista.
        val tablon = Rect(marco, marco, w - marco, h - marco)
        drawRect(if (oscuro) mezcla(e.fondo, Color(0xFF0E2416), 0.9f) else m(CREMA, 0.95f), tablon.topLeft, tablon.size)
        clipRect(tablon.left, tablon.top, tablon.right, tablon.bottom) {
            val sc = Offset(w / 2, h * 0.46f)
            val rayos = Path()
            val n = 18
            for (j in 0 until n) {
                val a0 = j * 2 * Math.PI / n; val a1 = a0 + Math.PI / n
                rayos.moveTo(sc.x, sc.y)
                rayos.lineTo(sc.x + w * cos(a0).toFloat(), sc.y + w * sin(a0).toFloat())
                rayos.lineTo(sc.x + w * cos(a1).toFloat(), sc.y + w * sin(a1).toFloat())
                rayos.close()
            }
            drawPath(rayos, if (oscuro) e.acento.copy(alpha = 0.05f) else m(AMARILLO, 0.18f))
            drawCircle(if (oscuro) e.acento.copy(alpha = 0.06f) else m(AMARILLO, 0.3f), 14f * u, sc)
        }
        // La cinta de aviso arriba del tablón y la placa con el átomo en el marco de abajo.
        cintaAviso(Rect(tablon.left, tablon.top, tablon.right, tablon.top + 1.6f * u), m(AMARILLO, if (oscuro) 0.5f else 0.8f), m(NEGRO, if (oscuro) 0.8f else 0.7f))
        val placa = Rect(w / 2 - 8f * u, h - 4.6f * u, w / 2 + 8f * u, h - 0.6f * u)
        drawRoundRect(m(AMARILLO, if (oscuro) 0.6f else 0.85f), placa.topLeft, placa.size, CornerRadius(1f * u))
        atomo(Offset(placa.left + 3f * u, placa.center.y), 1.6f * u, m(NEGRO, 0.8f), 0.3f * u)
        drawLine(m(NEGRO, 0.6f), Offset(placa.left + 6f * u, placa.center.y), Offset(placa.right - 2f * u, placa.center.y), 0.5f * u)
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect; val u = minOf(r.width, r.height) / 100f
        val papel = if (oscuro) AVISO_NOCHE else AVISO
        val radio = CornerRadius(5f * u)
        // La sombra, el papel y su borde.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.16f), Offset(r.left + 1.6f * u, r.top + 2.2f * u), r.size, radio)
        drawRoundRect(papel, r.topLeft, r.size, radio)
        // La franja de arriba, del color de su tipo; el recordatorio lleva la cinta de aviso.
        val franja = Rect(r.left, r.top, r.right, r.top + r.height * 0.1f)
        clipPath(Path().apply { addRoundRect(RoundRect(r, radio)) }) {
            when (e.tipo) {
                "idea" -> drawRect(t(AMARILLO, oscuro), franja.topLeft, franja.size)
                "pensamiento" -> drawRect(t(CHAPA_LUZ, oscuro), franja.topLeft, franja.size)
                "proposito" -> drawRect(t(NARANJA, oscuro), franja.topLeft, franja.size)
                "recordatorio" -> cintaAviso(franja, t(AMARILLO, oscuro), NEGRO)
                else -> drawRect(t(TUBO, oscuro), franja.topLeft, franja.size)
            }
        }
        // El engrane del refugio, chiquito, en la esquina de abajo.
        drawPath(engrane(Offset(r.right - 8f * u, r.bottom - 7f * u), 4f * u, 8), (if (oscuro) e.acento else CHAPA).copy(alpha = 0.3f))
        drawRoundRect(if (e.fijada) e.acento else (if (oscuro) e.acento.copy(alpha = 0.25f) else TUBO_OSC.copy(alpha = 0.3f)), r.topLeft, r.size, radio,
            style = Stroke(if (e.fijada) 1.6f * u else 0.7f * u))
        // La chincheta de estrella: amarilla y más grande si la nota está fijada.
        val p = Offset(r.center.x, r.top + r.height * 0.05f)
        val rr = if (e.fijada) 8.5f * u else 6.5f * u
        if (e.fijada && oscuro) drawCircle(AMARILLO.copy(alpha = 0.18f), rr * 1.8f, p)
        drawPath(estrella(Offset(p.x + 0.8f * u, p.y + 1.2f * u), rr, 5, 0.45f), Color.Black.copy(alpha = 0.3f))
        drawPath(estrella(p, rr, 5, 0.45f), if (e.fijada) AMARILLO else t(NARANJA, oscuro))
        drawCircle(Color.White.copy(alpha = 0.6f), rr * 0.18f, p)
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFF9CF5B0) else Color(0xFF1F2A2A)
}
