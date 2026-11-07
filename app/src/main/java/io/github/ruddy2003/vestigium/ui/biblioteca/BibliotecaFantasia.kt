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
