package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
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
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.manometro
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Edad Contemporánea
// =====================================================================

/**
 * Urbe: la librería del barrio. Bajo un arco rebajado de travertino, con sus dovelas y su clave, un estante de montantes de
 * hierro pintado y tablas de nogal sobre escuadras, contra un muro de revoque ocre. En el tímpano del arco, la ventana con sus
 * persianas verdes de lamas abiertas a los lados; sobre la tabla de arriba, una cafetera moka y una maceta de albahaca.
 * Abajo, el zócalo de travertino y el piso de granito. Los meses van en portaprecios de lata con su tarjeta blanca. Cada
 * entrada es un quaderno de tapa dura de pie, con su liga elástica, la etiqueta blanca con el día y el mes, el sello en seco
 * de la tapa y boletos de autobús que asoman por arriba (más cuanto más escribiste); de noche, una luna en lugar del sello.
 * De noche, la luz de los faroles de la calle entra por las lamas y raya el muro. Distinto del Ideario, que es la edícola de
 * hierro verde de los periódicos.
 */
object EstanteUrbe : DisenoBiblioteca {
    private val TRAVERTINO = Color(0xFFE2D5BC)
    private val TRAVERTINO_SOMBRA = Color(0xFFC2B190)
    private val TRAVERTINO_PORO = Color(0xFFA8956F)
    private val REVOQUE = Color(0xFFD9A867)
    private val REVOQUE_SOMBRA = Color(0xFFB9874B)
    private val HIERRO = Color(0xFF2C3330)
    private val HIERRO_LUZ = Color(0xFF55605A)
    private val NOGAL = Color(0xFF7A5232)
    private val NOGAL_LUZ = Color(0xFF9C6F45)
    private val NOGAL_VETA = Color(0xFF4E321D)
    private val PERSIANA = Color(0xFF3E6B45)
    private val PERSIANA_LUZ = Color(0xFF5E8C63)
    private val VIDRIO = Color(0xFF9DB4BF)
    private val ALUMINIO = Color(0xFFB4B8BA)
    private val BARRO = Color(0xFFB4643A)
    private val ALBAHACA = Color(0xFF4F8A3A)
    private val ALBAHACA_LUZ = Color(0xFF7FB25A)
    private val PAPEL = Color(0xFFF6F3EA)
    private val TINTA = Color(0xFF1E2224)
    private val ROJO = Color(0xFFB0413A)
    private val GRANITO = Color(0xFF9A948A)
    /** Las tapas de los quaderni: negro, rojo, ocre, petróleo, azul de Prusia y verde salvia. */
    private val TAPAS = listOf(Color(0xFF26272A), Color(0xFFA2382E), Color(0xFFC08A3A), Color(0xFF2C5A60), Color(0xFF283F66), Color(0xFF6E8768))
    /** Los boletos: amarillo, naranja, rosa y verde claro. */
    private val BOLETOS = listOf(Color(0xFFE9D46A), Color(0xFFE59A4D), Color(0xFFE6A3AE), Color(0xFFA9CF96))
    private val NOCHE = Color(0xFF1A2236)
    private val CALLE = Color(0xFF141A2A)
    private val FAROL = Color(0xFFF2C46A)
    private val LUNA = Color(0xFFF1EBD2)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo el cielo de la calle. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100E0C), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 110.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = Letra.placa

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.78f + 0.16f * azar(semilla, 3)

    /** Los poros del travertino: rayitas horizontales al azar. */
    private fun DrawScope.poros(r: Rect, color: Color, semilla: Int) {
        val u = density
        val n = (r.width * r.height / (180f * u * u)).toInt().coerceIn(4, 160)
        for (k in 0 until n) {
            val x = r.left + r.width * azar(semilla, k)
            val y = r.top + r.height * azar(semilla, k + 500)
            val l = (1.5f + 3.5f * azar(semilla, k + 900)) * u
            drawLine(color.copy(alpha = 0.55f), Offset(x, y), Offset((x + l).coerceAtMost(r.right), y), 0.9f * u)
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro del fondo: revoque ocre, con sus manchas de humedad y una grieta.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(REVOQUE), cuerpo.topLeft, cuerpo.size)
        for (k in 0 until 7) {
            val o = Offset(cuerpo.left + cuerpo.width * azar(5, k), cuerpo.top + cuerpo.height * azar(6, k))
            drawOval(c(REVOQUE_SOMBRA).copy(alpha = 0.22f), Offset(o.x - 18 * u, o.y - 10 * u), Size(36 * u, 20 * u))
        }
        val grieta = Path().apply {
            moveTo(cuerpo.left + cuerpo.width * 0.72f, cuerpo.top + 20 * u)
            lineTo(cuerpo.left + cuerpo.width * 0.7f, cuerpo.top + 34 * u)
            lineTo(cuerpo.left + cuerpo.width * 0.74f, cuerpo.top + 46 * u)
            lineTo(cuerpo.left + cuerpo.width * 0.72f, cuerpo.top + 62 * u)
        }
        drawPath(grieta, c(REVOQUE_SOMBRA), style = Stroke(0.8f * u))
        // De noche, la luz de los faroles entra por las lamas de las persianas y raya el muro de arriba.
        if (noche) {
            val alcanceLuz = minOf(cuerpo.bottom, cuerpo.top + 300 * u)
            clipRect(cuerpo.left, cuerpo.top, cuerpo.right, alcanceLuz) {
                var y = cuerpo.top - cuerpo.width * 0.35f
                while (y < alcanceLuz) {
                    val franja = Path().apply {
                        moveTo(cuerpo.left, y); lineTo(cuerpo.right, y + cuerpo.width * 0.35f)
                        lineTo(cuerpo.right, y + cuerpo.width * 0.35f + 6 * u); lineTo(cuerpo.left, y + 6 * u); close()
                    }
                    drawPath(franja, FAROL.copy(alpha = 0.1f))
                    y += 15 * u
                }
            }
        }
        // Cada repisa: la sombra de la tabla de arriba y de los montantes.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.3f else 0.2f), rep.topLeft, Size(rep.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.1f), rep.topLeft, Size(rep.width, 14 * u))
            drawRect(Color.Black.copy(alpha = 0.12f), rep.topLeft, Size(6 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.12f), Offset(rep.right - 6 * u, rep.top), Size(6 * u, rep.height))
        }
        // Los costados: las jambas de travertino del arco, con el montante de hierro perforado por dentro.
        listOf(d.izquierda to true, d.derecha to false).forEach { (lad, izq) ->
            drawRect(c(TRAVERTINO), lad.topLeft, lad.size)
            poros(lad, c(TRAVERTINO_PORO), if (izq) 61 else 62)
            var yy = lad.top + 34 * u
            while (yy < lad.bottom) { drawLine(c(TRAVERTINO_SOMBRA), Offset(lad.left, yy), Offset(lad.right, yy), 1f * u); yy += 34 * u }
            val m = lad.width * 0.3f
            val mx = if (izq) lad.right - m else lad.left
            drawRect(c(HIERRO), Offset(mx, lad.top), Size(m, lad.height))
            drawLine(c(HIERRO_LUZ), Offset(mx + m * 0.25f, lad.top), Offset(mx + m * 0.25f, lad.bottom), 0.8f * u)
            var hy = lad.top + 6 * u
            while (hy < lad.bottom - 6 * u) {
                drawRoundRect(Color.Black.copy(alpha = 0.6f), Offset(mx + m * 0.42f, hy), Size(m * 0.32f, 4 * u), CornerRadius(1 * u))
                hy += 9 * u
            }
            bisel(lad, Color.White.copy(alpha = 0.16f), Color.Black.copy(alpha = 0.16f), 1.2f * u)
        }
        // Las tablas de nogal sobre sus escuadras de hierro.
        d.tablas.forEachIndexed { k, tb ->
            drawRect(c(NOGAL), tb.topLeft, tb.size)
            vetas(tb, c(NOGAL_VETA).copy(alpha = 0.45f), 70 + k, densidad = 0.7f, grosor = 0.7f)
            drawRect(c(NOGAL_LUZ), tb.topLeft, Size(tb.width, 2 * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(tb.left, tb.bottom), Size(tb.width, 3 * u))
            listOf(tb.left + 12 * u, tb.right - 12 * u).forEach { x ->
                val escuadra = Path().apply { moveTo(x - 6 * u, tb.bottom); lineTo(x + 6 * u, tb.bottom); lineTo(x, tb.bottom + 7 * u); close() }
                drawPath(escuadra, c(HIERRO))
            }
        }
        // El portaprecios de lata de cada mes, con su tarjeta blanca y un filete verde.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1 * u, r.top + 1.2f * u), r.size)
            drawRect(c(ALUMINIO), r.inflate(1.4f * u).topLeft, r.inflate(1.4f * u).size)
            drawRect(c(PAPEL), r.topLeft, r.size)
            drawLine(c(PERSIANA), Offset(r.left, r.top + 1.2f * u), Offset(r.right, r.top + 1.2f * u), 1.2f * u)
            textoEn(medidor, p.texto, Rect(r.left + 4 * u, r.top + 1.5f * u, r.right - 4 * u, r.bottom), letra, c(TINTA), 10f, peso = FontWeight.SemiBold, espaciado = 1f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de travertino con su moldura y el piso de granito moteado.
        val b = d.base
        val piso = b.top + b.height * 0.6f
        drawRect(c(GRANITO), Offset(0f, piso), Size(w, h - piso))
        for (k in 0 until 120) {
            val o = Offset(w * azar(81, k), piso + (h - piso) * azar(82, k))
            val col = when (k % 3) { 0 -> TRAVERTINO; 1 -> TINTA; else -> BARRO }
            drawCircle(c(col).copy(alpha = 0.55f), (0.6f + 1.2f * azar(83, k)) * u, o)
        }
        drawRect(c(TRAVERTINO), b.topLeft, Size(b.width, piso - b.top))
        poros(Rect(b.left, b.top, b.right, piso), c(TRAVERTINO_PORO), 84)
        drawRect(c(TRAVERTINO_SOMBRA), b.topLeft, Size(b.width, 2.5f * u))
        drawLine(c(TRAVERTINO_SOMBRA), Offset(b.left, b.top + (piso - b.top) * 0.45f), Offset(b.right, b.top + (piso - b.top) * 0.45f), 1f * u)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(b.left, piso), Size(b.width, 3 * u))
    }

    /** El arco de travertino, la ventana con sus persianas, y la moka y la albahaca sobre la tabla de arriba. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // El paño de travertino en hiladas.
        drawRect(c(TRAVERTINO), r.topLeft, r.size)
        poros(r, c(TRAVERTINO_PORO), 63)
        var y = r.top + r.height * 0.18f
        while (y < r.bottom) { drawLine(c(TRAVERTINO_SOMBRA), Offset(r.left, y), Offset(r.right, y), 1f * u); y += r.height * 0.18f }
        // El vano del arco rebajado: el muro ocre por dentro.
        val izq = r.left + r.width * lado; val der = r.right - r.width * lado
        val flecha = r.height * 0.55f
        val clave = r.top + r.height * 0.14f
        val vano = Path().apply {
            moveTo(izq, r.bottom); lineTo(izq, clave + flecha)
            arcTo(Rect(izq, clave, der, clave + flecha * 2), 180f, 180f, false)
            lineTo(der, r.bottom); close()
        }
        drawPath(vano, c(REVOQUE))
        // Las dovelas: juntas que salen del intradós, la rosca y la clave.
        val e = r.height * 0.1f
        val cx = r.center.x; val cy = clave + flecha
        val ax = (der - izq) / 2; val ay = flecha
        for (k in 0..14) {
            val a = (Math.PI * (1.0 + k / 14.0)).toFloat()
            val co = cos(a); val si = sin(a)
            drawLine(c(TRAVERTINO_SOMBRA), Offset(cx + ax * co, cy + ay * si), Offset(cx + (ax + e) * co, cy + (ay + e) * si), 1f * u)
        }
        drawArc(c(TRAVERTINO_SOMBRA), 180f, 180f, false, Offset(izq - e, clave - e), Size(der - izq + 2 * e, flecha * 2 + 2 * e), style = Stroke(1f * u))
        drawPath(vano, c(TRAVERTINO_SOMBRA), style = Stroke(1.6f * u))
        val piedraClave = Path().apply {
            moveTo(cx - e * 0.45f, clave - e * 1.15f); lineTo(cx + e * 0.45f, clave - e * 1.15f)
            lineTo(cx + e * 0.32f, clave + e * 0.1f); lineTo(cx - e * 0.32f, clave + e * 0.1f); close()
        }
        drawPath(piedraClave, c(TRAVERTINO))
        drawPath(piedraClave, c(TRAVERTINO_SOMBRA), style = Stroke(1f * u))
        // La ventana del tímpano: de día, el vidrio con el reflejo; de noche, la calle con su farol.
        val tope = r.bottom - r.height * 0.1f
        val win = Rect(cx - r.width * 0.08f, clave + flecha * 0.4f, cx + r.width * 0.08f, tope - r.height * 0.1f)
        drawRect(if (noche) Color(0xFF1C2742) else c(VIDRIO), win.topLeft, win.size)
        clipRect(win.left, win.top, win.right, win.bottom) {
            if (noche) {
                val f = Offset(win.left + win.width * 0.68f, win.top + win.height * 0.36f)
                mancha(f, win.width * 0.6f, FAROL.copy(alpha = 0.55f))
                drawLine(CALLE, Offset(win.right, f.y - win.height * 0.2f), Offset(f.x, f.y - win.height * 0.2f), 1.6f * u)
                drawLine(CALLE, Offset(f.x, f.y - win.height * 0.2f), Offset(f.x, f.y - win.height * 0.12f), 1.2f * u)
                val farol = Path().apply {
                    moveTo(f.x - win.width * 0.07f, f.y - win.height * 0.12f); lineTo(f.x + win.width * 0.07f, f.y - win.height * 0.12f)
                    lineTo(f.x + win.width * 0.05f, f.y + win.height * 0.08f); lineTo(f.x - win.width * 0.05f, f.y + win.height * 0.08f); close()
                }
                drawPath(farol, FAROL)
                drawPath(farol, CALLE, style = Stroke(1f * u))
                // Los edificios de enfrente, con alguna ventana encendida.
                drawRect(CALLE, Offset(win.left, win.top + win.height * 0.62f), Size(win.width, win.height * 0.38f))
                for (j in 0 until 3) drawRect(FAROL.copy(alpha = 0.5f), Offset(win.left + win.width * (0.12f + 0.3f * j), win.top + win.height * 0.72f), Size(win.width * 0.1f, win.height * 0.1f))
            } else {
                drawRect(c(TRAVERTINO_SOMBRA).copy(alpha = 0.6f), Offset(win.left, win.top + win.height * 0.62f), Size(win.width, win.height * 0.38f))
                drawLine(Color.White.copy(alpha = 0.35f), Offset(win.left, win.top + win.height * 0.7f), Offset(win.left + win.width * 0.6f, win.top), 4 * u)
            }
        }
        drawLine(c(PERSIANA), Offset(win.center.x, win.top), Offset(win.center.x, win.bottom), 2 * u)
        drawLine(c(PERSIANA), Offset(win.left, win.top + win.height * 0.4f), Offset(win.right, win.top + win.height * 0.4f), 2 * u)
        drawRect(c(PERSIANA), win.topLeft, win.size, style = Stroke(2.4f * u))
        // Las persianas verdes abiertas a los lados, con sus lamas.
        listOf(win.left - win.width * 0.48f to win.left, win.right to win.right + win.width * 0.48f).forEach { (a, b) ->
            val hoja = Rect(a, win.top - 1 * u, b, win.bottom + 1 * u)
            drawRect(c(PERSIANA), hoja.topLeft, hoja.size)
            var ly = hoja.top + 3 * u
            while (ly < hoja.bottom - 2 * u) {
                drawLine(c(PERSIANA_LUZ), Offset(hoja.left + 2 * u, ly), Offset(hoja.right - 2 * u, ly), 1f * u)
                drawLine(Color.Black.copy(alpha = 0.35f), Offset(hoja.left + 2 * u, ly + 1.2f * u), Offset(hoja.right - 2 * u, ly + 1.2f * u), 0.6f * u)
                ly += 3.2f * u
            }
            drawRect(Color.Black.copy(alpha = 0.4f), hoja.topLeft, hoja.size, style = Stroke(1f * u))
        }
        // El alféizar.
        drawRect(c(TRAVERTINO), Offset(win.left - win.width * 0.55f, win.bottom), Size(win.width * 2.1f, 3.5f * u))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(win.left - win.width * 0.55f, win.bottom + 3.5f * u), Size(win.width * 2.1f, 2 * u))
        // La tabla de arriba, de nogal con su labio de hierro.
        val alta = Rect(izq, tope, der, r.bottom)
        drawRect(c(NOGAL), alta.topLeft, alta.size)
        vetas(alta, c(NOGAL_VETA).copy(alpha = 0.45f), 64, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(HIERRO), Offset(izq, r.bottom - 2.5f * u), Size(der - izq, 2.5f * u))
        // La moka: dos cuerpos ochavados de aluminio, la cintura, la tapa con su perilla, el pico y el asa negra.
        val mx = izq + (der - izq) * 0.16f
        val s = r.height * 0.36f
        val inferior = Path().apply { moveTo(mx - s * 0.32f, tope); lineTo(mx + s * 0.32f, tope); lineTo(mx + s * 0.22f, tope - s * 0.42f); lineTo(mx - s * 0.22f, tope - s * 0.42f); close() }
        val superior = Path().apply { moveTo(mx - s * 0.22f, tope - s * 0.47f); lineTo(mx + s * 0.22f, tope - s * 0.47f); lineTo(mx + s * 0.3f, tope - s * 0.86f); lineTo(mx - s * 0.3f, tope - s * 0.86f); close() }
        val pico = Path().apply { moveTo(mx - s * 0.28f, tope - s * 0.86f); lineTo(mx - s * 0.42f, tope - s * 0.92f); lineTo(mx - s * 0.26f, tope - s * 0.72f); close() }
        val asa = Path().apply { moveTo(mx + s * 0.27f, tope - s * 0.82f); lineTo(mx + s * 0.46f, tope - s * 0.8f); lineTo(mx + s * 0.4f, tope - s * 0.52f); lineTo(mx + s * 0.22f, tope - s * 0.54f) }
        drawPath(asa, c(TINTA), style = Stroke(3 * u))
        drawPath(inferior, c(ALUMINIO)); drawPath(superior, c(ALUMINIO)); drawPath(pico, c(ALUMINIO))
        listOf(-0.08f, 0.08f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.2f), Offset(mx + s * f, tope), Offset(mx + s * f * 1.2f, tope - s * 0.86f), 0.8f * u) }
        drawLine(Color.White.copy(alpha = 0.35f), Offset(mx - s * 0.14f, tope - s * 0.05f), Offset(mx - s * 0.12f, tope - s * 0.8f), 1f * u)
        drawRect(c(HIERRO_LUZ), Offset(mx - s * 0.24f, tope - s * 0.48f), Size(s * 0.48f, s * 0.05f))
        val tapa = Path().apply { moveTo(mx - s * 0.3f, tope - s * 0.86f); lineTo(mx + s * 0.3f, tope - s * 0.86f); lineTo(mx + s * 0.18f, tope - s * 0.96f); lineTo(mx - s * 0.18f, tope - s * 0.96f); close() }
        drawPath(tapa, c(ALUMINIO))
        drawPath(tapa, Color.Black.copy(alpha = 0.25f), style = Stroke(0.7f * u))
        drawCircle(c(TINTA), s * 0.05f, Offset(mx, tope - s * 0.99f))
        // Dos libros tumbados en medio.
        listOf(Triple(0.42f, 0.2f, TAPAS[1]), Triple(0.44f, 0.17f, TAPAS[4])).forEachIndexed { k, (x0, an, col) ->
            val alto0 = r.height * 0.055f
            val top = tope - alto0 * (k + 1)
            drawRect(c(col), Offset(izq + (der - izq) * x0, top), Size((der - izq) * an, alto0))
            drawRect(c(PAPEL).copy(alpha = 0.85f), Offset(izq + (der - izq) * (x0 + an * 0.9f), top + alto0 * 0.15f), Size((der - izq) * an * 0.08f, alto0 * 0.7f))
        }
        // La maceta de albahaca.
        val px = der - (der - izq) * 0.17f
        val pa = r.height * 0.26f
        val boca = tope - pa * 0.72f
        val maceta = Path().apply { moveTo(px - pa * 0.42f, tope - pa * 0.62f); lineTo(px + pa * 0.42f, tope - pa * 0.62f); lineTo(px + pa * 0.3f, tope); lineTo(px - pa * 0.3f, tope); close() }
        val hojas = (0 until 11).map { k ->
            val a = (Math.PI * (1.05 + 0.9 * k / 10.0)).toFloat()
            val rr = pa * (0.35f + 0.35f * azar(77, k))
            Offset(px + rr * cos(a) * 1.2f, boca + rr * sin(a))
        }
        hojas.forEach { o -> drawLine(c(ALBAHACA).copy(alpha = 0.8f), Offset(px, boca), o, 0.7f * u) }
        hojas.forEachIndexed { k, o ->
            rotate((k * 33f) % 180f - 90f, o) {
                drawOval(c(if (k % 3 == 0) ALBAHACA else ALBAHACA_LUZ), Offset(o.x - pa * 0.13f, o.y - pa * 0.07f), Size(pa * 0.26f, pa * 0.14f))
            }
        }
        drawPath(maceta, c(BARRO))
        drawRect(c(BARRO), Offset(px - pa * 0.48f, boca), Size(pa * 0.96f, pa * 0.14f))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(px - pa * 0.48f, boca + pa * 0.14f), Size(pa * 0.96f, pa * 0.04f))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = TAPAS[(v.semilla and 0x7fffffff) % TAPAS.size]
        val tapa = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val arriba = h * 0.07f
        // Los boletos de autobús que asoman por arriba: más cuanto más escribiste.
        val boletos = 1 + (v.lleno * 3).toInt()
        for (j in 0 until boletos) {
            val bw = w * 0.28f
            val bx = w * 0.16f + (w * 0.56f - bw) * azar(v.semilla, 20 + j) + w * 0.06f * j
            val sale = arriba * (0.5f + 0.5f * azar(v.semilla, 30 + j))
            val boleto = Rect(bx.coerceAtMost(w * 0.8f - bw), arriba - sale, bx.coerceAtMost(w * 0.8f - bw) + bw, arriba + 2 * u)
            drawRect(c(BOLETOS[(j + (v.semilla and 3)) % BOLETOS.size]), boleto.topLeft, boleto.size)
            drawLine(c(TINTA).copy(alpha = 0.4f), Offset(boleto.left + bw * 0.2f, boleto.top + sale * 0.45f), Offset(boleto.right - bw * 0.2f, boleto.top + sale * 0.45f), 0.6f * u)
        }
        // La tapa dura, con el lomo de tela a la izquierda.
        drawRoundRect(tapa, Offset(0f, arriba), Size(w, h - arriba), CornerRadius(2.5f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(0f, arriba), Size(w * 0.14f, h - arriba))
        drawLine(Color.White.copy(alpha = 0.12f), Offset(w * 0.15f, arriba), Offset(w * 0.15f, h), 0.8f * u)
        drawRect(Color.White.copy(alpha = 0.07f), Offset(w * 0.18f, arriba), Size(w * 0.1f, h - arriba))
        // La etiqueta blanca con el día y el mes.
        val et = Rect(w * 0.22f, h * 0.16f, w * 0.78f, h * 0.62f)
        drawRect(c(PAPEL), et.topLeft, et.size)
        drawRect(c(TINTA).copy(alpha = 0.5f), et.topLeft, et.size, style = Stroke(0.6f * u))
        drawLine(c(ROJO), Offset(et.left, et.top + et.height * 0.1f), Offset(et.right, et.top + et.height * 0.1f), 0.7f * u)
        textoEn(medidor, v.dia, Rect(et.left, et.top + et.height * 0.12f, et.right, et.top + et.height * 0.4f), letra, c(TINTA), 14f, peso = FontWeight.Bold)
        textoEn(medidor, v.mes, Rect(et.left + 1 * u, et.top + et.height * 0.42f, et.right - 1 * u, et.bottom - 2 * u), letra, c(TINTA).copy(alpha = 0.85f), 8f, vertical = true, minimo = 4.5f)
        // La liga elástica que cierra el cuaderno.
        drawRect(Color.Black.copy(alpha = 0.55f), Offset(w * 0.83f, arriba), Size(w * 0.07f, h - arriba))
        drawLine(Color.White.copy(alpha = 0.18f), Offset(w * 0.845f, arriba), Offset(w * 0.845f, h), 0.6f * u)
        // Abajo, el sello en seco de la tapa; de noche, una luna.
        val cs = Offset(w * 0.5f, h * 0.8f)
        val s = minOf(w * 0.4f, 10 * u)
        if (v.noche) {
            drawCircle(LUNA, s * 0.42f, cs)
            drawCircle(tapa, s * 0.36f, Offset(cs.x + s * 0.18f, cs.y - s * 0.1f))
            drawCircle(LUNA.copy(alpha = 0.8f), 0.8f * u, Offset(cs.x - s * 0.6f, cs.y + s * 0.5f))
        } else {
            drawRoundRect(Color.White.copy(alpha = 0.14f), Offset(cs.x - s / 2, cs.y - s * 0.3f), Size(s, s * 0.6f), CornerRadius(s * 0.3f), style = Stroke(0.8f * u))
            drawLine(Color.White.copy(alpha = 0.14f), Offset(cs.x - s * 0.25f, cs.y), Offset(cs.x + s * 0.25f, cs.y), 0.8f * u)
        }
        // Las esquinas redondeadas un poco gastadas.
        drawRect(Color.White.copy(alpha = 0.06f), Offset(0f, h - 2 * u), Size(w, 2 * u))
    }
}

/**
 * Terminal: el escritorio de noche, siempre en oscuro. Arriba, el borde inferior del monitor de tubo, con su carcasa de
 * plástico, el piloto verde y la pantalla de fósforo con el prompt y el cursor; debajo, la caja de disquetes con su tapa
 * ahumada abierta hacia atrás y sus bisagras. Cada repisa es una fila de la caja, separada por un tabique de plástico; los
 * meses van en las pestañas de los separadores. Abajo, el frente de la caja y la mesa, con el cable del ratón. Cada entrada
 * es un disquete de 3½ de pie, de plástico de color, con su obturador de metal y su ventanilla, la flecha de inserción, las
 * ventanas de protección y la etiqueta escrita a mano con el día y el mes (más renglones cuanto más escribiste); de noche,
 * una luna en lugar de la flecha. De noche la pantalla resplandece y baña la caja de luz de fósforo. Distinto del Ideario,
 * que es la ventana de la consola con su árbol de directorios.
 */
object EstanteCiber : DisenoBiblioteca {
    private val FONDO = Color(0xFF0B0D10)
    private val PLASTICO = Color(0xFF34373B)
    private val PLASTICO_LUZ = Color(0xFF5A5E63)
    private val PLASTICO_OSC = Color(0xFF1B1D20)
    private val AHUMADO = Color(0xFF6B5E52)
    private val CARCASA = Color(0xFFC4BCA6)
    private val CARCASA_SOMBRA = Color(0xFF948C76)
    private val PANTALLA = Color(0xFF06110F)
    private val FOSFORO = Color(0xFF4FE3D6)
    private val FOSFORO_TENUE = Color(0xFF237A72)
    private val PILOTO = Color(0xFF5CF06A)
    private val MESA = Color(0xFF4A4038)
    private val MESA_VETA = Color(0xFF2E2620)
    private val CABLE = Color(0xFF8E9296)
    private val METAL = Color(0xFFB9BEC3)
    private val ETIQUETA = Color(0xFFF0EEE6)
    private val BOLIGRAFO = Color(0xFF1C2E8C)
    /** El plástico de los disquetes: negro, azul, rojo, gris, verde y beige. */
    private val DISCOS = listOf(Color(0xFF2A2B2F), Color(0xFF3E5584), Color(0xFF8A3239), Color(0xFF6C7074), Color(0xFF356657), Color(0xFFBDB59E))
    private val NOCHE = Color(0xFF0A2A2A)
    private val LUNA = Color(0xFFE8F4EE)

    /** El color con la luz del momento: siempre en oscuro (el tema no tiene claro); de noche, bajo el resplandor del tubo. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF050607), 0.3f)
        if (noche) r = mezcla(r, NOCHE, 0.4f)
        return r
    }

    override val corona = 0.36f
    override val base = 0.11f
    override val lado = 0.05f
    override val altoRepisa: Dp = 70.dp
    override val tabla: Dp = 13.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = Letra.terminal

    override fun ancho(palabras: Int): Dp = (40 + (palabras / 80).coerceAtMost(12)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.84f + 0.12f * azar(semilla, 4)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de la caja: plástico negro con sus nervios.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(PLASTICO_OSC), cuerpo.topLeft, cuerpo.size)
        var x = cuerpo.left + 8 * u
        while (x < cuerpo.right) { drawLine(Color.White.copy(alpha = 0.04f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 1f * u); x += 8 * u }
        // Cada fila: la sombra del tabique de arriba y los dos rieles del piso donde se paran los disquetes.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = 0.35f), rep.topLeft, Size(rep.width, 6 * u))
            drawRect(Color.Black.copy(alpha = 0.15f), rep.topLeft, Size(rep.width, 12 * u))
            drawLine(c(PLASTICO_LUZ).copy(alpha = 0.5f), Offset(rep.left, rep.bottom - 3 * u), Offset(rep.right, rep.bottom - 3 * u), 0.8f * u)
            drawLine(c(PLASTICO_LUZ).copy(alpha = 0.5f), Offset(rep.left, rep.bottom - 6 * u), Offset(rep.right, rep.bottom - 6 * u), 0.8f * u)
        }
        // De noche, el resplandor de la pantalla baja por la caja (capas planas, sin degradado).
        if (noche) {
            val alcanceLuz = minOf(cuerpo.height, 260 * u)
            for (k in 1..4) drawRect(FOSFORO.copy(alpha = 0.03f), cuerpo.topLeft, Size(cuerpo.width, alcanceLuz * k / 4f))
        }
        // Las paredes de la caja, con su brillo de plástico.
        listOf(d.izquierda to true, d.derecha to false).forEach { (lad, izq) ->
            drawRect(c(PLASTICO), lad.topLeft, lad.size)
            val bx = if (izq) lad.left + lad.width * 0.2f else lad.right - lad.width * 0.3f
            drawRect(Color.White.copy(alpha = 0.08f), Offset(bx, lad.top), Size(lad.width * 0.1f, lad.height))
            if (noche) drawRect(FOSFORO.copy(alpha = 0.06f), lad.topLeft, lad.size)
            bisel(lad, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.3f), 1.2f * u)
        }
        // Los tabiques entre filas.
        d.tablas.forEach { tb ->
            drawRect(c(PLASTICO), tb.topLeft, tb.size)
            drawRect(c(PLASTICO_LUZ), tb.topLeft, Size(tb.width, 1.6f * u))
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tb.left, tb.bottom - 2 * u), Size(tb.width, 2 * u))
            var gx = tb.left + 4 * u
            while (gx < tb.right - 4 * u) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(gx, tb.top + tb.height * 0.3f), Offset(gx, tb.top + tb.height * 0.7f), 0.6f * u); gx += 5 * u }
        }
        // La pestaña del separador de cada mes, con el texto en fósforo.
        d.placas.forEach { p ->
            val r = p.r
            drawRoundRect(c(PLASTICO_LUZ), r.topLeft, r.size, CornerRadius(2 * u))
            drawRoundRect(c(PANTALLA), r.deflate(1.4f * u).topLeft, r.deflate(1.4f * u).size, CornerRadius(1.2f * u))
            drawRect(c(FOSFORO_TENUE), Offset(r.left + 4 * u, r.center.y - 2 * u), Size(4 * u, 4 * u))
            textoEn(medidor, p.texto, Rect(r.left + 10 * u, r.top + 1 * u, r.right - 4 * u, r.bottom - 1 * u), letra, FOSFORO, 10f, espaciado = 0.6f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el frente de la caja con su agarradera y la mesa, con el cable del ratón y un bolígrafo.
        val b = d.base
        val frente = Rect(b.left, b.top, b.right, b.top + b.height * 0.38f)
        val piso = frente.bottom
        drawRect(c(MESA), Offset(0f, piso), Size(w, h - piso))
        vetas(Rect(0f, piso, w, h), c(MESA_VETA).copy(alpha = 0.6f), 91, densidad = 0.8f, grosor = 0.6f)
        if (noche) drawRect(FOSFORO.copy(alpha = 0.06f), Offset(0f, piso), Size(w, h - piso))
        val cable = Path().apply {
            moveTo(w * 0.62f, h)
            cubicTo(w * 0.66f, piso + (h - piso) * 0.3f, w * 0.86f, piso + (h - piso) * 0.9f, w * 0.94f, piso + (h - piso) * 0.4f)
            cubicTo(w * 0.98f, piso + (h - piso) * 0.15f, w, piso + (h - piso) * 0.3f, w, piso + (h - piso) * 0.25f)
        }
        drawPath(cable, c(CABLE), style = Stroke(1.6f * u))
        rotate(-6f, Offset(w * 0.25f, piso + (h - piso) * 0.55f)) {
            drawRoundRect(c(DISCOS[1]), Offset(w * 0.12f, piso + (h - piso) * 0.5f), Size(w * 0.26f, 3.2f * u), CornerRadius(1.6f * u))
            drawRect(c(METAL), Offset(w * 0.36f, piso + (h - piso) * 0.5f), Size(w * 0.03f, 3.2f * u))
        }
        drawRect(Color.Black.copy(alpha = 0.4f), Offset(frente.left, piso), Size(frente.width, 3 * u))
        drawRect(c(PLASTICO), frente.topLeft, frente.size)
        drawRect(c(PLASTICO_LUZ), frente.topLeft, Size(frente.width, 1.4f * u))
        val agarre = Rect(frente.center.x - frente.width * 0.12f, frente.top + frente.height * 0.3f, frente.center.x + frente.width * 0.12f, frente.bottom - frente.height * 0.25f)
        drawRoundRect(c(PLASTICO_OSC), agarre.topLeft, agarre.size, CornerRadius(agarre.height / 2))
        if (noche) drawRect(FOSFORO.copy(alpha = 0.05f), frente.topLeft, frente.size)
    }

    /** El borde del monitor con su pantalla, el cursor y el piloto; la tapa ahumada abierta y el borde de la caja. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(FONDO), r.topLeft, r.size)
        // La carcasa del monitor: solo se ve su parte de abajo, con las esquinas redondeadas.
        val monitor = Rect(r.left + r.width * 0.05f, r.top - r.height * 0.2f, r.right - r.width * 0.05f, r.top + r.height * 0.56f)
        drawRoundRect(c(CARCASA), monitor.topLeft, monitor.size, CornerRadius(10 * u))
        drawRect(c(CARCASA_SOMBRA), Offset(monitor.left + 6 * u, monitor.bottom - 3 * u), Size(monitor.width - 12 * u, 3 * u))
        // La pantalla de fósforo, abombada, con sus líneas de barrido.
        val pantalla = Rect(monitor.left + monitor.width * 0.08f, r.top, monitor.right - monitor.width * 0.08f, monitor.bottom - monitor.height * 0.3f)
        drawRoundRect(c(CARCASA_SOMBRA), pantalla.inflate(3 * u).topLeft, pantalla.inflate(3 * u).size, CornerRadius(12 * u))
        drawRoundRect(PANTALLA, pantalla.topLeft, pantalla.size, CornerRadius(10 * u))
        val brillo = if (noche) 1f else 0.75f
        clipRect(pantalla.left, pantalla.top, pantalla.right, pantalla.bottom) {
            if (noche) mancha(pantalla.center, pantalla.width * 0.55f, FOSFORO.copy(alpha = 0.22f))
            var ly = pantalla.top + 1 * u
            while (ly < pantalla.bottom) { drawLine(FOSFORO.copy(alpha = 0.06f), Offset(pantalla.left, ly), Offset(pantalla.right, ly), 0.8f * u); ly += 2.6f * u }
            // Renglones de salida (bloques, no letras) y el prompt con su cursor.
            val alto0 = pantalla.height * 0.13f
            val x0 = pantalla.left + pantalla.width * 0.06f
            listOf(0.55f, 0.32f, 0.7f).forEachIndexed { k, largo ->
                val yy = pantalla.top + pantalla.height * (0.12f + 0.2f * k)
                drawRect(FOSFORO_TENUE.copy(alpha = brillo), Offset(x0, yy), Size(pantalla.width * largo * 0.8f, alto0 * 0.55f))
            }
            val yp = pantalla.top + pantalla.height * 0.74f
            val prompt = Path().apply { moveTo(x0, yp); lineTo(x0 + alto0 * 0.55f, yp + alto0 * 0.4f); lineTo(x0, yp + alto0 * 0.8f) }
            drawPath(prompt, FOSFORO.copy(alpha = brillo), style = Stroke(1.6f * u))
            drawRect(FOSFORO.copy(alpha = brillo * 0.7f), Offset(x0 + alto0 * 1.1f, yp + alto0 * 0.2f), Size(pantalla.width * 0.2f, alto0 * 0.5f))
            drawRect(FOSFORO.copy(alpha = brillo), Offset(x0 + alto0 * 1.3f + pantalla.width * 0.2f, yp), Size(alto0 * 0.5f, alto0 * 0.8f))
            drawLine(Color.White.copy(alpha = 0.07f), Offset(pantalla.left + pantalla.width * 0.1f, pantalla.bottom), Offset(pantalla.left + pantalla.width * 0.35f, pantalla.top), 10 * u)
        }
        // La barbilla del monitor: las rejillas, una ranura y el piloto encendido.
        val barbilla = monitor.bottom - monitor.height * 0.15f
        for (k in 0 until 6) drawLine(c(CARCASA_SOMBRA), Offset(monitor.left + monitor.width * (0.08f + 0.025f * k), barbilla - 3 * u), Offset(monitor.left + monitor.width * (0.08f + 0.025f * k), barbilla + 3 * u), 1f * u)
        drawRoundRect(c(CARCASA_SOMBRA), Offset(monitor.center.x - monitor.width * 0.08f, barbilla - 1.5f * u), Size(monitor.width * 0.16f, 3 * u), CornerRadius(1.5f * u))
        val led = Offset(monitor.right - monitor.width * 0.1f, barbilla)
        drawCircle(PILOTO.copy(alpha = 0.3f), 3.6f * u, led)
        drawCircle(PILOTO, 1.8f * u, led)
        // La caja: su borde trasero con las bisagras y la tapa ahumada abierta hacia atrás, por la que se ve el monitor.
        val borde = r.bottom - r.height * 0.1f
        val izq = r.left + r.width * lado * 0.5f; val der = r.right - r.width * lado * 0.5f
        val tapa = Path().apply {
            moveTo(izq + 4 * u, borde); lineTo(der - 4 * u, borde)
            lineTo(der - r.width * 0.06f, r.top + r.height * 0.36f); lineTo(izq + r.width * 0.06f, r.top + r.height * 0.36f); close()
        }
        drawPath(tapa, c(AHUMADO).copy(alpha = 0.5f))
        drawPath(tapa, c(PLASTICO_LUZ).copy(alpha = 0.8f), style = Stroke(1.2f * u))
        clipPath(tapa) {
            drawLine(Color.White.copy(alpha = 0.1f), Offset(r.left + r.width * 0.2f, borde), Offset(r.left + r.width * 0.32f, r.top + r.height * 0.36f), 8 * u)
            drawLine(Color.White.copy(alpha = 0.06f), Offset(r.left + r.width * 0.3f, borde), Offset(r.left + r.width * 0.4f, r.top + r.height * 0.36f), 3 * u)
            if (noche) drawRect(FOSFORO.copy(alpha = 0.08f), r.topLeft, r.size)
        }
        listOf(0.18f, 0.82f).forEach { f ->
            drawRoundRect(c(PLASTICO_LUZ), Offset(r.left + r.width * f - 8 * u, borde - 3 * u), Size(16 * u, 6 * u), CornerRadius(3 * u))
        }
        drawRect(c(PLASTICO), Offset(izq, borde), Size(der - izq, r.bottom - borde))
        drawRect(c(PLASTICO_LUZ), Offset(izq, borde), Size(der - izq, 1.4f * u))
        if (noche) drawRect(FOSFORO.copy(alpha = 0.1f), Offset(izq, borde), Size(der - izq, r.bottom - borde))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = DISCOS[(v.semilla and 0x7fffffff) % DISCOS.size]
        val cuerpo = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // El cuerpo de plástico, con la esquina cortada arriba a la derecha.
        val ch = w * 0.08f
        val forma = Path().apply { moveTo(0f, 0f); lineTo(w - ch, 0f); lineTo(w, ch); lineTo(w, h); lineTo(0f, h); close() }
        drawPath(forma, cuerpo)
        drawRect(Color.White.copy(alpha = 0.06f), Offset.Zero, Size(w, h * 0.04f))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h * 0.97f), Size(w, h * 0.03f))
        drawPath(forma, Color.White.copy(alpha = 0.14f), style = Stroke(0.8f * u))
        // El hueco del obturador y el obturador de metal con su ventanilla.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.16f, 0f), Size(w * 0.66f, h * 0.38f))
        val obt = Rect(w * 0.22f, 0f, w * 0.68f, h * 0.36f)
        drawRect(c(METAL), obt.topLeft, obt.size)
        drawRect(Color.White.copy(alpha = 0.3f), obt.topLeft, Size(obt.width, 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(obt.left, obt.bottom - 1.2f * u), Size(obt.width, 1.2f * u))
        val ventanilla = Rect(obt.left + obt.width * 0.55f, obt.top + obt.height * 0.2f, obt.left + obt.width * 0.8f, obt.bottom - obt.height * 0.14f)
        drawRect(Color(0xFF3A2E22), ventanilla.topLeft, ventanilla.size)
        drawRect(Color.Black.copy(alpha = 0.35f), ventanilla.topLeft, ventanilla.size, style = Stroke(0.6f * u))
        // Arriba a la izquierda, la flecha de inserción en relieve; de noche, una luna.
        val cf = Offset(w * 0.1f, h * 0.16f)
        val sf = minOf(w * 0.08f, 4 * u)
        if (v.noche) {
            drawCircle(LUNA, sf, cf)
            drawCircle(cuerpo, sf * 0.85f, Offset(cf.x + sf * 0.45f, cf.y - sf * 0.25f))
        } else {
            val flecha = Path().apply { moveTo(cf.x - sf, cf.y - sf * 0.6f); lineTo(cf.x + sf, cf.y - sf * 0.6f); lineTo(cf.x, cf.y + sf * 0.8f); close() }
            drawPath(flecha, Color.White.copy(alpha = 0.22f))
        }
        // La etiqueta escrita a mano: el día, el mes y renglones, más cuanto más escribiste.
        val et = Rect(w * 0.12f, h * 0.44f, w * 0.88f, h * 0.92f)
        drawRect(c(ETIQUETA), et.topLeft, et.size)
        drawRect(c(FOSFORO_TENUE), et.topLeft, Size(et.width, et.height * 0.08f))
        textoEn(medidor, v.dia, Rect(et.left, et.top + et.height * 0.1f, et.right, et.top + et.height * 0.5f), letra, c(BOLIGRAFO), 14f)
        textoEn(medidor, v.mes, Rect(et.left + 1 * u, et.top + et.height * 0.5f, et.right - 1 * u, et.top + et.height * 0.72f), letra, c(BOLIGRAFO).copy(alpha = 0.85f), 7f, minimo = 4f)
        val renglones = 1 + (v.lleno * 2).toInt()
        for (j in 0 until renglones) {
            val yy = et.top + et.height * (0.78f + 0.08f * j)
            if (yy < et.bottom - 1 * u) drawLine(c(BOLIGRAFO).copy(alpha = 0.45f), Offset(et.left + et.width * 0.1f, yy), Offset(et.left + et.width * (0.5f + 0.35f * azar(v.semilla, 50 + j)), yy), 0.7f * u)
        }
        // Las ventanas de protección contra escritura, abajo en las esquinas.
        val lv = minOf(w * 0.07f, 3.5f * u)
        drawRect(Color.Black.copy(alpha = 0.7f), Offset(w * 0.025f, h * 0.88f), Size(lv, lv))
        drawRect(if (azar(v.semilla, 8) > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.18f), Offset(w * 0.975f - lv, h * 0.88f), Size(lv, lv))
    }
}

/**
 * Vapor: el estante de hierro colado de la sala de máquinas, contra un muro de ladrillo tiznado. Los costados son montantes
 * calados con sus ojos redondos y sus pernos; las tablas, chapa remachada con un fleje de latón, y detrás de cada repisa
 * corre una tubería de cobre con sus bridas. Arriba, la cornisa de hierro con sus dentellones, el tubo de vapor que la
 * cruza, dos engranes que engranan, el manómetro al centro y la válvula de volante rojo que suelta vapor; a la izquierda, la
 * lámpara de gas en su brazo. Abajo, el zócalo de hierro con sus arcos calados y un piso de tablones. Los meses van en
 * placas de latón remachadas. Cada entrada es un libro mayor de lomo de cuero con esquinas de latón, la placa remachada con
 * el día, el mes dorado a lo largo, un engrane estampado y nervios que se suman cuanto más escribiste; de noche, una luna en
 * lugar del engrane. De noche la lámpara de gas se enciende. Distinto del Ideario, que es el gabinete de patentes de caoba.
 */
object EstanteVapor : DisenoBiblioteca {
    private val HIERRO = Color(0xFF2E3230)
    private val HIERRO_LUZ = Color(0xFF59605C)
    private val HIERRO_OSC = Color(0xFF181A19)
    private val LATON = Color(0xFFC9A04E)
    private val LATON_LUZ = Color(0xFFE6C77E)
    private val LATON_OSC = Color(0xFF8A6A2C)
    private val COBRE = Color(0xFFB06A3B)
    private val COBRE_LUZ = Color(0xFFD99464)
    private val LADRILLO = Color(0xFF7A3E2C)
    private val LADRILLO_JUNTA = Color(0xFF4C2A20)
    private val CARA = Color(0xFFEDE3C8)
    private val ROJO = Color(0xFF9A2A22)
    private val TABLON = Color(0xFF5E4630)
    private val TABLON_VETA = Color(0xFF3A2A1C)
    private val VAPOR = Color(0xFFEDEDE8)
    /** Los cueros de los libros mayores: sangre de buey, verde botella, castaño, azul marino y ciruela. */
    private val CUEROS = listOf(Color(0xFF5C1F1C), Color(0xFF2F4A35), Color(0xFF5A3A22), Color(0xFF27334F), Color(0xFF4A2A44))
    private val NOCHE = Color(0xFF1E1610)
    private val GAS = Color(0xFFFFC870)
    private val LUNA = Color(0xFFF2E6C2)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la penumbra tibia de la lámpara de gas. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0D0B09), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.48f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.1f
    override val lado = 0.065f
    override val altoRepisa: Dp = 116.dp
    override val tabla: Dp = 17.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.caldera

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 40).coerceAtMost(22)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.82f + 0.14f * azar(semilla, 5)

    /** Un muro de ladrillo en hiladas a soga, con sus juntas. */
    private fun DrawScope.ladrillos(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawRect(t(LADRILLO, oscuro, noche), r.topLeft, r.size)
        val fila = 9 * u; val pieza = 26 * u
        var y = r.top; var k = 0
        while (y < r.bottom) {
            drawLine(t(LADRILLO_JUNTA, oscuro, noche), Offset(r.left, y), Offset(r.right, y), 1.2f * u)
            var x = r.left + if (k % 2 == 0) 0f else pieza / 2
            while (x < r.right) { drawLine(t(LADRILLO_JUNTA, oscuro, noche), Offset(x, y), Offset(x, minOf(y + fila, r.bottom)), 1.2f * u); x += pieza }
            y += fila; k++
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El muro de ladrillo, tiznado de hollín.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        ladrillos(cuerpo, oscuro, noche)
        for (k in 0 until 5) {
            val o = Offset(cuerpo.left + cuerpo.width * azar(12, k), cuerpo.top + cuerpo.height * azar(13, k))
            drawOval(Color.Black.copy(alpha = 0.14f), Offset(o.x - 26 * u, o.y - 16 * u), Size(52 * u, 32 * u))
        }
        // Cada repisa: la tubería de cobre que corre por detrás, con sus bridas, y la sombra de la chapa de arriba.
        d.repisas.forEachIndexed { k, rep ->
            val ty = rep.top + 16 * u
            drawRect(c(COBRE), Offset(rep.left, ty - 3 * u), Size(rep.width, 6 * u))
            drawRect(c(COBRE_LUZ), Offset(rep.left, ty - 2.2f * u), Size(rep.width, 1.2f * u))
            var bx = rep.left + 30 * u + 20 * u * azar(k, 3)
            while (bx < rep.right - 10 * u) {
                drawRect(c(LATON_OSC), Offset(bx - 2.5f * u, ty - 5 * u), Size(5 * u, 10 * u))
                drawCircle(c(LATON_LUZ), 0.9f * u, Offset(bx, ty - 3.4f * u)); drawCircle(c(LATON_LUZ), 0.9f * u, Offset(bx, ty + 3.4f * u))
                bx += 90 * u
            }
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.3f), rep.topLeft, Size(rep.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.18f), rep.topLeft, Size(7 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.18f), Offset(rep.right - 7 * u, rep.top), Size(7 * u, rep.height))
        }
        // Los montantes de hierro colado, calados con ojos redondos y sujetos con pernos.
        listOf(d.izquierda, d.derecha).forEach { lad ->
            drawRect(c(HIERRO), lad.topLeft, lad.size)
            drawRect(c(HIERRO_LUZ), Offset(lad.left + lad.width * 0.12f, lad.top), Size(lad.width * 0.08f, lad.height))
            drawRect(c(HIERRO_OSC), Offset(lad.right - lad.width * 0.14f, lad.top), Size(lad.width * 0.14f, lad.height))
            val cx = lad.center.x
            var oy = lad.top + 20 * u
            while (oy < lad.bottom - 14 * u) {
                val ro = lad.width * 0.26f
                drawCircle(c(LADRILLO_JUNTA), ro, Offset(cx, oy))
                drawCircle(c(HIERRO_LUZ), ro, Offset(cx, oy), style = Stroke(1.2f * u))
                drawCircle(c(HIERRO_OSC), ro * 0.3f, Offset(cx, oy + ro + 6 * u))
                drawCircle(c(LATON), ro * 0.16f, Offset(cx, oy + ro + 6 * u))
                oy += 44 * u
            }
            bisel(lad, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.3f), 1.4f * u)
        }
        // Las tablas: chapa con su fleje de latón y una fila de remaches.
        d.tablas.forEach { tb ->
            drawRect(c(HIERRO), tb.topLeft, tb.size)
            drawRect(c(LATON), tb.topLeft, Size(tb.width, 2.2f * u))
            drawRect(c(HIERRO_OSC), Offset(tb.left, tb.bottom - tb.height * 0.22f), Size(tb.width, tb.height * 0.22f))
            var rx = tb.left + 6 * u
            while (rx < tb.right - 4 * u) {
                drawCircle(c(HIERRO_LUZ), 1.3f * u, Offset(rx, tb.top + tb.height * 0.5f))
                drawCircle(Color.Black.copy(alpha = 0.4f), 1.3f * u, Offset(rx + 0.5f * u, tb.top + tb.height * 0.5f + 0.5f * u), style = Stroke(0.5f * u))
                rx += 12 * u
            }
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(tb.left, tb.bottom), Size(tb.width, 4 * u))
        }
        // La placa de latón de cada mes, con un remache en cada esquina.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1.2f * u, r.top + 1.2f * u), r.size)
            drawRect(c(LATON), r.topLeft, r.size)
            bisel(r, c(LATON_LUZ), c(LATON_OSC), 1.2f * u)
            listOf(Offset(r.left + 3 * u, r.top + 3 * u), Offset(r.right - 3 * u, r.top + 3 * u), Offset(r.left + 3 * u, r.bottom - 3 * u), Offset(r.right - 3 * u, r.bottom - 3 * u)).forEach { o ->
                drawCircle(c(LATON_OSC), 1.3f * u, o); drawCircle(c(LATON_LUZ), 0.5f * u, Offset(o.x - 0.4f * u, o.y - 0.4f * u))
            }
            textoEn(medidor, p.texto, Rect(r.left + 7 * u, r.top + 1 * u, r.right - 7 * u, r.bottom - 1 * u), letra, c(HIERRO_OSC), 10f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de hierro calado en arcos, con sus pernos, sobre un piso de tablones.
        val b = d.base
        val piso = b.top + b.height * 0.66f
        drawRect(c(TABLON), Offset(0f, piso), Size(w, h - piso))
        vetas(Rect(0f, piso, w, h), c(TABLON_VETA).copy(alpha = 0.6f), 33, densidad = 0.8f, grosor = 0.6f)
        var tx = 0f
        while (tx < w) { drawLine(Color.Black.copy(alpha = 0.3f), Offset(tx, piso), Offset(tx, h), 0.8f * u); tx += 64 * u }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(HIERRO), zocalo.topLeft, zocalo.size)
        drawRect(c(LATON), zocalo.topLeft, Size(zocalo.width, 2 * u))
        val n = 7
        val paso = zocalo.width * 0.84f / n
        for (k in 0 until n) {
            val ar = Rect(zocalo.left + zocalo.width * 0.08f + paso * k + paso * 0.15f, zocalo.top + zocalo.height * 0.3f, zocalo.left + zocalo.width * 0.08f + paso * (k + 1) - paso * 0.15f, zocalo.bottom)
            val calado = Path().apply {
                moveTo(ar.left, ar.bottom); lineTo(ar.left, ar.top + ar.width / 2)
                arcTo(Rect(ar.left, ar.top, ar.right, ar.top + ar.width), 180f, 180f, false)
                lineTo(ar.right, ar.bottom); close()
            }
            drawPath(calado, c(HIERRO_OSC))
            drawPath(calado, c(HIERRO_LUZ), style = Stroke(0.8f * u))
        }
        listOf(zocalo.left + 6 * u, zocalo.right - 6 * u).forEach { x -> drawCircle(c(LATON), 2 * u, Offset(x, zocalo.top + zocalo.height * 0.5f)) }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** La cornisa, el tubo de vapor, los engranes, el manómetro, la válvula y la lámpara de gas. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        ladrillos(r, oscuro, noche)
        // La cornisa de hierro con sus molduras y dentellones.
        val cornisa = Rect(r.left, r.top + r.height * 0.04f, r.right, r.top + r.height * 0.18f)
        drawRect(c(HIERRO), cornisa.topLeft, cornisa.size)
        drawRect(c(HIERRO_LUZ), cornisa.topLeft, Size(cornisa.width, 1.6f * u))
        drawLine(c(LATON), Offset(cornisa.left, cornisa.top + cornisa.height * 0.4f), Offset(cornisa.right, cornisa.top + cornisa.height * 0.4f), 1f * u)
        var dx = cornisa.left + 2 * u
        while (dx < cornisa.right) { drawRect(c(HIERRO_OSC), Offset(dx, cornisa.bottom), Size(5 * u, 4 * u)); dx += 9 * u }
        // La tabla de arriba, de chapa remachada.
        val tope = r.bottom - r.height * 0.09f
        drawRect(c(HIERRO), Offset(r.left, tope), Size(r.width, r.bottom - tope))
        drawRect(c(LATON), Offset(r.left, tope), Size(r.width, 1.8f * u))
        // El tubo de vapor de cobre, con sus bridas.
        val ty = r.top + r.height * 0.32f
        val grueso = r.height * 0.075f
        drawRect(c(COBRE), Offset(r.left, ty - grueso / 2), Size(r.width, grueso))
        drawRect(c(COBRE_LUZ), Offset(r.left, ty - grueso * 0.32f), Size(r.width, grueso * 0.2f))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, ty + grueso * 0.25f), Size(r.width, grueso * 0.25f))
        listOf(0.14f, 0.38f, 0.62f, 0.9f).forEach { f ->
            val bx = r.left + r.width * f
            drawRect(c(LATON_OSC), Offset(bx - 3 * u, ty - grueso * 0.85f), Size(6 * u, grueso * 1.7f))
            drawRect(c(LATON_LUZ), Offset(bx - 3 * u, ty - grueso * 0.85f), Size(1.4f * u, grueso * 1.7f))
        }
        // Dos engranes que engranan, con su cubo y sus rayos.
        val g1 = Offset(r.left + r.width * 0.26f, r.top + r.height * 0.6f)
        val r1 = r.height * 0.22f
        val g2 = Offset(g1.x + r1 * 1.6f, r.top + r.height * 0.5f)
        val r2 = r.height * 0.14f
        listOf(Triple(g1, r1, LATON), Triple(g2, r2, COBRE)).forEachIndexed { k, (cg, rg, col) ->
            drawPath(engrane(cg, rg, if (k == 0) 11 else 7), c(col))
            drawPath(engrane(cg, rg, if (k == 0) 11 else 7), Color.Black.copy(alpha = 0.35f), style = Stroke(0.8f * u))
            drawCircle(c(HIERRO_OSC), rg * 0.62f, cg)
            for (j in 0 until 4) {
                val a = (Math.PI / 4 + j * Math.PI / 2).toFloat()
                drawLine(c(col), cg, Offset(cg.x + rg * 0.64f * cos(a), cg.y + rg * 0.64f * sin(a)), rg * 0.14f)
            }
            drawCircle(c(col), rg * 0.2f, cg)
            drawCircle(c(HIERRO_OSC), rg * 0.07f, cg)
        }
        // El manómetro al centro, colgado del tubo.
        val cm = Offset(r.center.x + r.width * 0.04f, r.top + r.height * 0.6f)
        val rm = r.height * 0.21f
        drawRect(c(COBRE), Offset(cm.x - 2 * u, ty), Size(4 * u, cm.y - rm - ty))
        manometro(cm, rm, c(LATON), c(CARA), c(ROJO), if (noche) 0.4f else 0.66f)
        drawCircle(c(LATON_OSC), rm, cm, style = Stroke(0.8f * u))
        // La válvula de vapor con su volante rojo, que suelta su nube.
        val cv = Offset(r.right - r.width * 0.2f, r.top + r.height * 0.62f)
        val rv = r.height * 0.13f
        drawRect(c(COBRE), Offset(cv.x - grueso * 0.35f, ty), Size(grueso * 0.7f, cv.y - ty))
        drawRect(c(LATON_OSC), Offset(cv.x - rv * 0.55f, cv.y - rv * 0.35f), Size(rv * 1.1f, rv * 0.7f))
        drawCircle(c(ROJO), rv, Offset(cv.x, cv.y - rv * 0.9f), style = Stroke(2.2f * u))
        for (j in 0 until 3) {
            val a = (j * Math.PI / 3).toFloat()
            val cc = Offset(cv.x, cv.y - rv * 0.9f)
            drawLine(c(ROJO), Offset(cc.x - rv * cos(a), cc.y - rv * sin(a)), Offset(cc.x + rv * cos(a), cc.y + rv * sin(a)), 1.4f * u)
        }
        drawCircle(c(LATON), rv * 0.2f, Offset(cv.x, cv.y - rv * 0.9f))
        val salida = Offset(cv.x + rv * 0.55f, cv.y)
        drawRect(c(LATON_OSC), Offset(salida.x, salida.y - 2 * u), Size(5 * u, 4 * u))
        listOf(Triple(0.6f, 0.45f, 0.5f), Triple(1.3f, 0.7f, 0.32f), Triple(2.1f, 1f, 0.18f)).forEach { (dxv, tam, al) ->
            val o = Offset(salida.x + 5 * u + rv * dxv, salida.y - rv * dxv * 0.8f)
            drawOval(VAPOR.copy(alpha = al), Offset(o.x - rv * tam, o.y - rv * tam * 0.6f), Size(rv * tam * 2, rv * tam * 1.2f))
        }
        // La lámpara de gas en su brazo, a la izquierda: de día apagada, de noche con su llama.
        val pared = Offset(r.left + r.width * 0.035f, r.top + r.height * 0.42f)
        val globo = Offset(r.left + r.width * 0.085f, r.top + r.height * 0.56f)
        val rgl = r.height * 0.1f
        val brazo = Path().apply { moveTo(pared.x, pared.y); quadraticTo(globo.x, pared.y - rgl * 0.5f, globo.x, globo.y + rgl * 1.1f) }
        drawPath(brazo, c(LATON_OSC), style = Stroke(1.8f * u))
        drawRect(c(LATON), Offset(pared.x - 3 * u, pared.y - 5 * u), Size(4 * u, 10 * u))
        if (noche) mancha(globo, r.height * 0.5f, GAS.copy(alpha = 0.45f))
        drawOval(if (noche) GAS.copy(alpha = 0.55f) else c(CARA).copy(alpha = 0.45f), Offset(globo.x - rgl * 0.75f, globo.y - rgl), Size(rgl * 1.5f, rgl * 2f))
        if (noche) {
            val llama = Path().apply {
                moveTo(globo.x, globo.y - rgl * 0.55f)
                quadraticTo(globo.x + rgl * 0.32f, globo.y + rgl * 0.1f, globo.x, globo.y + rgl * 0.4f)
                quadraticTo(globo.x - rgl * 0.32f, globo.y + rgl * 0.1f, globo.x, globo.y - rgl * 0.55f); close()
            }
            drawPath(llama, Color(0xFFFFF2C8))
        }
        drawOval(c(LATON_OSC), Offset(globo.x - rgl * 0.75f, globo.y - rgl), Size(rgl * 1.5f, rgl * 2f), style = Stroke(0.8f * u))
        drawRect(c(LATON), Offset(globo.x - rgl * 0.45f, globo.y + rgl * 0.85f), Size(rgl * 0.9f, rgl * 0.3f))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = CUEROS[(v.semilla and 0x7fffffff) % CUEROS.size]
        val cuero = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        // El lomo de cuero, redondeado: sombra en los cantos y una banda de luz.
        drawRoundRect(cuero, Offset.Zero, size, CornerRadius(2 * u))
        drawRect(Color.Black.copy(alpha = 0.28f), Offset.Zero, Size(w * 0.12f, h))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(w * 0.88f, 0f), Size(w * 0.12f, h))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.3f, 0f), Size(w * 0.14f, h))
        // Las esquinas de latón, cada una con su remache.
        val k = minOf(w * 0.36f, h * 0.08f)
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, s) ->
            val esquina = Path().apply { moveTo(o.x, o.y); lineTo(o.x + s.x * k, o.y); lineTo(o.x, o.y + s.y * k); close() }
            drawPath(esquina, c(LATON))
            drawPath(esquina, c(LATON_OSC), style = Stroke(0.6f * u))
            drawCircle(c(LATON_OSC), 0.8f * u, Offset(o.x + s.x * k * 0.3f, o.y + s.y * k * 0.3f))
        }
        // La placa remachada con el día.
        val placa = Rect(w * 0.12f, h * 0.1f, w * 0.88f, h * 0.28f)
        drawRect(c(LATON), placa.topLeft, placa.size)
        drawRect(c(LATON_LUZ), placa.topLeft, Size(placa.width, 1 * u))
        drawRect(c(LATON_OSC), placa.topLeft, placa.size, style = Stroke(0.7f * u))
        val rr = minOf(1.1f * u, placa.width * 0.06f)
        listOf(placa.left + rr * 2, placa.right - rr * 2).forEach { x ->
            drawCircle(c(LATON_OSC), rr, Offset(x, placa.top + rr * 2)); drawCircle(c(LATON_OSC), rr, Offset(x, placa.bottom - rr * 2))
        }
        textoEn(medidor, v.dia, Rect(placa.left + rr * 3.5f, placa.top + 1 * u, placa.right - rr * 3.5f, placa.bottom - 1 * u), letra, c(HIERRO_OSC), 13f, peso = FontWeight.Bold)
        // Un engrane estampado en dorado; de noche, una luna.
        val ce = Offset(w * 0.5f, h * 0.33f)
        val re = minOf(w * 0.2f, 5 * u)
        if (v.noche) {
            drawCircle(LUNA, re, ce)
            drawCircle(cuero, re * 0.85f, Offset(ce.x + re * 0.45f, ce.y - re * 0.25f))
        } else {
            drawPath(engrane(ce, re, 6), c(LATON_LUZ).copy(alpha = 0.8f))
            drawCircle(cuero, re * 0.35f, ce)
        }
        // El mes, dorado a lo largo del lomo.
        textoEn(medidor, v.mes, Rect(w * 0.16f, h * 0.39f, w * 0.84f, h * 0.72f), letra, c(LATON_LUZ), 8.5f, vertical = true, minimo = 4.5f)
        // Los nervios: más cuanto más escribiste.
        val nervios = 1 + (v.lleno * 2.5f).toInt()
        for (j in 0 until nervios) {
            val ny = h * (0.76f + 0.05f * j)
            drawRect(Color.Black.copy(alpha = 0.32f), Offset(0f, ny + 1.6f * u), Size(w, 1.2f * u))
            drawRect(Color.White.copy(alpha = 0.14f), Offset(0f, ny), Size(w, 1.6f * u))
            drawLine(c(LATON).copy(alpha = 0.7f), Offset(w * 0.14f, ny - 1 * u), Offset(w * 0.86f, ny - 1 * u), 0.5f * u)
        }
    }
}

/**
 * Napoleón: la biblioteca de campaña. Un baúl forrado de cuero puesto de pie y abierto, con cantoneras de latón, filas de
 * clavos dorados y sus estantes de caoba por dentro, forrados de paño rojo; delante de la lona de la tienda, a rayas azules
 * y crudas, con su lambrequín festoneado y el mástil. Encima del baúl, la tapa con su asa de latón, un mapa doblado y un
 * catalejo; de noche, el farol de campaña que cuelga del mástil se enciende. Abajo, los patines del baúl sobre una alfombra
 * de campaña. Los meses van en marcos de latón con su tarjeta. Cada entrada es un librito de tafilete con filetes dorados,
 * el tejuelo rojo con el día, el mes dorado a lo largo, la N en su corona de laurel y abejas que se suman cuanto más
 * escribiste; de noche, una luna en lugar de la primera abeja. Distinto del Ideario, que es el secrétaire de caoba.
 */
object EstanteNapoleon : DisenoBiblioteca {
    private val LONA = Color(0xFFE8DFC9)
    private val LONA_RAYA = Color(0xFF3D5A8A)
    private val CUERO = Color(0xFF5B3A24)
    private val CUERO_LUZ = Color(0xFF7A5234)
    private val CUERO_OSC = Color(0xFF3A2416)
    private val CAOBA = Color(0xFF6E2F1E)
    private val CAOBA_LUZ = Color(0xFF8D4430)
    private val CAOBA_VETA = Color(0xFF4A1E12)
    private val PANO = Color(0xFF7D2A2A)
    private val LATON = Color(0xFFC7A04F)
    private val LATON_LUZ = Color(0xFFE5C988)
    private val LATON_OSC = Color(0xFF8C6C2C)
    private val ORO = Color(0xFFD9B54A)
    private val TEJUELO = Color(0xFFA4282A)
    private val PERGAMINO = Color(0xFFEDE2C4)
    private val TINTA = Color(0xFF2A1C14)
    private val MASTIL = Color(0xFF6A5236)
    private val TIERRA = Color(0xFF8A7556)
    private val ALFOMBRA = Color(0xFF7A2E25)
    private val ALFOMBRA_ORLA = Color(0xFF2E3B5E)
    /** Los tafiletes: verde, rojo, azul, castaño y negro. */
    private val TAFILETES = listOf(Color(0xFF1F4A33), Color(0xFF6B1F24), Color(0xFF1E2D52), Color(0xFF4A2A1A), Color(0xFF26231F))
    private val NOCHE = Color(0xFF1A1610)
    private val FAROL = Color(0xFFFFD27A)
    private val LUNA = Color(0xFFF2E8C8)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la penumbra de la tienda. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0E0B08), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.46f)
        return r
    }

    override val corona = 0.3f
    override val base = 0.11f
    override val lado = 0.08f
    override val altoRepisa: Dp = 92.dp
    override val tabla: Dp = 14.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.bodoniTitulo

    override fun ancho(palabras: Int): Dp = (17 + (palabras / 50).coerceAtMost(16)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.72f + 0.2f * azar(semilla, 6)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo del baúl: paño rojo con su trama.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(PANO), cuerpo.topLeft, cuerpo.size)
        var k0 = -cuerpo.height
        while (k0 < cuerpo.width) {
            drawLine(Color.Black.copy(alpha = 0.06f), Offset(cuerpo.left + k0, cuerpo.bottom), Offset(cuerpo.left + k0 + cuerpo.height, cuerpo.top), 0.6f * u)
            k0 += 4 * u
        }
        // Cada repisa: la sombra del estante de arriba y de las paredes del baúl.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.42f else 0.3f), rep.topLeft, Size(rep.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.14f), rep.topLeft, Size(rep.width, 14 * u))
            drawRect(Color.Black.copy(alpha = 0.2f), rep.topLeft, Size(8 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(rep.right - 8 * u, rep.top), Size(8 * u, rep.height))
        }
        // Las paredes del baúl: cuero con su canto de caoba, filas de clavos dorados y una correa con hebilla.
        listOf(d.izquierda to true, d.derecha to false).forEach { (lad, izq) ->
            drawRect(c(CUERO), lad.topLeft, lad.size)
            val canto = lad.width * 0.22f
            val kx = if (izq) lad.right - canto else lad.left
            drawRect(c(CAOBA), Offset(kx, lad.top), Size(canto, lad.height))
            drawLine(c(CAOBA_LUZ), Offset(kx + canto * 0.3f, lad.top), Offset(kx + canto * 0.3f, lad.bottom), 0.8f * u)
            listOf(lad.left + lad.width * 0.14f, lad.left + lad.width * (if (izq) 0.66f else 0.86f)).forEach { cx ->
                var cy = lad.top + 5 * u
                while (cy < lad.bottom - 3 * u) { drawCircle(c(LATON), 1.3f * u, Offset(cx, cy)); drawCircle(c(LATON_LUZ), 0.5f * u, Offset(cx - 0.4f * u, cy - 0.4f * u)); cy += 8 * u }
            }
            val cx0 = lad.left + lad.width * (if (izq) 0.4f else 0.5f)
            drawRect(c(CUERO_OSC), Offset(cx0 - lad.width * 0.12f, lad.top), Size(lad.width * 0.24f, lad.height))
            var hy = lad.top + 60 * u
            while (hy < lad.bottom - 20 * u) {
                drawRect(c(LATON), Offset(cx0 - lad.width * 0.16f, hy), Size(lad.width * 0.32f, 9 * u), style = Stroke(1.2f * u))
                drawLine(c(LATON), Offset(cx0, hy), Offset(cx0, hy + 9 * u), 1f * u)
                hy += 180 * u
            }
            bisel(lad, Color.White.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
        }
        // Los estantes de caoba, con un filete de latón en el canto.
        d.tablas.forEachIndexed { k, tb ->
            drawRect(c(CAOBA), tb.topLeft, tb.size)
            vetas(tb, c(CAOBA_VETA).copy(alpha = 0.5f), 120 + k, densidad = 0.7f, grosor = 0.7f)
            drawRect(c(CAOBA_LUZ), tb.topLeft, Size(tb.width, 1.6f * u))
            drawRect(c(LATON), Offset(tb.left, tb.bottom - 2 * u), Size(tb.width, 1.2f * u))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(tb.left, tb.bottom), Size(tb.width, 3 * u))
        }
        // El marco de latón de cada mes, con su tarjeta.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1 * u, r.top + 1 * u), r.size)
            drawRect(c(LATON), r.topLeft, r.size)
            drawRect(c(PERGAMINO), r.deflate(1.8f * u).topLeft, r.deflate(1.8f * u).size)
            drawRect(c(LATON_OSC), r.topLeft, r.size, style = Stroke(0.6f * u))
            listOf(r.left + 4.5f * u, r.right - 4.5f * u).forEach { x -> drawCircle(c(LATON_OSC), 1 * u, Offset(x, r.center.y)) }
            textoEn(medidor, p.texto, Rect(r.left + 8 * u, r.top + 2 * u, r.right - 8 * u, r.bottom - 2 * u), letra, c(TINTA), 10f, espaciado = 1.2f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el fondo del baúl con sus cantoneras y sus patines, sobre la alfombra de campaña.
        val b = d.base
        val piso = b.top + b.height * 0.58f
        drawRect(c(TIERRA), Offset(0f, piso), Size(w, h - piso))
        val alfombra = Rect(w * 0.02f, piso + (h - piso) * 0.1f, w * 0.98f, h)
        drawRect(c(ALFOMBRA), alfombra.topLeft, alfombra.size)
        drawRect(c(ALFOMBRA_ORLA), alfombra.topLeft, Size(alfombra.width, (h - piso) * 0.18f))
        var ax = alfombra.left + 4 * u
        while (ax < alfombra.right - 4 * u) {
            val rombo = Path().apply {
                val cy = alfombra.top + (h - piso) * 0.09f
                moveTo(ax, cy); lineTo(ax + 3 * u, cy - 2 * u); lineTo(ax + 6 * u, cy); lineTo(ax + 3 * u, cy + 2 * u); close()
            }
            drawPath(rombo, c(ORO).copy(alpha = 0.7f))
            ax += 10 * u
        }
        val fondo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(CUERO), fondo.topLeft, fondo.size)
        drawRect(c(CAOBA), fondo.topLeft, Size(fondo.width, 3 * u))
        var cx = fondo.left + 6 * u
        while (cx < fondo.right - 4 * u) { drawCircle(c(LATON), 1.2f * u, Offset(cx, fondo.top + fondo.height * 0.4f)); cx += 8 * u }
        listOf(true, false).forEach { izq ->
            val x0 = if (izq) fondo.left else fondo.right
            val s = if (izq) 1f else -1f
            val cant = Path().apply { moveTo(x0, fondo.top + 3 * u); lineTo(x0 + s * fondo.height * 0.9f, fondo.bottom); lineTo(x0, fondo.bottom); close() }
            drawPath(cant, c(LATON))
            drawPath(cant, c(LATON_OSC), style = Stroke(0.7f * u))
            drawRect(c(CAOBA_VETA), Offset(if (izq) x0 + fondo.width * 0.04f else x0 - fondo.width * 0.16f, piso), Size(fondo.width * 0.12f, 3 * u))
        }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(fondo.left + fondo.width * 0.04f, piso + 3 * u), Size(fondo.width * 0.92f, 3 * u))
    }

    /** La lona de la tienda con su lambrequín y su mástil, el farol, y la tapa del baúl con lo que hay encima. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        // La lona a rayas, con sus pliegues.
        drawRect(c(LONA), r.topLeft, r.size)
        var x = r.left
        var k = 0
        while (x < r.right) {
            if (k % 2 == 0) drawRect(c(LONA_RAYA), Offset(x, r.top), Size(11 * u, r.height))
            x += 11 * u; k++
        }
        listOf(0.2f, 0.47f, 0.73f).forEach { f -> drawRect(Color.Black.copy(alpha = 0.08f), Offset(r.left + r.width * f, r.top), Size(r.width * 0.06f, r.height)) }
        // El lambrequín festoneado arriba, con su galón.
        val fest = r.top + r.height * 0.12f
        drawRect(c(LONA_RAYA), Offset(r.left, r.top), Size(r.width, fest - r.top))
        val ondas = Path().apply {
            moveTo(r.left, fest)
            var xx = r.left
            val paso = 18 * u
            while (xx < r.right) { quadraticTo(xx + paso / 2, fest + 9 * u, xx + paso, fest); xx += paso }
            lineTo(r.right, r.top); lineTo(r.left, r.top); close()
        }
        drawPath(ondas, c(LONA_RAYA))
        drawLine(c(ORO), Offset(r.left, fest - 2 * u), Offset(r.right, fest - 2 * u), 1.2f * u)
        // El mástil de la tienda y el farol de campaña que cuelga de él.
        val mx = r.left + r.width * 0.2f
        drawRect(c(MASTIL), Offset(mx - 3 * u, r.top), Size(6 * u, r.height))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(mx + 1.5f * u, r.top), Size(1.5f * u, r.height))
        val gancho = Offset(mx + 4 * u, r.top + r.height * 0.24f)
        drawLine(c(LATON_OSC), Offset(mx + 3 * u, gancho.y), Offset(gancho.x + 7 * u, gancho.y), 1.4f * u)
        val fc = Offset(gancho.x + 7 * u, gancho.y + r.height * 0.22f)
        val fa = r.height * 0.11f
        drawLine(c(LATON_OSC), Offset(gancho.x + 7 * u, gancho.y), Offset(fc.x, fc.y - fa * 1.2f), 1f * u)
        if (noche) mancha(fc, r.height * 0.55f, FAROL.copy(alpha = 0.4f))
        val cristal = Rect(fc.x - fa * 0.55f, fc.y - fa * 0.8f, fc.x + fa * 0.55f, fc.y + fa * 0.8f)
        drawRect(if (noche) FAROL.copy(alpha = 0.75f) else c(PERGAMINO).copy(alpha = 0.4f), cristal.topLeft, cristal.size)
        if (noche) drawOval(Color(0xFFFFF4D2), Offset(fc.x - fa * 0.14f, fc.y - fa * 0.3f), Size(fa * 0.28f, fa * 0.5f))
        drawRect(c(TINTA), cristal.topLeft, cristal.size, style = Stroke(1.2f * u))
        drawLine(c(TINTA), Offset(fc.x, cristal.top), Offset(fc.x, cristal.bottom), 0.8f * u)
        val techo = Path().apply { moveTo(cristal.left - 2 * u, cristal.top); lineTo(cristal.right + 2 * u, cristal.top); lineTo(fc.x, cristal.top - fa * 0.45f); close() }
        drawPath(techo, c(TINTA))
        drawRect(c(TINTA), Offset(cristal.left - 2 * u, cristal.bottom), Size(cristal.width + 4 * u, 2.5f * u))
        // La tapa del baúl: cuero con su canto de caoba, cantoneras, clavos y el asa de latón.
        val tapa = Rect(r.left, r.bottom - r.height * 0.3f, r.right, r.bottom)
        drawRect(c(CUERO), tapa.topLeft, tapa.size)
        drawRect(c(CUERO_LUZ), tapa.topLeft, Size(tapa.width, 1.6f * u))
        drawRect(c(CAOBA), Offset(tapa.left, tapa.bottom - 4 * u), Size(tapa.width, 4 * u))
        var cx = tapa.left + 8 * u
        while (cx < tapa.right - 6 * u) { drawCircle(c(LATON), 1.2f * u, Offset(cx, tapa.top + 4 * u)); drawCircle(c(LATON), 1.2f * u, Offset(cx, tapa.bottom - 7 * u)); cx += 8 * u }
        listOf(true, false).forEach { izq ->
            val x0 = if (izq) tapa.left else tapa.right
            val s = if (izq) 1f else -1f
            val cant = Path().apply { moveTo(x0, tapa.top); lineTo(x0 + s * tapa.height * 0.8f, tapa.top); lineTo(x0, tapa.top + tapa.height * 0.8f); close() }
            drawPath(cant, c(LATON))
            drawPath(cant, c(LATON_OSC), style = Stroke(0.7f * u))
        }
        val asa = Rect(tapa.center.x - tapa.width * 0.08f, tapa.top - tapa.height * 0.4f, tapa.center.x + tapa.width * 0.08f, tapa.top + tapa.height * 0.4f)
        drawArc(c(LATON), 180f, 180f, false, asa.topLeft, asa.size, style = Stroke(2.2f * u))
        listOf(asa.left, asa.right).forEach { ax -> drawRect(c(LATON_OSC), Offset(ax - 3 * u, tapa.top + 1 * u), Size(6 * u, 5 * u)) }
        // Encima, un mapa doblado y un catalejo.
        val mapa = Rect(r.left + r.width * 0.56f, tapa.top - r.height * 0.06f, r.left + r.width * 0.78f, tapa.top)
        drawRect(c(PERGAMINO), mapa.topLeft, mapa.size)
        listOf(0.33f, 0.66f).forEach { f -> drawLine(Color.Black.copy(alpha = 0.2f), Offset(mapa.left + mapa.width * f, mapa.top), Offset(mapa.left + mapa.width * f, mapa.bottom), 0.6f * u) }
        drawLine(c(TEJUELO).copy(alpha = 0.6f), Offset(mapa.left + mapa.width * 0.1f, mapa.top + mapa.height * 0.5f), Offset(mapa.right - mapa.width * 0.2f, mapa.top + mapa.height * 0.3f), 0.6f * u)
        val tubo = Rect(r.left + r.width * 0.32f, tapa.top - r.height * 0.045f, r.left + r.width * 0.48f, tapa.top)
        drawRect(c(LATON), tubo.topLeft, tubo.size)
        drawRect(c(CUERO_OSC), Offset(tubo.left + tubo.width * 0.3f, tubo.top), Size(tubo.width * 0.45f, tubo.height))
        drawRect(c(LATON_LUZ), Offset(tubo.left, tubo.top), Size(tubo.width, tubo.height * 0.25f))
        drawRect(c(LATON), Offset(tubo.right, tubo.top - tubo.height * 0.15f), Size(tubo.width * 0.12f, tubo.height * 1.15f))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = TAFILETES[(v.semilla and 0x7fffffff) % TAFILETES.size]
        val tafilete = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val oro = c(ORO)
        // El lomo de tafilete, con su grano y sus cantos en sombra.
        drawRoundRect(tafilete, Offset.Zero, size, CornerRadius(1.5f * u))
        for (j in 0 until (w * h / (40 * u * u)).toInt().coerceAtMost(60)) {
            drawCircle(Color.Black.copy(alpha = 0.1f), 0.5f * u, Offset(w * azar(v.semilla, 60 + j), h * azar(v.semilla, 160 + j)))
        }
        drawRect(Color.Black.copy(alpha = 0.26f), Offset.Zero, Size(w * 0.1f, h))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(w * 0.9f, 0f), Size(w * 0.1f, h))
        drawRect(Color.White.copy(alpha = 0.07f), Offset(w * 0.28f, 0f), Size(w * 0.12f, h))
        // Los filetes dorados que dividen el lomo en entrenervios.
        listOf(0.05f, 0.29f, 0.6f, 0.79f, 0.95f).forEach { f ->
            drawLine(oro, Offset(w * 0.1f, h * f), Offset(w * 0.9f, h * f), 0.6f * u)
            drawLine(oro, Offset(w * 0.1f, h * f + 1.6f * u), Offset(w * 0.9f, h * f + 1.6f * u), 0.5f * u)
        }
        // El tejuelo rojo con el día.
        val tj = Rect(w * 0.12f, h * 0.09f, w * 0.88f, h * 0.26f)
        drawRect(c(TEJUELO), tj.topLeft, tj.size)
        drawRect(oro, tj.deflate(1 * u).topLeft, tj.deflate(1 * u).size, style = Stroke(0.5f * u))
        textoEn(medidor, v.dia, tj.deflate(1.5f * u), letra, oro, 12f, peso = FontWeight.SemiBold)
        // El mes, dorado a lo largo.
        textoEn(medidor, v.mes, Rect(w * 0.16f, h * 0.31f, w * 0.84f, h * 0.59f), letra, oro, 8f, vertical = true, minimo = 4f, espaciado = 0.5f)
        // La N en su corona de laurel.
        val cn = Offset(w * 0.5f, h * 0.695f)
        val rn = minOf(w * 0.34f, h * 0.075f)
        drawArc(oro, 100f, 160f, false, Offset(cn.x - rn, cn.y - rn), Size(rn * 2, rn * 2), style = Stroke(0.6f * u))
        drawArc(oro, -80f, 160f, false, Offset(cn.x - rn, cn.y - rn), Size(rn * 2, rn * 2), style = Stroke(0.6f * u))
        for (j in 0 until 4) {
            val a = (Math.toRadians(120.0 + j * 30.0)).toFloat()
            drawCircle(oro, 0.7f * u, Offset(cn.x + rn * cos(a), cn.y + rn * sin(a)))
            drawCircle(oro, 0.7f * u, Offset(cn.x - rn * cos(a), cn.y + rn * sin(a)))
        }
        textoEn(medidor, "N", Rect(cn, rn * 0.85f), letra, oro, 10f, peso = FontWeight.SemiBold, minimo = 4f)
        // Las abejas, más cuanto más escribiste; de noche, la primera es una luna.
        val abejas = 1 + (v.lleno * 2.2f).toInt()
        val sb = minOf(w * 0.2f, 3.4f * u)
        for (j in 0 until abejas) {
            val o = Offset(w * (0.5f + (j - (abejas - 1) / 2f) * 0.26f), h * 0.875f)
            if (j == 0 && v.noche) {
                drawCircle(LUNA, sb * 0.8f, o)
                drawCircle(tafilete, sb * 0.66f, Offset(o.x + sb * 0.36f, o.y - sb * 0.2f))
            } else {
                drawOval(oro.copy(alpha = 0.6f), Offset(o.x - sb * 0.9f, o.y - sb * 0.7f), Size(sb * 0.8f, sb * 0.5f))
                drawOval(oro.copy(alpha = 0.6f), Offset(o.x + sb * 0.1f, o.y - sb * 0.7f), Size(sb * 0.8f, sb * 0.5f))
                drawOval(oro, Offset(o.x - sb * 0.25f, o.y - sb * 0.5f), Size(sb * 0.5f, sb * 1.1f))
            }
        }
    }
}

/**
 * Art déco: el estante del salón de un transatlántico. Arce claro chapeado en libro, con bandas de cromo en los costados y
 * en el canto de cada tabla. Arriba, el remate escalonado en tres gradas con un sol de marquetería, rayos de arce y nogal
 * que salen del ojo de buey de cromo atornillado: de día se ven el cielo y el mar con su horizonte; de noche, el mar oscuro
 * con la luna y su reflejo. Abajo, el zócalo escalonado con sus filetes de cromo y un piso de linóleo en damero. Los meses
 * van en cartelas de laca negra de esquinas cortadas. Cada entrada es un álbum de viaje con lomo geométrico, bandas de cromo
 * arriba y abajo, la cartela con el día, el mes a lo largo, chevrones dorados que se suman cuanto más escribiste y un sol
 * pequeño; de noche, una luna en lugar del sol. Distinto del Ideario, que es el casillero de laca de la recepción de un hotel.
 */
object EstanteDeco : DisenoBiblioteca {
    private val ARCE = Color(0xFFD9B97E)
    private val ARCE_LUZ = Color(0xFFEBD3A0)
    private val ARCE_VETA = Color(0xFFB8935A)
    private val NOGAL = Color(0xFF5A3A22)
    private val CROMO = Color(0xFFC9CED3)
    private val CROMO_LUZ = Color(0xFFF1F4F6)
    private val CROMO_OSC = Color(0xFF7D858C)
    private val LACA = Color(0xFF16171A)
    private val PARED = Color(0xFF23414A)
    private val MAR = Color(0xFF2E6A8E)
    private val CIELO = Color(0xFFAED3E3)
    private val MAR_NOCHE = Color(0xFF0F2034)
    private val CIELO_NOCHE = Color(0xFF16233C)
    private val CREMA = Color(0xFFF0E6CF)
    private val ORO = Color(0xFFCDAA5C)
    private val LINOLEO_A = Color(0xFF1D1E22)
    private val LINOLEO_B = Color(0xFFE6DCC4)
    /** Las tapas de los álbumes: negro, jade, coral, azul marino, crema y burdeos. */
    private val ALBUMES = listOf(Color(0xFF151619), Color(0xFF1F6B5C), Color(0xFFC4614B), Color(0xFF1E2F5C), Color(0xFFD9CBAA), Color(0xFF7A2638))
    private val NOCHE = Color(0xFF101B2E)
    private val LUNA = Color(0xFFF2EBCB)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, bajo la luna del mar. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0C0C0E), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.45f)
        return r
    }

    override val corona = 0.32f
    override val base = 0.1f
    override val lado = 0.055f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.pasaje

    override fun ancho(palabras: Int): Dp = (22 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.14f * azar(semilla, 7)

    /** Una banda de cromo: gris con su brillo arriba y su sombra abajo. */
    private fun DrawScope.cromo(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawRect(t(CROMO, oscuro, noche), r.topLeft, r.size)
        drawRect(t(CROMO_LUZ, oscuro, noche), r.topLeft, Size(r.width, minOf(r.height * 0.35f, 1.2f * u)))
        drawRect(t(CROMO_OSC, oscuro, noche), Offset(r.left, r.bottom - minOf(r.height * 0.3f, 1 * u)), Size(r.width, minOf(r.height * 0.3f, 1 * u)))
    }

    /** Un rectángulo con las cuatro esquinas cortadas en escalón, como las cartelas déco. */
    private fun escalonado(r: Rect, e: Float): Path = Path().apply {
        moveTo(r.left + e, r.top); lineTo(r.right - e, r.top); lineTo(r.right - e, r.top + e * 0.5f); lineTo(r.right - e * 0.5f, r.top + e * 0.5f)
        lineTo(r.right - e * 0.5f, r.top + e); lineTo(r.right, r.top + e); lineTo(r.right, r.bottom - e); lineTo(r.right - e * 0.5f, r.bottom - e)
        lineTo(r.right - e * 0.5f, r.bottom - e * 0.5f); lineTo(r.right - e, r.bottom - e * 0.5f); lineTo(r.right - e, r.bottom); lineTo(r.left + e, r.bottom)
        lineTo(r.left + e, r.bottom - e * 0.5f); lineTo(r.left + e * 0.5f, r.bottom - e * 0.5f); lineTo(r.left + e * 0.5f, r.bottom - e); lineTo(r.left, r.bottom - e)
        lineTo(r.left, r.top + e); lineTo(r.left + e * 0.5f, r.top + e); lineTo(r.left + e * 0.5f, r.top + e * 0.5f); lineTo(r.left + e, r.top + e * 0.5f); close()
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de arce chapeado en libro: paños con la veta en espejo.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(ARCE), cuerpo.topLeft, cuerpo.size)
        val panos = 4
        for (k in 0 until panos) {
            val p = Rect(cuerpo.left + cuerpo.width * k / panos, cuerpo.top, cuerpo.left + cuerpo.width * (k + 1) / panos, cuerpo.bottom)
            if (k % 2 == 1) drawRect(c(ARCE_LUZ).copy(alpha = 0.35f), p.topLeft, p.size)
            vetas(p, c(ARCE_VETA).copy(alpha = 0.45f), 140 + k / 2, densidad = 0.6f, vertical = true, grosor = 0.6f, ondulacion = 1.6f)
            drawLine(c(ARCE_VETA), Offset(p.left, p.top), Offset(p.left, p.bottom), 0.6f * u)
        }
        // Cada repisa: la sombra de la tabla de arriba.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.36f else 0.24f), rep.topLeft, Size(rep.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.1f), rep.topLeft, Size(rep.width, 15 * u))
            drawRect(Color.Black.copy(alpha = 0.14f), rep.topLeft, Size(6 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.14f), Offset(rep.right - 6 * u, rep.top), Size(6 * u, rep.height))
        }
        // Los costados: arce con el canto de cromo y, a trechos, tres líneas de velocidad.
        listOf(d.izquierda to true, d.derecha to false).forEach { (lad, izq) ->
            drawRect(c(ARCE), lad.topLeft, lad.size)
            vetas(lad, c(ARCE_VETA).copy(alpha = 0.5f), if (izq) 150 else 151, densidad = 1f, vertical = true, grosor = 0.6f)
            val ck = lad.width * 0.22f
            cromo(Rect(if (izq) lad.left else lad.right - ck, lad.top, if (izq) lad.left + ck else lad.right, lad.bottom), oscuro, noche)
            var yy = lad.top + 30 * u
            while (yy < lad.bottom - 20 * u) {
                for (j in 0 until 3) cromo(Rect(lad.left + lad.width * 0.25f, yy + j * 4 * u, lad.right - lad.width * 0.25f, yy + j * 4 * u + 1.8f * u), oscuro, noche)
                yy += 130 * u
            }
            bisel(lad, Color.White.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.18f), 1.2f * u)
        }
        // Las tablas: arce con el canto de cromo.
        d.tablas.forEachIndexed { k, tb ->
            drawRect(c(ARCE), tb.topLeft, tb.size)
            vetas(tb, c(ARCE_VETA).copy(alpha = 0.45f), 160 + k, densidad = 0.6f, grosor = 0.6f)
            cromo(Rect(tb.left, tb.top, tb.right, tb.top + 3 * u), oscuro, noche)
            cromo(Rect(tb.left, tb.bottom - 2.4f * u, tb.right, tb.bottom), oscuro, noche)
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(tb.left, tb.bottom), Size(tb.width, 3 * u))
        }
        // La cartela de laca negra de cada mes, de esquinas escalonadas y borde de cromo.
        d.placas.forEach { p ->
            val r = p.r
            val forma = escalonado(r, r.height * 0.36f)
            drawPath(forma, c(LACA))
            drawPath(forma, c(CROMO), style = Stroke(1.2f * u))
            textoEn(medidor, p.texto, Rect(r.left + r.height * 0.5f, r.top + 1 * u, r.right - r.height * 0.5f, r.bottom - 1 * u), letra, c(CREMA), 10f, peso = FontWeight.SemiBold, espaciado = 1.4f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo escalonado con sus filetes de cromo, sobre el linóleo en damero.
        val b = d.base
        val piso = b.top + b.height * 0.6f
        val cuadro = 14 * u
        var fy = piso; var fila = 0
        while (fy < h) {
            var fx = 0f; var col = 0
            while (fx < w) {
                drawRect(c(if ((fila + col) % 2 == 0) LINOLEO_A else LINOLEO_B), Offset(fx, fy), Size(cuadro, cuadro))
                fx += cuadro; col++
            }
            fy += cuadro; fila++
        }
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(LACA), zocalo.topLeft, zocalo.size)
        val grada = Rect(b.left + b.width * 0.03f, b.top, b.right - b.width * 0.03f, b.top + (piso - b.top) * 0.55f)
        drawRect(c(ARCE), grada.topLeft, grada.size)
        vetas(grada, c(ARCE_VETA).copy(alpha = 0.45f), 170, densidad = 0.6f, grosor = 0.6f)
        cromo(Rect(zocalo.left, grada.bottom, zocalo.right, grada.bottom + 2.4f * u), oscuro, noche)
        cromo(Rect(zocalo.left, zocalo.bottom - (piso - b.top) * 0.2f, zocalo.right, zocalo.bottom - (piso - b.top) * 0.2f + 1.8f * u), oscuro, noche)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** El remate escalonado con el sol de marquetería y el ojo de buey. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(PARED), r.topLeft, r.size)
        // Las tres gradas del remate.
        val gradas = listOf(
            Rect(r.left, r.top + r.height * 0.42f, r.right, r.bottom),
            Rect(r.left + r.width * 0.14f, r.top + r.height * 0.2f, r.right - r.width * 0.14f, r.bottom),
            Rect(r.left + r.width * 0.3f, r.top + r.height * 0.03f, r.right - r.width * 0.3f, r.bottom),
        )
        val silueta = Path().apply { gradas.forEach { g -> addRect(g) } }
        gradas.forEachIndexed { k, g ->
            drawRect(c(ARCE), g.topLeft, g.size)
            vetas(g, c(ARCE_VETA).copy(alpha = 0.4f), 180 + k, densidad = 0.5f, grosor = 0.6f)
        }
        // El sol de marquetería: rayos de arce claro y nogal que salen del ojo de buey.
        val co = Offset(r.center.x, r.top + r.height * 0.52f)
        val ro = r.height * 0.28f
        clipPath(silueta) {
            val largo = r.width
            val n = 18
            for (k in 0 until n) {
                val a0 = (Math.PI * (1.0 + k / n.toDouble())).toFloat()
                val a1 = (Math.PI * (1.0 + (k + 1) / n.toDouble())).toFloat()
                val rayo = Path().apply {
                    moveTo(co.x + ro * cos(a0), co.y + ro * sin(a0))
                    lineTo(co.x + largo * cos(a0), co.y + largo * sin(a0))
                    lineTo(co.x + largo * cos(a1), co.y + largo * sin(a1))
                    lineTo(co.x + ro * cos(a1), co.y + ro * sin(a1)); close()
                }
                drawPath(rayo, c(if (k % 2 == 0) NOGAL else ARCE_LUZ).copy(alpha = if (k % 2 == 0) 0.85f else 0.7f))
            }
        }
        // Los cantos de cada grada, en cromo.
        gradas.forEach { g -> cromo(Rect(g.left, g.top, g.right, g.top + 2.4f * u), oscuro, noche) }
        gradas.drop(1).forEach { g ->
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(g.left - 2 * u, g.top), Size(2 * u, r.bottom - g.top))
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(g.right, g.top), Size(2 * u, r.bottom - g.top))
        }
        // El ojo de buey: de día, cielo y mar; de noche, el mar oscuro con la luna y su reflejo.
        val vidrio = Path().apply { addOval(Rect(co, ro)) }
        drawPath(vidrio, if (noche) CIELO_NOCHE else c(CIELO))
        clipPath(vidrio) {
            val horizonte = co.y + ro * 0.15f
            drawRect(if (noche) MAR_NOCHE else c(MAR), Offset(co.x - ro, horizonte), Size(ro * 2, ro * 2))
            if (noche) {
                val lu = Offset(co.x + ro * 0.3f, co.y - ro * 0.4f)
                mancha(lu, ro * 0.7f, LUNA.copy(alpha = 0.35f))
                drawCircle(LUNA, ro * 0.16f, lu)
                for (j in 0 until 6) {
                    val yy = horizonte + ro * (0.08f + 0.13f * j)
                    val an = ro * (0.08f + 0.05f * j)
                    drawLine(LUNA.copy(alpha = 0.6f - 0.08f * j), Offset(lu.x - an, yy), Offset(lu.x + an, yy), 1f * u)
                }
                for (j in 0 until 5) drawCircle(LUNA.copy(alpha = 0.7f), 0.7f * u, Offset(co.x - ro * (0.6f - 0.22f * j), co.y - ro * (0.3f + 0.25f * azar(9, j))))
            } else {
                for (j in 0 until 5) {
                    val yy = horizonte + ro * (0.12f + 0.16f * j)
                    drawLine(Color.White.copy(alpha = 0.3f), Offset(co.x - ro * (0.7f - 0.1f * j), yy), Offset(co.x - ro * (0.2f - 0.1f * j), yy), 0.8f * u)
                }
                drawOval(Color.White.copy(alpha = 0.6f), Offset(co.x - ro * 0.55f, co.y - ro * 0.5f), Size(ro * 0.5f, ro * 0.16f))
            }
            drawLine(Color.White.copy(alpha = 0.18f), Offset(co.x - ro * 0.8f, co.y + ro * 0.3f), Offset(co.x - ro * 0.1f, co.y - ro * 0.9f), 4 * u)
        }
        drawCircle(c(CROMO), ro + 3 * u, co, style = Stroke(6 * u))
        drawCircle(c(CROMO_LUZ), ro + 4.5f * u, co, style = Stroke(1 * u))
        drawCircle(c(CROMO_OSC), ro, co, style = Stroke(1 * u))
        for (k in 0 until 8) {
            val a = (k * Math.PI / 4).toFloat()
            drawCircle(c(CROMO_OSC), 1.4f * u, Offset(co.x + (ro + 3 * u) * cos(a), co.y + (ro + 3 * u) * sin(a)))
        }
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val i = (v.semilla and 0x7fffffff) % ALBUMES.size
        val base = ALBUMES[i]
        val tapa = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val claro = i == 4
        val dorado = c(if (claro) NOGAL else ORO)
        // El lomo del álbum, con su sombra y su luz.
        drawRoundRect(tapa, Offset.Zero, size, CornerRadius(1.5f * u))
        drawRect(Color.Black.copy(alpha = 0.24f), Offset.Zero, Size(w * 0.1f, h))
        drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.86f, 0f), Size(w * 0.06f, h))
        // Las bandas de cromo arriba y abajo.
        cromo(Rect(0f, h * 0.05f, w, h * 0.085f), oscuro, v.noche)
        cromo(Rect(0f, h * 0.915f, w, h * 0.95f), oscuro, v.noche)
        // La cartela escalonada con el día.
        val ct = Rect(w * 0.14f, h * 0.12f, w * 0.86f, h * 0.3f)
        val forma = escalonado(ct, minOf(ct.width, ct.height) * 0.28f)
        drawPath(forma, c(CREMA))
        drawPath(forma, dorado, style = Stroke(0.7f * u))
        textoEn(medidor, v.dia, ct.deflate(2 * u), letra, c(LACA), 13f, peso = FontWeight.SemiBold)
        // El mes a lo largo del lomo.
        textoEn(medidor, v.mes, Rect(w * 0.18f, h * 0.33f, w * 0.82f, h * 0.66f), letra, if (claro) c(LACA) else c(CREMA), 8f, vertical = true, minimo = 4f, espaciado = 0.6f)
        // Los chevrones dorados: más cuanto más escribiste.
        val chevrones = 1 + (v.lleno * 2.5f).toInt()
        for (j in 0 until chevrones) {
            val yy = h * (0.7f + 0.04f * j)
            val cheuron = Path().apply { moveTo(w * 0.2f, yy + h * 0.025f); lineTo(w * 0.5f, yy); lineTo(w * 0.8f, yy + h * 0.025f) }
            drawPath(cheuron, dorado, style = Stroke(0.9f * u))
        }
        // Un sol pequeño abajo; de noche, una luna.
        val cs = Offset(w * 0.5f, h * 0.885f)
        val s = minOf(w * 0.22f, 4.5f * u)
        if (v.noche) {
            drawCircle(LUNA, s * 0.7f, cs)
            drawCircle(tapa, s * 0.6f, Offset(cs.x + s * 0.32f, cs.y - s * 0.18f))
        } else {
            drawArc(dorado, 180f, 180f, true, Offset(cs.x - s * 0.45f, cs.y - s * 0.45f), Size(s * 0.9f, s * 0.9f))
            for (j in 0 until 5) {
                val a = (Math.PI * (1.1 + 0.2 * j)).toFloat()
                drawLine(dorado, Offset(cs.x + s * 0.6f * cos(a), cs.y + s * 0.6f * sin(a)), Offset(cs.x + s * cos(a), cs.y + s * sin(a)), 0.6f * u)
            }
        }
    }
}

/**
 * Botánico: la caja de Ward. Arriba, la vitrina de hierro pintado de verde y vidrio, con su tejado a dos aguas, su remate y
 * sus parteluces, sobre una bandeja de tierra: dentro crecen un helecho, una begonia de hojas manchadas y el musgo, con gotas
 * de condensación en los vidrios. Debajo, el mueble de estantes de roble con montantes de hierro colado, cada uno con su
 * roseta de hojas, contra un fondo de tablas verdes. Abajo, el zócalo de hierro con sus pies en voluta y un piso de baldosas
 * de barro. Los meses van en etiquetas de herbario en su marco de latón. Cada entrada es un cuaderno de campo de tela verde,
 * con la etiqueta del día y el mes, su presilla para el lápiz, una hoja estampada en seco y hojas prensadas que asoman por
 * arriba (más cuanto más escribiste); de noche, una luciérnaga en lugar de la hoja estampada. De noche vuelan luciérnagas
 * por la vitrina y el estante. Distinto del Ideario, que es el gabinete de roble del naturalista con su campana de vidrio.
 */
object EstanteBotanico : DisenoBiblioteca {
    private val HIERRO = Color(0xFF24382C)
    private val HIERRO_LUZ = Color(0xFF4A6553)
    private val FONDO = Color(0xFF56705B)
    private val PARED = Color(0xFFCFC7AA)
    private val VIDRIO = Color(0xFFDDEDE2)
    private val ROBLE = Color(0xFF8A6A42)
    private val ROBLE_LUZ = Color(0xFFA8865A)
    private val ROBLE_VETA = Color(0xFF5E4528)
    private val TIERRA = Color(0xFF4A3526)
    private val HELECHO = Color(0xFF4F7D3E)
    private val HOJA_CLARA = Color(0xFF86A95B)
    private val MUSGO = Color(0xFF6B8A3F)
    private val BEGONIA = Color(0xFF6E4A4E)
    private val OTONO = Color(0xFFB0773A)
    private val PAPEL = Color(0xFFF1EAD6)
    private val TINTA = Color(0xFF2A2A22)
    private val BARRO = Color(0xFFB0603E)
    private val LATON = Color(0xFFC2A050)
    /** Las telas de los cuadernos: verde musgo, oliva, verde mar, salvia y verde botella. */
    private val TELAS = listOf(Color(0xFF3E5E3A), Color(0xFF566B3A), Color(0xFF2F4F45), Color(0xFF6E7A4E), Color(0xFF35503B))
    private val NOCHE = Color(0xFF121C18)
    private val LUCIERNAGA = Color(0xFFE8F27A)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la penumbra del invernadero. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0B0D0A), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.48f)
        return r
    }

    override val corona = 0.42f
    override val base = 0.1f
    override val lado = 0.05f
    override val altoRepisa: Dp = 110.dp
    override val tabla: Dp = 15.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.fraunces

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.14f * azar(semilla, 8)

    /** Una luciérnaga: el punto de luz con su halo en anillos (sin degradados). */
    private fun DrawScope.luciernaga(o: Offset, r: Float) {
        drawCircle(LUCIERNAGA.copy(alpha = 0.08f), r * 4f, o)
        drawCircle(LUCIERNAGA.copy(alpha = 0.14f), r * 2.4f, o)
        drawCircle(LUCIERNAGA.copy(alpha = 0.95f), r, o)
    }

    /** Una fronda de helecho: el raquis curvo y sus pinnas a los dos lados. */
    private fun DrawScope.fronda(base: Offset, largo: Float, giro: Float, color: Color) {
        val u = density
        val pasos = 9
        var p = base
        var a = Math.toRadians((-90f + giro).toDouble()).toFloat()
        for (k in 0 until pasos) {
            val tramo = largo / pasos
            val q = Offset(p.x + tramo * cos(a), p.y + tramo * sin(a))
            drawLine(color, p, q, 0.9f * u)
            val pinna = largo * 0.16f * (1f - k / pasos.toFloat())
            listOf(1f, -1f).forEach { lado0 ->
                val b = a + lado0 * 1.2f
                drawLine(color, q, Offset(q.x + pinna * cos(b), q.y + pinna * sin(b)), 1.4f * u)
            }
            p = q
            a += Math.toRadians((giro * 0.12f).toDouble()).toFloat()
        }
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de tablas pintadas de verde.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(FONDO), cuerpo.topLeft, cuerpo.size)
        var x = cuerpo.left + 28 * u
        while (x < cuerpo.right) { drawLine(Color.Black.copy(alpha = 0.18f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 0.8f * u); x += 28 * u }
        // Cada repisa: la sombra de la tabla de arriba.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.36f else 0.24f), rep.topLeft, Size(rep.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.1f), rep.topLeft, Size(rep.width, 14 * u))
            drawRect(Color.Black.copy(alpha = 0.14f), rep.topLeft, Size(6 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.14f), Offset(rep.right - 6 * u, rep.top), Size(6 * u, rep.height))
        }
        // Los montantes de hierro colado, con una columnita y una roseta de hojas a cada tramo.
        listOf(d.izquierda, d.derecha).forEach { lad ->
            drawRect(c(HIERRO), lad.topLeft, lad.size)
            drawRect(c(HIERRO_LUZ), Offset(lad.left + lad.width * 0.3f, lad.top), Size(lad.width * 0.1f, lad.height))
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(lad.right - lad.width * 0.2f, lad.top), Size(lad.width * 0.2f, lad.height))
            var ry = lad.top + 40 * u
            while (ry < lad.bottom - 20 * u) {
                val cr = Offset(lad.center.x, ry)
                val rr = lad.width * 0.3f
                for (j in 0 until 4) {
                    rotate(j * 90f + 45f, cr) { drawOval(c(HIERRO_LUZ), Offset(cr.x - rr * 0.22f, cr.y - rr), Size(rr * 0.44f, rr)) }
                }
                drawCircle(c(LATON), rr * 0.22f, cr)
                ry += 96 * u
            }
            bisel(lad, Color.White.copy(alpha = 0.1f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
        }
        // Las tablas de roble, con su fleje de hierro debajo.
        d.tablas.forEachIndexed { k, tb ->
            drawRect(c(ROBLE), tb.topLeft, tb.size)
            vetas(tb, c(ROBLE_VETA).copy(alpha = 0.5f), 200 + k, densidad = 0.8f, grosor = 0.7f)
            drawRect(c(ROBLE_LUZ), tb.topLeft, Size(tb.width, 2 * u))
            drawRect(c(HIERRO), Offset(tb.left, tb.bottom - 2.4f * u), Size(tb.width, 2.4f * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(tb.left, tb.bottom), Size(tb.width, 3 * u))
        }
        // La etiqueta de herbario de cada mes, con su doble filete, en un marco de latón.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(r.left + 1 * u, r.top + 1 * u), r.size)
            drawRect(c(LATON), r.topLeft, r.size)
            val et = r.deflate(1.6f * u)
            drawRect(c(PAPEL), et.topLeft, et.size)
            drawRect(c(TINTA).copy(alpha = 0.6f), et.deflate(1 * u).topLeft, et.deflate(1 * u).size, style = Stroke(0.5f * u))
            textoEn(medidor, p.texto, Rect(et.left + 3 * u, et.top + 1 * u, et.right - 3 * u, et.bottom - 1 * u), letra, c(TINTA), 10f, espaciado = 0.8f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de hierro con sus pies en voluta, sobre las baldosas de barro.
        val b = d.base
        val piso = b.top + b.height * 0.62f
        drawRect(c(BARRO), Offset(0f, piso), Size(w, h - piso))
        val lb = 20 * u
        var by = piso; var fila = 0
        while (by < h) {
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(0f, by), Offset(w, by), 1f * u)
            var bx = if (fila % 2 == 0) 0f else lb / 2
            while (bx < w) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(bx, by), Offset(bx, minOf(by + lb * 0.5f, h)), 1f * u); bx += lb }
            by += lb * 0.5f; fila++
        }
        val zocalo = Rect(b.left, b.top, b.right, piso - b.height * 0.12f)
        drawRect(c(HIERRO), zocalo.topLeft, zocalo.size)
        drawRect(c(HIERRO_LUZ), zocalo.topLeft, Size(zocalo.width, 1.6f * u))
        drawLine(c(LATON), Offset(zocalo.left, zocalo.top + zocalo.height * 0.5f), Offset(zocalo.right, zocalo.top + zocalo.height * 0.5f), 0.8f * u)
        listOf(zocalo.left + b.height * 0.25f, zocalo.right - b.height * 0.25f).forEach { px ->
            val rv = b.height * 0.18f
            drawArc(c(HIERRO), 0f, 360f, false, Offset(px - rv, zocalo.bottom - rv * 0.4f), Size(rv * 2, rv * 2), style = Stroke(2.4f * u))
            drawCircle(c(HIERRO), rv * 0.4f, Offset(px, zocalo.bottom + rv * 0.6f))
        }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
        // De noche, las luciérnagas que vuelan por el estante.
        if (noche) {
            for (k in 0 until 9) luciernaga(Offset(w * (0.08f + 0.84f * azar(210, k)), d.corona.bottom + (d.base.top - d.corona.bottom) * azar(211, k)), 1.4f * u)
        }
    }

    /** La caja de Ward: su bandeja de tierra, los vidrios con sus parteluces, el tejado y las plantas de dentro. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(PARED), r.topLeft, r.size)
        val izq = r.left + r.width * 0.07f; val der = r.right - r.width * 0.07f
        val bandeja = Rect(izq - 4 * u, r.bottom - r.height * 0.14f, der + 4 * u, r.bottom)
        val alero = r.top + r.height * 0.4f
        val cumbre = r.top + r.height * 0.08f
        val vitrina = Path().apply {
            moveTo(izq, bandeja.top); lineTo(izq, alero); lineTo(r.center.x, cumbre); lineTo(der, alero); lineTo(der, bandeja.top); close()
        }
        // El aire de dentro, un poco verdoso, y las plantas.
        drawPath(vitrina, c(mezcla(PARED, HELECHO, 0.25f)))
        clipPath(vitrina) {
            val suelo = bandeja.top
            // El musgo en montículos.
            for (k in 0 until 8) {
                val mx = izq + (der - izq) * (k + 0.5f) / 8f
                drawOval(c(if (k % 2 == 0) MUSGO else HOJA_CLARA), Offset(mx - r.width * 0.07f, suelo - r.height * 0.06f), Size(r.width * 0.14f, r.height * 0.1f))
            }
            // El helecho grande a la izquierda.
            val fb = Offset(izq + (der - izq) * 0.3f, suelo - r.height * 0.02f)
            listOf(-38f, -14f, 6f, 26f, 48f).forEachIndexed { k, g ->
                fronda(fb, r.height * (0.5f + 0.1f * (k % 2)), g, c(if (k % 2 == 0) HELECHO else HOJA_CLARA))
            }
            // La begonia a la derecha, de hojas grandes y manchadas.
            val bb = Offset(izq + (der - izq) * 0.72f, suelo - r.height * 0.03f)
            listOf(Offset(-0.12f, -0.2f), Offset(0.08f, -0.28f), Offset(0.16f, -0.12f), Offset(-0.04f, -0.36f)).forEachIndexed { k, f ->
                val o = Offset(bb.x + r.width * f.x * 0.6f, bb.y + r.height * f.y)
                drawLine(c(BEGONIA), bb, o, 1.2f * u)
                rotate(-30f + 25f * k, o) {
                    drawOval(c(if (k % 2 == 0) HELECHO else MUSGO), Offset(o.x - r.height * 0.09f, o.y - r.height * 0.06f), Size(r.height * 0.18f, r.height * 0.12f))
                    drawCircle(c(PAPEL).copy(alpha = 0.5f), r.height * 0.012f, Offset(o.x - r.height * 0.03f, o.y))
                    drawCircle(c(PAPEL).copy(alpha = 0.5f), r.height * 0.012f, Offset(o.x + r.height * 0.03f, o.y - r.height * 0.015f))
                    drawLine(c(BEGONIA), Offset(o.x - r.height * 0.08f, o.y), Offset(o.x + r.height * 0.08f, o.y), 0.6f * u)
                }
            }
            // El vidrio: un velo claro, el reflejo y las gotas de condensación.
            drawRect(c(VIDRIO).copy(alpha = 0.16f), r.topLeft, r.size)
            drawLine(Color.White.copy(alpha = 0.25f), Offset(izq + (der - izq) * 0.12f, bandeja.top), Offset(izq + (der - izq) * 0.42f, cumbre), 6 * u)
            for (k in 0 until 26) {
                drawCircle(Color.White.copy(alpha = 0.4f), (0.5f + 0.8f * azar(220, k)) * u, Offset(izq + (der - izq) * azar(221, k), cumbre + (bandeja.top - cumbre) * azar(222, k)))
            }
            // De noche, luciérnagas entre las plantas.
            if (noche) for (k in 0 until 6) luciernaga(Offset(izq + (der - izq) * (0.1f + 0.8f * azar(230, k)), cumbre + (bandeja.top - cumbre) * (0.35f + 0.6f * azar(231, k))), 1.3f * u)
        }
        // Los parteluces de hierro y el marco.
        val paneles = 5
        for (k in 1 until paneles) {
            val px = izq + (der - izq) * k / paneles
            val arribaPx = if (px < r.center.x) alero - (alero - cumbre) * (px - izq) / (r.center.x - izq) else alero - (alero - cumbre) * (der - px) / (der - r.center.x)
            drawLine(c(HIERRO), Offset(px, bandeja.top), Offset(px, arribaPx), 1.8f * u)
        }
        drawLine(c(HIERRO), Offset(izq, alero), Offset(der, alero), 2 * u)
        drawLine(c(HIERRO), Offset(izq, bandeja.top - r.height * 0.18f), Offset(der, bandeja.top - r.height * 0.18f), 1.2f * u)
        drawPath(vitrina, c(HIERRO), style = Stroke(2.6f * u))
        // El remate de la cumbre y las crestas del alero.
        drawCircle(c(HIERRO), 3.2f * u, Offset(r.center.x, cumbre - 3 * u))
        drawLine(c(HIERRO), Offset(r.center.x, cumbre), Offset(r.center.x, cumbre - 3 * u), 1.6f * u)
        drawCircle(c(LATON), 1.2f * u, Offset(r.center.x, cumbre - 3.2f * u))
        listOf(izq, der).forEach { ex -> drawCircle(c(HIERRO), 2.2f * u, Offset(ex, alero)) }
        // La bandeja de tierra, de hierro con su filete.
        drawRect(c(HIERRO), bandeja.topLeft, bandeja.size)
        drawRect(c(TIERRA), Offset(bandeja.left + 3 * u, bandeja.top), Size(bandeja.width - 6 * u, 3 * u))
        drawRect(c(HIERRO_LUZ), Offset(bandeja.left, bandeja.top + 3 * u), Size(bandeja.width, 1.2f * u))
        drawLine(c(LATON), Offset(bandeja.left + 4 * u, bandeja.center.y + 2 * u), Offset(bandeja.right - 4 * u, bandeja.center.y + 2 * u), 0.8f * u)
        // De noche, alguna luciérnaga fuera de la vitrina.
        if (noche) {
            luciernaga(Offset(r.left + r.width * 0.035f, r.top + r.height * 0.3f), 1.4f * u)
            luciernaga(Offset(r.right - r.width * 0.04f, r.top + r.height * 0.55f), 1.2f * u)
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = TELAS[(v.semilla and 0x7fffffff) % TELAS.size]
        val tela = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val arriba = h * 0.08f
        // Las hojas prensadas que asoman por arriba: más cuanto más escribiste.
        val hojas = 1 + (v.lleno * 3).toInt()
        for (j in 0 until hojas) {
            val hx = w * (0.22f + 0.56f * (if (hojas == 1) 0.5f else j / (hojas - 1f))) + w * 0.06f * (azar(v.semilla, 70 + j) - 0.5f)
            val largo = arriba * (0.8f + 0.5f * azar(v.semilla, 80 + j)) + 2 * u
            val an = minOf(w * 0.1f, 3 * u)
            val col = when (j % 3) { 0 -> HOJA_CLARA; 1 -> OTONO; else -> HELECHO }
            val hoja = Path().apply {
                moveTo(hx, arriba + 2 * u)
                quadraticTo(hx - an * 1.6f, arriba + 2 * u - largo * 0.5f, hx, (arriba + 2 * u - largo).coerceAtLeast(0f))
                quadraticTo(hx + an * 1.6f, arriba + 2 * u - largo * 0.5f, hx, arriba + 2 * u); close()
            }
            drawPath(hoja, c(col))
            drawLine(c(ROBLE_VETA).copy(alpha = 0.6f), Offset(hx, arriba + 2 * u), Offset(hx, (arriba + 2 * u - largo * 0.9f).coerceAtLeast(0f)), 0.5f * u)
        }
        // Las tapas de tela, con su trama y los cantos de las hojas en la cabeza.
        drawRoundRect(tela, Offset(0f, arriba), Size(w, h - arriba), CornerRadius(1.5f * u))
        var k = 0f
        while (k < w) { drawLine(Color.White.copy(alpha = 0.05f), Offset(k, arriba), Offset(k, h), 0.5f * u); k += 2 * u }
        drawRect(Color.Black.copy(alpha = 0.24f), Offset(0f, arriba), Size(w * 0.12f, h - arriba))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.16f, arriba), Size(w * 0.1f, h - arriba))
        drawRect(c(PAPEL).copy(alpha = 0.6f), Offset(w * 0.06f, arriba), Size(w * 0.88f, 1 * u))
        // La etiqueta de papel con el día y el mes.
        val et = Rect(w * 0.16f, h * 0.18f, w * 0.84f, h * 0.48f)
        drawRect(c(PAPEL), et.topLeft, et.size)
        drawRect(c(TINTA).copy(alpha = 0.6f), et.deflate(1.2f * u).topLeft, et.deflate(1.2f * u).size, style = Stroke(0.5f * u))
        textoEn(medidor, v.dia, Rect(et.left + 1.5f * u, et.top + et.height * 0.08f, et.right - 1.5f * u, et.top + et.height * 0.62f), letra, c(TINTA), 13f, peso = FontWeight.SemiBold)
        drawLine(c(TINTA).copy(alpha = 0.4f), Offset(et.left + et.width * 0.2f, et.top + et.height * 0.64f), Offset(et.right - et.width * 0.2f, et.top + et.height * 0.64f), 0.5f * u)
        textoEn(medidor, v.mes, Rect(et.left + 1.5f * u, et.top + et.height * 0.66f, et.right - 1.5f * u, et.bottom - 1.5f * u), letra, c(TINTA).copy(alpha = 0.85f), 7.5f, minimo = 4f)
        // La presilla de tela para el lápiz, a la derecha.
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(w * 0.88f, h * 0.55f), Size(w * 0.12f, h * 0.12f))
        drawRect(c(OTONO).copy(alpha = 0.8f), Offset(w * 0.9f, h * 0.53f), Size(w * 0.08f, h * 0.16f))
        // La hoja estampada en seco; de noche, una luciérnaga.
        val ch = Offset(w * 0.48f, h * 0.76f)
        val s = minOf(w * 0.24f, 6 * u)
        if (v.noche) {
            drawCircle(LUCIERNAGA.copy(alpha = 0.18f), s * 0.9f, ch)
            drawCircle(LUCIERNAGA.copy(alpha = 0.35f), s * 0.5f, ch)
            drawCircle(LUCIERNAGA, s * 0.22f, ch)
        } else {
            val emblema = Path().apply {
                moveTo(ch.x, ch.y + s)
                quadraticTo(ch.x - s * 0.9f, ch.y, ch.x, ch.y - s)
                quadraticTo(ch.x + s * 0.9f, ch.y, ch.x, ch.y + s); close()
            }
            drawPath(emblema, Color.Black.copy(alpha = 0.18f))
            drawLine(Color.White.copy(alpha = 0.12f), Offset(ch.x, ch.y + s), Offset(ch.x, ch.y - s * 0.8f), 0.6f * u)
        }
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h - 2 * u), Size(w, 2 * u))
    }
}

/**
 * Navidad: la repisa junto a la chimenea. Arriba, la repisa grande con su moldura, una guirnalda de abeto en festones con su
 * cordón de luces de colores, el reloj de capilla al centro entre dos velas, y la ventana de cuarterones con nieve en el
 * alféizar. Debajo, el mueble de estantes de nogal con sus costados acanalados contra un papel pintado de rayas. Abajo, el
 * reborde del que cuelgan los calcetines, rojos y verdes con su puño de lana, sobre el piso de tablas y la alfombra. Los
 * meses van en etiquetas de regalo atadas con su hilo rojo. Cada entrada es un libro de tela roja o verde con su cinta de
 * registro (más larga cuanto más escribiste), los filetes dorados, el día y el mes y una ramita de acebo; algunos van
 * envueltos como regalo, en papel con cordel y moño y el día en su etiqueta; de noche, una estrella en lugar del acebo. De
 * noche las luces se encienden, las velas arden y la ventana muestra la nieve que cae. Distinto del Ideario, que es la
 * alacena de la cocina.
 */
object EstanteNavidad : DisenoBiblioteca {
    private val PARED = Color(0xFFE9DCC0)
    private val PARED_RAYA = Color(0xFFC9B48E)
    private val NOGAL = Color(0xFF6A4528)
    private val NOGAL_LUZ = Color(0xFF8A603A)
    private val NOGAL_VETA = Color(0xFF432A17)
    private val ABETO = Color(0xFF2E5A3A)
    private val ABETO_OSC = Color(0xFF1E3F28)
    private val ROJO = Color(0xFFB3262E)
    private val VERDE = Color(0xFF2F6A45)
    private val ORO = Color(0xFFD9B04F)
    private val NIEVE = Color(0xFFF4F6FA)
    private val CIELO_DIA = Color(0xFFBFD0DD)
    private val CIELO_NOCHE = Color(0xFF1A2440)
    private val CREMA = Color(0xFFF3E8D2)
    private val CERA = Color(0xFFF2E6C8)
    private val TABLA_PISO = Color(0xFF7A5434)
    private val ALFOMBRA = Color(0xFF8E2A30)
    private val TINTA = Color(0xFF3A1E18)
    /** Las bombillas del cordón: roja, verde, amarilla, azul y rosa. */
    private val BOMBILLAS = listOf(Color(0xFFE84A3C), Color(0xFF4CC46A), Color(0xFFF2C94C), Color(0xFF4C8CE8), Color(0xFFF08CC8))
    /** Las telas de los libros: rojo, verde, rojo oscuro y verde bosque. */
    private val TELAS = listOf(Color(0xFFA3232B), Color(0xFF2E5E3C), Color(0xFF7A1C22), Color(0xFF234A30))
    /** Los papeles de regalo: estraza, blanco con lunares y burdeos. */
    private val PAPELES = listOf(Color(0xFFB98B5A), Color(0xFFE9E2D0), Color(0xFF8E2A30))
    private val CORDEL = Color(0xFFE8DCC0)
    private val NOCHE = Color(0xFF1E1A26)
    private val LLAMA = Color(0xFFFFD27A)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la penumbra de la sala. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF100C0A), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.46f)
        return r
    }

    override val corona = 0.32f
    override val base = 0.17f
    override val lado = 0.06f
    override val altoRepisa: Dp = 108.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.calida

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.78f + 0.16f * azar(semilla, 9)

    /** Una ramita de acebo: dos hojas y tres bayas. */
    private fun DrawScope.acebo(o: Offset, s: Float, hoja: Color, baya: Color) {
        listOf(-1f, 1f).forEach { lado0 ->
            rotate(lado0 * 35f, o) { drawOval(hoja, Offset(o.x - s * 0.25f + lado0 * s * 0.45f, o.y - s * 0.18f), Size(s * 0.5f, s * 0.36f)) }
        }
        drawCircle(baya, s * 0.12f, Offset(o.x - s * 0.1f, o.y - s * 0.08f))
        drawCircle(baya, s * 0.12f, Offset(o.x + s * 0.1f, o.y - s * 0.08f))
        drawCircle(baya, s * 0.12f, Offset(o.x, o.y + s * 0.1f))
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El papel pintado de rayas, con un ramito entre ellas.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(PARED), cuerpo.topLeft, cuerpo.size)
        var x = cuerpo.left + 10 * u
        var k = 0
        while (x < cuerpo.right) {
            drawLine(c(PARED_RAYA), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 1.2f * u)
            drawLine(c(PARED_RAYA), Offset(x + 3 * u, cuerpo.top), Offset(x + 3 * u, cuerpo.bottom), 0.6f * u)
            var y = cuerpo.top + 14 * u + (if (k % 2 == 0) 0f else 16 * u)
            while (y < cuerpo.bottom) { drawCircle(c(ROJO).copy(alpha = 0.35f), 1.2f * u, Offset(x + 12 * u, y)); y += 32 * u }
            x += 24 * u; k++
        }
        // Cada repisa: la sombra de la tabla de arriba.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.36f else 0.24f), rep.topLeft, Size(rep.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.1f), rep.topLeft, Size(rep.width, 14 * u))
            drawRect(Color.Black.copy(alpha = 0.14f), rep.topLeft, Size(6 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.14f), Offset(rep.right - 6 * u, rep.top), Size(6 * u, rep.height))
        }
        // Los costados de nogal, acanalados.
        listOf(d.izquierda, d.derecha).forEachIndexed { j, lad ->
            drawRect(c(NOGAL), lad.topLeft, lad.size)
            vetas(lad, c(NOGAL_VETA).copy(alpha = 0.5f), 240 + j, densidad = 1f, vertical = true, grosor = 0.6f)
            for (f in 1..3) {
                val fx = lad.left + lad.width * f / 4f
                drawLine(Color.Black.copy(alpha = 0.3f), Offset(fx, lad.top + 8 * u), Offset(fx, lad.bottom - 8 * u), 1.2f * u)
                drawLine(c(NOGAL_LUZ), Offset(fx + 1.2f * u, lad.top + 8 * u), Offset(fx + 1.2f * u, lad.bottom - 8 * u), 0.6f * u)
            }
            bisel(lad, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.2f), 1.2f * u)
        }
        // Las tablas de nogal.
        d.tablas.forEachIndexed { j, tb ->
            drawRect(c(NOGAL), tb.topLeft, tb.size)
            vetas(tb, c(NOGAL_VETA).copy(alpha = 0.5f), 250 + j, densidad = 0.7f, grosor = 0.7f)
            drawRect(c(NOGAL_LUZ), tb.topLeft, Size(tb.width, 2 * u))
            drawRect(Color.Black.copy(alpha = 0.22f), Offset(tb.left, tb.bottom), Size(tb.width, 3 * u))
        }
        // La etiqueta de regalo de cada mes, con su ojal y su hilo rojo.
        d.placas.forEach { p ->
            val r = p.r
            val e = r.height * 0.45f
            val etiqueta = Path().apply {
                moveTo(r.left + e, r.top); lineTo(r.right, r.top); lineTo(r.right, r.bottom); lineTo(r.left + e, r.bottom); lineTo(r.left, r.center.y); close()
            }
            drawPath(etiqueta, Color.Black.copy(alpha = 0.18f))
            drawPath(etiqueta, c(CREMA))
            drawPath(etiqueta, c(ROJO).copy(alpha = 0.6f), style = Stroke(0.7f * u))
            val ojal = Offset(r.left + e * 0.7f, r.center.y)
            drawCircle(c(NOGAL), 1.4f * u, ojal)
            drawLine(c(ROJO), ojal, Offset(ojal.x - e * 1.2f, r.top - 1 * u), 1 * u)
            acebo(Offset(r.right - r.height * 0.55f, r.center.y), r.height * 0.7f, c(VERDE), c(ROJO))
            textoEn(medidor, p.texto, Rect(r.left + e * 1.4f, r.top + 1 * u, r.right - r.height * 1.1f, r.bottom - 1 * u), letra, c(TINTA), 10f, peso = FontWeight.SemiBold)
        }
        corona(d.corona, oscuro, noche)
        // La base: el piso de tablas con la alfombra y el reborde con los calcetines colgados.
        val b = d.base
        val piso = b.top + b.height * 0.7f
        drawRect(c(TABLA_PISO), Offset(0f, piso), Size(w, h - piso))
        var tx = 0f
        while (tx < w) { drawLine(Color.Black.copy(alpha = 0.25f), Offset(tx, piso), Offset(tx - 8 * u, h), 0.8f * u); tx += 30 * u }
        drawRect(c(ALFOMBRA), Offset(w * 0.1f, piso + (h - piso) * 0.35f), Size(w * 0.8f, h - piso))
        drawRect(c(ORO).copy(alpha = 0.6f), Offset(w * 0.1f, piso + (h - piso) * 0.45f), Size(w * 0.8f, 1.2f * u))
        drawRect(c(PARED), Offset(b.left, b.top), Size(b.width, piso - b.top))
        drawRect(c(NOGAL), Offset(b.left, piso - 3 * u), Size(b.width, 3 * u))
        val reborde = Rect(b.left, b.top, b.right, b.top + b.height * 0.16f)
        drawRect(c(NOGAL), reborde.topLeft, reborde.size)
        vetas(reborde, c(NOGAL_VETA).copy(alpha = 0.5f), 260, densidad = 0.7f, grosor = 0.6f)
        drawRect(c(NOGAL_LUZ), reborde.topLeft, Size(reborde.width, 1.6f * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(reborde.left, reborde.bottom), Size(reborde.width, 3 * u))
        // Los calcetines, colgados de sus ganchos.
        val n = 4
        val largo = b.height * 0.62f
        for (j in 0 until n) {
            val cx = b.left + b.width * (0.2f + 0.6f * j / (n - 1f))
            val top = reborde.bottom
            val an = largo * 0.42f
            val col = if (j % 2 == 0) ROJO else VERDE
            drawLine(c(ORO), Offset(cx, reborde.bottom - 2 * u), Offset(cx, top + 2 * u), 1.2f * u)
            val calcetin = Path().apply {
                moveTo(cx - an / 2, top + largo * 0.2f); lineTo(cx + an / 2, top + largo * 0.2f)
                lineTo(cx + an / 2, top + largo * 0.7f)
                quadraticTo(cx + an / 2, top + largo, cx + an * 0.05f, top + largo)
                lineTo(cx - an * 0.9f, top + largo)
                quadraticTo(cx - an * 1.25f, top + largo * 0.82f, cx - an * 0.9f, top + largo * 0.66f)
                lineTo(cx - an / 2, top + largo * 0.6f); close()
            }
            drawPath(calcetin, c(col))
            drawRect(c(CREMA), Offset(cx - an * 0.92f, top + largo * 0.86f), Size(an * 0.3f, largo * 0.14f))
            drawRoundRect(c(CREMA), Offset(cx - an * 0.6f, top + largo * 0.06f), Size(an * 1.2f, largo * 0.18f), CornerRadius(largo * 0.06f))
            for (f in 1..3) drawLine(c(if (j % 2 == 0) VERDE else ROJO).copy(alpha = 0.7f), Offset(cx - an / 2, top + largo * (0.3f + 0.1f * f)), Offset(cx + an / 2, top + largo * (0.3f + 0.1f * f)), 1 * u)
        }
    }

    /** La repisa grande con la guirnalda, el cordón de luces, el reloj, las velas y la ventana con nieve. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(PARED), r.topLeft, r.size)
        var x = r.left + 10 * u
        while (x < r.right) { drawLine(c(PARED_RAYA), Offset(x, r.top), Offset(x, r.bottom), 1.2f * u); x += 24 * u }
        val tope = r.bottom - r.height * 0.18f
        // La ventana de cuarterones, a la derecha, con su nieve.
        val win = Rect(r.right - r.width * 0.3f, r.top + r.height * 0.08f, r.right - r.width * 0.08f, tope - r.height * 0.08f)
        drawRect(c(NOGAL), win.inflate(3 * u).topLeft, win.inflate(3 * u).size)
        drawRect(if (noche) CIELO_NOCHE else c(CIELO_DIA), win.topLeft, win.size)
        clipRect(win.left, win.top, win.right, win.bottom) {
            drawOval(c(NIEVE), Offset(win.left - win.width * 0.2f, win.bottom - win.height * 0.2f), Size(win.width * 0.9f, win.height * 0.4f))
            drawOval(c(NIEVE), Offset(win.left + win.width * 0.4f, win.bottom - win.height * 0.26f), Size(win.width * 0.9f, win.height * 0.5f))
            val copos = if (noche) 22 else 12
            for (j in 0 until copos) drawCircle(NIEVE.copy(alpha = 0.85f), (0.7f + 0.8f * azar(270, j)) * u, Offset(win.left + win.width * azar(271, j), win.top + win.height * azar(272, j) * 0.85f))
        }
        drawLine(c(NOGAL), Offset(win.center.x, win.top), Offset(win.center.x, win.bottom), 2 * u)
        drawLine(c(NOGAL), Offset(win.left, win.center.y), Offset(win.right, win.center.y), 2 * u)
        drawRect(c(NIEVE), Offset(win.left - 4 * u, win.bottom + 2 * u), Size(win.width + 8 * u, 2.4f * u))
        // La repisa grande, con su moldura.
        val repisa = Rect(r.left, tope, r.right, r.bottom)
        drawRect(c(NOGAL), repisa.topLeft, repisa.size)
        vetas(repisa, c(NOGAL_VETA).copy(alpha = 0.5f), 280, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(NOGAL_LUZ), repisa.topLeft, Size(repisa.width, 2 * u))
        drawLine(Color.Black.copy(alpha = 0.3f), Offset(repisa.left, repisa.top + repisa.height * 0.4f), Offset(repisa.right, repisa.top + repisa.height * 0.4f), 0.8f * u)
        // El reloj de capilla al centro.
        val cx = r.left + r.width * 0.42f
        val ra = r.height * 0.3f
        val caja = Path().apply {
            moveTo(cx - ra * 0.6f, tope); lineTo(cx - ra * 0.6f, tope - ra * 0.9f)
            quadraticTo(cx - ra * 0.6f, tope - ra * 1.6f, cx, tope - ra * 1.65f)
            quadraticTo(cx + ra * 0.6f, tope - ra * 1.6f, cx + ra * 0.6f, tope - ra * 0.9f)
            lineTo(cx + ra * 0.6f, tope); close()
        }
        drawPath(caja, c(NOGAL))
        drawPath(caja, c(NOGAL_VETA), style = Stroke(1 * u))
        drawRect(c(NOGAL_LUZ), Offset(cx - ra * 0.7f, tope - ra * 0.12f), Size(ra * 1.4f, ra * 0.12f))
        val esfera = Offset(cx, tope - ra * 0.95f)
        drawCircle(c(CREMA), ra * 0.4f, esfera)
        drawCircle(c(ORO), ra * 0.4f, esfera, style = Stroke(1.2f * u))
        for (j in 0 until 12) {
            val a = (j * Math.PI / 6).toFloat()
            drawCircle(c(TINTA), 0.5f * u, Offset(esfera.x + ra * 0.32f * cos(a), esfera.y + ra * 0.32f * sin(a)))
        }
        drawLine(c(TINTA), esfera, Offset(esfera.x, esfera.y - ra * 0.26f), 1.1f * u)
        drawLine(c(TINTA), esfera, Offset(esfera.x + ra * 0.18f, esfera.y + ra * 0.06f), 1.1f * u)
        // Las dos velas, apagadas de día y encendidas de noche.
        listOf(cx - ra * 1.3f, cx + ra * 1.3f).forEach { vx ->
            val alta = ra * 0.9f
            drawRect(c(ORO), Offset(vx - ra * 0.18f, tope - ra * 0.1f), Size(ra * 0.36f, ra * 0.1f))
            drawRect(c(CERA), Offset(vx - ra * 0.1f, tope - ra * 0.1f - alta), Size(ra * 0.2f, alta))
            drawLine(c(TINTA), Offset(vx, tope - ra * 0.1f - alta), Offset(vx, tope - ra * 0.18f - alta), 0.8f * u)
            if (noche) {
                drawCircle(LLAMA.copy(alpha = 0.18f), ra * 0.35f, Offset(vx, tope - ra * 0.3f - alta))
                drawOval(LLAMA, Offset(vx - ra * 0.06f, tope - ra * 0.42f - alta), Size(ra * 0.12f, ra * 0.24f))
            }
        }
        // La guirnalda de abeto en festones, con el cordón de luces: de día apagadas, de noche encendidas.
        val festones = 4
        val paso = r.width / festones
        for (j in 0 until festones) {
            val x0 = r.left + paso * j
            val caida = repisa.height * 0.9f
            for (s in 0..10) {
                val f = s / 10f
                val gx = x0 + paso * f
                val gy = tope + 2 * u + caida * 4f * f * (1f - f)
                drawCircle(c(if (s % 2 == 0) ABETO else ABETO_OSC), 3.8f * u, Offset(gx, gy))
                for (a in 0 until 3) {
                    val ang = (a * 2.1f + s).toFloat()
                    drawLine(c(ABETO_OSC), Offset(gx, gy), Offset(gx + 4.5f * u * cos(ang), gy + 4.5f * u * sin(ang)), 0.8f * u)
                }
            }
            for (s in 0 until 5) {
                val f = (s + 0.5f) / 5f
                val bo = Offset(x0 + paso * f, tope + 2 * u + caida * 4f * f * (1f - f) + 3.5f * u)
                val col = BOMBILLAS[(j * 5 + s) % BOMBILLAS.size]
                if (noche) {
                    drawCircle(col.copy(alpha = 0.15f), 6 * u, bo)
                    drawCircle(col.copy(alpha = 0.3f), 3.5f * u, bo)
                    drawCircle(col, 1.8f * u, bo)
                } else {
                    drawCircle(c(mezcla(col, Color.Black, 0.3f)), 1.8f * u, bo)
                    drawCircle(Color.White.copy(alpha = 0.3f), 0.6f * u, Offset(bo.x - 0.5f * u, bo.y - 0.5f * u))
                }
            }
            drawCircle(c(ROJO), 2.4f * u, Offset(x0, tope + 3 * u))
        }
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val envuelto = azar(v.semilla, 9) > 0.66f
        if (envuelto) {
            // El regalo: papel con su dibujo, cordel en cruz, moño y la etiqueta con el día.
            val base = PAPELES[(v.semilla and 0x7fffffff) % PAPELES.size]
            val papel = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
            drawRoundRect(papel, Offset.Zero, size, CornerRadius(1 * u))
            if (azar(v.semilla, 10) > 0.5f) {
                var yy = 3 * u
                var j = 0
                while (yy < h) { drawCircle(c(if (j % 2 == 0) ROJO else VERDE).copy(alpha = 0.6f), 1 * u, Offset(w * (if (j % 2 == 0) 0.25f else 0.75f), yy)); yy += 6 * u; j++ }
            } else {
                var yy = -w
                while (yy < h) { drawLine(c(ROJO).copy(alpha = 0.35f), Offset(0f, yy + w), Offset(w, yy), 1.2f * u); yy += 7 * u }
            }
            drawRect(Color.Black.copy(alpha = 0.18f), Offset.Zero, Size(w * 0.1f, h))
            drawRect(Color.White.copy(alpha = 0.1f), Offset(w * 0.88f, 0f), Size(w * 0.06f, h))
            // El cordel: una vuelta vertical y más vueltas cuanto más escribiste.
            val cordel = c(CORDEL)
            drawLine(cordel, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), 1.2f * u)
            val vueltas = 1 + (v.lleno * 2).toInt()
            for (j in 0 until vueltas) drawLine(cordel, Offset(0f, h * (0.34f + 0.05f * j)), Offset(w, h * (0.34f + 0.05f * j)), 1.1f * u)
            val nudo = Offset(w * 0.5f, h * 0.34f)
            val sm = minOf(w * 0.24f, 6 * u)
            drawOval(cordel, Offset(nudo.x - sm, nudo.y - sm * 0.55f), Size(sm, sm * 0.7f), style = Stroke(1.1f * u))
            drawOval(cordel, Offset(nudo.x, nudo.y - sm * 0.55f), Size(sm, sm * 0.7f), style = Stroke(1.1f * u))
            drawCircle(cordel, 1.4f * u, nudo)
            // El mes a lo largo, arriba, y la etiqueta con el día colgando del moño.
            textoEn(medidor, v.mes, Rect(w * 0.08f, h * 0.04f, w * 0.44f, h * 0.3f), letra, c(TINTA), 7f, vertical = true, minimo = 4f)
            val et = Rect(w * 0.14f, h * 0.48f, w * 0.86f, h * 0.7f)
            drawLine(cordel, nudo, Offset(et.center.x, et.top), 0.7f * u)
            drawRect(c(CREMA), et.topLeft, et.size)
            drawRect(c(ROJO).copy(alpha = 0.6f), et.topLeft, et.size, style = Stroke(0.6f * u))
            textoEn(medidor, v.dia, et.deflate(1.5f * u), letra, c(TINTA), 12f, peso = FontWeight.SemiBold)
            // Abajo, una ramita de acebo; de noche, una estrella.
            val ca = Offset(w * 0.5f, h * 0.84f)
            val sa = minOf(w * 0.4f, 9 * u)
            if (v.noche) drawPath(estrella(ca, sa * 0.4f, 5, 0.45f), c(ORO)) else acebo(ca, sa, c(VERDE), c(ROJO))
        } else {
            // El libro de tela, con su trama, sus filetes dorados y la cinta de registro.
            val base = TELAS[(v.semilla and 0x7fffffff) % TELAS.size]
            val tela = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
            val oro = c(ORO)
            drawRoundRect(tela, Offset.Zero, size, CornerRadius(1.5f * u))
            var yy = 0f
            while (yy < h) { drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, yy), Offset(w, yy), 0.5f * u); yy += 2.2f * u }
            drawRect(Color.Black.copy(alpha = 0.24f), Offset.Zero, Size(w * 0.1f, h))
            drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.26f, 0f), Size(w * 0.12f, h))
            listOf(0.08f, 0.92f).forEach { f ->
                drawLine(oro, Offset(w * 0.1f, h * f), Offset(w * 0.9f, h * f), 0.7f * u)
                drawLine(oro, Offset(w * 0.1f, h * f + (if (f < 0.5f) 1.6f else -1.6f) * u), Offset(w * 0.9f, h * f + (if (f < 0.5f) 1.6f else -1.6f) * u), 0.5f * u)
            }
            // El recuadro dorado con el día y el mes a lo largo.
            val rec = Rect(w * 0.14f, h * 0.14f, w * 0.86f, h * 0.32f)
            drawRect(oro, rec.topLeft, rec.size, style = Stroke(0.7f * u))
            textoEn(medidor, v.dia, rec.deflate(1.5f * u), letra, oro, 13f, peso = FontWeight.SemiBold)
            textoEn(medidor, v.mes, Rect(w * 0.18f, h * 0.36f, w * 0.82f, h * 0.7f), letra, oro, 8f, vertical = true, minimo = 4f)
            // La ramita de acebo; de noche, una estrella.
            val ca = Offset(w * 0.5f, h * 0.8f)
            val sa = minOf(w * 0.5f, 10 * u)
            if (v.noche) drawPath(estrella(ca, sa * 0.42f, 5, 0.45f), oro) else acebo(ca, sa, c(mezcla(VERDE, Color.White, 0.15f)), c(ROJO))
            // La cinta de registro que sale por la cabeza: más larga cuanto más escribiste.
            val cinta = c(if (base == TELAS[0] || base == TELAS[2]) ORO else ROJO)
            val lc = h * (0.06f + 0.14f * v.lleno)
            val cw = minOf(w * 0.16f, 3.5f * u)
            val cx = w * 0.72f
            val tira = Path().apply {
                moveTo(cx - cw / 2, 0f); lineTo(cx + cw / 2, 0f); lineTo(cx + cw / 2, lc); lineTo(cx, lc - cw * 0.6f); lineTo(cx - cw / 2, lc); close()
            }
            drawPath(tira, cinta)
            drawRect(Color.Black.copy(alpha = 0.2f), Offset(cx + cw * 0.2f, 0f), Size(cw * 0.3f, lc - cw * 0.3f))
        }
    }
}

/**
 * Abismo: el armario de cartas del batiscafo. Un mueble de teca barnizada con cantoneras de latón, atornillado al casco de
 * planchas color crema con sus costuras remachadas; cada tabla lleva su barandilla de latón para que nada se caiga con el
 * balanceo. Arriba, el casco curvo con el gran ojo de buey de latón y sus pernos, por el que se ven las criaturas del fondo
 * (un pez abisal con su señuelo, una medusa y la nieve marina), la lámpara enjaulada de la cabina y el profundímetro con su
 * contador. Abajo, el zócalo de teca con su placa de latón y una alfombra de goma de botones. Los meses van en cinta de
 * rotular de letras en relieve. Cada entrada es una bitácora de buceo de goma amarilla con su pinza de metal arriba, la
 * placa remachada con el día, el mes a lo largo, las hojas de la libreta asomando por el canto (más cuanto más escribiste)
 * y la bandera de buceo; de noche, una medusa luminosa en lugar de la bandera. De noche la cabina se apaga y todo lo
 * alumbra la bioluminiscencia. Distinto del Ideario, que es el rack estanco de acero.
 */
object EstanteAbismo : DisenoBiblioteca {
    private val TECA = Color(0xFF7A4E2C)
    private val TECA_LUZ = Color(0xFF9A6A3E)
    private val TECA_VETA = Color(0xFF4E301A)
    private val CASCO = Color(0xFFD8D2C0)
    private val CASCO_SOMBRA = Color(0xFFADA690)
    private val REMACHE = Color(0xFF8E8874)
    private val LATON = Color(0xFFC09A48)
    private val LATON_LUZ = Color(0xFFE2C27E)
    private val LATON_OSC = Color(0xFF7E6128)
    private val AGUA = Color(0xFF0C2338)
    private val AGUA_HONDA = Color(0xFF061426)
    private val SILUETA = Color(0xFF020A14)
    private val BIOLUZ = Color(0xFF5FF2E0)
    private val BIOLUZ_VERDE = Color(0xFF9CF27A)
    private val GOMA = Color(0xFF26292B)
    private val ALUMINIO = Color(0xFFB9BEC2)
    private val TINTA = Color(0xFF1B1E21)
    private val CINTA = Color(0xFF22252A)
    private val ROJO = Color(0xFFC7302A)
    private val LUZ_CABINA = Color(0xFFF2D28A)
    /** La goma de las bitácoras: amarillo, naranja, amarillo limón, ocre y amarillo verdoso. */
    private val GOMAS = listOf(Color(0xFFE8C21E), Color(0xFFF0A21C), Color(0xFFD9D02A), Color(0xFFE0B43A), Color(0xFFC9C23A))
    private val NOCHE = Color(0xFF0A1424)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, con la cabina apagada en el agua honda. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF07090B), 0.36f)
        if (noche) r = mezcla(r, NOCHE, 0.5f)
        return r
    }

    override val corona = 0.34f
    override val base = 0.1f
    override val lado = 0.06f
    override val altoRepisa: Dp = 106.dp
    override val tabla: Dp = 18.dp
    override val separa: Dp = 3.dp
    override val letra: FontFamily = LetraExtra.batiscafo

    override fun ancho(palabras: Int): Dp = (24 + (palabras / 50).coerceAtMost(16)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.14f * azar(semilla, 11)

    /** Las planchas del casco: costuras con sus filas de remaches. */
    private fun DrawScope.planchas(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        drawRect(t(CASCO, oscuro, noche), r.topLeft, r.size)
        drawRect(Color.Black.copy(alpha = 0.08f), r.topLeft, Size(r.width * 0.12f, r.height))
        drawRect(Color.Black.copy(alpha = 0.08f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
        var y = r.top + 60 * u
        while (y < r.bottom) {
            drawLine(t(CASCO_SOMBRA, oscuro, noche), Offset(r.left, y), Offset(r.right, y), 1.2f * u)
            var rx = r.left + 4 * u
            while (rx < r.right) { drawCircle(t(REMACHE, oscuro, noche), 1.1f * u, Offset(rx, y - 3.5f * u)); rx += 10 * u }
            y += 60 * u
        }
        var x = r.left + r.width * 0.5f
        while (x < r.right) { drawLine(t(CASCO_SOMBRA, oscuro, noche), Offset(x, r.top), Offset(x, r.bottom), 1.2f * u); x += r.width * 0.5f }
    }

    /** Un punto de bioluminiscencia con su halo en anillos (sin degradados). */
    private fun DrawScope.destello(o: Offset, r: Float, color: Color) {
        drawCircle(color.copy(alpha = 0.1f), r * 3.5f, o)
        drawCircle(color.copy(alpha = 0.22f), r * 2f, o)
        drawCircle(color, r, o)
    }

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El casco del fondo, con sus planchas remachadas.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        planchas(cuerpo, oscuro, noche)
        // De noche, el reflejo cian de la bioluminiscencia que entra por el ojo de buey.
        if (noche) {
            val alcanceLuz = minOf(cuerpo.height, 220 * u)
            for (k in 1..3) drawRect(BIOLUZ.copy(alpha = 0.03f), cuerpo.topLeft, Size(cuerpo.width * (0.4f + 0.2f * k), alcanceLuz * k / 3f))
        }
        // Cada repisa: la sombra de la tabla de arriba y de los costados.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.26f), rep.topLeft, Size(rep.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.12f), rep.topLeft, Size(rep.width, 14 * u))
            drawRect(Color.Black.copy(alpha = 0.16f), rep.topLeft, Size(7 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(rep.right - 7 * u, rep.top), Size(7 * u, rep.height))
        }
        // Los costados de teca, con cantoneras de latón y los pernos que los sujetan al casco.
        listOf(d.izquierda, d.derecha).forEachIndexed { j, lad ->
            drawRoundRect(c(TECA), lad.topLeft, lad.size, CornerRadius(3 * u))
            vetas(lad, c(TECA_VETA).copy(alpha = 0.55f), 300 + j, densidad = 1f, vertical = true, grosor = 0.6f)
            drawRect(c(TECA_LUZ), Offset(lad.left + lad.width * 0.15f, lad.top), Size(lad.width * 0.08f, lad.height))
            var py = lad.top + 24 * u
            while (py < lad.bottom - 10 * u) {
                drawCircle(c(LATON), 2 * u, Offset(lad.center.x, py))
                drawLine(c(LATON_OSC), Offset(lad.center.x - 1.4f * u, py), Offset(lad.center.x + 1.4f * u, py), 0.6f * u)
                py += 70 * u
            }
            bisel(lad, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
        }
        // Las tablas de teca, con su barandilla de latón sobre postes.
        d.tablas.forEachIndexed { k, tb ->
            drawRect(c(TECA), tb.topLeft, tb.size)
            vetas(tb, c(TECA_VETA).copy(alpha = 0.5f), 310 + k, densidad = 0.7f, grosor = 0.7f)
            drawRect(c(TECA_LUZ), tb.topLeft, Size(tb.width, 1.6f * u))
            drawRect(Color.Black.copy(alpha = 0.28f), Offset(tb.left, tb.bottom), Size(tb.width, 3 * u))
            val ry = tb.top + tb.height * 0.28f
            drawLine(c(LATON), Offset(tb.left, ry), Offset(tb.right, ry), 2 * u)
            drawLine(c(LATON_LUZ), Offset(tb.left, ry - 0.6f * u), Offset(tb.right, ry - 0.6f * u), 0.6f * u)
            var px = tb.left + 20 * u
            while (px < tb.right - 10 * u) { drawRect(c(LATON_OSC), Offset(px - 1.2f * u, ry), Size(2.4f * u, tb.height * 0.4f)); px += 60 * u }
        }
        // La cinta de rotular de cada mes, con las letras en relieve.
        d.placas.forEach { p ->
            val r = Rect(p.r.left, p.r.top + p.r.height * 0.36f, p.r.right, p.r.bottom)
            drawRect(Color.Black.copy(alpha = 0.3f), Offset(r.left + 1 * u, r.top + 1 * u), r.size)
            drawRect(c(CINTA), r.topLeft, r.size)
            drawRect(Color.White.copy(alpha = 0.1f), r.topLeft, Size(r.width, 1 * u))
            textoEn(medidor, p.texto, Rect(r.left + 4 * u, r.top, r.right - 4 * u, r.bottom), letra, c(CASCO), 9f, peso = FontWeight.SemiBold, espaciado = 1.4f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de teca con su placa de latón, sobre la alfombra de goma de botones.
        val b = d.base
        val piso = b.top + b.height * 0.6f
        drawRect(c(GOMA), Offset(0f, piso), Size(w, h - piso))
        var gy = piso + 4 * u; var fila = 0
        while (gy < h) {
            var gx = if (fila % 2 == 0) 4 * u else 9 * u
            while (gx < w) { drawCircle(Color.White.copy(alpha = 0.08f), 2 * u, Offset(gx, gy)); drawCircle(Color.Black.copy(alpha = 0.3f), 2 * u, Offset(gx + 0.6f * u, gy + 0.6f * u), style = Stroke(0.5f * u)); gx += 10 * u }
            gy += 6 * u; fila++
        }
        if (noche) for (k in 0 until 5) destello(Offset(w * (0.1f + 0.8f * azar(320, k)), piso + (h - piso) * azar(321, k)), 0.8f * u, BIOLUZ)
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(TECA), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(TECA_VETA).copy(alpha = 0.5f), 330, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(LATON), Offset(zocalo.left, zocalo.bottom - zocalo.height * 0.3f), Size(zocalo.width, zocalo.height * 0.3f))
        drawRect(c(LATON_LUZ), Offset(zocalo.left, zocalo.bottom - zocalo.height * 0.3f), Size(zocalo.width, 1 * u))
        var bx = zocalo.left + 8 * u
        while (bx < zocalo.right) { drawCircle(c(LATON_OSC), 1 * u, Offset(bx, zocalo.bottom - zocalo.height * 0.15f)); bx += 22 * u }
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** El casco curvo con el ojo de buey y sus criaturas, la lámpara de la cabina y el profundímetro. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        planchas(r, oscuro, noche)
        // La curva del techo del casco.
        val techo = Path().apply { moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + r.height * 0.2f); quadraticTo(r.center.x, r.top - r.height * 0.08f, r.left, r.top + r.height * 0.2f); close() }
        drawPath(techo, c(CASCO_SOMBRA))
        drawPath(techo, Color.Black.copy(alpha = 0.15f))
        // El ojo de buey: el agua honda, la nieve marina, el pez abisal y la medusa.
        val co = Offset(r.left + r.width * 0.32f, r.top + r.height * 0.54f)
        val ro = r.height * 0.34f
        val vidrio = Path().apply { addOval(Rect(co, ro)) }
        drawPath(vidrio, if (noche) AGUA_HONDA else AGUA)
        clipPath(vidrio) {
            if (!noche) {
                // El haz del reflector, de día, alumbra la nieve marina.
                val haz = Path().apply { moveTo(co.x - ro, co.y - ro * 0.2f); lineTo(co.x + ro, co.y - ro * 0.9f); lineTo(co.x + ro, co.y + ro * 0.5f); close() }
                drawPath(haz, Color.White.copy(alpha = 0.08f))
            }
            for (k in 0 until 30) {
                val o = Offset(co.x - ro + 2 * ro * azar(340, k), co.y - ro + 2 * ro * azar(341, k))
                if (noche && k % 3 == 0) destello(o, 0.7f * u, if (k % 2 == 0) BIOLUZ else BIOLUZ_VERDE)
                else drawCircle(Color.White.copy(alpha = if (noche) 0.12f else 0.35f), (0.4f + 0.6f * azar(342, k)) * u, o)
            }
            // El pez abisal: cuerpo, cola, la mandíbula con dientes y su señuelo luminoso.
            val pz = Offset(co.x - ro * 0.15f, co.y + ro * 0.25f)
            val pr = ro * 0.36f
            drawOval(SILUETA, Offset(pz.x - pr, pz.y - pr * 0.6f), Size(pr * 2, pr * 1.2f))
            val cola = Path().apply { moveTo(pz.x + pr * 0.8f, pz.y); lineTo(pz.x + pr * 1.5f, pz.y - pr * 0.5f); lineTo(pz.x + pr * 1.4f, pz.y + pr * 0.5f); close() }
            drawPath(cola, SILUETA)
            val boca = Path().apply { moveTo(pz.x - pr, pz.y - pr * 0.05f); lineTo(pz.x - pr * 0.45f, pz.y + pr * 0.05f); lineTo(pz.x - pr * 0.95f, pz.y + pr * 0.35f); close() }
            drawPath(boca, Color.Black)
            for (j in 0 until 4) drawLine(Color.White.copy(alpha = if (noche) 0.35f else 0.6f), Offset(pz.x - pr * (0.92f - 0.12f * j), pz.y), Offset(pz.x - pr * (0.88f - 0.12f * j), pz.y + pr * 0.12f), 0.6f * u)
            drawCircle(Color.White.copy(alpha = 0.6f), pr * 0.08f, Offset(pz.x - pr * 0.5f, pz.y - pr * 0.25f))
            val sen = Offset(pz.x - pr * 1.3f, pz.y - pr * 0.9f)
            val cana = Path().apply { moveTo(pz.x - pr * 0.4f, pz.y - pr * 0.55f); quadraticTo(pz.x - pr * 0.6f, pz.y - pr * 1.3f, sen.x, sen.y) }
            drawPath(cana, SILUETA, style = Stroke(1 * u))
            if (noche) mancha(sen, ro * 0.4f, BIOLUZ.copy(alpha = 0.6f))
            drawCircle(if (noche) BIOLUZ else BIOLUZ.copy(alpha = 0.6f), pr * 0.12f, sen)
            // La medusa arriba a la derecha.
            val md = Offset(co.x + ro * 0.45f, co.y - ro * 0.4f)
            val mr = ro * 0.2f
            val colMedusa = if (noche) BIOLUZ_VERDE else Color.White.copy(alpha = 0.4f)
            drawArc(colMedusa.copy(alpha = if (noche) 0.5f else 0.3f), 180f, 180f, true, Offset(md.x - mr, md.y - mr), Size(mr * 2, mr * 2))
            drawArc(colMedusa, 180f, 180f, false, Offset(md.x - mr, md.y - mr), Size(mr * 2, mr * 2), style = Stroke(0.8f * u))
            for (j in 0 until 4) {
                val tx = md.x - mr * 0.7f + mr * 0.46f * j
                val tentaculo = Path().apply { moveTo(tx, md.y); quadraticTo(tx - mr * 0.3f, md.y + mr * 0.8f, tx + mr * 0.1f, md.y + mr * 1.6f) }
                drawPath(tentaculo, colMedusa.copy(alpha = 0.7f), style = Stroke(0.6f * u))
            }
            drawLine(Color.White.copy(alpha = 0.1f), Offset(co.x - ro * 0.8f, co.y + ro * 0.4f), Offset(co.x - ro * 0.2f, co.y - ro * 0.9f), 4 * u)
        }
        // El anillo de latón y sus pernos.
        drawCircle(c(LATON), ro + 4 * u, co, style = Stroke(8 * u))
        drawCircle(c(LATON_LUZ), ro + 7.5f * u, co, style = Stroke(1 * u))
        drawCircle(c(LATON_OSC), ro, co, style = Stroke(1.2f * u))
        if (noche) drawCircle(BIOLUZ.copy(alpha = 0.2f), ro + 4 * u, co, style = Stroke(2 * u))
        for (k in 0 until 10) {
            val a = (k * Math.PI / 5).toFloat()
            drawCircle(c(LATON_OSC), 1.8f * u, Offset(co.x + (ro + 4 * u) * cos(a), co.y + (ro + 4 * u) * sin(a)))
        }
        // La lámpara enjaulada de la cabina: de día encendida, de noche apagada.
        val lc = Offset(r.left + r.width * 0.6f, r.top + r.height * 0.3f)
        val lr = r.height * 0.07f
        drawRect(c(LATON_OSC), Offset(lc.x - lr * 1.2f, lc.y - lr * 1.3f), Size(lr * 2.4f, lr * 0.4f))
        drawCircle(if (noche) c(CASCO_SOMBRA) else LUZ_CABINA, lr, lc)
        if (!noche) drawCircle(LUZ_CABINA.copy(alpha = 0.2f), lr * 2.4f, lc)
        for (j in -1..1) drawLine(c(LATON_OSC), Offset(lc.x + j * lr * 0.6f, lc.y - lr), Offset(lc.x + j * lr * 0.6f, lc.y + lr), 0.8f * u)
        drawLine(c(LATON_OSC), Offset(lc.x - lr, lc.y), Offset(lc.x + lr, lc.y), 0.8f * u)
        // El profundímetro con su contador de profundidad (cifras como casillas, no letras).
        val cp = Offset(r.right - r.width * 0.2f, r.top + r.height * 0.46f)
        val rp = r.height * 0.22f
        drawCircle(Color.Black.copy(alpha = 0.2f), rp + 3 * u, Offset(cp.x + 1.5f * u, cp.y + 2 * u))
        manometro(cp, rp, c(LATON), c(CASCO), c(ROJO), if (noche) 0.88f else 0.74f)
        drawCircle(c(LATON_OSC), rp, cp, style = Stroke(1 * u))
        val contador = Rect(cp.x - rp * 0.9f, cp.y + rp + 4 * u, cp.x + rp * 0.9f, cp.y + rp + 4 * u + r.height * 0.1f)
        drawRect(c(TINTA), contador.topLeft, contador.size)
        for (j in 0 until 4) {
            val cw = contador.width / 4
            drawRect(c(ALUMINIO), Offset(contador.left + cw * j + 1 * u, contador.top + 1 * u), Size(cw - 2 * u, contador.height - 2 * u))
            drawRect(c(TINTA), Offset(contador.left + cw * j + cw * 0.35f, contador.top + contador.height * 0.25f), Size(cw * 0.3f, contador.height * 0.5f))
        }
        drawRect(c(LATON), contador.inflate(1 * u).topLeft, contador.inflate(1 * u).size, style = Stroke(1 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val base = GOMAS[(v.semilla and 0x7fffffff) % GOMAS.size]
        val goma = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val arriba = h * 0.05f
        // La tapa de goma amarilla, con sus esquinas redondeadas y su textura de puntos.
        drawRoundRect(goma, Offset(0f, arriba), Size(w, h - arriba), CornerRadius(4 * u))
        var py = arriba + 3 * u
        while (py < h - 2 * u) {
            var px = 3 * u
            while (px < w - 2 * u) { drawCircle(Color.Black.copy(alpha = 0.08f), 0.5f * u, Offset(px, py)); px += 3.5f * u }
            py += 3.5f * u
        }
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(0f, arriba + 3 * u), Size(w * 0.08f, h - arriba - 6 * u))
        // Las hojas de la libreta que asoman por el canto: más cuanto más escribiste.
        val hojas = 1 + (v.lleno * 4).toInt()
        for (j in 0 until hojas) drawLine(c(ALUMINIO).copy(alpha = 0.9f), Offset(w - 1 * u - j * 1.3f * u, arriba + 6 * u), Offset(w - 1 * u - j * 1.3f * u, h - 6 * u), 0.7f * u)
        // La pinza de metal arriba, con su resorte.
        val pinza = Rect(w * 0.24f, 0f, w * 0.76f, h * 0.11f)
        drawRoundRect(c(ALUMINIO), pinza.topLeft, pinza.size, CornerRadius(1.5f * u))
        drawRect(Color.White.copy(alpha = 0.3f), pinza.topLeft, Size(pinza.width, 1 * u))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(pinza.left, pinza.bottom - 1.2f * u), Size(pinza.width, 1.2f * u))
        drawCircle(c(TINTA), minOf(pinza.height * 0.22f, 2 * u), Offset(pinza.center.x, pinza.top + pinza.height * 0.4f))
        // La placa remachada con el día.
        val placa = Rect(w * 0.14f, h * 0.18f, w * 0.86f, h * 0.38f)
        drawRect(c(ALUMINIO), placa.topLeft, placa.size)
        drawRect(Color.White.copy(alpha = 0.3f), placa.topLeft, Size(placa.width, 1 * u))
        drawRect(c(TINTA).copy(alpha = 0.4f), placa.topLeft, placa.size, style = Stroke(0.6f * u))
        val rr = minOf(1.1f * u, placa.width * 0.06f)
        listOf(placa.left + rr * 2, placa.right - rr * 2).forEach { x -> drawCircle(c(TINTA).copy(alpha = 0.5f), rr, Offset(x, placa.center.y)) }
        textoEn(medidor, v.dia, Rect(placa.left + rr * 3.5f, placa.top + 1 * u, placa.right - rr * 3.5f, placa.bottom - 1 * u), letra, c(TINTA), 13f, peso = FontWeight.Bold)
        // El mes a lo largo.
        textoEn(medidor, v.mes, Rect(w * 0.18f, h * 0.42f, w * 0.82f, h * 0.76f), letra, c(TINTA), 8f, vertical = true, minimo = 4f, peso = FontWeight.SemiBold)
        // La bandera de buceo; de noche, una medusa luminosa.
        val bc = Offset(w * 0.5f, h * 0.86f)
        val bs = minOf(w * 0.3f, 7 * u)
        if (v.noche) {
            destello(Offset(bc.x, bc.y - bs * 0.2f), bs * 0.18f, BIOLUZ)
            drawArc(BIOLUZ, 180f, 180f, false, Offset(bc.x - bs * 0.4f, bc.y - bs * 0.5f), Size(bs * 0.8f, bs * 0.6f), style = Stroke(0.8f * u))
            for (j in 0 until 3) drawLine(BIOLUZ.copy(alpha = 0.7f), Offset(bc.x - bs * 0.25f + bs * 0.25f * j, bc.y - bs * 0.2f), Offset(bc.x - bs * 0.25f + bs * 0.25f * j, bc.y + bs * 0.4f), 0.6f * u)
        } else {
            val bandera = Rect(bc.x - bs * 0.6f, bc.y - bs * 0.4f, bc.x + bs * 0.6f, bc.y + bs * 0.4f)
            drawRect(c(ROJO), bandera.topLeft, bandera.size)
            val diagonal = Path().apply {
                moveTo(bandera.left, bandera.top); lineTo(bandera.left + bandera.width * 0.22f, bandera.top)
                lineTo(bandera.right, bandera.bottom); lineTo(bandera.right - bandera.width * 0.22f, bandera.bottom); close()
            }
            drawPath(diagonal, c(Color.White))
        }
    }
}

/**
 * URSS: la biblioteca de la casa de cultura. Un estante de nogal pulido, de líneas sobrias, con costados de moldura
 * escalonada y un fondo de contrachapado. En la pared, encima, la banderola roja colgada de su barra, con su fleco dorado,
 * tres estrellas y bandas geométricas en lugar de lemas; sobre el mueble, la radio de mesa de madera con su rejilla de tela,
 * el dial y dos perillas de baquelita, y la lámpara de escritorio de pantalla verde. Abajo, el zócalo y un linóleo con su
 * pasillo de alfombra roja. Los meses van en tiras de esmalte rojo atornilladas. Cada entrada es un libro de tapa de tela con
 * la estrella estampada, el mes a lo largo, el sello violeta de la biblioteca, la ficha de préstamo asomando de su bolsillo
 * por arriba (con más sellos de fecha cuanto más escribiste) y el día como signatura en el tejuelo; de noche, una luna en
 * lugar del sello. De noche se enciende la lámpara verde y alumbra el estante, y el dial de la radio brilla. Distinto del
 * Ideario, que es la kartoteka metálica de cajoncitos.
 */
object EstanteUrss : DisenoBiblioteca {
    private val NOGAL = Color(0xFF5B3A26)
    private val NOGAL_LUZ = Color(0xFF7C5236)
    private val NOGAL_VETA = Color(0xFF3A2416)
    private val FONDO = Color(0xFFC9A777)
    private val PARED = Color(0xFFD8CCAE)
    private val ROJO = Color(0xFFB0241E)
    private val ROJO_OSC = Color(0xFF7E1914)
    private val ORO = Color(0xFFD9A93E)
    private val CREMA = Color(0xFFEFE4C9)
    private val RADIO = Color(0xFF6E4A2C)
    private val TELA_RADIO = Color(0xFFBFAE88)
    private val BAQUELITA = Color(0xFF2A2420)
    private val PANTALLA = Color(0xFF2E6B4A)
    private val PANTALLA_LUZ = Color(0xFF5FA07A)
    private val LATON = Color(0xFFC4A052)
    private val LINOLEO = Color(0xFF7A5A3E)
    private val ALFOMBRA = Color(0xFF8E2A24)
    private val ALFOMBRA_ORLA = Color(0xFF2E4E36)
    private val TINTA = Color(0xFF1E1C1A)
    private val VIOLETA = Color(0xFF6A4FA0)
    /** Las telas de las tapas: ladrillo, gris azulado, oliva, ocre oscuro, verde oscuro y castaño. */
    private val TELAS = listOf(Color(0xFF8A3A2E), Color(0xFF4A5A6E), Color(0xFF5E6440), Color(0xFF7E5E26), Color(0xFF2E4E3E), Color(0xFF6E4E3A))
    private val NOCHE = Color(0xFF161A1C)
    private val LUZ = Color(0xFFF6E2A0)
    private val LUNA = Color(0xFFF1E9CC)

    /** El color con la luz del momento: en modo oscuro, más hundido; de noche, en la sala a oscuras. */
    private fun t(c: Color, oscuro: Boolean, noche: Boolean): Color {
        var r = c
        if (oscuro) r = mezcla(r, Color(0xFF0D0C0B), 0.34f)
        if (noche) r = mezcla(r, NOCHE, 0.48f)
        return r
    }

    override val corona = 0.34f
    override val base = 0.1f
    override val lado = 0.055f
    override val altoRepisa: Dp = 112.dp
    override val tabla: Dp = 16.dp
    override val separa: Dp = 2.dp
    override val letra: FontFamily = LetraExtra.russo

    override fun ancho(palabras: Int): Dp = (20 + (palabras / 45).coerceAtMost(18)).dp
    override fun alto(semilla: Int, palabras: Int): Float = 0.8f + 0.15f * azar(semilla, 12)

    override fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, noche)
        // El fondo de contrachapado, con sus juntas.
        val cuerpo = Rect(d.izquierda.right, d.corona.bottom, d.derecha.left, d.base.top)
        drawRect(c(FONDO), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, c(NOGAL_LUZ).copy(alpha = 0.3f), 400, densidad = 0.5f, vertical = true, grosor = 0.6f, ondulacion = 2f)
        listOf(0.33f, 0.66f).forEach { f -> drawLine(c(NOGAL_VETA).copy(alpha = 0.5f), Offset(cuerpo.left + cuerpo.width * f, cuerpo.top), Offset(cuerpo.left + cuerpo.width * f, cuerpo.bottom), 0.8f * u) }
        // Cada repisa: la sombra de la tabla de arriba.
        d.repisas.forEach { rep ->
            drawRect(Color.Black.copy(alpha = if (noche) 0.4f else 0.26f), rep.topLeft, Size(rep.width, 7 * u))
            drawRect(Color.Black.copy(alpha = 0.12f), rep.topLeft, Size(rep.width, 14 * u))
            drawRect(Color.Black.copy(alpha = 0.16f), rep.topLeft, Size(6 * u, rep.height))
            drawRect(Color.Black.copy(alpha = 0.16f), Offset(rep.right - 6 * u, rep.top), Size(6 * u, rep.height))
        }
        // De noche, la luz de la lámpara verde baja sobre la primera repisa.
        if (noche) {
            val cl = d.corona.left + d.corona.width * 0.76f
            val fin = minOf(cuerpo.bottom, cuerpo.top + 180 * u)
            val cono = Path().apply { moveTo(cl - d.corona.width * 0.08f, cuerpo.top); lineTo(cl + d.corona.width * 0.08f, cuerpo.top); lineTo(cl + d.corona.width * 0.3f, fin); lineTo(cl - d.corona.width * 0.3f, fin); close() }
            drawPath(cono, Brush.verticalGradient(listOf(LUZ.copy(alpha = 0.22f), Color.Transparent), cuerpo.top, fin))
        }
        // Los costados de nogal pulido, con su moldura escalonada.
        listOf(d.izquierda to true, d.derecha to false).forEach { (lad, izq) ->
            drawRect(c(NOGAL), lad.topLeft, lad.size)
            vetas(lad, c(NOGAL_VETA).copy(alpha = 0.5f), if (izq) 410 else 411, densidad = 1f, vertical = true, grosor = 0.6f)
            val e = lad.width * 0.18f
            val x1 = if (izq) lad.left + e else lad.right - e
            val x2 = if (izq) lad.left + e * 2 else lad.right - e * 2
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(x1, lad.top), Offset(x1, lad.bottom), 1 * u)
            drawLine(c(NOGAL_LUZ), Offset(x2, lad.top), Offset(x2, lad.bottom), 1 * u)
            drawRect(Color.White.copy(alpha = 0.08f), Offset(lad.left + lad.width * 0.55f, lad.top), Size(lad.width * 0.08f, lad.height))
            bisel(lad, Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.22f), 1.2f * u)
        }
        // Las tablas de nogal.
        d.tablas.forEachIndexed { k, tb ->
            drawRect(c(NOGAL), tb.topLeft, tb.size)
            vetas(tb, c(NOGAL_VETA).copy(alpha = 0.5f), 420 + k, densidad = 0.7f, grosor = 0.7f)
            drawRect(c(NOGAL_LUZ), tb.topLeft, Size(tb.width, 2 * u))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(tb.left, tb.top + tb.height * 0.55f), Offset(tb.right, tb.top + tb.height * 0.55f), 0.6f * u)
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(tb.left, tb.bottom), Size(tb.width, 3 * u))
        }
        // La tira de esmalte rojo de cada mes, atornillada.
        d.placas.forEach { p ->
            val r = p.r
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(r.left + 1 * u, r.top + 1 * u), r.size)
            drawRoundRect(c(ROJO), r.topLeft, r.size, CornerRadius(1.5f * u))
            drawRoundRect(c(CREMA).copy(alpha = 0.7f), r.deflate(1.4f * u).topLeft, r.deflate(1.4f * u).size, CornerRadius(1 * u), style = Stroke(0.6f * u))
            listOf(r.left + 4 * u, r.right - 4 * u).forEach { x -> drawCircle(c(LATON), 1.2f * u, Offset(x, r.center.y)); drawLine(c(ROJO_OSC), Offset(x - 0.9f * u, r.center.y), Offset(x + 0.9f * u, r.center.y), 0.5f * u) }
            textoEn(medidor, p.texto, Rect(r.left + 8 * u, r.top + 1 * u, r.right - 8 * u, r.bottom - 1 * u), letra, c(CREMA), 10f, espaciado = 1f)
        }
        corona(d.corona, oscuro, noche)
        // La base: el zócalo de nogal, el linóleo y el pasillo de alfombra roja con su orla.
        val b = d.base
        val piso = b.top + b.height * 0.58f
        drawRect(c(LINOLEO), Offset(0f, piso), Size(w, h - piso))
        val pasillo = Rect(w * 0.12f, piso + (h - piso) * 0.2f, w * 0.88f, h)
        drawRect(c(ALFOMBRA), pasillo.topLeft, pasillo.size)
        drawRect(c(ALFOMBRA_ORLA), pasillo.topLeft, Size(pasillo.width, (h - piso) * 0.14f))
        drawRect(c(ALFOMBRA_ORLA), pasillo.topLeft, Size(pasillo.width * 0.04f, pasillo.height))
        drawRect(c(ALFOMBRA_ORLA), Offset(pasillo.right - pasillo.width * 0.04f, pasillo.top), Size(pasillo.width * 0.04f, pasillo.height))
        val zocalo = Rect(b.left, b.top, b.right, piso)
        drawRect(c(NOGAL), zocalo.topLeft, zocalo.size)
        vetas(zocalo, c(NOGAL_VETA).copy(alpha = 0.5f), 430, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(NOGAL_LUZ), zocalo.topLeft, Size(zocalo.width, 2 * u))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(zocalo.left, zocalo.top + zocalo.height * 0.35f), Size(zocalo.width, 1.2f * u))
        drawRect(Color.Black.copy(alpha = 0.3f), Offset(zocalo.left, piso), Size(zocalo.width, 3 * u))
    }

    /** La banderola roja con sus estrellas, la radio de mesa y la lámpara de pantalla verde. */
    private fun DrawScope.corona(r: Rect, oscuro: Boolean, noche: Boolean) {
        val u = density
        fun c(x: Color) = t(x, oscuro, noche)
        drawRect(c(PARED), r.topLeft, r.size)
        // La barra y la banderola, con sus pliegues, sus estrellas, sus bandas y el fleco.
        val barraY = r.top + r.height * 0.07f
        drawLine(c(LATON), Offset(r.left + r.width * 0.06f, barraY), Offset(r.right - r.width * 0.06f, barraY), 2 * u)
        listOf(r.left + r.width * 0.06f, r.right - r.width * 0.06f).forEach { x -> drawCircle(c(LATON), 2.6f * u, Offset(x, barraY)) }
        val bandera = Rect(r.left + r.width * 0.1f, barraY, r.right - r.width * 0.1f, r.top + r.height * 0.44f)
        drawRect(c(ROJO), bandera.topLeft, bandera.size)
        listOf(0.18f, 0.42f, 0.66f, 0.86f).forEach { f -> drawRect(c(ROJO_OSC).copy(alpha = 0.35f), Offset(bandera.left + bandera.width * f, bandera.top), Size(bandera.width * 0.05f, bandera.height)) }
        val oro = c(ORO)
        drawLine(oro, Offset(bandera.left + 6 * u, bandera.top + bandera.height * 0.18f), Offset(bandera.right - 6 * u, bandera.top + bandera.height * 0.18f), 1 * u)
        drawLine(oro, Offset(bandera.left + 6 * u, bandera.bottom - bandera.height * 0.18f), Offset(bandera.right - 6 * u, bandera.bottom - bandera.height * 0.18f), 1 * u)
        val re = bandera.height * 0.22f
        listOf(0.2f, 0.5f, 0.8f).forEachIndexed { k, f ->
            drawPath(estrella(Offset(bandera.left + bandera.width * f, bandera.center.y), if (k == 1) re * 1.2f else re, 5, 0.42f), oro)
        }
        listOf(0.32f, 0.62f).forEach { f ->
            for (j in 0 until 3) drawRect(oro, Offset(bandera.left + bandera.width * f + j * 4 * u, bandera.center.y - re * 0.5f), Size(2 * u, re))
        }
        var fx = bandera.left
        while (fx < bandera.right) { drawLine(oro, Offset(fx, bandera.bottom), Offset(fx, bandera.bottom + 4 * u), 0.8f * u); fx += 2.5f * u }
        // La tabla de arriba del estante.
        val tope = r.bottom - r.height * 0.12f
        drawRect(c(NOGAL), Offset(r.left, tope), Size(r.width, r.bottom - tope))
        vetas(Rect(r.left, tope, r.right, r.bottom), c(NOGAL_VETA).copy(alpha = 0.5f), 440, densidad = 0.7f, grosor = 0.7f)
        drawRect(c(NOGAL_LUZ), Offset(r.left, tope), Size(r.width, 2 * u))
        // La radio de mesa: caja de madera, rejilla de tela con listones, el dial y dos perillas.
        val radio = Rect(r.left + r.width * 0.12f, tope - r.height * 0.4f, r.left + r.width * 0.42f, tope)
        drawRoundRect(c(RADIO), radio.topLeft, radio.size, CornerRadius(radio.height * 0.18f))
        vetas(radio, c(NOGAL_VETA).copy(alpha = 0.45f), 450, densidad = 0.6f, grosor = 0.6f)
        val rejilla = Rect(radio.left + radio.width * 0.06f, radio.top + radio.height * 0.14f, radio.left + radio.width * 0.52f, radio.bottom - radio.height * 0.12f)
        drawRoundRect(c(TELA_RADIO), rejilla.topLeft, rejilla.size, CornerRadius(rejilla.width * 0.2f))
        for (j in 1..4) drawLine(c(RADIO), Offset(rejilla.left + rejilla.width * j / 5f, rejilla.top), Offset(rejilla.left + rejilla.width * j / 5f, rejilla.bottom), 1.6f * u)
        val dial = Rect(radio.left + radio.width * 0.58f, radio.top + radio.height * 0.18f, radio.right - radio.width * 0.06f, radio.top + radio.height * 0.48f)
        drawRect(if (noche) LUZ.copy(alpha = 0.8f) else c(CREMA), dial.topLeft, dial.size)
        for (j in 0 until 7) drawLine(c(TINTA).copy(alpha = 0.6f), Offset(dial.left + dial.width * (0.1f + 0.13f * j), dial.bottom - dial.height * 0.4f), Offset(dial.left + dial.width * (0.1f + 0.13f * j), dial.bottom), 0.6f * u)
        drawLine(c(ROJO), Offset(dial.left + dial.width * 0.62f, dial.top), Offset(dial.left + dial.width * 0.62f, dial.bottom), 1 * u)
        drawRect(c(LATON), dial.topLeft, dial.size, style = Stroke(0.8f * u))
        listOf(0.68f, 0.86f).forEach { f ->
            val pk = Offset(radio.left + radio.width * f, radio.top + radio.height * 0.72f)
            drawCircle(c(BAQUELITA), radio.height * 0.1f, pk)
            drawCircle(Color.White.copy(alpha = 0.15f), radio.height * 0.04f, Offset(pk.x - radio.height * 0.03f, pk.y - radio.height * 0.03f))
        }
        // Una pila de libros junto a la radio.
        listOf(Triple(0.48f, 0.16f, TELAS[1]), Triple(0.49f, 0.14f, TELAS[3]), Triple(0.475f, 0.17f, TELAS[0])).forEachIndexed { k, (x0, an, col) ->
            val alto0 = r.height * 0.05f
            drawRect(c(col), Offset(r.left + r.width * x0, tope - alto0 * (k + 1)), Size(r.width * an, alto0))
            drawRect(c(CREMA).copy(alpha = 0.8f), Offset(r.left + r.width * (x0 + an * 0.9f), tope - alto0 * (k + 1) + alto0 * 0.15f), Size(r.width * an * 0.07f, alto0 * 0.7f))
        }
        // La lámpara de escritorio: base y brazo de latón, pantalla de vidrio verde; de noche, encendida.
        val lx = r.left + r.width * 0.76f
        val pantallaR = Rect(lx - r.width * 0.09f, tope - r.height * 0.4f, lx + r.width * 0.09f, tope - r.height * 0.27f)
        drawRoundRect(c(LATON), Offset(lx - r.width * 0.06f, tope - r.height * 0.05f), Size(r.width * 0.12f, r.height * 0.05f), CornerRadius(2 * u))
        drawLine(c(LATON), Offset(lx, tope - r.height * 0.05f), Offset(lx, pantallaR.bottom), 2 * u)
        if (noche) mancha(Offset(lx, pantallaR.bottom + r.height * 0.04f), r.height * 0.4f, LUZ.copy(alpha = 0.5f))
        val pantalla = Path().apply {
            moveTo(pantallaR.left, pantallaR.bottom)
            arcTo(Rect(pantallaR.left, pantallaR.top, pantallaR.right, pantallaR.bottom + pantallaR.height), 180f, 180f, false)
            close()
        }
        drawPath(pantalla, if (noche) mezcla(PANTALLA, PANTALLA_LUZ, 0.5f) else c(PANTALLA))
        drawRect(Color.White.copy(alpha = 0.18f), Offset(pantallaR.left + pantallaR.width * 0.2f, pantallaR.top + pantallaR.height * 0.3f), Size(pantallaR.width * 0.12f, pantallaR.height * 0.5f))
        drawLine(c(LATON), Offset(pantallaR.left, pantallaR.bottom), Offset(pantallaR.right, pantallaR.bottom), 1.4f * u)
        if (noche) drawOval(LUZ, Offset(lx - r.width * 0.05f, pantallaR.bottom - 1 * u), Size(r.width * 0.1f, 3 * u))
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(r.left, r.bottom - 3 * u), Size(r.width, 3 * u))
    }

    override fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        fun c(x: Color) = t(x, oscuro, v.noche)
        val i = (v.semilla and 0x7fffffff) % TELAS.size
        val base = TELAS[i]
        val tela = c(if (v.tono != null) mezcla(base, v.tono, 0.22f) else base)
        val arriba = h * 0.07f
        // La ficha de préstamo que asoma de su bolsillo, con sellos de fecha: más cuanto más escribiste.
        val ficha = Rect(w * 0.2f, 0f, w * 0.64f, arriba + 3 * u)
        drawRect(c(CREMA), ficha.topLeft, ficha.size)
        drawLine(c(ROJO).copy(alpha = 0.6f), Offset(ficha.left, ficha.top + 1.2f * u), Offset(ficha.right, ficha.top + 1.2f * u), 0.5f * u)
        val sellos = 1 + (v.lleno * 2).toInt()
        for (j in 0 until sellos) drawRect(c(VIOLETA).copy(alpha = 0.6f), Offset(ficha.left + ficha.width * (0.12f + 0.28f * j), ficha.top + ficha.height * 0.45f), Size(ficha.width * 0.2f, 1 * u))
        // La tapa de tela, con su trama, la cabeza y el pie en sombra.
        drawRoundRect(tela, Offset(0f, arriba), Size(w, h - arriba), CornerRadius(1.5f * u))
        var k = arriba
        while (k < h) { drawLine(Color.White.copy(alpha = 0.04f), Offset(0f, k), Offset(w, k), 0.5f * u); k += 2 * u }
        drawRect(Color.Black.copy(alpha = 0.24f), Offset(0f, arriba), Size(w * 0.1f, h - arriba))
        drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.3f, arriba), Size(w * 0.1f, h - arriba))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, arriba), Size(w, h * 0.03f))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(0f, h * 0.97f), Size(w, h * 0.03f))
        // La estrella estampada, roja (o dorada sobre la tela ladrillo), entre dos filetes en seco.
        val sc = Offset(w * 0.5f, h * 0.19f)
        val sr = minOf(w * 0.3f, h * 0.07f)
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(w * 0.12f, h * 0.11f), Offset(w * 0.88f, h * 0.11f), 0.8f * u)
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(w * 0.12f, h * 0.27f), Offset(w * 0.88f, h * 0.27f), 0.8f * u)
        drawPath(estrella(sc, sr, 5, 0.42f), c(if (i == 0) ORO else ROJO))
        drawPath(estrella(sc, sr, 5, 0.42f), Color.Black.copy(alpha = 0.25f), style = Stroke(0.5f * u))
        // El mes a lo largo.
        textoEn(medidor, v.mes, Rect(w * 0.18f, h * 0.3f, w * 0.82f, h * 0.62f), letra, c(CREMA), 8f, vertical = true, minimo = 4f, espaciado = 0.4f)
        // El sello violeta de la biblioteca; de noche, una luna.
        val cs = Offset(w * 0.5f, h * 0.68f)
        val rs = minOf(w * 0.24f, 5.5f * u)
        if (v.noche) {
            drawCircle(LUNA, rs * 0.7f, cs)
            drawCircle(tela, rs * 0.6f, Offset(cs.x + rs * 0.32f, cs.y - rs * 0.18f))
        } else {
            drawCircle(c(VIOLETA).copy(alpha = 0.75f), rs, cs, style = Stroke(0.8f * u))
            drawCircle(c(VIOLETA).copy(alpha = 0.6f), rs * 0.6f, cs, style = Stroke(0.5f * u))
            drawCircle(c(VIOLETA).copy(alpha = 0.6f), rs * 0.18f, cs)
        }
        // El tejuelo con la signatura: el día.
        val tj = Rect(w * 0.12f, h * 0.75f, w * 0.88f, h * 0.93f)
        drawRect(c(CREMA), tj.topLeft, tj.size)
        drawRect(c(TINTA).copy(alpha = 0.5f), tj.topLeft, tj.size, style = Stroke(0.6f * u))
        drawLine(c(TINTA).copy(alpha = 0.5f), Offset(tj.left + tj.width * 0.15f, tj.top + tj.height * 0.24f), Offset(tj.right - tj.width * 0.15f, tj.top + tj.height * 0.24f), 0.6f * u)
        textoEn(medidor, v.dia, Rect(tj.left + 1 * u, tj.top + tj.height * 0.28f, tj.right - 1 * u, tj.bottom - 1 * u), letra, c(TINTA), 12f)
    }
}
