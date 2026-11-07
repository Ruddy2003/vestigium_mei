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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Fantasía
// =====================================================================

/**
 * Horror cósmico: la librería de la casa antigua, de roble oscuro, contra un papel tapiz de rombos. Las puertas de vidrio
 * están abiertas junto a los costados: la izquierda entreabierta, la derecha con un vidrio roto y su cerradura. Arriba, la
 * cornisa de molduras con el friso de paneles y una concha tallada; encima, un reloj de arena, una vela en su palmatoria de
 * latón y una telaraña en la esquina. Abajo, el zócalo con sus patas de bola y el hueco oscuro debajo. El mes va en un papel
 * clavado con dos tachuelas. Cada entrada es un libro de tela o de cuero con nervios y filetes dorados, el día en una
 * etiqueta de papel, el mes en dorado y un florón (de noche, una luna); asoman papelitos de notas, más cuanto más escribiste,
 * y algunos van atados con cordel. De noche se enciende la vela y, en el hueco bajo el mueble, se abren dos ojos. Distinto del
 * Ideario, que es el archivo de un pueblo de la costa con su quinqué y sus cajas de cartón.
 */
object EstanteArcano : DisenoBiblioteca {
    private val ROBLE = Color(0xFF3B2A1C)
    private val ROBLE_LUZ = Color(0xFF5E4630)
    private val ROBLE_OSC = Color(0xFF22170F)
    private val ROBLE_VETA = Color(0xFF1A110A)
    private val TAPIZ = Color(0xFF2F2B26)
    private val TAPIZ_LINEA = Color(0xFF4A4238)
    private val VIDRIO = Color(0xFF9FB4B0)
    private val LATON = Color(0xFFB8963E)
    private val LATON_OSC = Color(0xFF6E5622)
    private val PAPEL = Color(0xFFE8DFC8)
    private val PAPEL_VIEJO = Color(0xFFD6C7A0)
    private val TE = Color(0xFF9C7A48)
    private val CERA = Color(0xFFEDE3C8)
    private val ARENA = Color(0xFFCDB27A)
    private val LLAMA = Color(0xFFF4C66A)
    private val OJOS = Color(0xFFD8E06A)
    private val TINTA = Color(0xFF231A12)
    private val ORO = Color(0xFFC9A44A)
    private val CORDEL = Color(0xFFB09870)
    private val TELARANA = Color(0xFFDDD8CC)
    private val PISO = Color(0xFF2A1E15)
    /** Las tapas: burdeos, verde, azul de noche, cuero, ciruela y tela de oliva. */
    private val TELAS = listOf(Color(0xFF4A2A2A), Color(0xFF2E3B2E), Color(0xFF2A3346), Color(0xFF5A3E22), Color(0xFF3E2E40), Color(0xFF5A5034))
    private val NOCHE = Color(0xFF0D0F18)
    private val LUNA = Color(0xFFE6DFC4)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la penumbra de la casa. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0A0806), 0.32f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.11f
    override val lado = 0.08f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = Letra.castoro

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 40).coerceAtMost(20)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.74f + 0.22f * azar(semilla, 1)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo: tablas de roble a lo alto, en sombra.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(ROBLE_OSC), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(ROBLE_VETA).copy(alpha = 0.6f), 141, densidad = 0.6f, vertical = true, grosor = 0.7f)
        var x = cuerpo.left + 30 * u
        while (x < cuerpo.right) { drawLine(Color.Black.copy(alpha = 0.4f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 0.9f * u); x += 30 * u }
        // Cada repisa: la sombra honda de la tabla de arriba y de los costados.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.45f else 0.35f), r.topLeft, Size(r.width, 8 * u))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.left, r.top + 8 * u), Size(r.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.22f), r.topLeft, Size(5 * u, r.height))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.right - 5 * u, r.top), Size(5 * u, r.height))
        }
        // Los costados: el montante de roble y, por fuera, la puerta de vidrio abierta.
        listOf(d.izquierda to true, d.derecha to false).forEach { (r, izq) ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(ROBLE_VETA).copy(alpha = 0.5f), if (izq) 5 else 6, densidad = 1f, vertical = true, grosor = 0.7f)
            bisel(r, Color.White.copy(alpha = 0.08f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
            puerta(r, izq, rota = !izq, oscuro = oscuro, noche = noche)
        }
        // Las tablas, con su canto moldurado.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(ROBLE), r.topLeft, r.size)
            vetas(r, c(ROBLE_VETA).copy(alpha = 0.5f), 150 + k, densidad = 0.7f, grosor = 0.6f)
            drawRect(c(ROBLE_LUZ), r.topLeft, Size(r.width, 2 * u))
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(r.left, r.top + r.height * 0.62f), Offset(r.right, r.top + r.height * 0.62f), 0.8f * u)
            drawLine(c(ROBLE_LUZ).copy(alpha = 0.6f), Offset(r.left, r.top + r.height * 0.68f), Offset(r.right, r.top + r.height * 0.68f), 0.6f * u)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom), Size(r.width, 4 * u))
        }
        // El rótulo de cada mes: un papel viejo clavado con dos tachuelas, con su cerco de té.
        d.placas.forEach { p ->
            val r = p.r
            rotate((azar(p.texto.hashCode(), 3) - 0.5f) * 3f, r.center) {
                drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1.4f * u, r.top + 1.4f * u), r.size)
                drawRect(c(PAPEL_VIEJO), r.topLeft, r.size)
                drawCircle(c(TE).copy(alpha = 0.25f), r.height * 0.42f, Offset(r.right - r.height * 0.7f, r.center.y + r.height * 0.1f), style = Stroke(1.2f * u))
                drawRect(c(TE).copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(0.6f * u))
                listOf(r.left + 3.5f * u, r.right - 3.5f * u).forEach { tx ->
                    drawCircle(c(LATON), 1.6f * u, Offset(tx, r.center.y))
                    drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(tx - 0.5f * u, r.center.y - 0.5f * u))
                }
                textoEn(medidor, p.texto, Rect(r.left + 7 * u, r.top, r.right - 7 * u, r.bottom), letra, c(TINTA), 10f, minimo = 5f)
            }
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo moldurado, las patas de bola, el hueco oscuro debajo y el piso de tablas.
        val b = d.base
        val zocalo = Rect(b.left, b.top, b.right, b.top + b.height * 0.42f)
        val piso = zocalo.bottom
        drawRect(c(PISO), Offset(0f, piso), Size(w, h - piso))
        var py = piso + 5 * u
        while (py < h) { drawLine(Color.Black.copy(alpha = 0.5f), Offset(0f, py), Offset(w, py), 0.8f * u); py += 7 * u }
        val hueco = Rect(b.left + b.width * 0.12f, piso, b.right - b.width * 0.12f, piso + (h - piso) * 0.55f)
        drawRect(Color.Black.copy(alpha = 0.7f), hueco.topLeft, hueco.size)
        drawRect(c(ROBLE), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(ROBLE_VETA).copy(alpha = 0.5f), 161, densidad = 0.8f, grosor = 0.6f)
        drawRect(c(ROBLE_LUZ), zocalo.topLeft, Size(zocalo.width, 2.4f * u))
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(zocalo.left, zocalo.top + zocalo.height * 0.4f), Offset(zocalo.right, zocalo.top + zocalo.height * 0.4f), 0.8f * u)
        listOf(b.left + b.width * 0.02f, b.right - b.width * 0.12f).forEach { fx ->
            val pata = Rect(fx, piso - 2 * u, fx + b.width * 0.1f, piso + (h - piso) * 0.6f)
            drawOval(c(ROBLE_LUZ), pata.topLeft, pata.size)
            drawOval(Color.Black.copy(alpha = 0.4f), pata.topLeft, pata.size, style = Stroke(0.8f * u))
        }
        // De noche, dos ojos en lo oscuro (el segundo y último degradado del mueble; el primero es la vela).
        if (noche) {
            val ojo = Offset(hueco.left + hueco.width * 0.62f, hueco.center.y)
            mancha(ojo, 12 * u, OJOS.copy(alpha = 0.22f))
            listOf(-1f, 1f).forEach { s ->
                drawOval(OJOS, Offset(ojo.x + s * 4 * u - 2 * u, ojo.y - 1.2f * u), Size(4 * u, 2.4f * u))
                drawLine(Color.Black, Offset(ojo.x + s * 4 * u, ojo.y - 1.2f * u), Offset(ojo.x + s * 4 * u, ojo.y + 1.2f * u), 0.9f * u)
            }
        }
    }

    /**
     * Una puerta de vidrio abierta, vista en escorzo junto al costado [r]: la bisagra hacia adentro y el canto afuera; [rota]
     * si tiene un vidrio roto (la otra queda entreabierta, menos girada).
     */
    private fun DrawScope.puerta(r: Rect, izq: Boolean, rota: Boolean, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val bisagra = if (izq) r.left + r.width * 0.62f else r.right - r.width * 0.62f
        val canto = if (izq) r.left else r.right
        val caida = (if (rota) 9f else 4f) * u
        fun arriba(x: Float) = r.top + 2 * u + caida * ((x - canto) / (bisagra - canto))
        fun abajo(x: Float) = r.bottom - 2 * u - caida * ((x - canto) / (bisagra - canto))
        val hoja = Path().apply { moveTo(canto, arriba(canto)); lineTo(bisagra, arriba(bisagra)); lineTo(bisagra, abajo(bisagra)); lineTo(canto, abajo(canto)); close() }
        drawPath(hoja, c(ROBLE_LUZ))
        // El vidrio, dentro del marco, con sus travesaños y sus reflejos.
        val m = 3 * u
        val x0 = canto + (if (izq) m else -m)
        val x1 = bisagra + (if (izq) -m else m)
        val vidrio = Path().apply { moveTo(x0, arriba(x0) + m); lineTo(x1, arriba(x1) + m); lineTo(x1, abajo(x1) - m); lineTo(x0, abajo(x0) - m); close() }
        drawPath(vidrio, c(ROBLE_OSC))
        drawPath(vidrio, c(VIDRIO).copy(alpha = 0.22f))
        val paneles = (r.height / (70 * u)).toInt().coerceAtLeast(2)
        clipPath(vidrio) {
            for (j in 0 until paneles) {
                val yy = r.top + r.height * (j + 0.3f) / paneles
                drawLine(Color.White.copy(alpha = 0.14f), Offset(minOf(x0, x1), yy + 10 * u), Offset(maxOf(x0, x1), yy - 4 * u), 1.6f * u)
            }
            if (rota) {
                // El vidrio roto: el hueco sin vidrio y las grietas que salen de él.
                val mx = (x0 + x1) / 2
                val p = Offset(mx, arriba(mx) + (abajo(mx) - arriba(mx)) * (1.5f / paneles))
                val hueco = Path().apply { moveTo(p.x - 3 * u, p.y - 5 * u); lineTo(p.x + 4 * u, p.y - 2 * u); lineTo(p.x - 1 * u, p.y + 6 * u); close() }
                drawPath(hueco, Color.Black.copy(alpha = 0.6f))
                for (k in 0 until 6) {
                    val a = Math.toRadians(20.0 + k * 60.0)
                    drawLine(Color.White.copy(alpha = 0.45f), p, Offset(p.x + cos(a).toFloat() * 9 * u, p.y + sin(a).toFloat() * 13 * u), 0.6f * u)
                }
            }
        }
        for (j in 1 until paneles) {
            val f = j / paneles.toFloat()
            val y0 = arriba(x0) + m + (abajo(x0) - arriba(x0) - 2 * m) * f
            val y1 = arriba(x1) + m + (abajo(x1) - arriba(x1) - 2 * m) * f
            drawLine(c(ROBLE_LUZ), Offset(x0, y0), Offset(x1, y1), 2.4f * u)
        }
        // Las bisagras, el pomo y, en la rota, el escudo de la cerradura.
        for (f in listOf(0.12f, 0.88f)) drawRect(c(LATON), Offset(bisagra - 1.5f * u, r.top + r.height * f - 4 * u), Size(3 * u, 8 * u))
        val pomo = Offset(canto + (if (izq) 2.5f else -2.5f) * u, r.center.y)
        drawCircle(c(LATON), 2 * u, pomo)
        if (rota) {
            drawRoundRect(c(LATON), Offset(pomo.x - 1.5f * u, pomo.y + 4 * u), Size(3 * u, 6 * u), CornerRadius(1 * u))
            drawCircle(c(ROBLE_OSC), 0.7f * u, Offset(pomo.x, pomo.y + 6.2f * u))
        }
        drawPath(hoja, c(ROBLE_VETA), style = Stroke(0.8f * u))
    }

    /** La pared con su tapiz, la cornisa con la concha tallada, el reloj de arena, la vela (encendida de noche) y la telaraña. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // El papel tapiz de rombos.
        drawRect(c(TAPIZ), r.topLeft, r.size)
        var x = r.left - r.height
        while (x < r.right) {
            drawLine(c(TAPIZ_LINEA).copy(alpha = 0.5f), Offset(x, r.top), Offset(x + r.height, r.bottom), 0.6f * u)
            drawLine(c(TAPIZ_LINEA).copy(alpha = 0.5f), Offset(x + r.height, r.top), Offset(x, r.bottom), 0.6f * u)
            x += 18 * u
        }
        // La telaraña en la esquina de arriba.
        val radio = r.height * 0.4f
        for (k in 0..4) {
            val a = Math.toRadians(5.0 + k * 20.0)
            drawLine(c(TELARANA).copy(alpha = 0.4f), r.topLeft, Offset(r.left + cos(a).toFloat() * radio, r.top + sin(a).toFloat() * radio), 0.5f * u)
        }
        for (k in 1..3) {
            val rr = radio * k / 4f
            drawArc(c(TELARANA).copy(alpha = 0.35f), 5f, 80f, false, Offset(r.left - rr, r.top - rr), Size(rr * 2, rr * 2), style = Stroke(0.5f * u))
        }
        // La cornisa de molduras.
        val cornisa = r.top + r.height * 0.56f
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, cornisa - 2 * u), Size(r.width, 2 * u))
        drawRect(c(ROBLE_LUZ), Offset(r.left, cornisa), Size(r.width, r.height * 0.05f))
        drawRect(c(ROBLE), Offset(r.left + r.width * 0.01f, cornisa + r.height * 0.05f), Size(r.width * 0.98f, r.height * 0.06f))
        drawRect(c(ROBLE_OSC), Offset(r.left + r.width * 0.02f, cornisa + r.height * 0.11f), Size(r.width * 0.96f, r.height * 0.03f))
        // El friso: dos paneles y la concha tallada al centro.
        val friso = Rect(r.left + r.width * 0.02f, cornisa + r.height * 0.14f, r.right - r.width * 0.02f, r.bottom)
        drawRect(c(ROBLE), friso.topLeft, friso.size)
        vetas(friso, c(ROBLE_VETA).copy(alpha = 0.5f), 171, densidad = 0.8f, grosor = 0.7f)
        listOf(0.04f to 0.38f, 0.62f to 0.96f).forEach { (a, z) ->
            val pr = Rect(friso.left + friso.width * a, friso.top + friso.height * 0.2f, friso.left + friso.width * z, friso.bottom - friso.height * 0.25f)
            bisel(pr, Color.White.copy(alpha = 0.08f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
            drawRect(c(ROBLE_OSC), pr.topLeft, pr.size, style = Stroke(1f * u))
        }
        val cc = Offset(friso.center.x, friso.bottom - friso.height * 0.2f)
        val rc = friso.height * 0.6f
        drawArc(c(ROBLE_LUZ), 180f, 180f, true, Offset(cc.x - rc, cc.y - rc), Size(rc * 2, rc * 2))
        for (k in 1..6) {
            val a = Math.toRadians(180.0 + k * 180.0 / 7)
            drawLine(c(ROBLE_OSC), cc, Offset(cc.x + cos(a).toFloat() * rc, cc.y + sin(a).toFloat() * rc), 0.8f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
        // El reloj de arena.
        val rx = r.left + r.width * 0.26f
        val ar = r.height * 0.24f
        val reloj = Rect(rx - ar * 0.32f, cornisa - ar, rx + ar * 0.32f, cornisa)
        listOf(reloj.left - 1 * u, reloj.right + 1 * u).forEach { px -> drawLine(c(ROBLE_LUZ), Offset(px, reloj.top), Offset(px, reloj.bottom), 1.2f * u) }
        val ampollas = Path().apply {
            moveTo(reloj.left, reloj.top + 2.4f * u); lineTo(reloj.right, reloj.top + 2.4f * u); lineTo(rx + 0.8f * u, reloj.center.y)
            lineTo(reloj.right, reloj.bottom - 2.4f * u); lineTo(reloj.left, reloj.bottom - 2.4f * u); lineTo(rx - 0.8f * u, reloj.center.y); close()
        }
        drawPath(ampollas, c(VIDRIO).copy(alpha = 0.3f))
        val montonAbajo = Path().apply { moveTo(reloj.left + 1.5f * u, reloj.bottom - 2.4f * u); lineTo(rx, reloj.bottom - ar * 0.3f); lineTo(reloj.right - 1.5f * u, reloj.bottom - 2.4f * u); close() }
        drawPath(montonAbajo, c(ARENA))
        val restoArriba = Path().apply { moveTo(reloj.left + ar * 0.12f, reloj.top + ar * 0.3f); lineTo(reloj.right - ar * 0.12f, reloj.top + ar * 0.3f); lineTo(rx, reloj.center.y - 1 * u); close() }
        drawPath(restoArriba, c(ARENA))
        drawLine(c(ARENA), Offset(rx, reloj.center.y), Offset(rx, reloj.bottom - ar * 0.3f), 0.6f * u)
        drawPath(ampollas, c(VIDRIO).copy(alpha = 0.6f), style = Stroke(0.7f * u))
        drawRect(c(ROBLE_LUZ), Offset(reloj.left - 2.5f * u, reloj.top), Size(reloj.width + 5 * u, 2.4f * u))
        drawRect(c(ROBLE_LUZ), Offset(reloj.left - 2.5f * u, reloj.bottom - 2.4f * u), Size(reloj.width + 5 * u, 2.4f * u))
        // La vela en su palmatoria de latón.
        val vx = r.left + r.width * 0.72f
        drawCircle(c(LATON_OSC), 3 * u, Offset(vx + 13 * u, cornisa - 3 * u), style = Stroke(1.2f * u))
        drawOval(c(LATON), Offset(vx - 12 * u, cornisa - 3 * u), Size(24 * u, 3 * u))
        drawRect(c(LATON), Offset(vx - 4 * u, cornisa - 6 * u), Size(8 * u, 4 * u))
        drawRect(c(LATON_OSC), Offset(vx - 4 * u, cornisa - 6 * u), Size(8 * u, 1 * u))
        val altoVela = r.height * 0.3f
        val vela = Rect(vx - 3.5f * u, cornisa - 6 * u - altoVela, vx + 3.5f * u, cornisa - 6 * u)
        drawRect(c(CERA), vela.topLeft, vela.size)
        drawRect(Color.Black.copy(alpha = 0.14f), Offset(vela.right - 2 * u, vela.top), Size(2 * u, vela.height))
        drawRoundRect(c(CERA), Offset(vela.left - 1 * u, vela.top + 2 * u), Size(2 * u, altoVela * 0.4f), CornerRadius(1 * u))
        drawRoundRect(c(CERA), Offset(vela.right - 1 * u, vela.top + 1 * u), Size(2 * u, altoVela * 0.25f), CornerRadius(1 * u))
        drawOval(c(CERA), Offset(vela.left - 1.5f * u, vela.top - 1.2f * u), Size(vela.width + 3 * u, 2.4f * u))
        drawLine(c(TINTA), Offset(vx, vela.top), Offset(vx + 0.5f * u, vela.top - 3 * u), 0.9f * u)
        if (noche) {
            // La luz de la vela: el primer degradado del mueble.
            mancha(Offset(vx, vela.top - 6 * u), r.height * 0.7f, LLAMA.copy(alpha = 0.32f))
            val llama = Path().apply {
                moveTo(vx - 2.4f * u, vela.top - 2 * u)
                quadraticTo(vx - 2.8f * u, vela.top - 7 * u, vx + 0.4f * u, vela.top - 13 * u)
                quadraticTo(vx + 3 * u, vela.top - 7 * u, vx + 2.4f * u, vela.top - 2 * u)
                close()
            }
            drawPath(llama, LLAMA)
            drawOval(Color.White.copy(alpha = 0.8f), Offset(vx - 1 * u, vela.top - 7 * u), Size(2 * u, 4 * u))
        } else {
            // Un hilo de humo de la mecha apagada.
            val humo = Path().apply {
                moveTo(vx + 0.5f * u, vela.top - 3 * u)
                cubicTo(vx - 4 * u, vela.top - 8 * u, vx + 5 * u, vela.top - 12 * u, vx, vela.top - 18 * u)
            }
            drawPath(humo, Color.White.copy(alpha = 0.18f), style = Stroke(1f * u, cap = StrokeCap.Round))
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = TELAS[(v.semilla and 0x7fffffff) % TELAS.size]
        val tela = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // Los papelitos de notas que asoman por arriba: más cuanto más escribiste.
        val notas = (v.lleno * 4).toInt() + (if (azar(v.semilla, 3) > 0.5f) 1 else 0)
        val arriba = if (notas > 0) 6 * u else 0f
        for (j in 0 until notas) {
            val nx = w * (0.14f + 0.6f * azar(v.semilla, 10 + j))
            val nw = w * (0.14f + 0.12f * azar(v.semilla, 20 + j))
            val nh = arriba + 3 * u
            rotate((azar(v.semilla, 30 + j) - 0.5f) * 14f, Offset(nx + nw / 2, arriba + 2 * u)) {
                drawRect(c(if (j % 2 == 0) PAPEL else PAPEL_VIEJO), Offset(nx, arriba - nh * 0.55f), Size(nw, nh))
            }
        }
        // El libro, con su lomo redondeado (luz y sombra planas).
        val libro = Rect(0f, arriba, w, h)
        drawRoundRect(tela, libro.topLeft, libro.size, CornerRadius(1.5f * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.1f, libro.top), Size(w * 0.14f, libro.height))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.84f, libro.top), Size(w * 0.16f, libro.height))
        // Los nervios con sus filetes dorados.
        listOf(0.06f, 0.18f, 0.88f, 0.95f).forEach { f ->
            val ny = libro.top + libro.height * f
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, ny - 1.5f * u), Size(w, 3 * u))
            drawRect(Color.White.copy(alpha = 0.12f), Offset(0f, ny - 1.5f * u), Size(w, 1 * u))
            drawLine(c(ORO), Offset(w * 0.08f, ny - 2.4f * u), Offset(w * 0.92f, ny - 2.4f * u), 0.6f * u)
            drawLine(c(ORO), Offset(w * 0.08f, ny + 2.4f * u), Offset(w * 0.92f, ny + 2.4f * u), 0.6f * u)
        }
        // El florón dorado entre los nervios de arriba; de noche, una luna.
        val fc = Offset(w * 0.5f, libro.top + libro.height * 0.12f)
        val fr = minOf(w * 0.18f, libro.height * 0.035f)
        if (v.noche) {
            drawCircle(LUNA, fr, fc)
            drawCircle(tela, fr * 0.8f, Offset(fc.x + fr * 0.4f, fc.y - fr * 0.25f))
        } else {
            drawPath(estrella(fc, fr, 4, 0.35f), c(ORO))
        }
        // La etiqueta de papel con el día.
        val etiqueta = Rect(w * 0.14f, libro.top + libro.height * 0.22f, w * 0.86f, libro.top + libro.height * 0.42f)
        drawRect(c(PAPEL_VIEJO), etiqueta.topLeft, etiqueta.size)
        drawRect(c(TE).copy(alpha = 0.6f), etiqueta.topLeft, etiqueta.size, style = Stroke(0.6f * u))
        drawRect(c(TE).copy(alpha = 0.2f), Offset(etiqueta.left, etiqueta.bottom - etiqueta.height * 0.25f), Size(etiqueta.width * 0.6f, etiqueta.height * 0.25f))
        textoEn(medidor, v.dia, etiqueta.deflate(1.2f * u), letra, c(TINTA), 13f, peso = FontWeight.SemiBold)
        // El mes en dorado, a lo largo del lomo.
        textoEn(medidor, v.mes, Rect(w * 0.18f, libro.top + libro.height * 0.46f, w * 0.82f, libro.top + libro.height * 0.84f), letra, c(ORO), 8.5f, vertical = true, minimo = 4.5f)
        // Las esquinas gastadas.
        drawRect(Color.White.copy(alpha = 0.08f), Offset(0f, libro.top), Size(w, 1 * u))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h - 1.5f * u), Size(w, 1.5f * u))
        // Algunos van atados con cordel.
        if (azar(v.semilla, 7) > 0.6f) {
            val cy = libro.top + libro.height * 0.44f
            drawLine(c(CORDEL), Offset(0f, cy - 1.5f * u), Offset(w, cy + 1.5f * u), 1f * u)
            drawLine(c(CORDEL), Offset(0f, cy + 2.5f * u), Offset(w, cy - 0.5f * u), 1f * u)
            drawCircle(c(CORDEL), 1.6f * u, Offset(w * 0.5f, cy + 1 * u))
            drawLine(c(CORDEL), Offset(w * 0.5f, cy + 1 * u), Offset(w * 0.42f, cy + 6 * u), 0.8f * u)
        }
    }
}

/**
 * Tierras Antiguas: la biblioteca de la torre del camino, de madera tallada contra la pared de tablones. Los costados son
 * postes con una talla de soga y sus nudos; las tablas, gruesas, con una hilera de muescas talladas en el canto, y el mes va
 * en una tablilla clavada con letras quemadas. Arriba, una viga tallada de anillos entrelazados con sus remates, un mapa de
 * pergamino clavado en la pared (montes, río, bosque, el camino punteado hasta su marca y la rosa de los vientos) y un farol
 * de hierro colgado de su brazo. Abajo, el zócalo tallado, las patas y el piso de losas. Cada entrada es un tomo grueso de
 * cuero con esquinas de latón, una gema engastada en el lomo (de noche, una piedra de luna), la placa de latón con el día,
 * el mes en dorado, el broche y la cinta de marcar, que asoma más cuanto más escribiste. De noche se enciende el farol.
 * Distinto del Ideario, que es la sala de sillares con runas, los dos faroles y los arcones de la compañía.
 */
object EstanteAntiguas : DisenoBiblioteca {
    private val MADERA = Color(0xFF7A5232)
    private val MADERA_LUZ = Color(0xFFA27448)
    private val MADERA_OSC = Color(0xFF4A3018)
    private val NUDO = Color(0xFF3A240F)
    private val TALLA = Color(0xFF5E3E22)
    private val MURO = Color(0xFF60442C)
    private val PERGAMINO = Color(0xFFE6D3A6)
    private val PERGAMINO_OSC = Color(0xFFB89C68)
    private val TINTA = Color(0xFF3A2614)
    private val ROJO_MAPA = Color(0xFF9A3A24)
    private val VERDE_MAPA = Color(0xFF5E7A44)
    private val AZUL_MAPA = Color(0xFF4A6E8A)
    private val HIERRO = Color(0xFF2E2B28)
    private val HIERRO_LUZ = Color(0xFF5C5852)
    private val LATON = Color(0xFFC9A04A)
    private val LATON_OSC = Color(0xFF7E6024)
    private val LATON_LUZ = Color(0xFFE8CC7E)
    private val VIDRIO_FAROL = Color(0xFF8E8A70)
    private val CERA = Color(0xFFEDE3C8)
    private val LLAMA = Color(0xFFF4C66A)
    private val PIEDRA = Color(0xFF6E6658)
    /** Los cueros de los tomos: castaño, tabaco, verde musgo, vino y azul de pizarra. */
    private val CUEROS = listOf(Color(0xFF6A3A22), Color(0xFF4E2E1E), Color(0xFF3E4A2E), Color(0xFF5A2A2E), Color(0xFF2E3A4A))
    private val GEMAS = listOf(Color(0xFF2E8A5A), Color(0xFFB0303A), Color(0xFF3A5AB0), Color(0xFF8A3AA0))
    private val CINTAS = listOf(Color(0xFFA8302A), Color(0xFF2E6A4A), Color(0xFFC9A04A))
    private val NOCHE = Color(0xFF0F1426)
    private val LUNA = Color(0xFFE8E6D8)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la oscuridad de la torre. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0D0906), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.5f)
        return r
    }

    override val corona = 0.32f
    override val base = 0.12f
    override val lado = 0.07f
    override val altoRepisa: Dp = 116.dp
    override val tabla: Dp = 18.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.camino

    override fun ancho(palabras: Int): Dp = (26 + (palabras / 35).coerceAtMost(22)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.76f + 0.2f * azar(semilla, 1)

    /** Un nudo de la madera: el ojo oscuro y dos anillos de veta alrededor ([vertical] si la tabla corre de arriba abajo). */
    private fun DrawScope.nudo(o: Offset, ra: Float, vertical: Boolean, oscuro: Boolean, noche: Boolean) {
        val u = density
        val ancho0 = if (vertical) ra * 1.2f else ra * 2f
        val alto0 = if (vertical) ra * 2f else ra * 1.2f
        drawOval(t(NUDO, oscuro, noche), Offset(o.x - ancho0 / 2, o.y - alto0 / 2), Size(ancho0, alto0))
        for (k in 1..2) {
            val f = 1f + 0.7f * k
            drawOval(t(MADERA_OSC, oscuro, noche).copy(alpha = 0.7f), Offset(o.x - ancho0 * f / 2, o.y - alto0 * f / 2), Size(ancho0 * f, alto0 * f), style = Stroke(0.6f * u))
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // La pared de tablones de la torre, con sus clavos.
        val cuerpo = Rect(0f, d.corona.bottom, w, d.base.top)
        drawRect(c(MURO), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(MADERA_OSC).copy(alpha = 0.45f), 181, densidad = 0.6f, vertical = true, grosor = 0.7f, ondulacion = 1.4f)
        var x = 26 * u
        while (x < w) {
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 1f * u)
            x += 26 * u
        }
        // Cada repisa: la sombra de la tabla gruesa de arriba.
        d.repisas.forEach { r ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.32f), r.topLeft, Size(r.width, 9 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left, r.top + 9 * u), Size(r.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.2f), r.topLeft, Size(5 * u, r.height))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.right - 5 * u, r.top), Size(5 * u, r.height))
        }
        // Los postes: madera con su talla de soga por el centro y sus nudos.
        listOf(d.izquierda, d.derecha).forEachIndexed { s, r ->
            drawRect(c(MADERA), r.topLeft, r.size)
            vetas(r, c(MADERA_OSC).copy(alpha = 0.55f), 190 + s, densidad = 1.1f, vertical = true, grosor = 0.8f, ondulacion = 1.5f)
            val banda = Rect(r.left + r.width * 0.3f, r.top + 6 * u, r.right - r.width * 0.3f, r.bottom - 6 * u)
            drawRect(c(TALLA), banda.topLeft, banda.size)
            var yy = banda.top
            while (yy < banda.bottom - 5 * u) {
                drawLine(c(MADERA_LUZ), Offset(banda.left, yy + 5 * u), Offset(banda.right, yy), 2 * u, cap = StrokeCap.Round)
                yy += 5 * u
            }
            for (j in 0 until 3) {
                val nx = if ((j + s) % 2 == 0) r.left + r.width * 0.15f else r.right - r.width * 0.15f
                nudo(Offset(nx, r.top + r.height * (0.18f + 0.3f * j + 0.06f * azar(s * 5 + j, 4))), 1.6f * u, vertical = true, oscuro = oscuro, noche = noche)
            }
            bisel(r, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.25f), 1.4f * u)
        }
        // Las tablas gruesas, con su hilera de muescas talladas y algún nudo.
        d.tablas.forEachIndexed { k, r ->
            drawRect(c(MADERA), r.topLeft, r.size)
            vetas(r, c(MADERA_OSC).copy(alpha = 0.5f), 200 + k, densidad = 0.7f, grosor = 0.8f, ondulacion = 1.4f)
            drawRect(c(MADERA_LUZ), r.topLeft, Size(r.width, 2.4f * u))
            var mx = r.left + 2 * u
            while (mx < r.right - 6 * u) {
                val muesca = Path().apply { moveTo(mx, r.bottom - 1 * u); lineTo(mx + 3 * u, r.bottom - 5 * u); lineTo(mx + 6 * u, r.bottom - 1 * u); close() }
                drawPath(muesca, c(TALLA))
                mx += 7 * u
            }
            nudo(Offset(r.left + r.width * (0.1f + 0.2f * azar(k, 3)), r.top + r.height * 0.42f), 2.2f * u, vertical = false, oscuro = oscuro, noche = noche)
            drawRect(Color.Black.copy(alpha = 0.28f), Offset(r.left, r.bottom), Size(r.width, 4 * u))
        }
        // El rótulo de cada mes: una tablilla clavada, con las letras quemadas.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left + 1.4f * u, r.top + 1.4f * u), r.size)
            drawRect(c(MADERA_LUZ), r.topLeft, r.size)
            vetas(r, c(MADERA_OSC).copy(alpha = 0.35f), p.texto.hashCode(), densidad = 0.6f, grosor = 0.5f)
            drawRect(c(TALLA), r.topLeft, r.size, style = Stroke(1.2f * u))
            listOf(r.left + 3.5f * u, r.right - 3.5f * u).forEach { cx ->
                drawCircle(c(HIERRO), 1.4f * u, Offset(cx, r.center.y))
                drawCircle(c(HIERRO_LUZ), 0.5f * u, Offset(cx - 0.4f * u, r.center.y - 0.4f * u))
            }
            textoEn(medidor, p.texto, Rect(r.left + 7 * u, r.top, r.right - 7 * u, r.bottom), letra, c(TINTA), 10.5f, espaciado = 0.6f, minimo = 5f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo tallado con rombos, las patas y el piso de losas.
        val b = d.base
        val zocalo = Rect(b.left, b.top, b.right, b.top + b.height * 0.5f)
        val piso = zocalo.bottom
        drawRect(c(PIEDRA), Offset(0f, piso), Size(w, h - piso))
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(0f, piso + (h - piso) * 0.5f), Offset(w, piso + (h - piso) * 0.5f), 0.8f * u)
        var lx = 0f
        var j2 = 0
        while (lx < w) {
            val desfase = if (j2 % 2 == 0) 0f else 14 * u
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(lx + desfase, piso), Offset(lx + desfase - 3 * u, piso + (h - piso) * 0.5f), 0.8f * u)
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(lx + desfase + 20 * u, piso + (h - piso) * 0.5f), Offset(lx + desfase + 17 * u, h), 0.8f * u)
            lx += (34 + 10 * azar(j2, 6)) * u; j2++
        }
        listOf(d.izquierda, d.derecha).forEach { r ->
            drawRect(c(MADERA_OSC), Offset(r.left, piso - 1 * u), Size(r.width, (h - piso) * 0.55f))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, piso + (h - piso) * 0.55f - 1 * u), Size(r.width, 2 * u))
        }
        drawRect(c(MADERA_OSC), zocalo.topLeft, zocalo.size)
        vetas(zocalo, Color.Black.copy(alpha = 0.3f), 211, densidad = 0.8f, grosor = 0.7f)
        drawRect(c(MADERA), zocalo.topLeft, Size(zocalo.width, 3 * u))
        val rr = zocalo.height * 0.26f
        var rx = zocalo.left + 10 * u
        while (rx < zocalo.right - 6 * u) {
            val cy = zocalo.center.y + 1.5f * u
            val rombo = Path().apply { moveTo(rx, cy - rr); lineTo(rx + rr * 1.3f, cy); lineTo(rx, cy + rr); lineTo(rx - rr * 1.3f, cy); close() }
            drawPath(rombo, c(MADERA_LUZ).copy(alpha = 0.55f), style = Stroke(1.2f * u))
            drawCircle(c(TALLA), 1.2f * u, Offset(rx, cy))
            rx += rr * 3.4f
        }
    }

    /** El mapa de pergamino clavado en la pared: montes, río, bosque, el camino punteado y la rosa de los vientos. */
    private fun DrawScope.mapa(m: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawPath(contornoIrregular(m.translate(1.5f * u, 2f * u), 7, 36, 0.03f), Color.Black.copy(alpha = 0.25f))
        val hoja = contornoIrregular(m, 7, 36, 0.03f)
        drawPath(hoja, c(PERGAMINO))
        clipPath(hoja) {
            // Los bordes tostados.
            drawRect(c(PERGAMINO_OSC).copy(alpha = 0.35f), m.topLeft, Size(m.width, 3 * u))
            drawRect(c(PERGAMINO_OSC).copy(alpha = 0.35f), Offset(m.left, m.bottom - 3 * u), Size(m.width, 3 * u))
            drawRect(c(PERGAMINO_OSC).copy(alpha = 0.3f), m.topLeft, Size(3 * u, m.height))
            drawRect(c(PERGAMINO_OSC).copy(alpha = 0.3f), Offset(m.right - 3 * u, m.top), Size(3 * u, m.height))
            // Los montes.
            for (j in 0 until 4) {
                val bx = m.left + m.width * (0.1f + 0.08f * j)
                val by = m.top + m.height * (0.36f + 0.06f * (j % 2))
                val monte = Path().apply { moveTo(bx - 5 * u, by); lineTo(bx, by - 7 * u); lineTo(bx + 5 * u, by) }
                drawPath(monte, c(TINTA), style = Stroke(0.9f * u))
                drawLine(c(TINTA).copy(alpha = 0.5f), Offset(bx, by - 7 * u), Offset(bx + 1.5f * u, by), 0.6f * u)
            }
            // El río.
            val rio = Path().apply {
                moveTo(m.left + m.width * 0.5f, m.top)
                cubicTo(m.left + m.width * 0.42f, m.top + m.height * 0.3f, m.left + m.width * 0.66f, m.top + m.height * 0.5f, m.left + m.width * 0.56f, m.bottom)
            }
            drawPath(rio, c(AZUL_MAPA), style = Stroke(1.6f * u))
            // El bosque.
            for (j in 0 until 9) drawCircle(c(VERDE_MAPA), 1.8f * u, Offset(m.left + m.width * (0.66f + 0.05f * (j % 3)), m.top + m.height * (0.52f + 0.1f * (j / 3))))
            // El camino punteado, desde la torre hasta la marca.
            val p0 = Offset(m.left + m.width * 0.1f, m.bottom - m.height * 0.16f)
            val p1 = Offset(m.left + m.width * 0.45f, m.top + m.height * 0.5f)
            val p2 = Offset(m.left + m.width * 0.82f, m.bottom - m.height * 0.12f)
            for (k in 0..16) {
                val tt = k / 16f
                val a = (1 - tt) * (1 - tt); val bb = 2 * (1 - tt) * tt; val cc = tt * tt
                drawCircle(c(ROJO_MAPA), 0.9f * u, Offset(a * p0.x + bb * p1.x + cc * p2.x, a * p0.y + bb * p1.y + cc * p2.y))
            }
            drawRect(c(TINTA), Offset(p0.x - 2.5f * u, p0.y - 6 * u), Size(5 * u, 6 * u))
            for (k in 0 until 3) drawRect(c(TINTA), Offset(p0.x - 2.5f * u + k * 2 * u, p0.y - 7.5f * u), Size(1 * u, 1.5f * u))
            drawLine(c(ROJO_MAPA), Offset(p2.x - 2.5f * u, p2.y - 2.5f * u), Offset(p2.x + 2.5f * u, p2.y + 2.5f * u), 1.2f * u)
            drawLine(c(ROJO_MAPA), Offset(p2.x + 2.5f * u, p2.y - 2.5f * u), Offset(p2.x - 2.5f * u, p2.y + 2.5f * u), 1.2f * u)
            // La rosa de los vientos.
            val rosa = Offset(m.right - m.width * 0.12f, m.top + m.height * 0.24f)
            drawPath(estrella(rosa, m.height * 0.15f, 4, 0.28f), c(TINTA))
            drawPath(estrella(rosa, m.height * 0.09f, 4, 0.3f, giro = -45f), c(ROJO_MAPA))
            drawCircle(c(PERGAMINO), 1 * u, rosa)
        }
        drawPath(hoja, c(PERGAMINO_OSC), style = Stroke(0.8f * u))
        // Los clavos de las esquinas.
        listOf(m.topLeft, Offset(m.right, m.top), Offset(m.left, m.bottom), m.bottomRight).forEach { o ->
            val p = Offset(o.x + (if (o.x < m.center.x) 4 * u else -4 * u), o.y + (if (o.y < m.center.y) 4 * u else -4 * u))
            drawCircle(c(HIERRO), 1.8f * u, p)
            drawCircle(c(HIERRO_LUZ), 0.7f * u, Offset(p.x - 0.5f * u, p.y - 0.5f * u))
        }
    }

    /** El farol de hierro colgado de su gancho; de noche, encendido. */
    private fun DrawScope.farol(gancho: Offset, alto: Float, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        val tope = gancho.y + alto * 0.12f
        drawCircle(c(HIERRO), alto * 0.05f, Offset(gancho.x, gancho.y + alto * 0.06f), style = Stroke(1.2f * u))
        val anchoFarol = alto * 0.42f
        val vidrio = Rect(gancho.x - anchoFarol / 2, tope + alto * 0.16f, gancho.x + anchoFarol / 2, tope + alto * 0.72f)
        if (noche) {
            // El farol encendido: el único degradado del mueble.
            mancha(vidrio.center, alto * 1.4f, LLAMA.copy(alpha = 0.4f))
            drawRect(LLAMA.copy(alpha = 0.55f), vidrio.topLeft, vidrio.size)
        } else {
            drawRect(c(VIDRIO_FAROL).copy(alpha = 0.7f), vidrio.topLeft, vidrio.size)
        }
        // La vela de adentro.
        val vela = Rect(gancho.x - 2.5f * u, vidrio.bottom - vidrio.height * 0.32f, gancho.x + 2.5f * u, vidrio.bottom)
        drawRect(c(CERA), vela.topLeft, vela.size)
        drawLine(c(TINTA), Offset(gancho.x, vela.top), Offset(gancho.x, vela.top - 2 * u), 0.8f * u)
        if (noche) {
            val llama = Path().apply {
                moveTo(gancho.x - 2 * u, vela.top - 1 * u)
                quadraticTo(gancho.x - 2.4f * u, vela.top - 5 * u, gancho.x, vela.top - 10 * u)
                quadraticTo(gancho.x + 2.4f * u, vela.top - 5 * u, gancho.x + 2 * u, vela.top - 1 * u)
                close()
            }
            drawPath(llama, Color(0xFFFFE6A0))
        }
        drawRect(c(HIERRO), vidrio.topLeft, vidrio.size, style = Stroke(1.6f * u))
        drawLine(c(HIERRO), Offset(vidrio.left + vidrio.width / 3, vidrio.top), Offset(vidrio.left + vidrio.width / 3, vidrio.bottom), 1f * u)
        drawLine(c(HIERRO), Offset(vidrio.right - vidrio.width / 3, vidrio.top), Offset(vidrio.right - vidrio.width / 3, vidrio.bottom), 1f * u)
        val tapa = Path().apply {
            moveTo(gancho.x - alto * 0.08f, tope); lineTo(gancho.x + alto * 0.08f, tope)
            lineTo(vidrio.right + 2 * u, vidrio.top); lineTo(vidrio.left - 2 * u, vidrio.top); close()
        }
        drawPath(tapa, c(HIERRO))
        drawLine(c(HIERRO_LUZ), Offset(gancho.x - alto * 0.06f, tope + 1 * u), Offset(vidrio.left, vidrio.top - 1 * u), 0.6f * u)
        drawRect(c(HIERRO), Offset(vidrio.left - 2 * u, vidrio.bottom), Size(vidrio.width + 4 * u, alto * 0.08f))
        val remate = Path().apply {
            moveTo(gancho.x - alto * 0.1f, vidrio.bottom + alto * 0.08f); lineTo(gancho.x + alto * 0.1f, vidrio.bottom + alto * 0.08f)
            lineTo(gancho.x, vidrio.bottom + alto * 0.18f); close()
        }
        drawPath(remate, c(HIERRO))
    }

    /** La pared de arriba con el mapa clavado, el brazo con el farol y la viga tallada con sus remates. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(MURO), r.topLeft, r.size)
        vetas(r, c(MADERA_OSC).copy(alpha = 0.45f), 221, densidad = 0.6f, vertical = true, grosor = 0.7f, ondulacion = 1.4f)
        var x = r.left + 26 * u
        while (x < r.right) { drawLine(Color.Black.copy(alpha = 0.4f), Offset(x, r.top), Offset(x, r.bottom), 1f * u); x += 26 * u }
        mapa(Rect(r.left + r.width * 0.08f, r.top + r.height * 0.08f, r.left + r.width * 0.6f, r.top + r.height * 0.56f), oscuro, noche)
        // El brazo de hierro con su tornapunta y el farol colgado.
        val gancho = Offset(r.right - r.width * 0.2f, r.top + r.height * 0.1f)
        val pared = r.right - r.width * 0.03f
        drawLine(c(HIERRO), Offset(pared, gancho.y), gancho, 2.2f * u)
        drawLine(c(HIERRO), Offset(pared, gancho.y + r.height * 0.14f), Offset(gancho.x + r.width * 0.07f, gancho.y), 1.6f * u)
        drawArc(c(HIERRO), 180f, 270f, false, Offset(gancho.x - 3 * u, gancho.y - 6 * u), Size(6 * u, 6 * u), style = Stroke(1.4f * u))
        drawRect(c(HIERRO), Offset(pared - 1 * u, gancho.y - 4 * u), Size(3 * u, r.height * 0.2f))
        farol(gancho, r.height * 0.48f, oscuro, noche)
        // La viga tallada con anillos entrelazados.
        val viga = Rect(r.left, r.top + r.height * 0.64f, r.right, r.bottom)
        drawRect(c(MADERA), viga.topLeft, viga.size)
        vetas(viga, c(MADERA_OSC).copy(alpha = 0.5f), 231, densidad = 0.7f, grosor = 0.8f, ondulacion = 1.4f)
        drawRect(c(MADERA_LUZ), viga.topLeft, Size(viga.width, 2.4f * u))
        val banda = Rect(viga.left + 4 * u, viga.top + viga.height * 0.24f, viga.right - 4 * u, viga.bottom - viga.height * 0.24f)
        drawRect(c(TALLA), banda.topLeft, banda.size)
        val ra = banda.height * 0.42f
        var cx = banda.left + ra
        while (cx < banda.right - ra * 0.5f) {
            drawCircle(c(MADERA_LUZ), ra, Offset(cx, banda.center.y), style = Stroke(1.6f * u))
            cx += ra * 1.4f
        }
        // Los remates en forma de bellota en las puntas.
        listOf(r.left + r.width * 0.04f, r.right - r.width * 0.04f).forEach { px ->
            drawOval(c(MADERA_LUZ), Offset(px - 4 * u, viga.top - 9 * u), Size(8 * u, 10 * u))
            drawRect(c(MADERA_OSC), Offset(px - 4.5f * u, viga.top - 10 * u), Size(9 * u, 3 * u))
            drawLine(c(MADERA_OSC), Offset(px, viga.top - 13 * u), Offset(px, viga.top - 10 * u), 1.2f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val sem = v.semilla and 0x7fffffff
        val base = CUEROS[sem % CUEROS.size]
        val cuero = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // El tomo grueso de cuero, con su lomo redondeado (luz y sombra planas).
        drawRoundRect(cuero, Offset.Zero, size, CornerRadius(2 * u))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.1f, 0f), Size(w * 0.14f, h))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(w * 0.84f, 0f), Size(w * 0.16f, h))
        // Los nervios de arriba y de abajo.
        listOf(0.07f, 0.12f, 0.9f).forEach { f ->
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, h * f - 1.4f * u), Size(w, 2.8f * u))
            drawLine(c(LATON_LUZ), Offset(w * 0.06f, h * f - 1.8f * u), Offset(w * 0.94f, h * f - 1.8f * u), 0.6f * u)
        }
        // Las esquinas de latón con su remache.
        val k = minOf(w * 0.3f, 7 * u)
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, s) ->
            val esquina = Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * k, o.y); lineTo(o.x, o.y + s.y * k); close() }
            drawPath(esquina, c(LATON))
            drawLine(c(LATON_OSC), Offset(o.x + s.x * k, o.y), Offset(o.x, o.y + s.y * k), 0.7f * u)
            drawCircle(c(LATON_OSC), 0.8f * u, Offset(o.x + s.x * k * 0.3f, o.y + s.y * k * 0.3f))
        }
        // La gema engastada en el lomo; de noche, una piedra de luna en su lugar.
        val gc = Offset(w * 0.5f, h * 0.22f)
        val gr = minOf(w * 0.18f, 4.5f * u)
        drawCircle(c(LATON), gr * 1.3f, gc)
        drawCircle(c(LATON_OSC), gr * 1.3f, gc, style = Stroke(0.6f * u))
        if (v.noche) {
            drawCircle(LUNA, gr, gc)
            drawCircle(mezcla(LUNA, NOCHE, 0.35f), gr * 0.82f, Offset(gc.x + gr * 0.4f, gc.y - gr * 0.25f))
        } else {
            val gema = GEMAS[(sem / 7) % GEMAS.size]
            val talla = Path().apply {
                for (j in 0 until 6) {
                    val a = Math.toRadians(-90.0 + j * 60.0)
                    val p = Offset(gc.x + cos(a).toFloat() * gr, gc.y + sin(a).toFloat() * gr)
                    if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(talla, c(gema))
            drawPath(estrella(gc, gr * 0.55f, 6, 0.6f), Color.White.copy(alpha = 0.18f))
            drawCircle(Color.White.copy(alpha = 0.6f), gr * 0.22f, Offset(gc.x - gr * 0.35f, gc.y - gr * 0.35f))
        }
        // La placa de latón con el día.
        val placa = Rect(w * 0.16f, h * 0.32f, w * 0.84f, h * 0.5f)
        drawRect(c(LATON), placa.topLeft, placa.size)
        drawRect(c(LATON_LUZ), placa.topLeft, Size(placa.width, 1.2f * u))
        drawRect(c(LATON_OSC), placa.topLeft, placa.size, style = Stroke(0.8f * u))
        textoEn(medidor, v.dia, placa.deflate(1.4f * u), letra, c(TINTA), 13f)
        // El mes, en dorado a lo largo del lomo.
        textoEn(medidor, v.mes, Rect(w * 0.2f, h * 0.53f, w * 0.8f, h * 0.8f), letra, c(LATON_LUZ), 8.5f, vertical = true, minimo = 4.5f)
        // El broche: una correa de cuero con su hebilla de latón.
        val cy = h * 0.835f
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(0f, cy - 2 * u), Size(w, 4 * u))
        drawRect(c(MADERA_OSC), Offset(0f, cy - 1.6f * u), Size(w, 3.2f * u))
        drawRoundRect(c(LATON), Offset(w * 0.5f - 3 * u, cy - 3 * u), Size(6 * u, 6 * u), CornerRadius(1 * u))
        drawCircle(c(LATON_OSC), 1 * u, Offset(w * 0.5f, cy))
        // La cinta de marcar que asoma por abajo: más larga cuanto más escribiste.
        val cinta = CINTAS[(sem / 3) % CINTAS.size]
        val cx = w * (0.28f + 0.1f * azar(v.semilla, 9))
        val y0 = h * (0.94f - 0.06f * v.lleno)
        val lazo = Path().apply {
            moveTo(cx - 1.4f * u, y0); lineTo(cx + 1.4f * u, y0); lineTo(cx + 1.4f * u, h)
            lineTo(cx, h - 1.6f * u); lineTo(cx - 1.4f * u, h); close()
        }
        drawPath(lazo, c(cinta))
    }
}
