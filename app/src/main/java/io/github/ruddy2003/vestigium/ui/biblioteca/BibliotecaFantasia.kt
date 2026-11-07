package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
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
 * y algunos van atados con cordel. De noche se enciende la vela y, en el hueco bajo el mueble, se abren dos ojos. La tapa es
 * ese libro grande y de frente: la tela gastada con su filete dorado, el florón (de noche, la luna), la etiqueta con la fecha,
 * el cerco de una taza, el recorte que asoma, las notas por arriba y la correa con su hebilla; al abrirse, la tapa gira y
 * deja ver la primera hoja, garabateada. Distinto del Ideario, que es el archivo de un pueblo de la costa con su quinqué y
 * sus cajas de cartón.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.18f, 0.365f, 0.8f, 0.71f)
    override val letraTapa: FontFamily = Letra.spectralCursiva
    override val tamTapa = 14f
    override val lineasTapa = 3
    override val centradoTapa = true

    /** El papel de la etiqueta, donde va el extracto: de noche lo alumbra la vela, así que se hunde menos. */
    private fun papelTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PAPEL_VIEJO
        if (oscuro) r = mezcla(r, Color(0xFF0A0806), 0.32f)
        if (noche) r = mezcla(r, NOCHE, 0.28f)
        return r
    }

    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = legibleFan(papelTapa(oscuro, noche), TINTA, PAPEL)
    override fun palabrasTapa(n: Int): String = palabrasFan(n)

    /** El signo del ojo: la almendra, el iris con su pupila y tres rayos arriba, en [color]. */
    private fun DrawScope.signoOjo(o: Offset, m: Float, color: Color) {
        val u = density
        val ojo = Path().apply {
            moveTo(o.x - m, o.y)
            quadraticTo(o.x, o.y - m * 0.75f, o.x + m, o.y)
            quadraticTo(o.x, o.y + m * 0.75f, o.x - m, o.y)
            close()
        }
        drawPath(ojo, color, style = Stroke(1.1f * u))
        drawCircle(color, m * 0.34f, o, style = Stroke(1f * u))
        drawCircle(color, m * 0.14f, o)
        for (j in -1..1) drawLine(color, Offset(o.x + j * m * 0.45f, o.y - m * 0.5f), Offset(o.x + j * m * 0.6f, o.y - m * 0.85f), 0.9f * u)
    }

    /** La tapa, recortada a su lienzo: lo que se aparta al abrir y la luz de noche no se salen de su lugar. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect { dibujarTapa(e, medidor, oscuro) }

    private fun DrawScope.dibujarTapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val sem = e.semilla and 0x7fffffff
        val tela = c(TELAS[sem % TELAS.size])
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val tapa = Rect(w * 0.02f, h * 0.045f, w * 0.93f, h * 0.93f)
        val lomo = w * 0.07f
        // Los papelitos de notas que asoman por arriba: más cuanto más escribiste.
        val notas = 1 + (lleno * 3).toInt()
        for (j in 0 until notas) {
            val nx = w * (0.22f + 0.17f * j + 0.05f * azar(e.semilla, 10 + j))
            val nw = w * (0.07f + 0.04f * azar(e.semilla, 20 + j))
            rotate((azar(e.semilla, 30 + j) - 0.5f) * 12f, Offset(nx + nw / 2, tapa.top)) {
                drawRect(c(if (j % 2 == 0) PAPEL else PAPEL_VIEJO), Offset(nx, tapa.top - h * 0.04f), Size(nw, h * 0.1f))
                drawLine(c(TINTA).copy(alpha = 0.35f), Offset(nx + nw * 0.15f, tapa.top - h * 0.02f), Offset(nx + nw * 0.8f, tapa.top - h * 0.02f), 0.7f * u)
            }
        }
        // El bloque de las hojas, con sus cantos.
        val bloque = Rect(tapa.left + lomo * 0.5f, tapa.top + h * 0.025f, w * 0.965f, h * 0.97f)
        drawRect(c(PAPEL), bloque.topLeft, bloque.size)
        for (j in 1..5) {
            drawLine(c(TE).copy(alpha = 0.4f), Offset(bloque.left, bloque.bottom - j * 1.6f * u), Offset(bloque.right, bloque.bottom - j * 1.6f * u), 0.5f * u)
            drawLine(c(TE).copy(alpha = 0.4f), Offset(bloque.right - j * 1.6f * u, bloque.top), Offset(bloque.right - j * 1.6f * u, bloque.bottom), 0.5f * u)
        }
        // De noche, dos ojos que se abren en la sombra bajo el libro.
        if (e.noche) {
            drawRect(Color.Black.copy(alpha = 0.5f), Offset(bloque.left, bloque.bottom), Size(bloque.width, h - bloque.bottom))
            listOf(0.84f, 0.875f).forEach { f -> drawOval(OJOS, Offset(w * f, h * 0.975f), Size(3.4f * u, 1.6f * u)) }
        }
        // La primera hoja (se ve al abrir): renglones, garabatos a mano, un recorte pegado y el signo dibujado al margen.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + lomo, tapa.top + 2 * u, bloque.right - 3 * u, bloque.bottom - 3 * u)
            drawRect(c(PAPEL), pag.topLeft, pag.size)
            var y = pag.top + 14 * u
            var n = 0
            while (y < pag.bottom - 6 * u) {
                drawLine(c(TE).copy(alpha = 0.3f), Offset(pag.left + 8 * u, y), Offset(pag.right - 8 * u, y), 0.5f * u)
                if (n % 5 != 3) {
                    val garabato = Path()
                    garabato.moveTo(pag.left + 12 * u, y - 2 * u)
                    var x = pag.left + 12 * u
                    val fin = pag.left + pag.width * (0.5f + 0.4f * azar(e.semilla, 400 + n))
                    var k = 0
                    while (x < fin) { garabato.quadraticTo(x + 2 * u, y - (if (k % 2 == 0) 5f else 0.5f) * u, x + 4 * u, y - 2 * u); x += 4 * u; k++ }
                    drawPath(garabato, c(TINTA).copy(alpha = 0.55f), style = Stroke(0.8f * u))
                }
                y += 9 * u; n++
            }
            val recorte = Rect(pag.right - pag.width * 0.3f, pag.top + pag.height * 0.45f, pag.right - pag.width * 0.06f, pag.bottom - pag.height * 0.08f)
            rotate(3f, recorte.center) {
                drawRect(c(PAPEL_VIEJO), recorte.topLeft, recorte.size)
                for (j in 0 until 7) drawLine(c(TINTA).copy(alpha = 0.4f), Offset(recorte.left + 3 * u, recorte.top + 5 * u + j * 4 * u), Offset(recorte.right - 3 * u, recorte.top + 5 * u + j * 4 * u), 0.8f * u)
            }
            signoOjo(Offset(pag.right - pag.width * 0.18f, pag.top + pag.height * 0.22f), h * 0.06f, c(TINTA).copy(alpha = 0.7f))
        }
        // La tapa de tela gira sobre el lomo y deja ver la primera hoja.
        withTransform({ scale(1f - 0.92f * a, 1f, Offset(tapa.left, tapa.center.y)) }) {
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + 2 * u, tapa.top + 3 * u), tapa.size)
            drawRoundRect(tela, tapa.topLeft, tapa.size, CornerRadius(3 * u))
            // La trama de la tela y las orillas gastadas, más claras.
            var ty = tapa.top + 2 * u
            while (ty < tapa.bottom) { drawLine(Color.Black.copy(alpha = 0.07f), Offset(tapa.left, ty), Offset(tapa.right, ty), 0.6f * u); ty += 2.6f * u }
            drawRoundRect(c(ROBLE_LUZ).copy(alpha = 0.45f), tapa.topLeft, tapa.size, CornerRadius(3 * u), style = Stroke(2.4f * u))
            listOf(tapa.topLeft, Offset(tapa.right, tapa.top), Offset(tapa.left, tapa.bottom), tapa.bottomRight).forEach { p ->
                drawCircle(c(PAPEL_VIEJO).copy(alpha = 0.18f), 7 * u, p)
            }
            // El lomo, con sus nervios y filetes dorados.
            drawRect(mezcla(tela, Color.Black, 0.3f), tapa.topLeft, Size(lomo, tapa.height))
            drawRect(Color.White.copy(alpha = 0.08f), Offset(tapa.left + lomo * 0.2f, tapa.top), Size(lomo * 0.2f, tapa.height))
            listOf(0.14f, 0.36f, 0.64f, 0.86f).forEach { f ->
                val ny = tapa.top + tapa.height * f
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left, ny - 2 * u), Size(lomo, 4 * u))
                drawLine(c(ORO), Offset(tapa.left + 2 * u, ny - 2.6f * u), Offset(tapa.left + lomo - 2 * u, ny - 2.6f * u), 0.7f * u)
                drawLine(c(ORO), Offset(tapa.left + 2 * u, ny + 2.6f * u), Offset(tapa.left + lomo - 2 * u, ny + 2.6f * u), 0.7f * u)
            }
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(tapa.left + lomo, tapa.top), Offset(tapa.left + lomo, tapa.bottom), 1.2f * u)
            // El filete dorado de la tapa, doble.
            val marco = Rect(tapa.left + lomo + 7 * u, tapa.top + 7 * u, tapa.right - 7 * u, tapa.bottom - 7 * u)
            drawRect(c(ORO).copy(alpha = 0.8f), marco.topLeft, marco.size, style = Stroke(1.2f * u))
            drawRect(c(ORO).copy(alpha = 0.45f), marco.deflate(3 * u).topLeft, marco.deflate(3 * u).size, style = Stroke(0.6f * u))
            // El florón dorado arriba (de noche, una luna) y el signo del ojo dorado abajo.
            val fc = Offset(w * 0.49f, h * 0.12f)
            val fr = h * 0.042f
            if (e.noche) {
                drawCircle(LUNA, fr, fc)
                drawCircle(tela, fr * 0.8f, Offset(fc.x + fr * 0.4f, fc.y - fr * 0.25f))
            } else {
                drawPath(estrella(fc, fr, 4, 0.35f), c(ORO))
                drawPath(estrella(fc, fr * 0.6f, 4, 0.35f, giro = -45f), c(ORO).copy(alpha = 0.7f))
            }
            signoOjo(Offset(w * 0.49f, h * 0.875f), h * 0.03f, c(ORO))
            // El cerco de una taza en la tela, que asoma bajo la etiqueta.
            drawCircle(c(TE).copy(alpha = 0.3f), h * 0.075f, Offset(w * 0.71f, h * 0.84f), style = Stroke(2.4f * u))
            // La etiqueta de papel con la fecha, el extracto y las palabras; abajo, la mancha de té.
            val etiqueta = Rect(w * 0.15f, h * 0.19f, w * 0.83f, h * 0.805f)
            val papel = papelTapa(oscuro, e.noche)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(etiqueta.left + 1.5f * u, etiqueta.top + 2 * u), etiqueta.size)
            drawRect(papel, etiqueta.topLeft, etiqueta.size)
            drawRect(c(TE).copy(alpha = 0.6f), etiqueta.deflate(3 * u).topLeft, etiqueta.deflate(3 * u).size, style = Stroke(0.7f * u))
            drawRect(c(TE).copy(alpha = 0.16f), Offset(etiqueta.left, etiqueta.bottom - etiqueta.height * 0.12f), Size(etiqueta.width * 0.55f, etiqueta.height * 0.12f))
            val tinta = legibleFan(papel, c(TINTA), PAPEL)
            val zona = Rect(w * 0.19f, h * 0.215f, w * 0.79f, h * 0.335f)
            val fechas = renglonesFechaFan(e.fecha, 34)
            if (fechas.size == 1) {
                textoEn(medidor, fechas[0], zona, letra, tinta, 13f, peso = FontWeight.SemiBold, minimo = 7f)
            } else {
                val corte = zona.top + zona.height * 0.55f
                textoEn(medidor, fechas[0], Rect(zona.left, zona.top, zona.right, corte), letra, tinta, 12f, peso = FontWeight.SemiBold, minimo = 7f)
                textoEn(medidor, fechas[1], Rect(zona.left, corte, zona.right, zona.bottom), letra, tinta.copy(alpha = 0.8f), 10f, minimo = 6.5f)
            }
            drawLine(tinta.copy(alpha = 0.35f), Offset(w * 0.3f, h * 0.348f), Offset(w * 0.68f, h * 0.348f), 0.6f * u)
            if (e.palabras > 0) {
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.22f, h * 0.715f, w * 0.76f, h * 0.785f), letra, tinta.copy(alpha = 0.7f), 10.5f, minimo = 7f)
            }
            // Un recorte de periódico que asoma por el canto.
            rotate(-4f, Offset(tapa.right, h * 0.62f)) {
                drawRect(c(PAPEL_VIEJO), Offset(tapa.right - 4 * u, h * 0.56f), Size(w * 0.045f, h * 0.12f))
                for (j in 0 until 4) drawLine(c(TINTA).copy(alpha = 0.35f), Offset(tapa.right, h * (0.58f + 0.022f * j)), Offset(tapa.right + w * 0.035f, h * (0.58f + 0.022f * j)), 0.6f * u)
            }
            // La correa de cuero con su hebilla de latón, que cierra la tapa.
            val xc = w * 0.855f
            drawRect(mezcla(c(TELAS[3]), Color.Black, 0.25f), Offset(xc, tapa.top), Size(w * 0.035f, tapa.height))
            drawLine(Color.White.copy(alpha = 0.1f), Offset(xc + 1 * u, tapa.top), Offset(xc + 1 * u, tapa.bottom), 0.6f * u)
            val hebilla = Rect(xc - w * 0.01f, h * 0.45f, xc + w * 0.045f, h * 0.55f)
            drawRoundRect(c(LATON), hebilla.topLeft, hebilla.size, CornerRadius(2 * u), style = Stroke(2.2f * u))
            drawLine(c(LATON_OSC), Offset(hebilla.center.x, hebilla.top), Offset(hebilla.center.x, hebilla.bottom), 1.4f * u)
            // De noche, la luz de la vela que llega desde arriba a la izquierda (el único degradado de la tapa).
            if (e.noche) clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) { mancha(Offset(tapa.left + w * 0.08f, tapa.top), h * 0.8f, LLAMA.copy(alpha = 0.22f)) }
            // Al girar, la tapa se oscurece.
            drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /** Un clip de alambre, de pie, con la vuelta de adentro, a partir de [o] (arriba) y de largo [l]. */
    private fun DrawScope.clip(o: Offset, l: Float, color: Color) {
        val u = density
        val a = l * 0.26f
        val p = Path().apply {
            moveTo(o.x + a * 0.25f, o.y + l * 0.62f)
            lineTo(o.x + a * 0.25f, o.y + a * 0.5f)
            arcTo(Rect(o.x - a * 0.25f, o.y, o.x + a * 0.75f, o.y + a), 0f, -180f, false)
            lineTo(o.x - a * 0.25f, o.y + l - a * 0.5f)
            arcTo(Rect(o.x - a * 0.25f, o.y + l - a, o.x + a * 0.75f + a * 0.25f, o.y + l), 180f, -180f, false)
            lineTo(o.x + a * 1f, o.y + a * 0.4f)
        }
        drawPath(p, color, style = Stroke(1.3f * u, cap = StrokeCap.Round))
    }

    /** El cerco que deja una taza sobre el papel: dos anillos abiertos, irregulares, en [color]. */
    private fun DrawScope.cercoTaza(c: Offset, r: Float, color: Color, semilla: Int) {
        val u = density
        drawArc(color, 20f + 40f * azar(semilla, 1), 290f, false, Offset(c.x - r, c.y - r), Size(r * 2, r * 2), style = Stroke(1.8f * u))
        drawArc(color.copy(alpha = color.alpha * 0.6f), 200f, 120f, false, Offset(c.x - r * 0.93f, c.y - r * 0.9f), Size(r * 1.86f, r * 1.86f), style = Stroke(1f * u))
    }

    /**
     * El encabezado: la carpeta del caso. Arriba a la izquierda, la pestaña con el rótulo del legajo; debajo, la carpeta de
     * cartulina con su filete rojo de ficha, la fecha a máquina de leer (grande) y el detalle; a la derecha, el sello de tinta
     * con el signo del ojo, torcido (de noche, la luna creciente con dos estrellas); el clip arriba y el cerco de una taza.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val carton = if (p.oscura) mezcla(p.color, p.tinta, 0.09f) else tonoFan(PAPEL_VIEJO, p, oscuro)
        val pestana = Path().apply {
            moveTo(4 * u, 26 * u); lineTo(10 * u, 4 * u); lineTo(w * 0.62f, 4 * u); lineTo(w * 0.62f + 8 * u, 26 * u); close()
        }
        val cuerpo = Rect(2 * u, 24 * u, w - 4 * u, h - 5 * u)
        val sombra = Color.Black.copy(alpha = if (p.oscura) 0.32f else 0.15f)
        translate(2 * u, 3 * u) { drawPath(pestana, sombra); drawRect(sombra, cuerpo.topLeft, cuerpo.size) }
        drawPath(pestana, carton)
        drawRect(carton, cuerpo.topLeft, cuerpo.size)
        if (e.noche) clipRect(cuerpo.left, 0f, cuerpo.right, cuerpo.bottom) { mancha(Offset(cuerpo.right, cuerpo.bottom), w * 0.55f, LLAMA.copy(alpha = 0.14f)) }
        drawPath(pestana, p.tinta.copy(alpha = 0.3f), style = Stroke(0.9f * u))
        drawRect(p.tinta.copy(alpha = 0.3f), cuerpo.topLeft, cuerpo.size, style = Stroke(0.9f * u))
        drawLine(carton, Offset(5 * u, cuerpo.top), Offset(w * 0.62f + 7 * u, cuerpo.top), 2 * u)
        // El cerco de la taza y unas manchas de té.
        cercoTaza(Offset(cuerpo.left + 26 * u, cuerpo.bottom - 10 * u), 17 * u, (if (p.oscura) p.tenue else TE).copy(alpha = 0.3f), e.numero)
        // El rótulo en la pestaña, el filete rojo de la ficha, la fecha y el detalle.
        textoEn(medidor, e.rotulo, Rect(16 * u, 6 * u, w * 0.62f - 4 * u, 24 * u), Letra.spectralCursiva, p.acento, 14f, minimo = 8f)
        drawLine(p.acento.copy(alpha = 0.6f), Offset(cuerpo.left + 8 * u, cuerpo.top + 10 * u), Offset(cuerpo.right - 8 * u, cuerpo.top + 10 * u), 0.9f * u)
        val der = cuerpo.right - 70 * u
        fechaEncFan(medidor, e.fecha, Rect(cuerpo.left + 12 * u, cuerpo.top + 13 * u, der, cuerpo.top + 62 * u), Letra.spectral, p.tinta, 22f, 26, FontWeight.Bold)
        textoEn(medidor, e.detalle, Rect(cuerpo.left + 12 * u, cuerpo.top + 63 * u, der, cuerpo.top + 79 * u), Letra.castoro, p.tenue, 11f, minimo = 7f)
        // El sello de tinta: dos anillos y el signo, torcido y con la tinta pareja.
        val sc = Offset(cuerpo.right - 36 * u, cuerpo.center.y + 2 * u)
        val tinta = p.acento.copy(alpha = 0.72f)
        rotate(-14f, sc) {
            drawCircle(tinta, 27 * u, sc, style = Stroke(1.8f * u))
            drawCircle(tinta, 23 * u, sc, style = Stroke(0.8f * u))
            for (k in 0 until 12) {
                val a = Math.toRadians(k * 30.0)
                drawCircle(tinta, 1 * u, Offset(sc.x + 25 * u * cos(a).toFloat(), sc.y + 25 * u * sin(a).toFloat()))
            }
            if (e.noche) {
                drawCircle(tinta, 10 * u, sc)
                drawCircle(carton, 8.5f * u, Offset(sc.x + 4.5f * u, sc.y - 3.5f * u))
                drawPath(estrella(Offset(sc.x + 9 * u, sc.y + 10 * u), 3 * u, 4, 0.35f), tinta)
                drawCircle(tinta, 1.2f * u, Offset(sc.x - 10 * u, sc.y - 10 * u))
            } else {
                signoOjo(Offset(sc.x, sc.y + 3 * u), 13 * u, tinta)
            }
        }
        clip(Offset(w - 22 * u, 14 * u), 30 * u, if (p.oscura) p.tenue else Color(0xFF8A8C8E))
    }

    /**
     * La hoja de la libreta del investigador: el margen rojo desvaído, unas gotas de tinta que se corrieron, el cerco de una
     * taza, un garabato en espiral en el margen, el signo del ojo muy tenue al centro como marca de agua y la esquina de abajo
     * doblada; de noche, una luna creciente abajo a la derecha. En modo oscuro, la hoja en la penumbra y la luz de una vela
     * desde abajo a la derecha.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawLine(p.acento.copy(alpha = 0.35f), Offset(24 * u, 0f), Offset(24 * u, h), 1.3f * u)
        for (k in 0 until 6) {
            val o = Offset(w * azar(19, k), h * (0.15f + 0.75f * azar(23, k)))
            drawCircle(p.tinta.copy(alpha = 0.06f), (2 + 4 * azar(29, k)) * u, o)
            if (k % 2 == 0) drawCircle(p.tinta.copy(alpha = 0.05f), 1.2f * u, Offset(o.x + 7 * u, o.y + 4 * u))
        }
        cercoTaza(Offset(w * 0.78f, h * 0.3f), 30 * u, (if (p.oscura) p.tenue else TE).copy(alpha = 0.12f), 3)
        val espiral = Path()
        val ce = Offset(12 * u, h * 0.62f)
        for (s in 0..60) {
            val a = s * 0.32f
            val r = 0.6f * u + s * 0.16f * u
            val q = Offset(ce.x + r * cos(a), ce.y + r * sin(a))
            if (s == 0) espiral.moveTo(q.x, q.y) else espiral.lineTo(q.x, q.y)
        }
        drawPath(espiral, p.tinta.copy(alpha = 0.14f), style = Stroke(0.9f * u))
        signoOjo(Offset(w / 2, h * 0.55f), w * 0.28f, p.tinta.copy(alpha = 0.035f))
        val d = 30 * u
        drawPath(Path().apply { moveTo(w - d, h); lineTo(w, h - d); lineTo(w - d, h - d); close() }, p.tinta.copy(alpha = 0.07f))
        drawLine(p.tinta.copy(alpha = 0.2f), Offset(w - d, h), Offset(w - d, h - d), 1f * u)
        drawLine(p.tinta.copy(alpha = 0.2f), Offset(w - d, h - d), Offset(w, h - d), 1f * u)
        if (noche) {
            val luna = Offset(w - 44 * u, h - 128 * u)
            drawCircle(p.acento.copy(alpha = 0.3f), 12 * u, luna)
            drawCircle(p.color, 10 * u, Offset(luna.x + 6 * u, luna.y - 4 * u))
        }
        if (oscuro && !p.oscura) penumbraFan(Offset(w * 0.98f, h * 1.02f), LLAMA, 0.24f)
    }

    /**
     * La barra: el lomo de la carpeta de cartulina (un degradado, más claro arriba), con el canto de roble oscuro al pie y una
     * tachuela de latón en cada punta.
     */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val carton = if (p.oscura) mezcla(p.color, p.tinta, 0.07f) else tonoFan(PAPEL, p, oscuro)
        drawRect(Brush.verticalGradient(listOf(mezcla(carton, Color.White, 0.2f), carton, mezcla(carton, PAPEL_VIEJO, 0.7f)), 0f, h - 4 * u), Offset.Zero, Size(w, h - 4 * u))
        drawRect(if (p.oscura) mezcla(p.color, Color.Black, 0.4f) else tonoFan(ROBLE, p, oscuro), Offset(0f, h - 4 * u), Size(w, 4 * u))
        drawLine(p.acento.copy(alpha = 0.4f), Offset(0f, h - 8 * u), Offset(w, h - 8 * u), 0.8f * u)
        listOf(6 * u, w - 6 * u).forEach { x ->
            drawCircle(tonoFan(LATON, p, oscuro), 3 * u, Offset(x, h * 0.45f))
            drawCircle(Color.Black.copy(alpha = 0.35f), 3 * u, Offset(x, h * 0.45f), style = Stroke(0.7f * u))
        }
    }

    /**
     * El botón de guardar: el portaetiquetas de latón de un cajón de archivo (un degradado, con brillo arriba), con su borde
     * hundido, un tornillo de cabeza ranurada en cada punta y su sombra plana.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 4 * u)
        val laton = if (oscuro) mezcla(LATON, Color.Black, 0.1f) else LATON
        drawRoundRect(Color.Black.copy(alpha = 0.32f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(3 * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(laton, Color(0xFFF2DC98), 0.5f), laton, LATON_OSC), r.top, r.bottom), r.topLeft, r.size, CornerRadius(3 * u))
        drawRoundRect(LATON_OSC, Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - 6 * u, r.height - 6 * u), CornerRadius(2 * u), style = Stroke(1f * u))
        listOf(r.left + 7 * u, r.right - 7 * u).forEach { x ->
            val c = Offset(x, r.center.y)
            drawCircle(mezcla(laton, Color.White, 0.25f), 2.6f * u, c)
            drawCircle(LATON_OSC, 2.6f * u, c, style = Stroke(0.7f * u))
            drawLine(LATON_OSC, Offset(c.x - 1.8f * u, c.y + 1.2f * u), Offset(c.x + 1.8f * u, c.y - 1.2f * u), 0.8f * u)
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = TINTA

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 260.dp

    /**
     * El color de la libreta con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra del estudio salvo donde llega
     * la lámpara ([luz], de 0 lejos a 1 bajo la pantalla, que además lo entibia).
     */
    private fun lc(x: Color, oscuro: Boolean, luz: Float = 0f): Color =
        if (!oscuro) x else mezcla(mezcla(x, Color(0xFF070605), 0.74f - 0.46f * luz), LLAMA, 0.12f * luz)

    /** Un recorte de periódico en [r]: el papel, la cabeza en negrita y las columnas de renglones. */
    private fun DrawScope.recorteLib(r: Rect, papel: Color, tinta: Color, semilla: Int) {
        val u = density
        drawRect(papel, r.topLeft, r.size)
        drawRect(tinta.copy(alpha = 0.7f), Offset(r.left + 2 * u, r.top + 2 * u), Size(r.width * (0.5f + 0.4f * azar(semilla, 1)), 2.4f * u))
        val cols = if (r.width > 30 * u) 2 else 1
        val ancho = (r.width - 4 * u - (cols - 1) * 2 * u) / cols
        for (k in 0 until cols) {
            var y = r.top + 7 * u
            while (y < r.bottom - 2 * u) { drawLine(tinta.copy(alpha = 0.3f), Offset(r.left + 2 * u + k * (ancho + 2 * u), y), Offset(r.left + 2 * u + k * (ancho + 2 * u) + ancho, y), 0.6f * u); y += 2.2f * u }
        }
    }

    /**
     * La libreta de momentos: el diario de recortes sobre el escritorio de roble. El cuaderno, de tela gastada, va tumbado y
     * atado con un cordel en cruz; por los cantos le asoman recortes de periódico (más cuanto más guardaste) y el bloque de
     * hojas engorda. En la tapa, una etiqueta de papel con el nombre a máquina y la cuenta, el signo del ojo y el cerco de una
     * taza. Alrededor: la lámpara de latón con su pantalla verde, la lupa, las tijeras abiertas, el bote de engrudo con su
     * pincel y unos recortes sueltos; detrás, el papel tapiz de rombos. Al abrirse, el cordel se suelta y la tapa se levanta
     * sobre el lomo: deja ver la primera hoja con sus recortes pegados y, por dentro, las guardas jaspeadas. En modo oscuro el
     * estudio se apaga y solo alumbra la lámpara. Un degradado: la luz de la lámpara sobre el escritorio.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val gordo = (kotlin.math.sqrt(e.hojas.coerceAtLeast(1).toFloat()) / 14f).coerceIn(0.07f, 1f)
        val s = e.semilla
        fun c(x: Color, luz: Float = 0f) = lc(x, oscuro, luz)
        // El papel tapiz de rombos y el friso de la pared.
        val mesa0 = h * 0.42f
        drawRect(c(TAPIZ, 0.2f), Offset.Zero, Size(w, mesa0))
        val linea = c(TAPIZ_LINEA, 0.2f)
        val paso = 22 * u
        var x = -paso
        while (x < w + paso) {
            drawLine(linea, Offset(x, 0f), Offset(x + mesa0 * 0.6f, mesa0), 0.8f * u)
            drawLine(linea, Offset(x + mesa0 * 0.6f, 0f), Offset(x, mesa0), 0.8f * u)
            x += paso
        }
        var fy = 11 * u
        while (fy < mesa0) { var fx = 0f; while (fx < w) { drawCircle(linea, 1.2f * u, Offset(fx + (if ((fy / paso).toInt() % 2 == 0) 0f else paso / 2), fy)); fx += paso }; fy += paso }
        // Un recorte clavado en la pared con una chincheta y un hilo rojo hasta otro.
        val rojo = c(Color(0xFF8E2A22), 0.2f)
        rotate(-4f, Offset(w * 0.1f, h * 0.15f)) { recorteLib(Rect(w * 0.04f, h * 0.07f, w * 0.17f, h * 0.27f), c(PAPEL_VIEJO, 0.2f), c(TINTA, 0.2f), s + 1) }
        rotate(5f, Offset(w * 0.3f, h * 0.12f)) { recorteLib(Rect(w * 0.24f, h * 0.05f, w * 0.34f, h * 0.2f), c(PAPEL, 0.2f), c(TINTA, 0.2f), s + 2) }
        drawLine(rojo, Offset(w * 0.105f, h * 0.08f), Offset(w * 0.29f, h * 0.06f), 0.9f * u)
        drawCircle(rojo, 2.4f * u, Offset(w * 0.105f, h * 0.08f)); drawCircle(rojo, 2.4f * u, Offset(w * 0.29f, h * 0.06f))
        // El escritorio: el tablero visto desde arriba, con sus vetas, y el frente con el cajón y su tirador de latón.
        val frente = h * 0.84f
        val tablero = Path().apply { moveTo(w * 0.02f, mesa0); lineTo(w * 0.98f, mesa0); lineTo(w * 1.02f, frente); lineTo(-w * 0.02f, frente); close() }
        drawRect(c(ROBLE_OSC, 0.4f), Offset(0f, mesa0), Size(w, frente - mesa0))
        drawPath(tablero, c(ROBLE, 0.6f))
        clipPath(tablero) { vetas(Rect(-w * 0.02f, mesa0, w * 1.02f, frente), c(ROBLE_VETA, 0.6f).copy(alpha = 0.6f), s + 5, densidad = 0.5f, ondulacion = 1.8f) }
        drawRect(c(ROBLE_OSC, 0.4f), Offset(0f, mesa0 - 3 * u), Size(w, 3 * u))
        // La luz de la lámpara sobre el escritorio (el único degradado).
        val foco = Offset(w * 0.78f, h * 0.6f)
        mancha(foco, if (oscuro) w * 0.75f else w * 0.45f, LLAMA.copy(alpha = if (oscuro) 0.38f else 0.18f))
        drawRect(c(ROBLE_LUZ, 0.4f), Offset(0f, frente), Size(w, 3 * u))
        drawRect(c(ROBLE, 0.3f), Offset(0f, frente + 3 * u), Size(w, h - frente))
        drawRect(c(ROBLE_OSC, 0.3f), Offset(w * 0.3f, frente + 8 * u), Size(w * 0.4f, h - frente - 8 * u), style = Stroke(1.4f * u))
        drawRoundRect(c(LATON, 0.3f), Offset(w * 0.46f, frente + 14 * u), Size(w * 0.08f, 5 * u), CornerRadius(2.5f * u))
        drawCircle(c(LATON_OSC, 0.3f), 2.4f * u, Offset(w * 0.5f, frente + 26 * u))
        // Detrás a la izquierda: el bote de engrudo con su pincel.
        val bote = Offset(w * 0.08f, h * 0.53f)
        drawRect(c(Color(0xFFCFC6AE), 0.3f), Offset(bote.x - 9 * u, bote.y - 16 * u), Size(18 * u, 16 * u))
        drawOval(c(Color(0xFFE8E2D2), 0.3f), Offset(bote.x - 9 * u, bote.y - 19 * u), Size(18 * u, 6 * u))
        drawRect(c(PAPEL_VIEJO, 0.3f), Offset(bote.x - 9 * u, bote.y - 12 * u), Size(18 * u, 7 * u))
        drawLine(c(TINTA, 0.3f), Offset(bote.x - 6 * u, bote.y - 8.5f * u), Offset(bote.x + 5 * u, bote.y - 8.5f * u), 1 * u)
        drawLine(c(ROBLE_LUZ, 0.3f), Offset(bote.x + 2 * u, bote.y - 16 * u), Offset(bote.x + 10 * u, bote.y - 34 * u), 2 * u, StrokeCap.Round)
        // La lámpara de escritorio a la derecha: pie de latón, columna, pantalla de vidrio verde y su cadenita.
        val lx = w * 0.86f
        val verde = c(Color(0xFF2E5A3A), 0.8f)
        drawOval(Color.Black.copy(alpha = 0.3f), Offset(lx - 22 * u, h * 0.52f), Size(46 * u, 9 * u))
        drawOval(c(LATON_OSC, 0.8f), Offset(lx - 20 * u, h * 0.5f), Size(40 * u, 10 * u))
        drawOval(c(LATON, 0.8f), Offset(lx - 20 * u, h * 0.49f), Size(40 * u, 9 * u))
        drawRect(c(LATON, 0.8f), Offset(lx - 2.5f * u, h * 0.22f), Size(5 * u, h * 0.28f))
        drawRect(c(LATON_OSC, 0.8f), Offset(lx + 0.8f * u, h * 0.22f), Size(1.7f * u, h * 0.28f))
        val pantalla = Path().apply { moveTo(lx - 34 * u, h * 0.26f); quadraticTo(lx - 30 * u, h * 0.14f, lx, h * 0.13f); quadraticTo(lx + 30 * u, h * 0.14f, lx + 34 * u, h * 0.26f); close() }
        drawPath(pantalla, verde)
        drawPath(Path().apply { moveTo(lx - 26 * u, h * 0.2f); quadraticTo(lx, h * 0.15f, lx + 22 * u, h * 0.19f) }, Color.White.copy(alpha = 0.25f), style = Stroke(2 * u))
        drawRect(c(LATON, 0.8f), Offset(lx - 35 * u, h * 0.255f), Size(70 * u, 2.4f * u))
        drawOval(LLAMA.copy(alpha = if (oscuro) 1f else 0.85f), Offset(lx - 30 * u, h * 0.262f), Size(60 * u, 4 * u))
        drawLine(c(LATON, 0.8f), Offset(lx + 14 * u, h * 0.26f), Offset(lx + 14 * u, h * 0.36f), 0.8f * u)
        drawCircle(c(LATON, 0.8f), 2 * u, Offset(lx + 14 * u, h * 0.37f))
        // El cuaderno: el bloque de hojas con los recortes que asoman, y encima la tapa.
        val tapa = Rect(w * 0.18f, h * 0.47f, w * 0.68f, h * 0.76f)
        val grosor = (4 + 18 * gordo) * u
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(tapa.left + 6 * u, tapa.top + 8 * u), Size(tapa.width, tapa.height + grosor))
        val asoman = (2 + e.hojas / 12).coerceAtMost(9)
        for (k in 0 until asoman) {
            val derecha = k % 3 != 0
            val y = tapa.top + tapa.height * (0.08f + 0.8f * azar(s, 200 + k))
            val l = (12 + 12 * azar(s, 210 + k)) * u; val alto = (14 + 14 * azar(s, 220 + k)) * u
            val r = if (derecha) Rect(tapa.right - 6 * u, y, tapa.right + l, y + alto) else Rect(tapa.left - l, y, tapa.left + 6 * u, y + alto)
            rotate((azar(s, 230 + k) - 0.5f) * 14f, r.center) { recorteLib(r, c(if (k % 2 == 0) PAPEL else PAPEL_VIEJO, 0.5f), c(TINTA, 0.5f), s + k) }
        }
        val bloque = c(PAPEL_VIEJO, 0.55f)
        drawRect(bloque, Offset(tapa.left + 2 * u, tapa.bottom - 2 * u), Size(tapa.width + grosor * 0.4f, grosor))
        drawRect(bloque, Offset(tapa.right - 2 * u, tapa.top + 3 * u), Size(grosor * 0.4f + 2 * u, tapa.height))
        var gy = tapa.bottom; var j = 0
        while (gy < tapa.bottom + grosor - 1 * u) { drawLine(c(TE, 0.55f).copy(alpha = 0.6f), Offset(tapa.left + 2 * u, gy), Offset(tapa.right + grosor * 0.4f, gy), 0.6f * u); gy += (1.6f + azar(s, j) * 1.2f) * u; j++ }
        // La primera hoja, con sus recortes pegados (se ve al levantar la tapa).
        val hoja = Rect(tapa.left + 2 * u, tapa.top + 2 * u, tapa.right - 2 * u, tapa.bottom - 2 * u)
        drawRect(c(PAPEL, 0.6f), hoja.topLeft, hoja.size)
        if (a > 0.02f) {
            rotate(-3f, hoja.center) { recorteLib(Rect(hoja.left + hoja.width * 0.08f, hoja.top + hoja.height * 0.08f, hoja.left + hoja.width * 0.52f, hoja.top + hoja.height * 0.6f), c(PAPEL_VIEJO, 0.6f), c(TINTA, 0.6f), s + 40) }
            rotate(4f, hoja.center) { recorteLib(Rect(hoja.left + hoja.width * 0.56f, hoja.top + hoja.height * 0.12f, hoja.right - hoja.width * 0.08f, hoja.top + hoja.height * 0.5f), c(PAPEL_VIEJO, 0.6f), c(TINTA, 0.6f), s + 41) }
            val tinta = c(TINTA, 0.6f).copy(alpha = 0.7f)
            for (k in 0 until 4) {
                val y = hoja.top + hoja.height * (0.68f + 0.07f * k)
                drawPath(Path().apply { moveTo(hoja.left + hoja.width * 0.1f, y); var xx = hoja.left + hoja.width * 0.1f; var q = 0
                    while (xx < hoja.left + hoja.width * (0.6f + 0.3f * azar(s, 60 + k))) { quadraticTo(xx + 3 * u, y + (if (q % 2 == 0) -2.2f else 2.2f) * u, xx + 6 * u, y); xx += 6 * u; q++ } }, tinta, style = Stroke(0.9f * u))
            }
            signoOjo(Offset(hoja.right - hoja.width * 0.18f, hoja.top + hoja.height * 0.74f), hoja.height * 0.1f, c(Color(0xFF2E5A3A), 0.6f))
        }
        // La tapa de tela: gira sobre el lomo y se levanta; pasado el canto se ven las guardas jaspeadas.
        val ang = Math.toRadians(105.0 * a)
        val cosA = kotlin.math.cos(ang).toFloat(); val sinA = kotlin.math.sin(ang).toFloat()
        val m = androidx.compose.ui.graphics.Matrix()
        m.values[androidx.compose.ui.graphics.Matrix.ScaleX] = cosA
        m.values[androidx.compose.ui.graphics.Matrix.SkewY] = -sinA * 0.38f
        m.values[androidx.compose.ui.graphics.Matrix.TranslateX] = tapa.left * (1 - cosA)
        m.values[androidx.compose.ui.graphics.Matrix.TranslateY] = sinA * 0.38f * tapa.left
        val tela = c(TELAS[(s and 0x7fffffff) % TELAS.size], 0.6f)
        withTransform({ transform(m) }) {
            if (cosA > 0f) {
                drawRect(tela, tapa.topLeft, tapa.size)
                clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
                    var t = tapa.top + 2 * u
                    while (t < tapa.bottom) { drawLine(Color.Black.copy(alpha = 0.08f), Offset(tapa.left, t), Offset(tapa.right, t), 0.6f * u); t += 2.4f * u }
                    drawRect(Color.Black.copy(alpha = 0.25f), tapa.topLeft, Size(tapa.width * 0.08f, tapa.height))
                    drawLine(Color.White.copy(alpha = 0.12f), Offset(tapa.left + tapa.width * 0.08f, tapa.top), Offset(tapa.left + tapa.width * 0.08f, tapa.bottom), 1 * u)
                    cercoTaza(Offset(tapa.right - tapa.width * 0.16f, tapa.bottom - tapa.height * 0.2f), tapa.height * 0.17f, c(TE, 0.6f).copy(alpha = 0.55f), s)
                }
                drawRect(mezcla(tela, PAPEL_VIEJO, 0.35f), tapa.topLeft, tapa.size, style = Stroke(1.8f * u))
                drawRect(c(ORO, 0.6f).copy(alpha = 0.6f), tapa.deflate(6 * u).topLeft, tapa.deflate(6 * u).size, style = Stroke(0.8f * u))
                // La etiqueta pegada: el nombre a máquina, la cuenta y el signo del ojo.
                val et = Rect(tapa.left + tapa.width * 0.16f, tapa.top + tapa.height * 0.16f, tapa.right - tapa.width * 0.08f, tapa.top + tapa.height * 0.64f)
                val papel = c(PAPEL, 0.8f)
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(et.left + 1.5f * u, et.top + 2 * u), et.size)
                drawRect(papel, et.topLeft, et.size)
                drawRect(c(Color(0xFF8E2A22), 0.8f), Offset(et.left, et.top + et.height * 0.18f), Size(et.width, 0.8f * u))
                signoOjo(Offset(et.left + 15 * u, et.center.y + 4 * u), 9 * u, c(Color(0xFF2E5A3A), 0.8f))
                textoEn(medidor, e.nombre, Rect(et.left + 28 * u, et.top + et.height * 0.2f, et.right - 6 * u, et.top + et.height * 0.6f), letra,
                    legibleFan(papel, c(TINTA, 0.4f), PAPEL), 14.5f, peso = FontWeight.Bold, minimo = 7f)
                textoEn(medidor, e.cuenta, Rect(et.left + 28 * u, et.top + et.height * 0.58f, et.right - 6 * u, et.bottom - et.height * 0.08f), letra,
                    legibleFan(papel, c(TINTA, 0.4f), PAPEL).copy(alpha = 0.8f), 10f, minimo = 6.5f)
            } else {
                drawRect(c(Color(0xFFB8A57A), 0.6f), tapa.topLeft, tapa.size)
                clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) {
                    for (k in 0 until 26) {
                        val p0 = Offset(tapa.left + tapa.width * azar(s, 400 + k), tapa.top + tapa.height * azar(s, 430 + k))
                        drawOval(c(if (k % 3 == 0) Color(0xFF6A3A3A) else if (k % 3 == 1) Color(0xFF3A4A5A) else Color(0xFF8A7040), 0.6f).copy(alpha = 0.5f),
                            Offset(p0.x - 9 * u, p0.y - 4 * u), Size(18 * u, 8 * u), style = Stroke(2 * u))
                    }
                }
                drawRect(tela, tapa.topLeft, tapa.size, style = Stroke(3 * u))
            }
        }
        // El cordel en cruz: atado mientras está cerrado; al abrir, suelto sobre la mesa.
        val cordel = c(CORDEL, 0.6f)
        if (a < 0.08f) {
            drawLine(cordel, Offset(tapa.left - 2 * u, tapa.center.y + tapa.height * 0.32f), Offset(tapa.right + grosor * 0.4f, tapa.center.y + tapa.height * 0.32f), 1.6f * u)
            drawLine(cordel, Offset(tapa.left + tapa.width * 0.1f, tapa.top - 2 * u), Offset(tapa.left + tapa.width * 0.1f, tapa.bottom + grosor), 1.6f * u)
            val nudo = Offset(tapa.left + tapa.width * 0.1f, tapa.center.y + tapa.height * 0.32f)
            drawCircle(cordel, 3 * u, nudo, style = Stroke(1.4f * u))
            drawLine(cordel, nudo, nudo + Offset(-8 * u, 12 * u), 1.4f * u)
            drawLine(cordel, nudo, nudo + Offset(9 * u, 10 * u), 1.4f * u)
        } else {
            drawPath(Path().apply { moveTo(tapa.right + grosor * 0.4f, tapa.bottom - 8 * u); cubicTo(tapa.right + 30 * u, tapa.bottom + 10 * u, tapa.left + tapa.width * 0.6f, h * 0.84f, tapa.left + tapa.width * 0.3f, h * 0.81f) },
                cordel, style = Stroke(1.6f * u))
        }
        // Delante: las tijeras abiertas a la izquierda y la lupa a la derecha, con unos recortes sueltos.
        rotate(14f, Offset(w * 0.78f, h * 0.73f)) { recorteLib(Rect(w * 0.72f, h * 0.64f, w * 0.84f, h * 0.76f), c(PAPEL, 0.8f), c(TINTA, 0.8f), s + 70) }
        val hierro = c(Color(0xFF8A8A88), 0.4f)
        val eje = Offset(w * 0.1f, h * 0.74f)
        drawLine(hierro, eje, Offset(eje.x + 30 * u, eje.y - 14 * u), 2.4f * u, StrokeCap.Round)
        drawLine(hierro, eje, Offset(eje.x + 32 * u, eje.y - 4 * u), 2.4f * u, StrokeCap.Round)
        drawCircle(hierro, 6 * u, Offset(eje.x - 9 * u, eje.y + 2 * u), style = Stroke(2 * u))
        drawCircle(hierro, 6 * u, Offset(eje.x - 4 * u, eje.y + 10 * u), style = Stroke(2 * u))
        drawCircle(c(LATON, 0.4f), 1.6f * u, eje)
        val lupa = Offset(w * 0.8f, h * 0.71f); val rl = 15 * u
        drawLine(c(ROBLE_OSC, 0.9f), Offset(lupa.x + rl * 0.7f, lupa.y + rl * 0.7f), Offset(lupa.x + rl * 2.2f, lupa.y + rl * 1.5f), 5 * u, StrokeCap.Round)
        drawCircle(c(VIDRIO, 0.9f).copy(alpha = 0.35f), rl, lupa)
        clipPath(Path().apply { addOval(Rect(lupa, rl)) }) { recorteLib(Rect(lupa.x - rl * 1.3f, lupa.y - rl * 0.8f, lupa.x + rl * 0.6f, lupa.y + rl * 1.4f), c(PAPEL, 0.9f).copy(alpha = 0.6f), c(TINTA, 0.9f), s + 71) }
        drawCircle(c(LATON, 0.9f), rl, lupa, style = Stroke(2.6f * u))
        drawArc(Color.White.copy(alpha = 0.5f), 200f, 60f, false, Offset(lupa.x - rl * 0.7f, lupa.y - rl * 0.7f), Size(rl * 1.4f, rl * 1.4f), style = Stroke(1.4f * u))
    }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        // Un recorte de periódico de orilla rasgada, clavado con una chincheta del color del ánimo (de latón si no hay).
        val papel = lc(if ((s and 1) == 0) PAPEL else PAPEL_VIEJO, oscuro, 0.4f)
        val tinta = if (oscuro) PAPEL else TINTA
        rotate((azar(s, 1) - 0.5f) * 6f) {
            val r = Rect(w * 0.06f, h * 0.08f, w * 0.94f, h * 0.97f)
            val forma = contornoIrregular(r, s, 30, 0.025f)
            translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
            drawPath(forma, papel)
            clipPath(forma) {
                drawRect(tinta.copy(alpha = 0.6f), Offset(r.left + 3 * u, r.top + 3 * u), Size(r.width - 6 * u, 1.6f * u))
                var y = r.bottom - 10 * u
                while (y < r.bottom - 2 * u) { drawLine(tinta.copy(alpha = 0.25f), Offset(r.left + 3 * u, y), Offset(r.right - 3 * u, y), 0.6f * u); y += 2.2f * u }
            }
            textoEn(medidor, e.dia, Rect(r.left, r.top + 6 * u, r.right, r.top + r.height * 0.62f), letra, tinta, 20f, peso = FontWeight.Bold, minimo = 10f)
            textoEn(medidor, e.semana.uppercase(), Rect(r.left, r.top + r.height * 0.58f, r.right, r.top + r.height * 0.78f), letra, tinta.copy(alpha = 0.75f), 8.5f, espaciado = 1f, minimo = 6f)
        }
        val p = Offset(w * 0.5f + (azar(s, 2) - 0.5f) * 10 * u, h * 0.115f)
        drawCircle(Color.Black.copy(alpha = 0.35f), 5.8f * u, p + Offset(1.2f * u, 1.6f * u))
        drawCircle(e.tono ?: lc(LATON, oscuro, 0.5f), 5.4f * u, p)
        drawCircle(Color.Black.copy(alpha = 0.2f), 5.4f * u, p, style = Stroke(0.8f * u))
        drawCircle(Color.White.copy(alpha = 0.45f), 1.6f * u, p + Offset(-1.8f * u, -1.8f * u))
    }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Una tira de titular recortada del periódico, pegada con dos trozos de cinta amarillenta.
        val s = texto.hashCode()
        val papel = lc(PAPEL_VIEJO, oscuro, 0.4f)
        val tinta = legibleFan(papel, lc(TINTA, oscuro, 0.2f), PAPEL)
        rotate(-0.6f) {
            val r = Rect(w * 0.03f, 3 * u, w * 0.97f, h - 3 * u)
            val forma = contornoIrregular(r, s, 90, 0.04f)
            translate(2 * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
            drawPath(forma, papel)
            clipPath(forma) {
                drawLine(tinta.copy(alpha = 0.5f), Offset(r.left, r.top + 4 * u), Offset(r.right, r.top + 4 * u), 0.8f * u)
                drawLine(tinta.copy(alpha = 0.5f), Offset(r.left, r.bottom - 4 * u), Offset(r.right, r.bottom - 4 * u), 0.8f * u)
            }
            textoEn(medidor, texto.uppercase(), Rect(r.left + 30 * u, 0f, r.right - 30 * u, h), letra, tinta, 15f, peso = FontWeight.Bold, espaciado = 1f, minimo = 8f)
            val cinta = lc(ARENA, oscuro, 0.4f).copy(alpha = 0.55f)
            rotate(-25f, Offset(r.left + 10 * u, h / 2)) { drawRect(cinta, Offset(r.left, h / 2 - 5 * u), Size(22 * u, 10 * u)) }
            rotate(20f, Offset(r.right - 10 * u, h / 2)) { drawRect(cinta, Offset(r.right - 20 * u, h / 2 - 5 * u), Size(22 * u, 10 * u)) }
        }
    }

    // ---------------------------------------------------------------- las cartas

    private val MANILA = Color(0xFFC9B88A)
    private val MANILA_LUZ = Color(0xFFD9CA9E)
    private val MANILA_OSC = Color(0xFF8E7E57)
    private val FORRO = Color(0xFF2E4A3E)
    private val LACRE = Color(0xFF2C4034)
    private val CARTON = Color(0xFFB59A6A)
    private val SELLO_ROJO = Color(0xFF8A2E26)
    private val VERDE_RARO = Color(0xFFB8E07A)

    /** El color de las cartas con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra de la casa. */
    private fun cc(x: Color, oscuro: Boolean): Color = if (oscuro) mezcla(x, Color(0xFF0A0806), 0.55f) else x

    /**
     * El sello de la casa antigua, centrado en [c]: una gota de lacre verdinegro derramada sin cuidado, con dos goterones
     * que se corrieron, el aro hundido y el signo del ojo.
     */
    private fun DrawScope.selloArcano(c: Offset, r: Float, oscuro: Boolean) {
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0A0806), 0.25f) else x
        val gota = Path()
        for (j in 0..15) {
            val a = -Math.PI / 2 + j * 2 * Math.PI / 15
            val rr = r * (1f + 0.08f * sin(j * 2.3f) + if (j == 6) 0.34f else if (j == 10) 0.22f else 0f)
            val p = Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat())
            if (j == 0) gota.moveTo(p.x, p.y) else gota.lineTo(p.x, p.y)
        }
        gota.close()
        translate(r * 0.06f, r * 0.1f) { drawPath(gota, Color.Black.copy(alpha = 0.35f)) }
        drawPath(gota, k(LACRE))
        drawCircle(k(mezcla(LACRE, Color.Black, 0.4f)), r * 0.72f, c, style = Stroke(r * 0.07f))
        drawCircle(k(mezcla(LACRE, Color.White, 0.08f)), r * 0.64f, c)
        signoOjo(c, r * 0.9f, k(mezcla(LACRE, Color(0xFFCFE3C8), 0.55f)))
        drawOval(Color.White.copy(alpha = 0.2f), Offset(c.x - r * 0.55f, c.y - r * 0.62f), Size(r * 0.4f, r * 0.2f))
    }

    /** Un botón de cartón del cierre de hilo, con su ojal, centrado en [c]. */
    private fun DrawScope.botonCarton(c: Offset, r: Float, oscuro: Boolean) {
        drawCircle(Color.Black.copy(alpha = 0.3f), r, c + Offset(r * 0.12f, r * 0.18f))
        drawCircle(cc(CARTON, oscuro), r, c)
        drawCircle(cc(MANILA_OSC, oscuro), r * 0.8f, c, style = Stroke(r * 0.1f))
        drawCircle(cc(TINTA, oscuro), r * 0.16f, c)
    }

    /**
     * La carta de la casa antigua: el sobre de archivo de papel manila, con la solapa en pico y el cierre de hilo y botones
     * de cartón. Por fuera, el matasellos redondo que ya no se lee, un sello de goma rojo, el cerco de una taza y la etiqueta
     * escrita a máquina con a quién va y las fechas. Las de al futuro se cierran dando vueltas al hilo en ocho entre los dos
     * botones ([EstadoCarta.cordon]); en la punta de la solapa, la gota de lacre verdinegro con el ojo. Al abrirse, el lacre
     * se parte, la solapa se levanta mostrando el forro verde y sube la hoja de renglones con su margen rojo. Un degradado:
     * el brillo del sello (de un verde que no es de este mundo).
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val q = e.cuerpo
        val W = q.width; val H = q.height; val cx = q.center.x
        val ab = e.abrir.coerceIn(0f, 1f)
        val s = e.semilla
        fun c(x: Color) = cc(x, oscuro)
        val caja = Rect(q.left + W * 0.03f, q.top + H * 0.06f, q.right - W * 0.03f, q.bottom - H * 0.05f)
        val rad = CornerRadius(H * 0.02f)
        drawRoundRect(Color.Black.copy(alpha = 0.3f), caja.topLeft + Offset(2.5f * u, 4 * u), caja.size, rad)
        drawRoundRect(c(MANILA_OSC), caja.topLeft, caja.size, rad)
        val kk = 1f - 2f * ab
        val puntaCerrada = q.top + H * 0.45f
        val puntaY = caja.top + (puntaCerrada - caja.top) * kk
        val solapa = Path().apply {
            moveTo(caja.left, caja.top); lineTo(caja.left + W * 0.03f, caja.top + (puntaY - caja.top) * 0.18f)
            lineTo(cx - W * 0.03f, puntaY - (puntaY - caja.top) * 0.02f); quadraticTo(cx, puntaY + (puntaY - caja.top) * 0.03f, cx + W * 0.03f, puntaY - (puntaY - caja.top) * 0.02f)
            lineTo(caja.right - W * 0.03f, caja.top + (puntaY - caja.top) * 0.18f); lineTo(caja.right, caja.top); close()
        }
        if (ab > 0f) {
            // El forro verde del sobre por dentro, con su trama.
            val dentro = Path().apply { moveTo(caja.left, caja.top); lineTo(caja.right, caja.top); lineTo(cx, caja.top + H * 0.42f); close() }
            drawPath(dentro, c(FORRO))
            clipPath(dentro) { var x = caja.left - H; while (x < caja.right) { drawLine(c(mezcla(FORRO, Color.White, 0.15f)).copy(alpha = 0.5f), Offset(x, caja.top), Offset(x + H, caja.top + H), 1 * u); x += 5 * u } }
        }
        if (kk < 0f) {
            drawPath(solapa, c(FORRO))
            clipPath(solapa) { var x = caja.left - H; while (x < caja.right) { drawLine(c(mezcla(FORRO, Color.White, 0.15f)).copy(alpha = 0.5f), Offset(x, caja.top), Offset(x + H, puntaY), 1 * u); x += 5 * u } }
            drawPath(solapa, c(MANILA_OSC), style = Stroke(1.2f * u))
        }
        if (ab > 0.3f || e.subir > 0f) hojaCartaFan(q, e.subir, e.hoja, e.renglones, oscuro, 0.1f, 0.9f, margen = c(SELLO_ROJO).copy(alpha = 0.45f))
        // El frente: los dos lados y el fondo del sobre, con el escote en V.
        val frente = Path().apply {
            moveTo(caja.left, caja.top + H * 0.02f); lineTo(cx, caja.top + H * 0.44f); lineTo(caja.right, caja.top + H * 0.02f)
            lineTo(caja.right, caja.bottom); lineTo(caja.left, caja.bottom); close()
        }
        drawPath(frente, c(MANILA))
        clipPath(frente) {
            for (j in 0 until 20) drawCircle(c(MANILA_OSC).copy(alpha = 0.18f), (0.5f + 1.2f * azar(s, j)) * u, Offset(caja.left + caja.width * azar(s, j + 30), caja.top + caja.height * azar(s, j + 60)))
            // Las manchas del tiempo: el cerco de una taza y un sello de goma rojo, torcido.
            cercoTaza(Offset(caja.right - W * 0.12f, caja.bottom - H * 0.12f), H * 0.16f, c(TE).copy(alpha = 0.4f), s)
            rotate(-8f, Offset(caja.left + W * 0.14f, q.top + H * 0.5f)) {
                val sr = Rect(caja.left + W * 0.05f, q.top + H * 0.46f, caja.left + W * 0.24f, q.top + H * 0.56f)
                drawRect(c(SELLO_ROJO).copy(alpha = 0.45f), sr.topLeft, sr.size, style = Stroke(1.2f * u))
                drawLine(c(SELLO_ROJO).copy(alpha = 0.45f), Offset(sr.left + 3 * u, sr.center.y), Offset(sr.right - 3 * u, sr.center.y), 1.6f * u)
            }
        }
        drawLine(c(MANILA_OSC), Offset(caja.left, caja.top + H * 0.02f), Offset(cx, caja.top + H * 0.44f), 1 * u)
        drawLine(c(MANILA_OSC), Offset(cx, caja.top + H * 0.44f), Offset(caja.right, caja.top + H * 0.02f), 1 * u)
        drawLine(c(MANILA_OSC).copy(alpha = 0.6f), Offset(caja.left + 2 * u, caja.bottom - 2 * u), Offset(cx - W * 0.1f, caja.top + H * 0.5f), 0.8f * u)
        drawLine(c(MANILA_OSC).copy(alpha = 0.6f), Offset(caja.right - 2 * u, caja.bottom - 2 * u), Offset(cx + W * 0.1f, caja.top + H * 0.5f), 0.8f * u)
        // La etiqueta escrita a máquina.
        val etiqueta = Rect(caja.left + W * 0.08f, q.top + H * 0.62f, caja.right - W * 0.08f, q.top + H * 0.9f)
        val papel = c(PAPEL)
        rotate(-0.8f, etiqueta.center) {
            drawRect(Color.Black.copy(alpha = 0.25f), etiqueta.topLeft + Offset(1.2f * u, 1.6f * u), etiqueta.size)
            drawRect(papel, etiqueta.topLeft, etiqueta.size)
            drawLine(c(SELLO_ROJO).copy(alpha = 0.5f), Offset(etiqueta.left + 4 * u, etiqueta.top + 3 * u), Offset(etiqueta.right - 4 * u, etiqueta.top + 3 * u), 0.8f * u)
            val tinta = legibleFan(papel, c(TINTA), PAPEL)
            if (e.linea1.isBlank() && e.linea2.isBlank()) {
                for (j in 0 until 3) drawLine(c(TINTA).copy(alpha = 0.35f), Offset(etiqueta.left + 8 * u, etiqueta.top + etiqueta.height * (0.35f + 0.22f * j)), Offset(etiqueta.right - (8 + 30 * j) * u, etiqueta.top + etiqueta.height * (0.35f + 0.22f * j)), 1.2f * u)
            }
            val corte = etiqueta.top + etiqueta.height * 0.58f
            lineasCartaFan(medidor, e.linea1, e.linea2, Rect(etiqueta.left + 5 * u, etiqueta.top + 4 * u, etiqueta.right - 5 * u, corte),
                Rect(etiqueta.left + 6 * u, corte - 1 * u, etiqueta.right - 6 * u, etiqueta.bottom - 2.5f * u), Letra.castoro, tinta, 12f, 9.5f)
        }
        // El botón de abajo, en el cuerpo del sobre, a la derecha de la punta.
        val rB = minOf(H * 0.055f, 8 * u)
        val boton2 = Offset(cx + W * 0.3f, q.top + H * 0.5f)
        botonCarton(boton2, rB, oscuro)
        // La solapa cerrada (o levantándose): el manila con el matasellos que ya no se lee y el botón de arriba.
        if (kk >= 0f) {
            translate(0f, 2 * u) { drawPath(solapa, Color.Black.copy(alpha = 0.2f)) }
            drawPath(solapa, c(MANILA_LUZ))
            clipPath(solapa) {
                val m = Offset(caja.left + W * 0.2f, caja.top + (puntaY - caja.top) * 0.35f)
                val rm = H * 0.11f
                drawCircle(c(TINTA).copy(alpha = 0.3f), rm, m, style = Stroke(1.2f * u))
                drawCircle(c(TINTA).copy(alpha = 0.22f), rm * 0.7f, m, style = Stroke(0.8f * u))
                for (j in 0 until 4) drawLine(c(TINTA).copy(alpha = 0.2f), Offset(m.x + rm * 1.2f, m.y - rm * 0.5f + j * rm * 0.33f), Offset(m.x + rm * 3.4f, m.y - rm * 0.5f + j * rm * 0.33f), 1.3f * u)
            }
            drawPath(solapa, c(MANILA_OSC), style = Stroke(1 * u))
        }
        val boton1 = Offset(cx + W * 0.3f, caja.top + (puntaY - caja.top) * 0.42f)
        if (kk > 0.3f) botonCarton(boton1, rB, oscuro)
        // El hilo: vueltas en ocho entre los dos botones.
        if (e.cordon > 0f && kk > 0.3f) {
            val ruta = mutableListOf<Offset>()
            for (j in 0 until 3) {
                ruta += boton1 + Offset(-rB * 0.9f, 0f); ruta += boton2 + Offset(rB * 0.9f, 0f); ruta += boton2 + Offset(-rB * 0.9f, 0f); ruta += boton1 + Offset(rB * 0.9f, 0f)
            }
            ruta += boton1 + Offset(-rB * 0.9f, 0f); ruta += boton1 + Offset(-rB * 1.4f, rB * 2.6f)
            trazoParcialFan(ruta, e.cordon, c(mezcla(CORDEL, TINTA, 0.35f)), 1.4f * u)
        }
        val rS = minOf(H * 0.13f, W * 0.09f)
        val cS = Offset(cx, puntaCerrada)
        selloCartaFan(cS, rS, e.brillo, e.romper.coerceIn(0f, 1f), e.escalaSello, e.alfaSello, VERDE_RARO) { selloArcano(cS, rS, oscuro) }
    }

    override val cajaPrevia: Rect = Rect(0.15f, 0.175f, 0.85f, 0.345f)

    override val altoBuzon: Dp = 210.dp

    /** Un sobre chico dentro de un casillero, apoyado de lado, con su esquina de abajo en [o]: listo, cerrado con hilo o abierto. */
    private fun DrawScope.sobreChico(o: Offset, w: Float, h: Float, giro: Float, estado: Int, oscuro: Boolean, semilla: Int) {
        val u = density
        rotate(giro, o) {
            val r = Rect(o.x, o.y - h, o.x + w, o.y)
            if (estado == 2) {
                drawRect(cc(PAPEL, oscuro), Offset(r.left + w * 0.1f, r.top - h * 0.45f), Size(w * 0.8f, h * 0.8f))
                for (j in 0 until 3) drawLine(cc(TINTA, oscuro).copy(alpha = 0.4f), Offset(r.left + w * 0.18f, r.top - h * (0.32f - 0.14f * j)), Offset(r.right - w * 0.2f, r.top - h * (0.32f - 0.14f * j)), 0.8f * u)
            }
            drawRect(Color.Black.copy(alpha = 0.3f), r.topLeft + Offset(1.2f * u, 1.2f * u), r.size)
            drawRect(cc(if (semilla % 3 == 0) PAPEL_VIEJO else MANILA, oscuro), r.topLeft, r.size)
            val pico = Path().apply { moveTo(r.left, r.top); lineTo(o.x + w / 2, r.top + h * (if (estado == 2) 0.15f else 0.55f)); lineTo(r.right, r.top) }
            drawPath(pico, cc(MANILA_OSC, oscuro), style = Stroke(0.8f * u))
            if (estado != 2) {
                val cs = Offset(o.x + w / 2, r.top + h * 0.55f)
                if (estado == 1) {
                    drawCircle(cc(CARTON, oscuro), h * 0.09f, Offset(r.right - w * 0.18f, r.top + h * 0.35f))
                    drawLine(cc(CORDEL, oscuro), Offset(r.right - w * 0.18f, r.top + h * 0.35f), Offset(r.right - w * 0.18f, r.bottom - h * 0.12f), 0.9f * u)
                    drawCircle(cc(CARTON, oscuro), h * 0.09f, Offset(r.right - w * 0.18f, r.bottom - h * 0.16f))
                }
                drawCircle(cc(LACRE, oscuro), h * 0.15f, cs)
                drawCircle(cc(mezcla(LACRE, Color.White, 0.3f), oscuro), h * 0.08f, cs, style = Stroke(0.6f * u))
            }
        }
    }

    /**
     * El buzón de la casa antigua: el casillero de roble de la portería de una vieja pensión, contra el papel tapiz de rombos.
     * Tres columnas de casillas, cada una con su placa de latón atornillada arriba (el nombre y la cuenta grabados) y dos casillas
     * hondas donde se apoyan los sobres de archivo: a la izquierda los que ya se pueden abrir, con un resplandor verde que no es
     * de este mundo; en medio los cerrados con hilo y lacre; a la derecha los abiertos, con la carta afuera. Abajo, el
     * mostrador con el timbre de latón, una llave colgada de su gancho y el libro de registro. En modo oscuro, la portería se
     * apaga. Un degradado: el resplandor de las listas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = cc(x, oscuro)
        // El papel tapiz de rombos.
        drawRect(c(TAPIZ))
        var x = -h
        while (x < w) {
            drawLine(c(TAPIZ_LINEA).copy(alpha = 0.6f), Offset(x, 0f), Offset(x + h, h), 0.7f * u)
            drawLine(c(TAPIZ_LINEA).copy(alpha = 0.6f), Offset(x + h, 0f), Offset(x, h), 0.7f * u)
            x += 18 * u
        }
        // El casillero: el marco de roble con su cornisa.
        val mueble = Rect(w * 0.04f, h * 0.05f, w * 0.96f, h * 0.8f)
        drawRect(Color.Black.copy(alpha = 0.35f), mueble.topLeft + Offset(4 * u, 4 * u), mueble.size)
        drawRect(c(ROBLE), mueble.topLeft, mueble.size)
        vetas(mueble, c(ROBLE_VETA).copy(alpha = 0.5f), 333, densidad = 0.5f)
        drawRect(c(ROBLE_LUZ), Offset(mueble.left - 4 * u, mueble.top - 4 * u), Size(mueble.width + 8 * u, 7 * u))
        drawRect(c(ROBLE_OSC), Offset(mueble.left - 4 * u, mueble.top + 3 * u), Size(mueble.width + 8 * u, 2 * u))
        val zw = mueble.width / 3f
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        val nombres = listOf("PARA ABRIR", "CERRADAS", "ABIERTAS")
        val filas = listOf(h * 0.27f to h * 0.52f, h * 0.54f to h * 0.78f)
        for (g in 0 until 3) {
            val x0 = mueble.left + zw * g
            // La placa de latón con su nombre y su cuenta.
            val placa = Rect(x0 + zw * 0.12f, h * 0.11f, x0 + zw * 0.88f, h * 0.24f)
            drawRoundRect(Color.Black.copy(alpha = 0.35f), placa.topLeft + Offset(1.2f * u, 1.6f * u), placa.size, CornerRadius(2 * u))
            drawRoundRect(c(LATON), placa.topLeft, placa.size, CornerRadius(2 * u))
            drawRoundRect(c(LATON_OSC), placa.topLeft, placa.size, CornerRadius(2 * u), style = Stroke(1 * u))
            listOf(placa.left + 4 * u, placa.right - 4 * u).forEach { tx -> drawCircle(c(LATON_OSC), 1.5f * u, Offset(tx, placa.center.y)) }
            val grabado = legibleFan(c(LATON), c(TINTA), CERA)
            textoEn(medidor, nombres[g], Rect(placa.left + 6 * u, placa.top + 1 * u, placa.right - 6 * u, placa.top + placa.height * 0.46f), Letra.castoro, grabado, 8.5f, peso = FontWeight.Bold, espaciado = 0.2f, minimo = 5.5f)
            textoEn(medidor, cuentas[g].toString(), Rect(placa.left + 8 * u, placa.top + placa.height * 0.4f, placa.right - 8 * u, placa.bottom - 1 * u), Letra.castoro, grabado, 15f, peso = FontWeight.Bold, minimo = 7f)
            // Las dos casillas hondas, con su sombra adentro y los sobres apoyados.
            val n = minOf(cuentas[g], 4)
            filas.forEachIndexed { f, (y0, y1) ->
                val hueco = Rect(x0 + 5 * u, y0, x0 + zw - 5 * u, y1)
                drawRect(c(ROBLE_OSC), hueco.topLeft, hueco.size)
                drawRect(Color.Black.copy(alpha = 0.4f), hueco.topLeft, Size(hueco.width, 6 * u))
                drawRect(Color.Black.copy(alpha = 0.25f), hueco.topLeft, Size(5 * u, hueco.height))
                if (g == 0 && n > f * 2) mancha(Offset(hueco.center.x, hueco.bottom - 10 * u), hueco.width * 0.7f, OJOS.copy(alpha = if (oscuro) 0.55f else 0.4f))
                clipRect(hueco.left, hueco.top - 30 * u, hueco.right, hueco.bottom) {
                    for (i in 0 until 2) {
                        val k = f * 2 + i
                        if (k >= n) continue
                        val sw = minOf(hueco.width * 0.62f, 52 * u)
                        sobreChico(Offset(hueco.left + 6 * u + i * hueco.width * 0.28f, hueco.bottom - 2 * u), sw, sw * 0.6f, -12f + 16f * i, g, oscuro, 5 * g + k)
                    }
                }
                drawRect(c(ROBLE_LUZ), Offset(hueco.left - 5 * u, hueco.bottom), Size(hueco.width + 10 * u, 3 * u))
            }
            if (g < 2) drawRect(c(ROBLE_LUZ).copy(alpha = 0.5f), Offset(x0 + zw - 1 * u, h * 0.26f), Size(2 * u, mueble.bottom - h * 0.26f))
        }
        // El mostrador, con el timbre, la llave y el libro de registro.
        drawRect(c(ROBLE_OSC), Offset(0f, h * 0.84f), Size(w, h * 0.16f))
        drawRect(c(ROBLE_LUZ), Offset(0f, h * 0.82f), Size(w, 4 * u))
        val tb = Offset(w * 0.84f, h * 0.82f)
        drawArc(c(LATON), 180f, 180f, true, Offset(tb.x - 9 * u, tb.y - 9 * u), Size(18 * u, 18 * u))
        drawRect(c(LATON_OSC), Offset(tb.x - 11 * u, tb.y - 1.5f * u), Size(22 * u, 2 * u))
        drawCircle(c(LATON), 2 * u, Offset(tb.x, tb.y - 10 * u))
        drawRect(c(TELAS[0]), Offset(w * 0.1f, h * 0.82f - 6 * u), Size(46 * u, 6 * u))
        drawRect(c(PAPEL_VIEJO), Offset(w * 0.1f + 2 * u, h * 0.82f - 4.5f * u), Size(42 * u, 1.5f * u))
        val ll = Offset(w * 0.5f, h * 0.83f)
        drawCircle(c(LATON_OSC), 2 * u, Offset(ll.x, ll.y + 2 * u))
        drawCircle(c(LATON), 4 * u, Offset(ll.x, ll.y + 8 * u), style = Stroke(1.4f * u))
        drawLine(c(LATON), Offset(ll.x, ll.y + 12 * u), Offset(ll.x, ll.y + 24 * u), 1.6f * u)
        drawLine(c(LATON), Offset(ll.x, ll.y + 21 * u), Offset(ll.x + 4 * u, ll.y + 21 * u), 1.4f * u)
    }
}
/**
 * Tierras Antiguas: la biblioteca de la torre del camino, de madera tallada contra la pared de tablones. Los costados son
 * postes con una talla de soga y sus nudos; las tablas, gruesas, con una hilera de muescas talladas en el canto, y el mes va
 * en una tablilla clavada con letras quemadas. Arriba, una viga tallada de anillos entrelazados con sus remates, un mapa de
 * pergamino clavado en la pared (montes, río, bosque, el camino punteado hasta su marca y la rosa de los vientos) y un farol
 * de hierro colgado de su brazo. Abajo, el zócalo tallado, las patas y el piso de losas. Cada entrada es un tomo grueso de
 * cuero con esquinas de latón, una gema engastada en el lomo (de noche, una piedra de luna), la placa de latón con el día,
 * el mes en dorado, el broche y la cinta de marcar, que asoma más cuanto más escribiste. De noche se enciende el farol. La
 * tapa es ese tomo grande y de frente: el cuero con sus esquinas de latón, el marco dorado con su cadena de anillos, la gema
 * (de noche, la piedra de luna que alumbra), la placa con la fecha, el recuadro de pergamino para el extracto y el broche por
 * el canto; al abrirse, el broche se suelta y la tapa gira y deja ver el mapa del camino. Distinto del Ideario, que es la
 * sala de sillares con runas, los dos faroles y los arcones de la compañía.
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

    // ---------------------------------------------------------------- la tapa

    override val altoTapa: Dp = 188.dp
    override val cajaTapa = Rect(0.2f, 0.355f, 0.8f, 0.715f)
    override val letraTapa: FontFamily = LetraExtra.almendra
    override val tamTapa = 13.5f
    override val lineasTapa = 3
    override val centradoTapa = true

    /** El pergamino del recuadro, donde va el extracto: de noche lo alumbra la piedra de luna, así que se hunde menos. */
    private fun pergaminoTapa(oscuro: Boolean, noche: Boolean): Color {
        var r = PERGAMINO
        if (oscuro) r = mezcla(r, Color(0xFF0D0906), 0.28f)
        if (noche) r = mezcla(r, NOCHE, if (oscuro) 0.45f else 0.3f)
        return r
    }

    override fun tintaTapa(oscuro: Boolean, noche: Boolean): Color = legibleFan(pergaminoTapa(oscuro, noche), TINTA, PERGAMINO)
    override fun palabrasTapa(n: Int): String = palabrasFan(n)

    /** La tapa, recortada a su lienzo: lo que se aparta al abrir y la luz de noche no se salen de su lugar. */
    override fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) = clipRect { dibujarTapa(e, medidor, oscuro) }

    private fun DrawScope.dibujarTapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, e.noche)
        val a = e.abierto.coerceIn(0f, 1f)
        val sem = e.semilla and 0x7fffffff
        val cuero = c(CUEROS[sem % CUEROS.size])
        val lleno = (e.palabras / 800f).coerceIn(0f, 1f)
        val tapa = Rect(w * 0.02f, h * 0.04f, w * 0.91f, h * 0.935f)
        val lomo = w * 0.075f
        // El bloque de las hojas, gruesas, con sus cantos, y la cinta de marcar que asoma por abajo (más cuanto más escribiste).
        val bloque = Rect(tapa.left + lomo * 0.5f, tapa.top + h * 0.03f, w * 0.95f, h * 0.965f)
        drawRect(c(CERA), bloque.topLeft, bloque.size)
        for (j in 1..6) {
            drawLine(c(PERGAMINO_OSC).copy(alpha = 0.5f), Offset(bloque.left, bloque.bottom - j * 1.5f * u), Offset(bloque.right, bloque.bottom - j * 1.5f * u), 0.5f * u)
            drawLine(c(PERGAMINO_OSC).copy(alpha = 0.5f), Offset(bloque.right - j * 1.5f * u, bloque.top), Offset(bloque.right - j * 1.5f * u, bloque.bottom), 0.5f * u)
        }
        val cinta = c(CINTAS[(sem / 3) % CINTAS.size])
        val cx = w * 0.3f
        val fin = minOf(h, bloque.bottom + h * (0.012f + 0.022f * lleno) + 2 * u)
        drawPath(Path().apply {
            moveTo(cx - 2.4f * u, bloque.bottom - 2 * u); lineTo(cx + 2.4f * u, bloque.bottom - 2 * u); lineTo(cx + 2.4f * u, fin)
            lineTo(cx, fin - 2.4f * u); lineTo(cx - 2.4f * u, fin); close()
        }, cinta)
        // La primera hoja (se ve al abrir): el mapa del camino, con sus montes, el río, el bosque, la marca y la rosa de los vientos.
        if (a > 0.01f) {
            val pag = Rect(tapa.left + lomo, tapa.top + 2 * u, bloque.right - 3 * u, bloque.bottom - 3 * u)
            drawRect(c(PERGAMINO), pag.topLeft, pag.size)
            drawRect(c(PERGAMINO_OSC).copy(alpha = 0.7f), pag.deflate(5 * u).topLeft, pag.deflate(5 * u).size, style = Stroke(0.8f * u))
            for (j in 0 until 5) {
                val b = Offset(pag.left + pag.width * (0.12f + 0.07f * j), pag.top + pag.height * (0.3f + 0.04f * (j % 2)))
                val m = pag.height * (0.1f + 0.03f * (j % 3))
                drawPath(Path().apply { moveTo(b.x - m * 0.7f, b.y + m * 0.5f); lineTo(b.x, b.y - m * 0.5f); lineTo(b.x + m * 0.7f, b.y + m * 0.5f) }, c(TINTA).copy(alpha = 0.7f), style = Stroke(1f * u))
                drawLine(c(TINTA).copy(alpha = 0.4f), Offset(b.x, b.y - m * 0.5f), Offset(b.x + m * 0.2f, b.y + m * 0.3f), 0.7f * u)
            }
            val rio = Path().apply {
                moveTo(pag.left + pag.width * 0.08f, pag.bottom - pag.height * 0.2f)
                cubicTo(pag.left + pag.width * 0.3f, pag.bottom - pag.height * 0.45f, pag.left + pag.width * 0.45f, pag.bottom - pag.height * 0.05f, pag.left + pag.width * 0.7f, pag.bottom - pag.height * 0.3f)
                quadraticTo(pag.left + pag.width * 0.85f, pag.bottom - pag.height * 0.45f, pag.right - pag.width * 0.04f, pag.bottom - pag.height * 0.4f)
            }
            drawPath(rio, c(AZUL_MAPA), style = Stroke(1.8f * u, cap = StrokeCap.Round))
            for (j in 0 until 8) drawCircle(c(VERDE_MAPA), (3 + 1.5f * azar(e.semilla, 600 + j)) * u, Offset(pag.left + pag.width * (0.5f + 0.05f * (j % 4)), pag.top + pag.height * (0.22f + 0.07f * (j / 4))))
            val camino = Path().apply {
                moveTo(pag.left + pag.width * 0.15f, pag.bottom - pag.height * 0.12f)
                cubicTo(pag.left + pag.width * 0.35f, pag.top + pag.height * 0.55f, pag.left + pag.width * 0.55f, pag.top + pag.height * 0.75f, pag.left + pag.width * 0.72f, pag.top + pag.height * 0.42f)
            }
            drawPath(camino, c(ROJO_MAPA), style = Stroke(1.2f * u, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u))))
            val x0 = Offset(pag.left + pag.width * 0.72f, pag.top + pag.height * 0.42f)
            drawLine(c(ROJO_MAPA), x0 + Offset(-3 * u, -3 * u), x0 + Offset(3 * u, 3 * u), 1.6f * u)
            drawLine(c(ROJO_MAPA), x0 + Offset(-3 * u, 3 * u), x0 + Offset(3 * u, -3 * u), 1.6f * u)
            val rosa = Offset(pag.right - pag.width * 0.12f, pag.top + pag.height * 0.2f)
            drawPath(estrella(rosa, pag.height * 0.11f, 8, 0.3f), c(TINTA).copy(alpha = 0.65f), style = Stroke(0.8f * u))
            drawPath(estrella(rosa, pag.height * 0.11f, 4, 0.22f), c(TINTA).copy(alpha = 0.6f))
        }
        // La tapa de cuero gira sobre el lomo y deja ver el mapa.
        withTransform({ scale(1f - 0.92f * a, 1f, Offset(tapa.left, tapa.center.y)) }) {
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left + 2 * u, tapa.top + 3 * u), tapa.size)
            drawRoundRect(cuero, tapa.topLeft, tapa.size, CornerRadius(4 * u))
            vetas(tapa.deflate(2 * u), Color.Black.copy(alpha = 0.14f), sem + 5, densidad = 0.35f, grosor = 0.6f, ondulacion = 2.4f)
            drawRect(Color.White.copy(alpha = 0.07f), Offset(tapa.left + lomo, tapa.top), Size(tapa.width * 0.06f, tapa.height))
            // El lomo, con sus nervios.
            drawRect(mezcla(cuero, Color.Black, 0.3f), tapa.topLeft, Size(lomo, tapa.height))
            listOf(0.16f, 0.38f, 0.62f, 0.84f).forEach { f ->
                val ny = tapa.top + tapa.height * f
                drawRect(Color.Black.copy(alpha = 0.3f), Offset(tapa.left, ny - 2.4f * u), Size(lomo, 4.8f * u))
                drawRect(Color.White.copy(alpha = 0.1f), Offset(tapa.left, ny - 2.4f * u), Size(lomo, 1.2f * u))
                drawLine(c(LATON_LUZ), Offset(tapa.left + 2 * u, ny - 3.2f * u), Offset(tapa.left + lomo - 2 * u, ny - 3.2f * u), 0.6f * u)
            }
            drawLine(Color.Black.copy(alpha = 0.45f), Offset(tapa.left + lomo, tapa.top), Offset(tapa.left + lomo, tapa.bottom), 1.4f * u)
            // El marco gofrado y dorado, doble, con la cadena de anillos entrelazados arriba, a los lados de la gema.
            val marco = Rect(tapa.left + lomo + 7 * u, tapa.top + 7 * u, tapa.right - 7 * u, tapa.bottom - 7 * u)
            drawRect(c(LATON_OSC).copy(alpha = 0.8f), marco.topLeft, marco.size, style = Stroke(1.2f * u))
            drawRect(c(LATON).copy(alpha = 0.45f), marco.deflate(3.5f * u).topLeft, marco.deflate(3.5f * u).size, style = Stroke(0.7f * u))
            val gc = Offset(w * 0.5f, h * 0.135f)
            val ra = h * 0.022f
            listOf(marco.left + 12 * u to w * 0.43f, w * 0.57f to marco.right - 12 * u).forEach { (x0, x1) ->
                var xx = x0 + ra
                while (xx < x1 - ra) { drawCircle(c(LATON_OSC).copy(alpha = 0.85f), ra, Offset(xx, gc.y), style = Stroke(1.1f * u)); xx += ra * 1.45f }
            }
            // Las esquinas de latón con sus remaches.
            val k = h * 0.13f
            listOf(Offset(tapa.left + lomo, tapa.top) to Offset(1f, 1f), Offset(tapa.right, tapa.top) to Offset(-1f, 1f),
                Offset(tapa.left + lomo, tapa.bottom) to Offset(1f, -1f), Offset(tapa.right, tapa.bottom) to Offset(-1f, -1f)).forEach { (o, s) ->
                val esquina = Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * k, o.y); lineTo(o.x, o.y + s.y * k); close() }
                drawPath(esquina, c(LATON))
                drawPath(Path().apply { moveTo(o.x + s.x * k, o.y); lineTo(o.x, o.y + s.y * k) }, c(LATON_OSC), style = Stroke(1 * u))
                drawLine(c(LATON_LUZ).copy(alpha = 0.7f), Offset(o.x + s.x * 1.5f * u, o.y + s.y * 1.5f * u), Offset(o.x + s.x * k * 0.7f, o.y + s.y * 1.5f * u), 0.8f * u)
                drawCircle(c(LATON_OSC), 1.4f * u, Offset(o.x + s.x * k * 0.28f, o.y + s.y * k * 0.28f))
                drawCircle(c(LATON_LUZ).copy(alpha = 0.6f), 0.5f * u, Offset(o.x + s.x * k * 0.28f - 0.4f * u, o.y + s.y * k * 0.28f - 0.4f * u))
            }
            // La gema engastada arriba; de noche, una piedra de luna que alumbra el pergamino (el único degradado).
            val gr = h * 0.045f
            drawCircle(c(LATON), gr * 1.35f, gc)
            drawCircle(c(LATON_OSC), gr * 1.35f, gc, style = Stroke(0.8f * u))
            for (j in 0 until 8) {
                val ang = Math.toRadians(j * 45.0)
                drawCircle(c(LATON_LUZ), 1 * u, Offset(gc.x + cos(ang).toFloat() * gr * 1.18f, gc.y + sin(ang).toFloat() * gr * 1.18f))
            }
            if (e.noche) {
                clipRect(tapa.left, tapa.top, tapa.right, tapa.bottom) { mancha(gc, h * 0.7f, LUNA.copy(alpha = 0.2f)) }
                drawCircle(LUNA, gr, gc)
                drawCircle(mezcla(LUNA, NOCHE, 0.3f), gr * 0.82f, Offset(gc.x + gr * 0.4f, gc.y - gr * 0.25f))
            } else {
                val gema = c(GEMAS[(sem / 7) % GEMAS.size])
                val talla = Path().apply {
                    for (j in 0 until 6) {
                        val ang = Math.toRadians(-90.0 + j * 60.0)
                        val p = Offset(gc.x + cos(ang).toFloat() * gr, gc.y + sin(ang).toFloat() * gr)
                        if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                    close()
                }
                drawPath(talla, gema)
                drawPath(estrella(gc, gr * 0.55f, 6, 0.6f), Color.White.copy(alpha = 0.2f))
                drawCircle(Color.White.copy(alpha = 0.6f), gr * 0.22f, Offset(gc.x - gr * 0.35f, gc.y - gr * 0.35f))
            }
            // La placa de latón con la fecha.
            val placa = Rect(w * 0.26f, h * 0.205f, w * 0.74f, h * 0.305f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(placa.left + 1 * u, placa.top + 1.5f * u), placa.size)
            drawRect(c(LATON), placa.topLeft, placa.size)
            drawRect(c(LATON_LUZ), placa.topLeft, Size(placa.width, 1.2f * u))
            drawRect(c(LATON_OSC), placa.topLeft, placa.size, style = Stroke(0.9f * u))
            listOf(placa.left + 3.5f * u, placa.right - 3.5f * u).forEach { x -> drawCircle(c(LATON_OSC), 1.3f * u, Offset(x, placa.center.y)) }
            textoEn(medidor, e.fecha, Rect(placa.left + 7 * u, placa.top + 1 * u, placa.right - 7 * u, placa.bottom - 1 * u), letra,
                legibleFan(c(LATON), c(TINTA), LATON_LUZ), 13f, minimo = 7f)
            // El recuadro de pergamino, con su marco de latón y sus clavos, donde va el extracto.
            val recuadro = Rect(w * 0.18f, h * 0.335f, w * 0.82f, h * 0.735f)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(recuadro.left + 1.5f * u, recuadro.top + 2 * u), recuadro.size)
            drawRect(pergaminoTapa(oscuro, e.noche), recuadro.topLeft, recuadro.size)
            drawRect(c(PERGAMINO_OSC).copy(alpha = 0.35f), Offset(recuadro.left, recuadro.bottom - 3 * u), Size(recuadro.width, 3 * u))
            drawRect(c(LATON_OSC), recuadro.topLeft, recuadro.size, style = Stroke(1.6f * u))
            listOf(recuadro.topLeft, Offset(recuadro.right, recuadro.top), Offset(recuadro.left, recuadro.bottom), recuadro.bottomRight).forEach { p ->
                drawCircle(c(LATON), 2.2f * u, p)
                drawCircle(c(LATON_OSC), 2.2f * u, p, style = Stroke(0.6f * u))
            }
            // Las palabras, gofradas en dorado sobre el cuero.
            if (e.palabras > 0) {
                val oro = mezcla(mezcla(LATON_LUZ, NOCHE, if (e.noche) 0.2f else 0f), Color.Black, if (oscuro) 0.15f else 0f)
                textoEn(medidor, palabrasTapa(e.palabras), Rect(w * 0.22f, h * 0.755f, w * 0.78f, h * 0.835f), letra, oro, 11f, espaciado = 0.4f, minimo = 7f)
            }
            // El broche: la correa de cuero con su placa de latón y su gema, que cierra por el canto (se suelta al abrir).
            val suelta = (1f - a * 2.5f).coerceIn(0f, 1f)
            if (suelta > 0f) {
                val correa = Rect(tapa.right - w * 0.06f, h * 0.44f, tapa.right + w * 0.05f, h * 0.56f)
                drawRect(c(MADERA_OSC).copy(alpha = suelta), correa.topLeft, correa.size)
                drawLine(Color.White.copy(alpha = 0.1f * suelta), Offset(correa.left, correa.top + 1 * u), Offset(correa.right, correa.top + 1 * u), 0.6f * u)
                val broche = Rect(tapa.right - w * 0.03f, h * 0.42f, tapa.right + w * 0.02f, h * 0.58f)
                drawRoundRect(c(LATON).copy(alpha = suelta), broche.topLeft, broche.size, CornerRadius(2 * u))
                drawRoundRect(c(LATON_OSC).copy(alpha = suelta), broche.topLeft, broche.size, CornerRadius(2 * u), style = Stroke(0.8f * u))
                drawCircle(c(GEMAS[(sem / 5) % GEMAS.size]).copy(alpha = suelta), broche.width * 0.28f, broche.center)
                drawCircle(Color.White.copy(alpha = 0.5f * suelta), broche.width * 0.08f, Offset(broche.center.x - broche.width * 0.08f, broche.center.y - broche.width * 0.08f))
            }
            // Al girar, la tapa se oscurece.
            drawRect(Color.Black.copy(alpha = 0.45f * a), tapa.topLeft, tapa.size)
        }
    }

    // ---------------------------------------------------------------- la página y el escritor

    override val altoEncabezado: Dp = 120.dp

    /** Una rosa de los vientos de ocho puntas, las cardinales largas y mitad llenas, con su anillo, en [c] y de radio [r]. */
    private fun DrawScope.rosa(c: Offset, r: Float, tinta: Color, relleno: Color) {
        val u = density
        drawCircle(tinta, r * 0.62f, c, style = Stroke(0.8f * u))
        for (k in 0 until 8) {
            val a = Math.toRadians(-90.0 + 45.0 * k)
            val largo = if (k % 2 == 0) r else r * 0.55f
            val ancho = r * (if (k % 2 == 0) 0.16f else 0.12f)
            val punta = Offset(c.x + largo * cos(a).toFloat(), c.y + largo * sin(a).toFloat())
            val izq = Offset(c.x + ancho * cos(a - Math.PI / 2).toFloat(), c.y + ancho * sin(a - Math.PI / 2).toFloat())
            val der = Offset(c.x + ancho * cos(a + Math.PI / 2).toFloat(), c.y + ancho * sin(a + Math.PI / 2).toFloat())
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(izq.x, izq.y); lineTo(punta.x, punta.y); close() }, relleno)
            drawPath(Path().apply { moveTo(izq.x, izq.y); lineTo(punta.x, punta.y); lineTo(der.x, der.y); lineTo(c.x, c.y); close() }, tinta, style = Stroke(0.7f * u))
        }
        drawCircle(tinta, r * 0.07f, c)
    }

    /** La torre del camino: el cuerpo de piedra con sus almenas, la puerta en arco y una ventana, de pie en [pie]. */
    private fun DrawScope.torre(pie: Offset, alto: Float, color: Color, hueco: Color) {
        val a = alto * 0.36f
        drawRect(color, Offset(pie.x - a / 2, pie.y - alto * 0.8f), Size(a, alto * 0.8f))
        for (k in 0 until 3) drawRect(color, Offset(pie.x - a / 2 - a * 0.08f + k * a * 0.43f, pie.y - alto), Size(a * 0.22f, alto * 0.22f))
        drawRect(color, Offset(pie.x - a / 2 - a * 0.08f, pie.y - alto * 0.82f), Size(a * 1.16f, alto * 0.06f))
        drawRoundRect(hueco, Offset(pie.x - a * 0.17f, pie.y - alto * 0.26f), Size(a * 0.34f, alto * 0.26f), CornerRadius(a * 0.17f))
        drawRect(hueco, Offset(pie.x - a * 0.06f, pie.y - alto * 0.6f), Size(a * 0.12f, alto * 0.14f))
        drawLine(color, Offset(pie.x - alto * 0.4f, pie.y), Offset(pie.x + alto * 0.4f, pie.y), alto * 0.03f)
    }

    /**
     * El encabezado: la filacteria de la crónica, una cinta roja con las puntas dobladas hacia atrás y cortadas en cola de
     * golondrina, con el rótulo en letras claras; debajo, la fecha grande y el detalle, entre la rosa de los vientos (de noche,
     * la luna con sus estrellas) a la izquierda y la torre del camino a la derecha; abajo, el camino punteado que va de una a
     * otra.
     */
    override fun DrawScope.encabezado(e: EstadoEncabezado, p: Pagina, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cinta = if (p.oscura) mezcla(p.color, p.acento, 0.32f) else tonoFan(p.acento, p, oscuro)
        val doblez = mezcla(cinta, Color.Black, 0.35f)
        val letraCinta = if (p.oscura) p.tinta else PERGAMINO
        val c0 = w * 0.13f; val c1 = w * 0.87f
        val top = 6 * u; val bot = 30 * u
        // Las colas, más abajo y detrás, con su corte en V y el doblez.
        listOf(-1f, 1f).forEach { s ->
            val dentro = if (s < 0) c0 + 10 * u else c1 - 10 * u
            val fuera = if (s < 0) 2 * u else w - 2 * u
            val cola = Path().apply {
                moveTo(dentro, top + 6 * u); lineTo(fuera, top + 6 * u); lineTo(fuera - s * 9 * u, (top + bot) / 2 + 6 * u)
                lineTo(fuera, bot + 6 * u); lineTo(dentro, bot + 6 * u); close()
            }
            drawPath(cola, Color.Black.copy(alpha = 0.18f), style = Stroke(2 * u))
            drawPath(cola, mezcla(cinta, Color.Black, 0.15f))
            val pliegue = Path().apply {
                val borde = if (s < 0) c0 else c1
                moveTo(borde, bot); lineTo(dentro, bot + 6 * u); lineTo(borde, bot + 6 * u); close()
            }
            drawPath(pliegue, doblez)
        }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(c0 + 1.5f * u, top + 2 * u), Size(c1 - c0, bot - top))
        drawRect(cinta, Offset(c0, top), Size(c1 - c0, bot - top))
        if (e.noche) clipRect(c0, top, c1, bot) { mancha(Offset(w / 2, top), w * 0.4f, LUNA.copy(alpha = 0.15f)) }
        listOf(top + 2.5f * u, bot - 2.5f * u).forEach { y -> drawLine(letraCinta.copy(alpha = 0.4f), Offset(c0 + 4 * u, y), Offset(c1 - 4 * u, y), 0.7f * u) }
        textoEn(medidor, e.rotulo, Rect(c0 + 8 * u, top + 3 * u, c1 - 8 * u, bot - 3 * u), if (e.noche) LetraExtra.almendra else LetraExtra.camino,
            letraCinta, 13f, espaciado = 0.8f, minimo = 8f)
        // La rosa (o la luna) y la torre.
        val tinta = p.tinta.copy(alpha = 0.8f)
        val rc = Offset(26 * u, 72 * u)
        if (e.noche) {
            drawCircle(LUNA, 12 * u, rc)
            drawCircle(mezcla(p.color, p.tinta, 0.05f), 10 * u, Offset(rc.x + 5 * u, rc.y - 4 * u))
            for (k in 0 until 4) {
                val s = Offset(rc.x + (if (k % 2 == 0) -15f else 15f) * u, rc.y + (k - 1.5f) * 9 * u)
                if (k % 2 == 0) drawPath(estrella(s, 3 * u, 4, 0.35f), LUNA) else drawCircle(LUNA, 1.2f * u, s)
            }
        } else {
            rosa(rc, 20 * u, tinta, (if (p.oscura) p.acento else tonoFan(ROJO_MAPA, p, oscuro)).copy(alpha = 0.8f))
        }
        torre(Offset(w - 26 * u, 92 * u), 40 * u, (if (p.oscura) p.tinta else tonoFan(PIEDRA, p, oscuro)).copy(alpha = 0.85f),
            if (p.oscura) p.color else (if (e.noche) LLAMA else TINTA).copy(alpha = 0.85f))
        val izq = 52 * u
        val der = w - 52 * u
        fechaEncFan(medidor, e.fecha, Rect(izq - 4 * u, 35 * u, der + 4 * u, 77 * u), LetraExtra.almendra, p.tinta, 23f, 22, FontWeight.Bold)
        textoEn(medidor, e.detalle, Rect(izq - 6 * u, 77 * u, der + 6 * u, 92 * u), LetraExtra.vollkorn, p.tenue, 11f, minimo = 7f)
        // El camino punteado de la rosa a la torre, ondulado.
        val camino = p.acento.copy(alpha = 0.6f)
        var x = rc.x + 4 * u
        while (x < w - 40 * u) {
            val f = (x - rc.x) / (w - 40 * u - rc.x)
            val y = 104 * u + 5 * u * sin(f * 9.4f) - 6 * u * f
            drawLine(camino, Offset(x, y), Offset(x + 3 * u, y), 1.3f * u, StrokeCap.Round)
            x += 7 * u
        }
        drawLine(camino, Offset(w - 46 * u, 92 * u), Offset(w - 40 * u, 98 * u), 1.4f * u)
        drawLine(camino, Offset(w - 46 * u, 98 * u), Offset(w - 40 * u, 92 * u), 1.4f * u)
    }

    /**
     * El pergamino del mapa: el marco doble con la orla graduada a franjas en los costados y al pie (como los grados de una
     * carta), un nudo de anillos en cada esquina de abajo, unos montes trazados abajo a la izquierda, el camino punteado que
     * sube por el margen hasta su cruz y la rosa de los vientos abajo a la derecha; de noche, la luna creciente sobre la
     * rosa. En modo oscuro, el pergamino en la penumbra y la luz del farol desde arriba a la izquierda.
     */
    override fun DrawScope.pagina(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val m0 = 8 * u; val m1 = 13 * u
        drawRect(p.tinta.copy(alpha = 0.3f), Offset(m0, m0), Size(w - 2 * m0, h - 2 * m0), style = Stroke(1.3f * u))
        drawRect(p.detalle.copy(alpha = 0.3f), Offset(m1, m1), Size(w - 2 * m1, h - 2 * m1), style = Stroke(0.8f * u))
        val franja = p.tinta.copy(alpha = 0.14f)
        var y = m0
        var k = 0
        while (y < h - m1) {
            if (k % 2 == 0) {
                val alto = minOf(18 * u, h - m1 - y)
                drawRect(franja, Offset(m0, y), Size(m1 - m0, alto))
                drawRect(franja, Offset(w - m1, y), Size(m1 - m0, alto))
            }
            y += 18 * u; k++
        }
        var x = m1
        k = 0
        while (x < w - m1) {
            if (k % 2 == 0) drawRect(franja, Offset(x, h - m1), Size(minOf(18 * u, w - m1 - x), m1 - m0))
            x += 18 * u; k++
        }
        listOf(Offset(m1 + 8 * u, h - m1 - 8 * u), Offset(w - m1 - 8 * u, h - m1 - 8 * u)).forEach { c ->
            for (j in 0 until 4) {
                val a = Math.toRadians(45.0 + 90.0 * j)
                drawCircle(p.detalle.copy(alpha = 0.3f), 3.2f * u, Offset(c.x + 3 * u * cos(a).toFloat(), c.y + 3 * u * sin(a).toFloat()), style = Stroke(0.8f * u))
            }
        }
        // Los montes, abajo a la izquierda, con su sombreado del lado derecho.
        for (j in 0 until 4) {
            val b = Offset(m1 + 34 * u + j * 20 * u, h - 46 * u + (j % 2) * 4 * u)
            val alto = (16 + 6 * azar(61, j)) * u
            drawPath(Path().apply { moveTo(b.x - 11 * u, b.y); lineTo(b.x, b.y - alto); lineTo(b.x + 11 * u, b.y) }, p.tinta.copy(alpha = 0.2f), style = Stroke(1f * u))
            for (s in 1..3) drawLine(p.tinta.copy(alpha = 0.12f), Offset(b.x + s * 2.4f * u, b.y - alto + s * alto * 0.24f), Offset(b.x + s * 2.4f * u, b.y), 0.7f * u)
        }
        // El camino punteado que sube por el margen izquierdo y la cruz donde termina.
        var t = 0f
        while (t < 1f) {
            val py = h - 70 * u - t * (h * 0.5f)
            val px = m1 + 7 * u + 3 * u * sin(t * 22f)
            drawCircle(p.acento.copy(alpha = 0.3f), 1.1f * u, Offset(px, py))
            t += 0.012f
        }
        val cruz = Offset(m1 + 8 * u, h * 0.5f - 74 * u)
        drawLine(p.acento.copy(alpha = 0.4f), Offset(cruz.x - 4 * u, cruz.y - 4 * u), Offset(cruz.x + 4 * u, cruz.y + 4 * u), 1.4f * u)
        drawLine(p.acento.copy(alpha = 0.4f), Offset(cruz.x - 4 * u, cruz.y + 4 * u), Offset(cruz.x + 4 * u, cruz.y - 4 * u), 1.4f * u)
        rosa(Offset(w - 48 * u, h - 62 * u), 26 * u, p.tinta.copy(alpha = 0.2f), p.acento.copy(alpha = 0.12f))
        if (noche) {
            val luna = Offset(w - 44 * u, h - 128 * u)
            drawCircle(p.acento.copy(alpha = 0.3f), 12 * u, luna)
            drawCircle(p.color, 10 * u, Offset(luna.x + 6 * u, luna.y - 4 * u))
        }
        if (oscuro && !p.oscura) penumbraFan(Offset(-w * 0.04f, h * 0.07f), LLAMA, 0.24f)
    }

    /**
     * La barra: una franja de pergamino (un degradado, más claro arriba) con la cadena de anillos entrelazados de la talla
     * al pie, entre dos filetes.
     */
    override fun DrawScope.barra(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val perg = if (p.oscura) mezcla(p.color, p.tinta, 0.07f) else tonoFan(PERGAMINO, p, oscuro)
        drawRect(Brush.verticalGradient(listOf(mezcla(perg, Color.White, 0.25f), perg, mezcla(perg, PERGAMINO_OSC, 0.35f)), 0f, h))
        val linea = (if (p.oscura) p.tinta else TINTA).copy(alpha = 0.4f)
        drawLine(linea, Offset(0f, h - 12 * u), Offset(w, h - 12 * u), 0.8f * u)
        drawLine(linea, Offset(0f, h - 1.5f * u), Offset(w, h - 1.5f * u), 1f * u)
        var x = 4 * u
        while (x < w) {
            drawCircle(linea, 3.4f * u, Offset(x, h - 6.8f * u), style = Stroke(0.9f * u))
            x += 5.6f * u
        }
    }

    /**
     * El botón de guardar: una correa de cuero (un degradado, con su brillo arriba) con la costura punteada en latón todo
     * alrededor, un remache de latón en cada punta y su sombra plana.
     */
    override fun DrawScope.botonGuardar(p: Pagina, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val r = Rect(2 * u, 2 * u, w - 3 * u, h - 4 * u)
        val cuero = if (oscuro) mezcla(CUEROS[0], Color.Black, 0.1f) else CUEROS[0]
        drawRoundRect(Color.Black.copy(alpha = 0.32f), Offset(r.left + 1.5f * u, r.top + 2.5f * u), r.size, CornerRadius(6 * u))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(cuero, MADERA_LUZ, 0.35f), cuero, mezcla(cuero, Color.Black, 0.35f)), r.top, r.bottom), r.topLeft, r.size, CornerRadius(6 * u))
        val costura = Rect(r.left + 3.5f * u, r.top + 3.5f * u, r.right - 3.5f * u, r.bottom - 3.5f * u)
        var x = costura.left + 4 * u
        while (x < costura.right - 4 * u) {
            drawLine(LATON.copy(alpha = 0.8f), Offset(x, costura.top), Offset(x + 2.4f * u, costura.top), 0.9f * u)
            drawLine(LATON.copy(alpha = 0.8f), Offset(x, costura.bottom), Offset(x + 2.4f * u, costura.bottom), 0.9f * u)
            x += 4.6f * u
        }
        listOf(r.left + 8 * u, r.right - 8 * u).forEach { cx ->
            drawCircle(LATON, 2.6f * u, Offset(cx, r.center.y))
            drawCircle(LATON_LUZ, 1 * u, Offset(cx - 0.8f * u, r.center.y - 0.8f * u))
        }
    }

    override fun tintaBoton(p: Pagina, oscuro: Boolean, noche: Boolean): Color = LATON_LUZ

    // ---------------------------------------------------------------- la libreta de momentos

    override val altoLibreta: Dp = 262.dp

    /**
     * El color de la libreta con su luz: de día, tal cual; en modo oscuro, hundido en la penumbra de la torre salvo donde llega
     * el farol ([luz], de 0 lejos a 1 junto a la vela, que además lo entibia).
     */
    private fun lc(x: Color, oscuro: Boolean, luz: Float = 0f): Color =
        if (!oscuro) x else mezcla(mezcla(x, Color(0xFF080604), 0.72f - 0.44f * luz), LLAMA, 0.12f * luz)

    /** Un trozo de mapa dentro de [r]: la costa, el río, los montes, un bosque y el camino punteado, a tinta. */
    private fun DrawScope.trozoMapa(r: Rect, tinta: Color, agua: Color, monte: Color, semilla: Int) {
        val u = density
        val costa = Path().apply {
            moveTo(r.left, r.top + r.height * 0.3f)
            var x = r.left; var k = 0
            while (x < r.left + r.width * 0.45f) { val nx = x + r.width * 0.06f; quadraticTo(x + r.width * 0.03f, r.top + r.height * (0.25f + 0.2f * azar(semilla, k)), nx, r.top + r.height * (0.3f + 0.12f * k)); x = nx; k++ }
        }
        drawPath(costa, tinta, style = Stroke(1 * u))
        drawPath(costa, agua.copy(alpha = 0.5f), style = Stroke(4 * u))
        drawPath(Path().apply { moveTo(r.left + r.width * 0.5f, r.top); cubicTo(r.left + r.width * 0.35f, r.top + r.height * 0.4f, r.left + r.width * 0.65f, r.top + r.height * 0.6f, r.left + r.width * 0.5f, r.bottom) },
            agua, style = Stroke(1.2f * u))
        for (k in 0 until 4) {
            val m = Offset(r.left + r.width * (0.6f + 0.08f * k), r.top + r.height * (0.25f + 0.05f * (k % 2)))
            drawPath(Path().apply { moveTo(m.x - 6 * u, m.y + 4 * u); lineTo(m.x, m.y - 5 * u); lineTo(m.x + 6 * u, m.y + 4 * u) }, monte, style = Stroke(1 * u))
        }
        for (k in 0 until 6) drawCircle(monte.copy(alpha = 0.7f), 2.4f * u, Offset(r.left + r.width * (0.15f + 0.05f * k), r.top + r.height * (0.7f + 0.05f * (k % 2))))
        drawPath(Path().apply { moveTo(r.left + r.width * 0.12f, r.bottom - r.height * 0.12f); cubicTo(r.left + r.width * 0.4f, r.top + r.height * 0.5f, r.left + r.width * 0.6f, r.bottom, r.right - r.width * 0.12f, r.top + r.height * 0.45f) },
            tinta, style = Stroke(1 * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3 * u, 3 * u))))
        val x = Offset(r.right - r.width * 0.12f, r.top + r.height * 0.45f)
        drawLine(ROJO_MAPA, x + Offset(-3 * u, -3 * u), x + Offset(3 * u, 3 * u), 1.4f * u)
        drawLine(ROJO_MAPA, x + Offset(3 * u, -3 * u), x + Offset(-3 * u, 3 * u), 1.4f * u)
    }

    /**
     * La libreta de momentos: el libro de mapas, una cartera de cuero tumbada sobre la mesa de mapas. Por la boca y el canto
     * asoman los pliegos de pergamino (más cuanto más guardaste) y la cartera engorda; la cierra una solapa en pico con su
     * correa, la hebilla y una gema. Abajo, cosida al cuero, la etiqueta de pergamino con el nombre y la cuenta. La mesa está
     * cubierta por un mapa grande; encima, la brújula en su caja de latón, el compás de puntas y, al fondo, el farol de hierro
     * con su vela; detrás, la pared de tablones. Al abrirse, la correa se suelta, la solapa se levanta y el primer mapa sale
     * de la cartera. En modo oscuro la torre se apaga y alumbra el farol. Un degradado: el resplandor del farol.
     */
    override fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val a = e.abierto.coerceIn(0f, 1f)
        val gordo = (kotlin.math.sqrt(e.hojas.coerceAtLeast(1).toFloat()) / 14f).coerceIn(0.07f, 1f)
        val s = e.semilla
        fun c(x: Color, luz: Float = 0f) = lc(x, oscuro, luz)
        // La pared de tablones con sus clavos y sus nudos.
        val mesa0 = h * 0.4f
        drawRect(c(MURO, 0.2f), Offset.Zero, Size(w, mesa0))
        var x = 0f; var k = 0
        while (x < w) {
            val ancho = (30 + 12 * azar(k, 3)) * u
            drawRect(c(if (k % 2 == 0) MURO else mezcla(MURO, MADERA_OSC, 0.3f), 0.2f), Offset(x, 0f), Size(ancho, mesa0))
            vetas(Rect(x, 0f, x + ancho, mesa0), c(MADERA_OSC, 0.2f).copy(alpha = 0.4f), k + 31, densidad = 0.4f, vertical = true)
            drawLine(Color.Black.copy(alpha = 0.4f), Offset(x, 0f), Offset(x, mesa0), 1.2f * u)
            drawCircle(c(HIERRO, 0.2f), 1.6f * u, Offset(x + ancho / 2, h * 0.06f)); drawCircle(c(HIERRO, 0.2f), 1.6f * u, Offset(x + ancho / 2, mesa0 - 8 * u))
            if (k % 3 == 1) { drawOval(c(NUDO, 0.2f), Offset(x + ancho * 0.35f, h * 0.2f), Size(7 * u, 10 * u)) }
            x += ancho; k++
        }
        // Un rollo de cuerda colgado de una clavija.
        val clavija = Offset(w * 0.08f, h * 0.1f)
        drawRect(c(MADERA_OSC, 0.2f), Offset(clavija.x - 2 * u, clavija.y - 2 * u), Size(10 * u, 4 * u))
        val soga = c(Color(0xFFB09A6A), 0.2f)
        for (j in 0 until 6) drawOval(soga, Offset(clavija.x - 16 * u + j * 1.2f * u + (j % 2) * 2 * u, clavija.y + j * 0.6f * u), Size(32 * u - j * 2.4f * u, 50 * u - j * 2 * u), style = Stroke(2 * u))
        for (j in 0 until 10) drawLine(Color.Black.copy(alpha = 0.25f), Offset(clavija.x - 15 * u + j * 0.4f * u, clavija.y + 12 * u + j * 3.4f * u), Offset(clavija.x - 12 * u + j * 0.4f * u, clavija.y + 14 * u + j * 3.4f * u), 0.8f * u)
        drawLine(soga, Offset(clavija.x, clavija.y + 48 * u), Offset(clavija.x + 4 * u, clavija.y + 70 * u), 2 * u, StrokeCap.Round)
        // La mesa: el tablero cubierto por el mapa grande, el canto con sus muescas y las patas.
        val frente = h * 0.84f
        drawRect(c(MADERA_OSC, 0.4f), Offset(0f, mesa0), Size(w, h - mesa0))
        val tablero = Path().apply { moveTo(w * 0.03f, mesa0); lineTo(w * 0.97f, mesa0); lineTo(w * 1.02f, frente); lineTo(-w * 0.02f, frente); close() }
        drawPath(tablero, c(MADERA, 0.6f))
        val mapa = Path().apply { moveTo(w * 0.07f, mesa0 + 6 * u); lineTo(w * 0.93f, mesa0 + 4 * u); lineTo(w * 0.97f, frente - 8 * u); lineTo(w * 0.02f, frente - 6 * u); close() }
        val perg = c(PERGAMINO, 0.6f)
        drawPath(mapa, perg)
        clipPath(mapa) {
            drawRect(Color.Black.copy(alpha = 0.06f), Offset(0f, mesa0), Size(w, 8 * u))
            drawLine(c(PERGAMINO_OSC, 0.6f), Offset(w * 0.5f, mesa0), Offset(w * 0.5f, frente), 0.8f * u)
            drawLine(c(PERGAMINO_OSC, 0.6f), Offset(0f, (mesa0 + frente) / 2), Offset(w, (mesa0 + frente) / 2), 0.8f * u)
            trozoMapa(Rect(w * 0.04f, mesa0 + 10 * u, w * 0.96f, frente - 10 * u), c(TINTA, 0.6f).copy(alpha = 0.6f), c(AZUL_MAPA, 0.6f).copy(alpha = 0.7f), c(VERDE_MAPA, 0.6f).copy(alpha = 0.7f), s + 3)
            rosa(Offset(w * 0.12f, frente - 26 * u), 18 * u, c(TINTA, 0.6f).copy(alpha = 0.6f), c(ROJO_MAPA, 0.6f).copy(alpha = 0.5f))
        }
        drawPath(mapa, c(PERGAMINO_OSC, 0.6f), style = Stroke(1 * u))
        drawRect(c(MADERA_LUZ, 0.4f), Offset(0f, frente), Size(w, 3 * u))
        drawRect(c(MADERA, 0.4f), Offset(0f, frente + 3 * u), Size(w, h * 0.07f))
        for (j in 0 until 30) drawPath(Path().apply { val mx = j * w / 30f + 4 * u; moveTo(mx, frente + 6 * u); lineTo(mx + 4 * u, frente + 12 * u); lineTo(mx + 8 * u, frente + 6 * u); close() }, c(TALLA, 0.4f))
        listOf(w * 0.05f, w * 0.88f).forEach { lx -> drawRect(c(MADERA_OSC, 0.3f), Offset(lx, frente + 3 * u + h * 0.07f), Size(w * 0.07f, h)) }
        // El resplandor del farol (el único degradado).
        val vela = Offset(w * 0.87f, h * 0.3f)
        mancha(vela, if (oscuro) w * 0.9f else w * 0.3f, LLAMA.copy(alpha = if (oscuro) 0.42f else 0.25f))
        // El farol de hierro de pie sobre la mesa, al fondo a la derecha.
        val hierro = c(HIERRO, 0.9f)
        val vid = Rect(vela.x - 13 * u, h * 0.2f, vela.x + 13 * u, h * 0.43f)
        drawRect(LLAMA.copy(alpha = if (oscuro) 0.75f else 0.4f), vid.topLeft, vid.size)
        drawRect(c(CERA, 1f), Offset(vela.x - 3 * u, h * 0.34f), Size(6 * u, h * 0.09f))
        drawPath(Path().apply { moveTo(vela.x - 2.5f * u, h * 0.34f); quadraticTo(vela.x - 3 * u, h * 0.31f, vela.x, h * 0.28f); quadraticTo(vela.x + 3 * u, h * 0.31f, vela.x + 2.5f * u, h * 0.34f); close() }, Color(0xFFFFE6A0))
        drawRect(hierro, vid.topLeft, vid.size, style = Stroke(2 * u))
        drawLine(hierro, Offset(vela.x, vid.top), Offset(vela.x, vid.bottom), 1.2f * u)
        drawRect(hierro, Offset(vid.left - 3 * u, vid.bottom), Size(vid.width + 6 * u, 5 * u))
        drawPath(Path().apply { moveTo(vid.left - 4 * u, vid.top); lineTo(vela.x, vid.top - 14 * u); lineTo(vid.right + 4 * u, vid.top); close() }, hierro)
        drawCircle(hierro, 5 * u, Offset(vela.x, vid.top - 19 * u), style = Stroke(1.6f * u))
        // El compás de puntas, abierto sobre el mapa.
        val bisagra = Offset(w * 0.78f, h * 0.5f)
        val acero = c(HIERRO_LUZ, 0.7f)
        drawLine(acero, bisagra, Offset(bisagra.x - 20 * u, bisagra.y + 36 * u), 1.8f * u, StrokeCap.Round)
        drawLine(acero, bisagra, Offset(bisagra.x + 14 * u, bisagra.y + 38 * u), 1.8f * u, StrokeCap.Round)
        drawCircle(c(LATON, 0.7f), 3 * u, bisagra)
        // La brújula en su caja de latón, delante a la derecha.
        val br = Offset(w * 0.87f, h * 0.72f); val rr = 19 * u
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(br.x - rr - 3 * u, br.y - rr), Size(rr * 2 + 8 * u, rr * 2 + 8 * u))
        drawRoundRect(c(LATON_OSC, 0.7f), Offset(br.x - rr - 5 * u, br.y - rr - 5 * u), Size(rr * 2 + 10 * u, rr * 2 + 10 * u), CornerRadius(3 * u))
        drawRoundRect(c(LATON, 0.7f), Offset(br.x - rr - 3 * u, br.y - rr - 3 * u), Size(rr * 2 + 6 * u, rr * 2 + 6 * u), CornerRadius(2 * u))
        drawCircle(c(PERGAMINO, 0.8f), rr, br)
        rosa(br, rr * 0.9f, c(TINTA, 0.8f), c(ROJO_MAPA, 0.8f).copy(alpha = 0.5f))
        rotate(20f + 30f * azar(s, 5), br) {
            drawPath(Path().apply { moveTo(br.x, br.y - rr * 0.85f); lineTo(br.x + 3 * u, br.y); lineTo(br.x - 3 * u, br.y); close() }, c(ROJO_MAPA, 0.8f))
            drawPath(Path().apply { moveTo(br.x, br.y + rr * 0.85f); lineTo(br.x + 3 * u, br.y); lineTo(br.x - 3 * u, br.y); close() }, c(HIERRO, 0.8f))
        }
        drawCircle(Color.White.copy(alpha = 0.25f), rr, br, style = Stroke(1.4f * u))
        drawCircle(c(LATON_LUZ, 0.8f), 2 * u, br)
        // La cartera de mapas: el cuerpo de cuero, los pliegos que asoman y engordan, la solapa con su correa y la etiqueta.
        val caja = Rect(w * 0.14f, h * 0.43f, w * 0.68f, h * 0.8f)
        val grosor = (4 + 16 * gordo) * u
        val cuero = c(CUEROS[(s and 0x7fffffff) % CUEROS.size], 0.55f)
        val cueroOsc = mezcla(cuero, Color.Black, 0.3f)
        drawRect(Color.Black.copy(alpha = 0.35f), Offset(caja.left + 6 * u, caja.top + 8 * u), Size(caja.width, caja.height + grosor))
        // Los pliegos por el canto derecho.
        val pliegos = (1 + e.hojas / 15).coerceAtMost(8)
        for (j in 0 until pliegos) {
            val y = caja.top + caja.height * (0.15f + 0.7f * azar(s, 100 + j))
            rotate((azar(s, 110 + j) - 0.5f) * 10f, Offset(caja.right, y)) {
                drawRect(c(if (j % 2 == 0) PERGAMINO else PERGAMINO_OSC, 0.55f), Offset(caja.right - 10 * u, y), Size((16 + 12 * azar(s, 120 + j)) * u, (14 + 10 * azar(s, 130 + j)) * u))
                drawLine(c(AZUL_MAPA, 0.55f).copy(alpha = 0.6f), Offset(caja.right, y + 6 * u), Offset(caja.right + 14 * u, y + 9 * u), 0.8f * u)
            }
        }
        drawRect(cueroOsc, Offset(caja.left, caja.bottom), Size(caja.width, grosor))
        for (j in 1 until (grosor / (3 * u)).toInt().coerceAtLeast(1)) drawLine(c(PERGAMINO_OSC, 0.55f).copy(alpha = 0.7f), Offset(caja.left + 4 * u, caja.bottom + j * 3 * u), Offset(caja.right - 4 * u, caja.bottom + j * 3 * u), 0.6f * u)
        drawRect(cuero, caja.topLeft, caja.size)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(caja.right - caja.width * 0.06f, caja.top), Size(caja.width * 0.06f, caja.height))
        val costura = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.4f * u))
        drawRect(c(PERGAMINO_OSC, 0.55f), caja.deflate(5 * u).topLeft, caja.deflate(5 * u).size, style = Stroke(0.9f * u, pathEffect = costura))
        // Las esquinas de latón de abajo.
        listOf(Offset(caja.left, caja.bottom) to 1f, Offset(caja.right, caja.bottom) to -1f).forEach { (p, sg) ->
            drawPath(Path().apply { moveTo(p.x, p.y); lineTo(p.x + sg * 16 * u, p.y); lineTo(p.x, p.y - 16 * u); close() }, c(LATON, 0.55f))
        }
        // La solapa en pico: gira sobre el borde de arriba; al pasar de canto se ve por dentro, más clara.
        val pico = caja.top + caja.height * 0.5f
        val ang = Math.toRadians(70.0 * kotlin.math.sqrt(a.toDouble()))
        val kf = (kotlin.math.cos(ang) - 1.4 * kotlin.math.sin(ang)).toFloat().coerceAtLeast(-0.9f)
        val solapa = Path().apply { moveTo(caja.left - 2 * u, caja.top); lineTo(caja.right + 2 * u, caja.top); lineTo(caja.right + 2 * u, caja.top + caja.height * 0.14f); lineTo(caja.center.x, pico); lineTo(caja.left - 2 * u, caja.top + caja.height * 0.14f); close() }
        // Abierta, la solapa queda de pie detrás de la boca: se ve por dentro, más clara.
        if (kf < 0f) withTransform({ scale(1f, kf, Offset(caja.center.x, caja.top)) }) {
            drawPath(solapa, c(Color(0xFFB08A60), 0.55f))
            drawPath(solapa, cueroOsc, style = Stroke(1.6f * u))
        }
        // La boca: al abrir, el primer mapa sale de la cartera.
        val boca = caja.top + 4 * u
        if (a > 0.02f) {
            val sale = h * 0.2f * a
            val m = Rect(caja.left + caja.width * 0.08f, boca - sale, caja.right - caja.width * 0.12f, boca + 6 * u)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(m.left + 3 * u, m.top + 3 * u), m.size)
            drawRect(perg, m.topLeft, m.size)
            clipRect(m.left, m.top, m.right, m.bottom) { trozoMapa(Rect(m.left, m.top + 4 * u, m.right, m.top + h * 0.2f), c(TINTA, 0.6f), c(AZUL_MAPA, 0.6f), c(VERDE_MAPA, 0.6f), s + 9) }
            drawRect(c(PERGAMINO_OSC, 0.6f), m.topLeft, m.size, style = Stroke(0.8f * u))
            drawRect(cueroOsc, Offset(caja.left + 3 * u, boca - 2 * u), Size(caja.width - 6 * u, 4 * u))
        }
        // La etiqueta de pergamino cosida, con el nombre y la cuenta a tinta quemada.
        val et = Rect(caja.left + caja.width * 0.12f, caja.top + caja.height * 0.56f, caja.right - caja.width * 0.12f, caja.bottom - caja.height * 0.08f)
        val pe = c(PERGAMINO, 0.8f)
        drawRect(pe, et.topLeft, et.size)
        drawRect(c(PERGAMINO_OSC, 0.8f), et.topLeft, et.size, style = Stroke(1 * u))
        drawRect(cueroOsc, et.deflate(2.5f * u).topLeft, et.deflate(2.5f * u).size, style = Stroke(0.8f * u, pathEffect = costura))
        val tinta = legibleFan(pe, c(TINTA, 0.4f), PERGAMINO)
        textoEn(medidor, e.nombre, Rect(et.left + 8 * u, et.top + et.height * 0.08f, et.right - 8 * u, et.top + et.height * 0.56f), letra, tinta, 15f, peso = FontWeight.Bold, minimo = 7f)
        textoEn(medidor, e.cuenta, Rect(et.left + 8 * u, et.top + et.height * 0.54f, et.right - 8 * u, et.bottom - et.height * 0.1f), letra, tinta.copy(alpha = 0.85f), 10.5f, minimo = 6.5f)
        // Cerrada (o a punto de abrirse), la solapa por fuera, con su gema.
        if (kf > 0f) withTransform({ scale(1f, kf, Offset(caja.center.x, caja.top)) }) {
            translate(0f, 3 * u) { drawPath(solapa, Color.Black.copy(alpha = 0.3f)) }
            drawPath(solapa, mezcla(cuero, Color.White, 0.06f))
            drawPath(solapa, c(PERGAMINO_OSC, 0.55f), style = Stroke(0.9f * u, pathEffect = costura))
            drawPath(Path().apply { moveTo(caja.left + 8 * u, caja.top + 8 * u); lineTo(caja.right - 8 * u, caja.top + 8 * u) }, c(LATON, 0.55f).copy(alpha = 0.6f), style = Stroke(1 * u))
            val gema = Offset(caja.center.x, pico - caja.height * 0.14f)
            drawCircle(c(LATON, 0.55f), 7 * u, gema)
            drawPath(estrella(gema, 5 * u, 4, 0.55f, 0f), c(GEMAS[(s and 0x7fffffff) % GEMAS.size], 0.7f))
            drawCircle(Color.White.copy(alpha = 0.5f), 1.4f * u, gema + Offset(-1.5f * u, -1.5f * u))
        }
        // La correa con su hebilla: cerrada baja del pico hasta la hebilla; al abrir, cuelga suelta.
        val correa = c(MADERA_OSC, 0.55f)
        if (a < 0.1f) {
            drawRect(correa, Offset(caja.center.x - 4 * u, pico - 4 * u), Size(8 * u, et.top - pico + 2 * u))
            drawRect(c(LATON, 0.6f), Offset(caja.center.x - 7 * u, et.top - 6 * u), Size(14 * u, 9 * u), style = Stroke(2 * u))
            drawLine(c(LATON, 0.6f), Offset(caja.center.x, et.top - 6 * u), Offset(caja.center.x, et.top + 3 * u), 1.4f * u)
        } else {
            drawPath(Path().apply { moveTo(caja.center.x, et.top - 4 * u); quadraticTo(caja.center.x + 20 * u, et.top - 22 * u, caja.center.x + 34 * u, et.top - 6 * u) }, correa, style = Stroke(6 * u))
            drawRect(c(LATON, 0.6f), Offset(caja.center.x - 7 * u, et.top - 8 * u), Size(14 * u, 9 * u), style = Stroke(2 * u))
        }
    }

    override fun DrawScope.pestana(e: EstadoPestana, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        // Una lengüeta de cuero con el día en dorado, el remache de latón y, al pie, una gema del color del ánimo.
        val cuero = lc(CUEROS[(s and 0x7fffffff) % CUEROS.size], oscuro, 0.5f)
        val r = Rect(w * 0.07f, h * 0.02f, w * 0.93f, h * 0.97f)
        val forma = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom - 10 * u)
            quadraticTo(r.right, r.bottom, r.center.x, r.bottom); quadraticTo(r.left, r.bottom, r.left, r.bottom - 10 * u); close()
        }
        translate(1.5f * u, 2 * u) { drawPath(forma, Color.Black.copy(alpha = 0.3f)) }
        drawPath(forma, cuero)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(r.right - 6 * u, r.top), Size(6 * u, r.height - 10 * u))
        drawPath(forma, lc(LATON, oscuro, 0.5f).copy(alpha = 0.5f), style = Stroke(0.8f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 2 * u))))
        drawCircle(lc(LATON, oscuro, 0.5f), 2.2f * u, Offset(r.center.x, r.top + 5 * u))
        val oro = if (oscuro) LATON_LUZ else mezcla(LATON_LUZ, Color.White, 0.2f)
        textoEn(medidor, e.dia, Rect(r.left, r.top + 8 * u, r.right, r.top + r.height * 0.56f), letra, oro, 19f, peso = FontWeight.Bold, minimo = 9f)
        textoEn(medidor, e.semana, Rect(r.left, r.top + r.height * 0.52f, r.right, r.top + r.height * 0.68f), letra, oro.copy(alpha = 0.8f), 9f, minimo = 6f)
        val g = Offset(r.center.x, r.bottom - 10 * u)
        drawCircle(lc(LATON_OSC, oscuro, 0.5f), 7 * u, g)
        drawCircle(lc(LATON, oscuro, 0.5f), 6 * u, g)
        if (e.tono != null) {
            drawPath(estrella(g, 5 * u, 4, 0.6f, 0f), e.tono)
            drawCircle(e.tono, 3.2f * u, g)
            drawCircle(Color.White.copy(alpha = 0.55f), 1.2f * u, g + Offset(-1.4f * u, -1.4f * u))
        } else drawCircle(lc(LATON_OSC, oscuro, 0.5f), 2 * u, g)
    }

    override fun DrawScope.rotuloMes(texto: String, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Una cinta de pergamino con las puntas dobladas hacia atrás y cortadas en cola de golondrina.
        val perg = lc(PERGAMINO, oscuro, 0.4f)
        val doblez = lc(PERGAMINO_OSC, oscuro, 0.3f)
        val cola = w * 0.07f
        val alto = h - 10 * u
        listOf(0f to 1f, w to -1f).forEach { (x0, sg) ->
            drawPath(Path().apply { moveTo(x0, 9 * u); lineTo(x0 + sg * cola * 1.5f, 9 * u); lineTo(x0 + sg * cola * 1.5f, 9 * u + alto * 0.8f); lineTo(x0, 9 * u + alto * 0.8f); lineTo(x0 + sg * cola * 0.5f, 9 * u + alto * 0.4f); close() }, doblez)
        }
        val r = Rect(cola, 3 * u, w - cola, 3 * u + alto)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 2 * u, r.top + 2.5f * u), r.size)
        drawRect(perg, r.topLeft, r.size)
        drawRect(Color.White.copy(alpha = 0.15f), r.topLeft, Size(r.width, 3 * u))
        drawRect(Color.Black.copy(alpha = 0.1f), Offset(r.left, r.bottom - 4 * u), Size(r.width, 4 * u))
        val tinta = legibleFan(perg, lc(TINTA, oscuro, 0.2f), PERGAMINO)
        drawLine(lc(ROJO_MAPA, oscuro, 0.4f), Offset(r.left + 6 * u, r.top + 4 * u), Offset(r.right - 6 * u, r.top + 4 * u), 0.8f * u)
        drawLine(lc(ROJO_MAPA, oscuro, 0.4f), Offset(r.left + 6 * u, r.bottom - 4 * u), Offset(r.right - 6 * u, r.bottom - 4 * u), 0.8f * u)
        listOf(r.left + 14 * u, r.right - 14 * u).forEach { x -> drawPath(estrella(Offset(x, r.center.y), 5 * u, 4, 0.3f), tinta) }
        textoEn(medidor, texto, Rect(r.left + 26 * u, r.top, r.right - 26 * u, r.bottom), letra, tinta, 15f, peso = FontWeight.Bold, minimo = 8f)
    }

    // ---------------------------------------------------------------- las cartas

    private val PERGAMINO_CARTA = Color(0xFFE3D5B4)
    private val PERGAMINO_LUZ = Color(0xFFF1E6C8)
    private val TIENTO = Color(0xFF5A3A22)
    private val TIENTO_LUZ = Color(0xFF8A5E3A)
    private val CERA_VERDE = Color(0xFF2E7A5A)
    private val HOJA_SECA = Color(0xFFB8862E)
    private val PALOMA = Color(0xFFB9BCC2)
    private val PALOMA_OSC = Color(0xFF7E8590)
    private val CIELO = Color(0xFF9FB8C8)

    /** El color de las cartas con su luz: de día, tal cual; en modo oscuro, hundido en la oscuridad de la torre. */
    private fun cc(x: Color, oscuro: Boolean): Color = if (oscuro) mezcla(x, Color(0xFF0D0906), 0.55f) else x

    /**
     * El sello de las tierras antiguas, centrado en [c]: una gota de cera verde aplastada con el anillo de hierro, que deja su
     * aro de remaches; en medio, la gema tallada del anillo, engastada en la cera.
     */
    private fun DrawScope.selloAntiguas(c: Offset, r: Float, oscuro: Boolean, semilla: Int) {
        fun k(x: Color) = if (oscuro) mezcla(x, Color(0xFF0D0906), 0.25f) else x
        val gota = discoFan(c, r, semilla, 20, 0.1f)
        translate(r * 0.07f, r * 0.12f) { drawPath(gota, Color.Black.copy(alpha = 0.32f)) }
        drawPath(gota, k(CERA_VERDE))
        val hondo = k(mezcla(CERA_VERDE, Color.Black, 0.4f))
        val claro = k(mezcla(CERA_VERDE, Color.White, 0.4f))
        drawCircle(hondo, r * 0.72f, c, style = Stroke(r * 0.18f))
        drawCircle(claro.copy(alpha = 0.6f), r * 0.84f, c, style = Stroke(r * 0.04f))
        for (j in 0 until 6) {
            val a = (j * Math.PI / 3).toFloat()
            drawCircle(claro, r * 0.07f, Offset(c.x + r * 0.72f * cos(a), c.y + r * 0.72f * sin(a)))
        }
        val vertices = (0 until 6).map { j -> val a = (j * Math.PI / 3 + Math.PI / 6).toFloat(); Offset(c.x + r * 0.44f * cos(a), c.y + r * 0.44f * sin(a)) }
        val gema = Path().apply { vertices.forEachIndexed { i, v -> if (i == 0) moveTo(v.x, v.y) else lineTo(v.x, v.y) }; close() }
        drawPath(gema, k(GEMAS[0]))
        vertices.forEach { v -> drawLine(claro.copy(alpha = 0.5f), c, v, r * 0.03f) }
        drawPath(gema, hondo, style = Stroke(r * 0.05f))
        drawCircle(Color.White.copy(alpha = 0.6f), r * 0.08f, c - Offset(r * 0.14f, r * 0.14f))
    }

    /** Una hoja seca prensada, de alto [m], centrada en [o] y girada [giro] grados: la lámina con su nervio y sus venas. */
    private fun DrawScope.hojaSeca(o: Offset, m: Float, giro: Float, color: Color) {
        val u = density
        rotate(giro, o) {
            drawPath(Path().apply {
                moveTo(o.x, o.y - m / 2); quadraticTo(o.x + m * 0.36f, o.y - m * 0.1f, o.x, o.y + m / 2); quadraticTo(o.x - m * 0.36f, o.y - m * 0.1f, o.x, o.y - m / 2); close()
            }, color)
            val nervio = mezcla(color, Color.Black, 0.35f)
            drawLine(nervio, Offset(o.x, o.y - m * 0.45f), Offset(o.x, o.y + m * 0.62f), 0.8f * u)
            for (j in 0 until 3) {
                val y = o.y - m * 0.2f + j * m * 0.18f
                drawLine(nervio, Offset(o.x, y + m * 0.06f), Offset(o.x - m * 0.16f, y - m * 0.04f), 0.6f * u)
                drawLine(nervio, Offset(o.x, y + m * 0.06f), Offset(o.x + m * 0.16f, y - m * 0.04f), 0.6f * u)
            }
        }
    }

    /** La solapa rasgada: un pico de borde irregular, como pergamino cortado a mano, que baja hasta [puntaY]. */
    private fun solapaRasgada(l: Float, r: Float, arriba: Float, puntaY: Float, semilla: Int): Path = Path().apply {
        val cx = (l + r) / 2
        val pasos = 9
        moveTo(l, arriba)
        for (j in 1..pasos) {
            val f = j / pasos.toFloat()
            val x = l + (cx - l) * f
            val y = arriba + (puntaY - arriba) * f + (azar(semilla, j) - 0.5f) * kotlin.math.abs(puntaY - arriba) * 0.06f
            lineTo(x, y)
        }
        for (j in pasos - 1 downTo 0) {
            val f = j / pasos.toFloat()
            val x = r - (r - cx) * f
            val y = arriba + (puntaY - arriba) * f + (azar(semilla, j + 20) - 0.5f) * kotlin.math.abs(puntaY - arriba) * 0.06f
            lineTo(x, y)
        }
        close()
    }

    /**
     * La carta de las tierras antiguas: un pliego de pergamino doblado en tres, de orillas gastadas, con la solapa en pico
     * rasgada a mano y, dibujado en ella a tinta, un trozo del camino: los montes, la senda punteada hasta su cruz roja y una
     * rosa de los vientos. A quién va y las fechas, con la letra del camino al pie del pliego; bajo el tiento, una hoja seca.
     * Las de al futuro se atan con dos vueltas de tiento de cuero ([EstadoCarta.cordon]); en la punta, la cera verde con el
     * anillo de hierro y su gema. Al abrirse, la cera se parte, la solapa se levanta y sube la hoja de pergamino. Un degradado:
     * el brillo del sello.
     */
    override fun DrawScope.sobre(e: EstadoCarta, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val q = e.cuerpo
        val W = q.width; val H = q.height; val cx = q.center.x
        val ab = e.abrir.coerceIn(0f, 1f)
        val s = e.semilla
        fun c(x: Color) = cc(x, oscuro)
        val caja = Rect(q.left + W * 0.03f, q.top + H * 0.06f, q.right - W * 0.03f, q.bottom - H * 0.05f)
        val atras = contornoIrregular(caja, s, 34, 0.012f)
        translate(2.5f * u, 4 * u) { drawPath(atras, Color.Black.copy(alpha = 0.3f)) }
        drawPath(atras, c(PERGAMINO_OSC))
        // La boca del pliego: un escote en V entre los dos lados, por donde sale la carta.
        val boca = caja.top + H * 0.16f
        val escote = Path().apply {
            moveTo(caja.left - 4 * u, caja.top + H * 0.01f); lineTo(cx, boca); lineTo(caja.right + 4 * u, caja.top + H * 0.01f)
            lineTo(caja.right + 4 * u, caja.bottom + 4 * u); lineTo(caja.left - 4 * u, caja.bottom + 4 * u); close()
        }
        if (ab > 0f) clipPath(atras) {
            drawPath(Path().apply { moveTo(caja.left, caja.top); lineTo(caja.right, caja.top); lineTo(cx, boca); close() }, c(Color(0xFF2A1C10)).copy(alpha = ab))
        }
        val kk = 1f - 2f * ab
        val puntaCerrada = q.top + H * 0.46f
        val puntaY = caja.top + (puntaCerrada - caja.top) * kk
        val solapa = solapaRasgada(caja.left, caja.right, caja.top, puntaY, s)
        if (kk < 0f) {
            drawPath(solapa, c(PERGAMINO_OSC))
            drawPath(solapa, c(TINTA).copy(alpha = 0.5f), style = Stroke(1 * u))
        }
        if (ab > 0.3f || e.subir > 0f) hojaCartaFan(q, e.subir, e.hoja, e.renglones, oscuro, 0.12f, 0.88f, borde = s + 1)
        // El frente del pliego, con los dos dobleces y las manchas del viaje.
        clipPath(escote) {
            drawPath(atras, c(PERGAMINO_CARTA))
            clipPath(atras) {
                for (j in 0 until 14) drawCircle(c(PERGAMINO_OSC).copy(alpha = 0.22f), (1f + 3f * azar(s, j)) * u, Offset(caja.left + caja.width * azar(s, j + 30), caja.top + caja.height * azar(s, j + 60)))
                listOf(0.33f, 0.67f).forEach { f ->
                    drawLine(c(PERGAMINO_OSC).copy(alpha = 0.6f), Offset(caja.left + caja.width * f, caja.top), Offset(caja.left + caja.width * f, caja.bottom), 1 * u)
                    drawLine(c(PERGAMINO_LUZ).copy(alpha = 0.6f), Offset(caja.left + caja.width * f + 1.2f * u, caja.top), Offset(caja.left + caja.width * f + 1.2f * u, caja.bottom), 0.8f * u)
                }
            }
            drawPath(atras, c(TINTA).copy(alpha = 0.45f), style = Stroke(0.9f * u))
        }
        drawLine(c(TINTA).copy(alpha = 0.5f), Offset(caja.left + 2 * u, caja.top + H * 0.015f), Offset(cx, boca), 1 * u)
        drawLine(c(TINTA).copy(alpha = 0.5f), Offset(cx, boca), Offset(caja.right - 2 * u, caja.top + H * 0.015f), 1 * u)
        // A quién va y las fechas, a tinta; sin ellos (en grande), una línea de runas.
        val tinta = legibleFan(c(PERGAMINO_CARTA), c(TINTA), PERGAMINO_LUZ)
        if (e.linea1.isBlank() && e.linea2.isBlank()) {
            var x = cx - W * 0.3f
            var j = 0
            while (x < cx + W * 0.3f) {
                val y0 = q.top + H * 0.7f; val y1 = q.top + H * 0.82f
                drawLine(c(TINTA).copy(alpha = 0.55f), Offset(x, y0), Offset(x, y1), 1.2f * u)
                drawLine(c(TINTA).copy(alpha = 0.55f), Offset(x, y0 + (j % 3) * 3 * u), Offset(x + 5 * u, y0 + 4 * u + (j % 2) * 4 * u), 1.2f * u)
                x += 11 * u; j++
            }
        }
        lineasCartaFan(medidor, e.linea1, e.linea2, Rect(caja.left + W * 0.06f, q.top + H * 0.62f, caja.right - W * 0.06f, q.top + H * 0.79f),
            Rect(caja.left + W * 0.08f, q.top + H * 0.78f, caja.right - W * 0.08f, q.top + H * 0.9f), LetraExtra.camino, tinta, 12.5f, 9.5f)
        // La solapa cerrada (o levantándose): el pergamino con el trozo del camino.
        if (kk >= 0f) {
            translate(0f, 2 * u) { drawPath(solapa, Color.Black.copy(alpha = 0.2f)) }
            drawPath(solapa, c(PERGAMINO_LUZ))
            clipPath(solapa) {
                val alto = puntaY - caja.top
                if (alto > H * 0.2f && ab < 0.3f) {
                    val tin = c(TINTA).copy(alpha = 0.55f)
                    val camino = Path().apply {
                        moveTo(caja.left + W * 0.1f, caja.top + alto * 0.7f)
                        cubicTo(caja.left + W * 0.22f, caja.top + alto * 0.25f, caja.left + W * 0.32f, caja.top + alto * 0.75f, caja.left + W * 0.42f, caja.top + alto * 0.35f)
                    }
                    drawPath(camino, tin, style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2.5f * u))))
                    val xr = Offset(caja.left + W * 0.42f, caja.top + alto * 0.35f); val sx = H * 0.03f
                    drawLine(c(ROJO_MAPA), xr - Offset(sx, sx), xr + Offset(sx, sx), 1.6f * u); drawLine(c(ROJO_MAPA), xr + Offset(-sx, sx), xr + Offset(sx, -sx), 1.6f * u)
                    listOf(0.6f, 0.68f, 0.76f).forEachIndexed { i, f ->
                        val b = Offset(caja.left + W * f, caja.top + alto * (0.42f + 0.05f * (i % 2)))
                        drawPath(Path().apply { moveTo(b.x - H * 0.06f, b.y); lineTo(b.x, b.y - H * 0.08f); lineTo(b.x + H * 0.06f, b.y) }, tin, style = Stroke(1.1f * u))
                    }
                    rosa(Offset(caja.right - W * 0.1f, caja.top + alto * 0.3f), H * 0.08f, tin, c(PERGAMINO_OSC))
                }
            }
            drawPath(solapa, c(TINTA).copy(alpha = 0.5f), style = Stroke(1 * u))
        }
        // El tiento de cuero: dos vueltas a la altura de la punta, con una hoja seca prensada debajo.
        val rS = minOf(H * 0.14f, W * 0.1f)
        val cS = Offset(cx, puntaCerrada)
        if (e.cordon > 0f) {
            if (e.cordon > 0.3f) hojaSeca(Offset(cx - W * 0.22f, cS.y - rS * 0.1f), H * 0.2f, -60f, c(HOJA_SECA))
            listOf(-0.3f, 0.3f).forEachIndexed { i, dy ->
                val y = cS.y + rS * dy
                val av = ((e.cordon - i * 0.4f) / 0.6f).coerceIn(0f, 1f)
                val ruta = listOf(Offset(caja.left - W * 0.012f, y + 2 * u), Offset(cx, y), Offset(caja.right + W * 0.012f, y - 2 * u))
                trazoParcialFan(ruta.map { it + Offset(0f, 1.2f * u) }, av, Color.Black.copy(alpha = 0.3f), 2.8f * u)
                trazoParcialFan(ruta, av, c(TIENTO), 2.4f * u)
                trazoParcialFan(ruta, av, c(TIENTO_LUZ), 0.7f * u)
            }
        }
        selloCartaFan(cS, rS, e.brillo, e.romper.coerceIn(0f, 1f), e.escalaSello, e.alfaSello, LLAMA) { selloAntiguas(cS, rS, oscuro, s) }
    }

    override val cajaPrevia: Rect = Rect(0.17f, 0.175f, 0.83f, 0.345f)

    override val altoBuzon: Dp = 210.dp

    /** Un mensaje enrollado en el agujero del palomar, con su centro en [o]: listo (la cera brilla), atado con su cera o desenrollado. */
    private fun DrawScope.rolloPalomar(o: Offset, largo: Float, d: Float, giro: Float, estado: Int, oscuro: Boolean) {
        val u = density
        rotate(giro, o) {
            if (estado == 2) {
                // Desenrollado: la tira cuelga del agujero, con renglones.
                val tira = Rect(o.x - d * 0.9f, o.y, o.x + d * 0.9f, o.y + largo * 0.9f)
                drawRect(Color.Black.copy(alpha = 0.25f), tira.topLeft + Offset(1 * u, 1 * u), tira.size)
                drawRect(cc(PERGAMINO_LUZ, oscuro), tira.topLeft, tira.size)
                for (j in 0 until 4) drawLine(cc(TINTA, oscuro).copy(alpha = 0.45f), Offset(tira.left + 2 * u, tira.top + (5 + 5 * j) * u), Offset(tira.right - (2 + 3 * (j % 2)) * u, tira.top + (5 + 5 * j) * u), 0.8f * u)
            }
            val r = Rect(o.x - largo / 2, o.y - d / 2, o.x + largo / 2, o.y + d / 2)
            drawRoundRect(cc(PERGAMINO_CARTA, oscuro), r.topLeft, r.size, CornerRadius(d / 2))
            drawRect(Color.Black.copy(alpha = 0.15f), Offset(r.left, o.y + d * 0.1f), Size(r.width, d * 0.4f))
            drawOval(cc(PERGAMINO_OSC, oscuro), Offset(r.right - d * 0.4f, r.top), Size(d * 0.4f, d))
            if (estado != 2) {
                drawLine(cc(TIENTO, oscuro), Offset(o.x, r.top - 1 * u), Offset(o.x, r.bottom + 1 * u), 1.6f * u)
                drawCircle(cc(CERA_VERDE, oscuro), d * 0.42f, Offset(o.x, o.y))
                drawCircle(cc(mezcla(CERA_VERDE, Color.White, 0.4f), oscuro), d * 0.18f, Offset(o.x, o.y))
            }
        }
    }

    /** Una paloma mensajera de perfil, posada, con el pecho en [o] y de alto [m], mirando a la izquierda si [izq]. */
    private fun DrawScope.paloma(o: Offset, m: Float, izq: Boolean, oscuro: Boolean) {
        val s = if (izq) 1f else -1f
        val cuerpo = Path().apply {
            moveTo(o.x - s * m * 0.1f, o.y - m * 0.35f)
            quadraticTo(o.x - s * m * 0.35f, o.y - m * 0.1f, o.x - s * m * 0.1f, o.y + m * 0.2f)
            quadraticTo(o.x + s * m * 0.4f, o.y + m * 0.3f, o.x + s * m * 0.85f, o.y + m * 0.15f)
            quadraticTo(o.x + s * m * 0.5f, o.y - m * 0.05f, o.x + s * m * 0.1f, o.y - m * 0.3f)
            close()
        }
        drawPath(cuerpo, cc(PALOMA, oscuro))
        drawPath(Path().apply { moveTo(o.x + s * m * 0.05f, o.y - m * 0.1f); quadraticTo(o.x + s * m * 0.4f, o.y, o.x + s * m * 0.75f, o.y + m * 0.12f); quadraticTo(o.x + s * m * 0.35f, o.y + m * 0.15f, o.x + s * m * 0.05f, o.y - m * 0.1f); close() }, cc(PALOMA_OSC, oscuro))
        drawCircle(cc(PALOMA, oscuro), m * 0.16f, Offset(o.x - s * m * 0.12f, o.y - m * 0.42f))
        drawCircle(cc(Color(0xFF6E8A7A), oscuro), m * 0.12f, Offset(o.x - s * m * 0.14f, o.y - m * 0.26f))
        drawCircle(cc(HIERRO, oscuro), m * 0.035f, Offset(o.x - s * m * 0.18f, o.y - m * 0.45f))
        drawPath(Path().apply { moveTo(o.x - s * m * 0.26f, o.y - m * 0.45f); lineTo(o.x - s * m * 0.38f, o.y - m * 0.4f); lineTo(o.x - s * m * 0.26f, o.y - m * 0.38f); close() }, cc(Color(0xFFC9A04A), oscuro))
        drawLine(cc(ROJO_MAPA, oscuro), Offset(o.x, o.y + m * 0.22f), Offset(o.x - s * m * 0.05f, o.y + m * 0.4f), m * 0.05f)
    }

    /**
     * El buzón de las tierras antiguas: el palomar de la torre del camino, un mueble de tablones con tejadillo a dos aguas,
     * colgado de la pared bajo el cielo. Tres apartados con sus agujeros redondos y su percha; en cada agujero, los mensajes
     * enrollados: a la izquierda los que ya se pueden abrir, con la cera brillando; en medio los atados con tiento y cera
     * verde; a la derecha los abiertos, con la tira colgando. Bajo cada apartado cuelga de dos cadenas una tablilla con el
     * nombre y la cuenta en letras quemadas; en el tejado y en una percha, dos palomas mensajeras. En modo oscuro la torre se
     * apaga. Un degradado: el brillo de las listas.
     */
    override fun DrawScope.buzon(e: EstadoBuzon, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = cc(x, oscuro)
        // El cielo y la pared de tablones de la torre.
        drawRect(c(CIELO))
        drawRect(c(MURO), Offset(0f, h * 0.2f), Size(w, h * 0.8f))
        var x = 0f
        while (x < w) { drawLine(c(NUDO).copy(alpha = 0.5f), Offset(x, h * 0.2f), Offset(x, h), 1 * u); x += 26 * u }
        // El palomar: la caja de tablones y el tejadillo.
        val caja = Rect(w * 0.04f, h * 0.2f, w * 0.96f, h * 0.62f)
        drawRect(Color.Black.copy(alpha = 0.35f), caja.topLeft + Offset(4 * u, 4 * u), caja.size)
        drawRect(c(MADERA), caja.topLeft, caja.size)
        vetas(caja, c(MADERA_OSC).copy(alpha = 0.45f), 909, densidad = 0.6f)
        val tejado = Path().apply { moveTo(caja.left - 8 * u, caja.top + 2 * u); lineTo(w / 2, h * 0.04f); lineTo(caja.right + 8 * u, caja.top + 2 * u); lineTo(caja.right + 8 * u, caja.top + 8 * u); lineTo(w / 2, h * 0.04f + 6 * u); lineTo(caja.left - 8 * u, caja.top + 8 * u); close() }
        drawPath(Path().apply { moveTo(caja.left, caja.top); lineTo(w / 2, h * 0.07f); lineTo(caja.right, caja.top); close() }, c(MADERA_OSC))
        drawPath(tejado, c(PIEDRA))
        drawPath(tejado, c(HIERRO), style = Stroke(1 * u))
        drawCircle(c(NUDO), 6 * u, Offset(w / 2, h * 0.14f))
        drawCircle(c(MADERA_LUZ), 6 * u, Offset(w / 2, h * 0.14f), style = Stroke(1.2f * u))
        val px = w * 0.64f
        val techo = h * 0.04f + (px - w / 2) / (caja.right + 8 * u - w / 2) * (caja.top + 2 * u - h * 0.04f)
        paloma(Offset(px, techo - 24 * u * 0.26f), 24 * u, false, oscuro)
        val zw = caja.width / 3f
        val cuentas = listOf(e.listas, e.selladas, e.abiertas)
        val nombres = listOf("para abrir", "atados", "abiertos")
        for (g in 0 until 3) {
            val x0 = caja.left + zw * g
            if (g > 0) drawRect(c(MADERA_OSC), Offset(x0 - 2 * u, caja.top), Size(4 * u, caja.height))
            val n = minOf(cuentas[g], 4)
            // Dos agujeros por apartado, con su percha.
            for (k in 0 until 2) {
                val hc = Offset(x0 + zw * (0.3f + 0.4f * k), caja.top + caja.height * (0.45f + 0.08f * (k % 2)))
                val rh = minOf(zw * 0.2f, 18 * u)
                if (g == 0 && n > k * 2 && k == 0) mancha(Offset(x0 + zw * 0.5f, hc.y), zw * 0.62f, LLAMA.copy(alpha = if (oscuro) 0.6f else 0.5f))
                drawCircle(c(Color(0xFF1E140A)), rh, hc)
                drawCircle(c(MADERA_LUZ), rh, hc, style = Stroke(2 * u))
                drawRoundRect(c(MADERA_OSC), Offset(hc.x - rh * 1.1f, hc.y + rh + 2 * u), Size(rh * 2.2f, 3.5f * u), CornerRadius(1.5f * u))
                for (i in 0 until 2) {
                    val idx = k * 2 + i
                    if (idx >= n) continue
                    rolloPalomar(Offset(hc.x + (i - 0.5f) * rh * 0.5f, hc.y + (i - 0.5f) * rh * 0.55f), rh * 1.7f, rh * 0.5f, if (i == 0) -14f else 10f, g, oscuro)
                }
                if (g == 0 && n > k * 2) drawPath(estrella(Offset(hc.x + rh * 0.9f, hc.y - rh * 0.9f), 3.4f * u, 4, 0.25f, 0f), LLAMA)
            }
            // La tablilla colgada de dos cadenas, con el nombre y la cuenta quemados.
            val tab = Rect(x0 + zw * 0.12f, h * 0.69f, x0 + zw * 0.88f, h * 0.93f)
            listOf(tab.left + 8 * u, tab.right - 8 * u).forEach { cxx ->
                var yy = caja.bottom
                while (yy < tab.top) { drawOval(c(HIERRO_LUZ), Offset(cxx - 1.5f * u, yy), Size(3 * u, 4 * u), style = Stroke(0.9f * u)); yy += 3.5f * u }
            }
            drawRect(Color.Black.copy(alpha = 0.3f), tab.topLeft + Offset(2 * u, 2 * u), tab.size)
            drawRect(c(MADERA_LUZ), tab.topLeft, tab.size)
            vetas(tab, c(MADERA_OSC).copy(alpha = 0.35f), 950 + g, densidad = 0.5f)
            bisel(tab, c(PERGAMINO).copy(alpha = 0.3f), c(MADERA_OSC).copy(alpha = 0.6f), 1.4f * u)
            listOf(tab.left + 8 * u, tab.right - 8 * u).forEach { cxx -> drawCircle(c(HIERRO), 1.6f * u, Offset(cxx, tab.top + 3 * u)) }
            val quemado = legibleFan(c(MADERA_LUZ), c(NUDO), PERGAMINO)
            textoEn(medidor, nombres[g], Rect(tab.left + 4 * u, tab.top + 4 * u, tab.right - 4 * u, tab.top + tab.height * 0.44f), LetraExtra.camino, quemado, 10.5f, minimo = 6f)
            textoEn(medidor, cuentas[g].toString(), Rect(tab.left + 4 * u, tab.top + tab.height * 0.4f, tab.right - 4 * u, tab.bottom - 2 * u), LetraExtra.camino, quemado, 17f, peso = FontWeight.Bold, minimo = 7f)
        }
        // La segunda paloma, en la percha del último agujero si está vacío, o en la orilla del palomar.
        paloma(Offset(caja.right - zw * 0.16f, caja.bottom - 7 * u), 20 * u, true, oscuro)
    }
}

// =====================================================================
//  Ayudantes de las tapas de la Fantasía
// =====================================================================

/** De dos tintas, la que mejor se lee sobre [fondo] (la de más contraste). */
private fun legibleFan(fondo: Color, oscura: Color, clara: Color): Color {
    val f = fondo.luminance()
    val conOscura = (maxOf(f, oscura.luminance()) + 0.05f) / (minOf(f, oscura.luminance()) + 0.05f)
    val conClara = (maxOf(f, clara.luminance()) + 0.05f) / (minOf(f, clara.luminance()) + 0.05f)
    return if (conOscura >= conClara) oscura else clara
}

/** La fecha en uno o dos renglones: si pasa de [largo] letras, se parte por el « · » más cercano a la mitad. */
private fun renglonesFechaFan(f: String, largo: Int): List<String> {
    if (f.length <= largo) return listOf(f)
    val cortes = Regex(" · ").findAll(f).map { it.range.first }.toList()
    if (cortes.isEmpty()) return listOf(f)
    val k = cortes.minBy { kotlin.math.abs(it - f.length / 2) }
    return listOf(f.substring(0, k), f.substring(k + 3))
}

/** «1 palabra», «N palabras». */
private fun palabrasFan(n: Int): String = if (n == 1) "1 palabra" else "$n palabras"

// =====================================================================
//  Ayudantes de la página abierta de la Fantasía
// =====================================================================

/**
 * Un color del tema puesto sobre el papel [p]: tal cual de día; en modo oscuro (papel claro), hundido como el papel en su
 * penumbra; sobre un papel oscuro (el de un sueño), casi fundido con él para que la tinta clara siga leyéndose.
 */
private fun tonoFan(c: Color, p: Pagina, oscuro: Boolean): Color = when {
    p.oscura -> mezcla(c, p.color, 0.6f)
    oscuro -> mezcla(c, Color(0xFF0E0A07), 0.24f)
    else -> c
}

/**
 * La fecha del encabezado en [r]: en un renglón si cabe en [largo] letras; si no, partida por el « · » más cercano a la mitad,
 * el primer renglón grande y el segundo menor (o, si no tiene « · », en dos renglones parejos partidos por un espacio).
 */
private fun DrawScope.fechaEncFan(medidor: TextMeasurer, f: String, r: Rect, letra: FontFamily, color: Color, tam: Float, largo: Int,
                                  peso: FontWeight = FontWeight.Normal) {
    val renglones = renglonesFechaFan(f, largo)
    val espacio = f.indices.filter { f[it] == ' ' }.minByOrNull { kotlin.math.abs(it - f.length / 2) }
    if (renglones.size == 1 && f.length > largo + 4 && espacio != null) {
        // Larga y sin « · »: dos renglones parejos, partidos por el espacio más cercano a la mitad.
        val corte = r.top + r.height * 0.5f
        textoEn(medidor, f.substring(0, espacio), Rect(r.left, r.top, r.right, corte), letra, color, tam * 0.78f, peso = peso, minimo = 9f)
        textoEn(medidor, f.substring(espacio + 1), Rect(r.left, corte, r.right, r.bottom), letra, color, tam * 0.78f, peso = peso, minimo = 9f)
    } else if (renglones.size == 1) {
        textoEn(medidor, renglones[0], r, letra, color, tam, peso = peso, minimo = 9f)
    } else {
        val corte = r.top + r.height * 0.6f
        textoEn(medidor, renglones[0], Rect(r.left, r.top, r.right, corte), letra, color, tam * 0.84f, peso = peso, minimo = 9f)
        textoEn(medidor, renglones[1], Rect(r.left, corte, r.right, r.bottom), letra, color.copy(alpha = color.alpha * 0.85f), tam * 0.6f, minimo = 8f)
    }
}

/**
 * La penumbra del modo oscuro sobre un papel claro: un velo parejo del 12 %, las orillas que se hunden (una elipse que deja
 * claro el centro, donde va el texto, y llega a cerca del 60 % en las esquinas) y la luz cálida que llega desde [foco] (la
 * lucerna, la vela, el fuego) con fuerza [fuerza]. Dos degradados.
 */
private fun DrawScope.penumbraFan(foco: Offset, luz: Color, fuerza: Float) {
    val w = size.width; val h = size.height
    drawRect(Color.Black.copy(alpha = 0.12f))
    val centro = Offset(w * 0.5f + (foco.x - w * 0.5f) * 0.12f, h * 0.45f + (foco.y - h * 0.45f) * 0.12f)
    clipRect {
        withTransform({ scale(1f, h / w, centro) }) {
            drawRect(Brush.radialGradient(0f to Color.Transparent, 0.42f to Color.Black.copy(alpha = 0.06f), 0.72f to Color.Black.copy(alpha = 0.3f),
                1f to Color.Black.copy(alpha = 0.55f), center = centro, radius = w * 0.72f), Offset(-w, centro.y - h), Size(3 * w, 2 * h))
        }
    }
    drawRect(Brush.radialGradient(0f to luz.copy(alpha = fuerza), 0.4f to luz.copy(alpha = fuerza * 0.4f), 1f to Color.Transparent,
        center = foco, radius = maxOf(w, h) * 0.62f))
}


// =====================================================================
//  Ayudantes de las cartas de la Fantasía
// =====================================================================

/**
 * La hoja de la carta que sale de su objeto, del papel [hoja] con sus [renglones]: va de [izq] a [der] (fracciones del ancho
 * de [cuerpo]) y sube con [subir] hasta el 72 % del alto del cuerpo (a 0.5, la de la rejilla, asoma por encima del cuerpo);
 * la tapa el frente que se dibuja después. [borde] (una semilla) le da un contorno irregular (piel raspada, pergamino);
 * [fibras], el cruce de las fibras del papiro; [margen], un filete a la izquierda. En modo oscuro se apaga solo un poco:
 * encima van las primeras palabras, con la tinta del tema. Devuelve dónde quedó la hoja.
 */
private fun DrawScope.hojaCartaFan(cuerpo: Rect, subir: Float, hoja: Color, renglones: Color, oscuro: Boolean, izq: Float = 0.12f, der: Float = 0.88f,
                                   borde: Int? = null, fibras: Boolean = false, margen: Color? = null): Rect {
    val u = density
    val H = cuerpo.height; val W = cuerpo.width
    val arriba = cuerpo.top + H * 0.07f - H * 0.72f * subir.coerceIn(0f, 1f)
    val r = Rect(cuerpo.left + W * izq, arriba, cuerpo.left + W * der, arriba + H * 0.84f)
    val papel = if (oscuro) mezcla(hoja, Color.Black, 0.1f) else hoja
    val forma = if (borde != null) contornoIrregular(r, borde, 34, 0.012f) else Path().apply { addRect(r) }
    translate(1.5f * u, 2f * u) { drawPath(forma, Color.Black.copy(alpha = 0.24f)) }
    drawPath(forma, papel)
    clipPath(forma) {
        if (fibras) {
            var y = r.top + 2 * u
            while (y < r.bottom) { drawLine(Color(0xFF8A6E3A).copy(alpha = 0.1f), Offset(r.left, y), Offset(r.right, y), 0.7f * u); y += 2.6f * u }
            var x = r.left + 3 * u
            while (x < r.right) { drawLine(Color(0xFF8A6E3A).copy(alpha = 0.06f), Offset(x, r.top), Offset(x, r.bottom), 0.7f * u); x += 3.4f * u }
        }
        margen?.let { drawLine(it, Offset(r.left + r.width * 0.06f, r.top), Offset(r.left + r.width * 0.06f, r.bottom), 0.9f * u) }
        val paso = maxOf(H * 0.075f, 6 * u)
        var y = r.top + H * 0.1f
        var i = 0
        while (y < r.bottom - 2 * u && i < 12) {
            drawLine(renglones, Offset(r.left + r.width * 0.1f, y), Offset(r.right - r.width * (if (i % 4 == 3) 0.35f else 0.08f), y), 1.1f * u, StrokeCap.Round)
            y += paso; i++
        }
    }
    return r
}

/** Un disco de contorno irregular (arcilla aplastada, cera, barro), de [n] puntos, cada uno hundido hasta [hondo] del radio. */
private fun discoFan(c: Offset, r: Float, semilla: Int, n: Int = 20, hondo: Float = 0.08f): Path = Path().apply {
    for (k in 0 until n) {
        val a = (2 * Math.PI * k / n).toFloat()
        val rr = r * (1f - hondo * azar(semilla, k))
        val p = Offset(c.x + rr * cos(a), c.y + rr * sin(a))
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/**
 * El sello de una carta en [c] (radio [r]). Con [brillo], el resplandor de la que ya se puede abrir (un degradado, del color
 * [luz]) y cuatro destellos; con [romper], se parte por una grieta en zigzag y las dos mitades se apartan y giran; [escala]
 * y [alfa] son el sello que cae al sellar (grande y transparente, hasta posarse). [dibujar] pinta el sello entero, centrado
 * en [c].
 */
private fun DrawScope.selloCartaFan(c: Offset, r: Float, brillo: Float, romper: Float, escala: Float, alfa: Float, luz: Color,
                                    dibujar: DrawScope.() -> Unit) {
    if (alfa <= 0.01f) return
    if (brillo > 0f) {
        drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.8f * brillo), luz.copy(alpha = 0.3f * brillo), Color.Transparent), c, r * 3f), r * 3f, c)
        for (j in 0 until 4) {
            val a = (j * Math.PI / 2 + Math.PI / 4).toFloat()
            val p = Offset(c.x + r * 1.55f * cos(a), c.y + r * 1.4f * sin(a))
            drawPath(estrella(p, r * (0.2f + 0.18f * brillo), 4, 0.25f, giro = 0f), luz.copy(alpha = brillo))
        }
    }
    val pintar: DrawScope.() -> Unit = {
        if (romper <= 0f) withTransform({ scale(escala, escala, c) }) { dibujar() }
        else {
            val d = r * 0.42f * romper
            for (lado in listOf(-1f, 1f)) {
                val mitad = Path().apply {
                    moveTo(c.x + lado * r * 3f, c.y - r * 3f); lineTo(c.x, c.y - r * 3f)
                    for (j in 0..6) lineTo(c.x + (if (j % 2 == 0) -1f else 1f) * r * 0.16f, c.y - r * 1.2f + j * r * 0.4f)
                    lineTo(c.x, c.y + r * 3f); lineTo(c.x + lado * r * 3f, c.y + r * 3f); close()
                }
                withTransform({ translate(lado * d, r * 0.12f * romper); rotate(lado * 18f * romper, c) }) { clipPath(mitad) { dibujar() } }
            }
        }
    }
    if (alfa < 0.999f) {
        val m = r * 4f * maxOf(escala, 1f)
        drawContext.canvas.saveLayer(Rect(c.x - m, c.y - m, c.x + m, c.y + m), Paint().apply { this.alpha = alfa })
        pintar()
        drawContext.canvas.restore()
    } else pintar()
}

/** Un trazo de [puntos] que se dibuja solo hasta [avance] (0 a 1) de su largo: el cordón que se va atando. */
private fun DrawScope.trazoParcialFan(puntos: List<Offset>, avance: Float, color: Color, grosor: Float) {
    if (avance <= 0f || puntos.size < 2) return
    val largos = puntos.zipWithNext { a, b -> (b - a).getDistance() }
    var queda = largos.sum() * avance.coerceIn(0f, 1f)
    val p = Path().apply { moveTo(puntos[0].x, puntos[0].y) }
    for (i in largos.indices) {
        if (queda <= 0f) break
        val f = minOf(1f, queda / largos[i].coerceAtLeast(0.01f))
        val q = puntos[i] + (puntos[i + 1] - puntos[i]) * f
        p.lineTo(q.x, q.y); queda -= largos[i]
    }
    drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

/** Los dos renglones del sobre: [l1] (a quién va) más grande en [r1] y [l2] (las fechas) en [r2], si no vienen vacíos. */
private fun DrawScope.lineasCartaFan(medidor: TextMeasurer, l1: String, l2: String, r1: Rect, r2: Rect, letra: FontFamily, tinta: Color,
                                     tam1: Float = 12f, tam2: Float = 9.5f, peso1: FontWeight = FontWeight.Bold) {
    textoEn(medidor, l1, r1, letra, tinta, tam1, peso = peso1, minimo = 7f)
    textoEn(medidor, l2, r2, letra, tinta.copy(alpha = tinta.alpha * 0.9f), tam2, minimo = 7f)
}
